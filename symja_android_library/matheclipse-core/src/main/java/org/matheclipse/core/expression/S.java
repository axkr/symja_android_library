package org.matheclipse.core.expression;

import java.util.IdentityHashMap;
import java.util.Map;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.trie.TrieMatch;

/**
 * Class for creating the static Symja built-in symbols (interface {@link IBuiltInSymbol}). The
 * built-in symbols are generated with the tools class <code>BuiltinGenerator</code>.
 */
public class S {

  protected S() {} // static use only

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
  static final Map<IExpr, Short> GLOBAL_IDS_MAP =
      new IdentityHashMap<>((EXPRID_MAX_BUILTIN_LENGTH + 1000) * 4 / 3 + 1);

  static final Map<String, ISymbol> HIDDEN_SYMBOLS_MAP =
      Config.TRIE_STRING2SYMBOL_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();

  public static IBuiltInSymbol symbol(int id) {
    return BUILT_IN_SYMBOLS[id];
  }

  public final static IBuiltInSymbol $Aborted = F.initFinalSymbol("$Aborted", ID.$Aborted);

  /**
   * $Assumptions - contains the default assumptions for `Integrate`, `Refine` and `Simplify`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$Assumptions.md">$Assumptions
   *      documentation</a>
   */
  public final static IBuiltInSymbol $Assumptions =
      F.initFinalSymbol("$Assumptions", ID.$Assumptions);

  public final static IBuiltInSymbol $BaseDirectory =
      F.initFinalSymbol("$BaseDirectory", ID.$BaseDirectory);

  public final static IBuiltInSymbol $Cancel = F.initFinalSymbol("$Cancel", ID.$Cancel);

  public final static IBuiltInSymbol $Context = F.initFinalSymbol("$Context", ID.$Context);

  public final static IBuiltInSymbol $ContextPath =
      F.initFinalSymbol("$ContextPath", ID.$ContextPath);

  public final static IBuiltInSymbol $CreationDate =
      F.initFinalSymbol("$CreationDate", ID.$CreationDate);

  public final static IBuiltInSymbol $DisplayFunction =
      F.initFinalSymbol("$DisplayFunction", ID.$DisplayFunction);

  public final static IBuiltInSymbol $Failed = F.initFinalSymbol("$Failed", ID.$Failed);

  /**
   * $HistoryLength - specifies the maximum number of `In` and `Out` entries.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$HistoryLength.md">$HistoryLength
   *      documentation</a>
   */
  public final static IBuiltInSymbol $HistoryLength =
      F.initFinalSymbol("$HistoryLength", ID.$HistoryLength);

  public final static IBuiltInSymbol $HomeDirectory =
      F.initFinalSymbol("$HomeDirectory", ID.$HomeDirectory);

  public final static IBuiltInSymbol $IdentityMatrix =
      F.initFinalSymbol("$IdentityMatrix", ID.$IdentityMatrix);

  public final static IBuiltInSymbol $Input = F.initFinalSymbol("$Input", ID.$Input);

  public final static IBuiltInSymbol $InputFileName =
      F.initFinalSymbol("$InputFileName", ID.$InputFileName);

  /**
   * $IterationLimit - specifies the maximum number of times a reevaluation of an expression may
   * happen.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$IterationLimit.md">$IterationLimit
   *      documentation</a>
   */
  public final static IBuiltInSymbol $IterationLimit =
      F.initFinalSymbol("$IterationLimit", ID.$IterationLimit);

  /**
   * $Line - holds the current input line number.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$Line.md">$Line
   *      documentation</a>
   */
  public final static IBuiltInSymbol $Line = F.initFinalSymbol("$Line", ID.$Line);

  public final static IBuiltInSymbol $MachineEpsilon =
      F.initFinalSymbol("$MachineEpsilon", ID.$MachineEpsilon);

  public final static IBuiltInSymbol $MachinePrecision =
      F.initFinalSymbol("$MachinePrecision", ID.$MachinePrecision);

  public final static IBuiltInSymbol $MaxMachineNumber =
      F.initFinalSymbol("$MaxMachineNumber", ID.$MaxMachineNumber);

  public final static IBuiltInSymbol $MessageList =
      F.initFinalSymbol("$MessageList", ID.$MessageList);

  public final static IBuiltInSymbol $MinMachineNumber =
      F.initFinalSymbol("$MinMachineNumber", ID.$MinMachineNumber);

  public final static IBuiltInSymbol $Notebooks = F.initFinalSymbol("$Notebooks", ID.$Notebooks);

  /**
   * $OperatingSystem - gives the type of operating system ("Windows", "MacOSX", or "Unix") running
   * Symja.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$OperatingSystem.md">$OperatingSystem
   *      documentation</a>
   */
  public final static IBuiltInSymbol $OperatingSystem =
      F.initFinalSymbol("$OperatingSystem", ID.$OperatingSystem);

  public final static IBuiltInSymbol $OutputSizeLimit =
      F.initFinalSymbol("$OutputSizeLimit", ID.$OutputSizeLimit);

  public final static IBuiltInSymbol $Packages = F.initFinalSymbol("$Packages", ID.$Packages);

  public final static IBuiltInSymbol $Path = F.initFinalSymbol("$Path", ID.$Path);

  public final static IBuiltInSymbol $PathnameSeparator =
      F.initFinalSymbol("$PathnameSeparator", ID.$PathnameSeparator);

  public final static IBuiltInSymbol $PrePrint = F.initFinalSymbol("$PrePrint", ID.$PrePrint);

  public final static IBuiltInSymbol $PreRead = F.initFinalSymbol("$PreRead", ID.$PreRead);

  /**
   * $RecursionLimit - holds the current input line number
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$RecursionLimit.md">$RecursionLimit
   *      documentation</a>
   */
  public final static IBuiltInSymbol $RecursionLimit =
      F.initFinalSymbol("$RecursionLimit", ID.$RecursionLimit);

  public final static IBuiltInSymbol $RootDirectory =
      F.initFinalSymbol("$RootDirectory", ID.$RootDirectory);

  /**
   * $ScriptCommandLine - is a list of string arguments when running Symja in script mode. The list
   * starts with the name of the script.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$ScriptCommandLine.md">$ScriptCommandLine
   *      documentation</a>
   */
  public final static IBuiltInSymbol $ScriptCommandLine =
      F.initFinalSymbol("$ScriptCommandLine", ID.$ScriptCommandLine);

  public final static IBuiltInSymbol $SingleEntryMatrix =
      F.initFinalSymbol("$SingleEntryMatrix", ID.$SingleEntryMatrix);

  public final static IBuiltInSymbol $SystemCharacterEncoding =
      F.initFinalSymbol("$SystemCharacterEncoding", ID.$SystemCharacterEncoding);

  public final static IBuiltInSymbol $SystemMemory =
      F.initFinalSymbol("$SystemMemory", ID.$SystemMemory);

  public final static IBuiltInSymbol $TemporaryDirectory =
      F.initFinalSymbol("$TemporaryDirectory", ID.$TemporaryDirectory);

  public final static IBuiltInSymbol $UserBaseDirectory =
      F.initFinalSymbol("$UserBaseDirectory", ID.$UserBaseDirectory);

  public final static IBuiltInSymbol $UserName = F.initFinalSymbol("$UserName", ID.$UserName);

  public final static IBuiltInSymbol $Version = F.initFinalSymbol("$Version", ID.$Version);

  /**
   * Abort() - aborts an evaluation completely and returns `$Aborted`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Abort.md">Abort
   *      documentation</a>
   */
  public final static IBuiltInSymbol Abort = F.initFinalSymbol("Abort", ID.Abort);

  /**
   * Abs(expr) - returns the absolute value of the real or complex number `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Abs.md">Abs
   *      documentation</a>
   */
  public final static IBuiltInSymbol Abs = F.initFinalSymbol("Abs", ID.Abs);

  /**
   * AbsArg(expr) - returns a list of 2 values of the complex number `Abs(expr), Arg(expr)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AbsArg.md">AbsArg
   *      documentation</a>
   */
  public final static IBuiltInSymbol AbsArg = F.initFinalSymbol("AbsArg", ID.AbsArg);

  public final static IBuiltInSymbol AbsoluteCorrelation =
      F.initFinalSymbol("AbsoluteCorrelation", ID.AbsoluteCorrelation);

  public final static IBuiltInSymbol AbsoluteTime =
      F.initFinalSymbol("AbsoluteTime", ID.AbsoluteTime);

  /**
   * AbsoluteTiming(x) - returns a list with the first entry containing the evaluation time of `x`
   * and the second entry is the evaluation result of `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AbsoluteTiming.md">AbsoluteTiming
   *      documentation</a>
   */
  public final static IBuiltInSymbol AbsoluteTiming =
      F.initFinalSymbol("AbsoluteTiming", ID.AbsoluteTiming);

  /**
   * Accumulate(list) - accumulate the values of `list` returning a new list.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Accumulate.md">Accumulate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Accumulate = F.initFinalSymbol("Accumulate", ID.Accumulate);

  /**
   * AddSides(compare-expr, value) - add `value` to all elements of the `compare-expr`.
   * `compare-expr` can be `True`, `False` or an comparison expression with head `Equal, Unequal,
   * Less, LessEqual, Greater, GreaterEqual`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AddSides.md">AddSides
   *      documentation</a>
   */
  public final static IBuiltInSymbol AddSides = F.initFinalSymbol("AddSides", ID.AddSides);

  /**
   * AddTo(x, dx) - is equivalent to `x = x + dx`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AddTo.md">AddTo
   *      documentation</a>
   */
  public final static IBuiltInSymbol AddTo = F.initFinalSymbol("AddTo", ID.AddTo);

  public final static IBuiltInSymbol AddToClassPath =
      F.initFinalSymbol("AddToClassPath", ID.AddToClassPath);

  /**
   * AdjacencyMatrix(graph) - convert the `graph` into a adjacency matrix in sparse array format.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AdjacencyMatrix.md">AdjacencyMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol AdjacencyMatrix =
      F.initFinalSymbol("AdjacencyMatrix", ID.AdjacencyMatrix);

  /**
   * AiryAi(z) - returns the Airy function of the first kind of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryAi.md">AiryAi
   *      documentation</a>
   */
  public final static IBuiltInSymbol AiryAi = F.initFinalSymbol("AiryAi", ID.AiryAi);

  /**
   * AiryAiPrime(z) - returns the derivative of the `AiryAi` function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryAiPrime.md">AiryAiPrime
   *      documentation</a>
   */
  public final static IBuiltInSymbol AiryAiPrime = F.initFinalSymbol("AiryAiPrime", ID.AiryAiPrime);

  /**
   * AiryBi(z) - returns the Airy function of the second kind of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryBi.md">AiryBi
   *      documentation</a>
   */
  public final static IBuiltInSymbol AiryBi = F.initFinalSymbol("AiryBi", ID.AiryBi);

  /**
   * AiryBiPrime(z) - returns the derivative of the `AiryBi` function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryBiPrime.md">AiryBiPrime
   *      documentation</a>
   */
  public final static IBuiltInSymbol AiryBiPrime = F.initFinalSymbol("AiryBiPrime", ID.AiryBiPrime);

  public final static IBuiltInSymbol AlgebraicNumber =
      F.initFinalSymbol("AlgebraicNumber", ID.AlgebraicNumber);

  public final static IBuiltInSymbol Algebraics = F.initFinalSymbol("Algebraics", ID.Algebraics);

  /**
   * All - is a possible value for `Span` and `Quiet`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/All.md">All
   *      documentation</a>
   */
  public final static IBuiltInSymbol All = F.initFinalSymbol("All", ID.All);

  /**
   * AllTrue({expr1, expr2, ...}, test) - returns `True` if all applications of `test` to `expr1,
   * expr2, ...` evaluate to `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AllTrue.md">AllTrue
   *      documentation</a>
   */
  public final static IBuiltInSymbol AllTrue = F.initFinalSymbol("AllTrue", ID.AllTrue);

  public final static IBuiltInSymbol AllowShortContext =
      F.initFinalSymbol("AllowShortContext", ID.AllowShortContext);

  public final static IBuiltInSymbol AllowedHeads =
      F.initFinalSymbol("AllowedHeads", ID.AllowedHeads);

  /**
   * Alphabet() - gives the list of lowercase letters `a-z` in the English or Latin alphabet .
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Alphabet.md">Alphabet
   *      documentation</a>
   */
  public final static IBuiltInSymbol Alphabet = F.initFinalSymbol("Alphabet", ID.Alphabet);

  /**
   * Alternatives(p1, p2, ..., p_i) - is a pattern that matches any of the patterns `p1, p2,....,
   * p_i`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Alternatives.md">Alternatives
   *      documentation</a>
   */
  public final static IBuiltInSymbol Alternatives =
      F.initFinalSymbol("Alternatives", ID.Alternatives);

  public final static IBuiltInSymbol AmbientLight =
      F.initFinalSymbol("AmbientLight", ID.AmbientLight);

  /**
   * And(expr1, expr2, ...) - `expr1 && expr2 && ...` evaluates each expression in turn, returning
   * `False` as soon as an expression evaluates to `False`. If all expressions evaluate to `True`,
   * `And` returns `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/And.md">And
   *      documentation</a>
   */
  public final static IBuiltInSymbol And = F.initFinalSymbol("And", ID.And);

  /**
   * AngleVector(phi) - returns the point at angle `phi` on the unit circle.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AngleVector.md">AngleVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol AngleVector = F.initFinalSymbol("AngleVector", ID.AngleVector);

  public final static IBuiltInSymbol Annotation = F.initFinalSymbol("Annotation", ID.Annotation);

  /**
   * Annuity(p, t) - returns an annuity object.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Annuity.md">Annuity
   *      documentation</a>
   */
  public final static IBuiltInSymbol Annuity = F.initFinalSymbol("Annuity", ID.Annuity);

  /**
   * AnnuityDue(p, t) - returns an annuity due object.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnnuityDue.md">AnnuityDue
   *      documentation</a>
   */
  public final static IBuiltInSymbol AnnuityDue = F.initFinalSymbol("AnnuityDue", ID.AnnuityDue);

  public final static IBuiltInSymbol AntiSymmetric =
      F.initFinalSymbol("AntiSymmetric", ID.AntiSymmetric);

  /**
   * AntihermitianMatrixQ(m) - returns `True` if `m` is a anti hermitian matrix.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AntihermitianMatrixQ.md">AntihermitianMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol AntihermitianMatrixQ =
      F.initFinalSymbol("AntihermitianMatrixQ", ID.AntihermitianMatrixQ);

  /**
   * AntisymmetricMatrixQ(m) - returns `True` if `m` is a anti symmetric matrix.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AntisymmetricMatrixQ.md">AntisymmetricMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol AntisymmetricMatrixQ =
      F.initFinalSymbol("AntisymmetricMatrixQ", ID.AntisymmetricMatrixQ);

  /**
   * AnyTrue({expr1, expr2, ...}, test) - returns `True` if any application of `test` to `expr1,
   * expr2, ...` evaluates to `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnyTrue.md">AnyTrue
   *      documentation</a>
   */
  public final static IBuiltInSymbol AnyTrue = F.initFinalSymbol("AnyTrue", ID.AnyTrue);

  /**
   * Apart(expr) - rewrites `expr` as a sum of individual fractions.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Apart.md">Apart
   *      documentation</a>
   */
  public final static IBuiltInSymbol Apart = F.initFinalSymbol("Apart", ID.Apart);

  public final static IBuiltInSymbol AppellF1 = F.initFinalSymbol("AppellF1", ID.AppellF1);

  /**
   * Append(expr, item) - returns `expr` with `item` appended to its leaves.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Append.md">Append
   *      documentation</a>
   */
  public final static IBuiltInSymbol Append = F.initFinalSymbol("Append", ID.Append);

  /**
   * AppendTo(s, item) - append `item` to value of `s` and sets `s` to the result.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AppendTo.md">AppendTo
   *      documentation</a>
   */
  public final static IBuiltInSymbol AppendTo = F.initFinalSymbol("AppendTo", ID.AppendTo);

  /**
   * f @ expr - returns `f(expr)`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Apply.md">Apply
   *      documentation</a>
   */
  public final static IBuiltInSymbol Apply = F.initFinalSymbol("Apply", ID.Apply);

  /**
   * ApplySides(compare-expr, value) - divides all elements of the `compare-expr` by `value`.
   * `compare-expr` can be `True`, `False` or a comparison expression with head `Equal, Unequal,
   * Less, LessEqual, Greater, GreaterEqual`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ApplySides.md">ApplySides
   *      documentation</a>
   */
  public final static IBuiltInSymbol ApplySides = F.initFinalSymbol("ApplySides", ID.ApplySides);

  /**
   * ArcCos(expr) - returns the arc cosine (inverse cosine) of `expr` (measured in radians).
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCos.md">ArcCos
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcCos = F.initFinalSymbol("ArcCos", ID.ArcCos);

  /**
   * ArcCosh(z) - returns the inverse hyperbolic cosine of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCosh.md">ArcCosh
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcCosh = F.initFinalSymbol("ArcCosh", ID.ArcCosh);

  /**
   * ArcCot(z) - returns the inverse cotangent of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCot.md">ArcCot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcCot = F.initFinalSymbol("ArcCot", ID.ArcCot);

  /**
   * ArcCoth(z) - returns the inverse hyperbolic cotangent of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCoth.md">ArcCoth
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcCoth = F.initFinalSymbol("ArcCoth", ID.ArcCoth);

  /**
   * ArcCsc(z) - returns the inverse cosecant of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCsc.md">ArcCsc
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcCsc = F.initFinalSymbol("ArcCsc", ID.ArcCsc);

  /**
   * ArcCsch(z) - returns the inverse hyperbolic cosecant of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCsch.md">ArcCsch
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcCsch = F.initFinalSymbol("ArcCsch", ID.ArcCsch);

  public final static IBuiltInSymbol ArcLength = F.initFinalSymbol("ArcLength", ID.ArcLength);

  /**
   * ArcSec(z) - returns the inverse secant of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSec.md">ArcSec
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcSec = F.initFinalSymbol("ArcSec", ID.ArcSec);

  /**
   * ArcSech(z) - returns the inverse hyperbolic secant of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSech.md">ArcSech
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcSech = F.initFinalSymbol("ArcSech", ID.ArcSech);

  /**
   * ArcSin(expr) - returns the arc sine (inverse sine) of `expr` (measured in radians).
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSin.md">ArcSin
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcSin = F.initFinalSymbol("ArcSin", ID.ArcSin);

  /**
   * ArcSinh(z) - returns the inverse hyperbolic sine of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSinh.md">ArcSinh
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcSinh = F.initFinalSymbol("ArcSinh", ID.ArcSinh);

  /**
   * ArcTan(expr) - returns the arc tangent (inverse tangent) of `expr` (measured in radians).
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcTan.md">ArcTan
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcTan = F.initFinalSymbol("ArcTan", ID.ArcTan);

  /**
   * ArcTanh(z) - returns the inverse hyperbolic tangent of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcTanh.md">ArcTanh
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcTanh = F.initFinalSymbol("ArcTanh", ID.ArcTanh);

  public final static IBuiltInSymbol Area = F.initFinalSymbol("Area", ID.Area);

  /**
   * Arg(expr) - returns the argument of the complex number `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Arg.md">Arg
   *      documentation</a>
   */
  public final static IBuiltInSymbol Arg = F.initFinalSymbol("Arg", ID.Arg);

  /**
   * ArgMax(function, variable) - returns a maximizer point for a univariate `function`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArgMax.md">ArgMax
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArgMax = F.initFinalSymbol("ArgMax", ID.ArgMax);

  /**
   * ArgMin(function, variable) - returns a minimizer point for a univariate `function`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArgMin.md">ArgMin
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArgMin = F.initFinalSymbol("ArgMin", ID.ArgMin);

  /**
   * ArithmeticGeometricMean({a, b, c,...}) - returns the arithmetic geometric mean of `{a, b,
   * c,...}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArithmeticGeometricMean.md">ArithmeticGeometricMean
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArithmeticGeometricMean =
      F.initFinalSymbol("ArithmeticGeometricMean", ID.ArithmeticGeometricMean);

  /**
   * Array(f, n) - returns the `n`-element list `{f(1), ..., f(n)}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Array.md">Array
   *      documentation</a>
   */
  public final static IBuiltInSymbol Array = F.initFinalSymbol("Array", ID.Array);

  /**
   * ArrayDepth(a) - returns the depth of the non-ragged array `a`, defined as
   * `Length(Dimensions(a))`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayDepth.md">ArrayDepth
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArrayDepth = F.initFinalSymbol("ArrayDepth", ID.ArrayDepth);

  /**
   * ArrayPad(list, n) - adds `n` times `0` on the left and right of the `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayPad.md">ArrayPad
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArrayPad = F.initFinalSymbol("ArrayPad", ID.ArrayPad);

  /**
   * ArrayQ(expr) - tests whether expr is a full array.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayQ.md">ArrayQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArrayQ = F.initFinalSymbol("ArrayQ", ID.ArrayQ);

  /**
   * ArrayReshape(list-of-values, list-of-dimension) - returns the `list-of-values` elements
   * reshaped as nested list with dimensions according to the `list-of-dimension`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayReshape.md">ArrayReshape
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArrayReshape =
      F.initFinalSymbol("ArrayReshape", ID.ArrayReshape);

  /**
   * ArrayRules(sparse-array) - return the array of rules which define the sparse array.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayRules.md">ArrayRules
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArrayRules = F.initFinalSymbol("ArrayRules", ID.ArrayRules);

  public final static IBuiltInSymbol Arrays = F.initFinalSymbol("Arrays", ID.Arrays);

  /**
   * Arrow({p1, p2}) - represents a line from `p1` to `p2` that ends with an arrow at `p2`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Arrow.md">Arrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol Arrow = F.initFinalSymbol("Arrow", ID.Arrow);

  public final static IBuiltInSymbol Arrowheads = F.initFinalSymbol("Arrowheads", ID.Arrowheads);

  public final static IBuiltInSymbol AspectRatio = F.initFinalSymbol("AspectRatio", ID.AspectRatio);

  /**
   * AssociateTo(assoc, rule) - append `rule` to the association `assoc` and assign the result to
   * `assoc`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociateTo.md">AssociateTo
   *      documentation</a>
   */
  public final static IBuiltInSymbol AssociateTo = F.initFinalSymbol("AssociateTo", ID.AssociateTo);

  /**
   * Association(list-of-rules) - create a `key->value` association map from the `list-of-rules`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Association.md">Association
   *      documentation</a>
   */
  public final static IBuiltInSymbol Association = F.initFinalSymbol("Association", ID.Association);

  /**
   * AssociationMap(header, <|k1->v1, k2->v2,...|>) - create an association `<|header(k1->v1),
   * header(k2->v2),...|>` with the rules mapped by the `header`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationMap.md">AssociationMap
   *      documentation</a>
   */
  public final static IBuiltInSymbol AssociationMap =
      F.initFinalSymbol("AssociationMap", ID.AssociationMap);

  /**
   * AssociationQ(expr) - returns `True` if `expr` is an association, and `False` otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationQ.md">AssociationQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol AssociationQ =
      F.initFinalSymbol("AssociationQ", ID.AssociationQ);

  /**
   * AssociationThread({k1,k2,...}, {v1,v2,...}) - create an association with rules from the keys
   * `{k1,k2,...}` and values `{v1,v2,...}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationThread.md">AssociationThread
   *      documentation</a>
   */
  public final static IBuiltInSymbol AssociationThread =
      F.initFinalSymbol("AssociationThread", ID.AssociationThread);

  /**
   * Assuming(assumption, expression) - evaluate the `expression` with the assumptions appended to
   * the default `$Assumptions` assumptions.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Assuming.md">Assuming
   *      documentation</a>
   */
  public final static IBuiltInSymbol Assuming = F.initFinalSymbol("Assuming", ID.Assuming);

  public final static IBuiltInSymbol Assumptions = F.initFinalSymbol("Assumptions", ID.Assumptions);

  /**
   * AtomQ(x) - is true if `x` is an atom (an object such as a number or string, which cannot be
   * divided into subexpressions using 'Part').
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AtomQ.md">AtomQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol AtomQ = F.initFinalSymbol("AtomQ", ID.AtomQ);

  /**
   * Attributes(symbol) - returns the list of attributes which are assigned to `symbol`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Attributes.md">Attributes
   *      documentation</a>
   */
  public final static IBuiltInSymbol Attributes = F.initFinalSymbol("Attributes", ID.Attributes);

  public final static IBuiltInSymbol Automatic = F.initFinalSymbol("Automatic", ID.Automatic);

  public final static IBuiltInSymbol Axes = F.initFinalSymbol("Axes", ID.Axes);

  public final static IBuiltInSymbol AxesLabel = F.initFinalSymbol("AxesLabel", ID.AxesLabel);

  public final static IBuiltInSymbol AxesOrigin = F.initFinalSymbol("AxesOrigin", ID.AxesOrigin);

  public final static IBuiltInSymbol AxesStyle = F.initFinalSymbol("AxesStyle", ID.AxesStyle);

  public final static IBuiltInSymbol BSplineFunction =
      F.initFinalSymbol("BSplineFunction", ID.BSplineFunction);

  public final static IBuiltInSymbol Background = F.initFinalSymbol("Background", ID.Background);

  public final static IBuiltInSymbol Ball = F.initFinalSymbol("Ball", ID.Ball);

  /**
   * BarChart(list-of-values, options) - plot a bar chart for a `list-of-values` with option
   * `BarOrigin->Bottom` or `BarOrigin->Bottom`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BarChart.md">BarChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol BarChart = F.initFinalSymbol("BarChart", ID.BarChart);

  public final static IBuiltInSymbol BarOrigin = F.initFinalSymbol("BarOrigin", ID.BarOrigin);

  public final static IBuiltInSymbol BartlettWindow =
      F.initFinalSymbol("BartlettWindow", ID.BartlettWindow);

  /**
   * BaseDecode(string) - decodes a Base64 encoded `string` into a `ByteArray` using the Base64
   * encoding scheme.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseDecode.md">BaseDecode
   *      documentation</a>
   */
  public final static IBuiltInSymbol BaseDecode = F.initFinalSymbol("BaseDecode", ID.BaseDecode);

  /**
   * BaseEncode(byte-array) - encodes the specified `byte-array` into a string using the Base64
   * encoding scheme.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseEncode.md">BaseEncode
   *      documentation</a>
   */
  public final static IBuiltInSymbol BaseEncode = F.initFinalSymbol("BaseEncode", ID.BaseEncode);

  /**
   * BaseForm(integer, radix) - prints the `integer` number in base `radix` form.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseForm.md">BaseForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol BaseForm = F.initFinalSymbol("BaseForm", ID.BaseForm);

  public final static IBuiltInSymbol Beep = F.initFinalSymbol("Beep", ID.Beep);

  /**
   * Begin("<context-name>") - start a new context definition
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Begin.md">Begin
   *      documentation</a>
   */
  public final static IBuiltInSymbol Begin = F.initFinalSymbol("Begin", ID.Begin);

  /**
   * BeginPackage("<context-name>") - start a new package definition
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BeginPackage.md">BeginPackage
   *      documentation</a>
   */
  public final static IBuiltInSymbol BeginPackage =
      F.initFinalSymbol("BeginPackage", ID.BeginPackage);

  public final static IBuiltInSymbol BeginTestSection =
      F.initFinalSymbol("BeginTestSection", ID.BeginTestSection);

  /**
   * BellB(n) - the Bell number function counts the number of different ways to partition a set that
   * has exactly `n` elements
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BellB.md">BellB
   *      documentation</a>
   */
  public final static IBuiltInSymbol BellB = F.initFinalSymbol("BellB", ID.BellB);

  /**
   * BellY(n, k, {x1, x2, ... , xN}) - the second kind of Bell polynomials (incomplete Bell
   * polynomials).
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BellY.md">BellY
   *      documentation</a>
   */
  public final static IBuiltInSymbol BellY = F.initFinalSymbol("BellY", ID.BellY);

  /**
   * BernoulliB(expr) - computes the Bernoulli number of the first kind.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernoulliB.md">BernoulliB
   *      documentation</a>
   */
  public final static IBuiltInSymbol BernoulliB = F.initFinalSymbol("BernoulliB", ID.BernoulliB);

  /**
   * BernoulliDistribution(p) - returns the Bernoulli distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernoulliDistribution.md">BernoulliDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol BernoulliDistribution =
      F.initFinalSymbol("BernoulliDistribution", ID.BernoulliDistribution);

  /**
   * BernsteinBasis(n, v, expr) - computes the Bernstein basis for the expression `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernsteinBasis.md">BernsteinBasis
   *      documentation</a>
   */
  public final static IBuiltInSymbol BernsteinBasis =
      F.initFinalSymbol("BernsteinBasis", ID.BernsteinBasis);

  /**
   * BesselI(n, z) - modified Bessel function of the first kind.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselI.md">BesselI
   *      documentation</a>
   */
  public final static IBuiltInSymbol BesselI = F.initFinalSymbol("BesselI", ID.BesselI);

  /**
   * BesselJ(n, z) - Bessel function of the first kind.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselJ.md">BesselJ
   *      documentation</a>
   */
  public final static IBuiltInSymbol BesselJ = F.initFinalSymbol("BesselJ", ID.BesselJ);

  /**
   * BesselJZero(n, z) - is the `k`th zero of the `BesselJ(n,z)` function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselJZero.md">BesselJZero
   *      documentation</a>
   */
  public final static IBuiltInSymbol BesselJZero = F.initFinalSymbol("BesselJZero", ID.BesselJZero);

  /**
   * BesselK(n, z) - modified Bessel function of the second kind.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselK.md">BesselK
   *      documentation</a>
   */
  public final static IBuiltInSymbol BesselK = F.initFinalSymbol("BesselK", ID.BesselK);

  /**
   * BesselY(n, z) - Bessel function of the second kind.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselY.md">BesselY
   *      documentation</a>
   */
  public final static IBuiltInSymbol BesselY = F.initFinalSymbol("BesselY", ID.BesselY);

  /**
   * BesselYZero(n, z) - is the `k`th zero of the `BesselY(n,z)` function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselYZero.md">BesselYZero
   *      documentation</a>
   */
  public final static IBuiltInSymbol BesselYZero = F.initFinalSymbol("BesselYZero", ID.BesselYZero);

  /**
   * Beta(a, b) - is the beta function of the numbers `a`,`b`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Beta.md">Beta
   *      documentation</a>
   */
  public final static IBuiltInSymbol Beta = F.initFinalSymbol("Beta", ID.Beta);

  public final static IBuiltInSymbol BetaDistribution =
      F.initFinalSymbol("BetaDistribution", ID.BetaDistribution);

  public final static IBuiltInSymbol BetaRegularized =
      F.initFinalSymbol("BetaRegularized", ID.BetaRegularized);

  /**
   * BetweennessCentrality(graph) - Computes the betweenness centrality of each vertex of a `graph`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BetweennessCentrality.md">BetweennessCentrality
   *      documentation</a>
   */
  public final static IBuiltInSymbol BetweennessCentrality =
      F.initFinalSymbol("BetweennessCentrality", ID.BetweennessCentrality);

  public final static IBuiltInSymbol BezierFunction =
      F.initFinalSymbol("BezierFunction", ID.BezierFunction);

  /**
   * BinCounts(list, width-of-bin) - count the number of elements, if `list`, is divided into
   * successive bins with width `width-of-bin`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinCounts.md">BinCounts
   *      documentation</a>
   */
  public final static IBuiltInSymbol BinCounts = F.initFinalSymbol("BinCounts", ID.BinCounts);

  /**
   * BinaryDeserialize(byte-array) - deserialize the `byte-array` from WXF format into a Symja
   * expression.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinaryDeserialize.md">BinaryDeserialize
   *      documentation</a>
   */
  public final static IBuiltInSymbol BinaryDeserialize =
      F.initFinalSymbol("BinaryDeserialize", ID.BinaryDeserialize);

  /**
   * BinaryDistance(u, v) - returns the binary distance between `u` and `v`. `0` if `u` and `v` are
   * unequal. `1` if `u` and `v` are equal.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinaryDistance.md">BinaryDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol BinaryDistance =
      F.initFinalSymbol("BinaryDistance", ID.BinaryDistance);

  public final static IBuiltInSymbol BinaryRead = F.initFinalSymbol("BinaryRead", ID.BinaryRead);

  /**
   * BinarySerialize(expr) - serialize the Symja `expr` into a byte array expression in WXF format.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinarySerialize.md">BinarySerialize
   *      documentation</a>
   */
  public final static IBuiltInSymbol BinarySerialize =
      F.initFinalSymbol("BinarySerialize", ID.BinarySerialize);

  public final static IBuiltInSymbol BinaryWrite = F.initFinalSymbol("BinaryWrite", ID.BinaryWrite);

  /**
   * Binomial(n, k) - returns the binomial coefficient of the 2 integers `n` and `k`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Binomial.md">Binomial
   *      documentation</a>
   */
  public final static IBuiltInSymbol Binomial = F.initFinalSymbol("Binomial", ID.Binomial);

  /**
   * BinomialDistribution(n, p) - returns the binomial distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinomialDistribution.md">BinomialDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol BinomialDistribution =
      F.initFinalSymbol("BinomialDistribution", ID.BinomialDistribution);

  public final static IBuiltInSymbol BioSequence = F.initFinalSymbol("BioSequence", ID.BioSequence);

  public final static IBuiltInSymbol BioSequenceQ =
      F.initFinalSymbol("BioSequenceQ", ID.BioSequenceQ);

  public final static IBuiltInSymbol BioSequenceTranscribe =
      F.initFinalSymbol("BioSequenceTranscribe", ID.BioSequenceTranscribe);

  public final static IBuiltInSymbol BioSequenceTranslate =
      F.initFinalSymbol("BioSequenceTranslate", ID.BioSequenceTranslate);

  /**
   * BitLengthi(x) - gives the number of bits needed to represent the integer `x`. The sign of `x`
   * is ignored.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BitLength.md">BitLength
   *      documentation</a>
   */
  public final static IBuiltInSymbol BitLength = F.initFinalSymbol("BitLength", ID.BitLength);

  public final static IBuiltInSymbol Black = F.initFinalSymbol("Black", ID.Black);

  public final static IBuiltInSymbol BlackmanHarrisWindow =
      F.initFinalSymbol("BlackmanHarrisWindow", ID.BlackmanHarrisWindow);

  public final static IBuiltInSymbol BlackmanNuttallWindow =
      F.initFinalSymbol("BlackmanNuttallWindow", ID.BlackmanNuttallWindow);

  public final static IBuiltInSymbol BlackmanWindow =
      F.initFinalSymbol("BlackmanWindow", ID.BlackmanWindow);

  public final static IBuiltInSymbol Blank = F.initFinalSymbol("Blank", ID.Blank);

  public final static IBuiltInSymbol BlankNullSequence =
      F.initFinalSymbol("BlankNullSequence", ID.BlankNullSequence);

  public final static IBuiltInSymbol BlankSequence =
      F.initFinalSymbol("BlankSequence", ID.BlankSequence);

  /**
   * Block({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Block.md">Block
   *      documentation</a>
   */
  public final static IBuiltInSymbol Block = F.initFinalSymbol("Block", ID.Block);

  public final static IBuiltInSymbol Blue = F.initFinalSymbol("Blue", ID.Blue);

  /**
   * Boole(expr) - returns `1` if `expr` evaluates to `True`; returns `0` if `expr` evaluates to
   * `False`; and gives no result otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Boole.md">Boole
   *      documentation</a>
   */
  public final static IBuiltInSymbol Boole = F.initFinalSymbol("Boole", ID.Boole);

  /**
   * BooleanConvert(logical-expr) - convert the `logical-expr` to [disjunctive normal
   * form](https://en.wikipedia.org/wiki/Disjunctive_normal_form)
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanConvert.md">BooleanConvert
   *      documentation</a>
   */
  public final static IBuiltInSymbol BooleanConvert =
      F.initFinalSymbol("BooleanConvert", ID.BooleanConvert);

  /**
   * BooleanMinimize(expr) - minimizes a boolean function with the [Quine McCluskey
   * algorithm](https://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm)
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanMinimize.md">BooleanMinimize
   *      documentation</a>
   */
  public final static IBuiltInSymbol BooleanMinimize =
      F.initFinalSymbol("BooleanMinimize", ID.BooleanMinimize);

  /**
   * BooleanQ(expr) - returns `True` if `expr` is either `True` or `False`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanQ.md">BooleanQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol BooleanQ = F.initFinalSymbol("BooleanQ", ID.BooleanQ);

  /**
   * BooleanTable(logical-expr, variables) - generate [truth
   * values](https://en.wikipedia.org/wiki/Truth_table) from the `logical-expr`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanTable.md">BooleanTable
   *      documentation</a>
   */
  public final static IBuiltInSymbol BooleanTable =
      F.initFinalSymbol("BooleanTable", ID.BooleanTable);

  /**
   * BooleanVariables(logical-expr) - gives a list of the boolean variables that appear in the
   * `logical-expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanVariables.md">BooleanVariables
   *      documentation</a>
   */
  public final static IBuiltInSymbol BooleanVariables =
      F.initFinalSymbol("BooleanVariables", ID.BooleanVariables);

  /**
   * Booleans - is the set of boolean values.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Booleans.md">Booleans
   *      documentation</a>
   */
  public final static IBuiltInSymbol Booleans = F.initFinalSymbol("Booleans", ID.Booleans);

  public final static IBuiltInSymbol Bottom = F.initFinalSymbol("Bottom", ID.Bottom);

  public final static IBuiltInSymbol BoxRatios = F.initFinalSymbol("BoxRatios", ID.BoxRatios);

  /**
   * BoxWhiskerChart( ) - plot a box whisker chart.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BoxWhiskerChart.md">BoxWhiskerChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol BoxWhiskerChart =
      F.initFinalSymbol("BoxWhiskerChart", ID.BoxWhiskerChart);

  public final static IBuiltInSymbol Boxed = F.initFinalSymbol("Boxed", ID.Boxed);

  /**
   * BrayCurtisDistance(u, v) - returns the Bray Curtis distance between `u` and `v`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BrayCurtisDistance.md">BrayCurtisDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol BrayCurtisDistance =
      F.initFinalSymbol("BrayCurtisDistance", ID.BrayCurtisDistance);

  /**
   * Break() - exits a `For`, `While`, or `Do` loop.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Break.md">Break
   *      documentation</a>
   */
  public final static IBuiltInSymbol Break = F.initFinalSymbol("Break", ID.Break);

  public final static IBuiltInSymbol Brown = F.initFinalSymbol("Brown", ID.Brown);

  public final static IBuiltInSymbol Button = F.initFinalSymbol("Button", ID.Button);

  public final static IBuiltInSymbol Byte = F.initFinalSymbol("Byte", ID.Byte);

  /**
   * ByteArray({list-of-byte-values}) - converts the `list-of-byte-values` into a byte array.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ByteArray.md">ByteArray
   *      documentation</a>
   */
  public final static IBuiltInSymbol ByteArray = F.initFinalSymbol("ByteArray", ID.ByteArray);


