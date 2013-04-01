package org.matheclipse.generic.nested;

import java.util.List;

import org.matheclipse.generic.interfaces.IIndexFunction;

/**
 * Table structure generator (i.e. lists, vectors, matrices, tensors)
 */
public class IndexTableGenerator<T extends INestedListElement, L extends List<T> & INestedListElement> {
	final int[] fIndexArray;

	final L fPrototypeList;

	final IIndexFunction<? extends T> fFunction;

	int fIndex;

	int[] fCurrentIndex;

	private final INestedList<T, L> fCopier;

	/**
	 * 
	 * @param indexArray
	 * @param prototypeList prototype for cloning the basic structure of the resulting lists
	 * @param function
	 * @param copier
	 */
	public IndexTableGenerator(final int[] indexArray, final L prototypeList, final IIndexFunction<? extends T> function, INestedList<T, L> copier) {
		fIndexArray = indexArray;
		fPrototypeList = prototypeList;
		fFunction = function;
		fIndex = 0;
		fCurrentIndex = new int[indexArray.length];
		fCopier = copier;
	}

	public T table() {
		if (fIndex < fIndexArray.length) {
			final int iter = fIndexArray[fIndex];
			final int index = fIndex++;
			try {
				final L result = fCopier.clone(fPrototypeList);
				for (int i = 0; i < iter; i++) {
					fCurrentIndex[index] = i;
					result.add(table());
				}
				return fCopier.castList(result);
			} finally {
				--fIndex;
			}
		}
		return fFunction.evaluate(fCurrentIndex);
	}
}
