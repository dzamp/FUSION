package algorithms;

import algorithms.actions.EmitAction;
import algorithms.util.Operator;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jim on 5/11/2017.
 */
//Split the stream into to two streams according to condition algorithm
public abstract class StreamSplitter implements IRichBolt, Serializable{
    //Abstract class?

    protected TopologyContext topologyContext;
    protected OutputCollector collector;
    protected Map configMap;
    protected List<EmitAction> conditionTrueAction;
    protected List<EmitAction> conditionFalseAction;

    // 1. Is the threshold something we need?
    // 2. What family of algorithms will a splitter represent?
    // 3. Thresholding is one of them that works with numeric data
    // 4. Furthermore we need to think what kind of superclass will handle the actions and the declarers
    // 5. Lets say that Object counter might need to split some stream also, what are we going to do there?
    // 6. How will we support multiple values? How will we know which one will be used for any job?
    //


    public StreamSplitter() {
        this.conditionTrueAction = new ArrayList<>();
        this.conditionFalseAction = new ArrayList<>();
    }

    public void addConditionTrueAction(EmitAction em){
        this.conditionTrueAction.add(em);
    }

    public void addConditionFalseAction(EmitAction em){
        this.conditionFalseAction.add(em);
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.topologyContext = topologyContext;
        this.collector = outputCollector;
        this.configMap = map;

    }

    @Override
    //make this abstract?
    public abstract void execute(Tuple tuple);

    @Override
    public void cleanup() {

    }

    @Override
    //since this is a splitter should we leave this here?
    //should we make this class abstract too?
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        //declare the split of the stream into to two named streams
        conditionTrueAction.forEach(emitAction -> outputFieldsDeclarer.declareStream(emitAction.getStreamId(),new Fields(emitAction.getEmittedFields())));
        conditionFalseAction.forEach(emitAction -> outputFieldsDeclarer.declareStream(emitAction.getStreamId(),new Fields(emitAction.getEmittedFields())));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
