package org.matheclipse.core.convert;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.function.Blank;
import org.matheclipse.core.builtin.function.Complex;
import org.matheclipse.core.builtin.function.Pattern;
import org.matheclipse.core.builtin.function.Rational;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.util.SuggestTree;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FloatNode;
import org.matheclipse.parser.client.ast.FractionNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.IntegerNode;
import org.matheclipse.parser.client.ast.Pattern2Node;
import org.matheclipse.parser.client.ast.Pattern3Node;
import org.matheclipse.parser.client.ast.PatternNode;
import org.matheclipse.parser.client.ast.StringNode;
import org.matheclipse.parser.client.ast.SymbolNode;

/**
 * Converts a parsed <code>org.matheclipse.parser.client.ast.ASTNode</code> expression into an IExpr expression
 * 
 */
public class AST2Expr {
	public final static String[] UPPERCASE_SYMBOL_STRINGS = { "D", "E", "I", "N" };

	public final static String[] SYMBOL_STRINGS = { "Algebraics", "Automatic", "Booleans", "ComplexInfinity", "Catalan",
			"Complexes", "Degree", "EulerGamma", "False", "Flat", "Glaisher", "GoldenRatio", "HoldAll", "HoldFirst", "HoldRest",
			"Indeterminate", "Infinity", "Integer", "Integers", "Khinchin", "Listable", "Modulus", "Null", "NumericFunction",
			"OneIdentity", "Orderless", "Pi", "Primes", "Rationals", "Real", "Reals", "Slot", "SlotSequence", "String", "Symbol",
			"True" };

