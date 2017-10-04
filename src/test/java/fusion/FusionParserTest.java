package fusion;


import flux.fusion.FusionParser;
import flux.fusion.XMLParser;
import flux.model.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jim on 5/9/2017.
 */
public class FusionParserTest {

    @Test
    public void testParseXMLSimpleTopology() {
        FusionParser parser = new FusionParser(new XMLParser());
        String filename = "src/test/resources/simpleTopology.xml";
        TopologyDef topologyDef = parser.parseTopology(filename);


        assertNotNull(topologyDef);
        List<BoltDef> boltList = topologyDef.getBolts();
        assertNotNull(boltList);
        assertEquals(boltList.size(), 1);
        assertEquals(boltList.get(0).getId(), "bolt-1");
        assertEquals(boltList.get(0).getClassName(), "algorithms.WordCounter");
        assertEquals(boltList.get(0).getParallelism(), 2);

        List<SpoutDef> spoutList = topologyDef.getSpouts();
        assertNotNull(spoutList);
        assertEquals(spoutList.size(), 1);
        assertEquals(spoutList.get(0).getId(), "spout-1");
        assertEquals(spoutList.get(0).getClassName(), "algorithms.RandomWordSpout");
        assertEquals(spoutList.get(0).getParallelism(), 2);


        List<StreamDef> streamList = topologyDef.getStreams();
        assertNotNull(streamList);
        assertEquals(streamList.size(), 1);
        assertEquals(streamList.get(0).getName(), "spout-1 --> bolt-1");
        assertEquals(streamList.get(0).getFrom(), "spout-1");
        assertEquals(streamList.get(0).getTo(), "bolt-1");
        assertEquals(streamList.get(0).getGrouping().getType(), GroupingDef.Type.FIELDS);
        assertEquals(streamList.get(0).getGrouping().getArgs(), Arrays.asList("word"));
    }

    @Test
    public void testParseXMLSimpleWithArgsTopology() {
        FusionParser parser = new FusionParser(new XMLParser());
        String filename = "src/test/resources/simpleWithArgsTopology.xml";
        TopologyDef topologyDef = parser.parseTopology(filename);
        assertNotNull(topologyDef);

        List<BoltDef> boltList = topologyDef.getBolts();
        assertNotNull(boltList);
        assertEquals(boltList.size(), 1);
        assertEquals(boltList.get(0).getId(), "bolt-1");
        assertEquals(boltList.get(0).getClassName(), "algorithms.WordCounter");
        assertEquals(boltList.get(0).getParallelism(), 2);

        List<SpoutDef> spoutList = topologyDef.getSpouts();
        assertNotNull(spoutList);
        assertEquals(spoutList.size(), 1);
        assertEquals(spoutList.get(0).getId(), "spout-1");
        assertEquals(spoutList.get(0).getClassName(), "algorithms.RandomWordSpout");
        assertEquals(spoutList.get(0).getParallelism(), 2);
        assertEquals(spoutList.get(0).getConstructorArgs().get(0), (Object) new String("word"));

        List<StreamDef> streamList = topologyDef.getStreams();
        assertNotNull(streamList);
        assertEquals(streamList.size(), 1);
        assertEquals(streamList.get(0).getName(), "spout-1 --> bolt-1");
        assertEquals(streamList.get(0).getFrom(), "spout-1");
        assertEquals(streamList.get(0).getTo(), "bolt-1");
        assertEquals(streamList.get(0).getGrouping().getType(), GroupingDef.Type.FIELDS);
        assertEquals(streamList.get(0).getGrouping().getArgs(), Arrays.asList("words"));


    }


}
