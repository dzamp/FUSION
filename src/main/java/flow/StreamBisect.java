package flow;

import actions.Action;
import actions.BoltEmitter;
import exceptions.FieldsMismatchException;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jim on 5/11/2017.
 */
//Split the stream into to two streams according to condition algorithm
public abstract class StreamBisect implements IRichBolt {
    //Abstract class?

    protected TopologyContext topologyContext;
    protected OutputCollector collector;
    protected Map configMap;
    protected List<BoltEmitter> conditionTrueAction;
    protected List<BoltEmitter> conditionFalseAction;

    // 1. Is the threshold something we need?
    // 2. What family of algorithms will a splitter represent?
    // 3. Thresholding is one of them that works with numeric data
    // 4. Furthermore we need to think what kind of superclass will handle the actions and the declarers
    // 5. Lets say that Object counter might need to split some stream also, what are we going to do there?
    // 6. How will we support multiple values? How will we know which one will be used for any job?
    //


    public StreamBisect() {
        this.conditionTrueAction = new ArrayList<>();
        this.conditionFalseAction = new ArrayList<>();
    }

    public void addConditionTrueAction(BoltEmitter em) {
        this.conditionTrueAction.add(em);
    }

    public void addConditionFalseAction(BoltEmitter em) {
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

    public void emitValues(Values filteredValues, Values rejectedValues) {
        this.conditionTrueAction.forEach(em -> {
            try {
                em.execute(this.collector, em.getStreamId(), filteredValues);
            } catch (FieldsMismatchException e) {
                e.printStackTrace();
            }
        });
        this.conditionFalseAction.forEach(em -> {
            try {
                em.execute(this.collector, em.getStreamId(), rejectedValues);
            } catch (FieldsMismatchException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    //since this is a splitter should we leave this here?
    //should we make this class abstract too?
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        if (this.conditionTrueAction.size() + this.conditionFalseAction.size() == 1) {
            //this is in case of direct emit
            if (this.conditionTrueAction.size() > 0)
                outputFieldsDeclarer.declare(new Fields(conditionTrueAction.get(0).getEmittedFields()));
            if (this.conditionFalseAction.size() > 0)
                outputFieldsDeclarer.declare(new Fields(conditionFalseAction.get(0).getEmittedFields()));
        } else {
            //declare the split of the stream into to two named streams
            conditionTrueAction.forEach(action -> outputFieldsDeclarer.declareStream(action.getStreamId(), new Fields(action.getEmittedFields())));
            conditionFalseAction.forEach(action -> outputFieldsDeclarer.declareStream(action.getStreamId(), new Fields(action.getEmittedFields())));
        }

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
