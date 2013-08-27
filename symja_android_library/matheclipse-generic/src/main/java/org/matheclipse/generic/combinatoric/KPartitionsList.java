package org.matheclipse.generic.combinatoric;

import java.util.Iterator;

import org.matheclipse.core.interfaces.IAST;

/**
 * This <code>Iterable</code> iterates through all k-partition lists for a given list with N elements. <br/>
 * 
 * See <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia - Partition of a set</a>
 * 
 */
public class KPartitionsList implements Iterator<IAST>, Iterable<IAST> {

	final private IAST fList;
	final private IAST fResultList;
	final private int fOffset;
	final private KPartitionsIterable fIterable;

	public KPartitionsList(final IAST list, final int parts, IAST resultList) {
		this(list, parts, resultList, 0);
	}

	public KPartitionsList(final IAST list, final int parts, IAST resultList, final int offset) {
		super();
		fIterable = new KPartitionsIterable(list.size() - offset, parts);
		fList = list;
		fResultList = resultList;
		fOffset = offset;
	}

	/**
	 * Get the index array for the next partition.
	 * 
	 * @return <code>null</code> if no further index array could be generated
	 */
	public IAST next() {
		int[] partitionsIndex = fIterable.next();
		if (partitionsIndex == null) {
			return null;
		}
		IAST part = fResultList.clone();
		IAST temp;
		// System.out.println("Part:");
		int j = 0;
		for (int i = 1; i < partitionsIndex.length; i++) {
			// System.out.println(partitionsIndex[i] + ",");
			temp = fResultList.clone();
			for (int m = j; m < partitionsIndex[i]; m++) {
				temp.add(fList.get(m + fOffset));
			}
			j = partitionsIndex[i];
			part.add(temp);
		}

		temp = fResultList.clone();
		int n = fList.size() - fOffset;
		for (int m = j; m < n; m++) {
			temp.add(fList.get(m + fOffset));
		}
		part.add(temp);
		return part;
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
