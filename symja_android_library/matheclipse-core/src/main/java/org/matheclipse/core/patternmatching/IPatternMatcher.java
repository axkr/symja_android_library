package org.matheclipse.core.patternmatching;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Interface for the pattern matcher
 */
public abstract class IPatternMatcher implements Predicate<IExpr>, Cloneable, Serializable {// Comparable<IPatternMatcher>,

	public final static int NOFLAG = 0x0000;
	
	/**
	 * This rule is defined with the <code>Set[]</code>  function
	 */
	public final static int SET = 0x0001;
	
	/**
	 * This rule is defined with the <code>SetDelayed[]</code>  function
	 */
	public final static int SET_DELAYED = 0x0002;
	
	/**
	 * This rule is defined with the <code>Set[]</code>  function
	 */
	public final static int TAGSET = 0x0004;
	
	/**
	 * This rule is defined with the <code>Set[]</code>  function
	 */
	public final static int TAGSET_DELAYED = 0x0008;
	
	/**
	 * This rule is defined with the <code>Set[]</code>  function
	 */
	public final static int UPSET = 0x0010;
	
	/**
	 * This rule is defined with the <code>Set[]</code>  function
	 */
	public final static int UPSET_DELAYED = 0x0020;
	
	/**
	 * This rules left-hand-side is wrapped with a <code>Literal[]</code>  function
	 */
	public final static int LITERAL = 0x1000;
	
	/**
	 * This rules left-hand-side is wrapped with a <code>HoldPattern[]</code>  function
	 */
	public final static int HOLDPATTERN = 0x2000;
	
	/**
	 * Serialization mask
	 */
	public final static int SERIALIZATION_MASK = 0x8000;
	
	public static class EquivalenceComparator implements Comparator<IPatternMatcher>, Serializable {

		private static final long serialVersionUID = 8357661139299702326L;

		@Override
		public int compare(final IPatternMatcher o1, final IPatternMatcher o2) {
			if (o1 == o2) {
				return 0;
			}
			return o1.equivalentTo(o2);
		}
	}

	public static class PriorityComparator implements Comparator<IPatternMatcher> {
		@Override
		public int compare(IPatternMatcher o1, IPatternMatcher o2) {
			return o1.getLHSPriority() < o2.getLHSPriority() ? -1 : o1.getLHSPriority() > o2.getLHSPriority() ? 1 : 0;
		}
	}

	// Serializable {
	public final static EquivalenceComparator EQUIVALENCE_COMPARATOR = new EquivalenceComparator();

	public final static PriorityComparator PRIORITY_COMPARATOR = new PriorityComparator();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2841686297882535691L;

	/**
	 * Contains the "pattern-matching" expression
	 * 
	 */
	protected IExpr fLhsPatternExpr;

	protected IPatternMatcher() {
		fLhsPatternExpr = null;
	}

	public IPatternMatcher(IExpr lhsPatternExpr) {
		fLhsPatternExpr = lhsPatternExpr;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		IPatternMatcher v = (IPatternMatcher) super.clone();
		v.fLhsPatternExpr = fLhsPatternExpr;
		return v;
	}

//	public int determinePatterns() {
//		return PatternMap.DEFAULT_RULE_PRIORITY;
//	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IPatternMatcher other = (IPatternMatcher) obj;
		if (fLhsPatternExpr == null) {
			if (other.fLhsPatternExpr != null)
				return false;
		} else if (!fLhsPatternExpr.equals(other.fLhsPatternExpr))
			return false;
		return true;
	}

	/**
	 * Compare only the left-hand-side expressions in the matchers for equivalence
	 * 
	 * @param obj
	 * @return
	 */
	public abstract int equivalentLHS(final IPatternMatcher obj);

	public abstract int equivalentTo(IPatternMatcher patternMatcher);

	/**
	 * Match the given left-hand-side and return an evaluated expression
	 * 
	 * @param leftHandSide
	 *            left-hand-side expression
	 * @param engine
	 * @return <code>F.NIL</code> if the match wasn't successful, the evaluated expression otherwise.
	 */
	public abstract IExpr eval(final IExpr leftHandSide, @Nonnull EvalEngine engine);

	/**
	 * Get the "left-hand-side" of a pattern-matching rule.
	 * 
	 * @return
	 */
	public IExpr getLHS() {
		return fLhsPatternExpr;
	}

	/**
	 * Get the priority of the left-and-side of this pattern-matcher. Lower values have higher priorities.
	 * 
	 * @return the priority
	 */
	public abstract int getLHSPriority();

	public abstract int getPatternHash();

	/**
	 * Returns the matched pattern in the order they appear in the pattern expression.
	 * 
	 * 
	 * @param resultList
	 *            a list instance
	 * @param patternExpr
	 *            the expression which contains the pattern objects
	 */
	public abstract void getPatterns(List<IExpr> resultList, IExpr patternExpr);

	/**
	 * Get the "right-hand-side" of a pattern-matching rule.
	 * 
	 * @return <code>F.NIL</code> if no right-hand-side is defined for the pattern matcher
	 */
	public IExpr getRHS() {
		return F.NIL;
	}

	/**
	 * Get the priority of the left-and-side of this pattern-matcher. Lower values have higher priorities.
	 * 
	 * @return the priority
	 */
	public long getRHSleafCountSimplify() {
		return Long.MAX_VALUE;
	}

	@Override
	public int hashCode() {
		return fLhsPatternExpr.hashCode();
	}

	/**
	 * Check if <code>fPatterHash == 0 || fPatterHash == patternHash;</code>.
	 * 
	 * @param patternHash
	 * @return
	 */
	public abstract boolean isPatternHashAllowed(int patternHash);

	/**
	 * Check if the pattern-matchings left-hand-side expression contains no patterns.
	 * 
	 * @return <code>true</code>, if the given expression contains no patterns
	 */
	public abstract boolean isRuleWithoutPatterns();

	/**
	 * Start pattern matching.
	 * 
	 * @param expr
	 * @return <code>true</code> if the <code>expr</code> matches the pattern-matchings left-hand-side expression.
	 */
	@Override
	public abstract boolean test(IExpr expr);

	/**
	 * Start pattern matching.
	 * 
	 * @param expr
	 * @return <code>true</code> if the <code>expr</code> matches the pattern-matchings left-hand-side expression.
	 */
	public abstract boolean test(IExpr expr, EvalEngine engine);
}
