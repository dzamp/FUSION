package consumers;

import actions.ClassConverter;
import org.apache.storm.kafka.KeyValueScheme;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FusionScheme extends StringScheme implements KeyValueScheme {
    private String[] fieldNames;
    private static final Charset UTF8_CHARSET;
    public static final String STRING_SCHEME_KEY = "str";
    private String regex = null;
    protected List<ClassConverter<?>> converters=null;
    protected List<Class> classes = null;
    static {
        UTF8_CHARSET = StandardCharsets.UTF_8;
    }



   public FusionScheme withRegex(String regex) {
        this.regex = regex;
        return this;
    }

    public FusionScheme withFields(String[] fieldNames){
        this.fieldNames = fieldNames;
        return this;
    }

    public FusionScheme withClasses(String[] classNames){
        setClasses(classNames);
        resolveClassConverters(classNames);
        return this;
    }


    public Values mapToValues(String message, String regex, Class[] args) {
        Values values = new Values();
        if (regex != null) {
            String[] stringValues = message.split(regex);
            for (int i=0; i < stringValues.length; i++) {
                values.add(converters.get(i).convertToObject(stringValues[i]));
            }
        } else {
            values.add(converters.get(0).convertToObject(message));
        }
        return values;
    }



    public static String deserializeString(ByteBuffer string) {
        if (string.hasArray()) {
            int base = string.arrayOffset();
            return new String(string.array(), base + string.position(), string.remaining());
        } else {
            return new String(Utils.toByteArray(string), UTF8_CHARSET);
        }
    }

    @Override
    public List<Object> deserializeKeyAndValue(final ByteBuffer key, final ByteBuffer value) {
//        String key = byteBuffer.toString();
//        String value = byteBuffer1.toString();
//        return ImmutableList.of(key, value);
        if ( key == null ) {
            return deserialize(value);
        }
        String keyString = StringScheme.deserializeString(key);
        String valueString = StringScheme.deserializeString(value);
        //TODO INSERT HERE PATTERN TO SPLIT!!
        String[] stringValues =null;
        if(regex!=null){
            return mapToValues(valueString,regex,this.classes.toArray(new Class[0]));
        }

        //also if we have a key, since tuples are essentialy maps should we include the key?
        //Isn't the field values enough to represent a key?

        return new Values(valueString);
    }

    @Override
    public List<Object> deserialize(ByteBuffer ser) {
        String stringValue = deserializeString(ser);
        String[] values = null;
        if(regex!=null) {
            values= stringValue.split(regex);
            return mapToValues(stringValue,regex,this.classes.toArray(new Class[0]));
        }
        return new Values(stringValue);
    }

    private void setClasses(String... classes) {
       if( this.classes ==null) this.classes= new ArrayList<>();
        for (String clazz : classes)
            try {
                this.classes.add(Class.forName(clazz));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
    }


    private void resolveClassConverters(String... classes){
        if(this.converters == null) this.converters = new ArrayList<>();
        for(String clazz: classes){
            switch (clazz){
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

    @Override
    public Fields getOutputFields() {
            return new Fields(fieldNames);
    }
}
