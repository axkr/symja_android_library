package org.matheclipse.core.expression;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.fraction.BigFraction;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Namespace;
import org.matheclipse.core.eval.SystemNamespace;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

import com.google.common.base.Function;

/**
 * Factory for creating MathEclipse expression objects.
 * 
 * See <a href="http://code.google.com/p/symja/wiki/AddNewFunctions">AddNewFunctions </a>
 */
public class F {

	/**
	 * Set to <code>true</code> at the start of initSymbols() method
	 */
	public static boolean isSystemStarted = false;

	/**
	 * Set to <code>true</code> at the end of initSymbols() method
	 */
	public static boolean isSystemInitialized = false;

	/**
	 * The map for predefined strings for the {@link IExpr#internalFormString(boolean, int)} method.
	 */
	public final static Map<String, String> PREDEFINED_INTERNAL_FORM_STRINGS = new HashMap<String, String>(61);

	public final static Map<String, IPattern> PREDEFINED_PATTERN_MAP = new HashMap<String, IPattern>(61);

	/**
	 * The map for predefined symbols
	 */
	public final static Map<String, ISymbol> PREDEFINED_SYMBOLS_MAP = new HashMap<String, ISymbol>(997);

	public static ISymbolObserver SYMBOL_OBSERVER = new ISymbolObserver() {
		@Override
		public final boolean createPredefinedSymbol(String symbol) {
			return false;
		}

		@Override
		public void createUserSymbol(ISymbol symbol) {

		}

	};

