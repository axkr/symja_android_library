package org.matheclipse.core.convert;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.concurrent.NotThreadSafe;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;
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
import org.matheclipse.parser.trie.SuggestTree;
import org.matheclipse.parser.trie.TrieMatch;

/**
 * Converts a parsed <code>org.matheclipse.parser.client.ast.ASTNode</code> expression into an IExpr
 * expression
 */
@NotThreadSafe
public class AST2Expr {

  public static final String[] UPPERCASE_SYMBOL_STRINGS = {"C", "D", "E", "I", "N", "O"};

  public static final String[] PHYSICAL_CONSTANTS_STRINGS =
      {"AvogadroConstant", "BohrRadius", "UniverseAge"};

  public static final String[] DOLLAR_STRINGS = {"$Aborted", "$Assumptions", "$BaseDirectory",
      "$Cancel", "$CharacterEncoding", "$Context", "$CreationDate", "$ContextPath",
      "$DisplayFunction", "$Failed", "$HistoryLength", "$HomeDirectory", //
      "$IdentityMatrix", // for MatrixD
      "$Input", "$InputFileName", "$IterationLimit", "$Line", "$MachineEpsilon",
      "$MachinePrecision", "$MaxMachineNumber", "$MessageList", "$MinMachineNumber", "$Notebooks",
      "$OperatingSystem", "$OutputSizeLimit", "$Packages", "$Path", "$PathnameSeparator",
      "$PrePrint", "$PreRead", "$RecursionLimit", "$RootDirectory", "$Scaling",
      "$SingleEntryMatrix", "$ScriptCommandLine", "$SystemCharacterEncoding", "$SystemMemory",
      "$TemporaryDirectory", "$UserBaseDirectory", "$UserName", "$Version"};

  public static final String[] SYMBOL_STRINGS = {"AccuracyGoal", "All", "AllowedHeads",
      "AllowShortContext", "Algebraics", "AspectRatio", "Automatic", "Axis", "Axes", "AxesLabel",
      "AxesOrigin", "AxesStyle", "Background", "BarOrigin", "BetweennessCentrality", "Black",
      "Blue", "Booleans", "Bottom", "Boxed", "BoxRatios", "Brown", "Byte", "Center", "Character",
      "CharacterEncoding", "ColorFunction", "ComplexInfinity", "Catalan", "Complexes",
      "ComplexityFunction", "Constant", "Cyan", "Dashed", "DataRange", "DefaultValue", "Degree",
      "DegreeLexicographic", "DegreeReverseLexicographic", "Delimiters", "DigitCharacter",
      "DirectedEdges", "DisplayFunction", "Disputed", "DistanceFunction", "DotDashed", "Dotted",
      "EdgeLabels", "EdgeShapeFunction", "EdgeStyle", "EliminationOrder", "EndOfFile", "EndOfLine",
      "EndOfString", "EulerGamma", "Expression", "Extension", "False", "Filling", "FillingStyle",
      "Flat", "Float", "Full", "GaussianIntegers", "General", "GenerateConditions", "Glaisher",
      "GoldenAngle", "GoldenRatio", "Gray", "Green", "Heads", "HexidecimalCharacter", "HoldAll",
      "HoldComplete", "HoldAllComplete", "HoldFirst", "HoldRest", "IgnoreCase", "Indeterminate",
      "Inherited", "Infinity", "InsertionFunction", "Integer", "Integers", "InterpolationOrder",
      "Joined", "KeyAbsent", "Khinchin", "LabelingFunction", "LabelingSize", "Large", "Left",
      "LetterCharacter", "Lexicographic", "Lighting", "LightBlue", "LightBrown", "LightCyan",
      "LightGray", "LightGreen", "LightMagenta", "LightOrange", "LightPink", "LightPurple",
      "LightRed", "LightYellow", "Listable", "Locked", "LongForm", "Magenta", "Matrices",
      "MaxIterations", "MaxPoints", "MaxRoots", "Mesh", "Medium", "Method", "Modulus",
      "MonomialOrder", "NegativeDegreeLexicographic", "NegativeDegreeReverseLexicographic",
      "NegativeLexicographic", "NegativeIntegers", "NegativeRationals", "NegativeReals", "NHoldAll",
      "NHoldFirst", "NHoldRest", "None", "NonNegativeIntegers", "NonNegativeRationals",
      "NonNegativeReals", "Nothing", "Nonexistent", "NotApplicable", "NotAvailable", "Now", "Null",
      "Number", "NumberString", "NumericFunction", "OneIdentity", "Orange", "Orderless", "Overlaps",
      "Pi", "Pink", "PlotLabel", "PlotLegends", "PlotRange", "PlotStyle", "PositiveIntegers",
      "PositiveRationals", "PositiveReals", "PrecisionGoal", "Primes", "Protected", "Purple",
      "Rationals", "ReadProtected", "Real", "Record", "RecordSeparators", "Red", "Reals", "Right",
      "SameTest", "ScalingFunctions", "Second", "SequenceHold", "SetSystemOptions", "Small",
      "SystemOptions", "SlotAbsent", "StaticsVisible", "StartOfLine", "StartOfString", "Strict",
      "String", "Symbol", "TableAlignments", "TableDepth", "TableDirections", "TableHeadings",
      "TableSpacing", "TargetFunctions", "TestID", "Ticks", "Tiny", "TicksStyle", "Today",
      "TooLarge", "Top", "Trig", "True", "Unknown", "UseTypeChecking", "Variable", "Vectors",
      "VertexLabels", "VertexShapeFunction", "VertexSize", "VertexStyle", "ViewPoint", "White",
      "Whitespace", "WhitespaceCharacter", "WignerD", "Word", "WordCharacter", "WordSeparators",
      "WorkingPrecision", "Yellow", "ZeroTest"};

