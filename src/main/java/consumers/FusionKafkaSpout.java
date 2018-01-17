package consumers;


import flux.model.extended.KafkaSpoutConfigDef;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.tuple.Fields;

public class FusionKafkaSpout extends KafkaSpout {
    public String[] outgoingFields = null;
    public SpoutConfig config = null;

    public FusionKafkaSpout(KafkaSpoutConfigDef def) {
        super(def.createSpoutConfig());
        this.config = def.createSpoutConfig();
        this.outgoingFields = def.getFields();
    }

    private FusionKafkaSpout(SpoutConfig spoutConf) {
        super(spoutConf);
    }

    public FusionKafkaSpout() {
        super(null);
    }

    public String[] getOutgoingFields() {
        Fields fields = this.config.scheme.getOutputFields();
        return fields.toList().toArray(new String[fields.size()]);
    }

}