	public final static ISymbol Catalan = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "catalan" : "Catalan",
			new org.matheclipse.core.builtin.constant.Catalan());
	public final static ISymbol ComplexInfinity = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "complexinfinity"
			: "ComplexInfinity", new org.matheclipse.core.builtin.constant.ComplexInfinity());
	public final static ISymbol Degree = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "degree" : "Degree",
			new org.matheclipse.core.builtin.constant.Degree());
	public final static ISymbol E = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "e" : "E",
			new org.matheclipse.core.builtin.constant.E());
	public final static ISymbol EulerGamma = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "eulergamma" : "EulerGamma",
			new org.matheclipse.core.builtin.constant.EulerGamma());
	public final static ISymbol Glaisher = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "glaisher" : "Glaisher",
			new org.matheclipse.core.builtin.constant.Glaisher());
	public final static ISymbol GoldenRatio = F.initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "goldenratio" : "GoldenRatio",
			new org.matheclipse.core.builtin.constant.GoldenRatio());
	public final static ISymbol I = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "i" : "I",
			new org.matheclipse.core.builtin.constant.I());
	public final static ISymbol Infinity = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "infinity" : "Infinity",
			new org.matheclipse.core.builtin.constant.Infinity());
	public final static ISymbol Khinchin = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "khinchin" : "Khinchin",
			new org.matheclipse.core.builtin.constant.Khinchin());
	public final static ISymbol Pi = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "pi" : "Pi",
			new org.matheclipse.core.builtin.constant.Pi());

	public final static ISymbol IntegerHead = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "integer" : "Integer");
	public final static ISymbol SymbolHead = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "symbol" : "Symbol");
	public final static ISymbol RealHead = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "real" : "Real");
	public final static ISymbol PatternHead = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "pattern" : "Pattern");
	public final static ISymbol BlankHead = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "blank" : "Blank");
	public final static ISymbol StringHead = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "string" : "String");
	public final static ISymbol MethodHead = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "methodhead" : "MethodHead");

	public final static ISymbol False = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "false" : "False");
	public final static ISymbol List = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "list" : "List");
	public final static ISymbol True = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "true" : "True");
	public final static ISymbol Null = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "null" : "Null");
	public final static ISymbol Second = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "second" : "Second");
	public final static ISymbol Indeterminate = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "indeterminate"
			: "Indeterminate");
	public final static ISymbol DirectedInfinity = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "directedinfinity"
			: "DirectedInfinity");
	public final static ISymbol Listable = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "listable" : "Listable");
	public final static ISymbol Constant = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "constant" : "Constant");
	public final static ISymbol NumericFunction = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "numericfunction"
			: "NumericFunction");
	public final static ISymbol Orderless = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "orderless" : "Orderless");
	public final static ISymbol OneIdentity = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "oneidentity" : "OneIdentity");
	public final static ISymbol Flat = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "flat" : "Flat");
	public final static ISymbol HoldFirst = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "holdfirst" : "HoldFirst");
	public final static ISymbol HoldRest = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "holdrest" : "HoldRest");
	public final static ISymbol HoldAll = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "holdall" : "HoldAll");
	public final static ISymbol NHoldFirst = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nholdfirst" : "NHoldFirst");
	public final static ISymbol NHoldRest = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nholdrest" : "NHoldRest");
	public final static ISymbol NHoldAll = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nholdall" : "NHoldAll");
	public final static ISymbol Line = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "line" : "Line");
	public final static ISymbol BoxRatios = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "boxratios" : "BoxRatios");
	public final static ISymbol MeshRange = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "meshrange" : "MeshRange");
	public final static ISymbol PlotRange = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "plotrange" : "PlotRange");
	public final static ISymbol AxesStyle = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "axesstyle" : "AxesStyle");
	public final static ISymbol Automatic = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "automatic" : "Automatic");
	public final static ISymbol AxesOrigin = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "axesorigin" : "AxesOrigin");
	public final static ISymbol Axes = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "axes" : "Axes");
	public final static ISymbol Background = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "background" : "Background");
	public final static ISymbol White = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "white" : "White");
	public final static ISymbol Slot = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "slot" : "Slot");
	public final static ISymbol SlotSequence = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "slotsequence"
			: "SlotSequence");
	public final static ISymbol Options = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "options" : "Options");
	public final static ISymbol Graphics = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "graphics" : "Graphics");
	public final static ISymbol Show = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "show" : "Show");
	public final static ISymbol SurfaceGraphics = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "surfacegraphics"
			: "SurfaceGraphics");
	public final static ISymbol ArcCosh = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arccosh" : "ArcCosh");
	public final static ISymbol ArcSinh = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arcsinh" : "ArcSinh");
	public final static ISymbol ArcTanh = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arctanh" : "ArcTanh");
	public final static ISymbol Plot = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "plot" : "Plot");
	public final static ISymbol Plot3D = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "plot3d" : "Plot3D");
	public final static ISymbol RootOf = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rootof" : "RootOf");
	public final static ISymbol Sequence = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sequence" : "Sequence");

	public final static ISymbol Apply = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "apply" : "Apply",
			new org.matheclipse.core.builtin.function.Apply());
	public final static ISymbol Array = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "array" : "Array",
			new org.matheclipse.core.builtin.function.Array());
	public final static ISymbol Blank = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "blank" : "Blank",
			new org.matheclipse.core.builtin.function.Blank());
	public final static ISymbol Block = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "block" : "Block",
			new org.matheclipse.core.builtin.function.Block());
	public final static ISymbol Break = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "break" : "Break",
			new org.matheclipse.core.builtin.function.Break());
	public final static ISymbol Cases = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cases" : "Cases",
			new org.matheclipse.core.builtin.function.Cases());
	public final static ISymbol Catch = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "catch" : "Catch",
			new org.matheclipse.core.builtin.function.Catch());
	public final static ISymbol Clear = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "clear" : "Clear",
			new org.matheclipse.core.builtin.function.Clear());
	public final static ISymbol ClearAll = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "clearall" : "ClearAll",
			new org.matheclipse.core.builtin.function.ClearAll());
	public final static ISymbol Condition = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "condition" : "Condition",
			new org.matheclipse.core.builtin.function.Condition());
	public final static ISymbol Continue = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "continue" : "Continue",
			new org.matheclipse.core.builtin.function.Continue());
	public final static ISymbol Definition = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "definition" : "Definition",
			new org.matheclipse.core.builtin.function.Definition());
	public final static ISymbol Do = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "do" : "Do",
			new org.matheclipse.core.builtin.function.Do());
	public final static ISymbol FixedPoint = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fixedpoint" : "FixedPoint",
			new org.matheclipse.core.builtin.function.FixedPoint());
	public final static ISymbol Fold = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fold" : "Fold",
			new org.matheclipse.core.builtin.function.Fold());
	public final static ISymbol FoldList = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "foldlist" : "FoldList",
			new org.matheclipse.core.builtin.function.FoldList());
	public final static ISymbol For = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "for" : "For",
			new org.matheclipse.core.builtin.function.For());
	public final static ISymbol If = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "if" : "If",
			new org.matheclipse.core.builtin.function.If());
	public final static ISymbol Module = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "module" : "Module",
			new org.matheclipse.core.builtin.function.Module());
	public final static ISymbol N = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "n" : "N",
			new org.matheclipse.core.builtin.function.N());
	public final static ISymbol Nest = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nest" : "Nest",
			new org.matheclipse.core.builtin.function.Nest());
	public final static ISymbol NestList = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nestlist" : "NestList",
			new org.matheclipse.core.builtin.function.NestList());
	public final static ISymbol NestWhile = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nestwhile" : "NestWhile",
			new org.matheclipse.core.builtin.function.NestWhile());
	public final static ISymbol NestWhileList = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nestwhilelist"
			: "NestWhileList", new org.matheclipse.core.builtin.function.NestWhileList());
	public final static ISymbol Reap = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "reap" : "Reap",
			new org.matheclipse.core.builtin.function.Reap());
	public final static ISymbol Return = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "return" : "Return",
			new org.matheclipse.core.builtin.function.Return());
	public final static ISymbol Sow = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sow" : "Sow",
			new org.matheclipse.core.builtin.function.Sow());
	public final static ISymbol Switch = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "switch" : "Switch",
			new org.matheclipse.core.builtin.function.Switch());
	public final static ISymbol Throw = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "throw" : "Throw",
			new org.matheclipse.core.builtin.function.Throw());
	public final static ISymbol Which = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "which" : "Which",
			new org.matheclipse.core.builtin.function.Which());
	public final static ISymbol While = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "while" : "While",
			new org.matheclipse.core.builtin.function.While());

	public final static ISymbol Abs = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "abs" : "Abs");
	public final static ISymbol AddTo = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "addto" : "AddTo");
	public final static ISymbol And = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "and" : "And");
	public final static ISymbol Apart = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "apart" : "Apart");
	public final static ISymbol Append = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "append" : "Append");
	public final static ISymbol AppendTo = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "appendto" : "AppendTo");
	public final static ISymbol ArcCos = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arccos" : "ArcCos");
	public final static ISymbol ArcCot = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arccot" : "ArcCot");
	public final static ISymbol ArcSin = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arcsin" : "ArcSin");
	public final static ISymbol ArcTan = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arctan" : "ArcTan");
	public final static ISymbol Arg = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arg" : "Arg");
	public final static ISymbol AtomQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "atomq" : "AtomQ");
	public final static ISymbol Binomial = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "binomial" : "Binomial");
	public final static ISymbol Boole = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "boole" : "Boole");
	public final static ISymbol BooleanMinimize = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "booleanminimize"
			: "BooleanMinimize");
	public final static ISymbol Cancel = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cancel" : "Cancel");
	public final static ISymbol CartesianProduct = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cartesianproduct"
			: "CartesianProduct");
	public final static ISymbol CatalanNumber = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "catalannumber"
			: "CatalanNumber");
	public final static ISymbol Ceiling = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "ceiling" : "Ceiling");
	public final static ISymbol CharacteristicPolynomial = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "characteristicpolynomial" : "CharacteristicPolynomial");
	public final static ISymbol ChessboardDistance = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "chessboarddistance"
			: "ChessboardDistance");
	public final static ISymbol Chop = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "chop" : "Chop");
	public final static ISymbol Coefficient = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "coefficient" : "Coefficient");
	public final static ISymbol CoefficientList = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "coefficientlist"
			: "CoefficientList");
	public final static ISymbol Complement = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "complement" : "Complement");
	public final static ISymbol Complex = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "complex" : "Complex");
	public final static ISymbol ComposeList = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "composelist" : "ComposeList");
	public final static ISymbol CompoundExpression = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "compoundexpression"
			: "CompoundExpression");
	public final static ISymbol Conjugate = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "conjugate" : "Conjugate");
	public final static ISymbol ConjugateTranspose = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "conjugatetranspose"
			: "ConjugateTranspose");
	public final static ISymbol ConstantArray = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "constantarray"
			: "ConstantArray");
	public final static ISymbol ContinuedFraction = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "continuedfraction"
			: "ContinuedFraction");
	public final static ISymbol CoprimeQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "coprimeq" : "CoprimeQ");
	public final static ISymbol Cos = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cos" : "Cos");
	public final static ISymbol Cosh = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cosh" : "Cosh");
	public final static ISymbol Cot = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cot" : "Cot");
	public final static ISymbol Coth = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "coth" : "Coth");
	public final static ISymbol Count = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "count" : "Count");
	public final static ISymbol Cross = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cross" : "Cross");
	public final static ISymbol Csc = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "csc" : "Csc");
	public final static ISymbol Csch = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "csch" : "Csch");
	public final static ISymbol Curl = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "curl" : "Curl");
	public final static ISymbol D = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "d" : "D");
	public final static ISymbol Decrement = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "decrement" : "Decrement");
	public final static ISymbol Default = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "default" : "Default");
	public final static ISymbol Delete = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "delete" : "Delete");
	public final static ISymbol Denominator = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "denominator" : "Denominator");
	public final static ISymbol Depth = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "depth" : "Depth");
	public final static ISymbol Derivative = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "derivative" : "Derivative");
	public final static ISymbol Det = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "det" : "Det");
	public final static ISymbol DiagonalMatrix = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "diagonalmatrix"
			: "DiagonalMatrix");
	public final static ISymbol DigitQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "digitq" : "DigitQ");
	public final static ISymbol Dimensions = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "dimensions" : "Dimensions");
	public final static ISymbol Discriminant = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "discriminant"
			: "Discriminant");
	public final static ISymbol Distribute = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "distribute" : "Distribute");
	public final static ISymbol Divergence = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "divergence" : "Divergence");
	public final static ISymbol DivideBy = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "divideby" : "DivideBy");
	public final static ISymbol Dot = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "dot" : "Dot");
	public final static ISymbol Drop = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "drop" : "Drop");
	public final static ISymbol Eigenvalues = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "eigenvalues" : "Eigenvalues");
	public final static ISymbol Eigenvectors = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "eigenvectors"
			: "Eigenvectors");
	public final static ISymbol Equal = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "equal" : "Equal");
	public final static ISymbol Erf = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "erf" : "Erf");
	public final static ISymbol EuclidianDistance = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "euclidiandistance"
			: "EuclidianDistance");
	public final static ISymbol EulerPhi = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "eulerphi" : "EulerPhi");
	public final static ISymbol EvenQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "evenq" : "EvenQ");
	public final static ISymbol Exp = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "exp" : "Exp");
	public final static ISymbol Expand = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "expand" : "Expand");
	public final static ISymbol ExpandAll = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "expandall" : "ExpandAll");
	public final static ISymbol Exponent = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "exponent" : "Exponent");
	public final static ISymbol ExtendedGCD = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "extendedgcd" : "ExtendedGCD");
	public final static ISymbol Extract = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "extract" : "Extract");
	public final static ISymbol Factor = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "factor" : "Factor");
	public final static ISymbol Factorial = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "factorial" : "Factorial");
	public final static ISymbol Factorial2 = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "factorial2" : "Factorial2");
	public final static ISymbol FactorInteger = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "factorinteger"
			: "FactorInteger");
	public final static ISymbol FactorSquareFree = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "factorsquarefree"
			: "FactorSquareFree");
	public final static ISymbol FactorSquareFreeList = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "factorsquarefreelist" : "FactorSquareFreeList");
	public final static ISymbol FactorTerms = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "factorterms" : "FactorTerms");
	public final static ISymbol Fibonacci = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fibonacci" : "Fibonacci");
	public final static ISymbol FindRoot = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "findroot" : "FindRoot");
	public final static ISymbol First = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "first" : "First");
	public final static ISymbol Fit = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fit" : "Fit");
	public final static ISymbol Floor = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "floor" : "Floor");
	public final static ISymbol FractionalPart = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fractionalpart"
			: "FractionalPart");
	public final static ISymbol FreeQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "freeq" : "FreeQ");
	public final static ISymbol FrobeniusSolve = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "frobeniussolve"
			: "FrobeniusSolve");
	public final static ISymbol FromCharacterCode = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fromcharactercode"
			: "FromCharacterCode");
	public final static ISymbol FromContinuedFraction = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fromcontinuedfraction" : "FromContinuedFraction");
	public final static ISymbol FullForm = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fullform" : "FullForm");
	public final static ISymbol FullSimplify = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fullsimplify"
			: "FullSimplify");
	public final static ISymbol Function = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "function" : "Function");
	public final static ISymbol Gamma = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "gamma" : "Gamma");
	public final static ISymbol GCD = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "gcd" : "GCD");
	public final static ISymbol GeometricMean = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "geometricmean"
			: "GeometricMean");
	public final static ISymbol Greater = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "greater" : "Greater");
	public final static ISymbol GreaterEqual = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "greaterequal"
			: "GreaterEqual");
	public final static ISymbol GroebnerBasis = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "groebnerbasis"
			: "GroebnerBasis");
	public final static ISymbol HarmonicNumber = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "harmonicnumber"
			: "HarmonicNumber");
	public final static ISymbol Head = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "head" : "Head");
	public final static ISymbol HilbertMatrix = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "hilbertmatrix"
			: "HilbertMatrix");
	public final static ISymbol Hold = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "hold" : "Hold");
	public final static ISymbol Horner = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "horner" : "Horner");
	public final static ISymbol IdentityMatrix = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "identitymatrix"
			: "IdentityMatrix");
	public final static ISymbol Im = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "im" : "Im");
	public final static ISymbol Increment = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "increment" : "Increment");
	public final static ISymbol Inner = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "inner" : "Inner");
	public final static ISymbol IntegerPart = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "integerpart" : "IntegerPart");
	public final static ISymbol IntegerPartitions = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "integerpartitions"
			: "IntegerPartitions");
	public final static ISymbol IntegerQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "integerq" : "IntegerQ");
	public final static ISymbol Integrate = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "integrate" : "Integrate");
	public final static ISymbol Intersection = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "intersection"
			: "Intersection");
	public final static ISymbol Inverse = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "inverse" : "Inverse");
	public final static ISymbol InverseFunction = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "inversefunction"
			: "InverseFunction");
	public final static ISymbol JacobiMatrix = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "jacobimatrix"
			: "JacobiMatrix");
	public final static ISymbol JacobiSymbol = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "jacobisymbol"
			: "JacobiSymbol");
	public final static ISymbol JavaForm = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "javaform" : "JavaForm");
	public final static ISymbol Join = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "join" : "Join");
	public final static ISymbol KOrderlessPartitions = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "korderlesspartitions" : "KOrderlessPartitions");
	public final static ISymbol KPartitions = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "kpartitions" : "KPartitions");
	public final static ISymbol Last = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "last" : "Last");
	public final static ISymbol LCM = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "lcm" : "LCM");
	public final static ISymbol LeafCount = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "leafcount" : "LeafCount");
	public final static ISymbol Length = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "length" : "Length");
	public final static ISymbol Less = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "less" : "Less");
	public final static ISymbol LessEqual = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "lessequal" : "LessEqual");
	public final static ISymbol LetterQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "letterq" : "LetterQ");
	public final static ISymbol Level = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "level" : "Level");
	public final static ISymbol Limit = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "limit" : "Limit");
	public final static ISymbol LinearProgramming = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "linearprogramming"
			: "LinearProgramming");
	public final static ISymbol LinearSolve = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "linearsolve" : "LinearSolve");
	public final static ISymbol Log = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "log" : "Log");
	public final static ISymbol LowerCaseQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "lowercaseq" : "LowerCaseQ");
	public final static ISymbol LUDecomposition = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "ludecomposition"
			: "LUDecomposition");
	public final static ISymbol ManhattanDistance = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "manhattandistance"
			: "ManhattanDistance");
	public final static ISymbol Map = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "map" : "Map");
	public final static ISymbol MapAll = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "mapall" : "MapAll");
	public final static ISymbol MapThread = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "mapthread" : "MapThread");
	public final static ISymbol MatchQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "matchq" : "MatchQ");
	public final static ISymbol MatrixPower = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "matrixpower" : "MatrixPower");
	public final static ISymbol MatrixQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "matrixq" : "MatrixQ");
	public final static ISymbol Max = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "max" : "Max");
	public final static ISymbol Mean = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "mean" : "Mean");
	public final static ISymbol Median = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "median" : "Median");
	public final static ISymbol MemberQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "memberq" : "MemberQ");
	public final static ISymbol Min = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "min" : "Min");
	public final static ISymbol Mod = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "mod" : "Mod");
	public final static ISymbol MoebiusMu = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "moebiusmu" : "MoebiusMu");
	public final static ISymbol Most = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "most" : "Most");
	public final static ISymbol Multinomial = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "multinomial" : "Multinomial");
	public final static ISymbol Negative = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "negative" : "Negative");
	public final static ISymbol NextPrime = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nextprime" : "NextPrime");
	public final static ISymbol NFourierTransform = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nfouriertransform"
			: "NFourierTransform");
	public final static ISymbol NIntegrate = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nintegrate" : "NIntegrate");
	public final static ISymbol NonCommutativeMultiply = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "noncommutativemultiply" : "NonCommutativeMultiply");
	public final static ISymbol NonNegative = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nonnegative" : "NonNegative");
	public final static ISymbol Norm = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "norm" : "Norm");
	public final static ISymbol Not = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "not" : "Not");
	public final static ISymbol NRoots = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nroots" : "NRoots");
	public final static ISymbol NSolve = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nsolve" : "NSolve");
	public final static ISymbol NumberQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "numberq" : "NumberQ");
	public final static ISymbol Numerator = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "numerator" : "Numerator");
	public final static ISymbol NumericQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "numericq" : "NumericQ");
	public final static ISymbol OddQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "oddq" : "OddQ");
	public final static ISymbol Or = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "or" : "Or");
	public final static ISymbol Order = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "order" : "Order");
	public final static ISymbol OrderedQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "orderedq" : "OrderedQ");
	public final static ISymbol Out = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "out" : "Out");
	public final static ISymbol Outer = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "outer" : "Outer");
	public final static ISymbol Package = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "package" : "Package");
	public final static ISymbol PadLeft = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "padleft" : "PadLeft");
	public final static ISymbol PadRight = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "padright" : "PadRight");
	public final static ISymbol Part = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "part" : "Part");
	public final static ISymbol Partition = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "partition" : "Partition");
	public final static ISymbol Pattern = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "pattern" : "Pattern");
	public final static ISymbol Permutations = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "permutations"
			: "Permutations");
	public final static ISymbol Plus = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "plus" : "Plus");
	public final static ISymbol PolynomialExtendedGCD = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polynomialextendedgcd" : "PolynomialExtendedGCD");
	public final static ISymbol PolynomialGCD = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polynomialgcd"
			: "PolynomialGCD");
	public final static ISymbol PolynomialLCM = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polynomiallcm"
			: "PolynomialLCM");
	public final static ISymbol PolynomialQ = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polynomialq" : "PolynomialQ");
	public final static ISymbol PolynomialQuotient = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polynomialquotient"
			: "PolynomialQuotient");
	public final static ISymbol PolynomialQuotientRemainder = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polynomialquotientremainder" : "PolynomialQuotientRemainder");
	public final static ISymbol PolynomialRemainder = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polynomialremainder"
			: "PolynomialRemainder");
	public final static ISymbol Position = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "position" : "Position");
	public final static ISymbol Positive = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "positive" : "Positive");
	public final static ISymbol PossibleZeroQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "possiblezeroq"
			: "PossibleZeroQ");
	public final static ISymbol Power = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "power" : "Power");
	public final static ISymbol PowerExpand = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "powerexpand" : "PowerExpand");
	public final static ISymbol PowerMod = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "powermod" : "PowerMod");
	public final static ISymbol PreDecrement = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "predecrement"
			: "PreDecrement");
	public final static ISymbol PreIncrement = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "preincrement"
			: "PreIncrement");
	public final static ISymbol Prepend = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "prepend" : "Prepend");
	public final static ISymbol PrependTo = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "prependto" : "PrependTo");
	public final static ISymbol PrimeQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "primeq" : "PrimeQ");
	public final static ISymbol PrimitiveRoots = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "primitiveroots"
			: "PrimitiveRoots");
	public final static ISymbol Print = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "print" : "Print");
	public final static ISymbol Product = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "product" : "Product");
	public final static ISymbol Quotient = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "quotient" : "Quotient");
	public final static ISymbol RandomInteger = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "randominteger"
			: "RandomInteger");
	public final static ISymbol RandomReal = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "randomreal" : "RandomReal");
	public final static ISymbol Range = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "range" : "Range");
	public final static ISymbol Rational = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rational" : "Rational");
	public final static ISymbol Rationalize = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rationalize" : "Rationalize");
	public final static ISymbol Re = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "re" : "Re");
	public final static ISymbol ReplaceAll = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "replaceall" : "ReplaceAll");
	public final static ISymbol ReplacePart = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "replacepart" : "ReplacePart");
	public final static ISymbol ReplaceRepeated = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "replacerepeated"
			: "ReplaceRepeated");
	public final static ISymbol Rest = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rest" : "Rest");
	public final static ISymbol Resultant = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "resultant" : "Resultant");
	public final static ISymbol Reverse = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "reverse" : "Reverse");
	public final static ISymbol Riffle = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "riffle" : "Riffle");
	public final static ISymbol RootIntervals = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rootintervals"
			: "RootIntervals");
	public final static ISymbol Roots = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "roots" : "Roots");
	public final static ISymbol RotateLeft = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rotateleft" : "RotateLeft");
	public final static ISymbol RotateRight = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rotateright" : "RotateRight");
	public final static ISymbol Round = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "round" : "Round");
	public final static ISymbol Rule = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rule" : "Rule");
	public final static ISymbol RuleDelayed = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "ruledelayed" : "RuleDelayed");
	public final static ISymbol SameQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sameq" : "SameQ");
	public final static ISymbol Scan = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "scan" : "Scan");
	public final static ISymbol Sec = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sec" : "Sec");
	public final static ISymbol Sech = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sech" : "Sech");
	public final static ISymbol Select = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "select" : "Select");
	public final static ISymbol Set = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "set" : "Set");

	public final static ISymbol SetAttributes = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "setattributes"
			: "SetAttributes");
	public final static ISymbol SetDelayed = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "setdelayed" : "SetDelayed");
	public final static ISymbol Sign = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sign" : "Sign");
	public final static ISymbol SignCmp = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "signcmp" : "SignCmp");
	public final static ISymbol Simplify = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "simplify" : "Simplify");
	public final static ISymbol Sin = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sin" : "Sin");
	public final static ISymbol SingularValueDecomposition = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "singularvaluedecomposition" : "SingularValueDecomposition");
	public final static ISymbol Sinh = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sinh" : "Sinh");
	public final static ISymbol Solve = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "solve" : "Solve");
	public final static ISymbol Sort = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sort" : "Sort");
	public final static ISymbol Sqrt = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sqrt" : "Sqrt");
	public final static ISymbol SquaredEuclidianDistance = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "squaredeuclidiandistance" : "SquaredEuclidianDistance");
	public final static ISymbol SquareFreeQ = F
			.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "squarefreeq" : "SquareFreeQ");
	public final static ISymbol StirlingS2 = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "stirlings2" : "StirlingS2");
	public final static ISymbol StringDrop = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "stringdrop" : "StringDrop");
	public final static ISymbol StringJoin = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "stringjoin" : "StringJoin");
	public final static ISymbol StringLength = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "stringlength"
			: "StringLength");
	public final static ISymbol StringTake = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "stringtake" : "StringTake");
	public final static ISymbol Subsets = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "subsets" : "Subsets");
	public final static ISymbol SubtractFrom = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "subtractfrom"
			: "SubtractFrom");
	public final static ISymbol Sum = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sum" : "Sum");
	public final static ISymbol SyntaxLength = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "syntaxlength"
			: "SyntaxLength");
	public final static ISymbol SyntaxQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "syntaxq" : "SyntaxQ");
	public final static ISymbol Table = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "table" : "Table");
	public final static ISymbol Take = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "take" : "Take");
	public final static ISymbol Tan = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tan" : "Tan");
	public final static ISymbol Tanh = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tanh" : "Tanh");
	public final static ISymbol Taylor = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "taylor" : "Taylor");
	public final static ISymbol Thread = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "thread" : "Thread");
	public final static ISymbol Through = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "through" : "Through");
	public final static ISymbol Times = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "times" : "Times");
	public final static ISymbol TimesBy = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "timesby" : "TimesBy");
	public final static ISymbol Timing = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "timing" : "Timing");
	public final static ISymbol ToCharacterCode = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tocharactercode"
			: "ToCharacterCode");
	public final static ISymbol Together = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "together" : "Together");
	public final static ISymbol ToString = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tostring" : "ToString");
	public final static ISymbol Total = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "total" : "Total");
	public final static ISymbol ToUnicode = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tounicode" : "ToUnicode");
	public final static ISymbol Tr = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tr" : "Tr");
	public final static ISymbol Trace = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "trace" : "Trace");
	public final static ISymbol Transpose = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "transpose" : "Transpose");
	public final static ISymbol TrigExpand = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "trigexpand" : "TrigExpand");
	public final static ISymbol TrigReduce = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "trigreduce" : "TrigReduce");
	public final static ISymbol TrigToExp = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "trigtoexp" : "TrigToExp");
	public final static ISymbol TrueQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "trueq" : "TrueQ");
	public final static ISymbol Unequal = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "unequal" : "Unequal");
	public final static ISymbol Union = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "union" : "Union");
	public final static ISymbol UnitStep = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "unitstep" : "UnitStep");
	public final static ISymbol UnsameQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "unsameq" : "UnsameQ");
	public final static ISymbol UpperCaseQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "uppercaseq" : "UpperCaseQ");
	public final static ISymbol UpSet = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "upset" : "UpSet");
	public final static ISymbol UpSetDelayed = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "upsetdelayed"
			: "UpSetDelayed");
	public final static ISymbol ValueQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "valueq" : "ValueQ");
	public final static ISymbol VandermondeMatrix = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "vandermondematrix"
			: "VandermondeMatrix");
	public final static ISymbol Variables = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "variables" : "Variables");
	public final static ISymbol VectorQ = F.initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "vectorq" : "VectorQ");

	public final static ISymbol a = initFinalSymbol("a");
	public final static ISymbol b = initFinalSymbol("b");
	public final static ISymbol c = initFinalSymbol("c");
	public final static ISymbol d = initFinalSymbol("dv");
	public final static ISymbol e = initFinalSymbol("ev");
	public final static ISymbol f = initFinalSymbol("f");
	public final static ISymbol g = initFinalSymbol("g");
	public final static ISymbol h = initFinalSymbol("h");
	public final static ISymbol i = initFinalSymbol("iv");
	public final static ISymbol j = initFinalSymbol("j");
	public final static ISymbol k = initFinalSymbol("k");
	public final static ISymbol l = initFinalSymbol("l");
	public final static ISymbol m = initFinalSymbol("m");
	public final static ISymbol n = initFinalSymbol("nv");
	public final static ISymbol o = initFinalSymbol("o");
	public final static ISymbol p = initFinalSymbol("p");
	public final static ISymbol q = initFinalSymbol("q");
	public final static ISymbol r = initFinalSymbol("r");
	public final static ISymbol s = initFinalSymbol("s");
	public final static ISymbol t = initFinalSymbol("t");
	public final static ISymbol u = initFinalSymbol("u");
	public final static ISymbol v = initFinalSymbol("v");
	public final static ISymbol w = initFinalSymbol("w");
	public final static ISymbol x = initFinalSymbol("x");
	public final static ISymbol y = initFinalSymbol("y");
	public final static ISymbol z = initFinalSymbol("z");

	// public final static IPattern a_ = initPredefinedPattern(a);
	// public final static IPattern b_ = initPredefinedPattern(b);
	// public final static IPattern c_ = initPredefinedPattern(c);
	// public final static IPattern d_ = initPredefinedPattern(d);
	// public final static IPattern e_ = initPredefinedPattern(e);
	// public final static IPattern f_ = initPredefinedPattern(f);
	// public final static IPattern g_ = initPredefinedPattern(g);
	// public final static IPattern h_ = initPredefinedPattern(h);
	// public final static IPattern i_ = initPredefinedPattern(i);
	// public final static IPattern j_ = initPredefinedPattern(j);
	// public final static IPattern k_ = initPredefinedPattern(k);
	// public final static IPattern l_ = initPredefinedPattern(l);
	// public final static IPattern m_ = initPredefinedPattern(m);
	// public final static IPattern n_ = initPredefinedPattern(n);
	// public final static IPattern o_ = initPredefinedPattern(o);
	// public final static IPattern p_ = initPredefinedPattern(p);
	// public final static IPattern q_ = initPredefinedPattern(q);
	// public final static IPattern r_ = initPredefinedPattern(r);
	// public final static IPattern s_ = initPredefinedPattern(s);
	// public final static IPattern t_ = initPredefinedPattern(t);
	// public final static IPattern u_ = initPredefinedPattern(u);
	// public final static IPattern v_ = initPredefinedPattern(v);
	// public final static IPattern w_ = initPredefinedPattern(w);
	// public final static IPattern x_ = initPredefinedPattern(x);
	// public final static IPattern y_ = initPredefinedPattern(y);
	// public final static IPattern z_ = initPredefinedPattern(z);

	/**
	 * * Constant integer &quot;0&quot;
	 */
	public final static IInteger C0 = IntegerSym.valueOf(0);

	/**
	 * Constant integer &quot;1&quot;
	 */
	public final static IInteger C1 = IntegerSym.valueOf(1);

	/**
	 * Constant integer &quot;2&quot;
	 */
	public final static IInteger C2 = IntegerSym.valueOf(2);

	/**
	 * Constant integer &quot;3&quot;
	 */
	public final static IInteger C3 = IntegerSym.valueOf(3);

	/**
	 * Constant integer &quot;4&quot;
	 */
	public final static IInteger C4 = IntegerSym.valueOf(4);

	/**
	 * Constant integer &quot;5&quot;
	 */
	public final static IInteger C5 = IntegerSym.valueOf(5);

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
	public final static IFraction C1D2 = FractionSym.valueOf(1, 2);

	/**
	 * Constant fraction &quot;-1/2&quot;
	 */
	public final static IFraction CN1D2 = FractionSym.valueOf(-1, 2);

	/**
	 * Constant fraction &quot;1/3&quot;
	 */
	public final static IFraction C1D3 = FractionSym.valueOf(1, 3);

	/**
	 * Constant fraction &quot;-1/3&quot;
	 */
	public final static IFraction CN1D3 = FractionSym.valueOf(-1, 3);

	/**
	 * Constant fraction &quot;1/4&quot;
	 */
	public final static IFraction C1D4 = FractionSym.valueOf(1, 4);

	/**
	 * Constant fraction &quot;-1/4&quot;
	 */
	public final static IFraction CN1D4 = FractionSym.valueOf(-1, 4);

	/**
	 * Constant double &quot;0.0&quot;
	 */
	public final static INum CD0 = Num.valueOf(0.0);

	/**
	 * Constant double &quot;1.0&quot;
	 */
	public final static INum CD1 = Num.valueOf(1.0);

	/**
	 * Represents <code>Infinity</code> (i.e. <code>Infinity->DirectedInfinity[1]</code>)
	 */
	public static IAST CInfinity;

	/**
	 * Represents <code>-Infinity</code> (i.e. <code>-Infinity->DirectedInfinity[-1]</code>)
	 */
	public static IAST CNInfinity;

	/**
	 * Represents <code>ComplexInfinity</code> (i.e. <code>ComplexInfinity->DirectedInfinity[</code>)
	 */
	public static IAST CComplexInfinity;

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
	public final static IInteger CN1 = IntegerSym.valueOf(-1);

	static {

		try {
			if (Config.DEBUG) {
				System.out.println("Config.DEBUG == true");
			}
			if (Config.SHOW_STACKTRACE) {
				System.out.println("Config.SHOW_STACKTRACE == true");
			}
			// long start = System.currentTimeMillis();

			Slot.setAttributes(ISymbol.NHOLDALL);
			SlotSequence.setAttributes(ISymbol.NHOLDALL);

			CInfinity = $(DirectedInfinity, C1);
			CNInfinity = $(DirectedInfinity, CN1);
			CComplexInfinity = $(DirectedInfinity);

			Slot1 = $(Slot, C1);
			Slot2 = $(Slot, C2);

			PREDEFINED_INTERNAL_FORM_STRINGS.put("Pi", "Pi");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("E", "E");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("False", "False");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("True", "True");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("Null", "Null");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("Integer", "IntegerHead");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("Symbol", "SymbolHead");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("Infinity", "CInfinity");
			PREDEFINED_INTERNAL_FORM_STRINGS.put("ComplexInfinity", "CComplexInfinity");

			Plus.setDefaultValue(C0);
			Plus.setEvaluator(org.matheclipse.core.reflection.system.Plus.CONST);
			Times.setDefaultValue(C1);
			Times.setEvaluator(org.matheclipse.core.reflection.system.Times.CONST);
			Power.setDefaultValue(2, C1);
			Power.setEvaluator(org.matheclipse.core.reflection.system.Power.CONST);
			// initialize only the utility function rules for Integrate
			// other rules are "lazy loaded" on first use og Integrate function
			final EvalEngine engine = EvalEngine.get();
			IAST ruleList = org.matheclipse.core.reflection.system.Integrate.getUtilityFunctionsRuleAST();
			if (ruleList != null) {
				engine.addRules(ruleList);
			}
			// long end = System.currentTimeMillis();
			// System.out.println("Init time: " + (end - start));
		} catch (Throwable th) {
			th.printStackTrace();
		}

	}

	// --- generated source codes:
	public static IAST Abs(final IExpr a0) {
		return unary(Abs, a0);
	}

	public static IAST And(final IExpr a0, final IExpr a1) {
		return binary(And, a0, a1);
	}

	public static IAST ArcCos(final IExpr a0) {

		return unary(ArcCos, a0);
	}

	public static IAST ArcCosh(final IExpr a0) {

		return unary(ArcCosh, a0);
	}

	public static IAST Append(final IExpr a0, final IExpr a1) {

		return binary(Append, a0, a1);
	}

	public static IAST Apart(final IExpr a0) {
		return unary(Apart, a0);
	}

	public static IAST Apart(final IExpr a0, final IExpr a1) {
		return binary(Apart, a0, a1);
	}

	public static IAST Apply(final IExpr a0, final IExpr a1) {
		return binary(Apply, a0, a1);
	}

	public static IAST ArcSin(final IExpr a0) {

		return unary(ArcSin, a0);
	}

	public static IAST ArcSinh(final IExpr a0) {

		return unary(ArcSinh, a0);
	}

	public static IAST ArcCot(final IExpr a0) {
		return unary($s("ArcCot"), a0);
	}

	public static IAST ArcTan(final IExpr a0) {

		return unary(ArcTan, a0);
	}

	public static IAST ArcTanh(final IExpr a0) {

		return unary(ArcTanh, a0);
	}

	public static IAST ArcTan(final IExpr a0, final IExpr a1) {

		return binary(ArcTan, a0, a1);
	}

	public static IAST BernoulliB(final IExpr a0) {

		return unary($s("BernoulliB"), a0);
	}

	public static IAST Binomial(final IExpr a0, final IExpr a1) {

		return binary($s("Binomial"), a0, a1);
	}

	public static IAST Block(final IExpr a0, final IExpr a1) {
		return binary(Block, a0, a1);
	}

	public static IAST Cancel(final IExpr a) {
		return unary(Cancel, a);
	}

	public static IAST Ceiling(final IExpr a0) {

		return unary(Ceiling, a0);
	}

	public static IAST CNInfinity() {
		return binary(Times, CN1, Infinity);
	}

	public static IAST CompoundExpression(final IExpr... a) {
		return ast(a, CompoundExpression);
	}

	public static IAST Condition(final IExpr a0, final IExpr a1) {

		return binary(Condition, a0, a1);
	}

	public static IAST Conjugate(final IExpr a0) {
		return unary(Conjugate, a0);
	}

	public static IAST Cos(final IExpr a0) {
		return unary(Cos, a0);
	}

	public static IAST Cosh(final IExpr a0) {
		return unary(Cosh, a0);
	}

	public static IAST SameQ(final IExpr a0, final IExpr a1) {
		return binary(SameQ, a0, a1);
	}

	public static IAST Sec(final IExpr a0) {
		return unary(Sec, a0);
	}

	public static IAST Sech(final IExpr a0) {
		return unary(Sech, a0);
	}

	public static IAST Sequence() {
		return ast(Sequence);
	}

	public static IAST Sequence(final IExpr a0) {
		return unary(Sequence, a0);
	}

	public static IAST Sequence(final IExpr... a) {
		return ast(a, Sequence);
	}

	public static IAST Cross(final IExpr a0, final IExpr a1) {
		return binary(Cross, a0, a1);
	}

	public static IAST D() {
		return ast(D);
	}

	public static IAST D(final IExpr a0, final IExpr a1) {
		return binary(D, a0, a1);
	}

	public static IAST Denominator(final IExpr a0) {

		return unary(Denominator, a0);
	}

	public static IAST Depth(final IExpr a0) {

		return unary(Depth, a0);
	}

	public static IAST Derivative(final IExpr a0) {
		return unary($s("Derivative"), a0);
	}

	public static IAST Det(final IExpr a0) {

		return unary(Det, a0);
	}

	public static IAST Divide(final IExpr a0, final IExpr a1) {
		return binary(Times, a0, binary(Power, a1, CN1));
	}

	public static IAST Dot(final IExpr a0, final IExpr a1) {

		return binary(Dot, a0, a1);
	}

	public static IAST Dot(final IExpr... a) {// 0, final IExpr a1, final IExpr
		// a2) {
		return ast(a, Dot);
		// return ternary(Dot, a0, a1, a2);
	}

	public static IAST Equal(final IExpr a0, final IExpr a1) {

		return binary(Equal, a0, a1);
	}

	public static IAST Equal(final IExpr... a) {
		return ast(a, Equal);
	}

	public static IAST EvenQ(final IExpr a) {
		return unary(EvenQ, a);
	}

	public static IAST Exp(final IExpr a0) {
		return binary(Power, E, a0);
	}

	public static IAST Expand(final IExpr a0) {

		return unary(Expand, a0);
	}

	public static IAST Expand(final IExpr a0, final IExpr a1) {

		return binary(Expand, a0, a1);
	}

	public static IAST ExpandAll(final IExpr a0) {

		return unary(ExpandAll, a0);
	}

	public static IAST Factor(final IExpr a0) {

		return unary(Factor, a0);
	}

	public static IAST Factorial(final IExpr a0) {

		return unary(Factorial, a0);
	}

	public static IAST Floor(final IExpr a0) {

		return unary(Floor, a0);
	}

	public static IAST Fibonacci(final IExpr a0) {

		return unary(Fibonacci, a0);
	}

	public static IAST First(final IExpr a0) {
		return unary(First, a0);
	}

	public static IAST FreeQ(final IExpr a0, final IExpr a1) {

		return binary(FreeQ, a0, a1);
	}

	public static IAST Function(final IExpr a0) {
		return unary($s("Function"), a0);
	}

	public static IAST FullForm(final IExpr a0) {

		return unary(FullForm, a0);
	}

	public static IAST GCD(final IExpr a0, final IExpr a1) {

		return binary(GCD, a0, a1);
	}

	public static IAST Graphics() {

		return ast(Graphics);
	}

	public static IAST Hold(final IExpr a0) {
		return unary(Hold, a0);
	}

	public static IAST If(final IExpr a0, final IExpr a1) {
		return binary(If, a0, a1);
	}

	public static IAST If(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary(If, a0, a1, a2);
	}

	public static IAST Im(final IExpr a0) {
		return unary(Im, a0);
	}

	public static IAST IntegerQ(final IExpr a) {
		return unary(IntegerQ, a);
	}

	public static IAST MatchQ(final IExpr a0, final IExpr a1) {
		return binary(MatchQ, a0, a1);
	}

	public static IAST Not(final IExpr a) {
		return unary(Not, a);
	}

	public static IAST Numerator(final IExpr a0) {
		return unary(Numerator, a0);
	}

	/**
	 * Initialize the complete System. Calls {@link #initSymbols(String, ISymbolObserver, boolean)} with parameters
	 * <code>null, null</code>.
	 */
	public synchronized static void initSymbols() {
		initSymbols(null, null, false);
	}

	/**
	 * Initialize the complete System. Calls {@link #initSymbols(String, ISymbolObserver, boolean)} with parameters
	 * <code>fileName, null</code>.
	 * 
	 * @param fileName
	 */
	private synchronized static void initSymbols(String fileName) {
		initSymbols(fileName, null, false);
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
	public synchronized static void initSymbols(String fileName, ISymbolObserver symbolObserver, boolean noPackageLoading) {

		if (!isSystemStarted) {
			try {
				isSystemStarted = true;

				if (symbolObserver != null) {
					SYMBOL_OBSERVER = symbolObserver;
				}

				if (!noPackageLoading) {
					Reader reader = null;
					if (fileName != null) {
						try {
							reader = new FileReader(fileName);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
					if (reader == null) {
						InputStream systemPackage = F.class.getResourceAsStream("/System.mep");
						if (systemPackage != null) {
							reader = new InputStreamReader(systemPackage);
						}
					}
					if (reader != null) {
						org.matheclipse.core.reflection.system.Package.loadPackage(EvalEngine.get(), reader);
					}
				}

				isSystemInitialized = true;
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	public static IAST Integrate(final IExpr a0, final IExpr a1) {

		return binary(Integrate, a0, a1);
	}

	public static IAST Inverse(final IExpr a0) {

		return unary(Inverse, a0);
	}

	// public static IAST KOrderlessPartitions(final IExpr a0) {
	//
	// return unary(KOrderlessPartitions, a0);
	// }
	//
	// public static IAST KPartitions(final IExpr a0) {
	//
	// return unary(KPartitions, a0);
	// }
	//
	// public static IAST KSubsets(final IExpr a0) {
	//
	// return unary(KSubsets, a0);
	// }
	//
	public static IAST LeafCount(final IExpr a0) {
		return unary(LeafCount, a0);
	}

	public static IAST Less(final IExpr a0, final IExpr a1) {

		return binary(Less, a0, a1);
	}

	public static IAST LessEqual(final IExpr a0, final IExpr a1) {

		return binary(LessEqual, a0, a1);
	}

	public static IAST Greater(final IExpr a0, final IExpr a1) {

		return binary(Greater, a0, a1);
	}

	public static IAST GreaterEqual(final IExpr a0, final IExpr a1) {

		return binary(GreaterEqual, a0, a1);
	}

	public static IAST Line() {
		return ast(Line);
	}

	public static IAST LinearSolve(final IExpr a0, final IExpr a1) {
		return binary($s("LinearSolve"), a0, a1);
	}

	public static IAST Limit(final IExpr a0, final IExpr a1) {

		return binary(Limit, a0, a1);
	}

	public static IAST List() {
		return ast(List);
	}

	public static IAST List(final IExpr a0) {

		return unary(List, a0);
	}

	public static IAST List(final IExpr a0, final IExpr a1) {

		return binary(List, a0, a1);
	}

	public static IAST List(final double... numbers) {
		INum a[] = new INum[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			a[i] = F.num(numbers[i]);
		}
		return ast(a, List);
	}

	public static IAST List(final long... numbers) {
		IInteger a[] = new IInteger[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			a[i] = F.integer(numbers[i]);
		}
		return ast(a, List);
	}

	public static IAST List(final IExpr... a) {// 0, final IExpr a1, final IExpr
		// a2) {
		return ast(a, List);
		// return ternary(List, a0, a1, a2);
	}

	public static IAST Log(final IExpr a0) {

		return unary(Log, a0);
	}

	public static IAST Map(final IExpr a0) {

		return unary(Map, a0);
	}

	public static IAST MapAll(final IExpr a0) {

		return unary(MapAll, a0);
	}

	public static IAST MatrixPower(final IExpr a0) {

		return unary(MatrixPower, a0);
	}

	public static IAST Max() {
		return ast(Max);
	}

	public static IAST Max(final IExpr a0) {
		return unary(Max, a0);
	}

	public static IAST Max(final IExpr a0, final IExpr a1) {
		return binary(Max, a0, a1);
	}

	public static IAST MemberQ(final IExpr a0, final IExpr a1) {
		return binary(MemberQ, a0, a1);
	}

	public static IAST Min() {
		return ast(Min);
	}

	public static IAST Min(final IExpr a0) {
		return unary(Min, a0);
	}

	public static IAST Min(final IExpr a0, final IExpr a1) {
		return binary(Min, a0, a1);
	}

	public static IExpr Mod(final IExpr a0, final IExpr a1) {
		return binary(Mod, a0, a1);
	}

	public static IAST Module(final IExpr a0, final IExpr a1) {
		return binary(Module, a0, a1);
	}

	/**
	 * Evaluate the given expression in numeric mode
	 * 
	 * @param a0
	 * @return
	 */
	public static IAST N(final IExpr a0) {

		return unary(N, a0);
	}

	/**
	 * Multiplies the given argument by <code>-1</code>.
	 * 
	 * @param a
	 * @return
	 */
	public static IAST Negate(final IExpr a) {
		return binary(Times, CN1, a);
	}

	public static IAST Negative(final IExpr a0) {
		return unary(Negative, a0);
	}

	//
	// public static IAST NumberPartitions(final IExpr a0) {
	//
	// return unary(NumberPartitions, a0);
	// }

	public static IAST NumberQ(final IExpr a0) {

		return unary(NumberQ, a0);
	}

	public static IAST NumericQ(final IExpr a0) {
		return unary(NumericQ, a0);
	}

	public static IAST OddQ(final IExpr a) {
		return unary(OddQ, a);
	}

	public static IAST Options(final IExpr a0) {

		return unary(Options, a0);
	}

	public static IAST Or(final IExpr a0, final IExpr a1) {
		return binary(Or, a0, a1);
	}

	public static IAST Part() {
		return ast(Part);
	}

	public static IAST Part(final IExpr a0) {
		return unary(Part, a0);
	}

	public static IAST Part(final IExpr a0, final IExpr a1) {
		// if (a0 == null || a1 == null||a0 == F.Null || a1 == F.Null) {
		// System.out.println("Part argument is null");
		// }
		return binary(Part, a0, a1);
	}

	public static IAST Part(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary(Part, a0, a1, a2);
	}

	// public static IAST Partition(final IExpr a0) {
	//
	// return unary(Partition, a0);
	// }
	//
	// public static IAST Permutations(final IExpr a0) {
	//
	// return unary(Permutations, a0);
	// }

	public static IAST Plus() {
		return ast(Plus);
	}

	public static IAST Plus(final IExpr a0) {

		return unary(Plus, a0);
	}

	public static IAST Plus(final IExpr a0, final IExpr a1) {

		return binary(Plus, a0, a1);
	}

	public static IAST Plus(final IExpr... a) {// 0, final IExpr a1, final IExpr
		// a2) {
		return ast(a, Plus);
		// return ternary(Plus, a0, a1, a2);
	}

	public static IAST PossibleZeroQ(final IExpr a0) {
		return unary(PossibleZeroQ, a0);
	}

	public static IAST Power() {

		return ast(Power);
	}

	public static IAST Power(final IExpr a0, final IExpr a1) {
		return binary(Power, a0, a1);
	}

	public static IAST Power(final IExpr a0, final long exp) {
		return binary(Power, a0, integer(exp));
	}

	public static IAST PowerExpand(final IExpr a0) {

		return unary($s("PowerExpand"), a0);
	}

	public static IAST Prepend(final IExpr a0, final IExpr a1) {

		return binary(Prepend, a0, a1);
	}

	public static IAST PrimeQ(final IExpr a0) {

		return unary(PrimeQ, a0);
	}

	public static IAST Product(final IExpr a0, final IExpr a1) {

		return binary($s("Product"), a0, a1);
	}

	public static IAST Quotient(final IExpr a0, final IExpr a1) {

		return binary($s("Quotient"), a0, a1);
	}

	public static IAST Re(final IExpr a0) {
		return unary(Re, a0);
	}

	public static IAST Rest(final IExpr a0) {
		return unary(Rest, a0);
	}

	public static IAST ReplaceAll(final IExpr a0, final IExpr a1) {

		return binary(ReplaceAll, a0, a1);
	}

	public static IAST Roots(final IExpr a0) {
		return unary($s("Roots"), a0);
	}

	public static IAST Round(final IExpr a0) {
		return unary($s("Round"), a0);
	}

	public static IAST Rule(final IExpr a0, final IExpr a1) {
		return binary(Rule, a0, a1);
	}

	public static IAST RuleDelayed(final IExpr a0, final IExpr a1) {
		return binary(RuleDelayed, a0, a1);
	}

	public static IAST Set(final IExpr a0, final IExpr a1) {
		return binary(Set, a0, a1);
	}

	public static IAST SetAttributes(final IExpr a0) {

		return unary(SetAttributes, a0);
	}

	public static IAST SetDelayed(final IExpr a0, final IExpr a1) {
		if (a0.isAST()) {
			((IAST) a0).setEvalFlags(((IAST) a0).getEvalFlags() & IAST.IS_FLATTENED_OR_SORTED_MASK);
		}
		org.matheclipse.core.reflection.system.SetDelayed.CONST.createPatternMatcher(a0, a1, true);
		return null;// binary(SetDelayed, a0, a1);
	}

	public static IAST Show(final IExpr a0) {
		return unary(Show, a0);
	}

	public static IAST SignCmp(final IExpr a0) {
		return unary(SignCmp, a0);
	}

	public static IAST Simplify(final IExpr a0) {
		return unary($s("Simplify"), a0);
	}

	public static IAST Sin(final IExpr a0) {

		return unary(Sin, a0);
	}

	public static IAST Sinh(final IExpr a0) {

		return unary(Sinh, a0);
	}

	public static IAST Csc(final IExpr a0) {
		return unary(Csc, a0);
	}

	public static IAST Csch(final IExpr a0) {
		return unary(Csch, a0);
	}

	public static IAST Slot(final IExpr a0) {
		return unary(Slot, a0);
	}

	public static IAST Slot(final int i) {
		return unary(Slot, integer(i));
	}

	public static IAST Sqr(final IExpr a0) {

		return binary(Power, a0, C2);
	}

	public static IAST Sqrt(final IExpr a0) {

		return binary(Power, a0, C1D2);
	}

	public static IAST Subtract(final IExpr a0, final IExpr a1) {
		return binary(Plus, a0, binary(Times, CN1, a1));
	}

	public static IAST Sum(final IExpr a0, final IExpr a1) {

		return binary($s("Sum"), a0, a1);
	}

	public static IAST SurfaceGraphics() {

		return ast(SurfaceGraphics);
	}

	public static IAST Cot(final IExpr a0) {
		return unary(Cot, a0);
	}

	public static IAST Coth(final IExpr a0) {
		return unary(Coth, a0);
	}

	public static IAST Tan(final IExpr a0) {

		return unary(Tan, a0);
	}

	public static IAST Tanh(final IExpr a0) {

		return unary(Tanh, a0);
	}

	public static IAST Taylor(final IExpr a0, final IExpr a1) {
		return binary(Taylor, a0, a1);
	}

	public static IAST Times() {
		return ast(Times);
	}

	public static IAST Times(final IExpr a0) {
		return unary(Times, a0);
	}

	public static IAST Times(final IExpr a0, final IExpr a1) {
		return binary(Times, a0, a1);
	}

	public static IAST Times(final IExpr... a) {
		return ast(a, Times);
	}

	public static IAST Together(final IExpr a0) {
		return unary(Together, a0);
	}

	public static IAST Tr(final IExpr a0) {
		return unary(Tr, a0);
	}

	public static IAST Trace(final IExpr a0) {
		return unary(Trace, a0);
	}

	public static IAST Transpose(final IExpr a0) {
		return unary(Transpose, a0);
	}

	public static IAST IntegerPart(final IExpr a0) {
		return unary(IntegerPart, a0);
	}

	public static IAST UpSet(final IExpr a0, final IExpr a1) {
		return binary(UpSet, a0, a1);
	}

	public static IAST UpSetDelayed(final IExpr a0, final IExpr a1) {
		return binary(UpSetDelayed, a0, a1);
	}

	public static IAST UnsameQ(final IExpr a0, final IExpr a1) {
		return binary(UnsameQ, a0, a1);
	}

	/**
	 * Creates a new AST from the given <code>ast</code> and <code>head</code>. if <code>include</code> is set to <code>true </code>
	 * all arguments from index first to last-1 are copied in the new list if <code>include</code> is set to <code> false </code>
	 * all arguments excluded from index first to last-1 are copied in the new list
	 * 
	 */
	public static IAST ast(final IAST f, final IExpr head, final boolean include, final int first, final int last) {
		AST ast = null;
		if (include) {
			ast = AST.newInstance(last - first, head);
			// range include
			for (int i = first; i < last; i++) {
				ast.add(f.get(i));
			}
		} else {
			ast = AST.newInstance(f.size() - last + first - 1, head);
			// range exclude
			for (int i = 1; i < first; i++) {
				ast.add(f.get(i));
			}
			for (int j = last; j < f.size(); j++) {
				ast.add(f.get(j));
			}
		}
		return ast;
	}

	/**
	 * Create a new abstract syntax tree (AST).
	 * 
	 * @param intialArgumentsCapacity
	 *            the initial capacity of arguments of the AST.
	 * @param head
	 *            the header expression of the function. If the ast represents a function like <code>f[x,y], Sin[x],...</code>, the
	 *            <code>head</code> will be an instance of type ISymbol.
	 * @return
	 */
	public static IAST newInstance(final int intialArgumentsCapacity, final IExpr head) {
		return AST.newInstance(intialArgumentsCapacity, head);
	}

	/**
	 * Create a new abstract syntax tree (AST).
	 * 
	 * @param head
	 *            the header expression of the function. If the ast represents a function like <code>f[x,y], Sin[x],...</code>, the
	 *            <code>head</code> will be an instance of type ISymbol.
	 * 
	 */
	public final static IAST ast(final IExpr head) {
		return AST.newInstance(head);
	}

	/**
	 * Create a new abstract syntax tree (AST).
	 * 
	 * @param head
	 *            the header expression of the function. If the ast represents a function like <code>f[x,y], Sin[x],...</code>, the
	 *            <code>head</code> will be an instance of type ISymbol.
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
	 *            the header expression of the function. If the ast represents a function like <code>f[x,y], Sin[x],...</code>, the
	 *            <code>head</code> will be an instance of type ISymbol.
	 * @param initialCapacity
	 *            the initial capacity (i.e. number of arguments without the header element) of the list.
	 * @param initNull
	 *            initialize all elements with <code>null</code>.
	 * @return
	 */
	public static IAST ast(final IExpr head, final int initialCapacity, final boolean initNull) {
		final AST ast = AST.newInstance(initialCapacity, head);
		if (initNull) {
			for (int i = 0; i < initialCapacity; i++) {
				ast.add(null);
			}
		}
		return ast;
	}

	/**
	 * Create a new abstract syntax tree (AST).
	 * 
	 * @param arr
	 * @param head
	 *            the header expression of the function. If the ast represents a function like <code>f[x,y], Sin[x],...</code>, the
	 *            <code>head</code> will be an instance of type ISymbol.
	 * @return
	 */
	public static IAST ast(final IExpr[] arr, final IExpr head) {
		return new AST(head, arr);
	}

	/**
	 * Create a function with 1 argument without evaluation.
	 * 
	 * @param head
	 * @param a0
	 * @return
	 */
	public final static IAST unary(final IExpr head, final IExpr a0) {
		return new AST(new IExpr[] { head, a0 });
	}

	/**
	 * Create a function with 2 arguments without evaluation.
	 * 
	 * @param head
	 * @param a0
	 * @param a1
	 * @return
	 */
	public final static IAST binary(final IExpr head, final IExpr a0, final IExpr a1) {
		return new AST(new IExpr[] { head, a0, a1 });
	}

	public final static IAST ternary(final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2) {
		return new AST(new IExpr[] { head, a0, a1, a2 });
	}

	public final static IAST quaternary(final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
		return new AST(new IExpr[] { head, a0, a1, a2, a3 });
	}

	public final static IAST quinary(final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
			final IExpr a4) {
		return new AST(new IExpr[] { head, a0, a1, a2, a3, a4 });
	}

	public final static IAST senary(final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
			final IExpr a4, final IExpr a5) {
		return new AST(new IExpr[] { head, a0, a1, a2, a3, a4, a5 });
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

	/**
	 * Create a symbolic complex number
	 * 
	 * @param re
	 * @return
	 */
	public static IComplex complex(final IFraction re) {
		return complex(re, fraction(0, 1));
	}

	/**
	 * Create a symbolic complex number
	 * 
	 * @param re
	 * @param im
	 * @return
	 */
	public static IComplex complex(final IFraction re, final IFraction im) {
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
	 * Create a symbolic complex number
	 * 
	 * @param realPart
	 *            the real double value part which should be converted to a complex number
	 * @param imagPart
	 *            the imaginary double value part which should be converted to a complex number
	 * @return IFraction
	 */
	public static IComplex complex(final double realPart, final double imagPart) {
		return ComplexSym.valueOf(FractionSym.valueOf(realPart), FractionSym.valueOf(imagPart));
	}

	/**
	 * Create a symbolic complex number
	 * 
	 * @param re
	 * @param im
	 * @return
	 */
	public static IComplex complex(final IInteger re, final IInteger im) {
		return ComplexSym.valueOf(re, im);
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

	public static IComplexNum complexNum(final IInteger obj) {
		return F.complexNum(obj.doubleValue(), 0.0d);
	}

	public static IComplexNum complexNum(final IFraction obj) {
		return F.complexNum(obj.doubleValue(), 0.0d);
	}

	public static IComplexNum complexNum(final IComplex obj) {
		final BigFraction r = obj.getRealPart();
		final BigFraction i = obj.getImaginaryPart();
		double nr = 0.0;
		double dr = 1.0;
		double ni = 0.0;
		double di = 1.0;
		// if (r instanceof IFraction) {
		nr = r.getNumerator().doubleValue();
		dr = r.getDenominator().doubleValue();
		// }
		// if (r instanceof IInteger) {
		// nr = ((IInteger) r).getNumerator().doubleValue();
		// }
		// if (i instanceof IFraction) {
		ni = i.getNumerator().doubleValue();
		di = i.getDenominator().doubleValue();
		// }
		// if (i instanceof IInteger) {
		// ni = ((IInteger) i).getNumerator().doubleValue();
		// }
		return F.complexNum(nr / dr, ni / di);
	}

	/**
	 * Create a function with 1 argument and evaluate it.
	 * 
	 * @param head
	 * @param a0
	 * @return the evaluated object
	 */
	public static IExpr eval(final ISymbol head, final IExpr a0) {
		final IAST ast = ast(head);
		ast.add(a0);
		return EvalEngine.eval(ast);
	}

	/**
	 * Create a function with 2 arguments and evaluate it.
	 * 
	 * @param head
	 * @param a0
	 * @param a1
	 * @return the evaluated object
	 */
	public static IExpr eval(final ISymbol head, final IExpr a0, final IExpr a1) {
		final IAST ast = ast(head);
		ast.add(a0);
		ast.add(a1);
		return EvalEngine.eval(ast);
	}

	/**
	 * Create a function with 3 arguments and evaluate it.
	 * 
	 * @param head
	 * @param a0
	 * @param a1
	 * @param a2
	 * @return the evaluated object
	 */
	public static IExpr eval(final ISymbol head, final IExpr a0, final IExpr a1, final IExpr a2) {
		final IAST ast = ast(head);
		ast.add(a0);
		ast.add(a1);
		ast.add(a2);
		return EvalEngine.eval(ast);
	}

	/**
	 * Create a function with 1 argument and evaluate it.
	 * 
	 * @param head
	 * @param a0
	 * @return the evaluated object or <code>null</code> if no evaluation was possible.
	 */
	public static IExpr evalNull(final ISymbol head, final IExpr a0) {
		final IAST ast = ast(head);
		ast.add(a0);
		return EvalEngine.evalNull(ast);
	}

	/**
	 * Create a function with 2 arguments and evaluate it.
	 * 
	 * @param head
	 * @param a0
	 * @param a1
	 * @return the evaluated object or <code>null</code> if no evaluation was possible.
	 */
	public static IExpr evalNull(final ISymbol head, final IExpr a0, final IExpr a1) {
		final IAST ast = ast(head);
		ast.add(a0);
		ast.add(a1);
		return EvalEngine.evalNull(ast);
	}

	/**
	 * Create a function with 3 arguments and evaluate it.
	 * 
	 * @param head
	 * @param a0
	 * @param a1
	 * @param a2
	 * @return the evaluated object or <code>null</code> if no evaluation was possible.
	 */
	public static IExpr evalNull(final ISymbol head, final IExpr a0, final IExpr a1, final IExpr a2) {
		final IAST ast = ast(head);
		ast.add(a0);
		ast.add(a1);
		ast.add(a2);
		return EvalEngine.evalNull(ast);
	}

	/**
	 * Evaluate the given expression in numeric mode
	 * 
	 * @param a0
	 * @return
	 */
	public static IExpr evaln(final IExpr a0) {
		return eval(N, a0);
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
		return FractionSym.valueOf(numerator, denominator);
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
		return FractionSym.valueOf(numerator, denominator);
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
	public static IFraction fraction(final long numerator, final long denominator) {
		return FractionSym.valueOf(numerator, denominator);
	}

	/**
	 * Create a "fractional" number
	 * 
	 * @param value
	 *            the rational value which should be converted to a fractional number
	 * @return IFraction
	 */
	public static IFraction fraction(final BigFraction value) {
		return FractionSym.valueOf(value.getNumerator(), value.getDenominator());
	}

	/**
	 * Create a "fractional" number
	 * 
	 * @param value
	 *            the double value which should be converted to a fractional number
	 * @return IFraction
	 */
	public static IFraction fraction(final double value) {
		return FractionSym.valueOf(value);
	}

	/**
	 * Get the namespace
	 * 
	 * @return
	 */
	final public static Namespace getNamespace() {
		return SystemNamespace.DEFAULT;
	}

	/**
	 * Create a large integer number.
	 * 
	 * @param integerValue
	 * @return
	 */
	public static IInteger integer(final BigInteger integerValue) {
		return IntegerSym.valueOf(integerValue);
	}

	/**
	 * Create a large integer number.
	 * 
	 * @param integerValue
	 * @return
	 */
	public static IInteger integer(final long integerValue) {
		return IntegerSym.valueOf(integerValue);
	}

	/**
	 * Create a large integer number.
	 * 
	 * @param integerString
	 *            the integer number represented as a String
	 * @param numberFormat
	 *            the format of the number (usually 10)
	 * @return Object
	 */
	public static IInteger integer(final String integerString, final int numberFormat) {
		return IntegerSym.valueOf(integerString, numberFormat);
	}

	/**
	 * Create a numeric value
	 * 
	 * @param d
	 * @return
	 */
	public static Num num(final double d) {
		return Num.valueOf(d);
	}

	public static Num num(final IInteger obj) {
		return num(obj.doubleValue());
	}

	public static Num num(final IFraction obj) {
		final double n = obj.getBigNumerator().doubleValue();
		final double d = obj.getBigDenominator().doubleValue();
		return num(n / d);
	}

	/**
	 * Create a numeric value
	 * 
	 * @param d
	 * @return
	 */
	public static Num num(final String doubleString) {
		return Num.valueOf(Double.parseDouble(doubleString));
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbol
	 * @return IPattern
	 */
	public static IPattern $p(final ISymbol symbol) {
		return org.matheclipse.core.expression.Pattern.valueOf(symbol);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbol
	 * @param check
	 *            additional condition which should be checked in pattern-matching
	 * @param def
	 *            if <code>true</code>, the pattern can match to a default value associated with the AST's head the pattern is used
	 *            in.
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
	 * @return IPattern
	 */
	public static IPattern $p(final ISymbol symbol, final IExpr check) {
		return org.matheclipse.core.expression.Pattern.valueOf(symbol, check);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbolName
	 * @return IPattern
	 */
	public static IPattern $p(final String symbolName) {
		if (symbolName == null) {
			return org.matheclipse.core.expression.Pattern.valueOf(null);
		}
		return org.matheclipse.core.expression.Pattern.valueOf($s(symbolName));
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbolName
	 * @param check
	 *            additional condition which should be checked in pattern-matching
	 * @return IPattern
	 */
	public static IPattern $p(final String symbolName, final IExpr check) {
		if (symbolName == null) {
			return org.matheclipse.core.expression.Pattern.valueOf(null, check);
		}
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
	public static IPattern $p(final String symbolName, boolean def) {
		return $p($s(symbolName), null, def);
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
	public static IPattern $p(final ISymbol symbol, boolean def) {
		return $p(symbol, null, def);
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
	public static IPattern $p(final String symbolName, final IExpr check, boolean def) {
		if (symbolName == null) {
			return org.matheclipse.core.expression.Pattern.valueOf(null, check, def);
		}
		return org.matheclipse.core.expression.Pattern.valueOf($s(symbolName), check, def);
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
	 * @param def
	 *            if <code>true</code>, the pattern can match to a default value associated with the AST's head the pattern is used
	 *            in.
	 * @return IPattern
	 */
	public static IPatternSequence $ps(final ISymbol symbol, final IExpr check, final boolean def) {
		return PatternSequence.valueOf(symbol, check, def);
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
	 * @param symbolName
	 *            the name of the pattrn symbol
	 * @return IPattern
	 */
	public static IPatternSequence $ps(final String symbolName) {
		return PatternSequence.valueOf((Symbol) $s(symbolName));
	}

	/**
	 * Create a "predefined" symbol for constants or function names.
	 * 
	 * @param symbolName
	 * @return
	 */
	public static ISymbol predefinedSymbol(final String symbolName) {
		ISymbol temp = PREDEFINED_SYMBOLS_MAP.get(symbolName);
		if (temp != null) {
			return temp;
		}
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			String lcSymbolName = symbolName.toLowerCase();
			// use the lower case string here to use it as associated class name
			// in package org.matheclipse.core.reflection.system
			temp = new Symbol(lcSymbolName);
			PREDEFINED_SYMBOLS_MAP.put(lcSymbolName, temp);
			return temp;
		}
		temp = new Symbol(symbolName);
		PREDEFINED_SYMBOLS_MAP.put(symbolName, temp);
		return temp;
	}

	/**
	 * Convert the symbolName to lowercase (if <code>Config.PARSER_USE_LOWERCASE_SYMBOLS</code> is set) and insert a new Symbol in
	 * the <code>PREDEFINED_SYMBOLS_MAP</code>. The symbol is created using the given upper case string to use it as associated
	 * class name in package org.matheclipse.core.reflection.system.
	 * 
	 * @param symbolName
	 *            the predefined symbol name in upper-case form
	 * @return
	 */
	public static ISymbol initFinalSymbol(final String symbolName) {
		ISymbol temp = new Symbol(symbolName);
		PREDEFINED_SYMBOLS_MAP.put(symbolName, temp);
		return temp;
	}

	/**
	 * Convert the symbolName to lowercase (if <code>Config.PARSER_USE_LOWERCASE_SYMBOLS</code> is set) and insert a new Symbol in
	 * the <code>PREDEFINED_SYMBOLS_MAP</code>. The symbol is created using the given upper case string to use it as associated
	 * class name in package org.matheclipse.core.reflection.system.
	 * 
	 * @param symbolName
	 *            the predefined symbol name in upper-case form
	 * @return
	 */
	private static ISymbol initFinalSymbol(final String symbolName, IEvaluator evaluator) {
		ISymbol temp = new Symbol(symbolName, evaluator);
		evaluator.setUp(temp);
		PREDEFINED_SYMBOLS_MAP.put(symbolName, temp);
		return temp;
	}

	private static IPattern initPredefinedPattern(final ISymbol symbol) {
		IPattern temp = new Pattern(symbol);
		PREDEFINED_PATTERN_MAP.put(symbol.toString(), temp);
		return temp;
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
	final static public IStringX stringx(final StringBuffer str) {
		return StringX.valueOf(str);
	}

	public static ISymbol method(final String symbolName, final String packageName, final String className, final String methodName) {
		return new MethodSymbol(symbolName, packageName, className, methodName);
	}

	public static ISymbol method(final String symbolName, final String className, final String methodName) {
		return new MethodSymbol(symbolName, className, methodName);
	}

	/**
	 * Get a global symbol which is retrieved from the global symbols map or created or retrieved from the thread local variables
	 * map.
	 * 
	 * @param symbolName
	 * @return
	 */
	public static ISymbol $s(final String symbolName) {
		return $s(symbolName, true);
	}

	public static ISymbol $s(final String symbolName, boolean setEval) {
		String name = symbolName;
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			name = symbolName.toLowerCase();
		}
		ISymbol symbol = PREDEFINED_SYMBOLS_MAP.get(name);
		if (symbol != null) {
			return symbol;
		}
		EvalEngine engine = EvalEngine.get();
		Map<String, ISymbol> variableMap = engine.getVariableMap();
		symbol = variableMap.get(name);
		if (symbol != null) {
			return symbol;
		}
		if (Config.SERVER_MODE) {
			if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
				if (SYMBOL_OBSERVER.createPredefinedSymbol(name)) {
					// second try, because the symbol may now be added to
					// fSymbolMap
					ISymbol secondTry = PREDEFINED_SYMBOLS_MAP.get(name);
					if (secondTry != null) {
						return secondTry;
					}
				}
			} else {
				if (Character.isUpperCase(name.charAt(0))) {
					if (SYMBOL_OBSERVER.createPredefinedSymbol(name)) {
						// second try, because the symbol may now be added to
						// fSymbolMap
						ISymbol secondTry = PREDEFINED_SYMBOLS_MAP.get(name);
						if (secondTry != null) {
							return secondTry;
						}
					}
				}
			}
			symbol = new Symbol(name);
			variableMap.put(name, symbol);
			if (name.charAt(0) == '$') {
				SYMBOL_OBSERVER.createUserSymbol(symbol);
			}
		} else {
			symbol = new Symbol(name);
			PREDEFINED_SYMBOLS_MAP.put(name, symbol);
			if (!setEval) {
				symbol.setEvaluator(Symbol.DUMMY_EVALUATOR);
			} else {
				symbol.getEvaluator();
			}
		}

		return symbol;
	}

	/**
	 * Create a local symbol which is created or retrieved from the eval engines thread local variables map and push a value on the
	 * local stack;
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @return
	 */
	public static ISymbol local(final String symbolName, IExpr value) {
		// HashMap<String, ISymbol> variableMap = EvalEngine.getVariableMap();
		// ISymbol temp = variableMap.get(symbolName);
		// if (temp != null) {
		// temp.pushLocalVariable(value);
		// return temp;
		// }
		ISymbol temp = new Symbol(symbolName);
		// variableMap.put(symbolName, temp);
		temp.pushLocalVariable(value);
		return temp;
	}

	/**
	 * Create a local symbol which is created or retrieved from the eval engines thread local variables map and push a
	 * <code>null</code> value on the local stack;
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @return
	 */
	public static ISymbol local(final String symbolName) {
		return local(symbolName, null);
	}

	/**
	 * Pop the current top value from the symbols local variable stack.
	 * 
	 * @param temp
	 */
	public static void popLocal(ISymbol temp) {
		temp.popLocalVariable();
	}

	public static IExpr plus(Integer i, IExpr b) {
		return Plus(integer(i.longValue()), b);
	}

	public static IExpr plus(IExpr a, Integer i) {
		return Plus(a, integer(i.longValue()));
	}

	public static IExpr minus(Integer i, IExpr b) {
		return Plus(integer(i.longValue()), Times(b, CN1));
	}

	public static IExpr minus(IExpr a, Integer i) {
		return Plus(a, Times(integer(i.longValue()), CN1));
	}

	public static IExpr multiply(Integer i, IExpr b) {
		return Times(integer(i.longValue()), b);
	}

	public static IExpr multiply(IExpr a, Integer i) {
		return Times(a, integer(i.longValue()));
	}

	public static IExpr div(IExpr a, Integer i) {
		return Times(a, Power(integer(i.longValue()), CN1));
	}

	public static IExpr div(Integer i, IExpr b) {
		return Times(integer(i.longValue()), Power(b, CN1));
	}

	public static IExpr mod(IExpr a, Integer i) {
		return Mod(a, integer(i.longValue()));
	}

	public static IExpr mod(Integer i, IExpr b) {
		return Mod(integer(i.longValue()), b);
	}

	public static IExpr and(IExpr a, Integer i) {
		return And(a, integer(i.longValue()));
	}

	public static IExpr and(Integer i, IExpr b) {
		return And(integer(i.longValue()), b);
	}

	public static IExpr or(IExpr a, Integer i) {
		return $(Or, a, integer(i.longValue()));
	}

	public static IExpr or(Integer i, IExpr b) {
		return $(Or, integer(i.longValue()), b);
	}

	/**
	 * After a successful <code>isCase()</code> the symbols associated with the patterns contain the matched values on the local
	 * stack.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	// public static boolean isCase(IExpr a, IExpr b) {
	// if (a instanceof IAST) {
	// final PatternMatcher matcher = new PatternMatcher(a);
	// if (matcher.apply(b)) {
	// matcher.setPatternValue2Local(a);
	// return true;
	// }
	// }
	// return equals(a, b);
	// }
	//
	// public static boolean isCase(IExpr a, Integer i) {
	// return isCase(a, integer(i.longValue()));
	// }
	//
	// public static boolean isCase(Integer i, IExpr b) {
	// return equals(i, b);
	// }
	//
	// public static boolean isCase(IExpr a, java.math.BigInteger i) {
	// return isCase(a, integer(i));
	// }
	//
	// public static boolean isCase(java.math.BigInteger i, IExpr b) {
	// return equals(i, b);
	// }

	public static boolean isNumIntValue(double value) {
		return isZero(value - Math.round(value), Config.DOUBLE_TOLERANCE);
	}

	public static boolean isNumIntValue(double value, double epsilon) {
		return isZero(value - Math.round(value), epsilon);
	}

	/**
	 * Test if the absolute value is less <code>Config.DOUBLE_EPSILON</code>.
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isZero(double value) {
		return isZero(value, Config.DOUBLE_EPSILON);
	}

	/**
	 * Test if the absolute value is less than the given epsilon.
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isZero(double value, double epsilon) {
		return Math.abs(value) < epsilon;
	}

	/**
	 * Evaluate an expression. If no evaluation was possible this method returns the given argument.
	 * 
	 * @param a
	 *            the expression which should be evaluated
	 * @return the evaluated expression
	 * @see EvalEngine#eval(IExpr)
	 */
	public static IExpr eval(IExpr a) {
		return EvalEngine.eval(a);
	}

	/**
	 * Substitute all (sub-) expressions with the given unary function. If no substitution matches, the method returns the given
	 * <code>expr</code>.
	 * 
	 * @param function
	 *            if the unary functions <code>apply()</code> method returns <code>null</code> the expression isn't substituted.
	 * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or the substituted expression.
	 */
	public static IExpr subst(IExpr expr, final Function<IExpr, IExpr> function) {
		final IExpr result = expr.replaceAll(function);
		return (result == null) ? expr : result;
	}

	/**
	 * Substitute all (sub-) expressions with the given rule set. If no substitution matches, the method returns the given
	 * <code>expr</code>.
	 * 
	 * @param astRules
	 *            rules of the form <code>x-&gt;y</code> or <code>{a-&gt;b, c-&gt;d}</code>; the left-hand-side of the rule can
	 *            contain pattern objects.
	 * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or the substituted expression.
	 */
	public static IExpr subst(IExpr expr, final IAST astRules) {
		final IExpr result = expr.replaceAll(astRules);
		return (result == null) ? expr : result;
	}

	/**
	 * Apply <code>ExpandAll[]</code> to the given expression and evaluate it. If no evaluation was possible this method returns the
	 * given argument.
	 * 
	 * @param a
	 *            the expression which should be evaluated
	 * @return the evaluated expression
	 * @see EvalEngine#eval(IExpr)
	 */
	public static IExpr evalExpandAll(IExpr a) {
		return EvalEngine.eval(ExpandAll(a));
	}

	/**
	 * Evaluate an expression for a local variable.
	 * 
	 * 
	 * @param expr
	 *            the expression which should be evaluated for the given symbol
	 * @param symbol
	 *            the symbol which should be evaluated as a local variable
	 * @param localValue
	 *            the value
	 */
	public static IExpr evalBlock(IExpr expr, ISymbol symbol, IExpr localValue) {
		try {
			symbol.pushLocalVariable(localValue);
			return F.eval(expr);
		} finally {
			symbol.popLocalVariable();
		}
	}

	/**
	 * Evaluate the given expression and test if the result equals the symbol <code>True</code>.
	 * 
	 * @param expr
	 * @return
	 */
	public static boolean evalTrue(IExpr expr) {
		return EvalEngine.get().evalTrue(expr);
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
		return Object2Expr.CONST.convert(obj);
	}

	public static boolean equals(IExpr a, IExpr b) {
		IExpr tempA = a;
		IExpr tempB = b;
		if (a.isAST()) {
			tempA = eval(a);
		}
		if (b.isAST()) {
			tempB = eval(b);
		}
		return tempA.equals(tempB);
	}

	public static boolean equals(IExpr a, java.math.BigInteger i) {
		IExpr tempA = a;
		IExpr tempB = integer(i);
		if (a.isAST()) {
			tempA = eval(a);
		}
		return tempA.equals(tempB);
	}

	public static boolean equals(java.math.BigInteger i, IExpr b) {
		IExpr tempA = integer(i);
		IExpr tempB = b;
		if (b instanceof AST) {
			tempB = eval(b);
		}
		return tempA.equals(tempB);
	}

	public static boolean equals(IExpr a, Integer i) {
		IExpr tempA = a;
		IExpr tempB = integer(i.longValue());
		if (a instanceof AST) {
			tempA = eval(a);
		}
		return tempA.equals(tempB);
	}

	public static boolean equals(Integer i, IExpr b) {
		IExpr tempA = integer(i.longValue());
		IExpr tempB = b;
		if (b instanceof AST) {
			tempB = eval(b);
		}
		return tempA.equals(tempB);
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
		throw new UnsupportedOperationException("compareTo() - first argument could not be converted into a signed number.");
	}

	public static int compareTo(Integer i, IExpr b) throws UnsupportedOperationException {
		if (b instanceof ISignedNumber) {
			return integer(i.longValue()).compareTo(b);
		}
		IExpr temp = eval(b);
		if (temp instanceof ISignedNumber) {
			return integer(i.longValue()).compareTo(temp);
		}
		throw new UnsupportedOperationException("compareTo() - second argument could not be converted into a signed number.");
	}

	public static int compareTo(IExpr a, java.math.BigInteger i) throws UnsupportedOperationException {
		if (a instanceof ISignedNumber) {
			return a.compareTo(integer(i));
		}
		IExpr temp = eval(a);
		if (temp instanceof ISignedNumber) {
			return temp.compareTo(integer(i));
		}
		throw new UnsupportedOperationException("compareTo() - first argument could not be converted into a signed number.");
	}

	public static int compareTo(java.math.BigInteger i, IExpr b) throws UnsupportedOperationException {
		if (b instanceof ISignedNumber) {
			return integer(i).compareTo(b);
		}
		IExpr temp = eval(b);
		if (temp instanceof ISignedNumber) {
			return integer(i).compareTo(temp);
		}
		throw new UnsupportedOperationException("compareTo() - second argument could not be converted into a signed number.");
	}

	public static IExpr plus(java.math.BigInteger i, IExpr b) {
		return Plus(integer(i), b);
	}

	public static IExpr plus(IExpr a, java.math.BigInteger i) {
		return Plus(a, integer(i));
	}

	public static IExpr minus(java.math.BigInteger i, IExpr b) {
		return Plus(integer(i), Times(b, CN1));
	}

	public static IExpr minus(IExpr a, java.math.BigInteger i) {
		return Plus(a, Times(integer(i), CN1));
	}

	public static IExpr multiply(java.math.BigInteger i, IExpr b) {
		return Times(integer(i), b);
	}

	public static IExpr multiply(IExpr a, java.math.BigInteger i) {
		return Times(a, integer(i));
	}

	public static IExpr div(IExpr a, java.math.BigInteger i) {
		return Times(a, Power(integer(i), CN1));
	}

	public static IExpr div(java.math.BigInteger i, IExpr b) {
		return Times(integer(i), Power(b, CN1));
	}

	public static IExpr mod(IExpr a, java.math.BigInteger i) {
		return Mod(a, integer(i));
	}

	public static IExpr mod(java.math.BigInteger i, IExpr b) {
		return Mod(integer(i), b);
	}

	public static IExpr and(IExpr a, java.math.BigInteger i) {
		return And(a, integer(i));
	}

	public static IExpr and(java.math.BigInteger i, IExpr b) {
		return And(integer(i), b);
	}

	public static IExpr or(IExpr a, java.math.BigInteger i) {
		return Or(a, integer(i));
	}

	public static IExpr or(java.math.BigInteger i, IExpr b) {
		return Or(integer(i), b);
	}

}