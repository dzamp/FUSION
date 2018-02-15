package flux.model.extended;

import abstraction.FusionBolt;
import consumers.FusionIRichSpout;
import flux.model.*;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FluxBuilderExtra {
    private static final Logger LOG = LoggerFactory.getLogger(FluxBuilderExtra.class);
    public static void buildStreamDeclarers(ExecutionContext context, TopologyBuilder builder) {
        List<StreamDef> streamDefs = context.getTopologyDef().getStreams();
        List<SpoutDef> spoutDefs = context.getTopologyDef().getSpouts();
        //start from spouts and give the topology fields
        //if we have usual spouts or mqttSpouts we have to find the fields that are being sent
        for (SpoutDef spoutDef : spoutDefs) {
            IRichSpout spout = context.getSpout(spoutDef.getId());
            if (spout instanceof FusionIRichSpout) {
                for (StreamDef streamDef : streamDefs) {
                    String streamId = null;
                    if (streamDef.getFrom().equals(spoutDef.getId())) {
                        if (streamDef.getGrouping().getStreamId() != null) {
                            streamId = streamDef.getGrouping().getStreamId();
                            addStreamToVertex(context, spoutDef, streamId);
                        }
                        BoltDef boltdef = context.getTopologyDef().getBoltDef(streamDef
                                .getTo());
                        if (boltdef instanceof FusionBoltDef) {
                            FusionBoltDef fusionBoltDef = context.getTopologyDef()
                                    .getFusionBoltDef(streamDef.getTo());
                            if(fusionBoltDef == null)
                                fusionBoltDef= (FusionBoltDef) context.getTopologyDef().getBoltDef(streamDef.getTo());
                            if(fusionBoltDef == null) {
                                LOG.error("Cannot find fusion bolt with id " + streamDef.getTo());
                            }
                            findConnectingBoltsDFS(fusionBoltDef, streamDefs, context, ((FusionIRichSpout) spout).getFieldNames());
                        }
                    }
                }
            }
        }
    }


    public static void findConnectingBoltsDFS(FusionBoltDef fusionBoltDefFrom, List<StreamDef> streamDefs, ExecutionContext context, String[] fields) {
        FusionBolt from = (FusionBolt) context.getBolt(fusionBoltDefFrom.getId());

        boolean isTerminal = true;
        for (StreamDef streamDef : streamDefs) {
            String streamId = null;
            if (streamDef.getFrom().equals(fusionBoltDefFrom.getId())) {
                isTerminal = false;
                if (streamDef.getGrouping().getStreamId() != null) {
                    streamId = streamDef.getGrouping().getStreamId();
                    addStreamToVertex(context, fusionBoltDefFrom, streamId);
                }
                fusionBoltDefFrom.setFields(fields);
                from.setFields(false, fields);
                BoltDef boltDefTo = context.getTopologyDef().getBoltDef(streamDef.getTo());
                if (boltDefTo instanceof FusionBoltDef) {
                    FusionBoltDef fusionBoltDefTo = (FusionBoltDef) boltDefTo;
                    findConnectingBoltsDFS(fusionBoltDefTo, streamDefs, context, from.getOutgoingFields());
                }
            }
        }
        if (isTerminal) {
            //if node is terminal then we have to explicity invoke setFields with true parameter, in order to avoid building the outcoming fields
            ((FusionBolt) context.getBolt(fusionBoltDefFrom.getId())).setFields(true, fields);
        }
    }

    private static void addStreamToVertex(ExecutionContext context, VertexDef vertex, String streamId) {

        if (vertex instanceof SpoutDef) {
            ((FusionIRichSpout) context.getSpout(vertex.getId()))
                    .addOutgoingStreamName(streamId);
        }
        if (vertex instanceof FusionBoltDef) {
            if (context.getBolt(vertex.getId()) instanceof FusionBolt) {
                ((FusionBolt) context.getBolt(vertex.getId())).addOutgoingStreamName(streamId);
            }

        }

    }


}
