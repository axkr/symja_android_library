package org.matheclipse.core.patternmatching;

import java.util.List;

import org.matheclipse.core.interfaces.IExpr;

import com.google.common.base.Predicate;

/**
 * Interface for the pattern matcher
 */
public abstract class IPatternMatcher implements Predicate<IExpr>, Cloneable {

	/**
	 * Contains the "pattern-matching" expression
	 * 
	 */
	protected IExpr fLhsPatternExpr;

	protected IPatternMatcher() {
		fLhsPatternExpr= null;
	}
	
	public IPatternMatcher(IExpr lhsPatternExpr) {
		fLhsPatternExpr= lhsPatternExpr;
	}

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
	public abstract boolean apply(IExpr evalExpr);

	/**
	 * Match the given left-hand-side and return an evaluated expression
	 * 
	 * @param leftHandSide
	 * @return <code>null</code> if the match wasn't successful, the evaluated
	 *         expression otherwise.
	 */
	public abstract IExpr eval(final IExpr leftHandSide);

	@Override
	public Object clone() throws CloneNotSupportedException {
//		try {
			IPatternMatcher v = (IPatternMatcher) super.clone();
			v.fLhsPatternExpr = fLhsPatternExpr;
			return v;
//		} catch (CloneNotSupportedException e) {
//			// this shouldn't happen, since we are Cloneable
//			throw new InternalError();
//		}
	}
}
