package org.matheclipse.core.expression;

import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.trie.TrieMatch;

import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;

/**
 * Class for creating the static Symja built-in symbols (interface {@link IBuiltInSymbol}). The
 * built-in symbols are generated with the tools class <code>BuiltinGenerator</code>.
 */
public class S {

  private static final IBuiltInSymbol[] BUILT_IN_SYMBOLS = new IBuiltInSymbol[ID.Zeta + 10];

  /** package private */
  static final short EXPRID_MAX_BUILTIN_LENGTH = (short) (BUILT_IN_SYMBOLS.length + 1);

  /** package private */
  static IExpr[] COMMON_IDS = null;

  /**
   * Global map of predefined constant expressions. The predefined expressions corresponding to the
   * <code>id</code> from the internal table of built-in symbols {@link #BUILT_IN_SYMBOLS} or from
   * the internal table of predefined constant expressions {@link #COMMON_IDS} mapped to the
   * corresponding expressions.
   */
  public static final Object2ShortOpenHashMap<IExpr> GLOBAL_IDS_MAP =
      new Object2ShortOpenHashMap<IExpr>(EXPRID_MAX_BUILTIN_LENGTH + 1000);

  public static final Map<String, ISymbol> HIDDEN_SYMBOLS_MAP =
      Config.TRIE_STRING2SYMBOL_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();

  public static IBuiltInSymbol symbol(int id) {
    return S.BUILT_IN_SYMBOLS[id];
  }

  public static final IBuiltInSymbol $Aborted = F.initFinalSymbol("$Aborted", ID.$Aborted);

  public static final IBuiltInSymbol $Assumptions =
      F.initFinalSymbol("$Assumptions", ID.$Assumptions);

  public static final IBuiltInSymbol $Cancel = F.initFinalSymbol("$Cancel", ID.$Cancel);

  public static final IBuiltInSymbol $Context = F.initFinalSymbol("$Context", ID.$Context);

  public static final IBuiltInSymbol $ContextPath =
      F.initFinalSymbol("$ContextPath", ID.$ContextPath);

  public static final IBuiltInSymbol $CreationDate =
      F.initFinalSymbol("$CreationDate", ID.$CreationDate);

  public static final IBuiltInSymbol $DisplayFunction =
      F.initFinalSymbol("$DisplayFunction", ID.$DisplayFunction);

  public static final IBuiltInSymbol $Failed = F.initFinalSymbol("$Failed", ID.$Failed);

  /**
   * $HistoryLength - specifies the maximum number of `In` and `Out` entries. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$HistoryLength.md">$HistoryLength</a>
   */
  public static final IBuiltInSymbol $HistoryLength =
      F.initFinalSymbol("$HistoryLength", ID.$HistoryLength);

  public static final IBuiltInSymbol $HomeDirectory =
      F.initFinalSymbol("$HomeDirectory", ID.$HomeDirectory);

  public static final IBuiltInSymbol $Input = F.initFinalSymbol("$Input", ID.$Input);

  public static final IBuiltInSymbol $InputFileName =
      F.initFinalSymbol("$InputFileName", ID.$InputFileName);

  /**
   * $IterationLimit - specifies the maximum number of times a reevaluation of an expression may
   * happen. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$IterationLimit.md">$IterationLimit</a>
   */
  public static final IBuiltInSymbol $IterationLimit =
      F.initFinalSymbol("$IterationLimit", ID.$IterationLimit);

  /**
   * $Line - holds the current input line number. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$Line.md">$Line</a>
   */
  public static final IBuiltInSymbol $Line = F.initFinalSymbol("$Line", ID.$Line);

  public static final IBuiltInSymbol $MachineEpsilon =
      F.initFinalSymbol("$MachineEpsilon", ID.$MachineEpsilon);

  public static final IBuiltInSymbol $MachinePrecision =
      F.initFinalSymbol("$MachinePrecision", ID.$MachinePrecision);

  public static final IBuiltInSymbol $MaxMachineNumber =
      F.initFinalSymbol("$MaxMachineNumber", ID.$MaxMachineNumber);

  public static final IBuiltInSymbol $MessageList =
      F.initFinalSymbol("$MessageList", ID.$MessageList);

  public static final IBuiltInSymbol $MinMachineNumber =
      F.initFinalSymbol("$MinMachineNumber", ID.$MinMachineNumber);

  public static final IBuiltInSymbol $OperatingSystem =
      F.initFinalSymbol("$OperatingSystem", ID.$OperatingSystem);

  public static final IBuiltInSymbol $OutputSizeLimit =
      F.initFinalSymbol("$OutputSizeLimit", ID.$OutputSizeLimit);

  public static final IBuiltInSymbol $Packages = F.initFinalSymbol("$Packages", ID.$Packages);

  public static final IBuiltInSymbol $Path = F.initFinalSymbol("$Path", ID.$Path);

  public static final IBuiltInSymbol $PathnameSeparator =
      F.initFinalSymbol("$PathnameSeparator", ID.$PathnameSeparator);

  public static final IBuiltInSymbol $PrePrint = F.initFinalSymbol("$PrePrint", ID.$PrePrint);

  public static final IBuiltInSymbol $PreRead = F.initFinalSymbol("$PreRead", ID.$PreRead);

  /**
   * $RecursionLimit - holds the current input line number | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$RecursionLimit.md">$RecursionLimit</a>
   */
  public static final IBuiltInSymbol $RecursionLimit =
      F.initFinalSymbol("$RecursionLimit", ID.$RecursionLimit);

  public static final IBuiltInSymbol $RootDirectory =
      F.initFinalSymbol("$RootDirectory", ID.$RootDirectory);

  public static final IBuiltInSymbol $ScriptCommandLine =
      F.initFinalSymbol("$ScriptCommandLine", ID.$ScriptCommandLine);

  public static final IBuiltInSymbol $TemporaryDirectory =
      F.initFinalSymbol("$TemporaryDirectory", ID.$TemporaryDirectory);

  public static final IBuiltInSymbol $UserName = F.initFinalSymbol("$UserName", ID.$UserName);

  public static final IBuiltInSymbol $Version = F.initFinalSymbol("$Version", ID.$Version);

  /**
   * Abort() - aborts an evaluation completely and returns `$Aborted`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Abort.md">Abort</a>
   */
  public static final IBuiltInSymbol Abort = F.initFinalSymbol("Abort", ID.Abort);

  /**
   * Abs(expr) - returns the absolute value of the real or complex number `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Abs.md">Abs</a>
   */
  public static final IBuiltInSymbol Abs = F.initFinalSymbol("Abs", ID.Abs);

  /**
   * AbsArg(expr) - returns a list of 2 values of the complex number `Abs(expr), Arg(expr)`. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AbsArg.md">AbsArg</a>
   */
  public static final IBuiltInSymbol AbsArg = F.initFinalSymbol("AbsArg", ID.AbsArg);

  public static final IBuiltInSymbol AbsoluteCorrelation =
      F.initFinalSymbol("AbsoluteCorrelation", ID.AbsoluteCorrelation);

  public static final IBuiltInSymbol AbsoluteTime =
      F.initFinalSymbol("AbsoluteTime", ID.AbsoluteTime);

  /**
   * AbsoluteTiming(x) - returns a list with the first entry containing the evaluation time of `x`
   * and the second entry is the evaluation result of `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AbsoluteTiming.md">AbsoluteTiming</a>
   */
  public static final IBuiltInSymbol AbsoluteTiming =
      F.initFinalSymbol("AbsoluteTiming", ID.AbsoluteTiming);

  /**
   * Accumulate(list) - accumulate the values of `list` returning a new list. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Accumulate.md">Accumulate</a>
   */
  public static final IBuiltInSymbol Accumulate = F.initFinalSymbol("Accumulate", ID.Accumulate);

  /**
   * AddTo(x, dx) - is equivalent to `x = x + dx`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AddTo.md">AddTo</a>
   */
  public static final IBuiltInSymbol AddTo = F.initFinalSymbol("AddTo", ID.AddTo);

  /**
   * AdjacencyMatrix(graph) - convert the `graph` into a adjacency matrix in sparse array format. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AdjacencyMatrix.md">AdjacencyMatrix</a>
   */
  public static final IBuiltInSymbol AdjacencyMatrix =
      F.initFinalSymbol("AdjacencyMatrix", ID.AdjacencyMatrix);

  /**
   * AiryAi(z) - returns the Airy function of the first kind of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryAi.md">AiryAi</a>
   */
  public static final IBuiltInSymbol AiryAi = F.initFinalSymbol("AiryAi", ID.AiryAi);

  /**
   * AiryAiPrime(z) - returns the derivative of the `AiryAi` function. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryAiPrime.md">AiryAiPrime</a>
   */
  public static final IBuiltInSymbol AiryAiPrime = F.initFinalSymbol("AiryAiPrime", ID.AiryAiPrime);

  /**
   * AiryBi(z) - returns the Airy function of the second kind of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryBi.md">AiryBi</a>
   */
  public static final IBuiltInSymbol AiryBi = F.initFinalSymbol("AiryBi", ID.AiryBi);

  /**
   * AiryBiPrime(z) - returns the derivative of the `AiryBi` function. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryBiPrime.md">AiryBiPrime</a>
   */
  public static final IBuiltInSymbol AiryBiPrime = F.initFinalSymbol("AiryBiPrime", ID.AiryBiPrime);

  public static final IBuiltInSymbol AlgebraicNumber =
      F.initFinalSymbol("AlgebraicNumber", ID.AlgebraicNumber);

  public static final IBuiltInSymbol Algebraics = F.initFinalSymbol("Algebraics", ID.Algebraics);

  /**
   * All - is a possible value for `Span` and `Quiet`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/All.md">All</a>
   */
  public static final IBuiltInSymbol All = F.initFinalSymbol("All", ID.All);

  /**
   * AllTrue({expr1, expr2, ...}, test) - returns `True` if all applications of `test` to `expr1,
   * expr2, ...` evaluate to `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AllTrue.md">AllTrue</a>
   */
  public static final IBuiltInSymbol AllTrue = F.initFinalSymbol("AllTrue", ID.AllTrue);

  public static final IBuiltInSymbol AllowedHeads =
      F.initFinalSymbol("AllowedHeads", ID.AllowedHeads);

  /**
   * Alternatives(p1, p2, ..., p_i) - is a pattern that matches any of the patterns `p1, p2,....,
   * p_i`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Alternatives.md">Alternatives</a>
   */
  public static final IBuiltInSymbol Alternatives =
      F.initFinalSymbol("Alternatives", ID.Alternatives);

  /**
   * And(expr1, expr2, ...) - `expr1 && expr2 && ...` evaluates each expression in turn, returning
   * `False` as soon as an expression evaluates to `False`. If all expressions evaluate to `True`,
   * `And` returns `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/And.md">And</a>
   */
  public static final IBuiltInSymbol And = F.initFinalSymbol("And", ID.And);

  /**
   * AngleVector(phi) - returns the point at angle `phi` on the unit circle. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AngleVector.md">AngleVector</a>
   */
  public static final IBuiltInSymbol AngleVector = F.initFinalSymbol("AngleVector", ID.AngleVector);

  /**
   * Annuity(p, t) - returns an annuity object. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Annuity.md">Annuity</a>
   */
  public static final IBuiltInSymbol Annuity = F.initFinalSymbol("Annuity", ID.Annuity);

  /**
   * AnnuityDue(p, t) - returns an annuity due object. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnnuityDue.md">AnnuityDue</a>
   */
  public static final IBuiltInSymbol AnnuityDue = F.initFinalSymbol("AnnuityDue", ID.AnnuityDue);

  public static final IBuiltInSymbol AntiSymmetric =
      F.initFinalSymbol("AntiSymmetric", ID.AntiSymmetric);

  /**
   * AntihermitianMatrixQ(m) - returns `True` if `m` is a anti hermitian matrix. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AntihermitianMatrixQ.md">AntihermitianMatrixQ</a>
   */
  public static final IBuiltInSymbol AntihermitianMatrixQ =
      F.initFinalSymbol("AntihermitianMatrixQ", ID.AntihermitianMatrixQ);

  /**
   * AntisymmetricMatrixQ(m) - returns `True` if `m` is a anti symmetric matrix. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AntisymmetricMatrixQ.md">AntisymmetricMatrixQ</a>
   */
  public static final IBuiltInSymbol AntisymmetricMatrixQ =
      F.initFinalSymbol("AntisymmetricMatrixQ", ID.AntisymmetricMatrixQ);

  /**
   * AnyTrue({expr1, expr2, ...}, test) - returns `True` if any application of `test` to `expr1,
   * expr2, ...` evaluates to `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnyTrue.md">AnyTrue</a>
   */
  public static final IBuiltInSymbol AnyTrue = F.initFinalSymbol("AnyTrue", ID.AnyTrue);

  /**
   * Apart(expr) - rewrites `expr` as a sum of individual fractions. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Apart.md">Apart</a>
   */
  public static final IBuiltInSymbol Apart = F.initFinalSymbol("Apart", ID.Apart);

  public static final IBuiltInSymbol AppellF1 = F.initFinalSymbol("AppellF1", ID.AppellF1);

  /**
   * Append(expr, item) - returns `expr` with `item` appended to its leaves. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Append.md">Append</a>
   */
  public static final IBuiltInSymbol Append = F.initFinalSymbol("Append", ID.Append);

  /**
   * AppendTo(s, item) - append `item` to value of `s` and sets `s` to the result. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AppendTo.md">AppendTo</a>
   */
  public static final IBuiltInSymbol AppendTo = F.initFinalSymbol("AppendTo", ID.AppendTo);

  /**
   * f @ expr - returns `f(expr)` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Apply.md">Apply</a>
   */
  public static final IBuiltInSymbol Apply = F.initFinalSymbol("Apply", ID.Apply);

  /**
   * ArcCos(expr) - returns the arc cosine (inverse cosine) of `expr` (measured in radians). | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCos.md">ArcCos</a>
   */
  public static final IBuiltInSymbol ArcCos = F.initFinalSymbol("ArcCos", ID.ArcCos);

  /**
   * ArcCosh(z) - returns the inverse hyperbolic cosine of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCosh.md">ArcCosh</a>
   */
  public static final IBuiltInSymbol ArcCosh = F.initFinalSymbol("ArcCosh", ID.ArcCosh);

  /**
   * ArcCot(z) - returns the inverse cotangent of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCot.md">ArcCot</a>
   */
  public static final IBuiltInSymbol ArcCot = F.initFinalSymbol("ArcCot", ID.ArcCot);

  /**
   * ArcCoth(z) - returns the inverse hyperbolic cotangent of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCoth.md">ArcCoth</a>
   */
  public static final IBuiltInSymbol ArcCoth = F.initFinalSymbol("ArcCoth", ID.ArcCoth);

  /**
   * ArcCsc(z) - returns the inverse cosecant of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCsc.md">ArcCsc</a>
   */
  public static final IBuiltInSymbol ArcCsc = F.initFinalSymbol("ArcCsc", ID.ArcCsc);

  /**
   * ArcCsch(z) - returns the inverse hyperbolic cosecant of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCsch.md">ArcCsch</a>
   */
  public static final IBuiltInSymbol ArcCsch = F.initFinalSymbol("ArcCsch", ID.ArcCsch);

  /**
   * ArcSec(z) - returns the inverse secant of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSec.md">ArcSec</a>
   */
  public static final IBuiltInSymbol ArcSec = F.initFinalSymbol("ArcSec", ID.ArcSec);

  /**
   * ArcSech(z) - returns the inverse hyperbolic secant of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSech.md">ArcSech</a>
   */
  public static final IBuiltInSymbol ArcSech = F.initFinalSymbol("ArcSech", ID.ArcSech);

  /**
   * ArcSin(expr) - returns the arc sine (inverse sine) of `expr` (measured in radians). | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSin.md">ArcSin</a>
   */
  public static final IBuiltInSymbol ArcSin = F.initFinalSymbol("ArcSin", ID.ArcSin);

  /**
   * ArcSinh(z) - returns the inverse hyperbolic sine of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSinh.md">ArcSinh</a>
   */
  public static final IBuiltInSymbol ArcSinh = F.initFinalSymbol("ArcSinh", ID.ArcSinh);

  /**
   * ArcTan(expr) - returns the arc tangent (inverse tangent) of `expr` (measured in radians). |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcTan.md">ArcTan</a>
   */
  public static final IBuiltInSymbol ArcTan = F.initFinalSymbol("ArcTan", ID.ArcTan);

  /**
   * ArcTanh(z) - returns the inverse hyperbolic tangent of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcTanh.md">ArcTanh</a>
   */
  public static final IBuiltInSymbol ArcTanh = F.initFinalSymbol("ArcTanh", ID.ArcTanh);

  /**
   * Arg(expr) - returns the argument of the complex number `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Arg.md">Arg</a>
   */
  public static final IBuiltInSymbol Arg = F.initFinalSymbol("Arg", ID.Arg);

  /**
   * ArgMax(function, variable) - returns a maximizer point for a univariate `function`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArgMax.md">ArgMax</a>
   */
  public static final IBuiltInSymbol ArgMax = F.initFinalSymbol("ArgMax", ID.ArgMax);

  /**
   * ArgMin(function, variable) - returns a minimizer point for a univariate `function`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArgMin.md">ArgMin</a>
   */
  public static final IBuiltInSymbol ArgMin = F.initFinalSymbol("ArgMin", ID.ArgMin);

  /**
   * ArithmeticGeometricMean({a, b, c,...}) - returns the arithmetic geometric mean of `{a, b,
   * c,...}`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArithmeticGeometricMean.md">ArithmeticGeometricMean</a>
   */
  public static final IBuiltInSymbol ArithmeticGeometricMean =
      F.initFinalSymbol("ArithmeticGeometricMean", ID.ArithmeticGeometricMean);

  /**
   * Array(f, n) - returns the `n`-element list `{f(1), ..., f(n)}`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Array.md">Array</a>
   */
  public static final IBuiltInSymbol Array = F.initFinalSymbol("Array", ID.Array);

  /**
   * ArrayDepth(a) - returns the depth of the non-ragged array `a`, defined as
   * `Length(Dimensions(a))`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayDepth.md">ArrayDepth</a>
   */
  public static final IBuiltInSymbol ArrayDepth = F.initFinalSymbol("ArrayDepth", ID.ArrayDepth);

  /**
   * ArrayPad(list, n) - adds `n` times `0` on the left and right of the `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayPad.md">ArrayPad</a>
   */
  public static final IBuiltInSymbol ArrayPad = F.initFinalSymbol("ArrayPad", ID.ArrayPad);

  /**
   * ArrayQ(expr) - tests whether expr is a full array. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayQ.md">ArrayQ</a>
   */
  public static final IBuiltInSymbol ArrayQ = F.initFinalSymbol("ArrayQ", ID.ArrayQ);

  /**
   * ArrayReshape(list-of-values, list-of-dimension) - returns the `list-of-values` elements
   * reshaped as nested list with dimensions according to the `list-of-dimension`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayReshape.md">ArrayReshape</a>
   */
  public static final IBuiltInSymbol ArrayReshape =
      F.initFinalSymbol("ArrayReshape", ID.ArrayReshape);

  /**
   * ArrayRules(sparse-array) - return the array of rules which define the sparse array. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayRules.md">ArrayRules</a>
   */
  public static final IBuiltInSymbol ArrayRules = F.initFinalSymbol("ArrayRules", ID.ArrayRules);

  public static final IBuiltInSymbol Arrays = F.initFinalSymbol("Arrays", ID.Arrays);

  /**
   * AssociateTo(assoc, rule) - append `rule` to the association `assoc` and assign the result to
   * `assoc`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociateTo.md">AssociateTo</a>
   */
  public static final IBuiltInSymbol AssociateTo = F.initFinalSymbol("AssociateTo", ID.AssociateTo);

  /**
   * Association(list-of-rules) - create a `key->value` association map from the `list-of-rules`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Association.md">Association</a>
   */
  public static final IBuiltInSymbol Association = F.initFinalSymbol("Association", ID.Association);

  /**
   * AssociationMap(header, <|k1->v1, k2->v2,...|>) - create an association `<|header(k1->v1),
   * header(k2->v2),...|>` with the rules mapped by the `header`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationMap.md">AssociationMap</a>
   */
  public static final IBuiltInSymbol AssociationMap =
      F.initFinalSymbol("AssociationMap", ID.AssociationMap);

  /**
   * AssociationQ(expr) - returns `True` if `expr` is an association, and `False` otherwise. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationQ.md">AssociationQ</a>
   */
  public static final IBuiltInSymbol AssociationQ =
      F.initFinalSymbol("AssociationQ", ID.AssociationQ);

  /**
   * AssociationThread({k1,k2,...}, {v1,v2,...}) - create an association with rules from the keys
   * `{k1,k2,...}` and values `{v1,v2,...}`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationThread.md">AssociationThread</a>
   */
  public static final IBuiltInSymbol AssociationThread =
      F.initFinalSymbol("AssociationThread", ID.AssociationThread);

  public static final IBuiltInSymbol Assumptions = F.initFinalSymbol("Assumptions", ID.Assumptions);

  /**
   * AtomQ(x) - is true if `x` is an atom (an object such as a number or string, which cannot be
   * divided into subexpressions using 'Part'). | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AtomQ.md">AtomQ</a>
   */
  public static final IBuiltInSymbol AtomQ = F.initFinalSymbol("AtomQ", ID.AtomQ);

  /**
   * Attributes(symbol) - returns the list of attributes which are assigned to `symbol` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Attributes.md">Attributes</a>
   */
  public static final IBuiltInSymbol Attributes = F.initFinalSymbol("Attributes", ID.Attributes);

  public static final IBuiltInSymbol Automatic = F.initFinalSymbol("Automatic", ID.Automatic);

  public static final IBuiltInSymbol Axes = F.initFinalSymbol("Axes", ID.Axes);

  public static final IBuiltInSymbol AxesOrigin = F.initFinalSymbol("AxesOrigin", ID.AxesOrigin);

  public static final IBuiltInSymbol AxesStyle = F.initFinalSymbol("AxesStyle", ID.AxesStyle);

  public static final IBuiltInSymbol BSplineFunction =
      F.initFinalSymbol("BSplineFunction", ID.BSplineFunction);

  public static final IBuiltInSymbol Background = F.initFinalSymbol("Background", ID.Background);

  /**
   * BarChart(list-of-values, options) - plot a bar chart for a `list-of-values` with option
   * `BarOrigin->Bottom` or `BarOrigin->Bottom` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BarChart.md">BarChart</a>
   */
  public static final IBuiltInSymbol BarChart = F.initFinalSymbol("BarChart", ID.BarChart);

  public static final IBuiltInSymbol BarOrigin = F.initFinalSymbol("BarOrigin", ID.BarOrigin);

  public static final IBuiltInSymbol BartlettWindow =
      F.initFinalSymbol("BartlettWindow", ID.BartlettWindow);

  /**
   * BaseDecode(string) - decodes a Base64 encoded `string` into a `ByteArray` using the Base64
   * encoding scheme. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseDecode.md">BaseDecode</a>
   */
  public static final IBuiltInSymbol BaseDecode = F.initFinalSymbol("BaseDecode", ID.BaseDecode);

  /**
   * BaseEncode(byte-array) - encodes the specified `byte-array` into a string using the Base64
   * encoding scheme. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseEncode.md">BaseEncode</a>
   */
  public static final IBuiltInSymbol BaseEncode = F.initFinalSymbol("BaseEncode", ID.BaseEncode);

  /**
   * BaseForm(integer, radix) - prints the `integer` number in base `radix` form. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseForm.md">BaseForm</a>
   */
  public static final IBuiltInSymbol BaseForm = F.initFinalSymbol("BaseForm", ID.BaseForm);

  public static final IBuiltInSymbol Beep = F.initFinalSymbol("Beep", ID.Beep);

  /**
   * Begin("<context-name>") - start a new context definition | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Begin.md">Begin</a>
   */
  public static final IBuiltInSymbol Begin = F.initFinalSymbol("Begin", ID.Begin);

  /**
   * BeginPackage("<context-name>") - start a new package definition | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BeginPackage.md">BeginPackage</a>
   */
  public static final IBuiltInSymbol BeginPackage =
      F.initFinalSymbol("BeginPackage", ID.BeginPackage);

  public static final IBuiltInSymbol BeginTestSection =
      F.initFinalSymbol("BeginTestSection", ID.BeginTestSection);

  /**
   * BellB(n) - the Bell number function counts the number of different ways to partition a set that
   * has exactly `n` elements | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BellB.md">BellB</a>
   */
  public static final IBuiltInSymbol BellB = F.initFinalSymbol("BellB", ID.BellB);

  /**
   * BellY(n, k, {x1, x2, ... , xN}) - the second kind of Bell polynomials (incomplete Bell
   * polynomials). | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BellY.md">BellY</a>
   */
  public static final IBuiltInSymbol BellY = F.initFinalSymbol("BellY", ID.BellY);

  /**
   * BernoulliB(expr) - computes the Bernoulli number of the first kind. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernoulliB.md">BernoulliB</a>
   */
  public static final IBuiltInSymbol BernoulliB = F.initFinalSymbol("BernoulliB", ID.BernoulliB);

  /**
   * BernoulliDistribution(p) - returns the Bernoulli distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernoulliDistribution.md">BernoulliDistribution</a>
   */
  public static final IBuiltInSymbol BernoulliDistribution =
      F.initFinalSymbol("BernoulliDistribution", ID.BernoulliDistribution);

  /**
   * BernsteinBasis(n, v, expr) - computes the Bernstein basis for the expression `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernsteinBasis.md">BernsteinBasis</a>
   */
  public static final IBuiltInSymbol BernsteinBasis =
      F.initFinalSymbol("BernsteinBasis", ID.BernsteinBasis);

  /**
   * BesselI(n, z) - modified Bessel function of the first kind. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselI.md">BesselI</a>
   */
  public static final IBuiltInSymbol BesselI = F.initFinalSymbol("BesselI", ID.BesselI);

  /**
   * BesselJ(n, z) - Bessel function of the first kind. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselJ.md">BesselJ</a>
   */
  public static final IBuiltInSymbol BesselJ = F.initFinalSymbol("BesselJ", ID.BesselJ);

  /**
   * BesselJZero(n, z) - is the `k`th zero of the `BesselJ(n,z)` function. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselJZero.md">BesselJZero</a>
   */
  public static final IBuiltInSymbol BesselJZero = F.initFinalSymbol("BesselJZero", ID.BesselJZero);

  /**
   * BesselK(n, z) - modified Bessel function of the second kind. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselK.md">BesselK</a>
   */
  public static final IBuiltInSymbol BesselK = F.initFinalSymbol("BesselK", ID.BesselK);

  /**
   * BesselY(n, z) - Bessel function of the second kind. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselY.md">BesselY</a>
   */
  public static final IBuiltInSymbol BesselY = F.initFinalSymbol("BesselY", ID.BesselY);

  /**
   * BesselYZero(n, z) - is the `k`th zero of the `BesselY(n,z)` function. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselYZero.md">BesselYZero</a>
   */
  public static final IBuiltInSymbol BesselYZero = F.initFinalSymbol("BesselYZero", ID.BesselYZero);

  /**
   * Beta(a, b) - is the beta function of the numbers `a`,`b`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Beta.md">Beta</a>
   */
  public static final IBuiltInSymbol Beta = F.initFinalSymbol("Beta", ID.Beta);

  public static final IBuiltInSymbol BetaDistribution =
      F.initFinalSymbol("BetaDistribution", ID.BetaDistribution);

  public static final IBuiltInSymbol BetaRegularized =
      F.initFinalSymbol("BetaRegularized", ID.BetaRegularized);

  /**
   * BetweennessCentrality(graph) - Computes the betweenness centrality of each vertex of a `graph`.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BetweennessCentrality.md">BetweennessCentrality</a>
   */
  public static final IBuiltInSymbol BetweennessCentrality =
      F.initFinalSymbol("BetweennessCentrality", ID.BetweennessCentrality);

  public static final IBuiltInSymbol BezierFunction =
      F.initFinalSymbol("BezierFunction", ID.BezierFunction);

  /**
   * BinCounts(list, width-of-bin) - count the number of elements, if `list`, is divided into
   * successive bins with width `width-of-bin`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinCounts.md">BinCounts</a>
   */
  public static final IBuiltInSymbol BinCounts = F.initFinalSymbol("BinCounts", ID.BinCounts);

  /**
   * BinaryDeserialize(byte-array) - deserialize the `byte-array` from WXF format into a Symja
   * expression. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinaryDeserialize.md">BinaryDeserialize</a>
   */
  public static final IBuiltInSymbol BinaryDeserialize =
      F.initFinalSymbol("BinaryDeserialize", ID.BinaryDeserialize);

  /**
   * BinaryDistance(u, v) - returns the binary distance between `u` and `v`. `0` if `u` and `v` are
   * unequal. `1` if `u` and `v` are equal. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinaryDistance.md">BinaryDistance</a>
   */
  public static final IBuiltInSymbol BinaryDistance =
      F.initFinalSymbol("BinaryDistance", ID.BinaryDistance);

  /**
   * BinarySerialize(expr) - serialize the Symja `expr` into a byte array expression in WXF format.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinarySerialize.md">BinarySerialize</a>
   */
  public static final IBuiltInSymbol BinarySerialize =
      F.initFinalSymbol("BinarySerialize", ID.BinarySerialize);

  /**
   * Binomial(n, k) - returns the binomial coefficient of the 2 integers `n` and `k` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Binomial.md">Binomial</a>
   */
  public static final IBuiltInSymbol Binomial = F.initFinalSymbol("Binomial", ID.Binomial);

  /**
   * BinomialDistribution(n, p) - returns the binomial distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinomialDistribution.md">BinomialDistribution</a>
   */
  public static final IBuiltInSymbol BinomialDistribution =
      F.initFinalSymbol("BinomialDistribution", ID.BinomialDistribution);

  /**
   * BitLengthi(x) - gives the number of bits needed to represent the integer `x`. The sign of `x`
   * is ignored. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BitLength.md">BitLength</a>
   */
  public static final IBuiltInSymbol BitLength = F.initFinalSymbol("BitLength", ID.BitLength);

  public static final IBuiltInSymbol Black = F.initFinalSymbol("Black", ID.Black);

  public static final IBuiltInSymbol BlackmanHarrisWindow =
      F.initFinalSymbol("BlackmanHarrisWindow", ID.BlackmanHarrisWindow);

  public static final IBuiltInSymbol BlackmanNuttallWindow =
      F.initFinalSymbol("BlackmanNuttallWindow", ID.BlackmanNuttallWindow);

  public static final IBuiltInSymbol BlackmanWindow =
      F.initFinalSymbol("BlackmanWindow", ID.BlackmanWindow);

  public static final IBuiltInSymbol Blank = F.initFinalSymbol("Blank", ID.Blank);

  public static final IBuiltInSymbol BlankNullSequence =
      F.initFinalSymbol("BlankNullSequence", ID.BlankNullSequence);

  public static final IBuiltInSymbol BlankSequence =
      F.initFinalSymbol("BlankSequence", ID.BlankSequence);

  /**
   * Block({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables` |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Block.md">Block</a>
   */
  public static final IBuiltInSymbol Block = F.initFinalSymbol("Block", ID.Block);

  public static final IBuiltInSymbol Blue = F.initFinalSymbol("Blue", ID.Blue);

  /**
   * Boole(expr) - returns `1` if `expr` evaluates to `True`; returns `0` if `expr` evaluates to
   * `False`; and gives no result otherwise. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Boole.md">Boole</a>
   */
  public static final IBuiltInSymbol Boole = F.initFinalSymbol("Boole", ID.Boole);

  /**
   * BooleanConvert(logical-expr) - convert the `logical-expr` to [disjunctive normal
   * form](https://en.wikipedia.org/wiki/Disjunctive_normal_form) | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanConvert.md">BooleanConvert</a>
   */
  public static final IBuiltInSymbol BooleanConvert =
      F.initFinalSymbol("BooleanConvert", ID.BooleanConvert);

  /**
   * BooleanMinimize(expr) - minimizes a boolean function with the [Quine McCluskey
   * algorithm](https://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm) | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanMinimize.md">BooleanMinimize</a>
   */
  public static final IBuiltInSymbol BooleanMinimize =
      F.initFinalSymbol("BooleanMinimize", ID.BooleanMinimize);

  /**
   * BooleanQ(expr) - returns `True` if `expr` is either `True` or `False`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanQ.md">BooleanQ</a>
   */
  public static final IBuiltInSymbol BooleanQ = F.initFinalSymbol("BooleanQ", ID.BooleanQ);

  /**
   * BooleanTable(logical-expr, variables) - generate [truth
   * values](https://en.wikipedia.org/wiki/Truth_table) from the `logical-expr` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanTable.md">BooleanTable</a>
   */
  public static final IBuiltInSymbol BooleanTable =
      F.initFinalSymbol("BooleanTable", ID.BooleanTable);

  /**
   * BooleanVariables(logical-expr) - gives a list of the boolean variables that appear in the
   * `logical-expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanVariables.md">BooleanVariables</a>
   */
  public static final IBuiltInSymbol BooleanVariables =
      F.initFinalSymbol("BooleanVariables", ID.BooleanVariables);

  /**
   * Booleans - is the set of boolean values. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Booleans.md">Booleans</a>
   */
  public static final IBuiltInSymbol Booleans = F.initFinalSymbol("Booleans", ID.Booleans);

  public static final IBuiltInSymbol Bottom = F.initFinalSymbol("Bottom", ID.Bottom);

  /**
   * BoxWhiskerChart( ) - plot a box whisker chart. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BoxWhiskerChart.md">BoxWhiskerChart</a>
   */
  public static final IBuiltInSymbol BoxWhiskerChart =
      F.initFinalSymbol("BoxWhiskerChart", ID.BoxWhiskerChart);

  /**
   * BrayCurtisDistance(u, v) - returns the Bray Curtis distance between `u` and `v`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BrayCurtisDistance.md">BrayCurtisDistance</a>
   */
  public static final IBuiltInSymbol BrayCurtisDistance =
      F.initFinalSymbol("BrayCurtisDistance", ID.BrayCurtisDistance);

  /**
   * Break() - exits a `For`, `While`, or `Do` loop. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Break.md">Break</a>
   */
  public static final IBuiltInSymbol Break = F.initFinalSymbol("Break", ID.Break);

  public static final IBuiltInSymbol Brown = F.initFinalSymbol("Brown", ID.Brown);

  public static final IBuiltInSymbol Button = F.initFinalSymbol("Button", ID.Button);

  /**
   * ByteArray({list-of-byte-values}) - converts the `list-of-byte-values` into a byte array. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ByteArray.md">ByteArray</a>
   */
  public static final IBuiltInSymbol ByteArray = F.initFinalSymbol("ByteArray", ID.ByteArray);

  /**
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ByteArrayQ.md">ByteArrayQ</a>
   */
  public static final IBuiltInSymbol ByteArrayQ = F.initFinalSymbol("ByteArrayQ", ID.ByteArrayQ);

  /**
   * ByteArrayToString(byte-array) - decoding the specified `byte-array` using the default character
   * set `UTF-8`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ByteArrayToString.md">ByteArrayToString</a>
   */
  public static final IBuiltInSymbol ByteArrayToString =
      F.initFinalSymbol("ByteArrayToString", ID.ByteArrayToString);

  public static final IBuiltInSymbol ByteCount = F.initFinalSymbol("ByteCount", ID.ByteCount);

  /**
   * C(n) - represents the `n`-th constant in a solution to a differential equation. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/C.md">C</a>
   */
  public static final IBuiltInSymbol C = F.initFinalSymbol("C", ID.C);

