/*
 * @(#)Solution.java
 */
package jp.ac.kobe_u.cs.cream;

import java.util.List;

/**
 * Solutions. Solutions are returned by {@linkplain Solver constraint solvers}.
 * A solution consists of {@linkplain Domain domains} for variables and
 * {@linkplain Code a code}.
 * 
 * @see Solver
 * @see Domain
 * @see Code
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Solution {
	private Network network;

	private Domain objectiveDomain;

	private Domain[] bindings;

	private Code code;

	/**
	 * Constructs a solution from the given network.
	 * 
	 * @param network
	 *            the constraint network
	 */
	public Solution(Network network) {
		this.network = network;
		objectiveDomain = null;
		if (network.getObjective() != null) {
			objectiveDomain = network.getObjective().getDomain();
		}
		List<Variable> variables = network.getVariables();
		bindings = new Domain[variables.size()];
		for (Variable v : variables) {
			bindings[v.getIndex()] = v.getDomain();
		}
		code = new Code(network);
	}

	/**
	 * Returns the integer value of the objective variable in the solution.
	 * 
	 * @return the integer value of the objective variable
	 */
	public int getObjectiveIntValue() {
		return ((IntDomain) objectiveDomain).value();
	}

	/**
	 * Returns the domain of the given variable in the solution.
	 * 
	 * @param v
	 *            the variable
	 * @return the domain of the variable
	 */
	public Domain getDomain(Variable v) {
		return bindings[v.getIndex()];
	}

	/**
	 * Returns the integer value of the given variable in the solution.
	 * 
	 * @param v
	 *            the variable
	 * @return the integer value of the variable
	 */
	public int getIntValue(Variable v) {
		return ((IntDomain) getDomain(v)).value();
	}

	/**
	 * Returns the code of the solution.
	 * 
	 * @return the code of the solution
	 */
	public Code getCode() {
		return code;
	}

	/**
	 * Returns a readable string representation of this solution.
	 * 
	 * @return the readable string representation
	 */
	public String toString() {
		StringBuilder s = new StringBuilder();
		/*
		 * if (network.getObjective() != null) { s += "Objective: " +
		 * network.getObjective() + "=" + getObjectiveValue() + "\n"; }
		 */
		String delim = "";
		for (Variable v : network.getVariables()) {
			s.append(delim).append(v.getName()).append('=').append(getDomain(v));
			delim = ",";
		}
		return s.toString();
	}

}
