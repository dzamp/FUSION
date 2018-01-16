package abstraction;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;


import java.io.Serializable;
import java.util.*;


//TODO this algortihm will emit new fields! how to send them to Generic Bolt?
public class CusumAlgorithm implements IAlgorithm, Serializable{

    private final double median, drift, threshold;
    private  boolean enableUpperBound=true, enableLowerBound=false;
    private int position=0;
    private double positiveCusum=0.0, negativeCusum = 0.0;
    private String fieldUpperBoundBreached="cusumUpperBound", fieldLowerBoundBreached = "cusumLowerBound";



    public CusumAlgorithm(double median, double drift, double threshold){
        this.median = median;
        this.drift = drift;
        this.threshold = threshold;
    }

    public CusumAlgorithm withLowerBound(){
        this.enableLowerBound = true;
        return this;
    }

    public CusumAlgorithm withLowerBoundField(String fieldName){
        this.fieldLowerBoundBreached = fieldName;
        return this;
    }

    public CusumAlgorithm withUpperBoundField(String fieldName){
        this.fieldUpperBoundBreached = fieldName;
        return this;
    }


    public CusumAlgorithm withPositionInStream(int pos){
        this.position = pos;
        return this;
    }



    @Override
    public Values executeAlgorithm(Tuple tuple) {
        Values values = new Values();
        values.addAll(tuple.getValues());
        double upperBoundSignal = 0, lowerBoundSignal = 0;
        double currentValue  = tuple.getDouble(position);


        double temp = currentValue - (median + drift) + positiveCusum;
        positiveCusum = Math.max(0, temp);

        temp = currentValue - (median - drift) + negativeCusum;
        negativeCusum = Math.min(0, temp);

        if (positiveCusum > threshold) {
            upperBoundSignal = 1;
            positiveCusum = 0;
            negativeCusum = 0;
            //upper threshold breached here
            //todo add the new field to the generic bolt fields
            values.add(upperBoundSignal);
            return values;
        }
        if (negativeCusum < -threshold) {
            lowerBoundSignal = -1;
            positiveCusum = 0;
            negativeCusum = 0;
            //lower threshold breached here
            //todo add the new field to the generic bolt fields

            values.add(lowerBoundSignal);
            return values;
        }
        //else add ret as zero
        values.add(0);
        return values;
    }

    @Override
    public String[] getExtraFields() {
        return new String[]{"cusum"};
    }




}
