/*
 * @(#)IntDomain.java
 */
package jp.ac.kobe_u.cs.cream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * IntDomain class represents domains on integers.
 * Integer domain is implemented as a list of intervals. 
 * 
 * @see IntVariable
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class IntDomain extends Domain {
	/**
	 * Minimum integer value (-2<sup>31</sup>+1)
	 */
	public static final int MIN_VALUE = 0xc0000001;

	/**
	 * Maximum integer value (2<sup>31</sup>-1)
	 */
	public static final int MAX_VALUE = 0x3fffffff;

	/**
	 * Empty integer domain
	 */
	public static IntDomain EMPTY = new IntDomain();

	/**
	 * Full integer domain <tt>MIN_VALUE..MAX_VALUE</tt>.
	 */
	public static IntDomain FULL = new IntDomain(MIN_VALUE, MAX_VALUE);

	private ArrayList<int[]> intervals = new ArrayList<int[]>();

	private int min;

	private int max;

	/**
	 * Constructs the empty integer domain.
	 */
	public IntDomain() {
	}

	/**
	 * Constructs a singleton integer domain.
	 * @param value the singleton value
	 */
	public IntDomain(int value) {
		this(value, value);
	}

	/**
	 * Constructs an integer domain of <tt>min..max</tt>.
	 * @param min the lower bound
	 * @param max the upper bound
	 */
	public IntDomain(int min, int max) {
		min = Math.max(min, MIN_VALUE);
		max = Math.min(max, MAX_VALUE);
		if (min <= max) {
			int[] interval = { min, max };
			intervals.add(interval);
			size = max - min + 1;
			this.min = min;
			this.max = max;
		}
	}

	private void updateSize() {
		size = 0;
		try {
			for (int[] interval : intervals) {
				size += interval[1] - interval[0] + 1;
			}
		} catch (IndexOutOfBoundsException e) {
		}
	}

	private void updateMinMax() {
		if (size > 0) {
			try {
				int[] interval = intervals.get(0);
				min = interval[0];
				interval = intervals.get(intervals.size() - 1);
				max = interval[1];
			} catch (IndexOutOfBoundsException e) {
			}
		}
	}

	/* (non-Javadoc)
	 * @see jp.ac.kobe_u.cs.cream.Domain#equals(jp.ac.kobe_u.cs.cream.Domain)
	 */
	public boolean equals(Domain d0) {
		if (this == d0)
			return true;
		if (! (d0 instanceof IntDomain))
			return false;
		IntDomain d = (IntDomain) d0;
		if (intervals.size() != d.intervals.size())
			return false;
		try {
			for (int i = 0; i < intervals.size(); i++) {
				int[] i0 = intervals.get(i);
				int[] i1 = d.intervals.get(i);
				if (i0[0] != i1[0] || i0[1] != i1[1])
					return false;
			}
		} catch (IndexOutOfBoundsException e) {
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see jp.ac.kobe_u.cs.cream.Domain#clone()
	 */
	public Object clone() {
		IntDomain d = new IntDomain();
		try {
			for (int[] interval : intervals) {
				d.intervals.add(interval.clone());
			}
		} catch (IndexOutOfBoundsException e) {
		}
		d.size = size;
		d.min = min;
		d.max = max;
		return d;
	}

	/* (non-Javadoc)
	 * @see jp.ac.kobe_u.cs.cream.Domain#elements()
	 */
	public Iterator<Domain> elements() {
		Iterator<Domain> iter = new Iterator<Domain>() {
			private int choice = min;

			public boolean hasNext() {
				if (size == 0)
					return false;
				while (choice <= max) {
					if (contains(choice))
						return true;
					choice++;
				}
				return false;
			}

			public Domain next() throws NoSuchElementException {
				if (!hasNext())
					throw new NoSuchElementException();
				IntDomain d = new IntDomain(choice);
				choice++;
				return d;
			}

			public void remove() throws UnsupportedOperationException {
				throw new UnsupportedOperationException();
			}
		};
		return iter;
	}

	/**
	 * Returns the current minimum value of the domain.
	 * @return the minimum value
	 * @throws NoSuchElementException
	 */
	public int min() throws NoSuchElementException {
		if (size == 0)
			throw new NoSuchElementException();
		return min;
	}

	/**
	 * Returns the current maximum value of the domain.
	 * @return the maximum value
	 * @throws NoSuchElementException
	 */
	public int max() throws NoSuchElementException {
		if (size == 0)
			throw new NoSuchElementException();
		return max;
	}

	/**
	 * Returns the current only value when the domain is a singleton.
	 * @return the singleton value
	 * @throws NoSuchElementException
	 */
	public int value() throws NoSuchElementException {
		if (size != 1)
			throw new NoSuchElementException();
		return min;
	}

	/* (non-Javadoc)
	 * @see jp.ac.kobe_u.cs.cream.Domain#element()
	 */
	public Object element() throws NoSuchElementException {
		return new Integer(value());
	}

	private int indexOf(int elem) {
		if (elem < min || max < elem)
			return -1;
		try {
			for (int i = 0; i < intervals.size(); i++) {
				int[] interval = intervals.get(i);
				if (elem < interval[0])
					return -1;
				if (elem <= interval[1])
					return i;
			}
		} catch (IndexOutOfBoundsException e) {
		}
		return -1;
	}

	/**
	 * Returns true when the domain containts <tt>elem</tt> value.
	 * @param elem the integer value to be checked
	 * @return true when the domain containts <tt>elem</tt>
	 */
	public boolean contains(int elem) {
		return indexOf(elem) >= 0;
	}

	/* (non-Javadoc)
	 * @see jp.ac.kobe_u.cs.cream.Domain#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		if (!(o instanceof Number))
			return false;
		return contains(((Number) o).intValue());
	}

	/**
	 * Returns a randomly selected element value.
	 * @return a randomly selected element value
	 */
	public int randomElement() {
		int n = (int)(Math.random() * size);
		for (int[] interval : intervals) {
			int m = interval[1] - interval[0] + 1;
			if (n < m)
				return interval[0] + n;
			n -= m;
		}
		return min();
	}
	
	/**
	 * Not implemented.
	 * @param o
	 */
	public Domain insert(Object o) {
		// TODO not implemented
		System.out.println("insert: not implemented");
		return this;
	}

	/**
	 * Removes <tt>elem</tt> from the domain.  This is an internal method.
	 * @param elem the integer value to be removed
	 */
	public void remove(int elem) {
		int i = indexOf(elem);
		if (i < 0)
			return;
		try {
			int[] interval = intervals.get(i);
			int lo = interval[0];
			int hi = interval[1];
			if (elem == lo && elem == hi) {
				intervals.remove(i);
			} else if (elem == lo) {
				interval[0] = lo + 1;
			} else if (elem == hi) {
				interval[1] = hi - 1;
			} else {
				interval[0] = elem + 1;
				intervals.add(i, new int[] { lo, elem - 1 });
			}
			size--;
			updateMinMax();
		} catch (IndexOutOfBoundsException e) {
		}
	}

	/**
	 * Removes <tt>o</tt> from the domain.  This is an internal method.
	 * @param o the object to be removed
	 */
	public void remove(Object o) {
		if (!(o instanceof Number))
			return;
		remove(((Number) o).intValue());
	}

    /**
     * Returns a new domain obtained by deleting <tt>elem</tt> value.
     * @param elem the integer value to be deleted
     * @return the new domain
	 */
	public IntDomain delete(int elem) {
		if (!contains(elem))
			return this;
		IntDomain d = (IntDomain) clone();
		d.remove(elem);
		return d;
	}

	/* (non-Javadoc)
	 * @see jp.ac.kobe_u.cs.cream.Domain#delete(java.lang.Object)
	 */
	public Domain delete(Object o) {
		if (!(o instanceof Number))
			return this;
		return delete(((Number) o).intValue());
	}

	/**
     * Returns a new domain obtained by deleting the interval <tt>lo..hi</tt>.
	 * @param lo the lower bound of the interval to be deleted
	 * @param hi the upper bound of the interval to be deleted
	 * @return the new domain
	 */
	public IntDomain delete(int lo, int hi) {
		if (size == 0 || lo > hi || hi < min || max < lo)
			return this;
		if (lo == hi)
			return delete(lo);
		IntDomain d = new IntDomain();
		try {
			for (int i = 0; i < intervals.size(); i++) {
				int[] interval = intervals.get(i);
				int mi = Math.max(lo, interval[0]);
				int ma = Math.min(hi, interval[1]);
				if (mi <= ma) {
					if (interval[0] < mi) {
						int[] in = new int[2];
						in[0] = interval[0];
						in[1] = mi - 1;
						d.intervals.add(in);
					}
					if (ma < interval[1]) {
						int[] in = new int[2];
						in[0] = ma + 1;
						in[1] = interval[1];
						d.intervals.add(in);
					}
				} else {
					int[] in = new int[2];
					in[0] = interval[0];
					in[1] = interval[1];
					d.intervals.add(in);
				}
			}
		} catch (IndexOutOfBoundsException e) {
		}
		d.updateSize();
		d.updateMinMax();
		return d;
	}

	/* (non-Javadoc)
	 * @see jp.ac.kobe_u.cs.cream.Domain#cap(jp.ac.kobe_u.cs.cream.Domain)
	 */
	public Domain cap(Domain d) {
		if (!(d instanceof IntDomain))
			return EMPTY;
		IntDomain new_d = new IntDomain();
		IntDomain d0 = this;
		IntDomain d1 = (IntDomain) d;
		try {
			int[] interval;
			int min0, max0, min1, max1;
			int i0 = 0;
			int i1 = 0;
			while (i0 < d0.intervals.size() && i1 < d1.intervals.size()) {
				interval = d0.intervals.get(i0);
				min0 = interval[0];
				max0 = interval[1];
				interval = d1.intervals.get(i1);
				min1 = interval[0];
				max1 = interval[1];
				if (max0 < min1) {
					i0++;
					continue;
				}
				if (max1 < min0) {
					i1++;
					continue;
				}
				interval = new int[2];
				interval[0] = Math.max(min0, min1);
				interval[1] = Math.min(max0, max1);
				new_d.intervals.add(interval);
				if (max0 <= max1)
					i0++;
				if (max1 <= max0)
					i1++;
			}
		} catch (IndexOutOfBoundsException e) {
		}
		new_d.updateSize();
		new_d.updateMinMax();
		if (new_d.isEmpty())
			return EMPTY;
		return new_d;
	}

	/**
	 * Not implemented.
	 * @param d
	 */
	public Domain cup(Domain d) {
		// TODO not implemented
		System.out.println("cup: not implemented");
		return this;
	}

	/**
	 * Not implemented.
	 * @param d
	 */
	public Domain difference(Domain d) {
		// TODO not implemented
		System.out.println("difference: not implemented");
		return this;
	}

	/**
	 * Returns a new domain which is the intersection of the curren domain
	 * and the interval <tt>lo..hi</tt>.
	 * @param lo the lower bound
	 * @param hi the upper bound
	 * @return the new domain
	 */
	public IntDomain capInterval(int lo, int hi) {
		IntDomain d = this;
		if (MIN_VALUE < lo)
			d = d.delete(MIN_VALUE, lo - 1);
		if (hi < MAX_VALUE)
			d = d.delete(hi + 1, MAX_VALUE);
		return d;
	}

	private String toString(int x) {
		if (x == MIN_VALUE) {
			return "min";
		} else if (x == MAX_VALUE) {
			return "max";
		} else {
			return Integer.toString(x);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder s = new StringBuilder();
		String delim = "";
		try {
			for (int[] interval: intervals) {
				s.append(delim).append(toString(interval[0]));
				if (interval[0] < interval[1]) {
					s.append("..").append(toString(interval[1]));
				}
				delim = ",";
			}
		} catch (IndexOutOfBoundsException e) {
		}
		if (size() == 1) {
			return s.toString();
		}
		s.insert(0, '{').append('}');
		return s.toString();
	}
}
