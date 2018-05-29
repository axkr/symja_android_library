package org.matheclipse.core.expression;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;
import org.hipparchus.complex.Complex;
import org.hipparchus.fraction.BigFraction;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.builtin.AssumptionFunctions;
import org.matheclipse.core.builtin.AttributeFunctions;
import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.builtin.Combinatoric;
import org.matheclipse.core.builtin.ComputationalGeometryFunctions;
import org.matheclipse.core.builtin.ConstantDefinitions;
import org.matheclipse.core.builtin.EllipticIntegrals;
import org.matheclipse.core.builtin.ExpTrigsFunctions;
import org.matheclipse.core.builtin.FunctionDefinitions;
import org.matheclipse.core.builtin.HypergeometricFunctions;
import org.matheclipse.core.builtin.IntegerFunctions;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.builtin.ListFunctions;
import org.matheclipse.core.builtin.NumberTheory;
import org.matheclipse.core.builtin.OutputFunctions;
import org.matheclipse.core.builtin.PatternMatching;
import org.matheclipse.core.builtin.PolynomialFunctions;
import org.matheclipse.core.builtin.PredicateQ;
import org.matheclipse.core.builtin.Programming;
import org.matheclipse.core.builtin.RandomFunctions;
import org.matheclipse.core.builtin.SeriesFunctions;
import org.matheclipse.core.builtin.SpecialFunctions;
import org.matheclipse.core.builtin.StatisticsFunctions;
import org.matheclipse.core.builtin.StringFunctions;
import org.matheclipse.core.builtin.Structure;
import org.matheclipse.core.builtin.TensorFunctions;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.math.DoubleMath;

import edu.jas.kern.ComputerThreads;

/**
 * 
 * Factory for creating Symja expression objects.
 * 
 */
public class F {
	private final static IBuiltInSymbol[] BUILT_IN_SYMBOLS = new IBuiltInSymbol[ID.Zeta + 1];

	public static boolean PACKAGE_MODE = true;

	/**
	 * <p>
	 * In computing, memoization or memoisation is an optimization technique used primarily to speed up computer
	 * programs by storing the results of expensive function calls and returning the cached result when the same inputs
	 * occur again. This cache is especially useed for recursive integer functions to remember the results of the
	 * recursive call.
	 * </p>
	 * 
	 * See: <a href="https://en.wikipedia.org/wiki/Memoization">Wikipedia - Memoization</a>
	 */
	public static Cache<IAST, IExpr> REMEMBER_INTEGER_CACHE = CacheBuilder.newBuilder().maximumSize(5000).build();

	/**
	 * Set to <code>true</code> at the start of initSymbols() method
	 */
	public static volatile boolean isSystemStarted = false;

	/**
	 * Set to <code>true</code> at the end of initSymbols() method
	 */
	public static volatile boolean isSystemInitialized = false;

	/**
	 * The map for predefined strings for the {@link IExpr#internalFormString(boolean, int)} method.
	 */
	public final static Map<String, String> PREDEFINED_INTERNAL_FORM_STRINGS = new HashMap<String, String>(61);

	public final static Map<String, IPattern> PREDEFINED_PATTERN_MAP = new HashMap<String, IPattern>(61);

	public final static Map<String, IPatternSequence> PREDEFINED_PATTERNSEQUENCE_MAP = new HashMap<String, IPatternSequence>(
			61);

	public final static Map<String, ISymbol> HIDDEN_SYMBOLS_MAP = new HashMap<String, ISymbol>(197);

	public static ISymbolObserver SYMBOL_OBSERVER = new ISymbolObserver() {
		@Override
		public final boolean createPredefinedSymbol(String symbol) {
			return false;
		}

		@Override
		public void createUserSymbol(ISymbol symbol) {

		}

	};

