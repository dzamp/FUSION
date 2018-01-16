package consumers;

import org.apache.storm.topology.IRichSpout;

public interface FusionIRichSpout extends IRichSpout {
    public void setFields(String ...fieldNames);
//    public void setFields(String ...fieldNames);
    public void addOutgoingStreamName(String streamName);
}
