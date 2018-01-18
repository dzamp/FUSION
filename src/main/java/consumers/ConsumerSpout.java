//package consumers;
//
//import actions.Action;
//import actions.SpoutAction;
//import exceptions.EmissionException;
//import org.apache.log4j.Logger;
//import org.apache.storm.spout.SpoutOutputCollector;
//import org.apache.storm.task.TopologyContext;
//import org.apache.storm.topology.IRichSpout;
//import org.apache.storm.topology.OutputFieldsDeclarer;
//import org.apache.storm.tuple.Fields;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public abstract class ConsumerSpout implements IRichSpout {
//    protected String brokerUrl;
//    protected String clientId;
//    protected String topic;
//    protected SpoutOutputCollector collector;
//    protected Map configMap;
//    protected TopologyContext ctx;
//    protected String regex = null;
//    protected Class[] classMap = null;
//    protected List<SpoutAction> emitActions = null;
//    protected Logger log ;
//
//    public ConsumerSpout(String brokerUrl, String clientId, String topic) {
//        this.brokerUrl = brokerUrl;
//        this.clientId = clientId;
//        this.topic = topic;
//        this.emitActions = new ArrayList<>();
//    }
//
//    @Override
//    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
//        this.collector = collector;
//        this.configMap = conf;
//        this.ctx = context;
//
//    }
//
//    protected void checkActions() {
//        if (emitActions.size() == 1) {
//            SpoutAction action = emitActions.get(0);
//            if (action.getStreamId() != null) {
//                log.info("Default stream forwarding");
//            }
//        } else {
//          for(SpoutAction ac: emitActions){
//              if(ac.getStreamId()==null){
//                  log.error("ConsumerSpout has a default emition registered with many emitters. Only named stream emissions are allowed");
//                  try {
//                      throw new EmissionException("Emission Error, default emmition presend with named ones");
//                  } catch (EmissionException e) {
//                      e.printStackTrace();
//                  }
//              }
//          }
//        }
//    }
//
//    public void addEmitAction(SpoutAction action) {
//        this.emitActions.add(action);
//    }
//
//
//    @Override
//    public void declareOutputFields(OutputFieldsDeclarer declarer) {
//        //prepare the declarer
//        if (emitActions.size() == 1 && emitActions.get(0).getStreamId() == null)
//            declarer.declare(new Fields(emitActions.get(0).getEmittedFields()));
//        else
//            for (Action action : emitActions)
//                declarer.declareStream(action.getStreamId(), new Fields(action.getEmittedFields()));
//
//    }
//
//    @Override
//    public Map<String, Object> getComponentConfiguration() {
//        return null;
//    }
//
//
//    @Override
//    public void ack(Object msgId) {
//
//    }
//
//    @Override
//    public void fail(Object msgId) {
//
//    }
//
//}
