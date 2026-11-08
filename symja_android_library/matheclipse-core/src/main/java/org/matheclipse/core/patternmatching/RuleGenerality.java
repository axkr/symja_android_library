package org.matheclipse.core.patternmatching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.matheclipse.core.expression.Blank;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.OptionsPattern;
import org.matheclipse.core.expression.PatternNested;
import org.matheclipse.core.expression.PatternSequence;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.WildPattern;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Decide whether the left-hand side of one rule matches everything the left-hand side of another
 * rule matches. A new definition is tried before the first existing one which is more general
 * than it; when neither of two definitions is more general, they keep the order in which they
 * were made.
 *
 * <p>
 * The test is structural and conservative: <code>false</code> means "not shown". Arguments are
 * compared position by position, with <code>__</code> and <code>___</code> taking runs of
 * arguments. Heads with the attributes <code>Flat</code>, <code>Orderless</code> or
 * <code>OneIdentity</code>, conditions and tests of the more general side, and pattern functions
 * like <code>Except</code> or <code>Repeated</code> are not compared.
 */
final class RuleGenerality {

  private RuleGenerality() {}

  /**
   * Does <code>general</code> match every expression <code>specific</code> matches?
   *
   * @param general the left-hand side of a rule
   * @param specific the left-hand side of another rule
   * @return <code>true</code> if this could be shown
   */
  static boolean covers(IExpr general, IExpr specific) {
    if (hasRepeatedPatternName(general, new HashSet<ISymbol>())) {
      // f[x_, x_] needs equal arguments, so it matches less than its shape says
      return false;
    }
    return coversExpr(general, specific, false);
  }

  private static final int UNRELATED = 0;
  private static final int EQUAL = 1;
  private static final int RELAXED = 2;

  /**
   * Is the left-hand side of <code>general</code> the one of <code>specific</code> with some of
   * its blanks made optional or freed of their head test or pattern test, as
   * <code>f[x_^n_.]</code> is <code>f[x_^n_]</code>, <code>f[x_^n_Integer]</code> or
   * <code>f[x_^n_?IntegerQ]</code>? The names of the patterns may differ where their indices in
   * the pattern maps of the two rules are equal, as in {@link PatternMatcher#equivalent(IExpr,
   * IExpr, IPatternMap, IPatternMap)}: <code>f[x_^k_.]</code> and <code>f[x_^n_]</code>. Such a
   * blank matches everything it matched before, so <code>general</code> matches every expression
   * <code>specific</code> matches, whatever the attributes of the heads are. A head test and a
   * pattern test are unrelated: <code>f[x_?IntegerQ]</code> doesn't relax
   * <code>f[x_Integer]</code>.
   *
   * <p>
   * A condition of the left-hand side or of the right-hand side narrows a rule by an unknown
   * amount: <code>general</code> may have one only if <code>specific</code> has the same.
   *
   * @param general a rule
   * @param specific another rule
   * @return <code>true</code> if the two differ and only by such blanks of <code>general</code>
   */
  static boolean relaxes(IPatternMatcher general, IPatternMatcher specific) {
    if (!(general instanceof PatternMatcher) || !(specific instanceof PatternMatcher)) {
      return false;
    }
    IPatternMap generalMap = ((PatternMatcher) general).createPatternMap();
    IPatternMap specificMap = ((PatternMatcher) specific).createPatternMap();
    List<IExpr> generalConditions = new ArrayList<IExpr>();
    IExpr generalLHS = stripConditions(general.getOrderingLHS(), generalConditions);
    List<IExpr> specificConditions = new ArrayList<IExpr>();
    IExpr specificLHS = stripConditions(specific.getOrderingLHS(), specificConditions);
    if (!generalConditions.isEmpty() && !slotted(generalConditions, generalMap)
        .equals(slotted(specificConditions, specificMap))) {
      return false;
    }
    return new Relaxation(generalMap, specificMap, false).compare(generalLHS,
        specificLHS) == RELAXED;
  }

