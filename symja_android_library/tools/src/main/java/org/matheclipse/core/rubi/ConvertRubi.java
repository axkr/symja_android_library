package org.matheclipse.core.rubi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Convert the Rubi files from <a href="https://rulebasedintegration.org/">Rubi - Indefinite
 * Integration Reduction Rules</a>
 */
public class ConvertRubi {
  private static int GLOBAL_COUNTER = 0;
  private static final String HEADER =
      "package org.matheclipse.core.integrate.rubi;\n"
          + "\n"
          + "\n"
          + "import static org.matheclipse.core.expression.F.*;\n"
          + "import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;\n"
          + "import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;\n"
          + "import org.matheclipse.core.interfaces.IAST;\n"
          + "\n"
          + "/** \n"
          + " * IndefiniteIntegrationRules from the <a href=\"https://rulebasedintegration.org/\">Rubi -\n"
          + " * rule-based integrator</a>.\n"
          + " *  \n"
          + " */\n"
          + "class IntRules";

  private static final String FOOTER = "}\n";
  private static int NUMBER_OF_RULES_PER_FILE = 20;

  public static List<ASTNode> parseFileToList(String fileName) {
    try {
      File file = new File(fileName);
      final BufferedReader f = new BufferedReader(new FileReader(file));
      final StringBuffer buff = new StringBuffer(1024);
      String line;
      while ((line = f.readLine()) != null) {
        buff.append(line);
        buff.append('\n');
        // Insert newlines to let the parser see that a new rule starts
        buff.append('\n');
        buff.append('\n');
      }
      f.close();
      String inputString = buff.toString();
      Parser p = new Parser(RubiASTNodeFactory.RUBI_STYLE_FACTORY, false, true);
      return p.parsePackage(inputString);
      // return p.parsePackage(inputString);

      // assertEquals(obj.toString(),
      // "Plus[Plus[Times[-1, a], Times[-1, Times[b, Factorial2[c]]]], d]");
    } catch (Exception e) {
      e.printStackTrace();
      // assertEquals("", e.getMessage());
    }
    return null;
  }

  public static void writeFile(String fileName, StringBuffer buffer) {
    try {
      File file = new File(fileName);
      final BufferedWriter f = new BufferedWriter(new FileWriter(file));
      f.append(buffer);
      f.close();
    } catch (Exception e) {
      e.printStackTrace();
      // assertEquals("", e.getMessage());
    }
  }

