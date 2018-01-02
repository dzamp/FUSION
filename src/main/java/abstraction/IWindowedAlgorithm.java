package abstraction;

import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

import java.util.List;
import java.util.Map;

public interface IWindowedAlgorithm {
    Values executeWindowedAlgorithm(TupleWindow tupleWindow);

    int getWindowCount();

    BaseWindowedBolt.Duration getWindowDuration();

    BaseWindowedBolt.Duration getWindowLag();

    String getTimestampField();

    public void setInputSources(Map<String, Map<String, List<String>>> inputFieldsFromSources);
}
