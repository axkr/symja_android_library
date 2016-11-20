/*
 * @(#)Equals.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * Equals constraint.
 * 
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Equals extends Constraint {
	private Variable[] v;

	/**
	 * Adds the constraint of <tt>v0 == v1</tt>
	 * for two variables
	 * to the constraint network <tt>net</tt>.
	 * 
	 * @param net the constraint network
	 * @param v0 the first variable
	 * @param v1 the second variable
	 */
	public Equals(Network net, Variable v0, Variable v1) {
		this(net, new Variable[] { v0, v1 });
	}

	/**
	 * Adds the constraint of
	 * <tt>v[i] == v[j]</tt> for each pair of variables in <tt>v</tt>
	 * to the constraint network <tt>net</tt>.
	 * 
	 * @param net the constraint network
	 * @param v the array of variables 
	 */
	public Equals(Network net, Variable[] v) {
		super(net);
		this.v = v.clone();
	}

	public Constraint copy(Network net) {
		return new Equals(net, Constraint.copy(v, net));
	}

	public boolean isModified() {
		return isModified(v);
	}

	public boolean satisfy(Trail trail) {
		Domain d = v[0].getDomain();
		for (int i = 1; i < v.length; i++) {
			d = d.cap(v[i].getDomain());
			if (d.isEmpty())
				return false;
		}
		for (int i = 0; i < v.length; i++) {
			v[i].updateDomain(d, trail);
		}
		return true;
	}

	public String toString() {
		return "Equals(" + Constraint.toString(v) + ")";
	}
}
