/*
 * @(#)Element.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * Element constraints.
 * 
 * @since 1.4
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Element extends Constraint {
	private Variable v0;
	private Variable v1;
	private Variable[] v;

	/**
	 * Adds an element constraint meaning <tt>v0 == v[v1]</tt> 
	 * to the network.
	 * 
	 * @param net the network
	 * @param v0 integer variable for the result
	 * @param v1 integer variable for the index
	 * @param v the array of integer variables
	 */
	public Element(Network net, Variable v0, Variable v1, Variable[] v) {
		super(net);
		this.v0 = v0;
		this.v1 = v1;
		this.v = v.clone();
	}

	public Constraint copy(Network net) {
		return new Element(net, Constraint.copy(v0, net), Constraint.copy(v1, net), Constraint.copy(v, net));
	}

	public boolean isModified() {
		return v0.isModified() || v1.isModified() || isModified(v);
	}

	public boolean satisfy(Trail trail) {
		int n = v.length;
		// limit the domain of v1 to 0..n-1
		IntDomain d1 = (IntDomain) v1.getDomain();
		d1 = d1.capInterval(0, n - 1);
		if (d1.isEmpty())
			return false;
		// get the possible range of v[i] as min..max
		int min = IntDomain.MAX_VALUE;
		int max = IntDomain.MIN_VALUE;
		for (int i = 0; i < n; i++) {
			if (d1.contains(i)) {
				IntDomain d = (IntDomain) v[i].getDomain();
				min = Math.min(min, d.min());
				max = Math.max(max, d.max());
			}
		}
		if (min > max)
			return false;
		// limit the domain of v0 to min..max
		IntDomain d0 = (IntDomain) v0.getDomain();
		d0 = d0.capInterval(min, max);
		if (d0.isEmpty())
			return false;
		// delete impossible indices from v1
		for (int i = 0; i < n; i++) {
			if (d1.contains(i)) {
				IntDomain d = (IntDomain) v[i].getDomain();
				if (d0.capInterval(d.min(), d.max()).isEmpty()) {
					d1 = d1.delete(i);
				}
			}
		}
		if (d1.isEmpty())
			return false;
		// propagate v0 to v[v1] when v1 is determined
		if (d1.size() == 1) {
			int i = d1.value();
			IntDomain d = (IntDomain) v[i].getDomain();
			d0 = (IntDomain) d.cap(d0);
			if (d0.isEmpty())
				return false;
			v[i].updateDomain(d0, trail);
		}
		v0.updateDomain(d0, trail);
		v1.updateDomain(d1, trail);
		return true;
	}

	public String toString() {
		return "Element(" + v0 + "," + v1 + "," + Constraint.toString(v) + ")";
	}
}
