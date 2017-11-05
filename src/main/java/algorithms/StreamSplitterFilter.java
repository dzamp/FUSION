package algorithms;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 * Created by jim on 5/11/2017.
 */
public abstract class StreamSplitterFilter implements IRichBolt{
    //Abstract class?

    private TopologyContext topologyContext;
    private OutputCollector collector;
    private Map configMap;
    // 1. Is the threshold something we need?
    // 2. What family of algorithms will a splitter represent?
    // 3. Thresholding is one of them that works with numeric data
    // 4. Furthermore we need to think what kind of superclass will handle the actions and the declarers
    // 5. Lets say that Object counter might need to split some stream also, what are we going to do there?
    // 6. How will we support multiple values? How will we know which one will be used for any job?
    //
    private Number threshold;
    private Class clazz;


    public StreamSplitterFilter() {

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

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
