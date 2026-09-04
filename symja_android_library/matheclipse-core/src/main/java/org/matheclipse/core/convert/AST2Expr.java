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
      "CompilationOptions", "CompilationTarget", "ColorSpace", "ComplexInfinity", "Catalan",
      "CoefficientDomain", "Complexes", "ComplexityFunction", "Constant", "Constants", "Contours",
      "ContourShading", "ContourStyle", "Cyan", "Dashed", "DarkGray", "DataRange", "DefaultValue",
      "Degree", "DegreeLexicographic", "DegreeReverseLexicographic", "Delimiters", "DigitCharacter",
      "DirectedEdges", "DisplayFunction", "Disputed", "DistanceFunction", "DotDashed", "Dotted",
      "EdgeLabels", "EdgeShapeFunction", "EdgeStyle", "EliminationOrder", "EndOfFile", "EndOfLine",
      "EndOfString", "Epilog", "EulerGamma", "Expression", "Extension", "ExtentSize", "False",
      "Filling", "FillingStyle", "Flat", "Float", "FontColor", "FontFamily", "FontSize",
      "FourierParameters", "Frame", "FrameMargins", "FrameStyle", "FrameTicks", "Full",
      "GaussianIntegers", "General", "GenerateConditions", "GeneratedParameters", "Glaisher",
      "GoldenAngle", "GoldenRatio", "GraphLayout", "Gray", "Green", "GridLines", "GridLinesStyle",
      "Heads", "HexidecimalCharacter", "HoldAll", "HoldComplete", "HoldAllComplete", "HoldFirst",
      "HoldRest", "IgnoreCase", "Indeterminate", "Inherited", "Infinity", "InsertionFunction",
      "Inset", "Integer", "Integers", "InterpolationOrder", "Joined", "KeyAbsent", "Khinchin",
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
      "Parallelization", "PrecisionGoal", "Primes", "Prolog", "Protected", "Purple",
      "RationalFunctions", "Rationals", "ReadProtected", "Real", "Record", "RecordSeparators",
      "Red", "Reals", "Right", "RoundingRadius", "RuntimeAttributes", "RuntimeOptions", "SameTest",
      "ScalingFunctions", "Second", "SeriesTermGoal", "SequenceHold", "SetSystemOptions", "Small",
      "StereochemistryElements", "SystemOptions", "SlotAbsent", "SpanFromAbove", "SpanFromBoth",
      "SpanFromLeft", "SplineClosed", "SplineDegree", "SplineKnots", "SplineWeights",
      "StandardBlue", "StandardBrown", "StandardCyan", "StandardGray", "StandardGreen",
      "StandardMagenta", "StandardOrange", "StandardPink", "StandardPurple", "StandardRed",
      "StandardYellow", "StaticsVisible", "StartOfLine", "StartOfString", "Strict", "String",
      "Symbol", "TableAlignments", "TableDepth", "TableDirections", "TableHeadings", "TableSpacing",
      "TargetFunctions", "TestID", "Thick", "Thin", "Ticks", "Tiny", "TicksStyle", "Today",
      "Tolerance", "TooLarge", "Top", "Transparent", "Trig", "True", "Unknown", "UseTypeChecking",
      "Variable", "Vectors", "VertexLabels", "VertexShapeFunction", "VertexSize", "VertexStyle",
      "ViewPoint", "White", "Whitespace", "WhitespaceCharacter", "WignerD", "Word", "WordCharacter",
      "WordSeparators", "WorkingPrecision", "Yellow", "ZeroSymmetric", "ZeroTest"};

  // START_FUNCTION_SYMBOLS

  public static final String[] FUNCTION_STRINGS = {"$GeoLocation", "$TimeZone", "$UnitSystem",
      "AASTriangle", "Abort", "Abs", "AbsArg", "AbsoluteDashing", "AbsoluteCorrelation",
      "AbsoluteTime", "AbsoluteTiming", "AccountingForm", "Accumulate", "ActionMenu", "Activate",
      "AcyclicGraphQ", "AddSides", "AddTo", "AddToClassPath", "AdjacencyGraph", "AdjacencyList",
      "AdjacencyMatrix", "Adjugate", "AffineTransform", "AggregateBy", "AiryAi", "AiryAiPrime",
      "AiryBi", "AiryBiPrime", "AlgebraicIntegerQ", "AlgebraicNumber", "Alignment",
      "AllowedDimensions", "AllPoints", "AllTrue", "Alpha", "Alphabet", "AlphabeticOrder",
      "AlphaChannel", "Alternatives", "AltitudeMethod", "AmbientLight", "And", "AngerJ",
      "AnglePath", "AngleVector", "Animate", "AnimationDirection", "AnimationRate",
      "AnimationRepetitions", "AnimationRunning", "Animator", "Annotation", "AnnotationRules",
      "Annuity", "AnnuityDue", "Annulus", "Antialiasing", "Antihermitian", "AntihermitianMatrixQ",
      "AntisymmetricMatrixQ", "AnyTrue", "Apart", "Appearance", "AppearanceElements", "AppellF1",
      "Append", "AppendTo", "Apply", "ApplySides", "ArcCos", "ArcCosh", "ArcCot", "ArcCoth",
      "ArcCsc", "ArcCsch", "ArcLength", "ArcSec", "ArcSech", "ArcSin", "ArcSinh", "ArcTan",
      "ArcTanh", "Area", "Arg", "ArgMax", "ArgMin", "ArithmeticGeometricMean", "Around",
      "AroundReplace", "Array", "ArrayDepth", "ArrayDot", "ArrayExpand", "ArrayFlatten",
      "ArrayMesh", "ArrayPad", "ArrayPlot", "ArrayQ", "ArrayReduce", "ArrayReshape", "ArrayRules",
      "ArraySimplify", "ArraySymbol", "Arrays", "Arrow", "Arrowheads", "ASATriangle", "AssociateTo",
      "Association", "AssociationMap", "AssociationQ", "AssociationThread", "Assuming",
      "Assumptions", "AstroAngularSeparation", "AstroBackground", "AstroCenter", "AstroDistance",
      "AstroGraphics", "AstroGridLines", "AstroGridLinesStyle", "AstroPosition", "AstroProjection",
      "AstroRange", "AstroRangePadding", "AstroReferenceFrame", "AstroRiseSet", "AstroStyling",
      "AstroSubpoint", "AstroZoomLevel", "Asymptotic", "AsymptoticSolve", "AsymptoticDSolveValue",
      "AsymptoticIntegrate", "AsymptoticRSolveValue", "Atom", "AtomCount", "AtomDiagramCoordinates",
      "AtomList", "AtomQ", "Attributes", "AutoAction", "AutorunSequencing", "AxisObject",
      "Backslash", "Backsubstitution", "Ball", "Band", "BarChart", "BarChart3D", "BarLegend",
      "BarnesG", "BartlettWindow", "BaseDecode", "BaseEncode", "BaseForm", "Because", "Beep",
      "Begin", "BeginPackage", "BeginTestSection", "BellB", "BellY", "BenfordDistribution",
      "BenktanderGibratDistribution", "BenktanderWeibullDistribution", "BernoulliB",
      "BernoulliDistribution", "BernoulliProcess", "BernsteinBasis", "BesselI", "BesselJ",
      "BesselJZero", "BesselK", "BesselY", "BesselYZero", "Beta", "BetaBinomialDistribution",
      "BetaDistribution", "BetaPrimeDistribution", "BetaRegularized", "Between", "BezierCurve",
      "BezierFunction", "BilateralFilter", "Binarize", "BinaryDeserialize", "BinaryDistance",
      "BinaryRead", "BinarySerialize", "BinaryWrite", "BinCounts", "BinLists", "Binomial",
      "BinomialDistribution", "BinomialProcess", "BinormalDistribution", "BioSequence",
      "BioSequenceBackTranslateList", "BioSequenceComplement", "BioSequenceInstances",
      "BioSequenceModify", "BioSequenceQ", "BioSequenceReverseComplement", "BioSequenceTranscribe",
      "BioSequenceTranslate", "BipartiteGraphQ", "BitAnd", "BitClear", "BitFlip", "BitGet",
      "BitLength", "BitNot", "BitOr", "BitSet", "BitXor", "BlackmanHarrisWindow",
      "BlackmanNuttallWindow", "BlackmanWindow", "Blank", "BlankNullSequence", "BlankSequence",
      "Blend", "Block", "Blur", "Bold", "Bond", "BondCount", "BondList", "Bookmarks", "Boole",
      "BooleanConvert", "BooleanCountingFunction", "BooleanFunction", "BooleanMaxterms",
      "BooleanMinimize", "BooleanMinterms", "BooleanQ", "BooleanTable", "BooleanVariables",
      "BorelTannerDistribution", "BottomHatTransform", "BoundaryMeshRegion", "BoundaryMeshRegionQ",
      "BoundaryStyle", "BoundedRegionQ", "BoundingRegion", "BoxMatrix", "BoxStyle",
      "BoxWhiskerChart", "BrayCurtisDistance", "Break", "BrownianBridgeProcess", "BSplineCurve",
      "BSplineFunction", "BSplineSurface", "BubbleChart", "Button", "ButtonBar", "ByteArray",
      "ByteArrayQ", "ByteArrayToString", "ByteCount", "CachedValue", "CalendarConvert",
      "CalendarType", "Callout", "CalloutMarker", "CanberraDistance", "Cancel", "CancelButton",
      "CandlestickChart", "CantorMesh", "Cap", "CapForm", "CapitalDifferentialD", "CapsuleShape",
      "CarlsonRC", "CarlsonRD", "CarlsonRF", "CarlsonRG", "CarlsonRJ", "CarmichaelLambda",
      "CartesianProduct", "Cases", "Casoratian", "CatalanNumber", "Catch", "Catenate",
      "CauchyDistribution", "CDF", "Ceiling", "CelestialSystem", "Cell", "CellularAutomaton",
      "CensoredDistribution", "CenterDot", "CentralFeature", "CentralMoment",
      "CentralMomentGeneratingFunction", "CForm", "CharacteristicFunction",
      "CharacteristicPolynomial", "CharacterRange", "Characters", "ChartBaseStyle",
      "ChartElementFunction", "ChartElements", "ChartLayout", "ChebyshevT", "ChebyshevU", "Check",
      "CheckAbort", "Checkbox", "CheckboxBar", "ChemicalConvert", "ChemicalFormula",
      "ChemicalReaction", "ChessboardDistance", "ChineseRemainder", "ChiSquareDistribution",
      "CholeskyDecomposition", "Chop", "ChromaticPolynomial", "Circle", "CircleDot", "CircleMinus",
      "CirclePlus", "CirclePoints", "CircleTimes", "CircularArcThrough", "Circumsphere", "Clear",
      "ClearAll", "ClearAttributes", "ClebschGordan", "ClickPane", "Clip", "ClippingStyle",
      "ClipPlanes", "ClipPlanesStyle", "Clock", "Close", "ClosenessCentrality", "Closing",
      "CMYKColor", "Coefficient", "CoefficientArrays", "CoefficientList", "CoefficientRules",
      "Cofactor", "Collect", "CollinearPoints", "Colon", "ColorCombine", "ColorConvert",
      "ColorData", "ColorDataFunction", "ColorDistance", "ColorNegate", "ColorQuantize",
      "ColorReplace", "ColorRules", "ColorSeparate", "ColorSetter", "ColorSlider", "Column",
      "ColumnAlignments", "Commonest", "CommonestFilter", "CommonUnits", "CompatibleUnitQ",
      "Compile", "CompiledFunction", "CompilePrint", "Complement", "CompleteGraph",
      "CompleteGraphQ", "CompleteKaryTree", "Complex", "ComplexArrayPlot", "ComplexContourPlot",
      "ComplexExpand", "ComplexListPlot", "ComplexPlot", "ComplexPlot3D", "ComplexRegionPlot",
      "ComplexStreamPlot", "ComplexVectorPlot", "ComponentExpand", "ComponentMeasurements",
      "ComposeList", "ComposeSeries", "CompositeQ", "Composition", "CompoundExpression", "Compress",
      "Condition", "ConditionalExpression", "Conditioned", "Cone", "Congruent", "ConicHullRegion",
      "Conjugate", "ConjugateTranspose", "ConnectedComponents", "ConnectedGraphComponents",
      "ConnectedGraphQ", "ConnectedMoleculeComponents", "ConnectedMoleculeQ", "ConstantArray",
      "ConstantRegionQ", "ContainsAll", "ContainsAny", "ContainsExactly", "ContainsNone",
      "ContainsOnly", "ContentSize", "Context", "Contexts", "Continue", "ContinuedFraction",
      "ContinuousAction", "ContourLabels", "ContourLines", "ContourPlot", "ContourPlot3D",
      "Control", "ControllerLinking", "ControllerPath", "ControlPlacement", "ControlType",
      "Convergents", "ConvexHull", "ConvexHullMesh", "ConvexHullRegion", "ConvexRegionQ",
      "Convolve", "CoordinateBoundingBox", "CoordinateBounds", "CoplanarPoints", "CoprimeQ",
      "Coproduct", "CopyFile", "CornerFilter", "CornerNeighbors", "Correlation",
      "CorrelationDistance", "Cos", "Cosh", "CoshIntegral", "CosineDistance", "CosIntegral", "Cot",
      "Coth", "Count", "CountDistinct", "Counts", "CountsBy", "Covariance", "CreateDirectory",
      "CreateFile", "CreateUUID", "Cross", "CrossingDetect", "CrossMatrix", "Csc", "Csch", "Cube",
      "CubeRoot", "Cubics", "Cuboid", "Cumulant", "CumulantGeneratingFunction", "Cup", "CupCap",
      "Curl", "CurrencyConvert", "CurveClosed", "CycleGraph", "Cycles", "Cyclotomic", "Cylinder",
      "Darker", "Dashing", "DataDistribution", "DataReversed", "Dataset", "DatasetDisplayFormat",
      "DatasetTheme", "DateBounds", "Dated", "DateDifference", "DatedUnit", "DateFormat",
      "DateGranularity", "DateInterval", "DateList", "DateListLogPlot", "DateListPlot",
      "DateListStepPlot", "DateObject", "DateObjectQ", "DateOverlapsQ", "DatePlus", "DateRange",
      "DateSelect", "DateString", "DateValue", "DateWithinQ", "DawsonF", "DayCount",
      "DayHemisphere", "DaylightQ", "DayMatchQ", "DayName", "DayNightTerminator", "DayPlus",
      "DayRange", "DayRound", "DeBruijnSequence", "DecimalForm", "Decrement", "Decompose",
      "DedekindNumber", "Default", "DefaultButton", "DefaultDuration", "DefaultPrintPrecision",
      "DefaultValues", "Defer", "Definition", "Deinitialization", "Del", "DelaunayMesh", "Delete",
      "DeleteBorderComponents", "DeleteCases", "DeleteDuplicates", "DeleteDuplicatesBy",
      "DeleteFile", "DeleteMissing", "DeleteSmallComponents", "Delimiter", "Denominator",
      "DensityHistogram", "DensityPlot", "Deployed", "Depth", "Derivative", "DerivativeFilter",
      "DesignMatrix", "Det", "Diagonal", "DiagonalMatrix", "DiagonalMatrixQ", "DialogInput",
      "DialogNotebook", "DialogReturn", "Diamond", "DiamondMatrix", "DiceDissimilarity",
      "DifferenceDelta", "DifferenceQuotient", "DifferenceRoot", "Differences", "DifferentialD",
      "DigitBlock", "DigitCount", "DigitQ", "DigitSum", "Dilation", "DimensionalCombinations",
      "Dimensions", "DiracDelta", "DirectedEdge", "DirectedGraphQ", "DirectedInfinity", "Direction",
      "DirectionalLight", "Directive", "DirichletBeta", "DirichletEta", "DirichletLambda",
      "DirichletWindow", "DiscreteDelta", "DiscreteLimit", "DiscretePlot", "DiscretePlot3D",
      "DiscreteRatio", "DiscreteShift", "DiscreteUniformDistribution", "Discriminant", "DiskMatrix",
      "DisjointQ", "Disk", "DiskSegment", "Dispatch", "DisplayAllSteps", "DisplayForm",
      "DistanceTransform", "Distribute", "Distributed", "DistributionChart",
      "DistributionParameterQ", "Dithering", "Div", "Divide", "DivideBy", "Dividers", "Divides",
      "DivideSides", "Divisible", "Divisors", "DivisorSigma", "DivisorSum", "DMSList", "DMSString",
      "Do", "Dodecahedron", "Dot", "DotEqual", "DoubleDownArrow", "DoubleLeftArrow",
      "DoubleLeftRightArrow", "DoubleLeftTee", "DoubleLongLeftArrow", "DoubleLongLeftRightArrow",
      "DoubleLongRightArrow", "DoubleRightArrow", "DoubleRightTee", "DoubleUpArrow",
      "DoubleUpDownArrow", "DoubleVerticalBar", "DownArrow", "DownArrowBar", "DownArrowUpArrow",
      "DownLeftRightVector", "DownLeftTeeVector", "DownLeftVector", "DownLeftVectorBar",
      "DownRightTeeVector", "DownRightVector", "DownRightVectorBar", "DownTee", "DownTeeArrow",
      "DownValues", "Drop", "DropShadowing", "DSolve", "DSolveValue", "Dt", "DualPlanarGraph",
      "DuplicateFreeQ", "Dynamic", "DynamicModule", "DynamicWrapper", "EasterSunday", "Echo",
      "EchoFunction", "EclipseType", "EdgeAdd", "EdgeChromaticNumber", "EdgeCount", "EdgeContract",
      "EdgeCoverQ", "EdgeDelete", "EdgeDetect", "EdgeForm", "EdgeLabelStyle", "EdgeList", "EdgeQ",
      "EdgeRules", "EdgeWeight", "EditDistance", "EffectiveInterest", "Eigensystem", "Eigenvalues",
      "EigenvectorCentrality", "Eigenvectors", "Element", "ElementData", "Eliminate", "Ellipsoid",
      "EllipticE", "EllipticExp", "EllipticF", "EllipticK", "EllipticLog", "EllipticPi",
      "EllipticTheta", "Empirical", "EmpiricalDistribution", "EmptyRegion", "Enabled", "End",
      "EndPackage", "EndTestSection", "EngineeringForm", "Entity", "EntityClass", "EntityList",
      "EntityProperty", "EntityValue", "Entropy", "EntropyFilter", "Equal", "EqualTilde", "EqualTo",
      "Equilibrium", "Equivalent", "Erf", "Erfc", "Erfi", "ErlangDistribution", "Erosion",
      "EuclideanDistance", "EulerE", "EulerianGraphQ", "EulerPhi", "Evaluate", "EvaluationMonitor",
      "Evaluator", "EvenQ", "ExactNumberQ", "Except", "ExcludedLines", "Exclusions",
      "ExclusionsStyle", "Exists", "Exit", "Exp", "Expand", "ExpandAll", "ExpandDenominator",
      "ExpandNumerator", "Expectation", "ExpIntegralE", "ExpIntegralEi", "Exponent",
      "ExponentFunction", "ExponentialDistribution", "ExponentialGeneratingFunction",
      "ExponentialPowerDistribution", "ExponentStep", "Export", "ExportForm", "ExportString",
      "ExpressionGraph", "ExpToTrig", "ExtendedGCD", "ExtentElementFunction", "ExtentMarkers",
      "Extract", "FaceForm", "FaceGrids", "FaceGridsStyle", "Factor", "Factorial", "Factorial2",
      "FactorialMomentGeneratingFunction", "FactorList", "FactorialMoment", "FactorialPower",
      "FactorInteger", "FactorSquareFree", "FactorSquareFreeList", "FactorTerms", "FactorTermsList",
      "Failure", "Fibonacci", "FileExistsQ", "FileNameSetter", "FilledCurve", "FilledTorus",
      "FillingTransform", "FindAstroEvent", "FindClique", "FindDistributionParameters", "FindKClan",
      "FindKClique", "FindKClub", "FindKPlex", "FindMaximumFlow", "FindMoleculeSubstructure",
      "FindPlanarColoring", "FindPostmanTour", "FindSolarEclipse", "FindThreshold",
      "FiniteGroupCount", "FiniteAbelianGroupCount", "File", "FileFormat", "FileHash",
      "FileNameDrop", "FileNameJoin", "FileNames", "FileNameTake", "FilePrint", "FilterRules",
      "FindClusters", "FindCycle", "FindEdgeColoring", "FindEdgeCover", "FindEulerianCycle",
      "FindFit", "FindFormula", "FindGeneratingFunction", "FindGraphCommunities",
      "FindGraphIsomorphism", "FindHamiltonianCycle", "FindIndependentEdgeSet",
      "FindIndependentVertexSet", "FindInstance", "FindLinearRecurrence", "FindList", "FindMaximum",
      "FindMinimum", "FindMinimumCostFlow", "FindPermutation", "FindRoot", "FindSequenceFunction",
      "FindShortestCurve", "FindShortestPath", "FindShortestTour", "FindSpanningTree",
      "FindVertexColoring", "FindVertexCover", "First", "FirstCase", "FirstPosition", "Fit",
      "FittedModel", "FiveNum", "FixedPoint", "FixedPointList", "Flatten", "FlattenAt",
      "FlatTopWindow", "Floor", "Fold", "FoldList", "FontSlant", "FontTracking", "FontWeight",
      "For", "ForAll", "FormBox", "FormulaData", "Fourier", "FourierCosTransform", "FourierDCT",
      "FourierDCTMatrix", "FourierDST", "FourierDSTMatrix", "FourierMatrix", "FourierSinTransform",
      "FractionalPart", "FractionBox", "Framed", "FRatioDistribution", "FrechetDistribution",
      "FreeQ", "FresnelC", "FresnelS", "Friday", "FrobeniusNumber", "FrobeniusSolve",
      "FromAbsoluteTime", "FromCharacterCode", "FromContinuedFraction", "FromDataset",
      "FromDateString", "FromDigits", "FromDMS", "FromJulianDate", "FromLetterNumber",
      "FromLunationNumber", "FromPolarCoordinates", "FromRomanNumeral", "FromSphericalCoordinates",
      "FromUnixTime", "FullDefinition", "FullForm", "FullMoon", "FullRegion", "FullSimplify",
      "Function", "FunctionContinuous", "FunctionDiscontinuities", "FunctionDomain",
      "FunctionExpand", "FunctionPeriod", "FunctionRange", "FunctionSingularities", "FunctionURL",
      "Gamma", "GammaDistribution", "GammaRegularized", "GapPenalty", "Gather", "GatherBy",
      "GaussianFilter", "GaussianMatrix", "GaussianWindow", "GCD", "GegenbauerC",
      "GeneratedQuantityMagnitudes", "GeneratingFunction", "GeoBackground", "GeoCenter",
      "GeodesyData", "GeoDistance", "GeoGraphics", "GeoGridLines", "GeometricDistribution",
      "GeometricMean", "GeometricTransformation", "GeoPosition", "GeoProjection", "GeoRange", "Get",
      "GlobalClusteringCoefficient", "Glow", "GoldbachList", "GompertzMakehamDistribution", "Grad",
      "GradientFilter", "GradientOrientationFilter", "Graph", "Graph3D", "GraphCenter",
      "GraphComplement", "GraphData", "GraphDiameter", "GraphDifference", "GraphDisjointUnion",
      "GraphDistance", "GraphEmbedding", "GraphHighlight", "GraphHighlightStyle", "Graphics",
      "GraphicsGrid", "Graphics3D", "Graphics3DJSON", "GraphicsColumn", "GraphicsComplex",
      "GraphicsGroup", "GraphicsRow", "GraphicsJSON", "GraphIntersection", "GraphPeriphery",
      "GraphPlot", "GraphPower", "GraphQ", "GraphRadius", "GraphUnion", "GrayLevel", "Greater",
      "GreaterEqual", "GreaterEqualLess", "GreaterEqualThan", "GreaterFullEqual", "GreaterGreater",
      "GreaterLess", "GreaterSlantEqual", "GreaterThan", "GreaterTilde", "GreenFunction", "Grid",
      "GridGraph", "GroebnerBasis", "GroupBy", "Groupings", "GroupOrbits", "Gudermannian",
      "GumbelDistribution", "HalfLine", "HalfNormalDistribution", "HalfPlane", "HalfSpace",
      "Haloing", "HamiltonianGraphQ", "HammingDistance", "HammingWindow", "HankelH1", "HankelH2",
      "HankelMatrix", "HannWindow", "HarmonicMean", "HarmonicNumber", "Hash", "Haversine",
      "HazardFunction", "Head", "HeaderAlignment", "HeaderBackground", "HeaderDisplayFunction",
      "HeaderLines", "HeaderSize", "HeaderStyle", "HeavisideLambda", "HeavisidePi",
      "HeavisideTheta", "HermiteDecomposition", "HermiteH", "Hermitian", "HermitianMatrixQ",
      "HessenbergDecomposition", "HessianMatrix", "Hexahedron", "HiddenItems", "Highlighted",
      "HilbertMatrix", "Histogram", "HistogramDistribution", "HistogramList", "HistogramTransform",
      "HodgeDual", "Hold", "HoldForm", "HoldPattern", "Horner", "HornerForm", "Hue", "HumpDownHump",
      "HumpEqual", "HurwitzLerchPhi", "HurwitzZeta", "HypercubeGraph", "Hyperfactorial",
      "Hypergeometric0F1", "Hypergeometric0F1Regularized", "Hypergeometric1F1",
      "Hypergeometric1F1Regularized", "Hypergeometric2F1", "Hypergeometric2F1Regularized",
      "HypergeometricDistribution", "HypergeometricPFQ", "HypergeometricPFQRegularized",
      "HypergeometricU", "HyperHarmonicNumber", "HypoexponentialDistribution", "Icosahedron",
      "Identity", "IdentityMatrix", "If", "Im", "Image", "ImageAdd", "ImageAdjust", "ImageApply",
      "ImageAspectRatio", "ImageAssemble", "ImageChannels", "ImageClip", "ImageColorSpace",
      "ImageCompose", "ImageConvolve", "ImageCorners", "ImageCorrelate", "ImageCrop", "ImageData",
      "ImageDeconvolve", "ImageDifference", "ImageDimensions", "ImageDivide", "ImageEffect",
      "ImageFilter", "ImageForwardTransformation", "ImageHistogram", "ImageKeypoints", "ImageLines",
      "ImageMeasurements", "ImageMultiply", "ImagePad", "ImagePartition",
      "ImagePerspectiveTransformation", "ImageQ", "ImageReflect", "ImageResize", "ImageResolution",
      "ImageRotate", "ImageScaled", "ImageScan", "ImageSegmentationComponents", "ImageSize",
      "ImageSizeRaw", "ImageSubtract", "ImageTake", "ImageTransformation", "ImageTrim", "ImageType",
      "ImageValue", "ImageValuePositions", "ImplicitD", "ImplicitRegion", "Implies", "Import",
      "ImportString", "In", "Inactivate", "Inactive", "IncidenceMatrix", "IncludeAromaticBonds",
      "IncludeHydrogens", "IncludeMetaInformation", "IncludeOuterFace", "IncludeQuantities",
      "Increment", "IndependentEdgeSetQ", "IndependentPhysicalQuantity", "IndependentUnit",
      "IndependentUnitDimension", "IndependentVertexSetQ", "Indexed", "IndexGraph", "Inequality",
      "InexactNumberQ", "InfiniteLine", "InfinitePlane", "Infix", "InflationAdjust", "Information",
      "Initialization", "Inner", "Inpaint", "Input", "InputField", "InputForm", "InputStream",
      "InputString", "Insert", "InstallJava", "InstanceOf", "IntegerDigits", "IntegerExponent",
      "IntegerLength", "IntegerName", "IntegerPart", "IntegerPartitions", "IntegerQ", "Integrate",
      "Interleaving", "InterpolatingFunction", "InterpolatingPolynomial", "Interpolation",
      "InterquartileRange", "Interrupt", "IntersectingQ", "Intersection", "Interval",
      "IntervalComplement", "IntervalData", "IntervalIntersection", "IntervalMarkers",
      "IntervalMarkersStyle", "IntervalMemberQ", "IntervalSlider", "IntervalUnion", "Inverse",
      "InverseBetaRegularized", "InverseCDF", "InverseErf", "InverseErfc", "InverseFourier",
      "InverseFunction", "InverseFunctions", "InverseGammaDistribution", "InverseGammaRegularized",
      "InverseGudermannian", "InverseHaversine", "InverseJacobiCD", "InverseJacobiCN",
      "InverseJacobiDC", "InverseJacobiDN", "InverseJacobiNC", "InverseJacobiND", "InverseJacobiSC",
      "InverseJacobiSD", "InverseJacobiSN", "InverseLaplaceTransform", "InverseSeries",
      "InverseSurvivalFunction", "InverseWeierstrassP", "InverseZTransform",
      "InvisiblePostfixScriptBase", "InvisiblePrefixScriptBase", "IrreduciblePolynomialQ",
      "IsomorphicGraphQ", "IsotopeData", "Italic", "Item", "ItemAspectRatio", "ItemDisplayFunction",
      "ItemSize", "ItemStyle", "JaccardDissimilarity", "JacobiAmplitude", "JacobiCD", "JacobiCN",
      "JacobiDC", "JacobiDN", "JacobiEpsilon", "JacobiMatrix", "JacobiNC", "JacobiND", "JacobiP",
      "JacobiSC", "JacobiSD", "JacobiSN", "JacobiSymbol", "JacobiZeta", "JavaClass", "JavaForm",
      "JavaNew", "JavaObject", "JavaObjectQ", "JavaShow", "Join", "JoinAcross", "JoinedCurve",
      "JoinForm", "JordanDecomposition", "JSForm", "JSFormData", "JulianDate", "KagiChart",
      "KaryTree", "KCoreComponents", "KelvinBei", "KelvinBer", "Key", "KeyDrop", "KeyDropFrom",
      "KeyExistsQ", "KeyFreeQ", "KeyComplement", "KeyIntersection", "KeyMap", "KeyMemberQ", "Keys",
      "KeySelect", "KeySort", "KeySortBy", "KeyTake", "KeyUnion", "KeyValueMap", "KeyValuePattern",
      "KirchhoffMatrix", "KleinInvariantJ", "KnownUnitQ", "KolmogorovSmirnovTest",
      "KOrderlessPartitions", "KPartitions", "KroneckerDelta", "KroneckerProduct",
      "KroneckerSymbol", "Kurtosis", "LABColor", "Labeled", "LaguerreL", "LambdaComponents",
      "LambertW", "LaplaceDistribution", "LaplaceTransform", "Laplacian", "LaplacianFilter",
      "LaplacianGaussianFilter", "LaplacianPDETerm", "Last", "LCHColor", "LCM", "LeafCount",
      "LeapYearQ", "LeastSquares", "LeftArrow", "LeftArrowBar", "LeftArrowRightArrow",
      "LeftDownTeeVector", "LeftDownVector", "LeftDownVectorBar", "LeftRightArrow",
      "LeftRightVector", "LeftTee", "LeftTeeArrow", "LeftTeeVector", "LeftTriangle",
      "LeftTriangleBar", "LeftTriangleEqual", "LeftUpDownVector", "LeftUpTeeVector", "LeftUpVector",
      "LeftUpVectorBar", "LeftVector", "LeftVectorBar", "LegendAppearance", "LegendFunction",
      "LegendLabel", "LegendLayout", "LegendMargins", "LegendMarkers", "LegendMarkerSize",
      "LegendreP", "LegendreQ", "Length", "LengthWhile", "LerchPhi", "Less", "LessEqual",
      "LessEqualGreater", "LessEqualThan", "LessFullEqual", "LessGreater", "LessLess",
      "LessSlantEqual", "LessThan", "LessTilde", "LetterCounts", "LetterNumber", "LetterQ", "Level",
      "LevelQ", "LeviCivitaTensor", "Lighter", "LightingAngle", "Limit", "Line", "LinearModelFit",
      "LinearOptimization", "LinearProgramming", "LinearRecurrence", "LinearSolve",
      "LinearSolveFunction", "LineBreakChart", "LineGraph", "LineIntegralConvolutionPlot",
      "LineLegend", "LiouvilleLambda", "List", "ListAnimate", "ListContourPlot", "ListConvolve",
      "ListCorrelate", "ListCurvePathPlot", "ListDensityPlot", "ListLineIntegralConvolutionPlot",
      "ListLinePlot", "ListLinePlot3D", "ListLogLinearPlot", "ListLogLogPlot", "ListLogPlot",
      "ListPlot", "ListPlot3D", "ListPointPlot3D", "ListPolarPlot", "ListQ", "ListStepPlot",
      "ListStreamDensityPlot", "ListStreamPlot", "ListVectorDensityPlot", "ListVectorPlot",
      "Literal", "LLMFunction", "LoadJavaClass", "LocalAdaptiveBinarize",
      "LocalClusteringCoefficient", "LocalizeVariables", "LocalObject", "LocalTime", "Locator",
      "LocatorAutoCreate", "LocatorPane", "Log", "Log10", "Log2", "LogBarnesG", "LogGamma",
      "LogicalExpand", "LogIntegral", "LogisticDistribution", "LogisticSigmoid", "LogLinearPlot",
      "LogLogisticDistribution", "LogLogPlot", "LogNormalDistribution", "LogPlot",
      "LogSeriesDistribution", "Longest", "LongLeftArrow", "LongLeftRightArrow", "LongRightArrow",
      "Lookup", "LowerCaseQ", "LowerLeftArrow", "LowerRightArrow", "LowerTriangularize",
      "LowerTriangularMatrixQ", "LucasL", "LuccioSamiComponents", "LUDecomposition", "LunarEclipse",
      "LunationNumber", "LUVColor", "MachineNumberQ", "Magnification", "MakeBoxes",
      "MangoldtLambda", "ManhattanDistance", "Manipulate", "Manipulator", "MantissaExponent", "Map",
      "MapAll", "MapApply", "MapAt", "MapIndexed", "MapThread", "MarcumQ", "MarginalDistribution",
      "Masking", "MatchingDissimilarity", "MatchQ", "MathMLForm", "MatrixExp", "MatrixForm",
      "MatrixFunction", "MatrixLog", "MatrixMinimalPolynomial", "MatrixPlot", "MatrixPower",
      "MatrixQ", "MatrixRank", "MatrixSymbol", "Max", "MaxDate", "MaxExtraConditions",
      "MaxFeatures", "MaxFilter", "Maximize", "MaximalBy", "MaxItems", "MaxLimit", "MaxMemoryUsed",
      "MaxStableDistribution", "MaxwellDistribution", "Mean", "MeanAround",
      "MeanClusteringCoefficient", "MeanDeviation", "MeanFilter", "MeanShiftFilter", "Median",
      "MedianDeviation", "MedianFilter", "MeijerG", "MeijerGReduce", "MeixnerDistribution",
      "MemberQ", "MemoryAvailable", "MemoryInUse", "MenuView", "Merge", "MergeDifferences",
      "MersennePrimeExponent", "MersennePrimeExponentQ", "MeshCellCount", "MeshCellHighlight",
      "MeshCellLabel", "MeshCellMarker", "MeshCells", "MeshCellShapeFunction", "MeshCellStyle",
      "MeshCoordinates", "MeshFunctions", "MeshPrimitives", "MeshRange", "MeshRegion",
      "MeshRegionQ", "MeshShading", "MeshStyle", "Message", "MessageName", "Messages",
      "MetaInformation", "MidDate", "Min", "MinDate", "MinFilter", "MinimalPolynomial", "MinimalBy",
      "Minimize", "MinLimit", "MinMax", "Minor", "Minors", "MinStableDistribution", "Minus",
      "MinusPlus", "Missing", "MissingBehavior", "MissingQ", "MissingValuePattern",
      "MixedMagnitude", "MixedUnit", "MixtureDistribution", "Mod", "ModularInverse", "Module",
      "MoebiusMu", "Molecule", "MoleculeAlign", "MoleculeAlignment", "MoleculeContainsQ",
      "MoleculeDraw", "MoleculeEquivalentQ", "MoleculeFreeQ", "MoleculeGraph", "MoleculeMatchQ",
      "MoleculeMaximumCommonSubstructure", "MoleculeModify", "MoleculeName", "MoleculePattern",
      "MoleculePlot", "MoleculePlot3D", "MoleculeProperty", "MoleculeQ",
      "MoleculeSubstructureCount", "MoleculeValue", "Moment", "MomentGeneratingFunction",
      "MomentOfInertia", "Monday", "MonomialList", "MoonPhase", "MoonPhaseDate", "MoonPosition",
      "MorphologicalBinarize", "MorphologicalComponents", "MorphologicalPerimeter",
      "MorphologicalTransform", "Most", "Mouseover", "MovingAverage", "MovingMedian",
      "MoyalDistribution", "Multicolumn", "Multinomial", "MultinormalDistribution",
      "MultiplicativeOrder", "MultiplySides", "MultivariatePoissonDistribution",
      "MultivariateTDistribution", "NakagamiDistribution", "NameQ", "Names", "Nand", "NArgMax",
      "NArgMin", "NCache", "ND", "NDSolve", "NDSolveValue", "Nearest", "NearestTo",
      "NeedlemanWunschSimilarity", "Needs", "Negative", "NegativeDefiniteMatrixQ",
      "NegativeSemidefiniteMatrixQ", "NeighborhoodGraph", "Nest", "NestedGreaterGreater",
      "NestedLessLess", "NestList", "NestWhile", "NestWhileList", "NetGraph", "NewMoon",
      "NExpectation", "NextDate", "NextPrime", "NFourierTransform", "NightHemisphere", "NIntegrate",
      "NMaximize", "NMaxValue", "NMinimize", "NMinValue", "NoncentralChiSquareDistribution",
      "NonCommutativeMultiply", "NondimensionalizationTransform", "NoneTrue", "NonNegative",
      "NonPositive", "NonThreadable", "Nor", "Norm", "Normal", "NormalDistribution", "Normalize",
      "NormalMatrixQ", "NormalsFunction", "Not", "NotCongruent", "NotCupCap",
      "NotDoubleVerticalBar", "Notebook", "NotElement", "NotEqualTilde", "NotExists", "NotGreater",
      "NotGreaterEqual", "NotGreaterFullEqual", "NotGreaterGreater", "NotGreaterLess",
      "NotGreaterSlantEqual", "NotGreaterTilde", "NotHumpDownHump", "NotHumpEqual",
      "NotLeftTriangle", "NotLeftTriangleBar", "NotLeftTriangleEqual", "NotLess", "NotLessEqual",
      "NotLessFullEqual", "NotLessGreater", "NotLessLess", "NotLessSlantEqual", "NotLessTilde",
      "NotListQ", "NotNestedGreaterGreater", "NotNestedLessLess", "NotPrecedes", "NotPrecedesEqual",
      "NotPrecedesSlantEqual", "NotPrecedesTilde", "NotReverseElement", "NotRightTriangle",
      "NotRightTriangleBar", "NotRightTriangleEqual", "NotSquareSubset", "NotSquareSubsetEqual",
      "NotSquareSuperset", "NotSquareSupersetEqual", "NotSubset", "NotSubsetEqual", "NotSucceeds",
      "NotSucceedsEqual", "NotSucceedsSlantEqual", "NotSucceedsTilde", "NotSuperset",
      "NotSupersetEqual", "NotTilde", "NotTildeEqual", "NotTildeFullEqual", "NotTildeTilde",
      "NotVerticalBar", "NProbability", "NProduct", "NResidue", "NRoots", "NSolve", "NSolveValues",
      "NSum", "NullSpace", "NumberDigit", "NumberFieldRootsOfUnity", "NumberForm", "NumberFormat",
      "NumberLinePlot", "NumberMultiplier", "NumberPadding", "NumberPoint", "NumberQ",
      "NumberSeparator", "NumberSigns", "Numerator", "NumericalOrder", "NumericalSort",
      "NumericArray", "NumericArrayQ", "NumericArrayType", "NumericQ", "NuttallWindow",
      "Octahedron", "OddQ", "Off", "Offset", "On", "Opacity", "OpenAppend", "Opener", "Opening",
      "OpenRead", "OpenWrite", "Operate", "OptimizeExpression", "Optional", "Options",
      "OptionsPattern", "OptionValue", "Or", "OrbitalElements", "Order", "OrderedQ", "Ordering",
      "Orthogonalize", "OrthogonalMatrixQ", "Out", "Outer", "OutputForm", "OutputStream",
      "Overflow", "Overlay", "Overscript", "OverscriptBox", "OwnValues", "Package", "PaddedForm",
      "Padding", "PadeApproximant", "PadLeft", "PadRight", "PairedBarChart", "PairedHistogram",
      "PairedSmoothHistogram", "Pane", "Panel", "Paneled", "PaneSelector", "ParallelMap",
      "Parallelepiped", "Parallelogram", "ParameterMixtureDistribution", "ParametricPlot",
      "ParametricPlot3D", "ParametricRegion", "Parenthesis", "ParetoDistribution", "Part",
      "PartialD", "Partition", "PartitionsP", "PartitionsQ", "ParzenWindow", "PathGraph",
      "PathGraphQ", "Pattern", "PatternOrder", "PatternTest", "PauliMatrix", "Pause", "PDF",
      "PearsonChiSquareTest", "PearsonCorrelationTest", "PerfectNumber", "PerfectNumberQ",
      "Perimeter", "PeriodicTablePlot", "PeriodogramArray", "Permanent", "PermutationCycles",
      "PermutationCyclesQ", "PermutationList", "PermutationListQ", "PermutationProduct",
      "PermutationReplace", "Permutations", "Permute", "Perpendicular", "PetersenGraph", "Pick",
      "Piecewise", "PiecewiseExpand", "PieChart", "Placed", "Plain", "PlanarAngle",
      "PlanarFaceList", "PlanarGraph", "PlanarGraphQ", "Plot", "Plot3D", "PlotFit",
      "PlotFitElements", "PlotHighlighting", "PlotMarkers", "PlotTheme", "Plus", "PlusMinus",
      "Pochhammer", "Point", "PointFigureChart", "PointLegend", "PointLight", "PointSize",
      "PoissonConsulDistribution", "PoissonDistribution", "PoissonProcess", "PolarAxes",
      "PolarGridLines", "PolarPlot", "PolarTicks", "PolyGamma", "Polygon", "PolygonalNumber",
      "PolygonAngle", "PolygonCoordinates", "Polyhedron", "PolyLog", "PolynomialExtendedGCD",
      "PolynomialGCD", "PolynomialLCM", "PolynomialMod", "PolynomialQ", "PolynomialQuotient",
      "PolynomialQuotientRemainder", "PolynomialReduce", "PolynomialRemainder", "PopupMenu",
      "Position", "PositionIndex", "Positive", "PositiveDefiniteMatrixQ",
      "PositiveSemidefiniteMatrixQ", "PossibleZeroQ", "Postfix", "Power", "PowerExpand", "PowerMod",
      "PowerRange", "PowersRepresentations", "PrecedenceForm", "Precedes", "PrecedesEqual",
      "PrecedesSlantEqual", "PrecedesTilde", "Precision", "PreDecrement", "Prefix", "PreIncrement",
      "Prepend", "PrependTo", "PreviousDate", "Prime", "PrimeNu", "PrimeOmega", "PrimePi",
      "PrimePowerQ", "PrimeQ", "PrimeZetaP", "PrimitivePolynomialQ", "PrimitiveRoot",
      "PrimitiveRootList", "PrincipalComponents", "Print", "PrintableASCIIQ", "Prism",
      "Probability", "ProbabilityDistribution", "ProbabilityPlot", "ProbabilityScalePlot",
      "Product", "ProductDistribution", "ProductLog", "ProgressIndicator", "Projection",
      "Proportion", "Proportional", "Protect", "ProteinData", "Pruning", "PseudoInverse", "Put",
      "PutAppend", "Pyramid", "QPochhammer", "QRDecomposition", "QuadraticIrrationalQ", "Quantile",
      "QuantilePlot", "Quantity", "QuantityArray", "QuantityDistribution", "QuantityForm",
      "QuantityMagnitude", "QuantityQ", "QuantityUnit", "QuantityVariable",
      "QuantityVariableCanonicalUnit", "QuantityVariableDimensions", "QuantityVariableIdentifier",
      "QuantityVariablePhysicalQuantity", "Quartics", "QuarticSolve", "Quartiles", "Query", "Quiet",
      "Quit", "Quotient", "QuotientRemainder", "RadicalBox", "RadioButton", "RadioButtonBar",
      "Radius", "Ramp", "RamseyNumber", "Random", "RandomChoice", "RandomComplex", "RandomGraph",
      "RandomInteger", "RandomPermutation", "RandomPrime", "RandomReal", "RandomSample",
      "RandomVariate", "Range", "RangeFilter", "RangeSpace", "RankDecomposition", "RankedMax",
      "RankedMin", "Raster", "Raster3D", "Rasterize", "RasterSize", "Rational", "Rationalize",
      "Ratios", "RawBackquote", "RawBoxes", "Re", "ReactionBalance", "ReactionBalancedQ", "Read",
      "ReadLine", "ReadList", "ReadString", "RealAbs", "RealDigits", "RealSign",
      "RealValuedNumberQ", "RealValuedNumericQ", "Reap", "Rectangle", "RectangleChart", "Reduce",
      "ReferenceAltitude", "Refine", "ReflectionTransform", "Refresh", "RefreshRate", "Region",
      "RegionBoundary", "RegionBoundaryStyle", "RegionBounds", "RegionCentroid", "RegionDifference",
      "RegionDimension", "RegionDistance", "RegionEmbeddingDimension", "RegionEqual",
      "RegionFunction", "RegionIntersection", "RegionMeasure", "RegionMember",
      "RegionMemberFunction", "RegionMoment", "RegionNearest", "RegionNearestFunction",
      "RegionPlot", "RegionQ", "RegionSymmetricDifference", "RegionUnion", "RegionWithin",
      "RegularExpression", "RegularPolygon", "ReIm", "ReleaseHold", "ReliefImage", "ReliefPlot",
      "Remove", "RemoveAlphaChannel", "RemoveBackground", "RemoveDiacritics", "RenkoChart",
      "Repeated", "RepeatedNull", "RepeatedTiming", "Replace", "ReplaceAll", "ReplaceAt",
      "ReplaceList", "ReplacePart", "ReplaceRepeated", "Resampling", "Rescale", "Residue",
      "Resolve", "ResourceData", "Rest", "Resultant", "Return", "Reverse", "ReverseElement",
      "ReverseEquilibrium", "ReverseSort", "ReverseUpEquilibrium", "RevolutionAxis",
      "RevolutionPlot3D", "RGBColor", "RiccatiSolve", "RiceDistribution", "RidgeFilter",
      "RiemannSiegelTheta", "Riffle", "RightArrow", "RightArrowBar", "RightArrowLeftArrow",
      "RightComposition", "RightDownTeeVector", "RightDownVector", "RightDownVectorBar", "RightTee",
      "RightTeeArrow", "RightTeeVector", "RightTriangle", "RightTriangleBar", "RightTriangleEqual",
      "RightUpDownVector", "RightUpTeeVector", "RightUpVector", "RightUpVectorBar", "RightVector",
      "RightVectorBar", "RogersTanimotoDissimilarity", "RomanNumeral", "Root", "RootIntervals",
      "RootMeanSquare", "RootOf", "RootReduce", "Roots", "RootSum", "Rotate", "RotateLeft",
      "RotateRight", "RotationAction", "RotationMatrix", "RotationTransform", "Round",
      "RoundImplies", "Row", "RowBox", "RowReduce", "RSolve", "RSolveValue", "Rule", "RuleDelayed",
      "RussellRaoDissimilarity", "SameObjectQ", "SameQ", "SASTriangle", "SatisfiabilityCount",
      "SatisfiabilityInstances", "SatisfiableQ", "Saturday", "Save", "SaveDefinitions",
      "SawtoothWave", "Scale", "Scaled", "ScalingTransform", "Scan", "SchurDecomposition",
      "ScientificForm", "ScientificNotationThreshold", "Sec", "Sech", "SechDistribution",
      "SectorChart", "SectorOrigin", "SectorSpacing", "SeedRandom", "Segmented", "Select",
      "SelectComponents", "SelectFirst", "SemanticImport", "SemanticImportString",
      "SeparateBoundaries", "Sequence", "SequenceAlignment", "SequenceCount", "SequenceCases",
      "SequencePosition", "SequenceReplace", "SequenceSplit", "Series", "SeriesCoefficient",
      "SeriesData", "Set", "SetAlphaChannel", "SetAttributes", "SetDelayed", "Setter", "SetterBar",
      "Share", "Sharpen", "Sharpening", "ShearingTransform", "ShiftRegisterSequence", "Short",
      "ShortDownArrow", "Shortest", "ShortestCurveDistance", "ShortLeftArrow", "ShortRightArrow",
      "ShortUpArrow", "Show", "ShrinkingDelay", "SiderealTime", "Sign", "Signature", "SignCmp",
      "SignedRegionDistance", "SignPadding", "SimilarityRules", "Simplex", "Simplify", "Sin",
      "Sinc", "SinghMaddalaDistribution", "SingularValueDecomposition", "SingularValueList", "Sinh",
      "SinhIntegral", "SinIntegral", "SixJSymbol", "Skeleton", "SkeletonTransform", "Skewness",
      "Slider", "Slider2D", "Slot", "SlotNumber", "SlotSequence", "SlotSequenceNumber",
      "SmallCircle", "SmithDecomposition", "SmithWatermanSimilarity", "SmoothDensityHistogram",
      "SmoothHistogram", "SokalSneathDissimilarity", "SolarEclipse", "SolarTime", "Solve",
      "SolveAlways", "SolveValues", "Sort", "SortBy", "Sow", "Spacer", "Spacings", "Span",
      "SparseArray", "SparseArrayQ", "SpearmanRho", "SpecialsFreeQ", "Specularity",
      "SpectrogramArray", "Sphere", "SphericalBesselJ", "SphericalBesselY", "SphericalHankelH1",
      "SphericalHankelH2", "SphericalHarmonicY", "SphericalPlot3D", "SphericalRegion",
      "SphericalShell", "Splice", "Split", "SplitBy", "SpotLight", "Sqrt", "SqrtBox", "Square",
      "SquaredEuclideanDistance", "SquareFreeQ", "SquareIntersection", "SquareMatrixQ", "SquaresR",
      "SquareSubset", "SquareSubsetEqual", "SquareSuperset", "SquareSupersetEqual", "SquareUnion",
      "SquareWave", "SSSTriangle", "Stack", "StackBegin", "StackedDateListPlot", "StackedListPlot",
      "StadiumShape", "StandardDeviation", "StandardDeviationFilter", "StandardForm", "Standardize",
      "Standardized", "Star", "StarData", "StarGraph", "StatusArea", "StieltjesGamma", "StirlingS1",
      "StirlingS2", "StreamColorFunction", "StreamColorFunctionScaling", "StreamDensityPlot",
      "StreamPlot", "StreamPoints", "StreamScale", "StreamStyle", "StringCases", "StringContainsQ",
      "StringCount", "StringDrop", "StringExpression", "StringForm", "StringFormat", "StringFreeQ",
      "StringInsert", "StringJoin", "StringLength", "StringMatchQ", "StringPart", "StringPosition",
      "StringQ", "StringRepeat", "StringReplace", "StringReverse", "StringRiffle", "StringSplit",
      "StringStartsQ", "StringTake", "StringTemplate", "StringToByteArray", "StringToStream",
      "StringTrim", "Structure", "StruveH", "StruveL", "StudentTDistribution", "Style", "StyleForm",
      "Subdivide", "Subfactorial", "Subgraph", "Subresultants", "Subscript", "SubscriptBox",
      "Subsequences", "Subset", "SubsetCases", "SubsetCount", "SubsetEqual", "SubsetPosition",
      "SubsetQ", "SubsetReplace", "Subsets", "Subsuperscript", "SubsuperscriptBox", "Subtract",
      "SubtractFrom", "SubtractSides", "Succeeds", "SucceedsEqual", "SucceedsSlantEqual",
      "SucceedsTilde", "SuchThat", "SudokuSolve", "Sum", "Summary", "Sunday", "SunPosition",
      "Sunrise", "Sunset", "SuperDagger", "Superscript", "SuperscriptBox", "Superset",
      "SupersetEqual", "Surd", "SurfaceArea", "SurfaceGraphics", "SurvivalFunction",
      "SuzukiDistribution", "SwatchLegend", "Switch", "SymbolicDeltaProductArray",
      "SymbolicIdentityArray", "SymbolicOnesArray", "SymbolicZerosArray", "SymbolName", "SymbolQ",
      "Symmetric", "SymmetricMatrixQ", "SymmetricPolynomial", "SymmetricReduction", "Symmetrize",
      "SynchronousInitialization", "SynchronousUpdating", "SyntaxLength", "SyntaxQ",
      "SystemDialogInput", "Table", "TableForm", "TableView", "TabView", "TagSet", "TagSetDelayed",
      "TagUnset", "Take", "TakeLargest", "TakeLargestBy", "TakeList", "TakeSmallest",
      "TakeSmallestBy", "TakeWhile", "Tally", "Tan", "Tanh", "TargetUnits", "TautologyQ", "Taylor",
      "TemplateApply", "TemplateExpression", "TemplateIf", "TemplateSlot", "TensorContract",
      "TensorDimensions", "TensorProduct", "TensorRank", "TensorSymmetry", "TensorTranspose",
      "TensorWedge", "TestReport", "TestReportObject", "TestResultObject", "Tetrahedron", "TeXForm",
      "Text", "TextCell", "TextElement", "TextString", "TextStructure", "Texture",
      "TextureCoordinateFunction", "TextureCoordinateScaling", "Therefore", "Thickness", "Thinning",
      "Thread", "ThreeJSymbol", "Through", "Throw", "Thumbnail", "Thursday", "Tilde", "TildeEqual",
      "TildeFullEqual", "TildeTilde", "TimeConstrained", "TimeDirection", "TimelinePlot",
      "TimeObject", "TimeRemaining", "Times", "TimesBy", "TimeSystem", "TimeSystemConvert",
      "TimeValue", "TimeZone", "TimeZoneConvert", "TimeZoneOffset", "Timing", "ToBoxes",
      "ToCharacterCode", "ToDataset", "ToeplitzMatrix", "ToExpression", "Together", "Toggler",
      "TogglerBar", "ToIntervalData", "ToLowerCase", "Tooltip", "TopHatTransform",
      "ToPolarCoordinates", "TopologicalSort", "ToRadicals", "TortoiseShellBracket", "Torus",
      "TorusGraph", "ToSphericalCoordinates", "ToString", "Total", "TotalVariationFilter",
      "TouchscreenAutoZoom", "ToUnicode", "ToUpperCase", "Tr", "Trace", "TraceForm",
      "TrackedSymbols", "TradingChart", "TraditionalForm", "TransformationClass",
      "TransformationFunction", "TransformedDistribution", "TransformedRegion", "TransitiveClosure",
      "Translate", "TranslationTransform", "Transliterate", "Transpose", "TreeForm", "TreeGraph",
      "TreeGraphQ", "TreePlot", "Triangle", "TriangleCenter", "TriangleConstruct",
      "TriangleMeasurement", "TriangleWave", "TriangularDistribution", "TrigExpand", "TrigFactor",
      "Trigger", "TrigReduce", "TrigSimplifyFu", "TrigToExp", "TrueQ", "TruncatedDistribution",
      "TTest", "Tube", "Tuesday", "TukeyWindow", "Tuples", "TwoWayRule", "UnaryMinusPlus",
      "UnaryPlus", "UnaryPlusMinus", "Uncompress", "Undefined", "Underflow", "Underlined",
      "Underoverscript", "UnderoverscriptBox", "Underscript", "UnderscriptBox", "UndirectedEdge",
      "Unequal", "UnequalTo", "Unevaluated", "UniformDistribution", "UniformSumDistribution",
      "Union", "UnionPlus", "Unique", "UnitaryMatrixQ", "UnitBox", "UnitConvert", "UnitDimensions",
      "Unitize", "UnitSimplify", "UnitStep", "UnitSystem", "UnitTriangle", "UnitVector",
      "UnityDimensions", "UnixTime", "Unprotect", "UnsameQ", "UnsavedVariables", "Unset",
      "UntrackedVariables", "UpArrow", "UpArrowBar", "UpArrowDownArrow", "UpdateInterval",
      "UpDownArrow", "UpEquilibrium", "UpperCaseQ", "UpperLeftArrow", "UpperRightArrow",
      "UpperTriangularize", "UpperTriangularMatrixQ", "UpSet", "UpSetDelayed", "UpTee",
      "UpTeeArrow", "UpTo", "UpValues", "URLDecode", "URLEncode", "URLFetch",
      "ValenceErrorHandling", "ValueQ", "Values", "VandermondeMatrix", "Variables", "Variance",
      "VectorAngle", "VectorAround", "VectorAspectRatio", "VectorColorFunction",
      "VectorColorFunctionScaling", "VectorDensityPlot", "VectorGreater", "VectorGreaterEqual",
      "VectorLess", "VectorLessEqual", "VectorMarkers", "VectorPlot", "VectorPoints", "VectorQ",
      "VectorScale", "VectorSizes", "VectorStyle", "VectorSymbol", "Vee", "Verbatim",
      "VerificationTest", "VerifySolutions", "VertexAdd", "VertexChromaticNumber", "VertexColors",
      "VertexContract", "VertexCoverQ", "VertexDelete", "VertexCoordinates", "VertexCount",
      "VertexDegree", "VertexEccentricity", "VertexInDegree", "VertexLabelStyle", "VertexOutDegree",
      "VertexList", "VertexNormals", "VertexQ", "VertexShape", "VertexTextureCoordinates",
      "VertexWeight", "VerticalBar", "VerticalSeparator", "VerticalSlider", "VerticalTilde",
      "ViewAngle", "ViewCenter", "ViewMatrix", "ViewProjection", "ViewRange", "ViewVector",
      "ViewVertical", "Volume", "VonMisesDistribution", "VoronoiMesh", "WaringYuleDistribution",
      "WatershedComponents", "WeaklyConnectedGraphQ", "WeberE", "WebImageSearch", "WebSearch",
      "Wedge", "Wednesday", "Weekend", "WeibullDistribution", "WeierstrassHalfPeriods",
      "WeierstrassInvariants", "WeierstrassP", "WeierstrassPPrime", "WeightedAdjacencyMatrix",
      "WeightedData", "WeightedGraphQ", "WheelGraph", "Which", "While", "WhiteCornerBracket",
      "WhittakerM", "WhittakerW", "WienerFilter", "WignerSemicircleDistribution", "With",
      "WordBoundary", "WordCloud", "WordOrientation", "WordSelectionFunction", "WordSpacings",
      "Write", "WriteString", "Wronskian", "Xnor", "Xor", "XYZColor", "Yesterday",
      "YuleDissimilarity", "ZernikeR", "Zeta", "ZetaZero", "ZipfDistribution", "ZTransform"};

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
      // The parser writes a precision suffix into the node string - 1.2`30, or 1.5E3`20 once an
      // exponent has been folded in. Neither Double#parseDouble nor Apfloat accepts the backtick,
      // so it has to come off here, and the number keeps the precision it was written with rather
      // than whatever the engine happens to be set to.
      long numberPrecision = -1;
      int tickIndex = nStr.indexOf('`');
      if (tickIndex > 0) {
        String precisionPart = nStr.substring(tickIndex + 1);
        nStr = nStr.substring(0, tickIndex);
        if (precisionPart.length() > 0) {
          try {
            numberPrecision = (long) Double.parseDouble(precisionPart);
          } catch (NumberFormatException nfe) {
            numberPrecision = -1;
          }
        }
      }
      String floatStr = nStr;
      int index = nStr.indexOf("*^");
      int exponent = 1;
      if (index > 0) {
        floatStr = nStr.substring(0, index);
        exponent = Integer.parseInt(nStr.substring(index + 2));
      }
      if (numberPrecision > 0 || EvalEngine.isApfloat(fPrecision)) {
        Apfloat apfloatValue =
            new Apfloat(floatStr, numberPrecision > 0 ? numberPrecision : fPrecision);
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
