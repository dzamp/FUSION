import algorithms.MValuesShewhartBolt;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Tuple;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShewhartBoltTest extends StormTestCase {

    @Test
    public void testHewhartBolt() {
        MValuesShewhartBolt shewhartBolt = new MValuesShewhartBolt(0,3,3,500);
        final OutputCollector collector = mock(OutputCollector.class);
        Map config = new HashMap();
        shewhartBolt.prepare(config, null, collector);
        Tuple t = mock(Tuple.class);

        when(t.getDouble(0)).thenAnswer(new Answer() {
            private int count = 0;
            public Object answer(InvocationOnMock invocation) {
                count++;
//                if(count % 100 == 0)
//                    return 15000.0;
                return (10000 * Math.random());
            }
        });

        for(int i=0; i<100000; i++){
            shewhartBolt.execute(t);
        }

    }

}
