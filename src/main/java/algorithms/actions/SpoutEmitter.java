package algorithms.actions;

import algorithms.consumers.FieldMapper;
import algorithms.exceptions.FieldsMismatchException;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Values;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SpoutEmitter implements SpoutAction, FieldMapper,Serializable {

    protected List<Class> classes = null;
    protected String regex = null;
    protected String streamId = null;
    protected String[] emittedFields;

    public SpoutEmitter(String[] fields,  String... classes) {
        this.emittedFields = fields;
        setClasses(classes);
    }


    public SpoutEmitter(String streamId, String[] fields, String regex, String... classes) {
        if (!streamId.isEmpty()) this.streamId = streamId;
        if (!regex.isEmpty()) this.regex = regex;
        this.emittedFields = fields;
        setClasses(classes);
    }

    private void setClasses(String... classes) {
        this.classes = new ArrayList<>();
        for (String clazz : classes)
            try {
                this.classes.add(Class.forName(clazz));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
    }

    public SpoutEmitter(String[] fields) {
        this.emittedFields = fields;
    }

    @Override
    public String getStreamId() {
        return this.streamId;
    }

    @Override
    public String[] getEmittedFields() {
        return this.emittedFields;
    }


    @Override
    public Values mapToValues(String message, String regex, Class[] args) {
        Values values = new Values();
        for (Class clazz : args) {
            if (regex != null) {
                String[] stringValues = message.split(regex);
                for (String str : stringValues) {
                    values.add(args[0].cast(str));
                }
            } else values.add(message);
        }
        return values;
    }

    @Override
    public void execute(SpoutOutputCollector collector, String streamId, String message) throws FieldsMismatchException {
        Values values = mapToValues(message, regex, (Class[]) this.classes.toArray());
        if (values != null && values.size() > 0) {
            if (streamId == null) {
                //direct emit
                collector.emit(values);
            } else {
                if (values.size() != emittedFields.length) {
                    throw new FieldsMismatchException("Emitted Values do not match with declaration");
                }
                collector.emit(streamId, values);
            }
        }
    }


}
