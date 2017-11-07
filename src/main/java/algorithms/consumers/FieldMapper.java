package algorithms.consumers;

import org.apache.storm.tuple.Values;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface FieldMapper {
    public Values mapToValues(String message, String topic,String regex, Class ...args);
}
