package org.matheclipse.core.convert;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.builtin.PatternMatching;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.util.SuggestTree;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
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
import org.matheclipse.parser.client.eval.DoubleNode;

/**
 * Converts a parsed <code>org.matheclipse.parser.client.ast.ASTNode</code> expression into an IExpr expression
 * 
 */
public class AST2Expr {

	public final static String[] UPPERCASE_SYMBOL_STRINGS = { "D", "E", "I", "N", "O" };

	public final static String[] DOLLAR_STRINGS = { "$Aborted" };

	public final static String[] SYMBOL_STRINGS = { "All", "Algebraics", "Automatic", "Axes", "AxesOrigin", "AxesStyle",
			"Background", "Booleans", "CharacterEncoding", "ComplexInfinity", "Catalan", "Complexes",
			"ComplexityFunction", "Constant", "Degree", "Disputed", "EulerGamma", "False", "Flat", "Glaisher",
			"GoldenRatio", "HoldAll", "HoldFirst", "HoldRest", "Indeterminate", "Infinity", "Integer", "Integers",
			"Khinchin", "Listable", "MaxIterations", "MaxPoints", "Method", "Modulus", "NHoldAll", "NHoldFirst",
			"NHoldRest", "None", "Nonexistent", "NotApplicable", "NotAvailable", "Null", "NumericFunction",
			"OneIdentity", "Orderless", "Pi", "PrecisionGoal", "Primes", "Rationals", "Real", "Reals", "Second", "Slot",
			"SlotSequence", "String", "Symbol", "TooLarge", "Trig", "True", "Variable", "White", "Unknown" };

