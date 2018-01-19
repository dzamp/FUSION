package abstraction;

import org.apache.storm.topology.IRichBolt;

public interface FusionBolt {
    public void setFields(boolean terminalNode,String ...fieldNames);
    public void addOutgoingStreamName(String streamName);
    public String[] getOutgoingFields();
}
