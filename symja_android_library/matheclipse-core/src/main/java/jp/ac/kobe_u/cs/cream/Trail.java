/*
 * @(#)Trail.java
 */
package jp.ac.kobe_u.cs.cream;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Trail class implements trail stacks.
 * 
 * @see DefaultSolver
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Trail {
	private Stack<Object[]> trail = new Stack<Object[]>();

	/**
	 * Returns the size of the trail stack.
	 * @return the size
	 */
	public int size() {
		return trail.size();
	}

	/**
	 * Pushes a pair of the variable <tt>v</tt> and its domain
	 * to the trail stack.
	 * This is invoked for each assignment to the variable 
	 * by {@link Variable#updateDomain(Domain, Trail)}.
	 * @param v the variable
	 */
	public void push(Variable v) {
		Object[] pair = { v, v.getDomain() };
		trail.push(pair);
	}

	/**
	 * Undoes the previous assignments by 
	 * popping the variable-domain pair from the trail
	 * until the size becomes <tt>size0</tt>.
	 * @param size0 the size to be after undoing
	 * @throws EmptyStackException
	 */
	public void undo(int size0) throws EmptyStackException {
		for (int size = trail.size(); size > size0; size--) {
			Object[] pair = trail.pop();
			Variable v = (Variable) pair[0];
			v.setDomain((Domain) pair[1]);
		}
	}
}
