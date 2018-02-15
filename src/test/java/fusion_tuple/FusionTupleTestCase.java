//package fusion_tuple;
//
//import org.apache.storm.tuple.Tuple;
//import org.junit.Before;
//import org.junit.Test;
//import tuple.abstraction.*;
//import org.apache.storm.tuple.Values;
//
//import java.util.*;
//
//import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class FusionTupleTestCase {
//    private FusionTuple fTuple;
//
//    @Before
//    public void createFusionTuple() {
//        this.fTuple = new FusionTuple();
//
//        String streamId = "temperature";
//        List<Values> valuesList = new ArrayList<>();
//        Values vals = new Values("sensor-1", 25.0, 213712823L);
//        valuesList.add(vals);
//        fTuple.addValuestoStream(streamId, valuesList);
//        vals = new Values("sensor-1", 35.0, 213712123L);
//        valuesList.add(vals);
//        fTuple.addValuestoStream(streamId, valuesList);
//        List<Meta> pair = new ArrayList<>();
//        pair.add(new Meta("id", 0,"java.lang.String"));
//        pair.add(new Meta("value", 1,"java.lang.Double"));
//        pair.add(new Meta("timestamp", 2,"java.lang.Long"));
//        fTuple.setStreamMetadata(streamId, pair);
//
//        streamId = "humidity";
//        valuesList = new ArrayList<>();
//        pair = new ArrayList<>();
//        pair.add(new Meta("id", 0,"java.lang.String"));
//        pair.add(new Meta("value", 1,"java.lang.Double"));
//        pair.add(new Meta("timestamp", 2,"java.lang.Long"));
//        vals = new Values("sensor-1", 25.0, 213712823L, new ArrayList<>());
//        valuesList.add(vals);
//        fTuple.addValuestoStream(streamId, valuesList);
////        pair.add(new Meta("list", "java.util.ArrayList"));
//        fTuple.setStreamMetadata(streamId, pair);
//
//
//        System.out.println("dadwad");
//    }
//
//
//
//    @Test
//    public void simulateFShewhart(){
//        Tuple tuple = mock(Tuple.class, RETURNS_DEEP_STUBS);
//        when(tuple.getValue(0)).thenReturn(this.fTuple);
//
//        FShewhart shewhartSingleValue  = (FShewhart) new FShewhart().withFieldInStream("value");
//        Map<String,List<String>> inputSources = new HashMap<>();
//        inputSources.put("temperature", new ArrayList<>(Arrays.asList(new String[]{"id", "value", "timestamp"})));
//
//        shewhartSingleValue.setInputSources(inputSources);
//
//        shewhartSingleValue.executeAlgorithm(tuple);
//
//        System.out.println("dawdwa");
//
//    }
//
//
//    @Test
//    public void simulateFCusum(){
//        Tuple tuple = mock(Tuple.class, RETURNS_DEEP_STUBS);
//        when(tuple.getValue(0)).thenReturn(this.fTuple);
//
//        Fcusum cusum  = (Fcusum) new Fcusum(15.0, 5.0, 25).withFieldInStream("value");
//        Map<String,List<String>> inputSources = new HashMap<>();
//        inputSources.put("temperature", new ArrayList<>(Arrays.asList(new String[]{"id", "value", "timestamp"})));
//
//        cusum.setInputSources(inputSources);
//
//        cusum.executeAlgorithm(tuple);
//
//        System.out.println("dawdwa");
//
//    }
//
//    @Test
//    public void simulateFFieldFilter(){
//        Tuple tuple = mock(Tuple.class, RETURNS_DEEP_STUBS);
//        when(tuple.getValue(0)).thenReturn(this.fTuple);
//
//        FFieldFilter ffilter = new FFieldFilter("id","timestamp");
//
//        Map<String,List<String>> inputSources = new HashMap<>();
//        inputSources.put("temperature", new ArrayList<>(Arrays.asList(new String[]{"fusionTuple"})));
//
//        ffilter.setInputSources(inputSources);
//
//        ffilter.executeAlgorithm(tuple);
//
//        System.out.println("dawdwa");
//    }
//
//}