  /**
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ByteArrayQ.md">ByteArrayQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ByteArrayQ = F.initFinalSymbol("ByteArrayQ", ID.ByteArrayQ);

  /**
   * ByteArrayToString(byte-array) - decoding the specified `byte-array` using the default character
   * set `UTF-8`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ByteArrayToString.md">ByteArrayToString
   *      documentation</a>
   */
  public final static IBuiltInSymbol ByteArrayToString =
      F.initFinalSymbol("ByteArrayToString", ID.ByteArrayToString);

  public final static IBuiltInSymbol ByteCount = F.initFinalSymbol("ByteCount", ID.ByteCount);

  /**
   * C(n) - represents the `n`-th constant in a solution to a differential equation.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/C.md">C
   *      documentation</a>
   */
  public final static IBuiltInSymbol C = F.initFinalSymbol("C", ID.C);

  /**
   * CDF(distribution, value) - returns the cumulative distribution function of `value`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CDF.md">CDF
   *      documentation</a>
   */
  public final static IBuiltInSymbol CDF = F.initFinalSymbol("CDF", ID.CDF);

  public final static IBuiltInSymbol CForm = F.initFinalSymbol("CForm", ID.CForm);

  public final static IBuiltInSymbol CMYColor = F.initFinalSymbol("CMYColor", ID.CMYColor);

  /**
   * CanberraDistance(u, v) - returns the canberra distance between `u` and `v`, which is a weighted
   * version of the Manhattan distance.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CanberraDistance.md">CanberraDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol CanberraDistance =
      F.initFinalSymbol("CanberraDistance", ID.CanberraDistance);

  /**
   * Cancel(expr) - cancels out common factors in numerators and denominators.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cancel.md">Cancel
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cancel = F.initFinalSymbol("Cancel", ID.Cancel);

  public final static IBuiltInSymbol CancelButton =
      F.initFinalSymbol("CancelButton", ID.CancelButton);

  /**
   * CarlsonRC(x, y) - returns the Carlson RC function..
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRC.md">CarlsonRC
   *      documentation</a>
   */
  public final static IBuiltInSymbol CarlsonRC = F.initFinalSymbol("CarlsonRC", ID.CarlsonRC);

  /**
   * CarlsonRD(x, y, z) - returns the Carlson RD function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRD.md">CarlsonRD
   *      documentation</a>
   */
  public final static IBuiltInSymbol CarlsonRD = F.initFinalSymbol("CarlsonRD", ID.CarlsonRD);

  /**
   * CarlsonRF(x, y, z) - returns the Carlson RF function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRF.md">CarlsonRF
   *      documentation</a>
   */
  public final static IBuiltInSymbol CarlsonRF = F.initFinalSymbol("CarlsonRF", ID.CarlsonRF);

  /**
   * CarlsonRG(x, y, z) - returns the Carlson RG function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRG.md">CarlsonRG
   *      documentation</a>
   */
  public final static IBuiltInSymbol CarlsonRG = F.initFinalSymbol("CarlsonRG", ID.CarlsonRG);

  /**
   * CarlsonRJ(x, y, z, p) - returns the Carlson RJ function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRJ.md">CarlsonRJ
   *      documentation</a>
   */
  public final static IBuiltInSymbol CarlsonRJ = F.initFinalSymbol("CarlsonRJ", ID.CarlsonRJ);

  /**
   * CarmichaelLambda(n) - the Carmichael function of `n`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarmichaelLambda.md">CarmichaelLambda
   *      documentation</a>
   */
  public final static IBuiltInSymbol CarmichaelLambda =
      F.initFinalSymbol("CarmichaelLambda", ID.CarmichaelLambda);

  /**
   * CartesianProduct(list1, list2) - returns the cartesian product for multiple lists.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CartesianProduct.md">CartesianProduct
   *      documentation</a>
   */
  public final static IBuiltInSymbol CartesianProduct =
      F.initFinalSymbol("CartesianProduct", ID.CartesianProduct);

  /**
   * Cases(list, pattern) - returns the elements of `list` that match `pattern`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cases.md">Cases
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cases = F.initFinalSymbol("Cases", ID.Cases);

  /**
   * Catalan - Catalan's constant
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Catalan.md">Catalan
   *      documentation</a>
   */
  public final static IBuiltInSymbol Catalan = F.initFinalSymbol("Catalan", ID.Catalan);

  /**
   * CatalanNumber(n) - returns the catalan number for the argument `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CatalanNumber.md">CatalanNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol CatalanNumber =
      F.initFinalSymbol("CatalanNumber", ID.CatalanNumber);

  /**
   * Catch(expr) - returns the value argument of the first `Throw(value)` generated in the
   * evaluation of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Catch.md">Catch
   *      documentation</a>
   */
  public final static IBuiltInSymbol Catch = F.initFinalSymbol("Catch", ID.Catch);

  /**
   * Catenate({l1, l2, ...}) - concatenates the lists `l1, l2, ...`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Catenate.md">Catenate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Catenate = F.initFinalSymbol("Catenate", ID.Catenate);

  /**
   * CauchyDistribution(a,b) - returns the Cauchy distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CauchyDistribution.md">CauchyDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol CauchyDistribution =
      F.initFinalSymbol("CauchyDistribution", ID.CauchyDistribution);

  /**
   * Ceiling(expr) - gives the first integer greater than or equal `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ceiling.md">Ceiling
   *      documentation</a>
   */
  public final static IBuiltInSymbol Ceiling = F.initFinalSymbol("Ceiling", ID.Ceiling);

  public final static IBuiltInSymbol Center = F.initFinalSymbol("Center", ID.Center);

  public final static IBuiltInSymbol CenterDot = F.initFinalSymbol("CenterDot", ID.CenterDot);

  /**
   * CentralMoment(list, r) - gives the the `r`th central moment (i.e. the `r`th moment about the
   * mean) of `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CentralMoment.md">CentralMoment
   *      documentation</a>
   */
  public final static IBuiltInSymbol CentralMoment =
      F.initFinalSymbol("CentralMoment", ID.CentralMoment);

  public final static IBuiltInSymbol Character = F.initFinalSymbol("Character", ID.Character);

  public final static IBuiltInSymbol CharacterEncoding =
      F.initFinalSymbol("CharacterEncoding", ID.CharacterEncoding);

  /**
   * CharacterRange(min-character, max-character) - computes a list of character strings from
   * `min-character` to `max-character`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CharacterRange.md">CharacterRange
   *      documentation</a>
   */
  public final static IBuiltInSymbol CharacterRange =
      F.initFinalSymbol("CharacterRange", ID.CharacterRange);

  /**
   * CharacteristicPolynomial(matrix, var) - computes the characteristic polynomial of a `matrix`
   * for the variable `var`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CharacteristicPolynomial.md">CharacteristicPolynomial
   *      documentation</a>
   */
  public final static IBuiltInSymbol CharacteristicPolynomial =
      F.initFinalSymbol("CharacteristicPolynomial", ID.CharacteristicPolynomial);

  public final static IBuiltInSymbol Characters = F.initFinalSymbol("Characters", ID.Characters);

  /**
   * ChebyshevT(n, x) - returns the Chebyshev polynomial of the first kind `T_n(x)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChebyshevT.md">ChebyshevT
   *      documentation</a>
   */
  public final static IBuiltInSymbol ChebyshevT = F.initFinalSymbol("ChebyshevT", ID.ChebyshevT);

  /**
   * ChebyshevU(n, x) - returns the Chebyshev polynomial of the second kind `U_n(x)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChebyshevU.md">ChebyshevU
   *      documentation</a>
   */
  public final static IBuiltInSymbol ChebyshevU = F.initFinalSymbol("ChebyshevU", ID.ChebyshevU);

  /**
   * Check(expr, failure) - evaluates `expr`, and returns the result, unless messages were
   * generated, in which case `failure` will be returned.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Check.md">Check
   *      documentation</a>
   */
  public final static IBuiltInSymbol Check = F.initFinalSymbol("Check", ID.Check);

  /**
   * CheckAbort(expr, failure-expr) - evaluates `expr`, and returns the result, unless `Abort` was
   * called during the evaluation, in which case `failure-expr` will be returned.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CheckAbort.md">CheckAbort
   *      documentation</a>
   */
  public final static IBuiltInSymbol CheckAbort = F.initFinalSymbol("CheckAbort", ID.CheckAbort);

  /**
   * ChessboardDistance(u, v) - returns the chessboard distance (also known as Chebyshev distance)
   * between `u` and `v`, which is the number of moves a king on a chessboard needs to get from
   * square `u` to square `v`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChessboardDistance.md">ChessboardDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol ChessboardDistance =
      F.initFinalSymbol("ChessboardDistance", ID.ChessboardDistance);

  public final static IBuiltInSymbol ChiSquareDistribution =
      F.initFinalSymbol("ChiSquareDistribution", ID.ChiSquareDistribution);

  /**
   * ChineseRemainder({a1, a2, a3,...}, {n1, n2, n3,...}) - the chinese remainder function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChineseRemainder.md">ChineseRemainder
   *      documentation</a>
   */
  public final static IBuiltInSymbol ChineseRemainder =
      F.initFinalSymbol("ChineseRemainder", ID.ChineseRemainder);

  /**
   * CholeskyDecomposition(matrix) - calculate the Cholesky decomposition of a hermitian, positive
   * definite square `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CholeskyDecomposition.md">CholeskyDecomposition
   *      documentation</a>
   */
  public final static IBuiltInSymbol CholeskyDecomposition =
      F.initFinalSymbol("CholeskyDecomposition", ID.CholeskyDecomposition);

  /**
   * Chop(numerical-expr) - replaces numerical values in the `numerical-expr` which are close to
   * zero with symbolic value `0`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Chop.md">Chop
   *      documentation</a>
   */
  public final static IBuiltInSymbol Chop = F.initFinalSymbol("Chop", ID.Chop);

  public final static IBuiltInSymbol Circle = F.initFinalSymbol("Circle", ID.Circle);

  public final static IBuiltInSymbol CircleDot = F.initFinalSymbol("CircleDot", ID.CircleDot);

  /**
   * CirclePoints(i) - gives the `i` points on the unit circle for a positive integer `i`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CirclePoints.md">CirclePoints
   *      documentation</a>
   */
  public final static IBuiltInSymbol CirclePoints =
      F.initFinalSymbol("CirclePoints", ID.CirclePoints);

  public final static IBuiltInSymbol CircleTimes = F.initFinalSymbol("CircleTimes", ID.CircleTimes);

  /**
   * Clear(symbol1, symbol2,...) - clears all values of the given symbols.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Clear.md">Clear
   *      documentation</a>
   */
  public final static IBuiltInSymbol Clear = F.initFinalSymbol("Clear", ID.Clear);

  /**
   * ClearAll(symbol1, symbol2,...) - clears all values and attributes associated with the given
   * symbols.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClearAll.md">ClearAll
   *      documentation</a>
   */
  public final static IBuiltInSymbol ClearAll = F.initFinalSymbol("ClearAll", ID.ClearAll);

  /**
   * ClearAttributes(symbol, attrib) - removes `attrib` from `symbol`'s attributes.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClearAttributes.md">ClearAttributes
   *      documentation</a>
   */
  public final static IBuiltInSymbol ClearAttributes =
      F.initFinalSymbol("ClearAttributes", ID.ClearAttributes);

  /**
   * Clip(expr) - returns `expr` in the range `-1` to `1`. Returns `-1` if `expr` is less than `-1`.
   * Returns `1` if `expr` is greater than `1`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Clip.md">Clip
   *      documentation</a>
   */
  public final static IBuiltInSymbol Clip = F.initFinalSymbol("Clip", ID.Clip);

  /**
   * Close(stream) - closes an input or output `stream`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Close.md">Close
   *      documentation</a>
   */
  public final static IBuiltInSymbol Close = F.initFinalSymbol("Close", ID.Close);

  /**
   * ClosenessCentrality(graph) - Computes the closeness centrality of each vertex of a `graph`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClosenessCentrality.md">ClosenessCentrality
   *      documentation</a>
   */
  public final static IBuiltInSymbol ClosenessCentrality =
      F.initFinalSymbol("ClosenessCentrality", ID.ClosenessCentrality);

  /**
   * Coefficient(polynomial, variable, exponent) - get the coefficient of `variable^exponent` in
   * `polynomial`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Coefficient.md">Coefficient
   *      documentation</a>
   */
  public final static IBuiltInSymbol Coefficient = F.initFinalSymbol("Coefficient", ID.Coefficient);

  public final static IBuiltInSymbol CoefficientArrays =
      F.initFinalSymbol("CoefficientArrays", ID.CoefficientArrays);

  /**
   * CoefficientList(polynomial, variable) - get the coefficient list of a `polynomial`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoefficientList.md">CoefficientList
   *      documentation</a>
   */
  public final static IBuiltInSymbol CoefficientList =
      F.initFinalSymbol("CoefficientList", ID.CoefficientList);

  /**
   * CoefficientRules(polynomial, list-of-variables) - get the list of coefficient rules of a
   * `polynomial`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoefficientRules.md">CoefficientRules
   *      documentation</a>
   */
  public final static IBuiltInSymbol CoefficientRules =
      F.initFinalSymbol("CoefficientRules", ID.CoefficientRules);

  public final static IBuiltInSymbol Cofactor = F.initFinalSymbol("Cofactor", ID.Cofactor);

  /**
   * Collect(expr, variable) - collect subexpressions in `expr` which belong to the same `variable`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Collect.md">Collect
   *      documentation</a>
   */
  public final static IBuiltInSymbol Collect = F.initFinalSymbol("Collect", ID.Collect);

  /**
   * CollinearPoints({{x1,y1},{x2,y2},{a,b},...}) - returns true if the point `{a,b]` is on the line
   * defined by the first two points `{x1,y1},{x2,y2}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CollinearPoints.md">CollinearPoints
   *      documentation</a>
   */
  public final static IBuiltInSymbol CollinearPoints =
      F.initFinalSymbol("CollinearPoints", ID.CollinearPoints);

  public final static IBuiltInSymbol Colon = F.initFinalSymbol("Colon", ID.Colon);

  public final static IBuiltInSymbol ColorData = F.initFinalSymbol("ColorData", ID.ColorData);

  public final static IBuiltInSymbol ColorFunction =
      F.initFinalSymbol("ColorFunction", ID.ColorFunction);

  public final static IBuiltInSymbol Column = F.initFinalSymbol("Column", ID.Column);

  /**
   * Commonest(data-values-list) - the mode of a list of data values is the value that appears most
   * often.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Commonest.md">Commonest
   *      documentation</a>
   */
  public final static IBuiltInSymbol Commonest = F.initFinalSymbol("Commonest", ID.Commonest);

  public final static IBuiltInSymbol CompatibleUnitQ =
      F.initFinalSymbol("CompatibleUnitQ", ID.CompatibleUnitQ);

  /**
   * Compile(list-of-arguments}, expression) - compile the `expression` into a Java function, which
   * has the arguments defined in `list-of-arguments` and return the compiled result in an
   * `CompiledFunction` expression.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Compile.md">Compile
   *      documentation</a>
   */
  public final static IBuiltInSymbol Compile = F.initFinalSymbol("Compile", ID.Compile);

  /**
   * CompilePrint(list-of-arguments}, expression) - compile the `expression` into a Java function
   * and return the corresponding Java source code function, which has the arguments defined in
   * `list-of-arguments`n. You have to run Symja from a Java Development Kit (JDK) to compile to
   * Java binary code.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompilePrint.md">CompilePrint
   *      documentation</a>
   */
  public final static IBuiltInSymbol CompilePrint =
      F.initFinalSymbol("CompilePrint", ID.CompilePrint);

  /**
   * CompiledFunction(...) - represents a binary Java coded function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompiledFunction.md">CompiledFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol CompiledFunction =
      F.initFinalSymbol("CompiledFunction", ID.CompiledFunction);

  /**
   * Complement(set1, set2) - get the complement set from `set1` and `set2`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complement.md">Complement
   *      documentation</a>
   */
  public final static IBuiltInSymbol Complement = F.initFinalSymbol("Complement", ID.Complement);

  /**
   * CompleteGraph(order) - create a new complete graph with `order` number of total vertices.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompleteGraph.md">CompleteGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol CompleteGraph =
      F.initFinalSymbol("CompleteGraph", ID.CompleteGraph);

  /**
   * Complex - is the head of complex numbers.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complex.md">Complex
   *      documentation</a>
   */
  public final static IBuiltInSymbol Complex = F.initFinalSymbol("Complex", ID.Complex);

  /**
   * ComplexExpand(expr) - get the expanded `expr`. All variable symbols in `expr` are assumed to be
   * non complex numbers.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexExpand.md">ComplexExpand
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComplexExpand =
      F.initFinalSymbol("ComplexExpand", ID.ComplexExpand);

  /**
   * ComplexInfinity - represents an infinite complex quantity of undetermined direction.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexInfinity.md">ComplexInfinity
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComplexInfinity =
      F.initFinalSymbol("ComplexInfinity", ID.ComplexInfinity);

  /**
   * ComplexPlot3D(expr, {z, min, max ) - create a 3D plot of `expr` for the complex variable `z` in
   * the range `{ Re(min),Re(max) }` to `{ Im(min),Im(max) }`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexPlot3D.md">ComplexPlot3D
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComplexPlot3D =
      F.initFinalSymbol("ComplexPlot3D", ID.ComplexPlot3D);

  /**
   * Complexes - is the set of complex numbers.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complexes.md">Complexes
   *      documentation</a>
   */
  public final static IBuiltInSymbol Complexes = F.initFinalSymbol("Complexes", ID.Complexes);

  public final static IBuiltInSymbol ComplexityFunction =
      F.initFinalSymbol("ComplexityFunction", ID.ComplexityFunction);

  /**
   * ComposeList(list-of-symbols, variable) - creates a list of compositions of the symbols applied
   * at the argument `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComposeList.md">ComposeList
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComposeList = F.initFinalSymbol("ComposeList", ID.ComposeList);

  /**
   * ComposeSeries( series1, series2 ) - substitute `series2` into `series1`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComposeSeries.md">ComposeSeries
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComposeSeries =
      F.initFinalSymbol("ComposeSeries", ID.ComposeSeries);

  /**
   * Composition(sym1, sym2,...)[arg1, arg2,...] - creates a composition of the symbols applied at
   * the arguments.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Composition.md">Composition
   *      documentation</a>
   */
  public final static IBuiltInSymbol Composition = F.initFinalSymbol("Composition", ID.Composition);

  /**
   * CompoundExpression(expr1, expr2, ...) - evaluates its arguments in turn, returning the last
   * result.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompoundExpression.md">CompoundExpression
   *      documentation</a>
   */
  public final static IBuiltInSymbol CompoundExpression =
      F.initFinalSymbol("CompoundExpression", ID.CompoundExpression);

  /**
   * Condition(pattern, expr) - places an additional constraint on `pattern` that only allows it to
   * match if `expr` evaluates to `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Condition.md">Condition
   *      documentation</a>
   */
  public final static IBuiltInSymbol Condition = F.initFinalSymbol("Condition", ID.Condition);

  /**
   * ConditionalExpression(expr, condition) - if `condition` evaluates to `True` return `expr`, if
   * `condition` evaluates to `False` return `Undefined`. Otherwise return the
   * `ConditionalExpression` unevaluated.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConditionalExpression.md">ConditionalExpression
   *      documentation</a>
   */
  public final static IBuiltInSymbol ConditionalExpression =
      F.initFinalSymbol("ConditionalExpression", ID.ConditionalExpression);

  public final static IBuiltInSymbol Cone = F.initFinalSymbol("Cone", ID.Cone);

  /**
   * Conjugate(z) - returns the complex conjugate of the complex number `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Conjugate.md">Conjugate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Conjugate = F.initFinalSymbol("Conjugate", ID.Conjugate);

  /**
   * ConjugateTranspose(matrix) - get the transposed `matrix` with conjugated matrix elements.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConjugateTranspose.md">ConjugateTranspose
   *      documentation</a>
   */
  public final static IBuiltInSymbol ConjugateTranspose =
      F.initFinalSymbol("ConjugateTranspose", ID.ConjugateTranspose);

  public final static IBuiltInSymbol ConnectedGraphQ =
      F.initFinalSymbol("ConnectedGraphQ", ID.ConnectedGraphQ);

  /**
   * Constant - is an attribute that indicates that a symbol is a constant.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Constant.md">Constant
   *      documentation</a>
   */
  public final static IBuiltInSymbol Constant = F.initFinalSymbol("Constant", ID.Constant);

  /**
   * ConstantArray(expr, n) - returns a list of `n` copies of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConstantArray.md">ConstantArray
   *      documentation</a>
   */
  public final static IBuiltInSymbol ConstantArray =
      F.initFinalSymbol("ConstantArray", ID.ConstantArray);

  public final static IBuiltInSymbol ContainsAll = F.initFinalSymbol("ContainsAll", ID.ContainsAll);

  public final static IBuiltInSymbol ContainsAny = F.initFinalSymbol("ContainsAny", ID.ContainsAny);

  public final static IBuiltInSymbol ContainsExactly =
      F.initFinalSymbol("ContainsExactly", ID.ContainsExactly);

  public final static IBuiltInSymbol ContainsNone =
      F.initFinalSymbol("ContainsNone", ID.ContainsNone);

  /**
   * ContainsOnly(list1, list2) - yields True if `list1` contains only elements that appear in
   * `list2`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContainsOnly.md">ContainsOnly
   *      documentation</a>
   */
  public final static IBuiltInSymbol ContainsOnly =
      F.initFinalSymbol("ContainsOnly", ID.ContainsOnly);

  /**
   * Context(symbol) - return the context of the given symbol.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Context.md">Context
   *      documentation</a>
   */
  public final static IBuiltInSymbol Context = F.initFinalSymbol("Context", ID.Context);

  /**
   * Continue() - continues with the next iteration in a `For`, `While`, or `Do` loop.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Continue.md">Continue
   *      documentation</a>
   */
  public final static IBuiltInSymbol Continue = F.initFinalSymbol("Continue", ID.Continue);

  /**
   * ContinuedFraction(number) - the complete continued fraction representation for a rational or
   * quadradic irrational `number`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContinuedFraction.md">ContinuedFraction
   *      documentation</a>
   */
  public final static IBuiltInSymbol ContinuedFraction =
      F.initFinalSymbol("ContinuedFraction", ID.ContinuedFraction);

  public final static IBuiltInSymbol ContourPlot = F.initFinalSymbol("ContourPlot", ID.ContourPlot);

  /**
   * Convergents({n1, n2, ...}) - return the list of convergents which represents the continued
   * fraction list `{n1, n2, ...}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Convergents.md">Convergents
   *      documentation</a>
   */
  public final static IBuiltInSymbol Convergents = F.initFinalSymbol("Convergents", ID.Convergents);

  public final static IBuiltInSymbol ConvexHullMesh =
      F.initFinalSymbol("ConvexHullMesh", ID.ConvexHullMesh);

  /**
   * CoplanarPoints({{x1,y1,z1},{x2,y2,z2},{x3,y3,z3},{a,b,c},...}) - returns true if the point
   * `{a,b,c]` is on the plane defined by the first three points `{x1,y1,z1},{x2,y2,z2},{x3,y3,z3}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoplanarPoints.md">CoplanarPoints
   *      documentation</a>
   */
  public final static IBuiltInSymbol CoplanarPoints =
      F.initFinalSymbol("CoplanarPoints", ID.CoplanarPoints);

  /**
   * CoprimeQ(x, y) - tests whether `x` and `y` are coprime by computing their greatest common
   * divisor.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoprimeQ.md">CoprimeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol CoprimeQ = F.initFinalSymbol("CoprimeQ", ID.CoprimeQ);

  /**
   * Correlation(a, b) - computes Pearson's correlation of two equal-sized vectors `a` and `b`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Correlation.md">Correlation
   *      documentation</a>
   */
  public final static IBuiltInSymbol Correlation = F.initFinalSymbol("Correlation", ID.Correlation);

  /**
   * Cos(expr) - returns the cosine of `expr` (measured in radians). `Cos(expr)` will evaluate
   * automatically in the case `expr` is a multiple of `Pi, Pi/2, Pi/3, Pi/4` and `Pi/6`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cos.md">Cos
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cos = F.initFinalSymbol("Cos", ID.Cos);

  /**
   * CosIntegral(expr) - returns the cosine integral of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CosIntegral.md">CosIntegral
   *      documentation</a>
   */
  public final static IBuiltInSymbol CosIntegral = F.initFinalSymbol("CosIntegral", ID.CosIntegral);

  /**
   * Cosh(z) - returns the hyperbolic cosine of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cosh.md">Cosh
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cosh = F.initFinalSymbol("Cosh", ID.Cosh);

  /**
   * CoshIntegral(expr) - returns the hyperbolic cosine integral of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoshIntegral.md">CoshIntegral
   *      documentation</a>
   */
  public final static IBuiltInSymbol CoshIntegral =
      F.initFinalSymbol("CoshIntegral", ID.CoshIntegral);

  /**
   * CosineDistance(u, v) - returns the cosine distance between `u` and `v`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CosineDistance.md">CosineDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol CosineDistance =
      F.initFinalSymbol("CosineDistance", ID.CosineDistance);

  /**
   * Cot(expr) - the cotangent function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cot.md">Cot
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cot = F.initFinalSymbol("Cot", ID.Cot);

  /**
   * Coth(z) - returns the hyperbolic cotangent of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Coth.md">Coth
   *      documentation</a>
   */
  public final static IBuiltInSymbol Coth = F.initFinalSymbol("Coth", ID.Coth);

  /**
   * Count(list, pattern) - returns the number of times `pattern` appears in `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Count.md">Count
   *      documentation</a>
   */
  public final static IBuiltInSymbol Count = F.initFinalSymbol("Count", ID.Count);

  /**
   * CountDistinct(list) - returns the number of distinct entries in `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CountDistinct.md">CountDistinct
   *      documentation</a>
   */
  public final static IBuiltInSymbol CountDistinct =
      F.initFinalSymbol("CountDistinct", ID.CountDistinct);

  /**
   * Counts({elem1, elem2, elem3, ...}) - count the number of each distinct element in the list
   * `{elem1, elem2, elem3, ...}` and return the result as an association `<|elem1->counter1,
   * ...|>`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Counts.md">Counts
   *      documentation</a>
   */
  public final static IBuiltInSymbol Counts = F.initFinalSymbol("Counts", ID.Counts);

  /**
   * Covariance(a, b) - computes the covariance between the equal-sized vectors `a` and `b`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Covariance.md">Covariance
   *      documentation</a>
   */
  public final static IBuiltInSymbol Covariance = F.initFinalSymbol("Covariance", ID.Covariance);

  public final static IBuiltInSymbol CreateDirectory =
      F.initFinalSymbol("CreateDirectory", ID.CreateDirectory);

  public final static IBuiltInSymbol CreateFile = F.initFinalSymbol("CreateFile", ID.CreateFile);

  /**
   * Cross(a, b) - computes the vector cross product of `a` and `b`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cross.md">Cross
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cross = F.initFinalSymbol("Cross", ID.Cross);

  /**
   * Csc(z) - returns the cosecant of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Csc.md">Csc
   *      documentation</a>
   */
  public final static IBuiltInSymbol Csc = F.initFinalSymbol("Csc", ID.Csc);

  /**
   * Csch(z) - returns the hyperbolic cosecant of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Csch.md">Csch
   *      documentation</a>
   */
  public final static IBuiltInSymbol Csch = F.initFinalSymbol("Csch", ID.Csch);

  /**
   * CubeRoot(n) - finds the real-valued cube root of the given `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CubeRoot.md">CubeRoot
   *      documentation</a>
   */
  public final static IBuiltInSymbol CubeRoot = F.initFinalSymbol("CubeRoot", ID.CubeRoot);

  /**
   * Cuboid({xmin, ymin, zmin}) - is a unit cube.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cuboid.md">Cuboid
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cuboid = F.initFinalSymbol("Cuboid", ID.Cuboid);

  /**
   * Curl({f1, f2}, {x1, x2}) - gives the curl.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Curl.md">Curl
   *      documentation</a>
   */
  public final static IBuiltInSymbol Curl = F.initFinalSymbol("Curl", ID.Curl);

  public final static IBuiltInSymbol Cyan = F.initFinalSymbol("Cyan", ID.Cyan);

  public final static IBuiltInSymbol CycleGraph = F.initFinalSymbol("CycleGraph", ID.CycleGraph);

  /**
   * Cycles(a, b) - expression for defining canonical cycles of a permutation.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cycles.md">Cycles
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cycles = F.initFinalSymbol("Cycles", ID.Cycles);

  /**
   * Cyclotomic(n, x) - returns the Cyclotomic polynomial `C_n(x)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cyclotomic.md">Cyclotomic
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cyclotomic = F.initFinalSymbol("Cyclotomic", ID.Cyclotomic);

  /**
   * Cylinder({{x1, y1, z1}, {x2, y2, z2}}) - represents a cylinder of radius `1`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cylinder.md">Cylinder
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cylinder = F.initFinalSymbol("Cylinder", ID.Cylinder);

  /**
   * D(f, x) - gives the partial derivative of `f` with respect to `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/D.md">D
   *      documentation</a>
   */
  public final static IBuiltInSymbol D = F.initFinalSymbol("D", ID.D);

  /**
   * DSolve(equation, f(var), var) - attempts to solve a linear differential `equation` for the
   * function `f(var)` and variable `var`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DSolve.md">DSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol DSolve = F.initFinalSymbol("DSolve", ID.DSolve);

  public final static IBuiltInSymbol DataRange = F.initFinalSymbol("DataRange", ID.DataRange);

  /**
   * - create a `Dataset` object from the `association`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dataset.md">Dataset
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dataset = F.initFinalSymbol("Dataset", ID.Dataset);

  public final static IBuiltInSymbol DateObject = F.initFinalSymbol("DateObject", ID.DateObject);

  public final static IBuiltInSymbol DateString = F.initFinalSymbol("DateString", ID.DateString);

  public final static IBuiltInSymbol DateValue = F.initFinalSymbol("DateValue", ID.DateValue);

  /**
   * Decrement(x) - decrements `x` by `1`, returning the original value of `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Decrement.md">Decrement
   *      documentation</a>
   */
  public final static IBuiltInSymbol Decrement = F.initFinalSymbol("Decrement", ID.Decrement);

  /**
   * Default(symbol) - `Default` returns the default value associated with the `symbol` for a
   * pattern default `_.` expression.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Default.md">Default
   *      documentation</a>
   */
  public final static IBuiltInSymbol Default = F.initFinalSymbol("Default", ID.Default);

  public final static IBuiltInSymbol DefaultButton =
      F.initFinalSymbol("DefaultButton", ID.DefaultButton);

  public final static IBuiltInSymbol DefaultValue =
      F.initFinalSymbol("DefaultValue", ID.DefaultValue);

  /**
   * Defer(expr) - `Defer` doesn't evaluate `expr` and didn't appear in the output
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Defer.md">Defer
   *      documentation</a>
   */
  public final static IBuiltInSymbol Defer = F.initFinalSymbol("Defer", ID.Defer);

  /**
   * Definition(symbol) - prints user-defined values and rules associated with `symbol`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Definition.md">Definition
   *      documentation</a>
   */
  public final static IBuiltInSymbol Definition = F.initFinalSymbol("Definition", ID.Definition);

  /**
   * Degree - the constant `Degree` converts angles from degree to `Pi/180` radians.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Degree.md">Degree
   *      documentation</a>
   */
  public final static IBuiltInSymbol Degree = F.initFinalSymbol("Degree", ID.Degree);

  public final static IBuiltInSymbol DegreeLexicographic =
      F.initFinalSymbol("DegreeLexicographic", ID.DegreeLexicographic);

  public final static IBuiltInSymbol DegreeReverseLexicographic =
      F.initFinalSymbol("DegreeReverseLexicographic", ID.DegreeReverseLexicographic);

  /**
   * Delete(expr, n) - returns `expr` with part `n` removed.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Delete.md">Delete
   *      documentation</a>
   */
  public final static IBuiltInSymbol Delete = F.initFinalSymbol("Delete", ID.Delete);

  /**
   * DeleteCases(list, pattern) - returns the elements of `list` that do not match `pattern`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteCases.md">DeleteCases
   *      documentation</a>
   */
  public final static IBuiltInSymbol DeleteCases = F.initFinalSymbol("DeleteCases", ID.DeleteCases);

  /**
   * DeleteDuplicates(list) - deletes duplicates from `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteDuplicates.md">DeleteDuplicates
   *      documentation</a>
   */
  public final static IBuiltInSymbol DeleteDuplicates =
      F.initFinalSymbol("DeleteDuplicates", ID.DeleteDuplicates);

  /**
   * DeleteDuplicatesBy(list, predicate) - deletes duplicates from `list`, for which the `predicate`
   * returns `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteDuplicatesBy.md">DeleteDuplicatesBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol DeleteDuplicatesBy =
      F.initFinalSymbol("DeleteDuplicatesBy", ID.DeleteDuplicatesBy);

  /**
   * Denominator(expr) - gives the denominator in `expr`. Denominator collects expressions with
   * negative exponents.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Denominator.md">Denominator
   *      documentation</a>
   */
  public final static IBuiltInSymbol Denominator = F.initFinalSymbol("Denominator", ID.Denominator);

  /**
   * DensityHistogram( list-of-pair-values ) - plot a density histogram for a `list-of-pair-values`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DensityHistogram.md">DensityHistogram
   *      documentation</a>
   */
  public final static IBuiltInSymbol DensityHistogram =
      F.initFinalSymbol("DensityHistogram", ID.DensityHistogram);

  public final static IBuiltInSymbol DensityPlot = F.initFinalSymbol("DensityPlot", ID.DensityPlot);

  /**
   * Depth(expr) - gives the depth of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Depth.md">Depth
   *      documentation</a>
   */
  public final static IBuiltInSymbol Depth = F.initFinalSymbol("Depth", ID.Depth);

  /**
   * Derivative(n)[f] - represents the `n`-th derivative of the function `f`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Derivative.md">Derivative
   *      documentation</a>
   */
  public final static IBuiltInSymbol Derivative = F.initFinalSymbol("Derivative", ID.Derivative);

  /**
   * DesignMatrix(m, f, x) - returns the design matrix.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DesignMatrix.md">DesignMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol DesignMatrix =
      F.initFinalSymbol("DesignMatrix", ID.DesignMatrix);

  /**
   * Det(matrix) - computes the determinant of the `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Det.md">Det
   *      documentation</a>
   */
  public final static IBuiltInSymbol Det = F.initFinalSymbol("Det", ID.Det);

  /**
   * Diagonal(matrix) - computes the diagonal vector of the `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Diagonal.md">Diagonal
   *      documentation</a>
   */
  public final static IBuiltInSymbol Diagonal = F.initFinalSymbol("Diagonal", ID.Diagonal);

  /**
   * DiagonalMatrix(list) - gives a matrix with the values in `list` on its diagonal and zeroes
   * elsewhere.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiagonalMatrix.md">DiagonalMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiagonalMatrix =
      F.initFinalSymbol("DiagonalMatrix", ID.DiagonalMatrix);

  public final static IBuiltInSymbol DiagonalMatrixQ =
      F.initFinalSymbol("DiagonalMatrixQ", ID.DiagonalMatrixQ);


  /**
   * DialogInput() - if the file system is enabled, the user can input a string in a dialog box.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DialogInput.md">DialogInput
   *      documentation</a>
   */
  public final static IBuiltInSymbol DialogInput = F.initFinalSymbol("DialogInput", ID.DialogInput);

  public final static IBuiltInSymbol DialogNotebook =
      F.initFinalSymbol("DialogNotebook", ID.DialogNotebook);

  public final static IBuiltInSymbol DialogReturn =
      F.initFinalSymbol("DialogReturn", ID.DialogReturn);

  /**
   * DiceDissimilarity(u, v) - returns the Dice dissimilarity between the two boolean 1-D lists `u`
   * and `v`, which is defined as `(c_tf + c_ft) / (2 * c_tt + c_ft + c_tf)`, where n is `len(u)`
   * and `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiceDissimilarity.md">DiceDissimilarity
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiceDissimilarity =
      F.initFinalSymbol("DiceDissimilarity", ID.DiceDissimilarity);

  /**
   * DifferenceDelta(f(x), x) - generates a forward difference `f(x+1) - f(x)`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DifferenceDelta.md">DifferenceDelta
   *      documentation</a>
   */
  public final static IBuiltInSymbol DifferenceDelta =
      F.initFinalSymbol("DifferenceDelta", ID.DifferenceDelta);

  public final static IBuiltInSymbol Differences = F.initFinalSymbol("Differences", ID.Differences);

  /**
   * DigitCharacter - represents the digits 0-9.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitCharacter.md">DigitCharacter
   *      documentation</a>
   */
  public final static IBuiltInSymbol DigitCharacter =
      F.initFinalSymbol("DigitCharacter", ID.DigitCharacter);

  /**
   * DigitCount(n) - returns a list of the number of integer digits for `n` for `radix` 10.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitCount.md">DigitCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol DigitCount = F.initFinalSymbol("DigitCount", ID.DigitCount);

  /**
   * DigitQ(str) - returns `True` if `str` is a string which contains only digits.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitQ.md">DigitQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol DigitQ = F.initFinalSymbol("DigitQ", ID.DigitQ);

  /**
   * Dimensions(expr) - returns a list of the dimensions of the expression `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dimensions.md">Dimensions
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dimensions = F.initFinalSymbol("Dimensions", ID.Dimensions);

  /**
   * DiracDelta(x) - `DiracDelta` function returns `0` for all real numbers `x` where `x != 0`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiracDelta.md">DiracDelta
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiracDelta = F.initFinalSymbol("DiracDelta", ID.DiracDelta);

  /**
   * DirectedEdge(a, b) - is a directed edge from vertex `a` to vertex `b` in a `graph` object.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DirectedEdge.md">DirectedEdge
   *      documentation</a>
   */
  public final static IBuiltInSymbol DirectedEdge =
      F.initFinalSymbol("DirectedEdge", ID.DirectedEdge);

  /**
   * DirectedInfinity(z) - represents an infinite multiple of the complex number `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DirectedInfinity.md">DirectedInfinity
   *      documentation</a>
   */
  public final static IBuiltInSymbol DirectedInfinity =
      F.initFinalSymbol("DirectedInfinity", ID.DirectedInfinity);

  public final static IBuiltInSymbol Direction = F.initFinalSymbol("Direction", ID.Direction);

  public final static IBuiltInSymbol DirectionalLight =
      F.initFinalSymbol("DirectionalLight", ID.DirectionalLight);

  public final static IBuiltInSymbol Directive = F.initFinalSymbol("Directive", ID.Directive);

  public final static IBuiltInSymbol DirichletEta =
      F.initFinalSymbol("DirichletEta", ID.DirichletEta);

  public final static IBuiltInSymbol DirichletWindow =
      F.initFinalSymbol("DirichletWindow", ID.DirichletWindow);

  /**
   * DiscreteDelta(n1, n2, n3, ...) - `DiscreteDelta` function returns `1` if all the `ni` are `0`.
   * Returns `0` otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscreteDelta.md">DiscreteDelta
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiscreteDelta =
      F.initFinalSymbol("DiscreteDelta", ID.DiscreteDelta);

  /**
   * DiscreteUniformDistribution({min, max}) - returns a discrete uniform distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscreteUniformDistribution.md">DiscreteUniformDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiscreteUniformDistribution =
      F.initFinalSymbol("DiscreteUniformDistribution", ID.DiscreteUniformDistribution);

  /**
   * Discriminant(poly, var) - computes the discriminant of the polynomial `poly` with respect to
   * the variable `var`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Discriminant.md">Discriminant
   *      documentation</a>
   */
  public final static IBuiltInSymbol Discriminant =
      F.initFinalSymbol("Discriminant", ID.Discriminant);

