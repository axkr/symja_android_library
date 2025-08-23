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

  /**
   * Generates Java code for a class containing static operator tables based on the compiled
   * operator data from YAML files.
   */
  public static class OperatorTablesCodeGenerator {

    private static final String INDENTATION = "  ";
    private static final String DOUBLE_INDENTATION = INDENTATION + INDENTATION;

    /**
     * Generates Java source code for the OperatorTables class.
     *
     * @param compiledTables The compiled operator tables from the YAML resources
     * @param outputDirectory The directory where the generated Java file should be saved
     * @param packageName The package name for the generated class
     * @throws IOException If an I/O error occurs during file writing
     */
    public static void generateOperatorTablesClass(Map<String, Object> compiledTables,
        Path outputDirectory, String packageName) throws IOException {
      // Generate Java class with static tables from the compiled data
      String className = "WMAOperatorTables";
      StringBuilder javaCode = new StringBuilder();

      // Package and imports
      javaCode.append("package ").append(packageName).append(";\n\n");
      javaCode.append("import java.util.*;\n\n");

      // Class Javadoc
      javaCode.append("/**\n");
      javaCode.append(
          " * Auto-generated class containing operator tables for the matheclipse parser.\n");
      javaCode.append(
          " * <p>This class is generated from YAML resources defining operators and special characters.\n");
      javaCode
          .append(" * <p>DO NOT MODIFY MANUALLY - Generated by WMAOperatorTablesCodeGenerator\n");
      javaCode.append(" */\n");

      // Class declaration
      javaCode.append("public final class ").append(className).append(" {\n\n");

      // Add private constructor to prevent instantiation
      javaCode.append(INDENTATION).append("/** Private constructor to prevent instantiation */\n");
      javaCode.append(INDENTATION).append("private ").append(className).append("() {}\n\n");

      // Generate static field declarations and initializers for each table
      generateOperatorPrecedencesTable(javaCode, compiledTables);
      generateBinaryOperatorTables(javaCode, compiledTables);
      generatePrefixOperatorsTable(javaCode, compiledTables);
      generatePostfixOperatorsTable(javaCode, compiledTables);
      generateTernaryOperatorsTable(javaCode, compiledTables);
      generateSpecialCharacterTables(javaCode, compiledTables);
      generateAllOperatorStrings(javaCode, compiledTables);
      generateUtilityMethods(javaCode);

      // Close class
      javaCode.append("}\n");

      // Write to file
      // Files.createDirectories(outputDirectory);
      Path outputFile = outputDirectory.resolve(className + ".java");
      Files.writeString(outputFile, javaCode.toString());
    }

    private static void generateOperatorPrecedencesTable(StringBuilder code,
        Map<String, Object> compiledTables) {
      @SuppressWarnings("unchecked")
      Map<String, Object> operatorPrecedences =
          (Map<String, Object>) compiledTables.get("operator-precedences");

      code.append(INDENTATION).append("/** Map of operator names to their precedence values */\n");
      code.append(INDENTATION)
          .append("public static final Map<String, Integer> OPERATOR_PRECEDENCES = ")
          .append("createOperatorPrecedencesMap();\n\n");

      code.append(INDENTATION)
          .append("private static Map<String, Integer> createOperatorPrecedencesMap() {\n");
      code.append(DOUBLE_INDENTATION).append("Map<String, Integer> map = new HashMap<>();\n");

      operatorPrecedences.entrySet().stream().filter(entry -> entry.getValue() instanceof Integer)
          .forEach(entry -> {
            code.append(DOUBLE_INDENTATION).append("map.put(\"").append(entry.getKey())
                .append("\", ").append(entry.getValue()).append(");\n");
          });

      code.append(DOUBLE_INDENTATION).append("return Collections.unmodifiableMap(map);\n");
      code.append(INDENTATION).append("}\n\n");
    }

    private static void generateBinaryOperatorTables(StringBuilder code,
        Map<String, Object> compiledTables) {
      generateBinaryOperatorTable(code, compiledTables, "flat-binary-operators",
          "FLAT_BINARY_OPERATORS", "Operators with flat associativity");
      generateBinaryOperatorTable(code, compiledTables, "left-binary-operators",
          "LEFT_BINARY_OPERATORS", "Operators with left associativity");
      generateBinaryOperatorTable(code, compiledTables, "right-binary-operators",
          "RIGHT_BINARY_OPERATORS", "Operators with right associativity");
      generateBinaryOperatorTable(code, compiledTables, "non-associative-binary-operators",
          "NON_ASSOC_BINARY_OPERATORS", "Operators with non-associative property");
    }

    private static void generateBinaryOperatorTable(StringBuilder code,
        Map<String, Object> compiledTables, String tableKey, String constantName,
        String description) {
      @SuppressWarnings("unchecked")
      Map<String, Integer> operators = (Map<String, Integer>) compiledTables.get(tableKey);
      if (operators == null) {
        System.out.println("No operators found for " + tableKey);
        return;
      }

      code.append(INDENTATION).append("/** ").append(description).append(" */\n");
      code.append(INDENTATION).append("public static final Map<String, Integer> ")
          .append(constantName).append(" = create").append(camelCase(constantName))
          .append("();\n\n");

      code.append(INDENTATION).append("private static Map<String, Integer> create")
          .append(camelCase(constantName)).append("() {\n");
      code.append(DOUBLE_INDENTATION).append("Map<String, Integer> map = new HashMap<>();\n");


      operators.entrySet().forEach(entry -> {
        code.append(DOUBLE_INDENTATION).append("map.put(\"").append(entry.getKey()).append("\", ")
            .append(entry.getValue()).append(");\n");
      });

      code.append(DOUBLE_INDENTATION).append("return Collections.unmodifiableMap(map);\n");
      code.append(INDENTATION).append("}\n\n");
    }

    private static void generatePrefixOperatorsTable(StringBuilder code,
        Map<String, Object> compiledTables) {
      @SuppressWarnings("unchecked")
      Map<String, Integer> prefixOperators =
          (Map<String, Integer>) compiledTables.get("prefix-operators");
      if (prefixOperators == null) {
        System.out.println("No operators found for " + "prefix-operators");
        return;
      }
      code.append(INDENTATION).append("/** Prefix operators and their precedence values */\n");
      code.append(INDENTATION)
          .append("public static final Map<String, Integer> PREFIX_OPERATORS = ")
          .append("createPrefixOperatorsMap();\n\n");

      code.append(INDENTATION)
          .append("private static Map<String, Integer> createPrefixOperatorsMap() {\n");
      code.append(DOUBLE_INDENTATION).append("Map<String, Integer> map = new HashMap<>();\n");

      prefixOperators.entrySet().forEach(entry -> {
        code.append(DOUBLE_INDENTATION).append("map.put(\"").append(entry.getKey()).append("\", ")
            .append(entry.getValue()).append(");\n");
      });

      code.append(DOUBLE_INDENTATION).append("return Collections.unmodifiableMap(map);\n");
      code.append(INDENTATION).append("}\n\n");
    }

    private static void generatePostfixOperatorsTable(StringBuilder code,
        Map<String, Object> compiledTables) {
      @SuppressWarnings("unchecked")
      Map<String, Integer> postfixOperators =
          (Map<String, Integer>) compiledTables.get("postfix-operators");
      if (postfixOperators == null) {
        System.out.println("No operators found for " + "postfix-operators");
        return;
      }
      code.append(INDENTATION).append("/** Postfix operators and their precedence values */\n");
      code.append(INDENTATION)
          .append("public static final Map<String, Integer> POSTFIX_OPERATORS = ")
          .append("createPostfixOperatorsMap();\n\n");

      code.append(INDENTATION)
          .append("private static Map<String, Integer> createPostfixOperatorsMap() {\n");
      code.append(DOUBLE_INDENTATION).append("Map<String, Integer> map = new HashMap<>();\n");

      postfixOperators.entrySet().forEach(entry -> {
        code.append(DOUBLE_INDENTATION).append("map.put(\"").append(entry.getKey()).append("\", ")
            .append(entry.getValue()).append(");\n");
      });

      code.append(DOUBLE_INDENTATION).append("return Collections.unmodifiableMap(map);\n");
      code.append(INDENTATION).append("}\n\n");
    }

    private static void generateTernaryOperatorsTable(StringBuilder code,
        Map<String, Object> compiledTables) {
      @SuppressWarnings("unchecked")
      Map<String, List<Integer>> ternaryOperators =
          (Map<String, List<Integer>>) compiledTables.get("ternary-operators");

      if (ternaryOperators == null || ternaryOperators.isEmpty()) {
        return;
      }

      code.append(INDENTATION)
          .append("/** Ternary operators with their precedence values for each part */\n");
      code.append(INDENTATION)
          .append("public static final Map<String, List<Integer>> TERNARY_OPERATORS = ")
          .append("createTernaryOperatorsMap();\n\n");

      code.append(INDENTATION)
          .append("private static Map<String, List<Integer>> createTernaryOperatorsMap() {\n");
      code.append(DOUBLE_INDENTATION).append("Map<String, List<Integer>> map = new HashMap<>();\n");

      ternaryOperators.entrySet().forEach(entry -> {
        code.append(DOUBLE_INDENTATION).append("map.put(\"").append(entry.getKey())
            .append("\", Arrays.asList(");

        List<Integer> precedences = entry.getValue();
        for (int i = 0; i < precedences.size(); i++) {
          if (i > 0) {
            code.append(", ");
          }
          code.append(precedences.get(i));
        }

        code.append("));\n");
      });

      code.append(DOUBLE_INDENTATION).append("return Collections.unmodifiableMap(map);\n");
      code.append(INDENTATION).append("}\n\n");
    }

    private static void generateSpecialCharacterTables(StringBuilder code,
        Map<String, Object> compiledTables) {
      @SuppressWarnings("unchecked")
      Map<String, String> operator2amslatex =
          (Map<String, String>) compiledTables.get("operator-to-amslatex");

      @SuppressWarnings("unchecked")
      Map<String, List<String>> operator2string =
          (Map<String, List<String>>) compiledTables.get("operator-to_string");

      // Generate the character to operator name mapping
      code.append(INDENTATION).append("/** Mapping from special character to operator name */\n");
      code.append(INDENTATION)
          .append("public static final Map<String, String> CHARACTER_TO_OPERATOR = ")
          .append("createCharacterToOperatorMap();\n\n");

      code.append(INDENTATION)
          .append("private static Map<String, String> createCharacterToOperatorMap() {\n");
      code.append(DOUBLE_INDENTATION).append("Map<String, String> map = new HashMap<>();\n");

      if (operator2string != null) {
        operator2string.forEach((operatorName, charList) -> {
          charList.forEach(charStr -> {
            code.append(DOUBLE_INDENTATION).append("map.put(\"").append(escapeString(charStr))
                .append("\", \"").append(operatorName).append("\");\n");
          });
        });
      }

      code.append(DOUBLE_INDENTATION).append("return Collections.unmodifiableMap(map);\n");
      code.append(INDENTATION).append("}\n\n");

      // Generate the operator name to character mapping
      code.append(INDENTATION)
          .append("/** Mapping from operator name to list of representation characters */\n");
      code.append(INDENTATION)
          .append("public static final Map<String, List<String>> OPERATOR_TO_CHARACTERS = ")
          .append("createOperatorToCharactersMap();\n\n");

      code.append(INDENTATION)
          .append("private static Map<String, List<String>> createOperatorToCharactersMap() {\n");
      code.append(DOUBLE_INDENTATION).append("Map<String, List<String>> map = new HashMap<>();\n");

      if (operator2string != null) {
        operator2string.forEach((operatorName, charList) -> {
          code.append(DOUBLE_INDENTATION).append("map.put(\"").append(operatorName)
              .append("\", Arrays.asList(");

          for (int i = 0; i < charList.size(); i++) {
            if (i > 0) {
              code.append(", ");
            }
            code.append("\"").append(escapeString(charList.get(i))).append("\"");
          }

          code.append("));\n");
        });
      }

      code.append(DOUBLE_INDENTATION).append("return Collections.unmodifiableMap(map);\n");
      code.append(INDENTATION).append("}\n\n");

      // Generate the LaTeX mapping if available
      if (operator2amslatex != null && !operator2amslatex.isEmpty()) {
        code.append(INDENTATION).append("/** Mapping from character to LaTeX representation */\n");
        code.append(INDENTATION)
            .append("public static final Map<String, String> CHARACTER_TO_LATEX = ")
            .append("createCharacterToLatexMap();\n\n");

        code.append(INDENTATION)
            .append("private static Map<String, String> createCharacterToLatexMap() {\n");
        code.append(DOUBLE_INDENTATION).append("Map<String, String> map = new HashMap<>();\n");

        operator2amslatex.forEach((character, latexStr) -> {
          code.append(DOUBLE_INDENTATION).append("map.put(\"").append(escapeString(character))
              .append("\", \"").append(escapeString(latexStr)).append("\");\n");
        });

        code.append(DOUBLE_INDENTATION).append("return Collections.unmodifiableMap(map);\n");
        code.append(INDENTATION).append("}\n\n");
      }
    }

    private static void generateAllOperatorStrings(StringBuilder code,
        Map<String, Object> compiledTables) {
      code.append(INDENTATION)
          .append("/** Set of all operator strings for efficient scanning */\n");
      code.append(INDENTATION).append("public static final Set<String> ALL_OPERATOR_STRINGS = ")
          .append("createAllOperatorStringsSet();\n\n");

      code.append(INDENTATION)
          .append("private static Set<String> createAllOperatorStringsSet() {\n");
      code.append(DOUBLE_INDENTATION).append("Set<String> set = new HashSet<>();\n");

      // Extract all operator strings from the various tables
      @SuppressWarnings("unchecked")
      Map<String, List<String>> operator2string =
          (Map<String, List<String>>) compiledTables.get("operator-to_string");

      if (operator2string != null) {
        code.append(DOUBLE_INDENTATION).append("// Add all operator representation strings\n");
        operator2string.values().stream().flatMap(Collection::stream).distinct().forEach(str -> {
          code.append(DOUBLE_INDENTATION).append("set.add(\"").append(escapeString(str))
              .append("\");\n");
        });
      }

      code.append(DOUBLE_INDENTATION).append("return Collections.unmodifiableSet(set);\n");
      code.append(INDENTATION).append("}\n\n");
    }

    private static void generateUtilityMethods(StringBuilder code) {
      // Method to get operator precedence
      code.append(INDENTATION).append("/**\n");
      code.append(INDENTATION)
          .append(" * Get the precedence for an operator, or null if not defined.\n");
      code.append(INDENTATION).append(" * @param operator The operator string\n");
      code.append(INDENTATION).append(" * @return The precedence value, or null if not found\n");
      code.append(INDENTATION).append(" */\n");
      code.append(INDENTATION)
          .append("public static Integer getOperatorPrecedence(String operator) {\n");
      code.append(DOUBLE_INDENTATION).append("return OPERATOR_PRECEDENCES.get(operator);\n");
      code.append(INDENTATION).append("}\n\n");

      // Method to check if an operator is right associative
      code.append(INDENTATION).append("/**\n");
      code.append(INDENTATION).append(" * Check if an operator is right associative.\n");
      code.append(INDENTATION).append(" * @param operator The operator string\n");
      code.append(INDENTATION).append(" * @return true if the operator is right associative\n");
      code.append(INDENTATION).append(" */\n");
      code.append(INDENTATION)
          .append("public static boolean isRightAssociative(String operator) {\n");
      code.append(DOUBLE_INDENTATION)
          .append("return RIGHT_BINARY_OPERATORS.containsKey(operator);\n");
      code.append(INDENTATION).append("}\n\n");

      // Method to check if an operator is left associative
      code.append(INDENTATION).append("/**\n");
      code.append(INDENTATION).append(" * Check if an operator is left associative.\n");
      code.append(INDENTATION).append(" * @param operator The operator string\n");
      code.append(INDENTATION).append(" * @return true if the operator is left associative\n");
      code.append(INDENTATION).append(" */\n");
      code.append(INDENTATION)
          .append("public static boolean isLeftAssociative(String operator) {\n");
      code.append(DOUBLE_INDENTATION)
          .append("return LEFT_BINARY_OPERATORS.containsKey(operator);\n");
      code.append(INDENTATION).append("}\n\n");

      // Method to check if an operator is a prefix operator
      code.append(INDENTATION).append("/**\n");
      code.append(INDENTATION).append(" * Check if an operator is a prefix operator.\n");
      code.append(INDENTATION).append(" * @param operator The operator string\n");
      code.append(INDENTATION).append(" * @return true if the operator is a prefix operator\n");
      code.append(INDENTATION).append(" */\n");
      code.append(INDENTATION)
          .append("public static boolean isPrefixOperator(String operator) {\n");
      code.append(DOUBLE_INDENTATION).append("return PREFIX_OPERATORS.containsKey(operator);\n");
      code.append(INDENTATION).append("}\n\n");

      // Method to check if an operator is a postfix operator
      code.append(INDENTATION).append("/**\n");
      code.append(INDENTATION).append(" * Check if an operator is a postfix operator.\n");
      code.append(INDENTATION).append(" * @param operator The operator string\n");
      code.append(INDENTATION).append(" * @return true if the operator is a postfix operator\n");
      code.append(INDENTATION).append(" */\n");
      code.append(INDENTATION)
          .append("public static boolean isPostfixOperator(String operator) {\n");
      code.append(DOUBLE_INDENTATION).append("return POSTFIX_OPERATORS.containsKey(operator);\n");
      code.append(INDENTATION).append("}\n\n");

      // Method to get the operator name for a character
      code.append(INDENTATION).append("/**\n");
      code.append(INDENTATION).append(" * Get the operator name for a character string.\n");
      code.append(INDENTATION).append(" * @param character The character string\n");
      code.append(INDENTATION).append(" * @return The operator name, or null if not found\n");
      code.append(INDENTATION).append(" */\n");
      code.append(INDENTATION)
          .append("public static String getOperatorForCharacter(String character) {\n");
      code.append(DOUBLE_INDENTATION).append("return CHARACTER_TO_OPERATOR.get(character);\n");
      code.append(INDENTATION).append("}\n");
    }

    private static String camelCase(String constantName) {
      StringBuilder result = new StringBuilder();
      boolean capitalizeNext = true;

      for (char c : constantName.toCharArray()) {
        if (c == '_') {
          capitalizeNext = true;
        } else if (capitalizeNext) {
          result.append(Character.toUpperCase(c));
          capitalizeNext = false;
        } else {
          result.append(Character.toLowerCase(c));
        }
      }

      return result.toString();
    }

    private static String escapeString(String str) {
      return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
          .replace("\r", "\\r").replace("\t", "\\t");
    }
  }

  /**
   * Generates Java source code for operator tables and writes it to the specified output directory.
   * 
   * @param compiledTables The compiled operator tables
   * @param outputDirectory The directory to write the generated source to
   * @param packageName The package name for the generated class
   * @throws IOException If an I/O error occurs during file writing
   */
  public static void generateOperatorTablesJavaSource(Map<String, Object> compiledTables,
      Path outputDirectory, String packageName) throws IOException {
    OperatorTablesCodeGenerator.generateOperatorTablesClass(compiledTables, outputDirectory,
        packageName);
  }

  public static void main(String[] args) throws IOException {
    // --- Argument Parsing ---
    String defaultDataDir = "data"; // Default data directory relative to execution path
    Path dataDirPath = Paths.get(args.length > 0 ? args[0] : defaultDataDir);
    Path outputPath = dataDirPath.resolve("operators.json");
    String userHome = System.getProperty("user.home");
    File outputSourceDir = new File(userHome
        + "/git/symja_android_library/symja_android_library/matheclipse-parser/src/main/java/org/matheclipse/parser/wma/tablegen");

    // Path outputSourceDir = null;
    String packageName = "org.matheclipse.parser.wma.tablegen";

    // // Parse arguments
    // for (int i = 0; i < args.length; i++) {
    // if (("-o".equals(args[i]) || "--output".equals(args[i])) && i + 1 < args.length) {
    // outputPath = Paths.get(args[i + 1]);
    // i++;
    // } else if (("-s".equals(args[i]) || "--source".equals(args[i])) && i + 1 < args.length) {
    // outputSourceDir = Paths.get(args[i + 1]);
    // i++;
    // } else if (("-p".equals(args[i]) || "--package".equals(args[i])) && i + 1 < args.length) {
    // packageName = args[i + 1];
    // i++;
    // }
    // }

    System.out.println("Using data directory: " + dataDirPath.toAbsolutePath());
    System.out.println("Outputting to file: " + outputPath.toAbsolutePath());

    Path operatorsYamlPath = dataDirPath.resolve("operators.yml");
    Path charactersYamlPath = dataDirPath.resolve("named-characters.yml");

    // --- File Loading and Processing ---
    Yaml yaml = new Yaml();
    Map<String, Object> operatorData;
    Map<String, Object> characterData;

    try (
        InputStream opInputStream =
            OperatorTableBuilder.class.getClassLoader().getResourceAsStream("data/operators.yml");
        InputStream charInputStream = OperatorTableBuilder.class.getClassLoader()
            .getResourceAsStream("data/named-characters.yml")) {

      operatorData = yaml.load(opInputStream);
      characterData = yaml.load(charInputStream);

    } catch (FileNotFoundException e) {
      System.err.println("Error: Could not find input YAML files.");
      System.err.println(
          "Please ensure '" + operatorsYamlPath + "' and '" + charactersYamlPath + "' exist.");
      System.err.println("You can specify a different data directory as the first argument.");
      return;
    }

    // Precompile the tables
    Map<String, Object> compiledData = compileTables(operatorData, characterData);
    System.out.println(compiledData.toString());

    // Generate Java source file if output source directory is specified
    if (outputSourceDir != null) {
      Path path = outputSourceDir.toPath();
      System.out.println("Generating Java source file in: " + path.toAbsolutePath());
      generateOperatorTablesJavaSource(compiledData, path, packageName);
      System.out.println("Successfully generated OperatorTables.java");
    } else {
      System.out.println("No output source directory specified, skipping Java source generation.");
    }

  }
}
