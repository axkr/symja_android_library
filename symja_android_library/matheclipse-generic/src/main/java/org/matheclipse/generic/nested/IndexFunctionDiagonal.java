package org.matheclipse.generic.nested;

import org.matheclipse.generic.interfaces.IIndexFunction;

/**
 * Evaluates to one of two given values. Useful for table generation.
 * 
 * @param <T>
 */
public class IndexFunctionDiagonal<T> implements IIndexFunction<T> {
	final T[] fValues;

	public IndexFunctionDiagonal(final T[] values) {
		fValues = values;
	}

	public T evaluate(final int[] index) {
		if (isMatched(index)) {
			return fValues[1];
		}
		return fValues[0];
	}

	protected boolean isMatched(final int[] index) {
		for (int i = 1; i < index.length; i++) {
			if (index[i] != index[i - 1]) {
				return false;
			}
		}
		return true;
	}
}
