package algorithms;

import abstraction.IAlgorithm;
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

import java.io.Serializable;
import java.util.*;


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
public class BayesianNetwork implements IAlgorithm, Serializable {
    protected Logger log;
    protected BayesNet bayesNet;
    protected IBayesInferer inferer;
    protected BayesNode beliefNode;
    protected Map<String, List<String>> nodeWithOutcomesMap;
    protected Map<String, List<String>> parentsMap;
    protected Map<String, List<Values>> streamValues;
    protected Map<String, List<String>> streamFieldsMap;
    protected Map<String, List<String>> inputFieldsFromSources;
    protected Map<String, BayesNode> streamToNodeMap;
    protected Map<String, String> nodeWithProbabilitiesMap;
    protected BayesianInferrenceAlgorithm inferenceAlgorithm;
    protected String inferenceNodeName;

    public BayesianNetwork() {
        nodeWithProbabilitiesMap = new HashMap<>();
        parentsMap = new HashMap<>();
        nodeWithOutcomesMap = new HashMap<>();
        streamValues = new HashMap<>();
        streamFieldsMap = new HashMap<>();
        streamToNodeMap = new HashMap<>();
    }

    private static void setInference() {

    }

    public BayesianNetwork withInference(String inferenceNodeName, BayesianInferrenceAlgorithm inferenceAlgorithm) {
        this.inferenceAlgorithm = inferenceAlgorithm;
        this.inferenceNodeName = inferenceNodeName;
        //remove
        return this;
    }

    /**
     * Creates a new BayesNode with the name given, and adds the outcomes specified
     *
     * @param name     The name of the BayesNode that will be created
     * @param outcomes The outcomes of the node created
     */
    public BayesianNetwork addNodeWithOutcomes(String name, String... outcomes) {
        this.nodeWithOutcomesMap.put(name, Arrays.asList(outcomes));

        //remove


        return this;
    }

    /**
     * Adds parents to the node specified by its node
     * In case the probability table of the nodeName has been already defined. This is discouraged from the
     * documentation of Jayes
     *
     * @param nodeName       the name of the node that will have those parents
     * @param parentsTobeSet The list of parents that will be added. These nodes must be already created
     */
    public BayesianNetwork addParentsToNode(String nodeName, String... parentsTobeSet) {
        parentsMap.put(nodeName, Arrays.asList(parentsTobeSet));
        //remove
        return this;
    }

    /**
     * Set the CPT table of the node
     *
     * @param nodeName      the name of the node
     * @param probabilities a string representing with line delimeter = | and value delimeter = ,
     */
    public BayesianNetwork setProbabilities(String nodeName, String probabilities) {
        this.nodeWithProbabilitiesMap.put(nodeName, probabilities);
        return this;
    }

