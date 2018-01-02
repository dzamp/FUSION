package abstraction;

import actions.BoltEmitter;
import exceptions.FieldsMismatchException;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericWindowedBolt extends BaseWindowedBolt {

    protected TopologyContext topologyContext;
    protected OutputCollector collector;
    protected Map configMap;
    protected List<BoltEmitter> actions;
    protected IWindowedAlgorithm algorithm;
    protected OutputFieldsDeclarer declarer;

    //    public GenericWindowedBolt() {
//        super();
//    }

    public GenericWindowedBolt withAlgorithm(IWindowedAlgorithm algo) {
        this.algorithm = algo;
        if (!this.algorithm.getTimestampField().isEmpty())  super.withTimestampField(this.algorithm.getTimestampField());
        if (this.algorithm.getWindowCount() > 0) super.withWindow(Count.of(this.algorithm.getWindowCount()));
        if (this.algorithm.getWindowDuration() != null) super.withWindow(this.algorithm.getWindowDuration());

        return this;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);
        this.topologyContext = topologyContext;
        this.collector = collector;
        this.configMap = stormConf;
        this.actions = new ArrayList<>();
    }


    public TupleWindow beforeAlgorithmExecution(TupleWindow tupleWindow) {
        return tupleWindow;
    }

    public Values afterAlgorithmExecution(Values values) {
        return values;
    }

    @Override
    public void execute(TupleWindow inputWindow) {
        beforeAlgorithmExecution(inputWindow);
        Values values = algorithm.executeWindowedAlgorithm(inputWindow);
        afterAlgorithmExecution(values);
        emit(values);
    }


    @Override
    public void cleanup() {
        //nothing here
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        this.declarer = declarer;
        if (this.actions.size() == 1) {
            if (this.actions.get(0).getStreamId() == null)
                this.declarer.declare(new Fields(this.actions.get(0).getEmittedFields()));
        } else {
            actions.forEach(boltEmitter -> this.declarer.declareStream(boltEmitter.getStreamId(), new Fields(boltEmitter.getEmittedFields())));
        }
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return super.getComponentConfiguration();
    }


    public void addAction(BoltEmitter boltEmitter) {
        actions.add(boltEmitter);
    }

    public void emit(Values values) {
        if (values != null) {
            this.actions.forEach(boltEmitter -> {
                try {
                    boltEmitter.execute(this.collector, boltEmitter.getStreamId(), values);
                } catch (FieldsMismatchException e) {
                    e.printStackTrace();
                }
            });
        }
    }


}
