package algorithms.consumers;

import javafx.util.Pair;
import org.apache.storm.shade.org.eclipse.jetty.util.BlockingArrayQueue;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Values;
import org.eclipse.paho.client.mqttv3.*;


import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class MqttConsumerSpout implements IRichSpout, MqttCallback {
    private String brokerUrl;
    private String clientId;
    private String topic;
    private SpoutOutputCollector collector;
    private Map configMap;
    private TopologyContext ctx;
    private MqttClient client;
    private int qos = 1;
    protected BlockingQueue<Pair<String,MqttMessage>> messageQueue;

    public MqttConsumerSpout(String brokerUrl, String clientId, String topic) {
        this.brokerUrl = brokerUrl;
        this.clientId = clientId;
        this.topic = topic;
    }


    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        this.configMap = conf;
        this.ctx = context;
        messageQueue = new BlockingArrayQueue<>();
    }

    private void closeMqttClientConnection() {
        if (client.isConnected()) {
            try {
                client.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void close() {
        closeMqttClientConnection();
    }

    @Override
    public void activate() {
        try {
            client = new MqttClient(brokerUrl, clientId);
            client.connect();
            client.setCallback(this);
            client.subscribe(topic, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deactivate() {
        //should we close the connection here?
        System.out.println("MqttConsumerSpout.deactivate");
        try {
            client.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nextTuple() {
        while (!messageQueue.isEmpty()) {
            MqttMessage message = null;
            String topic = null;
            Values values = new Values();
            try {
                Pair<String,MqttMessage> p = messageQueue.take();
                message =  p.getValue();
                values.add(message.toString());
                topic = p.getKey();
                collector.emit(values);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void ack(Object msgId) {

    }

    @Override
    public void fail(Object msgId) {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        try {
            throw new Exception(throwable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        messageQueue.put(new Pair<>(topic.trim(), mqttMessage));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
