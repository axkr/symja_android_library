package org.matheclipse.parser.test.tablegen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class OperatorTableBuilder {

  /**
   * Compiles the general table into the tables used internally by the library. This facilitates
   * fast access of this information by clients needing this information.
   *
   * @param operatorData Data loaded from operators.yml
   * @param characterData Data loaded from named-characters.yml
   * @return A map containing all the compiled operator tables.
   */
  @SuppressWarnings("unchecked")
  public static Map<String, Object> compileTables(Map<String, Object> operatorData,
      Map<String, Object> characterData) {
    Map<String, Object> operatorPrecedences = new HashMap<>();

    Map<String, String> boxOperators = new HashMap<>();
    Map<String, Integer> flatBinaryOperators = new HashMap<>();
    Map<String, Integer> leftBinaryOperators = new HashMap<>();
    Map<String, Integer> miscellaneousOperators = new HashMap<>();
    Map<String, List<Object>> noMeaningInfixOperators = new HashMap<>();
    Map<String, List<Object>> noMeaningPostfixOperators = new HashMap<>();
    Map<String, List<Object>> noMeaningPrefixOperators = new HashMap<>();
    Map<String, Integer> nonassocBinaryOperators = new HashMap<>();
    // This is equivalent to Python's defaultdict(list)
    Map<String, List<String>> operator2string = new HashMap<>();
    Map<String, String> operator2amslatex = new HashMap<>();
    Map<String, Integer> postfixOperators = new HashMap<>();
    Map<String, Integer> prefixOperators = new HashMap<>();
    Map<String, Integer> rightBinaryOperators = new HashMap<>();
    Map<String, List<Integer>> ternaryOperators = new HashMap<>();

    for (Map.Entry<String, Object> entry : operatorData.entrySet()) {
      String operatorName = entry.getKey();
      Map<String, Object> operatorInfo = (Map<String, Object>) entry.getValue();

      Object precedenceObj = operatorInfo.get("precedence");
      operatorPrecedences.put(operatorName, precedenceObj);

      String affix = (String) operatorInfo.get("affix");
      String arity = (String) operatorInfo.get("arity");
      String associativity = (String) operatorInfo.get("associativity");

      // In Java, we assign directly to the final map instead of using a temporary 'operator_dict'
      if ("Ternary".equals(arity)) {
        if (precedenceObj instanceof Integer) {
          // Convert the precedence to a List<Integer> for ternary operators
          precedenceObj = Arrays.asList((Integer) precedenceObj, (Integer) precedenceObj,
              (Integer) precedenceObj);
        }
        ternaryOperators.put(operatorName, (List<Integer>) precedenceObj);
      } else if ("unknown".equals(associativity)) {
        miscellaneousOperators.put(operatorName, (Integer) precedenceObj);
      } else if ("Infix".equals(affix) || "Binary".equals(affix)) {
        if (associativity == null) {
          flatBinaryOperators.put(operatorName, (Integer) precedenceObj);
        } else {
          switch (associativity) {
            case "left":
              leftBinaryOperators.put(operatorName, (Integer) precedenceObj);
              break;
            case "right":
              rightBinaryOperators.put(operatorName, (Integer) precedenceObj);
              break;
            case "non-associative":
              nonassocBinaryOperators.put(operatorName, (Integer) precedenceObj);
              break;
            default:
              System.err.printf("FIXME: associativity %s not handled in %s%n", associativity,
                  operatorName);
              break;
          }
        }
      } else if ("Prefix".equals(affix)) {
        prefixOperators.put(operatorName, (Integer) precedenceObj);
      } else if ("Postfix".equals(affix)) {
        postfixOperators.put(operatorName, (Integer) precedenceObj);
      }

      if ((boolean) operatorInfo.getOrDefault("box-operator", false)) {
        System.out.println(operatorName);
        boxOperators.put(operatorName, operatorInfo.get("operator").toString());
      }

      Map<String, Object> characterInfo = (Map<String, Object>) characterData.get(operatorName);
      if (characterInfo == null) {
        System.out.println("no characterInfo: " + operatorName);
        continue;
      }

      String unicodeChar = (String) characterInfo.getOrDefault("unicode-equivalent", "no-unicode");
      String asciiChars = (String) characterInfo.getOrDefault("ascii", "no-ascii");

      if (!"no-unicode".equals(unicodeChar)) {
        operator2string.computeIfAbsent(operatorName, k -> new ArrayList<>()).add(unicodeChar);
        if (characterInfo.containsKey("amslatex")) {
          operator2amslatex.put(unicodeChar, (String) characterInfo.get("amslatex"));
        }
      }
      if (!"no-ascii".equals(asciiChars)) {
        operator2string.computeIfAbsent(operatorName, k -> new ArrayList<>()).add(asciiChars);
      }

      Object meaningful = operatorInfo.getOrDefault("meaningful", true);
      if (meaningful == "false" || meaningful == "none") {
        if ("no-unicode".equals(unicodeChar)) {
          String wlUnicode = (String) characterInfo.get("wl-unicode");
          if (wlUnicode == null) {
            System.err.printf("FIXME: no unicode or WMA equivalent for %s%n", operatorName);
            continue;
          }
          unicodeChar = wlUnicode;
        }

        // The value is a tuple in Python (char, precedence). We use a List in Java.
        List<Object> valueTuple = Arrays.asList(unicodeChar, precedenceObj);
        switch (affix) {
          case "Infix":
            noMeaningInfixOperators.put(operatorName, valueTuple);
            break;
          case "Postfix":
            noMeaningPostfixOperators.put(operatorName, valueTuple);
            break;
          case "Prefix":
            noMeaningPrefixOperators.put(operatorName, valueTuple);
            break;
          default:
            System.err.printf("FIXME: affix %s of %s not handled%n", affix, operatorName);
            break;
        }
      }
    }

    Map<String, Object> result = new LinkedHashMap<>(); // Use LinkedHashMap to preserve insertion
                                                        // order
    result.put("box-operators", boxOperators);
    result.put("flat-binary-operators", flatBinaryOperators);
    result.put("left-binary-operators", leftBinaryOperators);
    result.put("miscellaneous-operators", miscellaneousOperators);
    result.put("no-meaning-infix-operators", noMeaningInfixOperators);
    result.put("no-meaning-postfix-operators", noMeaningPostfixOperators);
    result.put("no-meaning-prefix-operators", noMeaningPrefixOperators);
    result.put("non-associative-binary-operators", nonassocBinaryOperators);
    result.put("operator-to-amslatex", operator2amslatex);
    result.put("operator-to_string", operator2string);
    result.put("operator-precedences", operatorPrecedences);
    result.put("postfix-operators", postfixOperators);
    result.put("prefix-operators", prefixOperators);
    result.put("right-binary-operators", rightBinaryOperators);
    result.put("ternary-operators", ternaryOperators);
    return result;
  }

  // The code-generation half of this class - OperatorTablesCodeGenerator and the main method
  // driving it - used to emit WMAOperatorTables.java for the org.matheclipse.parser.wma package.
  // That package was an experimental fork of Parser/Scanner (issue #1204) and has been removed:
  // its job is done by OperatorTable, which both production factories are built from, and it
  // carried a live associativity bug (it stored groupings with Operator's constants and read them
  // with InfixOperator's, whose values differ). compileTables above is still used by
  // OperatorTableReport to compare Symja's table against the Mathics3 YAML data.
}
