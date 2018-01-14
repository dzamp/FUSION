package bayesian;

import abstraction.BayesianNetwork;
import exceptions.AlgorithmDeclarationException;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.junit.Test;

import java.util.*;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by jim on 3/1/2018.
 */
public class BayesianNetworkTestCase  {

    @Test
    public void testBayesianAlgorithm(){
        BayesianNetwork bayesianNetwork = null;
        try {
            bayesianNetwork = new BayesianNetwork()
                    .addNodeWithOutcomes("temperature", "low","medium", "high").setProbabilities("temperature","0.2,0.4,0.4")
                    .addNodeWithOutcomes("humidity","low","medium", "high").setProbabilities("humidity","0.1,0.4,0.5")
                    .addNodeWithOutcomes("fire","low","high").addParentsToNode("fire","temperature","humidity").setProbabilities("fire",       //temperature == high
                            "0.2,0.8," +    //p(fire| temp= high, hum=high), p(not-fire|temp= high,hum=high)
                            "0.3,0.7," +   //p(fire| temp = high, hum=medium), p(not-fire |temp = high,hum=medium)
                            "0.5,0.5," +    //p(fire|temp = high,hum=low), p(not-fire|temp = high,hum=low)
                          //temperature= medium
                            "0.1,0.9,"+    //p(fire|temp = medium,hum=high), p(not-fire|temp = medium,hum=high)
                            "0.3,0.7," +   //p(fire|temp = medium,hum=medium), p(not-fire|temp = medium,hum=medium)
                            "0.3,0.7,"+    //p(fire|temp = medium,hum=low), p(not-fire|temp = medium,hum=low)
                          //temperature = low
                            "0.1,0.9," +  //p(fire|temp = low,hum=high), p(not-fire|temp = low,hum=high)
                            "0.2,0.8," +    //p(fire|temp = low,hum=medium), p(not-fire|temp = low,hum=medium)
                            "0.2,0.8,"     //p(fire|temp = low,hum=low), p(not-fire|temp = low,hum=low))
                    ).withInference("fire","").setStreamToNodeMap("temperature-spout","temperature").setStreamToNodeMap("humidity-spout", "humidity").build();
        } catch (AlgorithmDeclarationException e) {
            e.printStackTrace();
        }
//            bayesianNetwork.build()
            Tuple tuple = mock(Tuple.class,RETURNS_DEEP_STUBS);
            when(tuple.getValues().get(0)).thenAnswer(invocationOnMock -> {
                Map<String, List<Values>> streamValues = new HashMap<String, List<Values>>();
                streamValues.put("temperature-spout",new ArrayList<>());
                streamValues.put("humidity-spout",new ArrayList<>());
                for(int i=0; i< 20; i++) {
                    if(i%2==0)
                        streamValues.get("temperature-spout")
                                .add(new Values("T" + UUID.randomUUID(), Math.random() * 10 + 30, System.currentTimeMillis() ));
                    else streamValues.get("humidity-spout")
                            .add(new Values("T" + UUID.randomUUID(), Math.random() * 10 + 10, System.currentTimeMillis() ));
                }
                return streamValues;

            });


        when(tuple.getValues().get(1)).thenAnswer(invocationOnMock -> {
            Map<String, List<String>> streamFieldsMap = new HashMap<String, List<String>>();
            streamFieldsMap.put("temperature-spout",Arrays.asList("id","temperature","timestamp"));
            streamFieldsMap.put("humidity-spout",Arrays.asList("id","humidity","timestamp"));
            return streamFieldsMap;
        });

            bayesianNetwork.executeAlgorithm(tuple);


    }




}
