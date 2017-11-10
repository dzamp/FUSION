package consumers;

import actions.Action;
import actions.SpoutAction;
import actions.SpoutEmitter;
import exceptions.FieldsMismatchException;
import javafx.util.Pair;
import org.apache.storm.shade.org.eclipse.jetty.util.BlockingArrayQueue;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Time;
import org.eclipse.paho.client.mqttv3.*;


import java.util.Map;
import java.util.concurrent.BlockingQueue;


/**
 * This class implements an mqtt spout that connects to a specific topic.
 * The requirements for this class to work are
 * - String serverURI -> the connection to the broker
 * - String clientId -> the id of the client
 * - String topic -> the topic of subscription
 * - String regex -> regex so that we can split the incoming values
 * - String ...args / Class args the classes that will be used to emit the split values
 * Otherwise in case we want to emit to only one bolt we might as well use the second constructor and supply only one emitAction
 */
public class MqttConsumerSpout extends ConsumerSpout implements MqttCallback {
    private MqttClient client;
    private int qos = 1;
    private String regex = null;
    protected BlockingQueue<Pair<String, MqttMessage>> messageQueue;
//    private List<SpoutEmitter> emitActions = null;

    //TODO error with strings
    //ERROR with strings classes!!!
    public MqttConsumerSpout(String serverURI, String clientId, String topic, String regex, Class... args) {
        super(serverURI, clientId, topic, regex, args);
    }

    public MqttConsumerSpout(String brokerUrl, String clientId, String topic) {
        super(brokerUrl, clientId, topic);
    }


    @Override
    public void nextTuple() {
        while (!messageQueue.isEmpty()) {
            MqttMessage message = null;
            String topic = null;
            Values values;
            try {
            Pair<String, MqttMessage> p = messageQueue.take();
            //TODO emitAction should handle the direct stream?
//            if (emitActions.size() == 1 && emitActions.get(0).getStreamId() == null)
//                try {
//                    emitActions.get(0).execute(collector, null, emitActions.get(0).mapToValues(p.getValue().toString(), regex, classMap));
//                } catch (FieldsMismatchException e) {
//                    e.printStackTrace();
//                }
//            else
                emitActions.forEach(action -> {
                    try {
                        action.execute(collector, action.getStreamId(), p.getValue().toString());
                    } catch (FieldsMismatchException e) {
                        e.printStackTrace();
                    }
                });
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
        super.open(conf, context, collector);
        messageQueue = new BlockingArrayQueue<>();
        try {
            client = new MqttClient(brokerUrl, clientId+ Time.currentTimeMillis());
            client.connect();
            client.setCallback(this);
            client.subscribe(topic, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
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
    }

    @Override
    public void activate() {

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
            throw new Exception("Connection lost or not established ", throwable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }


}
