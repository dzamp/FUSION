package bayesian;


import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.IBayesInferer;
import org.eclipse.recommenders.jayes.inference.junctionTree.JunctionTreeAlgorithm;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Taken from
 */
public class JayesExampleTestCase {
    @Test
    public void TestExample(){
        BayesNet net = new BayesNet();
        BayesNode a = net.createNode("a");
        a.addOutcomes("true", "false");
        a.setProbabilities(0.2, 0.8);

        BayesNode b = net.createNode("b");
        b.addOutcomes("one", "two", "three");
        b.setParents(Arrays.asList(a));

        b.setProbabilities(
                0.1, 0.4, 0.5, // a == true
                0.3, 0.4, 0.3 // a == false
        );

        BayesNode c = net.createNode("c");
        c.addOutcomes("true", "false");
        c.setParents(Arrays.asList(a, b));
        c.setProbabilities(
                // a == true
                0.1, 0.9, // b == one
                0.0, 1.0, // b == two
                0.5, 0.5, // b == three
                // a == false
                0.2, 0.8, // b == one
                0.0, 1.0, // b == two
                0.7, 0.3 // b == three
        );
        IBayesInferer inferer = new JunctionTreeAlgorithm();
        inferer.setNetwork(net);

        Map<BayesNode,String> evidence = new HashMap<BayesNode,String>();
        evidence.put(a, "true");
        evidence.put(b, "one");
        inferer.setEvidence(evidence);

        double[] beliefsC = inferer.getBeliefs(c);
        System.out.println("P(c | a = true, b = one )= " + beliefsC[0] + " " + beliefsC[1] );
    }
}