	/**
	 * <p>
	 * The constant object <code>NIL</code> (not in list) indicates in the evaluation process that no evaluation was
	 * possible (i.e. no further definition was found to create a new expression from the existing one).
	 * </p>
	 * <p>
	 * Almost every modifying method in this class throws an <tt>UnsupportedOperationException</tt>, almost every
	 * predicate returns <code>false</code>. The main method to check if the object is valid is the
	 * <code>isPresent()</code> method. The method is similar to <code>java.util.Optional#isPresent()</code>.
	 * </p>
	 * 
	 * @see java.util.Optional#isPresent
	 */
	public final static NILPointer NIL = new NILPointer();
	public final static IBuiltInSymbol Abort = F.initFinalSymbol("Abort", ID.Abort);
	public final static IBuiltInSymbol Abs = F.initFinalSymbol("Abs", ID.Abs);
	public final static IBuiltInSymbol AbsArg = F.initFinalSymbol("AbsArg", ID.AbsArg);
	public final static IBuiltInSymbol Accumulate = F.initFinalSymbol("Accumulate", ID.Accumulate);
	public final static IBuiltInSymbol AddTo = F.initFinalSymbol("AddTo", ID.AddTo);
	public final static IBuiltInSymbol AlgebraicNumber = F.initFinalSymbol("AlgebraicNumber", ID.AlgebraicNumber);
	public final static IBuiltInSymbol Algebraics = F.initFinalSymbol("Algebraics", ID.Algebraics);
	public final static IBuiltInSymbol All = F.initFinalSymbol("All", ID.All);
	public final static IBuiltInSymbol AllTrue = F.initFinalSymbol("AllTrue", ID.AllTrue);
	public final static IBuiltInSymbol Alternatives = F.initFinalSymbol("Alternatives", ID.Alternatives);
	public final static IBuiltInSymbol And = F.initFinalSymbol("And", ID.And);
	public final static IBuiltInSymbol AngleVector = F.initFinalSymbol("AngleVector", ID.AngleVector);
	public final static IBuiltInSymbol AntihermitianMatrixQ = F.initFinalSymbol("AntihermitianMatrixQ",
			ID.AntihermitianMatrixQ);
	public final static IBuiltInSymbol AntisymmetricMatrixQ = F.initFinalSymbol("AntisymmetricMatrixQ",
			ID.AntisymmetricMatrixQ);
	public final static IBuiltInSymbol AnyTrue = F.initFinalSymbol("AnyTrue", ID.AnyTrue);
	public final static IBuiltInSymbol Apart = F.initFinalSymbol("Apart", ID.Apart);
	public final static IBuiltInSymbol AppellF1 = F.initFinalSymbol("AppellF1", ID.AppellF1);
	public final static IBuiltInSymbol Append = F.initFinalSymbol("Append", ID.Append);
	public final static IBuiltInSymbol AppendTo = F.initFinalSymbol("AppendTo", ID.AppendTo);
	public final static IBuiltInSymbol Apply = F.initFinalSymbol("Apply", ID.Apply);
	public final static IBuiltInSymbol ArcCos = F.initFinalSymbol("ArcCos", ID.ArcCos);
	public final static IBuiltInSymbol ArcCosh = F.initFinalSymbol("ArcCosh", ID.ArcCosh);
	public final static IBuiltInSymbol ArcCot = F.initFinalSymbol("ArcCot", ID.ArcCot);
	public final static IBuiltInSymbol ArcCoth = F.initFinalSymbol("ArcCoth", ID.ArcCoth);
	public final static IBuiltInSymbol ArcCsc = F.initFinalSymbol("ArcCsc", ID.ArcCsc);
	public final static IBuiltInSymbol ArcCsch = F.initFinalSymbol("ArcCsch", ID.ArcCsch);
	public final static IBuiltInSymbol ArcSec = F.initFinalSymbol("ArcSec", ID.ArcSec);
	public final static IBuiltInSymbol ArcSech = F.initFinalSymbol("ArcSech", ID.ArcSech);
	public final static IBuiltInSymbol ArcSin = F.initFinalSymbol("ArcSin", ID.ArcSin);
	public final static IBuiltInSymbol ArcSinh = F.initFinalSymbol("ArcSinh", ID.ArcSinh);
	public final static IBuiltInSymbol ArcTan = F.initFinalSymbol("ArcTan", ID.ArcTan);
	public final static IBuiltInSymbol ArcTanh = F.initFinalSymbol("ArcTanh", ID.ArcTanh);
	public final static IBuiltInSymbol Arg = F.initFinalSymbol("Arg", ID.Arg);
	public final static IBuiltInSymbol Array = F.initFinalSymbol("Array", ID.Array);
	public final static IBuiltInSymbol ArrayDepth = F.initFinalSymbol("ArrayDepth", ID.ArrayDepth);
	public final static IBuiltInSymbol ArrayPad = F.initFinalSymbol("ArrayPad", ID.ArrayPad);
	public final static IBuiltInSymbol ArrayQ = F.initFinalSymbol("ArrayQ", ID.ArrayQ);
	public final static IBuiltInSymbol Assumptions = F.initFinalSymbol("Assumptions", ID.Assumptions);
	public final static IBuiltInSymbol AtomQ = F.initFinalSymbol("AtomQ", ID.AtomQ);
	public final static IBuiltInSymbol Attributes = F.initFinalSymbol("Attributes", ID.Attributes);
	public final static IBuiltInSymbol Automatic = F.initFinalSymbol("Automatic", ID.Automatic);
	public final static IBuiltInSymbol Axes = F.initFinalSymbol("Axes", ID.Axes);
	public final static IBuiltInSymbol AxesOrigin = F.initFinalSymbol("AxesOrigin", ID.AxesOrigin);
	public final static IBuiltInSymbol AxesStyle = F.initFinalSymbol("AxesStyle", ID.AxesStyle);
	public final static IBuiltInSymbol Background = F.initFinalSymbol("Background", ID.Background);
	public final static IBuiltInSymbol Begin = F.initFinalSymbol("Begin", ID.Begin);
	public final static IBuiltInSymbol BeginPackage = F.initFinalSymbol("BeginPackage", ID.BeginPackage);
	public final static IBuiltInSymbol BellB = F.initFinalSymbol("BellB", ID.BellB);
	public final static IBuiltInSymbol BellY = F.initFinalSymbol("BellY", ID.BellY);
	public final static IBuiltInSymbol BernoulliB = F.initFinalSymbol("BernoulliB", ID.BernoulliB);
	public final static IBuiltInSymbol BernoulliDistribution = F.initFinalSymbol("BernoulliDistribution",
			ID.BernoulliDistribution);
	public final static IBuiltInSymbol BesselI = F.initFinalSymbol("BesselI", ID.BesselI);
	public final static IBuiltInSymbol BesselJ = F.initFinalSymbol("BesselJ", ID.BesselJ);
	public final static IBuiltInSymbol BesselK = F.initFinalSymbol("BesselK", ID.BesselK);
	public final static IBuiltInSymbol BesselY = F.initFinalSymbol("BesselY", ID.BesselY);
	public final static IBuiltInSymbol Beta = F.initFinalSymbol("Beta", ID.Beta);
	public final static IBuiltInSymbol BetaRegularized = F.initFinalSymbol("BetaRegularized", ID.BetaRegularized);
	public final static IBuiltInSymbol BinCounts = F.initFinalSymbol("BinCounts", ID.BinCounts);
	public final static IBuiltInSymbol Binomial = F.initFinalSymbol("Binomial", ID.Binomial);
	public final static IBuiltInSymbol BinomialDistribution = F.initFinalSymbol("BinomialDistribution",
			ID.BinomialDistribution);
	public final static IBuiltInSymbol BitLength = F.initFinalSymbol("BitLength", ID.BitLength);
	public final static IBuiltInSymbol Blank = F.initFinalSymbol("Blank", ID.Blank);
	public final static IBuiltInSymbol Block = F.initFinalSymbol("Block", ID.Block);
	public final static IBuiltInSymbol Boole = F.initFinalSymbol("Boole", ID.Boole);
	public final static IBuiltInSymbol BooleanConvert = F.initFinalSymbol("BooleanConvert", ID.BooleanConvert);
	public final static IBuiltInSymbol BooleanMinimize = F.initFinalSymbol("BooleanMinimize", ID.BooleanMinimize);
	public final static IBuiltInSymbol BooleanQ = F.initFinalSymbol("BooleanQ", ID.BooleanQ);
	public final static IBuiltInSymbol BooleanTable = F.initFinalSymbol("BooleanTable", ID.BooleanTable);
	public final static IBuiltInSymbol BooleanVariables = F.initFinalSymbol("BooleanVariables", ID.BooleanVariables);
	public final static IBuiltInSymbol Booleans = F.initFinalSymbol("Booleans", ID.Booleans);
	public final static IBuiltInSymbol BrayCurtisDistance = F.initFinalSymbol("BrayCurtisDistance",
			ID.BrayCurtisDistance);
	public final static IBuiltInSymbol Break = F.initFinalSymbol("Break", ID.Break);
	public final static IBuiltInSymbol CDF = F.initFinalSymbol("CDF", ID.CDF);
	public final static IBuiltInSymbol CanberraDistance = F.initFinalSymbol("CanberraDistance", ID.CanberraDistance);
	public final static IBuiltInSymbol Cancel = F.initFinalSymbol("Cancel", ID.Cancel);
	public final static IBuiltInSymbol CarmichaelLambda = F.initFinalSymbol("CarmichaelLambda", ID.CarmichaelLambda);
	public final static IBuiltInSymbol CartesianProduct = F.initFinalSymbol("CartesianProduct", ID.CartesianProduct);
	public final static IBuiltInSymbol Cases = F.initFinalSymbol("Cases", ID.Cases);
	public final static IBuiltInSymbol Catalan = F.initFinalSymbol("Catalan", ID.Catalan);
	public final static IBuiltInSymbol CatalanNumber = F.initFinalSymbol("CatalanNumber", ID.CatalanNumber);
	public final static IBuiltInSymbol Catch = F.initFinalSymbol("Catch", ID.Catch);
	public final static IBuiltInSymbol Catenate = F.initFinalSymbol("Catenate", ID.Catenate);
	public final static IBuiltInSymbol Ceiling = F.initFinalSymbol("Ceiling", ID.Ceiling);
	public final static IBuiltInSymbol CentralMoment = F.initFinalSymbol("CentralMoment", ID.CentralMoment);
	public final static IBuiltInSymbol CharacterEncoding = F.initFinalSymbol("CharacterEncoding", ID.CharacterEncoding);
	public final static IBuiltInSymbol CharacteristicPolynomial = F.initFinalSymbol("CharacteristicPolynomial",
			ID.CharacteristicPolynomial);
	public final static IBuiltInSymbol ChebyshevT = F.initFinalSymbol("ChebyshevT", ID.ChebyshevT);
	public final static IBuiltInSymbol ChebyshevU = F.initFinalSymbol("ChebyshevU", ID.ChebyshevU);
	public final static IBuiltInSymbol ChessboardDistance = F.initFinalSymbol("ChessboardDistance",
			ID.ChessboardDistance);
	public final static IBuiltInSymbol ChineseRemainder = F.initFinalSymbol("ChineseRemainder", ID.ChineseRemainder);
	public final static IBuiltInSymbol CholeskyDecomposition = F.initFinalSymbol("CholeskyDecomposition",
			ID.CholeskyDecomposition);
	public final static IBuiltInSymbol Chop = F.initFinalSymbol("Chop", ID.Chop);
	public final static IBuiltInSymbol CirclePoints = F.initFinalSymbol("CirclePoints", ID.CirclePoints);
	public final static IBuiltInSymbol Clear = F.initFinalSymbol("Clear", ID.Clear);
	public final static IBuiltInSymbol ClearAll = F.initFinalSymbol("ClearAll", ID.ClearAll);
	public final static IBuiltInSymbol ClearAttributes = F.initFinalSymbol("ClearAttributes", ID.ClearAttributes);
	public final static IBuiltInSymbol Clip = F.initFinalSymbol("Clip", ID.Clip);
	public final static IBuiltInSymbol Coefficient = F.initFinalSymbol("Coefficient", ID.Coefficient);
	public final static IBuiltInSymbol CoefficientList = F.initFinalSymbol("CoefficientList", ID.CoefficientList);
	public final static IBuiltInSymbol CoefficientRules = F.initFinalSymbol("CoefficientRules", ID.CoefficientRules);
	public final static IBuiltInSymbol Collect = F.initFinalSymbol("Collect", ID.Collect);
	public final static IBuiltInSymbol Colon = F.initFinalSymbol("Colon", ID.Colon);
	public final static IBuiltInSymbol Commonest = F.initFinalSymbol("Commonest", ID.Commonest);
	public final static IBuiltInSymbol Compile = F.initFinalSymbol("Compile", ID.Compile);
	public final static IBuiltInSymbol Complement = F.initFinalSymbol("Complement", ID.Complement);
	public final static IBuiltInSymbol Complex = F.initFinalSymbol("Complex", ID.Complex);
	public final static IBuiltInSymbol ComplexExpand = F.initFinalSymbol("ComplexExpand", ID.ComplexExpand);
	public final static IBuiltInSymbol ComplexInfinity = F.initFinalSymbol("ComplexInfinity", ID.ComplexInfinity);
	public final static IBuiltInSymbol Complexes = F.initFinalSymbol("Complexes", ID.Complexes);
	public final static IBuiltInSymbol ComplexityFunction = F.initFinalSymbol("ComplexityFunction",
			ID.ComplexityFunction);
	public final static IBuiltInSymbol ComposeList = F.initFinalSymbol("ComposeList", ID.ComposeList);
	public final static IBuiltInSymbol ComposeSeries = F.initFinalSymbol("ComposeSeries", ID.ComposeSeries);
	public final static IBuiltInSymbol Composition = F.initFinalSymbol("Composition", ID.Composition);
	public final static IBuiltInSymbol CompoundExpression = F.initFinalSymbol("CompoundExpression",
			ID.CompoundExpression);
	public final static IBuiltInSymbol Condition = F.initFinalSymbol("Condition", ID.Condition);
	public final static IBuiltInSymbol ConditionalExpression = F.initFinalSymbol("ConditionalExpression",
			ID.ConditionalExpression);
	public final static IBuiltInSymbol Conjugate = F.initFinalSymbol("Conjugate", ID.Conjugate);
	public final static IBuiltInSymbol ConjugateTranspose = F.initFinalSymbol("ConjugateTranspose",
			ID.ConjugateTranspose);
	public final static IBuiltInSymbol Constant = F.initFinalSymbol("Constant", ID.Constant);
	public final static IBuiltInSymbol ConstantArray = F.initFinalSymbol("ConstantArray", ID.ConstantArray);
	public final static IBuiltInSymbol Continue = F.initFinalSymbol("Continue", ID.Continue);
	public final static IBuiltInSymbol ContinuedFraction = F.initFinalSymbol("ContinuedFraction", ID.ContinuedFraction);
	public final static IBuiltInSymbol Convergents = F.initFinalSymbol("Convergents", ID.Convergents);
	public final static IBuiltInSymbol ConvexHullMesh = F.initFinalSymbol("ConvexHullMesh", ID.ConvexHullMesh);
	public final static IBuiltInSymbol CoprimeQ = F.initFinalSymbol("CoprimeQ", ID.CoprimeQ);
	public final static IBuiltInSymbol Correlation = F.initFinalSymbol("Correlation", ID.Correlation);
	public final static IBuiltInSymbol Cos = F.initFinalSymbol("Cos", ID.Cos);
	public final static IBuiltInSymbol CosIntegral = F.initFinalSymbol("CosIntegral", ID.CosIntegral);
	public final static IBuiltInSymbol Cosh = F.initFinalSymbol("Cosh", ID.Cosh);
	public final static IBuiltInSymbol CoshIntegral = F.initFinalSymbol("CoshIntegral", ID.CoshIntegral);
	public final static IBuiltInSymbol CosineDistance = F.initFinalSymbol("CosineDistance", ID.CosineDistance);
	public final static IBuiltInSymbol Cot = F.initFinalSymbol("Cot", ID.Cot);
	public final static IBuiltInSymbol Coth = F.initFinalSymbol("Coth", ID.Coth);
	public final static IBuiltInSymbol Count = F.initFinalSymbol("Count", ID.Count);
	public final static IBuiltInSymbol Covariance = F.initFinalSymbol("Covariance", ID.Covariance);
	public final static IBuiltInSymbol Cross = F.initFinalSymbol("Cross", ID.Cross);
	public final static IBuiltInSymbol Csc = F.initFinalSymbol("Csc", ID.Csc);
	public final static IBuiltInSymbol Csch = F.initFinalSymbol("Csch", ID.Csch);
	public final static IBuiltInSymbol CubeRoot = F.initFinalSymbol("CubeRoot", ID.CubeRoot);
	public final static IBuiltInSymbol Curl = F.initFinalSymbol("Curl", ID.Curl);
	public final static IBuiltInSymbol D = F.initFinalSymbol("D", ID.D);
	public final static IBuiltInSymbol DSolve = F.initFinalSymbol("DSolve", ID.DSolve);
	public final static IBuiltInSymbol Decrement = F.initFinalSymbol("Decrement", ID.Decrement);
	public final static IBuiltInSymbol Default = F.initFinalSymbol("Default", ID.Default);
	public final static IBuiltInSymbol Defer = F.initFinalSymbol("Defer", ID.Defer);
	public final static IBuiltInSymbol Definition = F.initFinalSymbol("Definition", ID.Definition);
	public final static IBuiltInSymbol Degree = F.initFinalSymbol("Degree", ID.Degree);
	public final static IBuiltInSymbol Delete = F.initFinalSymbol("Delete", ID.Delete);
	public final static IBuiltInSymbol DeleteCases = F.initFinalSymbol("DeleteCases", ID.DeleteCases);
	public final static IBuiltInSymbol DeleteDuplicates = F.initFinalSymbol("DeleteDuplicates", ID.DeleteDuplicates);
	public final static IBuiltInSymbol Denominator = F.initFinalSymbol("Denominator", ID.Denominator);
	public final static IBuiltInSymbol Depth = F.initFinalSymbol("Depth", ID.Depth);
	public final static IBuiltInSymbol Derivative = F.initFinalSymbol("Derivative", ID.Derivative);
	public final static IBuiltInSymbol DesignMatrix = F.initFinalSymbol("DesignMatrix", ID.DesignMatrix);
	public final static IBuiltInSymbol Det = F.initFinalSymbol("Det", ID.Det);
	public final static IBuiltInSymbol Diagonal = F.initFinalSymbol("Diagonal", ID.Diagonal);
	public final static IBuiltInSymbol DiagonalMatrix = F.initFinalSymbol("DiagonalMatrix", ID.DiagonalMatrix);
	public final static IBuiltInSymbol DiceDissimilarity = F.initFinalSymbol("DiceDissimilarity", ID.DiceDissimilarity);
	public final static IBuiltInSymbol DigitQ = F.initFinalSymbol("DigitQ", ID.DigitQ);
	public final static IBuiltInSymbol Dimensions = F.initFinalSymbol("Dimensions", ID.Dimensions);
	public final static IBuiltInSymbol DiracDelta = F.initFinalSymbol("DiracDelta", ID.DiracDelta);
	public final static IBuiltInSymbol DirectedInfinity = F.initFinalSymbol("DirectedInfinity", ID.DirectedInfinity);
	public final static IBuiltInSymbol Direction = F.initFinalSymbol("Direction", ID.Direction);
	public final static IBuiltInSymbol DiscreteDelta = F.initFinalSymbol("DiscreteDelta", ID.DiscreteDelta);
	public final static IBuiltInSymbol DiscreteUniformDistribution = F.initFinalSymbol("DiscreteUniformDistribution",
			ID.DiscreteUniformDistribution);
	public final static IBuiltInSymbol Discriminant = F.initFinalSymbol("Discriminant", ID.Discriminant);
	public final static IBuiltInSymbol Disputed = F.initFinalSymbol("Disputed", ID.Disputed);
	public final static IBuiltInSymbol Distribute = F.initFinalSymbol("Distribute", ID.Distribute);
	public final static IBuiltInSymbol Distributed = F.initFinalSymbol("Distributed", ID.Distributed);
	public final static IBuiltInSymbol Divergence = F.initFinalSymbol("Divergence", ID.Divergence);
	public final static IBuiltInSymbol Divide = F.initFinalSymbol("Divide", ID.Divide);
	public final static IBuiltInSymbol DivideBy = F.initFinalSymbol("DivideBy", ID.DivideBy);
	public final static IBuiltInSymbol Divisible = F.initFinalSymbol("Divisible", ID.Divisible);
	public final static IBuiltInSymbol DivisorSigma = F.initFinalSymbol("DivisorSigma", ID.DivisorSigma);
	public final static IBuiltInSymbol Divisors = F.initFinalSymbol("Divisors", ID.Divisors);
	public final static IBuiltInSymbol Do = F.initFinalSymbol("Do", ID.Do);
	public final static IBuiltInSymbol Dot = F.initFinalSymbol("Dot", ID.Dot);
	public final static IBuiltInSymbol Drop = F.initFinalSymbol("Drop", ID.Drop);
	public final static IBuiltInSymbol E = F.initFinalSymbol("E", ID.E);
	public final static IBuiltInSymbol EasterSunday = F.initFinalSymbol("EasterSunday", ID.EasterSunday);
	public final static IBuiltInSymbol Eigenvalues = F.initFinalSymbol("Eigenvalues", ID.Eigenvalues);
	public final static IBuiltInSymbol Eigenvectors = F.initFinalSymbol("Eigenvectors", ID.Eigenvectors);
	public final static IBuiltInSymbol Element = F.initFinalSymbol("Element", ID.Element);
	public final static IBuiltInSymbol ElementData = F.initFinalSymbol("ElementData", ID.ElementData);
	public final static IBuiltInSymbol Eliminate = F.initFinalSymbol("Eliminate", ID.Eliminate);
	public final static IBuiltInSymbol EllipticE = F.initFinalSymbol("EllipticE", ID.EllipticE);
	public final static IBuiltInSymbol EllipticF = F.initFinalSymbol("EllipticF", ID.EllipticF);
	public final static IBuiltInSymbol EllipticK = F.initFinalSymbol("EllipticK", ID.EllipticK);
	public final static IBuiltInSymbol EllipticPi = F.initFinalSymbol("EllipticPi", ID.EllipticPi);
	public final static IBuiltInSymbol End = F.initFinalSymbol("End", ID.End);
	public final static IBuiltInSymbol EndPackage = F.initFinalSymbol("EndPackage", ID.EndPackage);
	public final static IBuiltInSymbol Equal = F.initFinalSymbol("Equal", ID.Equal);
	public final static IBuiltInSymbol Equivalent = F.initFinalSymbol("Equivalent", ID.Equivalent);
	public final static IBuiltInSymbol Erf = F.initFinalSymbol("Erf", ID.Erf);
	public final static IBuiltInSymbol Erfc = F.initFinalSymbol("Erfc", ID.Erfc);
	public final static IBuiltInSymbol Erfi = F.initFinalSymbol("Erfi", ID.Erfi);
	public final static IBuiltInSymbol ErlangDistribution = F.initFinalSymbol("ErlangDistribution",
			ID.ErlangDistribution);
	public final static IBuiltInSymbol EuclideanDistance = F.initFinalSymbol("EuclideanDistance", ID.EuclideanDistance);
	public final static IBuiltInSymbol EulerE = F.initFinalSymbol("EulerE", ID.EulerE);
	public final static IBuiltInSymbol EulerGamma = F.initFinalSymbol("EulerGamma", ID.EulerGamma);
	public final static IBuiltInSymbol EulerPhi = F.initFinalSymbol("EulerPhi", ID.EulerPhi);
	public final static IBuiltInSymbol EvenQ = F.initFinalSymbol("EvenQ", ID.EvenQ);
	public final static IBuiltInSymbol ExactNumberQ = F.initFinalSymbol("ExactNumberQ", ID.ExactNumberQ);
	public final static IBuiltInSymbol Except = F.initFinalSymbol("Except", ID.Except);
	public final static IBuiltInSymbol Exists = F.initFinalSymbol("Exists", ID.Exists);
	public final static IBuiltInSymbol Exp = F.initFinalSymbol("Exp", ID.Exp);
	public final static IBuiltInSymbol ExpIntegralE = F.initFinalSymbol("ExpIntegralE", ID.ExpIntegralE);
	public final static IBuiltInSymbol ExpIntegralEi = F.initFinalSymbol("ExpIntegralEi", ID.ExpIntegralEi);
	public final static IBuiltInSymbol Expand = F.initFinalSymbol("Expand", ID.Expand);
	public final static IBuiltInSymbol ExpandAll = F.initFinalSymbol("ExpandAll", ID.ExpandAll);
	public final static IBuiltInSymbol Expectation = F.initFinalSymbol("Expectation", ID.Expectation);
	public final static IBuiltInSymbol Exponent = F.initFinalSymbol("Exponent", ID.Exponent);
	public final static IBuiltInSymbol ExponentialDistribution = F.initFinalSymbol("ExponentialDistribution",
			ID.ExponentialDistribution);
	public final static IBuiltInSymbol Export = F.initFinalSymbol("Export", ID.Export);
	public final static IBuiltInSymbol ExtendedGCD = F.initFinalSymbol("ExtendedGCD", ID.ExtendedGCD);
	public final static IBuiltInSymbol Extract = F.initFinalSymbol("Extract", ID.Extract);
	public final static IBuiltInSymbol Factor = F.initFinalSymbol("Factor", ID.Factor);
	public final static IBuiltInSymbol FactorInteger = F.initFinalSymbol("FactorInteger", ID.FactorInteger);
	public final static IBuiltInSymbol FactorSquareFree = F.initFinalSymbol("FactorSquareFree", ID.FactorSquareFree);
	public final static IBuiltInSymbol FactorSquareFreeList = F.initFinalSymbol("FactorSquareFreeList",
			ID.FactorSquareFreeList);
	public final static IBuiltInSymbol FactorTerms = F.initFinalSymbol("FactorTerms", ID.FactorTerms);
	public final static IBuiltInSymbol Factorial = F.initFinalSymbol("Factorial", ID.Factorial);
	public final static IBuiltInSymbol Factorial2 = F.initFinalSymbol("Factorial2", ID.Factorial2);
	public final static IBuiltInSymbol False = F.initFinalSymbol("False", ID.False);
	public final static IBuiltInSymbol Fibonacci = F.initFinalSymbol("Fibonacci", ID.Fibonacci);
	public final static IBuiltInSymbol FindInstance = F.initFinalSymbol("FindInstance", ID.FindInstance);
	public final static IBuiltInSymbol FindRoot = F.initFinalSymbol("FindRoot", ID.FindRoot);
	public final static IBuiltInSymbol First = F.initFinalSymbol("First", ID.First);
	public final static IBuiltInSymbol Fit = F.initFinalSymbol("Fit", ID.Fit);
	public final static IBuiltInSymbol FixedPoint = F.initFinalSymbol("FixedPoint", ID.FixedPoint);
	public final static IBuiltInSymbol FixedPointList = F.initFinalSymbol("FixedPointList", ID.FixedPointList);
	public final static IBuiltInSymbol Flat = F.initFinalSymbol("Flat", ID.Flat);
	public final static IBuiltInSymbol Flatten = F.initFinalSymbol("Flatten", ID.Flatten);
	public final static IBuiltInSymbol FlattenAt = F.initFinalSymbol("FlattenAt", ID.FlattenAt);
	public final static IBuiltInSymbol Floor = F.initFinalSymbol("Floor", ID.Floor);
	public final static IBuiltInSymbol Fold = F.initFinalSymbol("Fold", ID.Fold);
	public final static IBuiltInSymbol FoldList = F.initFinalSymbol("FoldList", ID.FoldList);
	public final static IBuiltInSymbol For = F.initFinalSymbol("For", ID.For);
	public final static IBuiltInSymbol ForAll = F.initFinalSymbol("ForAll", ID.ForAll);
	public final static IBuiltInSymbol FourierMatrix = F.initFinalSymbol("FourierMatrix", ID.FourierMatrix);
	public final static IBuiltInSymbol FractionalPart = F.initFinalSymbol("FractionalPart", ID.FractionalPart);
	public final static IBuiltInSymbol FrechetDistribution = F.initFinalSymbol("FrechetDistribution",
			ID.FrechetDistribution);
	public final static IBuiltInSymbol FreeQ = F.initFinalSymbol("FreeQ", ID.FreeQ);
	public final static IBuiltInSymbol FresnelC = F.initFinalSymbol("FresnelC", ID.FresnelC);
	public final static IBuiltInSymbol FresnelS = F.initFinalSymbol("FresnelS", ID.FresnelS);
	public final static IBuiltInSymbol FrobeniusNumber = F.initFinalSymbol("FrobeniusNumber", ID.FrobeniusNumber);
	public final static IBuiltInSymbol FrobeniusSolve = F.initFinalSymbol("FrobeniusSolve", ID.FrobeniusSolve);
	public final static IBuiltInSymbol FromCharacterCode = F.initFinalSymbol("FromCharacterCode", ID.FromCharacterCode);
	public final static IBuiltInSymbol FromContinuedFraction = F.initFinalSymbol("FromContinuedFraction",
			ID.FromContinuedFraction);
	public final static IBuiltInSymbol FromDigits = F.initFinalSymbol("FromDigits", ID.FromDigits);
	public final static IBuiltInSymbol FromPolarCoordinates = F.initFinalSymbol("FromPolarCoordinates",
			ID.FromPolarCoordinates);
	public final static IBuiltInSymbol FullForm = F.initFinalSymbol("FullForm", ID.FullForm);
	public final static IBuiltInSymbol FullSimplify = F.initFinalSymbol("FullSimplify", ID.FullSimplify);
	public final static IBuiltInSymbol Function = F.initFinalSymbol("Function", ID.Function);
	public final static IBuiltInSymbol GCD = F.initFinalSymbol("GCD", ID.GCD);
	public final static IBuiltInSymbol Gamma = F.initFinalSymbol("Gamma", ID.Gamma);
	public final static IBuiltInSymbol GammaDistribution = F.initFinalSymbol("GammaDistribution", ID.GammaDistribution);
	public final static IBuiltInSymbol GammaRegularized = F.initFinalSymbol("GammaRegularized", ID.GammaRegularized);
	public final static IBuiltInSymbol Gather = F.initFinalSymbol("Gather", ID.Gather);
	public final static IBuiltInSymbol GegenbauerC = F.initFinalSymbol("GegenbauerC", ID.GegenbauerC);
	public final static IBuiltInSymbol GeometricDistribution = F.initFinalSymbol("GeometricDistribution",
			ID.GeometricDistribution);
	public final static IBuiltInSymbol GeometricMean = F.initFinalSymbol("GeometricMean", ID.GeometricMean);
	public final static IBuiltInSymbol Get = F.initFinalSymbol("Get", ID.Get);
	public final static IBuiltInSymbol Glaisher = F.initFinalSymbol("Glaisher", ID.Glaisher);
	public final static IBuiltInSymbol GoldenRatio = F.initFinalSymbol("GoldenRatio", ID.GoldenRatio);
	public final static IBuiltInSymbol Graphics = F.initFinalSymbol("Graphics", ID.Graphics);
	public final static IBuiltInSymbol Graphics3D = F.initFinalSymbol("Graphics3D", ID.Graphics3D);
	public final static IBuiltInSymbol Greater = F.initFinalSymbol("Greater", ID.Greater);
	public final static IBuiltInSymbol GreaterEqual = F.initFinalSymbol("GreaterEqual", ID.GreaterEqual);
	public final static IBuiltInSymbol GroebnerBasis = F.initFinalSymbol("GroebnerBasis", ID.GroebnerBasis);
	public final static IBuiltInSymbol GumbelDistribution = F.initFinalSymbol("GumbelDistribution",
			ID.GumbelDistribution);
	public final static IBuiltInSymbol HarmonicNumber = F.initFinalSymbol("HarmonicNumber", ID.HarmonicNumber);
	public final static IBuiltInSymbol Haversine = F.initFinalSymbol("Haversine", ID.Haversine);
	public final static IBuiltInSymbol Head = F.initFinalSymbol("Head", ID.Head);
	public final static IBuiltInSymbol HeavisideTheta = F.initFinalSymbol("HeavisideTheta", ID.HeavisideTheta);
	public final static IBuiltInSymbol HermiteH = F.initFinalSymbol("HermiteH", ID.HermiteH);
	public final static IBuiltInSymbol HermitianMatrixQ = F.initFinalSymbol("HermitianMatrixQ", ID.HermitianMatrixQ);
	public final static IBuiltInSymbol HilbertMatrix = F.initFinalSymbol("HilbertMatrix", ID.HilbertMatrix);
	public final static IBuiltInSymbol Hold = F.initFinalSymbol("Hold", ID.Hold);
	public final static IBuiltInSymbol HoldAll = F.initFinalSymbol("HoldAll", ID.HoldAll);
	public final static IBuiltInSymbol HoldFirst = F.initFinalSymbol("HoldFirst", ID.HoldFirst);
	public final static IBuiltInSymbol HoldForm = F.initFinalSymbol("HoldForm", ID.HoldForm);
	public final static IBuiltInSymbol HoldPattern = F.initFinalSymbol("HoldPattern", ID.HoldPattern);
	public final static IBuiltInSymbol HoldRest = F.initFinalSymbol("HoldRest", ID.HoldRest);
	public final static IBuiltInSymbol Horner = F.initFinalSymbol("Horner", ID.Horner);
	public final static IBuiltInSymbol HornerForm = F.initFinalSymbol("HornerForm", ID.HornerForm);
	public final static IBuiltInSymbol HurwitzZeta = F.initFinalSymbol("HurwitzZeta", ID.HurwitzZeta);
	public final static IBuiltInSymbol Hypergeometric1F1 = F.initFinalSymbol("Hypergeometric1F1", ID.Hypergeometric1F1);
	public final static IBuiltInSymbol Hypergeometric2F1 = F.initFinalSymbol("Hypergeometric2F1", ID.Hypergeometric2F1);
	public final static IBuiltInSymbol HypergeometricDistribution = F.initFinalSymbol("HypergeometricDistribution",
			ID.HypergeometricDistribution);
	public final static IBuiltInSymbol HypergeometricPFQ = F.initFinalSymbol("HypergeometricPFQ", ID.HypergeometricPFQ);
	public final static IBuiltInSymbol HypergeometricPFQRegularized = F.initFinalSymbol("HypergeometricPFQRegularized",
			ID.HypergeometricPFQRegularized);
	public final static IBuiltInSymbol I = F.initFinalSymbol("I", ID.I);
	public final static IBuiltInSymbol Identity = F.initFinalSymbol("Identity", ID.Identity);
	public final static IBuiltInSymbol IdentityMatrix = F.initFinalSymbol("IdentityMatrix", ID.IdentityMatrix);
	public final static IBuiltInSymbol If = F.initFinalSymbol("If", ID.If);
	public final static IBuiltInSymbol Im = F.initFinalSymbol("Im", ID.Im);
	public final static IBuiltInSymbol Implies = F.initFinalSymbol("Implies", ID.Implies);
	public final static IBuiltInSymbol Import = F.initFinalSymbol("Import", ID.Import);
	public final static IBuiltInSymbol Increment = F.initFinalSymbol("Increment", ID.Increment);
	public final static IBuiltInSymbol Indeterminate = F.initFinalSymbol("Indeterminate", ID.Indeterminate);
	public final static IBuiltInSymbol Inequality = F.initFinalSymbol("Inequality", ID.Inequality);
	public final static IBuiltInSymbol InexactNumberQ = F.initFinalSymbol("InexactNumberQ", ID.InexactNumberQ);
	public final static IBuiltInSymbol Infinity = F.initFinalSymbol("Infinity", ID.Infinity);
	public final static IBuiltInSymbol Information = F.initFinalSymbol("Information", ID.Information);
	public final static IBuiltInSymbol Inner = F.initFinalSymbol("Inner", ID.Inner);
	public final static IBuiltInSymbol InputForm = F.initFinalSymbol("InputForm", ID.InputForm);
	public final static IBuiltInSymbol Insert = F.initFinalSymbol("Insert", ID.Insert);
	public final static IBuiltInSymbol Integer = F.initFinalSymbol("Integer", ID.Integer);
	public final static IBuiltInSymbol IntegerDigits = F.initFinalSymbol("IntegerDigits", ID.IntegerDigits);
	public final static IBuiltInSymbol IntegerExponent = F.initFinalSymbol("IntegerExponent", ID.IntegerExponent);
	public final static IBuiltInSymbol IntegerLength = F.initFinalSymbol("IntegerLength", ID.IntegerLength);
	public final static IBuiltInSymbol IntegerPart = F.initFinalSymbol("IntegerPart", ID.IntegerPart);
	public final static IBuiltInSymbol IntegerPartitions = F.initFinalSymbol("IntegerPartitions", ID.IntegerPartitions);
	public final static IBuiltInSymbol IntegerQ = F.initFinalSymbol("IntegerQ", ID.IntegerQ);
	public final static IBuiltInSymbol Integers = F.initFinalSymbol("Integers", ID.Integers);
	public final static IBuiltInSymbol Integrate = F.initFinalSymbol("Integrate", ID.Integrate);
	public final static IBuiltInSymbol InterpolatingFunction = F.initFinalSymbol("InterpolatingFunction",
			ID.InterpolatingFunction);
	public final static IBuiltInSymbol InterpolatingPolynomial = F.initFinalSymbol("InterpolatingPolynomial",
			ID.InterpolatingPolynomial);
	public final static IBuiltInSymbol Interpolation = F.initFinalSymbol("Interpolation", ID.Interpolation);
	public final static IBuiltInSymbol Intersection = F.initFinalSymbol("Intersection", ID.Intersection);
	public final static IBuiltInSymbol Interval = F.initFinalSymbol("Interval", ID.Interval);
	public final static IBuiltInSymbol Inverse = F.initFinalSymbol("Inverse", ID.Inverse);
	public final static IBuiltInSymbol InverseBetaRegularized = F.initFinalSymbol("InverseBetaRegularized",
			ID.InverseBetaRegularized);
	public final static IBuiltInSymbol InverseErf = F.initFinalSymbol("InverseErf", ID.InverseErf);
	public final static IBuiltInSymbol InverseErfc = F.initFinalSymbol("InverseErfc", ID.InverseErfc);
	public final static IBuiltInSymbol InverseFunction = F.initFinalSymbol("InverseFunction", ID.InverseFunction);
	public final static IBuiltInSymbol InverseGammaRegularized = F.initFinalSymbol("InverseGammaRegularized",
			ID.InverseGammaRegularized);
	public final static IBuiltInSymbol InverseHaversine = F.initFinalSymbol("InverseHaversine", ID.InverseHaversine);
	public final static IBuiltInSymbol InverseLaplaceTransform = F.initFinalSymbol("InverseLaplaceTransform",
			ID.InverseLaplaceTransform);
	public final static IBuiltInSymbol InverseSeries = F.initFinalSymbol("InverseSeries", ID.InverseSeries);
	public final static IBuiltInSymbol JaccardDissimilarity = F.initFinalSymbol("JaccardDissimilarity",
			ID.JaccardDissimilarity);
	public final static IBuiltInSymbol JacobiMatrix = F.initFinalSymbol("JacobiMatrix", ID.JacobiMatrix);
	public final static IBuiltInSymbol JacobiSymbol = F.initFinalSymbol("JacobiSymbol", ID.JacobiSymbol);
	public final static IBuiltInSymbol JacobiZeta = F.initFinalSymbol("JacobiZeta", ID.JacobiZeta);
	public final static IBuiltInSymbol JavaForm = F.initFinalSymbol("JavaForm", ID.JavaForm);
	public final static IBuiltInSymbol Join = F.initFinalSymbol("Join", ID.Join);
	public final static IBuiltInSymbol KOrderlessPartitions = F.initFinalSymbol("KOrderlessPartitions",
			ID.KOrderlessPartitions);
	public final static IBuiltInSymbol KPartitions = F.initFinalSymbol("KPartitions", ID.KPartitions);
	public final static IBuiltInSymbol Khinchin = F.initFinalSymbol("Khinchin", ID.Khinchin);
	public final static IBuiltInSymbol KroneckerDelta = F.initFinalSymbol("KroneckerDelta", ID.KroneckerDelta);
	public final static IBuiltInSymbol Kurtosis = F.initFinalSymbol("Kurtosis", ID.Kurtosis);
	public final static IBuiltInSymbol LCM = F.initFinalSymbol("LCM", ID.LCM);
	public final static IBuiltInSymbol LUDecomposition = F.initFinalSymbol("LUDecomposition", ID.LUDecomposition);
	public final static IBuiltInSymbol LaguerreL = F.initFinalSymbol("LaguerreL", ID.LaguerreL);
	public final static IBuiltInSymbol LaplaceTransform = F.initFinalSymbol("LaplaceTransform", ID.LaplaceTransform);
	public final static IBuiltInSymbol Last = F.initFinalSymbol("Last", ID.Last);
	public final static IBuiltInSymbol LeafCount = F.initFinalSymbol("LeafCount", ID.LeafCount);
	public final static IBuiltInSymbol LeastSquares = F.initFinalSymbol("LeastSquares", ID.LeastSquares);
	public final static IBuiltInSymbol LegendreP = F.initFinalSymbol("LegendreP", ID.LegendreP);
	public final static IBuiltInSymbol LegendreQ = F.initFinalSymbol("LegendreQ", ID.LegendreQ);
	public final static IBuiltInSymbol Length = F.initFinalSymbol("Length", ID.Length);
	public final static IBuiltInSymbol Less = F.initFinalSymbol("Less", ID.Less);
	public final static IBuiltInSymbol LessEqual = F.initFinalSymbol("LessEqual", ID.LessEqual);
	public final static IBuiltInSymbol LetterQ = F.initFinalSymbol("LetterQ", ID.LetterQ);
	public final static IBuiltInSymbol Level = F.initFinalSymbol("Level", ID.Level);
	public final static IBuiltInSymbol LevelQ = F.initFinalSymbol("LevelQ", ID.LevelQ);
	public final static IBuiltInSymbol Limit = F.initFinalSymbol("Limit", ID.Limit);
	public final static IBuiltInSymbol Line = F.initFinalSymbol("Line", ID.Line);
	public final static IBuiltInSymbol LinearModelFit = F.initFinalSymbol("LinearModelFit", ID.LinearModelFit);
	public final static IBuiltInSymbol LinearProgramming = F.initFinalSymbol("LinearProgramming", ID.LinearProgramming);
	public final static IBuiltInSymbol LinearSolve = F.initFinalSymbol("LinearSolve", ID.LinearSolve);
	public final static IBuiltInSymbol LiouvilleLambda = F.initFinalSymbol("LiouvilleLambda", ID.LiouvilleLambda);
	public final static IBuiltInSymbol List = F.initFinalSymbol("List", ID.List);
	public final static IBuiltInSymbol ListConvolve = F.initFinalSymbol("ListConvolve", ID.ListConvolve);
	public final static IBuiltInSymbol ListCorrelate = F.initFinalSymbol("ListCorrelate", ID.ListCorrelate);
	public final static IBuiltInSymbol ListQ = F.initFinalSymbol("ListQ", ID.ListQ);
	public final static IBuiltInSymbol Listable = F.initFinalSymbol("Listable", ID.Listable);
	public final static IBuiltInSymbol Literal = F.initFinalSymbol("Literal", ID.Literal);
	public final static IBuiltInSymbol Log = F.initFinalSymbol("Log", ID.Log);
	public final static IBuiltInSymbol Log10 = F.initFinalSymbol("Log10", ID.Log10);
	public final static IBuiltInSymbol Log2 = F.initFinalSymbol("Log2", ID.Log2);
	public final static IBuiltInSymbol LogGamma = F.initFinalSymbol("LogGamma", ID.LogGamma);
	public final static IBuiltInSymbol LogIntegral = F.initFinalSymbol("LogIntegral", ID.LogIntegral);
	public final static IBuiltInSymbol LogNormalDistribution = F.initFinalSymbol("LogNormalDistribution",
			ID.LogNormalDistribution);
	public final static IBuiltInSymbol LogicalExpand = F.initFinalSymbol("LogicalExpand", ID.LogicalExpand);
	public final static IBuiltInSymbol LogisticSigmoid = F.initFinalSymbol("LogisticSigmoid", ID.LogisticSigmoid);
	public final static IBuiltInSymbol LowerCaseQ = F.initFinalSymbol("LowerCaseQ", ID.LowerCaseQ);
	public final static IBuiltInSymbol LowerTriangularize = F.initFinalSymbol("LowerTriangularize",
			ID.LowerTriangularize);
	public final static IBuiltInSymbol LucasL = F.initFinalSymbol("LucasL", ID.LucasL);
	public final static IBuiltInSymbol MachineNumberQ = F.initFinalSymbol("MachineNumberQ", ID.MachineNumberQ);
	public final static IBuiltInSymbol MangoldtLambda = F.initFinalSymbol("MangoldtLambda", ID.MangoldtLambda);
	public final static IBuiltInSymbol ManhattanDistance = F.initFinalSymbol("ManhattanDistance", ID.ManhattanDistance);
	public final static IBuiltInSymbol MantissaExponent = F.initFinalSymbol("MantissaExponent", ID.MantissaExponent);
	public final static IBuiltInSymbol Map = F.initFinalSymbol("Map", ID.Map);
	public final static IBuiltInSymbol MapAll = F.initFinalSymbol("MapAll", ID.MapAll);
	public final static IBuiltInSymbol MapAt = F.initFinalSymbol("MapAt", ID.MapAt);
	public final static IBuiltInSymbol MapIndexed = F.initFinalSymbol("MapIndexed", ID.MapIndexed);
	public final static IBuiltInSymbol MapThread = F.initFinalSymbol("MapThread", ID.MapThread);
	public final static IBuiltInSymbol MatchQ = F.initFinalSymbol("MatchQ", ID.MatchQ);
	public final static IBuiltInSymbol MatchingDissimilarity = F.initFinalSymbol("MatchingDissimilarity",
			ID.MatchingDissimilarity);
	public final static IBuiltInSymbol MathMLForm = F.initFinalSymbol("MathMLForm", ID.MathMLForm);
	public final static IBuiltInSymbol MatrixForm = F.initFinalSymbol("MatrixForm", ID.MatrixForm);
	public final static IBuiltInSymbol MatrixMinimalPolynomial = F.initFinalSymbol("MatrixMinimalPolynomial",
			ID.MatrixMinimalPolynomial);
	public final static IBuiltInSymbol MatrixPower = F.initFinalSymbol("MatrixPower", ID.MatrixPower);
	public final static IBuiltInSymbol MatrixQ = F.initFinalSymbol("MatrixQ", ID.MatrixQ);
	public final static IBuiltInSymbol MatrixRank = F.initFinalSymbol("MatrixRank", ID.MatrixRank);
	public final static IBuiltInSymbol Max = F.initFinalSymbol("Max", ID.Max);
	public final static IBuiltInSymbol MaxIterations = F.initFinalSymbol("MaxIterations", ID.MaxIterations);
	public final static IBuiltInSymbol MaxPoints = F.initFinalSymbol("MaxPoints", ID.MaxPoints);
	public final static IBuiltInSymbol Mean = F.initFinalSymbol("Mean", ID.Mean);
	public final static IBuiltInSymbol MeanDeviation = F.initFinalSymbol("MeanDeviation", ID.MeanDeviation);
	public final static IBuiltInSymbol Median = F.initFinalSymbol("Median", ID.Median);
	public final static IBuiltInSymbol MeijerG = F.initFinalSymbol("MeijerG", ID.MeijerG);
	public final static IBuiltInSymbol MemberQ = F.initFinalSymbol("MemberQ", ID.MemberQ);
	public final static IBuiltInSymbol MersennePrimeExponent = F.initFinalSymbol("MersennePrimeExponent",
			ID.MersennePrimeExponent);
	public final static IBuiltInSymbol MersennePrimeExponentQ = F.initFinalSymbol("MersennePrimeExponentQ",
			ID.MersennePrimeExponentQ);
	public final static IBuiltInSymbol MeshRange = F.initFinalSymbol("MeshRange", ID.MeshRange);
	public final static IBuiltInSymbol MessageName = F.initFinalSymbol("MessageName", ID.MessageName);
	public final static IBuiltInSymbol Method = F.initFinalSymbol("Method", ID.Method);
	public final static IBuiltInSymbol Min = F.initFinalSymbol("Min", ID.Min);
	public final static IBuiltInSymbol MinimalPolynomial = F.initFinalSymbol("MinimalPolynomial", ID.MinimalPolynomial);
	public final static IBuiltInSymbol Minus = F.initFinalSymbol("Minus", ID.Minus);
	public final static IBuiltInSymbol Missing = F.initFinalSymbol("Missing", ID.Missing);
	public final static IBuiltInSymbol MissingQ = F.initFinalSymbol("MissingQ", ID.MissingQ);
	public final static IBuiltInSymbol Mod = F.initFinalSymbol("Mod", ID.Mod);
	public final static IBuiltInSymbol Module = F.initFinalSymbol("Module", ID.Module);
	public final static IBuiltInSymbol Modulus = F.initFinalSymbol("Modulus", ID.Modulus);
	public final static IBuiltInSymbol MoebiusMu = F.initFinalSymbol("MoebiusMu", ID.MoebiusMu);
	public final static IBuiltInSymbol MonomialList = F.initFinalSymbol("MonomialList", ID.MonomialList);
	public final static IBuiltInSymbol Most = F.initFinalSymbol("Most", ID.Most);
	public final static IBuiltInSymbol Multinomial = F.initFinalSymbol("Multinomial", ID.Multinomial);
	public final static IBuiltInSymbol MultiplicativeOrder = F.initFinalSymbol("MultiplicativeOrder",
			ID.MultiplicativeOrder);
	public final static IBuiltInSymbol N = F.initFinalSymbol("N", ID.N);
	public final static IBuiltInSymbol NDSolve = F.initFinalSymbol("NDSolve", ID.NDSolve);
	public final static IBuiltInSymbol NFourierTransform = F.initFinalSymbol("NFourierTransform", ID.NFourierTransform);
	public final static IBuiltInSymbol NHoldAll = F.initFinalSymbol("NHoldAll", ID.NHoldAll);
	public final static IBuiltInSymbol NHoldFirst = F.initFinalSymbol("NHoldFirst", ID.NHoldFirst);
	public final static IBuiltInSymbol NHoldRest = F.initFinalSymbol("NHoldRest", ID.NHoldRest);
	public final static IBuiltInSymbol NIntegrate = F.initFinalSymbol("NIntegrate", ID.NIntegrate);
	public final static IBuiltInSymbol NMaximize = F.initFinalSymbol("NMaximize", ID.NMaximize);
	public final static IBuiltInSymbol NMinimize = F.initFinalSymbol("NMinimize", ID.NMinimize);
	public final static IBuiltInSymbol NRoots = F.initFinalSymbol("NRoots", ID.NRoots);
	public final static IBuiltInSymbol NSolve = F.initFinalSymbol("NSolve", ID.NSolve);
	public final static IBuiltInSymbol NakagamiDistribution = F.initFinalSymbol("NakagamiDistribution",
			ID.NakagamiDistribution);
	public final static IBuiltInSymbol Names = F.initFinalSymbol("Names", ID.Names);
	public final static IBuiltInSymbol Nand = F.initFinalSymbol("Nand", ID.Nand);
	public final static IBuiltInSymbol Nearest = F.initFinalSymbol("Nearest", ID.Nearest);
	public final static IBuiltInSymbol Negative = F.initFinalSymbol("Negative", ID.Negative);
	public final static IBuiltInSymbol Nest = F.initFinalSymbol("Nest", ID.Nest);
	public final static IBuiltInSymbol NestList = F.initFinalSymbol("NestList", ID.NestList);
	public final static IBuiltInSymbol NestWhile = F.initFinalSymbol("NestWhile", ID.NestWhile);
	public final static IBuiltInSymbol NestWhileList = F.initFinalSymbol("NestWhileList", ID.NestWhileList);
	public final static IBuiltInSymbol NextPrime = F.initFinalSymbol("NextPrime", ID.NextPrime);
	public final static IBuiltInSymbol NonCommutativeMultiply = F.initFinalSymbol("NonCommutativeMultiply",
			ID.NonCommutativeMultiply);
	public final static IBuiltInSymbol NonNegative = F.initFinalSymbol("NonNegative", ID.NonNegative);
	public final static IBuiltInSymbol NonPositive = F.initFinalSymbol("NonPositive", ID.NonPositive);
	public final static IBuiltInSymbol None = F.initFinalSymbol("None", ID.None);
	public final static IBuiltInSymbol NoneTrue = F.initFinalSymbol("NoneTrue", ID.NoneTrue);
	public final static IBuiltInSymbol Nonexistent = F.initFinalSymbol("Nonexistent", ID.Nonexistent);
	public final static IBuiltInSymbol Nor = F.initFinalSymbol("Nor", ID.Nor);
	public final static IBuiltInSymbol Norm = F.initFinalSymbol("Norm", ID.Norm);
	public final static IBuiltInSymbol Normal = F.initFinalSymbol("Normal", ID.Normal);
	public final static IBuiltInSymbol NormalDistribution = F.initFinalSymbol("NormalDistribution",
			ID.NormalDistribution);
	public final static IBuiltInSymbol Normalize = F.initFinalSymbol("Normalize", ID.Normalize);
	public final static IBuiltInSymbol Not = F.initFinalSymbol("Not", ID.Not);
	public final static IBuiltInSymbol NotApplicable = F.initFinalSymbol("NotApplicable", ID.NotApplicable);
	public final static IBuiltInSymbol NotAvailable = F.initFinalSymbol("NotAvailable", ID.NotAvailable);
	public final static IBuiltInSymbol NotListQ = F.initFinalSymbol("NotListQ", ID.NotListQ);
	public final static IBuiltInSymbol Null = F.initFinalSymbol("Null", ID.Null);
	public final static IBuiltInSymbol NullSpace = F.initFinalSymbol("NullSpace", ID.NullSpace);
	public final static IBuiltInSymbol NumberFieldRootsOfUnity = F.initFinalSymbol("NumberFieldRootsOfUnity",
			ID.NumberFieldRootsOfUnity);
	public final static IBuiltInSymbol NumberQ = F.initFinalSymbol("NumberQ", ID.NumberQ);
	public final static IBuiltInSymbol Numerator = F.initFinalSymbol("Numerator", ID.Numerator);
	public final static IBuiltInSymbol NumericFunction = F.initFinalSymbol("NumericFunction", ID.NumericFunction);
	public final static IBuiltInSymbol NumericQ = F.initFinalSymbol("NumericQ", ID.NumericQ);
	public final static IBuiltInSymbol O = F.initFinalSymbol("O", ID.O);
	public final static IBuiltInSymbol OddQ = F.initFinalSymbol("OddQ", ID.OddQ);
	public final static IBuiltInSymbol OneIdentity = F.initFinalSymbol("OneIdentity", ID.OneIdentity);
	public final static IBuiltInSymbol Operate = F.initFinalSymbol("Operate", ID.Operate);
	public final static IBuiltInSymbol Optional = F.initFinalSymbol("Optional", ID.Optional);
	public final static IBuiltInSymbol Options = F.initFinalSymbol("Options", ID.Options);
	public final static IBuiltInSymbol Or = F.initFinalSymbol("Or", ID.Or);
	public final static IBuiltInSymbol Order = F.initFinalSymbol("Order", ID.Order);
	public final static IBuiltInSymbol OrderedQ = F.initFinalSymbol("OrderedQ", ID.OrderedQ);
	public final static IBuiltInSymbol Ordering = F.initFinalSymbol("Ordering", ID.Ordering);
	public final static IBuiltInSymbol Orderless = F.initFinalSymbol("Orderless", ID.Orderless);
	public final static IBuiltInSymbol OrthogonalMatrixQ = F.initFinalSymbol("OrthogonalMatrixQ", ID.OrthogonalMatrixQ);
	public final static IBuiltInSymbol Orthogonalize = F.initFinalSymbol("Orthogonalize", ID.Orthogonalize);
	public final static IBuiltInSymbol Out = F.initFinalSymbol("Out", ID.Out);
	public final static IBuiltInSymbol Outer = F.initFinalSymbol("Outer", ID.Outer);
	public final static IBuiltInSymbol OutputForm = F.initFinalSymbol("OutputForm", ID.OutputForm);
	public final static IBuiltInSymbol PDF = F.initFinalSymbol("PDF", ID.PDF);
	public final static IBuiltInSymbol Package = F.initFinalSymbol("Package", ID.Package);
	public final static IBuiltInSymbol PadLeft = F.initFinalSymbol("PadLeft", ID.PadLeft);
	public final static IBuiltInSymbol PadRight = F.initFinalSymbol("PadRight", ID.PadRight);
	public final static IBuiltInSymbol ParametricPlot = F.initFinalSymbol("ParametricPlot", ID.ParametricPlot);
	public final static IBuiltInSymbol Part = F.initFinalSymbol("Part", ID.Part);
	public final static IBuiltInSymbol Partition = F.initFinalSymbol("Partition", ID.Partition);
	public final static IBuiltInSymbol PartitionsP = F.initFinalSymbol("PartitionsP", ID.PartitionsP);
	public final static IBuiltInSymbol PartitionsQ = F.initFinalSymbol("PartitionsQ", ID.PartitionsQ);
	public final static IBuiltInSymbol Pattern = F.initFinalSymbol("Pattern", ID.Pattern);
	public final static IBuiltInSymbol PatternTest = F.initFinalSymbol("PatternTest", ID.PatternTest);
	public final static IBuiltInSymbol PerfectNumber = F.initFinalSymbol("PerfectNumber", ID.PerfectNumber);
	public final static IBuiltInSymbol PerfectNumberQ = F.initFinalSymbol("PerfectNumberQ", ID.PerfectNumberQ);
	public final static IBuiltInSymbol Permutations = F.initFinalSymbol("Permutations", ID.Permutations);
	public final static IBuiltInSymbol Pi = F.initFinalSymbol("Pi", ID.Pi);
	public final static IBuiltInSymbol Piecewise = F.initFinalSymbol("Piecewise", ID.Piecewise);
	public final static IBuiltInSymbol Plot = F.initFinalSymbol("Plot", ID.Plot);
	public final static IBuiltInSymbol Plot3D = F.initFinalSymbol("Plot3D", ID.Plot3D);
	public final static IBuiltInSymbol PlotRange = F.initFinalSymbol("PlotRange", ID.PlotRange);
	public final static IBuiltInSymbol Plus = F.initFinalSymbol("Plus", ID.Plus);
	public final static IBuiltInSymbol Pochhammer = F.initFinalSymbol("Pochhammer", ID.Pochhammer);
	public final static IBuiltInSymbol Point = F.initFinalSymbol("Point", ID.Point);
	public final static IBuiltInSymbol PoissonDistribution = F.initFinalSymbol("PoissonDistribution",
			ID.PoissonDistribution);
	public final static IBuiltInSymbol PolyGamma = F.initFinalSymbol("PolyGamma", ID.PolyGamma);
	public final static IBuiltInSymbol PolyLog = F.initFinalSymbol("PolyLog", ID.PolyLog);
	public final static IBuiltInSymbol Polygon = F.initFinalSymbol("Polygon", ID.Polygon);
	public final static IBuiltInSymbol PolynomialExtendedGCD = F.initFinalSymbol("PolynomialExtendedGCD",
			ID.PolynomialExtendedGCD);
	public final static IBuiltInSymbol PolynomialGCD = F.initFinalSymbol("PolynomialGCD", ID.PolynomialGCD);
	public final static IBuiltInSymbol PolynomialLCM = F.initFinalSymbol("PolynomialLCM", ID.PolynomialLCM);
	public final static IBuiltInSymbol PolynomialQ = F.initFinalSymbol("PolynomialQ", ID.PolynomialQ);
	public final static IBuiltInSymbol PolynomialQuotient = F.initFinalSymbol("PolynomialQuotient",
			ID.PolynomialQuotient);
	public final static IBuiltInSymbol PolynomialQuotientRemainder = F.initFinalSymbol("PolynomialQuotientRemainder",
			ID.PolynomialQuotientRemainder);
	public final static IBuiltInSymbol PolynomialRemainder = F.initFinalSymbol("PolynomialRemainder",
			ID.PolynomialRemainder);
	public final static IBuiltInSymbol Position = F.initFinalSymbol("Position", ID.Position);
	public final static IBuiltInSymbol Positive = F.initFinalSymbol("Positive", ID.Positive);
	public final static IBuiltInSymbol PossibleZeroQ = F.initFinalSymbol("PossibleZeroQ", ID.PossibleZeroQ);
	public final static IBuiltInSymbol Power = F.initFinalSymbol("Power", ID.Power);
	public final static IBuiltInSymbol PowerExpand = F.initFinalSymbol("PowerExpand", ID.PowerExpand);
	public final static IBuiltInSymbol PowerMod = F.initFinalSymbol("PowerMod", ID.PowerMod);
	public final static IBuiltInSymbol PreDecrement = F.initFinalSymbol("PreDecrement", ID.PreDecrement);
	public final static IBuiltInSymbol PreIncrement = F.initFinalSymbol("PreIncrement", ID.PreIncrement);
	public final static IBuiltInSymbol PrePlus = F.initFinalSymbol("PrePlus", ID.PrePlus);
	public final static IBuiltInSymbol Precision = F.initFinalSymbol("Precision", ID.Precision);
	public final static IBuiltInSymbol PrecisionGoal = F.initFinalSymbol("PrecisionGoal", ID.PrecisionGoal);
	public final static IBuiltInSymbol Prepend = F.initFinalSymbol("Prepend", ID.Prepend);
	public final static IBuiltInSymbol PrependTo = F.initFinalSymbol("PrependTo", ID.PrependTo);
	public final static IBuiltInSymbol Prime = F.initFinalSymbol("Prime", ID.Prime);
	public final static IBuiltInSymbol PrimeOmega = F.initFinalSymbol("PrimeOmega", ID.PrimeOmega);
	public final static IBuiltInSymbol PrimePi = F.initFinalSymbol("PrimePi", ID.PrimePi);
	public final static IBuiltInSymbol PrimePowerQ = F.initFinalSymbol("PrimePowerQ", ID.PrimePowerQ);
	public final static IBuiltInSymbol PrimeQ = F.initFinalSymbol("PrimeQ", ID.PrimeQ);
	public final static IBuiltInSymbol Primes = F.initFinalSymbol("Primes", ID.Primes);
	public final static IBuiltInSymbol PrimitiveRootList = F.initFinalSymbol("PrimitiveRootList", ID.PrimitiveRootList);
	public final static IBuiltInSymbol Print = F.initFinalSymbol("Print", ID.Print);
	public final static IBuiltInSymbol Product = F.initFinalSymbol("Product", ID.Product);
	public final static IBuiltInSymbol ProductLog = F.initFinalSymbol("ProductLog", ID.ProductLog);
	public final static IBuiltInSymbol Projection = F.initFinalSymbol("Projection", ID.Projection);
	public final static IBuiltInSymbol PseudoInverse = F.initFinalSymbol("PseudoInverse", ID.PseudoInverse);
	public final static IBuiltInSymbol Put = F.initFinalSymbol("Put", ID.Put);
	public final static IBuiltInSymbol QRDecomposition = F.initFinalSymbol("QRDecomposition", ID.QRDecomposition);
	public final static IBuiltInSymbol Quantile = F.initFinalSymbol("Quantile", ID.Quantile);
	public final static IBuiltInSymbol Quiet = F.initFinalSymbol("Quiet", ID.Quiet);
	public final static IBuiltInSymbol Quit = F.initFinalSymbol("Quit", ID.Quit);
	public final static IBuiltInSymbol Quotient = F.initFinalSymbol("Quotient", ID.Quotient);
	public final static IBuiltInSymbol QuotientRemainder = F.initFinalSymbol("QuotientRemainder", ID.QuotientRemainder);
	public final static IBuiltInSymbol RandomChoice = F.initFinalSymbol("RandomChoice", ID.RandomChoice);
	public final static IBuiltInSymbol RandomInteger = F.initFinalSymbol("RandomInteger", ID.RandomInteger);
	public final static IBuiltInSymbol RandomReal = F.initFinalSymbol("RandomReal", ID.RandomReal);
	public final static IBuiltInSymbol RandomSample = F.initFinalSymbol("RandomSample", ID.RandomSample);
	public final static IBuiltInSymbol RandomVariate = F.initFinalSymbol("RandomVariate", ID.RandomVariate);
	public final static IBuiltInSymbol Range = F.initFinalSymbol("Range", ID.Range);
	public final static IBuiltInSymbol Rational = F.initFinalSymbol("Rational", ID.Rational);
	public final static IBuiltInSymbol Rationalize = F.initFinalSymbol("Rationalize", ID.Rationalize);
	public final static IBuiltInSymbol Rationals = F.initFinalSymbol("Rationals", ID.Rationals);
	public final static IBuiltInSymbol Re = F.initFinalSymbol("Re", ID.Re);
	public final static IBuiltInSymbol Real = F.initFinalSymbol("Real", ID.Real);
	public final static IBuiltInSymbol RealNumberQ = F.initFinalSymbol("RealNumberQ", ID.RealNumberQ);
	public final static IBuiltInSymbol Reals = F.initFinalSymbol("Reals", ID.Reals);
	public final static IBuiltInSymbol Reap = F.initFinalSymbol("Reap", ID.Reap);
	public final static IBuiltInSymbol Rectangle = F.initFinalSymbol("Rectangle", ID.Rectangle);
	public final static IBuiltInSymbol Reduce = F.initFinalSymbol("Reduce", ID.Reduce);
	public final static IBuiltInSymbol Refine = F.initFinalSymbol("Refine", ID.Refine);
	public final static IBuiltInSymbol Repeated = F.initFinalSymbol("Repeated", ID.Repeated);
	public final static IBuiltInSymbol RepeatedNull = F.initFinalSymbol("RepeatedNull", ID.RepeatedNull);
	public final static IBuiltInSymbol Replace = F.initFinalSymbol("Replace", ID.Replace);
	public final static IBuiltInSymbol ReplaceAll = F.initFinalSymbol("ReplaceAll", ID.ReplaceAll);
	public final static IBuiltInSymbol ReplaceList = F.initFinalSymbol("ReplaceList", ID.ReplaceList);
	public final static IBuiltInSymbol ReplacePart = F.initFinalSymbol("ReplacePart", ID.ReplacePart);
	public final static IBuiltInSymbol ReplaceRepeated = F.initFinalSymbol("ReplaceRepeated", ID.ReplaceRepeated);
	public final static IBuiltInSymbol Rest = F.initFinalSymbol("Rest", ID.Rest);
	public final static IBuiltInSymbol Resultant = F.initFinalSymbol("Resultant", ID.Resultant);
	public final static IBuiltInSymbol Return = F.initFinalSymbol("Return", ID.Return);
	public final static IBuiltInSymbol Reverse = F.initFinalSymbol("Reverse", ID.Reverse);
	public final static IBuiltInSymbol Riffle = F.initFinalSymbol("Riffle", ID.Riffle);
	public final static IBuiltInSymbol RogersTanimotoDissimilarity = F.initFinalSymbol("RogersTanimotoDissimilarity",
			ID.RogersTanimotoDissimilarity);
	public final static IBuiltInSymbol Root = F.initFinalSymbol("Root", ID.Root);
	public final static IBuiltInSymbol RootIntervals = F.initFinalSymbol("RootIntervals", ID.RootIntervals);
	public final static IBuiltInSymbol RootOf = F.initFinalSymbol("RootOf", ID.RootOf);
	public final static IBuiltInSymbol Roots = F.initFinalSymbol("Roots", ID.Roots);
	public final static IBuiltInSymbol RotateLeft = F.initFinalSymbol("RotateLeft", ID.RotateLeft);
	public final static IBuiltInSymbol RotateRight = F.initFinalSymbol("RotateRight", ID.RotateRight);
	public final static IBuiltInSymbol Round = F.initFinalSymbol("Round", ID.Round);
	public final static IBuiltInSymbol RowReduce = F.initFinalSymbol("RowReduce", ID.RowReduce);
	public final static IBuiltInSymbol Rule = F.initFinalSymbol("Rule", ID.Rule);
	public final static IBuiltInSymbol RuleDelayed = F.initFinalSymbol("RuleDelayed", ID.RuleDelayed);
	public final static IBuiltInSymbol RussellRaoDissimilarity = F.initFinalSymbol("RussellRaoDissimilarity",
			ID.RussellRaoDissimilarity);
	public final static IBuiltInSymbol SameQ = F.initFinalSymbol("SameQ", ID.SameQ);
	public final static IBuiltInSymbol SatisfiabilityCount = F.initFinalSymbol("SatisfiabilityCount",
			ID.SatisfiabilityCount);
	public final static IBuiltInSymbol SatisfiabilityInstances = F.initFinalSymbol("SatisfiabilityInstances",
			ID.SatisfiabilityInstances);
	public final static IBuiltInSymbol SatisfiableQ = F.initFinalSymbol("SatisfiableQ", ID.SatisfiableQ);
	public final static IBuiltInSymbol Scan = F.initFinalSymbol("Scan", ID.Scan);
	public final static IBuiltInSymbol Sec = F.initFinalSymbol("Sec", ID.Sec);
	public final static IBuiltInSymbol Sech = F.initFinalSymbol("Sech", ID.Sech);
	public final static IBuiltInSymbol Second = F.initFinalSymbol("Second", ID.Second);
	public final static IBuiltInSymbol Select = F.initFinalSymbol("Select", ID.Select);
	public final static IBuiltInSymbol Sequence = F.initFinalSymbol("Sequence", ID.Sequence);
	public final static IBuiltInSymbol Series = F.initFinalSymbol("Series", ID.Series);
	public final static IBuiltInSymbol SeriesCoefficient = F.initFinalSymbol("SeriesCoefficient", ID.SeriesCoefficient);
	public final static IBuiltInSymbol SeriesData = F.initFinalSymbol("SeriesData", ID.SeriesData);
	public final static IBuiltInSymbol Set = F.initFinalSymbol("Set", ID.Set);
	public final static IBuiltInSymbol SetAttributes = F.initFinalSymbol("SetAttributes", ID.SetAttributes);
	public final static IBuiltInSymbol SetDelayed = F.initFinalSymbol("SetDelayed", ID.SetDelayed);
	public final static IBuiltInSymbol Share = F.initFinalSymbol("Share", ID.Share);
	public final static IBuiltInSymbol Show = F.initFinalSymbol("Show", ID.Show);
	public final static IBuiltInSymbol Sign = F.initFinalSymbol("Sign", ID.Sign);
	public final static IBuiltInSymbol SignCmp = F.initFinalSymbol("SignCmp", ID.SignCmp);
	public final static IBuiltInSymbol Simplify = F.initFinalSymbol("Simplify", ID.Simplify);
	public final static IBuiltInSymbol Sin = F.initFinalSymbol("Sin", ID.Sin);
	public final static IBuiltInSymbol SinIntegral = F.initFinalSymbol("SinIntegral", ID.SinIntegral);
	public final static IBuiltInSymbol Sinc = F.initFinalSymbol("Sinc", ID.Sinc);
	public final static IBuiltInSymbol SingularValueDecomposition = F.initFinalSymbol("SingularValueDecomposition",
			ID.SingularValueDecomposition);
	public final static IBuiltInSymbol Sinh = F.initFinalSymbol("Sinh", ID.Sinh);
	public final static IBuiltInSymbol SinhIntegral = F.initFinalSymbol("SinhIntegral", ID.SinhIntegral);
	public final static IBuiltInSymbol Skewness = F.initFinalSymbol("Skewness", ID.Skewness);
	public final static IBuiltInSymbol Slot = F.initFinalSymbol("Slot", ID.Slot);
	public final static IBuiltInSymbol SlotSequence = F.initFinalSymbol("SlotSequence", ID.SlotSequence);
	public final static IBuiltInSymbol SokalSneathDissimilarity = F.initFinalSymbol("SokalSneathDissimilarity",
			ID.SokalSneathDissimilarity);
	public final static IBuiltInSymbol Solve = F.initFinalSymbol("Solve", ID.Solve);
	public final static IBuiltInSymbol Sort = F.initFinalSymbol("Sort", ID.Sort);
	public final static IBuiltInSymbol Sow = F.initFinalSymbol("Sow", ID.Sow);
	public final static IBuiltInSymbol Span = F.initFinalSymbol("Span", ID.Span);
	public final static IBuiltInSymbol Split = F.initFinalSymbol("Split", ID.Split);
	public final static IBuiltInSymbol SplitBy = F.initFinalSymbol("SplitBy", ID.SplitBy);
	public final static IBuiltInSymbol Sqrt = F.initFinalSymbol("Sqrt", ID.Sqrt);
	public final static IBuiltInSymbol SquareFreeQ = F.initFinalSymbol("SquareFreeQ", ID.SquareFreeQ);
	public final static IBuiltInSymbol SquareMatrixQ = F.initFinalSymbol("SquareMatrixQ", ID.SquareMatrixQ);
	public final static IBuiltInSymbol SquaredEuclideanDistance = F.initFinalSymbol("SquaredEuclideanDistance",
			ID.SquaredEuclideanDistance);
	public final static IBuiltInSymbol StandardDeviation = F.initFinalSymbol("StandardDeviation", ID.StandardDeviation);
	public final static IBuiltInSymbol StandardForm = F.initFinalSymbol("StandardForm", ID.StandardForm);
	public final static IBuiltInSymbol Standardize = F.initFinalSymbol("Standardize", ID.Standardize);
	public final static IBuiltInSymbol StieltjesGamma = F.initFinalSymbol("StieltjesGamma", ID.StieltjesGamma);
	public final static IBuiltInSymbol StirlingS1 = F.initFinalSymbol("StirlingS1", ID.StirlingS1);
	public final static IBuiltInSymbol StirlingS2 = F.initFinalSymbol("StirlingS2", ID.StirlingS2);
	public final static IBuiltInSymbol String = F.initFinalSymbol("String", ID.String);
	public final static IBuiltInSymbol StringDrop = F.initFinalSymbol("StringDrop", ID.StringDrop);
	public final static IBuiltInSymbol StringJoin = F.initFinalSymbol("StringJoin", ID.StringJoin);
	public final static IBuiltInSymbol StringLength = F.initFinalSymbol("StringLength", ID.StringLength);
	public final static IBuiltInSymbol StringTake = F.initFinalSymbol("StringTake", ID.StringTake);
	public final static IBuiltInSymbol StruveH = F.initFinalSymbol("StruveH", ID.StruveH);
	public final static IBuiltInSymbol StruveL = F.initFinalSymbol("StruveL", ID.StruveL);
	public final static IBuiltInSymbol StudentTDistribution = F.initFinalSymbol("StudentTDistribution",
			ID.StudentTDistribution);
	public final static IBuiltInSymbol Subdivide = F.initFinalSymbol("Subdivide", ID.Subdivide);
	public final static IBuiltInSymbol Subfactorial = F.initFinalSymbol("Subfactorial", ID.Subfactorial);
	public final static IBuiltInSymbol Subscript = F.initFinalSymbol("Subscript", ID.Subscript);
	public final static IBuiltInSymbol Subsets = F.initFinalSymbol("Subsets", ID.Subsets);
	public final static IBuiltInSymbol Subsuperscript = F.initFinalSymbol("Subsuperscript", ID.Subsuperscript);
	public final static IBuiltInSymbol Subtract = F.initFinalSymbol("Subtract", ID.Subtract);
	public final static IBuiltInSymbol SubtractFrom = F.initFinalSymbol("SubtractFrom", ID.SubtractFrom);
	public final static IBuiltInSymbol Sum = F.initFinalSymbol("Sum", ID.Sum);
	public final static IBuiltInSymbol Superscript = F.initFinalSymbol("Superscript", ID.Superscript);
	public final static IBuiltInSymbol Surd = F.initFinalSymbol("Surd", ID.Surd);
	public final static IBuiltInSymbol SurfaceGraphics = F.initFinalSymbol("SurfaceGraphics", ID.SurfaceGraphics);
	public final static IBuiltInSymbol Switch = F.initFinalSymbol("Switch", ID.Switch);
	public final static IBuiltInSymbol Symbol = F.initFinalSymbol("Symbol", ID.Symbol);
	public final static IBuiltInSymbol SymbolName = F.initFinalSymbol("SymbolName", ID.SymbolName);
	public final static IBuiltInSymbol SymbolQ = F.initFinalSymbol("SymbolQ", ID.SymbolQ);
	public final static IBuiltInSymbol SymmetricMatrixQ = F.initFinalSymbol("SymmetricMatrixQ", ID.SymmetricMatrixQ);
	public final static IBuiltInSymbol SyntaxLength = F.initFinalSymbol("SyntaxLength", ID.SyntaxLength);
	public final static IBuiltInSymbol SyntaxQ = F.initFinalSymbol("SyntaxQ", ID.SyntaxQ);
	public final static IBuiltInSymbol Table = F.initFinalSymbol("Table", ID.Table);
	public final static IBuiltInSymbol Take = F.initFinalSymbol("Take", ID.Take);
	public final static IBuiltInSymbol Tally = F.initFinalSymbol("Tally", ID.Tally);
	public final static IBuiltInSymbol Tan = F.initFinalSymbol("Tan", ID.Tan);
	public final static IBuiltInSymbol Tanh = F.initFinalSymbol("Tanh", ID.Tanh);
	public final static IBuiltInSymbol TautologyQ = F.initFinalSymbol("TautologyQ", ID.TautologyQ);
	public final static IBuiltInSymbol Taylor = F.initFinalSymbol("Taylor", ID.Taylor);
	public final static IBuiltInSymbol TeXForm = F.initFinalSymbol("TeXForm", ID.TeXForm);
	public final static IBuiltInSymbol TensorDimensions = F.initFinalSymbol("TensorDimensions", ID.TensorDimensions);
	public final static IBuiltInSymbol TensorProduct = F.initFinalSymbol("TensorProduct", ID.TensorProduct);
	public final static IBuiltInSymbol TensorRank = F.initFinalSymbol("TensorRank", ID.TensorRank);
	public final static IBuiltInSymbol Thread = F.initFinalSymbol("Thread", ID.Thread);
	public final static IBuiltInSymbol Through = F.initFinalSymbol("Through", ID.Through);
	public final static IBuiltInSymbol Throw = F.initFinalSymbol("Throw", ID.Throw);
	public final static IBuiltInSymbol TimeConstrained = F.initFinalSymbol("TimeConstrained", ID.TimeConstrained);
	public final static IBuiltInSymbol Times = F.initFinalSymbol("Times", ID.Times);
	public final static IBuiltInSymbol TimesBy = F.initFinalSymbol("TimesBy", ID.TimesBy);
	public final static IBuiltInSymbol Timing = F.initFinalSymbol("Timing", ID.Timing);
	public final static IBuiltInSymbol ToCharacterCode = F.initFinalSymbol("ToCharacterCode", ID.ToCharacterCode);
	public final static IBuiltInSymbol ToPolarCoordinates = F.initFinalSymbol("ToPolarCoordinates",
			ID.ToPolarCoordinates);
	public final static IBuiltInSymbol ToRadicals = F.initFinalSymbol("ToRadicals", ID.ToRadicals);
	public final static IBuiltInSymbol ToString = F.initFinalSymbol("ToString", ID.ToString);
	public final static IBuiltInSymbol ToUnicode = F.initFinalSymbol("ToUnicode", ID.ToUnicode);
	public final static IBuiltInSymbol ToeplitzMatrix = F.initFinalSymbol("ToeplitzMatrix", ID.ToeplitzMatrix);
	public final static IBuiltInSymbol Together = F.initFinalSymbol("Together", ID.Together);
	public final static IBuiltInSymbol TooLarge = F.initFinalSymbol("TooLarge", ID.TooLarge);
	public final static IBuiltInSymbol Total = F.initFinalSymbol("Total", ID.Total);
	public final static IBuiltInSymbol Tr = F.initFinalSymbol("Tr", ID.Tr);
	public final static IBuiltInSymbol Trace = F.initFinalSymbol("Trace", ID.Trace);
	public final static IBuiltInSymbol TraditionalForm = F.initFinalSymbol("TraditionalForm", ID.TraditionalForm);
	public final static IBuiltInSymbol Transpose = F.initFinalSymbol("Transpose", ID.Transpose);
	public final static IBuiltInSymbol Trig = F.initFinalSymbol("Trig", ID.Trig);
	public final static IBuiltInSymbol TrigExpand = F.initFinalSymbol("TrigExpand", ID.TrigExpand);
	public final static IBuiltInSymbol TrigReduce = F.initFinalSymbol("TrigReduce", ID.TrigReduce);
	public final static IBuiltInSymbol TrigToExp = F.initFinalSymbol("TrigToExp", ID.TrigToExp);
	public final static IBuiltInSymbol True = F.initFinalSymbol("True", ID.True);
	public final static IBuiltInSymbol TrueQ = F.initFinalSymbol("TrueQ", ID.TrueQ);
	public final static IBuiltInSymbol Tuples = F.initFinalSymbol("Tuples", ID.Tuples);
	public final static IBuiltInSymbol Undefined = F.initFinalSymbol("Undefined", ID.Undefined);
	public final static IBuiltInSymbol Unequal = F.initFinalSymbol("Unequal", ID.Unequal);
	public final static IBuiltInSymbol Unevaluated = F.initFinalSymbol("Unevaluated", ID.Unevaluated);
	public final static IBuiltInSymbol Union = F.initFinalSymbol("Union", ID.Union);
	public final static IBuiltInSymbol Unique = F.initFinalSymbol("Unique", ID.Unique);
	public final static IBuiltInSymbol UnitStep = F.initFinalSymbol("UnitStep", ID.UnitStep);
	public final static IBuiltInSymbol UnitVector = F.initFinalSymbol("UnitVector", ID.UnitVector);
	public final static IBuiltInSymbol UnitaryMatrixQ = F.initFinalSymbol("UnitaryMatrixQ", ID.UnitaryMatrixQ);
	public final static IBuiltInSymbol Unitize = F.initFinalSymbol("Unitize", ID.Unitize);
	public final static IBuiltInSymbol Unknown = F.initFinalSymbol("Unknown", ID.Unknown);
	public final static IBuiltInSymbol UnsameQ = F.initFinalSymbol("UnsameQ", ID.UnsameQ);
	public final static IBuiltInSymbol Unset = F.initFinalSymbol("Unset", ID.Unset);
	public final static IBuiltInSymbol UpSet = F.initFinalSymbol("UpSet", ID.UpSet);
	public final static IBuiltInSymbol UpSetDelayed = F.initFinalSymbol("UpSetDelayed", ID.UpSetDelayed);
	public final static IBuiltInSymbol UpperCaseQ = F.initFinalSymbol("UpperCaseQ", ID.UpperCaseQ);
	public final static IBuiltInSymbol UpperTriangularize = F.initFinalSymbol("UpperTriangularize",
			ID.UpperTriangularize);
	public final static IBuiltInSymbol ValueQ = F.initFinalSymbol("ValueQ", ID.ValueQ);
	public final static IBuiltInSymbol VandermondeMatrix = F.initFinalSymbol("VandermondeMatrix", ID.VandermondeMatrix);
	public final static IBuiltInSymbol Variable = F.initFinalSymbol("Variable", ID.Variable);
	public final static IBuiltInSymbol Variables = F.initFinalSymbol("Variables", ID.Variables);
	public final static IBuiltInSymbol Variance = F.initFinalSymbol("Variance", ID.Variance);
	public final static IBuiltInSymbol VectorAngle = F.initFinalSymbol("VectorAngle", ID.VectorAngle);
	public final static IBuiltInSymbol VectorQ = F.initFinalSymbol("VectorQ", ID.VectorQ);
	public final static IBuiltInSymbol WeibullDistribution = F.initFinalSymbol("WeibullDistribution",
			ID.WeibullDistribution);
	public final static IBuiltInSymbol Which = F.initFinalSymbol("Which", ID.Which);
	public final static IBuiltInSymbol While = F.initFinalSymbol("While", ID.While);
	public final static IBuiltInSymbol White = F.initFinalSymbol("White", ID.White);
	public final static IBuiltInSymbol With = F.initFinalSymbol("With", ID.With);
	public final static IBuiltInSymbol Xor = F.initFinalSymbol("Xor", ID.Xor);
	public final static IBuiltInSymbol YuleDissimilarity = F.initFinalSymbol("YuleDissimilarity", ID.YuleDissimilarity);
	public final static IBuiltInSymbol Zeta = F.initFinalSymbol("Zeta", ID.Zeta);