  public final static IBuiltInSymbol DisjointQ = F.initFinalSymbol("DisjointQ", ID.DisjointQ);

  public final static IBuiltInSymbol Disk = F.initFinalSymbol("Disk", ID.Disk);

  /**
   * Dispatch({rule1, rule2, ...}) - create a dispatch map for a list of rules.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dispatch.md">Dispatch
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dispatch = F.initFinalSymbol("Dispatch", ID.Dispatch);

  public final static IBuiltInSymbol DisplayForm = F.initFinalSymbol("DisplayForm", ID.DisplayForm);

  public final static IBuiltInSymbol Disputed = F.initFinalSymbol("Disputed", ID.Disputed);

  public final static IBuiltInSymbol DisrectedEdges =
      F.initFinalSymbol("DisrectedEdges", ID.DisrectedEdges);

  public final static IBuiltInSymbol DistanceFunction =
      F.initFinalSymbol("DistanceFunction", ID.DistanceFunction);

  /**
   * Distribute(f(x1, x2, x3,...)) - distributes `f` over `Plus` appearing in any of the `xi`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Distribute.md">Distribute
   *      documentation</a>
   */
  public final static IBuiltInSymbol Distribute = F.initFinalSymbol("Distribute", ID.Distribute);

  public final static IBuiltInSymbol Distributed = F.initFinalSymbol("Distributed", ID.Distributed);

  /**
   * Div({f1, f2, f3,...},{x1, x2, x3,...}) - compute the divergence.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Div.md">Div
   *      documentation</a>
   */
  public final static IBuiltInSymbol Div = F.initFinalSymbol("Div", ID.Div);

  /**
   * Divide(a, b) - represents the division of `a` by `b`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divide.md">Divide
   *      documentation</a>
   */
  public final static IBuiltInSymbol Divide = F.initFinalSymbol("Divide", ID.Divide);

  /**
   * DivideBy(x, dx) - is equivalent to `x = x / dx`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivideBy.md">DivideBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol DivideBy = F.initFinalSymbol("DivideBy", ID.DivideBy);

  /**
   * DivideSides(compare-expr, value) - divides all elements of the `compare-expr` by `value`.
   * `compare-expr` can be `True`, `False` or a comparison expression with head `Equal, Unequal,
   * Less, LessEqual, Greater, GreaterEqual`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivideSides.md">DivideSides
   *      documentation</a>
   */
  public final static IBuiltInSymbol DivideSides = F.initFinalSymbol("DivideSides", ID.DivideSides);

  /**
   * Divisible(n, m) - returns `True` if `n` could be divide by `m`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divisible.md">Divisible
   *      documentation</a>
   */
  public final static IBuiltInSymbol Divisible = F.initFinalSymbol("Divisible", ID.Divisible);

  /**
   * DivisorSigma(k, n) - returns the sum of the `k`-th powers of the divisors of `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivisorSigma.md">DivisorSigma
   *      documentation</a>
   */
  public final static IBuiltInSymbol DivisorSigma =
      F.initFinalSymbol("DivisorSigma", ID.DivisorSigma);

  /**
   * DivisorSum(n, head) - returns the sum of the divisors of `n`. The `head` is applied to each
   * divisor.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivisorSum.md">DivisorSum
   *      documentation</a>
   */
  public final static IBuiltInSymbol DivisorSum = F.initFinalSymbol("DivisorSum", ID.DivisorSum);

  /**
   * Divisors(n) - returns all integers that divide the integer `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divisors.md">Divisors
   *      documentation</a>
   */
  public final static IBuiltInSymbol Divisors = F.initFinalSymbol("Divisors", ID.Divisors);

  /**
   * Do(expr, {max}) - evaluates `expr` `max` times.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Do.md">Do
   *      documentation</a>
   */
  public final static IBuiltInSymbol Do = F.initFinalSymbol("Do", ID.Do);

  public final static IBuiltInSymbol Dodecahedron =
      F.initFinalSymbol("Dodecahedron", ID.Dodecahedron);

  /**
   * Dot(x, y) or x . y - `x . y` computes the vector dot product or matrix product `x . y`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dot.md">Dot
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dot = F.initFinalSymbol("Dot", ID.Dot);

  /**
   * DownValues(symbol) - prints the down-value rules associated with `symbol`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownValues.md">DownValues
   *      documentation</a>
   */
  public final static IBuiltInSymbol DownValues = F.initFinalSymbol("DownValues", ID.DownValues);

  /**
   * Drop(expr, n) - returns `expr` with the first `n` leaves removed.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Drop.md">Drop
   *      documentation</a>
   */
  public final static IBuiltInSymbol Drop = F.initFinalSymbol("Drop", ID.Drop);

  public final static IBuiltInSymbol DuplicateFreeQ =
      F.initFinalSymbol("DuplicateFreeQ", ID.DuplicateFreeQ);

  public final static IBuiltInSymbol Dynamic = F.initFinalSymbol("Dynamic", ID.Dynamic);

  /**
   * E - Euler's constant E
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/E.md">E
   *      documentation</a>
   */
  public final static IBuiltInSymbol E = F.initFinalSymbol("E", ID.E);

  public final static IBuiltInSymbol EasterSunday =
      F.initFinalSymbol("EasterSunday", ID.EasterSunday);

  /**
   * Echo(expr) - prints the `expr` to the default output stream and returns `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Echo.md">Echo
   *      documentation</a>
   */
  public final static IBuiltInSymbol Echo = F.initFinalSymbol("Echo", ID.Echo);

  /**
   * EchoFunction()[expr] - operator form of the `Echo`function. Print the `expr` to the default
   * output stream and return `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EchoFunction.md">EchoFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol EchoFunction =
      F.initFinalSymbol("EchoFunction", ID.EchoFunction);

  public final static IBuiltInSymbol EdgeCount = F.initFinalSymbol("EdgeCount", ID.EdgeCount);

  public final static IBuiltInSymbol EdgeForm = F.initFinalSymbol("EdgeForm", ID.EdgeForm);

  public final static IBuiltInSymbol EdgeLabels = F.initFinalSymbol("EdgeLabels", ID.EdgeLabels);

  /**
   * EdgeList(graph) - convert the `graph` into a list of edges.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeList.md">EdgeList
   *      documentation</a>
   */
  public final static IBuiltInSymbol EdgeList = F.initFinalSymbol("EdgeList", ID.EdgeList);

  /**
   * EdgeQ(graph, edge) - test if `edge` is an edge in the `graph` object.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeQ.md">EdgeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol EdgeQ = F.initFinalSymbol("EdgeQ", ID.EdgeQ);

  /**
   * EdgeRules(graph) - convert the `graph` into a list of rules. All edge types (undirected,
   * directed) are represented by a rule `lhs->rhs`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeRules.md">EdgeRules
   *      documentation</a>
   */
  public final static IBuiltInSymbol EdgeRules = F.initFinalSymbol("EdgeRules", ID.EdgeRules);

  public final static IBuiltInSymbol EdgeShapeFunction =
      F.initFinalSymbol("EdgeShapeFunction", ID.EdgeShapeFunction);

  public final static IBuiltInSymbol EdgeStyle = F.initFinalSymbol("EdgeStyle", ID.EdgeStyle);

  public final static IBuiltInSymbol EdgeWeight = F.initFinalSymbol("EdgeWeight", ID.EdgeWeight);

  /**
   * EditDistance(a, b) - returns the Levenshtein distance of `a` and `b`, which is defined as the
   * minimum number of insertions, deletions and substitutions on the constituents of `a` and `b`
   * needed to transform one into the other.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EditDistance.md">EditDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol EditDistance =
      F.initFinalSymbol("EditDistance", ID.EditDistance);

  /**
   * EffectiveInterest(i, n) - returns an effective interest rate object.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EffectiveInterest.md">EffectiveInterest
   *      documentation</a>
   */
  public final static IBuiltInSymbol EffectiveInterest =
      F.initFinalSymbol("EffectiveInterest", ID.EffectiveInterest);

  /**
   * Eigenvalues(matrix) - get the numerical eigenvalues of the `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigenvalues.md">Eigenvalues
   *      documentation</a>
   */
  public final static IBuiltInSymbol Eigenvalues = F.initFinalSymbol("Eigenvalues", ID.Eigenvalues);

  /**
   * Eigenvectors(matrix) - get the numerical eigenvectors of the `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigenvectors.md">Eigenvectors
   *      documentation</a>
   */
  public final static IBuiltInSymbol Eigenvectors =
      F.initFinalSymbol("Eigenvectors", ID.Eigenvectors);

  /**
   * Element(symbol, dom) - assume (or test) that the `symbol` is in the domain `dom`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Element.md">Element
   *      documentation</a>
   */
  public final static IBuiltInSymbol Element = F.initFinalSymbol("Element", ID.Element);

  /**
   * ElementData("name", "property") - gives the value of the property for the chemical specified by
   * name.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ElementData.md">ElementData
   *      documentation</a>
   */
  public final static IBuiltInSymbol ElementData = F.initFinalSymbol("ElementData", ID.ElementData);

  /**
   * Eliminate(list-of-equations, list-of-variables) - attempts to eliminate the variables from the
   * `list-of-variables` in the `list-of-equations`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eliminate.md">Eliminate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Eliminate = F.initFinalSymbol("Eliminate", ID.Eliminate);

  public final static IBuiltInSymbol EliminationOrder =
      F.initFinalSymbol("EliminationOrder", ID.EliminationOrder);

  public final static IBuiltInSymbol Ellipsoid = F.initFinalSymbol("Ellipsoid", ID.Ellipsoid);

  /**
   * EllipticE(z) - returns the complete elliptic integral of the second kind.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticE.md">EllipticE
   *      documentation</a>
   */
  public final static IBuiltInSymbol EllipticE = F.initFinalSymbol("EllipticE", ID.EllipticE);

  /**
   * EllipticF(z) - returns the incomplete elliptic integral of the first kind.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticF.md">EllipticF
   *      documentation</a>
   */
  public final static IBuiltInSymbol EllipticF = F.initFinalSymbol("EllipticF", ID.EllipticF);

  /**
   * EllipticK(z) - returns the complete elliptic integral of the first kind.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticK.md">EllipticK
   *      documentation</a>
   */
  public final static IBuiltInSymbol EllipticK = F.initFinalSymbol("EllipticK", ID.EllipticK);

  /**
   * EllipticPi(n,m) - returns the complete elliptic integral of the third kind.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticPi.md">EllipticPi
   *      documentation</a>
   */
  public final static IBuiltInSymbol EllipticPi = F.initFinalSymbol("EllipticPi", ID.EllipticPi);

  public final static IBuiltInSymbol EllipticTheta =
      F.initFinalSymbol("EllipticTheta", ID.EllipticTheta);

  /**
   * End( ) - end a context definition started with `Begin`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/End.md">End
   *      documentation</a>
   */
  public final static IBuiltInSymbol End = F.initFinalSymbol("End", ID.End);

  public final static IBuiltInSymbol EndOfFile = F.initFinalSymbol("EndOfFile", ID.EndOfFile);

  public final static IBuiltInSymbol EndOfLine = F.initFinalSymbol("EndOfLine", ID.EndOfLine);

  public final static IBuiltInSymbol EndOfString = F.initFinalSymbol("EndOfString", ID.EndOfString);

  /**
   * EndPackage( ) - end a package definition
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EndPackage.md">EndPackage
   *      documentation</a>
   */
  public final static IBuiltInSymbol EndPackage = F.initFinalSymbol("EndPackage", ID.EndPackage);

  public final static IBuiltInSymbol EndTestSection =
      F.initFinalSymbol("EndTestSection", ID.EndTestSection);

  public final static IBuiltInSymbol Entity = F.initFinalSymbol("Entity", ID.Entity);

  /**
   * Entropy(list) - return the base `E` (Shannon) information entropy of the elements in `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Entropy.md">Entropy
   *      documentation</a>
   */
  public final static IBuiltInSymbol Entropy = F.initFinalSymbol("Entropy", ID.Entropy);

  /**
   * Equal(x, y) - yields `True` if `x` and `y` are known to be equal, or `False` if `x` and `y` are
   * known to be unequal.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Equal.md">Equal
   *      documentation</a>
   */
  public final static IBuiltInSymbol Equal = F.initFinalSymbol("Equal", ID.Equal);

  public final static IBuiltInSymbol EqualTo = F.initFinalSymbol("EqualTo", ID.EqualTo);

  /**
   * Equivalent(arg1, arg2, ...) - Equivalence relation. `Equivalent(A, B)` is `True` iff `A` and
   * `B` are both `True` or both `False`. Returns `True` if all of the arguments are logically
   * equivalent. Returns `False` otherwise. `Equivalent(arg1, arg2, ...)` is equivalent to `(arg1 &&
   * arg2 && ...) || (!arg1 && !arg2 && ...)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Equivalent.md">Equivalent
   *      documentation</a>
   */
  public final static IBuiltInSymbol Equivalent = F.initFinalSymbol("Equivalent", ID.Equivalent);

  /**
   * Erf(z) - returns the error function of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erf.md">Erf
   *      documentation</a>
   */
  public final static IBuiltInSymbol Erf = F.initFinalSymbol("Erf", ID.Erf);

  /**
   * Erfc(z) - returns the complementary error function of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erfc.md">Erfc
   *      documentation</a>
   */
  public final static IBuiltInSymbol Erfc = F.initFinalSymbol("Erfc", ID.Erfc);

  /**
   * Erfi(z) - returns the imaginary error function of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erfi.md">Erfi
   *      documentation</a>
   */
  public final static IBuiltInSymbol Erfi = F.initFinalSymbol("Erfi", ID.Erfi);

  /**
   * ErlangDistribution({k, lambda}) - returns a Erlang distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ErlangDistribution.md">ErlangDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol ErlangDistribution =
      F.initFinalSymbol("ErlangDistribution", ID.ErlangDistribution);

  /**
   * EuclideanDistance(u, v) - returns the euclidean distance between `u` and `v`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EuclideanDistance.md">EuclideanDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol EuclideanDistance =
      F.initFinalSymbol("EuclideanDistance", ID.EuclideanDistance);

  /**
   * EulerE(n) - gives the euler number `En`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerE.md">EulerE
   *      documentation</a>
   */
  public final static IBuiltInSymbol EulerE = F.initFinalSymbol("EulerE", ID.EulerE);

  /**
   * EulerGamma - Euler-Mascheroni constant
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerGamma.md">EulerGamma
   *      documentation</a>
   */
  public final static IBuiltInSymbol EulerGamma = F.initFinalSymbol("EulerGamma", ID.EulerGamma);

  /**
   * EulerPhi(n) - compute Euler's totient function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerPhi.md">EulerPhi
   *      documentation</a>
   */
  public final static IBuiltInSymbol EulerPhi = F.initFinalSymbol("EulerPhi", ID.EulerPhi);

  /**
   * EulerianGraphQ(graph) - returns `True` if `graph` is an eulerian graph, and `False` otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerianGraphQ.md">EulerianGraphQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol EulerianGraphQ =
      F.initFinalSymbol("EulerianGraphQ", ID.EulerianGraphQ);

  /**
   * Evaluate(expr) - the `Evaluate` function will be executed even if the function attributes
   * `HoldFirst, HoldRest, HoldAll` are set for the function head.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Evaluate.md">Evaluate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Evaluate = F.initFinalSymbol("Evaluate", ID.Evaluate);

  /**
   * EvenQ(x) - returns `True` if `x` is even, and `False` otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EvenQ.md">EvenQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol EvenQ = F.initFinalSymbol("EvenQ", ID.EvenQ);

  /**
   * ExactNumberQ(expr) - returns `True` if `expr` is an exact number, and `False` otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExactNumberQ.md">ExactNumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExactNumberQ =
      F.initFinalSymbol("ExactNumberQ", ID.ExactNumberQ);

  /**
   * Except(c) - represents a pattern object that matches any expression except those matching `c`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Except.md">Except
   *      documentation</a>
   */
  public final static IBuiltInSymbol Except = F.initFinalSymbol("Except", ID.Except);

  public final static IBuiltInSymbol Exists = F.initFinalSymbol("Exists", ID.Exists);

  public final static IBuiltInSymbol Exit = F.initFinalSymbol("Exit", ID.Exit);

  /**
   * Exp(z) - the exponential function `E^z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Exp.md">Exp
   *      documentation</a>
   */
  public final static IBuiltInSymbol Exp = F.initFinalSymbol("Exp", ID.Exp);

  /**
   * ExpIntegralE(n, expr) - returns the exponential integral `E_n(expr)` of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpIntegralE.md">ExpIntegralE
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExpIntegralE =
      F.initFinalSymbol("ExpIntegralE", ID.ExpIntegralE);

  /**
   * ExpIntegralEi(expr) - returns the exponential integral `Ei(expr)` of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpIntegralEi.md">ExpIntegralEi
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExpIntegralEi =
      F.initFinalSymbol("ExpIntegralEi", ID.ExpIntegralEi);

  public final static IBuiltInSymbol ExpToTrig = F.initFinalSymbol("ExpToTrig", ID.ExpToTrig);

  /**
   * Expand(expr) - expands out positive rational powers and products of sums in `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expand.md">Expand
   *      documentation</a>
   */
  public final static IBuiltInSymbol Expand = F.initFinalSymbol("Expand", ID.Expand);

  /**
   * ExpandAll(expr) - expands out all positive integer powers and products of sums in `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpandAll.md">ExpandAll
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExpandAll = F.initFinalSymbol("ExpandAll", ID.ExpandAll);

  /**
   * Expectation(pure-function, data-set) - returns the expected value of the `pure-function` for
   * the given `data-set`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expectation.md">Expectation
   *      documentation</a>
   */
  public final static IBuiltInSymbol Expectation = F.initFinalSymbol("Expectation", ID.Expectation);

  /**
   * Exponent(polynomial, x) - gives the maximum power with which `x` appears in the expanded form
   * of `polynomial`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Exponent.md">Exponent
   *      documentation</a>
   */
  public final static IBuiltInSymbol Exponent = F.initFinalSymbol("Exponent", ID.Exponent);

  /**
   * ExponentialDistribution(lambda) - returns an exponential distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExponentialDistribution.md">ExponentialDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExponentialDistribution =
      F.initFinalSymbol("ExponentialDistribution", ID.ExponentialDistribution);

  /**
   * Export("path-to-filename", expression, "WXF") - if the file system is enabled, export the
   * `expression` in WXF format to the "path-to-filename" file.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Export.md">Export
   *      documentation</a>
   */
  public final static IBuiltInSymbol Export = F.initFinalSymbol("Export", ID.Export);

  public final static IBuiltInSymbol ExportString =
      F.initFinalSymbol("ExportString", ID.ExportString);

  public final static IBuiltInSymbol Expression = F.initFinalSymbol("Expression", ID.Expression);

  /**
   * ExtendedGCD(n1, n2, ...) - computes the extended greatest common divisor of the given integers.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExtendedGCD.md">ExtendedGCD
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExtendedGCD = F.initFinalSymbol("ExtendedGCD", ID.ExtendedGCD);

  public final static IBuiltInSymbol Extension = F.initFinalSymbol("Extension", ID.Extension);

  /**
   * Extract(expr, list) - extracts parts of `expr` specified by `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Extract.md">Extract
   *      documentation</a>
   */
  public final static IBuiltInSymbol Extract = F.initFinalSymbol("Extract", ID.Extract);

  public final static IBuiltInSymbol FRatioDistribution =
      F.initFinalSymbol("FRatioDistribution", ID.FRatioDistribution);

  /**
   * Factor(expr) - factors the polynomial expression `expr`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factor.md">Factor
   *      documentation</a>
   */
  public final static IBuiltInSymbol Factor = F.initFinalSymbol("Factor", ID.Factor);

  /**
   * FactorInteger(n) - returns the factorization of `n` as a list of factors and exponents.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorInteger.md">FactorInteger
   *      documentation</a>
   */
  public final static IBuiltInSymbol FactorInteger =
      F.initFinalSymbol("FactorInteger", ID.FactorInteger);

  /**
   * FactorSquareFree(polynomial) - factor the polynomial expression `polynomial` square free.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorSquareFree.md">FactorSquareFree
   *      documentation</a>
   */
  public final static IBuiltInSymbol FactorSquareFree =
      F.initFinalSymbol("FactorSquareFree", ID.FactorSquareFree);

  /**
   * FactorSquareFreeList(polynomial) - get the square free factors of the polynomial expression
   * `polynomial`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorSquareFreeList.md">FactorSquareFreeList
   *      documentation</a>
   */
  public final static IBuiltInSymbol FactorSquareFreeList =
      F.initFinalSymbol("FactorSquareFreeList", ID.FactorSquareFreeList);

  /**
   * FactorTerms(poly) - pulls out any overall numerical factor in `poly`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorTerms.md">FactorTerms
   *      documentation</a>
   */
  public final static IBuiltInSymbol FactorTerms = F.initFinalSymbol("FactorTerms", ID.FactorTerms);

  /**
   * Factorial(n) - returns the factorial number of the integer `n`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factorial.md">Factorial
   *      documentation</a>
   */
  public final static IBuiltInSymbol Factorial = F.initFinalSymbol("Factorial", ID.Factorial);

  /**
   * Factorial2(n) - returns the double factorial number of the integer `n` as `n*(n-2)*(n-4)...`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factorial2.md">Factorial2
   *      documentation</a>
   */
  public final static IBuiltInSymbol Factorial2 = F.initFinalSymbol("Factorial2", ID.Factorial2);

  public final static IBuiltInSymbol FactorialPower =
      F.initFinalSymbol("FactorialPower", ID.FactorialPower);

  /**
   * False - the constant `False` represents the boolean value **false**
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/False.md">False
   *      documentation</a>
   */
  public final static IBuiltInSymbol False = F.initFinalSymbol("False", ID.False);

  /**
   * Fibonacci(n) - returns the Fibonacci number of the integer `n`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fibonacci.md">Fibonacci
   *      documentation</a>
   */
  public final static IBuiltInSymbol Fibonacci = F.initFinalSymbol("Fibonacci", ID.Fibonacci);

  public final static IBuiltInSymbol File = F.initFinalSymbol("File", ID.File);

  public final static IBuiltInSymbol FileNameJoin =
      F.initFinalSymbol("FileNameJoin", ID.FileNameJoin);

  public final static IBuiltInSymbol FileNameTake =
      F.initFinalSymbol("FileNameTake", ID.FileNameTake);

  /**
   * FileNames( ) - returns a list with the filenames in the current working folder..
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FileNames.md">FileNames
   *      documentation</a>
   */
  public final static IBuiltInSymbol FileNames = F.initFinalSymbol("FileNames", ID.FileNames);

  /**
   * FilePrint(file) - prints the raw contents of `file`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FilePrint.md">FilePrint
   *      documentation</a>
   */
  public final static IBuiltInSymbol FilePrint = F.initFinalSymbol("FilePrint", ID.FilePrint);

  public final static IBuiltInSymbol Filling = F.initFinalSymbol("Filling", ID.Filling);

  public final static IBuiltInSymbol FillingStyle =
      F.initFinalSymbol("FillingStyle", ID.FillingStyle);

  /**
   * FilterRules(list-of-option-rules, list-of-rules) - filter the `list-of-option-rules` by
   * `list-of-rules`or `list-of-symbols`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FilterRules.md">FilterRules
   *      documentation</a>
   */
  public final static IBuiltInSymbol FilterRules = F.initFinalSymbol("FilterRules", ID.FilterRules);

  /**
   * FindClusters(list-of-data-points, k) - Clustering algorithm based on David Arthur and Sergei
   * Vassilvitski k-means++ algorithm. Create `k` number of clusters to split the
   * `list-of-data-points` into.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindClusters.md">FindClusters
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindClusters =
      F.initFinalSymbol("FindClusters", ID.FindClusters);

  public final static IBuiltInSymbol FindEdgeCover =
      F.initFinalSymbol("FindEdgeCover", ID.FindEdgeCover);

  /**
   * FindEulerianCycle(graph) - find an eulerian cycle in the `graph`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindEulerianCycle.md">FindEulerianCycle
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindEulerianCycle =
      F.initFinalSymbol("FindEulerianCycle", ID.FindEulerianCycle);

  /**
   * FindFit(list-of-data-points, function, parameters, variable) - solve a least squares problem
   * using the Levenberg-Marquardt algorithm.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindFit.md">FindFit
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindFit = F.initFinalSymbol("FindFit", ID.FindFit);

  public final static IBuiltInSymbol FindGraphCommunities =
      F.initFinalSymbol("FindGraphCommunities", ID.FindGraphCommunities);

  /**
   * FindGraphIsomorphism(graph1, graph2) - returns an isomorphism between `graph1` and `graph2` if
   * it exists. Return an empty list if no isomorphism exists.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindGraphIsomorphism.md">FindGraphIsomorphism
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindGraphIsomorphism =
      F.initFinalSymbol("FindGraphIsomorphism", ID.FindGraphIsomorphism);

  /**
   * FindHamiltonianCycle(graph) - find an hamiltonian cycle in the `graph`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindHamiltonianCycle.md">FindHamiltonianCycle
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindHamiltonianCycle =
      F.initFinalSymbol("FindHamiltonianCycle", ID.FindHamiltonianCycle);

  public final static IBuiltInSymbol FindIndependentEdgeSet =
      F.initFinalSymbol("FindIndependentEdgeSet", ID.FindIndependentEdgeSet);

  public final static IBuiltInSymbol FindIndependentVertexSet =
      F.initFinalSymbol("FindIndependentVertexSet", ID.FindIndependentVertexSet);

  /**
   * FindInstance(equations, vars) - attempts to find one solution which solves the `equations` for
   * the variables `vars`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindInstance.md">FindInstance
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindInstance =
      F.initFinalSymbol("FindInstance", ID.FindInstance);

  /**
   * FindPermutation(list1, list2) - create a `Cycles({{...},{...}, ...})` permutation expression,
   * for two lists whose arguments are the same but may be differently arranged.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindPermutation.md">FindPermutation
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindPermutation =
      F.initFinalSymbol("FindPermutation", ID.FindPermutation);

  public final static IBuiltInSymbol FindMaximum = F.initFinalSymbol("FindMaximum", ID.FindMaximum);

  public final static IBuiltInSymbol FindMinimum = F.initFinalSymbol("FindMinimum", ID.FindMinimum);

  /**
   * FindRoot(f, {x, xmin, xmax}) - searches for a numerical root of `f` for the variable `x`, in
   * the range `xmin` to `xmax`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindRoot.md">FindRoot
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindRoot = F.initFinalSymbol("FindRoot", ID.FindRoot);

  /**
   * FindShortestPath(graph, source, destination) - find a shortest path in the `graph` from
   * `source` to `destination`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindShortestPath.md">FindShortestPath
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindShortestPath =
      F.initFinalSymbol("FindShortestPath", ID.FindShortestPath);

  /**
   * FindShortestTour({{p11, p12}, {p21, p22}, {p31, p32}, ...}) - find a shortest tour in the
   * `graph` with minimum `EuclideanDistance`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindShortestTour.md">FindShortestTour
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindShortestTour =
      F.initFinalSymbol("FindShortestTour", ID.FindShortestTour);

  /**
   * FindSpanningTree(graph) - find the minimum spanning tree in the `graph`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindSpanningTree.md">FindSpanningTree
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindSpanningTree =
      F.initFinalSymbol("FindSpanningTree", ID.FindSpanningTree);

  /**
   * FindVertexCover(graph) - algorithm to find a vertex cover for a `graph`. A vertex cover is a
   * set of vertices that touches all the edges in the graph.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindVertexCover.md">FindVertexCover
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindVertexCover =
      F.initFinalSymbol("FindVertexCover", ID.FindVertexCover);

  /**
   * First(expr) - returns the first element in `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/First.md">First
   *      documentation</a>
   */
  public final static IBuiltInSymbol First = F.initFinalSymbol("First", ID.First);

  /**
   * FirstCase({arg1, arg2, ...}, pattern-matcher) - returns the first of the elements `argi` for
   * which `pattern-matcher` is matching.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FirstCase.md">FirstCase
   *      documentation</a>
   */
  public final static IBuiltInSymbol FirstCase = F.initFinalSymbol("FirstCase", ID.FirstCase);

  /**
   * FirstPosition(expression, pattern-matcher) - returns the first subexpression of `expression`
   * for which `pattern-matcher` is matching.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FirstPosition.md">FirstPosition
   *      documentation</a>
   */
  public final static IBuiltInSymbol FirstPosition =
      F.initFinalSymbol("FirstPosition", ID.FirstPosition);

  /**
   * Fit(list-of-data-points, degree, variable) - solve a least squares problem using the
   * Levenberg-Marquardt algorithm.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fit.md">Fit
   *      documentation</a>
   */
  public final static IBuiltInSymbol Fit = F.initFinalSymbol("Fit", ID.Fit);

  /**
   * FittedModel( ) - `FittedModel`holds the model generated with `LinearModelFit`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FittedModel.md">FittedModel
   *      documentation</a>
   */
  public final static IBuiltInSymbol FittedModel = F.initFinalSymbol("FittedModel", ID.FittedModel);

  /**
   * FiveNum({dataset}) - the Tuckey five-number summary is a set of descriptive statistics that
   * provide information about a `dataset`. It consists of the five most important sample
   * percentiles:
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FiveNum.md">FiveNum
   *      documentation</a>
   */
  public final static IBuiltInSymbol FiveNum = F.initFinalSymbol("FiveNum", ID.FiveNum);

  /**
   * FixedPoint(f, expr) - starting with `expr`, iteratively applies `f` until the result no longer
   * changes.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FixedPoint.md">FixedPoint
   *      documentation</a>
   */
  public final static IBuiltInSymbol FixedPoint = F.initFinalSymbol("FixedPoint", ID.FixedPoint);

  /**
   * FixedPointList(f, expr) - starting with `expr`, iteratively applies `f` until the result no
   * longer changes, and returns a list of all intermediate results.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FixedPointList.md">FixedPointList
   *      documentation</a>
   */
  public final static IBuiltInSymbol FixedPointList =
      F.initFinalSymbol("FixedPointList", ID.FixedPointList);

  /**
   * Flat - is an attribute that specifies that nested occurrences of a function should be
   * automatically flattened.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Flat.md">Flat
   *      documentation</a>
   */
  public final static IBuiltInSymbol Flat = F.initFinalSymbol("Flat", ID.Flat);

  public final static IBuiltInSymbol FlatTopWindow =
      F.initFinalSymbol("FlatTopWindow", ID.FlatTopWindow);

  /**
   * Flatten(expr) - flattens out nested lists in `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Flatten.md">Flatten
   *      documentation</a>
   */
  public final static IBuiltInSymbol Flatten = F.initFinalSymbol("Flatten", ID.Flatten);

  /**
   * FlattenAt(expr, position) - flattens out nested lists at the given `position` in `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FlattenAt.md">FlattenAt
   *      documentation</a>
   */
  public final static IBuiltInSymbol FlattenAt = F.initFinalSymbol("FlattenAt", ID.FlattenAt);

  public final static IBuiltInSymbol Float = F.initFinalSymbol("Float", ID.Float);

  /**
   * Floor(expr) - gives the smallest integer less than or equal `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Floor.md">Floor
   *      documentation</a>
   */
  public final static IBuiltInSymbol Floor = F.initFinalSymbol("Floor", ID.Floor);

  /**
   * Fold[f, x, {a, b}] - returns `f[f[x, a], b]`, and this nesting continues for lists of arbitrary
   * length.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fold.md">Fold
   *      documentation</a>
   */
  public final static IBuiltInSymbol Fold = F.initFinalSymbol("Fold", ID.Fold);

  /**
   * FoldList[f, x, {a, b}] - returns `{x, f[x, a], f[f[x, a], b]}`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FoldList.md">FoldList
   *      documentation</a>
   */
  public final static IBuiltInSymbol FoldList = F.initFinalSymbol("FoldList", ID.FoldList);

  /**
   * For(start, test, incr, body) - evaluates `start`, and then iteratively `body` and `incr` as
   * long as test evaluates to `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/For.md">For
   *      documentation</a>
   */
  public final static IBuiltInSymbol For = F.initFinalSymbol("For", ID.For);

  public final static IBuiltInSymbol ForAll = F.initFinalSymbol("ForAll", ID.ForAll);

  /**
   * Fourier(vector-of-complex-numbers) - Discrete Fourier transform of a
   * `vector-of-complex-numbers`. Fourier transform is restricted to vectors with length of power of
   * 2.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fourier.md">Fourier
   *      documentation</a>
   */
  public final static IBuiltInSymbol Fourier = F.initFinalSymbol("Fourier", ID.Fourier);

  /**
   * FourierMatrix(n) - gives a fourier matrix with the dimension `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FourierMatrix.md">FourierMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol FourierMatrix =
      F.initFinalSymbol("FourierMatrix", ID.FourierMatrix);

  public final static IBuiltInSymbol FractionBox = F.initFinalSymbol("FractionBox", ID.FractionBox);

  /**
   * FractionalPart(number) - get the fractional part of a `number`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FractionalPart.md">FractionalPart
   *      documentation</a>
   */
  public final static IBuiltInSymbol FractionalPart =
      F.initFinalSymbol("FractionalPart", ID.FractionalPart);

  /**
   * FrechetDistribution(a,b) - returns a Frechet distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrechetDistribution.md">FrechetDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol FrechetDistribution =
      F.initFinalSymbol("FrechetDistribution", ID.FrechetDistribution);

  /**
   * FreeQ(`expr`, `x`) - returns 'True' if `expr` does not contain the expression `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FreeQ.md">FreeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol FreeQ = F.initFinalSymbol("FreeQ", ID.FreeQ);

  public final static IBuiltInSymbol FresnelC = F.initFinalSymbol("FresnelC", ID.FresnelC);

  public final static IBuiltInSymbol FresnelS = F.initFinalSymbol("FresnelS", ID.FresnelS);

  /**
   * FrobeniusNumber({a1, ... ,aN}) - returns the Frobenius number of the nonnegative integers `{a1,
   * ... ,aN}`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrobeniusNumber.md">FrobeniusNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol FrobeniusNumber =
      F.initFinalSymbol("FrobeniusNumber", ID.FrobeniusNumber);

  /**
   * FrobeniusSolve({a1, ... ,aN}, M) - get a list of solutions for the Frobenius equation given by
   * the list of integers `{a1, ... ,aN}` and the non-negative integer `M`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrobeniusSolve.md">FrobeniusSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol FrobeniusSolve =
      F.initFinalSymbol("FrobeniusSolve", ID.FrobeniusSolve);

  /**
   * FromCharacterCode({ch1, ch2, ...}) - converts the `ch1, ch2,...` character codes into a string
   * of corresponding characters.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromCharacterCode.md">FromCharacterCode
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromCharacterCode =
      F.initFinalSymbol("FromCharacterCode", ID.FromCharacterCode);

  /**
   * FromContinuedFraction({n1, n2, ...}) - reconstructs a number from the list of its continued
   * fraction terms `{n1, n2, ...}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromContinuedFraction.md">FromContinuedFraction
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromContinuedFraction =
      F.initFinalSymbol("FromContinuedFraction", ID.FromContinuedFraction);

  /**
   * FromDigits(list) - creates an expression from the list of digits for radix `10`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromDigits.md">FromDigits
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromDigits = F.initFinalSymbol("FromDigits", ID.FromDigits);

  /**
   * FromLetterNumber(number) - get the corresponding characters from the English alphabet.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromLetterNumber.md">FromLetterNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromLetterNumber =
      F.initFinalSymbol("FromLetterNumber", ID.FromLetterNumber);

  /**
   * FromPolarCoordinates({r, t}) - return the cartesian coordinates for the polar coordinates `{r,
   * t}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromPolarCoordinates.md">FromPolarCoordinates
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromPolarCoordinates =
      F.initFinalSymbol("FromPolarCoordinates", ID.FromPolarCoordinates);

  public final static IBuiltInSymbol Full = F.initFinalSymbol("Full", ID.Full);

  /**
   * FullForm(expression) - shows the internal representation of the given `expression`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FullForm.md">FullForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol FullForm = F.initFinalSymbol("FullForm", ID.FullForm);

  /**
   * FullSimplify(expr) - works like `Simplify` but additionally tries some `FunctionExpand` rule
   * transformations to simplify `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FullSimplify.md">FullSimplify
   *      documentation</a>
   */
  public final static IBuiltInSymbol FullSimplify =
      F.initFinalSymbol("FullSimplify", ID.FullSimplify);

  /**
   * Function(body) - represents a pure function with parameters `#1`, `#2`....
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Function.md">Function
   *      documentation</a>
   */
  public final static IBuiltInSymbol Function = F.initFinalSymbol("Function", ID.Function);

  public final static IBuiltInSymbol FunctionDomain =
      F.initFinalSymbol("FunctionDomain", ID.FunctionDomain);

  /**
   * FunctionExpand(expression) - expands the special function `expression`. `FunctionExpand`
   * expands simple nested radicals.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FunctionExpand.md">FunctionExpand
   *      documentation</a>
   */
  public final static IBuiltInSymbol FunctionExpand =
      F.initFinalSymbol("FunctionExpand", ID.FunctionExpand);

  public final static IBuiltInSymbol FunctionRange =
      F.initFinalSymbol("FunctionRange", ID.FunctionRange);

  /**
   * FunctionURL(built-in-symbol) - returns the GitHub URL of the `built-in-symbol` implementation
   * in the [Symja GitHub repository](https://github.com/axkr/symja_android_library).
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FunctionURL.md">FunctionURL
   *      documentation</a>
   */
  public final static IBuiltInSymbol FunctionURL = F.initFinalSymbol("FunctionURL", ID.FunctionURL);

  /**
   * GCD(n1, n2, ...) - computes the greatest common divisor of the given integers.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GCD.md">GCD
   *      documentation</a>
   */
  public final static IBuiltInSymbol GCD = F.initFinalSymbol("GCD", ID.GCD);

  /**
   * Gamma(z) - is the gamma function on the complex number `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gamma.md">Gamma
   *      documentation</a>
   */
  public final static IBuiltInSymbol Gamma = F.initFinalSymbol("Gamma", ID.Gamma);

  /**
   * GammaDistribution(a,b) - returns a gamma distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GammaDistribution.md">GammaDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol GammaDistribution =
      F.initFinalSymbol("GammaDistribution", ID.GammaDistribution);

  public final static IBuiltInSymbol GammaRegularized =
      F.initFinalSymbol("GammaRegularized", ID.GammaRegularized);

  /**
   * Gather(list, test) - gathers leaves of `list` into sub lists of items that are the same
   * according to `test`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gather.md">Gather
   *      documentation</a>
   */
  public final static IBuiltInSymbol Gather = F.initFinalSymbol("Gather", ID.Gather);

