package abstraction;

import org.apache.storm.topology.IRichBolt;

public interface FusionIRichBolt extends IRichBolt{
    public void setFields(boolean terminalNode,String ...fieldNames);
    public void addOutgoingStreamName(String streamName);
    public String[] getOutgoingFields();
    public IAlgorithm getAlgorithm();
}
