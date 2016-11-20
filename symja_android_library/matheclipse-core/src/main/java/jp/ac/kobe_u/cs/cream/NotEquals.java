/*
 * @(#)NotEquals.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * NotEquals constraint.
 * 
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class NotEquals extends Constraint {
	private Variable[] v;

	/**
	 * Adds the constraint of <tt>v0 != v1</tt>
	 * for two variables
	 * to the constraint network <tt>net</tt>.
	 * 
	 * @param net the constraint network
	 * @param v0 the first variable
	 * @param v1 the second variable
	 */
	public NotEquals(Network net, Variable v0, Variable v1) {
		this(net, new Variable[] { v0, v1 });
	}

	/**
	 * Adds the constraint of
	 * <tt>v[i] != v[j]</tt> for each pair of variables in <tt>v</tt>
	 * to the constraint network <tt>net</tt>.
	 * 
	 * @param net the constraint network
	 * @param v the array of variables 
	 */
	public NotEquals(Network net, Variable[] v) {
		super(net);
		this.v = v.clone();
	}

	public Constraint copy(Network net) {
		return new NotEquals(net, Constraint.copy(v, net));
	}

	public boolean isModified() {
		return isModified(v);
	}

	public boolean satisfy(Trail trail) {
		for (int i = 0; i < v.length; i++) {
			Domain d = v[i].getDomain();
			if (d.size() != 1)
				continue;
			Object elem = d.element();
			for (int j = 0; j < v.length; j++) {
				if (i == j)
					continue;
				Domain d1 = v[j].getDomain().delete(elem);
				if (d1.isEmpty())
					return false;
				v[j].updateDomain(d1, trail);
			}
		}
		return true;
	}

	public String toString() {
		return "NotEquals(" + Constraint.toString(v) + ")";
	}
}
