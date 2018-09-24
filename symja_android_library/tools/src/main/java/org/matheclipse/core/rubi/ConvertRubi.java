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
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Convert the Rubi files from <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - Indefinite Integration Reduction
 * Rules</a>
 * 
 */
public class ConvertRubi {
	private static int GLOBAL_COUNTER = 0;
	private static final String HEADER = "package org.matheclipse.core.integrate.rubi;\n" + "\n" + "\n"
			+ "import static org.matheclipse.core.expression.F.*;\n"
			+ "import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;\n"
			+ "import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;\n"
			+ "import org.matheclipse.core.interfaces.IAST;\n" + "\n" + "/** \n"
			+ " * IndefiniteIntegrationRules from the <a href=\"http://www.apmaths.uwo.ca/~arich/\">Rubi -\n"
			+ " * rule-based integrator</a>.\n" + " *  \n" + " */\n" + "public class IntRules";

	private static final String FOOTER = "}\n";
	private static int NUMBER_OF_RULES_PER_FILE = 50;

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
			Parser p = new Parser(false, true);
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

	public static void convert(ASTNode node, StringBuffer buffer, boolean last) {
		try {
			// convert ASTNode to an IExpr node

			IExpr expr = new AST2Expr().convert(node);

			if (expr.isAST(F.CompoundExpression, 3)) {
				IAST compoundExpressionAST = (IAST) expr;
				for (int i = 1; i < compoundExpressionAST.size(); i++) {
					expr = compoundExpressionAST.get(i);
					if (expr.isAST(F.SetDelayed, 3)) {
						IAST ast = (IAST) expr;
						appendSetDelayedToBuffer(ast, buffer, last);
					} else if (expr.isAST(F.If, 4)) {
						IAST ast = (IAST) expr;
						if (ast.get(1).toString().equals("§showsteps")) {
							expr = ast.get(3);
							if (expr.isAST(F.SetDelayed, 3)) {
								ast = (IAST) expr;
								appendSetDelayedToBuffer(ast, buffer, last);
							}
						}
					}
				}
			} else if (expr.isAST(F.SetDelayed, 3)) {
				IAST ast = (IAST) expr;
				appendSetDelayedToBuffer(ast, buffer, last);
			} else if (expr.isAST(F.If, 4)) {
				IAST ast = (IAST) expr;
				if (ast.get(1).toString().equals("§showsteps")) {
					expr = ast.get(3);
					if (expr.isAST(F.SetDelayed, 3)) {
						ast = (IAST) expr;
						appendSetDelayedToBuffer(ast, buffer, last);
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

	public static void appendSetDelayedToBuffer(IAST ast, StringBuffer buffer, boolean last) {
		if (ast.get(1).topHead().toString().equalsIgnoreCase("Int")) {
			IAST integrate = (IAST) ast.get(1);
			if (integrate.get(1).isPlus()) {
				System.out.println(ast.toString());
				return;
			}
		}

		IExpr leftHandSide = ast.get(1);
		IExpr rightHandSide = ast.arg2();
		// if (!rightHandSide.isFree(x -> x.topHead().toString().equalsIgnoreCase("unintegrable"), true)) {
		// System.out.println("IGNORED: " + ast.toString());
		// } else {

		GLOBAL_COUNTER++;
		// System.out.println(leftHandSide.toString());
		if (ast.get(1).isAST()) {
			// leftHandSide = PatternMatcher.evalLeftHandSide((IAST) leftHandSide, EvalEngine.get());
			IExpr temp = leftHandSide;

			if (leftHandSide.topHead().toString().equalsIgnoreCase("int")) {
				buffer.append("IIntegrate(" + GLOBAL_COUNTER + ",");
			} else {
				try {
					leftHandSide = EvalEngine.get().evaluate((IAST) temp);
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
		IExpr temp = ast.arg2().replaceAll(F.Rule(F.Defer(F.$s("Int")), F.Integrate));// F.$s("AbortRubi")));
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
	}

	public final static String INTEGRATE_PREFIX = "Integrate::";

	public static void addPredefinedSymbols() {
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AbortRubi", INTEGRATE_PREFIX + "AbortRubi");

		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AbsorbMinusSign", INTEGRATE_PREFIX + "AbsorbMinusSign");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AbsurdNumberFactors", INTEGRATE_PREFIX + "AbsurdNumberFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AbsurdNumberGCD", INTEGRATE_PREFIX + "AbsurdNumberGCD");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AbsurdNumberGCDList", INTEGRATE_PREFIX + "AbsurdNumberGCDList");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AbsurdNumberQ", INTEGRATE_PREFIX + "AbsurdNumberQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ActivateTrig", INTEGRATE_PREFIX + "ActivateTrig");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AlgebraicFunctionFactors",
				INTEGRATE_PREFIX + "AlgebraicFunctionFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AlgebraicFunctionQ", INTEGRATE_PREFIX + "AlgebraicFunctionQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AlgebraicTrigFunctionQ", INTEGRATE_PREFIX + "AlgebraicTrigFunctionQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AllNegTermQ", INTEGRATE_PREFIX + "AllNegTermQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("AtomBaseQ", INTEGRATE_PREFIX + "AtomBaseQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("BinomialDegree", INTEGRATE_PREFIX + "BinomialDegree");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("BinomialMatchQ", INTEGRATE_PREFIX + "BinomialMatchQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("BinomialParts", INTEGRATE_PREFIX + "BinomialParts");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("BinomialQ", INTEGRATE_PREFIX + "BinomialQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("BinomialTest", INTEGRATE_PREFIX + "BinomialTest");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CalculusFreeQ", INTEGRATE_PREFIX + "CalculusFreeQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CalculusQ", INTEGRATE_PREFIX + "CalculusQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CancelCommonFactors", INTEGRATE_PREFIX + "CancelCommonFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CannotIntegrate", INTEGRATE_PREFIX + "CannotIntegrate");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CollectReciprocals", INTEGRATE_PREFIX + "CollectReciprocals");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Coeff", INTEGRATE_PREFIX + "Coeff");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CombineExponents", INTEGRATE_PREFIX + "CombineExponents");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CommonFactors", INTEGRATE_PREFIX + "CommonFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CommonNumericFactors", INTEGRATE_PREFIX + "CommonNumericFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ComplexFreeQ", INTEGRATE_PREFIX + "ComplexFreeQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ComplexNumberQ", INTEGRATE_PREFIX + "ComplexNumberQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ConstantFactor", INTEGRATE_PREFIX + "ConstantFactor");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ContentFactor", INTEGRATE_PREFIX + "ContentFactor");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ContentFactorAux", INTEGRATE_PREFIX + "ContentFactorAux");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CosQ", INTEGRATE_PREFIX + "CosQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CoshQ", INTEGRATE_PREFIX + "CoshQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CotQ", INTEGRATE_PREFIX + "CotQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CothQ", INTEGRATE_PREFIX + "CothQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CscQ", INTEGRATE_PREFIX + "CscQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CschQ", INTEGRATE_PREFIX + "CschQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("CubicMatchQ", INTEGRATE_PREFIX + "CubicMatchQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("DeactivateTrig", INTEGRATE_PREFIX + "DeactivateTrig");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("DeactivateTrigAux", INTEGRATE_PREFIX + "DeactivateTrigAux");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("DerivativeDivides", INTEGRATE_PREFIX + "DerivativeDivides");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Distrib", INTEGRATE_PREFIX + "Distrib");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("DistributeDegree", INTEGRATE_PREFIX + "DistributeDegree");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("DivideDegreesOfFactors", INTEGRATE_PREFIX + "DivideDegreesOfFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Divides", INTEGRATE_PREFIX + "Divides");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("EasyDQ", INTEGRATE_PREFIX + "EasyDQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("EqQ", INTEGRATE_PREFIX + "EqQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("EulerIntegrandQ", INTEGRATE_PREFIX + "EulerIntegrandQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("EvenQuotientQ", INTEGRATE_PREFIX + "EvenQuotientQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("EveryQ", INTEGRATE_PREFIX + "EveryQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpQ", INTEGRATE_PREFIX + "ExpQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandAlgebraicFunction", INTEGRATE_PREFIX + "ExpandAlgebraicFunction");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandBinomial", INTEGRATE_PREFIX + "ExpandBinomial");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandCleanup", INTEGRATE_PREFIX + "ExpandCleanup");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandExpression", INTEGRATE_PREFIX + "ExpandExpression");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandIntegrand", INTEGRATE_PREFIX + "ExpandIntegrand");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandLinearProduct", INTEGRATE_PREFIX + "ExpandLinearProduct");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandToSum", INTEGRATE_PREFIX + "ExpandToSum");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandTrig", INTEGRATE_PREFIX + "ExpandTrig");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandTrigExpand", INTEGRATE_PREFIX + "ExpandTrigExpand");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandTrigReduce", INTEGRATE_PREFIX + "ExpandTrigReduce");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandTrigReduceAux", INTEGRATE_PREFIX + "ExpandTrigReduceAux");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExpandTrigToExp", INTEGRATE_PREFIX + "ExpandTrigToExp");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Expon", INTEGRATE_PREFIX + "Expon");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExponentIn", INTEGRATE_PREFIX + "ExponentIn");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ExponentInAux", INTEGRATE_PREFIX + "ExponentInAux");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FactorAbsurdNumber", INTEGRATE_PREFIX + "FactorAbsurdNumber");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FactorNumericGcd", INTEGRATE_PREFIX + "FactorNumericGcd");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FactorOrder", INTEGRATE_PREFIX + "FactorOrder");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FalseQ", INTEGRATE_PREFIX + "FalseQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FindTrigFactor", INTEGRATE_PREFIX + "FindTrigFactor");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FixInertTrigFunction", INTEGRATE_PREFIX + "FixInertTrigFunction");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FixIntRule", INTEGRATE_PREFIX + "FixIntRule");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FixIntRules", INTEGRATE_PREFIX + "FixIntRules");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FixRhsIntRule", INTEGRATE_PREFIX + "FixRhsIntRule");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FixSimplify", INTEGRATE_PREFIX + "FixSimplify");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FracPart", INTEGRATE_PREFIX + "FracPart");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FractionOrNegativeQ", INTEGRATE_PREFIX + "FractionOrNegativeQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FractionQ", INTEGRATE_PREFIX + "FractionQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FractionalPowerFreeQ", INTEGRATE_PREFIX + "FractionalPowerFreeQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FractionalPowerOfLinear", INTEGRATE_PREFIX + "FractionalPowerOfLinear");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FractionalPowerOfQuotientOfLinears",
				INTEGRATE_PREFIX + "FractionalPowerOfQuotientOfLinears");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FractionalPowerOfSquareQ",
				INTEGRATE_PREFIX + "FractionalPowerOfSquareQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FractionalPowerQ", INTEGRATE_PREFIX + "FractionalPowerQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FractionalPowerSubexpressionQ",
				INTEGRATE_PREFIX + "FractionalPowerSubexpressionQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FreeFactors", INTEGRATE_PREFIX + "FreeFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FreeTerms", INTEGRATE_PREFIX + "FreeTerms");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfCosQ", INTEGRATE_PREFIX + "FunctionOfCosQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfCoshQ", INTEGRATE_PREFIX + "FunctionOfCoshQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfDensePolynomialsQ",
				INTEGRATE_PREFIX + "FunctionOfDensePolynomialsQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfExpnQ", INTEGRATE_PREFIX + "FunctionOfExpnQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfExponential", INTEGRATE_PREFIX + "FunctionOfExponential");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfExponentialFunction",
				INTEGRATE_PREFIX + "FunctionOfExponentialFunction");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfExponentialFunctionAux",
				INTEGRATE_PREFIX + "FunctionOfExponentialFunctionAux");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfExponentialQ", INTEGRATE_PREFIX + "FunctionOfExponentialQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfExponentialTest",
				INTEGRATE_PREFIX + "FunctionOfExponentialTest");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfExponentialTestAux",
				INTEGRATE_PREFIX + "FunctionOfExponentialTestAux");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfHyperbolic", INTEGRATE_PREFIX + "FunctionOfHyperbolic");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfHyperbolicQ", INTEGRATE_PREFIX + "FunctionOfHyperbolicQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfInverseLinear", INTEGRATE_PREFIX + "FunctionOfInverseLinear");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfLinear", INTEGRATE_PREFIX + "FunctionOfLinear");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfLinearSubst", INTEGRATE_PREFIX + "FunctionOfLinearSubst");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfLog", INTEGRATE_PREFIX + "FunctionOfLog");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfQ", INTEGRATE_PREFIX + "FunctionOfQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfSinQ", INTEGRATE_PREFIX + "FunctionOfSinQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfSinhQ", INTEGRATE_PREFIX + "FunctionOfSinhQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfSquareRootOfQuadratic",
				INTEGRATE_PREFIX + "FunctionOfSquareRootOfQuadratic");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfTanQ", INTEGRATE_PREFIX + "FunctionOfTanQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfTanWeight", INTEGRATE_PREFIX + "FunctionOfTanWeight");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfTanhQ", INTEGRATE_PREFIX + "FunctionOfTanhQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfTanhWeight", INTEGRATE_PREFIX + "FunctionOfTanhWeight");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfTrig", INTEGRATE_PREFIX + "FunctionOfTrig");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfTrigOfLinearQ", INTEGRATE_PREFIX + "FunctionOfTrigOfLinearQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("FunctionOfTrigQ", INTEGRATE_PREFIX + "FunctionOfTrigQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GE", INTEGRATE_PREFIX + "GE");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GT", INTEGRATE_PREFIX + "GT");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Gcd", INTEGRATE_PREFIX + "Gcd");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GeneralizedBinomialDegree",
				INTEGRATE_PREFIX + "GeneralizedBinomialDegree");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GeneralizedBinomialMatchQ",
				INTEGRATE_PREFIX + "GeneralizedBinomialMatchQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GeneralizedBinomialParts",
				INTEGRATE_PREFIX + "GeneralizedBinomialParts");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GeneralizedBinomialQ", INTEGRATE_PREFIX + "GeneralizedBinomialQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GeneralizedBinomialTest", INTEGRATE_PREFIX + "GeneralizedBinomialTest");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GeneralizedTrinomialDegree",
				INTEGRATE_PREFIX + "GeneralizedTrinomialDegree");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GeneralizedTrinomialMatchQ",
				INTEGRATE_PREFIX + "GeneralizedTrinomialMatchQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GeneralizedTrinomialParts",
				INTEGRATE_PREFIX + "GeneralizedTrinomialParts");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GeneralizedTrinomialQ", INTEGRATE_PREFIX + "GeneralizedTrinomialQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GeneralizedTrinomialTest",
				INTEGRATE_PREFIX + "GeneralizedTrinomialTest");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GensymSubst", INTEGRATE_PREFIX + "GensymSubst");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("HalfIntegerQ", INTEGRATE_PREFIX + "HalfIntegerQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("HeldFormQ", INTEGRATE_PREFIX + "HeldFormQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("HyperbolicQ", INTEGRATE_PREFIX + "HyperbolicQ");

		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IGtQ", INTEGRATE_PREFIX + "IGtQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ILtQ", INTEGRATE_PREFIX + "ILtQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IGeQ", INTEGRATE_PREFIX + "IGeQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IGtQ", INTEGRATE_PREFIX + "IGtQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ILeQ", INTEGRATE_PREFIX + "ILeQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GtQ", INTEGRATE_PREFIX + "GtQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LtQ", INTEGRATE_PREFIX + "LtQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LeQ", INTEGRATE_PREFIX + "LeQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("GeQ", INTEGRATE_PREFIX + "GeQ");

		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ImaginaryNumericQ", INTEGRATE_PREFIX + "ImaginaryNumericQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ImaginaryQ", INTEGRATE_PREFIX + "ImaginaryQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IndependentQ", INTEGRATE_PREFIX + "IndependentQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("InertReciprocalQ", INTEGRATE_PREFIX + "InertReciprocalQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("InertTrigFreeQ", INTEGRATE_PREFIX + "InertTrigFreeQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("InertTrigQ", INTEGRATE_PREFIX + "InertTrigQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("InertTrigSumQ", INTEGRATE_PREFIX + "InertTrigSumQ");

		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntBinomialQ", INTEGRATE_PREFIX + "IntBinomialQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Integral", INTEGRATE_PREFIX + "Integral");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntHide", INTEGRATE_PREFIX + "IntSum");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntLinearQ", INTEGRATE_PREFIX + "IntLinearQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntPart", INTEGRATE_PREFIX + "IntPart");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntQuadraticQ", INTEGRATE_PREFIX + "IntQuadraticQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntSum", INTEGRATE_PREFIX + "IntSum");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntTerm", INTEGRATE_PREFIX + "IntTerm");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntegerPowerQ", INTEGRATE_PREFIX + "IntegerPowerQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntegerQuotientQ", INTEGRATE_PREFIX + "IntegerQuotientQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntegersQ", INTEGRATE_PREFIX + "IntegersQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("IntegralFreeQ", INTEGRATE_PREFIX + "IntegralFreeQ");

		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("InverseFunctionFreeQ", INTEGRATE_PREFIX + "InverseFunctionFreeQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("InverseFunctionOfLinear", INTEGRATE_PREFIX + "InverseFunctionOfLinear");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("InverseFunctionOfQuotientOfLinears",
				INTEGRATE_PREFIX + "InverseFunctionOfQuotientOfLinears");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("InverseFunctionQ", INTEGRATE_PREFIX + "InverseFunctionQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("InverseHyperbolicQ", INTEGRATE_PREFIX + "InverseHyperbolicQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("InverseTrigQ", INTEGRATE_PREFIX + "InverseTrigQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("KernelSubst", INTEGRATE_PREFIX + "KernelSubst");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("KnownCotangentIntegrandQ",
				INTEGRATE_PREFIX + "KnownCotangentIntegrandQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("KnownSecantIntegrandQ", INTEGRATE_PREFIX + "KnownSecantIntegrandQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("KnownSineIntegrandQ", INTEGRATE_PREFIX + "KnownSineIntegrandQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("KnownTangentIntegrandQ", INTEGRATE_PREFIX + "KnownTangentIntegrandQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("KnownTrigIntegrandQ", INTEGRATE_PREFIX + "KnownTrigIntegrandQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LE", INTEGRATE_PREFIX + "LE");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LT", INTEGRATE_PREFIX + "LT");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LeadBase", INTEGRATE_PREFIX + "LeadBase");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LeadDegree", INTEGRATE_PREFIX + "LeadDegree");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LeadFactor", INTEGRATE_PREFIX + "LeadFactor");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LeadTerm", INTEGRATE_PREFIX + "LeadTerm");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LinearMatchQ", INTEGRATE_PREFIX + "LinearMatchQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LinearPairQ", INTEGRATE_PREFIX + "LinearPairQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LinearQ", INTEGRATE_PREFIX + "LinearQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("LogQ", INTEGRATE_PREFIX + "LogQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MakeAssocList", INTEGRATE_PREFIX + "MakeAssocList");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Map2", INTEGRATE_PREFIX + "Map2");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MapAnd", INTEGRATE_PREFIX + "MapAnd");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MapOr", INTEGRATE_PREFIX + "MapOr");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MergeFactor", INTEGRATE_PREFIX + "MergeFactor");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MergeFactors", INTEGRATE_PREFIX + "MergeFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MergeMonomials", INTEGRATE_PREFIX + "MergeMonomials");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MergeableFactorQ", INTEGRATE_PREFIX + "MergeableFactorQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MinimumDegree", INTEGRATE_PREFIX + "MinimumDegree");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MinimumMonomialExponent", INTEGRATE_PREFIX + "MinimumMonomialExponent");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MonomialExponent", INTEGRATE_PREFIX + "MonomialExponent");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MonomialFactor", INTEGRATE_PREFIX + "MonomialFactor");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MonomialQ", INTEGRATE_PREFIX + "MonomialQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MonomialSumQ", INTEGRATE_PREFIX + "MonomialSumQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("MostMainFactorPosition", INTEGRATE_PREFIX + "MostMainFactorPosition");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NegQ", INTEGRATE_PREFIX + "NegQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NegativeCoefficientQ", INTEGRATE_PREFIX + "NegativeCoefficientQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NegativeIntegerQ", INTEGRATE_PREFIX + "NegativeIntegerQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NegativeOrZeroQ", INTEGRATE_PREFIX + "NegativeOrZeroQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NegativeQ", INTEGRATE_PREFIX + "NegativeQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NegSumBaseQ", INTEGRATE_PREFIX + "NegSumBaseQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NeQ", INTEGRATE_PREFIX + "NeQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NiceSqrtAuxQ", INTEGRATE_PREFIX + "NiceSqrtAuxQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NiceSqrtQ", INTEGRATE_PREFIX + "NiceSqrtQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NonabsurdNumberFactors", INTEGRATE_PREFIX + "NonabsurdNumberFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NonalgebraicFunctionFactors",
				INTEGRATE_PREFIX + "NonalgebraicFunctionFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NonfreeFactors", INTEGRATE_PREFIX + "NonfreeFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NonfreeTerms", INTEGRATE_PREFIX + "NonfreeTerms");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NonnumericFactors", INTEGRATE_PREFIX + "NonnumericFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NonpolynomialTerms", INTEGRATE_PREFIX + "NonpolynomialTerms");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NonpositiveFactors", INTEGRATE_PREFIX + "NonpositiveFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NonrationalFunctionFactors",
				INTEGRATE_PREFIX + "NonrationalFunctionFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NonsumQ", INTEGRATE_PREFIX + "NonsumQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NonzeroQ", INTEGRATE_PREFIX + "NonzeroQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NormalizeHyperbolic", INTEGRATE_PREFIX + "NormalizeHyperbolic");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NormalizeIntegrand", INTEGRATE_PREFIX + "NormalizeIntegrand");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NormalizeIntegrandAux", INTEGRATE_PREFIX + "NormalizeIntegrandAux");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NormalizeIntegrandFactor",
				INTEGRATE_PREFIX + "NormalizeIntegrandFactor");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NormalizeIntegrandFactorBase",
				INTEGRATE_PREFIX + "NormalizeIntegrandFactorBase");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NormalizeLeadTermSigns", INTEGRATE_PREFIX + "NormalizeLeadTermSigns");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NormalizePowerOfLinear", INTEGRATE_PREFIX + "NormalizePowerOfLinear");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NormalizePseudoBinomial", INTEGRATE_PREFIX + "NormalizePseudoBinomial");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NormalizeSumFactors", INTEGRATE_PREFIX + "NormalizeSumFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NormalizeTogether", INTEGRATE_PREFIX + "NormalizeTogether");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NormalizeTrig", INTEGRATE_PREFIX + "NormalizeTrig");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NormalizeTrigReduce", INTEGRATE_PREFIX + "NormalizeTrigReduce");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NotFalseQ", INTEGRATE_PREFIX + "NotFalseQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NotIntegrableQ", INTEGRATE_PREFIX + "NotIntegrableQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NthRoot", INTEGRATE_PREFIX + "NthRoot");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("NumericFactor", INTEGRATE_PREFIX + "NumericFactor");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("OddHyperbolicPowerQ", INTEGRATE_PREFIX + "OddHyperbolicPowerQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("OddQuotientQ", INTEGRATE_PREFIX + "OddQuotientQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("OddTrigPowerQ", INTEGRATE_PREFIX + "OddTrigPowerQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("OneQ", INTEGRATE_PREFIX + "OneQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PerfectPowerTest", INTEGRATE_PREFIX + "PerfectPowerTest");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PerfectSquareQ", INTEGRATE_PREFIX + "PerfectSquareQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PiecewiseLinearQ", INTEGRATE_PREFIX + "PiecewiseLinearQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PolyQ", INTEGRATE_PREFIX + "PolyQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PolyGCD", INTEGRATE_PREFIX + "PolyGCD");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PolynomialDivide", INTEGRATE_PREFIX + "PolynomialDivide");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PolynomialInAuxQ", INTEGRATE_PREFIX + "PolynomialInAuxQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PolynomialInQ", INTEGRATE_PREFIX + "PolynomialInQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PolynomialInSubst", INTEGRATE_PREFIX + "PolynomialInSubst");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PolynomialInSubstAux", INTEGRATE_PREFIX + "PolynomialInSubstAux");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PolynomialTermQ", INTEGRATE_PREFIX + "PolynomialTermQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PolynomialTerms", INTEGRATE_PREFIX + "PolynomialTerms");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PosAux", INTEGRATE_PREFIX + "PosAux");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PosQ", INTEGRATE_PREFIX + "PosQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PositiveFactors", INTEGRATE_PREFIX + "PositiveFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PositiveIntegerPowerQ", INTEGRATE_PREFIX + "PositiveIntegerPowerQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PositiveIntegerQ", INTEGRATE_PREFIX + "PositiveIntegerQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PositiveOrZeroQ", INTEGRATE_PREFIX + "PositiveOrZeroQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PositiveQ", INTEGRATE_PREFIX + "PositiveQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PowerOfInertTrigSumQ", INTEGRATE_PREFIX + "PowerOfInertTrigSumQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PowerOfLinearMatchQ", INTEGRATE_PREFIX + "PowerOfLinearMatchQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PowerOfLinearQ", INTEGRATE_PREFIX + "PowerOfLinearQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PowerQ", INTEGRATE_PREFIX + "PowerQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PowerVariableDegree", INTEGRATE_PREFIX + "PowerVariableDegree");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PowerVariableExpn", INTEGRATE_PREFIX + "PowerVariableExpn");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PowerVariableSubst", INTEGRATE_PREFIX + "PowerVariableSubst");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ProductOfLinearPowersQ", INTEGRATE_PREFIX + "ProductOfLinearPowersQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ProductQ", INTEGRATE_PREFIX + "ProductQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ProperPolyQ", INTEGRATE_PREFIX + "ProperPolyQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PseudoBinomialPairQ", INTEGRATE_PREFIX + "PseudoBinomialPairQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PseudoBinomialQ", INTEGRATE_PREFIX + "PseudoBinomialQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PseudoBinomialParts", INTEGRATE_PREFIX + "PseudoBinomialParts");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PureFunctionOfCosQ", INTEGRATE_PREFIX + "PureFunctionOfCosQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PureFunctionOfCoshQ", INTEGRATE_PREFIX + "PureFunctionOfCoshQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PureFunctionOfCotQ", INTEGRATE_PREFIX + "PureFunctionOfCotQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PureFunctionOfCothQ", INTEGRATE_PREFIX + "PureFunctionOfCothQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PureFunctionOfSinQ", INTEGRATE_PREFIX + "PureFunctionOfSinQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PureFunctionOfSinhQ", INTEGRATE_PREFIX + "PureFunctionOfSinhQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PureFunctionOfTanQ", INTEGRATE_PREFIX + "PureFunctionOfTanQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("PureFunctionOfTanhQ", INTEGRATE_PREFIX + "PureFunctionOfTanhQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("QuadraticMatchQ", INTEGRATE_PREFIX + "QuadraticMatchQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("QuadraticQ", INTEGRATE_PREFIX + "QuadraticQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("QuotientOfLinearsMatchQ", INTEGRATE_PREFIX + "QuotientOfLinearsMatchQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("QuotientOfLinearsP", INTEGRATE_PREFIX + "QuotientOfLinearsP");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("QuotientOfLinearsParts", INTEGRATE_PREFIX + "QuotientOfLinearsParts");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("QuotientOfLinearsQ", INTEGRATE_PREFIX + "QuotientOfLinearsQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("QuadraticProductQ", INTEGRATE_PREFIX + "QuadraticProductQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RationalFunctionExpand", INTEGRATE_PREFIX + "RationalFunctionExpand");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RationalFunctionExponents",
				INTEGRATE_PREFIX + "RationalFunctionExponents");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RationalFunctionFactors", INTEGRATE_PREFIX + "RationalFunctionFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RationalFunctionQ", INTEGRATE_PREFIX + "RationalFunctionQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RationalPowerQ", INTEGRATE_PREFIX + "RationalPowerQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RationalQ", INTEGRATE_PREFIX + "RationalQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RealNumericQ", INTEGRATE_PREFIX + "RealNumericQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RealQ", INTEGRATE_PREFIX + "RealQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ReapList", INTEGRATE_PREFIX + "ReapList");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RectifyCotangent", INTEGRATE_PREFIX + "RectifyCotangent");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RectifyTangent", INTEGRATE_PREFIX + "RectifyTangent");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ReduceInertTrig", INTEGRATE_PREFIX + "ReduceInertTrig");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RemainingFactors", INTEGRATE_PREFIX + "RemainingFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RemainingTerms", INTEGRATE_PREFIX + "RemainingTerms");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RemoveContent", INTEGRATE_PREFIX + "RemoveContent");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RemoveContentAux", INTEGRATE_PREFIX + "RemoveContentAux");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Rt", INTEGRATE_PREFIX + "Rt");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RuleName", INTEGRATE_PREFIX + "RuleName");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("RtAux", INTEGRATE_PREFIX + "RtAux");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SecQ", INTEGRATE_PREFIX + "SecQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SechQ", INTEGRATE_PREFIX + "SechQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SignOfFactor", INTEGRATE_PREFIX + "SignOfFactor");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Simp", INTEGRATE_PREFIX + "Simp");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SimpFixFactor", INTEGRATE_PREFIX + "SimpFixFactor");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SimpHelp", INTEGRATE_PREFIX + "SimpHelp");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SimplerIntegrandQ", INTEGRATE_PREFIX + "SimplerIntegrandQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SimplerQ", INTEGRATE_PREFIX + "SimplerQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SimplerSqrtQ", INTEGRATE_PREFIX + "SimplerSqrtQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SimplifyAntiderivative", INTEGRATE_PREFIX + "SimplifyAntiderivative");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SimplifyAntiderivativeSum",
				INTEGRATE_PREFIX + "SimplifyAntiderivativeSum");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SimplifyIntegrand", INTEGRATE_PREFIX + "SimplifyIntegrand");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SimplifyTerm", INTEGRATE_PREFIX + "SimplifyTerm");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SinCosQ", INTEGRATE_PREFIX + "SinCosQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SinQ", INTEGRATE_PREFIX + "SinQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SinhCoshQ", INTEGRATE_PREFIX + "SinhCoshQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SinhQ", INTEGRATE_PREFIX + "SinhQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Smallest", INTEGRATE_PREFIX + "Smallest");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SmartApart", INTEGRATE_PREFIX + "SmartApart");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SmartDenominator", INTEGRATE_PREFIX + "SmartDenominator");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SmartNumerator", INTEGRATE_PREFIX + "SmartNumerator");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SmartSimplify", INTEGRATE_PREFIX + "SmartSimplify");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SomeNegTermQ", INTEGRATE_PREFIX + "SomeNegTermQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SplitFreeFactors", INTEGRATE_PREFIX + "SplitFreeFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SplitProduct", INTEGRATE_PREFIX + "SplitProduct");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SplitSum", INTEGRATE_PREFIX + "SplitSum");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SqrtNumberQ", INTEGRATE_PREFIX + "SqrtNumberQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SqrtNumberSumQ", INTEGRATE_PREFIX + "SqrtNumberSumQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SqrtQ", INTEGRATE_PREFIX + "SqrtQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SquareRootOfQuadraticSubst",
				INTEGRATE_PREFIX + "SquareRootOfQuadraticSubst");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("StopFunctionQ", INTEGRATE_PREFIX + "StopFunctionQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Subst", INTEGRATE_PREFIX + "Subst");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstAux", INTEGRATE_PREFIX + "SubstAux");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstFor", INTEGRATE_PREFIX + "SubstFor");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstForAux", INTEGRATE_PREFIX + "SubstForAux");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstForExpn", INTEGRATE_PREFIX + "SubstForExpn");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstForFractionalPower", INTEGRATE_PREFIX + "SubstForFractionalPower");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstForFractionalPowerAuxQ",
				INTEGRATE_PREFIX + "SubstForFractionalPowerAuxQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstForFractionalPowerOfLinear",
				INTEGRATE_PREFIX + "SubstForFractionalPowerOfLinear");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstForFractionalPowerOfQuotientOfLinears",
				INTEGRATE_PREFIX + "SubstForFractionalPowerOfQuotientOfLinears");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstForFractionalPowerQ",
				INTEGRATE_PREFIX + "SubstForFractionalPowerQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstForHyperbolic", INTEGRATE_PREFIX + "SubstForHyperbolic");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstForInverseFunction", INTEGRATE_PREFIX + "SubstForInverseFunction");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstForInverseFunctionOfQuotientOfLinears",
				INTEGRATE_PREFIX + "SubstForInverseFunctionOfQuotientOfLinears");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SubstForTrig", INTEGRATE_PREFIX + "SubstForTrig");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SumBaseQ", INTEGRATE_PREFIX + "SumBaseQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SumQ", INTEGRATE_PREFIX + "SumQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SumSimplerAuxQ", INTEGRATE_PREFIX + "SumSimplerAuxQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("SumSimplerQ", INTEGRATE_PREFIX + "SumSimplerQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TanQ", INTEGRATE_PREFIX + "TanQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TanhQ", INTEGRATE_PREFIX + "TanhQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TogetherSimplify", INTEGRATE_PREFIX + "TogetherSimplify");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrigHyperbolicFreeQ", INTEGRATE_PREFIX + "TrigHyperbolicFreeQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrigQ", INTEGRATE_PREFIX + "TrigQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrigSimplify", INTEGRATE_PREFIX + "TrigSimplify");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrigSimplifyAux", INTEGRATE_PREFIX + "TrigSimplifyAux");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrigSimplifyQ", INTEGRATE_PREFIX + "TrigSimplifyQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrigSimplifyRecur", INTEGRATE_PREFIX + "TrigSimplifyRecur");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrigSquare", INTEGRATE_PREFIX + "TrigSquare");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrigSquareQ", INTEGRATE_PREFIX + "TrigSquareQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrinomialDegree", INTEGRATE_PREFIX + "TrinomialDegree");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrinomialMatchQ", INTEGRATE_PREFIX + "TrinomialMatchQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrinomialParts", INTEGRATE_PREFIX + "TrinomialParts");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrinomialQ", INTEGRATE_PREFIX + "TrinomialQ");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TrinomialTest", INTEGRATE_PREFIX + "TrinomialTest");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TryPureTanSubst", INTEGRATE_PREFIX + "TryPureTanSubst");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TryPureTanhSubst", INTEGRATE_PREFIX + "TryPureTanhSubst");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("TryTanhSubst", INTEGRATE_PREFIX + "TryTanhSubst");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("UnifyInertTrigFunction", INTEGRATE_PREFIX + "UnifyInertTrigFunction");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("UnifyNegativeBaseFactors",
				INTEGRATE_PREFIX + "UnifyNegativeBaseFactors");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("UnifySum", INTEGRATE_PREFIX + "UnifySum");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("UnifyTerm", INTEGRATE_PREFIX + "UnifyTerm");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("UnifyTerms", INTEGRATE_PREFIX + "UnifyTerms");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Unintegrable", INTEGRATE_PREFIX + "Unintegrable");
		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("ZeroQ", INTEGRATE_PREFIX + "ZeroQ");

		F.PREDEFINED_INTERNAL_FORM_STRINGS.put("Dist", INTEGRATE_PREFIX + "Dist");
	}

	public static void main(String[] args) {
		Config.SERVER_MODE = false;
		Config.PARSER_USE_LOWERCASE_SYMBOLS = false;
		Config.RUBI_CONVERT_SYMBOLS = true;
		EvalEngine.set(new EvalEngine(false));
		addPredefinedSymbols(); 
		// use same order as in Rubi.m
		String[] fileNames = { //
				"./Rubi/RubiRules4.16.0_FullLHS.m", };

		int fcnt = 0;
		for (int i = 0; i < fileNames.length; i++) {

			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
			System.out.println(">>>>> File name: " + fileNames[i]);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");

			StringBuffer buffer = new StringBuffer(100000);
			List<ASTNode> list = parseFileToList(fileNames[i]);
			// String currentFilename = fileNames[i].substring(fileNames[i].lastIndexOf('/'), fileNames[i].length() -
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
					convert(astNode, buffer, cnt == NUMBER_OF_RULES_PER_FILE || j == list.size() - 1);

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
	}
}
