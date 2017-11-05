package org.matheclipse.core.patternmatching;

import javax.annotation.Nonnull;

import org.matheclipse.combinatoric.IStepVisitor;
import org.matheclipse.combinatoric.MultisetPartitionsIterator;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher.StackMatcher;

/**
 * This visitor is used in an <code>MultisetPartitionsIterator</code> to match orderless expressions in pattern
 * matching.
 * 
 * @see PatternMatcher
 * @see MultisetPartitionsIterator
 */
public class OrderlessStepVisitor extends FlatOrderlessStepVisitor implements IStepVisitor {

	/**
	 * This visitor is used in an <code>MultisetPartitionsIterator</code> to match orderless expressions in pattern
	 * matching. The <code>lhsPatternAST.size()</code> must be equal to <code>lhsEvalAST.size()</code>.
	 * 
	 * @see PatternMatcher
	 * @see MultisetPartitionsIterator
	 */
	public OrderlessStepVisitor(final ISymbol sym, IAST lhsPatternAST, IAST lhsEvalAST, StackMatcher stackMatcher,
			PatternMap patternMap, boolean oneIdentity) {
		super(sym, lhsPatternAST, lhsEvalAST, stackMatcher, patternMap, oneIdentity);
	}

	@Override
	protected boolean matchSinglePartition(int[][] result, @Nonnull StackMatcher stackMatcher) {
		// IAST partitionElement;
		// if (Config.SHOW_STACKTRACE == true) {
		// }
		int lastStackSize = stackMatcher.size();
		boolean matched = true;
		try {

			for (int j = 0; j < result.length; j++) {
				final int n = result[j].length;
				if (n == 1) {
					if (fOneIdentity) {
						if (!stackMatcher.push(fLhsPatternAST.get(j + 1), (IExpr) array[result[j][0]])) {
							matched = false;
							return false;
						}
					} else {
						if (!stackMatcher.push(fLhsPatternAST.get(j + 1),
								F.unaryAST1(fSymbol, (IExpr) array[result[j][0]]))) {
							matched = false;
							return false;
						}
					}
				} else {
					throw new WrongNumberOfArguments((IAST) list, 1, n);
					// } else {
					// partitionElement = F.ast(fSymbol, n, false);
					// for (int i = 0; i < n; i++) {
					// partitionElement.add((IExpr) array[result[j][i]]);
					// }
					// if (!stackMatcher.push(fLhsPatternAST.get(j + 1),
					// partitionElement)) {
					// matched = false;
					// return false;
					// }
				}
			}

			if (stackMatcher != null && !stackMatcher.matchRest()) {
				matched = false;
				return false;
			}
			return true;
		} finally {
			if (!matched) {
				stackMatcher.removeFrom(lastStackSize);
			}
		}
	}
}
