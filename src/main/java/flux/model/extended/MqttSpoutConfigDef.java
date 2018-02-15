package flux.model.extended;

import util.MqttConfig;
import flux.model.BeanDef;

public class MqttSpoutConfigDef extends BeanDef {

    protected String brokerUrl = null;
    protected String clientId = null;
    protected String topic = null;
    protected String regex = null;
    protected String[] fields = null;
    protected String[] streamIds = null;
    protected String[] classes = null;
    protected int qos = 1;

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fieldNames) {
        this.fields = fieldNames;
    }

    public String[] getStreamIds() {
        return streamIds;
    }

    public void setStreamIds(String[] streamIds) {
        this.streamIds = streamIds;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String[] getClasses() {
        return classes;
    }

    public void setClasses(String[] classes) {
        this.classes = classes;
    }

    public MqttConfig createMqttConfig() {
        return new MqttConfig().withBrokerUrl(brokerUrl).withClientId(clientId).withTopic(topic).withRegex(regex).withFieldNames(fields).withClasses(classes);
    }
}