    public BayesianNetwork build() throws AlgorithmDeclarationException {
        //TODO maybe build a more complex validation
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

    //todo check this
    public BayesianNetwork setStreamToNodeMap(String streamName, String nodeName) {
        if (streamToNodeMap == null) streamToNodeMap = new HashMap<>();
        BayesNode node = bayesNet.getNode(nodeName);
        streamToNodeMap.put(streamName, node);
        return this;
    }

    //todo check this
    private void resolveStreamToNodes(Map<String, List<String>> streamFieldsMap) {
        //assume same name policy if no mapping has been defined
        if (streamToNodeMap != null && streamToNodeMap.isEmpty()) {
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

        //classify the incoming tuples
        //Ka8e tuple pou 8a erxetai 8a prepei na perilamanei dedomena apo ta evidence nodes, dld ta streams
        //an kapoio evidence leiepei 8a prepei na paralhf8ei h na krath8ei kapoia prohgoumenh morfh tou?
        //Epishs 8a prepei na ginei kapoio classification sta dedomena apo to inferrence(px gia 8ermokrasia ti shmainei high, low, medium?) ktl
        Map<String, String> classification = (Map<String, String>) tuple.getValue(0);
        Map<BayesNode, String> evidence = new HashMap<>();
        if(!classification.isEmpty()) {
            for (String stream : classification.keySet()) {

                evidence.put(bayesNet.getNode(stream), classification.get(stream));
                System.out.println(stream + " ---> " + classification.get(stream) );
            }
            if (!evidence.isEmpty())
                inferer.setEvidence(evidence);
            double[] beliefsC = inferer.getBeliefs(bayesNet.getNode(inferenceNodeName));

            printBelief(beliefsC, classification);
            //TODO edw prepei na exw 8esei ta evidence kai na kanw inferrece
            //TODO ti action 8a lavoume gnk? stelnw kapou report? grafw se vash? kanw http post?
        }
        return null;
    }


    private void printBelief(double[] beliefs, Map<String, String> classification) {
        StringBuilder builder = new StringBuilder("P(" + this.inferenceNodeName + "|");
        for (String node : classification.keySet()) {
            builder.append(node + " = " + classification.get(node) + ", ");
        }
        builder.append(") = ").append(beliefs[0]).append(" " + beliefs[1]);
        System.out.println(builder.toString());

    }

    @Override
    public void setInputSources(Map<String, List<String>> inputFieldsFromSources) {
        this.inputFieldsFromSources = inputFieldsFromSources;
        resolveStreamToNodes(inputFieldsFromSources);
        try {
            validateNetwork();
        } catch (AlgorithmDeclarationException e) {
            log.error("Bayesian network is invalid");
            e.printStackTrace();
        }
    }

    @Override
    public void prepare() {
        this.log = Logger.getLogger(this.getClass());
        this.bayesNet = new BayesNet();
        //setOutcomes
        this.nodeWithOutcomesMap.forEach((node, stringList) ->
                bayesNet.createNode(node).addOutcomes(stringList.toArray(new String[stringList.size()]))
        );
        //setParents
        this.parentsMap.forEach((node, parentStringList) -> {
            List<BayesNode> parents = new ArrayList<>();
            for (String parentName : parentStringList)
                parents.add(bayesNet.getNode(parentName));
            if (parents != null && parents.size() != 0)
                bayesNet.getNode(node).setParents(parents);
        });
        //setProbabilities
        this.nodeWithProbabilitiesMap.forEach((nodeName, probabilities) -> {
                    String valueRegex = ",";
                    ArrayList<Double> probabilityArray = new ArrayList<>();
                    String[] values = probabilities.split(valueRegex);
                    for (String value : values) probabilityArray.add(Double.valueOf(value));
                    Double[] probArray = probabilityArray.toArray(new Double[0]);
                    double[] probs = ArrayUtils.toPrimitive(probArray);
                    bayesNet.getNode(nodeName).setProbabilities(probs);
                }
        );
        //setInference
        setInference(this.inferenceNodeName, inferenceAlgorithm);


    }

    private void setInference(String inferenceNodeName, BayesianInferrenceAlgorithm inferenceAlgorithm) {
        switch (inferenceAlgorithm) {
            case JUNCTION_TREE:
                inferer = new JunctionTreeAlgorithm();
                break;
            case REJECTION_SAMPLING:
                inferer = new RejectionSampling();
                break;
            case LIKELIHOOD_WEIGHTED_SAMPLING:
                inferer = new LikelihoodWeightedSampling();
                break;
            default:
                inferer = new JunctionTreeAlgorithm();

        }
        log.info("Bayesian network using inference algorithm : " + inferer.getClass().getCanonicalName());
        inferer.setNetwork(bayesNet);
        beliefNode = bayesNet.getNode(inferenceNodeName);
    }

//    @Override
//    public void setInputSources(Map<String, Map<String, List<String>>> inputFieldsFromSources) {
//        this.inputFieldsFromSources = inputFieldsFromSources;
//        inputFieldsFromSources = new HashMap<>();
//        for (String stream : this.inputFieldsFromSources.keySet()) {
//            List<String> fields = this.inputFieldsFromSources.get(stream).get("default");
//            inputFieldsFromSources.put(stream, new ArrayList<>(fields));
//        }
//    }

    @Override
    public String[] transformFields(String[] incomingFields) {
        //todo wtf
        return incomingFields;
    }

    public enum BayesianInferrenceAlgorithm {
        JUNCTION_TREE,
        REJECTION_SAMPLING,
        LIKELIHOOD_WEIGHTED_SAMPLING
    }

}
