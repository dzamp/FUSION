//package flux.fusion;
//
//import algorithms.RandomWordSpout;
//import algorithms.ObjectCounterBolt;
//import flux.Flux;
//import flux.FluxBuilder;
//import flux.model.*;
//import org.apache.commons.cli.*;
//// import org.apache.log4j.Logger;
//// import org.apache.log4j.spi.LoggerFactory;
//import org.apache.storm.Config;
//import org.apache.storm.LocalCluster;
//import org.apache.storm.StormSubmitter;
//
//import org.apache.storm.generated.StormTopology;
//import org.apache.storm.generated.SubmitOptions;
//import org.apache.storm.generated.TopologyInitialStatus;
//import org.apache.storm.shade.com.twitter.chill.Base64;
//import org.apache.storm.topology.BoltDeclarer;
//import org.apache.storm.topology.SpoutDeclarer;
//import org.apache.storm.topology.TopologyBuilder;
//import org.apache.storm.tuple.Fields;
//import org.apache.storm.utils.Utils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
//import javax.xml.XMLConstants;
//import javax.xml.transform.stream.StreamSource;
//import javax.xml.validation.Schema;
//import javax.xml.validation.SchemaFactory;
//import javax.xml.validation.Validator;
//import java.io.*;
//import java.util.Map;
//import java.util.Properties;
//
///**
// * Created by jim on 5/9/2017.
// */
//public class Fusion extends Flux{
//
//    private static final Logger LOG = LoggerFactory.getLogger(Flux.class);
//
//    private static final String OPTION_LOCAL = "local";
//    private static final String OPTION_REMOTE = "remote";
//    private static final String OPTION_RESOURCE = "resource";
//    private static final String OPTION_SLEEP = "sleep";
//    private static final String OPTION_DRY_RUN = "dry-run";
//    private static final String OPTION_NO_DETAIL = "no-detail";
//    private static final String OPTION_NO_SPLASH = "no-splash";
//    private static final String OPTION_INACTIVE = "inactive";
//    private static final String OPTION_ZOOKEEPER = "zookeeper";
//    private static final String OPTION_FILTER = "filterOperation";
//    private static final String OPTION_ENV_FILTER = "env-filterOperation";
//    public static final Thread mainThread = Thread.currentThread();
//    static volatile boolean keepRunning = true;
//
//    public static void main(String[] args) throws Exception {
//        Options options = createOptions();
//        CommandLineParser parser = new BasicParser();
//        CommandLine cmd = parser.parse(options, args);
//        System.out.println("Arguments: " + cmd.getArgs());
//        if (cmd.getArgs().length != 1) {
//            usage(options);
//            System.exit(1);
//        }
//        runCli(cmd);
//    }
//
//    protected static Options createOptions(){
//        Options options = new Options();
//
//        options.addOption(option(0, "l", OPTION_LOCAL, "Run the topology in local mode."));
//
//        options.addOption(option(0, "r", OPTION_REMOTE, "Deploy the topology to a remote cluster."));
//
//        options.addOption(option(0, "R", OPTION_RESOURCE, "Treat the supplied path as a classpath resource instead of a file."));
//
//        options.addOption(option(1, "s", OPTION_SLEEP, "ms", "When running locally, the amount of time to sleep (in ms.) " +
//                "before killing the topology and shutting down the local cluster."));
//
//        options.addOption(option(0, "d", OPTION_DRY_RUN, "Do not run or deploy the topology. Just build, validate, " +
//                "and print information about the topology."));
//
//        options.addOption(option(0, "q", OPTION_NO_DETAIL, "Suppress the printing of topology details."));
//
//        options.addOption(option(0, "n", OPTION_NO_SPLASH, "Suppress the printing of the splash screen."));
//
//        options.addOption(option(0, "i", OPTION_INACTIVE, "Deploy the topology, but do not activate it."));
//
//        options.addOption(option(1, "z", OPTION_ZOOKEEPER, "host:port", "When running in local mode, use the ZooKeeper at the " +
//                "specified <host>:<port> instead of the in-process ZooKeeper. (requires Storm 0.9.3 or later)"));
//
//        options.addOption(option(1, "f", OPTION_FILTER, "file", "Perform property substitution. Use the specified file " +
//                "as a source of properties, and replace keys identified with {$[property name]} with the value defined " +
//                "in the properties file."));
//
//        options.addOption(option(0, "e", OPTION_ENV_FILTER, "Perform environment variable substitution. Replace keys" +
//                "identified with `${ENV-[NAME]}` will be replaced with the corresponding `NAME` environment value"));
//        return options;
//    }
//
//protected static boolean validateXml(String filename){
//    InputStream fileIs =null;
//    InputStream fileXsdIs =null;
//    try {
//        fileIs = new FileInputStream(new File(filename));
//        fileXsdIs = new FileInputStream(new File("src/main/resources/topology.xsd"));
//    } catch (FileNotFoundException e) {
//        e.printStackTrace();
//    }
//    try
//    {
//        SchemaFactory factory =
//                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//        Schema schema = factory.newSchema(new StreamSource(fileXsdIs));
//        Validator validator = schema.newValidator();
//        validator.validate(new StreamSource(fileIs));
//        return true;
//    }
//    catch(Exception ex)
//    {
//        return false;
//    }
//}
//
//
//
//    protected static void runCli(CommandLine cmd)throws Exception {
//        if(!cmd.hasOption(OPTION_NO_SPLASH)) {
//            printSplash();
//        }
//
//        boolean dumpYaml = cmd.hasOption("dump-yaml");
//
//        TopologyDef topologyDef = null;
//        String filePath = (String)cmd.getArgList().get(0);
//
//        // TODO conditionally load properties from a file our resource
//        String filterProps = null;
//        if(cmd.hasOption(OPTION_FILTER)){
//            filterProps = cmd.getOptionValue(OPTION_FILTER);
//        }
//
//        //changed in order to parse the XML topology
//        FusionParser parser = new FusionParser(new YAMLParser());
//        boolean envFilter = cmd.hasOption(OPTION_ENV_FILTER);
//
//
//        // if(!validateXml(filePath)){
//        //     LOG.error("Xml is invalid");
//        //     System.exit(1);
//        // }
//
//        if(cmd.hasOption(OPTION_RESOURCE)){
//            printf("Parsing classpath resource: %s", filePath);
//            topologyDef = parser.parseTopology(filePath);
//        } else {
//            printf("Parsing file: %s",
//                    new File(filePath).getAbsolutePath());
//            topologyDef = parser.parseTopology(filePath);
//        }
//
//
//        String topologyName = topologyDef.getName();
//        // merge contents of `config` into topology config
//        // Config conf = FluxBuilder.buildConfig(topologyDef);
//        Config conf = FluxBuilder.buildConfig(topologyDef);
//        ExecutionContext context = new ExecutionContext(topologyDef, conf);
//        StormTopology topology = FluxBuilder.buildTopology(context);
//        // TopologyBuilder builder2 = new TopologyBuilder();
//        // SpoutDeclarer declarer = builder2.setSpout("spout-1", new RandomWordSpout(),2);
//        // BoltDeclarer boltDeclarer = builder2.setBolt("bolt-1", new ObjectCounterBolt(),2);
//        // boltDeclarer.fieldsGrouping("spout-1", "spout-1 --> bolt-1", new Fields(new String(){"words"}));
//        // StormTopology topology2  = builder2.createTopology();
//        if(!cmd.hasOption(OPTION_NO_DETAIL)){
//            printTopologyInfo(context);
//        }
//
//        if(!cmd.hasOption(OPTION_DRY_RUN)) {
//            if (cmd.hasOption(OPTION_REMOTE)) {
//                LOG.info("Running remotely...");
//                // should the topology be active or inactive
//                SubmitOptions submitOptions = null;
//                if(cmd.hasOption(OPTION_INACTIVE)){
//                    LOG.info("Deploying topology in an INACTIVE state...");
//                    submitOptions = new SubmitOptions(TopologyInitialStatus.INACTIVE);
//                } else {
//                    LOG.info("Deploying topology in an ACTIVE state...");
//                    submitOptions = new SubmitOptions(TopologyInitialStatus.ACTIVE);
//                }
//                StormSubmitter.submitTopology(topologyName, conf, topology, submitOptions, null);
//            } else if(cmd.hasOption(OPTION_LOCAL)){
//                //set debug options
//                LocalCluster cluster = new LocalCluster();
//                conf.setDebug(true);
//                conf.registerMetricsConsumer(org.apache.storm.metric.LoggingMetricsConsumer.class, 10);
//                cluster.submitTopology(topologyName, conf, topology);
//                Utils.sleep(100000000);
//
//                Runtime.getRuntime().addShutdownHook(new Thread() {
//                    public void run() {
//                        System.out.println("Shutdown--------------------------");
//                        keepRunning = false;
//                        try {
//                            mainThread.join();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        cluster.killTopology(topologyName);
//                        cluster.shutdown();
//                    }
//                });
//                // LOG.error("To run in local mode run with 'storm local' instead of 'storm jar'");
//                // return;
//            }
//        }
//    }
//
//
//
//}
