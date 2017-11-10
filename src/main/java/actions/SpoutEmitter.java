package actions;

import consumers.FieldMapper;
import exceptions.FieldsMismatchException;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.tuple.Values;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * This emitter hadles emissions from spouts.
 */
public class SpoutEmitter implements SpoutAction, FieldMapper, Serializable {

    protected List<Class> classes = null;
    protected String regex = null;
    protected String streamId = null;
    protected String[] emittedFields;

    protected List<ClassConverter<?>> converter;

    public SpoutEmitter(String[] fields, String... classes) {
        this.emittedFields = fields;
        prepareClassLayout(classes);
    }

    private void prepareClassLayout(String[] classes){
        setClasses(classes);
        resolveClassConverters(classes);
    }

    public SpoutEmitter(String streamId, String[] fields, String regex, String... classes) {
        if (!streamId.isEmpty()) this.streamId = streamId;
        if (!regex.isEmpty()) this.regex = regex;
        this.emittedFields = fields;
        prepareClassLayout(classes);
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
                for (int i=0; i < stringValues.length; i++) {
                    values.add(converter.get(i).convertToObject(stringValues[i]));
                }
            } else {
                values.add(converter.get(0).convertToObject(message));
            }
        }
        return values;
    }


    @Override
    public void execute(SpoutOutputCollector collector, String streamId, String message) throws FieldsMismatchException {
        Values values = mapToValues(message, regex, this.classes.toArray(new Class[0]));
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


    private void resolveClassConverters(String[] classes){
        converter = new ArrayList<>();
        for(String clazz: classes){
            switch (clazz){
                case "java.lang.Integer":
                    converter.add(new ClassConverter<Integer>() {
                        @Override
                        public Integer convertToObject(String value) {
                            return Integer.valueOf(value);
                        }
                    });
                    break;
                case "java.lang.Double":
                    converter.add(new ClassConverter<Double>() {
                        @Override
                        public Double convertToObject(String value) {
                            return Double.valueOf(value);
                        }
                    });
                    break;
                case "java.lang.Float":
                    converter.add(new ClassConverter<Float>() {
                        @Override
                        public Float convertToObject( String value) {
                            return Float.valueOf(value);
                        }
                    });
                    break;
                case "java.lang.Long":
                    converter.add(new ClassConverter<Long>() {
                        @Override
                        public Long convertToObject(String value) {
                            return Long.valueOf(value);
                        }
                    });
                    break;
                default:
                    converter.add(new ClassConverter<String>() {
                        @Override
                        public String convertToObject(String value) {
                            return value;
                        }
                    });
            }
        }



    }

}
