//package tuple.abstraction;
//
//import abstraction.ShewhartSingleValue;
//import exceptions.AlgorithmDeclarationException;
//import org.apache.storm.tuple.Tuple;
//import org.apache.storm.tuple.Values;
//
//import java.util.List;
//import java.util.Map;
//
//public class FShewhart extends ShewhartSingleValue {
//    private String streamId;
//
//    public FShewhart() {
//        super();
//    }
//
//    public FShewhart(double initialMean, double initialVariance) {
//        super(initialMean, initialVariance);
//    }
//
//    @Override
//    public void setInputSources(Map<String, List<String>> inputFieldsFromSources) {
//        super.setInputSources(inputFieldsFromSources);
//        if (this.inputFieldsFromSources.keySet().size() > 1 || this.inputFieldsFromSources.keySet().size() == 0) {
//            try {
//                throw new AlgorithmDeclarationException("Shewhart algorithm has input from many stream sources, unable to continue");
//            } catch (AlgorithmDeclarationException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    @Override
//    public Values executeAlgorithm(Tuple tuple) {
//        FusionTuple ftuple = (FusionTuple) tuple.getValue(0);
////        LOG.info("incoming ftuple : " + ftuple.toString());
//        streamId = this.inputFieldsFromSources.keySet().stream().findFirst().orElse(null);
//        if (!fieldInStream.isEmpty() && positionInStream == -1) {
//            positionInStream = ftuple.getPositionOfFieldInStream(streamId, fieldInStream);
//        }
//
//        List<Values> incomingManyOrOne = ftuple.getStreamValues(streamId);
////        List<Double> valuesforShewhart = new ArrayList<>();
//
////        incomingManyOrOne.forEach(valueTuple -> valuesforShewhart.add((Double) valueTuple.get(positionInStream)));
//         if (incomingManyOrOne != null) {
//            incomingManyOrOne.forEach(valuesTuple -> {
//                //we may have many values
//                double value = (double) valuesTuple.get(positionInStream);
//                double curr_mean = previousState.getMean() + ((1.0 / n) * (value - previousState.getMean()));
//                double curr_Variance = Math.sqrt(
//                        (1.0 / n) * (
//                                ((n - 1.0) * Math.pow(previousState.getVariance(), 2))
//                                        + ((value - previousState.getMean()) * (value - curr_mean))
//                        )
//                );
//                double UCL = curr_mean + kplus * curr_Variance;
//                double LCL = curr_mean - kminus * curr_Variance;
//                //return values according to the outcome of the algorithm
//                if (value > UCL)
//                    valuesTuple.add(1);
//                else if (value < LCL)
//                    valuesTuple.add(-1);
//                else
//                    valuesTuple.add(0);
//
//                n++;
//                if (n == windowSize + 2)
//                    n = 2; //reset the window
//                this.previousState.nextState(curr_mean, curr_Variance);
//            });
//            ftuple.addMetadataToStream(streamId,new Meta("shewhart", -1, "java.lang.Integer"));
//        }
////        LOG.info("Outgoing ftuple : " + ftuple.toString());
//        return new Values(ftuple);
//    }
//
//
//    @Override
//    public String[] transformFields(String[] incomingFields) {
//        return new String[]{"fusionTuple"};
//    }
//}