	public final static String[] FUNCTION_STRINGS = { "Abort", "Abs", "AbsArg", "Accumulate", "AddTo", "AllTrue", "And",
			"AngleVector", "AnyTrue", "AntihermitianMatrixQ", "AntisymmetricMatrixQ", "AlgebraicNumber", "Alternatives",
			"Apart", "AppellF1", "Append", "AppendTo", "Apply", "ArcCos", "ArcCosh", "ArcCot", "ArcCoth", "ArcCsc",
			"ArcCsch", "ArcSec", "ArcSech", "ArcSin", "ArcSinh", "ArcTan", "ArcTanh", "Arg", "Array", "ArrayDepth",
			"ArrayPad", "Arrays", "ArrayQ", "Assumptions", "AtomQ", "Attributes", "Begin", "BeginPackage", "BellB", "BellY",
			"BernoulliB", "BernoulliDistribution", "BesselI", "BesselJ", "BesselK", "BesselY", "Beta",
			"BetaRegularized", "BinCounts", "Binomial", "BinomialDistribution", "BitLength", "Blank", "Block", "Boole",
			"BooleanQ", "BooleanConvert", "BooleanMinimize", "BooleanTable", "BooleanVariables", "BrayCurtisDistance",
			"Break", "CanberraDistance", "Cancel", "CarmichaelLambda", "CartesianProduct", "Cases", "CatalanNumber",
			"Catch", "Catenate", "CDF", "Ceiling", "CentralMoment", "CharacteristicPolynomial", "ChebyshevT",
			"ChebyshevU", "ChessboardDistance", "ChineseRemainder", "CholeskyDecomposition", "Chop", "CirclePoints",
			"Clear", "ClearAll", "ClearAttributes", "Clip", "Coefficient", "CoefficientList", "CoefficientRules",
			"Colon", "Collect", "Commonest", "Complement", "Compile", "Complex", "ComplexExpand", "ComposeList",
			"ComposeSeries", "Composition", "CompoundExpression", "Condition", "ConditionalExpression", "Conjugate",
			"ConjugateTranspose", "ConstantArray", "Continue", "ContinuedFraction", "Convergents", "ConvexHullMesh",
			"CoprimeQ", "Correlation", "Cos", "Cosh", "CosineDistance", "CosIntegral", "CoshIntegral", "Cot", "Coth",
			"Count", "Covariance", "Cross", "Csc", "Csch", "CubeRoot", "Curl", "Decrement", "Default", "Defer",
			"Definition", "Delete", "DeleteCases", "DeleteDuplicates", "Denominator", "Depth", "Derivative",
			"DesignMatrix", "Det", "Diagonal", "DiagonalMatrix", "DiceDissimilarity", "DigitQ", "Dimensions",
			"DiracDelta", "DiscreteDelta", "DiscreteUniformDistribution", "DirectedInfinity", "Direction",
			"Discriminant", "Distribute", "Distributed", "Divergence", "Divide", "DivideBy", "Divisible", "Divisors",
			"DivisorSigma", "Do", "Dot", "Drop", "DSolve", "EasterSunday", "Eigenvalues", "Eigenvectors", "Element",
			"ElementData", "Eliminate", "EllipticE", "EllipticF", "EllipticK", "EllipticPi", "End", "EndPackage",
			"Equal", "Equivalent", "Erf", "Erfc", "Erfi", "ErlangDistribution", "EuclideanDistance", "EulerE",
			"EulerPhi", "EvenQ", "ExactNumberQ", "Except", "Exists", "Exp", "Expand", "ExpandAll", "Expectation",
			"ExponentialDistribution", "ExpIntegralE", "ExpIntegralEi", "Exponent", "Export", "ExtendedGCD", "Extract",
			"Factor", "Factorial", "Factorial2", "FactorInteger", "FactorSquareFree", "FactorSquareFreeList",
			"FactorTerms", "Flatten", "FlattenAt", "Fibonacci", "FindInstance", "FindRoot", "First", "Fit",
			"FixedPoint", "FixedPointList", "Floor", "Fold", "FoldList", "For", "ForAll", "FourierMatrix",
			"FractionalPart", "FrechetDistribution", "FreeQ", "FresnelC", "FresnelS", "FrobeniusNumber",
			"FrobeniusSolve", "FromCharacterCode", "FromContinuedFraction", "FromDigits", "FromPolarCoordinates",
			"FullForm", "FullSimplify", "Function", "Gamma", "GammaDistribution", "GammaRegularized", "Gather", "GCD",
			"GegenbauerC", "GeometricDistribution", "GeometricMean", "Get", "Graphics", "Graphics3D", "Greater",
			"GreaterEqual", "GroebnerBasis", "GumbelDistribution", "Haversine", "HarmonicNumber", "Head",
			"HeavisideTheta", "HermiteH", "HermitianMatrixQ", "HilbertMatrix", "Hold", "HoldForm", "HoldPattern",
			"Horner", "HornerForm", "HurwitzZeta", "HypergeometricDistribution", "HypergeometricPFQ",
			"Hypergeometric1F1", "Hypergeometric2F1", "HypergeometricPFQRegularized", "Identity", "IdentityMatrix",
			"If", "Im", "Implies", "Import", "Increment", "Inequality", "InexactNumberQ", "Inner", "InputForm",
			"Insert", "Information", "Interval", "IntegerDigits", "IntegerExponent", "IntegerLength", "IntegerPart",
			"IntegerPartitions", "IntegerQ", "Integrate", "Interpolation", "InterpolatingFunction",
			"InterpolatingPolynomial", "Intersection", "Inverse", "InverseBetaRegularized", "InverseErf", "InverseErfc",
			"InverseFunction", "InverseGammaRegularized", "InverseHaversine", "InverseLaplaceTransform",
			"InverseSeries", "JaccardDissimilarity", "JacobiMatrix", "JacobiSymbol", "JacobiZeta", "JavaForm", "Join",
			"KOrderlessPartitions", "KPartitions", "KroneckerDelta", "Kurtosis", "Last", "LCM", "LeafCount",
			"LaguerreL", "LaplaceTransform", "LeastSquares", "LegendreP", "LegendreQ", "Length", "Less", "LessEqual",
			"LetterQ", "Level", "LevelQ", "Limit", "Line", "LinearModelFit", "LinearProgramming", "LinearSolve",
			"LiouvilleLambda", "List", "ListConvolve", "ListCorrelate", "ListQ", "Literal", "Log", "Log2", "Log10",
			"LogGamma", "LogNormalDistribution", "LogicalExpand", "LogisticSigmoid", "LogIntegral", "LowerCaseQ",
			"LowerTriangularize", "LucasL", "LUDecomposition", "MachineNumberQ", "ManhattanDistance", "MangoldtLambda",
			"MantissaExponent", "Map", "MapAt", "MapAll", "MapIndexed", "MapThread", "MatchingDissimilarity", "MatchQ",
			"MathMLForm", "MatrixForm", "MatrixMinimalPolynomial", "MatrixPower", "MatrixQ", "MatrixRank", "Max",
			"Mean", "MeanDeviation", "Median", "MeijerG", "MemberQ", "MeshRange", "MessageName",
			"MersennePrimeExponent", "MersennePrimeExponentQ", "Min", "MinimalPolynomial", "Minus", "Missing",
			"MissingQ", "Mod", "Module", "MoebiusMu", "MonomialList", "Most", "Multinomial", "MultiplicativeOrder",
			"NakagamiDistribution", "Names", "Nand", "NDSolve", "Nearest", "Negative", "Nest", "NestList", "NestWhile",
			"NestWhileList", "NextPrime", "NFourierTransform", "NIntegrate", "NMaximize", "NMinimize",
			"NonCommutativeMultiply", "NonNegative", "NonPositive", "NoneTrue", "Nor", "Normal", "Normalize", "Norm",
			"NormalDistribution", "Not", "NotListQ", "NRoots", "NSolve", "NullSpace", "NumberFieldRootsOfUnity",
			"NumberQ", "Numerator", "NumericQ", "OddQ", "Operate", "Optional", "Options", "Or", "Order", "Ordering",
			"OrderedQ", "Orthogonalize", "OrthogonalMatrixQ", "Out", "Outer", "OutputForm", "Package", "PadLeft",
			"PadRight", "ParametricPlot", "Part", "Partition", "PartitionsP", "PartitionsQ", "PerfectNumber",
			"PerfectNumberQ", "Pattern", "PatternTest", "PDF", "Permutations", "Piecewise", "Plot", "PlotRange",
			"Plot3D", "Plus", "Pochhammer", "Point", "PoissonDistribution", "PolyGamma", "Polygon", "PolyLog",
			"PolynomialExtendedGCD", "PolynomialGCD", "PolynomialLCM", "PolynomialQ", "PolynomialQuotient",
			"PolynomialQuotientRemainder", "PolynomialRemainder", "Position", "Positive", "PossibleZeroQ", "Power",
			"PowerExpand", "PowerMod", "Precision", "PreDecrement", "PreIncrement", "PrePlus", "Prepend", "PrependTo",
			"Prime", "PrimeOmega", "PrimePi", "PrimePowerQ", "PrimeQ", "PrimitiveRootList", "Print", "Product",
			"ProductLog", "Projection", "PseudoInverse", "Put", "QRDecomposition", "Quantile", "Quiet", "Quit",
			"Quotient", "QuotientRemainder", "RandomChoice", "RandomInteger", "RandomReal", "RandomSample",
			"RandomVariate", "Range", "Rational", "Rationalize", "Re", "RealNumberQ", "Reap", "Rectangle", "Reduce",
			"Refine", "Repeated", "RepeatedNull", "Replace", "ReplaceAll", "ReplaceList", "ReplacePart",
			"ReplaceRepeated", "Rest", "Resultant", "Return", "Reverse", "Riffle", "RogersTanimotoDissimilarity",
			"RootIntervals", "Root", "RootOf", "Roots", "RussellRaoDissimilarity", "Surd", "RotateLeft", "RotateRight",
			"Round", "RowReduce", "Rule", "RuleDelayed", "SameQ", "SatisfiabilityCount", "SatisfiabilityInstances",
			"SatisfiableQ", "Scan", "Sec", "Sech", "Select", "Sequence", "Series", "SeriesCoefficient", "SeriesData",
			"Set", "SetAttributes", "SetDelayed", "Share", "Show", "Sign", "SignCmp", "Simplify", "Sin", "Sinc",
			"SingularValueDecomposition", "Sinh", "SinIntegral", "SinhIntegral", "Skewness", "SokalSneathDissimilarity",
			"Solve", "Sort", "Sow", "Span", "Split", "SplitBy", "Sqrt", "SquaredEuclideanDistance", "SquareFreeQ",
			"SquareMatrixQ", "StandardDeviation", "StandardForm", "Standardize", "StieltjesGamma", "StirlingS1",
			"StirlingS2", "StringDrop", "StringJoin", "StringLength", "StringTake", "StruveH", "StruveL",
			"StudentTDistribution", "Subdivide", "Subfactorial", "Subscript", "Subsuperscript", "Subsets", "Subtract",
			"SubtractFrom", "Sum", "Superscript", "SurfaceGraphics", "Switch", "SyntaxLength", "SymbolName", "SymbolQ",
			"SymmetricMatrixQ", "SyntaxQ", "Table", "Take", "Tally", "Tan", "Tanh", "TautologyQ", "Taylor",
			"TensorDimensions", "TensorProduct", "TensorRank", "TeXForm", "Thread", "Through", "Throw",
			"TimeConstrained", "Times", "TimesBy", "Timing", "ToCharacterCode", "ToeplitzMatrix", "Together",
			"ToPolarCoordinates", "ToRadicals", "ToString", "Total", "ToUnicode", "Tr", "Trace", "TraditionalForm",
			"Transpose", "TrigExpand", "TrigReduce", "TrigToExp", "TrueQ", "Tuples", "Undefined", "Unequal",
			"Unevaluated", "Union", "Unique", "UnitaryMatrixQ", "Unitize", "UnitStep", "UnitVector", "UnsameQ", "Unset",
			"UpperCaseQ", "UpperTriangularize", "UpSet", "UpSetDelayed", "ValueQ", "VandermondeMatrix", "Variables",
			"Variance", "VectorAngle", "VectorQ", "WeibullDistribution", "Which", "While", "With", "Xor",
			"YuleDissimilarity", "Zeta" };

