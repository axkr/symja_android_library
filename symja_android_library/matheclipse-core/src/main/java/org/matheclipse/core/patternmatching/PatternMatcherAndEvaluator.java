package org.matheclipse.core.patternmatching;

import java.io.Serializable;

import org.matheclipse.core.builtin.function.Condition;
import org.matheclipse.core.builtin.function.Module;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class PatternMatcherAndEvaluator extends PatternMatcher implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2241135467123931061L;

	private IExpr fRightHandSide;
	private ISymbol fSetSymbol;

	/**
	 * 
	 * @param setSymbol
	 *            the symbol which defines this pattern-matching rule (i.e. Set,
	 *            SetDelayed,...)
	 * @param leftHandSide
	 *            could contain pattern expressions for "pattern-matching"
	 * @param rightHandSide
	 *            the result which should be evaluated if the "pattern-matching"
	 *            succeeds
	 */
	public PatternMatcherAndEvaluator(final ISymbol setSymbol, final IExpr leftHandSide, final IExpr rightHandSide) {
		super(leftHandSide);
		fSetSymbol = setSymbol;
		fRightHandSide = rightHandSide;
	}

	@Override
	public Object clone() {
		PatternMatcherAndEvaluator v = (PatternMatcherAndEvaluator) super.clone();
		v.fRightHandSide = fRightHandSide;
		v.fSetSymbol = fSetSymbol;
		return v;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof PatternMatcherAndEvaluator) {
			PatternMatcherAndEvaluator pm = (PatternMatcherAndEvaluator) obj;
			// don't compare fSetSymbol here
			if (super.equals(pm)) {
				// return equivalentRHS(fRightHandSide, pm.fRightHandSide);
				if (fRightHandSide.isCondition()) {
					if (pm.fRightHandSide.isCondition()) {
						return equivalentRHS(fRightHandSide.getAt(2), pm.fRightHandSide.getAt(2), fPatternMap, pm.fPatternMap);
					}
					return false;
				} else if (pm.fRightHandSide.isCondition()) {
					return false;
				} else if (fRightHandSide.isModule()) {
					if (pm.fRightHandSide.isModule()) {
						return equivalentRHS(fRightHandSide.getAt(2), pm.fRightHandSide.getAt(2), fPatternMap, pm.fPatternMap);
					}
					return false;
				} else if (pm.fRightHandSide.isModule()) {
					return false;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the two expressions are equivalent. (i.e. <code>f[x_,y_]</code>
	 * is equivalent to <code>f[a_,b_]</code> )
	 * 
	 * @param patternExpr1
	 * @param patternExpr2
	 * @param pm1
	 *            TODO
	 * @param pm2
	 *            TODO
	 * @return
	 */
	private static boolean equivalentRHS(final IExpr patternExpr1, final IExpr patternExpr2, final PatternMap pm1,
			final PatternMap pm2) {
		if (patternExpr1.isCondition()) {
			if (patternExpr2.isCondition()) {
				return equivalentRHS(patternExpr1.getAt(2), patternExpr2.getAt(2), pm1, pm2);
			}
			return false;
		} else if (patternExpr2.isCondition()) {
			return false;
		} else if (patternExpr1.isModule()) {
			if (patternExpr2.isModule()) {
				return equivalentRHS(patternExpr1.getAt(2), patternExpr2.getAt(2), pm1, pm2);
			}
			return false;
		} else if (patternExpr2.isModule()) {
			return false;
		}
		// TODO refine equivalent for RHS symbols which are patterns on the LHS.
		return equivalent(patternExpr1, patternExpr2, pm1, pm2);
	}

	@Override
	public int hashCode() {
		return super.hashCode() * 53;
	}

	/**
	 * Check if the condition for the right-hand-sides
	 * <code>Module[] or Condition[]</code> expressions evaluates to
	 * <code>true</code>.
	 * 
	 * @return <code>true</code> if the right-hand-sides condition is fulfilled.
	 */
	@Override
	public boolean checkRHSCondition(EvalEngine engine) {
		if (!(fRightHandSide.isModule() || fRightHandSide.isCondition())) {
			return true;
		}
		if (!fPatternMap.isAllPatternsAssigned()) {
			return true;
		}
		IExpr substConditon = fPatternMap.substitutePatternSymbols(fRightHandSide);
		if (substConditon.isCondition()) {
			return Condition.checkCondition(substConditon.getAt(1), substConditon.getAt(2), engine);
		} else if (substConditon.isModule()) {
			return Module.checkModuleCondition(substConditon.getAt(1), substConditon.getAt(2), engine);
		}
		return true;
	}

	/**
	 * Match the (left-hand-side) pattern with the given expression. If true
	 * evaluate the right-hand-side for the determined values of the patterns
	 * 
	 * @param ee
	 * @param evalExpr
	 * @return
	 */
	@Override
	public IExpr eval(final IExpr lhsEvalExpr) {
		// if(fRightHandSide.isAST("Condition")) {
		// System.out.println("2:"+fRightHandSide);
		// }
		if (isRuleWithoutPatterns()) {
			// no patterns found match equally:
			if (fLhsPatternExpr.equals(lhsEvalExpr)) {
				IExpr result = fRightHandSide;
				try {
					IExpr temp = F.eval(result);
					if (temp != null) {
						return temp;
					}
					return result;
				} catch (final ConditionException e) {
					return null;
				} catch (final ReturnException e) {
					return e.getValue();
				}
			}
			if (!(fLhsPatternExpr.isOrderlessAST() && lhsEvalExpr.isOrderlessAST())) {
				if (!(fLhsPatternExpr.isFlatAST() && lhsEvalExpr.isFlatAST())) {
					return null;
				}
				// TODO implement equals matching for special cases, if the AST
				// is
				// Orderless or Flat
			}
		} 
		fPatternMap.initPattern();

		if (fLhsPatternExpr.isAST() && lhsEvalExpr.isAST()) {
			IExpr result = evalAST((IAST) fLhsPatternExpr, (IAST) lhsEvalExpr, fRightHandSide, new StackMatcher());
			if (result != null) {
				return result;
			}
		}

		fPatternMap.initPattern();
		if (matchExpr(fLhsPatternExpr, lhsEvalExpr)) {
			IExpr result = fPatternMap.substitutePatternSymbols(fRightHandSide);
			try {
				result = F.eval(result);
			} catch (final ConditionException e) {
				// if (lhsEvalExpr.isAST(F.Integrate)) {
				// System.out.println(fLhsPatternExpr.toString());
				// System.out.println(lhsEvalExpr.toString() + "  :> " +
				// result.toString());
				// }
				return null;
			} catch (final ReturnException e) {
				result = e.getValue();
			}
			// if (lhsEvalExpr.isAST(F.Integrate)) {
			// System.out.println(fLhsPatternExpr.toString());
			// System.out.println(lhsEvalExpr.toString() + "  :> " +
			// result.toString());
			// }
			return result;
		}
		return null;
	}

	public IExpr getRHS() {
		return fRightHandSide;
	}

	public ISymbol getSetSymbol() {
		return fSetSymbol;
	}

}