  /**
   * GatherBy(list, f) - gathers leaves of `list` into sub lists of items whose image under `f`
   * identical.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GatherBy.md">GatherBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol GatherBy = F.initFinalSymbol("GatherBy", ID.GatherBy);

  public final static IBuiltInSymbol GaussianIntegers =
      F.initFinalSymbol("GaussianIntegers", ID.GaussianIntegers);

  public final static IBuiltInSymbol GaussianMatrix =
      F.initFinalSymbol("GaussianMatrix", ID.GaussianMatrix);

  public final static IBuiltInSymbol GaussianWindow =
      F.initFinalSymbol("GaussianWindow", ID.GaussianWindow);

  /**
   * GegenbauerC(n, a, x) - returns the GegenbauerC polynomial.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GegenbauerC.md">GegenbauerC
   *      documentation</a>
   */
  public final static IBuiltInSymbol GegenbauerC = F.initFinalSymbol("GegenbauerC", ID.GegenbauerC);

  public final static IBuiltInSymbol General = F.initFinalSymbol("General", ID.General);

  /**
   * GeoDistance({latitude1,longitude1}, {latitude2,longitude2}) - returns the geodesic distance
   * between `{latitude1,longitude1}` and `{latitude2,longitude2}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoDistance.md">GeoDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol GeoDistance = F.initFinalSymbol("GeoDistance", ID.GeoDistance);

  public final static IBuiltInSymbol GeoPosition = F.initFinalSymbol("GeoPosition", ID.GeoPosition);

  public final static IBuiltInSymbol GeodesyData = F.initFinalSymbol("GeodesyData", ID.GeodesyData);

  /**
   * GeometricDistribution(p) - returns a geometric distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeometricDistribution.md">GeometricDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol GeometricDistribution =
      F.initFinalSymbol("GeometricDistribution", ID.GeometricDistribution);

  /**
   * GeometricMean({a, b, c,...}) - returns the geometric mean of `{a, b, c,...}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeometricMean.md">GeometricMean
   *      documentation</a>
   */
  public final static IBuiltInSymbol GeometricMean =
      F.initFinalSymbol("GeometricMean", ID.GeometricMean);

  public final static IBuiltInSymbol GeometricTransformation =
      F.initFinalSymbol("GeometricTransformation", ID.GeometricTransformation);

  /**
   * Get("path-to-package-file-name") - load the package defined in `path-to-package-file-name`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Get.md">Get
   *      documentation</a>
   */
  public final static IBuiltInSymbol Get = F.initFinalSymbol("Get", ID.Get);

  /**
   * Glaisher - Glaisher constant.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Glaisher.md">Glaisher
   *      documentation</a>
   */
  public final static IBuiltInSymbol Glaisher = F.initFinalSymbol("Glaisher", ID.Glaisher);

  public final static IBuiltInSymbol GoldenAngle = F.initFinalSymbol("GoldenAngle", ID.GoldenAngle);

  /**
   * GoldenRatio - is the golden ratio `(1+Sqrt(5))/2`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GoldenRatio.md">GoldenRatio
   *      documentation</a>
   */
  public final static IBuiltInSymbol GoldenRatio = F.initFinalSymbol("GoldenRatio", ID.GoldenRatio);

  public final static IBuiltInSymbol GompertzMakehamDistribution =
      F.initFinalSymbol("GompertzMakehamDistribution", ID.GompertzMakehamDistribution);

  /**
   * Grad(function, list-of-variables) - gives the gradient of the function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Grad.md">Grad
   *      documentation</a>
   */
  public final static IBuiltInSymbol Grad = F.initFinalSymbol("Grad", ID.Grad);

  /**
   * Graph({edge1,...,edgeN}) - create a graph from the given edges `edge1,...,edgeN`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Graph.md">Graph
   *      documentation</a>
   */
  public final static IBuiltInSymbol Graph = F.initFinalSymbol("Graph", ID.Graph);

  /**
   * GraphCenter(graph) - compute the `graph` center. The center of a `graph` is the set of vertices
   * of graph eccentricity equal to the `graph` radius.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphCenter.md">GraphCenter
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphCenter = F.initFinalSymbol("GraphCenter", ID.GraphCenter);

  public final static IBuiltInSymbol GraphData = F.initFinalSymbol("GraphData", ID.GraphData);

  /**
   * GraphDiameter(graph) - return the diameter of the `graph`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphDiameter.md">GraphDiameter
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphDiameter =
      F.initFinalSymbol("GraphDiameter", ID.GraphDiameter);

  /**
   * GraphPeriphery(graph) - compute the `graph` periphery. The periphery of a `graph` is the set of
   * vertices of graph eccentricity equal to the graph diameter.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphPeriphery.md">GraphPeriphery
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphPeriphery =
      F.initFinalSymbol("GraphPeriphery", ID.GraphPeriphery);

  /**
   * GraphQ(expr) - test if `expr` is a graph object.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphQ.md">GraphQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphQ = F.initFinalSymbol("GraphQ", ID.GraphQ);

  /**
   * GraphRadius(graph) - return the radius of the `graph`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphRadius.md">GraphRadius
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphRadius = F.initFinalSymbol("GraphRadius", ID.GraphRadius);

  public final static IBuiltInSymbol GraphUnion = F.initFinalSymbol("GraphUnion", ID.GraphUnion);

  public final static IBuiltInSymbol Graphics = F.initFinalSymbol("Graphics", ID.Graphics);

  /**
   * Graphics3D(primitives, options) - represents a three-dimensional graphic.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Graphics3D.md">Graphics3D
   *      documentation</a>
   */
  public final static IBuiltInSymbol Graphics3D = F.initFinalSymbol("Graphics3D", ID.Graphics3D);

  public final static IBuiltInSymbol GraphicsComplex =
      F.initFinalSymbol("GraphicsComplex", ID.GraphicsComplex);

  public final static IBuiltInSymbol GraphicsGroup =
      F.initFinalSymbol("GraphicsGroup", ID.GraphicsGroup);

  public final static IBuiltInSymbol Gray = F.initFinalSymbol("Gray", ID.Gray);

  public final static IBuiltInSymbol GrayLevel = F.initFinalSymbol("GrayLevel", ID.GrayLevel);

  /**
   * Greater(x, y) - yields `True` if `x` is known to be greater than `y`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Greater.md">Greater
   *      documentation</a>
   */
  public final static IBuiltInSymbol Greater = F.initFinalSymbol("Greater", ID.Greater);

  /**
   * GreaterEqual(x, y) - yields `True` if `x` is known to be greater than or equal to `y`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterEqual.md">GreaterEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol GreaterEqual =
      F.initFinalSymbol("GreaterEqual", ID.GreaterEqual);

  public final static IBuiltInSymbol GreaterEqualThan =
      F.initFinalSymbol("GreaterEqualThan", ID.GreaterEqualThan);

  public final static IBuiltInSymbol GreaterThan = F.initFinalSymbol("GreaterThan", ID.GreaterThan);

  public final static IBuiltInSymbol Green = F.initFinalSymbol("Green", ID.Green);

  /**
   * GroebnerBasis({polynomial-list},{variable-list}) - returns a Gröbner basis for the
   * `polynomial-list` and `variable-list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GroebnerBasis.md">GroebnerBasis
   *      documentation</a>
   */
  public final static IBuiltInSymbol GroebnerBasis =
      F.initFinalSymbol("GroebnerBasis", ID.GroebnerBasis);

  /**
   * GroupBy(list, head) - return an association where the elements of `list` are grouped by
   * `head(element)`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GroupBy.md">GroupBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol GroupBy = F.initFinalSymbol("GroupBy", ID.GroupBy);

  /**
   * Gudermannian(expr) - computes the gudermannian function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gudermannian.md">Gudermannian
   *      documentation</a>
   */
  public final static IBuiltInSymbol Gudermannian =
      F.initFinalSymbol("Gudermannian", ID.Gudermannian);

  /**
   * GumbelDistribution(a, b) - returns a Gumbel distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GumbelDistribution.md">GumbelDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol GumbelDistribution =
      F.initFinalSymbol("GumbelDistribution", ID.GumbelDistribution);

  /**
   * HamiltonianGraphQ(graph) - returns `True` if `graph` is an hamiltonian graph, and `False`
   * otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HamiltonianGraphQ.md">HamiltonianGraphQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol HamiltonianGraphQ =
      F.initFinalSymbol("HamiltonianGraphQ", ID.HamiltonianGraphQ);

  /**
   * HammingDistance(a, b) - returns the Hamming distance of `a` and `b`, i.e. the number of
   * different elements.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HammingDistance.md">HammingDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol HammingDistance =
      F.initFinalSymbol("HammingDistance", ID.HammingDistance);

  public final static IBuiltInSymbol HammingWindow =
      F.initFinalSymbol("HammingWindow", ID.HammingWindow);

  public final static IBuiltInSymbol HankelH1 = F.initFinalSymbol("HankelH1", ID.HankelH1);

  public final static IBuiltInSymbol HankelH2 = F.initFinalSymbol("HankelH2", ID.HankelH2);

  public final static IBuiltInSymbol HannWindow = F.initFinalSymbol("HannWindow", ID.HannWindow);

  /**
   * HarmonicMean({a, b, c,...}) - returns the harmonic mean of `{a, b, c,...}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HarmonicMean.md">HarmonicMean
   *      documentation</a>
   */
  public final static IBuiltInSymbol HarmonicMean =
      F.initFinalSymbol("HarmonicMean", ID.HarmonicMean);

  /**
   * HarmonicNumber(n) - returns the `n`th harmonic number.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HarmonicNumber.md">HarmonicNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol HarmonicNumber =
      F.initFinalSymbol("HarmonicNumber", ID.HarmonicNumber);

  /**
   * Haversine(z) - returns the haversine function of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Haversine.md">Haversine
   *      documentation</a>
   */
  public final static IBuiltInSymbol Haversine = F.initFinalSymbol("Haversine", ID.Haversine);

  /**
   * Head(expr) - returns the head of the expression or atom `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Head.md">Head
   *      documentation</a>
   */
  public final static IBuiltInSymbol Head = F.initFinalSymbol("Head", ID.Head);

  public final static IBuiltInSymbol Heads = F.initFinalSymbol("Heads", ID.Heads);

  /**
   * HeavisideTheta(expr1, expr2, ... exprN) - returns `1` if all `expr1, expr2, ... exprN` are
   * positive and `0` if one of the `expr1, expr2, ... exprN` is negative. `HeavisideTheta(0)`
   * returns unevaluated as `HeavisideTheta(0)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HeavisideTheta.md">HeavisideTheta
   *      documentation</a>
   */
  public final static IBuiltInSymbol HeavisideTheta =
      F.initFinalSymbol("HeavisideTheta", ID.HeavisideTheta);

  /**
   * HermiteH(n, x) - returns the Hermite polynomial `H_n(x)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HermiteH.md">HermiteH
   *      documentation</a>
   */
  public final static IBuiltInSymbol HermiteH = F.initFinalSymbol("HermiteH", ID.HermiteH);

  /**
   * HermitianMatrixQ(m) - returns `True` if `m` is a hermitian matrix.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HermitianMatrixQ.md">HermitianMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol HermitianMatrixQ =
      F.initFinalSymbol("HermitianMatrixQ", ID.HermitianMatrixQ);

  /**
   * HexidecimalCharacter - represents the characters `0-9`, `a-f` and `A-F`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HexidecimalCharacter.md">HexidecimalCharacter
   *      documentation</a>
   */
  public final static IBuiltInSymbol HexidecimalCharacter =
      F.initFinalSymbol("HexidecimalCharacter", ID.HexidecimalCharacter);

  /**
   * HilbertMatrix(n) - gives the hilbert matrix with `n` rows and columns.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HilbertMatrix.md">HilbertMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol HilbertMatrix =
      F.initFinalSymbol("HilbertMatrix", ID.HilbertMatrix);

  /**
   * Histogram(list-of-values) - plots a histogram for a `list-of-values`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Histogram.md">Histogram
   *      documentation</a>
   */
  public final static IBuiltInSymbol Histogram = F.initFinalSymbol("Histogram", ID.Histogram);

  public final static IBuiltInSymbol HodgeDual = F.initFinalSymbol("HodgeDual", ID.HodgeDual);

  /**
   * Hold(expr) - `Hold` doesn't evaluate `expr`. `Hold` evaluates `UpValues`for its arguments.
   * `HoldComplete` doesn't evaluate `UpValues`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hold.md">Hold
   *      documentation</a>
   */
  public final static IBuiltInSymbol Hold = F.initFinalSymbol("Hold", ID.Hold);

  /**
   * HoldAll - is an attribute specifying that all arguments of a function should be left
   * unevaluated.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldAll.md">HoldAll
   *      documentation</a>
   */
  public final static IBuiltInSymbol HoldAll = F.initFinalSymbol("HoldAll", ID.HoldAll);

  public final static IBuiltInSymbol HoldAllComplete =
      F.initFinalSymbol("HoldAllComplete", ID.HoldAllComplete);

  /**
   * HoldComplete(expr) - `HoldComplete` doesn't evaluate `expr`. `Hold` evaluates `UpValues`for its
   * arguments. `HoldComplete` doesn't evaluate `UpValues`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldComplete.md">HoldComplete
   *      documentation</a>
   */
  public final static IBuiltInSymbol HoldComplete =
      F.initFinalSymbol("HoldComplete", ID.HoldComplete);

  /**
   * HoldFirst - is an attribute specifying that the first argument of a function should be left
   * unevaluated.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldFirst.md">HoldFirst
   *      documentation</a>
   */
  public final static IBuiltInSymbol HoldFirst = F.initFinalSymbol("HoldFirst", ID.HoldFirst);

  /**
   * HoldForm(expr) - `HoldForm` doesn't evaluate `expr` and didn't appear in the output.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldForm.md">HoldForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol HoldForm = F.initFinalSymbol("HoldForm", ID.HoldForm);

  /**
   * HoldPattern(expr) - `HoldPattern` doesn't evaluate `expr` for pattern-matching.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldPattern.md">HoldPattern
   *      documentation</a>
   */
  public final static IBuiltInSymbol HoldPattern = F.initFinalSymbol("HoldPattern", ID.HoldPattern);

  /**
   * HoldRest - is an attribute specifying that all but the first argument of a function should be
   * left unevaluated.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldRest.md">HoldRest
   *      documentation</a>
   */
  public final static IBuiltInSymbol HoldRest = F.initFinalSymbol("HoldRest", ID.HoldRest);

  public final static IBuiltInSymbol Horner = F.initFinalSymbol("Horner", ID.Horner);

  /**
   * HornerForm(polynomial) - Generate the horner scheme for a univariate `polynomial`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HornerForm.md">HornerForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol HornerForm = F.initFinalSymbol("HornerForm", ID.HornerForm);

  public final static IBuiltInSymbol Hue = F.initFinalSymbol("Hue", ID.Hue);

  /**
   * HurwitzZeta(s, a) - returns the Hurwitz zeta function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HurwitzZeta.md">HurwitzZeta
   *      documentation</a>
   */
  public final static IBuiltInSymbol HurwitzZeta = F.initFinalSymbol("HurwitzZeta", ID.HurwitzZeta);

  public final static IBuiltInSymbol HypercubeGraph =
      F.initFinalSymbol("HypercubeGraph", ID.HypercubeGraph);

  /**
   * Hypergeometric0F1(b, z) - return the `Hypergeometric0F1` function
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hypergeometric0F1.md">Hypergeometric0F1
   *      documentation</a>
   */
  public final static IBuiltInSymbol Hypergeometric0F1 =
      F.initFinalSymbol("Hypergeometric0F1", ID.Hypergeometric0F1);

  /**
   * Hypergeometric1F1(a, b, z) - return the `Hypergeometric1F1` function
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hypergeometric1F1.md">Hypergeometric1F1
   *      documentation</a>
   */
  public final static IBuiltInSymbol Hypergeometric1F1 =
      F.initFinalSymbol("Hypergeometric1F1", ID.Hypergeometric1F1);

  public final static IBuiltInSymbol Hypergeometric1F1Regularized =
      F.initFinalSymbol("Hypergeometric1F1Regularized", ID.Hypergeometric1F1Regularized);

  /**
   * Hypergeometric2F1(a, b, c, z) - return the `Hypergeometric2F1` function
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hypergeometric2F1.md">Hypergeometric2F1
   *      documentation</a>
   */
  public final static IBuiltInSymbol Hypergeometric2F1 =
      F.initFinalSymbol("Hypergeometric2F1", ID.Hypergeometric2F1);

  /**
   * HypergeometricDistribution(n, s, t) - returns a hypergeometric distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HypergeometricDistribution.md">HypergeometricDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol HypergeometricDistribution =
      F.initFinalSymbol("HypergeometricDistribution", ID.HypergeometricDistribution);

  /**
   * HypergeometricPFQ({a,...}, {b,...}, c) - return the `HypergeometricPFQ` function
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HypergeometricPFQ.md">HypergeometricPFQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol HypergeometricPFQ =
      F.initFinalSymbol("HypergeometricPFQ", ID.HypergeometricPFQ);

  public final static IBuiltInSymbol HypergeometricPFQRegularized =
      F.initFinalSymbol("HypergeometricPFQRegularized", ID.HypergeometricPFQRegularized);

  public final static IBuiltInSymbol HypergeometricU =
      F.initFinalSymbol("HypergeometricU", ID.HypergeometricU);

  /**
   * I - Imaginary unit - internally converted to the complex number `0+1*i`. `I` represents the
   * imaginary number `Sqrt(-1)`. `I^2` will be evaluated to `-1`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/I.md">I
   *      documentation</a>
   */
  public final static IBuiltInSymbol I = F.initFinalSymbol("I", ID.I);

  public final static IBuiltInSymbol Icosahedron = F.initFinalSymbol("Icosahedron", ID.Icosahedron);

  /**
   * Identity(x) - is the identity function, which returns `x` unchanged.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Identity.md">Identity
   *      documentation</a>
   */
  public final static IBuiltInSymbol Identity = F.initFinalSymbol("Identity", ID.Identity);

  /**
   * IdentityMatrix(n) - gives the identity matrix with `n` rows and columns.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IdentityMatrix.md">IdentityMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol IdentityMatrix =
      F.initFinalSymbol("IdentityMatrix", ID.IdentityMatrix);

  /**
   * If(cond, pos, neg) - returns `pos` if `cond` evaluates to `True`, and `neg` if it evaluates to
   * `False`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/If.md">If
   *      documentation</a>
   */
  public final static IBuiltInSymbol If = F.initFinalSymbol("If", ID.If);

  public final static IBuiltInSymbol IgnoreCase = F.initFinalSymbol("IgnoreCase", ID.IgnoreCase);

  /**
   * Im(z) - returns the imaginary component of the complex number `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Im.md">Im
   *      documentation</a>
   */
  public final static IBuiltInSymbol Im = F.initFinalSymbol("Im", ID.Im);

  public final static IBuiltInSymbol Image = F.initFinalSymbol("Image", ID.Image);

  public final static IBuiltInSymbol ImageChannels =
      F.initFinalSymbol("ImageChannels", ID.ImageChannels);

  public final static IBuiltInSymbol ImageColorSpace =
      F.initFinalSymbol("ImageColorSpace", ID.ImageColorSpace);

  public final static IBuiltInSymbol ImageCrop = F.initFinalSymbol("ImageCrop", ID.ImageCrop);

  public final static IBuiltInSymbol ImageData = F.initFinalSymbol("ImageData", ID.ImageData);

  public final static IBuiltInSymbol ImageDimensions =
      F.initFinalSymbol("ImageDimensions", ID.ImageDimensions);

  public final static IBuiltInSymbol ImageQ = F.initFinalSymbol("ImageQ", ID.ImageQ);

  public final static IBuiltInSymbol ImageResize = F.initFinalSymbol("ImageResize", ID.ImageResize);

  public final static IBuiltInSymbol ImageRotate = F.initFinalSymbol("ImageRotate", ID.ImageRotate);

  public final static IBuiltInSymbol ImageScaled = F.initFinalSymbol("ImageScaled", ID.ImageScaled);

  public final static IBuiltInSymbol ImageSize = F.initFinalSymbol("ImageSize", ID.ImageSize);

  public final static IBuiltInSymbol ImageType = F.initFinalSymbol("ImageType", ID.ImageType);

  /**
   * Implies(arg1, arg2) - Logical implication.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Implies.md">Implies
   *      documentation</a>
   */
  public final static IBuiltInSymbol Implies = F.initFinalSymbol("Implies", ID.Implies);

  /**
   * Import("path-to-filename", "WXF") - if the file system is enabled, import an expression in WXF
   * format from the "path-to-filename" file.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Import.md">Import
   *      documentation</a>
   */
  public final static IBuiltInSymbol Import = F.initFinalSymbol("Import", ID.Import);

  public final static IBuiltInSymbol ImportString =
      F.initFinalSymbol("ImportString", ID.ImportString);

  /**
   * In(k) - gives the `k`th line of input.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/In.md">In
   *      documentation</a>
   */
  public final static IBuiltInSymbol In = F.initFinalSymbol("In", ID.In);

  /**
   * Increment(x) - increments `x` by `1`, returning the original value of `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Increment.md">Increment
   *      documentation</a>
   */
  public final static IBuiltInSymbol Increment = F.initFinalSymbol("Increment", ID.Increment);

  /**
   * Indeterminate - represents an indeterminate result.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Indeterminate.md">Indeterminate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Indeterminate =
      F.initFinalSymbol("Indeterminate", ID.Indeterminate);

  public final static IBuiltInSymbol Inequality = F.initFinalSymbol("Inequality", ID.Inequality);

  /**
   * InexactNumberQ(expr) - returns `True` if `expr` is not an exact number, and `False` otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InexactNumberQ.md">InexactNumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol InexactNumberQ =
      F.initFinalSymbol("InexactNumberQ", ID.InexactNumberQ);

  /**
   * Infinity - represents an infinite real quantity.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Infinity.md">Infinity
   *      documentation</a>
   */
  public final static IBuiltInSymbol Infinity = F.initFinalSymbol("Infinity", ID.Infinity);

  public final static IBuiltInSymbol Infix = F.initFinalSymbol("Infix", ID.Infix);

  public final static IBuiltInSymbol Information = F.initFinalSymbol("Information", ID.Information);

  public final static IBuiltInSymbol Inherited = F.initFinalSymbol("Inherited", ID.Inherited);

  /**
   * Inner(f, x, y, g) - computes a generalized inner product of `x` and `y`, using a multiplication
   * function `f` and an addition function `g`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Inner.md">Inner
   *      documentation</a>
   */
  public final static IBuiltInSymbol Inner = F.initFinalSymbol("Inner", ID.Inner);

  /**
   * Input() - if the file system is enabled, the user can input an expression. After input this
   * expression will be evaluated immediately.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Input.md">Input
   *      documentation</a>
   */
  public final static IBuiltInSymbol Input = F.initFinalSymbol("Input", ID.Input);

  public final static IBuiltInSymbol InputField = F.initFinalSymbol("InputField", ID.InputField);

  /**
   * InputForm(expr) - print the `expr` as if it should be inserted by the user for evaluation.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InputForm.md">InputForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol InputForm = F.initFinalSymbol("InputForm", ID.InputForm);

  public final static IBuiltInSymbol InputStream = F.initFinalSymbol("InputStream", ID.InputStream);

  /**
   * InputString() - if the file system is enabled, the user can input a string.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InputString.md">InputString
   *      documentation</a>
   */
  public final static IBuiltInSymbol InputString = F.initFinalSymbol("InputString", ID.InputString);

  /**
   * Insert(list, elem, n) - inserts `elem` at position `n` in `list`. When `n` is negative, the
   * position is counted from the end.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Insert.md">Insert
   *      documentation</a>
   */
  public final static IBuiltInSymbol Insert = F.initFinalSymbol("Insert", ID.Insert);

  public final static IBuiltInSymbol InsertionFunction =
      F.initFinalSymbol("InsertionFunction", ID.InsertionFunction);

  public final static IBuiltInSymbol InstallJava = F.initFinalSymbol("InstallJava", ID.InstallJava);

  /**
   * InstanceOf[java-object, "class-name"] - return the result of the Java expression `java-object
   * instanceof class`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InstanceOf.md">InstanceOf
   *      documentation</a>
   */
  public final static IBuiltInSymbol InstanceOf = F.initFinalSymbol("InstanceOf", ID.InstanceOf);

  /**
   * Integer - is the head of integers.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Integer.md">Integer
   *      documentation</a>
   */
  public final static IBuiltInSymbol Integer = F.initFinalSymbol("Integer", ID.Integer);

  /**
   * IntegerDigits(n, base) - returns a list of integer digits for `n` under `base`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerDigits.md">IntegerDigits
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntegerDigits =
      F.initFinalSymbol("IntegerDigits", ID.IntegerDigits);

  /**
   * IntegerExponent(n, b) - gives the highest exponent of `b` that divides `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerExponent.md">IntegerExponent
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntegerExponent =
      F.initFinalSymbol("IntegerExponent", ID.IntegerExponent);

  /**
   * IntegerLength(x) - gives the number of digits in the base-10 representation of `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerLength.md">IntegerLength
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntegerLength =
      F.initFinalSymbol("IntegerLength", ID.IntegerLength);

  /**
   * IntegerName(integer-number) - gives the spoken number string of `integer-number` in language
   * `English`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerName.md">IntegerName
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntegerName = F.initFinalSymbol("IntegerName", ID.IntegerName);

  /**
   * IntegerPart(expr) - for real `expr` return the integer part of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerPart.md">IntegerPart
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntegerPart = F.initFinalSymbol("IntegerPart", ID.IntegerPart);

  /**
   * IntegerPartitions(n) - returns all partitions of the integer `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerPartitions.md">IntegerPartitions
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntegerPartitions =
      F.initFinalSymbol("IntegerPartitions", ID.IntegerPartitions);

  /**
   * IntegerQ(expr) - returns `True` if `expr` is an integer, and `False` otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerQ.md">IntegerQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntegerQ = F.initFinalSymbol("IntegerQ", ID.IntegerQ);

  /**
   * Integers - is the set of integer numbers.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Integers.md">Integers
   *      documentation</a>
   */
  public final static IBuiltInSymbol Integers = F.initFinalSymbol("Integers", ID.Integers);

  /**
   * Integrate(f, x) - integrates `f` with respect to `x`. The result does not contain the additive
   * integration constant.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Integrate.md">Integrate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Integrate = F.initFinalSymbol("Integrate", ID.Integrate);

  /**
   * InterpolatingFunction(data-list) - get the representation for the given `data-list` as
   * piecewise `InterpolatingPolynomial`s.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterpolatingFunction.md">InterpolatingFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol InterpolatingFunction =
      F.initFinalSymbol("InterpolatingFunction", ID.InterpolatingFunction);

  /**
   * InterpolatingPolynomial(data-list, symbol) - get the polynomial representation for the given
   * `data-list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterpolatingPolynomial.md">InterpolatingPolynomial
   *      documentation</a>
   */
  public final static IBuiltInSymbol InterpolatingPolynomial =
      F.initFinalSymbol("InterpolatingPolynomial", ID.InterpolatingPolynomial);

  public final static IBuiltInSymbol Interpolation =
      F.initFinalSymbol("Interpolation", ID.Interpolation);

  /**
   * InterquartileRange(list) - returns the interquartile range (IQR), which is between upper and
   * lower quartiles, IQR = Q3 − Q1.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterquartileRange.md">InterquartileRange
   *      documentation</a>
   */
  public final static IBuiltInSymbol InterquartileRange =
      F.initFinalSymbol("InterquartileRange", ID.InterquartileRange);

  /**
   * Interrupt( ) - Interrupt an evaluation and returns `$Aborted`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Interrupt.md">Interrupt
   *      documentation</a>
   */
  public final static IBuiltInSymbol Interrupt = F.initFinalSymbol("Interrupt", ID.Interrupt);

  public final static IBuiltInSymbol IntersectingQ =
      F.initFinalSymbol("IntersectingQ", ID.IntersectingQ);

  /**
   * Intersection(set1, set2, ...) - get the intersection set from `set1` and `set2` ....
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Intersection.md">Intersection
   *      documentation</a>
   */
  public final static IBuiltInSymbol Intersection =
      F.initFinalSymbol("Intersection", ID.Intersection);

  /**
   * Interval({a, b}) - represents the interval from `a` to `b`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Interval.md">Interval
   *      documentation</a>
   */
  public final static IBuiltInSymbol Interval = F.initFinalSymbol("Interval", ID.Interval);

  /**
   * IntervalIntersection(interval_1, interval_2, ...) - compute the intersection of the intervals
   * `interval_1, interval_2, ...`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalIntersection.md">IntervalIntersection
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntervalIntersection =
      F.initFinalSymbol("IntervalIntersection", ID.IntervalIntersection);

  /**
   * IntervalMemberQ(interval, interval-or-real-number) - returns `True`, if
   * `interval-or-real-number` is completly sourrounded by `interval`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalMemberQ.md">IntervalMemberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntervalMemberQ =
      F.initFinalSymbol("IntervalMemberQ", ID.IntervalMemberQ);

  /**
   * IntervalUnion(interval_1, interval_2, ...) - compute the union of the intervals `interval_1,
   * interval_2, ...`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalUnion.md">IntervalUnion
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntervalUnion =
      F.initFinalSymbol("IntervalUnion", ID.IntervalUnion);

  /**
   * Inverse(matrix) - computes the inverse of the `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Inverse.md">Inverse
   *      documentation</a>
   */
  public final static IBuiltInSymbol Inverse = F.initFinalSymbol("Inverse", ID.Inverse);

  public final static IBuiltInSymbol InverseBetaRegularized =
      F.initFinalSymbol("InverseBetaRegularized", ID.InverseBetaRegularized);

  /**
   * InverseCDF(dist, q) - returns the inverse cumulative distribution for the distribution `dist`
   * as a function of `q`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseCDF.md">InverseCDF
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseCDF = F.initFinalSymbol("InverseCDF", ID.InverseCDF);

  /**
   * InverseErf(z) - returns the inverse error function of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseErf.md">InverseErf
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseErf = F.initFinalSymbol("InverseErf", ID.InverseErf);

  /**
   * InverseErfc(z) - returns the inverse complementary error function of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseErfc.md">InverseErfc
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseErfc = F.initFinalSymbol("InverseErfc", ID.InverseErfc);

  /**
   * InverseFourier(vector-of-complex-numbers) - Inverse discrete Fourier transform of a
   * `vector-of-complex-numbers`. Fourier transform is restricted to vectors with length of power of
   * 2.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseFourier.md">InverseFourier
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseFourier =
      F.initFinalSymbol("InverseFourier", ID.InverseFourier);

  /**
   * InverseFunction(head) - returns the inverse function for the symbol `head`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseFunction.md">InverseFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseFunction =
      F.initFinalSymbol("InverseFunction", ID.InverseFunction);

  public final static IBuiltInSymbol InverseGammaRegularized =
      F.initFinalSymbol("InverseGammaRegularized", ID.InverseGammaRegularized);

  /**
   * InverseGudermannian(expr) - computes the inverse gudermannian function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseGudermannian.md">InverseGudermannian
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseGudermannian =
      F.initFinalSymbol("InverseGudermannian", ID.InverseGudermannian);

  /**
   * InverseHaversine(z) - returns the inverse haversine function of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseHaversine.md">InverseHaversine
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseHaversine =
      F.initFinalSymbol("InverseHaversine", ID.InverseHaversine);

  /**
   * InverseLaplaceTransform(f,s,t) - returns the inverse laplace transform.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseLaplaceTransform.md">InverseLaplaceTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseLaplaceTransform =
      F.initFinalSymbol("InverseLaplaceTransform", ID.InverseLaplaceTransform);

  /**
   * InverseSeries( series ) - return the inverse series.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseSeries.md">InverseSeries
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseSeries =
      F.initFinalSymbol("InverseSeries", ID.InverseSeries);

  public final static IBuiltInSymbol InverseWeierstrassP =
      F.initFinalSymbol("InverseWeierstrassP", ID.InverseWeierstrassP);

  /**
   * IsomorphicGraphQ(graph1, graph2) - returns `True` if an isomorphism exists between `graph1` and
   * `graph2`. Return `False`in all other cases.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IsomorphicGraphQ.md">IsomorphicGraphQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol IsomorphicGraphQ =
      F.initFinalSymbol("IsomorphicGraphQ", ID.IsomorphicGraphQ);

  /**
   * JSForm(expr) - returns the JavaScript form of the `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JSForm.md">JSForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol JSForm = F.initFinalSymbol("JSForm", ID.JSForm);

  public final static IBuiltInSymbol JSFormData = F.initFinalSymbol("JSFormData", ID.JSFormData);

  /**
   * JaccardDissimilarity(u, v) - returns the Jaccard-Needham dissimilarity between the two boolean
   * 1-D lists `u` and `v`, which is defined as `(c_tf + c_ft) / (c_tt + c_ft + c_tf)`, where n is
   * `len(u)` and `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JaccardDissimilarity.md">JaccardDissimilarity
   *      documentation</a>
   */
  public final static IBuiltInSymbol JaccardDissimilarity =
      F.initFinalSymbol("JaccardDissimilarity", ID.JaccardDissimilarity);

  /**
   * JacobiAmplitude(x, m) - returns the amplitude `am(x, m)` for Jacobian elliptic function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiAmplitude.md">JacobiAmplitude
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiAmplitude =
      F.initFinalSymbol("JacobiAmplitude", ID.JacobiAmplitude);

  /**
   * JacobiCD(x, m) - returns the Jacobian elliptic function `cd(x, m)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiCD.md">JacobiCD
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiCD = F.initFinalSymbol("JacobiCD", ID.JacobiCD);

  /**
   * JacobiCN(x, m) - returns the Jacobian elliptic function `cn(x, m)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiCN.md">JacobiCN
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiCN = F.initFinalSymbol("JacobiCN", ID.JacobiCN);

  public final static IBuiltInSymbol JacobiDC = F.initFinalSymbol("JacobiDC", ID.JacobiDC);

  /**
   * JacobiDN(x, m) - returns the Jacobian elliptic function `dn(x, m)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiDN.md">JacobiDN
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiDN = F.initFinalSymbol("JacobiDN", ID.JacobiDN);

  public final static IBuiltInSymbol JacobiEpsilon =
      F.initFinalSymbol("JacobiEpsilon", ID.JacobiEpsilon);

  /**
   * JacobiMatrix(matrix, var) - creates a Jacobian matrix.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiMatrix.md">JacobiMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiMatrix =
      F.initFinalSymbol("JacobiMatrix", ID.JacobiMatrix);

  public final static IBuiltInSymbol JacobiNC = F.initFinalSymbol("JacobiNC", ID.JacobiNC);

  public final static IBuiltInSymbol JacobiND = F.initFinalSymbol("JacobiND", ID.JacobiND);

  /**
   * JacobiSC(x, m) - returns the Jacobian elliptic function `sc(x, m)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSC.md">JacobiSC
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiSC = F.initFinalSymbol("JacobiSC", ID.JacobiSC);

  /**
   * JacobiSD(x, m) - returns the Jacobian elliptic function `sd(x, m)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSD.md">JacobiSD
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiSD = F.initFinalSymbol("JacobiSD", ID.JacobiSD);

  /**
   * JacobiSN(x, m) - returns the Jacobian elliptic function `sn(x, m)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSN.md">JacobiSN
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiSN = F.initFinalSymbol("JacobiSN", ID.JacobiSN);

  /**
   * JacobiSymbol(m, n) - calculates the Jacobi symbol.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSymbol.md">JacobiSymbol
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiSymbol =
      F.initFinalSymbol("JacobiSymbol", ID.JacobiSymbol);

  public final static IBuiltInSymbol JacobiZeta = F.initFinalSymbol("JacobiZeta", ID.JacobiZeta);

  /**
   * JavaClass[class-name] - a `JavaClass` expression can be created with the `LoadJavaClass`
   * function and wraps a Java `java.lang.Class` object. All static method names are assigned to a
   * context which will be created by the last part of the class name.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaClass.md">JavaClass
   *      documentation</a>
   */
  public final static IBuiltInSymbol JavaClass = F.initFinalSymbol("JavaClass", ID.JavaClass);

  /**
   * JavaForm(expr) - returns the Symja Java form of the `expr`. In Java you can use the created
   * Symja expressions.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaForm.md">JavaForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol JavaForm = F.initFinalSymbol("JavaForm", ID.JavaForm);

  /**
   * JavaObject[class className] - a `JavaObject` can be created with the `JavaNew` function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaNew.md">JavaNew
   *      documentation</a>
   */
  public final static IBuiltInSymbol JavaNew = F.initFinalSymbol("JavaNew", ID.JavaNew);

  /**
   * JavaNew["class-name"] - create a `JavaObject` from the `class-name` default constructor.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaObject.md">JavaObject
   *      documentation</a>
   */
  public final static IBuiltInSymbol JavaObject = F.initFinalSymbol("JavaObject", ID.JavaObject);

  /**
   * JavaObjectQ[java-object] - return `True` if `java-object` is a `JavaObject` expression.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaObjectQ.md">JavaObjectQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol JavaObjectQ = F.initFinalSymbol("JavaObjectQ", ID.JavaObjectQ);

  /**
   * JavaShow[ java.awt.Window ] - show the `JavaObject` which has to be an instance of
   * `java.awt.Window`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaShow.md">JavaShow
   *      documentation</a>
   */
  public final static IBuiltInSymbol JavaShow = F.initFinalSymbol("JavaShow", ID.JavaShow);

  /**
   * Join(l1, l2) - concatenates the lists `l1` and `l2`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Join.md">Join
   *      documentation</a>
   */
  public final static IBuiltInSymbol Join = F.initFinalSymbol("Join", ID.Join);

  public final static IBuiltInSymbol KOrderlessPartitions =
      F.initFinalSymbol("KOrderlessPartitions", ID.KOrderlessPartitions);

  public final static IBuiltInSymbol KPartitions = F.initFinalSymbol("KPartitions", ID.KPartitions);

  public final static IBuiltInSymbol KelvinBei = F.initFinalSymbol("KelvinBei", ID.KelvinBei);

  public final static IBuiltInSymbol KelvinBer = F.initFinalSymbol("KelvinBer", ID.KelvinBer);

  /**
   * Key(key) - represents a `key` used to access a value in an association.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Key.md">Key
   *      documentation</a>
   */
  public final static IBuiltInSymbol Key = F.initFinalSymbol("Key", ID.Key);

  public final static IBuiltInSymbol KeyAbsent = F.initFinalSymbol("KeyAbsent", ID.KeyAbsent);

  public final static IBuiltInSymbol KeyExistsQ = F.initFinalSymbol("KeyExistsQ", ID.KeyExistsQ);

  /**
   * KeySelect(<|key1->value1, ...|>, head) - returns an association of the elements for which
   * `head(keyi)` returns `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeySelect.md">KeySelect
   *      documentation</a>
   */
  public final static IBuiltInSymbol KeySelect = F.initFinalSymbol("KeySelect", ID.KeySelect);

  /**
   * KeySort(<|key1->value1, ...|>) - sort the `<|key1->value1, ...|>` entries by the `key` values.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeySort.md">KeySort
   *      documentation</a>
   */
  public final static IBuiltInSymbol KeySort = F.initFinalSymbol("KeySort", ID.KeySort);

  /**
   * KeyTake(<|key1->value1, ...|>, {k1, k2,...}) - returns an association of the rules for which
   * the `k1, k2,...` are keys in the association.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeyTake.md">KeyTake
   *      documentation</a>
   */
  public final static IBuiltInSymbol KeyTake = F.initFinalSymbol("KeyTake", ID.KeyTake);