  /**
   * CDF(distribution, value) - returns the cumulative distribution function of `value`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CDF.md">CDF</a>
   */
  public static final IBuiltInSymbol CDF = F.initFinalSymbol("CDF", ID.CDF);

  public static final IBuiltInSymbol CForm = F.initFinalSymbol("CForm", ID.CForm);

  public static final IBuiltInSymbol CMYColor = F.initFinalSymbol("CMYColor", ID.CMYColor);

  /**
   * CanberraDistance(u, v) - returns the canberra distance between `u` and `v`, which is a weighted
   * version of the Manhattan distance. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CanberraDistance.md">CanberraDistance</a>
   */
  public static final IBuiltInSymbol CanberraDistance =
      F.initFinalSymbol("CanberraDistance", ID.CanberraDistance);

  /**
   * Cancel(expr) - cancels out common factors in numerators and denominators. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cancel.md">Cancel</a>
   */
  public static final IBuiltInSymbol Cancel = F.initFinalSymbol("Cancel", ID.Cancel);

  public static final IBuiltInSymbol CancelButton =
      F.initFinalSymbol("CancelButton", ID.CancelButton);

  /**
   * CarmichaelLambda(n) - the Carmichael function of `n` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarmichaelLambda.md">CarmichaelLambda</a>
   */
  public static final IBuiltInSymbol CarmichaelLambda =
      F.initFinalSymbol("CarmichaelLambda", ID.CarmichaelLambda);

  /**
   * CartesianProduct(list1, list2) - returns the cartesian product for multiple lists. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CartesianProduct.md">CartesianProduct</a>
   */
  public static final IBuiltInSymbol CartesianProduct =
      F.initFinalSymbol("CartesianProduct", ID.CartesianProduct);

  /**
   * Cases(list, pattern) - returns the elements of `list` that match `pattern`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cases.md">Cases</a>
   */
  public static final IBuiltInSymbol Cases = F.initFinalSymbol("Cases", ID.Cases);

  /**
   * Catalan - Catalan's constant | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Catalan.md">Catalan</a>
   */
  public static final IBuiltInSymbol Catalan = F.initFinalSymbol("Catalan", ID.Catalan);

  /**
   * CatalanNumber(n) - returns the catalan number for the integer argument `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CatalanNumber.md">CatalanNumber</a>
   */
  public static final IBuiltInSymbol CatalanNumber =
      F.initFinalSymbol("CatalanNumber", ID.CatalanNumber);

  public static final IBuiltInSymbol Catch = F.initFinalSymbol("Catch", ID.Catch);

  /**
   * Catenate({l1, l2, ...}) - concatenates the lists `l1, l2, ...` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Catenate.md">Catenate</a>
   */
  public static final IBuiltInSymbol Catenate = F.initFinalSymbol("Catenate", ID.Catenate);

  /**
   * Ceiling(expr) - gives the first integer greater than or equal `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ceiling.md">Ceiling</a>
   */
  public static final IBuiltInSymbol Ceiling = F.initFinalSymbol("Ceiling", ID.Ceiling);

  public static final IBuiltInSymbol CenterDot = F.initFinalSymbol("CenterDot", ID.CenterDot);

  /**
   * CentralMoment(list, r) - gives the the `r`th central moment (i.e. the `r`th moment about the
   * mean) of `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CentralMoment.md">CentralMoment</a>
   */
  public static final IBuiltInSymbol CentralMoment =
      F.initFinalSymbol("CentralMoment", ID.CentralMoment);

  public static final IBuiltInSymbol CharacterEncoding =
      F.initFinalSymbol("CharacterEncoding", ID.CharacterEncoding);

  /**
   * CharacterRange(min-character, max-character) - computes a list of character strings from
   * `min-character` to `max-character` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CharacterRange.md">CharacterRange</a>
   */
  public static final IBuiltInSymbol CharacterRange =
      F.initFinalSymbol("CharacterRange", ID.CharacterRange);

  /**
   * CharacteristicPolynomial(matrix, var) - computes the characteristic polynomial of a `matrix`
   * for the variable `var`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CharacteristicPolynomial.md">CharacteristicPolynomial</a>
   */
  public static final IBuiltInSymbol CharacteristicPolynomial =
      F.initFinalSymbol("CharacteristicPolynomial", ID.CharacteristicPolynomial);

  public static final IBuiltInSymbol Characters = F.initFinalSymbol("Characters", ID.Characters);

  /**
   * ChebyshevT(n, x) - returns the Chebyshev polynomial of the first kind `T_n(x)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChebyshevT.md">ChebyshevT</a>
   */
  public static final IBuiltInSymbol ChebyshevT = F.initFinalSymbol("ChebyshevT", ID.ChebyshevT);

  /**
   * ChebyshevU(n, x) - returns the Chebyshev polynomial of the second kind `U_n(x)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChebyshevU.md">ChebyshevU</a>
   */
  public static final IBuiltInSymbol ChebyshevU = F.initFinalSymbol("ChebyshevU", ID.ChebyshevU);

  /**
   * Check(expr, failure) - evaluates `expr`, and returns the result, unless messages were
   * generated, in which case `failure` will be returned. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Check.md">Check</a>
   */
  public static final IBuiltInSymbol Check = F.initFinalSymbol("Check", ID.Check);

  /**
   * ChessboardDistance(u, v) - returns the chessboard distance (also known as Chebyshev distance)
   * between `u` and `v`, which is the number of moves a king on a chessboard needs to get from
   * square `u` to square `v`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChessboardDistance.md">ChessboardDistance</a>
   */
  public static final IBuiltInSymbol ChessboardDistance =
      F.initFinalSymbol("ChessboardDistance", ID.ChessboardDistance);

  public static final IBuiltInSymbol ChiSquareDistribution =
      F.initFinalSymbol("ChiSquareDistribution", ID.ChiSquareDistribution);

  /**
   * ChineseRemainder({a1, a2, a3,...}, {n1, n2, n3,...}) - the chinese remainder function. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChineseRemainder.md">ChineseRemainder</a>
   */
  public static final IBuiltInSymbol ChineseRemainder =
      F.initFinalSymbol("ChineseRemainder", ID.ChineseRemainder);

  /**
   * CholeskyDecomposition(matrix) - calculate the Cholesky decomposition of a hermitian, positive
   * definite square `matrix`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CholeskyDecomposition.md">CholeskyDecomposition</a>
   */
  public static final IBuiltInSymbol CholeskyDecomposition =
      F.initFinalSymbol("CholeskyDecomposition", ID.CholeskyDecomposition);

  /**
   * Chop(numerical-expr) - replaces numerical values in the `numerical-expr` which are close to
   * zero with symbolic value `0`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Chop.md">Chop</a>
   */
  public static final IBuiltInSymbol Chop = F.initFinalSymbol("Chop", ID.Chop);

  public static final IBuiltInSymbol CircleDot = F.initFinalSymbol("CircleDot", ID.CircleDot);

  /**
   * CirclePoints(i) - gives the `i` points on the unit circle for a positive integer `i`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CirclePoints.md">CirclePoints</a>
   */
  public static final IBuiltInSymbol CirclePoints =
      F.initFinalSymbol("CirclePoints", ID.CirclePoints);

  /**
   * Clear(symbol1, symbol2,...) - clears all values of the given symbols. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Clear.md">Clear</a>
   */
  public static final IBuiltInSymbol Clear = F.initFinalSymbol("Clear", ID.Clear);

  /**
   * ClearAll(symbol1, symbol2,...) - clears all values and attributes associated with the given
   * symbols. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClearAll.md">ClearAll</a>
   */
  public static final IBuiltInSymbol ClearAll = F.initFinalSymbol("ClearAll", ID.ClearAll);

  /**
   * ClearAttributes(symbol, attrib) - removes `attrib` from `symbol`'s attributes. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClearAttributes.md">ClearAttributes</a>
   */
  public static final IBuiltInSymbol ClearAttributes =
      F.initFinalSymbol("ClearAttributes", ID.ClearAttributes);

  /**
   * Clip(expr) - returns `expr` in the range `-1` to `1`. Returns `-1` if `expr` is less than `-1`.
   * Returns `1` if `expr` is greater than `1`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Clip.md">Clip</a>
   */
  public static final IBuiltInSymbol Clip = F.initFinalSymbol("Clip", ID.Clip);

  /**
   * ClosenessCentrality(graph) - Computes the closeness centrality of each vertex of a `graph`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClosenessCentrality.md">ClosenessCentrality</a>
   */
  public static final IBuiltInSymbol ClosenessCentrality =
      F.initFinalSymbol("ClosenessCentrality", ID.ClosenessCentrality);

  /**
   * Coefficient(polynomial, variable, exponent) - get the coefficient of `variable^exponent` in
   * `polynomial`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Coefficient.md">Coefficient</a>
   */
  public static final IBuiltInSymbol Coefficient = F.initFinalSymbol("Coefficient", ID.Coefficient);

  /**
   * CoefficientList(polynomial, variable) - get the coefficient list of a `polynomial`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoefficientList.md">CoefficientList</a>
   */
  public static final IBuiltInSymbol CoefficientList =
      F.initFinalSymbol("CoefficientList", ID.CoefficientList);

  /**
   * CoefficientRules(polynomial, list-of-variables) - get the list of coefficient rules of a
   * `polynomial`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoefficientRules.md">CoefficientRules</a>
   */
  public static final IBuiltInSymbol CoefficientRules =
      F.initFinalSymbol("CoefficientRules", ID.CoefficientRules);

  /**
   * Collect(expr, variable) - collect subexpressions in `expr` which belong to the same `variable`.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Collect.md">Collect</a>
   */
  public static final IBuiltInSymbol Collect = F.initFinalSymbol("Collect", ID.Collect);

  public static final IBuiltInSymbol Colon = F.initFinalSymbol("Colon", ID.Colon);

  public static final IBuiltInSymbol ColorData = F.initFinalSymbol("ColorData", ID.ColorData);

  public static final IBuiltInSymbol ColorFunction =
      F.initFinalSymbol("ColorFunction", ID.ColorFunction);

  public static final IBuiltInSymbol Column = F.initFinalSymbol("Column", ID.Column);

  /**
   * Commonest(data-values-list) - the mode of a list of data values is the value that appears most
   * often. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Commonest.md">Commonest</a>
   */
  public static final IBuiltInSymbol Commonest = F.initFinalSymbol("Commonest", ID.Commonest);

  public static final IBuiltInSymbol CompatibleUnitQ =
      F.initFinalSymbol("CompatibleUnitQ", ID.CompatibleUnitQ);

  /**
   * Compile(list-of-arguments}, expression) - compile the `expression` into a Java function, which
   * has the arguments defined in `list-of-arguments` and return the compile result in an
   * `CompiledFunction` expression. You have to run Symja from a Java Development Kit (JDK) to
   * compile to Java binary code. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Compile.md">Compile</a>
   */
  public static final IBuiltInSymbol Compile = F.initFinalSymbol("Compile", ID.Compile);

  /**
   * CompilePrint(list-of-arguments}, expression) - compile the `expression` into a Java function
   * and return the corresponding Java source code function, which has the arguments defined in
   * `list-of-arguments`n. You have to run Symja from a Java Development Kit (JDK) to compile to
   * Java binary code. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompilePrint.md">CompilePrint</a>
   */
  public static final IBuiltInSymbol CompilePrint =
      F.initFinalSymbol("CompilePrint", ID.CompilePrint);

  /**
   * CompiledFunction(...) - represents a binary Java coded function. You have to run Symja from a
   * Java Development Kit (JDK) to compile to Java binary code. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompiledFunction.md">CompiledFunction</a>
   */
  public static final IBuiltInSymbol CompiledFunction =
      F.initFinalSymbol("CompiledFunction", ID.CompiledFunction);

  /**
   * Complement(set1, set2) - get the complement set from `set1` and `set2`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complement.md">Complement</a>
   */
  public static final IBuiltInSymbol Complement = F.initFinalSymbol("Complement", ID.Complement);

  /**
   * Complex - is the head of complex numbers. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complex.md">Complex</a>
   */
  public static final IBuiltInSymbol Complex = F.initFinalSymbol("Complex", ID.Complex);

  /**
   * ComplexExpand(expr) - get the expanded `expr`. All variable symbols in `expr` are assumed to be
   * non complex numbers. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexExpand.md">ComplexExpand</a>
   */
  public static final IBuiltInSymbol ComplexExpand =
      F.initFinalSymbol("ComplexExpand", ID.ComplexExpand);

  /**
   * ComplexInfinity - represents an infinite complex quantity of undetermined direction. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexInfinity.md">ComplexInfinity</a>
   */
  public static final IBuiltInSymbol ComplexInfinity =
      F.initFinalSymbol("ComplexInfinity", ID.ComplexInfinity);

  /**
   * ComplexPlot3D(expr, {z, min, max ) - create a 3D plot of `expr` for the complex variable `z` in
   * the range `{ Re(min),Re(max) }` to `{ Im(min),Im(max) }` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexPlot3D.md">ComplexPlot3D</a>
   */
  public static final IBuiltInSymbol ComplexPlot3D =
      F.initFinalSymbol("ComplexPlot3D", ID.ComplexPlot3D);

  /**
   * Complexes - is the set of complex numbers. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complexes.md">Complexes</a>
   */
  public static final IBuiltInSymbol Complexes = F.initFinalSymbol("Complexes", ID.Complexes);

  public static final IBuiltInSymbol ComplexityFunction =
      F.initFinalSymbol("ComplexityFunction", ID.ComplexityFunction);

  /**
   * ComposeList(list-of-symbols, variable) - creates a list of compositions of the symbols applied
   * at the argument `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComposeList.md">ComposeList</a>
   */
  public static final IBuiltInSymbol ComposeList = F.initFinalSymbol("ComposeList", ID.ComposeList);

  /**
   * ComposeSeries( series1, series2 ) - substitute `series2` into `series1` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComposeSeries.md">ComposeSeries</a>
   */
  public static final IBuiltInSymbol ComposeSeries =
      F.initFinalSymbol("ComposeSeries", ID.ComposeSeries);

  /**
   * Composition(sym1, sym2,...)[arg1, arg2,...] - creates a composition of the symbols applied at
   * the arguments. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Composition.md">Composition</a>
   */
  public static final IBuiltInSymbol Composition = F.initFinalSymbol("Composition", ID.Composition);

  /**
   * CompoundExpression(expr1, expr2, ...) - evaluates its arguments in turn, returning the last
   * result. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompoundExpression.md">CompoundExpression</a>
   */
  public static final IBuiltInSymbol CompoundExpression =
      F.initFinalSymbol("CompoundExpression", ID.CompoundExpression);

  /**
   * Condition(pattern, expr) - places an additional constraint on `pattern` that only allows it to
   * match if `expr` evaluates to `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Condition.md">Condition</a>
   */
  public static final IBuiltInSymbol Condition = F.initFinalSymbol("Condition", ID.Condition);

  /**
   * ConditionalExpression(expr, condition) - if `condition` evaluates to `True` return `expr`, if
   * `condition` evaluates to `False` return `Undefined`. Otherwise return the
   * `ConditionalExpression` unevaluated. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConditionalExpression.md">ConditionalExpression</a>
   */
  public static final IBuiltInSymbol ConditionalExpression =
      F.initFinalSymbol("ConditionalExpression", ID.ConditionalExpression);

  /**
   * Conjugate(z) - returns the complex conjugate of the complex number `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Conjugate.md">Conjugate</a>
   */
  public static final IBuiltInSymbol Conjugate = F.initFinalSymbol("Conjugate", ID.Conjugate);

  /**
   * ConjugateTranspose(matrix) - get the transposed `matrix` with conjugated matrix elements. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConjugateTranspose.md">ConjugateTranspose</a>
   */
  public static final IBuiltInSymbol ConjugateTranspose =
      F.initFinalSymbol("ConjugateTranspose", ID.ConjugateTranspose);

  public static final IBuiltInSymbol ConnectedGraphQ =
      F.initFinalSymbol("ConnectedGraphQ", ID.ConnectedGraphQ);

  /**
   * Constant - is an attribute that indicates that a symbol is a constant. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Constant.md">Constant</a>
   */
  public static final IBuiltInSymbol Constant = F.initFinalSymbol("Constant", ID.Constant);

  /**
   * ConstantArray(expr, n) - returns a list of `n` copies of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConstantArray.md">ConstantArray</a>
   */
  public static final IBuiltInSymbol ConstantArray =
      F.initFinalSymbol("ConstantArray", ID.ConstantArray);

  public static final IBuiltInSymbol ContainsAll = F.initFinalSymbol("ContainsAll", ID.ContainsAll);

  public static final IBuiltInSymbol ContainsAny = F.initFinalSymbol("ContainsAny", ID.ContainsAny);

  public static final IBuiltInSymbol ContainsExactly =
      F.initFinalSymbol("ContainsExactly", ID.ContainsExactly);

  public static final IBuiltInSymbol ContainsNone =
      F.initFinalSymbol("ContainsNone", ID.ContainsNone);

  /**
   * ContainsOnly(list1, list2) - yields True if `list1` contains only elements that appear in
   * `list2`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContainsOnly.md">ContainsOnly</a>
   */
  public static final IBuiltInSymbol ContainsOnly =
      F.initFinalSymbol("ContainsOnly", ID.ContainsOnly);

  /**
   * Context(symbol) - return the context of the given symbol. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Context.md">Context</a>
   */
  public static final IBuiltInSymbol Context = F.initFinalSymbol("Context", ID.Context);

  /**
   * Continue() - continues with the next iteration in a `For`, `While`, or `Do` loop. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Continue.md">Continue</a>
   */
  public static final IBuiltInSymbol Continue = F.initFinalSymbol("Continue", ID.Continue);

  /**
   * ContinuedFraction(number) - the complete continued fraction representation for a rational or
   * quadradic irrational `number`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContinuedFraction.md">ContinuedFraction</a>
   */
  public static final IBuiltInSymbol ContinuedFraction =
      F.initFinalSymbol("ContinuedFraction", ID.ContinuedFraction);

  public static final IBuiltInSymbol ContourPlot = F.initFinalSymbol("ContourPlot", ID.ContourPlot);

  /**
   * Convergents({n1, n2, ...}) - return the list of convergents which represents the continued
   * fraction list `{n1, n2, ...}`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Convergents.md">Convergents</a>
   */
  public static final IBuiltInSymbol Convergents = F.initFinalSymbol("Convergents", ID.Convergents);

  public static final IBuiltInSymbol ConvexHullMesh =
      F.initFinalSymbol("ConvexHullMesh", ID.ConvexHullMesh);

  /**
   * CoprimeQ(x, y) - tests whether `x` and `y` are coprime by computing their greatest common
   * divisor. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoprimeQ.md">CoprimeQ</a>
   */
  public static final IBuiltInSymbol CoprimeQ = F.initFinalSymbol("CoprimeQ", ID.CoprimeQ);

  /**
   * Correlation(a, b) - computes Pearson's correlation of two equal-sized vectors `a` and `b`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Correlation.md">Correlation</a>
   */
  public static final IBuiltInSymbol Correlation = F.initFinalSymbol("Correlation", ID.Correlation);

  /**
   * Cos(expr) - returns the cosine of `expr` (measured in radians). `Cos(expr)` will evaluate
   * automatically in the case `expr` is a multiple of `Pi, Pi/2, Pi/3, Pi/4` and `Pi/6`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cos.md">Cos</a>
   */
  public static final IBuiltInSymbol Cos = F.initFinalSymbol("Cos", ID.Cos);

  /**
   * CosIntegral(expr) - returns the cosine integral of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CosIntegral.md">CosIntegral</a>
   */
  public static final IBuiltInSymbol CosIntegral = F.initFinalSymbol("CosIntegral", ID.CosIntegral);

  /**
   * Cosh(z) - returns the hyperbolic cosine of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cosh.md">Cosh</a>
   */
  public static final IBuiltInSymbol Cosh = F.initFinalSymbol("Cosh", ID.Cosh);

  /**
   * CoshIntegral(expr) - returns the hyperbolic cosine integral of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoshIntegral.md">CoshIntegral</a>
   */
  public static final IBuiltInSymbol CoshIntegral =
      F.initFinalSymbol("CoshIntegral", ID.CoshIntegral);

  /**
   * CosineDistance(u, v) - returns the cosine distance between `u` and `v`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CosineDistance.md">CosineDistance</a>
   */
  public static final IBuiltInSymbol CosineDistance =
      F.initFinalSymbol("CosineDistance", ID.CosineDistance);

  /**
   * Cot(expr) - the cotangent function. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cot.md">Cot</a>
   */
  public static final IBuiltInSymbol Cot = F.initFinalSymbol("Cot", ID.Cot);

  /**
   * Coth(z) - returns the hyperbolic cotangent of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Coth.md">Coth</a>
   */
  public static final IBuiltInSymbol Coth = F.initFinalSymbol("Coth", ID.Coth);

  /**
   * Count(list, pattern) - returns the number of times `pattern` appears in `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Count.md">Count</a>
   */
  public static final IBuiltInSymbol Count = F.initFinalSymbol("Count", ID.Count);

  /**
   * CountDistinct(list) - returns the number of distinct entries in `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CountDistinct.md">CountDistinct</a>
   */
  public static final IBuiltInSymbol CountDistinct =
      F.initFinalSymbol("CountDistinct", ID.CountDistinct);

  /**
   * Counts({elem1, elem2, elem3, ...}) - count the number of each distinct element in the list
   * `{elem1, elem2, elem3, ...}` and return the result as an association `<|elem1->counter1,
   * ...|>`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Counts.md">Counts</a>
   */
  public static final IBuiltInSymbol Counts = F.initFinalSymbol("Counts", ID.Counts);

  /**
   * Covariance(a, b) - computes the covariance between the equal-sized vectors `a` and `b`. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Covariance.md">Covariance</a>
   */
  public static final IBuiltInSymbol Covariance = F.initFinalSymbol("Covariance", ID.Covariance);

  public static final IBuiltInSymbol CreateDirectory =
      F.initFinalSymbol("CreateDirectory", ID.CreateDirectory);

  /**
   * Cross(a, b) - computes the vector cross product of `a` and `b`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cross.md">Cross</a>
   */
  public static final IBuiltInSymbol Cross = F.initFinalSymbol("Cross", ID.Cross);

  /**
   * Csc(z) - returns the cosecant of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Csc.md">Csc</a>
   */
  public static final IBuiltInSymbol Csc = F.initFinalSymbol("Csc", ID.Csc);

  /**
   * Csch(z) - returns the hyperbolic cosecant of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Csch.md">Csch</a>
   */
  public static final IBuiltInSymbol Csch = F.initFinalSymbol("Csch", ID.Csch);

  /**
   * CubeRoot(n) - finds the real-valued cube root of the given `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CubeRoot.md">CubeRoot</a>
   */
  public static final IBuiltInSymbol CubeRoot = F.initFinalSymbol("CubeRoot", ID.CubeRoot);

  /**
   * Curl({f1, f2}, {x1, x2}) - gives the curl. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Curl.md">Curl</a>
   */
  public static final IBuiltInSymbol Curl = F.initFinalSymbol("Curl", ID.Curl);

  public static final IBuiltInSymbol Cyan = F.initFinalSymbol("Cyan", ID.Cyan);

  /**
   * Cycles(a, b) - expression for defining canonical cycles of a permutation. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cycles.md">Cycles</a>
   */
  public static final IBuiltInSymbol Cycles = F.initFinalSymbol("Cycles", ID.Cycles);

  /**
   * Cyclotomic(n, x) - returns the Cyclotomic polynomial `C_n(x)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cyclotomic.md">Cyclotomic</a>
   */
  public static final IBuiltInSymbol Cyclotomic = F.initFinalSymbol("Cyclotomic", ID.Cyclotomic);

  /**
   * D(f, x) - gives the partial derivative of `f` with respect to `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/D.md">D</a>
   */
  public static final IBuiltInSymbol D = F.initFinalSymbol("D", ID.D);

  /**
   * DSolve(equation, f(var), var) - attempts to solve a linear differential `equation` for the
   * function `f(var)` and variable `var`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DSolve.md">DSolve</a>
   */
  public static final IBuiltInSymbol DSolve = F.initFinalSymbol("DSolve", ID.DSolve);

  public static final IBuiltInSymbol Dataset = F.initFinalSymbol("Dataset", ID.Dataset);

  public static final IBuiltInSymbol DateObject = F.initFinalSymbol("DateObject", ID.DateObject);

  public static final IBuiltInSymbol DateValue = F.initFinalSymbol("DateValue", ID.DateValue);

  /**
   * Decrement(x) - decrements `x` by `1`, returning the original value of `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Decrement.md">Decrement</a>
   */
  public static final IBuiltInSymbol Decrement = F.initFinalSymbol("Decrement", ID.Decrement);

  /**
   * Default(symbol) - `Default` returns the default value associated with the `symbol` for a
   * pattern default `_.` expression. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Default.md">Default</a>
   */
  public static final IBuiltInSymbol Default = F.initFinalSymbol("Default", ID.Default);

  public static final IBuiltInSymbol DefaultButton =
      F.initFinalSymbol("DefaultButton", ID.DefaultButton);

  /**
   * Defer(expr) - `Defer` doesn't evaluate `expr` and didn't appear in the output | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Defer.md">Defer</a>
   */
  public static final IBuiltInSymbol Defer = F.initFinalSymbol("Defer", ID.Defer);

  /**
   * Definition(symbol) - prints user-defined values and rules associated with `symbol`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Definition.md">Definition</a>
   */
  public static final IBuiltInSymbol Definition = F.initFinalSymbol("Definition", ID.Definition);

  /**
   * Degree - the constant `Degree` converts angles from degree to `Pi/180` radians. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Degree.md">Degree</a>
   */
  public static final IBuiltInSymbol Degree = F.initFinalSymbol("Degree", ID.Degree);

  public static final IBuiltInSymbol DegreeLexicographic =
      F.initFinalSymbol("DegreeLexicographic", ID.DegreeLexicographic);

  public static final IBuiltInSymbol DegreeReverseLexicographic =
      F.initFinalSymbol("DegreeReverseLexicographic", ID.DegreeReverseLexicographic);

  /**
   * Delete(expr, n) - returns `expr` with part `n` removed. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Delete.md">Delete</a>
   */
  public static final IBuiltInSymbol Delete = F.initFinalSymbol("Delete", ID.Delete);

  /**
   * DeleteCases(list, pattern) - returns the elements of `list` that do not match `pattern`. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteCases.md">DeleteCases</a>
   */
  public static final IBuiltInSymbol DeleteCases = F.initFinalSymbol("DeleteCases", ID.DeleteCases);

  /**
   * DeleteDuplicates(list) - deletes duplicates from `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteDuplicates.md">DeleteDuplicates</a>
   */
  public static final IBuiltInSymbol DeleteDuplicates =
      F.initFinalSymbol("DeleteDuplicates", ID.DeleteDuplicates);

  /**
   * DeleteDuplicatesBy(list, predicate) - deletes duplicates from `list`, for which the `predicate`
   * returns `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteDuplicatesBy.md">DeleteDuplicatesBy</a>
   */
  public static final IBuiltInSymbol DeleteDuplicatesBy =
      F.initFinalSymbol("DeleteDuplicatesBy", ID.DeleteDuplicatesBy);

  /**
   * Denominator(expr) - gives the denominator in `expr`. Denominator collects expressions with
   * negative exponents. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Denominator.md">Denominator</a>
   */
  public static final IBuiltInSymbol Denominator = F.initFinalSymbol("Denominator", ID.Denominator);

  /**
   * DensityHistogram( list-of-pair-values ) - plot a density histogram for a `list-of-pair-values`
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DensityHistogram.md">DensityHistogram</a>
   */
  public static final IBuiltInSymbol DensityHistogram =
      F.initFinalSymbol("DensityHistogram", ID.DensityHistogram);

  public static final IBuiltInSymbol DensityPlot = F.initFinalSymbol("DensityPlot", ID.DensityPlot);

  /**
   * Depth(expr) - gives the depth of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Depth.md">Depth</a>
   */
  public static final IBuiltInSymbol Depth = F.initFinalSymbol("Depth", ID.Depth);

  /**
   * Derivative(n)[f] - represents the `n`-th derivative of the function `f`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Derivative.md">Derivative</a>
   */
  public static final IBuiltInSymbol Derivative = F.initFinalSymbol("Derivative", ID.Derivative);

  /**
   * DesignMatrix(m, f, x) - returns the design matrix. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DesignMatrix.md">DesignMatrix</a>
   */
  public static final IBuiltInSymbol DesignMatrix =
      F.initFinalSymbol("DesignMatrix", ID.DesignMatrix);

  /**
   * Det(matrix) - computes the determinant of the `matrix`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Det.md">Det</a>
   */
  public static final IBuiltInSymbol Det = F.initFinalSymbol("Det", ID.Det);

  /**
   * Diagonal(matrix) - computes the diagonal vector of the `matrix`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Diagonal.md">Diagonal</a>
   */
  public static final IBuiltInSymbol Diagonal = F.initFinalSymbol("Diagonal", ID.Diagonal);

  /**
   * DiagonalMatrix(list) - gives a matrix with the values in `list` on its diagonal and zeroes
   * elsewhere. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiagonalMatrix.md">DiagonalMatrix</a>
   */
  public static final IBuiltInSymbol DiagonalMatrix =
      F.initFinalSymbol("DiagonalMatrix", ID.DiagonalMatrix);

  /**
   * DialogInput() - if the file system is enabled, the user can input a string in a dialog box. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DialogInput.md">DialogInput</a>
   */
  public static final IBuiltInSymbol DialogInput = F.initFinalSymbol("DialogInput", ID.DialogInput);

  public static final IBuiltInSymbol DialogNotebook =
      F.initFinalSymbol("DialogNotebook", ID.DialogNotebook);

  public static final IBuiltInSymbol DialogReturn =
      F.initFinalSymbol("DialogReturn", ID.DialogReturn);

  /**
   * DiceDissimilarity(u, v) - returns the Dice dissimilarity between the two boolean 1-D lists `u`
   * and `v`, which is defined as `(c_tf + c_ft) / (2 * c_tt + c_ft + c_tf)`, where n is `len(u)`
   * and `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiceDissimilarity.md">DiceDissimilarity</a>
   */
  public static final IBuiltInSymbol DiceDissimilarity =
      F.initFinalSymbol("DiceDissimilarity", ID.DiceDissimilarity);

  /**
   * DifferenceDelta(f(x), h) - generates a forward difference `f(x+1) - f(x)` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DifferenceDelta.md">DifferenceDelta</a>
   */
  public static final IBuiltInSymbol DifferenceDelta =
      F.initFinalSymbol("DifferenceDelta", ID.DifferenceDelta);

  public static final IBuiltInSymbol Differences = F.initFinalSymbol("Differences", ID.Differences);

  /**
   * DigitCharacter - represents the digits 0-9. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitCharacter.md">DigitCharacter</a>
   */
  public static final IBuiltInSymbol DigitCharacter =
      F.initFinalSymbol("DigitCharacter", ID.DigitCharacter);

  /**
   * DigitCount(n) - returns a list of the number of integer digits for `n` for `radix` 10. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitCount.md">DigitCount</a>
   */
  public static final IBuiltInSymbol DigitCount = F.initFinalSymbol("DigitCount", ID.DigitCount);

  /**
   * DigitQ(str) - returns `True` if `str` is a string which contains only digits. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitQ.md">DigitQ</a>
   */
  public static final IBuiltInSymbol DigitQ = F.initFinalSymbol("DigitQ", ID.DigitQ);

  /**
   * Dimensions(expr) - returns a list of the dimensions of the expression `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dimensions.md">Dimensions</a>
   */
  public static final IBuiltInSymbol Dimensions = F.initFinalSymbol("Dimensions", ID.Dimensions);

  /**
   * DiracDelta(x) - `DiracDelta` function returns `0` for all real numbers `x` where `x != 0`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiracDelta.md">DiracDelta</a>
   */
  public static final IBuiltInSymbol DiracDelta = F.initFinalSymbol("DiracDelta", ID.DiracDelta);

  public static final IBuiltInSymbol DirectedEdge =
      F.initFinalSymbol("DirectedEdge", ID.DirectedEdge);

  /**
   * DirectedInfinity(z) - represents an infinite multiple of the complex number `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DirectedInfinity.md">DirectedInfinity</a>
   */
  public static final IBuiltInSymbol DirectedInfinity =
      F.initFinalSymbol("DirectedInfinity", ID.DirectedInfinity);

  public static final IBuiltInSymbol Direction = F.initFinalSymbol("Direction", ID.Direction);

  public static final IBuiltInSymbol Directive = F.initFinalSymbol("Directive", ID.Directive);

  public static final IBuiltInSymbol DirichletEta =
      F.initFinalSymbol("DirichletEta", ID.DirichletEta);

  public static final IBuiltInSymbol DirichletWindow =
      F.initFinalSymbol("DirichletWindow", ID.DirichletWindow);

  /**
   * DiscreteDelta(n1, n2, n3, ...) - `DiscreteDelta` function returns `1` if all the `ni` are `0`.
   * Returns `0` otherwise. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscreteDelta.md">DiscreteDelta</a>
   */
  public static final IBuiltInSymbol DiscreteDelta =
      F.initFinalSymbol("DiscreteDelta", ID.DiscreteDelta);

  /**
   * DiscreteUniformDistribution({min, max}) - returns a discrete uniform distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscreteUniformDistribution.md">DiscreteUniformDistribution</a>
   */
  public static final IBuiltInSymbol DiscreteUniformDistribution =
      F.initFinalSymbol("DiscreteUniformDistribution", ID.DiscreteUniformDistribution);

  /**
   * Discriminant(poly, var) - computes the discriminant of the polynomial `poly` with respect to
   * the variable `var`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Discriminant.md">Discriminant</a>
   */
  public static final IBuiltInSymbol Discriminant =
      F.initFinalSymbol("Discriminant", ID.Discriminant);

  public static final IBuiltInSymbol DisjointQ = F.initFinalSymbol("DisjointQ", ID.DisjointQ);

  /**
   * Dispatch({rule1, rule2, ...}) - create a dispatch map for a list of rules. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dispatch.md">Dispatch</a>
   */
  public static final IBuiltInSymbol Dispatch = F.initFinalSymbol("Dispatch", ID.Dispatch);

  public static final IBuiltInSymbol Disputed = F.initFinalSymbol("Disputed", ID.Disputed);

  public static final IBuiltInSymbol DisrectedEdges =
      F.initFinalSymbol("DisrectedEdges", ID.DisrectedEdges);

  public static final IBuiltInSymbol DistanceFunction =
      F.initFinalSymbol("DistanceFunction", ID.DistanceFunction);

  /**
   * Distribute(f(x1, x2, x3,...)) - distributes `f` over `Plus` appearing in any of the `xi`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Distribute.md">Distribute</a>
   */
  public static final IBuiltInSymbol Distribute = F.initFinalSymbol("Distribute", ID.Distribute);

  public static final IBuiltInSymbol Distributed = F.initFinalSymbol("Distributed", ID.Distributed);

  /**
   * Div({f1, f2, f3,...},{x1, x2, x3,...}) - compute the divergence. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Div.md">Div</a>
   */
  public static final IBuiltInSymbol Div = F.initFinalSymbol("Div", ID.Div);

  /**
   * Divide(a, b) - represents the division of `a` by `b`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divide.md">Divide</a>
   */
  public static final IBuiltInSymbol Divide = F.initFinalSymbol("Divide", ID.Divide);

  /**
   * DivideBy(x, dx) - is equivalent to `x = x / dx`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivideBy.md">DivideBy</a>
   */
  public static final IBuiltInSymbol DivideBy = F.initFinalSymbol("DivideBy", ID.DivideBy);

