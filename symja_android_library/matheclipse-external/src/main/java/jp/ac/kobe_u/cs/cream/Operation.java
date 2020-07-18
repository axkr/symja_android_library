/*
 * @(#)Operation.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * Operations.
 * See {@link Code} for more details.
 * 
 * @see Code
 * @see Condition
 * @see Serialized
 * @see LocalSearch
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public abstract class Operation {

	/**
	 * Applies the operation to the network
	 * to change the {@linkplain Condition condition}
	 * for finding a neighbor solution.
	 * @param network the network
	 */
	public abstract void applyTo(Network network);

	/**
	 * Returns true when <tt>op</tt> is a taboo operation
	 * relative to this operation (for example, <tt>op</tt>
	 * is the same operation with this operation).
	 * @param op the operation to be checked
	 * @return true when <tt>op</tt> is a taboo
	 * @see TabooSearch
	 */
	public abstract boolean isTaboo(Operation op);

}