  /**
   * Keys(association) - return a list of keys of the `association`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Keys.md">Keys
   *      documentation</a>
   */
  public final static IBuiltInSymbol Keys = F.initFinalSymbol("Keys", ID.Keys);

  /**
   * Khinchin - Khinchin's constant
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Khinchin.md">Khinchin
   *      documentation</a>
   */
  public final static IBuiltInSymbol Khinchin = F.initFinalSymbol("Khinchin", ID.Khinchin);

  public final static IBuiltInSymbol KleinInvariantJ =
      F.initFinalSymbol("KleinInvariantJ", ID.KleinInvariantJ);

  public final static IBuiltInSymbol KnownUnitQ = F.initFinalSymbol("KnownUnitQ", ID.KnownUnitQ);

  /**
   * KolmogorovSmirnovTest(data) - Computes the `p-value`, or <i>observed significance level</i>, of
   * a one-sample [Wikipedia:Kolmogorov-Smirnov
   * test](http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test) evaluating the null hypothesis that
   * `data` conforms to the `NormalDistribution()`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KolmogorovSmirnovTest.md">KolmogorovSmirnovTest
   *      documentation</a>
   */
  public final static IBuiltInSymbol KolmogorovSmirnovTest =
      F.initFinalSymbol("KolmogorovSmirnovTest", ID.KolmogorovSmirnovTest);

  /**
   * KroneckerDelta(arg1, arg2, ... argN) - if all arguments `arg1` to `argN` are equal return `1`,
   * otherwise return `0`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KroneckerDelta.md">KroneckerDelta
   *      documentation</a>
   */
  public final static IBuiltInSymbol KroneckerDelta =
      F.initFinalSymbol("KroneckerDelta", ID.KroneckerDelta);

  public final static IBuiltInSymbol KroneckerProduct =
      F.initFinalSymbol("KroneckerProduct", ID.KroneckerProduct);

  /**
   * Kurtosis(list) - gives the Pearson measure of kurtosis for `list` (a measure of existing
   * outliers).
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Kurtosis.md">Kurtosis
   *      documentation</a>
   */
  public final static IBuiltInSymbol Kurtosis = F.initFinalSymbol("Kurtosis", ID.Kurtosis);

  /**
   * LCM(n1, n2, ...) - computes the least common multiple of the given integers.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LCM.md">LCM
   *      documentation</a>
   */
  public final static IBuiltInSymbol LCM = F.initFinalSymbol("LCM", ID.LCM);

  /**
   * LUDecomposition(matrix) - calculate the LUP-decomposition of a square `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LUDecomposition.md">LUDecomposition
   *      documentation</a>
   */
  public final static IBuiltInSymbol LUDecomposition =
      F.initFinalSymbol("LUDecomposition", ID.LUDecomposition);

  public final static IBuiltInSymbol Labeled = F.initFinalSymbol("Labeled", ID.Labeled);

  public final static IBuiltInSymbol LabelingFunction =
      F.initFinalSymbol("LabelingFunction", ID.LabelingFunction);

  public final static IBuiltInSymbol LabelingSize =
      F.initFinalSymbol("LabelingSize", ID.LabelingSize);

  /**
   * LaguerreL(n, x) - returns the Laguerre polynomial `L_n(x)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaguerreL.md">LaguerreL
   *      documentation</a>
   */
  public final static IBuiltInSymbol LaguerreL = F.initFinalSymbol("LaguerreL", ID.LaguerreL);

  public final static IBuiltInSymbol LambertW = F.initFinalSymbol("LambertW", ID.LambertW);

  /**
   * LaplaceTransform(f,t,s) - returns the laplace transform.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaplaceTransform.md">LaplaceTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol LaplaceTransform =
      F.initFinalSymbol("LaplaceTransform", ID.LaplaceTransform);

  /**
   * Last(expr) - returns the last element in `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Last.md">Last
   *      documentation</a>
   */
  public final static IBuiltInSymbol Last = F.initFinalSymbol("Last", ID.Last);

  /**
   * LeafCount(expr) - returns the total number of indivisible subexpressions in `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeafCount.md">LeafCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeafCount = F.initFinalSymbol("LeafCount", ID.LeafCount);

  /**
   * LeastSquares(matrix, right) - solves the linear least-squares problem 'matrix . x = right'.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeastSquares.md">LeastSquares
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeastSquares =
      F.initFinalSymbol("LeastSquares", ID.LeastSquares);

  public final static IBuiltInSymbol Left = F.initFinalSymbol("Left", ID.Left);

  /**
   * LegendreP(n, x) - returns the Legendre polynomial `P_n(x)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendreP.md">LegendreP
   *      documentation</a>
   */
  public final static IBuiltInSymbol LegendreP = F.initFinalSymbol("LegendreP", ID.LegendreP);

  /**
   * LegendreQ(n, x) - returns the Legendre functions of the second kind `Q_n(x)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendreQ.md">LegendreQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol LegendreQ = F.initFinalSymbol("LegendreQ", ID.LegendreQ);

  /**
   * Length(expr) - returns the number of leaves in `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Length.md">Length
   *      documentation</a>
   */
  public final static IBuiltInSymbol Length = F.initFinalSymbol("Length", ID.Length);

  /**
   * LengthWhile({e1, e2, ...}, head) - returns the number of elements `ei` at the start of list for
   * which `head(ei)` returns `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LengthWhile.md">LengthWhile
   *      documentation</a>
   */
  public final static IBuiltInSymbol LengthWhile = F.initFinalSymbol("LengthWhile", ID.LengthWhile);

  /**
   * Less(x, y) - yields `True` if `x` is known to be less than `y`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Less.md">Less
   *      documentation</a>
   */
  public final static IBuiltInSymbol Less = F.initFinalSymbol("Less", ID.Less);

  /**
   * LessEqual(x, y) - yields `True` if `x` is known to be less than or equal `y`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqual.md">LessEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol LessEqual = F.initFinalSymbol("LessEqual", ID.LessEqual);

  public final static IBuiltInSymbol LessEqualThan =
      F.initFinalSymbol("LessEqualThan", ID.LessEqualThan);

  public final static IBuiltInSymbol LessThan = F.initFinalSymbol("LessThan", ID.LessThan);

  /**
   * LetterCharacter - represents letters..
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterCharacter.md">LetterCharacter
   *      documentation</a>
   */
  public final static IBuiltInSymbol LetterCharacter =
      F.initFinalSymbol("LetterCharacter", ID.LetterCharacter);

  /**
   * LetterCounts(string) - count the number of each distinct character in the `string` and return
   * the result as an association `<|char->counter1, ...|>`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterCounts.md">LetterCounts
   *      documentation</a>
   */
  public final static IBuiltInSymbol LetterCounts =
      F.initFinalSymbol("LetterCounts", ID.LetterCounts);

  /**
   * LetterNumber(character) - returns the position of the `character` in the English alphabet.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterNumber.md">LetterNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol LetterNumber =
      F.initFinalSymbol("LetterNumber", ID.LetterNumber);

  /**
   * LetterQ(expr) - tests whether `expr` is a string, which only contains letters.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterQ.md">LetterQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol LetterQ = F.initFinalSymbol("LetterQ", ID.LetterQ);

  /**
   * Level(expr, levelspec) - gives a list of all sub-expressions of `expr` at the level(s)
   * specified by `levelspec`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Level.md">Level
   *      documentation</a>
   */
  public final static IBuiltInSymbol Level = F.initFinalSymbol("Level", ID.Level);

  /**
   * LevelQ(expr) - tests whether `expr` is a valid level specification.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LevelQ.md">LevelQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol LevelQ = F.initFinalSymbol("LevelQ", ID.LevelQ);

  /**
   * LeviCivitaTensor(n) - returns the `n`-dimensional Levi-Civita tensor as sparse array. The
   * Levi-Civita symbol represents a collection of numbers; defined from the sign of a permutation
   * of the natural numbers `1, 2, …, n`, for some positive integer `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeviCivitaTensor.md">LeviCivitaTensor
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeviCivitaTensor =
      F.initFinalSymbol("LeviCivitaTensor", ID.LeviCivitaTensor);

  public final static IBuiltInSymbol Lexicographic =
      F.initFinalSymbol("Lexicographic", ID.Lexicographic);

  public final static IBuiltInSymbol LightBlue = F.initFinalSymbol("LightBlue", ID.LightBlue);

  public final static IBuiltInSymbol LightBrown = F.initFinalSymbol("LightBrown", ID.LightBrown);

  public final static IBuiltInSymbol LightCyan = F.initFinalSymbol("LightCyan", ID.LightCyan);

  public final static IBuiltInSymbol LightGray = F.initFinalSymbol("LightGray", ID.LightGray);

  public final static IBuiltInSymbol LightGreen = F.initFinalSymbol("LightGreen", ID.LightGreen);

  public final static IBuiltInSymbol LightMagenta =
      F.initFinalSymbol("LightMagenta", ID.LightMagenta);

  public final static IBuiltInSymbol LightOrange = F.initFinalSymbol("LightOrange", ID.LightOrange);

  public final static IBuiltInSymbol LightPink = F.initFinalSymbol("LightPink", ID.LightPink);

  public final static IBuiltInSymbol LightPurple = F.initFinalSymbol("LightPurple", ID.LightPurple);

  public final static IBuiltInSymbol LightRed = F.initFinalSymbol("LightRed", ID.LightRed);

  public final static IBuiltInSymbol LightYellow = F.initFinalSymbol("LightYellow", ID.LightYellow);

  public final static IBuiltInSymbol Lighting = F.initFinalSymbol("Lighting", ID.Lighting);

  /**
   * Limit(expr, x->x0) - gives the limit of `expr` as `x` approaches `x0`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Limit.md">Limit
   *      documentation</a>
   */
  public final static IBuiltInSymbol Limit = F.initFinalSymbol("Limit", ID.Limit);

  public final static IBuiltInSymbol Line = F.initFinalSymbol("Line", ID.Line);

  public final static IBuiltInSymbol LineGraph = F.initFinalSymbol("LineGraph", ID.LineGraph);

  /**
   * LinearModelFit(list-of-data-points, expr, symbol) - In statistics, linear regression is a
   * linear approach to modeling the relationship between a scalar response (or dependent variable)
   * and one or more explanatory variables (or independent variables).
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearModelFit.md">LinearModelFit
   *      documentation</a>
   */
  public final static IBuiltInSymbol LinearModelFit =
      F.initFinalSymbol("LinearModelFit", ID.LinearModelFit);

  /**
   * LinearProgramming(coefficientsOfLinearObjectiveFunction, constraintList,
   * constraintRelationList) - the `LinearProgramming` function provides an implementation of
   * [George Dantzig's simplex algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for
   * solving linear optimization problems with linear equality and inequality constraints and
   * implicit non-negative variables.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearProgramming.md">LinearProgramming
   *      documentation</a>
   */
  public final static IBuiltInSymbol LinearProgramming =
      F.initFinalSymbol("LinearProgramming", ID.LinearProgramming);

  /**
   * LinearRecurrence(list1, list2, n) - solve the linear recurrence and return the generated
   * sequence of elements.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearRecurrence.md">LinearRecurrence
   *      documentation</a>
   */
  public final static IBuiltInSymbol LinearRecurrence =
      F.initFinalSymbol("LinearRecurrence", ID.LinearRecurrence);

  /**
   * LinearSolve(matrix, right) - solves the linear equation system 'matrix . x = right' and returns
   * one corresponding solution `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearSolve.md">LinearSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol LinearSolve = F.initFinalSymbol("LinearSolve", ID.LinearSolve);

  public final static IBuiltInSymbol LinearSolveFunction =
      F.initFinalSymbol("LinearSolveFunction", ID.LinearSolveFunction);

  public final static IBuiltInSymbol LiouvilleLambda =
      F.initFinalSymbol("LiouvilleLambda", ID.LiouvilleLambda);

  /**
   * List(e1, e2, ..., ei) - represents a list containing the elements `e1...ei`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/List.md">List
   *      documentation</a>
   */
  public final static IBuiltInSymbol List = F.initFinalSymbol("List", ID.List);

  public final static IBuiltInSymbol ListContourPlot =
      F.initFinalSymbol("ListContourPlot", ID.ListContourPlot);

  /**
   * ListConvolve(kernel-list, tensor-list) - create the convolution of the `kernel-list` with
   * `tensor-list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListConvolve.md">ListConvolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListConvolve =
      F.initFinalSymbol("ListConvolve", ID.ListConvolve);

  /**
   * ListCorrelate(kernel-list, tensor-list) - create the correlation of the `kernel-list` with
   * `tensor-list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListCorrelate.md">ListCorrelate
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListCorrelate =
      F.initFinalSymbol("ListCorrelate", ID.ListCorrelate);

  /**
   * ListLinePlot( { list-of-points } ) - generate a JavaScript list line plot control for the
   * `list-of-points`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLinePlot.md">ListLinePlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListLinePlot =
      F.initFinalSymbol("ListLinePlot", ID.ListLinePlot);

  public final static IBuiltInSymbol ListLinePlot3D =
      F.initFinalSymbol("ListLinePlot3D", ID.ListLinePlot3D);

  /**
   * ListPlot( { list-of-points } ) - generate a JavaScript list plot control for the
   * `list-of-points`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListPlot.md">ListPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListPlot = F.initFinalSymbol("ListPlot", ID.ListPlot);

  /**
   * ListPlot3D( { list-of-polygons } ) - generate a JavaScript list plot 3D control for the
   * `list-of-polygons`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListPlot3D.md">ListPlot3D
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListPlot3D = F.initFinalSymbol("ListPlot3D", ID.ListPlot3D);

  /**
   * ListPointPlot3D( { list-of-points } ) - generate a JavaScript list plot 3D control for the
   * `list-of-points`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListPointPlot3D.md">ListPointPlot3D
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListPointPlot3D =
      F.initFinalSymbol("ListPointPlot3D", ID.ListPointPlot3D);

  /**
   * ListQ(expr) - tests whether `expr` is a `List`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListQ.md">ListQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListQ = F.initFinalSymbol("ListQ", ID.ListQ);

  /**
   * Listable - is an attribute specifying that a function should be automatically applied to each
   * element of a list.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Listable.md">Listable
   *      documentation</a>
   */
  public final static IBuiltInSymbol Listable = F.initFinalSymbol("Listable", ID.Listable);

  public final static IBuiltInSymbol Literal = F.initFinalSymbol("Literal", ID.Literal);

  /**
   * LoadJavaClass["class-name"] - loads the class with the specified `class-name` and return a
   * `JavaClass` expression. All static method names are assigned to a context which will be created
   * by the last part of the class name.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LoadJavaClass.md">LoadJavaClass
   *      documentation</a>
   */
  public final static IBuiltInSymbol LoadJavaClass =
      F.initFinalSymbol("LoadJavaClass", ID.LoadJavaClass);

  /**
   * Log(z) - returns the natural logarithm of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log.md">Log
   *      documentation</a>
   */
  public final static IBuiltInSymbol Log = F.initFinalSymbol("Log", ID.Log);

  /**
   * Log10(z) - returns the base-`10` logarithm of `z`. `Log10(z)` will be converted to
   * `Log(z)/Log(10)` in symbolic mode.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log10.md">Log10
   *      documentation</a>
   */
  public final static IBuiltInSymbol Log10 = F.initFinalSymbol("Log10", ID.Log10);

  /**
   * Log2(z) - returns the base-`2` logarithm of `z`. `Log2(z)` will be converted to `Log(z)/Log(2)`
   * in symbolic mode.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log2.md">Log2
   *      documentation</a>
   */
  public final static IBuiltInSymbol Log2 = F.initFinalSymbol("Log2", ID.Log2);

  /**
   * LogGamma(z) - is the logarithmic gamma function on the complex number `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogGamma.md">LogGamma
   *      documentation</a>
   */
  public final static IBuiltInSymbol LogGamma = F.initFinalSymbol("LogGamma", ID.LogGamma);

  /**
   * LogIntegral(expr) - returns the integral logarithm of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogIntegral.md">LogIntegral
   *      documentation</a>
   */
  public final static IBuiltInSymbol LogIntegral = F.initFinalSymbol("LogIntegral", ID.LogIntegral);

  /**
   * LogNormalDistribution(m, s) - returns a log-normal distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogNormalDistribution.md">LogNormalDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol LogNormalDistribution =
      F.initFinalSymbol("LogNormalDistribution", ID.LogNormalDistribution);

  public final static IBuiltInSymbol LogicalExpand =
      F.initFinalSymbol("LogicalExpand", ID.LogicalExpand);

  /**
   * LogisticSigmoid(z) - returns the logistic sigmoid of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogisticSigmoid.md">LogisticSigmoid
   *      documentation</a>
   */
  public final static IBuiltInSymbol LogisticSigmoid =
      F.initFinalSymbol("LogisticSigmoid", ID.LogisticSigmoid);

  public final static IBuiltInSymbol LongForm = F.initFinalSymbol("LongForm", ID.LongForm);

  public final static IBuiltInSymbol Longest = F.initFinalSymbol("Longest", ID.Longest);

  /**
   * Lookup(association, key) - return the value in the `association` which is associated with the
   * `key`. If no value is available return `Missing("KeyAbsent",key)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Lookup.md">Lookup
   *      documentation</a>
   */
  public final static IBuiltInSymbol Lookup = F.initFinalSymbol("Lookup", ID.Lookup);

  /**
   * LowerCaseQ(str) - is `True` if the given `str` is a string which only contains lower case
   * characters.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerCaseQ.md">LowerCaseQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol LowerCaseQ = F.initFinalSymbol("LowerCaseQ", ID.LowerCaseQ);

  /**
   * LowerTriangularize(matrix) - create a lower triangular matrix from the given `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerTriangularize.md">LowerTriangularize
   *      documentation</a>
   */
  public final static IBuiltInSymbol LowerTriangularize =
      F.initFinalSymbol("LowerTriangularize", ID.LowerTriangularize);

  /**
   * LucasL(n) - gives the `n`th Lucas number.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LucasL.md">LucasL
   *      documentation</a>
   */
  public final static IBuiltInSymbol LucasL = F.initFinalSymbol("LucasL", ID.LucasL);

  /**
   * MachineNumberQ(expr) - returns `True` if `expr` is a machine-precision real or complex number.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MachineNumberQ.md">MachineNumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MachineNumberQ =
      F.initFinalSymbol("MachineNumberQ", ID.MachineNumberQ);

  public final static IBuiltInSymbol Magenta = F.initFinalSymbol("Magenta", ID.Magenta);

  public final static IBuiltInSymbol MakeBoxes = F.initFinalSymbol("MakeBoxes", ID.MakeBoxes);

  /**
   * MangoldtLambda(n) - the von Mangoldt function of `n`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MangoldtLambda.md">MangoldtLambda
   *      documentation</a>
   */
  public final static IBuiltInSymbol MangoldtLambda =
      F.initFinalSymbol("MangoldtLambda", ID.MangoldtLambda);

  /**
   * ManhattanDistance(u, v) - returns the Manhattan distance between `u` and `v`, which is the
   * number of horizontal or vertical moves in the grid like Manhattan city layout to get from `u`
   * to `v`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ManhattanDistance.md">ManhattanDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol ManhattanDistance =
      F.initFinalSymbol("ManhattanDistance", ID.ManhattanDistance);

  /**
   * Manipulate(plot, {x, min, max}) - generate a JavaScript control for the expression `plot` which
   * can be manipulated by a range slider `{x, min, max}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Manipulate.md">Manipulate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Manipulate = F.initFinalSymbol("Manipulate", ID.Manipulate);

  public final static IBuiltInSymbol MantissaExponent =
      F.initFinalSymbol("MantissaExponent", ID.MantissaExponent);

  /**
   * Map(f, expr) or f /@ expr - applies `f` to each part on the first level of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Map.md">Map
   *      documentation</a>
   */
  public final static IBuiltInSymbol Map = F.initFinalSymbol("Map", ID.Map);

  public final static IBuiltInSymbol MapAll = F.initFinalSymbol("MapAll", ID.MapAll);

  public final static IBuiltInSymbol MapAt = F.initFinalSymbol("MapAt", ID.MapAt);

  /**
   * MapIndexed(f, expr) - applies `f` to each part on the first level of `expr` and appending the
   * elements position as a list in the second argument.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MapIndexed.md">MapIndexed
   *      documentation</a>
   */
  public final static IBuiltInSymbol MapIndexed = F.initFinalSymbol("MapIndexed", ID.MapIndexed);

  /**
   * MapThread(f, {{a1, a2, ...}, {b1, b2, ...}, ...}) - returns `{f(a1, b1, ...), f(a2, b2, ...),
   * ...}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MapThread.md">MapThread
   *      documentation</a>
   */
  public final static IBuiltInSymbol MapThread = F.initFinalSymbol("MapThread", ID.MapThread);

  /**
   * MatchQ(expr, form) - tests whether `expr` matches `form`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatchQ.md">MatchQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatchQ = F.initFinalSymbol("MatchQ", ID.MatchQ);

  /**
   * MatchingDissimilarity(u, v) - returns the Matching dissimilarity between the two boolean 1-D
   * lists `u` and `v`, which is defined as `(c_tf + c_ft) / n`, where `n` is `len(u)` and `c_ij` is
   * the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatchingDissimilarity.md">MatchingDissimilarity
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatchingDissimilarity =
      F.initFinalSymbol("MatchingDissimilarity", ID.MatchingDissimilarity);

  /**
   * MathMLForm(expr) - returns the MathML form of the evaluated `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MathMLForm.md">MathMLForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol MathMLForm = F.initFinalSymbol("MathMLForm", ID.MathMLForm);

  public final static IBuiltInSymbol Matrices = F.initFinalSymbol("Matrices", ID.Matrices);

  /**
   * MatrixD(f, X) - gives the matrix derivative of `f` with respect to the matrix `X`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixD.md">MatrixD
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixD = F.initFinalSymbol("MatrixD", ID.MatrixD);

  /**
   * MatrixExp(matrix) - computes the matrix exponential of the square `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixExp.md">MatrixExp
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixExp = F.initFinalSymbol("MatrixExp", ID.MatrixExp);

  /**
   * MatrixForm(matrix) - print a `matrix` or sparse array in matrix form
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixForm.md">MatrixForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixForm = F.initFinalSymbol("MatrixForm", ID.MatrixForm);

  public final static IBuiltInSymbol MatrixFunction =
      F.initFinalSymbol("MatrixFunction", ID.MatrixFunction);

  public final static IBuiltInSymbol MatrixLog = F.initFinalSymbol("MatrixLog", ID.MatrixLog);

  /**
   * MatrixMinimalPolynomial(matrix, var) - computes the matrix minimal polynomial of a `matrix` for
   * the variable `var`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixMinimalPolynomial.md">MatrixMinimalPolynomial
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixMinimalPolynomial =
      F.initFinalSymbol("MatrixMinimalPolynomial", ID.MatrixMinimalPolynomial);

  /**
   * MatrixPlot( matrix ) - create a matrix plot.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixPlot.md">MatrixPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixPlot = F.initFinalSymbol("MatrixPlot", ID.MatrixPlot);

  /**
   * MatrixPower(matrix, n) - computes the `n`th power of a `matrix`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixPower.md">MatrixPower
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixPower = F.initFinalSymbol("MatrixPower", ID.MatrixPower);

  /**
   * MatrixQ(m) - returns `True` if `m` is a list of equal-length lists.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixQ.md">MatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixQ = F.initFinalSymbol("MatrixQ", ID.MatrixQ);

  /**
   * MatrixRank(matrix) - returns the rank of `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixRank.md">MatrixRank
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixRank = F.initFinalSymbol("MatrixRank", ID.MatrixRank);

  /**
   * Max(e_1, e_2, ..., e_i) - returns the expression with the greatest value among the `e_i`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Max.md">Max
   *      documentation</a>
   */
  public final static IBuiltInSymbol Max = F.initFinalSymbol("Max", ID.Max);

  /**
   * MaxFilter(list, r) - filter which evaluates the `Max` of `list` for the radius `r`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MaxFilter.md">MaxFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol MaxFilter = F.initFinalSymbol("MaxFilter", ID.MaxFilter);

  public final static IBuiltInSymbol MaxIterations =
      F.initFinalSymbol("MaxIterations", ID.MaxIterations);

  public final static IBuiltInSymbol MaxMemoryUsed =
      F.initFinalSymbol("MaxMemoryUsed", ID.MaxMemoryUsed);

  public final static IBuiltInSymbol MaxPoints = F.initFinalSymbol("MaxPoints", ID.MaxPoints);

  /**
   * Maximize(unary-function, variable) - returns the maximum of the unary function for the given
   * `variable`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Maximize.md">Maximize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Maximize = F.initFinalSymbol("Maximize", ID.Maximize);

  /**
   * Mean(list) - returns the statistical mean of `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Mean.md">Mean
   *      documentation</a>
   */
  public final static IBuiltInSymbol Mean = F.initFinalSymbol("Mean", ID.Mean);

  public final static IBuiltInSymbol MeanDeviation =
      F.initFinalSymbol("MeanDeviation", ID.MeanDeviation);

  /**
   * MeanFilter(list, r) - filter which evaluates the `Mean` of `list` for the radius `r`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeanFilter.md">MeanFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeanFilter = F.initFinalSymbol("MeanFilter", ID.MeanFilter);

  /**
   * Median(list) - returns the median of `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Median.md">Median
   *      documentation</a>
   */
  public final static IBuiltInSymbol Median = F.initFinalSymbol("Median", ID.Median);

  /**
   * MedianFilter(list, r) - filter which evaluates the `Median` of `list` for the radius `r`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MedianFilter.md">MedianFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol MedianFilter =
      F.initFinalSymbol("MedianFilter", ID.MedianFilter);

  public final static IBuiltInSymbol MeijerG = F.initFinalSymbol("MeijerG", ID.MeijerG);

  /**
   * MemberQ(list, pattern) - returns `True` if pattern matches any element of `list`, or `False`
   * otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MemberQ.md">MemberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MemberQ = F.initFinalSymbol("MemberQ", ID.MemberQ);

  public final static IBuiltInSymbol MemoryAvailable =
      F.initFinalSymbol("MemoryAvailable", ID.MemoryAvailable);

  public final static IBuiltInSymbol MemoryInUse = F.initFinalSymbol("MemoryInUse", ID.MemoryInUse);

  /**
   * MersennePrimeExponent(n) - returns the `n`th mersenne prime exponent. `2^n - 1` must be a prime
   * number.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MersennePrimeExponent.md">MersennePrimeExponent
   *      documentation</a>
   */
  public final static IBuiltInSymbol MersennePrimeExponent =
      F.initFinalSymbol("MersennePrimeExponent", ID.MersennePrimeExponent);

  /**
   * MersennePrimeExponentQ(n) - returns `True` if `2^n - 1` is a prime number. Currently `0 <= n <=
   * 47` can be computed in reasonable time.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MersennePrimeExponentQ.md">MersennePrimeExponentQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MersennePrimeExponentQ =
      F.initFinalSymbol("MersennePrimeExponentQ", ID.MersennePrimeExponentQ);

  public final static IBuiltInSymbol Mesh = F.initFinalSymbol("Mesh", ID.Mesh);

  public final static IBuiltInSymbol MeshRange = F.initFinalSymbol("MeshRange", ID.MeshRange);

  /**
   * Message(symbol::msg, expr1, expr2, ...) - displays the specified message, replacing
   * placeholders in the message text with the corresponding expressions.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Message.md">Message
   *      documentation</a>
   */
  public final static IBuiltInSymbol Message = F.initFinalSymbol("Message", ID.Message);

  /**
   * MessageName(symbol, msg) - `symbol::msg` identifies a message. `MessageName` is the head of
   * message IDs of the form `symbol::tag`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MessageName.md">MessageName
   *      documentation</a>
   */
  public final static IBuiltInSymbol MessageName = F.initFinalSymbol("MessageName", ID.MessageName);

  /**
   * Messages(symbol) - return all messages which are asociated to `symbol`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Messages.md">Messages
   *      documentation</a>
   */
  public final static IBuiltInSymbol Messages = F.initFinalSymbol("Messages", ID.Messages);

  public final static IBuiltInSymbol Method = F.initFinalSymbol("Method", ID.Method);

  /**
   * Min(e_1, e_2, ..., e_i) - returns the expression with the lowest value among the `e_i`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Min.md">Min
   *      documentation</a>
   */
  public final static IBuiltInSymbol Min = F.initFinalSymbol("Min", ID.Min);

  /**
   * MinFilter(list, r) - filter which evaluates the `Min` of `list` for the radius `r`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MinFilter.md">MinFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol MinFilter = F.initFinalSymbol("MinFilter", ID.MinFilter);

  public final static IBuiltInSymbol MinMax = F.initFinalSymbol("MinMax", ID.MinMax);

  public final static IBuiltInSymbol MinimalPolynomial =
      F.initFinalSymbol("MinimalPolynomial", ID.MinimalPolynomial);

  /**
   * Minimize(unary-function, variable) - returns the minimum of the unary function for the given
   * `variable`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Minimize.md">Minimize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Minimize = F.initFinalSymbol("Minimize", ID.Minimize);

  public final static IBuiltInSymbol Minor = F.initFinalSymbol("Minor", ID.Minor);

  public final static IBuiltInSymbol Minors = F.initFinalSymbol("Minors", ID.Minors);

  /**
   * Minus(expr) - is the negation of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Minus.md">Minus
   *      documentation</a>
   */
  public final static IBuiltInSymbol Minus = F.initFinalSymbol("Minus", ID.Minus);

  public final static IBuiltInSymbol Missing = F.initFinalSymbol("Missing", ID.Missing);

  /**
   * MissingQ(expr) - returns `True` if `expr` is a `Missing()` expression.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MissingQ.md">MissingQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MissingQ = F.initFinalSymbol("MissingQ", ID.MissingQ);

  /**
   * Mod(x, m) - returns `x` modulo `m`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Mod.md">Mod
   *      documentation</a>
   */
  public final static IBuiltInSymbol Mod = F.initFinalSymbol("Mod", ID.Mod);

  /**
   * Module({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables`
   * by renaming local variables.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Module.md">Module
   *      documentation</a>
   */
  public final static IBuiltInSymbol Module = F.initFinalSymbol("Module", ID.Module);

  public final static IBuiltInSymbol Modulus = F.initFinalSymbol("Modulus", ID.Modulus);

  /**
   * MoebiusMu(expr) - calculate the Möbius function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoebiusMu.md">MoebiusMu
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoebiusMu = F.initFinalSymbol("MoebiusMu", ID.MoebiusMu);

  /**
   * MonomialList(polynomial, list-of-variables) - get the list of monomials of a `polynomial`
   * expression, with respect to the `list-of-variables`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MonomialList.md">MonomialList
   *      documentation</a>
   */
  public final static IBuiltInSymbol MonomialList =
      F.initFinalSymbol("MonomialList", ID.MonomialList);

  public final static IBuiltInSymbol MonomialOrder =
      F.initFinalSymbol("MonomialOrder", ID.MonomialOrder);

  /**
   * Most(expr) - returns `expr` with the last element removed.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Most.md">Most
   *      documentation</a>
   */
  public final static IBuiltInSymbol Most = F.initFinalSymbol("Most", ID.Most);

  /**
   * Multinomial(n1, n2, ...) - gives the multinomial coefficient `(n1+n2+...)!/(n1! n2! ...)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Multinomial.md">Multinomial
   *      documentation</a>
   */
  public final static IBuiltInSymbol Multinomial = F.initFinalSymbol("Multinomial", ID.Multinomial);

  /**
   * MultiplicativeOrder(a, n) - gives the multiplicative order `a` modulo `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MultiplicativeOrder.md">MultiplicativeOrder
   *      documentation</a>
   */
  public final static IBuiltInSymbol MultiplicativeOrder =
      F.initFinalSymbol("MultiplicativeOrder", ID.MultiplicativeOrder);

  /**
   * MultiplySides(compare-expr, value) - multiplies `value` with all elements of the
   * `compare-expr`. `compare-expr` can be `True`, `False` or a comparison expression with head
   * `Equal, Unequal, Less, LessEqual, Greater, GreaterEqual`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MultiplySides.md">MultiplySides
   *      documentation</a>
   */
  public final static IBuiltInSymbol MultiplySides =
      F.initFinalSymbol("MultiplySides", ID.MultiplySides);

  /**
   * N(expr) - gives the numerical value of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/N.md">N
   *      documentation</a>
   */
  public final static IBuiltInSymbol N = F.initFinalSymbol("N", ID.N);

  /**
   * ND(function, x, value) - returns a numerical approximation of the partial derivative of the
   * `function` for the variable `x` and the given `value`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ND.md">ND
   *      documentation</a>
   */
  public final static IBuiltInSymbol ND = F.initFinalSymbol("ND", ID.ND);

  /**
   * NDSolve({equation-list}, functions, t) - attempts to solve the linear differential
   * `equation-list` for the `functions` and the time-dependent-variable `t`. Returns an
   * `InterpolatingFunction` function object.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NDSolve.md">NDSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol NDSolve = F.initFinalSymbol("NDSolve", ID.NDSolve);

  public final static IBuiltInSymbol NFourierTransform =
      F.initFinalSymbol("NFourierTransform", ID.NFourierTransform);

  /**
   * NHoldAll - is an attribute that protects all arguments of a function from numeric evaluation.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NHoldAll.md">NHoldAll
   *      documentation</a>
   */
  public final static IBuiltInSymbol NHoldAll = F.initFinalSymbol("NHoldAll", ID.NHoldAll);

  /**
   * NHoldFirst - is an attribute that protects the first argument of a function from numeric
   * evaluation.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NHoldFirst.md">NHoldFirst
   *      documentation</a>
   */
  public final static IBuiltInSymbol NHoldFirst = F.initFinalSymbol("NHoldFirst", ID.NHoldFirst);

  /**
   * NHoldRest - is an attribute that protects all but the first argument of a function from numeric
   * evaluation.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NHoldRest.md">NHoldRest
   *      documentation</a>
   */
  public final static IBuiltInSymbol NHoldRest = F.initFinalSymbol("NHoldRest", ID.NHoldRest);

  /**
   * NIntegrate(f, {x,a,b}) - computes the numerical univariate real integral of `f` with respect to
   * `x` from `a` to `b`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NIntegrate.md">NIntegrate
   *      documentation</a>
   */
  public final static IBuiltInSymbol NIntegrate = F.initFinalSymbol("NIntegrate", ID.NIntegrate);

  /**
   * NMaximize({maximize_function, constraints}, variables_list) - the `NMaximize` function provides
   * an implementation of [George Dantzig's simplex
   * algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear optimization
   * problems with linear equality and inequality constraints and implicit non-negative variables.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NMaximize.md">NMaximize
   *      documentation</a>
   */
  public final static IBuiltInSymbol NMaximize = F.initFinalSymbol("NMaximize", ID.NMaximize);

  /**
   * NMinimize({maximize_function, constraints}, variables_list) - the `NMinimize` function provides
   * an implementation of [George Dantzig's simplex
   * algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear optimization
   * problems with linear equality and inequality constraints and implicit non-negative variables.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NMinimize.md">NMinimize
   *      documentation</a>
   */
  public final static IBuiltInSymbol NMinimize = F.initFinalSymbol("NMinimize", ID.NMinimize);

  /**
   * NRoots(polynomial==0) - gives the numerical roots of a univariate polynomial `polynomial`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NRoots.md">NRoots
   *      documentation</a>
   */
  public final static IBuiltInSymbol NRoots = F.initFinalSymbol("NRoots", ID.NRoots);

  /**
   * NSolve(equations, vars) - attempts to solve `equations` for the variables `vars`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NSolve.md">NSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol NSolve = F.initFinalSymbol("NSolve", ID.NSolve);

  /**
   * NakagamiDistribution(m, o) - returns a Nakagami distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NakagamiDistribution.md">NakagamiDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol NakagamiDistribution =
      F.initFinalSymbol("NakagamiDistribution", ID.NakagamiDistribution);

  public final static IBuiltInSymbol NameQ = F.initFinalSymbol("NameQ", ID.NameQ);

  /**
   * Names(string) - return the symbols from the context path matching the `string` or `pattern`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Names.md">Names
   *      documentation</a>
   */
  public final static IBuiltInSymbol Names = F.initFinalSymbol("Names", ID.Names);

  /**
   * Nand(arg1, arg2, ...) - Logical NAND function. It evaluates its arguments in order, giving
   * `True` immediately if any of them are `False`, and `False` if they are all `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nand.md">Nand
   *      documentation</a>
   */
  public final static IBuiltInSymbol Nand = F.initFinalSymbol("Nand", ID.Nand);

  public final static IBuiltInSymbol Nearest = F.initFinalSymbol("Nearest", ID.Nearest);

  public final static IBuiltInSymbol NearestTo = F.initFinalSymbol("NearestTo", ID.NearestTo);

  public final static IBuiltInSymbol Needs = F.initFinalSymbol("Needs", ID.Needs);

  /**
   * Negative(x) - returns `True` if `x` is a negative real number.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Negative.md">Negative
   *      documentation</a>
   */
  public final static IBuiltInSymbol Negative = F.initFinalSymbol("Negative", ID.Negative);

  public final static IBuiltInSymbol NegativeDegreeLexicographic =
      F.initFinalSymbol("NegativeDegreeLexicographic", ID.NegativeDegreeLexicographic);

  public final static IBuiltInSymbol NegativeDegreeReverseLexicographic = F
      .initFinalSymbol("NegativeDegreeReverseLexicographic", ID.NegativeDegreeReverseLexicographic);

  public final static IBuiltInSymbol NegativeLexicographic =
      F.initFinalSymbol("NegativeLexicographic", ID.NegativeLexicographic);

  /**
   * Nest(f, expr, n) - starting with `expr`, iteratively applies `f` `n` times and returns the
   * final result.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nest.md">Nest
   *      documentation</a>
   */
  public final static IBuiltInSymbol Nest = F.initFinalSymbol("Nest", ID.Nest);

  /**
   * NestList(f, expr, n) - starting with `expr`, iteratively applies `f` `n` times and returns a
   * list of all intermediate results.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestList.md">NestList
   *      documentation</a>
   */
  public final static IBuiltInSymbol NestList = F.initFinalSymbol("NestList", ID.NestList);

  /**
   * NestWhile(f, expr, test) - applies a function `f` repeatedly on an expression `expr`, until
   * applying `test` on the result no longer yields `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestWhile.md">NestWhile
   *      documentation</a>
   */
  public final static IBuiltInSymbol NestWhile = F.initFinalSymbol("NestWhile", ID.NestWhile);

  /**
   * NestWhileList(f, expr, test) - applies a function `f` repeatedly on an expression `expr`, until
   * applying `test` on the result no longer yields `True`. It returns a list of all intermediate
   * results.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestWhileList.md">NestWhileList
   *      documentation</a>
   */
  public final static IBuiltInSymbol NestWhileList =
      F.initFinalSymbol("NestWhileList", ID.NestWhileList);

  /**
   * NextPrime(n) - gives the next prime after `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NextPrime.md">NextPrime
   *      documentation</a>
   */
  public final static IBuiltInSymbol NextPrime = F.initFinalSymbol("NextPrime", ID.NextPrime);

