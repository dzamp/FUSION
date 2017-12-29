package abstraction;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public interface IAlgorithm {
    public Values executeAlgorithm(Tuple tuple);
}
