package org.matheclipse.core.eval.util;

import javax.annotation.Nonnull;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Table structure generator (i.e. lists, vectors, matrices, tensors)
 */
public class IndexTableGenerator {
	final int[] fIndexArray;

	final IAST fPrototypeList;

	final IIndexFunction<? extends IExpr> fFunction;

	int fIndex;

	int[] fCurrentIndex;

//	private final INestedList<IExpr, IAST> fCopier;

	/**
	 * 
	 * @param indexArray
	 * @param prototypeList prototype for cloning the basic structure of the resulting lists
	 * @param function
	 */
	public IndexTableGenerator(final int[] indexArray, final IAST prototypeList, final IIndexFunction<? extends IExpr> function) {
		fIndexArray = indexArray;
		fPrototypeList = prototypeList;
		fFunction = function;
		fIndex = 0;
		fCurrentIndex = new int[indexArray.length];
//		fCopier = copier;
	}

	@Nonnull public IExpr table() {
		if (fIndex < fIndexArray.length) {
			final int iter = fIndexArray[fIndex];
			final int index = fIndex++;
			try {
				final IAST result = fPrototypeList.clone();
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
