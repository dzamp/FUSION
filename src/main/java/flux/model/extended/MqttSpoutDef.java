package flux.model.extended;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import flux.model.SpoutDef;

@JacksonXmlRootElement(localName = "mqtt-spout")
public class MqttSpoutDef extends SpoutDef {
    public String[] fields = null;

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }
}