  public final static IBuiltInSymbol NonCommutativeMultiply =
      F.initFinalSymbol("NonCommutativeMultiply", ID.NonCommutativeMultiply);

  /**
   * NonNegative(x) - returns `True` if `x` is a positive real number or zero.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NonNegative.md">NonNegative
   *      documentation</a>
   */
  public final static IBuiltInSymbol NonNegative = F.initFinalSymbol("NonNegative", ID.NonNegative);

  /**
   * NonPositive(x) - returns `True` if `x` is a negative real number or zero.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NonPositive.md">NonPositive
   *      documentation</a>
   */
  public final static IBuiltInSymbol NonPositive = F.initFinalSymbol("NonPositive", ID.NonPositive);

  /**
   * None - is a possible value for `Span` and `Quiet`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/None.md">None
   *      documentation</a>
   */
  public final static IBuiltInSymbol None = F.initFinalSymbol("None", ID.None);

  /**
   * NoneTrue({expr1, expr2, ...}, test) - returns `True` if no application of `test` to `expr1,
   * expr2, ...` evaluates to `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NoneTrue.md">NoneTrue
   *      documentation</a>
   */
  public final static IBuiltInSymbol NoneTrue = F.initFinalSymbol("NoneTrue", ID.NoneTrue);

  public final static IBuiltInSymbol Nonexistent = F.initFinalSymbol("Nonexistent", ID.Nonexistent);

  /**
   * Nor(arg1, arg2, ...)' - Logical NOR function. It evaluates its arguments in order, giving
   * `False` immediately if any of them are `True`, and `True` if they are all `False`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nor.md">Nor
   *      documentation</a>
   */
  public final static IBuiltInSymbol Nor = F.initFinalSymbol("Nor", ID.Nor);

  /**
   * Norm(m, l) - computes the `l`-norm of matrix `m` (currently only works for vectors!).
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Norm.md">Norm
   *      documentation</a>
   */
  public final static IBuiltInSymbol Norm = F.initFinalSymbol("Norm", ID.Norm);

  /**
   * Normal(expr) - converts a Symja expression `expr` into a normal expression.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Normal.md">Normal
   *      documentation</a>
   */
  public final static IBuiltInSymbol Normal = F.initFinalSymbol("Normal", ID.Normal);

  /**
   * NormalDistribution(m, s) - returns the normal distribution of mean `m` and sigma `s`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NormalDistribution.md">NormalDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol NormalDistribution =
      F.initFinalSymbol("NormalDistribution", ID.NormalDistribution);

  /**
   * Normalize(v) - calculates the normalized vector `v` as `v/Norm(v)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Normalize.md">Normalize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Normalize = F.initFinalSymbol("Normalize", ID.Normalize);

  /**
   * Not(expr) - Logical Not function (negation). Returns `True` if the statement is `False`.
   * Returns `False` if the `expr` is `True`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Not.md">Not
   *      documentation</a>
   */
  public final static IBuiltInSymbol Not = F.initFinalSymbol("Not", ID.Not);

  public final static IBuiltInSymbol NotApplicable =
      F.initFinalSymbol("NotApplicable", ID.NotApplicable);

  public final static IBuiltInSymbol NotAvailable =
      F.initFinalSymbol("NotAvailable", ID.NotAvailable);

  public final static IBuiltInSymbol NotElement = F.initFinalSymbol("NotElement", ID.NotElement);

  public final static IBuiltInSymbol NotListQ = F.initFinalSymbol("NotListQ", ID.NotListQ);

  /**
   * Nothing - during evaluation of a list with a `Nothing` element `{..., Nothing, ...}`, the
   * symbol `Nothing` is removed from the arguments.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nothing.md">Nothing
   *      documentation</a>
   */
  public final static IBuiltInSymbol Nothing = F.initFinalSymbol("Nothing", ID.Nothing);

  public final static IBuiltInSymbol Now = F.initFinalSymbol("Now", ID.Now);

  /**
   * Null - is the implicit result of expressions that do not yield a result.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Null.md">Null
   *      documentation</a>
   */
  public final static IBuiltInSymbol Null = F.initFinalSymbol("Null", ID.Null);

  /**
   * NullSpace(matrix) - returns a list of vectors that span the nullspace of the `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NullSpace.md">NullSpace
   *      documentation</a>
   */
  public final static IBuiltInSymbol NullSpace = F.initFinalSymbol("NullSpace", ID.NullSpace);

  public final static IBuiltInSymbol Number = F.initFinalSymbol("Number", ID.Number);

  public final static IBuiltInSymbol NumberFieldRootsOfUnity =
      F.initFinalSymbol("NumberFieldRootsOfUnity", ID.NumberFieldRootsOfUnity);

  /**
   * NumberQ(expr) - returns `True` if `expr` is an explicit number, and `False` otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberQ.md">NumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumberQ = F.initFinalSymbol("NumberQ", ID.NumberQ);

  /**
   * NumberString - represents the characters in a number.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberString.md">NumberString
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumberString =
      F.initFinalSymbol("NumberString", ID.NumberString);

  /**
   * Numerator(expr) - gives the numerator in `expr`. Numerator collects expressions with non
   * negative exponents.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Numerator.md">Numerator
   *      documentation</a>
   */
  public final static IBuiltInSymbol Numerator = F.initFinalSymbol("Numerator", ID.Numerator);

  public final static IBuiltInSymbol NumericArray =
      F.initFinalSymbol("NumericArray", ID.NumericArray);

  public final static IBuiltInSymbol NumericArrayQ =
      F.initFinalSymbol("NumericArrayQ", ID.NumericArrayQ);

  public final static IBuiltInSymbol NumericArrayType =
      F.initFinalSymbol("NumericArrayType", ID.NumericArrayType);

  public final static IBuiltInSymbol NumericFunction =
      F.initFinalSymbol("NumericFunction", ID.NumericFunction);

  /**
   * NumericQ(expr) - returns `True` if `expr` is an explicit numeric expression, and `False`
   * otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumericQ.md">NumericQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumericQ = F.initFinalSymbol("NumericQ", ID.NumericQ);

  public final static IBuiltInSymbol NuttallWindow =
      F.initFinalSymbol("NuttallWindow", ID.NuttallWindow);

  public final static IBuiltInSymbol O = F.initFinalSymbol("O", ID.O);

  public final static IBuiltInSymbol Octahedron = F.initFinalSymbol("Octahedron", ID.Octahedron);

  /**
   * OddQ(x) - returns `True` if `x` is odd, and `False` otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OddQ.md">OddQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol OddQ = F.initFinalSymbol("OddQ", ID.OddQ);

  /**
   * Off( ) - switch off the interactive trace.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Off.md">Off
   *      documentation</a>
   */
  public final static IBuiltInSymbol Off = F.initFinalSymbol("Off", ID.Off);

  /**
   * On( ) - switch on the interactive trace. The output is printed in the defined `out` stream.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/On.md">On
   *      documentation</a>
   */
  public final static IBuiltInSymbol On = F.initFinalSymbol("On", ID.On);

  /**
   * OneIdentity - is an attribute specifying that `f(x)` should be treated as equivalent to `x` in
   * pattern matching.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OneIdentity.md">OneIdentity
   *      documentation</a>
   */
  public final static IBuiltInSymbol OneIdentity = F.initFinalSymbol("OneIdentity", ID.OneIdentity);

  public final static IBuiltInSymbol Opacity = F.initFinalSymbol("Opacity", ID.Opacity);

  /**
   * OpenAppend("file-name") - opens a file and returns an OutputStream to which writes are
   * appended.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OpenAppend.md">OpenAppend
   *      documentation</a>
   */
  public final static IBuiltInSymbol OpenAppend = F.initFinalSymbol("OpenAppend", ID.OpenAppend);

  public final static IBuiltInSymbol OpenRead = F.initFinalSymbol("OpenRead", ID.OpenRead);

  /**
   * OpenWrite() - creates an empty file in the default temporary-file directory and returns an
   * OutputStream.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OpenWrite.md">OpenWrite
   *      documentation</a>
   */
  public final static IBuiltInSymbol OpenWrite = F.initFinalSymbol("OpenWrite", ID.OpenWrite);

  /**
   * Operate(p, expr) - applies `p` to the head of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Operate.md">Operate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Operate = F.initFinalSymbol("Operate", ID.Operate);

  /**
   * OptimizeExpression(function) - common subexpressions elimination for a complicated `function`
   * by generating "dummy" variables for these subexpressions.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OptimizeExpression.md">OptimizeExpression
   *      documentation</a>
   */
  public final static IBuiltInSymbol OptimizeExpression =
      F.initFinalSymbol("OptimizeExpression", ID.OptimizeExpression);

  /**
   * OptionValue(name) - gives the value of the option `name` as specified in a call to a function
   * with `OptionsPattern`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OptionValue.md">OptionValue
   *      documentation</a>
   */
  public final static IBuiltInSymbol OptionValue = F.initFinalSymbol("OptionValue", ID.OptionValue);

  /**
   * Optional(patt, default) - is a pattern which matches `patt`, which if omitted should be
   * replaced by `default`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Optional.md">Optional
   *      documentation</a>
   */
  public final static IBuiltInSymbol Optional = F.initFinalSymbol("Optional", ID.Optional);

  /**
   * Options(symbol) - gives a list of optional arguments to `symbol` and their default values.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Options.md">Options
   *      documentation</a>
   */
  public final static IBuiltInSymbol Options = F.initFinalSymbol("Options", ID.Options);

  public final static IBuiltInSymbol OptionsPattern =
      F.initFinalSymbol("OptionsPattern", ID.OptionsPattern);

  /**
   * Or(expr1, expr2, ...)' - `expr1 || expr2 || ...` evaluates each expression in turn, returning
   * `True` as soon as an expression evaluates to `True`. If all expressions evaluate to `False`,
   * `Or` returns `False`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Or.md">Or
   *      documentation</a>
   */
  public final static IBuiltInSymbol Or = F.initFinalSymbol("Or", ID.Or);

  public final static IBuiltInSymbol Orange = F.initFinalSymbol("Orange", ID.Orange);

  /**
   * Order(a, b) - is `0` if `a` equals `b`. Is `-1` or `1` according to canonical order of `a` and
   * `b`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Order.md">Order
   *      documentation</a>
   */
  public final static IBuiltInSymbol Order = F.initFinalSymbol("Order", ID.Order);

  /**
   * OrderedQ({a, b}) - is `True` if `a` sorts before `b` according to canonical ordering.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OrderedQ.md">OrderedQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol OrderedQ = F.initFinalSymbol("OrderedQ", ID.OrderedQ);

  /**
   * Ordering(list) - calculate the permutation list of the elements in the sorted `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ordering.md">Ordering
   *      documentation</a>
   */
  public final static IBuiltInSymbol Ordering = F.initFinalSymbol("Ordering", ID.Ordering);

  /**
   * Orderless - is an attribute indicating that the leaves in an expression `f(a, b, c)` can be
   * placed in any order.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Orderless.md">Orderless
   *      documentation</a>
   */
  public final static IBuiltInSymbol Orderless = F.initFinalSymbol("Orderless", ID.Orderless);

  /**
   * OrthogonalMatrixQ(matrix) - returns `True`, if `matrix` is an orthogonal matrix. `False`
   * otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OrthogonalMatrixQ.md">OrthogonalMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol OrthogonalMatrixQ =
      F.initFinalSymbol("OrthogonalMatrixQ", ID.OrthogonalMatrixQ);

  /**
   * Orthogonalize(matrix) - returns a basis for the orthogonalized set of vectors defined by
   * `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Orthogonalize.md">Orthogonalize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Orthogonalize =
      F.initFinalSymbol("Orthogonalize", ID.Orthogonalize);

  /**
   * Out(k) - gives the result of the `k`th input line.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Out.md">Out
   *      documentation</a>
   */
  public final static IBuiltInSymbol Out = F.initFinalSymbol("Out", ID.Out);

  /**
   * Outer(f, x, y) - computes a generalised outer product of `x` and `y`, using the function `f` in
   * place of multiplication.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Outer.md">Outer
   *      documentation</a>
   */
  public final static IBuiltInSymbol Outer = F.initFinalSymbol("Outer", ID.Outer);

  public final static IBuiltInSymbol OutputForm = F.initFinalSymbol("OutputForm", ID.OutputForm);

  /**
   * OutputStream("file-name") - opens a file and returns an OutputStream.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OutputStream.md">OutputStream
   *      documentation</a>
   */
  public final static IBuiltInSymbol OutputStream =
      F.initFinalSymbol("OutputStream", ID.OutputStream);

  public final static IBuiltInSymbol OverscriptBox =
      F.initFinalSymbol("OverscriptBox", ID.OverscriptBox);

  /**
   * OwnValues(symbol) - prints the own-value rule associated with `symbol`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OwnValues.md">OwnValues
   *      documentation</a>
   */
  public final static IBuiltInSymbol OwnValues = F.initFinalSymbol("OwnValues", ID.OwnValues);

  /**
   * PDF(distribution, value) - returns the probability density function of `value`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PDF.md">PDF
   *      documentation</a>
   */
  public final static IBuiltInSymbol PDF = F.initFinalSymbol("PDF", ID.PDF);

  public final static IBuiltInSymbol Package = F.initFinalSymbol("Package", ID.Package);

  /**
   * PadLeft(list, n) - pads `list` to length `n` by adding `0` on the left.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PadLeft.md">PadLeft
   *      documentation</a>
   */
  public final static IBuiltInSymbol PadLeft = F.initFinalSymbol("PadLeft", ID.PadLeft);

  /**
   * PadRight(list, n) - pads `list` to length `n` by adding `0` on the right.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PadRight.md">PadRight
   *      documentation</a>
   */
  public final static IBuiltInSymbol PadRight = F.initFinalSymbol("PadRight", ID.PadRight);

  /**
   * ParametricPlot({function1, function2}, {t, tMin, tMax}) - generate a JavaScript control for the
   * parametric expressions `function1`, `function2` in the `t` range `{t, tMin, tMax}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ParametricPlot.md">ParametricPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ParametricPlot =
      F.initFinalSymbol("ParametricPlot", ID.ParametricPlot);

  /**
   * Parenthesis(expr) - print `expr` with parenthesis surrounded in output forms.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Parenthesis.md">Parenthesis
   *      documentation</a>
   */
  public final static IBuiltInSymbol Parenthesis = F.initFinalSymbol("Parenthesis", ID.Parenthesis);

  /**
   * ParetoDistribution(k,a) - returns a Pareto distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ParetoDistribution.md">ParetoDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol ParetoDistribution =
      F.initFinalSymbol("ParetoDistribution", ID.ParetoDistribution);

  /**
   * Part(expr, i) - returns part `i` of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part
   *      documentation</a>
   */
  public final static IBuiltInSymbol Part = F.initFinalSymbol("Part", ID.Part);

  /**
   * Partition(list, n) - partitions `list` into sublists of length `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Partition.md">Partition
   *      documentation</a>
   */
  public final static IBuiltInSymbol Partition = F.initFinalSymbol("Partition", ID.Partition);

  /**
   * PartitionsP(n) - gives the number of unrestricted partitions of the integer `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PartitionsP.md">PartitionsP
   *      documentation</a>
   */
  public final static IBuiltInSymbol PartitionsP = F.initFinalSymbol("PartitionsP", ID.PartitionsP);

  /**
   * PartitionsQ(n) - gives the number of partitions of the integer `n` into distinct parts
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PartitionsQ.md">PartitionsQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PartitionsQ = F.initFinalSymbol("PartitionsQ", ID.PartitionsQ);

  public final static IBuiltInSymbol ParzenWindow =
      F.initFinalSymbol("ParzenWindow", ID.ParzenWindow);

  public final static IBuiltInSymbol Pattern = F.initFinalSymbol("Pattern", ID.Pattern);

  public final static IBuiltInSymbol PatternOrder =
      F.initFinalSymbol("PatternOrder", ID.PatternOrder);

  /**
   * PatternTest(pattern, test) - constrains `pattern` to match `expr` only if the evaluation of
   * `test(expr)` yields `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PatternTest.md">PatternTest
   *      documentation</a>
   */
  public final static IBuiltInSymbol PatternTest = F.initFinalSymbol("PatternTest", ID.PatternTest);

  /**
   * PauliMatrix(n) - returns the Pauli `2x2` matrix for `n` between `0` and `4`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PauliMatrix.md">PauliMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol PauliMatrix = F.initFinalSymbol("PauliMatrix", ID.PauliMatrix);

  /**
   * Pause(seconds) - pause the thread for the number of `seconds`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pause.md">Pause
   *      documentation</a>
   */
  public final static IBuiltInSymbol Pause = F.initFinalSymbol("Pause", ID.Pause);

  public final static IBuiltInSymbol PearsonChiSquareTest =
      F.initFinalSymbol("PearsonChiSquareTest", ID.PearsonChiSquareTest);

  /**
   * PerfectNumber(n) - returns the `n`th perfect number. In number theory, a perfect number is a
   * positive integer that is equal to the sum of its proper
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PerfectNumber.md">PerfectNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol PerfectNumber =
      F.initFinalSymbol("PerfectNumber", ID.PerfectNumber);

  /**
   * PerfectNumberQ(n) - returns `True` if `n` is a perfect number. In number theory, a perfect
   * number is a positive integer that is equal to the sum of its proper
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PerfectNumberQ.md">PerfectNumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PerfectNumberQ =
      F.initFinalSymbol("PerfectNumberQ", ID.PerfectNumberQ);

  public final static IBuiltInSymbol Perimeter = F.initFinalSymbol("Perimeter", ID.Perimeter);

  /**
   * PermutationCycles(permutation-list) - generate a `Cycles({{...},{...}, ...})` expression from
   * the `permutation-list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationCycles.md">PermutationCycles
   *      documentation</a>
   */
  public final static IBuiltInSymbol PermutationCycles =
      F.initFinalSymbol("PermutationCycles", ID.PermutationCycles);

  /**
   * PermutationCyclesQ(cycles-expression) - if `cycles-expression` is a valid `Cycles({{...},{...},
   * ...})` expression return `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationCyclesQ.md">PermutationCyclesQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PermutationCyclesQ =
      F.initFinalSymbol("PermutationCyclesQ", ID.PermutationCyclesQ);

  /**
   * PermutationList(Cycles({{...},{...}, ...})) - get the permutation list representation from the
   * `Cycles({{...},{...}, ...})` expression.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationList.md">PermutationList
   *      documentation</a>
   */
  public final static IBuiltInSymbol PermutationList =
      F.initFinalSymbol("PermutationList", ID.PermutationList);

  /**
   * PermutationListQ(permutation-list) - if `permutation-list` is a valid permutation list return
   * `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationListQ.md">PermutationListQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PermutationListQ =
      F.initFinalSymbol("PermutationListQ", ID.PermutationListQ);

  /**
   * PermutationReplace(list-or-integer, Cycles({{...},{...}, ...})) - replace the arguments of the
   * first expression with the corresponding element from the `Cycles({{...},{...}, ...})`
   * expression.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationReplace.md">PermutationReplace
   *      documentation</a>
   */
  public final static IBuiltInSymbol PermutationReplace =
      F.initFinalSymbol("PermutationReplace", ID.PermutationReplace);

  /**
   * Permutations(list) - gives all possible orderings of the items in `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Permutations.md">Permutations
   *      documentation</a>
   */
  public final static IBuiltInSymbol Permutations =
      F.initFinalSymbol("Permutations", ID.Permutations);

  /**
   * Permute(list, Cycles({permutation-cycles})) - permutes the `list` from the cycles in
   * `permutation-cycles`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Permute.md">Permute
   *      documentation</a>
   */
  public final static IBuiltInSymbol Permute = F.initFinalSymbol("Permute", ID.Permute);

  /**
   * PetersenGraph() - create a `PetersenGraph(5, 2)` graph.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PetersenGraph.md">PetersenGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol PetersenGraph =
      F.initFinalSymbol("PetersenGraph", ID.PetersenGraph);

  /**
   * Pi - is the constant `Pi`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pi.md">Pi
   *      documentation</a>
   */
  public final static IBuiltInSymbol Pi = F.initFinalSymbol("Pi", ID.Pi);

  /**
   * Pick(nested-list, nested-selection) - returns the elements of `nested-list` that have value
   * `True` in the corresponding position in `nested-selection`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pick.md">Pick
   *      documentation</a>
   */
  public final static IBuiltInSymbol Pick = F.initFinalSymbol("Pick", ID.Pick);

  /**
   * PieChart(list-of-values) - plot a pie chart from a `list-of-values`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PieChart.md">PieChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol PieChart = F.initFinalSymbol("PieChart", ID.PieChart);

  /**
   * Piecewise({{expr1, cond1}, ...}) - represents a piecewise function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Piecewise.md">Piecewise
   *      documentation</a>
   */
  public final static IBuiltInSymbol Piecewise = F.initFinalSymbol("Piecewise", ID.Piecewise);

  /**
   * PiecewiseExpand(function) - expands piecewise expressions into a `Piecewise` function.
   * Currently only `Abs, Clip, If, Ramp, UnitStep` are converted to Piecewise expressions.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PiecewiseExpand.md">PiecewiseExpand
   *      documentation</a>
   */
  public final static IBuiltInSymbol PiecewiseExpand =
      F.initFinalSymbol("PiecewiseExpand", ID.PiecewiseExpand);

  public final static IBuiltInSymbol Pink = F.initFinalSymbol("Pink", ID.Pink);

  public final static IBuiltInSymbol PlanarGraph = F.initFinalSymbol("PlanarGraph", ID.PlanarGraph);

  public final static IBuiltInSymbol PlanarGraphQ =
      F.initFinalSymbol("PlanarGraphQ", ID.PlanarGraphQ);

  /**
   * Plot(function, {x, xMin, xMax}, PlotRange->{yMin,yMax}) - generate a JavaScript control for the
   * expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plot.md">Plot
   *      documentation</a>
   */
  public final static IBuiltInSymbol Plot = F.initFinalSymbol("Plot", ID.Plot);

  /**
   * Plot3D(function, {x, xMin, xMax}, {y,yMin,yMax}) - generate a JavaScript control for the
   * expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plot3D.md">Plot3D
   *      documentation</a>
   */
  public final static IBuiltInSymbol Plot3D = F.initFinalSymbol("Plot3D", ID.Plot3D);

  public final static IBuiltInSymbol PlotLegends = F.initFinalSymbol("PlotLegends", ID.PlotLegends);

  public final static IBuiltInSymbol PlotRange = F.initFinalSymbol("PlotRange", ID.PlotRange);

  public final static IBuiltInSymbol PlotStyle = F.initFinalSymbol("PlotStyle", ID.PlotStyle);

  /**
   * Plus(a, b, ...) - represents the sum of the terms `a, b, ...`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus
   *      documentation</a>
   */
  public final static IBuiltInSymbol Plus = F.initFinalSymbol("Plus", ID.Plus);

  /**
   * Pochhammer(a, n) - returns the pochhammer symbol for a rational number `a` and an integer
   * number `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pochhammer.md">Pochhammer
   *      documentation</a>
   */
  public final static IBuiltInSymbol Pochhammer = F.initFinalSymbol("Pochhammer", ID.Pochhammer);

  /**
   * Point({point_1, point_2 ...}) - represents the point primitive.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Point.md">Point
   *      documentation</a>
   */
  public final static IBuiltInSymbol Point = F.initFinalSymbol("Point", ID.Point);

  public final static IBuiltInSymbol PointLight = F.initFinalSymbol("PointLight", ID.PointLight);

  /**
   * PoissonDistribution(m) - returns a Poisson distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PoissonDistribution.md">PoissonDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol PoissonDistribution =
      F.initFinalSymbol("PoissonDistribution", ID.PoissonDistribution);

  /**
   * PolarPlot(function, {t, tMin, tMax}) - generate a JavaScript control for the polar plot
   * expressions `function` in the `t` range `{t, tMin, tMax}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolarPlot.md">PolarPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolarPlot = F.initFinalSymbol("PolarPlot", ID.PolarPlot);

  /**
   * PolyGamma(value) - return the digamma function of the `value`. The digamma function is defined
   * as the logarithmic derivative of the gamma function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolyGamma.md">PolyGamma
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolyGamma = F.initFinalSymbol("PolyGamma", ID.PolyGamma);

  public final static IBuiltInSymbol PolyLog = F.initFinalSymbol("PolyLog", ID.PolyLog);

  /**
   * Polygon({point_1, point_2 ...}) - represents the filled polygon primitive.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Polygon.md">Polygon
   *      documentation</a>
   */
  public final static IBuiltInSymbol Polygon = F.initFinalSymbol("Polygon", ID.Polygon);

  public final static IBuiltInSymbol Polyhedron = F.initFinalSymbol("Polyhedron", ID.Polyhedron);

  /**
   * PolynomialExtendedGCD(p, q, x) - returns the extended GCD ('greatest common divisor') of the
   * univariate polynomials `p` and `q`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialExtendedGCD.md">PolynomialExtendedGCD
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialExtendedGCD =
      F.initFinalSymbol("PolynomialExtendedGCD", ID.PolynomialExtendedGCD);

  /**
   * PolynomialGCD(p, q) - returns the GCD ('greatest common divisor') of the polynomials `p` and
   * `q`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialGCD.md">PolynomialGCD
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialGCD =
      F.initFinalSymbol("PolynomialGCD", ID.PolynomialGCD);

  /**
   * PolynomialLCM(p, q) - returns the LCM ('least common multiple') of the polynomials `p` and `q`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialLCM.md">PolynomialLCM
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialLCM =
      F.initFinalSymbol("PolynomialLCM", ID.PolynomialLCM);

  /**
   * PolynomialQ(p, x) - return `True` if `p` is a polynomial for the variable `x`. Return `False`
   * in all other cases.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQ.md">PolynomialQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialQ = F.initFinalSymbol("PolynomialQ", ID.PolynomialQ);

  /**
   * PolynomialQuotient(p, q, x) - returns the polynomial quotient of the polynomials `p` and `q`
   * for the variable `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQuotient.md">PolynomialQuotient
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialQuotient =
      F.initFinalSymbol("PolynomialQuotient", ID.PolynomialQuotient);

  /**
   * PolynomialQuotientRemainder(p, q, x) - returns a list with the polynomial quotient and
   * remainder of the polynomials `p` and `q` for the variable `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQuotientRemainder.md">PolynomialQuotientRemainder
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialQuotientRemainder =
      F.initFinalSymbol("PolynomialQuotientRemainder", ID.PolynomialQuotientRemainder);

  /**
   * PolynomialQuotient(p, q, x) - returns the polynomial remainder of the polynomials `p` and `q`
   * for the variable `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialRemainder.md">PolynomialRemainder
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialRemainder =
      F.initFinalSymbol("PolynomialRemainder", ID.PolynomialRemainder);

  /**
   * Position(expr, patt) - returns the list of positions for which `expr` matches `patt`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Position.md">Position
   *      documentation</a>
   */
  public final static IBuiltInSymbol Position = F.initFinalSymbol("Position", ID.Position);

  /**
   * Positive(x) - returns `True` if `x` is a positive real number.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Positive.md">Positive
   *      documentation</a>
   */
  public final static IBuiltInSymbol Positive = F.initFinalSymbol("Positive", ID.Positive);

  /**
   * PossibleZeroQ(expr) - returns `True` if basic symbolic and numerical methods suggests that
   * `expr` has value zero, and `False` otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PossibleZeroQ.md">PossibleZeroQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PossibleZeroQ =
      F.initFinalSymbol("PossibleZeroQ", ID.PossibleZeroQ);

  public final static IBuiltInSymbol Postefix = F.initFinalSymbol("Postefix", ID.Postefix);

  /**
   * Power(a, b) - represents `a` raised to the power of `b`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Power.md">Power
   *      documentation</a>
   */
  public final static IBuiltInSymbol Power = F.initFinalSymbol("Power", ID.Power);

  /**
   * PowerExpand(expr) - expands out powers of the form `(x^y)^z` and `(x*y)^z` in `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PowerExpand.md">PowerExpand
   *      documentation</a>
   */
  public final static IBuiltInSymbol PowerExpand = F.initFinalSymbol("PowerExpand", ID.PowerExpand);

  /**
   * PowerMod(x, y, m) - computes `x^y` modulo `m`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PowerMod.md">PowerMod
   *      documentation</a>
   */
  public final static IBuiltInSymbol PowerMod = F.initFinalSymbol("PowerMod", ID.PowerMod);

  /**
   * PreDecrement(x) - decrements `x` by `1`, returning the new value of `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PreDecrement.md">PreDecrement
   *      documentation</a>
   */
  public final static IBuiltInSymbol PreDecrement =
      F.initFinalSymbol("PreDecrement", ID.PreDecrement);

  /**
   * PreIncrement(x) - increments `x` by `1`, returning the new value of `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PreIncrement.md">PreIncrement
   *      documentation</a>
   */
  public final static IBuiltInSymbol PreIncrement =
      F.initFinalSymbol("PreIncrement", ID.PreIncrement);

  public final static IBuiltInSymbol Precision = F.initFinalSymbol("Precision", ID.Precision);

  public final static IBuiltInSymbol PrecisionGoal =
      F.initFinalSymbol("PrecisionGoal", ID.PrecisionGoal);

  public final static IBuiltInSymbol Prefix = F.initFinalSymbol("Prefix", ID.Prefix);

  /**
   * Prepend(expr, item) - returns `expr` with `item` prepended to its leaves.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Prepend.md">Prepend
   *      documentation</a>
   */
  public final static IBuiltInSymbol Prepend = F.initFinalSymbol("Prepend", ID.Prepend);

  /**
   * PrependTo(s, item) - prepend `item` to value of `s` and sets `s` to the result.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrependTo.md">PrependTo
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrependTo = F.initFinalSymbol("PrependTo", ID.PrependTo);

  /**
   * Prime(n) - returns the `n`th prime number.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Prime.md">Prime
   *      documentation</a>
   */
  public final static IBuiltInSymbol Prime = F.initFinalSymbol("Prime", ID.Prime);

  /**
   * PrimeOmega(n) - returns the sum of the exponents of the prime factorization of `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimeOmega.md">PrimeOmega
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrimeOmega = F.initFinalSymbol("PrimeOmega", ID.PrimeOmega);

  /**
   * PrimePi(x) - gives the number of primes less than or equal to `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimePi.md">PrimePi
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrimePi = F.initFinalSymbol("PrimePi", ID.PrimePi);

  /**
   * PrimePowerQ(n) - returns `True` if `n` is a power of a prime number.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimePowerQ.md">PrimePowerQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrimePowerQ = F.initFinalSymbol("PrimePowerQ", ID.PrimePowerQ);

  /**
   * PrimeQ(n) - returns `True` if `n` is a integer prime number.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimeQ.md">PrimeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrimeQ = F.initFinalSymbol("PrimeQ", ID.PrimeQ);

  public final static IBuiltInSymbol Primes = F.initFinalSymbol("Primes", ID.Primes);

  public final static IBuiltInSymbol PrimitiveRoot =
      F.initFinalSymbol("PrimitiveRoot", ID.PrimitiveRoot);

  /**
   * PrimitiveRootList(n) - returns the list of the primitive roots of `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimitiveRootList.md">PrimitiveRootList
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrimitiveRootList =
      F.initFinalSymbol("PrimitiveRootList", ID.PrimitiveRootList);

  /**
   * Print(expr) - print the `expr` to the default output stream and return `Null`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Print.md">Print
   *      documentation</a>
   */
  public final static IBuiltInSymbol Print = F.initFinalSymbol("Print", ID.Print);

  /**
   * PrintableASCIIQ(str) - returns `True` if all characters in `str` are ASCII characters.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrintableASCIIQ.md">PrintableASCIIQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrintableASCIIQ =
      F.initFinalSymbol("PrintableASCIIQ", ID.PrintableASCIIQ);

  public final static IBuiltInSymbol Prism = F.initFinalSymbol("Prism", ID.Prism);

  /**
   * Probability(pure-function, data-set) - returns the probability of the `pure-function` for the
   * given `data-set`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Probability.md">Probability
   *      documentation</a>
   */
  public final static IBuiltInSymbol Probability = F.initFinalSymbol("Probability", ID.Probability);

  /**
   * Product(expr, {i, imin, imax}) - evaluates the discrete product of `expr` with `i` ranging from
   * `imin` to `imax`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Product.md">Product
   *      documentation</a>
   */
  public final static IBuiltInSymbol Product = F.initFinalSymbol("Product", ID.Product);

  /**
   * ProductLog(z) - returns the value of the Lambert W function at `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ProductLog.md">ProductLog
   *      documentation</a>
   */
  public final static IBuiltInSymbol ProductLog = F.initFinalSymbol("ProductLog", ID.ProductLog);

  /**
   * Projection(vector1, vector2) - Find the orthogonal projection of `vector1` onto another
   * `vector2`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Projection.md">Projection
   *      documentation</a>
   */
  public final static IBuiltInSymbol Projection = F.initFinalSymbol("Projection", ID.Projection);

  public final static IBuiltInSymbol Protect = F.initFinalSymbol("Protect", ID.Protect);

  public final static IBuiltInSymbol Protected = F.initFinalSymbol("Protected", ID.Protected);

  /**
   * PseudoInverse(matrix) - computes the Moore-Penrose pseudoinverse of the `matrix`. If `matrix`
   * is invertible, the pseudoinverse equals the inverse.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PseudoInverse.md">PseudoInverse
   *      documentation</a>
   */
  public final static IBuiltInSymbol PseudoInverse =
      F.initFinalSymbol("PseudoInverse", ID.PseudoInverse);

  public final static IBuiltInSymbol Purple = F.initFinalSymbol("Purple", ID.Purple);

  public final static IBuiltInSymbol Put = F.initFinalSymbol("Put", ID.Put);

  public final static IBuiltInSymbol Pyramid = F.initFinalSymbol("Pyramid", ID.Pyramid);

  /**
   * QRDecomposition(A) - computes the QR decomposition of the matrix `A`. The QR decomposition is a
   * decomposition of a matrix `A` into a product `A = Q.R` of an unitary matrix `Q` and an upper
   * triangular matrix `R`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QRDecomposition.md">QRDecomposition
   *      documentation</a>
   */
  public final static IBuiltInSymbol QRDecomposition =
      F.initFinalSymbol("QRDecomposition", ID.QRDecomposition);

  /**
   * QuadraticIrrationalQ(expr) - returns `True`, if the `expr` is of the form `(p + s * Sqrt(d)) /
   * q` for integers `p,q,d,s`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuadraticIrrationalQ.md">QuadraticIrrationalQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuadraticIrrationalQ =
      F.initFinalSymbol("QuadraticIrrationalQ", ID.QuadraticIrrationalQ);

  /**
   * Quantile(list, q) - returns the `q`-Quantile of `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantile.md">Quantile
   *      documentation</a>
   */
  public final static IBuiltInSymbol Quantile = F.initFinalSymbol("Quantile", ID.Quantile);

  /**
   * Quantity(value, unit) - returns the quantity for `value` and `unit`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantity.md">Quantity
   *      documentation</a>
   */
  public final static IBuiltInSymbol Quantity = F.initFinalSymbol("Quantity", ID.Quantity);

  public final static IBuiltInSymbol QuantityDistribution =
      F.initFinalSymbol("QuantityDistribution", ID.QuantityDistribution);

  /**
   * QuantityMagnitude(quantity) - returns the value of the `quantity`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityMagnitude.md">QuantityMagnitude
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuantityMagnitude =
      F.initFinalSymbol("QuantityMagnitude", ID.QuantityMagnitude);

  public final static IBuiltInSymbol QuantityQ = F.initFinalSymbol("QuantityQ", ID.QuantityQ);

  public final static IBuiltInSymbol QuarticSolve =
      F.initFinalSymbol("QuarticSolve", ID.QuarticSolve);

  /**
   * Quartiles(arg) - returns a list of the `1/4`, `1/2` and `3/4` quantile of `arg`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quartiles.md">Quartiles
   *      documentation</a>
   */
  public final static IBuiltInSymbol Quartiles = F.initFinalSymbol("Quartiles", ID.Quartiles);

  /**
   * Quiet(expr) - evaluates `expr` in "quiet" mode (i.e. no warning messages are shown during
   * evaluation).
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quiet.md">Quiet
   *      documentation</a>
   */
  public final static IBuiltInSymbol Quiet = F.initFinalSymbol("Quiet", ID.Quiet);

  public final static IBuiltInSymbol Quit = F.initFinalSymbol("Quit", ID.Quit);

  /**
   * Quotient(m, n) - computes the integer quotient of `m` and `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quotient.md">Quotient
   *      documentation</a>
   */
  public final static IBuiltInSymbol Quotient = F.initFinalSymbol("Quotient", ID.Quotient);

  /**
   * QuotientRemainder(m, n) - computes a list of the quotient and remainder from division of `m`
   * and `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuotientRemainder.md">QuotientRemainder
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuotientRemainder =
      F.initFinalSymbol("QuotientRemainder", ID.QuotientRemainder);

  public final static IBuiltInSymbol RGBColor = F.initFinalSymbol("RGBColor", ID.RGBColor);

  public final static IBuiltInSymbol RSolve = F.initFinalSymbol("RSolve", ID.RSolve);

  public final static IBuiltInSymbol RSolveValue = F.initFinalSymbol("RSolveValue", ID.RSolveValue);

  /**
   * Ramp(z) - The `Ramp` function is a unary real function, whose graph is shaped like a ramp.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ramp.md">Ramp
   *      documentation</a>
   */
  public final static IBuiltInSymbol Ramp = F.initFinalSymbol("Ramp", ID.Ramp);

  /**
   * RandomChoice({item1, item2, item3,...}) - randomly picks one `item` from items.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomChoice.md">RandomChoice
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomChoice =
      F.initFinalSymbol("RandomChoice", ID.RandomChoice);

  /**
   * RandomComplex[{z_min, z_max}] - yields a pseudo-random complex number in the rectangle with
   * complex corners `z_min` and `z_max`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomComplex.md">RandomComplex
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomComplex =
      F.initFinalSymbol("RandomComplex", ID.RandomComplex);

  public final static IBuiltInSymbol RandomGraph = F.initFinalSymbol("RandomGraph", ID.RandomGraph);

  /**
   * RandomInteger(n) - create a random integer number between `0` and `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomInteger.md">RandomInteger
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomInteger =
      F.initFinalSymbol("RandomInteger", ID.RandomInteger);

  /**
   * RandomPermutation(s) - create a pseudo random permutation between `1` and `s`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomPermutation.md">RandomPermutation
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomPermutation =
      F.initFinalSymbol("RandomPermutation", ID.RandomPermutation);

  /**
   * RandomPrime({imin, imax}) - create a random prime integer number between `imin` and `imax`
   * inclusive.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomPrime.md">RandomPrime
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomPrime = F.initFinalSymbol("RandomPrime", ID.RandomPrime);

  /**
   * RandomReal() - create a random number between `0.0` and `1.0`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomReal.md">RandomReal
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomReal = F.initFinalSymbol("RandomReal", ID.RandomReal);

  /**
   * RandomSample(items) - create a random sample for the arguments of the `items`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomSample.md">RandomSample
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomSample =
      F.initFinalSymbol("RandomSample", ID.RandomSample);

  public final static IBuiltInSymbol RandomVariate =
      F.initFinalSymbol("RandomVariate", ID.RandomVariate);

  /**
   * Range(n) - returns a list of integers from `1` to `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Range.md">Range
   *      documentation</a>
   */
  public final static IBuiltInSymbol Range = F.initFinalSymbol("Range", ID.Range);

