package org.matheclipse.core.generic;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.generic.nested.IndexFunctionDiagonal;

public class UnaryIndexFunctionDiagonal extends IndexFunctionDiagonal<IExpr> {
//	final IExpr[] fValues;

	public UnaryIndexFunctionDiagonal(final IExpr[] values) {
		super(values);
//		fValues = values;
	}

//	public IExpr evaluate(final int[] index) {
//		if (apply(index)) {
//			return fValues[1];
//		}
//		return fValues[0];
//	}
//
//	public boolean apply(final int[] index) {
//		for (int i = 1; i < index.length; i++) {
//			checkCanceled();
//			if (index[i] != index[i - 1]) {
//				return false;
//			}
//		}
//		return true;
//	}
}
