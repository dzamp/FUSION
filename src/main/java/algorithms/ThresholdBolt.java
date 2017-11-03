package algorithms;

import algorithms.actions.DirectEmitter;
import algorithms.actions.EmitAction;
import algorithms.exceptions.FieldsMismatchException;
import algorithms.util.Filter;
import algorithms.util.Operator;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.*;


/**
 * THOUGHTS
 * Having a class that will handle any values that checks for confitional values(like a threshold, equality or something else)
 * This proves challenging for a number of reasons.
 * First of all numbers are java primitives and being able to know which kind of primitive arrives from the queue and compare them is an issue. java.lang.Number
 * doesnt support comparison for apparent reason.
 * ASSUMPTIONS
 * 1. We assume that a stream of elements will always contain the same type of elements during an execution phase.
 * <p>
 * We have to come up with an efficient way to compare them without having to create separate classes for each primitive.
 * With that in mind we now have to define what is equality( will there be a decimal point that will be rounded?, check the comparator implementation:
 * When comparing  2 doubles lets say 1.02 and 1.32 the minus operation will produce -0.30. When this is being cast to int this will be rounded to 0
 * This perplexes things more.
 * <p>
 * After that we will need to create a clever way to define a number of actions that can happen whenever the criterion are being met.
 * Perhaps someone would like to filter certain values and guide them through another queue
 * or someone might want to keep those values
 * or send an alarm somewhere else
 * Solution 1: we can create extra bolts that do that. But splitting the flow is not covered
 * How should we implement something like that?
 */

public class ThresholdBolt implements IRichBolt {

    private TopologyContext ctx;
    private OutputCollector collector;
    private Number threshold;
    private Class clazz;
    private String[] emmitedFields = null;
    /**
     * Interface to delegate the action of comparator
     */
    private Comparator<Number> comparator;
    private Filter filter;
    public List<EmitAction> overThresholdEmitAction;
    public EmitAction[] overThresholdEmitAction2;
    public List<EmitAction> underThresholdEmitAction;
    private Map<String, Pair<String[], EmitAction>> declaredEmissions;

    /**
     * Operator containing the options available( gt - greater than, lt - less than , eq - equal, neq - not equal)
     */
    private Operator operator;

//    public ThresholdBolt(String className, Number threshold, String operator, String[] emmitedFields) {
//
//        try {
//            this.clazz = Class.forName(className);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        this.threshold = threshold;
//        this.operator = Operator.select(operator);
//        this.emmitedFields = emmitedFields;
//        this.overThresholdEmitAction.add(new DirectEmitter(null, emmitedFields));
//    }

