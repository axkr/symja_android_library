package org.matheclipse.core.patternmatching;

import org.matheclipse.core.generic.BinaryFunctorImpl;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Data structure for <code>HashedOrderlessMatcher</code> to set up a rule with a <code>BinaryFunctorImpl<IExpr></code> evaluation
 * implementation
 * 
 */
public class HashedPatternFunction extends AbstractHashedPatternRules {
	final BinaryFunctorImpl<IExpr> function;

	/**
	 * 
	 * @param lhsPattern1
	 *            first left-hand-side pattern
	 * @param lhsPattern2
	 *            second left-hand-side pattern
	 * @param function
	 *            the binary function to call for the arguments
	 * @param defaultHashCode
	 */
	public HashedPatternFunction(IExpr lhsPattern1, IExpr lhsPattern2, BinaryFunctorImpl<IExpr> function, boolean defaultHashCode) {
		super(lhsPattern1, lhsPattern2, defaultHashCode);
		this.function = function;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof HashedPatternFunction) {
			HashedPatternFunction other = (HashedPatternFunction) obj;
			if (hash1 != other.hash1) {
				return false;
			}
			if (hash2 != other.hash2) {
				return false;
			}
			if (fLHSPattern1 == null) {
				if (other.fLHSPattern1 != null) {
					return false;
				}
			} else if (!fLHSPattern1.equals(other.fLHSPattern1)) {
				return false;
			}
			if (fLHSPattern2 == null) {
				if (other.fLHSPattern2 != null) {
					return false;
				}
			} else if (!fLHSPattern2.equals(other.fLHSPattern2)) {
				return false;
			}
			return true;
		}
		return false;
	}

	public BinaryFunctorImpl<IExpr> getFunction() {
		return function;
	}

	@Override
	public IExpr evalDownRule(IExpr e1, IExpr e2) {
		PatternMatcher pm1 = new PatternMatcher(fLHSPattern1);
		PatternMatcher pm2 = new PatternMatcher(fLHSPattern2);
		if (pm1.test(e1) && pm2.test(e2)) {
			IExpr v1 = pm1.getPatternValue0();
			IExpr v2 = pm2.getPatternValue0();
			return function.apply(v1, v2);
		}
		return null;
	}

}
