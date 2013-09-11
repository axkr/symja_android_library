package org.matheclipse.core.generic.combinatoric;

import java.util.Iterator;

import org.matheclipse.core.interfaces.IAST;

/**
 * Generate a list of permutations
 * 
 * See <a href="http://en.wikipedia.org/wiki/Permutation">Permutation</a>
 */
public class KPermutationsList implements Iterator<IAST>, Iterable<IAST> {

	final private IAST fList;
	final private IAST fResultList;
	final private int fOffset;
	final private KPermutationsIterable fIterable;

	public KPermutationsList(final IAST list, final int parts, IAST resultList) {
		this(list, parts, resultList, 0);
	}

	public KPermutationsList(final IAST list, final int parts, IAST resultList, final int offset) {
		fIterable = new KPermutationsIterable(list, parts, offset);
		fList = list;
		fResultList = resultList;
		fOffset = offset;
	}

	/**
	 * Get the index array for the next permutation.
	 * 
	 * @return <code>null</code> if no further index array could be generated
	 */
	public IAST next() {
		int[] permutationsIndex = fIterable.next();
		if (permutationsIndex == null) {
			return null;
		}
		IAST temp = fResultList.clone();
		for (int i = 0; i < permutationsIndex.length; i++) {
			temp.add(fList.get(permutationsIndex[i] + fOffset));
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
