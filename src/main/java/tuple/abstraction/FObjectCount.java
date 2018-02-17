package tuple.abstraction;

import abstraction.ObjectCount;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FObjectCount extends ObjectCount {
    protected Map<String,Integer> streamCounts;
    protected String streamId;
    private Kryo serializer;

    public FObjectCount() {
        super();
    }

    @Override
    public void prepare() {
        super.prepare();
        streamCounts = new HashMap<>();

            serializer = new Kryo();

    }



    @Override
    public Values executeAlgorithm(Tuple tuple) {
        Input input = new Input(tuple.getBinary(0));
        FusionTuple ftuple = (FusionTuple)serializer.readClassAndObject(input);
//        streamId = this.inputFieldsFromSources.keySet().stream().findFirst().orElse("default");
        int pos = 0;
        for(String streamId : ftuple.valueMap.keySet()){
            int count = ftuple.valueMap.get(streamId).size();
            streamCounts.put(streamId,count);
            ftuple.addValuestoStream(streamId,new ArrayList<>(count));
            List<Meta> metaList = new ArrayList<>();
            metaList.add(new Meta("count",0, "java.lang.Integer"));
            ftuple.setStreamMetadata(streamId,metaList);
        }


        return new Values(serializeObject(ftuple));
    }

    private byte[] serializeObject(FusionTuple ftuple) {
        Output output = new Output(new ByteBufferOutput());
        serializer.writeClassAndObject(output,ftuple);
        return output.getBuffer();
    }


    @Override
    public String[] transformFields(String[] incomingFields) {
        return new String[]{"fusionTuple"};
    }

}
