package abstraction;

//import actions.BoltEmitter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.apache.storm.windowing.TupleWindow;

import java.util.*;

public class GenericWindowedBolt extends BaseWindowedBolt {

    protected TopologyContext topologyContext;
    protected OutputCollector collector;
    protected Map configMap;
//    protected List<BoltEmitter> actions;
    protected IWindowedAlgorithm algorithm;
    protected OutputFieldsDeclarer declarer;
    //convert to map of streamName to field list
    protected Map<String, List<String>> incomingStreamsFieldsMap;
    protected Map<String, List<String>> outcomingStreamsFieldsMap;
    private String[] streamIds;
    private String[] fieldNames;


    public GenericWindowedBolt outboundStreams(String... streamIds) {
        this.streamIds = streamIds;
        return this;

    }

    public GenericWindowedBolt withFields(String... fieldNames) {
        this.fieldNames = fieldNames;
        return this;
    }


    public GenericWindowedBolt withAlgorithm(IWindowedAlgorithm algo) {
        this.algorithm = algo;
        return this;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);
        this.topologyContext = context;
        this.algorithm.setInputSources(context.getThisInputFields());
        this.collector = collector;
        this.configMap = stormConf;
//        this.actions = new ArrayList<>();
        setOutboundStreams();
    }

    private void setInboundStreams() {
        //first get the incoming streams
        incomingStreamsFieldsMap = new HashMap<>();
        topologyContext.getThisInputFields().keySet().forEach(stream -> {
            List<String> fields = topologyContext.getThisInputFields().get(stream).get(Utils.DEFAULT_STREAM_ID);
            incomingStreamsFieldsMap.put(stream, new ArrayList<>(fields));
        });
    }

    private void setOutboundStreams() {
        outcomingStreamsFieldsMap = new HashMap<>();
        if (streamIds == null) //if no
            this.streamIds = new String[]{Utils.DEFAULT_STREAM_ID}; //default
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

    public TupleWindow beforeAlgorithmExecution(TupleWindow tupleWindow) {
        return tupleWindow;
    }

    public Values afterAlgorithmExecution(Values values) {
        return values;
    }

    @Override
    public void execute(TupleWindow inputWindow) {
        beforeAlgorithmExecution(inputWindow);
        Values values = algorithm.executeWindowedAlgorithm(inputWindow);
        afterAlgorithmExecution(values);
        emit(values);
    }


    @Override
    public void cleanup() {
        //nothing here
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        this.declarer = declarer;
        this.outcomingStreamsFieldsMap.forEach((stream, fieldStrings) -> declarer.declareStream(stream, new Fields(fieldStrings)));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return super.getComponentConfiguration();
    }


//    public void addAction(BoltEmitter boltEmitter) {
//        actions.add(boltEmitter);
//    }

//    public void emit(Values values) {
//        if (values != null) {
//            this.actions.forEach(boltEmitter -> {
//                try {
//                    boltEmitter.execute(this.collector, boltEmitter.getStreamId(), values);
//                } catch (FieldsMismatchException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
//    }


    public void emit(Values values) {
        if (values != null && values.size() > 0) {
            this.outcomingStreamsFieldsMap.forEach((stream, stringFields) ->
                    //no need to use emit(values), since they it maps to this emit with stream-id = "default"
                    collector.emit(stream, values)
            );
        }
    }

}
