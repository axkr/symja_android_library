package org.matheclipse.core.eval.util;

import org.matheclipse.core.interfaces.IExpr;

/**
 * Evaluates to one of two given values. Useful for table generation.
 * 
 * @param <IExpr>
 */
public class IndexFunctionDiagonal implements IIndexFunction<IExpr> {
	final IExpr[] fValues;

	public IndexFunctionDiagonal(final IExpr[] values) {
		fValues = values;
	}

	@Override
	public IExpr evaluate(final int[] index) {
		if (isMatched(index)) {
			return fValues[1];
		}
		return fValues[0];
	}

	private static boolean isMatched(final int[] index) {
		for (int i = 1; i < index.length; i++) {
			if (index[i] != index[i - 1]) {
				return false;
			}
		}
		return true;
	}
}
