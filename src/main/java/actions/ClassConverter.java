package actions;

import java.io.Serializable;

@FunctionalInterface
public interface ClassConverter<T> extends Serializable{
    public T convertToObject(String value);

}

