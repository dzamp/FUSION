package algorithms;

import abstraction.IAlgorithm;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.eclipse.recommenders.jayes.BayesNode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleAverageClassifier implements IAlgorithm, Serializable {


    protected Map<String, List<String>> inputFieldsMap;

    @Override
    public Values executeAlgorithm(Tuple tuple) {

        Map<String, List<Values>> streamValues = (Map<String, List<Values>>) tuple.getValues().get(0);
        Map<String, String> evidence = new HashMap<>();
        for (String stream : streamValues.keySet()) {
            //TODO classify the values according to the outcomes!
            //TODO An dn einai o shewhart??? kai einai o QUMSUM? ti ginetai an einai kapoios allos?
            //TODO edw prepei na kanw ena classification ths timhs/timwn pou dexomai se poio outcome anhkoun kai sthn sunexeia na to kanw set ws evidence
            List<Values> values = streamValues.get(stream);
            double sum = 0;
            for (Values val : values) sum += (double) val.get(1);
            double median = sum / values.size();
            String evidenceNodeName;
            if (stream.equals("blood-spout")) {
                evidenceNodeName = "blood_pressure";
                if (median < 7.0 && median > 3) evidence.put(evidenceNodeName, "medium");
                if (median >= 7.0) evidence.put(evidenceNodeName, "high");
                if (median <= 3) evidence.put(evidenceNodeName, "low");
            } else {
                evidenceNodeName = "temperature";
                if (median > 36.8 && median < 38.0) evidence.put(evidenceNodeName, "medium");
                if (median >= 38.0) evidence.put(evidenceNodeName, "high");
                if (median <= 36.8) evidence.put(evidenceNodeName, "low");
            }
            System.out.println("dahdawh");
        }
        return new Values(evidence);
    }

    @Override
    public void setInputSources(Map<String, List<String>> inputFieldsFromSources) {
        this.inputFieldsMap = inputFieldsFromSources;
    }

    @Override
    public void prepare() {

    }

    @Override
    public String[] transformFields(String[] incomingFields) {
        return new String[]{"classification"};
    }
}