  public static final String[] FUNCTION_STRINGS = {"AASTriangle", "Abort", "Abs", "AbsArg",
      "AbsoluteCorrelation", "AbsoluteTime", "AbsoluteTiming", "Accumulate", "AddSides", "AddTo",
      "AddToClassPath", "AdjacencyMatrix", "Adjugate", "AiryAi", "AiryAiPrime", "AiryBi",
      "AiryBiPrime", "AlgebraicNumber", "AllTrue", "Alphabet", "Alternatives", "AmbientLight",
      "And", "AngerJ", "AnglePath", "AngleVector", "Annotation", "Annuity", "AnnuityDue",
      "AntihermitianMatrixQ", "AntiSymmetric", "AntisymmetricMatrixQ", "AnyTrue", "Apart",
      "AppellF1", "Append", "AppendTo", "Apply", "ApplySides", "ArcCos", "ArcCosh", "ArcCot",
      "ArcCoth", "ArcCsc", "ArcCsch", "ArcLength", "ArcSec", "ArcSech", "ArcSin", "ArcSinh",
      "ArcTan", "ArcTanh", "Area", "Arg", "ArgMax", "ArgMin", "ArithmeticGeometricMean", "Array",
      "ArrayDepth", "ArrayFlatten", "ArrayPad", "ArrayPlot", "ArrayQ", "ArrayReduce",
      "ArrayReshape", "ArrayRules", "Arrays", "Arrow", "Arrowheads", "ASATriangle", "AssociateTo",
      "Association", "AssociationMap", "AssociationQ", "AssociationThread", "Assuming",
      "Assumptions", "AtomQ", "Attributes", "Ball", "BarChart", "BartlettWindow", "BaseDecode",
      "BaseEncode", "BaseForm", "Beep", "Begin", "BeginPackage", "BeginTestSection", "BellB",
      "BellY", "BernoulliB", "BernoulliDistribution", "BernoulliProcess", "BernsteinBasis",
      "BesselI", "BesselJ", "BesselJZero", "BesselK", "BesselY", "BesselYZero", "Beta",
      "BetaDistribution", "BetaRegularized", "Between", "BezierFunction", "BinaryDeserialize",
      "BinaryDistance", "BinaryRead", "BinarySerialize", "BinaryWrite", "BinCounts", "Binomial",
      "BinomialDistribution", "BinomialProcess", "BioSequence", "BioSequenceQ",
      "BioSequenceTranscribe", "BioSequenceTranslate", "BipartiteGraphQ", "BitAnd", "BitClear",
      "BitFlip", "BitGet", "BitLength", "BitNot", "BitOr", "BitSet", "BitXor",
      "BlackmanHarrisWindow", "BlackmanNuttallWindow", "BlackmanWindow", "Blank",
      "BlankNullSequence", "BlankSequence", "Block", "Boole", "BooleanConvert", "BooleanFunction",
      "BooleanMaxterms", "BooleanMinimize", "BooleanMinterms", "BooleanQ", "BooleanTable",
      "BooleanVariables", "BoxWhiskerChart", "BrayCurtisDistance", "Break", "BrownianBridgeProcess",
      "BSplineFunction", "Button", "ByteArray", "ByteArrayQ", "ByteArrayToString", "ByteCount",
      "CanberraDistance", "Cancel", "CancelButton", "CarlsonRC", "CarlsonRD", "CarlsonRF",
      "CarlsonRG", "CarlsonRJ", "CarmichaelLambda", "CartesianProduct", "Cases", "CatalanNumber",
      "Catch", "Catenate", "CauchyDistribution", "CDF", "Ceiling", "CenterDot", "CentralMoment",
      "CForm", "CharacteristicPolynomial", "CharacterRange", "Characters", "ChebyshevT",
      "ChebyshevU", "Check", "CheckAbort", "ChessboardDistance", "ChineseRemainder",
      "ChiSquareDistribution", "CholeskyDecomposition", "Chop", "Circle", "CircleDot",
      "CirclePoints", "CircleTimes", "Clear", "ClearAll", "ClearAttributes", "ClebschGordan",
      "Clip", "Close", "ClosenessCentrality", "CMYColor", "Coefficient", "CoefficientArrays",
      "CoefficientList", "CoefficientRules", "Cofactor", "Collect", "CollinearPoints", "Colon",
      "ColorData", "Column", "Commonest", "CompatibleUnitQ", "Compile", "CompiledFunction",
      "CompilePrint", "Complement", "CompleteGraph", "Complex", "ComplexExpand", "ComplexPlot3D",
      "ComposeList", "ComposeSeries", "CompositeQ", "Composition", "CompoundExpression",
      "Condition", "ConditionalExpression", "Cone", "Conjugate", "ConjugateTranspose",
      "ConnectedGraphQ", "ConstantArray", "ContainsAll", "ContainsAny", "ContainsExactly",
      "ContainsNone", "ContainsOnly", "Context", "Contexts", "Continue", "ContinuedFraction",
      "ContourPlot", "Convergents", "ConvexHullMesh", "CoordinateBoundingBox", "CoordinateBounds",
      "CoplanarPoints", "CoprimeQ", "Correlation", "CorrelationDistance", "Cos", "Cosh",
      "CoshIntegral", "CosineDistance", "CosIntegral", "Cot", "Coth", "Count", "CountDistinct",
      "Counts", "Covariance", "CreateDirectory", "CreateFile", "Cross", "Csc", "Csch", "Cube",
      "CubeRoot", "Cuboid", "Cumulant", "Curl", "CycleGraph", "Cycles", "Cyclotomic", "Cylinder",
      "Dashing", "Dataset", "DateObject", "DateString", "DateValue", "Decrement", "DedekindNumber",
      "Default", "DefaultButton", "Defer", "Definition", "Delete", "DeleteCases",
      "DeleteDuplicates", "DeleteDuplicatesBy", "DeleteMissing", "Denominator", "DensityHistogram",
      "DensityPlot", "Depth", "Derivative", "DesignMatrix", "Det", "Diagonal", "DiagonalMatrix",
      "DiagonalMatrixQ", "DialogInput", "DialogNotebook", "DialogReturn", "DiceDissimilarity",
      "DifferenceDelta", "Differences", "DigitCount", "DigitQ", "Dimensions", "DiracDelta",
      "DirectedEdge", "DirectedInfinity", "Direction", "DirectionalLight", "Directive",
      "DirichletBeta", "DirichletEta", "DirichletLambda", "DirichletWindow", "DiscreteDelta",
      "DiscretePlot", "DiscreteUniformDistribution", "Discriminant", "DisjointQ", "Disk",
      "Dispatch", "DisplayForm", "Distribute", "Distributed", "Div", "Divide", "DivideBy",
      "DivideSides", "Divisible", "Divisors", "DivisorSigma", "DivisorSum", "Do", "Dodecahedron",
      "Dot", "DownValues", "Drop", "DSolve", "Dt", "DuplicateFreeQ", "Dynamic", "EasterSunday",
      "Echo", "EchoFunction", "EdgeCount", "EdgeForm", "EdgeList", "EdgeQ", "EdgeRules",
      "EdgeWeight", "EditDistance", "EffectiveInterest", "Eigensystem", "Eigenvalues",
      "EigenvectorCentrality", "Eigenvectors", "Element", "ElementData", "Eliminate", "Ellipsoid",
      "EllipticE", "EllipticF", "EllipticK", "EllipticPi", "EllipticTheta", "EmpiricalDistribution",
      "End", "EndPackage", "EndTestSection", "Entity", "Entropy", "Equal", "EqualTo", "Equivalent",
      "Erf", "Erfc", "Erfi", "ErlangDistribution", "EuclideanDistance", "EulerE", "EulerianGraphQ",
      "EulerPhi", "Evaluate", "EvenQ", "ExactNumberQ", "Except", "Exists", "Exit", "Exp", "Expand",
      "ExpandAll", "ExpandDenominator", "ExpandNumerator", "Expectation", "ExpIntegralE",
      "ExpIntegralEi", "Exponent", "ExponentialDistribution", "Export", "ExportString", "ExpToTrig",
      "ExtendedGCD", "Extract", "Factor", "Factorial", "Factorial2", "FactorialMoment",
      "FactorialPower", "FactorInteger", "FactorSquareFree", "FactorSquareFreeList", "FactorTerms",
      "FactorTermsList", "Fibonacci", "File", "FileFormat", "FileHash", "FileNameDrop",
      "FileNameJoin", "FileNames", "FileNameTake", "FilePrint", "FilterRules", "FindClusters",
      "FindCycle", "FindEdgeCover", "FindEulerianCycle", "FindFit", "FindFormula",
      "FindGeneratingFunction", "FindGraphCommunities", "FindGraphIsomorphism",
      "FindHamiltonianCycle", "FindIndependentEdgeSet", "FindIndependentVertexSet", "FindInstance",
      "FindLinearRecurrence", "FindList", "FindMaximum", "FindMinimum", "FindMinimumCostFlow",
      "FindPermutation", "FindRoot", "FindSequenceFunction", "FindShortestPath", "FindShortestTour",
      "FindSpanningTree", "FindVertexCover", "First", "FirstCase", "FirstPosition", "Fit",
      "FittedModel", "FiveNum", "FixedPoint", "FixedPointList", "Flatten", "FlattenAt",
      "FlatTopWindow", "Floor", "Fold", "FoldList", "For", "ForAll", "FormBox", "Fourier",
      "FourierCosTransform", "FourierDCT", "FourierDCTMatrix", "FourierDST", "FourierDSTMatrix",
      "FourierMatrix", "FourierSinTransform", "FractionalPart", "FractionBox", "FRatioDistribution",
      "FrechetDistribution", "FreeQ", "FresnelC", "FresnelS", "FrobeniusNumber", "FrobeniusSolve",
      "FromCharacterCode", "FromContinuedFraction", "FromDigits", "FromLetterNumber",
      "FromPolarCoordinates", "FromRomanNumeral", "FromSphericalCoordinates", "FullDefinition",
      "FullForm", "FullSimplify", "Function", "FunctionDomain", "FunctionExpand", "FunctionPeriod",
      "FunctionRange", "FunctionURL", "Gamma", "GammaDistribution", "GammaRegularized", "Gather",
      "GatherBy", "GaussianMatrix", "GaussianWindow", "GCD", "GegenbauerC", "GeodesyData",
      "GeoDistance", "GeometricDistribution", "GeometricMean", "GeometricTransformation",
      "GeoPosition", "Get", "GoldbachList", "GompertzMakehamDistribution", "Grad", "Graph",
      "GraphCenter", "GraphComplement", "GraphData", "GraphDiameter", "GraphDifference",
      "GraphDisjointUnion", "Graphics", "Graphics3D", "Graphics3DJSON", "GraphicsComplex",
      "GraphicsGroup", "GraphicsJSON", "GraphIntersection", "GraphPeriphery", "GraphPower",
      "GraphQ", "GraphRadius", "GraphUnion", "GrayLevel", "Greater", "GreaterEqual",
      "GreaterEqualThan", "GreaterThan", "GridGraph", "GroebnerBasis", "GroupBy", "Gudermannian",
      "GumbelDistribution", "HalfNormalDistribution", "HamiltonianGraphQ", "HammingDistance",
      "HammingWindow", "HankelH1", "HankelH2", "HankelMatrix", "HannWindow", "HarmonicMean",
      "HarmonicNumber", "Haversine", "Head", "HeavisideLambda", "HeavisidePi", "HeavisideTheta",
      "HermiteH", "HermitianMatrixQ", "HessenbergDecomposition", "HessianMatrix", "HilbertMatrix",
      "Histogram", "HodgeDual", "Hold", "HoldForm", "HoldPattern", "Horner", "HornerForm", "Hue",
      "HurwitzLerchPhi", "HurwitzZeta", "HypercubeGraph", "Hyperfactorial", "Hypergeometric0F1",
      "Hypergeometric0F1Regularized", "Hypergeometric1F1", "Hypergeometric1F1Regularized",
      "Hypergeometric2F1", "Hypergeometric2F1Regularized", "HypergeometricDistribution",
      "HypergeometricPFQ", "HypergeometricPFQRegularized", "HypergeometricU", "Icosahedron",
      "Identity", "IdentityMatrix", "If", "Im", "Image", "ImageChannels", "ImageColorSpace",
      "ImageCrop", "ImageData", "ImageDimensions", "ImageQ", "ImageResize", "ImageRotate",
      "ImageScaled", "ImageSize", "ImageType", "ImplicitD", "Implies", "Import", "ImportString",
      "In", "Increment", "Indexed", "IndexGraph", "Inequality", "InexactNumberQ", "Infix",
      "Information", "Inner", "Input", "InputField", "InputForm", "InputStream", "InputString",
      "Insert", "InstallJava", "InstanceOf", "IntegerDigits", "IntegerExponent", "IntegerLength",
      "IntegerName", "IntegerPart", "IntegerPartitions", "IntegerQ", "Integrate",
      "InterpolatingFunction", "InterpolatingPolynomial", "Interpolation", "InterquartileRange",
      "Interrupt", "IntersectingQ", "Intersection", "Interval", "IntervalComplement",
      "IntervalData", "IntervalIntersection", "IntervalMemberQ", "IntervalUnion", "Inverse",
      "InverseBetaRegularized", "InverseCDF", "InverseErf", "InverseErfc", "InverseFourier",
      "InverseFunction", "InverseGammaDistribution", "InverseGammaRegularized",
      "InverseGudermannian", "InverseHaversine", "InverseJacobiCD", "InverseJacobiCN",
      "InverseJacobiDC", "InverseJacobiDN", "InverseJacobiNC", "InverseJacobiND", "InverseJacobiSC",
      "InverseJacobiSD", "InverseJacobiSN", "InverseLaplaceTransform", "InverseSeries",
      "InverseWeierstrassP", "InverseZTransform", "IsomorphicGraphQ", "JaccardDissimilarity",
      "JacobiAmplitude", "JacobiCD", "JacobiCN", "JacobiDC", "JacobiDN", "JacobiEpsilon",
      "JacobiMatrix", "JacobiNC", "JacobiND", "JacobiP", "JacobiSC", "JacobiSD", "JacobiSN",
      "JacobiSymbol", "JacobiZeta", "JavaClass", "JavaForm", "JavaNew", "JavaObject", "JavaObjectQ",
      "JavaShow", "Join", "JSForm", "JSFormData", "KelvinBei", "KelvinBer", "Key", "KeyDrop",
      "KeyExistsQ", "Keys", "KeySelect", "KeySort", "KeyTake", "KeyValuePattern", "KleinInvariantJ",
      "KnownUnitQ", "KolmogorovSmirnovTest", "KOrderlessPartitions", "KPartitions",
      "KroneckerDelta", "KroneckerProduct", "KroneckerSymbol", "Kurtosis", "Labeled", "LaguerreL",
      "LambertW", "LaplaceTransform", "Laplacian", "Last", "LCM", "LeafCount", "LeastSquares",
      "LegendreP", "LegendreQ", "Length", "LengthWhile", "LerchPhi", "Less", "LessEqual",
      "LessEqualThan", "LessThan", "LetterCounts", "LetterNumber", "LetterQ", "Level", "LevelQ",
      "LeviCivitaTensor", "Limit", "Line", "LinearModelFit", "LinearOptimization",
      "LinearProgramming", "LinearRecurrence", "LinearSolve", "LinearSolveFunction", "LineGraph",
      "LiouvilleLambda", "List", "ListContourPlot", "ListConvolve", "ListCorrelate",
      "ListDensityPlot", "ListLinePlot", "ListLinePlot3D", "ListLogLinearPlot", "ListLogLogPlot",
      "ListLogPlot", "ListPlot", "ListPlot3D", "ListPointPlot3D", "ListPolarPlot", "ListQ",
      "ListStepPlot", "ListStreamPlot", "ListVectorPlot", "Literal", "LLMFunction", "LoadJavaClass",
      "Log", "Log10", "Log2", "LogGamma", "LogicalExpand", "LogIntegral", "LogisticSigmoid",
      "LogLinearPlot", "LogLogPlot", "LogNormalDistribution", "LogPlot", "Longest", "Lookup",
      "LowerCaseQ", "LowerTriangularize", "LowerTriangularMatrixQ", "LucasL", "LUDecomposition",
      "MachineNumberQ", "MakeBoxes", "MangoldtLambda", "ManhattanDistance", "Manipulate",
      "MantissaExponent", "Map", "MapAll", "MapApply", "MapAt", "MapIndexed", "MapThread",
      "MatchingDissimilarity", "MatchQ", "MathMLForm", "MatrixD", "MatrixExp", "MatrixForm",
      "MatrixFunction", "MatrixLog", "MatrixMinimalPolynomial", "MatrixPlot", "MatrixPower",
      "MatrixQ", "MatrixRank", "Max", "MaxFilter", "Maximize", "MaxMemoryUsed", "Mean",
      "MeanDeviation", "MeanFilter", "Median", "MedianFilter", "MeijerG", "MemberQ",
      "MemoryAvailable", "MemoryInUse", "Merge", "MersennePrimeExponent", "MersennePrimeExponentQ",
      "MeshRange", "Message", "MessageName", "Messages", "Min", "MinFilter", "MinimalPolynomial",
      "Minimize", "MinMax", "Minor", "Minors", "Minus", "Missing", "MissingQ", "Mod",
      "ModularInverse", "Module", "MoebiusMu", "Moment", "MonomialList", "Most", "Multinomial",
      "MultiplicativeOrder", "MultiplySides", "NakagamiDistribution", "NameQ", "Names", "Nand",
      "NArgMax", "NArgMin", "ND", "NDSolve", "Nearest", "NearestTo", "Needs", "Negative", "Nest",
      "NestList", "NestWhile", "NestWhileList", "NewLimit", "NExpectation", "NextPrime",
      "NFourierTransform", "NIntegrate", "NMaximize", "NMaxValue", "NMinimize", "NMinValue",
      "NonCommutativeMultiply", "NoneTrue", "NonNegative", "NonPositive", "Nor", "Norm", "Normal",
      "NormalDistribution", "Normalize", "NormalMatrixQ", "Not", "NotElement", "NotListQ",
      "NProbability", "NProduct", "NRoots", "NSolve", "NSum", "NullSpace", "NumberDigit",
      "NumberFieldRootsOfUnity", "NumberLinePlot", "NumberQ", "Numerator", "NumericalOrder",
      "NumericalSort", "NumericArray", "NumericArrayQ", "NumericArrayType", "NumericQ",
      "NuttallWindow", "Octahedron", "OddQ", "Off", "On", "Opacity", "OpenAppend", "OpenRead",
      "OpenWrite", "Operate", "OptimizeExpression", "Optional", "Options", "OptionsPattern",
      "OptionValue", "Or", "Order", "OrderedQ", "Ordering", "Orthogonalize", "OrthogonalMatrixQ",
      "Out", "Outer", "OutputForm", "OutputStream", "Overflow", "Overscript", "OverscriptBox",
      "OwnValues", "Package", "PadeApproximant", "PadLeft", "PadRight", "ParallelMap",
      "ParametricPlot", "Parenthesis", "ParetoDistribution", "Part", "Partition", "PartitionsP",
      "PartitionsQ", "ParzenWindow", "PathGraph", "PathGraphQ", "Pattern", "PatternOrder",
      "PatternTest", "PauliMatrix", "Pause", "PDF", "PearsonChiSquareTest",
      "PearsonCorrelationTest", "PerfectNumber", "PerfectNumberQ", "Perimeter", "PermutationCycles",
      "PermutationCyclesQ", "PermutationList", "PermutationListQ", "PermutationReplace",
      "Permutations", "Permute", "PetersenGraph", "Pick", "Piecewise", "PiecewiseExpand",
      "PieChart", "PlanarGraph", "PlanarGraphQ", "Plot", "Plot3D", "Plus", "PlusMinus",
      "Pochhammer", "Point", "PointLight", "PointSize", "PoissonDistribution", "PoissonProcess",
      "PolarPlot", "PolyGamma", "Polygon", "PolygonalNumber", "Polyhedron", "PolyLog",
      "PolynomialExtendedGCD", "PolynomialGCD", "PolynomialLCM", "PolynomialQ",
      "PolynomialQuotient", "PolynomialQuotientRemainder", "PolynomialRemainder", "Position",
      "Positive", "PossibleZeroQ", "Postfix", "Power", "PowerExpand", "PowerMod",
      "PowersRepresentations", "PrecedenceForm", "Precision", "PreDecrement", "Prefix",
      "PreIncrement", "Prepend", "PrependTo", "Prime", "PrimeOmega", "PrimePi", "PrimePowerQ",
      "PrimeQ", "PrimitiveRoot", "PrimitiveRootList", "PrincipalComponents", "Print",
      "PrintableASCIIQ", "Prism", "Probability", "Product", "ProductLog", "Projection",
      "Proportion", "Proportional", "Protect", "PseudoInverse", "Put", "PutAppend", "Pyramid",
      "QRDecomposition", "QuadraticIrrationalQ", "Quantile", "Quantity", "QuantityDistribution",
      "QuantityMagnitude", "QuantityQ", "QuantityUnit", "QuarticSolve", "Quartiles", "Quiet",
      "Quit", "Quotient", "QuotientRemainder", "RadicalBox", "Ramp", "RamseyNumber", "Random",
      "RandomChoice", "RandomComplex", "RandomGraph", "RandomInteger", "RandomPermutation",
      "RandomPrime", "RandomReal", "RandomSample", "RandomVariate", "Range", "RankedMax",
      "RankedMin", "Rational", "Rationalize", "RawBackquote", "RawBoxes", "Re", "Read", "ReadLine",
      "ReadList", "ReadString", "RealAbs", "RealDigits", "RealSign", "RealValuedNumberQ",
      "RealValuedNumericQ", "Reap", "Rectangle", "Reduce", "Refine", "RegularExpression", "ReIm",
      "ReleaseHold", "Remove", "RemoveDiacritics", "Repeated", "RepeatedNull", "RepeatedTiming",
      "Replace", "ReplaceAll", "ReplaceAt", "ReplaceList", "ReplacePart", "ReplaceRepeated",
      "Rescale", "Residue", "Rest", "Resultant", "Return", "Reverse", "ReverseElement",
      "ReverseEquilibrium", "ReverseSort", "ReverseUpEquilibrium", "RGBColor", "RiccatiSolve",
      "Riffle", "RightArrow", "RightArrowBar", "RightArrowLeftArrow", "RightComposition",
      "RightDownTeeVector", "RightDownVector", "RightDownVectorBar", "RightTee", "RightTeeArrow",
      "RightTeeVector", "RightTriangle", "RightTriangleBar", "RightTriangleEqual",
      "RightUpDownVector", "RightUpTeeVector", "RightUpVector", "RightUpVectorBar", "RightVector",
      "RightVectorBar", "RogersTanimotoDissimilarity", "RomanNumeral", "Root", "RootIntervals",
      "RootMeanSquare", "RootOf", "RootReduce", "Roots", "RotateLeft", "RotateRight",
      "RotationMatrix", "RotationTransform", "Round", "RoundImplies", "Row", "RowBox", "RowReduce",
      "RSolve", "RSolveValue", "Rule", "RuleDelayed", "RussellRaoDissimilarity", "SameObjectQ",
      "SameQ", "SASTriangle", "SatisfiabilityCount", "SatisfiabilityInstances", "SatisfiableQ",
      "Save", "SawtoothWave", "Scale", "Scaled", "ScalingTransform", "Scan", "SchurDecomposition",
      "ScientificForm", "Sec", "Sech", "SeedRandom", "Select", "SelectFirst", "SemanticImport",
      "SemanticImportString", "Sequence", "SequenceCases", "SequenceReplace", "SequenceSplit",
      "Series", "SeriesCoefficient", "SeriesData", "Set", "SetAttributes", "SetDelayed", "Share",
      "ShearingTransform", "Short", "ShortDownArrow", "Shortest", "ShortLeftArrow",
      "ShortRightArrow", "ShortUpArrow", "Show", "Sign", "Signature", "SignCmp", "Simplex",
      "Simplify", "Sin", "Sinc", "SingularValueDecomposition", "SingularValueList", "Sinh",
      "SinhIntegral", "SinIntegral", "SixJSymbol", "Skeleton", "Skewness", "Slot", "SlotNumber",
      "SlotSequence", "SlotSequenceNumber", "SmallCircle", "SokalSneathDissimilarity", "Solve",
      "Sort", "SortBy", "Sow", "Span", "SparseArray", "SparseArrayQ", "SpecialsFreeQ",
      "Specularity", "Sphere", "SphericalBesselJ", "SphericalBesselY", "SphericalHankelH1",
      "SphericalHankelH2", "SphericalHarmonicY", "Splice", "Split", "SplitBy", "SpotLight", "Sqrt",
      "SqrtBox", "Square", "SquaredEuclideanDistance", "SquareFreeQ", "SquareIntersection",
      "SquareMatrixQ", "SquaresR", "SquareSubset", "SquareSubsetEqual", "SquareSuperset",
      "SquareSupersetEqual", "SquareUnion", "SSSTriangle", "Stack", "StackBegin",
      "StandardDeviation", "StandardForm", "Standardize", "Star", "StarGraph", "StieltjesGamma",
      "StirlingS1", "StirlingS2", "StreamPlot", "StringCases", "StringContainsQ", "StringCount",
      "StringDrop", "StringExpression", "StringForm", "StringFormat", "StringFreeQ", "StringInsert",
      "StringJoin", "StringLength", "StringMatchQ", "StringPart", "StringPosition", "StringQ",
      "StringRepeat", "StringReplace", "StringReverse", "StringRiffle", "StringSplit", "StringTake",
      "StringTemplate", "StringToByteArray", "StringToStream", "StringTrim", "Structure", "StruveH",
      "StruveL", "StudentTDistribution", "Style", "StyleForm", "Subdivide", "Subfactorial",
      "Subscript", "SubscriptBox", "Subset", "SubsetCases", "SubsetCount", "SubsetEqual",
      "SubsetPosition", "SubsetQ", "SubsetReplace", "Subsets", "Subsuperscript",
      "SubsuperscriptBox", "Subtract", "SubtractFrom", "SubtractSides", "Succeeds", "SucceedsEqual",
      "SucceedsSlantEqual", "SucceedsTilde", "SuchThat", "SudokuSolve", "Sum", "Summary",
      "SuperDagger", "Superscript", "SuperscriptBox", "Superset", "SupersetEqual", "Surd",
      "SurfaceArea", "SurfaceGraphics", "SurvivalFunction", "Switch", "SymbolName", "SymbolQ",
      "Symmetric", "SymmetricMatrixQ", "SyntaxLength", "SyntaxQ", "SystemDialogInput", "Table",
      "TableForm", "TagSet", "TagSetDelayed", "TagUnset", "Take", "TakeLargest", "TakeLargestBy",
      "TakeSmallest", "TakeSmallestBy", "TakeWhile", "Tally", "Tan", "Tanh", "TautologyQ", "Taylor",
      "TemplateApply", "TemplateExpression", "TemplateIf", "TemplateSlot", "TensorDimensions",
      "TensorProduct", "TensorRank", "TensorSymmetry", "TensorWedge", "TestReport",
      "TestReportObject", "TestResultObject", "Tetrahedron", "TeXForm", "Text", "TextCell",
      "TextElement", "TextString", "TextStructure", "Therefore", "Thickness", "Thread",
      "ThreeJSymbol", "Through", "Throw", "Tilde", "TildeEqual", "TildeFullEqual", "TildeTilde",
      "TimeConstrained", "TimeObject", "TimeRemaining", "Times", "TimesBy", "TimeValue", "Timing",
      "ToBoxes", "ToCharacterCode", "ToeplitzMatrix", "ToExpression", "Together", "ToIntervalData",
      "ToLowerCase", "ToPolarCoordinates", "ToRadicals", "TortoiseShellBracket",
      "ToSphericalCoordinates", "ToString", "Total", "ToUnicode", "ToUpperCase", "Tr", "Trace",
      "TraceForm", "TraditionalForm", "TransformationFunction", "TranslationTransform",
      "Transliterate", "Transpose", "TreeForm", "Triangle", "TrigExpand", "TrigFactor",
      "TrigReduce", "TrigSimplifyFu", "TrigToExp", "TrueQ", "TTest", "Tube", "TukeyWindow",
      "Tuples", "TwoWayRule", "UnaryMinusPlus", "UnaryPlus", "UnaryPlusMinus", "Undefined",
      "Underflow", "Underoverscript", "UnderoverscriptBox", "Underscript", "UnderscriptBox",
      "UndirectedEdge", "Unequal", "UnequalTo", "Unevaluated", "UniformDistribution", "Union",
      "UnionPlus", "Unique", "UnitaryMatrixQ", "UnitConvert", "Unitize", "UnitStep", "UnitTriangle",
      "UnitVector", "Unprotect", "UnsameQ", "Unset", "UpArrow", "UpArrowBar", "UpArrowDownArrow",
      "UpDownArrow", "UpEquilibrium", "UpperCaseQ", "UpperLeftArrow", "UpperRightArrow",
      "UpperTriangularize", "UpperTriangularMatrixQ", "UpSet", "UpSetDelayed", "UpTee",
      "UpTeeArrow", "UpTo", "UpValues", "URLFetch", "ValueQ", "Values", "VandermondeMatrix",
      "Variables", "Variance", "VectorAngle", "VectorGreater", "VectorGreaterEqual", "VectorLess",
      "VectorLessEqual", "VectorPlot", "VectorQ", "Vee", "Verbatim", "VerificationTest",
      "VertexCount", "VertexEccentricity", "VertexList", "VertexQ", "VerticalBar",
      "VerticalSeparator", "VerticalTilde", "Volume", "WeaklyConnectedGraphQ", "WeberE", "Wedge",
      "WeibullDistribution", "WeierstrassHalfPeriods", "WeierstrassInvariants", "WeierstrassP",
      "WeierstrassPPrime", "WeightedAdjacencyMatrix", "WeightedData", "WeightedGraphQ",
      "WheelGraph", "Which", "While", "WhiteCornerBracket", "WhittakerM", "WhittakerW", "With",
      "WordBoundary", "Write", "WriteString", "Xnor", "Xor", "YuleDissimilarity", "ZernikeR",
      "ZeroSymmetric", "Zeta", "ZTransform"};

