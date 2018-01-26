package consumers;

import actions.ClassConverter;
import org.apache.storm.tuple.Values;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OutputFieldsClassMapper implements Serializable {

    protected List<ClassConverter<?>> converters = null;
    protected List<Class> classes = null;
    protected String regex = null;
    protected String[] classNames;

    public String getClassname(int index){
        return classNames[index];
    }

    //    protected Logger log;
    public OutputFieldsClassMapper(String... classNames) {
        setClasses(classNames);
        resolveClassConverters(classNames);
    }

    public OutputFieldsClassMapper() {

    }

    public OutputFieldsClassMapper withRegex(String regex) {
        this.regex = regex;
        return this;
    }

    public OutputFieldsClassMapper withClasses(String... classNames) {
        this.classNames = classNames;
        setClasses(classNames);
        resolveClassConverters(classNames);
        return this;
    }

    private void setClasses(String... classNames) {
        this.classes = new ArrayList<>();
        if (classNames != null)
            for (String clazz : classNames)
                try {
                    this.classes.add(Class.forName(clazz));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
    }


    private void resolveClassConverters(String[] classes) {
        converters = new ArrayList<>();
        for (String clazz : classes) {
            switch (clazz) {
                case "java.lang.Integer":
                    converters.add((ClassConverter<Integer>) Integer::valueOf);
                    break;
                case "java.lang.Double":
                    converters.add((ClassConverter<Double>) Double::valueOf);
                    break;
                case "java.lang.Float":
                    converters.add((ClassConverter<Float>) Float::valueOf);
                    break;
                case "java.lang.Long":
                    converters.add((ClassConverter<Long>) Long::valueOf);
                    break;
                default:
                    converters.add((ClassConverter<String>) value -> value);
            }
        }
    }

    public Values mapToValues(String message) {
        Values values = new Values();
        if (regex != null) {
            String[] stringValues = message.split(regex);
            for (int i = 0; i < stringValues.length; i++) {
                values.add(converters.get(i).convertToObject(stringValues[i]));
            }
        } else {
            values.add(converters.get(0).convertToObject(message));
        }
        return values;
    }

}