  /**
   * Divisible(n, m) - returns `True` if `n` could be divide by `m`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divisible.md">Divisible</a>
   */
  public static final IBuiltInSymbol Divisible = F.initFinalSymbol("Divisible", ID.Divisible);

  /**
   * DivisorSigma(k, n) - returns the sum of the `k`-th powers of the divisors of `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivisorSigma.md">DivisorSigma</a>
   */
  public static final IBuiltInSymbol DivisorSigma =
      F.initFinalSymbol("DivisorSigma", ID.DivisorSigma);

  /**
   * DivisorSum(n, head) - returns the sum of the divisors of `n`. The `head` is applied to each
   * divisor. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivisorSum.md">DivisorSum</a>
   */
  public static final IBuiltInSymbol DivisorSum = F.initFinalSymbol("DivisorSum", ID.DivisorSum);

  /**
   * Divisors(n) - returns all integers that divide the integer `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divisors.md">Divisors</a>
   */
  public static final IBuiltInSymbol Divisors = F.initFinalSymbol("Divisors", ID.Divisors);

  /**
   * Do(expr, {max}) - evaluates `expr` `max` times. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Do.md">Do</a>
   */
  public static final IBuiltInSymbol Do = F.initFinalSymbol("Do", ID.Do);

  /**
   * Dot(x, y) or x . y - `x . y` computes the vector dot product or matrix product `x . y`. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dot.md">Dot</a>
   */
  public static final IBuiltInSymbol Dot = F.initFinalSymbol("Dot", ID.Dot);

  /**
   * DownValues(symbol) - prints the down-value rules associated with `symbol`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownValues.md">DownValues</a>
   */
  public static final IBuiltInSymbol DownValues = F.initFinalSymbol("DownValues", ID.DownValues);

  /**
   * Drop(expr, n) - returns `expr` with the first `n` leaves removed. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Drop.md">Drop</a>
   */
  public static final IBuiltInSymbol Drop = F.initFinalSymbol("Drop", ID.Drop);

  public static final IBuiltInSymbol DuplicateFreeQ =
      F.initFinalSymbol("DuplicateFreeQ", ID.DuplicateFreeQ);

  public static final IBuiltInSymbol Dynamic = F.initFinalSymbol("Dynamic", ID.Dynamic);

  /**
   * E - Euler's constant E | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/E.md">E</a>
   */
  public static final IBuiltInSymbol E = F.initFinalSymbol("E", ID.E);

  public static final IBuiltInSymbol EasterSunday =
      F.initFinalSymbol("EasterSunday", ID.EasterSunday);

  /**
   * Echo(expr) - prints the `expr` to the default output stream and returns `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Echo.md">Echo</a>
   */
  public static final IBuiltInSymbol Echo = F.initFinalSymbol("Echo", ID.Echo);

  /**
   * EchoFunction()[expr] - operator form of the `Echo`function. Print the `expr` to the default
   * output stream and return `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EchoFunction.md">EchoFunction</a>
   */
  public static final IBuiltInSymbol EchoFunction =
      F.initFinalSymbol("EchoFunction", ID.EchoFunction);

  public static final IBuiltInSymbol EdgeCount = F.initFinalSymbol("EdgeCount", ID.EdgeCount);

  public static final IBuiltInSymbol EdgeLabels = F.initFinalSymbol("EdgeLabels", ID.EdgeLabels);

  /**
   * EdgeList(graph) - convert the `graph` into a list of edges. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeList.md">EdgeList</a>
   */
  public static final IBuiltInSymbol EdgeList = F.initFinalSymbol("EdgeList", ID.EdgeList);

  /**
   * EdgeQ(graph, edge) - test if `edge` is an edge in the `graph` object. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeQ.md">EdgeQ</a>
   */
  public static final IBuiltInSymbol EdgeQ = F.initFinalSymbol("EdgeQ", ID.EdgeQ);

  public static final IBuiltInSymbol EdgeShapeFunction =
      F.initFinalSymbol("EdgeShapeFunction", ID.EdgeShapeFunction);

  public static final IBuiltInSymbol EdgeStyle = F.initFinalSymbol("EdgeStyle", ID.EdgeStyle);

  public static final IBuiltInSymbol EdgeWeight = F.initFinalSymbol("EdgeWeight", ID.EdgeWeight);

  /**
   * EditDistance(a, b) - returns the Levenshtein distance of `a` and `b`, which is defined as the
   * minimum number of insertions, deletions and substitutions on the constituents of `a` and `b`
   * needed to transform one into the other. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EditDistance.md">EditDistance</a>
   */
  public static final IBuiltInSymbol EditDistance =
      F.initFinalSymbol("EditDistance", ID.EditDistance);

  /**
   * EffectiveInterest(i, n) - returns an effective interest rate object. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EffectiveInterest.md">EffectiveInterest</a>
   */
  public static final IBuiltInSymbol EffectiveInterest =
      F.initFinalSymbol("EffectiveInterest", ID.EffectiveInterest);

  /**
   * Eigenvalues(matrix) - get the numerical eigenvalues of the `matrix`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigenvalues.md">Eigenvalues</a>
   */
  public static final IBuiltInSymbol Eigenvalues = F.initFinalSymbol("Eigenvalues", ID.Eigenvalues);

  /**
   * Eigenvectors(matrix) - get the numerical eigenvectors of the `matrix`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigenvectors.md">Eigenvectors</a>
   */
  public static final IBuiltInSymbol Eigenvectors =
      F.initFinalSymbol("Eigenvectors", ID.Eigenvectors);

  /**
   * Element(symbol, dom) - assume (or test) that the `symbol` is in the domain `dom`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Element.md">Element</a>
   */
  public static final IBuiltInSymbol Element = F.initFinalSymbol("Element", ID.Element);

  /**
   * ElementData("name", "property") - gives the value of the property for the chemical specified by
   * name. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ElementData.md">ElementData</a>
   */
  public static final IBuiltInSymbol ElementData = F.initFinalSymbol("ElementData", ID.ElementData);

  /**
   * Eliminate(list-of-equations, list-of-variables) - attempts to eliminate the variables from the
   * `list-of-variables` in the `list-of-equations`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eliminate.md">Eliminate</a>
   */
  public static final IBuiltInSymbol Eliminate = F.initFinalSymbol("Eliminate", ID.Eliminate);

  public static final IBuiltInSymbol EliminationOrder =
      F.initFinalSymbol("EliminationOrder", ID.EliminationOrder);

  /**
   * EllipticE(z) - returns the complete elliptic integral of the second kind. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticE.md">EllipticE</a>
   */
  public static final IBuiltInSymbol EllipticE = F.initFinalSymbol("EllipticE", ID.EllipticE);

  /**
   * EllipticF(z) - returns the incomplete elliptic integral of the first kind. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticF.md">EllipticF</a>
   */
  public static final IBuiltInSymbol EllipticF = F.initFinalSymbol("EllipticF", ID.EllipticF);

  /**
   * EllipticK(z) - returns the complete elliptic integral of the first kind. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticK.md">EllipticK</a>
   */
  public static final IBuiltInSymbol EllipticK = F.initFinalSymbol("EllipticK", ID.EllipticK);

  /**
   * EllipticPi(n,m) - returns the complete elliptic integral of the third kind. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticPi.md">EllipticPi</a>
   */
  public static final IBuiltInSymbol EllipticPi = F.initFinalSymbol("EllipticPi", ID.EllipticPi);

  public static final IBuiltInSymbol EllipticTheta =
      F.initFinalSymbol("EllipticTheta", ID.EllipticTheta);

  /**
   * End( ) - end a context definition started with `Begin` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/End.md">End</a>
   */
  public static final IBuiltInSymbol End = F.initFinalSymbol("End", ID.End);

  public static final IBuiltInSymbol EndOfLine = F.initFinalSymbol("EndOfLine", ID.EndOfLine);

  public static final IBuiltInSymbol EndOfString = F.initFinalSymbol("EndOfString", ID.EndOfString);

  /**
   * EndPackage( ) - end a package definition | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EndPackage.md">EndPackage</a>
   */
  public static final IBuiltInSymbol EndPackage = F.initFinalSymbol("EndPackage", ID.EndPackage);

  public static final IBuiltInSymbol EndTestSection =
      F.initFinalSymbol("EndTestSection", ID.EndTestSection);

  public static final IBuiltInSymbol Entity = F.initFinalSymbol("Entity", ID.Entity);

  /**
   * Entropy(list) - return the base `E` (Shannon) information entropy of the elements in `list`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Entropy.md">Entropy</a>
   */
  public static final IBuiltInSymbol Entropy = F.initFinalSymbol("Entropy", ID.Entropy);

  /**
   * Equal(x, y) - yields `True` if `x` and `y` are known to be equal, or `False` if `x` and `y` are
   * known to be unequal. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Equal.md">Equal</a>
   */
  public static final IBuiltInSymbol Equal = F.initFinalSymbol("Equal", ID.Equal);

  /**
   * Equivalent(arg1, arg2, ...) - Equivalence relation. `Equivalent(A, B)` is `True` iff `A` and
   * `B` are both `True` or both `False`. Returns `True` if all of the arguments are logically
   * equivalent. Returns `False` otherwise. `Equivalent(arg1, arg2, ...)` is equivalent to `(arg1 &&
   * arg2 && ...) || (!arg1 && !arg2 && ...)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Equivalent.md">Equivalent</a>
   */
  public static final IBuiltInSymbol Equivalent = F.initFinalSymbol("Equivalent", ID.Equivalent);

  /**
   * Erf(z) - returns the error function of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erf.md">Erf</a>
   */
  public static final IBuiltInSymbol Erf = F.initFinalSymbol("Erf", ID.Erf);

  /**
   * Erfc(z) - returns the complementary error function of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erfc.md">Erfc</a>
   */
  public static final IBuiltInSymbol Erfc = F.initFinalSymbol("Erfc", ID.Erfc);

  /**
   * Erfi(z) - returns the imaginary error function of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erfi.md">Erfi</a>
   */
  public static final IBuiltInSymbol Erfi = F.initFinalSymbol("Erfi", ID.Erfi);

  /**
   * ErlangDistribution({k, lambda}) - returns a Erlang distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ErlangDistribution.md">ErlangDistribution</a>
   */
  public static final IBuiltInSymbol ErlangDistribution =
      F.initFinalSymbol("ErlangDistribution", ID.ErlangDistribution);

  /**
   * EuclideanDistance(u, v) - returns the euclidean distance between `u` and `v`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EuclideanDistance.md">EuclideanDistance</a>
   */
  public static final IBuiltInSymbol EuclideanDistance =
      F.initFinalSymbol("EuclideanDistance", ID.EuclideanDistance);

  /**
   * EulerE(n) - gives the euler number `En`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerE.md">EulerE</a>
   */
  public static final IBuiltInSymbol EulerE = F.initFinalSymbol("EulerE", ID.EulerE);

  /**
   * EulerGamma - Euler-Mascheroni constant | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerGamma.md">EulerGamma</a>
   */
  public static final IBuiltInSymbol EulerGamma = F.initFinalSymbol("EulerGamma", ID.EulerGamma);

  /**
   * EulerPhi(n) - compute Euler's totient function. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerPhi.md">EulerPhi</a>
   */
  public static final IBuiltInSymbol EulerPhi = F.initFinalSymbol("EulerPhi", ID.EulerPhi);

  /**
   * EulerianGraphQ(graph) - returns `True` if `graph` is an eulerian graph, and `False` otherwise.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerianGraphQ.md">EulerianGraphQ</a>
   */
  public static final IBuiltInSymbol EulerianGraphQ =
      F.initFinalSymbol("EulerianGraphQ", ID.EulerianGraphQ);

  /**
   * Evaluate(expr) - the `Evaluate` function will be executed even if the function attributes
   * `HoldFirst, HoldRest, HoldAll` are set for the function head. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Evaluate.md">Evaluate</a>
   */
  public static final IBuiltInSymbol Evaluate = F.initFinalSymbol("Evaluate", ID.Evaluate);

  /**
   * EvenQ(x) - returns `True` if `x` is even, and `False` otherwise. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EvenQ.md">EvenQ</a>
   */
  public static final IBuiltInSymbol EvenQ = F.initFinalSymbol("EvenQ", ID.EvenQ);

  /**
   * ExactNumberQ(expr) - returns `True` if `expr` is an exact number, and `False` otherwise. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExactNumberQ.md">ExactNumberQ</a>
   */
  public static final IBuiltInSymbol ExactNumberQ =
      F.initFinalSymbol("ExactNumberQ", ID.ExactNumberQ);

  /**
   * Except(c) - represents a pattern object that matches any expression except those matching `c`.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Except.md">Except</a>
   */
  public static final IBuiltInSymbol Except = F.initFinalSymbol("Except", ID.Except);

  public static final IBuiltInSymbol Exists = F.initFinalSymbol("Exists", ID.Exists);

  public static final IBuiltInSymbol Exit = F.initFinalSymbol("Exit", ID.Exit);

  /**
   * Exp(z) - the exponential function `E^z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Exp.md">Exp</a>
   */
  public static final IBuiltInSymbol Exp = F.initFinalSymbol("Exp", ID.Exp);

  /**
   * ExpIntegralE(n, expr) - returns the exponential integral `E_n(expr)` of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpIntegralE.md">ExpIntegralE</a>
   */
  public static final IBuiltInSymbol ExpIntegralE =
      F.initFinalSymbol("ExpIntegralE", ID.ExpIntegralE);

  /**
   * ExpIntegralEi(expr) - returns the exponential integral `Ei(expr)` of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpIntegralEi.md">ExpIntegralEi</a>
   */
  public static final IBuiltInSymbol ExpIntegralEi =
      F.initFinalSymbol("ExpIntegralEi", ID.ExpIntegralEi);

  public static final IBuiltInSymbol ExpToTrig = F.initFinalSymbol("ExpToTrig", ID.ExpToTrig);

  /**
   * Expand(expr) - expands out positive rational powers and products of sums in `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expand.md">Expand</a>
   */
  public static final IBuiltInSymbol Expand = F.initFinalSymbol("Expand", ID.Expand);

  /**
   * ExpandAll(expr) - expands out all positive integer powers and products of sums in `expr`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpandAll.md">ExpandAll</a>
   */
  public static final IBuiltInSymbol ExpandAll = F.initFinalSymbol("ExpandAll", ID.ExpandAll);

  /**
   * Expectation(pure-function, data-set) - returns the expected value of the `pure-function` for
   * the given `data-set`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expectation.md">Expectation</a>
   */
  public static final IBuiltInSymbol Expectation = F.initFinalSymbol("Expectation", ID.Expectation);

  /**
   * Exponent(polynomial, x) - gives the maximum power with which `x` appears in the expanded form
   * of `polynomial`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Exponent.md">Exponent</a>
   */
  public static final IBuiltInSymbol Exponent = F.initFinalSymbol("Exponent", ID.Exponent);

  /**
   * ExponentialDistribution(lambda) - returns an exponential distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExponentialDistribution.md">ExponentialDistribution</a>
   */
  public static final IBuiltInSymbol ExponentialDistribution =
      F.initFinalSymbol("ExponentialDistribution", ID.ExponentialDistribution);

  /**
   * Export("path-to-filename", expression, "WXF") - if the file system is enabled, export the
   * `expression` in WXF format to the "path-to-filename" file. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Export.md">Export</a>
   */
  public static final IBuiltInSymbol Export = F.initFinalSymbol("Export", ID.Export);

  public static final IBuiltInSymbol ExportString =
      F.initFinalSymbol("ExportString", ID.ExportString);

  public static final IBuiltInSymbol Expression = F.initFinalSymbol("Expression", ID.Expression);

  /**
   * ExtendedGCD(n1, n2, ...) - computes the extended greatest common divisor of the given integers.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExtendedGCD.md">ExtendedGCD</a>
   */
  public static final IBuiltInSymbol ExtendedGCD = F.initFinalSymbol("ExtendedGCD", ID.ExtendedGCD);

  public static final IBuiltInSymbol Extension = F.initFinalSymbol("Extension", ID.Extension);

  /**
   * Extract(expr, list) - extracts parts of `expr` specified by `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Extract.md">Extract</a>
   */
  public static final IBuiltInSymbol Extract = F.initFinalSymbol("Extract", ID.Extract);

  public static final IBuiltInSymbol FRatioDistribution =
      F.initFinalSymbol("FRatioDistribution", ID.FRatioDistribution);

  /**
   * Factor(expr) - factors the polynomial expression `expr` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factor.md">Factor</a>
   */
  public static final IBuiltInSymbol Factor = F.initFinalSymbol("Factor", ID.Factor);

  /**
   * FactorInteger(n) - returns the factorization of `n` as a list of factors and exponents. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorInteger.md">FactorInteger</a>
   */
  public static final IBuiltInSymbol FactorInteger =
      F.initFinalSymbol("FactorInteger", ID.FactorInteger);

  /**
   * FactorSquareFree(polynomial) - factor the polynomial expression `polynomial` square free. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorSquareFree.md">FactorSquareFree</a>
   */
  public static final IBuiltInSymbol FactorSquareFree =
      F.initFinalSymbol("FactorSquareFree", ID.FactorSquareFree);

  /**
   * FactorSquareFreeList(polynomial) - get the square free factors of the polynomial expression
   * `polynomial`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorSquareFreeList.md">FactorSquareFreeList</a>
   */
  public static final IBuiltInSymbol FactorSquareFreeList =
      F.initFinalSymbol("FactorSquareFreeList", ID.FactorSquareFreeList);

  /**
   * FactorTerms(poly) - pulls out any overall numerical factor in `poly`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorTerms.md">FactorTerms</a>
   */
  public static final IBuiltInSymbol FactorTerms = F.initFinalSymbol("FactorTerms", ID.FactorTerms);

  /**
   * Factorial(n) - returns the factorial number of the integer `n` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factorial.md">Factorial</a>
   */
  public static final IBuiltInSymbol Factorial = F.initFinalSymbol("Factorial", ID.Factorial);

  /**
   * Factorial2(n) - returns the double factorial number of the integer `n` as `n*(n-2)*(n-4)...`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factorial2.md">Factorial2</a>
   */
  public static final IBuiltInSymbol Factorial2 = F.initFinalSymbol("Factorial2", ID.Factorial2);

  public static final IBuiltInSymbol FactorialPower =
      F.initFinalSymbol("FactorialPower", ID.FactorialPower);

  /**
   * False - the constant `False` represents the boolean value **false** | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/False.md">False</a>
   */
  public static final IBuiltInSymbol False = F.initFinalSymbol("False", ID.False);

  /**
   * Fibonacci(n) - returns the Fibonacci number of the integer `n` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fibonacci.md">Fibonacci</a>
   */
  public static final IBuiltInSymbol Fibonacci = F.initFinalSymbol("Fibonacci", ID.Fibonacci);

  public static final IBuiltInSymbol FileNameJoin =
      F.initFinalSymbol("FileNameJoin", ID.FileNameJoin);

  public static final IBuiltInSymbol FileNameTake =
      F.initFinalSymbol("FileNameTake", ID.FileNameTake);

  /**
   * FilterRules(list-of-option-rules, list-of-rules) - filter the `list-of-option-rules` by
   * `list-of-rules`or `list-of-symbols`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FilterRules.md">FilterRules</a>
   */
  public static final IBuiltInSymbol FilterRules = F.initFinalSymbol("FilterRules", ID.FilterRules);

  /**
   * FindClusters(list-of-data-points, k) - Clustering algorithm based on David Arthur and Sergei
   * Vassilvitski k-means++ algorithm. Create `k` number of clusters to split the
   * `list-of-data-points` into. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindClusters.md">FindClusters</a>
   */
  public static final IBuiltInSymbol FindClusters =
      F.initFinalSymbol("FindClusters", ID.FindClusters);

  public static final IBuiltInSymbol FindEdgeCover =
      F.initFinalSymbol("FindEdgeCover", ID.FindEdgeCover);

  /**
   * FindEulerianCycle(graph) - find an eulerian cycle in the `graph`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindEulerianCycle.md">FindEulerianCycle</a>
   */
  public static final IBuiltInSymbol FindEulerianCycle =
      F.initFinalSymbol("FindEulerianCycle", ID.FindEulerianCycle);

  /**
   * FindFit(list-of-data-points, function, parameters, variable) - solve a least squares problem
   * using the Levenberg-Marquardt algorithm. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindFit.md">FindFit</a>
   */
  public static final IBuiltInSymbol FindFit = F.initFinalSymbol("FindFit", ID.FindFit);

  public static final IBuiltInSymbol FindGraphCommunities =
      F.initFinalSymbol("FindGraphCommunities", ID.FindGraphCommunities);

  /**
   * FindHamiltonianCycle(graph) - find an hamiltonian cycle in the `graph`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindHamiltonianCycle.md">FindHamiltonianCycle</a>
   */
  public static final IBuiltInSymbol FindHamiltonianCycle =
      F.initFinalSymbol("FindHamiltonianCycle", ID.FindHamiltonianCycle);

  public static final IBuiltInSymbol FindIndependentEdgeSet =
      F.initFinalSymbol("FindIndependentEdgeSet", ID.FindIndependentEdgeSet);

  public static final IBuiltInSymbol FindIndependentVertexSet =
      F.initFinalSymbol("FindIndependentVertexSet", ID.FindIndependentVertexSet);

  /**
   * FindInstance(equations, vars) - attempts to find one solution which solves the `equations` for
   * the variables `vars`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindInstance.md">FindInstance</a>
   */
  public static final IBuiltInSymbol FindInstance =
      F.initFinalSymbol("FindInstance", ID.FindInstance);

  /**
   * FindPermutation(list1, list2) - create a `Cycles({{...},{...}, ...})` permutation expression,
   * for two lists whose arguments are the same but may be differently arranged. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindPermutation.md">FindPermutation</a>
   */
  public static final IBuiltInSymbol FindPermutation =
      F.initFinalSymbol("FindPermutation", ID.FindPermutation);

  /**
   * FindRoot(f, {x, xmin, xmax}) - searches for a numerical root of `f` for the variable `x`, in
   * the range `xmin` to `xmax`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindRoot.md">FindRoot</a>
   */
  public static final IBuiltInSymbol FindRoot = F.initFinalSymbol("FindRoot", ID.FindRoot);

  /**
   * FindShortestPath(graph, source, destination) - find a shortest path in the `graph` from
   * `source` to `destination`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindShortestPath.md">FindShortestPath</a>
   */
  public static final IBuiltInSymbol FindShortestPath =
      F.initFinalSymbol("FindShortestPath", ID.FindShortestPath);

  /**
   * FindShortestTour({{p11, p12}, {p21, p22}, {p31, p32}, ...}) - find a shortest tour in the
   * `graph` with minimum `EuclideanDistance`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindShortestTour.md">FindShortestTour</a>
   */
  public static final IBuiltInSymbol FindShortestTour =
      F.initFinalSymbol("FindShortestTour", ID.FindShortestTour);

  /**
   * FindSpanningTree(graph) - find the minimum spanning tree in the `graph`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindSpanningTree.md">FindSpanningTree</a>
   */
  public static final IBuiltInSymbol FindSpanningTree =
      F.initFinalSymbol("FindSpanningTree", ID.FindSpanningTree);

  /**
   * FindVertexCover(graph) - algorithm to find a vertex cover for a `graph`. A vertex cover is a
   * set of vertices that touches all the edges in the graph. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindVertexCover.md">FindVertexCover</a>
   */
  public static final IBuiltInSymbol FindVertexCover =
      F.initFinalSymbol("FindVertexCover", ID.FindVertexCover);

  /**
   * First(expr) - returns the first element in `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/First.md">First</a>
   */
  public static final IBuiltInSymbol First = F.initFinalSymbol("First", ID.First);

  /**
   * FirstCase({arg1, arg2, ...}, pattern-matcher) - returns the first of the elements `argi` for
   * which `pattern-matcher` is matching. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FirstCase.md">FirstCase</a>
   */
  public static final IBuiltInSymbol FirstCase = F.initFinalSymbol("FirstCase", ID.FirstCase);

  /**
   * FirstPosition(expression, pattern-matcher) - returns the first subexpression of `expression`
   * for which `pattern-matcher` is matching. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FirstPosition.md">FirstPosition</a>
   */
  public static final IBuiltInSymbol FirstPosition =
      F.initFinalSymbol("FirstPosition", ID.FirstPosition);

  /**
   * Fit(list-of-data-points, degree, variable) - solve a least squares problem using the
   * Levenberg-Marquardt algorithm. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fit.md">Fit</a>
   */
  public static final IBuiltInSymbol Fit = F.initFinalSymbol("Fit", ID.Fit);

  /**
   * FittedModel( ) - `FittedModel`holds the model generated with `LinearModelFit` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FittedModel.md">FittedModel</a>
   */
  public static final IBuiltInSymbol FittedModel = F.initFinalSymbol("FittedModel", ID.FittedModel);

  /**
   * FiveNum({dataset}) - the Tuckey five-number summary is a set of descriptive statistics that
   * provide information about a `dataset`. It consists of the five most important sample
   * percentiles: | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FiveNum.md">FiveNum</a>
   */
  public static final IBuiltInSymbol FiveNum = F.initFinalSymbol("FiveNum", ID.FiveNum);

  /**
   * FixedPoint(f, expr) - starting with `expr`, iteratively applies `f` until the result no longer
   * changes. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FixedPoint.md">FixedPoint</a>
   */
  public static final IBuiltInSymbol FixedPoint = F.initFinalSymbol("FixedPoint", ID.FixedPoint);

  /**
   * FixedPointList(f, expr) - starting with `expr`, iteratively applies `f` until the result no
   * longer changes, and returns a list of all intermediate results. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FixedPointList.md">FixedPointList</a>
   */
  public static final IBuiltInSymbol FixedPointList =
      F.initFinalSymbol("FixedPointList", ID.FixedPointList);

  /**
   * Flat - is an attribute that specifies that nested occurrences of a function should be
   * automatically flattened. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Flat.md">Flat</a>
   */
  public static final IBuiltInSymbol Flat = F.initFinalSymbol("Flat", ID.Flat);

  public static final IBuiltInSymbol FlatTopWindow =
      F.initFinalSymbol("FlatTopWindow", ID.FlatTopWindow);

  /**
   * Flatten(expr) - flattens out nested lists in `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Flatten.md">Flatten</a>
   */
  public static final IBuiltInSymbol Flatten = F.initFinalSymbol("Flatten", ID.Flatten);

  /**
   * FlattenAt(expr, position) - flattens out nested lists at the given `position` in `expr`. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FlattenAt.md">FlattenAt</a>
   */
  public static final IBuiltInSymbol FlattenAt = F.initFinalSymbol("FlattenAt", ID.FlattenAt);

  public static final IBuiltInSymbol Float = F.initFinalSymbol("Float", ID.Float);

  /**
   * Floor(expr) - gives the smallest integer less than or equal `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Floor.md">Floor</a>
   */
  public static final IBuiltInSymbol Floor = F.initFinalSymbol("Floor", ID.Floor);

  /**
   * Fold[f, x, {a, b}] - returns `f[f[x, a], b]`, and this nesting continues for lists of arbitrary
   * length. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fold.md">Fold</a>
   */
  public static final IBuiltInSymbol Fold = F.initFinalSymbol("Fold", ID.Fold);

  /**
   * FoldList[f, x, {a, b}] - returns `{x, f[x, a], f[f[x, a], b]}` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FoldList.md">FoldList</a>
   */
  public static final IBuiltInSymbol FoldList = F.initFinalSymbol("FoldList", ID.FoldList);

  /**
   * For(start, test, incr, body) - evaluates `start`, and then iteratively `body` and `incr` as
   * long as test evaluates to `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/For.md">For</a>
   */
  public static final IBuiltInSymbol For = F.initFinalSymbol("For", ID.For);

  public static final IBuiltInSymbol ForAll = F.initFinalSymbol("ForAll", ID.ForAll);

  /**
   * Fourier(vector-of-complex-numbers) - Discrete Fourier transform of a
   * `vector-of-complex-numbers`. Fourier transform is restricted to vectors with length of power of
   * 2. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fourier.md">Fourier</a>
   */
  public static final IBuiltInSymbol Fourier = F.initFinalSymbol("Fourier", ID.Fourier);

  /**
   * FourierMatrix(n) - gives a fourier matrix with the dimension `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FourierMatrix.md">FourierMatrix</a>
   */
  public static final IBuiltInSymbol FourierMatrix =
      F.initFinalSymbol("FourierMatrix", ID.FourierMatrix);

  /**
   * FractionalPart(number) - get the fractional part of a `number`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FractionalPart.md">FractionalPart</a>
   */
  public static final IBuiltInSymbol FractionalPart =
      F.initFinalSymbol("FractionalPart", ID.FractionalPart);

  /**
   * FrechetDistribution(a,b) - returns a Frechet distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrechetDistribution.md">FrechetDistribution</a>
   */
  public static final IBuiltInSymbol FrechetDistribution =
      F.initFinalSymbol("FrechetDistribution", ID.FrechetDistribution);

  /**
   * FreeQ(`expr`, `x`) - returns 'True' if `expr` does not contain the expression `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FreeQ.md">FreeQ</a>
   */
  public static final IBuiltInSymbol FreeQ = F.initFinalSymbol("FreeQ", ID.FreeQ);

  public static final IBuiltInSymbol FresnelC = F.initFinalSymbol("FresnelC", ID.FresnelC);

  public static final IBuiltInSymbol FresnelS = F.initFinalSymbol("FresnelS", ID.FresnelS);

  /**
   * FrobeniusNumber({a1, ... ,aN}) - returns the Frobenius number of the nonnegative integers `{a1,
   * ... ,aN}` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrobeniusNumber.md">FrobeniusNumber</a>
   */
  public static final IBuiltInSymbol FrobeniusNumber =
      F.initFinalSymbol("FrobeniusNumber", ID.FrobeniusNumber);

  /**
   * FrobeniusSolve({a1, ... ,aN}, M) - get a list of solutions for the Frobenius equation given by
   * the list of integers `{a1, ... ,aN}` and the non-negative integer `M`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrobeniusSolve.md">FrobeniusSolve</a>
   */
  public static final IBuiltInSymbol FrobeniusSolve =
      F.initFinalSymbol("FrobeniusSolve", ID.FrobeniusSolve);

  /**
   * FromCharacterCode({ch1, ch2, ...}) - converts the `ch1, ch2,...` character codes into a string
   * of corresponding characters. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromCharacterCode.md">FromCharacterCode</a>
   */
  public static final IBuiltInSymbol FromCharacterCode =
      F.initFinalSymbol("FromCharacterCode", ID.FromCharacterCode);

  /**
   * FromContinuedFraction({n1, n2, ...}) - reconstructs a number from the list of its continued
   * fraction terms `{n1, n2, ...}`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromContinuedFraction.md">FromContinuedFraction</a>
   */
  public static final IBuiltInSymbol FromContinuedFraction =
      F.initFinalSymbol("FromContinuedFraction", ID.FromContinuedFraction);

  /**
   * FromDigits(list) - creates an expression from the list of digits for radix `10`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromDigits.md">FromDigits</a>
   */
  public static final IBuiltInSymbol FromDigits = F.initFinalSymbol("FromDigits", ID.FromDigits);

  /**
   * FromPolarCoordinates({r, t}) - return the cartesian coordinates for the polar coordinates `{r,
   * t}`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromPolarCoordinates.md">FromPolarCoordinates</a>
   */
  public static final IBuiltInSymbol FromPolarCoordinates =
      F.initFinalSymbol("FromPolarCoordinates", ID.FromPolarCoordinates);

  public static final IBuiltInSymbol Full = F.initFinalSymbol("Full", ID.Full);

  /**
   * FullForm(expression) - shows the internal representation of the given `expression`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FullForm.md">FullForm</a>
   */
  public static final IBuiltInSymbol FullForm = F.initFinalSymbol("FullForm", ID.FullForm);

  /**
   * FullSimplify(expr) - works like `Simplify` but additionally tries some `FunctionExpand` rule
   * transformations to simplify `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FullSimplify.md">FullSimplify</a>
   */
  public static final IBuiltInSymbol FullSimplify =
      F.initFinalSymbol("FullSimplify", ID.FullSimplify);

  /**
   * Function(body) - represents a pure function with parameters `#1`, `#2`.... | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Function.md">Function</a>
   */
  public static final IBuiltInSymbol Function = F.initFinalSymbol("Function", ID.Function);

  /**
   * FunctionExpand(expression) - expands the special function `expression`. `FunctionExpand`
   * expands simple nested radicals. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FunctionExpand.md">FunctionExpand</a>
   */
  public static final IBuiltInSymbol FunctionExpand =
      F.initFinalSymbol("FunctionExpand", ID.FunctionExpand);

  public static final IBuiltInSymbol FunctionRange =
      F.initFinalSymbol("FunctionRange", ID.FunctionRange);

  /**
   * FunctionURL(built-in-symbol) - returns the GitHub URL of the `built-in-symbol` implementation
   * in the [Symja GitHub repository](https://github.com/axkr/symja_android_library). | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FunctionURL.md">FunctionURL</a>
   */
  public static final IBuiltInSymbol FunctionURL = F.initFinalSymbol("FunctionURL", ID.FunctionURL);

  /**
   * GCD(n1, n2, ...) - computes the greatest common divisor of the given integers. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GCD.md">GCD</a>
   */
  public static final IBuiltInSymbol GCD = F.initFinalSymbol("GCD", ID.GCD);

  /**
   * Gamma(z) - is the gamma function on the complex number `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gamma.md">Gamma</a>
   */
  public static final IBuiltInSymbol Gamma = F.initFinalSymbol("Gamma", ID.Gamma);

  /**
   * GammaDistribution(a,b) - returns a gamma distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GammaDistribution.md">GammaDistribution</a>
   */
  public static final IBuiltInSymbol GammaDistribution =
      F.initFinalSymbol("GammaDistribution", ID.GammaDistribution);

  public static final IBuiltInSymbol GammaRegularized =
      F.initFinalSymbol("GammaRegularized", ID.GammaRegularized);

  /**
   * Gather(list, test) - gathers leaves of `list` into sub lists of items that are the same
   * according to `test`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gather.md">Gather</a>
   */
  public static final IBuiltInSymbol Gather = F.initFinalSymbol("Gather", ID.Gather);

  /**
   * GatherBy(list, f) - gathers leaves of `list` into sub lists of items whose image under `f`
   * identical. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GatherBy.md">GatherBy</a>
   */
  public static final IBuiltInSymbol GatherBy = F.initFinalSymbol("GatherBy", ID.GatherBy);

  public static final IBuiltInSymbol GaussianIntegers =
      F.initFinalSymbol("GaussianIntegers", ID.GaussianIntegers);

  public static final IBuiltInSymbol GaussianMatrix =
      F.initFinalSymbol("GaussianMatrix", ID.GaussianMatrix);

  public static final IBuiltInSymbol GaussianWindow =
      F.initFinalSymbol("GaussianWindow", ID.GaussianWindow);

  /**
   * GegenbauerC(n, a, x) - returns the GegenbauerC polynomial. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GegenbauerC.md">GegenbauerC</a>
   */
  public static final IBuiltInSymbol GegenbauerC = F.initFinalSymbol("GegenbauerC", ID.GegenbauerC);

  public static final IBuiltInSymbol General = F.initFinalSymbol("General", ID.General);

  /**
   * GeoDistance({latitude1,longitude1}, {latitude2,longitude2}) - returns the geodesic distance
   * between `{latitude1,longitude1}` and `{latitude2,longitude2}`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoDistance.md">GeoDistance</a>
   */
  public static final IBuiltInSymbol GeoDistance = F.initFinalSymbol("GeoDistance", ID.GeoDistance);

