package org.matheclipse.core.patternmatching.hash;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.BinaryFunctorImpl;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherEvalEngine;

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

	@Override
	public IExpr evalDownRule(IExpr e1, IExpr num1, IExpr e2, IExpr num2, EvalEngine engine) {
		PatternMatcher pm1 = new PatternMatcherEvalEngine(fLHSPattern1, engine);
		PatternMatcher pm2 = new PatternMatcherEvalEngine(fLHSPattern2, engine);
		if (pm1.test(e1, engine) && pm2.test(e2, engine)) {
			IExpr v1 = pm1.getPatternValue0();
			IExpr v2 = pm2.getPatternValue0();
			return function.apply(v1, v2);
		}
		return F.NIL;
	}

}
