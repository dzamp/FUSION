package algorithms;

import abstraction.IAlgorithm;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.Serializable;
import java.util.*;

/**
 * Created by jim on 2/1/2018.
 */

/**
 * This cuts a field from the stream that you dont need. YTou have to respecify the fields of the stream to the bolt again
 */
public class FieldFilter implements IAlgorithm, Serializable {

    protected String[] fieldsTobeRemoved;
    protected Map<String, List<String>> inputFieldsFromSources;

    public FieldFilter() {
    }


    public FieldFilter(String... fieldsTobeRemoved) {
        this.fieldsTobeRemoved = fieldsTobeRemoved;
    }


    public FieldFilter build() {
        return this;
    }

    @Override
    public Values executeAlgorithm(Tuple tuple) {
        ArrayList<String> stringFieldsToBeRemoved = new ArrayList<>();
        Collections.addAll(stringFieldsToBeRemoved, fieldsTobeRemoved);
        Values values = new Values();
        ArrayList<String> stringFields = (ArrayList<String>) tuple.getFields().toList();
        stringFields.removeAll(stringFieldsToBeRemoved);//exception thrown if removeAll doesnt find all matches
        for (String field : stringFields) {
            values.add(tuple.getValueByField(field));
        }

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
        Set<String> fields = new LinkedHashSet<>();
        fields.addAll(Arrays.asList(incomingFields));
        fields.removeAll(Arrays.asList(fieldsTobeRemoved));
        return fields.toArray(new String[fields.size()]);
    }
//
//    @Override
//    public Map<String, List<String>> getConfig() {
//        Map<String, List<String>> configMap =  new HashMap<>();
//        configMap.put("REMOVE_FIELDS", Arrays.asList(fieldsTobeRemoved));
//        return configMap;
//    }


}
