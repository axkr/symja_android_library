package org.matheclipse.core.patternmatching;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.visit.HashValueVisitor;

public abstract class AbstractHashedPatternRules {

	protected int hash1;
	protected int hash2;
	protected int hashSum;
	protected RulesData fRulesData = null;
	protected final IExpr fLHSPattern1;
	protected final IExpr fLHSPattern2;

	/**
	 * 
	 * @param lhsPattern1
	 *            first left-hand-side pattern
	 * @param lhsPattern2
	 *            second left-hand-side pattern
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
		if (hashSum == 0) {
			hashSum = calculateHashcode(hash1, hash2);
		}
		return hashSum;
	}

	/**
	 * Symmetric hash code.
	 * 
	 * @param h1
	 * @param h2
	 * @return
	 */
	public static int calculateHashcode(int h1, int h2) {
		return 31 * (h1 + h2);
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

	/**
	 * Test if the first left-hand-side is a pattern object
	 * 
	 */
	public boolean isPattern1() {
		return fLHSPattern1.isPattern();
	}

	/**
	 * Test if the second left-hand-side is a pattern object
	 * 
	 */
	public boolean isPattern2() {
		return fLHSPattern2.isPattern();
	}

	public abstract IExpr evalDownRule(IExpr e1, IExpr e2);
}