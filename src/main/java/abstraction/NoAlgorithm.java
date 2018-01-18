package abstraction;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.Serializable;

public class NoAlgorithm  implements IAlgorithm, Serializable{
    @Override
    public Values executeAlgorithm(Tuple tuple) {
      Values values = new Values();
      values.addAll(tuple.getValues());
      return values;
    }

    @Override
    public String[] transformFields(String[] incomingFields) {
        return incomingFields;
    }
}
