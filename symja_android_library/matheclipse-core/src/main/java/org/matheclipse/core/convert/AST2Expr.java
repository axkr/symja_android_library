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
      "SystemOptions", "Slot", "SlotAbsent", "SlotSequence", "StaticsVisible", "StartOfLine",
      "StartOfString", "Strict", "String", "Symbol", "TableAlignments", "TableDepth",
      "TableDirections", "TableHeadings", "TableSpacing", "TargetFunctions", "TestID", "Ticks",
      "Tiny", "TicksStyle", "Today", "TooLarge", "Top", "Trig", "True", "Unknown",
      "UseTypeChecking", "Variable", "Vectors", "VertexLabels", "VertexShapeFunction", "VertexSize",
      "VertexStyle", "ViewPoint", "White", "Whitespace", "WhitespaceCharacter", "WignerD", "Word",
      "WordCharacter", "WordSeparators", "WorkingPrecision", "Yellow", "ZeroTest"};

  public static final String[] FUNCTION_STRINGS = {"AASTriangle", "Abort", "Abs", "AbsArg",
      "AbsoluteCorrelation", "AbsoluteTime", "AbsoluteTiming", "Accumulate", "AddTo", "AddSides",
      "AddToClassPath", "AdjacencyMatrix", "Adjugate", "AiryAi", "AiryAiPrime", "AiryBi",
      "AiryBiPrime", "AllTrue", "Alphabet", "AmbientLight", "And", "AnglePath", "AngerJ",
      "AngleVector", "AnyTrue", "AntihermitianMatrixQ", "AntiSymmetric", "AntisymmetricMatrixQ",
      "Annotation", "Annuity", "AnnuityDue", "AlgebraicNumber", "Alternatives", "Apart", "AppellF1",
      "Append", "AppendTo", "Apply", "ApplySides", "Area", "ArcCos", "ArcCosh", "ArcCot", "ArcCoth",
      "ArcCsc", "ArcCsch", "ArcLength", "ArcSec", "ArcSech", "ArithmeticGeometricMean", "ArcSin",
      "ArcSinh", "ArcTan", "ArcTanh", "Arg", "ArgMax", "ArgMin", "Array", "ArrayDepth",
      "ArrayFlatten", "ArrayPad", "ArrayPlot", "ArrayReduce", "ArrayReshape", "Arrays", "ArrayQ",
      "ArrayRules", "Arrow", "Arrowheads", "ASATriangle", "AssociateTo", "Association",
      "AssociationQ", "AssociationMap", "AssociationThread", "Assuming", "Assumptions", "AtomQ",
      "Attributes", "Ball", "BarChart", "BartlettWindow", "BaseDecode", "BaseEncode", "BaseForm",
      "Beep", "Between", "Begin", "BeginPackage", "BeginTestSection", "BellB", "BellY",
      "BernoulliB", "BernoulliDistribution", "BernoulliProcess", "BernsteinBasis", "BesselI",
      "BesselJ", "BesselJZero", "BesselK", "BesselY", "BesselYZero", "Beta", "BetaDistribution",
      "BetaRegularized", "BezierFunction", "BinaryDistance", "BinarySerialize", "BinaryDeserialize",
      "BinaryRead", "BinaryWrite", "BinCounts", "Binomial", "BinomialDistribution",
      "BinomialProcess", "BioSequence", "BioSequenceQ", "BioSequenceTranscribe",
      "BioSequenceTranslate", "BipartiteGraphQ", "BitAnd", "BitClear", "BitFlip", "BitGet",
      "BitLength", "BitNot", "BitOr", "BitSet", "BitXor", "BlackmanHarrisWindow",
      "BlackmanNuttallWindow", "BlackmanWindow", "Blank", "BlankSequence", "BlankNullSequence",
      "Block", "Boole", "BooleanQ", "BooleanConvert", "BooleanFunction", "BooleanMaxterms",
      "BooleanMinimize", "BooleanMinterms", "BooleanTable", "BooleanVariables", "BoxWhiskerChart",
      "BrayCurtisDistance", "Break", "BrownianBridgeProcess", "BSplineFunction", "Button",
      "ByteArray", "ByteArrayToString", "ByteArrayQ", "ByteCount", "CanberraDistance", "Cancel",
      "CancelButton", "CarmichaelLambda", "CarlsonRC", "CarlsonRD", "CarlsonRF", "CarlsonRG",
      "CarlsonRJ", "CartesianProduct", "Cases", "CatalanNumber", "Catch", "Catenate",
      "CauchyDistribution", "CDF", "Ceiling", "CenterDot", "CentralMoment", "CForm",
      "CharacterRange", "Characters", "CharacteristicPolynomial", "ChebyshevT", "ChebyshevU",
      "Check", "CheckAbort", "ChessboardDistance", "ChineseRemainder", "ChiSquareDistribution",
      "CholeskyDecomposition", "Chop", "Circle", "CircleDot", "CirclePoints", "CircleTimes",
      "Clear", "ClearAll", "ClearAttributes", "ClebschGordan", "Clip", "Close",
      "ClosenessCentrality", "CMYColor", "Coefficient", "CoefficientArrays", "CoefficientList",
      "CoefficientRules", "Cofactor", "CollinearPoints", "Colon", "ColorData", "Column", "Collect",
      "Commonest", "CompatibleUnitQ", "Complement", "CompleteGraph", "Compile", "CompiledFunction",
      "CompilePrint", "Complex", "ComplexExpand", "ComplexPlot3D", "ComposeList", "ComposeSeries",
      "Composition", "CompositeQ", "CompoundExpression", "Condition", "ConditionalExpression",
      "Cone", "ConnectedGraphQ", "Conjugate", "ConjugateTranspose", "ConstantArray", "ContainsAll",
      "ContainsAny", "ContainsNone", "ContainsExactly", "ContainsOnly", "Context", "Contexts",
      "Continue", "ContinuedFraction", "ContourPlot", "Convergents", "ConvexHullMesh",
      "CoordinateBoundingBox", "CoordinateBounds", "CoplanarPoints", "CoprimeQ", "Correlation",
      "CorrelationDistance", "Cos", "Cosh", "CosineDistance", "CosIntegral", "CoshIntegral", "Cot",
      "Coth", "Count", "CountDistinct", "Counts", "Covariance", "CreateFile", "CreateDirectory",
      "Cross", "Csc", "Csch", "Cube", "CubeRoot", "Cuboid", "Cumulant", "Curl", "Cyclotomic",
      "CycleGraph", "Cycles", "Cylinder", "Dataset", "DateObject", "Dashing", "DateString",
      "DateValue", "Decrement", "DedekindNumber", "Default", "DefaultButton", "Defer", "Definition",
      "Delete", "DeleteCases", "DeleteDuplicates", "DeleteDuplicatesBy", "DeleteMissing",
      "Denominator", "DensityHistogram", "DensityPlot", "Depth", "Derivative", "DesignMatrix",
      "Det", "Diagonal", "DiagonalMatrix", "DiagonalMatrixQ", "DialogInput", "DialogNotebook",
      "DialogReturn", "DiceDissimilarity", "DifferenceDelta", "Differences", "DigitCount", "DigitQ",
      "Dimensions", "DiracDelta", "DirectionalLight", "DirichletBeta", "DirichletEta",
      "DirichletLambda", "DiscreteDelta", "DiscretePlot", "DiscreteUniformDistribution",
      "DirectedEdge", "DirectedInfinity", "Direction", "Directive", "DirichletWindow",
      "Discriminant", "DisjointQ", "Disk", "Dispatch", "DisplayForm", "Distribute", "Distributed",
      "Div", "Divide", "DivideBy", "DivideSides", "Divisible", "Divisors", "DivisorSum",
      "DivisorSigma", "Do", "Dodecahedron", "Dot", "DownValues", "Drop", "DuplicateFreeQ",
      "Dynamic", "DSolve", "Dt", "EasterSunday", "Echo", "EchoFunction", "EdgeCount", "EdgeForm",
      "EdgeList", "EdgeRules", "EdgeQ", "EdgeWeight", "EditDistance", "EffectiveInterest",
      "Eigensystem", "Eigenvalues", "EigenvectorCentrality", "Eigenvectors", "Element",
      "ElementData", "Eliminate", "Ellipsoid", "EllipticE", "EllipticF", "EllipticK", "EllipticPi",
      "EllipticTheta", "EmpiricalDistribution", "End", "EndPackage", "EndTestSection", "Entity",
      "Entropy", "Equal", "EqualTo", "Equivalent", "Erf", "Erfc", "Erfi", "ErlangDistribution",
      "EuclideanDistance", "EulerE", "EulerianGraphQ", "EulerPhi", "Evaluate", "EvenQ",
      "ExactNumberQ", "Except", "Exists", "Exit", "Exp", "Expand", "ExpandAll", "ExpandDenominator",
      "ExpandNumerator", "Expectation", "ExponentialDistribution", "ExpIntegralE", "ExpIntegralEi",
      "Exponent", "Export", "ExportString", "ExpToTrig", "ExtendedGCD", "Extract", "Factor",
      "Factorial", "FactorialMoment", "FactorialPower", "Factorial2", "FactorInteger",
      "FactorSquareFree", "FactorSquareFreeList", "FactorTerms", "FactorTermsList", "Flatten",
      "FlattenAt", "FlatTopWindow", "Fibonacci", "File", "FileFormat", "FileHash", "FileNameDrop",
      "FileNameJoin", "FilePrint", "FileNameTake", "FileNames", "FilterRules", "FindClusters",
      "FindCycle", "FindEulerianCycle", "FindEdgeCover", "FindFit", "FindFormula",
      "FindGeneratingFunction", "FindGraphCommunities", "FindGraphIsomorphism",
      "FindHamiltonianCycle", "FindIndependentEdgeSet", "FindIndependentVertexSet", "FindInstance",
      "FindLinearRecurrence", "FindMaximum", "FindMinimum", "FindMinimumCostFlow",
      "FindPermutation", "FindRoot", "FindSequenceFunction", "FindShortestPath", "FindShortestTour",
      "FindSpanningTree", "FindVertexCover", "First", "FirstCase", "FirstPosition", "Fit",
      "FittedModel", "FiveNum", "FixedPoint", "FixedPointList", "Floor", "Fold", "FoldList", "For",
      "ForAll", "FormBox", "Fourier", "FourierCosTransform", "FourierDCT", "FourierDCTMatrix",
      "FourierDST", "FourierDSTMatrix", "FourierMatrix", "FourierSinTransform",
      "FRatioDistribution", "FractionBox", "FractionalPart", "FrechetDistribution", "FreeQ",
      "FresnelC", "FresnelS", "FrobeniusNumber", "FrobeniusSolve", "FromCharacterCode",
      "FromContinuedFraction", "FromDigits", "FromLetterNumber", "FromPolarCoordinates",
      "FromRomanNumeral", "FromSphericalCoordinates", "FullForm", "FullDefinition", "FullSimplify",
      "Function", "FunctionDomain", "FunctionExpand", "FunctionPeriod", "FunctionRange",
      "FunctionURL", "Gamma", "GammaDistribution", "GammaRegularized", "Gather", "GatherBy",
      "GaussianMatrix", "GaussianWindow", "GCD", "GegenbauerC", "GeodesyData", "GeoDistance",
      "GeometricDistribution", "GeometricMean", "GeometricTransformation", "GeoPosition", "Get",
      "GoldbachList", "GompertzMakehamDistribution", "Grad", "Graph", "GraphCenter", "GraphData",
      "GraphDiameter", "Graphics", "GraphicsJSON", "GraphicsComplex", "GraphicsGroup", "Graphics3D",
      "Graphics3DJSON", "GraphComplement", "GraphDifference", "GraphDisjointUnion",
      "GraphIntersection", "GraphPeriphery", "GraphPower", "GraphQ", "GraphRadius", "GraphUnion",
      "GrayLevel", "Greater", "GreaterEqual", "GreaterEqualThan", "GreaterThan", "GridGraph",
      "GroebnerBasis", "GroupBy", "Gudermannian", "GumbelDistribution", "HamiltonianGraphQ",
      "HammingDistance", "HammingWindow", "HankelH1", "HankelH2", "HankelMatrix", "HannWindow",
      "Haversine", "HarmonicMean", "HarmonicNumber", "Head", "HeavisideLambda", "HeavisidePi",
      "HeavisideTheta", "HermiteH", "HermitianMatrixQ", "HessenbergDecomposition", "HessianMatrix",
      "HilbertMatrix", "Histogram", "HodgeDual", "Hold", "HoldForm", "HoldPattern", "Horner",
      "HornerForm", "Hue", "HurwitzLerchPhi", "HurwitzZeta", "HypercubeGraph", "Hyperfactorial",
      "HypergeometricDistribution", "HypergeometricPFQ", "HypergeometricU", "Hypergeometric0F1",
      "Hypergeometric0F1Regularized", "Hypergeometric1F1", "Hypergeometric1F1Regularized",
      "Hypergeometric2F1", "Hypergeometric2F1Regularized", "HypergeometricPFQRegularized",
      "Icosahedron", "Identity", "IdentityMatrix", "If", "Im", "Image", "ImageData",
      "ImageChannels", "ImageColorSpace", "ImageDimensions", "ImageQ", "ImageCrop", "ImageResize",
      "ImageRotate", "ImageScaled", "ImageSize", "ImageType", "ImplicitD", "Implies", "Import",
      "ImportString", "In", "Increment", "Indexed", "IndexGraph", "Inequality", "InexactNumberQ",
      "Infix", "Information", "Inner", "Input", "InputField", "InputForm", "InputStream",
      "InputString", "Insert", "InstallJava", "InstanceOf", "IntegerDigits", "IntegerExponent",
      "IntegerLength", "IntegerName", "IntegerPart", "IntegerPartitions", "IntegerQ", "Integrate",
      "Interpolation", "InterpolatingFunction", "InterpolatingPolynomial", "InterquartileRange",
      "IntersectingQ", "Interrupt", "Intersection", "Interval", "IntervalComplement",
      "IntervalData", "IntervalIntersection", "IntervalMemberQ", "IntervalUnion", "Inverse",
      "InverseFourier", "InverseGudermannian", "InverseBetaRegularized", "InverseCDF", "InverseErf",
      "InverseErfc", "InverseFunction", "InverseGammaRegularized", "InverseHaversine",
      "InverseJacobiCD", "InverseJacobiCN", "InverseJacobiDC", "InverseJacobiDN", "InverseJacobiNC",
      "InverseJacobiND", "InverseJacobiSC", "InverseJacobiSD", "InverseJacobiSN",
      "InverseLaplaceTransform", "InverseSeries", "InverseWeierstrassP", "InverseZTransform",
      "IsomorphicGraphQ", "JaccardDissimilarity", "JacobiAmplitude", "JacobiMatrix", "JacobiSymbol",
      "JacobiCD", "JacobiCN", "JacobiDC", "JacobiEpsilon", "JacobiDN", "JacobiNC", "JacobiND",
      "JacobiP", "JacobiSC", "JacobiSD", "JacobiSN", "JacobiZeta", "JavaClass", "JavaForm",
      "JavaNew", "JavaObject", "JavaObjectQ", "JavaShow", "JSForm", "JSFormData", "Join",
      "KelvinBei", "KelvinBer", "Key", "KeyDrop", "KeyExistsQ", "Keys", "KeySelect", "KeySort",
      "KeyTake", "KeyValuePattern", "KleinInvariantJ", "KnownUnitQ", "KolmogorovSmirnovTest",
      "KOrderlessPartitions", "KPartitions", "KroneckerDelta", "KroneckerProduct",
      "KroneckerSymbol", "Kurtosis", "Last", "LCM", "Labeled", "LambertW", "LeafCount", "LerchPhi",
      "LaguerreL", "LaplaceTransform", "Laplacian", "LeastSquares", "LegendreP", "LegendreQ",
      "Length", "LengthWhile", "Less", "LessEqual", "LessEqualThan", "LessThan", "LetterCounts",
      "LetterNumber", "LetterQ", "Level", "LevelQ", "LeviCivitaTensor", "Limit", "Line",
      "LinearModelFit", "LinearOptimization", "LinearProgramming", "LinearRecurrence",
      "LinearSolve", "LinearSolveFunction", "LineGraph", "LiouvilleLambda", "List",
      "ListContourPlot", "ListConvolve", "ListCorrelate", "ListDensityPlot", "ListLinePlot",
      "ListLinePlot3D", "ListLogLogPlot", "ListLogPlot", "ListLogLinearPlot", "ListPlot",
      "ListStepPlot", "ListStreamPlot", "ListVectorPlot", "ListPlot3D", "ListPointPlot3D",
      "ListPolarPlot", "ListQ", "Literal", "LLMFunction", "LoadJavaClass", "Log", "Log2", "Log10",
      "LogGamma", "LogNormalDistribution", "LogPlot", "LogLinearPlot", "LogLogPlot",
      "LogicalExpand", "LogisticSigmoid", "LogIntegral", "Longest", "Lookup", "LowerCaseQ",
      "LowerTriangularize", "LowerTriangularMatrixQ", "LucasL", "LUDecomposition", "MachineNumberQ",
      "MakeBoxes", "MangoldtLambda", "ManhattanDistance", "Manipulate", "MantissaExponent", "Map",
      "MapApply", "MapAt", "MapAll", "MapIndexed", "MapThread", "MatchingDissimilarity", "MatchQ",
      "MathMLForm", "MatrixD", "MatrixExp", "MatrixLog", "MatrixForm", "MatrixFunction",
      "MatrixMinimalPolynomial", "MatrixPlot", "MatrixPower", "MatrixQ", "MatrixRank", "Max",
      "MaxFilter", "Maximize", "MaxMemoryUsed", "Mean", "MeanFilter", "MeanDeviation", "Median",
      "MedianFilter", "MeijerG", "MemberQ", "MemoryAvailable", "MemoryInUse", "Merge", "MeshRange",
      "MessageName", "Message", "Messages", "MersennePrimeExponent", "MersennePrimeExponentQ",
      "Min", "MinFilter", "MinimalPolynomial", "Minimize", "MinMax", "Minor", "Minors", "Minus",
      "Missing", "MissingQ", "Mod", "ModularInverse", "Module", "MoebiusMu", "Moment",
      "MonomialList", "Most", "Multinomial", "MultiplicativeOrder", "MultiplySides",
      "NakagamiDistribution", "NameQ", "Names", "Nand", "NArgMax", "NArgMin", "ND", "NDSolve",
      "NExpectation", "Nearest", "NearestTo", "Needs", "Negative", "Nest", "NestList", "NestWhile",
      "NestWhileList", "NewLimit", "NextPrime", "NFourierTransform", "NIntegrate", "NMaximize",
      "NMaxValue", "NMinimize", "NMinValue", "NonCommutativeMultiply", "NonNegative", "NonPositive",
      "NoneTrue", "Nor", "Normal", "Normalize", "Norm", "NormalDistribution", "NormalMatrixQ",
      "Not", "NotElement", "NotListQ", "NProbability", "NProduct", "NRoots", "NSolve", "NSum",
      "NullSpace", "NumberDigit", "NumberFieldRootsOfUnity", "NumberLinePlot", "NumberQ",
      "Numerator", "NumericalOrder", "NumericalSort", "NumericArray", "NumericArrayQ",
      "NumericArrayType", "NumericQ", "NuttallWindow", "Octahedron", "OddQ", "Off", "On", "Opacity",
      "OpenAppend", "OpenRead", "OpenWrite", "Operate", "OptimizeExpression", "Optional", "Options",
      "OptionsPattern", "OptionValue", "Or", "Order", "Ordering", "OrderedQ", "Orthogonalize",
      "OrthogonalMatrixQ", "Out", "Outer", "OutputForm", "OutputStream", "Overflow", "Overscript",
      "OverscriptBox", "OwnValues", "Package", "PadeApproximant", "PadLeft", "PadRight",
      "ParallelMap", "ParametricPlot", "Parenthesis", "ParetoDistribution", "Part", "Partition",
      "PartitionsP", "PartitionsQ", "ParzenWindow", "PathGraph", "PathGraphQ", "PatternOrder",
      "PauliMatrix", "Pause", "PearsonChiSquareTest", "PerfectNumber", "PerfectNumberQ", "Pattern",
      "PatternTest", "PDF", "PearsonCorrelationTest", "Perimeter", "PermutationCycles",
      "PermutationCyclesQ", "PermutationList", "PermutationListQ", "PermutationReplace",
      "Permutations", "Permute", "PetersenGraph", "Pick", "PieChart", "Piecewise",
      "PiecewiseExpand", "PlanarGraph", "PlanarGraphQ", "Plot", "Plot3D", "Plus", "PlusMinus",
      "Pochhammer", "PolarPlot", "Point", "PointLight", "PointSize", "PoissonDistribution",
      "PoissonProcess", "PolyGamma", "Polygon", "PolygonalNumber", "Polyhedron", "PolyLog",
      "PolynomialExtendedGCD", "PolynomialGCD", "PolynomialLCM", "PolynomialQ",
      "PolynomialQuotient", "PolynomialQuotientRemainder", "PolynomialRemainder", "Position",
      "Positive", "PossibleZeroQ", "Postfix", "Power", "PowerExpand", "PowerMod",
      "PowersRepresentations", "PrecedenceForm", "Precision", "PreDecrement", "Prefix",
      "PreIncrement", "Prepend", "PrependTo", "Prime", "PrimeOmega", "PrimePi", "PrimePowerQ",
      "PrimeQ", "PrimitiveRoot", "PrimitiveRootList", "PrincipalComponents", "Print",
      "PrintableASCIIQ", "Prism", "Probability", "Product", "ProductLog", "Projection", "Protect",
      "PseudoInverse", "Put", "Pyramid", "QRDecomposition", "QuadraticIrrationalQ", "QuarticSolve",
      "Quantile", "Quantity", "QuantityDistribution", "QuantityMagnitude", "QuantityQ",
      "QuantityUnit", "Quartiles", "Quiet", "Quit", "Quotient", "QuotientRemainder", "RadicalBox",
      "Ramp", "RamseyNumber", "Random", "RandomChoice", "RandomComplex", "RandomInteger",
      "RandomGraph", "RandomPermutation", "RandomPrime", "RandomReal", "RandomSample",
      "RandomVariate", "Range", "RankedMax", "RankedMin", "Rational", "Rationalize", "RawBoxes",
      "Re", "Read", "ReadList", "ReadString", "RealAbs", "RealDigits", "RealSign",
      "RealValuedNumericQ", "RealValuedNumberQ", "Reap", "Rectangle", "Reduce", "Refine",
      "RegularExpression", "ReIm", "ReleaseHold", "Remove", "RemoveDiacritics", "Repeated",
      "RepeatedNull", "RepeatedTiming", "Replace", "ReplaceAll", "ReplaceAt", "ReplaceList",
      "ReplacePart", "ReplaceRepeated", "Rescale", "Rest", "Resultant", "Return", "Reverse",
      "ReverseSort", "RGBColor", "RiccatiSolve", "Riffle", "RightComposition",
      "RogersTanimotoDissimilarity", "RomanNumeral", "Root", "RootIntervals", "RootMeanSquare",
      "RootOf", "RootReduce", "Roots", "RotateLeft", "RotateRight", "RotationMatrix",
      "RotationTransform", "Round", "Row", "RowBox", "RowReduce", "RSolve", "RSolveValue", "Rule",
      "RuleDelayed", "RussellRaoDissimilarity", "SASTriangle", "SameObjectQ", "SameQ",
      "SatisfiabilityCount", "SatisfiabilityInstances", "SatisfiableQ", "Save", "SawtoothWave",
      "Scale", "Scaled", "ScalingTransform", "Scan", "ScientificForm", "SchurDecomposition", "Sec",
      "Sech", "SeedRandom", "Select", "SelectFirst", "SemanticImport", "SemanticImportString",
      "Sequence", "SequenceCases", "SequenceReplace", "SequenceSplit", "Series",
      "SeriesCoefficient", "SeriesData", "Set", "SetAttributes", "SetDelayed", "Share",
      "ShearingTransform", "Short", "Shortest", "Show", "Sign", "Signature", "SignCmp", "Simplex",
      "Simplify", "Sin", "Sinc", "SingularValueDecomposition", "SingularValueList", "Sinh",
      "SinIntegral", "SinhIntegral", "SixJSymbol", "Skewness", "SudokuSolve",
      "SokalSneathDissimilarity", "Solve", "Sort", "SortBy", "Sow", "Span", "SparseArray",
      "SparseArrayQ", "SpecialsFreeQ", "Specularity", "Sphere", "SphericalBesselJ",
      "SphericalBesselY", "SphericalHankelH1", "SphericalHankelH2", "SphericalHarmonicY", "Splice",
      "Split", "SplitBy", "SpotLight", "Sqrt", "SqrtBox", "SquaredEuclideanDistance", "SquareFreeQ",
      "SquareMatrixQ", "SquaresR", "SSSTriangle", "Stack", "StackBegin", "StandardDeviation",
      "StandardForm", "Standardize", "StarGraph", "StieltjesGamma", "StirlingS1", "StirlingS2",
      "StreamPlot", "StringCases", "StringCount", "StringContainsQ", "StringDrop",
      "StringExpression", "StringForm", "StringFormat", "StringFreeQ", "StringInsert", "StringJoin",
      "StringLength", "StringMatchQ", "StringPart", "StringPosition", "StringRepeat",
      "StringRiffle", "StringSplit", "StringTake", "StringTemplate", "StringToByteArray",
      "StringToStream", "StringTrim", "StringQ", "StringReplace", "StringReverse", "Structure",
      "StruveH", "StruveL", "StudentTDistribution", "Style", "StyleForm", "Subdivide",
      "Subfactorial", "Summary", "Subscript", "SubscriptBox", "Subsuperscript", "SubsuperscriptBox",
      "Subsets", "SubsetCases", "SubsetCount", "SubsetPosition", "SubsetQ", "SubsetReplace",
      "Subtract", "SubtractFrom", "SubtractSides", "Sum", "Superscript", "SuperscriptBox", "Surd",
      "SurfaceArea", "SurfaceGraphics", "SurvivalFunction", "Switch", "SyntaxLength", "SymbolName",
      "SymbolQ", "Symmetric", "SymmetricMatrixQ", "SyntaxQ", "SystemDialogInput", "Table",
      "TableForm", "TagSet", "TagSetDelayed", "Take", "TakeLargest", "TakeLargestBy",
      "TakeSmallest", "TakeSmallestBy", "TakeWhile", "Tally", "Tan", "Tanh", "TautologyQ", "Taylor",
      "TemplateApply", "TemplateExpression", "TemplateIf", "TemplateSlot", "TensorDimensions",
      "TensorProduct", "TensorRank", "TensorSymmetry", "TestReport", "TestReportObject",
      "TestResultObject", "Tetrahedron", "Text", "TextCell", "TextElement", "TextString",
      "TextStructure", "TeXForm", "Thickness", "Thread", "ThreeJSymbol", "Through", "Throw",
      "TimeConstrained", "TimeRemaining", "Times", "TimesBy", "TimeObject", "TimeValue", "Timing",
      "ToBoxes", "ToCharacterCode", "ToExpression", "ToeplitzMatrix", "Together",
      "ToPolarCoordinates", "ToRadicals", "ToSphericalCoordinates", "ToString", "Total",
      "ToIntervalData", "ToLowerCase", "ToUnicode", "ToUpperCase", "Tr", "Trace", "TraceForm",
      "TraditionalForm", "TransformationFunction", "TranslationTransform", "Transliterate",
      "Transpose", "TreeForm", "Triangle", "TrigExpand", "TrigReduce", "TrigSimplifyFu",
      "TrigToExp", "TrueQ", "TTest", "TukeyWindow", "Tube", "Tuples", "TwoWayRule", "Undefined",
      "Underflow", "Underscript", "UnderscriptBox", "Underoverscript", "UnderoverscriptBox",
      "UndirectedEdge", "Unequal", "UnequalTo", "Unevaluated", "UniformDistribution", "Union",
      "Unique", "UnitaryMatrixQ", "UnitConvert", "Unitize", "UnitStep", "UnitTriangle",
      "UnitVector", "Unprotect", "UnsameQ", "Unset", "UpperCaseQ", "UpperTriangularize",
      "UpperTriangularMatrixQ", "UpSet", "UpSetDelayed", "UpTo", "UpValues", "URLFetch", "ValueQ",
      "Values", "VandermondeMatrix", "Variables", "Variance", "VectorAngle", "VectorGreater",
      "VectorGreaterEqual", "VectorLess", "VectorLessEqual", "VectorPlot", "VectorQ", "Verbatim",
      "VertexEccentricity", "VertexList", "VerificationTest", "VertexQ", "Volume",
      "WeaklyConnectedGraphQ", "WeberE", "WeibullDistribution", "WeierstrassHalfPeriods",
      "WeierstrassInvariants", "WeierstrassP", "WeierstrassPPrime", "WeightedAdjacencyMatrix",
      "WeightedData", "WeightedGraphQ", "WheelGraph", "Which", "While", "With", "WhittakerM",
      "WhittakerW", "Write", "WriteString", "WordBoundary", "Xnor", "Xor", "YuleDissimilarity",
      "ZernikeR", "ZeroSymmetric", "Zeta", "ZTransform"};

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