  /**
   * May <code>rule</code>, which goes in front of a rule it is more specific than, also pass the
   * rule <code>previous</code> directly in front of that one? Only if the two have the same
   * conditions and left-hand sides, except that an exponent <code>n_?IntegerQ</code> of
   * <code>previous</code> is <code>n_Integer</code> in <code>rule</code>: <code>f[x_^n_Integer]</code>
   * passes <code>f[x_^n_?IntegerQ]</code> on its way in front of <code>f[x_^n_.]</code>, but not the
   * other way round, and not outside of an exponent.
   *
   * @param previous a rule in front of the one <code>rule</code> is inserted before
   * @param rule the new rule
   * @return <code>true</code> if <code>rule</code> goes in front of <code>previous</code>
   */
  static boolean passesIntegerQ(IPatternMatcher previous, IPatternMatcher rule) {
    if (!(previous instanceof PatternMatcher) || !(rule instanceof PatternMatcher)) {
      return false;
    }
    IPatternMap previousMap = ((PatternMatcher) previous).createPatternMap();
    IPatternMap ruleMap = ((PatternMatcher) rule).createPatternMap();
    List<IExpr> previousConditions = new ArrayList<IExpr>();
    IExpr previousLHS = stripConditions(previous.getOrderingLHS(), previousConditions);
    List<IExpr> ruleConditions = new ArrayList<IExpr>();
    IExpr ruleLHS = stripConditions(rule.getOrderingLHS(), ruleConditions);
    if (!slotted(previousConditions, previousMap).equals(slotted(ruleConditions, ruleMap))) {
      return false;
    }
    Relaxation integerExponents = new Relaxation(previousMap, ruleMap, true);
    return integerExponents.compare(previousLHS, ruleLHS) == EQUAL
        && integerExponents.tieUsed;
  }

  /**
   * The conditions with every pattern name replaced by the slot of its index in
   * <code>patternMap</code>, so that conditions of rules with renamed patterns compare equal.
   */
  private static List<IExpr> slotted(List<IExpr> conditions, IPatternMap patternMap) {
    if (conditions.isEmpty()) {
      return conditions;
    }
    IPatternMap slots = patternMap.copy();
    slots.initSlotValues();
    List<IExpr> result = new ArrayList<IExpr>(conditions.size());
    for (IExpr condition : conditions) {
      result.add(slots.substituteSymbols(condition, F.NIL));
    }
    return result;
  }

  /** The left-hand side without its conditions, which are appended to <code>conditions</code>. */
  private static IExpr stripConditions(IExpr lhs, List<IExpr> conditions) {
    while (lhs.isCondition() || lhs.isAST(S.HoldPattern, 2)) {
      if (lhs.isCondition()) {
        conditions.add(((IAST) lhs).arg2());
      }
      lhs = ((IAST) lhs).arg1();
    }
    return lhs;
  }

  /**
   * A key for {@link #relaxes(IPatternMatcher, IPatternMatcher)}: two left-hand sides of which one
   * relaxes the other have the same key.
   */
  static int relaxationHash(IExpr lhs) {
    lhs = stripCondition(lhs);
    if (lhs instanceof Blank) {
      return lhs.getClass().hashCode();
    }
    if (lhs.isAST(S.Optional, 2) || lhs.isAST(S.Optional, 3) || lhs.isAST(S.PatternTest, 3)) {
      return relaxationHash(((IAST) lhs).arg1());
    }
    if (lhs instanceof IPatternObject) {
      return lhs.getClass().hashCode();
    }
    if (lhs.isAST()) {
      IAST ast = (IAST) lhs;
      int hash = ast.argSize();
      for (int i = 0; i < ast.size(); i++) {
        hash = 31 * hash + relaxationHash(ast.get(i));
      }
      return hash;
    }
    return lhs.hashCode();
  }

  /**
   * Compares two left-hand sides for {@link RuleGenerality#relaxes(IPatternMatcher,
   * IPatternMatcher)}.
   */
  private static final class Relaxation {
    private final IPatternMap generalMap;
    private final IPatternMap specificMap;
    /** An exponent <code>n_?IntegerQ</code> of the general side equals <code>n_Integer</code>. */
    private final boolean integerTie;
    /** Was such an exponent found? */
    boolean tieUsed;

