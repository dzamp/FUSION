package algorithms.consumers;

public class KafkaSpout extends ConsumerSpout {


    public KafkaSpout(String brokerUrl, String clientId, String topic, String regex, Class... args) {
        super(brokerUrl, clientId, topic, regex, args);
    }

    public KafkaSpout(String brokerUrl, String clientId, String topic) {
        super(brokerUrl, clientId, topic);
    }

    @Override
    public void close() {

    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void nextTuple() {
        
    }
}
