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
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.reflection.system.Package;

import com.google.common.base.Function;

/**
 * Factory for creating MathEclipse expression objects.
 * 
 * See <a
 * href="http://code.google.com/p/symja/wiki/AddNewFunctions">AddNewFunctions
 * </a>
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
	public final static Map<String, String> PREDEFINED_INTERNAL_STRINGS = new HashMap<String, String>(61);

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

	public static ISymbol Abs;

	public static ISymbol And;

	public static ISymbol Append;

	public static ISymbol Apart;

	public static ISymbol Apply;

	public static ISymbol ArcCos;

	public static ISymbol ArcSin;

	public static ISymbol ArcTan;

	public static ISymbol ArcCosh;

	public static ISymbol ArcSinh;

	public static ISymbol ArcTanh;

	public static ISymbol AtomQ;

	public static ISymbol Binomial;

	public static ISymbol Block;

	public static ISymbol Break;

	public static ISymbol Cancel;

	public static ISymbol Ceiling;

	public static ISymbol Complex;

	public static ISymbol CompoundExpression;

	public static ISymbol Condition;

	public static ISymbol Conjugate;

	public static ISymbol Continue;

	public static ISymbol Cos;

	public static ISymbol Cosh;

	public static ISymbol Cot;

	public static ISymbol Coth;

	public static ISymbol Cross;

	public static ISymbol Csc;

	public static ISymbol D;

	public static ISymbol Denominator;

	public static ISymbol Depth;

	public static ISymbol Derivative;

	public static ISymbol Det;

	public static ISymbol Dot;

	public static ISymbol Equal;

	public static ISymbol EvenQ;

	public static ISymbol Expand;

	public static ISymbol ExpandAll;

	public static ISymbol Factor;

	public static ISymbol Factorial;

	public static ISymbol FactorInteger;

	public static ISymbol Fibonacci;

	public static ISymbol FindRoot;

	public static ISymbol First;

	public static ISymbol Floor;

	public static ISymbol FreeQ;

	public static ISymbol FullForm;

	public static ISymbol Function;

	public static ISymbol GCD;

	public static ISymbol Greater;

	public static ISymbol GreaterEqual;

	// public static ISymbol GroebnerBasis;

	public static ISymbol Head;

	public static ISymbol Hold;

	public static ISymbol I;

	public static ISymbol If;

	public static ISymbol Im;

	public static ISymbol IntegerQ;

	public static ISymbol Integrate;

	public static ISymbol Inverse;

	// public static ISymbol KOrderlessPartitions;
	//
	// public static ISymbol KPartitions;
	//
	// public static ISymbol KSubsets;
	//
	public static ISymbol LeafCount;

	public static ISymbol Length;

	public static ISymbol Less;

	public static ISymbol LessEqual;

	public static ISymbol Level;

	public static ISymbol Map;

	public static ISymbol MapAll;

	public static ISymbol MatchQ;

	public static ISymbol MatrixPower;

	public static ISymbol Max;

	public static ISymbol MemberQ;

	public static ISymbol Min;

	public static ISymbol Mod;

	public static ISymbol Module;

	public static ISymbol N;

	public static ISymbol Numerator;

	public static ISymbol Negative;

	public static ISymbol NonNegative;

	public static ISymbol Not;

	// public static ISymbol NumberPartitions;

	public static ISymbol NumberQ;

	public static ISymbol NumericQ;

	public static ISymbol OddQ;

	public static ISymbol Or;

	public static ISymbol Order;

	public static ISymbol OrderedQ;

	public final static ISymbol Part = initPredefinedSymbol("Part");

	// public static ISymbol Partition;
	//
	// public static ISymbol Permutations;

	public static ISymbol Plot;

	public static ISymbol Plot3D;

	public static ISymbol Plus;

	public static ISymbol PolynomialQ;
	
	public static ISymbol Positive;

	public static ISymbol PossibleZeroQ;

	public static ISymbol Power;

	public static ISymbol Prepend;

	public static ISymbol PrimeQ;

	public static ISymbol Print;

	public static ISymbol Product;

	public static ISymbol Quotient;

	public static ISymbol Rational;

	public static ISymbol Re;

	public static ISymbol Rest;

	public static ISymbol ReplaceAll;

	public static ISymbol Reverse;

	public static ISymbol RootOf;

	public static ISymbol RotateLeft;

	public static ISymbol RotateRight;

	public static ISymbol Rule;

	public static ISymbol RuleDelayed;

	public static ISymbol SameQ;
	
	public static ISymbol Sec;

	public static ISymbol Sequence;

	public static ISymbol Set;

	public static ISymbol SetAttributes;

	public static ISymbol SetDelayed;

	public static ISymbol Sign;

	public static ISymbol SignCmp;

	public static ISymbol Sin;

	public static ISymbol Sinh;

	public static ISymbol Sort;

	public static ISymbol Sqrt;

	public static ISymbol Sum;

	public static ISymbol Tan;

	public static ISymbol Tanh;

	public static ISymbol Taylor;

	public static ISymbol Times;

	public static ISymbol Timing;

	public static ISymbol Together;

	/**
	 * Trace of a matrix
	 */
	public static ISymbol Tr;

	/**
	 * Trace of the execution in the evaluation engine
	 */
	public static ISymbol Trace;

	/**
	 * Transpose a matrix
	 */
	public static ISymbol Transpose;

	public static ISymbol TrueQ;

	public static ISymbol Trunc;

	public static ISymbol Unequal;
	
	public static ISymbol UnsameQ;

	public static ISymbol While;

	public final static ISymbol a = initPredefinedSymbol("a");
	public final static ISymbol b = initPredefinedSymbol("b");
	public final static ISymbol c = initPredefinedSymbol("c");
	public final static ISymbol d = initPredefinedSymbol("d");
	public final static ISymbol e = initPredefinedSymbol("e");
	public final static ISymbol f = initPredefinedSymbol("f");
	public final static ISymbol g = initPredefinedSymbol("g");
	public final static ISymbol h = initPredefinedSymbol("h");
	public final static ISymbol i = initPredefinedSymbol("i");
	public final static ISymbol j = initPredefinedSymbol("j");
	public final static ISymbol k = initPredefinedSymbol("k");
	public final static ISymbol l = initPredefinedSymbol("l");
	public final static ISymbol m = initPredefinedSymbol("m");
	public final static ISymbol n = initPredefinedSymbol("n");
	public final static ISymbol o = initPredefinedSymbol("o");
	public final static ISymbol p = initPredefinedSymbol("p");
	public final static ISymbol q = initPredefinedSymbol("q");
	public final static ISymbol r = initPredefinedSymbol("r");
	public final static ISymbol s = initPredefinedSymbol("s");
	public final static ISymbol t = initPredefinedSymbol("t");
	public final static ISymbol u = initPredefinedSymbol("u");
	public final static ISymbol v = initPredefinedSymbol("v");
	public final static ISymbol w = initPredefinedSymbol("w");
	public final static ISymbol x = initPredefinedSymbol("x");
	public final static ISymbol y = initPredefinedSymbol("y");
	public final static ISymbol z = initPredefinedSymbol("z");

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

	/**
	 * * Constant integer &quot;0&quot;
	 */
	public static IInteger C0;

	/**
	 * Constant integer &quot;1&quot;
	 */
	public static IInteger C1;

	/**
	 * Constant integer &quot;2&quot;
	 */
	public static IInteger C2;

	/**
	 * Constant integer &quot;3&quot;
	 */
	public static IInteger C3;

	/**
	 * Constant integer &quot;4&quot;
	 */
	public static IInteger C4;

	/**
	 * Constant integer &quot;5&quot;
	 */
	public static IInteger C5;

	/**
	 * Complex imaginary unit. The parsed symbol &quot;I&quot; is converted on
	 * input to this constant.
	 */
	public static IComplex CI;

	/**
	 * Complex negative imaginary unit.
	 */
	public static IComplex CNI;

	/**
	 * Constant fraction &quot;1/2&quot;
	 */
	public static IFraction C1D2;

	/**
	 * Constant fraction &quot;-1/2&quot;
	 */
	public static IFraction CN1D2;

	/**
	 * Constant fraction &quot;1/3&quot;
	 */
	public static IFraction C1D3;

	/**
	 * Constant fraction &quot;-1/3&quot;
	 */
	public static IFraction CN1D3;

	/**
	 * Constant fraction &quot;1/4&quot;
	 */
	public static IFraction C1D4;

	/**
	 * Constant fraction &quot;-1/4&quot;
	 */
	public static IFraction CN1D4;

	/**
	 * Constant double &quot;0.0&quot;
	 */
	public static INum CD0;

	/**
	 * Constant double &quot;1.0&quot;
	 */
	public static INum CD1;

	public static IAST CInfinity;
	public static IAST CNInfinity;
	public static IAST Slot1;
	public static IAST Slot2;

	/**
	 * Constant integer &quot;-1&quot;
	 */
	public static IInteger CN1;

	public static ISymbol ComplexInfinity;

	public static ISymbol Constant;

	public static ISymbol DirectedInfinity;

	public static ISymbol False;

	/**
	 * Attribute for associative functions (i.e. Dot, Times, Plus,...)
	 */
	public static ISymbol Flat;

	/**
	 * Attribute for <i><b>don't evaluate the arguments</b></i> of a function
	 */
	public static ISymbol HoldAll;

	/**
	 * Attribute for <i><b>don't evaluate the first argument</b></i> of a
	 * function
	 */
	public static ISymbol HoldFirst;

	/**
	 * Attribute for <i><b>only evaluate the first argument</b></i> of a
	 * function
	 */
	public static ISymbol HoldRest;

	public static ISymbol Indeterminate;

	public static ISymbol Infinity;

	public static ISymbol Line;

	public static ISymbol Limit;
	/**
	 * Head-Symbol for lists (i.e. <code>{a,b,c} ~~ List[a,b,c]</code>)
	 */
	public static ISymbol List;

	/**
	 * Attribute for listable functions (i.e. Sin, Cos,...)
	 */
	public static ISymbol Listable;

	public static ISymbol NHoldAll;

	public static ISymbol NHoldFirst;

	public static ISymbol NHoldRest;

	public static ISymbol Null;

	public static ISymbol NumericFunction;

	public static ISymbol OneIdentity;

	/**
	 * Attribute for commutative functions (i.e. Times, Plus,...)
	 */
	public static ISymbol Orderless;

	public static ISymbol E;

	public static ISymbol Pi;

	public static ISymbol Log;

	public static ISymbol True;

	public static ISymbol Second;

	public static ISymbol BoxRatios;

	public static ISymbol MeshRange;

	public static ISymbol PlotRange;

	public static ISymbol AxesStyle;

	public static ISymbol Automatic;

	public static ISymbol AxesOrigin;

	public static ISymbol Axes;

	public static ISymbol Background;

	public static ISymbol Blank;

	public static ISymbol White;

	public static ISymbol Slot;

	public static ISymbol SlotSequence;

	public static ISymbol Options;

	public static ISymbol Graphics;

	public static ISymbol SurfaceGraphics;

	public static ISymbol Show;

	public static ISymbol IntegerHead;

	public static ISymbol RationalHead;

	public static ISymbol SymbolHead;

	public static ISymbol RealHead;

	public static ISymbol ComplexHead;

	public static ISymbol PatternHead;

	public static ISymbol BlankHead;

	public static ISymbol StringHead;

	public static ISymbol MethodHead;

	// --- generated function symbols

	static {

		try {
			if (Config.DEBUG) {
				System.out.println("Config.DEBUG == true");
			}
			if (Config.SHOW_STACKTRACE) {
				System.out.println("Config.SHOW_STACKTRACE == true");
			}
			// long start = System.currentTimeMillis();

			C0 = IntegerSym.valueOf(0);
			C1 = IntegerSym.valueOf(1);
			C2 = IntegerSym.valueOf(2);
			C3 = IntegerSym.valueOf(3);
			C4 = IntegerSym.valueOf(4);
			C5 = IntegerSym.valueOf(5);
			CN1 = IntegerSym.valueOf(-1);

			C1D2 = FractionSym.valueOf(1, 2);
			C1D3 = FractionSym.valueOf(1, 3);
			C1D4 = FractionSym.valueOf(1, 4);
			CN1D2 = FractionSym.valueOf(-1, 2);
			CN1D3 = FractionSym.valueOf(-1, 3);
			CN1D4 = FractionSym.valueOf(-1, 4);

			CI = ComplexSym.valueOf(BigInteger.ZERO, BigInteger.ONE);
			CNI = ComplexSym.valueOf(BigInteger.ZERO, BigInteger.valueOf(-1L));

			CD0 = Num.valueOf(0.0);
			CD1 = Num.valueOf(1.0);
			// a = initPredefinedSymbol("a");
			// b = initPredefinedSymbol("b");
			// c = initPredefinedSymbol("c");
			// d = initPredefinedSymbol("d");
			// e = initPredefinedSymbol("e");
			// f = initPredefinedSymbol("f");
			// g = initPredefinedSymbol("g");
			// h = initPredefinedSymbol("h");
			// i = initPredefinedSymbol("i");
			// j = initPredefinedSymbol("j");
			// k = initPredefinedSymbol("k");
			// l = initPredefinedSymbol("l");
			// m = initPredefinedSymbol("m");
			// n = initPredefinedSymbol("n");
			// o = initPredefinedSymbol("o");
			// p = initPredefinedSymbol("p");
			// q = initPredefinedSymbol("q");
			// r = initPredefinedSymbol("r");
			// s = initPredefinedSymbol("s");
			// t = initPredefinedSymbol("t");
			// u = initPredefinedSymbol("u");
			// v = initPredefinedSymbol("v");
			// w = initPredefinedSymbol("w");
			// x = initPredefinedSymbol("x");
			// y = initPredefinedSymbol("y");
			// z = initPredefinedSymbol("z");

			// a_ = new Pattern(a);
			// b_ = new Pattern(b);
			// c_ = new Pattern(c);
			// d_ = new Pattern(d);
			// e_ = new Pattern(e);
			// f_ = new Pattern(f);
			// g_ = new Pattern(g);
			// h_ = new Pattern(h);
			// i_ = new Pattern(i);
			// j_ = new Pattern(j);
			// k_ = new Pattern(k);
			// l_ = new Pattern(l);
			// m_ = new Pattern(m);
			// n_ = new Pattern(n);
			// o_ = new Pattern(o);
			// p_ = new Pattern(p);
			// q_ = new Pattern(q);
			// r_ = new Pattern(r);
			// s_ = new Pattern(s);
			// t_ = new Pattern(t);
			// u_ = new Pattern(u);
			// v_ = new Pattern(v);
			// w_ = new Pattern(w);
			// x_ = new Pattern(x);
			// y_ = new Pattern(y);
			// z_ = new Pattern(z);

			// PREDEFINED_PATTERN_MAP.put("a", a_);
			// PREDEFINED_PATTERN_MAP.put("b", b_);
			// PREDEFINED_PATTERN_MAP.put("c", c_);
			// PREDEFINED_PATTERN_MAP.put("d", d_);
			// PREDEFINED_PATTERN_MAP.put("e", e_);
			// PREDEFINED_PATTERN_MAP.put("f", f_);
			// PREDEFINED_PATTERN_MAP.put("g", g_);
			// PREDEFINED_PATTERN_MAP.put("h", h_);
			// PREDEFINED_PATTERN_MAP.put("i", i_);
			// PREDEFINED_PATTERN_MAP.put("j", j_);
			// PREDEFINED_PATTERN_MAP.put("k", k_);
			// PREDEFINED_PATTERN_MAP.put("l", l_);
			// PREDEFINED_PATTERN_MAP.put("m", m_);
			// PREDEFINED_PATTERN_MAP.put("n", n_);
			// PREDEFINED_PATTERN_MAP.put("o", o_);
			// PREDEFINED_PATTERN_MAP.put("p", p_);
			// PREDEFINED_PATTERN_MAP.put("q", q_);
			// PREDEFINED_PATTERN_MAP.put("r", r_);
			// PREDEFINED_PATTERN_MAP.put("s", s_);
			// PREDEFINED_PATTERN_MAP.put("t", t_);
			// PREDEFINED_PATTERN_MAP.put("u", u_);
			// PREDEFINED_PATTERN_MAP.put("v", v_);
			// PREDEFINED_PATTERN_MAP.put("w", w_);
			// PREDEFINED_PATTERN_MAP.put("x", x_);
			// PREDEFINED_PATTERN_MAP.put("y", y_);
			// PREDEFINED_PATTERN_MAP.put("z", z_);

			/**
			 * Define the &quot;set symbols&quot; first, because of dependencies
			 * in the predefined rules
			 */
			Set = initPredefinedSymbol("Set");
			SetDelayed = initPredefinedSymbol("SetDelayed");

			Plus = initPredefinedSymbol("Plus");
			Times = initPredefinedSymbol("Times");
			Power = initPredefinedSymbol("Power");

			List = initPredefinedSymbol(IConstantHeaders.List);
			Log = initPredefinedSymbol(IConstantHeaders.Log);
			True = initPredefinedSymbol(IConstantHeaders.True);
			False = initPredefinedSymbol(IConstantHeaders.False);
			Null = initPredefinedSymbol(IConstantHeaders.Null);
			E = initPredefinedSymbol(IConstantHeaders.E);
			Pi = initPredefinedSymbol(IConstantHeaders.Pi);
			Second = initPredefinedSymbol(IConstantHeaders.Second);
			Indeterminate = initPredefinedSymbol("Indeterminate");
			Infinity = initPredefinedSymbol(IConstantHeaders.Infinity);
			ComplexInfinity = initPredefinedSymbol(IConstantHeaders.ComplexInfinity);
			DirectedInfinity = initPredefinedSymbol(IConstantHeaders.DirectedInfinity);

			Listable = initPredefinedSymbol(IConstantHeaders.Listable);
			Constant = initPredefinedSymbol(IConstantHeaders.Constant);
			NumericFunction = initPredefinedSymbol(IConstantHeaders.NumericFunction);
			Orderless = initPredefinedSymbol(IConstantHeaders.Orderless);
			OneIdentity = initPredefinedSymbol(IConstantHeaders.OneIdentity);
			Flat = initPredefinedSymbol(IConstantHeaders.Flat);
			HoldFirst = initPredefinedSymbol(IConstantHeaders.HoldFirst);
			HoldRest = initPredefinedSymbol(IConstantHeaders.HoldRest);
			HoldAll = initPredefinedSymbol(IConstantHeaders.HoldAll);
			NHoldFirst = initPredefinedSymbol(IConstantHeaders.NHoldFirst);
			NHoldRest = initPredefinedSymbol(IConstantHeaders.NHoldRest);
			NHoldAll = initPredefinedSymbol(IConstantHeaders.NHoldAll);

			Line = initPredefinedSymbol(IConstantHeaders.Line);
			BoxRatios = initPredefinedSymbol(IConstantHeaders.BoxRatios);
			MeshRange = initPredefinedSymbol(IConstantHeaders.MeshRange);
			PlotRange = initPredefinedSymbol(IConstantHeaders.PlotRange);

			AxesStyle = initPredefinedSymbol(IConstantHeaders.AxesStyle);
			Automatic = initPredefinedSymbol(IConstantHeaders.Automatic);
			AxesOrigin = initPredefinedSymbol(IConstantHeaders.AxesOrigin);
			Axes = initPredefinedSymbol(IConstantHeaders.Axes);
			Background = initPredefinedSymbol(IConstantHeaders.Background);
			White = initPredefinedSymbol(IConstantHeaders.White);

			// _Failed = createinitSymbol("$Failed");

			IntegerHead = initPredefinedSymbol(IConstantHeaders.IntegerHead);
			RationalHead = initPredefinedSymbol(IConstantHeaders.RationalHead);
			SymbolHead = initPredefinedSymbol(IConstantHeaders.SymbolHead);
			RealHead = initPredefinedSymbol(IConstantHeaders.RealHead);
			ComplexHead = initPredefinedSymbol(IConstantHeaders.ComplexHead);
			PatternHead = initPredefinedSymbol(IConstantHeaders.PatternHead);
			BlankHead = initPredefinedSymbol(IConstantHeaders.BlankHead);
			StringHead = initPredefinedSymbol(IConstantHeaders.StringHead);
			MethodHead = initPredefinedSymbol(IConstantHeaders.MethodHead);

			Slot = initPredefinedSymbol("Slot");
			Slot.setAttributes(ISymbol.NHOLDALL);
			SlotSequence = initPredefinedSymbol("SlotSequence");
			SlotSequence.setAttributes(ISymbol.NHOLDALL);
			Options = initPredefinedSymbol("Options");
			Graphics = initPredefinedSymbol("Graphics");
			ReplaceAll = initPredefinedSymbol("ReplaceAll");
			Show = initPredefinedSymbol("Show");
			SurfaceGraphics = initPredefinedSymbol("SurfaceGraphics");

			// generated symbols
			Abs = initPredefinedSymbol("Abs");
			And = initPredefinedSymbol("And");
			Append = initPredefinedSymbol("Append");
			Apart = initPredefinedSymbol("Apart");
			Apply = initPredefinedSymbol("Apply");
			ArcCos = initPredefinedSymbol("ArcCos");
			ArcSin = initPredefinedSymbol("ArcSin");
			ArcTan = initPredefinedSymbol("ArcTan");
			ArcCosh = initPredefinedSymbol("ArcCosh");
			ArcSinh = initPredefinedSymbol("ArcSinh");
			ArcTanh = initPredefinedSymbol("ArcTanh");
			AtomQ = initPredefinedSymbol("AtomQ");
			Binomial = initPredefinedSymbol("Binomial");
			Blank = initPredefinedSymbol("Blank");
			Block = initPredefinedSymbol("Block");
			Break = initPredefinedSymbol("Break");
			Cancel = initPredefinedSymbol("Cancel");
			Csc = initPredefinedSymbol("Csc");
			Ceiling = initPredefinedSymbol("Ceiling");
			CompoundExpression = initPredefinedSymbol("CompoundExpression");
			Condition = initPredefinedSymbol("Condition");
			Conjugate = initPredefinedSymbol("Conjugate");
			Continue = initPredefinedSymbol("Continue");
			Cos = initPredefinedSymbol("Cos");
			Cosh = initPredefinedSymbol("Cosh");
			Cot = initPredefinedSymbol("Cot");
			Coth = initPredefinedSymbol("Coth");
			Cross = initPredefinedSymbol("Cross");
			D = initPredefinedSymbol("D");
			Denominator = initPredefinedSymbol("Denominator");
			Derivative = initPredefinedSymbol("Derivative");
			Det = initPredefinedSymbol("Det");
			Dot = initPredefinedSymbol("Dot");
			Equal = initPredefinedSymbol("Equal");
			EvenQ = initPredefinedSymbol("EvenQ");
			Expand = initPredefinedSymbol("Expand");
			ExpandAll = initPredefinedSymbol("ExpandAll");
			Factor = initPredefinedSymbol("Factor");
			Factorial = initPredefinedSymbol("Factorial");
			FactorInteger = initPredefinedSymbol("FactorInteger");
			Fibonacci = initPredefinedSymbol("Fibonacci");
			FindRoot = initPredefinedSymbol("FindRoot");
			First = initPredefinedSymbol("First");
			Floor = initPredefinedSymbol("Floor");
			FreeQ = initPredefinedSymbol("FreeQ");
			FullForm = initPredefinedSymbol("FullForm");
			Function = initPredefinedSymbol("Function");
			GCD = initPredefinedSymbol("GCD");
			Greater = initPredefinedSymbol("Greater");
			GreaterEqual = initPredefinedSymbol("GreaterEqual");
			// GroebnerBasis = initSymbol("GroebnerBasis", new
			// GroebnerBasis());
			Head = initPredefinedSymbol("Head");
			Hold = initPredefinedSymbol("Hold");
			I = initPredefinedSymbol("I");
			If = initPredefinedSymbol("If");
			Im = initPredefinedSymbol("Im");
			IntegerQ = initPredefinedSymbol("IntegerQ");
			Integrate = initPredefinedSymbol("Integrate");
			Inverse = initPredefinedSymbol("Inverse");
			// KOrderlessPartitions =
			// initSymbol("KOrderlessPartitions", new
			// KOrderlessPartitions());
			// KPartitions = initSymbol("KPartitions", new
			// KPartitions());
			// KSubsets = initSymbol("KSubsets", new KSubsets());
			LeafCount = initPredefinedSymbol("LeafCount");
			Length = initPredefinedSymbol("Length");
			Less = initPredefinedSymbol("Less");
			LessEqual = initPredefinedSymbol("LessEqual");
			Level = initPredefinedSymbol("Level");
			Limit = initPredefinedSymbol("Limit");
			Map = initPredefinedSymbol("Map");
			MapAll = initPredefinedSymbol("MapAll");
			MatchQ = initPredefinedSymbol("MatchQ");
			MatrixPower = initPredefinedSymbol("MatrixPower");
			Max = initPredefinedSymbol("Max");
			MemberQ = initPredefinedSymbol("MemberQ");
			Min = initPredefinedSymbol("Min");
			Mod = initPredefinedSymbol("Mod");
			Module = initPredefinedSymbol("Module");
			N = initPredefinedSymbol("N");
			Negative = initPredefinedSymbol("Negative");
			NonNegative = initPredefinedSymbol("NonNegative");
			Not = initPredefinedSymbol("Not");
			// NumberPartitions = initSymbol("NumberPartitions", new
			// NumberPartitions());
			NumberQ = initPredefinedSymbol("NumberQ");
			NumericQ = initPredefinedSymbol("NumericQ");
			Numerator = initPredefinedSymbol("Numerator");
			OddQ = initPredefinedSymbol("OddQ");
			Or = initPredefinedSymbol("Or");
			Order = initPredefinedSymbol("Order");
			OrderedQ = initPredefinedSymbol("OrderedQ");
			// Part = initPredefinedSymbol("Part");
			// Partition = initSymbol("Partition", new Partition());
			// Permutations = initSymbol("Permutations", new
			// Permutations());
			Plot = initPredefinedSymbol("Plot");
			Plot3D = initPredefinedSymbol("Plot3D");
			PolynomialQ = initPredefinedSymbol("PolynomialQ");
			Positive = initPredefinedSymbol("Positive");
			PossibleZeroQ = initPredefinedSymbol("PossibleZeroQ");

			Prepend = initPredefinedSymbol("Prepend");
			PrimeQ = initPredefinedSymbol("PrimeQ");
			Print = initPredefinedSymbol("Print");
			Product = initPredefinedSymbol("Product");
			Quotient = initPredefinedSymbol("Quotient");
			Re = initPredefinedSymbol("Re");
			Rest = initPredefinedSymbol("Rest");
			Reverse = initPredefinedSymbol("Reverse");
			RootOf = initPredefinedSymbol("RootOf");
			RotateLeft = initPredefinedSymbol("RotateLeft");
			RotateRight = initPredefinedSymbol("RotateRight");
			Rule = initPredefinedSymbol("Rule");
			RuleDelayed = initPredefinedSymbol("RuleDelayed");
			SameQ = initPredefinedSymbol("SameQ");
			Sec = initPredefinedSymbol("Sec");
			Sequence = initPredefinedSymbol("Sequence");
			SetAttributes = initPredefinedSymbol("SetAttributes");
			Sign = initPredefinedSymbol("Sign");
			SignCmp = initPredefinedSymbol("SignCmp");
			Sin = initPredefinedSymbol("Sin");
			Sinh = initPredefinedSymbol("Sinh");
			Sort = initPredefinedSymbol("Sort");
			Sqrt = initPredefinedSymbol("Sqrt");
			Sum = initPredefinedSymbol("Sum");
			Tan = initPredefinedSymbol("Tan");
			Tanh = initPredefinedSymbol("Tanh");

			Taylor = initPredefinedSymbol("Taylor");
			Timing = initPredefinedSymbol("Timing");
			Together = initPredefinedSymbol("Together");
			Tr = initPredefinedSymbol("Tr");
			Trace = initPredefinedSymbol("Trace");
			Transpose = initPredefinedSymbol("Transpose");
			TrueQ = initPredefinedSymbol("TrueQ");
			Trunc = initPredefinedSymbol("Trunc");
			Unequal = initPredefinedSymbol("Unequal");
			UnsameQ = initPredefinedSymbol("UnsameQ");
			While = initPredefinedSymbol("While");

			CInfinity = $(DirectedInfinity, C1);
			CNInfinity = $(DirectedInfinity, CN1);
			Slot1 = $(Slot, C1);
			Slot2 = $(Slot, C2);

			// if (symbolObserver != null) {
			// SYMBOL_OBSERVER = symbolObserver;
			// }
			//
			// if (!noPackageLoading) {
			// Reader reader = null;
			// if (fileName != null) {
			// try {
			// reader = new FileReader(fileName);
			// } catch (FileNotFoundException e) {
			// e.printStackTrace();
			// }
			// }
			// if (reader == null) {
			// InputStream systemPackage =
			// F.class.getResourceAsStream("/System.mep");
			// if (systemPackage != null) {
			// reader = new InputStreamReader(systemPackage);
			// }
			// }
			// if (reader != null) {
			// Package.loadPackage(EvalEngine.get(), reader);
			// }
			// }
			PREDEFINED_INTERNAL_STRINGS.put("Pi", "Pi");
			PREDEFINED_INTERNAL_STRINGS.put("E", "E");
			PREDEFINED_INTERNAL_STRINGS.put("False", "False");
			PREDEFINED_INTERNAL_STRINGS.put("True", "True");
			PREDEFINED_INTERNAL_STRINGS.put("Null", "Null");

			Plus.setDefaultValue(C0);
			Plus.setEvaluator(org.matheclipse.core.reflection.system.Plus.CONST);
			Times.setDefaultValue(C1);
			Times.setEvaluator(org.matheclipse.core.reflection.system.Times.CONST);
			Power.setDefaultValue(2, C1);
			Power.setEvaluator(org.matheclipse.core.reflection.system.Power.CONST);
			Integrate.setEvaluator(org.matheclipse.core.reflection.system.Integrate.CONST);

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

	public static IAST SameQ(final IExpr a0,final IExpr a1) {
		return binary(SameQ, a0, a1);
	}
	
	public static IAST Sec(final IExpr a0) {
		return unary(Sec, a0);
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
	 * Initialize the complete System. Calls
	 * {@link #initSymbols(String, ISymbolObserver, boolean)} with parameters
	 * <code>null, null</code>.
	 */
	public synchronized static void initSymbols() {
		initSymbols(null, null, false);
	}

	/**
	 * Initialize the complete System. Calls
	 * {@link #initSymbols(String, ISymbolObserver, boolean)} with parameters
	 * <code>fileName, null</code>.
	 * 
	 * @param fileName
	 */
	public synchronized static void initSymbols(String fileName) {
		initSymbols(fileName, null, false);
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
	public synchronized static void initSymbols(String fileName, ISymbolObserver symbolObserver, boolean noPackageLoading) {

		if (!isSystemStarted) {
			try {
				isSystemStarted = true;
				if (Config.DEBUG) {
					System.out.println("Config.DEBUG == true");
				}
				if (Config.SHOW_STACKTRACE) {
					System.out.println("Config.SHOW_STACKTRACE == true");
				}
				// long start = System.currentTimeMillis();

				// C0 = IntegerSym.valueOf(0);
				// C1 = IntegerSym.valueOf(1);
				// C2 = IntegerSym.valueOf(2);
				// C3 = IntegerSym.valueOf(3);
				// C4 = IntegerSym.valueOf(4);
				// C5 = IntegerSym.valueOf(5);
				// CN1 = IntegerSym.valueOf(-1);
				//
				// C1D2 = FractionSym.valueOf(1, 2);
				// C1D3 = FractionSym.valueOf(1, 3);
				// C1D4 = FractionSym.valueOf(1, 4);
				// CN1D2 = FractionSym.valueOf(-1, 2);
				// CN1D3 = FractionSym.valueOf(-1, 3);
				// CN1D4 = FractionSym.valueOf(-1, 4);
				//
				// CI = ComplexSym.valueOf(BigInteger.ZERO, BigInteger.ONE);
				// CNI = ComplexSym.valueOf(BigInteger.ZERO,
				// BigInteger.valueOf(-1L));
				//
				// CD0 = Num.valueOf(0.0);
				// CD1 = Num.valueOf(1.0);
				// a = initPredefinedSymbol("a");
				// b = initPredefinedSymbol("b");
				// c = initPredefinedSymbol("c");
				// d = initPredefinedSymbol("d");
				// e = initPredefinedSymbol("e");
				// f = initPredefinedSymbol("f");
				// g = initPredefinedSymbol("g");
				// h = initPredefinedSymbol("h");
				// i = initPredefinedSymbol("i");
				// j = initPredefinedSymbol("j");
				// k = initPredefinedSymbol("k");
				// l = initPredefinedSymbol("l");
				// m = initPredefinedSymbol("m");
				// n = initPredefinedSymbol("n");
				// o = initPredefinedSymbol("o");
				// p = initPredefinedSymbol("p");
				// q = initPredefinedSymbol("q");
				// r = initPredefinedSymbol("r");
				// s = initPredefinedSymbol("s");
				// t = initPredefinedSymbol("t");
				// u = initPredefinedSymbol("u");
				// v = initPredefinedSymbol("v");
				// w = initPredefinedSymbol("w");
				// x = initPredefinedSymbol("x");
				// y = initPredefinedSymbol("y");
				// z = initPredefinedSymbol("z");
				//
				// a_ = new Pattern(a);
				// b_ = new Pattern(b);
				// c_ = new Pattern(c);
				// d_ = new Pattern(d);
				// e_ = new Pattern(e);
				// f_ = new Pattern(f);
				// g_ = new Pattern(g);
				// h_ = new Pattern(h);
				// i_ = new Pattern(i);
				// j_ = new Pattern(j);
				// k_ = new Pattern(k);
				// l_ = new Pattern(l);
				// m_ = new Pattern(m);
				// n_ = new Pattern(n);
				// o_ = new Pattern(o);
				// p_ = new Pattern(p);
				// q_ = new Pattern(q);
				// r_ = new Pattern(r);
				// s_ = new Pattern(s);
				// t_ = new Pattern(t);
				// u_ = new Pattern(u);
				// v_ = new Pattern(v);
				// w_ = new Pattern(w);
				// x_ = new Pattern(x);
				// y_ = new Pattern(y);
				// z_ = new Pattern(z);
				//
				// PREDEFINED_PATTERN_MAP.put("a", a_);
				// PREDEFINED_PATTERN_MAP.put("b", b_);
				// PREDEFINED_PATTERN_MAP.put("c", c_);
				// PREDEFINED_PATTERN_MAP.put("d", d_);
				// PREDEFINED_PATTERN_MAP.put("e", e_);
				// PREDEFINED_PATTERN_MAP.put("f", f_);
				// PREDEFINED_PATTERN_MAP.put("g", g_);
				// PREDEFINED_PATTERN_MAP.put("h", h_);
				// PREDEFINED_PATTERN_MAP.put("i", i_);
				// PREDEFINED_PATTERN_MAP.put("j", j_);
				// PREDEFINED_PATTERN_MAP.put("k", k_);
				// PREDEFINED_PATTERN_MAP.put("l", l_);
				// PREDEFINED_PATTERN_MAP.put("m", m_);
				// PREDEFINED_PATTERN_MAP.put("n", n_);
				// PREDEFINED_PATTERN_MAP.put("o", o_);
				// PREDEFINED_PATTERN_MAP.put("p", p_);
				// PREDEFINED_PATTERN_MAP.put("q", q_);
				// PREDEFINED_PATTERN_MAP.put("r", r_);
				// PREDEFINED_PATTERN_MAP.put("s", s_);
				// PREDEFINED_PATTERN_MAP.put("t", t_);
				// PREDEFINED_PATTERN_MAP.put("u", u_);
				// PREDEFINED_PATTERN_MAP.put("v", v_);
				// PREDEFINED_PATTERN_MAP.put("w", w_);
				// PREDEFINED_PATTERN_MAP.put("x", x_);
				// PREDEFINED_PATTERN_MAP.put("y", y_);
				// PREDEFINED_PATTERN_MAP.put("z", z_);
				//
				// /**
				// * Define the &quot;set symbols&quot; first, because of
				// * dependencies in the predefined rules
				// */
				// Set = initPredefinedSymbol("Set");
				// SetDelayed = initPredefinedSymbol("SetDelayed");
				//
				// Plus = initPredefinedSymbol("Plus");
				// Times = initPredefinedSymbol("Times");
				// Power = initPredefinedSymbol("Power");
				//
				// List = initPredefinedSymbol(IConstantHeaders.List);
				// Log = initPredefinedSymbol(IConstantHeaders.Log);
				// True = initPredefinedSymbol(IConstantHeaders.True);
				// False = initPredefinedSymbol(IConstantHeaders.False);
				// Null = initPredefinedSymbol(IConstantHeaders.Null);
				// E = initPredefinedSymbol(IConstantHeaders.E);
				// Pi = initPredefinedSymbol(IConstantHeaders.Pi);
				// Second = initPredefinedSymbol(IConstantHeaders.Second);
				// Indeterminate = initPredefinedSymbol("Indeterminate");
				// Infinity = initPredefinedSymbol(IConstantHeaders.Infinity);
				// ComplexInfinity =
				// initPredefinedSymbol(IConstantHeaders.ComplexInfinity);
				// DirectedInfinity =
				// initPredefinedSymbol(IConstantHeaders.DirectedInfinity);
				//
				// Listable = initPredefinedSymbol(IConstantHeaders.Listable);
				// Constant = initPredefinedSymbol(IConstantHeaders.Constant);
				// NumericFunction =
				// initPredefinedSymbol(IConstantHeaders.NumericFunction);
				// Orderless = initPredefinedSymbol(IConstantHeaders.Orderless);
				// OneIdentity =
				// initPredefinedSymbol(IConstantHeaders.OneIdentity);
				// Flat = initPredefinedSymbol(IConstantHeaders.Flat);
				// HoldFirst = initPredefinedSymbol(IConstantHeaders.HoldFirst);
				// HoldRest = initPredefinedSymbol(IConstantHeaders.HoldRest);
				// HoldAll = initPredefinedSymbol(IConstantHeaders.HoldAll);
				// NHoldFirst =
				// initPredefinedSymbol(IConstantHeaders.NHoldFirst);
				// NHoldRest = initPredefinedSymbol(IConstantHeaders.NHoldRest);
				// NHoldAll = initPredefinedSymbol(IConstantHeaders.NHoldAll);
				//
				// Line = initPredefinedSymbol(IConstantHeaders.Line);
				// BoxRatios = initPredefinedSymbol(IConstantHeaders.BoxRatios);
				// MeshRange = initPredefinedSymbol(IConstantHeaders.MeshRange);
				// PlotRange = initPredefinedSymbol(IConstantHeaders.PlotRange);
				//
				// AxesStyle = initPredefinedSymbol(IConstantHeaders.AxesStyle);
				// Automatic = initPredefinedSymbol(IConstantHeaders.Automatic);
				// AxesOrigin =
				// initPredefinedSymbol(IConstantHeaders.AxesOrigin);
				// Axes = initPredefinedSymbol(IConstantHeaders.Axes);
				// Background =
				// initPredefinedSymbol(IConstantHeaders.Background);
				// White = initPredefinedSymbol(IConstantHeaders.White);
				//
				// // _Failed = createinitSymbol("$Failed");
				//
				// IntegerHead =
				// initPredefinedSymbol(IConstantHeaders.IntegerHead);
				// RationalHead =
				// initPredefinedSymbol(IConstantHeaders.RationalHead);
				// SymbolHead =
				// initPredefinedSymbol(IConstantHeaders.SymbolHead);
				// RealHead = initPredefinedSymbol(IConstantHeaders.RealHead);
				// ComplexHead =
				// initPredefinedSymbol(IConstantHeaders.ComplexHead);
				// PatternHead =
				// initPredefinedSymbol(IConstantHeaders.PatternHead);
				// BlankHead = initPredefinedSymbol(IConstantHeaders.BlankHead);
				// StringHead =
				// initPredefinedSymbol(IConstantHeaders.StringHead);
				// MethodHead =
				// initPredefinedSymbol(IConstantHeaders.MethodHead);
				//
				// Slot = initPredefinedSymbol("Slot");
				// Slot.setAttributes(ISymbol.NHOLDALL);
				// SlotSequence = initPredefinedSymbol("SlotSequence");
				// SlotSequence.setAttributes(ISymbol.NHOLDALL);
				// Options = initPredefinedSymbol("Options");
				// Graphics = initPredefinedSymbol("Graphics");
				// ReplaceAll = initPredefinedSymbol("ReplaceAll");
				// Show = initPredefinedSymbol("Show");
				// SurfaceGraphics = initPredefinedSymbol("SurfaceGraphics");
				//
				// // generated symbols
				// Abs = initPredefinedSymbol("Abs");
				// And = initPredefinedSymbol("And");
				// Append = initPredefinedSymbol("Append");
				// Apart = initPredefinedSymbol("Apart");
				// Apply = initPredefinedSymbol("Apply");
				// ArcCos = initPredefinedSymbol("ArcCos");
				// ArcSin = initPredefinedSymbol("ArcSin");
				// ArcTan = initPredefinedSymbol("ArcTan");
				// ArcCosh = initPredefinedSymbol("ArcCosh");
				// ArcSinh = initPredefinedSymbol("ArcSinh");
				// ArcTanh = initPredefinedSymbol("ArcTanh");
				// AtomQ = initPredefinedSymbol("AtomQ");
				// Binomial = initPredefinedSymbol("Binomial");
				// Blank = initPredefinedSymbol("Blank");
				// Block = initPredefinedSymbol("Block");
				// Break = initPredefinedSymbol("Break");
				// Cancel = initPredefinedSymbol("Cancel");
				// Csc = initPredefinedSymbol("Csc");
				// Ceiling = initPredefinedSymbol("Ceiling");
				// CompoundExpression =
				// initPredefinedSymbol("CompoundExpression");
				// Condition = initPredefinedSymbol("Condition");
				// Conjugate = initPredefinedSymbol("Conjugate");
				// Continue = initPredefinedSymbol("Continue");
				// Cos = initPredefinedSymbol("Cos");
				// Cosh = initPredefinedSymbol("Cosh");
				// Cot = initPredefinedSymbol("Cot");
				// Coth = initPredefinedSymbol("Coth");
				// Cross = initPredefinedSymbol("Cross");
				// D = initPredefinedSymbol("D");
				// Denominator = initPredefinedSymbol("Denominator");
				// Derivative = initPredefinedSymbol("Derivative");
				// Det = initPredefinedSymbol("Det");
				// Dot = initPredefinedSymbol("Dot");
				// Equal = initPredefinedSymbol("Equal");
				// EvenQ = initPredefinedSymbol("EvenQ");
				// Expand = initPredefinedSymbol("Expand");
				// ExpandAll = initPredefinedSymbol("ExpandAll");
				// Factor = initPredefinedSymbol("Factor");
				// Factorial = initPredefinedSymbol("Factorial");
				// FactorInteger = initPredefinedSymbol("FactorInteger");
				// Fibonacci = initPredefinedSymbol("Fibonacci");
				// FindRoot = initPredefinedSymbol("FindRoot");
				// First = initPredefinedSymbol("First");
				// Floor = initPredefinedSymbol("Floor");
				// FreeQ = initPredefinedSymbol("FreeQ");
				// FullForm = initPredefinedSymbol("FullForm");
				// Function = initPredefinedSymbol("Function");
				// GCD = initPredefinedSymbol("GCD");
				// Greater = initPredefinedSymbol("Greater");
				// GreaterEqual = initPredefinedSymbol("GreaterEqual");
				// // GroebnerBasis = initSymbol("GroebnerBasis", new
				// // GroebnerBasis());
				// Head = initPredefinedSymbol("Head");
				// Hold = initPredefinedSymbol("Hold");
				// I = initPredefinedSymbol("I");
				// If = initPredefinedSymbol("If");
				// Im = initPredefinedSymbol("Im");
				// IntegerQ = initPredefinedSymbol("IntegerQ");
				// Integrate = initPredefinedSymbol("Integrate");
				// Inverse = initPredefinedSymbol("Inverse");
				// // KOrderlessPartitions =
				// // initSymbol("KOrderlessPartitions", new
				// // KOrderlessPartitions());
				// // KPartitions = initSymbol("KPartitions", new
				// // KPartitions());
				// // KSubsets = initSymbol("KSubsets", new KSubsets());
				// LeafCount = initPredefinedSymbol("LeafCount");
				// Length = initPredefinedSymbol("Length");
				// Less = initPredefinedSymbol("Less");
				// LessEqual = initPredefinedSymbol("LessEqual");
				// Level = initPredefinedSymbol("Level");
				// Limit = initPredefinedSymbol("Limit");
				// Map = initPredefinedSymbol("Map");
				// MapAll = initPredefinedSymbol("MapAll");
				// MatchQ = initPredefinedSymbol("MatchQ");
				// MatrixPower = initPredefinedSymbol("MatrixPower");
				// Max = initPredefinedSymbol("Max");
				// MemberQ = initPredefinedSymbol("MemberQ");
				// Min = initPredefinedSymbol("Min");
				// Mod = initPredefinedSymbol("Mod");
				// Module = initPredefinedSymbol("Module");
				// N = initPredefinedSymbol("N");
				// Negative = initPredefinedSymbol("Negative");
				// NonNegative = initPredefinedSymbol("NonNegative");
				// Not = initPredefinedSymbol("Not");
				// // NumberPartitions = initSymbol("NumberPartitions", new
				// // NumberPartitions());
				// NumberQ = initPredefinedSymbol("NumberQ");
				// NumericQ = initPredefinedSymbol("NumericQ");
				// Numerator = initPredefinedSymbol("Numerator");
				// OddQ = initPredefinedSymbol("OddQ");
				// Or = initPredefinedSymbol("Or");
				// Order = initPredefinedSymbol("Order");
				// OrderedQ = initPredefinedSymbol("OrderedQ");
				// Part = initPredefinedSymbol("Part");
				// // Partition = initSymbol("Partition", new Partition());
				// // Permutations = initSymbol("Permutations", new
				// // Permutations());
				// Plot = initPredefinedSymbol("Plot");
				// Plot3D = initPredefinedSymbol("Plot3D");
				//
				// Positive = initPredefinedSymbol("Positive");
				// PossibleZeroQ = initPredefinedSymbol("PossibleZeroQ");
				//
				// Prepend = initPredefinedSymbol("Prepend");
				// PrimeQ = initPredefinedSymbol("PrimeQ");
				// Print = initPredefinedSymbol("Print");
				// Product = initPredefinedSymbol("Product");
				// Quotient = initPredefinedSymbol("Quotient");
				// Re = initPredefinedSymbol("Re");
				// Rest = initPredefinedSymbol("Rest");
				// Reverse = initPredefinedSymbol("Reverse");
				// RootOf = initPredefinedSymbol("RootOf");
				// RotateLeft = initPredefinedSymbol("RotateLeft");
				// RotateRight = initPredefinedSymbol("RotateRight");
				// Rule = initPredefinedSymbol("Rule");
				// RuleDelayed = initPredefinedSymbol("RuleDelayed");
				// Sec = initPredefinedSymbol("Sec");
				// Sequence = initPredefinedSymbol("Sequence");
				// SetAttributes = initPredefinedSymbol("SetAttributes");
				// Sign = initPredefinedSymbol("Sign");
				// SignCmp = initPredefinedSymbol("SignCmp");
				// Sin = initPredefinedSymbol("Sin");
				// Sinh = initPredefinedSymbol("Sinh");
				// Sort = initPredefinedSymbol("Sort");
				// Sqrt = initPredefinedSymbol("Sqrt");
				// Sum = initPredefinedSymbol("Sum");
				// Tan = initPredefinedSymbol("Tan");
				// Tanh = initPredefinedSymbol("Tanh");
				//
				// Taylor = initPredefinedSymbol("Taylor");
				// Timing = initPredefinedSymbol("Timing");
				// Together = initPredefinedSymbol("Together");
				// Tr = initPredefinedSymbol("Tr");
				// Trace = initPredefinedSymbol("Trace");
				// Transpose = initPredefinedSymbol("Transpose");
				// TrueQ = initPredefinedSymbol("TrueQ");
				// Trunc = initPredefinedSymbol("Trunc");
				// Unequal = initPredefinedSymbol("Unequal");
				// While = initPredefinedSymbol("While");
				//
				// CInfinity = $(DirectedInfinity, C1);
				// CNInfinity = $(DirectedInfinity, CN1);
				// Slot1 = $(Slot, C1);
				// Slot2 = $(Slot, C2);

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
						Package.loadPackage(EvalEngine.get(), reader);
					}
				}
				// PREDEFINED_INTERNAL_STRINGS.put("Pi", "Pi");
				// PREDEFINED_INTERNAL_STRINGS.put("E", "E");
				// PREDEFINED_INTERNAL_STRINGS.put("False", "False");
				// PREDEFINED_INTERNAL_STRINGS.put("True", "True");
				// PREDEFINED_INTERNAL_STRINGS.put("Null", "Null");
				//
				// Plus.setDefaultValue(C0);
				// Plus.setEvaluator(org.matheclipse.core.reflection.system.Plus.CONST);
				// Times.setDefaultValue(C1);
				// Times.setEvaluator(org.matheclipse.core.reflection.system.Times.CONST);
				// Power.setDefaultValue(2, C1);
				// Power.setEvaluator(org.matheclipse.core.reflection.system.Power.CONST);
				// Integrate.setEvaluator(org.matheclipse.core.reflection.system.Integrate.CONST);
				//
				isSystemInitialized = true;
				// // long end = System.currentTimeMillis();
				// // System.out.println("Init time: " + (end - start));
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
//		if (a0 == null || a1 == null||a0 == F.Null || a1 == F.Null) {
//			System.out.println("Part argument is null");
//		}
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

		return binary(SetDelayed, a0, a1);
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

	public static IAST Trunc(final IExpr a0) {
		return unary(Trunc, a0);
	}
	
	public static IAST UnsameQ(final IExpr a0,final IExpr a1) {
		return binary(UnsameQ, a0, a1);
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
	 *            the header expression of the function. If the ast represents a
	 *            function like <code>f[x,y], Sin[x],...</code>, the
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
	 *            the header expression of the function. If the ast represents a
	 *            function like <code>f[x,y], Sin[x],...</code>, the
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
	 * Gives symbols "True" or "False" (type ISymbol) depending on the boolean
	 * value.
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
	 *            the real double value part which should be converted to a
	 *            complex number
	 * @param imagPart
	 *            the imaginary double value part which should be converted to a
	 *            complex number
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
	 * @return the evaluated object or <code>null</code> if no evaluation was
	 *         possible.
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
	 * @return the evaluated object or <code>null</code> if no evaluation was
	 *         possible.
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
	 * @return the evaluated object or <code>null</code> if no evaluation was
	 *         possible.
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
	 *            the rational value which should be converted to a fractional
	 *            number
	 * @return IFraction
	 */
	public static IFraction fraction(final BigFraction value) {
		return FractionSym.valueOf(value);
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
		return Pattern.valueOf(symbol);
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
		return Pattern.valueOf(symbol, check, def);
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
		return Pattern.valueOf(symbol, check);
	}

	/**
	 * Create a pattern for pattern-matching and term rewriting
	 * 
	 * @param symbolName
	 * @return IPattern
	 */
	public static IPattern $p(final String symbolName) {
		if (symbolName == null) {
			return Pattern.valueOf(null);
		}
		return Pattern.valueOf($s(symbolName));
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
	public static IPattern $p(final String symbolName, final IExpr check) {
		if (symbolName == null) {
			return Pattern.valueOf(null, check);
		}
		return Pattern.valueOf($s(symbolName), check);
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
	 * @param def
	 *            use a default value for this pattern if necessary
	 * @return IPattern
	 */
	public static IPattern $p(final String symbolName, final IExpr check, boolean def) {
		if (symbolName == null) {
			return Pattern.valueOf(null, check, def);
		}
		return Pattern.valueOf($s(symbolName), check, def);
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
	 * @param def
	 *            if <code>true</code>, the pattern can match to a default value
	 *            associated with the AST's head the pattern is used in.
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
		temp = new Symbol(symbolName);
		PREDEFINED_SYMBOLS_MAP.put(symbolName, temp);
		// if (!skipEvaluatorSettings &&
		// Character.isUpperCase(symbolName.charAt(0))) {
		// // probably a predefined function use reflection to setUp this
		// // symbol
		// SystemNamespace.DEFAULT.setEvaluator(temp);
		// }
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
	private static ISymbol initPredefinedSymbol(final String symbolName) {
		if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
			String lcSymbolName = symbolName.toLowerCase();
			// use the upper case string here to use it as associated class name
			// in package org.matheclipse.core.reflection.system
			ISymbol temp = new Symbol(lcSymbolName);
			PREDEFINED_SYMBOLS_MAP.put(lcSymbolName, temp);
			return temp;
		}
		ISymbol temp = new Symbol(symbolName);
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
	 * Get a global symbol which is retrieved from the global symbols map or
	 * created or retrieved from the thread local variables map.
	 * 
	 * @param symbolName
	 * @return
	 */
	public static ISymbol $s(final String symbolName) {
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
			if (Config.PARSER_USE_LOWERCASE_SYMBOLS) {
				SystemNamespace.DEFAULT.setEvaluator(symbol);
			} else {
				if (Character.isUpperCase(name.charAt(0))) {
					// probably a predefined function
					// use reflection to setUp this symbol
					SystemNamespace.DEFAULT.setEvaluator(symbol);
				}
			}
		}

		return symbol;
	}

	/**
	 * Create a local symbol which is created or retrieved from the eval engines
	 * thread local variables map and push a value on the local stack;
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
	 * Create a local symbol which is created or retrieved from the eval engines
	 * thread local variables map and push a <code>null</code> value on the
	 * local stack;
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
	 * After a successful <code>isCase()</code> the symbols associated with the
	 * patterns contain the matched values on the local stack.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isCase(IExpr a, IExpr b) {
		if (a instanceof IAST) {
			final PatternMatcher matcher = new PatternMatcher(a);
			if (matcher.apply(b)) {
				matcher.setPatternValue2Local(a);
				return true;
			}
		}
		return equals(a, b);
	}

	public static boolean isCase(IExpr a, Integer i) {
		return isCase(a, integer(i.longValue()));
	}

	public static boolean isCase(Integer i, IExpr b) {
		return equals(i, b);
	}

	public static boolean isCase(IExpr a, java.math.BigInteger i) {
		return isCase(a, integer(i));
	}

	public static boolean isCase(java.math.BigInteger i, IExpr b) {
		return equals(i, b);
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
	 * Evaluate an expression. If no evaluation was possible this method returns
	 * the given argument.
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
	 * Substitute all (sub-) expressions with the given unary function. If no
	 * substitution matches, the method returns the given <code>expr</code>.
	 * 
	 * @param function
	 *            if the unary functions <code>apply()</code> method returns
	 *            <code>null</code> the expression isn't substituted.
	 * @return the input <code>expr</code> if no substitution of a
	 *         (sub-)expression was possible or the substituted expression.
	 */
	public static IExpr subst(IExpr expr, final Function<IExpr, IExpr> function) {
		final IExpr result = expr.replaceAll(function);
		return (result == null) ? expr : result;
	}

	/**
	 * Substitute all (sub-) expressions with the given rule set. If no
	 * substitution matches, the method returns the given <code>expr</code>.
	 * 
	 * @param astRules
	 *            rules of the form <code>x-&gt;y</code> or
	 *            <code>{a-&gt;b, c-&gt;d}</code>; the left-hand-side of the
	 *            rule can contain pattern objects.
	 * @return the input <code>expr</code> if no substitution of a
	 *         (sub-)expression was possible or the substituted expression.
	 */
	public static IExpr subst(IExpr expr, final IAST astRules) {
		final IExpr result = expr.replaceAll(astRules);
		return (result == null) ? expr : result;
	}

	/**
	 * Apply <code>ExpandAll[]</code> to the given expression and evaluate it.
	 * If no evaluation was possible this method returns the given argument.
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
	 * Evaluate the given expression and test if the result equals the symbol
	 * <code>True</code>.
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