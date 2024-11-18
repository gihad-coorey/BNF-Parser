package repl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;

/**
 * This class can be used to validate sentences against defined BNF rules.
 * The BNF rules are read from a file or can be added programmatically.
 * The BNF rules should be in the following format:
 * <non_terminal> ::= "terminal" | <non_terminal> | ( <non_terminal> |
 * <non_terminal> ) | ...
 * 
 * Non-terminal symbols are enclosed in angle brackets (e.g. <location>).
 * Terminal symbols are enclosed in double quotes ("").
 * Alternative productions for a rule or subset of a rule are separated by the
 * pipe character (|).
 * Brackets can be used to group symbols separated by the pipe (|) where
 * ambiguity may arise. Nested brackets are allowed, but must be balanced and
 * unambiguous.
 * Repeatable symbols are followed by an asterisk (*).
 * Optional symbols are followed by a question mark (?).
 * Digits within a range are denoted by square brackets (e.g. [3-9]).
 * Whitespace is not allowed within any symbol. Use underscores (_) to represent
 * spaces in non-terminals (e.g. <my_location>), and hyphens (-) in terminals
 * (e.g. "light-switch").
 * 
 * Recursive definitions are not supported yet.
 * 
 * Example BNF rule with nested brackets:
 * <my_location> ::= "my-location-is" ( (<room> <floor>) | (<country> <city>) )
 * 
 * Example BNF rule with repeatable symbols and digits within a range:
 * <number> ::= ([1-9] [0-9]*) | [0-9]
 * 
 * Example BNF rule with optional symbols:
 * <optional> ::= <number>? "non-optional"
 * 
 * @author Gihad Coorey. 23091788.
 * @version 1.0
 * 
 */
public class BNFParser {
  private static final Pattern RULE_PATTERN = Pattern.compile("<(.*?)> ::= (.*)");
  private final Map<String, String> bnfRules;

  /**
   * Constructs a new BNFParser object and sets the {@code bnfRules} map based on
   * the specified BNF file.
   * 
   * @param filePath A path to a valid BNF file.
   */
  public BNFParser(String filePath) {
    bnfRules = new HashMap<>();
    parseBNFFile(filePath);
  }

  /**
   * Constructs a new BNFParser object with an empty {@code bnfRules} map
   * which can be populated later. This constructor is useful when the BNF rules
   * are set programmatically and not read from a file.
   */
  public BNFParser() {
    bnfRules = new HashMap<>();
  }

  /**
   * Add a new rule to the BNF rules map.
   * 
   * @param ruleName       The name of the rule, without the enclosing angle brackets. If the rule already exists, it will be overwritten.
   * @param ruleDefinition The definition of the rule as a single String. The definition should follow the BNF format specified in the class documentation.
   */
  public void addRule(String ruleName, String ruleDefinition) {
    bnfRules.put(ruleName, ruleDefinition);
  }

  /**
   * Get the whole definition of a rule, as defined in the class documentation.
   * 
   * @param ruleName The name of the rule, without the enclosing angle brackets.
   * @return The definition of the rule as a single String, or null if the rule does not exist.
   */
  public String getRuleDefinition(String ruleName) {
    return bnfRules.get(ruleName);
  }

  /**
   * Get the map of all existing rules in the parser instance.
   * 
   * @return A copy of the map {@code bnfRules} of all rules parsed from the BNF file or added programmatically. The map is a copy, so modifying it will not affect the internal state of the parser.
   * To modify the rules, use the {@code addRule} and {@code removeRule} methods.
   */
  public Map<String, String> getRules() {
    return new HashMap<>(bnfRules);
  }

  /**
   * Remove a rule from the BNF rules map.
   * 
   * @param ruleName The name of the rule to remove.
   */
  public void removeRule(String ruleName) {
    bnfRules.remove(ruleName);
  }

