package consumers;

import flux.model.extended.MqttSpoutConfigDef;
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


    private final MqttConfig config;
    protected BlockingQueue<Pair<String, MqttMessage>> messageQueue;
    protected Map<String, List<String>> outcomingStreamsFieldsMap;
    protected SpoutOutputCollector collector;
    protected TopologyContext ctx;
    protected Logger log;
    protected MqttClient client;
    private boolean outgoingFieldsSet = false;
    private Map configMap = null;


    public MqttConsumerSpout(MqttSpoutConfigDef def) {
        this.config = def.createMqttConfig();
    }


    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        this.configMap = conf;
        this.ctx = context;
        messageQueue = new BlockingArrayQueue<>();
        log = Logger.getLogger(this.getClass());
        setMqttClientConnection(this.config.getBrokerUrl(), this.config.getClientId());
    }

    protected void setOutboundStreams() {
        this.outcomingStreamsFieldsMap = new HashMap<>();
        if (config.getStreamIds() == null) //if no
            this.config.withStreamIds(new String[]{Utils.DEFAULT_STREAM_ID});
        if (config.getFieldNames() == null) throw new RuntimeException("No fields specified ");
        //todo throw exception
        //fill the outcomingStreamMap to be used by the declarer
        for (String stream : config.getStreamIds()) {
            outcomingStreamsFieldsMap.put(stream, Arrays.asList(config.getFieldNames()));
        }
    }

    protected void setMqttClientConnection(String brokerUrl, String clientId) {
        try {
            client = new MqttClient(brokerUrl, clientId + Time.currentTimeMillis());
            client.connect();
            client.setCallback(this);
            client.subscribe(config.getTopic(), config.getQos());
        } catch (MqttException e) {
            log.error("Unable to connect to client " + clientId + " on topic: " + config.getTopic());
            e.printStackTrace();
        }
    }

    @Override
    public void nextTuple() {
        while (!messageQueue.isEmpty()) {
            Values values;
            try {
                Pair<String, MqttMessage> messagePair = messageQueue.take();
                values = config.mapper.mapToValues(messagePair.getRight().toString());
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
        messageQueue.put(new ImmutablePair<>(config.getTopic().trim(), mqttMessage));
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
        config.withFieldNames(fieldNames);
        outgoingFieldsSet = true;
        setOutboundStreams();
    }

    @Override
    public void addOutgoingStreamName(String streamName) {
        if (this.config.getStreamIds() == null) this.config.streamIds = new ArrayList<>();
        this.config.streamIds.add(streamName);
    }
}
