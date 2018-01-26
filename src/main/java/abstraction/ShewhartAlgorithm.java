package abstraction;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

//TODO this algortihm will emit new fields! how to send them to GenericWindowed Bolt?
public class ShewhartAlgorithm implements IWindowedAlgorithm, Serializable{

    protected ShewhartState previousState;
    //positionInStream on which the shewhard algorithm will be applied
    protected int positionInStream = 0;
    protected String fieldInStream = "";
    protected double kplus = 3;
    protected double kminus = 3;


    //TODO what should time be? A timestamp in the stream?
    protected Map<String, List<String>> inputFieldsFromSources;

    protected boolean emitEntireWindow = false;
    //TODO what about timestamp? if this algorithm emits many values it cannot be combined with a timestamp merger



    public ShewhartAlgorithm() {
        this.previousState = new ShewhartState();
    }

    public ShewhartAlgorithm(double initialMean, double initialVariance) {
        this.previousState = new ShewhartState(initialMean, initialVariance);
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
        Values values = new Values();
        int outcome = 0;
        int n = tupleWindow.getNew().size();
        Tuple currentTuple=null;
        List<Tuple> newTuples= tupleWindow.getNew();
        List<Tuple> allTuples= tupleWindow.get();

        for (Tuple input : tupleWindow.get()) {
            currentTuple = input;
            double value = (double) (positionInStream == -1 ? (double) input.getValueByField(this.fieldInStream) : input.getDouble(positionInStream));
            double curr_mean = previousState.getMean()+ ((1.0 / n) * (value - previousState.getMean()));
            double curr_Variance = Math.sqrt(
                    (1.0 / n) * (
                            ((n - 1.0) * Math.pow(previousState.getVariance(), 2))
                                    + ((value - previousState.getMean()) * (value - curr_mean))
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
    public String[] transformFields(String[] incomingFields) {
        //todo
        return null;
    }
    @Override
    public void setInputSources(Map<String, List<String>> inputFieldsFromSources) {
        this.inputFieldsFromSources = inputFieldsFromSources;
    }

    @Override
    public void prepare() {

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



}
