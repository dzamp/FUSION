package abstraction;


import java.io.Serializable;

public class ShewhartState implements Serializable {
    //TODO could those be NUmbers?
    private double mean;
    private double variance;


    public ShewhartState() {
        this.mean = 0.0;
        this.variance = 0.0;
    }

    public ShewhartState(double mean, double variance) {
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
