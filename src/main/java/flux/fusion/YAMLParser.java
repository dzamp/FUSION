//package flux.fusion;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
//import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
//import flux.model.TopologyDef;
//import org.apache.log4j.Logger;
//import org.apache.storm.topology.TopologyBuilder;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
///**
// * Created by jim on 8/9/2017.
// */
//public class YAMLParser implements IParser {
//    Logger LOG = Logger.getLogger(YAMLParser.class);
//    @Override
//    public TopologyDef parseTopology(String inputFile) {
//                if (inputFile.endsWith(".xml")) {
//                    ObjectMapper objectMapper = new XmlMapper();
//                    JsonNode node = null;
//                    try {
//                        node = objectMapper.readTree(Files.readAllBytes(Paths.get(inputFile)));
//                        String yaml = new YAMLMapper().writeValueAsString(node);
//                        LOG.info("Attempting converting to YAML");
//                        LOG.info(yaml);
//                        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
//                        return (TopologyDef)mapper.readValue(yaml.getBytes(),TopologyDef.class);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//        }
//        else {
//            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
//            try {
//                return (TopologyDef)objectMapper.readValue(Files.readAllBytes(Paths.get(inputFile)),TopologyDef.class);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
//}
