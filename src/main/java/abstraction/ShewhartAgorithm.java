package abstraction;

import algorithms.MValuesShewhartBolt;
import exceptions.AlgorithmDeclarationException;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;


public class ShewhartAgorithm implements IWindowedAlgorithm {

    protected ShewHartState previousState;
    //position on which the shewhard algorithm will be applied
    protected int positionInStream = 0;
    protected String fieldInStream = "";
    protected double kplus = 3;
    protected double kminus = 3;


    //TODO what should time be? A timestamp in the stream?
    protected int maxWindow = 200;
    protected BaseWindowedBolt.Duration duration;




    public ShewhartAgorithm() {
        this.previousState = new ShewHartState();
    }

    public ShewhartAgorithm(double initialMean, double initialVariance ) {
        this.previousState = new ShewHartState(initialMean,initialVariance);
    }


    public ShewhartAgorithm withWindowCount(int count) {
        this.maxWindow = count;
        this.duration = null; //no duration
        return this;
    }

    public ShewhartAgorithm withPositionInStream(int position) {
        this.positionInStream = position;
        this.fieldInStream = "";
        return this;
    }

    public ShewhartAgorithm withFieldInStream(String fieldName) {
        this.fieldInStream = fieldName;
        this.positionInStream = -1;
        return this;
    }

    public ShewhartAgorithm withWindowSecDuration(int seconds) {
        this.duration = BaseWindowedBolt.Duration.seconds(seconds);
        this.maxWindow = -1; //means this window doesnt care about count, only duration
        return this;
    }


    public ShewhartAgorithm withWindowHoursDuration(int hours) {
        this.duration = BaseWindowedBolt.Duration.hours(hours);
        this.maxWindow = -1; //means this window doesnt care about count, only duration
        return this;
    }

    @Override
    public Values executeWindowedAlgorithm(TupleWindow tupleWindow) {
        int outcome = 0;
        int n = tupleWindow.get().size();
        for (Tuple input : tupleWindow.getNew()) {
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
            } else outcome = 0;
        }
        return new Values(tupleWindow.get(),outcome);

    }

    @Override
    public int getWindowCount() {
        return this.maxWindow;
    }

    @Override
    public BaseWindowedBolt.Duration getWindowDuration() {
        return this.duration;
    }

    public ShewhartAgorithm withKminus(int kminus) {
        this.kminus = kminus;
        return this;
    }

    public ShewhartAgorithm withKplus(int kplus) {
        this.kplus = kplus;
        return this;
    }


    public ShewhartAgorithm build() {
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
