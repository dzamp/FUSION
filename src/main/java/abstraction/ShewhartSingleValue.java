package abstraction;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.Serializable;
import java.util.*;

public class ShewhartSingleValue implements IAlgorithm, Serializable {

    protected ShewhartState previousState;
    //position on which the shewhard algorithm will be applied
    protected int positionInStream = 0;
    protected String fieldInStream = "";
    protected double kplus = 3;
    protected double kminus = 3;
    protected int windowSize = 100, n=2;
    protected boolean emitEntireWindow = false;
    protected Map<String, List<String>> inputFieldsFromSources;


    public ShewhartSingleValue() {
        this.previousState = new ShewhartState();
    }

    public ShewhartSingleValue(double initialMean, double initialVariance) {
        this.previousState = new ShewhartState(initialMean, initialVariance);
    }

    public ShewhartSingleValue withWindow(int n) {
        this.windowSize = n;
        return this;
    }

    public ShewhartSingleValue withPositionInStream(int position) {
        this.positionInStream = position;
        this.fieldInStream = "";
        return this;
    }

    public ShewhartSingleValue withFieldInStream(String fieldName) {
        this.fieldInStream = fieldName;
        this.positionInStream = -1;
        return this;
    }


    public ShewhartSingleValue emitEntireWindow(boolean value){
        this.emitEntireWindow = value;
        return this;
    }


    @Override
    public Values executeAlgorithm(Tuple tuple) {
        Values values = new Values();
        values.addAll(tuple.getValues());
        double value = tuple.getDouble(positionInStream);
        double curr_mean = previousState.getMean() + ((1.0 / n) * (value - previousState.getMean()));
        double curr_Variance = Math.sqrt(
                (1.0 / n) * (
                        ( (n - 1.0) * Math.pow(previousState.getVariance(), 2))
                                + ((value - previousState.getMean()) * (value - curr_mean))
                )
        );

        double UCL = curr_mean + kplus * curr_Variance;
        double LCL = curr_mean - kminus * curr_Variance;
        //return values according to the outcome of the algorithm
        if (value > UCL )
            values.add(1);
        else if (value < LCL)
            values.add(-1);
        else
            values.add(0);

        n++;
        if (n == windowSize + 2)
            n = 2; //reset the window
        this.previousState.nextState(curr_mean, curr_Variance);
        return values;
    }

    @Override
    public void setInputSources(Map<String, List<String>> inputFieldsFromSources) {
        this.inputFieldsFromSources = inputFieldsFromSources;

    }

    @Override
    public void prepare() {

    }


    @Override
    public String[] transformFields(String[] incomingFields) {
        Set<String> removeDuplicates = new LinkedHashSet<>();
        removeDuplicates.addAll(Arrays.asList(incomingFields));
        removeDuplicates.add(new String("shewhart"));
        return removeDuplicates.toArray(new String[removeDuplicates.size()]);
    }
}
