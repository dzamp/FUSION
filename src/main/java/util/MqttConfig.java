package util;

import scala.actors.threadpool.Arrays;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MqttConfig implements Serializable {

    protected String brokerUrl = null;
    protected String clientId = null;
    protected String topic = null;
    protected String[] fieldNames = null;
    protected List<String> streamIds = null;
    protected int qos = 1;
    protected OutputFieldsClassMapper mapper;

    public MqttConfig() {
        mapper = new OutputFieldsClassMapper();
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public MqttConfig withBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public MqttConfig withClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public MqttConfig withTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String[] getFieldNames() {
        return fieldNames;
    }

    public MqttConfig withFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
        return this;
    }

    public List<String> getStreamIds() {
        return streamIds;
    }

    public MqttConfig withStreamIds(String[] streamIds) {
        this.streamIds = Arrays.asList(streamIds);
        return this;
    }

    public int getQos() {
        return qos;
    }

    public MqttConfig setQos(int qos) {
        this.qos = qos;
        return this;
    }

    public MqttConfig withRegex(String regex) {
        this.mapper.setRegex(regex);
        return this;
    }

    public MqttConfig withClasses(String[] classes) {
        this.mapper.withClasses(classes);
        return this;
    }

    public void addStreamName(String streamId) {
        if (this.streamIds == null) this.streamIds = new ArrayList<>();
        if (!this.streamIds.contains(streamId))
            this.streamIds.add(streamId);
    }


    public OutputFieldsClassMapper getMapper() {
        return mapper;
    }
}
