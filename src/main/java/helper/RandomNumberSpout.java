package helper;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class RandomNumberSpout extends BaseRichSpout {
    SpoutOutputCollector collector;
    Class clazz;
    Number maximumNumber;
    Object threshold;
    String[] emittedFields;
    int sleeptime;

    public RandomNumberSpout(String className, Number max_value, Object threshold, String[] emittedFields, int sleeptime) {
        this.sleeptime = sleeptime;
        try {
            this.clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (clazz.getSuperclass() == Number.class) {
            try {
                this.maximumNumber = (Number) clazz.getConstructors()[0].newInstance(max_value);
                this.threshold = clazz.getConstructors()[0].newInstance(threshold);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        this.emittedFields = emittedFields;

    }


    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;

    }

    @Override
    public void nextTuple() {
        Utils.sleep(this.sleeptime);
        Number random = (int)(Math.random() *  (int)maximumNumber) + 1;
        collector.emit(new Values(random,10));

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(this.emittedFields));
    }
}
