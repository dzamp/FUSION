package abstraction;

import exceptions.AlgorithmDeclarationException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.IBayesInferer;
import org.eclipse.recommenders.jayes.inference.LikelihoodWeightedSampling;
import org.eclipse.recommenders.jayes.inference.RejectionSampling;
import org.eclipse.recommenders.jayes.inference.junctionTree.JunctionTreeAlgorithm;
import scala.actors.threadpool.Arrays;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * To Bayesian network 8a parei opws k na xei inputs apo diafora sources.
 * Epomenws to pio suneto 8a htan na dexetai ena hashmap pou ka8e stream 8a exei ena h parapanw values
 * Den 8a 3eroume apo poion prohgoumeno algori8mo 8a erxetai to stream alla 8a prepei na kanoume kapoio inference.
 * An exoune te8ei oi CPT pinakes kai 3eroume pou 8eloume na kanoume inference eimaste ok
 * Pws wstoso 8a diaxeiristoume thn roh pou erxetai ama dn 3eroume poio 8a einai to input?
 * An 8a erxetai apo Shewhart/cumsum h kati se voting
 * Documentation for this algorithm
 * <a> http://www.eclipse.org/recommenders/jayes </a>
 */
public class BayesianNetwork implements IAlgorithm, Serializable{

    protected Logger log = Logger.getLogger(this.getClass());
    protected BayesNet bayesNet;
    protected Map<String, List<String>> networkMap;
    protected IBayesInferer inferer = new JunctionTreeAlgorithm();
    protected BayesNode beliefNode;
    protected Map<String, List<Values>> streamValues;
    protected Map<String, List<String>> streamFieldsMap;
    protected Map<String, BayesNode> streamToNodeMap;

    public BayesianNetwork() {
        bayesNet = new BayesNet();
        networkMap = new HashMap<>();
    }

    public BayesianNetwork withInference(String inferenceNodeName, String inferenceAlgorithm) {
        switch (inferenceAlgorithm) {
            case "JunctionTreeAlgorithm":
                inferer = new JunctionTreeAlgorithm();
                break;
            case "RejectionSampling":
                inferer = new RejectionSampling();
                break;
            case "LikelihoodWeightedSampling":
                inferer = new LikelihoodWeightedSampling();
                break;
            default:
                inferer = new JunctionTreeAlgorithm();

        }
        log.info("Bayesian network using inference algorithm : " + inferer.getClass().getCanonicalName());
        inferer.setNetwork(bayesNet);
        beliefNode = bayesNet.getNode(inferenceNodeName);
        return this;
    }


    /**
     * Creates a new BayesNode with the name given, and adds the outcomes specified
     *
     * @param name     The name of the BayesNode that will be created
     * @param outcomes The outcomes of the node created
     */
    public BayesianNetwork addNodeWithOutcomes(String name, String... outcomes) {
        networkMap.putIfAbsent(name, new ArrayList<>(Arrays.asList(outcomes)));
        bayesNet.createNode(name).addOutcomes(outcomes);
        return this;
    }

    /**
     * Adds parents to the node specified by its node
     * In case the probability table of the nodeName has been already defined. This is discouraged from the
     *                                       documentation of Jayes
     * @param nodeName       the name of the node that will have those parents
     * @param parentsTobeSet The list of parents that will be added. These nodes must be already created
     */
    public BayesianNetwork addParentsToNode(String nodeName, String... parentsTobeSet) {
        BayesNode node = bayesNet.getNode(nodeName);
        double[] probs = node.getProbabilities();
//        if (node.getProbabilities().length > 0) {
//            log.error("Ensure that parent node is being set BEFORE adding probabilities to this node");
//            throw new AlgorithmDeclarationException("Ensure that parent node is being set BEFORE adding probabilities to this node");
//        }
        List<BayesNode> parents = new ArrayList<>();
        for (String parent : parentsTobeSet) {
            parents.add(bayesNet.getNode(parent));
        }

        node.setParents(parents);
        return this;
    }


