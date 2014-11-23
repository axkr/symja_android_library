package org.matheclipse.core.polynomials;

import java.util.Arrays;
import java.util.Comparator;

public class ExponentArray implements Comparable<ExponentArray>, Cloneable {

	public static Comparator<ExponentArray> degreeLexicographic() {
		return new Comparator<ExponentArray>() {

			@Override
			public int compare(ExponentArray o1, ExponentArray o2) {
				if (o1.sum < 0) {
					o1.sum = 0L;
					for (int i = 0; i < o1.fExponents.length; i++) {
						o1.sum += o1.fExponents[i];
					}
				}
				if (o2.sum < 0) {
					o2.sum = 0L;
					for (int i = 0; i < o2.fExponents.length; i++) {
						o2.sum += o2.fExponents[i];
					}
				}
				if (o1.sum > o2.sum) {
					return -1;
				}
				if (o1.sum < o2.sum) {
					return 1;
				}
				for (int i = 0; i < o1.fExponents.length; i++) {
					if (o1.fExponents[i] < o2.fExponents[i]) {
						return -1;
					}
					if (o1.fExponents[i] > o2.fExponents[i]) {
						return 1;
					}
				}
				return 0;
			}

		};
	}

	public static Comparator<ExponentArray> lexicographic() {
		return new Comparator<ExponentArray>() {

			@Override
			public int compare(ExponentArray o1, ExponentArray o2) {
				for (int i = 0; i < o1.fExponents.length; i++) {
					if (o1.fExponents[i] > o2.fExponents[i]) {
						return -1;
					}
					if (o1.fExponents[i] < o2.fExponents[i]) {
						return 1;
					}
				}
				return 0;
			}

		};
	}

	long sum = -1;
	long[] fExponents;

	public ExponentArray(int length) {
		this(length, 0, 0L);
	}

	public ExponentArray(int length, int position) {
		this(length, position, 1L);
	}

	public ExponentArray(int length, int position, long value) {
		fExponents = new long[length];
		fExponents[position] = value;
	}

	public ExponentArray(long[] exponents) {
		fExponents = exponents;
	}

	@Override
	protected ExponentArray clone() throws CloneNotSupportedException {
		ExponentArray e = (ExponentArray) super.clone();
		e.fExponents = new long[fExponents.length];
		for (int i = 0; i < fExponents.length; i++) {
			e.fExponents[i] = fExponents[i];
		}
		return e;
	}

	@Override
	public int compareTo(ExponentArray o) {
		for (int i = 0; i < fExponents.length; i++) {
			if (fExponents[i] < o.fExponents[i]) {
				return -1;
			}
			if (fExponents[i] > o.fExponents[i]) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ExponentArray) {
			ExponentArray other = (ExponentArray) obj;
			return Arrays.equals(fExponents, other.fExponents);
		}
		return false;
	}

	/**
	 * Get the exponent at the given <code>position</code>
	 * 
	 * @param position
	 * @return
	 */
	final public long getExponent(int position) {
		return fExponents[position];
	}

	public long[] getExponents() {
		return fExponents;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(fExponents);
	}

	/**
	 * The maximum degree of the exponents.
	 * 
	 * @return the maximum degree of the exponents.
	 */
	public long maximumDegree() {
		long maximum = 0L;
		for (int i = 0; i < fExponents.length; i++) {
			if (fExponents[i] > maximum) {
				maximum = fExponents[i];
			}
		}
		return maximum;
	}

	protected void timesBy(ExponentArray exponents) {
		for (int i = 0; i < fExponents.length; i++) {
			fExponents[i] += exponents.fExponents[i];
		}
	}

	protected void timesBy(int position) {
		fExponents[position] += 1L;
	}
}
