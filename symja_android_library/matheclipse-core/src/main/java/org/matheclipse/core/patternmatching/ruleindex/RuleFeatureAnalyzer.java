package org.matheclipse.core.patternmatching.ruleindex;

import java.util.HashSet;
import java.util.Set;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Collapse aware analysis of the left-hand-side of a pattern matching rule.
 *
 * <p>
 * For a left-hand-side <code>lhs</code> the analyzer determines the set of <i>required symbols</i>:
 * symbols which have to occur in <b>every</b> expression that can be matched by <code>lhs</code>,
 * either as the head of a sub-expression or as an atom. A symbol occurrence is counted with
 * {@link #symbolsOf(IExpr, Set)} on the expression side, so both sides use the same notion.
 *
 * <p>
 * The analysis must be an <b>under-approximation</b>: reporting too few required symbols only makes
 * a rule index less selective, reporting a symbol which the matcher does not actually require would
 * drop a matching rule and silently change results. Every case which cannot be decided therefore
 * contributes nothing.
 *
 * <p>
 * The two constructions which make a symbol <i>not</i> required, although it is present in the
 * left-hand-side, are:
 * <ul>
 * <li><b>vanishing arguments</b> - an argument with an associated default value (<code>x_.</code>,
 * <code>Optional(x_, v)</code>) or a pattern sequence can be absent from the matched expression, so
 * nothing below it is required. See {@link #canVanish(IExpr)}.</li>
 * <li><b>collapsing nodes</b> - if all but one argument of an {@link ISymbol#ONEIDENTITY} head can
 * vanish, the whole node matches expressions which do not contain that head at all. For example
 * <code>Power(x_, n_.)</code> matches the plain symbol <code>x</code> and <code>Times(u_., v_)</code>
 * matches a single factor, so neither <code>Power</code> nor <code>Times</code> is required. This
 * mirrors <code>PatternMatcher#matchDefaultArgumentsAST(...)</code>, which collapses the pattern
 * with <code>IAST#isOneIdentityAST1()</code> after the default values were filled in.</li>
 * </ul>
 *
 * <p>
 * Only the pattern part of {@link S#PatternTest} and {@link S#Condition} is analyzed. The test and
 * the condition are predicates applied to the match - their symbols (<code>IntegerQ</code>,
 * <code>FreeQ</code>, ...) do not occur in the matched expression.
 *
 * @see RuleFeatureIndex
 */
public final class RuleFeatureAnalyzer {

  /** Guards against pathologically deep left-hand-sides; deeper nodes contribute nothing. */
  private static final int MAX_DEPTH = 24;

  private RuleFeatureAnalyzer() {}

  /**
   * Determine the symbols which every expression matched by <code>lhs</code> must contain.
   *
   * @param lhs the left-hand-side of a pattern matching rule
   * @return the required symbols, an empty set if nothing could be derived
   */
  public static Set<ISymbol> requiredSymbols(IExpr lhs) {
    Set<ISymbol> result = new HashSet<ISymbol>();
    if (lhs != null) {
      collectRequired(lhs, result, 0);
    }
    return result;
  }

  /**
   * Collect all symbols occurring in <code>expr</code>, as the head of a sub-expression or as an
   * atom. Atoms contribute their head symbol, so a complex number contributes {@link S#Complex} and
   * an integer contributes {@link S#Integer}; this is what makes a required symbol like
   * <code>Complex</code> - from a left-hand-side such as <code>Complex(0, a_)*u_</code> - observable
   * on the expression side.
   *
   * @param expr the expression to scan
   * @param result collects the symbols found
   */
  public static void symbolsOf(IExpr expr, Set<ISymbol> result) {
    symbolsOf(expr, result, 0);
  }

  private static void symbolsOf(IExpr expr, Set<ISymbol> result, int depth) {
    if (depth > MAX_DEPTH) {
      return;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IExpr head = ast.head();
      if (head.isSymbol()) {
        result.add((ISymbol) head);
      } else {
        symbolsOf(head, result, depth + 1);
      }
      for (int i = 1; i < ast.size(); i++) {
        symbolsOf(ast.get(i), result, depth + 1);
      }
      return;
    }
    if (expr.isSymbol()) {
      result.add((ISymbol) expr);
      return;
    }
    IExpr head = expr.head();
    if (head.isSymbol()) {
      result.add((ISymbol) head);
    }
  }

  /**
   * Test whether an argument of a pattern can be absent from the matched expression, which makes
   * everything below it optional.
   */
  private static boolean canVanish(IExpr arg) {
    if (arg instanceof IPatternSequence) {
      // "__" needs at least one element, "___" none - do not distinguish, both are treated as
      // possibly absent which is the conservative direction
      return true;
    }
    if (arg.isPatternDefault() || arg.isOptional()) {
      return true;
    }
    if (arg.isAST()) {
      IAST ast = (IAST) arg;
      IExpr head = ast.head();
      if ((head == S.PatternTest || head == S.Condition || head == S.HoldPattern) && ast.size() > 1) {
        return canVanish(ast.arg1());
      }
      if (head == S.RepeatedNull || head == S.OptionsPattern) {
        return true;
      }
    }
    return false;
  }

  private static void collectRequired(IExpr pattern, Set<ISymbol> result, int depth) {
    if (depth > MAX_DEPTH) {
      return;
    }
    if (!pattern.isAST()) {
      if (pattern instanceof IPatternObject) {
        // "_", "x_", "x_Symbol", ... - the head test is a constraint on the matched expression but
        // not a symbol which must occur inside it
        return;
      }
      if (pattern.isSymbol()) {
        result.add((ISymbol) pattern);
        return;
      }
      IExpr head = pattern.head();
      if (head.isSymbol()) {
        result.add((ISymbol) head);
      }
      return;
    }

    IAST ast = (IAST) pattern;
    IExpr head = ast.head();

    if (head.isSymbol()) {
      ISymbol headSymbol = (ISymbol) head;
      if (headSymbol == S.HoldPattern || headSymbol == S.Verbatim) {
        if (ast.isAST1()) {
          collectRequired(ast.arg1(), result, depth + 1);
        }
        return;
      }
      if (headSymbol == S.PatternTest || headSymbol == S.Condition) {
        // only the pattern part describes the matched expression
        if (ast.size() > 1) {
          collectRequired(ast.arg1(), result, depth + 1);
        }
        return;
      }
      if (headSymbol == S.Optional) {
        // the whole node may be replaced by its default value
        return;
      }
      if (headSymbol == S.Alternatives) {
        collectAlternatives(ast, result, depth);
        return;
      }
      if (headSymbol == S.Pattern || headSymbol == S.Blank || headSymbol == S.BlankSequence
          || headSymbol == S.BlankNullSequence || headSymbol == S.Except || headSymbol == S.Repeated
          || headSymbol == S.RepeatedNull || headSymbol == S.OptionsPattern
          || headSymbol == S.Longest || headSymbol == S.Shortest) {
        // structure of the matched expression is not determined by these
        return;
      }

      if (headSymbol.hasOneIdentityAttribute()) {
        // a OneIdentity node collapses to its single remaining argument once the vanishing
        // arguments were replaced by their default values, so the head itself is not required
        IExpr single = null;
        int mandatory = 0;
        for (int i = 1; i < ast.size(); i++) {
          IExpr arg = ast.get(i);
          if (!canVanish(arg)) {
            mandatory++;
            single = arg;
          }
        }
        if (mandatory == 0) {
          return;
        }
        if (mandatory == 1) {
          collectRequired(single, result, depth + 1);
          return;
        }
      }
      result.add(headSymbol);
    } else if (head.isAST()) {
      // curried head, for example Derivative(1)[f_][x_]
      collectRequired(head, result, depth + 1);
    } else {
      // the head is a pattern object - the structure of the matched expression is unknown
      return;
    }

    for (int i = 1; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (!canVanish(arg)) {
        collectRequired(arg, result, depth + 1);
      }
    }
  }

  /**
   * Determine the head which the top node of every expression matched by <code>pattern</code> must
   * have.
   *
   * @return the head symbol, or <code>null</code> if it is not determined - which is also the
   *         answer for a node that may either collapse to its single argument or keep its head
   */
  public static ISymbol topHead(IExpr pattern) {
    return topHead(pattern, 0);
  }

  private static ISymbol topHead(IExpr pattern, int depth) {
    if (depth > MAX_DEPTH) {
      return null;
    }
    if (!pattern.isAST()) {
      if (pattern instanceof IPatternObject) {
        // "x_Symbol" and friends constrain the head of the matched expression
        IExpr headTest = ((IPatternObject) pattern).getHeadTest();
        return headTest != null && headTest.isSymbol() ? (ISymbol) headTest : null;
      }
      return null;
    }
    IAST ast = (IAST) pattern;
    IExpr head = ast.head();
    if (!head.isSymbol()) {
      return null;
    }
    ISymbol headSymbol = (ISymbol) head;
    if (headSymbol == S.HoldPattern || headSymbol == S.Verbatim) {
      return ast.isAST1() ? topHead(ast.arg1(), depth + 1) : null;
    }
    if (headSymbol == S.PatternTest || headSymbol == S.Condition) {
      return ast.size() > 1 ? topHead(ast.arg1(), depth + 1) : null;
    }
    if (headSymbol == S.Optional || headSymbol == S.Alternatives || headSymbol == S.Except
        || headSymbol == S.Pattern || headSymbol == S.Blank || headSymbol == S.BlankSequence
        || headSymbol == S.BlankNullSequence || headSymbol == S.Repeated
        || headSymbol == S.RepeatedNull || headSymbol == S.OptionsPattern
        || headSymbol == S.Longest || headSymbol == S.Shortest) {
      return null;
    }
    if (headSymbol.hasOneIdentityAttribute()) {
      int mandatory = 0;
      for (int i = 1; i < ast.size(); i++) {
        if (!canVanish(ast.get(i))) {
          mandatory++;
        }
      }
      if (mandatory < 2) {
        // the node may collapse to its single remaining argument, so the head of the matched
        // expression is not determined
        return null;
      }
    }
    return headSymbol;
  }

  /** Returned by {@link #maxTopLevelFactors(IExpr)} if the number of factors is not bounded. */
  public static final int UNBOUNDED_FACTORS = Integer.MAX_VALUE;

  /**
   * Upper bound for the number of top level {@link S#Times} factors of an expression matched by
   * <code>pattern</code>.
   *
   * <p>
   * <code>Times</code> is {@link ISymbol#FLAT} and {@link ISymbol#ORDERLESS}, so the matcher
   * distributes the factors of the matched expression over the arguments of the pattern and wraps a
   * group of more than one factor in <code>Times</code> again. An argument whose {@link #topHead}
   * is neither unknown nor <code>Times</code> can therefore never take more than a single factor,
   * which bounds the whole product by the number of pattern arguments. Rubi uses this shape for
   * most of its rules; only the ones with a <code>u_.</code> style collector are unbounded.
   *
   * <p>
   * Only an upper bound is derived. A lower bound would have to assume that a mandatory argument
   * never matches an empty group, which is not worth the risk of dropping a matching rule.
   */
  public static int maxTopLevelFactors(IExpr pattern) {
    if (pattern.isAST() && pattern.head() == S.Times) {
      IAST times = (IAST) pattern;
      for (int i = 1; i < times.size(); i++) {
        ISymbol argHead = topHead(times.get(i));
        if (argHead == null || argHead == S.Times) {
          return UNBOUNDED_FACTORS;
        }
      }
      return times.argSize();
    }
    ISymbol head = topHead(pattern);
    if (head == null || head == S.Times) {
      return UNBOUNDED_FACTORS;
    }
    return 1;
  }

  /**
   * Number of top level {@link S#Times} factors of an expression; <code>1</code> if it is no
   * product.
   */
  public static int topLevelFactors(IExpr expr) {
    return expr.isAST() && expr.head() == S.Times ? ((IAST) expr).argSize() : 1;
  }

  /**
   * <code>Alternatives(p1, p2, ...)</code> requires only what <b>all</b> alternatives require.
   */
  private static void collectAlternatives(IAST ast, Set<ISymbol> result, int depth) {
    if (ast.size() < 2) {
      return;
    }
    Set<ISymbol> intersection = new HashSet<ISymbol>();
    collectRequired(ast.arg1(), intersection, depth + 1);
    for (int i = 2; i < ast.size() && !intersection.isEmpty(); i++) {
      Set<ISymbol> alternative = new HashSet<ISymbol>();
      collectRequired(ast.get(i), alternative, depth + 1);
      intersection.retainAll(alternative);
    }
    result.addAll(intersection);
  }
}
