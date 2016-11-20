/*
 * @(#)Code.java
 */
package jp.ac.kobe_u.cs.cream;

import java.util.ArrayList;
import java.util.List;

/**
 * Code is a list of {@linkplain Condition conditions} extracted from a
 * currently solved {@linkplain Network network}. A code represents additional
 * conditions for the current solution. You can generate neighbor conditions,
 * and use the conditions to find neighbor solutions as follows.
 * Only useful for the problems containing {@link Serialized} constraints
 * in the current implementation.
 * 
 * <pre>
 * // Get a code from a solution
 * Code code = solution.getCode();
 * // Get applicable operations for the code
 * List&lt;Operation&gt; operations = code.operations();
 * // Choose an operation to generate a neighbor condition
 * Operation op = operations.get(0);
 * // Set the code to the network
 * code.setTo(network);
 * // Apply the operation to set a neighbor condition
 * op.applyTo(network);
 * // Find a neighbor solution which satisfies the neighbor condition
 * Solution sol = solver.findBest();
 * </pre>
 * 
 * @see Solution
 * @see Condition
 * @see Operation
 * @see Serialized
 * @see LocalSearch
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Code implements Cloneable {
	public Condition[] conditions;

	private Code() {
	}

	/**
	 * Constructs a code from the given network.
	 * 
	 * @param network
	 *            the constraint network
	 */
	public Code(Network network) {
		List<Constraint> constraints = network.getConstraints();
		conditions = new Condition[constraints.size()];
		for (int i = 0; i < conditions.length; i++) {
			Constraint c = constraints.get(i);
			conditions[i] = c.extractCondition();
		}
	}

	/**
	 * Returns a copy of this code.
	 * 
	 * @return a copy of this code
	 */
	public Object clone() {
		Code code = new Code();
		code.conditions = conditions.clone();
		return code;
	}

	/**
	 * Sets the conditions of this code to the network.
	 * 
	 * @param network
	 *            the network
	 */
	public void setTo(Network network) {
		for (int i = 0; i < conditions.length; i++) {
			if (conditions[i] == null) {
				network.getConstraint(i).clearCondition();
			} else {
				conditions[i].setTo(network);
			}
		}
	}

	/**
	 * Returns possible operations applicable to the code.
	 * 
	 * @return possible operations
	 */
	public List<Operation> operations() {
		List<Operation> operations = new ArrayList<Operation>();
		for (Condition condition : conditions) {
			if (condition != null) {
				operations.addAll(condition.operations());
			}
		}
		return operations;
	}

}
