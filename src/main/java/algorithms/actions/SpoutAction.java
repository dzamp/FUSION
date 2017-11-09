package algorithms.actions;

import algorithms.exceptions.FieldsMismatchException;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Values;

public interface SpoutAction extends Action {
    public void execute(SpoutOutputCollector collector, String streamId,String messageValue) throws FieldsMismatchException;

}
