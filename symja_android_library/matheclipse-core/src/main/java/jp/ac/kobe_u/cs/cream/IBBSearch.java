/*
 * @(#)IBBSearch.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * Iterativ Branch-and-Bound Search.
 * Only useful for the optimization problems containing {@link Serialized} constraints
 * in the current implementation.
 * The default exchangeRate value for {@link ParallelSolver}  
 * is 0.8.
 * 
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class IBBSearch extends LocalSearch {
	private double clearRate = 0.8;

	public IBBSearch(Network network) {
		this(network, DEFAULT, null);
	}

	public IBBSearch(Network network, int option) {
		this(network, option, null);
	}

	public IBBSearch(Network network, String name) {
		this(network, DEFAULT, name);
	}

	public IBBSearch(Network network, int option, String name) {
		super(network, option, name);
		setExchangeRate(0.8);
	}

	/**
	 * Returns the clear rate.
	 * The clear rate is the rate of clearing the {@linkplain Condition conditions}
	 * to find the neighbor solution.
	 * The default value is 0.8.
	 * @return the clear rate
	 */
	public double getClearRate() {
		return clearRate;
	}

	/**
	 * Sets the clear rate.
	 * @param clearRate the clear rate to set
	 */
	public void setClearRate(double clearRate) {
		this.clearRate = clearRate;
	}

	private void bbSearch(long timeout) {
		if (isAborted())
			return;
		for (solver.start(timeout); solver.waitNext(); solver.resume()) {
			solution = solver.getSolution();
			success();
			if (isAborted())
				break;
		}
		solver.stop();
		solution = solver.getBestSolution();
	}

	public void startSearch() {
		bbSearch(initialTimeout);
	}

	public void nextSearch() {
		if (isAborted())
			return;
		solution = getCandidate();
		if (solution == null)
			return;
		Code code = solution.getCode();
		code = (Code) code.clone();
		Condition[] conditions = code.conditions;
		for (int i = 0; i < conditions.length; i++) {
			if (Math.random() < clearRate) {
				conditions[i] = null;
			}
		}
		code.setTo(network);
		bbSearch(iterationTimeout);
	}

}