	public static Map<String, Integer> RUBI_STATISTICS_MAP;

	public static final Map<String, String> PREDEFINED_SYMBOLS_MAP = new HashMap<String, String>(997);

	private final static String[] ALIASES_STRINGS = { "ACos", "ASin", "ATan", "ACosh", "ASinh", "ATanh", "Diff",
			"EvalF", "Int", "Ln", "Trunc", "NthRoot" };
	private final static String[] ALIASES_SUBSTITUTES = { "ArcCos", "ArcSin", "ArcTan", "ArcCosh", "ArcSinh", "ArcTanh",
			"D", "N", "Integrate", "Log", "IntegerPart", "Surd" };

	/**
	 * Aliases which are mapped to the standard function symbols.
	 */
	public static final Map<String, String> PREDEFINED_ALIASES_MAP = new HashMap<String, String>(97);

	public static final String TIMES_STRING = Config.PARSER_USE_LOWERCASE_SYMBOLS ? "times" : "Times";
	public static final String TRUE_STRING = Config.PARSER_USE_LOWERCASE_SYMBOLS ? "true" : "True";

	private static SuggestTree SUGGEST_TREE = null;

	public static SuggestTree getSuggestTree() {
		synchronized (AST2Expr.class) {
			if (SUGGEST_TREE == null) {
				SUGGEST_TREE = new SuggestTree(100);
				synchronized (SUGGEST_TREE) {
					for (String str : FUNCTION_STRINGS) {
						SUGGEST_TREE.put(str, 2);
					}
					for (String str : SYMBOL_STRINGS) {
						SUGGEST_TREE.put(str, 1);
					}
					for (String str : DOLLAR_STRINGS) {
						SUGGEST_TREE.put(str, 1);
					}
				}
			}
		}
		return SUGGEST_TREE;
	}

