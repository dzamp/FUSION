package flux.fusion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import flux.model.TopologyDef;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by jim on 5/9/2017.
 */
public class XMLParser implements IParser {

    //will we need validation here?

    @Override
    public TopologyDef parseTopology(String inputFile) {
        ObjectMapper objectMapper = new XmlMapper();
        JsonNode node=null;
        try {
            node = objectMapper.readTree(Files.readAllBytes(Paths.get(inputFile)));
            // JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonString);
            // save it as YAML
            String jsonAsYaml = new YAMLMapper().writeValueAsString(node);
            // return jsonAsYaml;
        } catch (IOException e) {
            e.printStackTrace();
        }
        // YAMLFa node.toString()
        TopologyDef topology = null;
        try {
            topology = objectMapper.readValue(
                    Files.readAllBytes(Paths.get(inputFile)),
                    TopologyDef.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //validation?
        return topology;
    }
}