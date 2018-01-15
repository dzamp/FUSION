package abstraction;

import org.apache.storm.topology.IRichBolt;

public interface FusionIRichBolt extends IRichBolt{
    public void setFields(String ...fieldNames);
}