  public static final IBuiltInSymbol GeoPosition = F.initFinalSymbol("GeoPosition", ID.GeoPosition);

  public static final IBuiltInSymbol GeodesyData = F.initFinalSymbol("GeodesyData", ID.GeodesyData);

  /**
   * GeometricDistribution(p) - returns a geometric distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeometricDistribution.md">GeometricDistribution</a>
   */
  public static final IBuiltInSymbol GeometricDistribution =
      F.initFinalSymbol("GeometricDistribution", ID.GeometricDistribution);

  /**
   * GeometricMean({a, b, c,...}) - returns the geometric mean of `{a, b, c,...}`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeometricMean.md">GeometricMean</a>
   */
  public static final IBuiltInSymbol GeometricMean =
      F.initFinalSymbol("GeometricMean", ID.GeometricMean);

  /**
   * Get("path-to-package-file-name") - load the package defined in `path-to-package-file-name`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Get.md">Get</a>
   */
  public static final IBuiltInSymbol Get = F.initFinalSymbol("Get", ID.Get);

  /**
   * Glaisher - Glaisher constant. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Glaisher.md">Glaisher</a>
   */
  public static final IBuiltInSymbol Glaisher = F.initFinalSymbol("Glaisher", ID.Glaisher);

  public static final IBuiltInSymbol GoldenAngle = F.initFinalSymbol("GoldenAngle", ID.GoldenAngle);

  /**
   * GoldenRatio - is the golden ratio `(1+Sqrt(5))/2`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GoldenRatio.md">GoldenRatio</a>
   */
  public static final IBuiltInSymbol GoldenRatio = F.initFinalSymbol("GoldenRatio", ID.GoldenRatio);

  public static final IBuiltInSymbol GompertzMakehamDistribution =
      F.initFinalSymbol("GompertzMakehamDistribution", ID.GompertzMakehamDistribution);

  /**
   * Grad(function, list-of-variables) - gives the gradient of the function. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Grad.md">Grad</a>
   */
  public static final IBuiltInSymbol Grad = F.initFinalSymbol("Grad", ID.Grad);

  /**
   * Graph({edge1,...,edgeN}) - create a graph from the given edges `edge1,...,edgeN`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Graph.md">Graph</a>
   */
  public static final IBuiltInSymbol Graph = F.initFinalSymbol("Graph", ID.Graph);

  /**
   * GraphCenter(graph) - compute the `graph` center. The center of a `graph` is the set of vertices
   * of graph eccentricity equal to the `graph` radius. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphCenter.md">GraphCenter</a>
   */
  public static final IBuiltInSymbol GraphCenter = F.initFinalSymbol("GraphCenter", ID.GraphCenter);

  public static final IBuiltInSymbol GraphData = F.initFinalSymbol("GraphData", ID.GraphData);

  /**
   * GraphDiameter(graph) - return the diameter of the `graph`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphDiameter.md">GraphDiameter</a>
   */
  public static final IBuiltInSymbol GraphDiameter =
      F.initFinalSymbol("GraphDiameter", ID.GraphDiameter);

  /**
   * GraphPeriphery(graph) - compute the `graph` periphery. The periphery of a `graph` is the set of
   * vertices of graph eccentricity equal to the graph diameter. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphPeriphery.md">GraphPeriphery</a>
   */
  public static final IBuiltInSymbol GraphPeriphery =
      F.initFinalSymbol("GraphPeriphery", ID.GraphPeriphery);

  /**
   * GraphQ(expr) - test if `expr` is a graph object. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphQ.md">GraphQ</a>
   */
  public static final IBuiltInSymbol GraphQ = F.initFinalSymbol("GraphQ", ID.GraphQ);

  /**
   * GraphRadius(graph) - return the radius of the `graph`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphRadius.md">GraphRadius</a>
   */
  public static final IBuiltInSymbol GraphRadius = F.initFinalSymbol("GraphRadius", ID.GraphRadius);

  public static final IBuiltInSymbol GraphUnion = F.initFinalSymbol("GraphUnion", ID.GraphUnion);

  public static final IBuiltInSymbol Graphics = F.initFinalSymbol("Graphics", ID.Graphics);

  public static final IBuiltInSymbol Graphics3D = F.initFinalSymbol("Graphics3D", ID.Graphics3D);

  public static final IBuiltInSymbol Gray = F.initFinalSymbol("Gray", ID.Gray);

  public static final IBuiltInSymbol GrayLevel = F.initFinalSymbol("GrayLevel", ID.GrayLevel);

  /**
   * Greater(x, y) - yields `True` if `x` is known to be greater than `y`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Greater.md">Greater</a>
   */
  public static final IBuiltInSymbol Greater = F.initFinalSymbol("Greater", ID.Greater);

  /**
   * GreaterEqual(x, y) - yields `True` if `x` is known to be greater than or equal to `y`. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterEqual.md">GreaterEqual</a>
   */
  public static final IBuiltInSymbol GreaterEqual =
      F.initFinalSymbol("GreaterEqual", ID.GreaterEqual);

  public static final IBuiltInSymbol Green = F.initFinalSymbol("Green", ID.Green);

  /**
   * GroebnerBasis({polynomial-list},{variable-list}) - returns a Gröbner basis for the
   * `polynomial-list` and `variable-list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GroebnerBasis.md">GroebnerBasis</a>
   */
  public static final IBuiltInSymbol GroebnerBasis =
      F.initFinalSymbol("GroebnerBasis", ID.GroebnerBasis);

  /**
   * GroupBy(list, head) - return an association where the elements of `list` are grouped by
   * `head(element)` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GroupBy.md">GroupBy</a>
   */
  public static final IBuiltInSymbol GroupBy = F.initFinalSymbol("GroupBy", ID.GroupBy);

  /**
   * GumbelDistribution(a, b) - returns a Gumbel distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GumbelDistribution.md">GumbelDistribution</a>
   */
  public static final IBuiltInSymbol GumbelDistribution =
      F.initFinalSymbol("GumbelDistribution", ID.GumbelDistribution);

  /**
   * HamiltonianGraphQ(graph) - returns `True` if `graph` is an hamiltonian graph, and `False`
   * otherwise. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HamiltonianGraphQ.md">HamiltonianGraphQ</a>
   */
  public static final IBuiltInSymbol HamiltonianGraphQ =
      F.initFinalSymbol("HamiltonianGraphQ", ID.HamiltonianGraphQ);

  /**
   * HammingDistance(a, b) - returns the Hamming distance of `a` and `b`, i.e. the number of
   * different elements. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HammingDistance.md">HammingDistance</a>
   */
  public static final IBuiltInSymbol HammingDistance =
      F.initFinalSymbol("HammingDistance", ID.HammingDistance);

  public static final IBuiltInSymbol HammingWindow =
      F.initFinalSymbol("HammingWindow", ID.HammingWindow);

  public static final IBuiltInSymbol HankelH1 = F.initFinalSymbol("HankelH1", ID.HankelH1);

  public static final IBuiltInSymbol HankelH2 = F.initFinalSymbol("HankelH2", ID.HankelH2);

  public static final IBuiltInSymbol HannWindow = F.initFinalSymbol("HannWindow", ID.HannWindow);

  /**
   * HarmonicMean({a, b, c,...}) - returns the harmonic mean of `{a, b, c,...}`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HarmonicMean.md">HarmonicMean</a>
   */
  public static final IBuiltInSymbol HarmonicMean =
      F.initFinalSymbol("HarmonicMean", ID.HarmonicMean);

  /**
   * HarmonicNumber(n) - returns the `n`th harmonic number. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HarmonicNumber.md">HarmonicNumber</a>
   */
  public static final IBuiltInSymbol HarmonicNumber =
      F.initFinalSymbol("HarmonicNumber", ID.HarmonicNumber);

  /**
   * Haversine(z) - returns the haversine function of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Haversine.md">Haversine</a>
   */
  public static final IBuiltInSymbol Haversine = F.initFinalSymbol("Haversine", ID.Haversine);

  /**
   * Head(expr) - returns the head of the expression or atom `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Head.md">Head</a>
   */
  public static final IBuiltInSymbol Head = F.initFinalSymbol("Head", ID.Head);

  public static final IBuiltInSymbol Heads = F.initFinalSymbol("Heads", ID.Heads);

  /**
   * HeavisideTheta(expr1, expr2, ... exprN) - returns `1` if all `expr1, expr2, ... exprN` are
   * positive and `0` if one of the `expr1, expr2, ... exprN` is negative. `HeavisideTheta(0)`
   * returns unevaluated as `HeavisideTheta(0)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HeavisideTheta.md">HeavisideTheta</a>
   */
  public static final IBuiltInSymbol HeavisideTheta =
      F.initFinalSymbol("HeavisideTheta", ID.HeavisideTheta);

  /**
   * HermiteH(n, x) - returns the Hermite polynomial `H_n(x)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HermiteH.md">HermiteH</a>
   */
  public static final IBuiltInSymbol HermiteH = F.initFinalSymbol("HermiteH", ID.HermiteH);

  /**
   * HermitianMatrixQ(m) - returns `True` if `m` is a hermitian matrix. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HermitianMatrixQ.md">HermitianMatrixQ</a>
   */
  public static final IBuiltInSymbol HermitianMatrixQ =
      F.initFinalSymbol("HermitianMatrixQ", ID.HermitianMatrixQ);

  /**
   * HexidecimalCharacter - represents the characters `0-9`, `a-f` and `A-F`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HexidecimalCharacter.md">HexidecimalCharacter</a>
   */
  public static final IBuiltInSymbol HexidecimalCharacter =
      F.initFinalSymbol("HexidecimalCharacter", ID.HexidecimalCharacter);

  /**
   * HilbertMatrix(n) - gives the hilbert matrix with `n` rows and columns. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HilbertMatrix.md">HilbertMatrix</a>
   */
  public static final IBuiltInSymbol HilbertMatrix =
      F.initFinalSymbol("HilbertMatrix", ID.HilbertMatrix);

  /**
   * Histogram(list-of-values) - plots a histogram for a `list-of-values` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Histogram.md">Histogram</a>
   */
  public static final IBuiltInSymbol Histogram = F.initFinalSymbol("Histogram", ID.Histogram);

  /**
   * Hold(expr) - `Hold` doesn't evaluate `expr`. `Hold` evaluates `UpValues`for its arguments.
   * `HoldComplete` doesn't evaluate `UpValues`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hold.md">Hold</a>
   */
  public static final IBuiltInSymbol Hold = F.initFinalSymbol("Hold", ID.Hold);

  /**
   * HoldAll - is an attribute specifying that all arguments of a function should be left
   * unevaluated. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldAll.md">HoldAll</a>
   */
  public static final IBuiltInSymbol HoldAll = F.initFinalSymbol("HoldAll", ID.HoldAll);

  public static final IBuiltInSymbol HoldAllComplete =
      F.initFinalSymbol("HoldAllComplete", ID.HoldAllComplete);

  /**
   * HoldComplete(expr) - `HoldComplete` doesn't evaluate `expr`. `Hold` evaluates `UpValues`for its
   * arguments. `HoldComplete` doesn't evaluate `UpValues`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldComplete.md">HoldComplete</a>
   */
  public static final IBuiltInSymbol HoldComplete =
      F.initFinalSymbol("HoldComplete", ID.HoldComplete);

  /**
   * HoldFirst - is an attribute specifying that the first argument of a function should be left
   * unevaluated. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldFirst.md">HoldFirst</a>
   */
  public static final IBuiltInSymbol HoldFirst = F.initFinalSymbol("HoldFirst", ID.HoldFirst);

  /**
   * HoldForm(expr) - `HoldForm` doesn't evaluate `expr` and didn't appear in the output. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldForm.md">HoldForm</a>
   */
  public static final IBuiltInSymbol HoldForm = F.initFinalSymbol("HoldForm", ID.HoldForm);

  /**
   * HoldPattern(expr) - `HoldPattern` doesn't evaluate `expr` for pattern-matching. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldPattern.md">HoldPattern</a>
   */
  public static final IBuiltInSymbol HoldPattern = F.initFinalSymbol("HoldPattern", ID.HoldPattern);

  /**
   * HoldRest - is an attribute specifying that all but the first argument of a function should be
   * left unevaluated. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldRest.md">HoldRest</a>
   */
  public static final IBuiltInSymbol HoldRest = F.initFinalSymbol("HoldRest", ID.HoldRest);

  public static final IBuiltInSymbol Horner = F.initFinalSymbol("Horner", ID.Horner);

  /**
   * HornerForm(polynomial) - Generate the horner scheme for a univariate `polynomial`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HornerForm.md">HornerForm</a>
   */
  public static final IBuiltInSymbol HornerForm = F.initFinalSymbol("HornerForm", ID.HornerForm);

  public static final IBuiltInSymbol Hue = F.initFinalSymbol("Hue", ID.Hue);

  /**
   * HurwitzZeta(s, a) - returns the Hurwitz zeta function. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HurwitzZeta.md">HurwitzZeta</a>
   */
  public static final IBuiltInSymbol HurwitzZeta = F.initFinalSymbol("HurwitzZeta", ID.HurwitzZeta);

  /**
   * Hypergeometric0F1(b, z) - return the `Hypergeometric0F1` function | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hypergeometric0F1.md">Hypergeometric0F1</a>
   */
  public static final IBuiltInSymbol Hypergeometric0F1 =
      F.initFinalSymbol("Hypergeometric0F1", ID.Hypergeometric0F1);

  /**
   * Hypergeometric1F1(a, b, z) - return the `Hypergeometric1F1` function | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hypergeometric1F1.md">Hypergeometric1F1</a>
   */
  public static final IBuiltInSymbol Hypergeometric1F1 =
      F.initFinalSymbol("Hypergeometric1F1", ID.Hypergeometric1F1);

  public static final IBuiltInSymbol Hypergeometric1F1Regularized =
      F.initFinalSymbol("Hypergeometric1F1Regularized", ID.Hypergeometric1F1Regularized);

  /**
   * Hypergeometric2F1(a, b, c, z) - return the `Hypergeometric2F1` function | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hypergeometric2F1.md">Hypergeometric2F1</a>
   */
  public static final IBuiltInSymbol Hypergeometric2F1 =
      F.initFinalSymbol("Hypergeometric2F1", ID.Hypergeometric2F1);

  /**
   * HypergeometricDistribution(n, s, t) - returns a hypergeometric distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HypergeometricDistribution.md">HypergeometricDistribution</a>
   */
  public static final IBuiltInSymbol HypergeometricDistribution =
      F.initFinalSymbol("HypergeometricDistribution", ID.HypergeometricDistribution);

  /**
   * HypergeometricPFQ({a,...}, {b,...}, c) - return the `HypergeometricPFQ` function | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HypergeometricPFQ.md">HypergeometricPFQ</a>
   */
  public static final IBuiltInSymbol HypergeometricPFQ =
      F.initFinalSymbol("HypergeometricPFQ", ID.HypergeometricPFQ);

  public static final IBuiltInSymbol HypergeometricPFQRegularized =
      F.initFinalSymbol("HypergeometricPFQRegularized", ID.HypergeometricPFQRegularized);

  public static final IBuiltInSymbol HypergeometricU =
      F.initFinalSymbol("HypergeometricU", ID.HypergeometricU);

  /**
   * I - Imaginary unit - internally converted to the complex number `0+1*i`. `I` represents the
   * imaginary number `Sqrt(-1)`. `I^2` will be evaluated to `-1`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/I.md">I</a>
   */
  public static final IBuiltInSymbol I = F.initFinalSymbol("I", ID.I);

  /**
   * Identity(x) - is the identity function, which returns `x` unchanged. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Identity.md">Identity</a>
   */
  public static final IBuiltInSymbol Identity = F.initFinalSymbol("Identity", ID.Identity);

  /**
   * IdentityMatrix(n) - gives the identity matrix with `n` rows and columns. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IdentityMatrix.md">IdentityMatrix</a>
   */
  public static final IBuiltInSymbol IdentityMatrix =
      F.initFinalSymbol("IdentityMatrix", ID.IdentityMatrix);

  /**
   * If(cond, pos, neg) - returns `pos` if `cond` evaluates to `True`, and `neg` if it evaluates to
   * `False`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/If.md">If</a>
   */
  public static final IBuiltInSymbol If = F.initFinalSymbol("If", ID.If);

  public static final IBuiltInSymbol IgnoreCase = F.initFinalSymbol("IgnoreCase", ID.IgnoreCase);

  /**
   * Im(z) - returns the imaginary component of the complex number `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Im.md">Im</a>
   */
  public static final IBuiltInSymbol Im = F.initFinalSymbol("Im", ID.Im);

  /**
   * Implies(arg1, arg2) - Logical implication. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Implies.md">Implies</a>
   */
  public static final IBuiltInSymbol Implies = F.initFinalSymbol("Implies", ID.Implies);

  /**
   * Import("path-to-filename", "WXF") - if the file system is enabled, import an expression in WXF
   * format from the "path-to-filename" file. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Import.md">Import</a>
   */
  public static final IBuiltInSymbol Import = F.initFinalSymbol("Import", ID.Import);

  /**
   * In(k) - gives the `k`th line of input. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/In.md">In</a>
   */
  public static final IBuiltInSymbol In = F.initFinalSymbol("In", ID.In);

  /**
   * Increment(x) - increments `x` by `1`, returning the original value of `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Increment.md">Increment</a>
   */
  public static final IBuiltInSymbol Increment = F.initFinalSymbol("Increment", ID.Increment);

  /**
   * Indeterminate - represents an indeterminate result. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Indeterminate.md">Indeterminate</a>
   */
  public static final IBuiltInSymbol Indeterminate =
      F.initFinalSymbol("Indeterminate", ID.Indeterminate);

  public static final IBuiltInSymbol Inequality = F.initFinalSymbol("Inequality", ID.Inequality);

  /**
   * InexactNumberQ(expr) - returns `True` if `expr` is not an exact number, and `False` otherwise.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InexactNumberQ.md">InexactNumberQ</a>
   */
  public static final IBuiltInSymbol InexactNumberQ =
      F.initFinalSymbol("InexactNumberQ", ID.InexactNumberQ);

  /**
   * Infinity - represents an infinite real quantity. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Infinity.md">Infinity</a>
   */
  public static final IBuiltInSymbol Infinity = F.initFinalSymbol("Infinity", ID.Infinity);

  public static final IBuiltInSymbol Infix = F.initFinalSymbol("Infix", ID.Infix);

  public static final IBuiltInSymbol Information = F.initFinalSymbol("Information", ID.Information);

  public static final IBuiltInSymbol Inherited = F.initFinalSymbol("Inherited", ID.Inherited);

  /**
   * Inner(f, x, y, g) - computes a generalized inner product of `x` and `y`, using a multiplication
   * function `f` and an addition function `g`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Inner.md">Inner</a>
   */
  public static final IBuiltInSymbol Inner = F.initFinalSymbol("Inner", ID.Inner);

  /**
   * Input() - if the file system is enabled, the user can input an expression. After input this
   * expression will be evaluated immediately. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Input.md">Input</a>
   */
  public static final IBuiltInSymbol Input = F.initFinalSymbol("Input", ID.Input);

  public static final IBuiltInSymbol InputField = F.initFinalSymbol("InputField", ID.InputField);

  /**
   * InputForm(expr) - print the `expr` as if it should be inserted by the user for evaluation. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InputForm.md">InputForm</a>
   */
  public static final IBuiltInSymbol InputForm = F.initFinalSymbol("InputForm", ID.InputForm);

  /**
   * InputString() - if the file system is enabled, the user can input a string. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InputString.md">InputString</a>
   */
  public static final IBuiltInSymbol InputString = F.initFinalSymbol("InputString", ID.InputString);

  /**
   * Insert(list, elem, n) - inserts `elem` at position `n` in `list`. When `n` is negative, the
   * position is counted from the end. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Insert.md">Insert</a>
   */
  public static final IBuiltInSymbol Insert = F.initFinalSymbol("Insert", ID.Insert);

  /**
   * Integer - is the head of integers. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Integer.md">Integer</a>
   */
  public static final IBuiltInSymbol Integer = F.initFinalSymbol("Integer", ID.Integer);

  /**
   * IntegerDigits(n, base) - returns a list of integer digits for `n` under `base`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerDigits.md">IntegerDigits</a>
   */
  public static final IBuiltInSymbol IntegerDigits =
      F.initFinalSymbol("IntegerDigits", ID.IntegerDigits);

  /**
   * IntegerExponent(n, b) - gives the highest exponent of `b` that divides `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerExponent.md">IntegerExponent</a>
   */
  public static final IBuiltInSymbol IntegerExponent =
      F.initFinalSymbol("IntegerExponent", ID.IntegerExponent);

  /**
   * IntegerLength(x) - gives the number of digits in the base-10 representation of `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerLength.md">IntegerLength</a>
   */
  public static final IBuiltInSymbol IntegerLength =
      F.initFinalSymbol("IntegerLength", ID.IntegerLength);

  /**
   * IntegerName(integer-number) - gives the spoken number string of `integer-number` in language
   * `English`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerName.md">IntegerName</a>
   */
  public static final IBuiltInSymbol IntegerName = F.initFinalSymbol("IntegerName", ID.IntegerName);

  /**
   * IntegerPart(expr) - for real `expr` return the integer part of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerPart.md">IntegerPart</a>
   */
  public static final IBuiltInSymbol IntegerPart = F.initFinalSymbol("IntegerPart", ID.IntegerPart);

  /**
   * IntegerPartitions(n) - returns all partitions of the integer `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerPartitions.md">IntegerPartitions</a>
   */
  public static final IBuiltInSymbol IntegerPartitions =
      F.initFinalSymbol("IntegerPartitions", ID.IntegerPartitions);

  /**
   * IntegerQ(expr) - returns `True` if `expr` is an integer, and `False` otherwise. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerQ.md">IntegerQ</a>
   */
  public static final IBuiltInSymbol IntegerQ = F.initFinalSymbol("IntegerQ", ID.IntegerQ);

  /**
   * Integers - is the set of integer numbers. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Integers.md">Integers</a>
   */
  public static final IBuiltInSymbol Integers = F.initFinalSymbol("Integers", ID.Integers);

  /**
   * Integrate(f, x) - integrates `f` with respect to `x`. The result does not contain the additive
   * integration constant. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Integrate.md">Integrate</a>
   */
  public static final IBuiltInSymbol Integrate = F.initFinalSymbol("Integrate", ID.Integrate);

  /**
   * InterpolatingFunction(data-list) - get the representation for the given `data-list` as
   * piecewise `InterpolatingPolynomial`s. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterpolatingFunction.md">InterpolatingFunction</a>
   */
  public static final IBuiltInSymbol InterpolatingFunction =
      F.initFinalSymbol("InterpolatingFunction", ID.InterpolatingFunction);

  /**
   * InterpolatingPolynomial(data-list, symbol) - get the polynomial representation for the given
   * `data-list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterpolatingPolynomial.md">InterpolatingPolynomial</a>
   */
  public static final IBuiltInSymbol InterpolatingPolynomial =
      F.initFinalSymbol("InterpolatingPolynomial", ID.InterpolatingPolynomial);

  public static final IBuiltInSymbol Interpolation =
      F.initFinalSymbol("Interpolation", ID.Interpolation);

  /**
   * Interrupt( ) - Interrupt an evaluation and returns `$Aborted`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Interrupt.md">Interrupt</a>
   */
  public static final IBuiltInSymbol Interrupt = F.initFinalSymbol("Interrupt", ID.Interrupt);

  public static final IBuiltInSymbol IntersectingQ =
      F.initFinalSymbol("IntersectingQ", ID.IntersectingQ);

  /**
   * Intersection(set1, set2, ...) - get the intersection set from `set1` and `set2` .... | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Intersection.md">Intersection</a>
   */
  public static final IBuiltInSymbol Intersection =
      F.initFinalSymbol("Intersection", ID.Intersection);

  /**
   * Interval({a, b}) - represents the interval from `a` to `b`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Interval.md">Interval</a>
   */
  public static final IBuiltInSymbol Interval = F.initFinalSymbol("Interval", ID.Interval);

  /**
   * IntervalIntersection(interval_1, interval_2, ...) - compute the intersection of the intervals
   * `interval_1, interval_2, ...` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalIntersection.md">IntervalIntersection</a>
   */
  public static final IBuiltInSymbol IntervalIntersection =
      F.initFinalSymbol("IntervalIntersection", ID.IntervalIntersection);

  /**
   * IntervalMemberQ(interval, interval-or-real-number) - returns `True`, if
   * `interval-or-real-number` is completly sourrounded by `interval` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalMemberQ.md">IntervalMemberQ</a>
   */
  public static final IBuiltInSymbol IntervalMemberQ =
      F.initFinalSymbol("IntervalMemberQ", ID.IntervalMemberQ);

  /**
   * IntervalUnion(interval_1, interval_2, ...) - compute the union of the intervals `interval_1,
   * interval_2, ...` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalUnion.md">IntervalUnion</a>
   */
  public static final IBuiltInSymbol IntervalUnion =
      F.initFinalSymbol("IntervalUnion", ID.IntervalUnion);

  /**
   * Inverse(matrix) - computes the inverse of the `matrix`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Inverse.md">Inverse</a>
   */
  public static final IBuiltInSymbol Inverse = F.initFinalSymbol("Inverse", ID.Inverse);

  public static final IBuiltInSymbol InverseBetaRegularized =
      F.initFinalSymbol("InverseBetaRegularized", ID.InverseBetaRegularized);

  /**
   * InverseCDF(dist, q) - returns the inverse cumulative distribution for the distribution `dist`
   * as a function of `q` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseCDF.md">InverseCDF</a>
   */
  public static final IBuiltInSymbol InverseCDF = F.initFinalSymbol("InverseCDF", ID.InverseCDF);

  /**
   * InverseErf(z) - returns the inverse error function of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseErf.md">InverseErf</a>
   */
  public static final IBuiltInSymbol InverseErf = F.initFinalSymbol("InverseErf", ID.InverseErf);

  /**
   * InverseErfc(z) - returns the inverse complementary error function of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseErfc.md">InverseErfc</a>
   */
  public static final IBuiltInSymbol InverseErfc = F.initFinalSymbol("InverseErfc", ID.InverseErfc);

  /**
   * InverseFourier(vector-of-complex-numbers) - Inverse discrete Fourier transform of a
   * `vector-of-complex-numbers`. Fourier transform is restricted to vectors with length of power of
   * 2. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseFourier.md">InverseFourier</a>
   */
  public static final IBuiltInSymbol InverseFourier =
      F.initFinalSymbol("InverseFourier", ID.InverseFourier);

  /**
   * InverseFunction(head) - returns the inverse function for the symbol `head`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseFunction.md">InverseFunction</a>
   */
  public static final IBuiltInSymbol InverseFunction =
      F.initFinalSymbol("InverseFunction", ID.InverseFunction);

  public static final IBuiltInSymbol InverseGammaRegularized =
      F.initFinalSymbol("InverseGammaRegularized", ID.InverseGammaRegularized);

  /**
   * InverseHaversine(z) - returns the inverse haversine function of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseHaversine.md">InverseHaversine</a>
   */
  public static final IBuiltInSymbol InverseHaversine =
      F.initFinalSymbol("InverseHaversine", ID.InverseHaversine);

  /**
   * InverseLaplaceTransform(f,s,t) - returns the inverse laplace transform. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseLaplaceTransform.md">InverseLaplaceTransform</a>
   */
  public static final IBuiltInSymbol InverseLaplaceTransform =
      F.initFinalSymbol("InverseLaplaceTransform", ID.InverseLaplaceTransform);

  /**
   * InverseSeries( series ) - return the inverse series. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseSeries.md">InverseSeries</a>
   */
  public static final IBuiltInSymbol InverseSeries =
      F.initFinalSymbol("InverseSeries", ID.InverseSeries);

  public static final IBuiltInSymbol InverseWeierstrassP =
      F.initFinalSymbol("InverseWeierstrassP", ID.InverseWeierstrassP);

  /**
   * JSForm(expr) - returns the JavaScript form of the `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JSForm.md">JSForm</a>
   */
  public static final IBuiltInSymbol JSForm = F.initFinalSymbol("JSForm", ID.JSForm);

  public static final IBuiltInSymbol JSFormData = F.initFinalSymbol("JSFormData", ID.JSFormData);

  /**
   * JaccardDissimilarity(u, v) - returns the Jaccard-Needham dissimilarity between the two boolean
   * 1-D lists `u` and `v`, which is defined as `(c_tf + c_ft) / (c_tt + c_ft + c_tf)`, where n is
   * `len(u)` and `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JaccardDissimilarity.md">JaccardDissimilarity</a>
   */
  public static final IBuiltInSymbol JaccardDissimilarity =
      F.initFinalSymbol("JaccardDissimilarity", ID.JaccardDissimilarity);

  /**
   * JacobiAmplitude(x, m) - returns the amplitude `am(x, m)` for Jacobian elliptic function. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiAmplitude.md">JacobiAmplitude</a>
   */
  public static final IBuiltInSymbol JacobiAmplitude =
      F.initFinalSymbol("JacobiAmplitude", ID.JacobiAmplitude);

  public static final IBuiltInSymbol JacobiCD = F.initFinalSymbol("JacobiCD", ID.JacobiCD);

  /**
   * JacobiCN(x, m) - returns the Jacobian elliptic function `cn(x, m)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiCN.md">JacobiCN</a>
   */
  public static final IBuiltInSymbol JacobiCN = F.initFinalSymbol("JacobiCN", ID.JacobiCN);

  public static final IBuiltInSymbol JacobiDC = F.initFinalSymbol("JacobiDC", ID.JacobiDC);

  /**
   * JacobiDN(x, m) - returns the Jacobian elliptic function `dn(x, m)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiDN.md">JacobiDN</a>
   */
  public static final IBuiltInSymbol JacobiDN = F.initFinalSymbol("JacobiDN", ID.JacobiDN);

  /**
   * JacobiMatrix(matrix, var) - creates a Jacobian matrix. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiMatrix.md">JacobiMatrix</a>
   */
  public static final IBuiltInSymbol JacobiMatrix =
      F.initFinalSymbol("JacobiMatrix", ID.JacobiMatrix);

  public static final IBuiltInSymbol JacobiNC = F.initFinalSymbol("JacobiNC", ID.JacobiNC);

  public static final IBuiltInSymbol JacobiND = F.initFinalSymbol("JacobiND", ID.JacobiND);

  public static final IBuiltInSymbol JacobiSC = F.initFinalSymbol("JacobiSC", ID.JacobiSC);

  public static final IBuiltInSymbol JacobiSD = F.initFinalSymbol("JacobiSD", ID.JacobiSD);

  /**
   * JacobiSN(x, m) - returns the Jacobian elliptic function `sn(x, m)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSN.md">JacobiSN</a>
   */
  public static final IBuiltInSymbol JacobiSN = F.initFinalSymbol("JacobiSN", ID.JacobiSN);

  /**
   * JacobiSymbol(m, n) - calculates the Jacobi symbol. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSymbol.md">JacobiSymbol</a>
   */
  public static final IBuiltInSymbol JacobiSymbol =
      F.initFinalSymbol("JacobiSymbol", ID.JacobiSymbol);

  public static final IBuiltInSymbol JacobiZeta = F.initFinalSymbol("JacobiZeta", ID.JacobiZeta);

  /**
   * JavaForm(expr) - returns the Symja Java form of the `expr`. In Java you can use the created
   * Symja expressions. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaForm.md">JavaForm</a>
   */
  public static final IBuiltInSymbol JavaForm = F.initFinalSymbol("JavaForm", ID.JavaForm);

  /**
   * Join(l1, l2) - concatenates the lists `l1` and `l2`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Join.md">Join</a>
   */
  public static final IBuiltInSymbol Join = F.initFinalSymbol("Join", ID.Join);

  public static final IBuiltInSymbol KOrderlessPartitions =
      F.initFinalSymbol("KOrderlessPartitions", ID.KOrderlessPartitions);

  public static final IBuiltInSymbol KPartitions = F.initFinalSymbol("KPartitions", ID.KPartitions);

  /**
   * Key(key) - represents a `key` used to access a value in an association. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Key.md">Key</a>
   */
  public static final IBuiltInSymbol Key = F.initFinalSymbol("Key", ID.Key);

  public static final IBuiltInSymbol KeyAbsent = F.initFinalSymbol("KeyAbsent", ID.KeyAbsent);

  public static final IBuiltInSymbol KeyExistsQ = F.initFinalSymbol("KeyExistsQ", ID.KeyExistsQ);

  /**
   * KeySelect(<|key1->value1, ...|>, head) - returns an association of the elements for which
   * `head(keyi)` returns `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeySelect.md">KeySelect</a>
   */
  public static final IBuiltInSymbol KeySelect = F.initFinalSymbol("KeySelect", ID.KeySelect);

  /**
   * KeySort(<|key1->value1, ...|>) - sort the `<|key1->value1, ...|>` entries by the `key` values.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeySort.md">KeySort</a>
   */
  public static final IBuiltInSymbol KeySort = F.initFinalSymbol("KeySort", ID.KeySort);

  /**
   * KeyTake(<|key1->value1, ...|>, {k1, k2,...}) - returns an association of the rules for which
   * the `k1, k2,...` are keys in the association. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeyTake.md">KeyTake</a>
   */
  public static final IBuiltInSymbol KeyTake = F.initFinalSymbol("KeyTake", ID.KeyTake);

  /**
   * Keys(association) - return a list of keys of the `association`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Keys.md">Keys</a>
   */
  public static final IBuiltInSymbol Keys = F.initFinalSymbol("Keys", ID.Keys);

  /**
   * Khinchin - Khinchin's constant | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Khinchin.md">Khinchin</a>
   */
  public static final IBuiltInSymbol Khinchin = F.initFinalSymbol("Khinchin", ID.Khinchin);

  public static final IBuiltInSymbol KleinInvariantJ =
      F.initFinalSymbol("KleinInvariantJ", ID.KleinInvariantJ);

  public static final IBuiltInSymbol KnownUnitQ = F.initFinalSymbol("KnownUnitQ", ID.KnownUnitQ);

  /**
   * KolmogorovSmirnovTest(data) - Computes the `p-value`, or <i>observed significance level</i>, of
   * a one-sample [Wikipedia:Kolmogorov-Smirnov
   * test](http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test) evaluating the null hypothesis that
   * `data` conforms to the `NormalDistribution()`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KolmogorovSmirnovTest.md">KolmogorovSmirnovTest</a>
   */
  public static final IBuiltInSymbol KolmogorovSmirnovTest =
      F.initFinalSymbol("KolmogorovSmirnovTest", ID.KolmogorovSmirnovTest);

  /**
   * KroneckerDelta(arg1, arg2, ... argN) - if all arguments `arg1` to `argN` are equal return `1`,
   * otherwise return `0`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KroneckerDelta.md">KroneckerDelta</a>
   */
  public static final IBuiltInSymbol KroneckerDelta =
      F.initFinalSymbol("KroneckerDelta", ID.KroneckerDelta);

  /**
   * Kurtosis(list) - gives the Pearson measure of kurtosis for `list` (a measure of existing
   * outliers). | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Kurtosis.md">Kurtosis</a>
   */
  public static final IBuiltInSymbol Kurtosis = F.initFinalSymbol("Kurtosis", ID.Kurtosis);

  /**
   * LCM(n1, n2, ...) - computes the least common multiple of the given integers. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LCM.md">LCM</a>
   */
  public static final IBuiltInSymbol LCM = F.initFinalSymbol("LCM", ID.LCM);

