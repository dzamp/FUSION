//package flux.fusion;
//
//import flux.model.TopologyDef;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
///**
// * Created by jim on 4/9/2017.
// */
//public class FusionParser {
//    private static final Logger LOG = LoggerFactory.getLogger(FusionParser.class);
//
//    public FusionParser(IParser parser) {
//        this.parser = parser;
//    }
//
//    private IParser parser;
//
//    public IParser getParser() {
//        return parser;
//    }
//
//    public void setParser(IParser parser) {
//        this.parser = parser;
//    }
//
//    public TopologyDef parseTopology(String inputFile){
//        //delegate the parsing to an interface since there will be many implementations
//        return parser.parseTopology(inputFile);
//    }
//
//}
