package algorithms;

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

public abstract class GenericBolt implements IRichBolt {
    protected TopologyContext topologyContext;
    protected OutputCollector collector;
    protected Map configMap;
    protected List<BoltEmitter> actions;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.topologyContext = topologyContext;
        this.collector = outputCollector;
        this.configMap = map;
        this.actions = new ArrayList<>();
    }

    public void addAction(BoltEmitter boltEmitter) {
        actions.add(boltEmitter);
    }

    public void emit(Values values){
        this.actions.forEach(boltEmitter -> {
            try {
                boltEmitter.execute(this.collector, boltEmitter.getStreamId(), values);
            } catch (FieldsMismatchException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    //make this abstract?
    public abstract void execute(Tuple tuple);

    @Override
    public void cleanup(){
        //nothing here
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        if(this.actions.size() == 1) {
                if(this.actions.get(0).getStreamId()==null)
                    declarer.declare(new Fields(this.actions.get(0).getEmittedFields()));
        }
        else {
            actions.forEach(boltEmitter -> declarer.declareStream(boltEmitter.getStreamId(),new Fields(boltEmitter.getEmittedFields())));
        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}