  /**
   * LUDecomposition(matrix) - calculate the LUP-decomposition of a square `matrix`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LUDecomposition.md">LUDecomposition</a>
   */
  public static final IBuiltInSymbol LUDecomposition =
      F.initFinalSymbol("LUDecomposition", ID.LUDecomposition);

  public static final IBuiltInSymbol Labeled = F.initFinalSymbol("Labeled", ID.Labeled);

  /**
   * LaguerreL(n, x) - returns the Laguerre polynomial `L_n(x)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaguerreL.md">LaguerreL</a>
   */
  public static final IBuiltInSymbol LaguerreL = F.initFinalSymbol("LaguerreL", ID.LaguerreL);

  /**
   * LaplaceTransform(f,t,s) - returns the laplace transform. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaplaceTransform.md">LaplaceTransform</a>
   */
  public static final IBuiltInSymbol LaplaceTransform =
      F.initFinalSymbol("LaplaceTransform", ID.LaplaceTransform);

  /**
   * Last(expr) - returns the last element in `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Last.md">Last</a>
   */
  public static final IBuiltInSymbol Last = F.initFinalSymbol("Last", ID.Last);

  /**
   * LeafCount(expr) - returns the total number of indivisible subexpressions in `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeafCount.md">LeafCount</a>
   */
  public static final IBuiltInSymbol LeafCount = F.initFinalSymbol("LeafCount", ID.LeafCount);

  /**
   * LeastSquares(matrix, right) - solves the linear least-squares problem 'matrix . x = right'. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeastSquares.md">LeastSquares</a>
   */
  public static final IBuiltInSymbol LeastSquares =
      F.initFinalSymbol("LeastSquares", ID.LeastSquares);

  public static final IBuiltInSymbol Left = F.initFinalSymbol("Left", ID.Left);

  /**
   * LegendreP(n, x) - returns the Legendre polynomial `P_n(x)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendreP.md">LegendreP</a>
   */
  public static final IBuiltInSymbol LegendreP = F.initFinalSymbol("LegendreP", ID.LegendreP);

  /**
   * LegendreQ(n, x) - returns the Legendre functions of the second kind `Q_n(x)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendreQ.md">LegendreQ</a>
   */
  public static final IBuiltInSymbol LegendreQ = F.initFinalSymbol("LegendreQ", ID.LegendreQ);

  /**
   * Length(expr) - returns the number of leaves in `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Length.md">Length</a>
   */
  public static final IBuiltInSymbol Length = F.initFinalSymbol("Length", ID.Length);

  /**
   * Less(x, y) - yields `True` if `x` is known to be less than `y`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Less.md">Less</a>
   */
  public static final IBuiltInSymbol Less = F.initFinalSymbol("Less", ID.Less);

  /**
   * LessEqual(x, y) - yields `True` if `x` is known to be less than or equal `y`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqual.md">LessEqual</a>
   */
  public static final IBuiltInSymbol LessEqual = F.initFinalSymbol("LessEqual", ID.LessEqual);

  /**
   * LetterCharacter - represents letters.. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterCharacter.md">LetterCharacter</a>
   */
  public static final IBuiltInSymbol LetterCharacter =
      F.initFinalSymbol("LetterCharacter", ID.LetterCharacter);

  /**
   * LetterCounts(string) - count the number of each distinct character in the `string` and return
   * the result as an association `<|char->counter1, ...|>`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterCounts.md">LetterCounts</a>
   */
  public static final IBuiltInSymbol LetterCounts =
      F.initFinalSymbol("LetterCounts", ID.LetterCounts);

  /**
   * LetterQ(expr) - tests whether `expr` is a string, which only contains letters. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterQ.md">LetterQ</a>
   */
  public static final IBuiltInSymbol LetterQ = F.initFinalSymbol("LetterQ", ID.LetterQ);

  /**
   * Level(expr, levelspec) - gives a list of all sub-expressions of `expr` at the level(s)
   * specified by `levelspec`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Level.md">Level</a>
   */
  public static final IBuiltInSymbol Level = F.initFinalSymbol("Level", ID.Level);

  /**
   * LevelQ(expr) - tests whether `expr` is a valid level specification. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LevelQ.md">LevelQ</a>
   */
  public static final IBuiltInSymbol LevelQ = F.initFinalSymbol("LevelQ", ID.LevelQ);

  public static final IBuiltInSymbol Lexicographic =
      F.initFinalSymbol("Lexicographic", ID.Lexicographic);

  public static final IBuiltInSymbol LightBlue = F.initFinalSymbol("LightBlue", ID.LightBlue);

  public static final IBuiltInSymbol LightBrown = F.initFinalSymbol("LightBrown", ID.LightBrown);

  public static final IBuiltInSymbol LightCyan = F.initFinalSymbol("LightCyan", ID.LightCyan);

  public static final IBuiltInSymbol LightGray = F.initFinalSymbol("LightGray", ID.LightGray);

  public static final IBuiltInSymbol LightGreen = F.initFinalSymbol("LightGreen", ID.LightGreen);

  public static final IBuiltInSymbol LightMagenta =
      F.initFinalSymbol("LightMagenta", ID.LightMagenta);

  public static final IBuiltInSymbol LightOrange = F.initFinalSymbol("LightOrange", ID.LightOrange);

  public static final IBuiltInSymbol LightPink = F.initFinalSymbol("LightPink", ID.LightPink);

  public static final IBuiltInSymbol LightPurple = F.initFinalSymbol("LightPurple", ID.LightPurple);

  public static final IBuiltInSymbol LightRed = F.initFinalSymbol("LightRed", ID.LightRed);

  public static final IBuiltInSymbol LightYellow = F.initFinalSymbol("LightYellow", ID.LightYellow);

  /**
   * Limit(expr, x->x0) - gives the limit of `expr` as `x` approaches `x0` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Limit.md">Limit</a>
   */
  public static final IBuiltInSymbol Limit = F.initFinalSymbol("Limit", ID.Limit);

  public static final IBuiltInSymbol Line = F.initFinalSymbol("Line", ID.Line);

  public static final IBuiltInSymbol LineGraph = F.initFinalSymbol("LineGraph", ID.LineGraph);

  /**
   * LinearModelFit(list-of-data-points, expr, symbol) - In statistics, linear regression is a
   * linear approach to modeling the relationship between a scalar response (or dependent variable)
   * and one or more explanatory variables (or independent variables). | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearModelFit.md">LinearModelFit</a>
   */
  public static final IBuiltInSymbol LinearModelFit =
      F.initFinalSymbol("LinearModelFit", ID.LinearModelFit);

  /**
   * LinearProgramming(coefficientsOfLinearObjectiveFunction, constraintList,
   * constraintRelationList) - the `LinearProgramming` function provides an implementation of
   * [George Dantzig's simplex algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for
   * solving linear optimization problems with linear equality and inequality constraints and
   * implicit non-negative variables. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearProgramming.md">LinearProgramming</a>
   */
  public static final IBuiltInSymbol LinearProgramming =
      F.initFinalSymbol("LinearProgramming", ID.LinearProgramming);

  /**
   * LinearRecurrence(list1, list2, n) - solve the linear recurrence and return the generated
   * sequence of elements. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearRecurrence.md">LinearRecurrence</a>
   */
  public static final IBuiltInSymbol LinearRecurrence =
      F.initFinalSymbol("LinearRecurrence", ID.LinearRecurrence);

  /**
   * LinearSolve(matrix, right) - solves the linear equation system 'matrix . x = right' and returns
   * one corresponding solution `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearSolve.md">LinearSolve</a>
   */
  public static final IBuiltInSymbol LinearSolve = F.initFinalSymbol("LinearSolve", ID.LinearSolve);

  public static final IBuiltInSymbol LiouvilleLambda =
      F.initFinalSymbol("LiouvilleLambda", ID.LiouvilleLambda);

  /**
   * List(e1, e2, ..., ei) - represents a list containing the elements `e1...ei`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/List.md">List</a>
   */
  public static final IBuiltInSymbol List = F.initFinalSymbol("List", ID.List);

  public static final IBuiltInSymbol ListContourPlot =
      F.initFinalSymbol("ListContourPlot", ID.ListContourPlot);

  /**
   * ListConvolve(kernel-list, tensor-list) - create the convolution of the `kernel-list` with
   * `tensor-list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListConvolve.md">ListConvolve</a>
   */
  public static final IBuiltInSymbol ListConvolve =
      F.initFinalSymbol("ListConvolve", ID.ListConvolve);

  /**
   * ListCorrelate(kernel-list, tensor-list) - create the correlation of the `kernel-list` with
   * `tensor-list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListCorrelate.md">ListCorrelate</a>
   */
  public static final IBuiltInSymbol ListCorrelate =
      F.initFinalSymbol("ListCorrelate", ID.ListCorrelate);

  /**
   * ListLinePlot( { list-of-points } ) - generate a JavaScript list line plot control for the
   * `list-of-points`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLinePlot.md">ListLinePlot</a>
   */
  public static final IBuiltInSymbol ListLinePlot =
      F.initFinalSymbol("ListLinePlot", ID.ListLinePlot);

  /**
   * ListPlot( { list-of-points } ) - generate a JavaScript list plot control for the
   * `list-of-points`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListPlot.md">ListPlot</a>
   */
  public static final IBuiltInSymbol ListPlot = F.initFinalSymbol("ListPlot", ID.ListPlot);

  /**
   * ListPlot3D( { list-of-points } ) - generate a JavaScript list plot 3D control for the
   * `list-of-points`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListPlot3D.md">ListPlot3D</a>
   */
  public static final IBuiltInSymbol ListPlot3D = F.initFinalSymbol("ListPlot3D", ID.ListPlot3D);

  /**
   * ListQ(expr) - tests whether `expr` is a `List`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListQ.md">ListQ</a>
   */
  public static final IBuiltInSymbol ListQ = F.initFinalSymbol("ListQ", ID.ListQ);

  /**
   * Listable - is an attribute specifying that a function should be automatically applied to each
   * element of a list. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Listable.md">Listable</a>
   */
  public static final IBuiltInSymbol Listable = F.initFinalSymbol("Listable", ID.Listable);

  public static final IBuiltInSymbol Literal = F.initFinalSymbol("Literal", ID.Literal);

  /**
   * Log(z) - returns the natural logarithm of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log.md">Log</a>
   */
  public static final IBuiltInSymbol Log = F.initFinalSymbol("Log", ID.Log);

  /**
   * Log10(z) - returns the base-`10` logarithm of `z`. `Log10(z)` will be converted to
   * `Log(z)/Log(10)` in symbolic mode. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log10.md">Log10</a>
   */
  public static final IBuiltInSymbol Log10 = F.initFinalSymbol("Log10", ID.Log10);

  /**
   * Log2(z) - returns the base-`2` logarithm of `z`. `Log2(z)` will be converted to `Log(z)/Log(2)`
   * in symbolic mode. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log2.md">Log2</a>
   */
  public static final IBuiltInSymbol Log2 = F.initFinalSymbol("Log2", ID.Log2);

  /**
   * LogGamma(z) - is the logarithmic gamma function on the complex number `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogGamma.md">LogGamma</a>
   */
  public static final IBuiltInSymbol LogGamma = F.initFinalSymbol("LogGamma", ID.LogGamma);

  /**
   * LogIntegral(expr) - returns the integral logarithm of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogIntegral.md">LogIntegral</a>
   */
  public static final IBuiltInSymbol LogIntegral = F.initFinalSymbol("LogIntegral", ID.LogIntegral);

  /**
   * LogNormalDistribution(m, s) - returns a log-normal distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogNormalDistribution.md">LogNormalDistribution</a>
   */
  public static final IBuiltInSymbol LogNormalDistribution =
      F.initFinalSymbol("LogNormalDistribution", ID.LogNormalDistribution);

  public static final IBuiltInSymbol LogicalExpand =
      F.initFinalSymbol("LogicalExpand", ID.LogicalExpand);

  /**
   * LogisticSigmoid(z) - returns the logistic sigmoid of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogisticSigmoid.md">LogisticSigmoid</a>
   */
  public static final IBuiltInSymbol LogisticSigmoid =
      F.initFinalSymbol("LogisticSigmoid", ID.LogisticSigmoid);

  public static final IBuiltInSymbol LongForm = F.initFinalSymbol("LongForm", ID.LongForm);

  public static final IBuiltInSymbol Longest = F.initFinalSymbol("Longest", ID.Longest);

  /**
   * Lookup(association, key) - return the value in the `association` which is associated with the
   * `key`. If no value is available return `Missing("KeyAbsent",key)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Lookup.md">Lookup</a>
   */
  public static final IBuiltInSymbol Lookup = F.initFinalSymbol("Lookup", ID.Lookup);

  /**
   * LowerCaseQ(str) - is `True` if the given `str` is a string which only contains lower case
   * characters. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerCaseQ.md">LowerCaseQ</a>
   */
  public static final IBuiltInSymbol LowerCaseQ = F.initFinalSymbol("LowerCaseQ", ID.LowerCaseQ);

  /**
   * LowerTriangularize(matrix) - create a lower triangular matrix from the given `matrix`. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerTriangularize.md">LowerTriangularize</a>
   */
  public static final IBuiltInSymbol LowerTriangularize =
      F.initFinalSymbol("LowerTriangularize", ID.LowerTriangularize);

  /**
   * LucasL(n) - gives the `n`th Lucas number. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LucasL.md">LucasL</a>
   */
  public static final IBuiltInSymbol LucasL = F.initFinalSymbol("LucasL", ID.LucasL);

  /**
   * MachineNumberQ(expr) - returns `True` if `expr` is a machine-precision real or complex number.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MachineNumberQ.md">MachineNumberQ</a>
   */
  public static final IBuiltInSymbol MachineNumberQ =
      F.initFinalSymbol("MachineNumberQ", ID.MachineNumberQ);

  public static final IBuiltInSymbol Magenta = F.initFinalSymbol("Magenta", ID.Magenta);

  /**
   * MangoldtLambda(n) - the von Mangoldt function of `n` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MangoldtLambda.md">MangoldtLambda</a>
   */
  public static final IBuiltInSymbol MangoldtLambda =
      F.initFinalSymbol("MangoldtLambda", ID.MangoldtLambda);

  /**
   * ManhattanDistance(u, v) - returns the Manhattan distance between `u` and `v`, which is the
   * number of horizontal or vertical moves in the grid like Manhattan city layout to get from `u`
   * to `v`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ManhattanDistance.md">ManhattanDistance</a>
   */
  public static final IBuiltInSymbol ManhattanDistance =
      F.initFinalSymbol("ManhattanDistance", ID.ManhattanDistance);

  /**
   * Manipulate(plot, {x, min, max}) - generate a JavaScript control for the expression `plot` which
   * can be manipulated by a range slider `{x, min, max}`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Manipulate.md">Manipulate</a>
   */
  public static final IBuiltInSymbol Manipulate = F.initFinalSymbol("Manipulate", ID.Manipulate);

  public static final IBuiltInSymbol MantissaExponent =
      F.initFinalSymbol("MantissaExponent", ID.MantissaExponent);

  /**
   * Map(f, expr) or f /@ expr - applies `f` to each part on the first level of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Map.md">Map</a>
   */
  public static final IBuiltInSymbol Map = F.initFinalSymbol("Map", ID.Map);

  public static final IBuiltInSymbol MapAll = F.initFinalSymbol("MapAll", ID.MapAll);

  public static final IBuiltInSymbol MapAt = F.initFinalSymbol("MapAt", ID.MapAt);

  /**
   * MapIndexed(f, expr) - applies `f` to each part on the first level of `expr` and appending the
   * elements position as a list in the second argument. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MapIndexed.md">MapIndexed</a>
   */
  public static final IBuiltInSymbol MapIndexed = F.initFinalSymbol("MapIndexed", ID.MapIndexed);

  /**
   * MapThread(f, {{a1, a2, ...}, {b1, b2, ...}, ...}) - returns `{f(a1, b1, ...), f(a2, b2, ...),
   * ...}`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MapThread.md">MapThread</a>
   */
  public static final IBuiltInSymbol MapThread = F.initFinalSymbol("MapThread", ID.MapThread);

  /**
   * MatchQ(expr, form) - tests whether `expr` matches `form`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatchQ.md">MatchQ</a>
   */
  public static final IBuiltInSymbol MatchQ = F.initFinalSymbol("MatchQ", ID.MatchQ);

  /**
   * MatchingDissimilarity(u, v) - returns the Matching dissimilarity between the two boolean 1-D
   * lists `u` and `v`, which is defined as `(c_tf + c_ft) / n`, where `n` is `len(u)` and `c_ij` is
   * the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatchingDissimilarity.md">MatchingDissimilarity</a>
   */
  public static final IBuiltInSymbol MatchingDissimilarity =
      F.initFinalSymbol("MatchingDissimilarity", ID.MatchingDissimilarity);

  /**
   * MathMLForm(expr) - returns the MathML form of the evaluated `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MathMLForm.md">MathMLForm</a>
   */
  public static final IBuiltInSymbol MathMLForm = F.initFinalSymbol("MathMLForm", ID.MathMLForm);

  /**
   * MatrixExp(matrix) - computes the matrix exponential of the square `matrix`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixExp.md">MatrixExp</a>
   */
  public static final IBuiltInSymbol MatrixExp = F.initFinalSymbol("MatrixExp", ID.MatrixExp);

  /**
   * MatrixForm(matrix) - print a `matrix` or sparse array in matrix form | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixForm.md">MatrixForm</a>
   */
  public static final IBuiltInSymbol MatrixForm = F.initFinalSymbol("MatrixForm", ID.MatrixForm);

  /**
   * MatrixMinimalPolynomial(matrix, var) - computes the matrix minimal polynomial of a `matrix` for
   * the variable `var`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixMinimalPolynomial.md">MatrixMinimalPolynomial</a>
   */
  public static final IBuiltInSymbol MatrixMinimalPolynomial =
      F.initFinalSymbol("MatrixMinimalPolynomial", ID.MatrixMinimalPolynomial);

  /**
   * MatrixPlot( matrix ) - create a matrix plot. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixPlot.md">MatrixPlot</a>
   */
  public static final IBuiltInSymbol MatrixPlot = F.initFinalSymbol("MatrixPlot", ID.MatrixPlot);

  /**
   * MatrixPower(matrix, n) - computes the `n`th power of a `matrix` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixPower.md">MatrixPower</a>
   */
  public static final IBuiltInSymbol MatrixPower = F.initFinalSymbol("MatrixPower", ID.MatrixPower);

  /**
   * MatrixQ(m) - returns `True` if `m` is a list of equal-length lists. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixQ.md">MatrixQ</a>
   */
  public static final IBuiltInSymbol MatrixQ = F.initFinalSymbol("MatrixQ", ID.MatrixQ);

  /**
   * MatrixRank(matrix) - returns the rank of `matrix`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixRank.md">MatrixRank</a>
   */
  public static final IBuiltInSymbol MatrixRank = F.initFinalSymbol("MatrixRank", ID.MatrixRank);

  /**
   * Max(e_1, e_2, ..., e_i) - returns the expression with the greatest value among the `e_i`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Max.md">Max</a>
   */
  public static final IBuiltInSymbol Max = F.initFinalSymbol("Max", ID.Max);

  /**
   * MaxFilter(list, r) - filter which evaluates the `Max` of `list` for the radius `r`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MaxFilter.md">MaxFilter</a>
   */
  public static final IBuiltInSymbol MaxFilter = F.initFinalSymbol("MaxFilter", ID.MaxFilter);

  public static final IBuiltInSymbol MaxIterations =
      F.initFinalSymbol("MaxIterations", ID.MaxIterations);

  public static final IBuiltInSymbol MaxPoints = F.initFinalSymbol("MaxPoints", ID.MaxPoints);

  /**
   * Maximize(unary-function, variable) - returns the maximum of the unary function for the given
   * `variable`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Maximize.md">Maximize</a>
   */
  public static final IBuiltInSymbol Maximize = F.initFinalSymbol("Maximize", ID.Maximize);

  /**
   * Mean(list) - returns the statistical mean of `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Mean.md">Mean</a>
   */
  public static final IBuiltInSymbol Mean = F.initFinalSymbol("Mean", ID.Mean);

  public static final IBuiltInSymbol MeanDeviation =
      F.initFinalSymbol("MeanDeviation", ID.MeanDeviation);

  /**
   * MeanFilter(list, r) - filter which evaluates the `Mean` of `list` for the radius `r`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeanFilter.md">MeanFilter</a>
   */
  public static final IBuiltInSymbol MeanFilter = F.initFinalSymbol("MeanFilter", ID.MeanFilter);

  /**
   * Median(list) - returns the median of `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Median.md">Median</a>
   */
  public static final IBuiltInSymbol Median = F.initFinalSymbol("Median", ID.Median);

  /**
   * MedianFilter(list, r) - filter which evaluates the `Median` of `list` for the radius `r`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MedianFilter.md">MedianFilter</a>
   */
  public static final IBuiltInSymbol MedianFilter =
      F.initFinalSymbol("MedianFilter", ID.MedianFilter);

  public static final IBuiltInSymbol MeijerG = F.initFinalSymbol("MeijerG", ID.MeijerG);

  /**
   * MemberQ(list, pattern) - returns `True` if pattern matches any element of `list`, or `False`
   * otherwise. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MemberQ.md">MemberQ</a>
   */
  public static final IBuiltInSymbol MemberQ = F.initFinalSymbol("MemberQ", ID.MemberQ);

  /**
   * MersennePrimeExponent(n) - returns the `n`th mersenne prime exponent. `2^n - 1` must be a prime
   * number. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MersennePrimeExponent.md">MersennePrimeExponent</a>
   */
  public static final IBuiltInSymbol MersennePrimeExponent =
      F.initFinalSymbol("MersennePrimeExponent", ID.MersennePrimeExponent);

  /**
   * MersennePrimeExponentQ(n) - returns `True` if `2^n - 1` is a prime number. Currently `0 <= n <=
   * 47` can be computed in reasonable time. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MersennePrimeExponentQ.md">MersennePrimeExponentQ</a>
   */
  public static final IBuiltInSymbol MersennePrimeExponentQ =
      F.initFinalSymbol("MersennePrimeExponentQ", ID.MersennePrimeExponentQ);

  public static final IBuiltInSymbol MeshRange = F.initFinalSymbol("MeshRange", ID.MeshRange);

  /**
   * Message(symbol::msg, expr1, expr2, ...) - displays the specified message, replacing
   * placeholders in the message text with the corresponding expressions. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Message.md">Message</a>
   */
  public static final IBuiltInSymbol Message = F.initFinalSymbol("Message", ID.Message);

  /**
   * MessageName(symbol, msg) - `symbol::msg` identifies a message. `MessageName` is the head of
   * message IDs of the form `symbol::tag`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MessageName.md">MessageName</a>
   */
  public static final IBuiltInSymbol MessageName = F.initFinalSymbol("MessageName", ID.MessageName);

  /**
   * Messages(symbol) - return all messages which are asociated to `symbol`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Messages.md">Messages</a>
   */
  public static final IBuiltInSymbol Messages = F.initFinalSymbol("Messages", ID.Messages);

  public static final IBuiltInSymbol Method = F.initFinalSymbol("Method", ID.Method);

  /**
   * Min(e_1, e_2, ..., e_i) - returns the expression with the lowest value among the `e_i`. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Min.md">Min</a>
   */
  public static final IBuiltInSymbol Min = F.initFinalSymbol("Min", ID.Min);

  /**
   * MinFilter(list, r) - filter which evaluates the `Min` of `list` for the radius `r`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MinFilter.md">MinFilter</a>
   */
  public static final IBuiltInSymbol MinFilter = F.initFinalSymbol("MinFilter", ID.MinFilter);

  public static final IBuiltInSymbol MinMax = F.initFinalSymbol("MinMax", ID.MinMax);

  public static final IBuiltInSymbol MinimalPolynomial =
      F.initFinalSymbol("MinimalPolynomial", ID.MinimalPolynomial);

  /**
   * Minimize(unary-function, variable) - returns the minimum of the unary function for the given
   * `variable`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Minimize.md">Minimize</a>
   */
  public static final IBuiltInSymbol Minimize = F.initFinalSymbol("Minimize", ID.Minimize);

  /**
   * Minus(expr) - is the negation of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Minus.md">Minus</a>
   */
  public static final IBuiltInSymbol Minus = F.initFinalSymbol("Minus", ID.Minus);

  public static final IBuiltInSymbol Missing = F.initFinalSymbol("Missing", ID.Missing);

  /**
   * MissingQ(expr) - returns `True` if `expr` is a `Missing()` expression. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MissingQ.md">MissingQ</a>
   */
  public static final IBuiltInSymbol MissingQ = F.initFinalSymbol("MissingQ", ID.MissingQ);

  /**
   * Mod(x, m) - returns `x` modulo `m`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Mod.md">Mod</a>
   */
  public static final IBuiltInSymbol Mod = F.initFinalSymbol("Mod", ID.Mod);

  /**
   * Module({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables`
   * by renaming local variables. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Module.md">Module</a>
   */
  public static final IBuiltInSymbol Module = F.initFinalSymbol("Module", ID.Module);

  public static final IBuiltInSymbol Modulus = F.initFinalSymbol("Modulus", ID.Modulus);

  /**
   * MoebiusMu(expr) - calculate the Möbius function. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoebiusMu.md">MoebiusMu</a>
   */
  public static final IBuiltInSymbol MoebiusMu = F.initFinalSymbol("MoebiusMu", ID.MoebiusMu);

  /**
   * MonomialList(polynomial, list-of-variables) - get the list of monomials of a `polynomial`
   * expression, with respect to the `list-of-variables`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MonomialList.md">MonomialList</a>
   */
  public static final IBuiltInSymbol MonomialList =
      F.initFinalSymbol("MonomialList", ID.MonomialList);

  public static final IBuiltInSymbol MonomialOrder =
      F.initFinalSymbol("MonomialOrder", ID.MonomialOrder);

  /**
   * Most(expr) - returns `expr` with the last element removed. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Most.md">Most</a>
   */
  public static final IBuiltInSymbol Most = F.initFinalSymbol("Most", ID.Most);

  /**
   * Multinomial(n1, n2, ...) - gives the multinomial coefficient `(n1+n2+...)!/(n1! n2! ...)`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Multinomial.md">Multinomial</a>
   */
  public static final IBuiltInSymbol Multinomial = F.initFinalSymbol("Multinomial", ID.Multinomial);

  /**
   * MultiplicativeOrder(a, n) - gives the multiplicative order `a` modulo `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MultiplicativeOrder.md">MultiplicativeOrder</a>
   */
  public static final IBuiltInSymbol MultiplicativeOrder =
      F.initFinalSymbol("MultiplicativeOrder", ID.MultiplicativeOrder);

  /**
   * N(expr) - gives the numerical value of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/N.md">N</a>
   */
  public static final IBuiltInSymbol N = F.initFinalSymbol("N", ID.N);

  /**
   * ND(function, x, value) - returns a numerical approximation of the partial derivative of the
   * `function` for the variable `x` and the given `value`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ND.md">ND</a>
   */
  public static final IBuiltInSymbol ND = F.initFinalSymbol("ND", ID.ND);

  /**
   * NDSolve({equation-list}, functions, t) - attempts to solve the linear differential
   * `equation-list` for the `functions` and the time-dependent-variable `t`. Returns an
   * `InterpolatingFunction` function object. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NDSolve.md">NDSolve</a>
   */
  public static final IBuiltInSymbol NDSolve = F.initFinalSymbol("NDSolve", ID.NDSolve);

  public static final IBuiltInSymbol NFourierTransform =
      F.initFinalSymbol("NFourierTransform", ID.NFourierTransform);

  /**
   * NHoldAll - is an attribute that protects all arguments of a function from numeric evaluation. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NHoldAll.md">NHoldAll</a>
   */
  public static final IBuiltInSymbol NHoldAll = F.initFinalSymbol("NHoldAll", ID.NHoldAll);

  /**
   * NHoldFirst - is an attribute that protects the first argument of a function from numeric
   * evaluation. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NHoldFirst.md">NHoldFirst</a>
   */
  public static final IBuiltInSymbol NHoldFirst = F.initFinalSymbol("NHoldFirst", ID.NHoldFirst);

  /**
   * NHoldRest - is an attribute that protects all but the first argument of a function from numeric
   * evaluation. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NHoldRest.md">NHoldRest</a>
   */
  public static final IBuiltInSymbol NHoldRest = F.initFinalSymbol("NHoldRest", ID.NHoldRest);

  /**
   * NIntegrate(f, {x,a,b}) - computes the numerical univariate real integral of `f` with respect to
   * `x` from `a` to `b`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NIntegrate.md">NIntegrate</a>
   */
  public static final IBuiltInSymbol NIntegrate = F.initFinalSymbol("NIntegrate", ID.NIntegrate);

  /**
   * NMaximize({maximize_function, constraints}, variables_list) - the `NMaximize` function provides
   * an implementation of [George Dantzig's simplex
   * algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear optimization
   * problems with linear equality and inequality constraints and implicit non-negative variables. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NMaximize.md">NMaximize</a>
   */
  public static final IBuiltInSymbol NMaximize = F.initFinalSymbol("NMaximize", ID.NMaximize);

  /**
   * NMinimize({maximize_function, constraints}, variables_list) - the `NMinimize` function provides
   * an implementation of [George Dantzig's simplex
   * algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear optimization
   * problems with linear equality and inequality constraints and implicit non-negative variables. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NMinimize.md">NMinimize</a>
   */
  public static final IBuiltInSymbol NMinimize = F.initFinalSymbol("NMinimize", ID.NMinimize);

  /**
   * NRoots(polynomial==0) - gives the numerical roots of a univariate polynomial `polynomial`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NRoots.md">NRoots</a>
   */
  public static final IBuiltInSymbol NRoots = F.initFinalSymbol("NRoots", ID.NRoots);

  /**
   * NSolve(equations, vars) - attempts to solve `equations` for the variables `vars`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NSolve.md">NSolve</a>
   */
  public static final IBuiltInSymbol NSolve = F.initFinalSymbol("NSolve", ID.NSolve);

  /**
   * NakagamiDistribution(m, o) - returns a Nakagami distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NakagamiDistribution.md">NakagamiDistribution</a>
   */
  public static final IBuiltInSymbol NakagamiDistribution =
      F.initFinalSymbol("NakagamiDistribution", ID.NakagamiDistribution);

  /**
   * Names(string) - return the symbols from the context path matching the `string` or `pattern`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Names.md">Names</a>
   */
  public static final IBuiltInSymbol Names = F.initFinalSymbol("Names", ID.Names);

  /**
   * Nand(arg1, arg2, ...) - Logical NAND function. It evaluates its arguments in order, giving
   * `True` immediately if any of them are `False`, and `False` if they are all `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nand.md">Nand</a>
   */
  public static final IBuiltInSymbol Nand = F.initFinalSymbol("Nand", ID.Nand);

  public static final IBuiltInSymbol Nearest = F.initFinalSymbol("Nearest", ID.Nearest);

  public static final IBuiltInSymbol Needs = F.initFinalSymbol("Needs", ID.Needs);

  /**
   * Negative(x) - returns `True` if `x` is a negative real number. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Negative.md">Negative</a>
   */
  public static final IBuiltInSymbol Negative = F.initFinalSymbol("Negative", ID.Negative);

  public static final IBuiltInSymbol NegativeDegreeLexicographic =
      F.initFinalSymbol("NegativeDegreeLexicographic", ID.NegativeDegreeLexicographic);

  public static final IBuiltInSymbol NegativeDegreeReverseLexicographic =
      F.initFinalSymbol(
          "NegativeDegreeReverseLexicographic", ID.NegativeDegreeReverseLexicographic);

  public static final IBuiltInSymbol NegativeLexicographic =
      F.initFinalSymbol("NegativeLexicographic", ID.NegativeLexicographic);

  /**
   * Nest(f, expr, n) - starting with `expr`, iteratively applies `f` `n` times and returns the
   * final result. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nest.md">Nest</a>
   */
  public static final IBuiltInSymbol Nest = F.initFinalSymbol("Nest", ID.Nest);

  /**
   * NestList(f, expr, n) - starting with `expr`, iteratively applies `f` `n` times and returns a
   * list of all intermediate results. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestList.md">NestList</a>
   */
  public static final IBuiltInSymbol NestList = F.initFinalSymbol("NestList", ID.NestList);

  /**
   * NestWhile(f, expr, test) - applies a function `f` repeatedly on an expression `expr`, until
   * applying `test` on the result no longer yields `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestWhile.md">NestWhile</a>
   */
  public static final IBuiltInSymbol NestWhile = F.initFinalSymbol("NestWhile", ID.NestWhile);

  /**
   * NestWhileList(f, expr, test) - applies a function `f` repeatedly on an expression `expr`, until
   * applying `test` on the result no longer yields `True`. It returns a list of all intermediate
   * results. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestWhileList.md">NestWhileList</a>
   */
  public static final IBuiltInSymbol NestWhileList =
      F.initFinalSymbol("NestWhileList", ID.NestWhileList);

  /**
   * NextPrime(n) - gives the next prime after `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NextPrime.md">NextPrime</a>
   */
  public static final IBuiltInSymbol NextPrime = F.initFinalSymbol("NextPrime", ID.NextPrime);

  public static final IBuiltInSymbol NonCommutativeMultiply =
      F.initFinalSymbol("NonCommutativeMultiply", ID.NonCommutativeMultiply);

  /**
   * NonNegative(x) - returns `True` if `x` is a positive real number or zero. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NonNegative.md">NonNegative</a>
   */
  public static final IBuiltInSymbol NonNegative = F.initFinalSymbol("NonNegative", ID.NonNegative);

  /**
   * NonPositive(x) - returns `True` if `x` is a negative real number or zero. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NonPositive.md">NonPositive</a>
   */
  public static final IBuiltInSymbol NonPositive = F.initFinalSymbol("NonPositive", ID.NonPositive);

  /**
   * None - is a possible value for `Span` and `Quiet`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/None.md">None</a>
   */
  public static final IBuiltInSymbol None = F.initFinalSymbol("None", ID.None);

  /**
   * NoneTrue({expr1, expr2, ...}, test) - returns `True` if no application of `test` to `expr1,
   * expr2, ...` evaluates to `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NoneTrue.md">NoneTrue</a>
   */
  public static final IBuiltInSymbol NoneTrue = F.initFinalSymbol("NoneTrue", ID.NoneTrue);

  public static final IBuiltInSymbol Nonexistent = F.initFinalSymbol("Nonexistent", ID.Nonexistent);

  /**
   * Nor(arg1, arg2, ...)' - Logical NOR function. It evaluates its arguments in order, giving
   * `False` immediately if any of them are `True`, and `True` if they are all `False`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nor.md">Nor</a>
   */
  public static final IBuiltInSymbol Nor = F.initFinalSymbol("Nor", ID.Nor);

  /**
   * Norm(m, l) - computes the `l`-norm of matrix `m` (currently only works for vectors!). | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Norm.md">Norm</a>
   */
  public static final IBuiltInSymbol Norm = F.initFinalSymbol("Norm", ID.Norm);

  /**
   * Normal(expr) - converts a Symja expression `expr` into a normal expression. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Normal.md">Normal</a>
   */
  public static final IBuiltInSymbol Normal = F.initFinalSymbol("Normal", ID.Normal);

  /**
   * NormalDistribution(m, s) - returns the normal distribution of mean `m` and sigma `s`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NormalDistribution.md">NormalDistribution</a>
   */
  public static final IBuiltInSymbol NormalDistribution =
      F.initFinalSymbol("NormalDistribution", ID.NormalDistribution);

  /**
   * Normalize(v) - calculates the normalized vector `v` as `v/Norm(v)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Normalize.md">Normalize</a>
   */
  public static final IBuiltInSymbol Normalize = F.initFinalSymbol("Normalize", ID.Normalize);

