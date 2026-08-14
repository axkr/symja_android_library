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
      "$Input", "$InputFileName", "$IterationLimit", "$Line", "$MachineEpsilon",
      "$MachinePrecision", "$MaxMachineNumber", "$MessageList", "$MinMachineNumber", "$Notebooks",
      "$OperatingSystem", "$OutputSizeLimit", "$Packages", "$Path", "$PathnameSeparator",
      "$PerformanceGoal", "$PrePrint", "$PreRead", "$RecursionLimit", "$RootDirectory", "$Scaling",
      "$ScriptCommandLine", "$SystemCharacterEncoding", "$SystemMemory", "$TemporaryDirectory",
      "$UserBaseDirectory", "$UserName", "$Version"};

  public static final String[] SYMBOL_STRINGS = {"AlignmentPoint", "BaselinePosition",
      "ContentSelectable", "CoordinatesToolOptions", "FormatType", "FrameLabel", "FrameTicksStyle",
      "ImageMargins", "ImagePadding", "LabelStyle", "PlotRangeClipping", "PlotRegion",
      "PreserveImageOptions", "RotateLabel", //
      "AbsolutePointSize", "AbsoluteThickness", "AccuracyGoal", "All", "AllowedHeads",
      "AllowShortContext", "Algebraics", "Antisymmetric", "AspectRatio", "Automatic", "Axis",
      "Axes", "AxesEdge", "AxesLabel", "AxesOrigin", "AxesStyle", "Background", "BarOrigin",
      "BarSpacing", "BaseStyle", "BetweennessCentrality", "Black", "Blue", "Booleans", "Bottom",
      "Boxed", "BoxRatios", "Brown", "Byte", "Center", "Character", "CharacterEncoding",
      "ChartLabels", "ChartLegends", "ChartStyle", "ColorFunction", "ColorFunctionScaling",
      "ColorSpace", "ComplexInfinity", "Catalan", "CoefficientDomain", "Complexes",
      "ComplexityFunction", "Constant", "Constants", "Contours", "ContourShading", "ContourStyle",
      "Cyan", "Dashed", "DarkGray", "DataRange", "DefaultValue", "Degree", "DegreeLexicographic",
      "DegreeReverseLexicographic", "Delimiters", "DigitCharacter", "DirectedEdges",
      "DisplayFunction", "Disputed", "DistanceFunction", "DotDashed", "Dotted", "EdgeLabels",
      "EdgeShapeFunction", "EdgeStyle", "EliminationOrder", "EndOfFile", "EndOfLine", "EndOfString",
      "Epilog", "EulerGamma", "Expression", "Extension", "ExtentSize", "False", "Filling",
      "FillingStyle", "Flat", "Float", "FontColor", "FontFamily", "FontSize", "FourierParameters",
      "Frame", "FrameMargins", "FrameStyle", "FrameTicks", "Full", "GaussianIntegers", "General",
      "GenerateConditions", "GeneratedParameters", "Glaisher", "GoldenAngle", "GoldenRatio",
      "GraphLayout", "Gray", "Green", "GridLines", "GridLinesStyle", "Heads",
      "HexidecimalCharacter", "HoldAll", "HoldComplete", "HoldAllComplete", "HoldFirst", "HoldRest",
      "IgnoreCase", "Indeterminate", "Inherited", "Infinity", "InsertionFunction", "Inset",
      "Integer", "Integers", "InterpolationOrder", "Joined", "KeyAbsent", "Khinchin",
      "LabelingFunction", "LabelingSize", "Large", "Left", "Legended", "LetterCharacter",
      "Lexicographic", "Lighting", "LightBlue", "LightBrown", "LightCyan", "LightGray",
      "LightGreen", "LightMagenta", "LightOrange", "LightPink", "LightPurple", "LightRed",
      "LightYellow", "Listable", "Locked", "LongForm", "MachinePrecision", "Magenta", "Matrices",
      "MaxIterations", "MaxPlotPoints", "MaxPoints", "MaxRecursion", "MaxRoots", "Mesh", "Medium",
      "Method", "Modulus", "MonomialOrder", "NegativeDegreeLexicographic",
      "NegativeDegreeReverseLexicographic", "NegativeLexicographic", "NegativeIntegers",
      "NegativeRationals", "NegativeReals", "NHoldAll", "NHoldFirst", "NHoldRest", "None",
      "NonConstants", "NonNegativeIntegers", "NonNegativeRationals", "NonNegativeReals", "Nothing",
      "Nonexistent", "NotApplicable", "NotAvailable", "Now", "Null", "Number", "NumberString",
      "NumericFunction", "OneIdentity", "Orange", "Orderless", "Overlaps", "PerformanceGoal", "Pi",
      "Pink", "PlotLabel", "PlotLabels", "PlotLegends", "PlotPoints", "PlotRange",
      "PlotRangePadding", "PlotStyle", "PositiveIntegers", "PositiveRationals", "PositiveReals",
      "PrecisionGoal", "Primes", "Prolog", "Protected", "Purple", "RationalFunctions", "Rationals",
      "ReadProtected", "Real", "Record", "RecordSeparators", "Red", "Reals", "Right",
      "RoundingRadius", "RuntimeAttributes", "SameTest", "ScalingFunctions", "Second",
      "SeriesTermGoal", "SequenceHold", "SetSystemOptions", "Small", "StereochemistryElements",
      "SystemOptions", "SlotAbsent", "SpanFromAbove", "SpanFromBoth", "SpanFromLeft",
      "SplineClosed", "SplineDegree", "SplineKnots", "SplineWeights", "StaticsVisible",
      "StartOfLine", "StartOfString", "Strict", "String", "Symbol", "TableAlignments", "TableDepth",
      "TableDirections", "TableHeadings", "TableSpacing", "TargetFunctions", "TestID", "Thick",
      "Thin", "Ticks", "Tiny", "TicksStyle", "Today", "Tolerance", "TooLarge", "Top", "Transparent",
      "Trig", "True", "Unknown", "UseTypeChecking", "Variable", "Vectors", "VertexLabels",
      "VertexShapeFunction", "VertexSize", "VertexStyle", "ViewPoint", "White", "Whitespace",
      "WhitespaceCharacter", "WignerD", "Word", "WordCharacter", "WordSeparators",
      "WorkingPrecision", "Yellow", "ZeroSymmetric", "ZeroTest"};

  // START_FUNCTION_SYMBOLS

  public static final String[] FUNCTION_STRINGS = {"$TimeZone", "AASTriangle", "Abort", "Abs",
      "AbsArg", "AbsoluteDashing", "AbsoluteCorrelation", "AbsoluteTime", "AbsoluteTiming",
      "Accumulate", "Activate", "AcyclicGraphQ", "AddSides", "AddTo", "AddToClassPath",
      "AdjacencyGraph", "AdjacencyList", "AdjacencyMatrix", "Adjugate", "AffineTransform", "AiryAi",
      "AiryAiPrime", "AiryBi", "AiryBiPrime", "AlgebraicIntegerQ", "AlgebraicNumber", "AllPoints",
      "AllTrue", "Alphabet", "AlphabeticOrder", "Alternatives", "AmbientLight", "And", "AngerJ",
      "AnglePath", "AngleVector", "Annotation", "AnnotationRules", "Annuity", "AnnuityDue",
      "Annulus", "Antialiasing", "AntihermitianMatrixQ", "AntisymmetricMatrixQ", "AnyTrue", "Apart",
      "AppellF1", "Append", "AppendTo", "Apply", "ApplySides", "ArcCos", "ArcCosh", "ArcCot",
      "ArcCoth", "ArcCsc", "ArcCsch", "ArcLength", "ArcSec", "ArcSech", "ArcSin", "ArcSinh",
      "ArcTan", "ArcTanh", "Area", "Arg", "ArgMax", "ArgMin", "ArithmeticGeometricMean", "Array",
      "ArrayDepth", "ArrayDot", "ArrayFlatten", "ArrayMesh", "ArrayPad", "ArrayPlot", "ArrayQ",
      "ArrayReduce", "ArrayReshape", "ArrayRules", "ArraySymbol", "Arrays", "Arrow", "Arrowheads",
      "ASATriangle", "AssociateTo", "Association", "AssociationMap", "AssociationQ",
      "AssociationThread", "Assuming", "Assumptions", "Asymptotic", "AsymptoticSolve",
      "AsymptoticDSolveValue", "AsymptoticIntegrate", "AsymptoticRSolveValue", "Atom", "AtomList",
      "AtomQ", "Attributes", "AxisObject", "Ball", "Band", "BarChart", "BarChart3D", "BarLegend",
      "BarnesG", "BartlettWindow", "BaseDecode", "BaseEncode", "BaseForm", "Beep", "Begin",
      "BeginPackage", "BeginTestSection", "BellB", "BellY", "BenfordDistribution",
      "BenktanderGibratDistribution", "BenktanderWeibullDistribution", "BernoulliB",
      "BernoulliDistribution", "BernoulliProcess", "BernsteinBasis", "BesselI", "BesselJ",
      "BesselJZero", "BesselK", "BesselY", "BesselYZero", "Beta", "BetaBinomialDistribution",
      "BetaDistribution", "BetaPrimeDistribution", "BetaRegularized", "Between", "BezierCurve",
      "BezierFunction", "BinaryDeserialize", "BinaryDistance", "BinaryRead", "BinarySerialize",
      "BinaryWrite", "BinCounts", "BinLists", "Binomial", "BinomialDistribution", "BinomialProcess",
      "BinormalDistribution", "BioSequence", "BioSequenceQ", "BioSequenceTranscribe",
      "BioSequenceTranslate", "BipartiteGraphQ", "BitAnd", "BitClear", "BitFlip", "BitGet",
      "BitLength", "BitNot", "BitOr", "BitSet", "BitXor", "BlackmanHarrisWindow",
      "BlackmanNuttallWindow", "BlackmanWindow", "Blank", "BlankNullSequence", "BlankSequence",
      "Blend", "Block", "Bold", "Bond", "BondList", "Boole", "BooleanConvert",
      "BooleanCountingFunction", "BooleanFunction", "BooleanMaxterms", "BooleanMinimize",
      "BooleanMinterms", "BooleanQ", "BooleanTable", "BooleanVariables", "BorelTannerDistribution",
      "BoundaryMeshRegion", "BoundaryMeshRegionQ", "BoundaryStyle", "BoundedRegionQ",
      "BoundingRegion", "BoxMatrix", "BoxStyle", "BoxWhiskerChart", "BrayCurtisDistance", "Break",
      "BrownianBridgeProcess", "BSplineCurve", "BSplineFunction", "BSplineSurface", "BubbleChart",
      "Button", "ByteArray", "ByteArrayQ", "ByteArrayToString", "ByteCount", "CalendarConvert",
      "CalendarType", "Callout", "CalloutMarker", "CanberraDistance", "Cancel", "CancelButton",
      "CandlestickChart", "CantorMesh", "CapForm", "CapsuleShape", "CarlsonRC", "CarlsonRD",
      "CarlsonRF", "CarlsonRG", "CarlsonRJ", "CarmichaelLambda", "CartesianProduct", "Cases",
      "CatalanNumber", "Catch", "Catenate", "CauchyDistribution", "CDF", "Ceiling", "Cell",
      "CellularAutomaton", "CensoredDistribution", "CenterDot", "CentralFeature", "CentralMoment",
      "CentralMomentGeneratingFunction", "CForm", "CharacteristicFunction",
      "CharacteristicPolynomial", "CharacterRange", "Characters", "ChartBaseStyle",
      "ChartElementFunction", "ChartElements", "ChartLayout", "ChebyshevT", "ChebyshevU", "Check",
      "CheckAbort", "ChessboardDistance", "ChineseRemainder", "ChiSquareDistribution",
      "CholeskyDecomposition", "Chop", "ChromaticNumber", "ChromaticPolynomial", "Circle",
      "CircleDot", "CirclePoints", "CircleTimes", "CircularArcThrough", "Circumsphere", "Clear",
      "ClearAll", "ClearAttributes", "ClebschGordan", "Clip", "ClippingStyle", "ClipPlanes",
      "ClipPlanesStyle", "Close", "ClosenessCentrality", "CMYKColor", "Coefficient",
      "CoefficientArrays", "CoefficientList", "CoefficientRules", "Cofactor", "Collect",
      "CollinearPoints", "Colon", "ColorData", "ColorDataFunction", "ColorRules", "Column",
      "Commonest", "CommonUnits", "CompatibleUnitQ", "Compile", "CompiledFunction", "CompilePrint",
      "Complement", "CompleteGraph", "CompleteGraphQ", "CompleteKaryTree", "Complex",
      "ComplexArrayPlot", "ComplexContourPlot", "ComplexExpand", "ComplexListPlot", "ComplexPlot",
      "ComplexPlot3D", "ComplexRegionPlot", "ComplexStreamPlot", "ComplexVectorPlot", "ComposeList",
      "ComposeSeries", "CompositeQ", "Composition", "CompoundExpression", "Compress", "Condition",
      "ConditionalExpression", "Conditioned", "Cone", "ConicHullRegion", "Conjugate",
      "ConjugateTranspose", "ConnectedComponents", "ConnectedGraphComponents", "ConnectedGraphQ",
      "ConstantArray", "ContainsAll", "ContainsAny", "ContainsExactly", "ContainsNone",
      "ContainsOnly", "Context", "Contexts", "Continue", "ContinuedFraction", "ContourLabels",
      "ContourLines", "ContourPlot", "ContourPlot3D", "ControllerLinking", "ControllerPath",
      "Convergents", "ConvexHull", "ConvexHullMesh", "ConvexHullRegion", "ConvexRegionQ",
      "Convolve", "CoordinateBoundingBox", "CoordinateBounds", "CoplanarPoints", "CoprimeQ",
      "Correlation", "CorrelationDistance", "Cos", "Cosh", "CoshIntegral", "CosineDistance",
      "CosIntegral", "Cot", "Coth", "Count", "CountDistinct", "Counts", "CountsBy", "Covariance",
      "CreateDirectory", "CreateFile", "CreateUUID", "Cross", "CrossMatrix", "Csc", "Csch", "Cube",
      "CubeRoot", "Cuboid", "Cumulant", "CumulantGeneratingFunction", "Curl", "CurveClosed",
      "CycleGraph", "Cycles", "Cyclotomic", "Cylinder", "Darker", "Dashing", "DataDistribution",
      "Dataset", "DateBounds", "Dated", "DateDifference", "DateInterval", "DateList",
      "DateListLogPlot", "DateListPlot", "DateListStepPlot", "DateObject", "DateObjectQ",
      "DateOverlapsQ", "DatePlus", "DateRange", "DateSelect", "DateString", "DateValue",
      "DateWithinQ", "DawsonF", "DayCount", "DayMatchQ", "DayName", "DayPlus", "DayRange",
      "DayRound", "DeBruijnSequence", "Decrement", "Decompose", "DedekindNumber", "Default",
      "DefaultButton", "DefaultValues", "Defer", "Definition", "DelaunayMesh", "Delete",
      "DeleteCases", "DeleteDuplicates", "DeleteDuplicatesBy", "DeleteMissing", "Denominator",
      "DensityHistogram", "DensityPlot", "Depth", "Derivative", "DesignMatrix", "Det", "Diagonal",
      "DiagonalMatrix", "DiagonalMatrixQ", "DialogInput", "DialogNotebook", "DialogReturn",
      "DiamondMatrix", "DiceDissimilarity", "DifferenceDelta", "DifferenceQuotient",
      "DifferenceRoot", "Differences", "DigitCount", "DigitQ", "DigitSum",
      "DimensionalCombinations", "Dimensions", "DiracDelta", "DirectedEdge", "DirectedGraphQ",
      "DirectedInfinity", "Direction", "DirectionalLight", "Directive", "DirichletBeta",
      "DirichletEta", "DirichletLambda", "DirichletWindow", "DiscreteDelta", "DiscreteLimit",
      "DiscretePlot", "DiscretePlot3D", "DiscreteRatio", "DiscreteShift",
      "DiscreteUniformDistribution", "Discriminant", "DiskMatrix", "DisjointQ", "Disk",
      "DiskSegment", "Dispatch", "DisplayForm", "Distribute", "Distributed", "DistributionChart",
      "DistributionParameterQ", "Div", "Divide", "DivideBy", "DivideSides", "Divisible", "Divisors",
      "DivisorSigma", "DivisorSum", "Do", "Dodecahedron", "Dot", "DownValues", "Drop",
      "DropShadowing", "DSolve", "DSolveValue", "Dt", "DuplicateFreeQ", "Dynamic", "EasterSunday",
      "Echo", "EchoFunction", "EdgeAdd", "EdgeCount", "EdgeContract", "EdgeDelete", "EdgeForm",
      "EdgeLabelStyle", "EdgeList", "EdgeQ", "EdgeRules", "EdgeWeight", "EditDistance",
      "EffectiveInterest", "Eigensystem", "Eigenvalues", "EigenvectorCentrality", "Eigenvectors",
      "Element", "ElementData", "Eliminate", "Ellipsoid", "EllipticE", "EllipticExp", "EllipticF",
      "EllipticK", "EllipticLog", "EllipticPi", "EllipticTheta", "Empirical",
      "EmpiricalDistribution", "EmptyRegion", "End", "EndPackage", "EndTestSection", "Entity",
      "EntityClass", "EntityList", "EntityProperty", "Entropy", "Equal", "EqualTo", "Equivalent",
      "Erf", "Erfc", "Erfi", "ErlangDistribution", "EuclideanDistance", "EulerE", "EulerianGraphQ",
      "EulerPhi", "Evaluate", "EvaluationMonitor", "EvenQ", "ExactNumberQ", "Except", "Exclusions",
      "ExclusionsStyle", "Exists", "Exit", "Exp", "Expand", "ExpandAll", "ExpandDenominator",
      "ExpandNumerator", "Expectation", "ExpIntegralE", "ExpIntegralEi", "Exponent",
      "ExponentialDistribution", "ExponentialGeneratingFunction", "ExponentialPowerDistribution",
      "Export", "ExportString", "ExpressionGraph", "ExpToTrig", "ExtendedGCD",
      "ExtentElementFunction", "ExtentMarkers", "Extract", "FaceForm", "FaceGrids",
      "FaceGridsStyle", "Factor", "Factorial", "Factorial2", "FactorialMomentGeneratingFunction",
      "FactorList", "FactorialMoment", "FactorialPower", "FactorInteger", "FactorSquareFree",
      "FactorSquareFreeList", "FactorTerms", "FactorTermsList", "Fibonacci", "FilledCurve",
      "FilledTorus", "FindDistributionParameters", "FindMaximumFlow", "FiniteGroupCount",
      "FiniteAbelianGroupCount", "File", "FileFormat", "FileHash", "FileNameDrop", "FileNameJoin",
      "FileNames", "FileNameTake", "FilePrint", "FilterRules", "FindClusters", "FindCycle",
      "FindEdgeCover", "FindEulerianCycle", "FindFit", "FindFormula", "FindGeneratingFunction",
      "FindGraphCommunities", "FindGraphIsomorphism", "FindHamiltonianCycle",
      "FindIndependentEdgeSet", "FindIndependentVertexSet", "FindInstance", "FindLinearRecurrence",
      "FindList", "FindMaximum", "FindMinimum", "FindMinimumCostFlow", "FindPermutation",
      "FindRoot", "FindSequenceFunction", "FindShortestCurve", "FindShortestPath",
      "FindShortestTour", "FindSpanningTree", "FindVertexColoring", "FindVertexCover", "First",
      "FirstCase", "FirstPosition", "Fit", "FittedModel", "FiveNum", "FixedPoint", "FixedPointList",
      "Flatten", "FlattenAt", "FlatTopWindow", "Floor", "Fold", "FoldList", "FontSlant",
      "FontTracking", "FontWeight", "For", "ForAll", "FormBox", "Fourier", "FourierCosTransform",
      "FourierDCT", "FourierDCTMatrix", "FourierDST", "FourierDSTMatrix", "FourierMatrix",
      "FourierSinTransform", "FractionalPart", "FractionBox", "Framed", "FRatioDistribution",
      "FrechetDistribution", "FreeQ", "FresnelC", "FresnelS", "Friday", "FrobeniusNumber",
      "FrobeniusSolve", "FromAbsoluteTime", "FromCharacterCode", "FromContinuedFraction",
      "FromDateString", "FromDigits", "FromJulianDate", "FromLetterNumber", "FromPolarCoordinates",
      "FromRomanNumeral", "FromSphericalCoordinates", "FromUnixTime", "FullDefinition", "FullForm",
      "FullSimplify", "Function", "FunctionContinuous", "FunctionDiscontinuities", "FunctionDomain",
      "FunctionExpand", "FunctionPeriod", "FunctionRange", "FunctionSingularities", "FunctionURL",
      "Gamma", "GammaDistribution", "GammaRegularized", "Gather", "GatherBy", "GaussianMatrix",
      "GaussianWindow", "GCD", "GegenbauerC", "GeneratedQuantityMagnitudes", "GeneratingFunction",
      "GeodesyData", "GeoDistance", "GeometricDistribution", "GeometricMean",
      "GeometricTransformation", "GeoPosition", "Get", "GlobalClusteringCoefficient", "Glow",
      "GoldbachList", "GompertzMakehamDistribution", "Grad", "Graph", "Graph3D", "GraphCenter",
      "GraphComplement", "GraphData", "GraphDiameter", "GraphDifference", "GraphDisjointUnion",
      "GraphDistance", "GraphEmbedding", "GraphHighlight", "GraphHighlightStyle", "Graphics",
      "GraphicsGrid", "Graphics3D", "Graphics3DJSON", "GraphicsColumn", "GraphicsComplex",
      "GraphicsGroup", "GraphicsRow", "GraphicsJSON", "GraphIntersection", "GraphPeriphery",
      "GraphPlot", "GraphPower", "GraphQ", "GraphRadius", "GraphUnion", "GrayLevel", "Greater",
      "GreaterEqual", "GreaterEqualThan", "GreaterThan", "GridGraph", "GroebnerBasis", "GroupBy",
      "Groupings", "GroupOrbits", "Gudermannian", "GumbelDistribution", "HalfLine",
      "HalfNormalDistribution", "HalfPlane", "HalfSpace", "Haloing", "HamiltonianGraphQ",
      "HammingDistance", "HammingWindow", "HankelH1", "HankelH2", "HankelMatrix", "HannWindow",
      "HarmonicMean", "HarmonicNumber", "Hash", "Haversine", "HazardFunction", "Head",
      "HeavisideLambda", "HeavisidePi", "HeavisideTheta", "HermiteDecomposition", "HermiteH",
      "HermitianMatrixQ", "HessenbergDecomposition", "HessianMatrix", "Hexahedron", "Highlighted",
      "HilbertMatrix", "Histogram", "HistogramDistribution", "HistogramList", "HodgeDual", "Hold",
      "HoldForm", "HoldPattern", "Horner", "HornerForm", "Hue", "HurwitzLerchPhi", "HurwitzZeta",
      "HypercubeGraph", "Hyperfactorial", "Hypergeometric0F1", "Hypergeometric0F1Regularized",
      "Hypergeometric1F1", "Hypergeometric1F1Regularized", "Hypergeometric2F1",
      "Hypergeometric2F1Regularized", "HypergeometricDistribution", "HypergeometricPFQ",
      "HypergeometricPFQRegularized", "HypergeometricU", "HyperHarmonicNumber",
      "HypoexponentialDistribution", "Icosahedron", "Identity", "IdentityMatrix", "If", "Im",
      "Image", "ImageChannels", "ImageColorSpace", "ImageCrop", "ImageData", "ImageDimensions",
      "ImageHistogram", "ImageQ", "ImageResize", "ImageRotate", "ImageScaled", "ImageSize",
      "ImageSizeRaw", "ImageType", "ImplicitD", "Implies", "Import", "ImportString", "In",
      "Inactivate", "Inactive", "IncidenceMatrix", "IncludeQuantities", "Increment",
      "IndependentPhysicalQuantity", "IndependentUnit", "IndependentUnitDimension", "Indexed",
      "IndexGraph", "Inequality", "InexactNumberQ", "InfiniteLine", "InfinitePlane", "Infix",
      "Information", "Inner", "Input", "InputField", "InputForm", "InputStream", "InputString",
      "Insert", "InstallJava", "InstanceOf", "IntegerDigits", "IntegerExponent", "IntegerLength",
      "IntegerName", "IntegerPart", "IntegerPartitions", "IntegerQ", "Integrate",
      "InterpolatingFunction", "InterpolatingPolynomial", "Interpolation", "InterquartileRange",
      "Interrupt", "IntersectingQ", "Intersection", "Interval", "IntervalComplement",
      "IntervalData", "IntervalIntersection", "IntervalMarkers", "IntervalMarkersStyle",
      "IntervalMemberQ", "IntervalUnion", "Inverse", "InverseBetaRegularized", "InverseCDF",
      "InverseErf", "InverseErfc", "InverseFourier", "InverseFunction", "InverseGammaDistribution",
      "InverseGammaRegularized", "InverseGudermannian", "InverseHaversine", "InverseJacobiCD",
      "InverseJacobiCN", "InverseJacobiDC", "InverseJacobiDN", "InverseJacobiNC", "InverseJacobiND",
      "InverseJacobiSC", "InverseJacobiSD", "InverseJacobiSN", "InverseLaplaceTransform",
      "InverseSeries", "InverseSurvivalFunction", "InverseWeierstrassP", "InverseZTransform",
      "IrreduciblePolynomialQ", "IsomorphicGraphQ", "Italic", "JaccardDissimilarity",
      "JacobiAmplitude", "JacobiCD", "JacobiCN", "JacobiDC", "JacobiDN", "JacobiEpsilon",
      "JacobiMatrix", "JacobiNC", "JacobiND", "JacobiP", "JacobiSC", "JacobiSD", "JacobiSN",
      "JacobiSymbol", "JacobiZeta", "JavaClass", "JavaForm", "JavaNew", "JavaObject", "JavaObjectQ",
      "JavaShow", "Join", "JoinedCurve", "JoinForm", "JordanDecomposition", "JSForm", "JSFormData",
      "JulianDate", "KagiChart", "KaryTree", "KelvinBei", "KelvinBer", "Key", "KeyDrop",
      "KeyDropFrom", "KeyExistsQ", "KeyFreeQ", "KeyComplement", "KeyIntersection", "KeyMap",
      "KeyMemberQ", "Keys", "KeySelect", "KeySort", "KeySortBy", "KeyTake", "KeyUnion",
      "KeyValueMap", "KeyValuePattern", "KirchhoffMatrix", "KleinInvariantJ", "KnownUnitQ",
      "KolmogorovSmirnovTest", "KOrderlessPartitions", "KPartitions", "KroneckerDelta",
      "KroneckerProduct", "KroneckerSymbol", "Kurtosis", "LABColor", "Labeled", "LaguerreL",
      "LambertW", "LaplaceDistribution", "LaplaceTransform", "Laplacian", "LaplacianPDETerm",
      "Last", "LCHColor", "LCM", "LeafCount", "LeapYearQ", "LeastSquares", "LegendAppearance",
      "LegendFunction", "LegendLabel", "LegendLayout", "LegendMargins", "LegendMarkers",
      "LegendMarkerSize", "LegendreP", "LegendreQ", "Length", "LengthWhile", "LerchPhi", "Less",
      "LessEqual", "LessEqualThan", "LessThan", "LetterCounts", "LetterNumber", "LetterQ", "Level",
      "LevelQ", "LeviCivitaTensor", "Lighter", "LightingAngle", "Limit", "Line", "LinearModelFit",
      "LinearOptimization", "LinearProgramming", "LinearRecurrence", "LinearSolve",
      "LinearSolveFunction", "LineBreakChart", "LineGraph", "LineIntegralConvolutionPlot",
      "LineLegend", "LiouvilleLambda", "List", "ListContourPlot", "ListConvolve", "ListCorrelate",
      "ListCurvePathPlot", "ListDensityPlot", "ListLineIntegralConvolutionPlot", "ListLinePlot",
      "ListLinePlot3D", "ListLogLinearPlot", "ListLogLogPlot", "ListLogPlot", "ListPlot",
      "ListPlot3D", "ListPointPlot3D", "ListPolarPlot", "ListQ", "ListStepPlot",
      "ListStreamDensityPlot", "ListStreamPlot", "ListVectorDensityPlot", "ListVectorPlot",
      "Literal", "LLMFunction", "LoadJavaClass", "LocalClusteringCoefficient", "Locator", "Log",
      "Log10", "Log2", "LogBarnesG", "LogGamma", "LogicalExpand", "LogIntegral",
      "LogisticDistribution", "LogisticSigmoid", "LogLinearPlot", "LogLogisticDistribution",
      "LogLogPlot", "LogNormalDistribution", "LogPlot", "LogSeriesDistribution", "Longest",
      "Lookup", "LowerCaseQ", "LowerTriangularize", "LowerTriangularMatrixQ", "LucasL",
      "LUDecomposition", "LUVColor", "MachineNumberQ", "MakeBoxes", "MangoldtLambda",
      "ManhattanDistance", "Manipulate", "MantissaExponent", "Map", "MapAll", "MapApply", "MapAt",
      "MapIndexed", "MapThread", "MarcumQ", "MarginalDistribution", "MatchingDissimilarity",
      "MatchQ", "MathMLForm", "MatrixExp", "MatrixForm", "MatrixFunction", "MatrixLog",
      "MatrixMinimalPolynomial", "MatrixPlot", "MatrixPower", "MatrixQ", "MatrixRank",
      "MatrixSymbol", "Max", "MaxDate", "MaxFilter", "Maximize", "MaximalBy", "MaxMemoryUsed",
      "MaxStableDistribution", "MaxwellDistribution", "Mean", "MeanClusteringCoefficient",
      "MeanDeviation", "MeanFilter", "Median", "MedianDeviation", "MedianFilter", "MeijerG",
      "MeijerGReduce", "MeixnerDistribution", "MemberQ", "MemoryAvailable", "MemoryInUse", "Merge",
      "MersennePrimeExponent", "MersennePrimeExponentQ", "MeshCellCount", "MeshCellHighlight",
      "MeshCellLabel", "MeshCellMarker", "MeshCells", "MeshCellShapeFunction", "MeshCellStyle",
      "MeshCoordinates", "MeshFunctions", "MeshPrimitives", "MeshRange", "MeshRegion",
      "MeshRegionQ", "MeshShading", "MeshStyle", "Message", "MessageName", "Messages", "MidDate",
      "Min", "MinDate", "MinFilter", "MinimalPolynomial", "MinimalBy", "Minimize", "MinMax",
      "Minor", "Minors", "MinStableDistribution", "Minus", "Missing", "MissingQ", "MixedMagnitude",
      "MixedUnit", "MixtureDistribution", "Mod", "ModularInverse", "Module", "MoebiusMu",
      "Molecule", "MoleculeQ", "MoleculeValue", "Moment", "MomentGeneratingFunction",
      "MomentOfInertia", "Monday", "MonomialList", "Most", "Mouseover", "MovingAverage",
      "MovingMedian", "MoyalDistribution", "Multinomial", "MultinormalDistribution",
      "MultiplicativeOrder", "MultiplySides", "MultivariatePoissonDistribution",
      "MultivariateTDistribution", "NakagamiDistribution", "NameQ", "Names", "Nand", "NArgMax",
      "NArgMin", "ND", "NDSolve", "NDSolveValue", "Nearest", "NearestTo", "Needs", "Negative",
      "NegativeDefiniteMatrixQ", "NegativeSemidefiniteMatrixQ", "NeighborhoodGraph", "Nest",
      "NestList", "NestWhile", "NestWhileList", "NetGraph", "NExpectation", "NextDate", "NextPrime",
      "NFourierTransform", "NIntegrate", "NMaximize", "NMaxValue", "NMinimize", "NMinValue",
      "NoncentralChiSquareDistribution", "NonCommutativeMultiply", "NondimensionalizationTransform",
      "NoneTrue", "NonNegative", "NonPositive", "Nor", "Norm", "Normal", "NormalDistribution",
      "Normalize", "NormalMatrixQ", "NormalsFunction", "Not", "Notebook", "NotElement", "NotListQ",
      "NProbability", "NProduct", "NRoots", "NSolve", "NSolveValues", "NSum", "NullSpace",
      "NumberDigit", "NumberFieldRootsOfUnity", "NumberLinePlot", "NumberQ", "Numerator",
      "NumericalOrder", "NumericalSort", "NumericArray", "NumericArrayQ", "NumericArrayType",
      "NumericQ", "NuttallWindow", "Octahedron", "OddQ", "Off", "Offset", "On", "Opacity",
      "OpenAppend", "OpenRead", "OpenWrite", "Operate", "OptimizeExpression", "Optional", "Options",
      "OptionsPattern", "OptionValue", "Or", "Order", "OrderedQ", "Ordering", "Orthogonalize",
      "OrthogonalMatrixQ", "Out", "Outer", "OutputForm", "OutputStream", "Overflow", "Overscript",
      "OverscriptBox", "OwnValues", "Package", "PadeApproximant", "PadLeft", "PadRight",
      "PairedBarChart", "PairedHistogram", "PairedSmoothHistogram", "ParallelMap", "Parallelepiped",
      "Parallelogram", "ParameterMixtureDistribution", "ParametricPlot", "ParametricPlot3D",
      "Parenthesis", "ParetoDistribution", "Part", "Partition", "PartitionsP", "PartitionsQ",
      "ParzenWindow", "PathGraph", "PathGraphQ", "Pattern", "PatternOrder", "PatternTest",
      "PauliMatrix", "Pause", "PDF", "PearsonChiSquareTest", "PearsonCorrelationTest",
      "PerfectNumber", "PerfectNumberQ", "Perimeter", "PeriodicTablePlot", "PeriodogramArray",
      "Permanent", "PermutationCycles", "PermutationCyclesQ", "PermutationList", "PermutationListQ",
      "PermutationProduct", "PermutationReplace", "Permutations", "Permute", "PetersenGraph",
      "Pick", "Piecewise", "PiecewiseExpand", "PieChart", "Placed", "Plain", "PlanarAngle",
      "PlanarGraph", "PlanarGraphQ", "Plot", "Plot3D", "PlotFit", "PlotFitElements",
      "PlotHighlighting", "PlotMarkers", "PlotTheme", "Plus", "PlusMinus", "Pochhammer", "Point",
      "PointFigureChart", "PointLegend", "PointLight", "PointSize", "PoissonConsulDistribution",
      "PoissonDistribution", "PoissonProcess", "PolarAxes", "PolarGridLines", "PolarPlot",
      "PolarTicks", "PolyGamma", "Polygon", "PolygonalNumber", "PolygonAngle", "PolygonCoordinates",
      "Polyhedron", "PolyLog", "PolynomialExtendedGCD", "PolynomialGCD", "PolynomialLCM",
      "PolynomialMod", "PolynomialQ", "PolynomialQuotient", "PolynomialQuotientRemainder",
      "PolynomialReduce", "PolynomialRemainder", "Position", "PositionIndex", "Positive",
      "PositiveDefiniteMatrixQ", "PositiveSemidefiniteMatrixQ", "PossibleZeroQ", "Postfix", "Power",
      "PowerExpand", "PowerMod", "PowerRange", "PowersRepresentations", "PrecedenceForm",
      "Precision", "PreDecrement", "Prefix", "PreIncrement", "Prepend", "PrependTo", "PreviousDate",
      "Prime", "PrimeNu", "PrimeOmega", "PrimePi", "PrimePowerQ", "PrimeQ", "PrimeZetaP",
      "PrimitivePolynomialQ", "PrimitiveRoot", "PrimitiveRootList", "PrincipalComponents", "Print",
      "PrintableASCIIQ", "Prism", "Probability", "ProbabilityDistribution", "ProbabilityPlot",
      "ProbabilityScalePlot", "Product", "ProductDistribution", "ProductLog", "Projection",
      "Proportion", "Proportional", "Protect", "PseudoInverse", "Put", "PutAppend", "Pyramid",
      "QPochhammer", "QRDecomposition", "QuadraticIrrationalQ", "Quantile", "QuantilePlot",
      "Quantity", "QuantityArray", "QuantityDistribution", "QuantityForm", "QuantityMagnitude",
      "QuantityQ", "QuantityUnit", "QuantityVariable", "QuantityVariableCanonicalUnit",
      "QuantityVariableDimensions", "QuantityVariableIdentifier",
      "QuantityVariablePhysicalQuantity", "QuarticSolve", "Quartiles", "Query", "Quiet", "Quit",
      "Quotient", "QuotientRemainder", "RadicalBox", "Ramp", "RamseyNumber", "Random",
      "RandomChoice", "RandomComplex", "RandomGraph", "RandomInteger", "RandomPermutation",
      "RandomPrime", "RandomReal", "RandomSample", "RandomVariate", "Range", "RangeSpace",
      "RankDecomposition", "RankedMax", "RankedMin", "Raster", "Raster3D", "Rational",
      "Rationalize", "Ratios", "RawBackquote", "RawBoxes", "Re", "Read", "ReadLine", "ReadList",
      "ReadString", "RealAbs", "RealDigits", "RealSign", "RealValuedNumberQ", "RealValuedNumericQ",
      "Reap", "Rectangle", "RectangleChart", "Reduce", "Refine", "ReflectionTransform", "Region",
      "RegionBoundary", "RegionBoundaryStyle", "RegionBounds", "RegionCentroid", "RegionDimension",
      "RegionDistance", "RegionEmbeddingDimension", "RegionEqual", "RegionFunction",
      "RegionMeasure", "RegionMember", "RegionMoment", "RegionNearest", "RegionNearestFunction",
      "RegionPlot", "RegionQ", "RegionWithin", "RegularExpression", "RegularPolygon", "ReIm",
      "ReleaseHold", "ReliefPlot", "Remove", "RemoveDiacritics", "RenkoChart", "Repeated",
      "RepeatedNull", "RepeatedTiming", "Replace", "ReplaceAll", "ReplaceAt", "ReplaceList",
      "ReplacePart", "ReplaceRepeated", "Rescale", "Residue", "Resolve", "Rest", "Resultant",
      "Return", "Reverse", "ReverseElement", "ReverseEquilibrium", "ReverseSort",
      "ReverseUpEquilibrium", "RevolutionAxis", "RevolutionPlot3D", "RGBColor", "RiccatiSolve",
      "RiceDistribution", "RiemannSiegelTheta", "Riffle", "RightArrow", "RightArrowBar",
      "RightArrowLeftArrow", "RightComposition", "RightDownTeeVector", "RightDownVector",
      "RightDownVectorBar", "RightTee", "RightTeeArrow", "RightTeeVector", "RightTriangle",
      "RightTriangleBar", "RightTriangleEqual", "RightUpDownVector", "RightUpTeeVector",
      "RightUpVector", "RightUpVectorBar", "RightVector", "RightVectorBar",
      "RogersTanimotoDissimilarity", "RomanNumeral", "Root", "RootIntervals", "RootMeanSquare",
      "RootOf", "RootReduce", "Roots", "RootSum", "Rotate", "RotateLeft", "RotateRight",
      "RotationAction", "RotationMatrix", "RotationTransform", "Round", "RoundImplies", "Row",
      "RowBox", "RowReduce", "RSolve", "RSolveValue", "Rule", "RuleDelayed",
      "RussellRaoDissimilarity", "SameObjectQ", "SameQ", "SASTriangle", "SatisfiabilityCount",
      "SatisfiabilityInstances", "SatisfiableQ", "Saturday", "Save", "SawtoothWave", "Scale",
      "Scaled", "ScalingTransform", "Scan", "SchurDecomposition", "ScientificForm", "Sec", "Sech",
      "SechDistribution", "SectorChart", "SectorOrigin", "SectorSpacing", "SeedRandom", "Select",
      "SelectFirst", "SemanticImport", "SemanticImportString", "SeparateBoundaries", "Sequence",
      "SequenceCount", "SequenceCases", "SequencePosition", "SequenceReplace", "SequenceSplit",
      "Series", "SeriesCoefficient", "SeriesData", "Set", "SetAttributes", "SetDelayed", "Share",
      "ShearingTransform", "ShiftRegisterSequence", "Short", "ShortDownArrow", "Shortest",
      "ShortestCurveDistance", "ShortLeftArrow", "ShortRightArrow", "ShortUpArrow", "Show", "Sign",
      "Signature", "SignCmp", "SignedRegionDistance", "Simplex", "Simplify", "Sin", "Sinc",
      "SinghMaddalaDistribution", "SingularValueDecomposition", "SingularValueList", "Sinh",
      "SinhIntegral", "SinIntegral", "SixJSymbol", "Skeleton", "Skewness", "Slot", "SlotNumber",
      "SlotSequence", "SlotSequenceNumber", "SmallCircle", "SmithDecomposition",
      "SmoothDensityHistogram", "SmoothHistogram", "SokalSneathDissimilarity", "Solve",
      "SolveAlways", "SolveValues", "Sort", "SortBy", "Sow", "Spacer", "Spacings", "Span",
      "SparseArray", "SparseArrayQ", "SpearmanRho", "SpecialsFreeQ", "Specularity",
      "SpectrogramArray", "Sphere", "SphericalBesselJ", "SphericalBesselY", "SphericalHankelH1",
      "SphericalHankelH2", "SphericalHarmonicY", "SphericalPlot3D", "SphericalRegion",
      "SphericalShell", "Splice", "Split", "SplitBy", "SpotLight", "Sqrt", "SqrtBox", "Square",
      "SquaredEuclideanDistance", "SquareFreeQ", "SquareIntersection", "SquareMatrixQ", "SquaresR",
      "SquareSubset", "SquareSubsetEqual", "SquareSuperset", "SquareSupersetEqual", "SquareUnion",
      "SquareWave", "SSSTriangle", "Stack", "StackBegin", "StackedDateListPlot", "StackedListPlot",
      "StadiumShape", "StandardDeviation", "StandardForm", "Standardize", "Star", "StarGraph",
      "StatusArea", "StieltjesGamma", "StirlingS1", "StirlingS2", "StreamColorFunction",
      "StreamColorFunctionScaling", "StreamDensityPlot", "StreamPlot", "StreamPoints",
      "StreamScale", "StreamStyle", "StringCases", "StringContainsQ", "StringCount", "StringDrop",
      "StringExpression", "StringForm", "StringFormat", "StringFreeQ", "StringInsert", "StringJoin",
      "StringLength", "StringMatchQ", "StringPart", "StringPosition", "StringQ", "StringRepeat",
      "StringReplace", "StringReverse", "StringRiffle", "StringSplit", "StringStartsQ",
      "StringTake", "StringTemplate", "StringToByteArray", "StringToStream", "StringTrim",
      "Structure", "StruveH", "StruveL", "StudentTDistribution", "Style", "StyleForm", "Subdivide",
      "Subfactorial", "Subgraph", "Subresultants", "Subscript", "SubscriptBox", "Subsequences",
      "Subset", "SubsetCases", "SubsetCount", "SubsetEqual", "SubsetPosition", "SubsetQ",
      "SubsetReplace", "Subsets", "Subsuperscript", "SubsuperscriptBox", "Subtract", "SubtractFrom",
      "SubtractSides", "Succeeds", "SucceedsEqual", "SucceedsSlantEqual", "SucceedsTilde",
      "SuchThat", "SudokuSolve", "Sum", "Summary", "Sunday", "SuperDagger", "Superscript",
      "SuperscriptBox", "Superset", "SupersetEqual", "Surd", "SurfaceArea", "SurfaceGraphics",
      "SurvivalFunction", "SuzukiDistribution", "SwatchLegend", "Switch",
      "SymbolicDeltaProductArray", "SymbolicIdentityArray", "SymbolicOnesArray",
      "SymbolicZerosArray", "SymbolName", "SymbolQ", "Symmetric", "SymmetricMatrixQ",
      "SymmetricPolynomial", "SymmetricReduction", "Symmetrize", "SyntaxLength", "SyntaxQ",
      "SystemDialogInput", "Table", "TableForm", "TagSet", "TagSetDelayed", "TagUnset", "Take",
      "TakeLargest", "TakeLargestBy", "TakeList", "TakeSmallest", "TakeSmallestBy", "TakeWhile",
      "Tally", "Tan", "Tanh", "TautologyQ", "Taylor", "TemplateApply", "TemplateExpression",
      "TemplateIf", "TemplateSlot", "TensorContract", "TensorDimensions", "TensorProduct",
      "TensorRank", "TensorSymmetry", "TensorTranspose", "TensorWedge", "TestReport",
      "TestReportObject", "TestResultObject", "Tetrahedron", "TeXForm", "Text", "TextCell",
      "TextElement", "TextString", "TextStructure", "Texture", "TextureCoordinateFunction",
      "TextureCoordinateScaling", "Therefore", "Thickness", "Thread", "ThreeJSymbol", "Through",
      "Throw", "Thursday", "Tilde", "TildeEqual", "TildeFullEqual", "TildeTilde", "TimeConstrained",
      "TimelinePlot", "TimeObject", "TimeRemaining", "Times", "TimesBy", "TimeValue", "TimeZone",
      "TimeZoneOffset", "Timing", "ToBoxes", "ToCharacterCode", "ToeplitzMatrix", "ToExpression",
      "Together", "ToIntervalData", "ToLowerCase", "Tooltip", "ToPolarCoordinates",
      "TopologicalSort", "ToRadicals", "TortoiseShellBracket", "Torus", "TorusGraph",
      "ToSphericalCoordinates", "ToString", "Total", "TouchscreenAutoZoom", "ToUnicode",
      "ToUpperCase", "Tr", "Trace", "TraceForm", "TradingChart", "TraditionalForm",
      "TransformationFunction", "TransformedDistribution", "TransformedRegion", "TransitiveClosure",
      "Translate", "TranslationTransform", "Transliterate", "Transpose", "TreeForm", "TreeGraph",
      "TreeGraphQ", "TreePlot", "Triangle", "TriangleCenter", "TriangleConstruct",
      "TriangleMeasurement", "TriangleWave", "TriangularDistribution", "TrigExpand", "TrigFactor",
      "TrigReduce", "TrigSimplifyFu", "TrigToExp", "TrueQ", "TTest", "Tube", "Tuesday",
      "TukeyWindow", "Tuples", "TwoWayRule", "UnaryMinusPlus", "UnaryPlus", "UnaryPlusMinus",
      "Uncompress", "Undefined", "Underflow", "Underlined", "Underoverscript", "UnderoverscriptBox",
      "Underscript", "UnderscriptBox", "UndirectedEdge", "Unequal", "UnequalTo", "Unevaluated",
      "UniformDistribution", "UniformSumDistribution", "Union", "UnionPlus", "Unique",
      "UnitaryMatrixQ", "UnitBox", "UnitConvert", "UnitDimensions", "Unitize", "UnitSimplify",
      "UnitStep", "UnitTriangle", "UnitVector", "UnityDimensions", "UnixTime", "Unprotect",
      "UnsameQ", "Unset", "UpArrow", "UpArrowBar", "UpArrowDownArrow", "UpDownArrow",
      "UpEquilibrium", "UpperCaseQ", "UpperLeftArrow", "UpperRightArrow", "UpperTriangularize",
      "UpperTriangularMatrixQ", "UpSet", "UpSetDelayed", "UpTee", "UpTeeArrow", "UpTo", "UpValues",
      "URLDecode", "URLEncode", "URLFetch", "ValueQ", "Values", "VandermondeMatrix", "Variables",
      "Variance", "VectorAngle", "VectorAspectRatio", "VectorColorFunction",
      "VectorColorFunctionScaling", "VectorDensityPlot", "VectorGreater", "VectorGreaterEqual",
      "VectorLess", "VectorLessEqual", "VectorMarkers", "VectorPlot", "VectorPoints", "VectorQ",
      "VectorScale", "VectorSizes", "VectorStyle", "VectorSymbol", "Vee", "Verbatim",
      "VerificationTest", "VertexAdd", "VertexColors", "VertexContract", "VertexDelete",
      "VertexCoordinates", "VertexCount", "VertexDegree", "VertexEccentricity", "VertexInDegree",
      "VertexLabelStyle", "VertexOutDegree", "VertexList", "VertexNormals", "VertexQ",
      "VertexShape", "VertexTextureCoordinates", "VertexWeight", "VerticalBar", "VerticalSeparator",
      "VerticalTilde", "ViewAngle", "ViewCenter", "ViewMatrix", "ViewProjection", "ViewRange",
      "ViewVector", "ViewVertical", "Volume", "VoronoiMesh", "WaringYuleDistribution",
      "WeaklyConnectedGraphQ", "WeberE", "Wedge", "Wednesday", "Weekend", "WeibullDistribution",
      "WeierstrassHalfPeriods", "WeierstrassInvariants", "WeierstrassP", "WeierstrassPPrime",
      "WeightedAdjacencyMatrix", "WeightedData", "WeightedGraphQ", "WheelGraph", "Which", "While",
      "WhiteCornerBracket", "WhittakerM", "WhittakerW", "WignerSemicircleDistribution", "With",
      "WordBoundary", "WordCloud", "WordOrientation", "WordSelectionFunction", "WordSpacings",
      "Write", "WriteString", "Xnor", "Xor", "XYZColor", "Yesterday", "YuleDissimilarity",
      "ZernikeR", "Zeta", "ZetaZero", "ZipfDistribution", "ZTransform"};

  // END_FUNCTION_SYMBOLS

  public static Map<String, Integer> RUBI_STATISTICS_MAP;

  /** Map the lower case identifier name to the upper case MMA language function name. */
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
      IAST ast;
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
  private IExpr evaluateOnInput(int functionID, IAST ast, final FunctionNode functionNode) {
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
                ast = ast.setAtCopy(1, convertNode(functionNode.get(1)));
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
