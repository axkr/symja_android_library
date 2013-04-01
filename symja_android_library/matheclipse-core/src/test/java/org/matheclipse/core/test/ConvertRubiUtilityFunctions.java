package org.matheclipse.core.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

/**
 * Convert the Rubi UtilityFunctions from <a
 * href="http://www.apmaths.uwo.ca/~arich/">Rubi - Indefinite Integration
 * Reduction Rules</a>
 * 
 */
public class ConvertRubiUtilityFunctions {
	private final static String HEADER = "package org.matheclipse.core.integrate.rubi;\n" + "\n" + "\n"
			+ "import static org.matheclipse.core.expression.F.*;\r\n"
			+ "import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;\r\n"
			+ "import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;\r\n" + "\r\n"
			+ "import org.matheclipse.core.interfaces.IAST;\r\n" + "import org.matheclipse.core.interfaces.IExpr;\r\n"
			+ "import org.matheclipse.core.interfaces.ISymbol;\r\n" + "/** \r\n"
			+ " * UtilityFunctions rules from the <a href=\"http://www.apmaths.uwo.ca/~arich/\">Rubi -\r\n"
			+ " * rule-based integrator</a>.\r\n" + " *  \r\n" + " */\r\n" + "public class UtilityFunctions";

	private static final String FOOTER = "}\n";

	private static int NUMBER_OF_RULES_PER_FILE = 100;

