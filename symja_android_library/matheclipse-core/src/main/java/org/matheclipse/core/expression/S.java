package org.matheclipse.core.expression;

import java.util.IdentityHashMap;
import java.util.Locale;
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


  public final static IBuiltInSymbol $Aborted = S.initFinalSymbol("$Aborted", ID.$Aborted);

  /**
   * $Assumptions - contains the default assumptions for `Integrate`, `Refine` and `Simplify`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$Assumptions.md">$Assumptions
   *      documentation</a>
   */
  public final static IBuiltInSymbol $Assumptions =
      S.initFinalSymbol("$Assumptions", ID.$Assumptions);

  public final static IBuiltInSymbol $BaseDirectory =
      S.initFinalSymbol("$BaseDirectory", ID.$BaseDirectory);

  public final static IBuiltInSymbol $Cancel = S.initFinalSymbol("$Cancel", ID.$Cancel);

  public final static IBuiltInSymbol $CharacterEncoding =
      S.initFinalSymbol("$CharacterEncoding", ID.$CharacterEncoding);

  public final static IBuiltInSymbol $Context = S.initFinalSymbol("$Context", ID.$Context);

  public final static IBuiltInSymbol $ContextPath =
      S.initFinalSymbol("$ContextPath", ID.$ContextPath);

  public final static IBuiltInSymbol $CreationDate =
      S.initFinalSymbol("$CreationDate", ID.$CreationDate);

  public final static IBuiltInSymbol $DisplayFunction =
      S.initFinalSymbol("$DisplayFunction", ID.$DisplayFunction);

  public final static IBuiltInSymbol $Failed = S.initFinalSymbol("$Failed", ID.$Failed);

  /**
   * $HistoryLength - specifies the maximum number of `In` and `Out` entries.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$HistoryLength.md">$HistoryLength
   *      documentation</a>
   */
  public final static IBuiltInSymbol $HistoryLength =
      S.initFinalSymbol("$HistoryLength", ID.$HistoryLength);

  public final static IBuiltInSymbol $HomeDirectory =
      S.initFinalSymbol("$HomeDirectory", ID.$HomeDirectory);

  public final static IBuiltInSymbol $IdentityMatrix =
      S.initFinalSymbol("$IdentityMatrix", ID.$IdentityMatrix);

  public final static IBuiltInSymbol $Input = S.initFinalSymbol("$Input", ID.$Input);

  public final static IBuiltInSymbol $InputFileName =
      S.initFinalSymbol("$InputFileName", ID.$InputFileName);

  /**
   * $IterationLimit - specifies the maximum number of times a reevaluation of an expression may
   * happen.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$IterationLimit.md">$IterationLimit
   *      documentation</a>
   */
  public final static IBuiltInSymbol $IterationLimit =
      S.initFinalSymbol("$IterationLimit", ID.$IterationLimit);

  /**
   * $Line - holds the current input line number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$Line.md">$Line
   *      documentation</a>
   */
  public final static IBuiltInSymbol $Line = S.initFinalSymbol("$Line", ID.$Line);

  public final static IBuiltInSymbol $MachineEpsilon =
      S.initFinalSymbol("$MachineEpsilon", ID.$MachineEpsilon);

  public final static IBuiltInSymbol $MachinePrecision =
      S.initFinalSymbol("$MachinePrecision", ID.$MachinePrecision);

  /**
   * $MaxMachineNumber - return the largest positive finite Java `double` value (`Double.MAX_VALUE`
   * approx. `1.7976931348623157*^308`)
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$MaxMachineNumber.md">$MaxMachineNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol $MaxMachineNumber =
      S.initFinalSymbol("$MaxMachineNumber", ID.$MaxMachineNumber);

  public final static IBuiltInSymbol $MessageList =
      S.initFinalSymbol("$MessageList", ID.$MessageList);

  /**
   * $MinMachineNumber - return the smallest positive normal Java `double` value
   * (`Double.MIN_NORMAL` approx. 2.2250738585072014*^308)
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$MinMachineNumber.md">$MinMachineNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol $MinMachineNumber =
      S.initFinalSymbol("$MinMachineNumber", ID.$MinMachineNumber);

  public final static IBuiltInSymbol $Notebooks = S.initFinalSymbol("$Notebooks", ID.$Notebooks);

  /**
   * $OperatingSystem - gives the type of operating system ("Windows", "MacOSX", or "Unix") running
   * Symja.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$OperatingSystem.md">$OperatingSystem
   *      documentation</a>
   */
  public final static IBuiltInSymbol $OperatingSystem =
      S.initFinalSymbol("$OperatingSystem", ID.$OperatingSystem);

  public final static IBuiltInSymbol $OutputSizeLimit =
      S.initFinalSymbol("$OutputSizeLimit", ID.$OutputSizeLimit);

  public final static IBuiltInSymbol $Packages = S.initFinalSymbol("$Packages", ID.$Packages);

  public final static IBuiltInSymbol $Path = S.initFinalSymbol("$Path", ID.$Path);

  public final static IBuiltInSymbol $PathnameSeparator =
      S.initFinalSymbol("$PathnameSeparator", ID.$PathnameSeparator);

  public final static IBuiltInSymbol $PrePrint = S.initFinalSymbol("$PrePrint", ID.$PrePrint);

  public final static IBuiltInSymbol $PreRead = S.initFinalSymbol("$PreRead", ID.$PreRead);

  /**
   * $RecursionLimit - holds the current input line number
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$RecursionLimit.md">$RecursionLimit
   *      documentation</a>
   */
  public final static IBuiltInSymbol $RecursionLimit =
      S.initFinalSymbol("$RecursionLimit", ID.$RecursionLimit);

  public final static IBuiltInSymbol $RootDirectory =
      S.initFinalSymbol("$RootDirectory", ID.$RootDirectory);

  public final static IBuiltInSymbol $Scaling = S.initFinalSymbol("$Scaling", ID.$Scaling);

  /**
   * $ScriptCommandLine - is a list of string arguments when running Symja in script mode. The list
   * starts with the name of the script.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$ScriptCommandLine.md">$ScriptCommandLine
   *      documentation</a>
   */
  public final static IBuiltInSymbol $ScriptCommandLine =
      S.initFinalSymbol("$ScriptCommandLine", ID.$ScriptCommandLine);

  public final static IBuiltInSymbol $SingleEntryMatrix =
      S.initFinalSymbol("$SingleEntryMatrix", ID.$SingleEntryMatrix);

  public final static IBuiltInSymbol $SystemCharacterEncoding =
      S.initFinalSymbol("$SystemCharacterEncoding", ID.$SystemCharacterEncoding);

  public final static IBuiltInSymbol $SystemMemory =
      S.initFinalSymbol("$SystemMemory", ID.$SystemMemory);

  public final static IBuiltInSymbol $TemporaryDirectory =
      S.initFinalSymbol("$TemporaryDirectory", ID.$TemporaryDirectory);

  public final static IBuiltInSymbol $UserBaseDirectory =
      S.initFinalSymbol("$UserBaseDirectory", ID.$UserBaseDirectory);

  public final static IBuiltInSymbol $UserName = S.initFinalSymbol("$UserName", ID.$UserName);

  public final static IBuiltInSymbol $Version = S.initFinalSymbol("$Version", ID.$Version);

  /**
   * AASTriangle(alpha, beta, a) - returns a triangle from 2 angles `alpha`, `beta` and side `a`
   * (which is not between the angles).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AASTriangle.md">AASTriangle
   *      documentation</a>
   */
  public final static IBuiltInSymbol AASTriangle = S.initFinalSymbol("AASTriangle", ID.AASTriangle);

  /**
   * Abort() - aborts an evaluation completely and returns `$Aborted`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Abort.md">Abort
   *      documentation</a>
   */
  public final static IBuiltInSymbol Abort = S.initFinalSymbol("Abort", ID.Abort);

  /**
   * Abs(expr) - returns the absolute value of the real or complex number `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Abs.md">Abs
   *      documentation</a>
   */
  public final static IBuiltInSymbol Abs = S.initFinalSymbol("Abs", ID.Abs);

  /**
   * AbsArg(expr) - returns a list of 2 values of the complex number `Abs(expr), Arg(expr)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AbsArg.md">AbsArg
   *      documentation</a>
   */
  public final static IBuiltInSymbol AbsArg = S.initFinalSymbol("AbsArg", ID.AbsArg);

  public final static IBuiltInSymbol AbsoluteCorrelation =
      S.initFinalSymbol("AbsoluteCorrelation", ID.AbsoluteCorrelation);

  public final static IBuiltInSymbol AbsoluteTime =
      S.initFinalSymbol("AbsoluteTime", ID.AbsoluteTime);

  /**
   * AbsoluteTiming(x) - returns a list with the first entry containing the evaluation time of `x`
   * and the second entry is the evaluation result of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AbsoluteTiming.md">AbsoluteTiming
   *      documentation</a>
   */
  public final static IBuiltInSymbol AbsoluteTiming =
      S.initFinalSymbol("AbsoluteTiming", ID.AbsoluteTiming);

  /**
   * Accumulate(list) - accumulate the values of `list` returning a new list.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Accumulate.md">Accumulate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Accumulate = S.initFinalSymbol("Accumulate", ID.Accumulate);

  public final static IBuiltInSymbol AccuracyGoal =
      S.initFinalSymbol("AccuracyGoal", ID.AccuracyGoal);

  /**
   * AddSides(compare-expr, value) - add `value` to all elements of the `compare-expr`.
   * `compare-expr` can be `True`, `False` or an comparison expression with head `Equal, Unequal,
   * Less, LessEqual, Greater, GreaterEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AddSides.md">AddSides
   *      documentation</a>
   */
  public final static IBuiltInSymbol AddSides = S.initFinalSymbol("AddSides", ID.AddSides);

  /**
   * AddTo(x, dx) - is equivalent to `x = x + dx`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AddTo.md">AddTo
   *      documentation</a>
   */
  public final static IBuiltInSymbol AddTo = S.initFinalSymbol("AddTo", ID.AddTo);

  public final static IBuiltInSymbol AddToClassPath =
      S.initFinalSymbol("AddToClassPath", ID.AddToClassPath);

  /**
   * AdjacencyMatrix(graph) - convert the `graph` into a adjacency matrix in sparse array format.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AdjacencyMatrix.md">AdjacencyMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol AdjacencyMatrix =
      S.initFinalSymbol("AdjacencyMatrix", ID.AdjacencyMatrix);

  /**
   * Adjugate(matrix) - calculate the adjugate matrix `Inverse(matrix)*Det(matrix)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Adjugate.md">Adjugate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Adjugate = S.initFinalSymbol("Adjugate", ID.Adjugate);

  /**
   * AiryAi(z) - returns the Airy function of the first kind of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryAi.md">AiryAi
   *      documentation</a>
   */
  public final static IBuiltInSymbol AiryAi = S.initFinalSymbol("AiryAi", ID.AiryAi);

  /**
   * AiryAiPrime(z) - returns the derivative of the `AiryAi` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryAiPrime.md">AiryAiPrime
   *      documentation</a>
   */
  public final static IBuiltInSymbol AiryAiPrime = S.initFinalSymbol("AiryAiPrime", ID.AiryAiPrime);

  /**
   * AiryBi(z) - returns the Airy function of the second kind of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryBi.md">AiryBi
   *      documentation</a>
   */
  public final static IBuiltInSymbol AiryBi = S.initFinalSymbol("AiryBi", ID.AiryBi);

  /**
   * AiryBiPrime(z) - returns the derivative of the `AiryBi` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AiryBiPrime.md">AiryBiPrime
   *      documentation</a>
   */
  public final static IBuiltInSymbol AiryBiPrime = S.initFinalSymbol("AiryBiPrime", ID.AiryBiPrime);

  public final static IBuiltInSymbol AlgebraicNumber =
      S.initFinalSymbol("AlgebraicNumber", ID.AlgebraicNumber);

  public final static IBuiltInSymbol Algebraics = S.initFinalSymbol("Algebraics", ID.Algebraics);

  /**
   * All - is a value for a number of functions indicating to include everything. For example it is
   * a possible value for `Span`, `Part` and `Quiet`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/All.md">All
   *      documentation</a>
   */
  public final static IBuiltInSymbol All = S.initFinalSymbol("All", ID.All);

  public final static IBuiltInSymbol AllowedHeads =
      S.initFinalSymbol("AllowedHeads", ID.AllowedHeads);

  public final static IBuiltInSymbol AllowShortContext =
      S.initFinalSymbol("AllowShortContext", ID.AllowShortContext);

  /**
   * AllTrue({expr1, expr2, ...}, test) - returns `True` if all applications of `test` to `expr1,
   * expr2, ...` evaluate to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AllTrue.md">AllTrue
   *      documentation</a>
   */
  public final static IBuiltInSymbol AllTrue = S.initFinalSymbol("AllTrue", ID.AllTrue);

  /**
   * Alphabet() - gives the list of lowercase letters `a-z` in the English or Latin alphabet .
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Alphabet.md">Alphabet
   *      documentation</a>
   */
  public final static IBuiltInSymbol Alphabet = S.initFinalSymbol("Alphabet", ID.Alphabet);

  /**
   * Alternatives(p1, p2, ..., p_i) - is a pattern that matches any of the patterns `p1, p2,....,
   * p_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Alternatives.md">Alternatives
   *      documentation</a>
   */
  public final static IBuiltInSymbol Alternatives =
      S.initFinalSymbol("Alternatives", ID.Alternatives);

  public final static IBuiltInSymbol AmbientLight =
      S.initFinalSymbol("AmbientLight", ID.AmbientLight);

  /**
   * And(expr1, expr2, ...) - `expr1 && expr2 && ...` evaluates each expression in turn, returning
   * `False` as soon as an expression evaluates to `False`. If all expressions evaluate to `True`,
   * `And` returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/And.md">And
   *      documentation</a>
   */
  public final static IBuiltInSymbol And = S.initFinalSymbol("And", ID.And);

  public final static IBuiltInSymbol AngerJ = S.initFinalSymbol("AngerJ", ID.AngerJ);

  public final static IBuiltInSymbol AnglePath = S.initFinalSymbol("AnglePath", ID.AnglePath);

  /**
   * AngleVector(phi) - returns the point at angle `phi` on the unit circle.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AngleVector.md">AngleVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol AngleVector = S.initFinalSymbol("AngleVector", ID.AngleVector);

  public final static IBuiltInSymbol Annotation = S.initFinalSymbol("Annotation", ID.Annotation);

  /**
   * Annuity(p, t) - returns an annuity object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Annuity.md">Annuity
   *      documentation</a>
   */
  public final static IBuiltInSymbol Annuity = S.initFinalSymbol("Annuity", ID.Annuity);

  /**
   * AnnuityDue(p, t) - returns an annuity due object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnnuityDue.md">AnnuityDue
   *      documentation</a>
   */
  public final static IBuiltInSymbol AnnuityDue = S.initFinalSymbol("AnnuityDue", ID.AnnuityDue);

  /**
   * AntihermitianMatrixQ(m) - returns `True` if `m` is a anti hermitian matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AntihermitianMatrixQ.md">AntihermitianMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol AntihermitianMatrixQ =
      S.initFinalSymbol("AntihermitianMatrixQ", ID.AntihermitianMatrixQ);

  public final static IBuiltInSymbol AntiSymmetric =
      S.initFinalSymbol("AntiSymmetric", ID.AntiSymmetric);

  /**
   * AntisymmetricMatrixQ(m) - returns `True` if `m` is a anti symmetric matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AntisymmetricMatrixQ.md">AntisymmetricMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol AntisymmetricMatrixQ =
      S.initFinalSymbol("AntisymmetricMatrixQ", ID.AntisymmetricMatrixQ);

  /**
   * AnyTrue({expr1, expr2, ...}, test) - returns `True` if any application of `test` to `expr1,
   * expr2, ...` evaluates to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnyTrue.md">AnyTrue
   *      documentation</a>
   */
  public final static IBuiltInSymbol AnyTrue = S.initFinalSymbol("AnyTrue", ID.AnyTrue);

  /**
   * Apart(expr) - rewrites `expr` as a sum of individual fractions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Apart.md">Apart
   *      documentation</a>
   */
  public final static IBuiltInSymbol Apart = S.initFinalSymbol("Apart", ID.Apart);

  public final static IBuiltInSymbol AppellF1 = S.initFinalSymbol("AppellF1", ID.AppellF1);

  /**
   * Append(expr, item) - returns `expr` with `item` appended to its leaves.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Append.md">Append
   *      documentation</a>
   */
  public final static IBuiltInSymbol Append = S.initFinalSymbol("Append", ID.Append);

  /**
   * AppendTo(s, item) - append `item` to value of `s` and sets `s` to the result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AppendTo.md">AppendTo
   *      documentation</a>
   */
  public final static IBuiltInSymbol AppendTo = S.initFinalSymbol("AppendTo", ID.AppendTo);

  /**
   * f @ expr - returns `f(expr)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Apply.md">Apply
   *      documentation</a>
   */
  public final static IBuiltInSymbol Apply = S.initFinalSymbol("Apply", ID.Apply);

  /**
   * ApplySides(compare-expr, value) - divides all elements of the `compare-expr` by `value`.
   * `compare-expr` can be `True`, `False` or a comparison expression with head `Equal, Unequal,
   * Less, LessEqual, Greater, GreaterEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ApplySides.md">ApplySides
   *      documentation</a>
   */
  public final static IBuiltInSymbol ApplySides = S.initFinalSymbol("ApplySides", ID.ApplySides);

  /**
   * ArcCos(expr) - returns the arc cosine (inverse cosine) of `expr` (measured in radians).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCos.md">ArcCos
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcCos = S.initFinalSymbol("ArcCos", ID.ArcCos);

  /**
   * ArcCosh(z) - returns the inverse hyperbolic cosine of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCosh.md">ArcCosh
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcCosh = S.initFinalSymbol("ArcCosh", ID.ArcCosh);

  /**
   * ArcCot(z) - returns the inverse cotangent of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCot.md">ArcCot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcCot = S.initFinalSymbol("ArcCot", ID.ArcCot);

  /**
   * ArcCoth(z) - returns the inverse hyperbolic cotangent of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCoth.md">ArcCoth
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcCoth = S.initFinalSymbol("ArcCoth", ID.ArcCoth);

  /**
   * ArcCsc(z) - returns the inverse cosecant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCsc.md">ArcCsc
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcCsc = S.initFinalSymbol("ArcCsc", ID.ArcCsc);

  /**
   * ArcCsch(z) - returns the inverse hyperbolic cosecant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcCsch.md">ArcCsch
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcCsch = S.initFinalSymbol("ArcCsch", ID.ArcCsch);

  /**
   * ArcLength(geometric-form) - returns the length of the `geometric-form`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcLength.md">ArcLength
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcLength = S.initFinalSymbol("ArcLength", ID.ArcLength);

  /**
   * ArcSec(z) - returns the inverse secant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSec.md">ArcSec
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcSec = S.initFinalSymbol("ArcSec", ID.ArcSec);

  /**
   * ArcSech(z) - returns the inverse hyperbolic secant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSech.md">ArcSech
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcSech = S.initFinalSymbol("ArcSech", ID.ArcSech);

  /**
   * ArcSin(expr) - returns the arc sine (inverse sine) of `expr` (measured in radians).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSin.md">ArcSin
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcSin = S.initFinalSymbol("ArcSin", ID.ArcSin);

  /**
   * ArcSinh(z) - returns the inverse hyperbolic sine of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcSinh.md">ArcSinh
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcSinh = S.initFinalSymbol("ArcSinh", ID.ArcSinh);

  /**
   * ArcTan(expr) - returns the arc tangent (inverse tangent) of `expr` (measured in radians).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcTan.md">ArcTan
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcTan = S.initFinalSymbol("ArcTan", ID.ArcTan);

  /**
   * ArcTanh(z) - returns the inverse hyperbolic tangent of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArcTanh.md">ArcTanh
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArcTanh = S.initFinalSymbol("ArcTanh", ID.ArcTanh);

  /**
   * Area(geometric-form) - returns the area of the `geometric-form`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Area.md">Area
   *      documentation</a>
   */
  public final static IBuiltInSymbol Area = S.initFinalSymbol("Area", ID.Area);

  /**
   * Arg(expr) - returns the argument of the complex number `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Arg.md">Arg
   *      documentation</a>
   */
  public final static IBuiltInSymbol Arg = S.initFinalSymbol("Arg", ID.Arg);

  /**
   * ArgMax(function, variable) - returns a maximizer point for a univariate `function`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArgMax.md">ArgMax
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArgMax = S.initFinalSymbol("ArgMax", ID.ArgMax);

  /**
   * ArgMin(function, variable) - returns a minimizer point for a univariate `function`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArgMin.md">ArgMin
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArgMin = S.initFinalSymbol("ArgMin", ID.ArgMin);

  /**
   * ArithmeticGeometricMean({a, b, c,...}) - returns the arithmetic geometric mean of `{a, b,
   * c,...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArithmeticGeometricMean.md">ArithmeticGeometricMean
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArithmeticGeometricMean =
      S.initFinalSymbol("ArithmeticGeometricMean", ID.ArithmeticGeometricMean);

  /**
   * Array(f, n) - returns the `n`-element list `{f(1), ..., f(n)}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Array.md">Array
   *      documentation</a>
   */
  public final static IBuiltInSymbol Array = S.initFinalSymbol("Array", ID.Array);

  /**
   * ArrayDepth(a) - returns the depth of the non-ragged array `a`, defined as
   * `Length(Dimensions(a))`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayDepth.md">ArrayDepth
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArrayDepth = S.initFinalSymbol("ArrayDepth", ID.ArrayDepth);

  public final static IBuiltInSymbol ArrayFlatten =
      S.initFinalSymbol("ArrayFlatten", ID.ArrayFlatten);

  /**
   * ArrayPad(list, n) - adds `n` times `0` on the left and right of the `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayPad.md">ArrayPad
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArrayPad = S.initFinalSymbol("ArrayPad", ID.ArrayPad);

  /**
   * ArrayPlot( matrix-of-values ) - generate a rectangle image for the `matrix-of-values`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayPlot.md">ArrayPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArrayPlot = S.initFinalSymbol("ArrayPlot", ID.ArrayPlot);

  /**
   * ArrayQ(expr) - tests whether expr is a full array.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayQ.md">ArrayQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArrayQ = S.initFinalSymbol("ArrayQ", ID.ArrayQ);

  /**
   * ArrayReshape(list-of-values, list-of-dimension) - returns the `list-of-values` elements
   * reshaped as nested list with dimensions according to the `list-of-dimension`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayReshape.md">ArrayReshape
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArrayReshape =
      S.initFinalSymbol("ArrayReshape", ID.ArrayReshape);

  /**
   * ArrayRules(sparse-array) - return the array of rules which define the sparse array.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayRules.md">ArrayRules
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArrayRules = S.initFinalSymbol("ArrayRules", ID.ArrayRules);

  public final static IBuiltInSymbol Arrays = S.initFinalSymbol("Arrays", ID.Arrays);

  /**
   * Arrow({p1, p2}) - represents a line from `p1` to `p2` that ends with an arrow at `p2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Arrow.md">Arrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol Arrow = S.initFinalSymbol("Arrow", ID.Arrow);

  public final static IBuiltInSymbol Arrowheads = S.initFinalSymbol("Arrowheads", ID.Arrowheads);

  /**
   * ASATriangle(alpha, c, beta) - returns a triangle from 2 angles `alpha`, `beta` and side `c`
   * (which is between the angles).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ASATriangle.md">ASATriangle
   *      documentation</a>
   */
  public final static IBuiltInSymbol ASATriangle = S.initFinalSymbol("ASATriangle", ID.ASATriangle);

  public final static IBuiltInSymbol AspectRatio = S.initFinalSymbol("AspectRatio", ID.AspectRatio);

  /**
   * AssociateTo(assoc, rule) - append `rule` to the association `assoc` and assign the result to
   * `assoc`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociateTo.md">AssociateTo
   *      documentation</a>
   */
  public final static IBuiltInSymbol AssociateTo = S.initFinalSymbol("AssociateTo", ID.AssociateTo);

  /**
   * Association[key1 -> val1, key2 -> val2, ...> - represents an association between `key`s and
   * `value`s.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Association.md">Association
   *      documentation</a>
   */
  public final static IBuiltInSymbol Association = S.initFinalSymbol("Association", ID.Association);

  /**
   * AssociationMap(header, <|k1->v1, k2->v2,...|>) - create an association `<|header(k1->v1),
   * header(k2->v2),...|>` with the rules mapped by the `header`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationMap.md">AssociationMap
   *      documentation</a>
   */
  public final static IBuiltInSymbol AssociationMap =
      S.initFinalSymbol("AssociationMap", ID.AssociationMap);

  /**
   * AssociationQ(expr) - returns `True` if `expr` is an association, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationQ.md">AssociationQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol AssociationQ =
      S.initFinalSymbol("AssociationQ", ID.AssociationQ);

  /**
   * AssociationThread({k1,k2,...}, {v1,v2,...}) - create an association with rules from the keys
   * `{k1,k2,...}` and values `{v1,v2,...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AssociationThread.md">AssociationThread
   *      documentation</a>
   */
  public final static IBuiltInSymbol AssociationThread =
      S.initFinalSymbol("AssociationThread", ID.AssociationThread);

  /**
   * Assuming(assumption, expression) - evaluate the `expression` with the assumptions appended to
   * the default `$Assumptions` assumptions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Assuming.md">Assuming
   *      documentation</a>
   */
  public final static IBuiltInSymbol Assuming = S.initFinalSymbol("Assuming", ID.Assuming);

  public final static IBuiltInSymbol Assumptions = S.initFinalSymbol("Assumptions", ID.Assumptions);

  /**
   * AtomQ(x) - is true if `x` is an atom (an object such as a number or string, which cannot be
   * divided into subexpressions using 'Part').
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AtomQ.md">AtomQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol AtomQ = S.initFinalSymbol("AtomQ", ID.AtomQ);

  /**
   * Attributes(symbol) - returns the list of attributes which are assigned to `symbol`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Attributes.md">Attributes
   *      documentation</a>
   */
  public final static IBuiltInSymbol Attributes = S.initFinalSymbol("Attributes", ID.Attributes);

  public final static IBuiltInSymbol Automatic = S.initFinalSymbol("Automatic", ID.Automatic);

  public final static IBuiltInSymbol AvogadroConstant =
      S.initFinalSymbol("AvogadroConstant", ID.AvogadroConstant);

  public final static IBuiltInSymbol Axes = S.initFinalSymbol("Axes", ID.Axes);

  public final static IBuiltInSymbol AxesLabel = S.initFinalSymbol("AxesLabel", ID.AxesLabel);

  public final static IBuiltInSymbol AxesOrigin = S.initFinalSymbol("AxesOrigin", ID.AxesOrigin);

  public final static IBuiltInSymbol AxesStyle = S.initFinalSymbol("AxesStyle", ID.AxesStyle);

  public final static IBuiltInSymbol Axis = S.initFinalSymbol("Axis", ID.Axis);

  public final static IBuiltInSymbol Background = S.initFinalSymbol("Background", ID.Background);

  public final static IBuiltInSymbol Ball = S.initFinalSymbol("Ball", ID.Ball);

  /**
   * BarChart(list-of-values, options) - plot a bar chart for a `list-of-values` with option
   * `BarOrigin->Bottom` or `BarOrigin->Bottom`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BarChart.md">BarChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol BarChart = S.initFinalSymbol("BarChart", ID.BarChart);

  public final static IBuiltInSymbol BarOrigin = S.initFinalSymbol("BarOrigin", ID.BarOrigin);

  public final static IBuiltInSymbol BartlettWindow =
      S.initFinalSymbol("BartlettWindow", ID.BartlettWindow);

  /**
   * BaseDecode(string) - decodes a Base64 encoded `string` into a `ByteArray` using the Base64
   * encoding scheme.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseDecode.md">BaseDecode
   *      documentation</a>
   */
  public final static IBuiltInSymbol BaseDecode = S.initFinalSymbol("BaseDecode", ID.BaseDecode);

  /**
   * BaseEncode(byte-array) - encodes the specified `byte-array` into a string using the Base64
   * encoding scheme.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseEncode.md">BaseEncode
   *      documentation</a>
   */
  public final static IBuiltInSymbol BaseEncode = S.initFinalSymbol("BaseEncode", ID.BaseEncode);

  /**
   * BaseForm(integer, radix) - prints the `integer` number in base `radix` form.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BaseForm.md">BaseForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol BaseForm = S.initFinalSymbol("BaseForm", ID.BaseForm);

  public final static IBuiltInSymbol Beep = S.initFinalSymbol("Beep", ID.Beep);

  /**
   * Begin("<context-name>") - start a new context definition
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Begin.md">Begin
   *      documentation</a>
   */
  public final static IBuiltInSymbol Begin = S.initFinalSymbol("Begin", ID.Begin);

  /**
   * BeginPackage("<context-name>") - start a new package definition
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BeginPackage.md">BeginPackage
   *      documentation</a>
   */
  public final static IBuiltInSymbol BeginPackage =
      S.initFinalSymbol("BeginPackage", ID.BeginPackage);

  public final static IBuiltInSymbol BeginTestSection =
      S.initFinalSymbol("BeginTestSection", ID.BeginTestSection);

  /**
   * BellB(n) - the Bell number function counts the number of different ways to partition a set that
   * has exactly `n` elements
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BellB.md">BellB
   *      documentation</a>
   */
  public final static IBuiltInSymbol BellB = S.initFinalSymbol("BellB", ID.BellB);

  /**
   * BellY(n, k, {x1, x2, ... , xN}) - the second kind of Bell polynomials (incomplete Bell
   * polynomials).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BellY.md">BellY
   *      documentation</a>
   */
  public final static IBuiltInSymbol BellY = S.initFinalSymbol("BellY", ID.BellY);

  /**
   * BernoulliB(expr) - computes the Bernoulli number of the first kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernoulliB.md">BernoulliB
   *      documentation</a>
   */
  public final static IBuiltInSymbol BernoulliB = S.initFinalSymbol("BernoulliB", ID.BernoulliB);

  /**
   * BernoulliDistribution(p) - returns the Bernoulli distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernoulliDistribution.md">BernoulliDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol BernoulliDistribution =
      S.initFinalSymbol("BernoulliDistribution", ID.BernoulliDistribution);

  public final static IBuiltInSymbol BernoulliProcess =
      S.initFinalSymbol("BernoulliProcess", ID.BernoulliProcess);

  /**
   * BernsteinBasis(n, v, expr) - computes the Bernstein basis for the expression `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BernsteinBasis.md">BernsteinBasis
   *      documentation</a>
   */
  public final static IBuiltInSymbol BernsteinBasis =
      S.initFinalSymbol("BernsteinBasis", ID.BernsteinBasis);

  /**
   * BesselI(n, z) - modified Bessel function of the first kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselI.md">BesselI
   *      documentation</a>
   */
  public final static IBuiltInSymbol BesselI = S.initFinalSymbol("BesselI", ID.BesselI);

  /**
   * BesselJ(n, z) - Bessel function of the first kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselJ.md">BesselJ
   *      documentation</a>
   */
  public final static IBuiltInSymbol BesselJ = S.initFinalSymbol("BesselJ", ID.BesselJ);

  /**
   * BesselJZero(n, z) - is the `k`th zero of the `BesselJ(n,z)` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselJZero.md">BesselJZero
   *      documentation</a>
   */
  public final static IBuiltInSymbol BesselJZero = S.initFinalSymbol("BesselJZero", ID.BesselJZero);

  /**
   * BesselK(n, z) - modified Bessel function of the second kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselK.md">BesselK
   *      documentation</a>
   */
  public final static IBuiltInSymbol BesselK = S.initFinalSymbol("BesselK", ID.BesselK);

  /**
   * BesselY(n, z) - Bessel function of the second kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselY.md">BesselY
   *      documentation</a>
   */
  public final static IBuiltInSymbol BesselY = S.initFinalSymbol("BesselY", ID.BesselY);

  /**
   * BesselYZero(n, z) - is the `k`th zero of the `BesselY(n,z)` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BesselYZero.md">BesselYZero
   *      documentation</a>
   */
  public final static IBuiltInSymbol BesselYZero = S.initFinalSymbol("BesselYZero", ID.BesselYZero);

  /**
   * Beta(a, b) - is the beta function of the numbers `a`,`b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Beta.md">Beta
   *      documentation</a>
   */
  public final static IBuiltInSymbol Beta = S.initFinalSymbol("Beta", ID.Beta);

  public final static IBuiltInSymbol BetaDistribution =
      S.initFinalSymbol("BetaDistribution", ID.BetaDistribution);

  public final static IBuiltInSymbol BetaRegularized =
      S.initFinalSymbol("BetaRegularized", ID.BetaRegularized);

  public final static IBuiltInSymbol Between = S.initFinalSymbol("Between", ID.Between);

  /**
   * BetweennessCentrality(graph) - Computes the betweenness centrality of each vertex of a `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BetweennessCentrality.md">BetweennessCentrality
   *      documentation</a>
   */
  public final static IBuiltInSymbol BetweennessCentrality =
      S.initFinalSymbol("BetweennessCentrality", ID.BetweennessCentrality);

  public final static IBuiltInSymbol BezierFunction =
      S.initFinalSymbol("BezierFunction", ID.BezierFunction);

  /**
   * BinaryDeserialize(byte-array) - deserialize the `byte-array` from WXF format into a Symja
   * expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinaryDeserialize.md">BinaryDeserialize
   *      documentation</a>
   */
  public final static IBuiltInSymbol BinaryDeserialize =
      S.initFinalSymbol("BinaryDeserialize", ID.BinaryDeserialize);

  /**
   * BinaryDistance(u, v) - returns the binary distance between `u` and `v`. `0` if `u` and `v` are
   * unequal. `1` if `u` and `v` are equal.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinaryDistance.md">BinaryDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol BinaryDistance =
      S.initFinalSymbol("BinaryDistance", ID.BinaryDistance);

  public final static IBuiltInSymbol BinaryRead = S.initFinalSymbol("BinaryRead", ID.BinaryRead);

  /**
   * BinarySerialize(expr) - serialize the Symja `expr` into a byte array expression in WXF format.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinarySerialize.md">BinarySerialize
   *      documentation</a>
   */
  public final static IBuiltInSymbol BinarySerialize =
      S.initFinalSymbol("BinarySerialize", ID.BinarySerialize);

  public final static IBuiltInSymbol BinaryWrite = S.initFinalSymbol("BinaryWrite", ID.BinaryWrite);

  /**
   * BinCounts(list, width-of-bin) - count the number of elements, if `list`, is divided into
   * successive bins with width `width-of-bin`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinCounts.md">BinCounts
   *      documentation</a>
   */
  public final static IBuiltInSymbol BinCounts = S.initFinalSymbol("BinCounts", ID.BinCounts);

  /**
   * Binomial(n, k) - returns the binomial coefficient of the 2 integers `n` and `k`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Binomial.md">Binomial
   *      documentation</a>
   */
  public final static IBuiltInSymbol Binomial = S.initFinalSymbol("Binomial", ID.Binomial);

  /**
   * BinomialDistribution(n, p) - returns the binomial distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinomialDistribution.md">BinomialDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol BinomialDistribution =
      S.initFinalSymbol("BinomialDistribution", ID.BinomialDistribution);

  public final static IBuiltInSymbol BinomialProcess =
      S.initFinalSymbol("BinomialProcess", ID.BinomialProcess);

  public final static IBuiltInSymbol BioSequence = S.initFinalSymbol("BioSequence", ID.BioSequence);

  public final static IBuiltInSymbol BioSequenceQ =
      S.initFinalSymbol("BioSequenceQ", ID.BioSequenceQ);

  public final static IBuiltInSymbol BioSequenceTranscribe =
      S.initFinalSymbol("BioSequenceTranscribe", ID.BioSequenceTranscribe);

  public final static IBuiltInSymbol BioSequenceTranslate =
      S.initFinalSymbol("BioSequenceTranslate", ID.BioSequenceTranslate);

  public final static IBuiltInSymbol BipartiteGraphQ =
      S.initFinalSymbol("BipartiteGraphQ", ID.BipartiteGraphQ);

  /**
   * BitLength(x) - gives the number of bits needed to represent the integer `x`. The sign of `x` is
   * ignored.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BitLength.md">BitLength
   *      documentation</a>
   */
  public final static IBuiltInSymbol BitLength = S.initFinalSymbol("BitLength", ID.BitLength);

  public final static IBuiltInSymbol Black = S.initFinalSymbol("Black", ID.Black);

  public final static IBuiltInSymbol BlackmanHarrisWindow =
      S.initFinalSymbol("BlackmanHarrisWindow", ID.BlackmanHarrisWindow);

  public final static IBuiltInSymbol BlackmanNuttallWindow =
      S.initFinalSymbol("BlackmanNuttallWindow", ID.BlackmanNuttallWindow);

  public final static IBuiltInSymbol BlackmanWindow =
      S.initFinalSymbol("BlackmanWindow", ID.BlackmanWindow);

  public final static IBuiltInSymbol Blank = S.initFinalSymbol("Blank", ID.Blank);

  public final static IBuiltInSymbol BlankNullSequence =
      S.initFinalSymbol("BlankNullSequence", ID.BlankNullSequence);

  public final static IBuiltInSymbol BlankSequence =
      S.initFinalSymbol("BlankSequence", ID.BlankSequence);

  /**
   * Block({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Block.md">Block
   *      documentation</a>
   */
  public final static IBuiltInSymbol Block = S.initFinalSymbol("Block", ID.Block);

  public final static IBuiltInSymbol Blue = S.initFinalSymbol("Blue", ID.Blue);

  public final static IBuiltInSymbol BohrRadius = S.initFinalSymbol("BohrRadius", ID.BohrRadius);

  /**
   * Boole(expr) - returns `1` if `expr` evaluates to `True`; returns `0` if `expr` evaluates to
   * `False`; and gives no result otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Boole.md">Boole
   *      documentation</a>
   */
  public final static IBuiltInSymbol Boole = S.initFinalSymbol("Boole", ID.Boole);

  /**
   * BooleanConvert(logical-expr) - convert the `logical-expr` to [disjunctive normal
   * form](https://en.wikipedia.org/wiki/Disjunctive_normal_form)
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanConvert.md">BooleanConvert
   *      documentation</a>
   */
  public final static IBuiltInSymbol BooleanConvert =
      S.initFinalSymbol("BooleanConvert", ID.BooleanConvert);

  /**
   * BooleanFunction(n, number-of-variables) - create the `n`-th boolean function containing the
   * `number-of-variables`. The `i`-th variable is represented by the `i`-th slot.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanFunction.md">BooleanFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol BooleanFunction =
      S.initFinalSymbol("BooleanFunction", ID.BooleanFunction);

  /**
   * BooleaMaxterms({{b1,b2,...}}, {v1,v2,...}) - create the conjunction of the variables
   * `{v1,v2,...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanMaxterms.md">BooleanMaxterms
   *      documentation</a>
   */
  public final static IBuiltInSymbol BooleanMaxterms =
      S.initFinalSymbol("BooleanMaxterms", ID.BooleanMaxterms);

  /**
   * BooleanMinimize(expr) - minimizes a boolean function with the [Quine McCluskey
   * algorithm](https://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm)
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanMinimize.md">BooleanMinimize
   *      documentation</a>
   */
  public final static IBuiltInSymbol BooleanMinimize =
      S.initFinalSymbol("BooleanMinimize", ID.BooleanMinimize);

  /**
   * BooleanMinterms({{b1,b2,...}}, {v1,v2,...}) - create the disjunction of the variables
   * `{v1,v2,...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanMinterms.md">BooleanMinterms
   *      documentation</a>
   */
  public final static IBuiltInSymbol BooleanMinterms =
      S.initFinalSymbol("BooleanMinterms", ID.BooleanMinterms);

  /**
   * BooleanQ(expr) - returns `True` if `expr` is either `True` or `False`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanQ.md">BooleanQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol BooleanQ = S.initFinalSymbol("BooleanQ", ID.BooleanQ);

  /**
   * Booleans - is the set of boolean values.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Booleans.md">Booleans
   *      documentation</a>
   */
  public final static IBuiltInSymbol Booleans = S.initFinalSymbol("Booleans", ID.Booleans);

  /**
   * BooleanTable(logical-expr, variables) - generate [truth
   * values](https://en.wikipedia.org/wiki/Truth_table) from the `logical-expr`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanTable.md">BooleanTable
   *      documentation</a>
   */
  public final static IBuiltInSymbol BooleanTable =
      S.initFinalSymbol("BooleanTable", ID.BooleanTable);

  /**
   * BooleanVariables(logical-expr) - gives a list of the boolean variables that appear in the
   * `logical-expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanVariables.md">BooleanVariables
   *      documentation</a>
   */
  public final static IBuiltInSymbol BooleanVariables =
      S.initFinalSymbol("BooleanVariables", ID.BooleanVariables);

  public final static IBuiltInSymbol Bottom = S.initFinalSymbol("Bottom", ID.Bottom);

  public final static IBuiltInSymbol Boxed = S.initFinalSymbol("Boxed", ID.Boxed);

  public final static IBuiltInSymbol BoxRatios = S.initFinalSymbol("BoxRatios", ID.BoxRatios);

  /**
   * BoxWhiskerChart( ) - plot a box whisker chart.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BoxWhiskerChart.md">BoxWhiskerChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol BoxWhiskerChart =
      S.initFinalSymbol("BoxWhiskerChart", ID.BoxWhiskerChart);

  /**
   * BrayCurtisDistance(u, v) - returns the Bray Curtis distance between `u` and `v`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BrayCurtisDistance.md">BrayCurtisDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol BrayCurtisDistance =
      S.initFinalSymbol("BrayCurtisDistance", ID.BrayCurtisDistance);

  /**
   * Break() - exits a `For`, `While`, or `Do` loop.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Break.md">Break
   *      documentation</a>
   */
  public final static IBuiltInSymbol Break = S.initFinalSymbol("Break", ID.Break);

  public final static IBuiltInSymbol Brown = S.initFinalSymbol("Brown", ID.Brown);

  public final static IBuiltInSymbol BrownianBridgeProcess =
      S.initFinalSymbol("BrownianBridgeProcess", ID.BrownianBridgeProcess);

  public final static IBuiltInSymbol BSplineFunction =
      S.initFinalSymbol("BSplineFunction", ID.BSplineFunction);

  public final static IBuiltInSymbol Button = S.initFinalSymbol("Button", ID.Button);

  public final static IBuiltInSymbol Byte = S.initFinalSymbol("Byte", ID.Byte);

  /**
   * ByteArray({list-of-byte-values}) - converts the `list-of-byte-values` into a byte array.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ByteArray.md">ByteArray
   *      documentation</a>
   */
  public final static IBuiltInSymbol ByteArray = S.initFinalSymbol("ByteArray", ID.ByteArray);


  /**
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ByteArrayQ.md">ByteArrayQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ByteArrayQ = S.initFinalSymbol("ByteArrayQ", ID.ByteArrayQ);

  /**
   * ByteArrayToString(byte-array) - decoding the specified `byte-array` using the default character
   * set `UTF-8`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ByteArrayToString.md">ByteArrayToString
   *      documentation</a>
   */
  public final static IBuiltInSymbol ByteArrayToString =
      S.initFinalSymbol("ByteArrayToString", ID.ByteArrayToString);

  public final static IBuiltInSymbol ByteCount = S.initFinalSymbol("ByteCount", ID.ByteCount);

  /**
   * C(n) - represents the `n`-th constant in a solution to a differential equation.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/C.md">C
   *      documentation</a>
   */
  public final static IBuiltInSymbol C = S.initFinalSymbol("C", ID.C);

  /**
   * CanberraDistance(u, v) - returns the canberra distance between `u` and `v`, which is a weighted
   * version of the Manhattan distance.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CanberraDistance.md">CanberraDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol CanberraDistance =
      S.initFinalSymbol("CanberraDistance", ID.CanberraDistance);

  /**
   * Cancel(expr) - cancels out common factors in numerators and denominators.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cancel.md">Cancel
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cancel = S.initFinalSymbol("Cancel", ID.Cancel);

  public final static IBuiltInSymbol CancelButton =
      S.initFinalSymbol("CancelButton", ID.CancelButton);

  /**
   * CarlsonRC(x, y) - returns the Carlson RC function..
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRC.md">CarlsonRC
   *      documentation</a>
   */
  public final static IBuiltInSymbol CarlsonRC = S.initFinalSymbol("CarlsonRC", ID.CarlsonRC);

  /**
   * CarlsonRD(x, y, z) - returns the Carlson RD function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRD.md">CarlsonRD
   *      documentation</a>
   */
  public final static IBuiltInSymbol CarlsonRD = S.initFinalSymbol("CarlsonRD", ID.CarlsonRD);

  /**
   * CarlsonRF(x, y, z) - returns the Carlson RF function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRF.md">CarlsonRF
   *      documentation</a>
   */
  public final static IBuiltInSymbol CarlsonRF = S.initFinalSymbol("CarlsonRF", ID.CarlsonRF);

  /**
   * CarlsonRG(x, y, z) - returns the Carlson RG function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRG.md">CarlsonRG
   *      documentation</a>
   */
  public final static IBuiltInSymbol CarlsonRG = S.initFinalSymbol("CarlsonRG", ID.CarlsonRG);

  /**
   * CarlsonRJ(x, y, z, p) - returns the Carlson RJ function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarlsonRJ.md">CarlsonRJ
   *      documentation</a>
   */
  public final static IBuiltInSymbol CarlsonRJ = S.initFinalSymbol("CarlsonRJ", ID.CarlsonRJ);

  /**
   * CarmichaelLambda(n) - the Carmichael function of `n`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CarmichaelLambda.md">CarmichaelLambda
   *      documentation</a>
   */
  public final static IBuiltInSymbol CarmichaelLambda =
      S.initFinalSymbol("CarmichaelLambda", ID.CarmichaelLambda);

  /**
   * CartesianProduct(list1, list2) - returns the cartesian product for multiple lists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CartesianProduct.md">CartesianProduct
   *      documentation</a>
   */
  public final static IBuiltInSymbol CartesianProduct =
      S.initFinalSymbol("CartesianProduct", ID.CartesianProduct);

  /**
   * Cases(list, pattern) - returns the elements of `list` that match `pattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cases.md">Cases
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cases = S.initFinalSymbol("Cases", ID.Cases);

  /**
   * Catalan - Catalan's constant
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Catalan.md">Catalan
   *      documentation</a>
   */
  public final static IBuiltInSymbol Catalan = S.initFinalSymbol("Catalan", ID.Catalan);

  /**
   * CatalanNumber(n) - returns the catalan number for the argument `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CatalanNumber.md">CatalanNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol CatalanNumber =
      S.initFinalSymbol("CatalanNumber", ID.CatalanNumber);

  /**
   * Catch(expr) - returns the value argument of the first `Throw(value)` generated in the
   * evaluation of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Catch.md">Catch
   *      documentation</a>
   */
  public final static IBuiltInSymbol Catch = S.initFinalSymbol("Catch", ID.Catch);

  /**
   * Catenate({l1, l2, ...}) - concatenates the lists `l1, l2, ...`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Catenate.md">Catenate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Catenate = S.initFinalSymbol("Catenate", ID.Catenate);

  /**
   * CauchyDistribution(a,b) - returns the Cauchy distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CauchyDistribution.md">CauchyDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol CauchyDistribution =
      S.initFinalSymbol("CauchyDistribution", ID.CauchyDistribution);

  /**
   * CDF(distribution, value) - returns the cumulative distribution function of `value`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CDF.md">CDF
   *      documentation</a>
   */
  public final static IBuiltInSymbol CDF = S.initFinalSymbol("CDF", ID.CDF);

  /**
   * Ceiling(expr) - gives the first integer greater than or equal `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ceiling.md">Ceiling
   *      documentation</a>
   */
  public final static IBuiltInSymbol Ceiling = S.initFinalSymbol("Ceiling", ID.Ceiling);

  public final static IBuiltInSymbol Center = S.initFinalSymbol("Center", ID.Center);

  public final static IBuiltInSymbol CenterDot = S.initFinalSymbol("CenterDot", ID.CenterDot);

  /**
   * CentralMoment(list, r) - gives the the `r`-th central moment (i.e. the `r`th moment about the
   * mean) of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CentralMoment.md">CentralMoment
   *      documentation</a>
   */
  public final static IBuiltInSymbol CentralMoment =
      S.initFinalSymbol("CentralMoment", ID.CentralMoment);

  public final static IBuiltInSymbol CForm = S.initFinalSymbol("CForm", ID.CForm);

  public final static IBuiltInSymbol Character = S.initFinalSymbol("Character", ID.Character);

  public final static IBuiltInSymbol CharacterEncoding =
      S.initFinalSymbol("CharacterEncoding", ID.CharacterEncoding);

  /**
   * CharacteristicPolynomial(matrix, var) - computes the characteristic polynomial of a `matrix`
   * for the variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CharacteristicPolynomial.md">CharacteristicPolynomial
   *      documentation</a>
   */
  public final static IBuiltInSymbol CharacteristicPolynomial =
      S.initFinalSymbol("CharacteristicPolynomial", ID.CharacteristicPolynomial);

  /**
   * CharacterRange(min-character, max-character) - computes a list of character strings from
   * `min-character` to `max-character`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CharacterRange.md">CharacterRange
   *      documentation</a>
   */
  public final static IBuiltInSymbol CharacterRange =
      S.initFinalSymbol("CharacterRange", ID.CharacterRange);

  public final static IBuiltInSymbol Characters = S.initFinalSymbol("Characters", ID.Characters);

  /**
   * ChebyshevT(n, x) - returns the Chebyshev polynomial of the first kind `T_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChebyshevT.md">ChebyshevT
   *      documentation</a>
   */
  public final static IBuiltInSymbol ChebyshevT = S.initFinalSymbol("ChebyshevT", ID.ChebyshevT);

  /**
   * ChebyshevU(n, x) - returns the Chebyshev polynomial of the second kind `U_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChebyshevU.md">ChebyshevU
   *      documentation</a>
   */
  public final static IBuiltInSymbol ChebyshevU = S.initFinalSymbol("ChebyshevU", ID.ChebyshevU);

  /**
   * Check(expr, failure) - evaluates `expr`, and returns the result, unless messages were
   * generated, in which case `failure` will be returned.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Check.md">Check
   *      documentation</a>
   */
  public final static IBuiltInSymbol Check = S.initFinalSymbol("Check", ID.Check);

  /**
   * CheckAbort(expr, failure-expr) - evaluates `expr`, and returns the result, unless `Abort` was
   * called during the evaluation, in which case `failure-expr` will be returned.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CheckAbort.md">CheckAbort
   *      documentation</a>
   */
  public final static IBuiltInSymbol CheckAbort = S.initFinalSymbol("CheckAbort", ID.CheckAbort);

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
      S.initFinalSymbol("ChessboardDistance", ID.ChessboardDistance);

  /**
   * ChineseRemainder({a1, a2, a3,...}, {n1, n2, n3,...}) - the chinese remainder function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChineseRemainder.md">ChineseRemainder
   *      documentation</a>
   */
  public final static IBuiltInSymbol ChineseRemainder =
      S.initFinalSymbol("ChineseRemainder", ID.ChineseRemainder);

  public final static IBuiltInSymbol ChiSquareDistribution =
      S.initFinalSymbol("ChiSquareDistribution", ID.ChiSquareDistribution);

  /**
   * CholeskyDecomposition(matrix) - calculate the Cholesky decomposition of a hermitian, positive
   * definite square `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CholeskyDecomposition.md">CholeskyDecomposition
   *      documentation</a>
   */
  public final static IBuiltInSymbol CholeskyDecomposition =
      S.initFinalSymbol("CholeskyDecomposition", ID.CholeskyDecomposition);

  /**
   * Chop(numerical-expr) - replaces numerical values in the `numerical-expr` which are close to
   * zero with symbolic value `0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Chop.md">Chop
   *      documentation</a>
   */
  public final static IBuiltInSymbol Chop = S.initFinalSymbol("Chop", ID.Chop);

  public final static IBuiltInSymbol Circle = S.initFinalSymbol("Circle", ID.Circle);

  public final static IBuiltInSymbol CircleDot = S.initFinalSymbol("CircleDot", ID.CircleDot);

  /**
   * CirclePoints(i) - gives the `i` points on the unit circle for a positive integer `i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CirclePoints.md">CirclePoints
   *      documentation</a>
   */
  public final static IBuiltInSymbol CirclePoints =
      S.initFinalSymbol("CirclePoints", ID.CirclePoints);

  public final static IBuiltInSymbol CircleTimes = S.initFinalSymbol("CircleTimes", ID.CircleTimes);

  /**
   * Clear(symbol1, symbol2,...) - clears all values of the given symbols.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Clear.md">Clear
   *      documentation</a>
   */
  public final static IBuiltInSymbol Clear = S.initFinalSymbol("Clear", ID.Clear);

  /**
   * ClearAll(symbol1, symbol2,...) - clears all values and attributes associated with the given
   * symbols.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClearAll.md">ClearAll
   *      documentation</a>
   */
  public final static IBuiltInSymbol ClearAll = S.initFinalSymbol("ClearAll", ID.ClearAll);

  /**
   * ClearAttributes(symbol, attrib) - removes `attrib` from `symbol`'s attributes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClearAttributes.md">ClearAttributes
   *      documentation</a>
   */
  public final static IBuiltInSymbol ClearAttributes =
      S.initFinalSymbol("ClearAttributes", ID.ClearAttributes);

  /**
   * ClebschGordan({j1,m1},{j2,m2},{j3,m3}) - get the Clebsch–Gordan coefficients. Clebsch–Gordan
   * coefficients are numbers that arise in angular momentum coupling in quantum mechanic.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClebschGordan.md">ClebschGordan
   *      documentation</a>
   */
  public final static IBuiltInSymbol ClebschGordan =
      S.initFinalSymbol("ClebschGordan", ID.ClebschGordan);

  /**
   * Clip(expr) - returns `expr` in the range `-1` to `1`. Returns `-1` if `expr` is less than `-1`.
   * Returns `1` if `expr` is greater than `1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Clip.md">Clip
   *      documentation</a>
   */
  public final static IBuiltInSymbol Clip = S.initFinalSymbol("Clip", ID.Clip);

  /**
   * Close(stream) - closes an input or output `stream`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Close.md">Close
   *      documentation</a>
   */
  public final static IBuiltInSymbol Close = S.initFinalSymbol("Close", ID.Close);

  /**
   * ClosenessCentrality(graph) - Computes the closeness centrality of each vertex of a `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClosenessCentrality.md">ClosenessCentrality
   *      documentation</a>
   */
  public final static IBuiltInSymbol ClosenessCentrality =
      S.initFinalSymbol("ClosenessCentrality", ID.ClosenessCentrality);

  public final static IBuiltInSymbol CMYColor = S.initFinalSymbol("CMYColor", ID.CMYColor);

  /**
   * Coefficient(polynomial, variable, exponent) - get the coefficient of `variable^exponent` in
   * `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Coefficient.md">Coefficient
   *      documentation</a>
   */
  public final static IBuiltInSymbol Coefficient = S.initFinalSymbol("Coefficient", ID.Coefficient);

  public final static IBuiltInSymbol CoefficientArrays =
      S.initFinalSymbol("CoefficientArrays", ID.CoefficientArrays);

  /**
   * CoefficientList(polynomial, variable) - get the coefficient list of a `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoefficientList.md">CoefficientList
   *      documentation</a>
   */
  public final static IBuiltInSymbol CoefficientList =
      S.initFinalSymbol("CoefficientList", ID.CoefficientList);

  /**
   * CoefficientRules(polynomial, list-of-variables) - get the list of coefficient rules of a
   * `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoefficientRules.md">CoefficientRules
   *      documentation</a>
   */
  public final static IBuiltInSymbol CoefficientRules =
      S.initFinalSymbol("CoefficientRules", ID.CoefficientRules);

  /**
   * Cofactor(matrix, {i,j}) - calculate the cofactor of the matrix
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cofactor.md">Cofactor
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cofactor = S.initFinalSymbol("Cofactor", ID.Cofactor);

  /**
   * Collect(expr, variable) - collect subexpressions in `expr` which belong to the same `variable`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Collect.md">Collect
   *      documentation</a>
   */
  public final static IBuiltInSymbol Collect = S.initFinalSymbol("Collect", ID.Collect);

  /**
   * CollinearPoints({{x1,y1},{x2,y2},{a,b},...}) - returns true if the point `{a,b]` is on the line
   * defined by the first two points `{x1,y1},{x2,y2}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CollinearPoints.md">CollinearPoints
   *      documentation</a>
   */
  public final static IBuiltInSymbol CollinearPoints =
      S.initFinalSymbol("CollinearPoints", ID.CollinearPoints);

  public final static IBuiltInSymbol Colon = S.initFinalSymbol("Colon", ID.Colon);

  public final static IBuiltInSymbol ColorData = S.initFinalSymbol("ColorData", ID.ColorData);

  public final static IBuiltInSymbol ColorFunction =
      S.initFinalSymbol("ColorFunction", ID.ColorFunction);

  public final static IBuiltInSymbol Column = S.initFinalSymbol("Column", ID.Column);

  /**
   * Commonest(dataValueList) - the mode of a list of data values is the value that appears most
   * often.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Commonest.md">Commonest
   *      documentation</a>
   */
  public final static IBuiltInSymbol Commonest = S.initFinalSymbol("Commonest", ID.Commonest);

  public final static IBuiltInSymbol CompatibleUnitQ =
      S.initFinalSymbol("CompatibleUnitQ", ID.CompatibleUnitQ);

  /**
   * Compile(list-of-arguments}, expression) - compile the `expression` into a Java function, which
   * has the arguments defined in `list-of-arguments` and return the compiled result in an
   * `CompiledFunction` expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Compile.md">Compile
   *      documentation</a>
   */
  public final static IBuiltInSymbol Compile = S.initFinalSymbol("Compile", ID.Compile);

  /**
   * CompiledFunction(...) - represents a binary Java coded function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompiledFunction.md">CompiledFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol CompiledFunction =
      S.initFinalSymbol("CompiledFunction", ID.CompiledFunction);

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
      S.initFinalSymbol("CompilePrint", ID.CompilePrint);

  /**
   * Complement(set1, set2) - get the complement set from `set1` and `set2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complement.md">Complement
   *      documentation</a>
   */
  public final static IBuiltInSymbol Complement = S.initFinalSymbol("Complement", ID.Complement);

  /**
   * CompleteGraph(order) - returns the complete graph with `order` vertices.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompleteGraph.md">CompleteGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol CompleteGraph =
      S.initFinalSymbol("CompleteGraph", ID.CompleteGraph);

  /**
   * Complex - is the head of complex numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complex.md">Complex
   *      documentation</a>
   */
  public final static IBuiltInSymbol Complex = S.initFinalSymbol("Complex", ID.Complex);

  /**
   * Complexes - is the set of complex numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complexes.md">Complexes
   *      documentation</a>
   */
  public final static IBuiltInSymbol Complexes = S.initFinalSymbol("Complexes", ID.Complexes);

  /**
   * ComplexExpand(expr) - expands `expr`. All variable symbols in `expr` are assumed to be non
   * complex numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexExpand.md">ComplexExpand
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComplexExpand =
      S.initFinalSymbol("ComplexExpand", ID.ComplexExpand);

  /**
   * ComplexInfinity - represents an infinite complex quantity of undetermined direction.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexInfinity.md">ComplexInfinity
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComplexInfinity =
      S.initFinalSymbol("ComplexInfinity", ID.ComplexInfinity);

  public final static IBuiltInSymbol ComplexityFunction =
      S.initFinalSymbol("ComplexityFunction", ID.ComplexityFunction);

  /**
   * ComplexPlot3D(expr, {z, min, max ) - create a 3D plot of `expr` for the complex variable `z` in
   * the range `{ Re(min),Re(max) }` to `{ Im(min),Im(max) }`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexPlot3D.md">ComplexPlot3D
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComplexPlot3D =
      S.initFinalSymbol("ComplexPlot3D", ID.ComplexPlot3D);

  /**
   * ComposeList(list-of-symbols, variable) - creates a list of compositions of the symbols applied
   * at the argument `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComposeList.md">ComposeList
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComposeList = S.initFinalSymbol("ComposeList", ID.ComposeList);

  /**
   * ComposeSeries( series1, series2 ) - substitute `series2` into `series1`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComposeSeries.md">ComposeSeries
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComposeSeries =
      S.initFinalSymbol("ComposeSeries", ID.ComposeSeries);

  /**
   * CompositeQ(n) - returns `True` if `n` is a composite integer number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompositeQ.md">CompositeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol CompositeQ = S.initFinalSymbol("CompositeQ", ID.CompositeQ);

  /**
   * Composition(sym1, sym2,...)[arg1, arg2,...] - creates a composition of the symbols applied at
   * the arguments.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Composition.md">Composition
   *      documentation</a>
   */
  public final static IBuiltInSymbol Composition = S.initFinalSymbol("Composition", ID.Composition);

  /**
   * CompoundExpression(expr1, expr2, ...) - evaluates its arguments in turn, returning the last
   * result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompoundExpression.md">CompoundExpression
   *      documentation</a>
   */
  public final static IBuiltInSymbol CompoundExpression =
      S.initFinalSymbol("CompoundExpression", ID.CompoundExpression);

  /**
   * Condition(pattern, expr) - places an additional constraint on `pattern` that only allows it to
   * match if `expr` evaluates to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Condition.md">Condition
   *      documentation</a>
   */
  public final static IBuiltInSymbol Condition = S.initFinalSymbol("Condition", ID.Condition);

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
      S.initFinalSymbol("ConditionalExpression", ID.ConditionalExpression);

  public final static IBuiltInSymbol Cone = S.initFinalSymbol("Cone", ID.Cone);

  /**
   * Conjugate(z) - returns the complex conjugate of the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Conjugate.md">Conjugate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Conjugate = S.initFinalSymbol("Conjugate", ID.Conjugate);

  /**
   * ConjugateTranspose(matrix) - get the transposed `matrix` with conjugated matrix elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConjugateTranspose.md">ConjugateTranspose
   *      documentation</a>
   */
  public final static IBuiltInSymbol ConjugateTranspose =
      S.initFinalSymbol("ConjugateTranspose", ID.ConjugateTranspose);

  public final static IBuiltInSymbol ConnectedGraphQ =
      S.initFinalSymbol("ConnectedGraphQ", ID.ConnectedGraphQ);

  /**
   * Constant - is an attribute that indicates that a symbol is a constant.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Constant.md">Constant
   *      documentation</a>
   */
  public final static IBuiltInSymbol Constant = S.initFinalSymbol("Constant", ID.Constant);

  /**
   * ConstantArray(expr, n) - returns a list of `n` copies of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConstantArray.md">ConstantArray
   *      documentation</a>
   */
  public final static IBuiltInSymbol ConstantArray =
      S.initFinalSymbol("ConstantArray", ID.ConstantArray);

  public final static IBuiltInSymbol ContainsAll = S.initFinalSymbol("ContainsAll", ID.ContainsAll);

  public final static IBuiltInSymbol ContainsAny = S.initFinalSymbol("ContainsAny", ID.ContainsAny);

  public final static IBuiltInSymbol ContainsExactly =
      S.initFinalSymbol("ContainsExactly", ID.ContainsExactly);

  public final static IBuiltInSymbol ContainsNone =
      S.initFinalSymbol("ContainsNone", ID.ContainsNone);

  /**
   * ContainsOnly(list1, list2) - yields True if `list1` contains only elements that appear in
   * `list2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContainsOnly.md">ContainsOnly
   *      documentation</a>
   */
  public final static IBuiltInSymbol ContainsOnly =
      S.initFinalSymbol("ContainsOnly", ID.ContainsOnly);

  /**
   * Context(symbol) - yields the name of the context where `symbol` is defined in.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Context.md">Context
   *      documentation</a>
   */
  public final static IBuiltInSymbol Context = S.initFinalSymbol("Context", ID.Context);

  public final static IBuiltInSymbol Contexts = S.initFinalSymbol("Contexts", ID.Contexts);

  /**
   * Continue() - continues with the next iteration in a `For`, `While`, or `Do` loop.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Continue.md">Continue
   *      documentation</a>
   */
  public final static IBuiltInSymbol Continue = S.initFinalSymbol("Continue", ID.Continue);

  /**
   * ContinuedFraction(number) - the complete continued fraction representation for a rational or
   * quadradic irrational `number`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContinuedFraction.md">ContinuedFraction
   *      documentation</a>
   */
  public final static IBuiltInSymbol ContinuedFraction =
      S.initFinalSymbol("ContinuedFraction", ID.ContinuedFraction);

  public final static IBuiltInSymbol ContourPlot = S.initFinalSymbol("ContourPlot", ID.ContourPlot);

  /**
   * Convergents({n1, n2, ...}) - return the list of convergents which represents the continued
   * fraction list `{n1, n2, ...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Convergents.md">Convergents
   *      documentation</a>
   */
  public final static IBuiltInSymbol Convergents = S.initFinalSymbol("Convergents", ID.Convergents);

  public final static IBuiltInSymbol ConvexHullMesh =
      S.initFinalSymbol("ConvexHullMesh", ID.ConvexHullMesh);

  /**
   * CoordinateBoundingBox({{x1,y1,...},{x2,y2,...},{x3,y3,...},...}) - calculate the bounding box
   * of the points `{{x1,y1,...},{x2,y2,...},{x3,y3,...},...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoordinateBoundingBox.md">CoordinateBoundingBox
   *      documentation</a>
   */
  public final static IBuiltInSymbol CoordinateBoundingBox =
      S.initFinalSymbol("CoordinateBoundingBox", ID.CoordinateBoundingBox);

  public final static IBuiltInSymbol CoordinateBounds =
      S.initFinalSymbol("CoordinateBounds", ID.CoordinateBounds);

  /**
   * CoplanarPoints({{x1,y1,z1},{x2,y2,z2},{x3,y3,z3},{a,b,c},...}) - returns true if the point
   * `{a,b,c]` is on the plane defined by the first three points `{x1,y1,z1},{x2,y2,z2},{x3,y3,z3}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoplanarPoints.md">CoplanarPoints
   *      documentation</a>
   */
  public final static IBuiltInSymbol CoplanarPoints =
      S.initFinalSymbol("CoplanarPoints", ID.CoplanarPoints);

  /**
   * CoprimeQ(x, y) - tests whether `x` and `y` are coprime by computing their greatest common
   * divisor.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoprimeQ.md">CoprimeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol CoprimeQ = S.initFinalSymbol("CoprimeQ", ID.CoprimeQ);

  /**
   * Correlation(a, b) - computes Pearson's correlation of two equal-sized vectors `a` and `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Correlation.md">Correlation
   *      documentation</a>
   */
  public final static IBuiltInSymbol Correlation = S.initFinalSymbol("Correlation", ID.Correlation);

  /**
   * CorrelationDistance(u, v) - returns the correlation distance between `u` and `v`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CorrelationDistance.md">CorrelationDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol CorrelationDistance =
      S.initFinalSymbol("CorrelationDistance", ID.CorrelationDistance);

  /**
   * Cos(expr) - returns the cosine of `expr` (measured in radians). `Cos(expr)` will evaluate
   * automatically in the case `expr` is a multiple of `Pi, Pi/2, Pi/3, Pi/4` and `Pi/6`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cos.md">Cos
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cos = S.initFinalSymbol("Cos", ID.Cos);

  /**
   * Cosh(z) - returns the hyperbolic cosine of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cosh.md">Cosh
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cosh = S.initFinalSymbol("Cosh", ID.Cosh);

  /**
   * CoshIntegral(expr) - returns the hyperbolic cosine integral of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoshIntegral.md">CoshIntegral
   *      documentation</a>
   */
  public final static IBuiltInSymbol CoshIntegral =
      S.initFinalSymbol("CoshIntegral", ID.CoshIntegral);

  /**
   * CosineDistance(u, v) - returns the cosine distance between `u` and `v`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CosineDistance.md">CosineDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol CosineDistance =
      S.initFinalSymbol("CosineDistance", ID.CosineDistance);

  /**
   * CosIntegral(expr) - returns the cosine integral of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CosIntegral.md">CosIntegral
   *      documentation</a>
   */
  public final static IBuiltInSymbol CosIntegral = S.initFinalSymbol("CosIntegral", ID.CosIntegral);

  /**
   * Cot(expr) - the cotangent function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cot.md">Cot
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cot = S.initFinalSymbol("Cot", ID.Cot);

  /**
   * Coth(z) - returns the hyperbolic cotangent of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Coth.md">Coth
   *      documentation</a>
   */
  public final static IBuiltInSymbol Coth = S.initFinalSymbol("Coth", ID.Coth);

  /**
   * Count(list, pattern) - returns the number of times `pattern` appears in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Count.md">Count
   *      documentation</a>
   */
  public final static IBuiltInSymbol Count = S.initFinalSymbol("Count", ID.Count);

  /**
   * CountDistinct(list) - returns the number of distinct entries in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CountDistinct.md">CountDistinct
   *      documentation</a>
   */
  public final static IBuiltInSymbol CountDistinct =
      S.initFinalSymbol("CountDistinct", ID.CountDistinct);

  /**
   * Counts({elem1, elem2, elem3, ...}) - count the number of each distinct element in the list
   * `{elem1, elem2, elem3, ...}` and return the result as an association `<|elem1->counter1,
   * ...|>`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Counts.md">Counts
   *      documentation</a>
   */
  public final static IBuiltInSymbol Counts = S.initFinalSymbol("Counts", ID.Counts);

  /**
   * Covariance(a, b) - computes the covariance between the equal-sized vectors `a` and `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Covariance.md">Covariance
   *      documentation</a>
   */
  public final static IBuiltInSymbol Covariance = S.initFinalSymbol("Covariance", ID.Covariance);

  public final static IBuiltInSymbol CreateDirectory =
      S.initFinalSymbol("CreateDirectory", ID.CreateDirectory);

  public final static IBuiltInSymbol CreateFile = S.initFinalSymbol("CreateFile", ID.CreateFile);

  /**
   * Cross(a, b) - computes the vector cross product of `a` and `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cross.md">Cross
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cross = S.initFinalSymbol("Cross", ID.Cross);

  /**
   * Csc(z) - returns the cosecant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Csc.md">Csc
   *      documentation</a>
   */
  public final static IBuiltInSymbol Csc = S.initFinalSymbol("Csc", ID.Csc);

  /**
   * Csch(z) - returns the hyperbolic cosecant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Csch.md">Csch
   *      documentation</a>
   */
  public final static IBuiltInSymbol Csch = S.initFinalSymbol("Csch", ID.Csch);

  public final static IBuiltInSymbol Cube = S.initFinalSymbol("Cube", ID.Cube);

  /**
   * CubeRoot(n) - finds the real-valued cube root of the given `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CubeRoot.md">CubeRoot
   *      documentation</a>
   */
  public final static IBuiltInSymbol CubeRoot = S.initFinalSymbol("CubeRoot", ID.CubeRoot);

  /**
   * Cuboid({xmin, ymin, zmin}) - is a unit cube.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cuboid.md">Cuboid
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cuboid = S.initFinalSymbol("Cuboid", ID.Cuboid);

  /**
   * Curl({f1, f2}, {x1, x2}) - returns the curl `D(f2, x1) - D(f1, x2)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Curl.md">Curl
   *      documentation</a>
   */
  public final static IBuiltInSymbol Curl = S.initFinalSymbol("Curl", ID.Curl);

  public final static IBuiltInSymbol Cyan = S.initFinalSymbol("Cyan", ID.Cyan);

  /**
   * CycleGraph(order) - returns the cycle graph with `order` vertices.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CycleGraph.md">CycleGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol CycleGraph = S.initFinalSymbol("CycleGraph", ID.CycleGraph);

  /**
   * Cycles(a, b) - expression for defining canonical cycles of a permutation.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cycles.md">Cycles
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cycles = S.initFinalSymbol("Cycles", ID.Cycles);

  /**
   * Cyclotomic(n, x) - returns the Cyclotomic polynomial `C_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cyclotomic.md">Cyclotomic
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cyclotomic = S.initFinalSymbol("Cyclotomic", ID.Cyclotomic);

  /**
   * Cylinder({{x1, y1, z1}, {x2, y2, z2}}) - represents a cylinder of radius `1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cylinder.md">Cylinder
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cylinder = S.initFinalSymbol("Cylinder", ID.Cylinder);

  /**
   * D(f, x) - gives the partial derivative of `f` with respect to `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/D.md">D
   *      documentation</a>
   */
  public final static IBuiltInSymbol D = S.initFinalSymbol("D", ID.D);

  public final static IBuiltInSymbol Dashed = S.initFinalSymbol("Dashed", ID.Dashed);

  public final static IBuiltInSymbol Dashing = S.initFinalSymbol("Dashing", ID.Dashing);

  public final static IBuiltInSymbol DataRange = S.initFinalSymbol("DataRange", ID.DataRange);

  /**
   * - create a `Dataset` object from the `association`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dataset.md">Dataset
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dataset = S.initFinalSymbol("Dataset", ID.Dataset);

  public final static IBuiltInSymbol DateObject = S.initFinalSymbol("DateObject", ID.DateObject);

  public final static IBuiltInSymbol DateString = S.initFinalSymbol("DateString", ID.DateString);

  public final static IBuiltInSymbol DateValue = S.initFinalSymbol("DateValue", ID.DateValue);

  /**
   * Decrement(x) - decrements `x` by `1`, returning the original value of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Decrement.md">Decrement
   *      documentation</a>
   */
  public final static IBuiltInSymbol Decrement = S.initFinalSymbol("Decrement", ID.Decrement);

  /**
   * DedekindNumber(n) - returns the `n`th Dedekind number. Currently `0 <= n <= 9` can be computed,
   * otherwise the function returns unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DedekindNumber.md">DedekindNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol DedekindNumber =
      S.initFinalSymbol("DedekindNumber", ID.DedekindNumber);

  /**
   * Default(symbol) - `Default` returns the default value associated with the `symbol` for a
   * pattern default `_.` expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Default.md">Default
   *      documentation</a>
   */
  public final static IBuiltInSymbol Default = S.initFinalSymbol("Default", ID.Default);

  public final static IBuiltInSymbol DefaultButton =
      S.initFinalSymbol("DefaultButton", ID.DefaultButton);

  public final static IBuiltInSymbol DefaultValue =
      S.initFinalSymbol("DefaultValue", ID.DefaultValue);

  /**
   * Defer(expr) - `Defer` doesn't evaluate `expr` and didn't appear in the output
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Defer.md">Defer
   *      documentation</a>
   */
  public final static IBuiltInSymbol Defer = S.initFinalSymbol("Defer", ID.Defer);

  /**
   * Definition(symbol) - prints user-defined values and rules associated with `symbol`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Definition.md">Definition
   *      documentation</a>
   */
  public final static IBuiltInSymbol Definition = S.initFinalSymbol("Definition", ID.Definition);

  /**
   * Degree - the constant `Degree` converts angles from degree to `Pi/180` radians.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Degree.md">Degree
   *      documentation</a>
   */
  public final static IBuiltInSymbol Degree = S.initFinalSymbol("Degree", ID.Degree);

  public final static IBuiltInSymbol DegreeLexicographic =
      S.initFinalSymbol("DegreeLexicographic", ID.DegreeLexicographic);

  public final static IBuiltInSymbol DegreeReverseLexicographic =
      S.initFinalSymbol("DegreeReverseLexicographic", ID.DegreeReverseLexicographic);

  /**
   * Delete(expr, n) - returns `expr` with part `n` removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Delete.md">Delete
   *      documentation</a>
   */
  public final static IBuiltInSymbol Delete = S.initFinalSymbol("Delete", ID.Delete);

  /**
   * DeleteCases(list, pattern) - returns the elements of `list` that do not match `pattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteCases.md">DeleteCases
   *      documentation</a>
   */
  public final static IBuiltInSymbol DeleteCases = S.initFinalSymbol("DeleteCases", ID.DeleteCases);

  /**
   * DeleteDuplicates(list) - deletes duplicates from `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteDuplicates.md">DeleteDuplicates
   *      documentation</a>
   */
  public final static IBuiltInSymbol DeleteDuplicates =
      S.initFinalSymbol("DeleteDuplicates", ID.DeleteDuplicates);

  /**
   * DeleteDuplicatesBy(list, predicate) - deletes duplicates from `list`, for which the `predicate`
   * returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteDuplicatesBy.md">DeleteDuplicatesBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol DeleteDuplicatesBy =
      S.initFinalSymbol("DeleteDuplicatesBy", ID.DeleteDuplicatesBy);

  public final static IBuiltInSymbol DeleteMissing =
      S.initFinalSymbol("DeleteMissing", ID.DeleteMissing);

  public final static IBuiltInSymbol Delimiters = S.initFinalSymbol("Delimiters", ID.Delimiters);

  /**
   * Denominator(expr) - gives the denominator in `expr`. Denominator collects expressions with
   * negative exponents.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Denominator.md">Denominator
   *      documentation</a>
   */
  public final static IBuiltInSymbol Denominator = S.initFinalSymbol("Denominator", ID.Denominator);

  /**
   * DensityHistogram( list-of-pair-values ) - plot a density histogram for a `list-of-pair-values`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DensityHistogram.md">DensityHistogram
   *      documentation</a>
   */
  public final static IBuiltInSymbol DensityHistogram =
      S.initFinalSymbol("DensityHistogram", ID.DensityHistogram);

  public final static IBuiltInSymbol DensityPlot = S.initFinalSymbol("DensityPlot", ID.DensityPlot);

  /**
   * Depth(expr) - gets the depth of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Depth.md">Depth
   *      documentation</a>
   */
  public final static IBuiltInSymbol Depth = S.initFinalSymbol("Depth", ID.Depth);

  /**
   * Derivative(n)[f] - represents the `n`-th derivative of the function `f`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Derivative.md">Derivative
   *      documentation</a>
   */
  public final static IBuiltInSymbol Derivative = S.initFinalSymbol("Derivative", ID.Derivative);

  /**
   * DesignMatrix(m, f, x) - returns the design matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DesignMatrix.md">DesignMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol DesignMatrix =
      S.initFinalSymbol("DesignMatrix", ID.DesignMatrix);

  /**
   * Det(matrix) - computes the determinant of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Det.md">Det
   *      documentation</a>
   */
  public final static IBuiltInSymbol Det = S.initFinalSymbol("Det", ID.Det);

  /**
   * Diagonal(matrix) - computes the diagonal vector of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Diagonal.md">Diagonal
   *      documentation</a>
   */
  public final static IBuiltInSymbol Diagonal = S.initFinalSymbol("Diagonal", ID.Diagonal);

  /**
   * DiagonalMatrix(list) - gives a matrix with the values in `list` on its diagonal and zeroes
   * elsewhere.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiagonalMatrix.md">DiagonalMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiagonalMatrix =
      S.initFinalSymbol("DiagonalMatrix", ID.DiagonalMatrix);

  /**
   * DiagonalMatrixQ(matrix) - returns `True` if all elements of the `matrix` are `0` except the
   * elements on the `diagonal`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiagonalMatrixQ.md">DiagonalMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiagonalMatrixQ =
      S.initFinalSymbol("DiagonalMatrixQ", ID.DiagonalMatrixQ);

  /**
   * DialogInput() - if the file system is enabled, the user can input a string in a dialog box.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DialogInput.md">DialogInput
   *      documentation</a>
   */
  public final static IBuiltInSymbol DialogInput = S.initFinalSymbol("DialogInput", ID.DialogInput);

  public final static IBuiltInSymbol DialogNotebook =
      S.initFinalSymbol("DialogNotebook", ID.DialogNotebook);

  public final static IBuiltInSymbol DialogReturn =
      S.initFinalSymbol("DialogReturn", ID.DialogReturn);

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
      S.initFinalSymbol("DiceDissimilarity", ID.DiceDissimilarity);

  /**
   * DifferenceDelta(f(x), x) - generates a forward difference `f(x+1) - f(x)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DifferenceDelta.md">DifferenceDelta
   *      documentation</a>
   */
  public final static IBuiltInSymbol DifferenceDelta =
      S.initFinalSymbol("DifferenceDelta", ID.DifferenceDelta);

  public final static IBuiltInSymbol Differences = S.initFinalSymbol("Differences", ID.Differences);

  /**
   * DigitCharacter - represents the digits 0-9.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitCharacter.md">DigitCharacter
   *      documentation</a>
   */
  public final static IBuiltInSymbol DigitCharacter =
      S.initFinalSymbol("DigitCharacter", ID.DigitCharacter);

  /**
   * DigitCount(n) - returns a list of the number of integer digits for `n` for `radix` 10.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitCount.md">DigitCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol DigitCount = S.initFinalSymbol("DigitCount", ID.DigitCount);

  /**
   * DigitQ(str) - returns `True` if `str` is a string which contains only digits.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitQ.md">DigitQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol DigitQ = S.initFinalSymbol("DigitQ", ID.DigitQ);

  /**
   * Dimensions(expr) - returns a list of the dimensions of the expression `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dimensions.md">Dimensions
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dimensions = S.initFinalSymbol("Dimensions", ID.Dimensions);

  /**
   * DiracDelta(x) - `DiracDelta` function returns `0` for all real numbers `x` where `x != 0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiracDelta.md">DiracDelta
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiracDelta = S.initFinalSymbol("DiracDelta", ID.DiracDelta);

  /**
   * DirectedEdge(a, b) - is a directed edge from vertex `a` to vertex `b` in a `graph` object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DirectedEdge.md">DirectedEdge
   *      documentation</a>
   */
  public final static IBuiltInSymbol DirectedEdge =
      S.initFinalSymbol("DirectedEdge", ID.DirectedEdge);

  public final static IBuiltInSymbol DirectedEdges =
      S.initFinalSymbol("DirectedEdges", ID.DirectedEdges);

  /**
   * DirectedInfinity(z) - represents an infinite multiple of the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DirectedInfinity.md">DirectedInfinity
   *      documentation</a>
   */
  public final static IBuiltInSymbol DirectedInfinity =
      S.initFinalSymbol("DirectedInfinity", ID.DirectedInfinity);

  public final static IBuiltInSymbol Direction = S.initFinalSymbol("Direction", ID.Direction);

  public final static IBuiltInSymbol DirectionalLight =
      S.initFinalSymbol("DirectionalLight", ID.DirectionalLight);

  public final static IBuiltInSymbol Directive = S.initFinalSymbol("Directive", ID.Directive);

  public final static IBuiltInSymbol DirichletBeta =
      S.initFinalSymbol("DirichletBeta", ID.DirichletBeta);

  public final static IBuiltInSymbol DirichletEta =
      S.initFinalSymbol("DirichletEta", ID.DirichletEta);

  public final static IBuiltInSymbol DirichletWindow =
      S.initFinalSymbol("DirichletWindow", ID.DirichletWindow);

  /**
   * DiscreteDelta(n1, n2, n3, ...) - `DiscreteDelta` function returns `1` if all the `ni` are `0`.
   * Returns `0` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscreteDelta.md">DiscreteDelta
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiscreteDelta =
      S.initFinalSymbol("DiscreteDelta", ID.DiscreteDelta);

  /**
   * DiscretePlot( expr, {x, nmax} ) - plots `expr` with `x` ranging from `1` to `nmax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscretePlot.md">DiscretePlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiscretePlot =
      S.initFinalSymbol("DiscretePlot", ID.DiscretePlot);

  /**
   * DiscreteUniformDistribution({min, max}) - returns a discrete uniform distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscreteUniformDistribution.md">DiscreteUniformDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiscreteUniformDistribution =
      S.initFinalSymbol("DiscreteUniformDistribution", ID.DiscreteUniformDistribution);

  /**
   * Discriminant(poly, var) - computes the discriminant of the polynomial `poly` with respect to
   * the variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Discriminant.md">Discriminant
   *      documentation</a>
   */
  public final static IBuiltInSymbol Discriminant =
      S.initFinalSymbol("Discriminant", ID.Discriminant);

  public final static IBuiltInSymbol DisjointQ = S.initFinalSymbol("DisjointQ", ID.DisjointQ);

  public final static IBuiltInSymbol Disk = S.initFinalSymbol("Disk", ID.Disk);

  /**
   * Dispatch({rule1, rule2, ...}) - create a dispatch map for a list of rules.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dispatch.md">Dispatch
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dispatch = S.initFinalSymbol("Dispatch", ID.Dispatch);

  public final static IBuiltInSymbol DisplayForm = S.initFinalSymbol("DisplayForm", ID.DisplayForm);

  public final static IBuiltInSymbol DisplayFunction =
      S.initFinalSymbol("DisplayFunction", ID.DisplayFunction);

  public final static IBuiltInSymbol Disputed = S.initFinalSymbol("Disputed", ID.Disputed);

  public final static IBuiltInSymbol DistanceFunction =
      S.initFinalSymbol("DistanceFunction", ID.DistanceFunction);

  /**
   * Distribute(f(x1, x2, x3,...)) - distributes `f` over `Plus` appearing in any of the `xi`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Distribute.md">Distribute
   *      documentation</a>
   */
  public final static IBuiltInSymbol Distribute = S.initFinalSymbol("Distribute", ID.Distribute);

  public final static IBuiltInSymbol Distributed = S.initFinalSymbol("Distributed", ID.Distributed);

  /**
   * Div({f1, f2, f3,...},{x1, x2, x3,...}) - compute the divergence.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Div.md">Div
   *      documentation</a>
   */
  public final static IBuiltInSymbol Div = S.initFinalSymbol("Div", ID.Div);

  /**
   * Divide(a, b) - represents the division of `a` by `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divide.md">Divide
   *      documentation</a>
   */
  public final static IBuiltInSymbol Divide = S.initFinalSymbol("Divide", ID.Divide);

  /**
   * DivideBy(x, dx) - is equivalent to `x = x / dx`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivideBy.md">DivideBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol DivideBy = S.initFinalSymbol("DivideBy", ID.DivideBy);

  /**
   * DivideSides(compare-expr, value) - divides all elements of the `compare-expr` by `value`.
   * `compare-expr` can be `True`, `False` or a comparison expression with head `Equal, Unequal,
   * Less, LessEqual, Greater, GreaterEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivideSides.md">DivideSides
   *      documentation</a>
   */
  public final static IBuiltInSymbol DivideSides = S.initFinalSymbol("DivideSides", ID.DivideSides);

  /**
   * Divisible(n, m) - returns `True` if `n` could be divide by `m`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divisible.md">Divisible
   *      documentation</a>
   */
  public final static IBuiltInSymbol Divisible = S.initFinalSymbol("Divisible", ID.Divisible);

  /**
   * Divisors(n) - returns all integers that divide the integer `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divisors.md">Divisors
   *      documentation</a>
   */
  public final static IBuiltInSymbol Divisors = S.initFinalSymbol("Divisors", ID.Divisors);

  /**
   * DivisorSigma(k, n) - returns the sum of the `k`-th powers of the divisors of `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivisorSigma.md">DivisorSigma
   *      documentation</a>
   */
  public final static IBuiltInSymbol DivisorSigma =
      S.initFinalSymbol("DivisorSigma", ID.DivisorSigma);

  /**
   * DivisorSum(n, head) - returns the sum of the divisors of `n`. The `head` is applied to each
   * divisor.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DivisorSum.md">DivisorSum
   *      documentation</a>
   */
  public final static IBuiltInSymbol DivisorSum = S.initFinalSymbol("DivisorSum", ID.DivisorSum);

  /**
   * Do(expr, {max}) - evaluates `expr` `max` times.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Do.md">Do
   *      documentation</a>
   */
  public final static IBuiltInSymbol Do = S.initFinalSymbol("Do", ID.Do);

  public final static IBuiltInSymbol Dodecahedron =
      S.initFinalSymbol("Dodecahedron", ID.Dodecahedron);

  /**
   * Dot(x, y) or x . y - `x . y` computes the vector dot product or matrix product `x . y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dot.md">Dot
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dot = S.initFinalSymbol("Dot", ID.Dot);

  public final static IBuiltInSymbol DotDashed = S.initFinalSymbol("DotDashed", ID.DotDashed);

  public final static IBuiltInSymbol Dotted = S.initFinalSymbol("Dotted", ID.Dotted);

  /**
   * DownValues(symbol) - prints the down-value rules associated with `symbol`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownValues.md">DownValues
   *      documentation</a>
   */
  public final static IBuiltInSymbol DownValues = S.initFinalSymbol("DownValues", ID.DownValues);

  /**
   * Drop(expr, n) - returns `expr` with the first `n` leaves removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Drop.md">Drop
   *      documentation</a>
   */
  public final static IBuiltInSymbol Drop = S.initFinalSymbol("Drop", ID.Drop);

  /**
   * DSolve(equation, f(var), var) - attempts to solve a linear differential `equation` for the
   * function `f(var)` and variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DSolve.md">DSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol DSolve = S.initFinalSymbol("DSolve", ID.DSolve);

  public final static IBuiltInSymbol Dt = S.initFinalSymbol("Dt", ID.Dt);

  public final static IBuiltInSymbol DuplicateFreeQ =
      S.initFinalSymbol("DuplicateFreeQ", ID.DuplicateFreeQ);

  public final static IBuiltInSymbol Dynamic = S.initFinalSymbol("Dynamic", ID.Dynamic);

  /**
   * E - Euler's constant E
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/E.md">E
   *      documentation</a>
   */
  public final static IBuiltInSymbol E = S.initFinalSymbol("E", ID.E);

  public final static IBuiltInSymbol EasterSunday =
      S.initFinalSymbol("EasterSunday", ID.EasterSunday);

  /**
   * Echo(expr) - prints the `expr` to the default output stream and returns `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Echo.md">Echo
   *      documentation</a>
   */
  public final static IBuiltInSymbol Echo = S.initFinalSymbol("Echo", ID.Echo);

  /**
   * EchoFunction()[expr] - operator form of the `Echo`function. Print the `expr` to the default
   * output stream and return `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EchoFunction.md">EchoFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol EchoFunction =
      S.initFinalSymbol("EchoFunction", ID.EchoFunction);

  public final static IBuiltInSymbol EdgeCount = S.initFinalSymbol("EdgeCount", ID.EdgeCount);

  public final static IBuiltInSymbol EdgeForm = S.initFinalSymbol("EdgeForm", ID.EdgeForm);

  public final static IBuiltInSymbol EdgeLabels = S.initFinalSymbol("EdgeLabels", ID.EdgeLabels);

  /**
   * EdgeList(graph) - convert the `graph` into a list of edges.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeList.md">EdgeList
   *      documentation</a>
   */
  public final static IBuiltInSymbol EdgeList = S.initFinalSymbol("EdgeList", ID.EdgeList);

  /**
   * EdgeQ(graph, edge) - test if `edge` is an edge in the `graph` object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeQ.md">EdgeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol EdgeQ = S.initFinalSymbol("EdgeQ", ID.EdgeQ);

  /**
   * EdgeRules(graph) - convert the `graph` into a list of rules. All edge types (undirected,
   * directed) are represented by a rule `lhs->rhs`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeRules.md">EdgeRules
   *      documentation</a>
   */
  public final static IBuiltInSymbol EdgeRules = S.initFinalSymbol("EdgeRules", ID.EdgeRules);

  public final static IBuiltInSymbol EdgeShapeFunction =
      S.initFinalSymbol("EdgeShapeFunction", ID.EdgeShapeFunction);

  public final static IBuiltInSymbol EdgeStyle = S.initFinalSymbol("EdgeStyle", ID.EdgeStyle);

  public final static IBuiltInSymbol EdgeWeight = S.initFinalSymbol("EdgeWeight", ID.EdgeWeight);

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
      S.initFinalSymbol("EditDistance", ID.EditDistance);

  /**
   * EffectiveInterest(i, n) - returns an effective interest rate object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EffectiveInterest.md">EffectiveInterest
   *      documentation</a>
   */
  public final static IBuiltInSymbol EffectiveInterest =
      S.initFinalSymbol("EffectiveInterest", ID.EffectiveInterest);

  /**
   * Eigensystem(matrix) - return the numerical eigensystem of the `matrix` as a list `{eigenvalues,
   * eigenvectors}`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigensystem.md">Eigensystem
   *      documentation</a>
   */
  public final static IBuiltInSymbol Eigensystem = S.initFinalSymbol("Eigensystem", ID.Eigensystem);

  /**
   * Eigenvalues(matrix) - get the numerical eigenvalues of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigenvalues.md">Eigenvalues
   *      documentation</a>
   */
  public final static IBuiltInSymbol Eigenvalues = S.initFinalSymbol("Eigenvalues", ID.Eigenvalues);

  public final static IBuiltInSymbol EigenvectorCentrality =
      S.initFinalSymbol("EigenvectorCentrality", ID.EigenvectorCentrality);

  /**
   * Eigenvectors(matrix) - get the numerical eigenvectors of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eigenvectors.md">Eigenvectors
   *      documentation</a>
   */
  public final static IBuiltInSymbol Eigenvectors =
      S.initFinalSymbol("Eigenvectors", ID.Eigenvectors);

  /**
   * Element(symbol, domain) - assume (or test) that the `symbol` is in the domain `domain`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Element.md">Element
   *      documentation</a>
   */
  public final static IBuiltInSymbol Element = S.initFinalSymbol("Element", ID.Element);

  /**
   * ElementData("name", "property") - gives the value of the property for the chemical specified by
   * name.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ElementData.md">ElementData
   *      documentation</a>
   */
  public final static IBuiltInSymbol ElementData = S.initFinalSymbol("ElementData", ID.ElementData);

  /**
   * Eliminate(list-of-equations, list-of-variables) - attempts to eliminate the variables from the
   * `list-of-variables` in the `list-of-equations`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Eliminate.md">Eliminate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Eliminate = S.initFinalSymbol("Eliminate", ID.Eliminate);

  public final static IBuiltInSymbol EliminationOrder =
      S.initFinalSymbol("EliminationOrder", ID.EliminationOrder);

  public final static IBuiltInSymbol Ellipsoid = S.initFinalSymbol("Ellipsoid", ID.Ellipsoid);

  /**
   * EllipticE(z) - returns the complete elliptic integral of the second kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticE.md">EllipticE
   *      documentation</a>
   */
  public final static IBuiltInSymbol EllipticE = S.initFinalSymbol("EllipticE", ID.EllipticE);

  /**
   * EllipticF(z) - returns the incomplete elliptic integral of the first kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticF.md">EllipticF
   *      documentation</a>
   */
  public final static IBuiltInSymbol EllipticF = S.initFinalSymbol("EllipticF", ID.EllipticF);

  /**
   * EllipticK(z) - returns the complete elliptic integral of the first kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticK.md">EllipticK
   *      documentation</a>
   */
  public final static IBuiltInSymbol EllipticK = S.initFinalSymbol("EllipticK", ID.EllipticK);

  /**
   * EllipticPi(n,m) - returns the complete elliptic integral of the third kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticPi.md">EllipticPi
   *      documentation</a>
   */
  public final static IBuiltInSymbol EllipticPi = S.initFinalSymbol("EllipticPi", ID.EllipticPi);

  public final static IBuiltInSymbol EllipticTheta =
      S.initFinalSymbol("EllipticTheta", ID.EllipticTheta);

  public final static IBuiltInSymbol EmpiricalDistribution =
      S.initFinalSymbol("EmpiricalDistribution", ID.EmpiricalDistribution);

  /**
   * End( ) - end a context definition started with `Begin`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/End.md">End
   *      documentation</a>
   */
  public final static IBuiltInSymbol End = S.initFinalSymbol("End", ID.End);

  public final static IBuiltInSymbol EndOfFile = S.initFinalSymbol("EndOfFile", ID.EndOfFile);

  public final static IBuiltInSymbol EndOfLine = S.initFinalSymbol("EndOfLine", ID.EndOfLine);

  public final static IBuiltInSymbol EndOfString = S.initFinalSymbol("EndOfString", ID.EndOfString);

  /**
   * EndPackage( ) - end a package definition
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EndPackage.md">EndPackage
   *      documentation</a>
   */
  public final static IBuiltInSymbol EndPackage = S.initFinalSymbol("EndPackage", ID.EndPackage);

  public final static IBuiltInSymbol EndTestSection =
      S.initFinalSymbol("EndTestSection", ID.EndTestSection);

  public final static IBuiltInSymbol Entity = S.initFinalSymbol("Entity", ID.Entity);

  /**
   * Entropy(list) - return the base `E` (Shannon) information entropy of the elements in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Entropy.md">Entropy
   *      documentation</a>
   */
  public final static IBuiltInSymbol Entropy = S.initFinalSymbol("Entropy", ID.Entropy);

  /**
   * Equal(x, y) - yields `True` if `x` and `y` are known to be equal, or `False` if `x` and `y` are
   * known to be unequal.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Equal.md">Equal
   *      documentation</a>
   */
  public final static IBuiltInSymbol Equal = S.initFinalSymbol("Equal", ID.Equal);

  public final static IBuiltInSymbol EqualTo = S.initFinalSymbol("EqualTo", ID.EqualTo);

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
  public final static IBuiltInSymbol Equivalent = S.initFinalSymbol("Equivalent", ID.Equivalent);

  /**
   * Erf(z) - returns the error function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erf.md">Erf
   *      documentation</a>
   */
  public final static IBuiltInSymbol Erf = S.initFinalSymbol("Erf", ID.Erf);

  /**
   * Erfc(z) - returns the complementary error function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erfc.md">Erfc
   *      documentation</a>
   */
  public final static IBuiltInSymbol Erfc = S.initFinalSymbol("Erfc", ID.Erfc);

  /**
   * Erfi(z) - returns the imaginary error function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erfi.md">Erfi
   *      documentation</a>
   */
  public final static IBuiltInSymbol Erfi = S.initFinalSymbol("Erfi", ID.Erfi);

  /**
   * ErlangDistribution({k, lambda}) - returns a Erlang distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ErlangDistribution.md">ErlangDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol ErlangDistribution =
      S.initFinalSymbol("ErlangDistribution", ID.ErlangDistribution);

  /**
   * EuclideanDistance(u, v) - returns the euclidean distance between `u` and `v`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EuclideanDistance.md">EuclideanDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol EuclideanDistance =
      S.initFinalSymbol("EuclideanDistance", ID.EuclideanDistance);

  /**
   * EulerE(n) - gives the euler number `En`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerE.md">EulerE
   *      documentation</a>
   */
  public final static IBuiltInSymbol EulerE = S.initFinalSymbol("EulerE", ID.EulerE);

  /**
   * EulerGamma - Euler-Mascheroni constant
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerGamma.md">EulerGamma
   *      documentation</a>
   */
  public final static IBuiltInSymbol EulerGamma = S.initFinalSymbol("EulerGamma", ID.EulerGamma);

  /**
   * EulerianGraphQ(graph) - returns `True` if `graph` is an eulerian graph, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerianGraphQ.md">EulerianGraphQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol EulerianGraphQ =
      S.initFinalSymbol("EulerianGraphQ", ID.EulerianGraphQ);

  /**
   * EulerPhi(n) - compute Euler's totient function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EulerPhi.md">EulerPhi
   *      documentation</a>
   */
  public final static IBuiltInSymbol EulerPhi = S.initFinalSymbol("EulerPhi", ID.EulerPhi);

  /**
   * Evaluate(expr) - the `Evaluate` function will be executed even if the function attributes
   * `HoldFirst, HoldRest, HoldAll` are set for the function head.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Evaluate.md">Evaluate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Evaluate = S.initFinalSymbol("Evaluate", ID.Evaluate);

  /**
   * EvenQ(x) - returns `True` if `x` is even, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EvenQ.md">EvenQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol EvenQ = S.initFinalSymbol("EvenQ", ID.EvenQ);

  /**
   * ExactNumberQ(expr) - returns `True` if `expr` is an exact number, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExactNumberQ.md">ExactNumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExactNumberQ =
      S.initFinalSymbol("ExactNumberQ", ID.ExactNumberQ);

  /**
   * Except(c) - represents a pattern object that matches any expression except those matching `c`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Except.md">Except
   *      documentation</a>
   */
  public final static IBuiltInSymbol Except = S.initFinalSymbol("Except", ID.Except);

  public final static IBuiltInSymbol Exists = S.initFinalSymbol("Exists", ID.Exists);

  public final static IBuiltInSymbol Exit = S.initFinalSymbol("Exit", ID.Exit);

  /**
   * Exp(z) - the exponential function `E^z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Exp.md">Exp
   *      documentation</a>
   */
  public final static IBuiltInSymbol Exp = S.initFinalSymbol("Exp", ID.Exp);

  /**
   * Expand(expr) - expands out positive rational powers and products of sums in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expand.md">Expand
   *      documentation</a>
   */
  public final static IBuiltInSymbol Expand = S.initFinalSymbol("Expand", ID.Expand);

  /**
   * ExpandAll(expr) - expands out all positive integer powers and products of sums in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpandAll.md">ExpandAll
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExpandAll = S.initFinalSymbol("ExpandAll", ID.ExpandAll);

  public final static IBuiltInSymbol ExpandDenominator =
      S.initFinalSymbol("ExpandDenominator", ID.ExpandDenominator);

  public final static IBuiltInSymbol ExpandNumerator =
      S.initFinalSymbol("ExpandNumerator", ID.ExpandNumerator);

  /**
   * Expectation(pure-function, data-set) - returns the expected value of the `pure-function` for
   * the given `data-set`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Expectation.md">Expectation
   *      documentation</a>
   */
  public final static IBuiltInSymbol Expectation = S.initFinalSymbol("Expectation", ID.Expectation);

  /**
   * ExpIntegralE(n, expr) - returns the exponential integral `E_n(expr)` of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpIntegralE.md">ExpIntegralE
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExpIntegralE =
      S.initFinalSymbol("ExpIntegralE", ID.ExpIntegralE);

  /**
   * ExpIntegralEi(expr) - returns the exponential integral `Ei(expr)` of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpIntegralEi.md">ExpIntegralEi
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExpIntegralEi =
      S.initFinalSymbol("ExpIntegralEi", ID.ExpIntegralEi);

  /**
   * Exponent(polynomial, x) - gives the maximum power with which `x` appears in the expanded form
   * of `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Exponent.md">Exponent
   *      documentation</a>
   */
  public final static IBuiltInSymbol Exponent = S.initFinalSymbol("Exponent", ID.Exponent);

  /**
   * ExponentialDistribution(lambda) - returns an exponential distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExponentialDistribution.md">ExponentialDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExponentialDistribution =
      S.initFinalSymbol("ExponentialDistribution", ID.ExponentialDistribution);

  /**
   * Export("path-to-filename", expression, "WXF") - if the file system is enabled, export the
   * `expression` in WXF format to the "path-to-filename" file.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Export.md">Export
   *      documentation</a>
   */
  public final static IBuiltInSymbol Export = S.initFinalSymbol("Export", ID.Export);

  public final static IBuiltInSymbol ExportString =
      S.initFinalSymbol("ExportString", ID.ExportString);

  public final static IBuiltInSymbol Expression = S.initFinalSymbol("Expression", ID.Expression);

  public final static IBuiltInSymbol ExpToTrig = S.initFinalSymbol("ExpToTrig", ID.ExpToTrig);

  /**
   * ExtendedGCD(n1, n2, ...) - computes the extended greatest common divisor of the given integers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExtendedGCD.md">ExtendedGCD
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExtendedGCD = S.initFinalSymbol("ExtendedGCD", ID.ExtendedGCD);

  public final static IBuiltInSymbol Extension = S.initFinalSymbol("Extension", ID.Extension);

  /**
   * Extract(expr, list) - extracts parts of `expr` specified by `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Extract.md">Extract
   *      documentation</a>
   */
  public final static IBuiltInSymbol Extract = S.initFinalSymbol("Extract", ID.Extract);

  /**
   * Factor(expr) - factors the polynomial expression `expr`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factor.md">Factor
   *      documentation</a>
   */
  public final static IBuiltInSymbol Factor = S.initFinalSymbol("Factor", ID.Factor);

  /**
   * Factorial(n) - returns the factorial number of the integer `n`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factorial.md">Factorial
   *      documentation</a>
   */
  public final static IBuiltInSymbol Factorial = S.initFinalSymbol("Factorial", ID.Factorial);

  /**
   * Factorial2(n) - returns the double factorial number of the integer `n` as `n*(n-2)*(n-4)...`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Factorial2.md">Factorial2
   *      documentation</a>
   */
  public final static IBuiltInSymbol Factorial2 = S.initFinalSymbol("Factorial2", ID.Factorial2);

  public final static IBuiltInSymbol FactorialPower =
      S.initFinalSymbol("FactorialPower", ID.FactorialPower);

  /**
   * FactorInteger(n) - returns the factorization of `n` as a list of factors and exponents.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorInteger.md">FactorInteger
   *      documentation</a>
   */
  public final static IBuiltInSymbol FactorInteger =
      S.initFinalSymbol("FactorInteger", ID.FactorInteger);

  /**
   * FactorSquareFree(polynomial) - factor the polynomial expression `polynomial` square free.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorSquareFree.md">FactorSquareFree
   *      documentation</a>
   */
  public final static IBuiltInSymbol FactorSquareFree =
      S.initFinalSymbol("FactorSquareFree", ID.FactorSquareFree);

  /**
   * FactorSquareFreeList(polynomial) - get the square free factors of the polynomial expression
   * `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorSquareFreeList.md">FactorSquareFreeList
   *      documentation</a>
   */
  public final static IBuiltInSymbol FactorSquareFreeList =
      S.initFinalSymbol("FactorSquareFreeList", ID.FactorSquareFreeList);

  /**
   * FactorTerms(poly) - pulls out any overall numerical factor in `poly`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorTerms.md">FactorTerms
   *      documentation</a>
   */
  public final static IBuiltInSymbol FactorTerms = S.initFinalSymbol("FactorTerms", ID.FactorTerms);

  /**
   * FactorTermsList(poly) - pulls out any overall numerical factor in `poly` and returns the result
   * in a list.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorTermsList.md">FactorTermsList
   *      documentation</a>
   */
  public final static IBuiltInSymbol FactorTermsList =
      S.initFinalSymbol("FactorTermsList", ID.FactorTermsList);

  /**
   * False - the constant `False` represents the boolean value **false**
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/False.md">False
   *      documentation</a>
   */
  public final static IBuiltInSymbol False = S.initFinalSymbol("False", ID.False);

  /**
   * Fibonacci(n) - returns the Fibonacci number of the integer `n`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fibonacci.md">Fibonacci
   *      documentation</a>
   */
  public final static IBuiltInSymbol Fibonacci = S.initFinalSymbol("Fibonacci", ID.Fibonacci);

  public final static IBuiltInSymbol File = S.initFinalSymbol("File", ID.File);

  public final static IBuiltInSymbol FileFormat = S.initFinalSymbol("FileFormat", ID.FileFormat);

  public final static IBuiltInSymbol FileHash = S.initFinalSymbol("FileHash", ID.FileHash);

  public final static IBuiltInSymbol FileNameDrop =
      S.initFinalSymbol("FileNameDrop", ID.FileNameDrop);

  public final static IBuiltInSymbol FileNameJoin =
      S.initFinalSymbol("FileNameJoin", ID.FileNameJoin);

  /**
   * FileNames( ) - returns a list with the filenames in the current working folder..
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FileNames.md">FileNames
   *      documentation</a>
   */
  public final static IBuiltInSymbol FileNames = S.initFinalSymbol("FileNames", ID.FileNames);

  public final static IBuiltInSymbol FileNameTake =
      S.initFinalSymbol("FileNameTake", ID.FileNameTake);

  /**
   * FilePrint(file) - prints the raw contents of `file`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FilePrint.md">FilePrint
   *      documentation</a>
   */
  public final static IBuiltInSymbol FilePrint = S.initFinalSymbol("FilePrint", ID.FilePrint);

  public final static IBuiltInSymbol Filling = S.initFinalSymbol("Filling", ID.Filling);

  public final static IBuiltInSymbol FillingStyle =
      S.initFinalSymbol("FillingStyle", ID.FillingStyle);

  /**
   * FilterRules(list-of-option-rules, list-of-rules) - filter the `list-of-option-rules` by
   * `list-of-rules`or `list-of-symbols`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FilterRules.md">FilterRules
   *      documentation</a>
   */
  public final static IBuiltInSymbol FilterRules = S.initFinalSymbol("FilterRules", ID.FilterRules);

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
      S.initFinalSymbol("FindClusters", ID.FindClusters);

  public final static IBuiltInSymbol FindCycle = S.initFinalSymbol("FindCycle", ID.FindCycle);

  public final static IBuiltInSymbol FindEdgeCover =
      S.initFinalSymbol("FindEdgeCover", ID.FindEdgeCover);

  /**
   * FindEulerianCycle(graph) - find an eulerian cycle in the `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindEulerianCycle.md">FindEulerianCycle
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindEulerianCycle =
      S.initFinalSymbol("FindEulerianCycle", ID.FindEulerianCycle);

  /**
   * FindFit(list-of-data-points, function, parameters, variable) - solve a least squares problem
   * using the Levenberg-Marquardt algorithm.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindFit.md">FindFit
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindFit = S.initFinalSymbol("FindFit", ID.FindFit);

  public final static IBuiltInSymbol FindFormula = S.initFinalSymbol("FindFormula", ID.FindFormula);

  public final static IBuiltInSymbol FindGraphCommunities =
      S.initFinalSymbol("FindGraphCommunities", ID.FindGraphCommunities);

  /**
   * FindGraphIsomorphism(graph1, graph2) - returns an isomorphism between `graph1` and `graph2` if
   * it exists. Return an empty list if no isomorphism exists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindGraphIsomorphism.md">FindGraphIsomorphism
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindGraphIsomorphism =
      S.initFinalSymbol("FindGraphIsomorphism", ID.FindGraphIsomorphism);

  /**
   * FindHamiltonianCycle(graph) - find an hamiltonian cycle in the `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindHamiltonianCycle.md">FindHamiltonianCycle
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindHamiltonianCycle =
      S.initFinalSymbol("FindHamiltonianCycle", ID.FindHamiltonianCycle);

  public final static IBuiltInSymbol FindIndependentEdgeSet =
      S.initFinalSymbol("FindIndependentEdgeSet", ID.FindIndependentEdgeSet);

  public final static IBuiltInSymbol FindIndependentVertexSet =
      S.initFinalSymbol("FindIndependentVertexSet", ID.FindIndependentVertexSet);

  /**
   * FindInstance(equations, vars) - attempts to find one solution which solves the `equations` for
   * the variables `vars`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindInstance.md">FindInstance
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindInstance =
      S.initFinalSymbol("FindInstance", ID.FindInstance);

  /**
   * FindLinearRecurrence(list) - compute a minimal linear recurrence which returns list.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindLinearRecurrence.md">FindLinearRecurrence
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindLinearRecurrence =
      S.initFinalSymbol("FindLinearRecurrence", ID.FindLinearRecurrence);

  /**
   * FindMaximum(f, {x, xstart}) - searches for a local numerical maximum of `f` for the variable
   * `x` and the start value `xstart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindMaximum.md">FindMaximum
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindMaximum = S.initFinalSymbol("FindMaximum", ID.FindMaximum);

  /**
   * FindMinimum(f, {x, xstart}) - searches for a local numerical minimum of `f` for the variable
   * `x` and the start value `xstart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindMinimum.md">FindMinimum
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindMinimum = S.initFinalSymbol("FindMinimum", ID.FindMinimum);

  public final static IBuiltInSymbol FindMinimumCostFlow =
      S.initFinalSymbol("FindMinimumCostFlow", ID.FindMinimumCostFlow);

  /**
   * FindPermutation(list1, list2) - create a `Cycles({{...},{...}, ...})` permutation expression,
   * for two lists whose arguments are the same but may be differently arranged.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindPermutation.md">FindPermutation
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindPermutation =
      S.initFinalSymbol("FindPermutation", ID.FindPermutation);

  /**
   * FindRoot(f, {x, xmin, xmax}) - searches for a numerical root of `f` for the variable `x`, in
   * the range `xmin` to `xmax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindRoot.md">FindRoot
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindRoot = S.initFinalSymbol("FindRoot", ID.FindRoot);

  public final static IBuiltInSymbol FindSequenceFunction =
      S.initFinalSymbol("FindSequenceFunction", ID.FindSequenceFunction);

  /**
   * FindShortestPath(graph, source, destination) - find a shortest path in the `graph` from
   * `source` to `destination`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindShortestPath.md">FindShortestPath
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindShortestPath =
      S.initFinalSymbol("FindShortestPath", ID.FindShortestPath);

  /**
   * FindShortestTour({{p11, p12}, {p21, p22}, {p31, p32}, ...}) - find a shortest tour in the
   * `graph` with minimum `EuclideanDistance`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindShortestTour.md">FindShortestTour
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindShortestTour =
      S.initFinalSymbol("FindShortestTour", ID.FindShortestTour);

  /**
   * FindSpanningTree(graph) - find the minimum spanning tree in the `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindSpanningTree.md">FindSpanningTree
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindSpanningTree =
      S.initFinalSymbol("FindSpanningTree", ID.FindSpanningTree);

  /**
   * FindVertexCover(graph) - algorithm to find a vertex cover for a `graph`. A vertex cover is a
   * set of vertices that touches all the edges in the graph.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindVertexCover.md">FindVertexCover
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindVertexCover =
      S.initFinalSymbol("FindVertexCover", ID.FindVertexCover);

  /**
   * First(expr) - returns the first element in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/First.md">First
   *      documentation</a>
   */
  public final static IBuiltInSymbol First = S.initFinalSymbol("First", ID.First);

  /**
   * FirstCase({arg1, arg2, ...}, pattern-matcher) - returns the first of the elements `argi` for
   * which `pattern-matcher` is matching.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FirstCase.md">FirstCase
   *      documentation</a>
   */
  public final static IBuiltInSymbol FirstCase = S.initFinalSymbol("FirstCase", ID.FirstCase);

  /**
   * FirstPosition(expression, pattern-matcher) - returns the first subexpression of `expression`
   * for which `pattern-matcher` is matching.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FirstPosition.md">FirstPosition
   *      documentation</a>
   */
  public final static IBuiltInSymbol FirstPosition =
      S.initFinalSymbol("FirstPosition", ID.FirstPosition);

  /**
   * Fit(list-of-data-points, degree, variable) - solve a least squares problem using the
   * Levenberg-Marquardt algorithm.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fit.md">Fit
   *      documentation</a>
   */
  public final static IBuiltInSymbol Fit = S.initFinalSymbol("Fit", ID.Fit);

  /**
   * FittedModel( ) - `FittedModel`holds the model generated with `LinearModelFit`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FittedModel.md">FittedModel
   *      documentation</a>
   */
  public final static IBuiltInSymbol FittedModel = S.initFinalSymbol("FittedModel", ID.FittedModel);

  /**
   * FiveNum({dataset}) - the Tuckey five-number summary is a set of descriptive statistics that
   * provide information about a `dataset`. It consists of the five most important sample
   * percentiles:
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FiveNum.md">FiveNum
   *      documentation</a>
   */
  public final static IBuiltInSymbol FiveNum = S.initFinalSymbol("FiveNum", ID.FiveNum);

  /**
   * FixedPoint(f, expr) - starting with `expr`, iteratively applies `f` until the result no longer
   * changes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FixedPoint.md">FixedPoint
   *      documentation</a>
   */
  public final static IBuiltInSymbol FixedPoint = S.initFinalSymbol("FixedPoint", ID.FixedPoint);

  /**
   * FixedPointList(f, expr) - starting with `expr`, iteratively applies `f` until the result no
   * longer changes, and returns a list of all intermediate results.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FixedPointList.md">FixedPointList
   *      documentation</a>
   */
  public final static IBuiltInSymbol FixedPointList =
      S.initFinalSymbol("FixedPointList", ID.FixedPointList);

  /**
   * Flat - is an attribute that specifies that nested occurrences of a function should be
   * automatically flattened.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Flat.md">Flat
   *      documentation</a>
   */
  public final static IBuiltInSymbol Flat = S.initFinalSymbol("Flat", ID.Flat);

  /**
   * Flatten(expr) - flattens out nested lists in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Flatten.md">Flatten
   *      documentation</a>
   */
  public final static IBuiltInSymbol Flatten = S.initFinalSymbol("Flatten", ID.Flatten);

  /**
   * FlattenAt(expr, position) - flattens out nested lists at the given `position` in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FlattenAt.md">FlattenAt
   *      documentation</a>
   */
  public final static IBuiltInSymbol FlattenAt = S.initFinalSymbol("FlattenAt", ID.FlattenAt);

  public final static IBuiltInSymbol FlatTopWindow =
      S.initFinalSymbol("FlatTopWindow", ID.FlatTopWindow);

  public final static IBuiltInSymbol Float = S.initFinalSymbol("Float", ID.Float);

  /**
   * Floor(expr) - gives the smallest integer less than or equal `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Floor.md">Floor
   *      documentation</a>
   */
  public final static IBuiltInSymbol Floor = S.initFinalSymbol("Floor", ID.Floor);

  /**
   * Fold[f, x, {a, b}] - returns `f[f[x, a], b]`, and this nesting continues for lists of arbitrary
   * length.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fold.md">Fold
   *      documentation</a>
   */
  public final static IBuiltInSymbol Fold = S.initFinalSymbol("Fold", ID.Fold);

  /**
   * FoldList[f, x, {a, b}] - returns `{x, f[x, a], f[f[x, a], b]}`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FoldList.md">FoldList
   *      documentation</a>
   */
  public final static IBuiltInSymbol FoldList = S.initFinalSymbol("FoldList", ID.FoldList);

  /**
   * For(start, test, incr, body) - evaluates `start`, and then iteratively `body` and `incr` as
   * long as test evaluates to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/For.md">For
   *      documentation</a>
   */
  public final static IBuiltInSymbol For = S.initFinalSymbol("For", ID.For);

  public final static IBuiltInSymbol ForAll = S.initFinalSymbol("ForAll", ID.ForAll);

  public final static IBuiltInSymbol FormBox = S.initFinalSymbol("FormBox", ID.FormBox);

  /**
   * Fourier(vector-of-complex-numbers) - Discrete Fourier transform of a
   * `vector-of-complex-numbers`. Fourier transform is restricted to vectors with length of power of
   * 2.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fourier.md">Fourier
   *      documentation</a>
   */
  public final static IBuiltInSymbol Fourier = S.initFinalSymbol("Fourier", ID.Fourier);

  public final static IBuiltInSymbol FourierCosTransform =
      S.initFinalSymbol("FourierCosTransform", ID.FourierCosTransform);

  public final static IBuiltInSymbol FourierDCT = S.initFinalSymbol("FourierDCT", ID.FourierDCT);

  /**
   * FourierDCTMatrix(n) - gives a discrete cosine transform matrix with the dimension `(n,n)` and
   * method `DCT-2`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FourierDCTMatrix.md">FourierDCTMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol FourierDCTMatrix =
      S.initFinalSymbol("FourierDCTMatrix", ID.FourierDCTMatrix);

  public final static IBuiltInSymbol FourierDST = S.initFinalSymbol("FourierDST", ID.FourierDST);

  /**
   * FourierDSTMatrix(n) - gives a discrete sine transform matrix with the dimension `(n,n)` and
   * method `DST-2`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FourierDSTMatrix.md">FourierDSTMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol FourierDSTMatrix =
      S.initFinalSymbol("FourierDSTMatrix", ID.FourierDSTMatrix);

  /**
   * FourierMatrix(n) - gives a fourier matrix with the dimension `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FourierMatrix.md">FourierMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol FourierMatrix =
      S.initFinalSymbol("FourierMatrix", ID.FourierMatrix);

  public final static IBuiltInSymbol FourierSinTransform =
      S.initFinalSymbol("FourierSinTransform", ID.FourierSinTransform);

  /**
   * FractionalPart(number) - get the fractional part of a `number`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FractionalPart.md">FractionalPart
   *      documentation</a>
   */
  public final static IBuiltInSymbol FractionalPart =
      S.initFinalSymbol("FractionalPart", ID.FractionalPart);

  public final static IBuiltInSymbol FractionBox = S.initFinalSymbol("FractionBox", ID.FractionBox);

  public final static IBuiltInSymbol FRatioDistribution =
      S.initFinalSymbol("FRatioDistribution", ID.FRatioDistribution);

  /**
   * FrechetDistribution(a,b) - returns a Frechet distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrechetDistribution.md">FrechetDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol FrechetDistribution =
      S.initFinalSymbol("FrechetDistribution", ID.FrechetDistribution);

  /**
   * FreeQ(`expr`, `x`) - returns 'True' if `expr` does not contain the expression `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FreeQ.md">FreeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol FreeQ = S.initFinalSymbol("FreeQ", ID.FreeQ);

  public final static IBuiltInSymbol FresnelC = S.initFinalSymbol("FresnelC", ID.FresnelC);

  public final static IBuiltInSymbol FresnelS = S.initFinalSymbol("FresnelS", ID.FresnelS);

  /**
   * FrobeniusNumber({a1, ... ,aN}) - returns the Frobenius number of the nonnegative integers `{a1,
   * ... ,aN}`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrobeniusNumber.md">FrobeniusNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol FrobeniusNumber =
      S.initFinalSymbol("FrobeniusNumber", ID.FrobeniusNumber);

  /**
   * FrobeniusSolve({a1, ... ,aN}, M) - get a list of solutions for the Frobenius equation given by
   * the list of integers `{a1, ... ,aN}` and the non-negative integer `M`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FrobeniusSolve.md">FrobeniusSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol FrobeniusSolve =
      S.initFinalSymbol("FrobeniusSolve", ID.FrobeniusSolve);

  /**
   * FromCharacterCode({ch1, ch2, ...}) - converts the `ch1, ch2,...` character codes into a string
   * of corresponding characters.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromCharacterCode.md">FromCharacterCode
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromCharacterCode =
      S.initFinalSymbol("FromCharacterCode", ID.FromCharacterCode);

  /**
   * FromContinuedFraction({n1, n2, ...}) - reconstructs a number from the list of its continued
   * fraction terms `{n1, n2, ...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromContinuedFraction.md">FromContinuedFraction
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromContinuedFraction =
      S.initFinalSymbol("FromContinuedFraction", ID.FromContinuedFraction);

  /**
   * FromDigits(list) - creates an expression from the list of digits for radix `10`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromDigits.md">FromDigits
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromDigits = S.initFinalSymbol("FromDigits", ID.FromDigits);

  /**
   * FromLetterNumber(number) - get the corresponding characters from the English alphabet.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromLetterNumber.md">FromLetterNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromLetterNumber =
      S.initFinalSymbol("FromLetterNumber", ID.FromLetterNumber);

  /**
   * FromPolarCoordinates({r, t}) - return the cartesian coordinates for the polar coordinates `{r,
   * t}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromPolarCoordinates.md">FromPolarCoordinates
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromPolarCoordinates =
      S.initFinalSymbol("FromPolarCoordinates", ID.FromPolarCoordinates);

  /**
   * FromRomanNumeral(roman-number-string) - converts the given `roman-number-string` to an integer
   * number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromRomanNumeral.md">FromRomanNumeral
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromRomanNumeral =
      S.initFinalSymbol("FromRomanNumeral", ID.FromRomanNumeral);

  /**
   * FromSphericalCoordinates({r, t, p}) - returns the cartesian coordinates for the spherical
   * coordinates `{r, t, p}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromSphericalCoordinates.md">FromSphericalCoordinates
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromSphericalCoordinates =
      S.initFinalSymbol("FromSphericalCoordinates", ID.FromSphericalCoordinates);

  public final static IBuiltInSymbol Full = S.initFinalSymbol("Full", ID.Full);


  public final static IBuiltInSymbol FullDefinition =
      S.initFinalSymbol("FullDefinition", ID.FullDefinition);

  /**
   * FullForm(expression) - shows the internal representation of the given `expression`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FullForm.md">FullForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol FullForm = S.initFinalSymbol("FullForm", ID.FullForm);

  /**
   * FullSimplify(expr) - works like `Simplify` but additionally tries some `FunctionExpand` rule
   * transformations to simplify `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FullSimplify.md">FullSimplify
   *      documentation</a>
   */
  public final static IBuiltInSymbol FullSimplify =
      S.initFinalSymbol("FullSimplify", ID.FullSimplify);

  /**
   * Function(body) - represents a pure function with parameters `#1`, `#2`....
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Function.md">Function
   *      documentation</a>
   */
  public final static IBuiltInSymbol Function = S.initFinalSymbol("Function", ID.Function);

  public final static IBuiltInSymbol FunctionDomain =
      S.initFinalSymbol("FunctionDomain", ID.FunctionDomain);

  /**
   * FunctionExpand(expression) - expands the special function `expression`. `FunctionExpand`
   * expands simple nested radicals.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FunctionExpand.md">FunctionExpand
   *      documentation</a>
   */
  public final static IBuiltInSymbol FunctionExpand =
      S.initFinalSymbol("FunctionExpand", ID.FunctionExpand);

  public final static IBuiltInSymbol FunctionPeriod =
      S.initFinalSymbol("FunctionPeriod", ID.FunctionPeriod);

  public final static IBuiltInSymbol FunctionRange =
      S.initFinalSymbol("FunctionRange", ID.FunctionRange);

  /**
   * FunctionURL(built-in-symbol) - returns the GitHub URL of the `built-in-symbol` implementation
   * in the [Symja GitHub repository](https://github.com/axkr/symja_android_library).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FunctionURL.md">FunctionURL
   *      documentation</a>
   */
  public final static IBuiltInSymbol FunctionURL = S.initFinalSymbol("FunctionURL", ID.FunctionURL);

  /**
   * Gamma(z) - is the gamma function on the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gamma.md">Gamma
   *      documentation</a>
   */
  public final static IBuiltInSymbol Gamma = S.initFinalSymbol("Gamma", ID.Gamma);

  /**
   * GammaDistribution(a,b) - returns a gamma distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GammaDistribution.md">GammaDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol GammaDistribution =
      S.initFinalSymbol("GammaDistribution", ID.GammaDistribution);

  public final static IBuiltInSymbol GammaRegularized =
      S.initFinalSymbol("GammaRegularized", ID.GammaRegularized);

  /**
   * Gather(list, test) - gathers leaves of `list` into sub lists of items that are the same
   * according to `test`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gather.md">Gather
   *      documentation</a>
   */
  public final static IBuiltInSymbol Gather = S.initFinalSymbol("Gather", ID.Gather);

  /**
   * GatherBy(list, f) - gathers leaves of `list` into sub lists of items whose image under `f`
   * identical.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GatherBy.md">GatherBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol GatherBy = S.initFinalSymbol("GatherBy", ID.GatherBy);

  public final static IBuiltInSymbol GaussianIntegers =
      S.initFinalSymbol("GaussianIntegers", ID.GaussianIntegers);

  public final static IBuiltInSymbol GaussianMatrix =
      S.initFinalSymbol("GaussianMatrix", ID.GaussianMatrix);

  public final static IBuiltInSymbol GaussianWindow =
      S.initFinalSymbol("GaussianWindow", ID.GaussianWindow);

  /**
   * GCD(n1, n2, ...) - computes the greatest common divisor of the given integers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GCD.md">GCD
   *      documentation</a>
   */
  public final static IBuiltInSymbol GCD = S.initFinalSymbol("GCD", ID.GCD);

  /**
   * GegenbauerC(n, a, x) - returns the GegenbauerC polynomial.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GegenbauerC.md">GegenbauerC
   *      documentation</a>
   */
  public final static IBuiltInSymbol GegenbauerC = S.initFinalSymbol("GegenbauerC", ID.GegenbauerC);

  public final static IBuiltInSymbol General = S.initFinalSymbol("General", ID.General);

  public final static IBuiltInSymbol GenerateConditions =
      S.initFinalSymbol("GenerateConditions", ID.GenerateConditions);

  public final static IBuiltInSymbol GeodesyData = S.initFinalSymbol("GeodesyData", ID.GeodesyData);

  /**
   * GeoDistance({latitude1,longitude1}, {latitude2,longitude2}) - returns the geodesic distance
   * between `{latitude1,longitude1}` and `{latitude2,longitude2}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoDistance.md">GeoDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol GeoDistance = S.initFinalSymbol("GeoDistance", ID.GeoDistance);

  /**
   * GeometricDistribution(p) - returns a geometric distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeometricDistribution.md">GeometricDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol GeometricDistribution =
      S.initFinalSymbol("GeometricDistribution", ID.GeometricDistribution);

  /**
   * GeometricMean({a, b, c,...}) - returns the geometric mean of `{a, b, c,...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeometricMean.md">GeometricMean
   *      documentation</a>
   */
  public final static IBuiltInSymbol GeometricMean =
      S.initFinalSymbol("GeometricMean", ID.GeometricMean);

  public final static IBuiltInSymbol GeometricTransformation =
      S.initFinalSymbol("GeometricTransformation", ID.GeometricTransformation);

  public final static IBuiltInSymbol GeoPosition = S.initFinalSymbol("GeoPosition", ID.GeoPosition);

  /**
   * Get("path-to-package-file-name") - load the package defined in `path-to-package-file-name`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Get.md">Get
   *      documentation</a>
   */
  public final static IBuiltInSymbol Get = S.initFinalSymbol("Get", ID.Get);

  /**
   * Glaisher - Glaisher constant.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Glaisher.md">Glaisher
   *      documentation</a>
   */
  public final static IBuiltInSymbol Glaisher = S.initFinalSymbol("Glaisher", ID.Glaisher);

  public final static IBuiltInSymbol GoldbachList =
      S.initFinalSymbol("GoldbachList", ID.GoldbachList);

  public final static IBuiltInSymbol GoldenAngle = S.initFinalSymbol("GoldenAngle", ID.GoldenAngle);

  /**
   * GoldenRatio - is the golden ratio `(1+Sqrt(5))/2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GoldenRatio.md">GoldenRatio
   *      documentation</a>
   */
  public final static IBuiltInSymbol GoldenRatio = S.initFinalSymbol("GoldenRatio", ID.GoldenRatio);

  public final static IBuiltInSymbol GompertzMakehamDistribution =
      S.initFinalSymbol("GompertzMakehamDistribution", ID.GompertzMakehamDistribution);

  /**
   * Grad(function, list-of-variables) - gives the gradient of the function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Grad.md">Grad
   *      documentation</a>
   */
  public final static IBuiltInSymbol Grad = S.initFinalSymbol("Grad", ID.Grad);

  /**
   * Graph({edge1,...,edgeN}) - create a graph from the given edges `edge1,...,edgeN`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Graph.md">Graph
   *      documentation</a>
   */
  public final static IBuiltInSymbol Graph = S.initFinalSymbol("Graph", ID.Graph);

  /**
   * GraphCenter(graph) - compute the `graph` center. The center of a `graph` is the set of vertices
   * of graph eccentricity equal to the `graph` radius.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphCenter.md">GraphCenter
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphCenter = S.initFinalSymbol("GraphCenter", ID.GraphCenter);

  /**
   * GraphComplement(graph) - returns the graph complement of `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphComplement.md">GraphComplement
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphComplement =
      S.initFinalSymbol("GraphComplement", ID.GraphComplement);

  public final static IBuiltInSymbol GraphData = S.initFinalSymbol("GraphData", ID.GraphData);

  /**
   * GraphDiameter(graph) - return the diameter of the `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphDiameter.md">GraphDiameter
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphDiameter =
      S.initFinalSymbol("GraphDiameter", ID.GraphDiameter);

  /**
   * GraphDifference(graph1, graph2) - returns the graph difference of `graph1`, `graph2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphDifference.md">GraphDifference
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphDifference =
      S.initFinalSymbol("GraphDifference", ID.GraphDifference);

  /**
   * GraphDisjointUnion(graph1, graph2, graph3,...) - returns the disjoint graph union of `graph1`,
   * `graph2`, `graph3`,...
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphDisjointUnion.md">GraphDisjointUnion
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphDisjointUnion =
      S.initFinalSymbol("GraphDisjointUnion", ID.GraphDisjointUnion);

  public final static IBuiltInSymbol Graphics = S.initFinalSymbol("Graphics", ID.Graphics);

  /**
   * Graphics3D(primitives, options) - represents a three-dimensional graphic.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Graphics3D.md">Graphics3D
   *      documentation</a>
   */
  public final static IBuiltInSymbol Graphics3D = S.initFinalSymbol("Graphics3D", ID.Graphics3D);

  public final static IBuiltInSymbol Graphics3DJSON =
      S.initFinalSymbol("Graphics3DJSON", ID.Graphics3DJSON);

  public final static IBuiltInSymbol GraphicsComplex =
      S.initFinalSymbol("GraphicsComplex", ID.GraphicsComplex);

  public final static IBuiltInSymbol GraphicsGroup =
      S.initFinalSymbol("GraphicsGroup", ID.GraphicsGroup);

  public final static IBuiltInSymbol GraphicsJSON =
      S.initFinalSymbol("GraphicsJSON", ID.GraphicsJSON);

  /**
   * GraphIntersection(graph1, graph2, graph3,...) - returns the graph intersection of `graph1`,
   * `graph2`, `graph3`,...
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphIntersection.md">GraphIntersection
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphIntersection =
      S.initFinalSymbol("GraphIntersection", ID.GraphIntersection);

  /**
   * GraphPeriphery(graph) - compute the `graph` periphery. The periphery of a `graph` is the set of
   * vertices of graph eccentricity equal to the graph diameter.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphPeriphery.md">GraphPeriphery
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphPeriphery =
      S.initFinalSymbol("GraphPeriphery", ID.GraphPeriphery);

  public final static IBuiltInSymbol GraphPower = S.initFinalSymbol("GraphPower", ID.GraphPower);

  /**
   * GraphQ(expr) - test if `expr` is a graph object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphQ.md">GraphQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphQ = S.initFinalSymbol("GraphQ", ID.GraphQ);

  /**
   * GraphRadius(graph) - return the radius of the `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphRadius.md">GraphRadius
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphRadius = S.initFinalSymbol("GraphRadius", ID.GraphRadius);

  /**
   * GraphUnion(graph1, graph2, graph3,...) - returns the graph union of `graph1`, `graph2`,
   * `graph3`,...
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphUnion.md">GraphUnion
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphUnion = S.initFinalSymbol("GraphUnion", ID.GraphUnion);

  public final static IBuiltInSymbol Gray = S.initFinalSymbol("Gray", ID.Gray);

  public final static IBuiltInSymbol GrayLevel = S.initFinalSymbol("GrayLevel", ID.GrayLevel);

  /**
   * Greater(x, y) - yields `True` if `x` is known to be greater than `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Greater.md">Greater
   *      documentation</a>
   */
  public final static IBuiltInSymbol Greater = S.initFinalSymbol("Greater", ID.Greater);

  /**
   * GreaterEqual(x, y) - yields `True` if `x` is known to be greater than or equal to `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterEqual.md">GreaterEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol GreaterEqual =
      S.initFinalSymbol("GreaterEqual", ID.GreaterEqual);

  public final static IBuiltInSymbol GreaterEqualThan =
      S.initFinalSymbol("GreaterEqualThan", ID.GreaterEqualThan);

  public final static IBuiltInSymbol GreaterThan = S.initFinalSymbol("GreaterThan", ID.GreaterThan);

  public final static IBuiltInSymbol Green = S.initFinalSymbol("Green", ID.Green);

  public final static IBuiltInSymbol GridGraph = S.initFinalSymbol("GridGraph", ID.GridGraph);

  /**
   * GroebnerBasis({polynomial-list},{variable-list}) - returns a Gröbner basis for the
   * `polynomial-list` and `variable-list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GroebnerBasis.md">GroebnerBasis
   *      documentation</a>
   */
  public final static IBuiltInSymbol GroebnerBasis =
      S.initFinalSymbol("GroebnerBasis", ID.GroebnerBasis);

  /**
   * GroupBy(list, head) - return an association where the elements of `list` are grouped by
   * `head(element)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GroupBy.md">GroupBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol GroupBy = S.initFinalSymbol("GroupBy", ID.GroupBy);

  /**
   * Gudermannian(expr) - computes the gudermannian function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gudermannian.md">Gudermannian
   *      documentation</a>
   */
  public final static IBuiltInSymbol Gudermannian =
      S.initFinalSymbol("Gudermannian", ID.Gudermannian);

  /**
   * GumbelDistribution(a, b) - returns a Gumbel distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GumbelDistribution.md">GumbelDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol GumbelDistribution =
      S.initFinalSymbol("GumbelDistribution", ID.GumbelDistribution);

  /**
   * HamiltonianGraphQ(graph) - returns `True` if `graph` is an hamiltonian graph, and `False`
   * otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HamiltonianGraphQ.md">HamiltonianGraphQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol HamiltonianGraphQ =
      S.initFinalSymbol("HamiltonianGraphQ", ID.HamiltonianGraphQ);

  /**
   * HammingDistance(a, b) - returns the Hamming distance of `a` and `b`, i.e. the number of
   * different elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HammingDistance.md">HammingDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol HammingDistance =
      S.initFinalSymbol("HammingDistance", ID.HammingDistance);

  public final static IBuiltInSymbol HammingWindow =
      S.initFinalSymbol("HammingWindow", ID.HammingWindow);

  public final static IBuiltInSymbol HankelH1 = S.initFinalSymbol("HankelH1", ID.HankelH1);

  public final static IBuiltInSymbol HankelH2 = S.initFinalSymbol("HankelH2", ID.HankelH2);

  public final static IBuiltInSymbol HankelMatrix =
      S.initFinalSymbol("HankelMatrix", ID.HankelMatrix);

  public final static IBuiltInSymbol HannWindow = S.initFinalSymbol("HannWindow", ID.HannWindow);

  /**
   * HarmonicMean({a, b, c,...}) - returns the harmonic mean of `{a, b, c,...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HarmonicMean.md">HarmonicMean
   *      documentation</a>
   */
  public final static IBuiltInSymbol HarmonicMean =
      S.initFinalSymbol("HarmonicMean", ID.HarmonicMean);

  /**
   * HarmonicNumber(n) - returns the `n`th harmonic number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HarmonicNumber.md">HarmonicNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol HarmonicNumber =
      S.initFinalSymbol("HarmonicNumber", ID.HarmonicNumber);

  /**
   * Haversine(z) - returns the haversine function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Haversine.md">Haversine
   *      documentation</a>
   */
  public final static IBuiltInSymbol Haversine = S.initFinalSymbol("Haversine", ID.Haversine);

  /**
   * Head(expr) - returns the head of the expression or atom `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Head.md">Head
   *      documentation</a>
   */
  public final static IBuiltInSymbol Head = S.initFinalSymbol("Head", ID.Head);

  public final static IBuiltInSymbol Heads = S.initFinalSymbol("Heads", ID.Heads);

  public final static IBuiltInSymbol HeavisideLambda =
      S.initFinalSymbol("HeavisideLambda", ID.HeavisideLambda);

  public final static IBuiltInSymbol HeavisidePi = S.initFinalSymbol("HeavisidePi", ID.HeavisidePi);

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
      S.initFinalSymbol("HeavisideTheta", ID.HeavisideTheta);

  /**
   * HermiteH(n, x) - returns the Hermite polynomial `H_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HermiteH.md">HermiteH
   *      documentation</a>
   */
  public final static IBuiltInSymbol HermiteH = S.initFinalSymbol("HermiteH", ID.HermiteH);

  /**
   * HermitianMatrixQ(m) - returns `True` if `m` is a hermitian matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HermitianMatrixQ.md">HermitianMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol HermitianMatrixQ =
      S.initFinalSymbol("HermitianMatrixQ", ID.HermitianMatrixQ);

  /**
   * HessenbergDecomposition(matrix) - calculate the Hessenberg-decomposition as a list `{p, h}` of
   * a square `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HessenbergDecomposition.md">HessenbergDecomposition
   *      documentation</a>
   */
  public final static IBuiltInSymbol HessenbergDecomposition =
      S.initFinalSymbol("HessenbergDecomposition", ID.HessenbergDecomposition);

  public final static IBuiltInSymbol HessianMatrix =
      S.initFinalSymbol("HessianMatrix", ID.HessianMatrix);

  /**
   * HexidecimalCharacter - represents the characters `0-9`, `a-f` and `A-F`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HexidecimalCharacter.md">HexidecimalCharacter
   *      documentation</a>
   */
  public final static IBuiltInSymbol HexidecimalCharacter =
      S.initFinalSymbol("HexidecimalCharacter", ID.HexidecimalCharacter);

  /**
   * HilbertMatrix(n) - gives the hilbert matrix with `n` rows and columns.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HilbertMatrix.md">HilbertMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol HilbertMatrix =
      S.initFinalSymbol("HilbertMatrix", ID.HilbertMatrix);

  /**
   * Histogram(list-of-values) - plots a histogram for a `list-of-values`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Histogram.md">Histogram
   *      documentation</a>
   */
  public final static IBuiltInSymbol Histogram = S.initFinalSymbol("Histogram", ID.Histogram);

  public final static IBuiltInSymbol HodgeDual = S.initFinalSymbol("HodgeDual", ID.HodgeDual);

  /**
   * Hold(expr) - `Hold` doesn't evaluate `expr`. `Hold` evaluates `UpValues`for its arguments.
   * `HoldComplete` doesn't evaluate `UpValues`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hold.md">Hold
   *      documentation</a>
   */
  public final static IBuiltInSymbol Hold = S.initFinalSymbol("Hold", ID.Hold);

  /**
   * HoldAll - is an attribute specifying that all arguments of a function should be left
   * unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldAll.md">HoldAll
   *      documentation</a>
   */
  public final static IBuiltInSymbol HoldAll = S.initFinalSymbol("HoldAll", ID.HoldAll);

  public final static IBuiltInSymbol HoldAllComplete =
      S.initFinalSymbol("HoldAllComplete", ID.HoldAllComplete);

  /**
   * HoldComplete(expr) - `HoldComplete` doesn't evaluate `expr`. `Hold` evaluates `UpValues`for its
   * arguments. `HoldComplete` doesn't evaluate `UpValues`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldComplete.md">HoldComplete
   *      documentation</a>
   */
  public final static IBuiltInSymbol HoldComplete =
      S.initFinalSymbol("HoldComplete", ID.HoldComplete);

  /**
   * HoldFirst - is an attribute specifying that the first argument of a function should be left
   * unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldFirst.md">HoldFirst
   *      documentation</a>
   */
  public final static IBuiltInSymbol HoldFirst = S.initFinalSymbol("HoldFirst", ID.HoldFirst);

  /**
   * HoldForm(expr) - `HoldForm` doesn't evaluate `expr` and didn't appear in the output.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldForm.md">HoldForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol HoldForm = S.initFinalSymbol("HoldForm", ID.HoldForm);

  /**
   * HoldPattern(expr) - `HoldPattern` doesn't evaluate `expr` for pattern-matching.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldPattern.md">HoldPattern
   *      documentation</a>
   */
  public final static IBuiltInSymbol HoldPattern = S.initFinalSymbol("HoldPattern", ID.HoldPattern);

  /**
   * HoldRest - is an attribute specifying that all but the first argument of a function should be
   * left unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldRest.md">HoldRest
   *      documentation</a>
   */
  public final static IBuiltInSymbol HoldRest = S.initFinalSymbol("HoldRest", ID.HoldRest);

  public final static IBuiltInSymbol Horner = S.initFinalSymbol("Horner", ID.Horner);

  /**
   * HornerForm(polynomial) - Generate the horner scheme for a univariate `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HornerForm.md">HornerForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol HornerForm = S.initFinalSymbol("HornerForm", ID.HornerForm);

  public final static IBuiltInSymbol Hue = S.initFinalSymbol("Hue", ID.Hue);

  /**
   * HurwitzLerchPhi(z, s, a) - returns the Lerch transcendent function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HurwitzLerchPhi.md">HurwitzLerchPhi
   *      documentation</a>
   */
  public final static IBuiltInSymbol HurwitzLerchPhi =
      S.initFinalSymbol("HurwitzLerchPhi", ID.HurwitzLerchPhi);

  /**
   * HurwitzZeta(s, a) - returns the Hurwitz zeta function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HurwitzZeta.md">HurwitzZeta
   *      documentation</a>
   */
  public final static IBuiltInSymbol HurwitzZeta = S.initFinalSymbol("HurwitzZeta", ID.HurwitzZeta);

  public final static IBuiltInSymbol HypercubeGraph =
      S.initFinalSymbol("HypercubeGraph", ID.HypercubeGraph);

  public final static IBuiltInSymbol Hyperfactorial =
      S.initFinalSymbol("Hyperfactorial", ID.Hyperfactorial);

  /**
   * Hypergeometric0F1(b, z) - return the `Hypergeometric0F1` function
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hypergeometric0F1.md">Hypergeometric0F1
   *      documentation</a>
   */
  public final static IBuiltInSymbol Hypergeometric0F1 =
      S.initFinalSymbol("Hypergeometric0F1", ID.Hypergeometric0F1);

  public final static IBuiltInSymbol Hypergeometric0F1Regularized =
      S.initFinalSymbol("Hypergeometric0F1Regularized", ID.Hypergeometric0F1Regularized);

  /**
   * Hypergeometric1F1(a, b, z) - return the `Hypergeometric1F1` function
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hypergeometric1F1.md">Hypergeometric1F1
   *      documentation</a>
   */
  public final static IBuiltInSymbol Hypergeometric1F1 =
      S.initFinalSymbol("Hypergeometric1F1", ID.Hypergeometric1F1);

  public final static IBuiltInSymbol Hypergeometric1F1Regularized =
      S.initFinalSymbol("Hypergeometric1F1Regularized", ID.Hypergeometric1F1Regularized);

  /**
   * Hypergeometric2F1(a, b, c, z) - return the `Hypergeometric2F1` function
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hypergeometric2F1.md">Hypergeometric2F1
   *      documentation</a>
   */
  public final static IBuiltInSymbol Hypergeometric2F1 =
      S.initFinalSymbol("Hypergeometric2F1", ID.Hypergeometric2F1);

  public final static IBuiltInSymbol Hypergeometric2F1Regularized =
      S.initFinalSymbol("Hypergeometric2F1Regularized", ID.Hypergeometric2F1Regularized);

  /**
   * HypergeometricDistribution(n, s, t) - returns a hypergeometric distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HypergeometricDistribution.md">HypergeometricDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol HypergeometricDistribution =
      S.initFinalSymbol("HypergeometricDistribution", ID.HypergeometricDistribution);

  /**
   * HypergeometricPFQ({a,...}, {b,...}, c) - return the `HypergeometricPFQ` function
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HypergeometricPFQ.md">HypergeometricPFQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol HypergeometricPFQ =
      S.initFinalSymbol("HypergeometricPFQ", ID.HypergeometricPFQ);

  public final static IBuiltInSymbol HypergeometricPFQRegularized =
      S.initFinalSymbol("HypergeometricPFQRegularized", ID.HypergeometricPFQRegularized);

  public final static IBuiltInSymbol HypergeometricU =
      S.initFinalSymbol("HypergeometricU", ID.HypergeometricU);

  /**
   * I - Imaginary unit - internally converted to the complex number `0+1*i`. `I` represents the
   * imaginary number `Sqrt(-1)`. `I^2` will be evaluated to `-1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/I.md">I
   *      documentation</a>
   */
  public final static IBuiltInSymbol I = S.initFinalSymbol("I", ID.I);

  public final static IBuiltInSymbol Icosahedron = S.initFinalSymbol("Icosahedron", ID.Icosahedron);

  /**
   * Identity(x) - is the identity function, which returns `x` unchanged.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Identity.md">Identity
   *      documentation</a>
   */
  public final static IBuiltInSymbol Identity = S.initFinalSymbol("Identity", ID.Identity);

  /**
   * IdentityMatrix(n) - gives the identity matrix with `n` rows and columns.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IdentityMatrix.md">IdentityMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol IdentityMatrix =
      S.initFinalSymbol("IdentityMatrix", ID.IdentityMatrix);

  /**
   * If(cond, pos, neg) - returns `pos` if `cond` evaluates to `True`, and `neg` if it evaluates to
   * `False`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/If.md">If
   *      documentation</a>
   */
  public final static IBuiltInSymbol If = S.initFinalSymbol("If", ID.If);

  public final static IBuiltInSymbol IgnoreCase = S.initFinalSymbol("IgnoreCase", ID.IgnoreCase);

  /**
   * Im(z) - returns the imaginary component of the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Im.md">Im
   *      documentation</a>
   */
  public final static IBuiltInSymbol Im = S.initFinalSymbol("Im", ID.Im);

  public final static IBuiltInSymbol Image = S.initFinalSymbol("Image", ID.Image);

  public final static IBuiltInSymbol ImageChannels =
      S.initFinalSymbol("ImageChannels", ID.ImageChannels);

  public final static IBuiltInSymbol ImageColorSpace =
      S.initFinalSymbol("ImageColorSpace", ID.ImageColorSpace);

  public final static IBuiltInSymbol ImageCrop = S.initFinalSymbol("ImageCrop", ID.ImageCrop);

  public final static IBuiltInSymbol ImageData = S.initFinalSymbol("ImageData", ID.ImageData);

  public final static IBuiltInSymbol ImageDimensions =
      S.initFinalSymbol("ImageDimensions", ID.ImageDimensions);

  public final static IBuiltInSymbol ImageQ = S.initFinalSymbol("ImageQ", ID.ImageQ);

  public final static IBuiltInSymbol ImageResize = S.initFinalSymbol("ImageResize", ID.ImageResize);

  public final static IBuiltInSymbol ImageRotate = S.initFinalSymbol("ImageRotate", ID.ImageRotate);

  public final static IBuiltInSymbol ImageScaled = S.initFinalSymbol("ImageScaled", ID.ImageScaled);

  public final static IBuiltInSymbol ImageSize = S.initFinalSymbol("ImageSize", ID.ImageSize);

  public final static IBuiltInSymbol ImageType = S.initFinalSymbol("ImageType", ID.ImageType);

  public final static IBuiltInSymbol ImplicitD = S.initFinalSymbol("ImplicitD", ID.ImplicitD);

  /**
   * Implies(arg1, arg2) - Logical implication.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Implies.md">Implies
   *      documentation</a>
   */
  public final static IBuiltInSymbol Implies = S.initFinalSymbol("Implies", ID.Implies);

  /**
   * Import("path-to-filename", "WXF") - if the file system is enabled, import an expression in WXF
   * format from the "path-to-filename" file.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Import.md">Import
   *      documentation</a>
   */
  public final static IBuiltInSymbol Import = S.initFinalSymbol("Import", ID.Import);

  public final static IBuiltInSymbol ImportString =
      S.initFinalSymbol("ImportString", ID.ImportString);

  /**
   * In(k) - gives the `k`th line of input.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/In.md">In
   *      documentation</a>
   */
  public final static IBuiltInSymbol In = S.initFinalSymbol("In", ID.In);

  /**
   * Increment(x) - increments `x` by `1`, returning the original value of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Increment.md">Increment
   *      documentation</a>
   */
  public final static IBuiltInSymbol Increment = S.initFinalSymbol("Increment", ID.Increment);

  /**
   * Indeterminate - represents an indeterminate result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Indeterminate.md">Indeterminate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Indeterminate =
      S.initFinalSymbol("Indeterminate", ID.Indeterminate);

  public final static IBuiltInSymbol Indexed = S.initFinalSymbol("Indexed", ID.Indexed);

  public final static IBuiltInSymbol IndexGraph = S.initFinalSymbol("IndexGraph", ID.IndexGraph);

  public final static IBuiltInSymbol Inequality = S.initFinalSymbol("Inequality", ID.Inequality);

  /**
   * InexactNumberQ(expr) - returns `True` if `expr` is not an exact number, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InexactNumberQ.md">InexactNumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol InexactNumberQ =
      S.initFinalSymbol("InexactNumberQ", ID.InexactNumberQ);

  /**
   * Infinity - represents an infinite real quantity.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Infinity.md">Infinity
   *      documentation</a>
   */
  public final static IBuiltInSymbol Infinity = S.initFinalSymbol("Infinity", ID.Infinity);

  public final static IBuiltInSymbol Infix = S.initFinalSymbol("Infix", ID.Infix);

  public final static IBuiltInSymbol Information = S.initFinalSymbol("Information", ID.Information);

  public final static IBuiltInSymbol Inherited = S.initFinalSymbol("Inherited", ID.Inherited);

  /**
   * Inner(f, x, y, g) - computes a generalized inner product of `x` and `y`, using a multiplication
   * function `f` and an addition function `g`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Inner.md">Inner
   *      documentation</a>
   */
  public final static IBuiltInSymbol Inner = S.initFinalSymbol("Inner", ID.Inner);

  /**
   * Input() - if the file system is enabled, the user can input an expression. After input this
   * expression will be evaluated immediately.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Input.md">Input
   *      documentation</a>
   */
  public final static IBuiltInSymbol Input = S.initFinalSymbol("Input", ID.Input);

  public final static IBuiltInSymbol InputField = S.initFinalSymbol("InputField", ID.InputField);

  /**
   * InputForm(expr) - print the `expr` as if it should be inserted by the user for evaluation.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InputForm.md">InputForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol InputForm = S.initFinalSymbol("InputForm", ID.InputForm);

  public final static IBuiltInSymbol InputStream = S.initFinalSymbol("InputStream", ID.InputStream);

  /**
   * InputString() - if the file system is enabled, the user can input a string.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InputString.md">InputString
   *      documentation</a>
   */
  public final static IBuiltInSymbol InputString = S.initFinalSymbol("InputString", ID.InputString);

  /**
   * Insert(list, elem, n) - inserts `elem` at position `n` in `list`. When `n` is negative, the
   * position is counted from the end.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Insert.md">Insert
   *      documentation</a>
   */
  public final static IBuiltInSymbol Insert = S.initFinalSymbol("Insert", ID.Insert);

  public final static IBuiltInSymbol InsertionFunction =
      S.initFinalSymbol("InsertionFunction", ID.InsertionFunction);

  public final static IBuiltInSymbol InstallJava = S.initFinalSymbol("InstallJava", ID.InstallJava);

  /**
   * InstanceOf[java-object, "class-name"] - return the result of the Java expression `java-object
   * instanceof class`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InstanceOf.md">InstanceOf
   *      documentation</a>
   */
  public final static IBuiltInSymbol InstanceOf = S.initFinalSymbol("InstanceOf", ID.InstanceOf);

  /**
   * Integer - is the head of integers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Integer.md">Integer
   *      documentation</a>
   */
  public final static IBuiltInSymbol Integer = S.initFinalSymbol("Integer", ID.Integer);

  /**
   * IntegerDigits(n, base) - returns a list of integer digits for `n` under `base`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerDigits.md">IntegerDigits
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntegerDigits =
      S.initFinalSymbol("IntegerDigits", ID.IntegerDigits);

  /**
   * IntegerExponent(n, b) - gives the highest exponent of `b` that divides `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerExponent.md">IntegerExponent
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntegerExponent =
      S.initFinalSymbol("IntegerExponent", ID.IntegerExponent);

  /**
   * IntegerLength(x) - gives the number of digits in the base-10 representation of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerLength.md">IntegerLength
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntegerLength =
      S.initFinalSymbol("IntegerLength", ID.IntegerLength);

  /**
   * IntegerName(integer-number) - gives the spoken number string of `integer-number` in language
   * `English`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerName.md">IntegerName
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntegerName = S.initFinalSymbol("IntegerName", ID.IntegerName);

  /**
   * IntegerPart(expr) - for real `expr` return the integer part of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerPart.md">IntegerPart
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntegerPart = S.initFinalSymbol("IntegerPart", ID.IntegerPart);

  /**
   * IntegerPartitions(n) - returns all partitions of the integer `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerPartitions.md">IntegerPartitions
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntegerPartitions =
      S.initFinalSymbol("IntegerPartitions", ID.IntegerPartitions);

  /**
   * IntegerQ(expr) - returns `True` if `expr` is an integer, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntegerQ.md">IntegerQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntegerQ = S.initFinalSymbol("IntegerQ", ID.IntegerQ);

  /**
   * Integers - is the set of integer numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Integers.md">Integers
   *      documentation</a>
   */
  public final static IBuiltInSymbol Integers = S.initFinalSymbol("Integers", ID.Integers);

  /**
   * Integrate(f, x) - integrates `f` with respect to `x`. The result does not contain the additive
   * integration constant.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Integrate.md">Integrate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Integrate = S.initFinalSymbol("Integrate", ID.Integrate);

  /**
   * InterpolatingFunction(data-list) - get the representation for the given `data-list` as
   * piecewise `InterpolatingPolynomial`s.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterpolatingFunction.md">InterpolatingFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol InterpolatingFunction =
      S.initFinalSymbol("InterpolatingFunction", ID.InterpolatingFunction);

  /**
   * InterpolatingPolynomial(data-list, symbol) - get the polynomial representation for the given
   * `data-list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterpolatingPolynomial.md">InterpolatingPolynomial
   *      documentation</a>
   */
  public final static IBuiltInSymbol InterpolatingPolynomial =
      S.initFinalSymbol("InterpolatingPolynomial", ID.InterpolatingPolynomial);

  public final static IBuiltInSymbol Interpolation =
      S.initFinalSymbol("Interpolation", ID.Interpolation);

  public final static IBuiltInSymbol InterpolationOrder =
      S.initFinalSymbol("InterpolationOrder", ID.InterpolationOrder);

  /**
   * InterquartileRange(list) - returns the interquartile range (IQR), which is between upper and
   * lower quartiles, IQR = Q3 − Q1.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InterquartileRange.md">InterquartileRange
   *      documentation</a>
   */
  public final static IBuiltInSymbol InterquartileRange =
      S.initFinalSymbol("InterquartileRange", ID.InterquartileRange);

  /**
   * Interrupt( ) - Interrupt an evaluation and returns `$Aborted`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Interrupt.md">Interrupt
   *      documentation</a>
   */
  public final static IBuiltInSymbol Interrupt = S.initFinalSymbol("Interrupt", ID.Interrupt);

  public final static IBuiltInSymbol IntersectingQ =
      S.initFinalSymbol("IntersectingQ", ID.IntersectingQ);

  /**
   * Intersection(set1, set2, ...) - get the intersection set from `set1` and `set2` ....
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Intersection.md">Intersection
   *      documentation</a>
   */
  public final static IBuiltInSymbol Intersection =
      S.initFinalSymbol("Intersection", ID.Intersection);

  /**
   * Interval({a, b}) - represents the closed interval from `a` to `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Interval.md">Interval
   *      documentation</a>
   */
  public final static IBuiltInSymbol Interval = S.initFinalSymbol("Interval", ID.Interval);

  /**
   * IntervalComplement(interval_1, interval_2) - compute the complement of the intervals
   * `interval_1 \ interval_2`. The intervals must be of structure `IntervalData` (closed/opened
   * ends of interval) and not of structure `Interval` (only closed ends)
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalComplement.md">IntervalComplement
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntervalComplement =
      S.initFinalSymbol("IntervalComplement", ID.IntervalComplement);

  /**
   * IntervalData({a, leftEnd, rightEnd, b}) - represents the open/closed ends interval from `a` to
   * `b`. `leftEnd` and `rightEnd` must have the value `Less` for representing an open ended
   * interval or `LessEqual` for representing a closed ended interval.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalData.md">IntervalData
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntervalData =
      S.initFinalSymbol("IntervalData", ID.IntervalData);

  /**
   * IntervalIntersection(interval_1, interval_2, ...) - compute the intersection of the intervals
   * `interval_1, interval_2, ...`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalIntersection.md">IntervalIntersection
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntervalIntersection =
      S.initFinalSymbol("IntervalIntersection", ID.IntervalIntersection);

  /**
   * IntervalMemberQ(interval, intervalOrRealNumber) - returns `True`, if `intervalOrRealNumber` is
   * completly sourrounded by `interval`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalMemberQ.md">IntervalMemberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntervalMemberQ =
      S.initFinalSymbol("IntervalMemberQ", ID.IntervalMemberQ);

  /**
   * IntervalUnion(interval_1, interval_2, ...) - compute the union of the intervals `interval_1,
   * interval_2, ...`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalUnion.md">IntervalUnion
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntervalUnion =
      S.initFinalSymbol("IntervalUnion", ID.IntervalUnion);

  /**
   * Inverse(matrix) - computes the inverse of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Inverse.md">Inverse
   *      documentation</a>
   */
  public final static IBuiltInSymbol Inverse = S.initFinalSymbol("Inverse", ID.Inverse);

  public final static IBuiltInSymbol InverseBetaRegularized =
      S.initFinalSymbol("InverseBetaRegularized", ID.InverseBetaRegularized);

  /**
   * InverseCDF(dist, q) - returns the inverse cumulative distribution for the distribution `dist`
   * as a function of `q`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseCDF.md">InverseCDF
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseCDF = S.initFinalSymbol("InverseCDF", ID.InverseCDF);

  /**
   * InverseErf(z) - returns the inverse error function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseErf.md">InverseErf
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseErf = S.initFinalSymbol("InverseErf", ID.InverseErf);

  /**
   * InverseErfc(z) - returns the inverse complementary error function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseErfc.md">InverseErfc
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseErfc = S.initFinalSymbol("InverseErfc", ID.InverseErfc);

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
      S.initFinalSymbol("InverseFourier", ID.InverseFourier);

  /**
   * InverseFunction(head) - returns the inverse function for the symbol `head`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseFunction.md">InverseFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseFunction =
      S.initFinalSymbol("InverseFunction", ID.InverseFunction);

  public final static IBuiltInSymbol InverseGammaRegularized =
      S.initFinalSymbol("InverseGammaRegularized", ID.InverseGammaRegularized);

  /**
   * InverseGudermannian(expr) - computes the inverse gudermannian function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseGudermannian.md">InverseGudermannian
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseGudermannian =
      S.initFinalSymbol("InverseGudermannian", ID.InverseGudermannian);

  /**
   * InverseHaversine(z) - returns the inverse haversine function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseHaversine.md">InverseHaversine
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseHaversine =
      S.initFinalSymbol("InverseHaversine", ID.InverseHaversine);

  public final static IBuiltInSymbol InverseJacobiCD =
      S.initFinalSymbol("InverseJacobiCD", ID.InverseJacobiCD);

  public final static IBuiltInSymbol InverseJacobiCN =
      S.initFinalSymbol("InverseJacobiCN", ID.InverseJacobiCN);

  public final static IBuiltInSymbol InverseJacobiDC =
      S.initFinalSymbol("InverseJacobiDC", ID.InverseJacobiDC);

  public final static IBuiltInSymbol InverseJacobiDN =
      S.initFinalSymbol("InverseJacobiDN", ID.InverseJacobiDN);

  public final static IBuiltInSymbol InverseJacobiNC =
      S.initFinalSymbol("InverseJacobiNC", ID.InverseJacobiNC);

  public final static IBuiltInSymbol InverseJacobiND =
      S.initFinalSymbol("InverseJacobiND", ID.InverseJacobiND);

  public final static IBuiltInSymbol InverseJacobiSC =
      S.initFinalSymbol("InverseJacobiSC", ID.InverseJacobiSC);

  public final static IBuiltInSymbol InverseJacobiSD =
      S.initFinalSymbol("InverseJacobiSD", ID.InverseJacobiSD);

  public final static IBuiltInSymbol InverseJacobiSN =
      S.initFinalSymbol("InverseJacobiSN", ID.InverseJacobiSN);

  /**
   * InverseLaplaceTransform(f,s,t) - returns the inverse laplace transform.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseLaplaceTransform.md">InverseLaplaceTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseLaplaceTransform =
      S.initFinalSymbol("InverseLaplaceTransform", ID.InverseLaplaceTransform);

  /**
   * InverseSeries( series ) - return the inverse series.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseSeries.md">InverseSeries
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseSeries =
      S.initFinalSymbol("InverseSeries", ID.InverseSeries);

  public final static IBuiltInSymbol InverseWeierstrassP =
      S.initFinalSymbol("InverseWeierstrassP", ID.InverseWeierstrassP);

  /**
   * InverseZTransform(x,z,n) - returns the inverse Z-Transform of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseZTransform.md">InverseZTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseZTransform =
      S.initFinalSymbol("InverseZTransform", ID.InverseZTransform);

  /**
   * IsomorphicGraphQ(graph1, graph2) - returns `True` if an isomorphism exists between `graph1` and
   * `graph2`. Return `False`in all other cases.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IsomorphicGraphQ.md">IsomorphicGraphQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol IsomorphicGraphQ =
      S.initFinalSymbol("IsomorphicGraphQ", ID.IsomorphicGraphQ);

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
      S.initFinalSymbol("JaccardDissimilarity", ID.JaccardDissimilarity);

  /**
   * JacobiAmplitude(x, m) - returns the amplitude `am(x, m)` for Jacobian elliptic function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiAmplitude.md">JacobiAmplitude
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiAmplitude =
      S.initFinalSymbol("JacobiAmplitude", ID.JacobiAmplitude);

  /**
   * JacobiCD(x, m) - returns the Jacobian elliptic function `cd(x, m)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiCD.md">JacobiCD
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiCD = S.initFinalSymbol("JacobiCD", ID.JacobiCD);

  /**
   * JacobiCN(x, m) - returns the Jacobian elliptic function `cn(x, m)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiCN.md">JacobiCN
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiCN = S.initFinalSymbol("JacobiCN", ID.JacobiCN);

  public final static IBuiltInSymbol JacobiDC = S.initFinalSymbol("JacobiDC", ID.JacobiDC);

  /**
   * JacobiDN(x, m) - returns the Jacobian elliptic function `dn(x, m)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiDN.md">JacobiDN
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiDN = S.initFinalSymbol("JacobiDN", ID.JacobiDN);

  public final static IBuiltInSymbol JacobiEpsilon =
      S.initFinalSymbol("JacobiEpsilon", ID.JacobiEpsilon);

  /**
   * JacobiMatrix(matrix, var) - creates a Jacobian matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiMatrix.md">JacobiMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiMatrix =
      S.initFinalSymbol("JacobiMatrix", ID.JacobiMatrix);

  public final static IBuiltInSymbol JacobiNC = S.initFinalSymbol("JacobiNC", ID.JacobiNC);

  public final static IBuiltInSymbol JacobiND = S.initFinalSymbol("JacobiND", ID.JacobiND);

  /**
   * JacobiP(n, a, b, z) - returns the Jacobi polynomial.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiP.md">JacobiP
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiP = S.initFinalSymbol("JacobiP", ID.JacobiP);

  /**
   * JacobiSC(x, m) - returns the Jacobian elliptic function `sc(x, m)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSC.md">JacobiSC
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiSC = S.initFinalSymbol("JacobiSC", ID.JacobiSC);

  /**
   * JacobiSD(x, m) - returns the Jacobian elliptic function `sd(x, m)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSD.md">JacobiSD
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiSD = S.initFinalSymbol("JacobiSD", ID.JacobiSD);

  /**
   * JacobiSN(x, m) - returns the Jacobian elliptic function `sn(x, m)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSN.md">JacobiSN
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiSN = S.initFinalSymbol("JacobiSN", ID.JacobiSN);

  /**
   * JacobiSymbol(m, n) - calculates the Jacobi symbol.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JacobiSymbol.md">JacobiSymbol
   *      documentation</a>
   */
  public final static IBuiltInSymbol JacobiSymbol =
      S.initFinalSymbol("JacobiSymbol", ID.JacobiSymbol);

  public final static IBuiltInSymbol JacobiZeta = S.initFinalSymbol("JacobiZeta", ID.JacobiZeta);

  /**
   * JavaClass[class-name] - a `JavaClass` expression can be created with the `LoadJavaClass`
   * function and wraps a Java `java.lang.Class` object. All static method names are assigned to a
   * context which will be created by the last part of the class name.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaClass.md">JavaClass
   *      documentation</a>
   */
  public final static IBuiltInSymbol JavaClass = S.initFinalSymbol("JavaClass", ID.JavaClass);

  /**
   * JavaForm(expr) - returns the Symja Java form of the `expr`. In Java you can use the created
   * Symja expressions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaForm.md">JavaForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol JavaForm = S.initFinalSymbol("JavaForm", ID.JavaForm);

  /**
   * JavaObject[class className] - a `JavaObject` can be created with the `JavaNew` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaNew.md">JavaNew
   *      documentation</a>
   */
  public final static IBuiltInSymbol JavaNew = S.initFinalSymbol("JavaNew", ID.JavaNew);

  /**
   * JavaNew["class-name"] - create a `JavaObject` from the `class-name` default constructor.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaObject.md">JavaObject
   *      documentation</a>
   */
  public final static IBuiltInSymbol JavaObject = S.initFinalSymbol("JavaObject", ID.JavaObject);

  /**
   * JavaObjectQ[java-object] - return `True` if `java-object` is a `JavaObject` expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaObjectQ.md">JavaObjectQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol JavaObjectQ = S.initFinalSymbol("JavaObjectQ", ID.JavaObjectQ);

  /**
   * JavaShow[ java.awt.Window ] - show the `JavaObject` which has to be an instance of
   * `java.awt.Window`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JavaShow.md">JavaShow
   *      documentation</a>
   */
  public final static IBuiltInSymbol JavaShow = S.initFinalSymbol("JavaShow", ID.JavaShow);

  /**
   * Join(l1, l2) - concatenates the lists `l1` and `l2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Join.md">Join
   *      documentation</a>
   */
  public final static IBuiltInSymbol Join = S.initFinalSymbol("Join", ID.Join);

  public final static IBuiltInSymbol Joined = S.initFinalSymbol("Joined", ID.Joined);

  /**
   * JSForm(expr) - returns the JavaScript form of the `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JSForm.md">JSForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol JSForm = S.initFinalSymbol("JSForm", ID.JSForm);

  public final static IBuiltInSymbol JSFormData = S.initFinalSymbol("JSFormData", ID.JSFormData);

  public final static IBuiltInSymbol KelvinBei = S.initFinalSymbol("KelvinBei", ID.KelvinBei);

  public final static IBuiltInSymbol KelvinBer = S.initFinalSymbol("KelvinBer", ID.KelvinBer);

  /**
   * Key(key) - represents a `key` used to access a value in an association.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Key.md">Key
   *      documentation</a>
   */
  public final static IBuiltInSymbol Key = S.initFinalSymbol("Key", ID.Key);

  public final static IBuiltInSymbol KeyAbsent = S.initFinalSymbol("KeyAbsent", ID.KeyAbsent);

  public final static IBuiltInSymbol KeyExistsQ = S.initFinalSymbol("KeyExistsQ", ID.KeyExistsQ);

  /**
   * Keys(association) - return a list of keys of the `association`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Keys.md">Keys
   *      documentation</a>
   */
  public final static IBuiltInSymbol Keys = S.initFinalSymbol("Keys", ID.Keys);

  /**
   * KeySelect(<|key1->value1, ...|>, head) - returns an association of the elements for which
   * `head(keyi)` returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeySelect.md">KeySelect
   *      documentation</a>
   */
  public final static IBuiltInSymbol KeySelect = S.initFinalSymbol("KeySelect", ID.KeySelect);

  /**
   * KeySort(<|key1->value1, ...|>) - sort the `<|key1->value1, ...|>` entries by the `key` values.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeySort.md">KeySort
   *      documentation</a>
   */
  public final static IBuiltInSymbol KeySort = S.initFinalSymbol("KeySort", ID.KeySort);

  /**
   * KeyTake(<|key1->value1, ...|>, {k1, k2,...}) - returns an association of the rules for which
   * the `k1, k2,...` are keys in the association.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeyTake.md">KeyTake
   *      documentation</a>
   */
  public final static IBuiltInSymbol KeyTake = S.initFinalSymbol("KeyTake", ID.KeyTake);

  /**
   * Khinchin - Khinchin's constant
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Khinchin.md">Khinchin
   *      documentation</a>
   */
  public final static IBuiltInSymbol Khinchin = S.initFinalSymbol("Khinchin", ID.Khinchin);

  public final static IBuiltInSymbol KleinInvariantJ =
      S.initFinalSymbol("KleinInvariantJ", ID.KleinInvariantJ);

  public final static IBuiltInSymbol KnownUnitQ = S.initFinalSymbol("KnownUnitQ", ID.KnownUnitQ);

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
      S.initFinalSymbol("KolmogorovSmirnovTest", ID.KolmogorovSmirnovTest);

  public final static IBuiltInSymbol KOrderlessPartitions =
      S.initFinalSymbol("KOrderlessPartitions", ID.KOrderlessPartitions);

  public final static IBuiltInSymbol KPartitions = S.initFinalSymbol("KPartitions", ID.KPartitions);

  /**
   * KroneckerDelta(arg1, arg2, ... argN) - if all arguments `arg1` to `argN` are equal return `1`,
   * otherwise return `0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KroneckerDelta.md">KroneckerDelta
   *      documentation</a>
   */
  public final static IBuiltInSymbol KroneckerDelta =
      S.initFinalSymbol("KroneckerDelta", ID.KroneckerDelta);

  /**
   * KroneckerProduct(t1, t2, ...) - Kronecker product of the tensors `t1, t2, ...`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KroneckerProduct.md">KroneckerProduct
   *      documentation</a>
   */
  public final static IBuiltInSymbol KroneckerProduct =
      S.initFinalSymbol("KroneckerProduct", ID.KroneckerProduct);

  public final static IBuiltInSymbol KroneckerSymbol =
      S.initFinalSymbol("KroneckerSymbol", ID.KroneckerSymbol);

  /**
   * Kurtosis(list) - gives the Pearson measure of kurtosis for `list` (a measure of existing
   * outliers).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Kurtosis.md">Kurtosis
   *      documentation</a>
   */
  public final static IBuiltInSymbol Kurtosis = S.initFinalSymbol("Kurtosis", ID.Kurtosis);

  public final static IBuiltInSymbol Labeled = S.initFinalSymbol("Labeled", ID.Labeled);

  public final static IBuiltInSymbol LabelingFunction =
      S.initFinalSymbol("LabelingFunction", ID.LabelingFunction);

  public final static IBuiltInSymbol LabelingSize =
      S.initFinalSymbol("LabelingSize", ID.LabelingSize);

  /**
   * LaguerreL(n, x) - returns the Laguerre polynomial `L_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaguerreL.md">LaguerreL
   *      documentation</a>
   */
  public final static IBuiltInSymbol LaguerreL = S.initFinalSymbol("LaguerreL", ID.LaguerreL);

  public final static IBuiltInSymbol LambertW = S.initFinalSymbol("LambertW", ID.LambertW);

  /**
   * LaplaceTransform(f,t,s) - returns the laplace transform.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaplaceTransform.md">LaplaceTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol LaplaceTransform =
      S.initFinalSymbol("LaplaceTransform", ID.LaplaceTransform);

  public final static IBuiltInSymbol Laplacian = S.initFinalSymbol("Laplacian", ID.Laplacian);

  public final static IBuiltInSymbol Large = S.initFinalSymbol("Large", ID.Large);

  /**
   * Last(expr) - returns the last element in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Last.md">Last
   *      documentation</a>
   */
  public final static IBuiltInSymbol Last = S.initFinalSymbol("Last", ID.Last);

  /**
   * LCM(n1, n2, ...) - computes the least common multiple of the given integers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LCM.md">LCM
   *      documentation</a>
   */
  public final static IBuiltInSymbol LCM = S.initFinalSymbol("LCM", ID.LCM);

  /**
   * LeafCount(expr) - returns the total number of indivisible subexpressions in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeafCount.md">LeafCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeafCount = S.initFinalSymbol("LeafCount", ID.LeafCount);

  /**
   * LeastSquares(matrix, right) - solves the linear least-squares problem 'matrix . x = right'.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeastSquares.md">LeastSquares
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeastSquares =
      S.initFinalSymbol("LeastSquares", ID.LeastSquares);

  public final static IBuiltInSymbol Left = S.initFinalSymbol("Left", ID.Left);

  /**
   * LegendreP(n, x) - returns the Legendre polynomial `P_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendreP.md">LegendreP
   *      documentation</a>
   */
  public final static IBuiltInSymbol LegendreP = S.initFinalSymbol("LegendreP", ID.LegendreP);

  /**
   * LegendreQ(n, x) - returns the Legendre functions of the second kind `Q_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendreQ.md">LegendreQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol LegendreQ = S.initFinalSymbol("LegendreQ", ID.LegendreQ);

  /**
   * Length(expr) - returns the number of leaves in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Length.md">Length
   *      documentation</a>
   */
  public final static IBuiltInSymbol Length = S.initFinalSymbol("Length", ID.Length);

  /**
   * LengthWhile({e1, e2, ...}, head) - returns the number of elements `ei` at the start of list for
   * which `head(ei)` returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LengthWhile.md">LengthWhile
   *      documentation</a>
   */
  public final static IBuiltInSymbol LengthWhile = S.initFinalSymbol("LengthWhile", ID.LengthWhile);

  public final static IBuiltInSymbol LerchPhi = S.initFinalSymbol("LerchPhi", ID.LerchPhi);

  /**
   * Less(x, y) - yields `True` if `x` is known to be less than `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Less.md">Less
   *      documentation</a>
   */
  public final static IBuiltInSymbol Less = S.initFinalSymbol("Less", ID.Less);

  /**
   * LessEqual(x, y) - yields `True` if `x` is known to be less than or equal `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqual.md">LessEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol LessEqual = S.initFinalSymbol("LessEqual", ID.LessEqual);

  public final static IBuiltInSymbol LessEqualThan =
      S.initFinalSymbol("LessEqualThan", ID.LessEqualThan);

  public final static IBuiltInSymbol LessThan = S.initFinalSymbol("LessThan", ID.LessThan);

  /**
   * LetterCharacter - represents letters..
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterCharacter.md">LetterCharacter
   *      documentation</a>
   */
  public final static IBuiltInSymbol LetterCharacter =
      S.initFinalSymbol("LetterCharacter", ID.LetterCharacter);

  /**
   * LetterCounts(string) - count the number of each distinct character in the `string` and return
   * the result as an association `<|char->counter1, ...|>`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterCounts.md">LetterCounts
   *      documentation</a>
   */
  public final static IBuiltInSymbol LetterCounts =
      S.initFinalSymbol("LetterCounts", ID.LetterCounts);

  /**
   * LetterNumber(character) - returns the position of the `character` in the English alphabet.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterNumber.md">LetterNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol LetterNumber =
      S.initFinalSymbol("LetterNumber", ID.LetterNumber);

  /**
   * LetterQ(expr) - tests whether `expr` is a string, which only contains letters.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LetterQ.md">LetterQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol LetterQ = S.initFinalSymbol("LetterQ", ID.LetterQ);

  /**
   * Level(expr, levelspec) - gives a list of all sub-expressions of `expr` at the level(s)
   * specified by `levelspec`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Level.md">Level
   *      documentation</a>
   */
  public final static IBuiltInSymbol Level = S.initFinalSymbol("Level", ID.Level);

  /**
   * LevelQ(expr) - tests whether `expr` is a valid level specification.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LevelQ.md">LevelQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol LevelQ = S.initFinalSymbol("LevelQ", ID.LevelQ);

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
      S.initFinalSymbol("LeviCivitaTensor", ID.LeviCivitaTensor);

  public final static IBuiltInSymbol Lexicographic =
      S.initFinalSymbol("Lexicographic", ID.Lexicographic);

  public final static IBuiltInSymbol LightBlue = S.initFinalSymbol("LightBlue", ID.LightBlue);

  public final static IBuiltInSymbol LightBrown = S.initFinalSymbol("LightBrown", ID.LightBrown);

  public final static IBuiltInSymbol LightCyan = S.initFinalSymbol("LightCyan", ID.LightCyan);

  public final static IBuiltInSymbol LightGray = S.initFinalSymbol("LightGray", ID.LightGray);

  public final static IBuiltInSymbol LightGreen = S.initFinalSymbol("LightGreen", ID.LightGreen);

  public final static IBuiltInSymbol Lighting = S.initFinalSymbol("Lighting", ID.Lighting);

  public final static IBuiltInSymbol LightMagenta =
      S.initFinalSymbol("LightMagenta", ID.LightMagenta);

  public final static IBuiltInSymbol LightOrange = S.initFinalSymbol("LightOrange", ID.LightOrange);

  public final static IBuiltInSymbol LightPink = S.initFinalSymbol("LightPink", ID.LightPink);

  public final static IBuiltInSymbol LightPurple = S.initFinalSymbol("LightPurple", ID.LightPurple);

  public final static IBuiltInSymbol LightRed = S.initFinalSymbol("LightRed", ID.LightRed);

  public final static IBuiltInSymbol LightYellow = S.initFinalSymbol("LightYellow", ID.LightYellow);

  /**
   * Limit(expr, x->x0) - gives the limit of `expr` as `x` approaches `x0`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Limit.md">Limit
   *      documentation</a>
   */
  public final static IBuiltInSymbol Limit = S.initFinalSymbol("Limit", ID.Limit);

  public final static IBuiltInSymbol Line = S.initFinalSymbol("Line", ID.Line);

  /**
   * LinearModelFit({{x1,y1},{x2,y2},...}, expr, symbol) - Create a linear regression model from a
   * matrix of observed value pairs `{x_i, y_i}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearModelFit.md">LinearModelFit
   *      documentation</a>
   */
  public final static IBuiltInSymbol LinearModelFit =
      S.initFinalSymbol("LinearModelFit", ID.LinearModelFit);

  public final static IBuiltInSymbol LinearOptimization =
      S.initFinalSymbol("LinearOptimization", ID.LinearOptimization);

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
      S.initFinalSymbol("LinearProgramming", ID.LinearProgramming);

  /**
   * LinearRecurrence(list1, list2, n) - solve the linear recurrence and return the generated
   * sequence of elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearRecurrence.md">LinearRecurrence
   *      documentation</a>
   */
  public final static IBuiltInSymbol LinearRecurrence =
      S.initFinalSymbol("LinearRecurrence", ID.LinearRecurrence);

  /**
   * LinearSolve(matrix, right) - solves the linear equation system `matrix . x = right` and returns
   * one corresponding solution `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LinearSolve.md">LinearSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol LinearSolve = S.initFinalSymbol("LinearSolve", ID.LinearSolve);

  public final static IBuiltInSymbol LinearSolveFunction =
      S.initFinalSymbol("LinearSolveFunction", ID.LinearSolveFunction);

  public final static IBuiltInSymbol LineGraph = S.initFinalSymbol("LineGraph", ID.LineGraph);

  public final static IBuiltInSymbol LiouvilleLambda =
      S.initFinalSymbol("LiouvilleLambda", ID.LiouvilleLambda);

  /**
   * List(e1, e2, ..., ei) - represents a list containing the elements `e1...ei`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/List.md">List
   *      documentation</a>
   */
  public final static IBuiltInSymbol List = S.initFinalSymbol("List", ID.List);

  /**
   * Listable - is an attribute specifying that a function should be automatically applied to each
   * element of a list.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Listable.md">Listable
   *      documentation</a>
   */
  public final static IBuiltInSymbol Listable = S.initFinalSymbol("Listable", ID.Listable);

  public final static IBuiltInSymbol ListContourPlot =
      S.initFinalSymbol("ListContourPlot", ID.ListContourPlot);

  /**
   * ListConvolve(kernel-list, tensor-list) - create the convolution of the `kernel-list` with
   * `tensor-list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListConvolve.md">ListConvolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListConvolve =
      S.initFinalSymbol("ListConvolve", ID.ListConvolve);

  /**
   * ListCorrelate(kernel-list, tensor-list) - create the correlation of the `kernel-list` with
   * `tensor-list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListCorrelate.md">ListCorrelate
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListCorrelate =
      S.initFinalSymbol("ListCorrelate", ID.ListCorrelate);

  public final static IBuiltInSymbol ListDensityPlot =
      S.initFinalSymbol("ListDensityPlot", ID.ListDensityPlot);

  /**
   * ListLinePlot( { list-of-points } ) - generate a JavaScript list line plot control for the
   * `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLinePlot.md">ListLinePlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListLinePlot =
      S.initFinalSymbol("ListLinePlot", ID.ListLinePlot);

  /**
   * ListLinePlot3D( { list-of-lines } ) - generate a JavaScript list plot 3D control for the
   * `list-of-lines`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLinePlot3D.md">ListLinePlot3D
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListLinePlot3D =
      S.initFinalSymbol("ListLinePlot3D", ID.ListLinePlot3D);

  public final static IBuiltInSymbol ListLogLinearPlot =
      S.initFinalSymbol("ListLogLinearPlot", ID.ListLogLinearPlot);

  /**
   * ListLogLogPlot( { list-of-points } ) - generate an image of a logarithmic X and logarithmic Y
   * plot for the `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLogLogPlot.md">ListLogLogPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListLogLogPlot =
      S.initFinalSymbol("ListLogLogPlot", ID.ListLogLogPlot);

  /**
   * ListLogPlot( { list-of-points } ) - generate an image of a logarithmic Y plot for the
   * `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLogPlot.md">ListLogPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListLogPlot = S.initFinalSymbol("ListLogPlot", ID.ListLogPlot);

  /**
   * ListPlot( { list-of-points } ) - generate a JavaScript list plot control for the
   * `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListPlot.md">ListPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListPlot = S.initFinalSymbol("ListPlot", ID.ListPlot);

  /**
   * ListPlot3D( { list-of-polygons } ) - generate a JavaScript list plot 3D control for the
   * `list-of-polygons`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListPlot3D.md">ListPlot3D
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListPlot3D = S.initFinalSymbol("ListPlot3D", ID.ListPlot3D);

  /**
   * ListPointPlot3D( { list-of-points } ) - generate a JavaScript list plot 3D control for the
   * `list-of-points`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListPointPlot3D.md">ListPointPlot3D
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListPointPlot3D =
      S.initFinalSymbol("ListPointPlot3D", ID.ListPointPlot3D);

  public final static IBuiltInSymbol ListPolarPlot =
      S.initFinalSymbol("ListPolarPlot", ID.ListPolarPlot);

  /**
   * ListQ(expr) - tests whether `expr` is a `List`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListQ.md">ListQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListQ = S.initFinalSymbol("ListQ", ID.ListQ);

  public final static IBuiltInSymbol ListStreamPlot =
      S.initFinalSymbol("ListStreamPlot", ID.ListStreamPlot);

  public final static IBuiltInSymbol ListStepPlot =
      S.initFinalSymbol("ListStepPlot", ID.ListStepPlot);


  public final static IBuiltInSymbol ListVectorPlot =
      S.initFinalSymbol("ListVectorPlot", ID.ListVectorPlot);

  public final static IBuiltInSymbol Literal = S.initFinalSymbol("Literal", ID.Literal);

  public final static IBuiltInSymbol LLMFunction = S.initFinalSymbol("LLMFunction", ID.LLMFunction);

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
      S.initFinalSymbol("LoadJavaClass", ID.LoadJavaClass);

  public final static IBuiltInSymbol Locked = S.initFinalSymbol("Locked", ID.Locked);

  /**
   * Log(z) - returns the natural logarithm of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log.md">Log
   *      documentation</a>
   */
  public final static IBuiltInSymbol Log = S.initFinalSymbol("Log", ID.Log);

  /**
   * Log10(z) - returns the base-`10` logarithm of `z`. `Log10(z)` will be converted to
   * `Log(z)/Log(10)` in symbolic mode.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log10.md">Log10
   *      documentation</a>
   */
  public final static IBuiltInSymbol Log10 = S.initFinalSymbol("Log10", ID.Log10);

  /**
   * Log2(z) - returns the base-`2` logarithm of `z`. `Log2(z)` will be converted to `Log(z)/Log(2)`
   * in symbolic mode.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Log2.md">Log2
   *      documentation</a>
   */
  public final static IBuiltInSymbol Log2 = S.initFinalSymbol("Log2", ID.Log2);

  /**
   * LogGamma(z) - is the logarithmic gamma function on the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogGamma.md">LogGamma
   *      documentation</a>
   */
  public final static IBuiltInSymbol LogGamma = S.initFinalSymbol("LogGamma", ID.LogGamma);

  public final static IBuiltInSymbol LogicalExpand =
      S.initFinalSymbol("LogicalExpand", ID.LogicalExpand);

  /**
   * LogIntegral(expr) - returns the integral logarithm of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogIntegral.md">LogIntegral
   *      documentation</a>
   */
  public final static IBuiltInSymbol LogIntegral = S.initFinalSymbol("LogIntegral", ID.LogIntegral);

  /**
   * LogisticSigmoid(z) - returns the logistic sigmoid of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogisticSigmoid.md">LogisticSigmoid
   *      documentation</a>
   */
  public final static IBuiltInSymbol LogisticSigmoid =
      S.initFinalSymbol("LogisticSigmoid", ID.LogisticSigmoid);

  public final static IBuiltInSymbol LogLinearPlot =
      S.initFinalSymbol("LogLinearPlot", ID.LogLinearPlot);

  public final static IBuiltInSymbol LogLogPlot = S.initFinalSymbol("LogLogPlot", ID.LogLogPlot);

  /**
   * LogNormalDistribution(m, s) - returns a log-normal distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogNormalDistribution.md">LogNormalDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol LogNormalDistribution =
      S.initFinalSymbol("LogNormalDistribution", ID.LogNormalDistribution);

  public final static IBuiltInSymbol LogPlot = S.initFinalSymbol("LogPlot", ID.LogPlot);

  public final static IBuiltInSymbol Longest = S.initFinalSymbol("Longest", ID.Longest);

  public final static IBuiltInSymbol LongForm = S.initFinalSymbol("LongForm", ID.LongForm);

  /**
   * Lookup(association, key) - return the value in the `association` which is associated with the
   * `key`. If no value is available return `Missing("KeyAbsent",key)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Lookup.md">Lookup
   *      documentation</a>
   */
  public final static IBuiltInSymbol Lookup = S.initFinalSymbol("Lookup", ID.Lookup);

  /**
   * LowerCaseQ(str) - is `True` if the given `str` is a string which only contains lower case
   * characters.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerCaseQ.md">LowerCaseQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol LowerCaseQ = S.initFinalSymbol("LowerCaseQ", ID.LowerCaseQ);

  /**
   * LowerTriangularize(matrix) - create a lower triangular matrix from the given `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerTriangularize.md">LowerTriangularize
   *      documentation</a>
   */
  public final static IBuiltInSymbol LowerTriangularize =
      S.initFinalSymbol("LowerTriangularize", ID.LowerTriangularize);

  /**
   * LowerTriangularMatrixQ(matrix) - returns `True` if `matrix` is lower triangular.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerTriangularMatrixQ.md">LowerTriangularMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol LowerTriangularMatrixQ =
      S.initFinalSymbol("LowerTriangularMatrixQ", ID.LowerTriangularMatrixQ);

  /**
   * LucasL(n) - gives the `n`th Lucas number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LucasL.md">LucasL
   *      documentation</a>
   */
  public final static IBuiltInSymbol LucasL = S.initFinalSymbol("LucasL", ID.LucasL);

  /**
   * LUDecomposition(matrix) - calculate the LUP-decomposition of a square `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LUDecomposition.md">LUDecomposition
   *      documentation</a>
   */
  public final static IBuiltInSymbol LUDecomposition =
      S.initFinalSymbol("LUDecomposition", ID.LUDecomposition);

  /**
   * MachineNumberQ(expr) - returns `True` if `expr` is a machine-precision real or complex number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MachineNumberQ.md">MachineNumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MachineNumberQ =
      S.initFinalSymbol("MachineNumberQ", ID.MachineNumberQ);

  public final static IBuiltInSymbol Magenta = S.initFinalSymbol("Magenta", ID.Magenta);

  public final static IBuiltInSymbol MakeBoxes = S.initFinalSymbol("MakeBoxes", ID.MakeBoxes);

  /**
   * MangoldtLambda(n) - the von Mangoldt function of `n`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MangoldtLambda.md">MangoldtLambda
   *      documentation</a>
   */
  public final static IBuiltInSymbol MangoldtLambda =
      S.initFinalSymbol("MangoldtLambda", ID.MangoldtLambda);

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
      S.initFinalSymbol("ManhattanDistance", ID.ManhattanDistance);

  /**
   * Manipulate(plot, {x, min, max}) - generate a JavaScript control for the expression `plot` which
   * can be manipulated by a range slider `{x, min, max}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Manipulate.md">Manipulate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Manipulate = S.initFinalSymbol("Manipulate", ID.Manipulate);

  public final static IBuiltInSymbol MantissaExponent =
      S.initFinalSymbol("MantissaExponent", ID.MantissaExponent);

  /**
   * Map(f, expr) or f /@ expr - applies `f` to each part on the first level of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Map.md">Map
   *      documentation</a>
   */
  public final static IBuiltInSymbol Map = S.initFinalSymbol("Map", ID.Map);

  public final static IBuiltInSymbol MapApply = S.initFinalSymbol("MapApply", ID.MapApply);

  public final static IBuiltInSymbol MapAll = S.initFinalSymbol("MapAll", ID.MapAll);

  public final static IBuiltInSymbol MapAt = S.initFinalSymbol("MapAt", ID.MapAt);

  /**
   * MapIndexed(f, expr) - applies `f` to each part on the first level of `expr` and appending the
   * elements position as a list in the second argument.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MapIndexed.md">MapIndexed
   *      documentation</a>
   */
  public final static IBuiltInSymbol MapIndexed = S.initFinalSymbol("MapIndexed", ID.MapIndexed);

  /**
   * MapThread(f, {{a1, a2, ...}, {b1, b2, ...}, ...}) - returns `{f(a1, b1, ...), f(a2, b2, ...),
   * ...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MapThread.md">MapThread
   *      documentation</a>
   */
  public final static IBuiltInSymbol MapThread = S.initFinalSymbol("MapThread", ID.MapThread);

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
      S.initFinalSymbol("MatchingDissimilarity", ID.MatchingDissimilarity);

  /**
   * MatchQ(expr, form) - tests whether `expr` matches `form`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatchQ.md">MatchQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatchQ = S.initFinalSymbol("MatchQ", ID.MatchQ);

  /**
   * MathMLForm(expr) - returns the MathML form of the evaluated `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MathMLForm.md">MathMLForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol MathMLForm = S.initFinalSymbol("MathMLForm", ID.MathMLForm);

  public final static IBuiltInSymbol Matrices = S.initFinalSymbol("Matrices", ID.Matrices);

  /**
   * MatrixD(f, X) - gives the matrix derivative of `f` with respect to the matrix `X`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixD.md">MatrixD
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixD = S.initFinalSymbol("MatrixD", ID.MatrixD);

  /**
   * MatrixExp(matrix) - computes the matrix exponential of the square `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixExp.md">MatrixExp
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixExp = S.initFinalSymbol("MatrixExp", ID.MatrixExp);

  /**
   * MatrixForm(matrix) - print a `matrix` or sparse array in matrix form
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixForm.md">MatrixForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixForm = S.initFinalSymbol("MatrixForm", ID.MatrixForm);

  public final static IBuiltInSymbol MatrixFunction =
      S.initFinalSymbol("MatrixFunction", ID.MatrixFunction);

  public final static IBuiltInSymbol MatrixLog = S.initFinalSymbol("MatrixLog", ID.MatrixLog);

  /**
   * MatrixMinimalPolynomial(matrix, var) - computes the matrix minimal polynomial of a `matrix` for
   * the variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixMinimalPolynomial.md">MatrixMinimalPolynomial
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixMinimalPolynomial =
      S.initFinalSymbol("MatrixMinimalPolynomial", ID.MatrixMinimalPolynomial);

  /**
   * MatrixPlot( matrix ) - create a matrix plot.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixPlot.md">MatrixPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixPlot = S.initFinalSymbol("MatrixPlot", ID.MatrixPlot);

  /**
   * MatrixPower(matrix, n) - computes the `n`th power of a `matrix`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixPower.md">MatrixPower
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixPower = S.initFinalSymbol("MatrixPower", ID.MatrixPower);

  /**
   * MatrixQ(m) - returns `True` if `m` is a list of equal-length lists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixQ.md">MatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixQ = S.initFinalSymbol("MatrixQ", ID.MatrixQ);

  /**
   * MatrixRank(matrix) - returns the rank of `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixRank.md">MatrixRank
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixRank = S.initFinalSymbol("MatrixRank", ID.MatrixRank);

  /**
   * Max(e_1, e_2, ..., e_i) - returns the expression with the greatest value among the `e_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Max.md">Max
   *      documentation</a>
   */
  public final static IBuiltInSymbol Max = S.initFinalSymbol("Max", ID.Max);

  /**
   * MaxFilter(list, r) - filter which evaluates the `Max` of `list` for the radius `r`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MaxFilter.md">MaxFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol MaxFilter = S.initFinalSymbol("MaxFilter", ID.MaxFilter);

  /**
   * Maximize(unary-function, variable) - returns the maximum of the unary function for the given
   * `variable`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Maximize.md">Maximize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Maximize = S.initFinalSymbol("Maximize", ID.Maximize);

  public final static IBuiltInSymbol MaxIterations =
      S.initFinalSymbol("MaxIterations", ID.MaxIterations);

  public final static IBuiltInSymbol MaxMemoryUsed =
      S.initFinalSymbol("MaxMemoryUsed", ID.MaxMemoryUsed);

  public final static IBuiltInSymbol MaxPoints = S.initFinalSymbol("MaxPoints", ID.MaxPoints);

  public final static IBuiltInSymbol MaxRoots = S.initFinalSymbol("MaxRoots", ID.MaxRoots);

  /**
   * Mean(list) - returns the statistical mean of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Mean.md">Mean
   *      documentation</a>
   */
  public final static IBuiltInSymbol Mean = S.initFinalSymbol("Mean", ID.Mean);

  public final static IBuiltInSymbol MeanDeviation =
      S.initFinalSymbol("MeanDeviation", ID.MeanDeviation);

  /**
   * MeanFilter(list, r) - filter which evaluates the `Mean` of `list` for the radius `r`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeanFilter.md">MeanFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeanFilter = S.initFinalSymbol("MeanFilter", ID.MeanFilter);

  /**
   * Median(list) - returns the median of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Median.md">Median
   *      documentation</a>
   */
  public final static IBuiltInSymbol Median = S.initFinalSymbol("Median", ID.Median);

  /**
   * MedianFilter(list, r) - filter which evaluates the `Median` of `list` for the radius `r`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MedianFilter.md">MedianFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol MedianFilter =
      S.initFinalSymbol("MedianFilter", ID.MedianFilter);

  public final static IBuiltInSymbol Medium = S.initFinalSymbol("Medium", ID.Medium);

  public final static IBuiltInSymbol MeijerG = S.initFinalSymbol("MeijerG", ID.MeijerG);

  /**
   * MemberQ(list, pattern) - returns `True` if pattern matches any element of `list`, or `False`
   * otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MemberQ.md">MemberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MemberQ = S.initFinalSymbol("MemberQ", ID.MemberQ);

  public final static IBuiltInSymbol MemoryAvailable =
      S.initFinalSymbol("MemoryAvailable", ID.MemoryAvailable);

  public final static IBuiltInSymbol MemoryInUse = S.initFinalSymbol("MemoryInUse", ID.MemoryInUse);

  public final static IBuiltInSymbol Merge = S.initFinalSymbol("Merge", ID.Merge);
  /**
   * MersennePrimeExponent(n) - returns the `n`th mersenne prime exponent. `2^n - 1` must be a prime
   * number. Currently `0 < n <= 47` can be computed, otherwise the function returns unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MersennePrimeExponent.md">MersennePrimeExponent
   *      documentation</a>
   */
  public final static IBuiltInSymbol MersennePrimeExponent =
      S.initFinalSymbol("MersennePrimeExponent", ID.MersennePrimeExponent);

  /**
   * MersennePrimeExponentQ(n) - returns `True` if `2^n - 1` is a prime number. Currently `0 <= n <=
   * 47` can be computed in reasonable time.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MersennePrimeExponentQ.md">MersennePrimeExponentQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MersennePrimeExponentQ =
      S.initFinalSymbol("MersennePrimeExponentQ", ID.MersennePrimeExponentQ);

  public final static IBuiltInSymbol Mesh = S.initFinalSymbol("Mesh", ID.Mesh);

  public final static IBuiltInSymbol MeshRange = S.initFinalSymbol("MeshRange", ID.MeshRange);

  /**
   * Message(symbol::msg, expr1, expr2, ...) - displays the specified message, replacing
   * placeholders in the message text with the corresponding expressions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Message.md">Message
   *      documentation</a>
   */
  public final static IBuiltInSymbol Message = S.initFinalSymbol("Message", ID.Message);

  /**
   * MessageName(symbol, msg) - `symbol::msg` identifies a message. `MessageName` is the head of
   * message IDs of the form `symbol::tag`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MessageName.md">MessageName
   *      documentation</a>
   */
  public final static IBuiltInSymbol MessageName = S.initFinalSymbol("MessageName", ID.MessageName);

  /**
   * Messages(symbol) - return all messages which are asociated to `symbol`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Messages.md">Messages
   *      documentation</a>
   */
  public final static IBuiltInSymbol Messages = S.initFinalSymbol("Messages", ID.Messages);

  public final static IBuiltInSymbol Method = S.initFinalSymbol("Method", ID.Method);

  /**
   * Min(e_1, e_2, ..., e_i) - returns the expression with the lowest value among the `e_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Min.md">Min
   *      documentation</a>
   */
  public final static IBuiltInSymbol Min = S.initFinalSymbol("Min", ID.Min);

  /**
   * MinFilter(list, r) - filter which evaluates the `Min` of `list` for the radius `r`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MinFilter.md">MinFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol MinFilter = S.initFinalSymbol("MinFilter", ID.MinFilter);

  public final static IBuiltInSymbol MinimalPolynomial =
      S.initFinalSymbol("MinimalPolynomial", ID.MinimalPolynomial);

  /**
   * Minimize(unary-function, variable) - returns the minimum of the unary function for the given
   * `variable`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Minimize.md">Minimize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Minimize = S.initFinalSymbol("Minimize", ID.Minimize);

  public final static IBuiltInSymbol MinMax = S.initFinalSymbol("MinMax", ID.MinMax);

  public final static IBuiltInSymbol Minor = S.initFinalSymbol("Minor", ID.Minor);

  /**
   * Minors(matrix) - returns the minors of the matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Minors.md">Minors
   *      documentation</a>
   */
  public final static IBuiltInSymbol Minors = S.initFinalSymbol("Minors", ID.Minors);

  /**
   * Minus(expr) - is the negation of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Minus.md">Minus
   *      documentation</a>
   */
  public final static IBuiltInSymbol Minus = S.initFinalSymbol("Minus", ID.Minus);

  public final static IBuiltInSymbol Missing = S.initFinalSymbol("Missing", ID.Missing);

  /**
   * MissingQ(expr) - returns `True` if `expr` is a `Missing()` expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MissingQ.md">MissingQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MissingQ = S.initFinalSymbol("MissingQ", ID.MissingQ);

  /**
   * Mod(x, m) - returns `x` modulo `m`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Mod.md">Mod
   *      documentation</a>
   */
  public final static IBuiltInSymbol Mod = S.initFinalSymbol("Mod", ID.Mod);

  /**
   * ModularInverse(k, n) - returns the modular inverse `k^(-1) mod n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ModularInverse.md">ModularInverse
   *      documentation</a>
   */
  public final static IBuiltInSymbol ModularInverse =
      S.initFinalSymbol("ModularInverse", ID.ModularInverse);

  /**
   * Module({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables`
   * by renaming local variables.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Module.md">Module
   *      documentation</a>
   */
  public final static IBuiltInSymbol Module = S.initFinalSymbol("Module", ID.Module);

  public final static IBuiltInSymbol Modulus = S.initFinalSymbol("Modulus", ID.Modulus);

  /**
   * MoebiusMu(expr) - calculate the Möbius function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoebiusMu.md">MoebiusMu
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoebiusMu = S.initFinalSymbol("MoebiusMu", ID.MoebiusMu);

  /**
   * MonomialList(polynomial, list-of-variables) - get the list of monomials of a `polynomial`
   * expression, with respect to the `list-of-variables`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MonomialList.md">MonomialList
   *      documentation</a>
   */
  public final static IBuiltInSymbol MonomialList =
      S.initFinalSymbol("MonomialList", ID.MonomialList);

  public final static IBuiltInSymbol MonomialOrder =
      S.initFinalSymbol("MonomialOrder", ID.MonomialOrder);

  /**
   * Most(expr) - returns `expr` with the last element removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Most.md">Most
   *      documentation</a>
   */
  public final static IBuiltInSymbol Most = S.initFinalSymbol("Most", ID.Most);

  /**
   * Multinomial(n1, n2, ...) - gives the multinomial coefficient `(n1+n2+...)!/(n1! n2! ...)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Multinomial.md">Multinomial
   *      documentation</a>
   */
  public final static IBuiltInSymbol Multinomial = S.initFinalSymbol("Multinomial", ID.Multinomial);

  /**
   * MultiplicativeOrder(a, n) - gives the multiplicative order `a` modulo `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MultiplicativeOrder.md">MultiplicativeOrder
   *      documentation</a>
   */
  public final static IBuiltInSymbol MultiplicativeOrder =
      S.initFinalSymbol("MultiplicativeOrder", ID.MultiplicativeOrder);

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
      S.initFinalSymbol("MultiplySides", ID.MultiplySides);

  /**
   * N(expr) - gives the numerical value of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/N.md">N
   *      documentation</a>
   */
  public final static IBuiltInSymbol N = S.initFinalSymbol("N", ID.N);

  /**
   * NakagamiDistribution(m, o) - returns a Nakagami distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NakagamiDistribution.md">NakagamiDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol NakagamiDistribution =
      S.initFinalSymbol("NakagamiDistribution", ID.NakagamiDistribution);

  public final static IBuiltInSymbol NameQ = S.initFinalSymbol("NameQ", ID.NameQ);

  /**
   * Names(string) - return the symbols from the context path matching the `string` or `pattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Names.md">Names
   *      documentation</a>
   */
  public final static IBuiltInSymbol Names = S.initFinalSymbol("Names", ID.Names);

  public final static IBuiltInSymbol NArgMax = S.initFinalSymbol("NArgMax", ID.NArgMax);

  public final static IBuiltInSymbol NArgMin = S.initFinalSymbol("NArgMin", ID.NArgMin);

  /**
   * Nand(arg1, arg2, ...) - Logical NAND function. It evaluates its arguments in order, giving
   * `True` immediately if any of them are `False`, and `False` if they are all `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nand.md">Nand
   *      documentation</a>
   */
  public final static IBuiltInSymbol Nand = S.initFinalSymbol("Nand", ID.Nand);

  /**
   * ND(function, x, value) - returns a numerical approximation of the partial derivative of the
   * `function` for the variable `x` and the given `value`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ND.md">ND
   *      documentation</a>
   */
  public final static IBuiltInSymbol ND = S.initFinalSymbol("ND", ID.ND);

  /**
   * NDSolve({equation-list}, functions, t) - attempts to solve the linear differential
   * `equation-list` for the `functions` and the time-dependent-variable `t`. Returns an
   * `InterpolatingFunction` function object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NDSolve.md">NDSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol NDSolve = S.initFinalSymbol("NDSolve", ID.NDSolve);


  public final static IBuiltInSymbol NExpectation =
      S.initFinalSymbol("NExpectation", ID.NExpectation);

  public final static IBuiltInSymbol Nearest = S.initFinalSymbol("Nearest", ID.Nearest);

  public final static IBuiltInSymbol NearestTo = S.initFinalSymbol("NearestTo", ID.NearestTo);

  public final static IBuiltInSymbol Needs = S.initFinalSymbol("Needs", ID.Needs);

  /**
   * Negative(x) - returns `True` if `x` is a negative real number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Negative.md">Negative
   *      documentation</a>
   */
  public final static IBuiltInSymbol Negative = S.initFinalSymbol("Negative", ID.Negative);

  public final static IBuiltInSymbol NegativeDegreeLexicographic =
      S.initFinalSymbol("NegativeDegreeLexicographic", ID.NegativeDegreeLexicographic);

  public final static IBuiltInSymbol NegativeDegreeReverseLexicographic = S
      .initFinalSymbol("NegativeDegreeReverseLexicographic", ID.NegativeDegreeReverseLexicographic);

  public final static IBuiltInSymbol NegativeIntegers =
      S.initFinalSymbol("NegativeIntegers", ID.NegativeIntegers);

  public final static IBuiltInSymbol NegativeLexicographic =
      S.initFinalSymbol("NegativeLexicographic", ID.NegativeLexicographic);

  public final static IBuiltInSymbol NegativeRationals =
      S.initFinalSymbol("NegativeRationals", ID.NegativeRationals);

  public final static IBuiltInSymbol NegativeReals =
      S.initFinalSymbol("NegativeReals", ID.NegativeReals);

  /**
   * Nest(f, expr, n) - starting with `expr`, iteratively applies `f` `n` times and returns the
   * final result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nest.md">Nest
   *      documentation</a>
   */
  public final static IBuiltInSymbol Nest = S.initFinalSymbol("Nest", ID.Nest);

  /**
   * NestList(f, expr, n) - starting with `expr`, iteratively applies `f` `n` times and returns a
   * list of all intermediate results.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestList.md">NestList
   *      documentation</a>
   */
  public final static IBuiltInSymbol NestList = S.initFinalSymbol("NestList", ID.NestList);

  /**
   * NestWhile(f, expr, test) - applies a function `f` repeatedly on an expression `expr`, until
   * applying `test` on the result no longer yields `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestWhile.md">NestWhile
   *      documentation</a>
   */
  public final static IBuiltInSymbol NestWhile = S.initFinalSymbol("NestWhile", ID.NestWhile);

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
      S.initFinalSymbol("NestWhileList", ID.NestWhileList);

  public final static IBuiltInSymbol NewLimit = S.initFinalSymbol("NewLimit", ID.NewLimit);

  /**
   * NextPrime(n) - gives the next prime after `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NextPrime.md">NextPrime
   *      documentation</a>
   */
  public final static IBuiltInSymbol NextPrime = S.initFinalSymbol("NextPrime", ID.NextPrime);

  public final static IBuiltInSymbol NFourierTransform =
      S.initFinalSymbol("NFourierTransform", ID.NFourierTransform);

  /**
   * NHoldAll - is an attribute that protects all arguments of a function from numeric evaluation.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NHoldAll.md">NHoldAll
   *      documentation</a>
   */
  public final static IBuiltInSymbol NHoldAll = S.initFinalSymbol("NHoldAll", ID.NHoldAll);

  /**
   * NHoldFirst - is an attribute that protects the first argument of a function from numeric
   * evaluation.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NHoldFirst.md">NHoldFirst
   *      documentation</a>
   */
  public final static IBuiltInSymbol NHoldFirst = S.initFinalSymbol("NHoldFirst", ID.NHoldFirst);

  /**
   * NHoldRest - is an attribute that protects all but the first argument of a function from numeric
   * evaluation.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NHoldRest.md">NHoldRest
   *      documentation</a>
   */
  public final static IBuiltInSymbol NHoldRest = S.initFinalSymbol("NHoldRest", ID.NHoldRest);

  /**
   * NIntegrate(f, {x,a,b}) - computes the numerical univariate real integral of `f` with respect to
   * `x` from `a` to `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NIntegrate.md">NIntegrate
   *      documentation</a>
   */
  public final static IBuiltInSymbol NIntegrate = S.initFinalSymbol("NIntegrate", ID.NIntegrate);

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
  public final static IBuiltInSymbol NMaximize = S.initFinalSymbol("NMaximize", ID.NMaximize);

  public final static IBuiltInSymbol NMaxValue = S.initFinalSymbol("NMaxValue", ID.NMaxValue);

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
  public final static IBuiltInSymbol NMinimize = S.initFinalSymbol("NMinimize", ID.NMinimize);

  public final static IBuiltInSymbol NMinValue = S.initFinalSymbol("NMinValue", ID.NMinValue);

  public final static IBuiltInSymbol NonCommutativeMultiply =
      S.initFinalSymbol("NonCommutativeMultiply", ID.NonCommutativeMultiply);

  /**
   * None - is a possible value for `Span` and `Quiet`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/None.md">None
   *      documentation</a>
   */
  public final static IBuiltInSymbol None = S.initFinalSymbol("None", ID.None);

  /**
   * NoneTrue({expr1, expr2, ...}, test) - returns `True` if no application of `test` to `expr1,
   * expr2, ...` evaluates to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NoneTrue.md">NoneTrue
   *      documentation</a>
   */
  public final static IBuiltInSymbol NoneTrue = S.initFinalSymbol("NoneTrue", ID.NoneTrue);

  public final static IBuiltInSymbol Nonexistent = S.initFinalSymbol("Nonexistent", ID.Nonexistent);

  /**
   * NonNegative(x) - returns `True` if `x` is a positive real number or zero.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NonNegative.md">NonNegative
   *      documentation</a>
   */
  public final static IBuiltInSymbol NonNegative = S.initFinalSymbol("NonNegative", ID.NonNegative);

  public final static IBuiltInSymbol NonNegativeIntegers =
      S.initFinalSymbol("NonNegativeIntegers", ID.NonNegativeIntegers);

  public final static IBuiltInSymbol NonNegativeRationals =
      S.initFinalSymbol("NonNegativeRationals", ID.NonNegativeRationals);

  public final static IBuiltInSymbol NonNegativeReals =
      S.initFinalSymbol("NonNegativeReals", ID.NonNegativeReals);

  /**
   * NonPositive(x) - returns `True` if `x` is a negative real number or zero.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NonPositive.md">NonPositive
   *      documentation</a>
   */
  public final static IBuiltInSymbol NonPositive = S.initFinalSymbol("NonPositive", ID.NonPositive);

  /**
   * Nor(arg1, arg2, ...)' - Logical NOR function. It evaluates its arguments in order, giving
   * `False` immediately if any of them are `True`, and `True` if they are all `False`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nor.md">Nor
   *      documentation</a>
   */
  public final static IBuiltInSymbol Nor = S.initFinalSymbol("Nor", ID.Nor);

  /**
   * Norm(m, l) - computes the `l`-norm of matrix `m` (currently only works for vectors!).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Norm.md">Norm
   *      documentation</a>
   */
  public final static IBuiltInSymbol Norm = S.initFinalSymbol("Norm", ID.Norm);

  /**
   * Normal(expr) - converts a Symja expression `expr` into a normal expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Normal.md">Normal
   *      documentation</a>
   */
  public final static IBuiltInSymbol Normal = S.initFinalSymbol("Normal", ID.Normal);

  /**
   * NormalDistribution(m, s) - returns the normal distribution of mean `m` and sigma `s`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NormalDistribution.md">NormalDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol NormalDistribution =
      S.initFinalSymbol("NormalDistribution", ID.NormalDistribution);

  /**
   * Normalize(v) - calculates the normalized vector `v` as `v/Norm(v)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Normalize.md">Normalize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Normalize = S.initFinalSymbol("Normalize", ID.Normalize);

  public final static IBuiltInSymbol NormalMatrixQ =
      S.initFinalSymbol("NormalMatrixQ", ID.NormalMatrixQ);

  /**
   * Not(expr) - Logical Not function (negation). Returns `True` if the statement is `False`.
   * Returns `False` if the `expr` is `True`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Not.md">Not
   *      documentation</a>
   */
  public final static IBuiltInSymbol Not = S.initFinalSymbol("Not", ID.Not);

  public final static IBuiltInSymbol NotApplicable =
      S.initFinalSymbol("NotApplicable", ID.NotApplicable);

  public final static IBuiltInSymbol NotAvailable =
      S.initFinalSymbol("NotAvailable", ID.NotAvailable);

  public final static IBuiltInSymbol NotElement = S.initFinalSymbol("NotElement", ID.NotElement);

  /**
   * Nothing - during evaluation of a list with a `Nothing` element `{..., Nothing, ...}`, the
   * symbol `Nothing` is removed from the arguments.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nothing.md">Nothing
   *      documentation</a>
   */
  public final static IBuiltInSymbol Nothing = S.initFinalSymbol("Nothing", ID.Nothing);

  public final static IBuiltInSymbol NotListQ = S.initFinalSymbol("NotListQ", ID.NotListQ);

  public final static IBuiltInSymbol Now = S.initFinalSymbol("Now", ID.Now);

  public final static IBuiltInSymbol NProbability =
      S.initFinalSymbol("NProbability", ID.NProbability);

  public final static IBuiltInSymbol NProduct = S.initFinalSymbol("NProduct", ID.NProduct);

  /**
   * NRoots(polynomial==0) - gives the numerical roots of a univariate polynomial `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NRoots.md">NRoots
   *      documentation</a>
   */
  public final static IBuiltInSymbol NRoots = S.initFinalSymbol("NRoots", ID.NRoots);

  /**
   * NSolve(equations, vars) - attempts to solve `equations` for the variables `vars`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NSolve.md">NSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol NSolve = S.initFinalSymbol("NSolve", ID.NSolve);

  public final static IBuiltInSymbol NSum = S.initFinalSymbol("NSum", ID.NSum);

  /**
   * Null - is the implicit result of expressions that do not yield a result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Null.md">Null
   *      documentation</a>
   */
  public final static IBuiltInSymbol Null = S.initFinalSymbol("Null", ID.Null);

  /**
   * NullSpace(matrix) - returns a list of vectors that span the nullspace of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NullSpace.md">NullSpace
   *      documentation</a>
   */
  public final static IBuiltInSymbol NullSpace = S.initFinalSymbol("NullSpace", ID.NullSpace);

  public final static IBuiltInSymbol Number = S.initFinalSymbol("Number", ID.Number);

  public final static IBuiltInSymbol NumberFieldRootsOfUnity =
      S.initFinalSymbol("NumberFieldRootsOfUnity", ID.NumberFieldRootsOfUnity);

  /**
   * NumberLinePlot( list-of-numbers ) - generates a JavaScript control, which plots a list of
   * values along a line. for the `list-of-numbers`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberLinePlot.md">NumberLinePlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumberLinePlot =
      S.initFinalSymbol("NumberLinePlot", ID.NumberLinePlot);

  /**
   * NumberQ(expr) - returns `True` if `expr` is an explicit number, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberQ.md">NumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumberQ = S.initFinalSymbol("NumberQ", ID.NumberQ);

  public final static IBuiltInSymbol NumberDigit = S.initFinalSymbol("NumberDigit", ID.NumberDigit);

  /**
   * NumberString - represents the characters in a number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberString.md">NumberString
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumberString =
      S.initFinalSymbol("NumberString", ID.NumberString);

  /**
   * Numerator(expr) - gives the numerator in `expr`. Numerator collects expressions with non
   * negative exponents.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Numerator.md">Numerator
   *      documentation</a>
   */
  public final static IBuiltInSymbol Numerator = S.initFinalSymbol("Numerator", ID.Numerator);

  /**
   * NumericalOrder(a, b) - is `0` if `a` equals `b`. Is `-1` or `1` according to numerical order of
   * `a` and `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumericalOrder.md">NumericalOrder
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumericalOrder =
      S.initFinalSymbol("NumericalOrder", ID.NumericalOrder);

  /**
   * NumericalSort(list) - `NumericalSort(list)` is evaluated by calling `Sort(list,
   * NumericalOrder)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumericalSort.md">NumericalSort
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumericalSort =
      S.initFinalSymbol("NumericalSort", ID.NumericalSort);

  public final static IBuiltInSymbol NumericArray =
      S.initFinalSymbol("NumericArray", ID.NumericArray);

  public final static IBuiltInSymbol NumericArrayQ =
      S.initFinalSymbol("NumericArrayQ", ID.NumericArrayQ);

  public final static IBuiltInSymbol NumericArrayType =
      S.initFinalSymbol("NumericArrayType", ID.NumericArrayType);

  public final static IBuiltInSymbol NumericFunction =
      S.initFinalSymbol("NumericFunction", ID.NumericFunction);

  /**
   * NumericQ(expr) - returns `True` if `expr` is an explicit numeric expression, and `False`
   * otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumericQ.md">NumericQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumericQ = S.initFinalSymbol("NumericQ", ID.NumericQ);

  public final static IBuiltInSymbol NuttallWindow =
      S.initFinalSymbol("NuttallWindow", ID.NuttallWindow);

  public final static IBuiltInSymbol O = S.initFinalSymbol("O", ID.O);

  public final static IBuiltInSymbol Octahedron = S.initFinalSymbol("Octahedron", ID.Octahedron);

  /**
   * OddQ(x) - returns `True` if `x` is odd, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OddQ.md">OddQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol OddQ = S.initFinalSymbol("OddQ", ID.OddQ);

  /**
   * Off( ) - switch off the interactive trace.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Off.md">Off
   *      documentation</a>
   */
  public final static IBuiltInSymbol Off = S.initFinalSymbol("Off", ID.Off);

  /**
   * On( ) - switch on the interactive trace. The output is printed in the defined `out` stream.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/On.md">On
   *      documentation</a>
   */
  public final static IBuiltInSymbol On = S.initFinalSymbol("On", ID.On);

  /**
   * OneIdentity - is an attribute assigned to a symbol, say `f`, indicating that `f(x)`,
   * `f(f(x))`,... etc. are all equivalent to `x` in pattern matching.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OneIdentity.md">OneIdentity
   *      documentation</a>
   */
  public final static IBuiltInSymbol OneIdentity = S.initFinalSymbol("OneIdentity", ID.OneIdentity);

  public final static IBuiltInSymbol Opacity = S.initFinalSymbol("Opacity", ID.Opacity);

  /**
   * OpenAppend("file-name") - opens a file and returns an OutputStream to which writes are
   * appended.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OpenAppend.md">OpenAppend
   *      documentation</a>
   */
  public final static IBuiltInSymbol OpenAppend = S.initFinalSymbol("OpenAppend", ID.OpenAppend);

  public final static IBuiltInSymbol OpenRead = S.initFinalSymbol("OpenRead", ID.OpenRead);

  /**
   * OpenWrite() - creates an empty file in the default temporary-file directory and returns an
   * OutputStream.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OpenWrite.md">OpenWrite
   *      documentation</a>
   */
  public final static IBuiltInSymbol OpenWrite = S.initFinalSymbol("OpenWrite", ID.OpenWrite);

  /**
   * Operate(p, expr) - applies `p` to the head of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Operate.md">Operate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Operate = S.initFinalSymbol("Operate", ID.Operate);

  /**
   * OptimizeExpression(function) - common subexpressions elimination for a complicated `function`
   * by generating "dummy" variables for these subexpressions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OptimizeExpression.md">OptimizeExpression
   *      documentation</a>
   */
  public final static IBuiltInSymbol OptimizeExpression =
      S.initFinalSymbol("OptimizeExpression", ID.OptimizeExpression);

  /**
   * Optional(patt, default) - is a pattern which matches `patt`, which if omitted should be
   * replaced by `default`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Optional.md">Optional
   *      documentation</a>
   */
  public final static IBuiltInSymbol Optional = S.initFinalSymbol("Optional", ID.Optional);

  /**
   * Options(symbol) - gives a list of optional arguments to `symbol` and their default values.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Options.md">Options
   *      documentation</a>
   */
  public final static IBuiltInSymbol Options = S.initFinalSymbol("Options", ID.Options);

  public final static IBuiltInSymbol OptionsPattern =
      S.initFinalSymbol("OptionsPattern", ID.OptionsPattern);

  /**
   * OptionValue(name) - gives the value of the option `name` as specified in a call to a function
   * with `OptionsPattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OptionValue.md">OptionValue
   *      documentation</a>
   */
  public final static IBuiltInSymbol OptionValue = S.initFinalSymbol("OptionValue", ID.OptionValue);

  /**
   * Or(expr1, expr2, ...)' - `expr1 || expr2 || ...` evaluates each expression in turn, returning
   * `True` as soon as an expression evaluates to `True`. If all expressions evaluate to `False`,
   * `Or` returns `False`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Or.md">Or
   *      documentation</a>
   */
  public final static IBuiltInSymbol Or = S.initFinalSymbol("Or", ID.Or);

  public final static IBuiltInSymbol Orange = S.initFinalSymbol("Orange", ID.Orange);

  /**
   * Order(a, b) - is `0` if `a` equals `b`. Is `-1` or `1` according to canonical order of `a` and
   * `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Order.md">Order
   *      documentation</a>
   */
  public final static IBuiltInSymbol Order = S.initFinalSymbol("Order", ID.Order);

  /**
   * OrderedQ({a, b,...}) - is `True` if `a` sorts before `b` according to canonical ordering for
   * all adjacent elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OrderedQ.md">OrderedQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol OrderedQ = S.initFinalSymbol("OrderedQ", ID.OrderedQ);

  /**
   * Ordering(list) - calculate the permutation list of the elements in the sorted `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ordering.md">Ordering
   *      documentation</a>
   */
  public final static IBuiltInSymbol Ordering = S.initFinalSymbol("Ordering", ID.Ordering);

  /**
   * Orderless - is an attribute indicating that the leaves in an expression `f(a, b, c)` can be
   * placed in any order.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Orderless.md">Orderless
   *      documentation</a>
   */
  public final static IBuiltInSymbol Orderless = S.initFinalSymbol("Orderless", ID.Orderless);

  /**
   * Orthogonalize(matrix) - returns a basis for the orthogonalized set of vectors defined by
   * `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Orthogonalize.md">Orthogonalize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Orthogonalize =
      S.initFinalSymbol("Orthogonalize", ID.Orthogonalize);

  /**
   * OrthogonalMatrixQ(matrix) - returns `True`, if `matrix` is an orthogonal matrix. `False`
   * otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OrthogonalMatrixQ.md">OrthogonalMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol OrthogonalMatrixQ =
      S.initFinalSymbol("OrthogonalMatrixQ", ID.OrthogonalMatrixQ);

  /**
   * Out(k) - gives the result of the `k`th input line.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Out.md">Out
   *      documentation</a>
   */
  public final static IBuiltInSymbol Out = S.initFinalSymbol("Out", ID.Out);

  /**
   * Outer(f, x, y) - computes a generalised outer product of `x` and `y`, using the function `f` in
   * place of multiplication.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Outer.md">Outer
   *      documentation</a>
   */
  public final static IBuiltInSymbol Outer = S.initFinalSymbol("Outer", ID.Outer);

  public final static IBuiltInSymbol OutputForm = S.initFinalSymbol("OutputForm", ID.OutputForm);

  /**
   * OutputStream("file-name") - opens a file and returns an OutputStream.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OutputStream.md">OutputStream
   *      documentation</a>
   */
  public final static IBuiltInSymbol OutputStream =
      S.initFinalSymbol("OutputStream", ID.OutputStream);

  public final static IBuiltInSymbol Overflow = S.initFinalSymbol("Overflow", ID.Overflow);

  public final static IBuiltInSymbol Overlaps = S.initFinalSymbol("Overlaps", ID.Overlaps);

  public final static IBuiltInSymbol Overscript = S.initFinalSymbol("Overscript", ID.Overscript);

  public final static IBuiltInSymbol OverscriptBox =
      S.initFinalSymbol("OverscriptBox", ID.OverscriptBox);

  /**
   * OwnValues(symbol) - prints the own-value rule associated with `symbol`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OwnValues.md">OwnValues
   *      documentation</a>
   */
  public final static IBuiltInSymbol OwnValues = S.initFinalSymbol("OwnValues", ID.OwnValues);

  public final static IBuiltInSymbol Package = S.initFinalSymbol("Package", ID.Package);

  public final static IBuiltInSymbol PadeApproximant =
      S.initFinalSymbol("PadeApproximant", ID.PadeApproximant);

  /**
   * PadLeft(list, n) - pads `list` to length `n` by adding `0` on the left.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PadLeft.md">PadLeft
   *      documentation</a>
   */
  public final static IBuiltInSymbol PadLeft = S.initFinalSymbol("PadLeft", ID.PadLeft);

  /**
   * PadRight(list, n) - pads `list` to length `n` by adding `0` on the right.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PadRight.md">PadRight
   *      documentation</a>
   */
  public final static IBuiltInSymbol PadRight = S.initFinalSymbol("PadRight", ID.PadRight);

  public final static IBuiltInSymbol ParallelMap = S.initFinalSymbol("ParallelMap", ID.ParallelMap);

  /**
   * ParametricPlot({function1, function2}, {t, tMin, tMax}) - generate a JavaScript control for the
   * parametric expressions `function1`, `function2` in the `t` range `{t, tMin, tMax}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ParametricPlot.md">ParametricPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ParametricPlot =
      S.initFinalSymbol("ParametricPlot", ID.ParametricPlot);

  /**
   * Parenthesis(expr) - print `expr` with parenthesis surrounded in output forms.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Parenthesis.md">Parenthesis
   *      documentation</a>
   */
  public final static IBuiltInSymbol Parenthesis = S.initFinalSymbol("Parenthesis", ID.Parenthesis);

  /**
   * ParetoDistribution(k,a) - returns a Pareto distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ParetoDistribution.md">ParetoDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol ParetoDistribution =
      S.initFinalSymbol("ParetoDistribution", ID.ParetoDistribution);

  /**
   * Part(expr, i) - returns part `i` of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Part.md">Part
   *      documentation</a>
   */
  public final static IBuiltInSymbol Part = S.initFinalSymbol("Part", ID.Part);

  /**
   * Partition(list, n) - partitions `list` into sublists of length `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Partition.md">Partition
   *      documentation</a>
   */
  public final static IBuiltInSymbol Partition = S.initFinalSymbol("Partition", ID.Partition);

  /**
   * PartitionsP(n) - gives the number of unrestricted partitions of the integer `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PartitionsP.md">PartitionsP
   *      documentation</a>
   */
  public final static IBuiltInSymbol PartitionsP = S.initFinalSymbol("PartitionsP", ID.PartitionsP);

  /**
   * PartitionsQ(n) - gives the number of partitions of the integer `n` into distinct parts
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PartitionsQ.md">PartitionsQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PartitionsQ = S.initFinalSymbol("PartitionsQ", ID.PartitionsQ);

  public final static IBuiltInSymbol ParzenWindow =
      S.initFinalSymbol("ParzenWindow", ID.ParzenWindow);

  public final static IBuiltInSymbol PathGraph = S.initFinalSymbol("PathGraph", ID.PathGraph);

  public final static IBuiltInSymbol PathGraphQ = S.initFinalSymbol("PathGraphQ", ID.PathGraphQ);

  public final static IBuiltInSymbol Pattern = S.initFinalSymbol("Pattern", ID.Pattern);

  public final static IBuiltInSymbol PatternOrder =
      S.initFinalSymbol("PatternOrder", ID.PatternOrder);

  /**
   * PatternTest(pattern, test) - constrains `pattern` to match `expr` only if the evaluation of
   * `test(expr)` yields `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PatternTest.md">PatternTest
   *      documentation</a>
   */
  public final static IBuiltInSymbol PatternTest = S.initFinalSymbol("PatternTest", ID.PatternTest);

  /**
   * PauliMatrix(n) - returns `n`th Pauli spin `2x2` matrix for `n` between `0` and `4`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PauliMatrix.md">PauliMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol PauliMatrix = S.initFinalSymbol("PauliMatrix", ID.PauliMatrix);

  /**
   * Pause(seconds) - pause the thread for the number of `seconds`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pause.md">Pause
   *      documentation</a>
   */
  public final static IBuiltInSymbol Pause = S.initFinalSymbol("Pause", ID.Pause);

  /**
   * PDF(distribution, value) - returns the probability density function of `value`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PDF.md">PDF
   *      documentation</a>
   */
  public final static IBuiltInSymbol PDF = S.initFinalSymbol("PDF", ID.PDF);

  public final static IBuiltInSymbol PearsonChiSquareTest =
      S.initFinalSymbol("PearsonChiSquareTest", ID.PearsonChiSquareTest);

  /**
   * PearsonCorrelationTest(real-vector1, real-vector2) - `"value"` can be `"TestStatistic"`,
   * `"TestData"` or `"PValue"`. In statistics, the Pearson correlation coefficient (PCC) is a
   * correlation coefficient that measures linear correlation between two sets of data.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PearsonCorrelationTest.md">PearsonCorrelationTest
   *      documentation</a>
   */
  public final static IBuiltInSymbol PearsonCorrelationTest =
      S.initFinalSymbol("PearsonCorrelationTest", ID.PearsonCorrelationTest);

  /**
   * PerfectNumber(n) - returns the `n`th perfect number. In number theory, a perfect number is a
   * positive integer that is equal to the sum of its proper
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PerfectNumber.md">PerfectNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol PerfectNumber =
      S.initFinalSymbol("PerfectNumber", ID.PerfectNumber);

  /**
   * PerfectNumberQ(n) - returns `True` if `n` is a perfect number. In number theory, a perfect
   * number is a positive integer that is equal to the sum of its proper
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PerfectNumberQ.md">PerfectNumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PerfectNumberQ =
      S.initFinalSymbol("PerfectNumberQ", ID.PerfectNumberQ);

  /**
   * Perimeter(geometric-form) - returns the perimeter of the `geometric-form`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Perimeter.md">Perimeter
   *      documentation</a>
   */
  public final static IBuiltInSymbol Perimeter = S.initFinalSymbol("Perimeter", ID.Perimeter);

  /**
   * PermutationCycles(permutation-list) - generate a `Cycles({{...},{...}, ...})` expression from
   * the `permutation-list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationCycles.md">PermutationCycles
   *      documentation</a>
   */
  public final static IBuiltInSymbol PermutationCycles =
      S.initFinalSymbol("PermutationCycles", ID.PermutationCycles);

  /**
   * PermutationCyclesQ(cyclesExpression) - if `cyclesExpression` is a valid `Cycles({{...},{...},
   * ...})` expression return `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationCyclesQ.md">PermutationCyclesQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PermutationCyclesQ =
      S.initFinalSymbol("PermutationCyclesQ", ID.PermutationCyclesQ);

  /**
   * PermutationList(Cycles({{...},{...}, ...})) - get the permutation list representation from the
   * `Cycles({{...},{...}, ...})` expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationList.md">PermutationList
   *      documentation</a>
   */
  public final static IBuiltInSymbol PermutationList =
      S.initFinalSymbol("PermutationList", ID.PermutationList);

  /**
   * PermutationListQ(permutation-list) - if `permutation-list` is a valid permutation list return
   * `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PermutationListQ.md">PermutationListQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PermutationListQ =
      S.initFinalSymbol("PermutationListQ", ID.PermutationListQ);

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
      S.initFinalSymbol("PermutationReplace", ID.PermutationReplace);

  /**
   * Permutations(list) - gives all possible orderings of the items in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Permutations.md">Permutations
   *      documentation</a>
   */
  public final static IBuiltInSymbol Permutations =
      S.initFinalSymbol("Permutations", ID.Permutations);

  /**
   * Permute(list, Cycles({permutationCycles})) - permutes the `list` from the cycles in
   * `permutationCycles`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Permute.md">Permute
   *      documentation</a>
   */
  public final static IBuiltInSymbol Permute = S.initFinalSymbol("Permute", ID.Permute);

  /**
   * PetersenGraph() - create a `PetersenGraph(5, 2)` graph.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PetersenGraph.md">PetersenGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol PetersenGraph =
      S.initFinalSymbol("PetersenGraph", ID.PetersenGraph);

  /**
   * Pi - is the constant `Pi`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pi.md">Pi
   *      documentation</a>
   */
  public final static IBuiltInSymbol Pi = S.initFinalSymbol("Pi", ID.Pi);

  /**
   * Pick(nestedList, nestedSelection) - returns the elements of `nestedList` that have value `True`
   * in the corresponding position in `nestedSelection`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pick.md">Pick
   *      documentation</a>
   */
  public final static IBuiltInSymbol Pick = S.initFinalSymbol("Pick", ID.Pick);

  /**
   * Piecewise({{expr1, cond1}, ...}) - represents a piecewise function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Piecewise.md">Piecewise
   *      documentation</a>
   */
  public final static IBuiltInSymbol Piecewise = S.initFinalSymbol("Piecewise", ID.Piecewise);

  /**
   * PiecewiseExpand(function) - expands piecewise expressions into a `Piecewise` function.
   * Currently only `Abs, Clip, If, Ramp, UnitStep` are converted to Piecewise expressions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PiecewiseExpand.md">PiecewiseExpand
   *      documentation</a>
   */
  public final static IBuiltInSymbol PiecewiseExpand =
      S.initFinalSymbol("PiecewiseExpand", ID.PiecewiseExpand);

  /**
   * PieChart(list-of-values) - plot a pie chart from a `list-of-values`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PieChart.md">PieChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol PieChart = S.initFinalSymbol("PieChart", ID.PieChart);

  public final static IBuiltInSymbol Pink = S.initFinalSymbol("Pink", ID.Pink);

  public final static IBuiltInSymbol PlanarGraph = S.initFinalSymbol("PlanarGraph", ID.PlanarGraph);

  /**
   * PlanarGraphQ(g) - Returns `True` if `g` is a planar graph and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PlanarGraphQ.md">PlanarGraphQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PlanarGraphQ =
      S.initFinalSymbol("PlanarGraphQ", ID.PlanarGraphQ);

  /**
   * Plot(function, {x, xMin, xMax}, PlotRange->{yMin,yMax}) - generate a JavaScript control for the
   * expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plot.md">Plot
   *      documentation</a>
   */
  public final static IBuiltInSymbol Plot = S.initFinalSymbol("Plot", ID.Plot);

  /**
   * Plot3D(function, {x, xMin, xMax}, {y,yMin,yMax}) - generate a JavaScript control for the
   * expression `function` in the `x` range `{x, xMin, xMax}` and `{yMin, yMax}` in the `y` range.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plot3D.md">Plot3D
   *      documentation</a>
   */
  public final static IBuiltInSymbol Plot3D = S.initFinalSymbol("Plot3D", ID.Plot3D);

  public final static IBuiltInSymbol PlotLabel = S.initFinalSymbol("PlotLabel", ID.PlotLabel);

  public final static IBuiltInSymbol PlotLegends = S.initFinalSymbol("PlotLegends", ID.PlotLegends);

  public final static IBuiltInSymbol PlotRange = S.initFinalSymbol("PlotRange", ID.PlotRange);

  public final static IBuiltInSymbol PlotStyle = S.initFinalSymbol("PlotStyle", ID.PlotStyle);

  /**
   * Plus(a, b, ...) - represents the sum of the terms `a, b, ...`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plus.md">Plus
   *      documentation</a>
   */
  public final static IBuiltInSymbol Plus = S.initFinalSymbol("Plus", ID.Plus);

  /**
   * PlusMinus(a, b, ...) - has no built-in evaluating function, but represents the structure of the
   * `±` operator.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PlusMinus.md">PlusMinus
   *      documentation</a>
   */
  public final static IBuiltInSymbol PlusMinus = S.initFinalSymbol("PlusMinus", ID.PlusMinus);

  /**
   * Pochhammer(a, n) - returns the pochhammer symbol for a rational number `a` and an integer
   * number `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pochhammer.md">Pochhammer
   *      documentation</a>
   */
  public final static IBuiltInSymbol Pochhammer = S.initFinalSymbol("Pochhammer", ID.Pochhammer);

  /**
   * Point({point_1, point_2 ...}) - represents the point primitive.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Point.md">Point
   *      documentation</a>
   */
  public final static IBuiltInSymbol Point = S.initFinalSymbol("Point", ID.Point);

  public final static IBuiltInSymbol PointLight = S.initFinalSymbol("PointLight", ID.PointLight);

  public final static IBuiltInSymbol PointSize = S.initFinalSymbol("PointSize", ID.PointSize);

  /**
   * PoissonDistribution(m) - returns a Poisson distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PoissonDistribution.md">PoissonDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol PoissonDistribution =
      S.initFinalSymbol("PoissonDistribution", ID.PoissonDistribution);

  public final static IBuiltInSymbol PoissonProcess =
      S.initFinalSymbol("PoissonProcess", ID.PoissonProcess);

  /**
   * PolarPlot(function, {t, tMin, tMax}) - generate a JavaScript control for the polar plot
   * expressions `function` in the `t` range `{t, tMin, tMax}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolarPlot.md">PolarPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolarPlot = S.initFinalSymbol("PolarPlot", ID.PolarPlot);

  /**
   * PolyGamma(value) - return the digamma function of the `value`. The digamma function is defined
   * as the logarithmic derivative of the gamma function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolyGamma.md">PolyGamma
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolyGamma = S.initFinalSymbol("PolyGamma", ID.PolyGamma);

  /**
   * Polygon({point_1, point_2 ...}) - represents the filled polygon primitive.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Polygon.md">Polygon
   *      documentation</a>
   */
  public final static IBuiltInSymbol Polygon = S.initFinalSymbol("Polygon", ID.Polygon);

  /**
   * PolygonalNumber(nPoints) - returns the triangular number for `nPoints`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolygonalNumber.md">PolygonalNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolygonalNumber =
      S.initFinalSymbol("PolygonalNumber", ID.PolygonalNumber);

  public final static IBuiltInSymbol Polyhedron = S.initFinalSymbol("Polyhedron", ID.Polyhedron);

  /**
   * PolyLog(s, z) - returns the polylogarithm function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolyLog.md">PolyLog
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolyLog = S.initFinalSymbol("PolyLog", ID.PolyLog);

  /**
   * PolynomialExtendedGCD(p, q, x) - returns the extended GCD ('greatest common divisor') of the
   * univariate polynomials `p` and `q`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialExtendedGCD.md">PolynomialExtendedGCD
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialExtendedGCD =
      S.initFinalSymbol("PolynomialExtendedGCD", ID.PolynomialExtendedGCD);

  /**
   * PolynomialGCD(p, q) - returns the GCD ('greatest common divisor') of the polynomials `p` and
   * `q`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialGCD.md">PolynomialGCD
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialGCD =
      S.initFinalSymbol("PolynomialGCD", ID.PolynomialGCD);

  /**
   * PolynomialLCM(p, q) - returns the LCM ('least common multiple') of the polynomials `p` and `q`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialLCM.md">PolynomialLCM
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialLCM =
      S.initFinalSymbol("PolynomialLCM", ID.PolynomialLCM);

  /**
   * PolynomialQ(p, x) - return `True` if `p` is a polynomial for the variable `x`. Return `False`
   * in all other cases.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQ.md">PolynomialQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialQ = S.initFinalSymbol("PolynomialQ", ID.PolynomialQ);

  /**
   * PolynomialQuotient(p, q, x) - returns the polynomial quotient of the polynomials `p` and `q`
   * for the variable `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQuotient.md">PolynomialQuotient
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialQuotient =
      S.initFinalSymbol("PolynomialQuotient", ID.PolynomialQuotient);

  /**
   * PolynomialQuotientRemainder(p, q, x) - returns a list with the polynomial quotient and
   * remainder of the polynomials `p` and `q` for the variable `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialQuotientRemainder.md">PolynomialQuotientRemainder
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialQuotientRemainder =
      S.initFinalSymbol("PolynomialQuotientRemainder", ID.PolynomialQuotientRemainder);

  /**
   * PolynomialQuotient(p, q, x) - returns the polynomial remainder of the polynomials `p` and `q`
   * for the variable `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialRemainder.md">PolynomialRemainder
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialRemainder =
      S.initFinalSymbol("PolynomialRemainder", ID.PolynomialRemainder);

  /**
   * Position(expr, patt) - returns the list of positions for which `expr` matches `patt`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Position.md">Position
   *      documentation</a>
   */
  public final static IBuiltInSymbol Position = S.initFinalSymbol("Position", ID.Position);

  /**
   * Positive(x) - returns `True` if `x` is a positive real number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Positive.md">Positive
   *      documentation</a>
   */
  public final static IBuiltInSymbol Positive = S.initFinalSymbol("Positive", ID.Positive);

  public final static IBuiltInSymbol PositiveIntegers =
      S.initFinalSymbol("PositiveIntegers", ID.PositiveIntegers);

  public final static IBuiltInSymbol PositiveRationals =
      S.initFinalSymbol("PositiveRationals", ID.PositiveRationals);

  public final static IBuiltInSymbol PositiveReals =
      S.initFinalSymbol("PositiveReals", ID.PositiveReals);

  /**
   * PossibleZeroQ(expr) - returns `True` if basic symbolic and numerical methods suggests that
   * `expr` has value zero, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PossibleZeroQ.md">PossibleZeroQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PossibleZeroQ =
      S.initFinalSymbol("PossibleZeroQ", ID.PossibleZeroQ);

  public final static IBuiltInSymbol Postfix = S.initFinalSymbol("Postfix", ID.Postfix);

  /**
   * Power(a, b) - represents `a` raised to the power of `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Power.md">Power
   *      documentation</a>
   */
  public final static IBuiltInSymbol Power = S.initFinalSymbol("Power", ID.Power);

  /**
   * PowerExpand(expr) - expands out powers of the form `(x^y)^z` and `(x*y)^z` in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PowerExpand.md">PowerExpand
   *      documentation</a>
   */
  public final static IBuiltInSymbol PowerExpand = S.initFinalSymbol("PowerExpand", ID.PowerExpand);

  /**
   * PowerMod(x, y, m) - computes `x^y` modulo `m`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PowerMod.md">PowerMod
   *      documentation</a>
   */
  public final static IBuiltInSymbol PowerMod = S.initFinalSymbol("PowerMod", ID.PowerMod);

  public final static IBuiltInSymbol PowersRepresentations =
      S.initFinalSymbol("PowersRepresentations", ID.PowersRepresentations);

  public final static IBuiltInSymbol PrecedenceForm =
      S.initFinalSymbol("PrecedenceForm", ID.PrecedenceForm);

  public final static IBuiltInSymbol Precision = S.initFinalSymbol("Precision", ID.Precision);

  public final static IBuiltInSymbol PrecisionGoal =
      S.initFinalSymbol("PrecisionGoal", ID.PrecisionGoal);

  /**
   * PreDecrement(x) - decrements `x` by `1`, returning the new value of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PreDecrement.md">PreDecrement
   *      documentation</a>
   */
  public final static IBuiltInSymbol PreDecrement =
      S.initFinalSymbol("PreDecrement", ID.PreDecrement);

  public final static IBuiltInSymbol Prefix = S.initFinalSymbol("Prefix", ID.Prefix);

  /**
   * PreIncrement(x) - increments `x` by `1`, returning the new value of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PreIncrement.md">PreIncrement
   *      documentation</a>
   */
  public final static IBuiltInSymbol PreIncrement =
      S.initFinalSymbol("PreIncrement", ID.PreIncrement);

  /**
   * Prepend(expr, item) - returns `expr` with `item` prepended to its leaves.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Prepend.md">Prepend
   *      documentation</a>
   */
  public final static IBuiltInSymbol Prepend = S.initFinalSymbol("Prepend", ID.Prepend);

  /**
   * PrependTo(s, item) - prepend `item` to value of `s` and sets `s` to the result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrependTo.md">PrependTo
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrependTo = S.initFinalSymbol("PrependTo", ID.PrependTo);

  /**
   * Prime(n) - returns the `n`th prime number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Prime.md">Prime
   *      documentation</a>
   */
  public final static IBuiltInSymbol Prime = S.initFinalSymbol("Prime", ID.Prime);

  /**
   * PrimeOmega(n) - returns the sum of the exponents of the prime factorization of `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimeOmega.md">PrimeOmega
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrimeOmega = S.initFinalSymbol("PrimeOmega", ID.PrimeOmega);

  /**
   * PrimePi(x) - gives the number of primes less than or equal to `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimePi.md">PrimePi
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrimePi = S.initFinalSymbol("PrimePi", ID.PrimePi);

  /**
   * PrimePowerQ(n) - returns `True` if `n` is a power of a prime number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimePowerQ.md">PrimePowerQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrimePowerQ = S.initFinalSymbol("PrimePowerQ", ID.PrimePowerQ);

  /**
   * PrimeQ(n) - returns `True` if `n` is a integer prime number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimeQ.md">PrimeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrimeQ = S.initFinalSymbol("PrimeQ", ID.PrimeQ);

  public final static IBuiltInSymbol Primes = S.initFinalSymbol("Primes", ID.Primes);

  public final static IBuiltInSymbol PrimitiveRoot =
      S.initFinalSymbol("PrimitiveRoot", ID.PrimitiveRoot);

  /**
   * PrimitiveRootList(n) - returns the list of the primitive roots of `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimitiveRootList.md">PrimitiveRootList
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrimitiveRootList =
      S.initFinalSymbol("PrimitiveRootList", ID.PrimitiveRootList);

  public final static IBuiltInSymbol PrincipalComponents =
      S.initFinalSymbol("PrincipalComponents", ID.PrincipalComponents);

  /**
   * Print(expr) - print the `expr` to the default output stream and return `Null`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Print.md">Print
   *      documentation</a>
   */
  public final static IBuiltInSymbol Print = S.initFinalSymbol("Print", ID.Print);

  /**
   * PrintableASCIIQ(str) - returns `True` if all characters in `str` are ASCII characters.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrintableASCIIQ.md">PrintableASCIIQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrintableASCIIQ =
      S.initFinalSymbol("PrintableASCIIQ", ID.PrintableASCIIQ);

  public final static IBuiltInSymbol Prism = S.initFinalSymbol("Prism", ID.Prism);

  /**
   * Probability(pure-function, data-set) - returns the probability of the `pure-function` for the
   * given `data-set`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Probability.md">Probability
   *      documentation</a>
   */
  public final static IBuiltInSymbol Probability = S.initFinalSymbol("Probability", ID.Probability);

  /**
   * Product(expr, {i, imin, imax}) - evaluates the discrete product of `expr` with `i` ranging from
   * `imin` to `imax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Product.md">Product
   *      documentation</a>
   */
  public final static IBuiltInSymbol Product = S.initFinalSymbol("Product", ID.Product);

  /**
   * ProductLog(z) - returns the value of the Lambert W function at `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ProductLog.md">ProductLog
   *      documentation</a>
   */
  public final static IBuiltInSymbol ProductLog = S.initFinalSymbol("ProductLog", ID.ProductLog);

  /**
   * Projection(vector1, vector2) - Find the orthogonal projection of `vector1` onto another
   * `vector2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Projection.md">Projection
   *      documentation</a>
   */
  public final static IBuiltInSymbol Projection = S.initFinalSymbol("Projection", ID.Projection);

  public final static IBuiltInSymbol Protect = S.initFinalSymbol("Protect", ID.Protect);

  public final static IBuiltInSymbol Protected = S.initFinalSymbol("Protected", ID.Protected);

  /**
   * PseudoInverse(matrix) - computes the Moore-Penrose pseudoinverse of the `matrix`. If `matrix`
   * is invertible, the pseudoinverse equals the inverse.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PseudoInverse.md">PseudoInverse
   *      documentation</a>
   */
  public final static IBuiltInSymbol PseudoInverse =
      S.initFinalSymbol("PseudoInverse", ID.PseudoInverse);

  public final static IBuiltInSymbol Purple = S.initFinalSymbol("Purple", ID.Purple);

  public final static IBuiltInSymbol Put = S.initFinalSymbol("Put", ID.Put);

  public final static IBuiltInSymbol Pyramid = S.initFinalSymbol("Pyramid", ID.Pyramid);

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
      S.initFinalSymbol("QRDecomposition", ID.QRDecomposition);

  /**
   * QuadraticIrrationalQ(expr) - returns `True`, if the `expr` is of the form `(p + s * Sqrt(d)) /
   * q` for integers `p,q,d,s`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuadraticIrrationalQ.md">QuadraticIrrationalQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuadraticIrrationalQ =
      S.initFinalSymbol("QuadraticIrrationalQ", ID.QuadraticIrrationalQ);

  /**
   * Quantile(list, q) - returns the `q`-Quantile of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantile.md">Quantile
   *      documentation</a>
   */
  public final static IBuiltInSymbol Quantile = S.initFinalSymbol("Quantile", ID.Quantile);

  /**
   * Quantity(value, unit) - returns the quantity for `value` and `unit`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantity.md">Quantity
   *      documentation</a>
   */
  public final static IBuiltInSymbol Quantity = S.initFinalSymbol("Quantity", ID.Quantity);

  public final static IBuiltInSymbol QuantityDistribution =
      S.initFinalSymbol("QuantityDistribution", ID.QuantityDistribution);

  /**
   * QuantityMagnitude(quantity) - returns the value of the `quantity`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityMagnitude.md">QuantityMagnitude
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuantityMagnitude =
      S.initFinalSymbol("QuantityMagnitude", ID.QuantityMagnitude);

  public final static IBuiltInSymbol QuantityQ = S.initFinalSymbol("QuantityQ", ID.QuantityQ);

  public final static IBuiltInSymbol QuantityUnit =
      S.initFinalSymbol("QuantityUnit", ID.QuantityUnit);

  public final static IBuiltInSymbol QuarticSolve =
      S.initFinalSymbol("QuarticSolve", ID.QuarticSolve);

  /**
   * Quartiles(arg) - returns a list of the `1/4`, `1/2` and `3/4` quantile of `arg`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quartiles.md">Quartiles
   *      documentation</a>
   */
  public final static IBuiltInSymbol Quartiles = S.initFinalSymbol("Quartiles", ID.Quartiles);

  /**
   * Quiet(expr) - evaluates `expr` in "quiet" mode (i.e. no warning messages are shown during
   * evaluation).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quiet.md">Quiet
   *      documentation</a>
   */
  public final static IBuiltInSymbol Quiet = S.initFinalSymbol("Quiet", ID.Quiet);

  public final static IBuiltInSymbol Quit = S.initFinalSymbol("Quit", ID.Quit);

  /**
   * Quotient(m, n) - computes the integer quotient of `m` and `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quotient.md">Quotient
   *      documentation</a>
   */
  public final static IBuiltInSymbol Quotient = S.initFinalSymbol("Quotient", ID.Quotient);

  /**
   * QuotientRemainder(m, n) - computes a list of the quotient and remainder from division of `m`
   * and `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuotientRemainder.md">QuotientRemainder
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuotientRemainder =
      S.initFinalSymbol("QuotientRemainder", ID.QuotientRemainder);

  public final static IBuiltInSymbol RadicalBox = S.initFinalSymbol("RadicalBox", ID.RadicalBox);

  /**
   * Ramp(z) - The `Ramp` function is a unary real function, whose graph is shaped like a ramp.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ramp.md">Ramp
   *      documentation</a>
   */
  public final static IBuiltInSymbol Ramp = S.initFinalSymbol("Ramp", ID.Ramp);

  /**
   * RamseyNumber(r, s) - returns the Ramsey number `R(r,s)`. Currently not all values are known for
   * `1 <= r <= 4`. The function returns unevaluated if the value is unknown.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RamseyNumber.md">RamseyNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol RamseyNumber =
      S.initFinalSymbol("RamseyNumber", ID.RamseyNumber);

  public final static IBuiltInSymbol Random = S.initFinalSymbol("Random", ID.Random);

  /**
   * RandomChoice({item1, item2, item3,...}) - randomly picks one `item` from items.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomChoice.md">RandomChoice
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomChoice =
      S.initFinalSymbol("RandomChoice", ID.RandomChoice);

  /**
   * RandomComplex[{z_min, z_max}] - yields a pseudo-random complex number in the rectangle with
   * complex corners `z_min` and `z_max`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomComplex.md">RandomComplex
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomComplex =
      S.initFinalSymbol("RandomComplex", ID.RandomComplex);

  /**
   * RandomGraph({number-of-vertices,number-of-edges}) - create a random graph with
   * `number-of-vertices` vertices and `number-of-edges` edges.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomGraph.md">RandomGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomGraph = S.initFinalSymbol("RandomGraph", ID.RandomGraph);

  /**
   * RandomInteger(n) - create a random integer number between `0` and `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomInteger.md">RandomInteger
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomInteger =
      S.initFinalSymbol("RandomInteger", ID.RandomInteger);

  /**
   * RandomPermutation(s) - create a pseudo random permutation between `1` and `s`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomPermutation.md">RandomPermutation
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomPermutation =
      S.initFinalSymbol("RandomPermutation", ID.RandomPermutation);

  /**
   * RandomPrime({imin, imax}) - create a random prime integer number between `imin` and `imax`
   * inclusive.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomPrime.md">RandomPrime
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomPrime = S.initFinalSymbol("RandomPrime", ID.RandomPrime);

  /**
   * RandomReal() - create a random number between `0.0` and `1.0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomReal.md">RandomReal
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomReal = S.initFinalSymbol("RandomReal", ID.RandomReal);

  /**
   * RandomSample(items) - create a random sample for the arguments of the `items`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomSample.md">RandomSample
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomSample =
      S.initFinalSymbol("RandomSample", ID.RandomSample);

  /**
   * RandomVariate(distribution) - create a pseudo random variate from the `distribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RandomVariate.md">RandomVariate
   *      documentation</a>
   */
  public final static IBuiltInSymbol RandomVariate =
      S.initFinalSymbol("RandomVariate", ID.RandomVariate);

  /**
   * Range(n) - returns a list of integers from `1` to `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Range.md">Range
   *      documentation</a>
   */
  public final static IBuiltInSymbol Range = S.initFinalSymbol("Range", ID.Range);

  /**
   * RankedMax({e_1, e_2, ..., e_i}, n) - returns the n-th largest real value in the list `{e_1,
   * e_2, ..., e_i}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RankedMax.md">RankedMax
   *      documentation</a>
   */
  public final static IBuiltInSymbol RankedMax = S.initFinalSymbol("RankedMax", ID.RankedMax);

  /**
   * RankedMin({e_1, e_2, ..., e_i}, n) - returns the n-th smallest real value in the list `{e_1,
   * e_2, ..., e_i}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RankedMin.md">RankedMin
   *      documentation</a>
   */
  public final static IBuiltInSymbol RankedMin = S.initFinalSymbol("RankedMin", ID.RankedMin);

  /**
   * Rational - is the head of rational numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rational.md">Rational
   *      documentation</a>
   */
  public final static IBuiltInSymbol Rational = S.initFinalSymbol("Rational", ID.Rational);

  /**
   * Rationalize(expression) - convert numerical real or imaginary parts in (sub-)expressions into
   * rational numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rationalize.md">Rationalize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Rationalize = S.initFinalSymbol("Rationalize", ID.Rationalize);

  public final static IBuiltInSymbol Rationals = S.initFinalSymbol("Rationals", ID.Rationals);

  public final static IBuiltInSymbol RawBoxes = S.initFinalSymbol("RawBoxes", ID.RawBoxes);

  /**
   * Re(z) - returns the real component of the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Re.md">Re
   *      documentation</a>
   */
  public final static IBuiltInSymbol Re = S.initFinalSymbol("Re", ID.Re);

  /**
   * Read(input-stream) - reads the `input-stream` and return one expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Read.md">Read
   *      documentation</a>
   */
  public final static IBuiltInSymbol Read = S.initFinalSymbol("Read", ID.Read);

  public final static IBuiltInSymbol ReadList = S.initFinalSymbol("ReadList", ID.ReadList);

  public final static IBuiltInSymbol ReadProtected =
      S.initFinalSymbol("ReadProtected", ID.ReadProtected);

  public final static IBuiltInSymbol ReadString = S.initFinalSymbol("ReadString", ID.ReadString);

  /**
   * Real - is the head of real (floating point) numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Real.md">Real
   *      documentation</a>
   */
  public final static IBuiltInSymbol Real = S.initFinalSymbol("Real", ID.Real);

  /**
   * RealAbs(x) - returns the absolute value of the real number `x`. For complex number arguments
   * the function will be left unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RealAbs.md">RealAbs
   *      documentation</a>
   */
  public final static IBuiltInSymbol RealAbs = S.initFinalSymbol("RealAbs", ID.RealAbs);

  public final static IBuiltInSymbol RealDigits = S.initFinalSymbol("RealDigits", ID.RealDigits);

  /**
   * Reals - is the set of real numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reals.md">Reals
   *      documentation</a>
   */
  public final static IBuiltInSymbol Reals = S.initFinalSymbol("Reals", ID.Reals);

  /**
   * RealSign(x) - gives `-1`, `0` or `1` depending on whether `x` is negative, zero or positive.
   * For complex number arguments the function will be left unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RealSign.md">RealSign
   *      documentation</a>
   */
  public final static IBuiltInSymbol RealSign = S.initFinalSymbol("RealSign", ID.RealSign);

  /**
   * RealValuedNumberQ(expr) - returns `True` if `expr` is an explicit real number with no imaginary
   * component.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RealValuedNumberQ.md">RealValuedNumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol RealValuedNumberQ =
      S.initFinalSymbol("RealValuedNumberQ", ID.RealValuedNumberQ);

  public final static IBuiltInSymbol RealValuedNumericQ =
      S.initFinalSymbol("RealValuedNumericQ", ID.RealValuedNumericQ);

  /**
   * Reap(expr) - gives the result of evaluating `expr`, together with all values sown during this
   * evaluation. Values sown with different tags are given in different lists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reap.md">Reap
   *      documentation</a>
   */
  public final static IBuiltInSymbol Reap = S.initFinalSymbol("Reap", ID.Reap);

  public final static IBuiltInSymbol Record = S.initFinalSymbol("Record", ID.Record);

  public final static IBuiltInSymbol RecordSeparators =
      S.initFinalSymbol("RecordSeparators", ID.RecordSeparators);

  public final static IBuiltInSymbol Rectangle = S.initFinalSymbol("Rectangle", ID.Rectangle);

  public final static IBuiltInSymbol Red = S.initFinalSymbol("Red", ID.Red);

  /**
   * Reduce(logic-expression, var) - returns the reduced `logic-expression` for the variable `var`.
   * Reduce works only for the `Reals` domain.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reduce.md">Reduce
   *      documentation</a>
   */
  public final static IBuiltInSymbol Reduce = S.initFinalSymbol("Reduce", ID.Reduce);

  /**
   * Refine(expression, assumptions) - evaluate the `expression` for the given `assumptions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Refine.md">Refine
   *      documentation</a>
   */
  public final static IBuiltInSymbol Refine = S.initFinalSymbol("Refine", ID.Refine);

  /**
   * RegularExpression("regex") - represents the regular expression specified by the string
   * `“regex”`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegularExpression.md">RegularExpression
   *      documentation</a>
   */
  public final static IBuiltInSymbol RegularExpression =
      S.initFinalSymbol("RegularExpression", ID.RegularExpression);

  /**
   * ReIm(z) - returns a list of the real and imaginary component of the complex number `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReIm.md">ReIm
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReIm = S.initFinalSymbol("ReIm", ID.ReIm);

  /**
   * ReleaseHold(expr) - removes any `Hold`, `HoldForm`, `HoldPattern` or `HoldComplete` head from
   * `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReleaseHold.md">ReleaseHold
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReleaseHold = S.initFinalSymbol("ReleaseHold", ID.ReleaseHold);

  public final static IBuiltInSymbol Remove = S.initFinalSymbol("Remove", ID.Remove);

  /**
   * RemoveDiacritics("string") - returns a version of `string` with all diacritics removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RemoveDiacritics.md">RemoveDiacritics
   *      documentation</a>
   */
  public final static IBuiltInSymbol RemoveDiacritics =
      S.initFinalSymbol("RemoveDiacritics", ID.RemoveDiacritics);

  public final static IBuiltInSymbol Repeated = S.initFinalSymbol("Repeated", ID.Repeated);

  public final static IBuiltInSymbol RepeatedNull =
      S.initFinalSymbol("RepeatedNull", ID.RepeatedNull);

  /**
   * RepeatedTiming(x) - returns a list with the first entry containing the average evaluation time
   * of `x` and the second entry containing the evaluation result of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RepeatedTiming.md">RepeatedTiming
   *      documentation</a>
   */
  public final static IBuiltInSymbol RepeatedTiming =
      S.initFinalSymbol("RepeatedTiming", ID.RepeatedTiming);

  /**
   * Replace(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr` with
   * the right-hand-side `rhs`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Replace.md">Replace
   *      documentation</a>
   */
  public final static IBuiltInSymbol Replace = S.initFinalSymbol("Replace", ID.Replace);

  public final static IBuiltInSymbol ReplaceAt = S.initFinalSymbol("ReplaceAt", ID.ReplaceAt);

  /**
   * ReplaceAll(expr, i -> new) - replaces all `i` in `expr` with `new`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceAll.md">ReplaceAll
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReplaceAll = S.initFinalSymbol("ReplaceAll", ID.ReplaceAll);

  /**
   * ReplaceList(expr, lhs -> rhs) - replaces the left-hand-side pattern expression `lhs` in `expr`
   * with the right-hand-side `rhs`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceList.md">ReplaceList
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReplaceList = S.initFinalSymbol("ReplaceList", ID.ReplaceList);

  /**
   * ReplacePart(expr, i -> new) - replaces part `i` in `expr` with `new`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplacePart.md">ReplacePart
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReplacePart = S.initFinalSymbol("ReplacePart", ID.ReplacePart);

  /**
   * ReplaceRepeated(expr, lhs -> rhs) - repeatedly applies the rule `lhs -> rhs` to `expr` until
   * the result no longer changes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceRepeated.md">ReplaceRepeated
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReplaceRepeated =
      S.initFinalSymbol("ReplaceRepeated", ID.ReplaceRepeated);

  /**
   * Rescale(list) - returns `Rescale(list,{Min(list), Max(list)})`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rescale.md">Rescale
   *      documentation</a>
   */
  public final static IBuiltInSymbol Rescale = S.initFinalSymbol("Rescale", ID.Rescale);

  /**
   * Rest(expr) - returns `expr` with the first element removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rest.md">Rest
   *      documentation</a>
   */
  public final static IBuiltInSymbol Rest = S.initFinalSymbol("Rest", ID.Rest);

  /**
   * Resultant(polynomial1, polynomial2, var) - computes the resultant of the polynomials
   * `polynomial1` and `polynomial2` with respect to the variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Resultant.md">Resultant
   *      documentation</a>
   */
  public final static IBuiltInSymbol Resultant = S.initFinalSymbol("Resultant", ID.Resultant);

  /**
   * Return(expr) - aborts a function call and returns `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Return.md">Return
   *      documentation</a>
   */
  public final static IBuiltInSymbol Return = S.initFinalSymbol("Return", ID.Return);

  /**
   * Reverse(list) - reverse the elements of the `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Reverse.md">Reverse
   *      documentation</a>
   */
  public final static IBuiltInSymbol Reverse = S.initFinalSymbol("Reverse", ID.Reverse);

  public final static IBuiltInSymbol ReverseSort = S.initFinalSymbol("ReverseSort", ID.ReverseSort);

  public final static IBuiltInSymbol RGBColor = S.initFinalSymbol("RGBColor", ID.RGBColor);

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
      S.initFinalSymbol("RiccatiSolve", ID.RiccatiSolve);

  /**
   * Riffle(list1, list2) - insert elements of `list2` between the elements of `list1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Riffle.md">Riffle
   *      documentation</a>
   */
  public final static IBuiltInSymbol Riffle = S.initFinalSymbol("Riffle", ID.Riffle);

  public final static IBuiltInSymbol Right = S.initFinalSymbol("Right", ID.Right);

  /**
   * RightComposition(sym1, sym2,...)[arg1, arg2,...] - creates a composition of the symbols applied
   * in reversed order at the arguments.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RightComposition.md">RightComposition
   *      documentation</a>
   */
  public final static IBuiltInSymbol RightComposition =
      S.initFinalSymbol("RightComposition", ID.RightComposition);

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
      S.initFinalSymbol("RogersTanimotoDissimilarity", ID.RogersTanimotoDissimilarity);

  /**
   * RomanNumeral(positive-int-value) - converts the given `positive-int-value` to a roman numeral
   * string.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RomanNumeral.md">RomanNumeral
   *      documentation</a>
   */
  public final static IBuiltInSymbol RomanNumeral =
      S.initFinalSymbol("RomanNumeral", ID.RomanNumeral);

  public final static IBuiltInSymbol Root = S.initFinalSymbol("Root", ID.Root);

  public final static IBuiltInSymbol RootIntervals =
      S.initFinalSymbol("RootIntervals", ID.RootIntervals);

  public final static IBuiltInSymbol RootOf = S.initFinalSymbol("RootOf", ID.RootOf);

  public final static IBuiltInSymbol RootReduce = S.initFinalSymbol("RootReduce", ID.RootReduce);

  /**
   * Roots(polynomial-equation, var) - determine the roots of a univariate polynomial equation with
   * respect to the variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Roots.md">Roots
   *      documentation</a>
   */
  public final static IBuiltInSymbol Roots = S.initFinalSymbol("Roots", ID.Roots);

  /**
   * RotateLeft(list) - rotates the items of `list` by one item to the left.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotateLeft.md">RotateLeft
   *      documentation</a>
   */
  public final static IBuiltInSymbol RotateLeft = S.initFinalSymbol("RotateLeft", ID.RotateLeft);

  /**
   * RotateRight(list) - rotates the items of `list` by one item to the right.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotateRight.md">RotateRight
   *      documentation</a>
   */
  public final static IBuiltInSymbol RotateRight = S.initFinalSymbol("RotateRight", ID.RotateRight);

  /**
   * RotationMatrix(theta) - yields a rotation matrix for the angle `theta`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotationMatrix.md">RotationMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol RotationMatrix =
      S.initFinalSymbol("RotationMatrix", ID.RotationMatrix);

  /**
   * RotationTransform(phi) - gives a rotation by `phi`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotationTransform.md">RotationTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol RotationTransform =
      S.initFinalSymbol("RotationTransform", ID.RotationTransform);

  /**
   * Round(expr) - round a given `expr` to nearest integer.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Round.md">Round
   *      documentation</a>
   */
  public final static IBuiltInSymbol Round = S.initFinalSymbol("Round", ID.Round);

  public final static IBuiltInSymbol Row = S.initFinalSymbol("Row", ID.Row);

  public final static IBuiltInSymbol RowBox = S.initFinalSymbol("RowBox", ID.RowBox);

  /**
   * RowReduce(matrix) - returns the reduced row-echelon form of `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RowReduce.md">RowReduce
   *      documentation</a>
   */
  public final static IBuiltInSymbol RowReduce = S.initFinalSymbol("RowReduce", ID.RowReduce);

  public final static IBuiltInSymbol RSolve = S.initFinalSymbol("RSolve", ID.RSolve);

  public final static IBuiltInSymbol RSolveValue = S.initFinalSymbol("RSolveValue", ID.RSolveValue);

  /**
   * Rule(x, y) - represents a rule replacing `x` with `y`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rule.md">Rule
   *      documentation</a>
   */
  public final static IBuiltInSymbol Rule = S.initFinalSymbol("Rule", ID.Rule);

  /**
   * RuleDelayed(x, y) - represents a rule replacing `x` with `y`, with `y` held unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RuleDelayed.md">RuleDelayed
   *      documentation</a>
   */
  public final static IBuiltInSymbol RuleDelayed = S.initFinalSymbol("RuleDelayed", ID.RuleDelayed);

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
      S.initFinalSymbol("RussellRaoDissimilarity", ID.RussellRaoDissimilarity);

  /**
   * SameObjectQ[java-object1, java-object2] - gives `True` if the Java `==` operator for the Java
   * objects gives true. `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SameObjectQ.md">SameObjectQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SameObjectQ = S.initFinalSymbol("SameObjectQ", ID.SameObjectQ);

  /**
   * SameQ(x, y) - returns `True` if `x` and `y` are structurally identical.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SameQ.md">SameQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SameQ = S.initFinalSymbol("SameQ", ID.SameQ);

  public final static IBuiltInSymbol SameTest = S.initFinalSymbol("SameTest", ID.SameTest);

  /**
   * SASTriangle(a, gamma, b) - returns a triangle from 2 sides `a`, `b` and angle `gamma` (which is
   * between the sides).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SASTriangle.md">SASTriangle
   *      documentation</a>
   */
  public final static IBuiltInSymbol SASTriangle = S.initFinalSymbol("SASTriangle", ID.SASTriangle);

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
      S.initFinalSymbol("SatisfiabilityCount", ID.SatisfiabilityCount);

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
      S.initFinalSymbol("SatisfiabilityInstances", ID.SatisfiabilityInstances);

  /**
   * SatisfiableQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable
   * by a combination of boolean `False` and `True` values for the `list-of-variables`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SatisfiableQ.md">SatisfiableQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SatisfiableQ =
      S.initFinalSymbol("SatisfiableQ", ID.SatisfiableQ);

  public final static IBuiltInSymbol Save = S.initFinalSymbol("Save", ID.Save);

  /**
   * SawtoothWave(expr) - returns the sawtooth wave value of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SawtoothWave.md">SawtoothWave
   *      documentation</a>
   */
  public final static IBuiltInSymbol SawtoothWave =
      S.initFinalSymbol("SawtoothWave", ID.SawtoothWave);

  public final static IBuiltInSymbol Scale = S.initFinalSymbol("Scale", ID.Scale);

  public final static IBuiltInSymbol Scaled = S.initFinalSymbol("Scaled", ID.Scaled);

  public final static IBuiltInSymbol ScalingFunctions =
      S.initFinalSymbol("ScalingFunctions", ID.ScalingFunctions);

  /**
   * ScalingTransform(v) - gives a scaling transform of `v`. `v` may be a scalar or a vector.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ScalingTransform.md">ScalingTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol ScalingTransform =
      S.initFinalSymbol("ScalingTransform", ID.ScalingTransform);

  /**
   * Scan(f, expr) - applies `f` to each element of `expr` and returns `Null`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Scan.md">Scan
   *      documentation</a>
   */
  public final static IBuiltInSymbol Scan = S.initFinalSymbol("Scan", ID.Scan);

  /**
   * SchurDecomposition(matrix) - calculate the Schur-decomposition as a list `{q, t}` of a square
   * `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SchurDecomposition.md">SchurDecomposition
   *      documentation</a>
   */
  public final static IBuiltInSymbol SchurDecomposition =
      S.initFinalSymbol("SchurDecomposition", ID.SchurDecomposition);

  public final static IBuiltInSymbol ScientificForm =
      S.initFinalSymbol("ScientificForm", ID.ScientificForm);

  /**
   * Sec(z) - returns the secant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sec.md">Sec
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sec = S.initFinalSymbol("Sec", ID.Sec);

  /**
   * Sech(z) - returns the hyperbolic secant of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sech.md">Sech
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sech = S.initFinalSymbol("Sech", ID.Sech);

  public final static IBuiltInSymbol Second = S.initFinalSymbol("Second", ID.Second);

  public final static IBuiltInSymbol SeedRandom = S.initFinalSymbol("SeedRandom", ID.SeedRandom);

  /**
   * Select({e1, e2, ...}, head) - returns a list of the elements `ei` for which `head(ei)` returns
   * `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Select.md">Select
   *      documentation</a>
   */
  public final static IBuiltInSymbol Select = S.initFinalSymbol("Select", ID.Select);

  /**
   * SelectFirst({e1, e2, ...}, f) - returns the first of the elements `ei` for which `f(ei)`
   * returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SelectFirst.md">SelectFirst
   *      documentation</a>
   */
  public final static IBuiltInSymbol SelectFirst = S.initFinalSymbol("SelectFirst", ID.SelectFirst);

  /**
   * SemanticImport("path-to-filename") - if the file system is enabled, import the data from CSV
   * files and do a semantic interpretation of the columns.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SemanticImport.md">SemanticImport
   *      documentation</a>
   */
  public final static IBuiltInSymbol SemanticImport =
      S.initFinalSymbol("SemanticImport", ID.SemanticImport);

  /**
   * SemanticImportString("string-content") - import the data from a content string in CSV format
   * and do a semantic interpretation of the columns.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SemanticImportString.md">SemanticImportString
   *      documentation</a>
   */
  public final static IBuiltInSymbol SemanticImportString =
      S.initFinalSymbol("SemanticImportString", ID.SemanticImportString);

  /**
   * Sequence[x1, x2, ...] - represents a sequence of arguments to a function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sequence.md">Sequence
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sequence = S.initFinalSymbol("Sequence", ID.Sequence);

  public final static IBuiltInSymbol SequenceCases =
      S.initFinalSymbol("SequenceCases", ID.SequenceCases);

  public final static IBuiltInSymbol SequenceHold =
      S.initFinalSymbol("SequenceHold", ID.SequenceHold);

  public final static IBuiltInSymbol SequenceReplace =
      S.initFinalSymbol("SequenceReplace", ID.SequenceReplace);

  public final static IBuiltInSymbol SequenceSplit =
      S.initFinalSymbol("SequenceSplit", ID.SequenceSplit);

  /**
   * Series(expr, {x, x0, n}) - create a power series of `expr` up to order `(x- x0)^n` at the point
   * `x = x0`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Series.md">Series
   *      documentation</a>
   */
  public final static IBuiltInSymbol Series = S.initFinalSymbol("Series", ID.Series);

  /**
   * SeriesCoefficient(expr, {x, x0, n}) - get the coefficient of `(x- x0)^n` at the point `x = x0`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SeriesCoefficient.md">SeriesCoefficient
   *      documentation</a>
   */
  public final static IBuiltInSymbol SeriesCoefficient =
      S.initFinalSymbol("SeriesCoefficient", ID.SeriesCoefficient);

  /**
   * SeriesData(x, x0, {coeff0, coeff1, coeff2,...}, nMin, nMax, denominator) - internal structure
   * of a power series at the point `x = x0` the `coeff_i` are coefficients of the power series.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SeriesData.md">SeriesData
   *      documentation</a>
   */
  public final static IBuiltInSymbol SeriesData = S.initFinalSymbol("SeriesData", ID.SeriesData);

  /**
   * Set(expr, value) - evaluates `value` and assigns it to `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Set.md">Set
   *      documentation</a>
   */
  public final static IBuiltInSymbol Set = S.initFinalSymbol("Set", ID.Set);

  /**
   * SetAttributes(symbol, attrib) - adds `attrib` to `symbol`'s attributes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SetAttributes.md">SetAttributes
   *      documentation</a>
   */
  public final static IBuiltInSymbol SetAttributes =
      S.initFinalSymbol("SetAttributes", ID.SetAttributes);

  /**
   * SetDelayed(expr, value) - assigns `value` to `expr`, without evaluating `value`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SetDelayed.md">SetDelayed
   *      documentation</a>
   */
  public final static IBuiltInSymbol SetDelayed = S.initFinalSymbol("SetDelayed", ID.SetDelayed);

  public final static IBuiltInSymbol SetSystemOptions =
      S.initFinalSymbol("SetSystemOptions", ID.SetSystemOptions);

  /**
   * Share(function) - replace internally equal common subexpressions in `function` by the same
   * reference to reduce memory consumption and return the number of times where `Share(function)`
   * could replace a common subexpression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Share.md">Share
   *      documentation</a>
   */
  public final static IBuiltInSymbol Share = S.initFinalSymbol("Share", ID.Share);

  /**
   * ShearingTransform(phi, {1, 0}, {0, 1}) - gives a horizontal shear by the angle `phi`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ShearingTransform.md">ShearingTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol ShearingTransform =
      S.initFinalSymbol("ShearingTransform", ID.ShearingTransform);

  public final static IBuiltInSymbol Short = S.initFinalSymbol("Short", ID.Short);

  public final static IBuiltInSymbol Shortest = S.initFinalSymbol("Shortest", ID.Shortest);

  public final static IBuiltInSymbol Show = S.initFinalSymbol("Show", ID.Show);

  /**
   * Sign(x) - gives `-1`, `0` or `1` depending on whether `x` is negative, zero or positive. For
   * complex numbers `Sign` is defined as `x/Abs(x)`, if x is nonzero.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sign.md">Sign
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sign = S.initFinalSymbol("Sign", ID.Sign);

  /**
   * Signature(permutation-list) - determine if the `permutation-list` has odd (`-1`) or even (`1`)
   * parity. Returns `0` if two elements in the `permutation-list` are equal.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Signature.md">Signature
   *      documentation</a>
   */
  public final static IBuiltInSymbol Signature = S.initFinalSymbol("Signature", ID.Signature);

  public final static IBuiltInSymbol SignCmp = S.initFinalSymbol("SignCmp", ID.SignCmp);

  public final static IBuiltInSymbol Simplex = S.initFinalSymbol("Simplex", ID.Simplex);

  /**
   * Simplify(expr) - simplifies `expr`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Simplify.md">Simplify
   *      documentation</a>
   */
  public final static IBuiltInSymbol Simplify = S.initFinalSymbol("Simplify", ID.Simplify);

  /**
   * Sin(expr) - returns the sine of `expr` (measured in radians).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sin.md">Sin
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sin = S.initFinalSymbol("Sin", ID.Sin);

  /**
   * Sinc(expr) - the sinc function `Sin(expr)/expr` for `expr != 0`. `Sinc(0)` returns `1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sinc.md">Sinc
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sinc = S.initFinalSymbol("Sinc", ID.Sinc);

  /**
   * SingularValueDecomposition(matrix) - calculates the singular value decomposition for the
   * `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SingularValueDecomposition.md">SingularValueDecomposition
   *      documentation</a>
   */
  public final static IBuiltInSymbol SingularValueDecomposition =
      S.initFinalSymbol("SingularValueDecomposition", ID.SingularValueDecomposition);

  public final static IBuiltInSymbol SingularValueList =
      S.initFinalSymbol("SingularValueList", ID.SingularValueList);

  /**
   * Sinh(z) - returns the hyperbolic sine of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sinh.md">Sinh
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sinh = S.initFinalSymbol("Sinh", ID.Sinh);

  /**
   * SinhIntegral(expr) - returns the sine integral of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SinhIntegral.md">SinhIntegral
   *      documentation</a>
   */
  public final static IBuiltInSymbol SinhIntegral =
      S.initFinalSymbol("SinhIntegral", ID.SinhIntegral);

  /**
   * SinIntegral(expr) - returns the hyperbolic sine integral of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SinIntegral.md">SinIntegral
   *      documentation</a>
   */
  public final static IBuiltInSymbol SinIntegral = S.initFinalSymbol("SinIntegral", ID.SinIntegral);

  /**
   * SixJSymbol({j1,j2,j3},{j4,j5,j6}) - get the 6-j symbol coefficients.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SixJSymbol.md">SixJSymbol
   *      documentation</a>
   */
  public final static IBuiltInSymbol SixJSymbol = S.initFinalSymbol("SixJSymbol", ID.SixJSymbol);

  /**
   * Skewness(list) - gives Pearson's moment coefficient of skewness for `list` (a measure for
   * estimating the symmetry of a distribution).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Skewness.md">Skewness
   *      documentation</a>
   */
  public final static IBuiltInSymbol Skewness = S.initFinalSymbol("Skewness", ID.Skewness);

  /**
   * # - is a short-hand for `#1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Slot.md">Slot
   *      documentation</a>
   */
  public final static IBuiltInSymbol Slot = S.initFinalSymbol("Slot", ID.Slot);

  public final static IBuiltInSymbol SlotAbsent = S.initFinalSymbol("SlotAbsent", ID.SlotAbsent);

  /**
   * ## - is the sequence of arguments supplied to a pure function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SlotSequence.md">SlotSequence
   *      documentation</a>
   */
  public final static IBuiltInSymbol SlotSequence =
      S.initFinalSymbol("SlotSequence", ID.SlotSequence);

  public final static IBuiltInSymbol Small = S.initFinalSymbol("Small", ID.Small);

  public final static IBuiltInSymbol SudokuSolve = S.initFinalSymbol("SudokuSolve", ID.SudokuSolve);

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
      S.initFinalSymbol("SokalSneathDissimilarity", ID.SokalSneathDissimilarity);

  /**
   * Solve(equations, vars) - attempts to solve `equations` for the variables `vars`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Solve.md">Solve
   *      documentation</a>
   */
  public final static IBuiltInSymbol Solve = S.initFinalSymbol("Solve", ID.Solve);

  /**
   * Sort(list) - sorts `list` (or the leaves of any other expression) according to canonical
   * ordering.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sort.md">Sort
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sort = S.initFinalSymbol("Sort", ID.Sort);

  /**
   * SortBy(list, f) - sorts `list` (or the elements of any other expression) according to canonical
   * ordering of the keys that are extracted from the `list`'s elements using `f`. Chunks of leaves
   * that appear the same under `f` are sorted according to their natural order (without applying
   * `f`).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SortBy.md">SortBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol SortBy = S.initFinalSymbol("SortBy", ID.SortBy);

  /**
   * Sow(expr) - sends the value `expr` to the innermost `Reap`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sow.md">Sow
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sow = S.initFinalSymbol("Sow", ID.Sow);

  /**
   * Span - is the head of span ranges like `1;;3`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Span.md">Span
   *      documentation</a>
   */
  public final static IBuiltInSymbol Span = S.initFinalSymbol("Span", ID.Span);

  /**
   * SparseArray(nestedList) - create a sparse array from a `nestedList` structure.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SparseArray.md">SparseArray
   *      documentation</a>
   */
  public final static IBuiltInSymbol SparseArray = S.initFinalSymbol("SparseArray", ID.SparseArray);

  public final static IBuiltInSymbol SparseArrayQ =
      S.initFinalSymbol("SparseArrayQ", ID.SparseArrayQ);

  public final static IBuiltInSymbol Specularity = S.initFinalSymbol("Specularity", ID.Specularity);

  /**
   * Sphere({x, y, z}) - is a sphere of radius `1` centered at the point `{x, y, z}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sphere.md">Sphere
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sphere = S.initFinalSymbol("Sphere", ID.Sphere);

  /**
   * SphericalBesselJ(n, z) - spherical Bessel function `J(n, x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SphericalBesselJ.md">SphericalBesselJ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SphericalBesselJ =
      S.initFinalSymbol("SphericalBesselJ", ID.SphericalBesselJ);

  /**
   * SphericalBesselY(n, z) - spherical Bessel function `Y(n, x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SphericalBesselY.md">SphericalBesselY
   *      documentation</a>
   */
  public final static IBuiltInSymbol SphericalBesselY =
      S.initFinalSymbol("SphericalBesselY", ID.SphericalBesselY);

  public final static IBuiltInSymbol SphericalHankelH1 =
      S.initFinalSymbol("SphericalHankelH1", ID.SphericalHankelH1);

  public final static IBuiltInSymbol SphericalHankelH2 =
      S.initFinalSymbol("SphericalHankelH2", ID.SphericalHankelH2);

  public final static IBuiltInSymbol SphericalHarmonicY =
      S.initFinalSymbol("SphericalHarmonicY", ID.SphericalHarmonicY);

  public final static IBuiltInSymbol Splice = S.initFinalSymbol("Splice", ID.Splice);

  /**
   * Split(list) - splits `list` into collections of consecutive identical elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Split.md">Split
   *      documentation</a>
   */
  public final static IBuiltInSymbol Split = S.initFinalSymbol("Split", ID.Split);

  /**
   * SplitBy(list, f) - splits `list` into collections of consecutive elements that give the same
   * result when `f` is applied.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SplitBy.md">SplitBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol SplitBy = S.initFinalSymbol("SplitBy", ID.SplitBy);

  public final static IBuiltInSymbol SpotLight = S.initFinalSymbol("SpotLight", ID.SpotLight);

  /**
   * Sqrt(expr) - returns the square root of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sqrt.md">Sqrt
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sqrt = S.initFinalSymbol("Sqrt", ID.Sqrt);

  public final static IBuiltInSymbol SqrtBox = S.initFinalSymbol("SqrtBox", ID.SqrtBox);

  /**
   * SquaredEuclideanDistance(u, v) - returns squared the euclidean distance between `u$` and `v`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquaredEuclideanDistance.md">SquaredEuclideanDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol SquaredEuclideanDistance =
      S.initFinalSymbol("SquaredEuclideanDistance", ID.SquaredEuclideanDistance);

  /**
   * SquareFreeQ(n) - returns `True` if `n` is a square free integer number or a square free
   * univariate polynomial.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquareFreeQ.md">SquareFreeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SquareFreeQ = S.initFinalSymbol("SquareFreeQ", ID.SquareFreeQ);

  /**
   * SquareMatrixQ(m) - returns `True` if `m` is a square matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquareMatrixQ.md">SquareMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SquareMatrixQ =
      S.initFinalSymbol("SquareMatrixQ", ID.SquareMatrixQ);

  public final static IBuiltInSymbol SquaresR = S.initFinalSymbol("SquaresR", ID.SquaresR);

  /**
   * SSSTriangle(a, b, c) - returns a triangle from 3 sides `a`, `b` and `c`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SSSTriangle.md">SSSTriangle
   *      documentation</a>
   */
  public final static IBuiltInSymbol SSSTriangle = S.initFinalSymbol("SSSTriangle", ID.SSSTriangle);

  /**
   * Stack( ) - return a list of the heads of the current stack wrapped by `HoldForm`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Stack.md">Stack
   *      documentation</a>
   */
  public final static IBuiltInSymbol Stack = S.initFinalSymbol("Stack", ID.Stack);

  /**
   * Stack(expr) - begine a new stack and evaluate `èxpr`. Use `Stack(_)` as a subexpression in
   * `expr` to return the stack elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StackBegin.md">StackBegin
   *      documentation</a>
   */
  public final static IBuiltInSymbol StackBegin = S.initFinalSymbol("StackBegin", ID.StackBegin);

  /**
   * StandardDeviation(list) - computes the standard deviation of `list`. `list` may consist of
   * numerical values or symbols. Numerical values may be real or complex.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardDeviation.md">StandardDeviation
   *      documentation</a>
   */
  public final static IBuiltInSymbol StandardDeviation =
      S.initFinalSymbol("StandardDeviation", ID.StandardDeviation);

  public final static IBuiltInSymbol StandardForm =
      S.initFinalSymbol("StandardForm", ID.StandardForm);

  /**
   * Standardize(list-of-values) - shifts the `list-of-values` by `Mean(list-of-values)`and scales
   * by `StandardDeviation(list-of-values)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Standardize.md">Standardize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Standardize = S.initFinalSymbol("Standardize", ID.Standardize);

  /**
   * StarGraph(order) - create a new star graph with `order` number of total vertices including the
   * center vertex.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StarGraph.md">StarGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol StarGraph = S.initFinalSymbol("StarGraph", ID.StarGraph);

  /**
   * StartOfLine - begine a new stack and evaluate `èxpr`. Use `Stack(_)` as a subexpression in
   * `expr` to return the stack elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StartOfLine.md">StartOfLine
   *      documentation</a>
   */
  public final static IBuiltInSymbol StartOfLine = S.initFinalSymbol("StartOfLine", ID.StartOfLine);

  /**
   * StartOfString - represents the start of a string.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StartOfString.md">StartOfString
   *      documentation</a>
   */
  public final static IBuiltInSymbol StartOfString =
      S.initFinalSymbol("StartOfString", ID.StartOfString);

  public final static IBuiltInSymbol StaticsVisible =
      S.initFinalSymbol("StaticsVisible", ID.StaticsVisible);

  /**
   * StieltjesGamma(a) - returns Stieltjes constant.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StieltjesGamma.md">StieltjesGamma
   *      documentation</a>
   */
  public final static IBuiltInSymbol StieltjesGamma =
      S.initFinalSymbol("StieltjesGamma", ID.StieltjesGamma);

  /**
   * StirlingS1(n, k) - returns the Stirling numbers of the first kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StirlingS1.md">StirlingS1
   *      documentation</a>
   */
  public final static IBuiltInSymbol StirlingS1 = S.initFinalSymbol("StirlingS1", ID.StirlingS1);

  /**
   * StirlingS2(n, k) - returns the Stirling numbers of the second kind. `StirlingS2(n,k)` is the
   * number of ways of partitioning an `n`-element set into `k` non-empty subsets.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StirlingS2.md">StirlingS2
   *      documentation</a>
   */
  public final static IBuiltInSymbol StirlingS2 = S.initFinalSymbol("StirlingS2", ID.StirlingS2);

  public final static IBuiltInSymbol StreamPlot = S.initFinalSymbol("StreamPlot", ID.StreamPlot);

  public final static IBuiltInSymbol Strict = S.initFinalSymbol("Strict", ID.Strict);

  /**
   * String - is the head of strings..
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/String.md">String
   *      documentation</a>
   */
  public final static IBuiltInSymbol String = S.initFinalSymbol("String", ID.String);

  /**
   * StringCases(string, pattern) - gives all occurences of `pattern` in `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringCases.md">StringCases
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringCases = S.initFinalSymbol("StringCases", ID.StringCases);

  /**
   * StringContainsQ(str1, str2) - return a list of matches for `"p1", "p2",...` list of strings in
   * the string `str`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringContainsQ.md">StringContainsQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringContainsQ =
      S.initFinalSymbol("StringContainsQ", ID.StringContainsQ);

  /**
   * StringCount(string, pattern) - counts all occurences of `pattern` in `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringCount.md">StringCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringCount = S.initFinalSymbol("StringCount", ID.StringCount);

  public final static IBuiltInSymbol StringDrop = S.initFinalSymbol("StringDrop", ID.StringDrop);

  /**
   * StringExpression(s_1, s_2, ...) - represents a sequence of strings and symbolic string objects
   * `s_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringExpression.md">StringExpression
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringExpression =
      S.initFinalSymbol("StringExpression", ID.StringExpression);

  public final static IBuiltInSymbol StringForm = S.initFinalSymbol("StringForm", ID.StringForm);

  public final static IBuiltInSymbol StringFormat =
      S.initFinalSymbol("StringFormat", ID.StringFormat);

  /**
   * StringFreeQ("string", patt) - returns `True` if no substring in `string` matches the string
   * expression `patt`, and returns `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringFreeQ.md">StringFreeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringFreeQ = S.initFinalSymbol("StringFreeQ", ID.StringFreeQ);

  /**
   * StringInsert(string, new-string, position) - returns a string with `new-string` inserted
   * starting at `position` in `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringInsert.md">StringInsert
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringInsert =
      S.initFinalSymbol("StringInsert", ID.StringInsert);

  /**
   * StringJoin(str1, str2, ... strN) - returns the concatenation of the strings `str1, str2, ...
   * strN`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringJoin.md">StringJoin
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringJoin = S.initFinalSymbol("StringJoin", ID.StringJoin);

  /**
   * StringLength(string) - gives the length of `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringLength.md">StringLength
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringLength =
      S.initFinalSymbol("StringLength", ID.StringLength);

  /**
   * StringMatchQ(string, regex-pattern) - check if the regular expression `regex-pattern` matches
   * the `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringMatchQ.md">StringMatchQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringMatchQ =
      S.initFinalSymbol("StringMatchQ", ID.StringMatchQ);

  /**
   * StringPart(str, pos) - return the character at position `pos` from the `str` string expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringPart.md">StringPart
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringPart = S.initFinalSymbol("StringPart", ID.StringPart);

  /**
   * StringPosition("string", patt) - gives a list of starting and ending positions where `patt`
   * matches `"string"`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringPosition.md">StringPosition
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringPosition =
      S.initFinalSymbol("StringPosition", ID.StringPosition);

  /**
   * StringQ(x) - is `True` if `x` is a string object, or `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringQ.md">StringQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringQ = S.initFinalSymbol("StringQ", ID.StringQ);

  public final static IBuiltInSymbol StringRepeat =
      S.initFinalSymbol("StringRepeat", ID.StringRepeat);

  /**
   * StringReplace(string, fromStr -> toStr) - replaces each occurrence of `fromStr` with `toStr` in
   * `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringReplace.md">StringReplace
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringReplace =
      S.initFinalSymbol("StringReplace", ID.StringReplace);

  /**
   * StringReplace(string) - reverse the `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringReverse.md">StringReverse
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringReverse =
      S.initFinalSymbol("StringReverse", ID.StringReverse);

  /**
   * StringRiffle({s1, s2, s3, ...}) - returns a new string by concatenating all the `si`, with
   * spaces inserted between them.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringRiffle.md">StringRiffle
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringRiffle =
      S.initFinalSymbol("StringRiffle", ID.StringRiffle);

  /**
   * StringSplit(str) - split the string `str` by whitespaces into a list of strings.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringSplit.md">StringSplit
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringSplit = S.initFinalSymbol("StringSplit", ID.StringSplit);

  /**
   * StringTake("string", n) - gives the first `n` characters in `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringTake.md">StringTake
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringTake = S.initFinalSymbol("StringTake", ID.StringTake);

  /**
   * StringTemplate(string) - gives a `StringTemplate` expression with name `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringTemplate.md">StringTemplate
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringTemplate =
      S.initFinalSymbol("StringTemplate", ID.StringTemplate);

  /**
   * StringToByteArray(string) - encodes the `string` into a sequence of bytes using the default
   * character set `UTF-8`, storing the result into into a `ByteArray`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringToByteArray.md">StringToByteArray
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringToByteArray =
      S.initFinalSymbol("StringToByteArray", ID.StringToByteArray);

  /**
   * StringToStream("string") - converts a `string` to an open input stream.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringToStream.md">StringToStream
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringToStream =
      S.initFinalSymbol("StringToStream", ID.StringToStream);

  /**
   * StringTrim(s) - returns a version of `s `with whitespace removed from start and end.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StringTrim.md">StringTrim
   *      documentation</a>
   */
  public final static IBuiltInSymbol StringTrim = S.initFinalSymbol("StringTrim", ID.StringTrim);

  public final static IBuiltInSymbol Structure = S.initFinalSymbol("Structure", ID.Structure);

  /**
   * StruveH(n, z) - returns the Struve function `H_n(z)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StruveH.md">StruveH
   *      documentation</a>
   */
  public final static IBuiltInSymbol StruveH = S.initFinalSymbol("StruveH", ID.StruveH);

  /**
   * StruveL(n, z) - returns the modified Struve function `L_n(z)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StruveL.md">StruveL
   *      documentation</a>
   */
  public final static IBuiltInSymbol StruveL = S.initFinalSymbol("StruveL", ID.StruveL);

  /**
   * StudentTDistribution(v) - returns a Student's t-distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StudentTDistribution.md">StudentTDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol StudentTDistribution =
      S.initFinalSymbol("StudentTDistribution", ID.StudentTDistribution);

  public final static IBuiltInSymbol Style = S.initFinalSymbol("Style", ID.Style);

  public final static IBuiltInSymbol StyleForm = S.initFinalSymbol("StyleForm", ID.StyleForm);

  /**
   * Subdivide(n) - returns a list with `n+1` entries obtained by subdividing the range `0` to `1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subdivide.md">Subdivide
   *      documentation</a>
   */
  public final static IBuiltInSymbol Subdivide = S.initFinalSymbol("Subdivide", ID.Subdivide);

  /**
   * Subfactorial(n) - returns the subfactorial number of the integer `n`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subfactorial.md">Subfactorial
   *      documentation</a>
   */
  public final static IBuiltInSymbol Subfactorial =
      S.initFinalSymbol("Subfactorial", ID.Subfactorial);

  public final static IBuiltInSymbol Subscript = S.initFinalSymbol("Subscript", ID.Subscript);

  public final static IBuiltInSymbol SubscriptBox =
      S.initFinalSymbol("SubscriptBox", ID.SubscriptBox);

  /**
   * SubsetQ(set1, set2) - returns `True` if `set2` is a subset of `set1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubsetQ.md">SubsetQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SubsetQ = S.initFinalSymbol("SubsetQ", ID.SubsetQ);

  /**
   * Subsets(list) - finds a list of all possible subsets of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subsets.md">Subsets
   *      documentation</a>
   */
  public final static IBuiltInSymbol Subsets = S.initFinalSymbol("Subsets", ID.Subsets);

  public final static IBuiltInSymbol SubsetCases = S.initFinalSymbol("SubsetCases", ID.SubsetCases);

  public final static IBuiltInSymbol SubsetCount = S.initFinalSymbol("SubsetCount", ID.SubsetCount);

  public final static IBuiltInSymbol SubsetPosition =
      S.initFinalSymbol("SubsetPosition", ID.SubsetPosition);

  public final static IBuiltInSymbol SubsetReplace =
      S.initFinalSymbol("SubsetReplace", ID.SubsetReplace);

  public final static IBuiltInSymbol Subsuperscript =
      S.initFinalSymbol("Subsuperscript", ID.Subsuperscript);

  public final static IBuiltInSymbol SubsuperscriptBox =
      S.initFinalSymbol("SubsuperscriptBox", ID.SubsuperscriptBox);

  /**
   * Subtract(a, b) - represents the subtraction of `b` from `a`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subtract.md">Subtract
   *      documentation</a>
   */
  public final static IBuiltInSymbol Subtract = S.initFinalSymbol("Subtract", ID.Subtract);

  /**
   * SubtractFrom(x, dx) - is equivalent to `x = x - dx`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubtractFrom.md">SubtractFrom
   *      documentation</a>
   */
  public final static IBuiltInSymbol SubtractFrom =
      S.initFinalSymbol("SubtractFrom", ID.SubtractFrom);

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
      S.initFinalSymbol("SubtractSides", ID.SubtractSides);

  /**
   * Sum(expr, {i, imin, imax}) - evaluates the discrete sum of `expr` with `i` ranging from `imin`
   * to `imax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sum.md">Sum
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sum = S.initFinalSymbol("Sum", ID.Sum);

  public final static IBuiltInSymbol Summary = S.initFinalSymbol("Summary", ID.Summary);

  public final static IBuiltInSymbol Superscript = S.initFinalSymbol("Superscript", ID.Superscript);

  public final static IBuiltInSymbol SuperscriptBox =
      S.initFinalSymbol("SuperscriptBox", ID.SuperscriptBox);

  /**
   * Surd(expr, n) - returns the `n`-th root of `expr`. If the result is defined, it's a real value.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Surd.md">Surd
   *      documentation</a>
   */
  public final static IBuiltInSymbol Surd = S.initFinalSymbol("Surd", ID.Surd);

  public final static IBuiltInSymbol SurfaceArea = S.initFinalSymbol("SurfaceArea", ID.SurfaceArea);

  public final static IBuiltInSymbol SurfaceGraphics =
      S.initFinalSymbol("SurfaceGraphics", ID.SurfaceGraphics);

  /**
   * SurvivalFunction(dist, x) - returns the survival function for the distribution `dist` evaluated
   * at `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SurvivalFunction.md">SurvivalFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol SurvivalFunction =
      S.initFinalSymbol("SurvivalFunction", ID.SurvivalFunction);

  /**
   * Switch(expr, pattern1, value1, pattern2, value2, ...) - yields the first `value` for which
   * `expr` matches the corresponding pattern.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Switch.md">Switch
   *      documentation</a>
   */
  public final static IBuiltInSymbol Switch = S.initFinalSymbol("Switch", ID.Switch);

  /**
   * Symbol - is the head of symbols.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Symbol.md">Symbol
   *      documentation</a>
   */
  public final static IBuiltInSymbol Symbol = S.initFinalSymbol("Symbol", ID.Symbol);

  /**
   * SymbolName(s) - returns the name of the symbol `s` (without any leading context name).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymbolName.md">SymbolName
   *      documentation</a>
   */
  public final static IBuiltInSymbol SymbolName = S.initFinalSymbol("SymbolName", ID.SymbolName);

  /**
   * SymbolQ(x) - is `True` if `x` is a symbol, or `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymbolQ.md">SymbolQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SymbolQ = S.initFinalSymbol("SymbolQ", ID.SymbolQ);

  public final static IBuiltInSymbol Symmetric = S.initFinalSymbol("Symmetric", ID.Symmetric);

  /**
   * SymmetricMatrixQ(m) - returns `True` if `m` is a symmetric matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SymmetricMatrixQ.md">SymmetricMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SymmetricMatrixQ =
      S.initFinalSymbol("SymmetricMatrixQ", ID.SymmetricMatrixQ);

  public final static IBuiltInSymbol SyntaxLength =
      S.initFinalSymbol("SyntaxLength", ID.SyntaxLength);

  /**
   * SyntaxQ(str) - is `True` if the given `str` is a string which has the correct syntax.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SyntaxQ.md">SyntaxQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SyntaxQ = S.initFinalSymbol("SyntaxQ", ID.SyntaxQ);

  /**
   * SystemDialogInput("FileOpen") - if the file system is enabled, open a file chooser dialog box.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SystemDialogInput.md">SystemDialogInput
   *      documentation</a>
   */
  public final static IBuiltInSymbol SystemDialogInput =
      S.initFinalSymbol("SystemDialogInput", ID.SystemDialogInput);

  public final static IBuiltInSymbol SystemOptions =
      S.initFinalSymbol("SystemOptions", ID.SystemOptions);

  /**
   * Table(expr, {i, n}) - evaluates `expr` with `i` ranging from `1` to `n`, returning a list of
   * the results.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Table.md">Table
   *      documentation</a>
   */
  public final static IBuiltInSymbol Table = S.initFinalSymbol("Table", ID.Table);

  public final static IBuiltInSymbol TableAlignments =
      S.initFinalSymbol("TableAlignments", ID.TableAlignments);

  public final static IBuiltInSymbol TableDepth = S.initFinalSymbol("TableDepth", ID.TableDepth);

  public final static IBuiltInSymbol TableDirections =
      S.initFinalSymbol("TableDirections", ID.TableDirections);

  public final static IBuiltInSymbol TableForm = S.initFinalSymbol("TableForm", ID.TableForm);

  public final static IBuiltInSymbol TableHeadings =
      S.initFinalSymbol("TableHeadings", ID.TableHeadings);

  public final static IBuiltInSymbol TableSpacing =
      S.initFinalSymbol("TableSpacing", ID.TableSpacing);

  /**
   * TagSet(f, expr, value) - assigns the evaluated `value` to `expr` and associates the
   * corresponding rule with the symbol `f`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TagSet.md">TagSet
   *      documentation</a>
   */
  public final static IBuiltInSymbol TagSet = S.initFinalSymbol("TagSet", ID.TagSet);

  /**
   * TagSetDelayed(f, expr, value) - assigns `value` to `expr`, without evaluating `value` and
   * associates the corresponding rule with the symbol `f`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TagSetDelayed.md">TagSetDelayed
   *      documentation</a>
   */
  public final static IBuiltInSymbol TagSetDelayed =
      S.initFinalSymbol("TagSetDelayed", ID.TagSetDelayed);

  /**
   * Take(expr, n) - returns `expr` with all but the first `n` leaves removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Take.md">Take
   *      documentation</a>
   */
  public final static IBuiltInSymbol Take = S.initFinalSymbol("Take", ID.Take);

  /**
   * TakeLargest({e_1, e_2, ..., e_i}, n) - returns the `n` largest real values from the list `{e_1,
   * e_2, ..., e_i}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeLargest.md">TakeLargest
   *      documentation</a>
   */
  public final static IBuiltInSymbol TakeLargest = S.initFinalSymbol("TakeLargest", ID.TakeLargest);

  /**
   * TakeLargestBy({e_1, e_2, ..., e_i}, function, n) - returns the `n` values from the list `{e_1,
   * e_2, ..., e_i}`, where `function(e_i)` is largest.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeLargestBy.md">TakeLargestBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol TakeLargestBy =
      S.initFinalSymbol("TakeLargestBy", ID.TakeLargestBy);

  /**
   * TakeSmallest({e_1, e_2, ..., e_i}, n) - returns the `n` smallest real values from the list
   * `{e_1, e_2, ..., e_i}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeSmallest.md">TakeSmallest
   *      documentation</a>
   */
  public final static IBuiltInSymbol TakeSmallest =
      S.initFinalSymbol("TakeSmallest", ID.TakeSmallest);

  /**
   * TakeSmallestBy({e_1, e_2, ..., e_i}, function, n) - returns the `n` values from the list `{e_1,
   * e_2, ..., e_i}`, where `function(e_i)` is smallest.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeSmallestBy.md">TakeSmallestBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol TakeSmallestBy =
      S.initFinalSymbol("TakeSmallestBy", ID.TakeSmallestBy);

  /**
   * TakeWhile({e1, e2, ...}, head) - returns the list of elements `ei` at the start of list for
   * which `head(ei)` returns `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TakeWhile.md">TakeWhile
   *      documentation</a>
   */
  public final static IBuiltInSymbol TakeWhile = S.initFinalSymbol("TakeWhile", ID.TakeWhile);

  /**
   * Tally(list) - return the elements and their number of occurrences in `list` in a new result
   * list. The `binaryPredicate` tests if two elements are equivalent. `SameQ` is used as the
   * default `binaryPredicate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tally.md">Tally
   *      documentation</a>
   */
  public final static IBuiltInSymbol Tally = S.initFinalSymbol("Tally", ID.Tally);

  /**
   * Tan(expr) - returns the tangent of `expr` (measured in radians).
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tan.md">Tan
   *      documentation</a>
   */
  public final static IBuiltInSymbol Tan = S.initFinalSymbol("Tan", ID.Tan);

  /**
   * Tanh(z) - returns the hyperbolic tangent of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tanh.md">Tanh
   *      documentation</a>
   */
  public final static IBuiltInSymbol Tanh = S.initFinalSymbol("Tanh", ID.Tanh);

  public final static IBuiltInSymbol TargetFunctions =
      S.initFinalSymbol("TargetFunctions", ID.TargetFunctions);

  /**
   * TautologyQ(boolean-expr, list-of-variables) - test whether the `boolean-expr` is satisfiable by
   * all combinations of boolean `False` and `True` values for the `list-of-variables`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TautologyQ.md">TautologyQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol TautologyQ = S.initFinalSymbol("TautologyQ", ID.TautologyQ);

  public final static IBuiltInSymbol Taylor = S.initFinalSymbol("Taylor", ID.Taylor);

  /**
   * TemplateApply(string, values) - renders a `StringTemplate` expression by replacing
   * `TemplateSlot`s with mapped values.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateApply.md">TemplateApply
   *      documentation</a>
   */
  public final static IBuiltInSymbol TemplateApply =
      S.initFinalSymbol("TemplateApply", ID.TemplateApply);

  public final static IBuiltInSymbol TemplateExpression =
      S.initFinalSymbol("TemplateExpression", ID.TemplateExpression);

  /**
   * TemplateIf(condition-expression, true-expression, false-expression) - in `TemplateApply`
   * evaluation insert `true-expression` if `condition-expression` evaluates to `true`, otherwise
   * insert `false-expression`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateIf.md">TemplateIf
   *      documentation</a>
   */
  public final static IBuiltInSymbol TemplateIf = S.initFinalSymbol("TemplateIf", ID.TemplateIf);

  /**
   * TemplateSlot(string) - gives a `TemplateSlot` expression with name `string`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TemplateSlot.md">TemplateSlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol TemplateSlot =
      S.initFinalSymbol("TemplateSlot", ID.TemplateSlot);

  /**
   * TensorDimensions(t) - return the dimensions of the tensor `t`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TensorDimensions.md">TensorDimensions
   *      documentation</a>
   */
  public final static IBuiltInSymbol TensorDimensions =
      S.initFinalSymbol("TensorDimensions", ID.TensorDimensions);

  /**
   * TensorProduct(t1, t2, ...) - product of the tensors `t1, t2, ...`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TensorProduct.md">TensorProduct
   *      documentation</a>
   */
  public final static IBuiltInSymbol TensorProduct =
      S.initFinalSymbol("TensorProduct", ID.TensorProduct);

  /**
   * TensorRank(t) - return the rank of the tensor `t`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TensorRank.md">TensorRank
   *      documentation</a>
   */
  public final static IBuiltInSymbol TensorRank = S.initFinalSymbol("TensorRank", ID.TensorRank);

  public final static IBuiltInSymbol TensorSymmetry =
      S.initFinalSymbol("TensorSymmetry", ID.TensorSymmetry);

  public final static IBuiltInSymbol TestID = S.initFinalSymbol("TestID", ID.TestID);

  /**
   * TestReport("file-name-string") - load the unit tests from a `file-name-string` and print a
   * summary of the `VerificationTest` included in the file.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TestReport.md">TestReport
   *      documentation</a>
   */
  public final static IBuiltInSymbol TestReport = S.initFinalSymbol("TestReport", ID.TestReport);

  public final static IBuiltInSymbol TestReportObject =
      S.initFinalSymbol("TestReportObject", ID.TestReportObject);

  /**
   * TestResultObject( ... ) - is an association wrapped in a `TestResultObject`returned from
   * `VerificationTest` which stores the results from executing a single unit test.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TestResultObject.md">TestResultObject
   *      documentation</a>
   */
  public final static IBuiltInSymbol TestResultObject =
      S.initFinalSymbol("TestResultObject", ID.TestResultObject);

  public final static IBuiltInSymbol Tetrahedron = S.initFinalSymbol("Tetrahedron", ID.Tetrahedron);

  /**
   * TeXForm(expr) - returns the TeX form of the evaluated `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TeXForm.md">TeXForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol TeXForm = S.initFinalSymbol("TeXForm", ID.TeXForm);

  public final static IBuiltInSymbol Text = S.initFinalSymbol("Text", ID.Text);

  public final static IBuiltInSymbol TextCell = S.initFinalSymbol("TextCell", ID.TextCell);

  public final static IBuiltInSymbol TextElement = S.initFinalSymbol("TextElement", ID.TextElement);

  public final static IBuiltInSymbol TextString = S.initFinalSymbol("TextString", ID.TextString);

  public final static IBuiltInSymbol TextStructure =
      S.initFinalSymbol("TextStructure", ID.TextStructure);

  public final static IBuiltInSymbol Thickness = S.initFinalSymbol("Thickness", ID.Thickness);

  /**
   * Thread(f(args) - threads `f` over any lists that appear in `args`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Thread.md">Thread
   *      documentation</a>
   */
  public final static IBuiltInSymbol Thread = S.initFinalSymbol("Thread", ID.Thread);

  /**
   * ThreeJSymbol({j1,m1},{j2,m2},{j3,m3}) - get the 3-j symbol coefficients.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ThreeJSymbol.md">ThreeJSymbol
   *      documentation</a>
   */
  public final static IBuiltInSymbol ThreeJSymbol =
      S.initFinalSymbol("ThreeJSymbol", ID.ThreeJSymbol);

  /**
   * Through(p(f)[x]) - gives `p(f(x))`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Through.md">Through
   *      documentation</a>
   */
  public final static IBuiltInSymbol Through = S.initFinalSymbol("Through", ID.Through);

  /**
   * Throw(value) - stops evaluation and returns `value` as the value of the nearest enclosing
   * `Catch`. `Catch(value, tag)` is caught only by `Catch(expr, form)`, where `tag` matches `form`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Throw.md">Throw
   *      documentation</a>
   */
  public final static IBuiltInSymbol Throw = S.initFinalSymbol("Throw", ID.Throw);

  public final static IBuiltInSymbol Ticks = S.initFinalSymbol("Ticks", ID.Ticks);

  public final static IBuiltInSymbol TicksStyle = S.initFinalSymbol("TicksStyle", ID.TicksStyle);

  /**
   * TimeConstrained(expression, seconds) - stop evaluation of `expression` if time measurement of
   * the evaluation exceeds `seconds` and return `$Aborted`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeConstrained.md">TimeConstrained
   *      documentation</a>
   */
  public final static IBuiltInSymbol TimeConstrained =
      S.initFinalSymbol("TimeConstrained", ID.TimeConstrained);

  public final static IBuiltInSymbol TimeObject = S.initFinalSymbol("TimeObject", ID.TimeObject);

  public final static IBuiltInSymbol TimeRemaining =
      S.initFinalSymbol("TimeRemaining", ID.TimeRemaining);

  /**
   * Times(a, b, ...) - represents the product of the terms `a, b, ...`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Times.md">Times
   *      documentation</a>
   */
  public final static IBuiltInSymbol Times = S.initFinalSymbol("Times", ID.Times);

  /**
   * TimesBy(x, dx) - is equivalent to `x = x * dx`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimesBy.md">TimesBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol TimesBy = S.initFinalSymbol("TimesBy", ID.TimesBy);

  /**
   * TimeValue(p, i, n) - returns a time value calculation.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeValue.md">TimeValue
   *      documentation</a>
   */
  public final static IBuiltInSymbol TimeValue = S.initFinalSymbol("TimeValue", ID.TimeValue);

  /**
   * Timing(x) - returns a list with the first entry containing the evaluation CPU time of `x` and
   * the second entry is the evaluation result of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Timing.md">Timing
   *      documentation</a>
   */
  public final static IBuiltInSymbol Timing = S.initFinalSymbol("Timing", ID.Timing);

  public final static IBuiltInSymbol Tiny = S.initFinalSymbol("Tiny", ID.Tiny);

  public final static IBuiltInSymbol ToBoxes = S.initFinalSymbol("ToBoxes", ID.ToBoxes);

  /**
   * ToCharacterCode(string) - converts `string` into a list of corresponding integer character
   * codes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToCharacterCode.md">ToCharacterCode
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToCharacterCode =
      S.initFinalSymbol("ToCharacterCode", ID.ToCharacterCode);

  public final static IBuiltInSymbol Today = S.initFinalSymbol("Today", ID.Today);

  /**
   * ToeplitzMatrix(n) - gives a toeplitz matrix with the dimension `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToeplitzMatrix.md">ToeplitzMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToeplitzMatrix =
      S.initFinalSymbol("ToeplitzMatrix", ID.ToeplitzMatrix);

  /**
   * ToExpression("string", form) - converts the `string` given in `form` into an expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToExpression.md">ToExpression
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToExpression =
      S.initFinalSymbol("ToExpression", ID.ToExpression);

  /**
   * Together(expr) - writes sums of fractions in `expr` together.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Together.md">Together
   *      documentation</a>
   */
  public final static IBuiltInSymbol Together = S.initFinalSymbol("Together", ID.Together);

  public final static IBuiltInSymbol ToIntervalData =
      S.initFinalSymbol("ToIntervalData", ID.ToIntervalData);

  /**
   * ToLowerCase(string) - converts `string` into a string of corresponding lowercase character
   * codes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToLowerCase.md">ToLowerCase
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToLowerCase = S.initFinalSymbol("ToLowerCase", ID.ToLowerCase);

  public final static IBuiltInSymbol TooLarge = S.initFinalSymbol("TooLarge", ID.TooLarge);

  public final static IBuiltInSymbol Top = S.initFinalSymbol("Top", ID.Top);

  /**
   * ToPolarCoordinates({x, y}) - return the polar coordinates for the cartesian coordinates `{x,
   * y}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToPolarCoordinates.md">ToPolarCoordinates
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToPolarCoordinates =
      S.initFinalSymbol("ToPolarCoordinates", ID.ToPolarCoordinates);

  public final static IBuiltInSymbol ToRadicals = S.initFinalSymbol("ToRadicals", ID.ToRadicals);

  /**
   * ToSphericalCoordinates({x, y, z}) - returns the spherical coordinates for the cartesian
   * coordinates `{x, y, z}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToSphericalCoordinates.md">ToSphericalCoordinates
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToSphericalCoordinates =
      S.initFinalSymbol("ToSphericalCoordinates", ID.ToSphericalCoordinates);

  /**
   * ToString(expr) - converts `expr` into a string.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToString.md">ToString
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToString = S.initFinalSymbol("ToString", ID.ToString);

  /**
   * Total(list) - adds all values in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Total.md">Total
   *      documentation</a>
   */
  public final static IBuiltInSymbol Total = S.initFinalSymbol("Total", ID.Total);

  /**
   * ToUnicode(string) - converts `string` into a string of corresponding unicode character codes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToUnicode.md">ToUnicode
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToUnicode = S.initFinalSymbol("ToUnicode", ID.ToUnicode);

  /**
   * ToUpperCase(string) - converts `string` into a string of corresponding uppercase character
   * codes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToUpperCase.md">ToUpperCase
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToUpperCase = S.initFinalSymbol("ToUpperCase", ID.ToUpperCase);

  /**
   * Tr(matrix) - computes the trace of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tr.md">Tr
   *      documentation</a>
   */
  public final static IBuiltInSymbol Tr = S.initFinalSymbol("Tr", ID.Tr);

  /**
   * Trace(expr) - return the evaluation steps which are used to get the result.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Trace.md">Trace
   *      documentation</a>
   */
  public final static IBuiltInSymbol Trace = S.initFinalSymbol("Trace", ID.Trace);

  public final static IBuiltInSymbol TraceForm = S.initFinalSymbol("TraceForm", ID.TraceForm);

  public final static IBuiltInSymbol TraditionalForm =
      S.initFinalSymbol("TraditionalForm", ID.TraditionalForm);

  /**
   * TransformationFunction(m) - represents a transformation.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TransformationFunction.md">TransformationFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol TransformationFunction =
      S.initFinalSymbol("TransformationFunction", ID.TransformationFunction);

  /**
   * TranslationTransform(v) - gives a `TransformationFunction` that translates points by vector
   * `v`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TranslationTransform.md">TranslationTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol TranslationTransform =
      S.initFinalSymbol("TranslationTransform", ID.TranslationTransform);

  /**
   * Transliterate("string") - try converting the given string to a similar ASCII string
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Transliterate.md">Transliterate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Transliterate =
      S.initFinalSymbol("Transliterate", ID.Transliterate);

  /**
   * Transpose(m) - transposes rows and columns in the matrix `m`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Transpose.md">Transpose
   *      documentation</a>
   */
  public final static IBuiltInSymbol Transpose = S.initFinalSymbol("Transpose", ID.Transpose);

  /**
   * TreeForm(expr) - create a tree visualization from the given expression `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TreeForm.md">TreeForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol TreeForm = S.initFinalSymbol("TreeForm", ID.TreeForm);

  public final static IBuiltInSymbol Triangle = S.initFinalSymbol("Triangle", ID.Triangle);

  public final static IBuiltInSymbol Trig = S.initFinalSymbol("Trig", ID.Trig);

  /**
   * TrigExpand(expr) - expands out trigonometric expressions in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigExpand.md">TrigExpand
   *      documentation</a>
   */
  public final static IBuiltInSymbol TrigExpand = S.initFinalSymbol("TrigExpand", ID.TrigExpand);

  /**
   * TrigReduce(expr) - rewrites products and powers of trigonometric functions in `expr` in terms
   * of trigonometric functions with combined arguments.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigReduce.md">TrigReduce
   *      documentation</a>
   */
  public final static IBuiltInSymbol TrigReduce = S.initFinalSymbol("TrigReduce", ID.TrigReduce);

  public final static IBuiltInSymbol TrigSimplifyFu =
      S.initFinalSymbol("TrigSimplifyFu", ID.TrigSimplifyFu);

  /**
   * TrigToExp(expr) - converts trigonometric functions in `expr` to exponentials.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigToExp.md">TrigToExp
   *      documentation</a>
   */
  public final static IBuiltInSymbol TrigToExp = S.initFinalSymbol("TrigToExp", ID.TrigToExp);

  /**
   * True - the constant `True` represents the boolean value **true**
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/True.md">True
   *      documentation</a>
   */
  public final static IBuiltInSymbol True = S.initFinalSymbol("True", ID.True);

  /**
   * TrueQ(expr) - returns `True` if and only if `expr` is `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrueQ.md">TrueQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol TrueQ = S.initFinalSymbol("TrueQ", ID.TrueQ);

  /**
   * TTest(real-vector) - Returns the *observed significance level*, or *p-value*, associated with a
   * one-sample, two-tailed t-test comparing the mean of the input vector with the constant
   * <code>0.0</code>.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TTest.md">TTest
   *      documentation</a>
   */
  public final static IBuiltInSymbol TTest = S.initFinalSymbol("TTest", ID.TTest);

  public final static IBuiltInSymbol Tube = S.initFinalSymbol("Tube", ID.Tube);

  public final static IBuiltInSymbol TukeyWindow = S.initFinalSymbol("TukeyWindow", ID.TukeyWindow);

  /**
   * Tuples(list, n) - creates a list of all `n`-tuples of elements in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tuples.md">Tuples
   *      documentation</a>
   */
  public final static IBuiltInSymbol Tuples = S.initFinalSymbol("Tuples", ID.Tuples);

  public final static IBuiltInSymbol TwoWayRule = S.initFinalSymbol("TwoWayRule", ID.TwoWayRule);

  /**
   * Undefined - represents an undefined result for example in the `ConditionalExpression` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Undefined.md">Undefined
   *      documentation</a>
   */
  public final static IBuiltInSymbol Undefined = S.initFinalSymbol("Undefined", ID.Undefined);

  /**
   * Underflow( ) - represents a number too small to be represented by Symja.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Underflow.md">Underflow
   *      documentation</a>
   */
  public final static IBuiltInSymbol Underflow = S.initFinalSymbol("Underflow", ID.Underflow);

  public final static IBuiltInSymbol Underoverscript =
      S.initFinalSymbol("Underoverscript", ID.Underoverscript);

  public final static IBuiltInSymbol UnderoverscriptBox =
      S.initFinalSymbol("UnderoverscriptBox", ID.UnderoverscriptBox);

  public final static IBuiltInSymbol Underscript = S.initFinalSymbol("Underscript", ID.Underscript);

  public final static IBuiltInSymbol UnderscriptBox =
      S.initFinalSymbol("UnderscriptBox", ID.UnderscriptBox);

  /**
   * UndirectedEdge(a, b) - is an undirected edge between the vertices `a` and `b` in a `graph`
   * object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UndirectedEdge.md">UndirectedEdge
   *      documentation</a>
   */
  public final static IBuiltInSymbol UndirectedEdge =
      S.initFinalSymbol("UndirectedEdge", ID.UndirectedEdge);

  /**
   * Unequal(x, y) - yields `False` if `x` and `y` are known to be equal, or `True` if `x` and `y`
   * are known to be unequal.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unequal.md">Unequal
   *      documentation</a>
   */
  public final static IBuiltInSymbol Unequal = S.initFinalSymbol("Unequal", ID.Unequal);

  public final static IBuiltInSymbol UnequalTo = S.initFinalSymbol("UnequalTo", ID.UnequalTo);

  /**
   * Unevaluated(expr) - temporarily leaves `expr` in an unevaluated form when it appears as a
   * function argument.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unevaluated.md">Unevaluated
   *      documentation</a>
   */
  public final static IBuiltInSymbol Unevaluated = S.initFinalSymbol("Unevaluated", ID.Unevaluated);

  /**
   * UniformDistribution({min, max}) - returns a uniform distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UniformDistribution.md">UniformDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol UniformDistribution =
      S.initFinalSymbol("UniformDistribution", ID.UniformDistribution);

  /**
   * Union(set1, set2) - get the union set from `set1` and `set2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Union.md">Union
   *      documentation</a>
   */
  public final static IBuiltInSymbol Union = S.initFinalSymbol("Union", ID.Union);

  /**
   * Unique(expr) - create a unique symbol of the form `expr$...`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unique.md">Unique
   *      documentation</a>
   */
  public final static IBuiltInSymbol Unique = S.initFinalSymbol("Unique", ID.Unique);

  /**
   * UnitaryMatrixQ(U) - returns `True` if a complex square matrix `U` is unitary, that is, if its
   * conjugate transpose `U^(*)` is also its inverse, that is, if `U^(*).U = U.U^(*) = U.U^(-1) - 1
   * = I` where `I` is the identity matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitaryMatrixQ.md">UnitaryMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnitaryMatrixQ =
      S.initFinalSymbol("UnitaryMatrixQ", ID.UnitaryMatrixQ);

  /**
   * UnitConvert(quantity) - convert the `quantity` to the base unit
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitConvert.md">UnitConvert
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnitConvert = S.initFinalSymbol("UnitConvert", ID.UnitConvert);

  /**
   * Unitize(expr) - maps a non-zero `expr` to `1`, and a zero `expr` to `0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unitize.md">Unitize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Unitize = S.initFinalSymbol("Unitize", ID.Unitize);

  /**
   * UnitStep(expr) - returns `0`, if `expr` is less than `0` and returns `1`, if `expr` is greater
   * equal than `0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitStep.md">UnitStep
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnitStep = S.initFinalSymbol("UnitStep", ID.UnitStep);

  public final static IBuiltInSymbol UnitTriangle =
      S.initFinalSymbol("UnitTriangle", ID.UnitTriangle);

  /**
   * UnitVector(position) - returns a unit vector with element `1` at the given `position`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitVector.md">UnitVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnitVector = S.initFinalSymbol("UnitVector", ID.UnitVector);

  public final static IBuiltInSymbol UniverseAge = S.initFinalSymbol("UniverseAge", ID.UniverseAge);

  public final static IBuiltInSymbol Unknown = S.initFinalSymbol("Unknown", ID.Unknown);

  public final static IBuiltInSymbol Unprotect = S.initFinalSymbol("Unprotect", ID.Unprotect);

  /**
   * UnsameQ(x, y) - returns `True` if `x` and `y` are not structurally identical.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnsameQ.md">UnsameQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnsameQ = S.initFinalSymbol("UnsameQ", ID.UnsameQ);

  /**
   * Unset(expr) - removes any definitions belonging to the left-hand-side `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unset.md">Unset
   *      documentation</a>
   */
  public final static IBuiltInSymbol Unset = S.initFinalSymbol("Unset", ID.Unset);

  /**
   * UpperCaseQ(str) - is `True` if the given `str` is a string which only contains upper case
   * characters.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperCaseQ.md">UpperCaseQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol UpperCaseQ = S.initFinalSymbol("UpperCaseQ", ID.UpperCaseQ);

  /**
   * UpperTriangularize(matrix) - create a upper triangular matrix from the given `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperTriangularize.md">UpperTriangularize
   *      documentation</a>
   */
  public final static IBuiltInSymbol UpperTriangularize =
      S.initFinalSymbol("UpperTriangularize", ID.UpperTriangularize);

  /**
   * UpperTriangularMatrixQ(matrix) - returns `True` if `matrix` is upper triangular.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperTriangularMatrixQ.md">UpperTriangularMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol UpperTriangularMatrixQ =
      S.initFinalSymbol("UpperTriangularMatrixQ", ID.UpperTriangularMatrixQ);

  public final static IBuiltInSymbol UpSet = S.initFinalSymbol("UpSet", ID.UpSet);

  public final static IBuiltInSymbol UpSetDelayed =
      S.initFinalSymbol("UpSetDelayed", ID.UpSetDelayed);

  public final static IBuiltInSymbol UpTo = S.initFinalSymbol("UpTo", ID.UpTo);

  /**
   * UpValues(symbol) - prints the up-value rules associated with `symbol`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpValues.md">UpValues
   *      documentation</a>
   */
  public final static IBuiltInSymbol UpValues = S.initFinalSymbol("UpValues", ID.UpValues);

  public final static IBuiltInSymbol URLFetch = S.initFinalSymbol("URLFetch", ID.URLFetch);

  public final static IBuiltInSymbol UseTypeChecking =
      S.initFinalSymbol("UseTypeChecking", ID.UseTypeChecking);

  /**
   * ValueQ(expr) - returns `True` if and only if `expr` is defined.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ValueQ.md">ValueQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ValueQ = S.initFinalSymbol("ValueQ", ID.ValueQ);

  /**
   * Values(association) - return a list of values of the `association`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Values.md">Values
   *      documentation</a>
   */
  public final static IBuiltInSymbol Values = S.initFinalSymbol("Values", ID.Values);

  /**
   * VandermondeMatrix(n) - gives the Vandermonde matrix with `n` rows and columns.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VandermondeMatrix.md">VandermondeMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol VandermondeMatrix =
      S.initFinalSymbol("VandermondeMatrix", ID.VandermondeMatrix);

  public final static IBuiltInSymbol Variable = S.initFinalSymbol("Variable", ID.Variable);

  /**
   * Variables(expr) - gives a list of the variables that appear in the polynomial `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Variables.md">Variables
   *      documentation</a>
   */
  public final static IBuiltInSymbol Variables = S.initFinalSymbol("Variables", ID.Variables);

  /**
   * Variance(list) - computes the variance of `list`. `list` may consist of numerical values or
   * symbols. Numerical values may be real or complex.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Variance.md">Variance
   *      documentation</a>
   */
  public final static IBuiltInSymbol Variance = S.initFinalSymbol("Variance", ID.Variance);

  /**
   * VectorAngle(u, v) - gives the angles between vectors `u` and `v`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorAngle.md">VectorAngle
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorAngle = S.initFinalSymbol("VectorAngle", ID.VectorAngle);

  public final static IBuiltInSymbol VectorGreater =
      S.initFinalSymbol("VectorGreater", ID.VectorGreater);

  public final static IBuiltInSymbol VectorGreaterEqual =
      S.initFinalSymbol("VectorGreaterEqual", ID.VectorGreaterEqual);

  public final static IBuiltInSymbol VectorLess = S.initFinalSymbol("VectorLess", ID.VectorLess);

  public final static IBuiltInSymbol VectorLessEqual =
      S.initFinalSymbol("VectorLessEqual", ID.VectorLessEqual);

  public final static IBuiltInSymbol VectorPlot = S.initFinalSymbol("VectorPlot", ID.VectorPlot);

  /**
   * VectorQ(v) - returns `True` if `v` is a list of elements which are not themselves lists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorQ.md">VectorQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorQ = S.initFinalSymbol("VectorQ", ID.VectorQ);

  public final static IBuiltInSymbol Vectors = S.initFinalSymbol("Vectors", ID.Vectors);

  /**
   * Verbatim(expr) - prevents pattern constructs in `expr` from taking effect, allowing them to
   * match themselves.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Verbatim.md">Verbatim
   *      documentation</a>
   */
  public final static IBuiltInSymbol Verbatim = S.initFinalSymbol("Verbatim", ID.Verbatim);

  /**
   * VerificationTest(test-expr) - create a `TestResultObject` by testing if `test-expr` evaluates
   * to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VerificationTest.md">VerificationTest
   *      documentation</a>
   */
  public final static IBuiltInSymbol VerificationTest =
      S.initFinalSymbol("VerificationTest", ID.VerificationTest);

  /**
   * VertexEccentricity(graph, vertex) - compute the eccentricity of `vertex` in the `graph`. It's
   * the length of the longest shortest path from the `vertex` to every other vertex in the `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexEccentricity.md">VertexEccentricity
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexEccentricity =
      S.initFinalSymbol("VertexEccentricity", ID.VertexEccentricity);

  public final static IBuiltInSymbol VertexLabels =
      S.initFinalSymbol("VertexLabels", ID.VertexLabels);

  /**
   * VertexList(graph) - convert the `graph` into a list of vertices.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexList.md">VertexList
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexList = S.initFinalSymbol("VertexList", ID.VertexList);

  /**
   * VertexQ(graph, vertex) - test if `vertex` is a vertex in the `graph` object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexQ.md">VertexQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexQ = S.initFinalSymbol("VertexQ", ID.VertexQ);

  public final static IBuiltInSymbol VertexShapeFunction =
      S.initFinalSymbol("VertexShapeFunction", ID.VertexShapeFunction);

  public final static IBuiltInSymbol VertexSize = S.initFinalSymbol("VertexSize", ID.VertexSize);

  public final static IBuiltInSymbol VertexStyle = S.initFinalSymbol("VertexStyle", ID.VertexStyle);

  public final static IBuiltInSymbol ViewPoint = S.initFinalSymbol("ViewPoint", ID.ViewPoint);

  public final static IBuiltInSymbol Volume = S.initFinalSymbol("Volume", ID.Volume);

  public final static IBuiltInSymbol WeaklyConnectedGraphQ =
      S.initFinalSymbol("WeaklyConnectedGraphQ", ID.WeaklyConnectedGraphQ);

  public final static IBuiltInSymbol WeberE = S.initFinalSymbol("WeberE", ID.WeberE);

  /**
   * WeibullDistribution(a, b) - returns a Weibull distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeibullDistribution.md">WeibullDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol WeibullDistribution =
      S.initFinalSymbol("WeibullDistribution", ID.WeibullDistribution);

  public final static IBuiltInSymbol WeierstrassHalfPeriods =
      S.initFinalSymbol("WeierstrassHalfPeriods", ID.WeierstrassHalfPeriods);

  public final static IBuiltInSymbol WeierstrassInvariants =
      S.initFinalSymbol("WeierstrassInvariants", ID.WeierstrassInvariants);

  /**
   * WeierstrassP(expr, {n1, n2}) - Weierstrass elliptic function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeierstrassP.md">WeierstrassP
   *      documentation</a>
   */
  public final static IBuiltInSymbol WeierstrassP =
      S.initFinalSymbol("WeierstrassP", ID.WeierstrassP);

  public final static IBuiltInSymbol WeierstrassPPrime =
      S.initFinalSymbol("WeierstrassPPrime", ID.WeierstrassPPrime);

  /**
   * WeightedAdjacencyMatrix(graph) - convert the `graph` into a weighted adjacency matrix in sparse
   * array format.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeightedAdjacencyMatrix.md">WeightedAdjacencyMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol WeightedAdjacencyMatrix =
      S.initFinalSymbol("WeightedAdjacencyMatrix", ID.WeightedAdjacencyMatrix);

  public final static IBuiltInSymbol WeightedData =
      S.initFinalSymbol("WeightedData", ID.WeightedData);

  /**
   * WeightedGraphQ(expr) - test if `expr` is an explicit weighted graph object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WeightedGraphQ.md">WeightedGraphQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol WeightedGraphQ =
      S.initFinalSymbol("WeightedGraphQ", ID.WeightedGraphQ);

  public final static IBuiltInSymbol WheelGraph = S.initFinalSymbol("WheelGraph", ID.WheelGraph);

  /**
   * Which(cond1, expr1, cond2, expr2, ...) - yields `expr1` if `cond1` evaluates to `True`, `expr2`
   * if `cond2` evaluates to `True`, etc.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Which.md">Which
   *      documentation</a>
   */
  public final static IBuiltInSymbol Which = S.initFinalSymbol("Which", ID.Which);

  /**
   * While(test, body) - evaluates `body` as long as test evaluates to `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/While.md">While
   *      documentation</a>
   */
  public final static IBuiltInSymbol While = S.initFinalSymbol("While", ID.While);

  public final static IBuiltInSymbol White = S.initFinalSymbol("White", ID.White);

  /**
   * Whitespace - represents a sequence of whitespace characters.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Whitespace.md">Whitespace
   *      documentation</a>
   */
  public final static IBuiltInSymbol Whitespace = S.initFinalSymbol("Whitespace", ID.Whitespace);

  /**
   * WhitespaceCharacter - represents a single whitespace character.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WhitespaceCharacter.md">WhitespaceCharacter
   *      documentation</a>
   */
  public final static IBuiltInSymbol WhitespaceCharacter =
      S.initFinalSymbol("WhitespaceCharacter", ID.WhitespaceCharacter);

  public final static IBuiltInSymbol WhittakerM = S.initFinalSymbol("WhittakerM", ID.WhittakerM);

  public final static IBuiltInSymbol WhittakerW = S.initFinalSymbol("WhittakerW", ID.WhittakerW);

  public final static IBuiltInSymbol WignerD = S.initFinalSymbol("WignerD", ID.WignerD);

  /**
   * With({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables` by
   * replacing the local variables in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/With.md">With
   *      documentation</a>
   */
  public final static IBuiltInSymbol With = S.initFinalSymbol("With", ID.With);

  public final static IBuiltInSymbol Word = S.initFinalSymbol("Word", ID.Word);

  /**
   * WordBoundary - represents the boundary between words.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WordBoundary.md">WordBoundary
   *      documentation</a>
   */
  public final static IBuiltInSymbol WordBoundary =
      S.initFinalSymbol("WordBoundary", ID.WordBoundary);

  public final static IBuiltInSymbol WordCharacter =
      S.initFinalSymbol("WordCharacter", ID.WordCharacter);

  public final static IBuiltInSymbol WordSeparators =
      S.initFinalSymbol("WordSeparators", ID.WordSeparators);

  public final static IBuiltInSymbol WorkingPrecision =
      S.initFinalSymbol("WorkingPrecision", ID.WorkingPrecision);

  public final static IBuiltInSymbol Write = S.initFinalSymbol("Write", ID.Write);

  public final static IBuiltInSymbol WriteString = S.initFinalSymbol("WriteString", ID.WriteString);

  public final static IBuiltInSymbol Xnor = S.initFinalSymbol("Xnor", ID.Xnor);

  /**
   * Xor(arg1, arg2, ...) - Logical XOR (exclusive OR) function. Returns `True` if an odd number of
   * the arguments are `True` and the rest are `False`. Returns `False` if an even number of the
   * arguments are `True` and the rest are `False`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Xor.md">Xor
   *      documentation</a>
   */
  public final static IBuiltInSymbol Xor = S.initFinalSymbol("Xor", ID.Xor);

  public final static IBuiltInSymbol Yellow = S.initFinalSymbol("Yellow", ID.Yellow);

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
      S.initFinalSymbol("YuleDissimilarity", ID.YuleDissimilarity);

  public final static IBuiltInSymbol ZeroSymmetric =
      S.initFinalSymbol("ZeroSymmetric", ID.ZeroSymmetric);

  public final static IBuiltInSymbol ZeroTest = S.initFinalSymbol("ZeroTest", ID.ZeroTest);

  /**
   * Zeta(z) - returns the Riemann zeta function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Zeta.md">Zeta
   *      documentation</a>
   */
  public final static IBuiltInSymbol Zeta = S.initFinalSymbol("Zeta", ID.Zeta);

  /**
   * ZTransform(x,n,z) - returns the Z-Transform of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ZTransform.md">ZTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol ZTransform = S.initFinalSymbol("ZTransform", ID.ZTransform);


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

  public static final ISymbol f1 = initFinalHiddenSymbol("f1");
  public static final ISymbol f2 = initFinalHiddenSymbol("f2");
  public static final ISymbol f3 = initFinalHiddenSymbol("f3");
  public static final ISymbol f4 = initFinalHiddenSymbol("f4");

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
  /* package private */ static IBuiltInSymbol initFinalSymbol(final String symbolName,
      int ordinal) {
    String str;
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      str = (symbolName.length() == 1) ? symbolName : symbolName.toLowerCase(Locale.US);
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

  static {
    C.setAttributes(ISymbol.NHOLDALL);
  }
}
