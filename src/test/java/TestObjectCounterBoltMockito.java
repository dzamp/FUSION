import actions.BoltEmitter;
import algorithms.MValuesThresholdBolt;
import algorithms.ObjectCounterBolt;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.internal.matchers.Any;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by jim on 15/11/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestObjectCounterBoltMockito  {



    @Test
    public void testCounterBolt() {
        System.out.println("TestObjectCounterBoltMockito.testCounterBolt");
        Tuple t = mock(Tuple.class);
        when(t.getValue(0)).thenReturn(new String("first"));
        ObjectCounterBolt bolt = new ObjectCounterBolt();
        Map conf = new HashMap();
        OutputCollector collector = mock(OutputCollector.class);
        bolt.prepare(conf, null,collector);

        bolt.execute(t);



    }


    @Test
    public void testThresholdBolt() {
        System.out.println("TestObjectCounterBoltMockito.testThresholdBolt");
        Tuple t = mock(Tuple.class);
        MValuesThresholdBolt bolt = new MValuesThresholdBolt("java.lang.Integer",
                80,2, "gt");

        when(t.getValue(0)).thenReturn("id");
        when(t.getValue(1)).thenReturn("anotherString");
        when(t.getValue(2)).thenReturn(88);
        Map conf = new HashMap();
        OutputCollector collector = mock(OutputCollector.class);
        bolt.prepare(conf, null,collector);
        bolt.addConditionTrueAction(new BoltEmitter(new String[]{"id","anotherString","value"}));
        when(t.getValues()).thenReturn(new Values("id","anotherString",88));
        OutputFieldsDeclarer declarer = mock(OutputFieldsDeclarer.class);
//        doReturn("ok").when(declarer.declar
// e(new Fields("id","anotherString","value")));
        bolt.declareOutputFields(declarer);
        bolt.execute(t);


    }

}
