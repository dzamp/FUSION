package filter;

import abstraction.FieldFilter;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by jim on 2/1/2018.
 */
public class FieldFilterTestCase {
    ArrayList<String> fields = new ArrayList(Arrays.asList("id", "name", "surname", "age", "gender"));
    String[] fieldsTobeRemoved = new String[]{"name", "surname"};

    @Test
    public void testFieldFilter() {
        FieldFilter fieldFilter = new FieldFilter(fieldsTobeRemoved);
        Tuple unfiltered = mock(Tuple.class, RETURNS_DEEP_STUBS);
//        when(unfiltered.getFields()).thenReturn(new Fields(fieldList));
        when(unfiltered.getFields().toList()).thenReturn(fields);
        when(unfiltered.getValueByField(fields.get(0))).thenReturn(UUID.randomUUID());
        when(unfiltered.getValueByField(fields.get(3))).thenReturn(27);
        when(unfiltered.getValueByField(fields.get(4))).thenReturn("male");

        Values values = fieldFilter.executeAlgorithm(unfiltered);

        //assert here
        assertEquals(values.get(1),27);
        assertEquals(values.get(2),"male");

    }


}
