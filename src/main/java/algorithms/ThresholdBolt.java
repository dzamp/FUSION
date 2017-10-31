package algorithms;

import algorithms.util.Operator;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * THOUGHTS
 * Having a class that will handle any values that checks for confitional values(like a threshold, equality or something else)
 * This proves challenging for a number of reasons.
 * First of all numbers are java primitives and being able to know which kind of primitive arrives from the queue and compare them is an issue.
 * We assume that a stream of elements will always contain the same type of elemens.
 * We have to come up with an efficient way to compare them without having to create separate classes for each primitive.
 * With that in mind we now have to define what is equality( will there be a decimal point that will be rounded?, check the comparator implementation:
 * When comparing  2 doubles lets say 1.02 and 1.32 the minus operation will produce -0.30. When this is being cast to int this will be rounded to 0(pressumably)
 * This perplexes things more.

 * After that we will need to create a clever way to define a number of actions that can happen whenever the criterion are being met.
 * Perhaps someone would like to filter certain values and guide them through another queue
 * or someone might want to keep those values
 * or send an alarm somewhere else
 * Solution 1: we can create extra bolts that do that. But splitting the flow is not covered
 * How should we implement something like that?
 */




public class ThresholdBolt implements IRichBolt{
    TopologyContext ctx;
    OutputCollector collector;
    Number threshold;
    Class clazz;
    boolean firstNumber = false;
    Comparator<Number> comparator;
    List<Number> numberList ;

    /**
     *  Operator containing the options available( gt - greater than, lt - less than , eq - equal, neq - not equal)
     */
    Operator operator;


    public ThresholdBolt(String className, Number threshold, String operator) {
        try {
            this.clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.threshold = threshold;
        this.operator = Operator.select(operator);

    }

    /**
     * An interface handles the comparison between the Threshold Number and the incoming value.
     * It should be better to implement it this way since having an if-else clause in the execute method will prove costly
     */
    private void resolveComparator(String className){
        switch (className){
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
                        return (int) (o1.doubleValue() - o2.doubleValue());
                    }
                };
                break;
            case "java.lang.Long":
                comparator = new Comparator<Number>() {
                    @Override
                    public int compare(Number o1, Number o2) {
                        return (int) (o1.longValue() - o2.longValue());
                    }
                };
                break;
            case "java.lang.Float":
                comparator = new Comparator<Number>() {
                    @Override
                    public int compare(Number o1, Number o2) {
                        return (int) (o1.floatValue() - o2.floatValue());
                    }
                };
                break;
        }
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.ctx = context;
        this.collector = collector;
        resolveComparator(clazz.getName());
        numberList = new ArrayList<>();
    }

    @Override
    public void execute(Tuple input) {

        if(comparator.compare(threshold,(Number)input.getValue(0)) < 0) {
            numberList.add((Number)input.getValue(0));
        }

        //do appropriate action according to the Operator.



    }



    @Override
    public void cleanup() {
        numberList.forEach(number -> System.out.print(number + ", "));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //nothing
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
