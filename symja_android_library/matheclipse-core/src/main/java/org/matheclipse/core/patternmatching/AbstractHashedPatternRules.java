package org.matheclipse.core.patternmatching;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.HashValueVisitor;

public abstract class AbstractHashedPatternRules {

	protected int hash1;
	protected int hash2;
	protected DownRulesData fRulesData = null;
	protected final IExpr fLHSPattern1;
	protected final IExpr fLHSPattern2;

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
	public AbstractHashedPatternRules(IExpr lhsPattern1, IExpr lhsPattern2, boolean defaultHashCode) {
		fLHSPattern1 = lhsPattern1;
		fLHSPattern2 = lhsPattern2;
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

	public abstract IExpr evalDownRule(IExpr e1, IExpr e2);
}