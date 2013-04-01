package org.matheclipse.generic.nested;

import java.util.List;


public class Generating<T extends INestedListElement, L extends List<T> & INestedListElement> {
	final private L fOuterList;

	final private L fInnerList;

	final private int fHeadOffset;

	final private INestedList<T, L> fCopier;

	public Generating(L outerList, L innerList, int headOffset, INestedList<T, L> copier) {
		this.fOuterList = outerList;
		this.fInnerList = innerList;
		this.fHeadOffset = headOffset;
		this.fCopier = copier;
	}

	/**
	 * Outer product - every element from the first list will be combined with the
	 * second list
	 * 
	 */
	public L outer(L first, L second) {
		L result1 = fCopier.clone(fOuterList);
		L result2;
		L temp;
		for (int i = fHeadOffset; i < first.size(); i++) {

			if (fCopier.isInstance(first.get(i))) {
				result1.add(fCopier.castList(outer(fCopier.cast(first.get(i)), second)));
			} else {
				result2 = fCopier.clone(fOuterList);
				for (int j = fHeadOffset; j < second.size(); j++) {

					if (fCopier.isInstance(second.get(j))) {
						result2.add(fCopier.castList(outer(first.get(i), fCopier.cast(second.get(j)))));
					} else {
						temp = fCopier.clone(fInnerList);
						temp.add(first.get(i));
						temp.add(second.get(j));
						result2.add(fCopier.castList(temp));
					}
				}
				result1.add(fCopier.castList(result2));
			}
		}

		return result1;
	}

	private L outer(T element, L second) {
		L result = fCopier.clone(fOuterList);
		L temp;
		for (int j = fHeadOffset; j < second.size(); j++) {

			if (fCopier.isInstance(second.get(j))) {
				result.add(fCopier.castList(outer(element, fCopier.cast(second.get(j)))));
			} else {
				temp = fCopier.clone(fInnerList);
				temp.add(element);
				temp.add(second.get(j));
				result.add(fCopier.castList(temp));
			}
		}

		return result;
	}

}
