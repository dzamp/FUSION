package tuple.abstraction;

import abstraction.FieldFilter;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.List;
import java.util.Map;

public class FFieldFilter extends FieldFilter {

    private Kryo serializer;

    public FFieldFilter(String... fieldsTobeRemoved) {
        super(fieldsTobeRemoved);
    }

    @Override
    public void setInputSources(Map<String, List<String>> inputFieldsFromSources) {
        super.setInputSources(inputFieldsFromSources);
    }

    @Override
    public void prepare() {
        super.prepare();
        serializer = new Kryo();
    }

    @Override
    public String[] transformFields(String[] incomingFields) {
        return new String[]{"fusionTuple"};
    }

    @Override
    public Values executeAlgorithm(Tuple tuple) {
        Input input = new Input(tuple.getBinary(0));
        FusionTuple ftuple = (FusionTuple) serializer.readClassAndObject(input);
        for (String streamId : this.inputFieldsFromSources.keySet()) {
//            List<Values> incomingManyOrOne = ftuple.getStreamValues(streamId);
            for (String fieldName : fieldsTobeRemoved) {
                ftuple.removeFieldAndMetadataFromStream(streamId, fieldName);
            }

        }

        return new Values(serializeObject(ftuple));
    }

    private byte[] serializeObject(FusionTuple ftuple) {
        Output output = new Output(new ByteBufferOutput());
        serializer.writeClassAndObject(output, ftuple);
        return output.getBuffer();
    }

}
