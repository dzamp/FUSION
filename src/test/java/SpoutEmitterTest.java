import actions.ClassConverter;
import actions.SpoutEmitter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by jim on 16/11/2017.
 */
public class SpoutEmitterTest {
    @Test
    public void checkClassConverters() {
        //Check that class names of String are being mapped correctly to the equivalent Class objects
        Object[] classes = new Object[]{String.class, Integer.class, Long.class};
        SpoutEmitter_ emitter = new SpoutEmitter_("dummy", new String[]{"id", "value", "timestamp"}, ",", String.class.getCanonicalName(), Integer.class.getCanonicalName(), Long.class.getCanonicalName());
        assertArrayEquals(classes, emitter.getCLasses().toArray());
        
    }

    public class SpoutEmitter_ extends SpoutEmitter {
        public SpoutEmitter_(String streamId, String[] fields, String regex, String... classes) {
            super(streamId, fields, regex, classes);
        }

        public SpoutEmitter_(String[] fields, String... classes) {
            super(fields, classes);
        }

        public SpoutEmitter_(String[] fields) {
            super(fields);
        }

        public List<Class> getCLasses() {
            return this.classes;
        }

        public List<ClassConverter<?>> getConverters() {
            return this.converters;
        }
    }
}
