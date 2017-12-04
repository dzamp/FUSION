package actions;

import exceptions.FieldsMismatchException;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Values;

import java.io.Serializable;
import java.util.List;

public class BoltEmitter implements BoltAction, Serializable{
    public String streamId;
    public String[] fields;

    public BoltEmitter(String streamId, String[] fields){
        this.streamId = streamId;
        this.fields = fields;
    }

    public BoltEmitter(String[] fields){
        this.streamId = null;
        this.fields = fields;
    }
    @Override
    public String getStreamId() {
        return this.streamId;
    }

    @Override
    public String[] getEmittedFields() {
        return this.fields;
    }

    @Override
    public void execute(OutputCollector collector, String streamId, Values values) throws FieldsMismatchException {
//        List<Integer> list;
        if (values != null && values.size() > 0) {
            if(streamId == null) {
                //direct emit
               collector.emit(values);
            }
            else {
                if(values.size() != fields.length){
                    throw new FieldsMismatchException("Emitted Values do not match with declaration");
                }
//               list=  collector.emit(streamId, values);
            }
        }
    }

}
