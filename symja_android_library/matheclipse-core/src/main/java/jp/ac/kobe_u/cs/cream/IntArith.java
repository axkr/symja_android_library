/*
 * @(#)IntArith.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * IntArith constraint implements
 * arithmetic binary operations on integers.
 * 
 * @see IntFunc
 * @see IntComparison
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class IntArith extends Constraint {
	/**
	 * Addition operation
	 */
	public static final int ADD = 0;

	/**
	 * Subtraction operation
	 */
	public static final int SUBTRACT = 1;

	/**
	 * Multiplication operation
	 */
	public static final int MULTIPLY = 2;

	// public static final int DIVIDE = 3;
	// public static final int MOD = 4;
	// public static final int POW = 5;
	/**
	 * Max operation
	 */
	public static final int MAX = 6;

	/**
	 * Min operation
	 */
	public static final int MIN = 7;

	private int operation;

	private Variable[] v;

	/**
	 * Adds the constraint of <tt>v0 == v1 op v2</tt>
	 * to the constraint network <tt>net</tt>.
	 * @param net the constraint network
	 * @param op the operation
	 * @param v0 the result
	 * @param v1 the first operand
	 * @param v2 the second operand
	 */
	public IntArith(Network net, int op, Variable v0, Variable v1, Variable v2) {
		this(net, op, new Variable[] { v0, v1, v2 });
	}

	public IntArith(Network net, int op, Variable v0, Variable v1, int x2) {
		this(net, op, v0, v1, new IntVariable(net, x2));
	}

	public IntArith(Network net, int op, Variable v0, int x1, Variable v2) {
		this(net, op, v0, new IntVariable(net, x1), v2);
	}

	public IntArith(Network net, int op, int x0, Variable v1, Variable v2) {
		this(net, op, new IntVariable(net, x0), v1, v2);
	}

	private IntArith(Network net, int op, Variable[] v) {
		super(net);
		operation = op;
		this.v = v;
	}

	public Constraint copy(Network net) {
		return new IntArith(net, operation, Constraint.copy(this.v, net));
	}

	public boolean isModified() {
		return isModified(v);
	}

	private boolean satisfyADD(Variable v0, Variable v1, Variable v2,
			Trail trail) {
		IntDomain d0 = (IntDomain) v0.getDomain();
		IntDomain d1 = (IntDomain) v1.getDomain();
		IntDomain d2 = (IntDomain) v2.getDomain();

		if (d1.size() == 1 && d2.size() == 1) {
			// v0 = v1 + v2
			int value = d1.value() + d2.value();
			if (!d0.contains(value))
				return false;
			if (d0.size() > 1)
				v0.updateDomain(new IntDomain(value), trail);
			return true;
		} else if (d0.size() == 1 && d2.size() == 1) {
			// v1 = v0 - v2
			int value = d0.value() - d2.value();
			if (!d1.contains(value))
				return false;
			if (d1.size() > 1)
				v1.updateDomain(new IntDomain(value), trail);
			return true;
		} else if (d0.size() == 1 && d1.size() == 1) {
			// v2 = v0 - v1
			int value = d0.value() - d1.value();
			if (!d2.contains(value))
				return false;
			if (d2.size() > 1)
				v2.updateDomain(new IntDomain(value), trail);
			return true;
		}

		// v0 = v1 + v2
		d0 = d0.capInterval(d1.min() + d2.min(), d1.max() + d2.max());
		if (d0.isEmpty())
			return false;
		v0.updateDomain(d0, trail);
		// v1 = v0 - v2
		d1 = d1.capInterval(d0.min() - d2.max(), d0.max() - d2.min());
		if (d1.isEmpty())
			return false;
		v1.updateDomain(d1, trail);
		// v2 = v0 - v1
		d2 = d2.capInterval(d0.min() - d1.max(), d0.max() - d1.min());
		if (d2.isEmpty())
			return false;
		v2.updateDomain(d2, trail);

		return true;
	}

	private int toInt(long x) {
		return (int) Math.max(IntDomain.MIN_VALUE, Math.min(
				IntDomain.MAX_VALUE, x));
	}

	private int min(int[] x) {
		int m = x[0];
		for (int i = 1; i < x.length; i++)
			m = Math.min(m, x[i]);
		return m;
	}

	private int max(int[] x) {
		int m = x[0];
		for (int i = 1; i < x.length; i++)
			m = Math.max(m, x[i]);
		return m;
	}

	private IntDomain multiply(IntDomain d0, IntDomain d1, IntDomain d2) {
		if (!d1.contains(0) && !d2.contains(0)) {
			d0 = d0.delete(0);
			if (d0.isEmpty())
				return IntDomain.EMPTY;
		}
		int[] x = { toInt((long) d1.min() * (long) d2.min()),
				toInt((long) d1.min() * (long) d2.max()),
				toInt((long) d1.max() * (long) d2.min()),
				toInt((long) d1.max() * (long) d2.max()) };
		d0 = d0.capInterval(min(x), max(x));
		return d0;
	}

	private IntDomain divide(IntDomain d0, IntDomain d1, IntDomain d2) {
		if (!d1.contains(0)) {
			d0 = d0.delete(0);
			if (d0.isEmpty())
				return IntDomain.EMPTY;
		}
		if (d2.contains(0)) {
			return d0;
		} else if (d2.max() < 0 || 0 < d2.min()) {
			int[] x = { d1.min() / d2.min(), d1.max() / d2.min(),
					d1.min() / d2.max(), d1.max() / d2.max() };
			d0 = d0.capInterval(min(x), max(x));
		} else {
			int[] x = { d1.min() / d2.min(), d1.max() / d2.min(),
					d1.min() / d2.max(), d1.max() / d2.max(), d1.min(),
					d1.max(), -d1.min(), -d1.max() };
			d0 = d0.capInterval(min(x), max(x));
		}
		return d0;
	}

	private boolean satisfyMULTIPLY(Variable v0, Variable v1, Variable v2,
			Trail trail) {
		IntDomain d0 = (IntDomain) v0.getDomain();
		IntDomain d1 = (IntDomain) v1.getDomain();
		IntDomain d2 = (IntDomain) v2.getDomain();

		if (d1.size() == 1 && d2.size() == 1) {
			// v0 = v1 * v2
			int value = toInt((long) d1.value() * (long) d2.value());
			if (!d0.contains(value))
				return false;
			if (d0.size() > 1)
				v0.updateDomain(new IntDomain(value), trail);
			return true;
		} else if (d0.size() == 1 && d2.size() == 1) {
			// v1 = v0 / v2
			int x = d0.value();
			int y = d2.value();
			if (y == 0) {
				return x == 0;
			} else if (x % y != 0) {
				return false;
			}
			int value = x / y;
			if (!d1.contains(value))
				return false;
			if (d1.size() > 1)
				v1.updateDomain(new IntDomain(value), trail);
			return true;
		} else if (d0.size() == 1 && d1.size() == 1) {
			// v2 = v0 / v1
			int x = d0.value();
			int y = d1.value();
			if (y == 0) {
				return x == 0;
			} else if (x % y != 0) {
				return false;
			}
			int value = x / y;
			if (!d2.contains(value))
				return false;
			if (d2.size() > 1)
				v2.updateDomain(new IntDomain(value), trail);
			return true;
		}

		d0 = multiply(d0, d1, d2);
		if (d0.isEmpty())
			return false;
		d1 = divide(d1, d0, d2);
		if (d1.isEmpty())
			return false;
		d2 = divide(d2, d0, d1);
		if (d2.isEmpty())
			return false;

		if (d0 != v0.getDomain())
			v0.updateDomain(d0, trail);
		if (d1 != v1.getDomain())
			v1.updateDomain(d1, trail);
		if (d2 != v2.getDomain())
			v2.updateDomain(d2, trail);
		return true;
	}

	private boolean satisfyMAX(Variable v0, Variable v1, Variable v2,
			Trail trail) {
		IntDomain d0 = (IntDomain) v0.getDomain();
		IntDomain d1 = (IntDomain) v1.getDomain();
		IntDomain d2 = (IntDomain) v2.getDomain();

		if (d1.size() == 1 && d2.size() == 1) {
			// v0 = max(v1, v2)
			int value = Math.max(d1.value(), d2.value());
			if (!d0.contains(value))
				return false;
			if (d0.size() > 1)
				v0.updateDomain(new IntDomain(value), trail);
			return true;
		}
		if (d0.size() == 1) {
			// max(v1, v2) = v0
			int value = d0.value();
			if (!d1.contains(value) && !d2.contains(value))
				return false;
			if (d1.max() > value) {
				d1.capInterval(IntDomain.MIN_VALUE, value);
				if (d1.isEmpty())
					return false;
				v1.updateDomain(d1, trail);
			}
			if (d2.max() > value) {
				d2.capInterval(IntDomain.MIN_VALUE, value);
				if (d2.isEmpty())
					return false;
				v2.updateDomain(d2, trail);
			}
			return true;
		}

		// v0 = max(v1, v2)
		int min = Math.max(d1.min(), d2.min());
		int max = Math.max(d1.max(), d2.max());
		d0 = d0.capInterval(min, max);
		if (d0.isEmpty())
			return false;
		v0.updateDomain(d0, trail);

		// max(v1, v2) = v0
		if (d1.max() > d0.max())
			d1 = d1.capInterval(IntDomain.MIN_VALUE, d0.max());
		if (d2.max() > d0.max())
			d2 = d2.capInterval(IntDomain.MIN_VALUE, d0.max());
		if (d1.max() < d0.min()) {
			d0 = (IntDomain) d0.cap(d2);
			d2 = d0;
		}
		if (d2.max() < d0.min()) {
			d0 = (IntDomain) d0.cap(d1);
			d1 = d0;
		}
		if (d0.isEmpty())
			return false;
		if (d1.isEmpty())
			return false;
		if (d2.isEmpty())
			return false;
		v0.updateDomain(d0, trail);
		v1.updateDomain(d1, trail);
		v2.updateDomain(d2, trail);
		return true;
	}

	private boolean satisfyMIN(Variable v0, Variable v1, Variable v2,
			Trail trail) {
		IntDomain d0 = (IntDomain) v0.getDomain();
		IntDomain d1 = (IntDomain) v1.getDomain();
		IntDomain d2 = (IntDomain) v2.getDomain();

		if (d1.size() == 1 && d2.size() == 1) {
			// v0 = min(v1, v2)
			int value = Math.min(d1.value(), d2.value());
			if (!d0.contains(value))
				return false;
			if (d0.size() > 1)
				v0.updateDomain(new IntDomain(value), trail);
			return true;
		}
		if (d0.size() == 1) {
			int value = d0.value();
			if (!d1.contains(value) && !d2.contains(value))
				return false;
			// ???
			if (d1.min() < value) {
				d1.capInterval(value, IntDomain.MAX_VALUE);
				if (d1.isEmpty())
					return false;
				v1.updateDomain(d1, trail);
			}
			if (d2.min() < value) {
				d2.capInterval(value, IntDomain.MAX_VALUE);
				if (d2.isEmpty())
					return false;
				v2.updateDomain(d2, trail);
			}
			return true;
		}

		// v0 = min(v1, v2)
		int min = Math.min(d1.min(), d2.min());
		int max = Math.min(d1.max(), d2.max());
		d0 = d0.capInterval(min, max);
		if (d0.isEmpty())
			return false;
		v0.updateDomain(d0, trail);

		//
		if (d1.min() < d0.min())
			d1 = d1.capInterval(d0.min(), IntDomain.MAX_VALUE);
		if (d2.min() < d0.min())
			d2 = d2.capInterval(d0.min(), IntDomain.MAX_VALUE);
		if (d1.min() > d0.max()) {
			d0 = (IntDomain) d0.cap(d2);
			d2 = d0;
		}
		if (d2.min() > d0.max()) {
			d0 = (IntDomain) d0.cap(d1);
			d1 = d0;
		}
		if (d0.isEmpty())
			return false;
		if (d1.isEmpty())
			return false;
		if (d2.isEmpty())
			return false;
		v0.updateDomain(d0, trail);
		v1.updateDomain(d1, trail);
		v2.updateDomain(d2, trail);
		return true;
	}

	public boolean satisfy(Trail trail) {
		switch (operation) {
		case ADD:
			return satisfyADD(v[0], v[1], v[2], trail);
		case SUBTRACT:
			return satisfyADD(v[1], v[0], v[2], trail);
		case MULTIPLY:
			return satisfyMULTIPLY(v[0], v[1], v[2], trail);
		case MAX:
			return satisfyMAX(v[0], v[1], v[2], trail);
		case MIN:
			return satisfyMIN(v[0], v[1], v[2], trail);
		}
		return false;
	}

	public String toString() {
		StringBuilder a = new StringBuilder("IntArith(");
		switch (operation) {
		case ADD:
			a.append("ADD");
			break;
		case SUBTRACT:
			a.append("SUBTRACT");
			break;
		case MULTIPLY:
			a.append("MULTIPLY");
			break;
		case MAX:
			a.append("MAX");
			break;
		case MIN:
			a.append("MIN");
			break;
		}
		a.append(',').append(Constraint.toString(v)).append(')');
		return a.toString();
	}
}
