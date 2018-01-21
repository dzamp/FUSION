package abstraction;

//import actions.BoltEmitter;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.apache.storm.windowing.TimestampExtractor;
import org.apache.storm.windowing.TupleWindow;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class GenericWindowedBolt extends BaseWindowedBolt implements FusionBolt {

    protected TopologyContext topologyContext;
    protected OutputCollector collector;
    protected Map configMap;
    //    protected List<BoltEmitter> actions;
    protected IWindowedAlgorithm algorithm;
    protected OutputFieldsDeclarer declarer;
    //convert to map of streamName to field list
    protected Map<String, List<String>> sendingComponentToIncomingFieldsMap;
    protected Map<String, List<String>> outgoingStreamsFieldsMap;
    protected String[] incomingfieldNames = null;
    protected String[] outgoingFieldNames = null;
    protected List<String> streamIds = null;
    protected boolean outgoingFieldsSet = false;


    public GenericWindowedBolt withAlgorithm(IWindowedAlgorithm algo) {
        this.streamIds = new ArrayList<>();
        this.streamIds.add(Utils.DEFAULT_STREAM_ID);
        this.algorithm = algo;
        return this;
    }

    public GenericWindowedBolt withOutgoingStreams(String... streamIds) {
        if (this.streamIds == null) new ArrayList<>();
        this.streamIds.addAll(Arrays.asList(streamIds));
        return this;
    }


    public GenericWindowedBolt addOutgoingStream(String streamId) {
        this.streamIds.remove(Utils.DEFAULT_STREAM_ID);
        this.streamIds.add(streamId);
        return this;

    }

    public GenericWindowedBolt withFields(String... fieldNames) {
        this.incomingfieldNames = fieldNames;
        return this;
    }


    private void setOutgoingFields() {
//        setInboundStreams();
        //if the algorithm adds new fields to the stream add them here
        outgoingFieldNames = algorithm.transformFields(incomingfieldNames);

        outgoingStreamsFieldsMap = new HashMap<>();
        if (outgoingFieldNames != null) {
            Set<String> removeDuplicates = new LinkedHashSet<>();
            removeDuplicates.addAll(Arrays.asList(outgoingFieldNames));
            outgoingFieldNames = removeDuplicates.toArray(new String[removeDuplicates.size()]);
            //fill the outcomingStreamMap to be used by the declarer
            for (String stream : streamIds) {
                outgoingStreamsFieldsMap.put(stream, Arrays.asList(outgoingFieldNames));
            }
        }
        outgoingFieldsSet = true;
    }


    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);
        this.topologyContext = context;
        this.collector = collector;
        this.configMap = stormConf;
        //on the prepare method the topology has been validated and deployed
        //therefore we can get the info about the topology
        this.setInboundStreams(); //keep also the incoming streams in case we need them
        this.algorithm.setInputSources(this.sendingComponentToIncomingFieldsMap);
    }


    private void setInboundStreams() {
        //first get the incoming streams
        sendingComponentToIncomingFieldsMap = new HashMap<>();
        Map<String, Map<String, List<String>>> inputFields = topologyContext.getThisInputFields();
        List<String> fields = null;
        for (String component : inputFields.keySet()) {
            Map<String, List<String>> mapStreamFields = inputFields.get(component);
            for (String stream : mapStreamFields.keySet()) {
                fields = mapStreamFields.get(stream); //assume here that we have the same fields from each incoming component
                break;
            }
            if(fields!=null)
                sendingComponentToIncomingFieldsMap.put(component,fields);
        }
    }

    public void emit(Values values) {
        if (values != null && values.size() > 0) {
            if (this.outgoingStreamsFieldsMap != null)
            this.outgoingStreamsFieldsMap.forEach((stream, stringFields) ->
                    //no need to use emit(values), since they it maps to this emit with stream-id = "default"
                    collector.emit(stream, values)
            );
        }
    }




    @Override
    public void execute(TupleWindow inputWindow) {
        Values values = algorithm.executeWindowedAlgorithm(inputWindow);
        emit(values);
    }

    @Override
    public void cleanup() {
        //nothing here
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        if (!outgoingFieldsSet)
            setOutgoingFields();
        if (this.outgoingStreamsFieldsMap != null)
            this.outgoingStreamsFieldsMap.forEach((stream, fieldStrings) -> declarer.declareStream(stream, new Fields(fieldStrings)));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return super.getComponentConfiguration();
    }


    @Override
    public void setFields(boolean terminal, String... fieldNames) {
        withFields(fieldNames);
        if (!terminal)
            setOutgoingFields();
        if (terminal) outgoingFieldsSet = true;
        if (fieldNames == null) {//there is a chance that this node will be terminal, therefore null for its outgoing streams and fields
            this.outgoingFieldNames = null;
            this.outgoingStreamsFieldsMap = new HashMap<>();
        }
    }

    @Override
    public void addOutgoingStreamName(String streamId) {
        addOutgoingStream(streamId);
    }

    @Override
    public String[] getOutgoingFields() {
        if (outgoingFieldsSet) return this.outgoingFieldNames;
        else return null;
    }



//    @Override
//    public BaseWindowedBolt withWindow(Count windowLength, Count slidingInterval) {
//        return super.withWindow(windowLength, slidingInterval);
//    }
//
//    @Override
//    public BaseWindowedBolt withWindow(Count windowLength, Duration slidingInterval) {
//        return super.withWindow(windowLength, slidingInterval);
//    }
//
//    @Override
//    public BaseWindowedBolt withWindow(Duration windowLength, Count slidingInterval) {
//        return super.withWindow(windowLength, slidingInterval);
//    }
//
//    @Override
//    public BaseWindowedBolt withWindow(Duration windowLength, Duration slidingInterval) {
//        return super.withWindow(windowLength, slidingInterval);
//    }
//
//    @Override
//    public BaseWindowedBolt withWindow(Count windowLength) {
//        return super.withWindow(windowLength);
//    }
//
//    @Override
//    public BaseWindowedBolt withWindow(Duration windowLength) {
//        return super.withWindow(windowLength);
//    }
//
//    @Override
//    public BaseWindowedBolt withTumblingWindow(Count count) {
//        return super.withTumblingWindow(count);
//    }
//
//    @Override
//    public BaseWindowedBolt withTumblingWindow(Duration duration) {
//        return super.withTumblingWindow(duration);
//    }
//
//    @Override
//    public BaseWindowedBolt withTimestampField(String fieldName) {
//        return super.withTimestampField(fieldName);
//    }
//
//    @Override
//    public BaseWindowedBolt withTimestampExtractor(TimestampExtractor timestampExtractor) {
//        return super.withTimestampExtractor(timestampExtractor);
//    }
//
//    @Override
//    public TimestampExtractor getTimestampExtractor() {
//        return super.getTimestampExtractor();
//    }
//
//    @Override
//    public BaseWindowedBolt withLateTupleStream(String streamId) {
//        return super.withLateTupleStream(streamId);
//    }
//
//    @Override
//    public BaseWindowedBolt withLag(Duration duration) {
//        return super.withLag(duration);
//    }
//
//    @Override
//    public BaseWindowedBolt withWatermarkInterval(Duration interval) {
//        return super.withWatermarkInterval(interval);
//    }
}
