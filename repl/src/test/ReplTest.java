package test;

import org.junit.Before;
import org.junit.Test;
import repl.Repl;
import repl.BNFParser;

import static org.junit.Assert.*;

import java.io.*;

/**
 * Unit tests for the {@link Repl} class, ensuring correct REPL functionality.
 * This test suite uses a simple mock for the {@link BNFParser} to isolate the
 * REPL logic from the parser.
 */
public class ReplTest {

  private BNFParser mockParser;
  private Repl repl;
  private ByteArrayOutputStream outputStream;
  private InputStream inputStream;

  private class MockBNFParser extends BNFParser {
    private boolean validSentence;

    public void setValidSentence(boolean validSentence) {
      this.validSentence = validSentence;
    }

    @Override
    public boolean isValidSentence(String input, String ruleName) {
      return validSentence;
    }
  }

  /**
   * Sets up the test environment by initializing the mock parser, the REPL
   * instance, and the output stream for capturing REPL output.
   */
  @Before
  public void setUp() {
    mockParser = new MockBNFParser();
    repl = new Repl(mockParser);
    outputStream = new ByteArrayOutputStream();
  }

  /**
   * Tests that the REPL starts, prints the welcome message, and correctly
   * exits when the 'exit' command is entered.
   *
   * @throws IOException If an input or output stream error occurs.
   */
  @Test
  public void testStartAndExit() throws IOException {
    inputStream = new ByteArrayInputStream("exit\n".getBytes());

    repl.start(inputStream, outputStream, "mockRule");

    String output = outputStream.toString();
    assertTrue(output.contains("Welcome to the Domolect 2.0 parser!"));
    assertTrue(output.contains("Enter a command or type `exit` to quit."));
    assertTrue(output.contains(">>> "));
    assertTrue(output.contains("Exiting REPL..."));

    assertFalse(repl.isRunning());
  }

  /**
   * Tests that starting the REPL with null input and output streams throws
   * an {@link IllegalArgumentException}.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testStartWithNullStreamsThrowsException() {
    repl.start(null, null, "mockRule");
  }

  /**
   * Tests that the REPL recognizes a valid input command and returns the
   * success message.
   *
   * @throws IOException If an input or output stream error occurs.
   */
  @Test
  public void testValidCommand() throws IOException {
    ((MockBNFParser) mockParser).setValidSentence(true);

    inputStream = new ByteArrayInputStream("valid input\nexit\n".getBytes());

    repl.start(inputStream, outputStream, "mockRule");

    String output = outputStream.toString();
    assertTrue(output.contains(Repl.Response.SUCCESS.message));
  }

  /**
   * Tests that the REPL returns the 'unknown command' message for invalid input.
   *
   * @throws IOException If an input or output stream error occurs.
   */
  @Test
  public void testUnknownCommand() throws IOException {
    ((MockBNFParser) mockParser).setValidSentence(false);

    inputStream = new ByteArrayInputStream("invalid input\nexit\n".getBytes());

    repl.start(inputStream, outputStream, "mockRule");

    String output = outputStream.toString();
    assertTrue(output.contains(Repl.Response.UNKNOWN_COMMAND.message));
  }

  /**
   * Tests that the 'exit' command directly returns the {@link Repl.Response#EXIT}
   * response.
   */
  @Test
  public void testEvaluateInputExit() {
    Repl.Response response = repl.evaluateInput("exit", "mockRule");
    assertEquals(Repl.Response.EXIT, response);
  }

  /**
   * Tests that a valid input sentence returns the {@link Repl.Response#SUCCESS}
   * response.
   */
  @Test
  public void testEvaluateInputValid() {
    ((MockBNFParser) mockParser).setValidSentence(true);
    Repl.Response response = repl.evaluateInput("valid input", "mockRule");
    assertEquals(Repl.Response.SUCCESS, response);
  }

  /**
   * Tests that an invalid input sentence returns the
   * {@link Repl.Response#UNKNOWN_COMMAND} response.
   */
  @Test
  public void testEvaluateInputInvalid() {
    ((MockBNFParser) mockParser).setValidSentence(false);
    Repl.Response response = repl.evaluateInput("invalid input", "mockRule");
    assertEquals(Repl.Response.UNKNOWN_COMMAND, response);
  }
}
