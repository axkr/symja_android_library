package org.matheclipse.core.expression;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;
import org.hipparchus.Field;
import org.hipparchus.complex.Complex;
import org.hipparchus.fraction.BigFraction;
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
import org.matheclipse.core.builtin.ContainsFunctions;
import org.matheclipse.core.builtin.CurveFitterFunctions;
import org.matheclipse.core.builtin.EllipticIntegrals;
import org.matheclipse.core.builtin.EntityFunctions;
import org.matheclipse.core.builtin.ExpTrigsFunctions;
import org.matheclipse.core.builtin.FileFunctions;
import org.matheclipse.core.builtin.FinancialFunctions;
import org.matheclipse.core.builtin.FunctionDefinitions;
import org.matheclipse.core.builtin.GeodesyFunctions;
import org.matheclipse.core.builtin.GraphDataFunctions;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.builtin.GraphicsFunctions;
import org.matheclipse.core.builtin.HypergeometricFunctions;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.ImageFunctions;
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
import org.matheclipse.core.builtin.PolynomialFunctions;
import org.matheclipse.core.builtin.PredicateQ;
import org.matheclipse.core.builtin.Programming;
import org.matheclipse.core.builtin.QuantityFunctions;
import org.matheclipse.core.builtin.RandomFunctions;
import org.matheclipse.core.builtin.RootsFunctions;
import org.matheclipse.core.builtin.SeriesFunctions;
import org.matheclipse.core.builtin.SidesFunctions;
import org.matheclipse.core.builtin.SimplifyFunctions;
import org.matheclipse.core.builtin.SourceCodeFunctions;
import org.matheclipse.core.builtin.SparseArrayFunctions;
import org.matheclipse.core.builtin.SpecialFunctions;
import org.matheclipse.core.builtin.StatisticsFunctions;
import org.matheclipse.core.builtin.StringFunctions;
import org.matheclipse.core.builtin.StructureFunctions;
import org.matheclipse.core.builtin.TensorFunctions;
import org.matheclipse.core.builtin.UnitTestingFunctions;
import org.matheclipse.core.builtin.VectorAnalysisFunctions;
import org.matheclipse.core.builtin.WXFFunctions;
import org.matheclipse.core.builtin.WindowFunctions;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.convert.Object2Expr;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.eval.util.BiIntFunction;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.expression.data.GraphExpr;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.form.output.JSXGraphPageBuilder;
import org.matheclipse.core.generic.Functors;
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
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.parser.ExprParserFactory;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.tensor.QuantityParser;
import org.matheclipse.core.visit.VisitorLevelSpecification;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.trie.TrieMatch;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import edu.jas.kern.ComputerThreads;
import edu.jas.kern.PreemptStatus;
 
/** Factory for creating Symja predefined function expression objects (interface {@link IAST}). */
public class F extends S {

  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * In computing, memoization or memoisation is an optimization technique used primarily to speed
   * up computer programs by storing the results of expensive function calls and returning the
   * cached result when the same inputs occur again. This cache is especially used for recursive
   * integer functions to remember the results of the recursive call. See: <a
   * href="https://en.wikipedia.org/wiki/Memoization">Wikipedia - Memoization</a>
   */
  public static Cache<IAST, IExpr> REMEMBER_INTEGER_CACHE =
      CacheBuilder.newBuilder().maximumSize(500).build();

  /**
   * In computing, memoization or memoisation is an optimization technique used primarily to speed
   * up computer programs by storing the results of expensive function calls and returning the
   * cached result when the same inputs occur again.
   *
   * <p>This cache is especially used for expensive functions like <code>FullSimplify, Factor,...
   * </code> to remember the results of the function call. It often also stores the <code>F.NIL
   * </code> result to indicate that a new evaluation of a function is unnecessary. See: <a
   * href="https://en.wikipedia.org/wiki/Memoization">Wikipedia - Memoization</a>
   */
  public static Cache<IAST, IExpr> REMEMBER_AST_CACHE =
      CacheBuilder.newBuilder().maximumSize(500).build();

  /** Set to <code>true</code> at the start of initSymbols() method */
  public static volatile boolean isSystemStarted = false;

  /** Set to <code>true</code> at the end of initSymbols() method */
  public static volatile boolean isSystemInitialized = false;

  /**
   * The map for predefined strings for the {@link IExpr#internalFormString(boolean, int)} method.
   */
  public static final Map<String, String> PREDEFINED_INTERNAL_FORM_STRINGS =
      FEConfig.TRIE_STRING2STRING_BUILDER.withMatch(TrieMatch.EXACT).build();

  public static final Map<String, IPattern> PREDEFINED_PATTERN_MAP =
      Config.TRIE_STRING2PATTERN_BUILDER.withMatch(TrieMatch.EXACT).build();

  public static final Map<String, IPatternSequence> PREDEFINED_PATTERNSEQUENCE_MAP =
      Config.TRIE_STRING2PATTERNSEQUENCE_BUILDER.withMatch(TrieMatch.EXACT).build();

