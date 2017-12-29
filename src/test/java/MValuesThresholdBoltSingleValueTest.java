

import algorithms.MValuesThresholdBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.junit.Test;
import util.FilterOperation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Comparator;

public class MValuesThresholdBoltSingleValueTest {
    public class _MValuesThresholdBolt extends MValuesThresholdBolt {
        public Number testValue;
        Values acceptedValues, rejectedValues;
        public _MValuesThresholdBolt(String className, Number threshold, int positionInStream, String operator) {
            super(className, threshold, positionInStream, operator);
            this.resolveComparator(className);
            this.filterOperation = resolveFilterByOperator();
        }


        public int mockExecute() {
            this.acceptedValues = new Values();
            this.rejectedValues = new Values();
            this.filterOperation.apply(super.comparator, null, threshold, acceptedValues, rejectedValues);
            if (acceptedValues.size() != 0) return 1;
            else return 0;
        }

        @Override
        protected FilterOperation resolveFilterByOperator() {
            switch (operator) {
                case GREATER_THAN:
                    return new FilterOperation() {
                        @Override
                        public void apply(Comparator cmp, Tuple input, Number threshold, Values filteredValues, Values rejectedValues) {
                            //newValue-threshold > 0
                            if (comparator.compare(testValue, threshold) > 0) {
                                filteredValues.add(input);
                            } else rejectedValues.add(input);
                        }
                    };

                case LESS_THAN:
                    return new FilterOperation() {
                        @Override
                        public void apply(Comparator cmp, Tuple input, Number threshold, Values filteredValues, Values rejectedValues) {
                            //newValue-threshold < 0
                            if (comparator.compare(testValue, threshold) < 0) {
                                filteredValues.add(input);
                            } else rejectedValues.add(input);
                        }
                    };

                case EQUAL:
                    return new FilterOperation() {
                        @Override
                        public void apply(Comparator cmp, Tuple input, Number threshold, Values filteredValues, Values rejectedValues) {
                            //equality
                            if (comparator.compare(testValue, threshold) == positionInStream) {
                                filteredValues.add(input);
                            } else rejectedValues.add(input);
                        }
                    };

                case NOT_EQUAL:
                    return new FilterOperation() {
                        @Override
                        public void apply(Comparator cmp, Tuple input, Number threshold, Values filteredValues, Values rejectedValues) {
                            //inequality
                            if (comparator.compare(testValue, threshold) != 0) {
                                filteredValues.add(input);
                            } else rejectedValues.add(input);
                        }
                    };
                default:
                    return null;
            }
        }
    }

    @Test
    public void testIntegerThresholdGT() {
        //Remember VALUE GT THRESHOLD ? acceptedValues+1 : rejectedValues+1
        _MValuesThresholdBolt testBolt = new _MValuesThresholdBolt("java.lang.Integer", 80, 0, "gt");
        testBolt.testValue = 56;
        testBolt.mockExecute();
        assertEquals(1, testBolt.rejectedValues.size());
        testBolt.testValue = 88;
        testBolt.mockExecute();
        assertEquals(1, testBolt.acceptedValues.size());
        testBolt.testValue = 80;
        testBolt.mockExecute();
        assertEquals(1, testBolt.rejectedValues.size());
    }

    @Test
    public void testIntegerThresholdLT() {
        //Remember VALUE LT THRESHOLD ? acceptedValues+1 : rejectedValues+1
        _MValuesThresholdBolt testBolt = new _MValuesThresholdBolt("java.lang.Integer", 80, 0, "lt");
        testBolt.testValue = 56;
        testBolt.mockExecute();
        assertEquals(1, testBolt.acceptedValues.size());
        testBolt.testValue = 88;
        testBolt.mockExecute();
        assertEquals(1, testBolt.rejectedValues.size());
        testBolt.testValue = 56;
        testBolt.mockExecute();
        assertEquals(1, testBolt.acceptedValues.size());
    }

    @Test
    public void testIntegerThresholdEQ() {
        //Remember VALUE EQ THRESHOLD ? acceptedValues+1 : rejectedValues+1
        _MValuesThresholdBolt testBolt = new _MValuesThresholdBolt("java.lang.Integer", 80, 0, "eq");
        testBolt.testValue = 80;
        testBolt.mockExecute();
        assertEquals(1, testBolt.acceptedValues.size());
        testBolt.testValue = 88;
        testBolt.mockExecute();
        assertEquals(1,testBolt.rejectedValues.size());
    }

    @Test
    public void testIntegerThresholdNEQ() {
        //Remember VALUE NEQ THRESHOLD ? acceptedValues+1 : rejectedValues+1
        _MValuesThresholdBolt testBolt = new _MValuesThresholdBolt("java.lang.Integer", 80, 0, "neq");
        testBolt.testValue = 80;
        testBolt.mockExecute();
        assertEquals(1, testBolt.rejectedValues.size());
        testBolt.testValue = 88;
        testBolt.mockExecute();
        assertEquals(1,testBolt.acceptedValues.size());
    }




