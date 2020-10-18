package org.matheclipse.core.expression;

import java.util.Map;

import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.trie.Tries;

import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;

/**
 * Factory for creating Symja built-in symbols (interface {@link IBuiltInSymbol}). The built-in symbols are generated
 * with the tools class <code>BuiltinGenerator</code>.
 */
public class S {

	final private static IBuiltInSymbol[] BUILT_IN_SYMBOLS = new IBuiltInSymbol[ID.Zeta + 10];

	/** package private */
	final static short EXPRID_MAX_BUILTIN_LENGTH = (short) (BUILT_IN_SYMBOLS.length + 1);

	/** package private */
	static IExpr[] COMMON_IDS = null;

	/**
	 * Global map of predefined constant expressions.
	 */
	public final static Object2ShortOpenHashMap<IExpr> GLOBAL_IDS_MAP = new Object2ShortOpenHashMap<IExpr>(
			EXPRID_MAX_BUILTIN_LENGTH + 1000);

	public final static Map<String, ISymbol> HIDDEN_SYMBOLS_MAP = Tries.forStrings();

	public static IBuiltInSymbol symbol(int id) {
		return S.BUILT_IN_SYMBOLS[id];
	}

	/***/
	public final static IBuiltInSymbol $Aborted = F.initFinalSymbol("$Aborted", ID.$Aborted);

	/***/
	public final static IBuiltInSymbol $Assumptions = F.initFinalSymbol("$Assumptions", ID.$Assumptions);

	/***/
	public final static IBuiltInSymbol $Cancel = F.initFinalSymbol("$Cancel", ID.$Cancel);

	/***/
	public final static IBuiltInSymbol $Context = F.initFinalSymbol("$Context", ID.$Context);

	/***/
	public final static IBuiltInSymbol $ContextPath = F.initFinalSymbol("$ContextPath", ID.$ContextPath);

	/***/
	public final static IBuiltInSymbol $CreationDate = F.initFinalSymbol("$CreationDate", ID.$CreationDate);

	/***/
	public final static IBuiltInSymbol $DisplayFunction = F.initFinalSymbol("$DisplayFunction", ID.$DisplayFunction);

	/***/
	public final static IBuiltInSymbol $Failed = F.initFinalSymbol("$Failed", ID.$Failed);

	/***/
	public final static IBuiltInSymbol $HistoryLength = F.initFinalSymbol("$HistoryLength", ID.$HistoryLength);

	/***/
	public final static IBuiltInSymbol $HomeDirectory = F.initFinalSymbol("$HomeDirectory", ID.$HomeDirectory);

	/***/
	public final static IBuiltInSymbol $IterationLimit = F.initFinalSymbol("$IterationLimit", ID.$IterationLimit);

	/***/
	public final static IBuiltInSymbol $Line = F.initFinalSymbol("$Line", ID.$Line);

	/***/
	public final static IBuiltInSymbol $MachineEpsilon = F.initFinalSymbol("$MachineEpsilon", ID.$MachineEpsilon);

	/***/
	public final static IBuiltInSymbol $MachinePrecision = F.initFinalSymbol("$MachinePrecision", ID.$MachinePrecision);

	/***/
	public final static IBuiltInSymbol $MaxMachineNumber = F.initFinalSymbol("$MaxMachineNumber", ID.$MaxMachineNumber);

	/***/
	public final static IBuiltInSymbol $MessageList = F.initFinalSymbol("$MessageList", ID.$MessageList);

	/***/
	public final static IBuiltInSymbol $MinMachineNumber = F.initFinalSymbol("$MinMachineNumber", ID.$MinMachineNumber);

	/***/
	public final static IBuiltInSymbol $OutputSizeLimit = F.initFinalSymbol("$OutputSizeLimit", ID.$OutputSizeLimit);

	/***/
	public final static IBuiltInSymbol $Path = F.initFinalSymbol("$Path", ID.$Path);

	/***/
	public final static IBuiltInSymbol $PathnameSeparator = F.initFinalSymbol("$PathnameSeparator",
			ID.$PathnameSeparator);

	/***/
	public final static IBuiltInSymbol $PrePrint = F.initFinalSymbol("$PrePrint", ID.$PrePrint);

	/***/
	public final static IBuiltInSymbol $PreRead = F.initFinalSymbol("$PreRead", ID.$PreRead);

	/***/
	public final static IBuiltInSymbol $RecursionLimit = F.initFinalSymbol("$RecursionLimit", ID.$RecursionLimit);

	/***/
	public final static IBuiltInSymbol $RootDirectory = F.initFinalSymbol("$RootDirectory", ID.$RootDirectory);

	/***/
	public final static IBuiltInSymbol $TemporaryDirectory = F.initFinalSymbol("$TemporaryDirectory",
			ID.$TemporaryDirectory);

	/***/
	public final static IBuiltInSymbol $UserName = F.initFinalSymbol("$UserName", ID.$UserName);

	/***/
	public final static IBuiltInSymbol $Version = F.initFinalSymbol("$Version", ID.$Version);

	/** Abort() - aborts an evaluation completely and returns `$Aborted`. */
	public final static IBuiltInSymbol Abort = F.initFinalSymbol("Abort", ID.Abort);

	/** Abs(expr) - returns the absolute value of the real or complex number `expr`. */
	public final static IBuiltInSymbol Abs = F.initFinalSymbol("Abs", ID.Abs);

	/** AbsArg(expr) - returns a list of 2 values of the complex number `Abs(expr), Arg(expr)`. */
	public final static IBuiltInSymbol AbsArg = F.initFinalSymbol("AbsArg", ID.AbsArg);

	/***/
	public final static IBuiltInSymbol AbsoluteCorrelation = F.initFinalSymbol("AbsoluteCorrelation",
			ID.AbsoluteCorrelation);

	/***/
	public final static IBuiltInSymbol AbsoluteTime = F.initFinalSymbol("AbsoluteTime", ID.AbsoluteTime);

	/***/
	public final static IBuiltInSymbol AbsoluteTiming = F.initFinalSymbol("AbsoluteTiming", ID.AbsoluteTiming);

	/** Accumulate(list) - accumulate the values of `list` returning a new list. */
	public final static IBuiltInSymbol Accumulate = F.initFinalSymbol("Accumulate", ID.Accumulate);

	/** AddTo(x, dx) - is equivalent to `x = x + dx`. */
	public final static IBuiltInSymbol AddTo = F.initFinalSymbol("AddTo", ID.AddTo);

	/** AdjacencyMatrix(graph) - convert the `graph` into a adjacency matrix. */
	public final static IBuiltInSymbol AdjacencyMatrix = F.initFinalSymbol("AdjacencyMatrix", ID.AdjacencyMatrix);

	/** AiryAi(z) - returns the Airy function of the first kind of `z`. */
	public final static IBuiltInSymbol AiryAi = F.initFinalSymbol("AiryAi", ID.AiryAi);

	/** AiryAiPrime(z) - returns the derivative of the `AiryAi` function. */
	public final static IBuiltInSymbol AiryAiPrime = F.initFinalSymbol("AiryAiPrime", ID.AiryAiPrime);

	/** AiryBi(z) - returns the Airy function of the second kind of `z`. */
	public final static IBuiltInSymbol AiryBi = F.initFinalSymbol("AiryBi", ID.AiryBi);

	/** AiryBiPrime(z) - returns the derivative of the `AiryBi` function. */
	public final static IBuiltInSymbol AiryBiPrime = F.initFinalSymbol("AiryBiPrime", ID.AiryBiPrime);

	/***/
	public final static IBuiltInSymbol AlgebraicNumber = F.initFinalSymbol("AlgebraicNumber", ID.AlgebraicNumber);

	/***/
	public final static IBuiltInSymbol Algebraics = F.initFinalSymbol("Algebraics", ID.Algebraics);

	/** All - is a possible value for `Span` and `Quiet`. */
	public final static IBuiltInSymbol All = F.initFinalSymbol("All", ID.All);

	/**
	 * AllTrue({expr1, expr2, ...}, test) - returns `True` if all applications of `test` to `expr1, expr2, ...` evaluate
	 * to `True`.
	 */
	public final static IBuiltInSymbol AllTrue = F.initFinalSymbol("AllTrue", ID.AllTrue);

	/** Alternatives(p1, p2, ..., p_i) - is a pattern that matches any of the patterns `p1, p2,...., p_i`. */
	public final static IBuiltInSymbol Alternatives = F.initFinalSymbol("Alternatives", ID.Alternatives);

	/**
	 * And(expr1, expr2, ...) - `expr1 && expr2 && ...` evaluates each expression in turn, returning `False` as soon as
	 * an expression evaluates to `False`. If all expressions evaluate to `True`, `And` returns `True`.
	 */
	public final static IBuiltInSymbol And = F.initFinalSymbol("And", ID.And);

	/** AngleVector(phi) - returns the point at angle `phi` on the unit circle. */
	public final static IBuiltInSymbol AngleVector = F.initFinalSymbol("AngleVector", ID.AngleVector);

	/** Annuity(p, t) - returns an annuity object. */
	public final static IBuiltInSymbol Annuity = F.initFinalSymbol("Annuity", ID.Annuity);

	/** AnnuityDue(p, t) - returns an annuity due object. */
	public final static IBuiltInSymbol AnnuityDue = F.initFinalSymbol("AnnuityDue", ID.AnnuityDue);

	/***/
	public final static IBuiltInSymbol AntiSymmetric = F.initFinalSymbol("AntiSymmetric", ID.AntiSymmetric);

	/** AntihermitianMatrixQ(m) - returns `True` if `m` is a anti hermitian matrix. */
	public final static IBuiltInSymbol AntihermitianMatrixQ = F.initFinalSymbol("AntihermitianMatrixQ",
			ID.AntihermitianMatrixQ);

	/** AntisymmetricMatrixQ(m) - returns `True` if `m` is a anti symmetric matrix. */
	public final static IBuiltInSymbol AntisymmetricMatrixQ = F.initFinalSymbol("AntisymmetricMatrixQ",
			ID.AntisymmetricMatrixQ);

	/**
	 * AnyTrue({expr1, expr2, ...}, test) - returns `True` if any application of `test` to `expr1, expr2, ...` evaluates
	 * to `True`.
	 */
	public final static IBuiltInSymbol AnyTrue = F.initFinalSymbol("AnyTrue", ID.AnyTrue);

	/** Apart(expr) - rewrites `expr` as a sum of individual fractions. */
	public final static IBuiltInSymbol Apart = F.initFinalSymbol("Apart", ID.Apart);

	/***/
	public final static IBuiltInSymbol AppellF1 = F.initFinalSymbol("AppellF1", ID.AppellF1);

	/** Append(expr, item) - returns `expr` with `item` appended to its leaves. */
	public final static IBuiltInSymbol Append = F.initFinalSymbol("Append", ID.Append);

	/** AppendTo(s, item) - append `item` to value of `s` and sets `s` to the result. */
	public final static IBuiltInSymbol AppendTo = F.initFinalSymbol("AppendTo", ID.AppendTo);

	/** f @ expr - returns `f(expr)` */
	public final static IBuiltInSymbol Apply = F.initFinalSymbol("Apply", ID.Apply);

	/** ArcCos(expr) - returns the arc cosine (inverse cosine) of `expr` (measured in radians). */
	public final static IBuiltInSymbol ArcCos = F.initFinalSymbol("ArcCos", ID.ArcCos);

	/** ArcCosh(z) - returns the inverse hyperbolic cosine of `z`. */
	public final static IBuiltInSymbol ArcCosh = F.initFinalSymbol("ArcCosh", ID.ArcCosh);

	/** ArcCot(z) - returns the inverse cotangent of `z`. */
	public final static IBuiltInSymbol ArcCot = F.initFinalSymbol("ArcCot", ID.ArcCot);

	/** ArcCoth(z) - returns the inverse hyperbolic cotangent of `z`. */
	public final static IBuiltInSymbol ArcCoth = F.initFinalSymbol("ArcCoth", ID.ArcCoth);

	/** ArcCsc(z) - returns the inverse cosecant of `z`. */
	public final static IBuiltInSymbol ArcCsc = F.initFinalSymbol("ArcCsc", ID.ArcCsc);

	/** ArcCsch(z) - returns the inverse hyperbolic cosecant of `z`. */
	public final static IBuiltInSymbol ArcCsch = F.initFinalSymbol("ArcCsch", ID.ArcCsch);

	/** ArcSec(z) - returns the inverse secant of `z`. */
	public final static IBuiltInSymbol ArcSec = F.initFinalSymbol("ArcSec", ID.ArcSec);

	/** ArcSech(z) - returns the inverse hyperbolic secant of `z`. */
	public final static IBuiltInSymbol ArcSech = F.initFinalSymbol("ArcSech", ID.ArcSech);

	/** ArcSin(expr) - returns the arc sine (inverse sine) of `expr` (measured in radians). */
	public final static IBuiltInSymbol ArcSin = F.initFinalSymbol("ArcSin", ID.ArcSin);

	/** ArcSinh(z) - returns the inverse hyperbolic sine of `z`. */
	public final static IBuiltInSymbol ArcSinh = F.initFinalSymbol("ArcSinh", ID.ArcSinh);

	/** ArcTan(expr) - returns the arc tangent (inverse tangent) of `expr` (measured in radians). */
	public final static IBuiltInSymbol ArcTan = F.initFinalSymbol("ArcTan", ID.ArcTan);

	/** ArcTanh(z) - returns the inverse hyperbolic tangent of `z`. */
	public final static IBuiltInSymbol ArcTanh = F.initFinalSymbol("ArcTanh", ID.ArcTanh);

	/** Arg(expr) - returns the argument of the complex number `expr`. */
	public final static IBuiltInSymbol Arg = F.initFinalSymbol("Arg", ID.Arg);

	/** ArgMax(function, variable) - returns a maximizer point for a univariate `function`. */
	public final static IBuiltInSymbol ArgMax = F.initFinalSymbol("ArgMax", ID.ArgMax);

	/** ArgMin(function, variable) - returns a minimizer point for a univariate `function`. */
	public final static IBuiltInSymbol ArgMin = F.initFinalSymbol("ArgMin", ID.ArgMin);

	/** ArithmeticGeometricMean({a, b, c,...}) - returns the arithmetic geometric mean of `{a, b, c,...}`. */
	public final static IBuiltInSymbol ArithmeticGeometricMean = F.initFinalSymbol("ArithmeticGeometricMean",
			ID.ArithmeticGeometricMean);

	/** Array(f, n) - returns the `n`-element list `{f(1), ..., f(n)}`. */
	public final static IBuiltInSymbol Array = F.initFinalSymbol("Array", ID.Array);

	/** ArrayDepth(a) - returns the depth of the non-ragged array `a`, defined as `Length(Dimensions(a))`. */
	public final static IBuiltInSymbol ArrayDepth = F.initFinalSymbol("ArrayDepth", ID.ArrayDepth);

	/** ArrayPad(list, n) - adds `n` times `0` on the left and right of the `list`. */
	public final static IBuiltInSymbol ArrayPad = F.initFinalSymbol("ArrayPad", ID.ArrayPad);

	/** ArrayQ(expr) - tests whether expr is a full array. */
	public final static IBuiltInSymbol ArrayQ = F.initFinalSymbol("ArrayQ", ID.ArrayQ);

	/**
	 * ArrayReshape(list-of-values, list-of-dimension) - returns the `list-of-values` elements reshaped as nested list
	 * with dimensions according to the `list-of-dimension`.
	 */
	public final static IBuiltInSymbol ArrayReshape = F.initFinalSymbol("ArrayReshape", ID.ArrayReshape);

	/***/
	public final static IBuiltInSymbol ArrayRules = F.initFinalSymbol("ArrayRules", ID.ArrayRules);

	/***/
	public final static IBuiltInSymbol Arrays = F.initFinalSymbol("Arrays", ID.Arrays);

	/***/
	public final static IBuiltInSymbol AssociateTo = F.initFinalSymbol("AssociateTo", ID.AssociateTo);

	/** Association(list-of-rules) - create a `key->value` association map from the `list-of-rules`. */
	public final static IBuiltInSymbol Association = F.initFinalSymbol("Association", ID.Association);

	/***/
	public final static IBuiltInSymbol AssociationMap = F.initFinalSymbol("AssociationMap", ID.AssociationMap);

	/** AssociationQ(expr) - returns `True` if `expr` is an association, and `False` otherwise. */
	public final static IBuiltInSymbol AssociationQ = F.initFinalSymbol("AssociationQ", ID.AssociationQ);

	/***/
	public final static IBuiltInSymbol AssociationThread = F.initFinalSymbol("AssociationThread", ID.AssociationThread);

	/***/
	public final static IBuiltInSymbol Assumptions = F.initFinalSymbol("Assumptions", ID.Assumptions);

	/**
	 * AtomQ(x) - is true if `x` is an atom (an object such as a number or string, which cannot be divided into
	 * subexpressions using 'Part').
	 */
	public final static IBuiltInSymbol AtomQ = F.initFinalSymbol("AtomQ", ID.AtomQ);

	/** Attributes(symbol) - returns the list of attributes which are assigned to `symbol` */
	public final static IBuiltInSymbol Attributes = F.initFinalSymbol("Attributes", ID.Attributes);

	/***/
	public final static IBuiltInSymbol Automatic = F.initFinalSymbol("Automatic", ID.Automatic);

	/***/
	public final static IBuiltInSymbol Axes = F.initFinalSymbol("Axes", ID.Axes);

	/***/
	public final static IBuiltInSymbol AxesOrigin = F.initFinalSymbol("AxesOrigin", ID.AxesOrigin);

	/***/
	public final static IBuiltInSymbol AxesStyle = F.initFinalSymbol("AxesStyle", ID.AxesStyle);

	/***/
	public final static IBuiltInSymbol BSplineFunction = F.initFinalSymbol("BSplineFunction", ID.BSplineFunction);

	/***/
	public final static IBuiltInSymbol Background = F.initFinalSymbol("Background", ID.Background);

	/**
	 * BarChart(list-of-values, options) - plot a bar chart for a `list-of-values` with option `BarOrigin->Bottom` or
	 * `BarOrigin->Bottom`
	 */
	public final static IBuiltInSymbol BarChart = F.initFinalSymbol("BarChart", ID.BarChart);

	/***/
	public final static IBuiltInSymbol BarOrigin = F.initFinalSymbol("BarOrigin", ID.BarOrigin);

	/***/
	public final static IBuiltInSymbol BartlettWindow = F.initFinalSymbol("BartlettWindow", ID.BartlettWindow);

	/** BaseForm(integer, radix) - prints the `integer` number in base `radix` form. */
	public final static IBuiltInSymbol BaseForm = F.initFinalSymbol("BaseForm", ID.BaseForm);

	/** Begin("<context-name>") - start a new context definition */
	public final static IBuiltInSymbol Begin = F.initFinalSymbol("Begin", ID.Begin);

	/** BeginPackage("<context-name>") - start a new package definition */
	public final static IBuiltInSymbol BeginPackage = F.initFinalSymbol("BeginPackage", ID.BeginPackage);

	/***/
	public final static IBuiltInSymbol BeginTestSection = F.initFinalSymbol("BeginTestSection", ID.BeginTestSection);

	/**
	 * BellB(n) - the Bell number function counts the number of different ways to partition a set that has exactly `n`
	 * elements
	 */
	public final static IBuiltInSymbol BellB = F.initFinalSymbol("BellB", ID.BellB);

	/** BellY(n, k, {x1, x2, ... , xN}) - the second kind of Bell polynomials (incomplete Bell polynomials). */
	public final static IBuiltInSymbol BellY = F.initFinalSymbol("BellY", ID.BellY);

	/** BernoulliB(expr) - computes the Bernoulli number of the first kind. */
	public final static IBuiltInSymbol BernoulliB = F.initFinalSymbol("BernoulliB", ID.BernoulliB);

	/** BernoulliDistribution(p) - returns the Bernoulli distribution. */
	public final static IBuiltInSymbol BernoulliDistribution = F.initFinalSymbol("BernoulliDistribution",
			ID.BernoulliDistribution);

	/** BesselI(n, z) - modified Bessel function of the first kind. */
	public final static IBuiltInSymbol BesselI = F.initFinalSymbol("BesselI", ID.BesselI);

	/** BesselJ(n, z) - Bessel function of the first kind. */
	public final static IBuiltInSymbol BesselJ = F.initFinalSymbol("BesselJ", ID.BesselJ);

	/** BesselJZero(n, z) - is the `k`th zero of the `BesselJ(n,z)` function. */
	public final static IBuiltInSymbol BesselJZero = F.initFinalSymbol("BesselJZero", ID.BesselJZero);

	/** BesselK(n, z) - modified Bessel function of the second kind. */
	public final static IBuiltInSymbol BesselK = F.initFinalSymbol("BesselK", ID.BesselK);

	/** BesselY(n, z) - Bessel function of the second kind. */
	public final static IBuiltInSymbol BesselY = F.initFinalSymbol("BesselY", ID.BesselY);

	/** BesselYZero(n, z) - is the `k`th zero of the `BesselY(n,z)` function. */
	public final static IBuiltInSymbol BesselYZero = F.initFinalSymbol("BesselYZero", ID.BesselYZero);

	/** Beta(a, b) - is the beta function of the numbers `a`,`b`. */
	public final static IBuiltInSymbol Beta = F.initFinalSymbol("Beta", ID.Beta);

	/***/
	public final static IBuiltInSymbol BetaDistribution = F.initFinalSymbol("BetaDistribution", ID.BetaDistribution);

	/***/
	public final static IBuiltInSymbol BetaRegularized = F.initFinalSymbol("BetaRegularized", ID.BetaRegularized);

	/**
	 * BinCounts(list, width-of-bin) - count the number of elements, if `list`, is divided into successive bins with
	 * width `width-of-bin`.
	 */
	public final static IBuiltInSymbol BinCounts = F.initFinalSymbol("BinCounts", ID.BinCounts);

	/** BinaryDeserialize(byte-array) - deserialize the `byte-array` from WXF format into a Symja expression. */
	public final static IBuiltInSymbol BinaryDeserialize = F.initFinalSymbol("BinaryDeserialize", ID.BinaryDeserialize);

	/**
	 * BinaryDistance(u, v) - returns the binary distance between `u` and `v`. `0` if `u` and `v` are unequal. `1` if
	 * `u` and `v` are equal.
	 */
	public final static IBuiltInSymbol BinaryDistance = F.initFinalSymbol("BinaryDistance", ID.BinaryDistance);

	/** BinarySerialize(expr) - serialize the Symja `expr` into a byte array expression in WXF format. */
	public final static IBuiltInSymbol BinarySerialize = F.initFinalSymbol("BinarySerialize", ID.BinarySerialize);

	/** Binomial(n, k) - returns the binomial coefficient of the 2 integers `n` and `k` */
	public final static IBuiltInSymbol Binomial = F.initFinalSymbol("Binomial", ID.Binomial);

	/** BinomialDistribution(n, p) - returns the binomial distribution. */
	public final static IBuiltInSymbol BinomialDistribution = F.initFinalSymbol("BinomialDistribution",
			ID.BinomialDistribution);

	/** BitLengthi(x) - gives the number of bits needed to represent the integer `x`. The sign of `x` is ignored. */
	public final static IBuiltInSymbol BitLength = F.initFinalSymbol("BitLength", ID.BitLength);

	/***/
	public final static IBuiltInSymbol Black = F.initFinalSymbol("Black", ID.Black);

	/***/
	public final static IBuiltInSymbol BlackmanHarrisWindow = F.initFinalSymbol("BlackmanHarrisWindow",
			ID.BlackmanHarrisWindow);

	/***/
	public final static IBuiltInSymbol BlackmanNuttallWindow = F.initFinalSymbol("BlackmanNuttallWindow",
			ID.BlackmanNuttallWindow);

	/***/
	public final static IBuiltInSymbol BlackmanWindow = F.initFinalSymbol("BlackmanWindow", ID.BlackmanWindow);

	/***/
	public final static IBuiltInSymbol Blank = F.initFinalSymbol("Blank", ID.Blank);

	/***/
	public final static IBuiltInSymbol BlankNullSequence = F.initFinalSymbol("BlankNullSequence", ID.BlankNullSequence);

	/***/
	public final static IBuiltInSymbol BlankSequence = F.initFinalSymbol("BlankSequence", ID.BlankSequence);

	/** Block({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables` */
	public final static IBuiltInSymbol Block = F.initFinalSymbol("Block", ID.Block);

	/***/
	public final static IBuiltInSymbol Blue = F.initFinalSymbol("Blue", ID.Blue);

	/**
	 * Boole(expr) - returns `1` if `expr` evaluates to `True`; returns `0` if `expr` evaluates to `False`; and gives no
	 * result otherwise.
	 */
	public final static IBuiltInSymbol Boole = F.initFinalSymbol("Boole", ID.Boole);

	/**
	 * BooleanConvert(logical-expr) - convert the `logical-expr` to [disjunctive normal
	 * form](https://en.wikipedia.org/wiki/Disjunctive_normal_form)
	 */
	public final static IBuiltInSymbol BooleanConvert = F.initFinalSymbol("BooleanConvert", ID.BooleanConvert);

	/**
	 * BooleanMinimize(expr) - minimizes a boolean function with the [Quine McCluskey
	 * algorithm](https://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm)
	 */
	public final static IBuiltInSymbol BooleanMinimize = F.initFinalSymbol("BooleanMinimize", ID.BooleanMinimize);

	/** BooleanQ(expr) - returns `True` if `expr` is either `True` or `False`. */
	public final static IBuiltInSymbol BooleanQ = F.initFinalSymbol("BooleanQ", ID.BooleanQ);

	/**
	 * BooleanTable(logical-expr, variables) - generate [truth values](https://en.wikipedia.org/wiki/Truth_table) from
	 * the `logical-expr`
	 */
	public final static IBuiltInSymbol BooleanTable = F.initFinalSymbol("BooleanTable", ID.BooleanTable);

	/** BooleanVariables(logical-expr) - gives a list of the boolean variables that appear in the `logical-expr`. */
	public final static IBuiltInSymbol BooleanVariables = F.initFinalSymbol("BooleanVariables", ID.BooleanVariables);

	/** Booleans - is the set of boolean values. */
	public final static IBuiltInSymbol Booleans = F.initFinalSymbol("Booleans", ID.Booleans);

	/***/
	public final static IBuiltInSymbol Bottom = F.initFinalSymbol("Bottom", ID.Bottom);

	/** BoxWhiskerChart( ) - plot a box whisker chart. */
	public final static IBuiltInSymbol BoxWhiskerChart = F.initFinalSymbol("BoxWhiskerChart", ID.BoxWhiskerChart);

	/** BrayCurtisDistance(u, v) - returns the Bray Curtis distance between `u` and `v`. */
	public final static IBuiltInSymbol BrayCurtisDistance = F.initFinalSymbol("BrayCurtisDistance",
			ID.BrayCurtisDistance);

	/** Break() - exits a `For`, `While`, or `Do` loop. */
	public final static IBuiltInSymbol Break = F.initFinalSymbol("Break", ID.Break);

	/***/
	public final static IBuiltInSymbol Brown = F.initFinalSymbol("Brown", ID.Brown);

	/***/
	public final static IBuiltInSymbol Button = F.initFinalSymbol("Button", ID.Button);

	/** ByteArray({list-of-byte-values}) - converts the `list-of-byte-values` into a byte array. */
	public final static IBuiltInSymbol ByteArray = F.initFinalSymbol("ByteArray", ID.ByteArray);

	/***/
	public final static IBuiltInSymbol ByteArrayQ = F.initFinalSymbol("ByteArrayQ", ID.ByteArrayQ);

	/***/
	public final static IBuiltInSymbol ByteCount = F.initFinalSymbol("ByteCount", ID.ByteCount);

	/** C(n) - represents the `n`-th constant in a solution to a differential equation. */
	public final static IBuiltInSymbol C = F.initFinalSymbol("C", ID.C);

	/** CDF(distribution, value) - returns the cumulative distribution function of `value`. */
	public final static IBuiltInSymbol CDF = F.initFinalSymbol("CDF", ID.CDF);

	/***/
	public final static IBuiltInSymbol CForm = F.initFinalSymbol("CForm", ID.CForm);

	/***/
	public final static IBuiltInSymbol CMYColor = F.initFinalSymbol("CMYColor", ID.CMYColor);

	/**
	 * CanberraDistance(u, v) - returns the canberra distance between `u` and `v`, which is a weighted version of the
	 * Manhattan distance.
	 */
	public final static IBuiltInSymbol CanberraDistance = F.initFinalSymbol("CanberraDistance", ID.CanberraDistance);

