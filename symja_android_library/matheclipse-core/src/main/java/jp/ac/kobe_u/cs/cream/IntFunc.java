/*
 * @(#)IntFunc.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * IntFunc constraint implements
 * arithmetic unary function on integers.
 * 
 * @see IntArith
 * @see IntComparison
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class IntFunc extends Constraint {
	/**
	 * Negation function
	 */
	public static final int NEGATE = 0;

	/**
	 * Absolute value function
	 */
	public static final int ABS = 1;

	/**
	 * Sign function
	 */
	public static final int SIGN = 2;

	private int func;

	private Variable[] v;

	/**
	 * Adds the constraint of <tt>v0 == f(v1)</tt>
	 * to the constraint network <tt>net</tt>.
	 * @param net the constraint network
	 * @param f the function
	 * @param v0 the result
	 * @param v1 the argument
	 */
	public IntFunc(Network net, int f, Variable v0, Variable v1) {
		this(net, f, new Variable[] { v0, v1 });
	}

	public IntFunc(Network net, int f, Variable v0, int x1) {
		this(net, f, v0, new IntVariable(net, x1));
	}

	public IntFunc(Network net, int f, int x0, Variable v1) {
		this(net, f, new IntVariable(net, x0), v1);
	}

	private IntFunc(Network net, int f, Variable[] v) {
		super(net);
		func = f;
		this.v = v;
	}

	public Constraint copy(Network net) {
		return new IntFunc(net, func, Constraint.copy(this.v, net));
	}

	public boolean isModified() {
		return isModified(v);
	}

	private boolean satisfyNEGATE(Variable v0, Variable v1, Trail trail) {
		IntDomain d0 = (IntDomain) v0.getDomain();
		IntDomain d1 = (IntDomain) v1.getDomain();

		if (d1.size() == 1) {
			// v0 = -v1
			int value = -d1.value();
			if (!d0.contains(value))
				return false;
			if (d0.size() > 1)
				v0.updateDomain(new IntDomain(value), trail);
			return true;
		} else if (d0.size() == 1) {
			// v1 = -v0
			int value = -d0.value();
			if (!d1.contains(value))
				return false;
			if (d1.size() > 1)
				v1.updateDomain(new IntDomain(value), trail);
			return true;
		}

		// v0 = -v1
		d0 = d0.capInterval(-d1.max(), -d1.min());
		if (d0.isEmpty())
			return false;
		v0.updateDomain(d0, trail);
		// v1 = -v0
		d1 = d1.capInterval(-d0.max(), -d0.min());
		if (d1.isEmpty())
			return false;
		v1.updateDomain(d1, trail);

		return true;
	}

	private boolean satisfyABS(Variable v0, Variable v1, Trail trail) {
		IntDomain d0 = (IntDomain) v0.getDomain();
		IntDomain d1 = (IntDomain) v1.getDomain();

		if (d1.size() == 1) {
			// v0 = abs(v1)
			int value = Math.abs(d1.value());
			if (!d0.contains(value))
				return false;
			if (d0.size() > 1)
				v0.updateDomain(new IntDomain(value), trail);
			return true;
		} else if (d0.size() == 1) {
			// abs(v1) = v0
			int value = d0.value();
			if (value < 0) {
				return false;
			} else if (value == 0) {
				if (!d1.contains(value))
					return false;
				if (d1.size() > 1)
					v1.updateDomain(new IntDomain(value), trail);
				return true;
			} else {
				if (d1.contains(value) && d1.contains(-value)) {
					if (d1.size() > 2) {
						value = Math.abs(value);
						d1 = new IntDomain(-value, value);
						d1 = d1.delete(-value + 1, value - 1);
						v1.updateDomain(d1, trail);
					}
					return true;
				} else if (d1.contains(value)) {
					if (d1.size() > 1)
						v1.updateDomain(new IntDomain(value), trail);
					return true;
				} else if (d1.contains(-value)) {
					if (d1.size() > 1)
						v1.updateDomain(new IntDomain(-value), trail);
					return true;
				}
				return false;
			}
		}

		int min;
		int max;
		// v0 = abs(v1)
		if (d1.min() >= 0) {
			min = d1.min();
			max = d1.max();
		} else if (d1.max() <= 0) {
			min = -d1.max();
			max = -d1.min();
		} else {
			min = 0;
			max = Math.max(-d1.min(), d1.max());
		}
		d0 = d0.capInterval(min, max);
		if (d0.isEmpty())
			return false;
		v0.updateDomain(d0, trail);
		// abs(v1) = v0
		min = d0.min();
		max = d0.max();
		d1 = d1.capInterval(-max, max);
		if (d1.isEmpty())
			return false;
		if (min > 0)
			d1 = d1.delete(-min + 1, min - 1);
		v1.updateDomain(d1, trail);

		return true;
	}

	private boolean satisfySIGN(Variable v0, Variable v1, Trail trail) {
		IntDomain d0 = (IntDomain) v0.getDomain();
		IntDomain d1 = (IntDomain) v1.getDomain();

		if (d1.size() == 1) {
			// v0 = sign(v1)
			int sign = 0;
			if (d1.value() < 0) {
				sign = -1;
			} else if (d1.value() > 0) {
				sign = 1;
			}
			if (!d0.contains(sign))
				return false;
			if (d0.size() > 1)
				v0.updateDomain(new IntDomain(sign), trail);
			return true;
		} else if (d0.size() == 1) {
			// sign(v1) = v0
			int sign = d0.value();
			if (sign < 0) {
				if (d1.max() >= 0) {
					d1 = d1.capInterval(IntDomain.MIN_VALUE, -1);
					if (d1.isEmpty())
						return false;
					v1.updateDomain(d1, trail);
				}
				return true;
			} else if (sign == 0) {
				if (!d1.contains(0))
					return false;
				if (d1.size() > 1)
					v1.updateDomain(new IntDomain(0), trail);
				return true;
			} else if (sign > 0) {
				if (d1.min() <= 0) {
					d1 = d1.capInterval(1, IntDomain.MAX_VALUE);
					if (d1.isEmpty())
						return false;
					v1.updateDomain(d1, trail);
				}
				return true;
			}
			return false;
		}

		// v0 = sign(v1)
		if (!(-1 <= d0.min() && d0.max() <= 1)) {
			d0 = d0.capInterval(-1, 1);
		}
		if (d1.min() >= 0)
			d0 = d0.delete(-1);
		if (!d1.contains(0))
			d0 = d0.delete(0);
		if (d1.max() <= 0)
			d0 = d0.delete(1);

		// sign(v1) = v0
		if (!d0.contains(-1)) {
			if (d1.min() < 0)
				d1 = d1.capInterval(0, IntDomain.MAX_VALUE);
		}
		if (!d0.contains(0)) {
			d1 = d1.delete(0);
		}
		if (!d0.contains(1)) {
			if (d1.max() > 0)
				d1 = d1.capInterval(IntDomain.MIN_VALUE, 0);
		}
		if (d0.isEmpty())
			return false;
		if (d1.isEmpty())
			return false;
		v0.updateDomain(d0, trail);
		v1.updateDomain(d1, trail);

		return true;
	}

	public boolean satisfy(Trail trail) {
		switch (func) {
		case NEGATE:
			return satisfyNEGATE(v[0], v[1], trail);
		case ABS:
			return satisfyABS(v[0], v[1], trail);
		case SIGN:
			return satisfySIGN(v[0], v[1], trail);
		}
		return false;
	}

	public String toString() {
		String a = "";
		switch (func) {
		case NEGATE:
			a = "NEGATE";
			break;
		case ABS:
			a = "ABS";
			break;
		case SIGN:
			a = "SIGN";
			break;
		}
		return "IntFunc(" + a + "," + Constraint.toString(v) + ")";
	}
}
