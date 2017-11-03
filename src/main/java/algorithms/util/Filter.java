package algorithms.util;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Comparator;

public interface Filter {

    public  void apply(Comparator cmp, Tuple input, Number threshold, Values filteredVaues, Values rejectedValues);

}