    public ThresholdBolt(String className, Number threshold, String operator, List<EmitAction> actions) {

        try {
            this.clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.threshold = threshold;
        this.operator = Operator.select(operator);
//        this.overThresholdEmitAction = new ArrayList<>();
//        this.declaredEmissions = emittedFields;
//        for(String streamId : declaredEmissions.keySet()) {
//            this.overThresholdEmitAction.add(declaredEmissions.get(streamId).getRight());
//        }
        this.overThresholdEmitAction = actions;
    }


    public ThresholdBolt(String className, Number threshold, String operator) {

        try {
            this.clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.threshold = threshold;
        this.operator = Operator.select(operator);
        this.overThresholdEmitAction = new ArrayList<>();
//        this.declaredEmissions = emittedFields;
//        for(String streamId : declaredEmissions.keySet()) {
//            this.overThresholdEmitAction.add(declaredEmissions.get(streamId).getRight());
//        }
//        for(EmitAction action :actions){
//            this.overThresholdEmitAction.add(action);
//        }
    }


    public void configAction(List<EmitAction> actions) {
//        if(this.overThresholdEmitAction == null) this.overThresholdEmitAction = new ArrayList<>();
        this.overThresholdEmitAction = actions;
    }

    public void addOverAction(EmitAction action) {
        if(this.overThresholdEmitAction == null) this.overThresholdEmitAction = new ArrayList<>();
        this.overThresholdEmitAction.add(action);
    }

    public void addUnderAction(EmitAction action) {
        if(this.underThresholdEmitAction == null) this.underThresholdEmitAction = new ArrayList<>();
        this.underThresholdEmitAction.add(action);
    }



    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.ctx = context;
        this.collector = collector;
        resolveComparator(clazz.getName());
        this.filter = resolveFilterByOperator();
        this.overThresholdEmitAction = new ArrayList<>();
        this.underThresholdEmitAction = new ArrayList<>();
    }


    @Override
    /**
     * Do we have to support more complex inputs? if so how will we know the structure?
     * and how can we obtain in from the yaml file?
     */
    public void execute(Tuple input) {
        //we have to return a number of values that will be supplied from the operation
        //do appropriate action according to the Operator.
        Values rejectedValues = new Values();
        Values filteredValues = new Values();
        filter.apply(comparator, input, threshold, filteredValues, rejectedValues);
        //for every emitAction
        if (this.emmitedFields == null) {
            for (EmitAction em : this.overThresholdEmitAction) {
                try {
                    em.execute(collector, null, filteredValues);
                } catch (FieldsMismatchException e) {
                    e.printStackTrace();
                }
            }
            for (EmitAction em : this.underThresholdEmitAction) {
                try {
                    em.execute(collector, null, rejectedValues);
                } catch (FieldsMismatchException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void cleanup() {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        for (EmitAction action : this.overThresholdEmitAction) {
            declarer.declareStream(action.getStreamId(), new Fields(action.getEmittedFields()));
        }
//           TODO we have to find a way to define and implement lots of stream ids.
//           declareStream uses a unique string id which is actually a name for that stream (we could use the stream name from the yaml file)
//           but internally this changes the behaviour of the emitter. The emitter now has to know the stream mapping as well as what kind of arguments he must send
//           declarer.declareStream();


    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    /**
     * Returns a filter implementation that will filter each input value from the stream. Returns two lists of Values,
     * the accepted ones(i.e. the ones that smash the threshold) and the rejected ones(i.e. the ones who dont)
     *
     * @return
     */
    private void resolveComparator(String className) {
        switch (className) {
            case "java.lang.Integer":
                comparator = new Comparator<Number>() {
                    @Override
                    public int compare(Number o1, Number o2) {
                        return o1.intValue() - o2.intValue();
                    }
                };
                break;
            case "java.lang.Double":
                comparator = new Comparator<Number>() {
                    @Override
                    public int compare(Number o1, Number o2) {
                        int com = (o1.doubleValue() < o2.doubleValue()) ? -1 : ((o1.doubleValue() == o2.doubleValue()) ? 0 : 1);
                        return com;
                    }
                };
                break;
            case "java.lang.Long":
                comparator = new Comparator<Number>() {
                    @Override
                    public int compare(Number o1, Number o2) {
                        //what happens with long?
                        //might the number difference be that big so that the int can't store it?
                        return (o1.longValue() < o2.longValue()) ? -1 : ((o1.longValue() == o2.longValue()) ? 0 : 1);
                    }
                };
                break;
            case "java.lang.Float":
                comparator = new Comparator<Number>() {
                    @Override
                    public int compare(Number o1, Number o2) {
                        return (o1.floatValue() < o2.floatValue()) ? -1 : ((o1.floatValue() == o2.floatValue()) ? 0 : 1);
                    }
                };
                break;
        }
    }

    private Filter resolveFilterByOperator() {
        switch (operator) {
            case GREATER_THAN:
                return new Filter() {
                    @Override
                    public void apply(Comparator cmp, Tuple input, Number threshold, Values filteredValues, Values rejectedValues) {
                        Number newValue = (Number) input.getValue(0);
                        //newValue-threshold > 0
                        if (comparator.compare(newValue, threshold) > 0) {
                            filteredValues.add(newValue);
                        } else rejectedValues.add(newValue);
                    }
                };

            case LESS_THAN:
                return new Filter() {
                    @Override
                    public void apply(Comparator cmp, Tuple input, Number threshold, Values filteredValues, Values rejectedValues) {
                        Number newValue = (Number) input.getValue(0);
                        //newValue-threshold < 0
                        if (comparator.compare(newValue, threshold) < 0) {
                            filteredValues.add(newValue);
                        } else rejectedValues.add(newValue);
                    }
                };

            case EQUAL:
                return new Filter() {
                    @Override
                    public void apply(Comparator cmp, Tuple input, Number threshold, Values filteredValues, Values rejectedValues) {
                        Number newValue = (Number) input.getValue(0);
                        //equality
                        if (comparator.compare(newValue, threshold) == 0) {
                            filteredValues.add(newValue);
                        } else rejectedValues.add(newValue);
                    }
                };

            case NOT_EQUAL:
                return new Filter() {
                    @Override
                    public void apply(Comparator cmp, Tuple input, Number threshold, Values filteredValues, Values rejectedValues) {
                        Number newValue = (Number) input.getValue(0);
                        //inequality
                        if (comparator.compare(newValue, threshold) != 0) {
                            filteredValues.add(newValue);
                        } else rejectedValues.add(newValue);
                    }
                };
            default:
                return null;
        }
    }


}
