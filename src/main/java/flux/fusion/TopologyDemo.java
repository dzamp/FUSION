package flux.fusion;


import flux.model.TopologyDef;

/**
 * Created by jim on 5/9/2017.
 */
public class TopologyDemo {
    public static final Thread mainThread = Thread.currentThread();
    static volatile boolean keepRunning = true;

    public static void main(String[] args) {
        FusionParser parser = new FusionParser(new XMLParser());
        String filename = "/home/jim/Master/FUSION-STORM/apache.storm.forked/storm/flux/flux-core/src/main/resources/simpleTopology.xml";
        TopologyDef topologyDef = parser.parseTopology(filename);
        System.out.println("topology parsed");

    }

}
