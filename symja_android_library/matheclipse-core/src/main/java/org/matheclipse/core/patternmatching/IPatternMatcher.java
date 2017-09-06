package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Interface for the pattern matcher
 */
public abstract class IPatternMatcher implements Predicate<IExpr>, Cloneable, Serializable {// Comparable<IPatternMatcher>,
																							// Serializable {
	public final static EquivalenceComparator EQUIVALENCE_COMPARATOR = new EquivalenceComparator();

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

	/**
	 * 
	 */
	private static final long serialVersionUID = 2841686297882535691L;

	/**
	 * Contains the "pattern-matching" expression
	 * 
	 */
	protected IExpr fLhsPatternExpr;

	protected IPatternMatcher() {
		fLhsPatternExpr = null;
	}

	public IPatternMatcher(IExpr lhsPatternExpr) {
		fLhsPatternExpr = lhsPatternExpr;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		IPatternMatcher v = (IPatternMatcher) super.clone();
		v.fLhsPatternExpr = fLhsPatternExpr;
		return v;
	}

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

	public abstract int equivalentTo(IPatternMatcher patternMatcher);

	/**
	 * Compare the matchers for equivalence
	 * 
	 * @param obj
	 * @return
	 */
	// public abstract int equivalent(final IPatternMatcher obj);

	/**
	 * Compare only the left-hand-side expressions in the matchers for equivalence
	 * 
	 * @param obj
	 * @return
	 */
	public abstract int equivalentLHS(final IPatternMatcher obj);

	/**
	 * Match the given left-hand-side and return an evaluated expression
	 * 
	 * @param leftHandSide
	 *            left-hand-side expression
	 * @param engine
	 * @return <code>F.NIL</code> if the match wasn't successful, the evaluated
	 *         expression otherwise.
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
	 * Returns the matched pattern in the order they appear in the pattern
	 * expression.
	 * 
	 * 
	 * @param resultList
	 *            a list instance
	 * @param patternExpr
	 *            the expression which contains the pattern objects
	 */
	public abstract void getPatterns(List<IExpr> resultList, IExpr patternExpr);

	/**
	 * Get the priority of the left-and-side of this pattern-matcher. Lower values
	 * have higher priorities.
	 * 
	 * @return the priority
	 */
	public abstract int getLHSPriority();

	/**
	 * Get the priority of the left-and-side of this pattern-matcher. Lower values
	 * have higher priorities.
	 * 
	 * @return the priority
	 */
	public long getRHSleafCountSimplify() {
		return Long.MAX_VALUE;
	}

	/**
	 * Get the "right-hand-side" of a pattern-matching rule.
	 * 
	 * @return <code>F.NIL</code> if no right-hand-side is defined for the pattern
	 *         matcher
	 */
	public IExpr getRHS() {
		return F.NIL;
	}

	@Override
	public int hashCode() {
		return fLhsPatternExpr.hashCode();
	}

	/**
	 * Check if the pattern-matchings left-hand-side expression contains no
	 * patterns.
	 * 
	 * @return <code>true</code>, if the given expression contains no patterns
	 */
	public abstract boolean isRuleWithoutPatterns();

	/**
	 * Start pattern matching.
	 * 
	 * @param expr
	 * @return <code>true</code> if the <code>expr</code> matches the
	 *         pattern-matchings left-hand-side expression.
	 */
	@Override
	public abstract boolean test(IExpr expr);
	
	/**
	 * Start pattern matching.
	 * 
	 * @param expr
	 * @return <code>true</code> if the <code>expr</code> matches the
	 *         pattern-matchings left-hand-side expression.
	 */
	public abstract boolean test(IExpr expr, EvalEngine engine);
}
