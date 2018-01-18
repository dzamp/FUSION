//package actions;
//
//import exceptions.FieldsMismatchException;
//import org.apache.storm.task.OutputCollector;
//import org.apache.storm.tuple.Values;
//import org.apache.storm.utils.Time;
//
//import java.io.FileNotFoundException;
//import java.io.PrintWriter;
//import java.io.UnsupportedEncodingException;
//
//public class FilePrinter implements BoltAction {
//    PrintWriter writer;
//
//    public FilePrinter(String filename) {
//        try {
//            writer = new PrintWriter("joinBoltTest" + Time.currentTimeMillis(), "UTF-8");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void execute(OutputCollector collector, String streamId, Values values) throws FieldsMismatchException {
////        writer.println("Current value: " + value + " mean: " + curr_mean + " variance: "
////                + curr_Variance + " UCL: " + UCL + " LCL: " + LCL);
//    }
//
//    @Override
//    public String getStreamId() {
//        return null;
//    }
//
//    @Override
//    public String[] getEmittedFields() {
//        return new String[0];
//    }
//}