  /**
   * Not(expr) - Logical Not function (negation). Returns `True` if the statement is `False`.
   * Returns `False` if the `expr` is `True` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Not.md">Not</a>
   */
  public static final IBuiltInSymbol Not = F.initFinalSymbol("Not", ID.Not);

  public static final IBuiltInSymbol NotApplicable =
      F.initFinalSymbol("NotApplicable", ID.NotApplicable);

  public static final IBuiltInSymbol NotAvailable =
      F.initFinalSymbol("NotAvailable", ID.NotAvailable);

  public static final IBuiltInSymbol NotElement = F.initFinalSymbol("NotElement", ID.NotElement);

  public static final IBuiltInSymbol NotListQ = F.initFinalSymbol("NotListQ", ID.NotListQ);

  public static final IBuiltInSymbol Nothing = F.initFinalSymbol("Nothing", ID.Nothing);

  public static final IBuiltInSymbol Now = F.initFinalSymbol("Now", ID.Now);

  /**
   * Null - is the implicit result of expressions that do not yield a result. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Null.md">Null</a>
   */
  public static final IBuiltInSymbol Null = F.initFinalSymbol("Null", ID.Null);

  /**
   * NullSpace(matrix) - returns a list of vectors that span the nullspace of the `matrix`. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NullSpace.md">NullSpace</a>
   */
  public static final IBuiltInSymbol NullSpace = F.initFinalSymbol("NullSpace", ID.NullSpace);

  public static final IBuiltInSymbol Number = F.initFinalSymbol("Number", ID.Number);

  public static final IBuiltInSymbol NumberFieldRootsOfUnity =
      F.initFinalSymbol("NumberFieldRootsOfUnity", ID.NumberFieldRootsOfUnity);

  /**
   * NumberQ(expr) - returns `True` if `expr` is an explicit number, and `False` otherwise. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberQ.md">NumberQ</a>
   */
  public static final IBuiltInSymbol NumberQ = F.initFinalSymbol("NumberQ", ID.NumberQ);

  /**
   * NumberString - represents the characters in a number. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberString.md">NumberString</a>
   */
  public static final IBuiltInSymbol NumberString =
      F.initFinalSymbol("NumberString", ID.NumberString);

  /**
   * Numerator(expr) - gives the numerator in `expr`. Numerator collects expressions with non
   * negative exponents. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Numerator.md">Numerator</a>
   */
  public static final IBuiltInSymbol Numerator = F.initFinalSymbol("Numerator", ID.Numerator);

  public static final IBuiltInSymbol NumericArray =
      F.initFinalSymbol("NumericArray", ID.NumericArray);

  public static final IBuiltInSymbol NumericArrayQ =
      F.initFinalSymbol("NumericArrayQ", ID.NumericArrayQ);

  public static final IBuiltInSymbol NumericArrayType =
      F.initFinalSymbol("NumericArrayType", ID.NumericArrayType);

  public static final IBuiltInSymbol NumericFunction =
      F.initFinalSymbol("NumericFunction", ID.NumericFunction);

  /**
   * NumericQ(expr) - returns `True` if `expr` is an explicit numeric expression, and `False`
   * otherwise. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumericQ.md">NumericQ</a>
   */
  public static final IBuiltInSymbol NumericQ = F.initFinalSymbol("NumericQ", ID.NumericQ);

  public static final IBuiltInSymbol NuttallWindow =
      F.initFinalSymbol("NuttallWindow", ID.NuttallWindow);

  public static final IBuiltInSymbol O = F.initFinalSymbol("O", ID.O);

  /**
   * OddQ(x) - returns `True` if `x` is odd, and `False` otherwise. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OddQ.md">OddQ</a>
   */
  public static final IBuiltInSymbol OddQ = F.initFinalSymbol("OddQ", ID.OddQ);

  /**
   * Off( ) - switch off the interactive trace. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Off.md">Off</a>
   */
  public static final IBuiltInSymbol Off = F.initFinalSymbol("Off", ID.Off);

  /**
   * On( ) - switch on the interactive trace. The output is printed in the defined `out` stream. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/On.md">On</a>
   */
  public static final IBuiltInSymbol On = F.initFinalSymbol("On", ID.On);

  /**
   * OneIdentity - is an attribute specifying that `f(x)` should be treated as equivalent to `x` in
   * pattern matching. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OneIdentity.md">OneIdentity</a>
   */
  public static final IBuiltInSymbol OneIdentity = F.initFinalSymbol("OneIdentity", ID.OneIdentity);

  /**
   * Operate(p, expr) - applies `p` to the head of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Operate.md">Operate</a>
   */
  public static final IBuiltInSymbol Operate = F.initFinalSymbol("Operate", ID.Operate);

  /**
   * OptimizeExpression(function) - common subexpressions elimination for a complicated `function`
   * by generating "dummy" variables for these subexpressions. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OptimizeExpression.md">OptimizeExpression</a>
   */
  public static final IBuiltInSymbol OptimizeExpression =
      F.initFinalSymbol("OptimizeExpression", ID.OptimizeExpression);

  /**
   * OptionValue(name) - gives the value of the option `name` as specified in a call to a function
   * with `OptionsPattern`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OptionValue.md">OptionValue</a>
   */
  public static final IBuiltInSymbol OptionValue = F.initFinalSymbol("OptionValue", ID.OptionValue);

  /**
   * Optional(patt, default) - is a pattern which matches `patt`, which if omitted should be
   * replaced by `default`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Optional.md">Optional</a>
   */
  public static final IBuiltInSymbol Optional = F.initFinalSymbol("Optional", ID.Optional);

  /**
   * Options(symbol) - gives a list of optional arguments to `symbol` and their default values. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Options.md">Options</a>
   */
  public static final IBuiltInSymbol Options = F.initFinalSymbol("Options", ID.Options);

  public static final IBuiltInSymbol OptionsPattern =
      F.initFinalSymbol("OptionsPattern", ID.OptionsPattern);

  /**
   * Or(expr1, expr2, ...)' - `expr1 || expr2 || ...` evaluates each expression in turn, returning
   * `True` as soon as an expression evaluates to `True`. If all expressions evaluate to `False`,
   * `Or` returns `False`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Or.md">Or</a>
   */
  public static final IBuiltInSymbol Or = F.initFinalSymbol("Or", ID.Or);

  public static final IBuiltInSymbol Orange = F.initFinalSymbol("Orange", ID.Orange);

  /**
   * Order(a, b) - is `0` if `a` equals `b`. Is `-1` or `1` according to canonical order of `a` and
   * `b`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Order.md">Order</a>
   */
  public static final IBuiltInSymbol Order = F.initFinalSymbol("Order", ID.Order);

  /**
   * OrderedQ({a, b}) - is `True` if `a` sorts before `b` according to canonical ordering. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OrderedQ.md">OrderedQ</a>
   */
  public static final IBuiltInSymbol OrderedQ = F.initFinalSymbol("OrderedQ", ID.OrderedQ);

  /**
   * Ordering(list) - calculate the permutation list of the elements in the sorted `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ordering.md">Ordering</a>
   */
  public static final IBuiltInSymbol Ordering = F.initFinalSymbol("Ordering", ID.Ordering);

  /**
   * Orderless - is an attribute indicating that the leaves in an expression `f(a, b, c)` can be
   * placed in any order. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Orderless.md">Orderless</a>
   */
  public static final IBuiltInSymbol Orderless = F.initFinalSymbol("Orderless", ID.Orderless);

  /**
   * OrthogonalMatrixQ(matrix) - returns `True`, if `matrix` is an orthogonal matrix. `False`
   * otherwise. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OrthogonalMatrixQ.md">OrthogonalMatrixQ</a>
   */
  public static final IBuiltInSymbol OrthogonalMatrixQ =
      F.initFinalSymbol("OrthogonalMatrixQ", ID.OrthogonalMatrixQ);

  /**
   * Orthogonalize(matrix) - returns a basis for the orthogonalized set of vectors defined by
   * `matrix`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Orthogonalize.md">Orthogonalize</a>
   */
  public static final IBuiltInSymbol Orthogonalize =
      F.initFinalSymbol("Orthogonalize", ID.Orthogonalize);

  /**
   * Out(k) - gives the result of the `k`th input line. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Out.md">Out</a>
   */
  public static final IBuiltInSymbol Out = F.initFinalSymbol("Out", ID.Out);

  /**
   * Outer(f, x, y) - computes a generalised outer product of `x` and `y`, using the function `f` in
   * place of multiplication. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Outer.md">Outer</a>
   */
  public static final IBuiltInSymbol Outer = F.initFinalSymbol("Outer", ID.Outer);

  public static final IBuiltInSymbol OutputForm = F.initFinalSymbol("OutputForm", ID.OutputForm);

  public static final IBuiltInSymbol OutputStream =
      F.initFinalSymbol("OutputStream", ID.OutputStream);

  /**
   * OwnValues(symbol) - prints the own-value rule associated with `symbol`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OwnValues.md">OwnValues</a>
   */
  public static final IBuiltInSymbol OwnValues = F.initFinalSymbol("OwnValues", ID.OwnValues);

  /**
   * PDF(distribution, value) - returns the probability density function of `value`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PDF.md">PDF</a>
   */
  public static final IBuiltInSymbol PDF = F.initFinalSymbol("PDF", ID.PDF);

  public static final IBuiltInSymbol Package = F.initFinalSymbol("Package", ID.Package);

  /**
   * PadLeft(list, n) - pads `list` to length `n` by adding `0` on the left. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PadLeft.md">PadLeft</a>
   */
  public static final IBuiltInSymbol PadLeft = F.initFinalSymbol("PadLeft", ID.PadLeft);

  /**
   * PadRight(list, n) - pads `list` to length `n` by adding `0` on the right. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PadRight.md">PadRight</a>
   */
  public static final IBuiltInSymbol PadRight = F.initFinalSymbol("PadRight", ID.PadRight);

  /**
   * ParametricPlot({function1, function2}, {t, tMin, tMax}) - generate a JavaScript control for the
   * parametric expressions `function1`, `function2` in the `t` range `{t, tMin, tMax}`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ParametricPlot.md">ParametricPlot</a>
   */
  public static final IBuiltInSymbol ParametricPlot =
      F.initFinalSymbol("ParametricPlot", ID.ParametricPlot);

  /**
   * Part(expr, i) - returns part `i` of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part</a>
   */
  public static final IBuiltInSymbol Part = F.initFinalSymbol("Part", ID.Part);

  /**
   * Partition(list, n) - partitions `list` into sublists of length `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Partition.md">Partition</a>
   */
  public static final IBuiltInSymbol Partition = F.initFinalSymbol("Partition", ID.Partition);

  /**
   * PartitionsP(n) - gives the number of unrestricted partitions of the integer `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PartitionsP.md">PartitionsP</a>
   */
  public static final IBuiltInSymbol PartitionsP = F.initFinalSymbol("PartitionsP", ID.PartitionsP);

  /**
   * PartitionsQ(n) - gives the number of partitions of the integer `n` into distinct parts | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PartitionsQ.md">PartitionsQ</a>
   */
  public static final IBuiltInSymbol PartitionsQ = F.initFinalSymbol("PartitionsQ", ID.PartitionsQ);

  public static final IBuiltInSymbol ParzenWindow =
      F.initFinalSymbol("ParzenWindow", ID.ParzenWindow);

  public static final IBuiltInSymbol Pattern = F.initFinalSymbol("Pattern", ID.Pattern);

  public static final IBuiltInSymbol PatternOrder =
      F.initFinalSymbol("PatternOrder", ID.PatternOrder);

  /**
   * PatternTest(pattern, test) - constrains `pattern` to match `expr` only if the evaluation of
   * `test(expr)` yields `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PatternTest.md">PatternTest</a>
   */
  public static final IBuiltInSymbol PatternTest = F.initFinalSymbol("PatternTest", ID.PatternTest);

  /**
   * Pause(seconds) - pause the thread for the number of `seconds`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pause.md">Pause</a>
   */
  public static final IBuiltInSymbol Pause = F.initFinalSymbol("Pause", ID.Pause);

  public static final IBuiltInSymbol PearsonChiSquareTest =
      F.initFinalSymbol("PearsonChiSquareTest", ID.PearsonChiSquareTest);

  /**
   * PerfectNumber(n) - returns the `n`th perfect number. In number theory, a perfect number is a
   * positive integer that is equal to the sum of its proper | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PerfectNumber.md">PerfectNumber</a>
   */
  public static final IBuiltInSymbol PerfectNumber =
      F.initFinalSymbol("PerfectNumber", ID.PerfectNumber);

  /**
   * PerfectNumberQ(n) - returns `True` if `n` is a perfect number. In number theory, a perfect
   * number is a positive integer that is equal to the sum of its proper | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PerfectNumberQ.md">PerfectNumberQ</a>
   */
  public static final IBuiltInSymbol PerfectNumberQ =
      F.initFinalSymbol("PerfectNumberQ", ID.PerfectNumberQ);

  /**
   * PermutationCycles(permutation-list) - generate a `Cycles({{...},{...}, ...})` expression from
   * the `permutation-list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationCycles.md">PermutationCycles</a>
   */
  public static final IBuiltInSymbol PermutationCycles =
      F.initFinalSymbol("PermutationCycles", ID.PermutationCycles);

  /**
   * PermutationCyclesQ(cycles-expression) - if `cycles-expression` is a valid `Cycles({{...},{...},
   * ...})` expression return `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationCyclesQ.md">PermutationCyclesQ</a>
   */
  public static final IBuiltInSymbol PermutationCyclesQ =
      F.initFinalSymbol("PermutationCyclesQ", ID.PermutationCyclesQ);

  /**
   * PermutationList(Cycles({{...},{...}, ...})) - get the permutation list representation from the
   * `Cycles({{...},{...}, ...})` expression. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationList.md">PermutationList</a>
   */
  public static final IBuiltInSymbol PermutationList =
      F.initFinalSymbol("PermutationList", ID.PermutationList);

  /**
   * PermutationListQ(permutation-list) - if `permutation-list` is a valid permutation list return
   * `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationListQ.md">PermutationListQ</a>
   */
  public static final IBuiltInSymbol PermutationListQ =
      F.initFinalSymbol("PermutationListQ", ID.PermutationListQ);

  /**
   * PermutationReplace(list-or-integer, Cycles({{...},{...}, ...})) - replace the arguments of the
   * first expression with the corresponding element from the `Cycles({{...},{...}, ...})`
   * expression. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationReplace.md">PermutationReplace</a>
   */
  public static final IBuiltInSymbol PermutationReplace =
      F.initFinalSymbol("PermutationReplace", ID.PermutationReplace);

  /**
   * Permutations(list) - gives all possible orderings of the items in `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Permutations.md">Permutations</a>
   */
  public static final IBuiltInSymbol Permutations =
      F.initFinalSymbol("Permutations", ID.Permutations);

  /**
   * Permute(list, Cycles({permutation-cycles})) - permutes the `list` from the cycles in
   * `permutation-cycles`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Permute.md">Permute</a>
   */
  public static final IBuiltInSymbol Permute = F.initFinalSymbol("Permute", ID.Permute);

  /**
   * PetersenGraph() - create a `PetersenGraph(5, 2)` graph. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PetersenGraph.md">PetersenGraph</a>
   */
  public static final IBuiltInSymbol PetersenGraph =
      F.initFinalSymbol("PetersenGraph", ID.PetersenGraph);

  /**
   * Pi - is the constant `Pi`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pi.md">Pi</a>
   */
  public static final IBuiltInSymbol Pi = F.initFinalSymbol("Pi", ID.Pi);

  /**
   * Pick(nested-list, nested-selection) - returns the elements of `nested-list` that have value
   * `True` in the corresponding position in `nested-selection`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pick.md">Pick</a>
   */
  public static final IBuiltInSymbol Pick = F.initFinalSymbol("Pick", ID.Pick);

  /**
   * PieChart(list-of-values) - plot a pie chart from a `list-of-values`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PieChart.md">PieChart</a>
   */
  public static final IBuiltInSymbol PieChart = F.initFinalSymbol("PieChart", ID.PieChart);

  /**
   * Piecewise({{expr1, cond1}, ...}) - represents a piecewise function. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Piecewise.md">Piecewise</a>
   */
  public static final IBuiltInSymbol Piecewise = F.initFinalSymbol("Piecewise", ID.Piecewise);

  /**
   * PiecewiseExpand(function) - expands piecewise expressions into a `Piecewise` function.
   * Currently only `Abs, Clip, If, Ramp, UnitStep` are converted to Piecewise expressions. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PiecewiseExpand.md">PiecewiseExpand</a>
   */
  public static final IBuiltInSymbol PiecewiseExpand =
      F.initFinalSymbol("PiecewiseExpand", ID.PiecewiseExpand);

  public static final IBuiltInSymbol Pink = F.initFinalSymbol("Pink", ID.Pink);

  public static final IBuiltInSymbol PlanarGraph = F.initFinalSymbol("PlanarGraph", ID.PlanarGraph);

  public static final IBuiltInSymbol PlanarGraphQ =
      F.initFinalSymbol("PlanarGraphQ", ID.PlanarGraphQ);

  /**
   * Plot(function, {x, xMin, xMax}, PlotRange->{yMin,yMax}) - generate a JavaScript control for the
   * expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plot.md">Plot</a>
   */
  public static final IBuiltInSymbol Plot = F.initFinalSymbol("Plot", ID.Plot);

  /**
   * Plot3D(function, {x, xMin, xMax}, {y,yMin,yMax}) - generate a JavaScript control for the
   * expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plot3D.md">Plot3D</a>
   */
  public static final IBuiltInSymbol Plot3D = F.initFinalSymbol("Plot3D", ID.Plot3D);

  public static final IBuiltInSymbol PlotRange = F.initFinalSymbol("PlotRange", ID.PlotRange);

  public static final IBuiltInSymbol PlotStyle = F.initFinalSymbol("PlotStyle", ID.PlotStyle);

  /**
   * Plus(a, b, ...) - represents the sum of the terms `a, b, ...`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus</a>
   */
  public static final IBuiltInSymbol Plus = F.initFinalSymbol("Plus", ID.Plus);

  /**
   * Pochhammer(a, n) - returns the pochhammer symbol for a rational number `a` and an integer
   * number `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pochhammer.md">Pochhammer</a>
   */
  public static final IBuiltInSymbol Pochhammer = F.initFinalSymbol("Pochhammer", ID.Pochhammer);

  public static final IBuiltInSymbol Point = F.initFinalSymbol("Point", ID.Point);

  /**
   * PoissonDistribution(m) - returns a Poisson distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PoissonDistribution.md">PoissonDistribution</a>
   */
  public static final IBuiltInSymbol PoissonDistribution =
      F.initFinalSymbol("PoissonDistribution", ID.PoissonDistribution);

  /**
   * PolarPlot(function, {t, tMin, tMax}) - generate a JavaScript control for the polar plot
   * expressions `function` in the `t` range `{t, tMin, tMax}`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolarPlot.md">PolarPlot</a>
   */
  public static final IBuiltInSymbol PolarPlot = F.initFinalSymbol("PolarPlot", ID.PolarPlot);

  /**
   * PolyGamma(value) - return the digamma function of the `value`. The digamma function is defined
   * as the logarithmic derivative of the gamma function. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolyGamma.md">PolyGamma</a>
   */
  public static final IBuiltInSymbol PolyGamma = F.initFinalSymbol("PolyGamma", ID.PolyGamma);

  public static final IBuiltInSymbol PolyLog = F.initFinalSymbol("PolyLog", ID.PolyLog);

  public static final IBuiltInSymbol Polygon = F.initFinalSymbol("Polygon", ID.Polygon);

  /**
   * PolynomialExtendedGCD(p, q, x) - returns the extended GCD ('greatest common divisor') of the
   * univariate polynomials `p` and `q`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialExtendedGCD.md">PolynomialExtendedGCD</a>
   */
  public static final IBuiltInSymbol PolynomialExtendedGCD =
      F.initFinalSymbol("PolynomialExtendedGCD", ID.PolynomialExtendedGCD);

  /**
   * PolynomialGCD(p, q) - returns the GCD ('greatest common divisor') of the polynomials `p` and
   * `q`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialGCD.md">PolynomialGCD</a>
   */
  public static final IBuiltInSymbol PolynomialGCD =
      F.initFinalSymbol("PolynomialGCD", ID.PolynomialGCD);

  /**
   * PolynomialLCM(p, q) - returns the LCM ('least common multiple') of the polynomials `p` and `q`.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialLCM.md">PolynomialLCM</a>
   */
  public static final IBuiltInSymbol PolynomialLCM =
      F.initFinalSymbol("PolynomialLCM", ID.PolynomialLCM);

  /**
   * PolynomialQ(p, x) - return `True` if `p` is a polynomial for the variable `x`. Return `False`
   * in all other cases. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQ.md">PolynomialQ</a>
   */
  public static final IBuiltInSymbol PolynomialQ = F.initFinalSymbol("PolynomialQ", ID.PolynomialQ);

  /**
   * PolynomialQuotient(p, q, x) - returns the polynomial quotient of the polynomials `p` and `q`
   * for the variable `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQuotient.md">PolynomialQuotient</a>
   */
  public static final IBuiltInSymbol PolynomialQuotient =
      F.initFinalSymbol("PolynomialQuotient", ID.PolynomialQuotient);

  /**
   * PolynomialQuotientRemainder(p, q, x) - returns a list with the polynomial quotient and
   * remainder of the polynomials `p` and `q` for the variable `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQuotientRemainder.md">PolynomialQuotientRemainder</a>
   */
  public static final IBuiltInSymbol PolynomialQuotientRemainder =
      F.initFinalSymbol("PolynomialQuotientRemainder", ID.PolynomialQuotientRemainder);

  /**
   * PolynomialQuotient(p, q, x) - returns the polynomial remainder of the polynomials `p` and `q`
   * for the variable `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialRemainder.md">PolynomialRemainder</a>
   */
  public static final IBuiltInSymbol PolynomialRemainder =
      F.initFinalSymbol("PolynomialRemainder", ID.PolynomialRemainder);

  /**
   * Position(expr, patt) - returns the list of positions for which `expr` matches `patt`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Position.md">Position</a>
   */
  public static final IBuiltInSymbol Position = F.initFinalSymbol("Position", ID.Position);

  /**
   * Positive(x) - returns `True` if `x` is a positive real number. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Positive.md">Positive</a>
   */
  public static final IBuiltInSymbol Positive = F.initFinalSymbol("Positive", ID.Positive);

  /**
   * PossibleZeroQ(expr) - returns `True` if basic symbolic and numerical methods suggest that
   * `expr` has value zero, and `False` otherwise. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PossibleZeroQ.md">PossibleZeroQ</a>
   */
  public static final IBuiltInSymbol PossibleZeroQ =
      F.initFinalSymbol("PossibleZeroQ", ID.PossibleZeroQ);

  public static final IBuiltInSymbol Postefix = F.initFinalSymbol("Postefix", ID.Postefix);

  /**
   * Power(a, b) - represents `a` raised to the power of `b`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Power.md">Power</a>
   */
  public static final IBuiltInSymbol Power = F.initFinalSymbol("Power", ID.Power);

  /**
   * PowerExpand(expr) - expands out powers of the form `(x^y)^z` and `(x*y)^z` in `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PowerExpand.md">PowerExpand</a>
   */
  public static final IBuiltInSymbol PowerExpand = F.initFinalSymbol("PowerExpand", ID.PowerExpand);

  /**
   * PowerMod(x, y, m) - computes `x^y` modulo `m`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PowerMod.md">PowerMod</a>
   */
  public static final IBuiltInSymbol PowerMod = F.initFinalSymbol("PowerMod", ID.PowerMod);

  /**
   * PreDecrement(x) - decrements `x` by `1`, returning the new value of `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PreDecrement.md">PreDecrement</a>
   */
  public static final IBuiltInSymbol PreDecrement =
      F.initFinalSymbol("PreDecrement", ID.PreDecrement);

  /**
   * PreIncrement(x) - increments `x` by `1`, returning the new value of `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PreIncrement.md">PreIncrement</a>
   */
  public static final IBuiltInSymbol PreIncrement =
      F.initFinalSymbol("PreIncrement", ID.PreIncrement);

  public static final IBuiltInSymbol Precision = F.initFinalSymbol("Precision", ID.Precision);

  public static final IBuiltInSymbol PrecisionGoal =
      F.initFinalSymbol("PrecisionGoal", ID.PrecisionGoal);

  public static final IBuiltInSymbol Prefix = F.initFinalSymbol("Prefix", ID.Prefix);

  /**
   * Prepend(expr, item) - returns `expr` with `item` prepended to its leaves. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Prepend.md">Prepend</a>
   */
  public static final IBuiltInSymbol Prepend = F.initFinalSymbol("Prepend", ID.Prepend);

  /**
   * PrependTo(s, item) - prepend `item` to value of `s` and sets `s` to the result. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrependTo.md">PrependTo</a>
   */
  public static final IBuiltInSymbol PrependTo = F.initFinalSymbol("PrependTo", ID.PrependTo);

  /**
   * Prime(n) - returns the `n`th prime number. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Prime.md">Prime</a>
   */
  public static final IBuiltInSymbol Prime = F.initFinalSymbol("Prime", ID.Prime);

  /**
   * PrimeOmega(n) - returns the sum of the exponents of the prime factorization of `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimeOmega.md">PrimeOmega</a>
   */
  public static final IBuiltInSymbol PrimeOmega = F.initFinalSymbol("PrimeOmega", ID.PrimeOmega);

  /**
   * PrimePi(x) - gives the number of primes less than or equal to `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimePi.md">PrimePi</a>
   */
  public static final IBuiltInSymbol PrimePi = F.initFinalSymbol("PrimePi", ID.PrimePi);

  /**
   * PrimePowerQ(n) - returns `True` if `n` is a power of a prime number. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimePowerQ.md">PrimePowerQ</a>
   */
  public static final IBuiltInSymbol PrimePowerQ = F.initFinalSymbol("PrimePowerQ", ID.PrimePowerQ);

  /**
   * PrimeQ(n) - returns `True` if `n` is a integer prime number. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimeQ.md">PrimeQ</a>
   */
  public static final IBuiltInSymbol PrimeQ = F.initFinalSymbol("PrimeQ", ID.PrimeQ);

  public static final IBuiltInSymbol Primes = F.initFinalSymbol("Primes", ID.Primes);

  public static final IBuiltInSymbol PrimitiveRoot =
      F.initFinalSymbol("PrimitiveRoot", ID.PrimitiveRoot);

  /**
   * PrimitiveRootList(n) - returns the list of the primitive roots of `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimitiveRootList.md">PrimitiveRootList</a>
   */
  public static final IBuiltInSymbol PrimitiveRootList =
      F.initFinalSymbol("PrimitiveRootList", ID.PrimitiveRootList);

  /**
   * Print(expr) - print the `expr` to the default output stream and return `Null`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Print.md">Print</a>
   */
  public static final IBuiltInSymbol Print = F.initFinalSymbol("Print", ID.Print);

  /**
   * PrintableASCIIQ(str) - returns `True` if all characters in `str` are ASCII characters. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrintableASCIIQ.md">PrintableASCIIQ</a>
   */
  public static final IBuiltInSymbol PrintableASCIIQ =
      F.initFinalSymbol("PrintableASCIIQ", ID.PrintableASCIIQ);

  /**
   * Probability(pure-function, data-set) - returns the probability of the `pure-function` for the
   * given `data-set`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Probability.md">Probability</a>
   */
  public static final IBuiltInSymbol Probability = F.initFinalSymbol("Probability", ID.Probability);

  /**
   * Product(expr, {i, imin, imax}) - evaluates the discrete product of `expr` with `i` ranging from
   * `imin` to `imax`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Product.md">Product</a>
   */
  public static final IBuiltInSymbol Product = F.initFinalSymbol("Product", ID.Product);

  /**
   * ProductLog(z) - returns the value of the Lambert W function at `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ProductLog.md">ProductLog</a>
   */
  public static final IBuiltInSymbol ProductLog = F.initFinalSymbol("ProductLog", ID.ProductLog);

  /**
   * Projection(vector1, vector2) - Find the orthogonal projection of `vector1` onto another
   * `vector2`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Projection.md">Projection</a>
   */
  public static final IBuiltInSymbol Projection = F.initFinalSymbol("Projection", ID.Projection);

  public static final IBuiltInSymbol Protect = F.initFinalSymbol("Protect", ID.Protect);

  public static final IBuiltInSymbol Protected = F.initFinalSymbol("Protected", ID.Protected);

  /**
   * PseudoInverse(matrix) - computes the Moore-Penrose pseudoinverse of the `matrix`. If `matrix`
   * is invertible, the pseudoinverse equals the inverse. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PseudoInverse.md">PseudoInverse</a>
   */
  public static final IBuiltInSymbol PseudoInverse =
      F.initFinalSymbol("PseudoInverse", ID.PseudoInverse);

  public static final IBuiltInSymbol Purple = F.initFinalSymbol("Purple", ID.Purple);

  public static final IBuiltInSymbol Put = F.initFinalSymbol("Put", ID.Put);

  /**
   * QRDecomposition(A) - computes the QR decomposition of the matrix `A`. The QR decomposition is a
   * decomposition of a matrix `A` into a product `A = Q.R` of an unitary matrix `Q` and an upper
   * triangular matrix `R`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QRDecomposition.md">QRDecomposition</a>
   */
  public static final IBuiltInSymbol QRDecomposition =
      F.initFinalSymbol("QRDecomposition", ID.QRDecomposition);

  /**
   * QuadraticIrrationalQ(expr) - returns `True`, if the `expr` is of the form `(p + s * Sqrt(d)) /
   * q` for integers `p,q,d,s`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuadraticIrrationalQ.md">QuadraticIrrationalQ</a>
   */
  public static final IBuiltInSymbol QuadraticIrrationalQ =
      F.initFinalSymbol("QuadraticIrrationalQ", ID.QuadraticIrrationalQ);

  /**
   * Quantile(list, q) - returns the `q`-Quantile of `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantile.md">Quantile</a>
   */
  public static final IBuiltInSymbol Quantile = F.initFinalSymbol("Quantile", ID.Quantile);

  /**
   * Quantity(value, unit) - returns the quantity for `value` and `unit` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantity.md">Quantity</a>
   */
  public static final IBuiltInSymbol Quantity = F.initFinalSymbol("Quantity", ID.Quantity);

  public static final IBuiltInSymbol QuantityDistribution =
      F.initFinalSymbol("QuantityDistribution", ID.QuantityDistribution);

  /**
   * QuantityMagnitude(quantity) - returns the value of the `quantity` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityMagnitude.md">QuantityMagnitude</a>
   */
  public static final IBuiltInSymbol QuantityMagnitude =
      F.initFinalSymbol("QuantityMagnitude", ID.QuantityMagnitude);

  public static final IBuiltInSymbol QuantityQ = F.initFinalSymbol("QuantityQ", ID.QuantityQ);

  /**
   * Quartiles(arg) - returns a list of the `1/4`, `1/2` and `3/4` quantile of `arg`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quartiles.md">Quartiles</a>
   */
  public static final IBuiltInSymbol Quartiles = F.initFinalSymbol("Quartiles", ID.Quartiles);

  /**
   * Quiet(expr) - evaluates `expr` in "quiet" mode (i.e. no warning messages are shown during
   * evaluation). | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quiet.md">Quiet</a>
   */
  public static final IBuiltInSymbol Quiet = F.initFinalSymbol("Quiet", ID.Quiet);

  public static final IBuiltInSymbol Quit = F.initFinalSymbol("Quit", ID.Quit);

  /**
   * Quotient(m, n) - computes the integer quotient of `m` and `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quotient.md">Quotient</a>
   */
  public static final IBuiltInSymbol Quotient = F.initFinalSymbol("Quotient", ID.Quotient);

  /**
   * QuotientRemainder(m, n) - computes a list of the quotient and remainder from division of `m`
   * and `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuotientRemainder.md">QuotientRemainder</a>
   */
  public static final IBuiltInSymbol QuotientRemainder =
      F.initFinalSymbol("QuotientRemainder", ID.QuotientRemainder);

  public static final IBuiltInSymbol RGBColor = F.initFinalSymbol("RGBColor", ID.RGBColor);

  public static final IBuiltInSymbol RSolve = F.initFinalSymbol("RSolve", ID.RSolve);

  public static final IBuiltInSymbol RSolveValue = F.initFinalSymbol("RSolveValue", ID.RSolveValue);

  /**
   * Ramp(z) - The `Ramp` function is a unary real function, whose graph is shaped like a ramp. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ramp.md">Ramp</a>
   */
  public static final IBuiltInSymbol Ramp = F.initFinalSymbol("Ramp", ID.Ramp);

  /**
   * RandomChoice({item1, item2, item3,...}) - randomly picks one `item` from items. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomChoice.md">RandomChoice</a>
   */
  public static final IBuiltInSymbol RandomChoice =
      F.initFinalSymbol("RandomChoice", ID.RandomChoice);

  /**
   * RandomComplex[{z_min, z_max}] - yields a pseudo-random complex number in the rectangle with
   * complex corners `z_min` and `z_max`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomComplex.md">RandomComplex</a>
   */
  public static final IBuiltInSymbol RandomComplex =
      F.initFinalSymbol("RandomComplex", ID.RandomComplex);

  /**
   * RandomInteger(n) - create a random integer number between `0` and `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomInteger.md">RandomInteger</a>
   */
  public static final IBuiltInSymbol RandomInteger =
      F.initFinalSymbol("RandomInteger", ID.RandomInteger);

  /**
   * RandomPrime({imin, imax}) - create a random prime integer number between `imin` and `imax`
   * inclusive. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomPrime.md">RandomPrime</a>
   */
  public static final IBuiltInSymbol RandomPrime = F.initFinalSymbol("RandomPrime", ID.RandomPrime);

  /**
   * RandomReal() - create a random number between `0.0` and `1.0`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomReal.md">RandomReal</a>
   */
  public static final IBuiltInSymbol RandomReal = F.initFinalSymbol("RandomReal", ID.RandomReal);

  /**
   * RandomSample(items) - create a random sample for the arguments of the `items`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomSample.md">RandomSample</a>
   */
  public static final IBuiltInSymbol RandomSample =
      F.initFinalSymbol("RandomSample", ID.RandomSample);

  public static final IBuiltInSymbol RandomVariate =
      F.initFinalSymbol("RandomVariate", ID.RandomVariate);

  /**
   * Range(n) - returns a list of integers from `1` to `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Range.md">Range</a>
   */
  public static final IBuiltInSymbol Range = F.initFinalSymbol("Range", ID.Range);

  /**
   * Rational - is the head of rational numbers. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rational.md">Rational</a>
   */
  public static final IBuiltInSymbol Rational = F.initFinalSymbol("Rational", ID.Rational);

  /**
   * Rationalize(expression) - convert numerical real or imaginary parts in (sub-)expressions into
   * rational numbers. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rationalize.md">Rationalize</a>
   */
  public static final IBuiltInSymbol Rationalize = F.initFinalSymbol("Rationalize", ID.Rationalize);

  public static final IBuiltInSymbol Rationals = F.initFinalSymbol("Rationals", ID.Rationals);

  /**
   * Re(z) - returns the real component of the complex number `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Re.md">Re</a>
   */
  public static final IBuiltInSymbol Re = F.initFinalSymbol("Re", ID.Re);

  public static final IBuiltInSymbol ReadProtected =
      F.initFinalSymbol("ReadProtected", ID.ReadProtected);

  public static final IBuiltInSymbol ReadString = F.initFinalSymbol("ReadString", ID.ReadString);

