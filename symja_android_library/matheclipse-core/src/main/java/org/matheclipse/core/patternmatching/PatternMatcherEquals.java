package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.util.List;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Matches a given expression by simply comparing the left-hand-side expression of this pattern matcher with the
 * <code>equals()</code> method.
 * 
 */
public class PatternMatcherEquals extends IPatternMatcher implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3566534441225675728L;

	/**
	 * Contains the "pattern-matching" expression
	 * 
	 */
	// protected IExpr fLhsPatternExpr;

	protected IExpr fRightHandSide;

	/**
	 * Contains the "set" symbol used to define this pattern matcher
	 * 
	 */
	protected ISymbol fSetSymbol;

	/**
	 * 
	 * @param setSymbol
	 *            the symbol which defines this pattern-matching rule (i.e. Set, SetDelayed,...)
	 * @param leftHandSide
	 *            could contain pattern expressions for "pattern-matching"
	 * @param rightHandSide
	 *            the result which should be evaluated if the "pattern-matching" succeeds
	 */
	public PatternMatcherEquals(final ISymbol setSymbol, final IExpr leftHandSide, final IExpr rightHandSide) {
		super(leftHandSide);
		fSetSymbol = setSymbol;
		fRightHandSide = rightHandSide;
	}

	@Override
	public boolean apply(IExpr lhsEvalExpr) {
		return fLhsPatternExpr.equals(lhsEvalExpr);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		PatternMatcherEquals v = (PatternMatcherEquals) super.clone();
		v.fRightHandSide = fRightHandSide;
		v.fSetSymbol = fSetSymbol;
		return v;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PatternMatcherEquals) {
			return fLhsPatternExpr.equals(((PatternMatcherEquals) obj).fLhsPatternExpr);
		}
		return super.equals(obj);
	}

	@Override
	public IExpr eval(IExpr lhsEvalExpr) {
		if (apply(lhsEvalExpr)) {
			return fRightHandSide;
		}
		return null;
	}

	@Override
	public void getPatterns(List<IExpr> resultList, IExpr patternExpr) {
	}

	/** {@inheritDoc} */
	@Override
	public IExpr getRHS() {
		return fRightHandSide;
	}

	public ISymbol getSetSymbol() {
		return fSetSymbol;
	}

	@Override
	public int hashCode() {
		return fLhsPatternExpr.hashCode();
	}

	@Override
	public boolean isRuleWithoutPatterns() {
		return true;
	}

	public void setRHS(IExpr rightHandSide) {
		fRightHandSide = rightHandSide;
	}

}
