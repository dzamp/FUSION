package abstraction;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

import java.io.Serializable;
import java.util.*;

/**
 * Created by jim on 29/12/2017.
 */

//TODO somehow keep the metadata about the fields?

/**
 * Merges the incoming streams into a HashMap<Stream,List<Values>> per stream.
 * Additionally emits a HashMap<String,List<String>> of each field per stream
 * The declarer should have those 2 fields registered
 * <p>
 * Genika enas Merger 8a dexetai apo polla bolts/spouts kapoia pliroforia
 * Epeidh kaneis dn anagkazei thn ka8e roh na einai idia me thn allh arxika
 * Epomenws o Merger autos asxeta 8a ekpempsei
 * ena  Map<String,List<Values>> streamValues pou 8a periexei ola ta tuples per stream pou elave
 * kai ena Map<String,List<String>> meta pou 8a periexei to stream name kai ta StringFields pou antistoixoun se ka8e stream
 */

public class StreamMerger implements IWindowedAlgorithm, Serializable {

//    protected Map<String, Map<String, List<String>>> inputFieldsFromSources;
    protected Map<String, List<String>> inputFieldsFromSources;


    public void setInputSources(Map<String, List<String>> inputFieldsFromSources) {
        this.inputFieldsFromSources = inputFieldsFromSources;
    }

    @Override
    public Values executeWindowedAlgorithm(TupleWindow tupleWindow) {
        Map<String, List<Values>> streamValues = new HashMap<>();

        Set<String> streams = inputFieldsFromSources.keySet();
        streams.forEach(stream -> streamValues.put(stream, new ArrayList<>()));


        for (Tuple tuple : tupleWindow.get()) {
            streamValues.get(tuple.getSourceComponent()).add((Values) tuple.getValues());
        }
        return new Values(streamValues, inputFieldsFromSources);
        //TODO how to access the map on the next one
//        Map<String,List<Values>> streamValues = (Map<String, List<Values>>) input.getValues().get(0);
//        for(String stream: streamValues.keySet()){
//            List<Values> vals = streamValues.get(stream);
//            for (Values objects : vals) {
//                ((Tuple)objects.get(0)).getValueByField("word");
//                System.out.println("" + objects.get(0) + " " + objects.get(1));
//            }
//
//        }
    }



    @Override
    public String[] transformFields(String[] incomingFields) {
        return new String[0];
    }
}
