/*
 * @(#)SASearch.java
 */
package jp.ac.kobe_u.cs.cream;

import java.util.List;

/**
 * Simulated Annealing Search.
 * Only useful for the optimization problems containing {@link Serialized} constraints
 * in the current implementation.
 * The default exchangeRate value for {@link ParallelSolver}  
 * is 0.05.
 * 
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class SASearch extends LocalSearch {
	private double gamma = 0.999;

	private double temperature = 100.0;

	public SASearch(Network network) {
		this(network, DEFAULT, null);
	}

	public SASearch(Network network, int option) {
		this(network, option, null);
	}

	public SASearch(Network network, String name) {
		this(network, DEFAULT, name);
	}

	public SASearch(Network network, int option, String name) {
		super(network, option, name);
		setExchangeRate(0.05);
	}

	/**
	 * Returns the cooling parameter value gamma.
	 * When the timeout is set,
	 * the gamma value is re-calculated at each iteration
	 * so that the temperature will become 1.0 at the end.
	 * @return the gamma value
	 */
	public synchronized double getGamma() {
		return gamma;
	}

	/**
	 * Sets the cooling parameter value gamma.
	 * @param gamma the gamma to set
	 */
	public synchronized void setGamma(double gamma) {
		this.gamma = gamma;
	}

	/**
	 * Returns the temperature.
	 * @return the temperature
	 */
	public synchronized double getTemperature() {
		return temperature;
	}

	/**
	 * Sets the temperature.
	 * @param temp the temperature to set
	 */
	public synchronized void setTemperature(double temp) {
		temperature = temp;
	}

	public void startSearch() {
		super.startSearch();
		temperature = solution.getObjectiveIntValue() / 10.0;
	}

	public void nextSearch() {
		if (totalTimeout > 0) {
			long elapsedTime = Math.max(1, System.currentTimeMillis()
					- startTime);
			double iterationRate = (double) getIterations() / (double) elapsedTime;
			int expectedIteration = (int) (iterationRate * (totalTimeout - elapsedTime));
			if (expectedIteration > 0) {
				gamma = Math.exp(Math.log(1.0 / temperature)
						/ expectedIteration);
				gamma = Math.min(0.9999, gamma);
			}
		}
		temperature *= gamma;
		solution = getCandidate();
		if (solution == null)
			return;
		int value = solution.getObjectiveIntValue();
		Code code = solution.getCode();
		List<Operation> operations = code.operations();
		while (operations.size() > 0) {
			int i = (int) (operations.size() * Math.random());
			Operation op = operations.remove(i);
			code.setTo(network);
			op.applyTo(network);
			Solution sol = solver.findBest(iterationTimeout);
			if (sol == null)
				continue;
			double delta = sol.getObjectiveIntValue() - value;
			if (isOption(MAXIMIZE)) {
				delta = -delta;
			}
			if (delta < 0) {
				solution = sol;
				return;
			}
			double p = Math.exp(-delta / temperature);
			if (p < Math.random())
				continue;
			solution = sol;
			return;
		}
		solution = null;
	}
}