  /**
   * RankedMax({e_1, e_2, ..., e_i}, n) - returns the n-th largest real value in the list `{e_1,
   * e_2, ..., e_i}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RankedMax.md">RankedMax
   *      documentation</a>
   */
  public final static IBuiltInSymbol RankedMax = F.initFinalSymbol("RankedMax", ID.RankedMax);

  /**
   * RankedMin({e_1, e_2, ..., e_i}, n) - returns the n-th smallest real value in the list `{e_1,
   * e_2, ..., e_i}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RankedMin.md">RankedMin
   *      documentation</a>
   */
  public final static IBuiltInSymbol RankedMin = F.initFinalSymbol("RankedMin", ID.RankedMin);

  /**
   * Rational - is the head of rational numbers.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rational.md">Rational
   *      documentation</a>
   */
  public final static IBuiltInSymbol Rational = F.initFinalSymbol("Rational", ID.Rational);

  /**
   * Rationalize(expression) - convert numerical real or imaginary parts in (sub-)expressions into
   * rational numbers.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rationalize.md">Rationalize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Rationalize = F.initFinalSymbol("Rationalize", ID.Rationalize);

  public final static IBuiltInSymbol Rationals = F.initFinalSymbol("Rationals", ID.Rationals);

  public final static IBuiltInSymbol RawBoxes = F.initFinalSymbol("RawBoxes", ID.RawBoxes);

  /**
   * Re(z) - returns the real component of the complex number `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Re.md">Re
   *      documentation</a>
   */
  public final static IBuiltInSymbol Re = F.initFinalSymbol("Re", ID.Re);

  /**
   * Read(input-stream) - reads the `input-stream` and return one expression.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Read.md">Read
   *      documentation</a>
   */
  public final static IBuiltInSymbol Read = F.initFinalSymbol("Read", ID.Read);

  public final static IBuiltInSymbol ReadList = F.initFinalSymbol("ReadList", ID.ReadList);

  public final static IBuiltInSymbol ReadProtected =
      F.initFinalSymbol("ReadProtected", ID.ReadProtected);

  public final static IBuiltInSymbol ReadString = F.initFinalSymbol("ReadString", ID.ReadString);

  /**
   * Real - is the head of real (floating point) numbers.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Real.md">Real
   *      documentation</a>
   */
  public final static IBuiltInSymbol Real = F.initFinalSymbol("Real", ID.Real);

  /**
   * RealAbs(x) - returns the absolute value of the real number `x`. For complex number arguments
   * the function will be left unevaluated.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RealAbs.md">RealAbs
   *      documentation</a>
   */
  public final static IBuiltInSymbol RealAbs = F.initFinalSymbol("RealAbs", ID.RealAbs);

  public final static IBuiltInSymbol RealDigits = F.initFinalSymbol("RealDigits", ID.RealDigits);

  /**
   * RealNumberQ(expr) - returns `True` if `expr` is an explicit number with no imaginary component.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RealNumberQ.md">RealNumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol RealNumberQ = F.initFinalSymbol("RealNumberQ", ID.RealNumberQ);

  /**
   * RealSign(x) - gives `-1`, `0` or `1` depending on whether `x` is negative, zero or positive.
   * For complex number arguments the function will be left unevaluated.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RealSign.md">RealSign
   *      documentation</a>
   */
  public final static IBuiltInSymbol RealSign = F.initFinalSymbol("RealSign", ID.RealSign);

  /**
   * Reals - is the set of real numbers.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reals.md">Reals
   *      documentation</a>
   */
  public final static IBuiltInSymbol Reals = F.initFinalSymbol("Reals", ID.Reals);

  /**
   * Reap(expr) - gives the result of evaluating `expr`, together with all values sown during this
   * evaluation. Values sown with different tags are given in different lists.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reap.md">Reap
   *      documentation</a>
   */
  public final static IBuiltInSymbol Reap = F.initFinalSymbol("Reap", ID.Reap);

  public final static IBuiltInSymbol Record = F.initFinalSymbol("Record", ID.Record);

  public final static IBuiltInSymbol RecordSeparators =
      F.initFinalSymbol("RecordSeparators", ID.RecordSeparators);

  public final static IBuiltInSymbol Rectangle = F.initFinalSymbol("Rectangle", ID.Rectangle);

  public final static IBuiltInSymbol Red = F.initFinalSymbol("Red", ID.Red);

  /**
   * Reduce(logic-expression, var) - returns the reduced `logic-expression` for the variable `var`.
   * Reduce works only for the `Reals` domain.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reduce.md">Reduce
   *      documentation</a>
   */
  public final static IBuiltInSymbol Reduce = F.initFinalSymbol("Reduce", ID.Reduce);

  /**
   * Refine(expression, assumptions) - evaluate the `expression` for the given `assumptions`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Refine.md">Refine
   *      documentation</a>
   */
  public final static IBuiltInSymbol Refine = F.initFinalSymbol("Refine", ID.Refine);

  /**
   * RegularExpression("regex") - represents the regular expression specified by the string
   * `“regex”`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegularExpression.md">RegularExpression
   *      documentation</a>
   */
  public final static IBuiltInSymbol RegularExpression =
      F.initFinalSymbol("RegularExpression", ID.RegularExpression);

  /**
   * ReleaseHold(expr) - removes any `Hold`, `HoldForm`, `HoldPattern` or `HoldComplete` head from
   * `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReleaseHold.md">ReleaseHold
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReleaseHold = F.initFinalSymbol("ReleaseHold", ID.ReleaseHold);

  public final static IBuiltInSymbol Remove = F.initFinalSymbol("Remove", ID.Remove);

  /**
   * RemoveDiacritics("string") - returns a version of `string` with all diacritics removed.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RemoveDiacritics.md">RemoveDiacritics
   *      documentation</a>
   */
  public final static IBuiltInSymbol RemoveDiacritics =
      F.initFinalSymbol("RemoveDiacritics", ID.RemoveDiacritics);

  public final static IBuiltInSymbol Repeated = F.initFinalSymbol("Repeated", ID.Repeated);

  public final static IBuiltInSymbol RepeatedNull =
      F.initFinalSymbol("RepeatedNull", ID.RepeatedNull);

  /**
   * RepeatedTiming(x) - returns a list with the first entry containing the average evaluation time
   * of `x` and the second entry containing the evaluation result of `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RepeatedTiming.md">RepeatedTiming
   *      documentation</a>
   */
  public final static IBuiltInSymbol RepeatedTiming =
      F.initFinalSymbol("RepeatedTiming", ID.RepeatedTiming);

  /**
   * Replace(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr` with
   * the right-hand-side `rhs`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Replace.md">Replace
   *      documentation</a>
   */
  public final static IBuiltInSymbol Replace = F.initFinalSymbol("Replace", ID.Replace);

  /**
   * ReplaceAll(expr, i -> new) - replaces all `i` in `expr` with `new`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceAll.md">ReplaceAll
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReplaceAll = F.initFinalSymbol("ReplaceAll", ID.ReplaceAll);

  /**
   * ReplaceList(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr`
   * with the right-hand-side `rhs`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceList.md">ReplaceList
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReplaceList = F.initFinalSymbol("ReplaceList", ID.ReplaceList);

  /**
   * ReplacePart(expr, i -> new) - replaces part `i` in `expr` with `new`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplacePart.md">ReplacePart
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReplacePart = F.initFinalSymbol("ReplacePart", ID.ReplacePart);

  /**
   * ReplaceRepeated(expr, lhs -> rhs) - repeatedly applies the rule `lhs -> rhs` to `expr` until
   * the result no longer changes.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceRepeated.md">ReplaceRepeated
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReplaceRepeated =
      F.initFinalSymbol("ReplaceRepeated", ID.ReplaceRepeated);

  /**
   * Rescale(list) - returns `Rescale(list,{Min(list), Max(list)})`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rescale.md">Rescale
   *      documentation</a>
   */
  public final static IBuiltInSymbol Rescale = F.initFinalSymbol("Rescale", ID.Rescale);

  /**
   * Rest(expr) - returns `expr` with the first element removed.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rest.md">Rest
   *      documentation</a>
   */
  public final static IBuiltInSymbol Rest = F.initFinalSymbol("Rest", ID.Rest);

  /**
   * Resultant(polynomial1, polynomial2, var) - computes the resultant of the polynomials
   * `polynomial1` and `polynomial2` with respect to the variable `var`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Resultant.md">Resultant
   *      documentation</a>
   */
  public final static IBuiltInSymbol Resultant = F.initFinalSymbol("Resultant", ID.Resultant);

  /**
   * Return(expr) - aborts a function call and returns `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Return.md">Return
   *      documentation</a>
   */
  public final static IBuiltInSymbol Return = F.initFinalSymbol("Return", ID.Return);

  /**
   * Reverse(list) - reverse the elements of the `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reverse.md">Reverse
   *      documentation</a>
   */
  public final static IBuiltInSymbol Reverse = F.initFinalSymbol("Reverse", ID.Reverse);

  /**
   * RiccatiSolve({A,B},{Q,R}) - An algebraic Riccati equation is a type of nonlinear equation that
   * arises in the context of infinite-horizon optimal control problems in continuous time or
   * discrete time. The continuous time algebraic Riccati equation (CARE):
   * `A^{T}·X+X·A-X·B·R^{-1}·B^{T}·X+Q==0`. And the respective linear controller is: `K =
   * R^{-1}·B^{T}·P`. The solver receives `A`, `B`, `Q` and `R` and computes `P`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RiccatiSolve.md">RiccatiSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol RiccatiSolve =
      F.initFinalSymbol("RiccatiSolve", ID.RiccatiSolve);

  /**
   * Riffle(list1, list2) - insert elements of `list2` between the elements of `list1`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Riffle.md">Riffle
   *      documentation</a>
   */
  public final static IBuiltInSymbol Riffle = F.initFinalSymbol("Riffle", ID.Riffle);

  public final static IBuiltInSymbol Right = F.initFinalSymbol("Right", ID.Right);

  /**
   * RightComposition(sym1, sym2,...)[arg1, arg2,...] - creates a composition of the symbols applied
   * in reversed order at the arguments.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RightComposition.md">RightComposition
   *      documentation</a>
   */
  public final static IBuiltInSymbol RightComposition =
      F.initFinalSymbol("RightComposition", ID.RightComposition);

  /**
   * RogersTanimotoDissimilarity(u, v) - returns the Rogers-Tanimoto dissimilarity between the two
   * boolean 1-D lists `u` and `v`, which is defined as `R / (c_tt + c_ff + R)` where n is `len(u)`,
   * `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * (c_tf +
   * c_ft)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RogersTanimotoDissimilarity.md">RogersTanimotoDissimilarity
   *      documentation</a>
   */
  public final static IBuiltInSymbol RogersTanimotoDissimilarity =
      F.initFinalSymbol("RogersTanimotoDissimilarity", ID.RogersTanimotoDissimilarity);

  /**
   * RomanNumeral(positive-int-value) - converts the given `positive-int-value` to a roman numeral
   * string.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RomanNumeral.md">RomanNumeral
   *      documentation</a>
   */
  public final static IBuiltInSymbol RomanNumeral =
      F.initFinalSymbol("RomanNumeral", ID.RomanNumeral);

  public final static IBuiltInSymbol Root = F.initFinalSymbol("Root", ID.Root);

  public final static IBuiltInSymbol RootIntervals =
      F.initFinalSymbol("RootIntervals", ID.RootIntervals);

  public final static IBuiltInSymbol RootOf = F.initFinalSymbol("RootOf", ID.RootOf);

  public final static IBuiltInSymbol RootReduce = F.initFinalSymbol("RootReduce", ID.RootReduce);

  /**
   * Roots(polynomial-equation, var) - determine the roots of a univariate polynomial equation with
   * respect to the variable `var`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Roots.md">Roots
   *      documentation</a>
   */
  public final static IBuiltInSymbol Roots = F.initFinalSymbol("Roots", ID.Roots);

  /**
   * RotateLeft(list) - rotates the items of `list` by one item to the left.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotateLeft.md">RotateLeft
   *      documentation</a>
   */
  public final static IBuiltInSymbol RotateLeft = F.initFinalSymbol("RotateLeft", ID.RotateLeft);

  /**
   * RotateRight(list) - rotates the items of `list` by one item to the right.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotateRight.md">RotateRight
   *      documentation</a>
   */
  public final static IBuiltInSymbol RotateRight = F.initFinalSymbol("RotateRight", ID.RotateRight);

  /**
   * RotationMatrix(theta) - yields a rotation matrix for the angle `theta`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotationMatrix.md">RotationMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol RotationMatrix =
      F.initFinalSymbol("RotationMatrix", ID.RotationMatrix);

  /**
   * Round(expr) - round a given `expr` to nearest integer.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Round.md">Round
   *      documentation</a>
   */
  public final static IBuiltInSymbol Round = F.initFinalSymbol("Round", ID.Round);

  public final static IBuiltInSymbol Row = F.initFinalSymbol("Row", ID.Row);

  public final static IBuiltInSymbol RowBox = F.initFinalSymbol("RowBox", ID.RowBox);

  /**
   * RowReduce(matrix) - returns the reduced row-echelon form of `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RowReduce.md">RowReduce
   *      documentation</a>
   */
  public final static IBuiltInSymbol RowReduce = F.initFinalSymbol("RowReduce", ID.RowReduce);

  /**
   * Rule(x, y) - represents a rule replacing `x` with `y`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rule.md">Rule
   *      documentation</a>
   */
  public final static IBuiltInSymbol Rule = F.initFinalSymbol("Rule", ID.Rule);

  /**
   * RuleDelayed(x, y) - represents a rule replacing `x` with `y`, with `y` held unevaluated.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RuleDelayed.md">RuleDelayed
   *      documentation</a>
   */
  public final static IBuiltInSymbol RuleDelayed = F.initFinalSymbol("RuleDelayed", ID.RuleDelayed);

  /**
   * RussellRaoDissimilarity(u, v) - returns the Russell-Rao dissimilarity between the two boolean
   * 1-D lists `u` and `v`, which is defined as `(n - c_tt) / c_tt` where `n` is `len(u)` and `c_ij`
   * is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RussellRaoDissimilarity.md">RussellRaoDissimilarity
   *      documentation</a>
   */
  public final static IBuiltInSymbol RussellRaoDissimilarity =
      F.initFinalSymbol("RussellRaoDissimilarity", ID.RussellRaoDissimilarity);

  /**
   * SameObjectQ[java-object1, java-object2] - gives `True` if the Java `==` operator for the Java
   * objects gives true. `False` otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SameObjectQ.md">SameObjectQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SameObjectQ = F.initFinalSymbol("SameObjectQ", ID.SameObjectQ);

  /**
   * SameQ(x, y) - returns `True` if `x` and `y` are structurally identical.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SameQ.md">SameQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SameQ = F.initFinalSymbol("SameQ", ID.SameQ);

  public final static IBuiltInSymbol SameTest = F.initFinalSymbol("SameTest", ID.SameTest);

  /**
   * SatisfiabilityCount(boolean-expr) - test whether the `boolean-expr` is satisfiable by a
   * combination of boolean `False` and `True` values for the variables of the boolean expression
   * and return the number of possible combinations.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiabilityCount.md">SatisfiabilityCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol SatisfiabilityCount =
      F.initFinalSymbol("SatisfiabilityCount", ID.SatisfiabilityCount);

  /**
   * SatisfiabilityInstances(boolean-expr, list-of-variables) - test whether the `boolean-expr` is
   * satisfiable by a combination of boolean `False` and `True` values for the `list-of-variables`
   * and return exactly one instance of `True, False` combinations if possible.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiabilityInstances.md">SatisfiabilityInstances
   *      documentation</a>
   */
  public final static IBuiltInSymbol SatisfiabilityInstances =
      F.initFinalSymbol("SatisfiabilityInstances", ID.SatisfiabilityInstances);

  /**
   * SatisfiableQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable
   * by a combination of boolean `False` and `True` values for the `list-of-variables`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiableQ.md">SatisfiableQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SatisfiableQ =
      F.initFinalSymbol("SatisfiableQ", ID.SatisfiableQ);

  public final static IBuiltInSymbol Scaled = F.initFinalSymbol("Scaled", ID.Scaled);

  public final static IBuiltInSymbol ScalingFunctions =
      F.initFinalSymbol("ScalingFunctions", ID.ScalingFunctions);

  /**
   * Scan(f, expr) - applies `f` to each element of `expr` and returns `Null`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Scan.md">Scan
   *      documentation</a>
   */
  public final static IBuiltInSymbol Scan = F.initFinalSymbol("Scan", ID.Scan);

  /**
   * Sec(z) - returns the secant of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sec.md">Sec
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sec = F.initFinalSymbol("Sec", ID.Sec);

  /**
   * Sech(z) - returns the hyperbolic secant of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sech.md">Sech
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sech = F.initFinalSymbol("Sech", ID.Sech);

  public final static IBuiltInSymbol Second = F.initFinalSymbol("Second", ID.Second);

  /**
   * Select({e1, e2, ...}, head) - returns a list of the elements `ei` for which `head(ei)` returns
   * `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Select.md">Select
   *      documentation</a>
   */
  public final static IBuiltInSymbol Select = F.initFinalSymbol("Select", ID.Select);

  /**
   * SelectFirst({e1, e2, ...}, f) - returns the first of the elements `ei` for which `f(ei)`
   * returns `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SelectFirst.md">SelectFirst
   *      documentation</a>
   */
  public final static IBuiltInSymbol SelectFirst = F.initFinalSymbol("SelectFirst", ID.SelectFirst);

  /**
   * SemanticImport("path-to-filename") - if the file system is enabled, import the data from CSV
   * files and do a semantic interpretation of the columns.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SemanticImport.md">SemanticImport
   *      documentation</a>
   */
  public final static IBuiltInSymbol SemanticImport =
      F.initFinalSymbol("SemanticImport", ID.SemanticImport);

  /**
   * SemanticImportString("string-content") - import the data from a content string in CSV format
   * and do a semantic interpretation of the columns.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SemanticImportString.md">SemanticImportString
   *      documentation</a>
   */
  public final static IBuiltInSymbol SemanticImportString =
      F.initFinalSymbol("SemanticImportString", ID.SemanticImportString);

  /**
   * Sequence[x1, x2, ...] - represents a sequence of arguments to a function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sequence.md">Sequence
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sequence = F.initFinalSymbol("Sequence", ID.Sequence);

  public final static IBuiltInSymbol SequenceHold =
      F.initFinalSymbol("SequenceHold", ID.SequenceHold);

  /**
   * Series(expr, {x, x0, n}) - create a power series of `expr` up to order `(x- x0)^n` at the point
   * `x = x0`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Series.md">Series
   *      documentation</a>
   */
  public final static IBuiltInSymbol Series = F.initFinalSymbol("Series", ID.Series);

  /**
   * SeriesCoefficient(expr, {x, x0, n}) - get the coefficient of `(x- x0)^n` at the point `x = x0`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SeriesCoefficient.md">SeriesCoefficient
   *      documentation</a>
   */
  public final static IBuiltInSymbol SeriesCoefficient =
      F.initFinalSymbol("SeriesCoefficient", ID.SeriesCoefficient);

  /**
   * SeriesData(x, x0, {coeff0, coeff1, coeff2,...}, nMin, nMax, denominator}) - internal structure
   * of a power series at the point `x = x0` the `coeff_i` are coefficients of the power series.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SeriesData.md">SeriesData
   *      documentation</a>
   */
  public final static IBuiltInSymbol SeriesData = F.initFinalSymbol("SeriesData", ID.SeriesData);

  /**
   * Set(expr, value) - evaluates `value` and assigns it to `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Set.md">Set
   *      documentation</a>
   */
  public final static IBuiltInSymbol Set = F.initFinalSymbol("Set", ID.Set);

  /**
   * SetAttributes(symbol, attrib) - adds `attrib` to `symbol`'s attributes.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SetAttributes.md">SetAttributes
   *      documentation</a>
   */
  public final static IBuiltInSymbol SetAttributes =
      F.initFinalSymbol("SetAttributes", ID.SetAttributes);

  /**
   * SetDelayed(expr, value) - assigns `value` to `expr`, without evaluating `value`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SetDelayed.md">SetDelayed
   *      documentation</a>
   */
  public final static IBuiltInSymbol SetDelayed = F.initFinalSymbol("SetDelayed", ID.SetDelayed);

  public final static IBuiltInSymbol SetSystemOptions =
      F.initFinalSymbol("SetSystemOptions", ID.SetSystemOptions);

  /**
   * Share(function) - replace internally equal common subexpressions in `function` by the same
   * reference to reduce memory consumption and return the number of times where `Share(function)`
   * could replace a common subexpression.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Share.md">Share
   *      documentation</a>
   */
  public final static IBuiltInSymbol Share = F.initFinalSymbol("Share", ID.Share);

  public final static IBuiltInSymbol Short = F.initFinalSymbol("Short", ID.Short);

  public final static IBuiltInSymbol Shortest = F.initFinalSymbol("Shortest", ID.Shortest);

  public final static IBuiltInSymbol Show = F.initFinalSymbol("Show", ID.Show);

  /**
   * Sign(x) - gives `-1`, `0` or `1` depending on whether `x` is negative, zero or positive.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sign.md">Sign
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sign = F.initFinalSymbol("Sign", ID.Sign);

  public final static IBuiltInSymbol SignCmp = F.initFinalSymbol("SignCmp", ID.SignCmp);

  /**
   * Signature(permutation-list) - determine if the `permutation-list` has odd (`-1`) or even (`1`)
   * parity. Returns `0` if two elements in the `permutation-list` are equal.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Signature.md">Signature
   *      documentation</a>
   */
  public final static IBuiltInSymbol Signature = F.initFinalSymbol("Signature", ID.Signature);

  public final static IBuiltInSymbol Simplex = F.initFinalSymbol("Simplex", ID.Simplex);

  /**
   * Simplify(expr) - simplifies `expr`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Simplify.md">Simplify
   *      documentation</a>
   */
  public final static IBuiltInSymbol Simplify = F.initFinalSymbol("Simplify", ID.Simplify);

  /**
   * Sin(expr) - returns the sine of `expr` (measured in radians).
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sin.md">Sin
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sin = F.initFinalSymbol("Sin", ID.Sin);

  /**
   * SinIntegral(expr) - returns the hyperbolic sine integral of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SinIntegral.md">SinIntegral
   *      documentation</a>
   */
  public final static IBuiltInSymbol SinIntegral = F.initFinalSymbol("SinIntegral", ID.SinIntegral);

  /**
   * Sinc(expr) - the sinc function `Sin(expr)/expr` for `expr != 0`. `Sinc(0)` returns `1`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sinc.md">Sinc
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sinc = F.initFinalSymbol("Sinc", ID.Sinc);

  /**
   * SingularValueDecomposition(matrix) - calculates the singular value decomposition for the
   * `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SingularValueDecomposition.md">SingularValueDecomposition
   *      documentation</a>
   */
  public final static IBuiltInSymbol SingularValueDecomposition =
      F.initFinalSymbol("SingularValueDecomposition", ID.SingularValueDecomposition);

  public final static IBuiltInSymbol SingularValueList =
      F.initFinalSymbol("SingularValueList", ID.SingularValueList);

  /**
   * Sinh(z) - returns the hyperbolic sine of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sinh.md">Sinh
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sinh = F.initFinalSymbol("Sinh", ID.Sinh);

  /**
   * SinhIntegral(expr) - returns the sine integral of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SinhIntegral.md">SinhIntegral
   *      documentation</a>
   */
  public final static IBuiltInSymbol SinhIntegral =
      F.initFinalSymbol("SinhIntegral", ID.SinhIntegral);

  /**
   * Skewness(list) - gives Pearson's moment coefficient of skewness for `list` (a measure for
   * estimating the symmetry of a distribution).
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Skewness.md">Skewness
   *      documentation</a>
   */
  public final static IBuiltInSymbol Skewness = F.initFinalSymbol("Skewness", ID.Skewness);

  /**
   * # - is a short-hand for `#1`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Slot.md">Slot
   *      documentation</a>
   */
  public final static IBuiltInSymbol Slot = F.initFinalSymbol("Slot", ID.Slot);

  public final static IBuiltInSymbol SlotAbsent = F.initFinalSymbol("SlotAbsent", ID.SlotAbsent);

  /**
   * ## - is the sequence of arguments supplied to a pure function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SlotSequence.md">SlotSequence
   *      documentation</a>
   */
  public final static IBuiltInSymbol SlotSequence =
      F.initFinalSymbol("SlotSequence", ID.SlotSequence);

  /**
   * SokalSneathDissimilarity(u, v) - returns the Sokal-Sneath dissimilarity between the two boolean
   * 1-D lists `u` and `v`, which is defined as `R / (c_tt + R)` where n is `len(u)`, `c_ij` is the
   * number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * (c_tf + c_ft)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SokalSneathDissimilarity.md">SokalSneathDissimilarity
   *      documentation</a>
   */
  public final static IBuiltInSymbol SokalSneathDissimilarity =
      F.initFinalSymbol("SokalSneathDissimilarity", ID.SokalSneathDissimilarity);

  /**
   * Solve(equations, vars) - attempts to solve `equations` for the variables `vars`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Solve.md">Solve
   *      documentation</a>
   */
  public final static IBuiltInSymbol Solve = F.initFinalSymbol("Solve", ID.Solve);

  /**
   * Sort(list) - sorts `list` (or the leaves of any other expression) according to canonical
   * ordering.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sort.md">Sort
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sort = F.initFinalSymbol("Sort", ID.Sort);

  /**
   * SortBy(list, f) - sorts `list` (or the leaves of any other expression) according to canonical
   * ordering of the keys that are extracted from the `list`'s elements using `f`. Chunks of leaves
   * that appear the same under `f` are sorted according to their natural order (without applying
   * `f`).
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SortBy.md">SortBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol SortBy = F.initFinalSymbol("SortBy", ID.SortBy);

  /**
   * Sow(expr) - sends the value `expr` to the innermost `Reap`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sow.md">Sow
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sow = F.initFinalSymbol("Sow", ID.Sow);

  /**
   * Span - is the head of span ranges like `1;;3`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Span.md">Span
   *      documentation</a>
   */
  public final static IBuiltInSymbol Span = F.initFinalSymbol("Span", ID.Span);

  /**
   * SparseArray(nested-list) - create a sparse array from a `nested-list` structure.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SparseArray.md">SparseArray
   *      documentation</a>
   */
  public final static IBuiltInSymbol SparseArray = F.initFinalSymbol("SparseArray", ID.SparseArray);

  public final static IBuiltInSymbol Specularity = F.initFinalSymbol("Specularity", ID.Specularity);

  /**
   * Sphere({x, y, z}) - is a sphere of radius `1` centered at the point `{x, y, z}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sphere.md">Sphere
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sphere = F.initFinalSymbol("Sphere", ID.Sphere);

  /**
   * SphericalBesselJ(n, z) - spherical Bessel function `J(n, x)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SphericalBesselJ.md">SphericalBesselJ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SphericalBesselJ =
      F.initFinalSymbol("SphericalBesselJ", ID.SphericalBesselJ);

  /**
   * SphericalBesselY(n, z) - spherical Bessel function `Y(n, x)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SphericalBesselY.md">SphericalBesselY
   *      documentation</a>
   */
  public final static IBuiltInSymbol SphericalBesselY =
      F.initFinalSymbol("SphericalBesselY", ID.SphericalBesselY);

  public final static IBuiltInSymbol SphericalHankelH1 =
      F.initFinalSymbol("SphericalHankelH1", ID.SphericalHankelH1);

  public final static IBuiltInSymbol SphericalHankelH2 =
      F.initFinalSymbol("SphericalHankelH2", ID.SphericalHankelH2);

  public final static IBuiltInSymbol SphericalHarmonicY =
      F.initFinalSymbol("SphericalHarmonicY", ID.SphericalHarmonicY);

  /**
   * Split(list) - splits `list` into collections of consecutive identical elements.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Split.md">Split
   *      documentation</a>
   */
  public final static IBuiltInSymbol Split = F.initFinalSymbol("Split", ID.Split);

  /**
   * SplitBy(list, f) - splits `list` into collections of consecutive elements that give the same
   * result when `f` is applied.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SplitBy.md">SplitBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol SplitBy = F.initFinalSymbol("SplitBy", ID.SplitBy);

  public final static IBuiltInSymbol SpotLight = F.initFinalSymbol("SpotLight", ID.SpotLight);

  /**
   * Sqrt(expr) - returns the square root of `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sqrt.md">Sqrt
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sqrt = F.initFinalSymbol("Sqrt", ID.Sqrt);

  /**
   * SquareFreeQ(n) - returns `True` if `n` is a square free integer number or a square free
   * univariate polynomial.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquareFreeQ.md">SquareFreeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SquareFreeQ = F.initFinalSymbol("SquareFreeQ", ID.SquareFreeQ);

  /**
   * SquareMatrixQ(m) - returns `True` if `m` is a square matrix.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquareMatrixQ.md">SquareMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SquareMatrixQ =
      F.initFinalSymbol("SquareMatrixQ", ID.SquareMatrixQ);

  /**
   * SquaredEuclideanDistance(u, v) - returns squared the euclidean distance between `u$` and `v`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquaredEuclideanDistance.md">SquaredEuclideanDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol SquaredEuclideanDistance =
      F.initFinalSymbol("SquaredEuclideanDistance", ID.SquaredEuclideanDistance);

  /**
   * Stack( ) - return a list of the heads of the current stack wrapped by `HoldForm`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Stack.md">Stack
   *      documentation</a>
   */
  public final static IBuiltInSymbol Stack = F.initFinalSymbol("Stack", ID.Stack);

  /**
   * Stack(expr) - begine a new stack and evaluate `èxpr`. Use `Stack(_)` as a subexpression in
   * `expr` to return the stack elements.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StackBegin.md">StackBegin
   *      documentation</a>
   */
  public final static IBuiltInSymbol StackBegin = F.initFinalSymbol("StackBegin", ID.StackBegin);

  /**
   * StandardDeviation(list) - computes the standard deviation of `list`. `list` may consist of
   * numerical values or symbols. Numerical values may be real or complex.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardDeviation.md">StandardDeviation
   *      documentation</a>
   */
  public final static IBuiltInSymbol StandardDeviation =
      F.initFinalSymbol("StandardDeviation", ID.StandardDeviation);

  public final static IBuiltInSymbol StandardForm =
      F.initFinalSymbol("StandardForm", ID.StandardForm);

  public final static IBuiltInSymbol Standardize = F.initFinalSymbol("Standardize", ID.Standardize);

  /**
   * StarGraph(order) - create a new star graph with `order` number of total vertices including the
   * center vertex.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StarGraph.md">StarGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol StarGraph = F.initFinalSymbol("StarGraph", ID.StarGraph);

  /**
   * StartOfLine - begine a new stack and evaluate `èxpr`. Use `Stack(_)` as a subexpression in
   * `expr` to return the stack elements.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StartOfLine.md">StartOfLine
   *      documentation</a>
   */
  public final static IBuiltInSymbol StartOfLine = F.initFinalSymbol("StartOfLine", ID.StartOfLine);

  /**
   * StartOfString - represents the start of a string.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StartOfString.md">StartOfString
   *      documentation</a>
   */
  public final static IBuiltInSymbol StartOfString =
      F.initFinalSymbol("StartOfString", ID.StartOfString);

  public final static IBuiltInSymbol StaticsVisible =
      F.initFinalSymbol("StaticsVisible", ID.StaticsVisible);

  /**
   * StieltjesGamma(a) - returns Stieltjes constant.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StieltjesGamma.md">StieltjesGamma
   *      documentation</a>
   */
  public final static IBuiltInSymbol StieltjesGamma =
      F.initFinalSymbol("StieltjesGamma", ID.StieltjesGamma);

  /**
   * StirlingS1(n, k) - returns the Stirling numbers of the first kind.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StirlingS1.md">StirlingS1
   *      documentation</a>
   */
  public final static IBuiltInSymbol StirlingS1 = F.initFinalSymbol("StirlingS1", ID.StirlingS1);

  /**
   * StirlingS2(n, k) - returns the Stirling numbers of the second kind. `StirlingS2(n,k)` is the
   * number of ways of partitioning an `n`-element set into `k` non-empty subsets.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StirlingS2.md">StirlingS2
   *      documentation</a>
   */
  public final static IBuiltInSymbol StirlingS2 = F.initFinalSymbol("StirlingS2", ID.StirlingS2);

  public final static IBuiltInSymbol Strict = F.initFinalSymbol("Strict", ID.Strict);

  /**
   * String - is the head of strings..
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/String.md">String
   *      documentation</a>
   */
  public final static IBuiltInSymbol String = F.initFinalSymbol("String", ID.String);

  /**
   * StringCases(string, pattern) - gives all occurences of `pattern` in `string`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringCases.md">StringCases
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringCases = F.initFinalSymbol("StringCases", ID.StringCases);

  /**
   * StringContainsQ(str1, str2) - return a list of matches for `"p1", "p2",...` list of strings in
   * the string `str`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringContainsQ.md">StringContainsQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringContainsQ =
      F.initFinalSymbol("StringContainsQ", ID.StringContainsQ);

  /**
   * StringCount(string, pattern) - counts all occurences of `pattern` in `string`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringCount.md">StringCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringCount = F.initFinalSymbol("StringCount", ID.StringCount);

  public final static IBuiltInSymbol StringDrop = F.initFinalSymbol("StringDrop", ID.StringDrop);

  /**
   * StringExpression(s_1, s_2, ...) - represents a sequence of strings and symbolic string objects
   * `s_i`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringExpression.md">StringExpression
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringExpression =
      F.initFinalSymbol("StringExpression", ID.StringExpression);

  public final static IBuiltInSymbol StringFormat =
      F.initFinalSymbol("StringFormat", ID.StringFormat);

  /**
   * StringFreeQ("string", patt) - returns `True` if no substring in `string` matches the string
   * expression `patt`, and returns `False` otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringFreeQ.md">StringFreeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringFreeQ = F.initFinalSymbol("StringFreeQ", ID.StringFreeQ);

  /**
   * StringInsert(string, new-string, position) - returns a string with `new-string` inserted
   * starting at `position` in `string`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringInsert.md">StringInsert
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringInsert =
      F.initFinalSymbol("StringInsert", ID.StringInsert);

  /**
   * StringJoin(str1, str2, ... strN) - returns the concatenation of the strings `str1, str2, ...
   * strN`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringJoin.md">StringJoin
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringJoin = F.initFinalSymbol("StringJoin", ID.StringJoin);

  /**
   * StringLength(string) - gives the length of `string`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringLength.md">StringLength
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringLength =
      F.initFinalSymbol("StringLength", ID.StringLength);

  /**
   * StringMatchQ(string, regex-pattern) - check if the regular expression `regex-pattern` matches
   * the `string`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringMatchQ.md">StringMatchQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringMatchQ =
      F.initFinalSymbol("StringMatchQ", ID.StringMatchQ);

  /**
   * StringPart(str, pos) - return the character at position `pos` from the `str` string expression.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringPart.md">StringPart
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringPart = F.initFinalSymbol("StringPart", ID.StringPart);

  /**
   * StringPosition("string", patt) - gives a list of starting and ending positions where `patt`
   * matches `"string"`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringPosition.md">StringPosition
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringPosition =
      F.initFinalSymbol("StringPosition", ID.StringPosition);

  /**
   * StringQ(x) - is `True` if `x` is a string object, or `False` otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringQ.md">StringQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringQ = F.initFinalSymbol("StringQ", ID.StringQ);

  /**
   * StringReplace(string, fromStr -> toStr) - replaces each occurrence of `fromStr` with `toStr` in
   * `string`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringReplace.md">StringReplace
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringReplace =
      F.initFinalSymbol("StringReplace", ID.StringReplace);

  /**
   * StringReplace(string) - reverse the `string`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringReverse.md">StringReverse
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringReverse =
      F.initFinalSymbol("StringReverse", ID.StringReverse);

  /**
   * StringRiffle({s1, s2, s3, ...}) - returns a new string by concatenating all the `si`, with
   * spaces inserted between them.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringRiffle.md">StringRiffle
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringRiffle =
      F.initFinalSymbol("StringRiffle", ID.StringRiffle);

  /**
   * StringSplit(str) - split the string `str` by whitespaces into a list of strings.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringSplit.md">StringSplit
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringSplit = F.initFinalSymbol("StringSplit", ID.StringSplit);

  /**
   * StringTake("string", n) - gives the first `n` characters in `string`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringTake.md">StringTake
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringTake = F.initFinalSymbol("StringTake", ID.StringTake);

  /**
   * StringTemplate(string) - gives a `StringTemplate` expression with name `string`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringTemplate.md">StringTemplate
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringTemplate =
      F.initFinalSymbol("StringTemplate", ID.StringTemplate);

  /**
   * StringToByteArray(string) - encodes the `string` into a sequence of bytes using the default
   * character set `UTF-8`, storing the result into into a `ByteArray`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringToByteArray.md">StringToByteArray
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringToByteArray =
      F.initFinalSymbol("StringToByteArray", ID.StringToByteArray);

  /**
   * StringToStream("string") - converts a `string` to an open input stream.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringToStream.md">StringToStream
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringToStream =
      F.initFinalSymbol("StringToStream", ID.StringToStream);

  /**
   * StringTrim(s) - returns a version of `s `with whitespace removed from start and end.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringTrim.md">StringTrim
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringTrim = F.initFinalSymbol("StringTrim", ID.StringTrim);

  public final static IBuiltInSymbol Structure = F.initFinalSymbol("Structure", ID.Structure);

  /**
   * StruveH(n, z) - returns the Struve function `H_n(z)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StruveH.md">StruveH
   *      documentation</a>
   */
  public final static IBuiltInSymbol StruveH = F.initFinalSymbol("StruveH", ID.StruveH);

  /**
   * StruveL(n, z) - returns the modified Struve function `L_n(z)`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StruveL.md">StruveL
   *      documentation</a>
   */
  public final static IBuiltInSymbol StruveL = F.initFinalSymbol("StruveL", ID.StruveL);

  /**
   * StudentTDistribution(v) - returns a Student's t-distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StudentTDistribution.md">StudentTDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol StudentTDistribution =
      F.initFinalSymbol("StudentTDistribution", ID.StudentTDistribution);

  public final static IBuiltInSymbol Style = F.initFinalSymbol("Style", ID.Style);

  public final static IBuiltInSymbol StyleForm = F.initFinalSymbol("StyleForm", ID.StyleForm);

  /**
   * Subdivide(n) - returns a list with `n+1` entries obtained by subdividing the range `0` to `1`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subdivide.md">Subdivide
   *      documentation</a>
   */
  public final static IBuiltInSymbol Subdivide = F.initFinalSymbol("Subdivide", ID.Subdivide);

  /**
   * Subfactorial(n) - returns the subfactorial number of the integer `n`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subfactorial.md">Subfactorial
   *      documentation</a>
   */
  public final static IBuiltInSymbol Subfactorial =
      F.initFinalSymbol("Subfactorial", ID.Subfactorial);

