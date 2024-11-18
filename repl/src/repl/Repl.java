package repl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A REPL for parsing sentences based on a given BNF grammar.
 * Prints a canned response to the specified output stream
 * based on the input from the specified input stream.
 * The REPL stops when the user enters the command `exit`.
 */
public class Repl {

  private boolean isRunning;
  private final BNFParser parser;
  private static final String DEFAULT_BNF_FILE = "src/repl/domolect2-0.bnf";
  private static final String DEFAULT_RULE_NAME = "augmented_command";

  /**
   * Constructs a new {@code DomolectRepl} object with a given parser.
   *
   * @param parser The {@code BNFParser} used to parse input commands. Must not be
   *               {@code null}.
   */
  public Repl(BNFParser parser) {
    this.parser = parser;
    isRunning = false;
  }

  /**
   * The main method creates an instance of the {@code DomolectRepl} class
   * and invokes its {@code start} method to begin the REPL loop. The REPL
   * reads commands from the standard input stream and writes responses to
   * the standard output stream.
   *
   * @param args Command-line arguments passed to the application.
   *            The following optional arguments are supported:
   *           -f <file>: The path to the BNF file to load.
   *          -c <ruleName>: The name of the rule to use for parsing commands.
   */
  public static void main(String[] args) {
    String filePath = DEFAULT_BNF_FILE;
    String ruleName = DEFAULT_RULE_NAME;

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-f") && i + 1 < args.length) {
        filePath = args[i + 1];
        i++;
      } else if (args[i].equals("-c") && i + 1 < args.length) {
        ruleName = args[i + 1];
        i++;
      }
    }

    BNFParser parser = new BNFParser(filePath);
    System.out.println("Loaded BNF rules from " + filePath);
    System.out.println("Parsing against rule: " + ruleName);

    Repl repl = new Repl(parser);
    repl.start(System.in, System.out, ruleName);
  }

  /**
   * Initializes the REPL and starts the main REPL loop with the specified
   * input and output streams.
   * 
   * Note: It is up to the caller to ensure that the REPL is not already
   * running by checking the return value of {@code isRunning()}.
   * 
   * @param in       The input stream to read user commands from. Must not be
   *                 {@code null}.
   * @param out      The output stream to print responses to. Must not be
   *                 {@code null}.
   * @param ruleName The name of the rule to use for parsing commands. Must not be
   *                 {@code null}.
   * @throws IllegalArgumentException If either input or output streams are
   *                                  {@code null}.
   */
  public void start(InputStream in, OutputStream out, String ruleName) {

    if (in == null || out == null) {
      isRunning = false;
      throw new IllegalArgumentException("Input and output streams must be non-null.");
    }

    isRunning = true;
    PrintWriter writer = new PrintWriter(out, true);
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

    writer.println("Welcome to the Domolect 2.0 parser!");
    writer.println("Enter a command or type `exit` to quit.");

    while (isRunning) {
      writer.print(">>> ");
      writer.flush();
      String input;
      try {
        input = reader.readLine();
      } catch (IOException e) {
        writer.println("Error reading input: " + e.getMessage());
        continue;
      }

      if (input == null) {
        System.err.println("Error reading input stream: EOF reached.");
        this.stop();
        return;
      }

      Response response = evaluateInput(input, ruleName);
      writer.println(response.message);

      if (response.equals(Response.EXIT)) {
        this.stop();
        return;
      }
    }
  }

  /**
   * Evaluates the input command and returns a canned response.
   *
   * @param input    The input command to evaluate. Must not be {@code null}.
   *                 Must be in lowercase, with nonzero whitespace separating
   *                 terminal symbols.
   * @param ruleName The name of the rule to use for parsing commands. Must not be
   *                 {@code null}.
   * @return A canned {@code Response} based on the input command. Will return
   *         {@code Response.EXIT}
   *         if the input is the string "exit", {@code Response.SUCCESS} if the
   *         input is a valid
   *         sentence based on the given rule, or {@code Response.UNKNOWN_COMMAND}
   *         otherwise.
   */
  public Response evaluateInput(String input, String ruleName) {
    if (input.equals("exit")) {
      return Response.EXIT;
    }

    if (parser.isValidSentence(input, ruleName)) {
      return Response.SUCCESS;
    }

    return Response.UNKNOWN_COMMAND;
  }

  /**
   * Exits the REPL by setting the running flag to false.
   * Other cleanup tasks may be performed here in the future.
   */
  public void stop() {
    isRunning = false;
  }

  /**
   * Returns whether the REPL is currently running.
   * 
   * @return true if the REPL is running, false otherwise.
   */
  public boolean isRunning() {
    return isRunning;
  }

  public enum Response {
    EXIT("Exiting REPL..."),
    UNKNOWN_COMMAND("Unrecognised command."),
    SUCCESS("Command executed successfully.");

    public final String message;

    Response(String message) {
      this.message = message;
    }
  }

}
