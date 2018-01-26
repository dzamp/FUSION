package tuple.abstraction;

import consumers.MqttConsumerSpout;
import flux.model.extended.MqttSpoutConfigDef;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FMqttConsumer extends MqttConsumerSpout {
    long t1;
    private List<Long> timeDeltas;


    public FMqttConsumer(MqttSpoutConfigDef def) {
        super(def);
    }


    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        super.open(conf, context, collector);
        timeDeltas = new ArrayList<>();
    }

    @Override
    public void nextTuple() {
        t1 = System.currentTimeMillis();
        super.nextTuple();
    }

    @Override
    public void emit(Values values) {
        if (values != null && values.size() > 0) {
            this.outcomingStreamsFieldsMap.forEach((stream, stringFields) -> {
                //no need to use emit(values), since they it maps to this emit with stream-id = "default"
                List<Meta> metadataList = new ArrayList<>();
                int i = 0;
                for (String field : this.config.getFieldNames()) {
                    metadataList.add(new Meta(field, i, config.getMapper().getClassname(i)));
                    i++;
                }

                FusionTuple ftuple = new FusionTuple();
                ftuple.setStreamMetadata(stream, metadataList);
                List<Values> vals = new ArrayList<>();
                vals.add(values);
                ftuple.addValuestoStream(stream, vals);
                collector.emit(stream, new Values(ftuple));
            });
        }
        timeDeltas.add(System.currentTimeMillis() - t1);
        if(timeDeltas.size() % 1000 == 0) {
            long total = timeDeltas.stream().mapToLong(Long::intValue).sum();
            System.out.println("Reporting average time for " + timeDeltas.size() + " each emitted tuple = " + ((double)total/(double)timeDeltas.size()));
        }
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        setOutboundStreams();
        if (this.outcomingStreamsFieldsMap != null) {
            this.outcomingStreamsFieldsMap.forEach(
                    (stream, fieldStrings) -> declarer.declareStream(stream, new Fields("fusionTuple")));
        }
    }

    @Override
    public void close() {
        long total = timeDeltas.stream().mapToLong(Long::intValue).sum();
        System.out.println("Reporting average time for " + timeDeltas.size() + " each emitted tuple = " + ((double)total/(double)timeDeltas.size()));
    }
}