	public final static String[] FUNCTION_STRINGS = { "Abs", "AddTo", "And", "Alternatives", "Apart", "AppellF1", "Append",
			"AppendTo", "Apply", "ArcCos", "ArcCosh", "ArcCot", "ArcCoth", "ArcCsc", "ArcCsch", "ArcSec", "ArcSech", "ArcSin",
			"ArcSinh", "ArcTan", "ArcTanh", "Arg", "Array", "ArrayDepth", "ArrayQ", "Assumptions", "AtomQ", "Attributes", "Begin",
			"BeginPackage", "BernoulliB", "Binomial", "Blank", "Block", "Boole", "BooleanConvert", "BooleanMinimize", "Break",
			"Cancel", "CartesianProduct", "Cases", "CatalanNumber", "Catch", "Ceiling", "CharacteristicPolynomial", "ChebyshevT",
			"ChessboardDistance", "Chop", "Clear", "ClearAll", "Coefficient", "CoefficientList", "Colon", "Collect", "Complement",
			"Compile", "Complex", "ComplexExpand", "ComplexInfinity", "ComposeList", "CompoundExpression", "Condition",
			"Conjugate", "ConjugateTranspose", "ConstantArray", "Continue", "ContinuedFraction", "CoprimeQ", "Cos", "Cosh",
			"CosIntegral", "CoshIntegral", "Cot", "Coth", "Count", "Cross", "Csc", "Csch", "Curl", "Decrement", "Default", "Defer",
			"Definition", "Delete", "DeleteCases", "DeleteDuplicates", "Denominator", "Depth", "Derivative", "Det",
			"DiagonalMatrix", "DigitQ", "Dimensions", "DirectedInfinity", "Direction", "Discriminant", "Distribute", "Divergence",
			"DivideBy", "Divisible", "Divisors", "Do", "Dot", "Drop", "Eigenvalues", "Eigenvectors", "Element", "Eliminate",
			"EllipticE", "EllipticF", "EllipticPi", "End", "EndPackage", "Equal", "Equivalent", "Erf", "Erfc", "Erfi",
			"EuclidianDistance", "EulerE", "EulerPhi", "EvenQ", "Except", "Exp", "Expand", "ExpandAll", "ExpIntegralE",
			"ExpIntegralEi", "Exponent", "ExtendedGCD", "Extract", "Factor", "Factorial", "Factorial2", "FactorInteger",
			"FactorSquareFree", "FactorSquareFreeList", "FactorTerms", "Flatten", "Fibonacci", "FindRoot", "First", "Fit",
			"FixedPoint", "Floor", "Fold", "FoldList", "For", "FractionalPart", "FreeQ", "FresnelC", "FresnelS", "FrobeniusSolve",
			"FromCharacterCode", "FromContinuedFraction", "FullForm", "FullSimplify", "Function", "Gamma", "GCD", "GeometricMean",
			"Get", "Graphics", "Graphics3D", "Graphics3D", "Greater", "GreaterEqual", "GroebnerBasis", "Haversine",
			"HarmonicNumber", "Head", "HermiteH", "HilbertMatrix", "Hold", "HoldForm", "Horner", "HornerForm", "HurwitzZeta",
			"HypergeometricPFQ", "Hypergeometric2F1", "Identity", "IdentityMatrix", "If", "Im", "Implies", "Increment", "Inner",
			"Insert", "IntegerExponent", "IntegerPart", "IntegerPartitions", "IntegerQ", "Integrate", "InterpolatingFunction",
			"InterpolatingPolynomial", "Intersection", "Inverse", "InverseErf", "InverseFunction", "InverseHaversine",
			"JacobiMatrix", "JacobiSymbol", "JavaForm", "Join", "KOrderlessPartitions", "KPartitions", "Last", "LCM", "LeafCount",
			"LaguerreL", "LaplaceTransform", "LegendreP", "Length", "Less", "LessEqual", "LetterQ", "Level", "Limit", "Line",
			"LinearProgramming", "LinearSolve", "List", "ListQ", "Log", "Log2", "Log10", "LogGamma", "LogicalExpand",
			"LogIntegral", "LowerCaseQ", "LUDecomposition", "ManhattanDistance", "Map", "MapAll", "MapThread", "MatchQ",
			"MathMLForm", "MatrixForm", "MatrixPower", "MatrixQ", "MatrixRank", "Max", "Mean", "Median", "MemberQ", "MessageName",
			"Min", "Mod", "Module", "MoebiusMu", "MonomialList", "Most", "Multinomial", "Names", "Nand", "Negative", "Nest",
			"NestList", "NestWhile", "NestWhileList", "NextPrime", "NFourierTransform", "NIntegrate", "NMaximize", "NMinimize",
			"NonCommutativeMultiply", "NonNegative", "Nor", "Normalize", "Norm", "Not", "NRoots", "NSolve", "NullSpace", "NumberQ",
			"Numerator", "NumericQ", "OddQ", "Options", "Or", "Order", "OrderedQ", "Out", "Outer", "Package", "PadLeft",
			"PadRight", "ParametricPlot", "Part", "Partition", "Pattern", "PatternTest", "Permutations", "Piecewise", "Plot",
			"Plot3D", "Plus", "Pochhammer", "PolyGamma", "PolyLog", "PolynomialExtendedGCD", "PolynomialGCD", "PolynomialLCM",
			"PolynomialQ", "PolynomialQuotient", "PolynomialQuotientRemainder", "PolynomialRemainder", "Position", "Positive",
			"PossibleZeroQ", "Power", "PowerExpand", "PowerMod", "PreDecrement", "PreIncrement", "Prepend", "PrependTo", "Prime",
			"PrimeQ", "PrimitiveRoots", "Print", "Product", "ProductLog", "Quiet", "Quotient", "RandomInteger", "RandomReal",
			"RandomSample", "Range", "Rational", "Rationalize", "Re", "Reap", "Reduce", "Refine", "Repeated", "RepeatedNull",
			"Replace", "ReplaceAll", "ReplacePart", "ReplaceRepeated", "Rest", "Resultant", "Return", "Reverse", "Riffle",
			"RootIntervals", "RootOf", "Roots", "Surd", "RotateLeft", "RotateRight", "Round", "RowReduce", "Rule", "RuleDelayed",
			"SameQ", "Scan", "Sec", "Sech", "Select", "Sequence", "Series", "SeriesData", "Set", "SetAttributes", "SetDelayed",
			"Show", "Sign", "SignCmp", "Simplify", "Sin", "Sinc", "SingularValueDecomposition", "Sinh", "SinIntegral",
			"SinhIntegral", "Solve", "Sort", "Sow", "Sqrt", "SquaredEuclidianDistance", "SquareFreeQ", "StirlingS2", "StringDrop",
			"StringJoin", "StringLength", "StringTake", "Subfactorial", "Subscript", "Subsuperscript", "Subsets", "SubtractFrom",
			"Sum", "Superscript", "Switch", "SyntaxLength", "SyntaxQ", "Table", "Take", "Tan", "Tanh", "Taylor", "TeXForm",
			"Thread", "Through", "Throw", "TimeConstrained", "Times", "TimesBy", "Timing", "ToCharacterCode", "Together",
			"ToString", "Total", "ToUnicode", "Tr", "Trace", "Transpose", "TrigExpand", "TrigReduce", "TrigToExp", "TrueQ",
			"Tuples", "Unequal", "Unevaluated", "Union", "Unique", "UnitStep", "UnitVector", "UnsameQ", "Unset", "UpperCaseQ",
			"UpSet", "UpSetDelayed", "ValueQ", "VandermondeMatrix", "Variables", "VectorAngle", "VectorQ", "Which", "While", "Xor",
			"Zeta" };

