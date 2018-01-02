package abstraction;

import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

import java.util.*;

/**
 * Created by jim on 29/12/2017.
 */

//TODO implement this class??
public class WindowTimestampStreamMerger implements IWindowedAlgorithm {

    protected BaseWindowedBolt.Duration duration;
    private BaseWindowedBolt.Duration lag;
    protected String timestampField = "";

    protected Map<String,Map<String,List<String>>>  inputFieldsFromSources;

    public void setInputSources(Map<String,Map<String,List<String>>>  inputFieldsFromSources){
        this.inputFieldsFromSources = inputFieldsFromSources;
    }

    public WindowTimestampStreamMerger withSecondsLag(int seconds){
        this.lag = BaseWindowedBolt.Duration.seconds(seconds);
        return this;
    }


    public WindowTimestampStreamMerger withMinutesLag(int minutes){
        this.lag = BaseWindowedBolt.Duration.minutes(minutes);
        return this;
    }


    public WindowTimestampStreamMerger withHoursLag(int hours){
        this.lag = BaseWindowedBolt.Duration.hours(hours);
        return this;
    }



    public WindowTimestampStreamMerger withTimestampField(String fieldName) {
        this.timestampField = fieldName;
        return this;
    }

    public WindowTimestampStreamMerger withWindowSecDuration(int seconds) {
        this.duration = BaseWindowedBolt.Duration.seconds(seconds);
        return this;
    }


    public WindowTimestampStreamMerger withWindowHoursDuration(int hours) {
        this.duration = BaseWindowedBolt.Duration.hours(hours);
        return this;
    }

    public WindowTimestampStreamMerger withWindowMinDuration(int minutes) {
        this.duration = BaseWindowedBolt.Duration.minutes(minutes);
        return this;
    }


    @Override
    public Values executeWindowedAlgorithm(TupleWindow tupleWindow) {
        Map<String,List<Values>> streamValues = new HashMap<>();

        Set<String> streams = inputFieldsFromSources.keySet();
        streams.forEach(stream -> streamValues.put(stream,new ArrayList<>()));


        for(Tuple tuple: tupleWindow.get()){
            String streamName = tuple.getSourceComponent();
            Values vals = new Values(tuple.getValues());
            streamValues.get(tuple.getSourceComponent()).add(new Values(tuple.getValues()));
        }
        tupleWindow.get().forEach(tuple -> streamValues.get(tuple.getSourceComponent()).add(new Values(tuple.getValues())));
        return new Values(streamValues);
    }

    @Override
    public int getWindowCount() {
        return 0;
    }

    @Override
    public BaseWindowedBolt.Duration getWindowDuration() {
        return this.duration;
    }

    @Override
    public BaseWindowedBolt.Duration getWindowLag() {
        return this.lag;
    }

    @Override
    public String getTimestampField() {
        return this.timestampField;
    }


}