	/** Cancel(expr) - cancels out common factors in numerators and denominators. */
	public final static IBuiltInSymbol Cancel = F.initFinalSymbol("Cancel", ID.Cancel);

	/***/
	public final static IBuiltInSymbol CancelButton = F.initFinalSymbol("CancelButton", ID.CancelButton);

	/** CarmichaelLambda(n) - the Carmichael function of `n` */
	public final static IBuiltInSymbol CarmichaelLambda = F.initFinalSymbol("CarmichaelLambda", ID.CarmichaelLambda);

	/** CartesianProduct(list1, list2) - returns the cartesian product for multiple lists. */
	public final static IBuiltInSymbol CartesianProduct = F.initFinalSymbol("CartesianProduct", ID.CartesianProduct);

	/** Cases(list, pattern) - returns the elements of `list` that match `pattern`. */
	public final static IBuiltInSymbol Cases = F.initFinalSymbol("Cases", ID.Cases);

	/** Catalan - Catalan's constant */
	public final static IBuiltInSymbol Catalan = F.initFinalSymbol("Catalan", ID.Catalan);

	/** CatalanNumber(n) - returns the catalan number for the integer argument `n`. */
	public final static IBuiltInSymbol CatalanNumber = F.initFinalSymbol("CatalanNumber", ID.CatalanNumber);

	/***/
	public final static IBuiltInSymbol Catch = F.initFinalSymbol("Catch", ID.Catch);

	/** Catenate({l1, l2, ...}) - concatenates the lists `l1, l2, ...` */
	public final static IBuiltInSymbol Catenate = F.initFinalSymbol("Catenate", ID.Catenate);

	/** Ceiling(expr) - gives the first integer greater than or equal `expr`. */
	public final static IBuiltInSymbol Ceiling = F.initFinalSymbol("Ceiling", ID.Ceiling);

	/***/
	public final static IBuiltInSymbol CenterDot = F.initFinalSymbol("CenterDot", ID.CenterDot);

	/** CentralMoment(list, r) - gives the the `r`th central moment (i.e. the `r`th moment about the mean) of `list`. */
	public final static IBuiltInSymbol CentralMoment = F.initFinalSymbol("CentralMoment", ID.CentralMoment);

	/***/
	public final static IBuiltInSymbol CharacterEncoding = F.initFinalSymbol("CharacterEncoding", ID.CharacterEncoding);

	/***/
	public final static IBuiltInSymbol CharacterRange = F.initFinalSymbol("CharacterRange", ID.CharacterRange);

	/**
	 * CharacteristicPolynomial(matrix, var) - computes the characteristic polynomial of a `matrix` for the variable
	 * `var`.
	 */
	public final static IBuiltInSymbol CharacteristicPolynomial = F.initFinalSymbol("CharacteristicPolynomial",
			ID.CharacteristicPolynomial);

	/***/
	public final static IBuiltInSymbol Characters = F.initFinalSymbol("Characters", ID.Characters);

	/** ChebyshevT(n, x) - returns the Chebyshev polynomial of the first kind `T_n(x)`. */
	public final static IBuiltInSymbol ChebyshevT = F.initFinalSymbol("ChebyshevT", ID.ChebyshevT);

	/** ChebyshevU(n, x) - returns the Chebyshev polynomial of the second kind `U_n(x)`. */
	public final static IBuiltInSymbol ChebyshevU = F.initFinalSymbol("ChebyshevU", ID.ChebyshevU);

	/**
	 * Check(expr, failure) - evaluates `expr`, and returns the result, unless messages were generated, in which case
	 * `failure` will be returned.
	 */
	public final static IBuiltInSymbol Check = F.initFinalSymbol("Check", ID.Check);

	/**
	 * ChessboardDistance(u, v) - returns the chessboard distance (also known as Chebyshev distance) between `u` and
	 * `v`, which is the number of moves a king on a chessboard needs to get from square `u` to square `v`.
	 */
	public final static IBuiltInSymbol ChessboardDistance = F.initFinalSymbol("ChessboardDistance",
			ID.ChessboardDistance);

	/***/
	public final static IBuiltInSymbol ChiSquareDistribution = F.initFinalSymbol("ChiSquareDistribution",
			ID.ChiSquareDistribution);

	/** ChineseRemainder({a1, a2, a3,...}, {n1, n2, n3,...}) - the chinese remainder function. */
	public final static IBuiltInSymbol ChineseRemainder = F.initFinalSymbol("ChineseRemainder", ID.ChineseRemainder);

	/**
	 * CholeskyDecomposition(matrix) - calculate the Cholesky decomposition of a hermitian, positive definite square
	 * `matrix`.
	 */
	public final static IBuiltInSymbol CholeskyDecomposition = F.initFinalSymbol("CholeskyDecomposition",
			ID.CholeskyDecomposition);

	/**
	 * Chop(numerical-expr) - replaces numerical values in the `numerical-expr` which are close to zero with symbolic
	 * value `0`.
	 */
	public final static IBuiltInSymbol Chop = F.initFinalSymbol("Chop", ID.Chop);

	/***/
	public final static IBuiltInSymbol CircleDot = F.initFinalSymbol("CircleDot", ID.CircleDot);

	/** CirclePoints(i) - gives the `i` points on the unit circle for a positive integer `i`. */
	public final static IBuiltInSymbol CirclePoints = F.initFinalSymbol("CirclePoints", ID.CirclePoints);

	/** Clear(symbol1, symbol2,...) - clears all values of the given symbols. */
	public final static IBuiltInSymbol Clear = F.initFinalSymbol("Clear", ID.Clear);

	/** ClearAll(symbol1, symbol2,...) - clears all values and attributes associated with the given symbols. */
	public final static IBuiltInSymbol ClearAll = F.initFinalSymbol("ClearAll", ID.ClearAll);

	/** ClearAttributes(symbol, attrib) - removes `attrib` from `symbol`'s attributes. */
	public final static IBuiltInSymbol ClearAttributes = F.initFinalSymbol("ClearAttributes", ID.ClearAttributes);

	/**
	 * Clip(expr) - returns `expr` in the range `-1` to `1`. Returns `-1` if `expr` is less than `-1`. Returns `1` if
	 * `expr` is greater than `1`.
	 */
	public final static IBuiltInSymbol Clip = F.initFinalSymbol("Clip", ID.Clip);

	/** Coefficient(polynomial, variable, exponent) - get the coefficient of `variable^exponent` in `polynomial`. */
	public final static IBuiltInSymbol Coefficient = F.initFinalSymbol("Coefficient", ID.Coefficient);

	/** CoefficientList(polynomial, variable) - get the coefficient list of a `polynomial`. */
	public final static IBuiltInSymbol CoefficientList = F.initFinalSymbol("CoefficientList", ID.CoefficientList);

	/** CoefficientRules(polynomial, list-of-variables) - get the list of coefficient rules of a `polynomial`. */
	public final static IBuiltInSymbol CoefficientRules = F.initFinalSymbol("CoefficientRules", ID.CoefficientRules);

	/** Collect(expr, variable) - collect subexpressions in `expr` which belong to the same `variable`. */
	public final static IBuiltInSymbol Collect = F.initFinalSymbol("Collect", ID.Collect);

	/***/
	public final static IBuiltInSymbol Colon = F.initFinalSymbol("Colon", ID.Colon);

	/***/
	public final static IBuiltInSymbol ColorData = F.initFinalSymbol("ColorData", ID.ColorData);

	/***/
	public final static IBuiltInSymbol ColorFunction = F.initFinalSymbol("ColorFunction", ID.ColorFunction);

	/***/
	public final static IBuiltInSymbol Column = F.initFinalSymbol("Column", ID.Column);

	/** Commonest(data-values-list) - the mode of a list of data values is the value that appears most often. */
	public final static IBuiltInSymbol Commonest = F.initFinalSymbol("Commonest", ID.Commonest);

	/***/
	public final static IBuiltInSymbol CompatibleUnitQ = F.initFinalSymbol("CompatibleUnitQ", ID.CompatibleUnitQ);

	/***/
	public final static IBuiltInSymbol Compile = F.initFinalSymbol("Compile", ID.Compile);

	/***/
	public final static IBuiltInSymbol CompiledFunction = F.initFinalSymbol("CompiledFunction", ID.CompiledFunction);

	/** Complement(set1, set2) - get the complement set from `set1` and `set2`. */
	public final static IBuiltInSymbol Complement = F.initFinalSymbol("Complement", ID.Complement);

	/** Complex - is the head of complex numbers. */
	public final static IBuiltInSymbol Complex = F.initFinalSymbol("Complex", ID.Complex);

	/**
	 * ComplexExpand(expr) - get the expanded `expr`. All variable symbols in `expr` are assumed to be non complex
	 * numbers.
	 */
	public final static IBuiltInSymbol ComplexExpand = F.initFinalSymbol("ComplexExpand", ID.ComplexExpand);

	/** ComplexInfinity - represents an infinite complex quantity of undetermined direction. */
	public final static IBuiltInSymbol ComplexInfinity = F.initFinalSymbol("ComplexInfinity", ID.ComplexInfinity);

	/**
	 * ComplexPlot3D(expr, {z, min, max ) - create a 3D plot of `expr` for the complex variable `z` in the range `{
	 * Re(min),Re(max) }` to `{ Im(min),Im(max) }`
	 */
	public final static IBuiltInSymbol ComplexPlot3D = F.initFinalSymbol("ComplexPlot3D", ID.ComplexPlot3D);

	/** Complexes - is the set of complex numbers. */
	public final static IBuiltInSymbol Complexes = F.initFinalSymbol("Complexes", ID.Complexes);

	/***/
	public final static IBuiltInSymbol ComplexityFunction = F.initFinalSymbol("ComplexityFunction",
			ID.ComplexityFunction);

	/**
	 * ComposeList(list-of-symbols, variable) - creates a list of compositions of the symbols applied at the argument
	 * `x`.
	 */
	public final static IBuiltInSymbol ComposeList = F.initFinalSymbol("ComposeList", ID.ComposeList);

	/** ComposeSeries( series1, series2 ) - substitute `series2` into `series1` */
	public final static IBuiltInSymbol ComposeSeries = F.initFinalSymbol("ComposeSeries", ID.ComposeSeries);

	/** Composition(sym1, sym2,...)[arg1, arg2,...] - creates a composition of the symbols applied at the arguments. */
	public final static IBuiltInSymbol Composition = F.initFinalSymbol("Composition", ID.Composition);

	/** CompoundExpression(expr1, expr2, ...) - evaluates its arguments in turn, returning the last result. */
	public final static IBuiltInSymbol CompoundExpression = F.initFinalSymbol("CompoundExpression",
			ID.CompoundExpression);

	/**
	 * Condition(pattern, expr) - places an additional constraint on `pattern` that only allows it to match if `expr`
	 * evaluates to `True`.
	 */
	public final static IBuiltInSymbol Condition = F.initFinalSymbol("Condition", ID.Condition);

	/**
	 * ConditionalExpression(expr, condition) - if `condition` evaluates to `True` return `expr`, if `condition`
	 * evaluates to `False` return `Undefined`. Otherwise return the `ConditionalExpression` unevaluated.
	 */
	public final static IBuiltInSymbol ConditionalExpression = F.initFinalSymbol("ConditionalExpression",
			ID.ConditionalExpression);

	/** Conjugate(z) - returns the complex conjugate of the complex number `z`. */
	public final static IBuiltInSymbol Conjugate = F.initFinalSymbol("Conjugate", ID.Conjugate);

	/** ConjugateTranspose(matrix) - get the transposed `matrix` with conjugated matrix elements. */
	public final static IBuiltInSymbol ConjugateTranspose = F.initFinalSymbol("ConjugateTranspose",
			ID.ConjugateTranspose);

	/***/
	public final static IBuiltInSymbol ConnectedGraphQ = F.initFinalSymbol("ConnectedGraphQ", ID.ConnectedGraphQ);

	/** Constant - is an attribute that indicates that a symbol is a constant. */
	public final static IBuiltInSymbol Constant = F.initFinalSymbol("Constant", ID.Constant);

	/** ConstantArray(expr, n) - returns a list of `n` copies of `expr`. */
	public final static IBuiltInSymbol ConstantArray = F.initFinalSymbol("ConstantArray", ID.ConstantArray);

	/***/
	public final static IBuiltInSymbol ContainsAll = F.initFinalSymbol("ContainsAll", ID.ContainsAll);

	/***/
	public final static IBuiltInSymbol ContainsAny = F.initFinalSymbol("ContainsAny", ID.ContainsAny);

	/***/
	public final static IBuiltInSymbol ContainsExactly = F.initFinalSymbol("ContainsExactly", ID.ContainsExactly);

	/***/
	public final static IBuiltInSymbol ContainsNone = F.initFinalSymbol("ContainsNone", ID.ContainsNone);

	/** ContainsOnly(list1, list2) - yields True if `list1` contains only elements that appear in `list2`. */
	public final static IBuiltInSymbol ContainsOnly = F.initFinalSymbol("ContainsOnly", ID.ContainsOnly);

	/** Context(symbol) - return the context of the given symbol. */
	public final static IBuiltInSymbol Context = F.initFinalSymbol("Context", ID.Context);

	/** Continue() - continues with the next iteration in a `For`, `While`, or `Do` loop. */
	public final static IBuiltInSymbol Continue = F.initFinalSymbol("Continue", ID.Continue);

	/** ContinuedFraction(number) - get the continued fraction representation of `number`. */
	public final static IBuiltInSymbol ContinuedFraction = F.initFinalSymbol("ContinuedFraction", ID.ContinuedFraction);

	/***/
	public final static IBuiltInSymbol ContourPlot = F.initFinalSymbol("ContourPlot", ID.ContourPlot);

	/**
	 * Convergents({n1, n2, ...}) - return the list of convergents which represents the continued fraction list `{n1,
	 * n2, ...}`.
	 */
	public final static IBuiltInSymbol Convergents = F.initFinalSymbol("Convergents", ID.Convergents);

	/***/
	public final static IBuiltInSymbol ConvexHullMesh = F.initFinalSymbol("ConvexHullMesh", ID.ConvexHullMesh);

	/** CoprimeQ(x, y) - tests whether `x` and `y` are coprime by computing their greatest common divisor. */
	public final static IBuiltInSymbol CoprimeQ = F.initFinalSymbol("CoprimeQ", ID.CoprimeQ);

	/** Correlation(a, b) - computes Pearson's correlation of two equal-sized vectors `a` and `b`. */
	public final static IBuiltInSymbol Correlation = F.initFinalSymbol("Correlation", ID.Correlation);

	/**
	 * Cos(expr) - returns the cosine of `expr` (measured in radians). `Cos(expr)` will evaluate automatically in the
	 * case `expr` is a multiple of `Pi, Pi/2, Pi/3, Pi/4` and `Pi/6`.
	 */
	public final static IBuiltInSymbol Cos = F.initFinalSymbol("Cos", ID.Cos);

	/** CosIntegral(expr) - returns the cosine integral of `expr`. */
	public final static IBuiltInSymbol CosIntegral = F.initFinalSymbol("CosIntegral", ID.CosIntegral);

	/** Cosh(z) - returns the hyperbolic cosine of `z`. */
	public final static IBuiltInSymbol Cosh = F.initFinalSymbol("Cosh", ID.Cosh);

	/** CoshIntegral(expr) - returns the hyperbolic cosine integral of `expr`. */
	public final static IBuiltInSymbol CoshIntegral = F.initFinalSymbol("CoshIntegral", ID.CoshIntegral);

	/** CosineDistance(u, v) - returns the cosine distance between `u` and `v`. */
	public final static IBuiltInSymbol CosineDistance = F.initFinalSymbol("CosineDistance", ID.CosineDistance);

	/** Cot(expr) - the cotangent function. */
	public final static IBuiltInSymbol Cot = F.initFinalSymbol("Cot", ID.Cot);

	/** Coth(z) - returns the hyperbolic cotangent of `z`. */
	public final static IBuiltInSymbol Coth = F.initFinalSymbol("Coth", ID.Coth);

	/** Count(list, pattern) - returns the number of times `pattern` appears in `list`. */
	public final static IBuiltInSymbol Count = F.initFinalSymbol("Count", ID.Count);

	/** CountDistinct(list) - returns the number of distinct entries in `list`. */
	public final static IBuiltInSymbol CountDistinct = F.initFinalSymbol("CountDistinct", ID.CountDistinct);

	/**
	 * Counts({elem1, elem2, elem3, ...}) - count the number of each distinct element in the list `{elem1, elem2, elem3,
	 * ...}` and return the result as an association `<|elem1->counter1, ...|>`.
	 */
	public final static IBuiltInSymbol Counts = F.initFinalSymbol("Counts", ID.Counts);

	/** Covariance(a, b) - computes the covariance between the equal-sized vectors `a` and `b`. */
	public final static IBuiltInSymbol Covariance = F.initFinalSymbol("Covariance", ID.Covariance);

	/***/
	public final static IBuiltInSymbol CreateDirectory = F.initFinalSymbol("CreateDirectory", ID.CreateDirectory);

	/** Cross(a, b) - computes the vector cross product of `a` and `b`. */
	public final static IBuiltInSymbol Cross = F.initFinalSymbol("Cross", ID.Cross);

	/** Csc(z) - returns the cosecant of `z`. */
	public final static IBuiltInSymbol Csc = F.initFinalSymbol("Csc", ID.Csc);

	/** Csch(z) - returns the hyperbolic cosecant of `z`. */
	public final static IBuiltInSymbol Csch = F.initFinalSymbol("Csch", ID.Csch);

	/** CubeRoot(n) - finds the real-valued cube root of the given `n`. */
	public final static IBuiltInSymbol CubeRoot = F.initFinalSymbol("CubeRoot", ID.CubeRoot);

	/** Curl({f1, f2}, {x1, x2}) - gives the curl. */
	public final static IBuiltInSymbol Curl = F.initFinalSymbol("Curl", ID.Curl);

	/***/
	public final static IBuiltInSymbol Cyan = F.initFinalSymbol("Cyan", ID.Cyan);

	/** Cyclotomic(n, x) - returns the Cyclotomic polynomial `C_n(x)`. */
	public final static IBuiltInSymbol Cyclotomic = F.initFinalSymbol("Cyclotomic", ID.Cyclotomic);

	/** D(f, x) - gives the partial derivative of `f` with respect to `x`. */
	public final static IBuiltInSymbol D = F.initFinalSymbol("D", ID.D);

	/**
	 * DSolve(equation, f(var), var) - attempts to solve a linear differential `equation` for the function `f(var)` and
	 * variable `var`.
	 */
	public final static IBuiltInSymbol DSolve = F.initFinalSymbol("DSolve", ID.DSolve);

	/***/
	public final static IBuiltInSymbol Dataset = F.initFinalSymbol("Dataset", ID.Dataset);

	/***/
	public final static IBuiltInSymbol DateObject = F.initFinalSymbol("DateObject", ID.DateObject);

	/***/
	public final static IBuiltInSymbol DateValue = F.initFinalSymbol("DateValue", ID.DateValue);

	/** Decrement(x) - decrements `x` by `1`, returning the original value of `x`. */
	public final static IBuiltInSymbol Decrement = F.initFinalSymbol("Decrement", ID.Decrement);

	/**
	 * Default(symbol) - `Default` returns the default value associated with the `symbol` for a pattern default `_.`
	 * expression.
	 */
	public final static IBuiltInSymbol Default = F.initFinalSymbol("Default", ID.Default);

	/***/
	public final static IBuiltInSymbol DefaultButton = F.initFinalSymbol("DefaultButton", ID.DefaultButton);

	/** Defer(expr) - `Defer` doesn't evaluate `expr` and didn't appear in the output */
	public final static IBuiltInSymbol Defer = F.initFinalSymbol("Defer", ID.Defer);

	/** Definition(symbol) - prints user-defined values and rules associated with `symbol`. */
	public final static IBuiltInSymbol Definition = F.initFinalSymbol("Definition", ID.Definition);

	/** Degree - the constant `Degree` converts angles from degree to `Pi/180` radians. */
	public final static IBuiltInSymbol Degree = F.initFinalSymbol("Degree", ID.Degree);

	/***/
	public final static IBuiltInSymbol DegreeLexicographic = F.initFinalSymbol("DegreeLexicographic",
			ID.DegreeLexicographic);

	/***/
	public final static IBuiltInSymbol DegreeReverseLexicographic = F.initFinalSymbol("DegreeReverseLexicographic",
			ID.DegreeReverseLexicographic);

	/** Delete(expr, n) - returns `expr` with part `n` removed. */
	public final static IBuiltInSymbol Delete = F.initFinalSymbol("Delete", ID.Delete);

	/** DeleteCases(list, pattern) - returns the elements of `list` that do not match `pattern`. */
	public final static IBuiltInSymbol DeleteCases = F.initFinalSymbol("DeleteCases", ID.DeleteCases);

	/** DeleteDuplicates(list) - deletes duplicates from `list`. */
	public final static IBuiltInSymbol DeleteDuplicates = F.initFinalSymbol("DeleteDuplicates", ID.DeleteDuplicates);

	/**
	 * DeleteDuplicatesBy(list, predicate) - deletes duplicates from `list`, for which the `predicate` returns `True`.
	 */
	public final static IBuiltInSymbol DeleteDuplicatesBy = F.initFinalSymbol("DeleteDuplicatesBy",
			ID.DeleteDuplicatesBy);

	/**
	 * Denominator(expr) - gives the denominator in `expr`. Denominator collects expressions with negative exponents.
	 */
	public final static IBuiltInSymbol Denominator = F.initFinalSymbol("Denominator", ID.Denominator);

	/** DensityHistogram( list-of-pair-values ) - plot a density histogram for a `list-of-pair-values` */
	public final static IBuiltInSymbol DensityHistogram = F.initFinalSymbol("DensityHistogram", ID.DensityHistogram);

	/***/
	public final static IBuiltInSymbol DensityPlot = F.initFinalSymbol("DensityPlot", ID.DensityPlot);

	/** Depth(expr) - gives the depth of `expr`. */
	public final static IBuiltInSymbol Depth = F.initFinalSymbol("Depth", ID.Depth);

	/** Derivative(n)[f] - represents the `n`-th derivative of the function `f`. */
	public final static IBuiltInSymbol Derivative = F.initFinalSymbol("Derivative", ID.Derivative);

	/** DesignMatrix(m, f, x) - returns the design matrix. */
	public final static IBuiltInSymbol DesignMatrix = F.initFinalSymbol("DesignMatrix", ID.DesignMatrix);

	/** Det(matrix) - computes the determinant of the `matrix`. */
	public final static IBuiltInSymbol Det = F.initFinalSymbol("Det", ID.Det);

	/** Diagonal(matrix) - computes the diagonal vector of the `matrix`. */
	public final static IBuiltInSymbol Diagonal = F.initFinalSymbol("Diagonal", ID.Diagonal);

	/** DiagonalMatrix(list) - gives a matrix with the values in `list` on its diagonal and zeroes elsewhere. */
	public final static IBuiltInSymbol DiagonalMatrix = F.initFinalSymbol("DiagonalMatrix", ID.DiagonalMatrix);

	/** DialogInput() - if the file system is enabled, the user can input a string in a dialog box. */
	public final static IBuiltInSymbol DialogInput = F.initFinalSymbol("DialogInput", ID.DialogInput);

	/***/
	public final static IBuiltInSymbol DialogNotebook = F.initFinalSymbol("DialogNotebook", ID.DialogNotebook);

	/***/
	public final static IBuiltInSymbol DialogReturn = F.initFinalSymbol("DialogReturn", ID.DialogReturn);

	/**
	 * DiceDissimilarity(u, v) - returns the Dice dissimilarity between the two boolean 1-D lists `u` and `v`, which is
	 * defined as `(c_tf + c_ft) / (2 * c_tt + c_ft + c_tf)`, where n is `len(u)` and `c_ij` is the number of
	 * occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
	 */
	public final static IBuiltInSymbol DiceDissimilarity = F.initFinalSymbol("DiceDissimilarity", ID.DiceDissimilarity);

	/***/
	public final static IBuiltInSymbol Differences = F.initFinalSymbol("Differences", ID.Differences);

	/***/
	public final static IBuiltInSymbol DigitCharacter = F.initFinalSymbol("DigitCharacter", ID.DigitCharacter);

	/** DigitCount(n) - returns a list of the number of integer digits for `n` for `radix` 10. */
	public final static IBuiltInSymbol DigitCount = F.initFinalSymbol("DigitCount", ID.DigitCount);

	/** DigitQ(str) - returns `True` if `str` is a string which contains only digits. */
	public final static IBuiltInSymbol DigitQ = F.initFinalSymbol("DigitQ", ID.DigitQ);

	/** Dimensions(expr) - returns a list of the dimensions of the expression `expr`. */
	public final static IBuiltInSymbol Dimensions = F.initFinalSymbol("Dimensions", ID.Dimensions);

	/** DiracDelta(x) - `DiracDelta` function returns `0` for all real numbers `x` where `x != 0`. */
	public final static IBuiltInSymbol DiracDelta = F.initFinalSymbol("DiracDelta", ID.DiracDelta);

	/***/
	public final static IBuiltInSymbol DirectedEdge = F.initFinalSymbol("DirectedEdge", ID.DirectedEdge);

	/** DirectedInfinity(z) - represents an infinite multiple of the complex number `z`. */
	public final static IBuiltInSymbol DirectedInfinity = F.initFinalSymbol("DirectedInfinity", ID.DirectedInfinity);

	/***/
	public final static IBuiltInSymbol Direction = F.initFinalSymbol("Direction", ID.Direction);

	/***/
	public final static IBuiltInSymbol Directive = F.initFinalSymbol("Directive", ID.Directive);

	/***/
	public final static IBuiltInSymbol DirichletEta = F.initFinalSymbol("DirichletEta", ID.DirichletEta);

	/***/
	public final static IBuiltInSymbol DirichletWindow = F.initFinalSymbol("DirichletWindow", ID.DirichletWindow);

	/**
	 * DiscreteDelta(n1, n2, n3, ...) - `DiscreteDelta` function returns `1` if all the `ni` are `0`. Returns `0`
	 * otherwise.
	 */
	public final static IBuiltInSymbol DiscreteDelta = F.initFinalSymbol("DiscreteDelta", ID.DiscreteDelta);

	/** DiscreteUniformDistribution({min, max}) - returns a discrete uniform distribution. */
	public final static IBuiltInSymbol DiscreteUniformDistribution = F.initFinalSymbol("DiscreteUniformDistribution",
			ID.DiscreteUniformDistribution);

	/**
	 * Discriminant(poly, var) - computes the discriminant of the polynomial `poly` with respect to the variable `var`.
	 */
	public final static IBuiltInSymbol Discriminant = F.initFinalSymbol("Discriminant", ID.Discriminant);

	/***/
	public final static IBuiltInSymbol DisjointQ = F.initFinalSymbol("DisjointQ", ID.DisjointQ);

	/***/
	public final static IBuiltInSymbol Dispatch = F.initFinalSymbol("Dispatch", ID.Dispatch);

	/***/
	public final static IBuiltInSymbol Disputed = F.initFinalSymbol("Disputed", ID.Disputed);

	/***/
	public final static IBuiltInSymbol DistanceFunction = F.initFinalSymbol("DistanceFunction", ID.DistanceFunction);

	/** Distribute(f(x1, x2, x3,...)) - distributes `f` over `Plus` appearing in any of the `xi`. */
	public final static IBuiltInSymbol Distribute = F.initFinalSymbol("Distribute", ID.Distribute);

	/***/
	public final static IBuiltInSymbol Distributed = F.initFinalSymbol("Distributed", ID.Distributed);

	/** Div({f1, f2, f3,...},{x1, x2, x3,...}) - compute the divergence. */
	public final static IBuiltInSymbol Div = F.initFinalSymbol("Div", ID.Div);

	/** Divide(a, b) - represents the division of `a` by `b`. */
	public final static IBuiltInSymbol Divide = F.initFinalSymbol("Divide", ID.Divide);

	/** DivideBy(x, dx) - is equivalent to `x = x / dx`. */
	public final static IBuiltInSymbol DivideBy = F.initFinalSymbol("DivideBy", ID.DivideBy);

	/** Divisible(n, m) - returns `True` if `n` could be divide by `m`. */
	public final static IBuiltInSymbol Divisible = F.initFinalSymbol("Divisible", ID.Divisible);

	/** DivisorSigma(k, n) - returns the sum of the `k`-th powers of the divisors of `n`. */
	public final static IBuiltInSymbol DivisorSigma = F.initFinalSymbol("DivisorSigma", ID.DivisorSigma);