  public static ISymbolObserver SYMBOL_OBSERVER =
      new ISymbolObserver() {
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
  public static final IAssociation NIL = AbstractAST.NIL;

  // public final static ISymbol usage = initFinalHiddenSymbol("usage");

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
  public static final IInteger C1000 = AbstractIntegerSym.valueOf(1000);

  /**
   * Complex imaginary unit. The parsed symbol &quot;I&quot; is converted on input to this constant.
   */
  public static final IComplex CI = ComplexSym.valueOf(0, 1, 1, 1);

  /** Complex negative imaginary unit. */
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

  /** Constant double &quot;-1.0&quot; */
  public static final Num CND1 = new Num(-1.0);

  /** Constant double &quot;0.0&quot; */
  public static final Num CD0 = new Num(0.0);

  /** Constant double &quot;1.0&quot; */
  public static final Num CD1 = new Num(1.0);

  /** Complex numerical imaginary unit. */
  public static final IComplexNum CDI = ComplexNum.I;

  /** Complex negative numerical imaginary unit. */
  public static final IComplexNum CDNI = ComplexNum.NI;

  /** Represents the empty Smyja string <code>""</code> */
  public static IStringX CEmptyString;

  /** Represents <code>Sequence()</code> (i.e. the constant empty list) */
  public static IAST CEmptySequence;

  /** Represents <code>List()</code> (i.e. the constant empty list) */
  public static IAST CEmptyList;

  public static Function<IExpr, String> CNullFunction = x -> null;

  /** Represents <code>Missing("NotFound")</code> */
  public static IAST CMissingNotFound;

  /** Represents <code>List(0)</code> */
  public static IAST CListC0;

  /** Represents <code>List(1)</code> */
  public static IAST CListC1;

  /**
   * Represents <code>List(-1)</code>. Can be used to specify the &quot;leaf&quot; {@link
   * VisitorLevelSpecification} of an expression.
   */
  public static IAST CListCN1;

  /** Represents <code>List(1,1)</code> */
  public static IAST CListC1C1;

  /** Represents <code>List(1,2)</code> */
  public static IAST CListC1C2;

  /** Represents <code>List(2)</code> */
  public static IAST CListC2;

  /** Represents <code>List(2,1)</code> */
  public static IAST CListC2C1;

  /** Represents <code>List(2,2)</code> */
  public static IAST CListC2C2;

  /** Represents <code>Infinity</code> (i.e. <code>Infinity-&gt;DirectedInfinity(1)</code>) */
  public static IAST CInfinity;

  /** Represents <code>Return(False)</code> */
  public static IAST CReturnFalse;

  /** Represents <code>Return(True)</code> */
  public static IAST CReturnTrue;

  /** Represents <code>Throw(False)</code> */
  public static IAST CThrowFalse;

  /** Represents <code>Throw(True)</code> */
  public static IAST CThrowTrue;

  /**
   * Alias for CInfinity. Represents <code>Infinity</code> (i.e. <code>
   * Infinity-&gt;DirectedInfinity(1)</code>)
   */
  public static IAST oo;

  /** Represents <code>-Infinity</code> (i.e. <code>-Infinity-&gt;DirectedInfinity(-1)</code>) */
  public static IAST CNInfinity;

  /**
   * Alias for CNInfinity. Represents <code>-Infinity</code> (i.e. <code>
   * -Infinity-&gt;DirectedInfinity(-1)</code>)
   */
  public static IAST Noo;

  /** Represents <code>I*Infinity</code> (i.e. <code>I*Infinity-&gt;DirectedInfinity(I)</code>) */
  public static IAST CIInfinity;

  /**
   * Represents <code>-I*Infinity</code> (i.e. <code>-I*Infinity-&gt;DirectedInfinity(-I)</code>)
   */
  public static IAST CNIInfinity;

  /**
   * Represents <code>ComplexInfinity</code> (i.e. <code>ComplexInfinity-&gt;DirectedInfinity()
   * </code>)
   */
  public static IAST CComplexInfinity;

  /** Represents <code>-Pi</code> as Symja expression <code>Times(CN1, Pi)</code> */
  public static IAST CNPi;

  /** Represents <code>-2*Pi</code> as Symja expression <code>Times(CN2, Pi)</code> */
  public static IAST CN2Pi;

  /** Represents <code>2*Pi</code> as Symja expression <code>Times(C2, Pi)</code> */
  public static IAST C2Pi;

  /** Represents <code>-Pi/2</code> as Symja expression <code>Times(CN1D2, Pi)</code> */
  public static IAST CNPiHalf;

  /** Represents <code>Pi/2</code> as Symja expression <code>Times(C1D2, Pi)</code> */
  public static IAST CPiHalf;

  /** Represents <code>Sqrt(2)</code> */
  public static IAST CSqrt2;

  /** Represents <code>Sqrt(3)</code> */
  public static IAST CSqrt3;

  /** Represents <code>Sqrt(5)</code> */
  public static IAST CSqrt5;

  /** Represents <code>Sqrt(6)</code> */
  public static IAST CSqrt6;

  /** Represents <code>Sqrt(7)</code> */
  public static IAST CSqrt7;

  /** Represents <code>Sqrt(10)</code> */
  public static IAST CSqrt10;

  /** Represents <code>1/Sqrt(2)</code> */
  public static IAST C1DSqrt2;

  /** Represents <code>1/Sqrt(3)</code> */
  public static IAST C1DSqrt3;

  /** Represents <code>1/Sqrt(5)</code> */
  public static IAST C1DSqrt5;

  /** Represents <code>1/Sqrt(6)</code> */
  public static IAST C1DSqrt6;

  /** Represents <code>1/Sqrt(7)</code> */
  public static IAST C1DSqrt7;

  /** Represents <code>1/Sqrt(10)</code> */
  public static IAST C1DSqrt10;

  /** Represents <code>#1</code>, the first argument of a pure function. */
  public static IAST Slot1;

  /** Represents <code>#2</code>, the second argument of a pure function. */
  public static IAST Slot2;

  /** Represents <code>#3</code>, the third argument of a pure function. */
  public static IAST Slot3;

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

  public static Map<ISymbol, IExpr> UNARY_INVERSE_FUNCTIONS = new IdentityHashMap<ISymbol, IExpr>();

  public static ISymbol[] DENOMINATOR_NUMERATOR_SYMBOLS = null;

  public static IExpr[] DENOMINATOR_TRIG_TRUE_EXPRS = null;

  public static ISymbol[] NUMERATOR_NUMERATOR_SYMBOLS = null;

  public static IExpr[] NUMERATOR_TRIG_TRUE_EXPRS = null;

  private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

  /** Causes the current thread to wait until the main initialization has finished. */
  public static final void await() throws InterruptedException {
    COUNT_DOWN_LATCH.await();
  }

  static {
    try {
      AST2Expr.initialize();
      ExprParserFactory.initialize();

      PreemptStatus.setNotAllow();
      ComputerThreads.NO_THREADS = Config.JAS_NO_THREADS;

      ApfloatContext ctx = ApfloatContext.getContext();
      ctx.setNumberOfProcessors(1);

      Slot.setAttributes(ISymbol.NHOLDALL);
      Slot.setEvaluator(ICoreFunctionEvaluator.ARGS_EVALUATOR);
      SlotSequence.setAttributes(ISymbol.NHOLDALL);
      SlotSequence.setEvaluator(ICoreFunctionEvaluator.ARGS_EVALUATOR);
      PatternTest.setAttributes(ISymbol.HOLDREST);
      List.setEvaluator(ICoreFunctionEvaluator.ARGS_EVALUATOR);

      CEmptySequence = headAST0(Sequence);
      CEmptyList = headAST0(List);
      CEmptyString = $str("");
      CMissingNotFound = Missing("NotFound");
      CListC0 = new B1.List(C0);
      CListC1 = new B1.List(C1);
      CListC2 = new B1.List(C2);
      CListCN1 = new B1.List(CN1);

      CListC1C1 = new B2.List(C1, C1);
      CListC1C2 = new B2.List(C1, C2);
      CListC2C1 = new B2.List(C2, C1);
      CListC2C2 = new B2.List(C2, C2);

      CReturnFalse = new B1.Return(False);
      CReturnTrue = new B1.Return(True);
      CThrowFalse = new B1.Throw(False);
      CThrowTrue = new B1.Throw(True);

      CInfinity = unaryAST1(DirectedInfinity, C1);
      oo = CInfinity;
      CNInfinity = unaryAST1(DirectedInfinity, CN1);
      Noo = CNInfinity;
      CIInfinity = unaryAST1(DirectedInfinity, CI);
      CNIInfinity = unaryAST1(DirectedInfinity, CNI);
      CComplexInfinity = headAST0(DirectedInfinity);

      CNPi = new B2.Times(CN1, Pi);
      CN2Pi = new B2.Times(CN2, Pi);
      C2Pi = new B2.Times(C2, Pi);
      CNPiHalf = new B2.Times(CN1D2, Pi);
      CPiHalf = new B2.Times(C1D2, Pi);

      CSqrt2 = new B2.Power(C2, C1D2);
      CSqrt3 = new B2.Power(C3, C1D2);
      CSqrt5 = new B2.Power(C5, C1D2);
      CSqrt6 = new B2.Power(C6, C1D2);
      CSqrt7 = new B2.Power(C7, C1D2);
      CSqrt10 = new B2.Power(C10, C1D2);

      C1DSqrt2 = new B2.Power(C2, CN1D2);
      C1DSqrt3 = new B2.Power(C3, CN1D2);
      C1DSqrt5 = new B2.Power(C5, CN1D2);
      C1DSqrt6 = new B2.Power(C6, CN1D2);
      C1DSqrt7 = new B2.Power(C7, CN1D2);
      C1DSqrt10 = new B2.Power(C10, CN1D2);

      Slot1 = unaryAST1(Slot, C1);
      Slot2 = unaryAST1(Slot, C2);
      Slot3 = unaryAST1(Slot, C3);

      COMMON_IDS =
          new IExpr[] {
            CN1,
            CN2,
            CN3,
            CN4,
            CN5,
            CN6,
            CN7,
            CN8,
            CN9,
            CN10,
            C0,
            C1,
            C2,
            C3,
            C4,
            C5,
            C6,
            C7,
            C8,
            C9,
            C10,
            CI,
            CNI,
            C1D2,
            CN1D2,
            C1D3,
            CN1D3,
            C1D4,
            CN1D4,
            CD0,
            CD1,
            CInfinity,
            CNInfinity,
            CComplexInfinity,
            CSqrt2,
            CSqrt3,
            CSqrt5,
            CSqrt6,
            CSqrt7,
            CSqrt10,
            C1DSqrt2,
            C1DSqrt3,
            C1DSqrt5,
            C1DSqrt6,
            C1DSqrt7,
            C1DSqrt10,
            Slot1,
            Slot2,
            // start symbols
            a,
            b,
            c,
            d,
            e,
            f,
            g,
            h,
            i,
            j,
            k,
            l,
            m,
            n,
            o,
            p,
            q,
            r,
            s,
            t,
            u,
            v,
            w,
            x,
            y,
            z,
            ASymbol,
            BSymbol,
            CSymbol,
            FSymbol,
            GSymbol,
            // start pattern
            a_,
            b_,
            c_,
            d_,
            e_,
            f_,
            g_,
            h_,
            i_,
            j_,
            k_,
            l_,
            m_,
            n_,
            o_,
            p_,
            q_,
            r_,
            s_,
            t_,
            u_,
            v_,
            w_,
            x_,
            y_,
            z_,
            A_,
            B_,
            C_,
            F_,
            G_,
            a_Symbol,
            b_Symbol,
            c_Symbol,
            d_Symbol,
            e_Symbol,
            f_Symbol,
            g_Symbol,
            h_Symbol,
            i_Symbol,
            j_Symbol,
            k_Symbol,
            l_Symbol,
            m_Symbol,
            n_Symbol,
            o_Symbol,
            p_Symbol,
            q_Symbol,
            r_Symbol,
            s_Symbol,
            t_Symbol,
            u_Symbol,
            v_Symbol,
            w_Symbol,
            x_Symbol,
            y_Symbol,
            z_Symbol,
            a_DEFAULT,
            b_DEFAULT,
            c_DEFAULT,
            d_DEFAULT,
            e_DEFAULT,
            f_DEFAULT,
            g_DEFAULT,
            h_DEFAULT,
            i_DEFAULT,
            j_DEFAULT,
            k_DEFAULT,
            l_DEFAULT,
            m_DEFAULT,
            n_DEFAULT,
            o_DEFAULT,
            p_DEFAULT,
            q_DEFAULT,
            r_DEFAULT,
            s_DEFAULT,
            t_DEFAULT,
            u_DEFAULT,
            v_DEFAULT,
            w_DEFAULT,
            x_DEFAULT,
            y_DEFAULT,
            z_DEFAULT,
            A_DEFAULT,
            B_DEFAULT,
            C_DEFAULT,
            F_DEFAULT,
            G_DEFAULT
          };
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
      createDenominatorFunctionMap();
      createNumeratorFunctionMap();

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
      ImageFunctions.initialize();
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

      COUNT_DOWN_LATCH.countDown();

    } catch (Throwable th) {
      LOGGER.error("F-class initilaization failed", th);
    }
  }

  private static void createNumeratorFunctionMap() {
    NUMERATOR_NUMERATOR_SYMBOLS = new ISymbol[6];
    NUMERATOR_NUMERATOR_SYMBOLS[0] = Sin;
    NUMERATOR_NUMERATOR_SYMBOLS[1] = Cos;
    NUMERATOR_NUMERATOR_SYMBOLS[2] = Tan;
    NUMERATOR_NUMERATOR_SYMBOLS[3] = Csc;
    NUMERATOR_NUMERATOR_SYMBOLS[4] = Sec;
    NUMERATOR_NUMERATOR_SYMBOLS[5] = Cot;
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
    DENOMINATOR_NUMERATOR_SYMBOLS[0] = Sin;
    DENOMINATOR_NUMERATOR_SYMBOLS[1] = Cos;
    DENOMINATOR_NUMERATOR_SYMBOLS[2] = Tan;
    DENOMINATOR_NUMERATOR_SYMBOLS[3] = Csc;
    DENOMINATOR_NUMERATOR_SYMBOLS[4] = Sec;
    DENOMINATOR_NUMERATOR_SYMBOLS[5] = Cot;
    DENOMINATOR_TRIG_TRUE_EXPRS = new IExpr[6];
    DENOMINATOR_TRIG_TRUE_EXPRS[0] = C1;
    DENOMINATOR_TRIG_TRUE_EXPRS[1] = C1;
    DENOMINATOR_TRIG_TRUE_EXPRS[2] = Cos;
    DENOMINATOR_TRIG_TRUE_EXPRS[3] = Sin;
    DENOMINATOR_TRIG_TRUE_EXPRS[4] = Cos;
    DENOMINATOR_TRIG_TRUE_EXPRS[5] = Sin;
  }

  private static void createInverseFunctionMap() {
    UNARY_INVERSE_FUNCTIONS.put(Abs, Function(Times(CN1, Slot1)));
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
    UNARY_INVERSE_FUNCTIONS.put(Identity, Identity);

    UNARY_INVERSE_FUNCTIONS.put(Erf, InverseErf);
    UNARY_INVERSE_FUNCTIONS.put(Erfc, InverseErfc);

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
   *     optional
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
   * @param def use a default value for this pattern if necessary
   * @return IPattern
   */
  public static IPattern $p(final ISymbol symbol, boolean def) {
    return $p(symbol, null, def);
  }

  /**
   * Create a pattern for pattern-matching and term rewriting
   *
   * @param symbol
   * @param check additional condition which should be checked in pattern-matching
   * @return IPattern
   */
  public static IPattern $p(final ISymbol symbol, final IExpr check) {
    return org.matheclipse.core.expression.Pattern.valueOf(symbol, check);
  }

  /**
   * Create a pattern for pattern-matching and term rewriting
   *
   * @param symbol
   * @param check additional condition which should be checked in pattern-matching
   * @param def if <code>true</code>, the pattern can match to a default value associated with the
   *     AST's head the pattern is used in.
   * @return IPattern
   */
  public static IPattern $p(final ISymbol symbol, final IExpr check, final boolean def) {
    return org.matheclipse.core.expression.Pattern.valueOf(symbol, check, def);
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
   *     AST's head the pattern is used in.
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
   *     args has to be >= 1.
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
   *     AST's head the pattern is used in.
   * @param zeroArgsAllowed if <code>true</code> 0 argument sequences are allowed for this pattern
   * @return IPattern
   */
  public static IPatternSequence $ps(
      final ISymbol symbol, final IExpr check, final boolean def, boolean zeroArgsAllowed) {
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
  public static IPatternSequence $Repeated(
      final IExpr patternExpr, int min, int max, EvalEngine engine) {
    boolean nullAllowed = (min <= 0);
    return org.matheclipse.core.expression.RepeatedPattern.valueOf(
        patternExpr, min, max, nullAllowed, engine);
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
   * <p>is <code>True</code> if <code>x</code> is a symbol, or <code>False</code> otherwise.
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
   * @return the <code>object</code> converted to a {@link IExpr}}
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
   * Get or create a global predefined symbol which is retrieved from the SYSTEM context map or
   * created or retrieved from the SYSTEM context variables map.
   *
   * <p><b>Note:</b> user defined variables on the context path are defined with method <code>
   * userSymbol()</code>
   *
   * @param symbolName the name of the symbol
   */
  public static ISymbol $s(final String symbolName) {
    String name = symbolName;
    if (FEConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
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
      if (FEConfig.PARSER_USE_LOWERCASE_SYMBOLS
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
    return FEConfig.PARSER_USE_LOWERCASE_SYMBOLS
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

  /**
   * <code>expr1 && expr2 && expr3 ...</code> evaluates each expression in turn, returning {@link
   * S#False} as soon as an expression evaluates to {@link S#False}. If all expressions evaluate to
   * {@link S#True}, it returns {@link S#True}.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/And.md">And</a>
   *
   * @param expr
   * @return
   */
  public static IAST And(final IExpr... expr) {
    return function(And, expr);
  }

  /**
   * <code>expr1 && expr2</code> evaluates each expression in turn, returning {@link S#False} as
   * soon as an expression evaluates to {@link S#False}. If all expressions evaluate to {@link
   * S#True}, it returns {@link S#True}.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/And.md">And</a>
   *
   * @param expr1
   * @param expr2
   * @return
   */
  public static IAST And(final IExpr expr1, final IExpr expr2) {
    return new B2.And(expr1, expr2);
  }

  /**
   * <code>expr1 && expr2 && expr3</code> evaluates each expression in turn, returning {@link
   * S#False} as soon as an expression evaluates to {@link S#False}. If all expressions evaluate to
   * {@link S#True}, it returns {@link S#True}.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/And.md">And</a>
   *
   * @param expr1
   * @param expr2
   * @param expr3
   * @return
   */
  public static IAST And(final IExpr expr1, final IExpr expr2, final IExpr expr3) {
    return new AST3(And, expr1, expr2, expr3);
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

  public static IAST AppellF1(
      final IExpr a,
      final IExpr b1,
      final IExpr b2,
      final IExpr c,
      final IExpr z1,
      final IExpr z2) {
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

  /**
   * Operator <code>@@@</code>
   *
   * @param a0
   * @param a1
   * @return
   */
  public static IASTMutable ApplyListC1(final IExpr a0, final IExpr a1) {
    return new AST3(Apply, a0, a1, CListC1);
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
   * <p>tests whether expr is a full array.
   *
   * </blockquote>
   *
   * <pre>
   * 'ArrayQ(expr, pattern)
   * </pre>
   *
   * <blockquote>
   *
   * <p>also tests whether the array depth of expr matches pattern.
   *
   * </blockquote>
   *
   * <pre>
   * 'ArrayQ(expr, pattern, test)
   * </pre>
   *
   * <blockquote>
   *
   * <p>furthermore tests whether <code>test</code> yields <code>True</code> for all elements of
   * expr.
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
   * Create an association from a list of rules.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Association.md">Association</a>
   *
   * @param listOfRules
   * @return
   */
  public static IAssociation assoc(final IAST listOfRules) {
    if (listOfRules.isAST1() && listOfRules.arg1().isListOfRules(true)) {
      return new ASTAssociation((IAST) listOfRules.arg1());
    }
    return new ASTAssociation(listOfRules);
  }

  /**
   * Create an association and allocate the given capacity for the rules.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Association.md">Association</a>
   *
   * @param capacity
   * @return
   */
  public static IAssociation assoc(final int capacity) {
    return new ASTAssociation(capacity, false);
  }

  /**
   * Create a sparse array from the given rules list.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SparseArray.md">SparseArray</a>
   *
   * @param arrayRulesList
   * @return
   */
  public static ISparseArray sparseArray(final IAST arrayRulesList) {
    return SparseArrayExpr.newArrayRules(arrayRulesList, null, -1, C0);
  }

  /**
   * Creates a new AST from the given <code>ast</code> and <code>head</code>. if <code>include
   * </code> is set to <code>true </code> all arguments from index first to last-1 are copied in the
   * new list if <code>include</code> is set to <code> false </code> all arguments excluded from
   * index first to last-1 are copied in the new list
   */
  public static IAST ast(
      final IAST f, final IExpr head, final boolean include, final int first, final int last) {
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
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
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
   *     element) of the list.
   * @return
   */
  public static IASTAppendable ast(final IExpr head, final int initialCapacity) {
    return AST.newInstance(initialCapacity, head);
  }

  /**
   * Create a new abstract syntax tree (AST).
   *
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @param initialCapacity the initial capacity (i.e. number of arguments without the header
   *     element) of the list.
   * @param initNull initialize all elements with <code>null</code>.
   * @return
   * @deprecated use {@link #ast(IExpr, int)} or {@link #astMutable(IExpr, int)}
   */
  @Deprecated
  public static IASTAppendable ast(
      final IExpr head, final int initialCapacity, final boolean initNull) {
    return AST.newInstance(initialCapacity, head, initNull);
  }

  /**
   * Create a new abstract syntax tree (AST). Elements can be added at the end of the created AST
   * instance with the {@link IASTAppendable#append(IExpr)} method.
   *
   * @param head the header expression of the function. If the ast represents a function like <code>
   *     f[x,y], Sin[x],...</code>, the <code>head</code> will be an instance of type ISymbol.
   * @param initialCapacity the initial capacity (i.e. number of arguments without the header
   *     element) of the list.
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
   *     arguments without the header element) of the list.
   * @return
   */
  public static IASTAppendable ast(
      final IExpr head, Collection<? extends IExpr> collection, int initialCapacity) {
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
   *     element) of the list.
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
   *     list.
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
   *     list.
   * @return
   */
  public static IASTAppendable constantArray(
      final IExpr head, final IExpr value, final int copies) {
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
   *     zero and insert a {@link Num} real value.
   * @param arr the complex number arguments
   * @return
   */
  public static IASTAppendable ast(
      final ISymbol head, boolean evalComplex, org.hipparchus.complex.Complex[] arr) {
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
   * <p>is true if <code>x</code> is an atom (an object such as a number or string, which cannot be
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

  public static final IASTMutable binaryAST2(
      final IExpr head, final String arg1, final IExpr arg2) {
    return new AST2(head, $str(arg1), arg2);
  }

  public static final IASTMutable binaryAST2(
      final IExpr head, final String arg1, final String arg2) {
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
   * Gives symbols "True" or "False" (type ISymbol) depending on the boolean value.
   *
   * @param value
   * @return
   */
  public static ISymbol bool(final boolean value) {
    return value ? True : False;
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
   * <p>returns <code>True</code> if <code>expr</code> is either <code>True</code> or <code>False
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

  public static IAST Beta(final IExpr a0, final IExpr a1) {
    return new AST2(Beta, a0, a1);
  }

  public static IAST Beta(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Beta, a0, a1, a2);
  }

  public static IAST BetaRegularized(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(BetaRegularized, a0, a1, a2);
  }

  public static IAST BetaRegularized(
      final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(BetaRegularized, a0, a1, a2, a3);
  }

  public static IAST Break() {
    return new AST0(Break);
  }

  public static IAST C(final int index) {
    return new AST1(C, ZZ(index));
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
  public static IComplex CC(final IRational re, final IRational im) {
    return ComplexSym.valueOf(re, im);
  }

  /**
   * Create a symbolic complex number
   *
   * @param realNumerator
   * @param realDenominator
   * @param imagNumerator
   * @param imagDenominator
   * @return
   */
  public static IComplex CC(
      final long realNumerator,
      final long realDenominator,
      final long imagNumerator,
      final long imagDenominator) {
    return ComplexSym.valueOf(realNumerator, realDenominator, imagNumerator, imagDenominator);
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

  public static IAST CharacteristicPolynomial(final IExpr a0, final IExpr a1) {
    return new AST2(CharacteristicPolynomial, a0, a1);
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
      if (isZero(((INum) arg).getRealPart(), delta)) {
        return C0;
      }
    } else if (arg instanceof IComplexNum) {
      Complex c = ((IComplexNum) arg).evalComplex();
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
  public static org.hipparchus.complex.Complex chopComplex(
      org.hipparchus.complex.Complex arg, double delta) {
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

  public static IAST CentralMoment(final IExpr a0, final IExpr a1) {
    return new AST2(CentralMoment, a0, a1);
  }

  public static IAST Clear(final IExpr... a) {
    return function(Clear, a);
  }

  public static IAST ClearAttributes(final IExpr a0, final IExpr a1) {
    return new AST2(ClearAttributes, a0, a1);
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
      return a.compareTo(ZZ(i.longValue()));
    }
    IExpr temp = eval(a);
    if (temp instanceof ISignedNumber) {
      return temp.compareTo(ZZ(i.longValue()));
    }
    throw new UnsupportedOperationException(
        "compareTo() - first argument could not be converted into a signed number.");
  }

  public static int compareTo(IExpr a, java.math.BigInteger i)
      throws UnsupportedOperationException {
    if (a instanceof ISignedNumber) {
      return a.compareTo(ZZ(i));
    }
    IExpr temp = eval(a);
    if (temp instanceof ISignedNumber) {
      return temp.compareTo(ZZ(i));
    }
    throw new UnsupportedOperationException(
        "compareTo() - first argument could not be converted into a signed number.");
  }

  public static int compareTo(Integer i, IExpr b) throws UnsupportedOperationException {
    if (b instanceof ISignedNumber) {
      return ZZ(i.longValue()).compareTo(b);
    }
    IExpr temp = eval(b);
    if (temp instanceof ISignedNumber) {
      return ZZ(i.longValue()).compareTo(temp);
    }
    throw new UnsupportedOperationException(
        "compareTo() - second argument could not be converted into a signed number.");
  }

  public static int compareTo(java.math.BigInteger i, IExpr b)
      throws UnsupportedOperationException {
    if (b instanceof ISignedNumber) {
      return ZZ(i).compareTo(b);
    }
    IExpr temp = eval(b);
    if (temp instanceof ISignedNumber) {
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
   * @param realPart the real double value part which should be converted to a complex number
   * @param imagPart the imaginary double value part which should be converted to a complex number
   * @param epsilon
   * @return IFraction
   */
  public static IComplex complex(
      final double realPart, final double imagPart, final double epsilon) {
    return ComplexSym.valueOf(
        AbstractFractionSym.valueOfEpsilon(realPart, epsilon),
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
   * @param real_numerator
   * @param real_denominator
   * @param imag_numerator
   * @param imag_denominator
   * @return
   */
  public static IComplex complex(
      final long real_numerator,
      final long real_denominator,
      final long imag_numerator,
      final long imag_denominator) {
    return ComplexSym.valueOf(real_numerator, real_denominator, imag_numerator, imag_denominator);
  }

  /**
   * Create a Complex(a, b) symbolic expression?
   *
   * @param a0
   * @param a1
   * @return
   */
  public static IAST Complex(final IExpr a0, final IExpr a1) {
    return new AST2(Complex, a0, a1);
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
   * @param r the real part of the number
   * @return
   */
  public static IComplexNum complexNum(final double r) {
    return complexNum(r, 0.0);
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
      return ApcomplexNum.valueOf(
          realFraction.toBigNumerator(),
          realFraction.toBigDenominator(),
          imagFraction.toBigNumerator(),
          imagFraction.toBigDenominator());
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
      return ApcomplexNum.valueOf(
          value.toBigNumerator(), value.toBigDenominator(), BigInteger.ZERO, BigInteger.ONE);
    }
    return complexNum(value.doubleValue(), 0.0d);
  }

  public static IComplexNum complexNum(final IInteger value) {
    final EvalEngine engine = EvalEngine.get();
    if (engine.isArbitraryMode()) {
      return ApcomplexNum.valueOf(
          value.toBigNumerator(), BigInteger.ONE, BigInteger.ZERO, BigInteger.ONE);
    }
    return complexNum(value.doubleValue(), 0.0d);
  }

  /**
   * Evaluates its arguments in turn, returning the last result.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompoundExpression.md">CompoundExpression</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Condition.md">Condition</a>
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

  public static IAST CoplanarPoints(final IExpr a0) {
    return new AST1(CoplanarPoints, a0);
  }

  public static IAST CoprimeQ(final IExpr a0, final IExpr a1) {
    return new AST2(CoprimeQ, a0, a1);
  }

  /**
   * Returns the cosine of <code>z</code> (measured in <a
   * href="https://en.wikipedia.org/wiki/Radian">Radians</a>).
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cos.md">Cos</a>
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
   * Returns the cotangent of <code>z</code> (measured in <a
   * href="https://en.wikipedia.org/wiki/Radian">Radians</a>).
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cot.md">Cot</a>
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

  /**
   *
   *
   * <pre>
   * DigitQ(str)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>str</code> is a string which contains only digits.
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
   * <p>returns a list of the dimensions of the expression <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>A vector of length 3:
   *
   * <pre>
   * &gt;&gt; Dimensions({a, b, c})
   *  = {3}
   * </pre>
   *
   * <p>A 3x2 matrix:
   *
   * <pre>
   * &gt;&gt; Dimensions({{a, b}, {c, d}, {e, f}})
   *  = {3, 2}
   * </pre>
   *
   * <p>Ragged arrays are not taken into account:
   *
   * <pre>
   * &gt;&gt; Dimensions({{a, b}, {b, c}, {c, d, e}})
   * {3}
   * </pre>
   *
   * <p>The expression can have any head:
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
   * <code>DirectedEdge</code> is a directed edge from vertex <code>a</code to vertex <code>b
   * </code in a `graph` object.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DirectedEdge.md">DirectedEdge</a>
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

  /**
   * The division <code>arg1 / arg2</code> will be represented by <code>arg1 * arg2^(-1)</code>.
   *
   * @param arg1 numerator
   * @param arg2 denominator
   * @return
   */
  public static IAST Divide(final IExpr arg1, final IExpr arg2) {
    return new B2.Times(arg1, new B2.Power(arg2, CN1));
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

  public static IAST Dot(final IExpr a0, final IExpr a1) {
    return new AST2(Dot, a0, a1);
  }

  public static IAST Drop(final IExpr a0, final IExpr a1) {
    return new AST2(Drop, a0, a1);
  }

  public static IAST DSolve(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(DSolve, a0, a1, a2);
  }

  public static IAST Element(final IExpr x, final IExpr domain) {
    return new AST2(Element, x, domain);
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
   * Yields {@link S#True} if <code>lhs</code> and <code>rhs</code> are known to be equal, or {@link
   * S#False} if <code>lhs</code> and <code>rhs</code> are known to be unequal.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Equal.md">Equal</a>
   *
   * @param lhs
   * @param rhs
   * @return
   */
  public static IAST Equal(final IExpr lhs, final IExpr rhs) {
    return new B2.Equal(lhs, rhs);
  }

  /**
   * Yields {@link S#True} if <code>lhs</code> and <code>rhs</code> are known to be equal, or {@link
   * S#False} if <code>lhs</code> and <code>rhs</code> are known to be unequal.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Equal.md">Equal</a>
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
    EvalEngine engine = EvalEngine.get();
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      if (ast.isPlus()) {
        if (ast.exists(IExpr::isPlusTimesPower)) {
          return engine.evaluate(Expand(expr));
        }
      } else if (ast.isTimes() || ast.isPower()) {
        return engine.evaluate(Expand(expr));
      }
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

  /**
   *
   *
   * <pre>
   * EvenQ(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>x</code> is even, and <code>False</code> otherwise.
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
   * <p>returns <code>True</code> if <code>expr</code> is an exact number, and <code>False</code>
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
   * depending on the derived class of the given {@link Number}, the value is encoded as {@link
   * IInteger}, {@link INum}
   *
   * @param number non-null
   * @return scalar with best possible accuracy to encode given number
   * @throws Exception if number is null, or instance of an unsupported type
   */
  public static ISignedNumber expr(Number number) {
    if (number instanceof Integer
        || //
        number instanceof Long
        || //
        number instanceof Short
        || //
        number instanceof Byte) return ZZ(number.longValue());
    if (number instanceof Double
        || //
        number instanceof Float) return num(number.doubleValue());
    if (number instanceof BigInteger) return ZZ((BigInteger) number);
    throw new IllegalArgumentException(number.getClass().getName());
  }

  public static IAST EuclideanDistance(final IExpr a0, final IExpr a1) {
    return new AST2(EuclideanDistance, a0, a1);
  }

  public static IAST EulerE(final IExpr a0) {
    return new AST1(EulerE, a0);
  }

  public static IAST EulerPhi(final IExpr a0) {
    return new AST1(EulerPhi, a0);
  }

  /**
   * The exponential function <code>E^z</code>.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Exp.md">Exp</a>
   *
   * @param z
   * @return
   */
  public static IAST Exp(final IExpr z) {
    return new B2.Power(E, z);
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
  public static IExpr expand(
      IExpr a, boolean expandNegativePowers, boolean distributePlus, boolean evalParts) {
    if (a.isAST()) {
      EvalEngine engine = EvalEngine.get();
      IAST ast = engine.evalFlatOrderlessAttributesRecursive((IAST) a).orElse((IAST) a);
      return Algebra.expand(ast, null, expandNegativePowers, distributePlus, evalParts).orElse(a);
    }
    return a;
  }

  /**
   * Expands out positive rational powers and products of sums in <code>expr</code>.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expand.md">Expand</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expand.md">Expand</a>
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
      IAST ast = engine.evalFlatOrderlessAttributesRecursive((IAST) a).orElse((IAST) a);
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

  public static IAST FactorialPower(final IExpr a0, final IExpr a1) {
    return new AST2(FactorialPower, a0, a1);
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

  public static IAST FindFit(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(FindFit, a0, a1, a2, a3);
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
    return AbstractFractionSym.valueOf(value.getNumerator(), value.getDenominator());
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
   * @param value the double value which should be converted to a fractional number
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
   */
  public static IRational fraction(final long numerator, final long denominator) {
    return AbstractFractionSym.valueOf(numerator, denominator);
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FreeQ.md">FreeQ</a>
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

  public static IAST FunctionExpand(final IExpr a0) {
    return new AST1(FunctionExpand, a0);
  }

  public static IAST FunctionExpand(final IExpr a0, final IExpr a1) {
    return new AST2(FunctionExpand, a0, a1);
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

  public static IAST GammaDistribution(
      final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
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

  /**
   * Yields {@link S#True} if <code>x</code> is known to be greater than <code>y</code>.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Greater.md">Greater</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Greater.md">Greater</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterEqual.md">GreaterEqual</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterEqual.md">GreaterEqual</a>
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

  public static IAST HeavisideTheta(final IExpr a0) {
    return new AST1(HeavisideTheta, a0);
  }

  public static IAST HermitianMatrixQ(final IExpr a0) {
    return new AST1(HermitianMatrixQ, a0);
  }

  public static IAST Histogram(final IExpr a) {
    return new AST1(Histogram, a);
  }

  public static IAST Hold(final IExpr a0) {
    return new AST1(Hold, a0);
  }

  public static IAST HoldForm(final IExpr a0) {
    return new AST1(HoldForm, a0);
  }

  public static IAST HoldPattern(final IExpr a0) {
    return new AST1(HoldPattern, a0);
  }

  public static IAST HurwitzZeta(final IExpr a0, final IExpr a1) {
    return new AST2(HurwitzZeta, a0, a1);
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

  public static IAST Hypergeometric2F1(
      final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(Hypergeometric2F1, a0, a1, a2, a3);
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

  /**
   * Returns <code>trueExpr</code> if <code>condition</code> evaluates to {@link S#True}.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/If.md">If</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/If.md">If</a>
   *
   * @param condition
   * @param trueExpr
   * @param falseExpr
   * @return
   */
  public static IAST If(final IExpr condition, final IExpr trueExpr, final IExpr falseExpr) {
    return new AST3(If, condition, trueExpr, falseExpr);
  }

  /**
   * Returns <code>trueExpr</code> if <code>condition</code> evaluates to {@link S#True} and <code>
   * falseExpr</code> if it evaluates to {@link S#False} or <code>
   * undefinedExpr</code> if condition don't evakluate to {@link S#True} or {@link S#False}.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/If.md">If</a>
   *
   * @param condition
   * @param trueExpr
   * @param falseExpr
   * @return
   */
  public static IAST If(
      final IExpr condition,
      final IExpr trueExpr,
      final IExpr falseExpr,
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
    return new AST1(Im, a0);
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
   * <p>returns <code>True</code> if <code>expr</code> is not an exact number, and <code>False
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
   * <p><code>InexactNumberQ</code> can be applied to complex numbers:
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

  public static IPatternSequence initPredefinedPatternSequence(
      final ISymbol symbol, boolean zeroArgsAllowed) {
    PatternSequence temp = PatternSequence.valueOf(symbol, zeroArgsAllowed);
    PREDEFINED_PATTERNSEQUENCE_MAP.put(symbol.toString(), temp);
    return temp;
  }

  /**
   * Initialize the complete System
   *
   * @param noPackageLoading don't load any package at start up
   */
  public static synchronized void initSymbols() {

    if (!isSystemStarted) {
      try {
        isSystemStarted = true;

        if (Config.SHOW_PATTERN_EVAL_STEPS) {
          // watch the rules which are used in pattern matching in
          // system.out
          Config.SHOW_PATTERN_SYMBOL_STEPS.add(Integrate);
        }
        try {
          String autoload = ".\\Autoload";
          if (FEConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
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

        isSystemInitialized = true;
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
   * <p>returns <code>True</code> if <code>expr</code> is an integer, and <code>False</code>
   * otherwise.
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
  public static IAST IntegerQ(final IExpr a) {
    return new B1.IntegerQ(a);
  }

  /**
   * Integrates <code>f</code> with respect to <code>x</code>. The result does not contain the
   * additive integration constant.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Integrate.md">Integrate</a>
   *
   * @param f
   * @param x
   * @return
   */
  public static IAST Integrate(final IExpr f, final IExpr x) {
    return new B2.Integrate(f, x);
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
   *     internally).
   * @return
   */
  public static IASTAppendable IntervalAlloc(int capacity) {
    return ast(Interval, capacity);
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
  public static IAST intIterator(
      ISymbol head,
      final Function<IExpr, IExpr> function,
      final int from,
      final int to,
      final int step) {
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

  public static IRational sumRational(
      final IntFunction<IRational> function, final int from, final int to, final int step) {
    IRational result = C0;
    for (int i = from; i <= to; i += step) {
      result = result.add(function.apply(i));
    }
    return result;
  }

  public static IRational productRational(
      final IntFunction<IRational> function, final int from, final int to, final int step) {
    IRational result = C1;
    for (int i = from; i <= to; i += step) {
      result = result.multiply(function.apply(i));
    }
    return result;
  }

  /**
   * Iterate over an integer range <code>from <= i <= to</code> with the step <code>step/code>.
   *
   * @param head
   *            the header symbol of the result
   * @param function
   *            the integer function which should be applied on each iterator value
   * @param from
   * @param to
   * @param step
   * @return
   */
  public static IAST intIterator(
      ISymbol head,
      final IntFunction<IExpr> function,
      final int from,
      final int to,
      final int step) {
    IASTAppendable result = ast(head, to - from + 1);
    for (int i = from; i <= to; i += step) {
      result.append(function.apply(i));
    }
    return result;
  }

  public static IAST intIterator(
      ISymbol head, final Function<IExpr, IExpr> function, final IAST list) {
    IASTAppendable result = ast(head, list.size());
    for (int i = 1; i < list.size(); i++) {
      result.append(function.apply(list.get(i)));
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

  public static IAST InverseBetaRegularized(
      final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(InverseBetaRegularized, a0, a1, a2, a3);
  }

  public static IAST InverseCDF(final IExpr distribution, final IExpr q) {
    return new AST2(InverseCDF, distribution, q);
  }

  public static IAST InverseErf(final IExpr a0) {
    return new AST1(InverseErf, a0);
  }

  public static IAST InverseErfc(final IExpr a0) {
    return new AST1(InverseErfc, a0);
  }

  public static IAST InverseFunction(final IExpr a) {
    return new AST1(InverseFunction, a);
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

  public static IAST InverseLaplaceTransform(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(InverseLaplaceTransform, a0, a1, a2);
  }

  /**
   * Assign the evaluated <code>rhs</code> to the <code>lhs</code>.<br>
   * <b>Note:</b> this method returns <code>F.NIL</code>.
   *
   * @param lhs left-hand-side of the assignment
   * @param rhs right-hand-side of the assignment
   * @return <code>F.NIL</code>
   */
  public static IAST ISet(final IExpr lhs, final IExpr rhs) {
    if (lhs.isAST()) {
      ((IAST) lhs).setEvalFlags(((IAST) lhs).getEvalFlags() | IAST.IS_FLATTENED_OR_SORTED_MASK);
    }
    PatternMatching.setDownRule(IPatternMatcher.NOFLAG, lhs, rhs, true);
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
  public static IAST ISetDelayed(final IExpr lhs, final IExpr rhs) {
    if (lhs.isAST()) {
      ((IAST) lhs).setEvalFlags(((IAST) lhs).getEvalFlags() | IAST.IS_FLATTENED_OR_SORTED_MASK);
    }
    PatternMatching.setDelayedDownRule(IPatternMap.DEFAULT_RULE_PRIORITY, lhs, rhs, true);
    return NIL;
  }

  public static IAST ISetDelayed(int priority, final IExpr lhs, final IExpr rhs) {
    if (lhs.isAST()) {
      ((IAST) lhs).setEvalFlags(((IAST) lhs).getEvalFlags() | IAST.IS_FLATTENED_OR_SORTED_MASK);
    }
    PatternMatching.setDelayedDownRule(priority, lhs, rhs, true);
    return NIL;
  }

  public static IExpr IIntegrate(int priority, final IAST lhs, final IExpr rhs) {
    lhs.setEvalFlags(lhs.getEvalFlags() | IAST.IS_FLATTENED_OR_SORTED_MASK);
    org.matheclipse.core.reflection.system.Integrate.INTEGRATE_RULES_DATA.integrate(
        lhs, rhs, priority);
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
   * <p>infinity == infinity returns true eg 1/0
   *
   * <p>-infinity == infinity returns false eg -1/0
   *
   * <p>-infinity == -infinity returns true
   *
   * <p>undefined == undefined returns false eg 0/0
   *
   * @return whether x is equal to y
   */
  public static final boolean isEqual(double x, double y) {
    return isFuzzyEquals(x, y, Config.MACHINE_EPSILON);
  }

  public static final boolean isEqual(
      org.hipparchus.complex.Complex x, org.hipparchus.complex.Complex y) {
    return isFuzzyEquals(x, y, Config.MACHINE_EPSILON);
  }

  /**
   * Returns {@code true} if {@code a} and {@code b} are within {@code tolerance} (exclusive) of
   * each other.
   *
   * <p>Technically speaking, this is equivalent to {@code Math.abs(a - b) <= tolerance ||
   * Double.valueOf(a).equals(Double.valueOf(b))}.
   *
   * <p>Notable special cases include:
   *
   * <ul>
   *   <li>All NaNs are fuzzily equal.
   *   <li>If {@code a == b}, then {@code a} and {@code b} are always fuzzily equal.
   *   <li>Positive and negative zero are always fuzzily equal.
   *   <li>If {@code tolerance} is zero, and neither {@code a} nor {@code b} is NaN, then {@code a}
   *       and {@code b} are fuzzily equal if and only if {@code a == b}.
   *   <li>With {@link Double#POSITIVE_INFINITY} tolerance, all non-NaN values are fuzzily equal.
   *   <li>With finite tolerance, {@code Double.POSITIVE_INFINITY} and {@code
   *       Double.NEGATIVE_INFINITY} are fuzzily equal only to themselves.
   * </ul>
   *
   * <p>This is reflexive and symmetric, but <em>not</em> transitive, so it is <em>not</em> an
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

  public static final boolean isFuzzyEquals(
      org.hipparchus.complex.Complex x, org.hipparchus.complex.Complex y, double tolerance) {
    return isFuzzyEquals(x.getReal(), y.getReal(), tolerance) //
        && isFuzzyEquals(x.getImaginary(), y.getImaginary(), tolerance);
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
    return org.hipparchus.complex.Complex.equals(
        value, org.hipparchus.complex.Complex.ZERO, Config.MACHINE_EPSILON);
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

  /**
   * Yields {@link S#True} if <code>x</code> is known to be less than <code>y</code>.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Less.md">Less</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Less.md">Less</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Less.md">Less</a>
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

  public static IAST Less(
      final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3, final IExpr a4) {
    return quinary(Less, a0, a1, a2, a3, a4);
  }

  /**
   * Yields {@link S#True} if <code>x</code> is known to be less equal than <code>y</code>.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqual.md">LessEqual</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqual.md">LessEqual</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqual.md">LessEqual</a>
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

  public static IAST Line(final IExpr a0) {
    return new B1.Line(a0);
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

  public static IAST LinearModelFit(
      final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
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
    return size < 7 ? size : 7;
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
    return size < 15 ? size : 15;
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
    return size < 31 ? size : 31;
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
   *     internally).
   * @return
   */
  public static IASTAppendable ListAlloc(int initialCapacity) {
    return ast(List, initialCapacity);
  }

  /**
   * Create a new <code>List</code> with the capacity <code>collection.size()</code> and append the
   * elements of the collection.
   *
   * @param collection
   * @param capacity
   * @return
   */
  public static IASTMutable ListAlloc(Collection<? extends IExpr> collection) {
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

  public static IAST List(final double... numbers) {
    INum[] a = new INum[numbers.length];
    for (int i = 0; i < numbers.length; i++) {
      a[i] = num(numbers[i]);
    }
    return function(List, a);
  }

  public static IAST List(final String... strs) {
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
   * Create an immutable list <code>{ }</code> by converting the expressions into IExpr type.
   *
   * @param objects the objects which should be converted, before adding them to the list
   * @return
   * @see {@link #ListAlloc(final IExpr...)} to create an appendable list
   */
  public static IAST list(final Object... objects) {
    IExpr[] a = new IExpr[objects.length];
    for (int i = 0; i < objects.length; i++) {
      a[i] = Object2Expr.convert(objects[i], true, false);
    }
    return List(a);
  }

  /**
   * Create an immutable list <code>{ }</code>.
   *
   * @param a
   * @return
   * @see {@link #ListAlloc(final IExpr...)} to create an appendable list
   */
  public static IAST List(final IExpr... a) {
    switch (a.length) {
      case 1:
        if (a[0] != null) {
          if (a[0].equals(C0)) {
            return CListC0;
          }
          if (a[0].equals(C1)) {
            return CListC1;
          }
          if (a[0].equals(C2)) {
            return CListC2;
          }
          return new B1.List(a[0]);
        }
        break;
      case 2:
        if (a[0] != null) {
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
          return new B2.List(a[0], a[1]);
        }
        break;
      case 3:
        return new AST3(List, a[0], a[1], a[2]);
      default:
        break;
    }
    return ast(a, List);
  }

  /**
   * Return a single of value as a <code>List()</code>
   *
   * @param a
   * @return
   */
  public static IAST list(final IExpr a) {
    return new B1.List(a);
  }

  /**
   * Return a pair of values as a <code>List()</code>
   *
   * @param a0
   * @param a1
   * @return
   */
  public static IAST list(final IExpr a0, final IExpr a1) {
    return new B2.List(a0, a1);
  }

  /**
   * Return a triple of values as a <code>List()</code>
   *
   * @param a0
   * @param a1
   * @param a2
   * @return
   */
  public static IAST list(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(List, a0, a1, a2);
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

  /**
   *
   *
   * <pre>
   * ListQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>tests whether <code>expr</code> is a <code>List</code>.
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log.md">Log</a>
   *
   * @param z
   * @return
   */
  public static IAST Log(final IExpr z) {
    return new B1.Log(z);
  }

  /**
   * Returns the logarithm of <code>z</code> for the <code>base</code>.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log.md">Log</a>
   *
   * @param z
   * @return
   */
  public static IAST Log(final IExpr z, final IExpr base) {

    return new AST2(Log, z, base);
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
   * <p>returns <code>True</code> if <code>expr</code> is a machine-precision real or complex
   * number.
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

  public static IAST Map(final IExpr f) {
    return new AST1(Map, f);
  }

  public static IAST Map(final IExpr f, final IExpr expr) {
    return new AST2(Map, f, expr);
  }

  public static IAST Map(final IExpr f, final IExpr expr, final IExpr levelspec) {
    return new AST3(Map, f, expr, levelspec);
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

  public static IAST Max(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(Max, a0, a1, a2, a3);
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
   * Returns {@link S#True} if <code>form</code> matches any element of <code>list</code>, or {@link
   * S#False} otherwise.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MemberQ.md">MemberQ</a>
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

  public static IAST Min(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(Min, a0, a1, a2, a3);
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
   * <p>returns <code>True</code> if <code>expr</code> is a <code>Missing()</code> expression.
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Module.md">Module</a>
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

  /**
   * Evaluate the given expression in numeric mode
   *
   * @param a0
   * @return
   */
  public static IAST N(final IExpr a0) {
    return new AST1(N, a0);
  }

  public static IAST NakagamiDistribution(final IExpr a0, final IExpr a1) {
    return new AST2(NakagamiDistribution, a0, a1);
  }

  public static IAST Needs(final IExpr a0) {
    return new AST1(Needs, a0);
  }

  /**
   * Multiplies the given argument by <code>-1</code>. The <code>IExpr#negate()</code> method does
   * evaluations, which don't agree with pattern matching assumptions (in left-hand-sige
   * expressions). so it is only called called for <code>INumber</code> objects, otherwis a <code>
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

  public static IAST NMaximize(final IExpr a0, final IExpr a1) {
    return new AST2(NMaximize, a0, a1);
  }

  public static IAST NMinimize(final IExpr a0, final IExpr a1) {
    return new AST2(NMinimize, a0, a1);
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

  public static IAST Normalize(final IExpr a) {
    return new AST1(Normalize, a);
  }

  /**
   * Logical Not function (negation). Returns {@link S#True} if <code>expr</code> is {@link
   * S#False}. Returns {@link S#False} if <code>expr</code> is {@link S#True}.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Not.md">Not</a>
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

  public static IAST NullSpace(final IExpr a0) {
    return new AST1(NullSpace, a0);
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
    if (engine.isArbitraryMode()) {
      return ApfloatNum.valueOf(value.toBigNumerator(), value.toBigDenominator());
    }
    final double n = value.toBigNumerator().doubleValue();
    final double d = value.toBigDenominator().doubleValue();
    return num(n / d);
  }

  public static INum num(final IInteger value) {
    EvalEngine engine = EvalEngine.get();
    if (engine.isArbitraryMode()) {
      return ApfloatNum.valueOf(value.toBigNumerator());
    }
    return num(value.doubleValue());
  }

  /**
   * Create a numeric value from the input string.
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
   *
   *
   * <pre>
   * NumberQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>expr</code> is an explicit number, and <code>False</code>
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

  /**
   *
   *
   * <pre>
   * NumericQ(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns <code>True</code> if <code>expr</code> is an explicit numeric expression, and <code>
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
   * <p>returns <code>True</code> if <code>x</code> is odd, and <code>False</code> otherwise.
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Or.md">Or</a>
   *
   * @param expr1
   * @param expr2
   * @return
   */
  public static IAST Or(final IExpr expr1, final IExpr expr2) {
    return new B2.Or(expr1, expr2);
  }

  /**
   * <code>expr1 || expr2 || expr3</code> evaluates each expression in turn, returning {@link
   * S#True} as soon as an expression evaluates to {@link S#True}. If all expressions evaluate to
   * {@link S#False}, it returns {@link S#False}.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Or.md">Or</a>
   *
   * @param expr1
   * @param expr2
   * @param expr3
   * @return
   */
  public static IAST Or(final IExpr expr1, final IExpr expr2, final IExpr expr3) {
    return new AST3(Or, expr1, expr2, expr3);
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

  public static IAST Parenthesis(final IExpr a0) {
    return new AST1(Parenthesis, a0);
  }

  /**
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part</a>
   */
  public static IASTAppendable Part() {
    return ast(Part);
  }

  /**
   * Returns part <code>i</code> of <code>expr</code>.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part</a>
   *
   * @param expr
   * @param i
   * @param j
   * @return
   */
  public static IAST Part(final IExpr expr, final IExpr i, final IExpr j) {
    return new AST3(Part, expr, i, j);
  }

  /**
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part</a>
   */
  public static IASTAppendable Part(final IExpr... a) {
    return Part(0, a);
  }

  /**
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PatternTest.md">PatternTest</a>
   *
   * @param pattern
   * @param test
   * @return
   */
  public static IAST PatternTest(final IExpr pattern, final IExpr test) {
    return new AST2(PatternTest, pattern, test);
  }

  /**
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PDF.md">PDF</a>
   */
  public static IAST PDF(final IExpr distribution) {
    return new AST1(PDF, distribution);
  }

  /**
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PDF.md">PDF</a>
   */
  public static IAST PDF(final IExpr distribution, final IExpr x) {
    return new AST2(PDF, distribution, x);
  }

  /**
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Piecewise.md">Piecewise</a>
   */
  public static IAST Piecewise(final IExpr listOfConditions) {
    return new AST1(Piecewise, listOfConditions);
  }

  /**
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Piecewise.md">Piecewise</a>
   */
  public static IAST Piecewise(final IExpr listOfConditions, final IExpr defaultValue) {
    return new AST2(Piecewise, listOfConditions, defaultValue);
  }

  /**
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PiecewiseExpand.md">PiecewiseExpand</a>
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
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
   */
  public static IASTAppendable Plus() {
    return ast(Plus);
  }

  /**
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
   *
   * @param initialCapacity the initialCapacity of this AST
   * @return
   */
  public static IASTAppendable PlusAlloc(int initialCapacity) {
    return ast(Plus, initialCapacity);
  }

  /**
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
   */
  public static IASTAppendable Plus(final IExpr a0) {
    return unary(Plus, a0);
  }

  /**
   * Define a <code>Plus()</code> expression <code>a0 + a1 + a2 ...</code> for addition.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
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

  /**
   * Define a <code>Plus()</code> expression <code>x + y</code> for addition.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
   *
   * @param x
   * @param y
   * @return
   */
  public static IAST Plus(final IExpr x, final IExpr y) {
    if (x != null && y != null) {
      return plusOrderless(IExpr::isPlus, x, y);
    }
    return new B2.Plus(x, y);
  }

  /**
   * Define a <code>Plus()</code> expression <code>x + y + z</code> for addition.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
   */
  public static IAST Plus(final IExpr x, final IExpr y, final IExpr z) {
    return new AST3(Plus, x, y, z);
  }

  /**
   * Define a <code>Plus()</code> expression <code>num + a0 + a1 + a2 ...</code> for addition.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
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

  public static IAST PolyGamma(final IExpr a0) {
    return new AST1(PolyGamma, a0);
  }

  public static IAST PolyGamma(final IExpr a0, final IExpr a1) {
    return new AST2(PolyGamma, a0, a1);
  }

  public static IAST PolyLog(final IExpr a0, final IExpr a1) {
    return new AST2(PolyLog, a0, a1);
  }

  public static IAST PolynomialGCD(final IExpr a0, final IExpr a1) {
    return new AST2(PolynomialGCD, a0, a1);
  }

  /**
   * Return {@link S#True} if <code>expr</code> is a polynomial for the <code>variable</code>.
   * Return {@link S#False} in all other cases.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQ.md">PolynomialQ</a>
   *
   * @param expr
   * @param variable
   * @return
   */
  public static IAST PolynomialQ(final IExpr expr, final IExpr variable) {
    return new B2.PolynomialQ(expr, variable);
  }

  public static IAST PolynomialQuotient(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(PolynomialQuotient, a0, a1, a2);
  }

  public static IAST PolynomialQuotientRemainder(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(PolynomialQuotientRemainder, a0, a1, a2);
  }

  public static IAST PolynomialRemainder(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(PolynomialRemainder, a0, a1, a2);
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
   * Define a power expression <code>base ^ exponent</code>.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Power.md">Power</a>
   *
   * @param base
   * @param exponent
   * @return
   */
  public static IAST Power(final IExpr base, final IExpr exponent) {
    return new B2.Power(base, exponent);
  }

  /**
   * Define a power expression <code>base ^ exponent</code>.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Power.md">Power</a>
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
    if (FEConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
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

  public static IAST Product(final IExpr a0, final IExpr a1) {
    return new AST2(Product, a0, a1);
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
  public static IAST product(final Function<IExpr, IExpr> function, final int from, final int to) {
    return intIterator(Times, function, from, to, 1);
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
   * Create a "fractional" number
   *
   * @param frac a big fractional number
   * @return IFraction
   */
  public static IFraction QQ(final BigFraction frac) {
    return AbstractFractionSym.valueOf(frac);
  }

  /**
   * Create a "fractional" number
   *
   * @param numerator numerator of the fractional number
   * @param denominator denominator of the fractional number
   * @return IFraction
   */
  public static IFraction QQ(final IInteger numerator, final IInteger denominator) {
    return AbstractFractionSym.valueOf(numerator, denominator);
  }

  /**
   * Create a "fractional" number
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

  public static final IASTAppendable quaternary(
      final IExpr head, final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return new AST(new IExpr[] {head, a0, a1, a2, a3});
  }

  /**
   * Returns the Quantile of the <code>distribution</code>.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantile.md">Quantile</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantile.md">Quantile</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantile.md">Quantile</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantity.md">Quantity</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityMagnitude.md">QuantityMagnitude</a>
   *
   * @param quantity
   * @param unit
   * @return
   */
  public static IAST QuantityMagnitude(final IExpr quantity) {
    return new AST1(QuantityMagnitude, quantity);
  }

  /**
   * Returns the value of the <code>quantity</code> for the given <code>unit</code>
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityMagnitude.md">QuantityMagnitude</a>
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

  public static final IASTMutable quinary(
      final IExpr head,
      final IExpr a0,
      final IExpr a1,
      final IExpr a2,
      final IExpr a3,
      final IExpr a4) {
    return new AST(new IExpr[] {head, a0, a1, a2, a3, a4});
  }

  public static IAST Quotient(final IExpr a0, final IExpr a1) {
    return new AST2(Quotient, a0, a1);
  }

  public static IAST Quotient(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Quotient, a0, a1, a2);
  }

  public static IAST RandomComplex(final IExpr a0) {
    return new AST1(RandomComplex, a0);
  }

  public static IAST RandomInteger(final IExpr a0) {
    return new AST1(RandomInteger, a0);
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

  public static IExpr Re(final IExpr a0) {
    if (a0 != null && a0.isNumber()) {
      return ((INumber) a0).re();
    }
    return new AST1(Re, a0);
  }

  public static IAST RealAbs(final IExpr a) {
    return new AST1(RealAbs, a);
  }

  public static IAST RealNumberQ(final IExpr a) {
    return new AST1(RealNumberQ, a);
  }

  public static IAST RealSign(final IExpr a) {
    return new AST1(RealSign, a);
  }

  public static IAST Reap(final IExpr a) {
    return new AST1(Reap, a);
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

  public static IAST RGBColor(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(RGBColor, a0, a1, a2);
  }

  public static IAST RGBColor(final double a0, final double a1, final double a2) {
    return new AST3(RGBColor, num(a0), num(a1), num(a2));
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
   *     {@link F#Slot1} in the <code>assumptionAST</code> expression for this symbol.
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
   *     {@link F#Slot1} in the <code>assumptionAST</code> expression for this symbol.
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

  public static ISymbol symbol(
      final String symbolName, final String contextStr, IAST assumptionAST, EvalEngine engine) {
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
    //    ContextPath contextPath = engine.getContextPath();
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
   * Create a unique dummy symbol which is retrieved from the evaluation engines DUMMY context. A
   * &quot;Dummy&quot; symbol is not known in string parsing.
   *
   * @param symbolName the name of the symbol
   * @return the symbol object from the context path
   * @see #symbol(String)
   */
  public static ISymbol Dummy(final String symbolName) {
    String name = symbolName;
    if (FEConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      if (symbolName.length() != 1) {
        name = symbolName.toLowerCase(Locale.ENGLISH);
      }
    }
    return new Symbol(name, org.matheclipse.core.expression.Context.DUMMY);
  }

  /**
   * Create a unique dummy symbol with prefix "$", which is retrieved from the evaluation engines
   * DUMMY context. A &quot;Dummy&quot; symbol is not known in string parsing.
   *
   * @return the symbol object from the context path
   */
  public static ISymbol Dummy() {
    return Dummy(EvalEngine.uniqueName("$"));
  }

  public static IBuiltInSymbol localBiFunction(
      final String symbolName, BiFunction<IExpr, IExpr, IExpr> function) {
    IBuiltInSymbol localBuittIn = new BuiltInSymbol(symbolName, java.lang.Integer.MAX_VALUE);
    localBuittIn.setEvaluator(
        new AbstractCoreFunctionEvaluator() {
          @Override
          public IExpr evaluate(IAST ast, EvalEngine engine) {
            return function.apply(ast.arg1(), ast.arg2());
          }
        });
    return localBuittIn;
  }

  public static IBuiltInSymbol localFunction(
      final String symbolName, Function<IExpr, IExpr> function) {
    return localFunction(
        symbolName,
        new AbstractCoreFunctionEvaluator() {
          @Override
          public IExpr evaluate(IAST ast, EvalEngine engine) {
            return function.apply(ast.arg1());
          }
        });
  }

  public static IBuiltInSymbol localFunction(final String symbolName, IEvaluator evaluator) {
    IBuiltInSymbol localBuittIn = new BuiltInDummy(symbolName);
    localBuittIn.setEvaluator(evaluator);
    return localBuittIn;
  }

  public static IBuiltInSymbol localBiPredicate(
      final String symbolName, BiPredicate<IExpr, IExpr> function) {
    return localFunction(
        symbolName,
        new AbstractCoreFunctionEvaluator() {
          @Override
          public IExpr evaluate(IAST ast, EvalEngine engine) {
            return bool(function.test(ast.arg1(), ast.arg2()));
          }
        });
  }

  public static IBuiltInSymbol localPredicate(final String symbolName, Predicate<IExpr> function) {
    return localFunction(
        symbolName,
        new AbstractCoreFunctionEvaluator() {
          @Override
          public IExpr evaluate(IAST ast, EvalEngine engine) {
            return bool(function.test(ast.arg1()));
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

  public static IAST RomanNumeral(final IExpr a) {
    return new AST1(RomanNumeral, a);
  }

  public static IAST Root(final IExpr a0, final IExpr a1) {
    return new AST2(Root, a0, a1);
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

  public static IAST RowBox(final IAST list) {
    return new AST1(RowBox, list);
  }

  public static IAST RowReduce(final IExpr m) {
    return new AST1(RowReduce, m);
  }

  /**
   * Represents a rule replacing <code>lhsStr</code> with <code>rhs</code>.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rule.md">Rule</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rule.md">Rule</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rule.md">Rule</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rule.md">Rule</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RuleDelayed.md">RuleDelayed</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SameQ.md">SameQ</a>
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

  public static final IAST senary(
      final IExpr head,
      final IExpr a0,
      final IExpr a1,
      final IExpr a2,
      final IExpr a3,
      final IExpr a4,
      final IExpr a5) {
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Set.md">Set</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SetDelayed.md">SetDelayed</a>
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
   * Returns the sine of <code>z</code> (measured in <a
   * href="https://en.wikipedia.org/wiki/Radian">Radians</a>).
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sin.md">Sin</a>
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
    return new AST1(Slot, ZZ(i));
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

  /**
   * Solve an equation for a single variable.
   *
   * <p>Solve <code>100-x==0</code> for variable <code>x</code>
   *
   * <pre>
   *   ISymbol x = F.Dummy(engine);
   *   IExpr[] solutions = F.solve(F.Equal(F.Subtract(F.ZZ(100), x), F.C0), x);
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

  public static IAST SphericalHarmonicY(
      final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sqrt.md">Sqrt</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sqrt.md">Sqrt</a>
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

  public static IAST Subfactorial(final IExpr a0) {
    return new AST1(Subfactorial, a0);
  }

  public static IAST Subscript(final IExpr x, final IExpr y) {
    return new AST2(Subscript, x, y);
  }

  /**
   * Substitute all (sub-) expressions <code>x</code> with <code>y</code>. If no substitution
   * matches, the method returns the given <code>expr</code>.
   *
   * @param expr the complete expresssion
   * @param x the subexpression which should be replaced
   * @param y the expression which replaces <code>x</code>
   * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or
   *     the substituted expression.
   */
  public static IExpr subs(final IExpr expr, final IExpr x, final IExpr y) {
    return expr.replaceAll(Rule(x, y)).orElse(expr);
  }

  /**
   * Substitute all (sub-) expressions with the given unary function. If no substitution matches,
   * the method returns the given <code>expr</code>.
   *
   * @param expr
   * @param function if the unary functions <code>apply()</code> method returns <code>F.NIL</code>
   *     the expression isn't substituted.
   * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or
   *     the substituted expression.
   */
  public static IExpr subst(IExpr expr, final Function<IExpr, IExpr> function) {
    return expr.replaceAll(function).orElse(expr);
  }

  /**
   * Substitute all (sub-) expressions with the given map. If no substitution matches, the method
   * returns the given <code>expr</code>.
   *
   * @param expr
   * @param map if the maps <code>get()</code> method returns <code>null</code> the expression isn't
   *     substituted.
   * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or
   *     the substituted expression.
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
   *     left-hand-side of the rule can contain pattern objects.
   * @return the input <code>expr</code> if no substitution of a (sub-)expression was possible or
   *     the substituted expression.
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
   *     the substituted expression.
   */
  public static IExpr subst(IExpr expr, IExpr subExpr, IExpr replacementExpr) {
    return expr.replaceAll(Functors.rules(Rule(subExpr, replacementExpr), EvalEngine.get()))
        .orElse(expr);
  }

  /**
   * Return <code>x + (-1)*y</code>
   *
   * @param x
   * @param y
   * @return
   */
  public static IAST Subtract(final IExpr x, final IExpr y) {
    return new B2.Plus(x, new B2.Times(CN1, y));
  }

  public static IAST Sum(final IExpr a0, final IExpr a1) {
    return new AST2(Sum, a0, a1);
  }

  public static IAST Sum(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(Sum, a0, a1, a2);
  }

  public static IAST Sum(final IExpr a0, final IExpr a1, final IExpr a2, final IExpr a3) {
    return quaternary(Sum, a0, a1, a2, a3);
  }

  public static IRational sumRational(
      final IntFunction<IRational> function, final int iMin, final int iMax) {
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
  public static IAST sum(final Function<IExpr, IExpr> function, final int iMin, final int iMax) {
    return intIterator(Plus, function, iMin, iMax, 1);
  }

  public static IAST intSum(final IntFunction<IExpr> function, final int iMin, final int iMax) {
    return intIterator(Plus, function, iMin, iMax, 1);
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
  public static IAST sum(
      final Function<IExpr, IExpr> function, final int iMin, final int iMax, final int iStep) {
    return intIterator(Plus, function, iMin, iMax, iStep);
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
   */
  public static IAST Table(final IExpr a0, final IExpr a1) {
    return new AST2(Table, a0, a1);
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

  /**
   * Returns the tangent of <code>z</code> (measured in <a
   * href="https://en.wikipedia.org/wiki/Radian">Radians</a>).
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tan.md">Tan</a>
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
  public static final IASTMutable ternaryAST3(
      final IExpr head, final IExpr arg1, final IExpr arg2, final IExpr arg3) {
    return new AST3(head, arg1, arg2, arg3);
  }

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Thread.md">Thread</a>
   */
  public static IAST Thread(final IExpr a0) {
    return new AST1(Thread, a0);
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
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
   *
   * @return
   */
  public static IASTAppendable Times() {
    return ast(Times);
  }

  /**
   * Create a Times() function with allocated space for size elements. See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
   *
   * @param initialCapacity the initialCapacity of this AST
   * @return
   */
  public static IASTAppendable TimesAlloc(int initialCapacity) {
    return ast(Times, initialCapacity);
  }

  /**
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
   */
  public static IASTAppendable Times(final IExpr x) {
    return unary(Times, x);
  }

  /**
   * Define a <code>Times()</code> expression <code>a1 * a2 * ...</code> for multiplication.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
   */
  public static IASTMutable Times(final IExpr x, final IExpr y) {
    if (x != null && y != null) {
      return timesOrderless(IExpr::isTimes, x, y);
    }
    return new B2.Times(x, y);
  }

  /**
   * Define a <code>Times()</code> expression <code>a0 * a1 * a2</code> for multiplication.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
   */
  public static IAST Times(final IExpr x, final IExpr y, final IExpr z) {
    return new AST3(Times, x, y, z);
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

  /**
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
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
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Together.md">Together</a>
   */
  public static IAST Together(final IExpr a0) {
    return new AST1(Together, a0);
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

  /**
   * See <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Transpose.md">Transpose</a>
   */
  public static IAST Transpose(final IExpr list) {
    return new AST1(Transpose, list);
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

  /**
   * <code>UndirectedEdge</code> is an undirected edge between the vertices <code>a</code> and
   * <code>b</code> in a `graph` object.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UndirectedEdge.md">UndirectedEdge</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unequal.md">Unequal</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitConvert.md">UnitConvert</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitConvert.md">UnitConvert</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnsameQ.md">UnsameQ</a>
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

  public static IAST WeibullDistribution(final IExpr a0, final IExpr a1) {
    return new AST2(WeibullDistribution, a0, a1);
  }

  public static IAST WeibullDistribution(final IExpr a0, final IExpr a1, final IExpr a2) {
    return new AST3(WeibullDistribution, a0, a1, a2);
  }

  /**
   * Evaluates <code>body</code> as long as <code>test</code> evaluates to {@link S#True}.
   *
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/While.md">While</a>
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
   * <p>See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/With.md">With</a>
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
   * Create an integer number.
   *
   * @param integerValue
   * @return
   */
  public static IInteger ZZ(final int integerValue) {
    return AbstractIntegerSym.valueOf(integerValue);
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
      return new AST3(
          astArg.topHead(),
          astArg.arg1(),
          ((IAST) astArg.head()).arg1(),
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
        return IOFunctions.printArgMessage(ast1, expected, engine);
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
    IASTAppendable matrix = ListAlloc(n);
    for (int i = 0; i < n; i++) {
      IASTAppendable row = ListAlloc(m);
      for (int j = 0; j < m; j++) {
        row.append(biFunction.apply(i, j));
      }
      matrix.append(row);
    }
    // because the rows can contain sub lists the IAST.IS_MATRIX flag cannot be set directly.
    // isMatrix() must be
    // used!
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
    IASTAppendable matrix = ListAlloc(n);
    for (int i = 0; i < n; i++) {
      matrix.append(iFunction.apply(i));
    }
    matrix.addEvalFlags(IAST.IS_VECTOR);
    return matrix;
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
   * <p>Examples:
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
   * <p>Scalar types that are not supported include {@link GaussScalar}.
   *
   * @param string
   * @return scalar
   */
  public static IExpr fromString(String string) {
    try {
      return QuantityParser.of(string);
    } catch (Exception exception) {
      // ---
    }
    return stringx(string);
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
      LOGGER.debug("F.show() failed", ex);
    }
    return null;
  }

  public static String showGraphic(IExpr expr) {
    try {
      if (expr.isSameHeadSizeGE(Graphics, 2)) {
        return openSVGOnDesktop((IAST) expr);
      } else if (expr.isSameHeadSizeGE(Graphics3D, 2)) {
        StringBuilder buf = new StringBuilder();
        if (GraphicsFunctions.renderGraphics3D(buf, (IAST) expr, EvalEngine.get())) {
          String jsonStr = buf.toString();
          try {
            String html = Config.GRAPHICS3D_PAGE;
            html = StringUtils.replace(html, "`1`", jsonStr);
            return openHTMLOnDesktop(html);
          } catch (Exception ex) {
            LOGGER.debug("F.showGraphic() failed", ex);
          }
        }
      } else if (expr instanceof GraphExpr) {
        String javaScriptStr = GraphFunctions.graphToJSForm((GraphExpr) expr);
        if (javaScriptStr != null) {
          String html = Config.VISJS_PAGE;
          html = StringUtils.replace(html, "`1`", javaScriptStr);
          html = StringUtils.replace(html, "`2`", "var options = {};");
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
      LOGGER.debug("F.showGraphic() failed", ex);
    }
    return null;
  }

  private static String printJSFormData(IExpr expr) {
    IAST jsFormData = (IAST) expr;
    if (jsFormData.arg2().toString().equals("mathcell")) {
      try {
        String manipulateStr = jsFormData.arg1().toString();
        String html = Config.MATHCELL_PAGE;
        html = StringUtils.replace(html, "`1`", manipulateStr);
        return openHTMLOnDesktop(html);
      } catch (Exception ex) {
        LOGGER.debug("F.printJSFormData() failed", ex);
      }
      //    } else if (jsFormData.arg2().toString().equals("graphics3d")) {
      //      try {
      //        String graphics3dStr = jsFormData.arg1().toString();
      //        String html = Config.GRAPHICS3D_PAGE;
      //        html = StringUtils.replace(html, "`1`", graphics3dStr);
      //        return openHTMLOnDesktop(html);
      //      } catch (Exception ex) {
      //        LOGGER.debug("F.printJSFormData() failed", ex);
      //      }
    } else if (jsFormData.arg2().toString().equals("jsxgraph")) {
      try {
        String manipulateStr = jsFormData.arg1().toString();
        String html =
            JSXGraphPageBuilder.build(JSXGraphPageBuilder.JSXGRAPH_TEMPLATE, manipulateStr);

//        String manipulateStr = jsFormData.arg1().toString();
//        String html = Config.JSXGRAPH_PAGE;
//        html = StringUtils.replace(html, "`1`", manipulateStr);
        return openHTMLOnDesktop(html);
      } catch (Exception ex) {
        LOGGER.debug("F.printJSFormData() failed", ex);
      }
    } else if (jsFormData.arg2().toString().equals("plotly")) {
      try {
        String manipulateStr = jsFormData.arg1().toString();
        String html = Config.PLOTLY_PAGE;
        html = StringUtils.replace(html, "`1`", manipulateStr);
        return openHTMLOnDesktop(html);
      } catch (Exception ex) {
        LOGGER.debug("F.printJSFormData() failed", ex);
      }
    } else if (jsFormData.arg2().toString().equals("treeform")) {
      try {
        String manipulateStr = jsFormData.arg1().toString();
        String html = Config.VISJS_PAGE;
        html = StringUtils.replace(html, "`1`", manipulateStr);
        html =
            StringUtils.replace(
                html,
                "`2`", //
                "  var options = {\n"
                    + "          edges: {\n"
                    + "              smooth: {\n"
                    + "                  type: 'cubicBezier',\n"
                    + "                  forceDirection:  'vertical',\n"
                    + "                  roundness: 0.4\n"
                    + "              }\n"
                    + "          },\n"
                    + "          layout: {\n"
                    + "              hierarchical: {\n"
                    + "                  direction: \"UD\"\n"
                    + "              }\n"
                    + "          },\n"
                    + "          nodes: {\n"
                    + "            shape: 'box'\n"
                    + "          },\n"
                    + "          physics:false\n"
                    + "      }; " //
                );
        return openHTMLOnDesktop(html);
      } catch (Exception ex) {
        LOGGER.debug("F.printJSFormData() failed", ex);
      }
    } else if (jsFormData.arg2().toString().equals("traceform")) {
      try {
        String jsStr = jsFormData.arg1().toString();
        String html = Config.TRACEFORM_PAGE;
        html = StringUtils.replace(html, "`1`", jsStr);
        return openHTMLOnDesktop(html);
      } catch (Exception ex) {
        LOGGER.debug("F.printJSFormData() failed", ex);
      }
    }
    return null;
  }

  public static String openSVGOnDesktop(IAST show) throws IOException {
    String html = Config.SVG_PAGE;
    StringBuilder stw = new StringBuilder();
    GraphicsFunctions.graphicsToSVG(show.getAST(1), stw);
    html = StringUtils.replace(html, "`1`", stw.toString());
    File temp = java.io.File.createTempFile("tempfile", ".svg");
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
      bw.write(html);
    }
    if (Desktop.isDesktopSupported()) {
      Desktop.getDesktop().open(temp);
    }
    return temp.toString();
  }

  public static String openHTMLOnDesktop(String html) throws IOException {
    File temp = java.io.File.createTempFile("tempfile", ".html");
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp)); ) {
      bw.write(html);
    }
    if (Desktop.isDesktopSupported()) {
      Desktop.getDesktop().open(temp);
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
    int attr = list.topHead().getAttributes();
    final IASTAppendable[] seqResult = new IASTAppendable[] {NIL};

    list.forEach(
        (x, i) -> {
          if (x.isSequence()) {
            IAST seq = (IAST) x;
            if (!seqResult[0].isPresent()) {
              seqResult[0] = ast(list.head(), list.size() + seq.size());
              seqResult[0].appendArgs(list, i);
            }
            seqResult[0].appendArgs(seq);
            return;
          } else if (x.equals(Nothing)) {
            if ((ISymbol.HOLDALL & attr) == ISymbol.NOATTRIBUTE) {
              if (!seqResult[0].isPresent()) {
                seqResult[0] = ast(list.head(), list.size() - 1);
                seqResult[0].appendArgs(list, i);
              }
              return;
            }
          }
          if (seqResult[0].isPresent()) {
            seqResult[0].append(x);
          }
        });
    if (seqResult[0].isPresent()) {
      return seqResult[0];
    }
    list.addEvalFlags(IAST.SEQUENCE_FLATTENED);
    return NIL;
  }
}
