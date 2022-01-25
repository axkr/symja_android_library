package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/** Interface for the pattern matcher */
public abstract class IPatternMatcher implements Cloneable, Predicate<IExpr>, Serializable {

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

  public static class PriorityComparator implements Comparator<IPatternMatcher> {
    @Override
    public int compare(IPatternMatcher o1, IPatternMatcher o2) {
      return o1.getLHSPriority() < o2.getLHSPriority()
          ? -1
          : o1.getLHSPriority() > o2.getLHSPriority() ? 1 : 0;
    }
  }

  public static final int NOFLAG = 0x0000;

  /** This rule is defined with the <code>Set[]</code> function */
  public static final int SET = 0x0001;

  /** This rule is defined with the <code>SetDelayed[]</code> function */
  public static final int SET_DELAYED = 0x0002;

  /** This rule is defined with the <code>Set[]</code> function */
  public static final int TAGSET = 0x0004;

  /** This rule is defined with the <code>Set[]</code> function */
  public static final int TAGSET_DELAYED = 0x0008;

  /** This rule is defined with the <code>Set[]</code> function */
  public static final int UPSET = 0x0010;

  /** This rule is defined with the <code>Set[]</code> function */
  public static final int UPSET_DELAYED = 0x0020;

  public final static int THROW_IF_TRUE = 0x01001;

  /** This rules left-hand-side is wrapped with a <code>Literal[]</code> function */
  public static final int LITERAL = 0x1000;

  /** This rules left-hand-side is wrapped with a <code>HoldPattern[]</code> function */
  public static final int HOLDPATTERN = 0x2000;

  /** Serialization mask */
  public static final int SERIALIZATION_MASK = 0x8000;

  // Serializable {
  public static final EquivalenceComparator EQUIVALENCE_COMPARATOR = new EquivalenceComparator();

  public static final PriorityComparator PRIORITY_COMPARATOR = new PriorityComparator();

  /** */
  private static final long serialVersionUID = 2841686297882535691L;

  /** Contains the "pattern-matching" expression */
  protected IExpr fLhsPatternExpr;

  /**
   * Contains the lhs expression which should be matched in a clone of this pattern matcher during
   * matching
   */
  protected transient IExpr fLhsExprToMatch;

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
    IPatternMatcher v = (IPatternMatcher) super.clone();
    v.fLhsPatternExpr = fLhsPatternExpr;
    v.fLhsExprToMatch = fLhsExprToMatch;
    return v;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    IPatternMatcher other = (IPatternMatcher) obj;
    if (fLhsPatternExpr == null) {
      if (other.fLhsPatternExpr != null) return false;
    } else if (!fLhsPatternExpr.equals(other.fLhsPatternExpr)) return false;
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
    return fLhsPatternExpr.hashCode();
  }

  /**
   * Check if <code>fPatterHash == 0 || fPatterHash == patternHash;</code>.
   *
   * @param patternHash
   * @return
   */
  public abstract boolean isPatternHashAllowed(int patternHash);

  /**
   * Check if the pattern-matchings left-hand-side expression contains no patterns.
   *
   * @return <code>true</code>, if the given expression contains no patterns
   */
  public abstract boolean isRuleWithoutPatterns();

  /**
   * Contains the lhs expression which should be matched in a clone of this pattern matcher during
   * matching.
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
   *     expression.
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
   *     expression.
   */
  public abstract boolean test(IExpr expr, EvalEngine engine) throws ThrowException;

  /**
   * Start pattern matching. Initialize only <code>Blank...()</code> patterns (without assigned
   * symbol name) before matching.
   *
   * @param expr
   * @param engine
   * @return <code>true</code> if the <code>expr</code> matches the pattern-matchings left-hand-side
   *     expression.
   */
  public boolean testBlank(IExpr expr, EvalEngine engine) {
    return test(expr, engine);
  }

  /**
   * If <code>true</code> throw a {@link ThrowException} with the matching result as it's value.
   *
   * @param throwIfMatched
   */
  public void throwExceptionArgIfMatched(boolean throwIfMatched) {
    //
  }
}