	public final static ISymbol $Aborted = initFinalHiddenSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "$aborted" : "$Aborted");
	// public final static ISymbol $PowerSeries = initFinalHiddenSymbol(
	// Config.PARSER_USE_LOWERCASE_SYMBOLS ? "$powerseries" : "$PowerSeries");
	public final static ISymbol $RealVector = initFinalHiddenSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "$realvector" : "$RealVector");
	public final static ISymbol $RealMatrix = initFinalHiddenSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "$realmatrix" : "$RealMatrix");

	// public final static ISymbol usage = initFinalHiddenSymbol("usage");

	public final static ISymbol a = initFinalHiddenSymbol("a");
	public final static ISymbol b = initFinalHiddenSymbol("b");
	public final static ISymbol c = initFinalHiddenSymbol("c");
	public final static ISymbol d = initFinalHiddenSymbol("d");
	public final static ISymbol e = initFinalHiddenSymbol("e");
	public final static ISymbol f = initFinalHiddenSymbol("f");
	public final static ISymbol g = initFinalHiddenSymbol("g");
	public final static ISymbol h = initFinalHiddenSymbol("h");
	public final static ISymbol i = initFinalHiddenSymbol("i");
	public final static ISymbol j = initFinalHiddenSymbol("j");
	public final static ISymbol k = initFinalHiddenSymbol("k");
	public final static ISymbol l = initFinalHiddenSymbol("l");
	public final static ISymbol m = initFinalHiddenSymbol("m");
	public final static ISymbol n = initFinalHiddenSymbol("n");
	public final static ISymbol o = initFinalHiddenSymbol("o");
	public final static ISymbol p = initFinalHiddenSymbol("p");
	public final static ISymbol q = initFinalHiddenSymbol("q");
	public final static ISymbol r = initFinalHiddenSymbol("r");
	public final static ISymbol s = initFinalHiddenSymbol("s");
	public final static ISymbol t = initFinalHiddenSymbol("t");
	public final static ISymbol u = initFinalHiddenSymbol("u");
	public final static ISymbol v = initFinalHiddenSymbol("v");
	public final static ISymbol w = initFinalHiddenSymbol("w");
	public final static ISymbol x = initFinalHiddenSymbol("x");
	public final static ISymbol y = initFinalHiddenSymbol("y");
	public final static ISymbol z = initFinalHiddenSymbol("z");

	public final static ISymbol ASymbol = initFinalHiddenSymbol("A");
	public final static ISymbol BSymbol = initFinalHiddenSymbol("B");
	public final static ISymbol CSymbol = initFinalHiddenSymbol("C");
	public final static ISymbol FSymbol = initFinalHiddenSymbol("F");
	public final static ISymbol GSymbol = initFinalHiddenSymbol("G");

	public final static IPattern a_ = initPredefinedPattern(a);
	public final static IPattern b_ = initPredefinedPattern(b);
	public final static IPattern c_ = initPredefinedPattern(c);
	public final static IPattern d_ = initPredefinedPattern(d);
	public final static IPattern e_ = initPredefinedPattern(e);
	public final static IPattern f_ = initPredefinedPattern(f);
	public final static IPattern g_ = initPredefinedPattern(g);
	public final static IPattern h_ = initPredefinedPattern(h);
	public final static IPattern i_ = initPredefinedPattern(i);
	public final static IPattern j_ = initPredefinedPattern(j);
	public final static IPattern k_ = initPredefinedPattern(k);
	public final static IPattern l_ = initPredefinedPattern(l);
	public final static IPattern m_ = initPredefinedPattern(m);
	public final static IPattern n_ = initPredefinedPattern(n);
	public final static IPattern o_ = initPredefinedPattern(o);
	public final static IPattern p_ = initPredefinedPattern(p);
	public final static IPattern q_ = initPredefinedPattern(q);
	public final static IPattern r_ = initPredefinedPattern(r);
	public final static IPattern s_ = initPredefinedPattern(s);
	public final static IPattern t_ = initPredefinedPattern(t);
	public final static IPattern u_ = initPredefinedPattern(u);
	public final static IPattern v_ = initPredefinedPattern(v);
	public final static IPattern w_ = initPredefinedPattern(w);
	public final static IPattern x_ = initPredefinedPattern(x);
	public final static IPattern y_ = initPredefinedPattern(y);
	public final static IPattern z_ = initPredefinedPattern(z);

	public final static IPatternSequence x__ = initPredefinedPatternSequence(x);
	public final static IPatternSequence y__ = initPredefinedPatternSequence(y);
	public final static IPatternSequence z__ = initPredefinedPatternSequence(z);

	public final static IPattern A_ = initPredefinedPattern(ASymbol);
	public final static IPattern B_ = initPredefinedPattern(BSymbol);
	public final static IPattern C_ = initPredefinedPattern(CSymbol);
	public final static IPattern F_ = initPredefinedPattern(FSymbol);
	public final static IPattern G_ = initPredefinedPattern(GSymbol);

	public final static IPattern m_Integer = new Pattern(m, F.Integer);
	public final static IPattern n_Integer = new Pattern(n, F.Integer);

	public final static IPattern a_Symbol = new Pattern(a, F.Symbol);
	public final static IPattern b_Symbol = new Pattern(b, F.Symbol);
	public final static IPattern c_Symbol = new Pattern(c, F.Symbol);
	public final static IPattern d_Symbol = new Pattern(d, F.Symbol);
	public final static IPattern e_Symbol = new Pattern(e, F.Symbol);
	public final static IPattern f_Symbol = new Pattern(f, F.Symbol);
	public final static IPattern g_Symbol = new Pattern(g, F.Symbol);
	public final static IPattern h_Symbol = new Pattern(h, F.Symbol);
	public final static IPattern i_Symbol = new Pattern(i, F.Symbol);
	public final static IPattern j_Symbol = new Pattern(j, F.Symbol);
	public final static IPattern k_Symbol = new Pattern(k, F.Symbol);
	public final static IPattern l_Symbol = new Pattern(l, F.Symbol);
	public final static IPattern m_Symbol = new Pattern(m, F.Symbol);
	public final static IPattern n_Symbol = new Pattern(n, F.Symbol);
	public final static IPattern o_Symbol = new Pattern(o, F.Symbol);
	public final static IPattern p_Symbol = new Pattern(p, F.Symbol);
	public final static IPattern q_Symbol = new Pattern(q, F.Symbol);
	public final static IPattern r_Symbol = new Pattern(r, F.Symbol);
	public final static IPattern s_Symbol = new Pattern(s, F.Symbol);
	public final static IPattern t_Symbol = new Pattern(t, F.Symbol);
	public final static IPattern u_Symbol = new Pattern(u, F.Symbol);
	public final static IPattern v_Symbol = new Pattern(v, F.Symbol);
	public final static IPattern w_Symbol = new Pattern(w, F.Symbol);
	public final static IPattern x_Symbol = new Pattern(x, F.Symbol);
	public final static IPattern y_Symbol = new Pattern(y, F.Symbol);
	public final static IPattern z_Symbol = new Pattern(z, F.Symbol);

	public final static IPattern a_DEFAULT = new Pattern(a, null, true);
	public final static IPattern b_DEFAULT = new Pattern(b, null, true);
	public final static IPattern c_DEFAULT = new Pattern(c, null, true);
	public final static IPattern d_DEFAULT = new Pattern(d, null, true);
	public final static IPattern e_DEFAULT = new Pattern(e, null, true);
	public final static IPattern f_DEFAULT = new Pattern(f, null, true);
	public final static IPattern g_DEFAULT = new Pattern(g, null, true);
	public final static IPattern h_DEFAULT = new Pattern(h, null, true);
	public final static IPattern i_DEFAULT = new Pattern(i, null, true);
	public final static IPattern j_DEFAULT = new Pattern(j, null, true);
	public final static IPattern k_DEFAULT = new Pattern(k, null, true);
	public final static IPattern l_DEFAULT = new Pattern(l, null, true);
	public final static IPattern m_DEFAULT = new Pattern(m, null, true);
	public final static IPattern n_DEFAULT = new Pattern(n, null, true);
	public final static IPattern o_DEFAULT = new Pattern(o, null, true);
	public final static IPattern p_DEFAULT = new Pattern(p, null, true);
	public final static IPattern q_DEFAULT = new Pattern(q, null, true);
	public final static IPattern r_DEFAULT = new Pattern(r, null, true);
	public final static IPattern s_DEFAULT = new Pattern(s, null, true);
	public final static IPattern t_DEFAULT = new Pattern(t, null, true);
	public final static IPattern u_DEFAULT = new Pattern(u, null, true);
	public final static IPattern v_DEFAULT = new Pattern(v, null, true);
	public final static IPattern w_DEFAULT = new Pattern(w, null, true);
	public final static IPattern x_DEFAULT = new Pattern(x, null, true);
	public final static IPattern y_DEFAULT = new Pattern(y, null, true);
	public final static IPattern z_DEFAULT = new Pattern(z, null, true);

	public final static IPattern A_DEFAULT = new Pattern(ASymbol, null, true);
	public final static IPattern B_DEFAULT = new Pattern(BSymbol, null, true);
	public final static IPattern C_DEFAULT = new Pattern(CSymbol, null, true);
	public final static IPattern F_DEFAULT = new Pattern(FSymbol, null, true);
	public final static IPattern G_DEFAULT = new Pattern(GSymbol, null, true);
	/**
	 * Constant integer &quot;0&quot;
	 */
	public final static IntegerSym C0 = AbstractIntegerSym.valueOf(0);

	/**
	 * Constant integer &quot;1&quot;
	 */
	public final static IntegerSym C1 = AbstractIntegerSym.valueOf(1);

	/**
	 * Constant integer &quot;2&quot;
	 */
	public final static IntegerSym C2 = AbstractIntegerSym.valueOf(2);

	/**
	 * Constant integer &quot;3&quot;
	 */
	public final static IntegerSym C3 = AbstractIntegerSym.valueOf(3);

	/**
	 * Constant integer &quot;4&quot;
	 */
	public final static IntegerSym C4 = AbstractIntegerSym.valueOf(4);

	/**
	 * Constant integer &quot;5&quot;
	 */
	public final static IntegerSym C5 = AbstractIntegerSym.valueOf(5);

	/**
	 * Constant integer &quot;6&quot;
	 */
	public final static IntegerSym C6 = AbstractIntegerSym.valueOf(6);

	/**
	 * Constant integer &quot;7&quot;
	 */
	public final static IntegerSym C7 = AbstractIntegerSym.valueOf(7);

	/**
	 * Constant integer &quot;8&quot;
	 */
	public final static IntegerSym C8 = AbstractIntegerSym.valueOf(8);

	/**
	 * Constant integer &quot;9&quot;
	 */
	public final static IntegerSym C9 = AbstractIntegerSym.valueOf(9);

	/**
	 * Constant integer &quot;10&quot;
	 */
	public final static IntegerSym C10 = AbstractIntegerSym.valueOf(10);

	/**
	 * Complex imaginary unit. The parsed symbol &quot;I&quot; is converted on input to this constant.
	 */
	public final static IComplex CI = ComplexSym.valueOf(BigInteger.ZERO, BigInteger.ONE);

	/**
	 * Complex negative imaginary unit.
	 */
	public final static IComplex CNI = ComplexSym.valueOf(BigInteger.ZERO, BigInteger.valueOf(-1L));

	/**
	 * Constant fraction &quot;1/2&quot;
	 */
	public final static IFraction C1D2 = AbstractFractionSym.valueOf(1, 2);

	/**
	 * Constant fraction &quot;3/2&quot;
	 */
	public final static IFraction C3D2 = AbstractFractionSym.valueOf(3, 2);

	/**
	 * Constant fraction &quot;3/4&quot;
	 */
	public final static IFraction C3D4 = AbstractFractionSym.valueOf(3, 4);

	/**
	 * Constant fraction &quot;5/2&quot;
	 */
	public final static IFraction C5D2 = AbstractFractionSym.valueOf(5, 2);

	/**
	 * Constant fraction &quot;-1/2&quot;
	 */
	public final static IFraction CN1D2 = AbstractFractionSym.valueOf(-1, 2);

	/**
	 * Constant fraction &quot;-3/2&quot;
	 */
	public final static IFraction CN3D2 = AbstractFractionSym.valueOf(-3, 2);

	/**
	 * Constant fraction &quot;1/3&quot;
	 */
	public final static IFraction C1D3 = AbstractFractionSym.valueOf(1, 3);

	/**
	 * Constant fraction &quot;-1/3&quot;
	 */
	public final static IFraction CN1D3 = AbstractFractionSym.valueOf(-1, 3);

	/**
	 * Constant fraction &quot;1/4&quot;
	 */
	public final static IFraction C1D4 = AbstractFractionSym.valueOf(1, 4);

	/**
	 * Constant fraction &quot;-1/4&quot;
	 */
	public final static IFraction CN1D4 = AbstractFractionSym.valueOf(-1, 4);

	/**
	 * Constant double &quot;1.0&quot;
	 */
	public final static Num CND1 = new Num(-1.0);

	/**
	 * Constant double &quot;0.0&quot;
	 */
	public final static Num CD0 = new Num(0.0);

	/**
	 * Constant double &quot;1.0&quot;
	 */
	public final static Num CD1 = new Num(1.0);

	/**
	 * Represents <code>List()</code> (i.e. the constant empty list)
	 */
	public static IAST CEmptyList;

	/**
	 * Represents <code>Infinity</code> (i.e. <code>Infinity-&gt;DirectedInfinity(1)</code>)
	 */
	public static IAST CInfinity;

	/**
	 * Alias for CInfinity. Represents <code>Infinity</code> (i.e. <code>Infinity-&gt;DirectedInfinity(1)</code>)
	 */
	public static IAST oo;

	/**
	 * Represents <code>-Infinity</code> (i.e. <code>-Infinity-&gt;DirectedInfinity(-1)</code>)
	 */
	public static IAST CNInfinity;

	/**
	 * Alias for CNInfinity. Represents <code>-Infinity</code> (i.e. <code>-Infinity-&gt;DirectedInfinity(-1)</code>)
	 */
	public static IAST Noo;

	/**
	 * Represents <code>I*Infinity</code> (i.e. <code>I*Infinity-&gt;DirectedInfinity(I)</code>)
	 */
	public static IAST CIInfinity;

	/**
	 * Represents <code>-I*Infinity</code> (i.e. <code>-I*Infinity-&gt;DirectedInfinity(-I)</code>)
	 */
	public static IAST CNIInfinity;

	/**
	 * Represents <code>ComplexInfinity</code> (i.e. <code>ComplexInfinity-&gt;DirectedInfinity()</code>)
	 */
	public static IAST CComplexInfinity;

	/**
	 * Represents <code>-Pi/2</code> as Symja expression <code>Times(CN1D2, Pi)</code>
	 */
	public static IAST CNPiHalf;

	/**
	 * Represents <code>Pi/2</code> as Symja expression <code>Times(C1D2, Pi)</code>
	 */
	public static IAST CPiHalf;

	/**
	 * Represents <code>Sqrt(2)</code>
	 */
	public static IAST CSqrt2;

	/**
	 * Represents <code>Sqrt(3)</code>
	 */
	public static IAST CSqrt3;

	/**
	 * Represents <code>Sqrt(5)</code>
	 */
	public static IAST CSqrt5;

	/**
	 * Represents <code>Sqrt(6)</code>
	 */
	public static IAST CSqrt6;

	/**
	 * Represents <code>Sqrt(7)</code>
	 */
	public static IAST CSqrt7;

	/**
	 * Represents <code>Sqrt(10)</code>
	 */
	public static IAST CSqrt10;

	/**
	 * Represents <code>1/Sqrt(2)</code>
	 */
	public static IAST C1DSqrt2;

	/**
	 * Represents <code>1/Sqrt(3)</code>
	 */
	public static IAST C1DSqrt3;

	/**
	 * Represents <code>1/Sqrt(5)</code>
	 */
	public static IAST C1DSqrt5;

	/**
	 * Represents <code>1/Sqrt(6)</code>
	 */
	public static IAST C1DSqrt6;

	/**
	 * Represents <code>1/Sqrt(7)</code>
	 */
	public static IAST C1DSqrt7;

	/**
	 * Represents <code>1/Sqrt(10)</code>
	 */
	public static IAST C1DSqrt10;

	/**
	 * Represents <code>#1</code>
	 */
	public static IAST Slot1;

	/**
	 * Represents <code>#2</code>
	 */
	public static IAST Slot2;

	/**
	 * Constant integer &quot;-1&quot;
	 */
	public final static IntegerSym CN1 = AbstractIntegerSym.valueOf(-1);

	/**
	 * Constant integer &quot;-2&quot;
	 */
	public final static IntegerSym CN2 = AbstractIntegerSym.valueOf(-2);

	/**
	 * Constant integer &quot;-3&quot;
	 */
	public final static IntegerSym CN3 = AbstractIntegerSym.valueOf(-3);

	/**
	 * Constant integer &quot;-4&quot;
	 */
	public final static IntegerSym CN4 = AbstractIntegerSym.valueOf(-4);

	/**
	 * Constant integer &quot;-5&quot;
	 */
	public final static IntegerSym CN5 = AbstractIntegerSym.valueOf(-5);

	/**
	 * Constant integer &quot;-6&quot;
	 */
	public final static IntegerSym CN6 = AbstractIntegerSym.valueOf(-6);

	/**
	 * Constant integer &quot;-7&quot;
	 */
	public final static IntegerSym CN7 = AbstractIntegerSym.valueOf(-7);

	/**
	 * Constant integer &quot;-8&quot;
	 */
	public final static IntegerSym CN8 = AbstractIntegerSym.valueOf(-8);

	/**
	 * Constant integer &quot;-9&quot;
	 */
	public final static IntegerSym CN9 = AbstractIntegerSym.valueOf(-9);

	/**
	 * Constant integer &quot;-10&quot;
	 */
	public final static IntegerSym CN10 = AbstractIntegerSym.valueOf(-10);

	/**
	 * Global map of predefined constant expressions.
	 */
	public final static HashMap<IExpr, ExprID> GLOBAL_IDS_MAP = new HashMap<IExpr, ExprID>(9997);

	public static Map<ISymbol, IExpr> UNARY_INVERSE_FUNCTIONS = new IdentityHashMap<ISymbol, IExpr>();

	public static ISymbol[] DENOMINATOR_NUMERATOR_SYMBOLS = null;

	public static IExpr[] DENOMINATOR_TRIG_TRUE_EXPRS = null;

	public static ISymbol[] NUMERAATOR_NUMERATOR_SYMBOLS = null;

	public static IExpr[] NUMERATOR_TRIG_TRUE_EXPRS = null;

	/**
	 * Global array of predefined constant expressions.
	 */
	static IExpr[] GLOBAL_IDS = null;

	public static java.util.concurrent.ThreadFactory THREAD_FACTORY = null;

	static Thread INIT_THREAD = null;

	/**
	 * Waits for the INIT_THREAD which initializes the Integrate() rules.
	 */
	public synchronized static void join() {
		if ((THREAD_FACTORY != null) || !Config.JAS_NO_THREADS && INIT_THREAD != null) {
			try {
				INIT_THREAD.join();
			} catch (InterruptedException e) {
			}
		}
	}

	static {
		try {
			ComputerThreads.NO_THREADS = Config.JAS_NO_THREADS;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					final EvalEngine engine = EvalEngine.get();
					ContextPath path = engine.getContextPath();
					try {
						engine.setContextPath(new ContextPath("integrate`"));
						IAST ruleList = org.matheclipse.core.reflection.system.Integrate.getUtilityFunctionsRuleAST();
						if (ruleList != null) {
							engine.addRules(ruleList);
						}
						ruleList = org.matheclipse.core.reflection.system.Integrate.getRuleASTStatic();
						if (ruleList != null) {
							engine.addRules(ruleList);
						}
					} finally {
						engine.setContextPath(path);
					}
					Integrate.setEvaluator(org.matheclipse.core.reflection.system.Integrate.CONST);
					engine.setPackageMode(false);
				}
			};

			if (THREAD_FACTORY != null) {
				INIT_THREAD = THREAD_FACTORY.newThread(runnable);
			} else {
				INIT_THREAD = new Thread(runnable);
			}

			ApfloatContext ctx = ApfloatContext.getContext();
			ctx.setNumberOfProcessors(1);
			// long start = System.currentTimeMillis();

			Slot.setAttributes(ISymbol.NHOLDALL);
			SlotSequence.setAttributes(ISymbol.NHOLDALL);
			PatternTest.setAttributes(ISymbol.HOLDALL);

			CEmptyList = headAST0(List);

			CInfinity = unaryAST1(DirectedInfinity, C1);
			oo = CInfinity;
			CNInfinity = unaryAST1(DirectedInfinity, CN1);
			Noo = CNInfinity;
			CIInfinity = unaryAST1(DirectedInfinity, CI);
			CNIInfinity = unaryAST1(DirectedInfinity, CNI);
			CComplexInfinity = headAST0(DirectedInfinity);

			CNPiHalf = binaryAST2(Times, CN1D2, Pi);
			CPiHalf = binaryAST2(Times, C1D2, Pi);

			CSqrt2 = binaryAST2(Power, C2, C1D2);
			CSqrt3 = binaryAST2(Power, C3, C1D2);
			CSqrt5 = binaryAST2(Power, C5, C1D2);
			CSqrt6 = binaryAST2(Power, C6, C1D2);
			CSqrt7 = binaryAST2(Power, C7, C1D2);
			CSqrt10 = binaryAST2(Power, C10, C1D2);

			C1DSqrt2 = binaryAST2(Power, C2, CN1D2);
			C1DSqrt3 = binaryAST2(Power, C3, CN1D2);
			C1DSqrt5 = binaryAST2(Power, C5, CN1D2);
			C1DSqrt6 = binaryAST2(Power, C6, CN1D2);
			C1DSqrt7 = binaryAST2(Power, C7, CN1D2);
			C1DSqrt10 = binaryAST2(Power, C10, CN1D2);

			Slot1 = unaryAST1(Slot, C1);
			Slot2 = unaryAST1(Slot, C2);

			GLOBAL_IDS = new IExpr[] { CN1, CN2, CN3, CN4, CN5, CN6, CN7, CN8, CN9, CN10, C0, C1, C2, C3, C4, C5, C6,
					C7, C8, C9, C10, CI, CNI, C1D2, CN1D2, C1D3, CN1D3, C1D4, CN1D4, CD0, CD1, CInfinity, CNInfinity,
					CComplexInfinity, CSqrt2, CSqrt3, CSqrt5, CSqrt6, CSqrt7, CSqrt10, C1DSqrt2, C1DSqrt3, C1DSqrt5,
					C1DSqrt6, C1DSqrt7, C1DSqrt10, Slot1, Slot2,
					// start symbols
					a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, ASymbol, BSymbol,
					CSymbol, FSymbol, GSymbol,
					// start pattern
					a_, b_, c_, d_, e_, f_, g_, h_, i_, j_, k_, l_, m_, n_, o_, p_, q_, r_, s_, t_, u_, v_, w_, x_, y_,
					z_, A_, B_, C_, F_, G_, a_Symbol, b_Symbol, c_Symbol, d_Symbol, e_Symbol, f_Symbol, g_Symbol,
					h_Symbol, i_Symbol, j_Symbol, k_Symbol, l_Symbol, m_Symbol, n_Symbol, o_Symbol, p_Symbol, q_Symbol,
					r_Symbol, s_Symbol, t_Symbol, u_Symbol, v_Symbol, w_Symbol, x_Symbol, y_Symbol, z_Symbol, a_DEFAULT,
					b_DEFAULT, c_DEFAULT, d_DEFAULT, e_DEFAULT, f_DEFAULT, g_DEFAULT, h_DEFAULT, i_DEFAULT, j_DEFAULT,
					k_DEFAULT, l_DEFAULT, m_DEFAULT, n_DEFAULT, o_DEFAULT, p_DEFAULT, q_DEFAULT, r_DEFAULT, s_DEFAULT,
					t_DEFAULT, u_DEFAULT, v_DEFAULT, w_DEFAULT, x_DEFAULT, y_DEFAULT, z_DEFAULT, A_DEFAULT, B_DEFAULT,
					C_DEFAULT, F_DEFAULT, G_DEFAULT,
					// start symbol strings
					Algebraics, Booleans, ComplexInfinity, Catalan, Complexes, Degree, EulerGamma, False, Flat,
					Glaisher, GoldenRatio, HoldAll, HoldFirst, HoldForm, HoldRest, Indeterminate, Infinity, Integer,
					Integers, Khinchin, Listable, Modulus, Null, NumericFunction, OneIdentity, Orderless, Pi, Primes,
					Rationals, Real, Reals, Slot, SlotSequence, String, F.Symbol, True,
					// start function strings
					Abs, AddTo, And, Alternatives, Apart, AppellF1, Append, AppendTo, Apply, ArcCos, ArcCosh, ArcCot,
					ArcCoth, ArcCsc, ArcCsch, ArcSec, ArcSech, ArcSin, ArcSinh, ArcTan, ArcTanh, Arg, Array,
					// ArrayDepth,
					ArrayQ, Assumptions, AtomQ, Attributes,
					// BernoulliB,
					Binomial, Blank, Block, Boole,
					// BooleanConvert,
					BooleanMinimize, Break, Cancel, CartesianProduct, Cases, CatalanNumber, Catch, Ceiling,
					CharacteristicPolynomial,
					// ChebyshevT,
					ChessboardDistance, Chop, Clear, ClearAll, Coefficient, CoefficientList, Collect, Complement,
					Complex,
					// ComplexExpand,
					ComplexInfinity, ComposeList, CompoundExpression, Condition, Conjugate, ConjugateTranspose,
					ConstantArray, Continue, ContinuedFraction, CoprimeQ, Cos, Cosh, CosIntegral, CoshIntegral, Cot,
					Coth, Count, Cross, Csc, Csch, Curl, Decrement, Default, Defer, Definition, Delete, DeleteCases,
					// DeleteDuplicates,
					Denominator, Depth, Derivative, Det, DiagonalMatrix, DigitQ, Dimensions, DirectedInfinity,
					Discriminant, Distribute, Divergence, DivideBy, Divisible,
					// Divisors,
					Do, Dot, Drop, Eigenvalues, Eigenvectors, Element,
					// Eliminate,
					EllipticE, EllipticF, EllipticPi, Equal, Equivalent, Erf, Erfc, Erfi, EuclideanDistance,
					// EulerE,
					EulerPhi, EvenQ, Exp, Expand, ExpandAll, ExpIntegralE, ExpIntegralEi, Exponent, ExtendedGCD,
					Extract, Factor, Factorial, Factorial2, FactorInteger, FactorSquareFree, FactorSquareFreeList,
					FactorTerms, Flatten, Fibonacci, FindRoot, First, Fit, FixedPoint, Floor, Fold, FoldList, For,
					FractionalPart, FreeQ, FresnelC, FresnelS, FrobeniusSolve, FromCharacterCode, FromContinuedFraction,
					FullForm, FullSimplify, Function, Gamma, GCD, GeometricMean, Graphics, Graphics3D, Graphics3D,
					Greater, GreaterEqual, GroebnerBasis, HarmonicNumber, Head,
					// HermiteH,
					HilbertMatrix, Hold, HoldForm, Horner,
					// HornerForm,
					HurwitzZeta, HypergeometricPFQ, Hypergeometric2F1, Identity, IdentityMatrix, If, Im, Implies,
					Increment, Inner, Insert, IntegerPart, IntegerPartitions, IntegerQ, Integrate,
					// InterpolatingFunction, InterpolatingPolynomial,
					Intersection, Inverse, InverseErf, InverseFunction, JacobiMatrix, JacobiSymbol, JavaForm, Join,
					KOrderlessPartitions, KPartitions, LaplaceTransform, Last, LCM, LeafCount,
					// LaguerreL, LegendreP,
					Length, Less, LessEqual, LetterQ, Level, Limit, Line, LinearProgramming, LinearSolve, List, ListQ,
					Log,
					// Log2, Log10,
					LogGamma,
					// LogicalExpand,
					LogIntegral, LowerCaseQ, LUDecomposition, ManhattanDistance, Map, MapAll, MapThread, MatchQ,
					MathMLForm,
					// MatrixForm,
					MatrixPower, MatrixQ,
					// MatrixRank,
					Max, Mean, Median, MemberQ, Min, Mod, Module, MoebiusMu,
					// MonomialList,
					Most, Multinomial, Nand, Negative, Nest, NestList, NestWhile, NestWhileList, NextPrime,
					NFourierTransform, NIntegrate,
					// NMaximize, NMinimize,
					NonCommutativeMultiply, NonNegative, Nor, Norm, Not, NRoots, NSolve,
					// NullSpace,
					NumberQ, Numerator, NumericQ, OddQ, Options, Or, Order, OrderedQ, Out, Outer, PadLeft, PadRight,
					// ParametricPlot,
					Part, Partition, Pattern, Permutations, Piecewise, Plot, Plot3D, Plus,
					// Pochhammer,
					PolyGamma, PolyLog, PolynomialExtendedGCD, PolynomialGCD, PolynomialLCM, PolynomialQ,
					PolynomialQuotient, PolynomialQuotientRemainder, PolynomialRemainder, Position, Positive,
					PossibleZeroQ, Power, PowerExpand, PowerMod, PreDecrement, PreIncrement, Prepend, PrependTo,
					// Prime,
					PrimeQ, PrimitiveRootList, Print, Product, ProductLog, Quiet, Quotient, RandomInteger, RandomReal,
					// RandomSample,
					Range, Rational, Rationalize, Re, Reap, Refine, ReplaceAll, ReplacePart, ReplaceRepeated, Rest,
					Resultant, Return, Reverse, Riffle, RootIntervals, RootOf, Roots, Surd, RotateLeft, RotateRight,
					Round,
					// RowReduce,
					Rule, RuleDelayed, SameQ, Scan, Sec, Sech, Select, Sequence, Set, SetAttributes, SetDelayed, Show,
					Sign, SignCmp, Simplify, Sin, Sinc, SingularValueDecomposition, Sinh, SinIntegral, SinhIntegral,
					Solve, Sort, Sow, Sqrt, SquaredEuclideanDistance, SquareFreeQ, StirlingS2, StringDrop, StringJoin,
					StringLength, StringTake, Subfactorial, Subscript, Subsuperscript, Subsets, SubtractFrom, Sum,
					Superscript, Switch, SyntaxLength, SyntaxQ, Table, Take, Tan, Tanh, Taylor, TeXForm, Thread,
					Through, Throw, TimeConstrained, Times, TimesBy, Timing, ToCharacterCode, Together, ToString, Total,
					ToUnicode, Tr, Trace, Transpose, TrigExpand, TrigReduce, TrigToExp, TrueQ,
					// Tuples,
					Unequal, Unevaluated, Union, Unique, UnitStep,
					// UnitVector,
					UnsameQ, UpperCaseQ, UpSet, UpSetDelayed, ValueQ, VandermondeMatrix, Variables, VectorQ, Which,
					While, Xor,
					// Zeta
					NIL };

			for (short i = 0; i < GLOBAL_IDS.length; i++) {
				GLOBAL_IDS_MAP.put(GLOBAL_IDS[i], new ExprID(i));
			}

			PREDEFINED_INTERNAL_FORM_STRINGS.put("Pi", "Pi");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("E", "E");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("False", "False");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("True", "True");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("Null", "Null");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("Integer", "IntegerHead");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("Symbol", "SymbolHead");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("Infinity", "CInfinity");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("ComplexInfinity", "CComplexInfinity");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("Plus", "Plus");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("Power", "Power");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("Times", "Times");

			Arithmetic.initialize();
			PredicateQ.initialize();
			AttributeFunctions.initialize();

			createInverseFunctionMap();
			createDenominatorFunctionMap();
			createNumeratorFunctionMap();

			ConstantDefinitions.initialize();
			FunctionDefinitions.initialize();
			Programming.initialize();
			PatternMatching.initialize();
			Algebra.initialize();
			Structure.initialize();
			ExpTrigsFunctions.initialize();
			NumberTheory.initialize();
			BooleanFunctions.initialize();
			LinearAlgebra.initialize();
			TensorFunctions.initialize();
			ListFunctions.initialize();
			Combinatoric.initialize();
			IntegerFunctions.initialize();
			SpecialFunctions.initialize();
			StringFunctions.initialize();
			OutputFunctions.initialize();
			RandomFunctions.initialize();
			StatisticsFunctions.initialize();
			HypergeometricFunctions.initialize();
			EllipticIntegrals.initialize();
			PolynomialFunctions.initialize();
			SeriesFunctions.initialize();
			AssumptionFunctions.initialize();
			ComputationalGeometryFunctions.initialize();

			// initialize only the utility function rules for Integrate
			// final EvalEngine engine = EvalEngine.get();
			// IAST ruleList =
			// org.matheclipse.core.reflection.system.Integrate.getUtilityFunctionsRuleAST();
			// if (ruleList != null) {
			// engine.addRules(ruleList);
			// }
			if (Config.JAS_NO_THREADS) {
				// explicitly invoke run() because no threads should be spawned
				INIT_THREAD.run();
			} else {
				INIT_THREAD.start();
			}
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	private static void createNumeratorFunctionMap() {
		NUMERAATOR_NUMERATOR_SYMBOLS = new ISymbol[6];
		NUMERAATOR_NUMERATOR_SYMBOLS[0] = Sin;
		NUMERAATOR_NUMERATOR_SYMBOLS[1] = Cos;
		NUMERAATOR_NUMERATOR_SYMBOLS[2] = Tan;
		NUMERAATOR_NUMERATOR_SYMBOLS[3] = Csc;
		NUMERAATOR_NUMERATOR_SYMBOLS[4] = Sec;
		NUMERAATOR_NUMERATOR_SYMBOLS[5] = Cot;
		NUMERATOR_TRIG_TRUE_EXPRS = new IExpr[6];
		NUMERATOR_TRIG_TRUE_EXPRS[0] = Sin;
		NUMERATOR_TRIG_TRUE_EXPRS[1] = Cos;
		NUMERATOR_TRIG_TRUE_EXPRS[2] = Sin;
		NUMERATOR_TRIG_TRUE_EXPRS[3] = C1;
		NUMERATOR_TRIG_TRUE_EXPRS[4] = C1;
		NUMERATOR_TRIG_TRUE_EXPRS[5] = Cos;
	}

	private static void createDenominatorFunctionMap() {
		DENOMINATOR_NUMERATOR_SYMBOLS = new ISymbol[6];
		DENOMINATOR_NUMERATOR_SYMBOLS[0] = F.Sin;
		DENOMINATOR_NUMERATOR_SYMBOLS[1] = F.Cos;
		DENOMINATOR_NUMERATOR_SYMBOLS[2] = F.Tan;
		DENOMINATOR_NUMERATOR_SYMBOLS[3] = F.Csc;
		DENOMINATOR_NUMERATOR_SYMBOLS[4] = F.Sec;
		DENOMINATOR_NUMERATOR_SYMBOLS[5] = F.Cot;
		DENOMINATOR_TRIG_TRUE_EXPRS = new IExpr[6];
		DENOMINATOR_TRIG_TRUE_EXPRS[0] = F.C1;
		DENOMINATOR_TRIG_TRUE_EXPRS[1] = F.C1;
		DENOMINATOR_TRIG_TRUE_EXPRS[2] = F.Cos;
		DENOMINATOR_TRIG_TRUE_EXPRS[3] = F.Sin;
		DENOMINATOR_TRIG_TRUE_EXPRS[4] = F.Cos;
		DENOMINATOR_TRIG_TRUE_EXPRS[5] = F.Sin;
	}

	private static void createInverseFunctionMap() {
		UNARY_INVERSE_FUNCTIONS.put(Abs, Function(Times(CN1, Slot1)));
		UNARY_INVERSE_FUNCTIONS.put(Cos, ArcCos);
		UNARY_INVERSE_FUNCTIONS.put(Cot, ArcCot);
		UNARY_INVERSE_FUNCTIONS.put(Csc, ArcCsc);
		UNARY_INVERSE_FUNCTIONS.put(Sec, ArcSec);
		UNARY_INVERSE_FUNCTIONS.put(Sin, ArcSin);
		UNARY_INVERSE_FUNCTIONS.put(Tan, ArcTan);

		UNARY_INVERSE_FUNCTIONS.put(ArcCos, Cos);
		UNARY_INVERSE_FUNCTIONS.put(ArcCot, Cot);
		UNARY_INVERSE_FUNCTIONS.put(ArcCsc, Csc);
		UNARY_INVERSE_FUNCTIONS.put(ArcSec, Sec);
		UNARY_INVERSE_FUNCTIONS.put(ArcSin, Sin);
		UNARY_INVERSE_FUNCTIONS.put(ArcTan, Tan);
		UNARY_INVERSE_FUNCTIONS.put(Cosh, ArcCosh);
		UNARY_INVERSE_FUNCTIONS.put(Coth, ArcCoth);
		UNARY_INVERSE_FUNCTIONS.put(Csch, ArcCsch);
		UNARY_INVERSE_FUNCTIONS.put(Sech, ArcSech);
		UNARY_INVERSE_FUNCTIONS.put(Sinh, ArcSinh);
		UNARY_INVERSE_FUNCTIONS.put(Tanh, ArcTanh);
		UNARY_INVERSE_FUNCTIONS.put(ArcCosh, Cosh);
		UNARY_INVERSE_FUNCTIONS.put(ArcCoth, Coth);
		UNARY_INVERSE_FUNCTIONS.put(ArcCsch, Csch);
		UNARY_INVERSE_FUNCTIONS.put(ArcSech, Sech);
		UNARY_INVERSE_FUNCTIONS.put(ArcSinh, Sinh);
		UNARY_INVERSE_FUNCTIONS.put(ArcTanh, Tanh);
		UNARY_INVERSE_FUNCTIONS.put(Log, Exp);
	}

	/**
	 * Create a new abstract syntax tree (AST).
	 * 
	 * @param head
	 *            the header expression of the function. If the ast represents a function like
	 *            <code>f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
	 * @param a
	 * @return
	 */
	public final static IAST $(final IExpr head, final IExpr... a) {
		return ast(a, head);
	}

	/**
	 * Create a new abstract syntax tree (AST).
	 * 
	 * @param head
	 *            the header symbol of the function. If the ast represents a function like
	 *            <code>f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
	 * @param a
	 * @return
	 */
	public final static IAST function(final ISymbol head, final IExpr... a) {
		return ast(a, head);
	}

	/**
	 * Create a <code>Blank[]</code> pattern object for pattern-matching and term rewriting
	 * 
	 * @return IPattern
	 */
	public static IPattern $b() {
		return org.matheclipse.core.expression.Blank.valueOf();
	}

	/**
	 * Create a <code>Blank[condition]</code> pattern object for pattern-matching and term rewriting
	 * 
	 * @param condition
	 *            additional condition which should be checked in pattern-matching
	 * @return IPattern
	 */
	public static IPattern $b(final IExpr condition) {
		return org.matheclipse.core.expression.Blank.valueOf(condition);
	}

	/**
	 * Create a <code>Blank[condition]</code> pattern object for pattern-matching and term rewriting
	 * 
	 * @param condition
	 *            additional condition which should be checked in pattern-matching
	 * @param def
	 *            if <code>true</code> use a default value in pattern-matching if an argument is optional
	 * @return IPattern
	 */
	public static IPattern $b(final IExpr condition, boolean def) {
		return new org.matheclipse.core.expression.Blank(condition, def);
	}

	/**
	 * Create a <code>Blank[condition]</code> pattern object for pattern-matching and term rewriting
	 * 
	 * @param condition
	 *            additional condition which should be checked in pattern-matching
	 * @param defaultValue
	 *            use this <code>defaultValue</code> in pattern-matching if an argument is optional
	 * @return IPattern
	 */
	public static IPattern $b(final IExpr condition, IExpr defaultValue) {
		return new org.matheclipse.core.expression.Blank(condition, defaultValue);
	}

	/**
	 * Create a <code>Pattern[]</code> pattern for pattern-matching and term rewriting
	 * 
	 * @param symbol
	 * @return IPattern
	 */
	public static IPattern $p(@Nonnull final ISymbol symbol) {
		return org.matheclipse.core.expression.Pattern.valueOf(symbol);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbol
	 * @param def
	 *            use a default value for this pattern if necessary
	 * @return IPattern
	 */
	public static IPattern $p(final ISymbol symbol, boolean def) {
		return $p(symbol, null, def);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbol
	 * @param check
	 *            additional condition which should be checked in pattern-matching
	 * @return IPattern
	 */
	public static IPattern $p(final ISymbol symbol, final IExpr check) {
		return org.matheclipse.core.expression.Pattern.valueOf(symbol, check);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbol
	 * @param check
	 *            additional condition which should be checked in pattern-matching
	 * @param def
	 *            if <code>true</code>, the pattern can match to a default value associated with the AST's head the
	 *            pattern is used in.
	 * @return IPattern
	 */
	public static IPattern $p(final ISymbol symbol, final IExpr check, final boolean def) {
		return org.matheclipse.core.expression.Pattern.valueOf(symbol, check, def);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbol
	 * @param check
	 *            additional condition which should be checked in pattern-matching
	 * @param defaultValue
	 *            use this <code>defaultValue</code> in pattern-matching if an argument is optional
	 * @return IPattern
	 */
	public static IPattern $p(final ISymbol symbol, final IExpr check, final IExpr defaultValue) {
		return org.matheclipse.core.expression.Pattern.valueOf(symbol, check, defaultValue);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbolName
	 * @return IPattern
	 */
	public static IPattern $p(@Nonnull final String symbolName) {
		// if (symbolName == null) {
		// return org.matheclipse.core.expression.Pattern.valueOf(null);
		// }
		return org.matheclipse.core.expression.Pattern.valueOf($s(symbolName));
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbolName
	 * @param def
	 *            use a default value for this pattern if necessary
	 * @return IPattern
	 */
	public static IPattern $p(final String symbolName, boolean def) {
		return $p($s(symbolName), null, def);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbolName
	 * @param check
	 *            additional condition which should be checked in pattern-matching
	 * @return IPattern
	 */
	public static IPattern $p(@Nonnull final String symbolName, final IExpr check) {
		// if (symbolName == null) {
		// return org.matheclipse.core.expression.Pattern.valueOf(null, check);
		// }
		return org.matheclipse.core.expression.Pattern.valueOf($s(symbolName), check);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbolName
	 * @param check
	 *            additional condition which should be checked in pattern-matching
	 * @param def
	 *            use a default value for this pattern if necessary
	 * @return IPattern
	 */
	public static IPattern $p(@Nonnull final String symbolName, final IExpr check, boolean def) {
		return org.matheclipse.core.expression.Pattern.valueOf($s(symbolName), check, def);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbol
	 * @return IPattern
	 */
	public static IPattern pattern(final ISymbol symbol) {
		return org.matheclipse.core.expression.Pattern.valueOf(symbol);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbol
	 * @param check
	 *            additional condition which should be checked in pattern-matching
	 * @param def
	 *            if <code>true</code>, the pattern can match to a default value associated with the AST's head the
	 *            pattern is used in.
	 * @return IPattern
	 */
	public static IPattern pattern(final ISymbol symbol, final IExpr check, final boolean def) {
		return org.matheclipse.core.expression.Pattern.valueOf(symbol, check, def);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbol
	 * @param check
	 *            additional condition which should be checked in pattern-matching
	 * @param defaultValue
	 *            use this <code>defaultValue</code> in pattern-matching if an argument is optional
	 * @return IPattern
	 */
	public static IPattern pattern(final ISymbol symbol, final IExpr check, final IExpr defaultValue) {
		return org.matheclipse.core.expression.Pattern.valueOf(symbol, check, defaultValue);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbolName
	 * @param check
	 *            additional condition which should be checked in pattern-matching
	 * @param defaultValue
	 *            use this <code>defaultValue</code> in pattern-matching if an argument is optional
	 * @return IPattern
	 */
	public static IPattern $p(@Nonnull final String symbolName, final IExpr check, final IExpr defaultValue) {
		return org.matheclipse.core.expression.Pattern.valueOf($s(symbolName), check, defaultValue);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbol
	 * @return IPattern
	 */
	public static IPatternSequence $ps(final ISymbol symbol) {
		return PatternSequence.valueOf(symbol);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbol
	 * @param check
	 *            additional condition which should be checked in pattern-matching
	 * @return IPattern
	 */
	public static IPatternSequence $ps(final ISymbol symbol, final IExpr check) {
		return PatternSequence.valueOf(symbol, check);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbol
	 * @param check
	 *            additional condition which should be checked in pattern-matching
	 * @param def
	 *            if <code>true</code>, the pattern can match to a default value associated with the AST's head the
	 *            pattern is used in.
	 * @param zeroArgsAllowed
	 *            if <code>true</code> 0 argument sequences are allowed for this pattern
	 * @return IPattern
	 */
	public static IPatternSequence $ps(final ISymbol symbol, final IExpr check, final boolean def,
			boolean zeroArgsAllowed) {
		return PatternSequence.valueOf(symbol, check, def, zeroArgsAllowed);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbolName
	 *            the name of the pattrn symbol
	 * @return IPattern
	 */
	public static IPatternSequence $ps(final String symbolName) {
		return PatternSequence.valueOf($s(symbolName));
	}

	/**
	 * <p>
	 * Get or create a global predefined symbol which is retrieved from the SYSTEM context map or created or retrieved
	 * from the SYSTEM context variables map.
	 * </p>
	 * <p>
	 * <b>Note:</b> user defined variables on the context path are defined with method <code>userSymbol()</code>
	 * </p>
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @return
	 */
	public static ISymbol $s(final String symbolName) {
		return $s(symbolName, true);
	}

	/**
	 * Converts an arbitrary expression to a type that can be used inside Symja.
	 * 
	 * For example, it will convert Java <code>Integer</code> into instance of <code>IntegerSym</code>,
	 * <code>Double</code> into instances of <code>Num</code>, etc.
	 * 
	 * 
	 * @param object
	 * @return
	 */
	public static IExpr symjify(final Object object) {
		return Object2Expr.convert(object);
	}

	/**
	 * <p>
	 * Get or create a global predefined symbol which is retrieved from the SYSTEM context map or created or retrieved
	 * from the SYSTEM context variables map.
	 * </p>
	 * <p>
	 * <b>Note:</b> user defined variables on the context path are defined with method <code>userSymbol()</code>
	 * </p>
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @param setEval
	 *            if <code>true</code> determine and assign the built-in evaluator object to the symbol.
	 * @return
	 */
	private static ISymbol $s(final String symbolName, boolean setEval) {
		String name = symbolName;
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			if (symbolName.length() == 1) {
				name = symbolName;
			} else {
				name = symbolName.toLowerCase(Locale.ENGLISH);
			}
		}
		ISymbol symbol = Context.PREDEFINED_SYMBOLS_MAP.get(name);
		if (symbol != null) {
			return symbol;
		}
		symbol = HIDDEN_SYMBOLS_MAP.get(name);
		if (symbol != null) {
			return symbol;
		}
		if (Config.SERVER_MODE) {
			if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
				if (SYMBOL_OBSERVER.createPredefinedSymbol(name)) {
					// second try, because the symbol may now be added to
					// fSymbolMap
					ISymbol secondTry = Context.PREDEFINED_SYMBOLS_MAP.get(name);
					if (secondTry != null) {
						return secondTry;
					}
				}
			} else {
				if (Character.isUpperCase(name.charAt(0))) {
					if (SYMBOL_OBSERVER.createPredefinedSymbol(name)) {
						// second try, because the symbol may now be added to
						// fSymbolMap
						ISymbol secondTry = Context.PREDEFINED_SYMBOLS_MAP.get(name);
						if (secondTry != null) {
							return secondTry;
						}
					}
				}
			}
			// symbol = new BuiltInSymbol(name);
			symbol = symbol(name, EvalEngine.get());
			// engine.putUserVariable(name, symbol);
			HIDDEN_SYMBOLS_MAP.put(name, symbol);
			if (name.charAt(0) == '$') {
				SYMBOL_OBSERVER.createUserSymbol(symbol);
			}
		} else {
			// symbol = new BuiltInSymbol(name);
			symbol = symbol(name);
			HIDDEN_SYMBOLS_MAP.put(name, symbol);
			// if (symbol.isBuiltInSymbol()) {
			// if (!setEval) {
			// ((IBuiltInSymbol) symbol).setEvaluator(BuiltInSymbol.DUMMY_EVALUATOR);
			// } else {
			// ((IBuiltInSymbol) symbol).getEvaluator();
			// }
			// }
		}

		return symbol;
	}

	/**
	 * Create a string expression
	 * 
	 * @param str
	 * @return
	 */
	final static public IStringX $str(final String str) {
		return StringX.valueOf(str);
	}

	// --- generated source codes:
	public static IAST Abs(final IExpr a0) {
		return unaryAST1(Abs, a0);
	}

	public static IAST Alternatives(final IExpr... a) {
		return ast(a, Alternatives);
	}

	public static IExpr and(IExpr a, Integer i) {
		return And(a, integer(i.longValue()));
	}

	public static IExpr and(IExpr a, java.math.BigInteger i) {
		return And(a, integer(i));
	}

	public static IExpr and(Integer i, IExpr b) {
		return And(integer(i.longValue()), b);
	}

	public static IExpr and(java.math.BigInteger i, IExpr b) {
		return And(integer(i), b);
	}

	public static IASTAppendable And() {
		return ast(And);
	}

	public static IAST And(final IExpr... a) {
		return ast(a, And);
	}

	public static IAST AngleVector(final IExpr a0) {
		return unaryAST1(AngleVector, a0);
	}

	public static IAST And(final IExpr a0, final IExpr a1) {
		return binaryAST2(And, a0, a1);
	}

	public static IAST Apart(final IExpr a0) {
		return unaryAST1(Apart, a0);
	}

	public static IAST Apart(final IExpr a0, final IExpr a1) {
		return binaryAST2(Apart, a0, a1);
	}

	public static IAST AppellF1(final IExpr... a) {
		return ast(a, AppellF1);
	}

	public static IAST Append(final IExpr a0, final IExpr a1) {
		return binaryAST2(Append, a0, a1);
	}

	public static IAST AppendTo(final IExpr a0, final IExpr a1) {
		return binaryAST2(AppendTo, a0, a1);
	}

	public static IAST Apply(final IExpr a0, final IExpr a1) {
		return binaryAST2(Apply, a0, a1);
	}

	public static IAST ArcCos(final IExpr a0) {
		return unaryAST1(ArcCos, a0);
	}

	public static IAST ArcCosh(final IExpr a0) {
		return unaryAST1(ArcCosh, a0);
	}

	public static IAST ArcCot(final IExpr a0) {
		return unaryAST1(ArcCot, a0);
	}

	public static IAST ArcCoth(final IExpr a0) {
		return unaryAST1(ArcCoth, a0);
	}

	public static IAST ArcCsc(final IExpr a0) {
		return unaryAST1(ArcCsc, a0);
	}

	public static IAST ArcCsch(final IExpr a0) {
		return unaryAST1(ArcCsch, a0);
	}

	public static IAST ArcSec(final IExpr a0) {
		return unaryAST1(ArcSec, a0);
	}

	public static IAST ArcSech(final IExpr a0) {
		return unaryAST1(ArcSech, a0);
	}

	public static IAST ArcSin(final IExpr a0) {

		return unaryAST1(ArcSin, a0);
	}

	public static IAST ArcSinh(final IExpr a0) {
		return unaryAST1(ArcSinh, a0);
	}

	public static IAST ArcTan(final IExpr a0) {
		return unaryAST1(ArcTan, a0);
	}

	public static IAST ArcTan(final IExpr a0, final IExpr a1) {
		return binaryAST2(ArcTan, a0, a1);
	}

	public static IAST ArcTanh(final IExpr a0) {
		return unaryAST1(ArcTanh, a0);
	}

	public static IAST Arg(final IExpr a0) {
		return unaryAST1(Arg, a0);
	}

	/**
	 * Creates a new AST from the given <code>ast</code> and <code>head</code>. if <code>include</code> is set to
	 * <code>true </code> all arguments from index first to last-1 are copied in the new list if <code>include</code> is
	 * set to <code> false </code> all arguments excluded from index first to last-1 are copied in the new list
	 * 
	 */
	public static IAST ast(final IAST f, final IExpr head, final boolean include, final int first, final int last) {
		AST ast = null;
		if (include) {
			ast = AST.newInstance(last - first, head);
			// range include
			for (int i = first; i < last; i++) {
				ast.append(f.get(i));
			}
		} else {
			ast = AST.newInstance(f.size() - last + first - 1, head);
			// range exclude
			for (int i = 1; i < first; i++) {
				ast.append(f.get(i));
			}
			for (int j = last; j < f.size(); j++) {
				ast.append(f.get(j));
			}
		}
		return ast;
	}

	/**
	 * Create a new abstract syntax tree (AST).
	 * 
	 * @param head
	 *            the header expression of the function. If the ast represents a function like
	 *            <code>f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
	 * 
	 */
	public final static IASTAppendable ast(final IExpr head) {
		return AST.newInstance(head);
	}

	/**
	 * Create a new abstract syntax tree (AST).
	 * 
	 * @param head
	 *            the header expression of the function. If the ast represents a function like
	 *            <code>f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
	 * @param initialCapacity
	 *            the initial capacity (i.e. number of arguments without the header element) of the list.
	 * @param initNull
	 *            initialize all elements with <code>null</code>.
	 * @return
	 */
	public static IASTAppendable ast(final IExpr head, final int initialCapacity, final boolean initNull) {
		final AST ast = AST.newInstance(initialCapacity, head);
		if (initNull) {
			for (int i = 0; i < initialCapacity; i++) {
				ast.append(null);
			}
		}
		return ast;
	}

	/**
	 * Create a new <code>List()</code> with <code>copies</code> number of arguments, which are set to
	 * <code>value</code>.
	 * 
	 * @param value
	 *            initialize all elements with <code>value</code>.
	 * @param copies
	 *            the initial capacity (i.e. number of arguments without the header element) of the list.
	 * @return
	 */
	public static IASTAppendable constantArray(final IExpr value, final int copies) {
		return value.constantArray(F.List, 0, copies);
	}

	/**
	 * Create a new abstract syntax tree (AST) with a <code>head</code> and <code>copies</code> number of arguments,
	 * which are set to <code>value</code>.
	 * 
	 * @param head
	 *            the header expression of the function. If the ast represents a function like
	 *            <code>f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
	 * @param value
	 *            initialize all elements with <code>value</code>.
	 * @param copies
	 *            the initial capacity (i.e. number of arguments without the header element) of the list.
	 * @return
	 */
	public static IASTAppendable constantArray(final IExpr head, final IExpr value, final int copies) {
		return value.constantArray(head, 0, copies);
	}

	/**
	 * Create a new abstract syntax tree (AST).
	 * 
	 * @param arr
	 * @param head
	 *            the header expression of the function. If the ast represents a function like
	 *            <code>f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
	 * @return
	 */
	public static IASTAppendable ast(final IExpr[] arr, final IExpr head) {
		return new AST(head, arr);
	}

	public static IAST AtomQ(final IExpr a) {
		return unaryAST1(AtomQ, a);
	}

	/**
	 * Bell number.
	 * 
	 * @param a0
	 * @return
	 */
	public static IAST BellB(final IExpr a0) {
		return unaryAST1(F.BellB, a0);
	}

	/**
	 * Bell polynomial.
	 * 
	 * @param a0
	 * @param a1
	 * @return
	 */
	public static IAST BellB(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.BellB, a0, a1);
	}

	public static IAST BernoulliB(final IExpr a0) {
		return unaryAST1(F.BernoulliB, a0);
	}

	public static IAST BernoulliDistribution(final IExpr a0) {
		return unaryAST1(F.BernoulliDistribution, a0);
	}

	/**
	 * Create a function with 2 arguments without evaluation.
	 * 
	 * @param head
	 * @param a0
	 * @param a1
	 * @return
	 */
	public final static IASTAppendable binary(final IExpr head, final IExpr a0, final IExpr a1) {
		return new AST(new IExpr[] { head, a0, a1 });
	}

	/**
	 * Create a function with 2 arguments as a <code>AST2</code> immutable object without evaluation.
	 * 
	 * @param head
	 * @param a0
	 * @param a1
	 * @return
	 */
	public final static IASTMutable binaryAST2(final IExpr head, final IExpr a0, final IExpr a1) {
		return new AST2(head, a0, a1);
	}

	public static IAST Binomial(final IExpr a0, final IExpr a1) {
		return binaryAST2(F.Binomial, a0, a1);
	}

	public static IAST Block(final IExpr a0, final IExpr a1) {
		return binaryAST2(Block, a0, a1);
	}

	/**
	 * Gives symbols "True" or "False" (type ISymbol) depending on the boolean value.
	 * 
	 * @param value
	 * @return
	 */
	public static ISymbol bool(final boolean value) {
		return value ? True : False;
	}

	public static IAST BesselI(final IExpr a0, final IExpr a1) {
		return binaryAST2(BesselI, a0, a1);
	}

	public static IAST BesselJ(final IExpr a0, final IExpr a1) {
		return binaryAST2(BesselJ, a0, a1);
	}

	public static IAST BesselY(final IExpr a0, final IExpr a1) {
		return binaryAST2(BesselY, a0, a1);
	}

	public static IAST BesselK(final IExpr a0, final IExpr a1) {
		return binaryAST2(BesselK, a0, a1);
	}

	public static IAST Beta(final IExpr a0, final IExpr a1) {
		return binaryAST2(Beta, a0, a1);
	}

	public static IAST BetaRegularized(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(BetaRegularized, a0, a1, a2);
	}

	public static IAST Break() {
		return headAST0(Break);
	}

	public static IAST Cancel(final IExpr a) {
		return unaryAST1(Cancel, a);
	}

	public static IAST CarmichaelLambda(final IExpr a0) {
		return unaryAST1(CarmichaelLambda, a0);
	}

	/**
	 * Converts a given object into a MathEclipse IExpr expression
	 * 
	 * <pre>
	 * Java Object     -&gt; MathEclipse object
	 * -------------------------------------
	 * null object          Null symbol
	 * IExpr                IExpr type
	 * Boolean              True or False symbol
	 * BigInteger           Integer value  
	 * java.math.BigInteger Integer value  
	 * BigDecimal           Double with doubleValue() value
	 * Double               Double with doubleValue() value
	 * Float                Double with doubleValue() value
	 * Number               Integer with longValue() value
	 * java.util.List       0-th element of the list gives the head of the function 
	 *                      1..nth element of the list give the arguments of the function
	 * Object[]             a list of converted objects  
	 * int[]                a list of Integer values
	 * double[]             a list of Double values
	 * double[][]           a matrix (i.e. nested lists) of Double values
	 * boolean[]            a list of True or False symbols
	 * 
	 * </pre>
	 * 
	 * @param obj
	 * @return
	 */
	public static IExpr cast(Object obj) {
		return Object2Expr.convert(obj);
	}

	public static IAST CatalanNumber(final IExpr a) {
		return unaryAST1(CatalanNumber, a);
	}

	public static IAST Catch(final IExpr a) {
		return unaryAST1(Catch, a);
	}

	/**
	 * Create a symbolic complex number
	 * 
	 * @param re
	 * @return
	 */
	public static IComplex CC(final IFraction re) {
		return complex(re, fraction(0L, 1L));
	}

	/**
	 * Create a symbolic complex number
	 * 
	 * @param re
	 * @param im
	 * @return
	 */
	public static IComplex CC(final IFraction re, final IFraction im) {
		return ComplexSym.valueOf(re, im);
	}

	/**
	 * Create a symbolic complex number
	 * 
	 * @param re
	 * @param im
	 * @return
	 */
	public static IComplex CC(final long real_numerator, final long real_denominator, final long imag_numerator,
			final long imag_denominator) {
		return ComplexSym.valueOf(real_numerator, real_denominator, imag_numerator, imag_denominator);
	}

	public static IAST CDF(final IExpr a0) {
		return unaryAST1(CDF, a0);
	}

	public static IAST CDF(final IExpr a0, final IExpr a1) {
		return binaryAST2(CDF, a0, a1);
	}

	public static IAST Ceiling(final IExpr a0) {
		return unaryAST1(Ceiling, a0);
	}

	public static IAST ChebyshevT(final IExpr a0, final IExpr a1) {
		return binaryAST2(ChebyshevT, a0, a1);
	}

	public static IAST ChebyshevU(final IExpr a0, final IExpr a1) {
		return binaryAST2(ChebyshevU, a0, a1);
	}

	public static IAST Chop(final IExpr a0) {
		return unaryAST1(Chop, a0);
	}

	public static IExpr chopExpr(IExpr arg, double delta) {
		if (arg.isNumber()) {
			return chopNumber((INumber) arg, delta);
		}
		return arg;
	}

	/**
	 * Set real or imaginary parts of a numeric argument to zero, those absolute value is less than a delta.
	 * 
	 * @param arg
	 *            a numeric number
	 * @param delta
	 *            the delta for which
	 * @return <code>arg</code> if the argument couldn't be chopped
	 */
	public static INumber chopNumber(INumber arg, double delta) {
		if (arg instanceof INum) {
			if (isZero(((INum) arg).getRealPart(), delta)) {
				return C0;
			}
		} else if (arg instanceof IComplexNum) {
			if (isZero(((IComplexNum) arg).getRealPart(), delta)) {
				if (isZero(((IComplexNum) arg).getImaginaryPart(), delta)) {
					return C0;
				}
				return complexNum(0.0, ((IComplexNum) arg).getImaginaryPart());
			}
			if (isZero(((IComplexNum) arg).getImaginaryPart(), delta)) {
				return num(((IComplexNum) arg).getRealPart());
			}

		}
		return arg;
	}

	public static IAST CentralMoment(final IExpr a0, final IExpr a1) {
		return binaryAST2(CentralMoment, a0, a1);
	}

	public static IAST Clear(final IExpr... a) {
		return ast(a, Clear);
	}

	public static IAST ClearAttributes(final IExpr a0, final IExpr a1) {
		return binaryAST2(ClearAttributes, a0, a1);
	}

	public static IAST CNInfinity() {
		return binaryAST2(Times, CN1, Infinity);
	}

	public static IAST Coefficient(final IExpr a0, final IExpr a1) {
		return binaryAST2(Coefficient, a0, a1);
	}

	public static IAST Coefficient(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Coefficient, a0, a1, a2);
	}

	public static IAST CoefficientList(final IExpr a0, final IExpr a1) {
		return binaryAST2(CoefficientList, a0, a1);
	}

	public static IAST Collect(final IExpr a0, final IExpr a1) {
		return binaryAST2(Collect, a0, a1);
	}

	public static IAST Colon(final IExpr a0, final IExpr a1) {
		return binaryAST2(Colon, a0, a1);
	}

	public static int compareTo(IExpr a, IExpr b) throws UnsupportedOperationException {
		if (a instanceof ISignedNumber && b instanceof ISignedNumber) {
			return a.compareTo(b);
		}
		IExpr tempA = eval(a);
		IExpr tempB = eval(b);
		if (tempA instanceof ISignedNumber && tempB instanceof ISignedNumber) {
			return tempA.compareTo(tempB);
		}
		throw new UnsupportedOperationException(
				"compareTo() - first or second argument could not be converted into a signed number.");
	}

	public static int compareTo(IExpr a, Integer i) throws UnsupportedOperationException {
		if (a instanceof ISignedNumber) {
			return a.compareTo(integer(i.longValue()));
		}
		IExpr temp = eval(a);
		if (temp instanceof ISignedNumber) {
			return temp.compareTo(integer(i.longValue()));
		}
		throw new UnsupportedOperationException(
				"compareTo() - first argument could not be converted into a signed number.");
	}

	public static int compareTo(IExpr a, java.math.BigInteger i) throws UnsupportedOperationException {
		if (a instanceof ISignedNumber) {
			return a.compareTo(integer(i));
		}
		IExpr temp = eval(a);
		if (temp instanceof ISignedNumber) {
			return temp.compareTo(integer(i));
		}
		throw new UnsupportedOperationException(
				"compareTo() - first argument could not be converted into a signed number.");
	}

	public static int compareTo(Integer i, IExpr b) throws UnsupportedOperationException {
		if (b instanceof ISignedNumber) {
			return integer(i.longValue()).compareTo(b);
		}
		IExpr temp = eval(b);
		if (temp instanceof ISignedNumber) {
			return integer(i.longValue()).compareTo(temp);
		}
		throw new UnsupportedOperationException(
				"compareTo() - second argument could not be converted into a signed number.");
	}

	public static int compareTo(java.math.BigInteger i, IExpr b) throws UnsupportedOperationException {
		if (b instanceof ISignedNumber) {
			return integer(i).compareTo(b);
		}
		IExpr temp = eval(b);
		if (temp instanceof ISignedNumber) {
			return integer(i).compareTo(temp);
		}
		throw new UnsupportedOperationException(
				"compareTo() - second argument could not be converted into a signed number.");
	}

	/**
	 * Create a symbolic complex number
	 * 
	 * @param realPart
	 *            the real double value part which should be converted to a complex number
	 * @param imagPart
	 *            the imaginary double value part which should be converted to a complex number
	 * @return IFraction
	 */
	public static IComplex complex(final double realPart, final double imagPart) {
		return complex(realPart, imagPart, Config.DOUBLE_EPSILON);
	}

	/**
	 * Create a symbolic complex number
	 * 
	 * @param realPart
	 *            the real double value part which should be converted to a complex number
	 * @param imagPart
	 *            the imaginary double value part which should be converted to a complex number
	 * @param epsilon
	 * @return IFraction
	 */
	public static IComplex complex(final double realPart, final double imagPart, final double epsilon) {
		return ComplexSym.valueOf(AbstractFractionSym.valueOfEpsilon(realPart, epsilon),
				AbstractFractionSym.valueOfEpsilon(imagPart, epsilon));
	}

	/**
	 * Create a symbolic complex number
	 * 
	 * @param re
	 * @return
	 */
	public static IComplex complex(final IRational re) {
		return complex(re, fraction(0L, 1L));
	}

	/**
	 * Create a symbolic complex number
	 * 
	 * @param re
	 * @param im
	 * @return
	 */
	public static IComplex complex(final IRational re, final IRational im) {
		return ComplexSym.valueOf(re, im);
	}

	/**
	 * Create a symbolic complex number
	 * 
	 * @param re
	 * @param im
	 * @return
	 */
	public static IComplex complex(final long real_numerator, final long real_denominator, final long imag_numerator,
			final long imag_denominator) {
		return ComplexSym.valueOf(real_numerator, real_denominator, imag_numerator, imag_denominator);
	}

	/**
	 * TODO: check if Complex is working in pattern matching?
	 * 
	 * @param a0
	 * @param a1
	 * @return
	 */
	public static IAST Complex(final IExpr a0, final IExpr a1) {
		return binaryAST2(Complex, a0, a1);
	}

	public static IComplexNum complexNum(final Apcomplex c) {
		return ApcomplexNum.valueOf(c);
	}

	public static IComplexNum complexNum(final Apfloat r) {
		return ApcomplexNum.valueOf(r, Apcomplex.ZERO);
	}

	public static IComplexNum complexNum(final Apfloat r, final Apfloat i) {
		return ApcomplexNum.valueOf(r, i);
	}

	public static IComplexNum complexNum(final Complex c) {
		return ComplexNum.valueOf(c);
	}

	/**
	 * Create a complex numeric number with imaginary part = 0.0
	 * 
	 * @param r
	 *            the real part of the number
	 * @return
	 */
	public static IComplexNum complexNum(final double r) {
		return complexNum(r, 0.0);
	}

	/**
	 * Create a complex numeric value
	 * 
	 * @param r
	 *            real part
	 * @param i
	 *            imaginary part
	 * @return
	 */
	public static IComplexNum complexNum(final double r, final double i) {
		return ComplexNum.valueOf(r, i);
	}

	public static IComplexNum complexNum(final IComplex value) {
		final IRational realFraction = value.getRealPart();
		final IRational imagFraction = value.getImaginaryPart();
		final EvalEngine engine = EvalEngine.get();
		if (engine.isApfloat()) {
			return ApcomplexNum.valueOf(realFraction.toBigNumerator(), realFraction.toBigDenominator(),
					imagFraction.toBigNumerator(), imagFraction.toBigDenominator(), engine.getNumericPrecision());
		}
		// double precision complex number
		double nr = realFraction.numerator().doubleValue();
		double dr = realFraction.denominator().doubleValue();
		double ni = imagFraction.numerator().doubleValue();
		double di = imagFraction.denominator().doubleValue();

		return complexNum(nr / dr, ni / di);
	}

	public static IComplexNum complexNum(final IFraction value) {
		final EvalEngine engine = EvalEngine.get();
		if (engine.isApfloat()) {
			return ApcomplexNum.valueOf(value.toBigNumerator(), value.toBigDenominator(), BigInteger.ZERO,
					BigInteger.ONE, engine.getNumericPrecision());
		}
		return complexNum(value.doubleValue(), 0.0d);
	}

	public static IComplexNum complexNum(final IInteger value) {
		final EvalEngine engine = EvalEngine.get();
		if (engine.isApfloat()) {
			return ApcomplexNum.valueOf(value.toBigNumerator(), BigInteger.ONE, BigInteger.ZERO, BigInteger.ONE,
					engine.getNumericPrecision());
		}
		return complexNum(value.doubleValue(), 0.0d);
	}

	public static IAST CompoundExpression(final IExpr... a) {
		return ast(a, CompoundExpression);
	}

	public static IAST Condition(final IExpr a0, final IExpr a1) {
		return binaryAST2(Condition, a0, a1);
	}

	public static IAST ConditionalExpression(final IExpr a0, final IExpr a1) {
		return binaryAST2(ConditionalExpression, a0, a1);
	}

	public static IAST Conjugate(final IExpr a0) {
		return unaryAST1(Conjugate, a0);
	}

	public static IAST ConstantArray(final IExpr a0, final IExpr a1) {
		return binaryAST2(ConstantArray, a0, a1);
	}

	public static IAST ConjugateTranspose(final IExpr a0) {
		return unaryAST1(ConjugateTranspose, a0);
	}

	public static IAST Continue() {
		return headAST0(Continue);
	}

	public static IAST CoprimeQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(CoprimeQ, a0, a1);
	}

	public static IAST Cos(final IExpr a0) {
		return unaryAST1(Cos, a0);
	}

	public static IAST Cosh(final IExpr a0) {
		return unaryAST1(Cosh, a0);
	}

	public static IAST CoshIntegral(final IExpr a) {
		return unaryAST1(CoshIntegral, a);
	}

	public static IAST CosIntegral(final IExpr a) {
		return unaryAST1(CosIntegral, a);
	}

	public static IAST Cot(final IExpr a0) {
		return unaryAST1(Cot, a0);
	}

	public static IAST Coth(final IExpr a0) {
		return unaryAST1(Coth, a0);
	}

	public static IAST Count(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Count, a0, a1, a2);
	}

	public static IAST Covariance(final IExpr a0, final IExpr a1) {
		return binaryAST2(Covariance, a0, a1);
	}

	public static IAST Cross(final IExpr a0, final IExpr a1) {
		return binaryAST2(Cross, a0, a1);
	}

	public static IAST Csc(final IExpr a0) {
		return unaryAST1(Csc, a0);
	}

	public static IAST Csch(final IExpr a0) {
		return unaryAST1(Csch, a0);
	}

	public static IAST D() {
		return ast(D);
	}

	public static IAST D(final IExpr a0, final IExpr a1) {
		return binaryAST2(D, a0, a1);
	}

	public static IAST Decrement(final IExpr a) {
		return unaryAST1(Decrement, a);
	}

	public static IAST Defer(final IExpr a0) {
		return unaryAST1(Defer, a0);
	}

	public static IAST Delete(final IExpr a0, final IExpr a1) {
		return binaryAST2(Delete, a0, a1);
	}

	public static IAST DeleteCases(final IExpr... a) {
		return ast(a, DeleteCases);
	}

	public static IAST Denominator(final IExpr a0) {

		return unaryAST1(Denominator, a0);
	}

	public static IAST Depth(final IExpr a0) {

		return unaryAST1(Depth, a0);
	}

	public static IASTAppendable Derivative(final IExpr... a) {
		return ast(a, Derivative);
	}

	public static IAST DesignMatrix(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(DesignMatrix, a0, a1, a2);
	}

	public static IAST Det(final IExpr a0) {
		return unaryAST1(Det, a0);
	}

	public static IAST Dimensions(final IExpr a0) {
		return unaryAST1(Dimensions, a0);
	}

	public static IAST DiracDelta(final IExpr a0) {
		return unaryAST1(DiracDelta, a0);
	}

	public static IAST DirectedInfinity(final IExpr a0) {
		return unaryAST1(DirectedInfinity, a0);
	}

	public static IAST DiscreteUniformDistribution(final IExpr a) {
		return unaryAST1(DiscreteUniformDistribution, a);
	}

	public static IAST Discriminant(final IExpr a0, final IExpr a1) {
		return binaryAST2(Discriminant, a0, a1);
	}

	public static IAST Distribute(final IExpr a) {
		return unaryAST1(Distribute, a);
	}

	public static IAST Distribute(final IExpr a0, final IExpr a1) {
		return binaryAST2(Distribute, a0, a1);
	}

	public static IAST Distribute(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Distribute, a0, a1, a2);
	}

	/**
	 * Create a <code>Distributed(x, &lt;distribution&gt;)</code> AST.
	 * 
	 * @param x
	 * @param distribution
	 * @return
	 */
	public static IAST Distributed(final IExpr x, final IAST distribution) {
		return binaryAST2(Distributed, x, distribution);
	}

	public static IExpr div(IExpr a, Integer i) {
		return Times(a, Power(integer(i.longValue()), CN1));
	}

	public static IExpr div(IExpr a, java.math.BigInteger i) {
		return Times(a, Power(integer(i), CN1));
	}

	public static IExpr div(Integer i, IExpr b) {
		return Times(integer(i.longValue()), Power(b, CN1));
	}

	public static IExpr div(java.math.BigInteger i, IExpr b) {
		return Times(integer(i), Power(b, CN1));
	}

	/**
	 * The division <code>arg1 / arg2</code> will be represented by <code>arg1 * arg2^(-1)</code>.
	 * 
	 * @param arg1
	 *            numerator
	 * @param arg2
	 *            denominator
	 * @return
	 */
	public static IAST Divide(final IExpr arg1, final IExpr arg2) {
		return binaryAST2(Times, arg1, binaryAST2(Power, arg2, CN1));
	}

	public static IAST Divisible(final IExpr a0, final IExpr a1) {
		return binaryAST2(Divisible, a0, a1);
	}

	public static IAST DivisorSigma(final IExpr a0, final IExpr a1) {
		return binaryAST2(DivisorSigma, a0, a1);
	}

	public static IAST Do(final IExpr a0, final IExpr a1) {
		return binaryAST2(Do, a0, a1);
	}

	public static IASTAppendable Dot(final IExpr... a) {
		return ast(a, Dot);
	}

	public static IAST Dot(final IExpr a0, final IExpr a1) {
		return binaryAST2(Dot, a0, a1);
	}

	public static IAST Drop(final IExpr a0, final IExpr a1) {
		return binaryAST2(Drop, a0, a1);
	}

	public static IAST DSolve(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(DSolve, a0, a1, a2);
	}

	public static IAST Element(final IExpr a0, final IExpr a1) {
		return binaryAST2(Element, a0, a1);
	}

	public static IAST EllipticE(final IExpr a0) {
		return unaryAST1(EllipticE, a0);
	}

	public static IAST EllipticE(final IExpr a0, final IExpr a1) {
		return binaryAST2(EllipticE, a0, a1);
	}

	public static IAST EllipticF(final IExpr a0, final IExpr a1) {
		return binaryAST2(EllipticF, a0, a1);
	}

	public static IAST EllipticK(final IExpr a0) {
		return unaryAST1(EllipticK, a0);
	}

	public static IAST EllipticPi(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(EllipticPi, a0, a1, a2);
	}

	public static IASTAppendable Equal(final IExpr... a) {
		return ast(a, Equal);
	}

	public static IAST Equal(final IExpr a0, final IExpr a1) {
		return binaryAST2(Equal, a0, a1);
	}

	public static IAST Erf(final IExpr a) {
		return unaryAST1(Erf, a);
	}

	public static IAST Erfc(final IExpr a) {
		return unaryAST1(Erfc, a);
	}

	public static IAST Erfi(final IExpr a) {
		return unaryAST1(Erfi, a);
	}

	public static IAST ErlangDistribution(final IExpr a0, final IExpr a1) {
		return binaryAST2(ErlangDistribution, a0, a1);
	}

	/**
	 * Evaluate an expression. If no evaluation was possible this method returns the given argument.
	 * 
	 * @param a
	 *            the expression which should be evaluated
	 * @return the evaluated expression
	 * @see EvalEngine#evaluate(IExpr)
	 */
	public static IExpr eval(IExpr a) {
		return EvalEngine.get().evaluate(a);
	}

	/**
	 * Create a function with 1 argument and evaluate it.
	 * 
	 * @param head
	 * @param a0
	 * @return the evaluated object
	 * @deprecated
	 */
	@Deprecated
	private static IExpr eval(final ISymbol head, final IExpr a0) {
		final IASTAppendable ast = ast(head);
		ast.append(a0);
		return EvalEngine.get().evaluate(ast);
	}

	/**
	 * Evaluate <code>Expand()</code> for the given expression. returns the given argument.
	 * 
	 * @param a
	 *            the expression which should be evaluated
	 * @return the evaluated expression
	 * @see EvalEngine#evaluate(IExpr)
	 */
	public static IExpr evalExpand(IExpr a) {
		IExpr result = EvalEngine.get().evaluate(a);
		if (result.isAST()) {
			IAST ast = (IAST) result;
			if (ast.isPlus()) {
				for (int i = 1; i < ast.size(); i++) {
					IExpr temp = ast.get(i);
					if (temp.isTimes() || temp.isPower() || temp.isPlus()) {
						return EvalEngine.get().evaluate(Expand(result));
					}
				}
				return ast;
			}
			if (ast.isTimes() || ast.isPower()) {
				return EvalEngine.get().evaluate(Expand(result));
			}
		}
		return result;
	}

	/**
	 * Apply <code>ExpandAll()</code> to the given expression if it's an <code>IAST</code>. If expanding wasn't possible
	 * this method returns the given argument.
	 * 
	 * @param a
	 *            the expression which should be evaluated
	 * @return the evaluated expression
	 * @see EvalEngine#evaluate(IExpr)
	 */
	public static IExpr evalExpandAll(IExpr a) {
		return evalExpandAll(a, EvalEngine.get());
	}

	/**
	 * Apply <code>ExpandAll()</code> to the given expression if it's an <code>IAST</code>. If expanding wasn't possible
	 * this method returns the given argument.
	 * 
	 * @param a
	 *            the expression which should be evaluated
	 * @return the evaluated expression
	 * @see EvalEngine#evaluate(IExpr)
	 */
	public static IExpr evalExpandAll(IExpr a, EvalEngine engine) {
		return engine.evaluate(ExpandAll(a));
	}

	/**
	 * Evaluate the given expression in numeric mode
	 * 
	 * @param a0
	 * @return
	 * @deprecated use EvalEngine.get().evalN() instead
	 */
	@Deprecated
	public static IExpr evaln(final IExpr a0) {
		return eval(N, a0);
	}

	/**
	 * Evaluate an expression in &quot;quiet mode&quot;. If no evaluation was possible this method returns the given
	 * argument. In &quot;quiet mode&quot; all warnings would be suppressed.
	 * 
	 * @param a
	 *            the expression which should be evaluated
	 * @return the evaluated expression
	 * @see EvalEngine#evalQuiet(IExpr)
	 * @deprecated use EvalEngine#evalQuiet();
	 */
	@Deprecated
	public static IExpr evalQuiet(IExpr a) {
		return EvalEngine.get().evalQuiet(a);
	}

	/**
	 * Evaluate an expression in &quot;quiet mode&quot;. If evaluation is not possible return <code>null</code>. In
	 * &quot;quiet mode&quot; all warnings would be suppressed.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @return the evaluated object or <code>F.NIL</code> if no evaluation was possible
	 * @see EvalEngine#evalQuietNull(IExpr)
	 * @deprecated use EvalEngine#evalQuietNull()
	 */
	@Deprecated
	public static IExpr evalQuietNull(IExpr a) {
		return EvalEngine.get().evalQuietNull(a);
	}

	/**
	 * Evaluate the given expression and test if the result equals the symbol <code>True</code>.
	 * 
	 * @param expr
	 * @return
	 * @deprecated use EvalEngine#evalTrue()
	 */
	@Deprecated
	public static boolean evalTrue(IExpr expr) {
		return EvalEngine.get().evalTrue(expr);
	}

	public static IAST EvenQ(final IExpr a) {
		return unaryAST1(EvenQ, a);
	}

	public static IAST Exists(final IExpr a0, final IExpr a1) {
		return binaryAST2(Exists, a0, a1);
	}

	public static IAST Exists(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Exists, a0, a1, a2);
	}

	public static IAST EulerE(final IExpr a0) {
		return unaryAST1(EulerE, a0);
	}

	public static IAST EulerPhi(final IExpr a0) {
		return unaryAST1(EulerPhi, a0);
	}

	public static IAST Exp(final IExpr a0) {
		return binaryAST2(Power, E, a0);
	}

	/**
	 * Apply <code>Expand()</code> to the given expression if it's an <code>IAST</code>. If expanding wasn't possible
	 * this method returns the given argument.
	 * 
	 * @param a
	 *            the expression which should be evaluated
	 * @param expandNegativePowers
	 *            TODO
	 * @param distributePlus
	 *            TODO
	 * @return the evaluated expression
	 * @see EvalEngine#evaluate(IExpr)
	 */
	public static IExpr expand(IExpr a, boolean expandNegativePowers, boolean distributePlus) {
		if (a.isAST()) {
			EvalEngine engine = EvalEngine.get();
			IAST ast = engine.evalFlatOrderlessAttributesRecursive((IAST) a);
			if (!ast.isPresent()) {
				ast = (IAST) a;
			}
			return Algebra.expand(ast, null, expandNegativePowers, distributePlus).orElse(a);
		}
		return a;
	}

	public static IAST Expand(final IExpr a0) {
		return unaryAST1(Expand, a0);
	}

	public static IAST Expand(final IExpr a0, final IExpr a1) {

		return binaryAST2(Expand, a0, a1);
	}

	/**
	 * Apply <code>ExpandAll()</code> to the given expression if it's an <code>IAST</code>. If expanding wasn't possible
	 * this method returns the given argument.
	 * 
	 * @param a
	 *            the expression which should be evaluated
	 * @param expandNegativePowers
	 *            TODO
	 * @param distributePlus
	 *            TODO
	 * @return the evaluated expression
	 * @see EvalEngine#evaluate(IExpr)
	 */
	public static IExpr expandAll(IExpr a, boolean expandNegativePowers, boolean distributePlus) {
		if (a.isAST()) {
			EvalEngine engine = EvalEngine.get();
			IAST ast = engine.evalFlatOrderlessAttributesRecursive((IAST) a);
			if (!ast.isPresent()) {
				ast = (IAST) a;
			}
			IExpr temp = Algebra.expandAll(ast, null, expandNegativePowers, distributePlus, engine);
			if (temp.isPresent()) {
				return temp;
			}
			return ast;
		}
		return a;
	}

	//
	// public static IAST NumberPartitions(final IExpr a0) {
	//
	// return unaryAST2(NumberPartitions, a0);
	// }

	public static IAST ExpandAll(final IExpr a0) {
		return unaryAST1(ExpandAll, a0);
	}

	public static IAST ExpIntegralE(final IExpr a0, final IExpr a1) {
		return binaryAST2(ExpIntegralE, a0, a1);
	}

	public static IAST ExpIntegralEi(final IExpr a) {
		return unaryAST1(ExpIntegralEi, a);
	}

	public static IAST Exponent(final IExpr a0, final IExpr a1) {
		return binaryAST2(Exponent, a0, a1);
	}

	public static IAST Exponent(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Exponent, a0, a1, a2);
	}

	public static IAST ExponentialDistribution(final IExpr a0) {
		return unaryAST1(ExponentialDistribution, a0);
	}

	public static IAST Extract(final IExpr a0, final IExpr a1) {
		return binaryAST2(Extract, a0, a1);
	}

	public static IAST Factor(final IExpr a0) {
		return unaryAST1(Factor, a0);
	}

	public static IAST Factorial(final IExpr a0) {
		return unaryAST1(Factorial, a0);
	}

	public static IAST Factorial2(final IExpr a0) {
		return unaryAST1(Factorial2, a0);
	}

	public static IAST FactorInteger(final IExpr a0) {
		return unaryAST1(FactorInteger, a0);
	}

	public static IAST FactorSquareFree(final IExpr a) {
		return unaryAST1(FactorSquareFree, a);
	}

	public static IAST FactorSquareFreeList(final IExpr a) {
		return unaryAST1(FactorSquareFreeList, a);
	}

	public static IAST Fibonacci(final IExpr a0) {
		return unaryAST1(Fibonacci, a0);
	}

	public static IAST First(final IExpr a0) {
		return unaryAST1(First, a0);
	}

	public static IAST Flatten(final IExpr a0) {
		return unaryAST1(Flatten, a0);
	}

	public static IAST Flatten(final IExpr a0, final IExpr a1) {
		return binaryAST2(Flatten, a0, a1);
	}

	public static IAST Floor(final IExpr a0) {
		return unaryAST1(Floor, a0);
	}

	public static IAST Fold(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Fold, a0, a1, a2);
	}

	public static IAST ForAll(final IExpr a0, final IExpr a1) {
		return binaryAST2(ForAll, a0, a1);
	}

	public static IAST ForAll(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(ForAll, a0, a1, a2);
	}

	/**
	 * Create a "fractional" number
	 * 
	 * @param value
	 *            the rational value which should be converted to a fractional number
	 * @return IFraction
	 */
	public static IFraction fraction(final BigFraction value) {
		return AbstractFractionSym.valueOf(value.getNumerator(), value.getDenominator());
	}

	/**
	 * Create a "fractional" number
	 * 
	 * @param numerator
	 *            numerator of the fractional number
	 * @param denominator
	 *            denumerator of the fractional number
	 * @return IFraction
	 */
	public static IFraction fraction(final BigInteger numerator, final BigInteger denominator) {
		return AbstractFractionSym.valueOf(numerator, denominator);
	}

	/**
	 * Create a "fractional" number
	 * 
	 * @param value
	 *            the double value which should be converted to a fractional number
	 * @return IFraction
	 */
	public static IFraction fraction(final double value) {
		return AbstractFractionSym.valueOfEpsilon(value);
	}

	public static IFraction fraction(final double value, final double epsilon) {
		return AbstractFractionSym.valueOfEpsilon(value, epsilon);
	}

	/**
	 * Create a "fractional" number
	 * 
	 * @param numerator
	 *            numerator of the fractional number
	 * @param denominator
	 *            denumerator of the fractional number
	 * @return IFraction
	 */
	public static IFraction fraction(final IInteger numerator, final IInteger denominator) {
		return AbstractFractionSym.valueOf(numerator, denominator);
	}

	/**
	 * Create a "fractional" number
	 * 
	 * @param numerator
	 *            numerator of the fractional number
	 * @param denominator
	 *            denumerator of the fractional number
	 * @return IFraction
	 */
	public static IRational fraction(final long numerator, final long denominator) {
		return AbstractFractionSym.valueOf(numerator, denominator);
	}

	public static IAST FractionalPart(final IExpr a) {
		return unaryAST1(FractionalPart, a);
	}

	public static IAST FreeQ(final IExpr a0, final IExpr a1) {

		return binaryAST2(FreeQ, a0, a1);
	}

	public static IAST FrechetDistribution(final IExpr a0, final IExpr a1) {
		return binaryAST2(FrechetDistribution, a0, a1);
	}

	public static IAST FresnelC(final IExpr a) {
		return unaryAST1(FresnelC, a);
	}

	public static IAST FresnelS(final IExpr a) {
		return unaryAST1(FresnelS, a);
	}

	public static IAST FullForm(final IExpr a0) {
		return unaryAST1(FullForm, a0);
	}

	public static IAST FullSimplify(final IExpr a) {
		return unaryAST1(FullSimplify, a);
	}

	public static IAST Function(final IExpr a0) {
		return unaryAST1(Function, a0);
	}

	public static IAST Function(final IExpr a0, final IExpr a1) {
		return binaryAST2(Function, a0, a1);
	}

	public static IAST Get(final IExpr a0) {
		return unaryAST1(Get, a0);
	}

	public static IAST Get(final String str) {
		return unaryAST1(Get, F.stringx(str));
	}

	public static IAST Gamma(final IExpr a0) {
		return unaryAST1(Gamma, a0);
	}

	public static IAST Gamma(final IExpr a0, final IExpr a1) {
		return binaryAST2(Gamma, a0, a1);
	}

	public static IAST Gamma(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Gamma, a0, a1, a2);
	}

	public static IAST GammaDistribution(final IExpr a0, final IExpr a1) {
		return binaryAST2(GammaDistribution, a0, a1);
	}

	public static IAST GammaDistribution(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(GammaDistribution, a0, a1, a2, a3);
	}

	public static IAST GammaRegularized(final IExpr a0, final IExpr a1) {
		return binaryAST2(GammaRegularized, a0, a1);
	}

	public static IAST GammaRegularized(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(GammaRegularized, a0, a1, a2);
	}

	public static IAST GCD(final IExpr a0) {
		return unaryAST1(GCD, a0);
	}

	public static IAST GCD(final IExpr a0, final IExpr a1) {
		return binaryAST2(GCD, a0, a1);
	}

	public static IAST GegenbauerC(final IExpr a0, final IExpr a1) {
		return binaryAST2(GegenbauerC, a0, a1);
	}

	public static IASTAppendable Graphics() {
		return ast(Graphics);
	}

	public static IAST Greater(final IExpr a0, final IExpr a1) {
		return binaryAST2(Greater, a0, a1);
	}

	public static IAST GreaterEqual(final IExpr a0, final IExpr a1) {
		return binaryAST2(GreaterEqual, a0, a1);
	}

	public static IAST GumbelDistribution() {
		return headAST0(GumbelDistribution);
	}

	public static IAST GumbelDistribution(final IExpr a0, final IExpr a1) {
		return binaryAST2(GumbelDistribution, a0, a1);
	}

	public static IAST HarmonicNumber(final IExpr a) {
		return unaryAST1(HarmonicNumber, a);
	}

	public static IAST HarmonicNumber(final IExpr a0, final IExpr a1) {
		return binaryAST2(HarmonicNumber, a0, a1);
	}

	public static IAST Head(final IExpr a) {
		return unaryAST1(Head, a);
	}

	/**
	 * Create a new abstract syntax tree (AST).
	 * 
	 * @param head
	 *            the header expression of the function. If the ast represents a function like
	 *            <code>f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
	 * 
	 */
	public final static IAST headAST0(final IExpr head) {
		return new AST0(head);
	}

	public static IAST HeavisideTheta(final IExpr a0) {
		return unaryAST1(HeavisideTheta, a0);
	}

	public static IAST Hold(final IExpr a0) {
		return unaryAST1(Hold, a0);
	}

	public static IAST HoldForm(final IExpr a0) {
		return unaryAST1(HoldForm, a0);
	}

	public static IAST HoldPattern(final IExpr a0) {
		return unaryAST1(HoldPattern, a0);
	}

	public static IAST HurwitzZeta(final IExpr a0, final IExpr a1) {
		return binaryAST2(HurwitzZeta, a0, a1);
	}

	public static IAST Hypergeometric2F1(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(Hypergeometric2F1, a0, a1, a2, a3);
	}

	public static IAST HypergeometricPFQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(HypergeometricPFQ, a0, a1, a2);
	}

	public static IAST HypergeometricPFQRegularized(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(HypergeometricPFQRegularized, a0, a1, a2);
	}

	public static IAST Identity(final IExpr a0) {
		return unaryAST1(Identity, a0);
	}

	public static IAST If(final IExpr a0, final IExpr a1) {
		return binaryAST2(If, a0, a1);
	}

	public static IAST If(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(If, a0, a1, a2);
	}

	public static IAST If(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(If, a0, a1, a2, a3);
	}

	public static IAST IInit(final ISymbol sym, int[] sizes) {
		sym.createRulesData(sizes);
		return null;
	}

	public static IExpr Im(final IExpr a0) {
		if (a0 != null && a0.isNumber()) {
			return ((INumber) a0).im();
		}
		return unaryAST1(Im, a0);
	}

	public static IAST Implies(final IExpr a0, final IExpr a1) {
		return binaryAST2(Implies, a0, a1);
	}

	public static IAST Increment(final IExpr a) {
		return unaryAST1(Increment, a);
	}

	public static IASTAppendable Inequality(final IExpr... a) {
		return ast(a, Inequality);
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
		ISymbol temp = new Symbol(symbolName, Context.SYSTEM);
		HIDDEN_SYMBOLS_MAP.put(symbolName, temp);
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
	// public static IBuiltInSymbol initFinalSymbol(final String symbolName) {
	// IBuiltInSymbol temp = new BuiltInSymbol(symbolName);
	// Context.SYSTEM.put(symbolName, temp);
	// return temp;
	// }

	public static IBuiltInSymbol initFinalSymbol(final String symbolName, int ordinal) {
		String str = symbolName;
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			if (str.length() != 1) {
				str = symbolName.toLowerCase();
			} else {
				str = symbolName;
			}
		}
		IBuiltInSymbol temp = new BuiltInSymbol(str, ordinal);
		BUILT_IN_SYMBOLS[ordinal] = temp;
		Context.SYSTEM.put(str, temp);
		return temp;
	}

	// public static IBuiltInSymbol initFinalSymbol(final BuiltIns symbolName) {
	// String str = symbolName.name();
	// if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
	// if (str.length() != 1) {
	// str = symbolName.str();
	// }
	// }
	// IBuiltInSymbol temp = new BuiltInSymbol(str, symbolName.id());
	// Context.SYSTEM.put(str, temp);
	// return temp;
	// }

	/**
	 * Insert a new Symbol in the <code>SYSTEM</code> context.
	 * 
	 * @param symbolName
	 *            the predefined symbol name in upper-case form
	 * @param evaluator
	 *            defines the evaluation behaviour of the symbol
	 * @return
	 */
	// public static IBuiltInSymbol initFinalSymbol(final String symbolName, IEvaluator evaluator) {
	// IBuiltInSymbol temp = new BuiltInSymbol(symbolName, evaluator);
	// evaluator.setUp(temp);
	// Context.SYSTEM.put(symbolName, temp);
	// return temp;
	// }

	public static IPattern initPredefinedPattern(@Nonnull final ISymbol symbol) {
		IPattern temp = new Pattern(symbol);
		PREDEFINED_PATTERN_MAP.put(symbol.toString(), temp);
		return temp;
	}

	public static IPatternSequence initPredefinedPatternSequence(@Nonnull final ISymbol symbol) {
		PatternSequence temp = PatternSequence.valueOf(symbol);
		PREDEFINED_PATTERNSEQUENCE_MAP.put(symbol.toString(), temp);
		return temp;
	}

	/**
	 * Initialize the complete System. Calls {@link #initSymbols(String, ISymbolObserver, boolean)} with parameters
	 * <code>null, null</code>.
	 */
	public synchronized static void initSymbols() {
		initSymbols(null, null, false);
	}

	/**
	 * Initialize the complete System
	 * 
	 * @param fileName
	 *            <code>null</code> or optional text filename, which includes the preloaded system rules
	 * @param symbolObserver
	 *            the observer for newly created <code>ISymbols</code>
	 * @param noPackageLoading
	 *            don't load any package at start up
	 */
	public synchronized static void initSymbols(String fileName, ISymbolObserver symbolObserver,
			boolean noPackageLoading) {

		if (!isSystemStarted) {
			try {
				isSystemStarted = true;

				if (Config.SHOW_PATTERN_EVAL_STEPS) {
					// watch the rules which are used in pattern matching in
					// system.out
					Config.SHOW_PATTERN_SYMBOL_STEPS.add(Integrate);
				}
				if (symbolObserver != null) {
					SYMBOL_OBSERVER = symbolObserver;
				}

				// if (!noPackageLoading) {
				// Reader reader = null;
				// if (fileName != null) {
				// try {
				// reader = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
				// } catch (FileNotFoundException e) {
				// e.printStackTrace();
				// }
				// }
				// if (reader == null) {
				// InputStream systemPackage = F.class.getResourceAsStream("/System.mep");
				// if (systemPackage != null) {
				// reader = new InputStreamReader(systemPackage, "UTF-8");
				// }
				// }
				// if (reader != null) {
				// org.matheclipse.core.builtin.function.Package.loadPackage(EvalEngine.get(), reader);
				// }
				// }

				isSystemInitialized = true;
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	public static IAST Insert(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Insert, a0, a1, a2);
	}

	/**
	 * Create a large integer number.
	 * 
	 * @param integerValue
	 * @return
	 */
	public static IInteger integer(final BigInteger integerValue) {
		return AbstractIntegerSym.valueOf(integerValue);
	}

	/**
	 * Create a large integer number.
	 * 
	 * @param integerValue
	 * @return
	 */
	public static IInteger integer(final long integerValue) {
		return AbstractIntegerSym.valueOf(integerValue);
	}

	/**
	 * Create a large integer number.
	 * 
	 * @param integerString
	 *            the integer number represented as a String
	 * @param radix
	 *            the radix to be used while parsing
	 * @return Object
	 */
	public static IInteger integer(final String integerString, final int radix) {
		return AbstractIntegerSym.valueOf(integerString, radix);
	}

	public static IAST IntegerPart(final IExpr a0) {
		return unaryAST1(IntegerPart, a0);
	}

	public static IAST IntegerQ(final IExpr a) {
		return unaryAST1(IntegerQ, a);
	}

	public static IAST Integrate(final IExpr a0, final IExpr a1) {
		return binaryAST2(Integrate, a0, a1);
	}

	public static IAST Interpolation(final IExpr list) {
		return unaryAST1(Interpolation, list);
	}

	public static IAST InterpolatingFunction(final IExpr list) {
		return unaryAST1(InterpolatingFunction, list);
	}

	public static IAST InterpolatingPolynomial(final IExpr a0, final IExpr a1) {
		return binaryAST2(InterpolatingPolynomial, a0, a1);
	}

	/**
	 * Create an "interval" expression: <code>Interval(list)</code>.
	 * 
	 * @param list
	 * @return
	 */
	public static IAST Interval(final IExpr list) {
		return unaryAST1(Interval, list);
	}

	/**
	 * Create an "interval" expression: <code>Interval(List(from, to))</code>.
	 * 
	 * @param min
	 *            minimum value of the interval
	 * @param max
	 *            maximum value of the interval
	 * @return
	 */
	public static IAST Interval(final IExpr min, final IExpr max) {
		return unaryAST1(Interval, binaryAST2(List, min, max));
	}

	/**
	 * Iterate over an integer range <code>from <= i <= to</code> with the step <code>step/code>.
	 * 
	 * @param head
	 *            the header symbol of the result
	 * @param function
	 *            the function which should be applied on each iterator value
	 * @param from
	 * @param to
	 * @param step
	 * @return
	 */
	public static IAST intIterator(ISymbol head, final Function<IExpr, IExpr> function, final int from, final int to,
			final int step) {
		IASTAppendable result = F.ast(head, to - from + 1, false);
		for (int i = from; i <= to; i += step) {
			result.append(function.apply(F.ZZ(i)));
		}
		return result;
	}

	public static IAST Inverse(final IExpr a0) {

		return unaryAST1(Inverse, a0);
	}

	public static IAST InverseBetaRegularized(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(InverseBetaRegularized, a0, a1, a2);
	}

	public static IAST InverseErf(final IExpr a0) {
		return unaryAST1(InverseErf, a0);
	}

	public static IAST InverseErfc(final IExpr a0) {
		return unaryAST1(InverseErfc, a0);
	}

	public static IAST InverseFunction(final IExpr a) {
		return unaryAST1(InverseFunction, a);
	}

	public static IAST InverseGammaRegularized(final IExpr a0, final IExpr a1) {
		return binaryAST2(InverseGammaRegularized, a0, a1);
	}

	public static IAST InverseGammaRegularized(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(InverseGammaRegularized, a0, a1, a2);
	}

	public static IAST InverseLaplaceTransform(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(InverseLaplaceTransform, a0, a1, a2);
	}

	/**
	 * Assign the evaluated <code>rhs</code> to the <code>lhs</code>.<br/>
	 * 
	 * <b>Note:</b> this method returns <code>null</code>.
	 * 
	 * @param lhs
	 *            left-hand-side of the assignment
	 * @param rhs
	 *            right-hand-side of the assignment
	 * @return <code>null</code>
	 */
	public static IAST ISet(final IExpr lhs, final IExpr rhs) {
		if (lhs.isAST()) {
			((IAST) lhs).setEvalFlags(((IAST) lhs).getEvalFlags() | IAST.IS_FLATTENED_OR_SORTED_MASK);
		}
		PatternMatching.setDownRule(lhs, rhs, true);
		return null;
	}

	/**
	 * Assign the unevaluated <code>rhs</code> to the <code>lhs</code>.<br/>
	 * 
	 * <b>Note:</b> this method returns <code>null</code>.
	 * 
	 * @param lhs
	 *            left-hand-side of the assignment
	 * @param rhs
	 *            right-hand-side of the assignment
	 * @return <code>null</code>
	 */
	public static IAST ISetDelayed(final IExpr lhs, final IExpr rhs) {
		if (lhs.isAST()) {
			((IAST) lhs).setEvalFlags(((IAST) lhs).getEvalFlags() | IAST.IS_FLATTENED_OR_SORTED_MASK);
		}
		PatternMatching.setDelayedDownRule(lhs, rhs, true);
		return null;
	}

	public static boolean isNumEqualInteger(double value, IInteger ii) throws ArithmeticException {
		return isZero(value - ii.doubleValue(), Config.DOUBLE_TOLERANCE);
	}

	public static boolean isNumEqualRational(double value, IRational rational) throws ArithmeticException {
		return isZero(value - rational.doubleValue(), Config.DOUBLE_TOLERANCE);
	}

	public static boolean isNumIntValue(double value) {
		return isZero(value - Math.rint(value), Config.DOUBLE_TOLERANCE);
	}

	public static boolean isNumIntValue(double value, double epsilon) {
		return isZero(value - Math.rint(value), epsilon);
	}

	public static boolean isNumIntValue(double value, int i) {
		return isZero(value - i, Config.DOUBLE_TOLERANCE);
	}

	/**
	 * Check difference is less than a constant
	 * 
	 * infinity == infinity returns true eg 1/0
	 * 
	 * -infinity == infinity returns false eg -1/0
	 * 
	 * -infinity == -infinity returns true
	 * 
	 * undefined == undefined returns false eg 0/0
	 * 
	 * @return whether x is equal to y
	 * 
	 * 
	 */
	final public static boolean isEqual(double x, double y) {
		return DoubleMath.fuzzyEquals(x, y, Config.DOUBLE_TOLERANCE);
	}

	/**
	 * Test if the absolute value is less <code>Config.DOUBLE_EPSILON</code>.
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isZero(double value) {
		return isZero(value, Config.DOUBLE_TOLERANCE);
	}

	/**
	 * Test if the absolute value is less than the given epsilon.
	 * 
	 * @param x
	 * @param epsilon
	 * @return
	 */
	public static boolean isZero(double x, double epsilon) {
		return DoubleMath.fuzzyEquals(x, 0.0, epsilon);
		// return -epsilon < x && x < epsilon;
	}

	public static IAST Join(final IExpr a0, final IExpr a1) {
		return binaryAST2(Join, a0, a1);
	}

	public static IAST KroneckerDelta(final IExpr a0) {
		return unaryAST1(KroneckerDelta, a0);
	}

	public static IAST KroneckerDelta(final IExpr a0, final IExpr a1) {
		return binaryAST2(KroneckerDelta, a0, a1);
	}

	public static IAST LaguerreL(final IExpr a0, final IExpr a1) {
		return binaryAST2(LaguerreL, a0, a1);
	}

	public static IAST LaplaceTransform(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(LaplaceTransform, a0, a1, a2);
	}

	public static IAST Last(final IExpr a0) {
		return unaryAST1(Last, a0);
	}

	public static IAST LCM(final IExpr a0, final IExpr a1) {
		return binaryAST2(LCM, a0, a1);
	}

	public static IAST LegendreP(final IExpr a0, final IExpr a1) {
		return binaryAST2(LegendreP, a0, a1);
	}

	public static IAST LegendreQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(LegendreQ, a0, a1);
	}

	public static IAST LeafCount(final IExpr a0) {
		return unaryAST1(LeafCount, a0);
	}

	public static IAST Length(final IExpr a) {
		return unaryAST1(Length, a);
	}

	public static IAST Less(final IExpr a0, final IExpr a1) {
		return binaryAST2(Less, a0, a1);
	}

	public static IAST Less(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Less, a0, a1, a2);
	}

	public static IAST Less(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(Less, a0, a1, a2, a3);
	}

	public static IAST Less(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4) {
		return quinary(Less, a0, a1, a2, a3, a4);
	}

	public static IAST LessEqual(final IExpr a0, final IExpr a1) {
		return binaryAST2(LessEqual, a0, a1);
	}

	public static IAST LessEqual(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(LessEqual, a0, a1, a2);
	}

	public static IAST LessEqual(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(LessEqual, a0, a1, a2, a3);
	}

	public static IAST Limit(final IExpr a0, final IExpr a1) {
		return binaryAST2(Limit, a0, a1);
	}

	public static IAST Limit(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Limit, a0, a1, a2);
	}

	public static IASTAppendable Line() {
		return ast(Line);
	}

	public static IAST LinearModelFit(final IExpr a0) {
		return unaryAST1(LinearModelFit, a0);
	}

	public static IAST LinearModelFit(final IExpr a0, final IExpr a1) {
		return binaryAST2(LinearModelFit, a0, a1);
	}

	public static IAST LinearModelFit(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(LinearModelFit, a0, a1, a2);
	}

	public static IAST LinearModelFit(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(LinearModelFit, a0, a1, a2, a3);
	}

	public static IAST LinearProgramming(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(LinearProgramming, a0, a1, a2);
	}

	public static IAST LinearSolve(final IExpr a0, final IExpr a1) {
		return binaryAST2(LinearSolve, a0, a1);
	}

	/**
	 * Create a List() object.
	 * 
	 * @return
	 */
	public static IASTAppendable List() {
		return ast(List);
	}

	/**
	 * Create a new <code>List</code> with the given <code>capacity</code>.
	 * 
	 * @param capacity
	 *            the assumed number of arguments (+ 1 for the header expression is added internally).
	 * @return
	 */
	public static IASTAppendable ListAlloc(int capacity) {
		return ast(List, capacity, false);
	}

	/**
	 * For positive n, add the first n elements of <code>numbers</code> to the list.For negative n, add the first n
	 * elements of <code>numbers</code> to the list.
	 * 
	 * @param n
	 * @param numbers
	 * @return
	 */
	public static IAST List(final int n, final Integer... numbers) {
		int nPositive = n;
		if (n < 0) {
			nPositive = -n;
		}
		int size = numbers.length;
		if (nPositive > size) {
			nPositive = size;
		}
		IInteger[] a = new IInteger[nPositive];
		if (n < 0) {
			if (nPositive < size) {
				size = size + n;
			} else {
				size = 0;
			}
			int j = 0;
			for (int i = numbers.length - 1; i >= size; i--) {
				a[j++] = F.ZZ(numbers[i]);
			}
		} else {
			if (n < size) {
				size = n;
			}
			for (int i = 0; i < size; i++) {
				a[i] = F.ZZ(numbers[i]);
			}
		}
		return ast(a, List);
	}

	public static IAST List(final double... numbers) {
		INum a[] = new INum[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			a[i] = num(numbers[i]);
		}
		return ast(a, List);
	}

	public static IASTAppendable List(final IExpr... a) {
		return ast(a, List);
	}

	public static IAST List(final long... numbers) {
		IInteger a[] = new IInteger[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			a[i] = integer(numbers[i]);
		}
		return ast(a, List);
	}

	public static IAST ListQ(final IExpr a) {
		return unaryAST1(ListQ, a);
	}

	public static IAST Log(final IExpr a0) {
		return unaryAST1(Log, a0);
	}

	public static IAST Log(final IExpr a0, final IExpr a1) {

		return binaryAST2(Log, a0, a1);
	}

	/**
	 * <code>Log[10, a0]</code>.
	 * 
	 * @param a0
	 * @return <code>Log[10, a0]</code>.
	 */
	public static IAST Log10(final IExpr a0) {

		return binaryAST2(Log, F.C10, a0);
	}

	public static IAST LogGamma(final IExpr a0) {
		return unaryAST1(LogGamma, a0);
	}

	public static IAST LogIntegral(final IExpr a) {
		return unaryAST1(LogIntegral, a);
	}

	public static IAST LogisticSigmoid(final IExpr a) {
		return unaryAST1(LogisticSigmoid, a);
	}

	public static IAST LogNormalDistribution(final IExpr a0, final IExpr a1) {
		return binaryAST2(LogNormalDistribution, a0, a1);
	}

	public static IAST Map(final IExpr a0) {

		return unaryAST1(Map, a0);
	}

	public static IAST Map(final IExpr a0, final IExpr a1) {
		return binaryAST2(Map, a0, a1);
	}

	public static IAST MapThread(final IExpr a0, final IExpr a1) {
		return binaryAST2(MapThread, a0, a1);
	}

	public static IAST MapAll(final IExpr a0) {

		return unaryAST1(MapAll, a0);
	}

	public static IAST MatchQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(MatchQ, a0, a1);
	}

	public static IAST MathMLForm(final IExpr a0) {
		return unaryAST1(MathMLForm, a0);
	}

	public static IAST MatrixPower(final IExpr a0, final IExpr a1) {

		return binaryAST2(MatrixPower, a0, a1);
	}

	public static IASTAppendable Max() {
		return ast(Max);
	}

	public static IAST Max(final IExpr a0) {
		return unaryAST1(Max, a0);
	}

	public static IAST Max(final IExpr a0, final IExpr a1) {
		return binaryAST2(Max, a0, a1);
	}

	public static IAST Max(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(Max, a0, a1, a2, a3);
	}

	public static IAST Mean(final IExpr a0) {
		return unaryAST1(Mean, a0);
	}

	public static IAST MeanDeviation(final IExpr a0) {
		return unaryAST1(MeanDeviation, a0);
	}

	public static IAST Median(final IExpr a0) {
		return unaryAST1(Median, a0);
	}

	public static IAST MeijerG(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(MeijerG, a0, a1, a2);
	}

	public static IAST MemberQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(MemberQ, a0, a1);
	}

	public static IAST MessageName(final IExpr a0, final IExpr a1) {
		return binaryAST2(MessageName, a0, a1);
	}

	public static IASTAppendable Min() {
		return ast(Min);
	}

	public static IAST Min(final IExpr a0) {
		return unaryAST1(Min, a0);
	}

	public static IAST Min(final IExpr a0, final IExpr a1) {
		return binaryAST2(Min, a0, a1);
	}

	public static IAST Min(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return quaternary(Min, a0, a1, a2, a3);
	}

	public static IExpr minus(IExpr a, Integer i) {
		return Plus(a, Times(integer(i.longValue()), CN1));
	}

	public static IExpr minus(IExpr a, java.math.BigInteger i) {
		return Plus(a, Times(integer(i), CN1));
	}

	public static IExpr minus(Integer i, IExpr b) {
		return Plus(integer(i.longValue()), Times(b, CN1));
	}

	public static IExpr minus(java.math.BigInteger i, IExpr b) {
		return Plus(integer(i), Times(b, CN1));
	}

	public static IAST Missing(final IExpr a0) {
		return unaryAST1(Missing, a0);
	}

	public static IAST MoebiusMu(final IExpr a0) {
		return unaryAST1(MoebiusMu, a0);
	}

	public static IExpr mod(IExpr a, Integer i) {
		return Mod(a, integer(i.longValue()));
	}

	public static IExpr mod(IExpr a, java.math.BigInteger i) {
		return Mod(a, integer(i));
	}

	public static IExpr mod(Integer i, IExpr b) {
		return Mod(integer(i.longValue()), b);
	}

	public static IExpr mod(java.math.BigInteger i, IExpr b) {
		return Mod(integer(i), b);
	}

	public static IExpr Mod(final IExpr a0, final IExpr a1) {
		return binaryAST2(Mod, a0, a1);
	}

	public static IAST Module(final IExpr a0, final IExpr a1) {
		return binaryAST2(Module, a0, a1);
	}

	public static IAST Most(final IExpr a0) {
		return unaryAST1(Most, a0);
	}

	public static IExpr multiply(IExpr a, Integer i) {
		return Times(a, integer(i.longValue()));
	}

	public static IExpr multiply(IExpr a, java.math.BigInteger i) {
		return Times(a, integer(i));
	}

	public static IExpr multiply(Integer i, IExpr b) {
		return Times(integer(i.longValue()), b);
	}

	public static IExpr multiply(java.math.BigInteger i, IExpr b) {
		return Times(integer(i), b);
	}

	/**
	 * Evaluate the given expression in numeric mode
	 * 
	 * @param a0
	 * @return
	 */
	public static IAST N(final IExpr a0) {
		return unaryAST1(N, a0);
	}

	public static IAST NakagamiDistribution(final IExpr a0, final IExpr a1) {
		return binaryAST2(NakagamiDistribution, a0, a1);
	}

	/**
	 * Multiplies the given argument by <code>-1</code>. The <code>IExpr#negate()</code> method does evaluations, which
	 * don't agree with pattern matching assumptions (in left-hand-sige expressions). so it is only called called for
	 * <code>INumber</code> objects, otherwis a <code>Times(CN1, x)</code> AST would be created.
	 * 
	 * @param x
	 *            the expression which should be negated.
	 * @return
	 */
	public static IExpr Negate(final IExpr x) {
		if (x.isNumber()) {
			return x.negate();
		}
		if (x.isInfinity()) {
			return CNInfinity;
		}
		if (x.isNegativeInfinity()) {
			return CInfinity;
		}
		return binaryAST2(Times, CN1, x);
	}

	public static IAST Negative(final IExpr a0) {
		return unaryAST1(Negative, a0);
	}

	/**
	 * Create a new abstract syntax tree (AST).
	 * 
	 * @param intialArgumentsCapacity
	 *            the initial capacity of arguments of the AST.
	 * @param head
	 *            the header expression of the function. If the ast represents a function like
	 *            <code>f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
	 * @return
	 */
	public static IAST newInstance(final int intialArgumentsCapacity, final IExpr head) {
		return AST.newInstance(intialArgumentsCapacity, head);
	}

	public static IAST NMaximize(final IExpr a0, final IExpr a1) {
		return binaryAST2(NMaximize, a0, a1);
	}

	public static IAST NMinimize(final IExpr a0, final IExpr a1) {
		return binaryAST2(NMinimize, a0, a1);
	}

	public static IAST Norm(final IExpr a) {
		return unaryAST1(Norm, a);
	}

	public static IAST NormalDistribution() {
		return headAST0(NormalDistribution);
	}

	public static IAST NormalDistribution(final IExpr a0, final IExpr a1) {
		return binaryAST2(NormalDistribution, a0, a1);
	}

	public static IAST Normalize(final IExpr a) {
		return unaryAST1(Normalize, a);
	}

	public static IAST Not(final IExpr a) {
		return unaryAST1(Not, a);
	}

	public static IAST NullSpace(final IExpr a0) {
		return unaryAST1(NullSpace, a0);
	}

	public static INum num(final Apfloat af) {
		return ApfloatNum.valueOf(af);
	}

	/**
	 * Create a numeric value
	 * 
	 * @param d
	 * @return
	 */
	public static INum num(final double d) {
		return Num.valueOf(d);
	}

	public static INum num(final IFraction value) {
		EvalEngine engine = EvalEngine.get();
		if (engine.isApfloat()) {
			return ApfloatNum.valueOf(value.toBigNumerator(), value.toBigDenominator(), engine.getNumericPrecision());
		}
		final double n = value.toBigNumerator().doubleValue();
		final double d = value.toBigDenominator().doubleValue();
		return num(n / d);
	}

	public static INum num(final IInteger value) {
		EvalEngine engine = EvalEngine.get();
		if (engine.isApfloat()) {
			return ApfloatNum.valueOf(value.toBigNumerator(), engine.getNumericPrecision());
		}
		return num(value.doubleValue());
	}

	/**
	 * Create a numeric value from the input string.
	 * 
	 * @param valueString
	 *            the numeric value represented as a string.
	 * @return
	 */
	public static INum num(final String valueString) {
		EvalEngine engine = EvalEngine.get();
		if (engine.isApfloat()) {
			return ApfloatNum.valueOf(valueString, engine.getNumericPrecision());
		}
		return Num.valueOf(Double.parseDouble(valueString));
	}

	public static IAST NumberQ(final IExpr a0) {

		return unaryAST1(NumberQ, a0);
	}

	public static IAST Numerator(final IExpr a0) {
		return unaryAST1(Numerator, a0);
	}

	public static IAST NumericQ(final IExpr a0) {
		return unaryAST1(NumericQ, a0);
	}

	public static IAST O(final IExpr a0) {
		return unaryAST1(O, a0);
	}

	public static IAST OddQ(final IExpr a) {
		return unaryAST1(OddQ, a);
	}

	public static IAST Optional(final IExpr a0, final IExpr a1) {
		return binaryAST2(Optional, a0, a1);
	}

	public static IAST Options(final IExpr a0) {

		return unaryAST1(Options, a0);
	}

	public static IExpr or(IExpr a, Integer i) {
		return $(Or, a, integer(i.longValue()));
	}

	public static IExpr or(IExpr a, java.math.BigInteger i) {
		return Or(a, integer(i));
	}

	public static IExpr or(Integer i, IExpr b) {
		return $(Or, integer(i.longValue()), b);
	}

	public static IExpr or(java.math.BigInteger i, IExpr b) {
		return Or(integer(i), b);
	}

	public static IASTAppendable Or() {
		return ast(Or);
	}

	public static IAST Or(final IExpr a0, final IExpr a1) {
		return binaryAST2(Or, a0, a1);
	}

	public static IAST Or(final IExpr... a) {
		return ast(a, Or);
	}

	public static IAST Order(final IExpr a0, final IExpr a1) {
		return binaryAST2(Order, a0, a1);
	}

	public static IAST OrderedQ(final IExpr a) {
		return unaryAST1(OrderedQ, a);
	}

	public static IAST Part() {
		return ast(Part);
	}

	public static IASTAppendable Part(final IExpr... a) {
		IASTAppendable part = F.ast(Part, a.length + 1, false);
		for (int i = 0; i < a.length; i++) {
			part.append(a[i]);
		}
		return part;
	}

	public static IAST PartitionsP(final IExpr a0) {
		return unaryAST1(PartitionsP, a0);
	}

	public static IAST PartitionsQ(final IExpr a0) {
		return unaryAST1(PartitionsQ, a0);
	}

	public static IAST PatternTest(final IExpr a0, final IExpr a1) {
		return binaryAST2(PatternTest, a0, a1);
	}

	public static IAST PDF(final IExpr a0) {
		return unaryAST1(PDF, a0);
	}

	public static IAST PDF(final IExpr a0, final IExpr a1) {
		return binaryAST2(PDF, a0, a1);
	}

	public static IAST Piecewise(final IExpr a0) {
		return unaryAST1(Piecewise, a0);
	}

	public static IAST Piecewise(final IExpr a0, final IExpr a1) {
		return binaryAST2(Piecewise, a0, a1);
	}

	public static IExpr plus(IExpr a, Integer i) {
		return Plus(a, integer(i.longValue()));
	}

	public static IExpr plus(IExpr a, java.math.BigInteger i) {
		return Plus(a, integer(i));
	}

	public static IExpr plus(Integer i, IExpr b) {
		return Plus(integer(i.longValue()), b);
	}

	public static IExpr plus(java.math.BigInteger i, IExpr b) {
		return Plus(integer(i), b);
	}

	/**
	 * Create a Plus() function.
	 * 
	 * @return
	 */
	public static IAST Plus() {
		return ast(Plus);
	}

	/**
	 * Create a Plus() function with allocated space for size elements.
	 * 
	 * @param size
	 * @return
	 */
	public static IASTAppendable PlusAlloc(int size) {
		return ast(Plus, size, false);
	}

	public static IASTAppendable Plus(final IExpr a0) {
		return unary(Plus, a0);
	}

	public static IAST Plus(final IExpr... a) {
		return ast(a, Plus);
	}

	public static IAST Plus(final IExpr a0, final IExpr a1) {
		if (a0 != null && a1 != null) {
			if (a0.isPlus() || a1.isPlus()) {
				int size = 0;
				if (a0.isPlus()) {
					size += ((IAST) a0).size();
				} else {
					size++;
				}
				if (a1.isPlus()) {
					size += ((IAST) a1).size();
				} else {
					size++;
				}
				IASTAppendable result = PlusAlloc(size);
				if (a0.isPlus()) {
					result.appendArgs((IAST) a0);
				} else {
					result.append(a0);
				}
				if (a1.isPlus()) {
					result.appendArgs((IAST) a1);
				} else {
					result.append(a1);
				}
				EvalAttributes.sort(result);
				return result;
			}
			if (a0.compareTo(a1) > 0) {
				// swap arguments
				return binaryAST2(Plus, a1, a0);
			}
		}
		return binaryAST2(Plus, a0, a1);
	}

	public static IAST Plus(final long num, final IExpr... a) {
		return ast(a, Plus).prependClone(ZZ(num));
	}

	public static IAST Pochhammer(final IExpr a0, final IExpr a1) {
		return binaryAST2(Pochhammer, a0, a1);
	}

	public static IAST PolyGamma(final IExpr a0) {
		return unaryAST1(PolyGamma, a0);
	}

	public static IAST PolyGamma(final IExpr a0, final IExpr a1) {
		return binaryAST2(PolyGamma, a0, a1);
	}

	public static IAST PolyLog(final IExpr a0, final IExpr a1) {
		return binaryAST2(PolyLog, a0, a1);
	}

	public static IAST PolynomialGCD(final IExpr a0, final IExpr a1) {
		return binaryAST2(PolynomialGCD, a0, a1);
	}

	public static IAST PolynomialQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(PolynomialQ, a0, a1);
	}

	public static IAST PolynomialQuotient(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(PolynomialQuotient, a0, a1, a2);
	}

	public static IAST PolynomialQuotientRemainder(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(PolynomialQuotientRemainder, a0, a1, a2);
	}

	public static IAST PolynomialRemainder(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(PolynomialRemainder, a0, a1, a2);
	}

	/**
	 * Pop the current top value from the symbols local variable stack.
	 * 
	 * @param temp
	 */
	// public static void popLocal(ISymbol temp) {
	// temp.popLocalVariable();
	// }

	public static IAST Position(final IExpr a0, final IExpr a1) {
		return binaryAST2(Position, a0, a1);
	}

	public static IAST Positive(final IExpr a0) {
		return unaryAST1(Positive, a0);
	}

	public static IAST PossibleZeroQ(final IExpr a0) {
		return unaryAST1(PossibleZeroQ, a0);
	}

	public static IAST pow(final IExpr a0, final IExpr a1) {
		return binaryAST2(Power, a0, a1);
	}

	public static IAST Power(final IExpr a0, final IExpr a1) {
		return binaryAST2(Power, a0, a1);
	}

	public static IExpr Power(final IExpr a0, final long exp) {
		if (a0.isNumber()) {
			if (exp > 0L) {
				return a0.power(exp);
			}
			if (exp == -1L) {
				return a0.inverse();
			}
			if (exp == 0L && !a0.isZero()) {
				return C1;
			}
		}
		return binaryAST2(Power, a0, integer(exp));
	}

	public static IAST PowerExpand(final IExpr a0) {

		return unaryAST1(PowerExpand, a0);
	}

	/**
	 * Create a "predefined" symbol for constants or function names.
	 * 
	 * @param symbolName
	 * @return
	 */
	public static ISymbol predefinedSymbol(final String symbolName) {
		ISymbol temp = Context.SYSTEM.get(symbolName);
		if (temp != null) {
			return temp;
		}
		String lcSymbolName = symbolName;
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			if (symbolName.length() > 1) {
				// use the lower case string here to use it as associated class
				// name
				// in package org.matheclipse.core.reflection.system
				lcSymbolName = symbolName.toLowerCase(Locale.ENGLISH);
			}
		}
		temp = new Symbol(lcSymbolName, Context.SYSTEM);
		Context.SYSTEM.put(lcSymbolName, temp);
		return temp;
	}

	public static IAST Prepend(final IExpr a0, final IExpr a1) {

		return binaryAST2(Prepend, a0, a1);
	}

	public static IAST PrimeOmega(final IExpr a0) {
		return unaryAST1(PrimeOmega, a0);
	}

	public static IAST PrimeQ(final IExpr a0) {

		return unaryAST1(PrimeQ, a0);
	}

	public static IAST Print(final IExpr... a) {
		return ast(a, Print);
	}

	public static IAST Product(final IExpr a0, final IExpr a1) {
		return binaryAST2(Product, a0, a1);
	}

	/**
	 * Iterate over an integer range <code>from <= i <= to</code> and create a product of the created values.
	 * 
	 * @param function
	 *            the function which should be applied on each iterator value
	 * @param from
	 * @param to
	 * @return
	 */
	public static IAST product(final Function<IExpr, IExpr> function, final int from, final int to) {
		return intIterator(F.Times, function, from, to, 1);
	}

	public static IAST ProductLog(final IExpr a0) {
		return unaryAST1(ProductLog, a0);
	}

	public static IAST ProductLog(final IExpr a0, final IExpr a1) {
		return binaryAST2(ProductLog, a0, a1);
	}

	public static IAST PseudoInverse(final IExpr a0) {
		return unaryAST1(PseudoInverse, a0);
	}

	/**
	 * Create a "fractional" number
	 * 
	 * @param numerator
	 *            numerator of the fractional number
	 * @param fDenominator
	 *            denumerator of the fractional number
	 * @return IFraction
	 */
	public static IFraction QQ(final BigFraction frac) {
		return AbstractFractionSym.valueOf(frac);
	}

	/**
	 * Create a "fractional" number
	 * 
	 * @param numerator
	 *            numerator of the fractional number
	 * @param denominator
	 *            denumerator of the fractional number
	 * @return IFraction
	 */
	public static IFraction QQ(final IInteger numerator, final IInteger denominator) {
		return AbstractFractionSym.valueOf(numerator, denominator);
	}

	/**
	 * Create a "fractional" number
	 * 
	 * @param numerator
	 *            numerator of the fractional number
	 * @param denominator
	 *            denumerator of the fractional number
	 * @return IFraction
	 */
	public static IFraction QQ(final long numerator, final long denominator) {
		return AbstractFractionSym.valueOf(numerator, denominator);
	}

	public static IAST QRDecomposition(final IExpr a0) {
		return unaryAST1(QRDecomposition, a0);
	}

	public final static IASTMutable quaternary(final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2,
			final IExpr a3) {
		return new AST(new IExpr[] { head, a0, a1, a2, a3 });
	}

	public static IAST Quantile(final IExpr a0) {
		return unaryAST1(Quantile, a0);
	}

	public static IAST Quantile(final IExpr a0, final IExpr a1) {
		return binaryAST2(Quantile, a0, a1);
	}

	public static IAST Quiet(final IExpr a0) {
		return unaryAST1(Quiet, a0);
	}

	public final static IASTMutable quinary(final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2,
			final IExpr a3, final IExpr a4) {
		return new AST(new IExpr[] { head, a0, a1, a2, a3, a4 });
	}

	public static IAST Quotient(final IExpr a0, final IExpr a1) {
		return binaryAST2(Quotient, a0, a1);
	}

	public static IAST RandomVariate(final IExpr a0) {
		return unaryAST1(RandomVariate, a0);
	}

	public static IAST RandomVariate(final IExpr a0, final IExpr a1) {
		return binaryAST2(RandomVariate, a0, a1);
	}

	public static IAST Range(final IExpr a0) {
		return unaryAST1(Range, a0);
	}

	public static IAST Range(final IExpr a0, final IExpr a1) {
		return binaryAST2(Range, a0, a1);
	}

	public static IAST Rational(final IExpr a0, final IExpr a1) {
		return binaryAST2(Rational, a0, a1);
	}

	public static IExpr Re(final IExpr a0) {
		if (a0 != null && a0.isNumber()) {
			return ((INumber) a0).re();
		}
		return unaryAST1(Re, a0);
	}

	public static IAST RealNumberQ(final IExpr a) {
		return unaryAST1(RealNumberQ, a);
	}

	public static IAST Reap(final IExpr a) {
		return unaryAST1(Reap, a);
	}

	public static IAST Refine(final IExpr a) {
		return unaryAST1(Refine, a);
	}

	public static IAST Refine(final IExpr a0, final IExpr a1) {
		return binaryAST2(Refine, a0, a1);
	}

	public static IAST ReplaceAll(final IExpr a0, final IExpr a1) {
		return binaryAST2(ReplaceAll, a0, a1);
	}

	public static IAST ReplacePart(final IExpr a0, final IExpr a1) {
		return binaryAST2(ReplacePart, a0, a1);
	}

	/**
	 * 
	 * @param a0
	 * @param a1
	 * @param a2
	 * @return
	 */
	public static IAST ReplacePart(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(ReplacePart, a0, a1, a2);
	}

	public static IAST Rest(final IExpr a0) {
		return unaryAST1(Rest, a0);
	}

	public static IAST Resultant(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Resultant, a0, a1, a2);
	}

	public static IBuiltInSymbol symbol(int id) {
		return BUILT_IN_SYMBOLS[id];
	}

	/**
	 * Get or create a user defined symbol which is retrieved from the evaluation engines context path.
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @return the symbol object from the context path
	 */
	public static ISymbol symbol(final String symbolName) {
		return symbol(symbolName, null, EvalEngine.get());
	}

	/**
	 * Get or create a user defined symbol which is retrieved from the evaluation engines context path.
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @param engine
	 *            the evaluation engine
	 * @return the symbol object from the context path
	 */
	public static ISymbol symbol(final String symbolName, EvalEngine engine) {
		return symbol(symbolName, null, engine);
	}

	/**
	 * Get or create a user defined symbol which is retrieved from the evaluation engines context path. Additional set
	 * assumptions to the engines global assumptions. Use <code>#1</code> or {@link F#Slot1} in the
	 * <code>assumptionAST</code> expression for this symbol.
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @param assumptionAST
	 *            the assumptions which should be set for the symbol. Use <code>#1</code> or {@link F#Slot1} in the
	 *            <code>assumptionAST</code> expression for this symbol.
	 * @return the symbol object from the context path
	 */
	public static ISymbol symbol(final String symbolName, IAST assumptionAST) {
		return symbol(symbolName, assumptionAST, EvalEngine.get());
	}

	/**
	 * Get or create a user defined symbol which is retrieved from the evaluation engines context path. Additional set
	 * assumptions to the engines global assumptions. Use <code>#1</code> or {@link F#Slot1} in the
	 * <code>assumptionAST</code> expression for this symbol.
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @param assumptionAST
	 *            the assumptions which should be set for the symbol. Use <code>#1</code> or {@link F#Slot1} in the
	 *            <code>assumptionAST</code> expression for this symbol.
	 * @param engine
	 *            the evaluation engine
	 * @return the symbol object from the context path
	 */
	public static ISymbol symbol(final String symbolName, IAST assumptionAST, EvalEngine engine) {
		ISymbol symbol = engine.getContextPath().getSymbol(symbolName);
		if (assumptionAST != null) {
			IExpr temp = Lambda.replaceSlots(assumptionAST, F.List(symbol));
			if (!temp.isPresent()) {
				temp = assumptionAST;
			}
			if (temp.isAST()) {
				IAssumptions assumptions = engine.getAssumptions();
				if (assumptions == null) {
					assumptions = org.matheclipse.core.eval.util.Assumptions.getInstance(temp);
					engine.setAssumptions(assumptions);
				} else {
					assumptions.addAssumption((IAST) temp);
				}
			}
		}
		return symbol;
	}

	/**
	 * Get or create a user defined symbol which is retrieved from the evaluation engines context path.
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @return the symbol object from the context path
	 * @deprecated use {@link #symbol(String)}
	 */
	public static ISymbol userSymbol(final String symbolName) {
		return symbol(symbolName, null, EvalEngine.get());
	}

	/**
	 * Get or create a user defined symbol which is retrieved from the evaluation engines context path.
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @param engine
	 *            the evaluation engine
	 * @return the symbol object from the context path
	 * @deprecated use {@link #symbol(String, EvalEngine)}
	 */
	public static ISymbol userSymbol(final String symbolName, EvalEngine engine) {
		return symbol(symbolName, null, engine);
	}

	/**
	 * Create a unique dummy symbol which is retrieved from the evaluation engines context path.
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @param engine
	 *            the evaluation engine
	 * @return the symbol object from the context path
	 */
	public static ISymbol Dummy(final String symbolName) {
		String name = symbolName;
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			if (symbolName.length() == 1) {
				name = symbolName;
			} else {
				name = symbolName.toLowerCase(Locale.ENGLISH);
			}
		}
		return new Symbol(name, Context.DUMMY);
	}

	public static IBuiltInSymbol localBiFunction(final String symbolName, BiFunction<IExpr, IExpr, IExpr> function) {
		IBuiltInSymbol localBuittIn = new BuiltInSymbol(symbolName, java.lang.Integer.MAX_VALUE);
		localBuittIn.setEvaluator(new AbstractEvaluator() {
			@Override
			public IExpr evaluate(IAST ast, EvalEngine engine) {
				return function.apply(ast.arg1(), ast.arg2());
			}
		});
		return localBuittIn;
	}

	public static IBuiltInSymbol localFunction(final String symbolName, Function<IExpr, IExpr> function) {
		IBuiltInSymbol localBuittIn = new BuiltInSymbol(symbolName, java.lang.Integer.MAX_VALUE);
		localBuittIn.setEvaluator(new AbstractEvaluator() {
			@Override
			public IExpr evaluate(IAST ast, EvalEngine engine) {
				return function.apply(ast.arg1());
			}
		});
		return localBuittIn;
	}

	public static IBuiltInSymbol localBiPredicate(final String symbolName, BiPredicate<IExpr, IExpr> function) {
		IBuiltInSymbol localBuittIn = new BuiltInSymbol(symbolName, java.lang.Integer.MAX_VALUE);
		localBuittIn.setEvaluator(new AbstractEvaluator() {
			@Override
			public IExpr evaluate(IAST ast, EvalEngine engine) {
				return F.bool(function.test(ast.arg1(), ast.arg2()));
			}
		});
		return localBuittIn;
	}

	public static IBuiltInSymbol localPredicate(final String symbolName, Predicate<IExpr> function) {
		IBuiltInSymbol localBuittIn = new BuiltInSymbol(symbolName, java.lang.Integer.MAX_VALUE);
		localBuittIn.setEvaluator(new AbstractEvaluator() {
			@Override
			public IExpr evaluate(IAST ast, EvalEngine engine) {
				return F.bool(function.test(ast.arg1()));
			}
		});
		return localBuittIn;
	}

	/**
	 * Remove a user-defined symbol from the eval engines context path. Doesn't remove predefined names from the System
	 * Context.
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @return the removed symbol or <code>null</code> if no symbol was found
	 */
	public static ISymbol removeUserSymbol(final String symbolName) {
		ContextPath contextPath = EvalEngine.get().getContextPath();
		return contextPath.removeSymbol(symbolName);
	}

	public static IAST Return(final IExpr a) {
		return unaryAST1(Return, a);
	}

	public static IAST Reverse(final IExpr a) {
		return unaryAST1(Reverse, a);
	}

	public static IAST Root(final IExpr a0, final IExpr a1) {
		return binaryAST2(Root, a0, a1);
	}

	public static IAST Roots(final IExpr a0) {
		return unaryAST1(Roots, a0);
	}

	public static IAST Roots(final IExpr a0, final IExpr a1) {
		return binaryAST2(Roots, a0, a1);
	}

	public static IAST Round(final IExpr a0) {
		return unaryAST1(Round, a0);
	}

	public static IAST RowReduce(final IExpr a0) {
		return unaryAST1(RowReduce, a0);
	}

	public static IAST Rule(final IExpr a0, final IExpr a1) {
		return binaryAST2(Rule, a0, a1);
	}

	public static IAST RuleDelayed(final IExpr a0, final IExpr a1) {
		return binaryAST2(RuleDelayed, a0, a1);
	}

	public static IAST SameQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(SameQ, a0, a1);
	}

	public static IAST SameQ(final IExpr a0, final double d) {
		return binaryAST2(SameQ, a0, F.num(d));
	}

	public static IAST Scan(final IExpr a0, final IExpr a1) {
		return binaryAST2(Scan, a0, a1);
	}

	public static IAST Sec(final IExpr a0) {
		return unaryAST1(Sec, a0);
	}

	public static IAST Sech(final IExpr a0) {
		return unaryAST1(Sech, a0);
	}

	public static IAST Select(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Select, a0, a1, a2);
	}

	public final static IAST senary(final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
			final IExpr a4, final IExpr a5) {
		return new AST(new IExpr[] { head, a0, a1, a2, a3, a4, a5 });
	}

	public static IASTAppendable Sequence() {
		return ast(Sequence);
	}

	public static IAST Sequence(final IExpr a0) {
		return unary(Sequence, a0);
	}

	public static IAST Sequence(final IExpr... a) {
		return ast(a, Sequence);
	}

	public static IAST Series(final IExpr... a) {
		return ast(a, Series);
	}

	public static IAST SeriesCoefficient(final IExpr a0, final IExpr a1) {
		return binaryAST2(SeriesCoefficient, a0, a1);
	}

	public static IAST SeriesData(final IExpr... a) {
		return ast(a, SeriesData);
	}

	public static IAST Set(final IExpr a0, final IExpr a1) {
		return binaryAST2(Set, a0, a1);
	}

	public static IAST SetAttributes(final IExpr a0) {
		return unaryAST1(SetAttributes, a0);
	}

	public static IAST SetAttributes(final IExpr a0, final IExpr a1) {
		return binaryAST2(SetAttributes, a0, a1);
	}

	public static IAST SetDelayed(final IExpr a0, final IExpr a1) {
		return binaryAST2(SetDelayed, a0, a1);
	}

	public static IAST Show(final IExpr a0) {
		return unaryAST1(Show, a0);
	}

	public static IAST Sign(final IExpr a) {
		return unaryAST1(Sign, a);
	}

	public static IAST SignCmp(final IExpr a0) {
		return unaryAST1(SignCmp, a0);
	}

	public static IAST Simplify(final IExpr a0) {
		return unaryAST1(F.Simplify, a0);
	}

	public static IAST Sin(final IExpr a0) {
		return unaryAST1(Sin, a0);
	}

	public static IAST Sinc(final IExpr a0) {
		return unaryAST1(Sinc, a0);
	}

	public static IAST Sinh(final IExpr a0) {

		return unaryAST1(Sinh, a0);
	}

	public static IAST SinhIntegral(final IExpr a) {
		return unaryAST1(SinhIntegral, a);
	}

	public static IAST SinIntegral(final IExpr a) {
		return unaryAST1(SinIntegral, a);
	}

	public static IAST Slot(final IExpr a0) {
		return unaryAST1(Slot, a0);
	}

	public static IAST Slot(final int i) {
		return unaryAST1(Slot, integer(i));
	}

	public static IAST SlotSequence(final int i) {
		return unaryAST1(SlotSequence, integer(i));
	}

	public static IAST Solve(final IExpr a0, final IExpr a1) {
		return binaryAST2(Solve, a0, a1);
	}

	public static IAST Sort(final IExpr a0, final IExpr a1) {
		return binaryAST2(Sort, a0, a1);
	}

	public static IAST Sow(final IExpr a) {
		return unaryAST1(Sow, a);
	}

	public static IAST Span(final IExpr... a) {
		return ast(a, Span);
	}

	/**
	 * Create a "square" expression: <code>Power(x, 2)</code>.
	 * 
	 * @param x
	 * @return
	 */
	public static IAST Sqr(final IExpr x) {
		return binaryAST2(Power, x, C2);
	}

	/**
	 * Create a "square root" expression: <code>Power(x, 1/2)</code>.
	 * 
	 * @param x
	 * @return
	 */
	public static IAST Sqrt(final IExpr x) {
		return binaryAST2(Power, x, C1D2);
	}

	public static IAST StandardDeviation(final IExpr a0) {
		return unaryAST1(StandardDeviation, a0);
	}

	public static IAST Standardize(final IExpr a0) {
		return unaryAST1(Standardize, a0);
	}

	public static IAST StieltjesGamma(final IExpr a0) {
		return unaryAST1(StieltjesGamma, a0);
	}

	public static IAST StieltjesGamma(final IExpr a0, final IExpr a1) {
		return binaryAST2(StieltjesGamma, a0, a1);
	}

	public static IAST StirlingS1(final IExpr a0, final IExpr a1) {
		return binaryAST2(StirlingS1, a0, a1);
	}

	public static IAST StirlingS2(final IExpr a0, final IExpr a1) {
		return binaryAST2(StirlingS2, a0, a1);
	}

	public static IAST StringJoin(final IExpr a) {
		return unaryAST1(StringJoin, a);
	}

	public static IAST Surd(final IExpr a0, final IExpr a1) {
		return binaryAST2(Surd, a0, a1);
	}

	/**
	 * Create a string expression
	 * 
	 * @param str
	 * @return
	 */
	final static public IStringX stringx(final String str) {
		return StringX.valueOf(str);
	}

	/**
	 * Create a string expression
	 * 
	 * @param str
	 * @return
	 */
	final static public IStringX stringx(final StringBuilder str) {
		return StringX.valueOf(str);
	}

	public static IAST StruveH(final IExpr a0, final IExpr a1) {
		return binaryAST2(StruveH, a0, a1);
	}

	public static IAST StruveL(final IExpr a0, final IExpr a1) {
		return binaryAST2(StruveL, a0, a1);
	}

	public static IAST StudentTDistribution(final IExpr a0) {
		return unaryAST1(StudentTDistribution, a0);
	}

	public static IAST StudentTDistribution(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(StudentTDistribution, a0, a1, a2);
	}

	public static IAST Subdivide(final IExpr a0) {
		return unaryAST1(Subdivide, a0);
	}

	public static IAST Subdivide(final IExpr a0, final IExpr a1) {
		return binaryAST2(Subdivide, a0, a1);
	}

	public static IAST Subdivide(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Subdivide, a0, a1, a2);
	}

	public static IAST Subfactorial(final IExpr a0) {
		return unaryAST1(Subfactorial, a0);
	}

	/**
	 * Substitute all (sub-) expressions <code>x</code> with <code>y</code>. If no substitution matches, the method
	 * returns the given <code>expr</code>.
	 * 
	 * @param expr
	 *            the complete expresssion
	 * @param x
	 *            the subexpression which should be replaced
	 * @param y
	 *            the expression which replaces <code>x</code>
	 * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or the substituted
	 *         expression.
	 */
	public static IExpr subs(final IExpr expr, final IExpr x, final IExpr y) {
		return expr.replaceAll(F.Rule(x, y)).orElse(expr);
	}

	/**
	 * Substitute all (sub-) expressions with the given unary function. If no substitution matches, the method returns
	 * the given <code>expr</code>.
	 * 
	 * @param expr
	 * @param function
	 *            if the unary functions <code>apply()</code> method returns <code>null</code> the expression isn't
	 *            substituted.
	 * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or the substituted
	 *         expression.
	 */
	public static IExpr subst(IExpr expr, final Function<IExpr, IExpr> function) {
		return expr.replaceAll(function).orElse(expr);
	}

	/**
	 * Substitute all (sub-) expressions with the given rule set. If no substitution matches, the method returns the
	 * given <code>expr</code>.
	 * 
	 * @param expr
	 * @param astRules
	 *            rules of the form <code>x-&gt;y</code> or <code>{a-&gt;b, c-&gt;d}</code>; the left-hand-side of the
	 *            rule can contain pattern objects.
	 * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or the substituted
	 *         expression.
	 */
	public static IExpr subst(IExpr expr, final IAST list) {
		if (list.isListOfLists()) {
			IExpr result = expr;
			for (IExpr subList : list) {
				result = F.subst(result, (IAST) subList);
			}
			return result;
		}
		return expr.replaceAll(list).orElse(expr);
	}

	/**
	 * Substitute all (sub-) expressions with the given replacement expression. If no (sub-) expression matches, the
	 * method returns the given <code>expr</code>.
	 * 
	 * @param expr
	 * @param subExpr
	 * @param replacementExpr
	 * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or the substituted
	 *         expression.
	 */
	public static IExpr subst(IExpr expr, IExpr subExpr, IExpr replacementExpr) {
		return expr.replaceAll(Functors.rules(Rule(subExpr, replacementExpr), EvalEngine.get())).orElse(expr);
	}

	/**
	 * Return <code>arg1 + (-1)*arg2</code>
	 * 
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	public static IAST Subtract(final IExpr arg1, final IExpr arg2) {
		if (arg1.isPlus()) {
			if (arg2.isZero()) {
				return (IAST) arg1;
			}
			IASTAppendable clone = F.PlusAlloc(arg1.size() + 1);
			clone.appendArgs((IAST) arg1);
			clone.append(binaryAST2(Times, CN1, arg2));
			return clone;
		}
		return binaryAST2(Plus, arg1, binaryAST2(Times, CN1, arg2));
	}

	public static IAST Sum(final IExpr a0, final IExpr a1) {
		return binaryAST2(Sum, a0, a1);
	}

	public static IAST Sum(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(Sum, a0, a1, a2);
	}

	/**
	 * Evaluate the sum from <code>iMin</code> to <code>iMax</code> and step <code>1</code>.
	 * 
	 * @param function
	 * @param iMin
	 * @param iMax
	 * @return
	 */
	public static IAST sum(final Function<IExpr, IExpr> function, final int iMin, final int iMax) {
		return intIterator(F.Plus, function, iMin, iMax, 1);
	}

	/**
	 * Evaluate the sum from <code>iMin</code> to <code>iMax</code> and step <code>iStep</code>.
	 * 
	 * @param function
	 * @param iMin
	 * @param iMax
	 * @param iStep
	 * @return
	 */
	public static IAST sum(final Function<IExpr, IExpr> function, final int iMin, final int iMax, final int iStep) {
		return intIterator(F.Plus, function, iMin, iMax, iStep);
	}

	public static IASTAppendable SurfaceGraphics() {

		return ast(SurfaceGraphics);
	}

	public static IAST Switch(final IExpr... a) {
		return ast(a, Switch);
	}

	public static IAST Table(final IExpr a0, final IExpr a1) {
		return binaryAST2(Table, a0, a1);
	}

	public static IAST Take(final IExpr a0, final IExpr a1) {
		return binaryAST2(Take, a0, a1);
	}

	public static IAST Tan(final IExpr a0) {
		return unaryAST1(Tan, a0);
	}

	public static IAST Tanh(final IExpr a0) {

		return unaryAST1(Tanh, a0);
	}

	public static IAST Taylor(final IExpr a0, final IExpr a1) {
		return binaryAST2(Taylor, a0, a1);
	}

	public static IAST TeXForm(final IExpr a0) {
		return unaryAST1(TeXForm, a0);
	}

	public final static IAST ternary(final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2) {
		return new AST(new IExpr[] { head, a0, a1, a2 });
	}

	/**
	 * Create a function with 3 arguments as a <code>AST3</code> immutable object without evaluation.
	 * 
	 * @param head
	 * @param a0
	 * @param a1
	 * @param a2
	 * @return
	 */
	public final static IASTMutable ternaryAST3(final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2) {
		return new AST3(head, a0, a1, a2);
	}

	public static IAST Thread(final IExpr a0) {
		return unaryAST1(Thread, a0);
	}

	public static IAST Throw(final IExpr a) {
		return unaryAST1(Throw, a);
	}

	public static IAST TimeConstrained(final IExpr a0, final IExpr a1) {
		return binaryAST2(TimeConstrained, a0, a1);
	}

	public static IAST TimeConstrained(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(TimeConstrained, a0, a1, a2);
	}

	/**
	 * Create a Times() function.
	 * 
	 * @return
	 */
	public static IAST Times() {
		return ast(Times);
	}

	/**
	 * Create a Times() function with allocated space for size elements.
	 * 
	 * @param size
	 * @return
	 */
	public static IASTAppendable TimesAlloc(int size) {
		return ast(Times, size, false);
	}

	public static IASTAppendable Times(final IExpr a0) {
		return unary(Times, a0);
	}

	public static IAST Times(final IExpr... a) {
		return ast(a, Times);
	}

	public static IASTMutable Times(final IExpr a0, final IExpr a1) {
		if (a0 != null && a1 != null) {
			if (a0.isTimes() || a1.isTimes()) {
				int size = 0;
				if (a0.isTimes()) {
					size += a0.size();
				} else {
					size++;
				}
				if (a1.isTimes()) {
					size += a1.size();
				} else {
					size++;
				}
				IASTAppendable result = TimesAlloc(size);
				if (a0.isTimes()) {
					result.appendArgs((IAST) a0);
				} else {
					result.append(a0);
				}
				if (a1.isTimes()) {
					result.appendArgs((IAST) a1);
				} else {
					result.append(a1);
				}
				EvalAttributes.sort(result);
				return result;
			}
			if (a0.compareTo(a1) > 0) {
				// swap arguments
				return binaryAST2(Times, a1, a0);
			}
		}
		return binaryAST2(Times, a0, a1);
	}

	public static IAST Times(final long num, final IExpr... a) {
		return ast(a, Times).prependClone(ZZ(num));
	}

	public static IAST Together(final IExpr a0) {
		return unaryAST1(Together, a0);
	}

	public static IAST Total(final IExpr a0) {
		return unaryAST1(Total, a0);
	}

	public static IAST Tr(final IExpr a0) {
		return unaryAST1(Tr, a0);
	}

	public static IAST Trace(final IExpr a0) {
		return unaryAST1(Trace, a0);
	}

	public static IAST Transpose(final IExpr a0) {
		return unaryAST1(Transpose, a0);
	}

	public static IAST TrigExpand(final IExpr a0) {
		return unaryAST1(TrigExpand, a0);
	}

	public static IAST TrigReduce(final IExpr v) {
		return unaryAST1(TrigReduce, v);
	}

	public static IAST TrigToExp(final IExpr a0) {
		return unaryAST1(TrigToExp, a0);
	}

	/**
	 * Create a function with 1 argument without evaluation.
	 * 
	 * @param head
	 * @param a0
	 * @return
	 */
	public final static IASTAppendable unary(final IExpr head, final IExpr a0) {
		return new AST(new IExpr[] { head, a0 });
	}

	/**
	 * Create a function with 1 argument as a <code>AST1</code> immutable object without evaluation.
	 * 
	 * @param head
	 * @param a0
	 * @return
	 */
	public final static IASTMutable unaryAST1(final IExpr head, final IExpr a0) {
		return new AST1(head, a0);
	}

	public static IAST Unequal(final IExpr a0, final IExpr a1) {
		return binaryAST2(Unequal, a0, a1);
	}

	public static IAST Unevaluated(final IExpr a0) {
		return unaryAST1(Unevaluated, a0);
	}

	public static IAST Unique(final IExpr a0) {
		return unaryAST1(Unique, a0);
	}

	public static IAST UnitStep(final IExpr a0) {
		return unaryAST1(UnitStep, a0);
	}

	public static IAST UnsameQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(UnsameQ, a0, a1);
	}

	public static IAST Unset(final IExpr a0) {
		return unaryAST1(Unset, a0);
	}

	public static IAST UpSet(final IExpr a0, final IExpr a1) {
		return binaryAST2(UpSet, a0, a1);
	}

	public static IAST UpSetDelayed(final IExpr a0, final IExpr a1) {
		return binaryAST2(UpSetDelayed, a0, a1);
	}

	public static IAST Variance(final IExpr a0) {
		return unaryAST1(Variance, a0);
	}

	public static IAST WeibullDistribution(final IExpr a0, final IExpr a1) {
		return binaryAST2(WeibullDistribution, a0, a1);
	}

	public static IAST WeibullDistribution(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(WeibullDistribution, a0, a1, a2);
	}

	public static IAST While(final IExpr a0, final IExpr a1) {
		return binaryAST2(While, a0, a1);
	}

	public static IAST With(final IExpr a0, final IExpr a1) {
		return binaryAST2(With, a0, a1);
	}

	public static IAST Zeta(final IExpr a0) {
		return unaryAST1(Zeta, a0);
	}

	public static IAST Zeta(final IExpr a0, final IExpr a1) {
		return binaryAST2(Zeta, a0, a1);
	}

	/**
	 * Create a large integer number.
	 * 
	 * @param integerValue
	 * @return
	 */
	public static IInteger ZZ(final BigInteger integerValue) {
		return AbstractIntegerSym.valueOf(integerValue);
	}

	/**
	 * Create a large integer number.
	 * 
	 * @param integerValue
	 * @return
	 */
	public static IInteger ZZ(final long integerValue) {
		return AbstractIntegerSym.valueOf(integerValue);
	}

	public static IExpr operatorFormAST1(final IAST ast) {
		if (ast.head().isAST1() && ast.isAST1()) {
			return binaryAST2(ast.topHead(), ast.arg1(), ((IAST) ast.head()).arg1());
		}
		return NIL;
	}

	/**
	 * Generate a <code>n x m</code> matrix.
	 * 
	 * @param biFunction
	 * @param n
	 *            the number of rows of the matrix.
	 * @param m
	 *            the number of elements in one row
	 * @return
	 */
	public static IAST matrix(BiFunction<Integer, Integer, ? extends IExpr> biFunction, int n, int m) {
		IASTAppendable matrix = F.ListAlloc(n);
		for (int i = 0; i < n; i++) {
			IASTAppendable row = F.ListAlloc(m);
			for (int j = 0; j < m; j++) {
				row.append(biFunction.apply(i, j));
			}
			matrix.append(row);
		}
		matrix.addEvalFlags(IAST.IS_MATRIX);
		return matrix;
	}

	/**
	 * Generate a vector with <code>n</code> elements.
	 * 
	 * @param iFunction
	 * @param n
	 *            the number of elements of the vector.
	 * @return
	 */
	public static IAST vector(IntFunction<? extends IExpr> iFunction, int n) {
		IASTAppendable matrix = F.ListAlloc(n);
		for (int i = 0; i < n; i++) {
			matrix.append(iFunction.apply(i));
		}
		matrix.addEvalFlags(IAST.IS_VECTOR);
		return matrix;
	}

}