package org.matheclipse.core.convert;

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
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.trie.SuggestTree;
import org.matheclipse.core.trie.Tries;
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

	public final static String[] UPPERCASE_SYMBOL_STRINGS = { "C", "D", "E", "I", "N", "O" };

	public final static String[] DOLLAR_STRINGS = { "$Aborted", "$Assumptions", "$Cancel", "$Context", "$CreationDate",
			"$ContextPath", "$DisplayFunction", "$Failed", "$HistoryLength", "$HomeDirectory", "$IterationLimit",
			"$Line", "$MachineEpsilon", "$MachinePrecision", "$MaxMachineNumber", "$MessageList", "$MinMachineNumber",
			"$OutputSizeLimit", "$PrePrint", "$PreRead", "$RecursionLimit", "$UserName", "$Version" };

	public final static String[] SYMBOL_STRINGS = { "All", "Algebraics", "Automatic", "Axes", "AxesOrigin", "AxesStyle",
			"Background", "Booleans", "CharacterEncoding", "ComplexInfinity", "Catalan", "Complexes",
			"ComplexityFunction", "Constant", "Degree", "DegreeLexicographic", "DegreeReverseLexicographic", "Disputed",
			"EliminationOrder", "EulerGamma", "Expression", "Extension", "False", "Flat", "Float", "Full",
			"GaussianIntegers", "General", "Glaisher", "GoldenAngle", "GoldenRatio", "Heads", "HoldAll", "HoldComplete",
			"HoldAllComplete", "HoldFirst", "HoldRest", "Indeterminate", "Infinity", "Integer", "Integers", "Khinchin",
			"Lexicographic", "Listable", "LongForm", "MaxIterations", "MaxPoints", "Method", "Modulus", "MonomialOrder",
			"NegativeDegreeLexicographic", "NegativeDegreeReverseLexicographic", "NegativeLexicographic", "NHoldAll",
			"NHoldFirst", "NHoldRest", "None", "Nothing", "Nonexistent", "NotApplicable", "NotAvailable", "Null",
			"Number", "NumericFunction", "OneIdentity", "Orderless", "Pi", "PrecisionGoal", "Primes", "Protected",
			"Rationals", "ReadProtected", "Real", "Reals", "SameTest", "Second", "SequenceHold", "Slot", "SlotSequence",
			"Strict", "String", "Symbol", "TooLarge", "Trig", "True", "Variable", "White", "Unknown", "ViewPoint" };

	public final static String[] FUNCTION_STRINGS = { "Abort", "Abs", "AbsArg", "AbsoluteCorrelation", "Accumulate",
			"AddTo", "AdjacencyMatrix", "AiryAi", "AiryAiPrime", "AiryBi", "AiryBiPrime", "AllTrue", "And",
			"AngleVector", "AnyTrue", "AntihermitianMatrixQ", "AntiSymmetric", "AntisymmetricMatrixQ", "Annuity",
			"AnnuityDue", "AlgebraicNumber", "Alternatives", "Apart", "AppellF1", "Append", "AppendTo", "Apply",
			"ArcCos", "ArcCosh", "ArcCot", "ArcCoth", "ArcCsc", "ArcCsch", "ArcSec", "ArcSech",
			"ArithmeticGeometricMean", "ArcSin", "ArcSinh", "ArcTan", "ArcTanh", "Arg", "ArgMax", "ArgMin", "Array",
			"ArrayDepth", "ArrayPad", "ArrayReshape", "Arrays", "ArrayQ", "Association", "AssociationQ", "Assumptions",
			"AtomQ", "Attributes", "BarChart", "BartlettWindow", "BaseForm", "Begin", "BeginPackage", "BellB", "BellY",
			"BernoulliB", "BernoulliDistribution", "BesselI", "BesselJ", "BesselJZero", "BesselK", "BesselY",
			"BesselYZero", "Beta", "BetaDistribution", "BetaRegularized", "BinarySerialize", "BinaryDeserialize",
			"BinCounts", "Binomial", "BinomialDistribution", "BitLength", "BlackmanHarrisWindow",
			"BlackmanNuttallWindow", "BlackmanWindow", "Blank", "BlankSequence", "BlankNullSequence", "Block", "Boole",
			"BooleanQ", "BooleanConvert", "BooleanMinimize", "BooleanTable", "BooleanVariables", "BrayCurtisDistance",
			"Break", "Button", "ByteArray", "ByteArrayQ", "ByteCount", "CanberraDistance", "Cancel", "CancelButton",
			"CarmichaelLambda", "CartesianProduct", "Cases", "CatalanNumber", "Catch", "Catenate", "CDF", "Ceiling",
			"CenterDot", "CentralMoment", "CForm", "CharacteristicPolynomial", "ChebyshevT", "ChebyshevU", "Check",
			"ChessboardDistance", "ChineseRemainder", "ChiSquareDistribution", "CholeskyDecomposition", "Chop",
			"CircleDot", "CirclePoints", "Clear", "ClearAll", "ClearAttributes", "Clip", "Coefficient",
			"CoefficientList", "CoefficientRules", "Colon", "Column", "Collect", "Commonest", "CompatibleUnitQ",
			"Complement", "Compile", "CompiledFunction", "Complex", "ComplexExpand", "ComposeList", "ComposeSeries",
			"Composition", "CompoundExpression", "Condition", "ConditionalExpression", "ConnectedGraphQ", "Conjugate",
			"ConjugateTranspose", "ConstantArray", "ContainsAll", "ContainsAny", "ContainsNone", "ContainsExactly",
			"ContainsOnly", "Context", "Continue", "ContinuedFraction", "Convergents", "ConvexHullMesh", "CoprimeQ",
			"Correlation", "Cos", "Cosh", "CosineDistance", "CosIntegral", "CoshIntegral", "Cot", "Coth", "Count",
			"Counts", "Covariance", "CreateDirectory", "Cross", "Csc", "Csch", "CubeRoot", "Curl", "Cyclotomic",
			"Decrement", "Default", "DefaultButton", "Defer", "Definition", "Delete", "DeleteCases", "DeleteDuplicates",
			"Denominator", "Depth", "Derivative", "DesignMatrix", "Det", "Diagonal", "DiagonalMatrix", "DialogInput",
			"DialogNotebook", "DialogReturn", "DiceDissimilarity", "Differences", "DigitQ", "Dimensions", "DiracDelta",
			"DirichletEta", "DiscreteDelta", "DiscreteUniformDistribution", "DirectedEdge", "DirectedInfinity",
			"Direction", "DirichletWindow", "Discriminant", "DisjointQ", "Distribute", "Distributed", "Div", "Divide",
			"DivideBy", "Divisible", "Divisors", "DivisorSigma", "Do", "Dot", "Drop", "Dynamic", "DSolve",
			"EasterSunday", "EdgeCount", "EdgeList", "EdgeQ", "EdgeWeight", "EffectiveInterest", "Eigenvalues",
			"Eigenvectors", "Element", "ElementData", "Eliminate", "EllipticE", "EllipticF", "EllipticK", "EllipticPi",
			"EllipticTheta", "End", "EndPackage", "Equal", "Equivalent", "Erf", "Erfc", "Erfi", "ErlangDistribution",
			"EuclideanDistance", "EulerE", "EulerianGraphQ", "EulerPhi", "Evaluate", "EvenQ", "ExactNumberQ", "Except",
			"Exists", "Exp", "Expand", "ExpandAll", "Expectation", "ExponentialDistribution", "ExpIntegralE",
			"ExpIntegralEi", "Exponent", "Export", "ExportString", "ExpToTrig", "ExtendedGCD", "Extract", "Factor",
			"Factorial", "Factorial2", "FactorInteger", "FactorSquareFree", "FactorSquareFreeList", "FactorTerms",
			"Flatten", "FlattenAt", "FlatTopWindow", "Fibonacci", "FindEulerianCycle", "FindFit", "FindEdgeCover",
			"FindIndependentEdgeSet", "FindIndependentVertexSet", "FindHamiltonianCycle", "FindInstance", "FindRoot",
			"FindShortestPath", "FindShortestTour", "FindSpanningTree", "FindVertexCover", "First", "Fit", "FiveNum",
			"FixedPoint", "FixedPointList", "Floor", "Fold", "FoldList", "For", "ForAll", "Fourier", "FourierMatrix",
			"FRatioDistribution", "FractionalPart", "FrechetDistribution", "FreeQ", "FresnelC", "FresnelS",
			"FrobeniusNumber", "FrobeniusSolve", "FromCharacterCode", "FromContinuedFraction", "FromDigits",
			"FromPolarCoordinates", "FullForm", "FullSimplify", "Function", "FunctionExpand", "FunctionRange", "Gamma",
			"GammaDistribution", "GammaRegularized", "Gather", "GatherBy", "GaussianMatrix", "GaussianWindow", "GCD",
			"GegenbauerC", "GeodesyData", "GeoDistance", "GeometricDistribution", "GeometricMean", "GeoPosition", "Get",
			"Grad", "Graph", "GraphCenter", "GraphData", "GraphDiameter", "Graphics", "Graphics3D", "GraphPeriphery",
			"GraphQ", "GraphRadius", "Greater", "GreaterEqual", "GroebnerBasis", "GumbelDistribution",
			"HamiltonianGraphQ", "HammingWindow", "HankelH1", "HankelH2", "HannWindow", "Haversine", "HarmonicMean",
			"HarmonicNumber", "Head", "HeavisideTheta", "HermiteH", "HermitianMatrixQ", "HilbertMatrix", "Histogram",
			"Hold", "HoldForm", "HoldPattern", "Horner", "HornerForm", "HurwitzZeta", "HypergeometricDistribution",
			"HypergeometricPFQ", "HypergeometricU", "Hypergeometric0F1", "Hypergeometric1F1",
			"Hypergeometric1F1Regularized", "Hypergeometric2F1", "HypergeometricPFQRegularized", "Identity",
			"IdentityMatrix", "If", "Im", "Implies", "Import", "Increment", "Inequality", "InexactNumberQ", "Infix",
			"Inner", "Input", "InputField", "InputForm", "InputString", "Insert", "Information", "Interval",
			"IntegerDigits", "IntegerExponent", "IntegerLength", "IntegerPart", "IntegerPartitions", "IntegerQ",
			"Integrate", "Interpolation", "InterpolatingFunction", "InterpolatingPolynomial", "IntersectingQ",
			"Interrupt", "Intersection", "Inverse", "InverseFourier", "InverseBetaRegularized", "InverseCDF",
			"InverseErf", "InverseErfc", "InverseFunction", "InverseGammaRegularized", "InverseHaversine",
			"InverseLaplaceTransform", "InverseSeries", "InverseWeierstrassP", "JaccardDissimilarity",
			"JacobiAmplitude", "JacobiMatrix", "JacobiSymbol", "JacobiCN", "JacobiDN", "JacobiSN", "JacobiZeta",
			"JavaForm", "JSForm", "JSFormData", "Join", "Key", "KeyExistsQ", "Keys", "KeySort", "KnownUnitQ",
			"KolmogorovSmirnovTest", "KOrderlessPartitions", "KPartitions", "KroneckerDelta", "Kurtosis", "Last", "LCM",
			"LeafCount", "LaguerreL", "LaplaceTransform", "LeastSquares", "LegendreP", "LegendreQ", "Length", "Less",
			"LessEqual", "LetterQ", "Level", "LevelQ", "Limit", "Line", "LinearModelFit", "LinearProgramming",
			"LinearRecurrence", "LinearSolve", "LiouvilleLambda", "List", "ListConvolve", "ListCorrelate",
			"ListLinePlot", "ListPlot", "ListPlot3D", "ListQ", "Literal", "Log", "Log2", "Log10", "LogGamma",
			"LogNormalDistribution", "LogicalExpand", "LogisticSigmoid", "LogIntegral", "LowerCaseQ",
			"LowerTriangularize", "LucasL", "LUDecomposition", "MachineNumberQ", "MangoldtLambda", "ManhattanDistance",
			"Manipulate", "MantissaExponent", "Map", "MapAt", "MapAll", "MapIndexed", "MapThread",
			"MatchingDissimilarity", "MatchQ", "MathMLForm", "MatrixExp", "MatrixForm", "MatrixMinimalPolynomial",
			"MatrixPower", "MatrixQ", "MatrixRank", "Max", "MaxFilter", "Maximize", "Mean", "MeanFilter",
			"MeanDeviation", "Median", "MedianFilter", "MeijerG", "MemberQ", "MeshRange", "MessageName", "Message",
			"Messages", "MersennePrimeExponent", "MersennePrimeExponentQ", "Min", "MinFilter", "MinimalPolynomial",
			"Minimize", "Minus", "Missing", "MissingQ", "Mod", "Module", "MoebiusMu", "MonomialList", "Most",
			"Multinomial", "MultiplicativeOrder", "NakagamiDistribution", "Names", "Nand", "ND", "NDSolve", "Nearest",
			"Negative", "Nest", "NestList", "NestWhile", "NestWhileList", "NextPrime", "NFourierTransform",
			"NIntegrate", "NMaximize", "NMinimize", "NonCommutativeMultiply", "NonNegative", "NonPositive", "NoneTrue",
			"Nor", "Normal", "Normalize", "Norm", "NormalDistribution", "Not", "NotElement", "NotListQ", "NRoots",
			"NSolve", "NullSpace", "NumberFieldRootsOfUnity", "NumberQ", "Numerator", "NumericQ", "NuttallWindow",
			"OddQ", "Off", "On", "Operate", "OptimizeExpression", "Optional", "Options", "Or", "Order", "Ordering",
			"OrderedQ", "Orthogonalize", "OrthogonalMatrixQ", "Out", "Outer", "OutputForm", "OutputStream", "Package",
			"PadLeft", "PadRight", "ParametricPlot", "Part", "Partition", "PartitionsP", "PartitionsQ", "PatternOrder",
			"ParzenWindow", "PearsonChiSquareTest", "PerfectNumber", "PerfectNumberQ", "Pattern", "PatternTest", "PDF",
			"Permutations", "Piecewise", "Plot", "PlotRange", "Plot3D", "Plus", "Pochhammer", "PolarPlot", "Point",
			"PoissonDistribution", "PolyGamma", "Polygon", "PolyLog", "PolynomialExtendedGCD", "PolynomialGCD",
			"PolynomialLCM", "PolynomialQ", "PolynomialQuotient", "PolynomialQuotientRemainder", "PolynomialRemainder",
			"Position", "Positive", "PossibleZeroQ", "Postefix", "Power", "PowerExpand", "PowerMod", "Precision",
			"PreDecrement", "Prefix", "PreIncrement", "Prepend", "PrependTo", "Prime", "PrimeOmega", "PrimePi",
			"PrimePowerQ", "PrimeQ", "PrimitiveRoot", "PrimitiveRootList", "Print", "Probability", "Product",
			"ProductLog", "Projection", "Protect", "PseudoInverse", "Put", "QRDecomposition", "Quantile", "Quantity",
			"QuantityDistribution", "QuantityMagnitude", "QuantityQ", "Quartiles", "Quiet", "Quit", "Quotient",
			"QuotientRemainder", "RandomChoice", "RandomInteger", "RandomPrime", "RandomReal", "RandomSample",
			"RandomVariate", "Range", "Rational", "Rationalize", "Re", "RealNumberQ", "Reap", "Rectangle", "Reduce",
			"Refine", "RegularExpression", "Remove", "Repeated", "RepeatedNull", "Replace", "ReplaceAll", "ReplaceList",
			"ReplacePart", "ReplaceRepeated", "Rescale", "Rest", "Resultant", "Return", "Reverse", "Riffle",
			"RogersTanimotoDissimilarity", "RootIntervals", "Root", "RootOf", "Roots", "RotateLeft", "RotateRight",
			"RotationMatrix", "Round", "Row", "RowReduce", "Rule", "RuleDelayed", "RussellRaoDissimilarity", "Surd",
			"SameQ", "SatisfiabilityCount", "SatisfiabilityInstances", "SatisfiableQ", "Scan", "Sec", "Sech", "Select",
			"Sequence", "Series", "SeriesCoefficient", "SeriesData", "Set", "SetAttributes", "SetDelayed", "Share",
			"Show", "Sign", "SignCmp", "Simplify", "Sin", "Sinc", "SingularValueDecomposition", "Sinh", "SinIntegral",
			"SinhIntegral", "Skewness", "SokalSneathDissimilarity", "Solve", "Sort", "SortBy", "Sow", "Span",
			"SphericalBesselJ", "SphericalBesselY", "SphericalHankelH1", "SphericalHankelH2", "Split", "SplitBy",
			"Sqrt", "SquaredEuclideanDistance", "SquareFreeQ", "SquareMatrixQ", "StandardDeviation", "StandardForm",
			"Standardize", "StieltjesGamma", "StirlingS1", "StirlingS2", "StringDrop", "StringExpression", "StringJoin",
			"StringLength", "StringRiffle", "StringTake", "StringQ", "StringReplace", "StruveH", "StruveL",
			"StudentTDistribution", "Subdivide", "Subfactorial", "Subscript", "Subsuperscript", "SubsetQ", "Subsets",
			"Subtract", "SubtractFrom", "Sum", "Superscript", "SurfaceGraphics", "SurvivalFunction", "Switch",
			"SyntaxLength", "SymbolName", "SymbolQ", "Symmetric", "SymmetricMatrixQ", "SyntaxQ", "SystemDialogInput",
			"Table", "TableForm", "TagSet", "TagSetDelayed", "Take", "Tally", "Tan", "Tanh", "TautologyQ", "Taylor",
			"TensorDimensions", "TensorProduct", "TensorRank", "TensorSymmetry", "TextCell", "TextString", "TeXForm",
			"Thread", "Through", "Throw", "TimeConstrained", "Times", "TimesBy", "TimeValue", "Timing",
			"ToCharacterCode", "ToExpression", "ToeplitzMatrix", "Together", "ToPolarCoordinates", "ToRadicals",
			"ToString", "Total", "ToUnicode", "Tr", "Trace", "TraceForm", "TraditionalForm", "Transpose", "TreeForm",
			"TrigExpand", "TrigReduce", "TrigToExp", "TrueQ", "TukeyWindow", "Tuples", "TwoWayRule", "Undefined",
			"Underoverscript", "UndirectedEdge", "Unequal", "Unevaluated", "UniformDistribution", "Union", "Unique",
			"UnitaryMatrixQ", "UnitConvert", "Unitize", "UnitStep", "UnitVector", "Unprotect", "UnsameQ", "Unset",
			"UpperCaseQ", "UpperTriangularize", "UpSet", "UpSetDelayed", "ValueQ", "Values", "VandermondeMatrix",
			"Variables", "Variance", "VectorAngle", "VectorQ", "VertexEccentricity", "VertexList", "VertexQ",
			"WeibullDistribution", "WeierstrassHalfPeriods", "WeierstrassInvariants", "WeierstrassP",
			"WeierstrassPPrime", "WeightedAdjacencyMatrix", "Which", "While", "With", "WriteString", "Xor",
			"YuleDissimilarity", "ZeroSymmetric", "Zeta" };

	public static Map<String, Integer> RUBI_STATISTICS_MAP;

	public static final Map<String, String> PREDEFINED_SYMBOLS_MAP = Tries.forStrings();

	private final static String[] ALIASES_STRINGS = { "ACos", "ASin", "ATan", "ACosh", "ASinh", "ATanh", "Divergence",
			"Diff", "EvalF", "Int", "Ln", "Trunc", "NthRoot" };
	private final static String[] ALIASES_SUBSTITUTES = { "ArcCos", "ArcSin", "ArcTan", "ArcCosh", "ArcSinh", "ArcTanh",
			"Div", "D", "N", "Integrate", "Log", "IntegerPart", "Surd" };

	/**
	 * Aliases which are mapped to the standard function symbols.
	 */
	public static final Map<String, String> PREDEFINED_ALIASES_MAP = Tries.forStrings();

	public static final String TIMES_STRING = Config.PARSER_USE_LOWERCASE_SYMBOLS ? "times" : "Times";
	public static final String TRUE_STRING = "true";// : "True";

	/**
	 * SuggestTree for all <code>DOLLAR_STRINGS, SYMBOL_STRINGS, FUNCTION_STRINGS</code>
	 */
	private static SuggestTree SUGGEST_TREE = null;

	public static SuggestTree getSuggestTree() {

		synchronized (AST2Expr.class) {
			if (SUGGEST_TREE == null) {
				SUGGEST_TREE = new SuggestTree(100);
				synchronized (SUGGEST_TREE) {
					for (String str : FUNCTION_STRINGS) {
						if (str.length() > 1) {
							SUGGEST_TREE.put(str.toLowerCase(), 2);
						}
					}
					for (String str : SYMBOL_STRINGS) {
						if (str.length() > 1) {
							SUGGEST_TREE.put(str.toLowerCase(), 1);
						}
					}
					for (String str : DOLLAR_STRINGS) {
						if (str.length() > 1) {
							SUGGEST_TREE.put(str.toLowerCase(), 1);
						}
					}
				}
			}
			return SUGGEST_TREE;
		}

	}

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
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
	}

	public static void initialize() {
		Initializer.init();
	}

	private long fPrecision;

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
		fEngine = engine;
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
	 * Converts a parsed ASTNode expression into a Symja IExpr expression
	 * 
	 * @param node
	 *            the parsed ASTNode
	 * @return the Symja expression
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
				IASTAppendable appendableAST = F.ast(convertNode(functionNode.get(0)), functionNode.size(), false);
				for (int i = 1; i < functionNode.size(); i++) {
					appendableAST.append(convertNode(functionNode.get(i)));
				}
				ast = appendableAST;
			}

			int functionID = ast.headID();
			if (functionID > ID.UNKNOWN) {
				IExpr expr;
				switch (functionID) {
				case ID.Association:
					if (ast.isAST1() && ast.arg1().isList()) {
						IExpr arg1 = ast.arg1();
						if (arg1.isListOfRules(true)) {
							return F.assoc((IAST) arg1);
						} else if (arg1.isAST(F.List, 2)) {
							arg1 = arg1.first();
							if (arg1.isListOfRules(true)) {
								return F.assoc((IAST) arg1);
							}
						}
					}
					break;
				case ID.N:
					if (ast.isAST2() && ast.arg2().isInteger()) {
						try {
							int precision = ast.arg2().toIntDefault();
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
				case ID.Pattern:
					expr = PatternMatching.Pattern.CONST.evaluate(ast, fEngine);
					if (expr.isPresent()) {
						return expr;
					}
					break;
				case ID.Optional:
					expr = PatternMatching.Optional.CONST.evaluate(ast, fEngine);
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
				return F.$b(convertNode(pn.getConstraint()), pn.isDefault());
			}
			ASTNode defaultValue = pn.getDefaultValue();
			if (defaultValue != null) {
				return F.Optional(F.$p((ISymbol) convertNode(pn.getSymbol()), convertNode(pn.getConstraint())),
						convertNode(defaultValue));
			}
			return F.$p((ISymbol) convertNode(pn.getSymbol()), convertNode(pn.getConstraint()), pn.isDefault());
		}

		if (node instanceof IntegerNode) {
			final IntegerNode integerNode = (IntegerNode) node;
			final String iStr = integerNode.getString();
			if (iStr != null) {
				return F.ZZ(iStr, integerNode.getNumberFormat());
			}
			return F.ZZ(integerNode.getIntValue());
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

}
