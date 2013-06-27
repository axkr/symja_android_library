package org.matheclipse.core.interfaces;

import java.util.List;

import com.google.common.base.Predicate;

/**
 * Interface for the pattern matcher
 */
public abstract class IPatternMatcher<E> implements Predicate<E>, Cloneable {
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
	public abstract void getPatterns(List<E> resultList, E patternExpr);

	/**
	 * Returns <code>true</code>, if the given expression contains no patterns
	 * 
	 * @return
	 */
	public abstract boolean isRuleWithoutPatterns();

	/**
	 * Start pattern matching.
	 * 
	 * @return
	 */
	public abstract boolean apply(E evalExpr);

	/**
	 * Match the given left-hand-side and return an evaluated expression
	 * 
	 * @param leftHandSide
	 * @return <code>null</code> if the match wasn't successful, the evaluated
	 *         expression otherwise.
	 */
	public abstract IExpr eval(final IExpr leftHandSide);

	/**
	 * Check if this matchers pattern values equals the pattern values in the
	 * given <code>thatMatcher</code> for the same pattern symbols.
	 * 
	 * @param thatMatcher
	 * @return
	 */
	// public abstract boolean checkPatternMatcher(final PatternMatcher
	// thatMatcher);

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}
}
