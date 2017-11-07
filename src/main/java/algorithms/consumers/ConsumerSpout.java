package algorithms.consumers;

import algorithms.actions.Action;
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

public abstract class ConsumerSpout implements IRichSpout, FieldMapper {
    protected String brokerUrl;
    protected String clientId;
    protected String topic;
    protected SpoutOutputCollector collector;
    protected Map configMap;
    protected TopologyContext ctx;
    private String regex =null;
    private Class[] classMap= null;
    private List<Action> emitActions=null;


    public ConsumerSpout(String brokerUrl, String clientId, String topic, String regex, Class ...args) {
        this.brokerUrl = brokerUrl;
        this.clientId = clientId;
        this.topic = topic;
        this.regex = regex;
        this.classMap = args;
        this.emitActions = new ArrayList<>();
    }

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


    public void addEmitAction(Action action){
        this.emitActions.add(action);
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //prepare the declarer
        if(emitActions.size()==1 && emitActions.get(0).getStreamId()==null) declarer.declare(new Fields(emitActions.get(0).getEmittedFields()));
        else
            for(Action action: emitActions) declarer.declareStream(action.getStreamId(), new Fields(action.getEmittedFields()));

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public Values mapToValues(String message, String topic, String regex, Class... args) {
        Values values = new Values();

        for(Class clazz: args){
            String[] stringValues = message.split(regex);
            for(String str: stringValues){
                values.add(args[0].cast(str));
            }
        }
        return values;
    }

    @Override
    public void ack(Object msgId) {

    }

    @Override
    public void fail(Object msgId) {

    }

}
