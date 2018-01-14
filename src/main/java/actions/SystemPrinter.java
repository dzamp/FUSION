package actions;

import exceptions.FieldsMismatchException;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Values;

import java.io.Serializable;

/**
 * Created by jim on 4/11/2017.
 */
public class SystemPrinter implements BoltAction, Serializable {
    private String streamId= null;
    private String[] emitedFields;

    public SystemPrinter(String streamId, String[] fields){
        this.streamId = streamId;
        this.emitedFields = fields;
    }
    public SystemPrinter( String[] fields){
        this.streamId = null;
        this.emitedFields = fields;
    }

    @Override
    public String getStreamId() {
        return streamId;
    }

    @Override
    public String[] getEmittedFields() {
        return emitedFields;
    }

    @Override
    public void execute(OutputCollector collector, String streamId, Values values) throws FieldsMismatchException {
        values.forEach(o -> System.out.print("printing under threshold values " + o.toString()));
    }
}
