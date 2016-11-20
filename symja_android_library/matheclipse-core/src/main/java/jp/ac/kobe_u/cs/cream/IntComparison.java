/*
 * @(#)IntComparison.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * IntComparison constraint implements
 * comparison relations on integers.
 * 
 * @see IntArith
 * @see IntFunc
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class IntComparison extends Constraint {
	/**
	 * Less-than-or-equal comparison
	 */
	public static final int LE = 0;

	/**
	 * Less-than comparison
	 */
	public static final int LT = 1;

	/**
	 * Greater-than-or-equal comparison
	 */
	public static final int GE = 2;

	/**
	 * Greater-than comparison
	 */
	public static final int GT = 3;

	private int comparison;

	private Variable[] v;

	/**
	 * Adds the constraint of <tt>v0 comp v1</tt>
	 * to the constraint network <tt>net</tt>.
	 * @param net the constraint network
	 * @param comp the comparison
	 * @param v0 the first argument
	 * @param v1 the second argument
	 */
	public IntComparison(Network net, int comp, Variable v0, Variable v1) {
		this(net, comp, new Variable[] { v0, v1 });
	}

	public IntComparison(Network net, int comp, Variable v0, int x1) {
		this(net, comp, v0, new IntVariable(net, x1));
	}

	public IntComparison(Network net, int comp, int x0, Variable v1) {
		this(net, comp, new IntVariable(net, x0), v1);
	}

	private IntComparison(Network net, int comp, Variable[] v) {
		super(net);
		comparison = comp;
		this.v = v;
	}

	public Constraint copy(Network net) {
		return new IntComparison(net, comparison, Constraint.copy(this.v, net));
	}

	public boolean isModified() {
		return isModified(v);
	}

	private boolean satisfyLE(Variable v0, Variable v1, Trail trail) {
		IntDomain d0 = (IntDomain) v0.getDomain();
		IntDomain d1 = (IntDomain) v1.getDomain();
		d0 = d0.capInterval(IntDomain.MIN_VALUE, d1.max());
		if (d0.isEmpty())
			return false;
		v0.updateDomain(d0, trail);
		d1 = d1.capInterval(d0.min(), IntDomain.MAX_VALUE);
		if (d1.isEmpty())
			return false;
		v1.updateDomain(d1, trail);
		return true;
	}

	private boolean satisfyLT(Variable v0, Variable v1, Trail trail) {
		IntDomain d0 = (IntDomain) v0.getDomain();
		IntDomain d1 = (IntDomain) v1.getDomain();
		d0 = d0.capInterval(IntDomain.MIN_VALUE, d1.max() - 1);
		if (d0.isEmpty())
			return false;
		v0.updateDomain(d0, trail);
		d1 = d1.capInterval(d0.min() + 1, IntDomain.MAX_VALUE);
		if (d1.isEmpty())
			return false;
		v1.updateDomain(d1, trail);
		return true;
	}

	public boolean satisfy(Trail trail) {
		switch (comparison) {
		case LE:
			return satisfyLE(v[0], v[1], trail);
		case LT:
			return satisfyLT(v[0], v[1], trail);
		case GE:
			return satisfyLE(v[1], v[0], trail);
		case GT:
			return satisfyLT(v[1], v[0], trail);
		}
		return false;
	}

	public String toString() {
		StringBuilder c = new StringBuilder("IntComparison(");
		switch (comparison) {
		case LE:
			c.append("LE");
			break;
		case LT:
			c.append("LT");
			break;
		case GE:
			c.append("GE");
			break;
		case GT:
			c.append("GT");
			break;
		}
		c.append(',').append(Constraint.toString(v)).append(')');
		return c.toString();
	}
}