  public static void convert(
      ASTNode node, StringBuffer buffer, boolean last, IASTAppendable listOfRules) {
    try {
      // convert ASTNode to an IExpr node

      IExpr expr = new AST2Expr().convert(node);
      if (expr.isAST(S.CompoundExpression, 3)) {
        IAST compoundExpressionAST = (IAST) expr;
        for (int i = 1; i < compoundExpressionAST.size(); i++) {
          expr = compoundExpressionAST.get(i);
          if (expr.isAST(S.SetDelayed, 3)) {
            IAST ast = (IAST) expr;
            appendSetDelayedToBuffer(ast, buffer, last, listOfRules);
          } else if (expr.isAST(S.If, 4)) {
            IAST ast = (IAST) expr;
            if (ast.get(1).toString().equals("§showsteps")) {
              expr = ast.get(3);
              if (expr.isAST(S.SetDelayed, 3)) {
                ast = (IAST) expr;
                appendSetDelayedToBuffer(ast, buffer, last, listOfRules);
              }
            }
          }
        }
      } else if (expr.isAST(S.SetDelayed, 3)) {
        IAST ast = (IAST) expr;
        appendSetDelayedToBuffer(ast, buffer, last, listOfRules);
      } else if (expr.isAST(S.If, 4)) {
        IAST ast = (IAST) expr;
        if (ast.get(1).toString().equals("§showsteps")) {
          expr = ast.get(3);
          if (expr.isAST(S.SetDelayed, 3)) {
            ast = (IAST) expr;
            appendSetDelayedToBuffer(ast, buffer, last, listOfRules);
          }
        }
      }
      // }
    } catch (UnsupportedOperationException uoe) {
      System.out.println(uoe.getMessage());
      System.out.println(node.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // public static IExpr replaceTrig(IExpr expr) {
  // // IAST astRules = F.List();
  // // astRules.add(F.Rule(F.$s("F"), F.$s("§f")));
  // // astRules.add(F.Rule(F.$s("G"), F.$s("§g")));
  // // astRules.add(F.Rule(F.$s("H"), F.$s("§h")));
  // // astRules.add(F.Rule(F.$s("sin"), F.$s("§sin")));
  // // astRules.add(F.Rule(F.$s("cos"), F.$s("§cos")));
  // // astRules.add(F.Rule(F.$s("tan"), F.$s("§tan")));
  // // astRules.add(F.Rule(F.$s("cot"), F.$s("§cot")));
  // // astRules.add(F.Rule(F.$s("csc"), F.$s("§csc")));
  // // astRules.add(F.Rule(F.$s("sec"), F.$s("§sec")));
  // // IExpr temp = expr.replaceAll(astRules);
  // // if (temp.isPresent()) {
  // // return temp;
  // // }
  // return expr;
  // }

  public static void appendSetDelayedToBuffer(
      IAST ast, StringBuffer buffer, boolean last, IASTAppendable listOfRules) {
    if (ast.get(1).topHead().toString().equalsIgnoreCase("Int")) {
      IAST integrate = (IAST) ast.get(1);
      if (integrate.get(1).isPlus()) {
        System.out.println(ast.toString());
        return;
      }
    }

    IExpr leftHandSide = ast.get(1);
    IExpr rightHandSide = ast.arg2();
    // if (!rightHandSide.isFree(x -> x.topHead().toString().equalsIgnoreCase("unintegrable"),
    // true)) {
    // System.out.println("IGNORED: " + ast.toString());
    // } else {

    GLOBAL_COUNTER++;
    // System.out.println(leftHandSide.toString());
    if (ast.get(1).isAST()) {
      // leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, EvalEngine.get());
      IExpr temp = leftHandSide;

      if (leftHandSide.topHead().toString().equalsIgnoreCase("int")) {
        if (temp.size() == 3) {
          ((IASTMutable) temp).set(0, S.Integrate);
        }
        buffer.append("IIntegrate(" + GLOBAL_COUNTER + ",");
      } else {
        try {
          leftHandSide = EvalEngine.get().evalHoldPattern((IAST) temp);
        } catch (Exception ex) {
          System.out.println("GLOBAL_COUNTER: " + GLOBAL_COUNTER);
          ex.printStackTrace();
          leftHandSide = temp;
        }
        buffer.append("ISetDelayed(" + GLOBAL_COUNTER + ",");
      }
    } else {
      buffer.append("ISetDelayed(" + GLOBAL_COUNTER + ",");
    }
    buffer.append(leftHandSide.internalFormString(true, 0));
    buffer.append(",\n    ");

    // if (rightHandSide.topHead().toString().equalsIgnoreCase("CompoundExpression")) {
    // System.out.println(rightHandSide.toString());
    // }
    ISymbol s = F.symbol("Int");
    IExpr temp = ast.arg2().replaceAll(F.Rule(s, S.Integrate)); // F.$s("AbortRubi")));
    if (temp.isPresent()) {
      rightHandSide = temp;
    }
    // for Rubi patternExpression must be set to true in internalFormString() for right-hand-side
    buffer.append(rightHandSide.internalFormString(true, 0));
    if (last) {
      buffer.append(")\n");
    } else {
      buffer.append("),\n");
    }
    // }
    listOfRules.append(F.List(leftHandSide, rightHandSide));
  }

  public static void addPredefinedSymbols() {
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AbortRubi", Context.RUBI_STR + "AbortRubi");

    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AbsorbMinusSign", Context.RUBI_STR + "AbsorbMinusSign");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "AbsurdNumberFactors", Context.RUBI_STR + "AbsurdNumberFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AbsurdNumberGCD", Context.RUBI_STR + "AbsurdNumberGCD");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "AbsurdNumberGCDList", Context.RUBI_STR + "AbsurdNumberGCDList");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AbsurdNumberQ", Context.RUBI_STR + "AbsurdNumberQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ActivateTrig", Context.RUBI_STR + "ActivateTrig");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "AlgebraicFunctionFactors", Context.RUBI_STR + "AlgebraicFunctionFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "AlgebraicFunctionQ", Context.RUBI_STR + "AlgebraicFunctionQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "AlgebraicTrigFunctionQ", Context.RUBI_STR + "AlgebraicTrigFunctionQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AllNegTermQ", Context.RUBI_STR + "AllNegTermQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AtomBaseQ", Context.RUBI_STR + "AtomBaseQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("BinomialDegree", Context.RUBI_STR + "BinomialDegree");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("BinomialMatchQ", Context.RUBI_STR + "BinomialMatchQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("BinomialParts", Context.RUBI_STR + "BinomialParts");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("BinomialQ", Context.RUBI_STR + "BinomialQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("BinomialTest", Context.RUBI_STR + "BinomialTest");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CalculusFreeQ", Context.RUBI_STR + "CalculusFreeQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CalculusQ", Context.RUBI_STR + "CalculusQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "CancelCommonFactors", Context.RUBI_STR + "CancelCommonFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CannotIntegrate", Context.RUBI_STR + "CannotIntegrate");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "CollectReciprocals", Context.RUBI_STR + "CollectReciprocals");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Coeff", Context.RUBI_STR + "Coeff");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "CombineExponents", Context.RUBI_STR + "CombineExponents");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CommonFactors", Context.RUBI_STR + "CommonFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "CommonNumericFactors", Context.RUBI_STR + "CommonNumericFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ComplexFreeQ", Context.RUBI_STR + "ComplexFreeQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ComplexNumberQ", Context.RUBI_STR + "ComplexNumberQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ConstantFactor", Context.RUBI_STR + "ConstantFactor");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ContentFactor", Context.RUBI_STR + "ContentFactor");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "ContentFactorAux", Context.RUBI_STR + "ContentFactorAux");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CosQ", Context.RUBI_STR + "CosQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CoshQ", Context.RUBI_STR + "CoshQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CotQ", Context.RUBI_STR + "CotQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CothQ", Context.RUBI_STR + "CothQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CscQ", Context.RUBI_STR + "CscQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CschQ", Context.RUBI_STR + "CschQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CubicMatchQ", Context.RUBI_STR + "CubicMatchQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("DeactivateTrig", Context.RUBI_STR + "DeactivateTrig");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "DeactivateTrigAux", Context.RUBI_STR + "DeactivateTrigAux");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "DerivativeDivides", Context.RUBI_STR + "DerivativeDivides");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Distrib", Context.RUBI_STR + "Distrib");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "DistributeDegree", Context.RUBI_STR + "DistributeDegree");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "DivideDegreesOfFactors", Context.RUBI_STR + "DivideDegreesOfFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Divides", Context.RUBI_STR + "Divides");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("EasyDQ", Context.RUBI_STR + "EasyDQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("EqQ", Context.RUBI_STR + "EqQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("EulerIntegrandQ", Context.RUBI_STR + "EulerIntegrandQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("EvenQuotientQ", Context.RUBI_STR + "EvenQuotientQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("EveryQ", Context.RUBI_STR + "EveryQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpQ", Context.RUBI_STR + "ExpQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "ExpandAlgebraicFunction", Context.RUBI_STR + "ExpandAlgebraicFunction");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandBinomial", Context.RUBI_STR + "ExpandBinomial");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandCleanup", Context.RUBI_STR + "ExpandCleanup");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "ExpandExpression", Context.RUBI_STR + "ExpandExpression");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandIntegrand", Context.RUBI_STR + "ExpandIntegrand");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "ExpandLinearProduct", Context.RUBI_STR + "ExpandLinearProduct");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandToSum", Context.RUBI_STR + "ExpandToSum");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandTrig", Context.RUBI_STR + "ExpandTrig");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "ExpandTrigExpand", Context.RUBI_STR + "ExpandTrigExpand");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "ExpandTrigReduce", Context.RUBI_STR + "ExpandTrigReduce");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "ExpandTrigReduceAux", Context.RUBI_STR + "ExpandTrigReduceAux");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandTrigToExp", Context.RUBI_STR + "ExpandTrigToExp");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Expon", Context.RUBI_STR + "Expon");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExponentIn", Context.RUBI_STR + "ExponentIn");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExponentInAux", Context.RUBI_STR + "ExponentInAux");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FactorAbsurdNumber", Context.RUBI_STR + "FactorAbsurdNumber");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FactorNumericGcd", Context.RUBI_STR + "FactorNumericGcd");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FactorOrder", Context.RUBI_STR + "FactorOrder");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FalseQ", Context.RUBI_STR + "FalseQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FindTrigFactor", Context.RUBI_STR + "FindTrigFactor");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FixInertTrigFunction", Context.RUBI_STR + "FixInertTrigFunction");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FixIntRule", Context.RUBI_STR + "FixIntRule");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FixIntRules", Context.RUBI_STR + "FixIntRules");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FixRhsIntRule", Context.RUBI_STR + "FixRhsIntRule");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FixSimplify", Context.RUBI_STR + "FixSimplify");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FracPart", Context.RUBI_STR + "FracPart");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FractionOrNegativeQ", Context.RUBI_STR + "FractionOrNegativeQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FractionQ", Context.RUBI_STR + "FractionQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FractionalPowerFreeQ", Context.RUBI_STR + "FractionalPowerFreeQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FractionalPowerOfLinear", Context.RUBI_STR + "FractionalPowerOfLinear");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FractionalPowerOfQuotientOfLinears",
        Context.RUBI_STR + "FractionalPowerOfQuotientOfLinears");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FractionalPowerOfSquareQ", Context.RUBI_STR + "FractionalPowerOfSquareQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FractionalPowerQ", Context.RUBI_STR + "FractionalPowerQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FractionalPowerSubexpressionQ", Context.RUBI_STR + "FractionalPowerSubexpressionQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FreeFactors", Context.RUBI_STR + "FreeFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FreeTerms", Context.RUBI_STR + "FreeTerms");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfCosQ", Context.RUBI_STR + "FunctionOfCosQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfCoshQ", Context.RUBI_STR + "FunctionOfCoshQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfDensePolynomialsQ", Context.RUBI_STR + "FunctionOfDensePolynomialsQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfExpnQ", Context.RUBI_STR + "FunctionOfExpnQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfExponential", Context.RUBI_STR + "FunctionOfExponential");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfExponentialFunction", Context.RUBI_STR + "FunctionOfExponentialFunction");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfExponentialFunctionAux", Context.RUBI_STR + "FunctionOfExponentialFunctionAux");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfExponentialQ", Context.RUBI_STR + "FunctionOfExponentialQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfExponentialTest", Context.RUBI_STR + "FunctionOfExponentialTest");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfExponentialTestAux", Context.RUBI_STR + "FunctionOfExponentialTestAux");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfHyperbolic", Context.RUBI_STR + "FunctionOfHyperbolic");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfHyperbolicQ", Context.RUBI_STR + "FunctionOfHyperbolicQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfInverseLinear", Context.RUBI_STR + "FunctionOfInverseLinear");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfLinear", Context.RUBI_STR + "FunctionOfLinear");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfLinearSubst", Context.RUBI_STR + "FunctionOfLinearSubst");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfLog", Context.RUBI_STR + "FunctionOfLog");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfQ", Context.RUBI_STR + "FunctionOfQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfSinQ", Context.RUBI_STR + "FunctionOfSinQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfSinhQ", Context.RUBI_STR + "FunctionOfSinhQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfSquareRootOfQuadratic", Context.RUBI_STR + "FunctionOfSquareRootOfQuadratic");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfTanQ", Context.RUBI_STR + "FunctionOfTanQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfTanWeight", Context.RUBI_STR + "FunctionOfTanWeight");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfTanhQ", Context.RUBI_STR + "FunctionOfTanhQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfTanhWeight", Context.RUBI_STR + "FunctionOfTanhWeight");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfTrig", Context.RUBI_STR + "FunctionOfTrig");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "FunctionOfTrigOfLinearQ", Context.RUBI_STR + "FunctionOfTrigOfLinearQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfTrigQ", Context.RUBI_STR + "FunctionOfTrigQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GE", Context.RUBI_STR + "GE");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GT", Context.RUBI_STR + "GT");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Gcd", Context.RUBI_STR + "Gcd");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "GeneralizedBinomialDegree", Context.RUBI_STR + "GeneralizedBinomialDegree");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "GeneralizedBinomialMatchQ", Context.RUBI_STR + "GeneralizedBinomialMatchQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "GeneralizedBinomialParts", Context.RUBI_STR + "GeneralizedBinomialParts");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "GeneralizedBinomialQ", Context.RUBI_STR + "GeneralizedBinomialQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "GeneralizedBinomialTest", Context.RUBI_STR + "GeneralizedBinomialTest");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "GeneralizedTrinomialDegree", Context.RUBI_STR + "GeneralizedTrinomialDegree");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "GeneralizedTrinomialMatchQ", Context.RUBI_STR + "GeneralizedTrinomialMatchQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "GeneralizedTrinomialParts", Context.RUBI_STR + "GeneralizedTrinomialParts");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "GeneralizedTrinomialQ", Context.RUBI_STR + "GeneralizedTrinomialQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "GeneralizedTrinomialTest", Context.RUBI_STR + "GeneralizedTrinomialTest");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GensymSubst", Context.RUBI_STR + "GensymSubst");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("HalfIntegerQ", Context.RUBI_STR + "HalfIntegerQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("HeldFormQ", Context.RUBI_STR + "HeldFormQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("HyperbolicQ", Context.RUBI_STR + "HyperbolicQ");

    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IGtQ", Context.RUBI_STR + "IGtQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ILtQ", Context.RUBI_STR + "ILtQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IGeQ", Context.RUBI_STR + "IGeQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IGtQ", Context.RUBI_STR + "IGtQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ILeQ", Context.RUBI_STR + "ILeQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GtQ", Context.RUBI_STR + "GtQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LtQ", Context.RUBI_STR + "LtQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LeQ", Context.RUBI_STR + "LeQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GeQ", Context.RUBI_STR + "GeQ");

    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "ImaginaryNumericQ", Context.RUBI_STR + "ImaginaryNumericQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ImaginaryQ", Context.RUBI_STR + "ImaginaryQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IndependentQ", Context.RUBI_STR + "IndependentQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "InertReciprocalQ", Context.RUBI_STR + "InertReciprocalQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("InertTrigFreeQ", Context.RUBI_STR + "InertTrigFreeQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("InertTrigQ", Context.RUBI_STR + "InertTrigQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("InertTrigSumQ", Context.RUBI_STR + "InertTrigSumQ");

    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntBinomialQ", Context.RUBI_STR + "IntBinomialQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Integral", Context.RUBI_STR + "Integral");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntHide", Context.RUBI_STR + "IntSum");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntLinearQ", Context.RUBI_STR + "IntLinearQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntPart", Context.RUBI_STR + "IntPart");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntQuadraticQ", Context.RUBI_STR + "IntQuadraticQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntSum", Context.RUBI_STR + "IntSum");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntTerm", Context.RUBI_STR + "IntTerm");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntegerPowerQ", Context.RUBI_STR + "IntegerPowerQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "IntegerQuotientQ", Context.RUBI_STR + "IntegerQuotientQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntegersQ", Context.RUBI_STR + "IntegersQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntegralFreeQ", Context.RUBI_STR + "IntegralFreeQ");

    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "InverseFunctionFreeQ", Context.RUBI_STR + "InverseFunctionFreeQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "InverseFunctionOfLinear", Context.RUBI_STR + "InverseFunctionOfLinear");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "InverseFunctionOfQuotientOfLinears",
        Context.RUBI_STR + "InverseFunctionOfQuotientOfLinears");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "InverseFunctionQ", Context.RUBI_STR + "InverseFunctionQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "InverseHyperbolicQ", Context.RUBI_STR + "InverseHyperbolicQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("InverseTrigQ", Context.RUBI_STR + "InverseTrigQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("KernelSubst", Context.RUBI_STR + "KernelSubst");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "KnownCotangentIntegrandQ", Context.RUBI_STR + "KnownCotangentIntegrandQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "KnownSecantIntegrandQ", Context.RUBI_STR + "KnownSecantIntegrandQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "KnownSineIntegrandQ", Context.RUBI_STR + "KnownSineIntegrandQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "KnownTangentIntegrandQ", Context.RUBI_STR + "KnownTangentIntegrandQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "KnownTrigIntegrandQ", Context.RUBI_STR + "KnownTrigIntegrandQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LE", Context.RUBI_STR + "LE");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LT", Context.RUBI_STR + "LT");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LeadBase", Context.RUBI_STR + "LeadBase");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LeadDegree", Context.RUBI_STR + "LeadDegree");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LeadFactor", Context.RUBI_STR + "LeadFactor");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LeadTerm", Context.RUBI_STR + "LeadTerm");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LinearMatchQ", Context.RUBI_STR + "LinearMatchQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LinearPairQ", Context.RUBI_STR + "LinearPairQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LinearQ", Context.RUBI_STR + "LinearQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LogQ", Context.RUBI_STR + "LogQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MakeAssocList", Context.RUBI_STR + "MakeAssocList");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Map2", Context.RUBI_STR + "Map2");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MapAnd", Context.RUBI_STR + "MapAnd");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MapOr", Context.RUBI_STR + "MapOr");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MergeFactor", Context.RUBI_STR + "MergeFactor");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MergeFactors", Context.RUBI_STR + "MergeFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MergeMonomials", Context.RUBI_STR + "MergeMonomials");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "MergeableFactorQ", Context.RUBI_STR + "MergeableFactorQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MinimumDegree", Context.RUBI_STR + "MinimumDegree");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "MinimumMonomialExponent", Context.RUBI_STR + "MinimumMonomialExponent");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "MonomialExponent", Context.RUBI_STR + "MonomialExponent");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MonomialFactor", Context.RUBI_STR + "MonomialFactor");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MonomialQ", Context.RUBI_STR + "MonomialQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MonomialSumQ", Context.RUBI_STR + "MonomialSumQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "MostMainFactorPosition", Context.RUBI_STR + "MostMainFactorPosition");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NegQ", Context.RUBI_STR + "NegQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NegativeCoefficientQ", Context.RUBI_STR + "NegativeCoefficientQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NegativeIntegerQ", Context.RUBI_STR + "NegativeIntegerQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NegativeOrZeroQ", Context.RUBI_STR + "NegativeOrZeroQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NegativeQ", Context.RUBI_STR + "NegativeQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NegSumBaseQ", Context.RUBI_STR + "NegSumBaseQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NeQ", Context.RUBI_STR + "NeQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NiceSqrtAuxQ", Context.RUBI_STR + "NiceSqrtAuxQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NiceSqrtQ", Context.RUBI_STR + "NiceSqrtQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NonabsurdNumberFactors", Context.RUBI_STR + "NonabsurdNumberFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NonalgebraicFunctionFactors", Context.RUBI_STR + "NonalgebraicFunctionFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NonfreeFactors", Context.RUBI_STR + "NonfreeFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NonfreeTerms", Context.RUBI_STR + "NonfreeTerms");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NonnumericFactors", Context.RUBI_STR + "NonnumericFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NonpolynomialTerms", Context.RUBI_STR + "NonpolynomialTerms");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NonpositiveFactors", Context.RUBI_STR + "NonpositiveFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NonrationalFunctionFactors", Context.RUBI_STR + "NonrationalFunctionFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NonsumQ", Context.RUBI_STR + "NonsumQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NonzeroQ", Context.RUBI_STR + "NonzeroQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NormalizeHyperbolic", Context.RUBI_STR + "NormalizeHyperbolic");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NormalizeIntegrand", Context.RUBI_STR + "NormalizeIntegrand");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NormalizeIntegrandAux", Context.RUBI_STR + "NormalizeIntegrandAux");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NormalizeIntegrandFactor", Context.RUBI_STR + "NormalizeIntegrandFactor");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NormalizeIntegrandFactorBase", Context.RUBI_STR + "NormalizeIntegrandFactorBase");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NormalizeLeadTermSigns", Context.RUBI_STR + "NormalizeLeadTermSigns");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NormalizePowerOfLinear", Context.RUBI_STR + "NormalizePowerOfLinear");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NormalizePseudoBinomial", Context.RUBI_STR + "NormalizePseudoBinomial");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NormalizeSumFactors", Context.RUBI_STR + "NormalizeSumFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NormalizeTogether", Context.RUBI_STR + "NormalizeTogether");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NormalizeTrig", Context.RUBI_STR + "NormalizeTrig");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "NormalizeTrigReduce", Context.RUBI_STR + "NormalizeTrigReduce");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NotFalseQ", Context.RUBI_STR + "NotFalseQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NotIntegrableQ", Context.RUBI_STR + "NotIntegrableQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NthRoot", Context.RUBI_STR + "NthRoot");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NumericFactor", Context.RUBI_STR + "NumericFactor");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "OddHyperbolicPowerQ", Context.RUBI_STR + "OddHyperbolicPowerQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("OddQuotientQ", Context.RUBI_STR + "OddQuotientQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("OddTrigPowerQ", Context.RUBI_STR + "OddTrigPowerQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("OneQ", Context.RUBI_STR + "OneQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PerfectPowerTest", Context.RUBI_STR + "PerfectPowerTest");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PerfectSquareQ", Context.RUBI_STR + "PerfectSquareQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PiecewiseLinearQ", Context.RUBI_STR + "PiecewiseLinearQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PolyQ", Context.RUBI_STR + "PolyQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PolyGCD", Context.RUBI_STR + "PolyGCD");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PolynomialDivide", Context.RUBI_STR + "PolynomialDivide");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PolynomialInAuxQ", Context.RUBI_STR + "PolynomialInAuxQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PolynomialInQ", Context.RUBI_STR + "PolynomialInQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PolynomialInSubst", Context.RUBI_STR + "PolynomialInSubst");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PolynomialInSubstAux", Context.RUBI_STR + "PolynomialInSubstAux");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PolynomialTermQ", Context.RUBI_STR + "PolynomialTermQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PolynomialTerms", Context.RUBI_STR + "PolynomialTerms");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PosAux", Context.RUBI_STR + "PosAux");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PosQ", Context.RUBI_STR + "PosQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PositiveFactors", Context.RUBI_STR + "PositiveFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PositiveIntegerPowerQ", Context.RUBI_STR + "PositiveIntegerPowerQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PositiveIntegerQ", Context.RUBI_STR + "PositiveIntegerQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PositiveOrZeroQ", Context.RUBI_STR + "PositiveOrZeroQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PositiveQ", Context.RUBI_STR + "PositiveQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PowerOfInertTrigSumQ", Context.RUBI_STR + "PowerOfInertTrigSumQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PowerOfLinearMatchQ", Context.RUBI_STR + "PowerOfLinearMatchQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PowerOfLinearQ", Context.RUBI_STR + "PowerOfLinearQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PowerQ", Context.RUBI_STR + "PowerQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PowerVariableDegree", Context.RUBI_STR + "PowerVariableDegree");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PowerVariableExpn", Context.RUBI_STR + "PowerVariableExpn");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PowerVariableSubst", Context.RUBI_STR + "PowerVariableSubst");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "ProductOfLinearPowersQ", Context.RUBI_STR + "ProductOfLinearPowersQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ProductQ", Context.RUBI_STR + "ProductQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ProperPolyQ", Context.RUBI_STR + "ProperPolyQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PseudoBinomialPairQ", Context.RUBI_STR + "PseudoBinomialPairQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PseudoBinomialQ", Context.RUBI_STR + "PseudoBinomialQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PseudoBinomialParts", Context.RUBI_STR + "PseudoBinomialParts");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PureFunctionOfCosQ", Context.RUBI_STR + "PureFunctionOfCosQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PureFunctionOfCoshQ", Context.RUBI_STR + "PureFunctionOfCoshQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PureFunctionOfCotQ", Context.RUBI_STR + "PureFunctionOfCotQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PureFunctionOfCothQ", Context.RUBI_STR + "PureFunctionOfCothQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PureFunctionOfSinQ", Context.RUBI_STR + "PureFunctionOfSinQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PureFunctionOfSinhQ", Context.RUBI_STR + "PureFunctionOfSinhQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PureFunctionOfTanQ", Context.RUBI_STR + "PureFunctionOfTanQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "PureFunctionOfTanhQ", Context.RUBI_STR + "PureFunctionOfTanhQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("QuadraticMatchQ", Context.RUBI_STR + "QuadraticMatchQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("QuadraticQ", Context.RUBI_STR + "QuadraticQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "QuotientOfLinearsMatchQ", Context.RUBI_STR + "QuotientOfLinearsMatchQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "QuotientOfLinearsP", Context.RUBI_STR + "QuotientOfLinearsP");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "QuotientOfLinearsParts", Context.RUBI_STR + "QuotientOfLinearsParts");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "QuotientOfLinearsQ", Context.RUBI_STR + "QuotientOfLinearsQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "QuadraticProductQ", Context.RUBI_STR + "QuadraticProductQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "RationalFunctionExpand", Context.RUBI_STR + "RationalFunctionExpand");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "RationalFunctionExponents", Context.RUBI_STR + "RationalFunctionExponents");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "RationalFunctionFactors", Context.RUBI_STR + "RationalFunctionFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "RationalFunctionQ", Context.RUBI_STR + "RationalFunctionQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RationalPowerQ", Context.RUBI_STR + "RationalPowerQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RationalQ", Context.RUBI_STR + "RationalQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RealNumericQ", Context.RUBI_STR + "RealNumericQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RealQ", Context.RUBI_STR + "RealQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ReapList", Context.RUBI_STR + "ReapList");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "RectifyCotangent", Context.RUBI_STR + "RectifyCotangent");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RectifyTangent", Context.RUBI_STR + "RectifyTangent");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ReduceInertTrig", Context.RUBI_STR + "ReduceInertTrig");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "RemainingFactors", Context.RUBI_STR + "RemainingFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RemainingTerms", Context.RUBI_STR + "RemainingTerms");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RemoveContent", Context.RUBI_STR + "RemoveContent");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "RemoveContentAux", Context.RUBI_STR + "RemoveContentAux");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Rt", Context.RUBI_STR + "Rt");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RuleName", Context.RUBI_STR + "RuleName");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RtAux", Context.RUBI_STR + "RtAux");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SecQ", Context.RUBI_STR + "SecQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SechQ", Context.RUBI_STR + "SechQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SignOfFactor", Context.RUBI_STR + "SignOfFactor");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Simp", Context.RUBI_STR + "Simp");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SimpFixFactor", Context.RUBI_STR + "SimpFixFactor");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SimpHelp", Context.RUBI_STR + "SimpHelp");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SimplerIntegrandQ", Context.RUBI_STR + "SimplerIntegrandQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SimplerQ", Context.RUBI_STR + "SimplerQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SimplerSqrtQ", Context.RUBI_STR + "SimplerSqrtQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SimplifyAntiderivative", Context.RUBI_STR + "SimplifyAntiderivative");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SimplifyAntiderivativeSum", Context.RUBI_STR + "SimplifyAntiderivativeSum");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SimplifyIntegrand", Context.RUBI_STR + "SimplifyIntegrand");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SimplifyTerm", Context.RUBI_STR + "SimplifyTerm");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SinCosQ", Context.RUBI_STR + "SinCosQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SinQ", Context.RUBI_STR + "SinQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SinhCoshQ", Context.RUBI_STR + "SinhCoshQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SinhQ", Context.RUBI_STR + "SinhQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Smallest", Context.RUBI_STR + "Smallest");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SmartApart", Context.RUBI_STR + "SmartApart");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SmartDenominator", Context.RUBI_STR + "SmartDenominator");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SmartNumerator", Context.RUBI_STR + "SmartNumerator");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SmartSimplify", Context.RUBI_STR + "SmartSimplify");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SomeNegTermQ", Context.RUBI_STR + "SomeNegTermQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SplitFreeFactors", Context.RUBI_STR + "SplitFreeFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SplitProduct", Context.RUBI_STR + "SplitProduct");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SplitSum", Context.RUBI_STR + "SplitSum");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SqrtNumberQ", Context.RUBI_STR + "SqrtNumberQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SqrtNumberSumQ", Context.RUBI_STR + "SqrtNumberSumQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SqrtQ", Context.RUBI_STR + "SqrtQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SquareRootOfQuadraticSubst", Context.RUBI_STR + "SquareRootOfQuadraticSubst");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("StopFunctionQ", Context.RUBI_STR + "StopFunctionQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Subst", Context.RUBI_STR + "Subst");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstAux", Context.RUBI_STR + "SubstAux");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstFor", Context.RUBI_STR + "SubstFor");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstForAux", Context.RUBI_STR + "SubstForAux");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstForExpn", Context.RUBI_STR + "SubstForExpn");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SubstForFractionalPower", Context.RUBI_STR + "SubstForFractionalPower");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SubstForFractionalPowerAuxQ", Context.RUBI_STR + "SubstForFractionalPowerAuxQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SubstForFractionalPowerOfLinear", Context.RUBI_STR + "SubstForFractionalPowerOfLinear");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SubstForFractionalPowerOfQuotientOfLinears",
        Context.RUBI_STR + "SubstForFractionalPowerOfQuotientOfLinears");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SubstForFractionalPowerQ", Context.RUBI_STR + "SubstForFractionalPowerQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SubstForHyperbolic", Context.RUBI_STR + "SubstForHyperbolic");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SubstForInverseFunction", Context.RUBI_STR + "SubstForInverseFunction");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "SubstForInverseFunctionOfQuotientOfLinears",
        Context.RUBI_STR + "SubstForInverseFunctionOfQuotientOfLinears");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstForTrig", Context.RUBI_STR + "SubstForTrig");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SumBaseQ", Context.RUBI_STR + "SumBaseQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SumQ", Context.RUBI_STR + "SumQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SumSimplerAuxQ", Context.RUBI_STR + "SumSimplerAuxQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SumSimplerQ", Context.RUBI_STR + "SumSimplerQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TanQ", Context.RUBI_STR + "TanQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TanhQ", Context.RUBI_STR + "TanhQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "TogetherSimplify", Context.RUBI_STR + "TogetherSimplify");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "TrigHyperbolicFreeQ", Context.RUBI_STR + "TrigHyperbolicFreeQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrigQ", Context.RUBI_STR + "TrigQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrigSimplify", Context.RUBI_STR + "TrigSimplify");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrigSimplifyAux", Context.RUBI_STR + "TrigSimplifyAux");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrigSimplifyQ", Context.RUBI_STR + "TrigSimplifyQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "TrigSimplifyRecur", Context.RUBI_STR + "TrigSimplifyRecur");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrigSquare", Context.RUBI_STR + "TrigSquare");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrigSquareQ", Context.RUBI_STR + "TrigSquareQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrinomialDegree", Context.RUBI_STR + "TrinomialDegree");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrinomialMatchQ", Context.RUBI_STR + "TrinomialMatchQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrinomialParts", Context.RUBI_STR + "TrinomialParts");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrinomialQ", Context.RUBI_STR + "TrinomialQ");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrinomialTest", Context.RUBI_STR + "TrinomialTest");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TryPureTanSubst", Context.RUBI_STR + "TryPureTanSubst");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "TryPureTanhSubst", Context.RUBI_STR + "TryPureTanhSubst");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TryTanhSubst", Context.RUBI_STR + "TryTanhSubst");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "UnifyInertTrigFunction", Context.RUBI_STR + "UnifyInertTrigFunction");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put(
        "UnifyNegativeBaseFactors", Context.RUBI_STR + "UnifyNegativeBaseFactors");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("UnifySum", Context.RUBI_STR + "UnifySum");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("UnifyTerm", Context.RUBI_STR + "UnifyTerm");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("UnifyTerms", Context.RUBI_STR + "UnifyTerms");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Unintegrable", Context.RUBI_STR + "Unintegrable");
    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ZeroQ", Context.RUBI_STR + "ZeroQ");

    F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Dist", Context.RUBI_STR + "Dist");
  }

  public static void main(String[] args) {
    Config.SERVER_MODE = false;
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
    Config.RUBI_CONVERT_SYMBOLS = true;
    EvalEngine engine = new EvalEngine(false);
    engine.getContextPath().add(org.matheclipse.core.expression.Context.RUBI);
    //    engine.beginPackage(org.matheclipse.core.expression.Context.RUBI_STR);
    EvalEngine.set(engine);
    addPredefinedSymbols();
    // use same order as in Rubi.m
    String[] fileNames = { //
      "./Rubi/RubiRules4.16.0_FullLHS.m",
    };
    IASTAppendable listOfRules = F.ListAlloc(10000);
    int fcnt = 0;

    for (int i = 0; i < fileNames.length; i++) {

      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
      System.out.println(">>>>> File name: " + fileNames[i]);
      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");

      StringBuffer buffer = new StringBuffer(100000);
      List<ASTNode> list = parseFileToList(fileNames[i]);
      // String currentFilename = fileNames[i].substring(fileNames[i].lastIndexOf('/'),
      // fileNames[i].length() -
      // 2);
      if (list != null) {
        int cnt = 0;
        for (int j = 0; j < list.size(); j++) {
          if (cnt == 0) {
            buffer = new StringBuffer(100000);
            buffer.append(HEADER + fcnt + " { \n  public static IAST RULES = List( \n");
          }
          ASTNode astNode = list.get(j);
          cnt++;
          convert(
              astNode,
              buffer,
              cnt == NUMBER_OF_RULES_PER_FILE || j == list.size() - 1,
              listOfRules);

          if (cnt == NUMBER_OF_RULES_PER_FILE) {
            buffer.append("  );\n" + FOOTER);
            writeFile("C:/temp/rubi/IntRules" + fcnt + ".java", buffer);
            fcnt++;
            cnt = 0;
          }
        }
        if (cnt != 0) {
          // System.out.println(");");
          buffer.append("  );\n" + FOOTER);
          writeFile("C:/temp/rubi/IntRules" + fcnt + ".java", buffer);
          fcnt++;
          cnt = 0;
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(">>>>> Number of entries: " + list.size());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
      }
    }
    // which built-in symbols are used how often?
    for (Map.Entry<String, Integer> entry : AST2Expr.RUBI_STATISTICS_MAP.entrySet()) {
      System.out.println(entry.getKey() + "  >>>  " + entry.getValue());
    }

    //    File file = new File("./Rubi/IntegrateRules.bin");
    //
    //    byte[] byteArray = WL.serializeInternal(listOfRules);
    //    try {
    //
    //      System.out.println("Creating binary file:" + file.toString());
    //      com.google.common.io.Files.write(byteArray, file);
    //
    //      byteArray = com.google.common.io.Files.toByteArray(file);
    //      IExpr result = WL.deserializeInternal(byteArray);
    //      //      System.out.println(result.toString());
    //    } catch (IOException e) {
    //      // TODO Auto-generated catch block
    //      e.printStackTrace();
    //    }
  }
}
