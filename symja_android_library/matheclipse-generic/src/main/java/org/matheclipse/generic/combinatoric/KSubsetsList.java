package org.matheclipse.generic.combinatoric;

import java.util.Iterator;
import java.util.List;

import org.matheclipse.generic.nested.INestedList;
import org.matheclipse.generic.nested.INestedListElement;

/**
 * Iterate over the lists of all k-combinations from a given list
 * 
 * See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
 */
public class KSubsetsList<T extends INestedListElement, L extends List<T> & INestedListElement> implements Iterator<L>, Iterable<L> {

	final private L fList;
	final private L fResultList;
	final private int fOffset;
	final private Iterator<int[]> fIterable;
	final private INestedList<T, L> fCopier;
	final private int fK;

	public KSubsetsList(final Iterator<int[]> iterable, final L list, final int k, L resultList, INestedList<T, L> copier) {
		this(iterable, list, k, resultList, copier, 0);
	}

	public KSubsetsList(final Iterator<int[]> iterable, final L list, final int k, L resultList, INestedList<T, L> copier,
			final int offset) {
		fIterable = iterable;
		fList = list;
		fK = k;
		fResultList = resultList;
		fCopier = copier;
		fOffset = offset;
	}

	public static <T extends INestedListElement, L extends List<T> & INestedListElement> KSubsetsList<T, L> createKSubsets(final L list, final int k, L resultList,
			INestedList<T, L> copier, final int offset) {
		return new KSubsetsList<T, L>(new KSubsetsIterable(list.size() - offset, k), list, k, resultList, copier, offset);
	}

	/**
	 * Get the index array for the next partition.
	 * 
	 * @return <code>null</code> if no further index array could be generated
	 */
	public L next() {
		int j[] = fIterable.next();
		if (j == null) {
			return null;
		}

		L temp = fCopier.newInstance(fResultList);
		for (int i = 0; i < fK; i++) {
			temp.add(fList.get(j[i] + fOffset));
		}
 
		return temp;
	}

	public boolean hasNext() {
		return fIterable.hasNext();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public Iterator<L> iterator() {
		return this;
	}

}
