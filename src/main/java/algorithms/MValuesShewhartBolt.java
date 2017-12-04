package algorithms;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Time;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MValuesShewhartBolt extends GenericBolt {
    //TODO works for doubles only?
    private ShewHartState previousState;
    //position on which the shewhard algorithm will be applied
    private int positionInStream = 0;
    private double kplus = 3;
    private double kminus = 3;

    //TODO what should time be? A timestamp in the stream?
    private int maxWindow = 200;
    private int n = 2;
    PrintWriter writer ;

    //TODO export this in config method??
    public MValuesShewhartBolt(int positionInStream, double kplus, double kminus, double initialMean, double initialVariance, int maxWindow) {
        previousState = new ShewHartState(initialMean, initialVariance);
        this.kplus = kplus;
        this.positionInStream = positionInStream;
        this.kminus = kminus;
        this.maxWindow = this.maxWindow;
    }

    //aternative constructor for set values of previousState
    public MValuesShewhartBolt(int positionInStream, double kplus, double kminus, int maxWindow) {
        previousState = new ShewHartState();
        this.positionInStream = positionInStream;
        this.kminus = kminus;
        this.kplus = kplus;
        this.maxWindow = maxWindow;
    }


    @Override
    public void execute(Tuple input) {
        double value = input.getDouble(positionInStream);
        double curr_mean = previousState.mean + ((1.0 / n) * (value - previousState.mean));
        double curr_Variance = Math.sqrt(
                 (1.0 / n) * (
                        ( (n - 1.0) * Math.pow(previousState.variance, 2))
                                + ((value - previousState.mean) * (value - curr_mean))
                )
        );

        double UCL = curr_mean + kplus * curr_Variance;
        double LCL = curr_mean - kminus * curr_Variance;

        if (value > UCL || value < LCL) {
            emit(new Values(input, 1));
            writer.print(">>>");
        } else emit(new Values(input, 0));


        writer.println("Current value: " + value + " mean: " + curr_mean + " variance: "
                + curr_Variance + " UCL: " + UCL + " LCL: " + LCL );
        n++;
        if (n == maxWindow)
            n = 2; //reset the window
        this.previousState.nextState(curr_mean, curr_Variance);
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        super.prepare(map, topologyContext, outputCollector);
        try {
            writer = new PrintWriter("shewhartTest" + Time.currentTimeMillis(), "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

        if (this.actions.size() == 1) {
            if (this.actions.get(0).getStreamId() == null) {
                List<String> fieldsList = Arrays.asList(this.actions.get(0).getEmittedFields());
                fieldsList.add("shewhart");
                String[] fields = fieldsList.toArray(new String[fieldsList.size()]);
                declarer.declare(new Fields(fields));
            }
        } else {
            actions.forEach(boltEmitter -> {
                List<String> fieldsList = Arrays.asList(boltEmitter.getEmittedFields());
                fieldsList.add("shewhart");
                String[] fields = fieldsList.toArray(new String[fieldsList.size()]);
                declarer.declareStream(boltEmitter.getStreamId(), new Fields(boltEmitter.getEmittedFields()));
            });
        }
    }

    private class ShewHartState {
        //TODO could those be NUmbers?
        private double mean;
        private double variance;


        public ShewHartState() {
            this.mean = 10.0;
            this.variance = 10.0;
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
