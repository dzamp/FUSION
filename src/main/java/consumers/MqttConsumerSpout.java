package consumers;

import actions.SpoutAction;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.storm.shade.org.eclipse.jetty.util.BlockingArrayQueue;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Time;
import org.apache.storm.utils.Utils;
import org.eclipse.paho.client.mqttv3.*;

import java.util.*;
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
public class MqttConsumerSpout implements MqttCallback, FusionIRichSpout {
    protected String brokerUrl;
    protected String clientId;
    protected String topic;
    protected Map configMap;
    protected BlockingQueue<Pair<String, MqttMessage>> messageQueue;
    protected SpoutOutputCollector collector;
    protected TopologyContext ctx;
    protected List<SpoutAction> emitActions = null;
    protected Logger log;
    protected Map<String, List<String>> outcomingStreamsFieldsMap;
    protected String[] fieldNames = null;
    protected List<String> streamIds = null;
    protected MqttClient client;
    protected int qos = 1;
    private boolean outgoingFieldsSet = false;
    protected OutputFieldsClassMapper mapper;
//    protected OutputFieldsDeclarer declarer;

    public MqttConsumerSpout(String brokerUrl, String clientId, String topic) {
        this.brokerUrl = brokerUrl;
        this.clientId = clientId;
        this.topic = topic;
        this.mapper = new OutputFieldsClassMapper();
    }


    public MqttConsumerSpout withFields(String... fieldNames) {
        this.fieldNames = fieldNames;
        return this;
    }

    public MqttConsumerSpout addOutGoingStream(String streamId) {
        if (this.streamIds == null) new ArrayList<>();
        this.streamIds.add(streamId);
        return this;
    }

    public MqttConsumerSpout withRegex(String regex) {
        mapper.withRegex(regex);
        return this;
    }

    public MqttConsumerSpout withClasses(String... classNames) {
        mapper.withClasses(classNames);
        return this;
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        this.configMap = conf;
        this.ctx = context;
        messageQueue = new BlockingArrayQueue<>();
        log = Logger.getLogger(this.getClass());
        setMqttClientConnection(this.brokerUrl, this.clientId);
//        setOutboundStreams();
    }

    protected void setOutboundStreams() {
        this.outcomingStreamsFieldsMap = new HashMap<>();
        if (streamIds == null) //if no
            this.streamIds = Arrays.asList(new String[]{Utils.DEFAULT_STREAM_ID});
        if (fieldNames == null) throw new RuntimeException("No fields specified ");
        //todo throw exception
        //fill the outcomingStreamMap to be used by the declarer
        for (String stream : streamIds) {
            outcomingStreamsFieldsMap.put(stream, Arrays.asList(fieldNames));
        }
    }

    protected void setMqttClientConnection(String brokerUrl, String clientId) {
        try {
            client = new MqttClient(brokerUrl, clientId + Time.currentTimeMillis());
            client.connect();
            client.setCallback(this);
            client.subscribe(topic, qos);
        } catch (MqttException e) {
            log.error("Unable to connect to client " + clientId + " on topic: " + topic);
            e.printStackTrace();
        }
    }

    @Override
    public void nextTuple() {
        while (!messageQueue.isEmpty()) {
            Values values;
            try {
                Pair<String, MqttMessage> messagePair = messageQueue.take();
                values = mapper.mapToValues(messagePair.getRight().toString());
                if (values != null && values.size() > 0) {
                    emit(values);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void emit(Values values) {
        if (values != null && values.size() > 0) {
            this.outcomingStreamsFieldsMap.forEach((stream, stringFields) ->
                    //no need to use emit(values), since they it maps to this emit with stream-id = "default"
                    collector.emit(stream, values)
            );
        }
    }


    @Override
    public void ack(Object msgId) {

    }

    @Override
    public void fail(Object msgId) {

    }


    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        messageQueue.put(new ImmutablePair<>(topic.trim(), mqttMessage));
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


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //prepare the declarer
//        setOutboundStreams();
//        this.declarer = declarer;
        if (this.outcomingStreamsFieldsMap != null) {
            this.outcomingStreamsFieldsMap.forEach(
                    (stream, fieldStrings) -> declarer.declareStream(stream, new Fields(fieldStrings)));
        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }


    @Override
    public void setFields(String... fieldNames) {
        withFields(fieldNames);
        outgoingFieldsSet = true;
        setOutboundStreams();
    }

    @Override
    public void addOutgoingStreamName(String streamName) {
        if (this.streamIds == null) this.streamIds = new ArrayList<>();
        streamIds.add(streamName);
    }
}