	/** DivisorSum(n, head) - returns the sum of the divisors of `n`. The `head` is applied to each divisor. */
	public final static IBuiltInSymbol DivisorSum = F.initFinalSymbol("DivisorSum", ID.DivisorSum);

	/** Divisors(n) - returns all integers that divide the integer `n`. */
	public final static IBuiltInSymbol Divisors = F.initFinalSymbol("Divisors", ID.Divisors);

	/** Do(expr, {max}) - evaluates `expr` `max` times. */
	public final static IBuiltInSymbol Do = F.initFinalSymbol("Do", ID.Do);

	/** Dot(x, y) or x . y - `x . y` computes the vector dot product or matrix product `x . y`. */
	public final static IBuiltInSymbol Dot = F.initFinalSymbol("Dot", ID.Dot);

	/***/
	public final static IBuiltInSymbol DownValues = F.initFinalSymbol("DownValues", ID.DownValues);

	/** Drop(expr, n) - returns `expr` with the first `n` leaves removed. */
	public final static IBuiltInSymbol Drop = F.initFinalSymbol("Drop", ID.Drop);

	/***/
	public final static IBuiltInSymbol DuplicateFreeQ = F.initFinalSymbol("DuplicateFreeQ", ID.DuplicateFreeQ);

	/***/
	public final static IBuiltInSymbol Dynamic = F.initFinalSymbol("Dynamic", ID.Dynamic);

	/** E - Euler's constant E */
	public final static IBuiltInSymbol E = F.initFinalSymbol("E", ID.E);

	/***/
	public final static IBuiltInSymbol EasterSunday = F.initFinalSymbol("EasterSunday", ID.EasterSunday);

	/** Echo(expr) - prints the `expr` to the default output stream and returns `expr`. */
	public final static IBuiltInSymbol Echo = F.initFinalSymbol("Echo", ID.Echo);

	/**
	 * EchoFunction()[expr] - operator form of the `Echo`function. Print the `expr` to the default output stream and
	 * return `expr`.
	 */
	public final static IBuiltInSymbol EchoFunction = F.initFinalSymbol("EchoFunction", ID.EchoFunction);

	/***/
	public final static IBuiltInSymbol EdgeCount = F.initFinalSymbol("EdgeCount", ID.EdgeCount);

	/** EdgeList(graph) - convert the `graph` into a list of edges. */
	public final static IBuiltInSymbol EdgeList = F.initFinalSymbol("EdgeList", ID.EdgeList);

	/** EdgeQ(graph, edge) - test if `edge` is an edge in the `graph` object. */
	public final static IBuiltInSymbol EdgeQ = F.initFinalSymbol("EdgeQ", ID.EdgeQ);

	/***/
	public final static IBuiltInSymbol EdgeWeight = F.initFinalSymbol("EdgeWeight", ID.EdgeWeight);

	/** EffectiveInterest(i, n) - returns an effective interest rate object. */
	public final static IBuiltInSymbol EffectiveInterest = F.initFinalSymbol("EffectiveInterest", ID.EffectiveInterest);

	/** Eigenvalues(matrix) - get the numerical eigenvalues of the `matrix`. */
	public final static IBuiltInSymbol Eigenvalues = F.initFinalSymbol("Eigenvalues", ID.Eigenvalues);

	/** Eigenvectors(matrix) - get the numerical eigenvectors of the `matrix`. */
	public final static IBuiltInSymbol Eigenvectors = F.initFinalSymbol("Eigenvectors", ID.Eigenvectors);

	/** Element(symbol, dom) - assume (or test) that the `symbol` is in the domain `dom`. */
	public final static IBuiltInSymbol Element = F.initFinalSymbol("Element", ID.Element);

	/** ElementData("name", "property") - gives the value of the property for the chemical specified by name. */
	public final static IBuiltInSymbol ElementData = F.initFinalSymbol("ElementData", ID.ElementData);

	/**
	 * Eliminate(list-of-equations, list-of-variables) - attempts to eliminate the variables from the
	 * `list-of-variables` in the `list-of-equations`.
	 */
	public final static IBuiltInSymbol Eliminate = F.initFinalSymbol("Eliminate", ID.Eliminate);

	/***/
	public final static IBuiltInSymbol EliminationOrder = F.initFinalSymbol("EliminationOrder", ID.EliminationOrder);

	/** EllipticE(z) - returns the complete elliptic integral of the second kind. */
	public final static IBuiltInSymbol EllipticE = F.initFinalSymbol("EllipticE", ID.EllipticE);

	/** EllipticF(z) - returns the incomplete elliptic integral of the first kind. */
	public final static IBuiltInSymbol EllipticF = F.initFinalSymbol("EllipticF", ID.EllipticF);

	/** EllipticK(z) - returns the complete elliptic integral of the first kind. */
	public final static IBuiltInSymbol EllipticK = F.initFinalSymbol("EllipticK", ID.EllipticK);

	/** EllipticPi(n,m) - returns the complete elliptic integral of the third kind. */
	public final static IBuiltInSymbol EllipticPi = F.initFinalSymbol("EllipticPi", ID.EllipticPi);

	/***/
	public final static IBuiltInSymbol EllipticTheta = F.initFinalSymbol("EllipticTheta", ID.EllipticTheta);

	/** End( ) - end a context definition started with `Begin` */
	public final static IBuiltInSymbol End = F.initFinalSymbol("End", ID.End);

	/***/
	public final static IBuiltInSymbol EndOfLine = F.initFinalSymbol("EndOfLine", ID.EndOfLine);

	/***/
	public final static IBuiltInSymbol EndOfString = F.initFinalSymbol("EndOfString", ID.EndOfString);

	/** EndPackage( ) - end a package definition */
	public final static IBuiltInSymbol EndPackage = F.initFinalSymbol("EndPackage", ID.EndPackage);

	/***/
	public final static IBuiltInSymbol EndTestSection = F.initFinalSymbol("EndTestSection", ID.EndTestSection);

	/***/
	public final static IBuiltInSymbol Entity = F.initFinalSymbol("Entity", ID.Entity);

	/**
	 * Equal(x, y) - yields `True` if `x` and `y` are known to be equal, or `False` if `x` and `y` are known to be
	 * unequal.
	 */
	public final static IBuiltInSymbol Equal = F.initFinalSymbol("Equal", ID.Equal);

	/**
	 * Equivalent(arg1, arg2, ...) - Equivalence relation. `Equivalent(A, B)` is `True` iff `A` and `B` are both `True`
	 * or both `False`. Returns `True` if all of the arguments are logically equivalent. Returns `False` otherwise.
	 * `Equivalent(arg1, arg2, ...)` is equivalent to `(arg1 && arg2 && ...) || (!arg1 && !arg2 && ...)`.
	 */
	public final static IBuiltInSymbol Equivalent = F.initFinalSymbol("Equivalent", ID.Equivalent);

	/** Erf(z) - returns the error function of `z`. */
	public final static IBuiltInSymbol Erf = F.initFinalSymbol("Erf", ID.Erf);

	/** Erfc(z) - returns the complementary error function of `z`. */
	public final static IBuiltInSymbol Erfc = F.initFinalSymbol("Erfc", ID.Erfc);

	/** Erfi(z) - returns the imaginary error function of `z`. */
	public final static IBuiltInSymbol Erfi = F.initFinalSymbol("Erfi", ID.Erfi);

	/** ErlangDistribution({k, lambda}) - returns a Erlang distribution. */
	public final static IBuiltInSymbol ErlangDistribution = F.initFinalSymbol("ErlangDistribution",
			ID.ErlangDistribution);

	/** EuclideanDistance(u, v) - returns the euclidean distance between `u` and `v`. */
	public final static IBuiltInSymbol EuclideanDistance = F.initFinalSymbol("EuclideanDistance", ID.EuclideanDistance);

	/** EulerE(n) - gives the euler number `En`. */
	public final static IBuiltInSymbol EulerE = F.initFinalSymbol("EulerE", ID.EulerE);

	/** EulerGamma - Euler-Mascheroni constant */
	public final static IBuiltInSymbol EulerGamma = F.initFinalSymbol("EulerGamma", ID.EulerGamma);

	/** EulerPhi(n) - compute Euler's totient function. */
	public final static IBuiltInSymbol EulerPhi = F.initFinalSymbol("EulerPhi", ID.EulerPhi);

	/** EulerianGraphQ(graph) - returns `True` if `graph` is an eulerian graph, and `False` otherwise. */
	public final static IBuiltInSymbol EulerianGraphQ = F.initFinalSymbol("EulerianGraphQ", ID.EulerianGraphQ);

	/**
	 * Evaluate(expr) - the `Evaluate` function will be executed even if the function attributes `HoldFirst, HoldRest,
	 * HoldAll` are set for the function head.
	 */
	public final static IBuiltInSymbol Evaluate = F.initFinalSymbol("Evaluate", ID.Evaluate);

	/** EvenQ(x) - returns `True` if `x` is even, and `False` otherwise. */
	public final static IBuiltInSymbol EvenQ = F.initFinalSymbol("EvenQ", ID.EvenQ);

	/** ExactNumberQ(expr) - returns `True` if `expr` is an exact number, and `False` otherwise. */
	public final static IBuiltInSymbol ExactNumberQ = F.initFinalSymbol("ExactNumberQ", ID.ExactNumberQ);

	/** Except(c) - represents a pattern object that matches any expression except those matching `c`. */
	public final static IBuiltInSymbol Except = F.initFinalSymbol("Except", ID.Except);

	/***/
	public final static IBuiltInSymbol Exists = F.initFinalSymbol("Exists", ID.Exists);

	/***/
	public final static IBuiltInSymbol Exit = F.initFinalSymbol("Exit", ID.Exit);

	/** Exp(z) - the exponential function `E^z`. */
	public final static IBuiltInSymbol Exp = F.initFinalSymbol("Exp", ID.Exp);

	/** ExpIntegralE(n, expr) - returns the exponential integral `E_n(expr)` of `expr`. */
	public final static IBuiltInSymbol ExpIntegralE = F.initFinalSymbol("ExpIntegralE", ID.ExpIntegralE);

	/** ExpIntegralEi(expr) - returns the exponential integral `Ei(expr)` of `expr`. */
	public final static IBuiltInSymbol ExpIntegralEi = F.initFinalSymbol("ExpIntegralEi", ID.ExpIntegralEi);

	/***/
	public final static IBuiltInSymbol ExpToTrig = F.initFinalSymbol("ExpToTrig", ID.ExpToTrig);

	/** Expand(expr) - expands out positive rational powers and products of sums in `expr`. */
	public final static IBuiltInSymbol Expand = F.initFinalSymbol("Expand", ID.Expand);

	/** ExpandAll(expr) - expands out all positive integer powers and products of sums in `expr`. */
	public final static IBuiltInSymbol ExpandAll = F.initFinalSymbol("ExpandAll", ID.ExpandAll);

	/**
	 * Expectation(pure-function, data-set) - returns the expected value of the `pure-function` for the given
	 * `data-set`.
	 */
	public final static IBuiltInSymbol Expectation = F.initFinalSymbol("Expectation", ID.Expectation);

	/**
	 * Exponent(polynomial, x) - gives the maximum power with which `x` appears in the expanded form of `polynomial`.
	 */
	public final static IBuiltInSymbol Exponent = F.initFinalSymbol("Exponent", ID.Exponent);

	/** ExponentialDistribution(lambda) - returns an exponential distribution. */
	public final static IBuiltInSymbol ExponentialDistribution = F.initFinalSymbol("ExponentialDistribution",
			ID.ExponentialDistribution);

	/**
	 * Export("path-to-filename", expression, "WXF") - if the file system is enabled, export the `expression` in WXF
	 * format to the "path-to-filename" file.
	 */
	public final static IBuiltInSymbol Export = F.initFinalSymbol("Export", ID.Export);

	/***/
	public final static IBuiltInSymbol ExportString = F.initFinalSymbol("ExportString", ID.ExportString);

	/***/
	public final static IBuiltInSymbol Expression = F.initFinalSymbol("Expression", ID.Expression);

	/** ExtendedGCD(n1, n2, ...) - computes the extended greatest common divisor of the given integers. */
	public final static IBuiltInSymbol ExtendedGCD = F.initFinalSymbol("ExtendedGCD", ID.ExtendedGCD);

	/***/
	public final static IBuiltInSymbol Extension = F.initFinalSymbol("Extension", ID.Extension);

	/** Extract(expr, list) - extracts parts of `expr` specified by `list`. */
	public final static IBuiltInSymbol Extract = F.initFinalSymbol("Extract", ID.Extract);

	/***/
	public final static IBuiltInSymbol FRatioDistribution = F.initFinalSymbol("FRatioDistribution",
			ID.FRatioDistribution);

	/** Factor(expr) - factors the polynomial expression `expr` */
	public final static IBuiltInSymbol Factor = F.initFinalSymbol("Factor", ID.Factor);

	/** FactorInteger(n) - returns the factorization of `n` as a list of factors and exponents. */
	public final static IBuiltInSymbol FactorInteger = F.initFinalSymbol("FactorInteger", ID.FactorInteger);

	/** FactorSquareFree(polynomial) - factor the polynomial expression `polynomial` square free. */
	public final static IBuiltInSymbol FactorSquareFree = F.initFinalSymbol("FactorSquareFree", ID.FactorSquareFree);

	/** FactorSquareFreeList(polynomial) - get the square free factors of the polynomial expression `polynomial`. */
	public final static IBuiltInSymbol FactorSquareFreeList = F.initFinalSymbol("FactorSquareFreeList",
			ID.FactorSquareFreeList);

	/** FactorTerms(poly) - pulls out any overall numerical factor in `poly`. */
	public final static IBuiltInSymbol FactorTerms = F.initFinalSymbol("FactorTerms", ID.FactorTerms);

	/** Factorial(n) - returns the factorial number of the integer `n` */
	public final static IBuiltInSymbol Factorial = F.initFinalSymbol("Factorial", ID.Factorial);

	/** Factorial2(n) - returns the double factorial number of the integer `n`. */
	public final static IBuiltInSymbol Factorial2 = F.initFinalSymbol("Factorial2", ID.Factorial2);

	/***/
	public final static IBuiltInSymbol FactorialPower = F.initFinalSymbol("FactorialPower", ID.FactorialPower);

	/** False - the constant `False` represents the boolean value **false ***/
	public final static IBuiltInSymbol False = F.initFinalSymbol("False", ID.False);

	/** Fibonacci(n) - returns the Fibonacci number of the integer `n` */
	public final static IBuiltInSymbol Fibonacci = F.initFinalSymbol("Fibonacci", ID.Fibonacci);

	/***/
	public final static IBuiltInSymbol FilterRules = F.initFinalSymbol("FilterRules", ID.FilterRules);

	/**
	 * FindClusters(list-of-data-points, k) - Clustering algorithm based on David Arthur and Sergei Vassilvitski
	 * k-means++ algorithm. Create `k` number of clusters to split the `list-of-data-points` into.
	 */
	public final static IBuiltInSymbol FindClusters = F.initFinalSymbol("FindClusters", ID.FindClusters);

	/***/
	public final static IBuiltInSymbol FindEdgeCover = F.initFinalSymbol("FindEdgeCover", ID.FindEdgeCover);

	/** FindEulerianCycle(graph) - find an eulerian cycle in the `graph`. */
	public final static IBuiltInSymbol FindEulerianCycle = F.initFinalSymbol("FindEulerianCycle", ID.FindEulerianCycle);

	/**
	 * FindFit(list-of-data-points, function, parameters, variable) - solve a least squares problem using the
	 * Levenberg-Marquardt algorithm.
	 */
	public final static IBuiltInSymbol FindFit = F.initFinalSymbol("FindFit", ID.FindFit);

	/***/
	public final static IBuiltInSymbol FindGraphCommunities = F.initFinalSymbol("FindGraphCommunities",
			ID.FindGraphCommunities);

	/** FindHamiltonianCycle(graph) - find an hamiltonian cycle in the `graph`. */
	public final static IBuiltInSymbol FindHamiltonianCycle = F.initFinalSymbol("FindHamiltonianCycle",
			ID.FindHamiltonianCycle);

	/***/
	public final static IBuiltInSymbol FindIndependentEdgeSet = F.initFinalSymbol("FindIndependentEdgeSet",
			ID.FindIndependentEdgeSet);

	/***/
	public final static IBuiltInSymbol FindIndependentVertexSet = F.initFinalSymbol("FindIndependentVertexSet",
			ID.FindIndependentVertexSet);

	/**
	 * FindInstance(equations, vars) - attempts to find one solution which solves the `equations` for the variables
	 * `vars`.
	 */
	public final static IBuiltInSymbol FindInstance = F.initFinalSymbol("FindInstance", ID.FindInstance);

	/**
	 * FindRoot(f, {x, xmin, xmax}) - searches for a numerical root of `f` for the variable `x`, in the range `xmin` to
	 * `xmax`.
	 */
	public final static IBuiltInSymbol FindRoot = F.initFinalSymbol("FindRoot", ID.FindRoot);

	/**
	 * FindShortestPath(graph, source, destination) - find a shortest path in the `graph` from `source` to
	 * `destination`.
	 */
	public final static IBuiltInSymbol FindShortestPath = F.initFinalSymbol("FindShortestPath", ID.FindShortestPath);

	/**
	 * FindShortestTour({{p11, p12}, {p21, p22}, {p31, p32}, ...}) - find a shortest tour in the `graph` with minimum
	 * `EuclideanDistance`.
	 */
	public final static IBuiltInSymbol FindShortestTour = F.initFinalSymbol("FindShortestTour", ID.FindShortestTour);

	/** FindSpanningTree(graph) - find the minimum spanning tree in the `graph`. */
	public final static IBuiltInSymbol FindSpanningTree = F.initFinalSymbol("FindSpanningTree", ID.FindSpanningTree);

	/**
	 * FindVertexCover(graph) - algorithm to find a vertex cover for a `graph`. A vertex cover is a set of vertices that
	 * touches all the edges in the graph.
	 */
	public final static IBuiltInSymbol FindVertexCover = F.initFinalSymbol("FindVertexCover", ID.FindVertexCover);

	/** First(expr) - returns the first element in `expr`. */
	public final static IBuiltInSymbol First = F.initFinalSymbol("First", ID.First);

	/**
	 * Fit(list-of-data-points, degree, variable) - solve a least squares problem using the Levenberg-Marquardt
	 * algorithm.
	 */
	public final static IBuiltInSymbol Fit = F.initFinalSymbol("Fit", ID.Fit);

	/** FittedModel( ) - `FittedModel`holds the model generated with `LinearModelFit` */
	public final static IBuiltInSymbol FittedModel = F.initFinalSymbol("FittedModel", ID.FittedModel);

	/**
	 * FiveNum({dataset}) - the Tuckey five-number summary is a set of descriptive statistics that provide information
	 * about a `dataset`. It consists of the five most important sample percentiles:
	 */
	public final static IBuiltInSymbol FiveNum = F.initFinalSymbol("FiveNum", ID.FiveNum);

	/** FixedPoint(f, expr) - starting with `expr`, iteratively applies `f` until the result no longer changes. */
	public final static IBuiltInSymbol FixedPoint = F.initFinalSymbol("FixedPoint", ID.FixedPoint);

	/**
	 * FixedPointList(f, expr) - starting with `expr`, iteratively applies `f` until the result no longer changes, and
	 * returns a list of all intermediate results.
	 */
	public final static IBuiltInSymbol FixedPointList = F.initFinalSymbol("FixedPointList", ID.FixedPointList);

	/**
	 * Flat - is an attribute that specifies that nested occurrences of a function should be automatically flattened.
	 */
	public final static IBuiltInSymbol Flat = F.initFinalSymbol("Flat", ID.Flat);

	/***/
	public final static IBuiltInSymbol FlatTopWindow = F.initFinalSymbol("FlatTopWindow", ID.FlatTopWindow);

	/** Flatten(expr) - flattens out nested lists in `expr`. */
	public final static IBuiltInSymbol Flatten = F.initFinalSymbol("Flatten", ID.Flatten);

	/** FlattenAt(expr, position) - flattens out nested lists at the given `position` in `expr`. */
	public final static IBuiltInSymbol FlattenAt = F.initFinalSymbol("FlattenAt", ID.FlattenAt);

	/***/
	public final static IBuiltInSymbol Float = F.initFinalSymbol("Float", ID.Float);

	/** Floor(expr) - gives the smallest integer less than or equal `expr`. */
	public final static IBuiltInSymbol Floor = F.initFinalSymbol("Floor", ID.Floor);

	/** Fold[f, x, {a, b}] - returns `f[f[x, a], b]`, and this nesting continues for lists of arbitrary length. */
	public final static IBuiltInSymbol Fold = F.initFinalSymbol("Fold", ID.Fold);

	/** FoldList[f, x, {a, b}] - returns `{x, f[x, a], f[f[x, a], b]}` */
	public final static IBuiltInSymbol FoldList = F.initFinalSymbol("FoldList", ID.FoldList);

	/**
	 * For(start, test, incr, body) - evaluates `start`, and then iteratively `body` and `incr` as long as test
	 * evaluates to `True`.
	 */
	public final static IBuiltInSymbol For = F.initFinalSymbol("For", ID.For);

	/***/
	public final static IBuiltInSymbol ForAll = F.initFinalSymbol("ForAll", ID.ForAll);

	/**
	 * Fourier(vector-of-complex-numbers) - Discrete Fourier transform of a `vector-of-complex-numbers`. Fourier
	 * transform is restricted to vectors with length of power of 2.
	 */
	public final static IBuiltInSymbol Fourier = F.initFinalSymbol("Fourier", ID.Fourier);

	/** FourierMatrix(n) - gives a fourier matrix with the dimension `n`. */
	public final static IBuiltInSymbol FourierMatrix = F.initFinalSymbol("FourierMatrix", ID.FourierMatrix);

	/** FractionalPart(number) - get the fractional part of a `number`. */
	public final static IBuiltInSymbol FractionalPart = F.initFinalSymbol("FractionalPart", ID.FractionalPart);

	/** FrechetDistribution(a,b) - returns a Frechet distribution. */
	public final static IBuiltInSymbol FrechetDistribution = F.initFinalSymbol("FrechetDistribution",
			ID.FrechetDistribution);

	/** FreeQ(`expr`, `x`) - returns 'True' if `expr` does not contain the expression `x`. */
	public final static IBuiltInSymbol FreeQ = F.initFinalSymbol("FreeQ", ID.FreeQ);

	/***/
	public final static IBuiltInSymbol FresnelC = F.initFinalSymbol("FresnelC", ID.FresnelC);

	/***/
	public final static IBuiltInSymbol FresnelS = F.initFinalSymbol("FresnelS", ID.FresnelS);

	/** FrobeniusNumber({a1, ... ,aN}) - returns the Frobenius number of the nonnegative integers `{a1, ... ,aN}` */
	public final static IBuiltInSymbol FrobeniusNumber = F.initFinalSymbol("FrobeniusNumber", ID.FrobeniusNumber);

	/**
	 * FrobeniusSolve({a1, ... ,aN}, M) - get a list of solutions for the Frobenius equation given by the list of
	 * integers `{a1, ... ,aN}` and the non-negative integer `M`.
	 */
	public final static IBuiltInSymbol FrobeniusSolve = F.initFinalSymbol("FrobeniusSolve", ID.FrobeniusSolve);

	/**
	 * FromCharacterCode({ch1, ch2, ...}) - converts the `ch1, ch2,...` character codes into a string of corresponding
	 * characters.
	 */
	public final static IBuiltInSymbol FromCharacterCode = F.initFinalSymbol("FromCharacterCode", ID.FromCharacterCode);

	/**
	 * FromContinuedFraction({n1, n2, ...}) - return the number which represents the continued fraction list `{n1, n2,
	 * ...}`.
	 */
	public final static IBuiltInSymbol FromContinuedFraction = F.initFinalSymbol("FromContinuedFraction",
			ID.FromContinuedFraction);

	/** FromDigits(list) - creates an expression from the list of digits for radix `10`. */
	public final static IBuiltInSymbol FromDigits = F.initFinalSymbol("FromDigits", ID.FromDigits);

	/** FromPolarCoordinates({r, t}) - return the cartesian coordinates for the polar coordinates `{r, t}`. */
	public final static IBuiltInSymbol FromPolarCoordinates = F.initFinalSymbol("FromPolarCoordinates",
			ID.FromPolarCoordinates);

	/***/
	public final static IBuiltInSymbol Full = F.initFinalSymbol("Full", ID.Full);

	/** FullForm(expression) - shows the internal representation of the given `expression`. */
	public final static IBuiltInSymbol FullForm = F.initFinalSymbol("FullForm", ID.FullForm);

	/**
	 * FullSimplify(expr) - works like `Simplify` but additionally tries some `FunctionExpand` rule transformations to
	 * simplify `expr`.
	 */
	public final static IBuiltInSymbol FullSimplify = F.initFinalSymbol("FullSimplify", ID.FullSimplify);

	/***/
	public final static IBuiltInSymbol Function = F.initFinalSymbol("Function", ID.Function);

	/**
	 * FunctionExpand(expression) - expands the special function `expression`. `FunctionExpand` expands simple nested
	 * radicals.
	 */
	public final static IBuiltInSymbol FunctionExpand = F.initFinalSymbol("FunctionExpand", ID.FunctionExpand);

	/***/
	public final static IBuiltInSymbol FunctionRange = F.initFinalSymbol("FunctionRange", ID.FunctionRange);

	/**
	 * FunctionURL(built-in-symbol) - returns the GitHub URL of the `built-in-symbol` implementation in the [Symja
	 * GitHub repository](https://github.com/axkr/symja_android_library).
	 */
	public final static IBuiltInSymbol FunctionURL = F.initFinalSymbol("FunctionURL", ID.FunctionURL);

	/** GCD(n1, n2, ...) - computes the greatest common divisor of the given integers. */
	public final static IBuiltInSymbol GCD = F.initFinalSymbol("GCD", ID.GCD);

	/** Gamma(z) - is the gamma function on the complex number `z`. */
	public final static IBuiltInSymbol Gamma = F.initFinalSymbol("Gamma", ID.Gamma);

	/** GammaDistribution(a,b) - returns a gamma distribution. */
	public final static IBuiltInSymbol GammaDistribution = F.initFinalSymbol("GammaDistribution", ID.GammaDistribution);

	/***/
	public final static IBuiltInSymbol GammaRegularized = F.initFinalSymbol("GammaRegularized", ID.GammaRegularized);

	/** Gather(list, test) - gathers leaves of `list` into sub lists of items that are the same according to `test`. */
	public final static IBuiltInSymbol Gather = F.initFinalSymbol("Gather", ID.Gather);

	/** GatherBy(list, f) - gathers leaves of `list` into sub lists of items whose image under `f` identical. */
	public final static IBuiltInSymbol GatherBy = F.initFinalSymbol("GatherBy", ID.GatherBy);

	/***/
	public final static IBuiltInSymbol GaussianIntegers = F.initFinalSymbol("GaussianIntegers", ID.GaussianIntegers);

	/***/
	public final static IBuiltInSymbol GaussianMatrix = F.initFinalSymbol("GaussianMatrix", ID.GaussianMatrix);

	/***/
	public final static IBuiltInSymbol GaussianWindow = F.initFinalSymbol("GaussianWindow", ID.GaussianWindow);

	/** GegenbauerC(n, a, x) - returns the GegenbauerC polynomial. */
	public final static IBuiltInSymbol GegenbauerC = F.initFinalSymbol("GegenbauerC", ID.GegenbauerC);

	/***/
	public final static IBuiltInSymbol General = F.initFinalSymbol("General", ID.General);

	/**
	 * GeoDistance({latitude1,longitude1}, {latitude2,longitude2}) - returns the geodesic distance between
	 * `{latitude1,longitude1}` and `{latitude2,longitude2}`.
	 */
	public final static IBuiltInSymbol GeoDistance = F.initFinalSymbol("GeoDistance", ID.GeoDistance);

	/***/
	public final static IBuiltInSymbol GeoPosition = F.initFinalSymbol("GeoPosition", ID.GeoPosition);

	/***/
	public final static IBuiltInSymbol GeodesyData = F.initFinalSymbol("GeodesyData", ID.GeodesyData);

	/** GeometricDistribution(p) - returns a geometric distribution. */
	public final static IBuiltInSymbol GeometricDistribution = F.initFinalSymbol("GeometricDistribution",
			ID.GeometricDistribution);

	/** GeometricMean({a, b, c,...}) - returns the geometric mean of `{a, b, c,...}`. */
	public final static IBuiltInSymbol GeometricMean = F.initFinalSymbol("GeometricMean", ID.GeometricMean);

	/** Get("path-to-package-file-name") - load the package defined in `path-to-package-file-name`. */
	public final static IBuiltInSymbol Get = F.initFinalSymbol("Get", ID.Get);

