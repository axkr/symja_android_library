package org.matheclipse.core.expression;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;
import org.apfloat.OverflowException;
import org.apfloat.spi.FilenameGenerator;
import org.apfloat.spi.Util;
import org.hipparchus.Field;
import org.hipparchus.complex.Complex;
import org.hipparchus.fraction.BigFraction;
import org.matheclipse.core.basic.AndroidLoggerFix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.builtin.AssociationFunctions;
import org.matheclipse.core.builtin.AssumptionFunctions;
import org.matheclipse.core.builtin.AttributeFunctions;
import org.matheclipse.core.builtin.BesselFunctions;
import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.builtin.BoxesFunctions;
import org.matheclipse.core.builtin.ClusteringFunctions;
import org.matheclipse.core.builtin.Combinatoric;
import org.matheclipse.core.builtin.CompilerFunctions;
import org.matheclipse.core.builtin.ComputationalGeometryFunctions;
import org.matheclipse.core.builtin.ConstantDefinitions;
import org.matheclipse.core.builtin.ConstantPhysicsDefinitions;
import org.matheclipse.core.builtin.ContainsFunctions;
import org.matheclipse.core.builtin.CurveFitterFunctions;
import org.matheclipse.core.builtin.EllipticIntegrals;
import org.matheclipse.core.builtin.EntityFunctions;
import org.matheclipse.core.builtin.ExpTrigsFunctions;
import org.matheclipse.core.builtin.FileFunctions;
import org.matheclipse.core.builtin.FilterFunctions;
import org.matheclipse.core.builtin.FinancialFunctions;
import org.matheclipse.core.builtin.FunctionDefinitions;
import org.matheclipse.core.builtin.GeodesyFunctions;
import org.matheclipse.core.builtin.GraphDataFunctions;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.builtin.GraphicsFunctions;
import org.matheclipse.core.builtin.HypergeometricFunctions;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.IntegerFunctions;
import org.matheclipse.core.builtin.IntervalFunctions;
import org.matheclipse.core.builtin.JavaFunctions;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.builtin.ListFunctions;
import org.matheclipse.core.builtin.ManipulateFunction;
import org.matheclipse.core.builtin.MinMaxFunctions;
import org.matheclipse.core.builtin.NumberTheory;
import org.matheclipse.core.builtin.NumericArrayFunctions;
import org.matheclipse.core.builtin.OutputFunctions;
import org.matheclipse.core.builtin.PatternMatching;
import org.matheclipse.core.builtin.PiecewiseFunctions;
import org.matheclipse.core.builtin.PolynomialFunctions;
import org.matheclipse.core.builtin.PredicateQ;
import org.matheclipse.core.builtin.Programming;
import org.matheclipse.core.builtin.QuantityFunctions;
import org.matheclipse.core.builtin.QuantumPhysicsFunctions;
import org.matheclipse.core.builtin.RandomFunctions;
import org.matheclipse.core.builtin.RootsFunctions;
import org.matheclipse.core.builtin.SequenceFunctions;
import org.matheclipse.core.builtin.SeriesFunctions;
import org.matheclipse.core.builtin.SidesFunctions;
import org.matheclipse.core.builtin.SimplifyFunctions;
import org.matheclipse.core.builtin.SourceCodeFunctions;
import org.matheclipse.core.builtin.SparseArrayFunctions;
import org.matheclipse.core.builtin.SpecialFunctions;
import org.matheclipse.core.builtin.StatisticsFunctions;
import org.matheclipse.core.builtin.StringFunctions;
import org.matheclipse.core.builtin.StructureFunctions;
import org.matheclipse.core.builtin.SubsetFunctions;
import org.matheclipse.core.builtin.TensorFunctions;
import org.matheclipse.core.builtin.UnitTestingFunctions;
import org.matheclipse.core.builtin.VectorAnalysisFunctions;
import org.matheclipse.core.builtin.WXFFunctions;
import org.matheclipse.core.builtin.WindowFunctions;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.BigIntegerLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.eval.util.BiIntFunction;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.form.output.JSBuilder;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.generic.ObjIntFunction;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.parser.ExprParserFactory;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.reflection.system.rules.AutomaticRules;
import org.matheclipse.core.tensor.QuantityParser;
import org.matheclipse.core.visit.VisitorLevelSpecification;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.trie.TrieMatch;
import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.math.DoubleMath;
import edu.jas.kern.ComputerThreads;
import edu.jas.kern.JASConfig;
import edu.jas.kern.PreemptStatus;

/** Factory for creating Symja predefined function expression objects (interface {@link IAST}). */
public class F extends S {

  /**
   * <b>Attention:</b> special initialization of LOGGER for Android app
   */
  private static Logger LOGGER;

  /**
   * In computing, memoization or memoisation is an optimization technique used primarily to speed
   * up computer programs by storing the results of expensive function calls and returning the
   * cached result when the same inputs occur again. This cache is especially used for recursive
   * integer functions to remember the results of the recursive call. See:
   * <a href="https://en.wikipedia.org/wiki/Memoization">Wikipedia - Memoization</a>
   */
  public static final Cache<IAST, IExpr> REMEMBER_INTEGER_CACHE =
      CacheBuilder.newBuilder().maximumSize(500).build();

  public static final IAST[] SLOT_CACHE = new IAST[100];

  /** Set to <code>true</code> at the start of initSymbols() method */
  private static volatile boolean isSystemStarted = false;

  /** Set to <code>true</code> at the end of initSymbols() method */
  private static volatile boolean systemInitialized = false;

  public static boolean isSystemInitialized() {
    return systemInitialized;
  }

  /**
   * The map for predefined strings for the {@link IExpr#internalFormString(boolean, int)} method.
   */
  public static final Map<String, String> PREDEFINED_INTERNAL_FORM_STRINGS =
      ParserConfig.TRIE_STRING2STRING_BUILDER.withMatch(TrieMatch.EXACT).build();

  public static String getPredefinedInternalFormString(String key) {
    return PREDEFINED_INTERNAL_FORM_STRINGS.get(key);
  }

  private static final Map<String, IPattern> PREDEFINED_PATTERN_MAP =
      Config.TRIE_STRING2PATTERN_BUILDER.withMatch(TrieMatch.EXACT).build();

  public static IPattern getPredefinedPattern(String key) {
    return PREDEFINED_PATTERN_MAP.get(key);
  }

  private static final Map<String, IPatternSequence> PREDEFINED_PATTERNSEQUENCE_MAP =
      Config.TRIE_STRING2PATTERNSEQUENCE_BUILDER.withMatch(TrieMatch.EXACT).build();

  public static IPatternSequence getPredefinedPatternSequence(String key) {
    return PREDEFINED_PATTERNSEQUENCE_MAP.get(key);
  }

  public static final ISymbolObserver SYMBOL_OBSERVER = new ISymbolObserver() {
    @Override
    public final boolean createPredefinedSymbol(String symbol) {
      return false;
    }

    @Override
    public void createUserSymbol(ISymbol symbol) {}
  };

  /**
   * The constant object <code>NIL</code> (not in list) indicates in the evaluation process that no
   * evaluation was possible (i.e. no further definition was found to create a new expression from
   * the existing one).
   *
   * @see java.util.Optional#isPresent
   */
  public static final AbstractAST.NILPointer NIL = AbstractAST.NIL;

  public static final AbstractAST.NILPointer INVALID = AbstractAST.INVALID;
  // public final static ISymbol usage = initFinalHiddenSymbol("usage");

  /**
   * Built-in function IDs those arguments should be evaluated numerically in numeric mode:
   */
  public static final int[] SORTED_NUMERIC_ARGS_IDS = new int[] {//
      ID.Equal, ID.Greater, ID.GreaterEqual, ID.Labeled, ID.Less, ID.LessEqual, ID.Unequal //
  };

  /**
   * Used to represent a formal pattern <code>a_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern a_ = initPredefinedPattern(a);

  /**
   * Used to represent a formal pattern <code>b_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern b_ = initPredefinedPattern(b);

  /**
   * Used to represent a formal pattern <code>c_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern c_ = initPredefinedPattern(c);

  /**
   * Used to represent a formal pattern <code>d_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern d_ = initPredefinedPattern(d);

  /**
   * Used to represent a formal pattern <code>e_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern e_ = initPredefinedPattern(e);

  /**
   * Used to represent a formal pattern <code>f_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern f_ = initPredefinedPattern(f);

  /**
   * Used to represent a formal pattern <code>g_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern g_ = initPredefinedPattern(g);

  /**
   * Used to represent a formal pattern <code>h_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern h_ = initPredefinedPattern(h);

  /**
   * Used to represent a formal pattern <code>i_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern i_ = initPredefinedPattern(i);

  /**
   * Used to represent a formal pattern <code>j_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern j_ = initPredefinedPattern(j);

  /**
   * Used to represent a formal pattern <code>k_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern k_ = initPredefinedPattern(k);

  /**
   * Used to represent a formal pattern <code>l_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern l_ = initPredefinedPattern(l);

  /**
   * Used to represent a formal pattern <code>m_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern m_ = initPredefinedPattern(m);

  /**
   * Used to represent a formal pattern <code>n_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern n_ = initPredefinedPattern(n);

  /**
   * Used to represent a formal pattern <code>o_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern o_ = initPredefinedPattern(o);

  /**
   * Used to represent a formal pattern <code>p_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern p_ = initPredefinedPattern(p);

  /**
   * Used to represent a formal pattern <code>q_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern q_ = initPredefinedPattern(q);
  /**
   * Used to represent a formal pattern <code>r_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern r_ = initPredefinedPattern(r);

  /**
   * Used to represent a formal pattern <code>s_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern s_ = initPredefinedPattern(s);

  /**
   * Used to represent a formal pattern <code>t_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern t_ = initPredefinedPattern(t);

  /**
   * Used to represent a formal pattern <code>u_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern u_ = initPredefinedPattern(u);

  /**
   * Used to represent a formal pattern <code>v_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern v_ = initPredefinedPattern(v);

  /**
   * Used to represent a formal pattern <code>w_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern w_ = initPredefinedPattern(w);

  /**
   * Used to represent a formal pattern <code>x_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern x_ = initPredefinedPattern(x);

  /**
   * Used to represent a formal pattern <code>y_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern y_ = initPredefinedPattern(y);

  /**
   * Used to represent a formal pattern <code>z_</code> that will be used only for predefined
   * pattern-matching rules and can match one expression.
   */
  public static final IPattern z_ = initPredefinedPattern(z);

  /**
   * Used to represent a formal pattern sequence <code>x__</code> that will be used only for
   * predefined pattern-matching rules and can match any sequence of one or more expressions.
   */
  public static final IPatternSequence x__ = initPredefinedPatternSequence(x);

  /**
   * Used to represent a formal pattern sequence <code>y__</code> that will be used only for
   * predefined pattern-matching rules and can match any sequence of one or more expressions.
   */
  public static final IPatternSequence y__ = initPredefinedPatternSequence(y);

  /**
   * Used to represent a formal pattern sequence <code>z__</code> that will be used only for
   * predefined pattern-matching rules and can match any sequence of one or more expressions.
   */
  public static final IPatternSequence z__ = initPredefinedPatternSequence(z);

  /**
   * Used to represent a formal null pattern sequence <code>x___</code> that will be used only for
   * predefined pattern-matching rules and can match any sequence of zero or more expressions.
   */
  public static final IPatternSequence x___ = initPredefinedPatternSequence(x, true);

  /**
   * Used to represent a formal null pattern sequence <code>y___</code> that will be used only for
   * predefined pattern-matching rules and can match any sequence of zero or more expressions.
   */
  public static final IPatternSequence y___ = initPredefinedPatternSequence(y, true);

  /**
   * Used to represent a formal null pattern sequence <code>z___</code> that will be used only for
   * predefined pattern-matching rules and can match any sequence of zero or more expressions.
   */
  public static final IPatternSequence z___ = initPredefinedPatternSequence(z, true);

  public static final IPattern A_ = initPredefinedPattern(ASymbol);
  public static final IPattern B_ = initPredefinedPattern(BSymbol);
  public static final IPattern C_ = initPredefinedPattern(CSymbol);
  public static final IPattern F_ = initPredefinedPattern(FSymbol);
  public static final IPattern G_ = initPredefinedPattern(GSymbol);
  public static final IPattern P_ = initPredefinedPattern(PSymbol);
  public static final IPattern Q_ = initPredefinedPattern(QSymbol);

  public static final IPattern m_Integer = new Pattern(m, Integer);
  public static final IPattern n_Integer = new Pattern(n, Integer);

  public static final IPattern a_Symbol = new Pattern(a, Symbol);
  public static final IPattern b_Symbol = new Pattern(b, Symbol);
  public static final IPattern c_Symbol = new Pattern(c, Symbol);
  public static final IPattern d_Symbol = new Pattern(d, Symbol);
  public static final IPattern e_Symbol = new Pattern(e, Symbol);
  public static final IPattern f_Symbol = new Pattern(f, Symbol);
  public static final IPattern g_Symbol = new Pattern(g, Symbol);
  public static final IPattern h_Symbol = new Pattern(h, Symbol);
  public static final IPattern i_Symbol = new Pattern(i, Symbol);
  public static final IPattern j_Symbol = new Pattern(j, Symbol);
  public static final IPattern k_Symbol = new Pattern(k, Symbol);
  public static final IPattern l_Symbol = new Pattern(l, Symbol);
  public static final IPattern m_Symbol = new Pattern(m, Symbol);
  public static final IPattern n_Symbol = new Pattern(n, Symbol);
  public static final IPattern o_Symbol = new Pattern(o, Symbol);
  public static final IPattern p_Symbol = new Pattern(p, Symbol);
  public static final IPattern q_Symbol = new Pattern(q, Symbol);
  public static final IPattern r_Symbol = new Pattern(r, Symbol);
  public static final IPattern s_Symbol = new Pattern(s, Symbol);
  public static final IPattern t_Symbol = new Pattern(t, Symbol);
  public static final IPattern u_Symbol = new Pattern(u, Symbol);
  public static final IPattern v_Symbol = new Pattern(v, Symbol);
  public static final IPattern w_Symbol = new Pattern(w, Symbol);
  public static final IPattern x_Symbol = new Pattern(x, Symbol);
  public static final IPattern y_Symbol = new Pattern(y, Symbol);
  public static final IPattern z_Symbol = new Pattern(z, Symbol);

  public static final IPattern a_DEFAULT = new Pattern(a, null, true);
  public static final IPattern b_DEFAULT = new Pattern(b, null, true);
  public static final IPattern c_DEFAULT = new Pattern(c, null, true);
  public static final IPattern d_DEFAULT = new Pattern(d, null, true);
  public static final IPattern e_DEFAULT = new Pattern(e, null, true);
  public static final IPattern f_DEFAULT = new Pattern(f, null, true);
  public static final IPattern g_DEFAULT = new Pattern(g, null, true);
  public static final IPattern h_DEFAULT = new Pattern(h, null, true);
  public static final IPattern i_DEFAULT = new Pattern(i, null, true);
  public static final IPattern j_DEFAULT = new Pattern(j, null, true);
  public static final IPattern k_DEFAULT = new Pattern(k, null, true);
  public static final IPattern l_DEFAULT = new Pattern(l, null, true);
  public static final IPattern m_DEFAULT = new Pattern(m, null, true);
  public static final IPattern n_DEFAULT = new Pattern(n, null, true);
  public static final IPattern o_DEFAULT = new Pattern(o, null, true);
  public static final IPattern p_DEFAULT = new Pattern(p, null, true);
  public static final IPattern q_DEFAULT = new Pattern(q, null, true);
  public static final IPattern r_DEFAULT = new Pattern(r, null, true);
  public static final IPattern s_DEFAULT = new Pattern(s, null, true);
  public static final IPattern t_DEFAULT = new Pattern(t, null, true);
  public static final IPattern u_DEFAULT = new Pattern(u, null, true);
  public static final IPattern v_DEFAULT = new Pattern(v, null, true);
  public static final IPattern w_DEFAULT = new Pattern(w, null, true);
  public static final IPattern x_DEFAULT = new Pattern(x, null, true);
  public static final IPattern y_DEFAULT = new Pattern(y, null, true);
  public static final IPattern z_DEFAULT = new Pattern(z, null, true);

  public static final IPattern A_DEFAULT = new Pattern(ASymbol, null, true);
  public static final IPattern B_DEFAULT = new Pattern(BSymbol, null, true);
  public static final IPattern C_DEFAULT = new Pattern(CSymbol, null, true);
  public static final IPattern F_DEFAULT = new Pattern(FSymbol, null, true);
  public static final IPattern G_DEFAULT = new Pattern(GSymbol, null, true);
  public static final IPattern P_DEFAULT = new Pattern(PSymbol, null, true);
  public static final IPattern Q_DEFAULT = new Pattern(QSymbol, null, true);
  /** Constant integer &quot;0&quot; */
  public static final IInteger C0 = AbstractIntegerSym.valueOf(0);

  /** Constant integer &quot;1&quot; */
  public static final IInteger C1 = AbstractIntegerSym.valueOf(1);

  /** Constant integer &quot;2&quot; */
  public static final IInteger C2 = AbstractIntegerSym.valueOf(2);

  /** Constant integer &quot;3&quot; */
  public static final IInteger C3 = AbstractIntegerSym.valueOf(3);

  /** Constant integer &quot;4&quot; */
  public static final IInteger C4 = AbstractIntegerSym.valueOf(4);

  /** Constant integer &quot;5&quot; */
  public static final IInteger C5 = AbstractIntegerSym.valueOf(5);

  /** Constant integer &quot;6&quot; */
  public static final IInteger C6 = AbstractIntegerSym.valueOf(6);

  /** Constant integer &quot;7&quot; */
  public static final IInteger C7 = AbstractIntegerSym.valueOf(7);

  /** Constant integer &quot;8&quot; */
  public static final IInteger C8 = AbstractIntegerSym.valueOf(8);

  /** Constant integer &quot;9&quot; */
  public static final IInteger C9 = AbstractIntegerSym.valueOf(9);

  /** Constant integer &quot;10&quot; */
  public static final IInteger C10 = AbstractIntegerSym.valueOf(10);

  /** Constant integer &quot;100&quot; */
  public static final IInteger C100 = AbstractIntegerSym.valueOf(100);

  /** Constant integer &quot;1000&quot; */
  public static final IInteger C1000 = new IntegerSym(1000);

  /**
   * Complex imaginary unit &quot;0 + I&quot;. The parsed symbol &quot;I&quot; is converted on input
   * to this constant.
   */
  public static final IComplex CI = ComplexSym.valueOf(0, 1, 1, 1);

  /** Complex negative imaginary unit &quot;0 - I&quot;. */
  public static final IComplex CNI = ComplexSym.valueOf(0, 1, -1, 1);

  /** Constant fraction &quot;1/2&quot; */
  public static final IFraction C1D2 = AbstractFractionSym.valueOf(1, 2);

  /** Constant fraction &quot;3/2&quot; */
  public static final IFraction C3D2 = AbstractFractionSym.valueOf(3, 2);

  /** Constant fraction &quot;3/4&quot; */
  public static final IFraction C3D4 = AbstractFractionSym.valueOf(3, 4);

  /** Constant fraction &quot;5/2&quot; */
  public static final IFraction C5D2 = AbstractFractionSym.valueOf(5, 2);

  /** Constant fraction &quot;-1/2&quot; */
  public static final IFraction CN1D2 = AbstractFractionSym.valueOf(-1, 2);

  /** Constant fraction &quot;-3/2&quot; */
  public static final IFraction CN3D2 = AbstractFractionSym.valueOf(-3, 2);

  /** Constant fraction &quot;1/3&quot; */
  public static final IFraction C1D3 = AbstractFractionSym.valueOf(1, 3);

  /** Constant fraction &quot;-1/3&quot; */
  public static final IFraction CN1D3 = AbstractFractionSym.valueOf(-1, 3);

  /** Constant fraction &quot;1/4&quot; */
  public static final IFraction C1D4 = AbstractFractionSym.valueOf(1, 4);

  /** Constant fraction &quot;-1/4&quot; */
  public static final IFraction CN1D4 = AbstractFractionSym.valueOf(-1, 4);

  /** Constant fraction &quot;1/5&quot; */
  public static final IFraction C1D5 = AbstractFractionSym.valueOf(1, 5);

  /** Constant fraction &quot;-1/5&quot; */
  public static final IFraction CN1D5 = AbstractFractionSym.valueOf(-1, 5);

  /** Constant fraction &quot;1/6&quot; */
  public static final IFraction C1D6 = AbstractFractionSym.valueOf(1, 6);

  /** Constant fraction &quot;-1/6&quot; */
  public static final IFraction CN1D6 = AbstractFractionSym.valueOf(-1, 6);


  /** Constant fraction &quot;2/3&quot; */
  public static final IFraction C2D3 = AbstractFractionSym.valueOf(2, 3);

  /** Constant fraction &quot;-2/3&quot; */
  public static final IFraction CN2D3 = AbstractFractionSym.valueOf(-2, 3);

  /** Constant double &quot;-1.0&quot; */
  public static final Num CND1 = new Num(-1.0);

  /** Constant double &quot;0.0&quot; */
  public static final Num CD0 = new Num(0.0);

  /** Constant double &quot;1.0&quot; */
  public static final Num CD1 = new Num(1.0);

  /** Constant double &quot;(-1.0)/E&quot; */
  public static final Num CND1DE = new Num((-1.0) / Math.E);

  /** Constant double &quot;(1.0)/E&quot; */
  public static final Num CD1DE = new Num(1.0 / Math.E);

  /** Complex numerical imaginary unit. */
  public static final IComplexNum CDI = ComplexNum.I;

  /** Complex negative numerical imaginary unit. */
  public static final IComplexNum CDNI = ComplexNum.NI;

  /** Represents the empty Smyja string <code>""</code> */
  public static final IStringX CEmptyString;

  /** Represents <code>Sequence()</code> (i.e. the constant empty list) */
  public static final IAST CEmptySequence;

  /** Represents <code>List()</code> (i.e. the constant empty list) */
  public static final IAST CEmptyList;

  /** Represents <code>Interval()</code> (i.e. the constant empty interval with closed ends) */
  public static final IAST CEmptyInterval;

  /** Represents <code>Interval()</code> (i.e. the constant empty interval with closed/open ends) */
  public static final IAST CEmptyIntervalData;

  /** Represents <code>Missing("NotFound")</code> */
  public static final IAST CMissingNotFound;

  /** Represents <code>List(0)</code> */
  public static final IAST CListC0;

  /** Represents <code>List(1)</code> */
  public static final IAST CListC1;

  /**
   * Represents <code>List(-1)</code>. Can be used to specify the &quot;leaf&quot;
   * {@link VisitorLevelSpecification} of an expression.
   */
  public static final IAST CListCN1;

  /** Represents <code>List(0,0)</code> */
  public static final IAST CListC0C0;

  /** Represents <code>List(1,1)</code> */
  public static final IAST CListC1C1;

  /** Represents <code>List(1,2)</code> */
  public static final IAST CListC1C2;

  /** Represents <code>List(2)</code> */
  public static final IAST CListC2;

  /** Represents <code>List(2,1)</code> */
  public static final IAST CListC2C1;

  /** Represents <code>List(2,2)</code> */
  public static final IAST CListC2C2;

  /** Represents <code>Infinity</code> (i.e. <code>Infinity-&gt;DirectedInfinity(1)</code>) */
  public static final IAST CInfinity;

  /** Represents <code>Return(False)</code> */
  public static final IAST CReturnFalse;

  /** Represents <code>Return(True)</code> */
  public static final IAST CReturnTrue;

  /** Represents <code>Throw(False)</code> */
  public static final IAST CThrowFalse;

  /** Represents <code>Throw(True)</code> */
  public static final IAST CThrowTrue;

  /**
   * Alias for CInfinity. Represents <code>Infinity</code> (i.e. <code>
   * Infinity-&gt;DirectedInfinity(1)</code>)
   */
  public static final IAST oo;

  /** Represents <code>-Infinity</code> (i.e. <code>-Infinity-&gt;DirectedInfinity(-1)</code>) */
  public static final IAST CNInfinity;

  /**
   * Alias for CNInfinity. Represents <code>-Infinity</code> (i.e. <code>
   * -Infinity-&gt;DirectedInfinity(-1)</code>)
   */
  public static final IAST Noo;

  /** Represents <code>I*Infinity</code> (i.e. <code>I*Infinity-&gt;DirectedInfinity(I)</code>) */
  public static final IAST CIInfinity;

  /**
   * Represents <code>-I*Infinity</code> (i.e. <code>-I*Infinity-&gt;DirectedInfinity(-I)</code>)
   */
  public static final IAST CNIInfinity;

  /**
   * Represents {@link S#ComplexInfinity} as {@link F#DirectedInfinity()}.
   */
  public static final IAST CComplexInfinity;

  /** Represents <code>-Pi</code> as Symja expression <code>Times(CN1, Pi)</code> */
  public static final IAST CNPi;

  /** Represents <code>-2*Pi</code> as Symja expression <code>Times(CN2, Pi)</code> */
  public static final IAST CN2Pi;

  /** Represents <code>2*Pi</code> as Symja expression <code>Times(C2, Pi)</code> */
  public static final IAST C2Pi;

  /** Represents <code>-Pi/2</code> as Symja expression <code>Times(CN1D2, Pi)</code> */
  public static final IAST CNPiHalf;

  /** Represents <code>Pi/2</code> as Symja expression <code>Times(C1D2, Pi)</code> */
  public static final IAST CPiHalf;

  /** Represents <code>-Pi/3</code> as Symja expression <code>Times(CN1D3, Pi)</code> */
  public static final IAST CNPiThird;

  /** Represents <code>Pi/3</code> as Symja expression <code>Times(C1D3, Pi)</code> */
  public static final IAST CPiThird;

  /** Represents <code>-Pi/4</code> as Symja expression <code>Times(CN1D4, Pi)</code> */
  public static final IAST CNPiQuarter;

  /** Represents <code>Pi/4</code> as Symja expression <code>Times(C1D4, Pi)</code> */
  public static final IAST CPiQuarter;

  /** Represents <code>Sqrt(Pi)</code> */
  public static final IAST CSqrtPi;

  /** Represents <code>Sqrt(2)</code> */
  public static final IAST CSqrt2;

  /** Represents <code>Sqrt(3)</code> */
  public static final IAST CSqrt3;

  /** Represents <code>Sqrt(5)</code> */
  public static final IAST CSqrt5;

  /** Represents <code>Sqrt(6)</code> */
  public static final IAST CSqrt6;

  /** Represents <code>Sqrt(7)</code> */
  public static final IAST CSqrt7;

  /** Represents <code>Sqrt(10)</code> */
  public static final IAST CSqrt10;

  /** Represents <code>1/Sqrt(2)</code> */
  public static final IAST C1DSqrt2;

  /** Represents <code>1/Sqrt(3)</code> */
  public static final IAST C1DSqrt3;

  /** Represents <code>1/Sqrt(5)</code> */
  public static final IAST C1DSqrt5;

  /** Represents <code>1/Sqrt(6)</code> */
  public static final IAST C1DSqrt6;

  /** Represents <code>1/Sqrt(7)</code> */
  public static final IAST C1DSqrt7;

  /** Represents <code>1/Sqrt(10)</code> */
  public static final IAST C1DSqrt10;

  /** Represents <code>#1</code>, the first argument of a pure function. */
  public static final IAST Slot1;

  /** Represents <code>#2</code>, the second argument of a pure function. */
  public static final IAST Slot2;

  /** Represents <code>#3</code>, the third argument of a pure function. */
  public static final IAST Slot3;

  public static final IAST Slot4;

  public static final IAST Slot5;

  public static final IAST Slot6;

  public static final Field<IExpr> EXPR_FIELD = new ExprField();

  /** Constant integer &quot;-1&quot; */
  public static final IInteger CN1 = AbstractIntegerSym.valueOf(-1);

  /** Constant integer &quot;-2&quot; */
  public static final IInteger CN2 = AbstractIntegerSym.valueOf(-2);

  /** Constant integer &quot;-3&quot; */
  public static final IInteger CN3 = AbstractIntegerSym.valueOf(-3);

  /** Constant integer &quot;-4&quot; */
  public static final IInteger CN4 = AbstractIntegerSym.valueOf(-4);

  /** Constant integer &quot;-5&quot; */
  public static final IInteger CN5 = AbstractIntegerSym.valueOf(-5);

  /** Constant integer &quot;-6&quot; */
  public static final IInteger CN6 = AbstractIntegerSym.valueOf(-6);

  /** Constant integer &quot;-7&quot; */
  public static final IInteger CN7 = AbstractIntegerSym.valueOf(-7);

  /** Constant integer &quot;-8&quot; */
  public static final IInteger CN8 = AbstractIntegerSym.valueOf(-8);

  /** Constant integer &quot;-9&quot; */
  public static final IInteger CN9 = AbstractIntegerSym.valueOf(-9);

  /** Constant integer &quot;-10&quot; */
  public static final IInteger CN10 = AbstractIntegerSym.valueOf(-10);

  private static final Map<ISymbol, IExpr> UNARY_INVERSE_FUNCTIONS = new IdentityHashMap<>();

  public static IExpr getUnaryInverseFunction(IExpr symbol) {
    return UNARY_INVERSE_FUNCTIONS.get(symbol);
  }

  public static final java.util.List<ISymbol> DENOMINATOR_NUMERATOR_SYMBOLS;

  public static final java.util.List<IExpr> DENOMINATOR_TRIG_TRUE_EXPRS;

  public static final java.util.List<ISymbol> NUMERATOR_NUMERATOR_SYMBOLS;

  public static final java.util.List<IExpr> NUMERATOR_TRIG_TRUE_EXPRS;

  private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

  /** Causes the current thread to wait until the main initialization has finished. */
  public static final void await() throws InterruptedException {
    COUNT_DOWN_LATCH.await();
  }

  static {
    try {
      AndroidLoggerFix.fix();
      // initialize LOGGER after AndroidLoggerFix !!!
      LOGGER = LogManager.getLogger();
      AST2Expr.initialize();
      ExprParserFactory.initialize();

      PreemptStatus.setNotAllow();
      JASConfig.MAX_DEGREE_KRONECKER_FACTORIZATION = 100;
      JASConfig.MAX_ITERATIONS_KRONECKER_FACTORIZATION = 100;
      ComputerThreads.NO_THREADS = Config.JAS_NO_THREADS;

      initApfloat();

      Slot.setAttributes(ISymbol.NHOLDALL);
      Slot.setEvaluator(ICoreFunctionEvaluator.ARGS_EVALUATOR);
      SlotSequence.setAttributes(ISymbol.NHOLDALL);
      SlotSequence.setEvaluator(ICoreFunctionEvaluator.ARGS_EVALUATOR);
      PatternTest.setAttributes(ISymbol.HOLDREST);
      List.setEvaluator(ICoreFunctionEvaluator.ARGS_EVALUATOR);

      CEmptySequence = headAST0(Sequence);
      CEmptyList = headAST0(List).functionEvaled();
      CEmptyInterval = headAST0(Interval).functionEvaled();
      CEmptyIntervalData = headAST0(IntervalData).functionEvaled();
      CEmptyString = $str("");
      CMissingNotFound = Missing("NotFound").functionEvaled();
      CListC0 = new B1.List(C0).functionEvaled();
      CListC1 = new B1.List(C1).functionEvaled();
      CListC2 = new B1.List(C2).functionEvaled();
      CListCN1 = new B1.List(CN1).functionEvaled();

      CListC0C0 = new B2.List(C0, C0).functionEvaled();
      CListC1C1 = new B2.List(C1, C1).functionEvaled();
      CListC1C2 = new B2.List(C1, C2).functionEvaled();
      CListC2C1 = new B2.List(C2, C1).functionEvaled();
      CListC2C2 = new B2.List(C2, C2).functionEvaled();

      CReturnFalse = new B1.Return(False);
      CReturnTrue = new B1.Return(True);
      CThrowFalse = new B1.Throw(False);
      CThrowTrue = new B1.Throw(True);

      CInfinity = unaryAST1(DirectedInfinity, C1).functionEvaled();
      oo = CInfinity;
      CNInfinity = unaryAST1(DirectedInfinity, CN1).functionEvaled();
      Noo = CNInfinity;
      CIInfinity = unaryAST1(DirectedInfinity, CI).functionEvaled();
      CNIInfinity = unaryAST1(DirectedInfinity, CNI).functionEvaled();
      CComplexInfinity = headAST0(DirectedInfinity).functionEvaled();

      CNPi = new B2.Times(CN1, Pi).functionEvaled();
      CN2Pi = new B2.Times(CN2, Pi).functionEvaled();
      C2Pi = new B2.Times(C2, Pi).functionEvaled();
      CNPiHalf = new B2.Times(CN1D2, Pi).functionEvaled();
      CPiHalf = new B2.Times(C1D2, Pi).functionEvaled();
      CNPiThird = new B2.Times(CN1D3, Pi).functionEvaled();
      CPiThird = new B2.Times(C1D3, Pi).functionEvaled();
      CNPiQuarter = new B2.Times(CN1D4, Pi).functionEvaled();
      CPiQuarter = new B2.Times(C1D4, Pi).functionEvaled();

      CSqrtPi = new B2.Power(Pi, C1D2).functionEvaled();

      CSqrt2 = new B2.Power(C2, C1D2).functionEvaled();
      CSqrt3 = new B2.Power(C3, C1D2).functionEvaled();
      CSqrt5 = new B2.Power(C5, C1D2).functionEvaled();
      CSqrt6 = new B2.Power(C6, C1D2).functionEvaled();
      CSqrt7 = new B2.Power(C7, C1D2).functionEvaled();
      CSqrt10 = new B2.Power(C10, C1D2).functionEvaled();

      C1DSqrt2 = new B2.Power(C2, CN1D2).functionEvaled();
      C1DSqrt3 = new B2.Power(C3, CN1D2).functionEvaled();
      C1DSqrt5 = new B2.Power(C5, CN1D2).functionEvaled();
      C1DSqrt6 = new B2.Power(C6, CN1D2).functionEvaled();
      C1DSqrt7 = new B2.Power(C7, CN1D2).functionEvaled();
      C1DSqrt10 = new B2.Power(C10, CN1D2).functionEvaled();

      IAST Slot0 = new B1.Slot(C0);
      Slot1 = new B1.Slot(C1);
      Slot2 = new B1.Slot(C2);
      Slot3 = new B1.Slot(C3);
      Slot4 = new B1.Slot(C4);
      Slot5 = new B1.Slot(C5);
      Slot6 = new B1.Slot(C6);
      SLOT_CACHE[0] = Slot0;
      SLOT_CACHE[1] = Slot1;
      SLOT_CACHE[2] = Slot2;
      SLOT_CACHE[3] = Slot3;
      SLOT_CACHE[4] = Slot4;
      SLOT_CACHE[5] = Slot5;
      SLOT_CACHE[6] = Slot6;
      for (int i = 7; i < 100; i++) {
        SLOT_CACHE[i] = new B1.Slot(i);
      }

      COMMON_IDS = new IExpr[] {CN1, CN2, CN3, CN4, CN5, CN6, CN7, CN8, CN9, CN10, C0, C1, C2, C3,
          C4, C5, C6, C7, C8, C9, C10, CI, CNI, C1D2, CN1D2, C1D3, CN1D3, C1D4, CN1D4, CD0, CD1,
          CInfinity, CNInfinity, CComplexInfinity, CSqrt2, CSqrt3, CSqrt5, CSqrt6, CSqrt7, CSqrt10,
          C1DSqrt2, C1DSqrt3, C1DSqrt5, C1DSqrt6, C1DSqrt7, C1DSqrt10, Slot1, Slot2,
          // start symbols
          a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z, ASymbol,
          BSymbol, CSymbol, FSymbol, GSymbol,
          // start pattern
          a_, b_, c_, d_, e_, f_, g_, h_, i_, j_, k_, l_, m_, n_, o_, p_, q_, r_, s_, t_, u_, v_,
          w_, x_, y_, z_, A_, B_, C_, F_, G_, a_Symbol, b_Symbol, c_Symbol, d_Symbol, e_Symbol,
          f_Symbol, g_Symbol, h_Symbol, i_Symbol, j_Symbol, k_Symbol, l_Symbol, m_Symbol, n_Symbol,
          o_Symbol, p_Symbol, q_Symbol, r_Symbol, s_Symbol, t_Symbol, u_Symbol, v_Symbol, w_Symbol,
          x_Symbol, y_Symbol, z_Symbol, a_DEFAULT, b_DEFAULT, c_DEFAULT, d_DEFAULT, e_DEFAULT,
          f_DEFAULT, g_DEFAULT, h_DEFAULT, i_DEFAULT, j_DEFAULT, k_DEFAULT, l_DEFAULT, m_DEFAULT,
          n_DEFAULT, o_DEFAULT, p_DEFAULT, q_DEFAULT, r_DEFAULT, s_DEFAULT, t_DEFAULT, u_DEFAULT,
          v_DEFAULT, w_DEFAULT, x_DEFAULT, y_DEFAULT, z_DEFAULT, A_DEFAULT, B_DEFAULT, C_DEFAULT,
          F_DEFAULT, G_DEFAULT};
      short exprID = EXPRID_MAX_BUILTIN_LENGTH;
      for (short i = 0; i < COMMON_IDS.length; i++) {
        GLOBAL_IDS_MAP.put(COMMON_IDS[i], exprID++);
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
      DENOMINATOR_NUMERATOR_SYMBOLS =
          Collections.unmodifiableList(java.util.Arrays.asList(Sin, Cos, Tan, Csc, Sec, Cot));
      DENOMINATOR_TRIG_TRUE_EXPRS =
          Collections.unmodifiableList(java.util.Arrays.asList(C1, C1, Cos, Sin, Cos, Sin));

      NUMERATOR_NUMERATOR_SYMBOLS =
          Collections.unmodifiableList(java.util.Arrays.asList(Sin, Cos, Tan, Csc, Sec, Cot));
      NUMERATOR_TRIG_TRUE_EXPRS =
          Collections.unmodifiableList(java.util.Arrays.asList(Sin, Cos, Sin, C1, C1, Cos));

      ConstantDefinitions.initialize();
      FunctionDefinitions.initialize();
      Integrate.setEvaluator(org.matheclipse.core.reflection.system.Integrate.CONST);
      IOFunctions.initialize();
      Programming.initialize();
      PatternMatching.initialize();
      FileFunctions.initialize();
      Algebra.initialize();
      SimplifyFunctions.initialize();
      StructureFunctions.initialize();
      ExpTrigsFunctions.initialize();
      NumberTheory.initialize();
      BooleanFunctions.initialize();
      LinearAlgebra.initialize();
      TensorFunctions.initialize();
      ListFunctions.initialize();
      SubsetFunctions.initialize();
      SequenceFunctions.initialize();
      Combinatoric.initialize();
      IntegerFunctions.initialize();
      BesselFunctions.initialize();
      SpecialFunctions.initialize();
      StringFunctions.initialize();
      OutputFunctions.initialize();
      RandomFunctions.initialize();
      StatisticsFunctions.initialize();
      HypergeometricFunctions.initialize();
      EllipticIntegrals.initialize();
      PolynomialFunctions.initialize();
      RootsFunctions.initialize();
      SeriesFunctions.initialize();
      AssumptionFunctions.initialize();
      ContainsFunctions.initialize();
      CurveFitterFunctions.initialize();
      VectorAnalysisFunctions.initialize();
      QuantityFunctions.initialize();
      IntervalFunctions.initialize();
      FinancialFunctions.initialize();
      WXFFunctions.initialize();
      WindowFunctions.initialize();
      MinMaxFunctions.initialize();
      GraphFunctions.initialize();
      GraphDataFunctions.initialize();
      AssociationFunctions.initialize();
      GeodesyFunctions.initialize();
      ManipulateFunction.initialize();
      FilterFunctions.initialize();
      EntityFunctions.initialize();
      ClusteringFunctions.initialize();
      SourceCodeFunctions.initialize();
      SparseArrayFunctions.initialize();
      UnitTestingFunctions.initialize();
      BoxesFunctions.initialize();
      NumericArrayFunctions.initialize();
      GraphicsFunctions.initialize();
      CompilerFunctions.initialize();
      JavaFunctions.initialize();
      SidesFunctions.initialize();
      ComputationalGeometryFunctions.initialize();
      PiecewiseFunctions.initialize();
      QuantumPhysicsFunctions.initialize();
      ConstantPhysicsDefinitions.initialize();

      AutomaticRules.initialize();

      COUNT_DOWN_LATCH.countDown();
      Errors.initGeneralMessages();
    } catch (Throwable th) {
      LOGGER.error("F-class initialization failed", th);
      throw th;
    }
  }

  /**
   * Initialize the {@link ApfloatContext}
   */
  private static void initApfloat() {
    int numberOfProcessors = Config.MAX_APFLOAT_PROCESSORS;
    long maxMemoryBlockSize = Config.MAX_APFLOAT_MEMORY_BLOCKSIZE;
    long memoryThreshold = Math.max(maxMemoryBlockSize >> 10, 65536);
    int blockSize = Util.round2down((int) Math.min(memoryThreshold, java.lang.Integer.MAX_VALUE));
    ApfloatContext ctx = ApfloatContext.getContext();
    ctx.setProperty(ApfloatContext.BUILDER_FACTORY, "org.apfloat.internal.LongBuilderFactory");
    ctx.setProperty(ApfloatContext.CACHE_L1_SIZE, "8192");
    ctx.setProperty(ApfloatContext.CACHE_L2_SIZE, "262144");
    ctx.setProperty(ApfloatContext.CACHE_BURST, "32");
    ctx.setProperty(ApfloatContext.SHARED_MEMORY_TRESHOLD,
        java.lang.String.valueOf(maxMemoryBlockSize / numberOfProcessors / 32));
    ctx.setProperty(ApfloatContext.BLOCK_SIZE, java.lang.String.valueOf(blockSize));
    ctx.setFilenameGenerator(new FilenameGenerator("default", "0", "default") {
      @Override
      public synchronized String generateFilename() {
        throw new OverflowException("Apfloat disk file storage is disabled");
      }
    });
    ctx.setMemoryThreshold(memoryThreshold);
    ctx.setCleanupAtExit(false);
    ctx.setNumberOfProcessors(numberOfProcessors);
    ctx.setMaxMemoryBlockSize(maxMemoryBlockSize);
  }

  private static void createInverseFunctionMap() {
    UNARY_INVERSE_FUNCTIONS.put(Abs, Function(Times(CN1, Slot1)));
    UNARY_INVERSE_FUNCTIONS.put(Conjugate, Conjugate);
    UNARY_INVERSE_FUNCTIONS.put(ProductLog, Function(Times(Slot1, Power(E, Slot1))));
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
    UNARY_INVERSE_FUNCTIONS.put(Log2, F.Function(F.Power(F.C2, F.Slot1)));
    UNARY_INVERSE_FUNCTIONS.put(Log10, F.Function(F.Power(F.C10, F.Slot1)));
    UNARY_INVERSE_FUNCTIONS.put(Identity, Identity);

    UNARY_INVERSE_FUNCTIONS.put(Conjugate, Conjugate);
    UNARY_INVERSE_FUNCTIONS.put(Erf, InverseErf);
    UNARY_INVERSE_FUNCTIONS.put(Erfc, InverseErfc);
    UNARY_INVERSE_FUNCTIONS.put(Erfi,
        F.Function(F.Times(F.CNI, F.InverseErf(F.Times(F.CI, F.Slot1)))));

    UNARY_INVERSE_FUNCTIONS.put(Haversine, InverseHaversine);
    UNARY_INVERSE_FUNCTIONS.put(InverseHaversine, Haversine);

    UNARY_INVERSE_FUNCTIONS.put(Gudermannian, InverseGudermannian);
    UNARY_INVERSE_FUNCTIONS.put(InverseGudermannian, Gudermannian);

    UNARY_INVERSE_FUNCTIONS.put(InverseErf, Erf);
    UNARY_INVERSE_FUNCTIONS.put(InverseErfc, Erfc);
  }

  /**
   * Create a new abstract syntax tree (AST).
   *
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @param a
   * @return
   */
  public static final IASTMutable $(final IExpr head, final IExpr... a) {
    switch (a.length) {
      case 0:
        return headAST0(head);
      case 1:
        return unaryAST1(head, a[0]);
      case 2:
        return binaryAST2(head, a[0], a[1]);
      case 3:
        return ternaryAST3(head, a[0], a[1], a[2]);
      default:
        return ast(a, head);
    }
  }

  /**
   * Create a <code>BlankSequence[condition]</code> pattern object for pattern-matching and term
   * rewriting
   *
   * @param condition additional condition which should be checked in pattern-matching
   * @return IPattern
   */
  public static PatternSequence $bs(final IExpr condition) {
    return org.matheclipse.core.expression.PatternSequence.valueOf(null, condition, false);
  }

  /**
   * Create a <code>BlankNullSequence[condition]</code> pattern object for pattern-matching and term
   * rewriting
   *
   * @param condition additional condition which should be checked in pattern-matching
   * @return IPattern
   */
  public static PatternSequence $bns(final IExpr condition) {
    return org.matheclipse.core.expression.PatternSequence.valueOf(null, condition, false);
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
   * @param condition additional condition which should be checked in pattern-matching
   * @return IPattern
   */
  public static IPattern $b(final IExpr condition) {
    return org.matheclipse.core.expression.Blank.valueOf(condition);
  }

  /**
   * Create a <code>Blank[condition]</code> pattern object for pattern-matching and term rewriting
   *
   * @param condition additional condition which should be checked in pattern-matching
   * @param def if <code>true</code> use a default value in pattern-matching if an argument is
   *        optional
   * @return IPattern
   */
  public static IPattern $b(final IExpr condition, boolean def) {
    return new org.matheclipse.core.expression.Blank(condition, def);
  }

  /**
   * Create a <code>Pattern[]</code> pattern for pattern-matching and term rewriting
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
   * @param matchDefaultValue if <code>true</code>, the pattern can match to a default value
   *        associated with the AST's head the pattern is used in
   * @return IPattern
   */
  public static IPattern $p(final ISymbol symbol, boolean matchDefaultValue) {
    return $p(symbol, null, matchDefaultValue);
  }

  /**
   * Create a pattern for pattern-matching and term rewriting
   *
   * @param symbol
   * @param headerCheck additional condition which should be checked for the header of an expression
   *        in pattern-matching
   * @return IPattern
   */
  public static IPattern $p(final ISymbol symbol, final IExpr headerCheck) {
    return org.matheclipse.core.expression.Pattern.valueOf(symbol, headerCheck);
  }

  /**
   * Create a pattern for pattern-matching and term rewriting
   *
   * @param symbol
   * @param headerCheck additional condition which should be checked for the header of an expression
   * @param matchDefaultValue if <code>true</code>, the pattern can match to a default value
   *        associated with the AST's head the pattern is used in
   * @return IPattern
   */
  public static IPattern $p(final ISymbol symbol, final IExpr headerCheck,
      final boolean matchDefaultValue) {
    return org.matheclipse.core.expression.Pattern.valueOf(symbol, headerCheck, matchDefaultValue);
  }

  /**
   * Create a pattern for pattern-matching and term rewriting
   *
   * @param symbolName
   * @return IPattern
   */
  public static IPattern $p(final String symbolName) {
    // if (symbolName == null) {
    // return org.matheclipse.core.expression.Pattern.valueOf(null);
    // }
    return org.matheclipse.core.expression.Pattern.valueOf($s(symbolName));
  }

  /**
   * Create a pattern for pattern-matching and term rewriting
   *
   * @param symbolName
   * @param def use a default value for this pattern if necessary
   * @return IPattern
   */
  public static IPattern $p(final String symbolName, boolean def) {
    return $p($s(symbolName), null, def);
  }

  /**
   * Create a pattern for pattern-matching and term rewriting
   *
   * @param symbolName
   * @param check additional condition which should be checked in pattern-matching
   * @return IPattern
   */
  public static IPattern $p(final String symbolName, final IExpr check) {
    // if (symbolName == null) {
    // return org.matheclipse.core.expression.Pattern.valueOf(null, check);
    // }
    return org.matheclipse.core.expression.Pattern.valueOf($s(symbolName), check);
  }

  /**
   * Create a pattern for pattern-matching and term rewriting
   *
   * @param symbolName
   * @param check additional condition which should be checked in pattern-matching
   * @param def use a default value for this pattern if necessary
   * @return IPattern
   */
  public static IPattern $p(final String symbolName, final IExpr check, boolean def) {
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
   * @param check additional condition which should be checked in pattern-matching
   * @param def if <code>true</code>, the pattern can match to a default value associated with the
   *        AST's head the pattern is used in.
   * @return IPattern
   */
  public static IPattern pattern(final ISymbol symbol, final IExpr check, final boolean def) {
    return org.matheclipse.core.expression.Pattern.valueOf(symbol, check, def);
  }

  /**
   * Create a new PatternSequence <code>BlankSequence</code>.
   *
   * @param symbol
   * @return IPattern
   */
  public static IPatternSequence $ps(final ISymbol symbol) {
    return PatternSequence.valueOf(symbol, false);
  }

  /**
   * Create a new PatternSequence <code>BlankSequence</code> or <code>BlankNullSequence</code>.
   *
   * @param symbol the associated symbol of the pattern sequence. Maybe <code>null</code>.
   * @param zeroArgsAllowed if <code>true</code>, 0 arguments are allowed, otherwise the number of
   *        args has to be >= 1.
   * @return
   */
  public static IPatternSequence $ps(final ISymbol symbol, boolean zeroArgsAllowed) {
    return PatternSequence.valueOf(symbol, zeroArgsAllowed);
  }

  /**
   * Create a new PatternSequence <code>BlankSequence</code> or <code>BlankNullSequence</code>.
   *
   * @param symbol the associated symbol of the pattern sequence. Maybe <code>null</code>.
   * @param check a header check.Maybe <code>null</code>.
   * @return IPatternSequence
   */
  public static IPatternSequence $ps(final ISymbol symbol, final IExpr check) {
    return PatternSequence.valueOf(symbol, check, false);
  }

  /**
   * Create a new PatternSequence <code>BlankSequence</code> or <code>BlankNullSequence</code>.
   *
   * @param symbol
   * @param check additional condition which should be checked in pattern-matching
   * @param def if <code>true</code>, the pattern can match to a default value associated with the
   *        AST's head the pattern is used in.
   * @param zeroArgsAllowed if <code>true</code> 0 argument sequences are allowed for this pattern
   * @return IPattern
   */
  public static IPatternSequence $ps(final ISymbol symbol, final IExpr check, final boolean def,
      boolean zeroArgsAllowed) {
    return PatternSequence.valueOf(symbol, check, def, zeroArgsAllowed);
  }

  public static IPatternSequence $OptionsPattern() {
    return org.matheclipse.core.expression.OptionsPattern.valueOf(null);
  }

  public static IPatternSequence $OptionsPattern(final ISymbol symbol) {
    return org.matheclipse.core.expression.OptionsPattern.valueOf(symbol);
  }

  public static IPatternSequence $OptionsPattern(final ISymbol symbol, final IExpr defaultOptions) {
    return org.matheclipse.core.expression.OptionsPattern.valueOf(symbol, defaultOptions);
  }

  /**
   * @param patternExpr
   * @param min if <code>min==0</code> RepeatedNull is assumed
   * @param max
   * @param engine
   * @return
   */
  public static IPatternSequence $Repeated(final IExpr patternExpr, int min, int max,
      EvalEngine engine) {
    boolean nullAllowed = (min <= 0);
    return org.matheclipse.core.expression.RepeatedPattern.valueOf(patternExpr, min, max,
        nullAllowed, engine);
  }

  /**
   * Create a pattern for pattern-matching and term rewriting
   *
   * @param symbolName the name of the pattrn symbol
   * @return IPattern
   */
  public static IPatternSequence $ps(final String symbolName) {
    return PatternSequence.valueOf($s(symbolName), false);
  }

  /**
   *
   *
   * <pre>
   * SymbolQ(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is <code>True</code> if <code>x</code> is a symbol, or <code>False</code> otherwise.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SymbolQ(a)
   * True
   * &gt;&gt; SymbolQ(1)
   * False
   * &gt;&gt; SymbolQ(a + b)
   * False
   * </pre>
   */
  public static IAST SymbolQ(final IExpr x) {
    return new AST1(SymbolQ, x);
  }

  /**
   * Full symmetry
   *
   * @param a0
   * @return
   */
  public static IAST Symmetric(final IExpr a0) {
    return new AST1(Symmetric, a0);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/SymmetricMatrixQ.md">SymmetricMatrixQ</a>
   */
  public static IAST SymmetricMatrixQ(final IExpr a0) {
    return new AST1(SymmetricMatrixQ, a0);
  }

  /**
   * Converts and evaluates arbitrary expressions to a Symja type.
   *
   * <pre>
   * Java Object       -&gt; Symja object
   * -------------------------------------
   * null object          {@link S#Null} symbol
   * IExpr                {@link IExpr} type
   * Boolean              {@link S#True} or {@link S#False} symbol
   * BigInteger           {@link IInteger} value
   * BigDecimal           {@link INum} with {@link Apfloat#Apfloat(java.math.BigDecimal)} value
   * Double               {@link INum}  with doubleValue() value
   * Float                {@link INum}  with doubleValue() value
   * Integer              {@link IInteger} with intValue() value
   * Long                 {@link IInteger} with longValue() value
   * Number               {@link INum} with doubleValue() value
   * java.util.Collection list of elements
   *                      1..nth element of the list give the elements of the List()
   * Object[]             a list of converted objects
   * int[]                a list of {@link IInteger} integer values
   * double[]             a vector ASTRealVector of <code>double</code> values
   * double[][]           a matrix ASTRealMatrix of <code>double</code> values
   * Complex[]            a list of {@link IComplexNum} values
   * boolean[]            a list of {@link S#True} or {@link S#False} symbols
   *
   * </pre>
   *
   * @param object
   * @return the <code>object</code> converted to a {@link IExpr}}
   */
  public static IExpr symjify(final Object object) {
    return symjify(object, true);
  }

  /**
   * Converts and evaluates arbitrary expressions to a Symja type.
   *
   * <pre>
   * Java Object       -&gt; Symja object
   * -------------------------------------
   * null object          {@link S#Null} symbol
   * IExpr                {@link IExpr} type
   * Boolean              {@link S#True} or {@link S#False} symbol
   * BigInteger           {@link IInteger} value
   * BigDecimal           {@link INum} with {@link Apfloat#Apfloat(java.math.BigDecimal)} value
   * Double               {@link INum}  with doubleValue() value
   * Float                {@link INum}  with doubleValue() value
   * Integer              {@link IInteger} with intValue() value
   * Long                 {@link IInteger} with longValue() value
   * Number               {@link INum} with doubleValue() value
   * java.util.Collection list of elements
   *                      1..nth element of the list give the elements of the List()
   * Object[]             a list of converted objects
   * int[]                a list of {@link IInteger} integer values
   * double[]             a vector ASTRealVector of <code>double</code> values
   * double[][]           a matrix ASTRealMatrix of <code>double</code> values
   * Complex[]            a list of {@link IComplexNum} values
   * boolean[]            a list of {@link S#True} or {@link S#False} symbols
   *
   * </pre>
   *
   * @param object
   * @param evaluate if <code>true</code> evaluate the parsed string
   * @return the <code>object</code> converted to an {@link IExpr}}
   */
  public static IExpr symjify(final Object object, boolean evaluate) {
    IExpr temp = Object2Expr.convert(object, true, false);
    return evaluate ? eval(temp) : temp;
  }

  /**
   * Parses and evaluates a Java string to a Symja expression. May throw an SyntaxError exception,
   * if the string couldn't be parsed.
   *
   * @param str the expression which should be parsed
   * @return
   * @throws SyntaxError
   */
  public static IExpr symjify(final String str) {
    return symjify(str, true);
  }

  /**
   * Parses a Java string to a Symja expression. May throw a SyntaxError exception, if the string
   * couldn't be parsed.
   *
   * @param str the expression which should be parsed
   * @param evaluate if <code>true</code> evaluate the parsed string
   * @return
   * @throws SyntaxError
   */
  public static IExpr symjify(final String str, boolean evaluate) {
    EvalEngine engine = EvalEngine.get();
    ExprParser parser = new ExprParser(engine);
    IExpr temp = parser.parse(str);
    return evaluate ? engine.evaluate(temp) : temp;
  }

  /**
   * @param value
   * @return {@link IInteger} integer value
   */
  public static IInteger symjify(final long value) {
    return ZZ(value);
  }

  /**
   * @param value
   * @return {@link INum} double wrapper
   */
  public static INum symjify(final double value) {
    return num(value);
  }

  /**
   * Return {@link S#True} or {@link S#False} symbol
   *
   * @param value
   * @return {@link S#True} or {@link S#False} symbol
   */
  public static IBuiltInSymbol symjify(final boolean value) {
    return value ? True : False;
  }

  /**
   * <p>
   * Get or create a global predefined symbol which is retrieved from the SYSTEM context map or
   * created or retrieved from the SYSTEM context variables map.
   *
   * @param symbolName the name of the symbol
   */
  public static ISymbol $s(final String symbolName) {
    String name = symbolName;
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      if (symbolName.length() != 1) {
        name = symbolName.toLowerCase(Locale.ENGLISH);
      }
    }
    ISymbol symbol = org.matheclipse.core.expression.Context.PREDEFINED_SYMBOLS_MAP.get(name);
    if (symbol != null) {
      return symbol;
    }
    symbol = HIDDEN_SYMBOLS_MAP.get(name);
    if (symbol != null) {
      return symbol;
    }
    if (Config.SERVER_MODE) {
      if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS
          || java.lang.Character.isUpperCase(name.charAt(0))) {
        if (SYMBOL_OBSERVER.createPredefinedSymbol(name)) {
          // second try, because the symbol may now be added to
          // fSymbolMap
          ISymbol secondTry =
              org.matheclipse.core.expression.Context.PREDEFINED_SYMBOLS_MAP.get(name);
          if (secondTry != null) {
            return secondTry;
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
      symbol = new BuiltInDummy(name);
      // symbol = symbol(name);
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

  public static ISymbol $rubi(final String symbolName) {
    return $rubi(symbolName, BuiltInSymbol.DUMMY_EVALUATOR);
  }

  public static ISymbol $rubi(final String symbolName, IEvaluator evaluator) {
    String name = symbolNameNormalized(symbolName);
    ISymbol symbol = org.matheclipse.core.expression.Context.RUBI.get(name);
    if (symbol != null) {
      return symbol;
    }
    BuiltInRubi bSymbol = new BuiltInRubi(name);
    bSymbol.setEvaluator(evaluator);
    org.matheclipse.core.expression.Context.RUBI.put(name, bSymbol);
    return bSymbol;
  }

  public static String symbolNameNormalized(final String symbolName) {
    return ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS
        ? (symbolName.length() == 1 ? symbolName : symbolName.toLowerCase(Locale.ENGLISH))
        : symbolName;
  }

  public static final IStringX $str(final char ch) {
    return StringX.valueOf(ch);
  }

  /**
   * Create a string expression
   *
   * @param str
   * @return
   */
  public static final IStringX $str(final String str) {
    return StringX.valueOf(str);
  }

  public static final IStringX $str(final String str, final short mimeType) {
    return StringX.valueOf(str, mimeType);
  }

  public static IAST Abs(final IExpr z) {
    return new AST1(Abs, z);
  }

  public static IAST AbsoluteCorrelation(final IExpr a0, final IExpr a1) {
    return new AST2(AbsoluteCorrelation, a0, a1);
  }

  public static IASTMutable AddSides(final IExpr equationOrInequality, final IExpr a1) {
    return new AST2(AddSides, equationOrInequality, a1);
  }

  public static IAST Alternatives(final IExpr... a) {
    return function(Alternatives, a);
  }

  public static IExpr and(IExpr a, Integer i) {
    return And(a, ZZ(i.longValue()));
  }

  public static IExpr and(IExpr a, java.math.BigInteger i) {
    return And(a, ZZ(i));
  }

  public static IExpr and(Integer i, IExpr b) {
    return And(ZZ(i.longValue()), b);
  }

  public static IExpr and(java.math.BigInteger i, IExpr b) {
    return And(ZZ(i), b);
  }

  public static IASTAppendable And() {
    return ast(And);
  }

  public static IASTMutable AmbientLight(final IExpr color) {
    return new AST1(AmbientLight, color);
  }

  /**
   * <code>expr1 && expr2 && expr3 ...</code> evaluates each expression in turn, returning
   * {@link S#False} as soon as an expression evaluates to {@link S#False}. If all expressions
   * evaluate to {@link S#True}, it returns {@link S#True}.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/And.md">And</a>
   *
   * @param expr
   * @return
   */
  public static IAST And(final IExpr... expr) {
    return function(And, expr);
  }

  /**
   * <code>expr1 && expr2</code> evaluates each expression in turn, returning {@link S#False} as
   * soon as an expression evaluates to {@link S#False}. If all expressions evaluate to
   * {@link S#True}, it returns {@link S#True}.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/And.md">And</a>
   *
   * @param expr1
   * @param expr2
   * @return
   */
  public static IAST And(final IExpr expr1, final IExpr expr2) {
    return new B2.And(expr1, expr2);
  }

  /**
   * <code>expr1 && expr2 && expr3</code> evaluates each expression in turn, returning
   * {@link S#False} as soon as an expression evaluates to {@link S#False}. If all expressions
   * evaluate to {@link S#True}, it returns {@link S#True}.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/And.md">And</a>
   *
   * @param expr1
   * @param expr2
   * @param expr3
   * @return
   */
  public static IAST And(final IExpr expr1, final IExpr expr2, final IExpr expr3) {
    return new B3.And(expr1, expr2, expr3);
  }

  public static IAST AngerJ(final IExpr v, final IExpr z) {
    return new AST2(AngerJ, v, z);
  }

  public static IAST AngerJ(final IExpr v, final IExpr m, final IExpr z) {
    return new AST3(AngerJ, v, m, z);
  }

  public static IAST AngleVector(final IExpr a0) {
    return new AST1(AngleVector, a0);
  }

  public static IAST AntiSymmetric(final IExpr a0) {
    return new AST1(AntiSymmetric, a0);
  }

  public static IAST Apart(final IExpr a0) {
    return new AST1(Apart, a0);
  }

  public static IAST Apart(final IExpr a0, final IExpr a1) {
    return new AST2(Apart, a0, a1);
  }

  public static IAST AppellF1(final IExpr a, final IExpr b1, final IExpr b2, final IExpr c,
      final IExpr z1, final IExpr z2) {
    IExpr[] arr = new IExpr[] {a, b1, b2, c, z1, z2};
    return new AST(AppellF1, arr);
  }

  public static IAST Append(final IExpr a0, final IExpr a1) {
    return new AST2(Append, a0, a1);
  }

  public static IAST AppendTo(final IExpr a0, final IExpr a1) {
    return new AST2(AppendTo, a0, a1);
  }

  /**
   * Operator <code>@@</code>
   *
   * @param a0
   * @param a1
   * @return
   */
  public static IASTMutable Apply(final IExpr a0, final IExpr a1) {
    return new AST2(Apply, a0, a1);
  }

  public static IASTMutable Apply(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Apply, a0, a1, a2);
  }

  public static IASTMutable ApplySides(final IExpr a0, final IExpr equationOrInequality) {
    return new AST2(ApplySides, a0, equationOrInequality);
  }

  public static IAST AiryAi(final IExpr a0) {
    return new AST1(AiryAi, a0);
  }

  public static IAST AiryAiPrime(final IExpr a0) {
    return new AST1(AiryAiPrime, a0);
  }

  public static IAST AiryBi(final IExpr a0) {
    return new AST1(AiryBi, a0);
  }

  public static IAST AiryBiPrime(final IExpr a0) {
    return new AST1(AiryBiPrime, a0);
  }

  public static IAST Array(final IExpr a0, final IExpr a1) {
    return new AST2(Array, a0, a1);
  }

  public static IAST Arrays(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Arrays, a0, a1, a2);
  }

  public static IAST Arrays(final IExpr a0, final IExpr a1) {
    return new AST2(Arrays, a0, a1);
  }

  public static IAST Arrays(final IExpr a0) {
    return new AST1(Arrays, a0);
  }

  public static IAST ArcCos(final IExpr z) {
    return new AST1(ArcCos, z);
  }

  public static IAST ArcCosh(final IExpr z) {
    return new AST1(ArcCosh, z);
  }

  public static IAST ArcCot(final IExpr z) {
    return new AST1(ArcCot, z);
  }

  public static IAST ArcCoth(final IExpr z) {
    return new AST1(ArcCoth, z);
  }

  public static IAST ArcCsc(final IExpr z) {
    return new AST1(ArcCsc, z);
  }

  public static IAST ArcCsch(final IExpr z) {
    return new AST1(ArcCsch, z);
  }

  public static IAST ArcSec(final IExpr z) {
    return new AST1(ArcSec, z);
  }

  public static IAST ArcSech(final IExpr z) {
    return new AST1(ArcSech, z);
  }

  public static IAST ArcSin(final IExpr z) {

    return new AST1(ArcSin, z);
  }

  public static IAST ArcSinh(final IExpr z) {
    return new AST1(ArcSinh, z);
  }

  public static IAST ArcTan(final IExpr z) {
    return new AST1(ArcTan, z);
  }

  public static IAST ArcTan(final IExpr x, final IExpr y) {
    return new AST2(ArcTan, x, y);
  }

  public static IAST ArcTanh(final IExpr z) {
    return new AST1(ArcTanh, z);
  }

  public static IAST Arg(final IExpr z) {
    return new AST1(Arg, z);
  }

  /**
   *
   *
   * <pre>
   * 'ArrayQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * tests whether expr is a full array.
   *
   * </blockquote>
   *
   * <pre>
   * 'ArrayQ(expr, pattern)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * also tests whether the array depth of expr matches pattern.
   *
   * </blockquote>
   *
   * <pre>
   * 'ArrayQ(expr, pattern, test)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * furthermore tests whether <code>test</code> yields <code>True</code> for all elements of expr.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ArrayQ(a)
   * False
   * &gt;&gt; ArrayQ({a})
   * True
   * &gt;&gt; ArrayQ({{{a}},{{b,c}}})
   * False
   * &gt;&gt; ArrayQ({{a, b}, {c, d}}, 2, SymbolQ)
   * True
   * </pre>
   */
  public static IAST ArrayQ(final IExpr a0) {
    return new AST1(ArrayQ, a0);
  }

  /**
   * The domain of arrays.
   *
   * @param dimension
   * @return <code>Arrays(dimensions, Complexes, {})</code>.
   */
  public static IAST Arrays(final IAST dimension) {
    return Arrays(dimension, Complexes, List());
  }

  /**
   * The domain of arrays.
   *
   * @param dimension
   * @param domain
   * @return <code>Arrays(dimensions, domain, {})</code>.
   */
  public static IAST Arrays(final IAST dimension, ISymbol domain) {
    return Arrays(dimension, domain, List());
  }

  /**
   * The domain of arrays.
   *
   * @param dimension
   * @param domain
   * @param symmetry
   * @return <code>Arrays(dimensions, domain, symmetry)</code>.
   */
  public static IAST Arrays(final IAST dimension, ISymbol domain, IAST symmetry) {
    return new AST3(Arrays, dimension, domain, symmetry);
  }

  public static IAST ArithmeticGeometricMean(final IExpr a0, final IExpr a1) {
    return new AST2(ArithmeticGeometricMean, a0, a1);
  }

  /**
   * Create an association data structure <code>&lt;| x0-&gt;y0, x1-&gt;y1, ... |&gt;</code> from a
   * list of rules <code>{x0-&gt;y0, x1-&gt;y1, ... }</code>.
   * 
   * @param listOfRules a list of rules <code>{x0-&gt;y0, x1-&gt;y1, ... }</code>
   * @return
   */
  public static IAssociation assoc(final IAST listOfRules) {
    if (listOfRules.isAST1() && listOfRules.arg1().isListOfRules(true)) {
      return new ASTAssociation((IAST) listOfRules.arg1());
    }
    return new ASTAssociation(listOfRules);
  }

  /**
   * Create an association data structure <code>&lt;| x0-&gt;y0, x1-&gt;y1, ... |&gt;</code> from a
   * map of rules.
   * 
   * @param templateAssociation the template association, which determines the type of rule
   *        (<code>Rule</code> or <code>RuleDelayed</code> and the key of the new rule.
   * @param mapOfRules a map of keys to {@link IASTMutable} values
   * @return
   */
  public static IAssociation assoc(final IAssociation templateAssociation,
      final Map<IExpr, IASTMutable> mapOfRules) {
    ASTAssociation association = new ASTAssociation();
    for (int i = 1; i < templateAssociation.size(); i++) {
      IAST rule1 = templateAssociation.getRule(i);
      IExpr rule1Key = rule1.arg1();
      IExpr newRuleRHS = mapOfRules.get(rule1Key);
      if (newRuleRHS == null) {
        newRuleRHS = S.Nonexistent;
      }
      association.appendRule(F.binaryAST2(rule1.head(), rule1Key, newRuleRHS));
    }

    return association;
  }

  public static IAssociation assoc() {
    return new ASTAssociation();
  }

  /**
   * Create a sparse array from the list of rules.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SparseArray.md">SparseArray</a>
   *
   * @param arrayRulesList
   * @return a ISparseArray instance
   */
  public static ISparseArray sparseArray(final IAST arrayRulesList) {
    return SparseArrayExpr.newArrayRules(arrayRulesList, null, -1, C0);
  }

  /**
   * Create a sparse array in the given <code>dimension</code> from the list of rules.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SparseArray.md">SparseArray</a>
   *
   * @param arrayRulesList
   * @param dimension
   * @return a ISparseArray instance
   */
  public static ISparseArray sparseArray(final IAST arrayRulesList, int[] dimension) {
    return SparseArrayExpr.newArrayRules(arrayRulesList, dimension, 0, C0);
  }

  /**
   * Create a sparse array from the dense list representation (for example dense vectors and
   * matrices)
   * 
   * @param denseList
   * @param defaultValue default value for positions not specified in the dense list representation.
   * @return <code>null</code> if a new <code>SparseArrayExpr</code> cannot be created
   */
  public static ISparseArray sparseArray(final IAST denseList, IExpr defaultValue) {
    return SparseArrayExpr.newDenseList(denseList, defaultValue);
  }

  /**
   * Generate a <code>n x m</code> sparse matrix. The indices start in Java convention with
   * <code>0</code>.
   *
   * @param binaryFunction if the returned value unequals <code>0</code>, the value will be stored
   *        in the sparse matrix
   * @param n the number of rows of the matrix.
   * @param m the number of columns of the matrix.
   * @return
   */
  public static ISparseArray sparseMatrix(BiIntFunction<? extends IExpr> binaryFunction, int n,
      int m) {
    if (n > Config.MAX_MATRIX_DIMENSION_SIZE || m > Config.MAX_MATRIX_DIMENSION_SIZE) {
      ASTElementLimitExceeded.throwIt(((long) n) * ((long) m));
    }
    int[] dimension = new int[] {n, m};
    SparseArrayExpr sparseMatrix = SparseArrayExpr.newArrayRules(F.CEmptyList, dimension, 0, C0);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        IExpr value = binaryFunction.apply(i, j);
        if (!value.isZero()) {
          sparseMatrix.set(new int[] {i + 1, j + 1}, value);
        }
      }
    }
    return sparseMatrix;
  }

  /**
   * Creates a new AST from the given <code>ast</code> and <code>head</code>. if <code>include
   * </code> is set to <code>true </code> all arguments from index first to last-1 are copied in the
   * new list if <code>include</code> is set to <code> false </code> all arguments excluded from
   * index first to last-1 are copied in the new list
   */
  public static IAST ast(final IAST f, final IExpr head, final boolean include, final int first,
      final int last) {
    AST ast = null;
    if (include) {
      ast = AST.newInstance(last - first, head, false);
      // range include
      ast.appendAll(f, first, last);
      // for (int i = first; i < last; i++) {
      // ast.append(f.get(i));
      // }
    } else {
      ast = AST.newInstance(f.size() - last + first - 1, head, false);
      // range exclude
      ast.appendAll(f, 1, first);
      // for (int i = 1; i < first; i++) {
      // ast.append(f.get(i));
      // }
      ast.appendAll(f, last, f.size());
      // for (int j = last; j < f.size(); j++) {
      // ast.append(f.get(j));
      // }
    }
    return ast;
  }

  /**
   * Create a new abstract syntax tree (AST).
   *
   * @param head the header expression of the function. In the case, that the {@code ast} represents
   *        a function like {@code f[x,y], Sin[x],...}, the {@code head} must be an instance of type
   *        {@link ISymbol}
   */
  public static final IASTAppendable ast(final IExpr head) {
    return AST.newInstance(head);
  }

  /**
   * Create a new abstract syntax tree (AST). Elements can be added to the end of the created AST
   * instance with the {@link IASTAppendable#append(IExpr)} method.
   *
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @param initialCapacity the initial capacity (i.e. number of arguments without the header
   *        element) of the list.
   * @return
   */
  public static IASTAppendable ast(final IExpr head, final int initialCapacity) {
    if (initialCapacity > Config.MIN_LIMIT_PERSISTENT_LIST) {
      return ASTRRBTree.newInstance(initialCapacity, head);
    }
    return AST.newInstance(initialCapacity, head);
  }

  /**
   * Create a new abstract syntax tree (AST).
   *
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @param initialCapacity the initial capacity (i.e. number of arguments without the header
   *        element) of the list.
   * @param initNull initialize all elements with <code>null</code>.
   * @return
   * @deprecated use {@link #ast(IExpr, int)} or {@link #astMutable(IExpr, int)}
   */
  @Deprecated
  public static IASTAppendable ast(final IExpr head, final int initialCapacity,
      final boolean initNull) {
    return AST.newInstance(initialCapacity, head, initNull);
  }

  /**
   * Create a new abstract syntax tree (AST). Elements can be added at the end of the created AST
   * instance with the {@link IASTAppendable#append(IExpr)} method.
   *
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @param collection the collection which holds the elements which should be appended
   * @return
   */
  public static IASTAppendable ast(final IExpr head, Collection<? extends IExpr> collection) {
    return ast(head, collection, 0);
  }

  /**
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @param collection the collection which holds the elements which should be appended
   * @param initialCapacity the additional capacity added to the collections size (i.e. number of
   *        arguments without the header element) of the list.
   * @return
   */
  public static IASTAppendable ast(final IExpr head, Collection<? extends IExpr> collection,
      int initialCapacity) {
    IASTAppendable result = ast(head, collection.size() + initialCapacity);
    result.appendAll(collection);
    return result;
  }

  /**
   * Create a new abstract syntax tree (AST) with pre- allocated elements set to <code>null</code>.
   * Elements can be set in the created AST instance with the {@link IASTMutable#set(int, IExpr)}
   * method.
   *
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @param initialCapacity the initial capacity (i.e. number of arguments without the header
   *        element) of the list.
   * @return
   */
  public static IASTMutable astMutable(final IExpr head, final int initialCapacity) {
    return AST.newInstance(initialCapacity, head, true);
  }

  /**
   * Create a new <code>List()</code> with <code>copies</code> number of arguments, which are set to
   * <code>value</code>.
   *
   * @param value initialize all elements with <code>value</code>.
   * @param copies the initial capacity (i.e. number of arguments without the header element) of the
   *        list.
   * @return
   */
  public static IASTAppendable constantArray(final IExpr value, final int copies) {
    return value.constantArray(List, 0, copies);
  }

  /**
   * Create a new abstract syntax tree (AST) with a <code>head</code> and <code>copies</code> number
   * of arguments, which are set to <code>value</code>.
   *
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @param value initialize all elements with <code>value</code>.
   * @param copies the initial capacity (i.e. number of arguments without the header element) of the
   *        list.
   * @return
   */
  public static IASTAppendable constantArray(final IExpr head, final IExpr value,
      final int copies) {
    return value.constantArray(head, 0, copies);
  }

  /**
   * Create a new function expression (AST - abstract syntax tree).
   *
   * @param arr an array of arguments for this function expression
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @return
   */
  public static IASTAppendable ast(final IExpr[] arr, final IExpr head) {
    return new AST(head, arr);
  }

  /**
   * Create a new function expression (AST - abstract syntax tree), where all arguments are Java
   * <code>int</code> values.
   *
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @param arr the integer arguments of the function expression
   * @return
   */
  public static IASTAppendable ast(final ISymbol head, final int[] arr) {
    return AST.newInstance(head, arr);
  }

  /**
   * Create a new function expression (AST - abstract syntax tree), where all arguments are Java
   * <code>org.hipparchus.complex.Complex</code> values.
   *
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @param arr the <code>org.hipparchus.complex.Complex</code> arguments of the function expression
   * @return
   */
  public static IASTAppendable ast(final ISymbol head, final org.hipparchus.complex.Complex[] arr) {
    return AST.newInstance(head, false, arr);
  }

  /**
   * Create a new function expression (AST - abstract syntax tree), where all arguments are Java
   * {@link ComplexNum} values.
   *
   * @param head
   * @param evalComplex if <code>true</code> test if the imaginary part of the complex number is
   *        zero and insert a {@link Num} real value.
   * @param arr the complex number arguments
   * @return
   */
  public static IASTAppendable ast(final ISymbol head, boolean evalComplex,
      org.hipparchus.complex.Complex[] arr) {
    return AST.newInstance(head, evalComplex, arr);
  }

  /**
   *
   *
   * <pre>
   * AtomQ(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * is true if <code>x</code> is an atom (an object such as a number or string, which cannot be
   * divided into subexpressions using 'Part').
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; AtomQ(x)
   * True
   *
   * &gt;&gt; AtomQ(1.2)
   * True
   *
   * &gt;&gt; AtomQ(2 + I)
   * True
   *
   * &gt;&gt; AtomQ(2 / 3)
   * True
   *
   * &gt;&gt; AtomQ(x + y)
   * False
   * </pre>
   */
  public static IAST AtomQ(final IExpr a) {
    return new AST1(AtomQ, a);
  }

  public static IAST Attributes(final IExpr a) {
    return new AST1(Attributes, a);
  }

  public static IAST BaseForm(final IExpr a0, final IExpr a1) {
    return new AST2(BaseForm, a0, a1);
  }

  /**
   * Bell number.
   *
   * @param a0
   * @return
   */
  public static IAST BellB(final IExpr a0) {
    return new AST1(BellB, a0);
  }

  /**
   * Bell polynomial.
   *
   * @param a0
   * @param a1
   * @return
   */
  public static IAST BellB(final IExpr a0, final IExpr a1) {
    return new AST2(BellB, a0, a1);
  }

  public static IAST BellY(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(BellY, a0, a1, a2);
  }

  public static IAST BernoulliB(final IExpr a0) {
    return new AST1(BernoulliB, a0);
  }

  public static IAST BernoulliB(final IExpr a0, final IExpr a1) {
    return new AST2(BernoulliB, a0, a1);
  }

  public static IAST BernoulliDistribution(final IExpr a0) {
    return new AST1(BernoulliDistribution, a0);
  }

  /**
   * Create a function <code>head(arg1, arg2)</code> with 2 arguments without evaluation.
   *
   * @param head
   * @param arg1
   * @param arg2
   * @return
   */
  public static final IASTAppendable binary(final IExpr head, final IExpr arg1, final IExpr arg2) {
    return new AST(new IExpr[] {head, arg1, arg2});
  }

  /**
   * Create a function <code>head(arg1, arg2)</code> with 2 argument as an <code>AST2</code> mutable
   * object without evaluation.
   *
   * @param head
   * @param arg1
   * @param arg2
   * @return
   */
  public static final IASTMutable binaryAST2(final IExpr head, final IExpr arg1, final IExpr arg2) {
    return new AST2(head, arg1, arg2);
  }

  public static final IASTMutable binaryAST2(final IExpr head, final String arg1,
      final IExpr arg2) {
    return new AST2(head, $str(arg1), arg2);
  }

  public static final IASTMutable binaryAST2(final IExpr head, final String arg1,
      final String arg2) {
    return new AST2(head, $str(arg1), $str(arg2));
  }

  public static IAST Binomial(final IExpr a0, final IExpr a1) {
    return new AST2(Binomial, a0, a1);
  }

  public static IAST Binomial(final int a0, final int a1) {
    return new AST2(Binomial, ZZ(a0), ZZ(a1));
  }

  public static IAST BlankSequence() {
    return new AST0(BlankSequence);
  }

  public static IAST Block(final IExpr a0, final IExpr a1) {
    return new AST2(Block, a0, a1);
  }

  /**
   * Returns symbol "True" or "False" (type ISymbol) depending on the boolean value.
   *
   * @param value
   * @return
   * @deprecated use {@link #booleSymbol(boolean)} instead
   */
  @Deprecated
  public static ISymbol bool(final boolean value) {
    return booleSymbol(value);
  }

  /**
   * Returns symbol "True" or "False" (type ISymbol) depending on the boolean value.
   *
   * @param value
   * @return
   */
  public static ISymbol booleSymbol(final boolean value) {
    return value ? True : False;
  }

  /**
   * Returns integers 1 or 0 (type ISymbol) depending on the boolean value <code>true</code> or
   * <code>false</code>.
   *
   * @param value
   * @return
   */
  public static IInteger booleInteger(final boolean value) {
    return value ? F.C1 : F.C0;
  }

  public static IAST Boole(final IExpr a) {
    return new AST1(Boole, a);
  }

  public static IAST BooleanConvert(final IExpr a0, final IExpr a1) {
    return new AST2(BooleanConvert, a0, a1);
  }

  /**
   *
   *
   * <pre>
   * BooleanQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>expr</code> is either <code>True</code> or <code>False
   * </code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; BooleanQ(True)
   * True
   * &gt;&gt; BooleanQ(False)
   * True
   * &gt;&gt; BooleanQ(a)
   * False
   * &gt;&gt; BooleanQ(1 &lt; 2)
   * True
   * &gt;&gt; BooleanQ("string")
   * False
   * &gt;&gt; BooleanQ(Together(x/y + y/x))
   * False
   * </pre>
   */
  public static IAST BooleanQ(final IExpr a) {
    return new AST1(BooleanQ, a);
  }

  public static IAST BooleanMinimize(final IExpr a) {
    return new AST1(BooleanMinimize, a);
  }

  public static IAST BooleanTable(final IExpr a0, final IExpr a1) {
    return new AST2(BooleanTable, a0, a1);
  }

  public static IAST BooleanVariables(final IExpr a0) {
    return new AST1(BooleanVariables, a0);
  }

  public static IAST BesselI(final IExpr a0, final IExpr a1) {
    return new AST2(BesselI, a0, a1);
  }

  public static IAST BesselJ(final IExpr a0, final IExpr a1) {
    return new AST2(BesselJ, a0, a1);
  }

  public static IAST BesselY(final IExpr a0, final IExpr a1) {
    return new AST2(BesselY, a0, a1);
  }

  public static IAST BesselK(final IExpr a0, final IExpr a1) {
    return new AST2(BesselK, a0, a1);
  }

  /**
   * Beta function
   * 
   * @param a
   * @param b
   * @return
   */
  public static IAST Beta(final IExpr a, final IExpr b) {
    return new AST2(Beta, a, b);
  }

  /**
   * Incomplete Beta function
   * 
   * @param z
   * @param a
   * @param b
   * @return
   */
  public static IAST Beta(final IExpr z, final IExpr a, final IExpr b) {
    return new AST3(Beta, z, a, b);
  }

  /**
   * Generalized incomplete Beta function
   * 
   * @param z1
   * @param z2
   * @param a
   * @param b
   * @return
   */
  public static IAST Beta(final IExpr z1, final IExpr z2, final IExpr a, final IExpr b) {
    return quaternary(Beta, z1, z2, a, b);
  }

  public static IAST BetaRegularized(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(BetaRegularized, a0, a1, a2);
  }

  public static IAST BetaRegularized(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(BetaRegularized, a0, a1, a2, a3);
  }

  public static IAST BinomialDistribution(final IExpr a0, final IExpr a1) {
    return new AST2(BinomialDistribution, a0, a1);
  }

  public static IAST Break() {
    return new AST0(Break);
  }

  public static IAST BrownianBridgeProcess(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(BrownianBridgeProcess, a0, a1, a2);
  }

  /**
   * <code>C(n)</code> - represents the `n`-th constant in a solution to a differential equation or
   * {@link S#ConditionalExpression}.
   * 
   * @param n
   * @return
   */
  public static IAST C(final int n) {
    return new AST1(C, ZZ(n));
  }

  public static IAST Cancel(final IExpr a) {
    return new AST1(Cancel, a);
  }

  public static IAST CancelButton() {
    return new AST0(CancelButton);
  }

  public static IAST CarlsonRC(final IExpr a0, final IExpr a1) {
    return new AST2(CarlsonRC, a0, a1);
  }

  public static IAST CarlsonRD(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(CarlsonRD, a0, a1, a2);
  }

  public static IAST CarlsonRF(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(CarlsonRF, a0, a1, a2);
  }

  public static IAST CarlsonRG(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(CarlsonRG, a0, a1, a2);
  }

  public static IAST CarmichaelLambda(final IExpr a0) {
    return new AST1(CarmichaelLambda, a0);
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
    return Object2Expr.convert(obj, true, false);
  }

  public static IAST Cases(final IExpr a0, final IExpr a1) {
    return new AST2(Cases, a0, a1);
  }

  public static IAST CatalanNumber(final IExpr a) {
    return new AST1(CatalanNumber, a);
  }

  public static IAST Catch(final IExpr a) {
    return new AST1(Catch, a);
  }

  public static IAST CauchyDistribution(final IExpr a0, final IExpr a1) {
    return new AST2(CauchyDistribution, a0, a1);
  }

  /**
   * Create a symbolic complex number <code>re + I * 0</code>
   *
   * @param re
   * @return
   */
  public static IComplex CC(final IRational re) {
    return complex(re, F.C0);
  }

  /**
   * Create a symbolic complex number <code>real + I * imag</code>
   *
   * @param real the real part of the complex number
   * @param imag the imaginary part of the complex number
   * @return
   */
  public static IComplex CC(final IRational real, final IRational imag) {
    return ComplexSym.valueOf(real, imag);
  }

  /**
   * Create a symbolic complex number
   * <code>(realNumerator/realDenominator) + I * (imagNumerator/imagDenominator)</code>
   *
   * @param realNumerator
   * @param realDenominator
   * @param imagNumerator
   * @param imagDenominator
   * @return
   */
  public static IComplex CC(final long realNumerator, final long realDenominator,
      final long imagNumerator, final long imagDenominator) {
    return ComplexSym.valueOf(realNumerator, realDenominator, imagNumerator, imagDenominator);
  }

  /**
   * Create a symbolic complex number <code>real + I * imag</code>.
   * 
   * @param real the real part of the complex number
   * @param imag the imaginary part of the complex number
   * @return
   */
  public static IComplex CC(final long real, final long imag) {
    return ComplexSym.valueOf(real, 1, imag, 1);
  }

  public static IAST CDF(final IExpr distribution) {
    return new AST1(CDF, distribution);
  }

  public static IAST CDF(final IExpr distribution, final IExpr a1) {
    return new AST2(CDF, distribution, a1);
  }

  public static IAST Ceiling(final IExpr z) {
    return new AST1(Ceiling, z);
  }

  public static IAST ChebyshevT(final IExpr a0, final IExpr a1) {
    return new AST2(ChebyshevT, a0, a1);
  }

  public static IAST ChebyshevU(final IExpr a0, final IExpr a1) {
    return new AST2(ChebyshevU, a0, a1);
  }

  /**
   * CharacteristicPolynomial(matrix, var) - computes the characteristic polynomial of a `matrix`
   * for the variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CharacteristicPolynomial.md">CharacteristicPolynomial
   *      documentation</a>
   */
  public static IAST CharacteristicPolynomial(final IExpr matrix, final IExpr variable) {
    return new AST2(CharacteristicPolynomial, matrix, variable);
  }

  public static IAST Chop(final IExpr a0) {
    return new AST1(Chop, a0);
  }

  public static IExpr chopExpr(IExpr arg, double delta) {
    if (arg.isNumber()) {
      return chopNumber((INumber) arg, delta);
    }
    return arg;
  }

  /**
   * Set real or imaginary parts of a numeric argument to zero, those absolute value is less than a
   * delta.
   *
   * @param arg a numeric number
   * @param delta the delta for which the number should be set to zero
   * @return <code>arg</code> if the argument couldn't be chopped
   */
  public static INumber chopNumber(INumber arg, double delta) {
    if (arg instanceof INum) {
      if (arg instanceof ApfloatNum) {
        if (isZero(((ApfloatNum) arg).apfloatValue(), delta)) {
          return C0;
        }
      } else {
        if (isZero(((INum) arg).getRealPart(), delta)) {
          return C0;
        }
      }
    } else if (arg instanceof IComplexNum) {
      if (arg instanceof ApcomplexNum) {
        Apcomplex apcomplexValue = ((ApcomplexNum) arg).apcomplexValue();
        Apfloat eps = new Apfloat(delta, apcomplexValue.precision());
        Apfloat epsNegate = eps.negate();
        Apfloat real = apcomplexValue.real();
        Apfloat imag = apcomplexValue.imag();
        Apfloat newReal = null;
        Apfloat newImag = null;
        if (real.compareTo(epsNegate) > 0 && real.compareTo(eps) < 0) {
          newReal = Apfloat.ZERO;
        }
        if (imag.compareTo(epsNegate) > 0 && imag.compareTo(eps) < 0) {
          newImag = Apfloat.ZERO;
        }
        if (newImag != null) {
          if (newReal != null) {
            return F.C0;
          }
          return F.num(real);
        } else if (newReal != null) {
          return F.complexNum(Apfloat.ZERO, imag);
        }
      } else {
        Complex c = arg.evalfc();
        if (isZero(c.getReal(), delta)) {
          if (isZero(c.getImaginary(), delta)) {
            return C0;
          }
          return complexNum(0.0, c.getImaginary());
        }
        if (isZero(c.getImaginary(), delta)) {
          return num(((IComplexNum) arg).getRealPart());
        }
      }
    }
    return arg;
  }

  /**
   * Set real or imaginary parts of a numeric argument to zero, those absolute value is less than
   * <code>Config.DEFAULT_CHOP_DELTA</code>
   *
   * @param arg a numeric number
   * @return <code>arg</code> if the argument couldn't be chopped
   */
  public static org.hipparchus.complex.Complex chopComplex(org.hipparchus.complex.Complex arg) {
    return chopComplex(arg, Config.DEFAULT_CHOP_DELTA);
  }

  /**
   * Set real or imaginary parts of a numeric argument to zero, those absolute value is less than a
   * delta.
   *
   * @param arg a numeric number
   * @param delta the delta for which the number should be set to zero
   * @return <code>arg</code> if the argument couldn't be chopped
   */
  public static org.hipparchus.complex.Complex chopComplex(org.hipparchus.complex.Complex arg,
      double delta) {
    org.hipparchus.complex.Complex c = arg;
    if (isZero(c.getReal(), delta)) {
      if (isZero(c.getImaginary(), delta)) {
        return org.hipparchus.complex.Complex.ZERO;
      }
      return new org.hipparchus.complex.Complex(0.0, c.getImaginary());
    }
    if (isZero(c.getImaginary(), delta)) {
      return new org.hipparchus.complex.Complex(c.getReal());
    }
    return arg;
  }

  public static IAST Circle(final IAST originList) {
    return new AST1(Circle, originList);
  }

  public static IAST CentralMoment(final IExpr a0, final IExpr a1) {
    return new AST2(CentralMoment, a0, a1);
  }

  public static IAST Clear(final IExpr... a) {
    return function(Clear, a);
  }

  public static IAST ClearAttributes(final IExpr a0, final IExpr a1) {
    return new AST2(ClearAttributes, a0, a1);
  }

  public static final IAST ClebschGordan(final IExpr arg1, final IExpr arg2, final IExpr arg3) {
    return new AST3(S.ClebschGordan, arg1, arg2, arg3);
  }

  public static IAST Clip(final IExpr a0) {
    return new AST1(Clip, a0);
  }

  public static IAST Clip(final IExpr a0, final IExpr a1) {
    return new AST2(Clip, a0, a1);
  }

  public static IAST Coefficient(final IExpr a0, final IExpr a1) {
    return new AST2(Coefficient, a0, a1);
  }

  public static IAST Coefficient(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Coefficient, a0, a1, a2);
  }

  public static IAST CoefficientList(final IExpr a0, final IExpr a1) {
    return new AST2(CoefficientList, a0, a1);
  }

  public static IAST Collect(final IExpr a0, final IExpr a1) {
    return new AST2(Collect, a0, a1);
  }

  public static IAST Colon(final IExpr a0, final IExpr a1) {
    return new AST2(Colon, a0, a1);
  }

  public static int compareTo(IExpr a, IExpr b) throws UnsupportedOperationException {
    if (a instanceof IReal && b instanceof IReal) {
      return a.compareTo(b);
    }
    IExpr tempA = eval(a);
    IExpr tempB = eval(b);
    if (tempA instanceof IReal && tempB instanceof IReal) {
      return tempA.compareTo(tempB);
    }
    throw new UnsupportedOperationException(
        "compareTo() - first or second argument could not be converted into a signed number.");
  }

  public static int compareTo(IExpr a, Integer i) throws UnsupportedOperationException {
    if (a instanceof IReal) {
      return a.compareTo(ZZ(i.longValue()));
    }
    IExpr temp = eval(a);
    if (temp instanceof IReal) {
      return temp.compareTo(ZZ(i.longValue()));
    }
    throw new UnsupportedOperationException(
        "compareTo() - first argument could not be converted into a signed number.");
  }

  public static int compareTo(IExpr a, java.math.BigInteger i)
      throws UnsupportedOperationException {
    if (a instanceof IReal) {
      return a.compareTo(ZZ(i));
    }
    IExpr temp = eval(a);
    if (temp instanceof IReal) {
      return temp.compareTo(ZZ(i));
    }
    throw new UnsupportedOperationException(
        "compareTo() - first argument could not be converted into a signed number.");
  }

  public static int compareTo(Integer i, IExpr b) throws UnsupportedOperationException {
    if (b instanceof IReal) {
      return ZZ(i.longValue()).compareTo(b);
    }
    IExpr temp = eval(b);
    if (temp instanceof IReal) {
      return ZZ(i.longValue()).compareTo(temp);
    }
    throw new UnsupportedOperationException(
        "compareTo() - second argument could not be converted into a signed number.");
  }

  public static int compareTo(java.math.BigInteger i, IExpr b)
      throws UnsupportedOperationException {
    if (b instanceof IReal) {
      return ZZ(i).compareTo(b);
    }
    IExpr temp = eval(b);
    if (temp instanceof IReal) {
      return ZZ(i).compareTo(temp);
    }
    throw new UnsupportedOperationException(
        "compareTo() - second argument could not be converted into a signed number.");
  }

  public static IAST Compile(final IExpr a0, final IExpr a1) {
    return new AST2(Compile, a0, a1);
  }

  public static IAST CompilePrint(final IExpr a0, final IExpr a1) {
    return new AST2(CompilePrint, a0, a1);
  }

  public static IAST Complement(final IExpr a0, final IExpr a1) {
    return new AST2(Complement, a0, a1);
  }

  /**
   * Create a symbolic complex number
   *
   * @param realPart the real double value part which should be converted to a complex number
   * @param imagPart the imaginary double value part which should be converted to a complex number
   * @return IComplex
   */
  public static IComplex complex(final double realPart, final double imagPart) {
    return complex(realPart, imagPart, Config.DOUBLE_EPSILON);
  }

  /**
   * Create a symbolic complex number
   *
   * @param realPart the real double value which should be converted to the real part of symbolic a
   *        complex number
   * @param imagPart the imaginary double value which should be converted to the imaginary part of
   *        symbolic a complex number
   * @param epsilon
   * @return
   */
  public static IComplex complex(final double realPart, final double imagPart,
      final double epsilon) {
    return ComplexSym.valueOf(AbstractFractionSym.valueOfEpsilon(realPart, epsilon),
        AbstractFractionSym.valueOfEpsilon(imagPart, epsilon));
  }


  /**
   * Create a symbolic complex number.
   *
   * @param realPart the real double value which should be converted to the real part of symbolic a
   *        complex number
   * @param imagPart the imaginary double value which should be converted to the imaginary part of
   *        symbolic a complex number
   * @return
   */
  public static IComplex complexConvergent(final double realPart, final double imagPart) {
    return ComplexSym.valueOf(AbstractFractionSym.valueOfConvergent(realPart),
        AbstractFractionSym.valueOfConvergent(imagPart));
  }

  /**
   * <p>
   * Create a symbolic complex number
   * <p>
   * Use {@link #CC(IRational, IRational)} instead
   * 
   * @param re
   */
  @Deprecated
  public static IComplex complex(final IRational re) {
    return complex(re, QQ(0L, 1L));
  }

  /**
   * <p>
   * Create a symbolic complex number
   * <p>
   * Use {@link #CC(IRational, IRational)} instead
   *
   * @param re
   * @param im
   * @return
   */
  @Deprecated
  public static IComplex complex(final IRational re, final IRational im) {
    return ComplexSym.valueOf(re, im);
  }

  /**
   * Create a symbolic complex number
   *
   * @param real_numerator
   * @param real_denominator
   * @param imag_numerator
   * @param imag_denominator
   * @return
   */
  public static IComplex complex(final long real_numerator, final long real_denominator,
      final long imag_numerator, final long imag_denominator) {
    return ComplexSym.valueOf(real_numerator, real_denominator, imag_numerator, imag_denominator);
  }

  /**
   * Create a <code>Complex(a, b)</code> AST symbolic expression.
   *
   * @param a0
   * @param a1
   * @return
   */
  public static IAST Complex(final IExpr a0, final IExpr a1) {
    return new AST2(Complex, a0, a1);
  }

  /**
   * Return a {@link ApcomplexNum} which wraps a {@link Apcomplex} arbitrary precision
   * floating-point complex number.
   * 
   * @param c
   * @return
   */
  public static IComplexNum complexNum(final Apcomplex c) {
    return ApcomplexNum.valueOf(c);
  }

  /**
   * Return a {@link ApcomplexNum} which wraps two {@link Apfloat} arbitrary precision
   * floating-point numbers for the real and imaginary part with imaginary part set to
   * <code>0</code>.
   * 
   * @param realPart
   * @return a {@link ApcomplexNum} which wraps two {@link Apfloat} arbitrary precision
   *         floating-point numbers for the real and imaginary part with imaginary part set to
   *         <code>0</code>.
   */
  public static IComplexNum complexNum(final Apfloat realPart) {
    return ApcomplexNum.valueOf(realPart, Apcomplex.ZERO);
  }


  /**
   * Return a {@link ApcomplexNum} which wraps two {@link Apfloat} arbitrary precision
   * floating-point numbers for the real and imaginary part.
   * 
   * @param realPart
   * @param imaginaryPart
   * @return a {@link ApcomplexNum} which wraps two {@link Apfloat} arbitrary precision
   *         floating-point numbers for the real and imaginary part.
   */
  public static IComplexNum complexNum(final Apfloat realPart, final Apfloat imaginaryPart) {
    return ApcomplexNum.valueOf(realPart, imaginaryPart);
  }

  /**
   * Return a {@link ApcomplexNum} which wraps two {@link Apfloat} arbitrary precision
   * floating-point numbers for the real and imaginary part.
   * 
   * @param realPart
   * @param imaginaryPart
   * @param precision
   * @return a {@link ApcomplexNum} which wraps two {@link Apfloat} arbitrary precision
   *         floating-point numbers for the real and imaginary part.
   */
  public static IComplexNum complexNum(String realPart, String imaginaryPart, long precision) {
    return ApcomplexNum.valueOf(new Apfloat(realPart, precision),
        new Apfloat(imaginaryPart, precision));
  }

  /**
   * Return a {@link ComplexNum} which wraps a {@link Complex} number with Java double values for
   * the real and imaginary part.
   * 
   * @param c the complex number
   * @return
   */
  public static IComplexNum complexNum(final Complex c) {
    return ComplexNum.valueOf(c);
  }

  /**
   * Create a complex numeric number with imaginary part = 0.0
   *
   * @param r the real part of the number
   * @return a complex numeric number with imaginary part = 0.0
   */
  public static IComplexNum complexNum(final double r) {
    return complexNum(r, 0.0);
  }

  /**
   * Return a {@link Num} which wraps a double number, if the imaginary part is <code>0.0</code> or
   * a {@ComplexNum} which wraps a {@link Complex} number with Java double values for the real and
   * imaginary part.
   * 
   * @param c the complex number
   * @return
   */
  public static IInexactNumber inexactNum(final Complex c) {
    if (c.getImaginary() == 0.0 || c.getImaginary() == -0.0) {
      return Num.valueOf(c.getReal());
    }
    return ComplexNum.valueOf(c);
  }

  /**
   * Create a complex numeric value
   *
   * @param r real part
   * @param i imaginary part
   * @return
   */
  public static IComplexNum complexNum(final double r, final double i) {
    return ComplexNum.valueOf(r, i);
  }

  public static IComplexNum complexNum(final IComplex value) {
    final IRational realFraction = value.getRealPart();
    final IRational imagFraction = value.getImaginaryPart();
    final EvalEngine engine = EvalEngine.get();
    if (engine.isArbitraryMode()) {
      return ApcomplexNum.valueOf(realFraction.toBigNumerator(), realFraction.toBigDenominator(),
          imagFraction.toBigNumerator(), imagFraction.toBigDenominator());
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
    if (engine.isArbitraryMode()) {
      return ApcomplexNum.valueOf(value.toBigNumerator(), value.toBigDenominator(), BigInteger.ZERO,
          BigInteger.ONE);
    }
    return complexNum(value.doubleValue(), 0.0d);
  }

  public static IComplexNum complexNum(final IInteger value) {
    final EvalEngine engine = EvalEngine.get();
    if (engine.isArbitraryMode()) {
      return ApcomplexNum.valueOf(value.toBigNumerator(), BigInteger.ONE, BigInteger.ZERO,
          BigInteger.ONE);
    }
    return complexNum(value.doubleValue(), 0.0d);
  }

  /**
   * Perform &quot;common subexpression elimination&quot; (CSE) on an expression.
   * 
   * @param expr
   * @return the pair of <code>{shared-expressions, recursive-replacement-rules}</code>
   */
  public static IAST cse(IExpr expr) {
    return cse(expr, () -> "v");
  }

  /**
   * Perform &quot;common subexpression elimination&quot; (CSE) on an expression.
   * 
   * @param expr
   * @param variablePrefix the prefix string, which should be used for the variable names
   * @return the pair of <code>{shared-expressions, recursive-replacement-rules}</code>
   */
  public static IAST cse(IExpr expr, Supplier<String> variablePrefix) {
    if (expr.isAST()) {
      IASTMutable mutable = ((IAST) expr).copy();
      return org.matheclipse.core.reflection.system.OptimizeExpression.cse(mutable, variablePrefix);
    }
    return F.List(expr, F.CEmptyList);
  }

  /**
   * Perform &quot;common subexpression elimination&quot; (CSE) on an expression and create Java
   * source code in the given {@link StringBuilder}.
   * 
   * @param expr
   * @param buf
   */
  public static void cseAsJava(IExpr expr, StringBuilder buf) {
    cseAsJava(expr, () -> "v", buf);
  }

  /**
   * Perform &quot;common subexpression elimination&quot; (CSE) on an expression and create Java
   * source code in the given {@link StringBuilder}.
   * 
   * @param expr
   * @param variablePrefix the prefix string, which should be used for the variable names
   * @param buf
   */
  public static void cseAsJava(IExpr expr, Supplier<String> variablePrefix, StringBuilder buf) {
    IAST csePair = cse(expr, variablePrefix);
    org.matheclipse.core.reflection.system.OptimizeExpression.csePairAsJava(csePair, buf);
  }

  /**
   * Evaluates its arguments in turn, returning the last result.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompoundExpression.md">CompoundExpression</a>
   *
   * @param expr a list of expressions
   * @return
   */
  public static IAST CompoundExpression(final IExpr... expr) {
    return function(CompoundExpression, expr);
  }

  /**
   * Create a new abstract syntax tree (AST).
   *
   * @param head the header symbol of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @param a
   * @return
   */
  public static IASTMutable function(IExpr head, final IExpr... a) {
    final int size = a.length;
    switch (size) {
      case 0:
        return new AST0(head);
      case 1:
        return new AST1(head, a[0]);
      case 2:
        return new AST2(head, a[0], a[1]);
      case 3:
        return new AST3(head, a[0], a[1], a[2]);
      default:
        return new AST(head, a);
    }
  }

  /**
   * Places an additional constraint on <code>pattern</code> that only allows it to match if <code>
   * test</code> evaluates to {@link S#True}.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Condition.md">Condition</a>
   *
   * @param pattern
   * @param test
   * @return
   */
  public static IAST Condition(final IExpr pattern, final IExpr test) {
    return new B2.Condition(pattern, test);
  }

  public static IAST ConditionalExpression(final IExpr expr, final IExpr condition) {
    return new AST2(ConditionalExpression, expr, condition);
  }

  public static IAST Cone(final IExpr matrix) {
    return new AST1(Cone, matrix);
  }

  public static IAST Cone(final IExpr matrix, final IExpr radius) {
    return new AST2(Cone, matrix, radius);
  }

  public static IAST Conjugate(final IExpr a0) {
    return new AST1(Conjugate, a0);
  }

  public static IAST ConstantArray(final IExpr a0, final IExpr a1) {
    return new AST2(ConstantArray, a0, a1);
  }

  public static IAST ConjugateTranspose(final IExpr a0) {
    return new AST1(ConjugateTranspose, a0);
  }

  public static IAST Continue() {
    return new AST0(Continue);
  }

  public static IAST ContinuedFraction(final IExpr a0) {
    return new AST1(ContinuedFraction, a0);
  }

  public static IAST CoordinateBoundingBox(final IExpr a0) {
    return new AST1(CoordinateBoundingBox, a0);
  }

  public static IAST CoordinateBounds(final IExpr a0) {
    return new AST1(CoordinateBounds, a0);
  }

  public static IAST CoplanarPoints(final IExpr a0) {
    return new AST1(CoplanarPoints, a0);
  }

  public static IAST CoprimeQ(final IExpr a0, final IExpr a1) {
    return new AST2(CoprimeQ, a0, a1);
  }

  /**
   * Returns the cosine of <code>z</code> (measured in
   * <a href="https://en.wikipedia.org/wiki/Radian">Radians</a>).
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cos.md">Cos</a>
   *
   * @param z
   * @return
   */
  public static IAST Cos(final IExpr z) {
    return new B1.Cos(z);
  }

  public static IAST Cosh(final IExpr z) {
    return new AST1(Cosh, z);
  }

  public static IAST CoshIntegral(final IExpr z) {
    return new AST1(CoshIntegral, z);
  }

  public static IAST CosIntegral(final IExpr z) {
    return new AST1(CosIntegral, z);
  }

  /**
   * Returns the cotangent of <code>z</code> (measured in
   * <a href="https://en.wikipedia.org/wiki/Radian">Radians</a>).
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cot.md">Cot</a>
   *
   * @param z
   * @return
   */
  public static IAST Cot(final IExpr z) {
    return new AST1(Cot, z);
  }

  public static IAST Coth(final IExpr z) {
    return new AST1(Coth, z);
  }

  public static IAST Count(final IExpr a0, final IExpr a1) {
    return new AST2(Count, a0, a1);
  }

  public static IAST Count(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Count, a0, a1, a2);
  }

  public static IAST Covariance(final IExpr a0, final IExpr a1) {
    return new AST2(Covariance, a0, a1);
  }

  public static IAST Cross(final IExpr a0, final IExpr a1) {
    return new AST2(Cross, a0, a1);
  }

  public static IAST Csc(final IExpr z) {
    return new B1.Csc(z);
  }

  public static IAST Csch(final IExpr z) {
    return new AST1(Csch, z);
  }

  public static IAST Cube(final IExpr center, final IExpr length) {
    return new AST2(Cube, center, length);
  }

  public static IAST CubeRoot(final IExpr arg) {
    return new AST1(CubeRoot, arg);
  }


  /**
   * @param pMin lower corner
   * @param pMax upper corner
   * @return
   */
  public static IAST Cuboid(final IExpr pMin, final IExpr pMax) {
    return new AST2(Cuboid, pMin, pMax);
  }

  public static IAST Cycles(final IExpr a0) {
    return new AST1(Cycles, a0);
  }

  public static IAST Cylinder(final IExpr matrix) {
    return new AST1(Cylinder, matrix);
  }

  public static IAST Cylinder(final IExpr matrix, final IExpr radius) {
    return new AST2(Cylinder, matrix, radius);
  }

  public static IAST D() {
    return ast(D);
  }

  public static IAST D(final IExpr f, final IExpr x) {
    return new AST2(D, f, x);
  }

  public static IAST D(final IExpr f, final IAST l1, IAST l2) {
    return new AST3(D, f, l1, l2);
  }

  public static IAST Dashing(final IExpr a) {
    return new AST1(Dashing, a);
  }

  public static IAST Decrement(final IExpr a) {
    return new AST1(Decrement, a);
  }

  public static IAST Defer(final IExpr a0) {
    return new AST1(Defer, a0);
  }

  public static IAST Delete(final IExpr a0, final IExpr a1) {
    return new AST2(Delete, a0, a1);
  }

  public static IAST DeleteCases(final IExpr... a) {
    return function(DeleteCases, a);
  }

  public static IAST Denominator(final IExpr expr) {
    return new AST1(Denominator, expr);
  }

  public static IAST Depth(final IExpr a0) {

    return new AST1(Depth, a0);
  }

  public static IAST Derivative(final IExpr... a) {
    return function(Derivative, a);
  }

  public static IAST DesignMatrix(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(DesignMatrix, a0, a1, a2);
  }

  public static IAST Det(final IExpr a0) {
    return new AST1(Det, a0);
  }

  public static IAST DialogReturn() {
    return new AST0(DialogReturn);
  }

  public static IAST DialogReturn(final IExpr a0) {
    return new AST1(DialogReturn, a0);
  }

  public static IAST DiagonalMatrix(final IExpr a0) {
    return new AST1(DiagonalMatrix, a0);
  }

  public static IAST DiagonalMatrix(final IExpr a0, final IExpr a1) {
    return new AST2(DiagonalMatrix, a0, a1);
  }

  public static IAST DiagonalMatrixQ(final IExpr a0) {
    return new AST1(DiagonalMatrixQ, a0);
  }

  public static IAST DiagonalMatrixQ(final IExpr a0, final IExpr a1) {
    return new AST2(DiagonalMatrixQ, a0, a1);
  }

  /**
   *
   *
   * <pre>
   * DigitQ(str)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>str</code> is a string which contains only digits.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; DigitQ("1234")
   * True
   * </pre>
   */
  public static IAST DigitQ(final IExpr a0) {
    return new AST1(DigitQ, a0);
  }

  /**
   *
   *
   * <pre>
   * Dimensions(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a list of the dimensions of the expression <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * A vector of length 3:
   *
   * <pre>
   * &gt;&gt; Dimensions({a, b, c})
   *  = {3}
   * </pre>
   *
   * <p>
   * A 3x2 matrix:
   *
   * <pre>
   * &gt;&gt; Dimensions({{a, b}, {c, d}, {e, f}})
   *  = {3, 2}
   * </pre>
   *
   * <p>
   * Ragged arrays are not taken into account:
   *
   * <pre>
   * &gt;&gt; Dimensions({{a, b}, {b, c}, {c, d, e}})
   * {3}
   * </pre>
   *
   * <p>
   * The expression can have any head:
   *
   * <pre>
   * &gt;&gt; Dimensions[f[f[a, b, c]]]
   * {1, 3}
   * &gt;&gt; Dimensions({})
   * {0}
   * &gt;&gt; Dimensions({{}})
   * {1, 0}
   * </pre>
   */
  public static IAST Dimensions(final IExpr a0) {
    return new AST1(Dimensions, a0);
  }

  public static IAST DiracDelta(final IExpr a0) {
    return new AST1(DiracDelta, a0);
  }

  /**
   * <code>DirectedEdge</code> is a directed edge from vertex <code>a</code to vertex <code>b </code
   * in a `graph` object.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DirectedEdge.md">DirectedEdge</a>
   *
   * @param a
   * @param b
   * @return
   */
  public static IAST DirectedEdge(final IExpr a, final IExpr b) {
    return new B2.DirectedEdge(a, b);
  }

  public static IAST DirectedInfinity(final IExpr a0) {
    return new AST1(DirectedInfinity, a0);
  }

  public static IAST Disk(final IAST originList) {
    return new AST1(Disk, originList);
  }

  public static IAST DiscreteDelta(final IExpr a) {
    return new AST1(DiscreteDelta, a);
  }

  public static IAST DiscreteUniformDistribution(final IExpr a) {
    return new AST1(DiscreteUniformDistribution, a);
  }

  public static IAST Discriminant(final IExpr a0, final IExpr a1) {
    return new AST2(Discriminant, a0, a1);
  }

  public static IAST Distribute(final IExpr a) {
    return new AST1(Distribute, a);
  }

  public static IAST Distribute(final IExpr a0, final IExpr a1) {
    return new AST2(Distribute, a0, a1);
  }

  public static IAST Distribute(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Distribute, a0, a1, a2);
  }

  /**
   * Create a <code>Distributed(x, &lt;distribution&gt;)</code> AST.
   *
   * @param x
   * @param distribution
   * @return
   */
  public static IAST Distributed(final IExpr x, final IAST distribution) {
    return new AST2(Distributed, x, distribution);
  }

  public static IExpr div(IExpr a, Integer i) {
    return Times(a, Power(ZZ(i.longValue()), CN1));
  }

  public static IExpr div(IExpr a, java.math.BigInteger i) {
    return Times(a, Power(ZZ(i), CN1));
  }

  public static IExpr div(Integer i, IExpr b) {
    return Times(ZZ(i.longValue()), Power(b, CN1));
  }

  public static IExpr div(java.math.BigInteger i, IExpr b) {
    return Times(ZZ(i), Power(b, CN1));
  }


  public static IASTMutable DirectionalLight(final IExpr color, final IExpr point) {
    return new AST2(DirectionalLight, color, point);
  }

  /**
   * The division <code>numerator / denominator</code> will be represented by
   * <code>numerator * denominator^(-1)</code> (full form:
   * <code>Times(numerator, Power(denominator,-1))</code>). If <code>numerator.isOne()==true</code>
   * return <code>denominator^(-1)</code> (full form: <code>Power(denominator,-1)</code>). If
   * <code>denominator.isOne()==true</code> return <code>numerator</code>.
   *
   * @param numerator numerator
   * @param denominator denominator
   * @return
   */
  public static IExpr Divide(final IExpr numerator, final IExpr denominator) {
    if (denominator.isZero()) {
      // Indeterminate expression `1` encountered.
      Errors.printMessage(S.Power, "indet", F.list(F.Times(numerator, F.Power(denominator, F.CN1))),
          EvalEngine.get());
      return S.Indeterminate;
    }
    if (denominator.isOne()) {
      return numerator;
    }
    IExpr arg2 = denominator.isNumber() ? denominator.inverse() : new B2.Power(denominator, CN1);
    if (numerator.isOne()) {
      return arg2;
    }
    if (numerator.isNumber() && arg2.isNumber()) {
      return numerator.times(arg2);
    }
    if (arg2.isLEOrdered(numerator)) {
      return new B2.Times(arg2, numerator);
    }
    return new B2.Times(numerator, arg2);
  }

  /**
   * The division <code>numerator / denominator</code> will be represented by
   * <code>numerator * denominator^(-1)</code> (full form:
   * <code>Times(numerator, Power(denominator,-1))</code>). If <code>numerator.isOne()==true</code>
   * return <code>denominator^(-1)</code> (full form: <code>Power(denominator,-1)</code>). If
   * <code>denominator.isOne()==true</code> return <code>numerator</code>.
   * 
   * @param numerator
   * @param denominator
   * @return
   */
  public static IExpr Divide(final int numerator, final IExpr denominator) {
    return Divide(F.ZZ(numerator), denominator);
  }

  /**
   * The division <code>numerator / denominator</code> will be represented by
   * <code>numerator * denominator^(-1)</code>. If <code>numerator.isOne()==true</code> return
   * <code>denominator^(-1)</code>. If <code>denominator.isOne()==true</code> return
   * <code>numerator</code>.
   * 
   * @param numerator
   * @param denominator
   * @return
   */
  public static IExpr Divide(final IExpr numerator, final int denominator) {
    return Divide(numerator, F.ZZ(denominator));
  }

  public static IASTMutable DivideSides(final IExpr equationOrInequality) {
    return new AST1(DivideSides, equationOrInequality);
  }

  public static IASTMutable DivideSides(final IExpr equationOrInequality, final IExpr a1) {
    return new AST2(DivideSides, equationOrInequality, a1);
  }

  public static IAST Divisible(final IExpr a0, final IExpr a1) {
    return new AST2(Divisible, a0, a1);
  }

  public static IAST DivisorSigma(final IExpr a0, final IExpr a1) {
    return new AST2(DivisorSigma, a0, a1);
  }

  public static IAST Divisors(final IExpr a0) {
    return new AST1(Divisors, a0);
  }

  public static IAST Do(final IExpr a0, final IExpr a1) {
    return new AST2(Do, a0, a1);
  }

  public static IAST Dot(final IExpr... a) {
    return function(Dot, a);
  }

  public static IAST Dot(final IExpr a, final IExpr b) {
    return new AST2(Dot, a, b);
  }

  public static IAST Dot(final IExpr a, final IExpr b, final IExpr c) {
    return new AST3(Dot, a, b, c);
  }

  public static IAST Drop(final IExpr list, final IExpr a1) {
    return new AST2(Drop, list, a1);
  }

  public static IAST Drop(final IExpr list, final IExpr a1, final IExpr a2) {
    return new AST3(Drop, list, a1, a2);
  }

  public static IAST DSolve(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(DSolve, a0, a1, a2);
  }

  public static IAST Eigensystem(final IExpr m) {
    return new AST1(Eigensystem, m);
  }

  public static IAST Eigenvalues(final IExpr m) {
    return new AST1(Eigenvalues, m);
  }

  public static IAST Eigenvectors(final IExpr m) {
    return new AST1(Eigenvectors, m);
  }

  /**
   * Set a domain as an assumption for a variable.
   * 
   * @param variable the variable for which an assumption should be defined
   * @param domain very often used domains are {@link S#Booleans}, {@link S#Integers},
   *        {@link S#Reals}
   * @return
   */
  public static IAST Element(final IExpr variable, final IExpr domain) {
    return new AST2(Element, variable, domain);
  }

  /**
   * Property of a <code>name</code> chemical element.
   *
   * @param name
   * @param property
   * @return
   */
  public static IAST ElementData(final IExpr name, final IExpr property) {
    return new AST2(ElementData, name, property);
  }

  public static IAST Eliminate(final IExpr a0, final IExpr a1) {
    return new AST2(Eliminate, a0, a1);
  }

  public static IAST EllipticE(final IExpr m) {
    return new AST1(EllipticE, m);
  }

  public static IAST EllipticE(final IExpr phi, final IExpr m) {
    return new AST2(EllipticE, phi, m);
  }

  public static IAST EllipticF(final IExpr phi, final IExpr m) {
    return new AST2(EllipticF, phi, m);
  }

  public static IAST EllipticK(final IExpr m) {
    return new AST1(EllipticK, m);
  }

  public static IAST EllipticPi(final IExpr n, final IExpr m) {
    return new AST2(EllipticPi, n, m);
  }

  public static IAST EllipticPi(final IExpr n, final IExpr phi, final IExpr m) {
    return new AST3(EllipticPi, n, phi, m);
  }

  public static IAST Equal(final IExpr... a) {
    return function(Equal, a);
  }

  /**
   * Yields {@link S#True} if <code>lhs</code> and <code>rhs</code> are known to be equal, or
   * {@link S#False} if <code>lhs</code> and <code>rhs</code> are known to be unequal.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Equal.md">Equal</a>
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public static IAST Equal(final IExpr lhs, final IExpr rhs) {
    return new B2.Equal(lhs, rhs);
  }

  /**
   * Yields {@link S#True} if <code>lhs</code> and <code>rhs</code> are known to be equal, or
   * {@link S#False} if <code>lhs</code> and <code>rhs</code> are known to be unequal.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Equal.md">Equal</a>
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public static IAST Equal(final IExpr lhs, final int rhs) {
    return new B2.Equal(lhs, ZZ(rhs));
  }

  public static IAST Equivalent(final IExpr lhs, final IExpr rhs) {
    return new AST2(Equivalent, lhs, rhs);
  }

  public static IAST Erf(final IExpr a) {
    return new AST1(Erf, a);
  }

  public static IAST Erf(final IExpr a0, final IExpr a1) {
    return new AST2(Erf, a0, a1);
  }

  public static IAST Erfc(final IExpr a) {
    return new AST1(Erfc, a);
  }

  public static IAST Erfi(final IExpr a) {
    return new AST1(Erfi, a);
  }

  public static IAST ErlangDistribution(final IExpr a0, final IExpr a1) {
    return new AST2(ErlangDistribution, a0, a1);
  }

  /**
   * Evaluate an expression. If no evaluation was possible this method returns the given argument.
   *
   * @param a the expression which should be evaluated
   * @return the evaluated expression
   * @see EvalEngine#evaluate(IExpr)
   */
  public static IExpr eval(IExpr a) {
    return EvalEngine.get().evaluate(a);
  }

  /**
   * Parse and evaluate a string expression.
   *
   * @param str the string expression which should be parsed and evaluated
   * @return the evaluated expression
   * @see EvalEngine#evaluate(IExpr)
   */
  public static IExpr eval(String str) {
    return EvalEngine.get().evaluate(str);
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
   * Evaluate <code>Expand()</code> for the given expression. Returns the evaluated expression or
   * the given argument.
   *
   * @param expr the expression which should be evaluated
   * @return the evaluated expression
   * @see EvalEngine#evaluate(IExpr)
   */
  public static IExpr evalExpand(IExpr expr) {
    if (expr.isAST()) {
      EvalEngine engine = EvalEngine.get();
      IAST ast = (IAST) expr;
      if (ast.isPlus()) {
        if (ast.exists(IExpr::isPlusTimesPower)) {
          return Expand(expr)//
              .eval(engine);
        }
      } else if (ast.isTimes() || ast.isPower()) {
        return Expand(expr)//
            .eval(engine);
      }
    }
    return expr;
  }

  public static IExpr evalCollect(IExpr expr, IExpr x) {
    if (expr.isAST()) {
      EvalEngine engine = EvalEngine.get();
      return Collect(expr, x)//
          .eval(engine);
    }
    return expr;
  }

  public static IExpr evalSimplify(IExpr expr) {
    if (expr.isAST()) {
      EvalEngine engine = EvalEngine.get();
      return Simplify(expr)//
          .eval(engine);
    }
    return expr;
  }

  /**
   * Apply <code>ExpandAll()</code> to the given expression if it's an <code>IAST</code>. If
   * expanding wasn't possible this method returns the given argument.
   *
   * @param a the expression which should be evaluated
   * @return the evaluated expression
   * @see EvalEngine#evaluate(IExpr)
   */
  public static IExpr evalExpandAll(IExpr a) {
    return evalExpandAll(a, EvalEngine.get());
  }

  /**
   * Apply <code>ExpandAll()</code> to the given expression if it's an <code>IAST</code>. If
   * expanding wasn't possible this method returns the given argument.
   *
   * @param a the expression which should be evaluated
   * @return the evaluated expression
   * @see EvalEngine#evaluate(IExpr)
   */
  public static IExpr evalExpandAll(IExpr a, EvalEngine engine) {
    return ExpandAll(a)//
        .eval(engine);
  }

  /**
   * Evaluate the given expression in numeric mode
   *
   * @param a0
   * @return
   * @deprecated use {@link EvalEngine#evalN(IExpr)} instead
   */
  @Deprecated
  public static IExpr evaln(final IExpr a0) {
    return eval(N, a0);
  }

  /**
   * Evaluate an expression in &quot;quiet mode&quot;. If no evaluation was possible this method
   * returns the given argument. In &quot;quiet mode&quot; all warnings would be suppressed.
   *
   * @param a the expression which should be evaluated
   * @return the evaluated expression
   * @see EvalEngine#evalQuiet(IExpr)
   * @deprecated use EvalEngine#evalQuiet();
   */
  @Deprecated
  public static IExpr evalQuiet(IExpr a) {
    return EvalEngine.get().evalQuiet(a);
  }

  /**
   * Evaluate an expression in &quot;quiet mode&quot;. If evaluation is not possible return <code>
   * null</code>. In &quot;quiet mode&quot; all warnings would be suppressed.
   *
   * @param a the expression which should be evaluated
   * @return the evaluated object or <code>F.NIL</code> if no evaluation was possible
   * @see EvalEngine#evalQuietNIL(IExpr)
   * @deprecated use EvalEngine#evalQuietNull()
   */
  @Deprecated
  public static IExpr evalQuietNull(IExpr a) {
    return EvalEngine.get().evalQuietNIL(a);
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

  /**
   *
   *
   * <pre>
   * EvenQ(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>x</code> is even, and <code>False</code> otherwise.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; EvenQ(4)
   * True
   * &gt;&gt; EvenQ(-3)
   * False
   * &gt;&gt; EvenQ(n)
   * False
   * </pre>
   */
  public static IAST EvenQ(final IExpr x) {
    return new AST1(EvenQ, x);
  }

  /**
   *
   *
   * <pre>
   * ExactNumberQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>expr</code> is an exact number, and <code>False</code>
   * otherwise.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ExactNumberQ(10)
   * True
   *
   * &gt;&gt; ExactNumberQ(4.0)
   * False
   *
   * &gt;&gt; ExactNumberQ(n)
   * False
   *
   * &gt;&gt; ExactNumberQ(1+I)
   * True
   *
   * &gt;&gt; ExactNumberQ(1 + 1. * I)
   * False
   * </pre>
   */
  public static IAST ExactNumberQ(final IExpr expr) {
    return new AST1(ExactNumberQ, expr);
  }

  public static IAST Exists(final IExpr a0, final IExpr a1) {
    return new AST2(Exists, a0, a1);
  }

  public static IAST Exists(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Exists, a0, a1, a2);
  }

  /**
   * depending on the derived class of the given {@link Number}, the value is encoded as
   * {@link IInteger}, {@link INum}
   *
   * @param number non-null
   * @return scalar with best possible accuracy to encode given number
   * @throws Exception if number is null, or instance of an unsupported type
   */
  public static IReal expr(Number number) {
    if (number instanceof Integer || number instanceof Long || number instanceof Short
        || number instanceof Byte)
      return ZZ(number.longValue());
    if (number instanceof Double || number instanceof Float)
      return num(number.doubleValue());
    if (number instanceof BigInteger)
      return ZZ((BigInteger) number);
    throw new IllegalArgumentException(number.getClass().getName());
  }

  public static IAST EuclideanDistance(final IExpr a0, final IExpr a1) {
    return new AST2(EuclideanDistance, a0, a1);
  }

  public static IAST EulerE(final IExpr n) {
    return new AST1(EulerE, n);
  }

  public static IAST EulerE(final IExpr n, final IExpr x) {
    return new AST2(EulerE, n, x);
  }

  public static IAST EulerPhi(final IExpr a0) {
    return new AST1(EulerPhi, a0);
  }

  /**
   * The exponential function <code>E^z</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Exp.md">Exp</a>
   *
   * @param z
   * @return
   */
  public static IAST Exp(final IExpr z) {
    return new B2.Power(E, z);
  }

  public static IAST Exp(final int z) {
    return new B2.Power(E, F.ZZ(z));
  }

  public static IAST ExpToTrig(final IExpr a0) {
    return new AST1(ExpToTrig, a0);
  }

  /**
   * Apply <code>Expand()</code> to the given expression if it's an <code>IAST</code>. If expanding
   * wasn't possible this method returns the given argument.
   *
   * @param a the expression which should be evaluated
   * @param expandNegativePowers
   * @param distributePlus
   * @param evalParts evaluate the determined numerator and denominator parts
   * @return the evaluated expression
   * @see EvalEngine#evaluate(IExpr)
   */
  public static IExpr expand(IExpr a, boolean expandNegativePowers, boolean distributePlus,
      boolean evalParts) {
    if (a.isAST()) {
      EvalEngine engine = EvalEngine.get();
      IAST ast = engine.evalFlatOrderlessAttrsRecursive((IAST) a).orElse((IAST) a);
      return Algebra.expand(ast, null, expandNegativePowers, distributePlus, evalParts).orElse(a);
    }
    return a;
  }

  /**
   * Expands out positive rational powers and products of sums in <code>expr</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expand.md">Expand</a>
   *
   * @param expr
   * @return
   */
  public static IAST Expand(final IExpr expr) {
    return new AST1(Expand, expr);
  }

  /**
   * Expands out positive rational powers and products of sums in <code>expr</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expand.md">Expand</a>
   *
   * @param expr
   * @param pattern
   * @return
   */
  public static IAST Expand(final IExpr expr, final IExpr pattern) {

    return new AST2(Expand, expr, pattern);
  }

  /**
   * Apply <code>ExpandAll()</code> to the given expression if it's an <code>IAST</code>. If
   * expanding wasn't possible this method returns the given argument.
   *
   * @param a the expression which should be evaluated
   * @param expandNegativePowers
   * @param distributePlus
   * @return the evaluated expression
   * @see EvalEngine#evaluate(IExpr)
   */
  public static IExpr expandAll(IExpr a, boolean expandNegativePowers, boolean distributePlus) {
    if (a.isAST()) {
      EvalEngine engine = EvalEngine.get();
      IAST ast = engine.evalFlatOrderlessAttrsRecursive((IAST) a).orElse((IAST) a);
      return Algebra.expandAll(ast, null, expandNegativePowers, distributePlus, false, engine)
          .orElse(ast);
    }
    return a;
  }

  //
  // public static IAST NumberPartitions(final IExpr a0) {
  //
  // return unaryAST2(NumberPartitions, a0);
  // }

  public static IAST ExpandAll(final IExpr a0) {
    return new AST1(ExpandAll, a0);
  }

  public static IAST ExpandDenominator(final IExpr expr) {
    return new AST1(ExpandDenominator, expr);
  }

  public static IAST ExpandNumerator(final IExpr expr) {
    return new AST1(ExpandNumerator, expr);
  }

  public static IAST ExpIntegralE(final IExpr a0, final IExpr a1) {
    return new AST2(ExpIntegralE, a0, a1);
  }

  public static IAST ExpIntegralEi(final IExpr a) {
    return new AST1(ExpIntegralEi, a);
  }

  public static IAST Exponent(final IExpr a0, final IExpr a1) {
    return new AST2(Exponent, a0, a1);
  }

  public static IAST Exponent(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Exponent, a0, a1, a2);
  }

  public static IAST ExponentialDistribution(final IExpr a0) {
    return new AST1(ExponentialDistribution, a0);
  }

  public static IAST ExportString(final IExpr a0, final IExpr a1) {
    return new AST2(ExportString, a0, a1);
  }

  public static IAST Extract(final IExpr a0, final IExpr a1) {
    return new AST2(Extract, a0, a1);
  }

  public static IAST f1(final IExpr... a) {
    return ast(a, f1);
  }

  public static IAST f2(final IExpr... a) {
    return ast(a, f2);
  }

  public static IAST f3(final IExpr... a) {
    return ast(a, f3);
  }

  public static IAST f4(final IExpr... a) {
    return ast(a, f4);
  }

  public static IAST Factor(final IExpr poly) {
    return new AST1(Factor, poly);
  }

  public static IAST FactorTerms(final IExpr poly) {
    return new AST1(FactorTerms, poly);
  }

  public static IAST Factorial(final IExpr a0) {
    return new AST1(Factorial, a0);
  }

  public static IAST Factorial(final int a0) {
    return new AST1(Factorial, ZZ(a0));
  }

  public static IAST Factorial2(final IExpr a0) {
    return new AST1(Factorial2, a0);
  }

  public static IAST FactorialPower(final IExpr x, final IExpr n) {
    return new AST2(FactorialPower, x, n);
  }

  public static IAST FactorialPower(final IExpr x, final IExpr n, final IExpr h) {
    return new AST3(FactorialPower, x, n, h);
  }

  public static IAST FactorInteger(final IExpr a0) {
    return new AST1(FactorInteger, a0);
  }

  public static IAST FactorSquareFree(final IExpr a) {
    return new AST1(FactorSquareFree, a);
  }

  public static IAST FactorSquareFreeList(final IExpr a) {
    return new AST1(FactorSquareFreeList, a);
  }

  public static IAST Fibonacci(final IExpr a0) {
    return new AST1(Fibonacci, a0);
  }

  public static IAST Fibonacci(final IExpr a0, final IExpr a1) {
    return new AST2(Fibonacci, a0, a1);
  }

  public static IAST FileNameDrop(final IExpr data, final IExpr x) {
    return new AST2(FileNameDrop, data, x);
  }

  public static IAST FileNameJoin(final IExpr a0) {
    return new AST1(FileNameJoin, a0);
  }

  public static IAST FindFit(final IExpr data, final IExpr expr, final IExpr variablesList,
      final IExpr xVariable) {
    return quaternary(FindFit, data, expr, variablesList, xVariable);
  }

  public static IAST FindFormula(final IExpr data, final IExpr x) {
    return new AST2(FindFormula, data, x);
  }

  public static IAST FindFormula(final IExpr data, final IExpr x, IExpr n) {
    return new AST3(FindFormula, data, x, n);
  }

  public static IAST FindMaximum(final IExpr f, final IExpr x) {
    return new AST2(FindMaximum, f, x);
  }

  public static IAST FindMinimum(final IExpr f, final IExpr x) {
    return new AST2(FindMinimum, f, x);
  }

  public static IAST FindRoot(final IExpr f, final IExpr x) {
    return new AST2(FindRoot, f, x);
  }

  public static IAST FindRoot(final IExpr f, final IExpr x, final IExpr option) {
    return new AST3(FindRoot, f, x, option);
  }

  public static IAST FindShortestPath(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(FindShortestPath, a0, a1, a2);
  }

  public static IAST FindShortestTour(final IExpr a0) {
    return new AST1(FindShortestTour, a0);
  }

  public static IAST FindSpanningTree(final IExpr a0) {
    return new AST1(FindSpanningTree, a0);
  }

  public static IAST Fit(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Fit, a0, a1, a2);
  }

  public static IAST FiveNum(final IExpr a) {
    return new AST1(FiveNum, a);
  }

  public static IAST First(final IExpr a0) {
    return new AST1(First, a0);
  }

  public static IAST Flatten(final IExpr a0) {
    return new AST1(Flatten, a0);
  }

  public static IAST Flatten(final IExpr a0, final IExpr a1) {
    return new AST2(Flatten, a0, a1);
  }

  public static IAST Floor(final IExpr z) {
    return new AST1(Floor, z);
  }

  public static IAST Fold(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Fold, a0, a1, a2);
  }

  public static IAST ForAll(final IExpr a0, final IExpr a1) {
    return new AST2(ForAll, a0, a1);
  }

  public static IAST ForAll(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(ForAll, a0, a1, a2);
  }

  /**
   * Create a "fractional" number
   *
   * @param value the rational value which should be converted to a fractional number
   * @return IFraction
   */
  public static IFraction fraction(final BigFraction value) {
    return AbstractFractionSym.valueOf(value);
  }

  /**
   * Create a "fractional" number
   *
   * @param numerator numerator of the fractional number
   * @param denominator denumerator of the fractional number
   * @return IFraction
   */
  public static IFraction fraction(final BigInteger numerator, final BigInteger denominator) {
    return AbstractFractionSym.valueOf(numerator, denominator);
  }

  /**
   * Create a "fractional" number
   *
   * @param numerator numerator of the fractional number
   * @param denominator denumerator of the fractional number
   * @return IFraction
   */
  public static IFraction fraction(final IInteger numerator, final IInteger denominator) {
    return AbstractFractionSym.valueOf(numerator, denominator);
  }

  /**
   * Create a "fractional" number
   *
   * @param numerator numerator of the fractional number
   * @param denominator denumerator of the fractional number
   * @return IFraction
   * @deprecated
   */
  @Deprecated
  public static IRational fraction(final long numerator, final long denominator) {
    return AbstractFractionSym.valueOf(numerator, denominator);
  }

  /**
   * Create a "fractional" number from a double number with <code>Config.DOUBLE_EPSILON</code>
   * maximum error allowed.
   *
   * @param value the double value which should be converted to a fractional number
   */
  public static IFraction fraction(final double value) {
    return AbstractFractionSym.valueOfEpsilon(value, Config.DOUBLE_EPSILON);
  }

  /**
   * Create a "fractional" number from a double number.
   *
   * @param value the double value which should be converted to a fractional number
   * @param epsilon maximum error allowed. The resulting fraction is within epsilon of value, in
   *        absolute terms
   */
  public static IFraction fraction(final double value, final double epsilon) {
    return AbstractFractionSym.valueOfEpsilon(value, epsilon);
  }

  /**
   * Create a "fractional" number from a double number.
   *
   * @param value the double value which should be converted to a fractional number
   * @return
   */
  public static IFraction fractionConvergent(final double value) {
    return AbstractFractionSym.valueOfConvergent(value);
  }

  /**
   * Create a "fractional" expression that exactly represents the given double number.
   * <p>
   * This methods returns an {@link IExpr} that, when being evaluated to a double value (using
   * {@link IExpr#evalf()}), results to the exact same value (per bit) as the given one.
   * <p>
   * <p>
   * Because double values are not exact in all cases but this method returns an exact
   * representation of the given double the results may be unexpected if
   * {@code attemptNiceFraction = false}. For example for the value {@code 0.7} the result is
   * {@code 3152519739159347/4503599627370496} and not {@code 7/10} as one would actually
   * expect.<br>
   * When this method is called with {@code attemptNiceFraction=true} it is attempted to compute a
   * result that often meets a human's expectations better and is therefore considered 'nicer'. For
   * example the input {@code 0.7} then results in {@code 7/10}. The 'nicer' results comes with the
   * cost of an increased runtime. Nevertheless the same requirement regarding an evaluation of the
   * result to the exact same value also applies in this case.
   * </p>
   * <p>
   * While in most cases the returned reference is a {@link IFraction}, it is not guaranteed for all
   * cases so users should not expect an IFraction.
   * </p>
   * 
   * @param value the double value which should be converted to a fractional number
   * @param attemptNiceFraction if it should be attempted to compute a 'nicer' fraction
   * @return an expression without floating-point numbers that evaluates to the exact same value
   * @see AbstractFractionSym#valueOfExact
   * @see AbstractFractionSym#valueOfConvergent(double)
   */
  public static IExpr fractionExact(final double value, final boolean attemptNiceFraction) {
    return !attemptNiceFraction ? AbstractFractionSym.valueOfExact(value)
        : AbstractFractionSym.valueOfExactNice(value);
  }

  public static IAST FractionBox(final IExpr a0, final IExpr a1) {
    return new AST2(FractionBox, a0, a1);
  }

  public static IAST FractionalPart(final IExpr a) {
    return new AST1(FractionalPart, a);
  }

  /**
   * Returns {@link S#True} if <code>expr</code> does not contain the pattern <code>form</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FreeQ.md">FreeQ</a>
   *
   * @param expr
   * @param form
   * @return
   */
  public static IAST FreeQ(final IExpr expr, final IExpr form) {
    return new B2.FreeQ(expr, form);
  }

  public static IAST FrechetDistribution(final IExpr a0, final IExpr a1) {
    return new AST2(FrechetDistribution, a0, a1);
  }

  public static IAST FresnelC(final IExpr a) {
    return new AST1(FresnelC, a);
  }

  public static IAST FresnelS(final IExpr a) {
    return new AST1(FresnelS, a);
  }

  public static IAST FromPolarCoordinates(final IExpr a) {
    return new AST1(FromPolarCoordinates, a);
  }

  public static IAST FromSphericalCoordinates(final IExpr a) {
    return new AST1(FromSphericalCoordinates, a);
  }

  public static IAST FullForm(final IExpr a0) {
    return new AST1(FullForm, a0);
  }

  public static IAST FullSimplify(final IExpr a) {
    return new AST1(FullSimplify, a);
  }

  public static IAST Function(final IExpr a0) {
    return new AST1(Function, a0);
  }

  public static IAST Function(final IExpr a0, final IExpr a1) {
    return new AST2(Function, a0, a1);
  }

  public static IAST FunctionDomain(final IExpr a0, final IExpr a1) {
    return new AST2(FunctionDomain, a0, a1);
  }

  public static IAST FunctionDomain(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(FunctionDomain, a0, a1, a2);
  }

  public static IAST FunctionExpand(final IExpr a0) {
    return new AST1(FunctionExpand, a0);
  }

  public static IAST FunctionExpand(final IExpr a0, final IExpr a1) {
    return new AST2(FunctionExpand, a0, a1);
  }

  public static IAST FunctionRange(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(FunctionRange, a0, a1, a2);
  }

  public static IAST FunctionRange(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(FunctionRange, a0, a1, a2, a3);
  }

  public static IAST FunctionURL(final IExpr a0) {
    return new AST1(FunctionURL, a0);
  }

  public static IAST Get(final IExpr a0) {
    return new AST1(Get, a0);
  }

  public static IAST Get(final String str) {
    return new AST1(Get, stringx(str));
  }

  public static IAST Gamma(final IExpr a0) {
    return new AST1(Gamma, a0);
  }

  public static IAST Gamma(final IExpr a0, final IExpr a1) {
    return new AST2(Gamma, a0, a1);
  }

  public static IAST Gamma(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Gamma, a0, a1, a2);
  }

  public static IAST GammaDistribution(final IExpr a0, final IExpr a1) {
    return new AST2(GammaDistribution, a0, a1);
  }

  public static IAST GammaDistribution(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(GammaDistribution, a0, a1, a2, a3);
  }

  public static IAST GammaRegularized(final IExpr a0, final IExpr a1) {
    return new AST2(GammaRegularized, a0, a1);
  }

  public static IAST GammaRegularized(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(GammaRegularized, a0, a1, a2);
  }

  public static IAST Gather(final IExpr a0, final IExpr a1) {
    return new AST2(Gather, a0, a1);
  }

  public static IAST GatherBy(final IExpr a0, final IExpr a1) {
    return new AST2(GatherBy, a0, a1);
  }

  public static IAST GCD(final IExpr a0) {
    return new AST1(GCD, a0);
  }

  public static IAST GCD(final IExpr a0, final IExpr a1) {
    return new AST2(GCD, a0, a1);
  }

  public static IAST GegenbauerC(final IExpr a0, final IExpr a1) {
    return new AST2(GegenbauerC, a0, a1);
  }

  public static IAST GegenbauerC(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(GegenbauerC, a0, a1, a2);
  }

  public static IAST GeoDistance(final IExpr a0, final IExpr a1) {
    return new AST2(GeoDistance, a0, a1);
  }

  public static IAST GeometricMean(final IExpr a0) {
    return new AST1(GeometricMean, a0);
  }

  public static IAST Grad(final IExpr a0, final IExpr a1) {
    return new AST2(Grad, a0, a1);
  }

  public static IAST Graph(final IExpr a0) {
    return new AST1(Graph, a0);
  }

  public static IAST Graph(final IExpr a0, final IExpr a1) {
    return new AST2(Graph, a0, a1);
  }

  public static IAST Graph(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Graph, a0, a1, a2);
  }

  public static IASTAppendable Graphics() {
    return ast(Graphics);
  }

  public static IASTAppendable Graphics(final IExpr graphicPrimitives) {
    IASTAppendable ast = ast(Graphics);
    ast.append(graphicPrimitives);
    return ast;
  }

  public static IASTAppendable Graphics(final IExpr graphicPrimitives, IAST... optionRules) {
    IASTAppendable ast = ast(Graphics);
    ast.append(graphicPrimitives);
    for (int i = 0; i < optionRules.length; i++) {
      ast.append(optionRules[i]);
    }
    return ast;
  }

  public static IASTAppendable Graphics3D(final IExpr graphicPrimitives) {
    IASTAppendable ast = ast(Graphics3D);
    ast.append(graphicPrimitives);
    return ast;
  }

  public static IAST Graphics3D(final IExpr graphicPrimitives, final IExpr option) {
    IASTAppendable ast = ast(Graphics3D);
    ast.append(graphicPrimitives);
    ast.append(option);
    return ast;
  }

  /**
   * Yields {@link S#True} if <code>x</code> is known to be greater than <code>y</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Greater.md">Greater</a>
   *
   * @param x
   * @param y
   * @return
   */
  public static IAST Greater(final IExpr x, final IExpr y) {
    return new B2.Greater(x, y);
  }

  /**
   * Yields {@link S#True} if <code>x</code> is known to be greater than <code>y</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Greater.md">Greater</a>
   *
   * @param x
   * @param y
   * @return
   */
  public static IAST Greater(final IExpr x, final int y) {
    return new B2.Greater(x, ZZ(y));
  }

  /**
   * Yields {@link S#True} if <code>x</code> is known to be greater equal than <code>y</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterEqual.md">GreaterEqual</a>
   *
   * @param x
   * @param y
   * @return
   */
  public static IAST GreaterEqual(final IExpr x, final IExpr y) {
    return new B2.GreaterEqual(x, y);
  }

  /**
   * Yields {@link S#True} if <code>x</code> is known to be greater equal than <code>y</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterEqual.md">GreaterEqual</a>
   *
   * @param x
   * @param y
   * @return
   */
  public static IAST GreaterEqual(final IExpr x, final int y) {
    return new B2.GreaterEqual(x, ZZ(y));
  }

  public static IAST Gudermannian(final IExpr a0) {
    return new AST1(Gudermannian, a0);
  }

  public static IAST GumbelDistribution() {
    return new AST0(GumbelDistribution);
  }

  public static IAST GumbelDistribution(final IExpr a0, final IExpr a1) {
    return new AST2(GumbelDistribution, a0, a1);
  }

  public static IAST HankelH1(final IExpr a0, final IExpr a1) {
    return new AST2(HankelH1, a0, a1);
  }

  public static IAST HankelH2(final IExpr a0, final IExpr a1) {
    return new AST2(HankelH2, a0, a1);
  }

  public static IAST HarmonicMean(final IExpr a0) {
    return new AST1(HarmonicMean, a0);
  }

  public static IAST HarmonicNumber(final IExpr a) {
    return new AST1(HarmonicNumber, a);
  }

  public static IAST HarmonicNumber(final IExpr a0, final IExpr a1) {
    return new AST2(HarmonicNumber, a0, a1);
  }

  public static IAST Haversine(final IExpr a) {
    return new AST1(Haversine, a);
  }

  public static IAST Head(final IExpr a) {
    return new AST1(Head, a);
  }

  /**
   * Create a new abstract syntax tree (AST).
   *
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   */
  public static final IASTMutable headAST0(final IExpr head) {
    return new AST0(head);
  }

  public static IAST HeavisideLambda(final IExpr a0) {
    return new AST1(HeavisideLambda, a0);
  }

  public static IAST HeavisidePi(final IExpr a0) {
    return new AST1(HeavisidePi, a0);
  }

  public static IAST HeavisideTheta(final IExpr a0) {
    return new AST1(HeavisideTheta, a0);
  }

  public static IExpr heaviside(final IExpr a0, final IExpr defaultValue, EvalEngine engine) {
    IExpr arg = engine.evaluate(a0);
    if (arg.isZero()) {
      return defaultValue;
    }
    return F.HeavisideTheta(arg)//
        .eval(engine);
  }

  public static IAST HermiteH(final IExpr a0, final IExpr a1) {
    return new AST2(HermiteH, a0, a1);
  }

  public static IAST HermitianMatrixQ(final IExpr a0) {
    return new AST1(HermitianMatrixQ, a0);
  }

  public static IAST HilbertMatrix(final IExpr a0) {
    return new AST1(HilbertMatrix, a0);
  }

  public static IAST Histogram(final IExpr a) {
    return new AST1(Histogram, a);
  }

  public static IAST Hold(final IExpr a0) {
    return new AST1(Hold, a0);
  }

  public static IAST HoldComplete(final IExpr a0) {
    return new AST1(HoldComplete, a0);
  }

  public static IAST HoldForm(final IExpr a0) {
    return new AST1(HoldForm, a0);
  }

  public static IAST HoldPattern(final IExpr a0) {
    return new AST1(HoldPattern, a0);
  }

  public static IAST HurwitzLerchPhi(final IExpr z, final IExpr s, final IExpr a) {
    return new AST3(HurwitzLerchPhi, z, s, a);
  }

  public static IAST HurwitzZeta(final IExpr a0, final IExpr a1) {
    return new AST2(HurwitzZeta, a0, a1);
  }

  public static IAST Hyperfactorial(final IExpr a0) {
    return new AST1(Hyperfactorial, a0);
  }

  public static IAST Hypergeometric0F1(final IExpr a0, final IExpr a1) {
    return new AST2(Hypergeometric0F1, a0, a1);
  }

  public static IAST Hypergeometric1F1(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Hypergeometric1F1, a0, a1, a2);
  }

  public static IAST Hypergeometric1F1Regularized(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Hypergeometric1F1Regularized, a0, a1, a2);
  }

  public static IAST Hypergeometric2F1(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(Hypergeometric2F1, a0, a1, a2, a3);
  }

  public static IAST Hypergeometric2F1Regularized(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(Hypergeometric2F1Regularized, a0, a1, a2, a3);
  }

  public static IAST HypergeometricPFQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(HypergeometricPFQ, a0, a1, a2);
  }

  public static IAST HypergeometricPFQRegularized(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(HypergeometricPFQRegularized, a0, a1, a2);
  }

  public static IAST HypergeometricU(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(HypergeometricU, a0, a1, a2);
  }

  public static IAST Identity(final IExpr a0) {
    return new AST1(Identity, a0);
  }

  public static IAST IdentityMatrix(final IExpr a0) {
    return new AST1(IdentityMatrix, a0);
  }

  public static IAST IdentityMatrix(final int dim) {
    return new AST1(IdentityMatrix, F.ZZ(dim));
  }

  /**
   * Returns <code>trueExpr</code> if <code>condition</code> evaluates to {@link S#True}.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/If.md">If</a>
   *
   * @param condition
   * @param trueExpr
   * @return
   */
  public static IAST If(final IExpr condition, final IExpr trueExpr) {
    return new B2.If(condition, trueExpr);
  }

  /**
   * Returns <code>trueExpr</code> if <code>condition</code> evaluates to {@link S#True} and <code>
   * falseExpr</code> if it evaluates to {@link S#False}.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/If.md">If</a>
   *
   * @param condition
   * @param trueExpr
   * @param falseExpr
   * @return
   */
  public static IAST If(final IExpr condition, final IExpr trueExpr, final IExpr falseExpr) {
    return new B3.If(condition, trueExpr, falseExpr);
  }

  /**
   * Returns <code>trueExpr</code> if <code>condition</code> evaluates to {@link S#True} and <code>
   * falseExpr</code> if it evaluates to {@link S#False} or <code>
   * undefinedExpr</code> if condition don't evakluate to {@link S#True} or {@link S#False}.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/If.md">If</a>
   *
   * @param condition
   * @param trueExpr
   * @param falseExpr
   * @return
   */
  public static IAST If(final IExpr condition, final IExpr trueExpr, final IExpr falseExpr,
      final IExpr undefinedExpr) {
    return quaternary(If, condition, trueExpr, falseExpr, undefinedExpr);
  }

  public static IAST IInit(final ISymbol sym, int[] sizes) {
    sym.createRulesData(sizes);
    return null;
  }

  public static IExpr Im(final IExpr a0) {
    if (a0 != null && a0.isNumber()) {
      return ((INumber) a0).im();
    }
    return new B1.Im(a0);
  }

  public static IAST Implies(final IExpr a0, final IExpr a1) {
    return new AST2(Implies, a0, a1);
  }

  public static IAST In(final IExpr a0) {
    return new AST1(In, a0);
  }

  public static IAST Increment(final IExpr a) {
    return new AST1(Increment, a);
  }

  public static IAST Inequality(final IExpr... a) {
    return function(Inequality, a);
  }

  /**
   *
   *
   * <pre>
   * InexactNumberQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>expr</code> is not an exact number, and <code>False
   * </code> otherwise.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; InexactNumberQ(a)
   * False
   *
   * &gt;&gt; InexactNumberQ(3.0)
   * True
   *
   * &gt;&gt; InexactNumberQ(2/3)
   * False
   * </pre>
   *
   * <p>
   * <code>InexactNumberQ</code> can be applied to complex numbers:
   *
   * <pre>
   * &gt;&gt; InexactNumberQ(4.0+I)
   * True
   * </pre>
   */
  public static IAST InexactNumberQ(final IExpr a) {
    return new AST1(InexactNumberQ, a);
  }

  public static IAST Information(final IExpr a) {
    return new AST1(Information, a);
  }

  public static IAST Information(final IExpr a0, final IExpr a1) {
    return new AST2(Information, a0, a1);
  }

  public static IPattern initPredefinedPattern(final ISymbol symbol) {
    IPattern temp = new Pattern(symbol);
    PREDEFINED_PATTERN_MAP.put(symbol.toString(), temp);
    return temp;
  }

  public static IPatternSequence initPredefinedPatternSequence(final ISymbol symbol) {
    return initPredefinedPatternSequence(symbol, false);
  }

  public static IPatternSequence initPredefinedPatternSequence(final ISymbol symbol,
      boolean zeroArgsAllowed) {
    PatternSequence temp = PatternSequence.valueOf(symbol, zeroArgsAllowed);
    PREDEFINED_PATTERNSEQUENCE_MAP.put(symbol.toString(), temp);
    return temp;
  }

  /** Initialize the complete System */
  public static synchronized void initSymbols() {

    if (!isSystemStarted) {
      // AndroidLoggerFix.fix();
      try {
        isSystemStarted = true;

        if (Config.SHOW_PATTERN_EVAL_STEPS) {
          // watch the rules which are used in pattern matching in
          // system.out
          Config.SHOW_PATTERN_SYMBOL_STEPS.add(Integrate);
        }
        try {
          String autoload = ".\\Autoload";
          if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
            autoload = ".\\AutoloadSymja";
          }
          File sourceLocation = new File(autoload);
          final String[] files = sourceLocation.list();
          if (files != null) {
            for (int i = 0; i < files.length; i++) {
              if (files[i].endsWith(".m")) {
                File sourceFile = new File(sourceLocation, files[i]);
                FileFunctions.Get.loadPackage(EvalEngine.get(), sourceFile);
              }
            }
          }
        } catch (java.security.AccessControlException acex) {
          // no read access for current user
          LOGGER.warn("Cannot read packages in autoload folder:", acex);
        } catch (RuntimeException ex) {
          Errors.rethrowsInterruptException(ex);
          LOGGER.error(ex);
        }
        // if (!noPackageLoading) {
        // Reader reader = null;
        // if (fileName != null) {
        // try {
        // reader = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
        // } catch (FileNotFoundException e) {
        // LOGGER.error("initSymbols() failed", e);
        // }
        // }
        // if (reader == null) {
        // InputStream systemPackage = class.getResourceAsStream("/System.mep");
        // if (systemPackage != null) {
        // reader = new InputStreamReader(systemPackage, "UTF-8");
        // }
        // }
        // if (reader != null) {
        // org.matheclipse.core.builtin.function.Package.loadPackage(EvalEngine.get(), reader);
        // }
        // }

        systemInitialized = true;
      } catch (Throwable th) {
        LOGGER.error("F.initSymbols() failed", th);
      }
    }
  }

  public static IAST Insert(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Insert, a0, a1, a2);
  }

  /**
   * Create a large integer number.
   *
   * @param integerValue
   * @return
   * @deprecated use ZZ()
   */
  @Deprecated
  public static IInteger integer(final BigInteger integerValue) {
    return AbstractIntegerSym.valueOf(integerValue);
  }

  /**
   * Create a large integer number.
   *
   * @param integerValue
   * @return
   * @deprecated use ZZ()
   */
  @Deprecated
  public static IInteger integer(final long integerValue) {
    return AbstractIntegerSym.valueOf(integerValue);
  }

  /**
   * Create a large integer number.
   *
   * @param integerString the integer number represented as a String
   * @param radix the radix to be used while parsing
   * @return Object
   * @deprecated use ZZ()
   */
  @Deprecated
  public static IInteger integer(final String integerString, final int radix) {
    return AbstractIntegerSym.valueOf(integerString, radix);
  }

  public static IAST IntegerPart(final IExpr a0) {
    return new AST1(IntegerPart, a0);
  }

  public static IAST IntegerPartitions(final IExpr n, final IExpr k) {
    return new AST2(IntegerPartitions, n, k);
  }

  public static IAST IntegerPartitions(final IExpr n, final IExpr k, final IExpr p) {
    return new AST3(IntegerPartitions, n, k, p);
  }

  public static IAST IntegerName(final IExpr a0) {
    return new AST1(IntegerName, a0);
  }

  public static IAST IntegerName(final IExpr a0, final IExpr a1) {
    return new AST2(IntegerName, a0, a1);
  }

  /**
   *
   *
   * <pre>
   * IntegerQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>expr</code> is an integer, and <code>False</code> otherwise.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; IntegerQ(3)
   * 4
   *
   * &gt;&gt; IntegerQ(Pi)
   * False
   * </pre>
   */
  public static IAST IntegerQ(final IExpr expr) {
    return new B1.IntegerQ(expr);
  }

  /**
   * Integrates <code>f</code> with respect to <code>x</code>. The result does not contain the
   * additive integration constant.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Integrate.md">Integrate</a>
   *
   * @param f
   * @param x
   * @return
   */
  public static IAST Integrate(final IExpr f, final IExpr x) {
    return new B2.Integrate(f, x);
  }

  public static IAST Integrate(final IExpr f, final IExpr x, final IExpr assumptions) {
    return new AST3(Integrate, f, x, assumptions);
  }

  public static IAST Interpolation(final IExpr list) {
    return new AST1(Interpolation, list);
  }

  public static IAST InterpolatingPolynomial(final IExpr a0, final IExpr a1) {
    return new AST2(InterpolatingPolynomial, a0, a1);
  }

  public static IAST InterquartileRange(final IExpr a) {
    return new AST1(InterquartileRange, a);
  }

  /**
   * Create a new <code>List</code> with the given <code>capacity</code>.
   *
   * @param capacity the assumed number of arguments (+ 1 for the header expression is added
   *        internally).
   * @return
   */
  public static IASTAppendable IntervalAlloc(int capacity) {
    return ast(Interval, capacity);
  }

  public static IASTAppendable IntervalDataAlloc(int capacity) {
    return ast(IntervalData, capacity);
  }

  /**
   * Create an "interval" expression: <code>Interval(list)</code>.
   *
   * @param list
   * @return
   */
  public static IAST Interval(final IExpr list) {
    return new AST1(Interval, list);
  }

  /**
   * Create an "interval" expression: <code>Interval(List(from, to))</code>.
   *
   * @param min minimum value of the interval
   * @param max maximum value of the interval
   * @return
   */
  public static IAST Interval(final IExpr min, final IExpr max) {
    return new AST1(Interval, binaryAST2(List, min, max));
  }

  public static IAST IntervalComplement(final IExpr a1, IExpr a2) {
    return new AST2(IntervalComplement, a1, a2);
  }

  /**
   * IntervalIntersection(interval_1, interval_2, ...) - compute the intersection of the intervals
   * `interval_1, interval_2, ...`
   * 
   */
  public static IAST IntervalIntersection(final IExpr... intervals) {
    return ast(intervals, IntervalIntersection);
  }

  public static IAST IntervalMemberQ(final IExpr interval, IExpr expr) {
    return new AST2(IntervalMemberQ, interval, expr);
  }

  /**
   * IntervalUnion(interval_1, interval_2, ...) - compute the union of the intervals `interval_1,
   * interval_2, ...`
   * 
   */
  public static IAST IntervalUnion(final IExpr... intervals) {
    return ast(intervals, IntervalUnion);
  }

  /**
   * <p>
   * Intervals will be represented by objects with head {@link S#IntervalData} wrapped around a
   * sequence of quadruples of the form, e.g., <code>{a,Less,LessEqual,b}</code> representing the
   * half open interval <code>(a,b]</code>. The empty interval is represented by
   * <code>Interval()</code>.
   * 
   * <p>
   * See: <a href=
   * "https://mathematica.stackexchange.com/questions/162486/operating-with-real-intervals/162505#162505">162486/operating-with-real-intervals/162505#162505</a>
   *
   */
  public static IAST IntervalData(final IAST list) {
    return new AST1(IntervalData, list);
  }

  /**
   * <p>
   * Intervals will be represented by objects with head {@link S#IntervalData} wrapped around a
   * sequence of quadruples of the form, e.g., <code>{a,Less,LessEqual,b}</code> representing the
   * half open interval <code>(a,b]</code>. The empty interval set is represented by
   * <code>Interval()</code>.
   * 
   * <p>
   * See: <a href=
   * "https://mathematica.stackexchange.com/questions/162486/operating-with-real-intervals/162505#162505">162486/operating-with-real-intervals/162505#162505</a>
   *
   */
  public static IAST IntervalData(final IAST... lists) {
    return new AST(IntervalData, lists);
  }

  /**
   * Iterate over an integer range <code>from <= i <= to</code> with the step <code>step/code>.
   *
   * @param head the header symbol of the result
   * @param function the function which should be applied on each iterator value
   * @param from from this position (included)
   * @param to to this position (included)
   * @param step
   * @return
   */
  public static IAST intIterator(ISymbol head, final Function<IExpr, IExpr> function,
      final int from, final int to, final int step) {
    IASTAppendable result = ast(head, to - from + 1);
    long numberOfLeaves = 0;
    for (int i = from; i <= to; i += step) {
      IExpr temp = function.apply(ZZ(i));
      numberOfLeaves += temp.leafCount() + 1;
      if (numberOfLeaves >= Config.MAX_AST_SIZE) {
        ASTElementLimitExceeded.throwIt(numberOfLeaves);
      }
      result.append(temp);

    }
    return result;
  }

  /**
   * Iterate over an integer range <code>from <= i <= to</code> with the step <code>step/code>.
   *
   * @param head the header symbol of the result
   * @param function the integer function which should be applied on each iterator value
   * @param from
   * @param to
   * @param step
   * @return
   */
  public static IAST intIterator(ISymbol head, final IntFunction<IExpr> function, final int from,
      final int to, final int step) {
    IASTAppendable result = ast(head, to - from + 1);
    for (int i = from; i <= to; i += step) {
      result.append(EvalEngine.get().evaluate(function.apply(i)));
    }
    return result;
  }

  public static IAST intIterator(ISymbol head, final Function<IExpr, IExpr> function,
      final IAST list) {
    IASTAppendable result = ast(head, list.size());
    for (int i = 1; i < list.size(); i++) {
      result.append(function.apply(list.get(i)));
    }
    return result;
  }

  public static IRational productRational(final IntFunction<IRational> function, final int from,
      final int to, final int step) {
    IRational result = C1;
    for (int i = from; i <= to; i += step) {
      result = result.multiply(function.apply(i));
    }
    return result;
  }

  public static IRational sumRational(final IntFunction<IRational> function, final int from,
      final int to, final int step) {
    IRational result = C0;
    for (int i = from; i <= to; i += step) {
      result = result.add(function.apply(i));
    }
    return result;
  }

  public static IAST Intersection(final IExpr a0, final IExpr a1) {
    return new AST2(Intersection, a0, a1);
  }

  public static IAST Inverse(final IExpr a0) {
    return new AST1(Inverse, a0);
  }

  public static IAST InverseBetaRegularized(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(InverseBetaRegularized, a0, a1, a2);
  }

  public static IAST InverseBetaRegularized(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(InverseBetaRegularized, a0, a1, a2, a3);
  }

  public static IAST InverseCDF(final IExpr distribution, final IExpr q) {
    return new AST2(InverseCDF, distribution, q);
  }

  public static IAST InverseErf(final IExpr a0) {
    return new AST1(InverseErf, a0);
  }

  public static IAST InverseErf(final IExpr a0, final IExpr a1) {
    return new AST2(InverseErf, a0, a1);
  }

  public static IAST InverseErfc(final IExpr a0) {
    return new AST1(InverseErfc, a0);
  }

  public static IAST InverseFunction(final IExpr a) {
    return new AST1(InverseFunction, a);
  }

  public static IAST InverseFunction(final IExpr function, final IExpr nthArg,
      final IExpr totalArgs) {
    return new AST3(InverseFunction, function, nthArg, totalArgs);
  }

  public static IAST InverseGammaRegularized(final IExpr a0, final IExpr a1) {
    return new AST2(InverseGammaRegularized, a0, a1);
  }

  public static IAST InverseGammaRegularized(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(InverseGammaRegularized, a0, a1, a2);
  }

  public static IAST InverseGudermannian(final IExpr a0) {
    return new AST1(InverseGudermannian, a0);
  }

  public static IAST InverseHaversine(final IExpr a) {
    return new AST1(InverseHaversine, a);
  }

  public static IAST InverseJacobiCD(final IExpr a0, final IExpr a1) {
    return new AST2(InverseJacobiCD, a0, a1);
  }

  public static IAST InverseJacobiCN(final IExpr a0, final IExpr a1) {
    return new AST2(InverseJacobiCN, a0, a1);
  }

  public static IAST InverseJacobiDC(final IExpr a0, final IExpr a1) {
    return new AST2(InverseJacobiDC, a0, a1);
  }

  public static IAST InverseJacobiNC(final IExpr a0, final IExpr a1) {
    return new AST2(InverseJacobiNC, a0, a1);
  }

  public static IAST InverseJacobiND(final IExpr a0, final IExpr a1) {
    return new AST2(InverseJacobiND, a0, a1);
  }

  public static IAST InverseJacobiDN(final IExpr a0, final IExpr a1) {
    return new AST2(InverseJacobiDN, a0, a1);
  }

  public static IAST InverseJacobiSC(final IExpr a0, final IExpr a1) {
    return new AST2(InverseJacobiSC, a0, a1);
  }

  public static IAST InverseJacobiSD(final IExpr a0, final IExpr a1) {
    return new AST2(InverseJacobiSD, a0, a1);
  }

  public static IAST InverseJacobiSN(final IExpr a0, final IExpr a1) {
    return new AST2(InverseJacobiSN, a0, a1);
  }


  public static IAST InverseLaplaceTransform(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(InverseLaplaceTransform, a0, a1, a2);
  }


  public static IAST InverseZTransform(final IExpr expr, final IExpr z, final IExpr n) {
    return new AST3(InverseZTransform, expr, z, n);
  }

  /**
   * Assign the evaluated <code>rhs</code> to the <code>lhs</code>.<br>
   * <b>Note:</b> this method returns <code>F.NIL</code>.
   *
   * @param lhs left-hand-side of the assignment
   * @param rhs right-hand-side of the assignment
   * @return <code>F.NIL</code>
   */
  public static IAST ISet(final IAST lhs, final IExpr rhs) {
    return ISet(lhs, rhs, false);
  }

  /**
   * Assign the evaluated <code>rhs</code> to the <code>lhs</code>.<br>
   * <b>Note:</b> this method returns <code>F.NIL</code>.
   *
   * @param lhs left-hand-side of the assignment
   * @param rhs right-hand-side of the assignment
   * @param equalRule <code>true</code> if the leftHandSide could be matched with equality
   * @return <code>F.NIL</code>
   */
  public static IAST ISet(final IAST lhs, final IExpr rhs, boolean equalRule) {
    lhs.addEvalFlags(IAST.IS_FLATTENED_OR_SORTED_MASK);
    PatternMatching.setDownRule(lhs, rhs, equalRule, true);
    return NIL;
  }

  /**
   * Assign the evaluated <code>rhs</code> to the <code>lhs</code>.<br>
   * <b>Note:</b> this method returns <code>F.NIL</code>.
   * 
   * @param lhs left-hand-side of the assignment
   * @param rhs right-hand-side of the assignment
   * @return
   */
  public static IAST ISet(final ISymbol lhs, final IExpr rhs) {
    PatternMatching.setDownRule(lhs, rhs);
    return NIL;
  }

  /**
   * Assign the unevaluated <code>rhs</code> to the <code>lhs</code>.<br>
   * <b>Note:</b> this method returns <code>NIL</code>.
   *
   * @param lhs left-hand-side of the assignment
   * @param rhs right-hand-side of the assignment
   * @return <code>NIL</code>
   */
  public static IAST ISetDelayed(final IAST lhs, final IExpr rhs) {
    lhs.addEvalFlags(IAST.IS_FLATTENED_OR_SORTED_MASK);
    PatternMatching.setDelayedDownRule(IPatternMap.DEFAULT_RULE_PRIORITY, lhs, rhs, true);
    return NIL;
  }

  /**
   * Assign the unevaluated <code>rhs</code> to the <code>lhs</code>.<br>
   * <b>Note:</b> this method returns <code>NIL</code>.
   * 
   * @param priority
   * @param lhs left-hand-side of the assignment
   * @param rhs right-hand-side of the assignment
   * @return <code>NIL</code>
   */
  public static IAST ISetDelayed(int priority, final IAST lhs, final IExpr rhs) {
    lhs.addEvalFlags(IAST.IS_FLATTENED_OR_SORTED_MASK);
    PatternMatching.setDelayedDownRule(priority, lhs, rhs, true);
    return NIL;
  }

  /**
   * Assign the unevaluated <code>rhs</code> to the <code>lhs</code>.<br>
   * <b>Note:</b> this method returns <code>NIL</code>.
   * 
   * @param priority
   * @param lhs left-hand-side of the assignment
   * @param rhs right-hand-side of the assignment
   * @return <code>NIL</code>
   */
  public static IExpr IIntegrate(int priority, final IAST lhs, final IExpr rhs) {
    lhs.addEvalFlags(IAST.IS_FLATTENED_OR_SORTED_MASK);
    org.matheclipse.core.reflection.system.Integrate.INTEGRATE_RULES_DATA.integrate(lhs, rhs,
        priority);
    return NIL;
  }

  public static boolean isNumEqualInteger(double value, IInteger ii) throws ArithmeticException {
    return isZero(value - ii.doubleValue(), Config.DOUBLE_TOLERANCE);
  }

  /**
   * Test if <code>rational.doubleValue()</code> equals <code>value</code> within the tolerance
   * <code>Config.DOUBLE_TOLERANCE</code>.
   *
   * @param value
   * @param rational
   * @return
   * @throws ArithmeticException
   */
  public static boolean isNumEqualRational(double value, IRational rational)
      throws ArithmeticException {
    return isZero(value - rational.doubleValue(), Config.DOUBLE_TOLERANCE);
  }

  /**
   * Test if the value is a Java <code>int</code> value within the tolerance <code>
   * Config.DOUBLE_TOLERANCE</code>.
   *
   * @param value
   * @return
   */
  public static boolean isNumIntValue(double value) {
    return isZero(value - Math.rint(value), Config.DOUBLE_TOLERANCE);
  }

  /**
   * Test if the value is a Java <code>int</code> value within the given tolerance <code>epsilon
   * </code>.
   *
   * @param value
   * @param epsilon the tolerance
   * @return
   */
  public static boolean isNumIntValue(double value, double epsilon) {
    return isZero(value - Math.rint(value), epsilon);
  }

  public static boolean isNumIntValue(double value, int i) {
    return isZero(value - i, Config.DOUBLE_TOLERANCE);
  }

  /**
   * Check difference is less than a constant
   *
   * <p>
   * infinity == infinity returns true eg 1/0
   *
   * <p>
   * -infinity == infinity returns false eg -1/0
   *
   * <p>
   * -infinity == -infinity returns true
   *
   * <p>
   * undefined == undefined returns false eg 0/0
   *
   * @return whether x is equal to y
   */
  public static final boolean isEqual(double x, double y) {
    return isFuzzyEquals(x, y, Config.MACHINE_EPSILON);
  }

  public static final boolean isEqual(org.hipparchus.complex.Complex x,
      org.hipparchus.complex.Complex y) {
    return isFuzzyEquals(x, y, Config.MACHINE_EPSILON);
  }

  /**
   * Returns {@code true} if {@code a} and {@code b} are within {@code tolerance} (exclusive) of
   * each other.
   *
   * <p>
   * Technically speaking, this is equivalent to {@code Math.abs(a - b) <= tolerance ||
   * Double.valueOf(a).equals(Double.valueOf(b))}.
   *
   * <p>
   * Notable special cases include:
   *
   * <ul>
   * <li>All NaNs are fuzzily equal.
   * <li>If {@code a == b}, then {@code a} and {@code b} are always fuzzily equal.
   * <li>Positive and negative zero are always fuzzily equal.
   * <li>If {@code tolerance} is zero, and neither {@code a} nor {@code b} is NaN, then {@code a}
   * and {@code b} are fuzzily equal if and only if {@code a == b}.
   * <li>With {@link Double#POSITIVE_INFINITY} tolerance, all non-NaN values are fuzzily equal.
   * <li>With finite tolerance, {@code Double.POSITIVE_INFINITY} and {@code
   *       Double.NEGATIVE_INFINITY} are fuzzily equal only to themselves.
   * </ul>
   *
   * <p>
   * This is reflexive and symmetric, but <em>not</em> transitive, so it is <em>not</em> an
   * equivalence relation and <em>not</em> suitable for use in {@link Object#equals}
   * implementations.
   *
   * @throws IllegalArgumentException if {@code tolerance} is {@code < 0} or NaN
   */
  public static boolean isFuzzyEquals(double a, double b, double tolerance) {
    return Math.copySign(a - b, 1.0) < tolerance
        // copySign(x, 1.0) is a branch-free version of abs(x), but with different NaN semantics
        || (a == b) // needed to ensure that infinities equal themselves
        || (Double.isNaN(a) && Double.isNaN(b));
  }

  public static final boolean isFuzzyEquals(org.hipparchus.complex.Complex x,
      org.hipparchus.complex.Complex y, double tolerance) {
    return isFuzzyEquals(x.getReal(), y.getReal(), tolerance) //
        && isFuzzyEquals(x.getImaginary(), y.getImaginary(), tolerance);
  }

  /**
   * Test if <code>a</code> and <code>b</code> are almost the same values by comparing the
   * {@link Double#doubleToLongBits(double)} values.
   * <p>
   * See also the implementation of {@link Double#compare(double, double)}
   * 
   * @param a
   * @param b
   */
  public static boolean isAlmostSame(double a, double b) {
    if (a == b) {
      return true;
    } else {
      long ai = Double.doubleToLongBits(a);
      long bi = Double.doubleToLongBits(b);
      return -1 <= ai - bi && ai - bi <= 1;
    }
  }

  /**
   * Calculate the relative difference between x and y. In case |x+y|/2 is zero the absolute
   * difference is returned.
   *
   * @param x first value
   * @param y second value
   * @return relative error
   */
  public static double relativeDifference(final double x, final double y) {
    double error;
    if (Double.isInfinite(x) && Double.isInfinite(y)) {
      if (Double.compare(x, y) == 0) {
        error = 0;
      } else {
        error = Double.POSITIVE_INFINITY;
      }
    } else {
      final double z = java.lang.Math.abs(x + y) / 2;
      error = java.lang.Math.abs(x - y);
      if (z > 0) {
        error /= z;
      }
    }
    return error;
  }

  /**
   * Test if the absolute value is less <code>Config.DOUBLE_EPSILON</code>.
   *
   * @param value
   * @return
   */
  public static boolean isZero(double value) {
    return isZero(value, Config.MACHINE_EPSILON);
  }

  /**
   * Test if the absolute value is less <code>Config.MACHINE_EPSILON</code>.
   *
   * @param value
   * @return
   */
  public static boolean isZero(org.hipparchus.complex.Complex value) {
    return org.hipparchus.complex.Complex.equals(value, org.hipparchus.complex.Complex.ZERO,
        Config.MACHINE_EPSILON);
    // return isZero(value.getReal(), Config.MACHINE_EPSILON) && isZero(value.getImaginary(),
    // Config.MACHINE_EPSILON);
  }

  /**
   * Test if the absolute value is less than the given epsilon.
   *
   * @param x
   * @param epsilon
   * @return
   */
  public static boolean isZero(double x, double epsilon) {
    return isFuzzyEquals(x, 0.0, epsilon);
    // return -epsilon < x && x < epsilon;
  }

  /**
   * Test if the absolute value is less <code>Config.MACHINE_EPSILON</code>.
   *
   * @param x
   * @param epsilon
   * @return
   */
  public static boolean isZero(org.hipparchus.complex.Complex x, double epsilon) {
    return org.hipparchus.complex.Complex.equals(x, org.hipparchus.complex.Complex.ZERO, epsilon);
  }

  public static boolean isZero(Apfloat x, double epsilon) {
    Apfloat eps = new Apfloat(epsilon, x.precision());
    return x.compareTo(eps.negate()) > 0 && x.compareTo(eps) < 0;
  }

  public static boolean isZero(Apfloat x, Apfloat epsilon) {
    return x.compareTo(epsilon.negate()) > 0 && x.compareTo(epsilon) < 0;
  }

  public static boolean isZero(Apcomplex x, double epsilon) {
    Apfloat eps = new Apfloat(epsilon, x.precision());
    return isZero(x, eps);
  }

  public static boolean isZero(Apcomplex x, Apfloat epsilon) {
    Apfloat epsNegate = epsilon.negate();
    return x.real().compareTo(epsNegate) > 0 && x.real().compareTo(epsilon) < 0 //
        && x.imag().compareTo(epsNegate) > 0 && x.imag().compareTo(epsilon) < 0;
  }

  /**
   * Create JavaScript form data in the given format.
   *
   * @param plainJavaScript
   * @param format
   * @return
   */
  public static IAST JSFormData(final String plainJavaScript, final String format) {
    return new AST2(JSFormData, $str(plainJavaScript), $str(format));
  }

  public static IAST JacobiAmplitude(final IExpr a0, final IExpr a1) {
    return new AST2(JacobiAmplitude, a0, a1);
  }

  public static IAST JacobiCD(final IExpr a0, final IExpr a1) {
    return new AST2(JacobiCD, a0, a1);
  }

  public static IAST JacobiCN(final IExpr a0, final IExpr a1) {
    return new AST2(JacobiCN, a0, a1);
  }

  public static IAST JacobiDC(final IExpr a0, final IExpr a1) {
    return new AST2(JacobiDC, a0, a1);
  }

  public static IAST JacobiEpsilon(final IExpr a0, final IExpr a1) {
    return new AST2(JacobiEpsilon, a0, a1);
  }

  public static IAST JacobiNC(final IExpr a0, final IExpr a1) {
    return new AST2(JacobiNC, a0, a1);
  }

  public static IAST JacobiND(final IExpr a0, final IExpr a1) {
    return new AST2(JacobiND, a0, a1);
  }

  public static IAST JacobiP(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return function(JacobiP, a0, a1, a2, a3);
  }

  public static IAST JacobiDN(final IExpr a0, final IExpr a1) {
    return new AST2(JacobiDN, a0, a1);
  }

  public static IAST JacobiSC(final IExpr a0, final IExpr a1) {
    return new AST2(JacobiSC, a0, a1);
  }

  public static IAST JacobiSD(final IExpr a0, final IExpr a1) {
    return new AST2(JacobiSD, a0, a1);
  }

  public static IAST JacobiSN(final IExpr a0, final IExpr a1) {
    return new AST2(JacobiSN, a0, a1);
  }

  public static IAST JacobiSymbol(final IExpr a0, final IExpr a1) {
    return new AST2(JacobiSymbol, a0, a1);
  }

  public static IAST JavaForm(final IExpr a0, final IExpr a1) {
    return new AST2(JavaForm, a0, a1);
  }

  public static IAST Join(final IExpr a0, final IExpr a1) {
    return new AST2(Join, a0, a1);
  }

  public static IAST KelvinBei(final IExpr a0, final IExpr a1) {
    return new AST2(KelvinBei, a0, a1);
  }

  public static IAST KelvinBer(final IExpr a0, final IExpr a1) {
    return new AST2(KelvinBer, a0, a1);
  }

  public static IAST Key(final IExpr a0) {
    return new AST1(Key, a0);
  }

  public static IAST KleinInvariantJ(final IExpr a0) {
    return new AST1(KleinInvariantJ, a0);
  }

  public static IAST KroneckerDelta(final IExpr a0) {
    return new AST1(KroneckerDelta, a0);
  }

  public static IAST KroneckerDelta(final IExpr a0, final IExpr a1) {
    return new AST2(KroneckerDelta, a0, a1);
  }

  public static IAST KroneckerSymbol(final IExpr a0, final IExpr a1) {
    return new AST2(KroneckerSymbol, a0, a1);
  }

  public static IAST LaguerreL(final IExpr a0, final IExpr a1) {
    return new AST2(LaguerreL, a0, a1);
  }

  public static IAST LaguerreL(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(LaguerreL, a0, a1, a2);
  }

  public static IAST LaplaceTransform(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(LaplaceTransform, a0, a1, a2);
  }

  public static IAST Last(final IExpr a0) {
    return new AST1(Last, a0);
  }

  public static IAST LCM(final IExpr a0, final IExpr a1) {
    return new AST2(LCM, a0, a1);
  }

  public static IAST LegendreP(final IExpr a0, final IExpr a1) {
    return new AST2(LegendreP, a0, a1);
  }

  public static IAST LegendreP(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(LegendreP, a0, a1, a2);
  }

  public static IAST LegendreQ(final IExpr a0, final IExpr a1) {
    return new AST2(LegendreQ, a0, a1);
  }

  public static IAST LegendreQ(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(LegendreQ, a0, a1, a2);
  }

  public static IAST LeafCount(final IExpr expr) {
    return new AST1(LeafCount, expr);
  }

  public static IAST Length(final IExpr expr) {
    return new AST1(Length, expr);
  }

  public static IAST LerchPhi(final IExpr z, final IExpr s, final IExpr a) {
    return new AST3(LerchPhi, z, s, a);
  }

  /**
   * Yields {@link S#True} if <code>x</code> is known to be less than <code>y</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Less.md">Less</a>
   *
   * @param x
   * @param y
   * @return
   */
  public static IAST Less(final IExpr x, final IExpr y) {
    return new B2.Less(x, y);
  }

  /**
   * Yields {@link S#True} if <code>x</code> is known to be less than <code>y</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Less.md">Less</a>
   *
   * @param x
   * @param y
   * @return
   */
  public static IAST Less(final IExpr x, final int y) {
    return new B2.Less(x, ZZ(y));
  }

  /**
   * Yields {@link S#True} if <code>x1</code> is known to be less than <code>x2</code> and <code>x2
   * </code> is known to be less than <code>x3</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Less.md">Less</a>
   *
   * @param x1
   * @param x2
   * @param x3
   * @return
   */
  public static IAST Less(final IExpr x1, final IExpr x2, final IExpr x3) {
    return new AST3(Less, x1, x2, x3);
  }

  public static IAST Less(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(Less, a0, a1, a2, a3);
  }

  public static IAST Less(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3,
      final IExpr a4) {
    return quinary(Less, a0, a1, a2, a3, a4);
  }

  /**
   * Yields {@link S#True} if <code>x</code> is known to be less equal than <code>y</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqual.md">LessEqual</a>
   *
   * @param x
   * @param y
   * @return
   */
  public static IAST LessEqual(final IExpr x, final IExpr y) {
    return new B2.LessEqual(x, y);
  }

  /**
   * Yields {@link S#True} if <code>x</code> is known to be less equal than <code>y</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqual.md">LessEqual</a>
   *
   * @param x
   * @param y
   * @return
   */
  public static IAST LessEqual(final IExpr x, final int y) {
    return new B2.LessEqual(x, ZZ(y));
  }

  /**
   * Yields {@link S#True} if <code>x1</code> is known to be less equal than <code>x2</code> and
   * <code>x2</code> is known to be less equal than <code>x3</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqual.md">LessEqual</a>
   *
   * @param x1
   * @param x2
   * @param x3
   * @return
   */
  public static IAST LessEqual(final IExpr x1, final IExpr x2, final IExpr x3) {
    return new AST3(LessEqual, x1, x2, x3);
  }

  public static IAST LessEqual(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(LessEqual, a0, a1, a2, a3);
  }

  public static IAST LeviCivitaTensor(final IExpr d) {
    return new AST1(LeviCivitaTensor, d);
  }

  public static IAST Limit(final IExpr f, final IExpr rule) {
    return new AST2(Limit, f, rule);
  }

  public static IAST Limit(final IExpr f, final IExpr rule, final IExpr direction) {
    return new AST3(Limit, f, rule, direction);
  }

  public static IASTAppendable Line() {
    return ast(Line);
  }

  public static IAST Line(final IExpr listOfPoints) {
    return new B1.Line(listOfPoints);
  }

  public static IAST LinearModelFit(final IExpr a0) {
    return new AST1(LinearModelFit, a0);
  }

  public static IAST LinearModelFit(final IExpr a0, final IExpr a1) {
    return new AST2(LinearModelFit, a0, a1);
  }

  public static IAST LinearModelFit(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(LinearModelFit, a0, a1, a2);
  }

  public static IAST LinearModelFit(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(LinearModelFit, a0, a1, a2, a3);
  }

  public static IAST LinearProgramming(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(LinearProgramming, a0, a1, a2);
  }

  public static IAST LinearSolve(final IExpr a0, final IExpr a1) {
    return new AST2(LinearSolve, a0, a1);
  }

  /**
   * Calulate the allocation size for a new {@link IAST} object. If <code>predicate#test()</code>
   * returns <code>true</code> add the arguments {@link IAST#argSize()} to the <code>ast.argSize()
   * </code>
   *
   * @param ast
   * @param predicate
   * @return
   */
  public static int allocLevel1(final IAST ast, Predicate<IExpr> predicate) {
    int allocSize = ast.argSize();
    for (int i = 1; i < ast.size(); i++) {
      final IExpr arg = ast.get(i);
      if (predicate.test(arg)) {
        allocSize += arg.argSize();
      }
    }
    return allocSize;
  }

  /**
   * Determine the minimum of the <code>ast</code> {@link IAST#argSize()} and integer number 7
   *
   * @param ast
   * @return
   */
  public static int allocMin8(IAST ast) {
    return ast.argSize() < 7 ? ast.argSize() : 7;
  }

  /**
   * Determine the minimum of the <code>size</code> and integer number 7
   *
   * @param size
   * @return
   */
  public static int allocMin8(int size) {
    return size < 7 && size > 0 ? size : 7;
  }

  /**
   * Determine the minimum of the <code>ast</code> {@link IAST#argSize()} and integer number 15
   *
   * @param ast
   * @return
   */
  public static int allocMin16(IAST ast) {
    return ast.argSize() < 15 ? ast.argSize() : 15;
  }

  /**
   * Determine the minimum of the <code>size</code> and integer number 15
   *
   * @param size
   * @return
   */
  public static int allocMin16(int size) {
    return size < 15 && size > 0 ? size : 15;
  }

  /**
   * Determine the minimum of the <code>ast</code> {@link IAST#argSize()} and integer number 31,
   *
   * @param ast
   * @return
   */
  public static int allocMin32(IAST ast) {
    return ast.argSize() < 31 ? ast.argSize() : 31;
  }

  /**
   * Determine the minimum of the <code>size</code> and integer number 31
   *
   * @param size
   * @return
   */
  public static int allocMin32(int size) {
    return size < 31 && size > 0 ? size : 31;
  }

  /**
   * Determine the minimum of the <code>ast</code> {@link IAST#argSize()} and integer number 63,
   *
   * @param ast
   * @return
   */
  public static int allocMin64(IAST ast) {
    return ast.argSize() < 63 ? ast.argSize() : 63;
  }

  /**
   * Determine the maximum of the <code>ast</code> {@link IAST#argSize()} and integer number 7,
   *
   * @param ast
   * @return
   */
  public static int allocMax8(IAST ast) {
    return ast.argSize() > 7 ? ast.argSize() : 7;
  }

  /**
   * Determine the maximum of the <code>ast</code> {@link IAST#argSize()} and integer number 15,
   *
   * @param ast
   * @return
   */
  public static int allocMax16(IAST ast) {
    return ast.argSize() > 15 ? ast.argSize() : 15;
  }

  /**
   * Determine the maximum of the <code>ast</code> {@link IAST#argSize()} and integer number 31,
   *
   * @param ast
   * @return
   */
  public static int allocMax32(IAST ast) {
    return ast.argSize() > 31 ? ast.argSize() : 31;
  }

  /**
   * Determine the maximum of the <code>ast</code> {@link IAST#argSize()} and integer number 63,
   *
   * @param ast
   * @return
   */
  public static int allocMax64(IAST ast) {
    return ast.argSize() > 63 ? ast.argSize() : 63;
  }

  /**
   * Create an appendable list <code>{ }</code>.
   *
   * @return
   * @see {@link #List()} to create an empty unmodifiable AST
   */
  public static IASTAppendable ListAlloc() {
    return ast(List, 3);
  }

  /**
   * Create a new <code>List</code> with the given <code>initial capacity</code>.
   *
   * @param initialCapacity the assumed number of arguments (+ 1 for the header expression is added
   *        internally).
   * @return
   */
  public static IASTAppendable ListAlloc(int initialCapacity) {
    return ast(List, initialCapacity);
  }



  /**
   * Create a new <code>List</code> with the capacity <code>collection.size()</code> and append the
   * elements of the collection.
   *
   * @param collection the collection which holds the elements which should be appended
   * @return
   */
  public static IASTAppendable ListAlloc(Collection<? extends IExpr> collection) {
    return ListAlloc(collection, 0);
  }

  /**
   * Create a new <code>List</code> with the capacity <code>collection.size() + capacity</code> and
   * append the elements of the collection.
   *
   * @param collection
   * @param capacity
   * @return
   */
  public static IASTAppendable ListAlloc(Collection<? extends IExpr> collection, int capacity) {
    IASTAppendable result = ast(List, collection.size() + capacity);
    result.appendAll(collection);
    return result;
  }

  /**
   * Create an appendable list <code>{ }</code>.
   *
   * @param a
   * @return
   * @see {@link #List(final IExpr...)} to create an unmodifiable AST
   */
  public static IASTAppendable ListAlloc(final IExpr... a) {
    return ast(a, List);
  }

  /**
   * Constructs a list that holds the expressions of the input stream.
   * 
   * <p>
   * for instance,
   * <ul>
   * <li>if the stream consists of {@link IReal}s, the return value represents a vector,
   * <li>if the stream consists of vectors, the return value represents a matrix.
   * <li>if the stream consists of matrices, the return value represents a tensor with rank 3.
   * <li>etc.
   * </ul>
   * 
   * @param stream of expressions to form the first level of the return value
   * @return list that holds the expressions of the input stream
   */
  public static IASTAppendable ListAlloc(Stream<? extends IExpr> stream) {
    return ListAlloc(stream.map(IExpr.class::cast).collect(Collectors.toList()));
  }

  public static IASTAppendable ListAlloc(IntStream stream) {
    return ListAlloc(stream.mapToObj(i -> F.ZZ(i)));
  }


  public static IAST TemplateSlot(final IExpr a0) {
    return new AST1(TemplateSlot, a0);
  }

  public static IAST TemplateSlot(final IExpr a0, final IExpr a1) {
    return new AST2(TemplateSlot, a0, a1);
  }

  public static IAST TensorDimensions(final IExpr a0) {
    return new AST1(TensorDimensions, a0);
  }

  /**
   * For positive n, add the first n elements of <code>numbers</code> to the list.For negative n,
   * add the last n elements of <code>numbers</code> to the list.
   *
   * @param n
   * @param numbers
   * @return
   */
  public static IAST tensorList(final int n, final Integer... numbers) {
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
        a[j++] = ZZ(numbers[i]);
      }
    } else {
      if (n < size) {
        size = n;
      }
      for (int i = 0; i < size; i++) {
        a[i] = ZZ(numbers[i]);
      }
    }
    return ast(a, List);
  }

  public static IASTMutable List(final double... numbers) {
    INum[] a = new INum[numbers.length];
    for (int i = 0; i < numbers.length; i++) {
      a[i] = num(numbers[i]);
    }
    return function(List, a);
  }

  public static IASTMutable List(final String... strs) {
    IStringX[] a = new IStringX[strs.length];
    for (int i = 0; i < strs.length; i++) {
      a[i] = stringx(strs[i]);
    }
    return function(List, a);
  }

  /**
   * Create an empty immutable list <code>{ }</code> (i.e. <code>List()</code>).
   *
   * @return
   * @see {@link #ListAlloc()} to create an appendable list
   */
  public static IAST List() {
    return CEmptyList;
  }

  /**
   * Create an immutable list <code>{ }</code> by converting the {@link Object}s expression types
   * into {@link IExpr} types.
   *
   * @param objects the objects which should be converted, before adding them to the list
   * @return a <code>List(...)</code> of expressions
   * @see {@link Object2Expr#convert(Object, boolean, boolean)} for the conversion rules.
   */
  public static IAST listOfObjects(final Object... objects) {
    IExpr[] a = new IExpr[objects.length];
    for (int i = 0; i < objects.length; i++) {
      a[i] = Object2Expr.convert(objects[i], true, false);
    }
    return List(a);
  }

  /**
   * Create an immutable list from the arguments.
   *
   * @param a
   * @return
   * @see {@link #ListAlloc(final IExpr...)} to create an appendable list
   */
  public static IAST List(final IExpr... a) {
    switch (a.length) {
      case 1:
        if (a[0] instanceof IntegerSym) {
          if (a[0].equals(C0)) {
            return CListC0;
          }
          if (a[0].equals(C1)) {
            return CListC1;
          }
          if (a[0].equals(C2)) {
            return CListC2;
          }
        }
        return new B1.List(a[0]);
      case 2:
        if (a[0] instanceof IntegerSym) {
          if (a[0].equals(C1)) {
            if (a[1].equals(C1)) {
              return CListC1C1;
            }
            if (a[1].equals(C2)) {
              return CListC1C2;
            }
          } else if (a[0].equals(C2)) {
            if (a[1].equals(C1)) {
              return CListC2C1;
            }
            if (a[1].equals(C2)) {
              return CListC2C2;
            }
          }
        }
        return new B2.List(a[0], a[1]);
      case 3:
        return new B3.List(a[0], a[1], a[2]);
      default:
        break;
    }
    return ast(a, List);
  }

  public static IAST list(IExpr expr1, IExpr... array) {
    IExpr[] args = new IExpr[array.length + 1];
    args[0] = expr1;
    System.arraycopy(array, 0, args, 1, array.length);
    return F.List(args);
  }

  public static IAST list(IExpr expr1, IExpr expr2, IExpr... array) {
    IExpr[] args = new IExpr[array.length + 2];
    args[0] = expr1;
    args[1] = expr2;
    System.arraycopy(array, 0, args, 2, array.length);
    return F.List(args);
  }

  public static IAST list(IExpr expr1, IExpr expr2, IExpr expr3, IExpr... array) {
    IExpr[] args = new IExpr[array.length + 3];
    args[0] = expr1;
    args[1] = expr2;
    args[2] = expr3;
    System.arraycopy(array, 0, args, 3, array.length);
    return F.List(args);
  }

  /**
   * Return a single value as a <code>List()</code>
   *
   * @param expr
   * @return
   */
  public static IAST list(final IExpr expr) {
    return new B1.List(expr);
  }

  /**
   * Return a pair of values as a <code>List()</code>
   *
   * @param x1
   * @param x2
   * @return
   */
  public static IAST list(final IExpr x1, final IExpr x2) {
    return new B2.List(x1, x2);
  }

  /**
   * Return a pair of values as a <code>List()</code>
   *
   * @param x1
   * @param x2
   * @return
   */
  public static Pair pair(final IExpr x1, final IExpr x2) {
    return new Pair(x1, x2);
  }

  /**
   * Return a triple of values as a <code>List()</code>
   *
   * @param x1
   * @param x2
   * @param x3
   * @return
   */
  public static IAST list(final IExpr x1, final IExpr x2, final IExpr x3) {
    return new B3.List(x1, x2, x3);
  }

  public static IAST List(final long... numbers) {
    IInteger[] a = new IInteger[numbers.length];
    for (int i = 0; i < numbers.length; i++) {
      a[i] = ZZ(numbers[i]);
    }
    return List(a);
  }

  public static IASTMutable List(final int... numbers) {
    IInteger[] a = new IInteger[numbers.length];
    for (int i = 0; i < numbers.length; i++) {
      a[i] = ZZ(numbers[i]);
    }
    return function(List, a);
  }

  public static IAST ListConvolve(final IExpr a0, final IExpr a1) {
    return new AST2(ListConvolve, a0, a1);
  }

  public static IAST ListPlot(final IExpr a) {
    return new AST1(ListPlot, a);
  }

  public static IAST ListPlot3D(final IExpr a) {
    return new AST1(ListPlot3D, a);
  }

  public static IAST ListPointPlot3D(final IExpr a) {
    return new AST1(ListPointPlot3D, a);
  }

  /**
   *
   *
   * <pre>
   * ListQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * tests whether <code>expr</code> is a <code>List</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ListQ({1, 2, 3})
   * True
   *
   * &gt;&gt; ListQ({{1, 2}, {3, 4}})
   * True
   *
   * &gt;&gt; ListQ(x)
   * False
   * </pre>
   */
  public static IAST ListQ(final IExpr a) {
    return new AST1(ListQ, a);
  }

  /**
   * @param a0
   * @return
   * @deprecated use HoldPattern
   */
  @Deprecated
  public static IAST Literal(final IExpr a0) {
    return new AST1(Literal, a0);
  }

  /**
   * Returns the natural logarithm of <code>z</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log.md">Log</a>
   *
   * @param z
   * @return
   */
  public static IAST Log(final IExpr z) {
    return new B1.Log(z);
  }

  /**
   * Returns the natural logarithm of <code>z</code>.
   * 
   * @param z
   * @return
   */
  public static IAST Log(final int z) {
    return new B1.Log(F.ZZ(z));
  }

  /**
   * Returns the logarithm of <code>z</code> for the <code>base</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log.md">Log</a>
   *
   * @param base
   * @param z
   * @return
   */
  public static IAST Log(final IExpr base, final IExpr z) {
    return new AST2(Log, base, z);
  }

  /**
   * Returns the logarithm of <code>z</code> for the <code>base</code>.
   * 
   * @param base
   * @param z
   * @return
   */
  public static IAST Log(final IExpr base, final int z) {
    return Log(base, F.ZZ(z));
  }

  /**
   * Returns the logarithm of <code>z</code> for the <code>base</code>.
   * 
   * @param base
   * @param z
   * @return
   */
  public static IAST Log(final int base, final int z) {
    return Log(F.ZZ(base), F.ZZ(z));
  }

  /**
   * <code>Log[10, a0]</code>.
   *
   * @param a0
   * @return <code>Log[10, a0]</code>.
   */
  public static IAST Log10(final IExpr a0) {

    return new AST2(Log, C10, a0);
  }

  public static IAST LogGamma(final IExpr a0) {
    return new AST1(LogGamma, a0);
  }

  public static IAST LogIntegral(final IExpr a) {
    return new AST1(LogIntegral, a);
  }

  public static IAST LogisticSigmoid(final IExpr a) {
    return new AST1(LogisticSigmoid, a);
  }

  public static IAST LogNormalDistribution(final IExpr a0, final IExpr a1) {
    return new AST2(LogNormalDistribution, a0, a1);
  }

  public static IAST LucasL(final IExpr a) {
    return new AST1(LucasL, a);
  }

  public static IAST LucasL(final IExpr a, final IExpr b) {
    return new AST2(LucasL, a, b);
  }

  /**
   *
   *
   * <pre>
   * MachineNumberQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>expr</code> is a machine-precision real or complex number.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; MachineNumberQ(3.14159265358979324)
   * False
   *
   * &gt;&gt; MachineNumberQ(1.5 + 2.3*I)
   * True
   *
   * &gt;&gt; MachineNumberQ(2.71828182845904524 + 3.14159265358979324*I)
   * False
   *
   * &gt;&gt; MachineNumberQ(1.5 + 3.14159265358979324*I)
   * True
   *
   * &gt;&gt; MachineNumberQ(1.5 + 5 *I)
   * True
   * </pre>
   */
  public static IAST MachineNumberQ(final IExpr a0) {
    return new AST1(MachineNumberQ, a0);
  }

  public static IAST Manipulate(final IExpr a0) {
    return new AST1(Manipulate, a0);
  }

  public static IAST Manipulate(final IExpr a0, final IExpr a1) {
    return new AST2(Manipulate, a0, a1);
  }

  public static IASTAppendable mapFunction(final IExpr head, final IAST ast) {
    IASTAppendable result = F.ast(head, ast.size());
    result.appendArgs(ast);
    return result;
  }

  /**
   * Iterates over the <code>ast</code> elements and calls the function. Append the functions result
   * expression at the end of the result list, if the function results is not equal {@link F#NIL}.
   * If the function results is <code>null</code> stop iterating and return <code>F.NIL</code>.
   * 
   * @param head the head of the result ast
   * @param ast
   * @param function
   * @return {@link F#NIL} if the function returns <code>null</code>
   */
  public static <T extends IExpr> IASTAppendable mapFunction(final IExpr head, final IAST ast,
      Function<T, IExpr> function) {
    IASTAppendable result = F.ast(head, ast.size());
    if (result.append(ast, function)) {
      return result;
    }
    return F.NIL;
  }

  public static <T extends IExpr> IASTAppendable mapFunction(final IExpr head, final IAST ast,
      Function<T, IExpr> function, Predicate<T> predicate) {
    IASTAppendable result = F.ast(head, ast.size());
    if (result.append(ast, function, predicate)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * 
   * @param head the head of the result ast
   * @param ast
   * @param start start of the range (inclusive) of elements which should be mapped
   * @param end end of the range (exclusive) of elements which should be mapped
   * @param function
   * @return
   */
  public static <T extends IExpr> IASTAppendable mapFunction(final IExpr head, final IAST ast,
      int start, int end, Function<T, IExpr> function) {
    IASTAppendable result = F.ast(head, end - start);
    if (result.append(ast, start, end, function)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * 
   * @param head the head of the result ast
   * @param ast
   * @param start start of the range (inclusive) of elements which should be mapped
   * @param end end of the range (exclusive) of elements which should be mapped
   * @param function
   * @param predicate
   * @return
   */
  public static <T extends IExpr> IASTAppendable mapFunction(final IExpr head, final IAST ast,
      int start, int end, Function<T, IExpr> function, Predicate<T> predicate) {
    IASTAppendable result = F.ast(head, end - start);
    if (result.append(ast, start, end, function, predicate)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Iterates over the lists elements and calls the function. Append the functions result expression
   * at the end of the result list, if the function results is not equal {@link F#NIL}. If the
   * function results is <code>null</code> stop iterating and return <code>false</code>.
   * 
   * @param ast
   * @param function
   * @return {@link F#NIL} if the function returns <code>null</code>
   */
  public static IASTAppendable mapFunction(final IExpr head, final IAST ast,
      ObjIntFunction<IExpr, IExpr> function) {
    IASTAppendable result = F.ast(head, ast.size());
    if (result.append(ast, function)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Iterates over the lists elements and calls the function. Append the functions result expression
   * at the end of the result list, if the function results is not equal {@link F#NIL}. If the
   * function results is <code>null</code> stop iterating and return <code>false</code>.
   * 
   * @param list
   * @param function
   * @return {@link F#NIL} if the function returns <code>null</code>
   */
  public static <T> IASTAppendable mapFunction(final IExpr head, final List<T> list,
      Function<T, IExpr> function) {
    IASTAppendable result = F.ast(head, list.size());
    if (result.append(list, function)) {
      return result;
    }
    return F.NIL;
  }

  public static <T> IASTAppendable mapFunction(final IExpr head, final List<T> list,
      Function<T, IExpr> function, Predicate<T> predicate) {
    IASTAppendable result = F.ast(head, list.size());
    if (result.append(list, function, predicate)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Iterates over the lists elements and calls the function. Append the functions result expression
   * at the end of the result list, if the function results is not equal {@link F#NIL}. If the
   * function results is <code>null</code> stop iterating and return <code>false</code>.
   * 
   * @param ast
   * @param function
   * @return {@link F#NIL} if the function returns <code>null</code>
   */
  public static <T extends IExpr> IASTAppendable mapList(final IAST ast,
      Function<T, IExpr> function) {
    IASTAppendable result = F.ast(S.List, ast.size());
    if (result.append(ast, function)) {
      return result;
    }
    return F.NIL;
  }

  public static <T extends IExpr> IASTAppendable mapList(final IAST ast,
      Function<T, IExpr> function, Predicate<T> predicate) {
    IASTAppendable result = F.ast(S.List, ast.size());
    if (result.append(ast, function, predicate)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Iterates over the lists elements and calls the function. Append the functions result expression
   * at the end of the result list, if the function results is not equal {@link F#NIL}. If the
   * function results is <code>null</code> stop iterating and return <code>false</code>.
   * 
   * @param ast
   * @param function
   * @return {@link F#NIL} if the function returns <code>null</code>
   */
  public static IASTAppendable mapList(final IAST ast, ObjIntFunction<IExpr, IExpr> function) {
    IASTAppendable result = F.ast(S.List, ast.size());
    if (result.append(ast, function)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Iterates over the lists elements and calls the function. Append the functions result expression
   * at the end of the result list, if the function results is not equal {@link F#NIL}. If the
   * function results is <code>null</code> stop iterating and return <code>false</code>.
   * 
   * @param list
   * @param function
   * @return {@link F#NIL} if the function returns <code>null</code>
   */
  public static <T extends IExpr> IASTAppendable mapList(final List<T> list,
      Function<T, IExpr> function) {
    IASTAppendable result = F.ast(S.List, list.size());
    if (result.append(list, function)) {
      return result;
    }
    return F.NIL;
  }

  public static IASTAppendable mapList(final List<IExpr> list, Function<IExpr, IExpr> function,
      Predicate<IExpr> predicate) {
    IASTAppendable result = F.ast(S.List, list.size());
    if (result.append(list, function, predicate)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Iterates over the set elements and calls the function. Append the functions result expression
   * at the end of the result list, if the function results is not equal {@link F#NIL}. If the
   * function results is <code>null</code> stop iterating and return <code>false</code>.
   * 
   * @param exprSet
   * @param function
   * @return {@link F#NIL} if the function returns <code>null</code>
   */
  public static IASTAppendable mapSet(final Set<? extends IExpr> exprSet,
      Function<IExpr, IExpr> function) {
    IASTAppendable result = F.ast(S.List, exprSet.size());
    if (result.append(exprSet, function)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Iterates over the maps (key, value) pairs and calls the function. Append the functions result
   * expression at the end of the result list, if the function results is not equal {@link F#NIL}.
   * If the function results is <code>null</code> stop iterating and return <code>false</code>.
   * 
   * @param map
   * @param biFunction
   * @return {@link F#NIL} if the biFunction returns <code>null</code>
   */
  public static IASTAppendable mapMap(final Map<? extends IExpr, ? extends IExpr> map,
      BiFunction<IExpr, IExpr, IExpr> biFunction) {
    IASTAppendable result = F.ast(S.List, map.size());
    if (result.append(map, biFunction)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Create an integer range between <code>iMin</code> (inclusive) and <code>iMax</code> (exclusive)
   * and call the function with the elements of the created range. Append the result of the
   * <code>function</code> to the returned result list.If it's equal {@link F#NIL} then don't
   * appending an entry. If it's equal <code>null</code> then return {@link F#NIL} for this method.
   * 
   * @param iMin minimum range limit (inclusive)
   * @param iMax maximum range limit (exclusive)
   * @param function function those <code>apply(x)</code> method will be called with each number in
   *        the range. If the <code>apply</code> method returns <code>null</code> then return
   *        {@link F#NIL}.
   * @return
   */
  public static <T extends IExpr> IASTAppendable mapRange(final int iMin, final int iMax,
      IntFunction<T> function) {
    IASTAppendable result = F.ListAlloc(iMax - iMin);
    if (result.append(iMin, iMax, function)) {
      return result;
    }
    return F.NIL;
  }

  /**
   * Create an integer range between <code>iMin</code> (inclusive) and <code>iMax</code> (exclusive)
   * and call the function with the elements of the created range. Append the result of the
   * <code>function</code> to the returned result <code>ast</code>. If it's equal {@link F#NIL} then
   * don't appending an entry. If it's equal <code>null</code> then return {@link F#NIL} for this
   * method.
   * 
   * @param head the new head of the result <code>ast</code>
   * @param iMin minimum range limit (inclusive)
   * @param iMax maximum range limit (exclusive)
   * @param function those <code>apply(x)</code> method will be called with each number in the
   *        range. If the <code>apply</code> method returns <code>null</code> then return
   *        {@link F#NIL}.
   * @return
   */
  public static <T extends IExpr> IASTAppendable mapRange(final IExpr head, final int iMin,
      final int iMax, IntFunction<T> function) {
    IASTAppendable result = ast(head, iMax - iMin);
    if (result.append(iMin, iMax, function)) {
      return result;
    }
    return F.NIL;
  }

  public static IAST Map(final IExpr f) {
    return new AST1(Map, f);
  }

  public static IAST Map(final IExpr f, final IExpr expr) {
    return new AST2(Map, f, expr);
  }

  public static IAST Map(final IExpr f, final IExpr expr, final IExpr levelspec) {
    return new AST3(Map, f, expr, levelspec);
  }

  public static IASTMutable MapApply(final IExpr f, final IExpr expr) {
    return new AST2(MapApply, f, expr);
  }

  public static IAST MapThread(final IExpr f, final IExpr expr) {
    return new AST2(MapThread, f, expr);
  }

  public static IAST MapAll(final IExpr a0) {
    return new AST1(MapAll, a0);
  }

  public static IAST MatchQ(final IExpr expr, final IExpr form) {
    return new AST2(MatchQ, expr, form);
  }

  public static IAST MathMLForm(final IExpr expr) {
    return new AST1(MathMLForm, expr);
  }

  public static IAST MatrixD(final IExpr expr, final IExpr x) {
    return new AST2(MatrixD, expr, x);
  }

  public static IAST MatrixExp(final IExpr a0) {
    return new AST1(MatrixExp, a0);
  }

  public static IAST MatrixLog(final IExpr a0) {
    return new AST1(MatrixLog, a0);
  }

  public static IAST MatrixForm(final IExpr a0) {
    return new AST1(MatrixForm, a0);
  }

  public static IAST MatrixPower(final IExpr matrix, final IExpr n) {
    return new AST2(MatrixPower, matrix, n);
  }

  public static IASTAppendable Max() {
    return ast(Max);
  }

  public static IAST Max(final IExpr a0) {
    return new AST1(Max, a0);
  }

  public static IAST Max(final IExpr a0, final IExpr a1) {
    return new AST2(Max, a0, a1);
  }

  public static IAST Max(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Max, a0, a1, a2);
  }

  public static IAST Max(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(Max, a0, a1, a2, a3);
  }

  public static IAST Max(final IExpr... args) {
    return new AST(Max, args);
  }

  public static IAST Maximize(final IExpr a0, final IExpr a1) {
    return new AST2(Maximize, a0, a1);
  }

  public static IAST Mean(final IExpr list) {
    return new AST1(Mean, list);
  }

  public static IAST MeanDeviation(final IExpr list) {
    return new AST1(MeanDeviation, list);
  }

  public static IAST Median(final IExpr list) {
    return new AST1(Median, list);
  }

  public static IAST MeijerG(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(MeijerG, a0, a1, a2);
  }

  /**
   * Returns {@link S#True} if <code>form</code> matches any element of <code>list</code>, or
   * {@link S#False} otherwise.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MemberQ.md">MemberQ</a>
   *
   * @param list
   * @param form
   * @return
   */
  public static IAST MemberQ(final IExpr list, final IExpr form) {
    return new B2.MemberQ(list, form);
  }

  public static IAST MessageName(final IExpr a0, final IExpr a1) {
    return new AST2(MessageName, a0, a1);
  }

  public static IAST MessageName(final ISymbol symbol, final String str) {
    return new AST2(MessageName, symbol, $str(str));
  }

  public static IASTAppendable Min() {
    return ast(Min);
  }

  public static IAST Min(final IExpr a0) {
    return new AST1(Min, a0);
  }

  public static IAST Min(final IExpr a0, final IExpr a1) {
    return new AST2(Min, a0, a1);
  }

  public static IAST Min(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Min, a0, a1, a2);
  }

  public static IAST Min(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(Min, a0, a1, a2, a3);
  }

  public static IAST Min(final IExpr... args) {
    return new AST(Min, args);
  }

  public static IAST Minimize(final IExpr a0, final IExpr a1) {
    return new AST2(Minimize, a0, a1);
  }

  public static IExpr minus(IExpr a, Integer i) {
    return Plus(ZZ(i.longValue() * (-1)), a);
  }

  public static IExpr minus(IExpr a, java.math.BigInteger i) {
    return Plus(ZZ(i.negate()), a);
  }

  public static IExpr minus(Integer i, IExpr b) {
    return Plus(ZZ(i.longValue()), new B2.Times(CN1, b));
  }

  public static IExpr minus(java.math.BigInteger i, IExpr b) {
    return Plus(ZZ(i), new B2.Times(CN1, b));
  }

  public static IAST Missing(final IExpr reason) {
    return new B1.Missing(reason);
  }

  public static IAST Missing(final String reason) {
    return new B1.Missing(stringx(reason));
  }

  public static IAST Missing(final IExpr a0, final IExpr a1) {
    return new AST2(Missing, a0, a1);
  }

  /**
   *
   *
   * <pre>
   * MissingQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>expr</code> is a <code>Missing()</code> expression.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; MissingQ(Missing("Test message"))
   * True
   * </pre>
   */
  public static IAST MissingQ(final IExpr a0) {
    return new AST1(MissingQ, a0);
  }

  public static IAST MoebiusMu(final IExpr a0) {
    return new AST1(MoebiusMu, a0);
  }

  public static IExpr mod(IExpr a, Integer i) {
    return Mod(a, ZZ(i.longValue()));
  }

  public static IExpr mod(IExpr a, java.math.BigInteger i) {
    return Mod(a, ZZ(i));
  }

  public static IExpr mod(Integer i, IExpr b) {
    return Mod(ZZ(i.longValue()), b);
  }

  public static IExpr mod(java.math.BigInteger i, IExpr b) {
    return Mod(ZZ(i), b);
  }

  public static IExpr Mod(final IExpr a0, final IExpr a1) {
    return new AST2(Mod, a0, a1);
  }

  /**
   * Evaluates <code>expr</code> for the <code>listOfLocalVariables</code> by first renaming the
   * local variables in <code>expr</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Module.md">Module</a>
   *
   * @param listOfLocalVariables
   * @param expr
   * @return
   */
  public static IAST Module(final IExpr listOfLocalVariables, final IExpr expr) {
    return new AST2(Module, listOfLocalVariables, expr);
  }

  public static IAST Most(final IExpr a0) {
    return new AST1(Most, a0);
  }

  public static IExpr multiply(IExpr a, Integer i) {
    return new B2.Times(ZZ(i.longValue()), a);
  }

  public static IExpr multiply(IExpr a, java.math.BigInteger i) {
    return new B2.Times(ZZ(i), a);
  }

  public static IExpr multiply(Integer i, IExpr b) {
    return new B2.Times(ZZ(i.longValue()), b);
  }

  public static IExpr multiply(java.math.BigInteger i, IExpr b) {
    return Times(ZZ(i), b);
  }

  public static IAST Multinomial(final IExpr... a) {
    return function(Multinomial, a);
  }

  public static IAST MultiplicativeOrder(final IExpr a0, final IExpr a1) {
    return new AST2(MultiplicativeOrder, a0, a1);
  }

  public static IASTMutable MultiplySides(final IExpr equationOrInequality, final IExpr x) {
    return new AST2(MultiplySides, equationOrInequality, x);
  }

  /**
   * Evaluate the given (symbolic) expression in numeric mode.
   *
   * @param symbolicExpr
   * @return
   */
  public static IAST N(final IExpr symbolicExpr) {
    return new AST1(N, symbolicExpr);
  }

  /**
   * Evaluate the given (symbolic) expression in numeric mode with <code>precision</code> digits .
   * 
   * @param symbolicExpr
   * @param precision
   * @return
   */
  public static IAST N(final IExpr symbolicExpr, final IExpr precision) {
    return new AST2(N, symbolicExpr, precision);
  }

  public static IAST N(final IExpr symbolicExpr, long precision) {
    return new AST2(N, symbolicExpr, F.ZZ(precision));
  }

  public static IAST NakagamiDistribution(final IExpr a0, final IExpr a1) {
    return new AST2(NakagamiDistribution, a0, a1);
  }

  public static IAST NameQ(final IExpr a0) {
    return new AST1(NameQ, a0);
  }

  public static IAST NameQ(final String str) {
    return new AST1(NameQ, F.stringx(str));
  }

  public static IAST Needs(final IExpr a0) {
    return new AST1(Needs, a0);
  }

  /**
   * Multiplies the given argument by <code>-1</code>. The <code>IExpr#negate()</code> method does
   * evaluations, which don't agree with pattern matching assumptions (in left-hand-sides
   * expressions). so it is only called for <code>INumber</code> objects, otherwise a <code>
   * Times(CN1, x)</code> AST would be created.
   *
   * @param x the expression which should be negated.
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
    return new B2.Times(CN1, x);
  }

  public static IAST Negative(final IExpr x) {
    return new AST1(Negative, x);
  }

  public static IAST Nest(final IExpr a0, final IExpr a1, final int n) {
    return Nest(a0, a1, ZZ(n));
  }

  public static IAST Nest(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Nest, a0, a1, a2);
  }

  /**
   * Create a new abstract syntax tree (AST).
   *
   * @param intialArgumentsCapacity the initial capacity of arguments of the AST.
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @return
   */
  public static IAST newInstance(final int intialArgumentsCapacity, final IExpr head) {
    return AST.newInstance(intialArgumentsCapacity, head, false);
  }

  public static IAST NIntegrate(final IExpr f, final IExpr x) {
    return new AST2(NIntegrate, f, x);
  }

  public static IAST NewLimit(final IExpr f, final IExpr rule) {
    return new AST2(NewLimit, f, rule);
  }

  public static IAST NewLimit(final IExpr f, final IExpr rule, final IExpr direction) {
    return new AST3(NewLimit, f, rule, direction);
  }

  public static IAST NMaximize(final IExpr a0, final IExpr a1) {
    return new AST2(NMaximize, a0, a1);
  }

  public static IAST NMinimize(final IExpr a0, final IExpr a1) {
    return new AST2(NMinimize, a0, a1);
  }

  public static IAST NonCommutativeMultiply(final IExpr... a) {
    switch (a.length) {
      case 1:
        return new AST1(NonCommutativeMultiply, a[0]);
      case 2:
        return new AST2(NonCommutativeMultiply, a[0], a[1]);
      case 3:
        return new AST3(NonCommutativeMultiply, a[0], a[1], a[2]);
      default:
        return new AST(NonCommutativeMultiply, a);
    }
  }

  public static IAST Norm(final IExpr a) {
    return new AST1(Norm, a);
  }

  public static IAST NormalDistribution() {
    return new AST0(NormalDistribution);
  }

  public static IAST NormalDistribution(final IExpr a0, final IExpr a1) {
    return new AST2(NormalDistribution, a0, a1);
  }

  public static IAST NSum(final IExpr f, final IExpr x) {
    return new AST2(NSum, f, x);
  }

  public static IAST ParetoDistribution(final IExpr a0, final IExpr a1) {
    return new AST2(ParetoDistribution, a0, a1);
  }

  public static IAST ParetoDistribution(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(ParetoDistribution, a0, a1, a2);
  }

  public static IAST ParetoDistribution(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return quaternary(ParetoDistribution, a0, a1, a2, a3);
  }

  public static IAST Normalize(final IExpr a) {
    return new AST1(Normalize, a);
  }

  /**
   * Logical Not function (negation). Returns {@link S#True} if <code>expr</code> is
   * {@link S#False}. Returns {@link S#False} if <code>expr</code> is {@link S#True}.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Not.md">Not</a>
   *
   * @param expr
   * @return
   */
  public static IAST Not(final IExpr expr) {
    return new B1.Not(expr);
  }

  public static IAST NotElement(final IExpr x, final IExpr domain) {
    return new AST2(NotElement, x, domain);
  }

  public static IAST NRoots(final IExpr a0, final IExpr a1) {
    return new AST2(NRoots, a0, a1);
  }

  public static IAST NullSpace(final IExpr a0) {
    return new AST1(NullSpace, a0);
  }

  /**
   * Return a {@link ApfloatNum} which wraps a {@link Apfloat} arbitrary precision floating-point
   * number.
   * 
   * @param af
   * @return
   */
  public static INum num(final Apfloat af) {
    return ApfloatNum.valueOf(af);
  }

  /**
   * Create a numeric value from the input string. If {@link EvalEngine#isArbitraryMode()} is
   * <code>true</code> return a {@link ApfloatNum} which wraps a {@link Apfloat} arbitrary precision
   * floating-point number with the engines precision.
   *
   * @param valueString the numeric value represented as a string.
   * @return
   */
  public static INum num(final String valueString) {
    EvalEngine engine = EvalEngine.get();
    if (engine.isArbitraryMode()) {
      return ApfloatNum.valueOf(valueString, engine.getNumericPrecision());
    }
    return Num.valueOf(Double.parseDouble(valueString));
  }

  /**
   * Return a {@link ApfloatNum} which wraps a {@link Apfloat} arbitrary precision floating-point
   * number.
   * 
   * @param numberStr
   * @param precision
   * @return a {@link ApfloatNum} which wraps a {@link Apfloat} Arbitrary precision floating-point
   *         number.
   */
  public static INum num(String numberStr, long precision) {
    return num(new Apfloat(numberStr, precision));
  }

  /**
   * Return a {@link Num} which wraps a Java double number.
   *
   * @param value
   * @return
   */
  public static Num num(final double value) {
    return Num.valueOf(value);
  }

  public static INum num(final IFraction value) {
    EvalEngine engine = EvalEngine.get();
    if (engine.isArbitraryMode()) {
      return ApfloatNum.valueOf(value.toBigNumerator(), value.toBigDenominator());
    }
    return Num.valueOf(value.doubleValue());
  }

  public static INum num(final IInteger value) {
    EvalEngine engine = EvalEngine.get();
    if (engine.isArbitraryMode()) {
      return ApfloatNum.valueOf(value.toBigNumerator());
    }
    return num(value.doubleValue());
  }

  /**
   *
   *
   * <pre>
   * NumberQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>expr</code> is an explicit number, and <code>False</code>
   * otherwise.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; NumberQ(3+I)
   * True
   *
   * &gt;&gt; NumberQ(5!)
   * True
   *
   * &gt;&gt; NumberQ(Pi)
   * False
   * </pre>
   */
  public static IAST NumberQ(final IExpr a0) {
    return new AST1(NumberQ, a0);
  }

  public static IAST Numerator(final IExpr expr) {
    return new AST1(Numerator, expr);
  }

  public static IAST NumericalOrder(final IExpr a0, final IExpr a1) {
    return new AST2(NumericalOrder, a0, a1);
  }

  public static IAST NumericalSort(final IExpr a0) {
    return new AST1(NumericalSort, a0);
  }

  /**
   *
   *
   * <pre>
   * NumericQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>expr</code> is an explicit numeric expression, and <code>
   * False</code> otherwise.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; NumericQ(E+Pi)
   * True
   *
   * &gt;&gt; NumericQ(Sqrt(3))
   * True
   * </pre>
   */
  public static IAST NumericQ(final IExpr a0) {
    return new AST1(NumericQ, a0);
  }

  public static IAST O(final IExpr a0) {
    return new AST1(O, a0);
  }

  /**
   *
   *
   * <pre>
   * OddQ(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>True</code> if <code>x</code> is odd, and <code>False</code> otherwise.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; OddQ(-3)
   * True
   *
   * &gt;&gt; OddQ(0)
   * False
   * </pre>
   */
  public static IAST OddQ(final IExpr x) {
    return new AST1(OddQ, x);
  }

  public static IAST On(final IExpr a0, final IExpr a1) {
    return new AST2(On, a0, a1);
  }

  public static IAST On(final IExpr a0) {
    return new AST1(On, a0);
  }

  public static IAST OptimizeExpression(final IExpr a0) {
    return new AST1(OptimizeExpression, a0);
  }

  public static IAST Optional(final IExpr a0, final IExpr a1) {
    return new AST2(Optional, a0, a1);
  }

  public static IAST Optional(final IExpr a0) {
    return new AST1(Optional, a0);
  }

  public static IAST Options(final IExpr a0) {
    return new AST1(Options, a0);
  }

  public static IAST OptionValue(final IExpr a0, final IExpr a1) {
    return new AST2(OptionValue, a0, a1);
  }

  public static IAST OptionValue(final IExpr a0) {
    return new AST1(OptionValue, a0);
  }

  public static IASTAppendable Or() {
    return ast(Or);
  }

  /**
   * <code>expr1 || expr2</code> evaluates each expression in turn, returning {@link S#True} as soon
   * as an expression evaluates to {@link S#True}. If all expressions evaluate to {@link S#False},
   * it returns {@link S#False}.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Or.md">Or</a>
   *
   * @param expr1
   * @param expr2
   * @return
   */
  public static IAST Or(final IExpr expr1, final IExpr expr2) {
    return new B2.Or(expr1, expr2);
  }

  /**
   * <code>expr1 || expr2 || expr3</code> evaluates each expression in turn, returning
   * {@link S#True} as soon as an expression evaluates to {@link S#True}. If all expressions
   * evaluate to {@link S#False}, it returns {@link S#False}.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Or.md">Or</a>
   *
   * @param expr1
   * @param expr2
   * @param expr3
   * @return
   */
  public static IAST Or(final IExpr expr1, final IExpr expr2, final IExpr expr3) {
    return new B3.Or(expr1, expr2, expr3);
  }

  public static IAST Or(final IExpr... expr) {
    return function(Or, expr);
  }

  public static IAST Order(final IExpr a0, final IExpr a1) {
    return new AST2(Order, a0, a1);
  }

  public static IAST Ordering(final IExpr a) {
    return new AST1(Ordering, a);
  }

  public static IAST OrderedQ(final IExpr a) {
    return new AST1(OrderedQ, a);
  }

  public static IAST Out(final IExpr a0) {
    return new AST1(Out, a0);
  }

  public static IAST Out(final int n) {
    return new AST1(Out, F.ZZ(n));
  }

  public static IAST Overflow() {
    return new AST0(Overflow);
  }

  public static IAST Parenthesis(final IExpr a0) {
    return new AST1(Parenthesis, a0);
  }

  /**
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part</a>
   */
  public static IASTAppendable Part() {
    return ast(Part);
  }

  /**
   * Returns part <code>i</code> of <code>expr</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part</a>
   *
   * @param expr
   * @param i
   * @return
   */
  public static IAST Part(final IExpr expr, final IExpr i) {
    return new B2.Part(expr, i);
  }

  /**
   * Returns the part <code>i, j</code> of <code>expr</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part</a>
   *
   * @param expr
   * @param i
   * @param j
   * @return
   */
  public static IAST Part(final IExpr expr, final IExpr i, final IExpr j) {
    return new B3.Part(expr, i, j);
  }

  /**
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part</a>
   */
  public static IASTAppendable Part(final IExpr... a) {
    return Part(0, a);
  }

  /**
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part</a>
   */
  public static IASTAppendable Part(final int extraSize, final IExpr... a) {
    IASTAppendable part = ast(Part, a.length + extraSize + 1);
    for (int i = 0; i < a.length; i++) {
      part.append(a[i]);
    }
    return part;
  }

  public static IAST PartitionsP(final IExpr a0) {
    return new AST1(PartitionsP, a0);
  }

  public static IAST PartitionsQ(final IExpr a0) {
    return new AST1(PartitionsQ, a0);
  }

  /**
   * Constrains <code>pattern</code> to match <code>expr</code> only if the evaluation of <code>
   * test(expr)</code> yields {@link S#True}.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PatternTest.md">PatternTest</a>
   *
   * @param pattern
   * @param test
   * @return
   */
  public static IAST PatternTest(final IExpr pattern, final IExpr test) {
    return new AST2(PatternTest, pattern, test);
  }

  /**
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PDF.md">PDF</a>
   */
  public static IAST PDF(final IExpr distribution) {
    return new AST1(PDF, distribution);
  }

  /**
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PDF.md">PDF</a>
   */
  public static IAST PDF(final IExpr distribution, final IExpr x) {
    return new AST2(PDF, distribution, x);
  }

  /**
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Piecewise.md">Piecewise</a>
   */
  public static IAST Piecewise(final IExpr listOfConditions) {
    return new AST1(Piecewise, listOfConditions);
  }

  /**
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Piecewise.md">Piecewise</a>
   */
  public static IAST Piecewise(final IExpr listOfConditions, final IExpr defaultValue) {
    return new AST2(Piecewise, listOfConditions, defaultValue);
  }

  /**
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PiecewiseExpand.md">PiecewiseExpand</a>
   */
  public static IAST PiecewiseExpand(final IExpr expr) {
    return new AST1(PiecewiseExpand, expr);
  }

  public static IAST Plot(final IExpr a0, final IExpr a1) {
    return new AST2(Plot, a0, a1);
  }

  public static IAST Plot(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Plot, a0, a1, a2);
  }

  public static IAST Plot3D(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Plot3D, a0, a1, a2);
  }

  public static IExpr plus(IExpr a, Integer i) {
    return Plus(a, ZZ(i.longValue()));
  }

  public static IExpr plus(IExpr a, java.math.BigInteger i) {
    return Plus(a, ZZ(i));
  }

  public static IExpr plus(Integer i, IExpr b) {
    return Plus(ZZ(i.longValue()), b);
  }

  public static IExpr plus(java.math.BigInteger i, IExpr b) {
    return Plus(ZZ(i), b);
  }

  /**
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
   */
  public static IASTAppendable Plus() {
    return ast(Plus);
  }

  /**
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
   *
   * @param initialCapacity the initialCapacity of this AST
   * @return
   */
  public static IASTAppendable PlusAlloc(int initialCapacity) {
    return ast(Plus, initialCapacity);
  }

  /**
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
   */
  public static IASTAppendable Plus(final IExpr a0) {
    return unary(Plus, a0);
  }

  /**
   * Define a <code>Plus()</code> expression <code>a0 + a1 + a2 ...</code> for addition.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
   */
  public static IAST Plus(final IExpr... a) {
    switch (a.length) {
      case 1:
        return new AST1(Plus, a[0]);
      case 2:
        return new B2.Plus(a[0], a[1]);
      case 3:
        return new AST3(Plus, a[0], a[1], a[2]);
      default:
        return new AST(Plus, a);
    }
  }

  public static IAST PlusMinus(final IExpr... a) {
    switch (a.length) {
      case 1:
        return new AST1(PlusMinus, a[0]);
      case 2:
        return new AST2(PlusMinus, a[0], a[1]);
      case 3:
        return new AST3(PlusMinus, a[0], a[1], a[2]);
      default:
        return new AST(PlusMinus, a);
    }
  }

  /**
   * Define a <code>Plus()</code> expression <code>x + y</code> for addition.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
   *
   * @param x
   * @param y
   * @return
   */
  public static IASTMutable Plus(final IExpr x, final IExpr y) {
    if (x != null && y != null) {
      return plusOrderless(IExpr::isPlus, x, y);
    }
    return new B2.Plus(x, y);
  }

  /**
   * Define a <code>Plus()</code> expression <code>x + y + z</code> for addition.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
   */
  public static IAST Plus(final IExpr x, final IExpr y, final IExpr z) {
    if (x != null && y != null && z != null) {
      return plusOrderless(IExpr::isPlus, x, y, z);
    }
    return new B3.Plus(x, y, z);
  }

  /**
   * Define a <code>Plus()</code> expression <code>num + a0 + a1 + a2 ...</code> for addition.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
   */
  public static IAST Plus(final long num, final IExpr... a) {
    IASTAppendable ast = ast(Plus, a.length + 1);
    ast.append(ZZ(num));
    ast.appendAll(a, 0, a.length);
    return ast;
  }

  public static IAST Pochhammer(final IExpr a0, final IExpr a1) {
    return new AST2(Pochhammer, a0, a1);
  }

  public static IAST Point(final IAST list) {
    return new B1.Point(list);
  }

  public static IAST PoissonDistribution(final IExpr a0) {
    return new AST1(PoissonDistribution, a0);
  }

  public static IAST PolyGamma(final IExpr a0) {
    return new AST1(PolyGamma, a0);
  }

  public static IAST PolyGamma(final IExpr a0, final IExpr a1) {
    return new AST2(PolyGamma, a0, a1);
  }

  public static IAST PolyGamma(final int n, final IExpr z) {
    return new AST2(PolyGamma, F.ZZ(n), z);
  }

  public static IAST Polygon(final IExpr a0) {
    return new AST1(Polygon, a0);
  }

  public static IAST PolygonalNumber(final IExpr a) {
    return new AST1(PolygonalNumber, a);
  }

  public static IAST PolyLog(final IExpr a0, final IExpr a1) {
    return new AST2(PolyLog, a0, a1);
  }

  public static IAST PolyLog(final IExpr n, final IExpr p, final IExpr z) {
    return new AST3(PolyLog, n, p, z);
  }

  public static IAST PolynomialGCD(final IExpr poly1, final IExpr poly2) {
    return new AST2(PolynomialGCD, poly1, poly2);
  }

  /**
   * Return {@link S#True} if <code>expr</code> is a polynomial for the <code>variable</code>.
   * Return {@link S#False} in all other cases.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQ.md">PolynomialQ</a>
   *
   * @param expr
   * @param variable
   * @return
   */
  public static IAST PolynomialQ(final IExpr expr, final IExpr variable) {
    return new B2.PolynomialQ(expr, variable);
  }

  public static IAST PolynomialQuotient(final IExpr poly1, final IExpr poly2, final IExpr x) {
    return new AST3(PolynomialQuotient, poly1, poly2, x);
  }

  public static IAST PolynomialQuotientRemainder(final IExpr poly1, final IExpr poly2,
      final IExpr x) {
    return new AST3(PolynomialQuotientRemainder, poly1, poly2, x);
  }

  public static IAST PolynomialRemainder(final IExpr poly1, final IExpr poly2, final IExpr x) {
    return new AST3(PolynomialRemainder, poly1, poly2, x);
  }

  public static IAST PointSize(final IExpr a0) {
    return new AST1(PointSize, a0);
  }

  public static IAST PointSize(final double value) {
    return new AST1(PointSize, F.num(value));
  }

  public static IAST Position(final IExpr a0, final IExpr a1) {
    return new AST2(Position, a0, a1);
  }

  public static IAST Positive(final IExpr a0) {
    return new AST1(Positive, a0);
  }

  public static IAST PossibleZeroQ(final IExpr a0) {
    return new AST1(PossibleZeroQ, a0);
  }

  /**
   * Define <code>base</code> to the power of <code>exponent</code>. Symja operator form:
   * <code>base ^ exponent</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Power.md">Power</a>
   *
   * @param base
   * @param exponent
   * @return
   */
  public static IASTMutable Power(final IExpr base, final IExpr exponent) {
    return new B2.Power(base, exponent);
  }

  /**
   * Define a power expression <code>base ^ exponent</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Power.md">Power</a>
   *
   * @param base
   * @param exponent
   * @return
   */
  public static IExpr Power(final IExpr base, final long exponent) {
    if (exponent == 1L) {
      return base;
    }
    if (base.isNumber()) {
      if (exponent > 0L) {
        return base.power(exponent);
      }
      if (exponent == -1L) {
        if (base.isZero()) {
          LOGGER.log(EvalEngine.get().getLogLevel(), "Infinite expression 0^(-1)");
          return CComplexInfinity;
        }
        return base.inverse();
      }
      if (exponent == 0L && !base.isZero()) {
        return C1;
      }
    }
    return new B2.Power(base, ZZ(exponent));
  }

  public static IAST PowerExpand(final IExpr a0) {

    return new AST1(PowerExpand, a0);
  }

  public static IAST PowerMod(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(PowerMod, a0, a1, a2);
  }

  /**
   * Create a "predefined" symbol for constants or function names.
   *
   * @param symbolName
   * @return
   */
  public static ISymbol predefinedSymbol(final String symbolName) {
    ISymbol temp = org.matheclipse.core.expression.Context.SYSTEM.get(symbolName);
    if (temp != null) {
      return temp;
    }
    String lcSymbolName = symbolName;
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      if (symbolName.length() > 1) {
        // use the lower case string here to use it as associated class
        // name
        // in package org.matheclipse.core.reflection.system
        lcSymbolName = symbolName.toLowerCase(Locale.ENGLISH);
      }
    }
    temp = new Symbol(lcSymbolName, org.matheclipse.core.expression.Context.SYSTEM);
    org.matheclipse.core.expression.Context.SYSTEM.put(lcSymbolName, temp);
    return temp;
  }

  public static IAST Prepend(final IExpr a0, final IExpr a1) {

    return new AST2(Prepend, a0, a1);
  }

  public static IAST Prime(final IExpr a0) {
    return new AST1(Prime, a0);
  }

  public static IAST PrimeOmega(final IExpr a0) {
    return new AST1(PrimeOmega, a0);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/PrimePi.md">PrimePi</a>
   */
  public static IAST PrimePi(final IExpr a0) {
    return new AST1(PrimePi, a0);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/PrimeQ.md">PrimeQ</a>
   */
  public static IAST PrimeQ(final IExpr a0) {
    return new AST1(PrimeQ, a0);
  }

  public static IAST Print(final IExpr... a) {
    return function(Print, a);
  }

  /**
   * @param expr expression which should be multiplied up
   * @param iterationSpecification a standard iteration specification
   * @return <code>Product(expr, iterationSpecification)</code> AST
   */
  public static IAST Product(final IExpr expr, final IExpr iterationSpecification) {
    return new AST2(Product, expr, iterationSpecification);
  }

  /**
   * Iterate over an integer range <code>from <= i <= to</code> and create a product of the created
   * values.
   *
   * @param function the function which should be applied on each iterator value
   * @param from
   * @param to
   * @return
   */
  public static IExpr product(final Function<IInteger, IExpr> function, final int from,
      final int to) {
    return intProduct(function, from, to, 1);
  }

  public static IAST ProductLog(final IExpr a0) {
    return new AST1(ProductLog, a0);
  }

  public static IAST ProductLog(final IExpr a0, final IExpr a1) {
    return new AST2(ProductLog, a0, a1);
  }

  public static IAST PseudoInverse(final IExpr a0) {
    return new AST1(PseudoInverse, a0);
  }

  /**
   * Create a "fractional" number from a Hipparchus {@link BigFraction} number.
   *
   * @param frac a big fractional number
   * @return IFraction
   */
  public static IFraction QQ(final BigFraction frac) {
    return AbstractFractionSym.valueOf(frac);
  }

  /**
   * Create a "fractional" number <code>numerator / denominator</code>
   *
   * @param numerator numerator of the fractional number
   * @param denominator denominator of the fractional number
   * @return IFraction
   */
  public static IFraction QQ(final IInteger numerator, final IInteger denominator) {
    return AbstractFractionSym.valueOf(numerator, denominator);
  }

  public static IFraction QQ(final BigInteger numerator, final BigInteger denominator) {
    return AbstractFractionSym.valueOf(numerator, denominator);
  }

  /**
   * Create a "fractional" number <code>numerator / denominator</code>
   *
   * @param numerator numerator of the fractional number
   * @param denominator denominator of the fractional number
   * @return IFraction
   */
  public static IFraction QQ(final long numerator, final long denominator) {
    return AbstractFractionSym.valueOf(numerator, denominator);
  }

  public static IAST QRDecomposition(final IExpr a0) {
    return new AST1(QRDecomposition, a0);
  }

  public static final IASTAppendable quaternary(final IExpr head, final IExpr a0, final IExpr a1,
      final IExpr a2, final IExpr a3) {
    return new AST(new IExpr[] {head, a0, a1, a2, a3});
  }

  /**
   * Returns the Quantile of the <code>distribution</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantile.md">Quantile</a>
   *
   * @param distribution
   * @return
   */
  public static IAST Quantile(final IExpr distribution) {
    return new AST1(Quantile, distribution);
  }

  /**
   * Returns the <code>q</code>-Quantile of <code>list</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantile.md">Quantile</a>
   *
   * @param list
   * @param q
   * @return
   */
  public static IAST Quantile(final IExpr list, final IExpr q) {
    return new AST2(Quantile, list, q);
  }

  /**
   * Returns the <code>q</code>-Quantile of <code>list</code> with the given quantile <code>
   * definition</code>. The default parameters for the quantile definition are <code>{{0,0},{1,0}}
   * </code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantile.md">Quantile</a>
   *
   * @param list
   * @param q
   * @partam definition the quantile definition
   * @return
   */
  public static IAST Quantile(final IExpr list, final IExpr q, final IExpr definition) {
    return new AST3(Quantile, list, q, definition);
  }

  /**
   * Returns the quantity for <code>magnitude</code> and <code>unit</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantity.md">Quantity</a>
   *
   * @param magnitude
   * @param unit
   * @return
   */
  public static IAST Quantity(final IExpr magnitude, final IExpr unit) {
    return new AST2(Quantity, magnitude, unit);
  }

  /**
   * Returns the value of the <code>quantity</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityMagnitude.md">QuantityMagnitude</a>
   *
   * @param quantity
   * @return
   */
  public static IAST QuantityMagnitude(final IExpr quantity) {
    return new AST1(QuantityMagnitude, quantity);
  }

  /**
   * Returns the value of the <code>quantity</code> for the given <code>unit</code>
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityMagnitude.md">QuantityMagnitude</a>
   *
   * @param quantity
   * @param unit
   * @return
   */
  public static IAST QuantityMagnitude(final IExpr quantity, final IExpr unit) {
    return new AST2(QuantityMagnitude, quantity, unit);
  }

  public static IAST Quartiles(final IExpr a0) {
    return new AST1(Quartiles, a0);
  }

  public static IAST Quiet(final IExpr a0) {
    return new AST1(Quiet, a0);
  }

  public static final IASTMutable quinary(final IExpr head, final IExpr a0, final IExpr a1,
      final IExpr a2, final IExpr a3, final IExpr a4) {
    return new AST(new IExpr[] {head, a0, a1, a2, a3, a4});
  }

  public static IAST Quotient(final IExpr a0, final IExpr a1) {
    return new AST2(Quotient, a0, a1);
  }

  public static IAST Quotient(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Quotient, a0, a1, a2);
  }

  public static IAST RandomComplex() {
    return new AST0(RandomComplex);
  }

  public static IAST RandomComplex(final IExpr a0) {
    return new AST1(RandomComplex, a0);
  }

  public static IAST RandomInteger() {
    return new AST0(RandomInteger);
  }

  public static IAST RandomInteger(final IExpr a0) {
    return new AST1(RandomInteger, a0);
  }

  public static IAST RandomReal() {
    return new AST0(RandomReal);
  }

  public static IAST RandomReal(final IExpr a0) {
    return new AST1(RandomReal, a0);
  }

  public static IAST RandomVariate(final IExpr a0) {
    return new AST1(RandomVariate, a0);
  }

  public static IAST RandomVariate(final IExpr a0, final IExpr a1) {
    return new AST2(RandomVariate, a0, a1);
  }

  public static IAST Range(final IExpr a0) {
    return new AST1(Range, a0);
  }

  public static IAST Range(final IExpr a0, final IExpr a1) {
    return new AST2(Range, a0, a1);
  }

  public static IAST Range(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Range, a0, a1, a2);
  }

  public static IAST Rational(final IExpr a0, final IExpr a1) {
    return new AST2(Rational, a0, a1);
  }

  public static IAST Rationalize(final IExpr a0) {
    return new AST1(Rationalize, a0);
  }

  public static IAST Rationalize(final IExpr a0, final IExpr a1) {
    return new AST2(Rationalize, a0, a1);
  }

  public static IAST Rectangle(final IAST originList) {
    return new AST1(Rectangle, originList);
  }

  /**
   * Performs a reduction on the elements of this function range, using the provided identity value
   * and an associative accumulation function, and returns the reduced value. This is equivalent to:
   * 
   * <pre>
   * IExpr result = identity;
   * for (int i = startInclusive; i < endExclusive; i++) {
   *   IExpr element = ast.getRule(i);
   *   result = accumulator.apply(result, element);
   * }
   * return result;
   * </pre>
   * 
   * @param ast
   * @param startInclusive the first index to cover, inclusive
   * @param endExclusive index immediately past the last index to cover
   * @param identity the identity value for the accumulating function
   * @param accumulator an associative, non-interfering, stateless function for combining two values
   * @return
   */
  public static IExpr reduce(final IAST ast, int startInclusive, int endExclusive, IExpr identity,
      BinaryOperator<IExpr> accumulator) {
    return ast.stream(startInclusive, endExclusive).reduce(identity, accumulator);
  }

  public static IExpr Re(final IExpr a0) {
    if (a0 != null && a0.isNumber()) {
      return ((INumber) a0).re();
    }
    return new B1.Re(a0);
  }

  public static IAST RealAbs(final IExpr a) {
    return new AST1(RealAbs, a);
  }

  public static IAST RealValuedNumberQ(final IExpr a) {
    return new AST1(RealValuedNumberQ, a);
  }

  public static IAST RealValuedNumericQ(final IExpr a) {
    return new AST1(RealValuedNumericQ, a);
  }

  public static IAST RealSign(final IExpr a) {
    return new AST1(RealSign, a);
  }

  public static IAST Reap(final IExpr a) {
    return new AST1(Reap, a);
  }

  /**
   * Reduce(logic-expression, var) - returns the reduced `logic-expression` for the variable `var`.
   * Reduce works only for the `Reals` domain.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reduce.md">Reduce
   *      documentation</a>
   */
  public static IAST Reduce(final IExpr logicExpr, final IExpr variable) {
    return new AST2(Reduce, logicExpr, variable);
  }

  public static IAST Refine(final IExpr a) {
    return new AST1(Refine, a);
  }

  public static IAST Refine(final IExpr a0, final IExpr a1) {
    return new AST2(Refine, a0, a1);
  }

  public static IAST RegularExpression(final IExpr a0) {
    return new AST1(RegularExpression, a0);
  }

  public static IAST RegularExpression(final String str) {
    return new AST1(RegularExpression, $str(str));
  }

  public static IAST Replace(final IExpr a0, final IExpr a1) {
    return new AST2(Replace, a0, a1);
  }

  public static IAST ReplaceAll(final IExpr a0, final IExpr a1) {
    return new AST2(ReplaceAll, a0, a1);
  }

  public static IAST ReplaceList(final IExpr a0, final IExpr a1) {
    return new AST2(ReplaceList, a0, a1);
  }

  public static IAST ReplacePart(final IExpr a0, final IExpr a1) {
    return new AST2(ReplacePart, a0, a1);
  }

  public static IAST ReplacePart(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(ReplacePart, a0, a1, a2);
  }

  public static IAST ReplaceRepeated(final IExpr a0, final IExpr a1) {
    return new AST2(ReplaceRepeated, a0, a1);
  }

  public static IAST Rest(final IExpr a0) {
    return new AST1(Rest, a0);
  }

  public static IAST Resultant(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Resultant, a0, a1, a2);
  }

  public static IAST RGBColor(final IExpr red, final IExpr green, final IExpr blue) {
    return new AST3(RGBColor, red, green, blue);
  }

  public static IAST RGBColor(final double red, final double green, final double blue) {
    return new AST3(RGBColor, num(red), num(green), num(blue));
  }


  /**
   * Get or create a user defined symbol which is retrieved from the evaluation engines context
   * path.
   *
   * @param symbolName the name of the symbol
   * @return the symbol object from the context path
   */
  public static ISymbol symbol(final String symbolName) {
    return symbol(symbolName, null, EvalEngine.get());
  }

  /**
   * Get or create a user defined symbol which is retrieved from the evaluation engines context
   * path.
   *
   * @param symbolName the name of the symbol
   * @param engine the evaluation engine
   * @return the symbol object from the context path
   */
  public static ISymbol symbol(final String symbolName, EvalEngine engine) {
    return symbol(symbolName, null, engine);
  }

  /**
   * Get or create a user defined symbol which is retrieved from the evaluation engines context
   * path. Additional set assumptions to the engines global assumptions. Use <code>#1</code> or
   * {@link F#Slot1} in the <code>assumptionAST</code> expression for this symbol.
   *
   * @param symbolName the name of the symbol
   * @param assumptionAST the assumptions which should be set for the symbol. Use <code>#1</code> or
   *        {@link F#Slot1} in the <code>assumptionAST</code> expression for this symbol.
   * @return the symbol object from the context path
   */
  public static ISymbol symbol(final String symbolName, IAST assumptionAST) {
    return symbol(symbolName, assumptionAST, EvalEngine.get());
  }

  /**
   * Get or create a user defined symbol which is retrieved from the evaluation engines context
   * path. Additional set assumptions to the engines global assumptions. Use <code>#1</code> or
   * {@link F#Slot1} in the <code>assumptionAST</code> expression for this symbol.
   *
   * @param symbolName the name of the symbol
   * @param assumptionAST the assumptions which should be set for the symbol. Use <code>#1</code> or
   *        {@link F#Slot1} in the <code>assumptionAST</code> expression for this symbol.
   * @param engine the evaluation engine
   * @return the symbol object from the context path
   */
  public static ISymbol symbol(final String symbolName, IAST assumptionAST, EvalEngine engine) {
    ISymbol symbol =
        engine.getContextPath().symbol(symbolName, engine.getContext(), engine.isRelaxedSyntax());
    if (assumptionAST != null) {
      IExpr temp = Lambda.replaceSlots(assumptionAST, List(symbol)).orElse(assumptionAST);
      if (temp.isAST()) {
        IAssumptions assumptions = engine.getAssumptions();
        if (assumptions == null) {
          assumptions = org.matheclipse.core.eval.util.Assumptions.getInstance(temp);
          engine.setAssumptions(assumptions);
        } else {
          assumptions.addAssumption(temp);
        }
      }
    }
    return symbol;
  }

  /**
   * Test if the <code>symbolName</code> is defined in the one of the contexts available on the
   * context path.
   *
   * @param symbolName the name of a symbol
   * @param engine
   * @return
   */
  public static boolean hasSymbol(final String symbolName, EvalEngine engine) {
    return engine.getContextPath().hasSymbol(symbolName, engine.isRelaxedSyntax());
  }

  public static ISymbol symbol(final String symbolName, final String contextStr, IAST assumptionAST,
      EvalEngine engine) {
    if (contextStr.length() == 0) {
      return symbol(symbolName, assumptionAST, engine);
    }
    ISymbol symbol;
    ContextPath contextPath = engine.getContextPath();
    Context context = contextPath.getContext(contextStr);
    // if (context == null) {
    // contextPath.add(new Context(contextStr));
    // }
    symbol = ContextPath.getSymbol(symbolName, context, engine.isRelaxedSyntax());
    if (assumptionAST != null) {
      IExpr temp = Lambda.replaceSlots(assumptionAST, List(symbol)).orElse(assumptionAST);
      if (temp.isAST()) {
        IAssumptions assumptions = engine.getAssumptions();
        if (assumptions == null) {
          assumptions = org.matheclipse.core.eval.util.Assumptions.getInstance(temp);
          engine.setAssumptions(assumptions);
        } else {
          assumptions.addAssumption(temp);
        }
      }
    }
    return symbol;
  }

  public static ISymbol symbol(final Context context, final String symbolName, EvalEngine engine) {
    // ContextPath contextPath = engine.getContextPath();
    return ContextPath.getSymbol(symbolName, context, engine.isRelaxedSyntax());
  }

  /**
   * Print the documentation for the given symbol.
   *
   * @param head
   * @return
   */
  public static final String usage(final ISymbol head) {
    return usage(head.toString());
  }

  /**
   * Print the documentation for the given symbol name.
   *
   * @param symbolName
   * @return
   */
  public static final String usage(final String symbolName) {
    StringBuilder buf = new StringBuilder();
    Documentation.usageDocumentation(buf, symbolName);
    return buf.toString();
  }

  /**
   * <p>
   * Create a unique dummy symbol which is retrieved from the evaluation engines
   * {@link Context#DUMMY} context.
   * 
   * <p>
   * <b>Note:</b> a &quot;Dummy&quot; symbol will not be found by the expression parser.
   *
   * @param symbolName the name of the symbol
   * @return the symbol object from the {@link Context#DUMMY} context path
   * @see #symbol(String)
   */
  public static ISymbol Dummy(final char symbolName) {
    return Dummy(new String("" + symbolName));
  }

  /**
   * <p>
   * Create a unique dummy symbol which is retrieved from the evaluation engines
   * {@link Context#DUMMY} context.
   * 
   * <p>
   * <b>Note:</b> a &quot;Dummy&quot; symbol will not be found by the expression parser.
   *
   * @param symbolName the name of the symbol
   * @return the symbol object from the {@link Context#DUMMY} context path
   * @see #symbol(String)
   */
  public static ISymbol Dummy(final String symbolName) {
    return Dummy(symbolName, null);
  }

  /**
   * <p>
   * Create a unique dummy symbol which is retrieved from the evaluation engines
   * {@link Context#DUMMY} context.
   * 
   * <p>
   * <b>Note:</b> a &quot;Dummy&quot; symbol will not be found by the expression parser.
   *
   * @param symbolName the name of the symbol
   * @param assumptionAST the assumptions which should be set for the symbol. Use <code>#1</code> or
   *        {@link F#Slot1} in the <code>assumptionAST</code> expression for this symbol.
   * @return the symbol object from the {@link Context#DUMMY} context path
   * @see #symbol(String)
   */
  public static ISymbol Dummy(final String symbolName, IAST assumptionAST) {
    String name = symbolName;
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      if (symbolName.length() != 1) {
        name = symbolName.toLowerCase(Locale.ENGLISH);
      }
    }
    ISymbol symbol = new Symbol(name, org.matheclipse.core.expression.Context.DUMMY);
    if (assumptionAST != null) {
      IExpr temp = Lambda.replaceSlots(assumptionAST, List(symbol)).orElse(assumptionAST);
      if (temp.isAST()) {
        EvalEngine engine = EvalEngine.get();
        IAssumptions assumptions = engine.getAssumptions();
        if (assumptions == null) {
          assumptions = org.matheclipse.core.eval.util.Assumptions.getInstance(temp);
          engine.setAssumptions(assumptions);
        } else {
          assumptions.addAssumption(temp);
        }
      }
    }
    return symbol;
  }

  /**
   * <p>
   * Create a unique dummy symbol with prefix "$", which is retrieved from the evaluation engines
   * DUMMY context.
   * <p>
   * <b>Note:</b> a &quot;Dummy&quot; symbol will not be found by the expression parser.
   *
   *
   * @return the symbol object from the context path
   */
  public static ISymbol Dummy() {
    return Dummy(EvalEngine.uniqueName("$"));
  }

  public static IBuiltInSymbol localBiFunction(String symbolName, BinaryOperator<IExpr> function) {
    return localFunction(symbolName, new AbstractCoreFunctionEvaluator() {
      @Override
      public IExpr evaluate(IAST ast, EvalEngine engine) {
        return function.apply(ast.arg1(), ast.arg2());
      }
    });
  }

  public static IBuiltInSymbol localFunction(String symbolName, UnaryOperator<IExpr> function) {
    return localFunction(symbolName, new AbstractCoreFunctionEvaluator() {
      @Override
      public IExpr evaluate(IAST ast, EvalEngine engine) {
        return function.apply(ast.arg1());
      }
    });
  }

  public static IBuiltInSymbol localFunction(String symbolName, IEvaluator evaluator) {
    IBuiltInSymbol localBuiltIn = new BuiltInDummy(symbolName);
    localBuiltIn.setEvaluator(evaluator);
    return localBuiltIn;
  }

  public static IBuiltInSymbol localBiPredicate(String symbolName,
      BiPredicate<IExpr, IExpr> function) {
    return localFunction(symbolName, new AbstractCoreFunctionEvaluator() {
      @Override
      public IExpr evaluate(IAST ast, EvalEngine engine) {
        return booleSymbol(function.test(ast.arg1(), ast.arg2()));
      }
    });
  }

  public static IBuiltInSymbol localPredicate(String symbolName, Predicate<IExpr> function) {
    return localFunction(symbolName, new AbstractCoreFunctionEvaluator() {
      @Override
      public IExpr evaluate(IAST ast, EvalEngine engine) {
        return booleSymbol(function.test(ast.arg1()));
      }
    });
  }

  /**
   * Remove a user-defined symbol from the eval engines context path. Doesn't remove predefined
   * names from the System Context.
   *
   * @param symbolName the name of the symbol
   * @return the removed symbol or <code>null</code> if no symbol was found
   */
  public static ISymbol removeUserSymbol(final String symbolName) {
    ContextPath contextPath = EvalEngine.get().getContextPath();
    return contextPath.removeSymbol(symbolName);
  }

  public static IAST ReleaseHold(final IExpr z) {
    return new AST1(ReleaseHold, z);
  }

  public static IAST Return(final IExpr a) {
    if (a.isFalse()) {
      return CReturnFalse;
    }
    if (a.isTrue()) {
      return CReturnTrue;
    }
    return new AST1(Return, a);
  }

  public static IAST Reverse(final IExpr a) {
    return new AST1(Reverse, a);
  }

  public static IAST ReverseSort(final IExpr a) {
    return new AST1(ReverseSort, a);
  }

  public static IAST RomanNumeral(final IExpr a) {
    return new AST1(RomanNumeral, a);
  }

  public static IAST Root(final IExpr function, final IExpr k) {
    return new AST2(Root, function, k);
  }

  public static IAST Root(final IExpr function, final int k) {
    return new AST2(Root, function, F.ZZ(k));
  }

  public static IAST Roots(final IExpr a0) {
    return new AST1(Roots, a0);
  }

  public static IAST Roots(final IExpr a0, final IExpr a1) {
    return new AST2(Roots, a0, a1);
  }

  public static IAST Round(final IExpr x) {
    return new AST1(Round, x);
  }

  public static IAST RotationTransform(final IExpr x) {
    return new AST1(RotationTransform, x);
  }

  public static IAST Row(final IAST list, String separator) {
    return new AST2(Row, list, F.stringx(separator));
  }

  public static IAST RowBox(final IAST list) {
    return new AST1(RowBox, list);
  }

  public static IAST RowReduce(final IExpr m) {
    return new AST1(RowReduce, m);
  }

  /**
   * Represents a rule replacing <code>lhsStr</code> with <code>rhs</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rule.md">Rule</a>
   *
   * @param lhsStr
   * @param rhs
   * @return
   */
  public static IAST Rule(final String lhsStr, final IExpr rhs) {
    return new B2.Rule($str(lhsStr), rhs);
  }

  /**
   * Represents a rule replacing <code>lhsStr</code> with <code>rhsStr</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rule.md">Rule</a>
   *
   * @param lhsStr
   * @param rhsStr
   * @return
   */
  public static IAST Rule(final String lhsStr, final String rhsStr) {
    return new B2.Rule($str(lhsStr), $str(rhsStr));
  }

  /**
   * Represents a rule replacing <code>lhs</code> with <code>rhs</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rule.md">Rule</a>
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public static IAST Rule(final IExpr lhs, final IExpr rhs) {
    return new B2.Rule(lhs, rhs);
  }

  /**
   * Represents a rule replacing <code>lhs</code> with <code>rhsStr</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rule.md">Rule</a>
   *
   * @param lhs
   * @param rhsStr
   * @return
   */
  public static IAST Rule(final IExpr lhs, final String rhsStr) {
    return new B2.Rule(lhs, $str(rhsStr));
  }

  /**
   * Represents a rule replacing <code>lhs</code> with <code>rhs</code>, with <code>rhs</code> held
   * unevaluated.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RuleDelayed.md">RuleDelayed</a>
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public static IAST RuleDelayed(final IExpr lhs, final IExpr rhs) {
    return new B2.RuleDelayed(lhs, rhs);
  }

  /**
   * Returns {@link S#True} if <code>lhs</code> and <code>rhs</code> are structurally identical.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SameQ.md">SameQ</a>
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public static IAST SameQ(final IExpr lhs, final IExpr rhs) {
    return new B2.SameQ(lhs, rhs);
  }

  public static IAST SatisfiabilityInstances(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(SatisfiabilityInstances, a0, a1, a2);
  }

  public static IAST SameQ(final IExpr a0, final double d) {
    return new AST2(SameQ, a0, num(d));
  }

  public static IAST ScalingTransform(final IExpr z) {
    return new AST1(ScalingTransform, z);
  }

  public static IAST Scan(final IExpr a0, final IExpr a1) {
    return new AST2(Scan, a0, a1);
  }

  public static IAST Sec(final IExpr z) {
    return new AST1(Sec, z);
  }

  public static IAST Sech(final IExpr z) {
    return new AST1(Sech, z);
  }

  public static IAST Select(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Select, a0, a1, a2);
  }

  public static IAST Select(final IExpr a0, final IExpr a1) {
    return new AST2(Select, a0, a1);
  }

  public static final IAST senary(final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3, final IExpr a4, final IExpr a5) {
    return new AST(new IExpr[] {head, a0, a1, a2, a3, a4, a5});
  }

  public static IASTAppendable Sequence() {
    return ast(Sequence);
  }

  public static IAST Sequence(final IExpr a0) {
    return unary(Sequence, a0);
  }

  public static IAST Sequence(final IExpr... a) {
    return function(Sequence, a);
  }

  public static IASTMutable Sequence(final int... numbers) {
    IInteger[] a = new IInteger[numbers.length];
    for (int i = 0; i < numbers.length; i++) {
      a[i] = ZZ(numbers[i]);
    }
    return function(Sequence, a);
  }

  public static IAST Series(final IExpr... a) {
    return function(Series, a);
  }

  public static IAST SeriesCoefficient(final IExpr a0, final IExpr a1) {
    return new AST2(SeriesCoefficient, a0, a1);
  }

  public static IAST SeriesData(final IExpr... a) {
    return function(SeriesData, a);
  }

  /**
   * Evaluates <code>rhs</code> and assigns it to <code>lhs</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Set.md">Set</a>
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public static IAST Set(final IExpr lhs, final IExpr rhs) {
    return new B2.B2Set(lhs, rhs);
  }

  public static IAST SetAttributes(final IExpr symbol, final IExpr attribute) {
    return new AST2(SetAttributes, symbol, attribute);
  }

  /**
   * Assigns <code>rhs</code> to <code>lhs</code>, without evaluating <code>rhs</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SetDelayed.md">SetDelayed</a>
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public static IAST SetDelayed(final IExpr lhs, final IExpr rhs) {
    return new AST2(SetDelayed, lhs, rhs);
  }

  public static IAST Show(final IExpr a0) {
    return new AST1(Show, a0);
  }

  public static IAST Sign(final IExpr z) {
    return new AST1(Sign, z);
  }

  public static IAST Signature(final IExpr a) {
    return new AST1(Signature, a);
  }

  public static IAST SignCmp(final IExpr z) {
    return new AST1(SignCmp, z);
  }

  public static IAST Simplify(final IExpr expr) {
    return new AST1(Simplify, expr);
  }

  public static IAST Simplify(final IExpr expr, final IExpr assum) {
    return new AST2(Simplify, expr, assum);
  }

  /**
   * Returns the sine of <code>z</code> (measured in
   * <a href="https://en.wikipedia.org/wiki/Radian">Radians</a>).
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sin.md">Sin</a>
   *
   * @param z
   * @return
   */
  public static IAST Sin(final IExpr z) {
    return new B1.Sin(z);
  }

  public static IAST Sinc(final IExpr z) {
    return new AST1(Sinc, z);
  }

  public static IAST Sinh(final IExpr z) {

    return new AST1(Sinh, z);
  }

  public static IAST SinhIntegral(final IExpr z) {
    return new AST1(SinhIntegral, z);
  }

  public static IAST SinIntegral(final IExpr z) {
    return new AST1(SinIntegral, z);
  }

  public static IAST Skewness(final IExpr a0) {
    return new AST1(Skewness, a0);
  }

  public static IAST Slot(final IExpr a0) {
    return new AST1(Slot, a0);
  }

  public static IAST Slot(final int i) {
    if (i < SLOT_CACHE.length && i >= 0) {
      return SLOT_CACHE[i];
    }
    return new B1.Slot(ZZ(i));
  }

  public static IAST Slot(final String str) {
    return new B1.Slot(stringx(str));
  }

  public static IAST SlotSequence(final int i) {
    return new AST1(SlotSequence, ZZ(i));
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Solve.md">Solve</a>
   */
  public static IAST Solve(final IExpr a0, final IExpr a1) {
    return new AST2(Solve, a0, a1);
  }

  public static IAST Solve(final IExpr a0, final IExpr a1, IAST options) {
    return new AST3(Solve, a0, a1, options);
  }

  /**
   * Solve an equation for a single variable.
   *
   * <p>
   * Solve <code>100-x==0</code> for variable <code>x</code>
   *
   * <pre>
   * ISymbol x = F.Dummy(engine);
   * IExpr[] solutions = F.solve(F.Equal(F.Subtract(F.ZZ(100), x), F.C0), x);
   * </pre>
   *
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Solve.md">Solve</a>
   *
   * @param equations one single equation or a list of equations.
   * @param variable
   * @return
   */
  public static IExpr[] solve(final IAST equations, final ISymbol variable) {
    IExpr solve = Solve.of(equations, variable);
    if (!solve.isListOfLists()) {
      return new IExpr[0];
    }
    IExpr[] result = new IExpr[solve.size() - 1];
    int j = 0;
    for (int i = 1; i < solve.size(); i++) {
      IAST listRule = (IAST) solve.getAt(i);
      if (listRule.first().isRule()) {
        IAST rule = (IAST) listRule.first();
        result[j++] = rule.second();
      }
    }
    if (j < solve.size() - 1) {
      IExpr[] newResult = new IExpr[j];
      System.arraycopy(result, 0, newResult, 0, j);
      return newResult;
    }
    return result;
  }

  public static IAST Sort(final IExpr a0) {
    return new AST1(Sort, a0);
  }

  public static IAST Sort(final IExpr a0, final IExpr a1) {
    return new AST2(Sort, a0, a1);
  }

  public static IAST Sow(final IExpr a) {
    return new AST1(Sow, a);
  }

  public static IAST Span(final IExpr... a) {
    return function(Span, a);
  }

  public static IAST Sphere(final IExpr vector, final IExpr radius) {
    return new AST2(Sphere, vector, radius);
  }

  public static IAST SphericalBesselJ(final IExpr n, final IExpr z) {
    return new AST2(SphericalBesselJ, n, z);
  }

  public static IAST SphericalBesselY(final IExpr n, final IExpr z) {
    return new AST2(SphericalBesselY, n, z);
  }

  public static IAST SphericalHankelH1(final IExpr n, final IExpr z) {
    return new AST2(SphericalHankelH1, n, z);
  }

  public static IAST SphericalHankelH2(final IExpr n, final IExpr z) {
    return new AST2(SphericalHankelH2, n, z);
  }

  public static IAST SphericalHarmonicY(final IExpr a0, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    return function(SphericalHarmonicY, a0, a1, a2, a3);
  }

  /**
   * Create a &quot;square&quot; expression: <code>Power(x, 2)</code>.
   *
   * @param x
   * @return
   */
  public static IAST Sqr(final IExpr x) {
    return new B2.Power(x, C2);
  }

  /**
   * Create a &quot;square root&quot; expression: <code>Power(x, 1/2)</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sqrt.md">Sqrt</a>
   *
   * @param x
   * @return
   */
  public static IAST Sqrt(final IExpr x) {
    return new B2.Power(x, C1D2);
  }

  /**
   * Create a &quot;square root&quot; expression: <code>Power(n, 1/2)</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sqrt.md">Sqrt</a>
   *
   * @param n
   * @return
   */
  public static IAST Sqrt(int n) {
    return new B2.Power(ZZ(n), C1D2);
  }

  public static IAST StandardDeviation(final IExpr a0) {
    return new AST1(StandardDeviation, a0);
  }

  public static IAST Standardize(final IExpr a0) {
    return new AST1(Standardize, a0);
  }

  public static IAST Standardize(final IExpr a0, final IExpr a1) {
    return new AST2(Standardize, a0, a1);
  }

  public static IAST Standardize(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Standardize, a0, a1, a2);
  }

  public static IAST StieltjesGamma(final IExpr a0) {
    return new AST1(StieltjesGamma, a0);
  }

  public static IAST StieltjesGamma(final IExpr a0, final IExpr a1) {
    return new AST2(StieltjesGamma, a0, a1);
  }

  public static IAST StirlingS1(final IExpr n, final IExpr m) {
    return new AST2(StirlingS1, n, m);
  }

  public static IAST StirlingS2(final IExpr n, final IExpr m) {
    return new AST2(StirlingS2, n, m);
  }

  public static IAST StringJoin(final IExpr a) {
    return new AST1(StringJoin, a);
  }

  public static IAST Surd(final IExpr a0, final IExpr a1) {
    return new AST2(Surd, a0, a1);
  }

  /**
   * Create a Symja string expression with mime type TEXT_PLAIN.
   *
   * @param c
   * @return
   * @see IStringX#TEXT_PLAIN
   */
  public static final IStringX stringx(final char c) {
    return StringX.valueOf(c);
  }

  /**
   * Create a Symja string expression with mime type TEXT_PLAIN.
   *
   * @param str
   * @return
   * @see IStringX#TEXT_PLAIN
   */
  public static final IStringX stringx(final String str) {
    return StringX.valueOf(str);
  }

  /**
   * reate a Symja string expression.
   *
   * @param str
   * @param mimeType the mime type of the string
   * @return
   * @see IStringX#TEXT_PLAIN
   * @see IStringX#TEXT_LATEX
   * @see IStringX#TEXT_MATHML
   * @see IStringX#TEXT_HTML
   */
  public static final IStringX stringx(final String str, final short mimeType) {
    return StringX.valueOf(str, mimeType);
  }

  /**
   * Create a string expression
   *
   * @param str
   * @return
   */
  public static final IStringX stringx(final StringBuilder str) {
    return StringX.valueOf(str);
  }

  public static IAST StruveH(final IExpr a0, final IExpr a1) {
    return new AST2(StruveH, a0, a1);
  }

  public static IAST StruveL(final IExpr a0, final IExpr a1) {
    return new AST2(StruveL, a0, a1);
  }

  public static IAST StudentTDistribution(final IExpr a0) {
    return new AST1(StudentTDistribution, a0);
  }

  public static IAST StudentTDistribution(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(StudentTDistribution, a0, a1, a2);
  }

  public static IAST Style(final IExpr a0, final IExpr a1) {
    return new AST2(Style, a0, a1);
  }

  public static IAST Subdivide(final IExpr a0) {
    return new AST1(Subdivide, a0);
  }

  public static IAST Subdivide(final IExpr a0, final IExpr a1) {
    return new AST2(Subdivide, a0, a1);
  }

  public static IAST Subdivide(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Subdivide, a0, a1, a2);
  }

  /**
   * Create a list <code>{0, 1/n,2/n,3/n,...,n}</code>.
   * 
   * @param n the number of the &quot;elements of the result list&quot; minus 1
   * @return a list <code>{0, 1/n,2/n,3/n,...,n}</code>
   */
  public static IAST subdivide(int n) {
    IASTAppendable result = F.ListAlloc(n);
    for (int i = 0; i < n + 1; i++) {
      result.append(F.QQ(i, n));
    }
    return result;
  }

  /**
   * Create a list by dividing the range <code>0</code> to <code>xMax</code> into <code>n</code>
   * parts.
   * 
   * @param xMax end of range (inclusive)
   * @param n the number of the &quot;elements of the result list&quot; minus 1
   * @return
   */
  public static IAST subdivide(int xMax, int n) {
    IASTAppendable result = F.ListAlloc(n);
    IRational part = F.QQ(xMax, n);
    for (int i = 0; i < n + 1; i++) {
      IRational element = part.multiply(i);
      result.append(element);
    }
    return result;
  }

  /**
   * Create a list by dividing the range <code>xMin</code> to <code>xMax</code> into <code>n</code>
   * parts.
   * 
   * @param xMin start of range (inclusive)
   * @param xMax end of range (inclusive)
   * @param n the number of the &quot;elements of the result list&quot; minus 1
   * @return
   */
  public static IAST subdivide(int xMin, int xMax, int n) {
    IASTAppendable result = F.ListAlloc(n);
    int diff = xMax - xMin;
    IRational part = F.QQ(diff, n);
    IInteger xMinInt = F.ZZ(xMin);
    for (int i = 0; i < n + 1; i++) {
      IRational element = part.multiply(i).add(xMinInt);
      result.append(element);
    }
    return result;
  }

  /**
   * Create a list by dividing the range <code>0</code> to <code>xMax</code> into <code>n</code>
   * parts.
   * 
   * @param xMax end of range (inclusive)
   * @param n the number of the &quot;elements of the result list&quot; minus 1
   * @return
   */
  public static ASTRealVector subdivide(double xMax, int n) {
    double[] vector = new double[n + 1];

    double part = xMax / n;
    for (int i = 0; i < n + 1; i++) {
      vector[i] = part * i;
    }
    return new ASTRealVector(vector, false);
  }

  /**
   * Create a list by dividing the range <code>xMin</code> to <code>xMax</code> into <code>n</code>
   * parts.
   * 
   * @param xMin start of range (inclusive)
   * @param xMax end of range (inclusive)
   * @param n the number of the &quot;elements of the result list&quot; minus 1
   * @return
   */
  public static ASTRealVector subdivide(double xMin, double xMax, int n) {
    double[] vector = new double[n + 1];
    double diff = xMax - xMin;
    double part = diff / n;
    for (int i = 0; i < n + 1; i++) {
      vector[i] = part * i + xMin;
    }
    return new ASTRealVector(vector, false);
  }

  public static IAST Subfactorial(final IExpr a0) {
    return new AST1(Subfactorial, a0);
  }

  public static IAST Subscript(final IExpr x, final IExpr y) {
    return new AST2(Subscript, x, y);
  }

  /**
   * Substitute all (sub-) expressions <code>x</code> in <code>expr</code> with <code>y</code>. If
   * no substitution matches, the method returns the given <code>expr</code>.
   *
   * @param expr the complete expresssion
   * @param x the subexpression which should be replaced
   * @param y the expression which replaces <code>x</code>
   * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or
   *         the substituted expression.
   */
  public static IExpr xreplace(final IExpr expr, final IExpr x, final IExpr y) {
    return expr.xreplace(x, y);
  }

  /**
   * Substitute all (sub-) expressions <code>x</code> in <code>expr</code> with <code>y</code>. If
   * no substitution matches, the method returns the given <code>expr</code>.
   *
   * @param expr the complete expresssion
   * @param x the subexpression which should be replaced
   * @param y the expression which replaces <code>x</code>
   * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or
   *         the substituted expression.
   */
  public static IExpr subs(final IExpr expr, final IExpr x, final IExpr y) {
    return expr.subs(x, y);
  }

  /**
   * Substitute all (sub-) expressions contained as keys in <code>map</code> in <code>expr</code>
   * with the corresponding value in <code>map</code>. If no substitution matches, the method
   * returns the given <code>expr</code>.
   * 
   * @param expr
   * @return
   */
  public static IExpr subsList(final IExpr expr, final Map<? extends IExpr, ? extends IExpr> map) {
    return expr.replaceAll(map).orElse(expr);
  }

  /**
   * Substitute all (sub-) expressions with the given unary function. If no substitution matches,
   * the method returns the given <code>expr</code>.
   *
   * @param expr
   * @param function if the unary functions <code>apply()</code> method returns <code>F.NIL</code>
   *        the expression isn't substituted.
   * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or
   *         the substituted expression.
   */
  public static final IExpr subst(IExpr expr, final Function<IExpr, IExpr> function) {
    return expr.replaceAll(function).orElse(expr);
  }

  /**
   * Substitute all (sub-) expressions if the given binary predicate returns <code>true</code>. If
   * no substitution matches, the method returns the given <code>expr</code>.
   * 
   * @param expr
   * @param predicate if the unary predicate <code>test()</code> method returns <code>false</code>
   *        the expression isn't substituted.
   * @param value
   * @return
   */
  public static final IExpr subst(IExpr expr, final Predicate<IExpr> predicate, final IExpr value) {
    return expr.replaceAll(x -> predicate.test(x) ? value : F.NIL).orElse(expr);
  }

  /**
   * Substitute all (sub-) expressions which contain a &quot;complex&quot; {@link S#Abs} function
   * with the corresponding &quot;real&quot; {@link S#RealAbs} function. For {@link S#RealAbs} a
   * derivative <code>x/RealAbs(x)</code> is defined.
   * 
   * @param expr
   * @return
   */
  public static final IExpr substAbs(IExpr expr) {
    return subst(expr, x -> x.isAbs() ? F.RealAbs(x.first()) : F.NIL);
  }

  /**
   * Substitute all (sub-) expressions with the given map. If no substitution matches, the method
   * returns the given <code>expr</code>.
   *
   * @param expr
   * @param map if the maps <code>get()</code> method returns <code>null</code> the expression isn't
   *        substituted.
   * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or
   *         the substituted expression.
   */
  public static IExpr subst(IExpr expr, final Map<? extends IExpr, ? extends IExpr> map) {
    return expr.replaceAll(map).orElse(expr);
  }

  /**
   * Substitute all (sub-) expressions with the given rule set. If no substitution matches, the
   * method returns the given <code>expr</code>.
   *
   * @param expr
   * @param astRules rules of the form <code>x-&gt;y</code> or <code>{a-&gt;b, c-&gt;d}</code>; the
   *        left-hand-side of the rule can contain pattern objects.
   * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or
   *         the substituted expression.
   */
  public static IExpr subst(IExpr expr, final IAST astRules) {
    if (astRules.isListOfLists()) {
      IExpr result = expr;
      for (IExpr subList : astRules) {
        result = subst(result, (IAST) subList);
      }
      return result;
    }
    return expr.replaceAll(astRules).orElse(expr);
  }

  /**
   * Substitute all (sub-) expressions with the given replacement expression. If no (sub-)
   * expression matches, the method returns the given <code>expr</code>.
   *
   * @param expr
   * @param subExpr
   * @param replacementExpr
   * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or
   *         the substituted expression.
   */
  public static IExpr subst(IExpr expr, IExpr subExpr, IExpr replacementExpr) {
    return expr.replaceAll(Functors.rules(Rule(subExpr, replacementExpr), EvalEngine.get()))
        .orElse(expr);
  }

  public static IAST SubsetQ(IExpr x, IExpr y) {
    return new AST2(S.SubsetQ, x, y);
  }


  /**
   * Return <code>x + (-1)*y</code>
   *
   * @param x
   * @param y
   * @return
   */
  public static IAST Subtract(final IExpr x, final IExpr y) {
    IExpr yInverse = y.opposite();
    if (x.compareTo(yInverse) > 0) {
      // swap arguments
      return new B2.Plus(yInverse, x);
    }
    return new B2.Plus(x, yInverse);
  }

  /**
   * Return <code>x + (-1)*y</code>
   *
   * @param x
   * @param y
   * @return
   */
  public static IAST Subtract(final IExpr x, final int y) {
    return new B2.Plus(x, F.ZZ(-y));
  }

  public static IASTMutable SubtractSides(final IExpr equationOrInequality) {
    return new AST1(SubtractSides, equationOrInequality);
  }

  public static IASTMutable SubtractSides(final IExpr equationOrInequality, final IExpr x) {
    return new AST2(SubtractSides, equationOrInequality, x);
  }

  /**
   * @param expr expression which should be summed up
   * @param iterationSpecification a standard iteration specification
   * @return <code>Sum(expr, iterationSpecification)</code> AST
   */
  public static IAST Sum(final IExpr expr, final IExpr iterationSpecification) {
    return new AST2(Sum, expr, iterationSpecification);
  }

  public static IAST Sum(final IExpr expr, final IExpr iterationSpecification1,
      final IExpr iterationSpecification2) {
    return new AST3(Sum, expr, iterationSpecification1, iterationSpecification2);
  }

  public static IAST Sum(final IExpr expr, final IExpr iterationSpecification1,
      final IExpr iterationSpecification2, final IExpr iterationSpecification3) {
    return quaternary(Sum, expr, iterationSpecification1, iterationSpecification2,
        iterationSpecification3);
  }

  public static IAST Sum(final IExpr expr, final IExpr iterationSpecification1,
      final IExpr iterationSpecification2, final IExpr iterationSpecification3,
      final IExpr iterationSpecification4) {
    return quinary(Sum, expr, iterationSpecification1, iterationSpecification2,
        iterationSpecification3, iterationSpecification4);
  }

  public static IAST Sum(final IExpr expr, final IExpr iterationSpecification1,
      final IExpr iterationSpecification2, final IExpr iterationSpecification3,
      final IExpr iterationSpecification4, final IExpr iterationSpecification5) {
    IExpr[] args = new IExpr[] {expr, iterationSpecification1, iterationSpecification2,
        iterationSpecification3, iterationSpecification4, iterationSpecification5};
    return F.ast(args, Sum);
  }

  public static IRational sumRational(final IntFunction<IRational> function, final int iMin,
      final int iMax) {
    return sumRational(function, iMin, iMax, 1);
  }

  /**
   * Evaluate the sum from <code>iMin</code> to <code>iMax</code> and step <code>1</code>.
   *
   * @param function
   * @param iMin
   * @param iMax
   * @return
   */
  public static IExpr sum(final Function<IInteger, IExpr> function, final int iMin,
      final int iMax) {
    return intSum(function, iMin, iMax, 1);
  }


  /**
   * Iterate over an integer range <code>from <= i <= to</code> with the step <code>step/code> and
   * evaluate the {@link S#Product}
   * 
   * @param function the function which should be applied on each iterator value
   * @param from from this position (included)
   * @param to to this position (included)
   * @param step
   * @return
   */
  public static IExpr intProduct(final Function<IInteger, IExpr> function, final int from,
      final int to, final int step) {
    if (from > to && step > 0) {
      return F.C1;
    }
    IASTAppendable result = F.TimesAlloc(F.allocMin32(to - from + 1));
    long numberOfLeaves = 0;
    INumber number = F.C1;
    // insert number as placeholder
    result.append(number);
    EvalEngine engine = EvalEngine.get();
    for (int i = from; i <= to; i += step) {
      IExpr temp = engine.evaluate(function.apply(ZZ(i)));
      if (temp.isNumber()) {
        number = number.times((INumber) temp);
        if (number instanceof IInteger //
            && ((IInteger) number).bitLength() > Config.MAX_BIT_LENGTH / 100) {
          BigIntegerLimitExceeded.throwIt(Config.MAX_BIT_LENGTH / 100);
        }
      } else {
        numberOfLeaves += temp.leafCount() + 1;
        if (numberOfLeaves >= Config.MAX_AST_SIZE / 2) {
          ASTElementLimitExceeded.throwIt(numberOfLeaves);
        }
        result.append(temp);
      }
    }
    // replace placeholder with evaluated number
    result.set(1, number);
    return result.oneIdentity0();
  }

  /**
   * Calculate <code>Sum(function(k), {k, iMin, iMax})</code>
   * 
   * @param function
   * @param iMin from this position (included)
   * @param iMax to this position (included)
   * @return
   */
  public static IExpr intSum(final IntFunction<IExpr> function, final int iMin, final int iMax) {
    if (iMin > iMax) {
      return F.C0;
    }
    IASTAppendable result = F.PlusAlloc(F.allocMin32(iMax - iMin + 1));
    int numberOfLeaves = 0;
    EvalEngine engine = EvalEngine.get();
    INumber number = F.C0;
    // insert number as placeholder
    result.append(number);
    for (int i = iMin; i <= iMax; i += 1) {
      IExpr temp = engine.evaluate(function.apply(i));
      if (temp.isNumber()) {
        number = number.plus((INumber) temp);
        if (number instanceof IInteger //
            && ((IInteger) number).bitLength() > Config.MAX_BIT_LENGTH / 100) {
          BigIntegerLimitExceeded.throwIt(Config.MAX_BIT_LENGTH / 100);
        }
      } else {
        numberOfLeaves += temp.leafCount() + 1;
        if (numberOfLeaves >= Config.MAX_AST_SIZE / 2) {
          ASTElementLimitExceeded.throwIt(numberOfLeaves);
        }
        result.append(temp);
      }
    }
    // replace placeholder with evaluated number
    result.set(1, number);
    return result.oneIdentity0();
  }

  /**
   * Create a list of {@link S#Plus} expressions (sums), with an evaluated number in position 1 for
   * the sum of each step of the <code>function.apply(position)</code> from <code>iMin</code>
   * (inclusive) to <code>iMax</code> (inclusive).
   * 
   * @param function the function which should be applied on each iteration step
   * @param startValue will be used as placeholder in position 1 of plusList
   * @param iMin from this position (included)
   * @param iMax to this position (included)
   * @return a list of {@link S#Plus} expressions with the evaluated number in position 1
   */
  public static IAST intSumList(final IntFunction<IExpr> function, IExpr startValue, final int iMin,
      final int iMax) {
    if (iMin > iMax) {
      return F.CEmptyList;
    }
    IASTAppendable result = F.ListAlloc(F.allocMin32(iMax - iMin + 1));
    int numberOfLeaves = 0;
    EvalEngine engine = EvalEngine.get();
    IASTAppendable plusList = F.PlusAlloc(F.allocMin32(iMax - iMin + 1));
    // insert 0 as a placeholder for number calculations in intSumList()
    INumber numberPlaceholder = F.C0;
    if (startValue.isNumber()) {
      numberPlaceholder = (INumber) startValue;
      plusList.append(numberPlaceholder);
    } else {
      plusList.append(numberPlaceholder);
      plusList.append(startValue);
    }

    // INumber numberPlaceholder = startValue;
    for (int i = iMin; i <= iMax; i += 1) {
      IExpr temp = engine.evaluate(function.apply(i));
      if (temp.isNumber()) {
        numberPlaceholder = numberPlaceholder.plus((INumber) temp);
        if (numberPlaceholder instanceof IInteger //
            && ((IInteger) numberPlaceholder).bitLength() > Config.MAX_BIT_LENGTH / 100) {
          BigIntegerLimitExceeded.throwIt(Config.MAX_BIT_LENGTH / 100);
        }
      } else {
        numberOfLeaves += temp.leafCount() + 1;
        if (numberOfLeaves >= Config.MAX_AST_SIZE / 2) {
          ASTElementLimitExceeded.throwIt(numberOfLeaves);
        }
        plusList.append(temp);
      }
      result.append(plusList.setAtCopy(1, numberPlaceholder));
    }
    return result;
  }

  /**
   * Iterate over an integer range <code>from <= i <= to</code> with the step <code>step/code> and
   * evaluate the {@link S#Sum}
   *
   * @param function the function which should be applied on each iterator value
   * @param from from this position (included)
   * @param to to this position (included)
   * @param step
   * @return
   */
  public static IExpr intSum(final Function<IInteger, IExpr> function, final int from, final int to,
      final int step) {
    return intSum(function, from, to, step, false);
  }

  /**
   * Iterate over an integer range <code>from <= i <= to</code> with the step <code>step/code> and
   * evaluate the {@link S#Sum}
   *
   * @param function the function which should be applied on each iterator value
   * @param from from this position (included)
   * @param to to this position (included)
   * @param step
   * @param expand expand {@link S#Plus}, {@link S#Times}, {@link S#Power} subexpressions
   * @return
   */
  public static IExpr intSum(final Function<IInteger, IExpr> function, final int from, final int to,
      final int step, boolean expand) {
    if (from > to && step > 0) {
      return F.C0;
    }
    IASTAppendable result = F.PlusAlloc(F.allocMin32(to - from + 1));
    long numberOfLeaves = 0;
    INumber number = F.C0;
    // insert number as placeholder
    result.append(number);
    EvalEngine engine = EvalEngine.get();
    for (int i = from; i <= to; i += step) {
      IExpr temp = engine.evaluate(function.apply(ZZ(i)));
      if (temp.isNumber()) {
        number = number.plus((INumber) temp);
        if (number instanceof IInteger //
            && ((IInteger) number).bitLength() > Config.MAX_BIT_LENGTH / 100) {
          BigIntegerLimitExceeded.throwIt(((IInteger) number).bitLength());
        }
      } else {
        numberOfLeaves += temp.leafCount() + 1;
        if (numberOfLeaves >= Config.MAX_AST_SIZE / 2) {
          ASTElementLimitExceeded.throwIt(numberOfLeaves);
        }
        if (expand && temp.isPlusTimesPower()) {
          result.append(F.evalExpand(temp));
        } else {
          result.append(temp);
        }
      }
    }
    // replace placeholder with evaluated number
    result.set(1, number);
    return result.oneIdentity0();
  }

  /**
   * Evaluate the sum from <code>iMin</code> to <code>iMax</code> and step <code>iStep</code>.
   *
   * @param function
   * @param iMin from this position (included)
   * @param iMax to this position (included)
   * @param iStep
   * @return
   */
  public static IExpr sum(final Function<IInteger, IExpr> function, final int iMin, final int iMax,
      final int iStep) {
    return intSum(function, iMin, iMax, iStep);
  }

  /**
   * Evaluate the sum from <code>iMin</code> to <code>iMax</code> and step <code>iStep</code>.
   *
   * @param function
   * @param iMin from this position (included)
   * @param iMax to this position (included)
   * @param iStep
   * @param expand expand {@link S#Plus}, {@link S#Times}, {@link S#Power} subexpressions
   * @return
   */
  public static IExpr sum(final Function<IInteger, IExpr> function, final int iMin, final int iMax,
      final int iStep, boolean expand) {
    return intSum(function, iMin, iMax, iStep, expand);
  }

  public static IAST Superscript(final IExpr x, final IExpr y) {
    return new AST2(Superscript, x, y);
  }

  public static IASTAppendable SurfaceGraphics() {

    return ast(SurfaceGraphics);
  }

  public static IAST Switch(final IExpr... a) {
    return function(Switch, a);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Table.md">Table</a>
   *
   * @param expr expression which should be iterated
   * @param iterationSpecification a standard iteration specification
   * @return <code>Table(expr, iterationSpecification)</code> AST
   */
  public static IAST Table(final IExpr expr, final IExpr iterationSpecification) {
    return new AST2(Table, expr, iterationSpecification);
  }

  public static IAST Table(final IExpr expr, final IExpr iterationSpecification1,
      final IExpr iterationSpecification2) {
    return new AST3(Table, expr, iterationSpecification1, iterationSpecification2);
  }

  public static IASTMutable TagSet(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(TagSet, a0, a1, a2);
  }

  public static IASTMutable TagSetDelayed(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(TagSetDelayed, a0, a1, a2);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Take.md">Take</a>
   */
  public static IAST Take(final IExpr a0, final IExpr a1) {
    return new AST2(Take, a0, a1);
  }

  public static IASTMutable TakeLargest(final IExpr a0, final IExpr a1) {
    return new AST2(TakeLargest, a0, a1);
  }

  public static IASTMutable TakeLargestBy(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(TakeLargestBy, a0, a1, a2);
  }

  public static IASTMutable TakeSmallest(final IExpr a0, final IExpr a1) {
    return new AST2(TakeSmallest, a0, a1);
  }

  public static IASTMutable TakeSmallestBy(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(TakeSmallestBy, a0, a1, a2);
  }

  /**
   * Returns the tangent of <code>z</code> (measured in
   * <a href="https://en.wikipedia.org/wiki/Radian">Radians</a>).
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tan.md">Tan</a>
   *
   * @param z
   * @return
   */
  public static IAST Tan(final IExpr z) {
    return new B1.Tan(z);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Tanh.md">Tanh</a>
   */
  public static IAST Tanh(final IExpr z) {
    return new AST1(Tanh, z);
  }

  public static IAST Taylor(final IExpr a0, final IExpr a1) {
    return new AST2(Taylor, a0, a1);
  }

  public static IAST TensorRank(final IExpr a0) {
    return new AST1(TensorRank, a0);
  }

  public static IAST TensorSymmetry(final IExpr a0) {
    return new AST1(TensorSymmetry, a0);
  }

  public static IAST TensorSymmetry(final IExpr a0, final IExpr a1) {
    return new AST2(TensorSymmetry, a0, a1);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/TeXForm.md">TeXForm</a>
   */
  public static IAST TeXForm(final IExpr expr) {
    return new AST1(TeXForm, expr);
  }

  public static IAST Text(final IExpr expr, IAST coords) {
    return new AST2(Text, expr, coords);
  }

  /**
   * Create a function <code>head(arg1, arg2, arg3)</code> with 3 argument as an <code>AST3</code>
   * mutable object without evaluation.
   *
   * @param head
   * @param arg1
   * @param arg2
   * @param arg3
   * @return
   */
  public static final IASTMutable ternaryAST3(final IExpr head, final IExpr arg1, final IExpr arg2,
      final IExpr arg3) {
    return new AST3(head, arg1, arg2, arg3);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Thread.md">Thread</a>
   */
  public static IAST Thread(final IExpr a0) {
    return new AST1(Thread, a0);
  }

  public static final IAST ThreeJSymbol(final IExpr arg1, final IExpr arg2, final IExpr arg3) {
    return new AST3(S.ThreeJSymbol, arg1, arg2, arg3);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Throw.md">Throw</a>
   */
  public static IAST Throw(final IExpr a) {
    if (a.isFalse()) {
      return CThrowFalse;
    }
    if (a.isTrue()) {
      return CThrowTrue;
    }
    return new AST1(Throw, a);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/TimeConstrained.md">TimeConstrained</a>
   */
  public static IAST TimeConstrained(final IExpr a0, final IExpr a1) {
    return new AST2(TimeConstrained, a0, a1);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/TimeConstrained.md">TimeConstrained</a>
   */
  public static IAST TimeConstrained(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(TimeConstrained, a0, a1, a2);
  }

  /**
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
   *
   * @return
   */
  public static IASTAppendable Times() {
    return ast(Times);
  }

  /**
   * Create a Times() function with allocated space for size elements. See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
   *
   * @param initialCapacity the initialCapacity of this AST
   * @return
   */
  public static IASTAppendable TimesAlloc(int initialCapacity) {
    return ast(Times, initialCapacity);
  }

  /**
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
   */
  public static IASTAppendable Times(final IExpr x) {
    return unary(Times, x);
  }

  /**
   * Define a <code>Times()</code> expression <code>a1 * a2 * ...</code> for multiplication.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
   */
  public static IAST Times(final IExpr... a) {
    switch (a.length) {
      case 1:
        return new AST1(Times, a[0]);
      case 2:
        return new B2.Times(a[0], a[1]);
      case 3:
        return new AST3(Times, a[0], a[1], a[2]);
      default:
        return new AST(Times, a);
    }
  }

  /**
   * Define a <code>Times()</code> expression <code>x * y</code> for multiplication.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
   */
  public static IASTMutable Times(final IExpr x, final IExpr y) {
    if (x != null && y != null) {
      return timesOrderless(IExpr::isTimes, x, y);
    }
    return new B2.Times(x, y);
  }

  /**
   * Distribute {@link S#Plus} function expressions on <code>Times(x,y)</code> if <code>x</code> or
   * <code>y</code> is a {@link S#Plus} expression.
   * 
   * @param x maybe a {@link S#Plus} function expression
   * @param y maybe a {@link S#Plus} function expression
   * @return
   */
  public static IExpr distributePlusOnTimes(@Nonnull IExpr x, @Nonnull final IExpr y) {
    if (x.isPlus() || y.isPlus()) {
      return Algebra.distribute(F.Distribute(new B2.Times(x, y)), S.Plus);
    }
    return timesOrderless(IExpr::isTimes, x, y);
  }

  /**
   * Define a <code>Times()</code> expression <code>a0 * a1 * a2</code> for multiplication.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
   */
  public static IAST Times(final IExpr x, final IExpr y, final IExpr z) {
    if (x != null && y != null && z != null) {
      return timesOrderless(IExpr::isTimes, x, y, z);
    }
    return new B3.Times(x, y, z);
  }

  private static IASTMutable plusOrderless(Predicate<IExpr> t, final IExpr a1, final IExpr a2) {
    final boolean test1 = t.test(a1);
    final boolean test2 = t.test(a2);
    if (test1 || test2) {
      int size = test1 ? a1.size() : 1;
      size += test2 ? a2.size() : 1;
      IASTAppendable result = ast(Plus, size);
      if (test1) {
        result.appendArgs((IAST) a1);
      } else {
        result.append(a1);
      }
      if (test2) {
        result.appendArgs((IAST) a2);
      } else {
        result.append(a2);
      }
      EvalAttributes.sort(result);
      return result;
    }
    if (a1.compareTo(a2) > 0) {
      // swap arguments
      return new B2.Plus(a2, a1);
    }
    return new B2.Plus(a1, a2);
  }

  private static IASTMutable plusOrderless(Predicate<IExpr> t, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    final boolean test1 = t.test(a1);
    final boolean test2 = t.test(a2);
    final boolean test3 = t.test(a3);
    if (test1 || test2 || test3) {
      int size = test1 ? a1.size() : 1;
      size += test2 ? a2.size() : 1;
      size += test3 ? a3.size() : 1;
      IASTAppendable result = ast(Plus, size);
      if (test1) {
        result.appendArgs((IAST) a1);
      } else {
        result.append(a1);
      }
      if (test2) {
        result.appendArgs((IAST) a2);
      } else {
        result.append(a2);
      }
      if (test3) {
        result.appendArgs((IAST) a3);
      } else {
        result.append(a3);
      }
      EvalAttributes.sort(result);
      return result;
    }
    IASTMutable result = new B3.Plus(a1, a2, a3);
    EvalAttributes.sort(result);
    return result;
  }

  private static IASTMutable timesOrderless(Predicate<IExpr> t, final IExpr a1, final IExpr a2) {
    final boolean test1 = t.test(a1);
    final boolean test2 = t.test(a2);
    if (test1 || test2) {
      int size = test1 ? a1.size() : 1;
      size += test2 ? a2.size() : 1;
      IASTAppendable result = ast(Times, size);
      if (test1) {
        result.appendArgs((IAST) a1);
      } else {
        result.append(a1);
      }
      if (test2) {
        result.appendArgs((IAST) a2);
      } else {
        result.append(a2);
      }
      EvalAttributes.sort(result);
      return result;
    }
    if (a1.compareTo(a2) > 0) {
      // swap arguments
      return new B2.Times(a2, a1);
    }
    return new B2.Times(a1, a2);
  }

  private static IASTMutable timesOrderless(Predicate<IExpr> t, final IExpr a1, final IExpr a2,
      final IExpr a3) {
    final boolean test1 = t.test(a1);
    final boolean test2 = t.test(a2);
    final boolean test3 = t.test(a3);
    if (test1 || test2 || test3) {
      int size = test1 ? a1.size() : 1;
      size += test2 ? a2.size() : 1;
      size += test3 ? a3.size() : 1;
      IASTAppendable result = ast(Times, size);
      if (test1) {
        result.appendArgs((IAST) a1);
      } else {
        result.append(a1);
      }
      if (test2) {
        result.appendArgs((IAST) a2);
      } else {
        result.append(a2);
      }
      if (test3) {
        result.appendArgs((IAST) a3);
      } else {
        result.append(a3);
      }
      EvalAttributes.sort(result);
      return result;
    }
    IASTMutable result = new B3.Times(a1, a2, a3);
    EvalAttributes.sort(result);
    return result;
  }

  /**
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
   */
  public static IAST Times(final long num, final IExpr... a) {
    IASTAppendable ast = ast(Times, a.length + 1);
    ast.append(ZZ(num));
    ast.appendAll(a, 0, a.length);
    return ast;
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/ToExpression.md">ToExpression</a>
   */
  public static IAST ToExpression(final IExpr a0) {
    return new AST1(ToExpression, a0);
  }

  /**
   * Converts this number to an <code>int</code> value; this method returns
   * <code>Integer.MIN_VALUE</code>, if the value of this integer isn't in the range
   * {@link java.lang.Integer.MIN_VALUE}+1 to {@link java.lang.Integer.MAX_VALUE}-1 or the
   * expression is not convertible to the <code>int</code> range.
   *
   * @return the numeric value represented by this expression after conversion to type <code>int
   *     </code> or <code>Integer.MIN_VALUE</code> if this expression cannot be converted.
   */
  public static int toIntDefault(double value) {
    return toIntDefault(value, java.lang.Integer.MIN_VALUE);
  }

  /**
   * Converts this number to an <code>int</code> value; this method returns
   * <code>defaultValue</code>, if the value of this integer isn't in the range
   * {@link java.lang.Integer.MIN_VALUE}+1 to {@link java.lang.Integer.MAX_VALUE}-1 or the
   * expression is not convertible to the <code>int</code> range.
   *
   * @param value
   * @param defaultValue the default value, if this integer is not in the <code>int</code> range
   * @return the numeric value represented by this integer after conversion to type <code>int</code>
   */
  public static int toIntDefault(double value, int defaultValue) {
    if (DoubleMath.isMathematicalInteger(value)) {
      int v = (int) value;
      if (v == java.lang.Integer.MIN_VALUE //
          || v == java.lang.Integer.MAX_VALUE) {
        return defaultValue;
      }
      return v;
    }
    return defaultValue;
  }

  public static IAST ToIntervalData(final IExpr expr) {
    return new AST1(ToIntervalData, expr);
  }

  public static IAST ToIntervalData(final IExpr expr, final IExpr variable) {
    return new AST2(ToIntervalData, expr, variable);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Together.md">Together</a>
   */
  public static IAST Together(final IExpr a0) {
    return new AST1(Together, a0);
  }

  public static IAST ToPolarCoordinates(final IExpr a0) {
    return new AST1(ToPolarCoordinates, a0);
  }

  public static IAST ToSphericalCoordinates(final IExpr a0) {
    return new AST1(ToSphericalCoordinates, a0);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Total.md">Total</a>
   */
  public static IAST Total(final IExpr a0) {
    return new AST1(Total, a0);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Tr.md">Tr</a>
   */
  public static IAST Tr(final IExpr a0) {
    return new AST1(Tr, a0);
  }

  public static IAST Trace(final IExpr a0) {
    return new AST1(Trace, a0);
  }

  public static IAST TransformationFunction(final IExpr a0) {
    return new AST1(TransformationFunction, a0);
  }

  public static IAST TranslationTransform(final IExpr a0) {
    return new AST1(TranslationTransform, a0);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Transpose.md">Transpose</a>
   */
  public static IAST Transpose(final IExpr list) {
    return new AST1(Transpose, list);
  }

  public static IAST Triangle(final IAST list) {
    return new AST1(Triangle, list);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/TrigExpand.md">TrigExpand</a>
   */
  public static IAST TrigExpand(final IExpr expr) {
    return new AST1(TrigExpand, expr);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/TrigReduce.md">TrigReduce</a>
   */
  public static IAST TrigReduce(final IExpr expr) {
    return new AST1(TrigReduce, expr);
  }

  public static IAST TrigSimplifyFu(final IExpr expr) {
    return new AST1(TrigSimplifyFu, expr);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/TrigToExp.md">TrigToExp</a>
   */
  public static IAST TrigToExp(final IExpr expr) {
    return new AST1(TrigToExp, expr);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/TrueQ.md">TrueQ</a>
   */
  public static IAST TrueQ(final IExpr expr) {
    return new AST1(TrueQ, expr);
  }

  public static IAST Tuples(final IExpr x, final IExpr y) {
    return new AST2(Tuples, x, y);
  }

  /**
   * Create a function <code>head(arg)</code> with 1 argument without evaluation.
   *
   * @param head
   * @param arg
   * @return
   */
  public static final IASTAppendable unary(final IExpr head, final IExpr arg) {
    return new AST(new IExpr[] {head, arg});
  }

  /**
   * Create a function <code>head(arg)</code> with 1 argument as an <code>AST1</code> mutable object
   * without evaluation.
   *
   * @param head
   * @param arg
   * @return
   */
  public static final IASTMutable unaryAST1(final IExpr head, final IExpr arg) {
    return new AST1(head, arg);
  }

  public static IAST Underflow() {
    return new AST0(Underflow);
  }

  /**
   * <code>UndirectedEdge</code> is an undirected edge between the vertices <code>a</code> and
   * <code>b</code> in a `graph` object.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UndirectedEdge.md">UndirectedEdge</a>
   *
   * @param a
   * @param b
   * @return
   */
  public static IAST UndirectedEdge(final IExpr a, final IExpr b) {
    return new B2.UndirectedEdge(a, b);
  }

  /**
   * Yields {@link S#False} if <code>lhs</code> and <code>rhs</code> are known to be equal, or
   * {@link S#True} if <code>lhs</code> and <code>rhs</code> are known to be unequal.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unequal.md">Unequal</a>
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public static IAST Unequal(final IExpr lhs, final IExpr rhs) {
    return new AST2(Unequal, lhs, rhs);
  }

  public static IAST Unevaluated(final IExpr a0) {
    return new AST1(Unevaluated, a0);
  }

  public static IAST Union(final IExpr list1) {
    return new AST1(Union, list1);
  }

  public static IAST Union(final IExpr list1, final IExpr list2) {
    return new AST2(Union, list1, list2);
  }

  public static IAST Unique(final IExpr a0) {
    return new AST1(Unique, a0);
  }

  /**
   * Convert the <code>quantity</code> to the base unit.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitConvert.md">UnitConvert</a>
   *
   * @param quantity
   * @return
   */
  public static IAST UnitConvert(final IExpr quantity) {
    return new AST1(UnitConvert, quantity);
  }

  /**
   * Convert the <code>quantity</code> to the given <code>unit</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitConvert.md">UnitConvert</a>
   *
   * @param quantity
   * @param unit
   * @return
   */
  public static IAST UnitConvert(final IExpr quantity, final IExpr unit) {
    return new AST2(UnitConvert, quantity, unit);
  }

  public static IAST UniformDistribution(final IExpr a) {
    return new AST1(UniformDistribution, a);
  }

  public static IAST UnitStep(final IExpr a0) {
    return new AST1(UnitStep, a0);
  }

  /**
   * Returns {@link S#True} if <code>lhs</code> and <code>rhs</code> are not structurally identical.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnsameQ.md">UnsameQ</a>
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public static IAST UnsameQ(final IExpr lhs, final IExpr rhs) {
    return new AST2(UnsameQ, lhs, rhs);
  }

  public static IAST Unset(final IExpr a0) {
    return new AST1(Unset, a0);
  }

  public static IAST UpSet(final IExpr a0, final IExpr a1) {
    return new AST2(UpSet, a0, a1);
  }

  public static IAST UpSetDelayed(final IExpr a0, final IExpr a1) {
    return new AST2(UpSetDelayed, a0, a1);
  }

  public static IAST Variables(final IExpr a0) {
    return new AST1(Variables, a0);
  }

  public static IAST Variance(final IExpr a0) {
    return new AST1(Variance, a0);
  }

  public static IAST WeberE(final IExpr v, final IExpr z) {
    return new AST2(WeberE, v, z);
  }

  public static IAST WeberE(final IExpr v, final IExpr m, final IExpr z) {
    return new AST3(WeberE, v, m, z);
  }

  public static IAST WeibullDistribution(final IExpr a0, final IExpr a1) {
    return new AST2(WeibullDistribution, a0, a1);
  }

  public static IAST WeibullDistribution(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(WeibullDistribution, a0, a1, a2);
  }

  public static IAST Which(final IExpr... a) {
    return ast(a, Which);
  }

  /**
   * Evaluates <code>body</code> as long as <code>test</code> evaluates to {@link S#True}.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/While.md">While</a>
   *
   * @param test
   * @param body
   * @return
   */
  public static IAST While(final IExpr test, final IExpr body) {
    return new AST2(While, test, body);
  }

  public static IAST WhittakerM(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(WhittakerM, a0, a1, a2);
  }

  public static IAST WhittakerW(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(WhittakerW, a0, a1, a2);
  }

  /**
   * Evaluates <code>expr</code> for the<code>listOfLocalVariables</code> by replacing the local
   * variables in <code>expr</code>.
   *
   * <p>
   * See: <a href=
   * "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/With.md">With</a>
   *
   * @param listOfLocalVariables
   * @param expr
   * @return
   */
  public static IAST With(final IExpr listOfLocalVariables, final IExpr expr) {
    return new B2.With(listOfLocalVariables, expr);
  }

  /**
   * Symmetry of a zero tensor.
   *
   * @param a0
   * @return
   */
  public static IAST ZeroSymmetric(final IExpr a0) {
    return new AST1(ZeroSymmetric, a0);
  }

  public static IAST Zeta(final IExpr s) {
    return new AST1(Zeta, s);
  }

  public static IAST Zeta(final IExpr s, final IExpr a) {
    return new AST2(Zeta, s, a);
  }

  public static IAST ZTransform(final IExpr expr, final IExpr n, final IExpr z) {
    return new AST3(ZTransform, expr, n, z);
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
   * Create an integer number by using the internal small integer number cache. The returned object
   * references might be the same.
   *
   * @param integerValue
   * @return
   */
  public static IInteger ZZ(final int integerValue) {
    return AbstractIntegerSym.valueOf(integerValue);
  }

  /**
   * Create a new integer number without using the internal small integer number cache. The returned
   * object references will always be newly created.
   *
   * @param integerValue
   * @return
   */
  public static IInteger ZZUniqueReference(final int integerValue) {
    return AbstractIntegerSym.valueOfUniqueReference(integerValue);
  }

  /**
   * Create an integer number.
   *
   * @param integerValue
   * @return
   */
  public static IInteger ZZ(final long integerValue) {
    return AbstractIntegerSym.valueOf(integerValue);
  }

  /**
   * Create a large integer number.
   *
   * @param integerString the integer number represented as a String
   * @param radix the radix to be used while parsing
   * @return Object
   */
  public static IInteger ZZ(final String integerString, final int radix) {
    return AbstractIntegerSym.valueOf(integerString, radix);
  }

  /**
   * The operator form <code>op(f)[expr]</code> is transformed to <code>op(expr, f)</code>. The
   * operator form <code>op(f, g)[expr]</code> is transformed to <code>op(expr, f, g)</code>
   *
   * @param ast1 an IAST with condition <code>ast1Arg.head().isAST1() && ast1Arg.isAST1()</code>
   * @return
   */
  public static IAST operatorForm1Append(final IAST ast1) {
    if (ast1.isAST1() && ast1.head().isAST() && ast1.head().size() > 1) {
      IAST head = (IAST) ast1.head();
      switch (head.size()) {
        case 2:
          return new AST2(ast1.topHead(), ast1.arg1(), head.arg1());
        case 3:
          return new AST3(ast1.topHead(), ast1.arg1(), head.arg1(), head.arg2());
        default:
          IASTAppendable result = ast(ast1.topHead(), head.size() + 1);
          result.append(ast1.arg1());
          result.appendArgs(head);
          return result;
      }
    }
    return NIL;
  }

  /**
   * The binary operator form <code>op(f, g)[expr]</code> is transformed to <code>op(expr, f, g)
   * </code>
   *
   * @param astArg an IAST with condition <code>astArg.head().isAST2() && astArg.isAST1()</code>
   * @return
   */
  public static IAST operatorFormAppend2(final IAST astArg) {
    if (astArg.head().isAST2() && astArg.isAST1()) {
      return new AST3(astArg.topHead(), astArg.arg1(), ((IAST) astArg.head()).arg1(),
          ((IAST) astArg.head()).arg2());
    }
    return NIL;
  }

  /**
   * The operator form <code>op(f)[expr]</code> is transformed to <code>op(f, expr)</code>. The
   * operator form <code>op(f)[expr1, expr2]</code> is transformed to <code>op(f, expr1, expr2)
   * </code>.
   *
   * @param ast1 an <code>IAST</code> with condition <code>
   *     ast1Arg.head().isAST1() && ast1Arg.isAST1()</code>
   * @return
   */
  public static IAST operatorForm2Prepend(final IAST ast1, int[] expected, EvalEngine engine) {
    if (ast1.head().isAST1() && ast1.argSize() > 0) {
      if (ast1.argSize() + 1 < expected[0] || ast1.argSize() + 1 > expected[1]) {
        return Errors.printArgMessage(ast1, expected, engine);
      }
      IExpr headArg1 = ast1.head().first();
      switch (ast1.size()) {
        case 2:
          return new AST2(ast1.topHead(), headArg1, ast1.arg1());
        case 3:
          return new AST3(ast1.topHead(), headArg1, ast1.arg1(), ast1.arg2());
        default:
          IASTAppendable result = ast(ast1.topHead(), ast1.size() + 1);
          result.append(headArg1);
          result.appendArgs(ast1);
          return result;
      }
    }
    return NIL;
  }

  public static IAST Matrices(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Matrices, a0, a1, a2);
  }

  public static IAST Matrices(final IExpr a0, final IExpr a1) {
    return new AST2(Matrices, a0, a1);
  }

  public static IAST Matrices(final IExpr a0) {
    return new AST1(Matrices, a0);
  }

  /**
   * Generate a <code>n x m</code> matrix. The indices start in Java convention with <code>0</code>.
   *
   * @param biFunction
   * @param n the number of rows of the matrix.
   * @param m the number of elements in one row
   * @return
   */
  public static IAST matrix(BiIntFunction<? extends IExpr> biFunction, int n, int m) {
    if (n > Config.MAX_MATRIX_DIMENSION_SIZE || m > Config.MAX_MATRIX_DIMENSION_SIZE) {
      ASTElementLimitExceeded.throwIt(((long) n) * ((long) m));
    }
    IASTAppendable matrix = mapRange(0, n, i -> mapRange(0, m, j -> biFunction.apply(i, j)));
    // because the rows can contain sub lists the IAST.IS_MATRIX flag cannot be set directly.
    // isMatrix() must be used!
    matrix.isMatrix(true);
    return matrix;
  }

  /**
   * Generate a vector with <code>n</code> elements.
   *
   * @param iFunction
   * @param n the number of elements of the vector.
   * @return
   */
  public static IAST vector(IntFunction<? extends IExpr> iFunction, int n) {
    IASTAppendable vector = mapRange(0, n, i -> iFunction.apply(i));
    vector.addEvalFlags(IAST.IS_VECTOR);
    return vector;
  }

  public static IAST Vectors(final IExpr a0, final IExpr a1) {
    return new AST2(Vectors, a0, a1);
  }

  public static IAST Vectors(final IExpr a0) {
    return new AST1(Vectors, a0);
  }

  /**
   * Parses a given string to an instance of {@link IExpr}
   *
   * <p>
   * Examples:
   *
   * <pre>
   * "7/9" -> RationalScalar.of(7, 9)
   * "3.14" -> DoubleScalar.of(3.14)
   * "(3+2)*I/(-1+4)+8-I" -> ComplexScalar.of(8, 2/3) == "8+2/3*I"
   * "9.81[m*s^-2]" -> Quantity.of(9.81, "m*s^-2")
   * </pre>
   *
   * If the parsing logic encounters an inconsistency, the return type is a {@link IStringX} that
   * holds the input string.
   *
   * <p>
   * Scalar types that are not supported include {@link GaussScalar}.
   *
   * @param string
   * @return scalar
   */
  public static IExpr fromString(String string) {
    try {
      return QuantityParser.of(string);
    } catch (Exception exception) {
      Errors.rethrowsInterruptException(exception);

    }
    return stringx(string);
  }

  public static IAST ShearingTransform(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(ShearingTransform, a0, a1, a2);
  }

  /**
   * Show the result in an HTML page with the help of the Java <code>Desktop.getDesktop().open()
   * </code> method. On some platforms the Desktop API may not be supported; use the <code>
   * isDesktopSupported()</code> method todetermine if the current desktop is supported.
   *
   * @param expr
   * @return
   * @throws IOException
   */
  public static String show(IExpr expr) {
    try {
      if (expr.isSameHeadSizeGE(Show, 2)) {
        IAST show = (IAST) expr;
        return showGraphic(show.arg1());
      }
      return showGraphic(expr);
    } catch (Exception ex) {
      Errors.rethrowsInterruptException(ex);

      LOGGER.debug("F.show() failed", ex);
    }
    return null;
  }

  public static String showGraphic(IExpr expr) {
    try {
      if (expr.isSameHeadSizeGE(Graphics, 2)) {
        StringBuilder buf = new StringBuilder();
        if (GraphicsFunctions.renderGraphics2D(buf, (IAST) expr, EvalEngine.get())) {
          try {
            String graphicsStr = buf.toString();
            String html = JSBuilder.buildGraphics2D(JSBuilder.GRAPHICS2D_TEMPLATE, graphicsStr);
            return openHTMLOnDesktop(html);
          } catch (Exception ex) {
            Errors.rethrowsInterruptException(ex);
            LOGGER.debug("JSBuilder.buildGraphics2D() failed", ex);
          }
        }
        // return openSVGOnDesktop((IAST) expr);
      } else if (expr.isSameHeadSizeGE(Graphics3D, 2)) {
        StringBuilder buf = new StringBuilder();
        if (GraphicsFunctions.renderGraphics3D(buf, (IAST) expr, EvalEngine.get())) {
          try {
            String graphics3DStr = buf.toString();
            String html = JSBuilder.buildGraphics3D(JSBuilder.GRAPHICS3D_TEMPLATE, graphics3DStr);
            return openHTMLOnDesktop(html);
          } catch (Exception ex) {
            Errors.rethrowsInterruptException(ex);
            LOGGER.debug("JSBuilder.buildGraphics3D() failed", ex);
          }
        }
      } else if (expr instanceof DataExpr) {
        String html = ((DataExpr) expr).toHTML();
        if (html != null) {
          return openHTMLOnDesktop(html);
        }
      } else if (expr.isAST(JSFormData, 3)) {
        return printJSFormData(expr);
      } else if (expr.isString()) {
        IStringX str = (IStringX) expr;
        if (str.getMimeType() == IStringX.TEXT_HTML) {
          String htmlSnippet = str.toString();
          String htmlPage = Config.HTML_PAGE;
          htmlPage = StringUtils.replace(htmlPage, "`1`", htmlSnippet);
          EvalEngine.get().getOutPrintStream().println(htmlPage);
          return openHTMLOnDesktop(htmlPage);
        }
      } else if (expr.isList(x -> x.isAST(JSFormData, 3))) {
        StringBuilder buf = new StringBuilder();
        ((IAST) expr).forEach(x -> buf.append(printJSFormData(x)));
        return buf.toString();
      }
    } catch (Exception ex) {
      Errors.rethrowsInterruptException(ex);
      LOGGER.debug("F.showGraphic() failed", ex);
    }
    return null;
  }

  private static String printJSFormData(IExpr expr) {
    IAST jsFormData = (IAST) expr;
    if (jsFormData.arg2().toString().equals(JSBuilder.MATHCELL_STR)) {
      try {
        String manipulateStr = jsFormData.arg1().toString();
        String html = JSBuilder.buildMathcell(JSBuilder.MATHCELL_TEMPLATE, manipulateStr);
        return openHTMLOnDesktop(html);
      } catch (Exception ex) {
        Errors.rethrowsInterruptException(ex);
        LOGGER.debug("F.printJSFormData() failed", ex);
      }
      // } else if (jsFormData.arg2().toString().equals("graphics3d")) {
      // try {
      // String graphics3dStr = jsFormData.arg1().toString();
      // String html = Config.GRAPHICS3D_PAGE;
      // html = StringUtils.replace(html, "`1`", graphics3dStr);
      // return openHTMLOnDesktop(html);
      // } catch (Exception ex) {
      // LOGGER.debug("F.printJSFormData() failed", ex);
      // }
    } else if (jsFormData.arg2().toString().equals(JSBuilder.ECHARTS_STR)) {
      try {
        String manipulateStr = jsFormData.arg1().toString();
        String html = JSBuilder.buildECharts(JSBuilder.ECHARTS_TEMPLATE, manipulateStr);
        return openHTMLOnDesktop(html);
      } catch (Exception ex) {
        Errors.rethrowsInterruptException(ex);
        LOGGER.debug("F.printJSFormData() failed", ex);
      }
    } else if (jsFormData.arg2().toString().equals(JSBuilder.JSXGRAPH_STR)) {
      try {
        String manipulateStr = jsFormData.arg1().toString();
        String html = JSBuilder.buildJSXGraph(JSBuilder.JSXGRAPH_TEMPLATE, manipulateStr);
        return openHTMLOnDesktop(html);
      } catch (Exception ex) {
        Errors.rethrowsInterruptException(ex);
        LOGGER.debug("F.printJSFormData() failed", ex);
      }
    } else if (jsFormData.arg2().toString().equals(JSBuilder.MERMAID_STR)) {
      try {
        String manipulateStr = jsFormData.arg1().toString();
        String html = JSBuilder.buildMermaid(JSBuilder.MERMAID_TEMPLATE, manipulateStr);
        return openHTMLOnDesktop(html);
      } catch (Exception ex) {
        Errors.rethrowsInterruptException(ex);
        LOGGER.debug("F.printJSFormData() failed", ex);
      }
    } else if (jsFormData.arg2().toString().equals(JSBuilder.PLOTLY_STR)) {
      try {
        String manipulateStr = jsFormData.arg1().toString();
        String html = JSBuilder.buildPlotly(JSBuilder.PLOTLY_TEMPLATE, manipulateStr);
        return openHTMLOnDesktop(html);
      } catch (Exception ex) {
        Errors.rethrowsInterruptException(ex);
        LOGGER.debug("F.printJSFormData() failed", ex);
      }
    } else if (jsFormData.arg2().toString().equals(JSBuilder.TREEFORM_STR)) {
      try {
        String manipulateStr = jsFormData.arg1().toString();
        String html = Config.VISJS_PAGE;
        html = StringUtils.replace(html, "`1`", manipulateStr);
        html = StringUtils.replace(html, "`2`", //
            "  var options = {\n" + "          edges: {\n" + "              smooth: {\n"
                + "                  type: 'cubicBezier',\n"
                + "                  forceDirection:  'vertical',\n"
                + "                  roundness: 0.4\n" + "              }\n" + "          },\n"
                + "          layout: {\n" + "              hierarchical: {\n"
                + "                  direction: \"UD\"\n" + "              }\n" + "          },\n"
                + "          nodes: {\n" + "            shape: 'box'\n" + "          },\n"
                + "          physics:false\n" + "      }; " //
        );
        return openHTMLOnDesktop(html);
      } catch (Exception ex) {
        Errors.rethrowsInterruptException(ex);
        LOGGER.debug("F.printJSFormData() failed", ex);
      }
    } else if (jsFormData.arg2().toString().equals(JSBuilder.TRACEFORM_STR)) {
      try {
        String jsStr = jsFormData.arg1().toString();
        String html = Config.TRACEFORM_PAGE;
        html = StringUtils.replace(html, "`1`", jsStr);
        return openHTMLOnDesktop(html);
      } catch (Exception ex) {
        Errors.rethrowsInterruptException(ex);
        LOGGER.debug("F.printJSFormData() failed", ex);
      }
    }
    return null;
  }

  // public static String openSVGOnDesktop(IAST show) throws IOException {
  // String html = Config.SVG_PAGE;
  // StringBuilder stw = new StringBuilder();
  // GraphicsFunctions.graphicsToSVG(show.getAST(1), stw);
  // html = StringUtils.replace(html, "`1`", stw.toString());
  // File temp = java.io.File.createTempFile("tempfile", ".svg");
  // try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
  // bw.write(html);
  // }
  // if (Desktop.isDesktopSupported()) {
  // Desktop.getDesktop().open(temp);
  // }
  // return temp.toString();
  // }

  public static String openHTMLOnDesktop(String html) throws IOException {
    File temp = java.io.File.createTempFile("tempfile", ".html");
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp));) {
      bw.write(html);
    }
    if (Config.JAVA_AWT_DESKTOP_AVAILABLE) {
      if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().open(temp);
      }
    }
    return temp.toString();
  }

  /**
   * Iterate over the arguments of <code>list</code> and flatten the arguments of <code>
   * Sequence(...)
   * </code> expressions. (i.e. <code>{Sequence(a,b,...)}</code> is rewritten as <code>{a,b,...}
   * </code>). If some of the elements is the symbol <code>Nothing</code> it's automatically removed
   * from the arguments.
   *
   * @param list an AST which may contain <code>Sequence(...)</code> expressions or <code>Nothing
   *     </code> symbols.
   * @return <code>F.NIL</code> if no sequence is flattened
   */
  public static IAST flattenSequence(final IAST list) {
    if (list.isEvalFlagOn(IAST.SEQUENCE_FLATTENED)) {
      return NIL;
    }

    final boolean isList = list.isList();
    final int indx = list.indexOf(x -> x.isSequence() || (isList && x == S.Nothing));
    if (indx > 0) {
      final int extraSize = list.get(indx).size();
      final IASTAppendable seqResult = F.ast(list.head(), list.size() + extraSize + 1);
      seqResult.appendArgs(list, indx);
      list.forEach(indx, list.size(), x -> {
        if (x.isSequence()) {
          seqResult.appendArgs((IAST) x);
        } else if (isList && x == S.Nothing) {
        } else {
          seqResult.append(x);
        }
      });
      return seqResult;
    }
    list.addEvalFlags(IAST.SEQUENCE_FLATTENED);
    return NIL;
  }

}
