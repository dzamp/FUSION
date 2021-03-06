package tuple.abstraction;

import abstraction.CusumAlgorithm;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import exceptions.AlgorithmDeclarationException;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.List;
import java.util.Map;

public class Fcusum extends CusumAlgorithm {
    String streamId;
    protected  Kryo serializer;
    public Fcusum(double median, double drift, double threshold) {
        super(median, drift, threshold);
    }

    @Override
    public void prepare() {
        super.prepare();
        serializer = new Kryo();
    }

    @Override
    public Values executeAlgorithm(Tuple tuple) {

//        FusionTuple ftuple = (FusionTuple) tuple.getValue(0);
        Input input = new Input(tuple.getBinary(0));
        FusionTuple ftuple = (FusionTuple)serializer.readClassAndObject(input);
//        LOG.info("Incoming ftuple : " + ftuple.toString());
        streamId = this.inputFieldsFromSources.keySet().stream().findFirst().orElse("default");
        if (!fieldInStream.isEmpty() && positionInStream == -1) {
            positionInStream = ftuple.getPositionOfFieldInStream(streamId, fieldInStream);
        }

        List<Values> incomingManyOrOne = ftuple.getStreamValues(streamId);
//        List<Double> valuesforShewhart = new ArrayList<>();

//        incomingManyOrOne.forEach(valueTuple -> valuesforShewhart.add((Double) valueTuple.get(positionInStream)));
        if (incomingManyOrOne != null) {
            incomingManyOrOne.forEach(valuesTuple -> {

                double upperBoundSignal = 0, lowerBoundSignal = 0;
                double currentValue = (double) valuesTuple.get(positionInStream);


                double temp = currentValue - (median + drift) + positiveCusum;
                positiveCusum = Math.max(0, temp);

                temp = currentValue - (median - drift) + negativeCusum;
                negativeCusum = Math.min(0, temp);
                int signal = 12312;
                if (positiveCusum > threshold) {
                    signal = 1000; //upperBoundSignal
                    positiveCusum = 0;
                    negativeCusum = 0;
                    //upper threshold breached here
                    //todo add the new field to the generic bolt fields
//                valuesTuple.add(upperBoundSignal);

                }
                if (negativeCusum < -threshold) {
                    signal = -10000; //lowerBoundSignal = -1;
                    positiveCusum = 0;
                    negativeCusum = 0;
                    //lower threshold breached here
                    //todo add the new field to the generic bolt fields
//                valuesTuple.add(lowerBoundSignal);
                }
                //else add ret as zero
                valuesTuple.add(signal);

            });
//            ftuple.addValuestoStream(streamId,incomingManyOrOne);
            ftuple.addMetadataToStream(streamId,new Meta("cusum", -1, "java.lang.Integer"));
        }
//        LOG.info("Outgoing ftuple : " + ftuple.toString());

        return new Values(serializeObject(ftuple));
    }

    private byte[] serializeObject(FusionTuple ftuple) {
        Output output = new Output(new ByteBufferOutput());
        serializer.writeClassAndObject(output,ftuple);
        return output.getBuffer();
    }

    @Override
    public void setInputSources(Map<String, List<String>> inputFieldsFromSources) {
        super.setInputSources(inputFieldsFromSources);
        if (this.inputFieldsFromSources.keySet().size() > 1 || this.inputFieldsFromSources.keySet().size() == 0) {
            try {
                throw new AlgorithmDeclarationException("Cusum algorithm has input from many stream sources, unable to continue");
            } catch (AlgorithmDeclarationException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public String[] transformFields(String[] incomingFields) {
        return new String[]{"fusionTuple"};
    }
}
