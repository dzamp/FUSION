package abstraction;

import org.apache.commons.lang.ArrayUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.*;


public class GenericBolt implements FusionIRichBolt {
    protected TopologyContext topologyContext;
    protected OutputCollector collector;
    protected Map configMap;
    //    protected List<BoltEmitter> actions;
    protected IAlgorithm algorithm;
    //    protected OutputFieldsDeclarer declarer;
    //convert to map of streamName to field list
    protected Map<String, List<String>> outgoingSteamFieldsMap;
    private boolean outgoingFieldsSet = false;
    private String[] incomingfieldNames = null;
    private String[] outgoingFieldNames = null;
    private List<String> streamIds = null;

    public GenericBolt withAlgorithm(IAlgorithm algo) {
        this.algorithm = algo;
        return this;
    }


    public GenericBolt withOutgoingStreams(String ... streamIds){
        if(this.streamIds == null) new ArrayList<>();
        this.streamIds.addAll(Arrays.asList(streamIds));
        return this;
    }


    public GenericBolt addOutgoingStream(String  streamId) {
        if(streamIds == null) this.streamIds = new ArrayList<>();
        this.streamIds.add(streamId);
        return this;

    }

    public GenericBolt withFields(String... fieldNames) {
        this.incomingfieldNames = fieldNames;
        return this;
    }


    private void setOutgoingFields() {
        outgoingSteamFieldsMap = new HashMap<>();
        if (streamIds == null) //if no
            this.streamIds = Arrays.asList(Utils.DEFAULT_STREAM_ID);

        //if the algorithm adds new fields to the stream add them here
        if (algorithm.getExtraFields() != null) {
            outgoingFieldNames = (String[]) ArrayUtils.addAll(incomingfieldNames, algorithm.getExtraFields());
        }
        if (outgoingFieldNames != null) {
            Set<String> removeDuplicates = new LinkedHashSet<>();
            removeDuplicates.addAll(Arrays.asList(outgoingFieldNames));
            outgoingFieldNames = removeDuplicates.toArray(new String[removeDuplicates.size()]);
            //fill the outcomingStreamMap to be used by the declarer
            for (String stream : streamIds) {
                outgoingSteamFieldsMap.put(stream, Arrays.asList(outgoingFieldNames));
            }
        }
        outgoingFieldsSet = true;
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.topologyContext = topologyContext;
        this.collector = outputCollector;
        this.configMap = map;
    }


    public void emit(Values values) {
        if (values != null && values.size() > 0) {
            if(this.outgoingSteamFieldsMap!=null)
                this.outgoingSteamFieldsMap.forEach((stream, stringFields) -> collector.emit(stream, values));
            //no need to use emit(values), since they it maps to this emit with stream-id = "default"
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
        //here we register the fields we are going to send
        //now there is the case that we are not going to send any fields
        //but also the case that they are not configured
        //Flux handles the setting of the fields if we specify FusionBolts in the yaml, using the FusionIRichBolt. If Flux sets the fields(even null) the incomingFieldsSet will be true;
        //That means that if fieldsNamesSet = true and outgoingfields == null that this is a terminal bolt.
        if (!outgoingFieldsSet)
            setOutgoingFields();
        if(this.outgoingSteamFieldsMap!=null)
            this.outgoingSteamFieldsMap.forEach((stream, fieldStrings) -> declarer.declareStream(stream, new Fields(fieldStrings)));

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public void setFields(boolean terminal,String... fieldNames) {
        withFields(fieldNames);
        if(!terminal)
            setOutgoingFields();
        if(terminal) outgoingFieldsSet = true;
        if (fieldNames == null) {//there is a chance that this node will be terminal, therefore null for its outgoing streams and fields
            this.outgoingFieldNames = null;
            this.outgoingSteamFieldsMap = new HashMap<>();
        }
    }

    @Override
    public void addOutgoingStreamName(String streamNames) {
        addOutgoingStream(streamNames);
    }

    @Override
    public String[] getOutgoingFields() {
        if(outgoingFieldsSet) return this.outgoingFieldNames;
        else return null;
    }

    @Override
    public IAlgorithm getAlgorithm() {
        return this.algorithm;
    }
}