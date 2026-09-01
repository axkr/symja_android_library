package org.matheclipse.core.rubi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Convert the Rubi UtilityFunctions from <a href="https://rulebasedintegration.org/">Rubi -
 * Indefinite Integration Reduction Rules</a>
 */
public class ConvertRubiUtilityFunctions {
  private static final String HEADER = "package org.matheclipse.core.integrate.rubi;\n" + "\n"
      + "\n" + "import static org.matheclipse.core.expression.F.*;\n"
      + "import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;\n" + "\n"
      + "import org.matheclipse.core.interfaces.IAST;\n" + "/** \n"
      + " * UtilityFunctions rules from the <a href=\"https://rulebasedintegration.org/\">Rubi -\n"
      + " * rule-based integrator</a>.\n" + " *  \n" + " */\n" + "class UtilityFunctions";

  private static final String FOOTER = "}\n";

  private static int NUMBER_OF_RULES_PER_FILE = 20;

  /** Default Rubi input file inside the {@code Rubi} directory. */
  // the utility functions matching the integration rules selected in ConvertRubi
  private static final String DEFAULT_INPUT_FILE_NAME = "IntegrationUtilityFunctions4.16.0.m";
  // private static final String DEFAULT_INPUT_FILE_NAME = "IntegrationUtilityFunctions.m";

  /** Name of the serialized rules written next to the input file. */
  private static final String SERIALIZED_FILE_NAME = "IntegrationUtilityFunctions.ser";

  /** The generated files which are deleted before a new conversion is started. */
  private static final Pattern GENERATED_FILE_PATTERN =
      Pattern.compile("UtilityFunctions\\d+\\.java");

  public static List<ASTNode> parseFileToList(Path file) throws IOException {
    final StringBuffer buff = new StringBuffer(1024);
    appendFile(buff, file);
    String inputString = buff.toString();
    Parser p = new Parser(RubiASTNodeFactory.RUBI_STYLE_FACTORY, false, true);
    return p.parseScript(inputString);
  }

  private static void appendFile(StringBuffer buff, Path file) throws IOException {
    try (BufferedReader f = RubiConverterIO.newReader(file)) {
      String line;
      while ((line = f.readLine()) != null) {
        buff.append(line);
        buff.append('\n');
      }
    }
  }

  public static void writeFile(Path file, StringBuffer buffer) throws IOException {
    RubiConverterIO.writeFile(file, buffer);
  }

