package fusion;

import flux.FluxBuilder;
import flux.model.ExecutionContext;
import flux.model.TopologyDef;
import org.apache.storm.Config;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertNotNull;

/**
 * Created by jim on 5/9/2017.
 */
public class FluxXMLTopologyTest {
//    @Test
//    public void testSimpleTopologyImplTest() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException, NoSuchMethodException, ClassNotFoundException, InvalidTopologyException, AuthorizationException, AlreadyAliveException {
//        FusionParserTest test = new FusionParserTest();
//        test.testParseXMLSimpleTopology();
//        FusionParser parser = new FusionParser(new XMLParser());
//        String filename = "/home/jim/Master/FUSION/src/test/resources/simpleTopology.xml";
//        TopologyDef topologyDef = parser.parseTopology(filename);
//        Config conf = FluxBuilder.buildConfig(topologyDef);
//        ExecutionContext context = new ExecutionContext(topologyDef, conf);
//        StormTopology topology = FluxBuilder.buildTopology(context);
//        assertNotNull(topology);
//    }
//
//    @Test
//    public void testSimpleTopologyWithCmd() throws Exception {
//        Fusion.main(new String[]{"-s", "30000", "src/test/resources/simpleTopology.xml"});
//    }
}
