package tuple.abstraction;

import algorithms.FieldFilter;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.List;
import java.util.Map;

public class FFieldFilter extends FieldFilter {

    public FFieldFilter(String... fieldsTobeRemoved) {
        super(fieldsTobeRemoved);
    }

    @Override
    public void setInputSources(Map<String, List<String>> inputFieldsFromSources) {
        super.setInputSources(inputFieldsFromSources);
    }

    @Override
    public String[] transformFields(String[] incomingFields) {
        return new String[]{"fusionTuple"};
    }

    @Override
    public Values executeAlgorithm(Tuple tuple) {
        FusionTuple ftuple = (FusionTuple) tuple.getValue(0);
        for (String streamId : this.inputFieldsFromSources.keySet()) {
//            List<Values> incomingManyOrOne = ftuple.getStreamValues(streamId);
            for (String fieldName : fieldsTobeRemoved) {
                ftuple.removeFieldAndMetadataFromStream(streamId, fieldName);
            }

        }
        return new Values(ftuple);
    }

}
