//package tuple.abstraction;
//
//import abstraction.ObjectCount;
//import org.apache.storm.tuple.Tuple;
//import org.apache.storm.tuple.Values;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class FObjectCount extends ObjectCount {
//    protected Map<String,Integer> streamCounts;
//    protected String streamId;
//
//    public FObjectCount() {
//        super();
//    }
//
//    @Override
//    public void prepare() {
//        super.prepare();
//        streamCounts = new HashMap<>();
//    }
//
//    @Override
//    public Values executeAlgorithm(Tuple tuple) {
//        FusionTuple ftuple = (FusionTuple) tuple.getValue(0);
////        streamId = this.inputFieldsFromSources.keySet().stream().findFirst().orElse("default");
//        int pos = 0;
//        for(String streamId : ftuple.valueMap.keySet()){
//            int count = ftuple.valueMap.get(streamId).size();
//            streamCounts.put(streamId,count);
//            ftuple.addValuestoStream(streamId,new ArrayList<>(count));
//            List<Meta> metaList = new ArrayList<>();
//            metaList.add(new Meta("count",0, "java.lang.Integer"));
//            ftuple.setStreamMetadata(streamId,metaList);
//        }
//
//        return new Values(ftuple);
//    }
//
//    @Override
//    public String[] transformFields(String[] incomingFields) {
//        return new String[]{"fusionTuple"};
//    }
//
//}
