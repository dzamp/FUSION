//package jayes;
import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.IBayesInferer;
import org.eclipse.recommenders.jayes.inference.LikelihoodWeightedSampling;
import org.eclipse.recommenders.jayes.inference.RejectionSampling;
import org.eclipse.recommenders.jayes.inference.junctionTree.JunctionTreeAlgorithm;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


public class JayesNetTestCase {
    public BayesNet bayesianNetwork;
    public BayesNode temperature;
    public BayesNode humidity;
    public BayesNode fireIncident;

    @Before
    public void prepareBayesianNet(){

        bayesianNetwork = new BayesNet();
        temperature =bayesianNetwork.createNode("temperature");
        temperature.addOutcomes("high", "medium", "low");
        temperature.setProbabilities(0.6,0.3,0.1);

        humidity = bayesianNetwork.createNode("humidity");
        humidity.addOutcomes("high", "medium","low");
        humidity.setProbabilities(0.1,0.3,0.6);

        fireIncident = bayesianNetwork.createNode("fireIncident");
        fireIncident.addOutcomes("fire", "not-fire");
        fireIncident.setParents(Arrays.asList(temperature,humidity));
        fireIncident.setProbabilities(
                //temperature == high
                0.2,0.8,    //p(fire| temp= high, hum=high), p(fire|temp= high,hum=~high)
                0.3,0.7,   //p(fire| temp = high, hum=medium), p(fire|temp = high,hum=~medium)
                0.5,0.5,     //p(fire|temp = high,hum=low), p(fire|temp = high,hum=~low)
                //temperature= medium
                0.1,0.9,    //p(fire|temp = medium,hum=high), p(fire|temp = medium,hum=~high)
                0.3,0.7,    //p(fire|temp = medium,hum=medium), p(fire|temp = medium,hum=~medium)
                0.3,0.7,    //p(fire|temp = medium,hum=low), p(fire|temp = medium,hum=~low)
                //temperature = low
                0.1,0.9,  //p(fire|temp = low,hum=high), p(fire|temp = low,hum=~high)
                0.2,0.8,    //p(fire|temp = low,hum=medium), p(fire|temp = low,hum=~medium)
                0.2,0.8     //p(fire|temp = low,hum=low), p(fire|temp = low,hum=~low)

        );

    }

    @Test
    public void testExampleJayesNetUsingJunctionTreeAlgorithm() {
        IBayesInferer inferer = new JunctionTreeAlgorithm();
        inferer.setNetwork(bayesianNetwork);
        Map<BayesNode,String> evidence = new HashMap<BayesNode,String>();
        evidence.put(fireIncident, "not-fire");
        evidence.put(humidity,"medium");
//        evidence.put(b, "three");
        inferer.setEvidence(evidence);

        double[] beliefsC = inferer.getBeliefs(temperature);

        System.out.println("\nJunctionTreeAlgorithm reports " +
                "\nP(temperature=high|humidity=medium, fireIncident=not-fire) = " + beliefsC[0] +
                "\nP(temperature=medium|humidity=medium, fireIncident=not-fire) = " + beliefsC[1] +
                "\nP(temperature=high|humidity=low, fireIncident=not-fire) = " + beliefsC[2]
        );
    }

    @Test
    public void testExampleJayesNetUsingRejectionSampling() {

        IBayesInferer inferer = new RejectionSampling();
        inferer.setNetwork(bayesianNetwork);
        Map<BayesNode,String> evidence = new HashMap<BayesNode,String>();
        evidence.put(fireIncident, "not-fire");
        evidence.put(humidity,"medium");
//        evidence.put(b, "three");
        inferer.setEvidence(evidence);

        double[] beliefsC = inferer.getBeliefs(temperature);
        System.out.println("\nRejectionSampling reports " +
                "\nP(temperature=high|humidity=medium, fireIncident=not-fire) = " + beliefsC[0] +
                "\nP(temperature=medium|humidity=medium, fireIncident=not-fire) = " + beliefsC[1] +
                "\nP(temperature=high|humidity=low, fireIncident=not-fire) = " + beliefsC[2]
        );

        System.out.println();
//        assertEquals(1,1);
    }

    @Test
    public void testExampleJayesNetUsingLikelihoodWeightedSampling() {

        IBayesInferer inferer = new LikelihoodWeightedSampling();
        inferer.setNetwork(bayesianNetwork);
        Map<BayesNode,String> evidence = new HashMap<BayesNode,String>();
        evidence.put(fireIncident, "not-fire");
        evidence.put(humidity,"medium");
//        evidence.put(b, "three");
        inferer.setEvidence(evidence);

        double[] beliefsC = inferer.getBeliefs(temperature);

        System.out.println("\nLikelihoodWeightedSampling reports " +
                "\nP(temperature=high|humidity=medium, fireIncident=not-fire) = " + beliefsC[0] +
                "\nP(temperature=medium|humidity=medium, fireIncident=not-fire) = " + beliefsC[1] +
                "\nP(temperature=high|humidity=low, fireIncident=not-fire) = " + beliefsC[2]
        );
    }

}
