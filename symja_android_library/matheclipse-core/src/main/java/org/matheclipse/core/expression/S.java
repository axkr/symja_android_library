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
	public final static IBuiltInSymbol $Aborted = initFinalSymbol("$Aborted", ID.$Aborted);

	/***/
	public final static IBuiltInSymbol $Assumptions = initFinalSymbol("$Assumptions", ID.$Assumptions);

	/***/
	public final static IBuiltInSymbol $Cancel = initFinalSymbol("$Cancel", ID.$Cancel);

	/***/
	public final static IBuiltInSymbol $Context = initFinalSymbol("$Context", ID.$Context);

	/***/
	public final static IBuiltInSymbol $ContextPath = initFinalSymbol("$ContextPath", ID.$ContextPath);

	/***/
	public final static IBuiltInSymbol $CreationDate = initFinalSymbol("$CreationDate", ID.$CreationDate);

	/***/
	public final static IBuiltInSymbol $DisplayFunction = initFinalSymbol("$DisplayFunction", ID.$DisplayFunction);

	/***/
	public final static IBuiltInSymbol $Failed = initFinalSymbol("$Failed", ID.$Failed);

	/***/
	public final static IBuiltInSymbol $HistoryLength = initFinalSymbol("$HistoryLength", ID.$HistoryLength);

	/***/
	public final static IBuiltInSymbol $HomeDirectory = initFinalSymbol("$HomeDirectory", ID.$HomeDirectory);

	/***/
	public final static IBuiltInSymbol $IterationLimit = initFinalSymbol("$IterationLimit", ID.$IterationLimit);

	/***/
	public final static IBuiltInSymbol $Line = initFinalSymbol("$Line", ID.$Line);

	/***/
	public final static IBuiltInSymbol $MachineEpsilon = initFinalSymbol("$MachineEpsilon", ID.$MachineEpsilon);

	/***/
	public final static IBuiltInSymbol $MachinePrecision = initFinalSymbol("$MachinePrecision", ID.$MachinePrecision);

	/***/
	public final static IBuiltInSymbol $MaxMachineNumber = initFinalSymbol("$MaxMachineNumber", ID.$MaxMachineNumber);

	/***/
	public final static IBuiltInSymbol $MessageList = initFinalSymbol("$MessageList", ID.$MessageList);

	/***/
	public final static IBuiltInSymbol $MinMachineNumber = initFinalSymbol("$MinMachineNumber", ID.$MinMachineNumber);

	/***/
	public final static IBuiltInSymbol $OutputSizeLimit = initFinalSymbol("$OutputSizeLimit", ID.$OutputSizeLimit);

	/***/
	public final static IBuiltInSymbol $PrePrint = initFinalSymbol("$PrePrint", ID.$PrePrint);

	/***/
	public final static IBuiltInSymbol $PreRead = initFinalSymbol("$PreRead", ID.$PreRead);

	/***/
	public final static IBuiltInSymbol $RecursionLimit = initFinalSymbol("$RecursionLimit", ID.$RecursionLimit);

	/***/
	public final static IBuiltInSymbol $UserName = initFinalSymbol("$UserName", ID.$UserName);

	/***/
	public final static IBuiltInSymbol $Version = initFinalSymbol("$Version", ID.$Version);

	/** Abort() - aborts an evaluation completely and returns `$Aborted`. */
	public final static IBuiltInSymbol Abort = initFinalSymbol("Abort", ID.Abort);

	/** Abs(expr) - returns the absolute value of the real or complex number `expr`. */
	public final static IBuiltInSymbol Abs = initFinalSymbol("Abs", ID.Abs);

	/** AbsArg(expr) - returns a list of 2 values of the complex number `Abs(expr), Arg(expr)`. */
	public final static IBuiltInSymbol AbsArg = initFinalSymbol("AbsArg", ID.AbsArg);

	/***/
	public final static IBuiltInSymbol AbsoluteCorrelation = initFinalSymbol("AbsoluteCorrelation",
			ID.AbsoluteCorrelation);

	/***/
	public final static IBuiltInSymbol AbsoluteTime = initFinalSymbol("AbsoluteTime", ID.AbsoluteTime);

	/** Accumulate(list) - accumulate the values of `list` returning a new list. */
	public final static IBuiltInSymbol Accumulate = initFinalSymbol("Accumulate", ID.Accumulate);

	/** AddTo(x, dx) - is equivalent to `x = x + dx`. */
	public final static IBuiltInSymbol AddTo = initFinalSymbol("AddTo", ID.AddTo);

	/** AdjacencyMatrix(graph) - convert the `graph` into a adjacency matrix. */
	public final static IBuiltInSymbol AdjacencyMatrix = initFinalSymbol("AdjacencyMatrix", ID.AdjacencyMatrix);

	/***/
	public final static IBuiltInSymbol AiryAi = initFinalSymbol("AiryAi", ID.AiryAi);

	/***/
	public final static IBuiltInSymbol AiryAiPrime = initFinalSymbol("AiryAiPrime", ID.AiryAiPrime);

	/***/
	public final static IBuiltInSymbol AiryBi = initFinalSymbol("AiryBi", ID.AiryBi);

	/***/
	public final static IBuiltInSymbol AiryBiPrime = initFinalSymbol("AiryBiPrime", ID.AiryBiPrime);

	/***/
	public final static IBuiltInSymbol AlgebraicNumber = initFinalSymbol("AlgebraicNumber", ID.AlgebraicNumber);

	/***/
	public final static IBuiltInSymbol Algebraics = initFinalSymbol("Algebraics", ID.Algebraics);

	/** All - is a possible value for `Span` and `Quiet`. */
	public final static IBuiltInSymbol All = initFinalSymbol("All", ID.All);

	/**
	 * AllTrue({expr1, expr2, ...}, test) - returns `True` if all applications of `test` to `expr1, expr2, ...` evaluate
	 * to `True`.
	 */
	public final static IBuiltInSymbol AllTrue = initFinalSymbol("AllTrue", ID.AllTrue);

	/** Alternatives(p1, p2, ..., p_i) - is a pattern that matches any of the patterns `p1, p2,...., p_i`. */
	public final static IBuiltInSymbol Alternatives = initFinalSymbol("Alternatives", ID.Alternatives);

	/**
	 * And(expr1, expr2, ...) - `expr1 && expr2 && ...` evaluates each expression in turn, returning `False` as soon as
	 * an expression evaluates to `False`. If all expressions evaluate to `True`, `And` returns `True`.
	 */
	public final static IBuiltInSymbol And = initFinalSymbol("And", ID.And);

	/** AngleVector(phi) - returns the point at angle `phi` on the unit circle. */
	public final static IBuiltInSymbol AngleVector = initFinalSymbol("AngleVector", ID.AngleVector);

	/** Annuity(p, t) - returns an annuity object. */
	public final static IBuiltInSymbol Annuity = initFinalSymbol("Annuity", ID.Annuity);

	/** AnnuityDue(p, t) - returns an annuity due object. */
	public final static IBuiltInSymbol AnnuityDue = initFinalSymbol("AnnuityDue", ID.AnnuityDue);

	/***/
	public final static IBuiltInSymbol AntiSymmetric = initFinalSymbol("AntiSymmetric", ID.AntiSymmetric);

	/** AntihermitianMatrixQ(m) - returns `True` if `m` is a anti hermitian matrix. */
	public final static IBuiltInSymbol AntihermitianMatrixQ = initFinalSymbol("AntihermitianMatrixQ",
			ID.AntihermitianMatrixQ);

	/** AntisymmetricMatrixQ(m) - returns `True` if `m` is a anti symmetric matrix. */
	public final static IBuiltInSymbol AntisymmetricMatrixQ = initFinalSymbol("AntisymmetricMatrixQ",
			ID.AntisymmetricMatrixQ);

	/**
	 * AnyTrue({expr1, expr2, ...}, test) - returns `True` if any application of `test` to `expr1, expr2, ...` evaluates
	 * to `True`.
	 */
	public final static IBuiltInSymbol AnyTrue = initFinalSymbol("AnyTrue", ID.AnyTrue);

	/** Apart(expr) - rewrites `expr` as a sum of individual fractions. */
	public final static IBuiltInSymbol Apart = initFinalSymbol("Apart", ID.Apart);

	/***/
	public final static IBuiltInSymbol AppellF1 = initFinalSymbol("AppellF1", ID.AppellF1);

	/** Append(expr, item) - returns `expr` with `item` appended to its leaves. */
	public final static IBuiltInSymbol Append = initFinalSymbol("Append", ID.Append);

	/** AppendTo(s, item) - append `item` to value of `s` and sets `s` to the result. */
	public final static IBuiltInSymbol AppendTo = initFinalSymbol("AppendTo", ID.AppendTo);

	/** f @ expr - returns `f(expr)` */
	public final static IBuiltInSymbol Apply = initFinalSymbol("Apply", ID.Apply);

	/** ArcCos(expr) - returns the arc cosine (inverse cosine) of `expr` (measured in radians). */
	public final static IBuiltInSymbol ArcCos = initFinalSymbol("ArcCos", ID.ArcCos);

	/** ArcCosh(z) - returns the inverse hyperbolic cosine of `z`. */
	public final static IBuiltInSymbol ArcCosh = initFinalSymbol("ArcCosh", ID.ArcCosh);

	/** ArcCot(z) - returns the inverse cotangent of `z`. */
	public final static IBuiltInSymbol ArcCot = initFinalSymbol("ArcCot", ID.ArcCot);

	/** ArcCoth(z) - returns the inverse hyperbolic cotangent of `z`. */
	public final static IBuiltInSymbol ArcCoth = initFinalSymbol("ArcCoth", ID.ArcCoth);

	/** ArcCsc(z) - returns the inverse cosecant of `z`. */
	public final static IBuiltInSymbol ArcCsc = initFinalSymbol("ArcCsc", ID.ArcCsc);

	/** ArcCsch(z) - returns the inverse hyperbolic cosecant of `z`. */
	public final static IBuiltInSymbol ArcCsch = initFinalSymbol("ArcCsch", ID.ArcCsch);

	/** ArcSec(z) - returns the inverse secant of `z`. */
	public final static IBuiltInSymbol ArcSec = initFinalSymbol("ArcSec", ID.ArcSec);

	/** ArcSech(z) - returns the inverse hyperbolic secant of `z`. */
	public final static IBuiltInSymbol ArcSech = initFinalSymbol("ArcSech", ID.ArcSech);

	/** ArcSin(expr) - returns the arc sine (inverse sine) of `expr` (measured in radians). */
	public final static IBuiltInSymbol ArcSin = initFinalSymbol("ArcSin", ID.ArcSin);

	/** ArcSinh(z) - returns the inverse hyperbolic sine of `z`. */
	public final static IBuiltInSymbol ArcSinh = initFinalSymbol("ArcSinh", ID.ArcSinh);

	/** ArcTan(expr) - returns the arc tangent (inverse tangent) of `expr` (measured in radians). */
	public final static IBuiltInSymbol ArcTan = initFinalSymbol("ArcTan", ID.ArcTan);

	/** ArcTanh(z) - returns the inverse hyperbolic tangent of `z`. */
	public final static IBuiltInSymbol ArcTanh = initFinalSymbol("ArcTanh", ID.ArcTanh);

	/** Arg(expr) - returns the argument of the complex number `expr`. */
	public final static IBuiltInSymbol Arg = initFinalSymbol("Arg", ID.Arg);

	/** ArgMax(function, variable) - returns a maximizer point for a univariate `function`. */
	public final static IBuiltInSymbol ArgMax = initFinalSymbol("ArgMax", ID.ArgMax);

	/** ArgMin(function, variable) - returns a minimizer point for a univariate `function`. */
	public final static IBuiltInSymbol ArgMin = initFinalSymbol("ArgMin", ID.ArgMin);

	/** ArithmeticGeometricMean({a, b, c,...}) - returns the arithmetic geometric mean of `{a, b, c,...}`. */
	public final static IBuiltInSymbol ArithmeticGeometricMean = initFinalSymbol("ArithmeticGeometricMean",
			ID.ArithmeticGeometricMean);

	/** Array(f, n) - returns the `n`-element list `{f(1), ..., f(n)}`. */
	public final static IBuiltInSymbol Array = initFinalSymbol("Array", ID.Array);

	/** ArrayDepth(a) - returns the depth of the non-ragged array `a`, defined as `Length(Dimensions(a))`. */
	public final static IBuiltInSymbol ArrayDepth = initFinalSymbol("ArrayDepth", ID.ArrayDepth);

	/** ArrayPad(list, n) - adds `n` times `0` on the left and right of the `list`. */
	public final static IBuiltInSymbol ArrayPad = initFinalSymbol("ArrayPad", ID.ArrayPad);

	/** ArrayQ(expr) - tests whether expr is a full array. */
	public final static IBuiltInSymbol ArrayQ = initFinalSymbol("ArrayQ", ID.ArrayQ);

	/**
	 * ArrayReshape(list-of-values, list-of-dimension) - returns the `list-of-values` elements reshaped as nested list
	 * with dimensions according to the `list-of-dimension`.
	 */
	public final static IBuiltInSymbol ArrayReshape = initFinalSymbol("ArrayReshape", ID.ArrayReshape);

	/***/
	public final static IBuiltInSymbol Arrays = initFinalSymbol("Arrays", ID.Arrays);

	/** Association(list-of-rules) - create a `key->value` association map from the `list-of-rules`. */
	public final static IBuiltInSymbol Association = initFinalSymbol("Association", ID.Association);

	/** AssociationQ(expr) - returns `True` if `expr` is an association, and `False` otherwise. */
	public final static IBuiltInSymbol AssociationQ = initFinalSymbol("AssociationQ", ID.AssociationQ);

	/***/
	public final static IBuiltInSymbol Assumptions = initFinalSymbol("Assumptions", ID.Assumptions);

	/**
	 * AtomQ(x) - is true if `x` is an atom (an object such as a number or string, which cannot be divided into
	 * subexpressions using 'Part').
	 */
	public final static IBuiltInSymbol AtomQ = initFinalSymbol("AtomQ", ID.AtomQ);

	/** Attributes(symbol) - returns the list of attributes which are assigned to `symbol` */
	public final static IBuiltInSymbol Attributes = initFinalSymbol("Attributes", ID.Attributes);

	/***/
	public final static IBuiltInSymbol Automatic = initFinalSymbol("Automatic", ID.Automatic);

	/***/
	public final static IBuiltInSymbol Axes = initFinalSymbol("Axes", ID.Axes);

	/***/
	public final static IBuiltInSymbol AxesOrigin = initFinalSymbol("AxesOrigin", ID.AxesOrigin);

	/***/
	public final static IBuiltInSymbol AxesStyle = initFinalSymbol("AxesStyle", ID.AxesStyle);

	/***/
	public final static IBuiltInSymbol Background = initFinalSymbol("Background", ID.Background);

	/***/
	public final static IBuiltInSymbol BarChart = initFinalSymbol("BarChart", ID.BarChart);

	/***/
	public final static IBuiltInSymbol BarOrigin = initFinalSymbol("BarOrigin", ID.BarOrigin);

	/***/
	public final static IBuiltInSymbol BartlettWindow = initFinalSymbol("BartlettWindow", ID.BartlettWindow);

	/** BaseForm(integer, radix) - prints the `integer` number in base `radix` form. */
	public final static IBuiltInSymbol BaseForm = initFinalSymbol("BaseForm", ID.BaseForm);

	/** Begin("<context-name>") - start a new context definition */
	public final static IBuiltInSymbol Begin = initFinalSymbol("Begin", ID.Begin);

	/** BeginPackage("<context-name>") - start a new package definition */
	public final static IBuiltInSymbol BeginPackage = initFinalSymbol("BeginPackage", ID.BeginPackage);

	/**
	 * BellB(n) - the Bell number function counts the number of different ways to partition a set that has exactly `n`
	 * elements
	 */
	public final static IBuiltInSymbol BellB = initFinalSymbol("BellB", ID.BellB);

	/** BellY(n, k, {x1, x2, ... , xN}) - the second kind of Bell polynomials (incomplete Bell polynomials). */
	public final static IBuiltInSymbol BellY = initFinalSymbol("BellY", ID.BellY);

	/** BernoulliB(expr) - computes the Bernoulli number of the first kind. */
	public final static IBuiltInSymbol BernoulliB = initFinalSymbol("BernoulliB", ID.BernoulliB);

	/** BernoulliDistribution(p) - returns the Bernoulli distribution. */
	public final static IBuiltInSymbol BernoulliDistribution = initFinalSymbol("BernoulliDistribution",
			ID.BernoulliDistribution);

	/** BesselI(n, z) - modified Bessel function of the first kind. */
	public final static IBuiltInSymbol BesselI = initFinalSymbol("BesselI", ID.BesselI);

	/** BesselJ(n, z) - Bessel function of the first kind. */
	public final static IBuiltInSymbol BesselJ = initFinalSymbol("BesselJ", ID.BesselJ);

	/** BesselJZero(n, z) - is the `k`th zero of the `BesselJ(n,z)` function. */
	public final static IBuiltInSymbol BesselJZero = initFinalSymbol("BesselJZero", ID.BesselJZero);

	/** BesselK(n, z) - modified Bessel function of the second kind. */
	public final static IBuiltInSymbol BesselK = initFinalSymbol("BesselK", ID.BesselK);

	/** BesselY(n, z) - Bessel function of the second kind. */
	public final static IBuiltInSymbol BesselY = initFinalSymbol("BesselY", ID.BesselY);

	/***/
	public final static IBuiltInSymbol BesselYZero = initFinalSymbol("BesselYZero", ID.BesselYZero);

	/** Beta(a, b) - is the beta function of the numbers `a`,`b`. */
	public final static IBuiltInSymbol Beta = initFinalSymbol("Beta", ID.Beta);

	/***/
	public final static IBuiltInSymbol BetaDistribution = initFinalSymbol("BetaDistribution", ID.BetaDistribution);

	/***/
	public final static IBuiltInSymbol BetaRegularized = initFinalSymbol("BetaRegularized", ID.BetaRegularized);

	/**
	 * BinCounts(list, width-of-bin) - count the number of elements, if `list`, is divided into successive bins with
	 * width `width-of-bin`.
	 */
	public final static IBuiltInSymbol BinCounts = initFinalSymbol("BinCounts", ID.BinCounts);

	/** BinaryDeserialize(byte-array) - deserialize the `byte-array` into a Symja expression. */
	public final static IBuiltInSymbol BinaryDeserialize = initFinalSymbol("BinaryDeserialize", ID.BinaryDeserialize);

	/** BinarySerialize(expr) - serialize the `expr` into a byte array expression. */
	public final static IBuiltInSymbol BinarySerialize = initFinalSymbol("BinarySerialize", ID.BinarySerialize);

	/** Binomial(n, k) - returns the binomial coefficient of the 2 integers `n` and `k` */
	public final static IBuiltInSymbol Binomial = initFinalSymbol("Binomial", ID.Binomial);

	/** BinomialDistribution(n, p) - returns the binomial distribution. */
	public final static IBuiltInSymbol BinomialDistribution = initFinalSymbol("BinomialDistribution",
			ID.BinomialDistribution);

	/** BitLengthi(x) - gives the number of bits needed to represent the integer `x`. The sign of `x` is ignored. */
	public final static IBuiltInSymbol BitLength = initFinalSymbol("BitLength", ID.BitLength);

	/***/
	public final static IBuiltInSymbol BlackmanHarrisWindow = initFinalSymbol("BlackmanHarrisWindow",
			ID.BlackmanHarrisWindow);

	/***/
	public final static IBuiltInSymbol BlackmanNuttallWindow = initFinalSymbol("BlackmanNuttallWindow",
			ID.BlackmanNuttallWindow);

	/***/
	public final static IBuiltInSymbol BlackmanWindow = initFinalSymbol("BlackmanWindow", ID.BlackmanWindow);

	/***/
	public final static IBuiltInSymbol Blank = initFinalSymbol("Blank", ID.Blank);

	/***/
	public final static IBuiltInSymbol BlankNullSequence = initFinalSymbol("BlankNullSequence", ID.BlankNullSequence);

	/***/
	public final static IBuiltInSymbol BlankSequence = initFinalSymbol("BlankSequence", ID.BlankSequence);

	/** Block({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables` */
	public final static IBuiltInSymbol Block = initFinalSymbol("Block", ID.Block);

	/**
	 * Boole(expr) - returns `1` if `expr` evaluates to `True`; returns `0` if `expr` evaluates to `False`; and gives no
	 * result otherwise.
	 */
	public final static IBuiltInSymbol Boole = initFinalSymbol("Boole", ID.Boole);

	/**
	 * BooleanConvert(logical-expr) - convert the `logical-expr` to [disjunctive normal
	 * form](https://en.wikipedia.org/wiki/Disjunctive_normal_form)
	 */
	public final static IBuiltInSymbol BooleanConvert = initFinalSymbol("BooleanConvert", ID.BooleanConvert);

	/**
	 * BooleanMinimize(expr) - minimizes a boolean function with the [Quine McCluskey
	 * algorithm](https://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm)
	 */
	public final static IBuiltInSymbol BooleanMinimize = initFinalSymbol("BooleanMinimize", ID.BooleanMinimize);

	/** BooleanQ(expr) - returns `True` if `expr` is either `True` or `False`. */
	public final static IBuiltInSymbol BooleanQ = initFinalSymbol("BooleanQ", ID.BooleanQ);

	/**
	 * BooleanTable(logical-expr, variables) - generate [truth values](https://en.wikipedia.org/wiki/Truth_table) from
	 * the `logical-expr`
	 */
	public final static IBuiltInSymbol BooleanTable = initFinalSymbol("BooleanTable", ID.BooleanTable);

	/** BooleanVariables(logical-expr) - gives a list of the boolean variables that appear in the `logical-expr`. */
	public final static IBuiltInSymbol BooleanVariables = initFinalSymbol("BooleanVariables", ID.BooleanVariables);

	/** Booleans - is the set of boolean values. */
	public final static IBuiltInSymbol Booleans = initFinalSymbol("Booleans", ID.Booleans);

	/***/
	public final static IBuiltInSymbol Bottom = initFinalSymbol("Bottom", ID.Bottom);

	/***/
	public final static IBuiltInSymbol BoxWhiskerChart = initFinalSymbol("BoxWhiskerChart", ID.BoxWhiskerChart);

	/** BrayCurtisDistance(u, v) - returns the Bray Curtis distance between `u` and `v`. */
	public final static IBuiltInSymbol BrayCurtisDistance = initFinalSymbol("BrayCurtisDistance",
			ID.BrayCurtisDistance);

	/** Break() - exits a `For`, `While`, or `Do` loop. */
	public final static IBuiltInSymbol Break = initFinalSymbol("Break", ID.Break);

	/***/
	public final static IBuiltInSymbol Button = initFinalSymbol("Button", ID.Button);

	/** ByteArray({list-of-byte-values}) - converts the `list-of-byte-values` into a byte array. */
	public final static IBuiltInSymbol ByteArray = initFinalSymbol("ByteArray", ID.ByteArray);

	/***/
	public final static IBuiltInSymbol ByteArrayQ = initFinalSymbol("ByteArrayQ", ID.ByteArrayQ);

	/***/
	public final static IBuiltInSymbol ByteCount = initFinalSymbol("ByteCount", ID.ByteCount);

	/** C(n) - represents the `n`-th constant in a solution to a differential equation. */
	public final static IBuiltInSymbol C = initFinalSymbol("C", ID.C);

	/** CDF(distribution, value) - returns the cumulative distribution function of `value`. */
	public final static IBuiltInSymbol CDF = initFinalSymbol("CDF", ID.CDF);

	/***/
	public final static IBuiltInSymbol CForm = initFinalSymbol("CForm", ID.CForm);

	/**
	 * CanberraDistance(u, v) - returns the canberra distance between `u` and `v`, which is a weighted version of the
	 * Manhattan distance.
	 */
	public final static IBuiltInSymbol CanberraDistance = initFinalSymbol("CanberraDistance", ID.CanberraDistance);

	/** Cancel(expr) - cancels out common factors in numerators and denominators. */
	public final static IBuiltInSymbol Cancel = initFinalSymbol("Cancel", ID.Cancel);

	/***/
	public final static IBuiltInSymbol CancelButton = initFinalSymbol("CancelButton", ID.CancelButton);

	/** CarmichaelLambda(n) - the Carmichael function of `n` */
	public final static IBuiltInSymbol CarmichaelLambda = initFinalSymbol("CarmichaelLambda", ID.CarmichaelLambda);

	/** CartesianProduct(list1, list2) - returns the cartesian product for multiple lists. */
	public final static IBuiltInSymbol CartesianProduct = initFinalSymbol("CartesianProduct", ID.CartesianProduct);

	/** Cases(list, pattern) - returns the elements of `list` that match `pattern`. */
	public final static IBuiltInSymbol Cases = initFinalSymbol("Cases", ID.Cases);

	/** Catalan - Catalan's constant */
	public final static IBuiltInSymbol Catalan = initFinalSymbol("Catalan", ID.Catalan);

	/** CatalanNumber(n) - returns the catalan number for the integer argument `n`. */
	public final static IBuiltInSymbol CatalanNumber = initFinalSymbol("CatalanNumber", ID.CatalanNumber);

	/***/
	public final static IBuiltInSymbol Catch = initFinalSymbol("Catch", ID.Catch);

	/** Catenate({l1, l2, ...}) - concatenates the lists `l1, l2, ...` */
	public final static IBuiltInSymbol Catenate = initFinalSymbol("Catenate", ID.Catenate);

	/** Ceiling(expr) - gives the first integer greater than or equal `expr`. */
	public final static IBuiltInSymbol Ceiling = initFinalSymbol("Ceiling", ID.Ceiling);

	/***/
	public final static IBuiltInSymbol CenterDot = initFinalSymbol("CenterDot", ID.CenterDot);

	/** CentralMoment(list, r) - gives the the `r`th central moment (i.e. the `r`th moment about the mean) of `list`. */
	public final static IBuiltInSymbol CentralMoment = initFinalSymbol("CentralMoment", ID.CentralMoment);

	/***/
	public final static IBuiltInSymbol CharacterEncoding = initFinalSymbol("CharacterEncoding", ID.CharacterEncoding);

	/**
	 * CharacteristicPolynomial(matrix, var) - computes the characteristic polynomial of a `matrix` for the variable
	 * `var`.
	 */
	public final static IBuiltInSymbol CharacteristicPolynomial = initFinalSymbol("CharacteristicPolynomial",
			ID.CharacteristicPolynomial);

	/** ChebyshevT(n, x) - returns the Chebyshev polynomial of the first kind `T_n(x)`. */
	public final static IBuiltInSymbol ChebyshevT = initFinalSymbol("ChebyshevT", ID.ChebyshevT);

	/** ChebyshevU(n, x) - returns the Chebyshev polynomial of the second kind `U_n(x)`. */
	public final static IBuiltInSymbol ChebyshevU = initFinalSymbol("ChebyshevU", ID.ChebyshevU);

	/**
	 * Check(expr, failure) - evaluates `expr`, and returns the result, unless messages were generated, in which case
	 * `failure` will be returned.
	 */
	public final static IBuiltInSymbol Check = initFinalSymbol("Check", ID.Check);

	/**
	 * ChessboardDistance(u, v) - returns the chessboard distance (also known as Chebyshev distance) between `u` and
	 * `v`, which is the number of moves a king on a chessboard needs to get from square `u` to square `v`.
	 */
	public final static IBuiltInSymbol ChessboardDistance = initFinalSymbol("ChessboardDistance",
			ID.ChessboardDistance);

	/***/
	public final static IBuiltInSymbol ChiSquareDistribution = initFinalSymbol("ChiSquareDistribution",
			ID.ChiSquareDistribution);

	/** ChineseRemainder({a1, a2, a3,...}, {n1, n2, n3,...}) - the chinese remainder function. */
	public final static IBuiltInSymbol ChineseRemainder = initFinalSymbol("ChineseRemainder", ID.ChineseRemainder);

	/**
	 * CholeskyDecomposition(matrix) - calculate the Cholesky decomposition of a hermitian, positive definite square
	 * `matrix`.
	 */
	public final static IBuiltInSymbol CholeskyDecomposition = initFinalSymbol("CholeskyDecomposition",
			ID.CholeskyDecomposition);

	/**
	 * Chop(numerical-expr) - replaces numerical values in the `numerical-expr` which are close to zero with symbolic
	 * value `0`.
	 */
	public final static IBuiltInSymbol Chop = initFinalSymbol("Chop", ID.Chop);

	/***/
	public final static IBuiltInSymbol CircleDot = initFinalSymbol("CircleDot", ID.CircleDot);

	/** CirclePoints(i) - gives the `i` points on the unit circle for a positive integer `i`. */
	public final static IBuiltInSymbol CirclePoints = initFinalSymbol("CirclePoints", ID.CirclePoints);

	/** Clear(symbol1, symbol2,...) - clears all values of the given symbols. */
	public final static IBuiltInSymbol Clear = initFinalSymbol("Clear", ID.Clear);

	/** ClearAll(symbol1, symbol2,...) - clears all values and attributes associated with the given symbols. */
	public final static IBuiltInSymbol ClearAll = initFinalSymbol("ClearAll", ID.ClearAll);

	/** ClearAttributes(symbol, attrib) - removes `attrib` from `symbol`'s attributes. */
	public final static IBuiltInSymbol ClearAttributes = initFinalSymbol("ClearAttributes", ID.ClearAttributes);

	/**
	 * Clip(expr) - returns `expr` in the range `-1` to `1`. Returns `-1` if `expr` is less than `-1`. Returns `1` if
	 * `expr` is greater than `1`.
	 */
	public final static IBuiltInSymbol Clip = initFinalSymbol("Clip", ID.Clip);

	/** Coefficient(polynomial, variable, exponent) - get the coefficient of `variable^exponent` in `polynomial`. */
	public final static IBuiltInSymbol Coefficient = initFinalSymbol("Coefficient", ID.Coefficient);

	/** CoefficientList(polynomial, variable) - get the coefficient list of a `polynomial`. */
	public final static IBuiltInSymbol CoefficientList = initFinalSymbol("CoefficientList", ID.CoefficientList);

	/** CoefficientRules(polynomial, list-of-variables) - get the list of coefficient rules of a `polynomial`. */
	public final static IBuiltInSymbol CoefficientRules = initFinalSymbol("CoefficientRules", ID.CoefficientRules);

	/** Collect(expr, variable) - collect subexpressions in `expr` which belong to the same `variable`. */
	public final static IBuiltInSymbol Collect = initFinalSymbol("Collect", ID.Collect);

	/***/
	public final static IBuiltInSymbol Colon = initFinalSymbol("Colon", ID.Colon);

	/***/
	public final static IBuiltInSymbol ColorData = initFinalSymbol("ColorData", ID.ColorData);

	/***/
	public final static IBuiltInSymbol ColorFunction = initFinalSymbol("ColorFunction", ID.ColorFunction);

	/***/
	public final static IBuiltInSymbol Column = initFinalSymbol("Column", ID.Column);

	/***/
	public final static IBuiltInSymbol Commonest = initFinalSymbol("Commonest", ID.Commonest);

	/***/
	public final static IBuiltInSymbol CompatibleUnitQ = initFinalSymbol("CompatibleUnitQ", ID.CompatibleUnitQ);

	/***/
	public final static IBuiltInSymbol Compile = initFinalSymbol("Compile", ID.Compile);

	/***/
	public final static IBuiltInSymbol CompiledFunction = initFinalSymbol("CompiledFunction", ID.CompiledFunction);

	/** Complement (set1, set2) - get the complement set from `set1` and `set2`. */
	public final static IBuiltInSymbol Complement = initFinalSymbol("Complement", ID.Complement);

	/** Complex - is the head of complex numbers. */
	public final static IBuiltInSymbol Complex = initFinalSymbol("Complex", ID.Complex);

	/**
	 * ComplexExpand(expr) - get the expanded `expr`. All variable symbols in `expr` are assumed to be non complex
	 * numbers.
	 */
	public final static IBuiltInSymbol ComplexExpand = initFinalSymbol("ComplexExpand", ID.ComplexExpand);

	/** ComplexInfinity - represents an infinite complex quantity of undetermined direction. */
	public final static IBuiltInSymbol ComplexInfinity = initFinalSymbol("ComplexInfinity", ID.ComplexInfinity);

	/** Complexes - is the set of complex numbers. */
	public final static IBuiltInSymbol Complexes = initFinalSymbol("Complexes", ID.Complexes);

	/***/
	public final static IBuiltInSymbol ComplexityFunction = initFinalSymbol("ComplexityFunction",
			ID.ComplexityFunction);

	/***/
	public final static IBuiltInSymbol ComplexPlot3D = initFinalSymbol("ComplexPlot3D", ID.ComplexPlot3D);

	/**
	 * ComposeList(list-of-symbols, variable) - creates a list of compositions of the symbols applied at the argument
	 * `x`.
	 */
	public final static IBuiltInSymbol ComposeList = initFinalSymbol("ComposeList", ID.ComposeList);

	/** ComposeSeries( series1, series2 ) - substitute `series2` into `series1` */
	public final static IBuiltInSymbol ComposeSeries = initFinalSymbol("ComposeSeries", ID.ComposeSeries);

	/** Composition(sym1, sym2,...)[arg1, arg2,...] - creates a composition of the symbols applied at the arguments. */
	public final static IBuiltInSymbol Composition = initFinalSymbol("Composition", ID.Composition);

	/** CompoundExpression(expr1, expr2, ...) - evaluates its arguments in turn, returning the last result. */
	public final static IBuiltInSymbol CompoundExpression = initFinalSymbol("CompoundExpression",
			ID.CompoundExpression);

	/**
	 * Condition(pattern, expr) - places an additional constraint on `pattern` that only allows it to match if `expr`
	 * evaluates to `True`.
	 */
	public final static IBuiltInSymbol Condition = initFinalSymbol("Condition", ID.Condition);

	/***/
	public final static IBuiltInSymbol ConditionalExpression = initFinalSymbol("ConditionalExpression",
			ID.ConditionalExpression);

	/** Conjugate(z) - returns the complex conjugate of the complex number `z`. */
	public final static IBuiltInSymbol Conjugate = initFinalSymbol("Conjugate", ID.Conjugate);

	/** ConjugateTranspose(matrix) - get the transposed `matrix` with conjugated matrix elements. */
	public final static IBuiltInSymbol ConjugateTranspose = initFinalSymbol("ConjugateTranspose",
			ID.ConjugateTranspose);

	/***/
	public final static IBuiltInSymbol ConnectedGraphQ = initFinalSymbol("ConnectedGraphQ", ID.ConnectedGraphQ);

	/** Constant - is an attribute that indicates that a symbol is a constant. */
	public final static IBuiltInSymbol Constant = initFinalSymbol("Constant", ID.Constant);

	/** ConstantArray(expr, n) - returns a list of `n` copies of `expr`. */
	public final static IBuiltInSymbol ConstantArray = initFinalSymbol("ConstantArray", ID.ConstantArray);

	/***/
	public final static IBuiltInSymbol ContainsAll = initFinalSymbol("ContainsAll", ID.ContainsAll);

	/***/
	public final static IBuiltInSymbol ContainsAny = initFinalSymbol("ContainsAny", ID.ContainsAny);

	/***/
	public final static IBuiltInSymbol ContainsExactly = initFinalSymbol("ContainsExactly", ID.ContainsExactly);

	/***/
	public final static IBuiltInSymbol ContainsNone = initFinalSymbol("ContainsNone", ID.ContainsNone);

	/** ContainsOnly(list1, list2) - yields True if `list1` contains only elements that appear in `list2`. */
	public final static IBuiltInSymbol ContainsOnly = initFinalSymbol("ContainsOnly", ID.ContainsOnly);

	/** Context(symbol) - return the context of the given symbol. */
	public final static IBuiltInSymbol Context = initFinalSymbol("Context", ID.Context);

	/** Continue() - continues with the next iteration in a `For`, `While`, or `Do` loop. */
	public final static IBuiltInSymbol Continue = initFinalSymbol("Continue", ID.Continue);

	/** ContinuedFraction(number) - get the continued fraction representation of `number`. */
	public final static IBuiltInSymbol ContinuedFraction = initFinalSymbol("ContinuedFraction", ID.ContinuedFraction);

	/***/
	public final static IBuiltInSymbol ContourPlot = initFinalSymbol("ContourPlot", ID.ContourPlot);
	/**
	 * Convergents({n1, n2, ...}) - return the list of convergents which represents the continued fraction list `{n1,
	 * n2, ...}`.
	 */
	public final static IBuiltInSymbol Convergents = initFinalSymbol("Convergents", ID.Convergents);

	/***/
	public final static IBuiltInSymbol ConvexHullMesh = initFinalSymbol("ConvexHullMesh", ID.ConvexHullMesh);

	/** CoprimeQ(x, y) - tests whether `x` and `y` are coprime by computing their greatest common divisor. */
	public final static IBuiltInSymbol CoprimeQ = initFinalSymbol("CoprimeQ", ID.CoprimeQ);

	/** Correlation(a, b) - computes Pearson's correlation of two equal-sized vectors `a` and `b`. */
	public final static IBuiltInSymbol Correlation = initFinalSymbol("Correlation", ID.Correlation);

	/**
	 * Cos(expr) - returns the cosine of `expr` (measured in radians). `Cos(expr)` will evaluate automatically in the
	 * case `expr` is a multiple of `Pi, Pi/2, Pi/3, Pi/4` and `Pi/6`.
	 */
	public final static IBuiltInSymbol Cos = initFinalSymbol("Cos", ID.Cos);

	/***/
	public final static IBuiltInSymbol CosIntegral = initFinalSymbol("CosIntegral", ID.CosIntegral);

	/** Cosh(z) - returns the hyperbolic cosine of `z`. */
	public final static IBuiltInSymbol Cosh = initFinalSymbol("Cosh", ID.Cosh);

	/***/
	public final static IBuiltInSymbol CoshIntegral = initFinalSymbol("CoshIntegral", ID.CoshIntegral);

	/** CosineDistance(u, v) - returns the cosine distance between `u` and `v`. */
	public final static IBuiltInSymbol CosineDistance = initFinalSymbol("CosineDistance", ID.CosineDistance);

	/** Cot(expr) - the cotangent function. */
	public final static IBuiltInSymbol Cot = initFinalSymbol("Cot", ID.Cot);

	/** Coth(z) - returns the hyperbolic cotangent of `z`. */
	public final static IBuiltInSymbol Coth = initFinalSymbol("Coth", ID.Coth);

	/** Count(list, pattern) - returns the number of times `pattern` appears in `list`. */
	public final static IBuiltInSymbol Count = initFinalSymbol("Count", ID.Count);

	/***/
	public final static IBuiltInSymbol CountDistinct = initFinalSymbol("CountDistinct", ID.CountDistinct);

	/**
	 * Counts({elem1, elem2, elem3, ...}) - count the number of each distinct element in the list `{elem1, elem2, elem3,
	 * ...}` and return the result as an association `<|elem1->counter1, ...|>`.
	 */
	public final static IBuiltInSymbol Counts = initFinalSymbol("Counts", ID.Counts);

	/** Covariance(a, b) - computes the covariance between the equal-sized vectors `a` and `b`. */
	public final static IBuiltInSymbol Covariance = initFinalSymbol("Covariance", ID.Covariance);

	/***/
	public final static IBuiltInSymbol CreateDirectory = initFinalSymbol("CreateDirectory", ID.CreateDirectory);

	/** Cross(a, b) - computes the vector cross product of `a` and `b`. */
	public final static IBuiltInSymbol Cross = initFinalSymbol("Cross", ID.Cross);

	/** Csc(z) - returns the cosecant of `z`. */
	public final static IBuiltInSymbol Csc = initFinalSymbol("Csc", ID.Csc);

	/** Csch(z) - returns the hyperbolic cosecant of `z`. */
	public final static IBuiltInSymbol Csch = initFinalSymbol("Csch", ID.Csch);

	/** CubeRoot(n) - finds the real-valued cube root of the given `n`. */
	public final static IBuiltInSymbol CubeRoot = initFinalSymbol("CubeRoot", ID.CubeRoot);

	/** Curl({f1, f2}, {x1, x2}) - gives the curl. */
	public final static IBuiltInSymbol Curl = initFinalSymbol("Curl", ID.Curl);

	/** Cyclotomic(n, x) - returns the Cyclotomic polynomial `C_n(x)`. */
	public final static IBuiltInSymbol Cyclotomic = initFinalSymbol("Cyclotomic", ID.Cyclotomic);

	/** D(f, x) - gives the partial derivative of `f` with respect to `x`. */
	public final static IBuiltInSymbol D = initFinalSymbol("D", ID.D);

	/**
	 * DSolve(equation, f(var), var) - attempts to solve a linear differential `equation` for the function `f(var)` and
	 * variable `var`.
	 */
	public final static IBuiltInSymbol DSolve = initFinalSymbol("DSolve", ID.DSolve);

	/***/
	public final static IBuiltInSymbol Dataset = initFinalSymbol("Dataset", ID.Dataset);

	/***/
	public final static IBuiltInSymbol DateObject = initFinalSymbol("DateObject", ID.DateObject);

	/***/
	public final static IBuiltInSymbol DateValue = initFinalSymbol("DateValue", ID.DateValue);

	/** Decrement(x) - decrements `x` by `1`, returning the original value of `x`. */
	public final static IBuiltInSymbol Decrement = initFinalSymbol("Decrement", ID.Decrement);

	/**
	 * Default(symbol) - `Default` returns the default value associated with the `symbol` for a pattern default `_.`
	 * expression.
	 */
	public final static IBuiltInSymbol Default = initFinalSymbol("Default", ID.Default);

	/***/
	public final static IBuiltInSymbol DefaultButton = initFinalSymbol("DefaultButton", ID.DefaultButton);

	/** Defer(expr) - `Defer` doesn't evaluate `expr` and didn't appear in the output */
	public final static IBuiltInSymbol Defer = initFinalSymbol("Defer", ID.Defer);

	/** Definition(symbol) - prints user-defined values and rules associated with `symbol`. */
	public final static IBuiltInSymbol Definition = initFinalSymbol("Definition", ID.Definition);

	/** Degree - the constant `Degree` converts angles from degree to `Pi/180` radians. */
	public final static IBuiltInSymbol Degree = initFinalSymbol("Degree", ID.Degree);

	/***/
	public final static IBuiltInSymbol DegreeLexicographic = initFinalSymbol("DegreeLexicographic",
			ID.DegreeLexicographic);

	/***/
	public final static IBuiltInSymbol DegreeReverseLexicographic = initFinalSymbol("DegreeReverseLexicographic",
			ID.DegreeReverseLexicographic);

	/** Delete(expr, n) - returns `expr` with part `n` removed. */
	public final static IBuiltInSymbol Delete = initFinalSymbol("Delete", ID.Delete);

	/** DeleteCases(list, pattern) - returns the elements of `list` that do not match `pattern`. */
	public final static IBuiltInSymbol DeleteCases = initFinalSymbol("DeleteCases", ID.DeleteCases);

	/** DeleteDuplicates(list) - deletes duplicates from `list`. */
	public final static IBuiltInSymbol DeleteDuplicates = initFinalSymbol("DeleteDuplicates", ID.DeleteDuplicates);

	/**
	 * Denominator(expr) - gives the denominator in `expr`. Denominator collects expressions with negative exponents.
	 */
	public final static IBuiltInSymbol Denominator = initFinalSymbol("Denominator", ID.Denominator);

	/***/
	public final static IBuiltInSymbol DensityHistogram = initFinalSymbol("DensityHistogram", ID.DensityHistogram);

	/***/
	public final static IBuiltInSymbol DensityPlot = initFinalSymbol("DensityPlot", ID.DensityPlot);

	/** Depth(expr) - gives the depth of `expr`. */
	public final static IBuiltInSymbol Depth = initFinalSymbol("Depth", ID.Depth);

	/** Derivative(n)[f] - represents the `n`-th derivative of the function `f`. */
	public final static IBuiltInSymbol Derivative = initFinalSymbol("Derivative", ID.Derivative);

	/** DesignMatrix(m, f, x) - returns the design matrix. */
	public final static IBuiltInSymbol DesignMatrix = initFinalSymbol("DesignMatrix", ID.DesignMatrix);

	/** Det(matrix) - computes the determinant of the `matrix`. */
	public final static IBuiltInSymbol Det = initFinalSymbol("Det", ID.Det);

	/** Diagonal(matrix) - computes the diagonal vector of the `matrix`. */
	public final static IBuiltInSymbol Diagonal = initFinalSymbol("Diagonal", ID.Diagonal);

	/** DiagonalMatrix(list) - gives a matrix with the values in `list` on its diagonal and zeroes elsewhere. */
	public final static IBuiltInSymbol DiagonalMatrix = initFinalSymbol("DiagonalMatrix", ID.DiagonalMatrix);

	/** DialogInput() - if the file system is enabled, the user can input a string in a dialog box. */
	public final static IBuiltInSymbol DialogInput = initFinalSymbol("DialogInput", ID.DialogInput);

	/***/
	public final static IBuiltInSymbol DialogNotebook = initFinalSymbol("DialogNotebook", ID.DialogNotebook);

	/***/
	public final static IBuiltInSymbol DialogReturn = initFinalSymbol("DialogReturn", ID.DialogReturn);

	/**
	 * DiceDissimilarity(u, v) - returns the Dice dissimilarity between the two boolean 1-D lists `u` and `v`, which is
	 * defined as `(c_tf + c_ft) / (2 * c_tt + c_ft + c_tf)`, where n is `len(u)` and `c_ij` is the number of
	 * occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
	 */
	public final static IBuiltInSymbol DiceDissimilarity = initFinalSymbol("DiceDissimilarity", ID.DiceDissimilarity);

	/***/
	public final static IBuiltInSymbol Differences = initFinalSymbol("Differences", ID.Differences);

	/** DigitCount(n) - returns a list of the number of integer digits for `n` for `radix` 10. */
	public final static IBuiltInSymbol DigitCount = initFinalSymbol("DigitCount", ID.DigitCount);

	/** DigitQ(str) - returns `True` if `str` is a string which contains only digits. */
	public final static IBuiltInSymbol DigitQ = initFinalSymbol("DigitQ", ID.DigitQ);

	/** Dimensions(expr) - returns a list of the dimensions of the expression `expr`. */
	public final static IBuiltInSymbol Dimensions = initFinalSymbol("Dimensions", ID.Dimensions);

	/** DiracDelta(x) - `DiracDelta` function returns `0` for all real numbers `x` where `x != 0`. */
	public final static IBuiltInSymbol DiracDelta = initFinalSymbol("DiracDelta", ID.DiracDelta);

	/***/
	public final static IBuiltInSymbol DirectedEdge = initFinalSymbol("DirectedEdge", ID.DirectedEdge);

	/** DirectedInfinity(z) - represents an infinite multiple of the complex number `z`. */
	public final static IBuiltInSymbol DirectedInfinity = initFinalSymbol("DirectedInfinity", ID.DirectedInfinity);

	/***/
	public final static IBuiltInSymbol Direction = initFinalSymbol("Direction", ID.Direction);

	/***/
	public final static IBuiltInSymbol DirichletEta = initFinalSymbol("DirichletEta", ID.DirichletEta);

	/***/
	public final static IBuiltInSymbol DirichletWindow = initFinalSymbol("DirichletWindow", ID.DirichletWindow);

	/**
	 * DiscreteDelta(n1, n2, n3, ...) - `DiscreteDelta` function returns `1` if all the `ni` are `0`. Returns `0`
	 * otherwise.
	 */
	public final static IBuiltInSymbol DiscreteDelta = initFinalSymbol("DiscreteDelta", ID.DiscreteDelta);

	/** DiscreteUniformDistribution({min, max}) - returns a discrete uniform distribution. */
	public final static IBuiltInSymbol DiscreteUniformDistribution = initFinalSymbol("DiscreteUniformDistribution",
			ID.DiscreteUniformDistribution);

	/**
	 * Discriminant(poly, var) - computes the discriminant of the polynomial `poly` with respect to the variable `var`.
	 */
	public final static IBuiltInSymbol Discriminant = initFinalSymbol("Discriminant", ID.Discriminant);

	/***/
	public final static IBuiltInSymbol DisjointQ = initFinalSymbol("DisjointQ", ID.DisjointQ);

	/***/
	public final static IBuiltInSymbol Disputed = initFinalSymbol("Disputed", ID.Disputed);

	/** Distribute(f(x1, x2, x3,...)) - distributes `f` over `Plus` appearing in any of the `xi`. */
	public final static IBuiltInSymbol Distribute = initFinalSymbol("Distribute", ID.Distribute);

	/***/
	public final static IBuiltInSymbol Distributed = initFinalSymbol("Distributed", ID.Distributed);

	/** Div({f1, f2, f3,...},{x1, x2, x3,...}) - compute the divergence. */
	public final static IBuiltInSymbol Div = initFinalSymbol("Div", ID.Div);

	/** Divide(a, b) - represents the division of `a` by `b`. */
	public final static IBuiltInSymbol Divide = initFinalSymbol("Divide", ID.Divide);

	/** DivideBy(x, dx) - is equivalent to `x = x / dx`. */
	public final static IBuiltInSymbol DivideBy = initFinalSymbol("DivideBy", ID.DivideBy);

	/** Divisible(n, m) - returns `True` if `n` could be divide by `m`. */
	public final static IBuiltInSymbol Divisible = initFinalSymbol("Divisible", ID.Divisible);

	/** DivisorSigma(k, n) - returns the sum of the `k`-th powers of the divisors of `n`. */
	public final static IBuiltInSymbol DivisorSigma = initFinalSymbol("DivisorSigma", ID.DivisorSigma);

	/** Divisors(n) - returns all integers that divide the integer `n`. */
	public final static IBuiltInSymbol Divisors = initFinalSymbol("Divisors", ID.Divisors);

	/** DivisorSum */
	public final static IBuiltInSymbol DivisorSum = initFinalSymbol("DivisorSum", ID.DivisorSum);

	/** Do(expr, {max}) - evaluates `expr` `max` times. */
	public final static IBuiltInSymbol Do = initFinalSymbol("Do", ID.Do);

	/** Dot(x, y) or x . y - `x . y` computes the vector dot product or matrix product `x . y`. */
	public final static IBuiltInSymbol Dot = initFinalSymbol("Dot", ID.Dot);

	/** Drop(expr, n) - returns `expr` with the first `n` leaves removed. */
	public final static IBuiltInSymbol Drop = initFinalSymbol("Drop", ID.Drop);

	/***/
	public final static IBuiltInSymbol Dynamic = initFinalSymbol("Dynamic", ID.Dynamic);

	/** Euler's constant E */
	public final static IBuiltInSymbol E = initFinalSymbol("E", ID.E);

	/***/
	public final static IBuiltInSymbol EasterSunday = initFinalSymbol("EasterSunday", ID.EasterSunday);

	/***/
	public final static IBuiltInSymbol EdgeCount = initFinalSymbol("EdgeCount", ID.EdgeCount);

	/** EdgeList(graph) - convert the `graph` into a list of edges. */
	public final static IBuiltInSymbol EdgeList = initFinalSymbol("EdgeList", ID.EdgeList);

	/** EdgeQ(graph, edge) - test if `edge` is an edge in the `graph` object. */
	public final static IBuiltInSymbol EdgeQ = initFinalSymbol("EdgeQ", ID.EdgeQ);

	/***/
	public final static IBuiltInSymbol EdgeWeight = initFinalSymbol("EdgeWeight", ID.EdgeWeight);

	/** EffectiveInterest(i, n) - returns an effective interest rate object. */
	public final static IBuiltInSymbol EffectiveInterest = initFinalSymbol("EffectiveInterest", ID.EffectiveInterest);

	/** Eigenvalues(matrix) - get the numerical eigenvalues of the `matrix`. */
	public final static IBuiltInSymbol Eigenvalues = initFinalSymbol("Eigenvalues", ID.Eigenvalues);

	/** Eigenvectors(matrix) - get the numerical eigenvectors of the `matrix`. */
	public final static IBuiltInSymbol Eigenvectors = initFinalSymbol("Eigenvectors", ID.Eigenvectors);

	/** Element(symbol, dom) - assume (or test) that the `symbol` is in the domain `dom`. */
	public final static IBuiltInSymbol Element = initFinalSymbol("Element", ID.Element);

	/** ElementData("name", "property") - gives the value of the property for the chemical specified by name. */
	public final static IBuiltInSymbol ElementData = initFinalSymbol("ElementData", ID.ElementData);

	/**
	 * Eliminate(list-of-equations, list-of-variables) - attempts to eliminate the variables from the
	 * `list-of-variables` in the `list-of-equations`.
	 */
	public final static IBuiltInSymbol Eliminate = initFinalSymbol("Eliminate", ID.Eliminate);

	/***/
	public final static IBuiltInSymbol EliminationOrder = initFinalSymbol("EliminationOrder", ID.EliminationOrder);

	/** EllipticE(z) - returns the complete elliptic integral of the second kind. */
	public final static IBuiltInSymbol EllipticE = initFinalSymbol("EllipticE", ID.EllipticE);

	/** EllipticF(z) - returns the incomplete elliptic integral of the first kind. */
	public final static IBuiltInSymbol EllipticF = initFinalSymbol("EllipticF", ID.EllipticF);

	/** EllipticK(z) - returns the complete elliptic integral of the first kind. */
	public final static IBuiltInSymbol EllipticK = initFinalSymbol("EllipticK", ID.EllipticK);

	/** EllipticPi(n,m) - returns the complete elliptic integral of the third kind. */
	public final static IBuiltInSymbol EllipticPi = initFinalSymbol("EllipticPi", ID.EllipticPi);

	/***/
	public final static IBuiltInSymbol EllipticTheta = initFinalSymbol("EllipticTheta", ID.EllipticTheta);

	/** End( ) - end a context definition started with `Begin` */
	public final static IBuiltInSymbol End = initFinalSymbol("End", ID.End);

	/** EndPackage( ) - end a package definition */
	public final static IBuiltInSymbol EndPackage = initFinalSymbol("EndPackage", ID.EndPackage);

	/***/
	public final static IBuiltInSymbol Entity = initFinalSymbol("Entity", ID.Entity);

	/**
	 * Equal(x, y) - yields `True` if `x` and `y` are known to be equal, or `False` if `x` and `y` are known to be
	 * unequal.
	 */
	public final static IBuiltInSymbol Equal = initFinalSymbol("Equal", ID.Equal);

	/**
	 * Equivalent(arg1, arg2, ...) - Equivalence relation. `Equivalent(A, B)` is `True` iff `A` and `B` are both `True`
	 * or both `False`. Returns `True` if all of the arguments are logically equivalent. Returns `False` otherwise.
	 * `Equivalent(arg1, arg2, ...)` is equivalent to `(arg1 && arg2 && ...) || (!arg1 && !arg2 && ...)`.
	 */
	public final static IBuiltInSymbol Equivalent = initFinalSymbol("Equivalent", ID.Equivalent);

	/** Erf(z) - returns the error function of `z`. */
	public final static IBuiltInSymbol Erf = initFinalSymbol("Erf", ID.Erf);

	/** Erfc(z) - returns the complementary error function of `z`. */
	public final static IBuiltInSymbol Erfc = initFinalSymbol("Erfc", ID.Erfc);

	/***/
	public final static IBuiltInSymbol Erfi = initFinalSymbol("Erfi", ID.Erfi);

	/** ErlangDistribution({k, lambda}) - returns a Erlang distribution. */
	public final static IBuiltInSymbol ErlangDistribution = initFinalSymbol("ErlangDistribution",
			ID.ErlangDistribution);

	/** EuclideanDistance(u, v) - returns the euclidean distance between `u` and `v`. */
	public final static IBuiltInSymbol EuclideanDistance = initFinalSymbol("EuclideanDistance", ID.EuclideanDistance);

	/** EulerE(n) - gives the euler number `En`. */
	public final static IBuiltInSymbol EulerE = initFinalSymbol("EulerE", ID.EulerE);

	/***/
	public final static IBuiltInSymbol EulerGamma = initFinalSymbol("EulerGamma", ID.EulerGamma);

	/** EulerPhi(n) - compute Euler's totient function. */
	public final static IBuiltInSymbol EulerPhi = initFinalSymbol("EulerPhi", ID.EulerPhi);

	/** EulerianGraphQ(graph) - returns `True` if `graph` is an eulerian graph, and `False` otherwise. */
	public final static IBuiltInSymbol EulerianGraphQ = initFinalSymbol("EulerianGraphQ", ID.EulerianGraphQ);

	/***/
	public final static IBuiltInSymbol Evaluate = initFinalSymbol("Evaluate", ID.Evaluate);

	/** EvenQ(x) - returns `True` if `x` is even, and `False` otherwise. */
	public final static IBuiltInSymbol EvenQ = initFinalSymbol("EvenQ", ID.EvenQ);

	/** ExactNumberQ(expr) - returns `True` if `expr` is an exact number, and `False` otherwise. */
	public final static IBuiltInSymbol ExactNumberQ = initFinalSymbol("ExactNumberQ", ID.ExactNumberQ);

	/** Except(c) - represents a pattern object that matches any expression except those matching `c`. */
	public final static IBuiltInSymbol Except = initFinalSymbol("Except", ID.Except);

	/***/
	public final static IBuiltInSymbol Exists = initFinalSymbol("Exists", ID.Exists);

	/** Exp(z) - the exponential function `E^z`. */
	public final static IBuiltInSymbol Exp = initFinalSymbol("Exp", ID.Exp);

	/***/
	public final static IBuiltInSymbol ExpIntegralE = initFinalSymbol("ExpIntegralE", ID.ExpIntegralE);

	/***/
	public final static IBuiltInSymbol ExpIntegralEi = initFinalSymbol("ExpIntegralEi", ID.ExpIntegralEi);

	/***/
	public final static IBuiltInSymbol ExpToTrig = initFinalSymbol("ExpToTrig", ID.ExpToTrig);

	/** Expand(expr) - expands out positive rational powers and products of sums in `expr`. */
	public final static IBuiltInSymbol Expand = initFinalSymbol("Expand", ID.Expand);

	/** ExpandAll(expr) - expands out all positive integer powers and products of sums in `expr`. */
	public final static IBuiltInSymbol ExpandAll = initFinalSymbol("ExpandAll", ID.ExpandAll);

	/**
	 * Expectation(pure-function, data-set) - returns the expected value of the `pure-function` for the given
	 * `data-set`.
	 */
	public final static IBuiltInSymbol Expectation = initFinalSymbol("Expectation", ID.Expectation);

	/**
	 * Exponent(polynomial, x) - gives the maximum power with which `x` appears in the expanded form of `polynomial`.
	 */
	public final static IBuiltInSymbol Exponent = initFinalSymbol("Exponent", ID.Exponent);

	/** ExponentialDistribution(lambda) - returns an exponential distribution. */
	public final static IBuiltInSymbol ExponentialDistribution = initFinalSymbol("ExponentialDistribution",
			ID.ExponentialDistribution);

	/**
	 * Export("path-to-filename", expression, "WXF") - if the file system is enabled, export the `expression` in WXF
	 * format to the "path-to-filename" file.
	 */
	public final static IBuiltInSymbol Export = initFinalSymbol("Export", ID.Export);

	/***/
	public final static IBuiltInSymbol ExportString = initFinalSymbol("ExportString", ID.ExportString);

	/***/
	public final static IBuiltInSymbol Expression = initFinalSymbol("Expression", ID.Expression);

	/** ExtendedGCD(n1, n2, ...) - computes the extended greatest common divisor of the given integers. */
	public final static IBuiltInSymbol ExtendedGCD = initFinalSymbol("ExtendedGCD", ID.ExtendedGCD);

	/***/
	public final static IBuiltInSymbol Extension = initFinalSymbol("Extension", ID.Extension);

	/** Extract(expr, list) - extracts parts of `expr` specified by `list`. */
	public final static IBuiltInSymbol Extract = initFinalSymbol("Extract", ID.Extract);

	/***/
	public final static IBuiltInSymbol FRatioDistribution = initFinalSymbol("FRatioDistribution",
			ID.FRatioDistribution);

	/** Factor(expr) - factors the polynomial expression `expr` */
	public final static IBuiltInSymbol Factor = initFinalSymbol("Factor", ID.Factor);

	/** FactorInteger(n) - returns the factorization of `n` as a list of factors and exponents. */
	public final static IBuiltInSymbol FactorInteger = initFinalSymbol("FactorInteger", ID.FactorInteger);

	/** FactorSquareFree(polynomial) - factor the polynomial expression `polynomial` square free. */
	public final static IBuiltInSymbol FactorSquareFree = initFinalSymbol("FactorSquareFree", ID.FactorSquareFree);

	/** FactorSquareFreeList(polynomial) - get the square free factors of the polynomial expression `polynomial`. */
	public final static IBuiltInSymbol FactorSquareFreeList = initFinalSymbol("FactorSquareFreeList",
			ID.FactorSquareFreeList);

	/** FactorTerms(poly) - pulls out any overall numerical factor in `poly`. */
	public final static IBuiltInSymbol FactorTerms = initFinalSymbol("FactorTerms", ID.FactorTerms);

	/** Factorial(n) - returns the factorial number of the integer `n` */
	public final static IBuiltInSymbol Factorial = initFinalSymbol("Factorial", ID.Factorial);

	/***/
	public final static IBuiltInSymbol FactorialPower = initFinalSymbol("FactorialPower", ID.FactorialPower);

	/** Factorial2(n) - returns the double factorial number of the integer `n`. */
	public final static IBuiltInSymbol Factorial2 = initFinalSymbol("Factorial2", ID.Factorial2);

	/** False - the constant `False` represents the boolean value **false ***/
	public final static IBuiltInSymbol False = initFinalSymbol("False", ID.False);

	/** Fibonacci(n) - returns the Fibonacci number of the integer `n` */
	public final static IBuiltInSymbol Fibonacci = initFinalSymbol("Fibonacci", ID.Fibonacci);

	/***/
	public final static IBuiltInSymbol FindEdgeCover = initFinalSymbol("FindEdgeCover", ID.FindEdgeCover);

	/** FindEulerianCycle(graph) - find an eulerian cycle in the `graph`. */
	public final static IBuiltInSymbol FindEulerianCycle = initFinalSymbol("FindEulerianCycle", ID.FindEulerianCycle);

	/**
	 * FindFit(list-of-data-points, function, parameters, variable) - solve a least squares problem using the
	 * Levenberg-Marquardt algorithm.
	 */
	public final static IBuiltInSymbol FindFit = initFinalSymbol("FindFit", ID.FindFit);

	/***/
	public final static IBuiltInSymbol FindGraphCommunities = initFinalSymbol("FindGraphCommunities",
			ID.FindGraphCommunities);

	/** FindHamiltonianCycle(graph) - find an hamiltonian cycle in the `graph`. */
	public final static IBuiltInSymbol FindHamiltonianCycle = initFinalSymbol("FindHamiltonianCycle",
			ID.FindHamiltonianCycle);

	/***/
	public final static IBuiltInSymbol FindIndependentEdgeSet = initFinalSymbol("FindIndependentEdgeSet",
			ID.FindIndependentEdgeSet);

	/***/
	public final static IBuiltInSymbol FindIndependentVertexSet = initFinalSymbol("FindIndependentVertexSet",
			ID.FindIndependentVertexSet);

	/**
	 * FindInstance(equations, vars) - attempts to find one solution which solves the `equations` for the variables
	 * `vars`.
	 */
	public final static IBuiltInSymbol FindInstance = initFinalSymbol("FindInstance", ID.FindInstance);

	/**
	 * FindRoot(f, {x, xmin, xmax}) - searches for a numerical root of `f` for the variable `x`, in the range `xmin` to
	 * `xmax`.
	 */
	public final static IBuiltInSymbol FindRoot = initFinalSymbol("FindRoot", ID.FindRoot);

	/**
	 * FindShortestPath(graph, source, destination) - find a shortest path in the `graph` from `source` to
	 * `destination`.
	 */
	public final static IBuiltInSymbol FindShortestPath = initFinalSymbol("FindShortestPath", ID.FindShortestPath);

	/**
	 * FindShortestTour({{p11, p12}, {p21, p22}, {p31, p32}, ...}) - find a shortest tour in the `graph` with minimum
	 * `EuclideanDistance`.
	 */
	public final static IBuiltInSymbol FindShortestTour = initFinalSymbol("FindShortestTour", ID.FindShortestTour);

	/** FindSpanningTree(graph) - find the minimum spanning tree in the `graph`. */
	public final static IBuiltInSymbol FindSpanningTree = initFinalSymbol("FindSpanningTree", ID.FindSpanningTree);

	/**
	 * FindVertexCover(graph) - algorithm to find a vertex cover for a `graph`. A vertex cover is a set of vertices that
	 * touches all the edges in the graph.
	 */
	public final static IBuiltInSymbol FindVertexCover = initFinalSymbol("FindVertexCover", ID.FindVertexCover);

	/** First(expr) - returns the first element in `expr`. */
	public final static IBuiltInSymbol First = initFinalSymbol("First", ID.First);

	/**
	 * Fit(list-of-data-points, degree, variable) - solve a least squares problem using the Levenberg-Marquardt
	 * algorithm.
	 */
	public final static IBuiltInSymbol Fit = initFinalSymbol("Fit", ID.Fit);

	/**
	 * FiveNum({dataset}) - the Tuckey five-number summary is a set of descriptive statistics that provide information
	 * about a `dataset`. It consists of the five most important sample percentiles:
	 */
	public final static IBuiltInSymbol FiveNum = initFinalSymbol("FiveNum", ID.FiveNum);

	/** FixedPoint(f, expr) - starting with `expr`, iteratively applies `f` until the result no longer changes. */
	public final static IBuiltInSymbol FixedPoint = initFinalSymbol("FixedPoint", ID.FixedPoint);

	/**
	 * FixedPointList(f, expr) - starting with `expr`, iteratively applies `f` until the result no longer changes, and
	 * returns a list of all intermediate results.
	 */
	public final static IBuiltInSymbol FixedPointList = initFinalSymbol("FixedPointList", ID.FixedPointList);

	/**
	 * Flat - is an attribute that specifies that nested occurrences of a function should be automatically flattened.
	 */
	public final static IBuiltInSymbol Flat = initFinalSymbol("Flat", ID.Flat);

	/***/
	public final static IBuiltInSymbol FlatTopWindow = initFinalSymbol("FlatTopWindow", ID.FlatTopWindow);

	/** Flatten(expr) - flattens out nested lists in `expr`. */
	public final static IBuiltInSymbol Flatten = initFinalSymbol("Flatten", ID.Flatten);

	/** FlattenAt(expr, position) - flattens out nested lists at the given `position` in `expr`. */
	public final static IBuiltInSymbol FlattenAt = initFinalSymbol("FlattenAt", ID.FlattenAt);

	/***/
	public final static IBuiltInSymbol Float = initFinalSymbol("Float", ID.Float);

	/** Floor(expr) - gives the smallest integer less than or equal `expr`. */
	public final static IBuiltInSymbol Floor = initFinalSymbol("Floor", ID.Floor);

	/** Fold[f, x, {a, b}] - returns `f[f[x, a], b]`, and this nesting continues for lists of arbitrary length. */
	public final static IBuiltInSymbol Fold = initFinalSymbol("Fold", ID.Fold);

	/** FoldList[f, x, {a, b}] - returns `{x, f[x, a], f[f[x, a], b]}` */
	public final static IBuiltInSymbol FoldList = initFinalSymbol("FoldList", ID.FoldList);

	/**
	 * For(start, test, incr, body) - evaluates `start`, and then iteratively `body` and `incr` as long as test
	 * evaluates to `True`.
	 */
	public final static IBuiltInSymbol For = initFinalSymbol("For", ID.For);

	/***/
	public final static IBuiltInSymbol ForAll = initFinalSymbol("ForAll", ID.ForAll);

	/**
	 * Fourier(vector-of-complex-numbers) - Discrete Fourier transform of a `vector-of-complex-numbers`. Fourier
	 * transform is restricted to vectors with length of power of 2.
	 */
	public final static IBuiltInSymbol Fourier = initFinalSymbol("Fourier", ID.Fourier);

	/** FourierMatrix(n) - gives a fourier matrix with the dimension `n`. */
	public final static IBuiltInSymbol FourierMatrix = initFinalSymbol("FourierMatrix", ID.FourierMatrix);

	/** FractionalPart(number) - get the fractional part of a `number`. */
	public final static IBuiltInSymbol FractionalPart = initFinalSymbol("FractionalPart", ID.FractionalPart);

	/** FrechetDistribution(a,b) - returns a Frechet distribution. */
	public final static IBuiltInSymbol FrechetDistribution = initFinalSymbol("FrechetDistribution",
			ID.FrechetDistribution);

	/** FreeQ(`expr`, `x`) - returns 'True' if `expr` does not contain the expression `x`. */
	public final static IBuiltInSymbol FreeQ = initFinalSymbol("FreeQ", ID.FreeQ);

	/***/
	public final static IBuiltInSymbol FresnelC = initFinalSymbol("FresnelC", ID.FresnelC);

	/***/
	public final static IBuiltInSymbol FresnelS = initFinalSymbol("FresnelS", ID.FresnelS);

	/** FrobeniusNumber({a1, ... ,aN}) - returns the Frobenius number of the nonnegative integers `{a1, ... ,aN}` */
	public final static IBuiltInSymbol FrobeniusNumber = initFinalSymbol("FrobeniusNumber", ID.FrobeniusNumber);

	/**
	 * FrobeniusSolve({a1, ... ,aN}, M) - get a list of solutions for the Frobenius equation given by the list of
	 * integers `{a1, ... ,aN}` and the non-negative integer `M`.
	 */
	public final static IBuiltInSymbol FrobeniusSolve = initFinalSymbol("FrobeniusSolve", ID.FrobeniusSolve);

	/**
	 * FromCharacterCode({ch1, ch2, ...}) - converts the `ch1, ch2,...` character codes into a string of corresponding
	 * characters.
	 */
	public final static IBuiltInSymbol FromCharacterCode = initFinalSymbol("FromCharacterCode", ID.FromCharacterCode);

	/**
	 * FromContinuedFraction({n1, n2, ...}) - return the number which represents the continued fraction list `{n1, n2,
	 * ...}`.
	 */
	public final static IBuiltInSymbol FromContinuedFraction = initFinalSymbol("FromContinuedFraction",
			ID.FromContinuedFraction);

	/** FromDigits(list) - creates an expression from the list of digits for radix `10`. */
	public final static IBuiltInSymbol FromDigits = initFinalSymbol("FromDigits", ID.FromDigits);

	/** FromPolarCoordinates({r, t}) - return the cartesian coordinates for the polar coordinates `{r, t}`. */
	public final static IBuiltInSymbol FromPolarCoordinates = initFinalSymbol("FromPolarCoordinates",
			ID.FromPolarCoordinates);

	/***/
	public final static IBuiltInSymbol Full = initFinalSymbol("Full", ID.Full);

	/** FullForm(expression) - shows the internal representation of the given `expression`. */
	public final static IBuiltInSymbol FullForm = initFinalSymbol("FullForm", ID.FullForm);

	/**
	 * FullSimplify(expr) - works like `Simplify` but additionally tries some `FunctionExpand` rule transformations to
	 * simplify `expr`.
	 */
	public final static IBuiltInSymbol FullSimplify = initFinalSymbol("FullSimplify", ID.FullSimplify);

	/***/
	public final static IBuiltInSymbol Function = initFinalSymbol("Function", ID.Function);

	/**
	 * FunctionExpand(expression) - expands the special function `expression`. `FunctionExpand` expands simple nested
	 * radicals.
	 */
	public final static IBuiltInSymbol FunctionExpand = initFinalSymbol("FunctionExpand", ID.FunctionExpand);

	/***/
	public final static IBuiltInSymbol FunctionRange = initFinalSymbol("FunctionRange", ID.FunctionRange);

	/** GCD(n1, n2, ...) - computes the greatest common divisor of the given integers. */
	public final static IBuiltInSymbol GCD = initFinalSymbol("GCD", ID.GCD);

	/** Gamma(z) - is the gamma function on the complex number `z`. */
	public final static IBuiltInSymbol Gamma = initFinalSymbol("Gamma", ID.Gamma);

	/** GammaDistribution(a,b) - returns a gamma distribution. */
	public final static IBuiltInSymbol GammaDistribution = initFinalSymbol("GammaDistribution", ID.GammaDistribution);

	/***/
	public final static IBuiltInSymbol GammaRegularized = initFinalSymbol("GammaRegularized", ID.GammaRegularized);

	/** Gather(list, test) - gathers leaves of `list` into sub lists of items that are the same according to `test`. */
	public final static IBuiltInSymbol Gather = initFinalSymbol("Gather", ID.Gather);

	/** GatherBy(list, f) - gathers leaves of `list` into sub lists of items whose image under `f` identical. */
	public final static IBuiltInSymbol GatherBy = initFinalSymbol("GatherBy", ID.GatherBy);

	/***/
	public final static IBuiltInSymbol GaussianIntegers = initFinalSymbol("GaussianIntegers", ID.GaussianIntegers);

	/***/
	public final static IBuiltInSymbol GaussianMatrix = initFinalSymbol("GaussianMatrix", ID.GaussianMatrix);

	/***/
	public final static IBuiltInSymbol GaussianWindow = initFinalSymbol("GaussianWindow", ID.GaussianWindow);

	/** GegenbauerC(n, a, x) - returns the GegenbauerC polynomial. */
	public final static IBuiltInSymbol GegenbauerC = initFinalSymbol("GegenbauerC", ID.GegenbauerC);

	/***/
	public final static IBuiltInSymbol General = initFinalSymbol("General", ID.General);

	/**
	 * GeoDistance({latitude1,longitude1}, {latitude2,longitude2}) - returns the geodesic distance between
	 * `{latitude1,longitude1}` and `{latitude2,longitude2}`.
	 */
	public final static IBuiltInSymbol GeoDistance = initFinalSymbol("GeoDistance", ID.GeoDistance);

	/***/
	public final static IBuiltInSymbol GeoPosition = initFinalSymbol("GeoPosition", ID.GeoPosition);

	/***/
	public final static IBuiltInSymbol GeodesyData = initFinalSymbol("GeodesyData", ID.GeodesyData);

	/** GeometricDistribution(p) - returns a geometric distribution. */
	public final static IBuiltInSymbol GeometricDistribution = initFinalSymbol("GeometricDistribution",
			ID.GeometricDistribution);

	/** GeometricMean({a, b, c,...}) - returns the geometric mean of `{a, b, c,...}`. */
	public final static IBuiltInSymbol GeometricMean = initFinalSymbol("GeometricMean", ID.GeometricMean);

	/** Get("path-to-package-file-name") - load the package defined in `path-to-package-file-name`. */
	public final static IBuiltInSymbol Get = initFinalSymbol("Get", ID.Get);

	/** Glaisher - Glaisher constant. */
	public final static IBuiltInSymbol Glaisher = initFinalSymbol("Glaisher", ID.Glaisher);

	/***/
	public final static IBuiltInSymbol GoldenAngle = initFinalSymbol("GoldenAngle", ID.GoldenAngle);

	/** GoldenRatio - is the golden ratio `(1+Sqrt(5))/2`. */
	public final static IBuiltInSymbol GoldenRatio = initFinalSymbol("GoldenRatio", ID.GoldenRatio);

	/***/
	public final static IBuiltInSymbol GompertzMakehamDistribution = initFinalSymbol("GompertzMakehamDistribution",
			ID.GompertzMakehamDistribution);

	/** Grad(function, list-of-variables) - gives the gradient of the function. */
	public final static IBuiltInSymbol Grad = initFinalSymbol("Grad", ID.Grad);

	/** Graph({edge1,...,edgeN}) - create a graph from the given edges `edge1,...,edgeN`. */
	public final static IBuiltInSymbol Graph = initFinalSymbol("Graph", ID.Graph);

	/**
	 * GraphCenter(graph) - compute the `graph` center. The center of a `graph` is the set of vertices of graph
	 * eccentricity equal to the `graph` radius.
	 */
	public final static IBuiltInSymbol GraphCenter = initFinalSymbol("GraphCenter", ID.GraphCenter);

	/***/
	public final static IBuiltInSymbol GraphData = initFinalSymbol("GraphData", ID.GraphData);

	/** GraphDiameter(graph) - return the diameter of the `graph`. */
	public final static IBuiltInSymbol GraphDiameter = initFinalSymbol("GraphDiameter", ID.GraphDiameter);

	/**
	 * GraphPeriphery(graph) - compute the `graph` periphery. The periphery of a `graph` is the set of vertices of graph
	 * eccentricity equal to the graph diameter.
	 */
	public final static IBuiltInSymbol GraphPeriphery = initFinalSymbol("GraphPeriphery", ID.GraphPeriphery);

	/** GraphQ(expr) - test if `expr` is a graph object. */
	public final static IBuiltInSymbol GraphQ = initFinalSymbol("GraphQ", ID.GraphQ);

	/** GraphRadius(graph) - return the radius of the `graph`. */
	public final static IBuiltInSymbol GraphRadius = initFinalSymbol("GraphRadius", ID.GraphRadius);

	/***/
	public final static IBuiltInSymbol Graphics = initFinalSymbol("Graphics", ID.Graphics);

	/***/
	public final static IBuiltInSymbol Graphics3D = initFinalSymbol("Graphics3D", ID.Graphics3D);

	/** Greater(x, y) - yields `True` if `x` is known to be greater than `y`. */
	public final static IBuiltInSymbol Greater = initFinalSymbol("Greater", ID.Greater);

	/** GreaterEqual(x, y) - yields `True` if `x` is known to be greater than or equal to `y`. */
	public final static IBuiltInSymbol GreaterEqual = initFinalSymbol("GreaterEqual", ID.GreaterEqual);

	/**
	 * GroebnerBasis({polynomial-list},{variable-list}) - returns a Gröbner basis for the `polynomial-list` and
	 * `variable-list`.
	 */
	public final static IBuiltInSymbol GroebnerBasis = initFinalSymbol("GroebnerBasis", ID.GroebnerBasis);

	/***/
	public final static IBuiltInSymbol GroupBy = initFinalSymbol("GroupBy", ID.GroupBy);

	/** GumbelDistribution(a, b) - returns a Gumbel distribution. */
	public final static IBuiltInSymbol GumbelDistribution = initFinalSymbol("GumbelDistribution",
			ID.GumbelDistribution);

	/** HamiltonianGraphQ(graph) - returns `True` if `graph` is an hamiltonian graph, and `False` otherwise. */
	public final static IBuiltInSymbol HamiltonianGraphQ = initFinalSymbol("HamiltonianGraphQ", ID.HamiltonianGraphQ);

	/***/
	public final static IBuiltInSymbol HammingWindow = initFinalSymbol("HammingWindow", ID.HammingWindow);

	/***/
	public final static IBuiltInSymbol HankelH1 = initFinalSymbol("HankelH1", ID.HankelH1);

	/***/
	public final static IBuiltInSymbol HankelH2 = initFinalSymbol("HankelH2", ID.HankelH2);

	/***/
	public final static IBuiltInSymbol HannWindow = initFinalSymbol("HannWindow", ID.HannWindow);

	/** HarmonicMean({a, b, c,...}) - returns the harmonic mean of `{a, b, c,...}`. */
	public final static IBuiltInSymbol HarmonicMean = initFinalSymbol("HarmonicMean", ID.HarmonicMean);

	/** HarmonicNumber(n) - returns the `n`th harmonic number. */
	public final static IBuiltInSymbol HarmonicNumber = initFinalSymbol("HarmonicNumber", ID.HarmonicNumber);

	/** Haversine(z) - returns the haversine function of `z`. */
	public final static IBuiltInSymbol Haversine = initFinalSymbol("Haversine", ID.Haversine);

	/** Head(expr) - returns the head of the expression or atom `expr`. */
	public final static IBuiltInSymbol Head = initFinalSymbol("Head", ID.Head);

	/***/
	public final static IBuiltInSymbol Heads = initFinalSymbol("Heads", ID.Heads);

	/***/
	public final static IBuiltInSymbol HeavisideTheta = initFinalSymbol("HeavisideTheta", ID.HeavisideTheta);

	/** HermiteH(n, x) - returns the Hermite polynomial `H_n(x)`. */
	public final static IBuiltInSymbol HermiteH = initFinalSymbol("HermiteH", ID.HermiteH);

	/** HermitianMatrixQ(m) - returns `True` if `m` is a hermitian matrix. */
	public final static IBuiltInSymbol HermitianMatrixQ = initFinalSymbol("HermitianMatrixQ", ID.HermitianMatrixQ);

	/** HilbertMatrix(n) - gives the hilbert matrix with `n` rows and columns. */
	public final static IBuiltInSymbol HilbertMatrix = initFinalSymbol("HilbertMatrix", ID.HilbertMatrix);

	/***/
	public final static IBuiltInSymbol Histogram = initFinalSymbol("Histogram", ID.Histogram);

	/** Hold(expr) - `Hold` doesn't evaluate `expr`. */
	public final static IBuiltInSymbol Hold = initFinalSymbol("Hold", ID.Hold);

	/** HoldAll - is an attribute specifying that all arguments of a function should be left unevaluated. */
	public final static IBuiltInSymbol HoldAll = initFinalSymbol("HoldAll", ID.HoldAll);

	/***/
	public final static IBuiltInSymbol HoldAllComplete = initFinalSymbol("HoldAllComplete", ID.HoldAllComplete);

	/***/
	public final static IBuiltInSymbol HoldComplete = initFinalSymbol("HoldComplete", ID.HoldComplete);

	/** HoldFirst - is an attribute specifying that the first argument of a function should be left unevaluated. */
	public final static IBuiltInSymbol HoldFirst = initFinalSymbol("HoldFirst", ID.HoldFirst);

	/** HoldForm(expr) - `HoldForm` doesn't evaluate `expr` and didn't appear in the output. */
	public final static IBuiltInSymbol HoldForm = initFinalSymbol("HoldForm", ID.HoldForm);

	/** HoldPattern(expr) - `HoldPattern` doesn't evaluate `expr` for pattern-matching. */
	public final static IBuiltInSymbol HoldPattern = initFinalSymbol("HoldPattern", ID.HoldPattern);

	/**
	 * HoldRest - is an attribute specifying that all but the first argument of a function should be left unevaluated.
	 */
	public final static IBuiltInSymbol HoldRest = initFinalSymbol("HoldRest", ID.HoldRest);

	/***/
	public final static IBuiltInSymbol Horner = initFinalSymbol("Horner", ID.Horner);

	/** HornerForm(polynomial) - Generate the horner scheme for a univariate `polynomial`. */
	public final static IBuiltInSymbol HornerForm = initFinalSymbol("HornerForm", ID.HornerForm);

	/** HurwitzZeta(s, a) - returns the Hurwitz zeta function. */
	public final static IBuiltInSymbol HurwitzZeta = initFinalSymbol("HurwitzZeta", ID.HurwitzZeta);

	/** Hypergeometric0F1(b, z) - return the `Hypergeometric0F1` function */
	public final static IBuiltInSymbol Hypergeometric0F1 = initFinalSymbol("Hypergeometric0F1", ID.Hypergeometric0F1);

	/** Hypergeometric1F1(a, b, z) - return the `Hypergeometric1F1` function */
	public final static IBuiltInSymbol Hypergeometric1F1 = initFinalSymbol("Hypergeometric1F1", ID.Hypergeometric1F1);

	/***/
	public final static IBuiltInSymbol Hypergeometric1F1Regularized = initFinalSymbol("Hypergeometric1F1Regularized",
			ID.Hypergeometric1F1Regularized);

	/** Hypergeometric2F1(a, b, c, z) - return the `Hypergeometric2F1` function */
	public final static IBuiltInSymbol Hypergeometric2F1 = initFinalSymbol("Hypergeometric2F1", ID.Hypergeometric2F1);

	/** HypergeometricDistribution(n, s, t) - returns a hypergeometric distribution. */
	public final static IBuiltInSymbol HypergeometricDistribution = initFinalSymbol("HypergeometricDistribution",
			ID.HypergeometricDistribution);

	/** HypergeometricPFQ({a,...}, {b,...}, c) - return the `HypergeometricPFQ` function */
	public final static IBuiltInSymbol HypergeometricPFQ = initFinalSymbol("HypergeometricPFQ", ID.HypergeometricPFQ);

	/***/
	public final static IBuiltInSymbol HypergeometricPFQRegularized = initFinalSymbol("HypergeometricPFQRegularized",
			ID.HypergeometricPFQRegularized);

	/***/
	public final static IBuiltInSymbol HypergeometricU = initFinalSymbol("HypergeometricU", ID.HypergeometricU);

	/**
	 * I - Imaginary unit - internally converted to the complex number `0+1*i`. `I` represents the imaginary number
	 * `Sqrt(-1)`. `I^2` will be evaluated to `-1`.
	 */
	public final static IBuiltInSymbol I = initFinalSymbol("I", ID.I);

	/** Identity(expr) - returns `expr`. */
	public final static IBuiltInSymbol Identity = initFinalSymbol("Identity", ID.Identity);

	/** IdentityMatrix(n) - gives the identity matrix with `n` rows and columns. */
	public final static IBuiltInSymbol IdentityMatrix = initFinalSymbol("IdentityMatrix", ID.IdentityMatrix);

	/** If(cond, pos, neg) - returns `pos` if `cond` evaluates to `True`, and `neg` if it evaluates to `False`. */
	public final static IBuiltInSymbol If = initFinalSymbol("If", ID.If);

	/** Im(z) - returns the imaginary component of the complex number `z`. */
	public final static IBuiltInSymbol Im = initFinalSymbol("Im", ID.Im);

	/** Implies(arg1, arg2) - Logical implication. */
	public final static IBuiltInSymbol Implies = initFinalSymbol("Implies", ID.Implies);

	/**
	 * Import("path-to-filename", "WXF") - if the file system is enabled, import an expression in WXF format from the
	 * "path-to-filename" file.
	 */
	public final static IBuiltInSymbol Import = initFinalSymbol("Import", ID.Import);

	/** Increment(x) - increments `x` by `1`, returning the original value of `x`. */
	public final static IBuiltInSymbol Increment = initFinalSymbol("Increment", ID.Increment);

	/** Indeterminate - represents an indeterminate result. */
	public final static IBuiltInSymbol Indeterminate = initFinalSymbol("Indeterminate", ID.Indeterminate);

	/***/
	public final static IBuiltInSymbol Inequality = initFinalSymbol("Inequality", ID.Inequality);

	/** InexactNumberQ(expr) - returns `True` if `expr` is not an exact number, and `False` otherwise. */
	public final static IBuiltInSymbol InexactNumberQ = initFinalSymbol("InexactNumberQ", ID.InexactNumberQ);

	/** Infinity - represents an infinite real quantity. */
	public final static IBuiltInSymbol Infinity = initFinalSymbol("Infinity", ID.Infinity);

	/***/
	public final static IBuiltInSymbol Infix = initFinalSymbol("Infix", ID.Infix);

	/***/
	public final static IBuiltInSymbol Information = initFinalSymbol("Information", ID.Information);

	/**
	 * Inner(f, x, y, g) - computes a generalized inner product of `x` and `y`, using a multiplication function `f` and
	 * an addition function `g`.
	 */
	public final static IBuiltInSymbol Inner = initFinalSymbol("Inner", ID.Inner);

	/**
	 * Input() - if the file system is enabled, the user can input an expression. After input this expression will be
	 * evaluated immediately.
	 */
	public final static IBuiltInSymbol Input = initFinalSymbol("Input", ID.Input);

	/***/
	public final static IBuiltInSymbol InputField = initFinalSymbol("InputField", ID.InputField);

	/***/
	public final static IBuiltInSymbol InputForm = initFinalSymbol("InputForm", ID.InputForm);

	/** InputString() - if the file system is enabled, the user can input a string. */
	public final static IBuiltInSymbol InputString = initFinalSymbol("InputString", ID.InputString);

	/***/
	public final static IBuiltInSymbol Insert = initFinalSymbol("Insert", ID.Insert);

	/** Integer - is the head of integers. */
	public final static IBuiltInSymbol Integer = initFinalSymbol("Integer", ID.Integer);

	/** IntegerDigits(n, base) - returns a list of integer digits for `n` under `base`. */
	public final static IBuiltInSymbol IntegerDigits = initFinalSymbol("IntegerDigits", ID.IntegerDigits);

	/** IntegerExponent(n, b) - gives the highest exponent of `b` that divides `n`. */
	public final static IBuiltInSymbol IntegerExponent = initFinalSymbol("IntegerExponent", ID.IntegerExponent);

	/** IntegerLength(x) - gives the number of digits in the base-10 representation of `x`. */
	public final static IBuiltInSymbol IntegerLength = initFinalSymbol("IntegerLength", ID.IntegerLength);

	/***/
	public final static IBuiltInSymbol IntegerName = initFinalSymbol("IntegerName", ID.IntegerName);

	/** IntegerPart(expr) - for real `expr` return the integer part of `expr`. */
	public final static IBuiltInSymbol IntegerPart = initFinalSymbol("IntegerPart", ID.IntegerPart);

	/** IntegerPartitions(n) - returns all partitions of the integer `n`. */
	public final static IBuiltInSymbol IntegerPartitions = initFinalSymbol("IntegerPartitions", ID.IntegerPartitions);

	/** IntegerQ(expr) - returns `True` if `expr` is an integer, and `False` otherwise. */
	public final static IBuiltInSymbol IntegerQ = initFinalSymbol("IntegerQ", ID.IntegerQ);

	/** Integers - is the set of integer numbers. */
	public final static IBuiltInSymbol Integers = initFinalSymbol("Integers", ID.Integers);

	/**
	 * Integrate(f, x) - integrates `f` with respect to `x`. The result does not contain the additive integration
	 * constant.
	 */
	public final static IBuiltInSymbol Integrate = initFinalSymbol("Integrate", ID.Integrate);

	/**
	 * InterpolatingFunction(data-list) - get the representation for the given `data-list` as piecewise
	 * `InterpolatingPolynomial`s.
	 */
	public final static IBuiltInSymbol InterpolatingFunction = initFinalSymbol("InterpolatingFunction",
			ID.InterpolatingFunction);

	/** InterpolatingPolynomial(data-list, symbol) - get the polynomial representation for the given `data-list`. */
	public final static IBuiltInSymbol InterpolatingPolynomial = initFinalSymbol("InterpolatingPolynomial",
			ID.InterpolatingPolynomial);

	/***/
	public final static IBuiltInSymbol Interpolation = initFinalSymbol("Interpolation", ID.Interpolation);

	/** Interrupt( ) - Interrupt an evaluation and returns `$Aborted`. */
	public final static IBuiltInSymbol Interrupt = initFinalSymbol("Interrupt", ID.Interrupt);

	/***/
	public final static IBuiltInSymbol IntersectingQ = initFinalSymbol("IntersectingQ", ID.IntersectingQ);

	/** Intersection(set1, set2, ...) - get the intersection set from `set1` and `set2` .... */
	public final static IBuiltInSymbol Intersection = initFinalSymbol("Intersection", ID.Intersection);

	/** Interval({a, b}) - represents the interval from `a` to `b`. */
	public final static IBuiltInSymbol Interval = initFinalSymbol("Interval", ID.Interval);

	/** Inverse(matrix) - computes the inverse of the `matrix`. */
	public final static IBuiltInSymbol Inverse = initFinalSymbol("Inverse", ID.Inverse);

	/***/
	public final static IBuiltInSymbol InverseBetaRegularized = initFinalSymbol("InverseBetaRegularized",
			ID.InverseBetaRegularized);

	/**
	 * InverseCDF(dist, q) - returns the inverse cumulative distribution for the distribution `dist` as a function of
	 * `q`
	 */
	public final static IBuiltInSymbol InverseCDF = initFinalSymbol("InverseCDF", ID.InverseCDF);

	/** InverseErf(z) - returns the inverse error function of `z`. */
	public final static IBuiltInSymbol InverseErf = initFinalSymbol("InverseErf", ID.InverseErf);

	/** InverseErfc(z) - returns the inverse complementary error function of `z`. */
	public final static IBuiltInSymbol InverseErfc = initFinalSymbol("InverseErfc", ID.InverseErfc);

	/**
	 * InverseFourier(vector-of-complex-numbers) - Inverse discrete Fourier transform of a `vector-of-complex-numbers`.
	 * Fourier transform is restricted to vectors with length of power of 2.
	 */
	public final static IBuiltInSymbol InverseFourier = initFinalSymbol("InverseFourier", ID.InverseFourier);

	/** InverseFunction(head) - returns the inverse function for the symbol `head`. */
	public final static IBuiltInSymbol InverseFunction = initFinalSymbol("InverseFunction", ID.InverseFunction);

	/***/
	public final static IBuiltInSymbol InverseGammaRegularized = initFinalSymbol("InverseGammaRegularized",
			ID.InverseGammaRegularized);

	/** InverseHaversine(z) - returns the inverse haversine function of `z`. */
	public final static IBuiltInSymbol InverseHaversine = initFinalSymbol("InverseHaversine", ID.InverseHaversine);

	/** InverseLaplaceTransform(f,s,t) - returns the inverse laplace transform. */
	public final static IBuiltInSymbol InverseLaplaceTransform = initFinalSymbol("InverseLaplaceTransform",
			ID.InverseLaplaceTransform);

	/** InverseSeries( series ) - return the inverse series. */
	public final static IBuiltInSymbol InverseSeries = initFinalSymbol("InverseSeries", ID.InverseSeries);

	/***/
	public final static IBuiltInSymbol InverseWeierstrassP = initFinalSymbol("InverseWeierstrassP",
			ID.InverseWeierstrassP);

	/** JSForm(expr) - returns the JavaScript form of the `expr`. */
	public final static IBuiltInSymbol JSForm = initFinalSymbol("JSForm", ID.JSForm);

	/***/
	public final static IBuiltInSymbol JSFormData = initFinalSymbol("JSFormData", ID.JSFormData);

	/**
	 * JaccardDissimilarity(u, v) - returns the Jaccard-Needham dissimilarity between the two boolean 1-D lists `u` and
	 * `v`, which is defined as `(c_tf + c_ft) / (c_tt + c_ft + c_tf)`, where n is `len(u)` and `c_ij` is the number of
	 * occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
	 */
	public final static IBuiltInSymbol JaccardDissimilarity = initFinalSymbol("JaccardDissimilarity",
			ID.JaccardDissimilarity);

	/** JacobiAmplitude(x, m) - returns the amplitude `am(x, m)` for Jacobian elliptic function. */
	public final static IBuiltInSymbol JacobiAmplitude = initFinalSymbol("JacobiAmplitude", ID.JacobiAmplitude);

	/** JacobiCN(x, m) - returns the Jacobian elliptic function `cn(x, m)`. */
	public final static IBuiltInSymbol JacobiCN = initFinalSymbol("JacobiCN", ID.JacobiCN);

	/** JacobiDN(x, m) - returns the Jacobian elliptic function `dn(x, m)`. */
	public final static IBuiltInSymbol JacobiDN = initFinalSymbol("JacobiDN", ID.JacobiDN);

	/** JacobiMatrix(matrix, var) - creates a Jacobian matrix. */
	public final static IBuiltInSymbol JacobiMatrix = initFinalSymbol("JacobiMatrix", ID.JacobiMatrix);

	/** JacobiSN(x, m) - returns the Jacobian elliptic function `sn(x, m)`. */
	public final static IBuiltInSymbol JacobiSN = initFinalSymbol("JacobiSN", ID.JacobiSN);

	/** JacobiSymbol(m, n) - calculates the Jacobi symbol. */
	public final static IBuiltInSymbol JacobiSymbol = initFinalSymbol("JacobiSymbol", ID.JacobiSymbol);

	/***/
	public final static IBuiltInSymbol JacobiZeta = initFinalSymbol("JacobiZeta", ID.JacobiZeta);

	/**
	 * JavaForm(expr) - returns the Symja Java form of the `expr`. In Java you can use the created Symja expressions.
	 */
	public final static IBuiltInSymbol JavaForm = initFinalSymbol("JavaForm", ID.JavaForm);

	/** Join(l1, l2) - concatenates the lists `l1` and `l2`. */
	public final static IBuiltInSymbol Join = initFinalSymbol("Join", ID.Join);

	/***/
	public final static IBuiltInSymbol KOrderlessPartitions = initFinalSymbol("KOrderlessPartitions",
			ID.KOrderlessPartitions);

	/***/
	public final static IBuiltInSymbol KPartitions = initFinalSymbol("KPartitions", ID.KPartitions);

	/***/
	public final static IBuiltInSymbol Key = initFinalSymbol("Key", ID.Key);

	/***/
	public final static IBuiltInSymbol KeyExistsQ = initFinalSymbol("KeyExistsQ", ID.KeyExistsQ);

	/** KeySort(<|key1->value1, ...|>) - sort the `<|key1->value1, ...|>` entries by the `key` values. */
	public final static IBuiltInSymbol KeySort = initFinalSymbol("KeySort", ID.KeySort);

	/** Keys(association) - return a list of keys of the `association`. */
	public final static IBuiltInSymbol Keys = initFinalSymbol("Keys", ID.Keys);

	/** Khinchin - Khinchin's constant */
	public final static IBuiltInSymbol Khinchin = initFinalSymbol("Khinchin", ID.Khinchin);

	/***/
	public final static IBuiltInSymbol KleinInvariantJ = initFinalSymbol("KleinInvariantJ", ID.KleinInvariantJ);

	/***/
	public final static IBuiltInSymbol KnownUnitQ = initFinalSymbol("KnownUnitQ", ID.KnownUnitQ);

	/**
	 * KolmogorovSmirnovTest(data) - Computes the `p-value`, or <i>observed significance level</i>, of a one-sample
	 * [Wikipedia:Kolmogorov-Smirnov test](http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test) evaluating the null
	 * hypothesis that `data` conforms to the `NormalDistribution()`.
	 */
	public final static IBuiltInSymbol KolmogorovSmirnovTest = initFinalSymbol("KolmogorovSmirnovTest",
			ID.KolmogorovSmirnovTest);

	/**
	 * KroneckerDelta(arg1, arg2, ... argN) - if all arguments `arg1` to `argN` are equal return `1`, otherwise return
	 * `0`.
	 */
	public final static IBuiltInSymbol KroneckerDelta = initFinalSymbol("KroneckerDelta", ID.KroneckerDelta);

	/** Kurtosis(list) - gives the Pearson measure of kurtosis for `list` (a measure of existing outliers). */
	public final static IBuiltInSymbol Kurtosis = initFinalSymbol("Kurtosis", ID.Kurtosis);

	/** LCM(n1, n2, ...) - computes the least common multiple of the given integers. */
	public final static IBuiltInSymbol LCM = initFinalSymbol("LCM", ID.LCM);

	/** LUDecomposition(matrix) - calculate the LUP-decomposition of a square `matrix`. */
	public final static IBuiltInSymbol LUDecomposition = initFinalSymbol("LUDecomposition", ID.LUDecomposition);

	/** LaguerreL(n, x) - returns the Laguerre polynomial `L_n(x)`. */
	public final static IBuiltInSymbol LaguerreL = initFinalSymbol("LaguerreL", ID.LaguerreL);

	/** LaplaceTransform(f,t,s) - returns the laplace transform. */
	public final static IBuiltInSymbol LaplaceTransform = initFinalSymbol("LaplaceTransform", ID.LaplaceTransform);

	/** Last(expr) - returns the last element in `expr`. */
	public final static IBuiltInSymbol Last = initFinalSymbol("Last", ID.Last);

	/** LeafCount(expr) - returns the total number of indivisible subexpressions in `expr`. */
	public final static IBuiltInSymbol LeafCount = initFinalSymbol("LeafCount", ID.LeafCount);

	/** LeastSquares(matrix, right) - solves the linear least-squares problem 'matrix . x = right'. */
	public final static IBuiltInSymbol LeastSquares = initFinalSymbol("LeastSquares", ID.LeastSquares);

	/***/
	public final static IBuiltInSymbol Left = initFinalSymbol("Left", ID.Left);

	/** LegendreP(n, x) - returns the Legendre polynomial `P_n(x)`. */
	public final static IBuiltInSymbol LegendreP = initFinalSymbol("LegendreP", ID.LegendreP);

	/** LegendreQ(n, x) - returns the Legendre functions of the second kind `Q_n(x)`. */
	public final static IBuiltInSymbol LegendreQ = initFinalSymbol("LegendreQ", ID.LegendreQ);

	/** Length(expr) - returns the number of leaves in `expr`. */
	public final static IBuiltInSymbol Length = initFinalSymbol("Length", ID.Length);

	/** Less(x, y) - yields `True` if `x` is known to be less than `y`. */
	public final static IBuiltInSymbol Less = initFinalSymbol("Less", ID.Less);

	/** LessEqual(x, y) - yields `True` if `x` is known to be less than or equal `y`. */
	public final static IBuiltInSymbol LessEqual = initFinalSymbol("LessEqual", ID.LessEqual);

	/** LetterQ(expr) - tests whether `expr` is a string, which only contains letters. */
	public final static IBuiltInSymbol LetterQ = initFinalSymbol("LetterQ", ID.LetterQ);

	/**
	 * Level(expr, levelspec) - gives a list of all sub-expressions of `expr` at the level(s) specified by `levelspec`.
	 */
	public final static IBuiltInSymbol Level = initFinalSymbol("Level", ID.Level);

	/** LevelQ(expr) - tests whether `expr` is a valid level specification. */
	public final static IBuiltInSymbol LevelQ = initFinalSymbol("LevelQ", ID.LevelQ);

	/***/
	public final static IBuiltInSymbol Lexicographic = initFinalSymbol("Lexicographic", ID.Lexicographic);

	/** Limit(expr, x->x0) - gives the limit of `expr` as `x` approaches `x0` */
	public final static IBuiltInSymbol Limit = initFinalSymbol("Limit", ID.Limit);

	/***/
	public final static IBuiltInSymbol Line = initFinalSymbol("Line", ID.Line);

	/***/
	public final static IBuiltInSymbol LinearModelFit = initFinalSymbol("LinearModelFit", ID.LinearModelFit);

	/**
	 * LinearProgramming(coefficientsOfLinearObjectiveFunction, constraintList, constraintRelationList) - the
	 * `LinearProgramming` function provides an implementation of [George Dantzig's simplex
	 * algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear optimization problems with linear
	 * equality and inequality constraints and implicit non-negative variables.
	 */
	public final static IBuiltInSymbol LinearProgramming = initFinalSymbol("LinearProgramming", ID.LinearProgramming);

	/**
	 * LinearRecurrence(list1, list2, n) - solve the linear recurrence and return the generated sequence of elements.
	 */
	public final static IBuiltInSymbol LinearRecurrence = initFinalSymbol("LinearRecurrence", ID.LinearRecurrence);

	/**
	 * LinearSolve(matrix, right) - solves the linear equation system 'matrix . x = right' and returns one corresponding
	 * solution `x`.
	 */
	public final static IBuiltInSymbol LinearSolve = initFinalSymbol("LinearSolve", ID.LinearSolve);

	/***/
	public final static IBuiltInSymbol LiouvilleLambda = initFinalSymbol("LiouvilleLambda", ID.LiouvilleLambda);

	/** List(e1, e2, ..., ei) - represents a list containing the elements `e1...ei`. */
	public final static IBuiltInSymbol List = initFinalSymbol("List", ID.List);

	/** ListConvolve(kernel-list, tensor-list) - create the convolution of the `kernel-list` with `tensor-list`. */
	public final static IBuiltInSymbol ListConvolve = initFinalSymbol("ListConvolve", ID.ListConvolve);

	/***/
	public final static IBuiltInSymbol ListContourPlot = initFinalSymbol("ListContourPlot", ID.ListContourPlot);

	/** ListCorrelate(kernel-list, tensor-list) - create the correlation of the `kernel-list` with `tensor-list`. */
	public final static IBuiltInSymbol ListCorrelate = initFinalSymbol("ListCorrelate", ID.ListCorrelate);

	/** ListLinePlot( { list-of-points } ) - generate a JavaScript list line plot control for the `list-of-points`. */
	public final static IBuiltInSymbol ListLinePlot = initFinalSymbol("ListLinePlot", ID.ListLinePlot);

	/** ListPlot( { list-of-points } ) - generate a JavaScript list plot control for the `list-of-points`. */
	public final static IBuiltInSymbol ListPlot = initFinalSymbol("ListPlot", ID.ListPlot);

	/** ListPlot3D( { list-of-points } ) - generate a JavaScript list plot 3D control for the `list-of-points`. */
	public final static IBuiltInSymbol ListPlot3D = initFinalSymbol("ListPlot3D", ID.ListPlot3D);

	/** ListQ(expr) - tests whether `expr` is a `List`. */
	public final static IBuiltInSymbol ListQ = initFinalSymbol("ListQ", ID.ListQ);

	/**
	 * Listable - is an attribute specifying that a function should be automatically applied to each element of a list.
	 */
	public final static IBuiltInSymbol Listable = initFinalSymbol("Listable", ID.Listable);

	/***/
	public final static IBuiltInSymbol Literal = initFinalSymbol("Literal", ID.Literal);

	/** Log(z) - returns the natural logarithm of `z`. */
	public final static IBuiltInSymbol Log = initFinalSymbol("Log", ID.Log);

	/**
	 * Log10(z) - returns the base-`10` logarithm of `z`. `Log10(z)` will be converted to `Log(z)/Log(10)` in symbolic
	 * mode.
	 */
	public final static IBuiltInSymbol Log10 = initFinalSymbol("Log10", ID.Log10);

	/**
	 * Log2(z) - returns the base-`2` logarithm of `z`. `Log2(z)` will be converted to `Log(z)/Log(2)` in symbolic mode.
	 */
	public final static IBuiltInSymbol Log2 = initFinalSymbol("Log2", ID.Log2);

	/***/
	public final static IBuiltInSymbol LogGamma = initFinalSymbol("LogGamma", ID.LogGamma);

	/***/
	public final static IBuiltInSymbol LogIntegral = initFinalSymbol("LogIntegral", ID.LogIntegral);

	/** LogNormalDistribution(m, s) - returns a log-normal distribution. */
	public final static IBuiltInSymbol LogNormalDistribution = initFinalSymbol("LogNormalDistribution",
			ID.LogNormalDistribution);

	/***/
	public final static IBuiltInSymbol LogicalExpand = initFinalSymbol("LogicalExpand", ID.LogicalExpand);

	/** LogisticSigmoid(z) - returns the logistic sigmoid of `z`. */
	public final static IBuiltInSymbol LogisticSigmoid = initFinalSymbol("LogisticSigmoid", ID.LogisticSigmoid);

	/***/
	public final static IBuiltInSymbol LongForm = initFinalSymbol("LongForm", ID.LongForm);

	/**
	 * Lookup(association, key) - return the value in the `association` which is associated with the `key`. If no value
	 * is available return `Missing("KeyAbsent",key)`.
	 */
	public final static IBuiltInSymbol Lookup = initFinalSymbol("Lookup", ID.Lookup);

	/***/
	public final static IBuiltInSymbol LowerCaseQ = initFinalSymbol("LowerCaseQ", ID.LowerCaseQ);

	/** LowerTriangularize(matrix) - create a lower triangular matrix from the given `matrix`. */
	public final static IBuiltInSymbol LowerTriangularize = initFinalSymbol("LowerTriangularize",
			ID.LowerTriangularize);

	/** LucasL(n) - gives the `n`th Lucas number. */
	public final static IBuiltInSymbol LucasL = initFinalSymbol("LucasL", ID.LucasL);

	/** MachineNumberQ(expr) - returns `True` if `expr` is a machine-precision real or complex number. */
	public final static IBuiltInSymbol MachineNumberQ = initFinalSymbol("MachineNumberQ", ID.MachineNumberQ);

	/** MangoldtLambda(n) - the von Mangoldt function of `n` */
	public final static IBuiltInSymbol MangoldtLambda = initFinalSymbol("MangoldtLambda", ID.MangoldtLambda);

	/**
	 * ManhattanDistance(u, v) - returns the Manhattan distance between `u` and `v`, which is the number of horizontal
	 * or vertical moves in the grid like Manhattan city layout to get from `u` to `v`.
	 */
	public final static IBuiltInSymbol ManhattanDistance = initFinalSymbol("ManhattanDistance", ID.ManhattanDistance);

	/**
	 * Manipulate(plot, {x, min, max}) - generate a JavaScript control for the expression `plot` which can be
	 * manipulated by a range slider `{x, min, max}`.
	 */
	public final static IBuiltInSymbol Manipulate = initFinalSymbol("Manipulate", ID.Manipulate);

	/***/
	public final static IBuiltInSymbol MantissaExponent = initFinalSymbol("MantissaExponent", ID.MantissaExponent);

	/** Map(f, expr) or f /@ expr - applies `f` to each part on the first level of `expr`. */
	public final static IBuiltInSymbol Map = initFinalSymbol("Map", ID.Map);

	/***/
	public final static IBuiltInSymbol MapAll = initFinalSymbol("MapAll", ID.MapAll);

	/***/
	public final static IBuiltInSymbol MapAt = initFinalSymbol("MapAt", ID.MapAt);

	/**
	 * MapIndexed(f, expr) - applies `f` to each part on the first level of `expr` and appending the elements position
	 * as a list in the second argument.
	 */
	public final static IBuiltInSymbol MapIndexed = initFinalSymbol("MapIndexed", ID.MapIndexed);

	/** MapThread(f, {{a1, a2, ...}, {b1, b2, ...}, ...}) - returns `{f(a1, b1, ...), f(a2, b2, ...), ...}`. */
	public final static IBuiltInSymbol MapThread = initFinalSymbol("MapThread", ID.MapThread);

	/** MatchQ(expr, form) - tests whether `expr` matches `form`. */
	public final static IBuiltInSymbol MatchQ = initFinalSymbol("MatchQ", ID.MatchQ);

	/**
	 * MatchingDissimilarity(u, v) - returns the Matching dissimilarity between the two boolean 1-D lists `u` and `v`,
	 * which is defined as `(c_tf + c_ft) / n`, where `n` is `len(u)` and `c_ij` is the number of occurrences of
	 * `u(k)=i` and `v(k)=j` for `k<n`.
	 */
	public final static IBuiltInSymbol MatchingDissimilarity = initFinalSymbol("MatchingDissimilarity",
			ID.MatchingDissimilarity);

	/** MathMLForm(expr) - returns the MathML form of the evaluated `expr`. */
	public final static IBuiltInSymbol MathMLForm = initFinalSymbol("MathMLForm", ID.MathMLForm);

	/***/
	public final static IBuiltInSymbol MatrixExp = initFinalSymbol("MatrixExp", ID.MatrixExp);

	/***/
	public final static IBuiltInSymbol MatrixForm = initFinalSymbol("MatrixForm", ID.MatrixForm);

	/**
	 * MatrixMinimalPolynomial(matrix, var) - computes the matrix minimal polynomial of a `matrix` for the variable
	 * `var`.
	 */
	public final static IBuiltInSymbol MatrixMinimalPolynomial = initFinalSymbol("MatrixMinimalPolynomial",
			ID.MatrixMinimalPolynomial);

	/***/
	public final static IBuiltInSymbol MatrixPlot = initFinalSymbol("MatrixPlot", ID.MatrixPlot);

	/** MatrixPower(matrix, n) - computes the `n`th power of a `matrix` */
	public final static IBuiltInSymbol MatrixPower = initFinalSymbol("MatrixPower", ID.MatrixPower);

	/** MatrixQ(m) - returns `True` if `m` is a list of equal-length lists. */
	public final static IBuiltInSymbol MatrixQ = initFinalSymbol("MatrixQ", ID.MatrixQ);

	/** MatrixRank(matrix) - returns the rank of `matrix`. */
	public final static IBuiltInSymbol MatrixRank = initFinalSymbol("MatrixRank", ID.MatrixRank);

	/** Max(e_1, e_2, ..., e_i) - returns the expression with the greatest value among the `e_i`. */
	public final static IBuiltInSymbol Max = initFinalSymbol("Max", ID.Max);

	/** MaxFilter(list, r) - filter which evaluates the `Max` of `list` for the radius `r`. */
	public final static IBuiltInSymbol MaxFilter = initFinalSymbol("MaxFilter", ID.MaxFilter);

	/***/
	public final static IBuiltInSymbol MaxIterations = initFinalSymbol("MaxIterations", ID.MaxIterations);

	/***/
	public final static IBuiltInSymbol MaxPoints = initFinalSymbol("MaxPoints", ID.MaxPoints);

	/** Maximize(unary-function, variable) - returns the maximum of the unary function for the given `variable`. */
	public final static IBuiltInSymbol Maximize = initFinalSymbol("Maximize", ID.Maximize);

	/** Mean(list) - returns the statistical mean of `list`. */
	public final static IBuiltInSymbol Mean = initFinalSymbol("Mean", ID.Mean);

	/***/
	public final static IBuiltInSymbol MeanDeviation = initFinalSymbol("MeanDeviation", ID.MeanDeviation);

	/** MeanFilter(list, r) - filter which evaluates the `Mean` of `list` for the radius `r`. */
	public final static IBuiltInSymbol MeanFilter = initFinalSymbol("MeanFilter", ID.MeanFilter);

	/** Median(list) - returns the median of `list`. */
	public final static IBuiltInSymbol Median = initFinalSymbol("Median", ID.Median);

	/** MedianFilter(list, r) - filter which evaluates the `Median` of `list` for the radius `r`. */
	public final static IBuiltInSymbol MedianFilter = initFinalSymbol("MedianFilter", ID.MedianFilter);

	/***/
	public final static IBuiltInSymbol MeijerG = initFinalSymbol("MeijerG", ID.MeijerG);

	/** MemberQ(list, pattern) - returns `True` if pattern matches any element of `list`, or `False` otherwise. */
	public final static IBuiltInSymbol MemberQ = initFinalSymbol("MemberQ", ID.MemberQ);

	/** MersennePrimeExponent(n) - returns the `n`th mersenne prime exponent. `2^n - 1` must be a prime number. */
	public final static IBuiltInSymbol MersennePrimeExponent = initFinalSymbol("MersennePrimeExponent",
			ID.MersennePrimeExponent);

	/**
	 * MersennePrimeExponentQ(n) - returns `True` if `2^n - 1` is a prime number. Currently `0 <= n <= 47` can be
	 * computed in reasonable time.
	 */
	public final static IBuiltInSymbol MersennePrimeExponentQ = initFinalSymbol("MersennePrimeExponentQ",
			ID.MersennePrimeExponentQ);

	/***/
	public final static IBuiltInSymbol MeshRange = initFinalSymbol("MeshRange", ID.MeshRange);

	/**
	 * Message(symbol::msg, expr1, expr2, ...) - displays the specified message, replacing placeholders in the message
	 * text with the corresponding expressions.
	 */
	public final static IBuiltInSymbol Message = initFinalSymbol("Message", ID.Message);

	/**
	 * MessageName(symbol, msg) - `symbol::msg` identifies a message. `MessageName` is the head of message IDs of the
	 * form `symbol::tag`.
	 */
	public final static IBuiltInSymbol MessageName = initFinalSymbol("MessageName", ID.MessageName);

	/***/
	public final static IBuiltInSymbol Messages = initFinalSymbol("Messages", ID.Messages);

	/***/
	public final static IBuiltInSymbol Method = initFinalSymbol("Method", ID.Method);

	/** Min(e_1, e_2, ..., e_i) - returns the expression with the lowest value among the `e_i`. */
	public final static IBuiltInSymbol Min = initFinalSymbol("Min", ID.Min);

	/** MinFilter(list, r) - filter which evaluates the `Min` of `list` for the radius `r`. */
	public final static IBuiltInSymbol MinFilter = initFinalSymbol("MinFilter", ID.MinFilter);

	/***/
	public final static IBuiltInSymbol MinMax = initFinalSymbol("MinMax", ID.MinMax);

	/***/
	public final static IBuiltInSymbol MinimalPolynomial = initFinalSymbol("MinimalPolynomial", ID.MinimalPolynomial);

	/** Minimize(unary-function, variable) - returns the minimum of the unary function for the given `variable`. */
	public final static IBuiltInSymbol Minimize = initFinalSymbol("Minimize", ID.Minimize);

	/** Minus(expr) - is the negation of `expr`. */
	public final static IBuiltInSymbol Minus = initFinalSymbol("Minus", ID.Minus);

	/***/
	public final static IBuiltInSymbol Missing = initFinalSymbol("Missing", ID.Missing);

	/** MissingQ(expr) - returns `True` if `expr` is a `Missing()` expression. */
	public final static IBuiltInSymbol MissingQ = initFinalSymbol("MissingQ", ID.MissingQ);

	/** Mod(x, m) - returns `x` modulo `m`. */
	public final static IBuiltInSymbol Mod = initFinalSymbol("Mod", ID.Mod);

	/**
	 * Module({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables` by renaming local
	 * variables.
	 */
	public final static IBuiltInSymbol Module = initFinalSymbol("Module", ID.Module);

	/***/
	public final static IBuiltInSymbol Modulus = initFinalSymbol("Modulus", ID.Modulus);

	/** MoebiusMu(expr) - calculate the Möbius function. */
	public final static IBuiltInSymbol MoebiusMu = initFinalSymbol("MoebiusMu", ID.MoebiusMu);

	/**
	 * MonomialList(polynomial, list-of-variables) - get the list of monomials of a `polynomial` expression, with
	 * respect to the `list-of-variables`.
	 */
	public final static IBuiltInSymbol MonomialList = initFinalSymbol("MonomialList", ID.MonomialList);

	/***/
	public final static IBuiltInSymbol MonomialOrder = initFinalSymbol("MonomialOrder", ID.MonomialOrder);

	/** Most(expr) - returns `expr` with the last element removed. */
	public final static IBuiltInSymbol Most = initFinalSymbol("Most", ID.Most);

	/** Multinomial(n1, n2, ...) - gives the multinomial coefficient `(n1+n2+...)!/(n1! n2! ...)`. */
	public final static IBuiltInSymbol Multinomial = initFinalSymbol("Multinomial", ID.Multinomial);

	/** MultiplicativeOrder(a, n) - gives the multiplicative order `a` modulo `n`. */
	public final static IBuiltInSymbol MultiplicativeOrder = initFinalSymbol("MultiplicativeOrder",
			ID.MultiplicativeOrder);

	/** N(expr) - gives the numerical value of `expr`. */
	public final static IBuiltInSymbol N = initFinalSymbol("N", ID.N);

	/**
	 * ND(function, x, value) - returns a numerical approximation of the partial derivative of the `function` for the
	 * variable `x` and the given `value`.
	 */
	public final static IBuiltInSymbol ND = initFinalSymbol("ND", ID.ND);

	/***/
	public final static IBuiltInSymbol NDSolve = initFinalSymbol("NDSolve", ID.NDSolve);

	/***/
	public final static IBuiltInSymbol NFourierTransform = initFinalSymbol("NFourierTransform", ID.NFourierTransform);

	/** NHoldAll - is an attribute that protects all arguments of a function from numeric evaluation. */
	public final static IBuiltInSymbol NHoldAll = initFinalSymbol("NHoldAll", ID.NHoldAll);

	/** NHoldFirst - is an attribute that protects the first argument of a function from numeric evaluation. */
	public final static IBuiltInSymbol NHoldFirst = initFinalSymbol("NHoldFirst", ID.NHoldFirst);

	/** NHoldRest - is an attribute that protects all but the first argument of a function from numeric evaluation. */
	public final static IBuiltInSymbol NHoldRest = initFinalSymbol("NHoldRest", ID.NHoldRest);

	/**
	 * NIntegrate(f, {x,a,b}) - computes the numerical univariate real integral of `f` with respect to `x` from `a` to
	 * `b`.
	 */
	public final static IBuiltInSymbol NIntegrate = initFinalSymbol("NIntegrate", ID.NIntegrate);

	/**
	 * NMaximize({maximize_function, constraints}, variables_list) - the `NMaximize` function provides an implementation
	 * of [George Dantzig's simplex algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear
	 * optimization problems with linear equality and inequality constraints and implicit non-negative variables.
	 */
	public final static IBuiltInSymbol NMaximize = initFinalSymbol("NMaximize", ID.NMaximize);

	/**
	 * NMinimize({maximize_function, constraints}, variables_list) - the `NMinimize` function provides an implementation
	 * of [George Dantzig's simplex algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear
	 * optimization problems with linear equality and inequality constraints and implicit non-negative variables.
	 */
	public final static IBuiltInSymbol NMinimize = initFinalSymbol("NMinimize", ID.NMinimize);

	/** NRoots(poly) - gives the numerical roots of polynomial `poly`. */
	public final static IBuiltInSymbol NRoots = initFinalSymbol("NRoots", ID.NRoots);

	/** NSolve(equations, vars) - attempts to solve `equations` for the variables `vars`. */
	public final static IBuiltInSymbol NSolve = initFinalSymbol("NSolve", ID.NSolve);

	/** NakagamiDistribution(m, o) - returns a Nakagami distribution. */
	public final static IBuiltInSymbol NakagamiDistribution = initFinalSymbol("NakagamiDistribution",
			ID.NakagamiDistribution);

	/***/
	public final static IBuiltInSymbol Names = initFinalSymbol("Names", ID.Names);

	/**
	 * Nand(arg1, arg2, ...)' - Logical NAND function. It evaluates its arguments in order, giving `True` immediately if
	 * any of them are `False`, and `False` if they are all `True`.
	 */
	public final static IBuiltInSymbol Nand = initFinalSymbol("Nand", ID.Nand);

	/***/
	public final static IBuiltInSymbol Nearest = initFinalSymbol("Nearest", ID.Nearest);

	/** Negative(x) - returns `True` if `x` is a negative real number. */
	public final static IBuiltInSymbol Negative = initFinalSymbol("Negative", ID.Negative);

	/***/
	public final static IBuiltInSymbol NegativeDegreeLexicographic = initFinalSymbol("NegativeDegreeLexicographic",
			ID.NegativeDegreeLexicographic);

	/***/
	public final static IBuiltInSymbol NegativeDegreeReverseLexicographic = S
			.initFinalSymbol("NegativeDegreeReverseLexicographic", ID.NegativeDegreeReverseLexicographic);

	/***/
	public final static IBuiltInSymbol NegativeLexicographic = initFinalSymbol("NegativeLexicographic",
			ID.NegativeLexicographic);

	/** Nest(f, expr, n) - starting with `expr`, iteratively applies `f` `n` times and returns the final result. */
	public final static IBuiltInSymbol Nest = initFinalSymbol("Nest", ID.Nest);

	/**
	 * NestList(f, expr, n) - starting with `expr`, iteratively applies `f` `n` times and returns a list of all
	 * intermediate results.
	 */
	public final static IBuiltInSymbol NestList = initFinalSymbol("NestList", ID.NestList);

	/**
	 * NestWhile(f, expr, test) - applies a function `f` repeatedly on an expression `expr`, until applying `test` on
	 * the result no longer yields `True`.
	 */
	public final static IBuiltInSymbol NestWhile = initFinalSymbol("NestWhile", ID.NestWhile);

	/**
	 * NestWhileList(f, expr, test) - applies a function `f` repeatedly on an expression `expr`, until applying `test`
	 * on the result no longer yields `True`. It returns a list of all intermediate results.
	 */
	public final static IBuiltInSymbol NestWhileList = initFinalSymbol("NestWhileList", ID.NestWhileList);

	/** NextPrime(n) - gives the next prime after `n`. */
	public final static IBuiltInSymbol NextPrime = initFinalSymbol("NextPrime", ID.NextPrime);

	/***/
	public final static IBuiltInSymbol NonCommutativeMultiply = initFinalSymbol("NonCommutativeMultiply",
			ID.NonCommutativeMultiply);

	/** NonNegative(x) - returns `True` if `x` is a positive real number or zero. */
	public final static IBuiltInSymbol NonNegative = initFinalSymbol("NonNegative", ID.NonNegative);

	/** NonPositive(x) - returns `True` if `x` is a negative real number or zero. */
	public final static IBuiltInSymbol NonPositive = initFinalSymbol("NonPositive", ID.NonPositive);

	/** None - is a possible value for `Span` and `Quiet`. */
	public final static IBuiltInSymbol None = initFinalSymbol("None", ID.None);

	/**
	 * NoneTrue({expr1, expr2, ...}, test) - returns `True` if no application of `test` to `expr1, expr2, ...` evaluates
	 * to `True`.
	 */
	public final static IBuiltInSymbol NoneTrue = initFinalSymbol("NoneTrue", ID.NoneTrue);

	/***/
	public final static IBuiltInSymbol Nonexistent = initFinalSymbol("Nonexistent", ID.Nonexistent);

	/**
	 * Nor(arg1, arg2, ...)' - Logical NOR function. It evaluates its arguments in order, giving `False` immediately if
	 * any of them are `True`, and `True` if they are all `False`.
	 */
	public final static IBuiltInSymbol Nor = initFinalSymbol("Nor", ID.Nor);

	/** Norm(m, l) - computes the `l`-norm of matrix `m` (currently only works for vectors!). */
	public final static IBuiltInSymbol Norm = initFinalSymbol("Norm", ID.Norm);

	/** Normal(expr) - converts a special Symja expression `expr` into a standard expression. */
	public final static IBuiltInSymbol Normal = initFinalSymbol("Normal", ID.Normal);

	/** NormalDistribution(m, s) - returns the normal distribution of mean `m` and sigma `s`. */
	public final static IBuiltInSymbol NormalDistribution = initFinalSymbol("NormalDistribution",
			ID.NormalDistribution);

	/** Normalize(v) - calculates the normalized vector `v` as `v/Norm(v)`. */
	public final static IBuiltInSymbol Normalize = initFinalSymbol("Normalize", ID.Normalize);

	/**
	 * Not(expr) - Logical Not function (negation). Returns `True` if the statement is `False`. Returns `False` if the
	 * `expr` is `True`
	 */
	public final static IBuiltInSymbol Not = initFinalSymbol("Not", ID.Not);

	/***/
	public final static IBuiltInSymbol NotApplicable = initFinalSymbol("NotApplicable", ID.NotApplicable);

	/***/
	public final static IBuiltInSymbol NotAvailable = initFinalSymbol("NotAvailable", ID.NotAvailable);

	/***/
	public final static IBuiltInSymbol NotElement = initFinalSymbol("NotElement", ID.NotElement);

	/***/
	public final static IBuiltInSymbol NotListQ = initFinalSymbol("NotListQ", ID.NotListQ);

	/***/
	public final static IBuiltInSymbol Nothing = initFinalSymbol("Nothing", ID.Nothing);

	/***/
	public final static IBuiltInSymbol Now = initFinalSymbol("Now", ID.Now);

	/** Null - is the implicit result of expressions that do not yield a result. */
	public final static IBuiltInSymbol Null = initFinalSymbol("Null", ID.Null);

	/** NullSpace(matrix) - returns a list of vectors that span the nullspace of the `matrix`. */
	public final static IBuiltInSymbol NullSpace = initFinalSymbol("NullSpace", ID.NullSpace);

	/***/
	public final static IBuiltInSymbol Number = initFinalSymbol("Number", ID.Number);

	/***/
	public final static IBuiltInSymbol NumberFieldRootsOfUnity = initFinalSymbol("NumberFieldRootsOfUnity",
			ID.NumberFieldRootsOfUnity);

	/** NumberQ(expr) - returns `True` if `expr` is an explicit number, and `False` otherwise. */
	public final static IBuiltInSymbol NumberQ = initFinalSymbol("NumberQ", ID.NumberQ);

	/***/
	public final static IBuiltInSymbol NumberString = initFinalSymbol("NumberString", ID.NumberString);

	/** Numerator(expr) - gives the numerator in `expr`. Numerator collects expressions with non negative exponents. */
	public final static IBuiltInSymbol Numerator = initFinalSymbol("Numerator", ID.Numerator);

	/***/
	public final static IBuiltInSymbol NumericFunction = initFinalSymbol("NumericFunction", ID.NumericFunction);

	/** NumericQ(expr) - returns `True` if `expr` is an explicit numeric expression, and `False` otherwise. */
	public final static IBuiltInSymbol NumericQ = initFinalSymbol("NumericQ", ID.NumericQ);

	/***/
	public final static IBuiltInSymbol NuttallWindow = initFinalSymbol("NuttallWindow", ID.NuttallWindow);

	/***/
	public final static IBuiltInSymbol O = initFinalSymbol("O", ID.O);

	/** OddQ(x) - returns `True` if `x` is odd, and `False` otherwise. */
	public final static IBuiltInSymbol OddQ = initFinalSymbol("OddQ", ID.OddQ);

	/** Off( ) - switch off the interactive trace. */
	public final static IBuiltInSymbol Off = initFinalSymbol("Off", ID.Off);

	/** On( ) - switch on the interactive trace. The output is printed in the defined `out` stream. */
	public final static IBuiltInSymbol On = initFinalSymbol("On", ID.On);

	/**
	 * OneIdentity - is an attribute specifying that `f(x)` should be treated as equivalent to `x` in pattern matching.
	 */
	public final static IBuiltInSymbol OneIdentity = initFinalSymbol("OneIdentity", ID.OneIdentity);

	/** Operate(p, expr) - applies `p` to the head of `expr`. */
	public final static IBuiltInSymbol Operate = initFinalSymbol("Operate", ID.Operate);

	/**
	 * OptimizeExpression(function) - common subexpressions elimination for a complicated `function` by generating
	 * "dummy" variables for these subexpressions.
	 */
	public final static IBuiltInSymbol OptimizeExpression = initFinalSymbol("OptimizeExpression",
			ID.OptimizeExpression);

	/**
	 * Optional(patt, default) - is a pattern which matches `patt`, which if omitted should be replaced by `default`.
	 */
	public final static IBuiltInSymbol Optional = initFinalSymbol("Optional", ID.Optional);

	/***/
	public final static IBuiltInSymbol Options = initFinalSymbol("Options", ID.Options);

	/**
	 * Or(expr1, expr2, ...)' - `expr1 || expr2 || ...` evaluates each expression in turn, returning `True` as soon as
	 * an expression evaluates to `True`. If all expressions evaluate to `False`, `Or` returns `False`.
	 */
	public final static IBuiltInSymbol Or = initFinalSymbol("Or", ID.Or);

	/** Order(a, b) - is `0` if `a` equals `b`. Is `-1` or `1` according to canonical order of `a` and `b`. */
	public final static IBuiltInSymbol Order = initFinalSymbol("Order", ID.Order);

	/** OrderedQ({a, b}) - is `True` if `a` sorts before `b` according to canonical ordering. */
	public final static IBuiltInSymbol OrderedQ = initFinalSymbol("OrderedQ", ID.OrderedQ);

	/** Ordering(list) - calculate the permutation list of the elements in the sorted `list`. */
	public final static IBuiltInSymbol Ordering = initFinalSymbol("Ordering", ID.Ordering);

	/**
	 * Orderless - is an attribute indicating that the leaves in an expression `f(a, b, c)` can be placed in any order.
	 */
	public final static IBuiltInSymbol Orderless = initFinalSymbol("Orderless", ID.Orderless);

	/** OrthogonalMatrixQ(matrix) - returns `True`, if `matrix` is an orthogonal matrix. `False` otherwise. */
	public final static IBuiltInSymbol OrthogonalMatrixQ = initFinalSymbol("OrthogonalMatrixQ", ID.OrthogonalMatrixQ);

	/** Orthogonalize(matrix) - returns a basis for the orthogonalized set of vectors defined by `matrix`. */
	public final static IBuiltInSymbol Orthogonalize = initFinalSymbol("Orthogonalize", ID.Orthogonalize);

	/***/
	public final static IBuiltInSymbol Out = initFinalSymbol("Out", ID.Out);

	/**
	 * Outer(f, x, y) - computes a generalised outer product of `x` and `y`, using the function `f` in place of
	 * multiplication.
	 */
	public final static IBuiltInSymbol Outer = initFinalSymbol("Outer", ID.Outer);

	/***/
	public final static IBuiltInSymbol OutputForm = initFinalSymbol("OutputForm", ID.OutputForm);

	/***/
	public final static IBuiltInSymbol OutputStream = initFinalSymbol("OutputStream", ID.OutputStream);

	/** PDF(distribution, value) - returns the probability density function of `value`. */
	public final static IBuiltInSymbol PDF = initFinalSymbol("PDF", ID.PDF);

	/***/
	public final static IBuiltInSymbol Package = initFinalSymbol("Package", ID.Package);

	/** PadLeft(list, n) - pads `list` to length `n` by adding `0` on the left. */
	public final static IBuiltInSymbol PadLeft = initFinalSymbol("PadLeft", ID.PadLeft);

	/** PadRight(list, n) - pads `list` to length `n` by adding `0` on the right. */
	public final static IBuiltInSymbol PadRight = initFinalSymbol("PadRight", ID.PadRight);

	/**
	 * ParametricPlot({function1, function2}, {t, tMin, tMax}) - generate a JavaScript control for the parametric
	 * expressions `function1`, `function2` in the `t` range `{t, tMin, tMax}`.
	 */
	public final static IBuiltInSymbol ParametricPlot = initFinalSymbol("ParametricPlot", ID.ParametricPlot);

	/** Part(expr, i) - returns part `i` of `expr`. */
	public final static IBuiltInSymbol Part = initFinalSymbol("Part", ID.Part);

	/** Partition(list, n) - partitions `list` into sublists of length `n`. */
	public final static IBuiltInSymbol Partition = initFinalSymbol("Partition", ID.Partition);

	/** PartitionsP(n) - gives the number of unrestricted partitions of the integer `n`. */
	public final static IBuiltInSymbol PartitionsP = initFinalSymbol("PartitionsP", ID.PartitionsP);

	/** PartitionsQ(n) - gives the number of partitions of the integer `n` into distinct parts */
	public final static IBuiltInSymbol PartitionsQ = initFinalSymbol("PartitionsQ", ID.PartitionsQ);

	/***/
	public final static IBuiltInSymbol ParzenWindow = initFinalSymbol("ParzenWindow", ID.ParzenWindow);

	/***/
	public final static IBuiltInSymbol Pattern = initFinalSymbol("Pattern", ID.Pattern);

	/***/
	public final static IBuiltInSymbol PatternOrder = initFinalSymbol("PatternOrder", ID.PatternOrder);

	/**
	 * PatternTest(pattern, test) - constrains `pattern` to match `expr` only if the evaluation of `test(expr)` yields
	 * `True`.
	 */
	public final static IBuiltInSymbol PatternTest = initFinalSymbol("PatternTest", ID.PatternTest);

	/***/
	public final static IBuiltInSymbol PearsonChiSquareTest = initFinalSymbol("PearsonChiSquareTest",
			ID.PearsonChiSquareTest);

	/**
	 * PerfectNumber(n) - returns the `n`th perfect number. In number theory, a perfect number is a positive integer
	 * that is equal to the sum of its proper
	 */
	public final static IBuiltInSymbol PerfectNumber = initFinalSymbol("PerfectNumber", ID.PerfectNumber);

	/**
	 * PerfectNumberQ(n) - returns `True` if `n` is a perfect number. In number theory, a perfect number is a positive
	 * integer that is equal to the sum of its proper
	 */
	public final static IBuiltInSymbol PerfectNumberQ = initFinalSymbol("PerfectNumberQ", ID.PerfectNumberQ);

	/** Permutations(list) - gives all possible orderings of the items in `list`. */
	public final static IBuiltInSymbol Permutations = initFinalSymbol("Permutations", ID.Permutations);

	/** Pi - is the constant `Pi`. */
	public final static IBuiltInSymbol Pi = initFinalSymbol("Pi", ID.Pi);

	/***/
	public final static IBuiltInSymbol PieChart = initFinalSymbol("PieChart", ID.PieChart);

	/** Piecewise({{expr1, cond1}, ...}) - represents a piecewise function. */
	public final static IBuiltInSymbol Piecewise = initFinalSymbol("Piecewise", ID.Piecewise);

	/***/
	public final static IBuiltInSymbol PiecewiseExpand = initFinalSymbol("PiecewiseExpand", ID.PiecewiseExpand);

	/**
	 * Plot(function, {x, xMin, xMax}, PlotRange->{yMin,yMax}) - generate a JavaScript control for the expression
	 * `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
	 */
	public final static IBuiltInSymbol Plot = initFinalSymbol("Plot", ID.Plot);

	/**
	 * Plot3D(function, {x, xMin, xMax}, {y,yMin,yMax}) - generate a JavaScript control for the expression `function` in
	 * the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
	 */
	public final static IBuiltInSymbol Plot3D = initFinalSymbol("Plot3D", ID.Plot3D);

	/***/
	public final static IBuiltInSymbol PlotRange = initFinalSymbol("PlotRange", ID.PlotRange);

	/** Plus(a, b, ...) - represents the sum of the terms `a, b, ...`. */
	public final static IBuiltInSymbol Plus = initFinalSymbol("Plus", ID.Plus);

	/** Pochhammer(a, n) - returns the pochhammer symbol for a rational number `a` and an integer number `n`. */
	public final static IBuiltInSymbol Pochhammer = initFinalSymbol("Pochhammer", ID.Pochhammer);

	/***/
	public final static IBuiltInSymbol Point = initFinalSymbol("Point", ID.Point);

	/** PoissonDistribution(m) - returns a Poisson distribution. */
	public final static IBuiltInSymbol PoissonDistribution = initFinalSymbol("PoissonDistribution",
			ID.PoissonDistribution);

	/**
	 * PolarPlot(function, {t, tMin, tMax}) - generate a JavaScript control for the polar plot expressions `function` in
	 * the `t` range `{t, tMin, tMax}`.
	 */
	public final static IBuiltInSymbol PolarPlot = initFinalSymbol("PolarPlot", ID.PolarPlot);

	/**
	 * PolyGamma(value) - return the digamma function of the `value`. The digamma function is defined as the logarithmic
	 * derivative of the gamma function.
	 */
	public final static IBuiltInSymbol PolyGamma = initFinalSymbol("PolyGamma", ID.PolyGamma);

	/***/
	public final static IBuiltInSymbol PolyLog = initFinalSymbol("PolyLog", ID.PolyLog);

	/***/
	public final static IBuiltInSymbol Polygon = initFinalSymbol("Polygon", ID.Polygon);

	/**
	 * PolynomialExtendedGCD(p, q, x) - returns the extended GCD ('greatest common divisor') of the univariate
	 * polynomials `p` and `q`.
	 */
	public final static IBuiltInSymbol PolynomialExtendedGCD = initFinalSymbol("PolynomialExtendedGCD",
			ID.PolynomialExtendedGCD);

	/** PolynomialGCD(p, q) - returns the GCD ('greatest common divisor') of the polynomials `p` and `q`. */
	public final static IBuiltInSymbol PolynomialGCD = initFinalSymbol("PolynomialGCD", ID.PolynomialGCD);

	/** PolynomialLCM(p, q) - returns the LCM ('least common multiple') of the polynomials `p` and `q`. */
	public final static IBuiltInSymbol PolynomialLCM = initFinalSymbol("PolynomialLCM", ID.PolynomialLCM);

	/**
	 * PolynomialQ(p, x) - return `True` if `p` is a polynomial for the variable `x`. Return `False` in all other cases.
	 */
	public final static IBuiltInSymbol PolynomialQ = initFinalSymbol("PolynomialQ", ID.PolynomialQ);

	/**
	 * PolynomialQuotient(p, q, x) - returns the polynomial quotient of the polynomials `p` and `q` for the variable
	 * `x`.
	 */
	public final static IBuiltInSymbol PolynomialQuotient = initFinalSymbol("PolynomialQuotient",
			ID.PolynomialQuotient);

	/**
	 * PolynomialQuotientRemainder(p, q, x) - returns a list with the polynomial quotient and remainder of the
	 * polynomials `p` and `q` for the variable `x`.
	 */
	public final static IBuiltInSymbol PolynomialQuotientRemainder = initFinalSymbol("PolynomialQuotientRemainder",
			ID.PolynomialQuotientRemainder);

	/**
	 * PolynomialQuotient(p, q, x) - returns the polynomial remainder of the polynomials `p` and `q` for the variable
	 * `x`.
	 */
	public final static IBuiltInSymbol PolynomialRemainder = initFinalSymbol("PolynomialRemainder",
			ID.PolynomialRemainder);

	/** Position(expr, patt) - returns the list of positions for which `expr` matches `patt`. */
	public final static IBuiltInSymbol Position = initFinalSymbol("Position", ID.Position);

	/** Positive(x) - returns `True` if `x` is a positive real number. */
	public final static IBuiltInSymbol Positive = initFinalSymbol("Positive", ID.Positive);

	/** PossibleZeroQ(expr) - maps a (possible) zero `expr` to `True` and returns `False` otherwise. */
	public final static IBuiltInSymbol PossibleZeroQ = initFinalSymbol("PossibleZeroQ", ID.PossibleZeroQ);

	/***/
	public final static IBuiltInSymbol Postefix = initFinalSymbol("Postefix", ID.Postefix);

	/** Power(a, b) - represents `a` raised to the power of `b`. */
	public final static IBuiltInSymbol Power = initFinalSymbol("Power", ID.Power);

	/** PowerExpand(expr) - expands out powers of the form `(x^y)^z` and `(x*y)^z` in `expr`. */
	public final static IBuiltInSymbol PowerExpand = initFinalSymbol("PowerExpand", ID.PowerExpand);

	/** PowerMod(x, y, m) - computes `x^y` modulo `m`. */
	public final static IBuiltInSymbol PowerMod = initFinalSymbol("PowerMod", ID.PowerMod);

	/** PreDecrement(x) - decrements `x` by `1`, returning the new value of `x`. */
	public final static IBuiltInSymbol PreDecrement = initFinalSymbol("PreDecrement", ID.PreDecrement);

	/** PreIncrement(x) - increments `x` by `1`, returning the new value of `x`. */
	public final static IBuiltInSymbol PreIncrement = initFinalSymbol("PreIncrement", ID.PreIncrement);

	/***/
	public final static IBuiltInSymbol Precision = initFinalSymbol("Precision", ID.Precision);

	/***/
	public final static IBuiltInSymbol PrecisionGoal = initFinalSymbol("PrecisionGoal", ID.PrecisionGoal);

	/***/
	public final static IBuiltInSymbol Prefix = initFinalSymbol("Prefix", ID.Prefix);

	/** Prepend(expr, item) - returns `expr` with `item` prepended to its leaves. */
	public final static IBuiltInSymbol Prepend = initFinalSymbol("Prepend", ID.Prepend);

	/** PrependTo(s, item) - prepend `item` to value of `s` and sets `s` to the result. */
	public final static IBuiltInSymbol PrependTo = initFinalSymbol("PrependTo", ID.PrependTo);

	/** Prime(n) - returns the `n`th prime number. */
	public final static IBuiltInSymbol Prime = initFinalSymbol("Prime", ID.Prime);

	/** PrimeOmega(n) - returns the sum of the exponents of the prime factorization of `n`. */
	public final static IBuiltInSymbol PrimeOmega = initFinalSymbol("PrimeOmega", ID.PrimeOmega);

	/** PrimePi(x) - gives the number of primes less than or equal to `x`. */
	public final static IBuiltInSymbol PrimePi = initFinalSymbol("PrimePi", ID.PrimePi);

	/** PrimePowerQ(n) - returns `True` if `n` is a power of a prime number. */
	public final static IBuiltInSymbol PrimePowerQ = initFinalSymbol("PrimePowerQ", ID.PrimePowerQ);

	/** PrimeQ(n) - returns `True` if `n` is a integer prime number. */
	public final static IBuiltInSymbol PrimeQ = initFinalSymbol("PrimeQ", ID.PrimeQ);

	/***/
	public final static IBuiltInSymbol Primes = initFinalSymbol("Primes", ID.Primes);

	/***/
	public final static IBuiltInSymbol PrimitiveRoot = initFinalSymbol("PrimitiveRoot", ID.PrimitiveRoot);

	/** PrimitiveRootList(n) - returns the list of the primitive roots of `n`. */
	public final static IBuiltInSymbol PrimitiveRootList = initFinalSymbol("PrimitiveRootList", ID.PrimitiveRootList);

	/***/
	public final static IBuiltInSymbol Print = initFinalSymbol("Print", ID.Print);

	/**
	 * Probability(pure-function, data-set) - returns the probability of the `pure-function` for the given `data-set`.
	 */
	public final static IBuiltInSymbol Probability = initFinalSymbol("Probability", ID.Probability);

	/**
	 * Product(expr, {i, imin, imax}) - evaluates the discrete product of `expr` with `i` ranging from `imin` to `imax`.
	 */
	public final static IBuiltInSymbol Product = initFinalSymbol("Product", ID.Product);

	/** ProductLog(z) - returns the value of the Lambert W function at `z`. */
	public final static IBuiltInSymbol ProductLog = initFinalSymbol("ProductLog", ID.ProductLog);

	/** Projection(vector1, vector2) - Find the orthogonal projection of `vector1` onto another `vector2`. */
	public final static IBuiltInSymbol Projection = initFinalSymbol("Projection", ID.Projection);

	/***/
	public final static IBuiltInSymbol Protect = initFinalSymbol("Protect", ID.Protect);

	/***/
	public final static IBuiltInSymbol Protected = initFinalSymbol("Protected", ID.Protected);

	/**
	 * PseudoInverse(matrix) - computes the Moore-Penrose pseudoinverse of the `matrix`. If `matrix` is invertible, the
	 * pseudoinverse equals the inverse.
	 */
	public final static IBuiltInSymbol PseudoInverse = initFinalSymbol("PseudoInverse", ID.PseudoInverse);

	/***/
	public final static IBuiltInSymbol Put = initFinalSymbol("Put", ID.Put);

	/**
	 * QRDecomposition(A) - computes the QR decomposition of the matrix `A`. The QR decomposition is a decomposition of
	 * a matrix `A` into a product `A = Q.R` of an unitary matrix `Q` and an upper triangular matrix `R`.
	 */
	public final static IBuiltInSymbol QRDecomposition = initFinalSymbol("QRDecomposition", ID.QRDecomposition);

	/** Quantile(list, q) - returns the `q`-Quantile of `list`. */
	public final static IBuiltInSymbol Quantile = initFinalSymbol("Quantile", ID.Quantile);

	/** Quantity(value, unit) - returns the quantity for `value` and `unit` */
	public final static IBuiltInSymbol Quantity = initFinalSymbol("Quantity", ID.Quantity);

	/***/
	public final static IBuiltInSymbol QuantityDistribution = initFinalSymbol("QuantityDistribution",
			ID.QuantityDistribution);

	/** QuantityMagnitude(quantity) - returns the value of the `quantity` */
	public final static IBuiltInSymbol QuantityMagnitude = initFinalSymbol("QuantityMagnitude", ID.QuantityMagnitude);

	/***/
	public final static IBuiltInSymbol QuantityQ = initFinalSymbol("QuantityQ", ID.QuantityQ);

	/** Quartiles(arg) - returns a list of the `1/4`, `1/2` and `3/4` quantile of `arg`. */
	public final static IBuiltInSymbol Quartiles = initFinalSymbol("Quartiles", ID.Quartiles);

	/** Quiet(expr) - evaluates `expr` in "quiet" mode (i.e. no warning messages are shown during evaluation). */
	public final static IBuiltInSymbol Quiet = initFinalSymbol("Quiet", ID.Quiet);

	/***/
	public final static IBuiltInSymbol Quit = initFinalSymbol("Quit", ID.Quit);

	/** Quotient(m, n) - computes the integer quotient of `m` and `n`. */
	public final static IBuiltInSymbol Quotient = initFinalSymbol("Quotient", ID.Quotient);

	/** QuotientRemainder(m, n) - computes a list of the quotient and remainder from division of `m` and `n`. */
	public final static IBuiltInSymbol QuotientRemainder = initFinalSymbol("QuotientRemainder", ID.QuotientRemainder);

	/***/
	public final static IBuiltInSymbol Ramp = initFinalSymbol("Ramp", ID.Ramp);

	/** RandomChoice({arg1, arg2, arg3,...}) - chooses a random `arg` from the list. */
	public final static IBuiltInSymbol RandomChoice = initFinalSymbol("RandomChoice", ID.RandomChoice);

	public final static IBuiltInSymbol RandomComplex = initFinalSymbol("RandomComplex", ID.RandomComplex);

	/** RandomInteger(n) - create a random integer number between `0` and `n`. */
	public final static IBuiltInSymbol RandomInteger = initFinalSymbol("RandomInteger", ID.RandomInteger);

	/** RandomPrime(n) - create a random prime integer number between `2` and `n`. */
	public final static IBuiltInSymbol RandomPrime = initFinalSymbol("RandomPrime", ID.RandomPrime);

	/** RandomReal() - create a random number between `0.0` and `1.0`. */
	public final static IBuiltInSymbol RandomReal = initFinalSymbol("RandomReal", ID.RandomReal);

	/** RandomSample(<function>) - create a random sample for the arguments of the `function`. */
	public final static IBuiltInSymbol RandomSample = initFinalSymbol("RandomSample", ID.RandomSample);

	/***/
	public final static IBuiltInSymbol RandomVariate = initFinalSymbol("RandomVariate", ID.RandomVariate);

	/** Range(n) - returns a list of integers from `1` to `n`. */
	public final static IBuiltInSymbol Range = initFinalSymbol("Range", ID.Range);

	/** Rational - is the head of rational numbers. */
	public final static IBuiltInSymbol Rational = initFinalSymbol("Rational", ID.Rational);

	/**
	 * Rationalize(expression) - convert numerical real or imaginary parts in (sub-)expressions into rational numbers.
	 */
	public final static IBuiltInSymbol Rationalize = initFinalSymbol("Rationalize", ID.Rationalize);

	/***/
	public final static IBuiltInSymbol Rationals = initFinalSymbol("Rationals", ID.Rationals);

	/** Re(z) - returns the real component of the complex number `z`. */
	public final static IBuiltInSymbol Re = initFinalSymbol("Re", ID.Re);

	/***/
	public final static IBuiltInSymbol ReadProtected = initFinalSymbol("ReadProtected", ID.ReadProtected);

	/** Real - is the head of real (floating point) numbers. */
	public final static IBuiltInSymbol Real = initFinalSymbol("Real", ID.Real);

	public final static IBuiltInSymbol RealDigits = initFinalSymbol("RealDigits", ID.RealDigits);

	/** RealNumberQ(expr) - returns `True` if `expr` is an explicit number with no imaginary component. */
	public final static IBuiltInSymbol RealNumberQ = initFinalSymbol("RealNumberQ", ID.RealNumberQ);

	/** Reals - is the set of real numbers. */
	public final static IBuiltInSymbol Reals = initFinalSymbol("Reals", ID.Reals);

	/**
	 * Reap(expr) - gives the result of evaluating `expr`, together with all values sown during this evaluation. Values
	 * sown with different tags are given in different lists.
	 */
	public final static IBuiltInSymbol Reap = initFinalSymbol("Reap", ID.Reap);

	/***/
	public final static IBuiltInSymbol Rectangle = initFinalSymbol("Rectangle", ID.Rectangle);

	/***/
	public final static IBuiltInSymbol Reduce = initFinalSymbol("Reduce", ID.Reduce);

	/** Refine(expression, assumptions) - evaluate the `expression` for the given `assumptions`. */
	public final static IBuiltInSymbol Refine = initFinalSymbol("Refine", ID.Refine);

	/***/
	public final static IBuiltInSymbol RegularExpression = initFinalSymbol("RegularExpression", ID.RegularExpression);

	/***/
	public final static IBuiltInSymbol Remove = initFinalSymbol("Remove", ID.Remove);

	/***/
	public final static IBuiltInSymbol Repeated = initFinalSymbol("Repeated", ID.Repeated);

	/***/
	public final static IBuiltInSymbol RepeatedNull = initFinalSymbol("RepeatedNull", ID.RepeatedNull);

	/**
	 * Replace(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr` with the
	 * right-hand-side `rhs`.
	 */
	public final static IBuiltInSymbol Replace = initFinalSymbol("Replace", ID.Replace);

	/** ReplaceAll(expr, i -> new) - replaces all `i` in `expr` with `new`. */
	public final static IBuiltInSymbol ReplaceAll = initFinalSymbol("ReplaceAll", ID.ReplaceAll);

	/**
	 * ReplaceList(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr` with the
	 * right-hand-side `rhs`.
	 */
	public final static IBuiltInSymbol ReplaceList = initFinalSymbol("ReplaceList", ID.ReplaceList);

	/** ReplacePart(expr, i -> new) - replaces part `i` in `expr` with `new`. */
	public final static IBuiltInSymbol ReplacePart = initFinalSymbol("ReplacePart", ID.ReplacePart);

	/**
	 * ReplaceRepeated(expr, lhs -> rhs) - repeatedly applies the rule `lhs -> rhs` to `expr` until the result no longer
	 * changes.
	 */
	public final static IBuiltInSymbol ReplaceRepeated = initFinalSymbol("ReplaceRepeated", ID.ReplaceRepeated);

	/** Rescale(list) - returns `Rescale(list,{Min(list), Max(list)})`. */
	public final static IBuiltInSymbol Rescale = initFinalSymbol("Rescale", ID.Rescale);

	/** Rest(expr) - returns `expr` with the first element removed. */
	public final static IBuiltInSymbol Rest = initFinalSymbol("Rest", ID.Rest);

	/**
	 * Resultant(polynomial1, polynomial2, var) - computes the resultant of the polynomials `polynomial1` and
	 * `polynomial2` with respect to the variable `var`.
	 */
	public final static IBuiltInSymbol Resultant = initFinalSymbol("Resultant", ID.Resultant);

	/** Return(expr) - aborts a function call and returns `expr`. */
	public final static IBuiltInSymbol Return = initFinalSymbol("Return", ID.Return);

	/** Reverse(list) - reverse the elements of the `list`. */
	public final static IBuiltInSymbol Reverse = initFinalSymbol("Reverse", ID.Reverse);

	/** Riffle(list1, list2) - insert elements of `list2` between the elements of `list1`. */
	public final static IBuiltInSymbol Riffle = initFinalSymbol("Riffle", ID.Riffle);

	/***/
	public final static IBuiltInSymbol Right = initFinalSymbol("Right", ID.Right);

	/**
	 * RogersTanimotoDissimilarity(u, v) - returns the Rogers-Tanimoto dissimilarity between the two boolean 1-D lists
	 * `u` and `v`, which is defined as `R / (c_tt + c_ff + R)` where n is `len(u)`, `c_ij` is the number of occurrences
	 * of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * (c_tf + c_ft)`.
	 */
	public final static IBuiltInSymbol RogersTanimotoDissimilarity = initFinalSymbol("RogersTanimotoDissimilarity",
			ID.RogersTanimotoDissimilarity);

	/***/
	public final static IBuiltInSymbol RomanNumeral = initFinalSymbol("RomanNumeral", ID.RomanNumeral);

	/***/
	public final static IBuiltInSymbol Root = initFinalSymbol("Root", ID.Root);

	/***/
	public final static IBuiltInSymbol RootIntervals = initFinalSymbol("RootIntervals", ID.RootIntervals);

	/***/
	public final static IBuiltInSymbol RootOf = initFinalSymbol("RootOf", ID.RootOf);

	/**
	 * Roots(polynomial-equation, var) - determine the roots of a univariate polynomial equation with respect to the
	 * variable `var`.
	 */
	public final static IBuiltInSymbol Roots = initFinalSymbol("Roots", ID.Roots);

	/** RotateLeft(list) - rotates the items of `list` by one item to the left. */
	public final static IBuiltInSymbol RotateLeft = initFinalSymbol("RotateLeft", ID.RotateLeft);

	/** RotateRight(list) - rotates the items of `list` by one item to the right. */
	public final static IBuiltInSymbol RotateRight = initFinalSymbol("RotateRight", ID.RotateRight);

	/** RotationMatrix(theta) - yields a rotation matrix for the angle `theta`. */
	public final static IBuiltInSymbol RotationMatrix = initFinalSymbol("RotationMatrix", ID.RotationMatrix);

	/** Round(expr) - round a given `expr` to nearest integer. */
	public final static IBuiltInSymbol Round = initFinalSymbol("Round", ID.Round);

	/***/
	public final static IBuiltInSymbol Row = initFinalSymbol("Row", ID.Row);

	/** RowReduce(matrix) - returns the reduced row-echelon form of `matrix`. */
	public final static IBuiltInSymbol RowReduce = initFinalSymbol("RowReduce", ID.RowReduce);

	/** Rule(x, y) - represents a rule replacing `x` with `y`. */
	public final static IBuiltInSymbol Rule = initFinalSymbol("Rule", ID.Rule);

	/** RuleDelayed(x, y) - represents a rule replacing `x` with `y`, with `y` held unevaluated. */
	public final static IBuiltInSymbol RuleDelayed = initFinalSymbol("RuleDelayed", ID.RuleDelayed);

	/**
	 * RussellRaoDissimilarity(u, v) - returns the Russell-Rao dissimilarity between the two boolean 1-D lists `u` and
	 * `v`, which is defined as `(n - c_tt) / c_tt` where `n` is `len(u)` and `c_ij` is the number of occurrences of
	 * `u(k)=i` and `v(k)=j` for `k<n`.
	 */
	public final static IBuiltInSymbol RussellRaoDissimilarity = initFinalSymbol("RussellRaoDissimilarity",
			ID.RussellRaoDissimilarity);

	/** SameQ(x, y) - returns `True` if `x` and `y` are structurally identical. */
	public final static IBuiltInSymbol SameQ = initFinalSymbol("SameQ", ID.SameQ);

	/***/
	public final static IBuiltInSymbol SameTest = initFinalSymbol("SameTest", ID.SameTest);

	/**
	 * SatisfiabilityCount(boolean-expr) - test whether the `boolean-expr` is satisfiable by a combination of boolean
	 * `False` and `True` values for the variables of the boolean expression and return the number of possible
	 * combinations.
	 */
	public final static IBuiltInSymbol SatisfiabilityCount = initFinalSymbol("SatisfiabilityCount",
			ID.SatisfiabilityCount);

	/**
	 * SatisfiabilityInstances(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable by a
	 * combination of boolean `False` and `True` values for the `list-of-variables` and return exactly one instance of
	 * `True, False` combinations if possible.
	 */
	public final static IBuiltInSymbol SatisfiabilityInstances = initFinalSymbol("SatisfiabilityInstances",
			ID.SatisfiabilityInstances);

	/**
	 * SatisfiableQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable by a combination
	 * of boolean `False` and `True` values for the `list-of-variables`.
	 */
	public final static IBuiltInSymbol SatisfiableQ = initFinalSymbol("SatisfiableQ", ID.SatisfiableQ);

	/***/
	public final static IBuiltInSymbol Scaled = initFinalSymbol("Scaled", ID.Scaled);

	/** Scan(f, expr) - applies `f` to each element of `expr` and returns 'Null'. */
	public final static IBuiltInSymbol Scan = initFinalSymbol("Scan", ID.Scan);

	/** Sec(z) - returns the secant of `z`. */
	public final static IBuiltInSymbol Sec = initFinalSymbol("Sec", ID.Sec);

	/** Sech(z) - returns the hyperbolic secant of `z`. */
	public final static IBuiltInSymbol Sech = initFinalSymbol("Sech", ID.Sech);

	/***/
	public final static IBuiltInSymbol Second = initFinalSymbol("Second", ID.Second);

	/** Select({e1, e2, ...}, f) - returns a list of the elements `ei` for which `f(ei)` returns `True`. */
	public final static IBuiltInSymbol Select = initFinalSymbol("Select", ID.Select);

	/***/
	public final static IBuiltInSymbol SelectFirst = initFinalSymbol("SelectFirst", ID.SelectFirst);

	/**
	 * SemanticImport("path-to-filename") - if the file system is enabled, import the data from CSV files and do a
	 * semantic interpretation of the columns.
	 */
	public final static IBuiltInSymbol SemanticImport = initFinalSymbol("SemanticImport", ID.SemanticImport);

	/**
	 * SemanticImportString("string-content") - import the data from a content string in CSV format and do a semantic
	 * interpretation of the columns.
	 */
	public final static IBuiltInSymbol SemanticImportString = initFinalSymbol("SemanticImportString",
			ID.SemanticImportString);

	/***/
	public final static IBuiltInSymbol Sequence = initFinalSymbol("Sequence", ID.Sequence);

	/***/
	public final static IBuiltInSymbol SequenceHold = initFinalSymbol("SequenceHold", ID.SequenceHold);

	/** Series(expr, {x, x0, n}) - create a power series of `expr` up to order `(x- x0)^n` at the point `x = x0` */
	public final static IBuiltInSymbol Series = initFinalSymbol("Series", ID.Series);

	/** SeriesCoefficient(expr, {x, x0, n}) - get the coefficient of `(x- x0)^n` at the point `x = x0` */
	public final static IBuiltInSymbol SeriesCoefficient = initFinalSymbol("SeriesCoefficient", ID.SeriesCoefficient);

	/**
	 * SeriesData(x, x0, {coeff0, coeff1, coeff2,...}, nMin, nMax, denominator}) - internal structure of a power series
	 * at the point `x = x0` the `coeff`-i are coefficients of the power series.
	 */
	public final static IBuiltInSymbol SeriesData = initFinalSymbol("SeriesData", ID.SeriesData);

	/** Set(expr, value) - evaluates `value` and assigns it to `expr`. */
	public final static IBuiltInSymbol Set = initFinalSymbol("Set", ID.Set);

	/** SetAttributes(symbol, attrib) - adds `attrib` to `symbol`'s attributes. */
	public final static IBuiltInSymbol SetAttributes = initFinalSymbol("SetAttributes", ID.SetAttributes);

	/** SetDelayed(expr, value) - assigns `value` to `expr`, without evaluating `value`. */
	public final static IBuiltInSymbol SetDelayed = initFinalSymbol("SetDelayed", ID.SetDelayed);

	/***/
	public final static IBuiltInSymbol Share = initFinalSymbol("Share", ID.Share);

	/***/
	public final static IBuiltInSymbol Show = initFinalSymbol("Show", ID.Show);

	/** Sign(x) - gives `-1`, `0` or `1` depending on whether `x` is negative, zero or positive. */
	public final static IBuiltInSymbol Sign = initFinalSymbol("Sign", ID.Sign);

	/***/
	public final static IBuiltInSymbol SignCmp = initFinalSymbol("SignCmp", ID.SignCmp);

	/** Simplify(expr) - simplifies `expr` */
	public final static IBuiltInSymbol Simplify = initFinalSymbol("Simplify", ID.Simplify);

	/** Sin(expr) - returns the sine of `expr` (measured in radians). */
	public final static IBuiltInSymbol Sin = initFinalSymbol("Sin", ID.Sin);

	/***/
	public final static IBuiltInSymbol SinIntegral = initFinalSymbol("SinIntegral", ID.SinIntegral);

	/** Sinc(expr) - the sinc function `Sin(expr)/expr` for `expr != 0`. `Sinc(0)` returns `1`. */
	public final static IBuiltInSymbol Sinc = initFinalSymbol("Sinc", ID.Sinc);

	/** SingularValueDecomposition(matrix) - calculates the singular value decomposition for the `matrix`. */
	public final static IBuiltInSymbol SingularValueDecomposition = initFinalSymbol("SingularValueDecomposition",
			ID.SingularValueDecomposition);

	/** Sinh(z) - returns the hyperbolic sine of `z`. */
	public final static IBuiltInSymbol Sinh = initFinalSymbol("Sinh", ID.Sinh);

	/***/
	public final static IBuiltInSymbol SinhIntegral = initFinalSymbol("SinhIntegral", ID.SinhIntegral);

	/**
	 * Skewness(list) - gives Pearson's moment coefficient of skewness for `list` (a measure for estimating the symmetry
	 * of a distribution).
	 */
	public final static IBuiltInSymbol Skewness = initFinalSymbol("Skewness", ID.Skewness);

	/** # - is a short-hand for `#1`. */
	public final static IBuiltInSymbol Slot = initFinalSymbol("Slot", ID.Slot);

	/** ## - is the sequence of arguments supplied to a pure function. */
	public final static IBuiltInSymbol SlotSequence = initFinalSymbol("SlotSequence", ID.SlotSequence);

	/**
	 * SokalSneathDissimilarity(u, v) - returns the Sokal-Sneath dissimilarity between the two boolean 1-D lists `u` and
	 * `v`, which is defined as `R / (c_tt + R)` where n is `len(u)`, `c_ij` is the number of occurrences of `u(k)=i`
	 * and `v(k)=j` for `k<n`, and `R = 2 * (c_tf + c_ft)`.
	 */
	public final static IBuiltInSymbol SokalSneathDissimilarity = initFinalSymbol("SokalSneathDissimilarity",
			ID.SokalSneathDissimilarity);

	/** Solve(equations, vars) - attempts to solve `equations` for the variables `vars`. */
	public final static IBuiltInSymbol Solve = initFinalSymbol("Solve", ID.Solve);

	/** Sort(list) - sorts `list` (or the leaves of any other expression) according to canonical ordering. */
	public final static IBuiltInSymbol Sort = initFinalSymbol("Sort", ID.Sort);

	/**
	 * SortBy(list, f) - sorts `list` (or the leaves of any other expression) according to canonical ordering of the
	 * keys that are extracted from the `list`'s elements using `f`. Chunks of leaves that appear the same under `f` are
	 * sorted according to their natural order (without applying `f`).
	 */
	public final static IBuiltInSymbol SortBy = initFinalSymbol("SortBy", ID.SortBy);

	/** Sow(expr) - sends the value `expr` to the innermost `Reap`. */
	public final static IBuiltInSymbol Sow = initFinalSymbol("Sow", ID.Sow);

	/** Span - is the head of span ranges like `1;;3`. */
	public final static IBuiltInSymbol Span = initFinalSymbol("Span", ID.Span);

	/** SphericalBesselJ(n, z) - spherical Bessel function `J(n, x)`. */
	public final static IBuiltInSymbol SphericalBesselJ = initFinalSymbol("SphericalBesselJ", ID.SphericalBesselJ);

	/** SphericalBesselY(n, z) - spherical Bessel function `Y(n, x)`. */
	public final static IBuiltInSymbol SphericalBesselY = initFinalSymbol("SphericalBesselY", ID.SphericalBesselY);

	/***/
	public final static IBuiltInSymbol SphericalHankelH1 = initFinalSymbol("SphericalHankelH1", ID.SphericalHankelH1);

	/***/
	public final static IBuiltInSymbol SphericalHankelH2 = initFinalSymbol("SphericalHankelH2", ID.SphericalHankelH2);

	/** Split(list) - splits `list` into collections of consecutive identical elements. */
	public final static IBuiltInSymbol Split = initFinalSymbol("Split", ID.Split);

	/**
	 * SplitBy(list, f) - splits `list` into collections of consecutive elements that give the same result when `f` is
	 * applied.
	 */
	public final static IBuiltInSymbol SplitBy = initFinalSymbol("SplitBy", ID.SplitBy);

	/** Sqrt(expr) - returns the square root of `expr`. */
	public final static IBuiltInSymbol Sqrt = initFinalSymbol("Sqrt", ID.Sqrt);

	/**
	 * SquareFreeQ(n) - returns `True` if `n` is a square free integer number or a square free univariate polynomial.
	 */
	public final static IBuiltInSymbol SquareFreeQ = initFinalSymbol("SquareFreeQ", ID.SquareFreeQ);

	/** SquareMatrixQ(m) - returns `True` if `m` is a square matrix. */
	public final static IBuiltInSymbol SquareMatrixQ = initFinalSymbol("SquareMatrixQ", ID.SquareMatrixQ);

	/** SquaredEuclideanDistance(u, v) - returns squared the euclidean distance between `u$` and `v`. */
	public final static IBuiltInSymbol SquaredEuclideanDistance = initFinalSymbol("SquaredEuclideanDistance",
			ID.SquaredEuclideanDistance);

	/**
	 * StandardDeviation(list) - computes the standard deviation of `list`. `list` may consist of numerical values or
	 * symbols. Numerical values may be real or complex.
	 */
	public final static IBuiltInSymbol StandardDeviation = initFinalSymbol("StandardDeviation", ID.StandardDeviation);

	/***/
	public final static IBuiltInSymbol StandardForm = initFinalSymbol("StandardForm", ID.StandardForm);

	/***/
	public final static IBuiltInSymbol Standardize = initFinalSymbol("Standardize", ID.Standardize);

	/***/
	public final static IBuiltInSymbol StieltjesGamma = initFinalSymbol("StieltjesGamma", ID.StieltjesGamma);

	/** StirlingS1(n, k) - returns the Stirling numbers of the first kind. */
	public final static IBuiltInSymbol StirlingS1 = initFinalSymbol("StirlingS1", ID.StirlingS1);

	/**
	 * StirlingS2(n, k) - returns the Stirling numbers of the second kind. `StirlingS2(n,k)` is the number of ways of
	 * partitioning an `n`-element set into `k` non-empty subsets.
	 */
	public final static IBuiltInSymbol StirlingS2 = initFinalSymbol("StirlingS2", ID.StirlingS2);

	/***/
	public final static IBuiltInSymbol Strict = initFinalSymbol("Strict", ID.Strict);

	/***/
	public final static IBuiltInSymbol String = initFinalSymbol("String", ID.String);

	/**
	 * StringCases(str, {"p1", "p2",...}) - return a list of matches for `"p1", "p2",...` list of strings in the string
	 * `str`.
	 */
	public final static IBuiltInSymbol StringCases = initFinalSymbol("StringCases", ID.StringCases);

	/**
	 * StringContainsQ(str1, str2) - return a list of matches for `"p1", "p2",...` list of strings in the string `str`.
	 */
	public final static IBuiltInSymbol StringContainsQ = initFinalSymbol("StringContainsQ", ID.StringContainsQ);

	/***/
	public final static IBuiltInSymbol StringDrop = initFinalSymbol("StringDrop", ID.StringDrop);

	/***/
	public final static IBuiltInSymbol StringExpression = initFinalSymbol("StringExpression", ID.StringExpression);

	/** StringJoin(str1, str2, ... strN) - concatenate the strings `str1, str2, ... strN` into one string. */
	public final static IBuiltInSymbol StringJoin = initFinalSymbol("StringJoin", ID.StringJoin);

	/** StringLength(str) - return the length of the string `str`. */
	public final static IBuiltInSymbol StringLength = initFinalSymbol("StringLength", ID.StringLength);

	/**
	 * StringMatchQ(str, RegularExpression(pattern-string)) - check if the regular expression `pattern-string` matches
	 * the string `str`.
	 */
	public final static IBuiltInSymbol StringMatchQ = initFinalSymbol("StringMatchQ", ID.StringMatchQ);

	/** StringPart(str, pos) - return the character at position `pos` from the `str` string expression. */
	public final static IBuiltInSymbol StringPart = initFinalSymbol("StringPart", ID.StringPart);

	/** StringQ(x) - is `True` if `x` is a string object, or `False` otherwise. */
	public final static IBuiltInSymbol StringQ = initFinalSymbol("StringQ", ID.StringQ);

	/** StringReplace(str, fromStr -> toStr) - replaces the `fromStr` with the `toStr` in the `str` string. */
	public final static IBuiltInSymbol StringReplace = initFinalSymbol("StringReplace", ID.StringReplace);

	/***/
	public final static IBuiltInSymbol StringRiffle = initFinalSymbol("StringRiffle", ID.StringRiffle);

	/** StringSplit(str) - split the string `str` by whitespaces into a list of strings. */
	public final static IBuiltInSymbol StringSplit = initFinalSymbol("StringSplit", ID.StringSplit);

	/***/
	public final static IBuiltInSymbol StringTake = initFinalSymbol("StringTake", ID.StringTake);

	/***/
	public final static IBuiltInSymbol Structure = initFinalSymbol("Structure", ID.Structure);

	/** StruveH(n, z) - returns the Struve function `H_n(z)`. */
	public final static IBuiltInSymbol StruveH = initFinalSymbol("StruveH", ID.StruveH);

	/** StruveL(n, z) - returns the modified Struve function `L_n(z)`. */
	public final static IBuiltInSymbol StruveL = initFinalSymbol("StruveL", ID.StruveL);

	/** StudentTDistribution(v) - returns a Student's t-distribution. */
	public final static IBuiltInSymbol StudentTDistribution = initFinalSymbol("StudentTDistribution",
			ID.StudentTDistribution);

	/** Subdivide(n) - returns a list with `n+1` entries obtained by subdividing the range `0` to `1`. */
	public final static IBuiltInSymbol Subdivide = initFinalSymbol("Subdivide", ID.Subdivide);

	/** Subfactorial(n) - returns the subfactorial number of the integer `n` */
	public final static IBuiltInSymbol Subfactorial = initFinalSymbol("Subfactorial", ID.Subfactorial);

	/***/
	public final static IBuiltInSymbol Subscript = initFinalSymbol("Subscript", ID.Subscript);

	/***/
	public final static IBuiltInSymbol SubsetQ = initFinalSymbol("SubsetQ", ID.SubsetQ);

	/** Subsets(list) - finds a list of all possible subsets of `list`. */
	public final static IBuiltInSymbol Subsets = initFinalSymbol("Subsets", ID.Subsets);

	/***/
	public final static IBuiltInSymbol Subsuperscript = initFinalSymbol("Subsuperscript", ID.Subsuperscript);

	/** Subtract(a, b) - represents the subtraction of `b` from `a`. */
	public final static IBuiltInSymbol Subtract = initFinalSymbol("Subtract", ID.Subtract);

	/** SubtractFrom(x, dx) - is equivalent to `x = x - dx`. */
	public final static IBuiltInSymbol SubtractFrom = initFinalSymbol("SubtractFrom", ID.SubtractFrom);

	/** Sum(expr, {i, imin, imax}) - evaluates the discrete sum of `expr` with `i` ranging from `imin` to `imax`. */
	public final static IBuiltInSymbol Sum = initFinalSymbol("Sum", ID.Sum);

	public final static IBuiltInSymbol Summary = initFinalSymbol("Summary", ID.Summary);

	/***/
	public final static IBuiltInSymbol Superscript = initFinalSymbol("Superscript", ID.Superscript);

	/** Surd(expr, n) - returns the `n`-th root of `expr`. If the result is defined, it's a real value. */
	public final static IBuiltInSymbol Surd = initFinalSymbol("Surd", ID.Surd);

	/***/
	public final static IBuiltInSymbol SurfaceGraphics = initFinalSymbol("SurfaceGraphics", ID.SurfaceGraphics);

	/** SurvivalFunction(dist, x) - returns the survival function for the distribution `dist` evaluated at `x`. */
	public final static IBuiltInSymbol SurvivalFunction = initFinalSymbol("SurvivalFunction", ID.SurvivalFunction);

	/**
	 * Switch(expr, pattern1, value1, pattern2, value2, ...) - yields the first `value` for which `expr` matches the
	 * corresponding pattern.
	 */
	public final static IBuiltInSymbol Switch = initFinalSymbol("Switch", ID.Switch);

	/** Symbol - is the head of symbols. */
	public final static IBuiltInSymbol Symbol = initFinalSymbol("Symbol", ID.Symbol);

	/** SymbolName(s) - returns the name of the symbol `s` (without any leading context name). */
	public final static IBuiltInSymbol SymbolName = initFinalSymbol("SymbolName", ID.SymbolName);

	/** SymbolQ(x) - is `True` if `x` is a symbol, or `False` otherwise. */
	public final static IBuiltInSymbol SymbolQ = initFinalSymbol("SymbolQ", ID.SymbolQ);

	/***/
	public final static IBuiltInSymbol Symmetric = initFinalSymbol("Symmetric", ID.Symmetric);

	/** SymmetricMatrixQ(m) - returns `True` if `m` is a symmetric matrix. */
	public final static IBuiltInSymbol SymmetricMatrixQ = initFinalSymbol("SymmetricMatrixQ", ID.SymmetricMatrixQ);

	/***/
	public final static IBuiltInSymbol SyntaxLength = initFinalSymbol("SyntaxLength", ID.SyntaxLength);

	/** SyntaxQ(str) - is `True` if the given `str` is a string which has the correct syntax. */
	public final static IBuiltInSymbol SyntaxQ = initFinalSymbol("SyntaxQ", ID.SyntaxQ);

	/** SystemDialogInput("FileOpen") - if the file system is enabled, open a file chooser dialog box. */
	public final static IBuiltInSymbol SystemDialogInput = initFinalSymbol("SystemDialogInput", ID.SystemDialogInput);

	/** Table(expr, {i, n}) - evaluates `expr` with `i` ranging from `1` to `n`, returning a list of the results. */
	public final static IBuiltInSymbol Table = initFinalSymbol("Table", ID.Table);

	/***/
	public final static IBuiltInSymbol TableForm = initFinalSymbol("TableForm", ID.TableForm);

	/***/
	public final static IBuiltInSymbol TagSet = initFinalSymbol("TagSet", ID.TagSet);

	/***/
	public final static IBuiltInSymbol TagSetDelayed = initFinalSymbol("TagSetDelayed", ID.TagSetDelayed);

	/** Take(expr, n) - returns `expr` with all but the first `n` leaves removed. */
	public final static IBuiltInSymbol Take = initFinalSymbol("Take", ID.Take);

	/***/
	public final static IBuiltInSymbol TakeLargest = initFinalSymbol("TakeLargest", ID.TakeLargest);

	/***/
	public final static IBuiltInSymbol TakeLargestBy = initFinalSymbol("TakeLargestBy", ID.TakeLargestBy);

	/***/
	public final static IBuiltInSymbol Tally = initFinalSymbol("Tally", ID.Tally);

	/** Tan(expr) - returns the tangent of `expr` (measured in radians). */
	public final static IBuiltInSymbol Tan = initFinalSymbol("Tan", ID.Tan);

	/** Tanh(z) - returns the hyperbolic tangent of `z`. */
	public final static IBuiltInSymbol Tanh = initFinalSymbol("Tanh", ID.Tanh);

	/**
	 * TautologyQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable by all combinations
	 * of boolean `False` and `True` values for the `list-of-variables`.
	 */
	public final static IBuiltInSymbol TautologyQ = initFinalSymbol("TautologyQ", ID.TautologyQ);

	/***/
	public final static IBuiltInSymbol Taylor = initFinalSymbol("Taylor", ID.Taylor);

	/** TeXForm(expr) - returns the TeX form of the evaluated `expr`. */
	public final static IBuiltInSymbol TeXForm = initFinalSymbol("TeXForm", ID.TeXForm);

	/***/
	public final static IBuiltInSymbol TensorDimensions = initFinalSymbol("TensorDimensions", ID.TensorDimensions);

	/***/
	public final static IBuiltInSymbol TensorProduct = initFinalSymbol("TensorProduct", ID.TensorProduct);

	/***/
	public final static IBuiltInSymbol TensorRank = initFinalSymbol("TensorRank", ID.TensorRank);

	/***/
	public final static IBuiltInSymbol TensorSymmetry = initFinalSymbol("TensorSymmetry", ID.TensorSymmetry);

	/***/
	public final static IBuiltInSymbol TextCell = initFinalSymbol("TextCell", ID.TextCell);

	/***/
	public final static IBuiltInSymbol TextString = initFinalSymbol("TextString", ID.TextString);

	/** Thread(f(args) - threads `f` over any lists that appear in `args`. */
	public final static IBuiltInSymbol Thread = initFinalSymbol("Thread", ID.Thread);

	/** Through(p(f)[x]) - gives `p(f(x))`. */
	public final static IBuiltInSymbol Through = initFinalSymbol("Through", ID.Through);

	/***/
	public final static IBuiltInSymbol Throw = initFinalSymbol("Throw", ID.Throw);

	/***/
	public final static IBuiltInSymbol TimeConstrained = initFinalSymbol("TimeConstrained", ID.TimeConstrained);

	/***/
	public final static IBuiltInSymbol TimeObject = initFinalSymbol("TimeObject", ID.TimeObject);

	/** TimeValue(p, i, n) - returns a time value calculation. */
	public final static IBuiltInSymbol TimeValue = initFinalSymbol("TimeValue", ID.TimeValue);

	/** Times(a, b, ...) - represents the product of the terms `a, b, ...`. */
	public final static IBuiltInSymbol Times = initFinalSymbol("Times", ID.Times);

	/** TimesBy(x, dx) - is equivalent to `x = x * dx`. */
	public final static IBuiltInSymbol TimesBy = initFinalSymbol("TimesBy", ID.TimesBy);

	/**
	 * Timing(x) - returns a list with the first entry containing the evaluation time of `x` and the second entry is the
	 * evaluation result of `x`.
	 */
	public final static IBuiltInSymbol Timing = initFinalSymbol("Timing", ID.Timing);

	/***/
	public final static IBuiltInSymbol Today = initFinalSymbol("Today", ID.Today);

	/** ToCharacterCode(string) - converts `string` into a list of corresponding integer character codes. */
	public final static IBuiltInSymbol ToCharacterCode = initFinalSymbol("ToCharacterCode", ID.ToCharacterCode);

	/** ToExpression("string", form) - converts the `string` given in `form` into an expression. */
	public final static IBuiltInSymbol ToExpression = initFinalSymbol("ToExpression", ID.ToExpression);

	/** ToPolarCoordinates({x, y}) - return the polar coordinates for the cartesian coordinates `{x, y}`. */
	public final static IBuiltInSymbol ToPolarCoordinates = initFinalSymbol("ToPolarCoordinates",
			ID.ToPolarCoordinates);

	/***/
	public final static IBuiltInSymbol ToRadicals = initFinalSymbol("ToRadicals", ID.ToRadicals);

	/** ToString(expr) - converts `expr` into a string. */
	public final static IBuiltInSymbol ToString = initFinalSymbol("ToString", ID.ToString);

	/***/
	public final static IBuiltInSymbol ToUnicode = initFinalSymbol("ToUnicode", ID.ToUnicode);

	/** ToeplitzMatrix(n) - gives a toeplitz matrix with the dimension `n`. */
	public final static IBuiltInSymbol ToeplitzMatrix = initFinalSymbol("ToeplitzMatrix", ID.ToeplitzMatrix);

	/** Together(expr) - writes sums of fractions in `expr` together. */
	public final static IBuiltInSymbol Together = initFinalSymbol("Together", ID.Together);

	/***/
	public final static IBuiltInSymbol TooLarge = initFinalSymbol("TooLarge", ID.TooLarge);

	/***/
	public final static IBuiltInSymbol Top = initFinalSymbol("Top", ID.Top);

	/** Total(list) - adds all values in `list`. */
	public final static IBuiltInSymbol Total = initFinalSymbol("Total", ID.Total);

	/** Tr(matrix) - computes the trace of the `matrix`. */
	public final static IBuiltInSymbol Tr = initFinalSymbol("Tr", ID.Tr);

	/** Trace(expr) - return the evaluation steps which are used to get the result. */
	public final static IBuiltInSymbol Trace = initFinalSymbol("Trace", ID.Trace);

	/***/
	public final static IBuiltInSymbol TraceForm = initFinalSymbol("TraceForm", ID.TraceForm);

	/***/
	public final static IBuiltInSymbol TraditionalForm = initFinalSymbol("TraditionalForm", ID.TraditionalForm);

	/** Transpose(m) - transposes rows and columns in the matrix `m`. */
	public final static IBuiltInSymbol Transpose = initFinalSymbol("Transpose", ID.Transpose);

	/** TreeForm(expr) - create a tree visualization from the given expression `expr`. */
	public final static IBuiltInSymbol TreeForm = initFinalSymbol("TreeForm", ID.TreeForm);

	/***/
	public final static IBuiltInSymbol Trig = initFinalSymbol("Trig", ID.Trig);

	/** TrigExpand(expr) - expands out trigonometric expressions in `expr`. */
	public final static IBuiltInSymbol TrigExpand = initFinalSymbol("TrigExpand", ID.TrigExpand);

	/**
	 * TrigReduce(expr) - rewrites products and powers of trigonometric functions in `expr` in terms of trigonometric
	 * functions with combined arguments.
	 */
	public final static IBuiltInSymbol TrigReduce = initFinalSymbol("TrigReduce", ID.TrigReduce);

	/** TrigToExp(expr) - converts trigonometric functions in `expr` to exponentials. */
	public final static IBuiltInSymbol TrigToExp = initFinalSymbol("TrigToExp", ID.TrigToExp);

	/** True - the constant `True` represents the boolean value **true ***/
	public final static IBuiltInSymbol True = initFinalSymbol("True", ID.True);

	/** TrueQ(expr) - returns `True` if and only if `expr` is `True`. */
	public final static IBuiltInSymbol TrueQ = initFinalSymbol("TrueQ", ID.TrueQ);

	/***/
	public final static IBuiltInSymbol TukeyWindow = initFinalSymbol("TukeyWindow", ID.TukeyWindow);

	/** Tuples(list, n) - creates a list of all `n`-tuples of elements in `list`. */
	public final static IBuiltInSymbol Tuples = initFinalSymbol("Tuples", ID.Tuples);

	/***/
	public final static IBuiltInSymbol TwoWayRule = initFinalSymbol("TwoWayRule", ID.TwoWayRule);

	/***/
	public final static IBuiltInSymbol Undefined = initFinalSymbol("Undefined", ID.Undefined);

	/***/
	public final static IBuiltInSymbol Underoverscript = initFinalSymbol("Underoverscript", ID.Underoverscript);

	/***/
	public final static IBuiltInSymbol UndirectedEdge = initFinalSymbol("UndirectedEdge", ID.UndirectedEdge);

	/**
	 * Unequal(x, y) - yields `False` if `x` and `y` are known to be equal, or `True` if `x` and `y` are known to be
	 * unequal.
	 */
	public final static IBuiltInSymbol Unequal = initFinalSymbol("Unequal", ID.Unequal);

	/***/
	public final static IBuiltInSymbol Unevaluated = initFinalSymbol("Unevaluated", ID.Unevaluated);

	/** UniformDistribution({min, max}) - returns a uniform distribution. */
	public final static IBuiltInSymbol UniformDistribution = initFinalSymbol("UniformDistribution",
			ID.UniformDistribution);

	/** Union(set1, set2) - get the union set from `set1` and `set2`. */
	public final static IBuiltInSymbol Union = initFinalSymbol("Union", ID.Union);

	/** Unique(expr) - create a unique symbol of the form `expr$...`. */
	public final static IBuiltInSymbol Unique = initFinalSymbol("Unique", ID.Unique);

	/** UnitConvert(quantity) - convert the `quantity` to the base unit */
	public final static IBuiltInSymbol UnitConvert = initFinalSymbol("UnitConvert", ID.UnitConvert);

	/**
	 * UnitStep(expr) - returns `0`, if `expr` is less than `0` and returns `1`, if `expr` is greater equal than `0`.
	 */
	public final static IBuiltInSymbol UnitStep = initFinalSymbol("UnitStep", ID.UnitStep);

	/** UnitVector(position) - returns a unit vector with element `1` at the given `position`. */
	public final static IBuiltInSymbol UnitVector = initFinalSymbol("UnitVector", ID.UnitVector);

	/***/
	public final static IBuiltInSymbol UnitaryMatrixQ = initFinalSymbol("UnitaryMatrixQ", ID.UnitaryMatrixQ);

	/** Unitize(expr) - maps a non-zero `expr` to `1`, and a zero `expr` to `0`. */
	public final static IBuiltInSymbol Unitize = initFinalSymbol("Unitize", ID.Unitize);

	/***/
	public final static IBuiltInSymbol Unknown = initFinalSymbol("Unknown", ID.Unknown);

	/***/
	public final static IBuiltInSymbol Unprotect = initFinalSymbol("Unprotect", ID.Unprotect);

	/** UnsameQ(x, y) - returns `True` if `x` and `y` are not structurally identical. */
	public final static IBuiltInSymbol UnsameQ = initFinalSymbol("UnsameQ", ID.UnsameQ);

	/** Unset(expr) - removes any definitions belonging to the left-hand-side `expr`. */
	public final static IBuiltInSymbol Unset = initFinalSymbol("Unset", ID.Unset);

	/***/
	public final static IBuiltInSymbol UpSet = initFinalSymbol("UpSet", ID.UpSet);

	/***/
	public final static IBuiltInSymbol UpSetDelayed = initFinalSymbol("UpSetDelayed", ID.UpSetDelayed);

	/** UpperCaseQ(str) - is `True` if the given `str` is a string which only contains upper case characters. */
	public final static IBuiltInSymbol UpperCaseQ = initFinalSymbol("UpperCaseQ", ID.UpperCaseQ);

	/** UpperTriangularize(matrix) - create a upper triangular matrix from the given `matrix`. */
	public final static IBuiltInSymbol UpperTriangularize = initFinalSymbol("UpperTriangularize",
			ID.UpperTriangularize);

	/** ValueQ(expr) - returns `True` if and only if `expr` is defined. */
	public final static IBuiltInSymbol ValueQ = initFinalSymbol("ValueQ", ID.ValueQ);

	/** Values(association) - return a list of values of the `association`. */
	public final static IBuiltInSymbol Values = initFinalSymbol("Values", ID.Values);

	/** VandermondeMatrix(n) - gives the Vandermonde matrix with `n` rows and columns. */
	public final static IBuiltInSymbol VandermondeMatrix = initFinalSymbol("VandermondeMatrix", ID.VandermondeMatrix);

	/***/
	public final static IBuiltInSymbol Variable = initFinalSymbol("Variable", ID.Variable);

	/** Variables[expr] - gives a list of the variables that appear in the polynomial `expr`. */
	public final static IBuiltInSymbol Variables = initFinalSymbol("Variables", ID.Variables);

	/**
	 * Variance(list) - computes the variance of `list`. `list` may consist of numerical values or symbols. Numerical
	 * values may be real or complex.
	 */
	public final static IBuiltInSymbol Variance = initFinalSymbol("Variance", ID.Variance);

	/** VectorAngle(u, v) - gives the angles between vectors `u` and `v` */
	public final static IBuiltInSymbol VectorAngle = initFinalSymbol("VectorAngle", ID.VectorAngle);

	/** VectorQ(v) - returns `True` if `v` is a list of elements which are not themselves lists. */
	public final static IBuiltInSymbol VectorQ = initFinalSymbol("VectorQ", ID.VectorQ);

	/**
	 * VertexEccentricity(graph, vertex) - compute the eccentricity of `vertex` in the `graph`. It's the length of the
	 * longest shortest path from the `vertex` to every other vertex in the `graph`.
	 */
	public final static IBuiltInSymbol VertexEccentricity = initFinalSymbol("VertexEccentricity",
			ID.VertexEccentricity);

	/** VertexList(graph) - convert the `graph` into a list of vertices. */
	public final static IBuiltInSymbol VertexList = initFinalSymbol("VertexList", ID.VertexList);

	/** VertexQ(graph, vertex) - test if `vertex` is a vertex in the `graph` object. */
	public final static IBuiltInSymbol VertexQ = initFinalSymbol("VertexQ", ID.VertexQ);

	/***/
	public final static IBuiltInSymbol ViewPoint = initFinalSymbol("ViewPoint", ID.ViewPoint);

	/** WeibullDistribution(a, b) - returns a Weibull distribution. */
	public final static IBuiltInSymbol WeibullDistribution = initFinalSymbol("WeibullDistribution",
			ID.WeibullDistribution);

	/***/
	public final static IBuiltInSymbol WeierstrassHalfPeriods = initFinalSymbol("WeierstrassHalfPeriods",
			ID.WeierstrassHalfPeriods);

	/***/
	public final static IBuiltInSymbol WeierstrassInvariants = initFinalSymbol("WeierstrassInvariants",
			ID.WeierstrassInvariants);

	/***/
	public final static IBuiltInSymbol WeierstrassP = initFinalSymbol("WeierstrassP", ID.WeierstrassP);

	/***/
	public final static IBuiltInSymbol WeierstrassPPrime = initFinalSymbol("WeierstrassPPrime", ID.WeierstrassPPrime);

	/***/
	public final static IBuiltInSymbol WeightedAdjacencyMatrix = initFinalSymbol("WeightedAdjacencyMatrix",
			ID.WeightedAdjacencyMatrix);

	/***/
	public final static IBuiltInSymbol WeightedData = initFinalSymbol("WeightedData", ID.WeightedData);

	/**
	 * Which(cond1, expr1, cond2, expr2, ...) - yields `expr1` if `cond1` evaluates to `True`, `expr2` if `cond2`
	 * evaluates to `True`, etc.
	 */
	public final static IBuiltInSymbol Which = initFinalSymbol("Which", ID.Which);

	/** While(test, body) - evaluates `body` as long as test evaluates to `True`. */
	public final static IBuiltInSymbol While = initFinalSymbol("While", ID.While);

	/***/
	public final static IBuiltInSymbol White = initFinalSymbol("White", ID.White);

	/***/
	public final static IBuiltInSymbol WhittakerM = initFinalSymbol("WhittakerM", ID.WhittakerM);

	/***/
	public final static IBuiltInSymbol WhittakerW = initFinalSymbol("WhittakerW", ID.WhittakerW);

	/***/
	public final static IBuiltInSymbol Whitespace = initFinalSymbol("Whitespace", ID.Whitespace);

	/**
	 * With({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables` by replacing the
	 * local variables in `expr`.
	 */
	public final static IBuiltInSymbol With = initFinalSymbol("With", ID.With);

	/***/
	public final static IBuiltInSymbol WriteString = initFinalSymbol("WriteString", ID.WriteString);

	/**
	 * Xor(arg1, arg2, ...) - Logical XOR (exclusive OR) function. Returns `True` if an odd number of the arguments are
	 * `True` and the rest are `False`. Returns `False` if an even number of the arguments are `True` and the rest are
	 * `False`.
	 */
	public final static IBuiltInSymbol Xor = initFinalSymbol("Xor", ID.Xor);

	/**
	 * YuleDissimilarity(u, v) - returns the Yule dissimilarity between the two boolean 1-D lists `u` and `v`, which is
	 * defined as `R / (c_tt * c_ff + R / 2)` where `n` is `len(u)`, `c_ij` is the number of occurrences of `u(k)=i` and
	 * `v(k)=j` for `k<n`, and `R = 2 * c_tf * c_ft`.
	 */
	public final static IBuiltInSymbol YuleDissimilarity = initFinalSymbol("YuleDissimilarity", ID.YuleDissimilarity);

	/***/
	public final static IBuiltInSymbol ZeroSymmetric = initFinalSymbol("ZeroSymmetric", ID.ZeroSymmetric);

	/** Zeta(z) - returns the Riemann zeta function of `z`. */
	public final static IBuiltInSymbol Zeta = initFinalSymbol("Zeta", ID.Zeta);

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
		IBuiltInSymbol temp = new BuiltInSymbol(str, ordinal);
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
		ISymbol temp = new Symbol(symbolName, org.matheclipse.core.expression.Context.DUMMY);
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
