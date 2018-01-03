package abstraction;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

import java.util.List;
import java.util.Map;


public class ShewhartAlgorithm implements IWindowedAlgorithm {

    protected ShewHartState previousState;
    //position on which the shewhard algorithm will be applied
    protected int positionInStream = 0;
    protected String fieldInStream = "";
    protected double kplus = 3;
    protected double kminus = 3;


    //TODO what should time be? A timestamp in the stream?
    protected Map<String, Map<String, List<String>>> inputFieldsFromSources;

    protected boolean emitEntireWindow = false;
    //TODO what about timestamp? if this algorithm emits many values it cannot be combined with a timestamp merger


    public ShewhartAlgorithm() {
        this.previousState = new ShewHartState();
    }

    public ShewhartAlgorithm(double initialMean, double initialVariance) {
        this.previousState = new ShewHartState(initialMean, initialVariance);
    }



    public ShewhartAlgorithm withPositionInStream(int position) {
        this.positionInStream = position;
        this.fieldInStream = "";
        return this;
    }

    public ShewhartAlgorithm withFieldInStream(String fieldName) {
        this.fieldInStream = fieldName;
        this.positionInStream = -1;
        return this;
    }


    public ShewhartAlgorithm emitEntireWindow(boolean value){
        this.emitEntireWindow = value;
        return this;
    }

    @Override
    public Values executeWindowedAlgorithm(TupleWindow tupleWindow) {
        int outcome = 0;
        int n = tupleWindow.getNew().size();
        Tuple currentTuple=null;
        for (Tuple input : tupleWindow.getNew()) {
            currentTuple = input;
            double value = (double) (positionInStream == -1 ? (double) input.getValueByField(this.fieldInStream) : input.getDouble(positionInStream));
            double curr_mean = previousState.mean + ((1.0 / n) * (value - previousState.mean));
            double curr_Variance = Math.sqrt(
                    (1.0 / n) * (
                            ((n - 1.0) * Math.pow(previousState.variance, 2))
                                    + ((value - previousState.mean) * (value - curr_mean))
                    )
            );

            double UCL = curr_mean + kplus * curr_Variance;
            double LCL = curr_mean - kminus * curr_Variance;
            this.previousState.nextState(curr_mean, curr_Variance);
            //TODO should I return which boundary has been breached? UCL or LCL
            if (value > UCL || value < LCL) {
                outcome = 1;
                //TODO here we need to send all values
                //insert break?
                break;
            } else outcome = 0;
        }
        if (emitEntireWindow)
            return new Values(tupleWindow.get(), outcome);
        else return new Values(currentTuple,outcome);
    }


    @Override
    public void setInputSources(Map<String, Map<String, List<String>>> inputFieldsFromSources) {
        this.inputFieldsFromSources = inputFieldsFromSources;
    }


    public ShewhartAlgorithm withKminus(int kminus) {
        this.kminus = kminus;
        return this;
    }

    public ShewhartAlgorithm withKplus(int kplus) {
        this.kplus = kplus;
        return this;
    }


    public ShewhartAlgorithm build() {
        //TODO any error checking here?
        return this;
    }

    protected class ShewHartState {
        //TODO could those be NUmbers?
        private double mean;
        private double variance;


        public ShewHartState() {
            this.mean = 0.0;
            this.variance = 0.0;
        }

        public ShewHartState(double mean, double variance) {
            this.mean = mean;
            this.variance = variance;
        }

        public double getMean() {
            return mean;
        }

        public void setMean(double mean) {
            this.mean = mean;
        }

        public double getVariance() {
            return variance;
        }

        public void setVariance(double variance) {
            this.variance = variance;
        }


        public void nextState(double newMean, double newVariance) {
            this.mean = newMean;
            this.variance = newVariance;
        }
    }


}
