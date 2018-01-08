package abstraction;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jim on 2/1/2018.
 */
public class FieldFilter implements IAlgorithm, Serializable {

    protected String[] fieldsTobeRemoved;

    public FieldFilter(){}


    public FieldFilter(String... fieldsTobeRemoved) {
        this.fieldsTobeRemoved = fieldsTobeRemoved;
    }


    public FieldFilter build() {
        return this;
    }

    @Override
    public Values executeAlgorithm(Tuple tuple) {
        ArrayList<String> stringFieldsToBeRemoved = new ArrayList<>();
        Collections.addAll(stringFieldsToBeRemoved,fieldsTobeRemoved);
        Values values = new Values();
        ArrayList<String> stringFields = (ArrayList<String>) tuple.getFields().toList();
        stringFields.removeAll(stringFieldsToBeRemoved);//exception thrown if removeAll doesnt find all matches
        for(String field : stringFields){
            values.add(tuple.getValueByField(field));
        }

        return values;
    }


}
