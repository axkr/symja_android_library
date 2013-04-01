package org.matheclipse.core.patternmatching;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.HashValueVisitor;

/**
 * Data structure for <code>HashedOrderlessMatcher</code>.
 * 
 * To set up a rule like<br/>
 * <code>Sin[x]^2+Cos[x]^2 -> 1</code> <bR/>
 * use the method:<br/>
 * <code>setUpHashRule("Sin[x_]^2", "Cos[x_]^2", "1")</code>
 * 
 */
public class HashedPatternRules {
	private int hash1;
	private int hash2;
	private RulesData fRulesData = null;
	private final IExpr fLHSPattern1;
	private final IExpr fLHSPattern2;
	// private final IExpr fCondition;
	private final IExpr fRHS;

	/**
	 * 
	 * @param lhsPattern1
	 *          first left-hand-side pattern
	 * @param lhsPattern2
	 *          second left-hand-side pattern
	 * @param rhsResult
	 *          the right-hand-side result
	 * @param condition
	 *          a condition test
	 * @param defaultHashCode
	 *          TODO
	 */
	public HashedPatternRules(IExpr lhsPattern1, IExpr lhsPattern2, IExpr rhsResult, boolean defaultHashCode) {
		fLHSPattern1 = lhsPattern1;
		fLHSPattern2 = lhsPattern2;
		// fCondition = condition;
		fRHS = rhsResult;
		if (defaultHashCode) {
			hash1 = lhsPattern1.head().hashCode();
			hash2 = lhsPattern2.head().hashCode();
		} else {
			HashValueVisitor v = new HashValueVisitor();
			hash1 = lhsPattern1.accept(v);
			v.setUp();
			hash2 = lhsPattern2.accept(v);
			v.setUp();
		}
	}

	/**
	 * @return the right-hand-side result
	 */
	public IExpr getRHS() {
		return fRHS;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hash1;
		result = prime * result + hash2;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof HashedPatternRules) {
			HashedPatternRules other = (HashedPatternRules) obj;
			if (hash1 != other.hash1)
				return false;
			if (hash2 != other.hash2)
				return false;
			if (fLHSPattern1 == null) {
				if (other.fLHSPattern1 != null)
					return false;
			} else if (!fLHSPattern1.equals(other.fLHSPattern1))
				return false;
			if (fLHSPattern2 == null) {
				if (other.fLHSPattern2 != null)
					return false;
			} else if (!fLHSPattern2.equals(other.fLHSPattern2))
				return false;
			// if (fCondition == null) {
			// if (other.fCondition != null)
			// return false;
			// } else if (!fCondition.equals(other.fCondition))
			// return false;
			if (fRHS == null) {
				if (other.fRHS != null)
					return false;
			} else if (!fRHS.equals(other.fRHS))
				return false;
			return true;
		}
		return false;
	}

	/**
	 * Get the hash value for the first LHS expression.
	 * 
	 * @return the hash1
	 */
	public int getHash1() {
		return hash1;
	}

	/**
	 * Get the hash value for the second LHS expression.
	 * 
	 * @return the hash2
	 */
	public int getHash2() {
		return hash2;
	}

	public boolean isPattern2() {
		return fLHSPattern2.isPattern();
	}

	/**
	 * Get (or create) the rule
	 * <code>{&lt;first-left-hand-side&gt;, &lt;second-left-hand-side&gt;}:=&lt;right-hand-side&gt;</code>
	 * 
	 * @return
	 */
	public RulesData getRulesData() {
		if (fRulesData == null) {
			fRulesData = new RulesData();
			fRulesData.putDownRule(F.SetDelayed, false, F.List(fLHSPattern1, fLHSPattern2), fRHS, 0);
		}
		return fRulesData;
	}
}
