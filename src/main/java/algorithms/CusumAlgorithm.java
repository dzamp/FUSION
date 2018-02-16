package algorithms;

import abstraction.IAlgorithm;
import org.apache.log4j.Logger;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.Serializable;
import java.util.*;


//TODO this algortihm will emit new fields! how to send them to Generic Bolt?
public class CusumAlgorithm implements IAlgorithm, Serializable {

    protected final double median, drift, threshold;
    protected boolean enableUpperBound = true, enableLowerBound = false;
    protected int positionInStream = 0;
    protected double positiveCusum = 0.0, negativeCusum = 0.0;
    protected String fieldUpperBoundBreached = "cusumUpperBound", fieldLowerBoundBreached = "cusumLowerBound";
    protected Map<String, List<String>> inputFieldsFromSources;
    protected String fieldInStream="";
    protected Logger LOG;

    public CusumAlgorithm(double median, double drift, double threshold) {
        this.median = median;
        this.drift = drift;
        this.threshold = threshold;
    }

    public CusumAlgorithm withLowerBound() {
        this.enableLowerBound = true;
        return this;
    }

    public CusumAlgorithm withLowerBoundField(String fieldName) {
        this.fieldLowerBoundBreached = fieldName;
        return this;
    }

    public CusumAlgorithm withUpperBoundField(String fieldName) {
        this.fieldUpperBoundBreached = fieldName;
        return this;
    }


    public CusumAlgorithm withPositionInStream(int pos) {
        this.positionInStream = pos;
        return this;
    }

    public CusumAlgorithm withFieldInStream(String fieldName) {
        this.fieldInStream = fieldName;
        return this;
    }

    @Override
    public Values executeAlgorithm(Tuple tuple) {
        Values values = new Values();
        values.addAll(tuple.getValues());
        double upperBoundSignal = 0, lowerBoundSignal = 0;
        double currentValue = tuple.getDouble(positionInStream);


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
    public void setInputSources(Map<String, List<String>> inputFieldsFromSources) {
        this.inputFieldsFromSources = inputFieldsFromSources;
    }

    @Override
    public void prepare() {
        LOG = Logger.getLogger(this.getClass());
    }

//    @Override
//    public void setInputSources(Map<String, Map<String, List<String>>> inputFieldsFromSources) {
//        this.inputFieldsFromSources = inputFieldsFromSources;
//        inputFieldsFromSources = new HashMap<>();
//        for (String stream : this.inputFieldsFromSources.keySet()) {
//            List<String> fields = this.inputFieldsFromSources.get(stream).get("default");
//            inputFieldsFromSources.put(stream, new ArrayList<>(fields));
//        }
//    }


    @Override
    public String[] transformFields(String[] incomingFields) {
        Set<String> removeDuplicates = new LinkedHashSet<>();
        removeDuplicates.addAll(Arrays.asList(incomingFields));
        removeDuplicates.add(new String("cusum"));
        return removeDuplicates.toArray(new String[removeDuplicates.size()]);
    }
}
