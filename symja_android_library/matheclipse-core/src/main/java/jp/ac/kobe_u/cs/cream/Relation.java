/*
 * @(#)Relation.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * Relation constraints.
 * Possible combinations of two integer variables can be
 * defined by a two-dimentional array of boolean values.
 * 
 * @since 1.4
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Relation extends Constraint {
	private boolean[][] rel;
	private Variable v0;
	private Variable v1;

	/**
	 * Adds a constraint meaning <tt>rel[v0][v1]</tt>
	 * to the network.
	 * 
	 * @param net the network
	 * @param v0 the first argument integer variable
	 * @param v1 the second argument integer variable
	 * @param rel two-dimentional array of boolean values 
	 */
	public Relation(Network net, Variable v0, boolean[][] rel, Variable v1) {
		super(net);
		this.rel = rel;
		this.v0 = v0;
		this.v1 = v1;
	}

	public Constraint copy(Network net) {
		return new Relation(net, Constraint.copy(v0, net), rel, Constraint.copy(v1, net));
	}

	public boolean isModified() {
		return v0.isModified() || v1.isModified();
	}

	public boolean satisfy(Trail trail) {
		int m = rel.length;
		int n = rel[0].length;
		// limit the domain of v0 to 0..m-1
		IntDomain d0 = (IntDomain) v0.getDomain();
		d0 = d0.capInterval(0, m - 1);
		if (d0.isEmpty())
			return false;
		// limit the domain of v1 to 0..n-1
		IntDomain d1 = (IntDomain) v1.getDomain();
		d1 = d1.capInterval(0, n - 1);
		if (d1.isEmpty())
			return false;
		// delete impossible indices from v0
		for (int i = 0; i < m; i++) {
			if (! d0.contains(i))
				continue;
			boolean support = false;
			for (int j = 0; j < n; j++) {
				if (rel[i][j] && d1.contains(j)) {
					support = true;
					break;
				}
			}
			if (! support) {
				d0 = d0.delete(i);
			}
		}
		if (d0.isEmpty())
			return false;
		// delete impossible indices from v1
		for (int j = 0; j < n; j++) {
			if (! d1.contains(j))
				continue;
			boolean support = false;
			for (int i = 0; i < m; i++) {
				if (rel[i][j] && d0.contains(i)) {
					support = true;
					break;
				}
			}
			if (! support) {
				d1 = d1.delete(j);
			}
		}
		if (d1.isEmpty())
			return false;
		v0.updateDomain(d0, trail);
		v1.updateDomain(d1, trail);
		return true;
	}

	public String toString() {
		return "Relation(" + v0 + "," + v1 + ")";
	}
}
