package parser;

import flux.Flux;
import flux.parser.FluxParser;
import org.apache.commons.cli.CommandLine;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestExampleTopologies {

    @Test
    public void testTopology(){
        CommandLine cmd = mock(CommandLine.class,RETURNS_DEEP_STUBS);
        when(cmd.getArgList().get(0)).thenReturn(new String("src/test/resources/example-topology/topology.yaml"));
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
//        FluxParserTest.startTopology();
    }

    @Test
    public void testTopology2(){
        CommandLine cmd = mock(CommandLine.class,RETURNS_DEEP_STUBS);
        when(cmd.getArgList().get(0)).thenReturn(new String("src/test/resources/example-topology/topology2.yaml"));
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
    public void testTopology3(){
        CommandLine cmd = mock(CommandLine.class,RETURNS_DEEP_STUBS);
        when(cmd.getArgList().get(0)).thenReturn(new String("src/test/resources/example-topology/topology3.yaml"));
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