	public static Map<String, Integer> RUBI_STATISTICS_MAP;

	public static final Map<String, String> PREDEFINED_SYMBOLS_MAP = new HashMap<String, String>(997);

	private final static String[] ALIASES_STRINGS = { "ACos", "ASin", "ATan", "ACosh", "ASinh", "ATanh", "Diff", "EvalF", "Int",
			"Ln", "Trunc", "NthRoot", "Root" };
	private final static String[] ALIASES_SUBSTITUTES = { "ArcCos", "ArcSin", "ArcTan", "ArcCosh", "ArcSinh", "ArcTanh", "D", "N",
			"Integrate", "Log", "IntegerPart", "Surd", "Surd" };

	/**
	 * Aliases which are mapped to the standard function symbols.
	 */
	public static final Map<String, String> PREDEFINED_ALIASES_MAP = new HashMap<String, String>(97);

	public static final String TIMES_STRING = Config.PARSER_USE_LOWERCASE_SYMBOLS ? "times" : "Times";
	public static final String TRUE_STRING = Config.PARSER_USE_LOWERCASE_SYMBOLS ? "true" : "True";

	private static SuggestTree SUGGEST_TREE = null;

	public static SuggestTree getSuggestTree() {
		if (SUGGEST_TREE == null) {
			SUGGEST_TREE = new SuggestTree(FUNCTION_STRINGS.length);
			for (String str : FUNCTION_STRINGS) {
				SUGGEST_TREE.put(str, 1);
			}
		}
		return SUGGEST_TREE;
	}

