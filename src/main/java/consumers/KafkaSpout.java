package consumers;

import org.apache.storm.kafka.SpoutConfig;

public class KafkaSpout extends org.apache.storm.kafka.KafkaSpout {


    public KafkaSpout(SpoutConfig spoutConf) {
//        spoutConf = new SpoutConfig();
        super(spoutConf);
    }
}
