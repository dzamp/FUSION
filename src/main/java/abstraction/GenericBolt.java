package abstraction;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.*;


public class GenericBolt implements FusionBolt, IRichBolt {
    protected TopologyContext topologyContext;
    protected OutputCollector collector;
    protected Map configMap;
    //    protected List<BoltEmitter> actions;
    protected IAlgorithm algorithm;
    //    protected OutputFieldsDeclarer declarer;
    //convert to map of streamName to field list
    protected Map<String, List<String>> outgoingStreamsFieldsMap;
    protected boolean outgoingFieldsSet = false;
    protected String[] incomingfieldNames = null;
    protected String[] outgoingFieldNames = null;
    protected List<String> streamIds = null;
    protected Map<String, List<String>> sendingComponentToIncomingFieldsMap;


    public GenericBolt withAlgorithm(IAlgorithm algo) {
        this.streamIds = new ArrayList<>();
        this.streamIds.add(Utils.DEFAULT_STREAM_ID);
        this.algorithm = algo;
        return this;
    }


    public GenericBolt withOutgoingStreams(String... streamIds) {
        if (this.streamIds == null) new ArrayList<>();
        this.streamIds.addAll(Arrays.asList(streamIds));
        return this;
    }


    public GenericBolt addOutgoingStream(String streamId) {
        this.streamIds.remove(Utils.DEFAULT_STREAM_ID);
        this.streamIds.add(streamId);
        return this;

    }

    public GenericBolt withFields(String... fieldNames) {
        this.incomingfieldNames = fieldNames;
        return this;
    }


    private void setOutgoingFields() {
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
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.topologyContext = topologyContext;
        this.collector = outputCollector;
        this.configMap = map;
        //on the prepare method the topology has been validated and deployed
        //therefore we can get the info about the topology
        setInboundStreams();
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
                this.outgoingStreamsFieldsMap.forEach((stream, stringFields) -> collector.emit(stream, values));
            //no need to use emit(values), since they it maps to this emit with stream-id = "default"
        }
    }


    @Override
    //make this abstract?
    public void execute(Tuple tuple) {
        Values values = algorithm.executeAlgorithm(tuple);
        emit(values);
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //Again the declare method that takes no stream argument is the same method call
        //here we register the fields we are going to send
        //now there is the case that we are not going to send any fields
        //but also the case that they are not configured
        //Flux handles the setting of the fields if we specify FusionBolts in the yaml, using the FusionBolt. If Flux sets the fields(even null) the incomingFieldsSet will be true;
        //That means that if fieldsNamesSet = true and outgoingfields == null that this is a terminal bolt.
        if (!outgoingFieldsSet)
            setOutgoingFields();
        if (this.outgoingStreamsFieldsMap != null)
            this.outgoingStreamsFieldsMap.forEach((stream, fieldStrings) -> declarer.declareStream(stream, new Fields(fieldStrings)));

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

    @Override
    public void cleanup() {
        //nothing here
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }


}