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

  /** package private */
  static final IBuiltInSymbol[] BUILT_IN_SYMBOLS = new IBuiltInSymbol[ID.ZTransform + 10];

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
      new IdentityHashMap<>(((EXPRID_MAX_BUILTIN_LENGTH) + 1000) * 4 / 3 + 1);

  public static final Map<String, ISymbol> HIDDEN_SYMBOLS_MAP =
      Config.TRIE_STRING2SYMBOL_BUILDER.withMatch(TrieMatch.EXACT).build(); // Tries.forStrings();

  public static IBuiltInSymbol symbol(int id) {
    return BUILT_IN_SYMBOLS[id];
  }

  // START_S_SYMBOLS


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
   * $GeoLocation(x) - TODO describe `$GeoLocation`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$GeoLocation.md">$GeoLocation
   *      documentation</a>
   */
  public final static IBuiltInSymbol $GeoLocation =
      S.initFinalSymbol("$GeoLocation", ID.$GeoLocation);

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

  public final static IBuiltInSymbol $PerformanceGoal =
      S.initFinalSymbol("$PerformanceGoal", ID.$PerformanceGoal);

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

  public final static IBuiltInSymbol $SystemCharacterEncoding =
      S.initFinalSymbol("$SystemCharacterEncoding", ID.$SystemCharacterEncoding);

  public final static IBuiltInSymbol $SystemMemory =
      S.initFinalSymbol("$SystemMemory", ID.$SystemMemory);

  public final static IBuiltInSymbol $TemporaryDirectory =
      S.initFinalSymbol("$TemporaryDirectory", ID.$TemporaryDirectory);

  /**
   * $TimeZone(x) - TODO describe `$TimeZone`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$TimeZone.md">$TimeZone
   *      documentation</a>
   */
  public final static IBuiltInSymbol $TimeZone = S.initFinalSymbol("$TimeZone", ID.$TimeZone);

  /**
   * $UnitSystem(x) - TODO describe `$UnitSystem`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/$UnitSystem.md">$UnitSystem
   *      documentation</a>
   */
  public final static IBuiltInSymbol $UnitSystem = S.initFinalSymbol("$UnitSystem", ID.$UnitSystem);

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

  public final static IBuiltInSymbol AbsoluteDashing =
      S.initFinalSymbol("AbsoluteDashing", ID.AbsoluteDashing);

  public final static IBuiltInSymbol AbsolutePointSize =
      S.initFinalSymbol("AbsolutePointSize", ID.AbsolutePointSize);

  public final static IBuiltInSymbol AbsoluteThickness =
      S.initFinalSymbol("AbsoluteThickness", ID.AbsoluteThickness);

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
   * AccountingForm(x) - TODO describe `AccountingForm`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AccountingForm.md">AccountingForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol AccountingForm =
      S.initFinalSymbol("AccountingForm", ID.AccountingForm);

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
   * ActionMenu(x) - TODO describe `ActionMenu`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ActionMenu.md">ActionMenu
   *      documentation</a>
   */
  public final static IBuiltInSymbol ActionMenu = S.initFinalSymbol("ActionMenu", ID.ActionMenu);

  public final static IBuiltInSymbol Activate = S.initFinalSymbol("Activate", ID.Activate);

  public final static IBuiltInSymbol AcyclicGraphQ =
      S.initFinalSymbol("AcyclicGraphQ", ID.AcyclicGraphQ);

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
   * AdjacencyGraph(matrix) - convert the adjacency `matrix` into a graph expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AdjacencyGraph.md">AdjacencyGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol AdjacencyGraph =
      S.initFinalSymbol("AdjacencyGraph", ID.AdjacencyGraph);

  public final static IBuiltInSymbol AdjacencyList =
      S.initFinalSymbol("AdjacencyList", ID.AdjacencyList);

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
   * AffineTransform(m) - gives a `TransformationFunction` that represents an affine transform that
   * maps the vector `r` to `m.r`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AffineTransform.md">AffineTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol AffineTransform =
      S.initFinalSymbol("AffineTransform", ID.AffineTransform);

  /**
   * AggregateBy(x) - TODO describe `AggregateBy`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AggregateBy.md">AggregateBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol AggregateBy = S.initFinalSymbol("AggregateBy", ID.AggregateBy);

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

  public final static IBuiltInSymbol AlgebraicIntegerQ =
      S.initFinalSymbol("AlgebraicIntegerQ", ID.AlgebraicIntegerQ);

  public final static IBuiltInSymbol AlgebraicNumber =
      S.initFinalSymbol("AlgebraicNumber", ID.AlgebraicNumber);

  public final static IBuiltInSymbol Algebraics = S.initFinalSymbol("Algebraics", ID.Algebraics);

  /**
   * Alignment(x) - TODO describe `Alignment`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Alignment.md">Alignment
   *      documentation</a>
   */
  public final static IBuiltInSymbol Alignment = S.initFinalSymbol("Alignment", ID.Alignment);

  public final static IBuiltInSymbol AlignmentPoint =
      S.initFinalSymbol("AlignmentPoint", ID.AlignmentPoint);

  /**
   * All - is a value for a number of functions indicating to include everything. For example it is
   * a possible value for `Span`, `Part` and `Quiet`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/All.md">All
   *      documentation</a>
   */
  public final static IBuiltInSymbol All = S.initFinalSymbol("All", ID.All);

  /**
   * AllowedDimensions(x) - TODO describe `AllowedDimensions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AllowedDimensions.md">AllowedDimensions
   *      documentation</a>
   */
  public final static IBuiltInSymbol AllowedDimensions =
      S.initFinalSymbol("AllowedDimensions", ID.AllowedDimensions);

  public final static IBuiltInSymbol AllowedHeads =
      S.initFinalSymbol("AllowedHeads", ID.AllowedHeads);

  public final static IBuiltInSymbol AllowShortContext =
      S.initFinalSymbol("AllowShortContext", ID.AllowShortContext);

  /**
   * AllPoints(x) - TODO describe `AllPoints`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AllPoints.md">AllPoints
   *      documentation</a>
   */
  public final static IBuiltInSymbol AllPoints = S.initFinalSymbol("AllPoints", ID.AllPoints);

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
   * Alpha(x) - TODO describe `Alpha`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Alpha.md">Alpha
   *      documentation</a>
   */
  public final static IBuiltInSymbol Alpha = S.initFinalSymbol("Alpha", ID.Alpha);

  /**
   * Alphabet() - gives the list of lowercase letters `a-z` in the English or Latin alphabet .
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Alphabet.md">Alphabet
   *      documentation</a>
   */
  public final static IBuiltInSymbol Alphabet = S.initFinalSymbol("Alphabet", ID.Alphabet);

  public final static IBuiltInSymbol AlphabeticOrder =
      S.initFinalSymbol("AlphabeticOrder", ID.AlphabeticOrder);

  /**
   * AlphaChannel(x) - TODO describe `AlphaChannel`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AlphaChannel.md">AlphaChannel
   *      documentation</a>
   */
  public final static IBuiltInSymbol AlphaChannel =
      S.initFinalSymbol("AlphaChannel", ID.AlphaChannel);

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

  /**
   * AltitudeMethod(x) - TODO describe `AltitudeMethod`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AltitudeMethod.md">AltitudeMethod
   *      documentation</a>
   */
  public final static IBuiltInSymbol AltitudeMethod =
      S.initFinalSymbol("AltitudeMethod", ID.AltitudeMethod);

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

  /**
   * AnglePath({phi1, phi2, ...}) - returns the points formed by a turtle starting at `{0, 0}` and
   * angled at `0` degrees going through the turns given by angles`phi1, phi2, ...` and using
   * distance `1` for each step.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnglePath.md">AnglePath
   *      documentation</a>
   */
  public final static IBuiltInSymbol AnglePath = S.initFinalSymbol("AnglePath", ID.AnglePath);

  /**
   * AngleVector(phi) - returns the point at angle `phi` on the unit circle.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AngleVector.md">AngleVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol AngleVector = S.initFinalSymbol("AngleVector", ID.AngleVector);

  /**
   * Animate(x) - TODO describe `Animate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Animate.md">Animate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Animate = S.initFinalSymbol("Animate", ID.Animate);

  /**
   * AnimationDirection(x) - TODO describe `AnimationDirection`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnimationDirection.md">AnimationDirection
   *      documentation</a>
   */
  public final static IBuiltInSymbol AnimationDirection =
      S.initFinalSymbol("AnimationDirection", ID.AnimationDirection);

  /**
   * AnimationRate(x) - TODO describe `AnimationRate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnimationRate.md">AnimationRate
   *      documentation</a>
   */
  public final static IBuiltInSymbol AnimationRate =
      S.initFinalSymbol("AnimationRate", ID.AnimationRate);

  /**
   * AnimationRepetitions(x) - TODO describe `AnimationRepetitions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnimationRepetitions.md">AnimationRepetitions
   *      documentation</a>
   */
  public final static IBuiltInSymbol AnimationRepetitions =
      S.initFinalSymbol("AnimationRepetitions", ID.AnimationRepetitions);

  /**
   * AnimationRunning(x) - TODO describe `AnimationRunning`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnimationRunning.md">AnimationRunning
   *      documentation</a>
   */
  public final static IBuiltInSymbol AnimationRunning =
      S.initFinalSymbol("AnimationRunning", ID.AnimationRunning);

  /**
   * Animator(x) - TODO describe `Animator`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Animator.md">Animator
   *      documentation</a>
   */
  public final static IBuiltInSymbol Animator = S.initFinalSymbol("Animator", ID.Animator);

  public final static IBuiltInSymbol Annotation = S.initFinalSymbol("Annotation", ID.Annotation);

  /**
   * AnnotationRules(x) - TODO describe `AnnotationRules`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AnnotationRules.md">AnnotationRules
   *      documentation</a>
   */
  public final static IBuiltInSymbol AnnotationRules =
      S.initFinalSymbol("AnnotationRules", ID.AnnotationRules);

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

  public final static IBuiltInSymbol Annulus = S.initFinalSymbol("Annulus", ID.Annulus);

  /**
   * Antialiasing(x) - TODO describe `Antialiasing`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Antialiasing.md">Antialiasing
   *      documentation</a>
   */
  public final static IBuiltInSymbol Antialiasing =
      S.initFinalSymbol("Antialiasing", ID.Antialiasing);

  /**
   * AntihermitianMatrixQ(m) - returns `True` if `m` is a anti hermitian matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AntihermitianMatrixQ.md">AntihermitianMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol AntihermitianMatrixQ =
      S.initFinalSymbol("AntihermitianMatrixQ", ID.AntihermitianMatrixQ);

  public final static IBuiltInSymbol Antisymmetric =
      S.initFinalSymbol("Antisymmetric", ID.Antisymmetric);

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

  /**
   * Appearance(x) - TODO describe `Appearance`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Appearance.md">Appearance
   *      documentation</a>
   */
  public final static IBuiltInSymbol Appearance = S.initFinalSymbol("Appearance", ID.Appearance);

  /**
   * AppearanceElements(x) - TODO describe `AppearanceElements`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AppearanceElements.md">AppearanceElements
   *      documentation</a>
   */
  public final static IBuiltInSymbol AppearanceElements =
      S.initFinalSymbol("AppearanceElements", ID.AppearanceElements);

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
   * Around(x) - TODO describe `Around`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Around.md">Around
   *      documentation</a>
   */
  public final static IBuiltInSymbol Around = S.initFinalSymbol("Around", ID.Around);

  /**
   * AroundReplace(x) - TODO describe `AroundReplace`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AroundReplace.md">AroundReplace
   *      documentation</a>
   */
  public final static IBuiltInSymbol AroundReplace =
      S.initFinalSymbol("AroundReplace", ID.AroundReplace);

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

  public final static IBuiltInSymbol ArrayDot = S.initFinalSymbol("ArrayDot", ID.ArrayDot);

  public final static IBuiltInSymbol ArrayFlatten =
      S.initFinalSymbol("ArrayFlatten", ID.ArrayFlatten);

  /**
   * ArrayMesh(x) - TODO describe `ArrayMesh`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayMesh.md">ArrayMesh
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArrayMesh = S.initFinalSymbol("ArrayMesh", ID.ArrayMesh);

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
   * ArrayReduce(function, list-of-values, n) - returns the `list-of-values` structure reduced for
   * dimension `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ArrayReduce.md">ArrayReduce
   *      documentation</a>
   */
  public final static IBuiltInSymbol ArrayReduce = S.initFinalSymbol("ArrayReduce", ID.ArrayReduce);

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

  public final static IBuiltInSymbol ArraySymbol = S.initFinalSymbol("ArraySymbol", ID.ArraySymbol);

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
   * AstroAngularSeparation(x) - TODO describe `AstroAngularSeparation`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroAngularSeparation.md">AstroAngularSeparation
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroAngularSeparation =
      S.initFinalSymbol("AstroAngularSeparation", ID.AstroAngularSeparation);

  /**
   * AstroBackground(x) - TODO describe `AstroBackground`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroBackground.md">AstroBackground
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroBackground =
      S.initFinalSymbol("AstroBackground", ID.AstroBackground);

  /**
   * AstroCenter(x) - TODO describe `AstroCenter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroCenter.md">AstroCenter
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroCenter = S.initFinalSymbol("AstroCenter", ID.AstroCenter);

  /**
   * AstroDistance(x) - TODO describe `AstroDistance`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroDistance.md">AstroDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroDistance =
      S.initFinalSymbol("AstroDistance", ID.AstroDistance);

  /**
   * AstroGraphics(x) - TODO describe `AstroGraphics`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroGraphics.md">AstroGraphics
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroGraphics =
      S.initFinalSymbol("AstroGraphics", ID.AstroGraphics);

  /**
   * AstroGridLines(x) - TODO describe `AstroGridLines`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroGridLines.md">AstroGridLines
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroGridLines =
      S.initFinalSymbol("AstroGridLines", ID.AstroGridLines);

  /**
   * AstroGridLinesStyle(x) - TODO describe `AstroGridLinesStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroGridLinesStyle.md">AstroGridLinesStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroGridLinesStyle =
      S.initFinalSymbol("AstroGridLinesStyle", ID.AstroGridLinesStyle);

  /**
   * AstroPosition(x) - TODO describe `AstroPosition`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroPosition.md">AstroPosition
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroPosition =
      S.initFinalSymbol("AstroPosition", ID.AstroPosition);

  /**
   * AstroProjection(x) - TODO describe `AstroProjection`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroProjection.md">AstroProjection
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroProjection =
      S.initFinalSymbol("AstroProjection", ID.AstroProjection);

  /**
   * AstroRange(x) - TODO describe `AstroRange`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroRange.md">AstroRange
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroRange = S.initFinalSymbol("AstroRange", ID.AstroRange);

  /**
   * AstroRangePadding(x) - TODO describe `AstroRangePadding`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroRangePadding.md">AstroRangePadding
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroRangePadding =
      S.initFinalSymbol("AstroRangePadding", ID.AstroRangePadding);

  /**
   * AstroReferenceFrame(x) - TODO describe `AstroReferenceFrame`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroReferenceFrame.md">AstroReferenceFrame
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroReferenceFrame =
      S.initFinalSymbol("AstroReferenceFrame", ID.AstroReferenceFrame);

  /**
   * AstroRiseSet(x) - TODO describe `AstroRiseSet`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroRiseSet.md">AstroRiseSet
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroRiseSet =
      S.initFinalSymbol("AstroRiseSet", ID.AstroRiseSet);

  /**
   * AstroStyling(x) - TODO describe `AstroStyling`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroStyling.md">AstroStyling
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroStyling =
      S.initFinalSymbol("AstroStyling", ID.AstroStyling);

  /**
   * AstroSubpoint(x) - TODO describe `AstroSubpoint`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroSubpoint.md">AstroSubpoint
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroSubpoint =
      S.initFinalSymbol("AstroSubpoint", ID.AstroSubpoint);

  /**
   * AstroZoomLevel(x) - TODO describe `AstroZoomLevel`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AstroZoomLevel.md">AstroZoomLevel
   *      documentation</a>
   */
  public final static IBuiltInSymbol AstroZoomLevel =
      S.initFinalSymbol("AstroZoomLevel", ID.AstroZoomLevel);

  /**
   * Asymptotic(expression,{x,a,order}) - returns an asymptotic approximation for the `expression`
   * near `a` to order `order`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Asymptotic.md">Asymptotic
   *      documentation</a>
   */
  public final static IBuiltInSymbol Asymptotic = S.initFinalSymbol("Asymptotic", ID.Asymptotic);

  /**
   * AsymptoticDSolveValue(equation, f(x), {x,a,order}) - returns an approximation of order `order`
   * for the differential `equation` for the function `f(x)` and variable `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AsymptoticDSolveValue.md">AsymptoticDSolveValue
   *      documentation</a>
   */
  public final static IBuiltInSymbol AsymptoticDSolveValue =
      S.initFinalSymbol("AsymptoticDSolveValue", ID.AsymptoticDSolveValue);

  /**
   * AsymptoticIntegrate(integral, {x, a, order}) - returns an asymptotic approximation for the
   * `integral` to order `order`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AsymptoticIntegrate.md">AsymptoticIntegrate
   *      documentation</a>
   */
  public final static IBuiltInSymbol AsymptoticIntegrate =
      S.initFinalSymbol("AsymptoticIntegrate", ID.AsymptoticIntegrate);

  /**
   * AsymptoticRSolveValue(equation, f(x), {x,a,order}) - returns an approximation for the
   * difference `equation` for the function `f(x)` and variable `x` to order `order`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AsymptoticRSolveValue.md">AsymptoticRSolveValue
   *      documentation</a>
   */
  public final static IBuiltInSymbol AsymptoticRSolveValue =
      S.initFinalSymbol("AsymptoticRSolveValue", ID.AsymptoticRSolveValue);

  /**
   * AsymptoticSolve(equation, v->b, {x,a,order}) - returns an asymptotic approximation for the
   * `equation` to order `order`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AsymptoticSolve.md">AsymptoticSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol AsymptoticSolve =
      S.initFinalSymbol("AsymptoticSolve", ID.AsymptoticSolve);

  public final static IBuiltInSymbol Atom = S.initFinalSymbol("Atom", ID.Atom);

  /**
   * AtomCount(x) - TODO describe `AtomCount`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AtomCount.md">AtomCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol AtomCount = S.initFinalSymbol("AtomCount", ID.AtomCount);

  /**
   * AtomDiagramCoordinates(x) - TODO describe `AtomDiagramCoordinates`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AtomDiagramCoordinates.md">AtomDiagramCoordinates
   *      documentation</a>
   */
  public final static IBuiltInSymbol AtomDiagramCoordinates =
      S.initFinalSymbol("AtomDiagramCoordinates", ID.AtomDiagramCoordinates);

  public final static IBuiltInSymbol AtomList = S.initFinalSymbol("AtomList", ID.AtomList);

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

  /**
   * AutoAction(x) - TODO describe `AutoAction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AutoAction.md">AutoAction
   *      documentation</a>
   */
  public final static IBuiltInSymbol AutoAction = S.initFinalSymbol("AutoAction", ID.AutoAction);

  public final static IBuiltInSymbol Automatic = S.initFinalSymbol("Automatic", ID.Automatic);

  /**
   * AutorunSequencing(x) - TODO describe `AutorunSequencing`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AutorunSequencing.md">AutorunSequencing
   *      documentation</a>
   */
  public final static IBuiltInSymbol AutorunSequencing =
      S.initFinalSymbol("AutorunSequencing", ID.AutorunSequencing);

  public final static IBuiltInSymbol AvogadroConstant =
      S.initFinalSymbol("AvogadroConstant", ID.AvogadroConstant);

  public final static IBuiltInSymbol Axes = S.initFinalSymbol("Axes", ID.Axes);

  public final static IBuiltInSymbol AxesEdge = S.initFinalSymbol("AxesEdge", ID.AxesEdge);

  public final static IBuiltInSymbol AxesLabel = S.initFinalSymbol("AxesLabel", ID.AxesLabel);

  public final static IBuiltInSymbol AxesOrigin = S.initFinalSymbol("AxesOrigin", ID.AxesOrigin);

  public final static IBuiltInSymbol AxesStyle = S.initFinalSymbol("AxesStyle", ID.AxesStyle);

  public final static IBuiltInSymbol Axis = S.initFinalSymbol("Axis", ID.Axis);

  /**
   * AxisObject(x) - TODO describe `AxisObject`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/AxisObject.md">AxisObject
   *      documentation</a>
   */
  public final static IBuiltInSymbol AxisObject = S.initFinalSymbol("AxisObject", ID.AxisObject);

  public final static IBuiltInSymbol Background = S.initFinalSymbol("Background", ID.Background);

  /**
   * Backslash(x) - TODO describe `Backslash`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Backslash.md">Backslash
   *      documentation</a>
   */
  public final static IBuiltInSymbol Backslash = S.initFinalSymbol("Backslash", ID.Backslash);

  /**
   * Backsubstitution(x) - TODO describe `Backsubstitution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Backsubstitution.md">Backsubstitution
   *      documentation</a>
   */
  public final static IBuiltInSymbol Backsubstitution =
      S.initFinalSymbol("Backsubstitution", ID.Backsubstitution);

  public final static IBuiltInSymbol Ball = S.initFinalSymbol("Ball", ID.Ball);

  public final static IBuiltInSymbol Band = S.initFinalSymbol("Band", ID.Band);

  /**
   * BarChart(list-of-values, options) - plot a bar chart for a `list-of-values` with option
   * `BarOrigin->Bottom` or `BarOrigin->Bottom`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BarChart.md">BarChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol BarChart = S.initFinalSymbol("BarChart", ID.BarChart);

  /**
   * BarChart3D(x) - TODO describe `BarChart3D`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BarChart3D.md">BarChart3D
   *      documentation</a>
   */
  public final static IBuiltInSymbol BarChart3D = S.initFinalSymbol("BarChart3D", ID.BarChart3D);

  public final static IBuiltInSymbol BarLegend = S.initFinalSymbol("BarLegend", ID.BarLegend);

  public final static IBuiltInSymbol BarnesG = S.initFinalSymbol("BarnesG", ID.BarnesG);

  public final static IBuiltInSymbol BarOrigin = S.initFinalSymbol("BarOrigin", ID.BarOrigin);

  public final static IBuiltInSymbol BarSpacing = S.initFinalSymbol("BarSpacing", ID.BarSpacing);

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

  public final static IBuiltInSymbol BaselinePosition =
      S.initFinalSymbol("BaselinePosition", ID.BaselinePosition);

  public final static IBuiltInSymbol BaseStyle = S.initFinalSymbol("BaseStyle", ID.BaseStyle);

  /**
   * Because(x) - TODO describe `Because`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Because.md">Because
   *      documentation</a>
   */
  public final static IBuiltInSymbol Because = S.initFinalSymbol("Because", ID.Because);

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
   * BenfordDistribution(x) - TODO describe `BenfordDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BenfordDistribution.md">BenfordDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol BenfordDistribution =
      S.initFinalSymbol("BenfordDistribution", ID.BenfordDistribution);

  /**
   * BenktanderGibratDistribution(x) - TODO describe `BenktanderGibratDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BenktanderGibratDistribution.md">BenktanderGibratDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol BenktanderGibratDistribution =
      S.initFinalSymbol("BenktanderGibratDistribution", ID.BenktanderGibratDistribution);

  /**
   * BenktanderWeibullDistribution(x) - TODO describe `BenktanderWeibullDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BenktanderWeibullDistribution.md">BenktanderWeibullDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol BenktanderWeibullDistribution =
      S.initFinalSymbol("BenktanderWeibullDistribution", ID.BenktanderWeibullDistribution);

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

  /**
   * BetaBinomialDistribution(x) - TODO describe `BetaBinomialDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BetaBinomialDistribution.md">BetaBinomialDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol BetaBinomialDistribution =
      S.initFinalSymbol("BetaBinomialDistribution", ID.BetaBinomialDistribution);

  public final static IBuiltInSymbol BetaDistribution =
      S.initFinalSymbol("BetaDistribution", ID.BetaDistribution);

  /**
   * BetaPrimeDistribution(x) - TODO describe `BetaPrimeDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BetaPrimeDistribution.md">BetaPrimeDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol BetaPrimeDistribution =
      S.initFinalSymbol("BetaPrimeDistribution", ID.BetaPrimeDistribution);

  public final static IBuiltInSymbol BetaRegularized =
      S.initFinalSymbol("BetaRegularized", ID.BetaRegularized);

  /**
   * Between(expr, {min, max}) - equivalent to `(min <= expr) && (expr <= max)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Between.md">Between
   *      documentation</a>
   */
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

  public final static IBuiltInSymbol BezierCurve = S.initFinalSymbol("BezierCurve", ID.BezierCurve);

  /**
   * BezierFunction(list-of-control-points) - Bezier curve constructed by `list-of-control-points`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BezierFunction.md">BezierFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol BezierFunction =
      S.initFinalSymbol("BezierFunction", ID.BezierFunction);

  /**
   * BilateralFilter(x) - TODO describe `BilateralFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BilateralFilter.md">BilateralFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol BilateralFilter =
      S.initFinalSymbol("BilateralFilter", ID.BilateralFilter);

  /**
   * Binarize(x) - TODO describe `Binarize`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Binarize.md">Binarize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Binarize = S.initFinalSymbol("Binarize", ID.Binarize);

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
   * BinCounts(list, widthOfBin) - count the number of elements, if `list`, is divided into
   * successive bins with width `widthOfBin`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BinCounts.md">BinCounts
   *      documentation</a>
   */
  public final static IBuiltInSymbol BinCounts = S.initFinalSymbol("BinCounts", ID.BinCounts);

  public final static IBuiltInSymbol BinLists = S.initFinalSymbol("BinLists", ID.BinLists);

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

  public final static IBuiltInSymbol BinormalDistribution =
      S.initFinalSymbol("BinormalDistribution", ID.BinormalDistribution);

  public final static IBuiltInSymbol BioSequence = S.initFinalSymbol("BioSequence", ID.BioSequence);

  /**
   * BioSequenceBackTranslateList(x) - TODO describe `BioSequenceBackTranslateList`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BioSequenceBackTranslateList.md">BioSequenceBackTranslateList
   *      documentation</a>
   */
  public final static IBuiltInSymbol BioSequenceBackTranslateList =
      S.initFinalSymbol("BioSequenceBackTranslateList", ID.BioSequenceBackTranslateList);

  /**
   * BioSequenceComplement(x) - TODO describe `BioSequenceComplement`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BioSequenceComplement.md">BioSequenceComplement
   *      documentation</a>
   */
  public final static IBuiltInSymbol BioSequenceComplement =
      S.initFinalSymbol("BioSequenceComplement", ID.BioSequenceComplement);

  /**
   * BioSequenceInstances(x) - TODO describe `BioSequenceInstances`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BioSequenceInstances.md">BioSequenceInstances
   *      documentation</a>
   */
  public final static IBuiltInSymbol BioSequenceInstances =
      S.initFinalSymbol("BioSequenceInstances", ID.BioSequenceInstances);

  /**
   * BioSequenceModify(x) - TODO describe `BioSequenceModify`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BioSequenceModify.md">BioSequenceModify
   *      documentation</a>
   */
  public final static IBuiltInSymbol BioSequenceModify =
      S.initFinalSymbol("BioSequenceModify", ID.BioSequenceModify);

  public final static IBuiltInSymbol BioSequenceQ =
      S.initFinalSymbol("BioSequenceQ", ID.BioSequenceQ);

  /**
   * BioSequenceReverseComplement(x) - TODO describe `BioSequenceReverseComplement`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BioSequenceReverseComplement.md">BioSequenceReverseComplement
   *      documentation</a>
   */
  public final static IBuiltInSymbol BioSequenceReverseComplement =
      S.initFinalSymbol("BioSequenceReverseComplement", ID.BioSequenceReverseComplement);

  public final static IBuiltInSymbol BioSequenceTranscribe =
      S.initFinalSymbol("BioSequenceTranscribe", ID.BioSequenceTranscribe);

  public final static IBuiltInSymbol BioSequenceTranslate =
      S.initFinalSymbol("BioSequenceTranslate", ID.BioSequenceTranslate);

  /**
   * BipartiteGraphQ(expr) - test if `expr` is a bipartite graph object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BipartiteGraphQ.md">BipartiteGraphQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol BipartiteGraphQ =
      S.initFinalSymbol("BipartiteGraphQ", ID.BipartiteGraphQ);

  /**
   * BitAnd(int1, int2, int3, ...) - returns the bitwise `AND` of the integer numbers `int1, int2,
   * int3, ...`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BitAnd.md">BitAnd
   *      documentation</a>
   */
  public final static IBuiltInSymbol BitAnd = S.initFinalSymbol("BitAnd", ID.BitAnd);

  /**
   * BitClear(i, n) - clears the `n`th bit in the integer `i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BitClear.md">BitClear
   *      documentation</a>
   */
  public final static IBuiltInSymbol BitClear = S.initFinalSymbol("BitClear", ID.BitClear);

  /**
   * BitFlip(i, n) - flips the `n`th bit in the integer `i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BitFlip.md">BitFlip
   *      documentation</a>
   */
  public final static IBuiltInSymbol BitFlip = S.initFinalSymbol("BitFlip", ID.BitFlip);

  /**
   * BitGet(i, n) - gets the `n`th bit in the integer `i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BitGet.md">BitGet
   *      documentation</a>
   */
  public final static IBuiltInSymbol BitGet = S.initFinalSymbol("BitGet", ID.BitGet);

  /**
   * BitLength(x) - gives the number of bits needed to represent the integer `x`. The sign of `x` is
   * ignored.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BitLength.md">BitLength
   *      documentation</a>
   */
  public final static IBuiltInSymbol BitLength = S.initFinalSymbol("BitLength", ID.BitLength);

  /**
   * BitNot(integer-value) - returns the bitwise `NOT` of the integer number `integer-value`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BitNot.md">BitNot
   *      documentation</a>
   */
  public final static IBuiltInSymbol BitNot = S.initFinalSymbol("BitNot", ID.BitNot);

  /**
   * BitOr(int1, int2, int3, ...) - returns the bitwise `OR` of the integer numbers `int1, int2,
   * int3, ...`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BitOr.md">BitOr
   *      documentation</a>
   */
  public final static IBuiltInSymbol BitOr = S.initFinalSymbol("BitOr", ID.BitOr);

  /**
   * BitSet(i, b) - set the `b`th bit in the integer `i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BitSet.md">BitSet
   *      documentation</a>
   */
  public final static IBuiltInSymbol BitSet = S.initFinalSymbol("BitSet", ID.BitSet);

  /**
   * BitXor(int1, int2, int3, ...) - returns the bitwise `XOR` of the integer numbers `int1, int2,
   * int3, ...`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BitXor.md">BitXor
   *      documentation</a>
   */
  public final static IBuiltInSymbol BitXor = S.initFinalSymbol("BitXor", ID.BitXor);

  /**
   * Black - RGB color value for the color black
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Black.md">Black
   *      documentation</a>
   */
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
   * Blend(x) - TODO describe `Blend`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Blend.md">Blend
   *      documentation</a>
   */
  public final static IBuiltInSymbol Blend = S.initFinalSymbol("Blend", ID.Blend);

  /**
   * Block({list_of_local_variables}, expr ) - evaluates `expr` for the `list_of_local_variables`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Block.md">Block
   *      documentation</a>
   */
  public final static IBuiltInSymbol Block = S.initFinalSymbol("Block", ID.Block);

  /**
   * Blue - RGB color value for the color blue
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Blue.md">Blue
   *      documentation</a>
   */
  public final static IBuiltInSymbol Blue = S.initFinalSymbol("Blue", ID.Blue);

  /**
   * Blur(x) - TODO describe `Blur`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Blur.md">Blur
   *      documentation</a>
   */
  public final static IBuiltInSymbol Blur = S.initFinalSymbol("Blur", ID.Blur);

  public final static IBuiltInSymbol BohrRadius = S.initFinalSymbol("BohrRadius", ID.BohrRadius);

  /**
   * Bold(x) - TODO describe `Bold`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Bold.md">Bold
   *      documentation</a>
   */
  public final static IBuiltInSymbol Bold = S.initFinalSymbol("Bold", ID.Bold);

  public final static IBuiltInSymbol Bond = S.initFinalSymbol("Bond", ID.Bond);

  /**
   * BondCount(x) - TODO describe `BondCount`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BondCount.md">BondCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol BondCount = S.initFinalSymbol("BondCount", ID.BondCount);

  public final static IBuiltInSymbol BondList = S.initFinalSymbol("BondList", ID.BondList);

  /**
   * Bookmarks(x) - TODO describe `Bookmarks`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Bookmarks.md">Bookmarks
   *      documentation</a>
   */
  public final static IBuiltInSymbol Bookmarks = S.initFinalSymbol("Bookmarks", ID.Bookmarks);

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
   * BooleanCountingFunction(spec, vars) - Returns a boolean function (in disjunctive normal form)
   * in the given `vars` which evaluates to `True` exactly when the number of `True` variables
   * matches `spec`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BooleanCountingFunction.md">BooleanCountingFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol BooleanCountingFunction =
      S.initFinalSymbol("BooleanCountingFunction", ID.BooleanCountingFunction);

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

  /**
   * BorelTannerDistribution(x) - TODO describe `BorelTannerDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BorelTannerDistribution.md">BorelTannerDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol BorelTannerDistribution =
      S.initFinalSymbol("BorelTannerDistribution", ID.BorelTannerDistribution);

  public final static IBuiltInSymbol Bottom = S.initFinalSymbol("Bottom", ID.Bottom);

  /**
   * BottomHatTransform(x) - TODO describe `BottomHatTransform`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BottomHatTransform.md">BottomHatTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol BottomHatTransform =
      S.initFinalSymbol("BottomHatTransform", ID.BottomHatTransform);

  /**
   * BoundaryMeshRegion(x) - TODO describe `BoundaryMeshRegion`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BoundaryMeshRegion.md">BoundaryMeshRegion
   *      documentation</a>
   */
  public final static IBuiltInSymbol BoundaryMeshRegion =
      S.initFinalSymbol("BoundaryMeshRegion", ID.BoundaryMeshRegion);

  /**
   * BoundaryMeshRegionQ(x) - TODO describe `BoundaryMeshRegionQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BoundaryMeshRegionQ.md">BoundaryMeshRegionQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol BoundaryMeshRegionQ =
      S.initFinalSymbol("BoundaryMeshRegionQ", ID.BoundaryMeshRegionQ);

  /**
   * BoundaryStyle(x) - TODO describe `BoundaryStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BoundaryStyle.md">BoundaryStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol BoundaryStyle =
      S.initFinalSymbol("BoundaryStyle", ID.BoundaryStyle);

  /**
   * BoundedRegionQ(x) - TODO describe `BoundedRegionQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BoundedRegionQ.md">BoundedRegionQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol BoundedRegionQ =
      S.initFinalSymbol("BoundedRegionQ", ID.BoundedRegionQ);

  public final static IBuiltInSymbol BoundingRegion =
      S.initFinalSymbol("BoundingRegion", ID.BoundingRegion);

  public final static IBuiltInSymbol Boxed = S.initFinalSymbol("Boxed", ID.Boxed);

  /**
   * BoxMatrix(radius, dimension) - gives a matrix of `2*radius+1` size inside a `dimension x
   * dimension` matrix
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BoxMatrix.md">BoxMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol BoxMatrix = S.initFinalSymbol("BoxMatrix", ID.BoxMatrix);

  public final static IBuiltInSymbol BoxRatios = S.initFinalSymbol("BoxRatios", ID.BoxRatios);

  /**
   * BoxStyle(x) - TODO describe `BoxStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BoxStyle.md">BoxStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol BoxStyle = S.initFinalSymbol("BoxStyle", ID.BoxStyle);

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

  /**
   * Brown - RGB color value for the color brown
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Brown.md">Brown
   *      documentation</a>
   */
  public final static IBuiltInSymbol Brown = S.initFinalSymbol("Brown", ID.Brown);

  public final static IBuiltInSymbol BrownianBridgeProcess =
      S.initFinalSymbol("BrownianBridgeProcess", ID.BrownianBridgeProcess);

  public final static IBuiltInSymbol BSplineCurve =
      S.initFinalSymbol("BSplineCurve", ID.BSplineCurve);

  public final static IBuiltInSymbol BSplineFunction =
      S.initFinalSymbol("BSplineFunction", ID.BSplineFunction);

  /**
   * BSplineSurface(x) - TODO describe `BSplineSurface`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BSplineSurface.md">BSplineSurface
   *      documentation</a>
   */
  public final static IBuiltInSymbol BSplineSurface =
      S.initFinalSymbol("BSplineSurface", ID.BSplineSurface);

  /**
   * BubbleChart(x) - TODO describe `BubbleChart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/BubbleChart.md">BubbleChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol BubbleChart = S.initFinalSymbol("BubbleChart", ID.BubbleChart);

  public final static IBuiltInSymbol Button = S.initFinalSymbol("Button", ID.Button);

  /**
   * ButtonBar(x) - TODO describe `ButtonBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ButtonBar.md">ButtonBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol ButtonBar = S.initFinalSymbol("ButtonBar", ID.ButtonBar);

  public final static IBuiltInSymbol Byte = S.initFinalSymbol("Byte", ID.Byte);

  /**
   * ByteArray({list-of-byte-values}) - converts the `list-of-byte-values` into a byte array. The
   * argument in `ByteArray` should be a vector of unsigned byte values or a Base64-encoded string.
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
   * CachedValue(x) - TODO describe `CachedValue`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CachedValue.md">CachedValue
   *      documentation</a>
   */
  public final static IBuiltInSymbol CachedValue = S.initFinalSymbol("CachedValue", ID.CachedValue);

  /**
   * CalendarConvert(x) - TODO describe `CalendarConvert`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CalendarConvert.md">CalendarConvert
   *      documentation</a>
   */
  public final static IBuiltInSymbol CalendarConvert =
      S.initFinalSymbol("CalendarConvert", ID.CalendarConvert);

  /**
   * CalendarType(x) - TODO describe `CalendarType`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CalendarType.md">CalendarType
   *      documentation</a>
   */
  public final static IBuiltInSymbol CalendarType =
      S.initFinalSymbol("CalendarType", ID.CalendarType);

  /**
   * Callout(x) - TODO describe `Callout`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Callout.md">Callout
   *      documentation</a>
   */
  public final static IBuiltInSymbol Callout = S.initFinalSymbol("Callout", ID.Callout);

  /**
   * CalloutMarker(x) - TODO describe `CalloutMarker`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CalloutMarker.md">CalloutMarker
   *      documentation</a>
   */
  public final static IBuiltInSymbol CalloutMarker =
      S.initFinalSymbol("CalloutMarker", ID.CalloutMarker);

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
   * CandlestickChart(x) - TODO describe `CandlestickChart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CandlestickChart.md">CandlestickChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol CandlestickChart =
      S.initFinalSymbol("CandlestickChart", ID.CandlestickChart);

  /**
   * CantorMesh(x) - TODO describe `CantorMesh`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CantorMesh.md">CantorMesh
   *      documentation</a>
   */
  public final static IBuiltInSymbol CantorMesh = S.initFinalSymbol("CantorMesh", ID.CantorMesh);

  /**
   * Cap(x) - TODO describe `Cap`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cap.md">Cap
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cap = S.initFinalSymbol("Cap", ID.Cap);

  public final static IBuiltInSymbol CapForm = S.initFinalSymbol("CapForm", ID.CapForm);

  /**
   * CapitalDifferentialD(x) - TODO describe `CapitalDifferentialD`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CapitalDifferentialD.md">CapitalDifferentialD
   *      documentation</a>
   */
  public final static IBuiltInSymbol CapitalDifferentialD =
      S.initFinalSymbol("CapitalDifferentialD", ID.CapitalDifferentialD);

  /**
   * CapsuleShape(x) - TODO describe `CapsuleShape`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CapsuleShape.md">CapsuleShape
   *      documentation</a>
   */
  public final static IBuiltInSymbol CapsuleShape =
      S.initFinalSymbol("CapsuleShape", ID.CapsuleShape);

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

  /**
   * CelestialSystem(x) - TODO describe `CelestialSystem`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CelestialSystem.md">CelestialSystem
   *      documentation</a>
   */
  public final static IBuiltInSymbol CelestialSystem =
      S.initFinalSymbol("CelestialSystem", ID.CelestialSystem);

  public final static IBuiltInSymbol Cell = S.initFinalSymbol("Cell", ID.Cell);

  /**
   * CellularAutomaton(rule-or-pure-function, initial-conndition, steps) - create a list of the
   * evolution `steps` of the cellular automaton from the `rule-or-pure-function` specification for
   * the `initial-condition`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CellularAutomaton.md">CellularAutomaton
   *      documentation</a>
   */
  public final static IBuiltInSymbol CellularAutomaton =
      S.initFinalSymbol("CellularAutomaton", ID.CellularAutomaton);

  /**
   * CensoredDistribution(x) - TODO describe `CensoredDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CensoredDistribution.md">CensoredDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol CensoredDistribution =
      S.initFinalSymbol("CensoredDistribution", ID.CensoredDistribution);

  public final static IBuiltInSymbol Center = S.initFinalSymbol("Center", ID.Center);

  public final static IBuiltInSymbol CenterDot = S.initFinalSymbol("CenterDot", ID.CenterDot);

  /**
   * CentralFeature(list) - returns the central feature of a `list` or a `list-of-rules`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CentralFeature.md">CentralFeature
   *      documentation</a>
   */
  public final static IBuiltInSymbol CentralFeature =
      S.initFinalSymbol("CentralFeature", ID.CentralFeature);

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

  /**
   * CentralMomentGeneratingFunction(x) - TODO describe `CentralMomentGeneratingFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CentralMomentGeneratingFunction.md">CentralMomentGeneratingFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol CentralMomentGeneratingFunction =
      S.initFinalSymbol("CentralMomentGeneratingFunction", ID.CentralMomentGeneratingFunction);

  public final static IBuiltInSymbol CForm = S.initFinalSymbol("CForm", ID.CForm);

  public final static IBuiltInSymbol Character = S.initFinalSymbol("Character", ID.Character);

  public final static IBuiltInSymbol CharacterEncoding =
      S.initFinalSymbol("CharacterEncoding", ID.CharacterEncoding);

  /**
   * CharacteristicFunction(x) - TODO describe `CharacteristicFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CharacteristicFunction.md">CharacteristicFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol CharacteristicFunction =
      S.initFinalSymbol("CharacteristicFunction", ID.CharacteristicFunction);

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
   * ChartBaseStyle(x) - TODO describe `ChartBaseStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChartBaseStyle.md">ChartBaseStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol ChartBaseStyle =
      S.initFinalSymbol("ChartBaseStyle", ID.ChartBaseStyle);

  /**
   * ChartElementFunction(x) - TODO describe `ChartElementFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChartElementFunction.md">ChartElementFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol ChartElementFunction =
      S.initFinalSymbol("ChartElementFunction", ID.ChartElementFunction);

  /**
   * ChartElements(x) - TODO describe `ChartElements`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChartElements.md">ChartElements
   *      documentation</a>
   */
  public final static IBuiltInSymbol ChartElements =
      S.initFinalSymbol("ChartElements", ID.ChartElements);

  public final static IBuiltInSymbol ChartLabels = S.initFinalSymbol("ChartLabels", ID.ChartLabels);

  /**
   * ChartLayout(x) - TODO describe `ChartLayout`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChartLayout.md">ChartLayout
   *      documentation</a>
   */
  public final static IBuiltInSymbol ChartLayout = S.initFinalSymbol("ChartLayout", ID.ChartLayout);

  public final static IBuiltInSymbol ChartLegends =
      S.initFinalSymbol("ChartLegends", ID.ChartLegends);

  public final static IBuiltInSymbol ChartStyle = S.initFinalSymbol("ChartStyle", ID.ChartStyle);

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
   * Checkbox(x) - TODO describe `Checkbox`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Checkbox.md">Checkbox
   *      documentation</a>
   */
  public final static IBuiltInSymbol Checkbox = S.initFinalSymbol("Checkbox", ID.Checkbox);

  /**
   * CheckboxBar(x) - TODO describe `CheckboxBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CheckboxBar.md">CheckboxBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol CheckboxBar = S.initFinalSymbol("CheckboxBar", ID.CheckboxBar);

  /**
   * ChemicalConvert(x) - TODO describe `ChemicalConvert`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChemicalConvert.md">ChemicalConvert
   *      documentation</a>
   */
  public final static IBuiltInSymbol ChemicalConvert =
      S.initFinalSymbol("ChemicalConvert", ID.ChemicalConvert);

  /**
   * ChemicalFormula(x) - TODO describe `ChemicalFormula`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChemicalFormula.md">ChemicalFormula
   *      documentation</a>
   */
  public final static IBuiltInSymbol ChemicalFormula =
      S.initFinalSymbol("ChemicalFormula", ID.ChemicalFormula);

  /**
   * ChemicalReaction(x) - TODO describe `ChemicalReaction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ChemicalReaction.md">ChemicalReaction
   *      documentation</a>
   */
  public final static IBuiltInSymbol ChemicalReaction =
      S.initFinalSymbol("ChemicalReaction", ID.ChemicalReaction);

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

  public final static IBuiltInSymbol ChromaticPolynomial =
      S.initFinalSymbol("ChromaticPolynomial", ID.ChromaticPolynomial);

  public final static IBuiltInSymbol Circle = S.initFinalSymbol("Circle", ID.Circle);

  public final static IBuiltInSymbol CircleDot = S.initFinalSymbol("CircleDot", ID.CircleDot);

  /**
   * CircleMinus(x) - TODO describe `CircleMinus`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CircleMinus.md">CircleMinus
   *      documentation</a>
   */
  public final static IBuiltInSymbol CircleMinus = S.initFinalSymbol("CircleMinus", ID.CircleMinus);

  /**
   * CirclePlus(x) - TODO describe `CirclePlus`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CirclePlus.md">CirclePlus
   *      documentation</a>
   */
  public final static IBuiltInSymbol CirclePlus = S.initFinalSymbol("CirclePlus", ID.CirclePlus);

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
   * CircularArcThrough(x) - TODO describe `CircularArcThrough`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CircularArcThrough.md">CircularArcThrough
   *      documentation</a>
   */
  public final static IBuiltInSymbol CircularArcThrough =
      S.initFinalSymbol("CircularArcThrough", ID.CircularArcThrough);

  /**
   * Circumsphere(x) - TODO describe `Circumsphere`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Circumsphere.md">Circumsphere
   *      documentation</a>
   */
  public final static IBuiltInSymbol Circumsphere =
      S.initFinalSymbol("Circumsphere", ID.Circumsphere);

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
   * ClickPane(x) - TODO describe `ClickPane`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClickPane.md">ClickPane
   *      documentation</a>
   */
  public final static IBuiltInSymbol ClickPane = S.initFinalSymbol("ClickPane", ID.ClickPane);

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
   * ClippingStyle(x) - TODO describe `ClippingStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClippingStyle.md">ClippingStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol ClippingStyle =
      S.initFinalSymbol("ClippingStyle", ID.ClippingStyle);

  /**
   * ClipPlanes(x) - TODO describe `ClipPlanes`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClipPlanes.md">ClipPlanes
   *      documentation</a>
   */
  public final static IBuiltInSymbol ClipPlanes = S.initFinalSymbol("ClipPlanes", ID.ClipPlanes);

  /**
   * ClipPlanesStyle(x) - TODO describe `ClipPlanesStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ClipPlanesStyle.md">ClipPlanesStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol ClipPlanesStyle =
      S.initFinalSymbol("ClipPlanesStyle", ID.ClipPlanesStyle);

  /**
   * Clock(x) - TODO describe `Clock`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Clock.md">Clock
   *      documentation</a>
   */
  public final static IBuiltInSymbol Clock = S.initFinalSymbol("Clock", ID.Clock);

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

  /**
   * Closing(x) - TODO describe `Closing`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Closing.md">Closing
   *      documentation</a>
   */
  public final static IBuiltInSymbol Closing = S.initFinalSymbol("Closing", ID.Closing);

  public final static IBuiltInSymbol CMYKColor = S.initFinalSymbol("CMYKColor", ID.CMYKColor);

  /**
   * Coefficient(polynomial, variable, exponent) - get the coefficient of `variable^exponent` in
   * `polynomial`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Coefficient.md">Coefficient
   *      documentation</a>
   */
  public final static IBuiltInSymbol Coefficient = S.initFinalSymbol("Coefficient", ID.Coefficient);

  /**
   * CoefficientArrays(list-of-polynomials, list-of-variables) - returns the sparse arrays of
   * coefficients of the `list-of-variables` for the `list-of-polynomials`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CoefficientArrays.md">CoefficientArrays
   *      documentation</a>
   */
  public final static IBuiltInSymbol CoefficientArrays =
      S.initFinalSymbol("CoefficientArrays", ID.CoefficientArrays);

  public final static IBuiltInSymbol CoefficientDomain =
      S.initFinalSymbol("CoefficientDomain", ID.CoefficientDomain);

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

  /**
   * ColorCombine(x) - TODO describe `ColorCombine`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ColorCombine.md">ColorCombine
   *      documentation</a>
   */
  public final static IBuiltInSymbol ColorCombine =
      S.initFinalSymbol("ColorCombine", ID.ColorCombine);

  /**
   * ColorConvert(x) - TODO describe `ColorConvert`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ColorConvert.md">ColorConvert
   *      documentation</a>
   */
  public final static IBuiltInSymbol ColorConvert =
      S.initFinalSymbol("ColorConvert", ID.ColorConvert);

  public final static IBuiltInSymbol ColorData = S.initFinalSymbol("ColorData", ID.ColorData);

  public final static IBuiltInSymbol ColorDataFunction =
      S.initFinalSymbol("ColorDataFunction", ID.ColorDataFunction);

  /**
   * ColorDistance(x) - TODO describe `ColorDistance`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ColorDistance.md">ColorDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol ColorDistance =
      S.initFinalSymbol("ColorDistance", ID.ColorDistance);

  public final static IBuiltInSymbol ColorFunction =
      S.initFinalSymbol("ColorFunction", ID.ColorFunction);

  public final static IBuiltInSymbol ColorFunctionScaling =
      S.initFinalSymbol("ColorFunctionScaling", ID.ColorFunctionScaling);

  /**
   * ColorNegate(x) - TODO describe `ColorNegate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ColorNegate.md">ColorNegate
   *      documentation</a>
   */
  public final static IBuiltInSymbol ColorNegate = S.initFinalSymbol("ColorNegate", ID.ColorNegate);

  /**
   * ColorQuantize(x) - TODO describe `ColorQuantize`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ColorQuantize.md">ColorQuantize
   *      documentation</a>
   */
  public final static IBuiltInSymbol ColorQuantize =
      S.initFinalSymbol("ColorQuantize", ID.ColorQuantize);

  /**
   * ColorReplace(x) - TODO describe `ColorReplace`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ColorReplace.md">ColorReplace
   *      documentation</a>
   */
  public final static IBuiltInSymbol ColorReplace =
      S.initFinalSymbol("ColorReplace", ID.ColorReplace);

  /**
   * ColorRules(x) - TODO describe `ColorRules`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ColorRules.md">ColorRules
   *      documentation</a>
   */
  public final static IBuiltInSymbol ColorRules = S.initFinalSymbol("ColorRules", ID.ColorRules);

  /**
   * ColorSeparate(x) - TODO describe `ColorSeparate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ColorSeparate.md">ColorSeparate
   *      documentation</a>
   */
  public final static IBuiltInSymbol ColorSeparate =
      S.initFinalSymbol("ColorSeparate", ID.ColorSeparate);

  /**
   * ColorSetter(x) - TODO describe `ColorSetter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ColorSetter.md">ColorSetter
   *      documentation</a>
   */
  public final static IBuiltInSymbol ColorSetter = S.initFinalSymbol("ColorSetter", ID.ColorSetter);

  /**
   * ColorSlider(x) - TODO describe `ColorSlider`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ColorSlider.md">ColorSlider
   *      documentation</a>
   */
  public final static IBuiltInSymbol ColorSlider = S.initFinalSymbol("ColorSlider", ID.ColorSlider);

  public final static IBuiltInSymbol ColorSpace = S.initFinalSymbol("ColorSpace", ID.ColorSpace);

  public final static IBuiltInSymbol Column = S.initFinalSymbol("Column", ID.Column);

  /**
   * ColumnAlignments(x) - TODO describe `ColumnAlignments`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ColumnAlignments.md">ColumnAlignments
   *      documentation</a>
   */
  public final static IBuiltInSymbol ColumnAlignments =
      S.initFinalSymbol("ColumnAlignments", ID.ColumnAlignments);

  /**
   * Commonest(dataValueList) - the mode of a list of data values is the value that appears most
   * often.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Commonest.md">Commonest
   *      documentation</a>
   */
  public final static IBuiltInSymbol Commonest = S.initFinalSymbol("Commonest", ID.Commonest);

  /**
   * CommonestFilter(x) - TODO describe `CommonestFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CommonestFilter.md">CommonestFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol CommonestFilter =
      S.initFinalSymbol("CommonestFilter", ID.CommonestFilter);

  /**
   * CommonUnits(x) - TODO describe `CommonUnits`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CommonUnits.md">CommonUnits
   *      documentation</a>
   */
  public final static IBuiltInSymbol CommonUnits = S.initFinalSymbol("CommonUnits", ID.CommonUnits);

  public final static IBuiltInSymbol CompatibleUnitQ =
      S.initFinalSymbol("CompatibleUnitQ", ID.CompatibleUnitQ);

  /**
   * CompilationOptions - is an option for `Compile` which sets how the expression is compiled.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompilationOptions.md">CompilationOptions
   *      documentation</a>
   */
  public final static IBuiltInSymbol CompilationOptions =
      S.initFinalSymbol("CompilationOptions", ID.CompilationOptions);

  /**
   * CompilationTarget - is an option for `Compile` which names the target the code is generated
   * for.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompilationTarget.md">CompilationTarget
   *      documentation</a>
   */
  public final static IBuiltInSymbol CompilationTarget =
      S.initFinalSymbol("CompilationTarget", ID.CompilationTarget);

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

  public final static IBuiltInSymbol CompleteGraphQ =
      S.initFinalSymbol("CompleteGraphQ", ID.CompleteGraphQ);

  /**
   * CompleteKaryTree(level) - create a binary tree graph with `level` levels.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CompleteKaryTree.md">CompleteKaryTree
   *      documentation</a>
   */
  public final static IBuiltInSymbol CompleteKaryTree =
      S.initFinalSymbol("CompleteKaryTree", ID.CompleteKaryTree);

  /**
   * Complex - is the head of complex numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Complex.md">Complex
   *      documentation</a>
   */
  public final static IBuiltInSymbol Complex = S.initFinalSymbol("Complex", ID.Complex);

  /**
   * ComplexArrayPlot(x) - TODO describe `ComplexArrayPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexArrayPlot.md">ComplexArrayPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComplexArrayPlot =
      S.initFinalSymbol("ComplexArrayPlot", ID.ComplexArrayPlot);

  /**
   * ComplexContourPlot(x) - TODO describe `ComplexContourPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexContourPlot.md">ComplexContourPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComplexContourPlot =
      S.initFinalSymbol("ComplexContourPlot", ID.ComplexContourPlot);

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
   * ComplexListPlot(x) - TODO describe `ComplexListPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexListPlot.md">ComplexListPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComplexListPlot =
      S.initFinalSymbol("ComplexListPlot", ID.ComplexListPlot);

  public final static IBuiltInSymbol ComplexPlot = S.initFinalSymbol("ComplexPlot", ID.ComplexPlot);

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
   * ComplexRegionPlot(x) - TODO describe `ComplexRegionPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexRegionPlot.md">ComplexRegionPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComplexRegionPlot =
      S.initFinalSymbol("ComplexRegionPlot", ID.ComplexRegionPlot);

  /**
   * ComplexStreamPlot(x) - TODO describe `ComplexStreamPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexStreamPlot.md">ComplexStreamPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComplexStreamPlot =
      S.initFinalSymbol("ComplexStreamPlot", ID.ComplexStreamPlot);

  /**
   * ComplexVectorPlot(x) - TODO describe `ComplexVectorPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComplexVectorPlot.md">ComplexVectorPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComplexVectorPlot =
      S.initFinalSymbol("ComplexVectorPlot", ID.ComplexVectorPlot);

  /**
   * ComponentMeasurements(x) - TODO describe `ComponentMeasurements`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ComponentMeasurements.md">ComponentMeasurements
   *      documentation</a>
   */
  public final static IBuiltInSymbol ComponentMeasurements =
      S.initFinalSymbol("ComponentMeasurements", ID.ComponentMeasurements);

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
   * Compress(expression) - the `Compress` function creates a compressed, string-based
   * representation of any expression. The output string contains the compressed data of the
   * serialized expression. This string can be stored or transmitted, and the original expression
   * can be fully reconstructed using the `Uncompress` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Compress.md">Compress
   *      documentation</a>
   */
  public final static IBuiltInSymbol Compress = S.initFinalSymbol("Compress", ID.Compress);

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

  /**
   * Conditioned(x) - TODO describe `Conditioned`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Conditioned.md">Conditioned
   *      documentation</a>
   */
  public final static IBuiltInSymbol Conditioned = S.initFinalSymbol("Conditioned", ID.Conditioned);

  public final static IBuiltInSymbol Cone = S.initFinalSymbol("Cone", ID.Cone);

  /**
   * Congruent(x) - TODO describe `Congruent`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Congruent.md">Congruent
   *      documentation</a>
   */
  public final static IBuiltInSymbol Congruent = S.initFinalSymbol("Congruent", ID.Congruent);

  /**
   * ConicHullRegion(x) - TODO describe `ConicHullRegion`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConicHullRegion.md">ConicHullRegion
   *      documentation</a>
   */
  public final static IBuiltInSymbol ConicHullRegion =
      S.initFinalSymbol("ConicHullRegion", ID.ConicHullRegion);

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

  /**
   * ConnectedComponents(x) - TODO describe `ConnectedComponents`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConnectedComponents.md">ConnectedComponents
   *      documentation</a>
   */
  public final static IBuiltInSymbol ConnectedComponents =
      S.initFinalSymbol("ConnectedComponents", ID.ConnectedComponents);

  public final static IBuiltInSymbol ConnectedGraphComponents =
      S.initFinalSymbol("ConnectedGraphComponents", ID.ConnectedGraphComponents);

  /**
   * ConnectedGraphQ(graph) - returns `True` if the `graph` is strongly connected, which means that
   * every vertex is reachable from every other vertex.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConnectedGraphQ.md">ConnectedGraphQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ConnectedGraphQ =
      S.initFinalSymbol("ConnectedGraphQ", ID.ConnectedGraphQ);

  /**
   * ConnectedMoleculeComponents(x) - TODO describe `ConnectedMoleculeComponents`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConnectedMoleculeComponents.md">ConnectedMoleculeComponents
   *      documentation</a>
   */
  public final static IBuiltInSymbol ConnectedMoleculeComponents =
      S.initFinalSymbol("ConnectedMoleculeComponents", ID.ConnectedMoleculeComponents);

  /**
   * ConnectedMoleculeQ(x) - TODO describe `ConnectedMoleculeQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConnectedMoleculeQ.md">ConnectedMoleculeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ConnectedMoleculeQ =
      S.initFinalSymbol("ConnectedMoleculeQ", ID.ConnectedMoleculeQ);

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

  /**
   * ConstantRegionQ(x) - TODO describe `ConstantRegionQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConstantRegionQ.md">ConstantRegionQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ConstantRegionQ =
      S.initFinalSymbol("ConstantRegionQ", ID.ConstantRegionQ);

  public final static IBuiltInSymbol Constants = S.initFinalSymbol("Constants", ID.Constants);

  /**
   * ContainsAll(list1, list2) - returns `True` if `list1` contains all of the elements that appear
   * in `list2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContainsAll.md">ContainsAll
   *      documentation</a>
   */
  public final static IBuiltInSymbol ContainsAll = S.initFinalSymbol("ContainsAll", ID.ContainsAll);

  /**
   * ContainsAny(list1, list2) - returns `True` if `list1` contains one of the elements that appear
   * in `list2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContainsAny.md">ContainsAny
   *      documentation</a>
   */
  public final static IBuiltInSymbol ContainsAny = S.initFinalSymbol("ContainsAny", ID.ContainsAny);

  /**
   * ContainsExactly(list1, list2) - returns `True` if `list1` contains exactly the elements that
   * appear in `list2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContainsExactly.md">ContainsExactly
   *      documentation</a>
   */
  public final static IBuiltInSymbol ContainsExactly =
      S.initFinalSymbol("ContainsExactly", ID.ContainsExactly);

  /**
   * ContainsNone(list1, list2) - returns `True` if `list1` contains no element that appear in
   * `list2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContainsNone.md">ContainsNone
   *      documentation</a>
   */
  public final static IBuiltInSymbol ContainsNone =
      S.initFinalSymbol("ContainsNone", ID.ContainsNone);

  /**
   * ContainsOnly(list1, list2) - returns `True` if `list1` contains only elements thatare elements
   * in `list2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContainsOnly.md">ContainsOnly
   *      documentation</a>
   */
  public final static IBuiltInSymbol ContainsOnly =
      S.initFinalSymbol("ContainsOnly", ID.ContainsOnly);

  public final static IBuiltInSymbol ContentSelectable =
      S.initFinalSymbol("ContentSelectable", ID.ContentSelectable);

  /**
   * ContentSize(x) - TODO describe `ContentSize`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContentSize.md">ContentSize
   *      documentation</a>
   */
  public final static IBuiltInSymbol ContentSize = S.initFinalSymbol("ContentSize", ID.ContentSize);

  /**
   * Context(symbol) - yields the name of the context where `symbol` is defined in.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Context.md">Context
   *      documentation</a>
   */
  public final static IBuiltInSymbol Context = S.initFinalSymbol("Context", ID.Context);

  /**
   * Contexts() - return a list of all contexts
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Contexts.md">Contexts
   *      documentation</a>
   */
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

  /**
   * ContinuousAction(x) - TODO describe `ContinuousAction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContinuousAction.md">ContinuousAction
   *      documentation</a>
   */
  public final static IBuiltInSymbol ContinuousAction =
      S.initFinalSymbol("ContinuousAction", ID.ContinuousAction);

  /**
   * ContourLabels(x) - TODO describe `ContourLabels`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContourLabels.md">ContourLabels
   *      documentation</a>
   */
  public final static IBuiltInSymbol ContourLabels =
      S.initFinalSymbol("ContourLabels", ID.ContourLabels);

  /**
   * ContourLines(x) - TODO describe `ContourLines`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ContourLines.md">ContourLines
   *      documentation</a>
   */
  public final static IBuiltInSymbol ContourLines =
      S.initFinalSymbol("ContourLines", ID.ContourLines);

  public final static IBuiltInSymbol ContourPlot = S.initFinalSymbol("ContourPlot", ID.ContourPlot);

  public final static IBuiltInSymbol ContourPlot3D =
      S.initFinalSymbol("ContourPlot3D", ID.ContourPlot3D);

  public final static IBuiltInSymbol Contours = S.initFinalSymbol("Contours", ID.Contours);

  public final static IBuiltInSymbol ContourShading =
      S.initFinalSymbol("ContourShading", ID.ContourShading);

  public final static IBuiltInSymbol ContourStyle =
      S.initFinalSymbol("ContourStyle", ID.ContourStyle);

  /**
   * Control(x) - TODO describe `Control`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Control.md">Control
   *      documentation</a>
   */
  public final static IBuiltInSymbol Control = S.initFinalSymbol("Control", ID.Control);

  /**
   * ControllerLinking(x) - TODO describe `ControllerLinking`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ControllerLinking.md">ControllerLinking
   *      documentation</a>
   */
  public final static IBuiltInSymbol ControllerLinking =
      S.initFinalSymbol("ControllerLinking", ID.ControllerLinking);

  /**
   * ControllerPath(x) - TODO describe `ControllerPath`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ControllerPath.md">ControllerPath
   *      documentation</a>
   */
  public final static IBuiltInSymbol ControllerPath =
      S.initFinalSymbol("ControllerPath", ID.ControllerPath);

  /**
   * ControlPlacement(x) - TODO describe `ControlPlacement`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ControlPlacement.md">ControlPlacement
   *      documentation</a>
   */
  public final static IBuiltInSymbol ControlPlacement =
      S.initFinalSymbol("ControlPlacement", ID.ControlPlacement);

  /**
   * ControlType(x) - TODO describe `ControlType`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ControlType.md">ControlType
   *      documentation</a>
   */
  public final static IBuiltInSymbol ControlType = S.initFinalSymbol("ControlType", ID.ControlType);

  /**
   * Convergents({n1, n2, ...}) - return the list of convergents which represents the continued
   * fraction list `{n1, n2, ...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Convergents.md">Convergents
   *      documentation</a>
   */
  public final static IBuiltInSymbol Convergents = S.initFinalSymbol("Convergents", ID.Convergents);

  /**
   * ConvexHull(x) - TODO describe `ConvexHull`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConvexHull.md">ConvexHull
   *      documentation</a>
   */
  public final static IBuiltInSymbol ConvexHull = S.initFinalSymbol("ConvexHull", ID.ConvexHull);

  public final static IBuiltInSymbol ConvexHullMesh =
      S.initFinalSymbol("ConvexHullMesh", ID.ConvexHullMesh);

  /**
   * ConvexHullRegion(x) - TODO describe `ConvexHullRegion`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConvexHullRegion.md">ConvexHullRegion
   *      documentation</a>
   */
  public final static IBuiltInSymbol ConvexHullRegion =
      S.initFinalSymbol("ConvexHullRegion", ID.ConvexHullRegion);

  /**
   * ConvexRegionQ(x) - TODO describe `ConvexRegionQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ConvexRegionQ.md">ConvexRegionQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ConvexRegionQ =
      S.initFinalSymbol("ConvexRegionQ", ID.ConvexRegionQ);

  public final static IBuiltInSymbol Convolve = S.initFinalSymbol("Convolve", ID.Convolve);

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

  public final static IBuiltInSymbol CoordinatesToolOptions =
      S.initFinalSymbol("CoordinatesToolOptions", ID.CoordinatesToolOptions);

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
   * Coproduct(x) - TODO describe `Coproduct`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Coproduct.md">Coproduct
   *      documentation</a>
   */
  public final static IBuiltInSymbol Coproduct = S.initFinalSymbol("Coproduct", ID.Coproduct);

  /**
   * CopyFile(x) - TODO describe `CopyFile`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CopyFile.md">CopyFile
   *      documentation</a>
   */
  public final static IBuiltInSymbol CopyFile = S.initFinalSymbol("CopyFile", ID.CopyFile);

  /**
   * CornerFilter(x) - TODO describe `CornerFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CornerFilter.md">CornerFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol CornerFilter =
      S.initFinalSymbol("CornerFilter", ID.CornerFilter);

  /**
   * CornerNeighbors(x) - TODO describe `CornerNeighbors`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CornerNeighbors.md">CornerNeighbors
   *      documentation</a>
   */
  public final static IBuiltInSymbol CornerNeighbors =
      S.initFinalSymbol("CornerNeighbors", ID.CornerNeighbors);

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

  public final static IBuiltInSymbol CountsBy = S.initFinalSymbol("CountsBy", ID.CountsBy);

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
   * CreateUUID( ) - retrieve a type 4 (pseudo randomly generated) UUID. The UUID is generated using
   * a cryptographically strong pseudo random number generator.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CreateUUID.md">CreateUUID
   *      documentation</a>
   */
  public final static IBuiltInSymbol CreateUUID = S.initFinalSymbol("CreateUUID", ID.CreateUUID);

  /**
   * Cross(a, b) - computes the vector cross product of `a` and `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cross.md">Cross
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cross = S.initFinalSymbol("Cross", ID.Cross);

  /**
   * CrossingDetect(x) - TODO describe `CrossingDetect`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CrossingDetect.md">CrossingDetect
   *      documentation</a>
   */
  public final static IBuiltInSymbol CrossingDetect =
      S.initFinalSymbol("CrossingDetect", ID.CrossingDetect);

  public final static IBuiltInSymbol CrossMatrix = S.initFinalSymbol("CrossMatrix", ID.CrossMatrix);

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
   * Cubics(x) - TODO describe `Cubics`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cubics.md">Cubics
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cubics = S.initFinalSymbol("Cubics", ID.Cubics);

  /**
   * Cuboid({xmin, ymin, zmin}) - is a unit cube.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cuboid.md">Cuboid
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cuboid = S.initFinalSymbol("Cuboid", ID.Cuboid);

  public final static IBuiltInSymbol Cumulant = S.initFinalSymbol("Cumulant", ID.Cumulant);

  /**
   * CumulantGeneratingFunction(x) - TODO describe `CumulantGeneratingFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CumulantGeneratingFunction.md">CumulantGeneratingFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol CumulantGeneratingFunction =
      S.initFinalSymbol("CumulantGeneratingFunction", ID.CumulantGeneratingFunction);

  /**
   * Cup(x) - TODO describe `Cup`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cup.md">Cup
   *      documentation</a>
   */
  public final static IBuiltInSymbol Cup = S.initFinalSymbol("Cup", ID.Cup);

  /**
   * CupCap(x) - TODO describe `CupCap`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CupCap.md">CupCap
   *      documentation</a>
   */
  public final static IBuiltInSymbol CupCap = S.initFinalSymbol("CupCap", ID.CupCap);

  /**
   * Curl({f1, f2}, {x1, x2}) - returns the curl `D(f2, x1) - D(f1, x2)`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Curl.md">Curl
   *      documentation</a>
   */
  public final static IBuiltInSymbol Curl = S.initFinalSymbol("Curl", ID.Curl);

  /**
   * CurrencyConvert(x) - TODO describe `CurrencyConvert`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CurrencyConvert.md">CurrencyConvert
   *      documentation</a>
   */
  public final static IBuiltInSymbol CurrencyConvert =
      S.initFinalSymbol("CurrencyConvert", ID.CurrencyConvert);

  /**
   * CurveClosed(x) - TODO describe `CurveClosed`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/CurveClosed.md">CurveClosed
   *      documentation</a>
   */
  public final static IBuiltInSymbol CurveClosed = S.initFinalSymbol("CurveClosed", ID.CurveClosed);

  /**
   * Cyan - RGB color value for the color cyan
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Cyan.md">Cyan
   *      documentation</a>
   */
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

  public final static IBuiltInSymbol Darker = S.initFinalSymbol("Darker", ID.Darker);

  public final static IBuiltInSymbol DarkGray = S.initFinalSymbol("DarkGray", ID.DarkGray);

  public final static IBuiltInSymbol Dashed = S.initFinalSymbol("Dashed", ID.Dashed);

  public final static IBuiltInSymbol Dashing = S.initFinalSymbol("Dashing", ID.Dashing);

  /**
   * DataDistribution(x) - TODO describe `DataDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DataDistribution.md">DataDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol DataDistribution =
      S.initFinalSymbol("DataDistribution", ID.DataDistribution);

  public final static IBuiltInSymbol DataRange = S.initFinalSymbol("DataRange", ID.DataRange);

  /**
   * DataReversed(x) - TODO describe `DataReversed`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DataReversed.md">DataReversed
   *      documentation</a>
   */
  public final static IBuiltInSymbol DataReversed =
      S.initFinalSymbol("DataReversed", ID.DataReversed);

  /**
   * Dataset( association ) - create a `Dataset` object from the `association`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dataset.md">Dataset
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dataset = S.initFinalSymbol("Dataset", ID.Dataset);

  /**
   * DatasetDisplayFormat(x) - TODO describe `DatasetDisplayFormat`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DatasetDisplayFormat.md">DatasetDisplayFormat
   *      documentation</a>
   */
  public final static IBuiltInSymbol DatasetDisplayFormat =
      S.initFinalSymbol("DatasetDisplayFormat", ID.DatasetDisplayFormat);

  /**
   * DatasetTheme(x) - TODO describe `DatasetTheme`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DatasetTheme.md">DatasetTheme
   *      documentation</a>
   */
  public final static IBuiltInSymbol DatasetTheme =
      S.initFinalSymbol("DatasetTheme", ID.DatasetTheme);

  /**
   * DateBounds(x) - TODO describe `DateBounds`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateBounds.md">DateBounds
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateBounds = S.initFinalSymbol("DateBounds", ID.DateBounds);

  /**
   * Dated(x) - TODO describe `Dated`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dated.md">Dated
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dated = S.initFinalSymbol("Dated", ID.Dated);

  /**
   * DateDifference(x) - TODO describe `DateDifference`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateDifference.md">DateDifference
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateDifference =
      S.initFinalSymbol("DateDifference", ID.DateDifference);

  /**
   * DatedUnit(x) - TODO describe `DatedUnit`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DatedUnit.md">DatedUnit
   *      documentation</a>
   */
  public final static IBuiltInSymbol DatedUnit = S.initFinalSymbol("DatedUnit", ID.DatedUnit);

  /**
   * DateFormat(x) - TODO describe `DateFormat`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateFormat.md">DateFormat
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateFormat = S.initFinalSymbol("DateFormat", ID.DateFormat);

  /**
   * DateGranularity(x) - TODO describe `DateGranularity`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateGranularity.md">DateGranularity
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateGranularity =
      S.initFinalSymbol("DateGranularity", ID.DateGranularity);

  /**
   * DateInterval(x) - TODO describe `DateInterval`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateInterval.md">DateInterval
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateInterval =
      S.initFinalSymbol("DateInterval", ID.DateInterval);

  /**
   * DateList(x) - TODO describe `DateList`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateList.md">DateList
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateList = S.initFinalSymbol("DateList", ID.DateList);

  /**
   * DateListLogPlot(x) - TODO describe `DateListLogPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateListLogPlot.md">DateListLogPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateListLogPlot =
      S.initFinalSymbol("DateListLogPlot", ID.DateListLogPlot);

  /**
   * DateListPlot(x) - TODO describe `DateListPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateListPlot.md">DateListPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateListPlot =
      S.initFinalSymbol("DateListPlot", ID.DateListPlot);

  /**
   * DateListStepPlot(x) - TODO describe `DateListStepPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateListStepPlot.md">DateListStepPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateListStepPlot =
      S.initFinalSymbol("DateListStepPlot", ID.DateListStepPlot);

  /**
   * DateObject() - return the current date
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateObject.md">DateObject
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateObject = S.initFinalSymbol("DateObject", ID.DateObject);

  /**
   * DateObjectQ(x) - TODO describe `DateObjectQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateObjectQ.md">DateObjectQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateObjectQ = S.initFinalSymbol("DateObjectQ", ID.DateObjectQ);

  /**
   * DateOverlapsQ(x) - TODO describe `DateOverlapsQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateOverlapsQ.md">DateOverlapsQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateOverlapsQ =
      S.initFinalSymbol("DateOverlapsQ", ID.DateOverlapsQ);

  /**
   * DatePlus(x) - TODO describe `DatePlus`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DatePlus.md">DatePlus
   *      documentation</a>
   */
  public final static IBuiltInSymbol DatePlus = S.initFinalSymbol("DatePlus", ID.DatePlus);

  /**
   * DateRange(x) - TODO describe `DateRange`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateRange.md">DateRange
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateRange = S.initFinalSymbol("DateRange", ID.DateRange);

  /**
   * DateSelect(x) - TODO describe `DateSelect`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateSelect.md">DateSelect
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateSelect = S.initFinalSymbol("DateSelect", ID.DateSelect);

  /**
   * DateString() - return the current date as string
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateString.md">DateString
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateString = S.initFinalSymbol("DateString", ID.DateString);

  /**
   * DateValue("date-time-string") - return the current date in the specified date-time form
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateValue.md">DateValue
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateValue = S.initFinalSymbol("DateValue", ID.DateValue);

  /**
   * DateWithinQ(x) - TODO describe `DateWithinQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DateWithinQ.md">DateWithinQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol DateWithinQ = S.initFinalSymbol("DateWithinQ", ID.DateWithinQ);

  public final static IBuiltInSymbol DawsonF = S.initFinalSymbol("DawsonF", ID.DawsonF);

  /**
   * DayCount(x) - TODO describe `DayCount`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DayCount.md">DayCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol DayCount = S.initFinalSymbol("DayCount", ID.DayCount);

  /**
   * DayHemisphere(x) - TODO describe `DayHemisphere`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DayHemisphere.md">DayHemisphere
   *      documentation</a>
   */
  public final static IBuiltInSymbol DayHemisphere =
      S.initFinalSymbol("DayHemisphere", ID.DayHemisphere);

  /**
   * DaylightQ(x) - TODO describe `DaylightQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DaylightQ.md">DaylightQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol DaylightQ = S.initFinalSymbol("DaylightQ", ID.DaylightQ);

  /**
   * DayMatchQ(x) - TODO describe `DayMatchQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DayMatchQ.md">DayMatchQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol DayMatchQ = S.initFinalSymbol("DayMatchQ", ID.DayMatchQ);

  /**
   * DayName(x) - TODO describe `DayName`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DayName.md">DayName
   *      documentation</a>
   */
  public final static IBuiltInSymbol DayName = S.initFinalSymbol("DayName", ID.DayName);

  /**
   * DayNightTerminator(x) - TODO describe `DayNightTerminator`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DayNightTerminator.md">DayNightTerminator
   *      documentation</a>
   */
  public final static IBuiltInSymbol DayNightTerminator =
      S.initFinalSymbol("DayNightTerminator", ID.DayNightTerminator);

  /**
   * DayPlus(x) - TODO describe `DayPlus`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DayPlus.md">DayPlus
   *      documentation</a>
   */
  public final static IBuiltInSymbol DayPlus = S.initFinalSymbol("DayPlus", ID.DayPlus);

  /**
   * DayRange(x) - TODO describe `DayRange`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DayRange.md">DayRange
   *      documentation</a>
   */
  public final static IBuiltInSymbol DayRange = S.initFinalSymbol("DayRange", ID.DayRange);

  /**
   * DayRound(x) - TODO describe `DayRound`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DayRound.md">DayRound
   *      documentation</a>
   */
  public final static IBuiltInSymbol DayRound = S.initFinalSymbol("DayRound", ID.DayRound);

  /**
   * DeBruijnSequence(list, order) - returns the de Briujn sequence of order `order`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeBruijnSequence.md">DeBruijnSequence
   *      documentation</a>
   */
  public final static IBuiltInSymbol DeBruijnSequence =
      S.initFinalSymbol("DeBruijnSequence", ID.DeBruijnSequence);

  /**
   * DecimalForm(x) - TODO describe `DecimalForm`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DecimalForm.md">DecimalForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol DecimalForm = S.initFinalSymbol("DecimalForm", ID.DecimalForm);

  public final static IBuiltInSymbol Decompose = S.initFinalSymbol("Decompose", ID.Decompose);

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

  /**
   * DefaultDuration(x) - TODO describe `DefaultDuration`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DefaultDuration.md">DefaultDuration
   *      documentation</a>
   */
  public final static IBuiltInSymbol DefaultDuration =
      S.initFinalSymbol("DefaultDuration", ID.DefaultDuration);

  /**
   * DefaultPrintPrecision(x) - TODO describe `DefaultPrintPrecision`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DefaultPrintPrecision.md">DefaultPrintPrecision
   *      documentation</a>
   */
  public final static IBuiltInSymbol DefaultPrintPrecision =
      S.initFinalSymbol("DefaultPrintPrecision", ID.DefaultPrintPrecision);

  public final static IBuiltInSymbol DefaultValue =
      S.initFinalSymbol("DefaultValue", ID.DefaultValue);

  /**
   * DefaultValues(symbol) - `DefaultValues` returns the default values associated with the
   * `symbol`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DefaultValues.md">DefaultValues
   *      documentation</a>
   */
  public final static IBuiltInSymbol DefaultValues =
      S.initFinalSymbol("DefaultValues", ID.DefaultValues);

  /**
   * Defer(expr) - `Defer` doesn't evaluate `expr` and didn't appear in the output
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Defer.md">Defer
   *      documentation</a>
   */
  public final static IBuiltInSymbol Defer = S.initFinalSymbol("Defer", ID.Defer);

  /**
   * Definition(symbol) - prints values and rules associated with `symbol`.
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
   * Deinitialization(x) - TODO describe `Deinitialization`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Deinitialization.md">Deinitialization
   *      documentation</a>
   */
  public final static IBuiltInSymbol Deinitialization =
      S.initFinalSymbol("Deinitialization", ID.Deinitialization);

  /**
   * Del(x) - TODO describe `Del`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Del.md">Del
   *      documentation</a>
   */
  public final static IBuiltInSymbol Del = S.initFinalSymbol("Del", ID.Del);

  /**
   * DelaunayMesh(x) - TODO describe `DelaunayMesh`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DelaunayMesh.md">DelaunayMesh
   *      documentation</a>
   */
  public final static IBuiltInSymbol DelaunayMesh =
      S.initFinalSymbol("DelaunayMesh", ID.DelaunayMesh);

  /**
   * Delete(expr, n) - deletes the element at position `n` in `expr`. The position is counted from
   * the end if `n` is negative.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Delete.md">Delete
   *      documentation</a>
   */
  public final static IBuiltInSymbol Delete = S.initFinalSymbol("Delete", ID.Delete);

  /**
   * DeleteBorderComponents(x) - TODO describe `DeleteBorderComponents`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteBorderComponents.md">DeleteBorderComponents
   *      documentation</a>
   */
  public final static IBuiltInSymbol DeleteBorderComponents =
      S.initFinalSymbol("DeleteBorderComponents", ID.DeleteBorderComponents);

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

  /**
   * DeleteFile(x) - TODO describe `DeleteFile`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteFile.md">DeleteFile
   *      documentation</a>
   */
  public final static IBuiltInSymbol DeleteFile = S.initFinalSymbol("DeleteFile", ID.DeleteFile);

  public final static IBuiltInSymbol DeleteMissing =
      S.initFinalSymbol("DeleteMissing", ID.DeleteMissing);

  /**
   * DeleteSmallComponents(x) - TODO describe `DeleteSmallComponents`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DeleteSmallComponents.md">DeleteSmallComponents
   *      documentation</a>
   */
  public final static IBuiltInSymbol DeleteSmallComponents =
      S.initFinalSymbol("DeleteSmallComponents", ID.DeleteSmallComponents);

  /**
   * Delimiter(x) - TODO describe `Delimiter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Delimiter.md">Delimiter
   *      documentation</a>
   */
  public final static IBuiltInSymbol Delimiter = S.initFinalSymbol("Delimiter", ID.Delimiter);

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
   * Deployed(x) - TODO describe `Deployed`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Deployed.md">Deployed
   *      documentation</a>
   */
  public final static IBuiltInSymbol Deployed = S.initFinalSymbol("Deployed", ID.Deployed);

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
   * DerivativeFilter(x) - TODO describe `DerivativeFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DerivativeFilter.md">DerivativeFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol DerivativeFilter =
      S.initFinalSymbol("DerivativeFilter", ID.DerivativeFilter);

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
   * Diamond(x) - TODO describe `Diamond`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Diamond.md">Diamond
   *      documentation</a>
   */
  public final static IBuiltInSymbol Diamond = S.initFinalSymbol("Diamond", ID.Diamond);

  public final static IBuiltInSymbol DiamondMatrix =
      S.initFinalSymbol("DiamondMatrix", ID.DiamondMatrix);

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

  /**
   * DifferenceQuotient(f, {var, h}) - gives the difference quotient `(f(var+h)-f(var))/h` of the
   * expression `f` with respect to `var` and step-size `h`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DifferenceQuotient.md">DifferenceQuotient
   *      documentation</a>
   */
  public final static IBuiltInSymbol DifferenceQuotient =
      S.initFinalSymbol("DifferenceQuotient", ID.DifferenceQuotient);

  /**
   * DifferenceRoot(equation) - operator for generating a holonomic sequence defined by a linear
   * difference `equation`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DifferenceRoot.md">DifferenceRoot
   *      documentation</a>
   */
  public final static IBuiltInSymbol DifferenceRoot =
      S.initFinalSymbol("DifferenceRoot", ID.DifferenceRoot);

  public final static IBuiltInSymbol Differences = S.initFinalSymbol("Differences", ID.Differences);

  /**
   * DifferentialD(x) - TODO describe `DifferentialD`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DifferentialD.md">DifferentialD
   *      documentation</a>
   */
  public final static IBuiltInSymbol DifferentialD =
      S.initFinalSymbol("DifferentialD", ID.DifferentialD);

  /**
   * DigitBlock(x) - TODO describe `DigitBlock`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DigitBlock.md">DigitBlock
   *      documentation</a>
   */
  public final static IBuiltInSymbol DigitBlock = S.initFinalSymbol("DigitBlock", ID.DigitBlock);

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

  public final static IBuiltInSymbol DigitSum = S.initFinalSymbol("DigitSum", ID.DigitSum);

  /**
   * Dilation(x) - TODO describe `Dilation`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dilation.md">Dilation
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dilation = S.initFinalSymbol("Dilation", ID.Dilation);

  /**
   * DimensionalCombinations(x) - TODO describe `DimensionalCombinations`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DimensionalCombinations.md">DimensionalCombinations
   *      documentation</a>
   */
  public final static IBuiltInSymbol DimensionalCombinations =
      S.initFinalSymbol("DimensionalCombinations", ID.DimensionalCombinations);

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

  public final static IBuiltInSymbol DirectedGraphQ =
      S.initFinalSymbol("DirectedGraphQ", ID.DirectedGraphQ);

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

  /**
   * DirichletBeta(x) - `DirichletBeta` function returns the Dirichlet beta function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DirichletBeta.md">DirichletBeta
   *      documentation</a>
   */
  public final static IBuiltInSymbol DirichletBeta =
      S.initFinalSymbol("DirichletBeta", ID.DirichletBeta);

  /**
   * DirichletEta(x) - `DirichletEta` function returns the Dirichlet eta function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DirichletEta.md">DirichletEta
   *      documentation</a>
   */
  public final static IBuiltInSymbol DirichletEta =
      S.initFinalSymbol("DirichletEta", ID.DirichletEta);

  /**
   * DirichletLambda(z) - returns the Dirichlet lambda function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DirichletLambda.md">DirichletLambda
   *      documentation</a>
   */
  public final static IBuiltInSymbol DirichletLambda =
      S.initFinalSymbol("DirichletLambda", ID.DirichletLambda);

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
   * DiscreteLimit(f, n -> Infinity) - computes the limit of the sequence `f` as the integer
   * variable `n` tends to infinity.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscreteLimit.md">DiscreteLimit
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiscreteLimit =
      S.initFinalSymbol("DiscreteLimit", ID.DiscreteLimit);

  /**
   * DiscretePlot( expr, {x, nmax} ) - plots `expr` with `x` ranging from `1` to `nmax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscretePlot.md">DiscretePlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiscretePlot =
      S.initFinalSymbol("DiscretePlot", ID.DiscretePlot);

  public final static IBuiltInSymbol DiscretePlot3D =
      S.initFinalSymbol("DiscretePlot3D", ID.DiscretePlot3D);

  /**
   * DiscreteRatio(f(var), var) - `DiscreteRatio` computes `f(var+1)/f(var)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscreteRatio.md">DiscreteRatio
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiscreteRatio =
      S.initFinalSymbol("DiscreteRatio", ID.DiscreteRatio);

  /**
   * DiscreteShift(f(var), {var, shift}) - `DiscreteShift` computes the shift `f(var+shift)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiscreteShift.md">DiscreteShift
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiscreteShift =
      S.initFinalSymbol("DiscreteShift", ID.DiscreteShift);

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

  public final static IBuiltInSymbol DiskMatrix = S.initFinalSymbol("DiskMatrix", ID.DiskMatrix);

  /**
   * DiskSegment(x) - TODO describe `DiskSegment`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DiskSegment.md">DiskSegment
   *      documentation</a>
   */
  public final static IBuiltInSymbol DiskSegment = S.initFinalSymbol("DiskSegment", ID.DiskSegment);

  /**
   * Dispatch({rule1, rule2, ...}) - create a dispatch map for a list of rules.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dispatch.md">Dispatch
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dispatch = S.initFinalSymbol("Dispatch", ID.Dispatch);

  /**
   * DisplayAllSteps(x) - TODO describe `DisplayAllSteps`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DisplayAllSteps.md">DisplayAllSteps
   *      documentation</a>
   */
  public final static IBuiltInSymbol DisplayAllSteps =
      S.initFinalSymbol("DisplayAllSteps", ID.DisplayAllSteps);

  public final static IBuiltInSymbol DisplayForm = S.initFinalSymbol("DisplayForm", ID.DisplayForm);

  public final static IBuiltInSymbol DisplayFunction =
      S.initFinalSymbol("DisplayFunction", ID.DisplayFunction);

  public final static IBuiltInSymbol Disputed = S.initFinalSymbol("Disputed", ID.Disputed);

  public final static IBuiltInSymbol DistanceFunction =
      S.initFinalSymbol("DistanceFunction", ID.DistanceFunction);

  /**
   * DistanceTransform(x) - TODO describe `DistanceTransform`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DistanceTransform.md">DistanceTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol DistanceTransform =
      S.initFinalSymbol("DistanceTransform", ID.DistanceTransform);

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
   * DistributionChart(x) - TODO describe `DistributionChart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DistributionChart.md">DistributionChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol DistributionChart =
      S.initFinalSymbol("DistributionChart", ID.DistributionChart);

  /**
   * DistributionParameterQ(x) - TODO describe `DistributionParameterQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DistributionParameterQ.md">DistributionParameterQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol DistributionParameterQ =
      S.initFinalSymbol("DistributionParameterQ", ID.DistributionParameterQ);

  /**
   * Dithering(x) - TODO describe `Dithering`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dithering.md">Dithering
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dithering = S.initFinalSymbol("Dithering", ID.Dithering);

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
   * Divides(x) - TODO describe `Divides`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Divides.md">Divides
   *      documentation</a>
   */
  public final static IBuiltInSymbol Divides = S.initFinalSymbol("Divides", ID.Divides);

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
   * DMSList(x) - TODO describe `DMSList`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DMSList.md">DMSList
   *      documentation</a>
   */
  public final static IBuiltInSymbol DMSList = S.initFinalSymbol("DMSList", ID.DMSList);

  /**
   * DMSString(x) - TODO describe `DMSString`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DMSString.md">DMSString
   *      documentation</a>
   */
  public final static IBuiltInSymbol DMSString = S.initFinalSymbol("DMSString", ID.DMSString);

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
   * Dot(x, y) - computes products of vectors, matrices, and tensors.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dot.md">Dot
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dot = S.initFinalSymbol("Dot", ID.Dot);

  public final static IBuiltInSymbol DotDashed = S.initFinalSymbol("DotDashed", ID.DotDashed);

  /**
   * DotEqual(x) - TODO describe `DotEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DotEqual.md">DotEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol DotEqual = S.initFinalSymbol("DotEqual", ID.DotEqual);

  public final static IBuiltInSymbol Dotted = S.initFinalSymbol("Dotted", ID.Dotted);

  /**
   * DoubleDownArrow(x) - TODO describe `DoubleDownArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DoubleDownArrow.md">DoubleDownArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol DoubleDownArrow =
      S.initFinalSymbol("DoubleDownArrow", ID.DoubleDownArrow);

  /**
   * DoubleLeftArrow(x) - TODO describe `DoubleLeftArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DoubleLeftArrow.md">DoubleLeftArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol DoubleLeftArrow =
      S.initFinalSymbol("DoubleLeftArrow", ID.DoubleLeftArrow);

  /**
   * DoubleLeftRightArrow(x) - TODO describe `DoubleLeftRightArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DoubleLeftRightArrow.md">DoubleLeftRightArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol DoubleLeftRightArrow =
      S.initFinalSymbol("DoubleLeftRightArrow", ID.DoubleLeftRightArrow);

  /**
   * DoubleLeftTee(x) - TODO describe `DoubleLeftTee`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DoubleLeftTee.md">DoubleLeftTee
   *      documentation</a>
   */
  public final static IBuiltInSymbol DoubleLeftTee =
      S.initFinalSymbol("DoubleLeftTee", ID.DoubleLeftTee);

  /**
   * DoubleLongLeftArrow(x) - TODO describe `DoubleLongLeftArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DoubleLongLeftArrow.md">DoubleLongLeftArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol DoubleLongLeftArrow =
      S.initFinalSymbol("DoubleLongLeftArrow", ID.DoubleLongLeftArrow);

  /**
   * DoubleLongLeftRightArrow(x) - TODO describe `DoubleLongLeftRightArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DoubleLongLeftRightArrow.md">DoubleLongLeftRightArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol DoubleLongLeftRightArrow =
      S.initFinalSymbol("DoubleLongLeftRightArrow", ID.DoubleLongLeftRightArrow);

  /**
   * DoubleLongRightArrow(x) - TODO describe `DoubleLongRightArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DoubleLongRightArrow.md">DoubleLongRightArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol DoubleLongRightArrow =
      S.initFinalSymbol("DoubleLongRightArrow", ID.DoubleLongRightArrow);

  /**
   * DoubleRightArrow(x) - TODO describe `DoubleRightArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DoubleRightArrow.md">DoubleRightArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol DoubleRightArrow =
      S.initFinalSymbol("DoubleRightArrow", ID.DoubleRightArrow);

  /**
   * DoubleRightTee(x) - TODO describe `DoubleRightTee`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DoubleRightTee.md">DoubleRightTee
   *      documentation</a>
   */
  public final static IBuiltInSymbol DoubleRightTee =
      S.initFinalSymbol("DoubleRightTee", ID.DoubleRightTee);

  /**
   * DoubleUpArrow(x) - TODO describe `DoubleUpArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DoubleUpArrow.md">DoubleUpArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol DoubleUpArrow =
      S.initFinalSymbol("DoubleUpArrow", ID.DoubleUpArrow);

  /**
   * DoubleUpDownArrow(x) - TODO describe `DoubleUpDownArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DoubleUpDownArrow.md">DoubleUpDownArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol DoubleUpDownArrow =
      S.initFinalSymbol("DoubleUpDownArrow", ID.DoubleUpDownArrow);

  /**
   * DoubleVerticalBar(x) - TODO describe `DoubleVerticalBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DoubleVerticalBar.md">DoubleVerticalBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol DoubleVerticalBar =
      S.initFinalSymbol("DoubleVerticalBar", ID.DoubleVerticalBar);

  /**
   * DownArrow(x) - TODO describe `DownArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownArrow.md">DownArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol DownArrow = S.initFinalSymbol("DownArrow", ID.DownArrow);

  /**
   * DownArrowBar(x) - TODO describe `DownArrowBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownArrowBar.md">DownArrowBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol DownArrowBar =
      S.initFinalSymbol("DownArrowBar", ID.DownArrowBar);

  /**
   * DownArrowUpArrow(x) - TODO describe `DownArrowUpArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownArrowUpArrow.md">DownArrowUpArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol DownArrowUpArrow =
      S.initFinalSymbol("DownArrowUpArrow", ID.DownArrowUpArrow);

  /**
   * DownLeftRightVector(x) - TODO describe `DownLeftRightVector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownLeftRightVector.md">DownLeftRightVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol DownLeftRightVector =
      S.initFinalSymbol("DownLeftRightVector", ID.DownLeftRightVector);

  /**
   * DownLeftTeeVector(x) - TODO describe `DownLeftTeeVector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownLeftTeeVector.md">DownLeftTeeVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol DownLeftTeeVector =
      S.initFinalSymbol("DownLeftTeeVector", ID.DownLeftTeeVector);

  /**
   * DownLeftVector(x) - TODO describe `DownLeftVector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownLeftVector.md">DownLeftVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol DownLeftVector =
      S.initFinalSymbol("DownLeftVector", ID.DownLeftVector);

  /**
   * DownLeftVectorBar(x) - TODO describe `DownLeftVectorBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownLeftVectorBar.md">DownLeftVectorBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol DownLeftVectorBar =
      S.initFinalSymbol("DownLeftVectorBar", ID.DownLeftVectorBar);

  /**
   * DownRightTeeVector(x) - TODO describe `DownRightTeeVector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownRightTeeVector.md">DownRightTeeVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol DownRightTeeVector =
      S.initFinalSymbol("DownRightTeeVector", ID.DownRightTeeVector);

  /**
   * DownRightVector(x) - TODO describe `DownRightVector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownRightVector.md">DownRightVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol DownRightVector =
      S.initFinalSymbol("DownRightVector", ID.DownRightVector);

  /**
   * DownRightVectorBar(x) - TODO describe `DownRightVectorBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownRightVectorBar.md">DownRightVectorBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol DownRightVectorBar =
      S.initFinalSymbol("DownRightVectorBar", ID.DownRightVectorBar);

  /**
   * DownTee(x) - TODO describe `DownTee`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownTee.md">DownTee
   *      documentation</a>
   */
  public final static IBuiltInSymbol DownTee = S.initFinalSymbol("DownTee", ID.DownTee);

  /**
   * DownTeeArrow(x) - TODO describe `DownTeeArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DownTeeArrow.md">DownTeeArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol DownTeeArrow =
      S.initFinalSymbol("DownTeeArrow", ID.DownTeeArrow);

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
   * DropShadowing(x) - TODO describe `DropShadowing`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DropShadowing.md">DropShadowing
   *      documentation</a>
   */
  public final static IBuiltInSymbol DropShadowing =
      S.initFinalSymbol("DropShadowing", ID.DropShadowing);

  /**
   * DSolve(equation, f(var), var) - attempts to solve a linear differential `equation` for the
   * function `f(var)` and variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DSolve.md">DSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol DSolve = S.initFinalSymbol("DSolve", ID.DSolve);

  /**
   * DSolveValue(equation, f(var), var) - attempts to solve a linear differential `equation` for the
   * function `f(var)` and variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DSolveValue.md">DSolveValue
   *      documentation</a>
   */
  public final static IBuiltInSymbol DSolveValue = S.initFinalSymbol("DSolveValue", ID.DSolveValue);

  /**
   * Dt(f, x) - gives the total derivative of `f` with respect to `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Dt.md">Dt
   *      documentation</a>
   */
  public final static IBuiltInSymbol Dt = S.initFinalSymbol("Dt", ID.Dt);

  /**
   * DualPlanarGraph(graph) - gives the dual of the planar `graph`: one vertex for each face,
   * and an edge for each pair of faces separated from each other by an edge.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DualPlanarGraph.md">DualPlanarGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol DualPlanarGraph =
      S.initFinalSymbol("DualPlanarGraph", ID.DualPlanarGraph);

  public final static IBuiltInSymbol DuplicateFreeQ =
      S.initFinalSymbol("DuplicateFreeQ", ID.DuplicateFreeQ);

  public final static IBuiltInSymbol Dynamic = S.initFinalSymbol("Dynamic", ID.Dynamic);

  /**
   * DynamicModule(x) - TODO describe `DynamicModule`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DynamicModule.md">DynamicModule
   *      documentation</a>
   */
  public final static IBuiltInSymbol DynamicModule =
      S.initFinalSymbol("DynamicModule", ID.DynamicModule);

  /**
   * DynamicWrapper(x) - TODO describe `DynamicWrapper`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/DynamicWrapper.md">DynamicWrapper
   *      documentation</a>
   */
  public final static IBuiltInSymbol DynamicWrapper =
      S.initFinalSymbol("DynamicWrapper", ID.DynamicWrapper);

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

  /**
   * EclipseType(x) - TODO describe `EclipseType`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EclipseType.md">EclipseType
   *      documentation</a>
   */
  public final static IBuiltInSymbol EclipseType = S.initFinalSymbol("EclipseType", ID.EclipseType);

  public final static IBuiltInSymbol EdgeAdd = S.initFinalSymbol("EdgeAdd", ID.EdgeAdd);

  /**
   * EdgeCount(graph) - return the number of edges of the `graph`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeCount.md">EdgeCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol EdgeCount = S.initFinalSymbol("EdgeCount", ID.EdgeCount);

  /**
   * EdgeChromaticNumber(graph) - gives the smallest number of colors that can be assigned to
   * the edges of `graph` such that no two edges sharing an endpoint have the same color.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeChromaticNumber.md">EdgeChromaticNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol EdgeChromaticNumber =
      S.initFinalSymbol("EdgeChromaticNumber", ID.EdgeChromaticNumber);

  public final static IBuiltInSymbol EdgeContract =
      S.initFinalSymbol("EdgeContract", ID.EdgeContract);

  /**
   * EdgeCoverQ(graph, edges) - yields `True` if the edge list `edges` is an edge cover of
   * `graph`, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeCoverQ.md">EdgeCoverQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol EdgeCoverQ = S.initFinalSymbol("EdgeCoverQ", ID.EdgeCoverQ);

  public final static IBuiltInSymbol EdgeDelete = S.initFinalSymbol("EdgeDelete", ID.EdgeDelete);

  /**
   * EdgeDetect(x) - TODO describe `EdgeDetect`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeDetect.md">EdgeDetect
   *      documentation</a>
   */
  public final static IBuiltInSymbol EdgeDetect = S.initFinalSymbol("EdgeDetect", ID.EdgeDetect);

  public final static IBuiltInSymbol EdgeForm = S.initFinalSymbol("EdgeForm", ID.EdgeForm);

  public final static IBuiltInSymbol EdgeLabels = S.initFinalSymbol("EdgeLabels", ID.EdgeLabels);

  /**
   * EdgeLabelStyle(x) - TODO describe `EdgeLabelStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EdgeLabelStyle.md">EdgeLabelStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol EdgeLabelStyle =
      S.initFinalSymbol("EdgeLabelStyle", ID.EdgeLabelStyle);

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
   * EllipticExp(u, {a, b}) - returns the generalized exponential <code>{x,y}</code> of
   * <code>u</code> for the elliptic curve <code>y^2 == x^3 + a*x^2 + b*x</code>.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticExp.md">EllipticExp
   *      documentation</a>
   */
  public final static IBuiltInSymbol EllipticExp = S.initFinalSymbol("EllipticExp", ID.EllipticExp);

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
   * EllipticLog({x, y}, {a, b}) - returns the generalized logarithm of the point <code>{x,y}</code>
   * of the elliptic curve <code>y^2 == x^3 + a*x^2 + b*x</code>.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EllipticLog.md">EllipticLog
   *      documentation</a>
   */
  public final static IBuiltInSymbol EllipticLog = S.initFinalSymbol("EllipticLog", ID.EllipticLog);

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

  /**
   * Empirical(x) - TODO describe `Empirical`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Empirical.md">Empirical
   *      documentation</a>
   */
  public final static IBuiltInSymbol Empirical = S.initFinalSymbol("Empirical", ID.Empirical);

  public final static IBuiltInSymbol EmpiricalDistribution =
      S.initFinalSymbol("EmpiricalDistribution", ID.EmpiricalDistribution);

  /**
   * EmptyRegion(x) - TODO describe `EmptyRegion`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EmptyRegion.md">EmptyRegion
   *      documentation</a>
   */
  public final static IBuiltInSymbol EmptyRegion = S.initFinalSymbol("EmptyRegion", ID.EmptyRegion);

  /**
   * Enabled(x) - TODO describe `Enabled`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Enabled.md">Enabled
   *      documentation</a>
   */
  public final static IBuiltInSymbol Enabled = S.initFinalSymbol("Enabled", ID.Enabled);

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

  /**
   * EngineeringForm(x) - TODO describe `EngineeringForm`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EngineeringForm.md">EngineeringForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol EngineeringForm =
      S.initFinalSymbol("EngineeringForm", ID.EngineeringForm);

  public final static IBuiltInSymbol Entity = S.initFinalSymbol("Entity", ID.Entity);

  public final static IBuiltInSymbol EntityClass = S.initFinalSymbol("EntityClass", ID.EntityClass);

  public final static IBuiltInSymbol EntityList = S.initFinalSymbol("EntityList", ID.EntityList);

  public final static IBuiltInSymbol EntityProperty =
      S.initFinalSymbol("EntityProperty", ID.EntityProperty);

  /**
   * EntityValue(x) - TODO describe `EntityValue`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EntityValue.md">EntityValue
   *      documentation</a>
   */
  public final static IBuiltInSymbol EntityValue = S.initFinalSymbol("EntityValue", ID.EntityValue);

  /**
   * Entropy(list) - return the base `E` (Shannon) information entropy of the elements in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Entropy.md">Entropy
   *      documentation</a>
   */
  public final static IBuiltInSymbol Entropy = S.initFinalSymbol("Entropy", ID.Entropy);

  /**
   * EntropyFilter(x) - TODO describe `EntropyFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EntropyFilter.md">EntropyFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol EntropyFilter =
      S.initFinalSymbol("EntropyFilter", ID.EntropyFilter);

  public final static IBuiltInSymbol Epilog = S.initFinalSymbol("Epilog", ID.Epilog);

  /**
   * Equal(x, y) - yields `True` if `x` and `y` are known to be equal, or `False` if `x` and `y` are
   * known to be unequal.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Equal.md">Equal
   *      documentation</a>
   */
  public final static IBuiltInSymbol Equal = S.initFinalSymbol("Equal", ID.Equal);

  /**
   * EqualTilde(x) - TODO describe `EqualTilde`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EqualTilde.md">EqualTilde
   *      documentation</a>
   */
  public final static IBuiltInSymbol EqualTilde = S.initFinalSymbol("EqualTilde", ID.EqualTilde);

  public final static IBuiltInSymbol EqualTo = S.initFinalSymbol("EqualTo", ID.EqualTo);

  /**
   * Equilibrium(x) - TODO describe `Equilibrium`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Equilibrium.md">Equilibrium
   *      documentation</a>
   */
  public final static IBuiltInSymbol Equilibrium = S.initFinalSymbol("Equilibrium", ID.Equilibrium);

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
   * Erosion(x) - TODO describe `Erosion`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Erosion.md">Erosion
   *      documentation</a>
   */
  public final static IBuiltInSymbol Erosion = S.initFinalSymbol("Erosion", ID.Erosion);

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
   * EvaluationMonitor(x) - TODO describe `EvaluationMonitor`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/EvaluationMonitor.md">EvaluationMonitor
   *      documentation</a>
   */
  public final static IBuiltInSymbol EvaluationMonitor =
      S.initFinalSymbol("EvaluationMonitor", ID.EvaluationMonitor);

  /**
   * Evaluator(x) - TODO describe `Evaluator`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Evaluator.md">Evaluator
   *      documentation</a>
   */
  public final static IBuiltInSymbol Evaluator = S.initFinalSymbol("Evaluator", ID.Evaluator);

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

  /**
   * ExcludedLines(x) - TODO describe `ExcludedLines`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExcludedLines.md">ExcludedLines
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExcludedLines =
      S.initFinalSymbol("ExcludedLines", ID.ExcludedLines);

  /**
   * Exclusions(x) - TODO describe `Exclusions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Exclusions.md">Exclusions
   *      documentation</a>
   */
  public final static IBuiltInSymbol Exclusions = S.initFinalSymbol("Exclusions", ID.Exclusions);

  /**
   * ExclusionsStyle(x) - TODO describe `ExclusionsStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExclusionsStyle.md">ExclusionsStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExclusionsStyle =
      S.initFinalSymbol("ExclusionsStyle", ID.ExclusionsStyle);

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

  /**
   * ExpandDenominator(expr) - expands the denominator of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpandDenominator.md">ExpandDenominator
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExpandDenominator =
      S.initFinalSymbol("ExpandDenominator", ID.ExpandDenominator);

  /**
   * ExpandNumerator(expr) - expands the numerator of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpandNumerator.md">ExpandNumerator
   *      documentation</a>
   */
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
   * ExponentFunction(x) - TODO describe `ExponentFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExponentFunction.md">ExponentFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExponentFunction =
      S.initFinalSymbol("ExponentFunction", ID.ExponentFunction);

  /**
   * ExponentialDistribution(lambda) - returns an exponential distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExponentialDistribution.md">ExponentialDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExponentialDistribution =
      S.initFinalSymbol("ExponentialDistribution", ID.ExponentialDistribution);

  public final static IBuiltInSymbol ExponentialGeneratingFunction =
      S.initFinalSymbol("ExponentialGeneratingFunction", ID.ExponentialGeneratingFunction);

  /**
   * ExponentialPowerDistribution(x) - TODO describe `ExponentialPowerDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExponentialPowerDistribution.md">ExponentialPowerDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExponentialPowerDistribution =
      S.initFinalSymbol("ExponentialPowerDistribution", ID.ExponentialPowerDistribution);

  /**
   * ExponentStep(x) - TODO describe `ExponentStep`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExponentStep.md">ExponentStep
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExponentStep =
      S.initFinalSymbol("ExponentStep", ID.ExponentStep);

  /**
   * Export("path-to-filename", expression, "WXF") - if the file system is enabled, export the
   * `expression` in WXF format to the "path-to-filename" file.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Export.md">Export
   *      documentation</a>
   */
  public final static IBuiltInSymbol Export = S.initFinalSymbol("Export", ID.Export);

  /**
   * ExportForm(x) - TODO describe `ExportForm`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExportForm.md">ExportForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExportForm = S.initFinalSymbol("ExportForm", ID.ExportForm);

  /**
   * ExportString(string, export-format) - export the `string` in `export-format`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExportString.md">ExportString
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExportString =
      S.initFinalSymbol("ExportString", ID.ExportString);

  public final static IBuiltInSymbol Expression = S.initFinalSymbol("Expression", ID.Expression);

  /**
   * ExpressionGraph(x) - TODO describe `ExpressionGraph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExpressionGraph.md">ExpressionGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExpressionGraph =
      S.initFinalSymbol("ExpressionGraph", ID.ExpressionGraph);

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
   * ExtentElementFunction(x) - TODO describe `ExtentElementFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExtentElementFunction.md">ExtentElementFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExtentElementFunction =
      S.initFinalSymbol("ExtentElementFunction", ID.ExtentElementFunction);

  /**
   * ExtentMarkers(x) - TODO describe `ExtentMarkers`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ExtentMarkers.md">ExtentMarkers
   *      documentation</a>
   */
  public final static IBuiltInSymbol ExtentMarkers =
      S.initFinalSymbol("ExtentMarkers", ID.ExtentMarkers);

  public final static IBuiltInSymbol ExtentSize = S.initFinalSymbol("ExtentSize", ID.ExtentSize);

  /**
   * Extract(expr, list) - extracts parts of `expr` specified by `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Extract.md">Extract
   *      documentation</a>
   */
  public final static IBuiltInSymbol Extract = S.initFinalSymbol("Extract", ID.Extract);

  public final static IBuiltInSymbol FaceForm = S.initFinalSymbol("FaceForm", ID.FaceForm);

  /**
   * FaceGrids(x) - TODO describe `FaceGrids`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FaceGrids.md">FaceGrids
   *      documentation</a>
   */
  public final static IBuiltInSymbol FaceGrids = S.initFinalSymbol("FaceGrids", ID.FaceGrids);

  /**
   * FaceGridsStyle(x) - TODO describe `FaceGridsStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FaceGridsStyle.md">FaceGridsStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol FaceGridsStyle =
      S.initFinalSymbol("FaceGridsStyle", ID.FaceGridsStyle);

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

  public final static IBuiltInSymbol FactorialMoment =
      S.initFinalSymbol("FactorialMoment", ID.FactorialMoment);

  /**
   * FactorialMomentGeneratingFunction(x) - TODO describe `FactorialMomentGeneratingFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorialMomentGeneratingFunction.md">FactorialMomentGeneratingFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol FactorialMomentGeneratingFunction =
      S.initFinalSymbol("FactorialMomentGeneratingFunction", ID.FactorialMomentGeneratingFunction);

  /**
   * FactorialPower(v, n) - The `FactorialPower` implements the falling factorial. The falling
   * factorial (sometimes called the descending factorial, falling sequential product, or lower
   * factorial) is defined as the polynomial `v*(v-1)*(v-2)*...*(v-n+1)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FactorialPower.md">FactorialPower
   *      documentation</a>
   */
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

  public final static IBuiltInSymbol FactorList = S.initFinalSymbol("FactorList", ID.FactorList);

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
   * Failure(x) - TODO describe `Failure`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Failure.md">Failure
   *      documentation</a>
   */
  public final static IBuiltInSymbol Failure = S.initFinalSymbol("Failure", ID.Failure);

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

  /**
   * FileExistsQ(x) - TODO describe `FileExistsQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FileExistsQ.md">FileExistsQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol FileExistsQ = S.initFinalSymbol("FileExistsQ", ID.FileExistsQ);

  public final static IBuiltInSymbol FileFormat = S.initFinalSymbol("FileFormat", ID.FileFormat);

  /**
   * FileHash(file) - computes an MD5 hash for the contents of the specified `file`. The FileHash
   * function computes a cryptographic hash for the contents of a `file`. It is useful for verifying
   * file integrity and detecting changes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FileHash.md">FileHash
   *      documentation</a>
   */
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

  /**
   * FileNameSetter(x) - TODO describe `FileNameSetter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FileNameSetter.md">FileNameSetter
   *      documentation</a>
   */
  public final static IBuiltInSymbol FileNameSetter =
      S.initFinalSymbol("FileNameSetter", ID.FileNameSetter);

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

  /**
   * FilledCurve(x) - TODO describe `FilledCurve`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FilledCurve.md">FilledCurve
   *      documentation</a>
   */
  public final static IBuiltInSymbol FilledCurve = S.initFinalSymbol("FilledCurve", ID.FilledCurve);

  /**
   * FilledTorus(x) - TODO describe `FilledTorus`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FilledTorus.md">FilledTorus
   *      documentation</a>
   */
  public final static IBuiltInSymbol FilledTorus = S.initFinalSymbol("FilledTorus", ID.FilledTorus);

  public final static IBuiltInSymbol Filling = S.initFinalSymbol("Filling", ID.Filling);

  public final static IBuiltInSymbol FillingStyle =
      S.initFinalSymbol("FillingStyle", ID.FillingStyle);

  /**
   * FillingTransform(x) - TODO describe `FillingTransform`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FillingTransform.md">FillingTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol FillingTransform =
      S.initFinalSymbol("FillingTransform", ID.FillingTransform);

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
   * FindAstroEvent(x) - TODO describe `FindAstroEvent`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindAstroEvent.md">FindAstroEvent
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindAstroEvent =
      S.initFinalSymbol("FindAstroEvent", ID.FindAstroEvent);

  /**
   * FindClique(graph) - finds a largest clique of `graph` - a set of vertices every two of
   * which are joined by an edge.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindClique.md">FindClique
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindClique = S.initFinalSymbol("FindClique", ID.FindClique);

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

  /**
   * FindCycle(graph) - Find a cycle in the given `graph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindCycle.md">FindCycle
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindCycle = S.initFinalSymbol("FindCycle", ID.FindCycle);

  /**
   * FindDistributionParameters(x) - TODO describe `FindDistributionParameters`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindDistributionParameters.md">FindDistributionParameters
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindDistributionParameters =
      S.initFinalSymbol("FindDistributionParameters", ID.FindDistributionParameters);

  /**
   * FindEdgeColoring(graph) - finds a coloring with a minimal number of colors for the edges of
   * `graph`. The result is a list of integers, one per edge, in the order of `EdgeList(graph)`, with
   * different values for any two edges that share an endpoint.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindEdgeColoring.md">FindEdgeColoring
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindEdgeColoring =
      S.initFinalSymbol("FindEdgeColoring", ID.FindEdgeColoring);

  /**
   * FindEdgeCover(graph) - finds an edge cover of `graph` with a minimum number of edges - a set
   * of edges touching every vertex.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindEdgeCover.md">FindEdgeCover
   *      documentation</a>
   */
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

  /**
   * FindGeneratingFunction({i1, i2, i3, ...}, var) - searches for a unary generating function
   * applied to the variable `var`, where the series coefficients equals `{i1, i2, i3, ...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindGeneratingFunction.md">FindGeneratingFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindGeneratingFunction =
      S.initFinalSymbol("FindGeneratingFunction", ID.FindGeneratingFunction);

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

  /**
   * FindIndependentEdgeSet(graph) - finds an independent edge set of `graph` with a maximum number
   * of edges - a set of edges no two of which are incident to the same vertex.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindIndependentEdgeSet.md">FindIndependentEdgeSet
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindIndependentEdgeSet =
      S.initFinalSymbol("FindIndependentEdgeSet", ID.FindIndependentEdgeSet);

  /**
   * FindIndependentVertexSet(graph) - finds an independent vertex set of `graph` with a maximum
   * number of vertices - a set of vertices no two of which are joined by an edge.
   * FindIndependentVertexSet(graph, nspec, s) gives up to `s` of them, and `All` in place of `s`
   * gives every one.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindIndependentVertexSet.md">FindIndependentVertexSet
   *      documentation</a>
   */
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
   * FindKClan(graph, k) - finds a largest k-clan of `graph` - a k-clique whose induced
   * subgraph has diameter at most `k`. FindKClan(graph, k, nspec, s) gives up to `s` of them, and
   * `All` in place of `s` gives every one.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindKClan.md">FindKClan
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindKClan = S.initFinalSymbol("FindKClan", ID.FindKClan);

  /**
   * FindKClique(graph, k) - finds a largest k-clique of `graph` - a maximal set of vertices
   * that are at a distance no greater than `k` from each other. FindKClique(graph, k, nspec, s)
   * gives up to `s` of them, and `All` in place of `s` gives every one.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindKClique.md">FindKClique
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindKClique = S.initFinalSymbol("FindKClique", ID.FindKClique);

  /**
   * FindKClub(graph, k) - finds a largest k-club of `graph` - a maximal set of vertices
   * whose induced subgraph has diameter at most `k`. FindKClub(graph, k, nspec, s) gives up to `s`
   * of them, and `All` in place of `s` gives every one.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindKClub.md">FindKClub
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindKClub = S.initFinalSymbol("FindKClub", ID.FindKClub);

  /**
   * FindKPlex(graph, k) - finds a largest k-plex of `graph` - a maximal set of vertices in
   * which every vertex is adjacent to all but `k` of the members.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindKPlex.md">FindKPlex
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindKPlex = S.initFinalSymbol("FindKPlex", ID.FindKPlex);

  /**
   * FindLinearRecurrence(list) - compute a minimal linear recurrence which returns list.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindLinearRecurrence.md">FindLinearRecurrence
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindLinearRecurrence =
      S.initFinalSymbol("FindLinearRecurrence", ID.FindLinearRecurrence);

  public final static IBuiltInSymbol FindList = S.initFinalSymbol("FindList", ID.FindList);

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
   * FindMaximumFlow(x) - TODO describe `FindMaximumFlow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindMaximumFlow.md">FindMaximumFlow
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindMaximumFlow =
      S.initFinalSymbol("FindMaximumFlow", ID.FindMaximumFlow);

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
   * FindMoleculeSubstructure(x) - TODO describe `FindMoleculeSubstructure`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindMoleculeSubstructure.md">FindMoleculeSubstructure
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindMoleculeSubstructure =
      S.initFinalSymbol("FindMoleculeSubstructure", ID.FindMoleculeSubstructure);

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
   * FindPlanarColoring(graph) - finds a coloring with a minimal number of colors for the faces
   * of the planar `graph`, so that two faces sharing an edge get different colors.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindPlanarColoring.md">FindPlanarColoring
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindPlanarColoring =
      S.initFinalSymbol("FindPlanarColoring", ID.FindPlanarColoring);

  /**
   * FindPostmanTour(graph) - finds a Chinese postman tour of `graph` - a shortest closed walk
   * traversing every edge at least once.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindPostmanTour.md">FindPostmanTour
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindPostmanTour =
      S.initFinalSymbol("FindPostmanTour", ID.FindPostmanTour);

  /**
   * FindRoot(f, {x, xmin, xmax}) - searches for a numerical root of `f` for the variable `x`, in
   * the range `xmin` to `xmax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindRoot.md">FindRoot
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindRoot = S.initFinalSymbol("FindRoot", ID.FindRoot);

  /**
   * FindSequenceFunction({i1, i2, i3, ...}) - searches for a unary integer function, which
   * generates the integer sequence `{i1, i2, i3, ...}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindSequenceFunction.md">FindSequenceFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindSequenceFunction =
      S.initFinalSymbol("FindSequenceFunction", ID.FindSequenceFunction);

  public final static IBuiltInSymbol FindShortestCurve =
      S.initFinalSymbol("FindShortestCurve", ID.FindShortestCurve);

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
   * FindSolarEclipse(x) - TODO describe `FindSolarEclipse`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindSolarEclipse.md">FindSolarEclipse
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindSolarEclipse =
      S.initFinalSymbol("FindSolarEclipse", ID.FindSolarEclipse);

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
   * FindThreshold(x) - TODO describe `FindThreshold`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindThreshold.md">FindThreshold
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindThreshold =
      S.initFinalSymbol("FindThreshold", ID.FindThreshold);

  /**
   * FindVertexColoring(graph) - finds a coloring with a minimal number of colors for the vertices
   * of `graph`. The result is a list of integers, one per vertex, in the order of
   * `VertexList(graph)`, with different values for the two endpoints of every edge.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindVertexColoring.md">FindVertexColoring
   *      documentation</a>
   */
  /**
   * FindVertexColoring(graph) - finds a coloring with a minimal number of colors for the vertices of
   * `graph`. The result is a list of integers, one per vertex, in the order of `VertexList(graph)`,
   * with different values for the two endpoints of every edge.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FindVertexColoring.md">FindVertexColoring
   *      documentation</a>
   */
  public final static IBuiltInSymbol FindVertexColoring =
      S.initFinalSymbol("FindVertexColoring", ID.FindVertexColoring);

  /**
   * FiniteAbelianGroupCount(order) - returns the number of finite Abelian groups of order `order`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FiniteAbelianGroupCount.md">FiniteAbelianGroupCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol FiniteAbelianGroupCount =
      S.initFinalSymbol("FiniteAbelianGroupCount", ID.FiniteAbelianGroupCount);

  /**
   * FiniteGroupCount(order) - returns the number of finite groups of order `order`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FiniteGroupCount.md">FiniteGroupCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol FiniteGroupCount =
      S.initFinalSymbol("FiniteGroupCount", ID.FiniteGroupCount);

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
   * Fit(list-of-data-points, terms-list, variable) - solve a least squares problem using the
   * Levenberg-Marquardt algorithm.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Fit.md">Fit
   *      documentation</a>
   */
  public final static IBuiltInSymbol Fit = S.initFinalSymbol("Fit", ID.Fit);

  /**
   * FittedModel( ) - `FittedModel` holds the model generated with `LinearModelFit`
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

  public final static IBuiltInSymbol FontColor = S.initFinalSymbol("FontColor", ID.FontColor);

  public final static IBuiltInSymbol FontFamily = S.initFinalSymbol("FontFamily", ID.FontFamily);

  public final static IBuiltInSymbol FontSize = S.initFinalSymbol("FontSize", ID.FontSize);

  /**
   * FontSlant(x) - TODO describe `FontSlant`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FontSlant.md">FontSlant
   *      documentation</a>
   */
  public final static IBuiltInSymbol FontSlant = S.initFinalSymbol("FontSlant", ID.FontSlant);

  /**
   * FontTracking(x) - TODO describe `FontTracking`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FontTracking.md">FontTracking
   *      documentation</a>
   */
  public final static IBuiltInSymbol FontTracking =
      S.initFinalSymbol("FontTracking", ID.FontTracking);

  /**
   * FontWeight(x) - TODO describe `FontWeight`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FontWeight.md">FontWeight
   *      documentation</a>
   */
  public final static IBuiltInSymbol FontWeight = S.initFinalSymbol("FontWeight", ID.FontWeight);

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

  public final static IBuiltInSymbol FormatType = S.initFinalSymbol("FormatType", ID.FormatType);

  public final static IBuiltInSymbol FormBox = S.initFinalSymbol("FormBox", ID.FormBox);

  /**
   * FormulaData(x) - TODO describe `FormulaData`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FormulaData.md">FormulaData
   *      documentation</a>
   */
  public final static IBuiltInSymbol FormulaData = S.initFinalSymbol("FormulaData", ID.FormulaData);

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

  public final static IBuiltInSymbol FourierParameters =
      S.initFinalSymbol("FourierParameters", ID.FourierParameters);

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

  public final static IBuiltInSymbol Frame = S.initFinalSymbol("Frame", ID.Frame);

  public final static IBuiltInSymbol Framed = S.initFinalSymbol("Framed", ID.Framed);

  public final static IBuiltInSymbol FrameLabel = S.initFinalSymbol("FrameLabel", ID.FrameLabel);

  public final static IBuiltInSymbol FrameMargins =
      S.initFinalSymbol("FrameMargins", ID.FrameMargins);

  public final static IBuiltInSymbol FrameStyle = S.initFinalSymbol("FrameStyle", ID.FrameStyle);

  public final static IBuiltInSymbol FrameTicks = S.initFinalSymbol("FrameTicks", ID.FrameTicks);

  public final static IBuiltInSymbol FrameTicksStyle =
      S.initFinalSymbol("FrameTicksStyle", ID.FrameTicksStyle);

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
   * FreeQ(expr, x) - returns `True` if `expr` does not contain the expression `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FreeQ.md">FreeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol FreeQ = S.initFinalSymbol("FreeQ", ID.FreeQ);

  public final static IBuiltInSymbol FresnelC = S.initFinalSymbol("FresnelC", ID.FresnelC);

  public final static IBuiltInSymbol FresnelS = S.initFinalSymbol("FresnelS", ID.FresnelS);

  /**
   * Friday(x) - TODO describe `Friday`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Friday.md">Friday
   *      documentation</a>
   */
  public final static IBuiltInSymbol Friday = S.initFinalSymbol("Friday", ID.Friday);

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
   * FromAbsoluteTime(x) - TODO describe `FromAbsoluteTime`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromAbsoluteTime.md">FromAbsoluteTime
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromAbsoluteTime =
      S.initFinalSymbol("FromAbsoluteTime", ID.FromAbsoluteTime);

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
   * FromDataset(x) - TODO describe `FromDataset`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromDataset.md">FromDataset
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromDataset = S.initFinalSymbol("FromDataset", ID.FromDataset);

  /**
   * FromDateString(x) - TODO describe `FromDateString`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromDateString.md">FromDateString
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromDateString =
      S.initFinalSymbol("FromDateString", ID.FromDateString);

  /**
   * FromDigits(list) - creates an expression from the list of digits for radix `10`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromDigits.md">FromDigits
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromDigits = S.initFinalSymbol("FromDigits", ID.FromDigits);

  /**
   * FromJulianDate(julianDate) - returns the date corresponding to the given Julian date.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromJulianDate.md">FromJulianDate
   *      documentation</a>
   */
  // public final static IBuiltInSymbol FromJulianDate =
  // S.initFinalSymbol("FromJulianDate", ID.FromJulianDate);

  /**
   * FromDMS(x) - TODO describe `FromDMS`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromDMS.md">FromDMS
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromDMS = S.initFinalSymbol("FromDMS", ID.FromDMS);

  /**
   * FromJulianDate(x) - TODO describe `FromJulianDate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromJulianDate.md">FromJulianDate
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromJulianDate =
      S.initFinalSymbol("FromJulianDate", ID.FromJulianDate);

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
   * FromLunationNumber(x) - TODO describe `FromLunationNumber`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromLunationNumber.md">FromLunationNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromLunationNumber =
      S.initFinalSymbol("FromLunationNumber", ID.FromLunationNumber);

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

  /**
   * FromUnixTime(x) - TODO describe `FromUnixTime`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FromUnixTime.md">FromUnixTime
   *      documentation</a>
   */
  public final static IBuiltInSymbol FromUnixTime =
      S.initFinalSymbol("FromUnixTime", ID.FromUnixTime);

  public final static IBuiltInSymbol Full = S.initFinalSymbol("Full", ID.Full);

  /**
   * FullDefinition(symbol) - prints value and rule definitions associated with `symbol` and
   * dependent symbols without attribute `Protected` recursively.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FullDefinition.md">FullDefinition
   *      documentation</a>
   */
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
   * FullMoon(x) - TODO describe `FullMoon`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FullMoon.md">FullMoon
   *      documentation</a>
   */
  public final static IBuiltInSymbol FullMoon = S.initFinalSymbol("FullMoon", ID.FullMoon);

  /**
   * FullRegion(x) - TODO describe `FullRegion`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/FullRegion.md">FullRegion
   *      documentation</a>
   */
  public final static IBuiltInSymbol FullRegion = S.initFinalSymbol("FullRegion", ID.FullRegion);

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

  public final static IBuiltInSymbol FunctionContinuous =
      S.initFinalSymbol("FunctionContinuous", ID.FunctionContinuous);

  public final static IBuiltInSymbol FunctionDiscontinuities =
      S.initFinalSymbol("FunctionDiscontinuities", ID.FunctionDiscontinuities);

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

  public final static IBuiltInSymbol FunctionSingularities =
      S.initFinalSymbol("FunctionSingularities", ID.FunctionSingularities);

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
   * GapPenalty(x) - TODO describe `GapPenalty`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GapPenalty.md">GapPenalty
   *      documentation</a>
   */
  public final static IBuiltInSymbol GapPenalty = S.initFinalSymbol("GapPenalty", ID.GapPenalty);

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

  /**
   * GaussianFilter(x) - TODO describe `GaussianFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GaussianFilter.md">GaussianFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol GaussianFilter =
      S.initFinalSymbol("GaussianFilter", ID.GaussianFilter);

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

  public final static IBuiltInSymbol GeneratedParameters =
      S.initFinalSymbol("GeneratedParameters", ID.GeneratedParameters);

  /**
   * GeneratedQuantityMagnitudes(x) - TODO describe `GeneratedQuantityMagnitudes`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeneratedQuantityMagnitudes.md">GeneratedQuantityMagnitudes
   *      documentation</a>
   */
  public final static IBuiltInSymbol GeneratedQuantityMagnitudes =
      S.initFinalSymbol("GeneratedQuantityMagnitudes", ID.GeneratedQuantityMagnitudes);

  public final static IBuiltInSymbol GeneratingFunction =
      S.initFinalSymbol("GeneratingFunction", ID.GeneratingFunction);

  /**
   * GeoBackground(x) - TODO describe `GeoBackground`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoBackground.md">GeoBackground
   *      documentation</a>
   */
  public final static IBuiltInSymbol GeoBackground =
      S.initFinalSymbol("GeoBackground", ID.GeoBackground);

  /**
   * GeoCenter(x) - TODO describe `GeoCenter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoCenter.md">GeoCenter
   *      documentation</a>
   */
  public final static IBuiltInSymbol GeoCenter = S.initFinalSymbol("GeoCenter", ID.GeoCenter);

  public final static IBuiltInSymbol GeodesyData = S.initFinalSymbol("GeodesyData", ID.GeodesyData);

  /**
   * GeoDestination(position, {distance, azimuth}) - returns the position reached by travelling
   * `distance` from `position` along the rhumb line with the given `azimuth`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoDestination.md">GeoDestination
   *      documentation</a>
   */
  // public final static IBuiltInSymbol GeoDestination =
  // S.initFinalSymbol("GeoDestination", ID.GeoDestination);

  /**
   * GeoDirection(position1, position2) - returns the rhumb line azimuth from `position1` to
   * `position2`, in degrees clockwise from north.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoDirection.md">GeoDirection
   *      documentation</a>
   */
  // public final static IBuiltInSymbol GeoDirection =
  // S.initFinalSymbol("GeoDirection", ID.GeoDirection);

  /**
   * GeoDistance({latitude1,longitude1}, {latitude2,longitude2}) - returns the rhumb line distance
   * between `{latitude1,longitude1}` and `{latitude2,longitude2}`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoDistance.md">GeoDistance
   *      documentation</a>
   */
  public final static IBuiltInSymbol GeoDistance = S.initFinalSymbol("GeoDistance", ID.GeoDistance);

  /**
   * GeoGraphics(x) - TODO describe `GeoGraphics`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoGraphics.md">GeoGraphics
   *      documentation</a>
   */
  public final static IBuiltInSymbol GeoGraphics = S.initFinalSymbol("GeoGraphics", ID.GeoGraphics);

  /**
   * GeoGridLines(x) - TODO describe `GeoGridLines`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoGridLines.md">GeoGridLines
   *      documentation</a>
   */
  public final static IBuiltInSymbol GeoGridLines =
      S.initFinalSymbol("GeoGridLines", ID.GeoGridLines);

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
   * GeoPositionXYZ(GeoPosition({latitude, longitude, altitude})) - returns the geocentric cartesian
   * coordinates `{x, y, z}` of a position on the Earth.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoPositionXYZ.md">GeoPositionXYZ
   *      documentation</a>
   */
  // public final static IBuiltInSymbol GeoPositionXYZ =
  // S.initFinalSymbol("GeoPositionXYZ", ID.GeoPositionXYZ);

  /**
   * GeoProjection(x) - TODO describe `GeoProjection`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoProjection.md">GeoProjection
   *      documentation</a>
   */
  public final static IBuiltInSymbol GeoProjection =
      S.initFinalSymbol("GeoProjection", ID.GeoProjection);

  /**
   * GeoRange(x) - TODO describe `GeoRange`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GeoRange.md">GeoRange
   *      documentation</a>
   */
  public final static IBuiltInSymbol GeoRange = S.initFinalSymbol("GeoRange", ID.GeoRange);

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

  public final static IBuiltInSymbol GlobalClusteringCoefficient =
      S.initFinalSymbol("GlobalClusteringCoefficient", ID.GlobalClusteringCoefficient);

  public final static IBuiltInSymbol Glow = S.initFinalSymbol("Glow", ID.Glow);

  /**
   * GoldbachList(even-number) - return the list of Goldbach prime pairs for the `even-number`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GoldbachList.md">GoldbachList
   *      documentation</a>
   */
  public final static IBuiltInSymbol GoldbachList =
      S.initFinalSymbol("GoldbachList", ID.GoldbachList);

  /**
   * GoldenAngle - is the golden angle `Pi*(3-Sqrt(5))`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GoldenAngle.md">GoldenAngle
   *      documentation</a>
   */
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
   * GradientFilter(x) - TODO describe `GradientFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GradientFilter.md">GradientFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol GradientFilter =
      S.initFinalSymbol("GradientFilter", ID.GradientFilter);

  /**
   * GradientOrientationFilter(x) - TODO describe `GradientOrientationFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GradientOrientationFilter.md">GradientOrientationFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol GradientOrientationFilter =
      S.initFinalSymbol("GradientOrientationFilter", ID.GradientOrientationFilter);

  /**
   * Graph({edge1,...,edgeN}) - create a graph from the given edges `edge1,...,edgeN`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Graph.md">Graph
   *      documentation</a>
   */
  public final static IBuiltInSymbol Graph = S.initFinalSymbol("Graph", ID.Graph);

  public final static IBuiltInSymbol Graph3D = S.initFinalSymbol("Graph3D", ID.Graph3D);

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

  public final static IBuiltInSymbol GraphDistance =
      S.initFinalSymbol("GraphDistance", ID.GraphDistance);

  /**
   * GraphEmbedding(x) - TODO describe `GraphEmbedding`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphEmbedding.md">GraphEmbedding
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphEmbedding =
      S.initFinalSymbol("GraphEmbedding", ID.GraphEmbedding);

  /**
   * GraphHighlight(x) - TODO describe `GraphHighlight`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphHighlight.md">GraphHighlight
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphHighlight =
      S.initFinalSymbol("GraphHighlight", ID.GraphHighlight);

  /**
   * GraphHighlightStyle(x) - TODO describe `GraphHighlightStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphHighlightStyle.md">GraphHighlightStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol GraphHighlightStyle =
      S.initFinalSymbol("GraphHighlightStyle", ID.GraphHighlightStyle);

  /**
   * Graphics(primitives, options) - represents a two-dimensional graphic.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Graphics.md">Graphics
   *      documentation</a>
   */
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

  public final static IBuiltInSymbol GraphicsColumn =
      S.initFinalSymbol("GraphicsColumn", ID.GraphicsColumn);

  public final static IBuiltInSymbol GraphicsComplex =
      S.initFinalSymbol("GraphicsComplex", ID.GraphicsComplex);

  public final static IBuiltInSymbol GraphicsGrid =
      S.initFinalSymbol("GraphicsGrid", ID.GraphicsGrid);

  public final static IBuiltInSymbol GraphicsGroup =
      S.initFinalSymbol("GraphicsGroup", ID.GraphicsGroup);

  public final static IBuiltInSymbol GraphicsJSON =
      S.initFinalSymbol("GraphicsJSON", ID.GraphicsJSON);

  public final static IBuiltInSymbol GraphicsRow = S.initFinalSymbol("GraphicsRow", ID.GraphicsRow);

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

  public final static IBuiltInSymbol GraphLayout = S.initFinalSymbol("GraphLayout", ID.GraphLayout);

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

  public final static IBuiltInSymbol GraphPlot = S.initFinalSymbol("GraphPlot", ID.GraphPlot);

  /**
   * GraphPower(graph, n) - the function uses Dijkstra's algorithm (i.e.
   * [FindShortestPath](FindShortestPath.md)) to find the shortest path between each pair of
   * vertices. If the length of the shortest path is less than or equal to `n`, it adds an edge
   * between the vertices in the new graph. The result is a new graph that is the power of the
   * original graph.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GraphPower.md">GraphPower
   *      documentation</a>
   */
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

  /**
   * Gray - RGB color value for the color gray
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Gray.md">Gray
   *      documentation</a>
   */
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

  /**
   * GreaterEqualLess(x) - TODO describe `GreaterEqualLess`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterEqualLess.md">GreaterEqualLess
   *      documentation</a>
   */
  public final static IBuiltInSymbol GreaterEqualLess =
      S.initFinalSymbol("GreaterEqualLess", ID.GreaterEqualLess);

  /**
   * GreaterEqualThan(rhs) - operator applied to an expr `lhs` (`GreaterEqualThan(rhs)[lhs]`)
   * returns `GreaterEqual(lhs,rhs)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterEqualThan.md">GreaterEqualThan
   *      documentation</a>
   */
  public final static IBuiltInSymbol GreaterEqualThan =
      S.initFinalSymbol("GreaterEqualThan", ID.GreaterEqualThan);

  /**
   * GreaterFullEqual(x) - TODO describe `GreaterFullEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterFullEqual.md">GreaterFullEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol GreaterFullEqual =
      S.initFinalSymbol("GreaterFullEqual", ID.GreaterFullEqual);

  /**
   * GreaterGreater(x) - TODO describe `GreaterGreater`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterGreater.md">GreaterGreater
   *      documentation</a>
   */
  public final static IBuiltInSymbol GreaterGreater =
      S.initFinalSymbol("GreaterGreater", ID.GreaterGreater);

  /**
   * GreaterLess(x) - TODO describe `GreaterLess`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterLess.md">GreaterLess
   *      documentation</a>
   */
  public final static IBuiltInSymbol GreaterLess = S.initFinalSymbol("GreaterLess", ID.GreaterLess);

  /**
   * GreaterSlantEqual(x) - TODO describe `GreaterSlantEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterSlantEqual.md">GreaterSlantEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol GreaterSlantEqual =
      S.initFinalSymbol("GreaterSlantEqual", ID.GreaterSlantEqual);

  /**
   * GreaterThan(rhs) - operator applied to an expr `lhs` (`GreaterThan(rhs)[lhs]`) returns
   * `Greater(lhs,rhs)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterThan.md">GreaterThan
   *      documentation</a>
   */
  public final static IBuiltInSymbol GreaterThan = S.initFinalSymbol("GreaterThan", ID.GreaterThan);

  /**
   * GreaterTilde(x) - TODO describe `GreaterTilde`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GreaterTilde.md">GreaterTilde
   *      documentation</a>
   */
  public final static IBuiltInSymbol GreaterTilde =
      S.initFinalSymbol("GreaterTilde", ID.GreaterTilde);

  /**
   * Green - RGB color value for the color green
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Green.md">Green
   *      documentation</a>
   */
  public final static IBuiltInSymbol Green = S.initFinalSymbol("Green", ID.Green);

  /**
   * Grid(x) - TODO describe `Grid`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Grid.md">Grid
   *      documentation</a>
   */
  public final static IBuiltInSymbol Grid = S.initFinalSymbol("Grid", ID.Grid);

  /**
   * GridGraph({v1,v2}) - returns the grid graph with `v1 x v2` vertices.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GridGraph.md">GridGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol GridGraph = S.initFinalSymbol("GridGraph", ID.GridGraph);

  public final static IBuiltInSymbol GridLines = S.initFinalSymbol("GridLines", ID.GridLines);

  public final static IBuiltInSymbol GridLinesStyle =
      S.initFinalSymbol("GridLinesStyle", ID.GridLinesStyle);

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

  public final static IBuiltInSymbol Groupings = S.initFinalSymbol("Groupings", ID.Groupings);

  /**
   * GroupOrbits(x) - TODO describe `GroupOrbits`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/GroupOrbits.md">GroupOrbits
   *      documentation</a>
   */
  public final static IBuiltInSymbol GroupOrbits = S.initFinalSymbol("GroupOrbits", ID.GroupOrbits);

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

  public final static IBuiltInSymbol HalfLine = S.initFinalSymbol("HalfLine", ID.HalfLine);

  public final static IBuiltInSymbol HalfNormalDistribution =
      S.initFinalSymbol("HalfNormalDistribution", ID.HalfNormalDistribution);

  /**
   * HalfPlane(x) - TODO describe `HalfPlane`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HalfPlane.md">HalfPlane
   *      documentation</a>
   */
  public final static IBuiltInSymbol HalfPlane = S.initFinalSymbol("HalfPlane", ID.HalfPlane);

  /**
   * HalfSpace(x) - TODO describe `HalfSpace`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HalfSpace.md">HalfSpace
   *      documentation</a>
   */
  public final static IBuiltInSymbol HalfSpace = S.initFinalSymbol("HalfSpace", ID.HalfSpace);

  /**
   * Haloing(x) - TODO describe `Haloing`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Haloing.md">Haloing
   *      documentation</a>
   */
  public final static IBuiltInSymbol Haloing = S.initFinalSymbol("Haloing", ID.Haloing);

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

  /**
   * HankelH1(n, x) - returns Hankel function of the first kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HankelH1.md">HankelH1
   *      documentation</a>
   */
  public final static IBuiltInSymbol HankelH1 = S.initFinalSymbol("HankelH1", ID.HankelH1);

  /**
   * HankelH2(n, x) - returns Hankel function of the second kind.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HankelH2.md">HankelH2
   *      documentation</a>
   */
  public final static IBuiltInSymbol HankelH2 = S.initFinalSymbol("HankelH2", ID.HankelH2);

  /**
   * HankelMatrix(n) - gives a Hankel matrix with the dimension `n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HankelMatrix.md">HankelMatrix
   *      documentation</a>
   */
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
   * Hash(expression) - the `Hash` function computes a hash value for any `expression`. It can
   * generate both non-cryptographic integer hash codes and cryptographic hashes.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hash.md">Hash
   *      documentation</a>
   */
  public final static IBuiltInSymbol Hash = S.initFinalSymbol("Hash", ID.Hash);

  /**
   * Haversine(z) - returns the haversine function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Haversine.md">Haversine
   *      documentation</a>
   */
  public final static IBuiltInSymbol Haversine = S.initFinalSymbol("Haversine", ID.Haversine);

  /**
   * HazardFunction(x) - TODO describe `HazardFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HazardFunction.md">HazardFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol HazardFunction =
      S.initFinalSymbol("HazardFunction", ID.HazardFunction);

  /**
   * Head(expr) - returns the head of the expression or atom `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Head.md">Head
   *      documentation</a>
   */
  public final static IBuiltInSymbol Head = S.initFinalSymbol("Head", ID.Head);

  /**
   * HeaderAlignment(x) - TODO describe `HeaderAlignment`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HeaderAlignment.md">HeaderAlignment
   *      documentation</a>
   */
  public final static IBuiltInSymbol HeaderAlignment =
      S.initFinalSymbol("HeaderAlignment", ID.HeaderAlignment);

  /**
   * HeaderBackground(x) - TODO describe `HeaderBackground`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HeaderBackground.md">HeaderBackground
   *      documentation</a>
   */
  public final static IBuiltInSymbol HeaderBackground =
      S.initFinalSymbol("HeaderBackground", ID.HeaderBackground);

  /**
   * HeaderDisplayFunction(x) - TODO describe `HeaderDisplayFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HeaderDisplayFunction.md">HeaderDisplayFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol HeaderDisplayFunction =
      S.initFinalSymbol("HeaderDisplayFunction", ID.HeaderDisplayFunction);

  /**
   * HeaderLines(x) - TODO describe `HeaderLines`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HeaderLines.md">HeaderLines
   *      documentation</a>
   */
  public final static IBuiltInSymbol HeaderLines = S.initFinalSymbol("HeaderLines", ID.HeaderLines);

  /**
   * HeaderSize(x) - TODO describe `HeaderSize`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HeaderSize.md">HeaderSize
   *      documentation</a>
   */
  public final static IBuiltInSymbol HeaderSize = S.initFinalSymbol("HeaderSize", ID.HeaderSize);

  /**
   * HeaderStyle(x) - TODO describe `HeaderStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HeaderStyle.md">HeaderStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol HeaderStyle = S.initFinalSymbol("HeaderStyle", ID.HeaderStyle);

  public final static IBuiltInSymbol Heads = S.initFinalSymbol("Heads", ID.Heads);

  public final static IBuiltInSymbol HeavisideLambda =
      S.initFinalSymbol("HeavisideLambda", ID.HeavisideLambda);

  public final static IBuiltInSymbol HeavisidePi = S.initFinalSymbol("HeavisidePi", ID.HeavisidePi);

  /**
   * HeavisideTheta(expr1, expr2, ..., exprN) - returns `1` if all `expr1, expr2, ..., exprN` are
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
   * HermiteDecomposition(matrix) - calculate the Hermite-decomposition as a list `{u,r}` of a
   * square `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HermiteDecomposition.md">HermiteDecomposition
   *      documentation</a>
   */
  public final static IBuiltInSymbol HermiteDecomposition =
      S.initFinalSymbol("HermiteDecomposition", ID.HermiteDecomposition);

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

  public final static IBuiltInSymbol Hexahedron = S.initFinalSymbol("Hexahedron", ID.Hexahedron);

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
   * HiddenItems(x) - TODO describe `HiddenItems`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HiddenItems.md">HiddenItems
   *      documentation</a>
   */
  public final static IBuiltInSymbol HiddenItems = S.initFinalSymbol("HiddenItems", ID.HiddenItems);

  /**
   * Highlighted(x) - TODO describe `Highlighted`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Highlighted.md">Highlighted
   *      documentation</a>
   */
  public final static IBuiltInSymbol Highlighted = S.initFinalSymbol("Highlighted", ID.Highlighted);

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

  /**
   * HistogramDistribution(x) - TODO describe `HistogramDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HistogramDistribution.md">HistogramDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol HistogramDistribution =
      S.initFinalSymbol("HistogramDistribution", ID.HistogramDistribution);

  /**
   * HistogramList(x) - TODO describe `HistogramList`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HistogramList.md">HistogramList
   *      documentation</a>
   */
  public final static IBuiltInSymbol HistogramList =
      S.initFinalSymbol("HistogramList", ID.HistogramList);

  /**
   * HistogramTransform(x) - TODO describe `HistogramTransform`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HistogramTransform.md">HistogramTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol HistogramTransform =
      S.initFinalSymbol("HistogramTransform", ID.HistogramTransform);

  /**
   * HodgeDual(tensor,dimensions,slots) - `HodgeDual` evaluates the Hodge star of a tensor.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HodgeDual.md">HodgeDual
   *      documentation</a>
   */
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

  /**
   * HoldAllComplete - is an attribute specifying that all arguments of a function should be left
   * completely unevaluated and `Sequence` expressions shouldn't be flattened out.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HoldAllComplete.md">HoldAllComplete
   *      documentation</a>
   */
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
   * HumpDownHump(x) - TODO describe `HumpDownHump`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HumpDownHump.md">HumpDownHump
   *      documentation</a>
   */
  public final static IBuiltInSymbol HumpDownHump =
      S.initFinalSymbol("HumpDownHump", ID.HumpDownHump);

  /**
   * HumpEqual(x) - TODO describe `HumpEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HumpEqual.md">HumpEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol HumpEqual = S.initFinalSymbol("HumpEqual", ID.HumpEqual);

  /**
   * HurwitzLerchPhi(z, s, a) - returns the Hurwitz-Lerch transcendent function.
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

  /**
   * HyperCubeGraph(order) - the hypercube graph `Q_n` is the graph formed from the vertices and
   * edges of an n-dimensional hypercube. For instance, the cube graph `Q_3` is the graph formed by
   * the 8 vertices and 12 edges of a three-dimensional cube.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HypercubeGraph.md">HypercubeGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol HypercubeGraph =
      S.initFinalSymbol("HypercubeGraph", ID.HypercubeGraph);

  /**
   * Hyperfactorial(n) - returns the hyper factorial number of the integer `n`. The hyperfactorial
   * of a positive integer n is the product of the numbers of the form `x^x` from `1^1` to `n^n`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Hyperfactorial.md">Hyperfactorial
   *      documentation</a>
   */
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

  /**
   * HypergeometricU(a, b, z) - return the Tricomi confluent hypergeometric function
   * `HypergeometricU` fu
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HypergeometricU.md">HypergeometricU
   *      documentation</a>
   */
  public final static IBuiltInSymbol HypergeometricU =
      S.initFinalSymbol("HypergeometricU", ID.HypergeometricU);

  /**
   * HyperHarmonicNumber(r, n) - returns the `n`th hyperharmonic number of order `r`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HyperHarmonicNumber.md">HyperHarmonicNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol HyperHarmonicNumber =
      S.initFinalSymbol("HyperHarmonicNumber", ID.HyperHarmonicNumber);

  /**
   * HypoexponentialDistribution(x) - TODO describe `HypoexponentialDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/HypoexponentialDistribution.md">HypoexponentialDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol HypoexponentialDistribution =
      S.initFinalSymbol("HypoexponentialDistribution", ID.HypoexponentialDistribution);

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

  /**
   * ImageAdd(x) - TODO describe `ImageAdd`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageAdd.md">ImageAdd
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageAdd = S.initFinalSymbol("ImageAdd", ID.ImageAdd);

  /**
   * ImageAdjust(x) - TODO describe `ImageAdjust`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageAdjust.md">ImageAdjust
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageAdjust = S.initFinalSymbol("ImageAdjust", ID.ImageAdjust);

  /**
   * ImageApply(x) - TODO describe `ImageApply`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageApply.md">ImageApply
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageApply = S.initFinalSymbol("ImageApply", ID.ImageApply);

  /**
   * ImageAspectRatio(x) - TODO describe `ImageAspectRatio`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageAspectRatio.md">ImageAspectRatio
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageAspectRatio =
      S.initFinalSymbol("ImageAspectRatio", ID.ImageAspectRatio);

  /**
   * ImageAssemble(x) - TODO describe `ImageAssemble`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageAssemble.md">ImageAssemble
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageAssemble =
      S.initFinalSymbol("ImageAssemble", ID.ImageAssemble);

  public final static IBuiltInSymbol ImageChannels =
      S.initFinalSymbol("ImageChannels", ID.ImageChannels);

  /**
   * ImageClip(x) - TODO describe `ImageClip`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageClip.md">ImageClip
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageClip = S.initFinalSymbol("ImageClip", ID.ImageClip);

  public final static IBuiltInSymbol ImageColorSpace =
      S.initFinalSymbol("ImageColorSpace", ID.ImageColorSpace);

  /**
   * ImageCompose(x) - TODO describe `ImageCompose`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageCompose.md">ImageCompose
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageCompose =
      S.initFinalSymbol("ImageCompose", ID.ImageCompose);

  /**
   * ImageConvolve(x) - TODO describe `ImageConvolve`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageConvolve.md">ImageConvolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageConvolve =
      S.initFinalSymbol("ImageConvolve", ID.ImageConvolve);

  /**
   * ImageCorners(x) - TODO describe `ImageCorners`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageCorners.md">ImageCorners
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageCorners =
      S.initFinalSymbol("ImageCorners", ID.ImageCorners);

  /**
   * ImageCorrelate(x) - TODO describe `ImageCorrelate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageCorrelate.md">ImageCorrelate
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageCorrelate =
      S.initFinalSymbol("ImageCorrelate", ID.ImageCorrelate);

  public final static IBuiltInSymbol ImageCrop = S.initFinalSymbol("ImageCrop", ID.ImageCrop);

  public final static IBuiltInSymbol ImageData = S.initFinalSymbol("ImageData", ID.ImageData);

  /**
   * ImageDeconvolve(x) - TODO describe `ImageDeconvolve`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageDeconvolve.md">ImageDeconvolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageDeconvolve =
      S.initFinalSymbol("ImageDeconvolve", ID.ImageDeconvolve);

  /**
   * ImageDifference(x) - TODO describe `ImageDifference`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageDifference.md">ImageDifference
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageDifference =
      S.initFinalSymbol("ImageDifference", ID.ImageDifference);

  public final static IBuiltInSymbol ImageDimensions =
      S.initFinalSymbol("ImageDimensions", ID.ImageDimensions);

  /**
   * ImageDivide(x) - TODO describe `ImageDivide`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageDivide.md">ImageDivide
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageDivide = S.initFinalSymbol("ImageDivide", ID.ImageDivide);

  /**
   * ImageEffect(x) - TODO describe `ImageEffect`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageEffect.md">ImageEffect
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageEffect = S.initFinalSymbol("ImageEffect", ID.ImageEffect);

  /**
   * ImageFilter(x) - TODO describe `ImageFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageFilter.md">ImageFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageFilter = S.initFinalSymbol("ImageFilter", ID.ImageFilter);

  /**
   * ImageForwardTransformation(x) - TODO describe `ImageForwardTransformation`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageForwardTransformation.md">ImageForwardTransformation
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageForwardTransformation =
      S.initFinalSymbol("ImageForwardTransformation", ID.ImageForwardTransformation);

  /**
   * ImageHistogram(x) - TODO describe `ImageHistogram`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageHistogram.md">ImageHistogram
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageHistogram =
      S.initFinalSymbol("ImageHistogram", ID.ImageHistogram);

  /**
   * ImageKeypoints(x) - TODO describe `ImageKeypoints`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageKeypoints.md">ImageKeypoints
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageKeypoints =
      S.initFinalSymbol("ImageKeypoints", ID.ImageKeypoints);

  /**
   * ImageLines(x) - TODO describe `ImageLines`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageLines.md">ImageLines
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageLines = S.initFinalSymbol("ImageLines", ID.ImageLines);

  public final static IBuiltInSymbol ImageMargins =
      S.initFinalSymbol("ImageMargins", ID.ImageMargins);

  /**
   * ImageMeasurements(x) - TODO describe `ImageMeasurements`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageMeasurements.md">ImageMeasurements
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageMeasurements =
      S.initFinalSymbol("ImageMeasurements", ID.ImageMeasurements);

  /**
   * ImageMultiply(x) - TODO describe `ImageMultiply`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageMultiply.md">ImageMultiply
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageMultiply =
      S.initFinalSymbol("ImageMultiply", ID.ImageMultiply);

  /**
   * ImagePad(x) - TODO describe `ImagePad`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImagePad.md">ImagePad
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImagePad = S.initFinalSymbol("ImagePad", ID.ImagePad);

  public final static IBuiltInSymbol ImagePadding =
      S.initFinalSymbol("ImagePadding", ID.ImagePadding);

  /**
   * ImagePartition(x) - TODO describe `ImagePartition`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImagePartition.md">ImagePartition
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImagePartition =
      S.initFinalSymbol("ImagePartition", ID.ImagePartition);

  /**
   * ImagePerspectiveTransformation(x) - TODO describe `ImagePerspectiveTransformation`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImagePerspectiveTransformation.md">ImagePerspectiveTransformation
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImagePerspectiveTransformation =
      S.initFinalSymbol("ImagePerspectiveTransformation", ID.ImagePerspectiveTransformation);

  public final static IBuiltInSymbol ImageQ = S.initFinalSymbol("ImageQ", ID.ImageQ);

  /**
   * ImageReflect(x) - TODO describe `ImageReflect`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageReflect.md">ImageReflect
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageReflect =
      S.initFinalSymbol("ImageReflect", ID.ImageReflect);

  public final static IBuiltInSymbol ImageResize = S.initFinalSymbol("ImageResize", ID.ImageResize);

  /**
   * ImageResolution(x) - TODO describe `ImageResolution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageResolution.md">ImageResolution
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageResolution =
      S.initFinalSymbol("ImageResolution", ID.ImageResolution);

  public final static IBuiltInSymbol ImageRotate = S.initFinalSymbol("ImageRotate", ID.ImageRotate);

  public final static IBuiltInSymbol ImageScaled = S.initFinalSymbol("ImageScaled", ID.ImageScaled);

  /**
   * ImageScan(x) - TODO describe `ImageScan`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageScan.md">ImageScan
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageScan = S.initFinalSymbol("ImageScan", ID.ImageScan);

  /**
   * ImageSegmentationComponents(x) - TODO describe `ImageSegmentationComponents`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageSegmentationComponents.md">ImageSegmentationComponents
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageSegmentationComponents =
      S.initFinalSymbol("ImageSegmentationComponents", ID.ImageSegmentationComponents);

  public final static IBuiltInSymbol ImageSize = S.initFinalSymbol("ImageSize", ID.ImageSize);

  /**
   * ImageSizeRaw(x) - TODO describe `ImageSizeRaw`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageSizeRaw.md">ImageSizeRaw
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageSizeRaw =
      S.initFinalSymbol("ImageSizeRaw", ID.ImageSizeRaw);

  /**
   * ImageSubtract(x) - TODO describe `ImageSubtract`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageSubtract.md">ImageSubtract
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageSubtract =
      S.initFinalSymbol("ImageSubtract", ID.ImageSubtract);

  /**
   * ImageTake(x) - TODO describe `ImageTake`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageTake.md">ImageTake
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageTake = S.initFinalSymbol("ImageTake", ID.ImageTake);

  /**
   * ImageTransformation(x) - TODO describe `ImageTransformation`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageTransformation.md">ImageTransformation
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageTransformation =
      S.initFinalSymbol("ImageTransformation", ID.ImageTransformation);

  /**
   * ImageTrim(x) - TODO describe `ImageTrim`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageTrim.md">ImageTrim
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageTrim = S.initFinalSymbol("ImageTrim", ID.ImageTrim);

  public final static IBuiltInSymbol ImageType = S.initFinalSymbol("ImageType", ID.ImageType);

  /**
   * ImageValue(x) - TODO describe `ImageValue`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageValue.md">ImageValue
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageValue = S.initFinalSymbol("ImageValue", ID.ImageValue);

  /**
   * ImageValuePositions(x) - TODO describe `ImageValuePositions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImageValuePositions.md">ImageValuePositions
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImageValuePositions =
      S.initFinalSymbol("ImageValuePositions", ID.ImageValuePositions);

  public final static IBuiltInSymbol ImplicitD = S.initFinalSymbol("ImplicitD", ID.ImplicitD);

  /**
   * ImplicitRegion(x) - TODO describe `ImplicitRegion`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImplicitRegion.md">ImplicitRegion
   *      documentation</a>
   */
  public final static IBuiltInSymbol ImplicitRegion =
      S.initFinalSymbol("ImplicitRegion", ID.ImplicitRegion);

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

  /**
   * ImportString(string, import-format) - import the `string` from `import-format`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ImportString.md">ImportString
   *      documentation</a>
   */
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

  public final static IBuiltInSymbol Inactivate = S.initFinalSymbol("Inactivate", ID.Inactivate);

  public final static IBuiltInSymbol Inactive = S.initFinalSymbol("Inactive", ID.Inactive);

  public final static IBuiltInSymbol IncidenceMatrix =
      S.initFinalSymbol("IncidenceMatrix", ID.IncidenceMatrix);

  /**
   * IncludeAromaticBonds(x) - TODO describe `IncludeAromaticBonds`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IncludeAromaticBonds.md">IncludeAromaticBonds
   *      documentation</a>
   */
  public final static IBuiltInSymbol IncludeAromaticBonds =
      S.initFinalSymbol("IncludeAromaticBonds", ID.IncludeAromaticBonds);

  /**
   * IncludeHydrogens(x) - TODO describe `IncludeHydrogens`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IncludeHydrogens.md">IncludeHydrogens
   *      documentation</a>
   */
  public final static IBuiltInSymbol IncludeHydrogens =
      S.initFinalSymbol("IncludeHydrogens", ID.IncludeHydrogens);

  /**
   * IncludeMetaInformation(x) - TODO describe `IncludeMetaInformation`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IncludeMetaInformation.md">IncludeMetaInformation
   *      documentation</a>
   */
  public final static IBuiltInSymbol IncludeMetaInformation =
      S.initFinalSymbol("IncludeMetaInformation", ID.IncludeMetaInformation);

  /**
   * IncludeQuantities(x) - TODO describe `IncludeQuantities`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IncludeQuantities.md">IncludeQuantities
   *      documentation</a>
   */
  /**
   * IncludeOuterFace - an option for `PlanarFaceList`, specifying whether the unbounded outer
   * face of a planar graph is part of the result.
   */
  public final static IBuiltInSymbol IncludeOuterFace =
      S.initFinalSymbol("IncludeOuterFace", ID.IncludeOuterFace);

  public final static IBuiltInSymbol IncludeQuantities =
      S.initFinalSymbol("IncludeQuantities", ID.IncludeQuantities);

  /**
   * Increment(x) - increments `x` by `1`, returning the original value of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Increment.md">Increment
   *      documentation</a>
   */
  public final static IBuiltInSymbol Increment = S.initFinalSymbol("Increment", ID.Increment);

  /**
   * IndependentEdgeSetQ(graph, edges) - yields `True` if the edge list `edges` is an
   * independent edge set of `graph`, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IndependentEdgeSetQ.md">IndependentEdgeSetQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol IndependentEdgeSetQ =
      S.initFinalSymbol("IndependentEdgeSetQ", ID.IndependentEdgeSetQ);

  /**
   * IndependentPhysicalQuantity(x) - TODO describe `IndependentPhysicalQuantity`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IndependentPhysicalQuantity.md">IndependentPhysicalQuantity
   *      documentation</a>
   */
  public final static IBuiltInSymbol IndependentPhysicalQuantity =
      S.initFinalSymbol("IndependentPhysicalQuantity", ID.IndependentPhysicalQuantity);

  /**
   * IndependentUnit(x) - TODO describe `IndependentUnit`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IndependentUnit.md">IndependentUnit
   *      documentation</a>
   */
  public final static IBuiltInSymbol IndependentUnit =
      S.initFinalSymbol("IndependentUnit", ID.IndependentUnit);

  /**
   * IndependentUnitDimension(x) - TODO describe `IndependentUnitDimension`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IndependentUnitDimension.md">IndependentUnitDimension
   *      documentation</a>
   */
  public final static IBuiltInSymbol IndependentUnitDimension =
      S.initFinalSymbol("IndependentUnitDimension", ID.IndependentUnitDimension);

  /**
   * IndependentVertexSetQ(graph, vertices) - yields `True` if the vertex list `vertices` is an
   * independent vertex set of `graph`, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IndependentVertexSetQ.md">IndependentVertexSetQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol IndependentVertexSetQ =
      S.initFinalSymbol("IndependentVertexSetQ", ID.IndependentVertexSetQ);

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

  public final static IBuiltInSymbol InfiniteLine =
      S.initFinalSymbol("InfiniteLine", ID.InfiniteLine);

  /**
   * InfinitePlane(x) - TODO describe `InfinitePlane`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InfinitePlane.md">InfinitePlane
   *      documentation</a>
   */
  public final static IBuiltInSymbol InfinitePlane =
      S.initFinalSymbol("InfinitePlane", ID.InfinitePlane);

  /**
   * Infinity - represents an infinite real quantity.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Infinity.md">Infinity
   *      documentation</a>
   */
  public final static IBuiltInSymbol Infinity = S.initFinalSymbol("Infinity", ID.Infinity);

  public final static IBuiltInSymbol Infix = S.initFinalSymbol("Infix", ID.Infix);

  /**
   * InflationAdjust(x) - TODO describe `InflationAdjust`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InflationAdjust.md">InflationAdjust
   *      documentation</a>
   */
  public final static IBuiltInSymbol InflationAdjust =
      S.initFinalSymbol("InflationAdjust", ID.InflationAdjust);

  public final static IBuiltInSymbol Information = S.initFinalSymbol("Information", ID.Information);

  public final static IBuiltInSymbol Inherited = S.initFinalSymbol("Inherited", ID.Inherited);

  /**
   * Initialization(x) - TODO describe `Initialization`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Initialization.md">Initialization
   *      documentation</a>
   */
  public final static IBuiltInSymbol Initialization =
      S.initFinalSymbol("Initialization", ID.Initialization);

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
   * Inpaint(x) - TODO describe `Inpaint`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Inpaint.md">Inpaint
   *      documentation</a>
   */
  public final static IBuiltInSymbol Inpaint = S.initFinalSymbol("Inpaint", ID.Inpaint);

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

  public final static IBuiltInSymbol Inset = S.initFinalSymbol("Inset", ID.Inset);

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
   * Interleaving(x) - TODO describe `Interleaving`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Interleaving.md">Interleaving
   *      documentation</a>
   */
  public final static IBuiltInSymbol Interleaving =
      S.initFinalSymbol("Interleaving", ID.Interleaving);

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

  /**
   * Interpolation(data-list) - return an `InterpolationFunction` for the `data-list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Interpolation.md">Interpolation
   *      documentation</a>
   */
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
   * IntervalMarkers(x) - TODO describe `IntervalMarkers`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalMarkers.md">IntervalMarkers
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntervalMarkers =
      S.initFinalSymbol("IntervalMarkers", ID.IntervalMarkers);

  /**
   * IntervalMarkersStyle(x) - TODO describe `IntervalMarkersStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalMarkersStyle.md">IntervalMarkersStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntervalMarkersStyle =
      S.initFinalSymbol("IntervalMarkersStyle", ID.IntervalMarkersStyle);

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
   * IntervalSlider(x) - TODO describe `IntervalSlider`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IntervalSlider.md">IntervalSlider
   *      documentation</a>
   */
  public final static IBuiltInSymbol IntervalSlider =
      S.initFinalSymbol("IntervalSlider", ID.IntervalSlider);

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

  /**
   * InverseFunctions(x) - TODO describe `InverseFunctions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseFunctions.md">InverseFunctions
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseFunctions =
      S.initFinalSymbol("InverseFunctions", ID.InverseFunctions);

  /**
   * InverseGammaDistribution(a,b) - returns a inverse gamma distribution.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseGammaDistribution.md">InverseGammaDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseGammaDistribution =
      S.initFinalSymbol("InverseGammaDistribution", ID.InverseGammaDistribution);

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

  /**
   * InverseSurvivalFunction(x) - TODO describe `InverseSurvivalFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InverseSurvivalFunction.md">InverseSurvivalFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol InverseSurvivalFunction =
      S.initFinalSymbol("InverseSurvivalFunction", ID.InverseSurvivalFunction);

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
   * InvisiblePostfixScriptBase(x) - TODO describe `InvisiblePostfixScriptBase`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InvisiblePostfixScriptBase.md">InvisiblePostfixScriptBase
   *      documentation</a>
   */
  public final static IBuiltInSymbol InvisiblePostfixScriptBase =
      S.initFinalSymbol("InvisiblePostfixScriptBase", ID.InvisiblePostfixScriptBase);

  /**
   * InvisiblePrefixScriptBase(x) - TODO describe `InvisiblePrefixScriptBase`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/InvisiblePrefixScriptBase.md">InvisiblePrefixScriptBase
   *      documentation</a>
   */
  public final static IBuiltInSymbol InvisiblePrefixScriptBase =
      S.initFinalSymbol("InvisiblePrefixScriptBase", ID.InvisiblePrefixScriptBase);

  public final static IBuiltInSymbol IrreduciblePolynomialQ =
      S.initFinalSymbol("IrreduciblePolynomialQ", ID.IrreduciblePolynomialQ);

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
   * IsotopeData(x) - TODO describe `IsotopeData`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/IsotopeData.md">IsotopeData
   *      documentation</a>
   */
  public final static IBuiltInSymbol IsotopeData = S.initFinalSymbol("IsotopeData", ID.IsotopeData);

  /**
   * Italic(x) - TODO describe `Italic`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Italic.md">Italic
   *      documentation</a>
   */
  public final static IBuiltInSymbol Italic = S.initFinalSymbol("Italic", ID.Italic);

  /**
   * Item(x) - TODO describe `Item`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Item.md">Item
   *      documentation</a>
   */
  public final static IBuiltInSymbol Item = S.initFinalSymbol("Item", ID.Item);

  /**
   * ItemDisplayFunction(x) - TODO describe `ItemDisplayFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ItemDisplayFunction.md">ItemDisplayFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol ItemDisplayFunction =
      S.initFinalSymbol("ItemDisplayFunction", ID.ItemDisplayFunction);

  /**
   * ItemSize(x) - TODO describe `ItemSize`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ItemSize.md">ItemSize
   *      documentation</a>
   */
  public final static IBuiltInSymbol ItemSize = S.initFinalSymbol("ItemSize", ID.ItemSize);

  /**
   * ItemStyle(x) - TODO describe `ItemStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ItemStyle.md">ItemStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol ItemStyle = S.initFinalSymbol("ItemStyle", ID.ItemStyle);

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

  /**
   * JulianDate(date) - returns the Julian date of `date`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JulianDate.md">JulianDate
   *      documentation</a>
   */
  // public final static IBuiltInSymbol JulianDate = S.initFinalSymbol("JulianDate", ID.JulianDate);

  /**
   * JoinAcross(x) - TODO describe `JoinAcross`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JoinAcross.md">JoinAcross
   *      documentation</a>
   */
  public final static IBuiltInSymbol JoinAcross = S.initFinalSymbol("JoinAcross", ID.JoinAcross);

  public final static IBuiltInSymbol Joined = S.initFinalSymbol("Joined", ID.Joined);

  /**
   * JoinedCurve(x) - TODO describe `JoinedCurve`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JoinedCurve.md">JoinedCurve
   *      documentation</a>
   */
  public final static IBuiltInSymbol JoinedCurve = S.initFinalSymbol("JoinedCurve", ID.JoinedCurve);

  public final static IBuiltInSymbol JoinForm = S.initFinalSymbol("JoinForm", ID.JoinForm);

  /**
   * JordanDecomposition(matrix) - calculate the Jordan-decomposition as a list `{s, j}` of a square
   * `matrix` with the property `s.j.Inverse(s) == matrix`, where `s` is the similarity matrix and
   * `j` is the Jordan normal form of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JordanDecomposition.md">JordanDecomposition
   *      documentation</a>
   */
  public final static IBuiltInSymbol JordanDecomposition =
      S.initFinalSymbol("JordanDecomposition", ID.JordanDecomposition);

  /**
   * JSForm(expr) - returns the JavaScript form of the `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JSForm.md">JSForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol JSForm = S.initFinalSymbol("JSForm", ID.JSForm);

  public final static IBuiltInSymbol JSFormData = S.initFinalSymbol("JSFormData", ID.JSFormData);

  /**
   * JulianDate(x) - TODO describe `JulianDate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/JulianDate.md">JulianDate
   *      documentation</a>
   */
  public final static IBuiltInSymbol JulianDate = S.initFinalSymbol("JulianDate", ID.JulianDate);

  /**
   * KagiChart(x) - TODO describe `KagiChart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KagiChart.md">KagiChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol KagiChart = S.initFinalSymbol("KagiChart", ID.KagiChart);

  /**
   * KaryTree(v) - create a binary tree graph with `v` vertices.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KaryTree.md">KaryTree
   *      documentation</a>
   */
  public final static IBuiltInSymbol KaryTree = S.initFinalSymbol("KaryTree", ID.KaryTree);

  /**
   * KCoreComponents(graph, k) - gives the k-core components of `graph` - the maximal weakly
   * connected subgraphs in which every vertex has degree at least `k`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KCoreComponents.md">KCoreComponents
   *      documentation</a>
   */
  public final static IBuiltInSymbol KCoreComponents =
      S.initFinalSymbol("KCoreComponents", ID.KCoreComponents);

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

  /**
   * KeyDrop(<|key1->value1, ...|>, {k1, k2,...}) - `KeyDrop` is a function used to remove specified
   * keys `{k1, k2,...}` and their associated values from an association. It is useful for
   * simplifying or filtering associations by excluding unwanted keys.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeyDrop.md">KeyDrop
   *      documentation</a>
   */
  public final static IBuiltInSymbol KeyDrop = S.initFinalSymbol("KeyDrop", ID.KeyDrop);

  public final static IBuiltInSymbol KeyDropFrom = S.initFinalSymbol("KeyDropFrom", ID.KeyDropFrom);

  public final static IBuiltInSymbol KeyExistsQ = S.initFinalSymbol("KeyExistsQ", ID.KeyExistsQ);

  public final static IBuiltInSymbol KeyFreeQ = S.initFinalSymbol("KeyFreeQ", ID.KeyFreeQ);

  public final static IBuiltInSymbol KeyMap = S.initFinalSymbol("KeyMap", ID.KeyMap);

  public final static IBuiltInSymbol KeyMemberQ = S.initFinalSymbol("KeyMemberQ", ID.KeyMemberQ);

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

  public final static IBuiltInSymbol KeySortBy = S.initFinalSymbol("KeySortBy", ID.KeySortBy);

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
   * KeyComplement({assoc1, assoc2, ...}) - returns an association with the rules of `assoc1` whose
   * keys do not appear in any of `assoc2, ...`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeyComplement.md">KeyComplement
   *      documentation</a>
   */
  public final static IBuiltInSymbol KeyComplement =
      S.initFinalSymbol("KeyComplement", ID.KeyComplement);

  /**
   * KeyIntersection({assoc1, assoc2, ...}) - returns a list of associations, in which every
   * association is restricted to the keys which are common to all `assoc1, assoc2, ...`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeyIntersection.md">KeyIntersection
   *      documentation</a>
   */
  public final static IBuiltInSymbol KeyIntersection =
      S.initFinalSymbol("KeyIntersection", ID.KeyIntersection);

  /**
   * KeyUnion({assoc1, assoc2, ...}) - returns a list of associations, in which every association is
   * padded to the union of all keys of `assoc1, assoc2, ...`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeyUnion.md">KeyUnion
   *      documentation</a>
   */
  public final static IBuiltInSymbol KeyUnion = S.initFinalSymbol("KeyUnion", ID.KeyUnion);

  /**
   * KeyValueMap(head, association) - returns a list of the rules pairs
   * `{head(k1,v1),head(k2,v2),...}` for which the `k1, k2,...` are the keys and the `v1, v2,...`
   * are the corresponding values in the association.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeyValueMap.md">KeyValueMap
   *      documentation</a>
   */
  public final static IBuiltInSymbol KeyValueMap = S.initFinalSymbol("KeyValueMap", ID.KeyValueMap);

  /**
   * KeyValuePattern( {rule-pattern1, rule-pattern1,...}) - `KeyValuePattern` is a pattern-matching
   * construct used to identify elements in a collection (such as a list of associations) that match
   * a specified set of key-value pairs. It is particularly useful for filtering data structures
   * like lists of dictionaries or associations based on specific criteria.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/KeyValuePattern.md">KeyValuePattern
   *      documentation</a>
   */
  public final static IBuiltInSymbol KeyValuePattern =
      S.initFinalSymbol("KeyValuePattern", ID.KeyValuePattern);

  /**
   * Khinchin - Khinchin's constant
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Khinchin.md">Khinchin
   *      documentation</a>
   */
  public final static IBuiltInSymbol Khinchin = S.initFinalSymbol("Khinchin", ID.Khinchin);

  public final static IBuiltInSymbol KirchhoffMatrix =
      S.initFinalSymbol("KirchhoffMatrix", ID.KirchhoffMatrix);

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
   * KroneckerDelta(arg1, arg2, ..., argN) - if all arguments `arg1` to `argN` are equal return `1`,
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

  /**
   * LABColor(x) - TODO describe `LABColor`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LABColor.md">LABColor
   *      documentation</a>
   */
  public final static IBuiltInSymbol LABColor = S.initFinalSymbol("LABColor", ID.LABColor);

  public final static IBuiltInSymbol Labeled = S.initFinalSymbol("Labeled", ID.Labeled);

  public final static IBuiltInSymbol LabelingFunction =
      S.initFinalSymbol("LabelingFunction", ID.LabelingFunction);

  public final static IBuiltInSymbol LabelingSize =
      S.initFinalSymbol("LabelingSize", ID.LabelingSize);

  public final static IBuiltInSymbol LabelStyle = S.initFinalSymbol("LabelStyle", ID.LabelStyle);

  /**
   * LaguerreL(n, x) - returns the Laguerre polynomial `L_n(x)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaguerreL.md">LaguerreL
   *      documentation</a>
   */
  public final static IBuiltInSymbol LaguerreL = S.initFinalSymbol("LaguerreL", ID.LaguerreL);

  /**
   * LambdaComponents(graph) - gives the lambda components of `graph` - sets of vertices
   * joined to each other by more edge-independent paths than to any vertex outside the set.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LambdaComponents.md">LambdaComponents
   *      documentation</a>
   */
  public final static IBuiltInSymbol LambdaComponents =
      S.initFinalSymbol("LambdaComponents", ID.LambdaComponents);

  public final static IBuiltInSymbol LambertW = S.initFinalSymbol("LambertW", ID.LambertW);

  /**
   * LaplaceDistribution(x) - TODO describe `LaplaceDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaplaceDistribution.md">LaplaceDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol LaplaceDistribution =
      S.initFinalSymbol("LaplaceDistribution", ID.LaplaceDistribution);

  /**
   * LaplaceTransform(f,t,s) - returns the laplace transform.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaplaceTransform.md">LaplaceTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol LaplaceTransform =
      S.initFinalSymbol("LaplaceTransform", ID.LaplaceTransform);

  /**
   * Laplacian(function, {x1, x2, ... , xN}) - returns the Laplace operator
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Laplacian.md">Laplacian
   *      documentation</a>
   */
  public final static IBuiltInSymbol Laplacian = S.initFinalSymbol("Laplacian", ID.Laplacian);

  /**
   * LaplacianFilter(x) - TODO describe `LaplacianFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaplacianFilter.md">LaplacianFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol LaplacianFilter =
      S.initFinalSymbol("LaplacianFilter", ID.LaplacianFilter);

  /**
   * LaplacianGaussianFilter(x) - TODO describe `LaplacianGaussianFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LaplacianGaussianFilter.md">LaplacianGaussianFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol LaplacianGaussianFilter =
      S.initFinalSymbol("LaplacianGaussianFilter", ID.LaplacianGaussianFilter);

  public final static IBuiltInSymbol LaplacianPDETerm =
      S.initFinalSymbol("LaplacianPDETerm", ID.LaplacianPDETerm);

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
   * LCHColor(x) - TODO describe `LCHColor`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LCHColor.md">LCHColor
   *      documentation</a>
   */
  public final static IBuiltInSymbol LCHColor = S.initFinalSymbol("LCHColor", ID.LCHColor);

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
   * LeapYearQ(x) - TODO describe `LeapYearQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeapYearQ.md">LeapYearQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeapYearQ = S.initFinalSymbol("LeapYearQ", ID.LeapYearQ);

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
   * LeftArrow(x) - TODO describe `LeftArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftArrow.md">LeftArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftArrow = S.initFinalSymbol("LeftArrow", ID.LeftArrow);

  /**
   * LeftArrowBar(x) - TODO describe `LeftArrowBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftArrowBar.md">LeftArrowBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftArrowBar =
      S.initFinalSymbol("LeftArrowBar", ID.LeftArrowBar);

  /**
   * LeftArrowRightArrow(x) - TODO describe `LeftArrowRightArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftArrowRightArrow.md">LeftArrowRightArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftArrowRightArrow =
      S.initFinalSymbol("LeftArrowRightArrow", ID.LeftArrowRightArrow);

  /**
   * LeftDownTeeVector(x) - TODO describe `LeftDownTeeVector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftDownTeeVector.md">LeftDownTeeVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftDownTeeVector =
      S.initFinalSymbol("LeftDownTeeVector", ID.LeftDownTeeVector);

  /**
   * LeftDownVector(x) - TODO describe `LeftDownVector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftDownVector.md">LeftDownVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftDownVector =
      S.initFinalSymbol("LeftDownVector", ID.LeftDownVector);

  /**
   * LeftDownVectorBar(x) - TODO describe `LeftDownVectorBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftDownVectorBar.md">LeftDownVectorBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftDownVectorBar =
      S.initFinalSymbol("LeftDownVectorBar", ID.LeftDownVectorBar);

  /**
   * LeftRightArrow(x) - TODO describe `LeftRightArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftRightArrow.md">LeftRightArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftRightArrow =
      S.initFinalSymbol("LeftRightArrow", ID.LeftRightArrow);

  /**
   * LeftRightVector(x) - TODO describe `LeftRightVector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftRightVector.md">LeftRightVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftRightVector =
      S.initFinalSymbol("LeftRightVector", ID.LeftRightVector);

  /**
   * LeftTee(x) - TODO describe `LeftTee`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftTee.md">LeftTee
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftTee = S.initFinalSymbol("LeftTee", ID.LeftTee);

  /**
   * LeftTeeArrow(x) - TODO describe `LeftTeeArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftTeeArrow.md">LeftTeeArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftTeeArrow =
      S.initFinalSymbol("LeftTeeArrow", ID.LeftTeeArrow);

  /**
   * LeftTeeVector(x) - TODO describe `LeftTeeVector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftTeeVector.md">LeftTeeVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftTeeVector =
      S.initFinalSymbol("LeftTeeVector", ID.LeftTeeVector);

  /**
   * LeftTriangle(x) - TODO describe `LeftTriangle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftTriangle.md">LeftTriangle
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftTriangle =
      S.initFinalSymbol("LeftTriangle", ID.LeftTriangle);

  /**
   * LeftTriangleBar(x) - TODO describe `LeftTriangleBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftTriangleBar.md">LeftTriangleBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftTriangleBar =
      S.initFinalSymbol("LeftTriangleBar", ID.LeftTriangleBar);

  /**
   * LeftTriangleEqual(x) - TODO describe `LeftTriangleEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftTriangleEqual.md">LeftTriangleEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftTriangleEqual =
      S.initFinalSymbol("LeftTriangleEqual", ID.LeftTriangleEqual);

  /**
   * LeftUpDownVector(x) - TODO describe `LeftUpDownVector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftUpDownVector.md">LeftUpDownVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftUpDownVector =
      S.initFinalSymbol("LeftUpDownVector", ID.LeftUpDownVector);

  /**
   * LeftUpTeeVector(x) - TODO describe `LeftUpTeeVector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftUpTeeVector.md">LeftUpTeeVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftUpTeeVector =
      S.initFinalSymbol("LeftUpTeeVector", ID.LeftUpTeeVector);

  /**
   * LeftUpVector(x) - TODO describe `LeftUpVector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftUpVector.md">LeftUpVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftUpVector =
      S.initFinalSymbol("LeftUpVector", ID.LeftUpVector);

  /**
   * LeftUpVectorBar(x) - TODO describe `LeftUpVectorBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftUpVectorBar.md">LeftUpVectorBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftUpVectorBar =
      S.initFinalSymbol("LeftUpVectorBar", ID.LeftUpVectorBar);

  /**
   * LeftVector(x) - TODO describe `LeftVector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftVector.md">LeftVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftVector = S.initFinalSymbol("LeftVector", ID.LeftVector);

  /**
   * LeftVectorBar(x) - TODO describe `LeftVectorBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LeftVectorBar.md">LeftVectorBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol LeftVectorBar =
      S.initFinalSymbol("LeftVectorBar", ID.LeftVectorBar);

  /**
   * LegendAppearance(x) - TODO describe `LegendAppearance`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendAppearance.md">LegendAppearance
   *      documentation</a>
   */
  public final static IBuiltInSymbol LegendAppearance =
      S.initFinalSymbol("LegendAppearance", ID.LegendAppearance);

  public final static IBuiltInSymbol Legended = S.initFinalSymbol("Legended", ID.Legended);

  /**
   * LegendFunction(x) - TODO describe `LegendFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendFunction.md">LegendFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol LegendFunction =
      S.initFinalSymbol("LegendFunction", ID.LegendFunction);

  /**
   * LegendLabel(x) - TODO describe `LegendLabel`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendLabel.md">LegendLabel
   *      documentation</a>
   */
  public final static IBuiltInSymbol LegendLabel = S.initFinalSymbol("LegendLabel", ID.LegendLabel);

  /**
   * LegendLayout(x) - TODO describe `LegendLayout`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendLayout.md">LegendLayout
   *      documentation</a>
   */
  public final static IBuiltInSymbol LegendLayout =
      S.initFinalSymbol("LegendLayout", ID.LegendLayout);

  /**
   * LegendMargins(x) - TODO describe `LegendMargins`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendMargins.md">LegendMargins
   *      documentation</a>
   */
  public final static IBuiltInSymbol LegendMargins =
      S.initFinalSymbol("LegendMargins", ID.LegendMargins);

  /**
   * LegendMarkers(x) - TODO describe `LegendMarkers`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendMarkers.md">LegendMarkers
   *      documentation</a>
   */
  public final static IBuiltInSymbol LegendMarkers =
      S.initFinalSymbol("LegendMarkers", ID.LegendMarkers);

  /**
   * LegendMarkerSize(x) - TODO describe `LegendMarkerSize`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LegendMarkerSize.md">LegendMarkerSize
   *      documentation</a>
   */
  public final static IBuiltInSymbol LegendMarkerSize =
      S.initFinalSymbol("LegendMarkerSize", ID.LegendMarkerSize);

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

  /**
   * LerchPhi(z, s, a) - returns the Lerch transcendent function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LerchPhi.md">LerchPhi
   *      documentation</a>
   */
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

  /**
   * LessEqualGreater(x) - TODO describe `LessEqualGreater`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqualGreater.md">LessEqualGreater
   *      documentation</a>
   */
  public final static IBuiltInSymbol LessEqualGreater =
      S.initFinalSymbol("LessEqualGreater", ID.LessEqualGreater);

  /**
   * LessEqualThan(rhs) - operator applied to an expr `lhs` (`LessEqualThan(rhs)[lhs]`) returns
   * `LessEqual(lhs,rhs)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessEqualThan.md">LessEqualThan
   *      documentation</a>
   */
  public final static IBuiltInSymbol LessEqualThan =
      S.initFinalSymbol("LessEqualThan", ID.LessEqualThan);

  /**
   * LessFullEqual(x) - TODO describe `LessFullEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessFullEqual.md">LessFullEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol LessFullEqual =
      S.initFinalSymbol("LessFullEqual", ID.LessFullEqual);

  /**
   * LessGreater(x) - TODO describe `LessGreater`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessGreater.md">LessGreater
   *      documentation</a>
   */
  public final static IBuiltInSymbol LessGreater = S.initFinalSymbol("LessGreater", ID.LessGreater);

  /**
   * LessLess(x) - TODO describe `LessLess`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessLess.md">LessLess
   *      documentation</a>
   */
  public final static IBuiltInSymbol LessLess = S.initFinalSymbol("LessLess", ID.LessLess);

  /**
   * LessSlantEqual(x) - TODO describe `LessSlantEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessSlantEqual.md">LessSlantEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol LessSlantEqual =
      S.initFinalSymbol("LessSlantEqual", ID.LessSlantEqual);

  /**
   * LessThan(rhs) - operator applied to an expr `lhs` (`LessThan(rhs)[lhs]`) returns
   * `Less(lhs,rhs)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessThan.md">LessThan
   *      documentation</a>
   */
  public final static IBuiltInSymbol LessThan = S.initFinalSymbol("LessThan", ID.LessThan);

  /**
   * LessTilde(x) - TODO describe `LessTilde`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LessTilde.md">LessTilde
   *      documentation</a>
   */
  public final static IBuiltInSymbol LessTilde = S.initFinalSymbol("LessTilde", ID.LessTilde);

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

  /**
   * LightBlue - RGB color value for the color light blue
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LightBlue.md">LightBlue
   *      documentation</a>
   */
  public final static IBuiltInSymbol LightBlue = S.initFinalSymbol("LightBlue", ID.LightBlue);

  /**
   * LightBrown - RGB color value for the color light brown
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LightBrown.md">LightBrown
   *      documentation</a>
   */
  public final static IBuiltInSymbol LightBrown = S.initFinalSymbol("LightBrown", ID.LightBrown);

  /**
   * LightCyan - RGB color value for the color light cyan
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LightCyan.md">LightCyan
   *      documentation</a>
   */
  public final static IBuiltInSymbol LightCyan = S.initFinalSymbol("LightCyan", ID.LightCyan);

  public final static IBuiltInSymbol Lighter = S.initFinalSymbol("Lighter", ID.Lighter);

  /**
   * LightGray - RGB color value for the color light gray
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LightGray.md">LightGray
   *      documentation</a>
   */
  public final static IBuiltInSymbol LightGray = S.initFinalSymbol("LightGray", ID.LightGray);

  /**
   * LightGreen - RGB color value for the color light green
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LightGreen.md">LightGreen
   *      documentation</a>
   */
  public final static IBuiltInSymbol LightGreen = S.initFinalSymbol("LightGreen", ID.LightGreen);

  public final static IBuiltInSymbol Lighting = S.initFinalSymbol("Lighting", ID.Lighting);

  /**
   * LightingAngle(x) - TODO describe `LightingAngle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LightingAngle.md">LightingAngle
   *      documentation</a>
   */
  public final static IBuiltInSymbol LightingAngle =
      S.initFinalSymbol("LightingAngle", ID.LightingAngle);

  /**
   * LightMagenta - RGB color value for the color light magenta
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LightMagenta.md">LightMagenta
   *      documentation</a>
   */
  public final static IBuiltInSymbol LightMagenta =
      S.initFinalSymbol("LightMagenta", ID.LightMagenta);

  /**
   * LightOrange - RGB color value for the color light orange
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LightOrange.md">LightOrange
   *      documentation</a>
   */
  public final static IBuiltInSymbol LightOrange = S.initFinalSymbol("LightOrange", ID.LightOrange);

  /**
   * LightPink - RGB color value for the color light pink
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LightPink.md">LightPink
   *      documentation</a>
   */
  public final static IBuiltInSymbol LightPink = S.initFinalSymbol("LightPink", ID.LightPink);

  /**
   * LightPurple - RGB color value for the color light purple
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LightPurple.md">LightPurple
   *      documentation</a>
   */
  public final static IBuiltInSymbol LightPurple = S.initFinalSymbol("LightPurple", ID.LightPurple);

  /**
   * LightRed - RGB color value for the color light red
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LightRed.md">LightRed
   *      documentation</a>
   */
  public final static IBuiltInSymbol LightRed = S.initFinalSymbol("LightRed", ID.LightRed);

  /**
   * LightYellow - RGB color value for the color light yellow
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LightYellow.md">LightYellow
   *      documentation</a>
   */
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
   * LinearModelFit({{x11,x12,y1},{x21,x22,y2},...}, {Func1, func2,...}, {var1, var2,...}) - Create
   * a linear regression model from a matrix of observed value pairs `{x_ij,..., y_i}`.
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

  /**
   * LineBreakChart(x) - TODO describe `LineBreakChart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LineBreakChart.md">LineBreakChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol LineBreakChart =
      S.initFinalSymbol("LineBreakChart", ID.LineBreakChart);

  public final static IBuiltInSymbol LineGraph = S.initFinalSymbol("LineGraph", ID.LineGraph);

  /**
   * LineIntegralConvolutionPlot(x) - TODO describe `LineIntegralConvolutionPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LineIntegralConvolutionPlot.md">LineIntegralConvolutionPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol LineIntegralConvolutionPlot =
      S.initFinalSymbol("LineIntegralConvolutionPlot", ID.LineIntegralConvolutionPlot);

  /**
   * LineLegend(x) - TODO describe `LineLegend`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LineLegend.md">LineLegend
   *      documentation</a>
   */
  public final static IBuiltInSymbol LineLegend = S.initFinalSymbol("LineLegend", ID.LineLegend);

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

  /**
   * ListAnimate(x) - TODO describe `ListAnimate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListAnimate.md">ListAnimate
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListAnimate = S.initFinalSymbol("ListAnimate", ID.ListAnimate);

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

  /**
   * ListCurvePathPlot(x) - TODO describe `ListCurvePathPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListCurvePathPlot.md">ListCurvePathPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListCurvePathPlot =
      S.initFinalSymbol("ListCurvePathPlot", ID.ListCurvePathPlot);

  public final static IBuiltInSymbol ListDensityPlot =
      S.initFinalSymbol("ListDensityPlot", ID.ListDensityPlot);

  /**
   * ListLineIntegralConvolutionPlot(x) - TODO describe `ListLineIntegralConvolutionPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListLineIntegralConvolutionPlot.md">ListLineIntegralConvolutionPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListLineIntegralConvolutionPlot =
      S.initFinalSymbol("ListLineIntegralConvolutionPlot", ID.ListLineIntegralConvolutionPlot);

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

  public final static IBuiltInSymbol ListStepPlot =
      S.initFinalSymbol("ListStepPlot", ID.ListStepPlot);

  /**
   * ListStreamDensityPlot(x) - TODO describe `ListStreamDensityPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListStreamDensityPlot.md">ListStreamDensityPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListStreamDensityPlot =
      S.initFinalSymbol("ListStreamDensityPlot", ID.ListStreamDensityPlot);

  public final static IBuiltInSymbol ListStreamPlot =
      S.initFinalSymbol("ListStreamPlot", ID.ListStreamPlot);

  /**
   * ListVectorDensityPlot(x) - TODO describe `ListVectorDensityPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ListVectorDensityPlot.md">ListVectorDensityPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ListVectorDensityPlot =
      S.initFinalSymbol("ListVectorDensityPlot", ID.ListVectorDensityPlot);

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

  /**
   * LocalAdaptiveBinarize(x) - TODO describe `LocalAdaptiveBinarize`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LocalAdaptiveBinarize.md">LocalAdaptiveBinarize
   *      documentation</a>
   */
  public final static IBuiltInSymbol LocalAdaptiveBinarize =
      S.initFinalSymbol("LocalAdaptiveBinarize", ID.LocalAdaptiveBinarize);

  public final static IBuiltInSymbol LocalClusteringCoefficient =
      S.initFinalSymbol("LocalClusteringCoefficient", ID.LocalClusteringCoefficient);

  /**
   * LocalizeVariables(x) - TODO describe `LocalizeVariables`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LocalizeVariables.md">LocalizeVariables
   *      documentation</a>
   */
  public final static IBuiltInSymbol LocalizeVariables =
      S.initFinalSymbol("LocalizeVariables", ID.LocalizeVariables);

  /**
   * LocalObject(x) - TODO describe `LocalObject`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LocalObject.md">LocalObject
   *      documentation</a>
   */
  public final static IBuiltInSymbol LocalObject = S.initFinalSymbol("LocalObject", ID.LocalObject);

  /**
   * LocalTime(x) - TODO describe `LocalTime`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LocalTime.md">LocalTime
   *      documentation</a>
   */
  public final static IBuiltInSymbol LocalTime = S.initFinalSymbol("LocalTime", ID.LocalTime);

  /**
   * Locator(x) - TODO describe `Locator`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Locator.md">Locator
   *      documentation</a>
   */
  public final static IBuiltInSymbol Locator = S.initFinalSymbol("Locator", ID.Locator);

  /**
   * LocatorAutoCreate(x) - TODO describe `LocatorAutoCreate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LocatorAutoCreate.md">LocatorAutoCreate
   *      documentation</a>
   */
  public final static IBuiltInSymbol LocatorAutoCreate =
      S.initFinalSymbol("LocatorAutoCreate", ID.LocatorAutoCreate);

  /**
   * LocatorPane(x) - TODO describe `LocatorPane`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LocatorPane.md">LocatorPane
   *      documentation</a>
   */
  public final static IBuiltInSymbol LocatorPane = S.initFinalSymbol("LocatorPane", ID.LocatorPane);

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

  public final static IBuiltInSymbol LogBarnesG = S.initFinalSymbol("LogBarnesG", ID.LogBarnesG);

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
   * LogisticDistribution(a, b) - returns the logistic distribution with mean `a` and scale
   * parameter `b`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogisticDistribution.md">LogisticDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol LogisticDistribution =
      S.initFinalSymbol("LogisticDistribution", ID.LogisticDistribution);

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

  /**
   * LogLogisticDistribution(x) - TODO describe `LogLogisticDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogLogisticDistribution.md">LogLogisticDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol LogLogisticDistribution =
      S.initFinalSymbol("LogLogisticDistribution", ID.LogLogisticDistribution);

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

  /**
   * LogSeriesDistribution(x) - TODO describe `LogSeriesDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LogSeriesDistribution.md">LogSeriesDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol LogSeriesDistribution =
      S.initFinalSymbol("LogSeriesDistribution", ID.LogSeriesDistribution);

  public final static IBuiltInSymbol Longest = S.initFinalSymbol("Longest", ID.Longest);

  public final static IBuiltInSymbol LongForm = S.initFinalSymbol("LongForm", ID.LongForm);

  /**
   * LongLeftArrow(x) - TODO describe `LongLeftArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LongLeftArrow.md">LongLeftArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol LongLeftArrow =
      S.initFinalSymbol("LongLeftArrow", ID.LongLeftArrow);

  /**
   * LongLeftRightArrow(x) - TODO describe `LongLeftRightArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LongLeftRightArrow.md">LongLeftRightArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol LongLeftRightArrow =
      S.initFinalSymbol("LongLeftRightArrow", ID.LongLeftRightArrow);

  /**
   * LongRightArrow(x) - TODO describe `LongRightArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LongRightArrow.md">LongRightArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol LongRightArrow =
      S.initFinalSymbol("LongRightArrow", ID.LongRightArrow);

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
   * LowerLeftArrow(x) - TODO describe `LowerLeftArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerLeftArrow.md">LowerLeftArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol LowerLeftArrow =
      S.initFinalSymbol("LowerLeftArrow", ID.LowerLeftArrow);

  /**
   * LowerRightArrow(x) - TODO describe `LowerRightArrow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LowerRightArrow.md">LowerRightArrow
   *      documentation</a>
   */
  public final static IBuiltInSymbol LowerRightArrow =
      S.initFinalSymbol("LowerRightArrow", ID.LowerRightArrow);

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
   * LuccioSamiComponents(graph) - gives the Luccio-Sami components of `graph` - sets of
   * vertices in which every proper subset has more ties to the rest of the set than to
   * anything outside it.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LuccioSamiComponents.md">LuccioSamiComponents
   *      documentation</a>
   */
  public final static IBuiltInSymbol LuccioSamiComponents =
      S.initFinalSymbol("LuccioSamiComponents", ID.LuccioSamiComponents);

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
   * LunarEclipse(x) - TODO describe `LunarEclipse`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LunarEclipse.md">LunarEclipse
   *      documentation</a>
   */
  public final static IBuiltInSymbol LunarEclipse =
      S.initFinalSymbol("LunarEclipse", ID.LunarEclipse);

  /**
   * LunationNumber(x) - TODO describe `LunationNumber`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LunationNumber.md">LunationNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol LunationNumber =
      S.initFinalSymbol("LunationNumber", ID.LunationNumber);

  /**
   * LUVColor(x) - TODO describe `LUVColor`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/LUVColor.md">LUVColor
   *      documentation</a>
   */
  public final static IBuiltInSymbol LUVColor = S.initFinalSymbol("LUVColor", ID.LUVColor);

  /**
   * MachineNumberQ(expr) - returns `True` if `expr` is a machine-precision real or complex number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MachineNumberQ.md">MachineNumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MachineNumberQ =
      S.initFinalSymbol("MachineNumberQ", ID.MachineNumberQ);

  public final static IBuiltInSymbol MachinePrecision =
      S.initFinalSymbol("MachinePrecision", ID.MachinePrecision);

  /**
   * Magenta - RGB color value for the color magenta
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Magenta.md">Magenta
   *      documentation</a>
   */
  public final static IBuiltInSymbol Magenta = S.initFinalSymbol("Magenta", ID.Magenta);

  /**
   * Magnification(x) - TODO describe `Magnification`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Magnification.md">Magnification
   *      documentation</a>
   */
  public final static IBuiltInSymbol Magnification =
      S.initFinalSymbol("Magnification", ID.Magnification);

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

  /**
   * Manipulator(x) - TODO describe `Manipulator`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Manipulator.md">Manipulator
   *      documentation</a>
   */
  public final static IBuiltInSymbol Manipulator = S.initFinalSymbol("Manipulator", ID.Manipulator);

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

  public final static IBuiltInSymbol MapAll = S.initFinalSymbol("MapAll", ID.MapAll);

  /**
   * MapApply(head, expr) - is equivalent to `Apply(head, expr, {1})`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MapApply.md">MapApply
   *      documentation</a>
   */
  public final static IBuiltInSymbol MapApply = S.initFinalSymbol("MapApply", ID.MapApply);

  /**
   * MapAt(f, expr, n) - applies `f` to the element at position `n` in `expr`. If `n` is negative,
   * the position is counted from the end.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MapAt.md">MapAt
   *      documentation</a>
   */
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
   * MarcumQ(x) - TODO describe `MarcumQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MarcumQ.md">MarcumQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MarcumQ = S.initFinalSymbol("MarcumQ", ID.MarcumQ);

  public final static IBuiltInSymbol MarginalDistribution =
      S.initFinalSymbol("MarginalDistribution", ID.MarginalDistribution);

  /**
   * Masking(x) - TODO describe `Masking`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Masking.md">Masking
   *      documentation</a>
   */
  public final static IBuiltInSymbol Masking = S.initFinalSymbol("Masking", ID.Masking);

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

  /**
   * MatrixFunction(function-head, matrix) - computes the matrix function of the `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixFunction.md">MatrixFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol MatrixFunction =
      S.initFinalSymbol("MatrixFunction", ID.MatrixFunction);

  /**
   * MatrixLog(matrix) - computes the matrix logarithm of the square `matrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MatrixLog.md">MatrixLog
   *      documentation</a>
   */
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

  public final static IBuiltInSymbol MatrixSymbol =
      S.initFinalSymbol("MatrixSymbol", ID.MatrixSymbol);

  /**
   * Max(e_1, e_2, ..., e_i) - returns the expression with the greatest value among the `e_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Max.md">Max
   *      documentation</a>
   */
  public final static IBuiltInSymbol Max = S.initFinalSymbol("Max", ID.Max);

  /**
   * MaxDate(x) - TODO describe `MaxDate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MaxDate.md">MaxDate
   *      documentation</a>
   */
  public final static IBuiltInSymbol MaxDate = S.initFinalSymbol("MaxDate", ID.MaxDate);

  /**
   * MaxExtraConditions(x) - TODO describe `MaxExtraConditions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MaxExtraConditions.md">MaxExtraConditions
   *      documentation</a>
   */
  public final static IBuiltInSymbol MaxExtraConditions =
      S.initFinalSymbol("MaxExtraConditions", ID.MaxExtraConditions);

  /**
   * MaxFeatures(x) - TODO describe `MaxFeatures`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MaxFeatures.md">MaxFeatures
   *      documentation</a>
   */
  public final static IBuiltInSymbol MaxFeatures = S.initFinalSymbol("MaxFeatures", ID.MaxFeatures);

  /**
   * MaxFilter(list, r) - filter which evaluates the `Max` of `list` for the radius `r`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MaxFilter.md">MaxFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol MaxFilter = S.initFinalSymbol("MaxFilter", ID.MaxFilter);

  /**
   * MaximalBy(list, function-head) - get the elements from `list`, for which the applied
   * `function-head` is maximal.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MaximalBy.md">MaximalBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol MaximalBy = S.initFinalSymbol("MaximalBy", ID.MaximalBy);

  /**
   * Maximize(unary-function, variable) - returns the maximum of the unary function for the given
   * `variable`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Maximize.md">Maximize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Maximize = S.initFinalSymbol("Maximize", ID.Maximize);

  /**
   * MaxItems(x) - TODO describe `MaxItems`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MaxItems.md">MaxItems
   *      documentation</a>
   */
  public final static IBuiltInSymbol MaxItems = S.initFinalSymbol("MaxItems", ID.MaxItems);

  public final static IBuiltInSymbol MaxIterations =
      S.initFinalSymbol("MaxIterations", ID.MaxIterations);

  /**
   * MaxLimit(x) - TODO describe `MaxLimit`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MaxLimit.md">MaxLimit
   *      documentation</a>
   */
  public final static IBuiltInSymbol MaxLimit = S.initFinalSymbol("MaxLimit", ID.MaxLimit);

  public final static IBuiltInSymbol MaxMemoryUsed =
      S.initFinalSymbol("MaxMemoryUsed", ID.MaxMemoryUsed);

  public final static IBuiltInSymbol MaxPlotPoints =
      S.initFinalSymbol("MaxPlotPoints", ID.MaxPlotPoints);

  public final static IBuiltInSymbol MaxPoints = S.initFinalSymbol("MaxPoints", ID.MaxPoints);

  public final static IBuiltInSymbol MaxRecursion =
      S.initFinalSymbol("MaxRecursion", ID.MaxRecursion);

  public final static IBuiltInSymbol MaxRoots = S.initFinalSymbol("MaxRoots", ID.MaxRoots);

  /**
   * MaxStableDistribution(x) - TODO describe `MaxStableDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MaxStableDistribution.md">MaxStableDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol MaxStableDistribution =
      S.initFinalSymbol("MaxStableDistribution", ID.MaxStableDistribution);

  /**
   * MaxwellDistribution(x) - TODO describe `MaxwellDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MaxwellDistribution.md">MaxwellDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol MaxwellDistribution =
      S.initFinalSymbol("MaxwellDistribution", ID.MaxwellDistribution);

  /**
   * Mean(list) - returns the statistical mean of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Mean.md">Mean
   *      documentation</a>
   */
  public final static IBuiltInSymbol Mean = S.initFinalSymbol("Mean", ID.Mean);

  /**
   * MeanAround(x) - TODO describe `MeanAround`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeanAround.md">MeanAround
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeanAround = S.initFinalSymbol("MeanAround", ID.MeanAround);

  public final static IBuiltInSymbol MeanClusteringCoefficient =
      S.initFinalSymbol("MeanClusteringCoefficient", ID.MeanClusteringCoefficient);

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
   * MeanShiftFilter(x) - TODO describe `MeanShiftFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeanShiftFilter.md">MeanShiftFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeanShiftFilter =
      S.initFinalSymbol("MeanShiftFilter", ID.MeanShiftFilter);

  /**
   * Median(list) - returns the median of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Median.md">Median
   *      documentation</a>
   */
  public final static IBuiltInSymbol Median = S.initFinalSymbol("Median", ID.Median);

  /**
   * MedianDeviation(x) - TODO describe `MedianDeviation`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MedianDeviation.md">MedianDeviation
   *      documentation</a>
   */
  public final static IBuiltInSymbol MedianDeviation =
      S.initFinalSymbol("MedianDeviation", ID.MedianDeviation);

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

  /**
   * MeijerG({{a(1),a(2),...,a(n)},{a(n+1),a(n+2),...,a(p)}},{{b(1),b(2),...,b(m)},{b(m+1),b(m+2),...,b(q)}},
   * z) - return the `MeijerG` function. The G-function was introduced by Cornelis Simon Meijer
   * (1936) as a very general function intended to include most of the known special functions as
   * particular cases.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeijerG.md">MeijerG
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeijerG = S.initFinalSymbol("MeijerG", ID.MeijerG);

  public final static IBuiltInSymbol MeijerGReduce =
      S.initFinalSymbol("MeijerGReduce", ID.MeijerGReduce);

  /**
   * MeixnerDistribution(x) - TODO describe `MeixnerDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeixnerDistribution.md">MeixnerDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeixnerDistribution =
      S.initFinalSymbol("MeixnerDistribution", ID.MeixnerDistribution);

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

  /**
   * MenuView(x) - TODO describe `MenuView`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MenuView.md">MenuView
   *      documentation</a>
   */
  public final static IBuiltInSymbol MenuView = S.initFinalSymbol("MenuView", ID.MenuView);

  /**
   * Merge(list-of-rules-or-associations, function) - use the `function` to merge right-hand-side
   * values with the left-hand-side key in the `list-of-rules-or-associations`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Merge.md">Merge
   *      documentation</a>
   */
  public final static IBuiltInSymbol Merge = S.initFinalSymbol("Merge", ID.Merge);

  /**
   * MergeDifferences(x) - TODO describe `MergeDifferences`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MergeDifferences.md">MergeDifferences
   *      documentation</a>
   */
  public final static IBuiltInSymbol MergeDifferences =
      S.initFinalSymbol("MergeDifferences", ID.MergeDifferences);

  /**
   * MersennePrimeExponent(n) - returns the `n`th mersenne prime exponent. `2^n - 1` must be a prime
   * number. Currently `0 < n <= 52` can be computed, otherwise the function returns unevaluated.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MersennePrimeExponent.md">MersennePrimeExponent
   *      documentation</a>
   */
  public final static IBuiltInSymbol MersennePrimeExponent =
      S.initFinalSymbol("MersennePrimeExponent", ID.MersennePrimeExponent);

  /**
   * MersennePrimeExponentQ(n) - returns `True` if `2^n - 1` is a prime number. Currently `0 <= n <=
   * 52` can be computed in reasonable time.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MersennePrimeExponentQ.md">MersennePrimeExponentQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MersennePrimeExponentQ =
      S.initFinalSymbol("MersennePrimeExponentQ", ID.MersennePrimeExponentQ);

  public final static IBuiltInSymbol Mesh = S.initFinalSymbol("Mesh", ID.Mesh);

  /**
   * MeshCellCount(x) - TODO describe `MeshCellCount`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeshCellCount.md">MeshCellCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeshCellCount =
      S.initFinalSymbol("MeshCellCount", ID.MeshCellCount);

  /**
   * MeshCellHighlight(x) - TODO describe `MeshCellHighlight`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeshCellHighlight.md">MeshCellHighlight
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeshCellHighlight =
      S.initFinalSymbol("MeshCellHighlight", ID.MeshCellHighlight);

  /**
   * MeshCellLabel(x) - TODO describe `MeshCellLabel`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeshCellLabel.md">MeshCellLabel
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeshCellLabel =
      S.initFinalSymbol("MeshCellLabel", ID.MeshCellLabel);

  /**
   * MeshCellMarker(x) - TODO describe `MeshCellMarker`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeshCellMarker.md">MeshCellMarker
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeshCellMarker =
      S.initFinalSymbol("MeshCellMarker", ID.MeshCellMarker);

  /**
   * MeshCells(x) - TODO describe `MeshCells`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeshCells.md">MeshCells
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeshCells = S.initFinalSymbol("MeshCells", ID.MeshCells);

  /**
   * MeshCellShapeFunction(x) - TODO describe `MeshCellShapeFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeshCellShapeFunction.md">MeshCellShapeFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeshCellShapeFunction =
      S.initFinalSymbol("MeshCellShapeFunction", ID.MeshCellShapeFunction);

  /**
   * MeshCellStyle(x) - TODO describe `MeshCellStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeshCellStyle.md">MeshCellStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeshCellStyle =
      S.initFinalSymbol("MeshCellStyle", ID.MeshCellStyle);

  /**
   * MeshCoordinates(x) - TODO describe `MeshCoordinates`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeshCoordinates.md">MeshCoordinates
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeshCoordinates =
      S.initFinalSymbol("MeshCoordinates", ID.MeshCoordinates);

  /**
   * MeshFunctions(x) - TODO describe `MeshFunctions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeshFunctions.md">MeshFunctions
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeshFunctions =
      S.initFinalSymbol("MeshFunctions", ID.MeshFunctions);

  /**
   * MeshPrimitives(x) - TODO describe `MeshPrimitives`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeshPrimitives.md">MeshPrimitives
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeshPrimitives =
      S.initFinalSymbol("MeshPrimitives", ID.MeshPrimitives);

  public final static IBuiltInSymbol MeshRange = S.initFinalSymbol("MeshRange", ID.MeshRange);

  /**
   * MeshRegion(x) - TODO describe `MeshRegion`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeshRegion.md">MeshRegion
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeshRegion = S.initFinalSymbol("MeshRegion", ID.MeshRegion);

  /**
   * MeshRegionQ(x) - TODO describe `MeshRegionQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeshRegionQ.md">MeshRegionQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeshRegionQ = S.initFinalSymbol("MeshRegionQ", ID.MeshRegionQ);

  /**
   * MeshShading(x) - TODO describe `MeshShading`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeshShading.md">MeshShading
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeshShading = S.initFinalSymbol("MeshShading", ID.MeshShading);

  /**
   * MeshStyle(x) - TODO describe `MeshStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MeshStyle.md">MeshStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol MeshStyle = S.initFinalSymbol("MeshStyle", ID.MeshStyle);

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

  /**
   * MetaInformation(x) - TODO describe `MetaInformation`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MetaInformation.md">MetaInformation
   *      documentation</a>
   */
  public final static IBuiltInSymbol MetaInformation =
      S.initFinalSymbol("MetaInformation", ID.MetaInformation);

  public final static IBuiltInSymbol Method = S.initFinalSymbol("Method", ID.Method);

  /**
   * MidDate(x) - TODO describe `MidDate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MidDate.md">MidDate
   *      documentation</a>
   */
  public final static IBuiltInSymbol MidDate = S.initFinalSymbol("MidDate", ID.MidDate);

  /**
   * Min(e_1, e_2, ..., e_i) - returns the expression with the lowest value among the `e_i`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Min.md">Min
   *      documentation</a>
   */
  public final static IBuiltInSymbol Min = S.initFinalSymbol("Min", ID.Min);

  /**
   * MinDate(x) - TODO describe `MinDate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MinDate.md">MinDate
   *      documentation</a>
   */
  public final static IBuiltInSymbol MinDate = S.initFinalSymbol("MinDate", ID.MinDate);

  /**
   * MinFilter(list, r) - filter which evaluates the `Min` of `list` for the radius `r`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MinFilter.md">MinFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol MinFilter = S.initFinalSymbol("MinFilter", ID.MinFilter);

  /**
   * MinimalBy(list, function-head) - get the elements from `list`, for which the applied
   * `function-head` is minimal.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MinimalBy.md">MinimalBy
   *      documentation</a>
   */
  public final static IBuiltInSymbol MinimalBy = S.initFinalSymbol("MinimalBy", ID.MinimalBy);

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

  /**
   * MinLimit(x) - TODO describe `MinLimit`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MinLimit.md">MinLimit
   *      documentation</a>
   */
  public final static IBuiltInSymbol MinLimit = S.initFinalSymbol("MinLimit", ID.MinLimit);

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
   * MinStableDistribution(x) - TODO describe `MinStableDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MinStableDistribution.md">MinStableDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol MinStableDistribution =
      S.initFinalSymbol("MinStableDistribution", ID.MinStableDistribution);

  /**
   * Minus(expr) - is the negation of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Minus.md">Minus
   *      documentation</a>
   */
  public final static IBuiltInSymbol Minus = S.initFinalSymbol("Minus", ID.Minus);

  /**
   * MinusPlus(x) - TODO describe `MinusPlus`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MinusPlus.md">MinusPlus
   *      documentation</a>
   */
  public final static IBuiltInSymbol MinusPlus = S.initFinalSymbol("MinusPlus", ID.MinusPlus);

  public final static IBuiltInSymbol Missing = S.initFinalSymbol("Missing", ID.Missing);

  /**
   * MissingBehavior(x) - TODO describe `MissingBehavior`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MissingBehavior.md">MissingBehavior
   *      documentation</a>
   */
  public final static IBuiltInSymbol MissingBehavior =
      S.initFinalSymbol("MissingBehavior", ID.MissingBehavior);

  /**
   * MissingQ(expr) - returns `True` if `expr` is a `Missing()` expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MissingQ.md">MissingQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MissingQ = S.initFinalSymbol("MissingQ", ID.MissingQ);

  /**
   * MissingValuePattern(x) - TODO describe `MissingValuePattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MissingValuePattern.md">MissingValuePattern
   *      documentation</a>
   */
  public final static IBuiltInSymbol MissingValuePattern =
      S.initFinalSymbol("MissingValuePattern", ID.MissingValuePattern);

  /**
   * MixedMagnitude(x) - TODO describe `MixedMagnitude`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MixedMagnitude.md">MixedMagnitude
   *      documentation</a>
   */
  public final static IBuiltInSymbol MixedMagnitude =
      S.initFinalSymbol("MixedMagnitude", ID.MixedMagnitude);

  /**
   * MixedUnit(x) - TODO describe `MixedUnit`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MixedUnit.md">MixedUnit
   *      documentation</a>
   */
  public final static IBuiltInSymbol MixedUnit = S.initFinalSymbol("MixedUnit", ID.MixedUnit);

  /**
   * MixtureDistribution(x) - TODO describe `MixtureDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MixtureDistribution.md">MixtureDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol MixtureDistribution =
      S.initFinalSymbol("MixtureDistribution", ID.MixtureDistribution);

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

  public final static IBuiltInSymbol Molecule = S.initFinalSymbol("Molecule", ID.Molecule);

  /**
   * MoleculeAlign(x) - TODO describe `MoleculeAlign`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculeAlign.md">MoleculeAlign
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculeAlign =
      S.initFinalSymbol("MoleculeAlign", ID.MoleculeAlign);

  /**
   * MoleculeAlignment(x) - TODO describe `MoleculeAlignment`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculeAlignment.md">MoleculeAlignment
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculeAlignment =
      S.initFinalSymbol("MoleculeAlignment", ID.MoleculeAlignment);

  /**
   * MoleculeContainsQ(x) - TODO describe `MoleculeContainsQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculeContainsQ.md">MoleculeContainsQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculeContainsQ =
      S.initFinalSymbol("MoleculeContainsQ", ID.MoleculeContainsQ);

  /**
   * MoleculeDraw(x) - TODO describe `MoleculeDraw`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculeDraw.md">MoleculeDraw
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculeDraw =
      S.initFinalSymbol("MoleculeDraw", ID.MoleculeDraw);

  /**
   * MoleculeEquivalentQ(x) - TODO describe `MoleculeEquivalentQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculeEquivalentQ.md">MoleculeEquivalentQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculeEquivalentQ =
      S.initFinalSymbol("MoleculeEquivalentQ", ID.MoleculeEquivalentQ);

  /**
   * MoleculeFreeQ(x) - TODO describe `MoleculeFreeQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculeFreeQ.md">MoleculeFreeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculeFreeQ =
      S.initFinalSymbol("MoleculeFreeQ", ID.MoleculeFreeQ);

  /**
   * MoleculeGraph(x) - TODO describe `MoleculeGraph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculeGraph.md">MoleculeGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculeGraph =
      S.initFinalSymbol("MoleculeGraph", ID.MoleculeGraph);

  /**
   * MoleculeMatchQ(x) - TODO describe `MoleculeMatchQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculeMatchQ.md">MoleculeMatchQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculeMatchQ =
      S.initFinalSymbol("MoleculeMatchQ", ID.MoleculeMatchQ);

  /**
   * MoleculeMaximumCommonSubstructure(x) - TODO describe `MoleculeMaximumCommonSubstructure`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculeMaximumCommonSubstructure.md">MoleculeMaximumCommonSubstructure
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculeMaximumCommonSubstructure =
      S.initFinalSymbol("MoleculeMaximumCommonSubstructure", ID.MoleculeMaximumCommonSubstructure);

  /**
   * MoleculeModify(x) - TODO describe `MoleculeModify`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculeModify.md">MoleculeModify
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculeModify =
      S.initFinalSymbol("MoleculeModify", ID.MoleculeModify);

  /**
   * MoleculeName(x) - TODO describe `MoleculeName`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculeName.md">MoleculeName
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculeName =
      S.initFinalSymbol("MoleculeName", ID.MoleculeName);

  /**
   * MoleculePattern(x) - TODO describe `MoleculePattern`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculePattern.md">MoleculePattern
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculePattern =
      S.initFinalSymbol("MoleculePattern", ID.MoleculePattern);

  /**
   * MoleculePlot(x) - TODO describe `MoleculePlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculePlot.md">MoleculePlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculePlot =
      S.initFinalSymbol("MoleculePlot", ID.MoleculePlot);

  /**
   * MoleculePlot3D(x) - TODO describe `MoleculePlot3D`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculePlot3D.md">MoleculePlot3D
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculePlot3D =
      S.initFinalSymbol("MoleculePlot3D", ID.MoleculePlot3D);

  /**
   * MoleculeProperty(x) - TODO describe `MoleculeProperty`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculeProperty.md">MoleculeProperty
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculeProperty =
      S.initFinalSymbol("MoleculeProperty", ID.MoleculeProperty);

  public final static IBuiltInSymbol MoleculeQ = S.initFinalSymbol("MoleculeQ", ID.MoleculeQ);

  /**
   * MoleculeSubstructureCount(x) - TODO describe `MoleculeSubstructureCount`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoleculeSubstructureCount.md">MoleculeSubstructureCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoleculeSubstructureCount =
      S.initFinalSymbol("MoleculeSubstructureCount", ID.MoleculeSubstructureCount);

  public final static IBuiltInSymbol MoleculeValue =
      S.initFinalSymbol("MoleculeValue", ID.MoleculeValue);

  public final static IBuiltInSymbol Moment = S.initFinalSymbol("Moment", ID.Moment);

  /**
   * MomentGeneratingFunction(x) - TODO describe `MomentGeneratingFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MomentGeneratingFunction.md">MomentGeneratingFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol MomentGeneratingFunction =
      S.initFinalSymbol("MomentGeneratingFunction", ID.MomentGeneratingFunction);

  /**
   * MomentOfInertia(x) - TODO describe `MomentOfInertia`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MomentOfInertia.md">MomentOfInertia
   *      documentation</a>
   */
  public final static IBuiltInSymbol MomentOfInertia =
      S.initFinalSymbol("MomentOfInertia", ID.MomentOfInertia);

  /**
   * Monday(x) - TODO describe `Monday`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Monday.md">Monday
   *      documentation</a>
   */
  public final static IBuiltInSymbol Monday = S.initFinalSymbol("Monday", ID.Monday);

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
   * MoonPhase(x) - TODO describe `MoonPhase`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoonPhase.md">MoonPhase
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoonPhase = S.initFinalSymbol("MoonPhase", ID.MoonPhase);

  /**
   * MoonPhaseDate(x) - TODO describe `MoonPhaseDate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoonPhaseDate.md">MoonPhaseDate
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoonPhaseDate =
      S.initFinalSymbol("MoonPhaseDate", ID.MoonPhaseDate);

  /**
   * MoonPosition(x) - TODO describe `MoonPosition`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoonPosition.md">MoonPosition
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoonPosition =
      S.initFinalSymbol("MoonPosition", ID.MoonPosition);

  /**
   * MorphologicalBinarize(x) - TODO describe `MorphologicalBinarize`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MorphologicalBinarize.md">MorphologicalBinarize
   *      documentation</a>
   */
  public final static IBuiltInSymbol MorphologicalBinarize =
      S.initFinalSymbol("MorphologicalBinarize", ID.MorphologicalBinarize);

  /**
   * MorphologicalComponents(x) - TODO describe `MorphologicalComponents`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MorphologicalComponents.md">MorphologicalComponents
   *      documentation</a>
   */
  public final static IBuiltInSymbol MorphologicalComponents =
      S.initFinalSymbol("MorphologicalComponents", ID.MorphologicalComponents);

  /**
   * MorphologicalPerimeter(x) - TODO describe `MorphologicalPerimeter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MorphologicalPerimeter.md">MorphologicalPerimeter
   *      documentation</a>
   */
  public final static IBuiltInSymbol MorphologicalPerimeter =
      S.initFinalSymbol("MorphologicalPerimeter", ID.MorphologicalPerimeter);

  /**
   * MorphologicalTransform(x) - TODO describe `MorphologicalTransform`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MorphologicalTransform.md">MorphologicalTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol MorphologicalTransform =
      S.initFinalSymbol("MorphologicalTransform", ID.MorphologicalTransform);

  /**
   * Most(expr) - returns `expr` with the last element removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Most.md">Most
   *      documentation</a>
   */
  public final static IBuiltInSymbol Most = S.initFinalSymbol("Most", ID.Most);

  public final static IBuiltInSymbol Mouseover = S.initFinalSymbol("Mouseover", ID.Mouseover);

  public final static IBuiltInSymbol MovingAverage =
      S.initFinalSymbol("MovingAverage", ID.MovingAverage);

  public final static IBuiltInSymbol MovingMedian =
      S.initFinalSymbol("MovingMedian", ID.MovingMedian);

  /**
   * MoyalDistribution(x) - TODO describe `MoyalDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/MoyalDistribution.md">MoyalDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol MoyalDistribution =
      S.initFinalSymbol("MoyalDistribution", ID.MoyalDistribution);

  /**
   * Multicolumn(x) - TODO describe `Multicolumn`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Multicolumn.md">Multicolumn
   *      documentation</a>
   */
  public final static IBuiltInSymbol Multicolumn = S.initFinalSymbol("Multicolumn", ID.Multicolumn);

  /**
   * Multinomial(n1, n2, ...) - gives the multinomial coefficient `(n1+n2+...)!/(n1! n2! ...)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Multinomial.md">Multinomial
   *      documentation</a>
   */
  public final static IBuiltInSymbol Multinomial = S.initFinalSymbol("Multinomial", ID.Multinomial);

  public final static IBuiltInSymbol MultinormalDistribution =
      S.initFinalSymbol("MultinormalDistribution", ID.MultinormalDistribution);

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

  public final static IBuiltInSymbol MultivariatePoissonDistribution =
      S.initFinalSymbol("MultivariatePoissonDistribution", ID.MultivariatePoissonDistribution);

  public final static IBuiltInSymbol MultivariateTDistribution =
      S.initFinalSymbol("MultivariateTDistribution", ID.MultivariateTDistribution);

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

  /**
   * Nand(arg1, arg2, ...) - Logical NAND function. It evaluates its arguments in order, giving
   * `True` immediately if any of them are `False`, and `False` if they are all `True`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nand.md">Nand
   *      documentation</a>
   */
  public final static IBuiltInSymbol Nand = S.initFinalSymbol("Nand", ID.Nand);

  public final static IBuiltInSymbol NArgMax = S.initFinalSymbol("NArgMax", ID.NArgMax);

  public final static IBuiltInSymbol NArgMin = S.initFinalSymbol("NArgMin", ID.NArgMin);

  /**
   * ND(function, x, value) - returns a numerical approximation of the partial derivative of the
   * `function` for the variable `x` and the given `value`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ND.md">ND
   *      documentation</a>
   */
  /**
   * NCache(x, xn) - pairs the exact value `x` with its approximate numerical value `xn`, and
   * evaluates to `x`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NCache.md">NCache
   *      documentation</a>
   */
  public final static IBuiltInSymbol NCache = S.initFinalSymbol("NCache", ID.NCache);

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

  /**
   * NDSolveValue({equation-list}, functions, t) - attempts to solve the differential
   * `equation-list` for the `functions` and the time-dependent-variable `t`, and returns the
   * `InterpolatingFunction` function object itself.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NDSolveValue.md">NDSolveValue
   *      documentation</a>
   */
  public final static IBuiltInSymbol NDSolveValue =
      S.initFinalSymbol("NDSolveValue", ID.NDSolveValue);


  /**
   * Nearest(list-of-values, x-value) - returns the value from the `list-of-values` which is nearest
   * to `x-value`. By default the `EuclideanDistance` is used. With the `DistanceFunction` option
   * you can specify your own distance function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nearest.md">Nearest
   *      documentation</a>
   */
  public final static IBuiltInSymbol Nearest = S.initFinalSymbol("Nearest", ID.Nearest);

  /**
   * NearestTo(x-value) - returns the value from the `list-of-values` which is nearest to `x-value`.
   * By default the `EuclideanDistance` is used. With the `DistanceFunction` option you can specify
   * your own distance function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NearestTo.md">NearestTo
   *      documentation</a>
   */
  public final static IBuiltInSymbol NearestTo = S.initFinalSymbol("NearestTo", ID.NearestTo);

  /**
   * NeedlemanWunschSimilarity(x) - TODO describe `NeedlemanWunschSimilarity`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NeedlemanWunschSimilarity.md">NeedlemanWunschSimilarity
   *      documentation</a>
   */
  public final static IBuiltInSymbol NeedlemanWunschSimilarity =
      S.initFinalSymbol("NeedlemanWunschSimilarity", ID.NeedlemanWunschSimilarity);

  public final static IBuiltInSymbol Needs = S.initFinalSymbol("Needs", ID.Needs);

  /**
   * Negative(x) - returns `True` if `x` is a negative real number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Negative.md">Negative
   *      documentation</a>
   */
  public final static IBuiltInSymbol Negative = S.initFinalSymbol("Negative", ID.Negative);

  public final static IBuiltInSymbol NegativeDefiniteMatrixQ =
      S.initFinalSymbol("NegativeDefiniteMatrixQ", ID.NegativeDefiniteMatrixQ);

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

  public final static IBuiltInSymbol NegativeSemidefiniteMatrixQ =
      S.initFinalSymbol("NegativeSemidefiniteMatrixQ", ID.NegativeSemidefiniteMatrixQ);

  public final static IBuiltInSymbol NeighborhoodGraph =
      S.initFinalSymbol("NeighborhoodGraph", ID.NeighborhoodGraph);

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
   * NestedGreaterGreater(x) - TODO describe `NestedGreaterGreater`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestedGreaterGreater.md">NestedGreaterGreater
   *      documentation</a>
   */
  public final static IBuiltInSymbol NestedGreaterGreater =
      S.initFinalSymbol("NestedGreaterGreater", ID.NestedGreaterGreater);

  /**
   * NestedLessLess(x) - TODO describe `NestedLessLess`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NestedLessLess.md">NestedLessLess
   *      documentation</a>
   */
  public final static IBuiltInSymbol NestedLessLess =
      S.initFinalSymbol("NestedLessLess", ID.NestedLessLess);

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

  /**
   * NetGraph(x) - TODO describe `NetGraph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NetGraph.md">NetGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol NetGraph = S.initFinalSymbol("NetGraph", ID.NetGraph);

  /**
   * NewMoon(x) - TODO describe `NewMoon`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NewMoon.md">NewMoon
   *      documentation</a>
   */
  public final static IBuiltInSymbol NewMoon = S.initFinalSymbol("NewMoon", ID.NewMoon);

  /**
   * NExpectation(pure-function, data-set) - returns the expected value of the `pure-function` for
   * the given `data-set` numerically.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NExpectation.md">NExpectation
   *      documentation</a>
   */
  public final static IBuiltInSymbol NExpectation =
      S.initFinalSymbol("NExpectation", ID.NExpectation);

  /**
   * NextDate(x) - TODO describe `NextDate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NextDate.md">NextDate
   *      documentation</a>
   */
  public final static IBuiltInSymbol NextDate = S.initFinalSymbol("NextDate", ID.NextDate);

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
   * NightHemisphere(x) - TODO describe `NightHemisphere`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NightHemisphere.md">NightHemisphere
   *      documentation</a>
   */
  public final static IBuiltInSymbol NightHemisphere =
      S.initFinalSymbol("NightHemisphere", ID.NightHemisphere);

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

  /**
   * NoncentralChiSquareDistribution(x) - TODO describe `NoncentralChiSquareDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NoncentralChiSquareDistribution.md">NoncentralChiSquareDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol NoncentralChiSquareDistribution =
      S.initFinalSymbol("NoncentralChiSquareDistribution", ID.NoncentralChiSquareDistribution);

  public final static IBuiltInSymbol NonCommutativeMultiply =
      S.initFinalSymbol("NonCommutativeMultiply", ID.NonCommutativeMultiply);

  public final static IBuiltInSymbol NonConstants =
      S.initFinalSymbol("NonConstants", ID.NonConstants);

  /**
   * NondimensionalizationTransform(x) - TODO describe `NondimensionalizationTransform`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NondimensionalizationTransform.md">NondimensionalizationTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol NondimensionalizationTransform =
      S.initFinalSymbol("NondimensionalizationTransform", ID.NondimensionalizationTransform);

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
   * Nor(arg1, arg2, ...) - Logical NOR function. It evaluates its arguments in order, giving
   * `False` immediately if any of them are `True`, and `True` if they are all `False`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nor.md">Nor
   *      documentation</a>
   */
  public final static IBuiltInSymbol Nor = S.initFinalSymbol("Nor", ID.Nor);

  /**
   * Norm(v) - returns the norm of the vector `v`
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
   * NormalsFunction(x) - TODO describe `NormalsFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NormalsFunction.md">NormalsFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol NormalsFunction =
      S.initFinalSymbol("NormalsFunction", ID.NormalsFunction);

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

  /**
   * NotCongruent(x) - TODO describe `NotCongruent`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotCongruent.md">NotCongruent
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotCongruent =
      S.initFinalSymbol("NotCongruent", ID.NotCongruent);

  /**
   * NotCupCap(x) - TODO describe `NotCupCap`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotCupCap.md">NotCupCap
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotCupCap = S.initFinalSymbol("NotCupCap", ID.NotCupCap);

  /**
   * NotDoubleVerticalBar(x) - TODO describe `NotDoubleVerticalBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotDoubleVerticalBar.md">NotDoubleVerticalBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotDoubleVerticalBar =
      S.initFinalSymbol("NotDoubleVerticalBar", ID.NotDoubleVerticalBar);

  public final static IBuiltInSymbol Notebook = S.initFinalSymbol("Notebook", ID.Notebook);

  public final static IBuiltInSymbol NotElement = S.initFinalSymbol("NotElement", ID.NotElement);

  /**
   * NotEqualTilde(x) - TODO describe `NotEqualTilde`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotEqualTilde.md">NotEqualTilde
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotEqualTilde =
      S.initFinalSymbol("NotEqualTilde", ID.NotEqualTilde);

  /**
   * NotExists(x) - TODO describe `NotExists`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotExists.md">NotExists
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotExists = S.initFinalSymbol("NotExists", ID.NotExists);

  /**
   * NotGreater(x) - TODO describe `NotGreater`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotGreater.md">NotGreater
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotGreater = S.initFinalSymbol("NotGreater", ID.NotGreater);

  /**
   * NotGreaterEqual(x) - TODO describe `NotGreaterEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotGreaterEqual.md">NotGreaterEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotGreaterEqual =
      S.initFinalSymbol("NotGreaterEqual", ID.NotGreaterEqual);

  /**
   * NotGreaterFullEqual(x) - TODO describe `NotGreaterFullEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotGreaterFullEqual.md">NotGreaterFullEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotGreaterFullEqual =
      S.initFinalSymbol("NotGreaterFullEqual", ID.NotGreaterFullEqual);

  /**
   * NotGreaterGreater(x) - TODO describe `NotGreaterGreater`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotGreaterGreater.md">NotGreaterGreater
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotGreaterGreater =
      S.initFinalSymbol("NotGreaterGreater", ID.NotGreaterGreater);

  /**
   * NotGreaterLess(x) - TODO describe `NotGreaterLess`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotGreaterLess.md">NotGreaterLess
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotGreaterLess =
      S.initFinalSymbol("NotGreaterLess", ID.NotGreaterLess);

  /**
   * NotGreaterSlantEqual(x) - TODO describe `NotGreaterSlantEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotGreaterSlantEqual.md">NotGreaterSlantEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotGreaterSlantEqual =
      S.initFinalSymbol("NotGreaterSlantEqual", ID.NotGreaterSlantEqual);

  /**
   * NotGreaterTilde(x) - TODO describe `NotGreaterTilde`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotGreaterTilde.md">NotGreaterTilde
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotGreaterTilde =
      S.initFinalSymbol("NotGreaterTilde", ID.NotGreaterTilde);

  /**
   * Nothing - during evaluation of a list with a `Nothing` element `{..., Nothing, ...}`, the
   * symbol `Nothing` is removed from the arguments.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Nothing.md">Nothing
   *      documentation</a>
   */
  public final static IBuiltInSymbol Nothing = S.initFinalSymbol("Nothing", ID.Nothing);

  /**
   * NotHumpDownHump(x) - TODO describe `NotHumpDownHump`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotHumpDownHump.md">NotHumpDownHump
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotHumpDownHump =
      S.initFinalSymbol("NotHumpDownHump", ID.NotHumpDownHump);

  /**
   * NotHumpEqual(x) - TODO describe `NotHumpEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotHumpEqual.md">NotHumpEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotHumpEqual =
      S.initFinalSymbol("NotHumpEqual", ID.NotHumpEqual);

  /**
   * NotLeftTriangle(x) - TODO describe `NotLeftTriangle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotLeftTriangle.md">NotLeftTriangle
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotLeftTriangle =
      S.initFinalSymbol("NotLeftTriangle", ID.NotLeftTriangle);

  /**
   * NotLeftTriangleBar(x) - TODO describe `NotLeftTriangleBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotLeftTriangleBar.md">NotLeftTriangleBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotLeftTriangleBar =
      S.initFinalSymbol("NotLeftTriangleBar", ID.NotLeftTriangleBar);

  /**
   * NotLeftTriangleEqual(x) - TODO describe `NotLeftTriangleEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotLeftTriangleEqual.md">NotLeftTriangleEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotLeftTriangleEqual =
      S.initFinalSymbol("NotLeftTriangleEqual", ID.NotLeftTriangleEqual);

  /**
   * NotLess(x) - TODO describe `NotLess`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotLess.md">NotLess
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotLess = S.initFinalSymbol("NotLess", ID.NotLess);

  /**
   * NotLessEqual(x) - TODO describe `NotLessEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotLessEqual.md">NotLessEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotLessEqual =
      S.initFinalSymbol("NotLessEqual", ID.NotLessEqual);

  /**
   * NotLessFullEqual(x) - TODO describe `NotLessFullEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotLessFullEqual.md">NotLessFullEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotLessFullEqual =
      S.initFinalSymbol("NotLessFullEqual", ID.NotLessFullEqual);

  /**
   * NotLessGreater(x) - TODO describe `NotLessGreater`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotLessGreater.md">NotLessGreater
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotLessGreater =
      S.initFinalSymbol("NotLessGreater", ID.NotLessGreater);

  /**
   * NotLessLess(x) - TODO describe `NotLessLess`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotLessLess.md">NotLessLess
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotLessLess = S.initFinalSymbol("NotLessLess", ID.NotLessLess);

  /**
   * NotLessSlantEqual(x) - TODO describe `NotLessSlantEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotLessSlantEqual.md">NotLessSlantEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotLessSlantEqual =
      S.initFinalSymbol("NotLessSlantEqual", ID.NotLessSlantEqual);

  /**
   * NotLessTilde(x) - TODO describe `NotLessTilde`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotLessTilde.md">NotLessTilde
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotLessTilde =
      S.initFinalSymbol("NotLessTilde", ID.NotLessTilde);

  public final static IBuiltInSymbol NotListQ = S.initFinalSymbol("NotListQ", ID.NotListQ);

  /**
   * NotNestedGreaterGreater(x) - TODO describe `NotNestedGreaterGreater`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotNestedGreaterGreater.md">NotNestedGreaterGreater
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotNestedGreaterGreater =
      S.initFinalSymbol("NotNestedGreaterGreater", ID.NotNestedGreaterGreater);

  /**
   * NotNestedLessLess(x) - TODO describe `NotNestedLessLess`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotNestedLessLess.md">NotNestedLessLess
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotNestedLessLess =
      S.initFinalSymbol("NotNestedLessLess", ID.NotNestedLessLess);

  /**
   * NotPrecedes(x) - TODO describe `NotPrecedes`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotPrecedes.md">NotPrecedes
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotPrecedes = S.initFinalSymbol("NotPrecedes", ID.NotPrecedes);

  /**
   * NotPrecedesEqual(x) - TODO describe `NotPrecedesEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotPrecedesEqual.md">NotPrecedesEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotPrecedesEqual =
      S.initFinalSymbol("NotPrecedesEqual", ID.NotPrecedesEqual);

  /**
   * NotPrecedesSlantEqual(x) - TODO describe `NotPrecedesSlantEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotPrecedesSlantEqual.md">NotPrecedesSlantEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotPrecedesSlantEqual =
      S.initFinalSymbol("NotPrecedesSlantEqual", ID.NotPrecedesSlantEqual);

  /**
   * NotPrecedesTilde(x) - TODO describe `NotPrecedesTilde`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotPrecedesTilde.md">NotPrecedesTilde
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotPrecedesTilde =
      S.initFinalSymbol("NotPrecedesTilde", ID.NotPrecedesTilde);

  /**
   * NotReverseElement(x) - TODO describe `NotReverseElement`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotReverseElement.md">NotReverseElement
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotReverseElement =
      S.initFinalSymbol("NotReverseElement", ID.NotReverseElement);

  /**
   * NotRightTriangle(x) - TODO describe `NotRightTriangle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotRightTriangle.md">NotRightTriangle
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotRightTriangle =
      S.initFinalSymbol("NotRightTriangle", ID.NotRightTriangle);

  /**
   * NotRightTriangleBar(x) - TODO describe `NotRightTriangleBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotRightTriangleBar.md">NotRightTriangleBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotRightTriangleBar =
      S.initFinalSymbol("NotRightTriangleBar", ID.NotRightTriangleBar);

  /**
   * NotRightTriangleEqual(x) - TODO describe `NotRightTriangleEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotRightTriangleEqual.md">NotRightTriangleEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotRightTriangleEqual =
      S.initFinalSymbol("NotRightTriangleEqual", ID.NotRightTriangleEqual);

  /**
   * NotSquareSubset(x) - TODO describe `NotSquareSubset`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotSquareSubset.md">NotSquareSubset
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotSquareSubset =
      S.initFinalSymbol("NotSquareSubset", ID.NotSquareSubset);

  /**
   * NotSquareSubsetEqual(x) - TODO describe `NotSquareSubsetEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotSquareSubsetEqual.md">NotSquareSubsetEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotSquareSubsetEqual =
      S.initFinalSymbol("NotSquareSubsetEqual", ID.NotSquareSubsetEqual);

  /**
   * NotSquareSuperset(x) - TODO describe `NotSquareSuperset`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotSquareSuperset.md">NotSquareSuperset
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotSquareSuperset =
      S.initFinalSymbol("NotSquareSuperset", ID.NotSquareSuperset);

  /**
   * NotSquareSupersetEqual(x) - TODO describe `NotSquareSupersetEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotSquareSupersetEqual.md">NotSquareSupersetEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotSquareSupersetEqual =
      S.initFinalSymbol("NotSquareSupersetEqual", ID.NotSquareSupersetEqual);

  /**
   * NotSubset(x) - TODO describe `NotSubset`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotSubset.md">NotSubset
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotSubset = S.initFinalSymbol("NotSubset", ID.NotSubset);

  /**
   * NotSubsetEqual(x) - TODO describe `NotSubsetEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotSubsetEqual.md">NotSubsetEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotSubsetEqual =
      S.initFinalSymbol("NotSubsetEqual", ID.NotSubsetEqual);

  /**
   * NotSucceeds(x) - TODO describe `NotSucceeds`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotSucceeds.md">NotSucceeds
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotSucceeds = S.initFinalSymbol("NotSucceeds", ID.NotSucceeds);

  /**
   * NotSucceedsEqual(x) - TODO describe `NotSucceedsEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotSucceedsEqual.md">NotSucceedsEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotSucceedsEqual =
      S.initFinalSymbol("NotSucceedsEqual", ID.NotSucceedsEqual);

  /**
   * NotSucceedsSlantEqual(x) - TODO describe `NotSucceedsSlantEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotSucceedsSlantEqual.md">NotSucceedsSlantEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotSucceedsSlantEqual =
      S.initFinalSymbol("NotSucceedsSlantEqual", ID.NotSucceedsSlantEqual);

  /**
   * NotSucceedsTilde(x) - TODO describe `NotSucceedsTilde`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotSucceedsTilde.md">NotSucceedsTilde
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotSucceedsTilde =
      S.initFinalSymbol("NotSucceedsTilde", ID.NotSucceedsTilde);

  /**
   * NotSuperset(x) - TODO describe `NotSuperset`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotSuperset.md">NotSuperset
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotSuperset = S.initFinalSymbol("NotSuperset", ID.NotSuperset);

  /**
   * NotSupersetEqual(x) - TODO describe `NotSupersetEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotSupersetEqual.md">NotSupersetEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotSupersetEqual =
      S.initFinalSymbol("NotSupersetEqual", ID.NotSupersetEqual);

  /**
   * NotTilde(x) - TODO describe `NotTilde`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotTilde.md">NotTilde
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotTilde = S.initFinalSymbol("NotTilde", ID.NotTilde);

  /**
   * NotTildeEqual(x) - TODO describe `NotTildeEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotTildeEqual.md">NotTildeEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotTildeEqual =
      S.initFinalSymbol("NotTildeEqual", ID.NotTildeEqual);

  /**
   * NotTildeFullEqual(x) - TODO describe `NotTildeFullEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotTildeFullEqual.md">NotTildeFullEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotTildeFullEqual =
      S.initFinalSymbol("NotTildeFullEqual", ID.NotTildeFullEqual);

  /**
   * NotTildeTilde(x) - TODO describe `NotTildeTilde`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotTildeTilde.md">NotTildeTilde
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotTildeTilde =
      S.initFinalSymbol("NotTildeTilde", ID.NotTildeTilde);

  /**
   * NotVerticalBar(x) - TODO describe `NotVerticalBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NotVerticalBar.md">NotVerticalBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol NotVerticalBar =
      S.initFinalSymbol("NotVerticalBar", ID.NotVerticalBar);

  public final static IBuiltInSymbol Now = S.initFinalSymbol("Now", ID.Now);

  public final static IBuiltInSymbol NProbability =
      S.initFinalSymbol("NProbability", ID.NProbability);

  public final static IBuiltInSymbol NProduct = S.initFinalSymbol("NProduct", ID.NProduct);

  /**
   * NResidue(x) - TODO describe `NResidue`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NResidue.md">NResidue
   *      documentation</a>
   */
  public final static IBuiltInSymbol NResidue = S.initFinalSymbol("NResidue", ID.NResidue);

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

  /**
   * NSolveValues(equations, vars) - attempts to solve `equations` for the variables `vars`
   * numerically and returns a list of the values of the variables.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NSolveValues.md">NSolveValues
   *      documentation</a>
   */
  public final static IBuiltInSymbol NSolveValues =
      S.initFinalSymbol("NSolveValues", ID.NSolveValues);
  /**
   * NSum(expr, {i, imin, imax}) - evaluates the numerical approximated sum of `expr` with `i`
   * ranging from `imin` to `imax`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NSum.md">NSum
   *      documentation</a>
   */
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

  public final static IBuiltInSymbol NumberDigit = S.initFinalSymbol("NumberDigit", ID.NumberDigit);

  public final static IBuiltInSymbol NumberFieldRootsOfUnity =
      S.initFinalSymbol("NumberFieldRootsOfUnity", ID.NumberFieldRootsOfUnity);

  /**
   * NumberForm(x) - TODO describe `NumberForm`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberForm.md">NumberForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumberForm = S.initFinalSymbol("NumberForm", ID.NumberForm);

  /**
   * NumberFormat(x) - TODO describe `NumberFormat`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberFormat.md">NumberFormat
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumberFormat =
      S.initFinalSymbol("NumberFormat", ID.NumberFormat);

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
   * NumberMultiplier(x) - TODO describe `NumberMultiplier`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberMultiplier.md">NumberMultiplier
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumberMultiplier =
      S.initFinalSymbol("NumberMultiplier", ID.NumberMultiplier);

  /**
   * NumberPadding(x) - TODO describe `NumberPadding`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberPadding.md">NumberPadding
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumberPadding =
      S.initFinalSymbol("NumberPadding", ID.NumberPadding);

  /**
   * NumberPoint(x) - TODO describe `NumberPoint`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberPoint.md">NumberPoint
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumberPoint = S.initFinalSymbol("NumberPoint", ID.NumberPoint);

  /**
   * NumberQ(expr) - returns `True` if `expr` is an explicit number, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberQ.md">NumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumberQ = S.initFinalSymbol("NumberQ", ID.NumberQ);

  /**
   * NumberSeparator(x) - TODO describe `NumberSeparator`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberSeparator.md">NumberSeparator
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumberSeparator =
      S.initFinalSymbol("NumberSeparator", ID.NumberSeparator);

  /**
   * NumberSigns(x) - TODO describe `NumberSigns`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumberSigns.md">NumberSigns
   *      documentation</a>
   */
  public final static IBuiltInSymbol NumberSigns = S.initFinalSymbol("NumberSigns", ID.NumberSigns);

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

  /**
   * NumericFunction - is an attribute for a symbol `f` to denote that the result of `f(arg1, arg2,
   * ...)` can be treated as a numeric value provided that each `argN` is a numeric value.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/NumericFunction.md">NumericFunction
   *      documentation</a>
   */
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

  public final static IBuiltInSymbol Offset = S.initFinalSymbol("Offset", ID.Offset);

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

  /**
   * Opener(x) - TODO describe `Opener`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Opener.md">Opener
   *      documentation</a>
   */
  public final static IBuiltInSymbol Opener = S.initFinalSymbol("Opener", ID.Opener);

  /**
   * Opening(x) - TODO describe `Opening`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Opening.md">Opening
   *      documentation</a>
   */
  public final static IBuiltInSymbol Opening = S.initFinalSymbol("Opening", ID.Opening);

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

  /**
   * OptionsPattern(x) - is a pattern that stands for a sequence of options given to a function,
   * with default values taken from `Options(x)`. The options can be of the form `opt->value` or
   * `opt:>value`, and might be in arbitrarily nested lists.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OptionsPattern.md">OptionsPattern
   *      documentation</a>
   */
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
   * Or(expr1, expr2, ...) - `expr1 || expr2 || ...` evaluates each expression in turn, returning
   * `True` as soon as an expression evaluates to `True`. If all expressions evaluate to `False`,
   * `Or` returns `False`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Or.md">Or
   *      documentation</a>
   */
  public final static IBuiltInSymbol Or = S.initFinalSymbol("Or", ID.Or);

  /**
   * Orange - RGB color value for the color orange
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Orange.md">Orange
   *      documentation</a>
   */
  public final static IBuiltInSymbol Orange = S.initFinalSymbol("Orange", ID.Orange);

  /**
   * OrbitalElements(x) - TODO describe `OrbitalElements`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/OrbitalElements.md">OrbitalElements
   *      documentation</a>
   */
  public final static IBuiltInSymbol OrbitalElements =
      S.initFinalSymbol("OrbitalElements", ID.OrbitalElements);

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

  /**
   * Overflow( ) - represents a number too large to be represented by Symja.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Overflow.md">Overflow
   *      documentation</a>
   */
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

  /**
   * PaddedForm(x) - TODO describe `PaddedForm`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PaddedForm.md">PaddedForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol PaddedForm = S.initFinalSymbol("PaddedForm", ID.PaddedForm);

  /**
   * Padding(x) - TODO describe `Padding`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Padding.md">Padding
   *      documentation</a>
   */
  public final static IBuiltInSymbol Padding = S.initFinalSymbol("Padding", ID.Padding);

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

  /**
   * PairedBarChart(x) - TODO describe `PairedBarChart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PairedBarChart.md">PairedBarChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol PairedBarChart =
      S.initFinalSymbol("PairedBarChart", ID.PairedBarChart);

  /**
   * PairedHistogram(x) - TODO describe `PairedHistogram`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PairedHistogram.md">PairedHistogram
   *      documentation</a>
   */
  public final static IBuiltInSymbol PairedHistogram =
      S.initFinalSymbol("PairedHistogram", ID.PairedHistogram);

  /**
   * PairedSmoothHistogram(x) - TODO describe `PairedSmoothHistogram`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PairedSmoothHistogram.md">PairedSmoothHistogram
   *      documentation</a>
   */
  public final static IBuiltInSymbol PairedSmoothHistogram =
      S.initFinalSymbol("PairedSmoothHistogram", ID.PairedSmoothHistogram);

  /**
   * Pane(x) - TODO describe `Pane`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pane.md">Pane
   *      documentation</a>
   */
  public final static IBuiltInSymbol Pane = S.initFinalSymbol("Pane", ID.Pane);

  /**
   * Panel(x) - TODO describe `Panel`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Panel.md">Panel
   *      documentation</a>
   */
  public final static IBuiltInSymbol Panel = S.initFinalSymbol("Panel", ID.Panel);

  /**
   * Paneled(x) - TODO describe `Paneled`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Paneled.md">Paneled
   *      documentation</a>
   */
  public final static IBuiltInSymbol Paneled = S.initFinalSymbol("Paneled", ID.Paneled);

  /**
   * PaneSelector(x) - TODO describe `PaneSelector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PaneSelector.md">PaneSelector
   *      documentation</a>
   */
  public final static IBuiltInSymbol PaneSelector =
      S.initFinalSymbol("PaneSelector", ID.PaneSelector);

  public final static IBuiltInSymbol Parallelepiped =
      S.initFinalSymbol("Parallelepiped", ID.Parallelepiped);

  /**
   * Parallelization - is an option for `Compile` which says whether the compiled function may run
   * in parallel.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Parallelization.md">Parallelization
   *      documentation</a>
   */
  public final static IBuiltInSymbol Parallelization =
      S.initFinalSymbol("Parallelization", ID.Parallelization);

  public final static IBuiltInSymbol ParallelMap = S.initFinalSymbol("ParallelMap", ID.ParallelMap);

  public final static IBuiltInSymbol Parallelogram =
      S.initFinalSymbol("Parallelogram", ID.Parallelogram);

  /**
   * ParameterMixtureDistribution(x) - TODO describe `ParameterMixtureDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ParameterMixtureDistribution.md">ParameterMixtureDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol ParameterMixtureDistribution =
      S.initFinalSymbol("ParameterMixtureDistribution", ID.ParameterMixtureDistribution);

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

  public final static IBuiltInSymbol ParametricPlot3D =
      S.initFinalSymbol("ParametricPlot3D", ID.ParametricPlot3D);

  /**
   * ParametricRegion(x) - TODO describe `ParametricRegion`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ParametricRegion.md">ParametricRegion
   *      documentation</a>
   */
  public final static IBuiltInSymbol ParametricRegion =
      S.initFinalSymbol("ParametricRegion", ID.ParametricRegion);

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
   * PartialD(x) - TODO describe `PartialD`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PartialD.md">PartialD
   *      documentation</a>
   */
  public final static IBuiltInSymbol PartialD = S.initFinalSymbol("PartialD", ID.PartialD);

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

  /**
   * PathGraph({vertex1, vertex2, ...}) - create a new path graph with the given vertices `vertex1,
   * vertex2, ...`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PathGraph.md">PathGraph
   *      documentation</a>
   */
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
   * PauliMatrix(n) - returns the `n`th Pauli spin `2x2` matrix for `n` between `0` and `4`.
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
   * positive integer that is equal to the sum of its proper positive divisors, that is, the sum of
   * its positive divisors excluding the number itself.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PerfectNumber.md">PerfectNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol PerfectNumber =
      S.initFinalSymbol("PerfectNumber", ID.PerfectNumber);

  /**
   * PerfectNumberQ(n) - returns `True` if `n` is a perfect number. In number theory, a perfect
   * number is a positive integer that is equal to the sum of its proper positive divisors, that is,
   * the sum of its positive divisors excluding the number itself.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PerfectNumberQ.md">PerfectNumberQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol PerfectNumberQ =
      S.initFinalSymbol("PerfectNumberQ", ID.PerfectNumberQ);

  public final static IBuiltInSymbol PerformanceGoal =
      S.initFinalSymbol("PerformanceGoal", ID.PerformanceGoal);

  /**
   * Perimeter(geometric-form) - returns the perimeter of the `geometric-form`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Perimeter.md">Perimeter
   *      documentation</a>
   */
  public final static IBuiltInSymbol Perimeter = S.initFinalSymbol("Perimeter", ID.Perimeter);

  public final static IBuiltInSymbol PeriodicTablePlot =
      S.initFinalSymbol("PeriodicTablePlot", ID.PeriodicTablePlot);

  public final static IBuiltInSymbol PeriodogramArray =
      S.initFinalSymbol("PeriodogramArray", ID.PeriodogramArray);

  public final static IBuiltInSymbol Permanent = S.initFinalSymbol("Permanent", ID.Permanent);

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

  public final static IBuiltInSymbol PermutationProduct =
      S.initFinalSymbol("PermutationProduct", ID.PermutationProduct);

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
   * Perpendicular(x) - TODO describe `Perpendicular`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Perpendicular.md">Perpendicular
   *      documentation</a>
   */
  public final static IBuiltInSymbol Perpendicular =
      S.initFinalSymbol("Perpendicular", ID.Perpendicular);

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

  /**
   * Pink - RGB color value for the color pink
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pink.md">Pink
   *      documentation</a>
   */
  public final static IBuiltInSymbol Pink = S.initFinalSymbol("Pink", ID.Pink);

  /**
   * Placed(x) - TODO describe `Placed`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Placed.md">Placed
   *      documentation</a>
   */
  public final static IBuiltInSymbol Placed = S.initFinalSymbol("Placed", ID.Placed);

  /**
   * Plain(x) - TODO describe `Plain`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Plain.md">Plain
   *      documentation</a>
   */
  public final static IBuiltInSymbol Plain = S.initFinalSymbol("Plain", ID.Plain);

  /**
   * PlanarAngle(x) - TODO describe `PlanarAngle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PlanarAngle.md">PlanarAngle
   *      documentation</a>
   */
  public final static IBuiltInSymbol PlanarAngle = S.initFinalSymbol("PlanarAngle", ID.PlanarAngle);

  /**
   * PlanarFaceList(graph) - gives the list of faces of the planar `graph`. Each face is the
   * list of vertices bounding it, and the outer face is included.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PlanarFaceList.md">PlanarFaceList
   *      documentation</a>
   */
  public final static IBuiltInSymbol PlanarFaceList =
      S.initFinalSymbol("PlanarFaceList", ID.PlanarFaceList);

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

  /**
   * PlotFit(x) - TODO describe `PlotFit`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PlotFit.md">PlotFit
   *      documentation</a>
   */
  public final static IBuiltInSymbol PlotFit = S.initFinalSymbol("PlotFit", ID.PlotFit);

  /**
   * PlotFitElements(x) - TODO describe `PlotFitElements`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PlotFitElements.md">PlotFitElements
   *      documentation</a>
   */
  public final static IBuiltInSymbol PlotFitElements =
      S.initFinalSymbol("PlotFitElements", ID.PlotFitElements);

  /**
   * PlotHighlighting(x) - TODO describe `PlotHighlighting`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PlotHighlighting.md">PlotHighlighting
   *      documentation</a>
   */
  public final static IBuiltInSymbol PlotHighlighting =
      S.initFinalSymbol("PlotHighlighting", ID.PlotHighlighting);

  public final static IBuiltInSymbol PlotLabel = S.initFinalSymbol("PlotLabel", ID.PlotLabel);

  public final static IBuiltInSymbol PlotLabels = S.initFinalSymbol("PlotLabels", ID.PlotLabels);

  public final static IBuiltInSymbol PlotLegends = S.initFinalSymbol("PlotLegends", ID.PlotLegends);

  /**
   * PlotMarkers(x) - TODO describe `PlotMarkers`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PlotMarkers.md">PlotMarkers
   *      documentation</a>
   */
  public final static IBuiltInSymbol PlotMarkers = S.initFinalSymbol("PlotMarkers", ID.PlotMarkers);

  public final static IBuiltInSymbol PlotPoints = S.initFinalSymbol("PlotPoints", ID.PlotPoints);

  public final static IBuiltInSymbol PlotRange = S.initFinalSymbol("PlotRange", ID.PlotRange);

  public final static IBuiltInSymbol PlotRangeClipping =
      S.initFinalSymbol("PlotRangeClipping", ID.PlotRangeClipping);

  public final static IBuiltInSymbol PlotRangePadding =
      S.initFinalSymbol("PlotRangePadding", ID.PlotRangePadding);

  public final static IBuiltInSymbol PlotRegion = S.initFinalSymbol("PlotRegion", ID.PlotRegion);

  public final static IBuiltInSymbol PlotStyle = S.initFinalSymbol("PlotStyle", ID.PlotStyle);

  /**
   * PlotTheme(x) - TODO describe `PlotTheme`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PlotTheme.md">PlotTheme
   *      documentation</a>
   */
  public final static IBuiltInSymbol PlotTheme = S.initFinalSymbol("PlotTheme", ID.PlotTheme);

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

  /**
   * PointFigureChart(x) - TODO describe `PointFigureChart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PointFigureChart.md">PointFigureChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol PointFigureChart =
      S.initFinalSymbol("PointFigureChart", ID.PointFigureChart);

  /**
   * PointLegend(x) - TODO describe `PointLegend`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PointLegend.md">PointLegend
   *      documentation</a>
   */
  public final static IBuiltInSymbol PointLegend = S.initFinalSymbol("PointLegend", ID.PointLegend);

  public final static IBuiltInSymbol PointLight = S.initFinalSymbol("PointLight", ID.PointLight);

  public final static IBuiltInSymbol PointSize = S.initFinalSymbol("PointSize", ID.PointSize);

  /**
   * PoissonConsulDistribution(x) - TODO describe `PoissonConsulDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PoissonConsulDistribution.md">PoissonConsulDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol PoissonConsulDistribution =
      S.initFinalSymbol("PoissonConsulDistribution", ID.PoissonConsulDistribution);

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
   * PolarAxes(x) - TODO describe `PolarAxes`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolarAxes.md">PolarAxes
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolarAxes = S.initFinalSymbol("PolarAxes", ID.PolarAxes);

  /**
   * PolarGridLines(x) - TODO describe `PolarGridLines`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolarGridLines.md">PolarGridLines
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolarGridLines =
      S.initFinalSymbol("PolarGridLines", ID.PolarGridLines);

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
   * PolarTicks(x) - TODO describe `PolarTicks`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolarTicks.md">PolarTicks
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolarTicks = S.initFinalSymbol("PolarTicks", ID.PolarTicks);

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

  /**
   * PolygonAngle(poly) - returns the angles at the vertices of the polygon `poly`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolygonAngle.md">PolygonAngle
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolygonAngle =
      S.initFinalSymbol("PolygonAngle", ID.PolygonAngle);

  /**
   * PolygonCoordinates(poly) - returns the list of the coordinates of the polygon `poly`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolygonCoordinates.md">PolygonCoordinates
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolygonCoordinates =
      S.initFinalSymbol("PolygonCoordinates", ID.PolygonCoordinates);

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

  public final static IBuiltInSymbol PolynomialMod =
      S.initFinalSymbol("PolynomialMod", ID.PolynomialMod);

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

  public final static IBuiltInSymbol PolynomialReduce =
      S.initFinalSymbol("PolynomialReduce", ID.PolynomialReduce);

  /**
   * PolynomialRemainder(p, q, x) - returns the polynomial remainder of the polynomials `p` and `q`
   * for the variable `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PolynomialRemainder.md">PolynomialRemainder
   *      documentation</a>
   */
  public final static IBuiltInSymbol PolynomialRemainder =
      S.initFinalSymbol("PolynomialRemainder", ID.PolynomialRemainder);

  /**
   * PopupMenu(x) - TODO describe `PopupMenu`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PopupMenu.md">PopupMenu
   *      documentation</a>
   */
  public final static IBuiltInSymbol PopupMenu = S.initFinalSymbol("PopupMenu", ID.PopupMenu);

  /**
   * Position(expr, patt) - returns the list of positions for which `expr` matches `patt`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Position.md">Position
   *      documentation</a>
   */
  public final static IBuiltInSymbol Position = S.initFinalSymbol("Position", ID.Position);

  public final static IBuiltInSymbol PositionIndex =
      S.initFinalSymbol("PositionIndex", ID.PositionIndex);

  /**
   * Positive(x) - returns `True` if `x` is a positive real number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Positive.md">Positive
   *      documentation</a>
   */
  public final static IBuiltInSymbol Positive = S.initFinalSymbol("Positive", ID.Positive);

  public final static IBuiltInSymbol PositiveDefiniteMatrixQ =
      S.initFinalSymbol("PositiveDefiniteMatrixQ", ID.PositiveDefiniteMatrixQ);

  public final static IBuiltInSymbol PositiveIntegers =
      S.initFinalSymbol("PositiveIntegers", ID.PositiveIntegers);

  public final static IBuiltInSymbol PositiveRationals =
      S.initFinalSymbol("PositiveRationals", ID.PositiveRationals);

  public final static IBuiltInSymbol PositiveReals =
      S.initFinalSymbol("PositiveReals", ID.PositiveReals);

  public final static IBuiltInSymbol PositiveSemidefiniteMatrixQ =
      S.initFinalSymbol("PositiveSemidefiniteMatrixQ", ID.PositiveSemidefiniteMatrixQ);

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
   * PowerMod(x, y, m) - computes `x^y` modulo `m`. `x` and `m` must be Gaussian integers and the
   * `y` must be an integer or rational number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PowerMod.md">PowerMod
   *      documentation</a>
   */
  public final static IBuiltInSymbol PowerMod = S.initFinalSymbol("PowerMod", ID.PowerMod);

  /**
   * PowerRange(base) - Generates a list of powers from exponent `1` to `max`. Max is the largest
   * power of '10` less equal `b ase`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PowerRange.md">PowerRange
   *      documentation</a>
   */
  public final static IBuiltInSymbol PowerRange = S.initFinalSymbol("PowerRange", ID.PowerRange);

  /**
   * PowersRepresentations(intNumber, k, exponent) - computes the representations of the `intNumber`
   * as sum of `x^exponent` terms which occur `k` times.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PowersRepresentations.md">PowersRepresentations
   *      documentation</a>
   */
  public final static IBuiltInSymbol PowersRepresentations =
      S.initFinalSymbol("PowersRepresentations", ID.PowersRepresentations);

  public final static IBuiltInSymbol PrecedenceForm =
      S.initFinalSymbol("PrecedenceForm", ID.PrecedenceForm);

  /**
   * Precedes(x) - TODO describe `Precedes`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Precedes.md">Precedes
   *      documentation</a>
   */
  public final static IBuiltInSymbol Precedes = S.initFinalSymbol("Precedes", ID.Precedes);

  /**
   * PrecedesEqual(x) - TODO describe `PrecedesEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrecedesEqual.md">PrecedesEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrecedesEqual =
      S.initFinalSymbol("PrecedesEqual", ID.PrecedesEqual);

  /**
   * PrecedesSlantEqual(x) - TODO describe `PrecedesSlantEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrecedesSlantEqual.md">PrecedesSlantEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrecedesSlantEqual =
      S.initFinalSymbol("PrecedesSlantEqual", ID.PrecedesSlantEqual);

  /**
   * PrecedesTilde(x) - TODO describe `PrecedesTilde`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrecedesTilde.md">PrecedesTilde
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrecedesTilde =
      S.initFinalSymbol("PrecedesTilde", ID.PrecedesTilde);

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

  public final static IBuiltInSymbol PreserveImageOptions =
      S.initFinalSymbol("PreserveImageOptions", ID.PreserveImageOptions);

  /**
   * PreviousDate(x) - TODO describe `PreviousDate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PreviousDate.md">PreviousDate
   *      documentation</a>
   */
  public final static IBuiltInSymbol PreviousDate =
      S.initFinalSymbol("PreviousDate", ID.PreviousDate);

  /**
   * Prime(n) - returns the `n`th prime number.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Prime.md">Prime
   *      documentation</a>
   */
  public final static IBuiltInSymbol Prime = S.initFinalSymbol("Prime", ID.Prime);

  public final static IBuiltInSymbol PrimeNu = S.initFinalSymbol("PrimeNu", ID.PrimeNu);

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

  /**
   * PrimeZetaP(z) - returns the prime zeta function of `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/PrimeZetaP.md">PrimeZetaP
   *      documentation</a>
   */
  public final static IBuiltInSymbol PrimeZetaP = S.initFinalSymbol("PrimeZetaP", ID.PrimeZetaP);

  public final static IBuiltInSymbol PrimitivePolynomialQ =
      S.initFinalSymbol("PrimitivePolynomialQ", ID.PrimitivePolynomialQ);

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
   * ProbabilityDistribution(x) - TODO describe `ProbabilityDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ProbabilityDistribution.md">ProbabilityDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol ProbabilityDistribution =
      S.initFinalSymbol("ProbabilityDistribution", ID.ProbabilityDistribution);

  /**
   * ProbabilityPlot(x) - TODO describe `ProbabilityPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ProbabilityPlot.md">ProbabilityPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ProbabilityPlot =
      S.initFinalSymbol("ProbabilityPlot", ID.ProbabilityPlot);

  /**
   * ProbabilityScalePlot(x) - TODO describe `ProbabilityScalePlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ProbabilityScalePlot.md">ProbabilityScalePlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ProbabilityScalePlot =
      S.initFinalSymbol("ProbabilityScalePlot", ID.ProbabilityScalePlot);

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
   * ProductDistribution(x) - TODO describe `ProductDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ProductDistribution.md">ProductDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol ProductDistribution =
      S.initFinalSymbol("ProductDistribution", ID.ProductDistribution);

  /**
   * ProductLog(z) - returns the value of the Lambert W function at `z`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ProductLog.md">ProductLog
   *      documentation</a>
   */
  public final static IBuiltInSymbol ProductLog = S.initFinalSymbol("ProductLog", ID.ProductLog);

  /**
   * ProgressIndicator(x) - TODO describe `ProgressIndicator`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ProgressIndicator.md">ProgressIndicator
   *      documentation</a>
   */
  public final static IBuiltInSymbol ProgressIndicator =
      S.initFinalSymbol("ProgressIndicator", ID.ProgressIndicator);

  /**
   * Projection(vector1, vector2) - Find the orthogonal projection of `vector1` onto another
   * `vector2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Projection.md">Projection
   *      documentation</a>
   */
  public final static IBuiltInSymbol Projection = S.initFinalSymbol("Projection", ID.Projection);

  public final static IBuiltInSymbol Prolog = S.initFinalSymbol("Prolog", ID.Prolog);

  public final static IBuiltInSymbol Proportion = S.initFinalSymbol("Proportion", ID.Proportion);

  public final static IBuiltInSymbol Proportional =
      S.initFinalSymbol("Proportional", ID.Proportional);

  public final static IBuiltInSymbol Protect = S.initFinalSymbol("Protect", ID.Protect);

  public final static IBuiltInSymbol Protected = S.initFinalSymbol("Protected", ID.Protected);

  /**
   * ProteinData(x) - TODO describe `ProteinData`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ProteinData.md">ProteinData
   *      documentation</a>
   */
  public final static IBuiltInSymbol ProteinData = S.initFinalSymbol("ProteinData", ID.ProteinData);

  /**
   * Pruning(x) - TODO describe `Pruning`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Pruning.md">Pruning
   *      documentation</a>
   */
  public final static IBuiltInSymbol Pruning = S.initFinalSymbol("Pruning", ID.Pruning);

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

  /**
   * Purple - RGB color value for the color purple
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Purple.md">Purple
   *      documentation</a>
   */
  public final static IBuiltInSymbol Purple = S.initFinalSymbol("Purple", ID.Purple);

  public final static IBuiltInSymbol Put = S.initFinalSymbol("Put", ID.Put);

  public final static IBuiltInSymbol PutAppend = S.initFinalSymbol("PutAppend", ID.PutAppend);

  public final static IBuiltInSymbol Pyramid = S.initFinalSymbol("Pyramid", ID.Pyramid);

  public final static IBuiltInSymbol QPochhammer = S.initFinalSymbol("QPochhammer", ID.QPochhammer);

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
   * QuantilePlot(x) - TODO describe `QuantilePlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantilePlot.md">QuantilePlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuantilePlot =
      S.initFinalSymbol("QuantilePlot", ID.QuantilePlot);

  /**
   * Quantity(value, unit) - returns the quantity for `value` and `unit`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quantity.md">Quantity
   *      documentation</a>
   */
  public final static IBuiltInSymbol Quantity = S.initFinalSymbol("Quantity", ID.Quantity);

  /**
   * QuantityArray(x) - TODO describe `QuantityArray`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityArray.md">QuantityArray
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuantityArray =
      S.initFinalSymbol("QuantityArray", ID.QuantityArray);

  public final static IBuiltInSymbol QuantityDistribution =
      S.initFinalSymbol("QuantityDistribution", ID.QuantityDistribution);

  /**
   * QuantityForm(x) - TODO describe `QuantityForm`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityForm.md">QuantityForm
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuantityForm =
      S.initFinalSymbol("QuantityForm", ID.QuantityForm);

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

  /**
   * QuantityUnit(quantity) - return the unit of the `quantity`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityUnit.md">QuantityUnit
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuantityUnit =
      S.initFinalSymbol("QuantityUnit", ID.QuantityUnit);

  /**
   * QuantityVariable(x) - TODO describe `QuantityVariable`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityVariable.md">QuantityVariable
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuantityVariable =
      S.initFinalSymbol("QuantityVariable", ID.QuantityVariable);

  /**
   * QuantityVariableCanonicalUnit(x) - TODO describe `QuantityVariableCanonicalUnit`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityVariableCanonicalUnit.md">QuantityVariableCanonicalUnit
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuantityVariableCanonicalUnit =
      S.initFinalSymbol("QuantityVariableCanonicalUnit", ID.QuantityVariableCanonicalUnit);

  /**
   * QuantityVariableDimensions(x) - TODO describe `QuantityVariableDimensions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityVariableDimensions.md">QuantityVariableDimensions
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuantityVariableDimensions =
      S.initFinalSymbol("QuantityVariableDimensions", ID.QuantityVariableDimensions);

  /**
   * QuantityVariableIdentifier(x) - TODO describe `QuantityVariableIdentifier`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityVariableIdentifier.md">QuantityVariableIdentifier
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuantityVariableIdentifier =
      S.initFinalSymbol("QuantityVariableIdentifier", ID.QuantityVariableIdentifier);

  /**
   * QuantityVariablePhysicalQuantity(x) - TODO describe `QuantityVariablePhysicalQuantity`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/QuantityVariablePhysicalQuantity.md">QuantityVariablePhysicalQuantity
   *      documentation</a>
   */
  public final static IBuiltInSymbol QuantityVariablePhysicalQuantity =
      S.initFinalSymbol("QuantityVariablePhysicalQuantity", ID.QuantityVariablePhysicalQuantity);

  /**
   * Quartics(x) - TODO describe `Quartics`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Quartics.md">Quartics
   *      documentation</a>
   */
  public final static IBuiltInSymbol Quartics = S.initFinalSymbol("Quartics", ID.Quartics);

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


  public final static IBuiltInSymbol Query = S.initFinalSymbol("Query", ID.Query);

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
   * RadioButton(x) - TODO describe `RadioButton`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RadioButton.md">RadioButton
   *      documentation</a>
   */
  public final static IBuiltInSymbol RadioButton = S.initFinalSymbol("RadioButton", ID.RadioButton);

  /**
   * RadioButtonBar(x) - TODO describe `RadioButtonBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RadioButtonBar.md">RadioButtonBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol RadioButtonBar =
      S.initFinalSymbol("RadioButtonBar", ID.RadioButtonBar);

  /**
   * Radius(x) - TODO describe `Radius`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Radius.md">Radius
   *      documentation</a>
   */
  public final static IBuiltInSymbol Radius = S.initFinalSymbol("Radius", ID.Radius);

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

  /**
   * Random() - gives a pseudorandom real number in the range `0.0` to `1.0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Random.md">Random
   *      documentation</a>
   */
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
   * RangeFilter(x) - TODO describe `RangeFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RangeFilter.md">RangeFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol RangeFilter = S.initFinalSymbol("RangeFilter", ID.RangeFilter);

  public final static IBuiltInSymbol RangeSpace = S.initFinalSymbol("RangeSpace", ID.RangeSpace);


  public final static IBuiltInSymbol RankDecomposition =
      S.initFinalSymbol("RankDecomposition", ID.RankDecomposition);

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
   * Raster(x) - TODO describe `Raster`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Raster.md">Raster
   *      documentation</a>
   */
  public final static IBuiltInSymbol Raster = S.initFinalSymbol("Raster", ID.Raster);

  /**
   * Raster3D(x) - TODO describe `Raster3D`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Raster3D.md">Raster3D
   *      documentation</a>
   */
  public final static IBuiltInSymbol Raster3D = S.initFinalSymbol("Raster3D", ID.Raster3D);

  /**
   * Rasterize(x) - TODO describe `Rasterize`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rasterize.md">Rasterize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Rasterize = S.initFinalSymbol("Rasterize", ID.Rasterize);

  /**
   * RasterSize(x) - TODO describe `RasterSize`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RasterSize.md">RasterSize
   *      documentation</a>
   */
  public final static IBuiltInSymbol RasterSize = S.initFinalSymbol("RasterSize", ID.RasterSize);

  /**
   * Rational - is the head of rational numbers.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rational.md">Rational
   *      documentation</a>
   */
  public final static IBuiltInSymbol Rational = S.initFinalSymbol("Rational", ID.Rational);

  public final static IBuiltInSymbol RationalFunctions =
      S.initFinalSymbol("RationalFunctions", ID.RationalFunctions);

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

  /**
   * Ratios({x1, x2,...}) - computes the ratios `{x2/x1,x3/x2, x4/x2, x5/x4}`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Ratios.md">Ratios
   *      documentation</a>
   */
  public final static IBuiltInSymbol Ratios = S.initFinalSymbol("Ratios", ID.Ratios);

  public final static IBuiltInSymbol RawBackquote =
      S.initFinalSymbol("RawBackquote", ID.RawBackquote);

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
   * ReactionBalance(x) - TODO describe `ReactionBalance`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReactionBalance.md">ReactionBalance
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReactionBalance =
      S.initFinalSymbol("ReactionBalance", ID.ReactionBalance);

  /**
   * ReactionBalancedQ(x) - TODO describe `ReactionBalancedQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReactionBalancedQ.md">ReactionBalancedQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReactionBalancedQ =
      S.initFinalSymbol("ReactionBalancedQ", ID.ReactionBalancedQ);

  /**
   * Read(input-stream) - reads the `input-stream` and return one expression.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Read.md">Read
   *      documentation</a>
   */
  public final static IBuiltInSymbol Read = S.initFinalSymbol("Read", ID.Read);

  public final static IBuiltInSymbol ReadLine = S.initFinalSymbol("ReadLine", ID.ReadLine);

  /**
   * ReadList(input-stream) - reads all the expressions until the end of file and return a list of
   * these expressions.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReadList.md">ReadList
   *      documentation</a>
   */
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

  /**
   * RectangleChart(x) - TODO describe `RectangleChart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RectangleChart.md">RectangleChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol RectangleChart =
      S.initFinalSymbol("RectangleChart", ID.RectangleChart);

  /**
   * Red - RGB color value for the color red
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Red.md">Red
   *      documentation</a>
   */
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
   * ReferenceAltitude(x) - TODO describe `ReferenceAltitude`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReferenceAltitude.md">ReferenceAltitude
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReferenceAltitude =
      S.initFinalSymbol("ReferenceAltitude", ID.ReferenceAltitude);

  /**
   * Refine(expression, assumptions) - evaluate the `expression` for the given `assumptions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Refine.md">Refine
   *      documentation</a>
   */
  public final static IBuiltInSymbol Refine = S.initFinalSymbol("Refine", ID.Refine);

  /**
   * ReflectionTransform(v) - gives a `TransformationFunction` that represents a reflection in a
   * mirror through the origin, normal to the vector `v`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReflectionTransform.md">ReflectionTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReflectionTransform =
      S.initFinalSymbol("ReflectionTransform", ID.ReflectionTransform);

  /**
   * Refresh(x) - TODO describe `Refresh`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Refresh.md">Refresh
   *      documentation</a>
   */
  public final static IBuiltInSymbol Refresh = S.initFinalSymbol("Refresh", ID.Refresh);

  /**
   * RefreshRate(x) - TODO describe `RefreshRate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RefreshRate.md">RefreshRate
   *      documentation</a>
   */
  public final static IBuiltInSymbol RefreshRate = S.initFinalSymbol("RefreshRate", ID.RefreshRate);

  public final static IBuiltInSymbol Region = S.initFinalSymbol("Region", ID.Region);

  /**
   * RegionBoundary(x) - TODO describe `RegionBoundary`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegionBoundary.md">RegionBoundary
   *      documentation</a>
   */
  public final static IBuiltInSymbol RegionBoundary =
      S.initFinalSymbol("RegionBoundary", ID.RegionBoundary);

  /**
   * RegionBoundaryStyle(x) - TODO describe `RegionBoundaryStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegionBoundaryStyle.md">RegionBoundaryStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol RegionBoundaryStyle =
      S.initFinalSymbol("RegionBoundaryStyle", ID.RegionBoundaryStyle);

  public final static IBuiltInSymbol RegionBounds =
      S.initFinalSymbol("RegionBounds", ID.RegionBounds);

  public final static IBuiltInSymbol RegionCentroid =
      S.initFinalSymbol("RegionCentroid", ID.RegionCentroid);

  /**
   * RegionDifference(x) - TODO describe `RegionDifference`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegionDifference.md">RegionDifference
   *      documentation</a>
   */
  public final static IBuiltInSymbol RegionDifference =
      S.initFinalSymbol("RegionDifference", ID.RegionDifference);

  public final static IBuiltInSymbol RegionDimension =
      S.initFinalSymbol("RegionDimension", ID.RegionDimension);

  public final static IBuiltInSymbol RegionDistance =
      S.initFinalSymbol("RegionDistance", ID.RegionDistance);

  public final static IBuiltInSymbol RegionEmbeddingDimension =
      S.initFinalSymbol("RegionEmbeddingDimension", ID.RegionEmbeddingDimension);

  /**
   * RegionEqual(x) - TODO describe `RegionEqual`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegionEqual.md">RegionEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol RegionEqual = S.initFinalSymbol("RegionEqual", ID.RegionEqual);

  /**
   * RegionFunction(x) - TODO describe `RegionFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegionFunction.md">RegionFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol RegionFunction =
      S.initFinalSymbol("RegionFunction", ID.RegionFunction);

  /**
   * RegionIntersection(x) - TODO describe `RegionIntersection`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegionIntersection.md">RegionIntersection
   *      documentation</a>
   */
  public final static IBuiltInSymbol RegionIntersection =
      S.initFinalSymbol("RegionIntersection", ID.RegionIntersection);

  public final static IBuiltInSymbol RegionMeasure =
      S.initFinalSymbol("RegionMeasure", ID.RegionMeasure);

  public final static IBuiltInSymbol RegionMember =
      S.initFinalSymbol("RegionMember", ID.RegionMember);

  /**
   * RegionMemberFunction(x) - TODO describe `RegionMemberFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegionMemberFunction.md">RegionMemberFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol RegionMemberFunction =
      S.initFinalSymbol("RegionMemberFunction", ID.RegionMemberFunction);

  /**
   * RegionMoment(x) - TODO describe `RegionMoment`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegionMoment.md">RegionMoment
   *      documentation</a>
   */
  public final static IBuiltInSymbol RegionMoment =
      S.initFinalSymbol("RegionMoment", ID.RegionMoment);

  public final static IBuiltInSymbol RegionNearest =
      S.initFinalSymbol("RegionNearest", ID.RegionNearest);

  public final static IBuiltInSymbol RegionNearestFunction =
      S.initFinalSymbol("RegionNearestFunction", ID.RegionNearestFunction);

  /**
   * RegionPlot(x) - TODO describe `RegionPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegionPlot.md">RegionPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol RegionPlot = S.initFinalSymbol("RegionPlot", ID.RegionPlot);

  /**
   * RegionQ(x) - TODO describe `RegionQ`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegionQ.md">RegionQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol RegionQ = S.initFinalSymbol("RegionQ", ID.RegionQ);

  /**
   * RegionSymmetricDifference(x) - TODO describe `RegionSymmetricDifference`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegionSymmetricDifference.md">RegionSymmetricDifference
   *      documentation</a>
   */
  public final static IBuiltInSymbol RegionSymmetricDifference =
      S.initFinalSymbol("RegionSymmetricDifference", ID.RegionSymmetricDifference);

  /**
   * RegionUnion(x) - TODO describe `RegionUnion`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RegionUnion.md">RegionUnion
   *      documentation</a>
   */
  public final static IBuiltInSymbol RegionUnion = S.initFinalSymbol("RegionUnion", ID.RegionUnion);

  public final static IBuiltInSymbol RegionWithin =
      S.initFinalSymbol("RegionWithin", ID.RegionWithin);

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

  public final static IBuiltInSymbol RegularPolygon =
      S.initFinalSymbol("RegularPolygon", ID.RegularPolygon);

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

  /**
   * ReliefImage(x) - TODO describe `ReliefImage`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReliefImage.md">ReliefImage
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReliefImage = S.initFinalSymbol("ReliefImage", ID.ReliefImage);

  /**
   * ReliefPlot(x) - TODO describe `ReliefPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReliefPlot.md">ReliefPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReliefPlot = S.initFinalSymbol("ReliefPlot", ID.ReliefPlot);

  public final static IBuiltInSymbol Remove = S.initFinalSymbol("Remove", ID.Remove);

  /**
   * RemoveAlphaChannel(x) - TODO describe `RemoveAlphaChannel`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RemoveAlphaChannel.md">RemoveAlphaChannel
   *      documentation</a>
   */
  public final static IBuiltInSymbol RemoveAlphaChannel =
      S.initFinalSymbol("RemoveAlphaChannel", ID.RemoveAlphaChannel);

  /**
   * RemoveBackground(x) - TODO describe `RemoveBackground`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RemoveBackground.md">RemoveBackground
   *      documentation</a>
   */
  public final static IBuiltInSymbol RemoveBackground =
      S.initFinalSymbol("RemoveBackground", ID.RemoveBackground);

  /**
   * RemoveDiacritics("string") - returns a version of `string` with all diacritics removed.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RemoveDiacritics.md">RemoveDiacritics
   *      documentation</a>
   */
  public final static IBuiltInSymbol RemoveDiacritics =
      S.initFinalSymbol("RemoveDiacritics", ID.RemoveDiacritics);

  /**
   * RenkoChart(x) - TODO describe `RenkoChart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RenkoChart.md">RenkoChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol RenkoChart = S.initFinalSymbol("RenkoChart", ID.RenkoChart);

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

  /**
   * ReplaceAll(expr, i -> new) - replaces all `i` in `expr` with `new`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceAll.md">ReplaceAll
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReplaceAll = S.initFinalSymbol("ReplaceAll", ID.ReplaceAll);

  /**
   * ReplaceAt(expr, lhs -> rhs, position) - replaces the given `position` in `expr` which matches
   * `lhs` with `rhs`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReplaceAt.md">ReplaceAt
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReplaceAt = S.initFinalSymbol("ReplaceAt", ID.ReplaceAt);

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
   * Resampling(x) - TODO describe `Resampling`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Resampling.md">Resampling
   *      documentation</a>
   */
  public final static IBuiltInSymbol Resampling = S.initFinalSymbol("Resampling", ID.Resampling);

  /**
   * Rescale(list) - returns `Rescale(list,{Min(list), Max(list)})`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rescale.md">Rescale
   *      documentation</a>
   */
  public final static IBuiltInSymbol Rescale = S.initFinalSymbol("Rescale", ID.Rescale);

  public final static IBuiltInSymbol Residue = S.initFinalSymbol("Residue", ID.Residue);

  /**
   * Resolve(expr) - attempts to resolve `expr` into a form that eliminates the `ForAll` and
   * `Exists` quantifiers.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Resolve.md">Resolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol Resolve = S.initFinalSymbol("Resolve", ID.Resolve);

  /**
   * ResourceData(x) - TODO describe `ResourceData`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ResourceData.md">ResourceData
   *      documentation</a>
   */
  public final static IBuiltInSymbol ResourceData =
      S.initFinalSymbol("ResourceData", ID.ResourceData);

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

  public final static IBuiltInSymbol ReverseElement =
      S.initFinalSymbol("ReverseElement", ID.ReverseElement);

  public final static IBuiltInSymbol ReverseEquilibrium =
      S.initFinalSymbol("ReverseEquilibrium", ID.ReverseEquilibrium);

  /**
   * ReverseSort(list) - sorts `list` (or the leaves of any other expression) according to reversed
   * canonical ordering.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ReverseSort.md">ReverseSort
   *      documentation</a>
   */
  public final static IBuiltInSymbol ReverseSort = S.initFinalSymbol("ReverseSort", ID.ReverseSort);

  public final static IBuiltInSymbol ReverseUpEquilibrium =
      S.initFinalSymbol("ReverseUpEquilibrium", ID.ReverseUpEquilibrium);

  /**
   * RevolutionAxis(x) - TODO describe `RevolutionAxis`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RevolutionAxis.md">RevolutionAxis
   *      documentation</a>
   */
  public final static IBuiltInSymbol RevolutionAxis =
      S.initFinalSymbol("RevolutionAxis", ID.RevolutionAxis);

  public final static IBuiltInSymbol RevolutionPlot3D =
      S.initFinalSymbol("RevolutionPlot3D", ID.RevolutionPlot3D);

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
   * RiceDistribution(x) - TODO describe `RiceDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RiceDistribution.md">RiceDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol RiceDistribution =
      S.initFinalSymbol("RiceDistribution", ID.RiceDistribution);

  /**
   * RidgeFilter(x) - TODO describe `RidgeFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RidgeFilter.md">RidgeFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol RidgeFilter = S.initFinalSymbol("RidgeFilter", ID.RidgeFilter);

  /**
   * RiemannSiegelTheta(t) - gives the Riemann-Siegel function `theta(t)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RiemannSiegelTheta.md">RiemannSiegelTheta
   *      documentation</a>
   */
  public final static IBuiltInSymbol RiemannSiegelTheta =
      S.initFinalSymbol("RiemannSiegelTheta", ID.RiemannSiegelTheta);

  /**
   * Riffle(list1, list2) - insert elements of `list2` between the elements of `list1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Riffle.md">Riffle
   *      documentation</a>
   */
  public final static IBuiltInSymbol Riffle = S.initFinalSymbol("Riffle", ID.Riffle);

  public final static IBuiltInSymbol Right = S.initFinalSymbol("Right", ID.Right);

  public final static IBuiltInSymbol RightArrow = S.initFinalSymbol("RightArrow", ID.RightArrow);

  public final static IBuiltInSymbol RightArrowBar =
      S.initFinalSymbol("RightArrowBar", ID.RightArrowBar);

  public final static IBuiltInSymbol RightArrowLeftArrow =
      S.initFinalSymbol("RightArrowLeftArrow", ID.RightArrowLeftArrow);

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

  public final static IBuiltInSymbol RightDownTeeVector =
      S.initFinalSymbol("RightDownTeeVector", ID.RightDownTeeVector);

  public final static IBuiltInSymbol RightDownVector =
      S.initFinalSymbol("RightDownVector", ID.RightDownVector);

  public final static IBuiltInSymbol RightDownVectorBar =
      S.initFinalSymbol("RightDownVectorBar", ID.RightDownVectorBar);

  public final static IBuiltInSymbol RightTee = S.initFinalSymbol("RightTee", ID.RightTee);

  public final static IBuiltInSymbol RightTeeArrow =
      S.initFinalSymbol("RightTeeArrow", ID.RightTeeArrow);

  public final static IBuiltInSymbol RightTeeVector =
      S.initFinalSymbol("RightTeeVector", ID.RightTeeVector);

  public final static IBuiltInSymbol RightTriangle =
      S.initFinalSymbol("RightTriangle", ID.RightTriangle);

  public final static IBuiltInSymbol RightTriangleBar =
      S.initFinalSymbol("RightTriangleBar", ID.RightTriangleBar);

  public final static IBuiltInSymbol RightTriangleEqual =
      S.initFinalSymbol("RightTriangleEqual", ID.RightTriangleEqual);

  public final static IBuiltInSymbol RightUpDownVector =
      S.initFinalSymbol("RightUpDownVector", ID.RightUpDownVector);

  public final static IBuiltInSymbol RightUpTeeVector =
      S.initFinalSymbol("RightUpTeeVector", ID.RightUpTeeVector);

  public final static IBuiltInSymbol RightUpVector =
      S.initFinalSymbol("RightUpVector", ID.RightUpVector);

  public final static IBuiltInSymbol RightUpVectorBar =
      S.initFinalSymbol("RightUpVectorBar", ID.RightUpVectorBar);

  public final static IBuiltInSymbol RightVector = S.initFinalSymbol("RightVector", ID.RightVector);

  public final static IBuiltInSymbol RightVectorBar =
      S.initFinalSymbol("RightVectorBar", ID.RightVectorBar);

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

  /**
   * RootMeanSquare(list) - calculate the root mean square
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RootMeanSquare.md">RootMeanSquare
   *      documentation</a>
   */
  public final static IBuiltInSymbol RootMeanSquare =
      S.initFinalSymbol("RootMeanSquare", ID.RootMeanSquare);

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

  public final static IBuiltInSymbol RootSum = S.initFinalSymbol("RootSum", ID.RootSum);

  /**
   * Rotate(x) - TODO describe `Rotate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Rotate.md">Rotate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Rotate = S.initFinalSymbol("Rotate", ID.Rotate);

  public final static IBuiltInSymbol RotateLabel = S.initFinalSymbol("RotateLabel", ID.RotateLabel);

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
   * RotationAction(x) - TODO describe `RotationAction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RotationAction.md">RotationAction
   *      documentation</a>
   */
  public final static IBuiltInSymbol RotationAction =
      S.initFinalSymbol("RotationAction", ID.RotationAction);

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

  public final static IBuiltInSymbol RoundImplies =
      S.initFinalSymbol("RoundImplies", ID.RoundImplies);

  public final static IBuiltInSymbol RoundingRadius =
      S.initFinalSymbol("RoundingRadius", ID.RoundingRadius);

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

  /**
   * RSolve(equation, y(var), var) - attempts to solve a recurrence `equation` for the function
   * `y(var)` and variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RSolve.md">RSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol RSolve = S.initFinalSymbol("RSolve", ID.RSolve);

  /**
   * RSolveValue(equation, f(var), var) - attempts to solve a recurrence `equation` for the function
   * `y(var)` and variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RSolveValue.md">RSolveValue
   *      documentation</a>
   */
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
   * RuntimeAttributes - is an option for `Compile` which gives the attributes the compiled function
   * is evaluated with.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RuntimeAttributes.md">RuntimeAttributes
   *      documentation</a>
   */
  public final static IBuiltInSymbol RuntimeAttributes =
      S.initFinalSymbol("RuntimeAttributes", ID.RuntimeAttributes);

  /**
   * RuntimeOptions - is an option for `Compile` which sets how the compiled function behaves while
   * it runs.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/RuntimeOptions.md">RuntimeOptions
   *      documentation</a>
   */
  public final static IBuiltInSymbol RuntimeOptions =
      S.initFinalSymbol("RuntimeOptions", ID.RuntimeOptions);

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

  /**
   * Saturday(x) - TODO describe `Saturday`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Saturday.md">Saturday
   *      documentation</a>
   */
  public final static IBuiltInSymbol Saturday = S.initFinalSymbol("Saturday", ID.Saturday);

  /**
   * Save("path-to-filename", expression) - if the file system is enabled, export the
   * `FullDefinition` of the `expression` to the "path-to-filename" file. The saved file can be
   * imported with `Get`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Save.md">Save
   *      documentation</a>
   */
  public final static IBuiltInSymbol Save = S.initFinalSymbol("Save", ID.Save);

  /**
   * SaveDefinitions(x) - TODO describe `SaveDefinitions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SaveDefinitions.md">SaveDefinitions
   *      documentation</a>
   */
  public final static IBuiltInSymbol SaveDefinitions =
      S.initFinalSymbol("SaveDefinitions", ID.SaveDefinitions);

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
   * ScalingTransform({s1, s2, ...}) - gives a `TransformationFunction` that scales by the factor
   * `s1` along the first coordinate axis, by `s2` along the second and so on.
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
   * ScientificNotationThreshold(x) - TODO describe `ScientificNotationThreshold`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ScientificNotationThreshold.md">ScientificNotationThreshold
   *      documentation</a>
   */
  public final static IBuiltInSymbol ScientificNotationThreshold =
      S.initFinalSymbol("ScientificNotationThreshold", ID.ScientificNotationThreshold);

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

  /**
   * SechDistribution(x) - TODO describe `SechDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SechDistribution.md">SechDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol SechDistribution =
      S.initFinalSymbol("SechDistribution", ID.SechDistribution);

  public final static IBuiltInSymbol Second = S.initFinalSymbol("Second", ID.Second);

  /**
   * SectorChart(x) - TODO describe `SectorChart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SectorChart.md">SectorChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol SectorChart = S.initFinalSymbol("SectorChart", ID.SectorChart);

  /**
   * SectorOrigin(x) - TODO describe `SectorOrigin`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SectorOrigin.md">SectorOrigin
   *      documentation</a>
   */
  public final static IBuiltInSymbol SectorOrigin =
      S.initFinalSymbol("SectorOrigin", ID.SectorOrigin);

  /**
   * SectorSpacing(x) - TODO describe `SectorSpacing`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SectorSpacing.md">SectorSpacing
   *      documentation</a>
   */
  public final static IBuiltInSymbol SectorSpacing =
      S.initFinalSymbol("SectorSpacing", ID.SectorSpacing);

  public final static IBuiltInSymbol SeedRandom = S.initFinalSymbol("SeedRandom", ID.SeedRandom);

  /**
   * Segmented(x) - TODO describe `Segmented`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Segmented.md">Segmented
   *      documentation</a>
   */
  public final static IBuiltInSymbol Segmented = S.initFinalSymbol("Segmented", ID.Segmented);

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
   * SelectComponents(x) - TODO describe `SelectComponents`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SelectComponents.md">SelectComponents
   *      documentation</a>
   */
  public final static IBuiltInSymbol SelectComponents =
      S.initFinalSymbol("SelectComponents", ID.SelectComponents);

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
   * SeparateBoundaries(x) - TODO describe `SeparateBoundaries`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SeparateBoundaries.md">SeparateBoundaries
   *      documentation</a>
   */
  public final static IBuiltInSymbol SeparateBoundaries =
      S.initFinalSymbol("SeparateBoundaries", ID.SeparateBoundaries);

  /**
   * Sequence[x1, x2, ...] - represents a sequence of arguments to a function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sequence.md">Sequence
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sequence = S.initFinalSymbol("Sequence", ID.Sequence);

  /**
   * SequenceAlignment(x) - TODO describe `SequenceAlignment`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SequenceAlignment.md">SequenceAlignment
   *      documentation</a>
   */
  public final static IBuiltInSymbol SequenceAlignment =
      S.initFinalSymbol("SequenceAlignment", ID.SequenceAlignment);

  public final static IBuiltInSymbol SequenceCases =
      S.initFinalSymbol("SequenceCases", ID.SequenceCases);

  public final static IBuiltInSymbol SequenceCount =
      S.initFinalSymbol("SequenceCount", ID.SequenceCount);

  /**
   * SequenceHold - is an attribute specifying that in all arguments of a function the `Sequence`
   * expressions shouldn't be flattened out.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SequenceHold.md">SequenceHold
   *      documentation</a>
   */
  public final static IBuiltInSymbol SequenceHold =
      S.initFinalSymbol("SequenceHold", ID.SequenceHold);

  public final static IBuiltInSymbol SequencePosition =
      S.initFinalSymbol("SequencePosition", ID.SequencePosition);

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

  public final static IBuiltInSymbol SeriesTermGoal =
      S.initFinalSymbol("SeriesTermGoal", ID.SeriesTermGoal);

  /**
   * Set(expr, value) - evaluates `value` and assigns it to `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Set.md">Set
   *      documentation</a>
   */
  public final static IBuiltInSymbol Set = S.initFinalSymbol("Set", ID.Set);

  /**
   * SetAlphaChannel(x) - TODO describe `SetAlphaChannel`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SetAlphaChannel.md">SetAlphaChannel
   *      documentation</a>
   */
  public final static IBuiltInSymbol SetAlphaChannel =
      S.initFinalSymbol("SetAlphaChannel", ID.SetAlphaChannel);

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
   * Setter(x) - TODO describe `Setter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Setter.md">Setter
   *      documentation</a>
   */
  public final static IBuiltInSymbol Setter = S.initFinalSymbol("Setter", ID.Setter);

  /**
   * SetterBar(x) - TODO describe `SetterBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SetterBar.md">SetterBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol SetterBar = S.initFinalSymbol("SetterBar", ID.SetterBar);

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
   * Sharpen(x) - TODO describe `Sharpen`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sharpen.md">Sharpen
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sharpen = S.initFinalSymbol("Sharpen", ID.Sharpen);

  /**
   * Sharpening(x) - TODO describe `Sharpening`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sharpening.md">Sharpening
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sharpening = S.initFinalSymbol("Sharpening", ID.Sharpening);

  /**
   * ShearingTransform(phi, u, n) - gives a `TransformationFunction` that shears by the angle `phi`
   * in the direction of the vector `u`, normal to the vector `n` and leaves the origin fixed.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ShearingTransform.md">ShearingTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol ShearingTransform =
      S.initFinalSymbol("ShearingTransform", ID.ShearingTransform);

  public final static IBuiltInSymbol ShiftRegisterSequence =
      S.initFinalSymbol("ShiftRegisterSequence", ID.ShiftRegisterSequence);

  public final static IBuiltInSymbol Short = S.initFinalSymbol("Short", ID.Short);

  public final static IBuiltInSymbol ShortDownArrow =
      S.initFinalSymbol("ShortDownArrow", ID.ShortDownArrow);

  public final static IBuiltInSymbol Shortest = S.initFinalSymbol("Shortest", ID.Shortest);

  public final static IBuiltInSymbol ShortestCurveDistance =
      S.initFinalSymbol("ShortestCurveDistance", ID.ShortestCurveDistance);

  public final static IBuiltInSymbol ShortLeftArrow =
      S.initFinalSymbol("ShortLeftArrow", ID.ShortLeftArrow);

  public final static IBuiltInSymbol ShortRightArrow =
      S.initFinalSymbol("ShortRightArrow", ID.ShortRightArrow);

  public final static IBuiltInSymbol ShortUpArrow =
      S.initFinalSymbol("ShortUpArrow", ID.ShortUpArrow);

  public final static IBuiltInSymbol Show = S.initFinalSymbol("Show", ID.Show);

  /**
   * ShrinkingDelay(x) - TODO describe `ShrinkingDelay`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ShrinkingDelay.md">ShrinkingDelay
   *      documentation</a>
   */
  public final static IBuiltInSymbol ShrinkingDelay =
      S.initFinalSymbol("ShrinkingDelay", ID.ShrinkingDelay);

  /**
   * SiderealTime(x) - TODO describe `SiderealTime`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SiderealTime.md">SiderealTime
   *      documentation</a>
   */
  public final static IBuiltInSymbol SiderealTime =
      S.initFinalSymbol("SiderealTime", ID.SiderealTime);

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

  public final static IBuiltInSymbol SignedRegionDistance =
      S.initFinalSymbol("SignedRegionDistance", ID.SignedRegionDistance);

  /**
   * SignPadding(x) - TODO describe `SignPadding`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SignPadding.md">SignPadding
   *      documentation</a>
   */
  public final static IBuiltInSymbol SignPadding = S.initFinalSymbol("SignPadding", ID.SignPadding);

  /**
   * SimilarityRules(x) - TODO describe `SimilarityRules`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SimilarityRules.md">SimilarityRules
   *      documentation</a>
   */
  public final static IBuiltInSymbol SimilarityRules =
      S.initFinalSymbol("SimilarityRules", ID.SimilarityRules);

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
   * SinghMaddalaDistribution(x) - TODO describe `SinghMaddalaDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SinghMaddalaDistribution.md">SinghMaddalaDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol SinghMaddalaDistribution =
      S.initFinalSymbol("SinghMaddalaDistribution", ID.SinghMaddalaDistribution);

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
  /**
   * SiderealTime(date) - returns the Greenwich mean sidereal time at `date`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SiderealTime.md">SiderealTime
   *      documentation</a>
   */
  // public final static IBuiltInSymbol SiderealTime =
  // S.initFinalSymbol("SiderealTime", ID.SiderealTime);

  public final static IBuiltInSymbol Sinh = S.initFinalSymbol("Sinh", ID.Sinh);

  /**
   * SinhIntegral(expr) - returns the hyperbolic sine integral of `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SinhIntegral.md">SinhIntegral
   *      documentation</a>
   */
  public final static IBuiltInSymbol SinhIntegral =
      S.initFinalSymbol("SinhIntegral", ID.SinhIntegral);

  /**
   * SinIntegral(expr) - returns the sine integral of `expr`.
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

  public final static IBuiltInSymbol Skeleton = S.initFinalSymbol("Skeleton", ID.Skeleton);

  /**
   * SkeletonTransform(x) - TODO describe `SkeletonTransform`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SkeletonTransform.md">SkeletonTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol SkeletonTransform =
      S.initFinalSymbol("SkeletonTransform", ID.SkeletonTransform);

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
   * Slider(x) - TODO describe `Slider`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Slider.md">Slider
   *      documentation</a>
   */
  public final static IBuiltInSymbol Slider = S.initFinalSymbol("Slider", ID.Slider);

  /**
   * Slider2D(x) - TODO describe `Slider2D`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Slider2D.md">Slider2D
   *      documentation</a>
   */
  public final static IBuiltInSymbol Slider2D = S.initFinalSymbol("Slider2D", ID.Slider2D);

  /**
   * # - is a short-hand for `#1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Slot.md">Slot
   *      documentation</a>
   */
  public final static IBuiltInSymbol Slot = S.initFinalSymbol("Slot", ID.Slot);

  public final static IBuiltInSymbol SlotAbsent = S.initFinalSymbol("SlotAbsent", ID.SlotAbsent);

  public final static IBuiltInSymbol SlotNumber = S.initFinalSymbol("SlotNumber", ID.SlotNumber);

  /**
   * ## - is the sequence of arguments supplied to a pure function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SlotSequence.md">SlotSequence
   *      documentation</a>
   */
  public final static IBuiltInSymbol SlotSequence =
      S.initFinalSymbol("SlotSequence", ID.SlotSequence);

  public final static IBuiltInSymbol SlotSequenceNumber =
      S.initFinalSymbol("SlotSequenceNumber", ID.SlotSequenceNumber);

  public final static IBuiltInSymbol Small = S.initFinalSymbol("Small", ID.Small);

  public final static IBuiltInSymbol SmallCircle = S.initFinalSymbol("SmallCircle", ID.SmallCircle);

  public final static IBuiltInSymbol SmithDecomposition =
      S.initFinalSymbol("SmithDecomposition", ID.SmithDecomposition);

  /**
   * SmithWatermanSimilarity(x) - TODO describe `SmithWatermanSimilarity`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SmithWatermanSimilarity.md">SmithWatermanSimilarity
   *      documentation</a>
   */
  public final static IBuiltInSymbol SmithWatermanSimilarity =
      S.initFinalSymbol("SmithWatermanSimilarity", ID.SmithWatermanSimilarity);

  /**
   * SmoothDensityHistogram(x) - TODO describe `SmoothDensityHistogram`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SmoothDensityHistogram.md">SmoothDensityHistogram
   *      documentation</a>
   */
  public final static IBuiltInSymbol SmoothDensityHistogram =
      S.initFinalSymbol("SmoothDensityHistogram", ID.SmoothDensityHistogram);

  /**
   * SmoothHistogram(x) - TODO describe `SmoothHistogram`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SmoothHistogram.md">SmoothHistogram
   *      documentation</a>
   */
  public final static IBuiltInSymbol SmoothHistogram =
      S.initFinalSymbol("SmoothHistogram", ID.SmoothHistogram);

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
   * SolarEclipse(x) - TODO describe `SolarEclipse`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SolarEclipse.md">SolarEclipse
   *      documentation</a>
   */
  public final static IBuiltInSymbol SolarEclipse =
      S.initFinalSymbol("SolarEclipse", ID.SolarEclipse);

  /**
   * SolarTime(x) - TODO describe `SolarTime`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SolarTime.md">SolarTime
   *      documentation</a>
   */
  public final static IBuiltInSymbol SolarTime = S.initFinalSymbol("SolarTime", ID.SolarTime);

  /**
   * Solve(equations, vars) - attempts to solve `equations` for the variables `vars`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Solve.md">Solve
   *      documentation</a>
   */
  public final static IBuiltInSymbol Solve = S.initFinalSymbol("Solve", ID.Solve);

  public final static IBuiltInSymbol SolveAlways = S.initFinalSymbol("SolveAlways", ID.SolveAlways);

  /**
   * SolveValues(equations, vars) - attempts to solve `equations` for the variables `vars` and
   * returns a list of the values of the variables.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SolveValues.md">SolveValues
   *      documentation</a>
   */
  public final static IBuiltInSymbol SolveValues = S.initFinalSymbol("SolveValues", ID.SolveValues);

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
   * Spacer(x) - TODO describe `Spacer`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Spacer.md">Spacer
   *      documentation</a>
   */
  public final static IBuiltInSymbol Spacer = S.initFinalSymbol("Spacer", ID.Spacer);

  /**
   * Spacings(x) - TODO describe `Spacings`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Spacings.md">Spacings
   *      documentation</a>
   */
  public final static IBuiltInSymbol Spacings = S.initFinalSymbol("Spacings", ID.Spacings);

  /**
   * Span - is the head of span ranges like `1;;3`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Span.md">Span
   *      documentation</a>
   */
  public final static IBuiltInSymbol Span = S.initFinalSymbol("Span", ID.Span);

  public final static IBuiltInSymbol SpanFromAbove =
      S.initFinalSymbol("SpanFromAbove", ID.SpanFromAbove);

  public final static IBuiltInSymbol SpanFromBoth =
      S.initFinalSymbol("SpanFromBoth", ID.SpanFromBoth);

  public final static IBuiltInSymbol SpanFromLeft =
      S.initFinalSymbol("SpanFromLeft", ID.SpanFromLeft);

  /**
   * SparseArray(nestedList) - create a sparse array from a `nestedList` structure.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SparseArray.md">SparseArray
   *      documentation</a>
   */
  public final static IBuiltInSymbol SparseArray = S.initFinalSymbol("SparseArray", ID.SparseArray);

  /**
   * SparseArrayQ(expr) - return `True` if `expr` is a sparse array.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SparseArrayQ.md">SparseArrayQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SparseArrayQ =
      S.initFinalSymbol("SparseArrayQ", ID.SparseArrayQ);

  public final static IBuiltInSymbol SpearmanRho = S.initFinalSymbol("SpearmanRho", ID.SpearmanRho);

  /**
   * SpecialsFreeQ(expr) - returns `True` if `expr` does not contain the symbols `DirectedInfinity`
   * or `Indeterminate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SpecialsFreeQ.md">SpecialsFreeQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SpecialsFreeQ =
      S.initFinalSymbol("SpecialsFreeQ", ID.SpecialsFreeQ);

  public final static IBuiltInSymbol SpectrogramArray =
      S.initFinalSymbol("SpectrogramArray", ID.SpectrogramArray);

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

  /**
   * SphericalHarmonicY(l, m, theta, phi) - returns the spherical harmonic function `Y_l^m(theta,
   * phi)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SphericalHarmonicY.md">SphericalHarmonicY
   *      documentation</a>
   */
  public final static IBuiltInSymbol SphericalHarmonicY =
      S.initFinalSymbol("SphericalHarmonicY", ID.SphericalHarmonicY);

  public final static IBuiltInSymbol SphericalPlot3D =
      S.initFinalSymbol("SphericalPlot3D", ID.SphericalPlot3D);

  /**
   * SphericalRegion(x) - TODO describe `SphericalRegion`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SphericalRegion.md">SphericalRegion
   *      documentation</a>
   */
  public final static IBuiltInSymbol SphericalRegion =
      S.initFinalSymbol("SphericalRegion", ID.SphericalRegion);

  /**
   * SphericalShell(x) - TODO describe `SphericalShell`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SphericalShell.md">SphericalShell
   *      documentation</a>
   */
  public final static IBuiltInSymbol SphericalShell =
      S.initFinalSymbol("SphericalShell", ID.SphericalShell);

  /**
   * Splice(list-of-elements) - the `list-of-elements` will automatically be converted into a
   * `Sequence` of elements.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Splice.md">Splice
   *      documentation</a>
   */
  public final static IBuiltInSymbol Splice = S.initFinalSymbol("Splice", ID.Splice);

  public final static IBuiltInSymbol SplineClosed =
      S.initFinalSymbol("SplineClosed", ID.SplineClosed);

  public final static IBuiltInSymbol SplineDegree =
      S.initFinalSymbol("SplineDegree", ID.SplineDegree);

  public final static IBuiltInSymbol SplineKnots = S.initFinalSymbol("SplineKnots", ID.SplineKnots);

  public final static IBuiltInSymbol SplineWeights =
      S.initFinalSymbol("SplineWeights", ID.SplineWeights);

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

  public final static IBuiltInSymbol Square = S.initFinalSymbol("Square", ID.Square);

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

  public final static IBuiltInSymbol SquareIntersection =
      S.initFinalSymbol("SquareIntersection", ID.SquareIntersection);

  /**
   * SquareMatrixQ(m) - returns `True` if `m` is a square matrix.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquareMatrixQ.md">SquareMatrixQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SquareMatrixQ =
      S.initFinalSymbol("SquareMatrixQ", ID.SquareMatrixQ);

  /**
   * SquaresR(k, intNumber) - counts the numbers of the representation of `intNumber` as sum of
   * `x^2` terms which occur `k` times.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquaresR.md">SquaresR
   *      documentation</a>
   */
  public final static IBuiltInSymbol SquaresR = S.initFinalSymbol("SquaresR", ID.SquaresR);

  public final static IBuiltInSymbol SquareSubset =
      S.initFinalSymbol("SquareSubset", ID.SquareSubset);

  public final static IBuiltInSymbol SquareSubsetEqual =
      S.initFinalSymbol("SquareSubsetEqual", ID.SquareSubsetEqual);

  public final static IBuiltInSymbol SquareSuperset =
      S.initFinalSymbol("SquareSuperset", ID.SquareSuperset);

  public final static IBuiltInSymbol SquareSupersetEqual =
      S.initFinalSymbol("SquareSupersetEqual", ID.SquareSupersetEqual);

  public final static IBuiltInSymbol SquareUnion = S.initFinalSymbol("SquareUnion", ID.SquareUnion);

  /**
   * SquareWave(x) - TODO describe `SquareWave`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SquareWave.md">SquareWave
   *      documentation</a>
   */
  public final static IBuiltInSymbol SquareWave = S.initFinalSymbol("SquareWave", ID.SquareWave);

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
   * StackedDateListPlot(x) - TODO describe `StackedDateListPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StackedDateListPlot.md">StackedDateListPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol StackedDateListPlot =
      S.initFinalSymbol("StackedDateListPlot", ID.StackedDateListPlot);

  /**
   * StackedListPlot(x) - TODO describe `StackedListPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StackedListPlot.md">StackedListPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol StackedListPlot =
      S.initFinalSymbol("StackedListPlot", ID.StackedListPlot);

  /**
   * StadiumShape(x) - TODO describe `StadiumShape`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StadiumShape.md">StadiumShape
   *      documentation</a>
   */
  public final static IBuiltInSymbol StadiumShape =
      S.initFinalSymbol("StadiumShape", ID.StadiumShape);

  /**
   * StandardBlue(x) - TODO describe `StandardBlue`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardBlue.md">StandardBlue
   *      documentation</a>
   */
  public final static IBuiltInSymbol StandardBlue =
      S.initFinalSymbol("StandardBlue", ID.StandardBlue);

  /**
   * StandardBrown(x) - TODO describe `StandardBrown`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardBrown.md">StandardBrown
   *      documentation</a>
   */
  public final static IBuiltInSymbol StandardBrown =
      S.initFinalSymbol("StandardBrown", ID.StandardBrown);

  /**
   * StandardCyan(x) - TODO describe `StandardCyan`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardCyan.md">StandardCyan
   *      documentation</a>
   */
  public final static IBuiltInSymbol StandardCyan =
      S.initFinalSymbol("StandardCyan", ID.StandardCyan);

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

  /**
   * StandardDeviationFilter(x) - TODO describe `StandardDeviationFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardDeviationFilter.md">StandardDeviationFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol StandardDeviationFilter =
      S.initFinalSymbol("StandardDeviationFilter", ID.StandardDeviationFilter);

  public final static IBuiltInSymbol StandardForm =
      S.initFinalSymbol("StandardForm", ID.StandardForm);

  /**
   * StandardGray(x) - TODO describe `StandardGray`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardGray.md">StandardGray
   *      documentation</a>
   */
  public final static IBuiltInSymbol StandardGray =
      S.initFinalSymbol("StandardGray", ID.StandardGray);

  /**
   * StandardGreen(x) - TODO describe `StandardGreen`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardGreen.md">StandardGreen
   *      documentation</a>
   */
  public final static IBuiltInSymbol StandardGreen =
      S.initFinalSymbol("StandardGreen", ID.StandardGreen);

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
   * Standardized(x) - TODO describe `Standardized`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Standardized.md">Standardized
   *      documentation</a>
   */
  public final static IBuiltInSymbol Standardized =
      S.initFinalSymbol("Standardized", ID.Standardized);

  /**
   * StandardMagenta(x) - TODO describe `StandardMagenta`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardMagenta.md">StandardMagenta
   *      documentation</a>
   */
  public final static IBuiltInSymbol StandardMagenta =
      S.initFinalSymbol("StandardMagenta", ID.StandardMagenta);

  /**
   * StandardOrange(x) - TODO describe `StandardOrange`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardOrange.md">StandardOrange
   *      documentation</a>
   */
  public final static IBuiltInSymbol StandardOrange =
      S.initFinalSymbol("StandardOrange", ID.StandardOrange);

  /**
   * StandardPink(x) - TODO describe `StandardPink`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardPink.md">StandardPink
   *      documentation</a>
   */
  public final static IBuiltInSymbol StandardPink =
      S.initFinalSymbol("StandardPink", ID.StandardPink);

  /**
   * StandardPurple(x) - TODO describe `StandardPurple`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardPurple.md">StandardPurple
   *      documentation</a>
   */
  public final static IBuiltInSymbol StandardPurple =
      S.initFinalSymbol("StandardPurple", ID.StandardPurple);

  /**
   * StandardRed(x) - TODO describe `StandardRed`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardRed.md">StandardRed
   *      documentation</a>
   */
  public final static IBuiltInSymbol StandardRed = S.initFinalSymbol("StandardRed", ID.StandardRed);

  /**
   * StandardYellow(x) - TODO describe `StandardYellow`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StandardYellow.md">StandardYellow
   *      documentation</a>
   */
  public final static IBuiltInSymbol StandardYellow =
      S.initFinalSymbol("StandardYellow", ID.StandardYellow);

  public final static IBuiltInSymbol Star = S.initFinalSymbol("Star", ID.Star);

  /**
   * StarData(x) - TODO describe `StarData`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StarData.md">StarData
   *      documentation</a>
   */
  public final static IBuiltInSymbol StarData = S.initFinalSymbol("StarData", ID.StarData);

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

  public final static IBuiltInSymbol StatusArea = S.initFinalSymbol("StatusArea", ID.StatusArea);

  public final static IBuiltInSymbol StereochemistryElements =
      S.initFinalSymbol("StereochemistryElements", ID.StereochemistryElements);

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

  /**
   * StreamColorFunction(x) - TODO describe `StreamColorFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StreamColorFunction.md">StreamColorFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol StreamColorFunction =
      S.initFinalSymbol("StreamColorFunction", ID.StreamColorFunction);

  /**
   * StreamColorFunctionScaling(x) - TODO describe `StreamColorFunctionScaling`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StreamColorFunctionScaling.md">StreamColorFunctionScaling
   *      documentation</a>
   */
  public final static IBuiltInSymbol StreamColorFunctionScaling =
      S.initFinalSymbol("StreamColorFunctionScaling", ID.StreamColorFunctionScaling);

  /**
   * StreamDensityPlot(x) - TODO describe `StreamDensityPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StreamDensityPlot.md">StreamDensityPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol StreamDensityPlot =
      S.initFinalSymbol("StreamDensityPlot", ID.StreamDensityPlot);

  public final static IBuiltInSymbol StreamPlot = S.initFinalSymbol("StreamPlot", ID.StreamPlot);

  /**
   * StreamPoints(x) - TODO describe `StreamPoints`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StreamPoints.md">StreamPoints
   *      documentation</a>
   */
  public final static IBuiltInSymbol StreamPoints =
      S.initFinalSymbol("StreamPoints", ID.StreamPoints);

  /**
   * StreamScale(x) - TODO describe `StreamScale`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StreamScale.md">StreamScale
   *      documentation</a>
   */
  public final static IBuiltInSymbol StreamScale = S.initFinalSymbol("StreamScale", ID.StreamScale);

  /**
   * StreamStyle(x) - TODO describe `StreamStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/StreamStyle.md">StreamStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol StreamStyle = S.initFinalSymbol("StreamStyle", ID.StreamStyle);

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

  public final static IBuiltInSymbol StringStartsQ =
      S.initFinalSymbol("StringStartsQ", ID.StringStartsQ);

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

  public final static IBuiltInSymbol Subgraph = S.initFinalSymbol("Subgraph", ID.Subgraph);

  /**
   * Subresultants(polynomial1, polynomial2, var) - computes the subresultants of the polynomials
   * `polynomial1` and `polynomial2` with respect to the variable `var`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subresultants.md">Subresultants
   *      documentation</a>
   */
  public final static IBuiltInSymbol Subresultants =
      S.initFinalSymbol("Subresultants", ID.Subresultants);

  public final static IBuiltInSymbol Subscript = S.initFinalSymbol("Subscript", ID.Subscript);

  public final static IBuiltInSymbol SubscriptBox =
      S.initFinalSymbol("SubscriptBox", ID.SubscriptBox);

  public final static IBuiltInSymbol Subsequences =
      S.initFinalSymbol("Subsequences", ID.Subsequences);

  public final static IBuiltInSymbol Subset = S.initFinalSymbol("Subset", ID.Subset);

  /**
   * SubsetCases(list, sublist -> rhs) - returns a list of the right-hand-side `rhs` for the
   * matching sublist pattern expression `sublist` in `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubsetCases.md">SubsetCases
   *      documentation</a>
   */
  public final static IBuiltInSymbol SubsetCases = S.initFinalSymbol("SubsetCases", ID.SubsetCases);

  public final static IBuiltInSymbol SubsetCount = S.initFinalSymbol("SubsetCount", ID.SubsetCount);

  public final static IBuiltInSymbol SubsetEqual = S.initFinalSymbol("SubsetEqual", ID.SubsetEqual);

  public final static IBuiltInSymbol SubsetPosition =
      S.initFinalSymbol("SubsetPosition", ID.SubsetPosition);

  /**
   * SubsetQ(set1, set2) - returns `True` if `set2` is a subset of `set1`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubsetQ.md">SubsetQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol SubsetQ = S.initFinalSymbol("SubsetQ", ID.SubsetQ);

  /**
   * SubsetReplace(list, sublist -> rhs) - replaces the sublist pattern expression `sublist` in
   * `list` with the right-hand-side `rhs`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SubsetReplace.md">SubsetReplace
   *      documentation</a>
   */
  public final static IBuiltInSymbol SubsetReplace =
      S.initFinalSymbol("SubsetReplace", ID.SubsetReplace);

  /**
   * Subsets(list) - finds a list of all possible subsets of `list`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Subsets.md">Subsets
   *      documentation</a>
   */
  public final static IBuiltInSymbol Subsets = S.initFinalSymbol("Subsets", ID.Subsets);

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

  public final static IBuiltInSymbol Succeeds = S.initFinalSymbol("Succeeds", ID.Succeeds);

  public final static IBuiltInSymbol SucceedsEqual =
      S.initFinalSymbol("SucceedsEqual", ID.SucceedsEqual);

  public final static IBuiltInSymbol SucceedsSlantEqual =
      S.initFinalSymbol("SucceedsSlantEqual", ID.SucceedsSlantEqual);

  public final static IBuiltInSymbol SucceedsTilde =
      S.initFinalSymbol("SucceedsTilde", ID.SucceedsTilde);

  public final static IBuiltInSymbol SuchThat = S.initFinalSymbol("SuchThat", ID.SuchThat);

  /**
   * SudokuSolve(matrix) - In Sudoku, the objective is to fill a 9 × 9 `matrix` with digits so that
   * each column, each row, and each of the nine 3 × 3 subgrids that compose the grid (also called
   * "boxes", "blocks", or "regions") contains all of the digits from 1 to 9. Every input which is
   * not a number between 1 and 9 will be replaced with the correct number to fully solve the
   * sudoku.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SudokuSolve.md">SudokuSolve
   *      documentation</a>
   */
  public final static IBuiltInSymbol SudokuSolve = S.initFinalSymbol("SudokuSolve", ID.SudokuSolve);

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

  /**
   * Sunday(x) - TODO describe `Sunday`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sunday.md">Sunday
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sunday = S.initFinalSymbol("Sunday", ID.Sunday);

  /**
   * SunPosition(x) - TODO describe `SunPosition`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SunPosition.md">SunPosition
   *      documentation</a>
   */
  public final static IBuiltInSymbol SunPosition = S.initFinalSymbol("SunPosition", ID.SunPosition);

  /**
   * Sunrise(x) - TODO describe `Sunrise`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sunrise.md">Sunrise
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sunrise = S.initFinalSymbol("Sunrise", ID.Sunrise);

  /**
   * Sunset(x) - TODO describe `Sunset`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Sunset.md">Sunset
   *      documentation</a>
   */
  public final static IBuiltInSymbol Sunset = S.initFinalSymbol("Sunset", ID.Sunset);

  public final static IBuiltInSymbol SuperDagger = S.initFinalSymbol("SuperDagger", ID.SuperDagger);

  public final static IBuiltInSymbol Superscript = S.initFinalSymbol("Superscript", ID.Superscript);

  public final static IBuiltInSymbol SuperscriptBox =
      S.initFinalSymbol("SuperscriptBox", ID.SuperscriptBox);

  public final static IBuiltInSymbol Superset = S.initFinalSymbol("Superset", ID.Superset);

  public final static IBuiltInSymbol SupersetEqual =
      S.initFinalSymbol("SupersetEqual", ID.SupersetEqual);

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
   * SuzukiDistribution(x) - TODO describe `SuzukiDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SuzukiDistribution.md">SuzukiDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol SuzukiDistribution =
      S.initFinalSymbol("SuzukiDistribution", ID.SuzukiDistribution);

  /**
   * SwatchLegend(x) - TODO describe `SwatchLegend`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SwatchLegend.md">SwatchLegend
   *      documentation</a>
   */
  public final static IBuiltInSymbol SwatchLegend =
      S.initFinalSymbol("SwatchLegend", ID.SwatchLegend);

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

  public final static IBuiltInSymbol SymbolicDeltaProductArray =
      S.initFinalSymbol("SymbolicDeltaProductArray", ID.SymbolicDeltaProductArray);

  public final static IBuiltInSymbol SymbolicIdentityArray =
      S.initFinalSymbol("SymbolicIdentityArray", ID.SymbolicIdentityArray);

  public final static IBuiltInSymbol SymbolicOnesArray =
      S.initFinalSymbol("SymbolicOnesArray", ID.SymbolicOnesArray);

  public final static IBuiltInSymbol SymbolicZerosArray =
      S.initFinalSymbol("SymbolicZerosArray", ID.SymbolicZerosArray);

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

  public final static IBuiltInSymbol SymmetricPolynomial =
      S.initFinalSymbol("SymmetricPolynomial", ID.SymmetricPolynomial);

  public final static IBuiltInSymbol SymmetricReduction =
      S.initFinalSymbol("SymmetricReduction", ID.SymmetricReduction);

  public final static IBuiltInSymbol Symmetrize = S.initFinalSymbol("Symmetrize", ID.Symmetrize);

  /**
   * SynchronousInitialization(x) - TODO describe `SynchronousInitialization`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SynchronousInitialization.md">SynchronousInitialization
   *      documentation</a>
   */
  public final static IBuiltInSymbol SynchronousInitialization =
      S.initFinalSymbol("SynchronousInitialization", ID.SynchronousInitialization);

  /**
   * SynchronousUpdating(x) - TODO describe `SynchronousUpdating`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/SynchronousUpdating.md">SynchronousUpdating
   *      documentation</a>
   */
  public final static IBuiltInSymbol SynchronousUpdating =
      S.initFinalSymbol("SynchronousUpdating", ID.SynchronousUpdating);

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
   * TableView(x) - TODO describe `TableView`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TableView.md">TableView
   *      documentation</a>
   */
  public final static IBuiltInSymbol TableView = S.initFinalSymbol("TableView", ID.TableView);

  /**
   * TabView(x) - TODO describe `TabView`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TabView.md">TabView
   *      documentation</a>
   */
  public final static IBuiltInSymbol TabView = S.initFinalSymbol("TabView", ID.TabView);

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

  public final static IBuiltInSymbol TagUnset = S.initFinalSymbol("TagUnset", ID.TagUnset);

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

  public final static IBuiltInSymbol TakeList = S.initFinalSymbol("TakeList", ID.TakeList);

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
   * TargetUnits(x) - TODO describe `TargetUnits`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TargetUnits.md">TargetUnits
   *      documentation</a>
   */
  public final static IBuiltInSymbol TargetUnits = S.initFinalSymbol("TargetUnits", ID.TargetUnits);

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

  public final static IBuiltInSymbol TensorContract =
      S.initFinalSymbol("TensorContract", ID.TensorContract);

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

  public final static IBuiltInSymbol TensorTranspose =
      S.initFinalSymbol("TensorTranspose", ID.TensorTranspose);

  public final static IBuiltInSymbol TensorWedge = S.initFinalSymbol("TensorWedge", ID.TensorWedge);

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

  /**
   * Texture(x) - TODO describe `Texture`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Texture.md">Texture
   *      documentation</a>
   */
  public final static IBuiltInSymbol Texture = S.initFinalSymbol("Texture", ID.Texture);

  /**
   * TextureCoordinateFunction(x) - TODO describe `TextureCoordinateFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TextureCoordinateFunction.md">TextureCoordinateFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol TextureCoordinateFunction =
      S.initFinalSymbol("TextureCoordinateFunction", ID.TextureCoordinateFunction);

  /**
   * TextureCoordinateScaling(x) - TODO describe `TextureCoordinateScaling`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TextureCoordinateScaling.md">TextureCoordinateScaling
   *      documentation</a>
   */
  public final static IBuiltInSymbol TextureCoordinateScaling =
      S.initFinalSymbol("TextureCoordinateScaling", ID.TextureCoordinateScaling);

  public final static IBuiltInSymbol Therefore = S.initFinalSymbol("Therefore", ID.Therefore);

  public final static IBuiltInSymbol Thick = S.initFinalSymbol("Thick", ID.Thick);

  public final static IBuiltInSymbol Thickness = S.initFinalSymbol("Thickness", ID.Thickness);

  public final static IBuiltInSymbol Thin = S.initFinalSymbol("Thin", ID.Thin);

  /**
   * Thinning(x) - TODO describe `Thinning`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Thinning.md">Thinning
   *      documentation</a>
   */
  public final static IBuiltInSymbol Thinning = S.initFinalSymbol("Thinning", ID.Thinning);

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

  /**
   * Thumbnail(x) - TODO describe `Thumbnail`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Thumbnail.md">Thumbnail
   *      documentation</a>
   */
  public final static IBuiltInSymbol Thumbnail = S.initFinalSymbol("Thumbnail", ID.Thumbnail);

  /**
   * Thursday(x) - TODO describe `Thursday`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Thursday.md">Thursday
   *      documentation</a>
   */
  public final static IBuiltInSymbol Thursday = S.initFinalSymbol("Thursday", ID.Thursday);

  public final static IBuiltInSymbol Ticks = S.initFinalSymbol("Ticks", ID.Ticks);

  public final static IBuiltInSymbol TicksStyle = S.initFinalSymbol("TicksStyle", ID.TicksStyle);

  public final static IBuiltInSymbol Tilde = S.initFinalSymbol("Tilde", ID.Tilde);

  public final static IBuiltInSymbol TildeEqual = S.initFinalSymbol("TildeEqual", ID.TildeEqual);

  public final static IBuiltInSymbol TildeFullEqual =
      S.initFinalSymbol("TildeFullEqual", ID.TildeFullEqual);

  public final static IBuiltInSymbol TildeTilde = S.initFinalSymbol("TildeTilde", ID.TildeTilde);

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

  /**
   * TimeDirection(x) - TODO describe `TimeDirection`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeDirection.md">TimeDirection
   *      documentation</a>
   */
  public final static IBuiltInSymbol TimeDirection =
      S.initFinalSymbol("TimeDirection", ID.TimeDirection);

  /**
   * TimelinePlot(x) - TODO describe `TimelinePlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimelinePlot.md">TimelinePlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol TimelinePlot =
      S.initFinalSymbol("TimelinePlot", ID.TimelinePlot);

  /**
   * TimeObject() - returns the current time
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeObject.md">TimeObject
   *      documentation</a>
   */
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
   * TimeSystemConvert(date, "system") - returns the reading of `date` in another astronomical time
   * system.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeSystemConvert.md">TimeSystemConvert
   *      documentation</a>
   */
  // public final static IBuiltInSymbol TimeSystemConvert =
  // S.initFinalSymbol("TimeSystemConvert", ID.TimeSystemConvert);

  /**
   * TimeSystem(x) - TODO describe `TimeSystem`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeSystem.md">TimeSystem
   *      documentation</a>
   */
  public final static IBuiltInSymbol TimeSystem = S.initFinalSymbol("TimeSystem", ID.TimeSystem);

  /**
   * TimeSystemConvert(x) - TODO describe `TimeSystemConvert`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeSystemConvert.md">TimeSystemConvert
   *      documentation</a>
   */
  public final static IBuiltInSymbol TimeSystemConvert =
      S.initFinalSymbol("TimeSystemConvert", ID.TimeSystemConvert);

  /**
   * TimeValue(p, i, n) - returns a time value calculation.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeValue.md">TimeValue
   *      documentation</a>
   */
  public final static IBuiltInSymbol TimeValue = S.initFinalSymbol("TimeValue", ID.TimeValue);

  /**
   * TimeZone(x) - TODO describe `TimeZone`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeZone.md">TimeZone
   *      documentation</a>
   */
  public final static IBuiltInSymbol TimeZone = S.initFinalSymbol("TimeZone", ID.TimeZone);

  /**
   * TimeZoneConvert(x) - TODO describe `TimeZoneConvert`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeZoneConvert.md">TimeZoneConvert
   *      documentation</a>
   */
  public final static IBuiltInSymbol TimeZoneConvert =
      S.initFinalSymbol("TimeZoneConvert", ID.TimeZoneConvert);

  /**
   * TimeZoneOffset(x) - TODO describe `TimeZoneOffset`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TimeZoneOffset.md">TimeZoneOffset
   *      documentation</a>
   */
  public final static IBuiltInSymbol TimeZoneOffset =
      S.initFinalSymbol("TimeZoneOffset", ID.TimeZoneOffset);

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

  /**
   * ToDataset(x) - TODO describe `ToDataset`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ToDataset.md">ToDataset
   *      documentation</a>
   */
  public final static IBuiltInSymbol ToDataset = S.initFinalSymbol("ToDataset", ID.ToDataset);

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
   * ToExpression("string") - interprets a given string as Symja input.
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

  /**
   * Toggler(x) - TODO describe `Toggler`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Toggler.md">Toggler
   *      documentation</a>
   */
  public final static IBuiltInSymbol Toggler = S.initFinalSymbol("Toggler", ID.Toggler);

  /**
   * TogglerBar(x) - TODO describe `TogglerBar`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TogglerBar.md">TogglerBar
   *      documentation</a>
   */
  public final static IBuiltInSymbol TogglerBar = S.initFinalSymbol("TogglerBar", ID.TogglerBar);

  public final static IBuiltInSymbol ToIntervalData =
      S.initFinalSymbol("ToIntervalData", ID.ToIntervalData);

  public final static IBuiltInSymbol Tolerance = S.initFinalSymbol("Tolerance", ID.Tolerance);

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

  public final static IBuiltInSymbol Tooltip = S.initFinalSymbol("Tooltip", ID.Tooltip);

  public final static IBuiltInSymbol Top = S.initFinalSymbol("Top", ID.Top);

  /**
   * TopHatTransform(x) - TODO describe `TopHatTransform`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TopHatTransform.md">TopHatTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol TopHatTransform =
      S.initFinalSymbol("TopHatTransform", ID.TopHatTransform);

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

  public final static IBuiltInSymbol TopologicalSort =
      S.initFinalSymbol("TopologicalSort", ID.TopologicalSort);

  public final static IBuiltInSymbol ToRadicals = S.initFinalSymbol("ToRadicals", ID.ToRadicals);

  public final static IBuiltInSymbol TortoiseShellBracket =
      S.initFinalSymbol("TortoiseShellBracket", ID.TortoiseShellBracket);

  /**
   * Torus(x) - TODO describe `Torus`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Torus.md">Torus
   *      documentation</a>
   */
  public final static IBuiltInSymbol Torus = S.initFinalSymbol("Torus", ID.Torus);

  /**
   * TorusGraph(x) - TODO describe `TorusGraph`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TorusGraph.md">TorusGraph
   *      documentation</a>
   */
  public final static IBuiltInSymbol TorusGraph = S.initFinalSymbol("TorusGraph", ID.TorusGraph);

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
   * TotalVariationFilter(x) - TODO describe `TotalVariationFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TotalVariationFilter.md">TotalVariationFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol TotalVariationFilter =
      S.initFinalSymbol("TotalVariationFilter", ID.TotalVariationFilter);

  /**
   * TouchscreenAutoZoom(x) - TODO describe `TouchscreenAutoZoom`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TouchscreenAutoZoom.md">TouchscreenAutoZoom
   *      documentation</a>
   */
  public final static IBuiltInSymbol TouchscreenAutoZoom =
      S.initFinalSymbol("TouchscreenAutoZoom", ID.TouchscreenAutoZoom);

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

  /**
   * TrackedSymbols(x) - TODO describe `TrackedSymbols`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrackedSymbols.md">TrackedSymbols
   *      documentation</a>
   */
  public final static IBuiltInSymbol TrackedSymbols =
      S.initFinalSymbol("TrackedSymbols", ID.TrackedSymbols);

  /**
   * TradingChart(x) - TODO describe `TradingChart`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TradingChart.md">TradingChart
   *      documentation</a>
   */
  public final static IBuiltInSymbol TradingChart =
      S.initFinalSymbol("TradingChart", ID.TradingChart);

  public final static IBuiltInSymbol TraditionalForm =
      S.initFinalSymbol("TraditionalForm", ID.TraditionalForm);

  /**
   * TransformationClass(x) - TODO describe `TransformationClass`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TransformationClass.md">TransformationClass
   *      documentation</a>
   */
  public final static IBuiltInSymbol TransformationClass =
      S.initFinalSymbol("TransformationClass", ID.TransformationClass);

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
   * TransformedDistribution(x) - TODO describe `TransformedDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TransformedDistribution.md">TransformedDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol TransformedDistribution =
      S.initFinalSymbol("TransformedDistribution", ID.TransformedDistribution);

  /**
   * TransformedRegion(x) - TODO describe `TransformedRegion`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TransformedRegion.md">TransformedRegion
   *      documentation</a>
   */
  public final static IBuiltInSymbol TransformedRegion =
      S.initFinalSymbol("TransformedRegion", ID.TransformedRegion);

  public final static IBuiltInSymbol TransitiveClosure =
      S.initFinalSymbol("TransitiveClosure", ID.TransitiveClosure);

  /**
   * Translate(x) - TODO describe `Translate`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Translate.md">Translate
   *      documentation</a>
   */
  public final static IBuiltInSymbol Translate = S.initFinalSymbol("Translate", ID.Translate);

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

  public final static IBuiltInSymbol Transparent = S.initFinalSymbol("Transparent", ID.Transparent);

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

  public final static IBuiltInSymbol TreeGraph = S.initFinalSymbol("TreeGraph", ID.TreeGraph);

  public final static IBuiltInSymbol TreeGraphQ = S.initFinalSymbol("TreeGraphQ", ID.TreeGraphQ);

  /**
   * TreePlot(graph-expr) - create a tree plot from the given graph expression `graph-expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TreePlot.md">TreePlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol TreePlot = S.initFinalSymbol("TreePlot", ID.TreePlot);

  public final static IBuiltInSymbol Triangle = S.initFinalSymbol("Triangle", ID.Triangle);

  /**
   * TriangleCenter(tri, type) - returns the coordinates of the center of the specified `type` for
   * the triangle `tri`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TriangleCenter.md">TriangleCenter
   *      documentation</a>
   */
  public final static IBuiltInSymbol TriangleCenter =
      S.initFinalSymbol("TriangleCenter", ID.TriangleCenter);

  /**
   * TriangleConstruct(tri, type) - returns the geometric construct of the specified `type` for the
   * triangle `tri`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TriangleConstruct.md">TriangleConstruct
   *      documentation</a>
   */
  public final static IBuiltInSymbol TriangleConstruct =
      S.initFinalSymbol("TriangleConstruct", ID.TriangleConstruct);

  /**
   * TriangleMeasurement(tri, type) - returns the value of the measurement `type` for the triangle
   * `tri`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TriangleMeasurement.md">TriangleMeasurement
   *      documentation</a>
   */
  public final static IBuiltInSymbol TriangleMeasurement =
      S.initFinalSymbol("TriangleMeasurement", ID.TriangleMeasurement);

  /**
   * TriangleWave(x) - TODO describe `TriangleWave`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TriangleWave.md">TriangleWave
   *      documentation</a>
   */
  public final static IBuiltInSymbol TriangleWave =
      S.initFinalSymbol("TriangleWave", ID.TriangleWave);

  /**
   * TriangularDistribution(x) - TODO describe `TriangularDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TriangularDistribution.md">TriangularDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol TriangularDistribution =
      S.initFinalSymbol("TriangularDistribution", ID.TriangularDistribution);

  public final static IBuiltInSymbol Trig = S.initFinalSymbol("Trig", ID.Trig);

  /**
   * TrigExpand(expr) - expands out trigonometric expressions in `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TrigExpand.md">TrigExpand
   *      documentation</a>
   */
  public final static IBuiltInSymbol TrigExpand = S.initFinalSymbol("TrigExpand", ID.TrigExpand);

  public final static IBuiltInSymbol TrigFactor = S.initFinalSymbol("TrigFactor", ID.TrigFactor);

  /**
   * Trigger(x) - TODO describe `Trigger`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Trigger.md">Trigger
   *      documentation</a>
   */
  public final static IBuiltInSymbol Trigger = S.initFinalSymbol("Trigger", ID.Trigger);

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
   * TruncatedDistribution({min, max}, distribution) - a distribution which is conditioned on the
   * interval `min <= x <= max`.
   *
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/TruncatedDistribution.md">TruncatedDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol TruncatedDistribution =
      S.initFinalSymbol("TruncatedDistribution", ID.TruncatedDistribution);

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

  /**
   * Tuesday(x) - TODO describe `Tuesday`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Tuesday.md">Tuesday
   *      documentation</a>
   */
  public final static IBuiltInSymbol Tuesday = S.initFinalSymbol("Tuesday", ID.Tuesday);

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

  public final static IBuiltInSymbol UnaryMinusPlus =
      S.initFinalSymbol("UnaryMinusPlus", ID.UnaryMinusPlus);

  public final static IBuiltInSymbol UnaryPlus = S.initFinalSymbol("UnaryPlus", ID.UnaryPlus);

  public final static IBuiltInSymbol UnaryPlusMinus =
      S.initFinalSymbol("UnaryPlusMinus", ID.UnaryPlusMinus);

  /**
   * Uncompress(string) - an expression compressed by the `Compress` function can be fully
   * reconstructed using the `Uncompress` function.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Uncompress.md">Uncompress
   *      documentation</a>
   */
  public final static IBuiltInSymbol Uncompress = S.initFinalSymbol("Uncompress", ID.Uncompress);

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

  /**
   * Underlined(x) - TODO describe `Underlined`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Underlined.md">Underlined
   *      documentation</a>
   */
  public final static IBuiltInSymbol Underlined = S.initFinalSymbol("Underlined", ID.Underlined);

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
   * UniformSumDistribution(x) - TODO describe `UniformSumDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UniformSumDistribution.md">UniformSumDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol UniformSumDistribution =
      S.initFinalSymbol("UniformSumDistribution", ID.UniformSumDistribution);

  /**
   * Union(set1, set2) - get the union set from `set1` and `set2`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Union.md">Union
   *      documentation</a>
   */
  public final static IBuiltInSymbol Union = S.initFinalSymbol("Union", ID.Union);

  public final static IBuiltInSymbol UnionPlus = S.initFinalSymbol("UnionPlus", ID.UnionPlus);

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

  public final static IBuiltInSymbol UnitBox = S.initFinalSymbol("UnitBox", ID.UnitBox);

  /**
   * UnitConvert(quantity) - convert the `quantity` to the base unit
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitConvert.md">UnitConvert
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnitConvert = S.initFinalSymbol("UnitConvert", ID.UnitConvert);

  /**
   * UnitDimensions(x) - TODO describe `UnitDimensions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitDimensions.md">UnitDimensions
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnitDimensions =
      S.initFinalSymbol("UnitDimensions", ID.UnitDimensions);

  /**
   * Unitize(expr) - maps a non-zero `expr` to `1`, and a zero `expr` to `0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unitize.md">Unitize
   *      documentation</a>
   */
  public final static IBuiltInSymbol Unitize = S.initFinalSymbol("Unitize", ID.Unitize);

  /**
   * UnitSimplify(x) - TODO describe `UnitSimplify`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitSimplify.md">UnitSimplify
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnitSimplify =
      S.initFinalSymbol("UnitSimplify", ID.UnitSimplify);

  /**
   * UnitStep(expr) - returns `0`, if `expr` is less than `0` and returns `1`, if `expr` is greater
   * equal than `0`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitStep.md">UnitStep
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnitStep = S.initFinalSymbol("UnitStep", ID.UnitStep);

  /**
   * UnitSystem(x) - TODO describe `UnitSystem`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnitSystem.md">UnitSystem
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnitSystem = S.initFinalSymbol("UnitSystem", ID.UnitSystem);

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

  /**
   * UnityDimensions(x) - TODO describe `UnityDimensions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnityDimensions.md">UnityDimensions
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnityDimensions =
      S.initFinalSymbol("UnityDimensions", ID.UnityDimensions);

  public final static IBuiltInSymbol UniverseAge = S.initFinalSymbol("UniverseAge", ID.UniverseAge);

  /**
   * UnixTime(x) - TODO describe `UnixTime`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnixTime.md">UnixTime
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnixTime = S.initFinalSymbol("UnixTime", ID.UnixTime);

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
   * UnsavedVariables(x) - TODO describe `UnsavedVariables`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UnsavedVariables.md">UnsavedVariables
   *      documentation</a>
   */
  public final static IBuiltInSymbol UnsavedVariables =
      S.initFinalSymbol("UnsavedVariables", ID.UnsavedVariables);

  /**
   * Unset(expr) - removes any definitions belonging to the left-hand-side `expr`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Unset.md">Unset
   *      documentation</a>
   */
  public final static IBuiltInSymbol Unset = S.initFinalSymbol("Unset", ID.Unset);

  /**
   * UntrackedVariables(x) - TODO describe `UntrackedVariables`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UntrackedVariables.md">UntrackedVariables
   *      documentation</a>
   */
  public final static IBuiltInSymbol UntrackedVariables =
      S.initFinalSymbol("UntrackedVariables", ID.UntrackedVariables);

  public final static IBuiltInSymbol UpArrow = S.initFinalSymbol("UpArrow", ID.UpArrow);

  public final static IBuiltInSymbol UpArrowBar = S.initFinalSymbol("UpArrowBar", ID.UpArrowBar);

  public final static IBuiltInSymbol UpArrowDownArrow =
      S.initFinalSymbol("UpArrowDownArrow", ID.UpArrowDownArrow);

  /**
   * UpdateInterval(x) - TODO describe `UpdateInterval`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpdateInterval.md">UpdateInterval
   *      documentation</a>
   */
  public final static IBuiltInSymbol UpdateInterval =
      S.initFinalSymbol("UpdateInterval", ID.UpdateInterval);

  public final static IBuiltInSymbol UpDownArrow = S.initFinalSymbol("UpDownArrow", ID.UpDownArrow);

  public final static IBuiltInSymbol UpEquilibrium =
      S.initFinalSymbol("UpEquilibrium", ID.UpEquilibrium);

  /**
   * UpperCaseQ(str) - is `True` if the given `str` is a string which only contains upper case
   * characters.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpperCaseQ.md">UpperCaseQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol UpperCaseQ = S.initFinalSymbol("UpperCaseQ", ID.UpperCaseQ);

  public final static IBuiltInSymbol UpperLeftArrow =
      S.initFinalSymbol("UpperLeftArrow", ID.UpperLeftArrow);

  public final static IBuiltInSymbol UpperRightArrow =
      S.initFinalSymbol("UpperRightArrow", ID.UpperRightArrow);

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

  public final static IBuiltInSymbol UpTee = S.initFinalSymbol("UpTee", ID.UpTee);

  public final static IBuiltInSymbol UpTeeArrow = S.initFinalSymbol("UpTeeArrow", ID.UpTeeArrow);

  public final static IBuiltInSymbol UpTo = S.initFinalSymbol("UpTo", ID.UpTo);

  /**
   * UpValues(symbol) - prints the up-value rules associated with `symbol`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/UpValues.md">UpValues
   *      documentation</a>
   */
  public final static IBuiltInSymbol UpValues = S.initFinalSymbol("UpValues", ID.UpValues);

  /**
   * URLDecode(string) - the `URLDecode` function decodes a URL-encoded string, converting it back
   * to its original human-readable format. This is the inverse operation of `URLEncode`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/URLDecode.md">URLDecode
   *      documentation</a>
   */
  public final static IBuiltInSymbol URLDecode = S.initFinalSymbol("URLDecode", ID.URLDecode);

  /**
   * URLEncode(string) - the `URLEncode` function converts a string into a URL-encoded format,
   * making it safe for inclusion in URL query strings. This is the inverse operation of
   * `URLDecode`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/URLEncode.md">URLEncode
   *      documentation</a>
   */
  public final static IBuiltInSymbol URLEncode = S.initFinalSymbol("URLEncode", ID.URLEncode);

  public final static IBuiltInSymbol URLFetch = S.initFinalSymbol("URLFetch", ID.URLFetch);

  public final static IBuiltInSymbol UseTypeChecking =
      S.initFinalSymbol("UseTypeChecking", ID.UseTypeChecking);

  /**
   * ValenceErrorHandling(x) - TODO describe `ValenceErrorHandling`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ValenceErrorHandling.md">ValenceErrorHandling
   *      documentation</a>
   */
  public final static IBuiltInSymbol ValenceErrorHandling =
      S.initFinalSymbol("ValenceErrorHandling", ID.ValenceErrorHandling);

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

  /**
   * VectorAround(x) - TODO describe `VectorAround`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorAround.md">VectorAround
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorAround =
      S.initFinalSymbol("VectorAround", ID.VectorAround);

  /**
   * VectorAspectRatio(x) - TODO describe `VectorAspectRatio`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorAspectRatio.md">VectorAspectRatio
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorAspectRatio =
      S.initFinalSymbol("VectorAspectRatio", ID.VectorAspectRatio);

  /**
   * VectorColorFunction(x) - TODO describe `VectorColorFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorColorFunction.md">VectorColorFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorColorFunction =
      S.initFinalSymbol("VectorColorFunction", ID.VectorColorFunction);

  /**
   * VectorColorFunctionScaling(x) - TODO describe `VectorColorFunctionScaling`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorColorFunctionScaling.md">VectorColorFunctionScaling
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorColorFunctionScaling =
      S.initFinalSymbol("VectorColorFunctionScaling", ID.VectorColorFunctionScaling);

  /**
   * VectorDensityPlot(x) - TODO describe `VectorDensityPlot`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorDensityPlot.md">VectorDensityPlot
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorDensityPlot =
      S.initFinalSymbol("VectorDensityPlot", ID.VectorDensityPlot);

  /**
   * VectorGreater({vector1, vector2}) - the `VectorGreater` function is used to compare two
   * vectors, `vector1` and `vector2` recursively. It returns `True` if each corresponding element
   * of `vector1` is greater than the corresponding element of `vector2`, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorGreater.md">VectorGreater
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorGreater =
      S.initFinalSymbol("VectorGreater", ID.VectorGreater);

  /**
   * VectorGreaterEqual({vector1, vector2}) - the `VectorGreaterEqual` function is used to compare
   * two vectors, `vector1` and `vector2` recursively. It returns `True` if each corresponding
   * element of `vector1` is greater or equal than the corresponding element of `vector2`, and
   * `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorGreaterEqual.md">VectorGreaterEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorGreaterEqual =
      S.initFinalSymbol("VectorGreaterEqual", ID.VectorGreaterEqual);

  /**
   * VectorLess({vector1, vector2}) - the `VectorLess` function is used to compare two vectors,
   * `vector1` and `vector2` recursively. It returns `True` if each corresponding element of
   * `vector1` is less than the corresponding element of `vector2`, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorLess.md">VectorLess
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorLess = S.initFinalSymbol("VectorLess", ID.VectorLess);

  /**
   * VectorLessEqual({vector1, vector2}) - the `VectorLessEqual` function is used to compare two
   * vectors, `vector1` and `vector2` recursively. It returns `True` if each corresponding element
   * of `vector1` is less or equal than the corresponding element of `vector2`, and `False`
   * otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorLessEqual.md">VectorLessEqual
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorLessEqual =
      S.initFinalSymbol("VectorLessEqual", ID.VectorLessEqual);

  /**
   * VectorMarkers(x) - TODO describe `VectorMarkers`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorMarkers.md">VectorMarkers
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorMarkers =
      S.initFinalSymbol("VectorMarkers", ID.VectorMarkers);

  public final static IBuiltInSymbol VectorPlot = S.initFinalSymbol("VectorPlot", ID.VectorPlot);

  /**
   * VectorPoints(x) - TODO describe `VectorPoints`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorPoints.md">VectorPoints
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorPoints =
      S.initFinalSymbol("VectorPoints", ID.VectorPoints);

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
   * VectorScale(x) - TODO describe `VectorScale`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorScale.md">VectorScale
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorScale = S.initFinalSymbol("VectorScale", ID.VectorScale);

  /**
   * VectorSizes(x) - TODO describe `VectorSizes`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorSizes.md">VectorSizes
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorSizes = S.initFinalSymbol("VectorSizes", ID.VectorSizes);

  /**
   * VectorStyle(x) - TODO describe `VectorStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VectorStyle.md">VectorStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol VectorStyle = S.initFinalSymbol("VectorStyle", ID.VectorStyle);

  public final static IBuiltInSymbol VectorSymbol =
      S.initFinalSymbol("VectorSymbol", ID.VectorSymbol);

  public final static IBuiltInSymbol Vee = S.initFinalSymbol("Vee", ID.Vee);

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
   * VerifySolutions(x) - TODO describe `VerifySolutions`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VerifySolutions.md">VerifySolutions
   *      documentation</a>
   */
  public final static IBuiltInSymbol VerifySolutions =
      S.initFinalSymbol("VerifySolutions", ID.VerifySolutions);

  public final static IBuiltInSymbol VertexAdd = S.initFinalSymbol("VertexAdd", ID.VertexAdd);

  /**
   * VertexChromaticNumber(graph) - gives the smallest number of colors that can be assigned to the
   * vertices of `graph` such that no two adjacent vertices have the same color.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexChromaticNumber.md">VertexChromaticNumber
   *      documentation</a>
   */
  /**
   * VertexChromaticNumber(graph) - gives the smallest number of colors that can be assigned to the
   * vertices of `graph` such that no two adjacent vertices have the same color.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexChromaticNumber.md">VertexChromaticNumber
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexChromaticNumber =
      S.initFinalSymbol("VertexChromaticNumber", ID.VertexChromaticNumber);

  public final static IBuiltInSymbol VertexColors =
      S.initFinalSymbol("VertexColors", ID.VertexColors);

  public final static IBuiltInSymbol VertexContract =
      S.initFinalSymbol("VertexContract", ID.VertexContract);

  public final static IBuiltInSymbol VertexCoordinates =
      S.initFinalSymbol("VertexCoordinates", ID.VertexCoordinates);

  /**
   * VertexCount(graph) - return the number of vertices of the `graph`
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexCount.md">VertexCount
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexCount = S.initFinalSymbol("VertexCount", ID.VertexCount);

  /**
   * VertexCoverQ(graph, vertices) - yields `True` if the vertex list `vertices` is a vertex
   * cover of `graph`, and `False` otherwise.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexCoverQ.md">VertexCoverQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexCoverQ =
      S.initFinalSymbol("VertexCoverQ", ID.VertexCoverQ);

  public final static IBuiltInSymbol VertexDegree =
      S.initFinalSymbol("VertexDegree", ID.VertexDegree);

  public final static IBuiltInSymbol VertexDelete =
      S.initFinalSymbol("VertexDelete", ID.VertexDelete);

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

  public final static IBuiltInSymbol VertexInDegree =
      S.initFinalSymbol("VertexInDegree", ID.VertexInDegree);

  public final static IBuiltInSymbol VertexLabels =
      S.initFinalSymbol("VertexLabels", ID.VertexLabels);

  /**
   * VertexLabelStyle(x) - TODO describe `VertexLabelStyle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexLabelStyle.md">VertexLabelStyle
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexLabelStyle =
      S.initFinalSymbol("VertexLabelStyle", ID.VertexLabelStyle);

  /**
   * VertexList(graph) - convert the `graph` into a list of vertices.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexList.md">VertexList
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexList = S.initFinalSymbol("VertexList", ID.VertexList);

  public final static IBuiltInSymbol VertexNormals =
      S.initFinalSymbol("VertexNormals", ID.VertexNormals);

  public final static IBuiltInSymbol VertexOutDegree =
      S.initFinalSymbol("VertexOutDegree", ID.VertexOutDegree);

  /**
   * VertexQ(graph, vertex) - test if `vertex` is a vertex in the `graph` object.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexQ.md">VertexQ
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexQ = S.initFinalSymbol("VertexQ", ID.VertexQ);

  /**
   * VertexShape(x) - TODO describe `VertexShape`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexShape.md">VertexShape
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexShape = S.initFinalSymbol("VertexShape", ID.VertexShape);

  public final static IBuiltInSymbol VertexShapeFunction =
      S.initFinalSymbol("VertexShapeFunction", ID.VertexShapeFunction);

  public final static IBuiltInSymbol VertexSize = S.initFinalSymbol("VertexSize", ID.VertexSize);

  public final static IBuiltInSymbol VertexStyle = S.initFinalSymbol("VertexStyle", ID.VertexStyle);

  /**
   * VertexTextureCoordinates(x) - TODO describe `VertexTextureCoordinates`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexTextureCoordinates.md">VertexTextureCoordinates
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexTextureCoordinates =
      S.initFinalSymbol("VertexTextureCoordinates", ID.VertexTextureCoordinates);

  /**
   * VertexWeight(x) - TODO describe `VertexWeight`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VertexWeight.md">VertexWeight
   *      documentation</a>
   */
  public final static IBuiltInSymbol VertexWeight =
      S.initFinalSymbol("VertexWeight", ID.VertexWeight);

  public final static IBuiltInSymbol VerticalBar = S.initFinalSymbol("VerticalBar", ID.VerticalBar);

  public final static IBuiltInSymbol VerticalSeparator =
      S.initFinalSymbol("VerticalSeparator", ID.VerticalSeparator);

  /**
   * VerticalSlider(x) - TODO describe `VerticalSlider`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VerticalSlider.md">VerticalSlider
   *      documentation</a>
   */
  public final static IBuiltInSymbol VerticalSlider =
      S.initFinalSymbol("VerticalSlider", ID.VerticalSlider);

  public final static IBuiltInSymbol VerticalTilde =
      S.initFinalSymbol("VerticalTilde", ID.VerticalTilde);

  /**
   * ViewAngle(x) - TODO describe `ViewAngle`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ViewAngle.md">ViewAngle
   *      documentation</a>
   */
  public final static IBuiltInSymbol ViewAngle = S.initFinalSymbol("ViewAngle", ID.ViewAngle);

  /**
   * ViewCenter(x) - TODO describe `ViewCenter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ViewCenter.md">ViewCenter
   *      documentation</a>
   */
  public final static IBuiltInSymbol ViewCenter = S.initFinalSymbol("ViewCenter", ID.ViewCenter);

  /**
   * ViewMatrix(x) - TODO describe `ViewMatrix`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ViewMatrix.md">ViewMatrix
   *      documentation</a>
   */
  public final static IBuiltInSymbol ViewMatrix = S.initFinalSymbol("ViewMatrix", ID.ViewMatrix);

  public final static IBuiltInSymbol ViewPoint = S.initFinalSymbol("ViewPoint", ID.ViewPoint);

  /**
   * ViewProjection(x) - TODO describe `ViewProjection`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ViewProjection.md">ViewProjection
   *      documentation</a>
   */
  public final static IBuiltInSymbol ViewProjection =
      S.initFinalSymbol("ViewProjection", ID.ViewProjection);

  /**
   * ViewRange(x) - TODO describe `ViewRange`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ViewRange.md">ViewRange
   *      documentation</a>
   */
  public final static IBuiltInSymbol ViewRange = S.initFinalSymbol("ViewRange", ID.ViewRange);

  /**
   * ViewVector(x) - TODO describe `ViewVector`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ViewVector.md">ViewVector
   *      documentation</a>
   */
  public final static IBuiltInSymbol ViewVector = S.initFinalSymbol("ViewVector", ID.ViewVector);

  /**
   * ViewVertical(x) - TODO describe `ViewVertical`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ViewVertical.md">ViewVertical
   *      documentation</a>
   */
  public final static IBuiltInSymbol ViewVertical =
      S.initFinalSymbol("ViewVertical", ID.ViewVertical);

  public final static IBuiltInSymbol Volume = S.initFinalSymbol("Volume", ID.Volume);

  /**
   * VonMisesDistribution(x) - TODO describe `VonMisesDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VonMisesDistribution.md">VonMisesDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol VonMisesDistribution =
      S.initFinalSymbol("VonMisesDistribution", ID.VonMisesDistribution);

  /**
   * VoronoiMesh(x) - TODO describe `VoronoiMesh`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/VoronoiMesh.md">VoronoiMesh
   *      documentation</a>
   */
  public final static IBuiltInSymbol VoronoiMesh = S.initFinalSymbol("VoronoiMesh", ID.VoronoiMesh);

  /**
   * WaringYuleDistribution(x) - TODO describe `WaringYuleDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WaringYuleDistribution.md">WaringYuleDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol WaringYuleDistribution =
      S.initFinalSymbol("WaringYuleDistribution", ID.WaringYuleDistribution);

  /**
   * WatershedComponents(x) - TODO describe `WatershedComponents`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WatershedComponents.md">WatershedComponents
   *      documentation</a>
   */
  public final static IBuiltInSymbol WatershedComponents =
      S.initFinalSymbol("WatershedComponents", ID.WatershedComponents);

  public final static IBuiltInSymbol WeaklyConnectedGraphQ =
      S.initFinalSymbol("WeaklyConnectedGraphQ", ID.WeaklyConnectedGraphQ);

  public final static IBuiltInSymbol WeberE = S.initFinalSymbol("WeberE", ID.WeberE);

  /**
   * WebImageSearch(x) - TODO describe `WebImageSearch`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WebImageSearch.md">WebImageSearch
   *      documentation</a>
   */
  public final static IBuiltInSymbol WebImageSearch =
      S.initFinalSymbol("WebImageSearch", ID.WebImageSearch);

  /**
   * WebSearch(x) - TODO describe `WebSearch`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WebSearch.md">WebSearch
   *      documentation</a>
   */
  public final static IBuiltInSymbol WebSearch = S.initFinalSymbol("WebSearch", ID.WebSearch);

  public final static IBuiltInSymbol Wedge = S.initFinalSymbol("Wedge", ID.Wedge);

  /**
   * Wednesday(x) - TODO describe `Wednesday`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Wednesday.md">Wednesday
   *      documentation</a>
   */
  public final static IBuiltInSymbol Wednesday = S.initFinalSymbol("Wednesday", ID.Wednesday);

  /**
   * Weekend(x) - TODO describe `Weekend`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Weekend.md">Weekend
   *      documentation</a>
   */
  public final static IBuiltInSymbol Weekend = S.initFinalSymbol("Weekend", ID.Weekend);

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

  /**
   * WheelGraph(order) - in graph theory, a wheel graph is a graph formed by connecting a single
   * universal vertex to all vertices of a cycle.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WheelGraph.md">WheelGraph
   *      documentation</a>
   */
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

  /**
   * White - RGB color value for the color white
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/White.md">White
   *      documentation</a>
   */
  public final static IBuiltInSymbol White = S.initFinalSymbol("White", ID.White);

  public final static IBuiltInSymbol WhiteCornerBracket =
      S.initFinalSymbol("WhiteCornerBracket", ID.WhiteCornerBracket);

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

  /**
   * WhittakerM(a, b, z) - returns the Whittaker function `M_a,b(z)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WhittakerM.md">WhittakerM
   *      documentation</a>
   */
  public final static IBuiltInSymbol WhittakerM = S.initFinalSymbol("WhittakerM", ID.WhittakerM);

  /**
   * WhittakerW(a, b, z) - returns the Whittaker function `W_a,b(z)`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WhittakerW.md">WhittakerW
   *      documentation</a>
   */
  public final static IBuiltInSymbol WhittakerW = S.initFinalSymbol("WhittakerW", ID.WhittakerW);

  /**
   * WienerFilter(x) - TODO describe `WienerFilter`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WienerFilter.md">WienerFilter
   *      documentation</a>
   */
  public final static IBuiltInSymbol WienerFilter =
      S.initFinalSymbol("WienerFilter", ID.WienerFilter);

  /**
   * WignerD({j,m1,m2},a,b,c) - the Wigner D-function returns the matrix element of a rotation
   * operator.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WignerD.md">WignerD
   *      documentation</a>
   */
  public final static IBuiltInSymbol WignerD = S.initFinalSymbol("WignerD", ID.WignerD);

  /**
   * WignerSemicircleDistribution(x) - TODO describe `WignerSemicircleDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WignerSemicircleDistribution.md">WignerSemicircleDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol WignerSemicircleDistribution =
      S.initFinalSymbol("WignerSemicircleDistribution", ID.WignerSemicircleDistribution);

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

  public final static IBuiltInSymbol WordCloud = S.initFinalSymbol("WordCloud", ID.WordCloud);

  /**
   * WordOrientation(x) - TODO describe `WordOrientation`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WordOrientation.md">WordOrientation
   *      documentation</a>
   */
  public final static IBuiltInSymbol WordOrientation =
      S.initFinalSymbol("WordOrientation", ID.WordOrientation);

  /**
   * WordSelectionFunction(x) - TODO describe `WordSelectionFunction`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WordSelectionFunction.md">WordSelectionFunction
   *      documentation</a>
   */
  public final static IBuiltInSymbol WordSelectionFunction =
      S.initFinalSymbol("WordSelectionFunction", ID.WordSelectionFunction);

  public final static IBuiltInSymbol WordSeparators =
      S.initFinalSymbol("WordSeparators", ID.WordSeparators);

  /**
   * WordSpacings(x) - TODO describe `WordSpacings`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/WordSpacings.md">WordSpacings
   *      documentation</a>
   */
  public final static IBuiltInSymbol WordSpacings =
      S.initFinalSymbol("WordSpacings", ID.WordSpacings);

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

  /**
   * XYZColor(x) - TODO describe `XYZColor`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/XYZColor.md">XYZColor
   *      documentation</a>
   */
  public final static IBuiltInSymbol XYZColor = S.initFinalSymbol("XYZColor", ID.XYZColor);

  /**
   * Yellow - RGB color value for the color yellow
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Yellow.md">Yellow
   *      documentation</a>
   */
  public final static IBuiltInSymbol Yellow = S.initFinalSymbol("Yellow", ID.Yellow);

  /**
   * Yesterday(x) - TODO describe `Yesterday`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/Yesterday.md">Yesterday
   *      documentation</a>
   */
  public final static IBuiltInSymbol Yesterday = S.initFinalSymbol("Yesterday", ID.Yesterday);

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

  /**
   * ZernikeR(n,m,p) - returns the radial Zernike polynomial
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ZernikeR.md">ZernikeR
   *      documentation</a>
   */
  public final static IBuiltInSymbol ZernikeR = S.initFinalSymbol("ZernikeR", ID.ZernikeR);

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
   * ZetaZero(k) - represents the `k`-th zero of the Riemann zeta function on the critical line.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ZetaZero.md">ZetaZero
   *      documentation</a>
   */
  public final static IBuiltInSymbol ZetaZero = S.initFinalSymbol("ZetaZero", ID.ZetaZero);

  /**
   * ZipfDistribution(x) - TODO describe `ZipfDistribution`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ZipfDistribution.md">ZipfDistribution
   *      documentation</a>
   */
  public final static IBuiltInSymbol ZipfDistribution =
      S.initFinalSymbol("ZipfDistribution", ID.ZipfDistribution);

  /**
   * ZTransform(x,n,z) - returns the Z-Transform of `x`.
   * 
   * @see <a href=
   *      "https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/ZTransform.md">ZTransform
   *      documentation</a>
   */
  public final static IBuiltInSymbol ZTransform = S.initFinalSymbol("ZTransform", ID.ZTransform);


  // END_S_SYMBOLS

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
   * Formal symbols with visible Unicode "Combining Dot Below" (\u0323). These render as the letter
   * with a dot underneath (e.g., Ạ, ạ).
   */

  // --- Lowercase Formal Symbols (a-z) ---
  public static final ISymbol aF = initFinalHiddenSymbol("a\u0323");
  public static final ISymbol bF = initFinalHiddenSymbol("b\u0323");
  public static final ISymbol cF = initFinalHiddenSymbol("c\u0323");
  public static final ISymbol dF = initFinalHiddenSymbol("d\u0323");
  public static final ISymbol eF = initFinalHiddenSymbol("e\u0323");
  public static final ISymbol fF = initFinalHiddenSymbol("f\u0323");
  public static final ISymbol gF = initFinalHiddenSymbol("g\u0323");
  public static final ISymbol hF = initFinalHiddenSymbol("h\u0323");
  public static final ISymbol iF = initFinalHiddenSymbol("i\u0323");
  public static final ISymbol jF = initFinalHiddenSymbol("j\u0323");
  public static final ISymbol kF = initFinalHiddenSymbol("k\u0323");
  public static final ISymbol lF = initFinalHiddenSymbol("l\u0323");
  public static final ISymbol mF = initFinalHiddenSymbol("m\u0323");
  public static final ISymbol nF = initFinalHiddenSymbol("n\u0323");
  public static final ISymbol oF = initFinalHiddenSymbol("o\u0323");
  public static final ISymbol pF = initFinalHiddenSymbol("p\u0323");
  public static final ISymbol qF = initFinalHiddenSymbol("q\u0323");
  public static final ISymbol rF = initFinalHiddenSymbol("r\u0323");
  public static final ISymbol sF = initFinalHiddenSymbol("s\u0323");
  public static final ISymbol tF = initFinalHiddenSymbol("t\u0323");
  public static final ISymbol uF = initFinalHiddenSymbol("u\u0323");
  public static final ISymbol vF = initFinalHiddenSymbol("v\u0323");
  public static final ISymbol wF = initFinalHiddenSymbol("w\u0323");
  public static final ISymbol xF = initFinalHiddenSymbol("x\u0323");
  public static final ISymbol yF = initFinalHiddenSymbol("y\u0323");
  public static final ISymbol zF = initFinalHiddenSymbol("z\u0323");
  /**
   * Used to represent a formal parameter <code>LHS_HEAD</code> that will never be assigned a value.
   * Used for setting the left-hand-side in pattern-matching for <code>OptionValue(...)</code>
   */
  public static final ISymbol LHS_HEAD = initFinalHiddenSymbol("LHSHead");

  /**
   * Convert the symbolName to lower case (if <code>Config.PARSER_USE_LOWERCASE_SYMBOLS</code> is
   * set) and insert a new Symbol in the <code>PREDEFINED_SYMBOLS_MAP</code>. The symbol is created
   * using the given upper case string to use it as associated class name in package
   * org.matheclipse.core.reflection.system.
   *
   * @param symbolName the predefined symbol name in upper-case form
   * @param ordinal
   * @return
   */
  public static IBuiltInSymbol initFinalSymbol(final String symbolName, int ordinal) {
    final String str;
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      str = (symbolName.length() == 1) ? symbolName : symbolName.toLowerCase(Locale.US);
    } else {
      str = symbolName;
    }
    final IBuiltInSymbol temp = new BuiltInSymbol(str, ordinal);
    BUILT_IN_SYMBOLS[ordinal] = temp;
    org.matheclipse.core.expression.Context.SYSTEM.put(str, temp);
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
    final ISymbol symbol = new Symbol(symbolName, org.matheclipse.core.expression.Context.FORMAL);
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
