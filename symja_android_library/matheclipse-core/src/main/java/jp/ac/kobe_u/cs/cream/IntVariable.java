/*
 * @(#)IntVariable.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * Integer variables.
 * 
 * @see IntDomain
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class IntVariable extends Variable {

	/**
	 * Constructs an integer variable with {@linkplain IntDomain#FULL full domain}
	 * and a default name.
	 * @param net the network
	 */
	public IntVariable(Network net) {
		this(net, IntDomain.FULL, null);
	}

	/**
	 * Constructs an integer variable with {@linkplain IntDomain#FULL full domain}
	 * and the given name.
	 * @param net the network
	 * @param name the name of this variable
	 */
	public IntVariable(Network net, String name) {
		this(net, IntDomain.FULL, name);
	}

	/**
	 * Constructs an integer variable of the network with an initial integer
	 * domain <tt>d</tt> and a default name. This constructor is equivalent to
	 * <tt>IntVariable(network, d, null)</tt>.
	 * 
	 * @param net
	 *            the network
	 * @param d
	 *            the initial integer domain
	 */
	public IntVariable(Network net, IntDomain d) {
		this(net, d, null);
	}

	/**
	 * Constructs an integer variable of the network with an initial integer
	 * domain <tt>d</tt> and a name specified by the parameter <tt>name</tt>.
	 * When the parameter <tt>name</tt> is <tt>null</tt>, default names (<tt>v1</tt>,
	 * <tt>v2</tt>, and so on) are used.
	 * 
	 * @param net
	 *            the network
	 * @param d
	 *            the initial integer domain
	 * @param name
	 *            the name of the variable, or <tt>null</tt> for a default
	 *            name
	 */
	public IntVariable(Network net, IntDomain d, String name) {
		super(net, d, name);
	}

	/**
	 * Constructs an integer variable of a singleton domain
	 * and a default name. 
	 * @param net the network
	 * @param value the singleton value
	 */
	public IntVariable(Network net, int value) {
		this(net, new IntDomain(value), null);
	}

	/**
	 * Constructs an integer variable of a singleton domain
	 * and the given name.
	 * @param net the network
	 * @param value the singleton value
	 * @param name the name of the variable
	 */
	public IntVariable(Network net, int value, String name) {
		this(net, new IntDomain(value), name);
	}

	/**
	 * Constructs an integer variable of the interval <tt>lo..hi</tt>
	 * and a default name.
	 * @param net the network
	 * @param lo the lower bound
	 * @param hi the upper bound
	 */
	public IntVariable(Network net, int lo, int hi) {
		this(net, new IntDomain(lo, hi), null);
	}

	/**
	 * Constructs an integer variable of the interval <tt>lo..hi</tt>
	 * and the given name.
	 * @param net the network
	 * @param lo the lower bound
	 * @param hi the upper bound
	 * @param name the name of the variable
	 */
	public IntVariable(Network net, int lo, int hi, String name) {
		this(net, new IntDomain(lo, hi), name);
	}

	/**
	 * Returns a new integer variable which is equal to <tt>this + v</tt>.
	 * @param v the variable to be added
	 * @return the new integer variable
	 * @see IntArith
	 */
	public IntVariable add(IntVariable v) {
		Network net = getNetwork();
		IntVariable x = new IntVariable(net);
		// x = this + v
		new IntArith(net, IntArith.ADD, x, this, v);
		return x;
	}

	/**
	 * Returns a new integer variable which is equal to <tt>this + value</tt>.
	 * @param value the value to be added
	 * @return the new integer variable
	 * @see IntArith
	 */
	public IntVariable add(int value) {
		Network net = getNetwork();
		IntVariable x = new IntVariable(net);
		// x = this + value
		new IntArith(net, IntArith.ADD, x, this, new IntVariable(net, value));
		return x;
	}

	/**
	 * Returns a new integer variable which is equal to <tt>this - v</tt>.
	 * @param v the variable to be subtracted
	 * @return the new integer variable
	 * @see IntArith
	 */
	public IntVariable subtract(IntVariable v) {
		Network net = getNetwork();
		IntVariable x = new IntVariable(net);
		// x = this - v
		new IntArith(net, IntArith.SUBTRACT, x, this, v);
		return x;
	}

	/**
	 * Returns a new integer variable which is equal to <tt>this - value</tt>.
	 * @param value the value to be subtracted
	 * @return the new integer variable
	 * @see IntArith
	 */
	public IntVariable subtract(int value) {
		Network net = getNetwork();
		IntVariable x = new IntVariable(net);
		// x = this - value
		new IntArith(net, IntArith.SUBTRACT, x, this, new IntVariable(net,
				value));
		return x;
	}

	/**
	 * Returns a new integer variable which is equal to <tt>this * v</tt>.
	 * @param v the variable to be multiplied
	 * @return the new integer variable
	 * @see IntArith
	 */
	public IntVariable multiply(IntVariable v) {
		Network net = getNetwork();
		IntVariable x = new IntVariable(net);
		// x = this * v
		new IntArith(net, IntArith.MULTIPLY, x, this, v);
		return x;
	}

	/**
	 * Returns a new integer variable which is equal to <tt>this * value</tt>.
	 * @param value the value to be multiplied
	 * @return the new integer variable
	 * @see IntArith
	 */
	public IntVariable multiply(int value) {
		Network net = getNetwork();
		IntVariable x = new IntVariable(net);
		// x = this * value
		new IntArith(net, IntArith.MULTIPLY, x, this, new IntVariable(net,
				value));
		return x;
	}

	/**
	 * Returns a new integer variable which is equal to <tt>max(this, v)</tt>.
	 * @param v the variable
	 * @return the new integer variable
	 * @see IntArith
	 */
	public IntVariable max(IntVariable v) {
		Network net = getNetwork();
		IntVariable x = new IntVariable(net);
		// x = max(this, v)
		new IntArith(net, IntArith.MAX, x, this, v);
		return x;
	}

	/**
	 * Returns a new integer variable which is equal to <tt>max(this, value)</tt>.
	 * @param value the value
	 * @return the new integer variable
	 * @see IntArith
	 */
	public IntVariable max(int value) {
		Network net = getNetwork();
		IntVariable x = new IntVariable(net);
		// x = max(this, value)
		new IntArith(net, IntArith.MAX, x, this, new IntVariable(net, value));
		return x;
	}

	/**
	 * Returns a new integer variable which is equal to <tt>min(this, v)</tt>.
	 * @param v the variable
	 * @return the new integer variable
	 * @see IntArith
	 */
	public IntVariable min(IntVariable v) {
		Network net = getNetwork();
		IntVariable x = new IntVariable(net);
		// x = min(this, v)
		new IntArith(net, IntArith.MIN, x, this, v);
		return x;
	}

	/**
	 * Returns a new integer variable which is equal to <tt>min(this, value)</tt>.
	 * @param value the value
	 * @return the new integer variable
	 * @see IntArith
	 */
	public IntVariable min(int value) {
		Network net = getNetwork();
		IntVariable x = new IntVariable(net);
		// x = min(this, value)
		new IntArith(net, IntArith.MIN, x, this, new IntVariable(net, value));
		return x;
	}

	/**
	 * Returns a new integer variable which is equal to <tt>-this</tt>.
	 * @return the new integer variable
	 * @see IntFunc
	 */
	public IntVariable negate() {
		Network net = getNetwork();
		IntVariable x = new IntVariable(net);
		// x = - this
		new IntFunc(net, IntFunc.NEGATE, x, this);
		return x;
	}

	/**
	 * Returns a new integer variable which is equal to <tt>abs(this)</tt>.
	 * @return the new integer variable
	 * @see IntFunc
	 */
	public IntVariable abs() {
		Network net = getNetwork();
		IntVariable x = new IntVariable(net);
		// x = abs(this)
		new IntFunc(net, IntFunc.ABS, x, this);
		return x;
	}

	/**
	 * Returns a new integer variable which is equal to <tt>sign(this)</tt>.
	 * The function sign(x) gives -1, 0, +1 
	 * when x&lt;0, x=0, x&gt;0 respectively.
	 * @return the new integer variable
	 * @see IntFunc
	 */
	public IntVariable sign() {
		Network net = getNetwork();
		IntVariable x = new IntVariable(net);
		// x = sign(this)
		new IntFunc(net, IntFunc.SIGN, x, this);
		return x;
	}

	/**
	 * Adds a new constraint <tt>this == v</tt>.
	 * @param v the variable to be compared
	 * @see Equals
	 */
	public void equals(IntVariable v) {
		Network net = getNetwork();
		new Equals(net, this, v);
	}

	/**
	 * Adds a new constraint <tt>this == value</tt>.
	 * @param value the value to be compared
	 * @see Equals
	 */
	public void equals(int value) {
		Network net = getNetwork();
		new Equals(net, this, new IntVariable(net, value));
	}

	/**
	 * Adds a new constraint <tt>this != v</tt>.
	 * @param v the variable to be compared
	 * @see NotEquals
	 */
	public void notEquals(IntVariable v) {
		Network net = getNetwork();
		new NotEquals(net, this, v);
	}

	/**
	 * Adds a new constraint <tt>this != value</tt>.
	 * @param value the value to be compared
	 * @see NotEquals
	 */
	public void notEquals(int value) {
		Network net = getNetwork();
		new NotEquals(net, this, new IntVariable(net, value));
	}

	/**
	 * Adds a new constraint <tt>this &lt;= v</tt>.
	 * @param v the variable to be compared
	 * @see IntComparison
	 */
	public void le(IntVariable v) {
		Network net = getNetwork();
		new IntComparison(net, IntComparison.LE, this, v);
	}

	/**
	 * Adds a new constraint <tt>this &lt;= value</tt>.
	 * @param value the value to be compared
	 * @see IntComparison
	 */
	public void le(int value) {
		Network net = getNetwork();
		new IntComparison(net, IntComparison.LE, this, new IntVariable(net,
				value));
	}

	/**
	 * Adds a new constraint <tt>this &lt; v</tt>.
	 * @param v the variable to be compared
	 * @see IntComparison
	 */
	public void lt(IntVariable v) {
		Network net = getNetwork();
		new IntComparison(net, IntComparison.LT, this, v);
	}

	/**
	 * Adds a new constraint <tt>this &lt; value</tt>.
	 * @param value the value to be compared
	 * @see IntComparison
	 */
	public void lt(int value) {
		Network net = getNetwork();
		new IntComparison(net, IntComparison.LT, this, new IntVariable(net,
				value));
	}

	/**
	 * Adds a new constraint <tt>this &gt;= v</tt>.
	 * @param v the variable to be compared
	 * @see IntComparison
	 */
	public void ge(IntVariable v) {
		Network net = getNetwork();
		new IntComparison(net, IntComparison.GE, this, v);
	}

	/**
	 * Adds a new constraint <tt>this &gt;= value</tt>.
	 * @param value the value to be compared
	 * @see IntComparison
	 */
	public void ge(int value) {
		Network net = getNetwork();
		new IntComparison(net, IntComparison.GE, this, new IntVariable(net,
				value));
	}

	/**
	 * Adds a new constraint <tt>this &gt; v</tt>.
	 * @param v the variable to be compared
	 * @see IntComparison
	 */
	public void gt(IntVariable v) {
		Network net = getNetwork();
		new IntComparison(net, IntComparison.GT, this, v);
	}

	/**
	 * Adds a new constraint <tt>this &gt; value</tt>.
	 * @param value the value to be compared
	 * @see IntComparison
	 */
	public void gt(int value) {
		Network net = getNetwork();
		new IntComparison(net, IntComparison.GT, this, new IntVariable(net,
				value));
	}
}