    @Test
    public void testDoubleThresholdGT() {
        //Remember VALUE GT THRESHOLD ? acceptedValues+1 : rejectedValues+1
        _MValuesThresholdBolt testBolt = new _MValuesThresholdBolt("java.lang.Double", 80.0, 0, "gt");
        testBolt.testValue = 56.2;
        testBolt.mockExecute();
        assertEquals(1, testBolt.rejectedValues.size());
        testBolt.testValue = 88.3;
        testBolt.mockExecute();
        assertEquals(1, testBolt.acceptedValues.size());
        testBolt.testValue = 80.00;
        testBolt.mockExecute();
        assertEquals(1, testBolt.rejectedValues.size());
    }

    @Test
    public void testDoubleThresholdLT() {
        //Remember VALUE LT THRESHOLD ? acceptedValues+1 : rejectedValues+1
        _MValuesThresholdBolt testBolt = new _MValuesThresholdBolt("java.lang.Double", 80.1, 0, "lt");
        testBolt.testValue = 56.1;
        testBolt.mockExecute();
        assertEquals(1, testBolt.acceptedValues.size());
        testBolt.testValue = 88;
        testBolt.mockExecute();
        assertEquals(1, testBolt.rejectedValues.size());
        testBolt.testValue = 56.3;
        testBolt.mockExecute();
        assertEquals(1, testBolt.acceptedValues.size());
    }

    @Test
    public void testDoubleThresholdEQ() {
        //Remember VALUE EQ THRESHOLD ? acceptedValues+1 : rejectedValues+1
        _MValuesThresholdBolt testBolt = new _MValuesThresholdBolt("java.lang.Double", 80.0, 0, "eq");
        testBolt.testValue = 80.0;
        testBolt.mockExecute();
        assertEquals(1, testBolt.acceptedValues.size());
        testBolt.testValue = 80;
        testBolt.mockExecute();
        assertEquals(1, testBolt.acceptedValues.size());
        testBolt.testValue = 88.9;
        testBolt.mockExecute();
        assertEquals(1,testBolt.rejectedValues.size());
    }

    @Test
    public void testDoubleThresholdNEQ() {
        //Remember VALUE NEQ THRESHOLD ? acceptedValues+1 : rejectedValues+1
        _MValuesThresholdBolt testBolt = new _MValuesThresholdBolt("java.lang.Double", 80.0, 0, "neq");
        testBolt.testValue = 80;
        testBolt.mockExecute();
        assertEquals(1, testBolt.rejectedValues.size());
        testBolt.testValue = 80.0;
        testBolt.mockExecute();
        assertEquals(1, testBolt.rejectedValues.size());
        testBolt.testValue = 88.3;
        testBolt.mockExecute();
        assertEquals(1,testBolt.acceptedValues.size());
    }





    @Test
    public void testLongThresholdGT() {
        //Remember VALUE GT THRESHOLD ? acceptedValues+1 : rejectedValues+1
        _MValuesThresholdBolt testBolt = new _MValuesThresholdBolt("java.lang.Long", 29_000_000L, 0, "gt");
        testBolt.testValue = 25_000_000L;
        testBolt.mockExecute();
        assertEquals(1, testBolt.rejectedValues.size());
        testBolt.testValue = 30_000_000L;
        testBolt.mockExecute();
        assertEquals(1, testBolt.acceptedValues.size());
        testBolt.testValue = 28_999_999L;
        testBolt.mockExecute();
        assertEquals(1, testBolt.rejectedValues.size());
    }

    @Test
    public void testLongThresholdEQ() {
        //Remember VALUE EQ THRESHOLD ? acceptedValues+1 : rejectedValues+1
        _MValuesThresholdBolt testBolt = new _MValuesThresholdBolt("java.lang.Long", 29_000_000L, 0, "eq");
        testBolt.testValue = 29_000_000L;
        testBolt.mockExecute();
        assertEquals(1, testBolt.acceptedValues.size());
        testBolt.testValue = 29_000_001L;
        testBolt.mockExecute();
        assertEquals(1,testBolt.rejectedValues.size());
    }

    @Test
    public void testLongThresholdLT() {
        //Remember VALUE LT THRESHOLD ? acceptedValues+1 : rejectedValues+1
        _MValuesThresholdBolt testBolt = new _MValuesThresholdBolt("java.lang.Long", 29_000_000L, 0, "lt");
        testBolt.testValue = 28_999_999L;
        testBolt.mockExecute();
        assertEquals(1, testBolt.acceptedValues.size());
        testBolt.testValue = 29_000_001L;
        testBolt.mockExecute();
        assertEquals(1, testBolt.rejectedValues.size());
        testBolt.testValue = 28_000_000L;
        testBolt.mockExecute();
        assertEquals(1, testBolt.acceptedValues.size());
    }

    @Test
    public void testLongThresholdNEQ() {
        //Remember VALUE NEQ THRESHOLD ? acceptedValues+1 : rejectedValues+1
        _MValuesThresholdBolt testBolt = new _MValuesThresholdBolt("java.lang.Long", 29_000_000L, 0, "neq");
        testBolt.testValue = 29_000_000L;
        testBolt.mockExecute();
        assertEquals(1, testBolt.rejectedValues.size());
        testBolt.testValue = 29_000_001L;
        testBolt.mockExecute();
        assertEquals(1,testBolt.acceptedValues.size());
    }


//TODO test for float



}
