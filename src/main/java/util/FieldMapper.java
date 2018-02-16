package util;

import org.apache.storm.tuple.Values;

public interface FieldMapper {
    public Values mapToValues(String message, String regex, Class[] args);
}