  public static Map<String, Integer> RUBI_STATISTICS_MAP;

  /** Map the lower case identifier name to the upper case Wolfram language function name. */
  public static final Map<String, String> PREDEFINED_SYMBOLS_MAP =
      ParserConfig.TRIE_STRING2STRING_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();

  /** The alias name of some functions */
  private static final String[] ALIASES_STRINGS =
      {"ACos", "ACsc", "ASin", "ASec", "ATan", "ACosh", "ACsch", "ASinh", "ASech", "ATanh",
          "Divergence", "Diff", "EvalF", "Int", "Ln", "Trunc", "NthRoot"};

  /** The implemented function names for the alias function names. */
  private static final String[] ALIASES_SUBSTITUTES =
      {"ArcCos", "ArcCsc", "ArcSin", "ArcSec", "ArcTan", "ArcCosh", "ArcCsch", "ArcSinh", "ArcSech",
          "ArcTanh", "Div", "D", "N", "Integrate", "Log", "IntegerPart", "Surd"};

  /** Aliases which are mapped to the standard function symbols. */
  public static final Map<String, String> PREDEFINED_ALIASES_MAP =
      ParserConfig.TRIE_STRING2STRING_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();

  public static final String TIMES_STRING =
      ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? "times" : "Times";
  public static final String TRUE_STRING = "true"; // : "True";

