package metrics;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.Clock;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.storm.shade.org.eclipse.jetty.util.BlockingArrayQueue;
import org.apache.storm.tuple.Values;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import tuple.abstraction.FusionTuple;
import tuple.abstraction.Meta;
import util.OutputFieldsClassMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * Created by jim on 17/2/2018.
 */

@AxisRange(min = 0, max = 0.30)
@BenchmarkMethodChart(filePrefix = "map-types-benchmark-barchart")
//@BenchmarkOptions(clock = Clock.NANO_TIME)
public class TestFusionTupleBenchMark extends AbstractBenchmark {


    protected static BlockingQueue<Pair<String, MqttMessage>> messageQueue;
    protected static Random rand;
    protected static OutputFieldsClassMapper mapper;
    protected List<Long> timeDeltaList;
    protected static List<Long> timeDeltaFusionBuildList;
    protected final static int size = 2_000_000;
    protected static String[] streams;

    private static String[] getFieldNames() {
        return new String[]{"id", "value", "timestamp"};
    }

    @BeforeClass
    public static void setUp() {
        System.out.println("setUp");
        messageQueue = new BlockingArrayQueue<>();
        rand = new Random(123456789L);
        mapper = new OutputFieldsClassMapper(getClassNames());

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


    }

//    @Test
    public void testSimpleMessageMetric() {
        System.out.print("TestFusionTupleCreationMetrics.testSimpleMessageMetric :");
        measureParsingTime();
    }

    private void measureParsingTime() {
        while (!messageQueue.isEmpty()) {
            long t1 = System.nanoTime();
            process();
            timeDeltaFusionBuildList.add(System.nanoTime() - t1);
        }

        long total = timeDeltaFusionBuildList.stream().mapToLong(value -> value).sum();
        double mediantime = ((double) total / (double) timeDeltaFusionBuildList.size());
        long roundedTime = Math.round(mediantime);
        System.out.println("Reporting average time for constructing fusion tuples " + timeDeltaFusionBuildList.size() + " elements = " + mediantime + " rounded average " + roundedTime + "ns");
    }

    @Test
    @BenchmarkOptions(benchmarkRounds = 2_200_000, warmupRounds = 200_000, clock = Clock.NANO_TIME)
    public void process() {
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
                    int i = 0;
                    for (String field : getFieldNames()) {
                        metadataList.add(new Meta(field, i, mapper.getClassname(i)));
                        i++;
                    }
                    FusionTuple ftuple = new FusionTuple();
                    ftuple.setStreamMetadata(stream, metadataList);
                    List<Values> vals = new ArrayList<>();
                    vals.add(values);
                    ftuple.addValuestoStream(stream, vals);
                }
            }
        }
    }

    private static String[] getClassNames() {
        return new String[]{"java.lang.String", "java.lang.Integer", "java.lang.Long"};
    }

    private static String[] getStreams() {
        return new String[]{"default"};
    }

}