  /**
   * Real - is the head of real (floating point) numbers. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Real.md">Real</a>
   */
  public static final IBuiltInSymbol Real = F.initFinalSymbol("Real", ID.Real);

  public static final IBuiltInSymbol RealDigits = F.initFinalSymbol("RealDigits", ID.RealDigits);

  /**
   * RealNumberQ(expr) - returns `True` if `expr` is an explicit number with no imaginary component.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RealNumberQ.md">RealNumberQ</a>
   */
  public static final IBuiltInSymbol RealNumberQ = F.initFinalSymbol("RealNumberQ", ID.RealNumberQ);

  /**
   * Reals - is the set of real numbers. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reals.md">Reals</a>
   */
  public static final IBuiltInSymbol Reals = F.initFinalSymbol("Reals", ID.Reals);

  /**
   * Reap(expr) - gives the result of evaluating `expr`, together with all values sown during this
   * evaluation. Values sown with different tags are given in different lists. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reap.md">Reap</a>
   */
  public static final IBuiltInSymbol Reap = F.initFinalSymbol("Reap", ID.Reap);

  public static final IBuiltInSymbol Rectangle = F.initFinalSymbol("Rectangle", ID.Rectangle);

  public static final IBuiltInSymbol Red = F.initFinalSymbol("Red", ID.Red);

  public static final IBuiltInSymbol Reduce = F.initFinalSymbol("Reduce", ID.Reduce);

  /**
   * Refine(expression, assumptions) - evaluate the `expression` for the given `assumptions`. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Refine.md">Refine</a>
   */
  public static final IBuiltInSymbol Refine = F.initFinalSymbol("Refine", ID.Refine);

  /**
   * RegularExpression("regex") - represents the regular expression specified by the string
   * `“regex”`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegularExpression.md">RegularExpression</a>
   */
  public static final IBuiltInSymbol RegularExpression =
      F.initFinalSymbol("RegularExpression", ID.RegularExpression);

  /**
   * ReleaseHold(expr) - removes any `Hold`, `HoldForm`, `HoldPattern` or `HoldComplete` head from
   * `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReleaseHold.md">ReleaseHold</a>
   */
  public static final IBuiltInSymbol ReleaseHold = F.initFinalSymbol("ReleaseHold", ID.ReleaseHold);

  public static final IBuiltInSymbol Remove = F.initFinalSymbol("Remove", ID.Remove);

  /**
   * RemoveDiacritics("string") - returns a version of `string` with all diacritics removed. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RemoveDiacritics.md">RemoveDiacritics</a>
   */
  public static final IBuiltInSymbol RemoveDiacritics =
      F.initFinalSymbol("RemoveDiacritics", ID.RemoveDiacritics);

  public static final IBuiltInSymbol Repeated = F.initFinalSymbol("Repeated", ID.Repeated);

  public static final IBuiltInSymbol RepeatedNull =
      F.initFinalSymbol("RepeatedNull", ID.RepeatedNull);

  /**
   * Replace(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr` with
   * the right-hand-side `rhs`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Replace.md">Replace</a>
   */
  public static final IBuiltInSymbol Replace = F.initFinalSymbol("Replace", ID.Replace);

  /**
   * ReplaceAll(expr, i -> new) - replaces all `i` in `expr` with `new`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceAll.md">ReplaceAll</a>
   */
  public static final IBuiltInSymbol ReplaceAll = F.initFinalSymbol("ReplaceAll", ID.ReplaceAll);

  /**
   * ReplaceList(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr`
   * with the right-hand-side `rhs`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceList.md">ReplaceList</a>
   */
  public static final IBuiltInSymbol ReplaceList = F.initFinalSymbol("ReplaceList", ID.ReplaceList);

  /**
   * ReplacePart(expr, i -> new) - replaces part `i` in `expr` with `new`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplacePart.md">ReplacePart</a>
   */
  public static final IBuiltInSymbol ReplacePart = F.initFinalSymbol("ReplacePart", ID.ReplacePart);

  /**
   * ReplaceRepeated(expr, lhs -> rhs) - repeatedly applies the rule `lhs -> rhs` to `expr` until
   * the result no longer changes. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceRepeated.md">ReplaceRepeated</a>
   */
  public static final IBuiltInSymbol ReplaceRepeated =
      F.initFinalSymbol("ReplaceRepeated", ID.ReplaceRepeated);

  /**
   * Rescale(list) - returns `Rescale(list,{Min(list), Max(list)})`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rescale.md">Rescale</a>
   */
  public static final IBuiltInSymbol Rescale = F.initFinalSymbol("Rescale", ID.Rescale);

  /**
   * Rest(expr) - returns `expr` with the first element removed. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rest.md">Rest</a>
   */
  public static final IBuiltInSymbol Rest = F.initFinalSymbol("Rest", ID.Rest);

  /**
   * Resultant(polynomial1, polynomial2, var) - computes the resultant of the polynomials
   * `polynomial1` and `polynomial2` with respect to the variable `var`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Resultant.md">Resultant</a>
   */
  public static final IBuiltInSymbol Resultant = F.initFinalSymbol("Resultant", ID.Resultant);

  /**
   * Return(expr) - aborts a function call and returns `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Return.md">Return</a>
   */
  public static final IBuiltInSymbol Return = F.initFinalSymbol("Return", ID.Return);

  /**
   * Reverse(list) - reverse the elements of the `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reverse.md">Reverse</a>
   */
  public static final IBuiltInSymbol Reverse = F.initFinalSymbol("Reverse", ID.Reverse);

  /**
   * RiccatiSolve({A,B},{Q,R}) - An algebraic Riccati equation is a type of nonlinear equation that
   * arises in the context of infinite-horizon optimal control problems in continuous time or
   * discrete time. The continuous time algebraic Riccati equation (CARE):
   * `A^{T}·X+X·A-X·B·R^{-1}·B^{T}·X+Q==0`. And the respective linear controller is: `K =
   * R^{-1}·B^{T}·P`. The solver receives `A`, `B`, `Q` and `R` and computes `P`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RiccatiSolve.md">RiccatiSolve</a>
   */
  public static final IBuiltInSymbol RiccatiSolve =
      F.initFinalSymbol("RiccatiSolve", ID.RiccatiSolve);

  /**
   * Riffle(list1, list2) - insert elements of `list2` between the elements of `list1`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Riffle.md">Riffle</a>
   */
  public static final IBuiltInSymbol Riffle = F.initFinalSymbol("Riffle", ID.Riffle);

  public static final IBuiltInSymbol Right = F.initFinalSymbol("Right", ID.Right);

  /**
   * RogersTanimotoDissimilarity(u, v) - returns the Rogers-Tanimoto dissimilarity between the two
   * boolean 1-D lists `u` and `v`, which is defined as `R / (c_tt + c_ff + R)` where n is `len(u)`,
   * `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * (c_tf +
   * c_ft)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RogersTanimotoDissimilarity.md">RogersTanimotoDissimilarity</a>
   */
  public static final IBuiltInSymbol RogersTanimotoDissimilarity =
      F.initFinalSymbol("RogersTanimotoDissimilarity", ID.RogersTanimotoDissimilarity);

  /**
   * RomanNumeral(positive-int-value) - converts the given `positive-int-value` to a roman numeral
   * string. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RomanNumeral.md">RomanNumeral</a>
   */
  public static final IBuiltInSymbol RomanNumeral =
      F.initFinalSymbol("RomanNumeral", ID.RomanNumeral);

  public static final IBuiltInSymbol Root = F.initFinalSymbol("Root", ID.Root);

  public static final IBuiltInSymbol RootIntervals =
      F.initFinalSymbol("RootIntervals", ID.RootIntervals);

  public static final IBuiltInSymbol RootOf = F.initFinalSymbol("RootOf", ID.RootOf);

  /**
   * Roots(polynomial-equation, var) - determine the roots of a univariate polynomial equation with
   * respect to the variable `var`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Roots.md">Roots</a>
   */
  public static final IBuiltInSymbol Roots = F.initFinalSymbol("Roots", ID.Roots);

  /**
   * RotateLeft(list) - rotates the items of `list` by one item to the left. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotateLeft.md">RotateLeft</a>
   */
  public static final IBuiltInSymbol RotateLeft = F.initFinalSymbol("RotateLeft", ID.RotateLeft);

  /**
   * RotateRight(list) - rotates the items of `list` by one item to the right. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotateRight.md">RotateRight</a>
   */
  public static final IBuiltInSymbol RotateRight = F.initFinalSymbol("RotateRight", ID.RotateRight);

  /**
   * RotationMatrix(theta) - yields a rotation matrix for the angle `theta`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotationMatrix.md">RotationMatrix</a>
   */
  public static final IBuiltInSymbol RotationMatrix =
      F.initFinalSymbol("RotationMatrix", ID.RotationMatrix);

  /**
   * Round(expr) - round a given `expr` to nearest integer. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Round.md">Round</a>
   */
  public static final IBuiltInSymbol Round = F.initFinalSymbol("Round", ID.Round);

  public static final IBuiltInSymbol Row = F.initFinalSymbol("Row", ID.Row);

  /**
   * RowReduce(matrix) - returns the reduced row-echelon form of `matrix`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RowReduce.md">RowReduce</a>
   */
  public static final IBuiltInSymbol RowReduce = F.initFinalSymbol("RowReduce", ID.RowReduce);

  /**
   * Rule(x, y) - represents a rule replacing `x` with `y`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rule.md">Rule</a>
   */
  public static final IBuiltInSymbol Rule = F.initFinalSymbol("Rule", ID.Rule);

  /**
   * RuleDelayed(x, y) - represents a rule replacing `x` with `y`, with `y` held unevaluated. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RuleDelayed.md">RuleDelayed</a>
   */
  public static final IBuiltInSymbol RuleDelayed = F.initFinalSymbol("RuleDelayed", ID.RuleDelayed);

  /**
   * RussellRaoDissimilarity(u, v) - returns the Russell-Rao dissimilarity between the two boolean
   * 1-D lists `u` and `v`, which is defined as `(n - c_tt) / c_tt` where `n` is `len(u)` and `c_ij`
   * is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RussellRaoDissimilarity.md">RussellRaoDissimilarity</a>
   */
  public static final IBuiltInSymbol RussellRaoDissimilarity =
      F.initFinalSymbol("RussellRaoDissimilarity", ID.RussellRaoDissimilarity);

  /**
   * SameQ(x, y) - returns `True` if `x` and `y` are structurally identical. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SameQ.md">SameQ</a>
   */
  public static final IBuiltInSymbol SameQ = F.initFinalSymbol("SameQ", ID.SameQ);

  public static final IBuiltInSymbol SameTest = F.initFinalSymbol("SameTest", ID.SameTest);

  /**
   * SatisfiabilityCount(boolean-expr) - test whether the `boolean-expr` is satisfiable by a
   * combination of boolean `False` and `True` values for the variables of the boolean expression
   * and return the number of possible combinations. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiabilityCount.md">SatisfiabilityCount</a>
   */
  public static final IBuiltInSymbol SatisfiabilityCount =
      F.initFinalSymbol("SatisfiabilityCount", ID.SatisfiabilityCount);

  /**
   * SatisfiabilityInstances(boolean-expr, list-of-variables) - test whether the `boolean-expr` is
   * satisfiable by a combination of boolean `False` and `True` values for the `list-of-variables`
   * and return exactly one instance of `True, False` combinations if possible. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiabilityInstances.md">SatisfiabilityInstances</a>
   */
  public static final IBuiltInSymbol SatisfiabilityInstances =
      F.initFinalSymbol("SatisfiabilityInstances", ID.SatisfiabilityInstances);

  /**
   * SatisfiableQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable
   * by a combination of boolean `False` and `True` values for the `list-of-variables`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiableQ.md">SatisfiableQ</a>
   */
  public static final IBuiltInSymbol SatisfiableQ =
      F.initFinalSymbol("SatisfiableQ", ID.SatisfiableQ);

  public static final IBuiltInSymbol Scaled = F.initFinalSymbol("Scaled", ID.Scaled);

  /**
   * Scan(f, expr) - applies `f` to each element of `expr` and returns 'Null'. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Scan.md">Scan</a>
   */
  public static final IBuiltInSymbol Scan = F.initFinalSymbol("Scan", ID.Scan);

  /**
   * Sec(z) - returns the secant of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sec.md">Sec</a>
   */
  public static final IBuiltInSymbol Sec = F.initFinalSymbol("Sec", ID.Sec);

  /**
   * Sech(z) - returns the hyperbolic secant of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sech.md">Sech</a>
   */
  public static final IBuiltInSymbol Sech = F.initFinalSymbol("Sech", ID.Sech);

  public static final IBuiltInSymbol Second = F.initFinalSymbol("Second", ID.Second);

  /**
   * Select({e1, e2, ...}, head) - returns a list of the elements `ei` for which `head(ei)` returns
   * `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Select.md">Select</a>
   */
  public static final IBuiltInSymbol Select = F.initFinalSymbol("Select", ID.Select);

  /**
   * SelectFirst({e1, e2, ...}, f) - returns the first of the elements `ei` for which `f(ei)`
   * returns `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SelectFirst.md">SelectFirst</a>
   */
  public static final IBuiltInSymbol SelectFirst = F.initFinalSymbol("SelectFirst", ID.SelectFirst);

  /**
   * SemanticImport("path-to-filename") - if the file system is enabled, import the data from CSV
   * files and do a semantic interpretation of the columns. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SemanticImport.md">SemanticImport</a>
   */
  public static final IBuiltInSymbol SemanticImport =
      F.initFinalSymbol("SemanticImport", ID.SemanticImport);

  /**
   * SemanticImportString("string-content") - import the data from a content string in CSV format
   * and do a semantic interpretation of the columns. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SemanticImportString.md">SemanticImportString</a>
   */
  public static final IBuiltInSymbol SemanticImportString =
      F.initFinalSymbol("SemanticImportString", ID.SemanticImportString);

  /**
   * Sequence[x1, x2, ...] - represents a sequence of arguments to a function. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sequence.md">Sequence</a>
   */
  public static final IBuiltInSymbol Sequence = F.initFinalSymbol("Sequence", ID.Sequence);

  public static final IBuiltInSymbol SequenceHold =
      F.initFinalSymbol("SequenceHold", ID.SequenceHold);

  /**
   * Series(expr, {x, x0, n}) - create a power series of `expr` up to order `(x- x0)^n` at the point
   * `x = x0` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Series.md">Series</a>
   */
  public static final IBuiltInSymbol Series = F.initFinalSymbol("Series", ID.Series);

  /**
   * SeriesCoefficient(expr, {x, x0, n}) - get the coefficient of `(x- x0)^n` at the point `x = x0`
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SeriesCoefficient.md">SeriesCoefficient</a>
   */
  public static final IBuiltInSymbol SeriesCoefficient =
      F.initFinalSymbol("SeriesCoefficient", ID.SeriesCoefficient);

  /**
   * SeriesData(x, x0, {coeff0, coeff1, coeff2,...}, nMin, nMax, denominator}) - internal structure
   * of a power series at the point `x = x0` the `coeff_i` are coefficients of the power series. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SeriesData.md">SeriesData</a>
   */
  public static final IBuiltInSymbol SeriesData = F.initFinalSymbol("SeriesData", ID.SeriesData);

  /**
   * Set(expr, value) - evaluates `value` and assigns it to `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Set.md">Set</a>
   */
  public static final IBuiltInSymbol Set = F.initFinalSymbol("Set", ID.Set);

  /**
   * SetAttributes(symbol, attrib) - adds `attrib` to `symbol`'s attributes. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SetAttributes.md">SetAttributes</a>
   */
  public static final IBuiltInSymbol SetAttributes =
      F.initFinalSymbol("SetAttributes", ID.SetAttributes);

  /**
   * SetDelayed(expr, value) - assigns `value` to `expr`, without evaluating `value`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SetDelayed.md">SetDelayed</a>
   */
  public static final IBuiltInSymbol SetDelayed = F.initFinalSymbol("SetDelayed", ID.SetDelayed);

  /**
   * Share(function) - replace internally equal common subexpressions in `function` by the same
   * reference to reduce memory consumption and return the number of times where `Share(function)`
   * could replace a common subexpression. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Share.md">Share</a>
   */
  public static final IBuiltInSymbol Share = F.initFinalSymbol("Share", ID.Share);

  public static final IBuiltInSymbol Short = F.initFinalSymbol("Short", ID.Short);

  public static final IBuiltInSymbol Shortest = F.initFinalSymbol("Shortest", ID.Shortest);

  public static final IBuiltInSymbol Show = F.initFinalSymbol("Show", ID.Show);

  /**
   * Sign(x) - gives `-1`, `0` or `1` depending on whether `x` is negative, zero or positive. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sign.md">Sign</a>
   */
  public static final IBuiltInSymbol Sign = F.initFinalSymbol("Sign", ID.Sign);

  public static final IBuiltInSymbol SignCmp = F.initFinalSymbol("SignCmp", ID.SignCmp);

  /**
   * Signature(permutation-list) - determine if the `permutation-list` has odd (`-1`) or even (`1`)
   * parity. Returns `0` if two elements in the `permutation-list` are equal. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Signature.md">Signature</a>
   */
  public static final IBuiltInSymbol Signature = F.initFinalSymbol("Signature", ID.Signature);

  /**
   * Simplify(expr) - simplifies `expr` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Simplify.md">Simplify</a>
   */
  public static final IBuiltInSymbol Simplify = F.initFinalSymbol("Simplify", ID.Simplify);

  /**
   * Sin(expr) - returns the sine of `expr` (measured in radians). | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sin.md">Sin</a>
   */
  public static final IBuiltInSymbol Sin = F.initFinalSymbol("Sin", ID.Sin);

  /**
   * SinIntegral(expr) - returns the hyperbolic sine integral of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SinIntegral.md">SinIntegral</a>
   */
  public static final IBuiltInSymbol SinIntegral = F.initFinalSymbol("SinIntegral", ID.SinIntegral);

  /**
   * Sinc(expr) - the sinc function `Sin(expr)/expr` for `expr != 0`. `Sinc(0)` returns `1`. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sinc.md">Sinc</a>
   */
  public static final IBuiltInSymbol Sinc = F.initFinalSymbol("Sinc", ID.Sinc);

  /**
   * SingularValueDecomposition(matrix) - calculates the singular value decomposition for the
   * `matrix`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SingularValueDecomposition.md">SingularValueDecomposition</a>
   */
  public static final IBuiltInSymbol SingularValueDecomposition =
      F.initFinalSymbol("SingularValueDecomposition", ID.SingularValueDecomposition);

  /**
   * Sinh(z) - returns the hyperbolic sine of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sinh.md">Sinh</a>
   */
  public static final IBuiltInSymbol Sinh = F.initFinalSymbol("Sinh", ID.Sinh);

  /**
   * SinhIntegral(expr) - returns the sine integral of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SinhIntegral.md">SinhIntegral</a>
   */
  public static final IBuiltInSymbol SinhIntegral =
      F.initFinalSymbol("SinhIntegral", ID.SinhIntegral);

  /**
   * Skewness(list) - gives Pearson's moment coefficient of skewness for `list` (a measure for
   * estimating the symmetry of a distribution). | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Skewness.md">Skewness</a>
   */
  public static final IBuiltInSymbol Skewness = F.initFinalSymbol("Skewness", ID.Skewness);

  /**
   * # - is a short-hand for `#1`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Slot.md">Slot</a>
   */
  public static final IBuiltInSymbol Slot = F.initFinalSymbol("Slot", ID.Slot);

  /**
   * ## - is the sequence of arguments supplied to a pure function. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SlotSequence.md">SlotSequence</a>
   */
  public static final IBuiltInSymbol SlotSequence =
      F.initFinalSymbol("SlotSequence", ID.SlotSequence);

  /**
   * SokalSneathDissimilarity(u, v) - returns the Sokal-Sneath dissimilarity between the two boolean
   * 1-D lists `u` and `v`, which is defined as `R / (c_tt + R)` where n is `len(u)`, `c_ij` is the
   * number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * (c_tf + c_ft)`. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SokalSneathDissimilarity.md">SokalSneathDissimilarity</a>
   */
  public static final IBuiltInSymbol SokalSneathDissimilarity =
      F.initFinalSymbol("SokalSneathDissimilarity", ID.SokalSneathDissimilarity);

  /**
   * Solve(equations, vars) - attempts to solve `equations` for the variables `vars`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Solve.md">Solve</a>
   */
  public static final IBuiltInSymbol Solve = F.initFinalSymbol("Solve", ID.Solve);

  /**
   * Sort(list) - sorts `list` (or the leaves of any other expression) according to canonical
   * ordering. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sort.md">Sort</a>
   */
  public static final IBuiltInSymbol Sort = F.initFinalSymbol("Sort", ID.Sort);

  /**
   * SortBy(list, f) - sorts `list` (or the leaves of any other expression) according to canonical
   * ordering of the keys that are extracted from the `list`'s elements using `f`. Chunks of leaves
   * that appear the same under `f` are sorted according to their natural order (without applying
   * `f`). | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SortBy.md">SortBy</a>
   */
  public static final IBuiltInSymbol SortBy = F.initFinalSymbol("SortBy", ID.SortBy);

  /**
   * Sow(expr) - sends the value `expr` to the innermost `Reap`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sow.md">Sow</a>
   */
  public static final IBuiltInSymbol Sow = F.initFinalSymbol("Sow", ID.Sow);

  /**
   * Span - is the head of span ranges like `1;;3`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Span.md">Span</a>
   */
  public static final IBuiltInSymbol Span = F.initFinalSymbol("Span", ID.Span);

  /**
   * SparseArray(nested-list) - create a sparse array from a `nested-list` structure. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SparseArray.md">SparseArray</a>
   */
  public static final IBuiltInSymbol SparseArray = F.initFinalSymbol("SparseArray", ID.SparseArray);

  /**
   * SphericalBesselJ(n, z) - spherical Bessel function `J(n, x)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SphericalBesselJ.md">SphericalBesselJ</a>
   */
  public static final IBuiltInSymbol SphericalBesselJ =
      F.initFinalSymbol("SphericalBesselJ", ID.SphericalBesselJ);

  /**
   * SphericalBesselY(n, z) - spherical Bessel function `Y(n, x)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SphericalBesselY.md">SphericalBesselY</a>
   */
  public static final IBuiltInSymbol SphericalBesselY =
      F.initFinalSymbol("SphericalBesselY", ID.SphericalBesselY);

  public static final IBuiltInSymbol SphericalHankelH1 =
      F.initFinalSymbol("SphericalHankelH1", ID.SphericalHankelH1);

  public static final IBuiltInSymbol SphericalHankelH2 =
      F.initFinalSymbol("SphericalHankelH2", ID.SphericalHankelH2);

  /**
   * Split(list) - splits `list` into collections of consecutive identical elements. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Split.md">Split</a>
   */
  public static final IBuiltInSymbol Split = F.initFinalSymbol("Split", ID.Split);

  /**
   * SplitBy(list, f) - splits `list` into collections of consecutive elements that give the same
   * result when `f` is applied. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SplitBy.md">SplitBy</a>
   */
  public static final IBuiltInSymbol SplitBy = F.initFinalSymbol("SplitBy", ID.SplitBy);

  /**
   * Sqrt(expr) - returns the square root of `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sqrt.md">Sqrt</a>
   */
  public static final IBuiltInSymbol Sqrt = F.initFinalSymbol("Sqrt", ID.Sqrt);

  /**
   * SquareFreeQ(n) - returns `True` if `n` is a square free integer number or a square free
   * univariate polynomial. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquareFreeQ.md">SquareFreeQ</a>
   */
  public static final IBuiltInSymbol SquareFreeQ = F.initFinalSymbol("SquareFreeQ", ID.SquareFreeQ);

  /**
   * SquareMatrixQ(m) - returns `True` if `m` is a square matrix. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquareMatrixQ.md">SquareMatrixQ</a>
   */
  public static final IBuiltInSymbol SquareMatrixQ =
      F.initFinalSymbol("SquareMatrixQ", ID.SquareMatrixQ);

  /**
   * SquaredEuclideanDistance(u, v) - returns squared the euclidean distance between `u$` and `v`. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquaredEuclideanDistance.md">SquaredEuclideanDistance</a>
   */
  public static final IBuiltInSymbol SquaredEuclideanDistance =
      F.initFinalSymbol("SquaredEuclideanDistance", ID.SquaredEuclideanDistance);

  /**
   * Stack( ) - return a list of the heads of the current stack wrapped by `HoldForm`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Stack.md">Stack</a>
   */
  public static final IBuiltInSymbol Stack = F.initFinalSymbol("Stack", ID.Stack);

  /**
   * Stack(expr) - begine a new stack and evaluate `èxpr`. Use `Stack(_)` as a subexpression in
   * `expr` to return the stack elements. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StackBegin.md">StackBegin</a>
   */
  public static final IBuiltInSymbol StackBegin = F.initFinalSymbol("StackBegin", ID.StackBegin);

  /**
   * StandardDeviation(list) - computes the standard deviation of `list`. `list` may consist of
   * numerical values or symbols. Numerical values may be real or complex. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardDeviation.md">StandardDeviation</a>
   */
  public static final IBuiltInSymbol StandardDeviation =
      F.initFinalSymbol("StandardDeviation", ID.StandardDeviation);

  public static final IBuiltInSymbol StandardForm =
      F.initFinalSymbol("StandardForm", ID.StandardForm);

  public static final IBuiltInSymbol Standardize = F.initFinalSymbol("Standardize", ID.Standardize);

  /**
   * StarGraph(order) - create a new star graph with `order` number of total vertices including the
   * center vertex. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StarGraph.md">StarGraph</a>
   */
  public static final IBuiltInSymbol StarGraph = F.initFinalSymbol("StarGraph", ID.StarGraph);

  /**
   * StartOfLine - begine a new stack and evaluate `èxpr`. Use `Stack(_)` as a subexpression in
   * `expr` to return the stack elements. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StartOfLine.md">StartOfLine</a>
   */
  public static final IBuiltInSymbol StartOfLine = F.initFinalSymbol("StartOfLine", ID.StartOfLine);

  /**
   * StartOfString - represents the start of a string. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StartOfString.md">StartOfString</a>
   */
  public static final IBuiltInSymbol StartOfString =
      F.initFinalSymbol("StartOfString", ID.StartOfString);

  /**
   * StieltjesGamma(a) - returns Stieltjes constant. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StieltjesGamma.md">StieltjesGamma</a>
   */
  public static final IBuiltInSymbol StieltjesGamma =
      F.initFinalSymbol("StieltjesGamma", ID.StieltjesGamma);

  /**
   * StirlingS1(n, k) - returns the Stirling numbers of the first kind. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StirlingS1.md">StirlingS1</a>
   */
  public static final IBuiltInSymbol StirlingS1 = F.initFinalSymbol("StirlingS1", ID.StirlingS1);

  /**
   * StirlingS2(n, k) - returns the Stirling numbers of the second kind. `StirlingS2(n,k)` is the
   * number of ways of partitioning an `n`-element set into `k` non-empty subsets. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StirlingS2.md">StirlingS2</a>
   */
  public static final IBuiltInSymbol StirlingS2 = F.initFinalSymbol("StirlingS2", ID.StirlingS2);

  public static final IBuiltInSymbol Strict = F.initFinalSymbol("Strict", ID.Strict);

  /**
   * String - is the head of strings.. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/String.md">String</a>
   */
  public static final IBuiltInSymbol String = F.initFinalSymbol("String", ID.String);

  /**
   * StringCases(string, pattern) - gives all occurences of `pattern` in `string`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringCases.md">StringCases</a>
   */
  public static final IBuiltInSymbol StringCases = F.initFinalSymbol("StringCases", ID.StringCases);

  /**
   * StringContainsQ(str1, str2) - return a list of matches for `"p1", "p2",...` list of strings in
   * the string `str`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringContainsQ.md">StringContainsQ</a>
   */
  public static final IBuiltInSymbol StringContainsQ =
      F.initFinalSymbol("StringContainsQ", ID.StringContainsQ);

  /**
   * StringCount(string, pattern) - counts all occurences of `pattern` in `string`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringCount.md">StringCount</a>
   */
  public static final IBuiltInSymbol StringCount = F.initFinalSymbol("StringCount", ID.StringCount);

  public static final IBuiltInSymbol StringDrop = F.initFinalSymbol("StringDrop", ID.StringDrop);

  /**
   * StringExpression(s_1, s_2, ...) - represents a sequence of strings and symbolic string objects
   * `s_i`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringExpression.md">StringExpression</a>
   */
  public static final IBuiltInSymbol StringExpression =
      F.initFinalSymbol("StringExpression", ID.StringExpression);

  public static final IBuiltInSymbol StringFormat =
      F.initFinalSymbol("StringFormat", ID.StringFormat);

  /**
   * StringFreeQ("string", patt) - returns `True` if no substring in `string` matches the string
   * expression `patt`, and returns `False` otherwise. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringFreeQ.md">StringFreeQ</a>
   */
  public static final IBuiltInSymbol StringFreeQ = F.initFinalSymbol("StringFreeQ", ID.StringFreeQ);

  /**
   * StringInsert(string, new-string, position) - returns a string with `new-string` inserted
   * starting at `position` in `string`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringInsert.md">StringInsert</a>
   */
  public static final IBuiltInSymbol StringInsert =
      F.initFinalSymbol("StringInsert", ID.StringInsert);

  /**
   * StringJoin(str1, str2, ... strN) - returns the concatenation of the strings `str1, str2, ...
   * strN`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringJoin.md">StringJoin</a>
   */
  public static final IBuiltInSymbol StringJoin = F.initFinalSymbol("StringJoin", ID.StringJoin);

  /**
   * StringLength(string) - gives the length of `string`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringLength.md">StringLength</a>
   */
  public static final IBuiltInSymbol StringLength =
      F.initFinalSymbol("StringLength", ID.StringLength);

  /**
   * StringMatchQ(string, regex-pattern) - check if the regular expression `regex-pattern` matches
   * the `string`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringMatchQ.md">StringMatchQ</a>
   */
  public static final IBuiltInSymbol StringMatchQ =
      F.initFinalSymbol("StringMatchQ", ID.StringMatchQ);

  /**
   * StringPart(str, pos) - return the character at position `pos` from the `str` string expression.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringPart.md">StringPart</a>
   */
  public static final IBuiltInSymbol StringPart = F.initFinalSymbol("StringPart", ID.StringPart);

  /**
   * StringPosition("string", patt) - gives a list of starting and ending positions where `patt`
   * matches `"string"`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringPosition.md">StringPosition</a>
   */
  public static final IBuiltInSymbol StringPosition =
      F.initFinalSymbol("StringPosition", ID.StringPosition);

  /**
   * StringQ(x) - is `True` if `x` is a string object, or `False` otherwise. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringQ.md">StringQ</a>
   */
  public static final IBuiltInSymbol StringQ = F.initFinalSymbol("StringQ", ID.StringQ);

  /**
   * StringReplace(string, fromStr -> toStr) - replaces each occurrence of `fromStr` with `toStr` in
   * `string`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringReplace.md">StringReplace</a>
   */
  public static final IBuiltInSymbol StringReplace =
      F.initFinalSymbol("StringReplace", ID.StringReplace);

  /**
   * StringRiffle({s1, s2, s3, ...}) - returns a new string by concatenating all the `si`, with
   * spaces inserted between them. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringRiffle.md">StringRiffle</a>
   */
  public static final IBuiltInSymbol StringRiffle =
      F.initFinalSymbol("StringRiffle", ID.StringRiffle);

  /**
   * StringSplit(str) - split the string `str` by whitespaces into a list of strings. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringSplit.md">StringSplit</a>
   */
  public static final IBuiltInSymbol StringSplit = F.initFinalSymbol("StringSplit", ID.StringSplit);

  /**
   * StringTake("string", n) - gives the first `n` characters in `string`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringTake.md">StringTake</a>
   */
  public static final IBuiltInSymbol StringTake = F.initFinalSymbol("StringTake", ID.StringTake);

  /**
   * StringToByteArray(string) - encodes the `string` into a sequence of bytes using the default
   * character set `UTF-8`, storing the result into into a `ByteArray`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringToByteArray.md">StringToByteArray</a>
   */
  public static final IBuiltInSymbol StringToByteArray =
      F.initFinalSymbol("StringToByteArray", ID.StringToByteArray);

  /**
   * StringTrim(s) - returns a version of `s `with whitespace removed from start and end. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringTrim.md">StringTrim</a>
   */
  public static final IBuiltInSymbol StringTrim = F.initFinalSymbol("StringTrim", ID.StringTrim);

  public static final IBuiltInSymbol Structure = F.initFinalSymbol("Structure", ID.Structure);

  /**
   * StruveH(n, z) - returns the Struve function `H_n(z)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StruveH.md">StruveH</a>
   */
  public static final IBuiltInSymbol StruveH = F.initFinalSymbol("StruveH", ID.StruveH);

  /**
   * StruveL(n, z) - returns the modified Struve function `L_n(z)`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StruveL.md">StruveL</a>
   */
  public static final IBuiltInSymbol StruveL = F.initFinalSymbol("StruveL", ID.StruveL);

  /**
   * StudentTDistribution(v) - returns a Student's t-distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StudentTDistribution.md">StudentTDistribution</a>
   */
  public static final IBuiltInSymbol StudentTDistribution =
      F.initFinalSymbol("StudentTDistribution", ID.StudentTDistribution);

  public static final IBuiltInSymbol Style = F.initFinalSymbol("Style", ID.Style);

  public static final IBuiltInSymbol StyleForm = F.initFinalSymbol("StyleForm", ID.StyleForm);

  /**
   * Subdivide(n) - returns a list with `n+1` entries obtained by subdividing the range `0` to `1`.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subdivide.md">Subdivide</a>
   */
  public static final IBuiltInSymbol Subdivide = F.initFinalSymbol("Subdivide", ID.Subdivide);

  /**
   * Subfactorial(n) - returns the subfactorial number of the integer `n` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subfactorial.md">Subfactorial</a>
   */
  public static final IBuiltInSymbol Subfactorial =
      F.initFinalSymbol("Subfactorial", ID.Subfactorial);

  public static final IBuiltInSymbol Subscript = F.initFinalSymbol("Subscript", ID.Subscript);

  /**
   * SubsetQ(set1, set2) - returns `True` if `set2` is a subset of `set1`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubsetQ.md">SubsetQ</a>
   */
  public static final IBuiltInSymbol SubsetQ = F.initFinalSymbol("SubsetQ", ID.SubsetQ);

  /**
   * Subsets(list) - finds a list of all possible subsets of `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subsets.md">Subsets</a>
   */
  public static final IBuiltInSymbol Subsets = F.initFinalSymbol("Subsets", ID.Subsets);

  public static final IBuiltInSymbol Subsuperscript =
      F.initFinalSymbol("Subsuperscript", ID.Subsuperscript);

  /**
   * Subtract(a, b) - represents the subtraction of `b` from `a`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subtract.md">Subtract</a>
   */
  public static final IBuiltInSymbol Subtract = F.initFinalSymbol("Subtract", ID.Subtract);

  /**
   * SubtractFrom(x, dx) - is equivalent to `x = x - dx`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubtractFrom.md">SubtractFrom</a>
   */
  public static final IBuiltInSymbol SubtractFrom =
      F.initFinalSymbol("SubtractFrom", ID.SubtractFrom);

  /**
   * Sum(expr, {i, imin, imax}) - evaluates the discrete sum of `expr` with `i` ranging from `imin`
   * to `imax`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sum.md">Sum</a>
   */
  public static final IBuiltInSymbol Sum = F.initFinalSymbol("Sum", ID.Sum);

