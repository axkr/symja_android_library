/*
 * @(#)TabooSearch.java
 */
package jp.ac.kobe_u.cs.cream;

import java.util.Iterator;

/**
 * Taboo Search.
 * Only useful for the optimization problems containing {@link Serialized} constraints
 * in the current implementation.
 * The default exchangeRate value for {@link ParallelSolver}  
 * is 0.8.
 * 
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class TabooSearch extends LocalSearch {
	/**
	 * The length of the taboo list.
	 * The default value is 16.
	 */
	public int tabooLength = 16;

	private Operation[] taboo = null;

	private int taboo_i;

	public TabooSearch(Network network) {
		this(network, DEFAULT, null);
	}

	public TabooSearch(Network network, int option) {
		this(network, option, null);
	}

	public TabooSearch(Network network, String name) {
		this(network, DEFAULT, name);
	}

	public TabooSearch(Network network, int option, String name) {
		super(network, option, name);
		setExchangeRate(0.8);
	}

	/**
	 * Allocates a new taboo list.
	 */
	public void clearTaboo() {
		taboo = new Operation[tabooLength];
		for (int i = 0; i < taboo.length; i++) {
			taboo[i] = null;
		}
		taboo_i = 0;
	}

	/**
	 * Returns true when the operation <tt>op</tt> is a taboo.
	 * @param op the operation to be checked
	 * @return true when <tt>op</tt> is a taboo
	 */
	public boolean isTaboo(Operation op) {
		if (taboo == null)
			return false;
		for (int i = 0; i < taboo.length; i++) {
			if (taboo[i] != null && op.isTaboo(taboo[i]))
				return true;
		}
		return false;
	}

	/**
	 * Adds the operation <tt>op</tt> to the taboo list.
	 * @param op the operation to be added
	 */
	public void addTaboo(Operation op) {
		taboo[taboo_i] = op;
		taboo_i = (taboo_i + 1) % taboo.length;
	}

	public void startSearch() {
		super.startSearch();
		clearTaboo();
	}

	public void nextSearch() {
		Operation locallyBestOp = null;
		Solution locallyBestSol = null;
		int locallyBest = IntDomain.MAX_VALUE;
		solution = getCandidate();
		if (solution == null)
			return;
		Code code = solution.getCode();
		while (!isAborted()) {
			Iterator<Operation> ops = code.operations().iterator();
			while (ops.hasNext() && !isAborted()) {
				Operation op = ops.next();
				if (isTaboo(op))
					continue;
				code.setTo(network);
				op.applyTo(network);
				Solution sol = solver.findBest(iterationTimeout);
				if (sol == null)
					continue;
				int value = sol.getObjectiveIntValue();
				if (!isBetter(value, locallyBest))
					continue;
				locallyBest = value;
				locallyBestOp = op;
				locallyBestSol = sol;
			}
			if (locallyBestOp != null)
				break;
			clearTaboo();
		}
		if (isAborted())
			return;
		code.setTo(network);
		locallyBestOp.applyTo(network);
		solution = locallyBestSol;
		addTaboo(locallyBestOp);
	}
}
