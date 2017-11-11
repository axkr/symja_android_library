package org.matheclipse.core.eval.util;

import javax.annotation.Nonnull;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Table structure generator (i.e. lists, vectors, matrices, tensors)
 */
public class IndexTableGenerator {
	final int[] fIndexArray;

	final ISymbol fHead;

	final IIndexFunction<? extends IExpr> fFunction;

	int fIndex;

	int[] fCurrentIndex;

	/**
	 * 
	 * @param indexArray
	 * @param head
	 *            the head of the resulting lists
	 * @param function
	 */
	public IndexTableGenerator(final int[] indexArray, final ISymbol head,
			final IIndexFunction<? extends IExpr> function) {
		fIndexArray = indexArray;
		fHead = head;
		fFunction = function;
		fIndex = 0;
		fCurrentIndex = new int[indexArray.length];
	}

	@Nonnull
	public IExpr table() {
		if (fIndex < fIndexArray.length) {
			final int iter = fIndexArray[fIndex];
			final int index = fIndex++;
			try {
				final IASTAppendable result = F.ast(fHead, iter, false);
				for (int i = 0; i < iter; i++) {
					fCurrentIndex[index] = i;
					result.append(table());
				}
				return result;
			} finally {
				--fIndex;
			}
		}
		return fFunction.evaluate(fCurrentIndex);
	}
}
