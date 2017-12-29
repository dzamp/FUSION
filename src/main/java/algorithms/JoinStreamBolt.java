package algorithms;

import org.apache.storm.bolt.JoinBolt;

/**
 * Created by jim on 26/12/2017.
 */
public class JoinStreamBolt extends JoinBolt {
    public JoinStreamBolt(String sourceId, String fieldName) {
        super(sourceId, fieldName);
    }

    public JoinStreamBolt(Selector type, String srcOrStreamId, String fieldName) {
        super(type, srcOrStreamId, fieldName);
    }
}
