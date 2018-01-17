package consumers;


import flux.model.extended.KafkaSpoutConfigDef;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;

public class FusionKafkaSpout extends KafkaSpout {

    public FusionKafkaSpout(KafkaSpoutConfigDef def){
        super(def.createSpoutConfig());
    }

    private FusionKafkaSpout(SpoutConfig spoutConf) {
        super(spoutConf);
    }

    public FusionKafkaSpout(){
        super(null);
    }


}
