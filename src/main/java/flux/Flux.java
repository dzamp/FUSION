/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package flux;

import flux.model.*;
import flux.parser.FluxParser;
import org.apache.commons.cli.*;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;

import org.apache.storm.generated.StormTopology;
import org.apache.storm.generated.SubmitOptions;
import org.apache.storm.generated.TopologyInitialStatus;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Flux entry point.
 *
 */
public class Flux {
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
    public static final Thread mainThread = Thread.currentThread();
    static volatile boolean keepRunning = true;

    public static void main(String[] args) throws Exception {
      Options options = createOptions();

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.getArgs().length != 1) {
            usage(options);
            System.exit(1);
        }
        runCli(cmd);
    }

    protected static Options createOptions(){
        Options options = new Options();

        options.addOption(option(0, "l", OPTION_LOCAL, "Run the topology in local mode."));

        options.addOption(option(0, "r", OPTION_REMOTE, "Deploy the topology to a remote cluster."));

        options.addOption(option(0, "R", OPTION_RESOURCE, "Treat the supplied path as a classpath resource instead of a file."));

        options.addOption(option(1, "s", OPTION_SLEEP, "ms", "When running locally, the amount of time to sleep (in ms.) " +
                "before killing the topology and shutting down the local cluster."));

        options.addOption(option(0, "d", OPTION_DRY_RUN, "Do not run or deploy the topology. Just build, validate, " +
                "and print information about the topology."));

        options.addOption(option(0, "q", OPTION_NO_DETAIL, "Suppress the printing of topology details."));

        options.addOption(option(0, "n", OPTION_NO_SPLASH, "Suppress the printing of the splash screen."));

        options.addOption(option(0, "i", OPTION_INACTIVE, "Deploy the topology, but do not activate it."));

        options.addOption(option(1, "z", OPTION_ZOOKEEPER, "host:port", "When running in local mode, use the ZooKeeper at the " +
                "specified <host>:<port> instead of the in-process ZooKeeper. (requires Storm 0.9.3 or later)"));

        options.addOption(option(1, "f", OPTION_FILTER, "file", "Perform property substitution. Use the specified file " +
                "as a source of properties, and replace keys identified with {$[property name]} with the value defined " +
                "in the properties file."));

        options.addOption(option(0, "e", OPTION_ENV_FILTER, "Perform environment variable substitution. Replace keys" +
                "identified with `${ENV-[NAME]}` will be replaced with the corresponding `NAME` environment value"));
        return options;
    }


    protected static Option option(int argCount, String shortName, String longName, String description){
       return option(argCount, shortName, longName, longName, description);
    }

    protected static Option option(int argCount, String shortName, String longName, String argName, String description){
        Option option = OptionBuilder.hasArgs(argCount)
                .withArgName(argName)
                .withLongOpt(longName)
                .withDescription(description)
                .create(shortName);
        return option;
    }

    protected static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("storm jar <my_topology_uber_jar.jar> " +
                Flux.class.getName() +
                " [options] <topology-config.yaml>", options);
    }

    protected static void runCli(CommandLine cmd) throws Exception {
        if(!cmd.hasOption(OPTION_NO_SPLASH)) {
            printSplash();
        }

        boolean dumpYaml = cmd.hasOption("dump-yaml");

        TopologyDef topologyDef = null;
        String filePath = (String)cmd.getArgList().get(0);

        // TODO conditionally load properties from a file our resource
        String filterProps = null;
        if(cmd.hasOption(OPTION_FILTER)){
            filterProps = cmd.getOptionValue(OPTION_FILTER);
        }


        boolean envFilter = cmd.hasOption(OPTION_ENV_FILTER);
        if(cmd.hasOption(OPTION_RESOURCE)){
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

        if(!cmd.hasOption(OPTION_NO_DETAIL)){
            printTopologyInfo(context);
        }

        if(!cmd.hasOption(OPTION_DRY_RUN)) {
            if (cmd.hasOption(OPTION_REMOTE)) {
                LOG.info("Running remotely...");
                // should the topology be active or inactive
                SubmitOptions submitOptions = null;
                if(cmd.hasOption(OPTION_INACTIVE)){
                    LOG.info("Deploying topology in an INACTIVE state...");
                    submitOptions = new SubmitOptions(TopologyInitialStatus.INACTIVE);
                } else {
                    LOG.info("Deploying topology in an ACTIVE state...");
                    submitOptions = new SubmitOptions(TopologyInitialStatus.ACTIVE);
                }
                StormSubmitter.submitTopology(topologyName, conf, topology, submitOptions, null);
            } else {
                LocalCluster cluster = new LocalCluster();
                // conf.getsetDebug(true);
                conf.registerMetricsConsumer(org.apache.storm.metric.LoggingMetricsConsumer.class, 10);
                cluster.submitTopology(topologyName, conf, topology);
                Utils.sleep(100000000);

                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        System.out.println("Shutdown--------------------------");
                        keepRunning = false;
                        try {
                            mainThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        cluster.killTopology(topologyName);
                        cluster.shutdown();
                    }
                });
            }
        }
    }

   protected static void printTopologyInfo(ExecutionContext ctx){
        TopologyDef t = ctx.getTopologyDef();
        if(t.isDslTopology()) {
            print("---------- TOPOLOGY DETAILS ----------");

            printf("Topology Name: %s", t.getName());
            print("--------------- SPOUTS ---------------");
            for (SpoutDef s : t.getSpouts()) {
                printf("%s [%d] (%s)", s.getId(), s.getParallelism(), s.getClassName());
            }
            print("---------------- BOLTS ---------------");
            for (BoltDef b : t.getBolts()) {
                printf("%s [%d] (%s)", b.getId(), b.getParallelism(), b.getClassName());
            }

            print("--------------- STREAMS ---------------");
            for (StreamDef sd : t.getStreams()) {
                printf("%s --%s--> %s", sd.getFrom(), sd.getGrouping().getType(), sd.getTo());
            }
            print("--------------------------------------");
        }
    }

    // save a little typing
    protected static void printf(String format, Object... args){
        print(String.format(format, args));
    }

    protected static void print(String string){
        System.out.println(string);
    }

    protected static void printSplash() throws IOException {
        // banner
        InputStream is = Flux.class.getResourceAsStream("/splash.txt");
        if(is != null){
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while((line = br.readLine()) != null){
                System.out.println(line);
            }
        }
    }
}
