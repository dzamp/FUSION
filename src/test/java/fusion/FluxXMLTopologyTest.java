package fusion;

import flux.Flux;
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

    @Test
    public void testSimpleTopologyWithCmd() throws Exception {
        String s = "et";
        Flux.main(new String[]{"-s", "30000", "src/test/resources/mqttTopology.yaml"});
    }
}
