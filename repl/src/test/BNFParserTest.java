package test;

import org.junit.BeforeClass;
import org.junit.Test;
import repl.BNFParser;
import static org.junit.Assert.*;
import java.util.Set;

/**
 * Unit tests for the {@link BNFParser} class.
 * This test suite covers BNF grammar patterns, including nested brackets, repeatable symbols,
 * optional symbols, numbers, and rule management (adding and removing rules).
 */
public class BNFParserTest {

  private static BNFParser parser;
  private static final String TEST_GRAMMAR_FILE = "src/test/testGrammar.bnf";

  /**
   * Sets up the test environment by initializing a BNFParser instance with the test grammar file.
   */
  @BeforeClass
  public static void setUp() {
    parser = new BNFParser(TEST_GRAMMAR_FILE);
  }

  /**
   * Tests nested bracket parsing using the rule "nested_alternatives".
   * Valid inputs: "e", "a b d", and "a c d".
   * All should return true, indicating valid sentences.
   */
  @Test
  public void testNestedBrackets() {
    String ruleName = "nested_alternatives";
    String validInput = "e";
    String alsoValidInput = "a b d";
    String stillValidInput = "a c d";

    assertTrue(parser.isValidSentence(validInput, ruleName));
    assertTrue(parser.isValidSentence(alsoValidInput, ruleName));
    assertTrue(parser.isValidSentence(stillValidInput, ruleName));
  }

  /**
   * Tests repeatable symbol handling using the rule "repeatable".
   * Valid inputs: "repeatable", "repeatable repeatable", and "repeatable repeatable repeatable".
   * All should return true, indicating valid sentences.
   */
  @Test
  public void testRepeatableSymbols() {
    String ruleName = "repeatable";

    assertTrue(parser.isValidSentence("repeatable", ruleName));
    assertTrue(parser.isValidSentence("repeatable repeatable", ruleName));
    assertTrue(parser.isValidSentence("repeatable repeatable repeatable", ruleName));
  }

  /**
   * Tests optional symbol handling using the rule "optional".
   * Valid inputs: "optional not-optional" and "not-optional".
   * Both should return true, indicating valid sentences.
   */
  @Test
  public void testOptionalSymbols() {
    String ruleName = "optional";
    String validInputWithOptional = "optional not-optional";
    assertTrue(parser.isValidSentence(validInputWithOptional, ruleName));

    String validInputWithoutOptional = "not-optional";
    assertTrue(parser.isValidSentence(validInputWithoutOptional, ruleName));
  }

  /**
   * Tests ranged digit parsing using the rule "digit".
   * Valid input: "5", should return true.
   * Invalid input: "1", should return false.
   */
  @Test
  public void testRangedDigits() {
    String ruleName = "digit";
    String validDigit = "5";
    assertTrue(parser.isValidSentence(validDigit, ruleName));

    String invalidNumber = "1";
    assertFalse(parser.isValidSentence(invalidNumber, ruleName));
  }

  /**
   * Tests fetching the rule names using the {@link BNFParser#getRules()} method.
   * The rule set should contain "repeatable", "optional", "digit", and "nested_alternatives".
   */
  @Test
  public void testGetRuleNames() {
    Set<String> ruleNames = parser.getRules().keySet();
    assertNotNull(ruleNames);
    assertTrue(ruleNames.contains("repeatable"));
    assertTrue(ruleNames.contains("optional"));
    assertTrue(ruleNames.contains("digit"));
    assertTrue(ruleNames.contains("nested_alternatives"));
  }

  /**
   * Tests adding a new rule to the parser using {@link BNFParser#addRule(String, String)}.
   * Adds rule "newRule" with definition `"newWord" | "otherWord"`.
   * Valid inputs: "newWord" and "otherWord", both should return true.
   */
  @Test
  public void testAddRule() {
    String newRuleName = "newRule";
    String ruleDefinition = "\"newWord\" | \"otherWord\"";
    parser.addRule(newRuleName, ruleDefinition);

    Set<String> ruleNames = parser.getRules().keySet();
    assertTrue(ruleNames.contains(newRuleName));

    assertTrue(parser.isValidSentence("newWord", newRuleName));
    assertTrue(parser.isValidSentence("otherWord", newRuleName));
  }

  /**
   * Tests removing a rule using {@link BNFParser#removeRule(String)}.
   * Adds rule "ruleToRemove" with definition `"word"`, then removes it.
   * After removal, the rule should no longer exist in the parser.
   */
  @Test
  public void testRemoveRule() {
    String ruleName = "ruleToRemove";
    String ruleDefinition = "\"word\"";
    parser.addRule(ruleName, ruleDefinition);
    assertTrue(parser.getRules().keySet().contains(ruleName));

    parser.removeRule(ruleName);
    assertFalse(parser.getRules().keySet().contains(ruleName));
  }

  /**
   * Tests the behavior when using a non-existent rule.
   * Input: "valid input" with a non-existent rule "nonExistentRule".
   * Expected: {@link IllegalArgumentException}.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNonExistentRule() {
    String ruleName = "nonExistentRule";
    String input = "valid input";
    parser.isValidSentence(input, ruleName);
  }
}
