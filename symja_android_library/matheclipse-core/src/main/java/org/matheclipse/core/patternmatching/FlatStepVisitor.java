package org.matheclipse.core.patternmatching;

import org.matheclipse.core.combinatoric.AbstractListStepVisitor;
import org.matheclipse.core.combinatoric.NumberPartitionsIterator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher.StackMatcher;

/**
 * This visitor is used in an <code>NumberPartitionsIterator</code> to match flat expressions in pattern matching.
 * 
 * @see PatternMatcher
 * @see NumberPartitionsIterator
 */
public class FlatStepVisitor extends AbstractListStepVisitor<IExpr> {

	protected ISymbol fSymbol;
	protected StackMatcher stackMatcher;
	protected IPatternMap fPatternMap;
	protected IAST fLhsPatternAST;
	protected final boolean fOneIdentity;

	public FlatStepVisitor(final ISymbol sym, IAST lhsPatternAST, IAST lhsEvalAST, StackMatcher stackMatcher,
			IPatternMap patternMap) {
		this(sym, lhsPatternAST, lhsEvalAST, stackMatcher, patternMap, sym.hasOneIdentityAttribute());
	}

	public FlatStepVisitor(final ISymbol sym, IAST lhsPatternAST, IAST lhsEvalAST, StackMatcher stackMatcher,
			IPatternMap patternMap, boolean oneIdentity) {
		super(lhsEvalAST);// , 1, lhsEvalAST.size());
		this.fSymbol = sym;
		this.stackMatcher = stackMatcher;
		this.fPatternMap = patternMap;
		this.fLhsPatternAST = lhsPatternAST;
		this.fOneIdentity = oneIdentity;
	}

	@Override
	public boolean visit(int[][] result) {
		return !matchSinglePartition(result, stackMatcher);
	}

	/**
	 * Match a single partition combination
	 * 
	 * @param result
	 * @param stackMatcher
	 * @return
	 */
	protected boolean matchSinglePartition(int[][] result, StackMatcher stackMatcher) {
		IASTAppendable partitionElement;
		int lastStackSize = stackMatcher.size();
		boolean matched = true;
		IExpr[] patternValues = fPatternMap.copyPattern();
		try {

			for (int j = 0; j < result.length; j++) {
				final int n = result[j].length;
				final IExpr lhsPatternExpr = fLhsPatternAST.get(j + 1);
				if (n == 1 && fOneIdentity) {
					// OneIdentity here
					if (!stackMatcher.push(lhsPatternExpr, array[result[j][0]])) {
						matched = false;
						return false;
					}
				} else {
					ISymbol head = fSymbol;
					if (lhsPatternExpr.isPatternSequence(false)) {
						head = F.Sequence;
					}
					partitionElement = F.ast(head, n, false);
					for (int i = 0; i < n; i++) {
						partitionElement.append(array[result[j][i]]);
					}
					if (!stackMatcher.push(lhsPatternExpr, partitionElement)) {
						matched = false;
						return false;
					}
				}
			}

			if (!stackMatcher.matchRest()) {
				matched = false;
				return false;
			}
			return true;
		} finally {
			if (!matched) {
				stackMatcher.removeFrom(lastStackSize);
				fPatternMap.resetPattern(patternValues);
			}
		}
	}
}