    Relaxation(IPatternMap generalMap, IPatternMap specificMap, boolean integerTie) {
      this.generalMap = generalMap;
      this.specificMap = specificMap;
      this.integerTie = integerTie;
    }

    int compare(IExpr general, IExpr specific) {
      if (general == specific || (general.isFreeOfPatterns() && general.equals(specific))) {
        return EQUAL;
      }
      if (general instanceof Blank) {
        if (specific.isAST(S.PatternTest, 3)) {
          // x_ and x_?IntegerQ: the test only narrows the blank
          return compare(general, ((IAST) specific).arg1()) == UNRELATED ? UNRELATED : RELAXED;
        }
        return blank((Blank) general, specific);
      }
      if ((general.isAST(S.Optional, 2) || general.isAST(S.Optional, 3))
          && !specific.isAST(S.Optional)) {
        // x_:d and x_, x_Integer or x_?IntegerQ
        return compare(((IAST) general).arg1(), specific) == UNRELATED ? UNRELATED : RELAXED;
      }
      if (general instanceof IPatternObject || specific instanceof IPatternObject) {
        return general.equals(specific)
            && sameIndex((IPatternObject) general, (IPatternObject) specific) ? EQUAL : UNRELATED;
      }
      if (!general.isAST() || !specific.isAST() || general.size() != specific.size()) {
        return UNRELATED;
      }
      IAST g = (IAST) general;
      IAST s = (IAST) specific;
      int result = EQUAL;
      for (int i = 0; i < g.size(); i++) {
        int r = integerTie && i == 2 && g.isPower() && s.isPower() ? exponent(g.arg2(), s.arg2())
            : compare(g.get(i), s.get(i));
        if (r == UNRELATED) {
          return UNRELATED;
        }
        if (r == RELAXED) {
          result = RELAXED;
        }
      }
      return result;
    }

    /**
     * The blank <code>general</code> and <code>specific</code>: <code>EQUAL</code> if they differ
     * only by their names, <code>RELAXED</code> if <code>general</code> is optional and
     * <code>specific</code> is not, or <code>general</code> has no head test and
     * <code>specific</code> has one.
     */
    private int blank(Blank general, IExpr specific) {
      if (!(specific instanceof Blank) || general.getClass() != specific.getClass()) {
        return UNRELATED;
      }
      Blank s = (Blank) specific;
      if (!sameIndex(general, s)) {
        return UNRELATED;
      }
      IExpr headTest = general.getHeadTest();
      if (general.isPatternDefault() == s.isPatternDefault()
          && java.util.Objects.equals(headTest, s.getHeadTest())) {
        return EQUAL;
      }
      if (s.isPatternDefault()) {
        return UNRELATED;
      }
      return headTest == null || (general.isPatternDefault() && headTest.equals(s.getHeadTest()))
          ? RELAXED
          : UNRELATED;
    }

    /** Compares exponents, where <code>n_?IntegerQ</code> and <code>n_Integer</code> are equal. */
    private int exponent(IExpr general, IExpr specific) {
      Blank tested = integerQBlank(general);
      if (tested != null && integerBlank(specific) && tested.getClass() == specific.getClass()
          && sameIndex(tested, (Blank) specific)) {
        tieUsed = true;
        return EQUAL;
      }
      return compare(general, specific);
    }

    /** The blank <code>x_</code> of <code>x_?IntegerQ</code>, otherwise <code>null</code>. */
    private static Blank integerQBlank(IExpr expr) {
      if (expr.isAST(S.PatternTest, 3) && ((IAST) expr).arg2() == S.IntegerQ) {
        IExpr blank = ((IAST) expr).arg1();
        if (blank instanceof Blank && !blank.isPatternDefault()
            && ((Blank) blank).getHeadTest() == null) {
          return (Blank) blank;
        }
      }
      return null;
    }

    /** Is <code>expr</code> a blank <code>x_Integer</code> without a default? */
    private static boolean integerBlank(IExpr expr) {
      return expr instanceof Blank && !expr.isPatternDefault()
          && ((Blank) expr).getHeadTest() == S.Integer;
    }

