package org.matheclipse.core.patternmatching;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public abstract class AbstractPatternMatcherMethod extends PatternMatcher {

	/**
	 * Define a pattern-matching rule.
	 * 
	 * @param leftHandSide
	 *            could contain pattern expressions for "pattern-matching"
	 */
	public AbstractPatternMatcherMethod(final IExpr leftHandSide) {
		super(leftHandSide);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		AbstractPatternMatcherMethod v = (AbstractPatternMatcherMethod) super.clone();
		return v;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof AbstractPatternMatcherMethod) {
			AbstractPatternMatcherMethod pm = (AbstractPatternMatcherMethod) obj;
			return super.equals(pm);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() * 73;
	}

	abstract IExpr evalMethod();

	/** {@inheritDoc} */
	@Override
	public IExpr eval(final IExpr leftHandSide, EvalEngine engine) {
		if (isRuleWithoutPatterns()) {
			// no patterns found match equally:
			if (fLhsPatternExpr.equals(leftHandSide)) {
				try {
					return evalMethod();
				} catch (final ConditionException e) {
					return F.NIL;
				} catch (final ReturnException e) {
					return e.getValue();
				}
			}
			if (!(fLhsPatternExpr.isOrderlessAST() && leftHandSide.isOrderlessAST())) {
				if (!(fLhsPatternExpr.isFlatAST() && leftHandSide.isFlatAST())) {
					return F.NIL;
				}
			}
		}

		PatternMap patternMap=getPatternMap();
		patternMap.initPattern();
		if (matchExpr(fLhsPatternExpr, leftHandSide, engine)) {
			try {
				return evalMethod();
			} catch (final ConditionException e) {
				return F.NIL;
			} catch (final ReturnException e) {
				return e.getValue();
			}
		}
		return F.NIL;
	}

	@Override
	public IExpr getRHS() {
		return F.NIL;
	}

//	@Override
//	public int equivalent(final IPatternMatcher obj) {
//		// don't compare fSetSymbol here
//		int comp = super.equivalent(obj);
//		if (comp == 0) {
//			if (obj instanceof AbstractPatternMatcherMethod) {
//				// AbstractPatternMatcherMethod pm = (AbstractPatternMatcherMethod) obj;
//				// if (fRightHandSide != null) {
//				// if (fRightHandSide.isCondition()) {
//				// if (pm.fRightHandSide.isCondition()) {
//				// if (equivalentRHS(fRightHandSide.getAt(2),
//				// pm.fRightHandSide.getAt(2), fPatternMap,
//				// pm.fPatternMap)) {
//				// return 0;
//				// }
//				// return 1;
//				// }
//				// return -1;
//				// } else if (pm.fRightHandSide.isCondition()) {
//				// return 1;
//				// } else if (fRightHandSide.isModule()) {
//				// if (pm.fRightHandSide.isModule()) {
//				// if (equivalentRHS(fRightHandSide.getAt(2),
//				// pm.fRightHandSide.getAt(2), fPatternMap,
//				// pm.fPatternMap)) {
//				// return 0;
//				// }
//				// return 1;
//				// }
//				// return -1;
//				// } else if (pm.fRightHandSide.isModule()) {
//				// return 1;
//				// }
//				// }
//				return 0;
//			}
//		}
//		return comp;
//	}

}