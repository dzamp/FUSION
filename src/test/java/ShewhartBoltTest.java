//import algorithms.MValuesShewhartBolt;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Time;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShewhartBoltTest extends StormTestCase {

//    public class MValuesShewhartBoltTest extends MValuesShewhartBolt {
//        public int alarmInstances = 0;
//        private PrintWriter writer;
//
//        private void setWriter(String fileName) {
//            try {
//                writer = new PrintWriter(fileName + Time.currentTimeMillis(), "UTF-8");
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public MValuesShewhartBoltTest(String filename, int positionInStream, double kplus, double kminus, double initialMean, double initialVariance, int maxWindow) {
//            super(positionInStream, kplus, kminus, initialMean, initialVariance, maxWindow);
//            setWriter(filename);
//        }
//
//        public MValuesShewhartBoltTest(String filename, int positionInStream, double kplus, double kminus, int maxWindow) {
//            super(positionInStream, kplus, kminus, maxWindow);
//            setWriter(filename);
//        }
//
//        @Override
//        public void execute(Tuple input) {
//            double value = input.getDouble(positionInStream);
//            double curr_mean = previousState.getMean() + ((1.0 / n) * (value - previousState.getMean()));
//            double curr_Variance = Math.sqrt(
//                    (1.0 / n) * (
//                            ((n - 1.0) * Math.pow(previousState.getVariance(), 2))
//                                    + ((value - previousState.getMean()) * (value - curr_mean))
//                    )
//            );
//
//            double UCL = curr_mean + kplus * curr_Variance;
//            double LCL = curr_mean - kminus * curr_Variance;
//
//            if (value > UCL || value < LCL) {
//                emit(new Values(input, 1));
//                writer.println("Current value: " + value + " mean: " + curr_mean + " variance: "
//                        + curr_Variance + " UCL: " + UCL + " LCL: " + LCL + " shewhart: " + 1);
//                alarmInstances++;
//            } else {
//                emit(new Values(input, 0));
//                writer.println("Current value: " + value + " mean: " + curr_mean + " variance: "
//                        + curr_Variance + " UCL: " + UCL + " LCL: " + LCL + " shewhart: " + 0);
//            }
//            n++;
//            if (n == maxWindow + 2)
//                n = 2; //reset the window
//            this.previousState.nextState(curr_mean, curr_Variance);
//        }
//    }
//
//    @Test
//    public void testHewhartBoltWithK3() {
//        MValuesShewhartBoltTest shewhartBolt = new MValuesShewhartBoltTest("shewhartTestK3", 0, 3, 3, 10);
//        final OutputCollector collector = mock(OutputCollector.class);
//        Map config = new HashMap();
//        shewhartBolt.prepare(config, null, collector);
//        Tuple t = mock(Tuple.class);
//
//        when(t.getDouble(0)).thenAnswer(new Answer() {
//            private int count = 0;
//
//            public Object answer(InvocationOnMock invocation) {
//                //every 100 values emit a large value and every 30 values a small one
//                count++;
//                if (count % 100 == 0)
//                    return 19000.0;
//                if (count % 30 == 0)
//                    return -9000.0;
//                return (10000 * Math.random());
//            }
//        });
//        int N = 10000;
//        for (int i = 0; i < N; i++) {
//            shewhartBolt.execute(t);
//        }
////        StringBuilder builder = new StringBuilder("Alarm incidents were " + shewhartBolt.alarmInstances + " over " + N + " for k = 3. " +
////                "\nPercentage of alarms should be around 0.3% ");
////        builder.append("Actual percentage :" + (double)shewhartBolt.alarmInstances/N);
//    }
//
//    @Test
//    public void testHewhartBoltWithK2() {
//        MValuesShewhartBoltTest shewhartBolt = new MValuesShewhartBoltTest("shewhartTestK2", 0, 2, 2, 500);
//        final OutputCollector collector = mock(OutputCollector.class);
//        Map config = new HashMap();
//        shewhartBolt.prepare(config, null, collector);
//        Tuple t = mock(Tuple.class);
//
//        when(t.getDouble(0)).thenAnswer(new Answer() {
//            private int count = 0;
//
//            public Object answer(InvocationOnMock invocation) {
//                //every 100 values emit a large value and every 30 values a small one
//                return (10000 * Math.random());
//            }
//        });
//        int N = 10000;
//        for (int i = 0; i < N; i++) {
//            shewhartBolt.execute(t);
//        }
////        StringBuilder builder = new StringBuilder("Alarm incidents were " + shewhartBolt.alarmInstances + " over " + N + " for k = 3. " +
////                "\nPercentage of alarms should be around 0.3% ");
////        builder.append("Actual percentage :" + (double)shewhartBolt.alarmInstances/N);
//    }

}
