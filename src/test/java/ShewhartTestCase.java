import abstraction.ShewhartAgorithm;
import edu.emory.mathcs.backport.java.util.Arrays;
import exceptions.AlgorithmDeclarationException;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Time;
import org.apache.storm.windowing.TupleWindow;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShewhartTestCase {
    public static Answer shewhartAnswer() {
        return new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Tuple[] tuples = new Tuple[]{mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class),
                        mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class),
                        mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class),
                        mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class)};
                double prob = Math.round(100 * Math.random());
                boolean outage = false;
                if(prob <= 2) outage = true;
                if(prob <= 1) outage = true;
                for(int i=0; i<20; i++){
                    if(prob <= 1) {
                        if(i % 7 == 0) when(tuples[i].getDouble(2)).thenReturn(-25000.0 );
                        else when(tuples[i].getDouble(2)).thenReturn(10000 * Math.random());
                    }
                    else if(prob <= 2) {
                        if(i % 5 == 0) when(tuples[i].getDouble(2)).thenReturn(25000.0 );
                        else when(tuples[i].getDouble(2)).thenReturn(10000 * Math.random());
                    }
                    else
                        when(tuples[i].getDouble(2)).thenReturn(10000 * Math.random());
                }
                return  Arrays.asList(tuples);
            }
        };
    }
    static String temp = "temperature";
    public static Answer shewhartAnswerFieldName() {
        return new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Tuple[] tuples = new Tuple[]{mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class),
                        mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class),
                        mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class),
                        mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class)};
                double prob = Math.round(100 * Math.random());
                boolean outage = false;
                if(prob <= 2) outage = true;
                if(prob <= 1) outage = true;
                for(int i=0; i<20; i++){
                    if(prob <= 1) {
                        if(i % 7 == 0) when(tuples[i].getValueByField(temp)).thenReturn(-25000.0 );
                        else when(tuples[i].getValueByField(temp)).thenReturn(10000 * Math.random());
                    }
                    else if(prob <= 2) {
                        if(i % 5 == 0) when(tuples[i].getValueByField(temp)).thenReturn(25000.0 );
                        else when(tuples[i].getValueByField(temp)).thenReturn(10000 * Math.random());
                    }
                    else
                        when(tuples[i].getValueByField(temp)).thenReturn(10000 * Math.random());
                }
                return  Arrays.asList(tuples);
            }
        };
    }

    public class ShewhartAlgorithm_ extends ShewhartAgorithm {
        public int alarmInstances = 0;
        private PrintWriter writer;

        public void setWriter(String fileName) {
            try {
                writer = new PrintWriter(fileName + Time.currentTimeMillis()+ ".txt", "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        public ShewhartAlgorithm_() {
            super();
        }

        public ShewhartAlgorithm_(double initialMean, double initialVariance){
            super(initialMean,initialVariance);
        }

        @Override
        public Values executeWindowedAlgorithm(TupleWindow tupleWindow) {
            int outcome = 0;
            int n = tupleWindow.getNew().size();
            double UCL,LCL;
            if(n==0) try {
                throw new AlgorithmDeclarationException("Cannot divide by zero");
            } catch (AlgorithmDeclarationException e) {
                e.printStackTrace();
            }
            for (Tuple input : tupleWindow.getNew()) {
                double value = (double) (positionInStream == -1 ? (double) input.getValueByField(this.fieldInStream) : input.getDouble(positionInStream));
                double curr_mean = previousState.getMean() + ((1.0 / n) * (value - previousState.getMean()));
                double curr_Variance = Math.sqrt(
                        (1.0 / n) * (
                                ((n - 1.0) * Math.pow(previousState.getVariance(), 2))
                                        + ((value - previousState.getMean()) * (value - curr_mean))
                        )
                );

                 UCL= curr_mean + kplus * curr_Variance;
                 LCL = curr_mean - kminus * curr_Variance;
                this.previousState.nextState(curr_mean, curr_Variance);
                //TODO should I return which boundary has been breached? UCL or LCL

                if (value > UCL || value < LCL) {
                    outcome = 1;
                    alarmInstances++;
                    if(writer!=null) writer.println("Current value: " + value + " mean: " + curr_mean + " variance: "
                            + curr_Variance + " UCL: " + UCL + " LCL: " + LCL + " shewhart: " + outcome);
                    break;
                } else {
                    if(writer!=null) writer.println("Current value: " + value + " mean: " + curr_mean + " variance: "
                            + curr_Variance + " UCL: " + UCL + " LCL: " + LCL + " shewhart: " + outcome);
                    outcome = 0;
                }

            }
            return new Values(tupleWindow.getNew(), outcome);
        }
    }

    @Test
    public void testShewhart() {
        ShewhartAlgorithm_ shewhartAlgorithm = (ShewhartAlgorithm_) new ShewhartAlgorithm_(5000.0,3000).withPositionInStream(2);
        shewhartAlgorithm.setWriter("shewhart_");
        TupleWindow tupleWindow = mock(TupleWindow.class);

        when(tupleWindow.getNew()).thenAnswer(shewhartAnswer());
//        when(tupleWindow.getNew().size()).thenReturn(5);
        Values values;
        for(int i=0; i< 300; i++){
            values = shewhartAlgorithm.executeWindowedAlgorithm(tupleWindow);
            if((int)values.get(1)==1) System.out.println("we have a breach!");
        }
        System.out.println("END");
    }

    @Test
    public void testShewhartwithFieldName() {
        ShewhartAlgorithm_ shewhartAlgorithm = (ShewhartAlgorithm_) new ShewhartAlgorithm_().withFieldInStream(temp);
        TupleWindow tupleWindow = mock(TupleWindow.class);

        when(tupleWindow.getNew()).thenAnswer(shewhartAnswerFieldName());
//        when(tupleWindow.getNew().size()).thenReturn(5);
        Values values;
        for(int i=0; i< 300; i++){
            values = shewhartAlgorithm.executeWindowedAlgorithm(tupleWindow);
            if((int)values.get(1)==1) System.out.println("we have a breach!");
        }
        System.out.println("END");
    }

    @Test
    public void testShewhartwithK2() {

        ShewhartAlgorithm_ shewhartAlgorithm = (ShewhartAlgorithm_) new ShewhartAlgorithm_(5000,3000).withFieldInStream(temp).withKplus(2).withKminus(2);
        TupleWindow tupleWindow = mock(TupleWindow.class);
        shewhartAlgorithm.setWriter("shewhartK2_");
        when(tupleWindow.getNew()).thenAnswer(shewhartAnswerFieldName());
//        when(tupleWindow.getNew().size()).thenReturn(5);
        Values values;
        for(int i=0; i< 300; i++){
            values = shewhartAlgorithm.executeWindowedAlgorithm(tupleWindow);
            if((int)values.get(1)==1) System.out.println("we have a breach!");
        }
        System.out.println("END");
    }

}