	static {
		for (String str : UPPERCASE_SYMBOL_STRINGS) {
			// these constants must be written in upper case characters
			PREDEFINED_SYMBOLS_MAP.put(str, str);
		}
		for (String str : SYMBOL_STRINGS) {
			PREDEFINED_SYMBOLS_MAP.put(str.toLowerCase(Locale.ENGLISH), str);
		}
		for (String str : FUNCTION_STRINGS) {
			PREDEFINED_SYMBOLS_MAP.put(str.toLowerCase(Locale.ENGLISH), str);
		}
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			for (int i = 0; i < ALIASES_STRINGS.length; i++) {
				PREDEFINED_ALIASES_MAP.put(ALIASES_STRINGS[i].toLowerCase(Locale.ENGLISH), ALIASES_SUBSTITUTES[i]); // YMBOLS[i]);
			}
		}
		if (Config.RUBI_CONVERT_SYMBOLS) {
			for (int i = 0; i < ALIASES_STRINGS.length; i++) {
				PREDEFINED_SYMBOLS_MAP.put(ALIASES_STRINGS[i].toLowerCase(Locale.ENGLISH), ALIASES_STRINGS[i]);
			}
		}
		if (Config.RUBI_CONVERT_SYMBOLS) {
			RUBI_STATISTICS_MAP = new TreeMap<String, Integer>();
		}

	}
	/**
	 * Typical instance of an ASTNode to IExpr converter
	 */
	public final static AST2Expr CONST = new AST2Expr();

	public final static AST2Expr CONST_LC = new AST2Expr(true);

	private int fPrecision;

	private boolean fLowercaseEnabled;

	/**
	 * 
	 * @param sType
	 * @param tType
	 * @deprecated
	 */
	public AST2Expr(final Class<ASTNode> sType, final Class<IExpr> tType) {
		this(false);
	}

	public AST2Expr() {
		this(false);
	}

	public AST2Expr(boolean lowercaseEnabled) {
		super();
		fLowercaseEnabled = lowercaseEnabled;
	}

	/**
	 * Converts a parsed FunctionNode expression into an IAST expression
	 */
	public IAST convert(IAST ast, FunctionNode functionNode) throws ConversionException {
		ast.set(0, convert(functionNode.get(0)));
		for (int i = 1; i < functionNode.size(); i++) {
			ast.add(convert(functionNode.get(i)));
		}
		return ast;
	}

	public IExpr convert(ASTNode node, EvalEngine engine) throws ConversionException {
		fPrecision = 15;
		if (engine != null) {
			fPrecision = engine.getNumericPrecision();
		}
		return convert(node);
	}

	/**
	 * Converts a parsed ASTNode expression into an IExpr expression
	 */
	public IExpr convert(ASTNode node) throws ConversionException {
		if (node == null) {
			return null;
		}

		if (node instanceof FunctionNode) {
			final FunctionNode functionNode = (FunctionNode) node;
			int size = functionNode.size();
			IAST ast;
			switch (size) {
			case 1:
				ast = F.headAST0(convert(functionNode.get(0)));
				break;
			case 2:
				ast = F.unaryAST1(convert(functionNode.get(0)), convert(functionNode.get(1)));
				break;
			case 3:
				ast = F.binaryAST2(convert(functionNode.get(0)), convert(functionNode.get(1)), convert(functionNode.get(2)));
				break;
			case 4:
				ast = F.ternaryAST3(convert(functionNode.get(0)), convert(functionNode.get(1)), convert(functionNode.get(2)),
						convert(functionNode.get(3)));
				break;
			default:
				ast = F.ast(convert(functionNode.get(0)), functionNode.size(), false);
				for (int i = 1; i < functionNode.size(); i++) {
					ast.add(convert(functionNode.get(i)));
				}
			}

			IExpr head = ast.head();
			if (ast.isAST(F.N, 3)) {
				try {
					int precision = Validate.checkIntType(ast.arg2());
					if (EvalEngine.isApfloat(precision)) {
						fPrecision = precision;
						ast.set(1, convert(functionNode.get(1)));
					}
					return ast;
				} catch (WrongArgumentType wat) {

				}
			} else if (ast.isAST(F.Sqrt, 2)) {
				// rewrite from input: Sqrt(x) => Power(x, 1/2)
				return F.Power(ast.arg1(), F.C1D2);
			} else if (ast.isAST(F.Exp, 2)) {
				// rewrite from input: Exp(x) => E^x
				return F.Power(F.E, ast.arg1());
			} else if (ast.isPower() && ast.arg1().isPower() && ast.arg2().isMinusOne()) {
				IAST arg1 = (IAST) ast.arg1();
				if (arg1.arg2().isNumber()) {
					// Division operator
					// rewrite from input: Power(Power(x, <number>),-1) => Power(x, - <number>)
					return F.Power(arg1.arg1(), ((INumber) arg1.arg2()).negate());
				}
			} else if (ast.isASTSizeGE(F.GreaterEqual, 3)) {
				ISymbol compareHead = F.Greater;
				return rewriteLessGreaterAST(ast, compareHead);
			} else if (ast.isASTSizeGE(F.Greater, 3)) {
				ISymbol compareHead = F.GreaterEqual;
				return rewriteLessGreaterAST(ast, compareHead);
			} else if (ast.isASTSizeGE(F.LessEqual, 3)) {
				ISymbol compareHead = F.Less;
				return rewriteLessGreaterAST(ast, compareHead);
			} else if (ast.isASTSizeGE(F.Less, 3)) {
				ISymbol compareHead = F.LessEqual;
				return rewriteLessGreaterAST(ast, compareHead);
			} else if (head.equals(F.PatternHead)) {
				final IExpr expr = Pattern.CONST.evaluate(ast);
				if (expr != null) {
					return expr;
				}
			} else if (head.equals(F.BlankHead)) {
				final IExpr expr = Blank.CONST.evaluate(ast);
				if (expr != null) {
					return expr;
				}
			} else if (head.equals(F.Complex)) {
				final IExpr expr = Complex.CONST.evaluate(ast);
				if (expr != null) {
					return expr;
				}
			} else if (head.equals(F.Rational)) {
				final IExpr expr = Rational.CONST.evaluate(ast);
				if (expr != null) {
					return expr;
				}
			}
			return ast;
		}
		if (node instanceof SymbolNode) {
			String nodeStr = node.getString();
			return convertSymbol(nodeStr);
		}
		// because of inheritance: check Pattern3Node before Pattern2Node before PatternNode
		if (node instanceof Pattern3Node) {
			final Pattern3Node p3n = (Pattern3Node) node;
			SymbolNode sn = p3n.getSymbol();
			return F.$ps((ISymbol) convert(sn), convert(p3n.getConstraint()), p3n.isDefault(), true);
		}
		if (node instanceof Pattern2Node) {
			final Pattern2Node p2n = (Pattern2Node) node;
			SymbolNode sn = p2n.getSymbol();
			return F.$ps((ISymbol) convert(sn), convert(p2n.getConstraint()), p2n.isDefault(), false);
		}
		if (node instanceof PatternNode) {
			final PatternNode pn = (PatternNode) node;
			SymbolNode sn = pn.getSymbol();
			if (sn == null) {
				return F.$b(convert(pn.getConstraint())); // TODO , p2n.isDefault());
			}
			return F.$p((ISymbol) convert(pn.getSymbol()), convert(pn.getConstraint()), pn.isDefault());
		}

		if (node instanceof IntegerNode) {
			final IntegerNode integerNode = (IntegerNode) node;
			final String iStr = integerNode.getString();
			if (iStr != null) {
				return F.integer(iStr, integerNode.getNumberFormat());
			}
			return F.integer(integerNode.getIntValue());
		}
		if (node instanceof FractionNode) {
			FractionNode fr = (FractionNode) node;
			IInteger numerator = (IInteger) convert(fr.getNumerator());
			IInteger denominator = (IInteger) convert(fr.getDenominator());
			if (denominator.isZero()) {
				return F.Rational(fr.isSign() ? numerator.negate() : numerator, denominator);
			}
			return F.fraction(numerator, fr.isSign() ? (IInteger) denominator.negate() : denominator);
		}
		if (node instanceof StringNode) {
			return F.stringx(node.getString());
		}
		if (node instanceof FloatNode) {
			String nStr = node.getString();
			String floatStr = nStr;
			int index = nStr.indexOf("*^");
			int exponent = 1;
			if (index > 0) {
				floatStr = nStr.substring(0, index);
				exponent = Integer.parseInt(nStr.substring(index + 2));
			}
			if (EvalEngine.isApfloat(fPrecision)) {
				Apfloat apfloatValue = new Apfloat(floatStr, fPrecision);
				if (exponent != 1) {
					// value * 10 ^ exponent
					return F.num(apfloatValue.multiply(ApfloatMath.pow(new Apint(10), new Apint(exponent))));
				}
				return F.num(apfloatValue);
			}
			double doubleValue = Double.parseDouble(floatStr);
			if (exponent != 1) {
				// value * 10 ^ exponent
				return F.num(doubleValue * Math.pow(10, exponent));
			}
			return F.num(doubleValue);
		}

		return F.retrieveSymbol(node.toString());
	}

	public IExpr convertSymbol(final String nodeStr) {
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			if (nodeStr.length() == 1) {
				if (nodeStr.equals("I")) {
					// special - convert on input
					return F.CI;
				}
				return F.retrieveSymbol(nodeStr);
			}
			String lowercaseStr = nodeStr.toLowerCase(Locale.ENGLISH);
			if (lowercaseStr.equals("infinity")) {
				// special - convert on input
				return F.CInfinity;
			} else if (lowercaseStr.equals("complexinfinity")) {
				// special - convert on input
				return F.CComplexInfinity;
			}
			String temp = PREDEFINED_ALIASES_MAP.get(lowercaseStr);
			if (temp != null) {
				return F.retrieveSymbol(temp);
			}
			return F.retrieveSymbol(lowercaseStr);
		} else {
			String lowercaseStr = nodeStr;
			if (fLowercaseEnabled) {
				lowercaseStr = nodeStr.toLowerCase(Locale.ENGLISH);
				String temp = PREDEFINED_SYMBOLS_MAP.get(lowercaseStr);
				if (temp != null) {
					lowercaseStr = temp;
				}
			}

			if (Config.RUBI_CONVERT_SYMBOLS) {
				Integer num = RUBI_STATISTICS_MAP.get(lowercaseStr);
				if (num == null) {
					RUBI_STATISTICS_MAP.put(lowercaseStr, 1);
				} else {
					RUBI_STATISTICS_MAP.put(lowercaseStr, num + 1);
				}
			}

			if (lowercaseStr.equals("I")) {
				// special - convert on input
				return F.CI;
			} else if (lowercaseStr.equals("Infinity")) {
				// special - convert on input
				return F.CInfinity;
			}
			return F.retrieveSymbol(lowercaseStr);
		}
	}

	/**
	 * Convert less or greter relations on input. Example: convert expressions like <code>a<b<=c</code> to
	 * <code>Less[a,b]&&LessEqual[b,c]</code>.
	 * 
	 * @param ast
	 * @param compareHead
	 * @return
	 */
	private IExpr rewriteLessGreaterAST(final IAST ast, ISymbol compareHead) {
		IExpr temp;
		boolean evaled = false;
		IAST andAST = F.ast(F.And);
		for (int i = 1; i < ast.size(); i++) {
			temp = ast.get(i);
			if (temp.isASTSizeGE(compareHead, 3)) {
				IAST lt = (IAST) temp;
				andAST.add(lt);
				ast.set(i, lt.get(lt.size() - 1));
				evaled = true;
			}
		}
		if (evaled) {
			andAST.add(ast);
			return andAST;
		} else {
			return ast;
		}
	}
}
