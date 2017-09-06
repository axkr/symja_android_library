package org.matheclipse.core.patternmatching;

import org.matheclipse.combinatoric.IStepVisitor;
import org.matheclipse.combinatoric.MultisetPartitionsIterator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.PatternMatcher.StackMatcher;

/**
 * This visitor is used in an <code>MultisetPartitionsIterator</code> to match
 * flat and orderless expressions in pattern matching.
 * 
 * @see PatternMatcher
 * @see MultisetPartitionsIterator
 */
public class FlatOrderlessStepVisitor extends FlatStepVisitor implements IStepVisitor {
	protected int[] multiset;

	public FlatOrderlessStepVisitor(final ISymbol sym, IAST lhsPatternAST, IAST lhsEvalAST, StackMatcher stackMatcher,
			PatternMap patternMap) {
		super(sym, lhsPatternAST, lhsEvalAST, stackMatcher, patternMap);
		toIntArray(lhsEvalAST, 1, lhsEvalAST.size());
	}

	public FlatOrderlessStepVisitor(final ISymbol sym, IAST lhsPatternAST, IAST lhsEvalAST, StackMatcher stackMatcher,
			PatternMap patternMap, boolean oneIdentity) {
		super(sym, lhsPatternAST, lhsEvalAST, stackMatcher, patternMap, oneIdentity);
		toIntArray(lhsEvalAST, 1, lhsEvalAST.size());
	}
	
	/**
	 * Convert the <code>sortedList</code> to an <code>int[]</code> array. Equal
	 * elements get the same index in the resulting <code>int[]</code> array.
	 * 
	 * @param <T>
	 * @param sortedList
	 * @param start
	 * @param end
	 * @return
	 */
	final private void toIntArray(IAST sortedList, int start, int end) {
		multiset = new int[end - start];
		array = new Object[end - start];
		IExpr lastT = sortedList.get(start);
		IExpr currentT;
		int index = 0;
		int j = 0;
		multiset[j++] = index;
		array[index] = lastT;
		for (int i = start + 1; i < end; i++) {
			currentT = sortedList.get(i);
			if (currentT.equals(lastT)) {
				multiset[j++] = index;
			} else {
				multiset[j++] = ++index;
				array[index] = currentT;
			}
			lastT = currentT;
		}
	}

	@Override
	public int[] getMultisetArray() {
		return multiset;
	}
}