  public static void convert(ASTNode node, StringBuffer buffer, boolean last,
      Set<String> functionSet, IASTAppendable listOfRules) {
    try {
      // convert ASTNode to an IExpr node
      IExpr expr = new AST2Expr(false, EvalEngine.get()).convert(node);

      if (expr.isAST(S.CompoundExpression)) {
        IAST ast = (IAST) expr;
        ast.forEach(x -> convertExpr(x, buffer, last, functionSet, listOfRules));
      } else {
        convertExpr(expr, buffer, last, functionSet, listOfRules);
      }
    } catch (UnsupportedOperationException uoe) {
      System.out.println(uoe.getMessage());
      System.out.println(node.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void convertExpr(IExpr expr, StringBuffer buffer, boolean last,
      Set<String> functionSet, IASTAppendable listOfRules) {
    // ISymbol module = F.$s("Module");
    // if (expr.isFree(module, true)) {

    if (expr.isAST(S.SetDelayed, 3)) {
      IAST ast = (IAST) expr;
      addToFunctionSet(ast, functionSet);
      ConvertRubi.appendSetDelayedToBuffer(ast, buffer, last, listOfRules);
    } else if (expr.isAST(S.If, 4)) {
      IAST ast = (IAST) expr;
      if (ast.get(1).toString().equals("§showsteps")) {
        expr = ast.get(3);
        if (expr.isAST(S.SetDelayed, 3)) {
          ast = (IAST) expr;
          addToFunctionSet(ast, functionSet);
          ConvertRubi.appendSetDelayedToBuffer(ast, buffer, last, listOfRules);
        }
      }
    }
    // }
  }

  private static void addToFunctionSet(IAST ast, Set<String> functionSet) {
    if (ast.get(1).isAST()) {
      IAST lhsAST = (IAST) ast.arg1();
      if (lhsAST.head().isSymbol()) {
        ISymbol sym = (ISymbol) lhsAST.head();
        if (Character.isUpperCase(sym.toString().charAt(0))) {
          String entry = sym.toString() + "," + lhsAST.size();
          if (lhsAST.size() == 2 && lhsAST.get(1).isPatternSequence(false)) {
            entry = sym.toString() + "," + Integer.MAX_VALUE;
          }
          functionSet.add(entry);
        }
      }
    }
  }

  /**
   * Convert the Rubi utility functions into the {@code UtilityFunctions*.java} sources.
   *
   * @param args optional {@code <input-file> [<output-directory>]}, see {@link RubiConverterIO}
   */
  public static void main(String[] args) {
    try {
      generate(args);
    } catch (IOException | RuntimeException e) {
      System.err.println(">>>>> Rubi conversion FAILED: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static void generate(String[] args) throws IOException {
    Config.SERVER_MODE = false;
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
    Config.RUBI_CONVERT_SYMBOLS = true;
    EvalEngine engine = new EvalEngine(false);
    engine.getContextPath().add(org.matheclipse.core.expression.Context.RUBI);
    EvalEngine.set(engine);
    ConvertRubi.addPredefinedSymbols();

    IASTAppendable listOfRules = F.ListAlloc(10000);
    Path inputFile = RubiConverterIO.resolveInputFile(args, DEFAULT_INPUT_FILE_NAME);
    Path outputDirectory = RubiConverterIO.resolveOutputDirectory(args);

    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
    System.out.println(">>>>> File name:        " + inputFile);
    System.out.println(">>>>> Output directory: " + outputDirectory);
    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");

    // delete the previously generated files, so that a run which creates fewer files doesn't leave
    // stale rules behind
    RubiConverterIO.deleteGeneratedFiles(outputDirectory, GENERATED_FILE_PATTERN);

    StringBuffer buffer = new StringBuffer(100000);
    List<ASTNode> list = parseFileToList(inputFile);
    if (list != null) {
      int cnt = 0;
      int fcnt = 0;
      TreeSet<String> functionSet = new TreeSet<String>();
      TreeSet<String> uniqueFunctionSet = new TreeSet<String>();
      for (int j = 0; j < list.size(); j++) {
        if (cnt == 0) {
          buffer = new StringBuffer(100000);
          buffer.append(HEADER + fcnt + " { \n  public static IAST RULES = List( \n");
        }
        ASTNode astNode = list.get(j);

        cnt++;
        convert(astNode, buffer, cnt == NUMBER_OF_RULES_PER_FILE || j == list.size() - 1,
            functionSet, listOfRules);

        if (cnt == NUMBER_OF_RULES_PER_FILE) {
          RubiConverterIO.stripTrailingRuleSeparator(buffer);
          buffer.append("  );\n" + FOOTER);
          writeFile(outputDirectory.resolve("UtilityFunctions" + fcnt + ".java"), buffer);
          fcnt++;
          cnt = 0;
        }
      }
      if (cnt != 0) {
        // System.out.println(");");
        RubiConverterIO.stripTrailingRuleSeparator(buffer);
        buffer.append("  );\n" + FOOTER);
        writeFile(outputDirectory.resolve("UtilityFunctions" + fcnt + ".java"), buffer);
        fcnt++;
      }

      RubiConverterIO.writeRegistration(outputDirectory, "UtilityFunctions", fcnt,
          "getUtilityFunctionsRuleASTRubi45");

      buffer = new StringBuffer(100000);
      for (String str : functionSet) {
        String[] spl = str.split(",");
        String functionName = spl[0];
        uniqueFunctionSet.add(functionName);
      }
      for (String str : uniqueFunctionSet) {
        String functionName = str;
        buffer.append("    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(\"" + functionName
            + "\", Context.RUBI_STR + \"" + functionName + "\");\n");
      }
      System.out.println(buffer.toString());
      buffer = new StringBuffer(100000);
      for (String str : functionSet) {
        String[] spl = str.split(",");
        String functionName = spl[0];
        int numberOfArguments = Integer.valueOf(spl[1]).intValue() - 1;
        switch (numberOfArguments) {
          case 1:
            buffer.append("  public static IAST " + functionName + "(final IExpr a0) {\n");
            buffer.append("    return unary($rubi(\"" + functionName + "\"), a0);\n");
            buffer.append("  }\n\n");
            break;
          case 2:
            buffer.append(
                "  public static IAST " + functionName + "(final IExpr a0, final IExpr a1) {\n");
            buffer.append("    return binary($rubi(\"" + functionName + "\"), a0, a1);\n");
            buffer.append("  }\n\n");
            break;
          case 3:
            buffer.append("  public static IAST " + functionName
                + "(final IExpr a0, final IExpr a1, final IExpr a2) {\n");
            buffer.append("    return ternary($rubi(\"" + functionName + "\"), a0, a1, a2);\n");
            buffer.append("  }\n\n");
            break;
          case 4:
            buffer.append("  public static IAST " + functionName
                + "(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {\n");
            buffer.append(
                "    return quaternary($rubi(\"" + functionName + "\"), a0, a1, a2, a3);\n");
            buffer.append("  }\n\n");
            break;
          case 5:
            buffer.append("  public static IAST " + functionName
                + "(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4) {\n");
            buffer.append(
                "    return quinary($rubi(\"" + functionName + "\"), a0, a1, a2, a3, a4);\n");
            buffer.append("  }\n\n");
            break;
          case 6:
            buffer.append("  public static IAST " + functionName
                + "(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4, final IExpr a5) {\n");
            buffer.append(
                "    return senary($rubi(\"" + functionName + "\"), a0, a1, a2, a3, a4, a5);\n");
            buffer.append("  }\n\n");
            break;
          case Integer.MAX_VALUE:
            // MAX_VALUE indicates a left-hand-side form with a PetternSequence (i.e. f[x__]:=...)
            buffer.append("  public static IAST " + functionName + "(final IExpr... a) {\n");
            buffer.append("    return ast(a, $rubi(\"" + functionName + "\"));\n");
            buffer.append("  }\n\n");
            break;
          default:
            // System.out.println("ERROR in SWITCH:" + functionName + " - " + numberOfArguments);
            // public static IAST <functionName>(final IExpr... a) {
            // return ast(a, Part);
            // }
            buffer.append("  public static IAST " + functionName + "(final IExpr... a) {\n");
            buffer.append("    return ast(a, $rubi(\"" + functionName + "\"));\n");
            buffer.append("  }\n\n");
        }
      }
      System.out.println(buffer.toString());
      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
      System.out.println(">>>>> Number of entries: " + list.size());
      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");

      // for (int j = 0; j < list.size(); j++) {
      // System.out.println(list.get(j).toString());
      // }
    }
    // which built-in symbols are used how often?
    for (Map.Entry<String, Integer> entry : AST2Expr.RUBI_STATISTICS_MAP.entrySet()) {
      System.out.println(entry.getKey() + "  >>>  " + entry.getValue());
    }

    File file = RubiConverterIO.rubiDirectory().resolve(SERIALIZED_FILE_NAME).toFile();
    byte[] byteArray = WL.serialize(listOfRules);
    com.google.common.io.Files.write(byteArray, file);

    byteArray = com.google.common.io.Files.toByteArray(file);
    IExpr result = WL.deserialize(byteArray);
    System.out.println(result.toString());
  }
}