    /** Do the two patterns have the same index in the pattern maps of their rules? */
    private boolean sameIndex(IPatternObject general, IPatternObject specific) {
      if (general.getSymbol() == null || specific.getSymbol() == null) {
        return general.getSymbol() == specific.getSymbol();
      }
      return general.getIndex(generalMap) == specific.getIndex(specificMap);
    }
  }

  /**
   * Is <code>general</code> to be tried after <code>specific</code>? As {@link #covers(IExpr,
   * IExpr)}, but a pattern with a test or a condition also counts as more general than a literal
   * argument it has the shape of: <code>f[k_?Positive]</code> is tried after <code>f[1]</code>.
   */
  static boolean precedes(IExpr specific, IExpr general) {
    if (hasRepeatedPatternName(general, new HashSet<ISymbol>())) {
      return false;
    }
    return coversExpr(general, specific, true);
  }

  /**
   * Can two left-hand sides without a run of arguments match the same expression? Not if their
   * heads are built differently, as <code>f[x_]</code> and <code>f[x_][y_]</code>, or if they have
   * different numbers of arguments and no optional ones.
   */
  static boolean mayOverlap(IExpr lhs1, IExpr lhs2) {
    lhs1 = stripCondition(lhs1);
    lhs2 = stripCondition(lhs2);
    if (!lhs1.isAST() || !lhs2.isAST()) {
      return true;
    }
    IAST ast1 = (IAST) lhs1;
    IAST ast2 = (IAST) lhs2;
    if (ast1.isPatternMatchingFunction() || ast2.isPatternMatchingFunction()) {
      return true;
    }
    if (ast1.head().isAST() != ast2.head().isAST()) {
      return false;
    }
    if (ast1.head().isAST() && !mayOverlap(ast1.head(), ast2.head())) {
      return false;
    }
    if (ast1.argSize() != ast2.argSize()) {
      return ast1.exists(RuleGenerality::containsArgumentRun)
          || ast2.exists(RuleGenerality::containsArgumentRun);
    }
    return true;
  }

  private static IExpr stripCondition(IExpr lhs) {
    while (lhs.isCondition() || lhs.isAST(S.HoldPattern, 2)) {
      lhs = ((IAST) lhs).arg1();
    }
    return lhs;
  }

  private static boolean hasRepeatedPatternName(IExpr expr, Set<ISymbol> names) {
    if (expr instanceof IPatternObject) {
      ISymbol symbol = ((IPatternObject) expr).getSymbol();
      if (symbol != null && !names.add(symbol)) {
        return true;
      }
      if (expr instanceof PatternNested) {
        return hasRepeatedPatternName(((PatternNested) expr).getPatternExpr(), names);
      }
      return false;
    }
    if (expr.isASTOrAssociation()) {
      IAST ast = (IAST) expr;
      for (int i = 0; i < ast.size(); i++) {
        if (hasRepeatedPatternName(ast.get(i), names)) {
          return true;
        }
      }
    }
    return false;
  }

  /** Does the single-expression pattern <code>general</code> cover <code>specific</code>? */
  private static boolean coversExpr(IExpr general, IExpr specific, boolean literals) {
    if (general.equals(specific)) {
      return true;
    }
    if (general.isAST(S.HoldPattern, 2)) {
      return coversExpr(((IAST) general).arg1(), specific, literals);
    }
    if (specific.isAST(S.HoldPattern, 2)) {
      return coversExpr(general, ((IAST) specific).arg1(), literals);
    }
    // the specific side only gets narrower by a condition, a test or a name
    if (specific.isCondition() || specific.isAST(S.PatternTest, 3)) {
      return coversExpr(general, ((IAST) specific).arg1(), literals);
    }
    if (specific instanceof PatternNested) {
      return coversExpr(general, ((PatternNested) specific).getPatternExpr(), literals);
    }
    if (literals && (general.isCondition() || general.isAST(S.PatternTest, 3))
        && specific.isFreeOfPatterns()) {
      // a literal argument is more specific than a pattern with a test or a condition
      return coversExpr(((IAST) general).arg1(), specific, literals);
    }
    if (specific.isAST(S.Alternatives)) {
      return ((IAST) specific).forAll(x -> coversExpr(general, x, literals));
    }
    if (general instanceof PatternNested) {
      return coversExpr(((PatternNested) general).getPatternExpr(), specific, literals);
    }
    if (general.isAST(S.Alternatives)) {
      return ((IAST) general).exists(x -> coversExpr(x, specific, literals));
    }
    if (general instanceof IPatternSequence || specific instanceof IPatternSequence) {
      // runs of arguments are compared in coversArguments()
      return false;
    }
    if (general instanceof Blank) {
      if (general instanceof WildPattern || general.isPatternDefault()) {
        return false;
      }
      return hasHead(((Blank) general).getHeadTest(), specific);
    }
    if (general instanceof IPatternObject || !general.isAST()
        || ((IAST) general).isPatternMatchingFunction()) {
      return false;
    }
    if (!specific.isAST() || specific instanceof IPatternObject
        || ((IAST) specific).isPatternMatchingFunction()) {
      return false;
    }
    IAST generalAST = (IAST) general;
    IAST specificAST = (IAST) specific;
    IExpr head = generalAST.head();
    if (head.isSymbol() && (((ISymbol) head).hasFlatAttribute()
        || ((ISymbol) head).hasOrderlessAttribute() || ((ISymbol) head).hasOneIdentityAttribute())) {
      return false;
    }
    return coversExpr(head, specificAST.head(), literals)
        && coversArguments(generalAST, specificAST, literals);
  }

