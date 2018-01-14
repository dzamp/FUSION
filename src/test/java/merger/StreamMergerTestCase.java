package merger;

import abstraction.StreamMerger;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StreamMergerTestCase {
    Map<String, Map<String, List<String>>> streamsToFieldsMap;

    public static Answer tupleWindowMock() {
        return new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Tuple[] tuples = new Tuple[]{mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class),
                        mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class),
                        mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class),
                        mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class), mock(Tuple.class)};
                for (int i = 0; i < 20; i++) {
                    if (i % 2 == 0) {
                        // this is temperature-spout
                        when(tuples[i].getSourceComponent()).thenReturn("temperature-spout");
                        double temperature = Math.random() * 10 + 30;
                        when(tuples[i].getValues()).thenReturn(new Values("temp",temperature,System.currentTimeMillis()));
                    } else {
                        // humidity-spout
                        when(tuples[i].getSourceComponent()).thenReturn("humidity-spout");
                        double humidity = Math.random() * 10 + 20;
                        when(tuples[i].getValues()).thenReturn(new Values("hum",humidity,System.currentTimeMillis()));
                    }

                }

                return Arrays.asList(tuples);
            }
        };
    }

    @Before
    public void setUp() {
        //this is how the expected map is from storm
        streamsToFieldsMap = new HashMap<>();
        Map<String, List<String>> value = new HashMap<>();
        value.put("default", Arrays.asList("id, temperature, timestamp"));
        streamsToFieldsMap.put("temperature-spout", value);
        value = new HashMap<>();
        value.put("default", Arrays.asList("id, humidity, timestamp"));
        streamsToFieldsMap.put("humidity-spout", value);



    }

    @Test
    public void testWindowWithFieldTimestamp() {
        StreamMerger merger = new StreamMerger();
        merger.setInputSources(streamsToFieldsMap);
        TupleWindow tupleWindow = mock(TupleWindow.class);
        //TODO get() or getNew() ???
        when(tupleWindow.get()).thenAnswer(tupleWindowMock());

        Values values = merger.executeWindowedAlgorithm(tupleWindow);

//        values.get
        ArrayList<Values> humidityValues = (ArrayList<Values>) ((HashMap<String,List<Values>>)values.get(0)).get("humidity-spout");
        humidityValues.forEach(objects -> {
            assertEquals("hum",objects.get(0));
        });

        ArrayList<Values> temperatureValues = (ArrayList<Values>) ((HashMap<String,List<Values>>)values.get(0)).get("temperature-spout");
        humidityValues.forEach(objects -> {
            assertEquals("temp",objects.get(0));
        });


        System.out.println("breakpoint!");

    }


}
