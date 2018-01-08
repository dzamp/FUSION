package parser;

import flux.Flux;
import flux.FluxBuilder;
import flux.model.ExecutionContext;
import flux.model.TopologyDef;
import flux.parser.FluxParser;
import org.apache.commons.cli.CommandLine;
import org.apache.storm.Config;
import org.apache.storm.generated.StormTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FluxParserTest extends Flux {
    private static final Logger LOG = LoggerFactory.getLogger(Flux.class);

    private static final String OPTION_LOCAL = "local";
    private static final String OPTION_REMOTE = "remote";
    private static final String OPTION_RESOURCE = "resource";
    private static final String OPTION_SLEEP = "sleep";
    private static final String OPTION_DRY_RUN = "dry-run";
    private static final String OPTION_NO_DETAIL = "no-detail";
    private static final String OPTION_NO_SPLASH = "no-splash";
    private static final String OPTION_INACTIVE = "inactive";
    private static final String OPTION_ZOOKEEPER = "zookeeper";
    private static final String OPTION_FILTER = "filter";
    private static final String OPTION_ENV_FILTER = "env-filterOperation";
    protected static FluxParserTest instance = null;
    protected StormTopology topology = null;

    protected static void runCli(CommandLine cmd) throws Exception {
        instance = new FluxParserTest();
        boolean dumpYaml = cmd.hasOption("dump-yaml");

        TopologyDef topologyDef = null;
        String filePath = (String) cmd.getArgList().get(0);

        // TODO conditionally load properties from a file our resource
        String filterProps = null;
        if (cmd.hasOption(OPTION_FILTER)) {
            filterProps = cmd.getOptionValue(OPTION_FILTER);
        }


        boolean envFilter = cmd.hasOption(OPTION_ENV_FILTER);
        if (cmd.hasOption(OPTION_RESOURCE)) {
            printf("Parsing classpath resource: %s", filePath);
            topologyDef = FluxParser.parseResource(filePath, dumpYaml, true, filterProps, envFilter);
        } else {
            printf("Parsing file: %s",
                    new File(filePath).getAbsolutePath());
            topologyDef = FluxParser.parseFile(filePath, dumpYaml, true, filterProps, envFilter);
        }


        String topologyName = topologyDef.getName();
        // merge contents of `config` into topology config
        Config conf = FluxBuilder.buildConfig(topologyDef);
        ExecutionContext context = new ExecutionContext(topologyDef, conf);
        StormTopology topology = FluxBuilder.buildTopology(context);
        topology.validate();
        if (!cmd.hasOption(OPTION_NO_DETAIL)) {
            printTopologyInfo(context);
        }
    }

    public StormTopology getTopology() {
        return topology;
    }

}