	/** Glaisher - Glaisher constant. */
	public final static IBuiltInSymbol Glaisher = F.initFinalSymbol("Glaisher", ID.Glaisher);

	/***/
	public final static IBuiltInSymbol GoldenAngle = F.initFinalSymbol("GoldenAngle", ID.GoldenAngle);

	/** GoldenRatio - is the golden ratio `(1+Sqrt(5))/2`. */
	public final static IBuiltInSymbol GoldenRatio = F.initFinalSymbol("GoldenRatio", ID.GoldenRatio);

	/***/
	public final static IBuiltInSymbol GompertzMakehamDistribution = F.initFinalSymbol("GompertzMakehamDistribution",
			ID.GompertzMakehamDistribution);

	/** Grad(function, list-of-variables) - gives the gradient of the function. */
	public final static IBuiltInSymbol Grad = F.initFinalSymbol("Grad", ID.Grad);

	/** Graph({edge1,...,edgeN}) - create a graph from the given edges `edge1,...,edgeN`. */
	public final static IBuiltInSymbol Graph = F.initFinalSymbol("Graph", ID.Graph);

	/**
	 * GraphCenter(graph) - compute the `graph` center. The center of a `graph` is the set of vertices of graph
	 * eccentricity equal to the `graph` radius.
	 */
	public final static IBuiltInSymbol GraphCenter = F.initFinalSymbol("GraphCenter", ID.GraphCenter);

	/***/
	public final static IBuiltInSymbol GraphData = F.initFinalSymbol("GraphData", ID.GraphData);

	/** GraphDiameter(graph) - return the diameter of the `graph`. */
	public final static IBuiltInSymbol GraphDiameter = F.initFinalSymbol("GraphDiameter", ID.GraphDiameter);

	/**
	 * GraphPeriphery(graph) - compute the `graph` periphery. The periphery of a `graph` is the set of vertices of graph
	 * eccentricity equal to the graph diameter.
	 */
	public final static IBuiltInSymbol GraphPeriphery = F.initFinalSymbol("GraphPeriphery", ID.GraphPeriphery);

	/** GraphQ(expr) - test if `expr` is a graph object. */
	public final static IBuiltInSymbol GraphQ = F.initFinalSymbol("GraphQ", ID.GraphQ);

	/** GraphRadius(graph) - return the radius of the `graph`. */
	public final static IBuiltInSymbol GraphRadius = F.initFinalSymbol("GraphRadius", ID.GraphRadius);

	/***/
	public final static IBuiltInSymbol Graphics = F.initFinalSymbol("Graphics", ID.Graphics);

	/***/
	public final static IBuiltInSymbol Graphics3D = F.initFinalSymbol("Graphics3D", ID.Graphics3D);

	/***/
	public final static IBuiltInSymbol Gray = F.initFinalSymbol("Gray", ID.Gray);

	/***/
	public final static IBuiltInSymbol GrayLevel = F.initFinalSymbol("GrayLevel", ID.GrayLevel);

	/** Greater(x, y) - yields `True` if `x` is known to be greater than `y`. */
	public final static IBuiltInSymbol Greater = F.initFinalSymbol("Greater", ID.Greater);

	/** GreaterEqual(x, y) - yields `True` if `x` is known to be greater than or equal to `y`. */
	public final static IBuiltInSymbol GreaterEqual = F.initFinalSymbol("GreaterEqual", ID.GreaterEqual);

	/***/
	public final static IBuiltInSymbol Green = F.initFinalSymbol("Green", ID.Green);

	/**
	 * GroebnerBasis({polynomial-list},{variable-list}) - returns a Gröbner basis for the `polynomial-list` and
	 * `variable-list`.
	 */
	public final static IBuiltInSymbol GroebnerBasis = F.initFinalSymbol("GroebnerBasis", ID.GroebnerBasis);

	/***/
	public final static IBuiltInSymbol GroupBy = F.initFinalSymbol("GroupBy", ID.GroupBy);

	/** GumbelDistribution(a, b) - returns a Gumbel distribution. */
	public final static IBuiltInSymbol GumbelDistribution = F.initFinalSymbol("GumbelDistribution",
			ID.GumbelDistribution);

	/** HamiltonianGraphQ(graph) - returns `True` if `graph` is an hamiltonian graph, and `False` otherwise. */
	public final static IBuiltInSymbol HamiltonianGraphQ = F.initFinalSymbol("HamiltonianGraphQ", ID.HamiltonianGraphQ);

	/***/
	public final static IBuiltInSymbol HammingWindow = F.initFinalSymbol("HammingWindow", ID.HammingWindow);

	/***/
	public final static IBuiltInSymbol HankelH1 = F.initFinalSymbol("HankelH1", ID.HankelH1);

	/***/
	public final static IBuiltInSymbol HankelH2 = F.initFinalSymbol("HankelH2", ID.HankelH2);

	/***/
	public final static IBuiltInSymbol HannWindow = F.initFinalSymbol("HannWindow", ID.HannWindow);

	/** HarmonicMean({a, b, c,...}) - returns the harmonic mean of `{a, b, c,...}`. */
	public final static IBuiltInSymbol HarmonicMean = F.initFinalSymbol("HarmonicMean", ID.HarmonicMean);

	/** HarmonicNumber(n) - returns the `n`th harmonic number. */
	public final static IBuiltInSymbol HarmonicNumber = F.initFinalSymbol("HarmonicNumber", ID.HarmonicNumber);

	/** Haversine(z) - returns the haversine function of `z`. */
	public final static IBuiltInSymbol Haversine = F.initFinalSymbol("Haversine", ID.Haversine);

	/** Head(expr) - returns the head of the expression or atom `expr`. */
	public final static IBuiltInSymbol Head = F.initFinalSymbol("Head", ID.Head);

	/***/
	public final static IBuiltInSymbol Heads = F.initFinalSymbol("Heads", ID.Heads);

	/**
	 * HeavisideTheta(expr1, expr2, ... exprN) - returns `1` if all `expr1, expr2, ... exprN` are positive and `0` if
	 * one of the `expr1, expr2, ... exprN` is negative. `HeavisideTheta(0)` returns unevaluated as `HeavisideTheta(0)`.
	 */
	public final static IBuiltInSymbol HeavisideTheta = F.initFinalSymbol("HeavisideTheta", ID.HeavisideTheta);

	/** HermiteH(n, x) - returns the Hermite polynomial `H_n(x)`. */
	public final static IBuiltInSymbol HermiteH = F.initFinalSymbol("HermiteH", ID.HermiteH);

	/** HermitianMatrixQ(m) - returns `True` if `m` is a hermitian matrix. */
	public final static IBuiltInSymbol HermitianMatrixQ = F.initFinalSymbol("HermitianMatrixQ", ID.HermitianMatrixQ);

	/***/
	public final static IBuiltInSymbol HexidecimalCharacter = F.initFinalSymbol("HexidecimalCharacter",
			ID.HexidecimalCharacter);

	/** HilbertMatrix(n) - gives the hilbert matrix with `n` rows and columns. */
	public final static IBuiltInSymbol HilbertMatrix = F.initFinalSymbol("HilbertMatrix", ID.HilbertMatrix);

	/** Histogram(list-of-values) - plots a histogram for a `list-of-values` */
	public final static IBuiltInSymbol Histogram = F.initFinalSymbol("Histogram", ID.Histogram);

	/** Hold(expr) - `Hold` doesn't evaluate `expr`. */
	public final static IBuiltInSymbol Hold = F.initFinalSymbol("Hold", ID.Hold);

	/** HoldAll - is an attribute specifying that all arguments of a function should be left unevaluated. */
	public final static IBuiltInSymbol HoldAll = F.initFinalSymbol("HoldAll", ID.HoldAll);

	/***/
	public final static IBuiltInSymbol HoldAllComplete = F.initFinalSymbol("HoldAllComplete", ID.HoldAllComplete);

	/***/
	public final static IBuiltInSymbol HoldComplete = F.initFinalSymbol("HoldComplete", ID.HoldComplete);

	/** HoldFirst - is an attribute specifying that the first argument of a function should be left unevaluated. */
	public final static IBuiltInSymbol HoldFirst = F.initFinalSymbol("HoldFirst", ID.HoldFirst);

	/** HoldForm(expr) - `HoldForm` doesn't evaluate `expr` and didn't appear in the output. */
	public final static IBuiltInSymbol HoldForm = F.initFinalSymbol("HoldForm", ID.HoldForm);

	/** HoldPattern(expr) - `HoldPattern` doesn't evaluate `expr` for pattern-matching. */
	public final static IBuiltInSymbol HoldPattern = F.initFinalSymbol("HoldPattern", ID.HoldPattern);

	/**
	 * HoldRest - is an attribute specifying that all but the first argument of a function should be left unevaluated.
	 */
	public final static IBuiltInSymbol HoldRest = F.initFinalSymbol("HoldRest", ID.HoldRest);

	/***/
	public final static IBuiltInSymbol Horner = F.initFinalSymbol("Horner", ID.Horner);

	/** HornerForm(polynomial) - Generate the horner scheme for a univariate `polynomial`. */
	public final static IBuiltInSymbol HornerForm = F.initFinalSymbol("HornerForm", ID.HornerForm);

	/***/
	public final static IBuiltInSymbol Hue = F.initFinalSymbol("Hue", ID.Hue);

	/** HurwitzZeta(s, a) - returns the Hurwitz zeta function. */
	public final static IBuiltInSymbol HurwitzZeta = F.initFinalSymbol("HurwitzZeta", ID.HurwitzZeta);

	/** Hypergeometric0F1(b, z) - return the `Hypergeometric0F1` function */
	public final static IBuiltInSymbol Hypergeometric0F1 = F.initFinalSymbol("Hypergeometric0F1", ID.Hypergeometric0F1);

	/** Hypergeometric1F1(a, b, z) - return the `Hypergeometric1F1` function */
	public final static IBuiltInSymbol Hypergeometric1F1 = F.initFinalSymbol("Hypergeometric1F1", ID.Hypergeometric1F1);

	/***/
	public final static IBuiltInSymbol Hypergeometric1F1Regularized = F.initFinalSymbol("Hypergeometric1F1Regularized",
			ID.Hypergeometric1F1Regularized);

	/** Hypergeometric2F1(a, b, c, z) - return the `Hypergeometric2F1` function */
	public final static IBuiltInSymbol Hypergeometric2F1 = F.initFinalSymbol("Hypergeometric2F1", ID.Hypergeometric2F1);

	/** HypergeometricDistribution(n, s, t) - returns a hypergeometric distribution. */
	public final static IBuiltInSymbol HypergeometricDistribution = F.initFinalSymbol("HypergeometricDistribution",
			ID.HypergeometricDistribution);

	/** HypergeometricPFQ({a,...}, {b,...}, c) - return the `HypergeometricPFQ` function */
	public final static IBuiltInSymbol HypergeometricPFQ = F.initFinalSymbol("HypergeometricPFQ", ID.HypergeometricPFQ);

	/***/
	public final static IBuiltInSymbol HypergeometricPFQRegularized = F.initFinalSymbol("HypergeometricPFQRegularized",
			ID.HypergeometricPFQRegularized);

	/***/
	public final static IBuiltInSymbol HypergeometricU = F.initFinalSymbol("HypergeometricU", ID.HypergeometricU);

	/**
	 * I - Imaginary unit - internally converted to the complex number `0+1*i`. `I` represents the imaginary number
	 * `Sqrt(-1)`. `I^2` will be evaluated to `-1`.
	 */
	public final static IBuiltInSymbol I = F.initFinalSymbol("I", ID.I);

	/** Identity(expr) - returns `expr`. */
	public final static IBuiltInSymbol Identity = F.initFinalSymbol("Identity", ID.Identity);

	/** IdentityMatrix(n) - gives the identity matrix with `n` rows and columns. */
	public final static IBuiltInSymbol IdentityMatrix = F.initFinalSymbol("IdentityMatrix", ID.IdentityMatrix);

	/** If(cond, pos, neg) - returns `pos` if `cond` evaluates to `True`, and `neg` if it evaluates to `False`. */
	public final static IBuiltInSymbol If = F.initFinalSymbol("If", ID.If);

	/***/
	public final static IBuiltInSymbol IgnoreCase = F.initFinalSymbol("IgnoreCase", ID.IgnoreCase);

	/** Im(z) - returns the imaginary component of the complex number `z`. */
	public final static IBuiltInSymbol Im = F.initFinalSymbol("Im", ID.Im);

	/** Implies(arg1, arg2) - Logical implication. */
	public final static IBuiltInSymbol Implies = F.initFinalSymbol("Implies", ID.Implies);

	/**
	 * Import("path-to-filename", "WXF") - if the file system is enabled, import an expression in WXF format from the
	 * "path-to-filename" file.
	 */
	public final static IBuiltInSymbol Import = F.initFinalSymbol("Import", ID.Import);

	/** Increment(x) - increments `x` by `1`, returning the original value of `x`. */
	public final static IBuiltInSymbol Increment = F.initFinalSymbol("Increment", ID.Increment);

	/** Indeterminate - represents an indeterminate result. */
	public final static IBuiltInSymbol Indeterminate = F.initFinalSymbol("Indeterminate", ID.Indeterminate);

	/***/
	public final static IBuiltInSymbol Inequality = F.initFinalSymbol("Inequality", ID.Inequality);

	/** InexactNumberQ(expr) - returns `True` if `expr` is not an exact number, and `False` otherwise. */
	public final static IBuiltInSymbol InexactNumberQ = F.initFinalSymbol("InexactNumberQ", ID.InexactNumberQ);

	/** Infinity - represents an infinite real quantity. */
	public final static IBuiltInSymbol Infinity = F.initFinalSymbol("Infinity", ID.Infinity);

	/***/
	public final static IBuiltInSymbol Infix = F.initFinalSymbol("Infix", ID.Infix);

	/***/
	public final static IBuiltInSymbol Information = F.initFinalSymbol("Information", ID.Information);

	/**
	 * Inner(f, x, y, g) - computes a generalized inner product of `x` and `y`, using a multiplication function `f` and
	 * an addition function `g`.
	 */
	public final static IBuiltInSymbol Inner = F.initFinalSymbol("Inner", ID.Inner);

	/**
	 * Input() - if the file system is enabled, the user can input an expression. After input this expression will be
	 * evaluated immediately.
	 */
	public final static IBuiltInSymbol Input = F.initFinalSymbol("Input", ID.Input);

	/***/
	public final static IBuiltInSymbol InputField = F.initFinalSymbol("InputField", ID.InputField);

	/***/
	public final static IBuiltInSymbol InputForm = F.initFinalSymbol("InputForm", ID.InputForm);

	/** InputString() - if the file system is enabled, the user can input a string. */
	public final static IBuiltInSymbol InputString = F.initFinalSymbol("InputString", ID.InputString);

	/***/
	public final static IBuiltInSymbol Insert = F.initFinalSymbol("Insert", ID.Insert);

	/** Integer - is the head of integers. */
	public final static IBuiltInSymbol Integer = F.initFinalSymbol("Integer", ID.Integer);

	/** IntegerDigits(n, base) - returns a list of integer digits for `n` under `base`. */
	public final static IBuiltInSymbol IntegerDigits = F.initFinalSymbol("IntegerDigits", ID.IntegerDigits);

	/** IntegerExponent(n, b) - gives the highest exponent of `b` that divides `n`. */
	public final static IBuiltInSymbol IntegerExponent = F.initFinalSymbol("IntegerExponent", ID.IntegerExponent);

	/** IntegerLength(x) - gives the number of digits in the base-10 representation of `x`. */
	public final static IBuiltInSymbol IntegerLength = F.initFinalSymbol("IntegerLength", ID.IntegerLength);

	/** IntegerName(integer-number) - gives the spoken number string of `integer-number` in language `English`. */
	public final static IBuiltInSymbol IntegerName = F.initFinalSymbol("IntegerName", ID.IntegerName);

	/** IntegerPart(expr) - for real `expr` return the integer part of `expr`. */
	public final static IBuiltInSymbol IntegerPart = F.initFinalSymbol("IntegerPart", ID.IntegerPart);

	/** IntegerPartitions(n) - returns all partitions of the integer `n`. */
	public final static IBuiltInSymbol IntegerPartitions = F.initFinalSymbol("IntegerPartitions", ID.IntegerPartitions);

	/** IntegerQ(expr) - returns `True` if `expr` is an integer, and `False` otherwise. */
	public final static IBuiltInSymbol IntegerQ = F.initFinalSymbol("IntegerQ", ID.IntegerQ);

	/** Integers - is the set of integer numbers. */
	public final static IBuiltInSymbol Integers = F.initFinalSymbol("Integers", ID.Integers);

	/**
	 * Integrate(f, x) - integrates `f` with respect to `x`. The result does not contain the additive integration
	 * constant.
	 */
	public final static IBuiltInSymbol Integrate = F.initFinalSymbol("Integrate", ID.Integrate);

	/**
	 * InterpolatingFunction(data-list) - get the representation for the given `data-list` as piecewise
	 * `InterpolatingPolynomial`s.
	 */
	public final static IBuiltInSymbol InterpolatingFunction = F.initFinalSymbol("InterpolatingFunction",
			ID.InterpolatingFunction);

	/** InterpolatingPolynomial(data-list, symbol) - get the polynomial representation for the given `data-list`. */
	public final static IBuiltInSymbol InterpolatingPolynomial = F.initFinalSymbol("InterpolatingPolynomial",
			ID.InterpolatingPolynomial);

	/***/
	public final static IBuiltInSymbol Interpolation = F.initFinalSymbol("Interpolation", ID.Interpolation);

	/** Interrupt( ) - Interrupt an evaluation and returns `$Aborted`. */
	public final static IBuiltInSymbol Interrupt = F.initFinalSymbol("Interrupt", ID.Interrupt);

	/***/
	public final static IBuiltInSymbol IntersectingQ = F.initFinalSymbol("IntersectingQ", ID.IntersectingQ);

	/** Intersection(set1, set2, ...) - get the intersection set from `set1` and `set2` .... */
	public final static IBuiltInSymbol Intersection = F.initFinalSymbol("Intersection", ID.Intersection);

	/** Interval({a, b}) - represents the interval from `a` to `b`. */
	public final static IBuiltInSymbol Interval = F.initFinalSymbol("Interval", ID.Interval);

	/**
	 * IntervalIntersection(interval_1, interval_2, ...) - compute the intersection of the intervals `interval_1,
	 * interval_2, ...`
	 */
	public final static IBuiltInSymbol IntervalIntersection = F.initFinalSymbol("IntervalIntersection",
			ID.IntervalIntersection);

	/**
	 * IntervalMemberQ(interval, interval-or-real-number) - returns `True`, if `interval-or-real-number` is completly
	 * sourrounded by `interval`
	 */
	public final static IBuiltInSymbol IntervalMemberQ = F.initFinalSymbol("IntervalMemberQ", ID.IntervalMemberQ);

	/** IntervalUnion(interval_1, interval_2, ...) - compute the union of the intervals `interval_1, interval_2, ...` */
	public final static IBuiltInSymbol IntervalUnion = F.initFinalSymbol("IntervalUnion", ID.IntervalUnion);

	/** Inverse(matrix) - computes the inverse of the `matrix`. */
	public final static IBuiltInSymbol Inverse = F.initFinalSymbol("Inverse", ID.Inverse);

	/***/
	public final static IBuiltInSymbol InverseBetaRegularized = F.initFinalSymbol("InverseBetaRegularized",
			ID.InverseBetaRegularized);

	/**
	 * InverseCDF(dist, q) - returns the inverse cumulative distribution for the distribution `dist` as a function of
	 * `q`
	 */
	public final static IBuiltInSymbol InverseCDF = F.initFinalSymbol("InverseCDF", ID.InverseCDF);

	/** InverseErf(z) - returns the inverse error function of `z`. */
	public final static IBuiltInSymbol InverseErf = F.initFinalSymbol("InverseErf", ID.InverseErf);

	/** InverseErfc(z) - returns the inverse complementary error function of `z`. */
	public final static IBuiltInSymbol InverseErfc = F.initFinalSymbol("InverseErfc", ID.InverseErfc);

	/**
	 * InverseFourier(vector-of-complex-numbers) - Inverse discrete Fourier transform of a `vector-of-complex-numbers`.
	 * Fourier transform is restricted to vectors with length of power of 2.
	 */
	public final static IBuiltInSymbol InverseFourier = F.initFinalSymbol("InverseFourier", ID.InverseFourier);

	/** InverseFunction(head) - returns the inverse function for the symbol `head`. */
	public final static IBuiltInSymbol InverseFunction = F.initFinalSymbol("InverseFunction", ID.InverseFunction);

	/***/
	public final static IBuiltInSymbol InverseGammaRegularized = F.initFinalSymbol("InverseGammaRegularized",
			ID.InverseGammaRegularized);

	/** InverseHaversine(z) - returns the inverse haversine function of `z`. */
	public final static IBuiltInSymbol InverseHaversine = F.initFinalSymbol("InverseHaversine", ID.InverseHaversine);

	/** InverseLaplaceTransform(f,s,t) - returns the inverse laplace transform. */
	public final static IBuiltInSymbol InverseLaplaceTransform = F.initFinalSymbol("InverseLaplaceTransform",
			ID.InverseLaplaceTransform);

	/** InverseSeries( series ) - return the inverse series. */
	public final static IBuiltInSymbol InverseSeries = F.initFinalSymbol("InverseSeries", ID.InverseSeries);

	/***/
	public final static IBuiltInSymbol InverseWeierstrassP = F.initFinalSymbol("InverseWeierstrassP",
			ID.InverseWeierstrassP);

	/** JSForm(expr) - returns the JavaScript form of the `expr`. */
	public final static IBuiltInSymbol JSForm = F.initFinalSymbol("JSForm", ID.JSForm);

	/***/
	public final static IBuiltInSymbol JSFormData = F.initFinalSymbol("JSFormData", ID.JSFormData);

	/**
	 * JaccardDissimilarity(u, v) - returns the Jaccard-Needham dissimilarity between the two boolean 1-D lists `u` and
	 * `v`, which is defined as `(c_tf + c_ft) / (c_tt + c_ft + c_tf)`, where n is `len(u)` and `c_ij` is the number of
	 * occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
	 */
	public final static IBuiltInSymbol JaccardDissimilarity = F.initFinalSymbol("JaccardDissimilarity",
			ID.JaccardDissimilarity);

	/** JacobiAmplitude(x, m) - returns the amplitude `am(x, m)` for Jacobian elliptic function. */
	public final static IBuiltInSymbol JacobiAmplitude = F.initFinalSymbol("JacobiAmplitude", ID.JacobiAmplitude);

	/***/
	public final static IBuiltInSymbol JacobiCD = F.initFinalSymbol("JacobiCD", ID.JacobiCD);

	/** JacobiCN(x, m) - returns the Jacobian elliptic function `cn(x, m)`. */
	public final static IBuiltInSymbol JacobiCN = F.initFinalSymbol("JacobiCN", ID.JacobiCN);

	/***/
	public final static IBuiltInSymbol JacobiDC = F.initFinalSymbol("JacobiDC", ID.JacobiDC);

	/** JacobiDN(x, m) - returns the Jacobian elliptic function `dn(x, m)`. */
	public final static IBuiltInSymbol JacobiDN = F.initFinalSymbol("JacobiDN", ID.JacobiDN);

	/** JacobiMatrix(matrix, var) - creates a Jacobian matrix. */
	public final static IBuiltInSymbol JacobiMatrix = F.initFinalSymbol("JacobiMatrix", ID.JacobiMatrix);

	/***/
	public final static IBuiltInSymbol JacobiNC = F.initFinalSymbol("JacobiNC", ID.JacobiNC);

	/***/
	public final static IBuiltInSymbol JacobiND = F.initFinalSymbol("JacobiND", ID.JacobiND);

	/***/
	public final static IBuiltInSymbol JacobiSC = F.initFinalSymbol("JacobiSC", ID.JacobiSC);

	/***/
	public final static IBuiltInSymbol JacobiSD = F.initFinalSymbol("JacobiSD", ID.JacobiSD);

	/** JacobiSN(x, m) - returns the Jacobian elliptic function `sn(x, m)`. */
	public final static IBuiltInSymbol JacobiSN = F.initFinalSymbol("JacobiSN", ID.JacobiSN);

	/** JacobiSymbol(m, n) - calculates the Jacobi symbol. */
	public final static IBuiltInSymbol JacobiSymbol = F.initFinalSymbol("JacobiSymbol", ID.JacobiSymbol);

	/***/
	public final static IBuiltInSymbol JacobiZeta = F.initFinalSymbol("JacobiZeta", ID.JacobiZeta);

	/**
	 * JavaForm(expr) - returns the Symja Java form of the `expr`. In Java you can use the created Symja expressions.
	 */
	public final static IBuiltInSymbol JavaForm = F.initFinalSymbol("JavaForm", ID.JavaForm);

	/** Join(l1, l2) - concatenates the lists `l1` and `l2`. */
	public final static IBuiltInSymbol Join = F.initFinalSymbol("Join", ID.Join);

	/***/
	public final static IBuiltInSymbol KOrderlessPartitions = F.initFinalSymbol("KOrderlessPartitions",
			ID.KOrderlessPartitions);

	/***/
	public final static IBuiltInSymbol KPartitions = F.initFinalSymbol("KPartitions", ID.KPartitions);

	/***/
	public final static IBuiltInSymbol Key = F.initFinalSymbol("Key", ID.Key);

	/***/
	public final static IBuiltInSymbol KeyExistsQ = F.initFinalSymbol("KeyExistsQ", ID.KeyExistsQ);

	/** KeySort(<|key1->value1, ...|>) - sort the `<|key1->value1, ...|>` entries by the `key` values. */
	public final static IBuiltInSymbol KeySort = F.initFinalSymbol("KeySort", ID.KeySort);

	/***/
	public final static IBuiltInSymbol KeyTake = F.initFinalSymbol("KeyTake", ID.KeyTake);

	/** Keys(association) - return a list of keys of the `association`. */
	public final static IBuiltInSymbol Keys = F.initFinalSymbol("Keys", ID.Keys);

	/** Khinchin - Khinchin's constant */
	public final static IBuiltInSymbol Khinchin = F.initFinalSymbol("Khinchin", ID.Khinchin);

	/***/
	public final static IBuiltInSymbol KleinInvariantJ = F.initFinalSymbol("KleinInvariantJ", ID.KleinInvariantJ);

	/***/
	public final static IBuiltInSymbol KnownUnitQ = F.initFinalSymbol("KnownUnitQ", ID.KnownUnitQ);

	/**
	 * KolmogorovSmirnovTest(data) - Computes the `p-value`, or <i>observed significance level</i>, of a one-sample
	 * [Wikipedia:Kolmogorov-Smirnov test](http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test) evaluating the null
	 * hypothesis that `data` conforms to the `NormalDistribution()`.
	 */
	public final static IBuiltInSymbol KolmogorovSmirnovTest = F.initFinalSymbol("KolmogorovSmirnovTest",
			ID.KolmogorovSmirnovTest);

	/**
	 * KroneckerDelta(arg1, arg2, ... argN) - if all arguments `arg1` to `argN` are equal return `1`, otherwise return
	 * `0`.
	 */
	public final static IBuiltInSymbol KroneckerDelta = F.initFinalSymbol("KroneckerDelta", ID.KroneckerDelta);

	/** Kurtosis(list) - gives the Pearson measure of kurtosis for `list` (a measure of existing outliers). */
	public final static IBuiltInSymbol Kurtosis = F.initFinalSymbol("Kurtosis", ID.Kurtosis);

	/** LCM(n1, n2, ...) - computes the least common multiple of the given integers. */
	public final static IBuiltInSymbol LCM = F.initFinalSymbol("LCM", ID.LCM);

	/** LUDecomposition(matrix) - calculate the LUP-decomposition of a square `matrix`. */
	public final static IBuiltInSymbol LUDecomposition = F.initFinalSymbol("LUDecomposition", ID.LUDecomposition);

	/** LaguerreL(n, x) - returns the Laguerre polynomial `L_n(x)`. */
	public final static IBuiltInSymbol LaguerreL = F.initFinalSymbol("LaguerreL", ID.LaguerreL);

	/** LaplaceTransform(f,t,s) - returns the laplace transform. */
	public final static IBuiltInSymbol LaplaceTransform = F.initFinalSymbol("LaplaceTransform", ID.LaplaceTransform);

	/** Last(expr) - returns the last element in `expr`. */
	public final static IBuiltInSymbol Last = F.initFinalSymbol("Last", ID.Last);

