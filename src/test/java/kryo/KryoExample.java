package kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.storm.tuple.Values;
import org.junit.Before;
import org.junit.Test;
import tuple.abstraction.FusionTuple;
import tuple.abstraction.Meta;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotEquals;

/**
 * Created by jim on 27/1/2018.
 */
public class KryoExample {
    Kryo kryoSerialzer;


    @Before
    public void init() {
        this.kryoSerialzer = new Kryo();

    }

    @Test
    public void testSerialization() {
        Output output = new Output(new ByteBufferOutput());
        FusionTuple tuple = new FusionTuple();
        List<Values> valuesList = new ArrayList<>();
        valuesList.add(new Values("dimitris", 3123, 1234545L));
        tuple.addValuestoStream("stream1", valuesList);
        List<Meta> metaList = new ArrayList<>();
        metaList.add(new Meta("id",0,"java.lang.String"));
        metaList.add(new Meta("value",1,"java.lang.Integer"));
        metaList.add(new Meta("timestamp",2,"java.lang.Long"));
        tuple.setStreamMetadata("stream1", metaList);
        kryoSerialzer.writeClassAndObject(output, tuple);


        //test deserialization
        Input input = new Input(output.getBuffer());
        Object adw = kryoSerialzer.readClassAndObject(input);
        FusionTuple deserializedTuple = (FusionTuple) adw;
        deserializedTuple.getStreamValues("stream1").get(0).add("CHANGE");
        assertNotEquals(deserializedTuple.getStreamValues("stream1").get(0).size(),tuple.getStreamValues("stream1").get(0).size());

        //test changing the deserialized object does not change the old object
    }


}
