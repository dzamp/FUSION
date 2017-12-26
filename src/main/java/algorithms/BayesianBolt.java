package algorithms;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.IBayesInferer;
import org.eclipse.recommenders.jayes.util.Graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BayesianBolt extends GenericBolt {

    private BayesNet bayesianNetwork;
    private String inferrenceNode;
    private IBayesInferer inferer;
    private HashMap<BayesNode,String> evidence;



    //this algorithm needs as input an evidence map.
    //How will this be achieved?
    //One solution is that the bayesian network will tuples ONLY from one Bolt/Spout containing a sequence of evidence, like temperature=high, etc.
    //After that we will be feeding it to the system
    
    public BayesianBolt(BayesNet net, IBayesInferer inferer, HashMap<BayesNode,String> evidence, String inferrenceNode) {
        this.bayesianNetwork = net;
        this.inferrenceNode = inferrenceNode;
        this.inferer = inferer;
        this.evidence = evidence;
    }


    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        super.prepare(map, topologyContext, outputCollector);
        this.inferer.setNetwork(bayesianNetwork);
    }

    @Override
    public void execute(Tuple tuple) {
        //Steps to setup the bayesian network
        //1. inferer sets the network
        //2. A hashmap with evidence of Bayesian -> String is being set;
        //3. This hashmap is being put on the inferer
        //4. then you get your beliefs


        this.inferer.setEvidence(this.evidence);
    }


}