	/** LeafCount(expr) - returns the total number of indivisible subexpressions in `expr`. */
	public final static IBuiltInSymbol LeafCount = F.initFinalSymbol("LeafCount", ID.LeafCount);

	/** LeastSquares(matrix, right) - solves the linear least-squares problem 'matrix . x = right'. */
	public final static IBuiltInSymbol LeastSquares = F.initFinalSymbol("LeastSquares", ID.LeastSquares);

	/***/
	public final static IBuiltInSymbol Left = F.initFinalSymbol("Left", ID.Left);

	/** LegendreP(n, x) - returns the Legendre polynomial `P_n(x)`. */
	public final static IBuiltInSymbol LegendreP = F.initFinalSymbol("LegendreP", ID.LegendreP);

	/** LegendreQ(n, x) - returns the Legendre functions of the second kind `Q_n(x)`. */
	public final static IBuiltInSymbol LegendreQ = F.initFinalSymbol("LegendreQ", ID.LegendreQ);

	/** Length(expr) - returns the number of leaves in `expr`. */
	public final static IBuiltInSymbol Length = F.initFinalSymbol("Length", ID.Length);

	/** Less(x, y) - yields `True` if `x` is known to be less than `y`. */
	public final static IBuiltInSymbol Less = F.initFinalSymbol("Less", ID.Less);

	/** LessEqual(x, y) - yields `True` if `x` is known to be less than or equal `y`. */
	public final static IBuiltInSymbol LessEqual = F.initFinalSymbol("LessEqual", ID.LessEqual);

	/***/
	public final static IBuiltInSymbol LetterCharacter = F.initFinalSymbol("LetterCharacter", ID.LetterCharacter);

	/**
	 * LetterCounts(string) - count the number of each distinct character in the `string` and return the result as an
	 * association `<|char->counter1, ...|>`.
	 */
	public final static IBuiltInSymbol LetterCounts = F.initFinalSymbol("LetterCounts", ID.LetterCounts);

	/** LetterQ(expr) - tests whether `expr` is a string, which only contains letters. */
	public final static IBuiltInSymbol LetterQ = F.initFinalSymbol("LetterQ", ID.LetterQ);

	/**
	 * Level(expr, levelspec) - gives a list of all sub-expressions of `expr` at the level(s) specified by `levelspec`.
	 */
	public final static IBuiltInSymbol Level = F.initFinalSymbol("Level", ID.Level);

	/** LevelQ(expr) - tests whether `expr` is a valid level specification. */
	public final static IBuiltInSymbol LevelQ = F.initFinalSymbol("LevelQ", ID.LevelQ);

	/***/
	public final static IBuiltInSymbol Lexicographic = F.initFinalSymbol("Lexicographic", ID.Lexicographic);

	/***/
	public final static IBuiltInSymbol LightBlue = F.initFinalSymbol("LightBlue", ID.LightBlue);

	/***/
	public final static IBuiltInSymbol LightBrown = F.initFinalSymbol("LightBrown", ID.LightBrown);

	/***/
	public final static IBuiltInSymbol LightCyan = F.initFinalSymbol("LightCyan", ID.LightCyan);

	/***/
	public final static IBuiltInSymbol LightGray = F.initFinalSymbol("LightGray", ID.LightGray);

	/***/
	public final static IBuiltInSymbol LightGreen = F.initFinalSymbol("LightGreen", ID.LightGreen);

	/***/
	public final static IBuiltInSymbol LightMagenta = F.initFinalSymbol("LightMagenta", ID.LightMagenta);

	/***/
	public final static IBuiltInSymbol LightOrange = F.initFinalSymbol("LightOrange", ID.LightOrange);

	/***/
	public final static IBuiltInSymbol LightPink = F.initFinalSymbol("LightPink", ID.LightPink);

	/***/
	public final static IBuiltInSymbol LightPurple = F.initFinalSymbol("LightPurple", ID.LightPurple);

	/***/
	public final static IBuiltInSymbol LightRed = F.initFinalSymbol("LightRed", ID.LightRed);

	/***/
	public final static IBuiltInSymbol LightYellow = F.initFinalSymbol("LightYellow", ID.LightYellow);

	/** Limit(expr, x->x0) - gives the limit of `expr` as `x` approaches `x0` */
	public final static IBuiltInSymbol Limit = F.initFinalSymbol("Limit", ID.Limit);

	/***/
	public final static IBuiltInSymbol Line = F.initFinalSymbol("Line", ID.Line);

	/**
	 * LinearModelFit(list-of-data-points, expr, symbol) - In statistics, linear regression is a linear approach to
	 * modeling the relationship between a scalar response (or dependent variable) and one or more explanatory variables
	 * (or independent variables).
	 */
	public final static IBuiltInSymbol LinearModelFit = F.initFinalSymbol("LinearModelFit", ID.LinearModelFit);

	/**
	 * LinearProgramming(coefficientsOfLinearObjectiveFunction, constraintList, constraintRelationList) - the
	 * `LinearProgramming` function provides an implementation of [George Dantzig's simplex
	 * algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear optimization problems with linear
	 * equality and inequality constraints and implicit non-negative variables.
	 */
	public final static IBuiltInSymbol LinearProgramming = F.initFinalSymbol("LinearProgramming", ID.LinearProgramming);

	/**
	 * LinearRecurrence(list1, list2, n) - solve the linear recurrence and return the generated sequence of elements.
	 */
	public final static IBuiltInSymbol LinearRecurrence = F.initFinalSymbol("LinearRecurrence", ID.LinearRecurrence);

	/**
	 * LinearSolve(matrix, right) - solves the linear equation system 'matrix . x = right' and returns one corresponding
	 * solution `x`.
	 */
	public final static IBuiltInSymbol LinearSolve = F.initFinalSymbol("LinearSolve", ID.LinearSolve);

	/***/
	public final static IBuiltInSymbol LiouvilleLambda = F.initFinalSymbol("LiouvilleLambda", ID.LiouvilleLambda);

	/** List(e1, e2, ..., ei) - represents a list containing the elements `e1...ei`. */
	public final static IBuiltInSymbol List = F.initFinalSymbol("List", ID.List);

	/***/
	public final static IBuiltInSymbol ListContourPlot = F.initFinalSymbol("ListContourPlot", ID.ListContourPlot);

	/** ListConvolve(kernel-list, tensor-list) - create the convolution of the `kernel-list` with `tensor-list`. */
	public final static IBuiltInSymbol ListConvolve = F.initFinalSymbol("ListConvolve", ID.ListConvolve);

	/** ListCorrelate(kernel-list, tensor-list) - create the correlation of the `kernel-list` with `tensor-list`. */
	public final static IBuiltInSymbol ListCorrelate = F.initFinalSymbol("ListCorrelate", ID.ListCorrelate);

	/** ListLinePlot( { list-of-points } ) - generate a JavaScript list line plot control for the `list-of-points`. */
	public final static IBuiltInSymbol ListLinePlot = F.initFinalSymbol("ListLinePlot", ID.ListLinePlot);

	/** ListPlot( { list-of-points } ) - generate a JavaScript list plot control for the `list-of-points`. */
	public final static IBuiltInSymbol ListPlot = F.initFinalSymbol("ListPlot", ID.ListPlot);

	/** ListPlot3D( { list-of-points } ) - generate a JavaScript list plot 3D control for the `list-of-points`. */
	public final static IBuiltInSymbol ListPlot3D = F.initFinalSymbol("ListPlot3D", ID.ListPlot3D);

	/** ListQ(expr) - tests whether `expr` is a `List`. */
	public final static IBuiltInSymbol ListQ = F.initFinalSymbol("ListQ", ID.ListQ);

	/**
	 * Listable - is an attribute specifying that a function should be automatically applied to each element of a list.
	 */
	public final static IBuiltInSymbol Listable = F.initFinalSymbol("Listable", ID.Listable);

	/***/
	public final static IBuiltInSymbol Literal = F.initFinalSymbol("Literal", ID.Literal);

	/** Log(z) - returns the natural logarithm of `z`. */
	public final static IBuiltInSymbol Log = F.initFinalSymbol("Log", ID.Log);

	/**
	 * Log10(z) - returns the base-`10` logarithm of `z`. `Log10(z)` will be converted to `Log(z)/Log(10)` in symbolic
	 * mode.
	 */
	public final static IBuiltInSymbol Log10 = F.initFinalSymbol("Log10", ID.Log10);

	/**
	 * Log2(z) - returns the base-`2` logarithm of `z`. `Log2(z)` will be converted to `Log(z)/Log(2)` in symbolic mode.
	 */
	public final static IBuiltInSymbol Log2 = F.initFinalSymbol("Log2", ID.Log2);

	/** LogGamma(z) - is the logarithmic gamma function on the complex number `z`. */
	public final static IBuiltInSymbol LogGamma = F.initFinalSymbol("LogGamma", ID.LogGamma);

	/** LogIntegral(expr) - returns the integral logarithm of `expr`. */
	public final static IBuiltInSymbol LogIntegral = F.initFinalSymbol("LogIntegral", ID.LogIntegral);

	/** LogNormalDistribution(m, s) - returns a log-normal distribution. */
	public final static IBuiltInSymbol LogNormalDistribution = F.initFinalSymbol("LogNormalDistribution",
			ID.LogNormalDistribution);

	/***/
	public final static IBuiltInSymbol LogicalExpand = F.initFinalSymbol("LogicalExpand", ID.LogicalExpand);

	/** LogisticSigmoid(z) - returns the logistic sigmoid of `z`. */
	public final static IBuiltInSymbol LogisticSigmoid = F.initFinalSymbol("LogisticSigmoid", ID.LogisticSigmoid);

	/***/
	public final static IBuiltInSymbol Longest = F.initFinalSymbol("Longest", ID.Longest);

	/***/
	public final static IBuiltInSymbol LongForm = F.initFinalSymbol("LongForm", ID.LongForm);

	/**
	 * Lookup(association, key) - return the value in the `association` which is associated with the `key`. If no value
	 * is available return `Missing("KeyAbsent",key)`.
	 */
	public final static IBuiltInSymbol Lookup = F.initFinalSymbol("Lookup", ID.Lookup);

	/***/
	public final static IBuiltInSymbol LowerCaseQ = F.initFinalSymbol("LowerCaseQ", ID.LowerCaseQ);

	/** LowerTriangularize(matrix) - create a lower triangular matrix from the given `matrix`. */
	public final static IBuiltInSymbol LowerTriangularize = F.initFinalSymbol("LowerTriangularize",
			ID.LowerTriangularize);

	/** LucasL(n) - gives the `n`th Lucas number. */
	public final static IBuiltInSymbol LucasL = F.initFinalSymbol("LucasL", ID.LucasL);

	/** MachineNumberQ(expr) - returns `True` if `expr` is a machine-precision real or complex number. */
	public final static IBuiltInSymbol MachineNumberQ = F.initFinalSymbol("MachineNumberQ", ID.MachineNumberQ);

	/***/
	public final static IBuiltInSymbol Magenta = F.initFinalSymbol("Magenta", ID.Magenta);

	/** MangoldtLambda(n) - the von Mangoldt function of `n` */
	public final static IBuiltInSymbol MangoldtLambda = F.initFinalSymbol("MangoldtLambda", ID.MangoldtLambda);

	/**
	 * ManhattanDistance(u, v) - returns the Manhattan distance between `u` and `v`, which is the number of horizontal
	 * or vertical moves in the grid like Manhattan city layout to get from `u` to `v`.
	 */
	public final static IBuiltInSymbol ManhattanDistance = F.initFinalSymbol("ManhattanDistance", ID.ManhattanDistance);

	/**
	 * Manipulate(plot, {x, min, max}) - generate a JavaScript control for the expression `plot` which can be
	 * manipulated by a range slider `{x, min, max}`.
	 */
	public final static IBuiltInSymbol Manipulate = F.initFinalSymbol("Manipulate", ID.Manipulate);

	/***/
	public final static IBuiltInSymbol MantissaExponent = F.initFinalSymbol("MantissaExponent", ID.MantissaExponent);

	/** Map(f, expr) or f /@ expr - applies `f` to each part on the first level of `expr`. */
	public final static IBuiltInSymbol Map = F.initFinalSymbol("Map", ID.Map);

	/***/
	public final static IBuiltInSymbol MapAll = F.initFinalSymbol("MapAll", ID.MapAll);

	/***/
	public final static IBuiltInSymbol MapAt = F.initFinalSymbol("MapAt", ID.MapAt);

	/**
	 * MapIndexed(f, expr) - applies `f` to each part on the first level of `expr` and appending the elements position
	 * as a list in the second argument.
	 */
	public final static IBuiltInSymbol MapIndexed = F.initFinalSymbol("MapIndexed", ID.MapIndexed);

	/** MapThread(f, {{a1, a2, ...}, {b1, b2, ...}, ...}) - returns `{f(a1, b1, ...), f(a2, b2, ...), ...}`. */
	public final static IBuiltInSymbol MapThread = F.initFinalSymbol("MapThread", ID.MapThread);

	/** MatchQ(expr, form) - tests whether `expr` matches `form`. */
	public final static IBuiltInSymbol MatchQ = F.initFinalSymbol("MatchQ", ID.MatchQ);

	/**
	 * MatchingDissimilarity(u, v) - returns the Matching dissimilarity between the two boolean 1-D lists `u` and `v`,
	 * which is defined as `(c_tf + c_ft) / n`, where `n` is `len(u)` and `c_ij` is the number of occurrences of
	 * `u(k)=i` and `v(k)=j` for `k<n`.
	 */
	public final static IBuiltInSymbol MatchingDissimilarity = F.initFinalSymbol("MatchingDissimilarity",
			ID.MatchingDissimilarity);

	/** MathMLForm(expr) - returns the MathML form of the evaluated `expr`. */
	public final static IBuiltInSymbol MathMLForm = F.initFinalSymbol("MathMLForm", ID.MathMLForm);

	/** MatrixExp(matrix) - computes the matrix exponential of the square `matrix`. */
	public final static IBuiltInSymbol MatrixExp = F.initFinalSymbol("MatrixExp", ID.MatrixExp);

	/***/
	public final static IBuiltInSymbol MatrixForm = F.initFinalSymbol("MatrixForm", ID.MatrixForm);

	/**
	 * MatrixMinimalPolynomial(matrix, var) - computes the matrix minimal polynomial of a `matrix` for the variable
	 * `var`.
	 */
	public final static IBuiltInSymbol MatrixMinimalPolynomial = F.initFinalSymbol("MatrixMinimalPolynomial",
			ID.MatrixMinimalPolynomial);

	/** MatrixPlot( matrix ) - create a matrix plot. */
	public final static IBuiltInSymbol MatrixPlot = F.initFinalSymbol("MatrixPlot", ID.MatrixPlot);

	/** MatrixPower(matrix, n) - computes the `n`th power of a `matrix` */
	public final static IBuiltInSymbol MatrixPower = F.initFinalSymbol("MatrixPower", ID.MatrixPower);

	/** MatrixQ(m) - returns `True` if `m` is a list of equal-length lists. */
	public final static IBuiltInSymbol MatrixQ = F.initFinalSymbol("MatrixQ", ID.MatrixQ);

	/** MatrixRank(matrix) - returns the rank of `matrix`. */
	public final static IBuiltInSymbol MatrixRank = F.initFinalSymbol("MatrixRank", ID.MatrixRank);

	/** Max(e_1, e_2, ..., e_i) - returns the expression with the greatest value among the `e_i`. */
	public final static IBuiltInSymbol Max = F.initFinalSymbol("Max", ID.Max);

	/** MaxFilter(list, r) - filter which evaluates the `Max` of `list` for the radius `r`. */
	public final static IBuiltInSymbol MaxFilter = F.initFinalSymbol("MaxFilter", ID.MaxFilter);

	/***/
	public final static IBuiltInSymbol MaxIterations = F.initFinalSymbol("MaxIterations", ID.MaxIterations);

	/***/
	public final static IBuiltInSymbol MaxPoints = F.initFinalSymbol("MaxPoints", ID.MaxPoints);

	/** Maximize(unary-function, variable) - returns the maximum of the unary function for the given `variable`. */
	public final static IBuiltInSymbol Maximize = F.initFinalSymbol("Maximize", ID.Maximize);

	/** Mean(list) - returns the statistical mean of `list`. */
	public final static IBuiltInSymbol Mean = F.initFinalSymbol("Mean", ID.Mean);

	/***/
	public final static IBuiltInSymbol MeanDeviation = F.initFinalSymbol("MeanDeviation", ID.MeanDeviation);

	/** MeanFilter(list, r) - filter which evaluates the `Mean` of `list` for the radius `r`. */
	public final static IBuiltInSymbol MeanFilter = F.initFinalSymbol("MeanFilter", ID.MeanFilter);

	/** Median(list) - returns the median of `list`. */
	public final static IBuiltInSymbol Median = F.initFinalSymbol("Median", ID.Median);

	/** MedianFilter(list, r) - filter which evaluates the `Median` of `list` for the radius `r`. */
	public final static IBuiltInSymbol MedianFilter = F.initFinalSymbol("MedianFilter", ID.MedianFilter);

	/***/
	public final static IBuiltInSymbol MeijerG = F.initFinalSymbol("MeijerG", ID.MeijerG);

	/** MemberQ(list, pattern) - returns `True` if pattern matches any element of `list`, or `False` otherwise. */
	public final static IBuiltInSymbol MemberQ = F.initFinalSymbol("MemberQ", ID.MemberQ);

	/** MersennePrimeExponent(n) - returns the `n`th mersenne prime exponent. `2^n - 1` must be a prime number. */
	public final static IBuiltInSymbol MersennePrimeExponent = F.initFinalSymbol("MersennePrimeExponent",
			ID.MersennePrimeExponent);

	/**
	 * MersennePrimeExponentQ(n) - returns `True` if `2^n - 1` is a prime number. Currently `0 <= n <= 47` can be
	 * computed in reasonable time.
	 */
	public final static IBuiltInSymbol MersennePrimeExponentQ = F.initFinalSymbol("MersennePrimeExponentQ",
			ID.MersennePrimeExponentQ);

	/***/
	public final static IBuiltInSymbol MeshRange = F.initFinalSymbol("MeshRange", ID.MeshRange);

	/**
	 * Message(symbol::msg, expr1, expr2, ...) - displays the specified message, replacing placeholders in the message
	 * text with the corresponding expressions.
	 */
	public final static IBuiltInSymbol Message = F.initFinalSymbol("Message", ID.Message);

	/**
	 * MessageName(symbol, msg) - `symbol::msg` identifies a message. `MessageName` is the head of message IDs of the
	 * form `symbol::tag`.
	 */
	public final static IBuiltInSymbol MessageName = F.initFinalSymbol("MessageName", ID.MessageName);

	/***/
	public final static IBuiltInSymbol Messages = F.initFinalSymbol("Messages", ID.Messages);

	/***/
	public final static IBuiltInSymbol Method = F.initFinalSymbol("Method", ID.Method);

	/** Min(e_1, e_2, ..., e_i) - returns the expression with the lowest value among the `e_i`. */
	public final static IBuiltInSymbol Min = F.initFinalSymbol("Min", ID.Min);

	/** MinFilter(list, r) - filter which evaluates the `Min` of `list` for the radius `r`. */
	public final static IBuiltInSymbol MinFilter = F.initFinalSymbol("MinFilter", ID.MinFilter);

	/***/
	public final static IBuiltInSymbol MinMax = F.initFinalSymbol("MinMax", ID.MinMax);

	/***/
	public final static IBuiltInSymbol MinimalPolynomial = F.initFinalSymbol("MinimalPolynomial", ID.MinimalPolynomial);

	/** Minimize(unary-function, variable) - returns the minimum of the unary function for the given `variable`. */
	public final static IBuiltInSymbol Minimize = F.initFinalSymbol("Minimize", ID.Minimize);

	/** Minus(expr) - is the negation of `expr`. */
	public final static IBuiltInSymbol Minus = F.initFinalSymbol("Minus", ID.Minus);

	/***/
	public final static IBuiltInSymbol Missing = F.initFinalSymbol("Missing", ID.Missing);

	/** MissingQ(expr) - returns `True` if `expr` is a `Missing()` expression. */
	public final static IBuiltInSymbol MissingQ = F.initFinalSymbol("MissingQ", ID.MissingQ);

	/** Mod(x, m) - returns `x` modulo `m`. */
	public final static IBuiltInSymbol Mod = F.initFinalSymbol("Mod", ID.Mod);

	/**
	 * Module({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables` by renaming local
	 * variables.
	 */
	public final static IBuiltInSymbol Module = F.initFinalSymbol("Module", ID.Module);

	/***/
	public final static IBuiltInSymbol Modulus = F.initFinalSymbol("Modulus", ID.Modulus);

	/** MoebiusMu(expr) - calculate the Möbius function. */
	public final static IBuiltInSymbol MoebiusMu = F.initFinalSymbol("MoebiusMu", ID.MoebiusMu);

	/**
	 * MonomialList(polynomial, list-of-variables) - get the list of monomials of a `polynomial` expression, with
	 * respect to the `list-of-variables`.
	 */
	public final static IBuiltInSymbol MonomialList = F.initFinalSymbol("MonomialList", ID.MonomialList);

	/***/
	public final static IBuiltInSymbol MonomialOrder = F.initFinalSymbol("MonomialOrder", ID.MonomialOrder);

	/** Most(expr) - returns `expr` with the last element removed. */
	public final static IBuiltInSymbol Most = F.initFinalSymbol("Most", ID.Most);

	/** Multinomial(n1, n2, ...) - gives the multinomial coefficient `(n1+n2+...)!/(n1! n2! ...)`. */
	public final static IBuiltInSymbol Multinomial = F.initFinalSymbol("Multinomial", ID.Multinomial);

	/** MultiplicativeOrder(a, n) - gives the multiplicative order `a` modulo `n`. */
	public final static IBuiltInSymbol MultiplicativeOrder = F.initFinalSymbol("MultiplicativeOrder",
			ID.MultiplicativeOrder);

	/** N(expr) - gives the numerical value of `expr`. */
	public final static IBuiltInSymbol N = F.initFinalSymbol("N", ID.N);

	/**
	 * ND(function, x, value) - returns a numerical approximation of the partial derivative of the `function` for the
	 * variable `x` and the given `value`.
	 */
	public final static IBuiltInSymbol ND = F.initFinalSymbol("ND", ID.ND);

	/***/
	public final static IBuiltInSymbol NDSolve = F.initFinalSymbol("NDSolve", ID.NDSolve);

	/***/
	public final static IBuiltInSymbol NFourierTransform = F.initFinalSymbol("NFourierTransform", ID.NFourierTransform);

	/** NHoldAll - is an attribute that protects all arguments of a function from numeric evaluation. */
	public final static IBuiltInSymbol NHoldAll = F.initFinalSymbol("NHoldAll", ID.NHoldAll);

	/** NHoldFirst - is an attribute that protects the first argument of a function from numeric evaluation. */
	public final static IBuiltInSymbol NHoldFirst = F.initFinalSymbol("NHoldFirst", ID.NHoldFirst);

	/** NHoldRest - is an attribute that protects all but the first argument of a function from numeric evaluation. */
	public final static IBuiltInSymbol NHoldRest = F.initFinalSymbol("NHoldRest", ID.NHoldRest);

	/**
	 * NIntegrate(f, {x,a,b}) - computes the numerical univariate real integral of `f` with respect to `x` from `a` to
	 * `b`.
	 */
	public final static IBuiltInSymbol NIntegrate = F.initFinalSymbol("NIntegrate", ID.NIntegrate);

	/**
	 * NMaximize({maximize_function, constraints}, variables_list) - the `NMaximize` function provides an implementation
	 * of [George Dantzig's simplex algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear
	 * optimization problems with linear equality and inequality constraints and implicit non-negative variables.
	 */
	public final static IBuiltInSymbol NMaximize = F.initFinalSymbol("NMaximize", ID.NMaximize);

	/**
	 * NMinimize({maximize_function, constraints}, variables_list) - the `NMinimize` function provides an implementation
	 * of [George Dantzig's simplex algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear
	 * optimization problems with linear equality and inequality constraints and implicit non-negative variables.
	 */
	public final static IBuiltInSymbol NMinimize = F.initFinalSymbol("NMinimize", ID.NMinimize);

	/** NRoots(polynomial==0) - gives the numerical roots of a univariate polynomial `polynomial`. */
	public final static IBuiltInSymbol NRoots = F.initFinalSymbol("NRoots", ID.NRoots);

	/** NSolve(equations, vars) - attempts to solve `equations` for the variables `vars`. */
	public final static IBuiltInSymbol NSolve = F.initFinalSymbol("NSolve", ID.NSolve);

	/** NakagamiDistribution(m, o) - returns a Nakagami distribution. */
	public final static IBuiltInSymbol NakagamiDistribution = F.initFinalSymbol("NakagamiDistribution",
			ID.NakagamiDistribution);

	/***/
	public final static IBuiltInSymbol Names = F.initFinalSymbol("Names", ID.Names);

	/**
	 * Nand(arg1, arg2, ...)' - Logical NAND function. It evaluates its arguments in order, giving `True` immediately if
	 * any of them are `False`, and `False` if they are all `True`.
	 */
	public final static IBuiltInSymbol Nand = F.initFinalSymbol("Nand", ID.Nand);

	/***/
	public final static IBuiltInSymbol Nearest = F.initFinalSymbol("Nearest", ID.Nearest);

	/** Negative(x) - returns `True` if `x` is a negative real number. */
	public final static IBuiltInSymbol Negative = F.initFinalSymbol("Negative", ID.Negative);

	/***/
	public final static IBuiltInSymbol NegativeDegreeLexicographic = F.initFinalSymbol("NegativeDegreeLexicographic",
			ID.NegativeDegreeLexicographic);

	/***/
	public final static IBuiltInSymbol NegativeDegreeReverseLexicographic = F
			.initFinalSymbol("NegativeDegreeReverseLexicographic", ID.NegativeDegreeReverseLexicographic);

	/***/
	public final static IBuiltInSymbol NegativeLexicographic = F.initFinalSymbol("NegativeLexicographic",
			ID.NegativeLexicographic);

	/** Nest(f, expr, n) - starting with `expr`, iteratively applies `f` `n` times and returns the final result. */
	public final static IBuiltInSymbol Nest = F.initFinalSymbol("Nest", ID.Nest);

	/**
	 * NestList(f, expr, n) - starting with `expr`, iteratively applies `f` `n` times and returns a list of all
	 * intermediate results.
	 */
	public final static IBuiltInSymbol NestList = F.initFinalSymbol("NestList", ID.NestList);

	/**
	 * NestWhile(f, expr, test) - applies a function `f` repeatedly on an expression `expr`, until applying `test` on
	 * the result no longer yields `True`.
	 */
	public final static IBuiltInSymbol NestWhile = F.initFinalSymbol("NestWhile", ID.NestWhile);

	/**
	 * NestWhileList(f, expr, test) - applies a function `f` repeatedly on an expression `expr`, until applying `test`
	 * on the result no longer yields `True`. It returns a list of all intermediate results.
	 */
	public final static IBuiltInSymbol NestWhileList = F.initFinalSymbol("NestWhileList", ID.NestWhileList);

	/** NextPrime(n) - gives the next prime after `n`. */
	public final static IBuiltInSymbol NextPrime = F.initFinalSymbol("NextPrime", ID.NextPrime);

	/***/
	public final static IBuiltInSymbol NonCommutativeMultiply = F.initFinalSymbol("NonCommutativeMultiply",
			ID.NonCommutativeMultiply);

	/** NonNegative(x) - returns `True` if `x` is a positive real number or zero. */
	public final static IBuiltInSymbol NonNegative = F.initFinalSymbol("NonNegative", ID.NonNegative);

	/** NonPositive(x) - returns `True` if `x` is a negative real number or zero. */
	public final static IBuiltInSymbol NonPositive = F.initFinalSymbol("NonPositive", ID.NonPositive);

	/** None - is a possible value for `Span` and `Quiet`. */
	public final static IBuiltInSymbol None = F.initFinalSymbol("None", ID.None);

	/**
	 * NoneTrue({expr1, expr2, ...}, test) - returns `True` if no application of `test` to `expr1, expr2, ...` evaluates
	 * to `True`.
	 */
	public final static IBuiltInSymbol NoneTrue = F.initFinalSymbol("NoneTrue", ID.NoneTrue);

	/***/
	public final static IBuiltInSymbol Nonexistent = F.initFinalSymbol("Nonexistent", ID.Nonexistent);

	/**
	 * Nor(arg1, arg2, ...)' - Logical NOR function. It evaluates its arguments in order, giving `False` immediately if
	 * any of them are `True`, and `True` if they are all `False`.
	 */
	public final static IBuiltInSymbol Nor = F.initFinalSymbol("Nor", ID.Nor);

