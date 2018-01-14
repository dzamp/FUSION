package abstraction;

import org.apache.commons.lang.ArrayUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.*;


public class GenericBolt implements IRichBolt {
    protected TopologyContext topologyContext;
    protected OutputCollector collector;
    protected Map configMap;
//    protected List<BoltEmitter> actions;
    protected IAlgorithm algorithm;
    protected OutputFieldsDeclarer declarer;
    //convert to map of streamName to field list
    protected Map<String, List<String>> incomingStreamsFieldsMap;
    protected Map<String, List<String>> outcomingStreamsFieldsMap;

    private String[] fieldNames = null;
    private String[] streamIds = null;

    public GenericBolt withAlgorithm(IAlgorithm algo) {
        this.algorithm = algo;
        return this;
    }

    public GenericBolt outboundStreams(String... streamIds) {
        this.streamIds = streamIds;
        return this;

    }

    public GenericBolt withFields(String... fieldNames) {
        this.fieldNames = fieldNames;
        return this;
    }

    private void setInboundStreams() {
        //first get the incoming streams
        incomingStreamsFieldsMap = new HashMap<>();
        for (String stream : topologyContext.getThisInputFields().keySet()) {
            List<String> fields = topologyContext.getThisInputFields().get(stream).get(Utils.DEFAULT_STREAM_ID);
            incomingStreamsFieldsMap.put(stream, new ArrayList<>(fields));
        }
    }

    private void setOutboundStreams() {
        outcomingStreamsFieldsMap = new HashMap<>();
        if (streamIds == null) //if no
            this.streamIds = new String[]{Utils.DEFAULT_STREAM_ID};
        if (fieldNames == null)
            fieldNames = incomingStreamsFieldsMap.values().iterator().next().toArray(new String[0]);
        //if the algorithm adds new fields to the stream add them here
        if (algorithm.getExtraFields() != null) {
            fieldNames = (String[]) ArrayUtils.addAll(fieldNames, algorithm.getExtraFields());
        }
        //fill the outcomingStreamMap to be used by the declarer
        for (String stream : streamIds) {
            outcomingStreamsFieldsMap.put(stream, Arrays.asList(fieldNames));
        }
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.topologyContext = topologyContext;
        this.collector = outputCollector;
        this.configMap = map;
//        this.actions = new ArrayList<>();
        setOutboundStreams();
    }

//    public void addAction(BoltEmitter boltEmitter) {
//        actions.add(boltEmitter);
//    }


    public void emit(Values values) {
        if (values != null && values.size() > 0) {
            this.outcomingStreamsFieldsMap.forEach((stream, stringFields) ->
                    //no need to use emit(values), since they it maps to this emit with stream-id = "default"
                    collector.emit(stream, values)
            );
        }
    }

    public Tuple beforeAlgorithmExecution(Tuple tuple) {
        return tuple;
    }

    public Values afterAlgorithmExecution(Values values) {
        return values;
    }

    @Override
    //make this abstract?
    public void execute(Tuple tuple) {
        beforeAlgorithmExecution(tuple);
        Values values = algorithm.executeAlgorithm(tuple);
        afterAlgorithmExecution(values);
        emit(values);
    }

    @Override
    public void cleanup() {
        //nothing here
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //Again the declare method that takes no stream argument is the same method call
        this.declarer = declarer;
        this.outcomingStreamsFieldsMap.forEach((stream, fieldStrings) -> declarer.declareStream(stream, new Fields(fieldStrings)));

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}