package parser;

import org.apache.commons.cli.CommandLine;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestMqttSpouts {

    @Test
    public void testMqttTopology(){
        CommandLine cmd = mock(CommandLine.class,RETURNS_DEEP_STUBS);
        when(cmd.getArgList().get(0)).thenReturn(new String("src/test/resources/mqtt-consumer/cusum.yaml"));
        when(cmd.hasOption("filter")).thenReturn(false);
        when(cmd.hasOption("resource")).thenReturn(false);
        try {
            FluxParserTest.runCli(cmd);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            fail("Exception in creating topology");
        }
        System.out.println("Topology parsed successfully");
    }

    @Test
    public void testMqttShewhartSingleTopology(){
        CommandLine cmd = mock(CommandLine.class,RETURNS_DEEP_STUBS);
        when(cmd.getArgList().get(0)).thenReturn(new String("src/test/resources/mqtt-consumer/extended/mqtt-shewhart-single.yaml"));
        when(cmd.hasOption("filter")).thenReturn(false);
        when(cmd.hasOption("resource")).thenReturn(false);
        try {
            FluxParserTest.runCli(cmd);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            fail("Exception in creating topology");
        }
        System.out.println("Topology parsed successfully");
    }


    @Test
    public void testMqttShewhartTubmle(){
        CommandLine cmd = mock(CommandLine.class,RETURNS_DEEP_STUBS);
        when(cmd.getArgList().get(0)).thenReturn(new String("src/test/resources/mqtt-consumer/extended/mqtt-shewhart-tumble.yaml"));
        when(cmd.hasOption("filter")).thenReturn(false);
        when(cmd.hasOption("resource")).thenReturn(false);
        try {
            FluxParserTest.runCli(cmd);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            fail("Exception in creating topology");
        }
        System.out.println("Topology parsed successfully");
    }

}