	/** Norm(m, l) - computes the `l`-norm of matrix `m` (currently only works for vectors!). */
	public final static IBuiltInSymbol Norm = F.initFinalSymbol("Norm", ID.Norm);

	/** Normal(expr) - converts a special Symja expression `expr` into a standard expression. */
	public final static IBuiltInSymbol Normal = F.initFinalSymbol("Normal", ID.Normal);

	/** NormalDistribution(m, s) - returns the normal distribution of mean `m` and sigma `s`. */
	public final static IBuiltInSymbol NormalDistribution = F.initFinalSymbol("NormalDistribution",
			ID.NormalDistribution);

	/** Normalize(v) - calculates the normalized vector `v` as `v/Norm(v)`. */
	public final static IBuiltInSymbol Normalize = F.initFinalSymbol("Normalize", ID.Normalize);

	/**
	 * Not(expr) - Logical Not function (negation). Returns `True` if the statement is `False`. Returns `False` if the
	 * `expr` is `True`
	 */
	public final static IBuiltInSymbol Not = F.initFinalSymbol("Not", ID.Not);

	/***/
	public final static IBuiltInSymbol NotApplicable = F.initFinalSymbol("NotApplicable", ID.NotApplicable);

	/***/
	public final static IBuiltInSymbol NotAvailable = F.initFinalSymbol("NotAvailable", ID.NotAvailable);

	/***/
	public final static IBuiltInSymbol NotElement = F.initFinalSymbol("NotElement", ID.NotElement);

	/***/
	public final static IBuiltInSymbol NotListQ = F.initFinalSymbol("NotListQ", ID.NotListQ);

	/***/
	public final static IBuiltInSymbol Nothing = F.initFinalSymbol("Nothing", ID.Nothing);

	/***/
	public final static IBuiltInSymbol Now = F.initFinalSymbol("Now", ID.Now);

	/** Null - is the implicit result of expressions that do not yield a result. */
	public final static IBuiltInSymbol Null = F.initFinalSymbol("Null", ID.Null);

	/** NullSpace(matrix) - returns a list of vectors that span the nullspace of the `matrix`. */
	public final static IBuiltInSymbol NullSpace = F.initFinalSymbol("NullSpace", ID.NullSpace);

	/***/
	public final static IBuiltInSymbol Number = F.initFinalSymbol("Number", ID.Number);

	/***/
	public final static IBuiltInSymbol NumberFieldRootsOfUnity = F.initFinalSymbol("NumberFieldRootsOfUnity",
			ID.NumberFieldRootsOfUnity);

	/** NumberQ(expr) - returns `True` if `expr` is an explicit number, and `False` otherwise. */
	public final static IBuiltInSymbol NumberQ = F.initFinalSymbol("NumberQ", ID.NumberQ);

	/***/
	public final static IBuiltInSymbol NumberString = F.initFinalSymbol("NumberString", ID.NumberString);

	/** Numerator(expr) - gives the numerator in `expr`. Numerator collects expressions with non negative exponents. */
	public final static IBuiltInSymbol Numerator = F.initFinalSymbol("Numerator", ID.Numerator);

	/***/
	public final static IBuiltInSymbol NumericFunction = F.initFinalSymbol("NumericFunction", ID.NumericFunction);

	/** NumericQ(expr) - returns `True` if `expr` is an explicit numeric expression, and `False` otherwise. */
	public final static IBuiltInSymbol NumericQ = F.initFinalSymbol("NumericQ", ID.NumericQ);

	/***/
	public final static IBuiltInSymbol NuttallWindow = F.initFinalSymbol("NuttallWindow", ID.NuttallWindow);

	/***/
	public final static IBuiltInSymbol O = F.initFinalSymbol("O", ID.O);

	/** OddQ(x) - returns `True` if `x` is odd, and `False` otherwise. */
	public final static IBuiltInSymbol OddQ = F.initFinalSymbol("OddQ", ID.OddQ);

	/** Off( ) - switch off the interactive trace. */
	public final static IBuiltInSymbol Off = F.initFinalSymbol("Off", ID.Off);

	/** On( ) - switch on the interactive trace. The output is printed in the defined `out` stream. */
	public final static IBuiltInSymbol On = F.initFinalSymbol("On", ID.On);

	/**
	 * OneIdentity - is an attribute specifying that `f(x)` should be treated as equivalent to `x` in pattern matching.
	 */
	public final static IBuiltInSymbol OneIdentity = F.initFinalSymbol("OneIdentity", ID.OneIdentity);

	/** Operate(p, expr) - applies `p` to the head of `expr`. */
	public final static IBuiltInSymbol Operate = F.initFinalSymbol("Operate", ID.Operate);

	/**
	 * OptimizeExpression(function) - common subexpressions elimination for a complicated `function` by generating
	 * "dummy" variables for these subexpressions.
	 */
	public final static IBuiltInSymbol OptimizeExpression = F.initFinalSymbol("OptimizeExpression",
			ID.OptimizeExpression);

	/***/
	public final static IBuiltInSymbol OptionValue = F.initFinalSymbol("OptionValue", ID.OptionValue);

	/**
	 * Optional(patt, default) - is a pattern which matches `patt`, which if omitted should be replaced by `default`.
	 */
	public final static IBuiltInSymbol Optional = F.initFinalSymbol("Optional", ID.Optional);

	/***/
	public final static IBuiltInSymbol Options = F.initFinalSymbol("Options", ID.Options);

	/***/
	public final static IBuiltInSymbol OptionsPattern = F.initFinalSymbol("OptionsPattern", ID.OptionsPattern);

	/**
	 * Or(expr1, expr2, ...)' - `expr1 || expr2 || ...` evaluates each expression in turn, returning `True` as soon as
	 * an expression evaluates to `True`. If all expressions evaluate to `False`, `Or` returns `False`.
	 */
	public final static IBuiltInSymbol Or = F.initFinalSymbol("Or", ID.Or);

	/***/
	public final static IBuiltInSymbol Orange = F.initFinalSymbol("Orange", ID.Orange);

	/** Order(a, b) - is `0` if `a` equals `b`. Is `-1` or `1` according to canonical order of `a` and `b`. */
	public final static IBuiltInSymbol Order = F.initFinalSymbol("Order", ID.Order);

	/** OrderedQ({a, b}) - is `True` if `a` sorts before `b` according to canonical ordering. */
	public final static IBuiltInSymbol OrderedQ = F.initFinalSymbol("OrderedQ", ID.OrderedQ);

	/** Ordering(list) - calculate the permutation list of the elements in the sorted `list`. */
	public final static IBuiltInSymbol Ordering = F.initFinalSymbol("Ordering", ID.Ordering);

	/**
	 * Orderless - is an attribute indicating that the leaves in an expression `f(a, b, c)` can be placed in any order.
	 */
	public final static IBuiltInSymbol Orderless = F.initFinalSymbol("Orderless", ID.Orderless);

	/** OrthogonalMatrixQ(matrix) - returns `True`, if `matrix` is an orthogonal matrix. `False` otherwise. */
	public final static IBuiltInSymbol OrthogonalMatrixQ = F.initFinalSymbol("OrthogonalMatrixQ", ID.OrthogonalMatrixQ);

	/** Orthogonalize(matrix) - returns a basis for the orthogonalized set of vectors defined by `matrix`. */
	public final static IBuiltInSymbol Orthogonalize = F.initFinalSymbol("Orthogonalize", ID.Orthogonalize);

	/***/
	public final static IBuiltInSymbol Out = F.initFinalSymbol("Out", ID.Out);

	/**
	 * Outer(f, x, y) - computes a generalised outer product of `x` and `y`, using the function `f` in place of
	 * multiplication.
	 */
	public final static IBuiltInSymbol Outer = F.initFinalSymbol("Outer", ID.Outer);

	/***/
	public final static IBuiltInSymbol OutputForm = F.initFinalSymbol("OutputForm", ID.OutputForm);

	/***/
	public final static IBuiltInSymbol OutputStream = F.initFinalSymbol("OutputStream", ID.OutputStream);

	/***/
	public final static IBuiltInSymbol OwnValues = F.initFinalSymbol("OwnValues", ID.OwnValues);

	/** PDF(distribution, value) - returns the probability density function of `value`. */
	public final static IBuiltInSymbol PDF = F.initFinalSymbol("PDF", ID.PDF);

	/***/
	public final static IBuiltInSymbol Package = F.initFinalSymbol("Package", ID.Package);

	/** PadLeft(list, n) - pads `list` to length `n` by adding `0` on the left. */
	public final static IBuiltInSymbol PadLeft = F.initFinalSymbol("PadLeft", ID.PadLeft);

	/** PadRight(list, n) - pads `list` to length `n` by adding `0` on the right. */
	public final static IBuiltInSymbol PadRight = F.initFinalSymbol("PadRight", ID.PadRight);

	/**
	 * ParametricPlot({function1, function2}, {t, tMin, tMax}) - generate a JavaScript control for the parametric
	 * expressions `function1`, `function2` in the `t` range `{t, tMin, tMax}`.
	 */
	public final static IBuiltInSymbol ParametricPlot = F.initFinalSymbol("ParametricPlot", ID.ParametricPlot);

	/** Part(expr, i) - returns part `i` of `expr`. */
	public final static IBuiltInSymbol Part = F.initFinalSymbol("Part", ID.Part);

	/** Partition(list, n) - partitions `list` into sublists of length `n`. */
	public final static IBuiltInSymbol Partition = F.initFinalSymbol("Partition", ID.Partition);

	/** PartitionsP(n) - gives the number of unrestricted partitions of the integer `n`. */
	public final static IBuiltInSymbol PartitionsP = F.initFinalSymbol("PartitionsP", ID.PartitionsP);

	/** PartitionsQ(n) - gives the number of partitions of the integer `n` into distinct parts */
	public final static IBuiltInSymbol PartitionsQ = F.initFinalSymbol("PartitionsQ", ID.PartitionsQ);

	/***/
	public final static IBuiltInSymbol ParzenWindow = F.initFinalSymbol("ParzenWindow", ID.ParzenWindow);

	/***/
	public final static IBuiltInSymbol Pattern = F.initFinalSymbol("Pattern", ID.Pattern);

	/***/
	public final static IBuiltInSymbol PatternOrder = F.initFinalSymbol("PatternOrder", ID.PatternOrder);

	/**
	 * PatternTest(pattern, test) - constrains `pattern` to match `expr` only if the evaluation of `test(expr)` yields
	 * `True`.
	 */
	public final static IBuiltInSymbol PatternTest = F.initFinalSymbol("PatternTest", ID.PatternTest);

	/***/
	public final static IBuiltInSymbol Pause = F.initFinalSymbol("Pause", ID.Pause);

	/***/
	public final static IBuiltInSymbol PearsonChiSquareTest = F.initFinalSymbol("PearsonChiSquareTest",
			ID.PearsonChiSquareTest);

	/**
	 * PerfectNumber(n) - returns the `n`th perfect number. In number theory, a perfect number is a positive integer
	 * that is equal to the sum of its proper
	 */
	public final static IBuiltInSymbol PerfectNumber = F.initFinalSymbol("PerfectNumber", ID.PerfectNumber);

	/**
	 * PerfectNumberQ(n) - returns `True` if `n` is a perfect number. In number theory, a perfect number is a positive
	 * integer that is equal to the sum of its proper
	 */
	public final static IBuiltInSymbol PerfectNumberQ = F.initFinalSymbol("PerfectNumberQ", ID.PerfectNumberQ);

	/** Permutations(list) - gives all possible orderings of the items in `list`. */
	public final static IBuiltInSymbol Permutations = F.initFinalSymbol("Permutations", ID.Permutations);

	/** Pi - is the constant `Pi`. */
	public final static IBuiltInSymbol Pi = F.initFinalSymbol("Pi", ID.Pi);

	/** PieChart(list-of-values) - plot a pie chart from a `list-of-values`. */
	public final static IBuiltInSymbol PieChart = F.initFinalSymbol("PieChart", ID.PieChart);

	/** Piecewise({{expr1, cond1}, ...}) - represents a piecewise function. */
	public final static IBuiltInSymbol Piecewise = F.initFinalSymbol("Piecewise", ID.Piecewise);

	/**
	 * PiecewiseExpand(function) - expands piecewise expressions into a `Piecewise` function. Currently only `Abs, Clip,
	 * If, Ramp, UnitStep` are converted to Piecewise expressions.
	 */
	public final static IBuiltInSymbol PiecewiseExpand = F.initFinalSymbol("PiecewiseExpand", ID.PiecewiseExpand);

	/***/
	public final static IBuiltInSymbol Pink = F.initFinalSymbol("Pink", ID.Pink);

	/**
	 * Plot(function, {x, xMin, xMax}, PlotRange->{yMin,yMax}) - generate a JavaScript control for the expression
	 * `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
	 */
	public final static IBuiltInSymbol Plot = F.initFinalSymbol("Plot", ID.Plot);

	/**
	 * Plot3D(function, {x, xMin, xMax}, {y,yMin,yMax}) - generate a JavaScript control for the expression `function` in
	 * the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
	 */
	public final static IBuiltInSymbol Plot3D = F.initFinalSymbol("Plot3D", ID.Plot3D);

	/***/
	public final static IBuiltInSymbol PlotRange = F.initFinalSymbol("PlotRange", ID.PlotRange);

	/***/
	public final static IBuiltInSymbol PlotStyle = F.initFinalSymbol("PlotStyle", ID.PlotStyle);

	/** Plus(a, b, ...) - represents the sum of the terms `a, b, ...`. */
	public final static IBuiltInSymbol Plus = F.initFinalSymbol("Plus", ID.Plus);

	/** Pochhammer(a, n) - returns the pochhammer symbol for a rational number `a` and an integer number `n`. */
	public final static IBuiltInSymbol Pochhammer = F.initFinalSymbol("Pochhammer", ID.Pochhammer);

	/***/
	public final static IBuiltInSymbol Point = F.initFinalSymbol("Point", ID.Point);

	/** PoissonDistribution(m) - returns a Poisson distribution. */
	public final static IBuiltInSymbol PoissonDistribution = F.initFinalSymbol("PoissonDistribution",
			ID.PoissonDistribution);

	/**
	 * PolarPlot(function, {t, tMin, tMax}) - generate a JavaScript control for the polar plot expressions `function` in
	 * the `t` range `{t, tMin, tMax}`.
	 */
	public final static IBuiltInSymbol PolarPlot = F.initFinalSymbol("PolarPlot", ID.PolarPlot);

	/**
	 * PolyGamma(value) - return the digamma function of the `value`. The digamma function is defined as the logarithmic
	 * derivative of the gamma function.
	 */
	public final static IBuiltInSymbol PolyGamma = F.initFinalSymbol("PolyGamma", ID.PolyGamma);

	/***/
	public final static IBuiltInSymbol PolyLog = F.initFinalSymbol("PolyLog", ID.PolyLog);

	/***/
	public final static IBuiltInSymbol Polygon = F.initFinalSymbol("Polygon", ID.Polygon);

	/**
	 * PolynomialExtendedGCD(p, q, x) - returns the extended GCD ('greatest common divisor') of the univariate
	 * polynomials `p` and `q`.
	 */
	public final static IBuiltInSymbol PolynomialExtendedGCD = F.initFinalSymbol("PolynomialExtendedGCD",
			ID.PolynomialExtendedGCD);

	/** PolynomialGCD(p, q) - returns the GCD ('greatest common divisor') of the polynomials `p` and `q`. */
	public final static IBuiltInSymbol PolynomialGCD = F.initFinalSymbol("PolynomialGCD", ID.PolynomialGCD);

	/** PolynomialLCM(p, q) - returns the LCM ('least common multiple') of the polynomials `p` and `q`. */
	public final static IBuiltInSymbol PolynomialLCM = F.initFinalSymbol("PolynomialLCM", ID.PolynomialLCM);

	/**
	 * PolynomialQ(p, x) - return `True` if `p` is a polynomial for the variable `x`. Return `False` in all other cases.
	 */
	public final static IBuiltInSymbol PolynomialQ = F.initFinalSymbol("PolynomialQ", ID.PolynomialQ);

	/**
	 * PolynomialQuotient(p, q, x) - returns the polynomial quotient of the polynomials `p` and `q` for the variable
	 * `x`.
	 */
	public final static IBuiltInSymbol PolynomialQuotient = F.initFinalSymbol("PolynomialQuotient",
			ID.PolynomialQuotient);

	/**
	 * PolynomialQuotientRemainder(p, q, x) - returns a list with the polynomial quotient and remainder of the
	 * polynomials `p` and `q` for the variable `x`.
	 */
	public final static IBuiltInSymbol PolynomialQuotientRemainder = F.initFinalSymbol("PolynomialQuotientRemainder",
			ID.PolynomialQuotientRemainder);

	/**
	 * PolynomialQuotient(p, q, x) - returns the polynomial remainder of the polynomials `p` and `q` for the variable
	 * `x`.
	 */
	public final static IBuiltInSymbol PolynomialRemainder = F.initFinalSymbol("PolynomialRemainder",
			ID.PolynomialRemainder);

	/** Position(expr, patt) - returns the list of positions for which `expr` matches `patt`. */
	public final static IBuiltInSymbol Position = F.initFinalSymbol("Position", ID.Position);

	/** Positive(x) - returns `True` if `x` is a positive real number. */
	public final static IBuiltInSymbol Positive = F.initFinalSymbol("Positive", ID.Positive);

	/** PossibleZeroQ(expr) - maps a (possible) zero `expr` to `True` and returns `False` otherwise. */
	public final static IBuiltInSymbol PossibleZeroQ = F.initFinalSymbol("PossibleZeroQ", ID.PossibleZeroQ);

	/***/
	public final static IBuiltInSymbol Postefix = F.initFinalSymbol("Postefix", ID.Postefix);

	/** Power(a, b) - represents `a` raised to the power of `b`. */
	public final static IBuiltInSymbol Power = F.initFinalSymbol("Power", ID.Power);

	/** PowerExpand(expr) - expands out powers of the form `(x^y)^z` and `(x*y)^z` in `expr`. */
	public final static IBuiltInSymbol PowerExpand = F.initFinalSymbol("PowerExpand", ID.PowerExpand);

	/** PowerMod(x, y, m) - computes `x^y` modulo `m`. */
	public final static IBuiltInSymbol PowerMod = F.initFinalSymbol("PowerMod", ID.PowerMod);

	/** PreDecrement(x) - decrements `x` by `1`, returning the new value of `x`. */
	public final static IBuiltInSymbol PreDecrement = F.initFinalSymbol("PreDecrement", ID.PreDecrement);

	/** PreIncrement(x) - increments `x` by `1`, returning the new value of `x`. */
	public final static IBuiltInSymbol PreIncrement = F.initFinalSymbol("PreIncrement", ID.PreIncrement);

	/***/
	public final static IBuiltInSymbol Precision = F.initFinalSymbol("Precision", ID.Precision);

	/***/
	public final static IBuiltInSymbol PrecisionGoal = F.initFinalSymbol("PrecisionGoal", ID.PrecisionGoal);

	/***/
	public final static IBuiltInSymbol Prefix = F.initFinalSymbol("Prefix", ID.Prefix);

	/** Prepend(expr, item) - returns `expr` with `item` prepended to its leaves. */
	public final static IBuiltInSymbol Prepend = F.initFinalSymbol("Prepend", ID.Prepend);

	/** PrependTo(s, item) - prepend `item` to value of `s` and sets `s` to the result. */
	public final static IBuiltInSymbol PrependTo = F.initFinalSymbol("PrependTo", ID.PrependTo);

	/** Prime(n) - returns the `n`th prime number. */
	public final static IBuiltInSymbol Prime = F.initFinalSymbol("Prime", ID.Prime);

	/** PrimeOmega(n) - returns the sum of the exponents of the prime factorization of `n`. */
	public final static IBuiltInSymbol PrimeOmega = F.initFinalSymbol("PrimeOmega", ID.PrimeOmega);

	/** PrimePi(x) - gives the number of primes less than or equal to `x`. */
	public final static IBuiltInSymbol PrimePi = F.initFinalSymbol("PrimePi", ID.PrimePi);

	/** PrimePowerQ(n) - returns `True` if `n` is a power of a prime number. */
	public final static IBuiltInSymbol PrimePowerQ = F.initFinalSymbol("PrimePowerQ", ID.PrimePowerQ);

	/** PrimeQ(n) - returns `True` if `n` is a integer prime number. */
	public final static IBuiltInSymbol PrimeQ = F.initFinalSymbol("PrimeQ", ID.PrimeQ);

	/***/
	public final static IBuiltInSymbol Primes = F.initFinalSymbol("Primes", ID.Primes);

	/***/
	public final static IBuiltInSymbol PrimitiveRoot = F.initFinalSymbol("PrimitiveRoot", ID.PrimitiveRoot);

	/** PrimitiveRootList(n) - returns the list of the primitive roots of `n`. */
	public final static IBuiltInSymbol PrimitiveRootList = F.initFinalSymbol("PrimitiveRootList", ID.PrimitiveRootList);

	/** Print(expr) - print the `expr` to the default output stream and return `Null`. */
	public final static IBuiltInSymbol Print = F.initFinalSymbol("Print", ID.Print);

	/***/
	public final static IBuiltInSymbol PrintableASCIIQ = F.initFinalSymbol("PrintableASCIIQ", ID.PrintableASCIIQ);

	/**
	 * Probability(pure-function, data-set) - returns the probability of the `pure-function` for the given `data-set`.
	 */
	public final static IBuiltInSymbol Probability = F.initFinalSymbol("Probability", ID.Probability);

	/**
	 * Product(expr, {i, imin, imax}) - evaluates the discrete product of `expr` with `i` ranging from `imin` to `imax`.
	 */
	public final static IBuiltInSymbol Product = F.initFinalSymbol("Product", ID.Product);

	/** ProductLog(z) - returns the value of the Lambert W function at `z`. */
	public final static IBuiltInSymbol ProductLog = F.initFinalSymbol("ProductLog", ID.ProductLog);

	/** Projection(vector1, vector2) - Find the orthogonal projection of `vector1` onto another `vector2`. */
	public final static IBuiltInSymbol Projection = F.initFinalSymbol("Projection", ID.Projection);

	/***/
	public final static IBuiltInSymbol Protect = F.initFinalSymbol("Protect", ID.Protect);

	/***/
	public final static IBuiltInSymbol Protected = F.initFinalSymbol("Protected", ID.Protected);

	/**
	 * PseudoInverse(matrix) - computes the Moore-Penrose pseudoinverse of the `matrix`. If `matrix` is invertible, the
	 * pseudoinverse equals the inverse.
	 */
	public final static IBuiltInSymbol PseudoInverse = F.initFinalSymbol("PseudoInverse", ID.PseudoInverse);

	/***/
	public final static IBuiltInSymbol Purple = F.initFinalSymbol("Purple", ID.Purple);

	/***/
	public final static IBuiltInSymbol Put = F.initFinalSymbol("Put", ID.Put);

	/**
	 * QRDecomposition(A) - computes the QR decomposition of the matrix `A`. The QR decomposition is a decomposition of
	 * a matrix `A` into a product `A = Q.R` of an unitary matrix `Q` and an upper triangular matrix `R`.
	 */
	public final static IBuiltInSymbol QRDecomposition = F.initFinalSymbol("QRDecomposition", ID.QRDecomposition);

	/** Quantile(list, q) - returns the `q`-Quantile of `list`. */
	public final static IBuiltInSymbol Quantile = F.initFinalSymbol("Quantile", ID.Quantile);

	/** Quantity(value, unit) - returns the quantity for `value` and `unit` */
	public final static IBuiltInSymbol Quantity = F.initFinalSymbol("Quantity", ID.Quantity);

	/***/
	public final static IBuiltInSymbol QuantityDistribution = F.initFinalSymbol("QuantityDistribution",
			ID.QuantityDistribution);

	/** QuantityMagnitude(quantity) - returns the value of the `quantity` */
	public final static IBuiltInSymbol QuantityMagnitude = F.initFinalSymbol("QuantityMagnitude", ID.QuantityMagnitude);

	/***/
	public final static IBuiltInSymbol QuantityQ = F.initFinalSymbol("QuantityQ", ID.QuantityQ);

	/** Quartiles(arg) - returns a list of the `1/4`, `1/2` and `3/4` quantile of `arg`. */
	public final static IBuiltInSymbol Quartiles = F.initFinalSymbol("Quartiles", ID.Quartiles);

	/** Quiet(expr) - evaluates `expr` in "quiet" mode (i.e. no warning messages are shown during evaluation). */
	public final static IBuiltInSymbol Quiet = F.initFinalSymbol("Quiet", ID.Quiet);

	/***/
	public final static IBuiltInSymbol Quit = F.initFinalSymbol("Quit", ID.Quit);

	/** Quotient(m, n) - computes the integer quotient of `m` and `n`. */
	public final static IBuiltInSymbol Quotient = F.initFinalSymbol("Quotient", ID.Quotient);

	/** QuotientRemainder(m, n) - computes a list of the quotient and remainder from division of `m` and `n`. */
	public final static IBuiltInSymbol QuotientRemainder = F.initFinalSymbol("QuotientRemainder", ID.QuotientRemainder);

	/***/
	public final static IBuiltInSymbol RGBColor = F.initFinalSymbol("RGBColor", ID.RGBColor);

	/** Ramp(z) - The `Ramp` function is a unary real function, whose graph is shaped like a ramp. */
	public final static IBuiltInSymbol Ramp = F.initFinalSymbol("Ramp", ID.Ramp);

	/** RandomChoice({arg1, arg2, arg3,...}) - chooses a random `arg` from the list. */
	public final static IBuiltInSymbol RandomChoice = F.initFinalSymbol("RandomChoice", ID.RandomChoice);

	/***/
	public final static IBuiltInSymbol RandomComplex = F.initFinalSymbol("RandomComplex", ID.RandomComplex);

	/** RandomInteger(n) - create a random integer number between `0` and `n`. */
	public final static IBuiltInSymbol RandomInteger = F.initFinalSymbol("RandomInteger", ID.RandomInteger);

	/** RandomPrime(n) - create a random prime integer number between `2` and `n`. */
	public final static IBuiltInSymbol RandomPrime = F.initFinalSymbol("RandomPrime", ID.RandomPrime);

	/** RandomReal() - create a random number between `0.0` and `1.0`. */
	public final static IBuiltInSymbol RandomReal = F.initFinalSymbol("RandomReal", ID.RandomReal);

	/** RandomSample(<function>) - create a random sample for the arguments of the `function`. */
	public final static IBuiltInSymbol RandomSample = F.initFinalSymbol("RandomSample", ID.RandomSample);

	/***/
	public final static IBuiltInSymbol RandomVariate = F.initFinalSymbol("RandomVariate", ID.RandomVariate);

	/** Range(n) - returns a list of integers from `1` to `n`. */
	public final static IBuiltInSymbol Range = F.initFinalSymbol("Range", ID.Range);

	/** Rational - is the head of rational numbers. */
	public final static IBuiltInSymbol Rational = F.initFinalSymbol("Rational", ID.Rational);

	/**
	 * Rationalize(expression) - convert numerical real or imaginary parts in (sub-)expressions into rational numbers.
	 */
	public final static IBuiltInSymbol Rationalize = F.initFinalSymbol("Rationalize", ID.Rationalize);

	/***/
	public final static IBuiltInSymbol Rationals = F.initFinalSymbol("Rationals", ID.Rationals);

	/** Re(z) - returns the real component of the complex number `z`. */
	public final static IBuiltInSymbol Re = F.initFinalSymbol("Re", ID.Re);

	/***/
	public final static IBuiltInSymbol ReadProtected = F.initFinalSymbol("ReadProtected", ID.ReadProtected);

	/***/
	public final static IBuiltInSymbol ReadString = F.initFinalSymbol("ReadString", ID.ReadString);

	/** Real - is the head of real (floating point) numbers. */
	public final static IBuiltInSymbol Real = F.initFinalSymbol("Real", ID.Real);

	/***/
	public final static IBuiltInSymbol RealDigits = F.initFinalSymbol("RealDigits", ID.RealDigits);

	/** RealNumberQ(expr) - returns `True` if `expr` is an explicit number with no imaginary component. */
	public final static IBuiltInSymbol RealNumberQ = F.initFinalSymbol("RealNumberQ", ID.RealNumberQ);

	/** Reals - is the set of real numbers. */
	public final static IBuiltInSymbol Reals = F.initFinalSymbol("Reals", ID.Reals);

