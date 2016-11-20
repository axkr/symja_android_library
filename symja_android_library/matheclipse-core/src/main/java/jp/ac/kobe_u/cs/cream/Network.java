/*
 * @(#)Network.java
 */
package jp.ac.kobe_u.cs.cream;

import java.util.ArrayList;
import java.util.List;

/**
 * Constraint networks. A constraint network consists of
 * {@linkplain Variable variables}, {@linkplain Constraint constraints}, and
 * an objective variable (optional). Variables and constraints are added by
 * <tt>add</tt> methods:
 * 
 * <pre>
 * Network net = new Network();
 * Domain d = new IntDomain(0, IntDomain.MAX_VALUE);
 * Variable x = new Variable(net, d);
 * Variable y = new Variable(net, d);
 * new NotEquals(net, x, y);
 * </pre>
 * 
 * <p>
 * Please note that any variable or any constraint can not be added to two
 * different networks. In other words, a network can not share a variable or a
 * constraint with another network.
 * 
 * @see Variable
 * @see Constraint
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Network implements Cloneable {
	private Variable objective;

	private List<Variable> variables;

	private List<Constraint> constraints;

	/**
	 * Constructs an empty constraint network.
	 */
	public Network() {
		objective = null;
		variables = new ArrayList<Variable>();
		constraints = new ArrayList<Constraint>();
	}

	/**
	 * Sets the objective variable of this network. If <tt>v</tt> is
	 * <tt>null</tt>, this network is set to have no objective variable. If
	 * <tt>v</tt> is not <tt>null</tt>, the objective variable is
	 * automatically added to the network.
	 * 
	 * @param v
	 *            the objective variable
	 */
	public void setObjective(Variable v) {
		objective = v;
		if (v != null) {
			add(v);
		}
	}

	/**
	 * Adds a variable to this network. If the variable is already in the
	 * nework, this invocation has no effect.
	 * 
	 * @param v
	 *            the variable to be added
	 * @return the variable itself
	 * @throws NullPointerException
	 *             if <tt>v</tt> is <tt>null</tt>
	 * @throws IllegalArgumentException
	 *             if <tt>v</tt> is already added to another network
	 */
	protected Variable add(Variable v) throws IllegalArgumentException {
		if (!variables.contains(v)) {
			if (v.getIndex() >= 0) {
				throw new IllegalArgumentException();
			}
			v.setIndex(variables.size());
			variables.add(v);
		}
		return v;
	}

	/**
	 * Adds a constraint to this network. If the constraint is already in the
	 * nework, this invocation has no effect. Please note that variables in the
	 * constraint are not automatically added.
	 * 
	 * @param c
	 *            the constraint to be added
	 * @return the constraint itself
	 * @throws NullPointerException
	 *             if <tt>c</tt> is <tt>null</tt>
	 * @throws IllegalArgumentException
	 *             if <tt>c</tt> is already added to another network
	 */
	protected Constraint add(Constraint c) throws IllegalArgumentException {
		if (!constraints.contains(c)) {
			if (c.getIndex() >= 0) {
				throw new IllegalArgumentException();
			}
			c.setIndex(constraints.size());
			constraints.add(c);
		}
		return c;
	}

	/**
	 * Returns the objective variable of this network.
	 * 
	 * @return the objective variable or <tt>null</tt> if the network has no
	 *         objective variable
	 */
	public Variable getObjective() {
		return objective;
	}

	/**
	 * Returns the list of variables of this network.
	 * 
	 * @return the list of variables
	 */
	public List<Variable> getVariables() {
		return variables;
	}

	/**
	 * Returns the list of constraints of this network.
	 * 
	 * @return the list of constraints
	 */
	public List<Constraint> getConstraints() {
		return constraints;
	}

	/**
	 * Returns the <tt>i</tt>-th variable of this network. The index starts
	 * from 0.
	 * 
	 * @param i
	 *            the index value of the variable to be returned
	 * @return the <tt>i</tt>-th variable
	 * @throws IndexOutOfBoundsException
	 *             if <tt>i</tt> is out-of-range
	 */
	public Variable getVariable(int i) {
		return getVariables().get(i);
	}

	/**
	 * Returns the <tt>i</tt>-th constraint of this network. The index starts
	 * from 0.
	 * 
	 * @param i
	 *            the index value of the constraint to be returned
	 * @return the <tt>i</tt>-th constraint
	 * @throws IndexOutOfBoundsException
	 *             if <tt>i</tt> is out-of-range
	 */
	public Constraint getConstraint(int i) {
		return getConstraints().get(i);
	}

	/**
	 * Returns a copy of this network. The new network has the same structure as
	 * the original network.
	 * 
	 * @return a copy of this network
	 */
	public Object clone() {
		Network net = new Network();
		for (Variable v : variables) {
			Variable v1 = v.copy(net);
			if (v.getIndex() != v1.getIndex())
				throw new IllegalArgumentException();
		}
		for (Constraint c: constraints) { 
			Constraint c1 = c.copy(net);
			if (c.getIndex() != c1.getIndex())
				throw new IllegalArgumentException();
		}
		if (objective != null) {
			net.setObjective(net.getVariable(objective.getIndex()));
		}
		return net;
	}

	/**
	 * Returns a readable string representation of this network.
	 * 
	 * @return the readable string representation
	 */
	public String toString() {
		StringBuilder s = new StringBuilder("");
		if (objective != null) {
			s.append("Objective: ").append(objective.getName())
			 .append('=').append(objective.getDomain())
			 .append('\n');
		}
		for (Variable v : variables) {
			s.append(v.getName()).append('=').append(v.getDomain())
			 .append('\n');
		}
		for (Constraint c : constraints) {
			s.append(c.toString()).append('\n');
		}
		return s.toString();
	}
}
