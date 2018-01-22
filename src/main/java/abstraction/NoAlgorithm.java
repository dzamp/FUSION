package abstraction;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class NoAlgorithm  implements IAlgorithm, Serializable{
    protected Map<String, List<String>> inputFieldsFromSources;

    @Override
    public Values executeAlgorithm(Tuple tuple) {
      Values values = new Values();
      values.addAll(tuple.getValues());
      return values;
    }

    @Override
    public void setInputSources(Map<String, List<String>> inputFieldsFromSources) {
        this.inputFieldsFromSources = inputFieldsFromSources;
    }

    @Override
    public void prepare() {

    }

    @Override
    public String[] transformFields(String[] incomingFields) {
        return incomingFields;
    }
}
