package org.matheclipse.core.generic.combinatoric;

import java.util.Iterator;

/**
 * This class returns the indexes for partitioning a list of N elements. <br/>
 * Usage pattern:
 * 
 * <pre>
 * final KPartitionsIterable iter = new KPartitionsIterable(n, k);
 * for (int[] partitionsIndex : iter) {
 *   ...
 * }
 * </pre>
 * 
 * Example: KPartitionsIterable(3,5) gives the following sequences [0, 1, 2],
 * [0, 1, 3], [0, 1, 4], [0, 2, 3], [0, 2, 4], [0, 3, 4] <br/>
 * If you interpret these integer lists as indexes for a list {a,b,c,d,e} which
 * should be partitioned into 3 parts the results are: <br/>
 * {{{a},{b},{c,d,e}}, {{a},{b,c},{d,e}}, {{a},{b,c,d},{e}}, {{a,b},{c},{d,e}},
 * {{a,b},{c,d},{e}}, {{a,b,c},{d},{e}}}
 * 
 * <br/>
 * <br/>
 * See <a href="http://en.wikipedia.org/wiki/Partition_of_a_set">Wikipedia -
 * Partition of a set</a>
 */
public class KPartitionsIterable implements Iterator<int[]>, Iterable<int[]> {

	final private int fLength;

	final private int fNumberOfParts;

	final private int fPartitionsIndex[];

	final private int fCopiedResultIndex[];

	private int fResultIndex[];

	public KPartitionsIterable(final int length, final int parts) {
		super();
		fLength = length;
		fNumberOfParts = parts;
		fPartitionsIndex = new int[fNumberOfParts];
		fCopiedResultIndex = new int[fNumberOfParts];
		fPartitionsIndex[0] = -1;
		fResultIndex = nextBeforehand();
	}

	public final void reset() {
		fResultIndex = null;
		for (int i = 1; i < fNumberOfParts; i++) {
			fPartitionsIndex[i] = 0;
		}
		fPartitionsIndex[0] = -1;
		fResultIndex = nextBeforehand();
	}

	/**
	 * Get the index array for the next partition.
	 * 
	 * @return <code>null</code> if no further index array could be generated
	 */
	private final int[] nextBeforehand() {
		if (fPartitionsIndex[0] < 0) {
			for (int i = 0; i < fNumberOfParts; ++i) {
				fPartitionsIndex[i] = i;
			}
			return fPartitionsIndex;
		} else {
			int i = 0;
			for (i = fNumberOfParts - 1; (i >= 0) && (fPartitionsIndex[i] >= fLength - fNumberOfParts + i); --i) {
			}
			if (i <= 0) {
				return null;
			}
			fPartitionsIndex[i]++;
			for (int m = i + 1; m < fNumberOfParts; ++m) {
				fPartitionsIndex[m] = fPartitionsIndex[m - 1] + 1;
			}
			return fPartitionsIndex;
		}
	}

	/**
	 * Get the index array for the next partition.
	 * 
	 * @return <code>null</code> if no further index array could be generated
	 */
	public int[] next() {
		System.arraycopy(fResultIndex, 0, fCopiedResultIndex, 0, fResultIndex.length);
		fResultIndex = nextBeforehand();
		return fCopiedResultIndex;
	}

	public boolean hasNext() {
		return fResultIndex != null;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public Iterator<int[]> iterator() {
		return this;
	}

}