	/**
	 * Reap(expr) - gives the result of evaluating `expr`, together with all values sown during this evaluation. Values
	 * sown with different tags are given in different lists.
	 */
	public final static IBuiltInSymbol Reap = F.initFinalSymbol("Reap", ID.Reap);

	/***/
	public final static IBuiltInSymbol Rectangle = F.initFinalSymbol("Rectangle", ID.Rectangle);

	/***/
	public final static IBuiltInSymbol Red = F.initFinalSymbol("Red", ID.Red);

	/***/
	public final static IBuiltInSymbol Reduce = F.initFinalSymbol("Reduce", ID.Reduce);

	/** Refine(expression, assumptions) - evaluate the `expression` for the given `assumptions`. */
	public final static IBuiltInSymbol Refine = F.initFinalSymbol("Refine", ID.Refine);

	/***/
	public final static IBuiltInSymbol RegularExpression = F.initFinalSymbol("RegularExpression", ID.RegularExpression);

	/***/
	public final static IBuiltInSymbol Remove = F.initFinalSymbol("Remove", ID.Remove);

	/***/
	public final static IBuiltInSymbol RemoveDiacritics = F.initFinalSymbol("RemoveDiacritics", ID.RemoveDiacritics);

	/***/
	public final static IBuiltInSymbol Repeated = F.initFinalSymbol("Repeated", ID.Repeated);

	/***/
	public final static IBuiltInSymbol RepeatedNull = F.initFinalSymbol("RepeatedNull", ID.RepeatedNull);

	/**
	 * Replace(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr` with the
	 * right-hand-side `rhs`.
	 */
	public final static IBuiltInSymbol Replace = F.initFinalSymbol("Replace", ID.Replace);

	/** ReplaceAll(expr, i -> new) - replaces all `i` in `expr` with `new`. */
	public final static IBuiltInSymbol ReplaceAll = F.initFinalSymbol("ReplaceAll", ID.ReplaceAll);

	/**
	 * ReplaceList(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr` with the
	 * right-hand-side `rhs`.
	 */
	public final static IBuiltInSymbol ReplaceList = F.initFinalSymbol("ReplaceList", ID.ReplaceList);

	/** ReplacePart(expr, i -> new) - replaces part `i` in `expr` with `new`. */
	public final static IBuiltInSymbol ReplacePart = F.initFinalSymbol("ReplacePart", ID.ReplacePart);

	/**
	 * ReplaceRepeated(expr, lhs -> rhs) - repeatedly applies the rule `lhs -> rhs` to `expr` until the result no longer
	 * changes.
	 */
	public final static IBuiltInSymbol ReplaceRepeated = F.initFinalSymbol("ReplaceRepeated", ID.ReplaceRepeated);

	/** Rescale(list) - returns `Rescale(list,{Min(list), Max(list)})`. */
	public final static IBuiltInSymbol Rescale = F.initFinalSymbol("Rescale", ID.Rescale);

	/** Rest(expr) - returns `expr` with the first element removed. */
	public final static IBuiltInSymbol Rest = F.initFinalSymbol("Rest", ID.Rest);

	/**
	 * Resultant(polynomial1, polynomial2, var) - computes the resultant of the polynomials `polynomial1` and
	 * `polynomial2` with respect to the variable `var`.
	 */
	public final static IBuiltInSymbol Resultant = F.initFinalSymbol("Resultant", ID.Resultant);

	/** Return(expr) - aborts a function call and returns `expr`. */
	public final static IBuiltInSymbol Return = F.initFinalSymbol("Return", ID.Return);

	/** Reverse(list) - reverse the elements of the `list`. */
	public final static IBuiltInSymbol Reverse = F.initFinalSymbol("Reverse", ID.Reverse);

	/**
	 * RiccatiSolve({A,B},{Q,R}) - An algebraic Riccati equation is a type of nonlinear equation that arises in the
	 * context of infinite-horizon optimal control problems in continuous time or discrete time. The continuous time
	 * algebraic Riccati equation (CARE): `A^{T}·X+X·A-X·B·R^{-1}·B^{T}·X+Q==0`. And the respective linear controller
	 * is: `K = R^{-1}·B^{T}·P`. The solver receives `A`, `B`, `Q` and `R` and computes `P`.
	 */
	public final static IBuiltInSymbol RiccatiSolve = F.initFinalSymbol("RiccatiSolve", ID.RiccatiSolve);

	/** Riffle(list1, list2) - insert elements of `list2` between the elements of `list1`. */
	public final static IBuiltInSymbol Riffle = F.initFinalSymbol("Riffle", ID.Riffle);

	/***/
	public final static IBuiltInSymbol Right = F.initFinalSymbol("Right", ID.Right);

	/**
	 * RogersTanimotoDissimilarity(u, v) - returns the Rogers-Tanimoto dissimilarity between the two boolean 1-D lists
	 * `u` and `v`, which is defined as `R / (c_tt + c_ff + R)` where n is `len(u)`, `c_ij` is the number of occurrences
	 * of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * (c_tf + c_ft)`.
	 */
	public final static IBuiltInSymbol RogersTanimotoDissimilarity = F.initFinalSymbol("RogersTanimotoDissimilarity",
			ID.RogersTanimotoDissimilarity);

	/** RomanNumeral(positive-int-value) - converts the given `positive-int-value` to a roman numeral string. */
	public final static IBuiltInSymbol RomanNumeral = F.initFinalSymbol("RomanNumeral", ID.RomanNumeral);

	/***/
	public final static IBuiltInSymbol Root = F.initFinalSymbol("Root", ID.Root);

	/***/
	public final static IBuiltInSymbol RootIntervals = F.initFinalSymbol("RootIntervals", ID.RootIntervals);

	/***/
	public final static IBuiltInSymbol RootOf = F.initFinalSymbol("RootOf", ID.RootOf);

	/**
	 * Roots(polynomial-equation, var) - determine the roots of a univariate polynomial equation with respect to the
	 * variable `var`.
	 */
	public final static IBuiltInSymbol Roots = F.initFinalSymbol("Roots", ID.Roots);

	/** RotateLeft(list) - rotates the items of `list` by one item to the left. */
	public final static IBuiltInSymbol RotateLeft = F.initFinalSymbol("RotateLeft", ID.RotateLeft);

	/** RotateRight(list) - rotates the items of `list` by one item to the right. */
	public final static IBuiltInSymbol RotateRight = F.initFinalSymbol("RotateRight", ID.RotateRight);

	/** RotationMatrix(theta) - yields a rotation matrix for the angle `theta`. */
	public final static IBuiltInSymbol RotationMatrix = F.initFinalSymbol("RotationMatrix", ID.RotationMatrix);

	/** Round(expr) - round a given `expr` to nearest integer. */
	public final static IBuiltInSymbol Round = F.initFinalSymbol("Round", ID.Round);

	/***/
	public final static IBuiltInSymbol Row = F.initFinalSymbol("Row", ID.Row);

	/** RowReduce(matrix) - returns the reduced row-echelon form of `matrix`. */
	public final static IBuiltInSymbol RowReduce = F.initFinalSymbol("RowReduce", ID.RowReduce);

	/** Rule(x, y) - represents a rule replacing `x` with `y`. */
	public final static IBuiltInSymbol Rule = F.initFinalSymbol("Rule", ID.Rule);

	/** RuleDelayed(x, y) - represents a rule replacing `x` with `y`, with `y` held unevaluated. */
	public final static IBuiltInSymbol RuleDelayed = F.initFinalSymbol("RuleDelayed", ID.RuleDelayed);

	/**
	 * RussellRaoDissimilarity(u, v) - returns the Russell-Rao dissimilarity between the two boolean 1-D lists `u` and
	 * `v`, which is defined as `(n - c_tt) / c_tt` where `n` is `len(u)` and `c_ij` is the number of occurrences of
	 * `u(k)=i` and `v(k)=j` for `k<n`.
	 */
	public final static IBuiltInSymbol RussellRaoDissimilarity = F.initFinalSymbol("RussellRaoDissimilarity",
			ID.RussellRaoDissimilarity);

	/** SameQ(x, y) - returns `True` if `x` and `y` are structurally identical. */
	public final static IBuiltInSymbol SameQ = F.initFinalSymbol("SameQ", ID.SameQ);

	/***/
	public final static IBuiltInSymbol SameTest = F.initFinalSymbol("SameTest", ID.SameTest);

	/**
	 * SatisfiabilityCount(boolean-expr) - test whether the `boolean-expr` is satisfiable by a combination of boolean
	 * `False` and `True` values for the variables of the boolean expression and return the number of possible
	 * combinations.
	 */
	public final static IBuiltInSymbol SatisfiabilityCount = F.initFinalSymbol("SatisfiabilityCount",
			ID.SatisfiabilityCount);

	/**
	 * SatisfiabilityInstances(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable by a
	 * combination of boolean `False` and `True` values for the `list-of-variables` and return exactly one instance of
	 * `True, False` combinations if possible.
	 */
	public final static IBuiltInSymbol SatisfiabilityInstances = F.initFinalSymbol("SatisfiabilityInstances",
			ID.SatisfiabilityInstances);

	/**
	 * SatisfiableQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable by a combination
	 * of boolean `False` and `True` values for the `list-of-variables`.
	 */
	public final static IBuiltInSymbol SatisfiableQ = F.initFinalSymbol("SatisfiableQ", ID.SatisfiableQ);

	/***/
	public final static IBuiltInSymbol Scaled = F.initFinalSymbol("Scaled", ID.Scaled);

	/** Scan(f, expr) - applies `f` to each element of `expr` and returns 'Null'. */
	public final static IBuiltInSymbol Scan = F.initFinalSymbol("Scan", ID.Scan);

	/** Sec(z) - returns the secant of `z`. */
	public final static IBuiltInSymbol Sec = F.initFinalSymbol("Sec", ID.Sec);

	/** Sech(z) - returns the hyperbolic secant of `z`. */
	public final static IBuiltInSymbol Sech = F.initFinalSymbol("Sech", ID.Sech);

	/***/
	public final static IBuiltInSymbol Second = F.initFinalSymbol("Second", ID.Second);

	/** Select({e1, e2, ...}, f) - returns a list of the elements `ei` for which `f(ei)` returns `True`. */
	public final static IBuiltInSymbol Select = F.initFinalSymbol("Select", ID.Select);

	/** SelectFirst({e1, e2, ...}, f) - returns the first of the elements `ei` for which `f(ei)` returns `True`. */
	public final static IBuiltInSymbol SelectFirst = F.initFinalSymbol("SelectFirst", ID.SelectFirst);

	/**
	 * SemanticImport("path-to-filename") - if the file system is enabled, import the data from CSV files and do a
	 * semantic interpretation of the columns.
	 */
	public final static IBuiltInSymbol SemanticImport = F.initFinalSymbol("SemanticImport", ID.SemanticImport);

	/**
	 * SemanticImportString("string-content") - import the data from a content string in CSV format and do a semantic
	 * interpretation of the columns.
	 */
	public final static IBuiltInSymbol SemanticImportString = F.initFinalSymbol("SemanticImportString",
			ID.SemanticImportString);

	/***/
	public final static IBuiltInSymbol Sequence = F.initFinalSymbol("Sequence", ID.Sequence);

	/***/
	public final static IBuiltInSymbol SequenceHold = F.initFinalSymbol("SequenceHold", ID.SequenceHold);

	/** Series(expr, {x, x0, n}) - create a power series of `expr` up to order `(x- x0)^n` at the point `x = x0` */
	public final static IBuiltInSymbol Series = F.initFinalSymbol("Series", ID.Series);

	/** SeriesCoefficient(expr, {x, x0, n}) - get the coefficient of `(x- x0)^n` at the point `x = x0` */
	public final static IBuiltInSymbol SeriesCoefficient = F.initFinalSymbol("SeriesCoefficient", ID.SeriesCoefficient);

	/**
	 * SeriesData(x, x0, {coeff0, coeff1, coeff2,...}, nMin, nMax, denominator}) - internal structure of a power series
	 * at the point `x = x0` the `coeff_i` are coefficients of the power series.
	 */
	public final static IBuiltInSymbol SeriesData = F.initFinalSymbol("SeriesData", ID.SeriesData);

	/** Set(expr, value) - evaluates `value` and assigns it to `expr`. */
	public final static IBuiltInSymbol Set = F.initFinalSymbol("Set", ID.Set);

	/** SetAttributes(symbol, attrib) - adds `attrib` to `symbol`'s attributes. */
	public final static IBuiltInSymbol SetAttributes = F.initFinalSymbol("SetAttributes", ID.SetAttributes);

	/** SetDelayed(expr, value) - assigns `value` to `expr`, without evaluating `value`. */
	public final static IBuiltInSymbol SetDelayed = F.initFinalSymbol("SetDelayed", ID.SetDelayed);

	/***/
	public final static IBuiltInSymbol Share = F.initFinalSymbol("Share", ID.Share);

	/***/
	public final static IBuiltInSymbol Short = F.initFinalSymbol("Short", ID.Short);

	/***/
	public final static IBuiltInSymbol Shortest = F.initFinalSymbol("Shortest", ID.Shortest);

	/***/
	public final static IBuiltInSymbol Show = F.initFinalSymbol("Show", ID.Show);

	/** Sign(x) - gives `-1`, `0` or `1` depending on whether `x` is negative, zero or positive. */
	public final static IBuiltInSymbol Sign = F.initFinalSymbol("Sign", ID.Sign);

	/***/
	public final static IBuiltInSymbol SignCmp = F.initFinalSymbol("SignCmp", ID.SignCmp);

	/**
	 * Signature(permutation-list) - determine if the `permutation-list` has odd (`-1`) or even (`1`) parity. Returns
	 * `0` if two elements in the `permutation-list` are equal.
	 */
	public final static IBuiltInSymbol Signature = F.initFinalSymbol("Signature", ID.Signature);

	/** Simplify(expr) - simplifies `expr` */
	public final static IBuiltInSymbol Simplify = F.initFinalSymbol("Simplify", ID.Simplify);

	/** Sin(expr) - returns the sine of `expr` (measured in radians). */
	public final static IBuiltInSymbol Sin = F.initFinalSymbol("Sin", ID.Sin);

	/** SinIntegral(expr) - returns the hyperbolic sine integral of `expr`. */
	public final static IBuiltInSymbol SinIntegral = F.initFinalSymbol("SinIntegral", ID.SinIntegral);

	/** Sinc(expr) - the sinc function `Sin(expr)/expr` for `expr != 0`. `Sinc(0)` returns `1`. */
	public final static IBuiltInSymbol Sinc = F.initFinalSymbol("Sinc", ID.Sinc);

	/** SingularValueDecomposition(matrix) - calculates the singular value decomposition for the `matrix`. */
	public final static IBuiltInSymbol SingularValueDecomposition = F.initFinalSymbol("SingularValueDecomposition",
			ID.SingularValueDecomposition);

	/** Sinh(z) - returns the hyperbolic sine of `z`. */
	public final static IBuiltInSymbol Sinh = F.initFinalSymbol("Sinh", ID.Sinh);

	/** SinhIntegral(expr) - returns the sine integral of `expr`. */
	public final static IBuiltInSymbol SinhIntegral = F.initFinalSymbol("SinhIntegral", ID.SinhIntegral);

	/**
	 * Skewness(list) - gives Pearson's moment coefficient of skewness for `list` (a measure for estimating the symmetry
	 * of a distribution).
	 */
	public final static IBuiltInSymbol Skewness = F.initFinalSymbol("Skewness", ID.Skewness);

	/** # - is a short-hand for `#1`. */
	public final static IBuiltInSymbol Slot = F.initFinalSymbol("Slot", ID.Slot);

	/** ## - is the sequence of arguments supplied to a pure function. */
	public final static IBuiltInSymbol SlotSequence = F.initFinalSymbol("SlotSequence", ID.SlotSequence);

	/**
	 * SokalSneathDissimilarity(u, v) - returns the Sokal-Sneath dissimilarity between the two boolean 1-D lists `u` and
	 * `v`, which is defined as `R / (c_tt + R)` where n is `len(u)`, `c_ij` is the number of occurrences of `u(k)=i`
	 * and `v(k)=j` for `k<n`, and `R = 2 * (c_tf + c_ft)`.
	 */
	public final static IBuiltInSymbol SokalSneathDissimilarity = F.initFinalSymbol("SokalSneathDissimilarity",
			ID.SokalSneathDissimilarity);

	/** Solve(equations, vars) - attempts to solve `equations` for the variables `vars`. */
	public final static IBuiltInSymbol Solve = F.initFinalSymbol("Solve", ID.Solve);

	/** Sort(list) - sorts `list` (or the leaves of any other expression) according to canonical ordering. */
	public final static IBuiltInSymbol Sort = F.initFinalSymbol("Sort", ID.Sort);

	/**
	 * SortBy(list, f) - sorts `list` (or the leaves of any other expression) according to canonical ordering of the
	 * keys that are extracted from the `list`'s elements using `f`. Chunks of leaves that appear the same under `f` are
	 * sorted according to their natural order (without applying `f`).
	 */
	public final static IBuiltInSymbol SortBy = F.initFinalSymbol("SortBy", ID.SortBy);

	/** Sow(expr) - sends the value `expr` to the innermost `Reap`. */
	public final static IBuiltInSymbol Sow = F.initFinalSymbol("Sow", ID.Sow);

	/** Span - is the head of span ranges like `1;;3`. */
	public final static IBuiltInSymbol Span = F.initFinalSymbol("Span", ID.Span);

	/***/
	public final static IBuiltInSymbol SparseArray = F.initFinalSymbol("SparseArray", ID.SparseArray);

	/** SphericalBesselJ(n, z) - spherical Bessel function `J(n, x)`. */
	public final static IBuiltInSymbol SphericalBesselJ = F.initFinalSymbol("SphericalBesselJ", ID.SphericalBesselJ);

	/** SphericalBesselY(n, z) - spherical Bessel function `Y(n, x)`. */
	public final static IBuiltInSymbol SphericalBesselY = F.initFinalSymbol("SphericalBesselY", ID.SphericalBesselY);

	/***/
	public final static IBuiltInSymbol SphericalHankelH1 = F.initFinalSymbol("SphericalHankelH1", ID.SphericalHankelH1);

	/***/
	public final static IBuiltInSymbol SphericalHankelH2 = F.initFinalSymbol("SphericalHankelH2", ID.SphericalHankelH2);

	/** Split(list) - splits `list` into collections of consecutive identical elements. */
	public final static IBuiltInSymbol Split = F.initFinalSymbol("Split", ID.Split);

	/**
	 * SplitBy(list, f) - splits `list` into collections of consecutive elements that give the same result when `f` is
	 * applied.
	 */
	public final static IBuiltInSymbol SplitBy = F.initFinalSymbol("SplitBy", ID.SplitBy);

	/** Sqrt(expr) - returns the square root of `expr`. */
	public final static IBuiltInSymbol Sqrt = F.initFinalSymbol("Sqrt", ID.Sqrt);

	/**
	 * SquareFreeQ(n) - returns `True` if `n` is a square free integer number or a square free univariate polynomial.
	 */
	public final static IBuiltInSymbol SquareFreeQ = F.initFinalSymbol("SquareFreeQ", ID.SquareFreeQ);

	/** SquareMatrixQ(m) - returns `True` if `m` is a square matrix. */
	public final static IBuiltInSymbol SquareMatrixQ = F.initFinalSymbol("SquareMatrixQ", ID.SquareMatrixQ);

	/** SquaredEuclideanDistance(u, v) - returns squared the euclidean distance between `u$` and `v`. */
	public final static IBuiltInSymbol SquaredEuclideanDistance = F.initFinalSymbol("SquaredEuclideanDistance",
			ID.SquaredEuclideanDistance);

	/** Stack( ) - return a list of the heads of the current stack wrapped by `HoldForm`. */
	public final static IBuiltInSymbol Stack = F.initFinalSymbol("Stack", ID.Stack);

	/**
	 * Stack(expr) - begine a new stack and evaluate `èxpr`. Use `Stack(_)` as a subexpression in `expr` to return the
	 * stack elements.
	 */
	public final static IBuiltInSymbol StackBegin = F.initFinalSymbol("StackBegin", ID.StackBegin);

	/**
	 * StandardDeviation(list) - computes the standard deviation of `list`. `list` may consist of numerical values or
	 * symbols. Numerical values may be real or complex.
	 */
	public final static IBuiltInSymbol StandardDeviation = F.initFinalSymbol("StandardDeviation", ID.StandardDeviation);

	/***/
	public final static IBuiltInSymbol StandardForm = F.initFinalSymbol("StandardForm", ID.StandardForm);

	/***/
	public final static IBuiltInSymbol Standardize = F.initFinalSymbol("Standardize", ID.Standardize);

	/***/
	public final static IBuiltInSymbol StartOfLine = F.initFinalSymbol("StartOfLine", ID.StartOfLine);

	/***/
	public final static IBuiltInSymbol StartOfString = F.initFinalSymbol("StartOfString", ID.StartOfString);

	/** StieltjesGamma(a) - returns Stieltjes constant. */
	public final static IBuiltInSymbol StieltjesGamma = F.initFinalSymbol("StieltjesGamma", ID.StieltjesGamma);

	/** StirlingS1(n, k) - returns the Stirling numbers of the first kind. */
	public final static IBuiltInSymbol StirlingS1 = F.initFinalSymbol("StirlingS1", ID.StirlingS1);

	/**
	 * StirlingS2(n, k) - returns the Stirling numbers of the second kind. `StirlingS2(n,k)` is the number of ways of
	 * partitioning an `n`-element set into `k` non-empty subsets.
	 */
	public final static IBuiltInSymbol StirlingS2 = F.initFinalSymbol("StirlingS2", ID.StirlingS2);

	/***/
	public final static IBuiltInSymbol Strict = F.initFinalSymbol("Strict", ID.Strict);

	/***/
	public final static IBuiltInSymbol String = F.initFinalSymbol("String", ID.String);

	/** StringCases(string, pattern) - gives all occurences of `pattern` in `string`. */
	public final static IBuiltInSymbol StringCases = F.initFinalSymbol("StringCases", ID.StringCases);

	/**
	 * StringContainsQ(str1, str2) - return a list of matches for `"p1", "p2",...` list of strings in the string `str`.
	 */
	public final static IBuiltInSymbol StringContainsQ = F.initFinalSymbol("StringContainsQ", ID.StringContainsQ);

	/** StringCount(string, pattern) - counts all occurences of `pattern` in `string`. */
	public final static IBuiltInSymbol StringCount = F.initFinalSymbol("StringCount", ID.StringCount);

	/***/
	public final static IBuiltInSymbol StringDrop = F.initFinalSymbol("StringDrop", ID.StringDrop);

	/***/
	public final static IBuiltInSymbol StringExpression = F.initFinalSymbol("StringExpression", ID.StringExpression);

	/** StringJoin(str1, str2, ... strN) - returns the concatenation of the strings `str1, str2, ... strN`. */
	public final static IBuiltInSymbol StringJoin = F.initFinalSymbol("StringJoin", ID.StringJoin);

	/** StringLength(string) - gives the length of `string`. */
	public final static IBuiltInSymbol StringLength = F.initFinalSymbol("StringLength", ID.StringLength);

	/** StringMatchQ(string, regex-pattern) - check if the regular expression `regex-pattern` matches the `string`. */
	public final static IBuiltInSymbol StringMatchQ = F.initFinalSymbol("StringMatchQ", ID.StringMatchQ);

	/** StringPart(str, pos) - return the character at position `pos` from the `str` string expression. */
	public final static IBuiltInSymbol StringPart = F.initFinalSymbol("StringPart", ID.StringPart);

	/** StringQ(x) - is `True` if `x` is a string object, or `False` otherwise. */
	public final static IBuiltInSymbol StringQ = F.initFinalSymbol("StringQ", ID.StringQ);

	/** StringReplace(string, fromStr -> toStr) - replaces each occurrence of `fromStr` with `toStr` in `string`. */
	public final static IBuiltInSymbol StringReplace = F.initFinalSymbol("StringReplace", ID.StringReplace);

	/***/
	public final static IBuiltInSymbol StringRiffle = F.initFinalSymbol("StringRiffle", ID.StringRiffle);

	/** StringSplit(str) - split the string `str` by whitespaces into a list of strings. */
	public final static IBuiltInSymbol StringSplit = F.initFinalSymbol("StringSplit", ID.StringSplit);

	/***/
	public final static IBuiltInSymbol StringTake = F.initFinalSymbol("StringTake", ID.StringTake);

	/***/
	public final static IBuiltInSymbol StringTrim = F.initFinalSymbol("StringTrim", ID.StringTrim);

	/***/
	public final static IBuiltInSymbol Structure = F.initFinalSymbol("Structure", ID.Structure);

	/** StruveH(n, z) - returns the Struve function `H_n(z)`. */
	public final static IBuiltInSymbol StruveH = F.initFinalSymbol("StruveH", ID.StruveH);

	/** StruveL(n, z) - returns the modified Struve function `L_n(z)`. */
	public final static IBuiltInSymbol StruveL = F.initFinalSymbol("StruveL", ID.StruveL);

	/** StudentTDistribution(v) - returns a Student's t-distribution. */
	public final static IBuiltInSymbol StudentTDistribution = F.initFinalSymbol("StudentTDistribution",
			ID.StudentTDistribution);

	/***/
	public final static IBuiltInSymbol Style = F.initFinalSymbol("Style", ID.Style);

	/***/
	public final static IBuiltInSymbol StyleForm = F.initFinalSymbol("StyleForm", ID.StyleForm);

	/** Subdivide(n) - returns a list with `n+1` entries obtained by subdividing the range `0` to `1`. */
	public final static IBuiltInSymbol Subdivide = F.initFinalSymbol("Subdivide", ID.Subdivide);

	/** Subfactorial(n) - returns the subfactorial number of the integer `n` */
	public final static IBuiltInSymbol Subfactorial = F.initFinalSymbol("Subfactorial", ID.Subfactorial);

	/***/
	public final static IBuiltInSymbol Subscript = F.initFinalSymbol("Subscript", ID.Subscript);

	/***/
	public final static IBuiltInSymbol SubsetQ = F.initFinalSymbol("SubsetQ", ID.SubsetQ);

	/** Subsets(list) - finds a list of all possible subsets of `list`. */
	public final static IBuiltInSymbol Subsets = F.initFinalSymbol("Subsets", ID.Subsets);

	/***/
	public final static IBuiltInSymbol Subsuperscript = F.initFinalSymbol("Subsuperscript", ID.Subsuperscript);

	/** Subtract(a, b) - represents the subtraction of `b` from `a`. */
	public final static IBuiltInSymbol Subtract = F.initFinalSymbol("Subtract", ID.Subtract);

	/** SubtractFrom(x, dx) - is equivalent to `x = x - dx`. */
	public final static IBuiltInSymbol SubtractFrom = F.initFinalSymbol("SubtractFrom", ID.SubtractFrom);

	/** Sum(expr, {i, imin, imax}) - evaluates the discrete sum of `expr` with `i` ranging from `imin` to `imax`. */
	public final static IBuiltInSymbol Sum = F.initFinalSymbol("Sum", ID.Sum);

	/***/
	public final static IBuiltInSymbol Summary = F.initFinalSymbol("Summary", ID.Summary);

	/***/
	public final static IBuiltInSymbol Superscript = F.initFinalSymbol("Superscript", ID.Superscript);

	/** Surd(expr, n) - returns the `n`-th root of `expr`. If the result is defined, it's a real value. */
	public final static IBuiltInSymbol Surd = F.initFinalSymbol("Surd", ID.Surd);

	/***/
	public final static IBuiltInSymbol SurfaceGraphics = F.initFinalSymbol("SurfaceGraphics", ID.SurfaceGraphics);

	/** SurvivalFunction(dist, x) - returns the survival function for the distribution `dist` evaluated at `x`. */
	public final static IBuiltInSymbol SurvivalFunction = F.initFinalSymbol("SurvivalFunction", ID.SurvivalFunction);

	/**
	 * Switch(expr, pattern1, value1, pattern2, value2, ...) - yields the first `value` for which `expr` matches the
	 * corresponding pattern.
	 */
	public final static IBuiltInSymbol Switch = F.initFinalSymbol("Switch", ID.Switch);

	/** Symbol - is the head of symbols. */
	public final static IBuiltInSymbol Symbol = F.initFinalSymbol("Symbol", ID.Symbol);

	/** SymbolName(s) - returns the name of the symbol `s` (without any leading context name). */
	public final static IBuiltInSymbol SymbolName = F.initFinalSymbol("SymbolName", ID.SymbolName);

	/** SymbolQ(x) - is `True` if `x` is a symbol, or `False` otherwise. */
	public final static IBuiltInSymbol SymbolQ = F.initFinalSymbol("SymbolQ", ID.SymbolQ);

