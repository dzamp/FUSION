package consumers;

import org.apache.storm.topology.IRichSpout;

public interface FusionIRichSpout extends IRichSpout {
    public String[] getFieldNames();
    public void addOutgoingStreamName(String streamName);
}
