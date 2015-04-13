package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.util.List;

import org.matheclipse.core.interfaces.IExpr;

import com.google.common.base.Predicate;

/**
 * Interface for the pattern matcher
 */
public abstract class IPatternMatcher implements Predicate<IExpr>, Cloneable, Comparable<IPatternMatcher>, Serializable {

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

	/**
	 * Get the "left-hand-side" of a pattern-matching rule.
	 * 
	 * @return
	 */
	public IExpr getLHS() {
		return fLhsPatternExpr;
	}

	/**
	 * Get the "right-hand-side" of a pattern-matching rule.
	 * 
	 * @return <code>null</code> if no right-hand-side is defined for the pattern matcher
	 */
	public IExpr getRHS() {
		return null;
	}

	/**
	 * Returns the matched pattern in the order they appear in the pattern expression.
	 * 
	 * 
	 * @param resultList
	 *            a list instance
	 * @param patternExpr
	 *            the expression which contains the pattern objects
	 */
	public abstract void getPatterns(List<IExpr> resultList, IExpr patternExpr);

	/**
	 * Get the priority of this pattern-matcher. Lower values have higher priorities.
	 * 
	 * @return the priority
	 */
	public abstract int getPriority();
	
	/**
	 * Check if the pattern-matchings left-hand-side expression contains no patterns.
	 * 
	 * @return <code>true</code>, if the given expression contains no patterns
	 */
	public abstract boolean isRuleWithoutPatterns();

	/**
	 * Start pattern matching.
	 * 
	 * @param expr
	 * @return <code>true</code> if the <code>expr</code> matches the pattern-matchings left-hand-side expression.
	 */
	public abstract boolean apply(IExpr expr);

	/**
	 * Match the given left-hand-side and return an evaluated expression
	 * 
	 * @param expr
	 * @return <code>null</code> if the match wasn't successful, the evaluated expression otherwise.
	 */
	public abstract IExpr eval(final IExpr expr);

	@Override
	public Object clone() throws CloneNotSupportedException {
		IPatternMatcher v = (IPatternMatcher) super.clone();
		v.fLhsPatternExpr = fLhsPatternExpr;
		return v;
	}
}
