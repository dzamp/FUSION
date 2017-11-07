package algorithms.consumers;

import algorithms.actions.Action;
import javafx.util.Pair;
import org.apache.storm.shade.org.eclipse.jetty.util.BlockingArrayQueue;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Values;
import org.eclipse.paho.client.mqttv3.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class MqttConsumerSpout extends ConsumerSpout implements MqttCallback{
    private MqttClient client;
    private int qos = 1;
    private String regex =null;
    protected BlockingQueue<Pair<String,MqttMessage>> messageQueue;
    private Class[] classMap= null;
    private List<Action> emitActions=null;

    public MqttConsumerSpout(String brokerUrl, String clientId, String topic, String regex, Class ...args) {
        super(brokerUrl,clientId,topic,regex,args);
    }

    public MqttConsumerSpout(String brokerUrl, String clientId, String topic) {
       super(brokerUrl,clientId,topic);
    }



    @Override
    public void nextTuple() {
        while (!messageQueue.isEmpty()) {
            MqttMessage message = null;
            String topic = null;
            Values values;
            try {
                Pair<String,MqttMessage> p = messageQueue.take();
                values= mapToValues(p.getValue().toString() ,p.getKey(),regex, classMap);
                if(emitActions.size()==1 && emitActions.get(0).getStreamId()==null) collector.emit(values);
                else emitActions.forEach(action -> collector.emit(action.getStreamId(), values));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        messageQueue.put(new Pair<>(topic.trim(), mqttMessage));
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        super.open(conf,context,collector);
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
    public void connectionLost(Throwable throwable) {
        try {
            throw new Exception(throwable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }


}
