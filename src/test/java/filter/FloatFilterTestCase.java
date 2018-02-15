package filter;

import algorithms.ValueFilter;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FloatFilterTestCase {
    String classname = "java.lang.Float";

    @Test
    public void testGreaterThan(){
        ValueFilter greatherThanFilter = new ValueFilter().withOperator("gt").withThreshold(100.0,classname).onPosition(2).build();
        Tuple t  = mock(Tuple.class);
        //Assert value less than threshold, values should be null
        when(t.getValue(2)).thenReturn(50.0);
        Values values = greatherThanFilter.executeAlgorithm(t);
        assertNull(values);
        //assert value gt threshold, values should not be null
        when(t.getValue(2)).thenReturn(150.0);
        values = greatherThanFilter.executeAlgorithm(t);
        assertNotNull(values);
        assertEquals(((Tuple)values.get(0)).getValue(2),150.0);
    }

    @Test
    public void testLessThan(){
        ValueFilter greatherThanFilter = new ValueFilter().withOperator("lt").withThreshold(100.0,classname).onPosition(2).build();
        Tuple t  = mock(Tuple.class);
        //Assert value greater than threshold, values should not be null
        when(t.getValue(2)).thenReturn(500.0);
        Values values = greatherThanFilter.executeAlgorithm(t);
        assertNull(values);
        //assert value less  than threshold, values should be null
        when(t.getValue(2)).thenReturn(50.0);
        values = greatherThanFilter.executeAlgorithm(t);
        assertNotNull(values);
        assertEquals(((Tuple)values.get(0)).getValue(2),50.0);
    }

    @Test
    public void testEqual(){
        ValueFilter greatherThanFilter = new ValueFilter().withOperator("eq").withThreshold(100.0,classname).onPosition(2).build();
        Tuple t  = mock(Tuple.class);
        //Assert value less than threshold, values should be null
        when(t.getValue(2)).thenReturn(50.0);
        Values values = greatherThanFilter.executeAlgorithm(t);
        assertNull(values);
        //assert value equal to threshold, values should not be null
        when(t.getValue(2)).thenReturn(100.0);
        values = greatherThanFilter.executeAlgorithm(t);
        assertNotNull(values);
        assertEquals(((Tuple)values.get(0)).getValue(2),100.0);
    }

    @Test
    public void testNotEqual(){
        ValueFilter greatherThanFilter = new ValueFilter().withOperator("neq").withThreshold(100,classname).onPosition(2).build();
        Tuple t  = mock(Tuple.class);
        //Assert value less than threshold, values should not be null
        when(t.getValue(2)).thenReturn(100.0);
        Values values = greatherThanFilter.executeAlgorithm(t);
        assertNull(values);
        //assert value equal to threshold, values should not be null
        when(t.getValue(2)).thenReturn(50.0);
        values = greatherThanFilter.executeAlgorithm(t);
        assertNotNull(values);
        assertEquals(((Tuple)values.get(0)).getValue(2),50.0);
    }
}
