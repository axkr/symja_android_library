/*
 * @(#)Constraint.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * An abstract class for constraints. A constraint is a component of a
 * {@linkplain Network constraint network}. See {@link Network} for example
 * programs to construct constraints and add them to a constraint network.
 * 
 * @see Network
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public abstract class Constraint {
	private Network network;

	private int index = -1;

	/**
	 * Sole constructor. (for invocation by subclass constructors, typically
	 * implicit)
	 */
	protected Constraint(Network net) {
		network = net;
		network.add(this);
	}

	/**
	 * Returns the {@linkplain Network constraint network}.
	 * 
	 * @return the constraint network
	 */
	public Network getNetwork() {
		return network;
	}

	/**
	 * Sets the index of the constraint in the network. This is an internal
	 * method.
	 * 
	 * @param index
	 *            the index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Returns the index of the constraint in the network. This is an internal
	 * method.
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Creates a copy of this constraint for a new network <tt>net</tt>.
	 * 
	 * @return the copy of this constraint
	 */
	public abstract Constraint copy(Network net);

	protected void clearCondition() {
	}

	protected void setCondition(Condition condition) {
	}

	protected Condition extractCondition() {
		return null;
	}

	/**
	 * Returns true when some argument variable is modified.
	 * 
	 * @return true when some argument variable is modified
	 */
	public abstract boolean isModified();

	/**
	 * Performs consistency algorithm.
	 * Assignments are saved to the {@linkplain Trail trail stack}.
	 * @param trail the trail stack
	 * @return true when the consistency algorithm succeeds
	 */
	public abstract boolean satisfy(Trail trail);

	/**
	 * Returns the string representation of the constraint.
	 * @return the string representation
	 */
	public abstract String toString();
	
	protected static Variable copy(Variable v0, Network net) {
		int j = v0.getIndex();
		return net.getVariable(j);
	}

	protected static Variable[] copy(Variable[] v0, Network net) {
		Variable[] v = new Variable[v0.length];
		for (int i = 0; i < v0.length; i++) {
			int j = v0[i].getIndex();
			v[i] = net.getVariable(j);
		}
		return v;
	}

	protected static boolean isModified(Variable[] vars) {
		for (Variable v : vars) {
			if (v.isModified())
				return true;
		}
		return false;
	}

	protected static String toString(Variable[] vars) {
		StringBuilder s = new StringBuilder("{");
		if (vars != null) {
			String delim = "";
			for (Variable v : vars) {
				s.append(delim).append(v);
				delim = ",";
			}
		}
		s.append('}');
		return s.toString();
	}

	protected static String toString(int[] a) {
		StringBuilder s = new StringBuilder("{");
		if (a != null) {
			String delim = "";
			for (int i: a) {
				s.append(delim).append(i);
				delim = ",";
			}
		}
		s.append('}');
		return s.toString();
	}
}
