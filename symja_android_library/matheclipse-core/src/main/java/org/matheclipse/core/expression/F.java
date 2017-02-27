package org.matheclipse.core.expression;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;
import org.hipparchus.complex.Complex;
import org.hipparchus.fraction.BigFraction;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.ConstantDefinitions;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Namespace;
import org.matheclipse.core.eval.SystemNamespace;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IEvaluator;
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

import edu.jas.kern.ComputerThreads;

/**
 * 
 * Factory for creating Symja expression objects.
 * 
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
	 * The map for predefined strings for the
	 * {@link IExpr#internalFormString(boolean, int)} method.
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
	 * The constant object <code>NIL</code> (not in list) indicates in the
	 * evaluation process that no evaluation was possible (i.e. no further
	 * definition was found to create a new expression from the existing one).
	 */
	public final static NILPointer NIL = new NILPointer();

	public final static IBuiltInSymbol Catalan = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "catalan" : "Catalan");
	public final static IBuiltInSymbol ComplexInfinity = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "complexinfinity" : "ComplexInfinity");
	public final static IBuiltInSymbol Degree = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "degree" : "Degree");
	public final static IBuiltInSymbol E = initFinalSymbol("E");
	public final static IBuiltInSymbol EulerGamma = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "eulergamma" : "EulerGamma");
	public final static IBuiltInSymbol Glaisher = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "glaisher" : "Glaisher");
	public final static IBuiltInSymbol GoldenRatio = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "goldenratio" : "GoldenRatio");
	public final static IBuiltInSymbol I = initFinalSymbol("I");
	public final static IBuiltInSymbol Infinity = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "infinity" : "Infinity");
	public final static IBuiltInSymbol Khinchin = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "khinchin" : "Khinchin");
	public final static IBuiltInSymbol Pi = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "pi" : "Pi");

	public final static IBuiltInSymbol Aborted = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "$aborted" : "$Aborted");
	public final static IBuiltInSymbol Assumptions = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "assumptions" : "Assumptions");
	public final static IBuiltInSymbol Begin = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "begin" : "Begin");
	public final static IBuiltInSymbol BeginPackage = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "beginpackage" : "BeginPackage");
	public final static IBuiltInSymbol End = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "end" : "End");
	public final static IBuiltInSymbol EndPackage = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "endpackage" : "EndPackage");
	public final static IBuiltInSymbol Except = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "except" : "Except");
	public final static IBuiltInSymbol IntegerHead = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "integer" : "Integer");
	public final static IBuiltInSymbol SymbolHead = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "symbol" : "Symbol");
	public final static IBuiltInSymbol RealHead = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "real" : "Real");
	public final static IBuiltInSymbol PatternHead = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "pattern" : "Pattern");
	public final static IBuiltInSymbol BlankHead = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "blank" : "Blank");
	public final static IBuiltInSymbol StringHead = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "string" : "String");
	public final static IBuiltInSymbol MethodHead = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "methodhead" : "MethodHead");
	public final static IBuiltInSymbol PatternTest = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "patterntest" : "PatternTest");
	public final static IBuiltInSymbol Colon = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "colon" : "Colon");
	public final static IBuiltInSymbol Repeated = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "repeated" : "Repeated");
	public final static IBuiltInSymbol RepeatedNull = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "repeatednull" : "RepeatedNull");

	public final static IBuiltInSymbol All = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "all" : "All");
	public final static IBuiltInSymbol None = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "none" : "None");

	public final static IBuiltInSymbol Algebraics = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "algebraics" : "Algebraics");
	public final static IBuiltInSymbol Booleans = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "booleans" : "Booleans");
	public final static IBuiltInSymbol Complexes = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "complexes" : "Complexes");
	public final static IBuiltInSymbol Integers = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "integers" : "Integers");
	public final static IBuiltInSymbol Primes = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "primes" : "Primes");
	public final static IBuiltInSymbol Rationals = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rationals" : "Rationals");
	public final static IBuiltInSymbol Reals = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "reals" : "Reals");

	public final static IBuiltInSymbol False = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "false" : "False");
	public final static IBuiltInSymbol Alternatives = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "alternatives" : "Alternatives");

	public final static IBuiltInSymbol Direction = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "direction" : "Direction");
	public final static IBuiltInSymbol List = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "list" : "List");
	public final static IBuiltInSymbol $RealVector = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "$realvector" : "$RealVector");
	public final static IBuiltInSymbol $RealMatrix = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "$realmatrix" : "$RealMatrix");

	public final static IBuiltInSymbol True = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "true" : "True");
	public final static IBuiltInSymbol Null = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "null" : "Null");
	public final static IBuiltInSymbol Second = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "second" : "Second");
	public final static IBuiltInSymbol Indeterminate = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "indeterminate" : "Indeterminate");
	public final static IBuiltInSymbol Listable = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "listable" : "Listable");
	public final static IBuiltInSymbol Constant = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "constant" : "Constant");
	public final static IBuiltInSymbol NumericFunction = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "numericfunction" : "NumericFunction");

	public final static IBuiltInSymbol Orderless = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "orderless" : "Orderless");
	public final static IBuiltInSymbol OneIdentity = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "oneidentity" : "OneIdentity");
	public final static IBuiltInSymbol Flat = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "flat" : "Flat");
	public final static IBuiltInSymbol HoldFirst = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "holdfirst" : "HoldFirst");
	public final static IBuiltInSymbol HoldRest = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "holdrest" : "HoldRest");
	public final static IBuiltInSymbol HoldAll = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "holdall" : "HoldAll");
	public final static IBuiltInSymbol NHoldFirst = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nholdfirst" : "NHoldFirst");
	public final static IBuiltInSymbol NHoldRest = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nholdrest" : "NHoldRest");
	public final static IBuiltInSymbol NHoldAll = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nholdall" : "NHoldAll");
	public final static IBuiltInSymbol Line = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "line" : "Line");
	public final static IBuiltInSymbol Polygon = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polygon" : "Polygon");

	public final static IBuiltInSymbol BoxRatios = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "boxratios" : "BoxRatios");
	public final static IBuiltInSymbol Modulus = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "modulus" : "Modulus");
	public final static IBuiltInSymbol MeshRange = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "meshrange" : "MeshRange");
	public final static IBuiltInSymbol PlotRange = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "plotrange" : "PlotRange");
	public final static IBuiltInSymbol AxesStyle = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "axesstyle" : "AxesStyle");
	public final static IBuiltInSymbol Automatic = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "automatic" : "Automatic");
	public final static IBuiltInSymbol AxesOrigin = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "axesorigin" : "AxesOrigin");
	public final static IBuiltInSymbol Axes = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "axes" : "Axes");
	public final static IBuiltInSymbol Background = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "background" : "Background");
	public final static IBuiltInSymbol White = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "white" : "White");
	public final static IBuiltInSymbol Slot = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "slot" : "Slot");
	public final static IBuiltInSymbol SlotSequence = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "slotsequence" : "SlotSequence");
	public final static IBuiltInSymbol Span = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "span" : "Span");
	public final static IBuiltInSymbol Options = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "options" : "Options");
	public final static IBuiltInSymbol Graphics = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "graphics" : "Graphics");
	public final static IBuiltInSymbol Graphics3D = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "graphics3d" : "Graphics3D");

	public final static IBuiltInSymbol Show = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "show" : "Show");
	public final static IBuiltInSymbol SurfaceGraphics = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "surfacegraphics" : "SurfaceGraphics");
	public final static IBuiltInSymbol RootOf = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rootof" : "RootOf");
	public final static IBuiltInSymbol Sequence = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sequence" : "Sequence");
	public final static IBuiltInSymbol Missing = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "missing" : "Missing");
	public final static IBuiltInSymbol NotApplicable = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "notapplicable" : "NotApplicable");
	public final static IBuiltInSymbol NotAvailable = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "notavailable" : "NotAvailable");
	public final static IBuiltInSymbol Unknown = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "unknown" : "Unknown");

	public final static IBuiltInSymbol Erfc = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "erfc" : "Erfc");
	public final static IBuiltInSymbol Erfi = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "erfi" : "Erfi");
	public final static IBuiltInSymbol HurwitzZeta = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "hurwitzzeta" : "HurwitzZeta");
	public final static IBuiltInSymbol Literal = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "literal" : "Literal");
	public final static IBuiltInSymbol O = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "O" : "O");

	public final static IBuiltInSymbol Subscript = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "subscript" : "Subscript");
	public final static IBuiltInSymbol Subsuperscript = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "subsuperscript" : "Subsuperscript");
	public final static IBuiltInSymbol Superscript = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "subscript" : "Superscript");

	public final static IBuiltInSymbol BinomialDistribution = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "binomialdistribution" : "BinomialDistribution");
	public final static IBuiltInSymbol BernoulliDistribution = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "bernoullidistribution" : "BernoulliDistribution");
	public final static IBuiltInSymbol HypergeometricDistribution = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "hypergeometricdistribution" : "HypergeometricDistribution");
	public final static IBuiltInSymbol NormalDistribution = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "normaldistribution" : "NormalDistribution");
	public final static IBuiltInSymbol PoissonDistribution = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "poissondistribution" : "PoissonDistribution");

	public final static IBuiltInSymbol And = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "and" : "And",
			new org.matheclipse.core.builtin.function.And());
	public final static IBuiltInSymbol Append = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "append" : "Append",
			new org.matheclipse.core.builtin.function.Append());
	public final static IBuiltInSymbol AppendTo = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "appendto" : "AppendTo",
			new org.matheclipse.core.builtin.function.AppendTo());
	public final static IBuiltInSymbol Apply = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "apply" : "Apply",
			new org.matheclipse.core.builtin.function.Apply());
	public final static IBuiltInSymbol Array = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "array" : "Array",
			new org.matheclipse.core.builtin.function.Array());
	public final static IBuiltInSymbol ArrayQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arrayq" : "ArrayQ",
			new org.matheclipse.core.builtin.function.ArrayQ());
	public final static IBuiltInSymbol AtomQ = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "atomq" : "AtomQ",
			new org.matheclipse.core.builtin.function.AtomQ());
	public final static IBuiltInSymbol Attributes = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "attributes" : "Attributes",
			new org.matheclipse.core.builtin.function.Attributes());
	public final static IBuiltInSymbol Blank = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "blank" : "Blank",
			new org.matheclipse.core.builtin.function.Blank());
	public final static IBuiltInSymbol Block = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "block" : "Block",
			new org.matheclipse.core.builtin.function.Block());
	public final static IBuiltInSymbol Break = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "break" : "Break",
			new org.matheclipse.core.builtin.function.Break());
	public final static IBuiltInSymbol BooleanQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "booleanq" : "BooleanQ",
			new org.matheclipse.core.builtin.function.BooleanQ());
	public final static IBuiltInSymbol Cases = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cases" : "Cases",
			new org.matheclipse.core.builtin.function.Cases());
	public final static IBuiltInSymbol Catch = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "catch" : "Catch",
			new org.matheclipse.core.builtin.function.Catch());
	public final static IBuiltInSymbol Chop = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "chop" : "Chop",
			new org.matheclipse.core.builtin.function.Chop());
	public final static IBuiltInSymbol Clear = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "clear" : "Clear",
			new org.matheclipse.core.builtin.function.Clear());
	public final static IBuiltInSymbol ClearAll = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "clearall" : "ClearAll",
			new org.matheclipse.core.builtin.function.ClearAll());
	public final static IBuiltInSymbol Collect = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "collect" : "Collect",
			new org.matheclipse.core.builtin.function.Collect());
	public final static IBuiltInSymbol Compile = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "compile" : "Compile",
			new org.matheclipse.core.builtin.function.Compile());
	public final static IBuiltInSymbol Complex = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "complex" : "Complex",
			new org.matheclipse.core.builtin.function.Complex());
	public final static IBuiltInSymbol CompoundExpression = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "compoundexpression" : "CompoundExpression",
			new org.matheclipse.core.builtin.function.CompoundExpression());
	public final static IBuiltInSymbol Condition = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "condition" : "Condition",
			new org.matheclipse.core.builtin.function.Condition());
	public final static IBuiltInSymbol Continue = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "continue" : "Continue",
			new org.matheclipse.core.builtin.function.Continue());
	public final static IBuiltInSymbol Count = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "count" : "Count",
			new org.matheclipse.core.builtin.function.Count());
	public final static IBuiltInSymbol Defer = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "defer" : "Defer",
			new org.matheclipse.core.builtin.function.Defer());
	public final static IBuiltInSymbol Definition = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "definition" : "Definition",
			new org.matheclipse.core.builtin.function.Definition());
	public final static IBuiltInSymbol Delete = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "delete" : "Delete",
			new org.matheclipse.core.builtin.function.Delete());
	public final static IBuiltInSymbol DeleteCases = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "deletecases" : "DeleteCases",
			new org.matheclipse.core.builtin.function.DeleteCases());
	public final static IBuiltInSymbol Depth = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "depth" : "Depth",
			new org.matheclipse.core.builtin.function.Depth());
	public final static IBuiltInSymbol DigitQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "digitq" : "DigitQ",
			new org.matheclipse.core.builtin.function.DigitQ());
	public final static IBuiltInSymbol DirectedInfinity = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "directedinfinity" : "DirectedInfinity",
			new org.matheclipse.core.builtin.function.DirectedInfinity());
	public final static IBuiltInSymbol Do = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "do" : "Do",
			new org.matheclipse.core.builtin.function.Do());
	public final static IBuiltInSymbol Drop = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "drop" : "Drop",
			new org.matheclipse.core.builtin.function.Drop());
	public final static IBuiltInSymbol Element = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "element" : "Element",
			new org.matheclipse.core.builtin.function.Element());
	public final static IBuiltInSymbol EvenQ = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "evenq" : "EvenQ",
			new org.matheclipse.core.builtin.function.EvenQ());
	public final static IBuiltInSymbol Exponent = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "exponent" : "Exponent",
			new org.matheclipse.core.builtin.function.Exponent());
	public final static IBuiltInSymbol First = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "first" : "First",
			new org.matheclipse.core.builtin.function.First());
	public final static IBuiltInSymbol FixedPoint = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fixedpoint" : "FixedPoint",
			new org.matheclipse.core.builtin.function.FixedPoint());
	public final static IBuiltInSymbol Flatten = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "flatten" : "Flatten",
			new org.matheclipse.core.builtin.function.Flatten());
	public final static IBuiltInSymbol Fold = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fold" : "Fold",
			new org.matheclipse.core.builtin.function.Fold());
	public final static IBuiltInSymbol FoldList = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "foldlist" : "FoldList",
			new org.matheclipse.core.builtin.function.FoldList());
	public final static IBuiltInSymbol For = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "for" : "For",
			new org.matheclipse.core.builtin.function.For());
	public final static IBuiltInSymbol FreeQ = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "freeq" : "FreeQ",
			new org.matheclipse.core.builtin.function.FreeQ());
	public final static IBuiltInSymbol FullForm = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fullform" : "FullForm",
			new org.matheclipse.core.builtin.function.FullForm());
	public final static IBuiltInSymbol Function = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "function" : "Function",
			new org.matheclipse.core.builtin.function.Function());
	public final static IBuiltInSymbol Get = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "get" : "Get",
			new org.matheclipse.core.builtin.function.Get());
	public final static IBuiltInSymbol Head = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "head" : "Head",
			new org.matheclipse.core.builtin.function.Head());
	public final static IBuiltInSymbol Hold = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "hold" : "Hold",
			new org.matheclipse.core.builtin.function.Hold());
	public final static IBuiltInSymbol HoldForm = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "holdform" : "HoldForm",
			new org.matheclipse.core.builtin.function.HoldForm());
	public final static IBuiltInSymbol Identity = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "identity" : "Identity",
			new org.matheclipse.core.builtin.function.Identity());
	public final static IBuiltInSymbol If = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "if" : "If",
			new org.matheclipse.core.builtin.function.If());
	public final static IBuiltInSymbol Implies = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "implies" : "Implies",
			new org.matheclipse.core.builtin.function.Implies());
	public final static IBuiltInSymbol Insert = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "insert" : "Insert",
			new org.matheclipse.core.builtin.function.Insert());
	public final static IBuiltInSymbol IntegerQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "integerq" : "IntegerQ",
			new org.matheclipse.core.builtin.function.IntegerQ());
	public final static IBuiltInSymbol JavaForm = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "javaform" : "JavaForm",
			new org.matheclipse.core.builtin.function.JavaForm());
	public final static IBuiltInSymbol Last = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "last" : "Last",
			new org.matheclipse.core.builtin.function.Last());
	public final static IBuiltInSymbol LeafCount = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "leafcount" : "LeafCount",
			new org.matheclipse.core.builtin.function.LeafCount());
	public final static IBuiltInSymbol Length = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "length" : "Length",
			new org.matheclipse.core.builtin.function.Length());
	public final static IBuiltInSymbol ListQ = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "listq" : "ListQ",
			new org.matheclipse.core.builtin.function.ListQ());
	public final static IBuiltInSymbol MatchQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "matchq" : "MatchQ",
			new org.matheclipse.core.builtin.function.MatchQ());
	public final static IBuiltInSymbol MathMLForm = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "mathmlform" : "MathMLForm",
			new org.matheclipse.core.builtin.function.MathMLForm());
	public final static IBuiltInSymbol MatrixQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "matrixq" : "MatrixQ",
			new org.matheclipse.core.builtin.function.MatrixQ());
	public final static IBuiltInSymbol MemberQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "memberq" : "MemberQ",
			new org.matheclipse.core.builtin.function.MemberQ());
	public final static IBuiltInSymbol MessageName = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "messagename" : "MessageName",
			new org.matheclipse.core.builtin.function.MessageName());
	public final static IBuiltInSymbol MissingQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "missingq" : "MissingQ",
			new org.matheclipse.core.builtin.function.MissingQ());
	public final static IBuiltInSymbol Module = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "module" : "Module",
			new org.matheclipse.core.builtin.function.Module());
	public final static IBuiltInSymbol N = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "N" : "N",
			new org.matheclipse.core.builtin.function.N());
	public final static IBuiltInSymbol Nand = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nand" : "Nand",
			new org.matheclipse.core.builtin.function.Nand());
	public final static IBuiltInSymbol Nest = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nest" : "Nest",
			new org.matheclipse.core.builtin.function.Nest());
	public final static IBuiltInSymbol NestList = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nestlist" : "NestList",
			new org.matheclipse.core.builtin.function.NestList());
	public final static IBuiltInSymbol NestWhile = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nestwhile" : "NestWhile",
			new org.matheclipse.core.builtin.function.NestWhile());
	public final static IBuiltInSymbol NestWhileList = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nestwhilelist" : "NestWhileList",
			new org.matheclipse.core.builtin.function.NestWhileList());
	public final static IBuiltInSymbol Nor = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nor" : "Nor",
			new org.matheclipse.core.builtin.function.Nor());
	public final static IBuiltInSymbol NumberQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "numberq" : "NumberQ",
			new org.matheclipse.core.builtin.function.NumberQ());
	public final static IBuiltInSymbol NumericQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "numericq" : "NumericQ",
			new org.matheclipse.core.builtin.function.NumericQ());
	public final static IBuiltInSymbol OddQ = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "oddq" : "OddQ",
			new org.matheclipse.core.builtin.function.OddQ());
	public final static IBuiltInSymbol Or = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "or" : "Or",
			new org.matheclipse.core.builtin.function.Or());
	public final static IBuiltInSymbol Package = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "package" : "Package",
			new org.matheclipse.core.builtin.function.Package());
	public final static IBuiltInSymbol Part = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "part" : "Part",
			new org.matheclipse.core.builtin.function.Part());
	public final static IBuiltInSymbol Pattern = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "pattern" : "Pattern",
			new org.matheclipse.core.builtin.function.Pattern());
	public final static IBuiltInSymbol Position = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "position" : "Position",
			new org.matheclipse.core.builtin.function.Position());
	public final static IBuiltInSymbol PossibleZeroQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "possiblezeroq" : "PossibleZeroQ",
			new org.matheclipse.core.builtin.function.PossibleZeroQ());
	public final static IBuiltInSymbol Prepend = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "prepend" : "Prepend",
			new org.matheclipse.core.builtin.function.Prepend());
	public final static IBuiltInSymbol PrependTo = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "prependto" : "PrependTo",
			new org.matheclipse.core.builtin.function.PrependTo());
	public final static IBuiltInSymbol PrimeQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "primeq" : "PrimeQ",
			new org.matheclipse.core.builtin.function.PrimeQ());
	public final static IBuiltInSymbol Print = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "print" : "Print",
			new org.matheclipse.core.builtin.function.Print());
	public final static IBuiltInSymbol Put = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "put" : "Put",
			new org.matheclipse.core.builtin.function.Put());
	public final static IBuiltInSymbol Quiet = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "quiet" : "Quiet",
			new org.matheclipse.core.builtin.function.Quiet());
	public final static IBuiltInSymbol Rational = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rational" : "Rational",
			new org.matheclipse.core.builtin.function.Rational());
	public final static IBuiltInSymbol Reap = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "reap" : "Reap",
			new org.matheclipse.core.builtin.function.Reap());
	public final static IBuiltInSymbol Refine = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "refine" : "Refine",
			new org.matheclipse.core.builtin.function.Refine());
	public final static IBuiltInSymbol Rest = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rest" : "Rest",
			new org.matheclipse.core.builtin.function.Rest());
	public final static IBuiltInSymbol Return = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "return" : "Return",
			new org.matheclipse.core.builtin.function.Return());
	public final static IBuiltInSymbol Riffle = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "riffle" : "Riffle",
			new org.matheclipse.core.builtin.function.Riffle());
	public final static IBuiltInSymbol RotateLeft = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rotateleft" : "RotateLeft",
			new org.matheclipse.core.builtin.function.RotateLeft());
	public final static IBuiltInSymbol RotateRight = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rotateright" : "RotateRight",
			new org.matheclipse.core.builtin.function.RotateRight());
	public final static IBuiltInSymbol Rule = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rule" : "Rule",
			new org.matheclipse.core.builtin.function.Rule());
	public final static IBuiltInSymbol RuleDelayed = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "ruledelayed" : "RuleDelayed",
			new org.matheclipse.core.builtin.function.RuleDelayed());
	public final static IBuiltInSymbol Set = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "set" : "Set",
			new org.matheclipse.core.builtin.function.Set());
	public final static IBuiltInSymbol SetAttributes = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "setattributes" : "SetAttributes",
			new org.matheclipse.core.builtin.function.SetAttributes());
	public final static IBuiltInSymbol SetDelayed = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "setdelayed" : "SetDelayed",
			new org.matheclipse.core.builtin.function.SetDelayed());
	public final static IBuiltInSymbol Sow = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sow" : "Sow",
			new org.matheclipse.core.builtin.function.Sow());
	public final static IBuiltInSymbol Switch = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "switch" : "Switch",
			new org.matheclipse.core.builtin.function.Switch());
	public final static IBuiltInSymbol SyntaxQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "syntaxq" : "SyntaxQ",
			new org.matheclipse.core.builtin.function.SyntaxQ());
	public final static IBuiltInSymbol Take = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "take" : "Take",
			new org.matheclipse.core.builtin.function.Take());
	public final static IBuiltInSymbol TeXForm = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "texform" : "TeXForm",
			new org.matheclipse.core.builtin.function.TeXForm());
	public final static IBuiltInSymbol Throw = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "throw" : "Throw",
			new org.matheclipse.core.builtin.function.Throw());
	public final static IBuiltInSymbol TimeConstrained = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "timeconstrained" : "TimeConstrained",
			new org.matheclipse.core.builtin.function.TimeConstrained());
	public final static IBuiltInSymbol Timing = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "timing" : "Timing",
			new org.matheclipse.core.builtin.function.Timing());
	public final static IBuiltInSymbol Trace = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "trace" : "Trace",
			new org.matheclipse.core.builtin.function.Trace());
	public final static IBuiltInSymbol Unevaluated = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "unevaluated" : "Unevaluated",
			new org.matheclipse.core.builtin.function.Unevaluated());
	public final static IBuiltInSymbol Unique = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "unique" : "Unique",
			new org.matheclipse.core.builtin.function.Unique());
	public final static IBuiltInSymbol Unset = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "unset" : "Unset",
			new org.matheclipse.core.builtin.function.Unset());
	public final static IBuiltInSymbol UpperCaseQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "uppercaseq" : "UpperCaseQ",
			new org.matheclipse.core.builtin.function.UpperCaseQ());
	public final static IBuiltInSymbol UpSet = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "upset" : "UpSet",
			new org.matheclipse.core.builtin.function.UpSet());
	public final static IBuiltInSymbol UpSetDelayed = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "upsetdelayed" : "UpSetDelayed",
			new org.matheclipse.core.builtin.function.UpSetDelayed());
	public final static IBuiltInSymbol ValueQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "valueq" : "ValueQ",
			new org.matheclipse.core.builtin.function.ValueQ());
	public final static IBuiltInSymbol VectorQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "vectorq" : "VectorQ",
			new org.matheclipse.core.builtin.function.VectorQ());
	public final static IBuiltInSymbol Which = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "which" : "Which",
			new org.matheclipse.core.builtin.function.Which());
	public final static IBuiltInSymbol While = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "while" : "While",
			new org.matheclipse.core.builtin.function.While());

	public final static IBuiltInSymbol Abs = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "abs" : "Abs");
	public final static IBuiltInSymbol AbsArg = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "absarg" : "AbsArg");
	public final static IBuiltInSymbol AddTo = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "addto" : "AddTo");
	public final static IBuiltInSymbol AllTrue = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "alltrue" : "AllTrue");
	public final static IBuiltInSymbol AnyTrue = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "anytrue" : "AnyTrue");
	public final static IBuiltInSymbol Apart = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "apart" : "Apart");
	public final static IBuiltInSymbol ArcCos = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arccos" : "ArcCos");
	public final static IBuiltInSymbol ArcCosh = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arccosh" : "ArcCosh");
	public final static IBuiltInSymbol ArcCot = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arccot" : "ArcCot");
	public final static IBuiltInSymbol ArcCoth = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arccoth" : "ArcCoth");
	public final static IBuiltInSymbol ArcCsc = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arccsc" : "ArcCsc");
	public final static IBuiltInSymbol ArcCsch = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arccsch" : "ArcCsch");
	public final static IBuiltInSymbol ArcSec = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arcsec" : "ArcSec");
	public final static IBuiltInSymbol ArcSech = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arcsech" : "ArcSech");
	public final static IBuiltInSymbol ArcSin = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arcsin" : "ArcSin");
	public final static IBuiltInSymbol ArcSinh = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arcsinh" : "ArcSinh");
	public final static IBuiltInSymbol ArcTan = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arctan" : "ArcTan");
	public final static IBuiltInSymbol ArcTanh = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arctanh" : "ArcTanh");
	public final static IBuiltInSymbol Arg = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arg" : "Arg");
	public final static IBuiltInSymbol ArrayDepth = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "arraydepth" : "ArrayDepth");
	public final static IBuiltInSymbol BernoulliB = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "bernoullib" : "BernoulliB");
	public final static IBuiltInSymbol BesselI = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "besseli" : "BesselI");
	public final static IBuiltInSymbol BesselJ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "besselj" : "BesselJ");
	public final static IBuiltInSymbol BesselK = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "besselk" : "BesselK");
	public final static IBuiltInSymbol BesselY = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "bessely" : "BesselY");
	public final static IBuiltInSymbol Binomial = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "binomial" : "Binomial");
	public final static IBuiltInSymbol Boole = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "boole" : "Boole");
	public final static IBuiltInSymbol BooleanConvert = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "booleanconvert" : "BooleanConvert");
	public final static IBuiltInSymbol BooleanMinimize = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "booleanminimize" : "BooleanMinimize");
	public final static IBuiltInSymbol BooleanTable = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "booleantable" : "BooleanTable");
	public final static IBuiltInSymbol BooleanVariables = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "booleanvariables" : "BooleanVariables");
	public final static IBuiltInSymbol Cancel = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cancel" : "Cancel");
	public final static IBuiltInSymbol CarmichaelLambda = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "carmichaellambda" : "CarmichaelLambda");
	public final static IBuiltInSymbol CartesianProduct = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cartesianproduct" : "CartesianProduct");
	public final static IBuiltInSymbol CatalanNumber = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "catalannumber" : "CatalanNumber");
	public final static IBuiltInSymbol Catenate = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "catenate" : "Catenate");
	public final static IBuiltInSymbol CDF = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cdf" : "CDF");
	public final static IBuiltInSymbol Ceiling = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "ceiling" : "Ceiling");
	public final static IBuiltInSymbol CentralMoment = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "centralmoment" : "CentralMoment");
	public final static IBuiltInSymbol CharacteristicPolynomial = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "characteristicpolynomial" : "CharacteristicPolynomial");
	public final static IBuiltInSymbol ChebyshevT = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "chebyshevt" : "ChebyshevT");
	public final static IBuiltInSymbol ChebyshevU = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "chebyshevu" : "ChebyshevU");
	public final static IBuiltInSymbol ChessboardDistance = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "chessboarddistance" : "ChessboardDistance");
	public final static IBuiltInSymbol ChineseRemainder = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "chineseremainder" : "ChineseRemainder");
	public final static IBuiltInSymbol Coefficient = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "coefficient" : "Coefficient");
	public final static IBuiltInSymbol CoefficientList = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "coefficientlist" : "CoefficientList");
	public final static IBuiltInSymbol CoefficientRules = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "coefficientrules" : "CoefficientRules");
	public final static IBuiltInSymbol Commonest = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "commonest" : "Commonest");
	public final static IBuiltInSymbol Complement = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "complement" : "Complement");
	public final static IBuiltInSymbol ComplexExpand = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "complexexpand" : "ComplexExpand");
	public final static IBuiltInSymbol ComposeList = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "composelist" : "ComposeList");
	public final static IBuiltInSymbol Conjugate = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "conjugate" : "Conjugate");
	public final static IBuiltInSymbol ConjugateTranspose = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "conjugatetranspose" : "ConjugateTranspose");
	public final static IBuiltInSymbol ConstantArray = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "constantarray" : "ConstantArray");
	public final static IBuiltInSymbol ContinuedFraction = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "continuedfraction" : "ContinuedFraction");
	public final static IBuiltInSymbol CoprimeQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "coprimeq" : "CoprimeQ");
	public final static IBuiltInSymbol Cos = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cos" : "Cos");
	public final static IBuiltInSymbol Cosh = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cosh" : "Cosh");
	public final static IBuiltInSymbol CosineDistance = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cosinedistance" : "CosineDistance");
	public final static IBuiltInSymbol CosIntegral = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cosintegral" : "CosIntegral");
	public final static IBuiltInSymbol Cot = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cot" : "Cot");
	public final static IBuiltInSymbol Coth = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "coth" : "Coth");
	public final static IBuiltInSymbol Covariance = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "covariance" : "Covariance");
	public final static IBuiltInSymbol Cross = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "cross" : "Cross");
	public final static IBuiltInSymbol Csc = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "csc" : "Csc");
	public final static IBuiltInSymbol Csch = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "csch" : "Csch");
	public final static IBuiltInSymbol Curl = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "curl" : "Curl");
	public final static IBuiltInSymbol D = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "D" : "D");
	public final static IBuiltInSymbol Decrement = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "decrement" : "Decrement");
	public final static IBuiltInSymbol Default = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "default" : "Default");
	public final static IBuiltInSymbol DeleteDuplicates = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "deleteduplicates" : "DeleteDuplicates");
	public final static IBuiltInSymbol Denominator = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "denominator" : "Denominator");
	public final static IBuiltInSymbol Derivative = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "derivative" : "Derivative");
	public final static IBuiltInSymbol DesignMatrix = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "designmatrix" : "DesignMatrix");
	public final static IBuiltInSymbol Det = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "det" : "Det");
	public final static IBuiltInSymbol DiagonalMatrix = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "diagonalmatrix" : "DiagonalMatrix");
	public final static IBuiltInSymbol DiceDissimilarity = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "dicedissimilarity" : "DiceDissimilarity");
	public final static IBuiltInSymbol Dimensions = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "dimensions" : "Dimensions");
	public final static IBuiltInSymbol DiracDelta = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "diracdelta" : "DiracDelta");
	public final static IBuiltInSymbol Discriminant = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "discriminant" : "Discriminant");
	public final static IBuiltInSymbol Distribute = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "distribute" : "Distribute");
	public final static IBuiltInSymbol Divergence = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "divergence" : "Divergence");
	public final static IBuiltInSymbol DivideBy = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "divideby" : "DivideBy");
	public final static IBuiltInSymbol Divisible = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "divisible" : "Divisible");
	public final static IBuiltInSymbol Divisors = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "divisors" : "Divisors");
	public final static IBuiltInSymbol Dot = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "dot" : "Dot");
	public final static IBuiltInSymbol DSolve = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "dsolve" : "DSolve");
	public final static IBuiltInSymbol EasterSunday = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "eastersunday" : "EasterSunday");
	public final static IBuiltInSymbol Eigenvalues = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "eigenvalues" : "Eigenvalues");
	public final static IBuiltInSymbol Eigenvectors = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "eigenvectors" : "Eigenvectors");
	public final static IBuiltInSymbol ElementData = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "elementdata" : "ElementData");
	public final static IBuiltInSymbol Eliminate = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "eliminate" : "Eliminate");
	public final static IBuiltInSymbol Equal = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "equal" : "Equal");
	public final static IBuiltInSymbol Equivalent = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "equivalent" : "Equivalent");
	public final static IBuiltInSymbol Erf = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "erf" : "Erf");
	public final static IBuiltInSymbol EuclidianDistance = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "euclidiandistance" : "EuclidianDistance");
	public final static IBuiltInSymbol EulerE = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "eulere" : "EulerE");
	public final static IBuiltInSymbol EulerPhi = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "eulerphi" : "EulerPhi");
	public final static IBuiltInSymbol Exp = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "exp" : "Exp");
	public final static IBuiltInSymbol Expand = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "expand" : "Expand");
	public final static IBuiltInSymbol ExpandAll = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "expandall" : "ExpandAll");
	public final static IBuiltInSymbol Export = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "export" : "Export");
	public final static IBuiltInSymbol ExtendedGCD = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "extendedgcd" : "ExtendedGCD");
	public final static IBuiltInSymbol Extract = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "extract" : "Extract");
	public final static IBuiltInSymbol Factor = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "factor" : "Factor");
	public final static IBuiltInSymbol Factorial = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "factorial" : "Factorial");
	public final static IBuiltInSymbol Factorial2 = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "factorial2" : "Factorial2");
	public final static IBuiltInSymbol FactorInteger = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "factorinteger" : "FactorInteger");
	public final static IBuiltInSymbol FactorSquareFree = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "factorsquarefree" : "FactorSquareFree");
	public final static IBuiltInSymbol FactorSquareFreeList = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "factorsquarefreelist" : "FactorSquareFreeList");
	public final static IBuiltInSymbol FactorTerms = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "factorterms" : "FactorTerms");
	public final static IBuiltInSymbol Fibonacci = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fibonacci" : "Fibonacci");
	public final static IBuiltInSymbol FindInstance = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "findinstance" : "FindInstance");
	public final static IBuiltInSymbol FindRoot = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "findroot" : "FindRoot");
	public final static IBuiltInSymbol Fit = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fit" : "Fit");
	public final static IBuiltInSymbol Floor = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "floor" : "Floor");
	public final static IBuiltInSymbol FractionalPart = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fractionalpart" : "FractionalPart");
	public final static IBuiltInSymbol FresnelC = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fresnelc" : "FresnelC");
	public final static IBuiltInSymbol FresnelS = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fresnels" : "FresnelS");
	public final static IBuiltInSymbol FrobeniusSolve = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "frobeniussolve" : "FrobeniusSolve");
	public final static IBuiltInSymbol FromCharacterCode = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fromcharactercode" : "FromCharacterCode");
	public final static IBuiltInSymbol FromContinuedFraction = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fromcontinuedfraction" : "FromContinuedFraction");
	public final static IBuiltInSymbol FromPolarCoordinates = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "frompolarcoordinates" : "FromPolarCoordinates");
	public final static IBuiltInSymbol FullSimplify = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "fullsimplify" : "FullSimplify");
	public final static IBuiltInSymbol Gamma = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "gamma" : "Gamma");
	public final static IBuiltInSymbol Gather = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "gather" : "Gather");
	public final static IBuiltInSymbol GCD = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "gcd" : "GCD");
	public final static IBuiltInSymbol GeometricMean = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "geometricmean" : "GeometricMean");
	public final static IBuiltInSymbol Greater = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "greater" : "Greater");
	public final static IBuiltInSymbol GreaterEqual = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "greaterequal" : "GreaterEqual");
	public final static IBuiltInSymbol GroebnerBasis = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "groebnerbasis" : "GroebnerBasis");
	public final static IBuiltInSymbol HarmonicNumber = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "harmonicnumber" : "HarmonicNumber");
	public final static IBuiltInSymbol Haversine = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "haversine" : "Haversine");
	public final static IBuiltInSymbol HeavisideTheta = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "heavisidetheta" : "HeavisideTheta");
	public final static IBuiltInSymbol HermiteH = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "hermiteh" : "HermiteH");
	public final static IBuiltInSymbol HilbertMatrix = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "hilbertmatrix" : "HilbertMatrix");
	public final static IBuiltInSymbol Horner = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "horner" : "Horner");
	public final static IBuiltInSymbol HornerForm = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "hornerform" : "HornerForm");
	public final static IBuiltInSymbol Hypergeometric1F1 = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "hypergeometric1f1" : "Hypergeometric1F1");
	public final static IBuiltInSymbol IdentityMatrix = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "identitymatrix" : "IdentityMatrix");
	public final static IBuiltInSymbol Im = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "im" : "Im");
	public final static IBuiltInSymbol Import = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "import" : "Import");
	public final static IBuiltInSymbol Increment = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "increment" : "Increment");
	public final static IBuiltInSymbol Inner = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "inner" : "Inner");
	public final static IBuiltInSymbol IntegerExponent = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "integerexponent" : "IntegerExponent");
	public final static IBuiltInSymbol IntegerPart = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "integerpart" : "IntegerPart");
	public final static IBuiltInSymbol IntegerPartitions = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "integerpartitions" : "IntegerPartitions");
	public final static IBuiltInSymbol Integrate = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "integrate" : "Integrate");
	public final static IBuiltInSymbol InterpolatingFunction = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "interpolatingfunction" : "InterpolatingFunction");
	public final static IBuiltInSymbol InterpolatingPolynomial = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "interpolatingpolynomial" : "InterpolatingPolynomial");
	public final static IBuiltInSymbol Interpolation = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "interpolation" : "Interpolation");
	public final static IBuiltInSymbol Intersection = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "intersection" : "Intersection");
	public final static IBuiltInSymbol Interval = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "interval" : "Interval");
	public final static IBuiltInSymbol Inverse = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "inverse" : "Inverse");
	public final static IBuiltInSymbol InverseErf = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "inverseerf" : "InverseErf");
	public final static IBuiltInSymbol InverseErfc = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "inverseerfc" : "InverseErfc");
	public final static IBuiltInSymbol InverseFunction = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "inversefunction" : "InverseFunction");
	public final static IBuiltInSymbol InverseHaversine = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "inversehaversine" : "InverseHaversine");
	public final static IBuiltInSymbol JaccardDissimilarity = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "jaccarddissimilarity" : "JaccardDissimilarity");
	public final static IBuiltInSymbol JacobiMatrix = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "jacobimatrix" : "JacobiMatrix");
	public final static IBuiltInSymbol JacobiSymbol = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "jacobisymbol" : "JacobiSymbol");
	public final static IBuiltInSymbol Join = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "join" : "Join");
	public final static IBuiltInSymbol KOrderlessPartitions = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "korderlesspartitions" : "KOrderlessPartitions");
	public final static IBuiltInSymbol KPartitions = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "kpartitions" : "KPartitions");
	public final static IBuiltInSymbol Kurtosis = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "kurtosis" : "Kurtosis");
	public final static IBuiltInSymbol LaguerreL = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "laguerrel" : "LaguerreL");
	public final static IBuiltInSymbol LaplaceTransform = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "laplacetransform" : "LaplaceTransform");
	public final static IBuiltInSymbol LCM = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "lcm" : "LCM");
	public final static IBuiltInSymbol LegendreP = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "legendrep" : "LegendreP");
	public final static IBuiltInSymbol Less = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "less" : "Less");
	public final static IBuiltInSymbol LessEqual = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "lessequal" : "LessEqual");
	public final static IBuiltInSymbol LetterQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "letterq" : "LetterQ");
	public final static IBuiltInSymbol Level = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "level" : "Level");
	public final static IBuiltInSymbol Limit = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "limit" : "Limit");
	public final static IBuiltInSymbol LinearProgramming = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "linearprogramming" : "LinearProgramming");
	public final static IBuiltInSymbol LinearSolve = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "linearsolve" : "LinearSolve");
	public final static IBuiltInSymbol Log = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "log" : "Log");
	public final static IBuiltInSymbol Log10 = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "log10" : "Log10");
	public final static IBuiltInSymbol Log2 = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "log2" : "Log2");
	public final static IBuiltInSymbol LogisticSigmoid = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "logisticsigmoid" : "LogisticSigmoid");
	public final static IBuiltInSymbol LowerCaseQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "lowercaseq" : "LowerCaseQ");
	public final static IBuiltInSymbol LUDecomposition = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "ludecomposition" : "LUDecomposition");
	public final static IBuiltInSymbol ManhattanDistance = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "manhattandistance" : "ManhattanDistance");
	public final static IBuiltInSymbol Map = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "map" : "Map");
	public final static IBuiltInSymbol MapAll = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "mapall" : "MapAll");
	public final static IBuiltInSymbol MapAt = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "mapat" : "MapAt");
	public final static IBuiltInSymbol MapThread = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "mapthread" : "MapThread");
	public final static IBuiltInSymbol MatchingDissimilarity = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "matchingdissimilarity" : "MatchingDissimilarity");
	public final static IBuiltInSymbol MatrixPower = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "matrixpower" : "MatrixPower");
	public final static IBuiltInSymbol MatrixRank = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "matrixrank" : "MatrixRank");
	public final static IBuiltInSymbol Max = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "max" : "Max");
	public final static IBuiltInSymbol Mean = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "mean" : "Mean");
	public final static IBuiltInSymbol Median = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "median" : "Median");
	public final static IBuiltInSymbol Min = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "min" : "Min");
	public final static IBuiltInSymbol Mod = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "mod" : "Mod");
	public final static IBuiltInSymbol MoebiusMu = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "moebiusmu" : "MoebiusMu");
	public final static IBuiltInSymbol MonomialList = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "monomiallist" : "MonomialList");
	public final static IBuiltInSymbol Most = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "most" : "Most");
	public final static IBuiltInSymbol Multinomial = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "multinomial" : "Multinomial");
	public final static IBuiltInSymbol MultiplicativeOrder = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "multiplicativeorder" : "MultiplicativeOrder");
	public final static IBuiltInSymbol Names = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "names" : "Names");
	public final static IBuiltInSymbol NDSolve = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "ndsolve" : "NDSolve");
	public final static IBuiltInSymbol Nearest = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nearest" : "Nearest");
	public final static IBuiltInSymbol Negative = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "negative" : "Negative");
	public final static IBuiltInSymbol NextPrime = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nextprime" : "NextPrime");
	public final static IBuiltInSymbol NFourierTransform = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nfouriertransform" : "NFourierTransform");
	public final static IBuiltInSymbol NIntegrate = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nintegrate" : "NIntegrate");
	public final static IBuiltInSymbol NMaximize = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nmaximize" : "NMaximize");
	public final static IBuiltInSymbol NMinimize = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nminimize" : "NMinimize");
	public final static IBuiltInSymbol NonCommutativeMultiply = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "noncommutativemultiply" : "NonCommutativeMultiply");
	public final static IBuiltInSymbol NoneTrue = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nonetrue" : "NoneTrue");
	public final static IBuiltInSymbol NonNegative = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nonnegative" : "NonNegative");
	public final static IBuiltInSymbol Norm = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "norm" : "Norm");
	public final static IBuiltInSymbol Normal = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "normal" : "Normal");
	public final static IBuiltInSymbol Normalize = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "normalize" : "Normalize");
	public final static IBuiltInSymbol Not = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "not" : "Not");
	public final static IBuiltInSymbol NRoots = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nroots" : "NRoots");
	public final static IBuiltInSymbol NSolve = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nsolve" : "NSolve");
	public final static IBuiltInSymbol NullSpace = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "nullspace" : "NullSpace");
	public final static IBuiltInSymbol Numerator = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "numerator" : "Numerator");
	public final static IBuiltInSymbol Order = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "order" : "Order");
	public final static IBuiltInSymbol OrderedQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "orderedq" : "OrderedQ");
	public final static IBuiltInSymbol Out = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "out" : "Out");
	public final static IBuiltInSymbol Outer = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "outer" : "Outer");
	public final static IBuiltInSymbol PadLeft = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "padleft" : "PadLeft");
	public final static IBuiltInSymbol PadRight = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "padright" : "PadRight");
	public final static IBuiltInSymbol Partition = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "partition" : "Partition");
	public final static IBuiltInSymbol PDF = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "pdf" : "PDF");
	public final static IBuiltInSymbol Permutations = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "permutations" : "Permutations");
	public final static IBuiltInSymbol Piecewise = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "piecewise" : "Piecewise");
	public final static IBuiltInSymbol Plot = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "plot" : "Plot");
	public final static IBuiltInSymbol Plot3D = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "plot3d" : "Plot3D");
	public final static IBuiltInSymbol Plus = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "plus" : "Plus");
	public final static IBuiltInSymbol Pochhammer = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "pochhammer" : "Pochhammer");
	public final static IBuiltInSymbol PolynomialExtendedGCD = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polynomialextendedgcd" : "PolynomialExtendedGCD");
	public final static IBuiltInSymbol PolynomialGCD = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polynomialgcd" : "PolynomialGCD");
	public final static IBuiltInSymbol PolynomialLCM = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polynomiallcm" : "PolynomialLCM");
	public final static IBuiltInSymbol PolynomialQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polynomialq" : "PolynomialQ");
	public final static IBuiltInSymbol PolynomialQuotient = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polynomialquotient" : "PolynomialQuotient");
	public final static IBuiltInSymbol PolynomialQuotientRemainder = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polynomialquotientremainder" : "PolynomialQuotientRemainder");
	public final static IBuiltInSymbol PolynomialRemainder = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polynomialremainder" : "PolynomialRemainder");
	public final static IBuiltInSymbol Positive = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "positive" : "Positive");
	public final static IBuiltInSymbol Power = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "power" : "Power");
	public final static IBuiltInSymbol PowerExpand = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "powerexpand" : "PowerExpand");
	public final static IBuiltInSymbol PowerMod = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "powermod" : "PowerMod");
	public final static IBuiltInSymbol PreDecrement = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "predecrement" : "PreDecrement");
	public final static IBuiltInSymbol PreIncrement = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "preincrement" : "PreIncrement");
	public final static IBuiltInSymbol Prime = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "prime" : "Prime");
	public final static IBuiltInSymbol PrimitiveRoots = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "primitiveroots" : "PrimitiveRoots");
	public final static IBuiltInSymbol Product = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "product" : "Product");
	public final static IBuiltInSymbol ProductLog = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "productlog" : "ProductLog");
	public final static IBuiltInSymbol PseudoInverse = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "pseudoinverse" : "PseudoInverse");
	public final static IBuiltInSymbol Quotient = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "quotient" : "Quotient");
	public final static IBuiltInSymbol RandomInteger = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "randominteger" : "RandomInteger");
	public final static IBuiltInSymbol RandomReal = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "randomreal" : "RandomReal");
	public final static IBuiltInSymbol RandomSample = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "randomsample" : "RandomSample");
	public final static IBuiltInSymbol Range = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "range" : "Range");
	public final static IBuiltInSymbol Rationalize = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rationalize" : "Rationalize");
	public final static IBuiltInSymbol Re = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "re" : "Re");
	public final static IBuiltInSymbol Replace = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "replace" : "Replace");
	public final static IBuiltInSymbol ReplaceAll = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "replaceall" : "ReplaceAll");
	public final static IBuiltInSymbol ReplaceList = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "replacelist" : "ReplaceList");
	public final static IBuiltInSymbol ReplacePart = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "replacepart" : "ReplacePart");
	public final static IBuiltInSymbol ReplaceRepeated = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "replacerepeated" : "ReplaceRepeated");
	public final static IBuiltInSymbol Resultant = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "resultant" : "Resultant");
	public final static IBuiltInSymbol Reverse = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "reverse" : "Reverse");
	public final static IBuiltInSymbol RogersTanimotoDissimilarity = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rogerstanimotodissimilarity" : "RogersTanimotoDissimilarity");
	public final static IBuiltInSymbol Root = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "root" : "Root");
	public final static IBuiltInSymbol RootIntervals = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rootintervals" : "RootIntervals");
	public final static IBuiltInSymbol Roots = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "roots" : "Roots");
	public final static IBuiltInSymbol Round = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "round" : "Round");
	public final static IBuiltInSymbol RowReduce = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "rowreduce" : "RowReduce");
	public final static IBuiltInSymbol RussellRaoDissimilarity = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "russellraodissimilarity" : "RussellRaoDissimilarity");
	public final static IBuiltInSymbol SameQ = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sameq" : "SameQ");
	public final static IBuiltInSymbol SatisfiableQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "satisfiableq" : "SatisfiableQ");
	public final static IBuiltInSymbol Scan = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "scan" : "Scan");
	public final static IBuiltInSymbol Sec = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sec" : "Sec");
	public final static IBuiltInSymbol Sech = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sech" : "Sech");
	public final static IBuiltInSymbol Select = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "select" : "Select");
	public final static IBuiltInSymbol Series = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "series" : "Series");
	public final static IBuiltInSymbol SeriesData = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "seriesdata" : "SeriesData");
	public final static IBuiltInSymbol Share = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "share" : "Share");
	public final static IBuiltInSymbol Sign = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sign" : "Sign");
	public final static IBuiltInSymbol SignCmp = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "signcmp" : "SignCmp");
	public final static IBuiltInSymbol Simplify = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "simplify" : "Simplify");
	public final static IBuiltInSymbol Sin = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sin" : "Sin");
	public final static IBuiltInSymbol Sinc = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sinc" : "Sinc");
	public final static IBuiltInSymbol SingularValueDecomposition = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "singularvaluedecomposition" : "SingularValueDecomposition");
	public final static IBuiltInSymbol Sinh = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sinh" : "Sinh");
	public final static IBuiltInSymbol SinIntegral = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sinintegral" : "SinIntegral");
	public final static IBuiltInSymbol Skewness = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "skewness" : "Skewness");
	public final static IBuiltInSymbol SokalSneathDissimilarity = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sokalsneathdissimilarity" : "SokalSneathDissimilarity");
	public final static IBuiltInSymbol Solve = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "solve" : "Solve");
	public final static IBuiltInSymbol Sort = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sort" : "Sort");
	public final static IBuiltInSymbol Sqrt = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sqrt" : "Sqrt");
	public final static IBuiltInSymbol SquaredEuclidianDistance = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "squaredeuclidiandistance" : "SquaredEuclidianDistance");
	public final static IBuiltInSymbol SquareFreeQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "squarefreeq" : "SquareFreeQ");
	public final static IBuiltInSymbol StirlingS1 = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "stirlings1" : "StirlingS1");
	public final static IBuiltInSymbol StirlingS2 = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "stirlings2" : "StirlingS2");
	public final static IBuiltInSymbol StringDrop = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "stringdrop" : "StringDrop");
	public final static IBuiltInSymbol StringJoin = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "stringjoin" : "StringJoin");
	public final static IBuiltInSymbol StringLength = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "stringlength" : "StringLength");
	public final static IBuiltInSymbol StringTake = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "stringtake" : "StringTake");
	public final static IBuiltInSymbol StruveH = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "struveh" : "StruveH");
	public final static IBuiltInSymbol StruveL = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "struvel" : "StruveL");
	public final static IBuiltInSymbol Subfactorial = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "subfactorial" : "Subfactorial");
	public final static IBuiltInSymbol Subsets = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "subsets" : "Subsets");
	public final static IBuiltInSymbol SubtractFrom = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "subtractfrom" : "SubtractFrom");
	public final static IBuiltInSymbol Sum = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sum" : "Sum");
	public final static IBuiltInSymbol Surd = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "surd" : "Surd");
	public final static IBuiltInSymbol SyntaxLength = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "syntaxlength" : "SyntaxLength");
	public final static IBuiltInSymbol Table = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "table" : "Table");
	public final static IBuiltInSymbol Tally = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tally" : "Tally");
	public final static IBuiltInSymbol Tan = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tan" : "Tan");
	public final static IBuiltInSymbol Tanh = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tanh" : "Tanh");
	public final static IBuiltInSymbol TautologyQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tautologyq" : "TautologyQ");
	public final static IBuiltInSymbol Taylor = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "taylor" : "Taylor");
	public final static IBuiltInSymbol Thread = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "thread" : "Thread");
	public final static IBuiltInSymbol Through = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "through" : "Through");
	public final static IBuiltInSymbol Times = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "times" : "Times");
	public final static IBuiltInSymbol TimesBy = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "timesby" : "TimesBy");
	public final static IBuiltInSymbol ToCharacterCode = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tocharactercode" : "ToCharacterCode");
	public final static IBuiltInSymbol Together = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "together" : "Together");
	public final static IBuiltInSymbol ToPolarCoordinates = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "topolarcoordinates" : "ToPolarCoordinates");
	public final static IBuiltInSymbol ToString = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tostring" : "ToString");
	public final static IBuiltInSymbol Total = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "total" : "Total");
	public final static IBuiltInSymbol ToUnicode = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tounicode" : "ToUnicode");
	public final static IBuiltInSymbol Tr = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tr" : "Tr");
	public final static IBuiltInSymbol Transpose = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "transpose" : "Transpose");
	public final static IBuiltInSymbol TrigExpand = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "trigexpand" : "TrigExpand");
	public final static IBuiltInSymbol TrigReduce = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "trigreduce" : "TrigReduce");
	public final static IBuiltInSymbol TrigToExp = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "trigtoexp" : "TrigToExp");
	public final static IBuiltInSymbol TrueQ = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "trueq" : "TrueQ");
	public final static IBuiltInSymbol Tuples = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "tuples" : "Tuples");
	public final static IBuiltInSymbol Unequal = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "unequal" : "Unequal");
	public final static IBuiltInSymbol Union = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "union" : "Union");
	public final static IBuiltInSymbol UnitStep = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "unitstep" : "UnitStep");
	public final static IBuiltInSymbol UnitVector = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "unitvector" : "UnitVector");
	public final static IBuiltInSymbol UnsameQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "unsameq" : "UnsameQ");
	public final static IBuiltInSymbol VandermondeMatrix = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "vandermondematrix" : "VandermondeMatrix");
	public final static IBuiltInSymbol Variables = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "variables" : "Variables");
	public final static IBuiltInSymbol Variance = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "variance" : "Variance");
	public final static IBuiltInSymbol VectorAngle = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "vectorangle" : "VectorAngle");
	public final static IBuiltInSymbol Xor = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "xor" : "Xor");
	public final static IBuiltInSymbol YuleDissimilarity = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "yuledissimilarity" : "YuleDissimilarity");
	public final static IBuiltInSymbol Zeta = initFinalSymbol(Config.PARSER_USE_LOWERCASE_SYMBOLS ? "zeta" : "Zeta");

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

	public final static IPattern a_Symbol = new Pattern(a, SymbolHead);
	public final static IPattern b_Symbol = new Pattern(b, SymbolHead);
	public final static IPattern c_Symbol = new Pattern(c, SymbolHead);
	public final static IPattern d_Symbol = new Pattern(d, SymbolHead);
	public final static IPattern e_Symbol = new Pattern(e, SymbolHead);
	public final static IPattern f_Symbol = new Pattern(f, SymbolHead);
	public final static IPattern g_Symbol = new Pattern(g, SymbolHead);
	public final static IPattern h_Symbol = new Pattern(h, SymbolHead);
	public final static IPattern i_Symbol = new Pattern(i, SymbolHead);
	public final static IPattern j_Symbol = new Pattern(j, SymbolHead);
	public final static IPattern k_Symbol = new Pattern(k, SymbolHead);
	public final static IPattern l_Symbol = new Pattern(l, SymbolHead);
	public final static IPattern m_Symbol = new Pattern(m, SymbolHead);
	public final static IPattern n_Symbol = new Pattern(n, SymbolHead);
	public final static IPattern o_Symbol = new Pattern(o, SymbolHead);
	public final static IPattern p_Symbol = new Pattern(p, SymbolHead);
	public final static IPattern q_Symbol = new Pattern(q, SymbolHead);
	public final static IPattern r_Symbol = new Pattern(r, SymbolHead);
	public final static IPattern s_Symbol = new Pattern(s, SymbolHead);
	public final static IPattern t_Symbol = new Pattern(t, SymbolHead);
	public final static IPattern u_Symbol = new Pattern(u, SymbolHead);
	public final static IPattern v_Symbol = new Pattern(v, SymbolHead);
	public final static IPattern w_Symbol = new Pattern(w, SymbolHead);
	public final static IPattern x_Symbol = new Pattern(x, SymbolHead);
	public final static IPattern y_Symbol = new Pattern(y, SymbolHead);
	public final static IPattern z_Symbol = new Pattern(z, SymbolHead);

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
	public final static IntegerSym C0 = new IntegerSym(0);

	/**
	 * Constant integer &quot;1&quot;
	 */
	public final static IntegerSym C1 = new IntegerSym(1);

	/**
	 * Constant integer &quot;2&quot;
	 */
	public final static IntegerSym C2 = new IntegerSym(2);

	/**
	 * Constant integer &quot;3&quot;
	 */
	public final static IntegerSym C3 = new IntegerSym(3);

	/**
	 * Constant integer &quot;4&quot;
	 */
	public final static IntegerSym C4 = new IntegerSym(4);

	/**
	 * Constant integer &quot;5&quot;
	 */
	public final static IntegerSym C5 = new IntegerSym(5);

	/**
	 * Constant integer &quot;6&quot;
	 */
	public final static IntegerSym C6 = new IntegerSym(6);

	/**
	 * Constant integer &quot;7&quot;
	 */
	public final static IntegerSym C7 = new IntegerSym(7);

	/**
	 * Constant integer &quot;8&quot;
	 */
	public final static IntegerSym C8 = new IntegerSym(8);

	/**
	 * Constant integer &quot;9&quot;
	 */
	public final static IntegerSym C9 = new IntegerSym(9);

	/**
	 * Constant integer &quot;10&quot;
	 */
	public final static IntegerSym C10 = new IntegerSym(10);

	/**
	 * Complex imaginary unit. The parsed symbol &quot;I&quot; is converted on
	 * input to this constant.
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
	public final static INum CND1 = Num.valueOf(-1.0);

	/**
	 * Constant double &quot;0.0&quot;
	 */
	public final static INum CD0 = Num.valueOf(0.0);

	/**
	 * Constant double &quot;1.0&quot;
	 */
	public final static INum CD1 = Num.valueOf(1.0);

	/**
	 * Represents <code>Infinity</code> (i.e.
	 * <code>Infinity-&gt;DirectedInfinity(1)</code>)
	 */
	public static IAST CInfinity;

	/**
	 * Alias for CInfinity. Represents <code>Infinity</code> (i.e.
	 * <code>Infinity-&gt;DirectedInfinity(1)</code>)
	 */
	public static IAST oo;

	/**
	 * Represents <code>-Infinity</code> (i.e.
	 * <code>-Infinity-&gt;DirectedInfinity(-1)</code>)
	 */
	public static IAST CNInfinity;

	/**
	 * Alias for CNInfinity. Represents <code>-Infinity</code> (i.e.
	 * <code>-Infinity-&gt;DirectedInfinity(-1)</code>)
	 */
	public static IAST Noo;

	/**
	 * Represents <code>I*Infinity</code> (i.e.
	 * <code>I*Infinity-&gt;DirectedInfinity(I)</code>)
	 */
	public static IAST CIInfinity;

	/**
	 * Represents <code>-I*Infinity</code> (i.e.
	 * <code>-I*Infinity-&gt;DirectedInfinity(-I)</code>)
	 */
	public static IAST CNIInfinity;

	/**
	 * Represents <code>ComplexInfinity</code> (i.e.
	 * <code>ComplexInfinity-&gt;DirectedInfinity()</code>)
	 */
	public static IAST CComplexInfinity;

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
	public final static IntegerSym CN1 = new IntegerSym(-1);

	/**
	 * Constant integer &quot;-2&quot;
	 */
	public final static IntegerSym CN2 = new IntegerSym(-2);

	/**
	 * Constant integer &quot;-3&quot;
	 */
	public final static IntegerSym CN3 = new IntegerSym(-3);

	/**
	 * Constant integer &quot;-4&quot;
	 */
	public final static IntegerSym CN4 = new IntegerSym(-4);

	/**
	 * Constant integer &quot;-5&quot;
	 */
	public final static IntegerSym CN5 = new IntegerSym(-5);

	/**
	 * Constant integer &quot;-6&quot;
	 */
	public final static IntegerSym CN6 = new IntegerSym(-6);

	/**
	 * Constant integer &quot;-7&quot;
	 */
	public final static IntegerSym CN7 = new IntegerSym(-7);

	/**
	 * Constant integer &quot;-8&quot;
	 */
	public final static IntegerSym CN8 = new IntegerSym(-8);

	/**
	 * Constant integer &quot;-9&quot;
	 */
	public final static IntegerSym CN9 = new IntegerSym(-9);

	/**
	 * Constant integer &quot;-10&quot;
	 */
	public final static IntegerSym CN10 = new IntegerSym(-10);

	public final static IBuiltInSymbol AppellF1 = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "appellf1" : "AppellF1");

	public final static IBuiltInSymbol EllipticE = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "elliptice" : "EllipticE");

	public final static IBuiltInSymbol EllipticF = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "ellipticf" : "EllipticF");

	public final static IBuiltInSymbol EllipticPi = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "ellipticpi" : "EllipticPi");

	public final static IBuiltInSymbol HypergeometricPFQ = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "hypergeometricpfq" : "HypergeometricPFQ");

	public final static IBuiltInSymbol Hypergeometric2F1 = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "hypergeometric2f1" : "Hypergeometric2F1");
	public final static IBuiltInSymbol LinearModelFit = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "linearmodelfit" : "LinearModelFit");
	public final static IBuiltInSymbol CoshIntegral = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "coshintegral" : "CoshIntegral");

	public final static IBuiltInSymbol SinhIntegral = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "sinhintegral" : "SinhIntegral");

	public final static IBuiltInSymbol ExpIntegralEi = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "expintegralei" : "ExpIntegralEi");

	public final static IBuiltInSymbol LogIntegral = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "logintegral" : "LogIntegral");

	public final static IBuiltInSymbol PolyLog = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polylog" : "PolyLog");

	public final static IBuiltInSymbol LogGamma = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "loggamma" : "LogGamma");

	public final static IBuiltInSymbol PolyGamma = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "polygamma" : "PolyGamma");

	public final static IBuiltInSymbol StieltjesGamma = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "stieltjesgamma" : "StieltjesGamma");

	public final static IBuiltInSymbol ExpIntegralE = initFinalSymbol(
			Config.PARSER_USE_LOWERCASE_SYMBOLS ? "expintegrale" : "ExpIntegralE");

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

	static {
		try {
			// if (Config.DEBUG) {
			// System.out.println("Config.DEBUG == true");
			// }
			// if (Config.SHOW_STACKTRACE) {
			// System.out.println("Config.SHOW_STACKTRACE == true");
			// }
			// System.out.println("F#static()");

			ComputerThreads.NO_THREADS = Config.JAS_NO_THREADS;

			ApfloatContext ctx = ApfloatContext.getContext();
			ctx.setNumberOfProcessors(1);
			// long start = System.currentTimeMillis();

			ConstantDefinitions.initialize();
			
			Slot.setAttributes(ISymbol.NHOLDALL);
			SlotSequence.setAttributes(ISymbol.NHOLDALL);

			CInfinity = unaryAST1(DirectedInfinity, C1);
			oo = CInfinity;
			CNInfinity = unaryAST1(DirectedInfinity, CN1);
			Noo = CNInfinity;
			CIInfinity = unaryAST1(DirectedInfinity, CI);
			CNIInfinity = unaryAST1(DirectedInfinity, CNI);
			CComplexInfinity = headAST0(DirectedInfinity);

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
					Glaisher, GoldenRatio, HoldAll, HoldFirst, HoldForm, HoldRest, Indeterminate, Infinity, IntegerHead,
					Integers, Khinchin, Listable, Modulus, Null, NumericFunction, OneIdentity, Orderless, Pi, Primes,
					Rationals, RealHead, Reals, Slot, SlotSequence, StringHead, SymbolHead, True,
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
					EllipticE, EllipticF, EllipticPi, Equal, Equivalent, Erf, Erfc, Erfi, EuclidianDistance,
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
					NumberQ, Numerator, NumericQ, OddQ, Options, Or, Order, OrderedQ, Out, Outer, Package, PadLeft,
					PadRight,
					// ParametricPlot,
					Part, Partition, Pattern, Permutations, Piecewise, Plot, Plot3D, Plus,
					// Pochhammer,
					PolyGamma, PolyLog, PolynomialExtendedGCD, PolynomialGCD, PolynomialLCM, PolynomialQ,
					PolynomialQuotient, PolynomialQuotientRemainder, PolynomialRemainder, Position, Positive,
					PossibleZeroQ, Power, PowerExpand, PowerMod, PreDecrement, PreIncrement, Prepend, PrependTo,
					// Prime,
					PrimeQ, PrimitiveRoots, Print, Product, ProductLog, Quiet, Quotient, RandomInteger, RandomReal,
					// RandomSample,
					Range, Rational, Rationalize, Re, Reap, Refine, ReplaceAll, ReplacePart, ReplaceRepeated, Rest,
					Resultant, Return, Reverse, Riffle, RootIntervals, RootOf, Roots, Surd, RotateLeft, RotateRight,
					Round,
					// RowReduce,
					Rule, RuleDelayed, SameQ, Scan, Sec, Sech, Select, Sequence, Set, SetAttributes, SetDelayed, Show,
					Sign, SignCmp, Simplify, Sin, Sinc, SingularValueDecomposition, Sinh, SinIntegral, SinhIntegral,
					Solve, Sort, Sow, Sqrt, SquaredEuclidianDistance, SquareFreeQ, StirlingS2, StringDrop, StringJoin,
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

			Plus.setDefaultValue(C0);
			((IBuiltInSymbol) Plus).setEvaluator(org.matheclipse.core.reflection.system.Plus.CONST);
			Times.setDefaultValue(C1);
			((IBuiltInSymbol) Times).setEvaluator(org.matheclipse.core.reflection.system.Times.CONST);
			Power.setDefaultValue(2, C1);
			((IBuiltInSymbol) Power).setEvaluator(org.matheclipse.core.reflection.system.Power.CONST);
			((IBuiltInSymbol) Sqrt).setEvaluator(org.matheclipse.core.reflection.system.Sqrt.CONST);

			createInverseFunctionMap();
			createDenominatorFunctionMap();
			createNumeratorFunctionMap();

			setEvaluators();

			// initialize only the utility function rules for Integrate
			// other rules are "lazy loaded" on first use of Integrate
			// function
			final EvalEngine engine = EvalEngine.get();
			org.matheclipse.core.reflection.system.Integrate.getUtilityFunctionsRuleAST();
			// Integrate.setEvaluator(new
			// org.matheclipse.core.reflection.system.Integrate());
			// long end = System.currentTimeMillis();
			// System.out.println("Init time: " + (end - start));
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

	private static void setEvaluators() {

		((IBuiltInSymbol) Abs).setEvaluator(new org.matheclipse.core.reflection.system.Abs());
		((IBuiltInSymbol) AbsArg).setEvaluator(new org.matheclipse.core.reflection.system.AbsArg());
		((IBuiltInSymbol) AddTo).setEvaluator(new org.matheclipse.core.reflection.system.AddTo());
		((IBuiltInSymbol) AllTrue).setEvaluator(new org.matheclipse.core.reflection.system.AllTrue());
		((IBuiltInSymbol) AnyTrue).setEvaluator(new org.matheclipse.core.reflection.system.AnyTrue());
		((IBuiltInSymbol) Apart).setEvaluator(new org.matheclipse.core.reflection.system.Apart());
		((IBuiltInSymbol) ArcCos).setEvaluator(new org.matheclipse.core.reflection.system.ArcCos());
		((IBuiltInSymbol) ArcCosh).setEvaluator(new org.matheclipse.core.reflection.system.ArcCosh());
		((IBuiltInSymbol) ArcCot).setEvaluator(new org.matheclipse.core.reflection.system.ArcCot());
		((IBuiltInSymbol) ArcCoth).setEvaluator(new org.matheclipse.core.reflection.system.ArcCoth());
		((IBuiltInSymbol) ArcCsc).setEvaluator(new org.matheclipse.core.reflection.system.ArcCsc());
		((IBuiltInSymbol) ArcCsch).setEvaluator(new org.matheclipse.core.reflection.system.ArcCsch());
		((IBuiltInSymbol) ArcSec).setEvaluator(new org.matheclipse.core.reflection.system.ArcSec());
		((IBuiltInSymbol) ArcSech).setEvaluator(new org.matheclipse.core.reflection.system.ArcSech());
		((IBuiltInSymbol) ArcSin).setEvaluator(new org.matheclipse.core.reflection.system.ArcSin());
		((IBuiltInSymbol) ArcSinh).setEvaluator(new org.matheclipse.core.reflection.system.ArcSinh());
		((IBuiltInSymbol) ArcTan).setEvaluator(new org.matheclipse.core.reflection.system.ArcTan());
		((IBuiltInSymbol) ArcTanh).setEvaluator(new org.matheclipse.core.reflection.system.ArcTanh());
		((IBuiltInSymbol) Arg).setEvaluator(new org.matheclipse.core.reflection.system.Arg());
		((IBuiltInSymbol) ArrayDepth).setEvaluator(new org.matheclipse.core.reflection.system.ArrayDepth());
		((IBuiltInSymbol) BernoulliB).setEvaluator(new org.matheclipse.core.reflection.system.BernoulliB());
		((IBuiltInSymbol) BesselJ).setEvaluator(new org.matheclipse.core.reflection.system.BesselJ());
		((IBuiltInSymbol) Binomial).setEvaluator(new org.matheclipse.core.reflection.system.Binomial());
		((IBuiltInSymbol) Boole).setEvaluator(new org.matheclipse.core.reflection.system.Boole());
		((IBuiltInSymbol) BooleanConvert).setEvaluator(new org.matheclipse.core.reflection.system.BooleanConvert());
		((IBuiltInSymbol) BooleanMinimize).setEvaluator(new org.matheclipse.core.reflection.system.BooleanMinimize());
		((IBuiltInSymbol) BooleanTable).setEvaluator(new org.matheclipse.core.reflection.system.BooleanTable());
		((IBuiltInSymbol) BooleanVariables).setEvaluator(new org.matheclipse.core.reflection.system.BooleanVariables());
		((IBuiltInSymbol) Cancel).setEvaluator(new org.matheclipse.core.reflection.system.Cancel());
		((IBuiltInSymbol) CarmichaelLambda).setEvaluator(new org.matheclipse.core.reflection.system.CarmichaelLambda());
		((IBuiltInSymbol) CartesianProduct).setEvaluator(new org.matheclipse.core.reflection.system.CartesianProduct());
		((IBuiltInSymbol) CatalanNumber).setEvaluator(new org.matheclipse.core.reflection.system.CatalanNumber());
		((IBuiltInSymbol) Catenate).setEvaluator(new org.matheclipse.core.reflection.system.Catenate());
		((IBuiltInSymbol) CDF).setEvaluator(new org.matheclipse.core.reflection.system.CDF());
		((IBuiltInSymbol) Ceiling).setEvaluator(new org.matheclipse.core.reflection.system.Ceiling());
		((IBuiltInSymbol) CentralMoment).setEvaluator(new org.matheclipse.core.reflection.system.CentralMoment());
		((IBuiltInSymbol) CharacteristicPolynomial)
				.setEvaluator(new org.matheclipse.core.reflection.system.CharacteristicPolynomial());
		((IBuiltInSymbol) ChebyshevT).setEvaluator(new org.matheclipse.core.reflection.system.ChebyshevT());
		((IBuiltInSymbol) ChebyshevU).setEvaluator(new org.matheclipse.core.reflection.system.ChebyshevU());
		((IBuiltInSymbol) ChessboardDistance)
				.setEvaluator(new org.matheclipse.core.reflection.system.ChessboardDistance());
		((IBuiltInSymbol) ChineseRemainder).setEvaluator(new org.matheclipse.core.reflection.system.ChineseRemainder());
		((IBuiltInSymbol) Coefficient).setEvaluator(new org.matheclipse.core.reflection.system.Coefficient());
		((IBuiltInSymbol) CoefficientList).setEvaluator(new org.matheclipse.core.reflection.system.CoefficientList());
		((IBuiltInSymbol) CoefficientRules).setEvaluator(new org.matheclipse.core.reflection.system.CoefficientRules());
		((IBuiltInSymbol) Commonest).setEvaluator(new org.matheclipse.core.reflection.system.Commonest());
		((IBuiltInSymbol) Complement).setEvaluator(new org.matheclipse.core.reflection.system.Complement());
		((IBuiltInSymbol) ComplexExpand).setEvaluator(new org.matheclipse.core.reflection.system.ComplexExpand());
		((IBuiltInSymbol) ComposeList).setEvaluator(new org.matheclipse.core.reflection.system.ComposeList());
		((IBuiltInSymbol) Conjugate).setEvaluator(new org.matheclipse.core.reflection.system.Conjugate());
		((IBuiltInSymbol) ConjugateTranspose)
				.setEvaluator(new org.matheclipse.core.reflection.system.ConjugateTranspose());
		((IBuiltInSymbol) ConstantArray).setEvaluator(new org.matheclipse.core.reflection.system.ConstantArray());
		((IBuiltInSymbol) ContinuedFraction)
				.setEvaluator(new org.matheclipse.core.reflection.system.ContinuedFraction());
		((IBuiltInSymbol) CoprimeQ).setEvaluator(new org.matheclipse.core.reflection.system.CoprimeQ());
		((IBuiltInSymbol) Cos).setEvaluator(new org.matheclipse.core.reflection.system.Cos());
		((IBuiltInSymbol) Cosh).setEvaluator(new org.matheclipse.core.reflection.system.Cosh());
		((IBuiltInSymbol) CosineDistance).setEvaluator(new org.matheclipse.core.reflection.system.CosineDistance());
		((IBuiltInSymbol) CosIntegral).setEvaluator(new org.matheclipse.core.reflection.system.CosIntegral());
		((IBuiltInSymbol) Cot).setEvaluator(new org.matheclipse.core.reflection.system.Cot());
		((IBuiltInSymbol) Coth).setEvaluator(new org.matheclipse.core.reflection.system.Coth());
		((IBuiltInSymbol) Covariance).setEvaluator(new org.matheclipse.core.reflection.system.Covariance());
		((IBuiltInSymbol) Cross).setEvaluator(new org.matheclipse.core.reflection.system.Cross());
		((IBuiltInSymbol) Csc).setEvaluator(new org.matheclipse.core.reflection.system.Csc());
		((IBuiltInSymbol) Csch).setEvaluator(new org.matheclipse.core.reflection.system.Csch());
		((IBuiltInSymbol) Curl).setEvaluator(new org.matheclipse.core.reflection.system.Curl());
		((IBuiltInSymbol) D).setEvaluator(new org.matheclipse.core.reflection.system.D());
		((IBuiltInSymbol) Decrement).setEvaluator(new org.matheclipse.core.reflection.system.Decrement());
		((IBuiltInSymbol) Default).setEvaluator(new org.matheclipse.core.reflection.system.Default());
		((IBuiltInSymbol) DeleteDuplicates).setEvaluator(new org.matheclipse.core.reflection.system.DeleteDuplicates());
		((IBuiltInSymbol) Denominator).setEvaluator(new org.matheclipse.core.reflection.system.Denominator());
		((IBuiltInSymbol) Derivative).setEvaluator(new org.matheclipse.core.reflection.system.Derivative());
		((IBuiltInSymbol) DesignMatrix).setEvaluator(new org.matheclipse.core.reflection.system.DesignMatrix());
		((IBuiltInSymbol) Det).setEvaluator(new org.matheclipse.core.reflection.system.Det());
		((IBuiltInSymbol) DiagonalMatrix).setEvaluator(new org.matheclipse.core.reflection.system.DiagonalMatrix());
		((IBuiltInSymbol) DiceDissimilarity)
				.setEvaluator(new org.matheclipse.core.reflection.system.DiceDissimilarity());
		((IBuiltInSymbol) Dimensions).setEvaluator(new org.matheclipse.core.reflection.system.Dimensions());
		((IBuiltInSymbol) Discriminant).setEvaluator(new org.matheclipse.core.reflection.system.Discriminant());
		((IBuiltInSymbol) Distribute).setEvaluator(new org.matheclipse.core.reflection.system.Distribute());
		((IBuiltInSymbol) Divergence).setEvaluator(new org.matheclipse.core.reflection.system.Divergence());
		((IBuiltInSymbol) DivideBy).setEvaluator(new org.matheclipse.core.reflection.system.DivideBy());
		((IBuiltInSymbol) Divisible).setEvaluator(new org.matheclipse.core.reflection.system.Divisible());
		((IBuiltInSymbol) Divisors).setEvaluator(new org.matheclipse.core.reflection.system.Divisors());
		((IBuiltInSymbol) Dot).setEvaluator(new org.matheclipse.core.reflection.system.Dot());
		((IBuiltInSymbol) DSolve).setEvaluator(new org.matheclipse.core.reflection.system.DSolve());
		((IBuiltInSymbol) EasterSunday).setEvaluator(new org.matheclipse.core.reflection.system.EasterSunday());
		((IBuiltInSymbol) Eigenvalues).setEvaluator(new org.matheclipse.core.reflection.system.Eigenvalues());
		((IBuiltInSymbol) Eigenvectors).setEvaluator(new org.matheclipse.core.reflection.system.Eigenvectors());
		((IBuiltInSymbol) ElementData).setEvaluator(new org.matheclipse.core.reflection.system.ElementData());
		((IBuiltInSymbol) Eliminate).setEvaluator(new org.matheclipse.core.reflection.system.Eliminate());
		((IBuiltInSymbol) EllipticE).setEvaluator(new org.matheclipse.core.reflection.system.EllipticE());
		((IBuiltInSymbol) EllipticPi).setEvaluator(new org.matheclipse.core.reflection.system.EllipticPi());
		((IBuiltInSymbol) Equal).setEvaluator(new org.matheclipse.core.reflection.system.Equal());
		((IBuiltInSymbol) Equivalent).setEvaluator(new org.matheclipse.core.reflection.system.Equivalent());
		((IBuiltInSymbol) Erf).setEvaluator(new org.matheclipse.core.reflection.system.Erf());
		((IBuiltInSymbol) EuclidianDistance)
				.setEvaluator(new org.matheclipse.core.reflection.system.EuclidianDistance());
		((IBuiltInSymbol) EulerE).setEvaluator(new org.matheclipse.core.reflection.system.EulerE());
		((IBuiltInSymbol) EulerPhi).setEvaluator(new org.matheclipse.core.reflection.system.EulerPhi());
		((IBuiltInSymbol) Exp).setEvaluator(new org.matheclipse.core.reflection.system.Exp());
		((IBuiltInSymbol) Expand).setEvaluator(new org.matheclipse.core.reflection.system.Expand());
		((IBuiltInSymbol) ExpandAll).setEvaluator(new org.matheclipse.core.reflection.system.ExpandAll());
		((IBuiltInSymbol) Export).setEvaluator(new org.matheclipse.core.reflection.system.Export());
		((IBuiltInSymbol) ExtendedGCD).setEvaluator(new org.matheclipse.core.reflection.system.ExtendedGCD());
		((IBuiltInSymbol) Extract).setEvaluator(new org.matheclipse.core.reflection.system.Extract());
		((IBuiltInSymbol) Factor).setEvaluator(new org.matheclipse.core.reflection.system.Factor());
		((IBuiltInSymbol) Factorial).setEvaluator(new org.matheclipse.core.reflection.system.Factorial());
		((IBuiltInSymbol) Factorial2).setEvaluator(new org.matheclipse.core.reflection.system.Factorial2());
		((IBuiltInSymbol) FactorInteger).setEvaluator(new org.matheclipse.core.reflection.system.FactorInteger());
		((IBuiltInSymbol) FactorSquareFree).setEvaluator(new org.matheclipse.core.reflection.system.FactorSquareFree());
		((IBuiltInSymbol) FactorSquareFreeList)
				.setEvaluator(new org.matheclipse.core.reflection.system.FactorSquareFreeList());
		((IBuiltInSymbol) FactorTerms).setEvaluator(new org.matheclipse.core.reflection.system.FactorTerms());
		((IBuiltInSymbol) Fibonacci).setEvaluator(new org.matheclipse.core.reflection.system.Fibonacci());
		((IBuiltInSymbol) FindInstance).setEvaluator(new org.matheclipse.core.reflection.system.FindInstance());
		((IBuiltInSymbol) FindRoot).setEvaluator(new org.matheclipse.core.reflection.system.FindRoot());
		((IBuiltInSymbol) Fit).setEvaluator(new org.matheclipse.core.reflection.system.Fit());
		((IBuiltInSymbol) Floor).setEvaluator(new org.matheclipse.core.reflection.system.Floor());
		((IBuiltInSymbol) FractionalPart).setEvaluator(new org.matheclipse.core.reflection.system.FractionalPart());
		((IBuiltInSymbol) FresnelC).setEvaluator(new org.matheclipse.core.reflection.system.FresnelC());
		((IBuiltInSymbol) FresnelS).setEvaluator(new org.matheclipse.core.reflection.system.FresnelS());
		((IBuiltInSymbol) FrobeniusSolve).setEvaluator(new org.matheclipse.core.reflection.system.FrobeniusSolve());
		((IBuiltInSymbol) FromCharacterCode)
				.setEvaluator(new org.matheclipse.core.reflection.system.FromCharacterCode());
		((IBuiltInSymbol) FromContinuedFraction)
				.setEvaluator(new org.matheclipse.core.reflection.system.FromContinuedFraction());
		((IBuiltInSymbol) FromPolarCoordinates)
				.setEvaluator(new org.matheclipse.core.reflection.system.FromPolarCoordinates());
		((IBuiltInSymbol) FullSimplify).setEvaluator(new org.matheclipse.core.reflection.system.FullSimplify());
		((IBuiltInSymbol) Gamma).setEvaluator(new org.matheclipse.core.reflection.system.Gamma());
		((IBuiltInSymbol) Gather).setEvaluator(new org.matheclipse.core.reflection.system.Gather());
		((IBuiltInSymbol) GCD).setEvaluator(new org.matheclipse.core.reflection.system.GCD());
		((IBuiltInSymbol) GeometricMean).setEvaluator(new org.matheclipse.core.reflection.system.GeometricMean());
		((IBuiltInSymbol) Greater).setEvaluator(new org.matheclipse.core.reflection.system.Greater());
		((IBuiltInSymbol) GreaterEqual).setEvaluator(new org.matheclipse.core.reflection.system.GreaterEqual());
		((IBuiltInSymbol) GroebnerBasis).setEvaluator(new org.matheclipse.core.reflection.system.GroebnerBasis());
		((IBuiltInSymbol) HarmonicNumber).setEvaluator(new org.matheclipse.core.reflection.system.HarmonicNumber());
		((IBuiltInSymbol) Haversine).setEvaluator(new org.matheclipse.core.reflection.system.Haversine());
		((IBuiltInSymbol) HermiteH).setEvaluator(new org.matheclipse.core.reflection.system.HermiteH());
		((IBuiltInSymbol) HilbertMatrix).setEvaluator(new org.matheclipse.core.reflection.system.HilbertMatrix());
		((IBuiltInSymbol) Horner).setEvaluator(new org.matheclipse.core.reflection.system.Horner());
		((IBuiltInSymbol) HornerForm).setEvaluator(new org.matheclipse.core.reflection.system.HornerForm());
		((IBuiltInSymbol) Hypergeometric1F1)
				.setEvaluator(new org.matheclipse.core.reflection.system.Hypergeometric1F1());
		((IBuiltInSymbol) Hypergeometric2F1)
				.setEvaluator(new org.matheclipse.core.reflection.system.Hypergeometric2F1());
		((IBuiltInSymbol) IdentityMatrix).setEvaluator(new org.matheclipse.core.reflection.system.IdentityMatrix());
		((IBuiltInSymbol) Im).setEvaluator(new org.matheclipse.core.reflection.system.Im());
		((IBuiltInSymbol) Import).setEvaluator(new org.matheclipse.core.reflection.system.Import());
		((IBuiltInSymbol) Increment).setEvaluator(new org.matheclipse.core.reflection.system.Increment());
		((IBuiltInSymbol) Inner).setEvaluator(new org.matheclipse.core.reflection.system.Inner());
		((IBuiltInSymbol) IntegerExponent).setEvaluator(new org.matheclipse.core.reflection.system.IntegerExponent());
		((IBuiltInSymbol) IntegerPart).setEvaluator(new org.matheclipse.core.reflection.system.IntegerPart());
		((IBuiltInSymbol) IntegerPartitions)
				.setEvaluator(new org.matheclipse.core.reflection.system.IntegerPartitions());
		// ((IVarSymbol)Integrate).setEvaluator(new
		// org.matheclipse.core.reflection.system.Integrate());
		((IBuiltInSymbol) InterpolatingFunction)
				.setEvaluator(new org.matheclipse.core.reflection.system.InterpolatingFunction());
		((IBuiltInSymbol) InterpolatingPolynomial)
				.setEvaluator(new org.matheclipse.core.reflection.system.InterpolatingPolynomial());
		((IBuiltInSymbol) Interpolation).setEvaluator(new org.matheclipse.core.reflection.system.Interpolation());
		((IBuiltInSymbol) Intersection).setEvaluator(new org.matheclipse.core.reflection.system.Intersection());
		((IBuiltInSymbol) Interval).setEvaluator(new org.matheclipse.core.reflection.system.Interval());
		((IBuiltInSymbol) Inverse).setEvaluator(new org.matheclipse.core.reflection.system.Inverse());
		((IBuiltInSymbol) InverseErf).setEvaluator(new org.matheclipse.core.reflection.system.InverseErf());
		((IBuiltInSymbol) InverseErfc).setEvaluator(new org.matheclipse.core.reflection.system.InverseErfc());
		((IBuiltInSymbol) InverseFunction).setEvaluator(new org.matheclipse.core.reflection.system.InverseFunction());
		((IBuiltInSymbol) InverseHaversine).setEvaluator(new org.matheclipse.core.reflection.system.InverseHaversine());
		((IBuiltInSymbol) JaccardDissimilarity)
				.setEvaluator(new org.matheclipse.core.reflection.system.JaccardDissimilarity());
		((IBuiltInSymbol) JacobiMatrix).setEvaluator(new org.matheclipse.core.reflection.system.JacobiMatrix());
		((IBuiltInSymbol) JacobiSymbol).setEvaluator(new org.matheclipse.core.reflection.system.JacobiSymbol());
		((IBuiltInSymbol) Join).setEvaluator(new org.matheclipse.core.reflection.system.Join());
		((IBuiltInSymbol) KOrderlessPartitions)
				.setEvaluator(new org.matheclipse.core.reflection.system.KOrderlessPartitions());
		((IBuiltInSymbol) KPartitions).setEvaluator(new org.matheclipse.core.reflection.system.KPartitions());
		((IBuiltInSymbol) Kurtosis).setEvaluator(new org.matheclipse.core.reflection.system.Kurtosis());
		((IBuiltInSymbol) LaguerreL).setEvaluator(new org.matheclipse.core.reflection.system.LaguerreL());
		((IBuiltInSymbol) LaplaceTransform).setEvaluator(new org.matheclipse.core.reflection.system.LaplaceTransform());
		((IBuiltInSymbol) LCM).setEvaluator(new org.matheclipse.core.reflection.system.LCM());
		((IBuiltInSymbol) LegendreP).setEvaluator(new org.matheclipse.core.reflection.system.LegendreP());
		((IBuiltInSymbol) Less).setEvaluator(new org.matheclipse.core.reflection.system.Less());
		((IBuiltInSymbol) LessEqual).setEvaluator(new org.matheclipse.core.reflection.system.LessEqual());
		((IBuiltInSymbol) LetterQ).setEvaluator(new org.matheclipse.core.reflection.system.LetterQ());
		((IBuiltInSymbol) Level).setEvaluator(new org.matheclipse.core.reflection.system.Level());
		((IBuiltInSymbol) Limit).setEvaluator(new org.matheclipse.core.reflection.system.Limit());
		((IBuiltInSymbol) LinearProgramming)
				.setEvaluator(new org.matheclipse.core.reflection.system.LinearProgramming());
		((IBuiltInSymbol) LinearSolve).setEvaluator(new org.matheclipse.core.reflection.system.LinearSolve());
		((IBuiltInSymbol) Log).setEvaluator(new org.matheclipse.core.reflection.system.Log());
		((IBuiltInSymbol) Log10).setEvaluator(new org.matheclipse.core.reflection.system.Log10());
		((IBuiltInSymbol) Log2).setEvaluator(new org.matheclipse.core.reflection.system.Log2());
		((IBuiltInSymbol) LogisticSigmoid).setEvaluator(new org.matheclipse.core.reflection.system.LogisticSigmoid());
		((IBuiltInSymbol) LowerCaseQ).setEvaluator(new org.matheclipse.core.reflection.system.LowerCaseQ());
		((IBuiltInSymbol) LUDecomposition).setEvaluator(new org.matheclipse.core.reflection.system.LUDecomposition());
		((IBuiltInSymbol) ManhattanDistance)
				.setEvaluator(new org.matheclipse.core.reflection.system.ManhattanDistance());
		((IBuiltInSymbol) Map).setEvaluator(new org.matheclipse.core.reflection.system.Map());
		((IBuiltInSymbol) MapAll).setEvaluator(new org.matheclipse.core.reflection.system.MapAll());
		((IBuiltInSymbol) MapAt).setEvaluator(new org.matheclipse.core.reflection.system.MapAt());
		((IBuiltInSymbol) MapThread).setEvaluator(new org.matheclipse.core.reflection.system.MapThread());
		((IBuiltInSymbol) MatchingDissimilarity)
				.setEvaluator(new org.matheclipse.core.reflection.system.MatchingDissimilarity());
		((IBuiltInSymbol) MatrixPower).setEvaluator(new org.matheclipse.core.reflection.system.MatrixPower());
		((IBuiltInSymbol) MatrixRank).setEvaluator(new org.matheclipse.core.reflection.system.MatrixRank());
		((IBuiltInSymbol) Max).setEvaluator(new org.matheclipse.core.reflection.system.Max());
		((IBuiltInSymbol) Mean).setEvaluator(new org.matheclipse.core.reflection.system.Mean());
		((IBuiltInSymbol) Median).setEvaluator(new org.matheclipse.core.reflection.system.Median());
		((IBuiltInSymbol) Min).setEvaluator(new org.matheclipse.core.reflection.system.Min());
		((IBuiltInSymbol) Mod).setEvaluator(new org.matheclipse.core.reflection.system.Mod());
		((IBuiltInSymbol) MoebiusMu).setEvaluator(new org.matheclipse.core.reflection.system.MoebiusMu());
		((IBuiltInSymbol) MonomialList).setEvaluator(new org.matheclipse.core.reflection.system.MonomialList());
		((IBuiltInSymbol) Most).setEvaluator(new org.matheclipse.core.reflection.system.Most());
		((IBuiltInSymbol) Multinomial).setEvaluator(new org.matheclipse.core.reflection.system.Multinomial());
		((IBuiltInSymbol) MultiplicativeOrder)
				.setEvaluator(new org.matheclipse.core.reflection.system.MultiplicativeOrder());
		((IBuiltInSymbol) Names).setEvaluator(new org.matheclipse.core.reflection.system.Names());
		((IBuiltInSymbol) NDSolve).setEvaluator(new org.matheclipse.core.reflection.system.NDSolve());
		((IBuiltInSymbol) Nearest).setEvaluator(new org.matheclipse.core.reflection.system.Nearest());
		((IBuiltInSymbol) Negative).setEvaluator(new org.matheclipse.core.reflection.system.Negative());
		((IBuiltInSymbol) NextPrime).setEvaluator(new org.matheclipse.core.reflection.system.NextPrime());
		((IBuiltInSymbol) NFourierTransform)
				.setEvaluator(new org.matheclipse.core.reflection.system.NFourierTransform());
		((IBuiltInSymbol) NIntegrate).setEvaluator(new org.matheclipse.core.reflection.system.NIntegrate());
		((IBuiltInSymbol) NMaximize).setEvaluator(new org.matheclipse.core.reflection.system.NMaximize());
		((IBuiltInSymbol) NMinimize).setEvaluator(new org.matheclipse.core.reflection.system.NMinimize());
		((IBuiltInSymbol) NonCommutativeMultiply)
				.setEvaluator(new org.matheclipse.core.reflection.system.NonCommutativeMultiply());
		((IBuiltInSymbol) NoneTrue).setEvaluator(new org.matheclipse.core.reflection.system.NoneTrue());
		((IBuiltInSymbol) NonNegative).setEvaluator(new org.matheclipse.core.reflection.system.NonNegative());
		((IBuiltInSymbol) Norm).setEvaluator(new org.matheclipse.core.reflection.system.Norm());
		((IBuiltInSymbol) Normal).setEvaluator(new org.matheclipse.core.reflection.system.Normal());
		((IBuiltInSymbol) Normalize).setEvaluator(new org.matheclipse.core.reflection.system.Normalize());
		((IBuiltInSymbol) Not).setEvaluator(new org.matheclipse.core.reflection.system.Not());
		((IBuiltInSymbol) NRoots).setEvaluator(new org.matheclipse.core.reflection.system.NRoots());
		((IBuiltInSymbol) NSolve).setEvaluator(new org.matheclipse.core.reflection.system.NSolve());
		((IBuiltInSymbol) NullSpace).setEvaluator(new org.matheclipse.core.reflection.system.NullSpace());
		((IBuiltInSymbol) Numerator).setEvaluator(new org.matheclipse.core.reflection.system.Numerator());
		((IBuiltInSymbol) Order).setEvaluator(new org.matheclipse.core.reflection.system.Order());
		((IBuiltInSymbol) OrderedQ).setEvaluator(new org.matheclipse.core.reflection.system.OrderedQ());
		((IBuiltInSymbol) Out).setEvaluator(new org.matheclipse.core.reflection.system.Out());
		((IBuiltInSymbol) Outer).setEvaluator(new org.matheclipse.core.reflection.system.Outer());
		((IBuiltInSymbol) PadLeft).setEvaluator(new org.matheclipse.core.reflection.system.PadLeft());
		((IBuiltInSymbol) PadRight).setEvaluator(new org.matheclipse.core.reflection.system.PadRight());
		((IBuiltInSymbol) Partition).setEvaluator(new org.matheclipse.core.reflection.system.Partition());
		((IBuiltInSymbol) PDF).setEvaluator(new org.matheclipse.core.reflection.system.PDF());
		((IBuiltInSymbol) Permutations).setEvaluator(new org.matheclipse.core.reflection.system.Permutations());
		((IBuiltInSymbol) Piecewise).setEvaluator(new org.matheclipse.core.reflection.system.Piecewise());
		((IBuiltInSymbol) Plot).setEvaluator(new org.matheclipse.core.reflection.system.Plot());
		((IBuiltInSymbol) Plot3D).setEvaluator(new org.matheclipse.core.reflection.system.Plot3D());
		// ((IVarSymbol)Plus).setEvaluator(new
		// org.matheclipse.core.reflection.system.Plus());
		((IBuiltInSymbol) Pochhammer).setEvaluator(new org.matheclipse.core.reflection.system.Pochhammer());
		((IBuiltInSymbol) PolyLog).setEvaluator(new org.matheclipse.core.reflection.system.PolyLog());
		((IBuiltInSymbol) PolynomialExtendedGCD)
				.setEvaluator(new org.matheclipse.core.reflection.system.PolynomialExtendedGCD());
		((IBuiltInSymbol) PolynomialGCD).setEvaluator(new org.matheclipse.core.reflection.system.PolynomialGCD());
		((IBuiltInSymbol) PolynomialLCM).setEvaluator(new org.matheclipse.core.reflection.system.PolynomialLCM());
		((IBuiltInSymbol) PolynomialQ).setEvaluator(new org.matheclipse.core.reflection.system.PolynomialQ());
		((IBuiltInSymbol) PolynomialQuotient)
				.setEvaluator(new org.matheclipse.core.reflection.system.PolynomialQuotient());
		((IBuiltInSymbol) PolynomialQuotientRemainder)
				.setEvaluator(new org.matheclipse.core.reflection.system.PolynomialQuotientRemainder());
		((IBuiltInSymbol) PolynomialRemainder)
				.setEvaluator(new org.matheclipse.core.reflection.system.PolynomialRemainder());
		((IBuiltInSymbol) Positive).setEvaluator(new org.matheclipse.core.reflection.system.Positive());
		// ((IVarSymbol)Power).setEvaluator(new
		// org.matheclipse.core.reflection.system.Power());
		((IBuiltInSymbol) PowerExpand).setEvaluator(new org.matheclipse.core.reflection.system.PowerExpand());
		((IBuiltInSymbol) PowerMod).setEvaluator(new org.matheclipse.core.reflection.system.PowerMod());
		((IBuiltInSymbol) PreDecrement).setEvaluator(new org.matheclipse.core.reflection.system.PreDecrement());
		((IBuiltInSymbol) PreIncrement).setEvaluator(new org.matheclipse.core.reflection.system.PreIncrement());
		((IBuiltInSymbol) Prime).setEvaluator(new org.matheclipse.core.reflection.system.Prime());
		((IBuiltInSymbol) PrimitiveRoots).setEvaluator(new org.matheclipse.core.reflection.system.PrimitiveRoots());
		((IBuiltInSymbol) Product).setEvaluator(new org.matheclipse.core.reflection.system.Product());
		((IBuiltInSymbol) ProductLog).setEvaluator(new org.matheclipse.core.reflection.system.ProductLog());
		((IBuiltInSymbol) PseudoInverse).setEvaluator(new org.matheclipse.core.reflection.system.PseudoInverse());
		((IBuiltInSymbol) Quotient).setEvaluator(new org.matheclipse.core.reflection.system.Quotient());
		((IBuiltInSymbol) RandomInteger).setEvaluator(new org.matheclipse.core.reflection.system.RandomInteger());
		((IBuiltInSymbol) RandomReal).setEvaluator(new org.matheclipse.core.reflection.system.RandomReal());
		((IBuiltInSymbol) RandomSample).setEvaluator(new org.matheclipse.core.reflection.system.RandomSample());
		((IBuiltInSymbol) Range).setEvaluator(new org.matheclipse.core.reflection.system.Range());
		((IBuiltInSymbol) Rationalize).setEvaluator(new org.matheclipse.core.reflection.system.Rationalize());
		((IBuiltInSymbol) Re).setEvaluator(new org.matheclipse.core.reflection.system.Re());
		((IBuiltInSymbol) Replace).setEvaluator(new org.matheclipse.core.reflection.system.Replace());
		((IBuiltInSymbol) ReplaceAll).setEvaluator(new org.matheclipse.core.reflection.system.ReplaceAll());
		((IBuiltInSymbol) ReplaceList).setEvaluator(new org.matheclipse.core.reflection.system.ReplaceList());
		((IBuiltInSymbol) ReplacePart).setEvaluator(new org.matheclipse.core.reflection.system.ReplacePart());
		((IBuiltInSymbol) ReplaceRepeated).setEvaluator(new org.matheclipse.core.reflection.system.ReplaceRepeated());
		((IBuiltInSymbol) Resultant).setEvaluator(new org.matheclipse.core.reflection.system.Resultant());
		((IBuiltInSymbol) Reverse).setEvaluator(new org.matheclipse.core.reflection.system.Reverse());
		((IBuiltInSymbol) RogersTanimotoDissimilarity)
				.setEvaluator(new org.matheclipse.core.reflection.system.RogersTanimotoDissimilarity());
		((IBuiltInSymbol) Root).setEvaluator(new org.matheclipse.core.reflection.system.Root());
		((IBuiltInSymbol) RootIntervals).setEvaluator(new org.matheclipse.core.reflection.system.RootIntervals());
		((IBuiltInSymbol) Roots).setEvaluator(new org.matheclipse.core.reflection.system.Roots());
		((IBuiltInSymbol) Round).setEvaluator(new org.matheclipse.core.reflection.system.Round());
		((IBuiltInSymbol) RowReduce).setEvaluator(new org.matheclipse.core.reflection.system.RowReduce());
		((IBuiltInSymbol) RussellRaoDissimilarity)
				.setEvaluator(new org.matheclipse.core.reflection.system.RussellRaoDissimilarity());
		((IBuiltInSymbol) SameQ).setEvaluator(new org.matheclipse.core.reflection.system.SameQ());
		((IBuiltInSymbol) SatisfiableQ).setEvaluator(new org.matheclipse.core.reflection.system.SatisfiableQ());
		((IBuiltInSymbol) Scan).setEvaluator(new org.matheclipse.core.reflection.system.Scan());
		((IBuiltInSymbol) Sec).setEvaluator(new org.matheclipse.core.reflection.system.Sec());
		((IBuiltInSymbol) Sech).setEvaluator(new org.matheclipse.core.reflection.system.Sech());
		((IBuiltInSymbol) Select).setEvaluator(new org.matheclipse.core.reflection.system.Select());
		((IBuiltInSymbol) Series).setEvaluator(new org.matheclipse.core.reflection.system.Series());
		((IBuiltInSymbol) SeriesData).setEvaluator(new org.matheclipse.core.reflection.system.SeriesData());
		((IBuiltInSymbol) Share).setEvaluator(new org.matheclipse.core.reflection.system.Share());
		((IBuiltInSymbol) Sign).setEvaluator(new org.matheclipse.core.reflection.system.Sign());
		((IBuiltInSymbol) SignCmp).setEvaluator(new org.matheclipse.core.reflection.system.SignCmp());
		((IBuiltInSymbol) Simplify).setEvaluator(new org.matheclipse.core.reflection.system.Simplify());
		((IBuiltInSymbol) Sin).setEvaluator(new org.matheclipse.core.reflection.system.Sin());
		((IBuiltInSymbol) Sinc).setEvaluator(new org.matheclipse.core.reflection.system.Sinc());
		((IBuiltInSymbol) SingularValueDecomposition)
				.setEvaluator(new org.matheclipse.core.reflection.system.SingularValueDecomposition());
		((IBuiltInSymbol) Sinh).setEvaluator(new org.matheclipse.core.reflection.system.Sinh());
		((IBuiltInSymbol) SinIntegral).setEvaluator(new org.matheclipse.core.reflection.system.SinIntegral());
		((IBuiltInSymbol) Skewness).setEvaluator(new org.matheclipse.core.reflection.system.Skewness());
		((IBuiltInSymbol) SokalSneathDissimilarity)
				.setEvaluator(new org.matheclipse.core.reflection.system.SokalSneathDissimilarity());
		((IBuiltInSymbol) Solve).setEvaluator(new org.matheclipse.core.reflection.system.Solve());
		((IBuiltInSymbol) Sort).setEvaluator(new org.matheclipse.core.reflection.system.Sort());
		// ((IVarSymbol)Sqrt).setEvaluator(new
		// org.matheclipse.core.reflection.system.Sqrt());
		((IBuiltInSymbol) SquaredEuclidianDistance)
				.setEvaluator(new org.matheclipse.core.reflection.system.SquaredEuclidianDistance());
		((IBuiltInSymbol) SquareFreeQ).setEvaluator(new org.matheclipse.core.reflection.system.SquareFreeQ());
		((IBuiltInSymbol) StirlingS2).setEvaluator(new org.matheclipse.core.reflection.system.StirlingS2());
		((IBuiltInSymbol) StringDrop).setEvaluator(new org.matheclipse.core.reflection.system.StringDrop());
		((IBuiltInSymbol) StringJoin).setEvaluator(new org.matheclipse.core.reflection.system.StringJoin());
		((IBuiltInSymbol) StringLength).setEvaluator(new org.matheclipse.core.reflection.system.StringLength());
		((IBuiltInSymbol) StringTake).setEvaluator(new org.matheclipse.core.reflection.system.StringTake());
		((IBuiltInSymbol) Subfactorial).setEvaluator(new org.matheclipse.core.reflection.system.Subfactorial());
		((IBuiltInSymbol) Subsets).setEvaluator(new org.matheclipse.core.reflection.system.Subsets());
		((IBuiltInSymbol) SubtractFrom).setEvaluator(new org.matheclipse.core.reflection.system.SubtractFrom());
		((IBuiltInSymbol) Sum).setEvaluator(new org.matheclipse.core.reflection.system.Sum());
		((IBuiltInSymbol) Surd).setEvaluator(new org.matheclipse.core.reflection.system.Surd());
		((IBuiltInSymbol) SyntaxLength).setEvaluator(new org.matheclipse.core.reflection.system.SyntaxLength());
		((IBuiltInSymbol) Table).setEvaluator(new org.matheclipse.core.reflection.system.Table());
		((IBuiltInSymbol) Tally).setEvaluator(new org.matheclipse.core.reflection.system.Tally());
		((IBuiltInSymbol) Tan).setEvaluator(new org.matheclipse.core.reflection.system.Tan());
		((IBuiltInSymbol) Tanh).setEvaluator(new org.matheclipse.core.reflection.system.Tanh());
		((IBuiltInSymbol) TautologyQ).setEvaluator(new org.matheclipse.core.reflection.system.TautologyQ());
		((IBuiltInSymbol) Taylor).setEvaluator(new org.matheclipse.core.reflection.system.Taylor());
		((IBuiltInSymbol) Thread).setEvaluator(new org.matheclipse.core.reflection.system.Thread());
		((IBuiltInSymbol) Through).setEvaluator(new org.matheclipse.core.reflection.system.Through());
		// ((IVarSymbol)Times).setEvaluator(new
		// org.matheclipse.core.reflection.system.Times());
		((IBuiltInSymbol) TimesBy).setEvaluator(new org.matheclipse.core.reflection.system.TimesBy());
		((IBuiltInSymbol) ToCharacterCode).setEvaluator(new org.matheclipse.core.reflection.system.ToCharacterCode());
		((IBuiltInSymbol) Together).setEvaluator(new org.matheclipse.core.reflection.system.Together());
		((IBuiltInSymbol) ToPolarCoordinates)
				.setEvaluator(new org.matheclipse.core.reflection.system.ToPolarCoordinates());
		((IBuiltInSymbol) ToString).setEvaluator(new org.matheclipse.core.reflection.system.ToString());
		((IBuiltInSymbol) Total).setEvaluator(new org.matheclipse.core.reflection.system.Total());
		((IBuiltInSymbol) ToUnicode).setEvaluator(new org.matheclipse.core.reflection.system.ToUnicode());
		((IBuiltInSymbol) Tr).setEvaluator(new org.matheclipse.core.reflection.system.Tr());
		((IBuiltInSymbol) Transpose).setEvaluator(new org.matheclipse.core.reflection.system.Transpose());
		((IBuiltInSymbol) TrigExpand).setEvaluator(new org.matheclipse.core.reflection.system.TrigExpand());
		((IBuiltInSymbol) TrigReduce).setEvaluator(new org.matheclipse.core.reflection.system.TrigReduce());
		((IBuiltInSymbol) TrigToExp).setEvaluator(new org.matheclipse.core.reflection.system.TrigToExp());
		((IBuiltInSymbol) TrueQ).setEvaluator(new org.matheclipse.core.reflection.system.TrueQ());
		((IBuiltInSymbol) Tuples).setEvaluator(new org.matheclipse.core.reflection.system.Tuples());
		((IBuiltInSymbol) Unequal).setEvaluator(new org.matheclipse.core.reflection.system.Unequal());
		((IBuiltInSymbol) Union).setEvaluator(new org.matheclipse.core.reflection.system.Union());
		((IBuiltInSymbol) UnitStep).setEvaluator(new org.matheclipse.core.reflection.system.UnitStep());
		((IBuiltInSymbol) UnitVector).setEvaluator(new org.matheclipse.core.reflection.system.UnitVector());
		((IBuiltInSymbol) UnsameQ).setEvaluator(new org.matheclipse.core.reflection.system.UnsameQ());
		((IBuiltInSymbol) VandermondeMatrix)
				.setEvaluator(new org.matheclipse.core.reflection.system.VandermondeMatrix());
		((IBuiltInSymbol) Variables).setEvaluator(new org.matheclipse.core.reflection.system.Variables());
		((IBuiltInSymbol) Variance).setEvaluator(new org.matheclipse.core.reflection.system.Variance());
		((IBuiltInSymbol) VectorAngle).setEvaluator(new org.matheclipse.core.reflection.system.VectorAngle());
		((IBuiltInSymbol) Xor).setEvaluator(new org.matheclipse.core.reflection.system.Xor());
		((IBuiltInSymbol) YuleDissimilarity)
				.setEvaluator(new org.matheclipse.core.reflection.system.YuleDissimilarity());
		((IBuiltInSymbol) Zeta).setEvaluator(new org.matheclipse.core.reflection.system.Zeta());
	}

	/**
	 * Create a new abstract syntax tree (AST).
	 * 
	 * @param head
	 *            the header expression of the function. If the ast represents a
	 *            function like <code>f[x,y], Sin[x],...</code>, the
	 *            <code>head</code> will be an instance of type ISymbol.
	 * @param a
	 * @return
	 */
	public final static IAST $(final IExpr head, final IExpr... a) {
		return ast(a, head);
	}

	/**
	 * Create a <code>Blank[]</code> pattern object for pattern-matching and
	 * term rewriting
	 * 
	 * @return IPattern
	 */
	public static IPattern $b() {
		return org.matheclipse.core.expression.Blank.valueOf();
	}

	/**
	 * Create a <code>Blank[condition]</code> pattern object for
	 * pattern-matching and term rewriting
	 * 
	 * @param condition
	 *            additional condition which should be checked in
	 *            pattern-matching
	 * @return IPattern
	 */
	public static IPattern $b(final IExpr condition) {
		return org.matheclipse.core.expression.Blank.valueOf(condition);
	}

	/**
	 * Create a <code>Blank[condition]</code> pattern object for
	 * pattern-matching and term rewriting
	 * 
	 * @param condition
	 *            additional condition which should be checked in
	 *            pattern-matching
	 * @param def
	 *            if <code>true</code> use a default value in pattern-matching
	 *            if an argument is optional
	 * @return IPattern
	 */
	public static IPattern $b(final IExpr condition, boolean def) {
		return new org.matheclipse.core.expression.Blank(condition, def);
	}

	/**
	 * Create a <code>Blank[condition]</code> pattern object for
	 * pattern-matching and term rewriting
	 * 
	 * @param condition
	 *            additional condition which should be checked in
	 *            pattern-matching
	 * @param defaultValue
	 *            use this <code>defaultValue</code> in pattern-matching if an
	 *            argument is optional
	 * @return IPattern
	 */
	public static IPattern $b(final IExpr condition, IExpr defaultValue) {
		return new org.matheclipse.core.expression.Blank(condition, defaultValue);
	}

	/**
	 * Create a <code>Pattern[]</code> pattern for pattern-matching and term
	 * rewriting
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
	 *            additional condition which should be checked in
	 *            pattern-matching
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
	 *            additional condition which should be checked in
	 *            pattern-matching
	 * @param def
	 *            if <code>true</code>, the pattern can match to a default value
	 *            associated with the AST's head the pattern is used in.
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
	 *            additional condition which should be checked in
	 *            pattern-matching
	 * @param defaultValue
	 *            use this <code>defaultValue</code> in pattern-matching if an
	 *            argument is optional
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
	 *            additional condition which should be checked in
	 *            pattern-matching
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
	 *            additional condition which should be checked in
	 *            pattern-matching
	 * @param def
	 *            use a default value for this pattern if necessary
	 * @return IPattern
	 */
	public static IPattern $p(@Nonnull final String symbolName, final IExpr check, boolean def) {
		// if (symbolName == null) {
		// return org.matheclipse.core.expression.Pattern.valueOf(null, check,
		// def);
		// }
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
	 *            additional condition which should be checked in
	 *            pattern-matching
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
	 *            additional condition which should be checked in
	 *            pattern-matching
	 * @param def
	 *            if <code>true</code>, the pattern can match to a default value
	 *            associated with the AST's head the pattern is used in.
	 * @param zeroArgsAllowed
	 *            if <code>true</code> 0 argument sequences are allowed for this
	 *            pattern
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
	 * Get or create a global symbol which is retrieved from the global symbols
	 * map or created or retrieved from the thread local variables map.
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @return
	 */
	public static ISymbol $s(final String symbolName) {
		return $s(symbolName, true);
	}

	/**
	 * Get or create a global symbol which is retrieved from the global symbols
	 * map or created or retrieved from the thread local variables map.
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @param setEval
	 *            if <code>true</code> determine and assign the built-in
	 *            evaluator object to the symbol.
	 * @return
	 */
	public static ISymbol $s(final String symbolName, boolean setEval) {
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
		if (Config.SERVER_MODE) {
			symbol = HIDDEN_SYMBOLS_MAP.get(name);
			// symbol = engine.getUserVariable(name);
			if (symbol != null) {
				return symbol;
			}
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
			symbol = new BuiltInSymbol(name);
			// engine.putUserVariable(name, symbol);
			HIDDEN_SYMBOLS_MAP.put(name, symbol);
			if (name.charAt(0) == '$') {
				SYMBOL_OBSERVER.createUserSymbol(symbol);
			}
		} else {
			symbol = HIDDEN_SYMBOLS_MAP.get(name);
			if (symbol != null) {
				return symbol;
			}
			symbol = new BuiltInSymbol(name);
			HIDDEN_SYMBOLS_MAP.put(name, symbol);
			if (symbol.isBuiltInSymbol()) {
				if (!setEval) {
					((IBuiltInSymbol) symbol).setEvaluator(BuiltInSymbol.DUMMY_EVALUATOR);
				} else {
					((IBuiltInSymbol) symbol).getEvaluator();
				}
			}
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

	public static IAST And() {
		return ast(And);
	}

	public static IAST And(final IExpr... a) {
		return ast(a, And);
	}

	public static IAST And(final IExpr a0, final IExpr a1) {
		return binary(And, a0, a1);
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
	 * Creates a new AST from the given <code>ast</code> and <code>head</code>.
	 * if <code>include</code> is set to <code>true </code> all arguments from
	 * index first to last-1 are copied in the new list if <code>include</code>
	 * is set to <code> false </code> all arguments excluded from index first to
	 * last-1 are copied in the new list
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
	 *            the header expression of the function. If the ast represents a
	 *            function like <code>f[x,y], Sin[x],...</code>, the
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
	 *            the header expression of the function. If the ast represents a
	 *            function like <code>f[x,y], Sin[x],...</code>, the
	 *            <code>head</code> will be an instance of type ISymbol.
	 * @param initialCapacity
	 *            the initial capacity (i.e. number of arguments without the
	 *            header element) of the list.
	 * @param initNull
	 *            initialize all elements with <code>null</code>.
	 * @return
	 */
	public static IAST ast(final IExpr head, final int initialCapacity, final boolean initNull) {
		final AST ast = AST.newInstance(initialCapacity, head);
		if (initNull) {
			for (int i = 0; i < initialCapacity; i++) {
				ast.append(null);
			}
		}
		return ast;
	}

	/**
	 * Create a new abstract syntax tree (AST).
	 * 
	 * @param arr
	 * @param head
	 *            the header expression of the function. If the ast represents a
	 *            function like <code>f[x,y], Sin[x],...</code>, the
	 *            <code>head</code> will be an instance of type ISymbol.
	 * @return
	 */
	public static IAST ast(final IExpr[] arr, final IExpr head) {
		return new AST(head, arr);
	}

	public static IAST AtomQ(final IExpr a) {
		return unaryAST1(AtomQ, a);
	}

	public static IAST BernoulliB(final IExpr a0) {
		return unaryAST1($s("BernoulliB"), a0);
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

	/**
	 * Create a function with 2 arguments as a <code>AST2</code> immutable
	 * object without evaluation.
	 * 
	 * @param head
	 * @param a0
	 * @param a1
	 * @return
	 */
	public final static IAST binaryAST2(final IExpr head, final IExpr a0, final IExpr a1) {
		return new AST2(head, a0, a1);
	}

	public static IAST Binomial(final IExpr a0, final IExpr a1) {

		return binaryAST2($s("Binomial"), a0, a1);
	}

	public static IAST Block(final IExpr a0, final IExpr a1) {
		return binaryAST2(Block, a0, a1);
	}

	/**
	 * Gives symbols "True" or "False" (type ISymbol) depending on the boolean
	 * value.
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

	public static IAST Break() {
		return headAST0(Break);
	}

	public static IAST Cancel(final IExpr a) {
		return unaryAST1(Cancel, a);
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
	 * Set real or imaginary parts of a numeric argument to zero, those absolute
	 * value is less than a delta.
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

	public static IAST CNInfinity() {
		return binary(Times, CN1, Infinity);
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
	 *            the real double value part which should be converted to a
	 *            complex number
	 * @param imagPart
	 *            the imaginary double value part which should be converted to a
	 *            complex number
	 * @return IFraction
	 */
	public static IComplex complex(final double realPart, final double imagPart) {
		return complex(realPart, imagPart, Config.DOUBLE_EPSILON);
	}

	/**
	 * Create a symbolic complex number
	 * 
	 * @param realPart
	 *            the real double value part which should be converted to a
	 *            complex number
	 * @param imagPart
	 *            the imaginary double value part which should be converted to a
	 *            complex number
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
		double nr = realFraction.getNumerator().doubleValue();
		double dr = realFraction.getDenominator().doubleValue();
		double ni = imagFraction.getNumerator().doubleValue();
		double di = imagFraction.getDenominator().doubleValue();

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

	public static IAST Conjugate(final IExpr a0) {
		return unaryAST1(Conjugate, a0);
	}

	public static IAST ConstantArray(final IExpr a0, final IExpr a1) {
		return binaryAST2(ConstantArray, a0, a1);
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

	public static IAST Derivative(final IExpr... a) {
		return ast(a, Derivative);
	}

	public static IAST DesignMatrix(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(DesignMatrix, a0, a1, a2);
	}

	public static IAST Det(final IExpr a0) {
		return unaryAST1(Det, a0);
	}

	public static IAST DirectedInfinity(final IExpr a0) {
		return unaryAST1(DirectedInfinity, a0);
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
	 * The division <code>a0 / a1</code> will be represented by
	 * <code>Times(a0, Power(a1, -1))</code>.
	 * 
	 * @param a0
	 *            numerator
	 * @param a1
	 *            denominator
	 * @return
	 */
	public static IAST Divide(final IExpr a0, final IExpr a1) {
		return binary(Times, a0, binaryAST2(Power, a1, CN1));
	}

	public static IAST Divisible(final IExpr a0, final IExpr a1) {
		return binaryAST2(Divisible, a0, a1);
	}

	public static IAST Do(final IExpr a0, final IExpr a1) {
		return binaryAST2(Do, a0, a1);
	}

	public static IAST Dot(final IExpr... a) {
		return ast(a, Dot);
	}

	public static IAST Dot(final IExpr a0, final IExpr a1) {
		return binary(Dot, a0, a1);
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

	public static IAST EllipticE(final IExpr a0, final IExpr a1) {
		return binaryAST2(EllipticE, a0, a1);
	}

	public static IAST EllipticF(final IExpr a0, final IExpr a1) {
		return binaryAST2(EllipticF, a0, a1);
	}

	public static IAST EllipticPi(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternaryAST3(EllipticPi, a0, a1, a2);
	}

	public static IAST Equal(final IExpr... a) {
		return ast(a, Equal);
	}

	public static IAST Equal(final IExpr a0, final IExpr a1) {
		return binary(Equal, a0, a1);
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

	/**
	 * Evaluate an expression. If no evaluation was possible this method returns
	 * the given argument.
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
	 */
	public static IExpr eval(final ISymbol head, final IExpr a0) {
		final IAST ast = ast(head);
		ast.append(a0);
		return EvalEngine.get().evaluate(ast);
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
		ast.append(a0);
		ast.append(a1);
		return EvalEngine.get().evaluate(ast);
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
		ast.append(a0);
		ast.append(a1);
		ast.append(a2);
		return EvalEngine.get().evaluate(ast);
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
	public static IExpr evalBlock(IExpr expr, ISymbol symbol, @Nonnull IExpr localValue) {
		try {
			symbol.pushLocalVariable(localValue);
			return eval(expr);
		} finally {
			symbol.popLocalVariable();
		}
	}

	/**
	 * Evaluate <code>Expand()</code> for the given expression. returns the
	 * given argument.
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
	 * Apply <code>ExpandAll()</code> to the given expression if it's an
	 * <code>IAST</code>. If expanding wasn't possible this method returns the
	 * given argument.
	 * 
	 * @param a
	 *            the expression which should be evaluated
	 * @return the evaluated expression
	 * @see EvalEngine#evaluate(IExpr)
	 */
	public static IExpr evalExpandAll(IExpr a) {
		return EvalEngine.get().evaluate(ExpandAll(a));
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
	 * Evaluate an expression in &quot;quiet mode&quot;. If no evaluation was
	 * possible this method returns the given argument. In &quot;quiet
	 * mode&quot; all warnings would be suppressed.
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
	 * Evaluate an expression in &quot;quiet mode&quot;. If evaluation is not
	 * possible return <code>null</code>. In &quot;quiet mode&quot; all warnings
	 * would be suppressed.
	 * 
	 * @param expr
	 *            the expression which should be evaluated
	 * @return the evaluated object or <code>F.NIL</code> if no evaluation was
	 *         possible
	 * @see EvalEngine#evalQuietNull(IExpr)
	 * @deprecated use EvalEngine#evalQuietNull()
	 */
	@Deprecated
	public static IExpr evalQuietNull(IExpr a) {
		return EvalEngine.get().evalQuietNull(a);
	}

	/**
	 * Evaluate the given expression and test if the result equals the symbol
	 * <code>True</code>.
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

	public static IAST Exp(final IExpr a0) {
		return binaryAST2(Power, E, a0);
	}

	/**
	 * Apply <code>Expand()</code> to the given expression if it's an
	 * <code>IAST</code>. If expanding wasn't possible this method returns the
	 * given argument.
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
			return org.matheclipse.core.reflection.system.Expand.expand(ast, null, expandNegativePowers, distributePlus)
					.orElse(a);
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
	 * Apply <code>ExpandAll()</code> to the given expression if it's an
	 * <code>IAST</code>. If expanding wasn't possible this method returns the
	 * given argument.
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
			IExpr temp = org.matheclipse.core.reflection.system.ExpandAll.expandAll(ast, null, expandNegativePowers,
					distributePlus);
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

	/**
	 * Create a "fractional" number
	 * 
	 * @param value
	 *            the rational value which should be converted to a fractional
	 *            number
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
	 *            the double value which should be converted to a fractional
	 *            number
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
		return unary(Function, a0);
	}

	public static IAST Function(final IExpr a0, final IExpr a1) {
		return binary(Function, a0, a1);
	}

	public static IAST Gamma(final IExpr a0) {
		return unaryAST1(Gamma, a0);
	}

	public static IAST Gamma(final IExpr a0, final IExpr a1) {
		return binaryAST2(Gamma, a0, a1);
	}

	public static IAST GCD(final IExpr a0) {
		return unaryAST1(GCD, a0);
	}

	public static IAST GCD(final IExpr a0, final IExpr a1) {
		return binaryAST2(GCD, a0, a1);
	}

	/**
	 * Get the namespace
	 * 
	 * @return
	 */
	final public static Namespace getNamespace() {
		return SystemNamespace.DEFAULT;
	}

	public static IAST Graphics() {

		return ast(Graphics);
	}

	public static IExpr Greater(final IExpr a0, final IExpr a1) {
		if (a0.isSignedNumber() && a1.isSignedNumber()) {
			return ((ISignedNumber) a0).isGreaterThan(((ISignedNumber) a1)) ? True : False;
		}
		return binaryAST2(Greater, a0, a1);
	}

	public static IExpr GreaterEqual(final IExpr a0, final IExpr a1) {
		if (a0.isSignedNumber() && a1.isSignedNumber()) {
			return ((ISignedNumber) a0).isLessThan(((ISignedNumber) a1)) ? False : True;
		}
		return binaryAST2(GreaterEqual, a0, a1);
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
	 *            the header expression of the function. If the ast represents a
	 *            function like <code>f[x,y], Sin[x],...</code>, the
	 *            <code>head</code> will be an instance of type ISymbol.
	 * 
	 */
	public final static IAST headAST0(final IExpr head) {
		return new AST0(head);
	}

	public static IAST Hold(final IExpr a0) {
		return unaryAST1(Hold, a0);
	}

	public static IAST HoldForm(final IExpr a0) {
		return unaryAST1(HoldForm, a0);
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

	public static IAST Increment(final IExpr a) {
		return unaryAST1(Increment, a);
	}

	/**
	 * Convert the symbolName to lowercase (if
	 * <code>Config.PARSER_USE_LOWERCASE_SYMBOLS</code> is set) and insert a new
	 * Symbol in the <code>PREDEFINED_SYMBOLS_MAP</code>. The symbol is created
	 * using the given upper case string to use it as associated class name in
	 * package org.matheclipse.core.reflection.system.
	 * 
	 * @param symbolName
	 *            the predefined symbol name in upper-case form
	 * @return
	 */
	public static ISymbol initFinalHiddenSymbol(final String symbolName) {
		ISymbol temp = new Symbol(symbolName);
		HIDDEN_SYMBOLS_MAP.put(symbolName, temp);
		return temp;
	}

	/**
	 * Convert the symbolName to lowercase (if
	 * <code>Config.PARSER_USE_LOWERCASE_SYMBOLS</code> is set) and insert a new
	 * Symbol in the <code>PREDEFINED_SYMBOLS_MAP</code>. The symbol is created
	 * using the given upper case string to use it as associated class name in
	 * package org.matheclipse.core.reflection.system.
	 * 
	 * @param symbolName
	 *            the predefined symbol name in upper-case form
	 * @return
	 */
	public static IBuiltInSymbol initFinalSymbol(final String symbolName) {
		IBuiltInSymbol temp = new BuiltInSymbol(symbolName);
		Context.SYSTEM.put(symbolName, temp);
		return temp;
	}

	/**
	 * Insert a new Symbol in the <code>SYSTEM</code> context.
	 * 
	 * @param symbolName
	 *            the predefined symbol name in upper-case form
	 * @param evaluator
	 *            defines the evaluation behaviour of the symbol
	 * @return
	 */
	public static IBuiltInSymbol initFinalSymbol(final String symbolName, IEvaluator evaluator) {
		IBuiltInSymbol temp = new BuiltInSymbol(symbolName, evaluator);
		evaluator.setUp(temp);
		Context.SYSTEM.put(symbolName, temp);
		return temp;
	}

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
	 * Initialize the complete System. Calls
	 * {@link #initSymbols(String, ISymbolObserver, boolean)} with parameters
	 * <code>null, null</code>.
	 */
	public synchronized static void initSymbols() {
		initSymbols(null, null, false);
	}

	public synchronized static void initSymbols(Reader reader, ISymbolObserver symbolObserver) {
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

				org.matheclipse.core.builtin.function.Package.loadPackage(EvalEngine.get(), reader);

				isSystemInitialized = true;
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	/**
	 * Initialize the complete System
	 * 
	 * @param fileName
	 *            <code>null</code> or optional text filename, which includes
	 *            the preloaded system rules
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

				if (!noPackageLoading) {
					Reader reader = null;
					if (fileName != null) {
						try {
							reader = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
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
						org.matheclipse.core.builtin.function.Package.loadPackage(EvalEngine.get(), reader);
					}
				}

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
	public static IAST Interval(final ISignedNumber min, ISignedNumber max) {
		return unaryAST1(Interval, binaryAST2(List, min, max));
	}

	public static IAST Inverse(final IExpr a0) {

		return unaryAST1(Inverse, a0);
	}

	public static IAST InverseErf(final IExpr a0) {
		return unaryAST1(InverseErf, a0);
	}

	public static IAST InverseFunction(final IExpr a) {
		return unaryAST1(InverseFunction, a);
	}

	// public static ISymbol method(final String symbolName, final String
	// className, final String methodName) {
	// return new MethodSymbol(symbolName, className, methodName);
	// }
	//
	// public static ISymbol method(final String symbolName, final String
	// packageName, final String className,
	// final String methodName) {
	// return new MethodSymbol(symbolName, packageName, className, methodName);
	// }

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
		org.matheclipse.core.builtin.function.Set.CONST.putDownRule(lhs, rhs, true);
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
		org.matheclipse.core.builtin.function.SetDelayed.putDownRule(lhs, rhs, true);
		return null;
	}

	/**
	 * After a successful <code>isCase()</code> the symbols associated with the
	 * patterns contain the matched values on the local stack.
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

	public static boolean isNumEqualInteger(double value, IInteger ii) throws ArithmeticException {
		long l = ii.toLong();
		return isZero(value - l, Config.DOUBLE_TOLERANCE);
	}

	public static boolean isNumEqualRational(double value, IRational rational) throws ArithmeticException {
		double d = rational.doubleValue();
		return isZero(value - d, Config.DOUBLE_TOLERANCE);
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
	 * @param epsilon
	 * @return
	 */
	public static boolean isZero(double value, double epsilon) {
		return Math.abs(value) < epsilon;
	}

	public static IAST Join(final IExpr a0, final IExpr a1) {
		return binaryAST2(Join, a0, a1);
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

	// public static IAST KOrderlessPartitions(final IExpr a0) {
	//
	// return unaryAST2(KOrderlessPartitions, a0);
	// }
	//
	// public static IAST KPartitions(final IExpr a0) {
	//
	// return unaryAST2(KPartitions, a0);
	// }
	//
	// public static IAST KSubsets(final IExpr a0) {
	//
	// return unaryAST2(KSubsets, a0);
	// }
	//
	public static IAST LeafCount(final IExpr a0) {
		return unaryAST1(LeafCount, a0);
	}

	public static IAST Length(final IExpr a) {
		return unaryAST1(Length, a);
	}

	public static IExpr Less(final IExpr a0, final IExpr a1) {
		if (a0.isSignedNumber() && a1.isSignedNumber()) {
			return ((ISignedNumber) a0).isLessThan(((ISignedNumber) a1)) ? True : False;
		}
		return binaryAST2(Less, a0, a1);
	}

	public static IExpr LessEqual(final IExpr a0, final IExpr a1) {
		if (a0.isSignedNumber() && a1.isSignedNumber()) {
			return ((ISignedNumber) a0).isGreaterThan(((ISignedNumber) a1)) ? False : True;
		}
		return binaryAST2(LessEqual, a0, a1);
	}

	public static IAST Limit(final IExpr a0, final IExpr a1) {

		return binaryAST2(Limit, a0, a1);
	}

	public static IAST Line() {
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

	public static IAST LinearSolve(final IExpr a0, final IExpr a1) {
		return binaryAST2(LinearSolve, a0, a1);
	}

	/**
	 * Create a List() object.
	 * 
	 * @return
	 */
	public static IAST List() {
		return ast(List);
	}

	/**
	 * Create a List() object with allocated space for size elements.
	 * 
	 * @param size
	 * @return
	 */
	public static IAST ListAlloc(int size) {
		return ast(List, size, false);
	}

	public static IAST List(final double... numbers) {
		INum a[] = new INum[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			a[i] = num(numbers[i]);
		}
		return ast(a, List);
	}

	public static IAST List(final IExpr... a) {// 0, final IExpr a1, final IExpr
		// a2) {
		return ast(a, List);
		// return ternary(List, a0, a1, a2);
	}

	public static IAST List(final long... numbers) {
		IInteger a[] = new IInteger[numbers.length];
		for (int i = 0; i < numbers.length; i++) {
			a[i] = integer(numbers[i]);
		}
		return ast(a, List);
	}

	/**
	 * Create a new <code>List</code> with the given <code>capacity</code>.
	 * 
	 * @param capacity
	 *            the assumed number of arguments (+ 1 for the header expression
	 *            is added internally).
	 * @return
	 */
	public static IAST ListC(int capacity) {
		return ast(List, capacity, false);
	}

	public static IAST ListQ(final IExpr a) {
		return unaryAST1(ListQ, a);
	}

	/**
	 * Get a local symbol which is created or retrieved from the eval engines
	 * thread local variables map and push a value on the local stack;
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @param value
	 * @return
	 */
	public static ISymbol local(@Nonnull final String symbolName, @Nonnull IExpr value) {
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

	public static IAST MatrixPower(final IExpr a0, final IExpr a1) {

		return binaryAST2(MatrixPower, a0, a1);
	}

	public static IAST Max() {
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

	public static IAST MemberQ(final IExpr a0, final IExpr a1) {
		return binaryAST2(MemberQ, a0, a1);
	}

	public static IAST MessageName(final IExpr a0, final IExpr a1) {
		return binaryAST2(MessageName, a0, a1);
	}

	public static IAST Min() {
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

	/**
	 * Multiplies the given argument by <code>-1</code>. The
	 * <code>IExpr#negate()</code> method does evaluations, which don't agree
	 * with pattern matching assumptions (in left-hand-sige expressions). so it
	 * is only called called for <code>INumber</code> objects, otherwis a
	 * <code>Times(CN1, x)</code> AST would be created.
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
		return binary(Times, CN1, x);
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
	 *            the header expression of the function. If the ast represents a
	 *            function like <code>f[x,y], Sin[x],...</code>, the
	 *            <code>head</code> will be an instance of type ISymbol.
	 * @return
	 */
	public static IAST newInstance(final int intialArgumentsCapacity, final IExpr head) {
		return AST.newInstance(intialArgumentsCapacity, head);
	}

	public static IAST Norm(final IExpr a) {
		return unaryAST1(Norm, a);
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

	public static IAST Or() {
		return ast(Or);
	}

	public static IAST Or(final IExpr a0, final IExpr a1) {
		return binary(Or, a0, a1);
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

	public static IAST Part(final IExpr... a) {
		IAST part = F.ast(Part, a.length + 1, false);
		for (int i = 0; i < a.length; i++) {
			part.append(a[i]);
		}
		return part;
	}

	public static IAST PatternTest(final IExpr a0, final IExpr a1) {
		return binaryAST2(PatternTest, a0, a1);
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
	public static IAST PlusAlloc(int size) {
		return ast(Plus, size, false);
	}

	public static IAST Plus(final IExpr a0) {
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
				IAST result = PlusAlloc(size);
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
				return binary(Plus, a1, a0);
			}
		}
		return binary(Plus, a0, a1);
	}

	public static IAST Plus(final long num, final IExpr... a) {
		return ast(a, Plus).prependClone(ZZ(num));
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
		temp = new Symbol(lcSymbolName);
		Context.SYSTEM.put(lcSymbolName, temp);
		return temp;
	}

	public static IAST Prepend(final IExpr a0, final IExpr a1) {

		return binaryAST2(Prepend, a0, a1);
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

	public static IAST ProductLog(final IExpr a0) {
		return unaryAST1(ProductLog, a0);
	}

	public static IAST ProductLog(final IExpr a0, final IExpr a1) {
		return binaryAST2(ProductLog, a0, a1);
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

	public final static IAST quaternary(final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2,
			final IExpr a3) {
		return new AST(new IExpr[] { head, a0, a1, a2, a3 });
	}

	public static IAST Quiet(final IExpr a0) {
		return unaryAST1(Quiet, a0);
	}

	public final static IAST quinary(final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
			final IExpr a4) {
		return new AST(new IExpr[] { head, a0, a1, a2, a3, a4 });
	}

	public static IAST Quotient(final IExpr a0, final IExpr a1) {
		return binaryAST2(Quotient, a0, a1);
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

	public static IAST Reap(final IExpr a) {
		return unaryAST1(Reap, a);
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

	/**
	 * Get or create a symbol which is retrieved from the eval engines context
	 * path.
	 * 
	 * @param symbolName
	 *            the name of the symbol
	 * @return the symbol object from the context path
	 */
	public static ISymbol retrieveSymbol(final String symbolName) {
		EvalEngine engine = EvalEngine.get();
		ContextPath contextPath = engine.getContextPath();
		return contextPath.getSymbol(symbolName);
	}

	public static IAST Return(final IExpr a) {
		return unaryAST1(Return, a);
	}

	public static IAST Reverse(final IExpr a) {
		return unaryAST1(Reverse, a);
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

	public static IAST Sequence() {
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
		return unary(Show, a0);
	}

	public static IAST Sign(final IExpr a) {
		return unaryAST1(Sign, a);
	}

	public static IAST SignCmp(final IExpr a0) {
		return unaryAST1(SignCmp, a0);
	}

	public static IAST Simplify(final IExpr a0) {
		return unaryAST1($s("Simplify"), a0);
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

	public static IAST StruveH(final IExpr a0, final IExpr a1) {
		return binaryAST2(StruveH, a0, a1);
	}

	public static IAST StruveL(final IExpr a0, final IExpr a1) {
		return binaryAST2(StruveL, a0, a1);
	}

	public static IAST Subfactorial(final IExpr a0) {
		return unaryAST1(Subfactorial, a0);
	}

	/**
	 * Substitute all (sub-) expressions with the given unary function. If no
	 * substitution matches, the method returns the given <code>expr</code>.
	 * 
	 * @param expr
	 * @param function
	 *            if the unary functions <code>apply()</code> method returns
	 *            <code>null</code> the expression isn't substituted.
	 * @return the input <code>expr</code> if no substitution of a
	 *         (sub-)expression was possible or the substituted expression.
	 */
	public static IExpr subst(IExpr expr, final Function<IExpr, IExpr> function) {
		return expr.replaceAll(function).orElse(expr);
	}

	/**
	 * Substitute all (sub-) expressions with the given rule set. If no
	 * substitution matches, the method returns the given <code>expr</code>.
	 * 
	 * @param expr
	 * @param astRules
	 *            rules of the form <code>x-&gt;y</code> or
	 *            <code>{a-&gt;b, c-&gt;d}</code>; the left-hand-side of the
	 *            rule can contain pattern objects.
	 * @return the input <code>expr</code> if no substitution of a
	 *         (sub-)expression was possible or the substituted expression.
	 */
	public static IExpr subst(IExpr expr, final IAST astRules) {
		return expr.replaceAll(astRules).orElse(expr);
	}

	/**
	 * Substitute all (sub-) expressions with the given replacement expression.
	 * If no (sub-) expression matches, the method returns the given
	 * <code>expr</code>.
	 * 
	 * @param expr
	 * @param subExpr
	 * @param replacementExpr
	 * @return the input <code>expr</code> if no substitution of a
	 *         (sub-)expression was possible or the substituted expression.
	 */
	public static IExpr subst(IExpr expr, IExpr subExpr, IExpr replacementExpr) {
		return expr.replaceAll(Functors.rules(Rule(subExpr, replacementExpr))).orElse(expr);
	}

	public static IAST Subtract(final IExpr a0, final IExpr a1) {
		if (a0.isPlus()) {
			if (a1.isZero()) {
				return (IAST) a0;
			}
			IAST clone = F.PlusAlloc(((IAST) a0).size() + 1);
			clone.appendArgs((IAST) a0);
			clone.append(binary(Times, CN1, a1));
			return clone;
		}
		return binary(Plus, a0, binary(Times, CN1, a1));
	}

	public static IAST Sum(final IExpr a0, final IExpr a1) {

		return binaryAST2(Sum, a0, a1);
	}

	public static IAST SurfaceGraphics() {

		return ast(SurfaceGraphics);
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

	public final static IAST ternary(final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2) {
		return new AST(new IExpr[] { head, a0, a1, a2 });
	}

	/**
	 * Create a function with 3 arguments as a <code>AST3</code> immutable
	 * object without evaluation.
	 * 
	 * @param head
	 * @param a0
	 * @param a1
	 * @param a2
	 * @return
	 */
	public final static IAST ternaryAST3(final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2) {
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
	public static IAST TimesAlloc(int size) {
		return ast(Times, size, false);
	}

	public static IAST Times(final IExpr a0) {
		return unary(Times, a0);
	}

	public static IAST Times(final IExpr... a) {
		return ast(a, Times);
	}

	public static IAST Times(final IExpr a0, final IExpr a1) {
		if (a0 != null && a1 != null) {
			if (a0.isTimes() || a1.isTimes()) {
				int size = 0;
				if (a0.isTimes()) {
					size += ((IAST) a0).size();
				} else {
					size++;
				}
				if (a1.isTimes()) {
					size += ((IAST) a1).size();
				} else {
					size++;
				}
				IAST result = TimesAlloc(size);
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
				return binary(Times, a1, a0);
			}
		}
		return binary(Times, a0, a1);
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
	public final static IAST unary(final IExpr head, final IExpr a0) {
		return new AST(new IExpr[] { head, a0 });
	}

	/**
	 * Create a function with 1 argument as a <code>AST1</code> immutable object
	 * without evaluation.
	 * 
	 * @param head
	 * @param a0
	 * @return
	 */
	public final static IAST unaryAST1(final IExpr head, final IExpr a0) {
		return new AST1(head, a0);
	}

	public static IAST Unequal(final IExpr a0, final IExpr a1) {
		return binary(Unequal, a0, a1);
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

	public static IAST While(final IExpr a0, final IExpr a1) {
		return binaryAST2(While, a0, a1);
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

}