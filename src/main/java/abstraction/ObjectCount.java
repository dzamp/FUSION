package abstraction;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectCount implements IAlgorithm, Serializable {
    private HashMap<Object,Integer> countMap;
    protected Map<String, List<String>> inputFieldsFromSources;

    public ObjectCount(){
        this.countMap = new HashMap<>();
    }

    @Override
    public Values executeAlgorithm(Tuple tuple) {
        Object value = tuple.getValue(0);
        if(!countMap.containsKey(value)){
            countMap.put(value,0);
        }else {
            countMap.put(value, countMap.get(value)+1);
        }
        return (Values) tuple.getValues();
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
