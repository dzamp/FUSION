package algorithms.consumers;

import algorithms.actions.Action;
import algorithms.actions.SpoutAction;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ConsumerSpout implements IRichSpout {
    protected String brokerUrl;
    protected String clientId;
    protected String topic;
    protected SpoutOutputCollector collector;
    protected Map configMap;
    protected TopologyContext ctx;
    protected String regex = null;
    protected Class[] classMap = null;
    private List<SpoutAction> emitActions = null;


    public ConsumerSpout(String serverURI, String clientId, String topic, String regex, Class... args) {
        this.brokerUrl = serverURI;
        this.clientId = clientId;
        this.topic = topic;
        this.regex = regex;
        this.classMap = args;
        this.emitActions = new ArrayList<>();
    }

//    private List<Class> resolveClasses(String[] args){
//        if
//    }

    public ConsumerSpout(String brokerUrl, String clientId, String topic) {
        this.brokerUrl = brokerUrl;
        this.clientId = clientId;
        this.topic = topic;
        this.emitActions = new ArrayList<>();
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        this.configMap = conf;
        this.ctx = context;
    }


    public void addEmitAction(SpoutAction action) {
        this.emitActions.add(action);
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //prepare the declarer
        if (emitActions.size() == 1 && emitActions.get(0).getStreamId() == null)
            declarer.declare(new Fields(emitActions.get(0).getEmittedFields()));
        else
            for (Action action : emitActions)
                declarer.declareStream(action.getStreamId(), new Fields(action.getEmittedFields()));

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }



    @Override
    public void ack(Object msgId) {

    }

    @Override
    public void fail(Object msgId) {

    }

}