	public static List<ASTNode> parseFileToList(String fileName) {
		try {
			File file = new File(fileName);
			final BufferedReader f = new BufferedReader(new FileReader(file));
			final StringBuffer buff = new StringBuffer(1024);
			String line;
			while ((line = f.readLine()) != null) {
				buff.append(line);
				buff.append('\n');
			}
			f.close();
			String inputString = buff.toString();
			Parser p = new Parser(false, true);
			return p.parseList(inputString);
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

	public static void convert(ASTNode node, StringBuffer buffer, boolean last, Set<String> functionSet) {
		try {
			// convert ASTNode to an IExpr node
			IExpr expr = AST2Expr.CONST.convert(node);

			// ISymbol module = F.$s("Module");
			// if (expr.isFree(module, true)) {
			if (expr.isAST(F.SetDelayed, 3)) {
				IAST ast = (IAST) expr;
				addToFunctionSet(ast, functionSet);
				ConvertRubiFiles.appendSetDelayedToBuffer(ast, buffer, last);
			} else if (expr.isAST(F.If, 4)) {
				IAST ast = (IAST) expr;
				// if (ast.get(1).toString().equals("ShowSteps")) {
				expr = ast.get(3);
				if (expr.isAST(F.SetDelayed, 3)) {
					ast = (IAST) expr;
					addToFunctionSet(ast, functionSet);
					ConvertRubiFiles.appendSetDelayedToBuffer(ast, buffer, last);
				}
				// }
			}
			// }
		} catch (UnsupportedOperationException uoe) {
			System.out.println(uoe.getMessage());
			System.out.println(node.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void addToFunctionSet(IAST ast, Set<String> functionSet) {
		if (ast.get(1).isAST()) {
			IAST lhsAST = (IAST) ast.get(1);
			if (lhsAST.head().isSymbol()) {
				ISymbol sym = (ISymbol) lhsAST.head();
				if (Character.isUpperCase(sym.toString().charAt(0))) {
					String entry = sym.toString() + "," + lhsAST.size();
					functionSet.add(entry);
				}
			}
		}
	}

	public final static String INTEGRATE_PREFIX = "Integrate::";

	public static void main(String[] args) {
		F.initSymbols(null, null, false);
		F.PREDEFINED_INTERNAL_STRINGS.put("AlgebraicFunctionQ", INTEGRATE_PREFIX + "AlgebraicFunctionQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("BinomialQ", INTEGRATE_PREFIX + "BinomialQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("BinomialTest", INTEGRATE_PREFIX + "BinomialTest");
		F.PREDEFINED_INTERNAL_STRINGS.put("CalculusFreeQ", INTEGRATE_PREFIX + "CalculusFreeQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("CalculusQ", INTEGRATE_PREFIX + "CalculusQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("ClearDownValues", INTEGRATE_PREFIX + "ClearDownValues");
		F.PREDEFINED_INTERNAL_STRINGS.put("CommonFactors", INTEGRATE_PREFIX + "CommonFactors");
		F.PREDEFINED_INTERNAL_STRINGS.put("CommonNumericFactors", INTEGRATE_PREFIX + "CommonNumericFactors");
		F.PREDEFINED_INTERNAL_STRINGS.put("ComplexFreeQ", INTEGRATE_PREFIX + "ComplexFreeQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("ConstantFactor", INTEGRATE_PREFIX + "ConstantFactor");
		F.PREDEFINED_INTERNAL_STRINGS.put("ContainsQ", INTEGRATE_PREFIX + "ContainsQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("ContentFactor", INTEGRATE_PREFIX + "ContentFactor");
		F.PREDEFINED_INTERNAL_STRINGS.put("CosQ", INTEGRATE_PREFIX + "CosQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("CoshQ", INTEGRATE_PREFIX + "CoshQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("CotQ", INTEGRATE_PREFIX + "CotQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("CothQ", INTEGRATE_PREFIX + "CothQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("CscQ", INTEGRATE_PREFIX + "CscQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("CschQ", INTEGRATE_PREFIX + "CschQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("DerivativeDivides", INTEGRATE_PREFIX + "DerivativeDivides");
		F.PREDEFINED_INTERNAL_STRINGS.put("Dist", INTEGRATE_PREFIX + "Dist");
		F.PREDEFINED_INTERNAL_STRINGS.put("DivideDegreesOfFactors", INTEGRATE_PREFIX + "DivideDegreesOfFactors");
		F.PREDEFINED_INTERNAL_STRINGS.put("EasyDQ", INTEGRATE_PREFIX + "EasyDQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("EvenQuotientQ", INTEGRATE_PREFIX + "EvenQuotientQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("ExpQ", INTEGRATE_PREFIX + "ExpQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("ExpandExpression", INTEGRATE_PREFIX + "ExpandExpression");
		F.PREDEFINED_INTERNAL_STRINGS.put("ExpandExpressionAux", INTEGRATE_PREFIX + "ExpandExpressionAux");
		F.PREDEFINED_INTERNAL_STRINGS.put("ExpandImproperFraction", INTEGRATE_PREFIX + "ExpandImproperFraction");
		F.PREDEFINED_INTERNAL_STRINGS.put("ExpandImproperFraction", INTEGRATE_PREFIX + "ExpandImproperFraction");
		F.PREDEFINED_INTERNAL_STRINGS.put("ExpandIntegrandQ", INTEGRATE_PREFIX + "ExpandIntegrandQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("ExpandPolynomial", INTEGRATE_PREFIX + "ExpandPolynomial");
		F.PREDEFINED_INTERNAL_STRINGS.put("ExpandTrigExpression", INTEGRATE_PREFIX + "ExpandTrigExpression");
		F.PREDEFINED_INTERNAL_STRINGS.put("ExpandTrigExpressionAux", INTEGRATE_PREFIX + "ExpandTrigExpressionAux");
		F.PREDEFINED_INTERNAL_STRINGS.put("ExpnExpand", INTEGRATE_PREFIX + "ExpnExpand");
		F.PREDEFINED_INTERNAL_STRINGS.put("ExpnExpandAux", INTEGRATE_PREFIX + "ExpnExpandAux");
		F.PREDEFINED_INTERNAL_STRINGS.put("FactorOrder", INTEGRATE_PREFIX + "FactorOrder");
		F.PREDEFINED_INTERNAL_STRINGS.put("FalseQ", INTEGRATE_PREFIX + "FalseQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FindKernel", INTEGRATE_PREFIX + "FindKernel");
		F.PREDEFINED_INTERNAL_STRINGS.put("FindTrigFactor", INTEGRATE_PREFIX + "FindTrigFactor");
		F.PREDEFINED_INTERNAL_STRINGS.put("FractionOrNegativeQ", INTEGRATE_PREFIX + "FractionOrNegativeQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FractionQ", INTEGRATE_PREFIX + "FractionQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FractionalPowerFreeQ", INTEGRATE_PREFIX + "FractionalPowerFreeQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FractionalPowerOfLinear", INTEGRATE_PREFIX + "FractionalPowerOfLinear");
		F.PREDEFINED_INTERNAL_STRINGS
				.put("FractionalPowerOfQuotientOfLinears", INTEGRATE_PREFIX + "FractionalPowerOfQuotientOfLinears");
		F.PREDEFINED_INTERNAL_STRINGS.put("FractionalPowerQ", INTEGRATE_PREFIX + "FractionalPowerQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfCosQ", INTEGRATE_PREFIX + "FunctionOfCosQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfCoshQ", INTEGRATE_PREFIX + "FunctionOfCoshQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfDensePolynomialsQ", INTEGRATE_PREFIX + "FunctionOfDensePolynomialsQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfExpnQ", INTEGRATE_PREFIX + "FunctionOfExpnQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfExponentialOfLinear", INTEGRATE_PREFIX + "FunctionOfExponentialOfLinear");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfExponentialOfLinear", INTEGRATE_PREFIX + "FunctionOfExponentialOfLinear");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfExponentialOfLinearAux", INTEGRATE_PREFIX + "FunctionOfExponentialOfLinearAux");
		F.PREDEFINED_INTERNAL_STRINGS
				.put("FunctionOfExponentialOfLinearSubst", INTEGRATE_PREFIX + "FunctionOfExponentialOfLinearSubst");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfHyperbolic", INTEGRATE_PREFIX + "FunctionOfHyperbolic");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfHyperbolic", INTEGRATE_PREFIX + "FunctionOfHyperbolic");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfHyperbolicQ", INTEGRATE_PREFIX + "FunctionOfHyperbolicQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfInverseLinear", INTEGRATE_PREFIX + "FunctionOfInverseLinear");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfInverseLinear", INTEGRATE_PREFIX + "FunctionOfInverseLinear");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfKernelQ", INTEGRATE_PREFIX + "FunctionOfKernelQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfLinear", INTEGRATE_PREFIX + "FunctionOfLinear");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfLinear", INTEGRATE_PREFIX + "FunctionOfLinear");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfLinearSubst", INTEGRATE_PREFIX + "FunctionOfLinearSubst");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfLog", INTEGRATE_PREFIX + "FunctionOfLog");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfLog", INTEGRATE_PREFIX + "FunctionOfLog");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfPowerQ", INTEGRATE_PREFIX + "FunctionOfPowerQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfProductLog", INTEGRATE_PREFIX + "FunctionOfProductLog");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfProductLog", INTEGRATE_PREFIX + "FunctionOfProductLog");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfQ", INTEGRATE_PREFIX + "FunctionOfQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfQ", INTEGRATE_PREFIX + "FunctionOfQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfSinQ", INTEGRATE_PREFIX + "FunctionOfSinQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfSinhQ", INTEGRATE_PREFIX + "FunctionOfSinhQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfSquareRootOfQuadratic", INTEGRATE_PREFIX + "FunctionOfSquareRootOfQuadratic");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfSquareRootOfQuadratic", INTEGRATE_PREFIX + "FunctionOfSquareRootOfQuadratic");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfTanQ", INTEGRATE_PREFIX + "FunctionOfTanQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfTanWeight", INTEGRATE_PREFIX + "FunctionOfTanWeight");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfTanhQ", INTEGRATE_PREFIX + "FunctionOfTanhQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfTanhWeight", INTEGRATE_PREFIX + "FunctionOfTanhWeight");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfTrig", INTEGRATE_PREFIX + "FunctionOfTrig");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfTrig", INTEGRATE_PREFIX + "FunctionOfTrig");
		F.PREDEFINED_INTERNAL_STRINGS.put("FunctionOfTrigQ", INTEGRATE_PREFIX + "FunctionOfTrigQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("GE", INTEGRATE_PREFIX + "GE");
		F.PREDEFINED_INTERNAL_STRINGS.put("GE", INTEGRATE_PREFIX + "GE");
		F.PREDEFINED_INTERNAL_STRINGS.put("GT", INTEGRATE_PREFIX + "GT");
		F.PREDEFINED_INTERNAL_STRINGS.put("GT", INTEGRATE_PREFIX + "GT");
		F.PREDEFINED_INTERNAL_STRINGS.put("GoodExpansionQ", INTEGRATE_PREFIX + "GoodExpansionQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("HalfIntegerQ", INTEGRATE_PREFIX + "HalfIntegerQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("HyperbolicQ", INTEGRATE_PREFIX + "HyperbolicQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("ImaginaryNumericQ", INTEGRATE_PREFIX + "ImaginaryNumericQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("ImaginaryQ", INTEGRATE_PREFIX + "ImaginaryQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("ImproperRationalFunctionQ", INTEGRATE_PREFIX + "ImproperRationalFunctionQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("IndentedPrint", INTEGRATE_PREFIX + "IndentedPrint");
		F.PREDEFINED_INTERNAL_STRINGS.put("IndependentQ", INTEGRATE_PREFIX + "IndependentQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("IntegerPowerQ", INTEGRATE_PREFIX + "IntegerPowerQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("IntegerQuotientQ", INTEGRATE_PREFIX + "IntegerQuotientQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("InverseFunctionFreeQ", INTEGRATE_PREFIX + "InverseFunctionFreeQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("InverseFunctionOfLinear", INTEGRATE_PREFIX + "InverseFunctionOfLinear");
		F.PREDEFINED_INTERNAL_STRINGS
				.put("InverseFunctionOfQuotientOfLinears", INTEGRATE_PREFIX + "InverseFunctionOfQuotientOfLinears");
		F.PREDEFINED_INTERNAL_STRINGS.put("InverseFunctionQ", INTEGRATE_PREFIX + "InverseFunctionQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("InverseHyperbolicQ", INTEGRATE_PREFIX + "InverseHyperbolicQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("InverseTrigQ", INTEGRATE_PREFIX + "InverseTrigQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("LE", INTEGRATE_PREFIX + "LE");
		F.PREDEFINED_INTERNAL_STRINGS.put("LE", INTEGRATE_PREFIX + "LE");
		F.PREDEFINED_INTERNAL_STRINGS.put("LT", INTEGRATE_PREFIX + "LT");
		F.PREDEFINED_INTERNAL_STRINGS.put("LT", INTEGRATE_PREFIX + "LT");
		F.PREDEFINED_INTERNAL_STRINGS.put("LeadBase", INTEGRATE_PREFIX + "LeadBase");
		F.PREDEFINED_INTERNAL_STRINGS.put("LeadDegree", INTEGRATE_PREFIX + "LeadDegree");
		F.PREDEFINED_INTERNAL_STRINGS.put("LeadFactor", INTEGRATE_PREFIX + "LeadFactor");
		F.PREDEFINED_INTERNAL_STRINGS.put("LeadTerm", INTEGRATE_PREFIX + "LeadTerm");
		F.PREDEFINED_INTERNAL_STRINGS.put("LinearQ", INTEGRATE_PREFIX + "LinearQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("LogQ", INTEGRATE_PREFIX + "LogQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("MakeList", INTEGRATE_PREFIX + "MakeList");
		F.PREDEFINED_INTERNAL_STRINGS.put("Map2", INTEGRATE_PREFIX + "Map2");
		F.PREDEFINED_INTERNAL_STRINGS.put("MapAnd", INTEGRATE_PREFIX + "MapAnd");
		F.PREDEFINED_INTERNAL_STRINGS.put("MapAnd", INTEGRATE_PREFIX + "MapAnd");
		F.PREDEFINED_INTERNAL_STRINGS.put("MapOr", INTEGRATE_PREFIX + "MapOr");
		F.PREDEFINED_INTERNAL_STRINGS.put("MinimumDegree", INTEGRATE_PREFIX + "MinimumDegree");
		F.PREDEFINED_INTERNAL_STRINGS.put("Mods", INTEGRATE_PREFIX + "Mods");
		F.PREDEFINED_INTERNAL_STRINGS.put("MonomialFactor", INTEGRATE_PREFIX + "MonomialFactor");
		F.PREDEFINED_INTERNAL_STRINGS.put("MonomialQ", INTEGRATE_PREFIX + "MonomialQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("MonomialSumQ", INTEGRATE_PREFIX + "MonomialSumQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("MostMainFactorPosition", INTEGRATE_PREFIX + "MostMainFactorPosition");
		F.PREDEFINED_INTERNAL_STRINGS.put("MoveDownValues", INTEGRATE_PREFIX + "MoveDownValues");
		F.PREDEFINED_INTERNAL_STRINGS.put("NegQ", INTEGRATE_PREFIX + "NegQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("NegativeCoefficientQ", INTEGRATE_PREFIX + "NegativeCoefficientQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("NegativeOrZeroQ", INTEGRATE_PREFIX + "NegativeOrZeroQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("NegativeQ", INTEGRATE_PREFIX + "NegativeQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("NonnumericFactors", INTEGRATE_PREFIX + "NonnumericFactors");
		F.PREDEFINED_INTERNAL_STRINGS.put("NonpolynomialTerms", INTEGRATE_PREFIX + "NonpolynomialTerms");
		F.PREDEFINED_INTERNAL_STRINGS.put("NonpositiveFactors", INTEGRATE_PREFIX + "NonpositiveFactors");
		F.PREDEFINED_INTERNAL_STRINGS.put("NonsumQ", INTEGRATE_PREFIX + "NonsumQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("NonzeroQ", INTEGRATE_PREFIX + "NonzeroQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("NormalForm", INTEGRATE_PREFIX + "NormalForm");
		F.PREDEFINED_INTERNAL_STRINGS.put("NotFalseQ", INTEGRATE_PREFIX + "NotFalseQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("NotIntegrableQ", INTEGRATE_PREFIX + "NotIntegrableQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("NumericFactor", INTEGRATE_PREFIX + "NumericFactor");
		F.PREDEFINED_INTERNAL_STRINGS.put("OddHyperbolicPowerQ", INTEGRATE_PREFIX + "OddHyperbolicPowerQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("OddQuotientQ", INTEGRATE_PREFIX + "OddQuotientQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("OddTrigPowerQ", INTEGRATE_PREFIX + "OddTrigPowerQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PerfectPowerTest", INTEGRATE_PREFIX + "PerfectPowerTest");
		F.PREDEFINED_INTERNAL_STRINGS.put("PolynomialDivide", INTEGRATE_PREFIX + "PolynomialDivide");
		F.PREDEFINED_INTERNAL_STRINGS.put("PolynomialFunctionOf", INTEGRATE_PREFIX + "PolynomialFunctionOf");
		F.PREDEFINED_INTERNAL_STRINGS.put("PolynomialTermQ", INTEGRATE_PREFIX + "PolynomialTermQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PolynomialTerms", INTEGRATE_PREFIX + "PolynomialTerms");
		F.PREDEFINED_INTERNAL_STRINGS.put("PosQ", INTEGRATE_PREFIX + "PosQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PositiveFactors", INTEGRATE_PREFIX + "PositiveFactors");
		F.PREDEFINED_INTERNAL_STRINGS.put("PositiveIntegerPowerQ", INTEGRATE_PREFIX + "PositiveIntegerPowerQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PositiveOrZeroQ", INTEGRATE_PREFIX + "PositiveOrZeroQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PositiveQ", INTEGRATE_PREFIX + "PositiveQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PowerQ", INTEGRATE_PREFIX + "PowerQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PowerVariableDegree", INTEGRATE_PREFIX + "PowerVariableDegree");
		F.PREDEFINED_INTERNAL_STRINGS.put("PowerVariableExpn", INTEGRATE_PREFIX + "PowerVariableExpn");
		F.PREDEFINED_INTERNAL_STRINGS.put("PowerVariableSubst", INTEGRATE_PREFIX + "PowerVariableSubst");
		F.PREDEFINED_INTERNAL_STRINGS.put("ProductLogQ", INTEGRATE_PREFIX + "ProductLogQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("ProductQ", INTEGRATE_PREFIX + "ProductQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PureFunctionOfCosQ", INTEGRATE_PREFIX + "PureFunctionOfCosQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PureFunctionOfCoshQ", INTEGRATE_PREFIX + "PureFunctionOfCoshQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PureFunctionOfCotQ", INTEGRATE_PREFIX + "PureFunctionOfCotQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PureFunctionOfCothQ", INTEGRATE_PREFIX + "PureFunctionOfCothQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PureFunctionOfSinQ", INTEGRATE_PREFIX + "PureFunctionOfSinQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PureFunctionOfSinhQ", INTEGRATE_PREFIX + "PureFunctionOfSinhQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PureFunctionOfTanQ", INTEGRATE_PREFIX + "PureFunctionOfTanQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("PureFunctionOfTanhQ", INTEGRATE_PREFIX + "PureFunctionOfTanhQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("QuadraticPolynomialQ", INTEGRATE_PREFIX + "QuadraticPolynomialQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("QuadraticQ", INTEGRATE_PREFIX + "QuadraticQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("QuotientOfLinearsParts", INTEGRATE_PREFIX + "QuotientOfLinearsParts");
		F.PREDEFINED_INTERNAL_STRINGS.put("QuotientOfLinearsQ", INTEGRATE_PREFIX + "QuotientOfLinearsQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("RationalFunctionExponents", INTEGRATE_PREFIX + "RationalFunctionExponents");
		F.PREDEFINED_INTERNAL_STRINGS.put("RationalFunctionQ", INTEGRATE_PREFIX + "RationalFunctionQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("RationalPowerQ", INTEGRATE_PREFIX + "RationalPowerQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("RationalQ", INTEGRATE_PREFIX + "RationalQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("RealNumericQ", INTEGRATE_PREFIX + "RealNumericQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("RealQ", INTEGRATE_PREFIX + "RealQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("ReapList", INTEGRATE_PREFIX + "ReapList");
		F.PREDEFINED_INTERNAL_STRINGS.put("RecognizedFormQ", INTEGRATE_PREFIX + "RecognizedFormQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("Regularize", INTEGRATE_PREFIX + "Regularize");
		F.PREDEFINED_INTERNAL_STRINGS.put("RegularizeSubst", INTEGRATE_PREFIX + "RegularizeSubst");
		F.PREDEFINED_INTERNAL_STRINGS.put("RegularizeTerm", INTEGRATE_PREFIX + "RegularizeTerm");
		F.PREDEFINED_INTERNAL_STRINGS.put("RemainingFactors", INTEGRATE_PREFIX + "RemainingFactors");
		F.PREDEFINED_INTERNAL_STRINGS.put("RemainingTerms", INTEGRATE_PREFIX + "RemainingTerms");
		F.PREDEFINED_INTERNAL_STRINGS.put("Rt", INTEGRATE_PREFIX + "Rt");
		F.PREDEFINED_INTERNAL_STRINGS.put("SecQ", INTEGRATE_PREFIX + "SecQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("SechQ", INTEGRATE_PREFIX + "SechQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("Second", INTEGRATE_PREFIX + "Second");
		F.PREDEFINED_INTERNAL_STRINGS.put("SetDownValues", INTEGRATE_PREFIX + "SetDownValues");
		F.PREDEFINED_INTERNAL_STRINGS.put("Simp", INTEGRATE_PREFIX + "Simp");
		F.PREDEFINED_INTERNAL_STRINGS.put("SimpAux", INTEGRATE_PREFIX + "SimpAux");
		F.PREDEFINED_INTERNAL_STRINGS.put("SimpProduct", INTEGRATE_PREFIX + "SimpProduct");
		F.PREDEFINED_INTERNAL_STRINGS.put("SimpSum", INTEGRATE_PREFIX + "SimpSum");
		F.PREDEFINED_INTERNAL_STRINGS.put("SimplerExpressionQ", INTEGRATE_PREFIX + "SimplerExpressionQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("SimplerRationalFunctionQ", INTEGRATE_PREFIX + "SimplerRationalFunctionQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("SimplifyExpression", INTEGRATE_PREFIX + "SimplifyExpression");
		F.PREDEFINED_INTERNAL_STRINGS.put("SinCosQ", INTEGRATE_PREFIX + "SinCosQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("SinQ", INTEGRATE_PREFIX + "SinQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("SinhCoshQ", INTEGRATE_PREFIX + "SinhCoshQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("SinhQ", INTEGRATE_PREFIX + "SinhQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("Smallest", INTEGRATE_PREFIX + "Smallest");
		F.PREDEFINED_INTERNAL_STRINGS.put("Smallest", INTEGRATE_PREFIX + "Smallest");
		F.PREDEFINED_INTERNAL_STRINGS.put("SmartDenominator", INTEGRATE_PREFIX + "SmartDenominator");
		F.PREDEFINED_INTERNAL_STRINGS.put("SmartLeafCount", INTEGRATE_PREFIX + "SmartLeafCount");
		F.PREDEFINED_INTERNAL_STRINGS.put("SmartNumerator", INTEGRATE_PREFIX + "SmartNumerator");
		F.PREDEFINED_INTERNAL_STRINGS.put("SmartTrigExpand", INTEGRATE_PREFIX + "SmartTrigExpand");
		F.PREDEFINED_INTERNAL_STRINGS.put("SplitFactorsOfTerms", INTEGRATE_PREFIX + "SplitFactorsOfTerms");
		F.PREDEFINED_INTERNAL_STRINGS.put("SplitFreeFactors", INTEGRATE_PREFIX + "SplitFreeFactors");
		F.PREDEFINED_INTERNAL_STRINGS.put("SplitFreeTerms", INTEGRATE_PREFIX + "SplitFreeTerms");
		F.PREDEFINED_INTERNAL_STRINGS.put("SplitMonomialTerms", INTEGRATE_PREFIX + "SplitMonomialTerms");
		F.PREDEFINED_INTERNAL_STRINGS.put("SqrtNumberQ", INTEGRATE_PREFIX + "SqrtNumberQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("SqrtNumberSumQ", INTEGRATE_PREFIX + "SqrtNumberSumQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("SqrtQ", INTEGRATE_PREFIX + "SqrtQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("SquareRootOfQuadraticSubst", INTEGRATE_PREFIX + "SquareRootOfQuadraticSubst");
		F.PREDEFINED_INTERNAL_STRINGS.put("Subst", INTEGRATE_PREFIX + "Subst");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstFor", INTEGRATE_PREFIX + "SubstFor");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstForExpn", INTEGRATE_PREFIX + "SubstForExpn");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstForFractionalPower", INTEGRATE_PREFIX + "SubstForFractionalPower");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstForFractionalPowerAuxQ", INTEGRATE_PREFIX + "SubstForFractionalPowerAuxQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstForFractionalPowerOfLinear", INTEGRATE_PREFIX + "SubstForFractionalPowerOfLinear");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstForFractionalPowerOfQuotientOfLinears", INTEGRATE_PREFIX
				+ "SubstForFractionalPowerOfQuotientOfLinears");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstForFractionalPowerQ", INTEGRATE_PREFIX + "SubstForFractionalPowerQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstForHyperbolic", INTEGRATE_PREFIX + "SubstForHyperbolic");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstForInverseFunction", INTEGRATE_PREFIX + "SubstForInverseFunction");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstForInverseFunction", INTEGRATE_PREFIX + "SubstForInverseFunction");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstForInverseFunctionOfLinear", INTEGRATE_PREFIX + "SubstForInverseFunctionOfLinear");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstForInverseFunctionOfQuotientOfLinears", INTEGRATE_PREFIX
				+ "SubstForInverseFunctionOfQuotientOfLinears");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstForInverseLinear", INTEGRATE_PREFIX + "SubstForInverseLinear");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstForPower", INTEGRATE_PREFIX + "SubstForPower");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstForTrig", INTEGRATE_PREFIX + "SubstForTrig");
		F.PREDEFINED_INTERNAL_STRINGS.put("SubstQ", INTEGRATE_PREFIX + "SubstQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("SumFreeQ", INTEGRATE_PREFIX + "SumFreeQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("SumQ", INTEGRATE_PREFIX + "SumQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("TanQ", INTEGRATE_PREFIX + "TanQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("TanhQ", INTEGRATE_PREFIX + "TanhQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("TrigHyperbolicFreeQ", INTEGRATE_PREFIX + "TrigHyperbolicFreeQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("TrigQ", INTEGRATE_PREFIX + "TrigQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("TrigSimplify", INTEGRATE_PREFIX + "TrigSimplify");
		F.PREDEFINED_INTERNAL_STRINGS.put("TrigSimplifyAux", INTEGRATE_PREFIX + "TrigSimplifyAux");
		F.PREDEFINED_INTERNAL_STRINGS.put("TryTrigReduceQ", INTEGRATE_PREFIX + "TryTrigReduceQ");
		F.PREDEFINED_INTERNAL_STRINGS.put("ZeroQ", INTEGRATE_PREFIX + "ZeroQ");

		Config.SERVER_MODE = false;

		String[] fileNames = {
		// "C:\\temp\\RationalFunctionIntegrationRules.m",
		// "C:\\temp\\AlgebraicFunctionIntegrationRules.m",
		// "C:\\temp\\ExponentialFunctionIntegrationRules.m",
		// "C:\\temp\\Rubi\\TrigFunctionIntegrationRules.m",
		// "C:\\temp\\HyperbolicFunctionIntegrationRules.m",
		// "C:\\temp\\Rubi\\\\LogarithmFunctionIntegrationRules.m",
		// "C:\\temp\\InverseTrigFunctionIntegrationRules.m",
		// "C:\\temp\\InverseHyperbolicFunctionIntegrationRules.m",
		// "C:\\temp\\ErrorFunctionIntegrationRules.m",
		// "C:\\temp\\IntegralFunctionIntegrationRules.m",
		// "C:\\temp\\SpecialFunctionIntegrationRules.m",
		// "C:\\temp\\Rubi\\GeneralIntegrationRules.m",
		// "C:\\temp\\Rubi\\IndefiniteIntegrationRules.m",
		"C:\\temp\\Rubi\\UtilityFunctions.m" };
		for (int i = 0; i < fileNames.length; i++) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
			System.out.println(">>>>> File name: " + fileNames[i]);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");

			StringBuffer buffer = new StringBuffer(100000);
			List<ASTNode> list = parseFileToList(fileNames[i]);
			if (list != null) {
				int cnt = 0;
				int fcnt = 0;
				TreeSet<String> functionSet = new TreeSet<String>();
				for (int j = 0; j < list.size(); j++) {
					if (cnt == 0) {
						buffer = new StringBuffer(100000);
						buffer.append(HEADER + fcnt + " { \n  public static IAST RULES = List( \n");
					}
					ASTNode astNode = list.get(j);
					cnt++;
					convert(astNode, buffer, cnt == NUMBER_OF_RULES_PER_FILE, functionSet);

					if (cnt == NUMBER_OF_RULES_PER_FILE) {
						buffer.append("  );\n" + FOOTER);
						writeFile("C:\\temp\\Rubi\\UtilityFunctions" + fcnt + ".java", buffer);
						fcnt++;
						cnt = 0;
					}
				}
				if (cnt != 0) {
					// System.out.println(");");
					buffer.append("  );\n" + FOOTER);
					writeFile("C:\\temp\\Rubi\\UtilityFunctions" + fcnt + ".java", buffer);
				}
				buffer = new StringBuffer(100000);
				for (String str : functionSet) {
					String[] spl = str.split(",");
					String functionName = spl[0];
					int numberOfArguments = Integer.valueOf(spl[1]).intValue() - 1;
					buffer.append("    F.PREDEFINED_INTERNAL_STRINGS.put(\"" + functionName + "\",INTEGRATE_PREFIX+\"" + functionName
							+ "\");\n");
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
						buffer.append("    return unary($s(INTEGRATE_PREFIX+\"" + functionName + "\"), a0);\n");
						buffer.append("  }\n\n");
						break;
					case 2:
						buffer.append("  public static IAST " + functionName + "(final IExpr a0, final IExpr a1) {\n");
						buffer.append("    return binary($s(INTEGRATE_PREFIX+\"" + functionName + "\"), a0, a1);\n");
						buffer.append("  }\n\n");
						break;
					case 3:
						buffer.append("  public static IAST " + functionName + "(final IExpr a0, final IExpr a1, final IExpr a2) {\n");
						buffer.append("    return ternary($s(INTEGRATE_PREFIX+\"" + functionName + "\"), a0, a1, a2);\n");
						buffer.append("  }\n\n");
						break;
					case 4:
						buffer.append("  public static IAST " + functionName
								+ "(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {\n");
						buffer.append("    return quaternary($s(INTEGRATE_PREFIX+\"" + functionName + "\"), a0, a1, a2, a3);\n");
						buffer.append("  }\n\n");
						break;
					case 5:
						buffer.append("  public static IAST " + functionName
								+ "(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4) {\n");
						buffer.append("    return quinary($s(INTEGRATE_PREFIX+\"" + functionName + "\"), a0, a1, a2, a3, a4);\n");
						buffer.append("  }\n\n");
						break;
					case 6:
						buffer.append("  public static IAST " + functionName
								+ "(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4, final IExpr a5) {\n");
						buffer.append("    return senary($s(INTEGRATE_PREFIX+\"" + functionName + "\"), a0, a1, a2, a3, a4, a5);\n");
						buffer.append("  }\n\n");
						break;
					default:
						System.out.println("ERROR in SWITCH:" + functionName + " - " + numberOfArguments);
					}
				}
				System.out.println(buffer.toString());
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
				System.out.println(">>>>> Number of entries: " + list.size());
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>");
			}
		}
	}
}
