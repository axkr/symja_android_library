package org.matheclipse.core.expression;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;
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
      new HashMap<>((EXPRID_MAX_BUILTIN_LENGTH + 1000) * 4 / 3 + 1);

  static final Map<String, ISymbol> HIDDEN_SYMBOLS_MAP =
      Config.TRIE_STRING2SYMBOL_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();

  public static IBuiltInSymbol symbol(int id) {
    return BUILT_IN_SYMBOLS[id];
  }

  public static final IBuiltInSymbol $Aborted = initFinalSymbol("$Aborted", ID.$Aborted);

  /**
   * $Assumptions - contains the default assumptions for `Integrate`, `Refine` and `Simplify`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$Assumptions.md">$Assumptions
   *     documentation</a>
   */
  public static final IBuiltInSymbol $Assumptions =
      initFinalSymbol("$Assumptions", ID.$Assumptions);

  public static final IBuiltInSymbol $BaseDirectory =
      initFinalSymbol("$BaseDirectory", ID.$BaseDirectory);

  public static final IBuiltInSymbol $Cancel = initFinalSymbol("$Cancel", ID.$Cancel);

  public static final IBuiltInSymbol $Context = initFinalSymbol("$Context", ID.$Context);

  public static final IBuiltInSymbol $ContextPath =
      initFinalSymbol("$ContextPath", ID.$ContextPath);

  public static final IBuiltInSymbol $CreationDate =
      initFinalSymbol("$CreationDate", ID.$CreationDate);

  public static final IBuiltInSymbol $DisplayFunction =
      initFinalSymbol("$DisplayFunction", ID.$DisplayFunction);

  public static final IBuiltInSymbol $Failed = initFinalSymbol("$Failed", ID.$Failed);

  /**
   * $HistoryLength - specifies the maximum number of `In` and `Out` entries.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$HistoryLength.md">$HistoryLength
   *     documentation</a>
   */
  public static final IBuiltInSymbol $HistoryLength =
      initFinalSymbol("$HistoryLength", ID.$HistoryLength);

  public static final IBuiltInSymbol $HomeDirectory =
      initFinalSymbol("$HomeDirectory", ID.$HomeDirectory);

  public static final IBuiltInSymbol $IdentityMatrix =
      initFinalSymbol("$IdentityMatrix", ID.$IdentityMatrix);

  public static final IBuiltInSymbol $Input = initFinalSymbol("$Input", ID.$Input);

  public static final IBuiltInSymbol $InputFileName =
      initFinalSymbol("$InputFileName", ID.$InputFileName);

  /**
   * $IterationLimit - specifies the maximum number of times a reevaluation of an expression may
   * happen.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$IterationLimit.md">$IterationLimit
   *     documentation</a>
   */
  public static final IBuiltInSymbol $IterationLimit =
      initFinalSymbol("$IterationLimit", ID.$IterationLimit);

  /**
   * $Line - holds the current input line number.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$Line.md">$Line
   *     documentation</a>
   */
  public static final IBuiltInSymbol $Line = initFinalSymbol("$Line", ID.$Line);

  public static final IBuiltInSymbol $MachineEpsilon =
      initFinalSymbol("$MachineEpsilon", ID.$MachineEpsilon);

  public static final IBuiltInSymbol $MachinePrecision =
      initFinalSymbol("$MachinePrecision", ID.$MachinePrecision);

  public static final IBuiltInSymbol $MaxMachineNumber =
      initFinalSymbol("$MaxMachineNumber", ID.$MaxMachineNumber);

  public static final IBuiltInSymbol $MessageList =
      initFinalSymbol("$MessageList", ID.$MessageList);

  public static final IBuiltInSymbol $MinMachineNumber =
      initFinalSymbol("$MinMachineNumber", ID.$MinMachineNumber);

  public static final IBuiltInSymbol $Notebooks = initFinalSymbol("$Notebooks", ID.$Notebooks);

  /**
   * $OperatingSystem - gives the type of operating system ("Windows", "MacOSX", or "Unix") running
   * Symja.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$OperatingSystem.md">$OperatingSystem
   *     documentation</a>
   */
  public static final IBuiltInSymbol $OperatingSystem =
      initFinalSymbol("$OperatingSystem", ID.$OperatingSystem);

  public static final IBuiltInSymbol $OutputSizeLimit =
      initFinalSymbol("$OutputSizeLimit", ID.$OutputSizeLimit);

  public static final IBuiltInSymbol $Packages = initFinalSymbol("$Packages", ID.$Packages);

  public static final IBuiltInSymbol $Path = initFinalSymbol("$Path", ID.$Path);

  public static final IBuiltInSymbol $PathnameSeparator =
      initFinalSymbol("$PathnameSeparator", ID.$PathnameSeparator);

  public static final IBuiltInSymbol $PrePrint = initFinalSymbol("$PrePrint", ID.$PrePrint);

  public static final IBuiltInSymbol $PreRead = initFinalSymbol("$PreRead", ID.$PreRead);

  /**
   * $RecursionLimit - holds the current input line number
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$RecursionLimit.md">$RecursionLimit
   *     documentation</a>
   */
  public static final IBuiltInSymbol $RecursionLimit =
      initFinalSymbol("$RecursionLimit", ID.$RecursionLimit);

  public static final IBuiltInSymbol $RootDirectory =
      initFinalSymbol("$RootDirectory", ID.$RootDirectory);

  /**
   * $ScriptCommandLine - is a list of string arguments when running Symja in script mode. The list
   * starts with the name of the script.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$ScriptCommandLine.md">$ScriptCommandLine
   *     documentation</a>
   */
  public static final IBuiltInSymbol $ScriptCommandLine =
      initFinalSymbol("$ScriptCommandLine", ID.$ScriptCommandLine);

  public static final IBuiltInSymbol $SingleEntryMatrix =
      initFinalSymbol("$SingleEntryMatrix", ID.$SingleEntryMatrix);

  public static final IBuiltInSymbol $SystemCharacterEncoding =
      initFinalSymbol("$SystemCharacterEncoding", ID.$SystemCharacterEncoding);

  public static final IBuiltInSymbol $SystemMemory =
      initFinalSymbol("$SystemMemory", ID.$SystemMemory);

  public static final IBuiltInSymbol $TemporaryDirectory =
      initFinalSymbol("$TemporaryDirectory", ID.$TemporaryDirectory);

  public static final IBuiltInSymbol $UserBaseDirectory =
      initFinalSymbol("$UserBaseDirectory", ID.$UserBaseDirectory);

  public static final IBuiltInSymbol $UserName = initFinalSymbol("$UserName", ID.$UserName);

  public static final IBuiltInSymbol $Version = initFinalSymbol("$Version", ID.$Version);

  /**
   * Abort() - aborts an evaluation completely and returns `$Aborted`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Abort.md">Abort
   *     documentation</a>
   */
  public static final IBuiltInSymbol Abort = initFinalSymbol("Abort", ID.Abort);

  /**
   * Abs(expr) - returns the absolute value of the real or complex number `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Abs.md">Abs
   *     documentation</a>
   */
  public static final IBuiltInSymbol Abs = initFinalSymbol("Abs", ID.Abs);

  /**
   * AbsArg(expr) - returns a list of 2 values of the complex number `Abs(expr), Arg(expr)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AbsArg.md">AbsArg
   *     documentation</a>
   */
  public static final IBuiltInSymbol AbsArg = initFinalSymbol("AbsArg", ID.AbsArg);

  public static final IBuiltInSymbol AbsoluteCorrelation =
      initFinalSymbol("AbsoluteCorrelation", ID.AbsoluteCorrelation);

  public static final IBuiltInSymbol AbsoluteTime =
      initFinalSymbol("AbsoluteTime", ID.AbsoluteTime);

  /**
   * AbsoluteTiming(x) - returns a list with the first entry containing the evaluation time of `x`
   * and the second entry is the evaluation result of `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AbsoluteTiming.md">AbsoluteTiming
   *     documentation</a>
   */
  public static final IBuiltInSymbol AbsoluteTiming =
      initFinalSymbol("AbsoluteTiming", ID.AbsoluteTiming);

  /**
   * Accumulate(list) - accumulate the values of `list` returning a new list.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Accumulate.md">Accumulate
   *     documentation</a>
   */
  public static final IBuiltInSymbol Accumulate = initFinalSymbol("Accumulate", ID.Accumulate);

  public static final IBuiltInSymbol AddSides = initFinalSymbol("AddSides", ID.AddSides);

  /**
   * AddTo(x, dx) - is equivalent to `x = x + dx`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AddTo.md">AddTo
   *     documentation</a>
   */
  public static final IBuiltInSymbol AddTo = initFinalSymbol("AddTo", ID.AddTo);

  public static final IBuiltInSymbol AddToClassPath =
      initFinalSymbol("AddToClassPath", ID.AddToClassPath);

  /**
   * AdjacencyMatrix(graph) - convert the `graph` into a adjacency matrix in sparse array format.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AdjacencyMatrix.md">AdjacencyMatrix
   *     documentation</a>
   */
  public static final IBuiltInSymbol AdjacencyMatrix =
      initFinalSymbol("AdjacencyMatrix", ID.AdjacencyMatrix);

  /**
   * AiryAi(z) - returns the Airy function of the first kind of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryAi.md">AiryAi
   *     documentation</a>
   */
  public static final IBuiltInSymbol AiryAi = initFinalSymbol("AiryAi", ID.AiryAi);

  /**
   * AiryAiPrime(z) - returns the derivative of the `AiryAi` function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryAiPrime.md">AiryAiPrime
   *     documentation</a>
   */
  public static final IBuiltInSymbol AiryAiPrime = initFinalSymbol("AiryAiPrime", ID.AiryAiPrime);

  /**
   * AiryBi(z) - returns the Airy function of the second kind of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryBi.md">AiryBi
   *     documentation</a>
   */
  public static final IBuiltInSymbol AiryBi = initFinalSymbol("AiryBi", ID.AiryBi);

  /**
   * AiryBiPrime(z) - returns the derivative of the `AiryBi` function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryBiPrime.md">AiryBiPrime
   *     documentation</a>
   */
  public static final IBuiltInSymbol AiryBiPrime = initFinalSymbol("AiryBiPrime", ID.AiryBiPrime);

  public static final IBuiltInSymbol AlgebraicNumber =
      initFinalSymbol("AlgebraicNumber", ID.AlgebraicNumber);

  public static final IBuiltInSymbol Algebraics = initFinalSymbol("Algebraics", ID.Algebraics);

  /**
   * All - is a possible value for `Span` and `Quiet`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/All.md">All
   *     documentation</a>
   */
  public static final IBuiltInSymbol All = initFinalSymbol("All", ID.All);

  /**
   * AllTrue({expr1, expr2, ...}, test) - returns `True` if all applications of `test` to `expr1,
   * expr2, ...` evaluate to `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AllTrue.md">AllTrue
   *     documentation</a>
   */
  public static final IBuiltInSymbol AllTrue = initFinalSymbol("AllTrue", ID.AllTrue);

  public static final IBuiltInSymbol AllowShortContext =
      initFinalSymbol("AllowShortContext", ID.AllowShortContext);

  public static final IBuiltInSymbol AllowedHeads =
      initFinalSymbol("AllowedHeads", ID.AllowedHeads);

  /**
   * Alphabet() - gives the list of lowercase letters `a-z` in the English or Latin alphabet .
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Alphabet.md">Alphabet
   *     documentation</a>
   */
  public static final IBuiltInSymbol Alphabet = initFinalSymbol("Alphabet", ID.Alphabet);

  /**
   * Alternatives(p1, p2, ..., p_i) - is a pattern that matches any of the patterns `p1, p2,....,
   * p_i`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Alternatives.md">Alternatives
   *     documentation</a>
   */
  public static final IBuiltInSymbol Alternatives =
      initFinalSymbol("Alternatives", ID.Alternatives);

  /**
   * And(expr1, expr2, ...) - `expr1 && expr2 && ...` evaluates each expression in turn, returning
   * `False` as soon as an expression evaluates to `False`. If all expressions evaluate to `True`,
   * `And` returns `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/And.md">And
   *     documentation</a>
   */
  public static final IBuiltInSymbol And = initFinalSymbol("And", ID.And);

  /**
   * AngleVector(phi) - returns the point at angle `phi` on the unit circle.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AngleVector.md">AngleVector
   *     documentation</a>
   */
  public static final IBuiltInSymbol AngleVector = initFinalSymbol("AngleVector", ID.AngleVector);

  public static final IBuiltInSymbol Annotation = initFinalSymbol("Annotation", ID.Annotation);

  /**
   * Annuity(p, t) - returns an annuity object.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Annuity.md">Annuity
   *     documentation</a>
   */
  public static final IBuiltInSymbol Annuity = initFinalSymbol("Annuity", ID.Annuity);

  /**
   * AnnuityDue(p, t) - returns an annuity due object.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnnuityDue.md">AnnuityDue
   *     documentation</a>
   */
  public static final IBuiltInSymbol AnnuityDue = initFinalSymbol("AnnuityDue", ID.AnnuityDue);

  public static final IBuiltInSymbol AntiSymmetric =
      initFinalSymbol("AntiSymmetric", ID.AntiSymmetric);

  /**
   * AntihermitianMatrixQ(m) - returns `True` if `m` is a anti hermitian matrix.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AntihermitianMatrixQ.md">AntihermitianMatrixQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol AntihermitianMatrixQ =
      initFinalSymbol("AntihermitianMatrixQ", ID.AntihermitianMatrixQ);

  /**
   * AntisymmetricMatrixQ(m) - returns `True` if `m` is a anti symmetric matrix.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AntisymmetricMatrixQ.md">AntisymmetricMatrixQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol AntisymmetricMatrixQ =
      initFinalSymbol("AntisymmetricMatrixQ", ID.AntisymmetricMatrixQ);

  /**
   * AnyTrue({expr1, expr2, ...}, test) - returns `True` if any application of `test` to `expr1,
   * expr2, ...` evaluates to `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnyTrue.md">AnyTrue
   *     documentation</a>
   */
  public static final IBuiltInSymbol AnyTrue = initFinalSymbol("AnyTrue", ID.AnyTrue);

  /**
   * Apart(expr) - rewrites `expr` as a sum of individual fractions.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Apart.md">Apart
   *     documentation</a>
   */
  public static final IBuiltInSymbol Apart = initFinalSymbol("Apart", ID.Apart);

  public static final IBuiltInSymbol AppellF1 = initFinalSymbol("AppellF1", ID.AppellF1);

  /**
   * Append(expr, item) - returns `expr` with `item` appended to its leaves.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Append.md">Append
   *     documentation</a>
   */
  public static final IBuiltInSymbol Append = initFinalSymbol("Append", ID.Append);

  /**
   * AppendTo(s, item) - append `item` to value of `s` and sets `s` to the result.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AppendTo.md">AppendTo
   *     documentation</a>
   */
  public static final IBuiltInSymbol AppendTo = initFinalSymbol("AppendTo", ID.AppendTo);

  /**
   * f @ expr - returns `f(expr)`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Apply.md">Apply
   *     documentation</a>
   */
  public static final IBuiltInSymbol Apply = initFinalSymbol("Apply", ID.Apply);

  public static final IBuiltInSymbol ApplySides = initFinalSymbol("ApplySides", ID.ApplySides);

  /**
   * ArcCos(expr) - returns the arc cosine (inverse cosine) of `expr` (measured in radians).
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCos.md">ArcCos
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArcCos = initFinalSymbol("ArcCos", ID.ArcCos);

  /**
   * ArcCosh(z) - returns the inverse hyperbolic cosine of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCosh.md">ArcCosh
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArcCosh = initFinalSymbol("ArcCosh", ID.ArcCosh);

  /**
   * ArcCot(z) - returns the inverse cotangent of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCot.md">ArcCot
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArcCot = initFinalSymbol("ArcCot", ID.ArcCot);

  /**
   * ArcCoth(z) - returns the inverse hyperbolic cotangent of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCoth.md">ArcCoth
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArcCoth = initFinalSymbol("ArcCoth", ID.ArcCoth);

  /**
   * ArcCsc(z) - returns the inverse cosecant of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCsc.md">ArcCsc
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArcCsc = initFinalSymbol("ArcCsc", ID.ArcCsc);

  /**
   * ArcCsch(z) - returns the inverse hyperbolic cosecant of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCsch.md">ArcCsch
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArcCsch = initFinalSymbol("ArcCsch", ID.ArcCsch);

  public static final IBuiltInSymbol ArcLength = initFinalSymbol("ArcLength", ID.ArcLength);

  /**
   * ArcSec(z) - returns the inverse secant of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSec.md">ArcSec
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArcSec = initFinalSymbol("ArcSec", ID.ArcSec);

  /**
   * ArcSech(z) - returns the inverse hyperbolic secant of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSech.md">ArcSech
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArcSech = initFinalSymbol("ArcSech", ID.ArcSech);

  /**
   * ArcSin(expr) - returns the arc sine (inverse sine) of `expr` (measured in radians).
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSin.md">ArcSin
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArcSin = initFinalSymbol("ArcSin", ID.ArcSin);

  /**
   * ArcSinh(z) - returns the inverse hyperbolic sine of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSinh.md">ArcSinh
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArcSinh = initFinalSymbol("ArcSinh", ID.ArcSinh);

  /**
   * ArcTan(expr) - returns the arc tangent (inverse tangent) of `expr` (measured in radians).
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcTan.md">ArcTan
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArcTan = initFinalSymbol("ArcTan", ID.ArcTan);

  /**
   * ArcTanh(z) - returns the inverse hyperbolic tangent of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcTanh.md">ArcTanh
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArcTanh = initFinalSymbol("ArcTanh", ID.ArcTanh);

  public static final IBuiltInSymbol Area = initFinalSymbol("Area", ID.Area);

  /**
   * Arg(expr) - returns the argument of the complex number `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Arg.md">Arg
   *     documentation</a>
   */
  public static final IBuiltInSymbol Arg = initFinalSymbol("Arg", ID.Arg);

  /**
   * ArgMax(function, variable) - returns a maximizer point for a univariate `function`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArgMax.md">ArgMax
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArgMax = initFinalSymbol("ArgMax", ID.ArgMax);

  /**
   * ArgMin(function, variable) - returns a minimizer point for a univariate `function`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArgMin.md">ArgMin
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArgMin = initFinalSymbol("ArgMin", ID.ArgMin);

  /**
   * ArithmeticGeometricMean({a, b, c,...}) - returns the arithmetic geometric mean of `{a, b,
   * c,...}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArithmeticGeometricMean.md">ArithmeticGeometricMean
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArithmeticGeometricMean =
      initFinalSymbol("ArithmeticGeometricMean", ID.ArithmeticGeometricMean);

  /**
   * Array(f, n) - returns the `n`-element list `{f(1), ..., f(n)}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Array.md">Array
   *     documentation</a>
   */
  public static final IBuiltInSymbol Array = initFinalSymbol("Array", ID.Array);

  /**
   * ArrayDepth(a) - returns the depth of the non-ragged array `a`, defined as
   * `Length(Dimensions(a))`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayDepth.md">ArrayDepth
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArrayDepth = initFinalSymbol("ArrayDepth", ID.ArrayDepth);

  /**
   * ArrayPad(list, n) - adds `n` times `0` on the left and right of the `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayPad.md">ArrayPad
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArrayPad = initFinalSymbol("ArrayPad", ID.ArrayPad);

  /**
   * ArrayQ(expr) - tests whether expr is a full array.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayQ.md">ArrayQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArrayQ = initFinalSymbol("ArrayQ", ID.ArrayQ);

  /**
   * ArrayReshape(list-of-values, list-of-dimension) - returns the `list-of-values` elements
   * reshaped as nested list with dimensions according to the `list-of-dimension`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayReshape.md">ArrayReshape
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArrayReshape =
      initFinalSymbol("ArrayReshape", ID.ArrayReshape);

  /**
   * ArrayRules(sparse-array) - return the array of rules which define the sparse array.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayRules.md">ArrayRules
   *     documentation</a>
   */
  public static final IBuiltInSymbol ArrayRules = initFinalSymbol("ArrayRules", ID.ArrayRules);

  public static final IBuiltInSymbol Arrays = initFinalSymbol("Arrays", ID.Arrays);

  /**
   * Arrow({p1, p2}) - represents a line from `p1` to `p2` that ends with an arrow at `p2`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Arrow.md">Arrow
   *     documentation</a>
   */
  public static final IBuiltInSymbol Arrow = initFinalSymbol("Arrow", ID.Arrow);

  public static final IBuiltInSymbol Arrowheads = initFinalSymbol("Arrowheads", ID.Arrowheads);

  public static final IBuiltInSymbol AspectRatio = initFinalSymbol("AspectRatio", ID.AspectRatio);

  /**
   * AssociateTo(assoc, rule) - append `rule` to the association `assoc` and assign the result to
   * `assoc`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociateTo.md">AssociateTo
   *     documentation</a>
   */
  public static final IBuiltInSymbol AssociateTo = initFinalSymbol("AssociateTo", ID.AssociateTo);

  /**
   * Association(list-of-rules) - create a `key->value` association map from the `list-of-rules`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Association.md">Association
   *     documentation</a>
   */
  public static final IBuiltInSymbol Association = initFinalSymbol("Association", ID.Association);

  /**
   * AssociationMap(header, <|k1->v1, k2->v2,...|>) - create an association `<|header(k1->v1),
   * header(k2->v2),...|>` with the rules mapped by the `header`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationMap.md">AssociationMap
   *     documentation</a>
   */
  public static final IBuiltInSymbol AssociationMap =
      initFinalSymbol("AssociationMap", ID.AssociationMap);

  /**
   * AssociationQ(expr) - returns `True` if `expr` is an association, and `False` otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationQ.md">AssociationQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol AssociationQ =
      initFinalSymbol("AssociationQ", ID.AssociationQ);

  /**
   * AssociationThread({k1,k2,...}, {v1,v2,...}) - create an association with rules from the keys
   * `{k1,k2,...}` and values `{v1,v2,...}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationThread.md">AssociationThread
   *     documentation</a>
   */
  public static final IBuiltInSymbol AssociationThread =
      initFinalSymbol("AssociationThread", ID.AssociationThread);

  /**
   * Assuming(assumption, expression) - evaluate the `expression` with the assumptions appended to
   * the default `$Assumptions` assumptions.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Assuming.md">Assuming
   *     documentation</a>
   */
  public static final IBuiltInSymbol Assuming = initFinalSymbol("Assuming", ID.Assuming);

  public static final IBuiltInSymbol Assumptions = initFinalSymbol("Assumptions", ID.Assumptions);

  /**
   * AtomQ(x) - is true if `x` is an atom (an object such as a number or string, which cannot be
   * divided into subexpressions using 'Part').
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AtomQ.md">AtomQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol AtomQ = initFinalSymbol("AtomQ", ID.AtomQ);

  /**
   * Attributes(symbol) - returns the list of attributes which are assigned to `symbol`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Attributes.md">Attributes
   *     documentation</a>
   */
  public static final IBuiltInSymbol Attributes = initFinalSymbol("Attributes", ID.Attributes);

  public static final IBuiltInSymbol Automatic = initFinalSymbol("Automatic", ID.Automatic);

  public static final IBuiltInSymbol Axes = initFinalSymbol("Axes", ID.Axes);

  public static final IBuiltInSymbol AxesLabel = initFinalSymbol("AxesLabel", ID.AxesLabel);

  public static final IBuiltInSymbol AxesOrigin = initFinalSymbol("AxesOrigin", ID.AxesOrigin);

  public static final IBuiltInSymbol AxesStyle = initFinalSymbol("AxesStyle", ID.AxesStyle);

  public static final IBuiltInSymbol BSplineFunction =
      initFinalSymbol("BSplineFunction", ID.BSplineFunction);

  public static final IBuiltInSymbol Background = initFinalSymbol("Background", ID.Background);

  public static final IBuiltInSymbol Ball = initFinalSymbol("Ball", ID.Ball);

  /**
   * BarChart(list-of-values, options) - plot a bar chart for a `list-of-values` with option
   * `BarOrigin->Bottom` or `BarOrigin->Bottom`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BarChart.md">BarChart
   *     documentation</a>
   */
  public static final IBuiltInSymbol BarChart = initFinalSymbol("BarChart", ID.BarChart);

  public static final IBuiltInSymbol BarOrigin = initFinalSymbol("BarOrigin", ID.BarOrigin);

  public static final IBuiltInSymbol BartlettWindow =
      initFinalSymbol("BartlettWindow", ID.BartlettWindow);

  /**
   * BaseDecode(string) - decodes a Base64 encoded `string` into a `ByteArray` using the Base64
   * encoding scheme.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseDecode.md">BaseDecode
   *     documentation</a>
   */
  public static final IBuiltInSymbol BaseDecode = initFinalSymbol("BaseDecode", ID.BaseDecode);

  /**
   * BaseEncode(byte-array) - encodes the specified `byte-array` into a string using the Base64
   * encoding scheme.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseEncode.md">BaseEncode
   *     documentation</a>
   */
  public static final IBuiltInSymbol BaseEncode = initFinalSymbol("BaseEncode", ID.BaseEncode);

  /**
   * BaseForm(integer, radix) - prints the `integer` number in base `radix` form.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseForm.md">BaseForm
   *     documentation</a>
   */
  public static final IBuiltInSymbol BaseForm = initFinalSymbol("BaseForm", ID.BaseForm);

  public static final IBuiltInSymbol Beep = initFinalSymbol("Beep", ID.Beep);

  /**
   * Begin("<context-name>") - start a new context definition
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Begin.md">Begin
   *     documentation</a>
   */
  public static final IBuiltInSymbol Begin = initFinalSymbol("Begin", ID.Begin);

  /**
   * BeginPackage("<context-name>") - start a new package definition
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BeginPackage.md">BeginPackage
   *     documentation</a>
   */
  public static final IBuiltInSymbol BeginPackage =
      initFinalSymbol("BeginPackage", ID.BeginPackage);

  public static final IBuiltInSymbol BeginTestSection =
      initFinalSymbol("BeginTestSection", ID.BeginTestSection);

  /**
   * BellB(n) - the Bell number function counts the number of different ways to partition a set that
   * has exactly `n` elements
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BellB.md">BellB
   *     documentation</a>
   */
  public static final IBuiltInSymbol BellB = initFinalSymbol("BellB", ID.BellB);

  /**
   * BellY(n, k, {x1, x2, ... , xN}) - the second kind of Bell polynomials (incomplete Bell
   * polynomials).
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BellY.md">BellY
   *     documentation</a>
   */
  public static final IBuiltInSymbol BellY = initFinalSymbol("BellY", ID.BellY);

  /**
   * BernoulliB(expr) - computes the Bernoulli number of the first kind.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernoulliB.md">BernoulliB
   *     documentation</a>
   */
  public static final IBuiltInSymbol BernoulliB = initFinalSymbol("BernoulliB", ID.BernoulliB);

  /**
   * BernoulliDistribution(p) - returns the Bernoulli distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernoulliDistribution.md">BernoulliDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol BernoulliDistribution =
      initFinalSymbol("BernoulliDistribution", ID.BernoulliDistribution);

  /**
   * BernsteinBasis(n, v, expr) - computes the Bernstein basis for the expression `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernsteinBasis.md">BernsteinBasis
   *     documentation</a>
   */
  public static final IBuiltInSymbol BernsteinBasis =
      initFinalSymbol("BernsteinBasis", ID.BernsteinBasis);

  /**
   * BesselI(n, z) - modified Bessel function of the first kind.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselI.md">BesselI
   *     documentation</a>
   */
  public static final IBuiltInSymbol BesselI = initFinalSymbol("BesselI", ID.BesselI);

  /**
   * BesselJ(n, z) - Bessel function of the first kind.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselJ.md">BesselJ
   *     documentation</a>
   */
  public static final IBuiltInSymbol BesselJ = initFinalSymbol("BesselJ", ID.BesselJ);

  /**
   * BesselJZero(n, z) - is the `k`th zero of the `BesselJ(n,z)` function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselJZero.md">BesselJZero
   *     documentation</a>
   */
  public static final IBuiltInSymbol BesselJZero = initFinalSymbol("BesselJZero", ID.BesselJZero);

  /**
   * BesselK(n, z) - modified Bessel function of the second kind.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselK.md">BesselK
   *     documentation</a>
   */
  public static final IBuiltInSymbol BesselK = initFinalSymbol("BesselK", ID.BesselK);

  /**
   * BesselY(n, z) - Bessel function of the second kind.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselY.md">BesselY
   *     documentation</a>
   */
  public static final IBuiltInSymbol BesselY = initFinalSymbol("BesselY", ID.BesselY);

  /**
   * BesselYZero(n, z) - is the `k`th zero of the `BesselY(n,z)` function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselYZero.md">BesselYZero
   *     documentation</a>
   */
  public static final IBuiltInSymbol BesselYZero = initFinalSymbol("BesselYZero", ID.BesselYZero);

  /**
   * Beta(a, b) - is the beta function of the numbers `a`,`b`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Beta.md">Beta
   *     documentation</a>
   */
  public static final IBuiltInSymbol Beta = initFinalSymbol("Beta", ID.Beta);

  public static final IBuiltInSymbol BetaDistribution =
      initFinalSymbol("BetaDistribution", ID.BetaDistribution);

  public static final IBuiltInSymbol BetaRegularized =
      initFinalSymbol("BetaRegularized", ID.BetaRegularized);

  /**
   * BetweennessCentrality(graph) - Computes the betweenness centrality of each vertex of a `graph`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BetweennessCentrality.md">BetweennessCentrality
   *     documentation</a>
   */
  public static final IBuiltInSymbol BetweennessCentrality =
      initFinalSymbol("BetweennessCentrality", ID.BetweennessCentrality);

  public static final IBuiltInSymbol BezierFunction =
      initFinalSymbol("BezierFunction", ID.BezierFunction);

  /**
   * BinCounts(list, width-of-bin) - count the number of elements, if `list`, is divided into
   * successive bins with width `width-of-bin`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinCounts.md">BinCounts
   *     documentation</a>
   */
  public static final IBuiltInSymbol BinCounts = initFinalSymbol("BinCounts", ID.BinCounts);

  /**
   * BinaryDeserialize(byte-array) - deserialize the `byte-array` from WXF format into a Symja
   * expression.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinaryDeserialize.md">BinaryDeserialize
   *     documentation</a>
   */
  public static final IBuiltInSymbol BinaryDeserialize =
      initFinalSymbol("BinaryDeserialize", ID.BinaryDeserialize);

  /**
   * BinaryDistance(u, v) - returns the binary distance between `u` and `v`. `0` if `u` and `v` are
   * unequal. `1` if `u` and `v` are equal.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinaryDistance.md">BinaryDistance
   *     documentation</a>
   */
  public static final IBuiltInSymbol BinaryDistance =
      initFinalSymbol("BinaryDistance", ID.BinaryDistance);

  public static final IBuiltInSymbol BinaryRead = initFinalSymbol("BinaryRead", ID.BinaryRead);

  /**
   * BinarySerialize(expr) - serialize the Symja `expr` into a byte array expression in WXF format.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinarySerialize.md">BinarySerialize
   *     documentation</a>
   */
  public static final IBuiltInSymbol BinarySerialize =
      initFinalSymbol("BinarySerialize", ID.BinarySerialize);

  public static final IBuiltInSymbol BinaryWrite = initFinalSymbol("BinaryWrite", ID.BinaryWrite);

  /**
   * Binomial(n, k) - returns the binomial coefficient of the 2 integers `n` and `k`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Binomial.md">Binomial
   *     documentation</a>
   */
  public static final IBuiltInSymbol Binomial = initFinalSymbol("Binomial", ID.Binomial);

  /**
   * BinomialDistribution(n, p) - returns the binomial distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinomialDistribution.md">BinomialDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol BinomialDistribution =
      initFinalSymbol("BinomialDistribution", ID.BinomialDistribution);

  /**
   * BitLengthi(x) - gives the number of bits needed to represent the integer `x`. The sign of `x`
   * is ignored.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BitLength.md">BitLength
   *     documentation</a>
   */
  public static final IBuiltInSymbol BitLength = initFinalSymbol("BitLength", ID.BitLength);

  public static final IBuiltInSymbol Black = initFinalSymbol("Black", ID.Black);

  public static final IBuiltInSymbol BlackmanHarrisWindow =
      initFinalSymbol("BlackmanHarrisWindow", ID.BlackmanHarrisWindow);

  public static final IBuiltInSymbol BlackmanNuttallWindow =
      initFinalSymbol("BlackmanNuttallWindow", ID.BlackmanNuttallWindow);

  public static final IBuiltInSymbol BlackmanWindow =
      initFinalSymbol("BlackmanWindow", ID.BlackmanWindow);

  public static final IBuiltInSymbol Blank = initFinalSymbol("Blank", ID.Blank);

  public static final IBuiltInSymbol BlankNullSequence =
      initFinalSymbol("BlankNullSequence", ID.BlankNullSequence);

  public static final IBuiltInSymbol BlankSequence =
      initFinalSymbol("BlankSequence", ID.BlankSequence);

  /**
   * Block({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Block.md">Block
   *     documentation</a>
   */
  public static final IBuiltInSymbol Block = initFinalSymbol("Block", ID.Block);

  public static final IBuiltInSymbol Blue = initFinalSymbol("Blue", ID.Blue);

  /**
   * Boole(expr) - returns `1` if `expr` evaluates to `True`; returns `0` if `expr` evaluates to
   * `False`; and gives no result otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Boole.md">Boole
   *     documentation</a>
   */
  public static final IBuiltInSymbol Boole = initFinalSymbol("Boole", ID.Boole);

  /**
   * BooleanConvert(logical-expr) - convert the `logical-expr` to [disjunctive normal
   * form](https://en.wikipedia.org/wiki/Disjunctive_normal_form)
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanConvert.md">BooleanConvert
   *     documentation</a>
   */
  public static final IBuiltInSymbol BooleanConvert =
      initFinalSymbol("BooleanConvert", ID.BooleanConvert);

  /**
   * BooleanMinimize(expr) - minimizes a boolean function with the [Quine McCluskey
   * algorithm](https://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm)
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanMinimize.md">BooleanMinimize
   *     documentation</a>
   */
  public static final IBuiltInSymbol BooleanMinimize =
      initFinalSymbol("BooleanMinimize", ID.BooleanMinimize);

  /**
   * BooleanQ(expr) - returns `True` if `expr` is either `True` or `False`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanQ.md">BooleanQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol BooleanQ = initFinalSymbol("BooleanQ", ID.BooleanQ);

  /**
   * BooleanTable(logical-expr, variables) - generate [truth
   * values](https://en.wikipedia.org/wiki/Truth_table) from the `logical-expr`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanTable.md">BooleanTable
   *     documentation</a>
   */
  public static final IBuiltInSymbol BooleanTable =
      initFinalSymbol("BooleanTable", ID.BooleanTable);

  /**
   * BooleanVariables(logical-expr) - gives a list of the boolean variables that appear in the
   * `logical-expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanVariables.md">BooleanVariables
   *     documentation</a>
   */
  public static final IBuiltInSymbol BooleanVariables =
      initFinalSymbol("BooleanVariables", ID.BooleanVariables);

  /**
   * Booleans - is the set of boolean values.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Booleans.md">Booleans
   *     documentation</a>
   */
  public static final IBuiltInSymbol Booleans = initFinalSymbol("Booleans", ID.Booleans);

  public static final IBuiltInSymbol Bottom = initFinalSymbol("Bottom", ID.Bottom);

  public static final IBuiltInSymbol BoxRatios = initFinalSymbol("BoxRatios", ID.BoxRatios);

  /**
   * BoxWhiskerChart( ) - plot a box whisker chart.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BoxWhiskerChart.md">BoxWhiskerChart
   *     documentation</a>
   */
  public static final IBuiltInSymbol BoxWhiskerChart =
      initFinalSymbol("BoxWhiskerChart", ID.BoxWhiskerChart);

  public static final IBuiltInSymbol Boxed = initFinalSymbol("Boxed", ID.Boxed);

  /**
   * BrayCurtisDistance(u, v) - returns the Bray Curtis distance between `u` and `v`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BrayCurtisDistance.md">BrayCurtisDistance
   *     documentation</a>
   */
  public static final IBuiltInSymbol BrayCurtisDistance =
      initFinalSymbol("BrayCurtisDistance", ID.BrayCurtisDistance);

  /**
   * Break() - exits a `For`, `While`, or `Do` loop.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Break.md">Break
   *     documentation</a>
   */
  public static final IBuiltInSymbol Break = initFinalSymbol("Break", ID.Break);

  public static final IBuiltInSymbol Brown = initFinalSymbol("Brown", ID.Brown);

  public static final IBuiltInSymbol Button = initFinalSymbol("Button", ID.Button);

  public static final IBuiltInSymbol Byte = initFinalSymbol("Byte", ID.Byte);

  /**
   * ByteArray({list-of-byte-values}) - converts the `list-of-byte-values` into a byte array.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ByteArray.md">ByteArray
   *     documentation</a>
   */
  public static final IBuiltInSymbol ByteArray = initFinalSymbol("ByteArray", ID.ByteArray);

  /**
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ByteArrayQ.md">ByteArrayQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol ByteArrayQ = initFinalSymbol("ByteArrayQ", ID.ByteArrayQ);

  /**
   * ByteArrayToString(byte-array) - decoding the specified `byte-array` using the default character
   * set `UTF-8`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ByteArrayToString.md">ByteArrayToString
   *     documentation</a>
   */
  public static final IBuiltInSymbol ByteArrayToString =
      initFinalSymbol("ByteArrayToString", ID.ByteArrayToString);

  public static final IBuiltInSymbol ByteCount = initFinalSymbol("ByteCount", ID.ByteCount);

  /**
   * C(n) - represents the `n`-th constant in a solution to a differential equation.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/C.md">C
   *     documentation</a>
   */
  public static final IBuiltInSymbol C = initFinalSymbol("C", ID.C);

  /**
   * CDF(distribution, value) - returns the cumulative distribution function of `value`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CDF.md">CDF
   *     documentation</a>
   */
  public static final IBuiltInSymbol CDF = initFinalSymbol("CDF", ID.CDF);

  public static final IBuiltInSymbol CForm = initFinalSymbol("CForm", ID.CForm);

  public static final IBuiltInSymbol CMYColor = initFinalSymbol("CMYColor", ID.CMYColor);

  /**
   * CanberraDistance(u, v) - returns the canberra distance between `u` and `v`, which is a weighted
   * version of the Manhattan distance.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CanberraDistance.md">CanberraDistance
   *     documentation</a>
   */
  public static final IBuiltInSymbol CanberraDistance =
      initFinalSymbol("CanberraDistance", ID.CanberraDistance);

  /**
   * Cancel(expr) - cancels out common factors in numerators and denominators.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cancel.md">Cancel
   *     documentation</a>
   */
  public static final IBuiltInSymbol Cancel = initFinalSymbol("Cancel", ID.Cancel);

  public static final IBuiltInSymbol CancelButton =
      initFinalSymbol("CancelButton", ID.CancelButton);

  /**
   * CarlsonRC(x, y) - returns the Carlson RC function..
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRC.md">CarlsonRC
   *     documentation</a>
   */
  public static final IBuiltInSymbol CarlsonRC = initFinalSymbol("CarlsonRC", ID.CarlsonRC);

  /**
   * CarlsonRD(x, y, z) - returns the Carlson RD function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRD.md">CarlsonRD
   *     documentation</a>
   */
  public static final IBuiltInSymbol CarlsonRD = initFinalSymbol("CarlsonRD", ID.CarlsonRD);

  /**
   * CarlsonRF(x, y, z) - returns the Carlson RF function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRF.md">CarlsonRF
   *     documentation</a>
   */
  public static final IBuiltInSymbol CarlsonRF = initFinalSymbol("CarlsonRF", ID.CarlsonRF);

  /**
   * CarlsonRG(x, y, z) - returns the Carlson RG function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRG.md">CarlsonRG
   *     documentation</a>
   */
  public static final IBuiltInSymbol CarlsonRG = initFinalSymbol("CarlsonRG", ID.CarlsonRG);

  /**
   * CarlsonRJ(x, y, z, p) - returns the Carlson RJ function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRJ.md">CarlsonRJ
   *     documentation</a>
   */
  public static final IBuiltInSymbol CarlsonRJ = initFinalSymbol("CarlsonRJ", ID.CarlsonRJ);

  /**
   * CarmichaelLambda(n) - the Carmichael function of `n`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarmichaelLambda.md">CarmichaelLambda
   *     documentation</a>
   */
  public static final IBuiltInSymbol CarmichaelLambda =
      initFinalSymbol("CarmichaelLambda", ID.CarmichaelLambda);

  /**
   * CartesianProduct(list1, list2) - returns the cartesian product for multiple lists.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CartesianProduct.md">CartesianProduct
   *     documentation</a>
   */
  public static final IBuiltInSymbol CartesianProduct =
      initFinalSymbol("CartesianProduct", ID.CartesianProduct);

  /**
   * Cases(list, pattern) - returns the elements of `list` that match `pattern`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cases.md">Cases
   *     documentation</a>
   */
  public static final IBuiltInSymbol Cases = initFinalSymbol("Cases", ID.Cases);

  /**
   * Catalan - Catalan's constant
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Catalan.md">Catalan
   *     documentation</a>
   */
  public static final IBuiltInSymbol Catalan = initFinalSymbol("Catalan", ID.Catalan);

  /**
   * CatalanNumber(n) - returns the catalan number for the integer argument `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CatalanNumber.md">CatalanNumber
   *     documentation</a>
   */
  public static final IBuiltInSymbol CatalanNumber =
      initFinalSymbol("CatalanNumber", ID.CatalanNumber);

  public static final IBuiltInSymbol Catch = initFinalSymbol("Catch", ID.Catch);

  /**
   * Catenate({l1, l2, ...}) - concatenates the lists `l1, l2, ...`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Catenate.md">Catenate
   *     documentation</a>
   */
  public static final IBuiltInSymbol Catenate = initFinalSymbol("Catenate", ID.Catenate);

  /**
   * CauchyDistribution(a,b) - returns the Cauchy distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CauchyDistribution.md">CauchyDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol CauchyDistribution =
      initFinalSymbol("CauchyDistribution", ID.CauchyDistribution);

  /**
   * Ceiling(expr) - gives the first integer greater than or equal `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ceiling.md">Ceiling
   *     documentation</a>
   */
  public static final IBuiltInSymbol Ceiling = initFinalSymbol("Ceiling", ID.Ceiling);

  public static final IBuiltInSymbol Center = initFinalSymbol("Center", ID.Center);

  public static final IBuiltInSymbol CenterDot = initFinalSymbol("CenterDot", ID.CenterDot);

  /**
   * CentralMoment(list, r) - gives the the `r`th central moment (i.e. the `r`th moment about the
   * mean) of `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CentralMoment.md">CentralMoment
   *     documentation</a>
   */
  public static final IBuiltInSymbol CentralMoment =
      initFinalSymbol("CentralMoment", ID.CentralMoment);

  public static final IBuiltInSymbol Character = initFinalSymbol("Character", ID.Character);

  public static final IBuiltInSymbol CharacterEncoding =
      initFinalSymbol("CharacterEncoding", ID.CharacterEncoding);

  /**
   * CharacterRange(min-character, max-character) - computes a list of character strings from
   * `min-character` to `max-character`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CharacterRange.md">CharacterRange
   *     documentation</a>
   */
  public static final IBuiltInSymbol CharacterRange =
      initFinalSymbol("CharacterRange", ID.CharacterRange);

  /**
   * CharacteristicPolynomial(matrix, var) - computes the characteristic polynomial of a `matrix`
   * for the variable `var`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CharacteristicPolynomial.md">CharacteristicPolynomial
   *     documentation</a>
   */
  public static final IBuiltInSymbol CharacteristicPolynomial =
      initFinalSymbol("CharacteristicPolynomial", ID.CharacteristicPolynomial);

  public static final IBuiltInSymbol Characters = initFinalSymbol("Characters", ID.Characters);

  /**
   * ChebyshevT(n, x) - returns the Chebyshev polynomial of the first kind `T_n(x)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChebyshevT.md">ChebyshevT
   *     documentation</a>
   */
  public static final IBuiltInSymbol ChebyshevT = initFinalSymbol("ChebyshevT", ID.ChebyshevT);

  /**
   * ChebyshevU(n, x) - returns the Chebyshev polynomial of the second kind `U_n(x)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChebyshevU.md">ChebyshevU
   *     documentation</a>
   */
  public static final IBuiltInSymbol ChebyshevU = initFinalSymbol("ChebyshevU", ID.ChebyshevU);

  /**
   * Check(expr, failure) - evaluates `expr`, and returns the result, unless messages were
   * generated, in which case `failure` will be returned.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Check.md">Check
   *     documentation</a>
   */
  public static final IBuiltInSymbol Check = initFinalSymbol("Check", ID.Check);

  /**
   * ChessboardDistance(u, v) - returns the chessboard distance (also known as Chebyshev distance)
   * between `u` and `v`, which is the number of moves a king on a chessboard needs to get from
   * square `u` to square `v`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChessboardDistance.md">ChessboardDistance
   *     documentation</a>
   */
  public static final IBuiltInSymbol ChessboardDistance =
      initFinalSymbol("ChessboardDistance", ID.ChessboardDistance);

  public static final IBuiltInSymbol ChiSquareDistribution =
      initFinalSymbol("ChiSquareDistribution", ID.ChiSquareDistribution);

  /**
   * ChineseRemainder({a1, a2, a3,...}, {n1, n2, n3,...}) - the chinese remainder function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChineseRemainder.md">ChineseRemainder
   *     documentation</a>
   */
  public static final IBuiltInSymbol ChineseRemainder =
      initFinalSymbol("ChineseRemainder", ID.ChineseRemainder);

  /**
   * CholeskyDecomposition(matrix) - calculate the Cholesky decomposition of a hermitian, positive
   * definite square `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CholeskyDecomposition.md">CholeskyDecomposition
   *     documentation</a>
   */
  public static final IBuiltInSymbol CholeskyDecomposition =
      initFinalSymbol("CholeskyDecomposition", ID.CholeskyDecomposition);

  /**
   * Chop(numerical-expr) - replaces numerical values in the `numerical-expr` which are close to
   * zero with symbolic value `0`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Chop.md">Chop
   *     documentation</a>
   */
  public static final IBuiltInSymbol Chop = initFinalSymbol("Chop", ID.Chop);

  public static final IBuiltInSymbol Circle = initFinalSymbol("Circle", ID.Circle);

  public static final IBuiltInSymbol CircleDot = initFinalSymbol("CircleDot", ID.CircleDot);

  /**
   * CirclePoints(i) - gives the `i` points on the unit circle for a positive integer `i`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CirclePoints.md">CirclePoints
   *     documentation</a>
   */
  public static final IBuiltInSymbol CirclePoints =
      initFinalSymbol("CirclePoints", ID.CirclePoints);

  public static final IBuiltInSymbol CircleTimes = initFinalSymbol("CircleTimes", ID.CircleTimes);

  /**
   * Clear(symbol1, symbol2,...) - clears all values of the given symbols.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Clear.md">Clear
   *     documentation</a>
   */
  public static final IBuiltInSymbol Clear = initFinalSymbol("Clear", ID.Clear);

  /**
   * ClearAll(symbol1, symbol2,...) - clears all values and attributes associated with the given
   * symbols.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClearAll.md">ClearAll
   *     documentation</a>
   */
  public static final IBuiltInSymbol ClearAll = initFinalSymbol("ClearAll", ID.ClearAll);

  /**
   * ClearAttributes(symbol, attrib) - removes `attrib` from `symbol`'s attributes.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClearAttributes.md">ClearAttributes
   *     documentation</a>
   */
  public static final IBuiltInSymbol ClearAttributes =
      initFinalSymbol("ClearAttributes", ID.ClearAttributes);

  /**
   * Clip(expr) - returns `expr` in the range `-1` to `1`. Returns `-1` if `expr` is less than `-1`.
   * Returns `1` if `expr` is greater than `1`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Clip.md">Clip
   *     documentation</a>
   */
  public static final IBuiltInSymbol Clip = initFinalSymbol("Clip", ID.Clip);

  /**
   * Close(stream) - closes an input or output `stream`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Close.md">Close
   *     documentation</a>
   */
  public static final IBuiltInSymbol Close = initFinalSymbol("Close", ID.Close);

  /**
   * ClosenessCentrality(graph) - Computes the closeness centrality of each vertex of a `graph`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClosenessCentrality.md">ClosenessCentrality
   *     documentation</a>
   */
  public static final IBuiltInSymbol ClosenessCentrality =
      initFinalSymbol("ClosenessCentrality", ID.ClosenessCentrality);

  /**
   * Coefficient(polynomial, variable, exponent) - get the coefficient of `variable^exponent` in
   * `polynomial`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Coefficient.md">Coefficient
   *     documentation</a>
   */
  public static final IBuiltInSymbol Coefficient = initFinalSymbol("Coefficient", ID.Coefficient);

  public static final IBuiltInSymbol CoefficientArrays =
      initFinalSymbol("CoefficientArrays", ID.CoefficientArrays);

  /**
   * CoefficientList(polynomial, variable) - get the coefficient list of a `polynomial`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoefficientList.md">CoefficientList
   *     documentation</a>
   */
  public static final IBuiltInSymbol CoefficientList =
      initFinalSymbol("CoefficientList", ID.CoefficientList);

  /**
   * CoefficientRules(polynomial, list-of-variables) - get the list of coefficient rules of a
   * `polynomial`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoefficientRules.md">CoefficientRules
   *     documentation</a>
   */
  public static final IBuiltInSymbol CoefficientRules =
      initFinalSymbol("CoefficientRules", ID.CoefficientRules);

  /**
   * Collect(expr, variable) - collect subexpressions in `expr` which belong to the same `variable`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Collect.md">Collect
   *     documentation</a>
   */
  public static final IBuiltInSymbol Collect = initFinalSymbol("Collect", ID.Collect);

  /**
   * CollinearPoints({{x1,y1},{x2,y2},{a,b},...}) - returns true if the point `{a,b]` is on the line
   * defined by the first two points `{x1,y1},{x2,y2}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CollinearPoints.md">CollinearPoints
   *     documentation</a>
   */
  public static final IBuiltInSymbol CollinearPoints =
      initFinalSymbol("CollinearPoints", ID.CollinearPoints);

  public static final IBuiltInSymbol Colon = initFinalSymbol("Colon", ID.Colon);

  public static final IBuiltInSymbol ColorData = initFinalSymbol("ColorData", ID.ColorData);

  public static final IBuiltInSymbol ColorFunction =
      initFinalSymbol("ColorFunction", ID.ColorFunction);

  public static final IBuiltInSymbol Column = initFinalSymbol("Column", ID.Column);

  /**
   * Commonest(data-values-list) - the mode of a list of data values is the value that appears most
   * often.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Commonest.md">Commonest
   *     documentation</a>
   */
  public static final IBuiltInSymbol Commonest = initFinalSymbol("Commonest", ID.Commonest);

  public static final IBuiltInSymbol CompatibleUnitQ =
      initFinalSymbol("CompatibleUnitQ", ID.CompatibleUnitQ);

  /**
   * Compile(list-of-arguments}, expression) - compile the `expression` into a Java function, which
   * has the arguments defined in `list-of-arguments` and return the compiled result in an
   * `CompiledFunction` expression.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Compile.md">Compile
   *     documentation</a>
   */
  public static final IBuiltInSymbol Compile = initFinalSymbol("Compile", ID.Compile);

  /**
   * CompilePrint(list-of-arguments}, expression) - compile the `expression` into a Java function
   * and return the corresponding Java source code function, which has the arguments defined in
   * `list-of-arguments`n. You have to run Symja from a Java Development Kit (JDK) to compile to
   * Java binary code.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompilePrint.md">CompilePrint
   *     documentation</a>
   */
  public static final IBuiltInSymbol CompilePrint =
      initFinalSymbol("CompilePrint", ID.CompilePrint);

  /**
   * CompiledFunction(...) - represents a binary Java coded function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompiledFunction.md">CompiledFunction
   *     documentation</a>
   */
  public static final IBuiltInSymbol CompiledFunction =
      initFinalSymbol("CompiledFunction", ID.CompiledFunction);

  /**
   * Complement(set1, set2) - get the complement set from `set1` and `set2`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complement.md">Complement
   *     documentation</a>
   */
  public static final IBuiltInSymbol Complement = initFinalSymbol("Complement", ID.Complement);

  /**
   * CompleteGraph(order) - create a new complete graph with `order` number of total vertices.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompleteGraph.md">CompleteGraph
   *     documentation</a>
   */
  public static final IBuiltInSymbol CompleteGraph =
      initFinalSymbol("CompleteGraph", ID.CompleteGraph);

  /**
   * Complex - is the head of complex numbers.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complex.md">Complex
   *     documentation</a>
   */
  public static final IBuiltInSymbol Complex = initFinalSymbol("Complex", ID.Complex);

  /**
   * ComplexExpand(expr) - get the expanded `expr`. All variable symbols in `expr` are assumed to be
   * non complex numbers.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexExpand.md">ComplexExpand
   *     documentation</a>
   */
  public static final IBuiltInSymbol ComplexExpand =
      initFinalSymbol("ComplexExpand", ID.ComplexExpand);

  /**
   * ComplexInfinity - represents an infinite complex quantity of undetermined direction.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexInfinity.md">ComplexInfinity
   *     documentation</a>
   */
  public static final IBuiltInSymbol ComplexInfinity =
      initFinalSymbol("ComplexInfinity", ID.ComplexInfinity);

  /**
   * ComplexPlot3D(expr, {z, min, max ) - create a 3D plot of `expr` for the complex variable `z` in
   * the range `{ Re(min),Re(max) }` to `{ Im(min),Im(max) }`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexPlot3D.md">ComplexPlot3D
   *     documentation</a>
   */
  public static final IBuiltInSymbol ComplexPlot3D =
      initFinalSymbol("ComplexPlot3D", ID.ComplexPlot3D);

  /**
   * Complexes - is the set of complex numbers.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complexes.md">Complexes
   *     documentation</a>
   */
  public static final IBuiltInSymbol Complexes = initFinalSymbol("Complexes", ID.Complexes);

  public static final IBuiltInSymbol ComplexityFunction =
      initFinalSymbol("ComplexityFunction", ID.ComplexityFunction);

  /**
   * ComposeList(list-of-symbols, variable) - creates a list of compositions of the symbols applied
   * at the argument `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComposeList.md">ComposeList
   *     documentation</a>
   */
  public static final IBuiltInSymbol ComposeList = initFinalSymbol("ComposeList", ID.ComposeList);

  /**
   * ComposeSeries( series1, series2 ) - substitute `series2` into `series1`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComposeSeries.md">ComposeSeries
   *     documentation</a>
   */
  public static final IBuiltInSymbol ComposeSeries =
      initFinalSymbol("ComposeSeries", ID.ComposeSeries);

  /**
   * Composition(sym1, sym2,...)[arg1, arg2,...] - creates a composition of the symbols applied at
   * the arguments.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Composition.md">Composition
   *     documentation</a>
   */
  public static final IBuiltInSymbol Composition = initFinalSymbol("Composition", ID.Composition);

  /**
   * CompoundExpression(expr1, expr2, ...) - evaluates its arguments in turn, returning the last
   * result.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompoundExpression.md">CompoundExpression
   *     documentation</a>
   */
  public static final IBuiltInSymbol CompoundExpression =
      initFinalSymbol("CompoundExpression", ID.CompoundExpression);

  /**
   * Condition(pattern, expr) - places an additional constraint on `pattern` that only allows it to
   * match if `expr` evaluates to `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Condition.md">Condition
   *     documentation</a>
   */
  public static final IBuiltInSymbol Condition = initFinalSymbol("Condition", ID.Condition);

  /**
   * ConditionalExpression(expr, condition) - if `condition` evaluates to `True` return `expr`, if
   * `condition` evaluates to `False` return `Undefined`. Otherwise return the
   * `ConditionalExpression` unevaluated.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConditionalExpression.md">ConditionalExpression
   *     documentation</a>
   */
  public static final IBuiltInSymbol ConditionalExpression =
      initFinalSymbol("ConditionalExpression", ID.ConditionalExpression);

  public static final IBuiltInSymbol Cone = initFinalSymbol("Cone", ID.Cone);

  /**
   * Conjugate(z) - returns the complex conjugate of the complex number `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Conjugate.md">Conjugate
   *     documentation</a>
   */
  public static final IBuiltInSymbol Conjugate = initFinalSymbol("Conjugate", ID.Conjugate);

  /**
   * ConjugateTranspose(matrix) - get the transposed `matrix` with conjugated matrix elements.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConjugateTranspose.md">ConjugateTranspose
   *     documentation</a>
   */
  public static final IBuiltInSymbol ConjugateTranspose =
      initFinalSymbol("ConjugateTranspose", ID.ConjugateTranspose);

  public static final IBuiltInSymbol ConnectedGraphQ =
      initFinalSymbol("ConnectedGraphQ", ID.ConnectedGraphQ);

  /**
   * Constant - is an attribute that indicates that a symbol is a constant.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Constant.md">Constant
   *     documentation</a>
   */
  public static final IBuiltInSymbol Constant = initFinalSymbol("Constant", ID.Constant);

  /**
   * ConstantArray(expr, n) - returns a list of `n` copies of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConstantArray.md">ConstantArray
   *     documentation</a>
   */
  public static final IBuiltInSymbol ConstantArray =
      initFinalSymbol("ConstantArray", ID.ConstantArray);

  public static final IBuiltInSymbol ContainsAll = initFinalSymbol("ContainsAll", ID.ContainsAll);

  public static final IBuiltInSymbol ContainsAny = initFinalSymbol("ContainsAny", ID.ContainsAny);

  public static final IBuiltInSymbol ContainsExactly =
      initFinalSymbol("ContainsExactly", ID.ContainsExactly);

  public static final IBuiltInSymbol ContainsNone =
      initFinalSymbol("ContainsNone", ID.ContainsNone);

  /**
   * ContainsOnly(list1, list2) - yields True if `list1` contains only elements that appear in
   * `list2`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContainsOnly.md">ContainsOnly
   *     documentation</a>
   */
  public static final IBuiltInSymbol ContainsOnly =
      initFinalSymbol("ContainsOnly", ID.ContainsOnly);

  /**
   * Context(symbol) - return the context of the given symbol.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Context.md">Context
   *     documentation</a>
   */
  public static final IBuiltInSymbol Context = initFinalSymbol("Context", ID.Context);

  /**
   * Continue() - continues with the next iteration in a `For`, `While`, or `Do` loop.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Continue.md">Continue
   *     documentation</a>
   */
  public static final IBuiltInSymbol Continue = initFinalSymbol("Continue", ID.Continue);

  /**
   * ContinuedFraction(number) - the complete continued fraction representation for a rational or
   * quadradic irrational `number`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContinuedFraction.md">ContinuedFraction
   *     documentation</a>
   */
  public static final IBuiltInSymbol ContinuedFraction =
      initFinalSymbol("ContinuedFraction", ID.ContinuedFraction);

  public static final IBuiltInSymbol ContourPlot = initFinalSymbol("ContourPlot", ID.ContourPlot);

  /**
   * Convergents({n1, n2, ...}) - return the list of convergents which represents the continued
   * fraction list `{n1, n2, ...}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Convergents.md">Convergents
   *     documentation</a>
   */
  public static final IBuiltInSymbol Convergents = initFinalSymbol("Convergents", ID.Convergents);

  public static final IBuiltInSymbol ConvexHullMesh =
      initFinalSymbol("ConvexHullMesh", ID.ConvexHullMesh);

  /**
   * CoplanarPoints({{x1,y1,z1},{x2,y2,z2},{x3,y3,z3},{a,b,c},...}) - returns true if the point
   * `{a,b,c]` is on the plane defined by the first three points `{x1,y1,z1},{x2,y2,z2},{x3,y3,z3}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoplanarPoints.md">CoplanarPoints
   *     documentation</a>
   */
  public static final IBuiltInSymbol CoplanarPoints =
      initFinalSymbol("CoplanarPoints", ID.CoplanarPoints);

  /**
   * CoprimeQ(x, y) - tests whether `x` and `y` are coprime by computing their greatest common
   * divisor.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoprimeQ.md">CoprimeQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol CoprimeQ = initFinalSymbol("CoprimeQ", ID.CoprimeQ);

  /**
   * Correlation(a, b) - computes Pearson's correlation of two equal-sized vectors `a` and `b`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Correlation.md">Correlation
   *     documentation</a>
   */
  public static final IBuiltInSymbol Correlation = initFinalSymbol("Correlation", ID.Correlation);

  /**
   * Cos(expr) - returns the cosine of `expr` (measured in radians). `Cos(expr)` will evaluate
   * automatically in the case `expr` is a multiple of `Pi, Pi/2, Pi/3, Pi/4` and `Pi/6`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cos.md">Cos
   *     documentation</a>
   */
  public static final IBuiltInSymbol Cos = initFinalSymbol("Cos", ID.Cos);

  /**
   * CosIntegral(expr) - returns the cosine integral of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CosIntegral.md">CosIntegral
   *     documentation</a>
   */
  public static final IBuiltInSymbol CosIntegral = initFinalSymbol("CosIntegral", ID.CosIntegral);

  /**
   * Cosh(z) - returns the hyperbolic cosine of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cosh.md">Cosh
   *     documentation</a>
   */
  public static final IBuiltInSymbol Cosh = initFinalSymbol("Cosh", ID.Cosh);

  /**
   * CoshIntegral(expr) - returns the hyperbolic cosine integral of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoshIntegral.md">CoshIntegral
   *     documentation</a>
   */
  public static final IBuiltInSymbol CoshIntegral =
      initFinalSymbol("CoshIntegral", ID.CoshIntegral);

  /**
   * CosineDistance(u, v) - returns the cosine distance between `u` and `v`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CosineDistance.md">CosineDistance
   *     documentation</a>
   */
  public static final IBuiltInSymbol CosineDistance =
      initFinalSymbol("CosineDistance", ID.CosineDistance);

  /**
   * Cot(expr) - the cotangent function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cot.md">Cot
   *     documentation</a>
   */
  public static final IBuiltInSymbol Cot = initFinalSymbol("Cot", ID.Cot);

  /**
   * Coth(z) - returns the hyperbolic cotangent of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Coth.md">Coth
   *     documentation</a>
   */
  public static final IBuiltInSymbol Coth = initFinalSymbol("Coth", ID.Coth);

  /**
   * Count(list, pattern) - returns the number of times `pattern` appears in `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Count.md">Count
   *     documentation</a>
   */
  public static final IBuiltInSymbol Count = initFinalSymbol("Count", ID.Count);

  /**
   * CountDistinct(list) - returns the number of distinct entries in `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CountDistinct.md">CountDistinct
   *     documentation</a>
   */
  public static final IBuiltInSymbol CountDistinct =
      initFinalSymbol("CountDistinct", ID.CountDistinct);

  /**
   * Counts({elem1, elem2, elem3, ...}) - count the number of each distinct element in the list
   * `{elem1, elem2, elem3, ...}` and return the result as an association `<|elem1->counter1,
   * ...|>`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Counts.md">Counts
   *     documentation</a>
   */
  public static final IBuiltInSymbol Counts = initFinalSymbol("Counts", ID.Counts);

  /**
   * Covariance(a, b) - computes the covariance between the equal-sized vectors `a` and `b`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Covariance.md">Covariance
   *     documentation</a>
   */
  public static final IBuiltInSymbol Covariance = initFinalSymbol("Covariance", ID.Covariance);

  public static final IBuiltInSymbol CreateDirectory =
      initFinalSymbol("CreateDirectory", ID.CreateDirectory);

  public static final IBuiltInSymbol CreateFile = initFinalSymbol("CreateFile", ID.CreateFile);

  /**
   * Cross(a, b) - computes the vector cross product of `a` and `b`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cross.md">Cross
   *     documentation</a>
   */
  public static final IBuiltInSymbol Cross = initFinalSymbol("Cross", ID.Cross);

  /**
   * Csc(z) - returns the cosecant of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Csc.md">Csc
   *     documentation</a>
   */
  public static final IBuiltInSymbol Csc = initFinalSymbol("Csc", ID.Csc);

  /**
   * Csch(z) - returns the hyperbolic cosecant of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Csch.md">Csch
   *     documentation</a>
   */
  public static final IBuiltInSymbol Csch = initFinalSymbol("Csch", ID.Csch);

  /**
   * CubeRoot(n) - finds the real-valued cube root of the given `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CubeRoot.md">CubeRoot
   *     documentation</a>
   */
  public static final IBuiltInSymbol CubeRoot = initFinalSymbol("CubeRoot", ID.CubeRoot);

  /**
   * Cuboid({xmin, ymin, zmin}) - is a unit cube.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cuboid.md">Cuboid
   *     documentation</a>
   */
  public static final IBuiltInSymbol Cuboid = initFinalSymbol("Cuboid", ID.Cuboid);

  /**
   * Curl({f1, f2}, {x1, x2}) - gives the curl.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Curl.md">Curl
   *     documentation</a>
   */
  public static final IBuiltInSymbol Curl = initFinalSymbol("Curl", ID.Curl);

  public static final IBuiltInSymbol Cyan = initFinalSymbol("Cyan", ID.Cyan);

  public static final IBuiltInSymbol CycleGraph = initFinalSymbol("CycleGraph", ID.CycleGraph);

  /**
   * Cycles(a, b) - expression for defining canonical cycles of a permutation.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cycles.md">Cycles
   *     documentation</a>
   */
  public static final IBuiltInSymbol Cycles = initFinalSymbol("Cycles", ID.Cycles);

  /**
   * Cyclotomic(n, x) - returns the Cyclotomic polynomial `C_n(x)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cyclotomic.md">Cyclotomic
   *     documentation</a>
   */
  public static final IBuiltInSymbol Cyclotomic = initFinalSymbol("Cyclotomic", ID.Cyclotomic);

  /**
   * Cylinder({{x1, y1, z1}, {x2, y2, z2}}) - represents a cylinder of radius `1`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cylinder.md">Cylinder
   *     documentation</a>
   */
  public static final IBuiltInSymbol Cylinder = initFinalSymbol("Cylinder", ID.Cylinder);

  /**
   * D(f, x) - gives the partial derivative of `f` with respect to `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/D.md">D
   *     documentation</a>
   */
  public static final IBuiltInSymbol D = initFinalSymbol("D", ID.D);

  /**
   * DSolve(equation, f(var), var) - attempts to solve a linear differential `equation` for the
   * function `f(var)` and variable `var`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DSolve.md">DSolve
   *     documentation</a>
   */
  public static final IBuiltInSymbol DSolve = initFinalSymbol("DSolve", ID.DSolve);

  /**
   * - create a `Dataset` object from the `association`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dataset.md">Dataset
   *     documentation</a>
   */
  public static final IBuiltInSymbol Dataset = initFinalSymbol("Dataset", ID.Dataset);

  public static final IBuiltInSymbol DateObject = initFinalSymbol("DateObject", ID.DateObject);

  public static final IBuiltInSymbol DateString = initFinalSymbol("DateString", ID.DateString);

  public static final IBuiltInSymbol DateValue = initFinalSymbol("DateValue", ID.DateValue);

  /**
   * Decrement(x) - decrements `x` by `1`, returning the original value of `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Decrement.md">Decrement
   *     documentation</a>
   */
  public static final IBuiltInSymbol Decrement = initFinalSymbol("Decrement", ID.Decrement);

  /**
   * Default(symbol) - `Default` returns the default value associated with the `symbol` for a
   * pattern default `_.` expression.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Default.md">Default
   *     documentation</a>
   */
  public static final IBuiltInSymbol Default = initFinalSymbol("Default", ID.Default);

  public static final IBuiltInSymbol DefaultButton =
      initFinalSymbol("DefaultButton", ID.DefaultButton);

  public static final IBuiltInSymbol DefaultValue =
      initFinalSymbol("DefaultValue", ID.DefaultValue);

  /**
   * Defer(expr) - `Defer` doesn't evaluate `expr` and didn't appear in the output
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Defer.md">Defer
   *     documentation</a>
   */
  public static final IBuiltInSymbol Defer = initFinalSymbol("Defer", ID.Defer);

  /**
   * Definition(symbol) - prints user-defined values and rules associated with `symbol`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Definition.md">Definition
   *     documentation</a>
   */
  public static final IBuiltInSymbol Definition = initFinalSymbol("Definition", ID.Definition);

  /**
   * Degree - the constant `Degree` converts angles from degree to `Pi/180` radians.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Degree.md">Degree
   *     documentation</a>
   */
  public static final IBuiltInSymbol Degree = initFinalSymbol("Degree", ID.Degree);

  public static final IBuiltInSymbol DegreeLexicographic =
      initFinalSymbol("DegreeLexicographic", ID.DegreeLexicographic);

  public static final IBuiltInSymbol DegreeReverseLexicographic =
      initFinalSymbol("DegreeReverseLexicographic", ID.DegreeReverseLexicographic);

  /**
   * Delete(expr, n) - returns `expr` with part `n` removed.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Delete.md">Delete
   *     documentation</a>
   */
  public static final IBuiltInSymbol Delete = initFinalSymbol("Delete", ID.Delete);

  /**
   * DeleteCases(list, pattern) - returns the elements of `list` that do not match `pattern`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteCases.md">DeleteCases
   *     documentation</a>
   */
  public static final IBuiltInSymbol DeleteCases = initFinalSymbol("DeleteCases", ID.DeleteCases);

  /**
   * DeleteDuplicates(list) - deletes duplicates from `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteDuplicates.md">DeleteDuplicates
   *     documentation</a>
   */
  public static final IBuiltInSymbol DeleteDuplicates =
      initFinalSymbol("DeleteDuplicates", ID.DeleteDuplicates);

  /**
   * DeleteDuplicatesBy(list, predicate) - deletes duplicates from `list`, for which the `predicate`
   * returns `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteDuplicatesBy.md">DeleteDuplicatesBy
   *     documentation</a>
   */
  public static final IBuiltInSymbol DeleteDuplicatesBy =
      initFinalSymbol("DeleteDuplicatesBy", ID.DeleteDuplicatesBy);

  /**
   * Denominator(expr) - gives the denominator in `expr`. Denominator collects expressions with
   * negative exponents.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Denominator.md">Denominator
   *     documentation</a>
   */
  public static final IBuiltInSymbol Denominator = initFinalSymbol("Denominator", ID.Denominator);

  /**
   * DensityHistogram( list-of-pair-values ) - plot a density histogram for a `list-of-pair-values`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DensityHistogram.md">DensityHistogram
   *     documentation</a>
   */
  public static final IBuiltInSymbol DensityHistogram =
      initFinalSymbol("DensityHistogram", ID.DensityHistogram);

  public static final IBuiltInSymbol DensityPlot = initFinalSymbol("DensityPlot", ID.DensityPlot);

  /**
   * Depth(expr) - gives the depth of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Depth.md">Depth
   *     documentation</a>
   */
  public static final IBuiltInSymbol Depth = initFinalSymbol("Depth", ID.Depth);

  /**
   * Derivative(n)[f] - represents the `n`-th derivative of the function `f`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Derivative.md">Derivative
   *     documentation</a>
   */
  public static final IBuiltInSymbol Derivative = initFinalSymbol("Derivative", ID.Derivative);

  /**
   * DesignMatrix(m, f, x) - returns the design matrix.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DesignMatrix.md">DesignMatrix
   *     documentation</a>
   */
  public static final IBuiltInSymbol DesignMatrix =
      initFinalSymbol("DesignMatrix", ID.DesignMatrix);

  /**
   * Det(matrix) - computes the determinant of the `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Det.md">Det
   *     documentation</a>
   */
  public static final IBuiltInSymbol Det = initFinalSymbol("Det", ID.Det);

  /**
   * Diagonal(matrix) - computes the diagonal vector of the `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Diagonal.md">Diagonal
   *     documentation</a>
   */
  public static final IBuiltInSymbol Diagonal = initFinalSymbol("Diagonal", ID.Diagonal);

  /**
   * DiagonalMatrix(list) - gives a matrix with the values in `list` on its diagonal and zeroes
   * elsewhere.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiagonalMatrix.md">DiagonalMatrix
   *     documentation</a>
   */
  public static final IBuiltInSymbol DiagonalMatrix =
      initFinalSymbol("DiagonalMatrix", ID.DiagonalMatrix);

  /**
   * DialogInput() - if the file system is enabled, the user can input a string in a dialog box.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DialogInput.md">DialogInput
   *     documentation</a>
   */
  public static final IBuiltInSymbol DialogInput = initFinalSymbol("DialogInput", ID.DialogInput);

  public static final IBuiltInSymbol DialogNotebook =
      initFinalSymbol("DialogNotebook", ID.DialogNotebook);

  public static final IBuiltInSymbol DialogReturn =
      initFinalSymbol("DialogReturn", ID.DialogReturn);

  /**
   * DiceDissimilarity(u, v) - returns the Dice dissimilarity between the two boolean 1-D lists `u`
   * and `v`, which is defined as `(c_tf + c_ft) / (2 * c_tt + c_ft + c_tf)`, where n is `len(u)`
   * and `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiceDissimilarity.md">DiceDissimilarity
   *     documentation</a>
   */
  public static final IBuiltInSymbol DiceDissimilarity =
      initFinalSymbol("DiceDissimilarity", ID.DiceDissimilarity);

  /**
   * DifferenceDelta(f(x), x) - generates a forward difference `f(x+1) - f(x)`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DifferenceDelta.md">DifferenceDelta
   *     documentation</a>
   */
  public static final IBuiltInSymbol DifferenceDelta =
      initFinalSymbol("DifferenceDelta", ID.DifferenceDelta);

  public static final IBuiltInSymbol Differences = initFinalSymbol("Differences", ID.Differences);

  /**
   * DigitCharacter - represents the digits 0-9.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitCharacter.md">DigitCharacter
   *     documentation</a>
   */
  public static final IBuiltInSymbol DigitCharacter =
      initFinalSymbol("DigitCharacter", ID.DigitCharacter);

  /**
   * DigitCount(n) - returns a list of the number of integer digits for `n` for `radix` 10.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitCount.md">DigitCount
   *     documentation</a>
   */
  public static final IBuiltInSymbol DigitCount = initFinalSymbol("DigitCount", ID.DigitCount);

  /**
   * DigitQ(str) - returns `True` if `str` is a string which contains only digits.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitQ.md">DigitQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol DigitQ = initFinalSymbol("DigitQ", ID.DigitQ);

  /**
   * Dimensions(expr) - returns a list of the dimensions of the expression `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dimensions.md">Dimensions
   *     documentation</a>
   */
  public static final IBuiltInSymbol Dimensions = initFinalSymbol("Dimensions", ID.Dimensions);

  /**
   * DiracDelta(x) - `DiracDelta` function returns `0` for all real numbers `x` where `x != 0`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiracDelta.md">DiracDelta
   *     documentation</a>
   */
  public static final IBuiltInSymbol DiracDelta = initFinalSymbol("DiracDelta", ID.DiracDelta);

  /**
   * DirectedEdge(a, b) - is a directed edge from vertex `a` to vertex `b` in a `graph` object.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DirectedEdge.md">DirectedEdge
   *     documentation</a>
   */
  public static final IBuiltInSymbol DirectedEdge =
      initFinalSymbol("DirectedEdge", ID.DirectedEdge);

  /**
   * DirectedInfinity(z) - represents an infinite multiple of the complex number `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DirectedInfinity.md">DirectedInfinity
   *     documentation</a>
   */
  public static final IBuiltInSymbol DirectedInfinity =
      initFinalSymbol("DirectedInfinity", ID.DirectedInfinity);

  public static final IBuiltInSymbol Direction = initFinalSymbol("Direction", ID.Direction);

  public static final IBuiltInSymbol Directive = initFinalSymbol("Directive", ID.Directive);

  public static final IBuiltInSymbol DirichletEta =
      initFinalSymbol("DirichletEta", ID.DirichletEta);

  public static final IBuiltInSymbol DirichletWindow =
      initFinalSymbol("DirichletWindow", ID.DirichletWindow);

  /**
   * DiscreteDelta(n1, n2, n3, ...) - `DiscreteDelta` function returns `1` if all the `ni` are `0`.
   * Returns `0` otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscreteDelta.md">DiscreteDelta
   *     documentation</a>
   */
  public static final IBuiltInSymbol DiscreteDelta =
      initFinalSymbol("DiscreteDelta", ID.DiscreteDelta);

  /**
   * DiscreteUniformDistribution({min, max}) - returns a discrete uniform distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscreteUniformDistribution.md">DiscreteUniformDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol DiscreteUniformDistribution =
      initFinalSymbol("DiscreteUniformDistribution", ID.DiscreteUniformDistribution);

  /**
   * Discriminant(poly, var) - computes the discriminant of the polynomial `poly` with respect to
   * the variable `var`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Discriminant.md">Discriminant
   *     documentation</a>
   */
  public static final IBuiltInSymbol Discriminant =
      initFinalSymbol("Discriminant", ID.Discriminant);

  public static final IBuiltInSymbol DisjointQ = initFinalSymbol("DisjointQ", ID.DisjointQ);

  public static final IBuiltInSymbol Disk = initFinalSymbol("Disk", ID.Disk);

  /**
   * Dispatch({rule1, rule2, ...}) - create a dispatch map for a list of rules.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dispatch.md">Dispatch
   *     documentation</a>
   */
  public static final IBuiltInSymbol Dispatch = initFinalSymbol("Dispatch", ID.Dispatch);

  public static final IBuiltInSymbol DisplayForm = initFinalSymbol("DisplayForm", ID.DisplayForm);

  public static final IBuiltInSymbol Disputed = initFinalSymbol("Disputed", ID.Disputed);

  public static final IBuiltInSymbol DisrectedEdges =
      initFinalSymbol("DisrectedEdges", ID.DisrectedEdges);

  public static final IBuiltInSymbol DistanceFunction =
      initFinalSymbol("DistanceFunction", ID.DistanceFunction);

  /**
   * Distribute(f(x1, x2, x3,...)) - distributes `f` over `Plus` appearing in any of the `xi`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Distribute.md">Distribute
   *     documentation</a>
   */
  public static final IBuiltInSymbol Distribute = initFinalSymbol("Distribute", ID.Distribute);

  public static final IBuiltInSymbol Distributed = initFinalSymbol("Distributed", ID.Distributed);

  /**
   * Div({f1, f2, f3,...},{x1, x2, x3,...}) - compute the divergence.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Div.md">Div
   *     documentation</a>
   */
  public static final IBuiltInSymbol Div = initFinalSymbol("Div", ID.Div);

  /**
   * Divide(a, b) - represents the division of `a` by `b`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divide.md">Divide
   *     documentation</a>
   */
  public static final IBuiltInSymbol Divide = initFinalSymbol("Divide", ID.Divide);

  /**
   * DivideBy(x, dx) - is equivalent to `x = x / dx`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivideBy.md">DivideBy
   *     documentation</a>
   */
  public static final IBuiltInSymbol DivideBy = initFinalSymbol("DivideBy", ID.DivideBy);

  public static final IBuiltInSymbol DivideSides = initFinalSymbol("DivideSides", ID.DivideSides);

  /**
   * Divisible(n, m) - returns `True` if `n` could be divide by `m`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divisible.md">Divisible
   *     documentation</a>
   */
  public static final IBuiltInSymbol Divisible = initFinalSymbol("Divisible", ID.Divisible);

  /**
   * DivisorSigma(k, n) - returns the sum of the `k`-th powers of the divisors of `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivisorSigma.md">DivisorSigma
   *     documentation</a>
   */
  public static final IBuiltInSymbol DivisorSigma =
      initFinalSymbol("DivisorSigma", ID.DivisorSigma);

  /**
   * DivisorSum(n, head) - returns the sum of the divisors of `n`. The `head` is applied to each
   * divisor.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivisorSum.md">DivisorSum
   *     documentation</a>
   */
  public static final IBuiltInSymbol DivisorSum = initFinalSymbol("DivisorSum", ID.DivisorSum);

  /**
   * Divisors(n) - returns all integers that divide the integer `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divisors.md">Divisors
   *     documentation</a>
   */
  public static final IBuiltInSymbol Divisors = initFinalSymbol("Divisors", ID.Divisors);

  /**
   * Do(expr, {max}) - evaluates `expr` `max` times.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Do.md">Do
   *     documentation</a>
   */
  public static final IBuiltInSymbol Do = initFinalSymbol("Do", ID.Do);

  public static final IBuiltInSymbol Dodecahedron =
      initFinalSymbol("Dodecahedron", ID.Dodecahedron);

  /**
   * Dot(x, y) or x . y - `x . y` computes the vector dot product or matrix product `x . y`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dot.md">Dot
   *     documentation</a>
   */
  public static final IBuiltInSymbol Dot = initFinalSymbol("Dot", ID.Dot);

  /**
   * DownValues(symbol) - prints the down-value rules associated with `symbol`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownValues.md">DownValues
   *     documentation</a>
   */
  public static final IBuiltInSymbol DownValues = initFinalSymbol("DownValues", ID.DownValues);

  /**
   * Drop(expr, n) - returns `expr` with the first `n` leaves removed.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Drop.md">Drop
   *     documentation</a>
   */
  public static final IBuiltInSymbol Drop = initFinalSymbol("Drop", ID.Drop);

  public static final IBuiltInSymbol DuplicateFreeQ =
      initFinalSymbol("DuplicateFreeQ", ID.DuplicateFreeQ);

  public static final IBuiltInSymbol Dynamic = initFinalSymbol("Dynamic", ID.Dynamic);

  /**
   * E - Euler's constant E
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/E.md">E
   *     documentation</a>
   */
  public static final IBuiltInSymbol E = initFinalSymbol("E", ID.E);

  public static final IBuiltInSymbol EasterSunday =
      initFinalSymbol("EasterSunday", ID.EasterSunday);

  /**
   * Echo(expr) - prints the `expr` to the default output stream and returns `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Echo.md">Echo
   *     documentation</a>
   */
  public static final IBuiltInSymbol Echo = initFinalSymbol("Echo", ID.Echo);

  /**
   * EchoFunction()[expr] - operator form of the `Echo`function. Print the `expr` to the default
   * output stream and return `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EchoFunction.md">EchoFunction
   *     documentation</a>
   */
  public static final IBuiltInSymbol EchoFunction =
      initFinalSymbol("EchoFunction", ID.EchoFunction);

  public static final IBuiltInSymbol EdgeCount = initFinalSymbol("EdgeCount", ID.EdgeCount);

  public static final IBuiltInSymbol EdgeForm = initFinalSymbol("EdgeForm", ID.EdgeForm);

  public static final IBuiltInSymbol EdgeLabels = initFinalSymbol("EdgeLabels", ID.EdgeLabels);

  /**
   * EdgeList(graph) - convert the `graph` into a list of edges.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeList.md">EdgeList
   *     documentation</a>
   */
  public static final IBuiltInSymbol EdgeList = initFinalSymbol("EdgeList", ID.EdgeList);

  /**
   * EdgeQ(graph, edge) - test if `edge` is an edge in the `graph` object.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeQ.md">EdgeQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol EdgeQ = initFinalSymbol("EdgeQ", ID.EdgeQ);

  /**
   * EdgeRules(graph) - convert the `graph` into a list of rules. All edge types (undirected,
   * directed) are represented by a rule `lhs->rhs`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeRules.md">EdgeRules
   *     documentation</a>
   */
  public static final IBuiltInSymbol EdgeRules = initFinalSymbol("EdgeRules", ID.EdgeRules);

  public static final IBuiltInSymbol EdgeShapeFunction =
      initFinalSymbol("EdgeShapeFunction", ID.EdgeShapeFunction);

  public static final IBuiltInSymbol EdgeStyle = initFinalSymbol("EdgeStyle", ID.EdgeStyle);

  public static final IBuiltInSymbol EdgeWeight = initFinalSymbol("EdgeWeight", ID.EdgeWeight);

  /**
   * EditDistance(a, b) - returns the Levenshtein distance of `a` and `b`, which is defined as the
   * minimum number of insertions, deletions and substitutions on the constituents of `a` and `b`
   * needed to transform one into the other.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EditDistance.md">EditDistance
   *     documentation</a>
   */
  public static final IBuiltInSymbol EditDistance =
      initFinalSymbol("EditDistance", ID.EditDistance);

  /**
   * EffectiveInterest(i, n) - returns an effective interest rate object.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EffectiveInterest.md">EffectiveInterest
   *     documentation</a>
   */
  public static final IBuiltInSymbol EffectiveInterest =
      initFinalSymbol("EffectiveInterest", ID.EffectiveInterest);

  /**
   * Eigenvalues(matrix) - get the numerical eigenvalues of the `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigenvalues.md">Eigenvalues
   *     documentation</a>
   */
  public static final IBuiltInSymbol Eigenvalues = initFinalSymbol("Eigenvalues", ID.Eigenvalues);

  /**
   * Eigenvectors(matrix) - get the numerical eigenvectors of the `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigenvectors.md">Eigenvectors
   *     documentation</a>
   */
  public static final IBuiltInSymbol Eigenvectors =
      initFinalSymbol("Eigenvectors", ID.Eigenvectors);

  /**
   * Element(symbol, dom) - assume (or test) that the `symbol` is in the domain `dom`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Element.md">Element
   *     documentation</a>
   */
  public static final IBuiltInSymbol Element = initFinalSymbol("Element", ID.Element);

  /**
   * ElementData("name", "property") - gives the value of the property for the chemical specified by
   * name.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ElementData.md">ElementData
   *     documentation</a>
   */
  public static final IBuiltInSymbol ElementData = initFinalSymbol("ElementData", ID.ElementData);

  /**
   * Eliminate(list-of-equations, list-of-variables) - attempts to eliminate the variables from the
   * `list-of-variables` in the `list-of-equations`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eliminate.md">Eliminate
   *     documentation</a>
   */
  public static final IBuiltInSymbol Eliminate = initFinalSymbol("Eliminate", ID.Eliminate);

  public static final IBuiltInSymbol EliminationOrder =
      initFinalSymbol("EliminationOrder", ID.EliminationOrder);

  public static final IBuiltInSymbol Ellipsoid = initFinalSymbol("Ellipsoid", ID.Ellipsoid);

  /**
   * EllipticE(z) - returns the complete elliptic integral of the second kind.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticE.md">EllipticE
   *     documentation</a>
   */
  public static final IBuiltInSymbol EllipticE = initFinalSymbol("EllipticE", ID.EllipticE);

  /**
   * EllipticF(z) - returns the incomplete elliptic integral of the first kind.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticF.md">EllipticF
   *     documentation</a>
   */
  public static final IBuiltInSymbol EllipticF = initFinalSymbol("EllipticF", ID.EllipticF);

  /**
   * EllipticK(z) - returns the complete elliptic integral of the first kind.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticK.md">EllipticK
   *     documentation</a>
   */
  public static final IBuiltInSymbol EllipticK = initFinalSymbol("EllipticK", ID.EllipticK);

  /**
   * EllipticPi(n,m) - returns the complete elliptic integral of the third kind.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticPi.md">EllipticPi
   *     documentation</a>
   */
  public static final IBuiltInSymbol EllipticPi = initFinalSymbol("EllipticPi", ID.EllipticPi);

  public static final IBuiltInSymbol EllipticTheta =
      initFinalSymbol("EllipticTheta", ID.EllipticTheta);

  /**
   * End( ) - end a context definition started with `Begin`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/End.md">End
   *     documentation</a>
   */
  public static final IBuiltInSymbol End = initFinalSymbol("End", ID.End);

  public static final IBuiltInSymbol EndOfFile = initFinalSymbol("EndOfFile", ID.EndOfFile);

  public static final IBuiltInSymbol EndOfLine = initFinalSymbol("EndOfLine", ID.EndOfLine);

  public static final IBuiltInSymbol EndOfString = initFinalSymbol("EndOfString", ID.EndOfString);

  /**
   * EndPackage( ) - end a package definition
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EndPackage.md">EndPackage
   *     documentation</a>
   */
  public static final IBuiltInSymbol EndPackage = initFinalSymbol("EndPackage", ID.EndPackage);

  public static final IBuiltInSymbol EndTestSection =
      initFinalSymbol("EndTestSection", ID.EndTestSection);

  public static final IBuiltInSymbol Entity = initFinalSymbol("Entity", ID.Entity);

  /**
   * Entropy(list) - return the base `E` (Shannon) information entropy of the elements in `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Entropy.md">Entropy
   *     documentation</a>
   */
  public static final IBuiltInSymbol Entropy = initFinalSymbol("Entropy", ID.Entropy);

  /**
   * Equal(x, y) - yields `True` if `x` and `y` are known to be equal, or `False` if `x` and `y` are
   * known to be unequal.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Equal.md">Equal
   *     documentation</a>
   */
  public static final IBuiltInSymbol Equal = initFinalSymbol("Equal", ID.Equal);

  public static final IBuiltInSymbol EqualTo = initFinalSymbol("EqualTo", ID.EqualTo);

  /**
   * Equivalent(arg1, arg2, ...) - Equivalence relation. `Equivalent(A, B)` is `True` iff `A` and
   * `B` are both `True` or both `False`. Returns `True` if all of the arguments are logically
   * equivalent. Returns `False` otherwise. `Equivalent(arg1, arg2, ...)` is equivalent to `(arg1 &&
   * arg2 && ...) || (!arg1 && !arg2 && ...)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Equivalent.md">Equivalent
   *     documentation</a>
   */
  public static final IBuiltInSymbol Equivalent = initFinalSymbol("Equivalent", ID.Equivalent);

  /**
   * Erf(z) - returns the error function of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erf.md">Erf
   *     documentation</a>
   */
  public static final IBuiltInSymbol Erf = initFinalSymbol("Erf", ID.Erf);

  /**
   * Erfc(z) - returns the complementary error function of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erfc.md">Erfc
   *     documentation</a>
   */
  public static final IBuiltInSymbol Erfc = initFinalSymbol("Erfc", ID.Erfc);

  /**
   * Erfi(z) - returns the imaginary error function of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erfi.md">Erfi
   *     documentation</a>
   */
  public static final IBuiltInSymbol Erfi = initFinalSymbol("Erfi", ID.Erfi);

  /**
   * ErlangDistribution({k, lambda}) - returns a Erlang distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ErlangDistribution.md">ErlangDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol ErlangDistribution =
      initFinalSymbol("ErlangDistribution", ID.ErlangDistribution);

  /**
   * EuclideanDistance(u, v) - returns the euclidean distance between `u` and `v`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EuclideanDistance.md">EuclideanDistance
   *     documentation</a>
   */
  public static final IBuiltInSymbol EuclideanDistance =
      initFinalSymbol("EuclideanDistance", ID.EuclideanDistance);

  /**
   * EulerE(n) - gives the euler number `En`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerE.md">EulerE
   *     documentation</a>
   */
  public static final IBuiltInSymbol EulerE = initFinalSymbol("EulerE", ID.EulerE);

  /**
   * EulerGamma - Euler-Mascheroni constant
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerGamma.md">EulerGamma
   *     documentation</a>
   */
  public static final IBuiltInSymbol EulerGamma = initFinalSymbol("EulerGamma", ID.EulerGamma);

  /**
   * EulerPhi(n) - compute Euler's totient function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerPhi.md">EulerPhi
   *     documentation</a>
   */
  public static final IBuiltInSymbol EulerPhi = initFinalSymbol("EulerPhi", ID.EulerPhi);

  /**
   * EulerianGraphQ(graph) - returns `True` if `graph` is an eulerian graph, and `False` otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerianGraphQ.md">EulerianGraphQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol EulerianGraphQ =
      initFinalSymbol("EulerianGraphQ", ID.EulerianGraphQ);

  /**
   * Evaluate(expr) - the `Evaluate` function will be executed even if the function attributes
   * `HoldFirst, HoldRest, HoldAll` are set for the function head.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Evaluate.md">Evaluate
   *     documentation</a>
   */
  public static final IBuiltInSymbol Evaluate = initFinalSymbol("Evaluate", ID.Evaluate);

  /**
   * EvenQ(x) - returns `True` if `x` is even, and `False` otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EvenQ.md">EvenQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol EvenQ = initFinalSymbol("EvenQ", ID.EvenQ);

  /**
   * ExactNumberQ(expr) - returns `True` if `expr` is an exact number, and `False` otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExactNumberQ.md">ExactNumberQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol ExactNumberQ =
      initFinalSymbol("ExactNumberQ", ID.ExactNumberQ);

  /**
   * Except(c) - represents a pattern object that matches any expression except those matching `c`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Except.md">Except
   *     documentation</a>
   */
  public static final IBuiltInSymbol Except = initFinalSymbol("Except", ID.Except);

  public static final IBuiltInSymbol Exists = initFinalSymbol("Exists", ID.Exists);

  public static final IBuiltInSymbol Exit = initFinalSymbol("Exit", ID.Exit);

  /**
   * Exp(z) - the exponential function `E^z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Exp.md">Exp
   *     documentation</a>
   */
  public static final IBuiltInSymbol Exp = initFinalSymbol("Exp", ID.Exp);

  /**
   * ExpIntegralE(n, expr) - returns the exponential integral `E_n(expr)` of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpIntegralE.md">ExpIntegralE
   *     documentation</a>
   */
  public static final IBuiltInSymbol ExpIntegralE =
      initFinalSymbol("ExpIntegralE", ID.ExpIntegralE);

  /**
   * ExpIntegralEi(expr) - returns the exponential integral `Ei(expr)` of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpIntegralEi.md">ExpIntegralEi
   *     documentation</a>
   */
  public static final IBuiltInSymbol ExpIntegralEi =
      initFinalSymbol("ExpIntegralEi", ID.ExpIntegralEi);

  public static final IBuiltInSymbol ExpToTrig = initFinalSymbol("ExpToTrig", ID.ExpToTrig);

  /**
   * Expand(expr) - expands out positive rational powers and products of sums in `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expand.md">Expand
   *     documentation</a>
   */
  public static final IBuiltInSymbol Expand = initFinalSymbol("Expand", ID.Expand);

  /**
   * ExpandAll(expr) - expands out all positive integer powers and products of sums in `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpandAll.md">ExpandAll
   *     documentation</a>
   */
  public static final IBuiltInSymbol ExpandAll = initFinalSymbol("ExpandAll", ID.ExpandAll);

  /**
   * Expectation(pure-function, data-set) - returns the expected value of the `pure-function` for
   * the given `data-set`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expectation.md">Expectation
   *     documentation</a>
   */
  public static final IBuiltInSymbol Expectation = initFinalSymbol("Expectation", ID.Expectation);

  /**
   * Exponent(polynomial, x) - gives the maximum power with which `x` appears in the expanded form
   * of `polynomial`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Exponent.md">Exponent
   *     documentation</a>
   */
  public static final IBuiltInSymbol Exponent = initFinalSymbol("Exponent", ID.Exponent);

  /**
   * ExponentialDistribution(lambda) - returns an exponential distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExponentialDistribution.md">ExponentialDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol ExponentialDistribution =
      initFinalSymbol("ExponentialDistribution", ID.ExponentialDistribution);

  /**
   * Export("path-to-filename", expression, "WXF") - if the file system is enabled, export the
   * `expression` in WXF format to the "path-to-filename" file.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Export.md">Export
   *     documentation</a>
   */
  public static final IBuiltInSymbol Export = initFinalSymbol("Export", ID.Export);

  public static final IBuiltInSymbol ExportString =
      initFinalSymbol("ExportString", ID.ExportString);

  public static final IBuiltInSymbol Expression = initFinalSymbol("Expression", ID.Expression);

  /**
   * ExtendedGCD(n1, n2, ...) - computes the extended greatest common divisor of the given integers.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExtendedGCD.md">ExtendedGCD
   *     documentation</a>
   */
  public static final IBuiltInSymbol ExtendedGCD = initFinalSymbol("ExtendedGCD", ID.ExtendedGCD);

  public static final IBuiltInSymbol Extension = initFinalSymbol("Extension", ID.Extension);

  /**
   * Extract(expr, list) - extracts parts of `expr` specified by `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Extract.md">Extract
   *     documentation</a>
   */
  public static final IBuiltInSymbol Extract = initFinalSymbol("Extract", ID.Extract);

  public static final IBuiltInSymbol FRatioDistribution =
      initFinalSymbol("FRatioDistribution", ID.FRatioDistribution);

  /**
   * Factor(expr) - factors the polynomial expression `expr`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factor.md">Factor
   *     documentation</a>
   */
  public static final IBuiltInSymbol Factor = initFinalSymbol("Factor", ID.Factor);

  /**
   * FactorInteger(n) - returns the factorization of `n` as a list of factors and exponents.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorInteger.md">FactorInteger
   *     documentation</a>
   */
  public static final IBuiltInSymbol FactorInteger =
      initFinalSymbol("FactorInteger", ID.FactorInteger);

  /**
   * FactorSquareFree(polynomial) - factor the polynomial expression `polynomial` square free.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorSquareFree.md">FactorSquareFree
   *     documentation</a>
   */
  public static final IBuiltInSymbol FactorSquareFree =
      initFinalSymbol("FactorSquareFree", ID.FactorSquareFree);

  /**
   * FactorSquareFreeList(polynomial) - get the square free factors of the polynomial expression
   * `polynomial`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorSquareFreeList.md">FactorSquareFreeList
   *     documentation</a>
   */
  public static final IBuiltInSymbol FactorSquareFreeList =
      initFinalSymbol("FactorSquareFreeList", ID.FactorSquareFreeList);

  /**
   * FactorTerms(poly) - pulls out any overall numerical factor in `poly`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorTerms.md">FactorTerms
   *     documentation</a>
   */
  public static final IBuiltInSymbol FactorTerms = initFinalSymbol("FactorTerms", ID.FactorTerms);

  /**
   * Factorial(n) - returns the factorial number of the integer `n`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factorial.md">Factorial
   *     documentation</a>
   */
  public static final IBuiltInSymbol Factorial = initFinalSymbol("Factorial", ID.Factorial);

  /**
   * Factorial2(n) - returns the double factorial number of the integer `n` as `n*(n-2)*(n-4)...`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factorial2.md">Factorial2
   *     documentation</a>
   */
  public static final IBuiltInSymbol Factorial2 = initFinalSymbol("Factorial2", ID.Factorial2);

  public static final IBuiltInSymbol FactorialPower =
      initFinalSymbol("FactorialPower", ID.FactorialPower);

  /**
   * False - the constant `False` represents the boolean value **false**
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/False.md">False
   *     documentation</a>
   */
  public static final IBuiltInSymbol False = initFinalSymbol("False", ID.False);

  /**
   * Fibonacci(n) - returns the Fibonacci number of the integer `n`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fibonacci.md">Fibonacci
   *     documentation</a>
   */
  public static final IBuiltInSymbol Fibonacci = initFinalSymbol("Fibonacci", ID.Fibonacci);

  public static final IBuiltInSymbol File = initFinalSymbol("File", ID.File);

  public static final IBuiltInSymbol FileNameJoin =
      initFinalSymbol("FileNameJoin", ID.FileNameJoin);

  public static final IBuiltInSymbol FileNameTake =
      initFinalSymbol("FileNameTake", ID.FileNameTake);

  /**
   * FileNames( ) - returns a list with the filenames in the current working folder..
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FileNames.md">FileNames
   *     documentation</a>
   */
  public static final IBuiltInSymbol FileNames = initFinalSymbol("FileNames", ID.FileNames);

  /**
   * FilePrint(file) - prints the raw contents of `file`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FilePrint.md">FilePrint
   *     documentation</a>
   */
  public static final IBuiltInSymbol FilePrint = initFinalSymbol("FilePrint", ID.FilePrint);

  /**
   * FilterRules(list-of-option-rules, list-of-rules) - filter the `list-of-option-rules` by
   * `list-of-rules`or `list-of-symbols`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FilterRules.md">FilterRules
   *     documentation</a>
   */
  public static final IBuiltInSymbol FilterRules = initFinalSymbol("FilterRules", ID.FilterRules);

  /**
   * FindClusters(list-of-data-points, k) - Clustering algorithm based on David Arthur and Sergei
   * Vassilvitski k-means++ algorithm. Create `k` number of clusters to split the
   * `list-of-data-points` into.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindClusters.md">FindClusters
   *     documentation</a>
   */
  public static final IBuiltInSymbol FindClusters =
      initFinalSymbol("FindClusters", ID.FindClusters);

  public static final IBuiltInSymbol FindEdgeCover =
      initFinalSymbol("FindEdgeCover", ID.FindEdgeCover);

  /**
   * FindEulerianCycle(graph) - find an eulerian cycle in the `graph`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindEulerianCycle.md">FindEulerianCycle
   *     documentation</a>
   */
  public static final IBuiltInSymbol FindEulerianCycle =
      initFinalSymbol("FindEulerianCycle", ID.FindEulerianCycle);

  /**
   * FindFit(list-of-data-points, function, parameters, variable) - solve a least squares problem
   * using the Levenberg-Marquardt algorithm.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindFit.md">FindFit
   *     documentation</a>
   */
  public static final IBuiltInSymbol FindFit = initFinalSymbol("FindFit", ID.FindFit);

  public static final IBuiltInSymbol FindGraphCommunities =
      initFinalSymbol("FindGraphCommunities", ID.FindGraphCommunities);

  /**
   * FindHamiltonianCycle(graph) - find an hamiltonian cycle in the `graph`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindHamiltonianCycle.md">FindHamiltonianCycle
   *     documentation</a>
   */
  public static final IBuiltInSymbol FindHamiltonianCycle =
      initFinalSymbol("FindHamiltonianCycle", ID.FindHamiltonianCycle);

  public static final IBuiltInSymbol FindIndependentEdgeSet =
      initFinalSymbol("FindIndependentEdgeSet", ID.FindIndependentEdgeSet);

  public static final IBuiltInSymbol FindIndependentVertexSet =
      initFinalSymbol("FindIndependentVertexSet", ID.FindIndependentVertexSet);

  /**
   * FindInstance(equations, vars) - attempts to find one solution which solves the `equations` for
   * the variables `vars`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindInstance.md">FindInstance
   *     documentation</a>
   */
  public static final IBuiltInSymbol FindInstance =
      initFinalSymbol("FindInstance", ID.FindInstance);

  /**
   * FindPermutation(list1, list2) - create a `Cycles({{...},{...}, ...})` permutation expression,
   * for two lists whose arguments are the same but may be differently arranged.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindPermutation.md">FindPermutation
   *     documentation</a>
   */
  public static final IBuiltInSymbol FindPermutation =
      initFinalSymbol("FindPermutation", ID.FindPermutation);

  /**
   * FindRoot(f, {x, xmin, xmax}) - searches for a numerical root of `f` for the variable `x`, in
   * the range `xmin` to `xmax`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindRoot.md">FindRoot
   *     documentation</a>
   */
  public static final IBuiltInSymbol FindRoot = initFinalSymbol("FindRoot", ID.FindRoot);

  /**
   * FindShortestPath(graph, source, destination) - find a shortest path in the `graph` from
   * `source` to `destination`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindShortestPath.md">FindShortestPath
   *     documentation</a>
   */
  public static final IBuiltInSymbol FindShortestPath =
      initFinalSymbol("FindShortestPath", ID.FindShortestPath);

  /**
   * FindShortestTour({{p11, p12}, {p21, p22}, {p31, p32}, ...}) - find a shortest tour in the
   * `graph` with minimum `EuclideanDistance`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindShortestTour.md">FindShortestTour
   *     documentation</a>
   */
  public static final IBuiltInSymbol FindShortestTour =
      initFinalSymbol("FindShortestTour", ID.FindShortestTour);

  /**
   * FindSpanningTree(graph) - find the minimum spanning tree in the `graph`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindSpanningTree.md">FindSpanningTree
   *     documentation</a>
   */
  public static final IBuiltInSymbol FindSpanningTree =
      initFinalSymbol("FindSpanningTree", ID.FindSpanningTree);

  /**
   * FindVertexCover(graph) - algorithm to find a vertex cover for a `graph`. A vertex cover is a
   * set of vertices that touches all the edges in the graph.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindVertexCover.md">FindVertexCover
   *     documentation</a>
   */
  public static final IBuiltInSymbol FindVertexCover =
      initFinalSymbol("FindVertexCover", ID.FindVertexCover);

  /**
   * First(expr) - returns the first element in `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/First.md">First
   *     documentation</a>
   */
  public static final IBuiltInSymbol First = initFinalSymbol("First", ID.First);

  /**
   * FirstCase({arg1, arg2, ...}, pattern-matcher) - returns the first of the elements `argi` for
   * which `pattern-matcher` is matching.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FirstCase.md">FirstCase
   *     documentation</a>
   */
  public static final IBuiltInSymbol FirstCase = initFinalSymbol("FirstCase", ID.FirstCase);

  /**
   * FirstPosition(expression, pattern-matcher) - returns the first subexpression of `expression`
   * for which `pattern-matcher` is matching.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FirstPosition.md">FirstPosition
   *     documentation</a>
   */
  public static final IBuiltInSymbol FirstPosition =
      initFinalSymbol("FirstPosition", ID.FirstPosition);

  /**
   * Fit(list-of-data-points, degree, variable) - solve a least squares problem using the
   * Levenberg-Marquardt algorithm.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fit.md">Fit
   *     documentation</a>
   */
  public static final IBuiltInSymbol Fit = initFinalSymbol("Fit", ID.Fit);

  /**
   * FittedModel( ) - `FittedModel`holds the model generated with `LinearModelFit`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FittedModel.md">FittedModel
   *     documentation</a>
   */
  public static final IBuiltInSymbol FittedModel = initFinalSymbol("FittedModel", ID.FittedModel);

  /**
   * FiveNum({dataset}) - the Tuckey five-number summary is a set of descriptive statistics that
   * provide information about a `dataset`. It consists of the five most important sample
   * percentiles:
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FiveNum.md">FiveNum
   *     documentation</a>
   */
  public static final IBuiltInSymbol FiveNum = initFinalSymbol("FiveNum", ID.FiveNum);

  /**
   * FixedPoint(f, expr) - starting with `expr`, iteratively applies `f` until the result no longer
   * changes.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FixedPoint.md">FixedPoint
   *     documentation</a>
   */
  public static final IBuiltInSymbol FixedPoint = initFinalSymbol("FixedPoint", ID.FixedPoint);

  /**
   * FixedPointList(f, expr) - starting with `expr`, iteratively applies `f` until the result no
   * longer changes, and returns a list of all intermediate results.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FixedPointList.md">FixedPointList
   *     documentation</a>
   */
  public static final IBuiltInSymbol FixedPointList =
      initFinalSymbol("FixedPointList", ID.FixedPointList);

  /**
   * Flat - is an attribute that specifies that nested occurrences of a function should be
   * automatically flattened.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Flat.md">Flat
   *     documentation</a>
   */
  public static final IBuiltInSymbol Flat = initFinalSymbol("Flat", ID.Flat);

  public static final IBuiltInSymbol FlatTopWindow =
      initFinalSymbol("FlatTopWindow", ID.FlatTopWindow);

  /**
   * Flatten(expr) - flattens out nested lists in `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Flatten.md">Flatten
   *     documentation</a>
   */
  public static final IBuiltInSymbol Flatten = initFinalSymbol("Flatten", ID.Flatten);

  /**
   * FlattenAt(expr, position) - flattens out nested lists at the given `position` in `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FlattenAt.md">FlattenAt
   *     documentation</a>
   */
  public static final IBuiltInSymbol FlattenAt = initFinalSymbol("FlattenAt", ID.FlattenAt);

  public static final IBuiltInSymbol Float = initFinalSymbol("Float", ID.Float);

  /**
   * Floor(expr) - gives the smallest integer less than or equal `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Floor.md">Floor
   *     documentation</a>
   */
  public static final IBuiltInSymbol Floor = initFinalSymbol("Floor", ID.Floor);

  /**
   * Fold[f, x, {a, b}] - returns `f[f[x, a], b]`, and this nesting continues for lists of arbitrary
   * length.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fold.md">Fold
   *     documentation</a>
   */
  public static final IBuiltInSymbol Fold = initFinalSymbol("Fold", ID.Fold);

  /**
   * FoldList[f, x, {a, b}] - returns `{x, f[x, a], f[f[x, a], b]}`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FoldList.md">FoldList
   *     documentation</a>
   */
  public static final IBuiltInSymbol FoldList = initFinalSymbol("FoldList", ID.FoldList);

  /**
   * For(start, test, incr, body) - evaluates `start`, and then iteratively `body` and `incr` as
   * long as test evaluates to `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/For.md">For
   *     documentation</a>
   */
  public static final IBuiltInSymbol For = initFinalSymbol("For", ID.For);

  public static final IBuiltInSymbol ForAll = initFinalSymbol("ForAll", ID.ForAll);

  /**
   * Fourier(vector-of-complex-numbers) - Discrete Fourier transform of a
   * `vector-of-complex-numbers`. Fourier transform is restricted to vectors with length of power of
   * 2.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fourier.md">Fourier
   *     documentation</a>
   */
  public static final IBuiltInSymbol Fourier = initFinalSymbol("Fourier", ID.Fourier);

  /**
   * FourierMatrix(n) - gives a fourier matrix with the dimension `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FourierMatrix.md">FourierMatrix
   *     documentation</a>
   */
  public static final IBuiltInSymbol FourierMatrix =
      initFinalSymbol("FourierMatrix", ID.FourierMatrix);

  public static final IBuiltInSymbol FractionBox = initFinalSymbol("FractionBox", ID.FractionBox);

  /**
   * FractionalPart(number) - get the fractional part of a `number`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FractionalPart.md">FractionalPart
   *     documentation</a>
   */
  public static final IBuiltInSymbol FractionalPart =
      initFinalSymbol("FractionalPart", ID.FractionalPart);

  /**
   * FrechetDistribution(a,b) - returns a Frechet distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrechetDistribution.md">FrechetDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol FrechetDistribution =
      initFinalSymbol("FrechetDistribution", ID.FrechetDistribution);

  /**
   * FreeQ(`expr`, `x`) - returns 'True' if `expr` does not contain the expression `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FreeQ.md">FreeQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol FreeQ = initFinalSymbol("FreeQ", ID.FreeQ);

  public static final IBuiltInSymbol FresnelC = initFinalSymbol("FresnelC", ID.FresnelC);

  public static final IBuiltInSymbol FresnelS = initFinalSymbol("FresnelS", ID.FresnelS);

  /**
   * FrobeniusNumber({a1, ... ,aN}) - returns the Frobenius number of the nonnegative integers `{a1,
   * ... ,aN}`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrobeniusNumber.md">FrobeniusNumber
   *     documentation</a>
   */
  public static final IBuiltInSymbol FrobeniusNumber =
      initFinalSymbol("FrobeniusNumber", ID.FrobeniusNumber);

  /**
   * FrobeniusSolve({a1, ... ,aN}, M) - get a list of solutions for the Frobenius equation given by
   * the list of integers `{a1, ... ,aN}` and the non-negative integer `M`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrobeniusSolve.md">FrobeniusSolve
   *     documentation</a>
   */
  public static final IBuiltInSymbol FrobeniusSolve =
      initFinalSymbol("FrobeniusSolve", ID.FrobeniusSolve);

  /**
   * FromCharacterCode({ch1, ch2, ...}) - converts the `ch1, ch2,...` character codes into a string
   * of corresponding characters.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromCharacterCode.md">FromCharacterCode
   *     documentation</a>
   */
  public static final IBuiltInSymbol FromCharacterCode =
      initFinalSymbol("FromCharacterCode", ID.FromCharacterCode);

  /**
   * FromContinuedFraction({n1, n2, ...}) - reconstructs a number from the list of its continued
   * fraction terms `{n1, n2, ...}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromContinuedFraction.md">FromContinuedFraction
   *     documentation</a>
   */
  public static final IBuiltInSymbol FromContinuedFraction =
      initFinalSymbol("FromContinuedFraction", ID.FromContinuedFraction);

  /**
   * FromDigits(list) - creates an expression from the list of digits for radix `10`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromDigits.md">FromDigits
   *     documentation</a>
   */
  public static final IBuiltInSymbol FromDigits = initFinalSymbol("FromDigits", ID.FromDigits);

  /**
   * FromLetterNumber(number) - get the corresponding characters from the English alphabet.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromLetterNumber.md">FromLetterNumber
   *     documentation</a>
   */
  public static final IBuiltInSymbol FromLetterNumber =
      initFinalSymbol("FromLetterNumber", ID.FromLetterNumber);

  /**
   * FromPolarCoordinates({r, t}) - return the cartesian coordinates for the polar coordinates `{r,
   * t}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromPolarCoordinates.md">FromPolarCoordinates
   *     documentation</a>
   */
  public static final IBuiltInSymbol FromPolarCoordinates =
      initFinalSymbol("FromPolarCoordinates", ID.FromPolarCoordinates);

  public static final IBuiltInSymbol Full = initFinalSymbol("Full", ID.Full);

  /**
   * FullForm(expression) - shows the internal representation of the given `expression`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FullForm.md">FullForm
   *     documentation</a>
   */
  public static final IBuiltInSymbol FullForm = initFinalSymbol("FullForm", ID.FullForm);

  /**
   * FullSimplify(expr) - works like `Simplify` but additionally tries some `FunctionExpand` rule
   * transformations to simplify `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FullSimplify.md">FullSimplify
   *     documentation</a>
   */
  public static final IBuiltInSymbol FullSimplify =
      initFinalSymbol("FullSimplify", ID.FullSimplify);

  /**
   * Function(body) - represents a pure function with parameters `#1`, `#2`....
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Function.md">Function
   *     documentation</a>
   */
  public static final IBuiltInSymbol Function = initFinalSymbol("Function", ID.Function);

  /**
   * FunctionExpand(expression) - expands the special function `expression`. `FunctionExpand`
   * expands simple nested radicals.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FunctionExpand.md">FunctionExpand
   *     documentation</a>
   */
  public static final IBuiltInSymbol FunctionExpand =
      initFinalSymbol("FunctionExpand", ID.FunctionExpand);

  public static final IBuiltInSymbol FunctionRange =
      initFinalSymbol("FunctionRange", ID.FunctionRange);

  /**
   * FunctionURL(built-in-symbol) - returns the GitHub URL of the `built-in-symbol` implementation
   * in the [Symja GitHub repository](https://github.com/axkr/symja_android_library).
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FunctionURL.md">FunctionURL
   *     documentation</a>
   */
  public static final IBuiltInSymbol FunctionURL = initFinalSymbol("FunctionURL", ID.FunctionURL);

  /**
   * GCD(n1, n2, ...) - computes the greatest common divisor of the given integers.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GCD.md">GCD
   *     documentation</a>
   */
  public static final IBuiltInSymbol GCD = initFinalSymbol("GCD", ID.GCD);

  /**
   * Gamma(z) - is the gamma function on the complex number `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gamma.md">Gamma
   *     documentation</a>
   */
  public static final IBuiltInSymbol Gamma = initFinalSymbol("Gamma", ID.Gamma);

  /**
   * GammaDistribution(a,b) - returns a gamma distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GammaDistribution.md">GammaDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol GammaDistribution =
      initFinalSymbol("GammaDistribution", ID.GammaDistribution);

  public static final IBuiltInSymbol GammaRegularized =
      initFinalSymbol("GammaRegularized", ID.GammaRegularized);

  /**
   * Gather(list, test) - gathers leaves of `list` into sub lists of items that are the same
   * according to `test`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gather.md">Gather
   *     documentation</a>
   */
  public static final IBuiltInSymbol Gather = initFinalSymbol("Gather", ID.Gather);

  /**
   * GatherBy(list, f) - gathers leaves of `list` into sub lists of items whose image under `f`
   * identical.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GatherBy.md">GatherBy
   *     documentation</a>
   */
  public static final IBuiltInSymbol GatherBy = initFinalSymbol("GatherBy", ID.GatherBy);

  public static final IBuiltInSymbol GaussianIntegers =
      initFinalSymbol("GaussianIntegers", ID.GaussianIntegers);

  public static final IBuiltInSymbol GaussianMatrix =
      initFinalSymbol("GaussianMatrix", ID.GaussianMatrix);

  public static final IBuiltInSymbol GaussianWindow =
      initFinalSymbol("GaussianWindow", ID.GaussianWindow);

  /**
   * GegenbauerC(n, a, x) - returns the GegenbauerC polynomial.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GegenbauerC.md">GegenbauerC
   *     documentation</a>
   */
  public static final IBuiltInSymbol GegenbauerC = initFinalSymbol("GegenbauerC", ID.GegenbauerC);

  public static final IBuiltInSymbol General = initFinalSymbol("General", ID.General);

  /**
   * GeoDistance({latitude1,longitude1}, {latitude2,longitude2}) - returns the geodesic distance
   * between `{latitude1,longitude1}` and `{latitude2,longitude2}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoDistance.md">GeoDistance
   *     documentation</a>
   */
  public static final IBuiltInSymbol GeoDistance = initFinalSymbol("GeoDistance", ID.GeoDistance);

  public static final IBuiltInSymbol GeoPosition = initFinalSymbol("GeoPosition", ID.GeoPosition);

  public static final IBuiltInSymbol GeodesyData = initFinalSymbol("GeodesyData", ID.GeodesyData);

  /**
   * GeometricDistribution(p) - returns a geometric distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeometricDistribution.md">GeometricDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol GeometricDistribution =
      initFinalSymbol("GeometricDistribution", ID.GeometricDistribution);

  /**
   * GeometricMean({a, b, c,...}) - returns the geometric mean of `{a, b, c,...}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeometricMean.md">GeometricMean
   *     documentation</a>
   */
  public static final IBuiltInSymbol GeometricMean =
      initFinalSymbol("GeometricMean", ID.GeometricMean);

  public static final IBuiltInSymbol GeometricTransformation =
      initFinalSymbol("GeometricTransformation", ID.GeometricTransformation);

  /**
   * Get("path-to-package-file-name") - load the package defined in `path-to-package-file-name`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Get.md">Get
   *     documentation</a>
   */
  public static final IBuiltInSymbol Get = initFinalSymbol("Get", ID.Get);

  /**
   * Glaisher - Glaisher constant.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Glaisher.md">Glaisher
   *     documentation</a>
   */
  public static final IBuiltInSymbol Glaisher = initFinalSymbol("Glaisher", ID.Glaisher);

  public static final IBuiltInSymbol GoldenAngle = initFinalSymbol("GoldenAngle", ID.GoldenAngle);

  /**
   * GoldenRatio - is the golden ratio `(1+Sqrt(5))/2`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GoldenRatio.md">GoldenRatio
   *     documentation</a>
   */
  public static final IBuiltInSymbol GoldenRatio = initFinalSymbol("GoldenRatio", ID.GoldenRatio);

  public static final IBuiltInSymbol GompertzMakehamDistribution =
      initFinalSymbol("GompertzMakehamDistribution", ID.GompertzMakehamDistribution);

  /**
   * Grad(function, list-of-variables) - gives the gradient of the function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Grad.md">Grad
   *     documentation</a>
   */
  public static final IBuiltInSymbol Grad = initFinalSymbol("Grad", ID.Grad);

  /**
   * Graph({edge1,...,edgeN}) - create a graph from the given edges `edge1,...,edgeN`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Graph.md">Graph
   *     documentation</a>
   */
  public static final IBuiltInSymbol Graph = initFinalSymbol("Graph", ID.Graph);

  /**
   * GraphCenter(graph) - compute the `graph` center. The center of a `graph` is the set of vertices
   * of graph eccentricity equal to the `graph` radius.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphCenter.md">GraphCenter
   *     documentation</a>
   */
  public static final IBuiltInSymbol GraphCenter = initFinalSymbol("GraphCenter", ID.GraphCenter);

  public static final IBuiltInSymbol GraphData = initFinalSymbol("GraphData", ID.GraphData);

  /**
   * GraphDiameter(graph) - return the diameter of the `graph`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphDiameter.md">GraphDiameter
   *     documentation</a>
   */
  public static final IBuiltInSymbol GraphDiameter =
      initFinalSymbol("GraphDiameter", ID.GraphDiameter);

  /**
   * GraphPeriphery(graph) - compute the `graph` periphery. The periphery of a `graph` is the set of
   * vertices of graph eccentricity equal to the graph diameter.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphPeriphery.md">GraphPeriphery
   *     documentation</a>
   */
  public static final IBuiltInSymbol GraphPeriphery =
      initFinalSymbol("GraphPeriphery", ID.GraphPeriphery);

  /**
   * GraphQ(expr) - test if `expr` is a graph object.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphQ.md">GraphQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol GraphQ = initFinalSymbol("GraphQ", ID.GraphQ);

  /**
   * GraphRadius(graph) - return the radius of the `graph`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphRadius.md">GraphRadius
   *     documentation</a>
   */
  public static final IBuiltInSymbol GraphRadius = initFinalSymbol("GraphRadius", ID.GraphRadius);

  public static final IBuiltInSymbol GraphUnion = initFinalSymbol("GraphUnion", ID.GraphUnion);

  public static final IBuiltInSymbol Graphics = initFinalSymbol("Graphics", ID.Graphics);

  /**
   * Graphics3D(primitives, options) - represents a three-dimensional graphic.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Graphics3D.md">Graphics3D
   *     documentation</a>
   */
  public static final IBuiltInSymbol Graphics3D = initFinalSymbol("Graphics3D", ID.Graphics3D);

  public static final IBuiltInSymbol GraphicsComplex =
      initFinalSymbol("GraphicsComplex", ID.GraphicsComplex);

  public static final IBuiltInSymbol GraphicsGroup =
      initFinalSymbol("GraphicsGroup", ID.GraphicsGroup);

  public static final IBuiltInSymbol Gray = initFinalSymbol("Gray", ID.Gray);

  public static final IBuiltInSymbol GrayLevel = initFinalSymbol("GrayLevel", ID.GrayLevel);

  /**
   * Greater(x, y) - yields `True` if `x` is known to be greater than `y`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Greater.md">Greater
   *     documentation</a>
   */
  public static final IBuiltInSymbol Greater = initFinalSymbol("Greater", ID.Greater);

  /**
   * GreaterEqual(x, y) - yields `True` if `x` is known to be greater than or equal to `y`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterEqual.md">GreaterEqual
   *     documentation</a>
   */
  public static final IBuiltInSymbol GreaterEqual =
      initFinalSymbol("GreaterEqual", ID.GreaterEqual);

  public static final IBuiltInSymbol GreaterEqualThan =
      initFinalSymbol("GreaterEqualThan", ID.GreaterEqualThan);

  public static final IBuiltInSymbol GreaterThan = initFinalSymbol("GreaterThan", ID.GreaterThan);

  public static final IBuiltInSymbol Green = initFinalSymbol("Green", ID.Green);

  /**
   * GroebnerBasis({polynomial-list},{variable-list}) - returns a Gröbner basis for the
   * `polynomial-list` and `variable-list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GroebnerBasis.md">GroebnerBasis
   *     documentation</a>
   */
  public static final IBuiltInSymbol GroebnerBasis =
      initFinalSymbol("GroebnerBasis", ID.GroebnerBasis);

  /**
   * GroupBy(list, head) - return an association where the elements of `list` are grouped by
   * `head(element)`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GroupBy.md">GroupBy
   *     documentation</a>
   */
  public static final IBuiltInSymbol GroupBy = initFinalSymbol("GroupBy", ID.GroupBy);

  /**
   * Gudermannian(expr) - computes the gudermannian function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gudermannian.md">Gudermannian
   *     documentation</a>
   */
  public static final IBuiltInSymbol Gudermannian =
      initFinalSymbol("Gudermannian", ID.Gudermannian);

  /**
   * GumbelDistribution(a, b) - returns a Gumbel distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GumbelDistribution.md">GumbelDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol GumbelDistribution =
      initFinalSymbol("GumbelDistribution", ID.GumbelDistribution);

  /**
   * HamiltonianGraphQ(graph) - returns `True` if `graph` is an hamiltonian graph, and `False`
   * otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HamiltonianGraphQ.md">HamiltonianGraphQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol HamiltonianGraphQ =
      initFinalSymbol("HamiltonianGraphQ", ID.HamiltonianGraphQ);

  /**
   * HammingDistance(a, b) - returns the Hamming distance of `a` and `b`, i.e. the number of
   * different elements.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HammingDistance.md">HammingDistance
   *     documentation</a>
   */
  public static final IBuiltInSymbol HammingDistance =
      initFinalSymbol("HammingDistance", ID.HammingDistance);

  public static final IBuiltInSymbol HammingWindow =
      initFinalSymbol("HammingWindow", ID.HammingWindow);

  public static final IBuiltInSymbol HankelH1 = initFinalSymbol("HankelH1", ID.HankelH1);

  public static final IBuiltInSymbol HankelH2 = initFinalSymbol("HankelH2", ID.HankelH2);

  public static final IBuiltInSymbol HannWindow = initFinalSymbol("HannWindow", ID.HannWindow);

  /**
   * HarmonicMean({a, b, c,...}) - returns the harmonic mean of `{a, b, c,...}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HarmonicMean.md">HarmonicMean
   *     documentation</a>
   */
  public static final IBuiltInSymbol HarmonicMean =
      initFinalSymbol("HarmonicMean", ID.HarmonicMean);

  /**
   * HarmonicNumber(n) - returns the `n`th harmonic number.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HarmonicNumber.md">HarmonicNumber
   *     documentation</a>
   */
  public static final IBuiltInSymbol HarmonicNumber =
      initFinalSymbol("HarmonicNumber", ID.HarmonicNumber);

  /**
   * Haversine(z) - returns the haversine function of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Haversine.md">Haversine
   *     documentation</a>
   */
  public static final IBuiltInSymbol Haversine = initFinalSymbol("Haversine", ID.Haversine);

  /**
   * Head(expr) - returns the head of the expression or atom `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Head.md">Head
   *     documentation</a>
   */
  public static final IBuiltInSymbol Head = initFinalSymbol("Head", ID.Head);

  public static final IBuiltInSymbol Heads = initFinalSymbol("Heads", ID.Heads);

  /**
   * HeavisideTheta(expr1, expr2, ... exprN) - returns `1` if all `expr1, expr2, ... exprN` are
   * positive and `0` if one of the `expr1, expr2, ... exprN` is negative. `HeavisideTheta(0)`
   * returns unevaluated as `HeavisideTheta(0)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HeavisideTheta.md">HeavisideTheta
   *     documentation</a>
   */
  public static final IBuiltInSymbol HeavisideTheta =
      initFinalSymbol("HeavisideTheta", ID.HeavisideTheta);

  /**
   * HermiteH(n, x) - returns the Hermite polynomial `H_n(x)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HermiteH.md">HermiteH
   *     documentation</a>
   */
  public static final IBuiltInSymbol HermiteH = initFinalSymbol("HermiteH", ID.HermiteH);

  /**
   * HermitianMatrixQ(m) - returns `True` if `m` is a hermitian matrix.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HermitianMatrixQ.md">HermitianMatrixQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol HermitianMatrixQ =
      initFinalSymbol("HermitianMatrixQ", ID.HermitianMatrixQ);

  /**
   * HexidecimalCharacter - represents the characters `0-9`, `a-f` and `A-F`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HexidecimalCharacter.md">HexidecimalCharacter
   *     documentation</a>
   */
  public static final IBuiltInSymbol HexidecimalCharacter =
      initFinalSymbol("HexidecimalCharacter", ID.HexidecimalCharacter);

  /**
   * HilbertMatrix(n) - gives the hilbert matrix with `n` rows and columns.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HilbertMatrix.md">HilbertMatrix
   *     documentation</a>
   */
  public static final IBuiltInSymbol HilbertMatrix =
      initFinalSymbol("HilbertMatrix", ID.HilbertMatrix);

  /**
   * Histogram(list-of-values) - plots a histogram for a `list-of-values`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Histogram.md">Histogram
   *     documentation</a>
   */
  public static final IBuiltInSymbol Histogram = initFinalSymbol("Histogram", ID.Histogram);

  public static final IBuiltInSymbol HodgeDual = initFinalSymbol("HodgeDual", ID.HodgeDual);

  /**
   * Hold(expr) - `Hold` doesn't evaluate `expr`. `Hold` evaluates `UpValues`for its arguments.
   * `HoldComplete` doesn't evaluate `UpValues`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hold.md">Hold
   *     documentation</a>
   */
  public static final IBuiltInSymbol Hold = initFinalSymbol("Hold", ID.Hold);

  /**
   * HoldAll - is an attribute specifying that all arguments of a function should be left
   * unevaluated.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldAll.md">HoldAll
   *     documentation</a>
   */
  public static final IBuiltInSymbol HoldAll = initFinalSymbol("HoldAll", ID.HoldAll);

  public static final IBuiltInSymbol HoldAllComplete =
      initFinalSymbol("HoldAllComplete", ID.HoldAllComplete);

  /**
   * HoldComplete(expr) - `HoldComplete` doesn't evaluate `expr`. `Hold` evaluates `UpValues`for its
   * arguments. `HoldComplete` doesn't evaluate `UpValues`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldComplete.md">HoldComplete
   *     documentation</a>
   */
  public static final IBuiltInSymbol HoldComplete =
      initFinalSymbol("HoldComplete", ID.HoldComplete);

  /**
   * HoldFirst - is an attribute specifying that the first argument of a function should be left
   * unevaluated.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldFirst.md">HoldFirst
   *     documentation</a>
   */
  public static final IBuiltInSymbol HoldFirst = initFinalSymbol("HoldFirst", ID.HoldFirst);

  /**
   * HoldForm(expr) - `HoldForm` doesn't evaluate `expr` and didn't appear in the output.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldForm.md">HoldForm
   *     documentation</a>
   */
  public static final IBuiltInSymbol HoldForm = initFinalSymbol("HoldForm", ID.HoldForm);

  /**
   * HoldPattern(expr) - `HoldPattern` doesn't evaluate `expr` for pattern-matching.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldPattern.md">HoldPattern
   *     documentation</a>
   */
  public static final IBuiltInSymbol HoldPattern = initFinalSymbol("HoldPattern", ID.HoldPattern);

  /**
   * HoldRest - is an attribute specifying that all but the first argument of a function should be
   * left unevaluated.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldRest.md">HoldRest
   *     documentation</a>
   */
  public static final IBuiltInSymbol HoldRest = initFinalSymbol("HoldRest", ID.HoldRest);

  public static final IBuiltInSymbol Horner = initFinalSymbol("Horner", ID.Horner);

  /**
   * HornerForm(polynomial) - Generate the horner scheme for a univariate `polynomial`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HornerForm.md">HornerForm
   *     documentation</a>
   */
  public static final IBuiltInSymbol HornerForm = initFinalSymbol("HornerForm", ID.HornerForm);

  public static final IBuiltInSymbol Hue = initFinalSymbol("Hue", ID.Hue);

  /**
   * HurwitzZeta(s, a) - returns the Hurwitz zeta function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HurwitzZeta.md">HurwitzZeta
   *     documentation</a>
   */
  public static final IBuiltInSymbol HurwitzZeta = initFinalSymbol("HurwitzZeta", ID.HurwitzZeta);

  public static final IBuiltInSymbol HypercubeGraph =
      initFinalSymbol("HypercubeGraph", ID.HypercubeGraph);

  /**
   * Hypergeometric0F1(b, z) - return the `Hypergeometric0F1` function
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hypergeometric0F1.md">Hypergeometric0F1
   *     documentation</a>
   */
  public static final IBuiltInSymbol Hypergeometric0F1 =
      initFinalSymbol("Hypergeometric0F1", ID.Hypergeometric0F1);

  /**
   * Hypergeometric1F1(a, b, z) - return the `Hypergeometric1F1` function
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hypergeometric1F1.md">Hypergeometric1F1
   *     documentation</a>
   */
  public static final IBuiltInSymbol Hypergeometric1F1 =
      initFinalSymbol("Hypergeometric1F1", ID.Hypergeometric1F1);

  public static final IBuiltInSymbol Hypergeometric1F1Regularized =
      initFinalSymbol("Hypergeometric1F1Regularized", ID.Hypergeometric1F1Regularized);

  /**
   * Hypergeometric2F1(a, b, c, z) - return the `Hypergeometric2F1` function
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hypergeometric2F1.md">Hypergeometric2F1
   *     documentation</a>
   */
  public static final IBuiltInSymbol Hypergeometric2F1 =
      initFinalSymbol("Hypergeometric2F1", ID.Hypergeometric2F1);

  /**
   * HypergeometricDistribution(n, s, t) - returns a hypergeometric distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HypergeometricDistribution.md">HypergeometricDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol HypergeometricDistribution =
      initFinalSymbol("HypergeometricDistribution", ID.HypergeometricDistribution);

  /**
   * HypergeometricPFQ({a,...}, {b,...}, c) - return the `HypergeometricPFQ` function
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HypergeometricPFQ.md">HypergeometricPFQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol HypergeometricPFQ =
      initFinalSymbol("HypergeometricPFQ", ID.HypergeometricPFQ);

  public static final IBuiltInSymbol HypergeometricPFQRegularized =
      initFinalSymbol("HypergeometricPFQRegularized", ID.HypergeometricPFQRegularized);

  public static final IBuiltInSymbol HypergeometricU =
      initFinalSymbol("HypergeometricU", ID.HypergeometricU);

  /**
   * I - Imaginary unit - internally converted to the complex number `0+1*i`. `I` represents the
   * imaginary number `Sqrt(-1)`. `I^2` will be evaluated to `-1`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/I.md">I
   *     documentation</a>
   */
  public static final IBuiltInSymbol I = initFinalSymbol("I", ID.I);

  public static final IBuiltInSymbol Icosahedron = initFinalSymbol("Icosahedron", ID.Icosahedron);

  /**
   * Identity(x) - is the identity function, which returns `x` unchanged.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Identity.md">Identity
   *     documentation</a>
   */
  public static final IBuiltInSymbol Identity = initFinalSymbol("Identity", ID.Identity);

  /**
   * IdentityMatrix(n) - gives the identity matrix with `n` rows and columns.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IdentityMatrix.md">IdentityMatrix
   *     documentation</a>
   */
  public static final IBuiltInSymbol IdentityMatrix =
      initFinalSymbol("IdentityMatrix", ID.IdentityMatrix);

  /**
   * If(cond, pos, neg) - returns `pos` if `cond` evaluates to `True`, and `neg` if it evaluates to
   * `False`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/If.md">If
   *     documentation</a>
   */
  public static final IBuiltInSymbol If = initFinalSymbol("If", ID.If);

  public static final IBuiltInSymbol IgnoreCase = initFinalSymbol("IgnoreCase", ID.IgnoreCase);

  /**
   * Im(z) - returns the imaginary component of the complex number `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Im.md">Im
   *     documentation</a>
   */
  public static final IBuiltInSymbol Im = initFinalSymbol("Im", ID.Im);

  /**
   * Implies(arg1, arg2) - Logical implication.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Implies.md">Implies
   *     documentation</a>
   */
  public static final IBuiltInSymbol Implies = initFinalSymbol("Implies", ID.Implies);

  /**
   * Import("path-to-filename", "WXF") - if the file system is enabled, import an expression in WXF
   * format from the "path-to-filename" file.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Import.md">Import
   *     documentation</a>
   */
  public static final IBuiltInSymbol Import = initFinalSymbol("Import", ID.Import);

  public static final IBuiltInSymbol ImportString =
      initFinalSymbol("ImportString", ID.ImportString);

  /**
   * In(k) - gives the `k`th line of input.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/In.md">In
   *     documentation</a>
   */
  public static final IBuiltInSymbol In = initFinalSymbol("In", ID.In);

  /**
   * Increment(x) - increments `x` by `1`, returning the original value of `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Increment.md">Increment
   *     documentation</a>
   */
  public static final IBuiltInSymbol Increment = initFinalSymbol("Increment", ID.Increment);

  /**
   * Indeterminate - represents an indeterminate result.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Indeterminate.md">Indeterminate
   *     documentation</a>
   */
  public static final IBuiltInSymbol Indeterminate =
      initFinalSymbol("Indeterminate", ID.Indeterminate);

  public static final IBuiltInSymbol Inequality = initFinalSymbol("Inequality", ID.Inequality);

  /**
   * InexactNumberQ(expr) - returns `True` if `expr` is not an exact number, and `False` otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InexactNumberQ.md">InexactNumberQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol InexactNumberQ =
      initFinalSymbol("InexactNumberQ", ID.InexactNumberQ);

  /**
   * Infinity - represents an infinite real quantity.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Infinity.md">Infinity
   *     documentation</a>
   */
  public static final IBuiltInSymbol Infinity = initFinalSymbol("Infinity", ID.Infinity);

  public static final IBuiltInSymbol Infix = initFinalSymbol("Infix", ID.Infix);

  public static final IBuiltInSymbol Information = initFinalSymbol("Information", ID.Information);

  public static final IBuiltInSymbol Inherited = initFinalSymbol("Inherited", ID.Inherited);

  /**
   * Inner(f, x, y, g) - computes a generalized inner product of `x` and `y`, using a multiplication
   * function `f` and an addition function `g`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Inner.md">Inner
   *     documentation</a>
   */
  public static final IBuiltInSymbol Inner = initFinalSymbol("Inner", ID.Inner);

  /**
   * Input() - if the file system is enabled, the user can input an expression. After input this
   * expression will be evaluated immediately.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Input.md">Input
   *     documentation</a>
   */
  public static final IBuiltInSymbol Input = initFinalSymbol("Input", ID.Input);

  public static final IBuiltInSymbol InputField = initFinalSymbol("InputField", ID.InputField);

  /**
   * InputForm(expr) - print the `expr` as if it should be inserted by the user for evaluation.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InputForm.md">InputForm
   *     documentation</a>
   */
  public static final IBuiltInSymbol InputForm = initFinalSymbol("InputForm", ID.InputForm);

  public static final IBuiltInSymbol InputStream = initFinalSymbol("InputStream", ID.InputStream);

  /**
   * InputString() - if the file system is enabled, the user can input a string.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InputString.md">InputString
   *     documentation</a>
   */
  public static final IBuiltInSymbol InputString = initFinalSymbol("InputString", ID.InputString);

  /**
   * Insert(list, elem, n) - inserts `elem` at position `n` in `list`. When `n` is negative, the
   * position is counted from the end.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Insert.md">Insert
   *     documentation</a>
   */
  public static final IBuiltInSymbol Insert = initFinalSymbol("Insert", ID.Insert);

  public static final IBuiltInSymbol InsertionFunction =
      initFinalSymbol("InsertionFunction", ID.InsertionFunction);

  public static final IBuiltInSymbol InstallJava = initFinalSymbol("InstallJava", ID.InstallJava);

  /**
   * InstanceOf[java-object, "class-name"] - return the result of the Java expression `java-object
   * instanceof class`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InstanceOf.md">InstanceOf
   *     documentation</a>
   */
  public static final IBuiltInSymbol InstanceOf = initFinalSymbol("InstanceOf", ID.InstanceOf);

  /**
   * Integer - is the head of integers.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Integer.md">Integer
   *     documentation</a>
   */
  public static final IBuiltInSymbol Integer = initFinalSymbol("Integer", ID.Integer);

  /**
   * IntegerDigits(n, base) - returns a list of integer digits for `n` under `base`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerDigits.md">IntegerDigits
   *     documentation</a>
   */
  public static final IBuiltInSymbol IntegerDigits =
      initFinalSymbol("IntegerDigits", ID.IntegerDigits);

  /**
   * IntegerExponent(n, b) - gives the highest exponent of `b` that divides `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerExponent.md">IntegerExponent
   *     documentation</a>
   */
  public static final IBuiltInSymbol IntegerExponent =
      initFinalSymbol("IntegerExponent", ID.IntegerExponent);

  /**
   * IntegerLength(x) - gives the number of digits in the base-10 representation of `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerLength.md">IntegerLength
   *     documentation</a>
   */
  public static final IBuiltInSymbol IntegerLength =
      initFinalSymbol("IntegerLength", ID.IntegerLength);

  /**
   * IntegerName(integer-number) - gives the spoken number string of `integer-number` in language
   * `English`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerName.md">IntegerName
   *     documentation</a>
   */
  public static final IBuiltInSymbol IntegerName = initFinalSymbol("IntegerName", ID.IntegerName);

  /**
   * IntegerPart(expr) - for real `expr` return the integer part of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerPart.md">IntegerPart
   *     documentation</a>
   */
  public static final IBuiltInSymbol IntegerPart = initFinalSymbol("IntegerPart", ID.IntegerPart);

  /**
   * IntegerPartitions(n) - returns all partitions of the integer `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerPartitions.md">IntegerPartitions
   *     documentation</a>
   */
  public static final IBuiltInSymbol IntegerPartitions =
      initFinalSymbol("IntegerPartitions", ID.IntegerPartitions);

  /**
   * IntegerQ(expr) - returns `True` if `expr` is an integer, and `False` otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerQ.md">IntegerQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol IntegerQ = initFinalSymbol("IntegerQ", ID.IntegerQ);

  /**
   * Integers - is the set of integer numbers.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Integers.md">Integers
   *     documentation</a>
   */
  public static final IBuiltInSymbol Integers = initFinalSymbol("Integers", ID.Integers);

  /**
   * Integrate(f, x) - integrates `f` with respect to `x`. The result does not contain the additive
   * integration constant.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Integrate.md">Integrate
   *     documentation</a>
   */
  public static final IBuiltInSymbol Integrate = initFinalSymbol("Integrate", ID.Integrate);

  /**
   * InterpolatingFunction(data-list) - get the representation for the given `data-list` as
   * piecewise `InterpolatingPolynomial`s.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterpolatingFunction.md">InterpolatingFunction
   *     documentation</a>
   */
  public static final IBuiltInSymbol InterpolatingFunction =
      initFinalSymbol("InterpolatingFunction", ID.InterpolatingFunction);

  /**
   * InterpolatingPolynomial(data-list, symbol) - get the polynomial representation for the given
   * `data-list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterpolatingPolynomial.md">InterpolatingPolynomial
   *     documentation</a>
   */
  public static final IBuiltInSymbol InterpolatingPolynomial =
      initFinalSymbol("InterpolatingPolynomial", ID.InterpolatingPolynomial);

  public static final IBuiltInSymbol Interpolation =
      initFinalSymbol("Interpolation", ID.Interpolation);

  /**
   * InterquartileRange(list) - returns the interquartile range (IQR), which is between upper and
   * lower quartiles, IQR = Q3 − Q1.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterquartileRange.md">InterquartileRange
   *     documentation</a>
   */
  public static final IBuiltInSymbol InterquartileRange =
      initFinalSymbol("InterquartileRange", ID.InterquartileRange);

  /**
   * Interrupt( ) - Interrupt an evaluation and returns `$Aborted`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Interrupt.md">Interrupt
   *     documentation</a>
   */
  public static final IBuiltInSymbol Interrupt = initFinalSymbol("Interrupt", ID.Interrupt);

  public static final IBuiltInSymbol IntersectingQ =
      initFinalSymbol("IntersectingQ", ID.IntersectingQ);

  /**
   * Intersection(set1, set2, ...) - get the intersection set from `set1` and `set2` ....
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Intersection.md">Intersection
   *     documentation</a>
   */
  public static final IBuiltInSymbol Intersection =
      initFinalSymbol("Intersection", ID.Intersection);

  /**
   * Interval({a, b}) - represents the interval from `a` to `b`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Interval.md">Interval
   *     documentation</a>
   */
  public static final IBuiltInSymbol Interval = initFinalSymbol("Interval", ID.Interval);

  /**
   * IntervalIntersection(interval_1, interval_2, ...) - compute the intersection of the intervals
   * `interval_1, interval_2, ...`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalIntersection.md">IntervalIntersection
   *     documentation</a>
   */
  public static final IBuiltInSymbol IntervalIntersection =
      initFinalSymbol("IntervalIntersection", ID.IntervalIntersection);

  /**
   * IntervalMemberQ(interval, interval-or-real-number) - returns `True`, if
   * `interval-or-real-number` is completly sourrounded by `interval`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalMemberQ.md">IntervalMemberQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol IntervalMemberQ =
      initFinalSymbol("IntervalMemberQ", ID.IntervalMemberQ);

  /**
   * IntervalUnion(interval_1, interval_2, ...) - compute the union of the intervals `interval_1,
   * interval_2, ...`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalUnion.md">IntervalUnion
   *     documentation</a>
   */
  public static final IBuiltInSymbol IntervalUnion =
      initFinalSymbol("IntervalUnion", ID.IntervalUnion);

  /**
   * Inverse(matrix) - computes the inverse of the `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Inverse.md">Inverse
   *     documentation</a>
   */
  public static final IBuiltInSymbol Inverse = initFinalSymbol("Inverse", ID.Inverse);

  public static final IBuiltInSymbol InverseBetaRegularized =
      initFinalSymbol("InverseBetaRegularized", ID.InverseBetaRegularized);

  /**
   * InverseCDF(dist, q) - returns the inverse cumulative distribution for the distribution `dist`
   * as a function of `q`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseCDF.md">InverseCDF
   *     documentation</a>
   */
  public static final IBuiltInSymbol InverseCDF = initFinalSymbol("InverseCDF", ID.InverseCDF);

  /**
   * InverseErf(z) - returns the inverse error function of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseErf.md">InverseErf
   *     documentation</a>
   */
  public static final IBuiltInSymbol InverseErf = initFinalSymbol("InverseErf", ID.InverseErf);

  /**
   * InverseErfc(z) - returns the inverse complementary error function of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseErfc.md">InverseErfc
   *     documentation</a>
   */
  public static final IBuiltInSymbol InverseErfc = initFinalSymbol("InverseErfc", ID.InverseErfc);

  /**
   * InverseFourier(vector-of-complex-numbers) - Inverse discrete Fourier transform of a
   * `vector-of-complex-numbers`. Fourier transform is restricted to vectors with length of power of
   * 2.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseFourier.md">InverseFourier
   *     documentation</a>
   */
  public static final IBuiltInSymbol InverseFourier =
      initFinalSymbol("InverseFourier", ID.InverseFourier);

  /**
   * InverseFunction(head) - returns the inverse function for the symbol `head`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseFunction.md">InverseFunction
   *     documentation</a>
   */
  public static final IBuiltInSymbol InverseFunction =
      initFinalSymbol("InverseFunction", ID.InverseFunction);

  public static final IBuiltInSymbol InverseGammaRegularized =
      initFinalSymbol("InverseGammaRegularized", ID.InverseGammaRegularized);

  /**
   * InverseGudermannian(expr) - computes the inverse gudermannian function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseGudermannian.md">InverseGudermannian
   *     documentation</a>
   */
  public static final IBuiltInSymbol InverseGudermannian =
      initFinalSymbol("InverseGudermannian", ID.InverseGudermannian);

  /**
   * InverseHaversine(z) - returns the inverse haversine function of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseHaversine.md">InverseHaversine
   *     documentation</a>
   */
  public static final IBuiltInSymbol InverseHaversine =
      initFinalSymbol("InverseHaversine", ID.InverseHaversine);

  /**
   * InverseLaplaceTransform(f,s,t) - returns the inverse laplace transform.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseLaplaceTransform.md">InverseLaplaceTransform
   *     documentation</a>
   */
  public static final IBuiltInSymbol InverseLaplaceTransform =
      initFinalSymbol("InverseLaplaceTransform", ID.InverseLaplaceTransform);

  /**
   * InverseSeries( series ) - return the inverse series.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseSeries.md">InverseSeries
   *     documentation</a>
   */
  public static final IBuiltInSymbol InverseSeries =
      initFinalSymbol("InverseSeries", ID.InverseSeries);

  public static final IBuiltInSymbol InverseWeierstrassP =
      initFinalSymbol("InverseWeierstrassP", ID.InverseWeierstrassP);

  /**
   * JSForm(expr) - returns the JavaScript form of the `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JSForm.md">JSForm
   *     documentation</a>
   */
  public static final IBuiltInSymbol JSForm = initFinalSymbol("JSForm", ID.JSForm);

  public static final IBuiltInSymbol JSFormData = initFinalSymbol("JSFormData", ID.JSFormData);

  /**
   * JaccardDissimilarity(u, v) - returns the Jaccard-Needham dissimilarity between the two boolean
   * 1-D lists `u` and `v`, which is defined as `(c_tf + c_ft) / (c_tt + c_ft + c_tf)`, where n is
   * `len(u)` and `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JaccardDissimilarity.md">JaccardDissimilarity
   *     documentation</a>
   */
  public static final IBuiltInSymbol JaccardDissimilarity =
      initFinalSymbol("JaccardDissimilarity", ID.JaccardDissimilarity);

  /**
   * JacobiAmplitude(x, m) - returns the amplitude `am(x, m)` for Jacobian elliptic function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiAmplitude.md">JacobiAmplitude
   *     documentation</a>
   */
  public static final IBuiltInSymbol JacobiAmplitude =
      initFinalSymbol("JacobiAmplitude", ID.JacobiAmplitude);

  /**
   * JacobiCD(x, m) - returns the Jacobian elliptic function `cd(x, m)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiCD.md">JacobiCD
   *     documentation</a>
   */
  public static final IBuiltInSymbol JacobiCD = initFinalSymbol("JacobiCD", ID.JacobiCD);

  /**
   * JacobiCN(x, m) - returns the Jacobian elliptic function `cn(x, m)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiCN.md">JacobiCN
   *     documentation</a>
   */
  public static final IBuiltInSymbol JacobiCN = initFinalSymbol("JacobiCN", ID.JacobiCN);

  public static final IBuiltInSymbol JacobiDC = initFinalSymbol("JacobiDC", ID.JacobiDC);

  /**
   * JacobiDN(x, m) - returns the Jacobian elliptic function `dn(x, m)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiDN.md">JacobiDN
   *     documentation</a>
   */
  public static final IBuiltInSymbol JacobiDN = initFinalSymbol("JacobiDN", ID.JacobiDN);

  public static final IBuiltInSymbol JacobiEpsilon =
      initFinalSymbol("JacobiEpsilon", ID.JacobiEpsilon);

  /**
   * JacobiMatrix(matrix, var) - creates a Jacobian matrix.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiMatrix.md">JacobiMatrix
   *     documentation</a>
   */
  public static final IBuiltInSymbol JacobiMatrix =
      initFinalSymbol("JacobiMatrix", ID.JacobiMatrix);

  public static final IBuiltInSymbol JacobiNC = initFinalSymbol("JacobiNC", ID.JacobiNC);

  public static final IBuiltInSymbol JacobiND = initFinalSymbol("JacobiND", ID.JacobiND);

  /**
   * JacobiSC(x, m) - returns the Jacobian elliptic function `sc(x, m)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSC.md">JacobiSC
   *     documentation</a>
   */
  public static final IBuiltInSymbol JacobiSC = initFinalSymbol("JacobiSC", ID.JacobiSC);

  /**
   * JacobiSD(x, m) - returns the Jacobian elliptic function `sd(x, m)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSD.md">JacobiSD
   *     documentation</a>
   */
  public static final IBuiltInSymbol JacobiSD = initFinalSymbol("JacobiSD", ID.JacobiSD);

  /**
   * JacobiSN(x, m) - returns the Jacobian elliptic function `sn(x, m)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSN.md">JacobiSN
   *     documentation</a>
   */
  public static final IBuiltInSymbol JacobiSN = initFinalSymbol("JacobiSN", ID.JacobiSN);

  /**
   * JacobiSymbol(m, n) - calculates the Jacobi symbol.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSymbol.md">JacobiSymbol
   *     documentation</a>
   */
  public static final IBuiltInSymbol JacobiSymbol =
      initFinalSymbol("JacobiSymbol", ID.JacobiSymbol);

  public static final IBuiltInSymbol JacobiZeta = initFinalSymbol("JacobiZeta", ID.JacobiZeta);

  /**
   * JavaClass[class-name] - a `JavaClass` expression can be created with the `LoadJavaClass`
   * function and wraps a Java `java.lang.Class` object. All static method names are assigned to a
   * context which will be created by the last part of the class name.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaClass.md">JavaClass
   *     documentation</a>
   */
  public static final IBuiltInSymbol JavaClass = initFinalSymbol("JavaClass", ID.JavaClass);

  /**
   * JavaForm(expr) - returns the Symja Java form of the `expr`. In Java you can use the created
   * Symja expressions.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaForm.md">JavaForm
   *     documentation</a>
   */
  public static final IBuiltInSymbol JavaForm = initFinalSymbol("JavaForm", ID.JavaForm);

  /**
   * JavaObject[class className] - a `JavaObject` can be created with the `JavaNew` function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaNew.md">JavaNew
   *     documentation</a>
   */
  public static final IBuiltInSymbol JavaNew = initFinalSymbol("JavaNew", ID.JavaNew);

  /**
   * JavaNew["class-name"] - create a `JavaObject` from the `class-name` default constructor.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaObject.md">JavaObject
   *     documentation</a>
   */
  public static final IBuiltInSymbol JavaObject = initFinalSymbol("JavaObject", ID.JavaObject);

  /**
   * JavaObjectQ[java-object] - return `True` if `java-object` is a `JavaObject` expression.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaObjectQ.md">JavaObjectQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol JavaObjectQ = initFinalSymbol("JavaObjectQ", ID.JavaObjectQ);

  /**
   * JavaShow[ java.awt.Window ] - show the `JavaObject` which has to be an instance of
   * `java.awt.Window`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaShow.md">JavaShow
   *     documentation</a>
   */
  public static final IBuiltInSymbol JavaShow = initFinalSymbol("JavaShow", ID.JavaShow);

  /**
   * Join(l1, l2) - concatenates the lists `l1` and `l2`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Join.md">Join
   *     documentation</a>
   */
  public static final IBuiltInSymbol Join = initFinalSymbol("Join", ID.Join);

  public static final IBuiltInSymbol KOrderlessPartitions =
      initFinalSymbol("KOrderlessPartitions", ID.KOrderlessPartitions);

  public static final IBuiltInSymbol KPartitions = initFinalSymbol("KPartitions", ID.KPartitions);

  public static final IBuiltInSymbol KelvinBei = initFinalSymbol("KelvinBei", ID.KelvinBei);

  public static final IBuiltInSymbol KelvinBer = initFinalSymbol("KelvinBer", ID.KelvinBer);

  /**
   * Key(key) - represents a `key` used to access a value in an association.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Key.md">Key
   *     documentation</a>
   */
  public static final IBuiltInSymbol Key = initFinalSymbol("Key", ID.Key);

  public static final IBuiltInSymbol KeyAbsent = initFinalSymbol("KeyAbsent", ID.KeyAbsent);

  public static final IBuiltInSymbol KeyExistsQ = initFinalSymbol("KeyExistsQ", ID.KeyExistsQ);

  /**
   * KeySelect(<|key1->value1, ...|>, head) - returns an association of the elements for which
   * `head(keyi)` returns `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeySelect.md">KeySelect
   *     documentation</a>
   */
  public static final IBuiltInSymbol KeySelect = initFinalSymbol("KeySelect", ID.KeySelect);

  /**
   * KeySort(<|key1->value1, ...|>) - sort the `<|key1->value1, ...|>` entries by the `key` values.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeySort.md">KeySort
   *     documentation</a>
   */
  public static final IBuiltInSymbol KeySort = initFinalSymbol("KeySort", ID.KeySort);

  /**
   * KeyTake(<|key1->value1, ...|>, {k1, k2,...}) - returns an association of the rules for which
   * the `k1, k2,...` are keys in the association.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeyTake.md">KeyTake
   *     documentation</a>
   */
  public static final IBuiltInSymbol KeyTake = initFinalSymbol("KeyTake", ID.KeyTake);

  /**
   * Keys(association) - return a list of keys of the `association`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Keys.md">Keys
   *     documentation</a>
   */
  public static final IBuiltInSymbol Keys = initFinalSymbol("Keys", ID.Keys);

  /**
   * Khinchin - Khinchin's constant
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Khinchin.md">Khinchin
   *     documentation</a>
   */
  public static final IBuiltInSymbol Khinchin = initFinalSymbol("Khinchin", ID.Khinchin);

  public static final IBuiltInSymbol KleinInvariantJ =
      initFinalSymbol("KleinInvariantJ", ID.KleinInvariantJ);

  public static final IBuiltInSymbol KnownUnitQ = initFinalSymbol("KnownUnitQ", ID.KnownUnitQ);

  /**
   * KolmogorovSmirnovTest(data) - Computes the `p-value`, or <i>observed significance level</i>, of
   * a one-sample [Wikipedia:Kolmogorov-Smirnov
   * test](http://en.wikipedia.org/wiki/Kolmogorov-Smirnov_test) evaluating the null hypothesis that
   * `data` conforms to the `NormalDistribution()`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KolmogorovSmirnovTest.md">KolmogorovSmirnovTest
   *     documentation</a>
   */
  public static final IBuiltInSymbol KolmogorovSmirnovTest =
      initFinalSymbol("KolmogorovSmirnovTest", ID.KolmogorovSmirnovTest);

  /**
   * KroneckerDelta(arg1, arg2, ... argN) - if all arguments `arg1` to `argN` are equal return `1`,
   * otherwise return `0`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KroneckerDelta.md">KroneckerDelta
   *     documentation</a>
   */
  public static final IBuiltInSymbol KroneckerDelta =
      initFinalSymbol("KroneckerDelta", ID.KroneckerDelta);

  public static final IBuiltInSymbol KroneckerProduct =
      initFinalSymbol("KroneckerProduct", ID.KroneckerProduct);

  /**
   * Kurtosis(list) - gives the Pearson measure of kurtosis for `list` (a measure of existing
   * outliers).
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Kurtosis.md">Kurtosis
   *     documentation</a>
   */
  public static final IBuiltInSymbol Kurtosis = initFinalSymbol("Kurtosis", ID.Kurtosis);

  /**
   * LCM(n1, n2, ...) - computes the least common multiple of the given integers.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LCM.md">LCM
   *     documentation</a>
   */
  public static final IBuiltInSymbol LCM = initFinalSymbol("LCM", ID.LCM);

  /**
   * LUDecomposition(matrix) - calculate the LUP-decomposition of a square `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LUDecomposition.md">LUDecomposition
   *     documentation</a>
   */
  public static final IBuiltInSymbol LUDecomposition =
      initFinalSymbol("LUDecomposition", ID.LUDecomposition);

  public static final IBuiltInSymbol Labeled = initFinalSymbol("Labeled", ID.Labeled);

  /**
   * LaguerreL(n, x) - returns the Laguerre polynomial `L_n(x)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaguerreL.md">LaguerreL
   *     documentation</a>
   */
  public static final IBuiltInSymbol LaguerreL = initFinalSymbol("LaguerreL", ID.LaguerreL);

  public static final IBuiltInSymbol LambertW = initFinalSymbol("LambertW", ID.LambertW);

  /**
   * LaplaceTransform(f,t,s) - returns the laplace transform.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaplaceTransform.md">LaplaceTransform
   *     documentation</a>
   */
  public static final IBuiltInSymbol LaplaceTransform =
      initFinalSymbol("LaplaceTransform", ID.LaplaceTransform);

  /**
   * Last(expr) - returns the last element in `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Last.md">Last
   *     documentation</a>
   */
  public static final IBuiltInSymbol Last = initFinalSymbol("Last", ID.Last);

  /**
   * LeafCount(expr) - returns the total number of indivisible subexpressions in `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeafCount.md">LeafCount
   *     documentation</a>
   */
  public static final IBuiltInSymbol LeafCount = initFinalSymbol("LeafCount", ID.LeafCount);

  /**
   * LeastSquares(matrix, right) - solves the linear least-squares problem 'matrix . x = right'.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeastSquares.md">LeastSquares
   *     documentation</a>
   */
  public static final IBuiltInSymbol LeastSquares =
      initFinalSymbol("LeastSquares", ID.LeastSquares);

  public static final IBuiltInSymbol Left = initFinalSymbol("Left", ID.Left);

  /**
   * LegendreP(n, x) - returns the Legendre polynomial `P_n(x)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendreP.md">LegendreP
   *     documentation</a>
   */
  public static final IBuiltInSymbol LegendreP = initFinalSymbol("LegendreP", ID.LegendreP);

  /**
   * LegendreQ(n, x) - returns the Legendre functions of the second kind `Q_n(x)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendreQ.md">LegendreQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol LegendreQ = initFinalSymbol("LegendreQ", ID.LegendreQ);

  /**
   * Length(expr) - returns the number of leaves in `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Length.md">Length
   *     documentation</a>
   */
  public static final IBuiltInSymbol Length = initFinalSymbol("Length", ID.Length);

  /**
   * Less(x, y) - yields `True` if `x` is known to be less than `y`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Less.md">Less
   *     documentation</a>
   */
  public static final IBuiltInSymbol Less = initFinalSymbol("Less", ID.Less);

  /**
   * LessEqual(x, y) - yields `True` if `x` is known to be less than or equal `y`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqual.md">LessEqual
   *     documentation</a>
   */
  public static final IBuiltInSymbol LessEqual = initFinalSymbol("LessEqual", ID.LessEqual);

  public static final IBuiltInSymbol LessEqualThan =
      initFinalSymbol("LessEqualThan", ID.LessEqualThan);

  public static final IBuiltInSymbol LessThan = initFinalSymbol("LessThan", ID.LessThan);

  /**
   * LetterCharacter - represents letters..
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterCharacter.md">LetterCharacter
   *     documentation</a>
   */
  public static final IBuiltInSymbol LetterCharacter =
      initFinalSymbol("LetterCharacter", ID.LetterCharacter);

  /**
   * LetterCounts(string) - count the number of each distinct character in the `string` and return
   * the result as an association `<|char->counter1, ...|>`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterCounts.md">LetterCounts
   *     documentation</a>
   */
  public static final IBuiltInSymbol LetterCounts =
      initFinalSymbol("LetterCounts", ID.LetterCounts);

  /**
   * LetterNumber(character) - returns the position of the `character` in the English alphabet.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterNumber.md">LetterNumber
   *     documentation</a>
   */
  public static final IBuiltInSymbol LetterNumber =
      initFinalSymbol("LetterNumber", ID.LetterNumber);

  /**
   * LetterQ(expr) - tests whether `expr` is a string, which only contains letters.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterQ.md">LetterQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol LetterQ = initFinalSymbol("LetterQ", ID.LetterQ);

  /**
   * Level(expr, levelspec) - gives a list of all sub-expressions of `expr` at the level(s)
   * specified by `levelspec`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Level.md">Level
   *     documentation</a>
   */
  public static final IBuiltInSymbol Level = initFinalSymbol("Level", ID.Level);

  /**
   * LevelQ(expr) - tests whether `expr` is a valid level specification.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LevelQ.md">LevelQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol LevelQ = initFinalSymbol("LevelQ", ID.LevelQ);

  /**
   * LeviCivitaTensor(n) - returns the `n`-dimensional Levi-Civita tensor as sparse array. The
   * Levi-Civita symbol represents a collection of numbers; defined from the sign of a permutation
   * of the natural numbers `1, 2, …, n`, for some positive integer `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeviCivitaTensor.md">LeviCivitaTensor
   *     documentation</a>
   */
  public static final IBuiltInSymbol LeviCivitaTensor =
      initFinalSymbol("LeviCivitaTensor", ID.LeviCivitaTensor);

  public static final IBuiltInSymbol Lexicographic =
      initFinalSymbol("Lexicographic", ID.Lexicographic);

  public static final IBuiltInSymbol LightBlue = initFinalSymbol("LightBlue", ID.LightBlue);

  public static final IBuiltInSymbol LightBrown = initFinalSymbol("LightBrown", ID.LightBrown);

  public static final IBuiltInSymbol LightCyan = initFinalSymbol("LightCyan", ID.LightCyan);

  public static final IBuiltInSymbol LightGray = initFinalSymbol("LightGray", ID.LightGray);

  public static final IBuiltInSymbol LightGreen = initFinalSymbol("LightGreen", ID.LightGreen);

  public static final IBuiltInSymbol LightMagenta =
      initFinalSymbol("LightMagenta", ID.LightMagenta);

  public static final IBuiltInSymbol LightOrange = initFinalSymbol("LightOrange", ID.LightOrange);

  public static final IBuiltInSymbol LightPink = initFinalSymbol("LightPink", ID.LightPink);

  public static final IBuiltInSymbol LightPurple = initFinalSymbol("LightPurple", ID.LightPurple);

  public static final IBuiltInSymbol LightRed = initFinalSymbol("LightRed", ID.LightRed);

  public static final IBuiltInSymbol LightYellow = initFinalSymbol("LightYellow", ID.LightYellow);

  public static final IBuiltInSymbol Lighting = initFinalSymbol("Lighting", ID.Lighting);

  /**
   * Limit(expr, x->x0) - gives the limit of `expr` as `x` approaches `x0`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Limit.md">Limit
   *     documentation</a>
   */
  public static final IBuiltInSymbol Limit = initFinalSymbol("Limit", ID.Limit);

  public static final IBuiltInSymbol Line = initFinalSymbol("Line", ID.Line);

  public static final IBuiltInSymbol LineGraph = initFinalSymbol("LineGraph", ID.LineGraph);

  /**
   * LinearModelFit(list-of-data-points, expr, symbol) - In statistics, linear regression is a
   * linear approach to modeling the relationship between a scalar response (or dependent variable)
   * and one or more explanatory variables (or independent variables).
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearModelFit.md">LinearModelFit
   *     documentation</a>
   */
  public static final IBuiltInSymbol LinearModelFit =
      initFinalSymbol("LinearModelFit", ID.LinearModelFit);

  /**
   * LinearProgramming(coefficientsOfLinearObjectiveFunction, constraintList,
   * constraintRelationList) - the `LinearProgramming` function provides an implementation of
   * [George Dantzig's simplex algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for
   * solving linear optimization problems with linear equality and inequality constraints and
   * implicit non-negative variables.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearProgramming.md">LinearProgramming
   *     documentation</a>
   */
  public static final IBuiltInSymbol LinearProgramming =
      initFinalSymbol("LinearProgramming", ID.LinearProgramming);

  /**
   * LinearRecurrence(list1, list2, n) - solve the linear recurrence and return the generated
   * sequence of elements.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearRecurrence.md">LinearRecurrence
   *     documentation</a>
   */
  public static final IBuiltInSymbol LinearRecurrence =
      initFinalSymbol("LinearRecurrence", ID.LinearRecurrence);

  /**
   * LinearSolve(matrix, right) - solves the linear equation system 'matrix . x = right' and returns
   * one corresponding solution `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearSolve.md">LinearSolve
   *     documentation</a>
   */
  public static final IBuiltInSymbol LinearSolve = initFinalSymbol("LinearSolve", ID.LinearSolve);

  public static final IBuiltInSymbol LinearSolveFunction =
      initFinalSymbol("LinearSolveFunction", ID.LinearSolveFunction);

  public static final IBuiltInSymbol LiouvilleLambda =
      initFinalSymbol("LiouvilleLambda", ID.LiouvilleLambda);

  /**
   * List(e1, e2, ..., ei) - represents a list containing the elements `e1...ei`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/List.md">List
   *     documentation</a>
   */
  public static final IBuiltInSymbol List = initFinalSymbol("List", ID.List);

  public static final IBuiltInSymbol ListContourPlot =
      initFinalSymbol("ListContourPlot", ID.ListContourPlot);

  /**
   * ListConvolve(kernel-list, tensor-list) - create the convolution of the `kernel-list` with
   * `tensor-list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListConvolve.md">ListConvolve
   *     documentation</a>
   */
  public static final IBuiltInSymbol ListConvolve =
      initFinalSymbol("ListConvolve", ID.ListConvolve);

  /**
   * ListCorrelate(kernel-list, tensor-list) - create the correlation of the `kernel-list` with
   * `tensor-list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListCorrelate.md">ListCorrelate
   *     documentation</a>
   */
  public static final IBuiltInSymbol ListCorrelate =
      initFinalSymbol("ListCorrelate", ID.ListCorrelate);

  /**
   * ListLinePlot( { list-of-points } ) - generate a JavaScript list line plot control for the
   * `list-of-points`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLinePlot.md">ListLinePlot
   *     documentation</a>
   */
  public static final IBuiltInSymbol ListLinePlot =
      initFinalSymbol("ListLinePlot", ID.ListLinePlot);

  /**
   * ListPlot( { list-of-points } ) - generate a JavaScript list plot control for the
   * `list-of-points`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListPlot.md">ListPlot
   *     documentation</a>
   */
  public static final IBuiltInSymbol ListPlot = initFinalSymbol("ListPlot", ID.ListPlot);

  /**
   * ListPlot3D( { list-of-points } ) - generate a JavaScript list plot 3D control for the
   * `list-of-points`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListPlot3D.md">ListPlot3D
   *     documentation</a>
   */
  public static final IBuiltInSymbol ListPlot3D = initFinalSymbol("ListPlot3D", ID.ListPlot3D);

  /**
   * ListQ(expr) - tests whether `expr` is a `List`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListQ.md">ListQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol ListQ = initFinalSymbol("ListQ", ID.ListQ);

  /**
   * Listable - is an attribute specifying that a function should be automatically applied to each
   * element of a list.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Listable.md">Listable
   *     documentation</a>
   */
  public static final IBuiltInSymbol Listable = initFinalSymbol("Listable", ID.Listable);

  public static final IBuiltInSymbol Literal = initFinalSymbol("Literal", ID.Literal);

  /**
   * LoadJavaClass["class-name"] - loads the class with the specified `class-name` and return a
   * `JavaClass` expression. All static method names are assigned to a context which will be created
   * by the last part of the class name.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LoadJavaClass.md">LoadJavaClass
   *     documentation</a>
   */
  public static final IBuiltInSymbol LoadJavaClass =
      initFinalSymbol("LoadJavaClass", ID.LoadJavaClass);

  /**
   * Log(z) - returns the natural logarithm of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log.md">Log
   *     documentation</a>
   */
  public static final IBuiltInSymbol Log = initFinalSymbol("Log", ID.Log);

  /**
   * Log10(z) - returns the base-`10` logarithm of `z`. `Log10(z)` will be converted to
   * `Log(z)/Log(10)` in symbolic mode.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log10.md">Log10
   *     documentation</a>
   */
  public static final IBuiltInSymbol Log10 = initFinalSymbol("Log10", ID.Log10);

  /**
   * Log2(z) - returns the base-`2` logarithm of `z`. `Log2(z)` will be converted to `Log(z)/Log(2)`
   * in symbolic mode.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log2.md">Log2
   *     documentation</a>
   */
  public static final IBuiltInSymbol Log2 = initFinalSymbol("Log2", ID.Log2);

  /**
   * LogGamma(z) - is the logarithmic gamma function on the complex number `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogGamma.md">LogGamma
   *     documentation</a>
   */
  public static final IBuiltInSymbol LogGamma = initFinalSymbol("LogGamma", ID.LogGamma);

  /**
   * LogIntegral(expr) - returns the integral logarithm of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogIntegral.md">LogIntegral
   *     documentation</a>
   */
  public static final IBuiltInSymbol LogIntegral = initFinalSymbol("LogIntegral", ID.LogIntegral);

  /**
   * LogNormalDistribution(m, s) - returns a log-normal distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogNormalDistribution.md">LogNormalDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol LogNormalDistribution =
      initFinalSymbol("LogNormalDistribution", ID.LogNormalDistribution);

  public static final IBuiltInSymbol LogicalExpand =
      initFinalSymbol("LogicalExpand", ID.LogicalExpand);

  /**
   * LogisticSigmoid(z) - returns the logistic sigmoid of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogisticSigmoid.md">LogisticSigmoid
   *     documentation</a>
   */
  public static final IBuiltInSymbol LogisticSigmoid =
      initFinalSymbol("LogisticSigmoid", ID.LogisticSigmoid);

  public static final IBuiltInSymbol LongForm = initFinalSymbol("LongForm", ID.LongForm);

  public static final IBuiltInSymbol Longest = initFinalSymbol("Longest", ID.Longest);

  /**
   * Lookup(association, key) - return the value in the `association` which is associated with the
   * `key`. If no value is available return `Missing("KeyAbsent",key)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Lookup.md">Lookup
   *     documentation</a>
   */
  public static final IBuiltInSymbol Lookup = initFinalSymbol("Lookup", ID.Lookup);

  /**
   * LowerCaseQ(str) - is `True` if the given `str` is a string which only contains lower case
   * characters.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerCaseQ.md">LowerCaseQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol LowerCaseQ = initFinalSymbol("LowerCaseQ", ID.LowerCaseQ);

  /**
   * LowerTriangularize(matrix) - create a lower triangular matrix from the given `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerTriangularize.md">LowerTriangularize
   *     documentation</a>
   */
  public static final IBuiltInSymbol LowerTriangularize =
      initFinalSymbol("LowerTriangularize", ID.LowerTriangularize);

  /**
   * LucasL(n) - gives the `n`th Lucas number.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LucasL.md">LucasL
   *     documentation</a>
   */
  public static final IBuiltInSymbol LucasL = initFinalSymbol("LucasL", ID.LucasL);

  /**
   * MachineNumberQ(expr) - returns `True` if `expr` is a machine-precision real or complex number.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MachineNumberQ.md">MachineNumberQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol MachineNumberQ =
      initFinalSymbol("MachineNumberQ", ID.MachineNumberQ);

  public static final IBuiltInSymbol Magenta = initFinalSymbol("Magenta", ID.Magenta);

  public static final IBuiltInSymbol MakeBoxes = initFinalSymbol("MakeBoxes", ID.MakeBoxes);

  /**
   * MangoldtLambda(n) - the von Mangoldt function of `n`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MangoldtLambda.md">MangoldtLambda
   *     documentation</a>
   */
  public static final IBuiltInSymbol MangoldtLambda =
      initFinalSymbol("MangoldtLambda", ID.MangoldtLambda);

  /**
   * ManhattanDistance(u, v) - returns the Manhattan distance between `u` and `v`, which is the
   * number of horizontal or vertical moves in the grid like Manhattan city layout to get from `u`
   * to `v`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ManhattanDistance.md">ManhattanDistance
   *     documentation</a>
   */
  public static final IBuiltInSymbol ManhattanDistance =
      initFinalSymbol("ManhattanDistance", ID.ManhattanDistance);

  /**
   * Manipulate(plot, {x, min, max}) - generate a JavaScript control for the expression `plot` which
   * can be manipulated by a range slider `{x, min, max}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Manipulate.md">Manipulate
   *     documentation</a>
   */
  public static final IBuiltInSymbol Manipulate = initFinalSymbol("Manipulate", ID.Manipulate);

  public static final IBuiltInSymbol MantissaExponent =
      initFinalSymbol("MantissaExponent", ID.MantissaExponent);

  /**
   * Map(f, expr) or f /@ expr - applies `f` to each part on the first level of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Map.md">Map
   *     documentation</a>
   */
  public static final IBuiltInSymbol Map = initFinalSymbol("Map", ID.Map);

  public static final IBuiltInSymbol MapAll = initFinalSymbol("MapAll", ID.MapAll);

  public static final IBuiltInSymbol MapAt = initFinalSymbol("MapAt", ID.MapAt);

  /**
   * MapIndexed(f, expr) - applies `f` to each part on the first level of `expr` and appending the
   * elements position as a list in the second argument.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MapIndexed.md">MapIndexed
   *     documentation</a>
   */
  public static final IBuiltInSymbol MapIndexed = initFinalSymbol("MapIndexed", ID.MapIndexed);

  /**
   * MapThread(f, {{a1, a2, ...}, {b1, b2, ...}, ...}) - returns `{f(a1, b1, ...), f(a2, b2, ...),
   * ...}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MapThread.md">MapThread
   *     documentation</a>
   */
  public static final IBuiltInSymbol MapThread = initFinalSymbol("MapThread", ID.MapThread);

  /**
   * MatchQ(expr, form) - tests whether `expr` matches `form`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatchQ.md">MatchQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol MatchQ = initFinalSymbol("MatchQ", ID.MatchQ);

  /**
   * MatchingDissimilarity(u, v) - returns the Matching dissimilarity between the two boolean 1-D
   * lists `u` and `v`, which is defined as `(c_tf + c_ft) / n`, where `n` is `len(u)` and `c_ij` is
   * the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatchingDissimilarity.md">MatchingDissimilarity
   *     documentation</a>
   */
  public static final IBuiltInSymbol MatchingDissimilarity =
      initFinalSymbol("MatchingDissimilarity", ID.MatchingDissimilarity);

  /**
   * MathMLForm(expr) - returns the MathML form of the evaluated `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MathMLForm.md">MathMLForm
   *     documentation</a>
   */
  public static final IBuiltInSymbol MathMLForm = initFinalSymbol("MathMLForm", ID.MathMLForm);

  public static final IBuiltInSymbol Matrices = initFinalSymbol("Matrices", ID.Matrices);

  /**
   * MatrixD(f, X) - gives the matrix derivative of `f` with respect to the matrix `X`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixD.md">MatrixD
   *     documentation</a>
   */
  public static final IBuiltInSymbol MatrixD = initFinalSymbol("MatrixD", ID.MatrixD);

  /**
   * MatrixExp(matrix) - computes the matrix exponential of the square `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixExp.md">MatrixExp
   *     documentation</a>
   */
  public static final IBuiltInSymbol MatrixExp = initFinalSymbol("MatrixExp", ID.MatrixExp);

  /**
   * MatrixForm(matrix) - print a `matrix` or sparse array in matrix form
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixForm.md">MatrixForm
   *     documentation</a>
   */
  public static final IBuiltInSymbol MatrixForm = initFinalSymbol("MatrixForm", ID.MatrixForm);

  public static final IBuiltInSymbol MatrixLog = initFinalSymbol("MatrixLog", ID.MatrixLog);

  /**
   * MatrixMinimalPolynomial(matrix, var) - computes the matrix minimal polynomial of a `matrix` for
   * the variable `var`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixMinimalPolynomial.md">MatrixMinimalPolynomial
   *     documentation</a>
   */
  public static final IBuiltInSymbol MatrixMinimalPolynomial =
      initFinalSymbol("MatrixMinimalPolynomial", ID.MatrixMinimalPolynomial);

  /**
   * MatrixPlot( matrix ) - create a matrix plot.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixPlot.md">MatrixPlot
   *     documentation</a>
   */
  public static final IBuiltInSymbol MatrixPlot = initFinalSymbol("MatrixPlot", ID.MatrixPlot);

  /**
   * MatrixPower(matrix, n) - computes the `n`th power of a `matrix`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixPower.md">MatrixPower
   *     documentation</a>
   */
  public static final IBuiltInSymbol MatrixPower = initFinalSymbol("MatrixPower", ID.MatrixPower);

  /**
   * MatrixQ(m) - returns `True` if `m` is a list of equal-length lists.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixQ.md">MatrixQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol MatrixQ = initFinalSymbol("MatrixQ", ID.MatrixQ);

  /**
   * MatrixRank(matrix) - returns the rank of `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixRank.md">MatrixRank
   *     documentation</a>
   */
  public static final IBuiltInSymbol MatrixRank = initFinalSymbol("MatrixRank", ID.MatrixRank);

  /**
   * Max(e_1, e_2, ..., e_i) - returns the expression with the greatest value among the `e_i`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Max.md">Max
   *     documentation</a>
   */
  public static final IBuiltInSymbol Max = initFinalSymbol("Max", ID.Max);

  /**
   * MaxFilter(list, r) - filter which evaluates the `Max` of `list` for the radius `r`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MaxFilter.md">MaxFilter
   *     documentation</a>
   */
  public static final IBuiltInSymbol MaxFilter = initFinalSymbol("MaxFilter", ID.MaxFilter);

  public static final IBuiltInSymbol MaxIterations =
      initFinalSymbol("MaxIterations", ID.MaxIterations);

  public static final IBuiltInSymbol MaxMemoryUsed =
      initFinalSymbol("MaxMemoryUsed", ID.MaxMemoryUsed);

  public static final IBuiltInSymbol MaxPoints = initFinalSymbol("MaxPoints", ID.MaxPoints);

  /**
   * Maximize(unary-function, variable) - returns the maximum of the unary function for the given
   * `variable`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Maximize.md">Maximize
   *     documentation</a>
   */
  public static final IBuiltInSymbol Maximize = initFinalSymbol("Maximize", ID.Maximize);

  /**
   * Mean(list) - returns the statistical mean of `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Mean.md">Mean
   *     documentation</a>
   */
  public static final IBuiltInSymbol Mean = initFinalSymbol("Mean", ID.Mean);

  public static final IBuiltInSymbol MeanDeviation =
      initFinalSymbol("MeanDeviation", ID.MeanDeviation);

  /**
   * MeanFilter(list, r) - filter which evaluates the `Mean` of `list` for the radius `r`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeanFilter.md">MeanFilter
   *     documentation</a>
   */
  public static final IBuiltInSymbol MeanFilter = initFinalSymbol("MeanFilter", ID.MeanFilter);

  /**
   * Median(list) - returns the median of `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Median.md">Median
   *     documentation</a>
   */
  public static final IBuiltInSymbol Median = initFinalSymbol("Median", ID.Median);

  /**
   * MedianFilter(list, r) - filter which evaluates the `Median` of `list` for the radius `r`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MedianFilter.md">MedianFilter
   *     documentation</a>
   */
  public static final IBuiltInSymbol MedianFilter =
      initFinalSymbol("MedianFilter", ID.MedianFilter);

  public static final IBuiltInSymbol MeijerG = initFinalSymbol("MeijerG", ID.MeijerG);

  /**
   * MemberQ(list, pattern) - returns `True` if pattern matches any element of `list`, or `False`
   * otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MemberQ.md">MemberQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol MemberQ = initFinalSymbol("MemberQ", ID.MemberQ);

  public static final IBuiltInSymbol MemoryAvailable =
      initFinalSymbol("MemoryAvailable", ID.MemoryAvailable);

  public static final IBuiltInSymbol MemoryInUse = initFinalSymbol("MemoryInUse", ID.MemoryInUse);

  /**
   * MersennePrimeExponent(n) - returns the `n`th mersenne prime exponent. `2^n - 1` must be a prime
   * number.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MersennePrimeExponent.md">MersennePrimeExponent
   *     documentation</a>
   */
  public static final IBuiltInSymbol MersennePrimeExponent =
      initFinalSymbol("MersennePrimeExponent", ID.MersennePrimeExponent);

  /**
   * MersennePrimeExponentQ(n) - returns `True` if `2^n - 1` is a prime number. Currently `0 <= n <=
   * 47` can be computed in reasonable time.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MersennePrimeExponentQ.md">MersennePrimeExponentQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol MersennePrimeExponentQ =
      initFinalSymbol("MersennePrimeExponentQ", ID.MersennePrimeExponentQ);

  public static final IBuiltInSymbol MeshRange = initFinalSymbol("MeshRange", ID.MeshRange);

  /**
   * Message(symbol::msg, expr1, expr2, ...) - displays the specified message, replacing
   * placeholders in the message text with the corresponding expressions.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Message.md">Message
   *     documentation</a>
   */
  public static final IBuiltInSymbol Message = initFinalSymbol("Message", ID.Message);

  /**
   * MessageName(symbol, msg) - `symbol::msg` identifies a message. `MessageName` is the head of
   * message IDs of the form `symbol::tag`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MessageName.md">MessageName
   *     documentation</a>
   */
  public static final IBuiltInSymbol MessageName = initFinalSymbol("MessageName", ID.MessageName);

  /**
   * Messages(symbol) - return all messages which are asociated to `symbol`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Messages.md">Messages
   *     documentation</a>
   */
  public static final IBuiltInSymbol Messages = initFinalSymbol("Messages", ID.Messages);

  public static final IBuiltInSymbol Method = initFinalSymbol("Method", ID.Method);

  /**
   * Min(e_1, e_2, ..., e_i) - returns the expression with the lowest value among the `e_i`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Min.md">Min
   *     documentation</a>
   */
  public static final IBuiltInSymbol Min = initFinalSymbol("Min", ID.Min);

  /**
   * MinFilter(list, r) - filter which evaluates the `Min` of `list` for the radius `r`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MinFilter.md">MinFilter
   *     documentation</a>
   */
  public static final IBuiltInSymbol MinFilter = initFinalSymbol("MinFilter", ID.MinFilter);

  public static final IBuiltInSymbol MinMax = initFinalSymbol("MinMax", ID.MinMax);

  public static final IBuiltInSymbol MinimalPolynomial =
      initFinalSymbol("MinimalPolynomial", ID.MinimalPolynomial);

  /**
   * Minimize(unary-function, variable) - returns the minimum of the unary function for the given
   * `variable`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Minimize.md">Minimize
   *     documentation</a>
   */
  public static final IBuiltInSymbol Minimize = initFinalSymbol("Minimize", ID.Minimize);

  /**
   * Minus(expr) - is the negation of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Minus.md">Minus
   *     documentation</a>
   */
  public static final IBuiltInSymbol Minus = initFinalSymbol("Minus", ID.Minus);

  public static final IBuiltInSymbol Missing = initFinalSymbol("Missing", ID.Missing);

  /**
   * MissingQ(expr) - returns `True` if `expr` is a `Missing()` expression.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MissingQ.md">MissingQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol MissingQ = initFinalSymbol("MissingQ", ID.MissingQ);

  /**
   * Mod(x, m) - returns `x` modulo `m`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Mod.md">Mod
   *     documentation</a>
   */
  public static final IBuiltInSymbol Mod = initFinalSymbol("Mod", ID.Mod);

  /**
   * Module({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables`
   * by renaming local variables.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Module.md">Module
   *     documentation</a>
   */
  public static final IBuiltInSymbol Module = initFinalSymbol("Module", ID.Module);

  public static final IBuiltInSymbol Modulus = initFinalSymbol("Modulus", ID.Modulus);

  /**
   * MoebiusMu(expr) - calculate the Möbius function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoebiusMu.md">MoebiusMu
   *     documentation</a>
   */
  public static final IBuiltInSymbol MoebiusMu = initFinalSymbol("MoebiusMu", ID.MoebiusMu);

  /**
   * MonomialList(polynomial, list-of-variables) - get the list of monomials of a `polynomial`
   * expression, with respect to the `list-of-variables`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MonomialList.md">MonomialList
   *     documentation</a>
   */
  public static final IBuiltInSymbol MonomialList =
      initFinalSymbol("MonomialList", ID.MonomialList);

  public static final IBuiltInSymbol MonomialOrder =
      initFinalSymbol("MonomialOrder", ID.MonomialOrder);

  /**
   * Most(expr) - returns `expr` with the last element removed.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Most.md">Most
   *     documentation</a>
   */
  public static final IBuiltInSymbol Most = initFinalSymbol("Most", ID.Most);

  /**
   * Multinomial(n1, n2, ...) - gives the multinomial coefficient `(n1+n2+...)!/(n1! n2! ...)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Multinomial.md">Multinomial
   *     documentation</a>
   */
  public static final IBuiltInSymbol Multinomial = initFinalSymbol("Multinomial", ID.Multinomial);

  /**
   * MultiplicativeOrder(a, n) - gives the multiplicative order `a` modulo `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MultiplicativeOrder.md">MultiplicativeOrder
   *     documentation</a>
   */
  public static final IBuiltInSymbol MultiplicativeOrder =
      initFinalSymbol("MultiplicativeOrder", ID.MultiplicativeOrder);

  public static final IBuiltInSymbol MultiplySides =
      initFinalSymbol("MultiplySides", ID.MultiplySides);

  /**
   * N(expr) - gives the numerical value of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/N.md">N
   *     documentation</a>
   */
  public static final IBuiltInSymbol N = initFinalSymbol("N", ID.N);

  /**
   * ND(function, x, value) - returns a numerical approximation of the partial derivative of the
   * `function` for the variable `x` and the given `value`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ND.md">ND
   *     documentation</a>
   */
  public static final IBuiltInSymbol ND = initFinalSymbol("ND", ID.ND);

  /**
   * NDSolve({equation-list}, functions, t) - attempts to solve the linear differential
   * `equation-list` for the `functions` and the time-dependent-variable `t`. Returns an
   * `InterpolatingFunction` function object.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NDSolve.md">NDSolve
   *     documentation</a>
   */
  public static final IBuiltInSymbol NDSolve = initFinalSymbol("NDSolve", ID.NDSolve);

  public static final IBuiltInSymbol NFourierTransform =
      initFinalSymbol("NFourierTransform", ID.NFourierTransform);

  /**
   * NHoldAll - is an attribute that protects all arguments of a function from numeric evaluation.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NHoldAll.md">NHoldAll
   *     documentation</a>
   */
  public static final IBuiltInSymbol NHoldAll = initFinalSymbol("NHoldAll", ID.NHoldAll);

  /**
   * NHoldFirst - is an attribute that protects the first argument of a function from numeric
   * evaluation.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NHoldFirst.md">NHoldFirst
   *     documentation</a>
   */
  public static final IBuiltInSymbol NHoldFirst = initFinalSymbol("NHoldFirst", ID.NHoldFirst);

  /**
   * NHoldRest - is an attribute that protects all but the first argument of a function from numeric
   * evaluation.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NHoldRest.md">NHoldRest
   *     documentation</a>
   */
  public static final IBuiltInSymbol NHoldRest = initFinalSymbol("NHoldRest", ID.NHoldRest);

  /**
   * NIntegrate(f, {x,a,b}) - computes the numerical univariate real integral of `f` with respect to
   * `x` from `a` to `b`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NIntegrate.md">NIntegrate
   *     documentation</a>
   */
  public static final IBuiltInSymbol NIntegrate = initFinalSymbol("NIntegrate", ID.NIntegrate);

  /**
   * NMaximize({maximize_function, constraints}, variables_list) - the `NMaximize` function provides
   * an implementation of [George Dantzig's simplex
   * algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear optimization
   * problems with linear equality and inequality constraints and implicit non-negative variables.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NMaximize.md">NMaximize
   *     documentation</a>
   */
  public static final IBuiltInSymbol NMaximize = initFinalSymbol("NMaximize", ID.NMaximize);

  /**
   * NMinimize({maximize_function, constraints}, variables_list) - the `NMinimize` function provides
   * an implementation of [George Dantzig's simplex
   * algorithm](http://en.wikipedia.org/wiki/Simplex_algorithm) for solving linear optimization
   * problems with linear equality and inequality constraints and implicit non-negative variables.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NMinimize.md">NMinimize
   *     documentation</a>
   */
  public static final IBuiltInSymbol NMinimize = initFinalSymbol("NMinimize", ID.NMinimize);

  /**
   * NRoots(polynomial==0) - gives the numerical roots of a univariate polynomial `polynomial`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NRoots.md">NRoots
   *     documentation</a>
   */
  public static final IBuiltInSymbol NRoots = initFinalSymbol("NRoots", ID.NRoots);

  /**
   * NSolve(equations, vars) - attempts to solve `equations` for the variables `vars`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NSolve.md">NSolve
   *     documentation</a>
   */
  public static final IBuiltInSymbol NSolve = initFinalSymbol("NSolve", ID.NSolve);

  /**
   * NakagamiDistribution(m, o) - returns a Nakagami distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NakagamiDistribution.md">NakagamiDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol NakagamiDistribution =
      initFinalSymbol("NakagamiDistribution", ID.NakagamiDistribution);

  /**
   * Names(string) - return the symbols from the context path matching the `string` or `pattern`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Names.md">Names
   *     documentation</a>
   */
  public static final IBuiltInSymbol Names = initFinalSymbol("Names", ID.Names);

  /**
   * Nand(arg1, arg2, ...) - Logical NAND function. It evaluates its arguments in order, giving
   * `True` immediately if any of them are `False`, and `False` if they are all `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nand.md">Nand
   *     documentation</a>
   */
  public static final IBuiltInSymbol Nand = initFinalSymbol("Nand", ID.Nand);

  public static final IBuiltInSymbol Nearest = initFinalSymbol("Nearest", ID.Nearest);

  public static final IBuiltInSymbol NearestTo = initFinalSymbol("NearestTo", ID.NearestTo);

  public static final IBuiltInSymbol Needs = initFinalSymbol("Needs", ID.Needs);

  /**
   * Negative(x) - returns `True` if `x` is a negative real number.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Negative.md">Negative
   *     documentation</a>
   */
  public static final IBuiltInSymbol Negative = initFinalSymbol("Negative", ID.Negative);

  public static final IBuiltInSymbol NegativeDegreeLexicographic =
      initFinalSymbol("NegativeDegreeLexicographic", ID.NegativeDegreeLexicographic);

  public static final IBuiltInSymbol NegativeDegreeReverseLexicographic =
      initFinalSymbol(
          "NegativeDegreeReverseLexicographic", ID.NegativeDegreeReverseLexicographic);

  public static final IBuiltInSymbol NegativeLexicographic =
      initFinalSymbol("NegativeLexicographic", ID.NegativeLexicographic);

  /**
   * Nest(f, expr, n) - starting with `expr`, iteratively applies `f` `n` times and returns the
   * final result.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nest.md">Nest
   *     documentation</a>
   */
  public static final IBuiltInSymbol Nest = initFinalSymbol("Nest", ID.Nest);

  /**
   * NestList(f, expr, n) - starting with `expr`, iteratively applies `f` `n` times and returns a
   * list of all intermediate results.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestList.md">NestList
   *     documentation</a>
   */
  public static final IBuiltInSymbol NestList = initFinalSymbol("NestList", ID.NestList);

  /**
   * NestWhile(f, expr, test) - applies a function `f` repeatedly on an expression `expr`, until
   * applying `test` on the result no longer yields `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestWhile.md">NestWhile
   *     documentation</a>
   */
  public static final IBuiltInSymbol NestWhile = initFinalSymbol("NestWhile", ID.NestWhile);

  /**
   * NestWhileList(f, expr, test) - applies a function `f` repeatedly on an expression `expr`, until
   * applying `test` on the result no longer yields `True`. It returns a list of all intermediate
   * results.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestWhileList.md">NestWhileList
   *     documentation</a>
   */
  public static final IBuiltInSymbol NestWhileList =
      initFinalSymbol("NestWhileList", ID.NestWhileList);

  /**
   * NextPrime(n) - gives the next prime after `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NextPrime.md">NextPrime
   *     documentation</a>
   */
  public static final IBuiltInSymbol NextPrime = initFinalSymbol("NextPrime", ID.NextPrime);

  public static final IBuiltInSymbol NonCommutativeMultiply =
      initFinalSymbol("NonCommutativeMultiply", ID.NonCommutativeMultiply);

  /**
   * NonNegative(x) - returns `True` if `x` is a positive real number or zero.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NonNegative.md">NonNegative
   *     documentation</a>
   */
  public static final IBuiltInSymbol NonNegative = initFinalSymbol("NonNegative", ID.NonNegative);

  /**
   * NonPositive(x) - returns `True` if `x` is a negative real number or zero.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NonPositive.md">NonPositive
   *     documentation</a>
   */
  public static final IBuiltInSymbol NonPositive = initFinalSymbol("NonPositive", ID.NonPositive);

  /**
   * None - is a possible value for `Span` and `Quiet`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/None.md">None
   *     documentation</a>
   */
  public static final IBuiltInSymbol None = initFinalSymbol("None", ID.None);

  /**
   * NoneTrue({expr1, expr2, ...}, test) - returns `True` if no application of `test` to `expr1,
   * expr2, ...` evaluates to `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NoneTrue.md">NoneTrue
   *     documentation</a>
   */
  public static final IBuiltInSymbol NoneTrue = initFinalSymbol("NoneTrue", ID.NoneTrue);

  public static final IBuiltInSymbol Nonexistent = initFinalSymbol("Nonexistent", ID.Nonexistent);

  /**
   * Nor(arg1, arg2, ...)' - Logical NOR function. It evaluates its arguments in order, giving
   * `False` immediately if any of them are `True`, and `True` if they are all `False`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nor.md">Nor
   *     documentation</a>
   */
  public static final IBuiltInSymbol Nor = initFinalSymbol("Nor", ID.Nor);

  /**
   * Norm(m, l) - computes the `l`-norm of matrix `m` (currently only works for vectors!).
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Norm.md">Norm
   *     documentation</a>
   */
  public static final IBuiltInSymbol Norm = initFinalSymbol("Norm", ID.Norm);

  /**
   * Normal(expr) - converts a Symja expression `expr` into a normal expression.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Normal.md">Normal
   *     documentation</a>
   */
  public static final IBuiltInSymbol Normal = initFinalSymbol("Normal", ID.Normal);

  /**
   * NormalDistribution(m, s) - returns the normal distribution of mean `m` and sigma `s`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NormalDistribution.md">NormalDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol NormalDistribution =
      initFinalSymbol("NormalDistribution", ID.NormalDistribution);

  /**
   * Normalize(v) - calculates the normalized vector `v` as `v/Norm(v)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Normalize.md">Normalize
   *     documentation</a>
   */
  public static final IBuiltInSymbol Normalize = initFinalSymbol("Normalize", ID.Normalize);

  /**
   * Not(expr) - Logical Not function (negation). Returns `True` if the statement is `False`.
   * Returns `False` if the `expr` is `True`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Not.md">Not
   *     documentation</a>
   */
  public static final IBuiltInSymbol Not = initFinalSymbol("Not", ID.Not);

  public static final IBuiltInSymbol NotApplicable =
      initFinalSymbol("NotApplicable", ID.NotApplicable);

  public static final IBuiltInSymbol NotAvailable =
      initFinalSymbol("NotAvailable", ID.NotAvailable);

  public static final IBuiltInSymbol NotElement = initFinalSymbol("NotElement", ID.NotElement);

  public static final IBuiltInSymbol NotListQ = initFinalSymbol("NotListQ", ID.NotListQ);

  public static final IBuiltInSymbol Nothing = initFinalSymbol("Nothing", ID.Nothing);

  public static final IBuiltInSymbol Now = initFinalSymbol("Now", ID.Now);

  /**
   * Null - is the implicit result of expressions that do not yield a result.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Null.md">Null
   *     documentation</a>
   */
  public static final IBuiltInSymbol Null = initFinalSymbol("Null", ID.Null);

  /**
   * NullSpace(matrix) - returns a list of vectors that span the nullspace of the `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NullSpace.md">NullSpace
   *     documentation</a>
   */
  public static final IBuiltInSymbol NullSpace = initFinalSymbol("NullSpace", ID.NullSpace);

  public static final IBuiltInSymbol Number = initFinalSymbol("Number", ID.Number);

  public static final IBuiltInSymbol NumberFieldRootsOfUnity =
      initFinalSymbol("NumberFieldRootsOfUnity", ID.NumberFieldRootsOfUnity);

  /**
   * NumberQ(expr) - returns `True` if `expr` is an explicit number, and `False` otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberQ.md">NumberQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol NumberQ = initFinalSymbol("NumberQ", ID.NumberQ);

  /**
   * NumberString - represents the characters in a number.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberString.md">NumberString
   *     documentation</a>
   */
  public static final IBuiltInSymbol NumberString =
      initFinalSymbol("NumberString", ID.NumberString);

  /**
   * Numerator(expr) - gives the numerator in `expr`. Numerator collects expressions with non
   * negative exponents.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Numerator.md">Numerator
   *     documentation</a>
   */
  public static final IBuiltInSymbol Numerator = initFinalSymbol("Numerator", ID.Numerator);

  public static final IBuiltInSymbol NumericArray =
      initFinalSymbol("NumericArray", ID.NumericArray);

  public static final IBuiltInSymbol NumericArrayQ =
      initFinalSymbol("NumericArrayQ", ID.NumericArrayQ);

  public static final IBuiltInSymbol NumericArrayType =
      initFinalSymbol("NumericArrayType", ID.NumericArrayType);

  public static final IBuiltInSymbol NumericFunction =
      initFinalSymbol("NumericFunction", ID.NumericFunction);

  /**
   * NumericQ(expr) - returns `True` if `expr` is an explicit numeric expression, and `False`
   * otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumericQ.md">NumericQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol NumericQ = initFinalSymbol("NumericQ", ID.NumericQ);

  public static final IBuiltInSymbol NuttallWindow =
      initFinalSymbol("NuttallWindow", ID.NuttallWindow);

  public static final IBuiltInSymbol O = initFinalSymbol("O", ID.O);

  public static final IBuiltInSymbol Octahedron = initFinalSymbol("Octahedron", ID.Octahedron);

  /**
   * OddQ(x) - returns `True` if `x` is odd, and `False` otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OddQ.md">OddQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol OddQ = initFinalSymbol("OddQ", ID.OddQ);

  /**
   * Off( ) - switch off the interactive trace.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Off.md">Off
   *     documentation</a>
   */
  public static final IBuiltInSymbol Off = initFinalSymbol("Off", ID.Off);

  /**
   * On( ) - switch on the interactive trace. The output is printed in the defined `out` stream.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/On.md">On
   *     documentation</a>
   */
  public static final IBuiltInSymbol On = initFinalSymbol("On", ID.On);

  /**
   * OneIdentity - is an attribute specifying that `f(x)` should be treated as equivalent to `x` in
   * pattern matching.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OneIdentity.md">OneIdentity
   *     documentation</a>
   */
  public static final IBuiltInSymbol OneIdentity = initFinalSymbol("OneIdentity", ID.OneIdentity);

  public static final IBuiltInSymbol Opacity = initFinalSymbol("Opacity", ID.Opacity);

  /**
   * OpenAppend("file-name") - opens a file and returns an OutputStream to which writes are
   * appended.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OpenAppend.md">OpenAppend
   *     documentation</a>
   */
  public static final IBuiltInSymbol OpenAppend = initFinalSymbol("OpenAppend", ID.OpenAppend);

  public static final IBuiltInSymbol OpenRead = initFinalSymbol("OpenRead", ID.OpenRead);

  /**
   * OpenWrite() - creates an empty file in the default temporary-file directory and returns an
   * OutputStream.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OpenWrite.md">OpenWrite
   *     documentation</a>
   */
  public static final IBuiltInSymbol OpenWrite = initFinalSymbol("OpenWrite", ID.OpenWrite);

  /**
   * Operate(p, expr) - applies `p` to the head of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Operate.md">Operate
   *     documentation</a>
   */
  public static final IBuiltInSymbol Operate = initFinalSymbol("Operate", ID.Operate);

  /**
   * OptimizeExpression(function) - common subexpressions elimination for a complicated `function`
   * by generating "dummy" variables for these subexpressions.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OptimizeExpression.md">OptimizeExpression
   *     documentation</a>
   */
  public static final IBuiltInSymbol OptimizeExpression =
      initFinalSymbol("OptimizeExpression", ID.OptimizeExpression);

  /**
   * OptionValue(name) - gives the value of the option `name` as specified in a call to a function
   * with `OptionsPattern`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OptionValue.md">OptionValue
   *     documentation</a>
   */
  public static final IBuiltInSymbol OptionValue = initFinalSymbol("OptionValue", ID.OptionValue);

  /**
   * Optional(patt, default) - is a pattern which matches `patt`, which if omitted should be
   * replaced by `default`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Optional.md">Optional
   *     documentation</a>
   */
  public static final IBuiltInSymbol Optional = initFinalSymbol("Optional", ID.Optional);

  /**
   * Options(symbol) - gives a list of optional arguments to `symbol` and their default values.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Options.md">Options
   *     documentation</a>
   */
  public static final IBuiltInSymbol Options = initFinalSymbol("Options", ID.Options);

  public static final IBuiltInSymbol OptionsPattern =
      initFinalSymbol("OptionsPattern", ID.OptionsPattern);

  /**
   * Or(expr1, expr2, ...)' - `expr1 || expr2 || ...` evaluates each expression in turn, returning
   * `True` as soon as an expression evaluates to `True`. If all expressions evaluate to `False`,
   * `Or` returns `False`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Or.md">Or
   *     documentation</a>
   */
  public static final IBuiltInSymbol Or = initFinalSymbol("Or", ID.Or);

  public static final IBuiltInSymbol Orange = initFinalSymbol("Orange", ID.Orange);

  /**
   * Order(a, b) - is `0` if `a` equals `b`. Is `-1` or `1` according to canonical order of `a` and
   * `b`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Order.md">Order
   *     documentation</a>
   */
  public static final IBuiltInSymbol Order = initFinalSymbol("Order", ID.Order);

  /**
   * OrderedQ({a, b}) - is `True` if `a` sorts before `b` according to canonical ordering.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OrderedQ.md">OrderedQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol OrderedQ = initFinalSymbol("OrderedQ", ID.OrderedQ);

  /**
   * Ordering(list) - calculate the permutation list of the elements in the sorted `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ordering.md">Ordering
   *     documentation</a>
   */
  public static final IBuiltInSymbol Ordering = initFinalSymbol("Ordering", ID.Ordering);

  /**
   * Orderless - is an attribute indicating that the leaves in an expression `f(a, b, c)` can be
   * placed in any order.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Orderless.md">Orderless
   *     documentation</a>
   */
  public static final IBuiltInSymbol Orderless = initFinalSymbol("Orderless", ID.Orderless);

  /**
   * OrthogonalMatrixQ(matrix) - returns `True`, if `matrix` is an orthogonal matrix. `False`
   * otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OrthogonalMatrixQ.md">OrthogonalMatrixQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol OrthogonalMatrixQ =
      initFinalSymbol("OrthogonalMatrixQ", ID.OrthogonalMatrixQ);

  /**
   * Orthogonalize(matrix) - returns a basis for the orthogonalized set of vectors defined by
   * `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Orthogonalize.md">Orthogonalize
   *     documentation</a>
   */
  public static final IBuiltInSymbol Orthogonalize =
      initFinalSymbol("Orthogonalize", ID.Orthogonalize);

  /**
   * Out(k) - gives the result of the `k`th input line.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Out.md">Out
   *     documentation</a>
   */
  public static final IBuiltInSymbol Out = initFinalSymbol("Out", ID.Out);

  /**
   * Outer(f, x, y) - computes a generalised outer product of `x` and `y`, using the function `f` in
   * place of multiplication.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Outer.md">Outer
   *     documentation</a>
   */
  public static final IBuiltInSymbol Outer = initFinalSymbol("Outer", ID.Outer);

  public static final IBuiltInSymbol OutputForm = initFinalSymbol("OutputForm", ID.OutputForm);

  /**
   * OutputStream("file-name") - opens a file and returns an OutputStream.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OutputStream.md">OutputStream
   *     documentation</a>
   */
  public static final IBuiltInSymbol OutputStream =
      initFinalSymbol("OutputStream", ID.OutputStream);

  public static final IBuiltInSymbol OverscriptBox =
      initFinalSymbol("OverscriptBox", ID.OverscriptBox);

  /**
   * OwnValues(symbol) - prints the own-value rule associated with `symbol`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OwnValues.md">OwnValues
   *     documentation</a>
   */
  public static final IBuiltInSymbol OwnValues = initFinalSymbol("OwnValues", ID.OwnValues);

  /**
   * PDF(distribution, value) - returns the probability density function of `value`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PDF.md">PDF
   *     documentation</a>
   */
  public static final IBuiltInSymbol PDF = initFinalSymbol("PDF", ID.PDF);

  public static final IBuiltInSymbol Package = initFinalSymbol("Package", ID.Package);

  /**
   * PadLeft(list, n) - pads `list` to length `n` by adding `0` on the left.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PadLeft.md">PadLeft
   *     documentation</a>
   */
  public static final IBuiltInSymbol PadLeft = initFinalSymbol("PadLeft", ID.PadLeft);

  /**
   * PadRight(list, n) - pads `list` to length `n` by adding `0` on the right.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PadRight.md">PadRight
   *     documentation</a>
   */
  public static final IBuiltInSymbol PadRight = initFinalSymbol("PadRight", ID.PadRight);

  /**
   * ParametricPlot({function1, function2}, {t, tMin, tMax}) - generate a JavaScript control for the
   * parametric expressions `function1`, `function2` in the `t` range `{t, tMin, tMax}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ParametricPlot.md">ParametricPlot
   *     documentation</a>
   */
  public static final IBuiltInSymbol ParametricPlot =
      initFinalSymbol("ParametricPlot", ID.ParametricPlot);

  /**
   * Parenthesis(expr) - print `expr` with parenthesis surrounded in output forms.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Parenthesis.md">Parenthesis
   *     documentation</a>
   */
  public static final IBuiltInSymbol Parenthesis = initFinalSymbol("Parenthesis", ID.Parenthesis);

  /**
   * Part(expr, i) - returns part `i` of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part
   *     documentation</a>
   */
  public static final IBuiltInSymbol Part = initFinalSymbol("Part", ID.Part);

  /**
   * Partition(list, n) - partitions `list` into sublists of length `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Partition.md">Partition
   *     documentation</a>
   */
  public static final IBuiltInSymbol Partition = initFinalSymbol("Partition", ID.Partition);

  /**
   * PartitionsP(n) - gives the number of unrestricted partitions of the integer `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PartitionsP.md">PartitionsP
   *     documentation</a>
   */
  public static final IBuiltInSymbol PartitionsP = initFinalSymbol("PartitionsP", ID.PartitionsP);

  /**
   * PartitionsQ(n) - gives the number of partitions of the integer `n` into distinct parts
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PartitionsQ.md">PartitionsQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol PartitionsQ = initFinalSymbol("PartitionsQ", ID.PartitionsQ);

  public static final IBuiltInSymbol ParzenWindow =
      initFinalSymbol("ParzenWindow", ID.ParzenWindow);

  public static final IBuiltInSymbol Pattern = initFinalSymbol("Pattern", ID.Pattern);

  public static final IBuiltInSymbol PatternOrder =
      initFinalSymbol("PatternOrder", ID.PatternOrder);

  /**
   * PatternTest(pattern, test) - constrains `pattern` to match `expr` only if the evaluation of
   * `test(expr)` yields `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PatternTest.md">PatternTest
   *     documentation</a>
   */
  public static final IBuiltInSymbol PatternTest = initFinalSymbol("PatternTest", ID.PatternTest);

  /**
   * PauliMatrix(n) - returns the Pauli `2x2` matrix for `n` between `0` and `4`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PauliMatrix.md">PauliMatrix
   *     documentation</a>
   */
  public static final IBuiltInSymbol PauliMatrix = initFinalSymbol("PauliMatrix", ID.PauliMatrix);

  /**
   * Pause(seconds) - pause the thread for the number of `seconds`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pause.md">Pause
   *     documentation</a>
   */
  public static final IBuiltInSymbol Pause = initFinalSymbol("Pause", ID.Pause);

  public static final IBuiltInSymbol PearsonChiSquareTest =
      initFinalSymbol("PearsonChiSquareTest", ID.PearsonChiSquareTest);

  /**
   * PerfectNumber(n) - returns the `n`th perfect number. In number theory, a perfect number is a
   * positive integer that is equal to the sum of its proper
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PerfectNumber.md">PerfectNumber
   *     documentation</a>
   */
  public static final IBuiltInSymbol PerfectNumber =
      initFinalSymbol("PerfectNumber", ID.PerfectNumber);

  /**
   * PerfectNumberQ(n) - returns `True` if `n` is a perfect number. In number theory, a perfect
   * number is a positive integer that is equal to the sum of its proper
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PerfectNumberQ.md">PerfectNumberQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol PerfectNumberQ =
      initFinalSymbol("PerfectNumberQ", ID.PerfectNumberQ);

  public static final IBuiltInSymbol Perimeter = initFinalSymbol("Perimeter", ID.Perimeter);

  /**
   * PermutationCycles(permutation-list) - generate a `Cycles({{...},{...}, ...})` expression from
   * the `permutation-list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationCycles.md">PermutationCycles
   *     documentation</a>
   */
  public static final IBuiltInSymbol PermutationCycles =
      initFinalSymbol("PermutationCycles", ID.PermutationCycles);

  /**
   * PermutationCyclesQ(cycles-expression) - if `cycles-expression` is a valid `Cycles({{...},{...},
   * ...})` expression return `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationCyclesQ.md">PermutationCyclesQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol PermutationCyclesQ =
      initFinalSymbol("PermutationCyclesQ", ID.PermutationCyclesQ);

  /**
   * PermutationList(Cycles({{...},{...}, ...})) - get the permutation list representation from the
   * `Cycles({{...},{...}, ...})` expression.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationList.md">PermutationList
   *     documentation</a>
   */
  public static final IBuiltInSymbol PermutationList =
      initFinalSymbol("PermutationList", ID.PermutationList);

  /**
   * PermutationListQ(permutation-list) - if `permutation-list` is a valid permutation list return
   * `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationListQ.md">PermutationListQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol PermutationListQ =
      initFinalSymbol("PermutationListQ", ID.PermutationListQ);

  /**
   * PermutationReplace(list-or-integer, Cycles({{...},{...}, ...})) - replace the arguments of the
   * first expression with the corresponding element from the `Cycles({{...},{...}, ...})`
   * expression.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationReplace.md">PermutationReplace
   *     documentation</a>
   */
  public static final IBuiltInSymbol PermutationReplace =
      initFinalSymbol("PermutationReplace", ID.PermutationReplace);

  /**
   * Permutations(list) - gives all possible orderings of the items in `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Permutations.md">Permutations
   *     documentation</a>
   */
  public static final IBuiltInSymbol Permutations =
      initFinalSymbol("Permutations", ID.Permutations);

  /**
   * Permute(list, Cycles({permutation-cycles})) - permutes the `list` from the cycles in
   * `permutation-cycles`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Permute.md">Permute
   *     documentation</a>
   */
  public static final IBuiltInSymbol Permute = initFinalSymbol("Permute", ID.Permute);

  /**
   * PetersenGraph() - create a `PetersenGraph(5, 2)` graph.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PetersenGraph.md">PetersenGraph
   *     documentation</a>
   */
  public static final IBuiltInSymbol PetersenGraph =
      initFinalSymbol("PetersenGraph", ID.PetersenGraph);

  /**
   * Pi - is the constant `Pi`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pi.md">Pi
   *     documentation</a>
   */
  public static final IBuiltInSymbol Pi = initFinalSymbol("Pi", ID.Pi);

  /**
   * Pick(nested-list, nested-selection) - returns the elements of `nested-list` that have value
   * `True` in the corresponding position in `nested-selection`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pick.md">Pick
   *     documentation</a>
   */
  public static final IBuiltInSymbol Pick = initFinalSymbol("Pick", ID.Pick);

  /**
   * PieChart(list-of-values) - plot a pie chart from a `list-of-values`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PieChart.md">PieChart
   *     documentation</a>
   */
  public static final IBuiltInSymbol PieChart = initFinalSymbol("PieChart", ID.PieChart);

  /**
   * Piecewise({{expr1, cond1}, ...}) - represents a piecewise function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Piecewise.md">Piecewise
   *     documentation</a>
   */
  public static final IBuiltInSymbol Piecewise = initFinalSymbol("Piecewise", ID.Piecewise);

  /**
   * PiecewiseExpand(function) - expands piecewise expressions into a `Piecewise` function.
   * Currently only `Abs, Clip, If, Ramp, UnitStep` are converted to Piecewise expressions.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PiecewiseExpand.md">PiecewiseExpand
   *     documentation</a>
   */
  public static final IBuiltInSymbol PiecewiseExpand =
      initFinalSymbol("PiecewiseExpand", ID.PiecewiseExpand);

  public static final IBuiltInSymbol Pink = initFinalSymbol("Pink", ID.Pink);

  public static final IBuiltInSymbol PlanarGraph = initFinalSymbol("PlanarGraph", ID.PlanarGraph);

  public static final IBuiltInSymbol PlanarGraphQ =
      initFinalSymbol("PlanarGraphQ", ID.PlanarGraphQ);

  /**
   * Plot(function, {x, xMin, xMax}, PlotRange->{yMin,yMax}) - generate a JavaScript control for the
   * expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plot.md">Plot
   *     documentation</a>
   */
  public static final IBuiltInSymbol Plot = initFinalSymbol("Plot", ID.Plot);

  /**
   * Plot3D(function, {x, xMin, xMax}, {y,yMin,yMax}) - generate a JavaScript control for the
   * expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plot3D.md">Plot3D
   *     documentation</a>
   */
  public static final IBuiltInSymbol Plot3D = initFinalSymbol("Plot3D", ID.Plot3D);

  public static final IBuiltInSymbol PlotRange = initFinalSymbol("PlotRange", ID.PlotRange);

  public static final IBuiltInSymbol PlotStyle = initFinalSymbol("PlotStyle", ID.PlotStyle);

  /**
   * Plus(a, b, ...) - represents the sum of the terms `a, b, ...`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus
   *     documentation</a>
   */
  public static final IBuiltInSymbol Plus = initFinalSymbol("Plus", ID.Plus);

  /**
   * Pochhammer(a, n) - returns the pochhammer symbol for a rational number `a` and an integer
   * number `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pochhammer.md">Pochhammer
   *     documentation</a>
   */
  public static final IBuiltInSymbol Pochhammer = initFinalSymbol("Pochhammer", ID.Pochhammer);

  /**
   * Point({point_1, point_2 ...}) - represents the point primitive.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Point.md">Point
   *     documentation</a>
   */
  public static final IBuiltInSymbol Point = initFinalSymbol("Point", ID.Point);

  /**
   * PoissonDistribution(m) - returns a Poisson distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PoissonDistribution.md">PoissonDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol PoissonDistribution =
      initFinalSymbol("PoissonDistribution", ID.PoissonDistribution);

  /**
   * PolarPlot(function, {t, tMin, tMax}) - generate a JavaScript control for the polar plot
   * expressions `function` in the `t` range `{t, tMin, tMax}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolarPlot.md">PolarPlot
   *     documentation</a>
   */
  public static final IBuiltInSymbol PolarPlot = initFinalSymbol("PolarPlot", ID.PolarPlot);

  /**
   * PolyGamma(value) - return the digamma function of the `value`. The digamma function is defined
   * as the logarithmic derivative of the gamma function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolyGamma.md">PolyGamma
   *     documentation</a>
   */
  public static final IBuiltInSymbol PolyGamma = initFinalSymbol("PolyGamma", ID.PolyGamma);

  public static final IBuiltInSymbol PolyLog = initFinalSymbol("PolyLog", ID.PolyLog);

  /**
   * Polygon({point_1, point_2 ...}) - represents the filled polygon primitive.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Polygon.md">Polygon
   *     documentation</a>
   */
  public static final IBuiltInSymbol Polygon = initFinalSymbol("Polygon", ID.Polygon);

  public static final IBuiltInSymbol Polyhedron = initFinalSymbol("Polyhedron", ID.Polyhedron);

  /**
   * PolynomialExtendedGCD(p, q, x) - returns the extended GCD ('greatest common divisor') of the
   * univariate polynomials `p` and `q`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialExtendedGCD.md">PolynomialExtendedGCD
   *     documentation</a>
   */
  public static final IBuiltInSymbol PolynomialExtendedGCD =
      initFinalSymbol("PolynomialExtendedGCD", ID.PolynomialExtendedGCD);

  /**
   * PolynomialGCD(p, q) - returns the GCD ('greatest common divisor') of the polynomials `p` and
   * `q`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialGCD.md">PolynomialGCD
   *     documentation</a>
   */
  public static final IBuiltInSymbol PolynomialGCD =
      initFinalSymbol("PolynomialGCD", ID.PolynomialGCD);

  /**
   * PolynomialLCM(p, q) - returns the LCM ('least common multiple') of the polynomials `p` and `q`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialLCM.md">PolynomialLCM
   *     documentation</a>
   */
  public static final IBuiltInSymbol PolynomialLCM =
      initFinalSymbol("PolynomialLCM", ID.PolynomialLCM);

  /**
   * PolynomialQ(p, x) - return `True` if `p` is a polynomial for the variable `x`. Return `False`
   * in all other cases.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQ.md">PolynomialQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol PolynomialQ = initFinalSymbol("PolynomialQ", ID.PolynomialQ);

  /**
   * PolynomialQuotient(p, q, x) - returns the polynomial quotient of the polynomials `p` and `q`
   * for the variable `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQuotient.md">PolynomialQuotient
   *     documentation</a>
   */
  public static final IBuiltInSymbol PolynomialQuotient =
      initFinalSymbol("PolynomialQuotient", ID.PolynomialQuotient);

  /**
   * PolynomialQuotientRemainder(p, q, x) - returns a list with the polynomial quotient and
   * remainder of the polynomials `p` and `q` for the variable `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQuotientRemainder.md">PolynomialQuotientRemainder
   *     documentation</a>
   */
  public static final IBuiltInSymbol PolynomialQuotientRemainder =
      initFinalSymbol("PolynomialQuotientRemainder", ID.PolynomialQuotientRemainder);

  /**
   * PolynomialQuotient(p, q, x) - returns the polynomial remainder of the polynomials `p` and `q`
   * for the variable `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialRemainder.md">PolynomialRemainder
   *     documentation</a>
   */
  public static final IBuiltInSymbol PolynomialRemainder =
      initFinalSymbol("PolynomialRemainder", ID.PolynomialRemainder);

  /**
   * Position(expr, patt) - returns the list of positions for which `expr` matches `patt`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Position.md">Position
   *     documentation</a>
   */
  public static final IBuiltInSymbol Position = initFinalSymbol("Position", ID.Position);

  /**
   * Positive(x) - returns `True` if `x` is a positive real number.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Positive.md">Positive
   *     documentation</a>
   */
  public static final IBuiltInSymbol Positive = initFinalSymbol("Positive", ID.Positive);

  /**
   * PossibleZeroQ(expr) - returns `True` if basic symbolic and numerical methods suggests that
   * `expr` has value zero, and `False` otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PossibleZeroQ.md">PossibleZeroQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol PossibleZeroQ =
      initFinalSymbol("PossibleZeroQ", ID.PossibleZeroQ);

  public static final IBuiltInSymbol Postefix = initFinalSymbol("Postefix", ID.Postefix);

  /**
   * Power(a, b) - represents `a` raised to the power of `b`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Power.md">Power
   *     documentation</a>
   */
  public static final IBuiltInSymbol Power = initFinalSymbol("Power", ID.Power);

  /**
   * PowerExpand(expr) - expands out powers of the form `(x^y)^z` and `(x*y)^z` in `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PowerExpand.md">PowerExpand
   *     documentation</a>
   */
  public static final IBuiltInSymbol PowerExpand = initFinalSymbol("PowerExpand", ID.PowerExpand);

  /**
   * PowerMod(x, y, m) - computes `x^y` modulo `m`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PowerMod.md">PowerMod
   *     documentation</a>
   */
  public static final IBuiltInSymbol PowerMod = initFinalSymbol("PowerMod", ID.PowerMod);

  /**
   * PreDecrement(x) - decrements `x` by `1`, returning the new value of `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PreDecrement.md">PreDecrement
   *     documentation</a>
   */
  public static final IBuiltInSymbol PreDecrement =
      initFinalSymbol("PreDecrement", ID.PreDecrement);

  /**
   * PreIncrement(x) - increments `x` by `1`, returning the new value of `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PreIncrement.md">PreIncrement
   *     documentation</a>
   */
  public static final IBuiltInSymbol PreIncrement =
      initFinalSymbol("PreIncrement", ID.PreIncrement);

  public static final IBuiltInSymbol Precision = initFinalSymbol("Precision", ID.Precision);

  public static final IBuiltInSymbol PrecisionGoal =
      initFinalSymbol("PrecisionGoal", ID.PrecisionGoal);

  public static final IBuiltInSymbol Prefix = initFinalSymbol("Prefix", ID.Prefix);

  /**
   * Prepend(expr, item) - returns `expr` with `item` prepended to its leaves.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Prepend.md">Prepend
   *     documentation</a>
   */
  public static final IBuiltInSymbol Prepend = initFinalSymbol("Prepend", ID.Prepend);

  /**
   * PrependTo(s, item) - prepend `item` to value of `s` and sets `s` to the result.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrependTo.md">PrependTo
   *     documentation</a>
   */
  public static final IBuiltInSymbol PrependTo = initFinalSymbol("PrependTo", ID.PrependTo);

  /**
   * Prime(n) - returns the `n`th prime number.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Prime.md">Prime
   *     documentation</a>
   */
  public static final IBuiltInSymbol Prime = initFinalSymbol("Prime", ID.Prime);

  /**
   * PrimeOmega(n) - returns the sum of the exponents of the prime factorization of `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimeOmega.md">PrimeOmega
   *     documentation</a>
   */
  public static final IBuiltInSymbol PrimeOmega = initFinalSymbol("PrimeOmega", ID.PrimeOmega);

  /**
   * PrimePi(x) - gives the number of primes less than or equal to `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimePi.md">PrimePi
   *     documentation</a>
   */
  public static final IBuiltInSymbol PrimePi = initFinalSymbol("PrimePi", ID.PrimePi);

  /**
   * PrimePowerQ(n) - returns `True` if `n` is a power of a prime number.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimePowerQ.md">PrimePowerQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol PrimePowerQ = initFinalSymbol("PrimePowerQ", ID.PrimePowerQ);

  /**
   * PrimeQ(n) - returns `True` if `n` is a integer prime number.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimeQ.md">PrimeQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol PrimeQ = initFinalSymbol("PrimeQ", ID.PrimeQ);

  public static final IBuiltInSymbol Primes = initFinalSymbol("Primes", ID.Primes);

  public static final IBuiltInSymbol PrimitiveRoot =
      initFinalSymbol("PrimitiveRoot", ID.PrimitiveRoot);

  /**
   * PrimitiveRootList(n) - returns the list of the primitive roots of `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimitiveRootList.md">PrimitiveRootList
   *     documentation</a>
   */
  public static final IBuiltInSymbol PrimitiveRootList =
      initFinalSymbol("PrimitiveRootList", ID.PrimitiveRootList);

  /**
   * Print(expr) - print the `expr` to the default output stream and return `Null`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Print.md">Print
   *     documentation</a>
   */
  public static final IBuiltInSymbol Print = initFinalSymbol("Print", ID.Print);

  /**
   * PrintableASCIIQ(str) - returns `True` if all characters in `str` are ASCII characters.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrintableASCIIQ.md">PrintableASCIIQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol PrintableASCIIQ =
      initFinalSymbol("PrintableASCIIQ", ID.PrintableASCIIQ);

  public static final IBuiltInSymbol Prism = initFinalSymbol("Prism", ID.Prism);

  /**
   * Probability(pure-function, data-set) - returns the probability of the `pure-function` for the
   * given `data-set`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Probability.md">Probability
   *     documentation</a>
   */
  public static final IBuiltInSymbol Probability = initFinalSymbol("Probability", ID.Probability);

  /**
   * Product(expr, {i, imin, imax}) - evaluates the discrete product of `expr` with `i` ranging from
   * `imin` to `imax`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Product.md">Product
   *     documentation</a>
   */
  public static final IBuiltInSymbol Product = initFinalSymbol("Product", ID.Product);

  /**
   * ProductLog(z) - returns the value of the Lambert W function at `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ProductLog.md">ProductLog
   *     documentation</a>
   */
  public static final IBuiltInSymbol ProductLog = initFinalSymbol("ProductLog", ID.ProductLog);

  /**
   * Projection(vector1, vector2) - Find the orthogonal projection of `vector1` onto another
   * `vector2`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Projection.md">Projection
   *     documentation</a>
   */
  public static final IBuiltInSymbol Projection = initFinalSymbol("Projection", ID.Projection);

  public static final IBuiltInSymbol Protect = initFinalSymbol("Protect", ID.Protect);

  public static final IBuiltInSymbol Protected = initFinalSymbol("Protected", ID.Protected);

  /**
   * PseudoInverse(matrix) - computes the Moore-Penrose pseudoinverse of the `matrix`. If `matrix`
   * is invertible, the pseudoinverse equals the inverse.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PseudoInverse.md">PseudoInverse
   *     documentation</a>
   */
  public static final IBuiltInSymbol PseudoInverse =
      initFinalSymbol("PseudoInverse", ID.PseudoInverse);

  public static final IBuiltInSymbol Purple = initFinalSymbol("Purple", ID.Purple);

  public static final IBuiltInSymbol Put = initFinalSymbol("Put", ID.Put);

  public static final IBuiltInSymbol Pyramid = initFinalSymbol("Pyramid", ID.Pyramid);

  /**
   * QRDecomposition(A) - computes the QR decomposition of the matrix `A`. The QR decomposition is a
   * decomposition of a matrix `A` into a product `A = Q.R` of an unitary matrix `Q` and an upper
   * triangular matrix `R`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QRDecomposition.md">QRDecomposition
   *     documentation</a>
   */
  public static final IBuiltInSymbol QRDecomposition =
      initFinalSymbol("QRDecomposition", ID.QRDecomposition);

  /**
   * QuadraticIrrationalQ(expr) - returns `True`, if the `expr` is of the form `(p + s * Sqrt(d)) /
   * q` for integers `p,q,d,s`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuadraticIrrationalQ.md">QuadraticIrrationalQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol QuadraticIrrationalQ =
      initFinalSymbol("QuadraticIrrationalQ", ID.QuadraticIrrationalQ);

  /**
   * Quantile(list, q) - returns the `q`-Quantile of `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantile.md">Quantile
   *     documentation</a>
   */
  public static final IBuiltInSymbol Quantile = initFinalSymbol("Quantile", ID.Quantile);

  /**
   * Quantity(value, unit) - returns the quantity for `value` and `unit`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantity.md">Quantity
   *     documentation</a>
   */
  public static final IBuiltInSymbol Quantity = initFinalSymbol("Quantity", ID.Quantity);

  public static final IBuiltInSymbol QuantityDistribution =
      initFinalSymbol("QuantityDistribution", ID.QuantityDistribution);

  /**
   * QuantityMagnitude(quantity) - returns the value of the `quantity`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityMagnitude.md">QuantityMagnitude
   *     documentation</a>
   */
  public static final IBuiltInSymbol QuantityMagnitude =
      initFinalSymbol("QuantityMagnitude", ID.QuantityMagnitude);

  public static final IBuiltInSymbol QuantityQ = initFinalSymbol("QuantityQ", ID.QuantityQ);

  public static final IBuiltInSymbol QuarticSolve =
      initFinalSymbol("QuarticSolve", ID.QuarticSolve);

  /**
   * Quartiles(arg) - returns a list of the `1/4`, `1/2` and `3/4` quantile of `arg`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quartiles.md">Quartiles
   *     documentation</a>
   */
  public static final IBuiltInSymbol Quartiles = initFinalSymbol("Quartiles", ID.Quartiles);

  /**
   * Quiet(expr) - evaluates `expr` in "quiet" mode (i.e. no warning messages are shown during
   * evaluation).
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quiet.md">Quiet
   *     documentation</a>
   */
  public static final IBuiltInSymbol Quiet = initFinalSymbol("Quiet", ID.Quiet);

  public static final IBuiltInSymbol Quit = initFinalSymbol("Quit", ID.Quit);

  /**
   * Quotient(m, n) - computes the integer quotient of `m` and `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quotient.md">Quotient
   *     documentation</a>
   */
  public static final IBuiltInSymbol Quotient = initFinalSymbol("Quotient", ID.Quotient);

  /**
   * QuotientRemainder(m, n) - computes a list of the quotient and remainder from division of `m`
   * and `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuotientRemainder.md">QuotientRemainder
   *     documentation</a>
   */
  public static final IBuiltInSymbol QuotientRemainder =
      initFinalSymbol("QuotientRemainder", ID.QuotientRemainder);

  public static final IBuiltInSymbol RGBColor = initFinalSymbol("RGBColor", ID.RGBColor);

  public static final IBuiltInSymbol RSolve = initFinalSymbol("RSolve", ID.RSolve);

  public static final IBuiltInSymbol RSolveValue = initFinalSymbol("RSolveValue", ID.RSolveValue);

  /**
   * Ramp(z) - The `Ramp` function is a unary real function, whose graph is shaped like a ramp.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ramp.md">Ramp
   *     documentation</a>
   */
  public static final IBuiltInSymbol Ramp = initFinalSymbol("Ramp", ID.Ramp);

  /**
   * RandomChoice({item1, item2, item3,...}) - randomly picks one `item` from items.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomChoice.md">RandomChoice
   *     documentation</a>
   */
  public static final IBuiltInSymbol RandomChoice =
      initFinalSymbol("RandomChoice", ID.RandomChoice);

  /**
   * RandomComplex[{z_min, z_max}] - yields a pseudo-random complex number in the rectangle with
   * complex corners `z_min` and `z_max`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomComplex.md">RandomComplex
   *     documentation</a>
   */
  public static final IBuiltInSymbol RandomComplex =
      initFinalSymbol("RandomComplex", ID.RandomComplex);

  public static final IBuiltInSymbol RandomGraph = initFinalSymbol("RandomGraph", ID.RandomGraph);

  /**
   * RandomInteger(n) - create a random integer number between `0` and `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomInteger.md">RandomInteger
   *     documentation</a>
   */
  public static final IBuiltInSymbol RandomInteger =
      initFinalSymbol("RandomInteger", ID.RandomInteger);

  /**
   * RandomPermutation(s) - create a pseudo random permutation between `1` and `s`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomPermutation.md">RandomPermutation
   *     documentation</a>
   */
  public static final IBuiltInSymbol RandomPermutation =
      initFinalSymbol("RandomPermutation", ID.RandomPermutation);

  /**
   * RandomPrime({imin, imax}) - create a random prime integer number between `imin` and `imax`
   * inclusive.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomPrime.md">RandomPrime
   *     documentation</a>
   */
  public static final IBuiltInSymbol RandomPrime = initFinalSymbol("RandomPrime", ID.RandomPrime);

  /**
   * RandomReal() - create a random number between `0.0` and `1.0`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomReal.md">RandomReal
   *     documentation</a>
   */
  public static final IBuiltInSymbol RandomReal = initFinalSymbol("RandomReal", ID.RandomReal);

  /**
   * RandomSample(items) - create a random sample for the arguments of the `items`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomSample.md">RandomSample
   *     documentation</a>
   */
  public static final IBuiltInSymbol RandomSample =
      initFinalSymbol("RandomSample", ID.RandomSample);

  public static final IBuiltInSymbol RandomVariate =
      initFinalSymbol("RandomVariate", ID.RandomVariate);

  /**
   * Range(n) - returns a list of integers from `1` to `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Range.md">Range
   *     documentation</a>
   */
  public static final IBuiltInSymbol Range = initFinalSymbol("Range", ID.Range);

  /**
   * RankedMax({e_1, e_2, ..., e_i}, n) - returns the n-th largest real value in the list `{e_1,
   * e_2, ..., e_i}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RankedMax.md">RankedMax
   *     documentation</a>
   */
  public static final IBuiltInSymbol RankedMax = initFinalSymbol("RankedMax", ID.RankedMax);

  /**
   * RankedMin({e_1, e_2, ..., e_i}, n) - returns the n-th smallest real value in the list `{e_1,
   * e_2, ..., e_i}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RankedMin.md">RankedMin
   *     documentation</a>
   */
  public static final IBuiltInSymbol RankedMin = initFinalSymbol("RankedMin", ID.RankedMin);

  /**
   * Rational - is the head of rational numbers.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rational.md">Rational
   *     documentation</a>
   */
  public static final IBuiltInSymbol Rational = initFinalSymbol("Rational", ID.Rational);

  /**
   * Rationalize(expression) - convert numerical real or imaginary parts in (sub-)expressions into
   * rational numbers.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rationalize.md">Rationalize
   *     documentation</a>
   */
  public static final IBuiltInSymbol Rationalize = initFinalSymbol("Rationalize", ID.Rationalize);

  public static final IBuiltInSymbol Rationals = initFinalSymbol("Rationals", ID.Rationals);

  public static final IBuiltInSymbol RawBoxes = initFinalSymbol("RawBoxes", ID.RawBoxes);

  /**
   * Re(z) - returns the real component of the complex number `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Re.md">Re
   *     documentation</a>
   */
  public static final IBuiltInSymbol Re = initFinalSymbol("Re", ID.Re);

  /**
   * Read(input-stream) - reads the `input-stream` and return one expression.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Read.md">Read
   *     documentation</a>
   */
  public static final IBuiltInSymbol Read = initFinalSymbol("Read", ID.Read);

  public static final IBuiltInSymbol ReadList = initFinalSymbol("ReadList", ID.ReadList);

  public static final IBuiltInSymbol ReadProtected =
      initFinalSymbol("ReadProtected", ID.ReadProtected);

  public static final IBuiltInSymbol ReadString = initFinalSymbol("ReadString", ID.ReadString);

  /**
   * Real - is the head of real (floating point) numbers.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Real.md">Real
   *     documentation</a>
   */
  public static final IBuiltInSymbol Real = initFinalSymbol("Real", ID.Real);

  /**
   * RealAbs(x) - returns the absolute value of the real number `x`. For complex number arguments
   * the function will be left unevaluated.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RealAbs.md">RealAbs
   *     documentation</a>
   */
  public static final IBuiltInSymbol RealAbs = initFinalSymbol("RealAbs", ID.RealAbs);

  public static final IBuiltInSymbol RealDigits = initFinalSymbol("RealDigits", ID.RealDigits);

  /**
   * RealNumberQ(expr) - returns `True` if `expr` is an explicit number with no imaginary component.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RealNumberQ.md">RealNumberQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol RealNumberQ = initFinalSymbol("RealNumberQ", ID.RealNumberQ);

  /**
   * RealSign(x) - gives `-1`, `0` or `1` depending on whether `x` is negative, zero or positive.
   * For complex number arguments the function will be left unevaluated.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RealSign.md">RealSign
   *     documentation</a>
   */
  public static final IBuiltInSymbol RealSign = initFinalSymbol("RealSign", ID.RealSign);

  /**
   * Reals - is the set of real numbers.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reals.md">Reals
   *     documentation</a>
   */
  public static final IBuiltInSymbol Reals = initFinalSymbol("Reals", ID.Reals);

  /**
   * Reap(expr) - gives the result of evaluating `expr`, together with all values sown during this
   * evaluation. Values sown with different tags are given in different lists.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reap.md">Reap
   *     documentation</a>
   */
  public static final IBuiltInSymbol Reap = initFinalSymbol("Reap", ID.Reap);

  public static final IBuiltInSymbol Record = initFinalSymbol("Record", ID.Record);

  public static final IBuiltInSymbol RecordSeparators =
      initFinalSymbol("RecordSeparators", ID.RecordSeparators);

  public static final IBuiltInSymbol Rectangle = initFinalSymbol("Rectangle", ID.Rectangle);

  public static final IBuiltInSymbol Red = initFinalSymbol("Red", ID.Red);

  /**
   * Reduce(logic-expression, var) - returns the reduced `logic-expression` for the variable `var`.
   * Reduce works only for the `Reals` domain.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reduce.md">Reduce
   *     documentation</a>
   */
  public static final IBuiltInSymbol Reduce = initFinalSymbol("Reduce", ID.Reduce);

  /**
   * Refine(expression, assumptions) - evaluate the `expression` for the given `assumptions`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Refine.md">Refine
   *     documentation</a>
   */
  public static final IBuiltInSymbol Refine = initFinalSymbol("Refine", ID.Refine);

  /**
   * RegularExpression("regex") - represents the regular expression specified by the string
   * `“regex”`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegularExpression.md">RegularExpression
   *     documentation</a>
   */
  public static final IBuiltInSymbol RegularExpression =
      initFinalSymbol("RegularExpression", ID.RegularExpression);

  /**
   * ReleaseHold(expr) - removes any `Hold`, `HoldForm`, `HoldPattern` or `HoldComplete` head from
   * `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReleaseHold.md">ReleaseHold
   *     documentation</a>
   */
  public static final IBuiltInSymbol ReleaseHold = initFinalSymbol("ReleaseHold", ID.ReleaseHold);

  public static final IBuiltInSymbol Remove = initFinalSymbol("Remove", ID.Remove);

  /**
   * RemoveDiacritics("string") - returns a version of `string` with all diacritics removed.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RemoveDiacritics.md">RemoveDiacritics
   *     documentation</a>
   */
  public static final IBuiltInSymbol RemoveDiacritics =
      initFinalSymbol("RemoveDiacritics", ID.RemoveDiacritics);

  public static final IBuiltInSymbol Repeated = initFinalSymbol("Repeated", ID.Repeated);

  public static final IBuiltInSymbol RepeatedNull =
      initFinalSymbol("RepeatedNull", ID.RepeatedNull);

  /**
   * Replace(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr` with
   * the right-hand-side `rhs`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Replace.md">Replace
   *     documentation</a>
   */
  public static final IBuiltInSymbol Replace = initFinalSymbol("Replace", ID.Replace);

  /**
   * ReplaceAll(expr, i -> new) - replaces all `i` in `expr` with `new`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceAll.md">ReplaceAll
   *     documentation</a>
   */
  public static final IBuiltInSymbol ReplaceAll = initFinalSymbol("ReplaceAll", ID.ReplaceAll);

  /**
   * ReplaceList(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr`
   * with the right-hand-side `rhs`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceList.md">ReplaceList
   *     documentation</a>
   */
  public static final IBuiltInSymbol ReplaceList = initFinalSymbol("ReplaceList", ID.ReplaceList);

  /**
   * ReplacePart(expr, i -> new) - replaces part `i` in `expr` with `new`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplacePart.md">ReplacePart
   *     documentation</a>
   */
  public static final IBuiltInSymbol ReplacePart = initFinalSymbol("ReplacePart", ID.ReplacePart);

  /**
   * ReplaceRepeated(expr, lhs -> rhs) - repeatedly applies the rule `lhs -> rhs` to `expr` until
   * the result no longer changes.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceRepeated.md">ReplaceRepeated
   *     documentation</a>
   */
  public static final IBuiltInSymbol ReplaceRepeated =
      initFinalSymbol("ReplaceRepeated", ID.ReplaceRepeated);

  /**
   * Rescale(list) - returns `Rescale(list,{Min(list), Max(list)})`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rescale.md">Rescale
   *     documentation</a>
   */
  public static final IBuiltInSymbol Rescale = initFinalSymbol("Rescale", ID.Rescale);

  /**
   * Rest(expr) - returns `expr` with the first element removed.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rest.md">Rest
   *     documentation</a>
   */
  public static final IBuiltInSymbol Rest = initFinalSymbol("Rest", ID.Rest);

  /**
   * Resultant(polynomial1, polynomial2, var) - computes the resultant of the polynomials
   * `polynomial1` and `polynomial2` with respect to the variable `var`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Resultant.md">Resultant
   *     documentation</a>
   */
  public static final IBuiltInSymbol Resultant = initFinalSymbol("Resultant", ID.Resultant);

  /**
   * Return(expr) - aborts a function call and returns `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Return.md">Return
   *     documentation</a>
   */
  public static final IBuiltInSymbol Return = initFinalSymbol("Return", ID.Return);

  /**
   * Reverse(list) - reverse the elements of the `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reverse.md">Reverse
   *     documentation</a>
   */
  public static final IBuiltInSymbol Reverse = initFinalSymbol("Reverse", ID.Reverse);

  /**
   * RiccatiSolve({A,B},{Q,R}) - An algebraic Riccati equation is a type of nonlinear equation that
   * arises in the context of infinite-horizon optimal control problems in continuous time or
   * discrete time. The continuous time algebraic Riccati equation (CARE):
   * `A^{T}·X+X·A-X·B·R^{-1}·B^{T}·X+Q==0`. And the respective linear controller is: `K =
   * R^{-1}·B^{T}·P`. The solver receives `A`, `B`, `Q` and `R` and computes `P`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RiccatiSolve.md">RiccatiSolve
   *     documentation</a>
   */
  public static final IBuiltInSymbol RiccatiSolve =
      initFinalSymbol("RiccatiSolve", ID.RiccatiSolve);

  /**
   * Riffle(list1, list2) - insert elements of `list2` between the elements of `list1`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Riffle.md">Riffle
   *     documentation</a>
   */
  public static final IBuiltInSymbol Riffle = initFinalSymbol("Riffle", ID.Riffle);

  public static final IBuiltInSymbol Right = initFinalSymbol("Right", ID.Right);

  /**
   * RightComposition(sym1, sym2,...)[arg1, arg2,...] - creates a composition of the symbols applied
   * in reversed order at the arguments.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RightComposition.md">RightComposition
   *     documentation</a>
   */
  public static final IBuiltInSymbol RightComposition =
      initFinalSymbol("RightComposition", ID.RightComposition);

  /**
   * RogersTanimotoDissimilarity(u, v) - returns the Rogers-Tanimoto dissimilarity between the two
   * boolean 1-D lists `u` and `v`, which is defined as `R / (c_tt + c_ff + R)` where n is `len(u)`,
   * `c_ij` is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * (c_tf +
   * c_ft)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RogersTanimotoDissimilarity.md">RogersTanimotoDissimilarity
   *     documentation</a>
   */
  public static final IBuiltInSymbol RogersTanimotoDissimilarity =
      initFinalSymbol("RogersTanimotoDissimilarity", ID.RogersTanimotoDissimilarity);

  /**
   * RomanNumeral(positive-int-value) - converts the given `positive-int-value` to a roman numeral
   * string.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RomanNumeral.md">RomanNumeral
   *     documentation</a>
   */
  public static final IBuiltInSymbol RomanNumeral =
      initFinalSymbol("RomanNumeral", ID.RomanNumeral);

  public static final IBuiltInSymbol Root = initFinalSymbol("Root", ID.Root);

  public static final IBuiltInSymbol RootIntervals =
      initFinalSymbol("RootIntervals", ID.RootIntervals);

  public static final IBuiltInSymbol RootOf = initFinalSymbol("RootOf", ID.RootOf);

  public static final IBuiltInSymbol RootReduce = initFinalSymbol("RootReduce", ID.RootReduce);

  /**
   * Roots(polynomial-equation, var) - determine the roots of a univariate polynomial equation with
   * respect to the variable `var`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Roots.md">Roots
   *     documentation</a>
   */
  public static final IBuiltInSymbol Roots = initFinalSymbol("Roots", ID.Roots);

  /**
   * RotateLeft(list) - rotates the items of `list` by one item to the left.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotateLeft.md">RotateLeft
   *     documentation</a>
   */
  public static final IBuiltInSymbol RotateLeft = initFinalSymbol("RotateLeft", ID.RotateLeft);

  /**
   * RotateRight(list) - rotates the items of `list` by one item to the right.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotateRight.md">RotateRight
   *     documentation</a>
   */
  public static final IBuiltInSymbol RotateRight = initFinalSymbol("RotateRight", ID.RotateRight);

  /**
   * RotationMatrix(theta) - yields a rotation matrix for the angle `theta`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotationMatrix.md">RotationMatrix
   *     documentation</a>
   */
  public static final IBuiltInSymbol RotationMatrix =
      initFinalSymbol("RotationMatrix", ID.RotationMatrix);

  /**
   * Round(expr) - round a given `expr` to nearest integer.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Round.md">Round
   *     documentation</a>
   */
  public static final IBuiltInSymbol Round = initFinalSymbol("Round", ID.Round);

  public static final IBuiltInSymbol Row = initFinalSymbol("Row", ID.Row);

  public static final IBuiltInSymbol RowBox = initFinalSymbol("RowBox", ID.RowBox);

  /**
   * RowReduce(matrix) - returns the reduced row-echelon form of `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RowReduce.md">RowReduce
   *     documentation</a>
   */
  public static final IBuiltInSymbol RowReduce = initFinalSymbol("RowReduce", ID.RowReduce);

  /**
   * Rule(x, y) - represents a rule replacing `x` with `y`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rule.md">Rule
   *     documentation</a>
   */
  public static final IBuiltInSymbol Rule = initFinalSymbol("Rule", ID.Rule);

  /**
   * RuleDelayed(x, y) - represents a rule replacing `x` with `y`, with `y` held unevaluated.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RuleDelayed.md">RuleDelayed
   *     documentation</a>
   */
  public static final IBuiltInSymbol RuleDelayed = initFinalSymbol("RuleDelayed", ID.RuleDelayed);

  /**
   * RussellRaoDissimilarity(u, v) - returns the Russell-Rao dissimilarity between the two boolean
   * 1-D lists `u` and `v`, which is defined as `(n - c_tt) / c_tt` where `n` is `len(u)` and `c_ij`
   * is the number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RussellRaoDissimilarity.md">RussellRaoDissimilarity
   *     documentation</a>
   */
  public static final IBuiltInSymbol RussellRaoDissimilarity =
      initFinalSymbol("RussellRaoDissimilarity", ID.RussellRaoDissimilarity);

  /**
   * SameObjectQ[java-object1, java-object2] - gives `True` if the Java `==` operator for the Java
   * objects gives true. `False` otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SameObjectQ.md">SameObjectQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol SameObjectQ = initFinalSymbol("SameObjectQ", ID.SameObjectQ);

  /**
   * SameQ(x, y) - returns `True` if `x` and `y` are structurally identical.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SameQ.md">SameQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol SameQ = initFinalSymbol("SameQ", ID.SameQ);

  public static final IBuiltInSymbol SameTest = initFinalSymbol("SameTest", ID.SameTest);

  /**
   * SatisfiabilityCount(boolean-expr) - test whether the `boolean-expr` is satisfiable by a
   * combination of boolean `False` and `True` values for the variables of the boolean expression
   * and return the number of possible combinations.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiabilityCount.md">SatisfiabilityCount
   *     documentation</a>
   */
  public static final IBuiltInSymbol SatisfiabilityCount =
      initFinalSymbol("SatisfiabilityCount", ID.SatisfiabilityCount);

  /**
   * SatisfiabilityInstances(boolean-expr, list-of-variables) - test whether the `boolean-expr` is
   * satisfiable by a combination of boolean `False` and `True` values for the `list-of-variables`
   * and return exactly one instance of `True, False` combinations if possible.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiabilityInstances.md">SatisfiabilityInstances
   *     documentation</a>
   */
  public static final IBuiltInSymbol SatisfiabilityInstances =
      initFinalSymbol("SatisfiabilityInstances", ID.SatisfiabilityInstances);

  /**
   * SatisfiableQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable
   * by a combination of boolean `False` and `True` values for the `list-of-variables`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiableQ.md">SatisfiableQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol SatisfiableQ =
      initFinalSymbol("SatisfiableQ", ID.SatisfiableQ);

  public static final IBuiltInSymbol Scaled = initFinalSymbol("Scaled", ID.Scaled);

  /**
   * Scan(f, expr) - applies `f` to each element of `expr` and returns 'Null'.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Scan.md">Scan
   *     documentation</a>
   */
  public static final IBuiltInSymbol Scan = initFinalSymbol("Scan", ID.Scan);

  /**
   * Sec(z) - returns the secant of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sec.md">Sec
   *     documentation</a>
   */
  public static final IBuiltInSymbol Sec = initFinalSymbol("Sec", ID.Sec);

  /**
   * Sech(z) - returns the hyperbolic secant of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sech.md">Sech
   *     documentation</a>
   */
  public static final IBuiltInSymbol Sech = initFinalSymbol("Sech", ID.Sech);

  public static final IBuiltInSymbol Second = initFinalSymbol("Second", ID.Second);

  /**
   * Select({e1, e2, ...}, head) - returns a list of the elements `ei` for which `head(ei)` returns
   * `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Select.md">Select
   *     documentation</a>
   */
  public static final IBuiltInSymbol Select = initFinalSymbol("Select", ID.Select);

  /**
   * SelectFirst({e1, e2, ...}, f) - returns the first of the elements `ei` for which `f(ei)`
   * returns `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SelectFirst.md">SelectFirst
   *     documentation</a>
   */
  public static final IBuiltInSymbol SelectFirst = initFinalSymbol("SelectFirst", ID.SelectFirst);

  /**
   * SemanticImport("path-to-filename") - if the file system is enabled, import the data from CSV
   * files and do a semantic interpretation of the columns.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SemanticImport.md">SemanticImport
   *     documentation</a>
   */
  public static final IBuiltInSymbol SemanticImport =
      initFinalSymbol("SemanticImport", ID.SemanticImport);

  /**
   * SemanticImportString("string-content") - import the data from a content string in CSV format
   * and do a semantic interpretation of the columns.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SemanticImportString.md">SemanticImportString
   *     documentation</a>
   */
  public static final IBuiltInSymbol SemanticImportString =
      initFinalSymbol("SemanticImportString", ID.SemanticImportString);

  /**
   * Sequence[x1, x2, ...] - represents a sequence of arguments to a function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sequence.md">Sequence
   *     documentation</a>
   */
  public static final IBuiltInSymbol Sequence = initFinalSymbol("Sequence", ID.Sequence);

  public static final IBuiltInSymbol SequenceHold =
      initFinalSymbol("SequenceHold", ID.SequenceHold);

  /**
   * Series(expr, {x, x0, n}) - create a power series of `expr` up to order `(x- x0)^n` at the point
   * `x = x0`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Series.md">Series
   *     documentation</a>
   */
  public static final IBuiltInSymbol Series = initFinalSymbol("Series", ID.Series);

  /**
   * SeriesCoefficient(expr, {x, x0, n}) - get the coefficient of `(x- x0)^n` at the point `x = x0`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SeriesCoefficient.md">SeriesCoefficient
   *     documentation</a>
   */
  public static final IBuiltInSymbol SeriesCoefficient =
      initFinalSymbol("SeriesCoefficient", ID.SeriesCoefficient);

  /**
   * SeriesData(x, x0, {coeff0, coeff1, coeff2,...}, nMin, nMax, denominator}) - internal structure
   * of a power series at the point `x = x0` the `coeff_i` are coefficients of the power series.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SeriesData.md">SeriesData
   *     documentation</a>
   */
  public static final IBuiltInSymbol SeriesData = initFinalSymbol("SeriesData", ID.SeriesData);

  /**
   * Set(expr, value) - evaluates `value` and assigns it to `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Set.md">Set
   *     documentation</a>
   */
  public static final IBuiltInSymbol Set = initFinalSymbol("Set", ID.Set);

  /**
   * SetAttributes(symbol, attrib) - adds `attrib` to `symbol`'s attributes.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SetAttributes.md">SetAttributes
   *     documentation</a>
   */
  public static final IBuiltInSymbol SetAttributes =
      initFinalSymbol("SetAttributes", ID.SetAttributes);

  /**
   * SetDelayed(expr, value) - assigns `value` to `expr`, without evaluating `value`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SetDelayed.md">SetDelayed
   *     documentation</a>
   */
  public static final IBuiltInSymbol SetDelayed = initFinalSymbol("SetDelayed", ID.SetDelayed);

  public static final IBuiltInSymbol SetSystemOptions =
      initFinalSymbol("SetSystemOptions", ID.SetSystemOptions);

  /**
   * Share(function) - replace internally equal common subexpressions in `function` by the same
   * reference to reduce memory consumption and return the number of times where `Share(function)`
   * could replace a common subexpression.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Share.md">Share
   *     documentation</a>
   */
  public static final IBuiltInSymbol Share = initFinalSymbol("Share", ID.Share);

  public static final IBuiltInSymbol Short = initFinalSymbol("Short", ID.Short);

  public static final IBuiltInSymbol Shortest = initFinalSymbol("Shortest", ID.Shortest);

  public static final IBuiltInSymbol Show = initFinalSymbol("Show", ID.Show);

  /**
   * Sign(x) - gives `-1`, `0` or `1` depending on whether `x` is negative, zero or positive.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sign.md">Sign
   *     documentation</a>
   */
  public static final IBuiltInSymbol Sign = initFinalSymbol("Sign", ID.Sign);

  public static final IBuiltInSymbol SignCmp = initFinalSymbol("SignCmp", ID.SignCmp);

  /**
   * Signature(permutation-list) - determine if the `permutation-list` has odd (`-1`) or even (`1`)
   * parity. Returns `0` if two elements in the `permutation-list` are equal.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Signature.md">Signature
   *     documentation</a>
   */
  public static final IBuiltInSymbol Signature = initFinalSymbol("Signature", ID.Signature);

  public static final IBuiltInSymbol Simplex = initFinalSymbol("Simplex", ID.Simplex);

  /**
   * Simplify(expr) - simplifies `expr`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Simplify.md">Simplify
   *     documentation</a>
   */
  public static final IBuiltInSymbol Simplify = initFinalSymbol("Simplify", ID.Simplify);

  /**
   * Sin(expr) - returns the sine of `expr` (measured in radians).
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sin.md">Sin
   *     documentation</a>
   */
  public static final IBuiltInSymbol Sin = initFinalSymbol("Sin", ID.Sin);

  /**
   * SinIntegral(expr) - returns the hyperbolic sine integral of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SinIntegral.md">SinIntegral
   *     documentation</a>
   */
  public static final IBuiltInSymbol SinIntegral = initFinalSymbol("SinIntegral", ID.SinIntegral);

  /**
   * Sinc(expr) - the sinc function `Sin(expr)/expr` for `expr != 0`. `Sinc(0)` returns `1`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sinc.md">Sinc
   *     documentation</a>
   */
  public static final IBuiltInSymbol Sinc = initFinalSymbol("Sinc", ID.Sinc);

  /**
   * SingularValueDecomposition(matrix) - calculates the singular value decomposition for the
   * `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SingularValueDecomposition.md">SingularValueDecomposition
   *     documentation</a>
   */
  public static final IBuiltInSymbol SingularValueDecomposition =
      initFinalSymbol("SingularValueDecomposition", ID.SingularValueDecomposition);

  /**
   * Sinh(z) - returns the hyperbolic sine of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sinh.md">Sinh
   *     documentation</a>
   */
  public static final IBuiltInSymbol Sinh = initFinalSymbol("Sinh", ID.Sinh);

  /**
   * SinhIntegral(expr) - returns the sine integral of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SinhIntegral.md">SinhIntegral
   *     documentation</a>
   */
  public static final IBuiltInSymbol SinhIntegral =
      initFinalSymbol("SinhIntegral", ID.SinhIntegral);

  /**
   * Skewness(list) - gives Pearson's moment coefficient of skewness for `list` (a measure for
   * estimating the symmetry of a distribution).
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Skewness.md">Skewness
   *     documentation</a>
   */
  public static final IBuiltInSymbol Skewness = initFinalSymbol("Skewness", ID.Skewness);

  /**
   * # - is a short-hand for `#1`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Slot.md">Slot
   *     documentation</a>
   */
  public static final IBuiltInSymbol Slot = initFinalSymbol("Slot", ID.Slot);

  public static final IBuiltInSymbol SlotAbsent = initFinalSymbol("SlotAbsent", ID.SlotAbsent);

  /**
   * ## - is the sequence of arguments supplied to a pure function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SlotSequence.md">SlotSequence
   *     documentation</a>
   */
  public static final IBuiltInSymbol SlotSequence =
      initFinalSymbol("SlotSequence", ID.SlotSequence);

  /**
   * SokalSneathDissimilarity(u, v) - returns the Sokal-Sneath dissimilarity between the two boolean
   * 1-D lists `u` and `v`, which is defined as `R / (c_tt + R)` where n is `len(u)`, `c_ij` is the
   * number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * (c_tf + c_ft)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SokalSneathDissimilarity.md">SokalSneathDissimilarity
   *     documentation</a>
   */
  public static final IBuiltInSymbol SokalSneathDissimilarity =
      initFinalSymbol("SokalSneathDissimilarity", ID.SokalSneathDissimilarity);

  /**
   * Solve(equations, vars) - attempts to solve `equations` for the variables `vars`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Solve.md">Solve
   *     documentation</a>
   */
  public static final IBuiltInSymbol Solve = initFinalSymbol("Solve", ID.Solve);

  /**
   * Sort(list) - sorts `list` (or the leaves of any other expression) according to canonical
   * ordering.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sort.md">Sort
   *     documentation</a>
   */
  public static final IBuiltInSymbol Sort = initFinalSymbol("Sort", ID.Sort);

  /**
   * SortBy(list, f) - sorts `list` (or the leaves of any other expression) according to canonical
   * ordering of the keys that are extracted from the `list`'s elements using `f`. Chunks of leaves
   * that appear the same under `f` are sorted according to their natural order (without applying
   * `f`).
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SortBy.md">SortBy
   *     documentation</a>
   */
  public static final IBuiltInSymbol SortBy = initFinalSymbol("SortBy", ID.SortBy);

  /**
   * Sow(expr) - sends the value `expr` to the innermost `Reap`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sow.md">Sow
   *     documentation</a>
   */
  public static final IBuiltInSymbol Sow = initFinalSymbol("Sow", ID.Sow);

  /**
   * Span - is the head of span ranges like `1;;3`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Span.md">Span
   *     documentation</a>
   */
  public static final IBuiltInSymbol Span = initFinalSymbol("Span", ID.Span);

  /**
   * SparseArray(nested-list) - create a sparse array from a `nested-list` structure.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SparseArray.md">SparseArray
   *     documentation</a>
   */
  public static final IBuiltInSymbol SparseArray = initFinalSymbol("SparseArray", ID.SparseArray);

  public static final IBuiltInSymbol Specularity = initFinalSymbol("Specularity", ID.Specularity);

  /**
   * Sphere({x, y, z}) - is a sphere of radius `1` centered at the point `{x, y, z}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sphere.md">Sphere
   *     documentation</a>
   */
  public static final IBuiltInSymbol Sphere = initFinalSymbol("Sphere", ID.Sphere);

  /**
   * SphericalBesselJ(n, z) - spherical Bessel function `J(n, x)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SphericalBesselJ.md">SphericalBesselJ
   *     documentation</a>
   */
  public static final IBuiltInSymbol SphericalBesselJ =
      initFinalSymbol("SphericalBesselJ", ID.SphericalBesselJ);

  /**
   * SphericalBesselY(n, z) - spherical Bessel function `Y(n, x)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SphericalBesselY.md">SphericalBesselY
   *     documentation</a>
   */
  public static final IBuiltInSymbol SphericalBesselY =
      initFinalSymbol("SphericalBesselY", ID.SphericalBesselY);

  public static final IBuiltInSymbol SphericalHankelH1 =
      initFinalSymbol("SphericalHankelH1", ID.SphericalHankelH1);

  public static final IBuiltInSymbol SphericalHankelH2 =
      initFinalSymbol("SphericalHankelH2", ID.SphericalHankelH2);

  public static final IBuiltInSymbol SphericalHarmonicY =
      initFinalSymbol("SphericalHarmonicY", ID.SphericalHarmonicY);

  /**
   * Split(list) - splits `list` into collections of consecutive identical elements.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Split.md">Split
   *     documentation</a>
   */
  public static final IBuiltInSymbol Split = initFinalSymbol("Split", ID.Split);

  /**
   * SplitBy(list, f) - splits `list` into collections of consecutive elements that give the same
   * result when `f` is applied.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SplitBy.md">SplitBy
   *     documentation</a>
   */
  public static final IBuiltInSymbol SplitBy = initFinalSymbol("SplitBy", ID.SplitBy);

  /**
   * Sqrt(expr) - returns the square root of `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sqrt.md">Sqrt
   *     documentation</a>
   */
  public static final IBuiltInSymbol Sqrt = initFinalSymbol("Sqrt", ID.Sqrt);

  /**
   * SquareFreeQ(n) - returns `True` if `n` is a square free integer number or a square free
   * univariate polynomial.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquareFreeQ.md">SquareFreeQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol SquareFreeQ = initFinalSymbol("SquareFreeQ", ID.SquareFreeQ);

  /**
   * SquareMatrixQ(m) - returns `True` if `m` is a square matrix.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquareMatrixQ.md">SquareMatrixQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol SquareMatrixQ =
      initFinalSymbol("SquareMatrixQ", ID.SquareMatrixQ);

  /**
   * SquaredEuclideanDistance(u, v) - returns squared the euclidean distance between `u$` and `v`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquaredEuclideanDistance.md">SquaredEuclideanDistance
   *     documentation</a>
   */
  public static final IBuiltInSymbol SquaredEuclideanDistance =
      initFinalSymbol("SquaredEuclideanDistance", ID.SquaredEuclideanDistance);

  /**
   * Stack( ) - return a list of the heads of the current stack wrapped by `HoldForm`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Stack.md">Stack
   *     documentation</a>
   */
  public static final IBuiltInSymbol Stack = initFinalSymbol("Stack", ID.Stack);

  /**
   * Stack(expr) - begine a new stack and evaluate `èxpr`. Use `Stack(_)` as a subexpression in
   * `expr` to return the stack elements.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StackBegin.md">StackBegin
   *     documentation</a>
   */
  public static final IBuiltInSymbol StackBegin = initFinalSymbol("StackBegin", ID.StackBegin);

  /**
   * StandardDeviation(list) - computes the standard deviation of `list`. `list` may consist of
   * numerical values or symbols. Numerical values may be real or complex.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardDeviation.md">StandardDeviation
   *     documentation</a>
   */
  public static final IBuiltInSymbol StandardDeviation =
      initFinalSymbol("StandardDeviation", ID.StandardDeviation);

  public static final IBuiltInSymbol StandardForm =
      initFinalSymbol("StandardForm", ID.StandardForm);

  public static final IBuiltInSymbol Standardize = initFinalSymbol("Standardize", ID.Standardize);

  /**
   * StarGraph(order) - create a new star graph with `order` number of total vertices including the
   * center vertex.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StarGraph.md">StarGraph
   *     documentation</a>
   */
  public static final IBuiltInSymbol StarGraph = initFinalSymbol("StarGraph", ID.StarGraph);

  /**
   * StartOfLine - begine a new stack and evaluate `èxpr`. Use `Stack(_)` as a subexpression in
   * `expr` to return the stack elements.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StartOfLine.md">StartOfLine
   *     documentation</a>
   */
  public static final IBuiltInSymbol StartOfLine = initFinalSymbol("StartOfLine", ID.StartOfLine);

  /**
   * StartOfString - represents the start of a string.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StartOfString.md">StartOfString
   *     documentation</a>
   */
  public static final IBuiltInSymbol StartOfString =
      initFinalSymbol("StartOfString", ID.StartOfString);

  public static final IBuiltInSymbol StaticsVisible =
      initFinalSymbol("StaticsVisible", ID.StaticsVisible);

  /**
   * StieltjesGamma(a) - returns Stieltjes constant.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StieltjesGamma.md">StieltjesGamma
   *     documentation</a>
   */
  public static final IBuiltInSymbol StieltjesGamma =
      initFinalSymbol("StieltjesGamma", ID.StieltjesGamma);

  /**
   * StirlingS1(n, k) - returns the Stirling numbers of the first kind.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StirlingS1.md">StirlingS1
   *     documentation</a>
   */
  public static final IBuiltInSymbol StirlingS1 = initFinalSymbol("StirlingS1", ID.StirlingS1);

  /**
   * StirlingS2(n, k) - returns the Stirling numbers of the second kind. `StirlingS2(n,k)` is the
   * number of ways of partitioning an `n`-element set into `k` non-empty subsets.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StirlingS2.md">StirlingS2
   *     documentation</a>
   */
  public static final IBuiltInSymbol StirlingS2 = initFinalSymbol("StirlingS2", ID.StirlingS2);

  public static final IBuiltInSymbol Strict = initFinalSymbol("Strict", ID.Strict);

  /**
   * String - is the head of strings..
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/String.md">String
   *     documentation</a>
   */
  public static final IBuiltInSymbol String = initFinalSymbol("String", ID.String);

  /**
   * StringCases(string, pattern) - gives all occurences of `pattern` in `string`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringCases.md">StringCases
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringCases = initFinalSymbol("StringCases", ID.StringCases);

  /**
   * StringContainsQ(str1, str2) - return a list of matches for `"p1", "p2",...` list of strings in
   * the string `str`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringContainsQ.md">StringContainsQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringContainsQ =
      initFinalSymbol("StringContainsQ", ID.StringContainsQ);

  /**
   * StringCount(string, pattern) - counts all occurences of `pattern` in `string`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringCount.md">StringCount
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringCount = initFinalSymbol("StringCount", ID.StringCount);

  public static final IBuiltInSymbol StringDrop = initFinalSymbol("StringDrop", ID.StringDrop);

  /**
   * StringExpression(s_1, s_2, ...) - represents a sequence of strings and symbolic string objects
   * `s_i`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringExpression.md">StringExpression
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringExpression =
      initFinalSymbol("StringExpression", ID.StringExpression);

  public static final IBuiltInSymbol StringFormat =
      initFinalSymbol("StringFormat", ID.StringFormat);

  /**
   * StringFreeQ("string", patt) - returns `True` if no substring in `string` matches the string
   * expression `patt`, and returns `False` otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringFreeQ.md">StringFreeQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringFreeQ = initFinalSymbol("StringFreeQ", ID.StringFreeQ);

  /**
   * StringInsert(string, new-string, position) - returns a string with `new-string` inserted
   * starting at `position` in `string`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringInsert.md">StringInsert
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringInsert =
      initFinalSymbol("StringInsert", ID.StringInsert);

  /**
   * StringJoin(str1, str2, ... strN) - returns the concatenation of the strings `str1, str2, ...
   * strN`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringJoin.md">StringJoin
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringJoin = initFinalSymbol("StringJoin", ID.StringJoin);

  /**
   * StringLength(string) - gives the length of `string`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringLength.md">StringLength
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringLength =
      initFinalSymbol("StringLength", ID.StringLength);

  /**
   * StringMatchQ(string, regex-pattern) - check if the regular expression `regex-pattern` matches
   * the `string`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringMatchQ.md">StringMatchQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringMatchQ =
      initFinalSymbol("StringMatchQ", ID.StringMatchQ);

  /**
   * StringPart(str, pos) - return the character at position `pos` from the `str` string expression.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringPart.md">StringPart
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringPart = initFinalSymbol("StringPart", ID.StringPart);

  /**
   * StringPosition("string", patt) - gives a list of starting and ending positions where `patt`
   * matches `"string"`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringPosition.md">StringPosition
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringPosition =
      initFinalSymbol("StringPosition", ID.StringPosition);

  /**
   * StringQ(x) - is `True` if `x` is a string object, or `False` otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringQ.md">StringQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringQ = initFinalSymbol("StringQ", ID.StringQ);

  /**
   * StringReplace(string, fromStr -> toStr) - replaces each occurrence of `fromStr` with `toStr` in
   * `string`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringReplace.md">StringReplace
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringReplace =
      initFinalSymbol("StringReplace", ID.StringReplace);

  /**
   * StringReplace(string) - reverse the `string`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringReverse.md">StringReverse
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringReverse =
      initFinalSymbol("StringReverse", ID.StringReverse);

  /**
   * StringRiffle({s1, s2, s3, ...}) - returns a new string by concatenating all the `si`, with
   * spaces inserted between them.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringRiffle.md">StringRiffle
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringRiffle =
      initFinalSymbol("StringRiffle", ID.StringRiffle);

  /**
   * StringSplit(str) - split the string `str` by whitespaces into a list of strings.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringSplit.md">StringSplit
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringSplit = initFinalSymbol("StringSplit", ID.StringSplit);

  /**
   * StringTake("string", n) - gives the first `n` characters in `string`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringTake.md">StringTake
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringTake = initFinalSymbol("StringTake", ID.StringTake);

  /**
   * StringTemplate(string) - gives a `StringTemplate` expression with name `string`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringTemplate.md">StringTemplate
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringTemplate =
      initFinalSymbol("StringTemplate", ID.StringTemplate);

  /**
   * StringToByteArray(string) - encodes the `string` into a sequence of bytes using the default
   * character set `UTF-8`, storing the result into into a `ByteArray`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringToByteArray.md">StringToByteArray
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringToByteArray =
      initFinalSymbol("StringToByteArray", ID.StringToByteArray);

  /**
   * StringToStream("string") - converts a `string` to an open input stream.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringToStream.md">StringToStream
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringToStream =
      initFinalSymbol("StringToStream", ID.StringToStream);

  /**
   * StringTrim(s) - returns a version of `s `with whitespace removed from start and end.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringTrim.md">StringTrim
   *     documentation</a>
   */
  public static final IBuiltInSymbol StringTrim = initFinalSymbol("StringTrim", ID.StringTrim);

  public static final IBuiltInSymbol Structure = initFinalSymbol("Structure", ID.Structure);

  /**
   * StruveH(n, z) - returns the Struve function `H_n(z)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StruveH.md">StruveH
   *     documentation</a>
   */
  public static final IBuiltInSymbol StruveH = initFinalSymbol("StruveH", ID.StruveH);

  /**
   * StruveL(n, z) - returns the modified Struve function `L_n(z)`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StruveL.md">StruveL
   *     documentation</a>
   */
  public static final IBuiltInSymbol StruveL = initFinalSymbol("StruveL", ID.StruveL);

  /**
   * StudentTDistribution(v) - returns a Student's t-distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StudentTDistribution.md">StudentTDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol StudentTDistribution =
      initFinalSymbol("StudentTDistribution", ID.StudentTDistribution);

  public static final IBuiltInSymbol Style = initFinalSymbol("Style", ID.Style);

  public static final IBuiltInSymbol StyleForm = initFinalSymbol("StyleForm", ID.StyleForm);

  /**
   * Subdivide(n) - returns a list with `n+1` entries obtained by subdividing the range `0` to `1`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subdivide.md">Subdivide
   *     documentation</a>
   */
  public static final IBuiltInSymbol Subdivide = initFinalSymbol("Subdivide", ID.Subdivide);

  /**
   * Subfactorial(n) - returns the subfactorial number of the integer `n`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subfactorial.md">Subfactorial
   *     documentation</a>
   */
  public static final IBuiltInSymbol Subfactorial =
      initFinalSymbol("Subfactorial", ID.Subfactorial);

  public static final IBuiltInSymbol Subscript = initFinalSymbol("Subscript", ID.Subscript);

  public static final IBuiltInSymbol SubscriptBox =
      initFinalSymbol("SubscriptBox", ID.SubscriptBox);

  /**
   * SubsetQ(set1, set2) - returns `True` if `set2` is a subset of `set1`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubsetQ.md">SubsetQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol SubsetQ = initFinalSymbol("SubsetQ", ID.SubsetQ);

  /**
   * Subsets(list) - finds a list of all possible subsets of `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subsets.md">Subsets
   *     documentation</a>
   */
  public static final IBuiltInSymbol Subsets = initFinalSymbol("Subsets", ID.Subsets);

  public static final IBuiltInSymbol Subsuperscript =
      initFinalSymbol("Subsuperscript", ID.Subsuperscript);

  /**
   * Subtract(a, b) - represents the subtraction of `b` from `a`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subtract.md">Subtract
   *     documentation</a>
   */
  public static final IBuiltInSymbol Subtract = initFinalSymbol("Subtract", ID.Subtract);

  /**
   * SubtractFrom(x, dx) - is equivalent to `x = x - dx`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubtractFrom.md">SubtractFrom
   *     documentation</a>
   */
  public static final IBuiltInSymbol SubtractFrom =
      initFinalSymbol("SubtractFrom", ID.SubtractFrom);

  public static final IBuiltInSymbol SubtractSides =
      initFinalSymbol("SubtractSides", ID.SubtractSides);

  /**
   * Sum(expr, {i, imin, imax}) - evaluates the discrete sum of `expr` with `i` ranging from `imin`
   * to `imax`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sum.md">Sum
   *     documentation</a>
   */
  public static final IBuiltInSymbol Sum = initFinalSymbol("Sum", ID.Sum);

  public static final IBuiltInSymbol Summary = initFinalSymbol("Summary", ID.Summary);

  public static final IBuiltInSymbol Superscript = initFinalSymbol("Superscript", ID.Superscript);

  public static final IBuiltInSymbol SuperscriptBox =
      initFinalSymbol("SuperscriptBox", ID.SuperscriptBox);

  /**
   * Surd(expr, n) - returns the `n`-th root of `expr`. If the result is defined, it's a real value.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Surd.md">Surd
   *     documentation</a>
   */
  public static final IBuiltInSymbol Surd = initFinalSymbol("Surd", ID.Surd);

  public static final IBuiltInSymbol SurfaceArea = initFinalSymbol("SurfaceArea", ID.SurfaceArea);

  public static final IBuiltInSymbol SurfaceGraphics =
      initFinalSymbol("SurfaceGraphics", ID.SurfaceGraphics);

  /**
   * SurvivalFunction(dist, x) - returns the survival function for the distribution `dist` evaluated
   * at `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SurvivalFunction.md">SurvivalFunction
   *     documentation</a>
   */
  public static final IBuiltInSymbol SurvivalFunction =
      initFinalSymbol("SurvivalFunction", ID.SurvivalFunction);

  /**
   * Switch(expr, pattern1, value1, pattern2, value2, ...) - yields the first `value` for which
   * `expr` matches the corresponding pattern.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Switch.md">Switch
   *     documentation</a>
   */
  public static final IBuiltInSymbol Switch = initFinalSymbol("Switch", ID.Switch);

  /**
   * Symbol - is the head of symbols.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Symbol.md">Symbol
   *     documentation</a>
   */
  public static final IBuiltInSymbol Symbol = initFinalSymbol("Symbol", ID.Symbol);

  /**
   * SymbolName(s) - returns the name of the symbol `s` (without any leading context name).
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymbolName.md">SymbolName
   *     documentation</a>
   */
  public static final IBuiltInSymbol SymbolName = initFinalSymbol("SymbolName", ID.SymbolName);

  /**
   * SymbolQ(x) - is `True` if `x` is a symbol, or `False` otherwise.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymbolQ.md">SymbolQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol SymbolQ = initFinalSymbol("SymbolQ", ID.SymbolQ);

  public static final IBuiltInSymbol Symmetric = initFinalSymbol("Symmetric", ID.Symmetric);

  /**
   * SymmetricMatrixQ(m) - returns `True` if `m` is a symmetric matrix.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymmetricMatrixQ.md">SymmetricMatrixQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol SymmetricMatrixQ =
      initFinalSymbol("SymmetricMatrixQ", ID.SymmetricMatrixQ);

  public static final IBuiltInSymbol SyntaxLength =
      initFinalSymbol("SyntaxLength", ID.SyntaxLength);

  /**
   * SyntaxQ(str) - is `True` if the given `str` is a string which has the correct syntax.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SyntaxQ.md">SyntaxQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol SyntaxQ = initFinalSymbol("SyntaxQ", ID.SyntaxQ);

  /**
   * SystemDialogInput("FileOpen") - if the file system is enabled, open a file chooser dialog box.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SystemDialogInput.md">SystemDialogInput
   *     documentation</a>
   */
  public static final IBuiltInSymbol SystemDialogInput =
      initFinalSymbol("SystemDialogInput", ID.SystemDialogInput);

  public static final IBuiltInSymbol SystemOptions =
      initFinalSymbol("SystemOptions", ID.SystemOptions);

  /**
   * Table(expr, {i, n}) - evaluates `expr` with `i` ranging from `1` to `n`, returning a list of
   * the results.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Table.md">Table
   *     documentation</a>
   */
  public static final IBuiltInSymbol Table = initFinalSymbol("Table", ID.Table);

  public static final IBuiltInSymbol TableForm = initFinalSymbol("TableForm", ID.TableForm);

  public static final IBuiltInSymbol TableHeadings =
      initFinalSymbol("TableHeadings", ID.TableHeadings);

  /**
   * TagSet(f, expr, value) - assigns the evaluated `value` to `expr` and associates the
   * corresponding rule with the symbol `f`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TagSet.md">TagSet
   *     documentation</a>
   */
  public static final IBuiltInSymbol TagSet = initFinalSymbol("TagSet", ID.TagSet);

  /**
   * TagSetDelayed(f, expr, value) - assigns `value` to `expr`, without evaluating `value` and
   * associates the corresponding rule with the symbol `f`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TagSetDelayed.md">TagSetDelayed
   *     documentation</a>
   */
  public static final IBuiltInSymbol TagSetDelayed =
      initFinalSymbol("TagSetDelayed", ID.TagSetDelayed);

  /**
   * Take(expr, n) - returns `expr` with all but the first `n` leaves removed.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Take.md">Take
   *     documentation</a>
   */
  public static final IBuiltInSymbol Take = initFinalSymbol("Take", ID.Take);

  /**
   * TakeLargest({e_1, e_2, ..., e_i}, n) - returns the `n` largest real values from the list `{e_1,
   * e_2, ..., e_i}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeLargest.md">TakeLargest
   *     documentation</a>
   */
  public static final IBuiltInSymbol TakeLargest = initFinalSymbol("TakeLargest", ID.TakeLargest);

  /**
   * TakeLargestBy({e_1, e_2, ..., e_i}, function, n) - returns the `n` values from the list `{e_1,
   * e_2, ..., e_i}`, where `function(e_i)` is largest.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeLargestBy.md">TakeLargestBy
   *     documentation</a>
   */
  public static final IBuiltInSymbol TakeLargestBy =
      initFinalSymbol("TakeLargestBy", ID.TakeLargestBy);

  /**
   * TakeSmallest({e_1, e_2, ..., e_i}, n) - returns the `n` smallest real values from the list
   * `{e_1, e_2, ..., e_i}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeSmallest.md">TakeSmallest
   *     documentation</a>
   */
  public static final IBuiltInSymbol TakeSmallest =
      initFinalSymbol("TakeSmallest", ID.TakeSmallest);

  /**
   * TakeSmallestBy({e_1, e_2, ..., e_i}, function, n) - returns the `n` values from the list `{e_1,
   * e_2, ..., e_i}`, where `function(e_i)` is smallest.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeSmallestBy.md">TakeSmallestBy
   *     documentation</a>
   */
  public static final IBuiltInSymbol TakeSmallestBy =
      initFinalSymbol("TakeSmallestBy", ID.TakeSmallestBy);

  /**
   * Tally(list) - return the elements and their number of occurrences in `list` in a new result
   * list. The `binary-predicate` tests if two elements are equivalent. `SameQ` is used as the
   * default `binary-predicate`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tally.md">Tally
   *     documentation</a>
   */
  public static final IBuiltInSymbol Tally = initFinalSymbol("Tally", ID.Tally);

  /**
   * Tan(expr) - returns the tangent of `expr` (measured in radians).
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tan.md">Tan
   *     documentation</a>
   */
  public static final IBuiltInSymbol Tan = initFinalSymbol("Tan", ID.Tan);

  /**
   * Tanh(z) - returns the hyperbolic tangent of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tanh.md">Tanh
   *     documentation</a>
   */
  public static final IBuiltInSymbol Tanh = initFinalSymbol("Tanh", ID.Tanh);

  /**
   * TautologyQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable by
   * all combinations of boolean `False` and `True` values for the `list-of-variables`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TautologyQ.md">TautologyQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol TautologyQ = initFinalSymbol("TautologyQ", ID.TautologyQ);

  public static final IBuiltInSymbol Taylor = initFinalSymbol("Taylor", ID.Taylor);

  /**
   * TeXForm(expr) - returns the TeX form of the evaluated `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TeXForm.md">TeXForm
   *     documentation</a>
   */
  public static final IBuiltInSymbol TeXForm = initFinalSymbol("TeXForm", ID.TeXForm);

  /**
   * TemplateApply(string, values) - renders a `StringTemplate` expression by replacing
   * `TemplateSlot`s with mapped values.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateApply.md">TemplateApply
   *     documentation</a>
   */
  public static final IBuiltInSymbol TemplateApply =
      initFinalSymbol("TemplateApply", ID.TemplateApply);

  public static final IBuiltInSymbol TemplateExpression =
      initFinalSymbol("TemplateExpression", ID.TemplateExpression);

  /**
   * TemplateIf(condition-expression, true-expression, false-expression) - in `TemplateApply`
   * evaluation insert `true-expression` if `condition-expression` evaluates to `true`, otherwise
   * insert `false-expression`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateIf.md">TemplateIf
   *     documentation</a>
   */
  public static final IBuiltInSymbol TemplateIf = initFinalSymbol("TemplateIf", ID.TemplateIf);

  /**
   * TemplateSlot(string) - gives a `TemplateSlot` expression with name `string`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateSlot.md">TemplateSlot
   *     documentation</a>
   */
  public static final IBuiltInSymbol TemplateSlot =
      initFinalSymbol("TemplateSlot", ID.TemplateSlot);

  public static final IBuiltInSymbol TensorDimensions =
      initFinalSymbol("TensorDimensions", ID.TensorDimensions);

  /**
   * TensorProduct(t1, t2, ...) - product of the tensors `t1, t2, ...`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TensorProduct.md">TensorProduct
   *     documentation</a>
   */
  public static final IBuiltInSymbol TensorProduct =
      initFinalSymbol("TensorProduct", ID.TensorProduct);

  public static final IBuiltInSymbol TensorRank = initFinalSymbol("TensorRank", ID.TensorRank);

  public static final IBuiltInSymbol TensorSymmetry =
      initFinalSymbol("TensorSymmetry", ID.TensorSymmetry);

  public static final IBuiltInSymbol TestID = initFinalSymbol("TestID", ID.TestID);

  /**
   * TestReport("file-name-string") - load the unit tests from a `file-name-string` and print a
   * summary of the `VerificationTest` included in the file.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TestReport.md">TestReport
   *     documentation</a>
   */
  public static final IBuiltInSymbol TestReport = initFinalSymbol("TestReport", ID.TestReport);

  public static final IBuiltInSymbol TestReportObject =
      initFinalSymbol("TestReportObject", ID.TestReportObject);

  /**
   * TestResultObject( ... ) - is an association wrapped in a `TestResultObject`returned from
   * `VerificationTest` which stores the results from executing a single unit test.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TestResultObject.md">TestResultObject
   *     documentation</a>
   */
  public static final IBuiltInSymbol TestResultObject =
      initFinalSymbol("TestResultObject", ID.TestResultObject);

  public static final IBuiltInSymbol Tetrahedron = initFinalSymbol("Tetrahedron", ID.Tetrahedron);

  public static final IBuiltInSymbol Text = initFinalSymbol("Text", ID.Text);

  public static final IBuiltInSymbol TextCell = initFinalSymbol("TextCell", ID.TextCell);

  public static final IBuiltInSymbol TextString = initFinalSymbol("TextString", ID.TextString);

  public static final IBuiltInSymbol Thickness = initFinalSymbol("Thickness", ID.Thickness);

  /**
   * Thread(f(args) - threads `f` over any lists that appear in `args`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Thread.md">Thread
   *     documentation</a>
   */
  public static final IBuiltInSymbol Thread = initFinalSymbol("Thread", ID.Thread);

  /**
   * Through(p(f)[x]) - gives `p(f(x))`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Through.md">Through
   *     documentation</a>
   */
  public static final IBuiltInSymbol Through = initFinalSymbol("Through", ID.Through);

  public static final IBuiltInSymbol Throw = initFinalSymbol("Throw", ID.Throw);

  /**
   * TimeConstrained(expression, seconds) - stop evaluation of `expression` if time measurement of
   * the evaluation exceeds `seconds` and return `$Aborted`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeConstrained.md">TimeConstrained
   *     documentation</a>
   */
  public static final IBuiltInSymbol TimeConstrained =
      initFinalSymbol("TimeConstrained", ID.TimeConstrained);

  public static final IBuiltInSymbol TimeObject = initFinalSymbol("TimeObject", ID.TimeObject);

  public static final IBuiltInSymbol TimeRemaining =
      initFinalSymbol("TimeRemaining", ID.TimeRemaining);

  /**
   * TimeValue(p, i, n) - returns a time value calculation.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeValue.md">TimeValue
   *     documentation</a>
   */
  public static final IBuiltInSymbol TimeValue = initFinalSymbol("TimeValue", ID.TimeValue);

  /**
   * Times(a, b, ...) - represents the product of the terms `a, b, ...`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times
   *     documentation</a>
   */
  public static final IBuiltInSymbol Times = initFinalSymbol("Times", ID.Times);

  /**
   * TimesBy(x, dx) - is equivalent to `x = x * dx`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimesBy.md">TimesBy
   *     documentation</a>
   */
  public static final IBuiltInSymbol TimesBy = initFinalSymbol("TimesBy", ID.TimesBy);

  /**
   * Timing(x) - returns a list with the first entry containing the evaluation CPU time of `x` and
   * the second entry is the evaluation result of `x`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Timing.md">Timing
   *     documentation</a>
   */
  public static final IBuiltInSymbol Timing = initFinalSymbol("Timing", ID.Timing);

  public static final IBuiltInSymbol ToBoxes = initFinalSymbol("ToBoxes", ID.ToBoxes);

  /**
   * ToCharacterCode(string) - converts `string` into a list of corresponding integer character
   * codes.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToCharacterCode.md">ToCharacterCode
   *     documentation</a>
   */
  public static final IBuiltInSymbol ToCharacterCode =
      initFinalSymbol("ToCharacterCode", ID.ToCharacterCode);

  /**
   * ToExpression("string", form) - converts the `string` given in `form` into an expression.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToExpression.md">ToExpression
   *     documentation</a>
   */
  public static final IBuiltInSymbol ToExpression =
      initFinalSymbol("ToExpression", ID.ToExpression);

  /**
   * ToLowerCase(string) - converts `string` into a string of corresponding lowercase character
   * codes.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToLowerCase.md">ToLowerCase
   *     documentation</a>
   */
  public static final IBuiltInSymbol ToLowerCase = initFinalSymbol("ToLowerCase", ID.ToLowerCase);

  /**
   * ToPolarCoordinates({x, y}) - return the polar coordinates for the cartesian coordinates `{x,
   * y}`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToPolarCoordinates.md">ToPolarCoordinates
   *     documentation</a>
   */
  public static final IBuiltInSymbol ToPolarCoordinates =
      initFinalSymbol("ToPolarCoordinates", ID.ToPolarCoordinates);

  public static final IBuiltInSymbol ToRadicals = initFinalSymbol("ToRadicals", ID.ToRadicals);

  /**
   * ToString(expr) - converts `expr` into a string.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToString.md">ToString
   *     documentation</a>
   */
  public static final IBuiltInSymbol ToString = initFinalSymbol("ToString", ID.ToString);

  /**
   * ToUnicode(string) - converts `string` into a string of corresponding unicode character codes.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToUnicode.md">ToUnicode
   *     documentation</a>
   */
  public static final IBuiltInSymbol ToUnicode = initFinalSymbol("ToUnicode", ID.ToUnicode);

  /**
   * ToUpperCase(string) - converts `string` into a string of corresponding uppercase character
   * codes.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToUpperCase.md">ToUpperCase
   *     documentation</a>
   */
  public static final IBuiltInSymbol ToUpperCase = initFinalSymbol("ToUpperCase", ID.ToUpperCase);

  public static final IBuiltInSymbol Today = initFinalSymbol("Today", ID.Today);

  /**
   * ToeplitzMatrix(n) - gives a toeplitz matrix with the dimension `n`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToeplitzMatrix.md">ToeplitzMatrix
   *     documentation</a>
   */
  public static final IBuiltInSymbol ToeplitzMatrix =
      initFinalSymbol("ToeplitzMatrix", ID.ToeplitzMatrix);

  /**
   * Together(expr) - writes sums of fractions in `expr` together.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Together.md">Together
   *     documentation</a>
   */
  public static final IBuiltInSymbol Together = initFinalSymbol("Together", ID.Together);

  public static final IBuiltInSymbol TooLarge = initFinalSymbol("TooLarge", ID.TooLarge);

  public static final IBuiltInSymbol Top = initFinalSymbol("Top", ID.Top);

  /**
   * Total(list) - adds all values in `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Total.md">Total
   *     documentation</a>
   */
  public static final IBuiltInSymbol Total = initFinalSymbol("Total", ID.Total);

  /**
   * Tr(matrix) - computes the trace of the `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tr.md">Tr
   *     documentation</a>
   */
  public static final IBuiltInSymbol Tr = initFinalSymbol("Tr", ID.Tr);

  /**
   * Trace(expr) - return the evaluation steps which are used to get the result.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Trace.md">Trace
   *     documentation</a>
   */
  public static final IBuiltInSymbol Trace = initFinalSymbol("Trace", ID.Trace);

  public static final IBuiltInSymbol TraceForm = initFinalSymbol("TraceForm", ID.TraceForm);

  public static final IBuiltInSymbol TraditionalForm =
      initFinalSymbol("TraditionalForm", ID.TraditionalForm);

  /**
   * Transliterate("string") - try converting the given string to a similar ASCII string
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Transliterate.md">Transliterate
   *     documentation</a>
   */
  public static final IBuiltInSymbol Transliterate =
      initFinalSymbol("Transliterate", ID.Transliterate);

  /**
   * Transpose(m) - transposes rows and columns in the matrix `m`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Transpose.md">Transpose
   *     documentation</a>
   */
  public static final IBuiltInSymbol Transpose = initFinalSymbol("Transpose", ID.Transpose);

  /**
   * TreeForm(expr) - create a tree visualization from the given expression `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TreeForm.md">TreeForm
   *     documentation</a>
   */
  public static final IBuiltInSymbol TreeForm = initFinalSymbol("TreeForm", ID.TreeForm);

  public static final IBuiltInSymbol Triangle = initFinalSymbol("Triangle", ID.Triangle);

  public static final IBuiltInSymbol Trig = initFinalSymbol("Trig", ID.Trig);

  /**
   * TrigExpand(expr) - expands out trigonometric expressions in `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigExpand.md">TrigExpand
   *     documentation</a>
   */
  public static final IBuiltInSymbol TrigExpand = initFinalSymbol("TrigExpand", ID.TrigExpand);

  /**
   * TrigReduce(expr) - rewrites products and powers of trigonometric functions in `expr` in terms
   * of trigonometric functions with combined arguments.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigReduce.md">TrigReduce
   *     documentation</a>
   */
  public static final IBuiltInSymbol TrigReduce = initFinalSymbol("TrigReduce", ID.TrigReduce);

  /**
   * TrigToExp(expr) - converts trigonometric functions in `expr` to exponentials.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigToExp.md">TrigToExp
   *     documentation</a>
   */
  public static final IBuiltInSymbol TrigToExp = initFinalSymbol("TrigToExp", ID.TrigToExp);

  /**
   * True - the constant `True` represents the boolean value **true**
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/True.md">True
   *     documentation</a>
   */
  public static final IBuiltInSymbol True = initFinalSymbol("True", ID.True);

  /**
   * TrueQ(expr) - returns `True` if and only if `expr` is `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrueQ.md">TrueQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol TrueQ = initFinalSymbol("TrueQ", ID.TrueQ);

  public static final IBuiltInSymbol Tube = initFinalSymbol("Tube", ID.Tube);

  public static final IBuiltInSymbol TukeyWindow = initFinalSymbol("TukeyWindow", ID.TukeyWindow);

  /**
   * Tuples(list, n) - creates a list of all `n`-tuples of elements in `list`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tuples.md">Tuples
   *     documentation</a>
   */
  public static final IBuiltInSymbol Tuples = initFinalSymbol("Tuples", ID.Tuples);

  public static final IBuiltInSymbol TwoWayRule = initFinalSymbol("TwoWayRule", ID.TwoWayRule);

  public static final IBuiltInSymbol URLFetch = initFinalSymbol("URLFetch", ID.URLFetch);

  public static final IBuiltInSymbol Undefined = initFinalSymbol("Undefined", ID.Undefined);

  public static final IBuiltInSymbol Underoverscript =
      initFinalSymbol("Underoverscript", ID.Underoverscript);

  /**
   * UndirectedEdge(a, b) - is an undirected edge between the vertices `a` and `b` in a `graph`
   * object.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UndirectedEdge.md">UndirectedEdge
   *     documentation</a>
   */
  public static final IBuiltInSymbol UndirectedEdge =
      initFinalSymbol("UndirectedEdge", ID.UndirectedEdge);

  /**
   * Unequal(x, y) - yields `False` if `x` and `y` are known to be equal, or `True` if `x` and `y`
   * are known to be unequal.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unequal.md">Unequal
   *     documentation</a>
   */
  public static final IBuiltInSymbol Unequal = initFinalSymbol("Unequal", ID.Unequal);

  public static final IBuiltInSymbol UnequalTo = initFinalSymbol("UnequalTo", ID.UnequalTo);

  /**
   * Unevaluated(expr) - temporarily leaves `expr` in an unevaluated form when it appears as a
   * function argument.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unevaluated.md">Unevaluated
   *     documentation</a>
   */
  public static final IBuiltInSymbol Unevaluated = initFinalSymbol("Unevaluated", ID.Unevaluated);

  /**
   * UniformDistribution({min, max}) - returns a uniform distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UniformDistribution.md">UniformDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol UniformDistribution =
      initFinalSymbol("UniformDistribution", ID.UniformDistribution);

  /**
   * Union(set1, set2) - get the union set from `set1` and `set2`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Union.md">Union
   *     documentation</a>
   */
  public static final IBuiltInSymbol Union = initFinalSymbol("Union", ID.Union);

  /**
   * Unique(expr) - create a unique symbol of the form `expr$...`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unique.md">Unique
   *     documentation</a>
   */
  public static final IBuiltInSymbol Unique = initFinalSymbol("Unique", ID.Unique);

  /**
   * UnitConvert(quantity) - convert the `quantity` to the base unit
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitConvert.md">UnitConvert
   *     documentation</a>
   */
  public static final IBuiltInSymbol UnitConvert = initFinalSymbol("UnitConvert", ID.UnitConvert);

  /**
   * UnitStep(expr) - returns `0`, if `expr` is less than `0` and returns `1`, if `expr` is greater
   * equal than `0`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitStep.md">UnitStep
   *     documentation</a>
   */
  public static final IBuiltInSymbol UnitStep = initFinalSymbol("UnitStep", ID.UnitStep);

  public static final IBuiltInSymbol UnitTriangle =
      initFinalSymbol("UnitTriangle", ID.UnitTriangle);

  /**
   * UnitVector(position) - returns a unit vector with element `1` at the given `position`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitVector.md">UnitVector
   *     documentation</a>
   */
  public static final IBuiltInSymbol UnitVector = initFinalSymbol("UnitVector", ID.UnitVector);

  public static final IBuiltInSymbol UnitaryMatrixQ =
      initFinalSymbol("UnitaryMatrixQ", ID.UnitaryMatrixQ);

  /**
   * Unitize(expr) - maps a non-zero `expr` to `1`, and a zero `expr` to `0`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unitize.md">Unitize
   *     documentation</a>
   */
  public static final IBuiltInSymbol Unitize = initFinalSymbol("Unitize", ID.Unitize);

  public static final IBuiltInSymbol Unknown = initFinalSymbol("Unknown", ID.Unknown);

  public static final IBuiltInSymbol Unprotect = initFinalSymbol("Unprotect", ID.Unprotect);

  /**
   * UnsameQ(x, y) - returns `True` if `x` and `y` are not structurally identical.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnsameQ.md">UnsameQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol UnsameQ = initFinalSymbol("UnsameQ", ID.UnsameQ);

  /**
   * Unset(expr) - removes any definitions belonging to the left-hand-side `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unset.md">Unset
   *     documentation</a>
   */
  public static final IBuiltInSymbol Unset = initFinalSymbol("Unset", ID.Unset);

  public static final IBuiltInSymbol UpSet = initFinalSymbol("UpSet", ID.UpSet);

  public static final IBuiltInSymbol UpSetDelayed =
      initFinalSymbol("UpSetDelayed", ID.UpSetDelayed);

  public static final IBuiltInSymbol UpTo = initFinalSymbol("UpTo", ID.UpTo);

  /**
   * UpValues(symbol) - prints the up-value rules associated with `symbol`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpValues.md">UpValues
   *     documentation</a>
   */
  public static final IBuiltInSymbol UpValues = initFinalSymbol("UpValues", ID.UpValues);

  /**
   * UpperCaseQ(str) - is `True` if the given `str` is a string which only contains upper case
   * characters.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperCaseQ.md">UpperCaseQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol UpperCaseQ = initFinalSymbol("UpperCaseQ", ID.UpperCaseQ);

  /**
   * UpperTriangularize(matrix) - create a upper triangular matrix from the given `matrix`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperTriangularize.md">UpperTriangularize
   *     documentation</a>
   */
  public static final IBuiltInSymbol UpperTriangularize =
      initFinalSymbol("UpperTriangularize", ID.UpperTriangularize);

  public static final IBuiltInSymbol UseTypeChecking =
      initFinalSymbol("UseTypeChecking", ID.UseTypeChecking);

  /**
   * ValueQ(expr) - returns `True` if and only if `expr` is defined.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ValueQ.md">ValueQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol ValueQ = initFinalSymbol("ValueQ", ID.ValueQ);

  /**
   * Values(association) - return a list of values of the `association`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Values.md">Values
   *     documentation</a>
   */
  public static final IBuiltInSymbol Values = initFinalSymbol("Values", ID.Values);

  /**
   * VandermondeMatrix(n) - gives the Vandermonde matrix with `n` rows and columns.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VandermondeMatrix.md">VandermondeMatrix
   *     documentation</a>
   */
  public static final IBuiltInSymbol VandermondeMatrix =
      initFinalSymbol("VandermondeMatrix", ID.VandermondeMatrix);

  public static final IBuiltInSymbol Variable = initFinalSymbol("Variable", ID.Variable);

  /**
   * Variables(expr) - gives a list of the variables that appear in the polynomial `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Variables.md">Variables
   *     documentation</a>
   */
  public static final IBuiltInSymbol Variables = initFinalSymbol("Variables", ID.Variables);

  /**
   * Variance(list) - computes the variance of `list`. `list` may consist of numerical values or
   * symbols. Numerical values may be real or complex.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Variance.md">Variance
   *     documentation</a>
   */
  public static final IBuiltInSymbol Variance = initFinalSymbol("Variance", ID.Variance);

  /**
   * VectorAngle(u, v) - gives the angles between vectors `u` and `v`
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorAngle.md">VectorAngle
   *     documentation</a>
   */
  public static final IBuiltInSymbol VectorAngle = initFinalSymbol("VectorAngle", ID.VectorAngle);

  /**
   * VectorQ(v) - returns `True` if `v` is a list of elements which are not themselves lists.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorQ.md">VectorQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol VectorQ = initFinalSymbol("VectorQ", ID.VectorQ);

  public static final IBuiltInSymbol Vectors = initFinalSymbol("Vectors", ID.Vectors);

  /**
   * Verbatim(expr) - prevents pattern constructs in `expr` from taking effect, allowing them to
   * match themselves.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Verbatim.md">Verbatim
   *     documentation</a>
   */
  public static final IBuiltInSymbol Verbatim = initFinalSymbol("Verbatim", ID.Verbatim);

  /**
   * VerificationTest(test-expr) - create a `TestResultObject` by testing if `test-expr` evaluates
   * to `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VerificationTest.md">VerificationTest
   *     documentation</a>
   */
  public static final IBuiltInSymbol VerificationTest =
      initFinalSymbol("VerificationTest", ID.VerificationTest);

  /**
   * VertexEccentricity(graph, vertex) - compute the eccentricity of `vertex` in the `graph`. It's
   * the length of the longest shortest path from the `vertex` to every other vertex in the `graph`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexEccentricity.md">VertexEccentricity
   *     documentation</a>
   */
  public static final IBuiltInSymbol VertexEccentricity =
      initFinalSymbol("VertexEccentricity", ID.VertexEccentricity);

  public static final IBuiltInSymbol VertexLabels =
      initFinalSymbol("VertexLabels", ID.VertexLabels);

  /**
   * VertexList(graph) - convert the `graph` into a list of vertices.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexList.md">VertexList
   *     documentation</a>
   */
  public static final IBuiltInSymbol VertexList = initFinalSymbol("VertexList", ID.VertexList);

  /**
   * VertexQ(graph, vertex) - test if `vertex` is a vertex in the `graph` object.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexQ.md">VertexQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol VertexQ = initFinalSymbol("VertexQ", ID.VertexQ);

  public static final IBuiltInSymbol VertexShapeFunction =
      initFinalSymbol("VertexShapeFunction", ID.VertexShapeFunction);

  public static final IBuiltInSymbol VertexSize = initFinalSymbol("VertexSize", ID.VertexSize);

  public static final IBuiltInSymbol VertexStyle = initFinalSymbol("VertexStyle", ID.VertexStyle);

  public static final IBuiltInSymbol ViewPoint = initFinalSymbol("ViewPoint", ID.ViewPoint);

  public static final IBuiltInSymbol Volume = initFinalSymbol("Volume", ID.Volume);

  /**
   * WeibullDistribution(a, b) - returns a Weibull distribution.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeibullDistribution.md">WeibullDistribution
   *     documentation</a>
   */
  public static final IBuiltInSymbol WeibullDistribution =
      initFinalSymbol("WeibullDistribution", ID.WeibullDistribution);

  public static final IBuiltInSymbol WeierstrassHalfPeriods =
      initFinalSymbol("WeierstrassHalfPeriods", ID.WeierstrassHalfPeriods);

  public static final IBuiltInSymbol WeierstrassInvariants =
      initFinalSymbol("WeierstrassInvariants", ID.WeierstrassInvariants);

  /**
   * WeierstrassP(expr, {n1, n2}) - Weierstrass elliptic function.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeierstrassP.md">WeierstrassP
   *     documentation</a>
   */
  public static final IBuiltInSymbol WeierstrassP =
      initFinalSymbol("WeierstrassP", ID.WeierstrassP);

  public static final IBuiltInSymbol WeierstrassPPrime =
      initFinalSymbol("WeierstrassPPrime", ID.WeierstrassPPrime);

  /**
   * WeightedAdjacencyMatrix(graph) - convert the `graph` into a weighted adjacency matrix in sparse
   * array format.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeightedAdjacencyMatrix.md">WeightedAdjacencyMatrix
   *     documentation</a>
   */
  public static final IBuiltInSymbol WeightedAdjacencyMatrix =
      initFinalSymbol("WeightedAdjacencyMatrix", ID.WeightedAdjacencyMatrix);

  public static final IBuiltInSymbol WeightedData =
      initFinalSymbol("WeightedData", ID.WeightedData);

  /**
   * WeightedGraphQ(expr) - test if `expr` is an explicit weighted graph object.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeightedGraphQ.md">WeightedGraphQ
   *     documentation</a>
   */
  public static final IBuiltInSymbol WeightedGraphQ =
      initFinalSymbol("WeightedGraphQ", ID.WeightedGraphQ);

  public static final IBuiltInSymbol WheelGraph = initFinalSymbol("WheelGraph", ID.WheelGraph);

  /**
   * Which(cond1, expr1, cond2, expr2, ...) - yields `expr1` if `cond1` evaluates to `True`, `expr2`
   * if `cond2` evaluates to `True`, etc.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Which.md">Which
   *     documentation</a>
   */
  public static final IBuiltInSymbol Which = initFinalSymbol("Which", ID.Which);

  /**
   * While(test, body) - evaluates `body` as long as test evaluates to `True`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/While.md">While
   *     documentation</a>
   */
  public static final IBuiltInSymbol While = initFinalSymbol("While", ID.While);

  public static final IBuiltInSymbol White = initFinalSymbol("White", ID.White);

  /**
   * Whitespace - represents a sequence of whitespace characters.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Whitespace.md">Whitespace
   *     documentation</a>
   */
  public static final IBuiltInSymbol Whitespace = initFinalSymbol("Whitespace", ID.Whitespace);

  /**
   * WhitespaceCharacter - represents a single whitespace character.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WhitespaceCharacter.md">WhitespaceCharacter
   *     documentation</a>
   */
  public static final IBuiltInSymbol WhitespaceCharacter =
      initFinalSymbol("WhitespaceCharacter", ID.WhitespaceCharacter);

  public static final IBuiltInSymbol WhittakerM = initFinalSymbol("WhittakerM", ID.WhittakerM);

  public static final IBuiltInSymbol WhittakerW = initFinalSymbol("WhittakerW", ID.WhittakerW);

  /**
   * With({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables` by
   * replacing the local variables in `expr`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/With.md">With
   *     documentation</a>
   */
  public static final IBuiltInSymbol With = initFinalSymbol("With", ID.With);

  public static final IBuiltInSymbol Word = initFinalSymbol("Word", ID.Word);

  /**
   * WordBoundary - represents the boundary between words.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WordBoundary.md">WordBoundary
   *     documentation</a>
   */
  public static final IBuiltInSymbol WordBoundary =
      initFinalSymbol("WordBoundary", ID.WordBoundary);

  /**
   * WordCharacter] - represents a single letter or digit character.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WordCharacter.md">WordCharacter
   *     documentation</a>
   */
  public static final IBuiltInSymbol WordCharacter =
      initFinalSymbol("WordCharacter", ID.WordCharacter);

  public static final IBuiltInSymbol WordSeparators =
      initFinalSymbol("WordSeparators", ID.WordSeparators);

  public static final IBuiltInSymbol Write = initFinalSymbol("Write", ID.Write);

  public static final IBuiltInSymbol WriteString = initFinalSymbol("WriteString", ID.WriteString);

  /**
   * Xor(arg1, arg2, ...) - Logical XOR (exclusive OR) function. Returns `True` if an odd number of
   * the arguments are `True` and the rest are `False`. Returns `False` if an even number of the
   * arguments are `True` and the rest are `False`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Xor.md">Xor
   *     documentation</a>
   */
  public static final IBuiltInSymbol Xor = initFinalSymbol("Xor", ID.Xor);

  public static final IBuiltInSymbol Yellow = initFinalSymbol("Yellow", ID.Yellow);

  /**
   * YuleDissimilarity(u, v) - returns the Yule dissimilarity between the two boolean 1-D lists `u`
   * and `v`, which is defined as `R / (c_tt * c_ff + R / 2)` where `n` is `len(u)`, `c_ij` is the
   * number of occurrences of `u(k)=i` and `v(k)=j` for `k<n`, and `R = 2 * c_tf * c_ft`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/YuleDissimilarity.md">YuleDissimilarity
   *     documentation</a>
   */
  public static final IBuiltInSymbol YuleDissimilarity =
      initFinalSymbol("YuleDissimilarity", ID.YuleDissimilarity);

  public static final IBuiltInSymbol ZeroSymmetric =
      initFinalSymbol("ZeroSymmetric", ID.ZeroSymmetric);

  public static final IBuiltInSymbol ZeroTest = initFinalSymbol("ZeroTest", ID.ZeroTest);

  /**
   * Zeta(z) - returns the Riemann zeta function of `z`.
   *
   * @see <a
   *     href="https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Zeta.md">Zeta
   *     documentation</a>
   */
  public static final IBuiltInSymbol Zeta = initFinalSymbol("Zeta", ID.Zeta);

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
    final ISymbol symbol = new Symbol(symbolName, org.matheclipse.core.expression.Context.DUMMY);
    // TODO make this a real protected symbol
    //    symbol.setAttributes(ISymbol.PROTECTED);
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
    return domain == Algebraics
        || domain == Booleans
        || domain == Complexes
        || domain == Integers
        || domain == Primes
        || domain == Rationals
        || domain == Reals;
  }
}
