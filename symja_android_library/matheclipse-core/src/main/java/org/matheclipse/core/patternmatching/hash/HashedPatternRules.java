package org.matheclipse.core.patternmatching.hash;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.ExprUtil;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.RulesData;

/**
 * Data structure for <code>HashedOrderlessMatcher</code>.
 * 
 * To set up a rule like<br/>
 * <code>Sin[x]^2+Cos[x]^2 -> 1</code> <bR/>
 * use the method:<br/>
 * <code>HashedPatternRules(F.Sin(F.x_)^F.C2, F.Cos(F.x_)^F.C2, F.C1)</code>
 * 
 */
public class HashedPatternRules extends AbstractHashedPatternRules {

	final IExpr fCondition;

	final IExpr fRHS;

	/**
	 * 
	 * @param lhsPattern1
	 *            first left-hand-side pattern
	 * @param lhsPattern2
	 *            second left-hand-side pattern
	 * @param rhsResult
	 *            the right-hand-side result
	 * @param defaultHashCode
	 */
	// public HashedPatternRules(IExpr lhsPattern1, IExpr lhsPattern2, IExpr
	// rhsResult, boolean defaultHashCode) {
	// this(lhsPattern1, lhsPattern2, rhsResult, null, defaultHashCode);
	// }

	/**
	 * 
	 * @param lhsPattern1
	 *            first left-hand-side pattern
	 * @param lhsPattern2
	 *            second left-hand-side pattern
	 * @param rhsResult
	 *            the right-hand-side result
	 * @param condition
	 *            a condition test
	 * @param defaultHashCode
	 *            TODO
	 */
	public HashedPatternRules(IExpr lhsPattern1, IExpr lhsPattern2, IExpr rhsResult, IExpr condition,
			boolean defaultHashCode) {
		super(lhsPattern1, lhsPattern2, defaultHashCode);
		fCondition = condition;
		fRHS = rhsResult;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof HashedPatternRules) {
			HashedPatternRules other = (HashedPatternRules) obj;
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
			if (fCondition == null) {
				if (other.fCondition != null) {
					return false;
				}
			} else if (!fCondition.equals(other.fCondition)) {
				return false;
			}
			if (fRHS == null) {
				if (other.fRHS != null) {
					return false;
				}
			} else if (!fRHS.equals(other.fRHS)) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * @return the right-hand-side result
	 */
	public IExpr getRHS() {
		return ExprUtil.ofNullable(fRHS);
	}

	/**
	 * Get the Condition for this rule.
	 * 
	 * @return may return <code>null</code>.
	 */
	public IExpr getCondition() {
		return fCondition;
	}

	/**
	 * Get (or create) the rule
	 * <code>{&lt;first-left-hand-side&gt;, &lt;second-left-hand-side&gt;}:=&lt;right-hand-side&gt;</code>
	 * 
	 * @return
	 */
	public RulesData getRulesData() {
		if (fRulesData == null) {
			fRulesData = new RulesData(Context.SYSTEM);
			if (fCondition != null) {
				fRulesData.putDownRule(ISymbol.RuleType.SET_DELAYED, false, F.List(fLHSPattern1, fLHSPattern2),
						F.Condition(fRHS, fCondition));
			} else {
				fRulesData.putDownRule(ISymbol.RuleType.SET_DELAYED, false, F.List(fLHSPattern1, fLHSPattern2), fRHS);
			}
		}
		return fRulesData;
	}

	@Override
	public IExpr evalDownRule(IExpr e1, IExpr num1, IExpr e2, IExpr num2, EvalEngine engine) {
		return getRulesData().evalDownRule(F.List(e1, e2), engine);
	}

}
