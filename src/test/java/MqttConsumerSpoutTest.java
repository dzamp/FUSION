//import actions.SpoutAction;
//import actions.SpoutEmitter;
import consumers.MqttConsumerSpout;
import flux.model.extended.MqttSpoutConfigDef;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Values;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;
/**
 * Created by jim on 16/11/2017.
 */
public class MqttConsumerSpoutTest {


    public class MqttConsumerSpoutFoo extends MqttConsumerSpout{

        public MqttConsumerSpoutFoo(String brokerUrl, String clientId, String topic) {
//            super(brokerUrl, clientId, topic);
            super(new MqttSpoutConfigDef());
        }

        @Override
        protected void setMqttClientConnection(String brokerUrl, String clientId) {
            //Do nothing
        }
        public BlockingQueue<Pair<String, MqttMessage>> getMessageQueue() {
            return this.messageQueue;
        }
    }


//    @Test
//    public void testSplitPattern() {
//        MqttConsumerSpout spout = new MqttConsumerSpoutFoo(null,"id1","health_monitor/blood_pressure");
//        SpoutOutputCollector collector = mock(SpoutOutputCollector.class);
//        SpoutAction action = new SpoutEmitter("testStream1",new String[]{"id", "value", "timestamp"}, ",",String.class.getCanonicalName(),Integer.class.getCanonicalName(),Long.class.getCanonicalName());
//        SpoutAction action2 = new SpoutEmitter("testStream2",new String[]{"id", "value", "timestamp"}, ",",String.class.getCanonicalName(),Integer.class.getCanonicalName(),Long.class.getCanonicalName());
////        spout.addEmitAction(action);
////        spout.addEmitAction(action2);
//        spout.open(null,null,collector);
//        OutputFieldsDeclarer declarer = mock(OutputFieldsDeclarer.class);
//        spout.declareOutputFields(declarer);
//        MqttMessage message = new MqttMessage("UUID,8888,12334254".getBytes());
//        try {
//            spout.messageArrived("what is this string",message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        BlockingQueue<Pair<String,MqttMessage>> queue= ((MqttConsumerSpoutFoo)spout).getMessageQueue();
//        Pair<String,MqttMessage> messgaePair=null;
//        try {
//            messgaePair = queue.take();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        assertNotNull(messgaePair);
//        assertEquals(message.toString(),messgaePair.getRight().toString());
//        spout.nextTuple();
//        Values expected = ((SpoutEmitter)action).mapToValues(message.toString(),",", new Class[]{String.class,Integer.class,Long.class});
//        assertArrayEquals(new Object[]{"UUID",(int)8888,(long)12334254},expected.toArray());
//
//    }



}
