/*
 * @(#)Sequential.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * Sequential constraints
 * constructed from
 * an array of <tt>n</tt> integer variables (<tt>v[0]</tt>, ..., <tt>v[n-1]</tt>) and
 * an array of <tt>n</tt> positive integer constants (<tt>a[0]</tt>, ..., <tt>a[n-1]</tt>).
 * This constraint means
 * <tt>v[i-1]+a[i-1] &lt;= v[i]</tt> (<tt>i = 1, 2, ..., n-1</tt>).
 * 
 * @see Serialized
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Sequential extends Constraint {
	private Variable[] v;

	private int[] a;

	/**
	 * Adds a sequential constraint to the network.
	 * The length of two arrays should be the same.
	 * All elements of <tt>a</tt> should be positive.
	 * @param net the network
	 * @param v the array of integer variables
	 * @param a the array of positive integer constants
	 */
	public Sequential(Network net, Variable[] v, int[] a) {
		super(net);
		this.v = v.clone();
		this.a = a.clone();
	}

	public Constraint copy(Network net) {
		return new Sequential(net, Constraint.copy(v, net), a);
	}

	public boolean isModified() {
		return isModified(v);
	}

	public boolean satisfy(Trail trail) {
		for (int i = 0; i < v.length - 1; i++) {
			int j = i + 1;
			IntDomain d0 = (IntDomain) v[i].getDomain();
			IntDomain d1 = (IntDomain) v[j].getDomain();
			int diffMin = d1.max() - a[i] + 1;
			int diffMax = d0.min() + a[i] - 1;
			d0 = d0.delete(diffMin, IntDomain.MAX_VALUE);
			if (d0.isEmpty())
				return false;
			d1 = d1.delete(IntDomain.MIN_VALUE, diffMax);
			if (d1.isEmpty())
				return false;
			v[i].updateDomain(d0, trail);
			v[j].updateDomain(d1, trail);
		}
		return true;
	}

	public String toString() {
		return "Sequential(" + Constraint.toString(v) + ","
				+ Constraint.toString(a) + ")";
	}
}
