package org.matheclipse.core.generic.combinatoric;

import java.util.Iterator;

import org.matheclipse.core.interfaces.IAST;

/**
 * Iterate over the lists of all k-combinations from a given list
 * 
 * See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
 */
public class KSubsetsList implements Iterator<IAST>, Iterable<IAST> {

	final private IAST fList;
	final private IAST fResultList;
	final private int fOffset;
	final private Iterator<int[]> fIterable;
	// final private INestedList<IExpr, IAST> fCopier;
	final private int fK;

	public KSubsetsList(final Iterator<int[]> iterable, final IAST list, final int k, IAST resultList) {
		this(iterable, list, k, resultList, 0);
	}

	public KSubsetsList(final Iterator<int[]> iterable, final IAST list, final int k, IAST resultList, final int offset) {
		fIterable = iterable;
		fList = list;
		fK = k;
		fResultList = resultList;
		// fCopier = copier;
		fOffset = offset;
	}

	public static KSubsetsList createKSubsets(final IAST list, final int k, IAST resultList, final int offset) {
		return new KSubsetsList(new KSubsetsIterable(list.size() - offset, k), list, k, resultList, offset);
	}

	/**
	 * Get the index array for the next partition.
	 * 
	 * @return <code>null</code> if no further index array could be generated
	 */
	public IAST next() {
		int j[] = fIterable.next();
		if (j == null) {
			return null;
		}

		IAST temp = fResultList.clone();
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

	public Iterator<IAST> iterator() {
		return this;
	}

}