  /**
   * Is every expression matched by <code>specific</code> one with the head <code>headTest</code>
   * (any head if <code>headTest</code> is <code>null</code>)?
   */
  private static boolean hasHead(IExpr headTest, IExpr specific) {
    if (specific instanceof IPatternSequence) {
      return false;
    }
    if (headTest == null) {
      return !specific.isPatternDefault();
    }
    if (specific instanceof Blank) {
      return !(specific instanceof WildPattern) && !specific.isPatternDefault()
          && headTest.equals(((Blank) specific).getHeadTest());
    }
    if (specific instanceof IPatternObject) {
      return false;
    }
    if (specific.isAST()) {
      IAST ast = (IAST) specific;
      return !ast.isPatternMatchingFunction() && headTest.equals(ast.head());
    }
    return headTest.equals(specific.head());
  }

  /**
   * How many arguments one argument pattern stands for, and which shape each of them has. Plain
   * patterns stand for exactly one argument, <code>__</code> for one or more, <code>___</code> and
   * an optional <code>x_.</code> for none or more resp. none or one.
   */
  private static final class Arity {
    final int min;
    final boolean atMostOne;
    /** the pattern of one argument, or the head test of a run */
    final IExpr element;
    final boolean isRun;
    /** a run of options, <code>OptionsPattern[]</code> */
    final boolean isOptions;

    Arity(int min, boolean atMostOne, IExpr element, boolean isRun) {
      this(min, atMostOne, element, isRun, false);
    }

    Arity(int min, boolean atMostOne, IExpr element, boolean isRun, boolean isOptions) {
      this.min = min;
      this.atMostOne = atMostOne;
      this.element = element;
      this.isRun = isRun;
      this.isOptions = isOptions;
    }
  }

  private static final Arity OPTIONS = new Arity(0, false, null, true, true);