	static {
		for (String str : UPPERCASE_SYMBOL_STRINGS) {
			// these constants must be written in upper case characters
			PREDEFINED_SYMBOLS_MAP.put(str, str);
		}
		for (String str : DOLLAR_STRINGS) {
			PREDEFINED_SYMBOLS_MAP.put(str.toLowerCase(Locale.ENGLISH), str);
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

	private int fPrecision;

	private boolean fLowercaseEnabled;

	private EvalEngine fEngine;

	/**
	 * 
	 * @param sType
	 * @param tType
	 * @deprecated
	 */
	@Deprecated
	public AST2Expr(final Class<ASTNode> sType, final Class<IExpr> tType) {
		this(false, EvalEngine.get());
	}

	public AST2Expr() {
		this(false, EvalEngine.get());
	}

	public AST2Expr(EvalEngine engine) {
		this(false, engine);
	}

	public AST2Expr(boolean lowercaseEnabled, EvalEngine engine) {
		super();
		fLowercaseEnabled = lowercaseEnabled;
		fEngine = EvalEngine.get();
	}

	/**
	 * Converts a parsed FunctionNode expression into an IAST expression.
	 * 
	 * @param functionNode
	 *            the parsed elements which should be added to the <code>IAST</code>
	 * @param ast
	 *            the empty <code>IAST</code> instance without any elements
	 * @return the <code>ast</code>with the added elements
	 * @throws ConversionException
	 */
	public IAST convert(FunctionNode functionNode, IASTAppendable ast) throws ConversionException {
		ast.set(0, convertNode(functionNode.get(0)));
		for (int i = 1; i < functionNode.size(); i++) {
			ast.append(convertNode(functionNode.get(i)));
		}
		return ast;
	}

	public IExpr convert(ASTNode node) throws ConversionException {
		fPrecision = fEngine.getNumericPrecision();
		return convertNode(node);
	}

	/**
	 * Converts a parsed ASTNode expression into an IExpr expression
	 * 
	 * @param engine
	 *            TODO
	 */
	private IExpr convertNode(ASTNode node) throws ConversionException {
		if (node == null) {
			return null;
		}

		if (node instanceof FunctionNode) {
			final FunctionNode functionNode = (FunctionNode) node;
			int size = functionNode.size();
			IASTMutable ast;
			switch (size) {
			case 1:
				ast = (IASTMutable) F.headAST0(convertNode(functionNode.get(0)));
				break;
			case 2:
				ast = (IASTMutable) F.unaryAST1(convertNode(functionNode.get(0)), convertNode(functionNode.get(1)));
				break;
			case 3:
				ast = (IASTMutable) F.binaryAST2(convertNode(functionNode.get(0)), convertNode(functionNode.get(1)),
						convertNode(functionNode.get(2)));
				break;
			case 4:
				ast = (IASTMutable) F.ternaryAST3(convertNode(functionNode.get(0)), convertNode(functionNode.get(1)),
						convertNode(functionNode.get(2)), convertNode(functionNode.get(3)));
				break;
			default:
				ast = F.ast(convertNode(functionNode.get(0)), functionNode.size(), false);
				for (int i = 1; i < functionNode.size(); i++) {
					((IASTAppendable) ast).append(convertNode(functionNode.get(i)));
				}
			}

			int functionID = ast.headID();
			if (functionID > ID.UNKNOWN) {
				IExpr expr;
				switch (functionID) {
				case ID.N:
					if (ast.isAST2()) {
						try {
							int precision = Validate.checkIntType(ast.arg2());
							if (EvalEngine.isApfloat(precision)) {
								fPrecision = precision;
								ast.set(1, convertNode(functionNode.get(1)));
							}
							return ast;
						} catch (WrongArgumentType wat) {

						}
					}
					break;
				case ID.Sqrt:
					if (ast.isAST1()) {
						// rewrite from input: Sqrt(x) => Power(x, 1/2)
						return F.Power(ast.arg1(), F.C1D2);
					}
					break;
				case ID.Exp:
					if (ast.isAST1()) {
						// rewrite from input: Exp(x) => E^x
						return F.Power(F.E, ast.arg1());
					}
					break;
				case ID.Power:
					if (ast.isPower() && ast.base().isPower() && ast.exponent().isMinusOne()) {
						IAST arg1Power = (IAST) ast.base();
						if (arg1Power.exponent().isNumber()) {
							// Division operator
							// rewrite from input: Power(Power(x, <number>),-1) =>
							// Power(x, - <number>)
							return F.Power(arg1Power.base(), ((INumber) arg1Power.exponent()).negate());
						}
					}
					break;
				case ID.GreaterEqual:
					if (ast.isASTSizeGE(F.GreaterEqual, 3)) {
						ISymbol compareHead = F.Greater;
						return rewriteLessGreaterAST(ast, compareHead);
					}
					break;
				case ID.Greater:
					if (ast.isASTSizeGE(F.Greater, 3)) {
						ISymbol compareHead = F.GreaterEqual;
						return rewriteLessGreaterAST(ast, compareHead);
					}
					break;
				case ID.LessEqual:
					if (ast.isASTSizeGE(F.LessEqual, 3)) {
						ISymbol compareHead = F.Less;
						return rewriteLessGreaterAST(ast, compareHead);
					}
					break;
				case ID.Less:
					if (ast.isASTSizeGE(F.Less, 3)) {
						ISymbol compareHead = F.LessEqual;
						return rewriteLessGreaterAST(ast, compareHead);
					}
				case ID.Pattern:
					expr = PatternMatching.Pattern.CONST.evaluate(ast, fEngine);
					if (expr.isPresent()) {
						return expr;
					}
					break;
				case ID.Blank:
					expr = PatternMatching.Blank.CONST.evaluate(ast, fEngine);
					if (expr.isPresent()) {
						return expr;
					}
					break;
				case ID.Complex:
					expr = Arithmetic.CONST_COMPLEX.evaluate(ast, fEngine);
					if (expr.isPresent()) {
						return expr;
					}
					break;
				case ID.Rational:
					expr = Arithmetic.CONST_RATIONAL.evaluate(ast, fEngine);
					if (expr.isPresent()) {
						return expr;
					}
					break;
				}
			}
			return ast;
		}
		if (node instanceof SymbolNode) {
			String nodeStr = node.getString();
			return convertSymbol(nodeStr);
		}
		// because of inheritance: check Pattern3Node before Pattern2Node before
		// PatternNode
		if (node instanceof Pattern3Node) {
			final Pattern3Node p3n = (Pattern3Node) node;
			SymbolNode sn = p3n.getSymbol();
			return F.$ps((ISymbol) convertNode(sn), convertNode(p3n.getConstraint()), p3n.isDefault(), true);
		}
		if (node instanceof Pattern2Node) {
			final Pattern2Node p2n = (Pattern2Node) node;
			SymbolNode sn = p2n.getSymbol();
			return F.$ps((ISymbol) convertNode(sn), convertNode(p2n.getConstraint()), p2n.isDefault(), false);
		}
		if (node instanceof PatternNode) {
			final PatternNode pn = (PatternNode) node;
			SymbolNode sn = pn.getSymbol();
			if (sn == null) {
				return F.$b(convertNode(pn.getConstraint())); // TODO ,
																// p2n.isDefault());
			}
			ASTNode defaultValue = pn.getDefaultValue();
			if (defaultValue != null) {
				return F.$p((ISymbol) convertNode(pn.getSymbol()), convertNode(pn.getConstraint()),
						convertNode(defaultValue));
			}
			return F.$p((ISymbol) convertNode(pn.getSymbol()), convertNode(pn.getConstraint()), pn.isDefault());
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
			IInteger numerator = (IInteger) convertNode(fr.getNumerator());
			IInteger denominator = (IInteger) convertNode(fr.getDenominator());
			if (denominator.isZero()) {
				return F.Rational(fr.isSign() ? numerator.negate() : numerator, denominator);
			}
			if (denominator.isOne()) {
				return fr.isSign() ? numerator.negate() : numerator;
			}
			// return F.Rational(fr.isSign() ? numerator.negate() : numerator, denominator);
			return F.fraction(fr.isSign() ? numerator.negate() : numerator, denominator);
		}
		if (node instanceof StringNode) {
			return F.$str(node.getString());
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
		if (node instanceof DoubleNode) {

			return F.num(((DoubleNode) node).doubleValue());
		}

		return F.symbol(node.toString());
	}

	public IExpr convertSymbol(final String nodeStr) {
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			if (nodeStr.length() == 1) {
				if (nodeStr.equals("I")) {
					// special - convert on input
					return F.CI;
				}
				return F.symbol(nodeStr, fEngine);
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
				return F.symbol(temp, fEngine);
			}
			return F.symbol(lowercaseStr, fEngine);
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
			return F.symbol(lowercaseStr, fEngine);
		}
	}

	/**
	 * Convert less or greater relations on input. Example: convert expressions like <code>a &lt; b &lt;= c</code> to
	 * <code>Less[a,b]&&LessEqual[b,c]</code>.
	 * 
	 * @param ast
	 * @param compareHead
	 * @return
	 */
	public static IExpr rewriteLessGreaterAST(final IASTMutable ast, ISymbol compareHead) {
		IExpr temp;
		boolean evaled = false;
		IASTAppendable andAST = F.And();
		for (int i = 1; i < ast.size(); i++) {
			temp = ast.get(i);
			if (temp.isASTSizeGE(compareHead, 3)) {
				IAST lt = (IAST) temp;
				andAST.append(lt);
				ast.set(i, lt.last());
				evaled = true;
			}
		}
		if (evaled) {
			andAST.append(ast);
			return andAST;
		} else {
			return ast;
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < FUNCTION_STRINGS.length; i++) {
			System.out.println(FUNCTION_STRINGS[i]);
		}
	}
}
