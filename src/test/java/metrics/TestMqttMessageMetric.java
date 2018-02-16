package metrics;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.storm.shade.org.eclipse.jetty.util.BlockingArrayQueue;
import org.apache.storm.tuple.Values;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Before;
import org.junit.Test;
import util.OutputFieldsClassMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.assertEquals;

public class TestMqttMessageMetric {
    protected BlockingQueue<Pair<String, MqttMessage>> messageQueue;
    protected Random rand;
    protected OutputFieldsClassMapper mapper;
    protected List<Long> timeDeltaList;
    protected int size = 100000;

    @Before
    public void setUp() {
        messageQueue = new BlockingArrayQueue<>();
        rand = new Random(123456789L);
        mapper = new OutputFieldsClassMapper("java.lang.String", "java.lang.Integer", "java.lang.Long");
        timeDeltaList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int val = rand.nextInt();
            long time = System.currentTimeMillis();
            String message = "patient-1," + val + "," + time;
            try {
                messageQueue.put(new ImmutablePair<>("health_monitor/blood_pressure",
                        new MqttMessage(message.getBytes())));
//                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSimpleMessageMetric() {
        measureParsingTime();
    }

    @Test
    public void testSimpleMessageMetricAgain() {
        measureParsingTime();
    }

    @Test
    public void testSimpleMessageMetricThirdTime() {
        measureParsingTime();
    }


    private void measureParsingTime() {
        while (!messageQueue.isEmpty()) {
            long t1 = System.nanoTime();
            Pair<String, MqttMessage> messagePair = null;
            messagePair = messageQueue.poll(); //resolve to polling
            if (messagePair != null) {
                Values values = mapper.mapToValues(messagePair.getRight().toString());
                if (values != null && values.size() > 0) {
                    long t2 = System.nanoTime();
                    timeDeltaList.add(t2 - t1);
                }
            }
        }

        long total = timeDeltaList.stream().mapToLong(value -> value).sum();
        long total2 = 0;
        for (Long aTimeDeltaList : timeDeltaList) total2 += aTimeDeltaList;
        assertEquals(total, total2);
        double mediantime = ((double) total / (double) timeDeltaList.size());
        long roundedTime = Math.round(mediantime);
        System.out.println("Reporting average time for " + timeDeltaList.size() + " each emitted tuple = " + mediantime);
        System.out.println("Reporting rounded average time " + roundedTime + "ns");
    }
}
