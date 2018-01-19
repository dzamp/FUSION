package abstraction;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.List;
import java.util.Map;

public interface IAlgorithm extends FieldTransformer
{
    Values executeAlgorithm(Tuple tuple);
    void setInputSources(Map<String,  List<String>> inputFieldsFromSources);

}
