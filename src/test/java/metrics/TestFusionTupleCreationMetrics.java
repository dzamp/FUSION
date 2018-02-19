package metrics;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.storm.shade.org.eclipse.jetty.util.BlockingArrayQueue;
import org.apache.storm.tuple.Values;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Before;
import org.junit.Test;
import tuple.abstraction.FusionTuple;
import tuple.abstraction.Meta;
import util.OutputFieldsClassMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;


public class TestFusionTupleCreationMetrics {
    protected BlockingQueue<Pair<String, MqttMessage>> messageQueue;
    protected Random rand;
    protected OutputFieldsClassMapper mapper;
    protected List<Long> timeDeltaList;
    protected List<Long> timeDeltaFusionBuildList;
    protected int size = 2_000_000;
    protected String[] streams;


    public static void warmup(BlockingQueue<Pair<String, MqttMessage>> messageQueue, OutputFieldsClassMapper mapper, String[] streams, List<Long> timeDeltaFusionBuildList) {
        System.out.println("TestFusionTupleCreationMetrics.warmup");
        for(int i=0;i<10_000;i++) {
            long t1 = System.nanoTime();
            Pair<String, MqttMessage> messagePair = null;
            messagePair = messageQueue.poll(); //resolve to polling
            if (messagePair != null) {
                Values values = mapper.mapToValues(messagePair.getRight().toString());
                if (values != null && values.size() > 0) {
//                    long t2 = System.nanoTime();
//                    timeDeltaList.add(t2 - t1);
                    long t3 = System.nanoTime();
                    for (String stream : streams) {
                        List<Meta> metadataList = new ArrayList<>();
                        int k = 0;
                        for (String field : getFieldNames()) {
                            metadataList.add(new Meta(field, i, mapper.getClassname(k)));
                            k++;
                        }

                        FusionTuple ftuple = new FusionTuple();
                        ftuple.setStreamMetadata(stream, metadataList);
                        List<Values> vals = new ArrayList<>();
                        vals.add(values);
                        ftuple.addValuestoStream(stream, vals);
                    }
                    timeDeltaFusionBuildList.add(System.nanoTime() - t3);
                }
            }
        }

//        long total = timeDeltaList.stream().mapToLong(value -> value).sum();
//        double mediantime = ((double) total / (double) timeDeltaList.size());
//        long roundedTime = Math.round(mediantime);
//        System.out.println("Reporting average time for parsing " + timeDeltaList.size() + " elements = " + mediantime + " rounded average " + roundedTime + "ns");

        long total = timeDeltaFusionBuildList.stream().mapToLong(value -> value).sum();
        double mediantime = ((double) total / (double) timeDeltaFusionBuildList.size());
        long roundedTime = Math.round(mediantime);
        System.out.println("Reporting warmup average time for constructing fusion tuples " + timeDeltaFusionBuildList.size() + " elements = " + mediantime + " rounded average " + roundedTime + "ns");
        timeDeltaFusionBuildList.clear();
    }

    private static String[] getFieldNames() {
        return new String[]{"id", "value", "timestamp"};
    }

    @Before
    public void setUp() {
        System.out.println("setUp");
        messageQueue = new BlockingArrayQueue<>();
        rand = new Random(123456789L);
        mapper = new OutputFieldsClassMapper(getClassNames());
        timeDeltaList = new ArrayList<>();
        timeDeltaFusionBuildList = new ArrayList<>();
        streams = getStreams();
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

//        warmup(messageQueue,mapper,streams,timeDeltaFusionBuildList);


    }


//    @Test
//    public void testSimpleMessageMetric2() {
//        System.out.print("TestFusionTupleCreationMetrics.testSimpleMessageMetric2  :");
//        measureParsingTime();
//    }
//
//    @Test
//    public void testSimpleMessageMetric3() {
//        System.out.print("TestFusionTupleCreationMetrics.testSimpleMessageMetric3  :");
//        measureParsingTime();
//    }
//    @Test
//    public void testSimpleMessageMetric4() {
//        System.out.print("TestFusionTupleCreationMetrics.testSimpleMessageMetric4  :");
//        measureParsingTime();
//    }
//    @Test
//    public void testSimpleMessageMetric5() {
//        System.out.print("TestFusionTupleCreationMetrics.testSimpleMessageMetric5  :");
//        measureParsingTime();
//    }
//
//    @Test
//    public void testSimpleMessageMetric7() {
//        System.out.print("TestFusionTupleCreationMetrics.testSimpleMessageMetric7  :");
//        measureParsingTime();
//    }
//
//    @Test
//    public void testSimpleMessageMetric6() {
//        System.out.print("TestFusionTupleCreationMetrics.testSimpleMessageMetric6  :");
//        measureParsingTime();
//    }
//
//    @Test
//    public void testSimpleMessageMetric8() {
//        System.out.print("TestFusionTupleCreationMetrics.testSimpleMessageMetric8  :");
//        measureParsingTime();
//    }

    @Test
    public void testSimpleMessageMetric() {
        System.out.println("TestFusionTupleCreationMetrics.testSimpleMessageMetric :");
        for(int i=1;i<=10;i++) {
            System.out.print("round " + i + " ");
            measureParsingTime();
            timeDeltaFusionBuildList.clear();
        }
    }

    private void measureParsingTime() {
        for(int i=0; i<200_000; i++) {
            long t1 = System.nanoTime();
            Pair<String, MqttMessage> messagePair = null;
            messagePair = messageQueue.poll(); //resolve to polling
            if (messagePair != null) {
                Values values = mapper.mapToValues(messagePair.getRight().toString());
                if (values != null && values.size() > 0) {
//                    long t2 = System.nanoTime();
//                    timeDeltaList.add(t2 - t1);
                    long t3 = System.nanoTime();
                    for (String stream : streams) {
                        List<Meta> metadataList = new ArrayList<>();
                        int k = 0;
                        for (String field : getFieldNames()) {
                            metadataList.add(new Meta(field, i, mapper.getClassname(k)));
                            k++;
                        }

                        FusionTuple ftuple = new FusionTuple();
                        ftuple.setStreamMetadata(stream, metadataList);
                        List<Values> vals = new ArrayList<>();
                        vals.add(values);
                        ftuple.addValuestoStream(stream, vals);
                    }
                    timeDeltaFusionBuildList.add(System.nanoTime() - t3);
                }
            }
        }

//        long total = timeDeltaList.stream().mapToLong(value -> value).sum();
//        double mediantime = ((double) total / (double) timeDeltaList.size());
//        long roundedTime = Math.round(mediantime);
//        System.out.println("Reporting average time for parsing " + timeDeltaList.size() + " elements = " + mediantime + " rounded average " + roundedTime + "ns");

        long total = timeDeltaFusionBuildList.stream().mapToLong(value -> value).sum();
        double mediantime = ((double) total / (double) timeDeltaFusionBuildList.size());
        long roundedTime = Math.round(mediantime);
        System.out.println("Reporting average time for constructing fusion tuples " + timeDeltaFusionBuildList.size() + " elements = " + mediantime + " rounded average " + roundedTime + "ns");
    }

    private String[] getClassNames() {
        return new String[]{"java.lang.String", "java.lang.Integer", "java.lang.Long"};
    }

    private String[] getStreams() {
        return new String[]{"default"};
    }

}

