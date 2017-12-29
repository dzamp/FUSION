package shewhart;

import abstraction.ShewhartAgorithm;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Time;
import org.apache.storm.windowing.TupleWindow;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class ShewhartTestCase {

    public class ShewhartAlgorithm_ extends ShewhartAgorithm {
        public int alarmInstances = 0;
        private PrintWriter writer;

        private void setWriter(String fileName) {
            try {
                writer = new PrintWriter(fileName + Time.currentTimeMillis(), "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        public ShewhartAlgorithm_(String filename) {
            super();
            setWriter(filename);
        }

        @Override
        public Values executeWindowedAlgorithm(TupleWindow tupleWindow) {
            int outcome = 0;
            int n = tupleWindow.get().size();
            for (Tuple input : tupleWindow.getNew()) {
                double value = (double) (positionInStream == -1 ? (double) input.getValueByField(this.fieldInStream) : input.getDouble(positionInStream));
                double curr_mean = previousState.getMean() + ((1.0 / n) * (value - previousState.getMean()));
                double curr_Variance = Math.sqrt(
                        (1.0 / n) * (
                                ((n - 1.0) * Math.pow(previousState.getVariance(), 2))
                                        + ((value - previousState.getMean()) * (value - curr_mean))
                        )
                );

                double UCL = curr_mean + kplus * curr_Variance;
                double LCL = curr_mean - kminus * curr_Variance;
                this.previousState.nextState(curr_mean, curr_Variance);
                //TODO should I return which boundary has been breached? UCL or LCL
                if (value > UCL || value < LCL) {
                    outcome = 1;
                    writer.println("Current value: " + value + " mean: " + curr_mean + " variance: "
                            + curr_Variance + " UCL: " + UCL + " LCL: " + LCL + " shewhart: " + 1);
                    alarmInstances++;
                } else {
                    outcome = 0;
                    writer.println("Current value: " + value + " mean: " + curr_mean + " variance: "
                            + curr_Variance + " UCL: " + UCL + " LCL: " + LCL + " shewhart: " + 0);
                }
            }
            return new Values(tupleWindow.get(), outcome);
        }
    }

    @Test
    public void testShewhart() {


    }


}