  /** SuggestTree for all <code>DOLLAR_STRINGS, SYMBOL_STRINGS, FUNCTION_STRINGS</code> */
  private static SuggestTree SUGGEST_TREE = new SuggestTree(10000);

  public static SuggestTree getSuggestTree() {

    synchronized (AST2Expr.class) {
      if (SUGGEST_TREE.size() == 0) {
        synchronized (SUGGEST_TREE) {
          for (String str : FUNCTION_STRINGS) {
            if (str.length() > 1) {
              SUGGEST_TREE.put(str.toLowerCase(Locale.US), 2);
            }
          }
          for (String str : SYMBOL_STRINGS) {
            if (str.length() > 1) {
              SUGGEST_TREE.put(str.toLowerCase(Locale.US), 1);
            }
          }
          for (String str : DOLLAR_STRINGS) {
            if (str.length() > 1) {
              SUGGEST_TREE.put(str.toLowerCase(Locale.US), 1);
            }
          }
          for (String str : PHYSICAL_CONSTANTS_STRINGS) {
            if (str.length() > 1) {
              SUGGEST_TREE.put(str.toLowerCase(Locale.US), 1);
            }
          }
        }
      }
      return SUGGEST_TREE;
    }
  }

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
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
      for (String str : PHYSICAL_CONSTANTS_STRINGS) {
        PREDEFINED_SYMBOLS_MAP.put(str.toLowerCase(Locale.ENGLISH), str);
      }
      for (String str : FUNCTION_STRINGS) {
        PREDEFINED_SYMBOLS_MAP.put(str.toLowerCase(Locale.ENGLISH), str);
      }
      if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        for (int i = 0; i < ALIASES_STRINGS.length; i++) {
          PREDEFINED_ALIASES_MAP.put(ALIASES_STRINGS[i].toLowerCase(Locale.ENGLISH),
              ALIASES_SUBSTITUTES[i]); // YMBOLS[i]);
        }
      }
      if (Config.RUBI_CONVERT_SYMBOLS) {
        for (int i = 0; i < ALIASES_STRINGS.length; i++) {
          PREDEFINED_SYMBOLS_MAP.put(ALIASES_STRINGS[i].toLowerCase(Locale.ENGLISH),
              ALIASES_STRINGS[i]);
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
   * @param functionNode the parsed elements which should be added to the <code>IAST</code>
   * @param ast the empty <code>IAST</code> instance without any elements
   * @return the <code>ast</code>with the added elements
   */
  public IAST convert(FunctionNode functionNode, IASTAppendable ast) {
    ast.set(0, convertNode(functionNode.get(0)));
    for (int i = 1; i < functionNode.size(); i++) {
      ast.append(convertNode(functionNode.get(i)));
    }
    return ast;
  }

  public IExpr convert(ASTNode node) {
    fPrecision = fEngine.getNumericPrecision();
    return convertNode(node);
  }

  /**
   * Converts a parsed ASTNode expression into a Symja IExpr expression
   *
   * @param node the parsed ASTNode
   * @return the Symja expression
   */
  private IExpr convertNode(ASTNode node) {
    if (node == null) {
      return null;
    }

    if (node instanceof FunctionNode) {
      final FunctionNode functionNode = (FunctionNode) node;
      int size = functionNode.size();
      IASTMutable ast;
      switch (size) {
        case 1:
          ast = F.headAST0(convertNode(functionNode.get(0)));
          break;
        case 2:
          ast = F.unaryAST1(convertNode(functionNode.get(0)), convertNode(functionNode.get(1)));
          break;
        case 3:
          ast = F.binaryAST2(convertNode(functionNode.get(0)), convertNode(functionNode.get(1)),
              convertNode(functionNode.get(2)));
          break;
        case 4:
          ast = F.ternaryAST3(convertNode(functionNode.get(0)), convertNode(functionNode.get(1)),
              convertNode(functionNode.get(2)), convertNode(functionNode.get(3)));
          break;
        default:
          ast = F.mapRange(convertNode(functionNode.get(0)), 1, functionNode.size(), i -> {
            return convertNode(functionNode.get(i));
          });
      }

      int functionID = ast.headID();
      if (functionID > ID.UNKNOWN) {
        IExpr temp = evaluateOnInput(functionID, ast, functionNode);
        if (temp.isPresent()) {
          return temp;
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
      return F.$ps((ISymbol) convertNode(sn), convertNode(p3n.getConstraint()), p3n.isDefault(),
          true);
    }
    if (node instanceof Pattern2Node) {
      final Pattern2Node p2n = (Pattern2Node) node;
      SymbolNode sn = p2n.getSymbol();
      return F.$ps((ISymbol) convertNode(sn), convertNode(p2n.getConstraint()), p2n.isDefault(),
          false);
    }
    if (node instanceof PatternNode) {
      final PatternNode pn = (PatternNode) node;
      SymbolNode sn = pn.getSymbol();
      if (sn == null) {
        return F.$b(convertNode(pn.getConstraint()), pn.isDefault());
      }
      ASTNode defaultValue = pn.getDefaultValue();
      if (defaultValue != null) {
        return F.Optional(
            F.$p((ISymbol) convertNode(pn.getSymbol()), convertNode(pn.getConstraint())),
            convertNode(defaultValue));
      }
      return F.$p((ISymbol) convertNode(pn.getSymbol()), convertNode(pn.getConstraint()),
          pn.isDefault());
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

  /**
   * Try some &quot;evaluations&quot; for special expressions directly from the &quot;input
   * form&quot;.
   *
   * @param functionID an id <code>&gt; ID.UNKNOWN</code> i.e. a built-in function ID
   * @param ast
   * @param functionNode
   * @return
   */
  private IExpr evaluateOnInput(int functionID, IASTMutable ast, final FunctionNode functionNode) {
    try {
      IExpr expr;
      switch (functionID) {
        case ID.Association:
          if (ast.isAST1() && ast.arg1().isList()) {
            IExpr arg1 = ast.arg1();
            if (arg1.isListOfRules(true)) {
              return F.assoc((IAST) arg1);
            } else if (arg1.isList1()) {
              arg1 = arg1.first();
              if (arg1.isListOfRules(true)) {
                return F.assoc((IAST) arg1);
              }
            }
          }
          break;
        case ID.Get:
          if (ast.isAST1() && ast.arg1().isString()) {
            return S.Get.of(ast.arg1());
          }
          break;
        case ID.Import:
          if (ast.isAST1() && ast.arg1().isString()) {
            return S.Import.of(ast.arg1());
          }
          break;
        case ID.N:
          if (ast.isAST2() && ast.arg2().isInteger()) {
            try {
              long precision = ast.arg2().toLongDefault();
              if (EvalEngine.isApfloat(precision)) {
                fPrecision = precision;
                ast.set(1, convertNode(functionNode.get(1)));
              }
              return ast;
            } catch (ValidateException ve) {

            }
          }
          break;
        case ID.Sqrt:
          if (ast.isAST1()) {
            // rewrite from input: Sqrt(x) => Power(x, 1/2)
            return F.Power(ast.getUnevaluated(1), F.C1D2);
          }
          break;
        case ID.Exp:
          if (ast.isAST1()) {
            // rewrite from input: Exp(x) => E^x
            return F.Power(S.E, ast.getUnevaluated(1));
          }
          break;
        case ID.Power:
          if (ast.isPower() && ast.base().isPower() && ast.exponent().isMinusOne()) {
            IAST arg1Power = (IAST) ast.base();
            if (arg1Power.exponent().isNumber()) {
              // Division operator
              // rewrite from input: Power(Power(x, <number>),-1) =>
              // Power(x, - <number>)
              return F.Power(arg1Power.getUnevaluated(1),
                  ((INumber) arg1Power.getUnevaluated(2)).negate());
            }
          }
          break;
        case ID.Blank:
          expr = S.Blank.getEvaluator().evaluate(ast, fEngine);
          if (expr.isPresent()) {
            return expr;
          }
          break;
        case ID.BlankSequence:
          expr = S.BlankSequence.getEvaluator().evaluate(ast, fEngine);
          if (expr.isPresent()) {
            return expr;
          }
          break;
        case ID.BlankNullSequence:
          expr = S.BlankNullSequence.getEvaluator().evaluate(ast, fEngine);
          if (expr.isPresent()) {
            return expr;
          }
          break;
        case ID.Pattern:
          expr = S.Pattern.getEvaluator().evaluate(ast, fEngine);
          if (expr.isPresent()) {
            return expr;
          }
          break;
        case ID.Optional:
          expr = S.Optional.getEvaluator().evaluate(ast, fEngine);
          if (expr.isPresent()) {
            return expr;
          }
          break;
        // case ID.OptionsPattern:
        // expr = S.OptionsPattern.getEvaluator().evaluate(ast, fEngine);
        // if (expr.isPresent()) {
        // return expr;
        // }
        // break;
        case ID.Repeated:
          expr = S.Repeated.getEvaluator().evaluate(ast, fEngine);
          if (expr.isPresent()) {
            return expr;
          }
          break;
        case ID.Complex:
          expr = S.Complex.getEvaluator().evaluate(ast, fEngine);
          if (expr.isPresent()) {
            return expr;
          }
          break;
        case ID.Rational:
          expr = S.Rational.getEvaluator().evaluate(ast, fEngine);
          if (expr.isPresent()) {
            return expr;
          }
          break;
      }
    } catch (ValidateException ve) {
      Errors.printMessage(S.General, ve, fEngine);
    }
    return F.NIL;
  }

  public IExpr convertSymbol(final String nodeStr) {
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
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