	/***/
	public final static IBuiltInSymbol Symmetric = F.initFinalSymbol("Symmetric", ID.Symmetric);

	/** SymmetricMatrixQ(m) - returns `True` if `m` is a symmetric matrix. */
	public final static IBuiltInSymbol SymmetricMatrixQ = F.initFinalSymbol("SymmetricMatrixQ", ID.SymmetricMatrixQ);

	/***/
	public final static IBuiltInSymbol SyntaxLength = F.initFinalSymbol("SyntaxLength", ID.SyntaxLength);

	/** SyntaxQ(str) - is `True` if the given `str` is a string which has the correct syntax. */
	public final static IBuiltInSymbol SyntaxQ = F.initFinalSymbol("SyntaxQ", ID.SyntaxQ);

	/** SystemDialogInput("FileOpen") - if the file system is enabled, open a file chooser dialog box. */
	public final static IBuiltInSymbol SystemDialogInput = F.initFinalSymbol("SystemDialogInput", ID.SystemDialogInput);

	/** Table(expr, {i, n}) - evaluates `expr` with `i` ranging from `1` to `n`, returning a list of the results. */
	public final static IBuiltInSymbol Table = F.initFinalSymbol("Table", ID.Table);

	/***/
	public final static IBuiltInSymbol TableForm = F.initFinalSymbol("TableForm", ID.TableForm);

	/***/
	public final static IBuiltInSymbol TableHeadings = F.initFinalSymbol("TableHeadings", ID.TableHeadings);

	/***/
	public final static IBuiltInSymbol TagSet = F.initFinalSymbol("TagSet", ID.TagSet);

	/***/
	public final static IBuiltInSymbol TagSetDelayed = F.initFinalSymbol("TagSetDelayed", ID.TagSetDelayed);

	/** Take(expr, n) - returns `expr` with all but the first `n` leaves removed. */
	public final static IBuiltInSymbol Take = F.initFinalSymbol("Take", ID.Take);

	/***/
	public final static IBuiltInSymbol TakeLargest = F.initFinalSymbol("TakeLargest", ID.TakeLargest);

	/***/
	public final static IBuiltInSymbol TakeLargestBy = F.initFinalSymbol("TakeLargestBy", ID.TakeLargestBy);

	/***/
	public final static IBuiltInSymbol Tally = F.initFinalSymbol("Tally", ID.Tally);

	/** Tan(expr) - returns the tangent of `expr` (measured in radians). */
	public final static IBuiltInSymbol Tan = F.initFinalSymbol("Tan", ID.Tan);

	/** Tanh(z) - returns the hyperbolic tangent of `z`. */
	public final static IBuiltInSymbol Tanh = F.initFinalSymbol("Tanh", ID.Tanh);

	/**
	 * TautologyQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable by all combinations
	 * of boolean `False` and `True` values for the `list-of-variables`.
	 */
	public final static IBuiltInSymbol TautologyQ = F.initFinalSymbol("TautologyQ", ID.TautologyQ);

	/***/
	public final static IBuiltInSymbol Taylor = F.initFinalSymbol("Taylor", ID.Taylor);

	/** TeXForm(expr) - returns the TeX form of the evaluated `expr`. */
	public final static IBuiltInSymbol TeXForm = F.initFinalSymbol("TeXForm", ID.TeXForm);

	/***/
	public final static IBuiltInSymbol TensorDimensions = F.initFinalSymbol("TensorDimensions", ID.TensorDimensions);

	/***/
	public final static IBuiltInSymbol TensorProduct = F.initFinalSymbol("TensorProduct", ID.TensorProduct);

	/***/
	public final static IBuiltInSymbol TensorRank = F.initFinalSymbol("TensorRank", ID.TensorRank);

	/***/
	public final static IBuiltInSymbol TensorSymmetry = F.initFinalSymbol("TensorSymmetry", ID.TensorSymmetry);

	/***/
	public final static IBuiltInSymbol TestID = F.initFinalSymbol("TestID", ID.TestID);

	/***/
	public final static IBuiltInSymbol TestReport = F.initFinalSymbol("TestReport", ID.TestReport);

	/***/
	public final static IBuiltInSymbol TestReportObject = F.initFinalSymbol("TestReportObject", ID.TestReportObject);

	/***/
	public final static IBuiltInSymbol TestResultObject = F.initFinalSymbol("TestResultObject", ID.TestResultObject);

	/***/
	public final static IBuiltInSymbol TextCell = F.initFinalSymbol("TextCell", ID.TextCell);

	/***/
	public final static IBuiltInSymbol TextString = F.initFinalSymbol("TextString", ID.TextString);

	/** Thread(f(args) - threads `f` over any lists that appear in `args`. */
	public final static IBuiltInSymbol Thread = F.initFinalSymbol("Thread", ID.Thread);

	/** Through(p(f)[x]) - gives `p(f(x))`. */
	public final static IBuiltInSymbol Through = F.initFinalSymbol("Through", ID.Through);

	/***/
	public final static IBuiltInSymbol Throw = F.initFinalSymbol("Throw", ID.Throw);

	/***/
	public final static IBuiltInSymbol TimeConstrained = F.initFinalSymbol("TimeConstrained", ID.TimeConstrained);

	/***/
	public final static IBuiltInSymbol TimeObject = F.initFinalSymbol("TimeObject", ID.TimeObject);

	/** TimeValue(p, i, n) - returns a time value calculation. */
	public final static IBuiltInSymbol TimeValue = F.initFinalSymbol("TimeValue", ID.TimeValue);

	/** Times(a, b, ...) - represents the product of the terms `a, b, ...`. */
	public final static IBuiltInSymbol Times = F.initFinalSymbol("Times", ID.Times);

	/** TimesBy(x, dx) - is equivalent to `x = x * dx`. */
	public final static IBuiltInSymbol TimesBy = F.initFinalSymbol("TimesBy", ID.TimesBy);

	/**
	 * Timing(x) - returns a list with the first entry containing the evaluation time of `x` and the second entry is the
	 * evaluation result of `x`.
	 */
	public final static IBuiltInSymbol Timing = F.initFinalSymbol("Timing", ID.Timing);

	/** ToCharacterCode(string) - converts `string` into a list of corresponding integer character codes. */
	public final static IBuiltInSymbol ToCharacterCode = F.initFinalSymbol("ToCharacterCode", ID.ToCharacterCode);

	/** ToExpression("string", form) - converts the `string` given in `form` into an expression. */
	public final static IBuiltInSymbol ToExpression = F.initFinalSymbol("ToExpression", ID.ToExpression);

	/** ToPolarCoordinates({x, y}) - return the polar coordinates for the cartesian coordinates `{x, y}`. */
	public final static IBuiltInSymbol ToPolarCoordinates = F.initFinalSymbol("ToPolarCoordinates",
			ID.ToPolarCoordinates);

	/***/
	public final static IBuiltInSymbol ToRadicals = F.initFinalSymbol("ToRadicals", ID.ToRadicals);

	/** ToString(expr) - converts `expr` into a string. */
	public final static IBuiltInSymbol ToString = F.initFinalSymbol("ToString", ID.ToString);

	/***/
	public final static IBuiltInSymbol ToUnicode = F.initFinalSymbol("ToUnicode", ID.ToUnicode);

	/***/
	public final static IBuiltInSymbol Today = F.initFinalSymbol("Today", ID.Today);

	/** ToeplitzMatrix(n) - gives a toeplitz matrix with the dimension `n`. */
	public final static IBuiltInSymbol ToeplitzMatrix = F.initFinalSymbol("ToeplitzMatrix", ID.ToeplitzMatrix);

	/** Together(expr) - writes sums of fractions in `expr` together. */
	public final static IBuiltInSymbol Together = F.initFinalSymbol("Together", ID.Together);

	/***/
	public final static IBuiltInSymbol TooLarge = F.initFinalSymbol("TooLarge", ID.TooLarge);

	/***/
	public final static IBuiltInSymbol Top = F.initFinalSymbol("Top", ID.Top);

	/** Total(list) - adds all values in `list`. */
	public final static IBuiltInSymbol Total = F.initFinalSymbol("Total", ID.Total);

	/** Tr(matrix) - computes the trace of the `matrix`. */
	public final static IBuiltInSymbol Tr = F.initFinalSymbol("Tr", ID.Tr);

	/** Trace(expr) - return the evaluation steps which are used to get the result. */
	public final static IBuiltInSymbol Trace = F.initFinalSymbol("Trace", ID.Trace);

	/***/
	public final static IBuiltInSymbol TraceForm = F.initFinalSymbol("TraceForm", ID.TraceForm);

	/***/
	public final static IBuiltInSymbol TraditionalForm = F.initFinalSymbol("TraditionalForm", ID.TraditionalForm);

	/***/
	public final static IBuiltInSymbol Transliterate = F.initFinalSymbol("Transliterate", ID.Transliterate);

	/** Transpose(m) - transposes rows and columns in the matrix `m`. */
	public final static IBuiltInSymbol Transpose = F.initFinalSymbol("Transpose", ID.Transpose);

	/** TreeForm(expr) - create a tree visualization from the given expression `expr`. */
	public final static IBuiltInSymbol TreeForm = F.initFinalSymbol("TreeForm", ID.TreeForm);

	/***/
	public final static IBuiltInSymbol Trig = F.initFinalSymbol("Trig", ID.Trig);

	/** TrigExpand(expr) - expands out trigonometric expressions in `expr`. */
	public final static IBuiltInSymbol TrigExpand = F.initFinalSymbol("TrigExpand", ID.TrigExpand);

	/**
	 * TrigReduce(expr) - rewrites products and powers of trigonometric functions in `expr` in terms of trigonometric
	 * functions with combined arguments.
	 */
	public final static IBuiltInSymbol TrigReduce = F.initFinalSymbol("TrigReduce", ID.TrigReduce);

	/** TrigToExp(expr) - converts trigonometric functions in `expr` to exponentials. */
	public final static IBuiltInSymbol TrigToExp = F.initFinalSymbol("TrigToExp", ID.TrigToExp);

	/** True - the constant `True` represents the boolean value **true ***/
	public final static IBuiltInSymbol True = F.initFinalSymbol("True", ID.True);

	/** TrueQ(expr) - returns `True` if and only if `expr` is `True`. */
	public final static IBuiltInSymbol TrueQ = F.initFinalSymbol("TrueQ", ID.TrueQ);

	/***/
	public final static IBuiltInSymbol TukeyWindow = F.initFinalSymbol("TukeyWindow", ID.TukeyWindow);

	/** Tuples(list, n) - creates a list of all `n`-tuples of elements in `list`. */
	public final static IBuiltInSymbol Tuples = F.initFinalSymbol("Tuples", ID.Tuples);

	/***/
	public final static IBuiltInSymbol TwoWayRule = F.initFinalSymbol("TwoWayRule", ID.TwoWayRule);

	/***/
	public final static IBuiltInSymbol Undefined = F.initFinalSymbol("Undefined", ID.Undefined);

	/***/
	public final static IBuiltInSymbol Underoverscript = F.initFinalSymbol("Underoverscript", ID.Underoverscript);

	/***/
	public final static IBuiltInSymbol UndirectedEdge = F.initFinalSymbol("UndirectedEdge", ID.UndirectedEdge);

	/**
	 * Unequal(x, y) - yields `False` if `x` and `y` are known to be equal, or `True` if `x` and `y` are known to be
	 * unequal.
	 */
	public final static IBuiltInSymbol Unequal = F.initFinalSymbol("Unequal", ID.Unequal);

	/***/
	public final static IBuiltInSymbol Unevaluated = F.initFinalSymbol("Unevaluated", ID.Unevaluated);

	/** UniformDistribution({min, max}) - returns a uniform distribution. */
	public final static IBuiltInSymbol UniformDistribution = F.initFinalSymbol("UniformDistribution",
			ID.UniformDistribution);

	/** Union(set1, set2) - get the union set from `set1` and `set2`. */
	public final static IBuiltInSymbol Union = F.initFinalSymbol("Union", ID.Union);

	/** Unique(expr) - create a unique symbol of the form `expr$...`. */
	public final static IBuiltInSymbol Unique = F.initFinalSymbol("Unique", ID.Unique);

	/** UnitConvert(quantity) - convert the `quantity` to the base unit */
	public final static IBuiltInSymbol UnitConvert = F.initFinalSymbol("UnitConvert", ID.UnitConvert);

	/**
	 * UnitStep(expr) - returns `0`, if `expr` is less than `0` and returns `1`, if `expr` is greater equal than `0`.
	 */
	public final static IBuiltInSymbol UnitStep = F.initFinalSymbol("UnitStep", ID.UnitStep);

	/** UnitVector(position) - returns a unit vector with element `1` at the given `position`. */
	public final static IBuiltInSymbol UnitVector = F.initFinalSymbol("UnitVector", ID.UnitVector);

	/***/
	public final static IBuiltInSymbol UnitaryMatrixQ = F.initFinalSymbol("UnitaryMatrixQ", ID.UnitaryMatrixQ);

	/** Unitize(expr) - maps a non-zero `expr` to `1`, and a zero `expr` to `0`. */
	public final static IBuiltInSymbol Unitize = F.initFinalSymbol("Unitize", ID.Unitize);

	/***/
	public final static IBuiltInSymbol Unknown = F.initFinalSymbol("Unknown", ID.Unknown);

	/***/
	public final static IBuiltInSymbol Unprotect = F.initFinalSymbol("Unprotect", ID.Unprotect);

	/** UnsameQ(x, y) - returns `True` if `x` and `y` are not structurally identical. */
	public final static IBuiltInSymbol UnsameQ = F.initFinalSymbol("UnsameQ", ID.UnsameQ);

	/** Unset(expr) - removes any definitions belonging to the left-hand-side `expr`. */
	public final static IBuiltInSymbol Unset = F.initFinalSymbol("Unset", ID.Unset);

	/***/
	public final static IBuiltInSymbol UpSet = F.initFinalSymbol("UpSet", ID.UpSet);

	/***/
	public final static IBuiltInSymbol UpSetDelayed = F.initFinalSymbol("UpSetDelayed", ID.UpSetDelayed);

	/***/
	public final static IBuiltInSymbol UpTo = F.initFinalSymbol("UpTo", ID.UpTo);

	/***/
	public final static IBuiltInSymbol UpValues = F.initFinalSymbol("UpValues", ID.UpValues);

	/** UpperCaseQ(str) - is `True` if the given `str` is a string which only contains upper case characters. */
	public final static IBuiltInSymbol UpperCaseQ = F.initFinalSymbol("UpperCaseQ", ID.UpperCaseQ);

	/** UpperTriangularize(matrix) - create a upper triangular matrix from the given `matrix`. */
	public final static IBuiltInSymbol UpperTriangularize = F.initFinalSymbol("UpperTriangularize",
			ID.UpperTriangularize);

	/** ValueQ(expr) - returns `True` if and only if `expr` is defined. */
	public final static IBuiltInSymbol ValueQ = F.initFinalSymbol("ValueQ", ID.ValueQ);

	/** Values(association) - return a list of values of the `association`. */
	public final static IBuiltInSymbol Values = F.initFinalSymbol("Values", ID.Values);

	/** VandermondeMatrix(n) - gives the Vandermonde matrix with `n` rows and columns. */
	public final static IBuiltInSymbol VandermondeMatrix = F.initFinalSymbol("VandermondeMatrix", ID.VandermondeMatrix);

	/***/
	public final static IBuiltInSymbol Variable = F.initFinalSymbol("Variable", ID.Variable);

	/** Variables(expr) - gives a list of the variables that appear in the polynomial `expr`. */
	public final static IBuiltInSymbol Variables = F.initFinalSymbol("Variables", ID.Variables);

	/**
	 * Variance(list) - computes the variance of `list`. `list` may consist of numerical values or symbols. Numerical
	 * values may be real or complex.
	 */
	public final static IBuiltInSymbol Variance = F.initFinalSymbol("Variance", ID.Variance);

	/** VectorAngle(u, v) - gives the angles between vectors `u` and `v` */
	public final static IBuiltInSymbol VectorAngle = F.initFinalSymbol("VectorAngle", ID.VectorAngle);

	/** VectorQ(v) - returns `True` if `v` is a list of elements which are not themselves lists. */
	public final static IBuiltInSymbol VectorQ = F.initFinalSymbol("VectorQ", ID.VectorQ);

	/***/
	public final static IBuiltInSymbol VerificationTest = F.initFinalSymbol("VerificationTest", ID.VerificationTest);

	/**
	 * VertexEccentricity(graph, vertex) - compute the eccentricity of `vertex` in the `graph`. It's the length of the
	 * longest shortest path from the `vertex` to every other vertex in the `graph`.
	 */
	public final static IBuiltInSymbol VertexEccentricity = F.initFinalSymbol("VertexEccentricity",
			ID.VertexEccentricity);

	/** VertexList(graph) - convert the `graph` into a list of vertices. */
	public final static IBuiltInSymbol VertexList = F.initFinalSymbol("VertexList", ID.VertexList);

	/** VertexQ(graph, vertex) - test if `vertex` is a vertex in the `graph` object. */
	public final static IBuiltInSymbol VertexQ = F.initFinalSymbol("VertexQ", ID.VertexQ);

	/***/
	public final static IBuiltInSymbol ViewPoint = F.initFinalSymbol("ViewPoint", ID.ViewPoint);

	/** WeibullDistribution(a, b) - returns a Weibull distribution. */
	public final static IBuiltInSymbol WeibullDistribution = F.initFinalSymbol("WeibullDistribution",
			ID.WeibullDistribution);

	/***/
	public final static IBuiltInSymbol WeierstrassHalfPeriods = F.initFinalSymbol("WeierstrassHalfPeriods",
			ID.WeierstrassHalfPeriods);

	/***/
	public final static IBuiltInSymbol WeierstrassInvariants = F.initFinalSymbol("WeierstrassInvariants",
			ID.WeierstrassInvariants);

	/** WeierstrassP(expr, {n1, n2}) - Weierstrass elliptic function. */
	public final static IBuiltInSymbol WeierstrassP = F.initFinalSymbol("WeierstrassP", ID.WeierstrassP);

	/***/
	public final static IBuiltInSymbol WeierstrassPPrime = F.initFinalSymbol("WeierstrassPPrime", ID.WeierstrassPPrime);

	/***/
	public final static IBuiltInSymbol WeightedAdjacencyMatrix = F.initFinalSymbol("WeightedAdjacencyMatrix",
			ID.WeightedAdjacencyMatrix);

	/***/
	public final static IBuiltInSymbol WeightedData = F.initFinalSymbol("WeightedData", ID.WeightedData);

	/**
	 * Which(cond1, expr1, cond2, expr2, ...) - yields `expr1` if `cond1` evaluates to `True`, `expr2` if `cond2`
	 * evaluates to `True`, etc.
	 */
	public final static IBuiltInSymbol Which = F.initFinalSymbol("Which", ID.Which);

	/** While(test, body) - evaluates `body` as long as test evaluates to `True`. */
	public final static IBuiltInSymbol While = F.initFinalSymbol("While", ID.While);

	/***/
	public final static IBuiltInSymbol White = F.initFinalSymbol("White", ID.White);

	/***/
	public final static IBuiltInSymbol Whitespace = F.initFinalSymbol("Whitespace", ID.Whitespace);

	/***/
	public final static IBuiltInSymbol WhitespaceCharacter = F.initFinalSymbol("WhitespaceCharacter",
			ID.WhitespaceCharacter);

	/***/
	public final static IBuiltInSymbol WhittakerM = F.initFinalSymbol("WhittakerM", ID.WhittakerM);

	/***/
	public final static IBuiltInSymbol WhittakerW = F.initFinalSymbol("WhittakerW", ID.WhittakerW);

	/**
	 * With({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables` by replacing the
	 * local variables in `expr`.
	 */
	public final static IBuiltInSymbol With = F.initFinalSymbol("With", ID.With);

	/***/
	public final static IBuiltInSymbol WordBoundary = F.initFinalSymbol("WordBoundary", ID.WordBoundary);

	/***/
	public final static IBuiltInSymbol WordCharacter = F.initFinalSymbol("WordCharacter", ID.WordCharacter);

	/***/
	public final static IBuiltInSymbol WriteString = F.initFinalSymbol("WriteString", ID.WriteString);

	/**
	 * Xor(arg1, arg2, ...) - Logical XOR (exclusive OR) function. Returns `True` if an odd number of the arguments are
	 * `True` and the rest are `False`. Returns `False` if an even number of the arguments are `True` and the rest are
	 * `False`.
	 */
	public final static IBuiltInSymbol Xor = F.initFinalSymbol("Xor", ID.Xor);

	/***/
	public final static IBuiltInSymbol Yellow = F.initFinalSymbol("Yellow", ID.Yellow);

	/**
	 * YuleDissimilarity(u, v) - returns the Yule dissimilarity between the two boolean 1-D lists `u` and `v`, which is
	 * defined as `R / (c_tt * c_ff + R / 2)` where `n` is `len(u)`, `c_ij` is the number of occurrences of `u(k)=i` and
	 * `v(k)=j` for `k<n`, and `R = 2 * c_tf * c_ft`.
	 */
	public final static IBuiltInSymbol YuleDissimilarity = F.initFinalSymbol("YuleDissimilarity", ID.YuleDissimilarity);

	/***/
	public final static IBuiltInSymbol ZeroSymmetric = F.initFinalSymbol("ZeroSymmetric", ID.ZeroSymmetric);

	/** Zeta(z) - returns the Riemann zeta function of `z`. */
	public final static IBuiltInSymbol Zeta = F.initFinalSymbol("Zeta", ID.Zeta);

	public final static ISymbol $RealVector = initFinalHiddenSymbol(
			FEConfig.PARSER_USE_LOWERCASE_SYMBOLS ? "$realvector" : "$RealVector");
	public final static ISymbol $RealMatrix = initFinalHiddenSymbol(
			FEConfig.PARSER_USE_LOWERCASE_SYMBOLS ? "$realmatrix" : "$RealMatrix");

	/** Used to represent a formal parameter <code>a</code> that will never be assigned a value. */
	public final static ISymbol a = initFinalHiddenSymbol("a");
	/** Used to represent a formal parameter <code>b</code> that will never be assigned a value. */
	public final static ISymbol b = initFinalHiddenSymbol("b");
	/** Used to represent a formal parameter <code>c</code> that will never be assigned a value. */
	public final static ISymbol c = initFinalHiddenSymbol("c");
	/** Used to represent a formal parameter <code>d</code> that will never be assigned a value. */
	public final static ISymbol d = initFinalHiddenSymbol("d");
	/** Used to represent a formal parameter <code>e</code> that will never be assigned a value. */
	public final static ISymbol e = initFinalHiddenSymbol("e");
	/** Used to represent a formal parameter <code>f</code> that will never be assigned a value. */
	public final static ISymbol f = initFinalHiddenSymbol("f");
	/** Used to represent a formal parameter <code>g</code> that will never be assigned a value. */
	public final static ISymbol g = initFinalHiddenSymbol("g");
	/** Used to represent a formal parameter <code>h</code> that will never be assigned a value. */
	public final static ISymbol h = initFinalHiddenSymbol("h");
	/** Used to represent a formal parameter <code>i</code> that will never be assigned a value. */
	public final static ISymbol i = initFinalHiddenSymbol("i");
	/** Used to represent a formal parameter <code>j</code> that will never be assigned a value. */
	public final static ISymbol j = initFinalHiddenSymbol("j");
	/** Used to represent a formal parameter <code>k</code> that will never be assigned a value. */
	public final static ISymbol k = initFinalHiddenSymbol("k");
	/** Used to represent a formal parameter <code>l</code> that will never be assigned a value. */
	public final static ISymbol l = initFinalHiddenSymbol("l");
	/** Used to represent a formal parameter <code>m</code> that will never be assigned a value. */
	public final static ISymbol m = initFinalHiddenSymbol("m");
	/** Used to represent a formal parameter <code>n</code> that will never be assigned a value. */
	public final static ISymbol n = initFinalHiddenSymbol("n");
	/** Used to represent a formal parameter <code>o</code> that will never be assigned a value. */
	public final static ISymbol o = initFinalHiddenSymbol("o");
	/** Used to represent a formal parameter <code>p</code> that will never be assigned a value. */
	public final static ISymbol p = initFinalHiddenSymbol("p");
	/** Used to represent a formal parameter <code>q</code> that will never be assigned a value. */
	public final static ISymbol q = initFinalHiddenSymbol("q");
	/** Used to represent a formal parameter <code>r</code> that will never be assigned a value. */
	public final static ISymbol r = initFinalHiddenSymbol("r");
	/** Used to represent a formal parameter <code>s</code> that will never be assigned a value. */
	public final static ISymbol s = initFinalHiddenSymbol("s");
	/** Used to represent a formal parameter <code>t</code> that will never be assigned a value. */
	public final static ISymbol t = initFinalHiddenSymbol("t");
	/** Used to represent a formal parameter <code>u</code> that will never be assigned a value. */
	public final static ISymbol u = initFinalHiddenSymbol("u");
	/** Used to represent a formal parameter <code>v</code> that will never be assigned a value. */
	public final static ISymbol v = initFinalHiddenSymbol("v");
	/** Used to represent a formal parameter <code>w</code> that will never be assigned a value. */
	public final static ISymbol w = initFinalHiddenSymbol("w");
	/** Used to represent a formal parameter <code>x</code> that will never be assigned a value. */
	public final static ISymbol x = initFinalHiddenSymbol("x");
	/** Used to represent a formal parameter <code>y</code> that will never be assigned a value. */
	public final static ISymbol y = initFinalHiddenSymbol("y");
	/** Used to represent a formal parameter <code>z</code> that will never be assigned a value. */
	public final static ISymbol z = initFinalHiddenSymbol("z");

	public final static ISymbol ASymbol = initFinalHiddenSymbol("A");
	public final static ISymbol BSymbol = initFinalHiddenSymbol("B");
	public final static ISymbol CSymbol = initFinalHiddenSymbol("C"); // don't use constant BuiltinSymbol 'C' here
	public final static ISymbol FSymbol = initFinalHiddenSymbol("F");
	public final static ISymbol GSymbol = initFinalHiddenSymbol("G");
	public final static ISymbol PSymbol = initFinalHiddenSymbol("P");
	public final static ISymbol QSymbol = initFinalHiddenSymbol("Q");

	/**
	 * Used to represent a formal parameter <code>LHS_HEAD</code> that will never be assigned a value. Used for setting
	 * the left-hand-side in pattern-matching for <code>OptionValue(...)</code>
	 */
	public final static ISymbol LHS_HEAD = initFinalHiddenSymbol("LHSHead");

	/**
	 * Convert the symbolName to lowercase (if <code>Config.PARSER_USE_LOWERCASE_SYMBOLS</code> is set) and insert a new
	 * Symbol in the <code>PREDEFINED_SYMBOLS_MAP</code>. The symbol is created using the given upper case string to use
	 * it as associated class name in package org.matheclipse.core.reflection.system.
	 * 
	 * @param symbolName
	 *            the predefined symbol name in upper-case form
	 * @param ordinal
	 * @return
	 */
	public static IBuiltInSymbol initFinalSymbol(final String symbolName, int ordinal) {
		String str;
		if (FEConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
			str = (symbolName.length() == 1) ? symbolName : symbolName.toLowerCase();
		} else {
			str = symbolName;
		}
		final IBuiltInSymbol temp = new BuiltInSymbol(str, ordinal);
		BUILT_IN_SYMBOLS[ordinal] = temp;
		org.matheclipse.core.expression.Context.SYSTEM.put(str, temp);
		GLOBAL_IDS_MAP.put(temp, (short) ordinal);
		return temp;
	}

	/**
	 * Convert the symbolName to lowercase (if <code>Config.PARSER_USE_LOWERCASE_SYMBOLS</code> is set) and insert a new
	 * Symbol in the <code>PREDEFINED_SYMBOLS_MAP</code>. The symbol is created using the given upper case string to use
	 * it as associated class name in package org.matheclipse.core.reflection.system.
	 * 
	 * @param symbolName
	 *            the predefined symbol name in upper-case form
	 * @return
	 */
	public static ISymbol initFinalHiddenSymbol(final String symbolName) {
		final ISymbol temp = new Symbol(symbolName, org.matheclipse.core.expression.Context.DUMMY);
		HIDDEN_SYMBOLS_MAP.put(symbolName, temp);
		return temp;
	}

	public static IExpr exprID(short id) {
		if (id >= EXPRID_MAX_BUILTIN_LENGTH) {
			return COMMON_IDS[id - EXPRID_MAX_BUILTIN_LENGTH];
		}
		return BUILT_IN_SYMBOLS[id];
	}
}
