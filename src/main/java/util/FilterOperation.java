package util;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Comparator;

public interface FilterOperation {

    public  void apply(Comparator cmp, Tuple input, Number threshold, Values filteredValues, Values rejectedValues);

}