  public final static IBuiltInSymbol Subscript = F.initFinalSymbol("Subscript", ID.Subscript);

  public final static IBuiltInSymbol SubscriptBox =
      F.initFinalSymbol("SubscriptBox", ID.SubscriptBox);

  /**
   * SubsetQ(set1, set2) - returns `True` if `set2` is a subset of `set1`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubsetQ.md">SubsetQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SubsetQ = F.initFinalSymbol("SubsetQ", ID.SubsetQ);

  /**
   * Subsets(list) - finds a list of all possible subsets of `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subsets.md">Subsets
   *      documentation</a>
   */
  public final static IBuiltInSymbol Subsets = F.initFinalSymbol("Subsets", ID.Subsets);

  public final static IBuiltInSymbol Subsuperscript =
      F.initFinalSymbol("Subsuperscript", ID.Subsuperscript);

  /**
   * Subtract(a, b) - represents the subtraction of `b` from `a`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subtract.md">Subtract
   *      documentation</a>
   */
  public final static IBuiltInSymbol Subtract = F.initFinalSymbol("Subtract", ID.Subtract);

  /**
   * SubtractFrom(x, dx) - is equivalent to `x = x - dx`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubtractFrom.md">SubtractFrom
   *      documentation</a>
   */
  public final static IBuiltInSymbol SubtractFrom =
      F.initFinalSymbol("SubtractFrom", ID.SubtractFrom);

  /**
   * SubtractSides(compare-expr, value) - subtracts `value` from all elements of the `compare-expr`.
   * `compare-expr` can be `True`, `False` or a comparison expression with head `Equal, Unequal,
   * Less, LessEqual, Greater, GreaterEqual`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubtractSides.md">SubtractSides
   *      documentation</a>
   */
  public final static IBuiltInSymbol SubtractSides =
      F.initFinalSymbol("SubtractSides", ID.SubtractSides);

  /**
   * Sum(expr, {i, imin, imax}) - evaluates the discrete sum of `expr` with `i` ranging from `imin`
   * to `imax`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sum.md">Sum
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sum = F.initFinalSymbol("Sum", ID.Sum);

  public final static IBuiltInSymbol Summary = F.initFinalSymbol("Summary", ID.Summary);

  public final static IBuiltInSymbol Superscript = F.initFinalSymbol("Superscript", ID.Superscript);

  public final static IBuiltInSymbol SuperscriptBox =
      F.initFinalSymbol("SuperscriptBox", ID.SuperscriptBox);

  /**
   * Surd(expr, n) - returns the `n`-th root of `expr`. If the result is defined, it's a real value.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Surd.md">Surd
   *      documentation</a>
   */
  public final static IBuiltInSymbol Surd = F.initFinalSymbol("Surd", ID.Surd);

  public final static IBuiltInSymbol SurfaceArea = F.initFinalSymbol("SurfaceArea", ID.SurfaceArea);

  public final static IBuiltInSymbol SurfaceGraphics =
      F.initFinalSymbol("SurfaceGraphics", ID.SurfaceGraphics);

  /**
   * SurvivalFunction(dist, x) - returns the survival function for the distribution `dist` evaluated
   * at `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SurvivalFunction.md">SurvivalFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol SurvivalFunction =
      F.initFinalSymbol("SurvivalFunction", ID.SurvivalFunction);

  /**
   * Switch(expr, pattern1, value1, pattern2, value2, ...) - yields the first `value` for which
   * `expr` matches the corresponding pattern.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Switch.md">Switch
   *      documentation</a>
   */
  public final static IBuiltInSymbol Switch = F.initFinalSymbol("Switch", ID.Switch);

  /**
   * Symbol - is the head of symbols.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Symbol.md">Symbol
   *      documentation</a>
   */
  public final static IBuiltInSymbol Symbol = F.initFinalSymbol("Symbol", ID.Symbol);

  /**
   * SymbolName(s) - returns the name of the symbol `s` (without any leading context name).
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymbolName.md">SymbolName
   *      documentation</a>
   */
  public final static IBuiltInSymbol SymbolName = F.initFinalSymbol("SymbolName", ID.SymbolName);

  /**
   * SymbolQ(x) - is `True` if `x` is a symbol, or `False` otherwise.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymbolQ.md">SymbolQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SymbolQ = F.initFinalSymbol("SymbolQ", ID.SymbolQ);

  public final static IBuiltInSymbol Symmetric = F.initFinalSymbol("Symmetric", ID.Symmetric);

  /**
   * SymmetricMatrixQ(m) - returns `True` if `m` is a symmetric matrix.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymmetricMatrixQ.md">SymmetricMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SymmetricMatrixQ =
      F.initFinalSymbol("SymmetricMatrixQ", ID.SymmetricMatrixQ);

  public final static IBuiltInSymbol SyntaxLength =
      F.initFinalSymbol("SyntaxLength", ID.SyntaxLength);

  /**
   * SyntaxQ(str) - is `True` if the given `str` is a string which has the correct syntax.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SyntaxQ.md">SyntaxQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SyntaxQ = F.initFinalSymbol("SyntaxQ", ID.SyntaxQ);

  /**
   * SystemDialogInput("FileOpen") - if the file system is enabled, open a file chooser dialog box.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SystemDialogInput.md">SystemDialogInput
   *      documentation</a>
   */
  public final static IBuiltInSymbol SystemDialogInput =
      F.initFinalSymbol("SystemDialogInput", ID.SystemDialogInput);

  public final static IBuiltInSymbol SystemOptions =
      F.initFinalSymbol("SystemOptions", ID.SystemOptions);

  /**
   * TTest(real-vector) - Returns the *observed significance level*, or *p-value*, associated with a
   * one-sample, two-tailed t-test comparing the mean of the input vector with the constant
   * <code>0.0</code>.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TTest.md">TTest
   *      documentation</a>
   */
  public final static IBuiltInSymbol TTest = F.initFinalSymbol("TTest", ID.TTest);

  /**
   * Table(expr, {i, n}) - evaluates `expr` with `i` ranging from `1` to `n`, returning a list of
   * the results.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Table.md">Table
   *      documentation</a>
   */
  public final static IBuiltInSymbol Table = F.initFinalSymbol("Table", ID.Table);

  public final static IBuiltInSymbol TableAlignments =
      F.initFinalSymbol("TableAlignments", ID.TableAlignments);

  public final static IBuiltInSymbol TableDepth = F.initFinalSymbol("TableDepth", ID.TableDepth);

  public final static IBuiltInSymbol TableDirections =
      F.initFinalSymbol("TableDirections", ID.TableDirections);

  public final static IBuiltInSymbol TableForm = F.initFinalSymbol("TableForm", ID.TableForm);

  public final static IBuiltInSymbol TableHeadings =
      F.initFinalSymbol("TableHeadings", ID.TableHeadings);

  public final static IBuiltInSymbol TableSpacing =
      F.initFinalSymbol("TableSpacing", ID.TableSpacing);

  /**
   * TagSet(f, expr, value) - assigns the evaluated `value` to `expr` and associates the
   * corresponding rule with the symbol `f`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TagSet.md">TagSet
   *      documentation</a>
   */
  public final static IBuiltInSymbol TagSet = F.initFinalSymbol("TagSet", ID.TagSet);

  /**
   * TagSetDelayed(f, expr, value) - assigns `value` to `expr`, without evaluating `value` and
   * associates the corresponding rule with the symbol `f`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TagSetDelayed.md">TagSetDelayed
   *      documentation</a>
   */
  public final static IBuiltInSymbol TagSetDelayed =
      F.initFinalSymbol("TagSetDelayed", ID.TagSetDelayed);

  /**
   * Take(expr, n) - returns `expr` with all but the first `n` leaves removed.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Take.md">Take
   *      documentation</a>
   */
  public final static IBuiltInSymbol Take = F.initFinalSymbol("Take", ID.Take);

  /**
   * TakeLargest({e_1, e_2, ..., e_i}, n) - returns the `n` largest real values from the list `{e_1,
   * e_2, ..., e_i}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeLargest.md">TakeLargest
   *      documentation</a>
   */
  public final static IBuiltInSymbol TakeLargest = F.initFinalSymbol("TakeLargest", ID.TakeLargest);

  /**
   * TakeLargestBy({e_1, e_2, ..., e_i}, function, n) - returns the `n` values from the list `{e_1,
   * e_2, ..., e_i}`, where `function(e_i)` is largest.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeLargestBy.md">TakeLargestBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol TakeLargestBy =
      F.initFinalSymbol("TakeLargestBy", ID.TakeLargestBy);

  /**
   * TakeSmallest({e_1, e_2, ..., e_i}, n) - returns the `n` smallest real values from the list
   * `{e_1, e_2, ..., e_i}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeSmallest.md">TakeSmallest
   *      documentation</a>
   */
  public final static IBuiltInSymbol TakeSmallest =
      F.initFinalSymbol("TakeSmallest", ID.TakeSmallest);

  /**
   * TakeSmallestBy({e_1, e_2, ..., e_i}, function, n) - returns the `n` values from the list `{e_1,
   * e_2, ..., e_i}`, where `function(e_i)` is smallest.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeSmallestBy.md">TakeSmallestBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol TakeSmallestBy =
      F.initFinalSymbol("TakeSmallestBy", ID.TakeSmallestBy);

  /**
   * TakeWhile({e1, e2, ...}, head) - returns the list of elements `ei` at the start of list for
   * which `head(ei)` returns `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeWhile.md">TakeWhile
   *      documentation</a>
   */
  public final static IBuiltInSymbol TakeWhile = F.initFinalSymbol("TakeWhile", ID.TakeWhile);

  /**
   * Tally(list) - return the elements and their number of occurrences in `list` in a new result
   * list. The `binary-predicate` tests if two elements are equivalent. `SameQ` is used as the
   * default `binary-predicate`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tally.md">Tally
   *      documentation</a>
   */
  public final static IBuiltInSymbol Tally = F.initFinalSymbol("Tally", ID.Tally);

  /**
   * Tan(expr) - returns the tangent of `expr` (measured in radians).
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tan.md">Tan
   *      documentation</a>
   */
  public final static IBuiltInSymbol Tan = F.initFinalSymbol("Tan", ID.Tan);

  /**
   * Tanh(z) - returns the hyperbolic tangent of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tanh.md">Tanh
   *      documentation</a>
   */
  public final static IBuiltInSymbol Tanh = F.initFinalSymbol("Tanh", ID.Tanh);

  /**
   * TautologyQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable by
   * all combinations of boolean `False` and `True` values for the `list-of-variables`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TautologyQ.md">TautologyQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol TautologyQ = F.initFinalSymbol("TautologyQ", ID.TautologyQ);

  public final static IBuiltInSymbol Taylor = F.initFinalSymbol("Taylor", ID.Taylor);

  /**
   * TeXForm(expr) - returns the TeX form of the evaluated `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TeXForm.md">TeXForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol TeXForm = F.initFinalSymbol("TeXForm", ID.TeXForm);

  /**
   * TemplateApply(string, values) - renders a `StringTemplate` expression by replacing
   * `TemplateSlot`s with mapped values.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateApply.md">TemplateApply
   *      documentation</a>
   */
  public final static IBuiltInSymbol TemplateApply =
      F.initFinalSymbol("TemplateApply", ID.TemplateApply);

  public final static IBuiltInSymbol TemplateExpression =
      F.initFinalSymbol("TemplateExpression", ID.TemplateExpression);

  /**
   * TemplateIf(condition-expression, true-expression, false-expression) - in `TemplateApply`
   * evaluation insert `true-expression` if `condition-expression` evaluates to `true`, otherwise
   * insert `false-expression`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateIf.md">TemplateIf
   *      documentation</a>
   */
  public final static IBuiltInSymbol TemplateIf = F.initFinalSymbol("TemplateIf", ID.TemplateIf);

  /**
   * TemplateSlot(string) - gives a `TemplateSlot` expression with name `string`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateSlot.md">TemplateSlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol TemplateSlot =
      F.initFinalSymbol("TemplateSlot", ID.TemplateSlot);

  /**
   * TensorDimensions(t) - return the dimensions of the tensor `t`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TensorDimensions.md">TensorDimensions
   *      documentation</a>
   */
  public final static IBuiltInSymbol TensorDimensions =
      F.initFinalSymbol("TensorDimensions", ID.TensorDimensions);

  /**
   * TensorProduct(t1, t2, ...) - product of the tensors `t1, t2, ...`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TensorProduct.md">TensorProduct
   *      documentation</a>
   */
  public final static IBuiltInSymbol TensorProduct =
      F.initFinalSymbol("TensorProduct", ID.TensorProduct);

  /**
   * TensorRank(t) - return the rank of the tensor `t`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TensorRank.md">TensorRank
   *      documentation</a>
   */
  public final static IBuiltInSymbol TensorRank = F.initFinalSymbol("TensorRank", ID.TensorRank);

  public final static IBuiltInSymbol TensorSymmetry =
      F.initFinalSymbol("TensorSymmetry", ID.TensorSymmetry);

  public final static IBuiltInSymbol TestID = F.initFinalSymbol("TestID", ID.TestID);

  /**
   * TestReport("file-name-string") - load the unit tests from a `file-name-string` and print a
   * summary of the `VerificationTest` included in the file.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TestReport.md">TestReport
   *      documentation</a>
   */
  public final static IBuiltInSymbol TestReport = F.initFinalSymbol("TestReport", ID.TestReport);

  public final static IBuiltInSymbol TestReportObject =
      F.initFinalSymbol("TestReportObject", ID.TestReportObject);

  /**
   * TestResultObject( ... ) - is an association wrapped in a `TestResultObject`returned from
   * `VerificationTest` which stores the results from executing a single unit test.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TestResultObject.md">TestResultObject
   *      documentation</a>
   */
  public final static IBuiltInSymbol TestResultObject =
      F.initFinalSymbol("TestResultObject", ID.TestResultObject);

  public final static IBuiltInSymbol Tetrahedron = F.initFinalSymbol("Tetrahedron", ID.Tetrahedron);

  public final static IBuiltInSymbol Text = F.initFinalSymbol("Text", ID.Text);

  public final static IBuiltInSymbol TextCell = F.initFinalSymbol("TextCell", ID.TextCell);

  public final static IBuiltInSymbol TextElement = F.initFinalSymbol("TextElement", ID.TextElement);

  public final static IBuiltInSymbol TextString = F.initFinalSymbol("TextString", ID.TextString);

  public final static IBuiltInSymbol TextStructure =
      F.initFinalSymbol("TextStructure", ID.TextStructure);

  public final static IBuiltInSymbol Thickness = F.initFinalSymbol("Thickness", ID.Thickness);

  /**
   * Thread(f(args) - threads `f` over any lists that appear in `args`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Thread.md">Thread
   *      documentation</a>
   */
  public final static IBuiltInSymbol Thread = F.initFinalSymbol("Thread", ID.Thread);

  /**
   * Through(p(f)[x]) - gives `p(f(x))`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Through.md">Through
   *      documentation</a>
   */
  public final static IBuiltInSymbol Through = F.initFinalSymbol("Through", ID.Through);

  /**
   * Throw(value) - stops evaluation and returns `value` as the value of the nearest enclosing
   * `Catch`. `Catch(value, tag)` is caught only by `Catch(expr, form)`, where `tag` matches `form`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Throw.md">Throw
   *      documentation</a>
   */
  public final static IBuiltInSymbol Throw = F.initFinalSymbol("Throw", ID.Throw);

  /**
   * TimeConstrained(expression, seconds) - stop evaluation of `expression` if time measurement of
   * the evaluation exceeds `seconds` and return `$Aborted`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeConstrained.md">TimeConstrained
   *      documentation</a>
   */
  public final static IBuiltInSymbol TimeConstrained =
      F.initFinalSymbol("TimeConstrained", ID.TimeConstrained);

  public final static IBuiltInSymbol TimeObject = F.initFinalSymbol("TimeObject", ID.TimeObject);

  public final static IBuiltInSymbol TimeRemaining =
      F.initFinalSymbol("TimeRemaining", ID.TimeRemaining);

  /**
   * TimeValue(p, i, n) - returns a time value calculation.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeValue.md">TimeValue
   *      documentation</a>
   */
  public final static IBuiltInSymbol TimeValue = F.initFinalSymbol("TimeValue", ID.TimeValue);

  /**
   * Times(a, b, ...) - represents the product of the terms `a, b, ...`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times
   *      documentation</a>
   */
  public final static IBuiltInSymbol Times = F.initFinalSymbol("Times", ID.Times);

  /**
   * TimesBy(x, dx) - is equivalent to `x = x * dx`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimesBy.md">TimesBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol TimesBy = F.initFinalSymbol("TimesBy", ID.TimesBy);

  /**
   * Timing(x) - returns a list with the first entry containing the evaluation CPU time of `x` and
   * the second entry is the evaluation result of `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Timing.md">Timing
   *      documentation</a>
   */
  public final static IBuiltInSymbol Timing = F.initFinalSymbol("Timing", ID.Timing);

  public final static IBuiltInSymbol ToBoxes = F.initFinalSymbol("ToBoxes", ID.ToBoxes);

  /**
   * ToCharacterCode(string) - converts `string` into a list of corresponding integer character
   * codes.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToCharacterCode.md">ToCharacterCode
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToCharacterCode =
      F.initFinalSymbol("ToCharacterCode", ID.ToCharacterCode);

  /**
   * ToExpression("string", form) - converts the `string` given in `form` into an expression.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToExpression.md">ToExpression
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToExpression =
      F.initFinalSymbol("ToExpression", ID.ToExpression);

  /**
   * ToLowerCase(string) - converts `string` into a string of corresponding lowercase character
   * codes.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToLowerCase.md">ToLowerCase
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToLowerCase = F.initFinalSymbol("ToLowerCase", ID.ToLowerCase);

  /**
   * ToPolarCoordinates({x, y}) - return the polar coordinates for the cartesian coordinates `{x,
   * y}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToPolarCoordinates.md">ToPolarCoordinates
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToPolarCoordinates =
      F.initFinalSymbol("ToPolarCoordinates", ID.ToPolarCoordinates);

  public final static IBuiltInSymbol ToRadicals = F.initFinalSymbol("ToRadicals", ID.ToRadicals);

  /**
   * ToString(expr) - converts `expr` into a string.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToString.md">ToString
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToString = F.initFinalSymbol("ToString", ID.ToString);

  /**
   * ToUnicode(string) - converts `string` into a string of corresponding unicode character codes.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToUnicode.md">ToUnicode
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToUnicode = F.initFinalSymbol("ToUnicode", ID.ToUnicode);

  /**
   * ToUpperCase(string) - converts `string` into a string of corresponding uppercase character
   * codes.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToUpperCase.md">ToUpperCase
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToUpperCase = F.initFinalSymbol("ToUpperCase", ID.ToUpperCase);

  public final static IBuiltInSymbol Today = F.initFinalSymbol("Today", ID.Today);

  /**
   * ToeplitzMatrix(n) - gives a toeplitz matrix with the dimension `n`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToeplitzMatrix.md">ToeplitzMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToeplitzMatrix =
      F.initFinalSymbol("ToeplitzMatrix", ID.ToeplitzMatrix);

  /**
   * Together(expr) - writes sums of fractions in `expr` together.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Together.md">Together
   *      documentation</a>
   */
  public final static IBuiltInSymbol Together = F.initFinalSymbol("Together", ID.Together);

  public final static IBuiltInSymbol TooLarge = F.initFinalSymbol("TooLarge", ID.TooLarge);

  public final static IBuiltInSymbol Top = F.initFinalSymbol("Top", ID.Top);

  /**
   * Total(list) - adds all values in `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Total.md">Total
   *      documentation</a>
   */
  public final static IBuiltInSymbol Total = F.initFinalSymbol("Total", ID.Total);

  /**
   * Tr(matrix) - computes the trace of the `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tr.md">Tr
   *      documentation</a>
   */
  public final static IBuiltInSymbol Tr = F.initFinalSymbol("Tr", ID.Tr);

  /**
   * Trace(expr) - return the evaluation steps which are used to get the result.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Trace.md">Trace
   *      documentation</a>
   */
  public final static IBuiltInSymbol Trace = F.initFinalSymbol("Trace", ID.Trace);

  public final static IBuiltInSymbol TraceForm = F.initFinalSymbol("TraceForm", ID.TraceForm);

  public final static IBuiltInSymbol TraditionalForm =
      F.initFinalSymbol("TraditionalForm", ID.TraditionalForm);

  /**
   * Transliterate("string") - try converting the given string to a similar ASCII string
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Transliterate.md">Transliterate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Transliterate =
      F.initFinalSymbol("Transliterate", ID.Transliterate);

  /**
   * Transpose(m) - transposes rows and columns in the matrix `m`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Transpose.md">Transpose
   *      documentation</a>
   */
  public final static IBuiltInSymbol Transpose = F.initFinalSymbol("Transpose", ID.Transpose);

  /**
   * TreeForm(expr) - create a tree visualization from the given expression `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TreeForm.md">TreeForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol TreeForm = F.initFinalSymbol("TreeForm", ID.TreeForm);

  public final static IBuiltInSymbol Triangle = F.initFinalSymbol("Triangle", ID.Triangle);

  public final static IBuiltInSymbol Trig = F.initFinalSymbol("Trig", ID.Trig);

  /**
   * TrigExpand(expr) - expands out trigonometric expressions in `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigExpand.md">TrigExpand
   *      documentation</a>
   */
  public final static IBuiltInSymbol TrigExpand = F.initFinalSymbol("TrigExpand", ID.TrigExpand);

  /**
   * TrigReduce(expr) - rewrites products and powers of trigonometric functions in `expr` in terms
   * of trigonometric functions with combined arguments.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigReduce.md">TrigReduce
   *      documentation</a>
   */
  public final static IBuiltInSymbol TrigReduce = F.initFinalSymbol("TrigReduce", ID.TrigReduce);

  /**
   * TrigToExp(expr) - converts trigonometric functions in `expr` to exponentials.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigToExp.md">TrigToExp
   *      documentation</a>
   */
  public final static IBuiltInSymbol TrigToExp = F.initFinalSymbol("TrigToExp", ID.TrigToExp);

  /**
   * True - the constant `True` represents the boolean value **true**
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/True.md">True
   *      documentation</a>
   */
  public final static IBuiltInSymbol True = F.initFinalSymbol("True", ID.True);

  /**
   * TrueQ(expr) - returns `True` if and only if `expr` is `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrueQ.md">TrueQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol TrueQ = F.initFinalSymbol("TrueQ", ID.TrueQ);

  public final static IBuiltInSymbol Tube = F.initFinalSymbol("Tube", ID.Tube);

  public final static IBuiltInSymbol TukeyWindow = F.initFinalSymbol("TukeyWindow", ID.TukeyWindow);

  /**
   * Tuples(list, n) - creates a list of all `n`-tuples of elements in `list`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tuples.md">Tuples
   *      documentation</a>
   */
  public final static IBuiltInSymbol Tuples = F.initFinalSymbol("Tuples", ID.Tuples);

  public final static IBuiltInSymbol TwoWayRule = F.initFinalSymbol("TwoWayRule", ID.TwoWayRule);

  public final static IBuiltInSymbol URLFetch = F.initFinalSymbol("URLFetch", ID.URLFetch);

  public final static IBuiltInSymbol Undefined = F.initFinalSymbol("Undefined", ID.Undefined);

  public final static IBuiltInSymbol Underoverscript =
      F.initFinalSymbol("Underoverscript", ID.Underoverscript);

  /**
   * UndirectedEdge(a, b) - is an undirected edge between the vertices `a` and `b` in a `graph`
   * object.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UndirectedEdge.md">UndirectedEdge
   *      documentation</a>
   */
  public final static IBuiltInSymbol UndirectedEdge =
      F.initFinalSymbol("UndirectedEdge", ID.UndirectedEdge);

  /**
   * Unequal(x, y) - yields `False` if `x` and `y` are known to be equal, or `True` if `x` and `y`
   * are known to be unequal.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unequal.md">Unequal
   *      documentation</a>
   */
  public final static IBuiltInSymbol Unequal = F.initFinalSymbol("Unequal", ID.Unequal);

  public final static IBuiltInSymbol UnequalTo = F.initFinalSymbol("UnequalTo", ID.UnequalTo);

  /**
   * Unevaluated(expr) - temporarily leaves `expr` in an unevaluated form when it appears as a
   * function argument.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unevaluated.md">Unevaluated
   *      documentation</a>
   */
  public final static IBuiltInSymbol Unevaluated = F.initFinalSymbol("Unevaluated", ID.Unevaluated);

  /**
   * UniformDistribution({min, max}) - returns a uniform distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UniformDistribution.md">UniformDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol UniformDistribution =
      F.initFinalSymbol("UniformDistribution", ID.UniformDistribution);

  /**
   * Union(set1, set2) - get the union set from `set1` and `set2`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Union.md">Union
   *      documentation</a>
   */
  public final static IBuiltInSymbol Union = F.initFinalSymbol("Union", ID.Union);

  /**
   * Unique(expr) - create a unique symbol of the form `expr$...`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unique.md">Unique
   *      documentation</a>
   */
  public final static IBuiltInSymbol Unique = F.initFinalSymbol("Unique", ID.Unique);

  /**
   * UnitConvert(quantity) - convert the `quantity` to the base unit
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitConvert.md">UnitConvert
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnitConvert = F.initFinalSymbol("UnitConvert", ID.UnitConvert);

  /**
   * UnitStep(expr) - returns `0`, if `expr` is less than `0` and returns `1`, if `expr` is greater
   * equal than `0`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitStep.md">UnitStep
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnitStep = F.initFinalSymbol("UnitStep", ID.UnitStep);

  public final static IBuiltInSymbol UnitTriangle =
      F.initFinalSymbol("UnitTriangle", ID.UnitTriangle);

  /**
   * UnitVector(position) - returns a unit vector with element `1` at the given `position`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitVector.md">UnitVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnitVector = F.initFinalSymbol("UnitVector", ID.UnitVector);

  public final static IBuiltInSymbol UnitaryMatrixQ =
      F.initFinalSymbol("UnitaryMatrixQ", ID.UnitaryMatrixQ);

  /**
   * Unitize(expr) - maps a non-zero `expr` to `1`, and a zero `expr` to `0`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unitize.md">Unitize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Unitize = F.initFinalSymbol("Unitize", ID.Unitize);

  public final static IBuiltInSymbol Unknown = F.initFinalSymbol("Unknown", ID.Unknown);

  public final static IBuiltInSymbol Unprotect = F.initFinalSymbol("Unprotect", ID.Unprotect);

  /**
   * UnsameQ(x, y) - returns `True` if `x` and `y` are not structurally identical.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnsameQ.md">UnsameQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnsameQ = F.initFinalSymbol("UnsameQ", ID.UnsameQ);

  /**
   * Unset(expr) - removes any definitions belonging to the left-hand-side `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unset.md">Unset
   *      documentation</a>
   */
  public final static IBuiltInSymbol Unset = F.initFinalSymbol("Unset", ID.Unset);

  public final static IBuiltInSymbol UpSet = F.initFinalSymbol("UpSet", ID.UpSet);

  public final static IBuiltInSymbol UpSetDelayed =
      F.initFinalSymbol("UpSetDelayed", ID.UpSetDelayed);

  public final static IBuiltInSymbol UpTo = F.initFinalSymbol("UpTo", ID.UpTo);

  /**
   * UpValues(symbol) - prints the up-value rules associated with `symbol`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpValues.md">UpValues
   *      documentation</a>
   */
  public final static IBuiltInSymbol UpValues = F.initFinalSymbol("UpValues", ID.UpValues);

  /**
   * UpperCaseQ(str) - is `True` if the given `str` is a string which only contains upper case
   * characters.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperCaseQ.md">UpperCaseQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol UpperCaseQ = F.initFinalSymbol("UpperCaseQ", ID.UpperCaseQ);

  /**
   * UpperTriangularize(matrix) - create a upper triangular matrix from the given `matrix`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperTriangularize.md">UpperTriangularize
   *      documentation</a>
   */
  public final static IBuiltInSymbol UpperTriangularize =
      F.initFinalSymbol("UpperTriangularize", ID.UpperTriangularize);

  public final static IBuiltInSymbol UseTypeChecking =
      F.initFinalSymbol("UseTypeChecking", ID.UseTypeChecking);

  /**
   * ValueQ(expr) - returns `True` if and only if `expr` is defined.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ValueQ.md">ValueQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ValueQ = F.initFinalSymbol("ValueQ", ID.ValueQ);

  /**
   * Values(association) - return a list of values of the `association`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Values.md">Values
   *      documentation</a>
   */
  public final static IBuiltInSymbol Values = F.initFinalSymbol("Values", ID.Values);

  /**
   * VandermondeMatrix(n) - gives the Vandermonde matrix with `n` rows and columns.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VandermondeMatrix.md">VandermondeMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol VandermondeMatrix =
      F.initFinalSymbol("VandermondeMatrix", ID.VandermondeMatrix);

  public final static IBuiltInSymbol Variable = F.initFinalSymbol("Variable", ID.Variable);

  /**
   * Variables(expr) - gives a list of the variables that appear in the polynomial `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Variables.md">Variables
   *      documentation</a>
   */
  public final static IBuiltInSymbol Variables = F.initFinalSymbol("Variables", ID.Variables);

  /**
   * Variance(list) - computes the variance of `list`. `list` may consist of numerical values or
   * symbols. Numerical values may be real or complex.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Variance.md">Variance
   *      documentation</a>
   */
  public final static IBuiltInSymbol Variance = F.initFinalSymbol("Variance", ID.Variance);

  /**
   * VectorAngle(u, v) - gives the angles between vectors `u` and `v`
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorAngle.md">VectorAngle
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorAngle = F.initFinalSymbol("VectorAngle", ID.VectorAngle);

  /**
   * VectorQ(v) - returns `True` if `v` is a list of elements which are not themselves lists.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorQ.md">VectorQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorQ = F.initFinalSymbol("VectorQ", ID.VectorQ);

  public final static IBuiltInSymbol Vectors = F.initFinalSymbol("Vectors", ID.Vectors);

  /**
   * Verbatim(expr) - prevents pattern constructs in `expr` from taking effect, allowing them to
   * match themselves.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Verbatim.md">Verbatim
   *      documentation</a>
   */
  public final static IBuiltInSymbol Verbatim = F.initFinalSymbol("Verbatim", ID.Verbatim);

  /**
   * VerificationTest(test-expr) - create a `TestResultObject` by testing if `test-expr` evaluates
   * to `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VerificationTest.md">VerificationTest
   *      documentation</a>
   */
  public final static IBuiltInSymbol VerificationTest =
      F.initFinalSymbol("VerificationTest", ID.VerificationTest);

  /**
   * VertexEccentricity(graph, vertex) - compute the eccentricity of `vertex` in the `graph`. It's
   * the length of the longest shortest path from the `vertex` to every other vertex in the `graph`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexEccentricity.md">VertexEccentricity
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexEccentricity =
      F.initFinalSymbol("VertexEccentricity", ID.VertexEccentricity);

  public final static IBuiltInSymbol VertexLabels =
      F.initFinalSymbol("VertexLabels", ID.VertexLabels);

  /**
   * VertexList(graph) - convert the `graph` into a list of vertices.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexList.md">VertexList
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexList = F.initFinalSymbol("VertexList", ID.VertexList);

  /**
   * VertexQ(graph, vertex) - test if `vertex` is a vertex in the `graph` object.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexQ.md">VertexQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexQ = F.initFinalSymbol("VertexQ", ID.VertexQ);

  public final static IBuiltInSymbol VertexShapeFunction =
      F.initFinalSymbol("VertexShapeFunction", ID.VertexShapeFunction);

  public final static IBuiltInSymbol VertexSize = F.initFinalSymbol("VertexSize", ID.VertexSize);

  public final static IBuiltInSymbol VertexStyle = F.initFinalSymbol("VertexStyle", ID.VertexStyle);

  public final static IBuiltInSymbol ViewPoint = F.initFinalSymbol("ViewPoint", ID.ViewPoint);

  public final static IBuiltInSymbol Volume = F.initFinalSymbol("Volume", ID.Volume);

  public final static IBuiltInSymbol WeberE = F.initFinalSymbol("WeberE", ID.WeberE);

  /**
   * WeibullDistribution(a, b) - returns a Weibull distribution.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeibullDistribution.md">WeibullDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol WeibullDistribution =
      F.initFinalSymbol("WeibullDistribution", ID.WeibullDistribution);

  public final static IBuiltInSymbol WeierstrassHalfPeriods =
      F.initFinalSymbol("WeierstrassHalfPeriods", ID.WeierstrassHalfPeriods);

  public final static IBuiltInSymbol WeierstrassInvariants =
      F.initFinalSymbol("WeierstrassInvariants", ID.WeierstrassInvariants);

  /**
   * WeierstrassP(expr, {n1, n2}) - Weierstrass elliptic function.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeierstrassP.md">WeierstrassP
   *      documentation</a>
   */
  public final static IBuiltInSymbol WeierstrassP =
      F.initFinalSymbol("WeierstrassP", ID.WeierstrassP);

  public final static IBuiltInSymbol WeierstrassPPrime =
      F.initFinalSymbol("WeierstrassPPrime", ID.WeierstrassPPrime);

  /**
   * WeightedAdjacencyMatrix(graph) - convert the `graph` into a weighted adjacency matrix in sparse
   * array format.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeightedAdjacencyMatrix.md">WeightedAdjacencyMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol WeightedAdjacencyMatrix =
      F.initFinalSymbol("WeightedAdjacencyMatrix", ID.WeightedAdjacencyMatrix);

  public final static IBuiltInSymbol WeightedData =
      F.initFinalSymbol("WeightedData", ID.WeightedData);

  /**
   * WeightedGraphQ(expr) - test if `expr` is an explicit weighted graph object.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeightedGraphQ.md">WeightedGraphQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol WeightedGraphQ =
      F.initFinalSymbol("WeightedGraphQ", ID.WeightedGraphQ);

  public final static IBuiltInSymbol WheelGraph = F.initFinalSymbol("WheelGraph", ID.WheelGraph);

  /**
   * Which(cond1, expr1, cond2, expr2, ...) - yields `expr1` if `cond1` evaluates to `True`, `expr2`
   * if `cond2` evaluates to `True`, etc.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Which.md">Which
   *      documentation</a>
   */
  public final static IBuiltInSymbol Which = F.initFinalSymbol("Which", ID.Which);

  /**
   * While(test, body) - evaluates `body` as long as test evaluates to `True`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/While.md">While
   *      documentation</a>
   */
  public final static IBuiltInSymbol While = F.initFinalSymbol("While", ID.While);

  public final static IBuiltInSymbol White = F.initFinalSymbol("White", ID.White);

  /**
   * Whitespace - represents a sequence of whitespace characters.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Whitespace.md">Whitespace
   *      documentation</a>
   */
  public final static IBuiltInSymbol Whitespace = F.initFinalSymbol("Whitespace", ID.Whitespace);

  /**
   * WhitespaceCharacter - represents a single whitespace character.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WhitespaceCharacter.md">WhitespaceCharacter
   *      documentation</a>
   */
  public final static IBuiltInSymbol WhitespaceCharacter =
      F.initFinalSymbol("WhitespaceCharacter", ID.WhitespaceCharacter);

  public final static IBuiltInSymbol WhittakerM = F.initFinalSymbol("WhittakerM", ID.WhittakerM);

  public final static IBuiltInSymbol WhittakerW = F.initFinalSymbol("WhittakerW", ID.WhittakerW);

  /**
   * With({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables` by
   * replacing the local variables in `expr`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/With.md">With
   *      documentation</a>
   */
  public final static IBuiltInSymbol With = F.initFinalSymbol("With", ID.With);

  public final static IBuiltInSymbol Word = F.initFinalSymbol("Word", ID.Word);

  /**
   * WordBoundary - represents the boundary between words.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WordBoundary.md">WordBoundary
   *      documentation</a>
   */
  public final static IBuiltInSymbol WordBoundary =
      F.initFinalSymbol("WordBoundary", ID.WordBoundary);

  /**
   * WordCharacter] - represents a single letter or digit character.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WordCharacter.md">WordCharacter
   *      documentation</a>
   */
  public final static IBuiltInSymbol WordCharacter =
      F.initFinalSymbol("WordCharacter", ID.WordCharacter);

  public final static IBuiltInSymbol WordSeparators =
      F.initFinalSymbol("WordSeparators", ID.WordSeparators);

  public final static IBuiltInSymbol Write = F.initFinalSymbol("Write", ID.Write);

  public final static IBuiltInSymbol WriteString = F.initFinalSymbol("WriteString", ID.WriteString);

  /**
   * Xor(arg1, arg2, ...) - Logical XOR (exclusive OR) function. Returns `True` if an odd number of
   * the arguments are `True` and the rest are `False`. Returns `False` if an even number of the
   * arguments are `True` and the rest are `False`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Xor.md">Xor
   *      documentation</a>
   */
  public final static IBuiltInSymbol Xor = F.initFinalSymbol("Xor", ID.Xor);

  public final static IBuiltInSymbol Yellow = F.initFinalSymbol("Yellow", ID.Yellow);

  /**
   * YuleDissimilarity(u, v) - returns the Yule dissimilarity between the two boolean 1-D lists `u`
   * and `v`, which is defined as `R / (c_tt * c_ff + R / 2)` where `n` is `len(u)`, `c_ij` is the
   * number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * c_tf * c_ft`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/YuleDissimilarity.md">YuleDissimilarity
   *      documentation</a>
   */
  public final static IBuiltInSymbol YuleDissimilarity =
      F.initFinalSymbol("YuleDissimilarity", ID.YuleDissimilarity);

  public final static IBuiltInSymbol ZeroSymmetric =
      F.initFinalSymbol("ZeroSymmetric", ID.ZeroSymmetric);

  public final static IBuiltInSymbol ZeroTest = F.initFinalSymbol("ZeroTest", ID.ZeroTest);

  /**
   * Zeta(z) - returns the Riemann zeta function of `z`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Zeta.md">Zeta
   *      documentation</a>
   */
  public final static IBuiltInSymbol Zeta = F.initFinalSymbol("Zeta", ID.Zeta);

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
  public static final ISymbol CSymbol = initFinalHiddenSymbol("C"); // don't use constant
                                                                    // BuiltinSymbol 'C' here
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
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
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
    final ISymbol symbol = new Symbol(symbolName, org.matheclipse.core.expression.Context.DUMMY);
    // TODO make this a real protected symbol
    // symbol.setAttributes(ISymbol.PROTECTED);
    HIDDEN_SYMBOLS_MAP.put(symbolName, symbol);
    return symbol;
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

  public static IExpr exprID(IExpr expr) {
    Short id = GLOBAL_IDS_MAP.get(expr);
    if (id != null) {
      return new ExprID(id);
    }
    return expr;
  }

  /**
   * Is the symbol <code>domain</code> one of the following predefined domain symbols: <code>
   * Algebraics, Booleans, Complexes, Integers, Primes, Rationals, Reals</code>
   *
   * @param domain the symbol which can represent a predefined domain
   * @return
   */
  public static boolean isDomain(ISymbol domain) {
    return domain == Algebraics || domain == Booleans || domain == Complexes || domain == Integers
        || domain == Primes || domain == Rationals || domain == Reals;
  }
}