  /** @return <code>null</code> if the number of arguments the pattern takes is not known */
  private static Arity arity(IExpr argument) {
    if (argument instanceof OptionsPattern || (argument instanceof PatternNested
        && ((PatternNested) argument).getPatternExpr() instanceof OptionsPattern)) {
      return OPTIONS;
    }
    if (argument instanceof PatternSequence) {
      PatternSequence sequence = (PatternSequence) argument;
      if (sequence.isDefault()) {
        return null;
      }
      return new Arity(sequence.isNullSequence() ? 0 : 1, false, sequence.getHeadTest(), true);
    }
    if (argument instanceof IPatternSequence) {
      return null;
    }
    if (argument.isAST(S.Optional, 2) || argument.isAST(S.Optional, 3)) {
      // n_:12 takes no argument or one which n_ matches
      IExpr inner = ((IAST) argument).arg1();
      if (inner instanceof Blank && !(inner instanceof WildPattern) && !inner.isPatternDefault()) {
        return new Arity(0, true, ((Blank) inner).getHeadTest(), true);
      }
      return null;
    }
    if (argument.isPatternDefault()) {
      if (argument instanceof Blank && !(argument instanceof WildPattern)) {
        return new Arity(0, true, ((Blank) argument).getHeadTest(), true);
      }
      return null;
    }
    if (argument instanceof PatternNested) {
      IExpr inner = ((PatternNested) argument).getPatternExpr();
      return containsArgumentRun(inner) ? null : new Arity(1, true, argument, false);
    }
    if (argument.isAST()) {
      IAST ast = (IAST) argument;
      if (ast.isCondition() || ast.isAST(S.PatternTest, 3) || ast.isAST(S.HoldPattern, 2)
          || ast.isAST(S.Alternatives)) {
        return containsArgumentRun(ast) ? null : new Arity(1, true, argument, false);
      }
      if (ast.isPatternMatchingFunction() || ast.isAST(S.PatternSequence)
          || ast.isAST(S.OrderlessPatternSequence) || ast.isAST(S.Longest)
          || ast.isAST(S.Shortest)) {
        return null;
      }
    }
    return new Arity(1, true, argument, false);
  }

  /** Is there a run of arguments at the top of this argument pattern? */
  private static boolean containsArgumentRun(IExpr pattern) {
    if (pattern instanceof IPatternSequence || pattern.isPatternDefault()) {
      return true;
    }
    if (pattern instanceof PatternNested) {
      return containsArgumentRun(((PatternNested) pattern).getPatternExpr());
    }
    if (pattern.isCondition() || pattern.isAST(S.PatternTest, 3)
        || pattern.isAST(S.HoldPattern, 2)) {
      return containsArgumentRun(((IAST) pattern).arg1());
    }
    if (pattern.isAST(S.Alternatives)) {
      return ((IAST) pattern).exists(RuleGenerality::containsArgumentRun);
    }
    return false;
  }

  /**
   * Does the argument list of <code>general</code> cover the one of <code>specific</code>? A run
   * of <code>general</code> takes a run of consecutive arguments of <code>specific</code>, each of
   * which must have its head, and at least as many as the run needs.
   */
  private static boolean coversArguments(IAST general, IAST specific, boolean literals) {
    final int gSize = general.argSize();
    final int sSize = specific.argSize();
    Arity[] g = new Arity[gSize];
    Arity[] s = new Arity[sSize];
    for (int i = 0; i < gSize; i++) {
      if ((g[i] = arity(general.get(i + 1))) == null) {
        return false;
      }
    }
    for (int j = 0; j < sSize; j++) {
      if ((s[j] = arity(specific.get(j + 1))) == null) {
        return false;
      }
    }
    // covered[i][j]: the first i patterns of general cover the first j of specific
    boolean[][] covered = new boolean[gSize + 1][sSize + 1];
    covered[0][0] = true;
    for (int i = 0; i < gSize; i++) {
      for (int j = 0; j <= sSize; j++) {
        if (!covered[i][j]) {
          continue;
        }
        if (!g[i].isRun) {
          if (j < sSize && !s[j].isRun && coversExpr(g[i].element, s[j].element, literals)) {
            covered[i + 1][j + 1] = true;
          }
          continue;
        }
        // the run g[i] takes specific's patterns j..k; an optional argument takes at most one
        covered[i + 1][j] |= g[i].min == 0;
        int min = 0;
        for (int k = j; k < sSize; k++) {
          if (!runTakes(g[i], s[k])
              || (g[i].atMostOne && (k > j || !s[k].atMostOne))) {
            break;
          }
          min += s[k].min;
          if (min >= g[i].min) {
            covered[i + 1][k + 1] = true;
          }
        }
      }
    }
    return covered[gSize][sSize];
  }

  /** Can the run <code>general</code> take every argument <code>specific</code> stands for? */
  private static boolean runTakes(Arity general, Arity specific) {
    if (general.isOptions || specific.isOptions) {
      // options go with options, or with a run which takes anything
      return specific.isOptions && (general.isOptions || general.element == null);
    }
    final IExpr headTest = general.element;
    if (specific.isRun) {
      return headTest == null || headTest.equals(specific.element);
    }
    return hasHead(headTest, specific.element);
  }
}
