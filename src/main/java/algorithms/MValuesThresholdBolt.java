package algorithms;

import exceptions.FieldsMismatchException;
import flow.StreamBisect;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import util.FilterOperation;
import util.Operator;

import java.util.Comparator;
import java.util.Map;

public class MValuesThresholdBolt extends StreamBisect {

    protected Number threshold;
    protected Class clazz;

    /**
     * Interface to delegate the action of comparator
     */
    protected Comparator<Number> comparator;
    /**
     * The filterOperation that will implement the comparison
     */
    protected FilterOperation filterOperation;
    /**
     * Operator containing the options available( gt - greater than, lt - less than , eq - equal, neq - not equal)
     */
    protected Operator operator;

    /**
     * The position in the values list that the Threshold will be implemented
     */
    protected int positionInStream = 0;

    /**
     * @param className        The className of the Number that will be compared(java.lang.Integer, java.lang.Long. java.lang.Float, java.lang.Double)
     * @param threshold        A value of className that will be the threshold
     * @param positionInStream An integer indicating the position among the N values that the threshold will be executed. Allowed values are [0,N)
     * @param operator         An operator indicating the kind of comparison we would like with the threshold value
     *                         Constructor to create the MValuesThresholdBolt that will apply thresholding to the values of the stream. The stream has N values meaning that in any incoming tuple
     *                         there are multiple objects.
     */

    public MValuesThresholdBolt(String className,Number threshold, int positionInStream, String operator) {
        super();
        try {
            this.clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.threshold = threshold;
        this.positionInStream = positionInStream;
        this.operator = Operator.select(operator);
    }

    public MValuesThresholdBolt(String className,Number threshold,  String operator) {
        super();
        try {
            this.clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.threshold = threshold;
        this.operator = Operator.select(operator);
    }


    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);
        resolveComparator(clazz.getName());
        this.filterOperation = resolveFilterByOperator();
    }


    @Override
    public void execute(Tuple tuple) {
        Values rejectedValues = new Values(), filteredValues = new Values();
        filterOperation.apply(comparator, tuple, threshold, filteredValues, rejectedValues);
//        //for every emitAction
//        if (filteredValues.size()!=0) {
//            //above threshold
//            for (BoltEmitter em : this.conditionTrueAction) {
//                try {
//                    em.execute(this.collector, em.getStreamId(), (Values)tuple.getValues());
//                } catch (FieldsMismatchException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        if (rejectedValues.size()!=0) {
//            //below threshold
//            for (BoltEmitter em : this.conditionFalseAction) {
//                try {
//                    em.execute(this.collector, em.getStreamId(), (Values)tuple.getValues());
//                } catch (FieldsMismatchException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    /**
     * Returns a filterOperation implementation that will filterOperation each input value from the stream. Returns two lists of Values,
     * the accepted ones(i.e. the ones that smash the threshold) and the rejected ones(i.e. the ones who dont)
     *
     * @return
     */
    protected void resolveComparator(String className) {
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

    protected FilterOperation resolveFilterByOperator() {
        //Here if the value is greater than the threshold the entire tuple gets added to the rejected or accepted values
        switch (operator) {
            case GREATER_THAN:
                 return new FilterOperation() {
                    @Override
                    public void apply(Comparator cmp, Tuple input, Number threshold, Values filteredValues, Values rejectedValues) {
                        Number newValue = (Number) input.getValue(positionInStream);
                        //newValue-threshold > 0
                        if (comparator.compare(newValue, threshold) > 0) {
                            filteredValues.add(input);
                        } else rejectedValues.add(input);
                    }
                };

            case LESS_THAN:
                return new FilterOperation() {
                    @Override
                    public void apply(Comparator cmp, Tuple input, Number threshold, Values filteredValues, Values rejectedValues) {
                        Number newValue = (Number) input.getValue(positionInStream);
                        //newValue-threshold < 0
                        if (comparator.compare(newValue, threshold) < 0) {
                            filteredValues.add(input);
                        } else rejectedValues.add(input);
                    }
                };

            case EQUAL:
                return new FilterOperation() {
                    @Override
                    public void apply(Comparator cmp, Tuple input, Number threshold, Values filteredValues, Values rejectedValues) {
                        Number newValue = (Number) input.getValue(0);
                        //equality
                        if (comparator.compare(newValue, threshold) == positionInStream) {
                            filteredValues.add(input);
                        } else rejectedValues.add(input);
                    }
                };

            case NOT_EQUAL:
                return new FilterOperation() {
                    @Override
                    public void apply(Comparator cmp, Tuple input, Number threshold, Values filteredValues, Values rejectedValues) {
                        Number newValue = (Number) input.getValue(positionInStream);
                        //inequality
                        if (comparator.compare(newValue, threshold) != 0) {
                            filteredValues.add(input);
                        } else rejectedValues.add(input);
                    }
                };
            default:
                return null;
        }
    }

}
