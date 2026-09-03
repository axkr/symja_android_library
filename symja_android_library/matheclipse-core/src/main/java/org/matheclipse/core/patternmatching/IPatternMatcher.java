package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Abstract base class for <b>Pattern Matching Engines</b>.
 * <p>
 * {@code IPatternMatcher} defines the contract for checking if a target expression matches a
 * specific structural pattern (the "Left-Hand Side" or LHS of a rule). It implements
 * {@link Predicate}, allowing pattern matchers to be used directly in Java streams and filters.
 * </p>
 *
 * <h3>1. Hierarchy</h3>
 * <p>
 * This class serves as the parent for:
 * </p>
 * <ul>
 * <li><b>{@link PatternMatcher}:</b> The standard matcher that verifies if {@code expr} matches a
 * pattern and binds variables (like {@code x_}) to an {@link IPatternMap}.</li>
 * <li><b>{@link PatternMatcherAndEvaluator}:</b> Extends matching by also holding the "Right-Hand
 * Side" (RHS). If the match succeeds, it can immediately substitute the bound variables into the
 * RHS to produce a result.</li>
 * </ul>
 *
 * <h3>2. Key Methods</h3>
 * <ul>
 * <li>{@link #test(IExpr)}: The primary method to check if the input expression matches the pattern
 * structure.</li>
 * <li>{@link #eval(IExpr, EvalEngine)}: Match the given left-hand-side and return an evaluated
 * expression if match succeeds.</li>
 * </ul>
 *
 * <h3>3. Usage Examples</h3>
 *
 * <h4>Basic Matching</h4>
 * 
 * <pre>
 * // Create a pattern: f[x_Integer]
 * IAST pattern = F.unary(F.Dummy("f"), F.$p(F.Dummy("x"), S.Integer));
 *
 * // Create the matcher
 * IPatternMatcher matcher = new PatternMatcher(pattern);
 *
 * // Test against expressions
 * boolean match1 = matcher.test(F.unary(F.Dummy("f"), F.C10)); // True
 * boolean match2 = matcher.test(F.unary(F.Dummy("f"), F.num(2.5))); // False (not Integer)
 * </pre>
 *
 * <h4>Usage as Predicate</h4>
 * 
 * <pre>
 * // Filter a list of expressions
 * List&lt;IExpr&gt; exprs = Arrays.asList(F.C1, F.a, F.C2);
 * IPatternMatcher isInteger = new PatternMatcher(F.$b(S.Integer)); // _Integer
 *
 * // Keep only integers: {1, 2}
 * exprs.stream().filter(isInteger).collect(Collectors.toList());
 * </pre>
 *
 * @see org.matheclipse.core.patternmatching.PatternMatcher
 * @see org.matheclipse.core.patternmatching.PatternMatcherAndEvaluator
 * @see org.matheclipse.core.patternmatching.IPatternMap
 */
public abstract class IPatternMatcher implements Cloneable, Predicate<IExpr>, Serializable {

  /**
   * Dummy <code>right-hand-side</code> for a rule in {@link S#SubsetCases} to get the sublists in
   * list that match the <code>left-hand-side pattern</code>.
   */
  public final static ISymbol DUMMY_SUBSET_CASES = F.Dummy("§subsetcases§");

  public static class EquivalenceComparator implements Comparator<IPatternMatcher>, Serializable {

    private static final long serialVersionUID = 8357661139299702326L;

    @Override
    public int compare(final IPatternMatcher o1, final IPatternMatcher o2) {
      if (o1 == o2) {
        return 0;
      }
      return o1.equivalentTo(o2);
    }
  }

  public static final int NOFLAG = 0x0000;

  /** This rule is defined with the <code>Set[]</code> function */
  public static final int SET = 0x0001;

  /** This rule is defined with the <code>SetDelayed[]</code> function */
  public static final int SET_DELAYED = 0x0002;

  /** This rule is defined with the <code>TagSet[]</code> function */
  public static final int TAGSET = 0x0004;

  /** This rule is defined with the <code>TagSetDelayed[]</code> function */
  public static final int TAGSET_DELAYED = 0x0008;

  /** This rule is defined with the <code>UpSet[]</code> function */
  public static final int UPSET = 0x0010;

  /** This rule is defined with the <code>UpSetDelayed[]</code> function */
  public static final int UPSET_DELAYED = 0x0020;

  /** This rules left-hand-side is wrapped with a <code>Literal[]</code> function */
  public static final int LITERAL = 0x1000;

  /** This rules left-hand-side is wrapped with a <code>HoldPattern[]</code> function */
  public static final int HOLDPATTERN = 0x2000;

  public static final EquivalenceComparator EQUIVALENCE_COMPARATOR = new EquivalenceComparator();

  /** */
  private static final long serialVersionUID = 2841686297882535691L;

  /** Contains the "pattern-matching" expression */
  protected IExpr fLhsPatternExpr;

  /**
   * The flags of the definition which created this matcher, i.e. one of {@link #SET},
   * {@link #SET_DELAYED}, {@link #UPSET},... possibly combined with {@link #HOLDPATTERN} or
   * {@link #LITERAL}.
   */
  protected int fSetFlags;

  /**
   * Contains the lhs expression which should be matched in a clone of this pattern matcher during
   * matching
   */
  protected transient IExpr fLhsExprToMatch;

  /**
   * <code>0</code> if this matcher instance is free, <code>1</code> if it is currently used for a
   * pattern-matching evaluation. Updated via {@link #IN_USE_UPDATER} in {@link #tryAcquire()}.
   */
  private transient volatile int fInUse;

  private static final AtomicIntegerFieldUpdater<IPatternMatcher> IN_USE_UPDATER =
      AtomicIntegerFieldUpdater.newUpdater(IPatternMatcher.class, "fInUse");

  /**
   * Try to acquire this matcher instance for exclusive use in a pattern-matching evaluation.
   *
   * <p>
   * A matcher holds mutable per-match state (the pattern map bindings, the left-hand-side to match,
   * a possibly evaluated right-hand-side result). The instances stored in {@link RulesData} are
   * shared - between threads, and re-entrantly on the same thread when the evaluation of a rules
   * right-hand-side (or condition) recurses into the same rule set. Callers which formerly always
   * worked on a {@link #copy()} can instead acquire the shared instance: if the acquisition
   * succeeds, evaluate with <code>this</code> and {@link #release()} in a <code>finally</code>
   * block; if it fails, the instance is currently in use and the caller must fall back to
   * {@link #copy()}.
   *
   * @return <code>true</code> if this instance may be used directly; <code>false</code> if it is
   *         already in use and a copy is required
   * @see RulesData#evalDownRule(IExpr, EvalEngine)
   */
  public final boolean tryAcquire() {
    return IN_USE_UPDATER.compareAndSet(this, 0, 1);
  }

  /**
   * Release this matcher instance after a successful {@link #tryAcquire()}. Must be called exactly
   * once, in a <code>finally</code> block.
   */
  public final void release() {
    fInUse = 0;
  }

  protected IPatternMatcher() {
    fLhsPatternExpr = null;
    fLhsExprToMatch = F.NIL;
  }

  public IPatternMatcher(IExpr lhsPatternExpr) {
    fLhsPatternExpr = lhsPatternExpr;
    fLhsExprToMatch = F.NIL;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public abstract IPatternMatcher copy();

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IPatternMatcher other = (IPatternMatcher) obj;
    if (fLhsPatternExpr == null) {
      if (other.fLhsPatternExpr != null)
        return false;
    } else if (!fLhsPatternExpr.equals(other.fLhsPatternExpr))
      return false;
    return true;
  }

  /**
   * Compare only the left-hand-side expressions in the matchers for equivalence
   *
   * @param obj
   * @return
   */
  public abstract int equivalentLHS(final IPatternMatcher obj);

  public abstract int equivalentTo(IPatternMatcher patternMatcher);

  /**
   * Match the given left-hand-side and return an evaluated expression
   *
   * @param leftHandSide left-hand-side expression
   * @param engine
   * @return <code>F.NIL</code> if the match wasn't successful, the evaluated expression otherwise.
   */
  public abstract IExpr eval(final IExpr leftHandSide, EvalEngine engine);

  /**
   * The rule as an expression: <code>Set(lhs, rhs)</code>, <code>SetDelayed(lhs, rhs)</code>,...
   * possibly wrapped in <code>HoldPattern()</code> or <code>Literal()</code>.
   */
  public IAST getAsAST() {
    IAST temp = F.binaryAST2(getSetSymbol(), getLHS(), getRHS());
    if (isFlagOn(HOLDPATTERN)) {
      return F.HoldPattern(temp);
    }
    if (isFlagOn(LITERAL)) {
      return F.Literal(temp);
    }
    return temp;
  }

  /**
   * Get the flags for this matcher.
   *
   * @see #SET
   */
  public final int getFlags() {
    return fSetFlags;
  }

  /**
   * Return the <code>Set, SetDelayed, UpSet,...</code> symbol which defined this rule.
   *
   * @return <code>null</code> if no symbol was defined
   */
  public final ISymbol getSetSymbol() {
    if (isFlagOn(SET_DELAYED)) {
      return S.SetDelayed;
    }
    if (isFlagOn(SET)) {
      return S.Set;
    }
    if (isFlagOn(UPSET_DELAYED)) {
      return S.UpSetDelayed;
    }
    if (isFlagOn(UPSET)) {
      return S.UpSet;
    }
    if (isFlagOn(TAGSET_DELAYED)) {
      return S.TagSetDelayed;
    }
    if (isFlagOn(TAGSET)) {
      return S.TagSet;
    }
    return null;
  }

  /**
   * Are the given flags enabled ?
   *
   * @param flags
   * @see #SET
   */
  public final boolean isFlagOn(int flags) {
    return (fSetFlags & flags) == flags;
  }

  /**
   * Get the "left-hand-side" of a pattern-matching rule.
   *
   * @return
   */
  public IExpr getLHS() {
    return fLhsPatternExpr;
  }

  /**
   * During evaluation get the lhs expression which should match the patterns.
   *
   * @return {@link F#NIL} if not defined
   */
  public IExpr getLHSExprToMatch() {
    return fLhsExprToMatch;
  }

  /**
   * Get the priority of the left-and-side of this pattern-matcher. Lower values have higher
   * priorities.
   *
   * @return the priority
   */
  public abstract int getLHSPriority();

  public abstract int getPatternHash();

  /**
   * Get the current pattern map of this matcher. If not initialized return <code>null</code>.
   *
   * @return <code>null</code> if not initialized; the pattern map otherwise
   */
  public IPatternMap getPatternMap() {
    return null;
  }

  /**
   * Returns the matched pattern in the order they appear in the pattern expression.
   *
   * @param resultList a list instance
   * @param patternExpr the expression which contains the pattern objects
   */
  public abstract void getPatterns(List<IExpr> resultList, IExpr patternExpr);

  /**
   * Get the "right-hand-side" of a pattern-matching rule.
   *
   * @return <code>F.NIL</code> if no right-hand-side is defined for the pattern matcher
   */
  public IExpr getRHS() {
    return F.NIL;
  }

  @Override
  public int hashCode() {
    // fLhsPatternExpr can be null for matchers created by the serialization constructors;
    // equals() guards against that case as well
    return fLhsPatternExpr == null ? 0 : fLhsPatternExpr.hashCode();
  }

  /**
   * Check if <code>fPatterHash == 0 || fPatterHash == patternHash;</code>.
   *
   * @param patternHash
   */
  public abstract boolean isPatternHashAllowed(int patternHash);

  /**
   * Check if the pattern-matchings left-hand-side expression contains no patterns.
   *
   * @return <code>true</code>, if the given expression contains no patterns
   */
  public abstract boolean isRuleWithoutPatterns();

  /**
   * Contains the left-hand-side expression which should be matched in a clone of this pattern
   * matcher during matching.
   *
   * @param lhsExprToMatch
   */
  public void setLHSExprToMatch(IExpr lhsExprToMatch) {
    this.fLhsExprToMatch = lhsExprToMatch;
  }

  /**
   * Return <code>true</code> if the pattern-matchings left-hand-side matches the <code>expr</code>
   * or if <code>Orderlesss</code> the pattern-matchings left-hand-side matches only a part of the
   * <code>expr</code>.
   *
   * @param expr
   * @return <code>true</code> if the <code>expr</code> matches the pattern-matchings left-hand-side
   *         expression.
   */
  @Override
  public abstract boolean test(IExpr expr) throws ThrowException;

  /**
   * Return <code>true</code> if the the pattern-matchings left-hand-side matches the <code>expr
   * </code>.
   *
   * @param expr
   * @param engine
   * @return <code>true</code> if the <code>expr</code> matches the pattern-matchings left-hand-side
   *         expression.
   */
  public abstract boolean test(IExpr expr, EvalEngine engine) throws ThrowException;

  /**
   * Start pattern matching. Initialize only <code>Blank...()</code> patterns (without assigned
   * symbol name) before matching.
   *
   * @param expr
   * @param engine
   * @return <code>true</code> if the <code>expr</code> matches the pattern-matchings left-hand-side
   *         expression.
   */
  public boolean testBlank(IExpr expr, EvalEngine engine) {
    return test(expr, engine);
  }

  /**
   * If <code>true</code> the {@link #test(IExpr)} method throws a
   * {@link org.matheclipse.core.eval.exception.ResultException} with the matched expression as its
   * value instead of returning <code>true</code>.
   *
   * @param throwIfMatched
   */
  public void throwExceptionArgIfMatched(boolean throwIfMatched) {
    //
  }
}