  private void parseBNFFile(String filePath) {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();

        if (line.isEmpty() || line.startsWith("#")) {
          continue;
        }

        Matcher matcher = RULE_PATTERN.matcher(line);
        if (matcher.matches()) {
          String ruleName = matcher.group(1).trim();
          String ruleDefinition = matcher.group(2).trim();

          addRule(ruleName, ruleDefinition);
        }
      }
    } catch (IOException e) {
      System.err.println("Error reading BNF file: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Check if a given sentence conforms to a specified rule.
   *
   * @param sentence The sentence to validate, with words separated by whitespace.
   * @param ruleName The rule to validate against, without the enclosing angle brackets.
   * @return True if the sentence matches the rule, false otherwise.
   * 
   * @throws IllegalArgumentException If a rule with the specified name has not been parsed. To check the current state of the {@code bnfRules} map, use the {@code getRules} method.
   */
  public boolean isValidSentence(String sentence, String ruleName) {
    String[] tokens = sentence.split("\\s+");
    String ruleDef = getRuleDefinition(ruleName);
    if (ruleDef == null) {
      throw new IllegalArgumentException("Rule " + ruleName + " not found.");
    }
    return matchTokensToRuleDef(tokens, 0, ruleDef) == tokens.length;
  }

  private int matchTokensToRuleDef(String[] tokens, int start, String ruleDef) {
    String[] ruleProductions = splitByTopLevelOr(ruleDef);
    for (String production : ruleProductions) {
      int end = matchTokensToProduction(tokens, start, production);
      if (end != -1) {
        return end;
      }
    }
    return -1;
  }

  private String[] splitByTopLevelOr(String ruleDefinition) {
    List<String> result = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    Stack<Character> parenthesesStack = new Stack<>();

    for (char c : ruleDefinition.toCharArray()) {
      if (c == '(') {
        parenthesesStack.push(c);
        current.append(c);
      } else if (c == ')') {
        parenthesesStack.pop();
        current.append(c);
      } else if (c == '|' && parenthesesStack.isEmpty()) {
        result.add(current.toString().trim());
        current.setLength(0);
      } else {
        current.append(c);
      }
    }

    if (current.length() > 0) {
      result.add(current.toString().trim());
    }

    return result.toArray(new String[0]);
  }

  private String[] tokeniseProduction(String production) {
    List<String> tokens = new ArrayList<>();
    StringBuilder token = new StringBuilder();
    Stack<Character> parenthesesStack = new Stack<>();

    for (char c : production.toCharArray()) {
      if (c == '(') {
        token.append(c);
        parenthesesStack.push(c);
      } else if (c == ')') {
        parenthesesStack.pop();
        token.append(c);
        if (parenthesesStack.isEmpty()) {
          tokens.add(token.toString().trim());
          token.setLength(0);
        }
      } else if (c == ' ' && parenthesesStack.isEmpty()) {
        if (token.length() > 0) {
          tokens.add(token.toString().trim());
          token.setLength(0);
        }
      } else {
        token.append(c);
      }
    }

    if (token.length() > 0) {
      tokens.add(token.toString().trim());
    }

    return tokens.toArray(new String[0]);
  }

  private int matchTokensToProduction(String[] tokens, int start, String production) {
    int tokensIdx = start;
    String[] productionTokens = tokeniseProduction(production);
    for (String productionToken : productionTokens) {
      if (tokensIdx >= tokens.length && !isOptional(productionToken)) {
        return -1;
      }
      if (isRepeatable(productionToken)) {
        productionToken = trimString(productionToken, "*");
        while (tokensIdx < tokens.length) {
          int end = matchSingleToken(tokens, tokensIdx, productionToken);
          if (end == -1) {
            break;
          }
          tokensIdx = end;
        }
        continue;
      }

      if (isOptional(productionToken)) {
        String optionalToken = trimString(productionToken, "?");
        if (isTerminal(optionalToken)) {
          String terminal = trimString(optionalToken, "\"");
          if (terminal.equals(tokens[tokensIdx])) {
            tokensIdx++;
          }
          continue;
        } else if (isNonTerminal(optionalToken)) {
          String ruleName = trimString(optionalToken, "<>");
          int potentialTokensIdx = matchTokensToRuleDef(tokens, tokensIdx, getRuleDefinition(ruleName));
          if (potentialTokensIdx != -1) {
            tokensIdx = potentialTokensIdx;
          }
          continue;
        } else if (isBracketed(optionalToken)) {
          String unbracketed = optionalToken.substring(1, optionalToken.length() - 1);
          int potentialTokensIdx = matchTokensToRuleDef(tokens, tokensIdx, unbracketed);
          if (potentialTokensIdx != -1) {
            tokensIdx = potentialTokensIdx;
          }
          continue;
        } else if (isSquareBracketed(optionalToken)) {
          if (tokens[tokensIdx].matches(productionToken)) {
            tokensIdx++;
            continue;
          }
        }
      }

      int end = matchSingleToken(tokens, tokensIdx, productionToken);
      if (end == -1) {
        return -1;
      }
      tokensIdx = end;
    }
    return tokensIdx;
  }

  private int matchSingleToken(String[] tokens, int start, String productionToken) {
    if (isTerminal(productionToken)) {
      String terminal = trimString(productionToken, "\"");
      if (terminal.equals(tokens[start])) {
        return start + 1;
      } else {
        return -1;
      }
    } else if (isNonTerminal(productionToken)) {
      String ruleName = trimString(productionToken, "<>");
      return matchTokensToRuleDef(tokens, start, getRuleDefinition(ruleName));
    } else if (isBracketed(productionToken)) {
      String unbracketed = productionToken.substring(1, productionToken.length() - 1);
      return matchTokensToRuleDef(tokens, start, unbracketed);
    } else if (isSquareBracketed(productionToken)) {
      if (tokens[start].matches(productionToken)) {
        return start + 1;
      }
    }
    return -1;
  }

  private boolean isNonTerminal(String token) {
    if(token.startsWith("<") && token.endsWith(">")) {
      return token.indexOf(">", 1) == token.length() - 1;
    }
    return false;
  }

  private boolean isTerminal(String token) {
    if (token.startsWith("\"") && token.endsWith("\"")) {
      return token.indexOf("\"", 1) == token.length() - 1;
    }
    return false;
  }

  private boolean isBracketed(String token) {
    if (token.startsWith("(") && token.endsWith(")")) {
      int openBracketCount = 0;
      for (int i = 0; i < token.length(); i++) {
        char c = token.charAt(i);
        if (c == '(') {
          openBracketCount++;
        } else if (c == ')') {
          openBracketCount--;
          if (openBracketCount < 0) {
            return false;
          }
        }
      }
      return openBracketCount == 0;
    }
    return false;
  }

  private boolean isOptional(String token) {
    return token.endsWith("?");
  }

  private boolean isSquareBracketed(String token) {
    if (token.startsWith("[") && token.endsWith("]")) {
      return token.indexOf("]", 1) == token.length() - 1;
    }
    return false;
  }

  private boolean isRepeatable(String token) {
    return token.endsWith("*");
  }

  private String trimString(String str, String trimChars) {
    if (str == null || str.isEmpty() || trimChars == null || trimChars.isEmpty()) {
      return str;
    }

    int start = 0;
    int end = str.length() - 1;

    while (start <= end && trimChars.indexOf(str.charAt(start)) >= 0) {
      start++;
    }

    while (end >= start && trimChars.indexOf(str.charAt(end)) >= 0) {
      end--;
    }

    return str.substring(start, end + 1);
  }

}
