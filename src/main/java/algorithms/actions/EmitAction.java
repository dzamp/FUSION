package algorithms.actions;

import algorithms.exceptions.FieldsMismatchException;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Values;

public interface EmitAction {
    public String getStreamId();
    public String[] getEmittedFields();
    public void execute(OutputCollector collector, String streamId, Values values) throws FieldsMismatchException;

}
