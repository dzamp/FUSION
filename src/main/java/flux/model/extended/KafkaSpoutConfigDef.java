package flux.model.extended;

import consumers.FusionScheme;
import flux.model.BeanDef;
import org.apache.storm.kafka.KeyValueSchemeAsMultiScheme;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;

public class KafkaSpoutConfigDef extends BeanDef{

    public String[] classes = null;
    public String regex = null;
    public String[] fields = null;
    public String topic = null;
    public String zkRoot = null;
    public FusionScheme fusionScheme = null;
    public String zkHostString =null;
    public String clientId = null;
    //an example of fetch size and buffer size
    public int bufferSizeBytes = 4194304;
    public int fetchSizeBytes = 4194304;



    public SpoutConfig createSpoutConfig() {
        SpoutConfig config = new SpoutConfig(createZkHosts(),topic,zkRoot,clientId);
        config.bufferSizeBytes = this.bufferSizeBytes;
        config.fetchSizeBytes = this.fetchSizeBytes;
        config.scheme = getTopLevelScheme();
        return config;
    }

    public String[] getFields() {
        return fields;
    }
    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public void setZkHosts(String url){
        this.zkHostString = url;
    }

    public String getZkHosts(){
        return this.zkHostString;
    }

    public FusionScheme createFusionScheme() {
        this.fusionScheme = new FusionScheme().withClasses(classes).withFields(fields).withRegex(regex);
        return this.fusionScheme;
    }

    public KeyValueSchemeAsMultiScheme getTopLevelScheme(){
        return new KeyValueSchemeAsMultiScheme(createFusionScheme());
    }

    public ZkHosts createZkHosts(){
        return new ZkHosts(zkHostString);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getZkRoot() {
        return zkRoot;
    }

    public void setZkRoot(String zkRoot) {
        this.zkRoot = zkRoot;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getBufferSizeBytes() {
        return bufferSizeBytes;
    }

    public void setBufferSizeBytes(int bufferSizeBytes) {
        this.bufferSizeBytes = bufferSizeBytes;
    }

    public int getFetchSizeBytes() {
        return fetchSizeBytes;
    }

    public void setFetchSizeBytes(int fetchSizeBytes) {
        this.fetchSizeBytes = fetchSizeBytes;
    }




}
