package org.matheclipse.generic.combinatoric;

import java.util.Iterator;
import java.util.List;

import org.matheclipse.generic.nested.INestedList;
import org.matheclipse.generic.nested.INestedListElement;

/**
 * This <code>Iterable</code> iterates through all k-partition lists for a given
 * list with N elements. <br/>
 * 
 * See <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia -
 * Partition of a set</a>
 * 
 */
public class KPartitionsList<T extends INestedListElement, L extends List<T> & INestedListElement> implements Iterator<L>, Iterable<L> {

	final private L fList;
	final private L fResultList;
	final private int fOffset;
	final private KPartitionsIterable fIterable;
	final private INestedList<T, L> fCopier;

	public KPartitionsList(final L list, final int parts, L resultList, INestedList<T, L> copier) {
		this(list, parts, resultList, copier, 0);
	}

	public KPartitionsList(final L list, final int parts, L resultList, INestedList<T, L> copier, final int offset) {
		super();
		fIterable = new KPartitionsIterable(list.size() - offset, parts);
		fList = list;
		fResultList = resultList;
		fCopier = copier;
		fOffset = offset;
	}

	/**
	 * Get the index array for the next partition.
	 * 
	 * @return <code>null</code> if no further index array could be generated
	 */
	public L next() {
		int[] partitionsIndex = fIterable.next();
		if (partitionsIndex == null) {
			return null;
		}
		L part = fCopier.newInstance(fResultList);
		L temp;
		// System.out.println("Part:");
		int j = 0;
		for (int i = 1; i < partitionsIndex.length; i++) {
			// System.out.println(partitionsIndex[i] + ",");
			temp = fCopier.newInstance(fResultList);
			for (int m = j; m < partitionsIndex[i]; m++) {
				temp.add(fList.get(m + fOffset));
			}
			j = partitionsIndex[i];
			part.add(fCopier.castList(temp));
		}

		temp = fCopier.newInstance(fResultList);
		int n = fList.size() - fOffset;
		for (int m = j; m < n; m++) {
			temp.add(fList.get(m + fOffset));
		}
		part.add(fCopier.castList(temp));
		return part;
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
