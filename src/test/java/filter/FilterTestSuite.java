package filter;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        DoubleFilterTestCase.class,
        FloatFilterTestCase.class,
        IntegerFilterTestCase.class,
        LongFilterTestCase.class,
        FieldFilterTestCase.class
})
public class FilterTestSuite {
}
