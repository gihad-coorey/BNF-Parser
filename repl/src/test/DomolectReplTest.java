package test;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import repl.BNFParser;
import repl.Repl;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;



public class DomolectReplTest {

  private static BNFParser parser;
  private static Repl repl;
  private static final String DOMOLECT_BNF_FILE = "src/repl/domolect2-0.bnf";

  @BeforeClass
  public static void setUp() {
    parser = new BNFParser(DOMOLECT_BNF_FILE);
    repl = new Repl(parser);
  }

  @Test
  public void testReplStartWithThermalDeviceCommand() {
    String input = "living-room set thermostat to 6 9 9 K\n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    
    repl.start(inputStream, outputStream, "command");
    
    String output = outputStream.toString();
    assertTrue(output.contains(Repl.Response.SUCCESS.message));
  }

  @Test
  public void testReplStartWithBarrierCommand() {
    String input = "open curtains\n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    
    repl.start(inputStream, outputStream, "command");
    
    String output = outputStream.toString();
    assertTrue(output.contains(Repl.Response.SUCCESS.message));
  }

  @Test
  public void testReplStartWithApplianceCommand() {
    String input = "turn laser-cannon on\n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    
    repl.start(inputStream, outputStream, "command");
    
    String output = outputStream.toString();
    assertTrue(output.contains(Repl.Response.SUCCESS.message));
  }

  @Test
  public void testReplStartWithLightingCommand() {
    String input = "turn brazier off\n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    repl.start(inputStream, outputStream, "command");

    String output = outputStream.toString();
    assertTrue(output.contains(Repl.Response.SUCCESS.message));
  }
  
  @Test
  public void testReplStartWithInvalidCommand() {
    String input = "invalid command\n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    repl.start(inputStream, outputStream, "command");

    String output = outputStream.toString();
    assertTrue(output.contains(Repl.Response.UNKNOWN_COMMAND.message));
  }

  @Test
  public void testReplStartWithExitCommand() {
    String input = "exit\n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    repl.start(inputStream, outputStream, "command");

    String output = outputStream.toString();
    assertTrue(output.contains(Repl.Response.EXIT.message));
  }

  @Test
  public void testReplStartWithWhenTempCondition() {
    String input = "kitchen set incinerator to 6 9 K when current-temperature less-than 6 9 K\n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    
    repl.start(inputStream, outputStream, "augmented_command");
    
    String output = outputStream.toString();
    assertTrue(output.contains(Repl.Response.SUCCESS.message));
  }

  @Test
  public void testReplStartWithUntilTempCondition() {
    String input = "kitchen set incinerator to 6 9 K until current-temperature equal-to 6 9 K\n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    
    repl.start(inputStream, outputStream, "augmented_command");
    
    String output = outputStream.toString();
    assertTrue(output.contains(Repl.Response.SUCCESS.message));
  }

  @Test
  public void testReplStartWithWhenUntilTimeCondition() {
    String input = "kitchen set incinerator to 6 9 K when 1 2 : 3 4 am until 0 7 : 5 6 pm\n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    
    repl.start(inputStream, outputStream, "augmented_command");
    
    String output = outputStream.toString();
    assertTrue(output.contains(Repl.Response.SUCCESS.message));
  }

  @Test
  public void testReplStartWithWhenTempUntilTimeCondition() {
    String input = "kitchen set incinerator to 6 9 K when current-temperature greater-than 6 9 K until 0 9 : 0 8 am\n";
    InputStream inputStream = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    
    repl.start(inputStream, outputStream, "augmented_command");
    
    String output = outputStream.toString();
    assertTrue(output.contains(Repl.Response.SUCCESS.message));
  }

}
