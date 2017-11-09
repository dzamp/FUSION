package algorithms.consumers;

import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;

import java.util.Map;

public class KafkaSpout extends org.apache.storm.kafka.KafkaSpout {


    public KafkaSpout(SpoutConfig spoutConf) {
//        spoutConf = new SpoutConfig();
        super(spoutConf);
    }
}