    /**
     * Set the CPT table of the node
     *
     * @param nodeName      the name of the node
     * @param probabilities a string representing with line delimeter = | and value delimeter = ,
     */
    public BayesianNetwork setProbabilities(String nodeName, String probabilities) {
        BayesNode node = bayesNet.getNode(nodeName);
        String valueRegex = ",";
        ArrayList<Double> probabilityArray = new ArrayList<>();
        String[] values = probabilities.split(valueRegex);
        for (String value : values) probabilityArray.add(Double.valueOf(value));

        Double[] probArray = probabilityArray.toArray(new Double[0]);
        double[] probs = ArrayUtils.toPrimitive(probArray);
        node.setProbabilities(probs);
        return this;
    }

    public BayesianNetwork build() throws AlgorithmDeclarationException {
        //TODO maybe build a more complex validation

        resolveStreamToNodes(streamFieldsMap);
        validateNetwork();
        return this;
    }

    private void validateNetwork() throws AlgorithmDeclarationException {
        for (BayesNode node : bayesNet.getNodes()) {
            if (node.getProbabilities().length == 0) {
                log.error("Node " + node.getName() + " has not a CPT configured");
                throw new AlgorithmDeclarationException("Bayesian network instantiation error");
            }
        }
    }

    public BayesianNetwork setStreamToNodeMap(String streamName, String nodeName) {
        if (streamToNodeMap == null) streamToNodeMap = new HashMap<>();
        BayesNode node = bayesNet.getNode(nodeName);
        streamToNodeMap.put(streamName, node);
        return this;
    }

    private void resolveStreamToNodes(Map<String, List<String>> streamFieldsMap) {
        //assume same name policy if no mapping has been defined
        if (streamToNodeMap == null) {
            log.info("Assuming same stream to bayesian node name policy ");
            for (String stream : streamFieldsMap.keySet()) {
                for (BayesNode node : bayesNet.getNodes()) {
                    if (stream.toLowerCase().contains(node.getName().toLowerCase())) streamToNodeMap.put(stream, node);
                }
            }
        }

        //else there is a policy
    }

    @Override
    public Values executeAlgorithm(Tuple tuple) {
        //TODO edw uparxei to erwthma ti morfhs 8a einai to input tuple.
        //TODO 8a einai profanws values apo polla streams epomenws 8a prepei na anagnwrisoume ka8e stream se poio network node pou anhkei
        //TODO
        Map<BayesNode, String> evidence = new HashMap<>();

        //classify the incoming tuples
        //Ka8e tuple pou 8a erxetai 8a prepei na perilamanei dedomena apo ta evidence nodes, dld ta streams
        //an kapoio evidence leiepei 8a prepei na paralhf8ei h na krath8ei kapoia prohgoumenh morfh tou?
        //Epishs 8a prepei na ginei kapoio classification sta dedomena apo to inferrence(px gia 8ermokrasia ti shmainei high, low, medium?) ktl
        streamValues = (Map<String, List<Values>>) tuple.getValues().get(0);
        streamFieldsMap = (Map<String, List<String>>) tuple.getValues().get(1);
        for (String stream : streamValues.keySet()) {
            //TODO classify the values according to the outcomes!
            //TODO An dn einai o shewhart??? kai einai o QUMSUM? ti ginetai an einai kapoios allos?
            //TODO edw prepei na kanw ena classification ths timhs/timwn pou dexomai se poio outcome anhkoun kai sthn sunexeia na to kanw set ws evidence
        }
        System.out.println("eheeee");
        //TODO edw prepei na exw 8esei ta evidence kai na kanw inferrece
        //TODO ti action 8a lavoume gnk? stelnw kapou report? grafw se vash? kanw http post?
        return null;
    }

    @Override
    public String[] getExtraFields() {
        return null;
    }


}
