package parser;

import filter.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        TestExampleTopologies.class,
        TestKafkaExtendedModel.class,
        TestKafkaSpouts.class,
        TestMqttSpouts.class
})
public class ParserTestSuite {
}
