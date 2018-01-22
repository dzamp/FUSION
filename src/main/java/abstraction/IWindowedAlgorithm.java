package abstraction;

import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

import java.util.List;
import java.util.Map;

public interface IWindowedAlgorithm extends FieldTransformer{
    Values executeWindowedAlgorithm(TupleWindow tupleWindow);
     void setInputSources(Map<String,  List<String>> incomingStreamsFieldsMap);
     void prepare();
}
