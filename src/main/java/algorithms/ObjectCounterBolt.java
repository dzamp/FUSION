package algorithms;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jim on 28/8/2017.
 */
public class ObjectCounterBolt extends BaseRichBolt {
    private HashMap<Object,Integer> countMap;

    OutputCollector collector;
    String id;

    public ObjectCounterBolt() {
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;
        countMap = new HashMap<>();
    }

    @Override
    public void execute(Tuple tuple) {
        Object value = tuple.getValue(0);
        if(!countMap.containsKey(value)){
            countMap.put(value,0);
        }else {
            countMap.put(value, countMap.get(value)+1);
        }

    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        //nothing
    }
}