  public static final IBuiltInSymbol Summary = F.initFinalSymbol("Summary", ID.Summary);

  public static final IBuiltInSymbol Superscript = F.initFinalSymbol("Superscript", ID.Superscript);

  /**
   * Surd(expr, n) - returns the `n`-th root of `expr`. If the result is defined, it's a real value.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Surd.md">Surd</a>
   */
  public static final IBuiltInSymbol Surd = F.initFinalSymbol("Surd", ID.Surd);

  public static final IBuiltInSymbol SurfaceGraphics =
      F.initFinalSymbol("SurfaceGraphics", ID.SurfaceGraphics);

  /**
   * SurvivalFunction(dist, x) - returns the survival function for the distribution `dist` evaluated
   * at `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SurvivalFunction.md">SurvivalFunction</a>
   */
  public static final IBuiltInSymbol SurvivalFunction =
      F.initFinalSymbol("SurvivalFunction", ID.SurvivalFunction);

  /**
   * Switch(expr, pattern1, value1, pattern2, value2, ...) - yields the first `value` for which
   * `expr` matches the corresponding pattern. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Switch.md">Switch</a>
   */
  public static final IBuiltInSymbol Switch = F.initFinalSymbol("Switch", ID.Switch);

  /**
   * Symbol - is the head of symbols. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Symbol.md">Symbol</a>
   */
  public static final IBuiltInSymbol Symbol = F.initFinalSymbol("Symbol", ID.Symbol);

  /**
   * SymbolName(s) - returns the name of the symbol `s` (without any leading context name). | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymbolName.md">SymbolName</a>
   */
  public static final IBuiltInSymbol SymbolName = F.initFinalSymbol("SymbolName", ID.SymbolName);

  /**
   * SymbolQ(x) - is `True` if `x` is a symbol, or `False` otherwise. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymbolQ.md">SymbolQ</a>
   */
  public static final IBuiltInSymbol SymbolQ = F.initFinalSymbol("SymbolQ", ID.SymbolQ);

  public static final IBuiltInSymbol Symmetric = F.initFinalSymbol("Symmetric", ID.Symmetric);

  /**
   * SymmetricMatrixQ(m) - returns `True` if `m` is a symmetric matrix. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymmetricMatrixQ.md">SymmetricMatrixQ</a>
   */
  public static final IBuiltInSymbol SymmetricMatrixQ =
      F.initFinalSymbol("SymmetricMatrixQ", ID.SymmetricMatrixQ);

  public static final IBuiltInSymbol SyntaxLength =
      F.initFinalSymbol("SyntaxLength", ID.SyntaxLength);

  /**
   * SyntaxQ(str) - is `True` if the given `str` is a string which has the correct syntax. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SyntaxQ.md">SyntaxQ</a>
   */
  public static final IBuiltInSymbol SyntaxQ = F.initFinalSymbol("SyntaxQ", ID.SyntaxQ);

  /**
   * SystemDialogInput("FileOpen") - if the file system is enabled, open a file chooser dialog box.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SystemDialogInput.md">SystemDialogInput</a>
   */
  public static final IBuiltInSymbol SystemDialogInput =
      F.initFinalSymbol("SystemDialogInput", ID.SystemDialogInput);

  /**
   * Table(expr, {i, n}) - evaluates `expr` with `i` ranging from `1` to `n`, returning a list of
   * the results. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Table.md">Table</a>
   */
  public static final IBuiltInSymbol Table = F.initFinalSymbol("Table", ID.Table);

  public static final IBuiltInSymbol TableForm = F.initFinalSymbol("TableForm", ID.TableForm);

  public static final IBuiltInSymbol TableHeadings =
      F.initFinalSymbol("TableHeadings", ID.TableHeadings);

  public static final IBuiltInSymbol TagSet = F.initFinalSymbol("TagSet", ID.TagSet);

  public static final IBuiltInSymbol TagSetDelayed =
      F.initFinalSymbol("TagSetDelayed", ID.TagSetDelayed);

  /**
   * Take(expr, n) - returns `expr` with all but the first `n` leaves removed. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Take.md">Take</a>
   */
  public static final IBuiltInSymbol Take = F.initFinalSymbol("Take", ID.Take);

  public static final IBuiltInSymbol TakeLargest = F.initFinalSymbol("TakeLargest", ID.TakeLargest);

  public static final IBuiltInSymbol TakeLargestBy =
      F.initFinalSymbol("TakeLargestBy", ID.TakeLargestBy);

  /**
   * Tally(list) - return the elements and their number of occurrences in `list` in a new result
   * list. The `binary-predicate` tests if two elements are equivalent. `SameQ` is used as the
   * default `binary-predicate`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tally.md">Tally</a>
   */
  public static final IBuiltInSymbol Tally = F.initFinalSymbol("Tally", ID.Tally);

  /**
   * Tan(expr) - returns the tangent of `expr` (measured in radians). | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tan.md">Tan</a>
   */
  public static final IBuiltInSymbol Tan = F.initFinalSymbol("Tan", ID.Tan);

  /**
   * Tanh(z) - returns the hyperbolic tangent of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tanh.md">Tanh</a>
   */
  public static final IBuiltInSymbol Tanh = F.initFinalSymbol("Tanh", ID.Tanh);

  /**
   * TautologyQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable by
   * all combinations of boolean `False` and `True` values for the `list-of-variables`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TautologyQ.md">TautologyQ</a>
   */
  public static final IBuiltInSymbol TautologyQ = F.initFinalSymbol("TautologyQ", ID.TautologyQ);

  public static final IBuiltInSymbol Taylor = F.initFinalSymbol("Taylor", ID.Taylor);

  /**
   * TeXForm(expr) - returns the TeX form of the evaluated `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TeXForm.md">TeXForm</a>
   */
  public static final IBuiltInSymbol TeXForm = F.initFinalSymbol("TeXForm", ID.TeXForm);

  public static final IBuiltInSymbol TensorDimensions =
      F.initFinalSymbol("TensorDimensions", ID.TensorDimensions);

  /**
   * TensorProduct(t1, t2, ...) - product of the tensors `t1, t2, ...`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TensorProduct.md">TensorProduct</a>
   */
  public static final IBuiltInSymbol TensorProduct =
      F.initFinalSymbol("TensorProduct", ID.TensorProduct);

  public static final IBuiltInSymbol TensorRank = F.initFinalSymbol("TensorRank", ID.TensorRank);

  public static final IBuiltInSymbol TensorSymmetry =
      F.initFinalSymbol("TensorSymmetry", ID.TensorSymmetry);

  public static final IBuiltInSymbol TestID = F.initFinalSymbol("TestID", ID.TestID);

  /**
   * TestReport("file-name-string") - load the unit tests from a `file-name-string` and print a
   * summary of the `VerificationTest` included in the file. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TestReport.md">TestReport</a>
   */
  public static final IBuiltInSymbol TestReport = F.initFinalSymbol("TestReport", ID.TestReport);

  public static final IBuiltInSymbol TestReportObject =
      F.initFinalSymbol("TestReportObject", ID.TestReportObject);

  /**
   * TestResultObject( ... ) - is an association wrapped in a `TestResultObject`returned from
   * `VerificationTest` which stores the results from executing a single unit test. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TestResultObject.md">TestResultObject</a>
   */
  public static final IBuiltInSymbol TestResultObject =
      F.initFinalSymbol("TestResultObject", ID.TestResultObject);

  public static final IBuiltInSymbol TextCell = F.initFinalSymbol("TextCell", ID.TextCell);

  public static final IBuiltInSymbol TextString = F.initFinalSymbol("TextString", ID.TextString);

  /**
   * Thread(f(args) - threads `f` over any lists that appear in `args`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Thread.md">Thread</a>
   */
  public static final IBuiltInSymbol Thread = F.initFinalSymbol("Thread", ID.Thread);

  /**
   * Through(p(f)[x]) - gives `p(f(x))`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Through.md">Through</a>
   */
  public static final IBuiltInSymbol Through = F.initFinalSymbol("Through", ID.Through);

  public static final IBuiltInSymbol Throw = F.initFinalSymbol("Throw", ID.Throw);

  /**
   * TimeConstrained(expression, seconds) - stop evaluation of `expression` if time measurement of
   * the evaluation exceeds `seconds` and return `$Aborted`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeConstrained.md">TimeConstrained</a>
   */
  public static final IBuiltInSymbol TimeConstrained =
      F.initFinalSymbol("TimeConstrained", ID.TimeConstrained);

  public static final IBuiltInSymbol TimeObject = F.initFinalSymbol("TimeObject", ID.TimeObject);

  public static final IBuiltInSymbol TimeRemaining =
      F.initFinalSymbol("TimeRemaining", ID.TimeRemaining);

  /**
   * TimeValue(p, i, n) - returns a time value calculation. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeValue.md">TimeValue</a>
   */
  public static final IBuiltInSymbol TimeValue = F.initFinalSymbol("TimeValue", ID.TimeValue);

  /**
   * Times(a, b, ...) - represents the product of the terms `a, b, ...`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times</a>
   */
  public static final IBuiltInSymbol Times = F.initFinalSymbol("Times", ID.Times);

  /**
   * TimesBy(x, dx) - is equivalent to `x = x * dx`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimesBy.md">TimesBy</a>
   */
  public static final IBuiltInSymbol TimesBy = F.initFinalSymbol("TimesBy", ID.TimesBy);

  /**
   * Timing(x) - returns a list with the first entry containing the evaluation CPU time of `x` and
   * the second entry is the evaluation result of `x`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Timing.md">Timing</a>
   */
  public static final IBuiltInSymbol Timing = F.initFinalSymbol("Timing", ID.Timing);

  /**
   * ToCharacterCode(string) - converts `string` into a list of corresponding integer character
   * codes. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToCharacterCode.md">ToCharacterCode</a>
   */
  public static final IBuiltInSymbol ToCharacterCode =
      F.initFinalSymbol("ToCharacterCode", ID.ToCharacterCode);

  /**
   * ToExpression("string", form) - converts the `string` given in `form` into an expression. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToExpression.md">ToExpression</a>
   */
  public static final IBuiltInSymbol ToExpression =
      F.initFinalSymbol("ToExpression", ID.ToExpression);

  /**
   * ToPolarCoordinates({x, y}) - return the polar coordinates for the cartesian coordinates `{x,
   * y}`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToPolarCoordinates.md">ToPolarCoordinates</a>
   */
  public static final IBuiltInSymbol ToPolarCoordinates =
      F.initFinalSymbol("ToPolarCoordinates", ID.ToPolarCoordinates);

  public static final IBuiltInSymbol ToRadicals = F.initFinalSymbol("ToRadicals", ID.ToRadicals);

  /**
   * ToString(expr) - converts `expr` into a string. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToString.md">ToString</a>
   */
  public static final IBuiltInSymbol ToString = F.initFinalSymbol("ToString", ID.ToString);

  /**
   * ToUnicode(string) - converts `string` into a string of corresponding unicode character codes. |
   * See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToUnicode.md">ToUnicode</a>
   */
  public static final IBuiltInSymbol ToUnicode = F.initFinalSymbol("ToUnicode", ID.ToUnicode);

  public static final IBuiltInSymbol Today = F.initFinalSymbol("Today", ID.Today);

  /**
   * ToeplitzMatrix(n) - gives a toeplitz matrix with the dimension `n`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToeplitzMatrix.md">ToeplitzMatrix</a>
   */
  public static final IBuiltInSymbol ToeplitzMatrix =
      F.initFinalSymbol("ToeplitzMatrix", ID.ToeplitzMatrix);

  /**
   * Together(expr) - writes sums of fractions in `expr` together. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Together.md">Together</a>
   */
  public static final IBuiltInSymbol Together = F.initFinalSymbol("Together", ID.Together);

  public static final IBuiltInSymbol TooLarge = F.initFinalSymbol("TooLarge", ID.TooLarge);

  public static final IBuiltInSymbol Top = F.initFinalSymbol("Top", ID.Top);

  /**
   * Total(list) - adds all values in `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Total.md">Total</a>
   */
  public static final IBuiltInSymbol Total = F.initFinalSymbol("Total", ID.Total);

  /**
   * Tr(matrix) - computes the trace of the `matrix`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tr.md">Tr</a>
   */
  public static final IBuiltInSymbol Tr = F.initFinalSymbol("Tr", ID.Tr);

  /**
   * Trace(expr) - return the evaluation steps which are used to get the result. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Trace.md">Trace</a>
   */
  public static final IBuiltInSymbol Trace = F.initFinalSymbol("Trace", ID.Trace);

  public static final IBuiltInSymbol TraceForm = F.initFinalSymbol("TraceForm", ID.TraceForm);

  public static final IBuiltInSymbol TraditionalForm =
      F.initFinalSymbol("TraditionalForm", ID.TraditionalForm);

  /**
   * Transliterate("string") - try converting the given string to a similar ASCII string | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Transliterate.md">Transliterate</a>
   */
  public static final IBuiltInSymbol Transliterate =
      F.initFinalSymbol("Transliterate", ID.Transliterate);

  /**
   * Transpose(m) - transposes rows and columns in the matrix `m`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Transpose.md">Transpose</a>
   */
  public static final IBuiltInSymbol Transpose = F.initFinalSymbol("Transpose", ID.Transpose);

  /**
   * TreeForm(expr) - create a tree visualization from the given expression `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TreeForm.md">TreeForm</a>
   */
  public static final IBuiltInSymbol TreeForm = F.initFinalSymbol("TreeForm", ID.TreeForm);

  public static final IBuiltInSymbol Trig = F.initFinalSymbol("Trig", ID.Trig);

  /**
   * TrigExpand(expr) - expands out trigonometric expressions in `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigExpand.md">TrigExpand</a>
   */
  public static final IBuiltInSymbol TrigExpand = F.initFinalSymbol("TrigExpand", ID.TrigExpand);

  /**
   * TrigReduce(expr) - rewrites products and powers of trigonometric functions in `expr` in terms
   * of trigonometric functions with combined arguments. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigReduce.md">TrigReduce</a>
   */
  public static final IBuiltInSymbol TrigReduce = F.initFinalSymbol("TrigReduce", ID.TrigReduce);

  /**
   * TrigToExp(expr) - converts trigonometric functions in `expr` to exponentials. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigToExp.md">TrigToExp</a>
   */
  public static final IBuiltInSymbol TrigToExp = F.initFinalSymbol("TrigToExp", ID.TrigToExp);

  /**
   * True - the constant `True` represents the boolean value **true** | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/True.md">True</a>
   */
  public static final IBuiltInSymbol True = F.initFinalSymbol("True", ID.True);

  /**
   * TrueQ(expr) - returns `True` if and only if `expr` is `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrueQ.md">TrueQ</a>
   */
  public static final IBuiltInSymbol TrueQ = F.initFinalSymbol("TrueQ", ID.TrueQ);

  public static final IBuiltInSymbol TukeyWindow = F.initFinalSymbol("TukeyWindow", ID.TukeyWindow);

  /**
   * Tuples(list, n) - creates a list of all `n`-tuples of elements in `list`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tuples.md">Tuples</a>
   */
  public static final IBuiltInSymbol Tuples = F.initFinalSymbol("Tuples", ID.Tuples);

  public static final IBuiltInSymbol TwoWayRule = F.initFinalSymbol("TwoWayRule", ID.TwoWayRule);

  public static final IBuiltInSymbol URLFetch = F.initFinalSymbol("URLFetch", ID.URLFetch);

  public static final IBuiltInSymbol Undefined = F.initFinalSymbol("Undefined", ID.Undefined);

  public static final IBuiltInSymbol Underoverscript =
      F.initFinalSymbol("Underoverscript", ID.Underoverscript);

  public static final IBuiltInSymbol UndirectedEdge =
      F.initFinalSymbol("UndirectedEdge", ID.UndirectedEdge);

  /**
   * Unequal(x, y) - yields `False` if `x` and `y` are known to be equal, or `True` if `x` and `y`
   * are known to be unequal. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unequal.md">Unequal</a>
   */
  public static final IBuiltInSymbol Unequal = F.initFinalSymbol("Unequal", ID.Unequal);

  /**
   * Unevaluated(expr) - temporarily leaves `expr` in an unevaluated form when it appears as a
   * function argument. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unevaluated.md">Unevaluated</a>
   */
  public static final IBuiltInSymbol Unevaluated = F.initFinalSymbol("Unevaluated", ID.Unevaluated);

  /**
   * UniformDistribution({min, max}) - returns a uniform distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UniformDistribution.md">UniformDistribution</a>
   */
  public static final IBuiltInSymbol UniformDistribution =
      F.initFinalSymbol("UniformDistribution", ID.UniformDistribution);

  /**
   * Union(set1, set2) - get the union set from `set1` and `set2`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Union.md">Union</a>
   */
  public static final IBuiltInSymbol Union = F.initFinalSymbol("Union", ID.Union);

  /**
   * Unique(expr) - create a unique symbol of the form `expr$...`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unique.md">Unique</a>
   */
  public static final IBuiltInSymbol Unique = F.initFinalSymbol("Unique", ID.Unique);

  /**
   * UnitConvert(quantity) - convert the `quantity` to the base unit | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitConvert.md">UnitConvert</a>
   */
  public static final IBuiltInSymbol UnitConvert = F.initFinalSymbol("UnitConvert", ID.UnitConvert);

  /**
   * UnitStep(expr) - returns `0`, if `expr` is less than `0` and returns `1`, if `expr` is greater
   * equal than `0`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitStep.md">UnitStep</a>
   */
  public static final IBuiltInSymbol UnitStep = F.initFinalSymbol("UnitStep", ID.UnitStep);

  /**
   * UnitVector(position) - returns a unit vector with element `1` at the given `position`. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitVector.md">UnitVector</a>
   */
  public static final IBuiltInSymbol UnitVector = F.initFinalSymbol("UnitVector", ID.UnitVector);

  public static final IBuiltInSymbol UnitaryMatrixQ =
      F.initFinalSymbol("UnitaryMatrixQ", ID.UnitaryMatrixQ);

  /**
   * Unitize(expr) - maps a non-zero `expr` to `1`, and a zero `expr` to `0`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unitize.md">Unitize</a>
   */
  public static final IBuiltInSymbol Unitize = F.initFinalSymbol("Unitize", ID.Unitize);

  public static final IBuiltInSymbol Unknown = F.initFinalSymbol("Unknown", ID.Unknown);

  public static final IBuiltInSymbol Unprotect = F.initFinalSymbol("Unprotect", ID.Unprotect);

  /**
   * UnsameQ(x, y) - returns `True` if `x` and `y` are not structurally identical. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnsameQ.md">UnsameQ</a>
   */
  public static final IBuiltInSymbol UnsameQ = F.initFinalSymbol("UnsameQ", ID.UnsameQ);

  /**
   * Unset(expr) - removes any definitions belonging to the left-hand-side `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unset.md">Unset</a>
   */
  public static final IBuiltInSymbol Unset = F.initFinalSymbol("Unset", ID.Unset);

  public static final IBuiltInSymbol UpSet = F.initFinalSymbol("UpSet", ID.UpSet);

  public static final IBuiltInSymbol UpSetDelayed =
      F.initFinalSymbol("UpSetDelayed", ID.UpSetDelayed);

  public static final IBuiltInSymbol UpTo = F.initFinalSymbol("UpTo", ID.UpTo);

  /**
   * UpValues(symbol) - prints the up-value rules associated with `symbol`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpValues.md">UpValues</a>
   */
  public static final IBuiltInSymbol UpValues = F.initFinalSymbol("UpValues", ID.UpValues);

  /**
   * UpperCaseQ(str) - is `True` if the given `str` is a string which only contains upper case
   * characters. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperCaseQ.md">UpperCaseQ</a>
   */
  public static final IBuiltInSymbol UpperCaseQ = F.initFinalSymbol("UpperCaseQ", ID.UpperCaseQ);

  /**
   * UpperTriangularize(matrix) - create a upper triangular matrix from the given `matrix`. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperTriangularize.md">UpperTriangularize</a>
   */
  public static final IBuiltInSymbol UpperTriangularize =
      F.initFinalSymbol("UpperTriangularize", ID.UpperTriangularize);

  /**
   * ValueQ(expr) - returns `True` if and only if `expr` is defined. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ValueQ.md">ValueQ</a>
   */
  public static final IBuiltInSymbol ValueQ = F.initFinalSymbol("ValueQ", ID.ValueQ);

  /**
   * Values(association) - return a list of values of the `association`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Values.md">Values</a>
   */
  public static final IBuiltInSymbol Values = F.initFinalSymbol("Values", ID.Values);

  /**
   * VandermondeMatrix(n) - gives the Vandermonde matrix with `n` rows and columns. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VandermondeMatrix.md">VandermondeMatrix</a>
   */
  public static final IBuiltInSymbol VandermondeMatrix =
      F.initFinalSymbol("VandermondeMatrix", ID.VandermondeMatrix);

  public static final IBuiltInSymbol Variable = F.initFinalSymbol("Variable", ID.Variable);

  /**
   * Variables(expr) - gives a list of the variables that appear in the polynomial `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Variables.md">Variables</a>
   */
  public static final IBuiltInSymbol Variables = F.initFinalSymbol("Variables", ID.Variables);

  /**
   * Variance(list) - computes the variance of `list`. `list` may consist of numerical values or
   * symbols. Numerical values may be real or complex. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Variance.md">Variance</a>
   */
  public static final IBuiltInSymbol Variance = F.initFinalSymbol("Variance", ID.Variance);

  /**
   * VectorAngle(u, v) - gives the angles between vectors `u` and `v` | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorAngle.md">VectorAngle</a>
   */
  public static final IBuiltInSymbol VectorAngle = F.initFinalSymbol("VectorAngle", ID.VectorAngle);

  /**
   * VectorQ(v) - returns `True` if `v` is a list of elements which are not themselves lists. | See:
   * <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorQ.md">VectorQ</a>
   */
  public static final IBuiltInSymbol VectorQ = F.initFinalSymbol("VectorQ", ID.VectorQ);

  /**
   * Verbatim(expr) - prevents pattern constructs in `expr` from taking effect, allowing them to
   * match themselves. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Verbatim.md">Verbatim</a>
   */
  public static final IBuiltInSymbol Verbatim = F.initFinalSymbol("Verbatim", ID.Verbatim);

  /**
   * VerificationTest(test-expr) - create a `TestResultObject` by testing if `test-expr` evaluates
   * to `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VerificationTest.md">VerificationTest</a>
   */
  public static final IBuiltInSymbol VerificationTest =
      F.initFinalSymbol("VerificationTest", ID.VerificationTest);

  /**
   * VertexEccentricity(graph, vertex) - compute the eccentricity of `vertex` in the `graph`. It's
   * the length of the longest shortest path from the `vertex` to every other vertex in the `graph`.
   * | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexEccentricity.md">VertexEccentricity</a>
   */
  public static final IBuiltInSymbol VertexEccentricity =
      F.initFinalSymbol("VertexEccentricity", ID.VertexEccentricity);

  public static final IBuiltInSymbol VertexLabels =
      F.initFinalSymbol("VertexLabels", ID.VertexLabels);

  /**
   * VertexList(graph) - convert the `graph` into a list of vertices. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexList.md">VertexList</a>
   */
  public static final IBuiltInSymbol VertexList = F.initFinalSymbol("VertexList", ID.VertexList);

  /**
   * VertexQ(graph, vertex) - test if `vertex` is a vertex in the `graph` object. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexQ.md">VertexQ</a>
   */
  public static final IBuiltInSymbol VertexQ = F.initFinalSymbol("VertexQ", ID.VertexQ);

  public static final IBuiltInSymbol VertexShapeFunction =
      F.initFinalSymbol("VertexShapeFunction", ID.VertexShapeFunction);

  public static final IBuiltInSymbol VertexSize = F.initFinalSymbol("VertexSize", ID.VertexSize);

  public static final IBuiltInSymbol VertexStyle = F.initFinalSymbol("VertexStyle", ID.VertexStyle);

  public static final IBuiltInSymbol ViewPoint = F.initFinalSymbol("ViewPoint", ID.ViewPoint);

  /**
   * WeibullDistribution(a, b) - returns a Weibull distribution. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeibullDistribution.md">WeibullDistribution</a>
   */
  public static final IBuiltInSymbol WeibullDistribution =
      F.initFinalSymbol("WeibullDistribution", ID.WeibullDistribution);

  public static final IBuiltInSymbol WeierstrassHalfPeriods =
      F.initFinalSymbol("WeierstrassHalfPeriods", ID.WeierstrassHalfPeriods);

  public static final IBuiltInSymbol WeierstrassInvariants =
      F.initFinalSymbol("WeierstrassInvariants", ID.WeierstrassInvariants);

  /**
   * WeierstrassP(expr, {n1, n2}) - Weierstrass elliptic function. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeierstrassP.md">WeierstrassP</a>
   */
  public static final IBuiltInSymbol WeierstrassP =
      F.initFinalSymbol("WeierstrassP", ID.WeierstrassP);

  public static final IBuiltInSymbol WeierstrassPPrime =
      F.initFinalSymbol("WeierstrassPPrime", ID.WeierstrassPPrime);

  public static final IBuiltInSymbol WeightedAdjacencyMatrix =
      F.initFinalSymbol("WeightedAdjacencyMatrix", ID.WeightedAdjacencyMatrix);

  public static final IBuiltInSymbol WeightedData =
      F.initFinalSymbol("WeightedData", ID.WeightedData);

  /**
   * Which(cond1, expr1, cond2, expr2, ...) - yields `expr1` if `cond1` evaluates to `True`, `expr2`
   * if `cond2` evaluates to `True`, etc. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Which.md">Which</a>
   */
  public static final IBuiltInSymbol Which = F.initFinalSymbol("Which", ID.Which);

  /**
   * While(test, body) - evaluates `body` as long as test evaluates to `True`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/While.md">While</a>
   */
  public static final IBuiltInSymbol While = F.initFinalSymbol("While", ID.While);

  public static final IBuiltInSymbol White = F.initFinalSymbol("White", ID.White);

  /**
   * Whitespace - represents a sequence of whitespace characters. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Whitespace.md">Whitespace</a>
   */
  public static final IBuiltInSymbol Whitespace = F.initFinalSymbol("Whitespace", ID.Whitespace);

  /**
   * WhitespaceCharacter - represents a single whitespace character. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WhitespaceCharacter.md">WhitespaceCharacter</a>
   */
  public static final IBuiltInSymbol WhitespaceCharacter =
      F.initFinalSymbol("WhitespaceCharacter", ID.WhitespaceCharacter);

  public static final IBuiltInSymbol WhittakerM = F.initFinalSymbol("WhittakerM", ID.WhittakerM);

  public static final IBuiltInSymbol WhittakerW = F.initFinalSymbol("WhittakerW", ID.WhittakerW);

  /**
   * With({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables` by
   * replacing the local variables in `expr`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/With.md">With</a>
   */
  public static final IBuiltInSymbol With = F.initFinalSymbol("With", ID.With);

  /**
   * WordBoundary - represents the boundary between words. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WordBoundary.md">WordBoundary</a>
   */
  public static final IBuiltInSymbol WordBoundary =
      F.initFinalSymbol("WordBoundary", ID.WordBoundary);

  /**
   * WordCharacter] - represents a single letter or digit character. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WordCharacter.md">WordCharacter</a>
   */
  public static final IBuiltInSymbol WordCharacter =
      F.initFinalSymbol("WordCharacter", ID.WordCharacter);

  public static final IBuiltInSymbol WriteString = F.initFinalSymbol("WriteString", ID.WriteString);

  /**
   * Xor(arg1, arg2, ...) - Logical XOR (exclusive OR) function. Returns `True` if an odd number of
   * the arguments are `True` and the rest are `False`. Returns `False` if an even number of the
   * arguments are `True` and the rest are `False`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Xor.md">Xor</a>
   */
  public static final IBuiltInSymbol Xor = F.initFinalSymbol("Xor", ID.Xor);

  public static final IBuiltInSymbol Yellow = F.initFinalSymbol("Yellow", ID.Yellow);

  /**
   * YuleDissimilarity(u, v) - returns the Yule dissimilarity between the two boolean 1-D lists `u`
   * and `v`, which is defined as `R / (c_tt * c_ff + R / 2)` where `n` is `len(u)`, `c_ij` is the
   * number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * c_tf * c_ft`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/YuleDissimilarity.md">YuleDissimilarity</a>
   */
  public static final IBuiltInSymbol YuleDissimilarity =
      F.initFinalSymbol("YuleDissimilarity", ID.YuleDissimilarity);

  public static final IBuiltInSymbol ZeroSymmetric =
      F.initFinalSymbol("ZeroSymmetric", ID.ZeroSymmetric);

  public static final IBuiltInSymbol ZeroTest = F.initFinalSymbol("ZeroTest", ID.ZeroTest);

  /**
   * Zeta(z) - returns the Riemann zeta function of `z`. | See: <a
   * href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Zeta.md">Zeta</a>
   */
  public static final IBuiltInSymbol Zeta = F.initFinalSymbol("Zeta", ID.Zeta);

  public static final ISymbol $RealVector =
      initFinalHiddenSymbol(FEConfig.PARSER_USE_LOWERCASE_SYMBOLS ? "$realvector" : "$RealVector");
  public static final ISymbol $RealMatrix =
      initFinalHiddenSymbol(FEConfig.PARSER_USE_LOWERCASE_SYMBOLS ? "$realmatrix" : "$RealMatrix");

  /** Used to represent a formal parameter <code>a</code> that will never be assigned a value. */
  public static final ISymbol a = initFinalHiddenSymbol("a");
  /** Used to represent a formal parameter <code>b</code> that will never be assigned a value. */
  public static final ISymbol b = initFinalHiddenSymbol("b");
  /** Used to represent a formal parameter <code>c</code> that will never be assigned a value. */
  public static final ISymbol c = initFinalHiddenSymbol("c");
  /** Used to represent a formal parameter <code>d</code> that will never be assigned a value. */
  public static final ISymbol d = initFinalHiddenSymbol("d");
  /** Used to represent a formal parameter <code>e</code> that will never be assigned a value. */
  public static final ISymbol e = initFinalHiddenSymbol("e");
  /** Used to represent a formal parameter <code>f</code> that will never be assigned a value. */
  public static final ISymbol f = initFinalHiddenSymbol("f");
  /** Used to represent a formal parameter <code>g</code> that will never be assigned a value. */
  public static final ISymbol g = initFinalHiddenSymbol("g");
  /** Used to represent a formal parameter <code>h</code> that will never be assigned a value. */
  public static final ISymbol h = initFinalHiddenSymbol("h");
  /** Used to represent a formal parameter <code>i</code> that will never be assigned a value. */
  public static final ISymbol i = initFinalHiddenSymbol("i");
  /** Used to represent a formal parameter <code>j</code> that will never be assigned a value. */
  public static final ISymbol j = initFinalHiddenSymbol("j");
  /** Used to represent a formal parameter <code>k</code> that will never be assigned a value. */
  public static final ISymbol k = initFinalHiddenSymbol("k");
  /** Used to represent a formal parameter <code>l</code> that will never be assigned a value. */
  public static final ISymbol l = initFinalHiddenSymbol("l");
  /** Used to represent a formal parameter <code>m</code> that will never be assigned a value. */
  public static final ISymbol m = initFinalHiddenSymbol("m");
  /** Used to represent a formal parameter <code>n</code> that will never be assigned a value. */
  public static final ISymbol n = initFinalHiddenSymbol("n");
  /** Used to represent a formal parameter <code>o</code> that will never be assigned a value. */
  public static final ISymbol o = initFinalHiddenSymbol("o");
  /** Used to represent a formal parameter <code>p</code> that will never be assigned a value. */
  public static final ISymbol p = initFinalHiddenSymbol("p");
  /** Used to represent a formal parameter <code>q</code> that will never be assigned a value. */
  public static final ISymbol q = initFinalHiddenSymbol("q");
  /** Used to represent a formal parameter <code>r</code> that will never be assigned a value. */
  public static final ISymbol r = initFinalHiddenSymbol("r");
  /** Used to represent a formal parameter <code>s</code> that will never be assigned a value. */
  public static final ISymbol s = initFinalHiddenSymbol("s");
  /** Used to represent a formal parameter <code>t</code> that will never be assigned a value. */
  public static final ISymbol t = initFinalHiddenSymbol("t");
  /** Used to represent a formal parameter <code>u</code> that will never be assigned a value. */
  public static final ISymbol u = initFinalHiddenSymbol("u");
  /** Used to represent a formal parameter <code>v</code> that will never be assigned a value. */
  public static final ISymbol v = initFinalHiddenSymbol("v");
  /** Used to represent a formal parameter <code>w</code> that will never be assigned a value. */
  public static final ISymbol w = initFinalHiddenSymbol("w");
  /** Used to represent a formal parameter <code>x</code> that will never be assigned a value. */
  public static final ISymbol x = initFinalHiddenSymbol("x");
  /** Used to represent a formal parameter <code>y</code> that will never be assigned a value. */
  public static final ISymbol y = initFinalHiddenSymbol("y");
  /** Used to represent a formal parameter <code>z</code> that will never be assigned a value. */
  public static final ISymbol z = initFinalHiddenSymbol("z");

  public static final ISymbol ASymbol = initFinalHiddenSymbol("A");
  public static final ISymbol BSymbol = initFinalHiddenSymbol("B");
  public static final ISymbol CSymbol =
      initFinalHiddenSymbol("C"); // don't use constant BuiltinSymbol 'C' here
  public static final ISymbol FSymbol = initFinalHiddenSymbol("F");
  public static final ISymbol GSymbol = initFinalHiddenSymbol("G");
  public static final ISymbol PSymbol = initFinalHiddenSymbol("P");
  public static final ISymbol QSymbol = initFinalHiddenSymbol("Q");

  /**
   * Used to represent a formal parameter <code>LHS_HEAD</code> that will never be assigned a value.
   * Used for setting the left-hand-side in pattern-matching for <code>OptionValue(...)</code>
   */
  public static final ISymbol LHS_HEAD = initFinalHiddenSymbol("LHSHead");

  /**
   * Convert the symbolName to lowercase (if <code>Config.PARSER_USE_LOWERCASE_SYMBOLS</code> is
   * set) and insert a new Symbol in the <code>PREDEFINED_SYMBOLS_MAP</code>. The symbol is created
   * using the given upper case string to use it as associated class name in package
   * org.matheclipse.core.reflection.system.
   *
   * @param symbolName the predefined symbol name in upper-case form
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
   * Convert the symbolName to lowercase (if <code>Config.PARSER_USE_LOWERCASE_SYMBOLS</code> is
   * set) and insert a new Symbol in the <code>PREDEFINED_SYMBOLS_MAP</code>. The symbol is created
   * using the given upper case string to use it as associated class name in package
   * org.matheclipse.core.reflection.system.
   *
   * @param symbolName the predefined symbol name in upper-case form
   * @return
   */
  public static ISymbol initFinalHiddenSymbol(final String symbolName) {
    final ISymbol temp = new Symbol(symbolName, org.matheclipse.core.expression.Context.DUMMY);
    HIDDEN_SYMBOLS_MAP.put(symbolName, temp);
    return temp;
  }

  /**
   * Return the predefined expression corresponding to the <code>id</code> from the internal table
   * of built-in symbols {@link #BUILT_IN_SYMBOLS} or from the internal table of predefined constant
   * expressions {@link #COMMON_IDS}.
   *
   * @param id
   * @return
   */
  public static IExpr exprID(short id) {
    if (id >= EXPRID_MAX_BUILTIN_LENGTH) {
      return COMMON_IDS[id - EXPRID_MAX_BUILTIN_LENGTH];
    }
    return BUILT_IN_SYMBOLS[id];
  }
}
