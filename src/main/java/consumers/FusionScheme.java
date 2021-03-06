package consumers;

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
    public static final String STRING_SCHEME_KEY = "str";
    private static final Charset UTF8_CHARSET;

    static {
        UTF8_CHARSET = StandardCharsets.UTF_8;
    }

    protected List<Class> classes = null;
    protected OutputFieldsClassMapper mapper;
    private String[] fieldNames;

    public FusionScheme() {
        mapper = new OutputFieldsClassMapper();
    }

    public static String deserializeString(ByteBuffer string) {
        if (string.hasArray()) {
            int base = string.arrayOffset();
            return new String(string.array(), base + string.position(), string.remaining());
        } else {
            return new String(Utils.toByteArray(string), UTF8_CHARSET);
        }
    }

    public FusionScheme withRegex(String regex) {
        mapper.withRegex(regex);
        return this;
    }

    public FusionScheme withFields(String[] fieldNames) {
        this.fieldNames = fieldNames;
        return this;
    }

    public FusionScheme withClasses(String[] classNames) {
        mapper.withClasses(classNames);
        return this;
    }

    @Override
    public List<Object> deserializeKeyAndValue(final ByteBuffer key, final ByteBuffer value) {
        if (key == null) {
            return deserialize(value);
        }
        String keyString = StringScheme.deserializeString(key);
        String valueString = StringScheme.deserializeString(value);
        String[] stringValues = null;
        if (mapper.regex != null) {
            return mapper.mapToValues(valueString);
        }

        //also if we have a key, since tuples are essentialy maps should we include the key?
        //Isn't the field values enough to represent a key?

        return new Values(valueString);
    }

    @Override
    public List<Object> deserialize(ByteBuffer ser) {
        String stringValue = deserializeString(ser);
        String[] values = null;
        if (mapper.regex != null) {
            values = stringValue.split(mapper.regex);
            return mapper.mapToValues(stringValue);
        }
        return new Values(stringValue);
    }

    private void setClasses(String... classes) {
        if (this.classes == null) this.classes = new ArrayList<>();
        for (String clazz : classes)
            try {
                this.classes.add(Class.forName(clazz));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
    }


    @Override
    public Fields getOutputFields() {
        return new Fields(fieldNames);
    }
}
