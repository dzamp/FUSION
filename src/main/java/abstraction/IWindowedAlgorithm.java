package abstraction;

import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

public interface IWindowedAlgorithm {
    Values executeWindowedAlgorithm(TupleWindow tupleWindow);
    int getWindowCount();
    BaseWindowedBolt.Duration getWindowDuration();
}
