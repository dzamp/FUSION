import algorithms.ObjectCounterBolt;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Tuple;
import org.jmock.Expectations;

import java.util.HashMap;
import java.util.Map;

public class TestRepeatVisitBolt extends StormTestCase{

    public void testExecute() {
        ObjectCounterBolt bolt = new ObjectCounterBolt();
        final OutputCollector collector = context.mock(OutputCollector.class);
        Map config = new HashMap();
        bolt.prepare(config, null, collector);
        final Tuple tuple = getTuple();
        context.checking(new Expectations(){{
            oneOf(tuple).getValue(0);will(returnValue("eeey"));
        }});
    }
}
