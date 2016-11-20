/*
 * @(#)LocalSearch.java
 */
package jp.ac.kobe_u.cs.cream;

import java.util.List;

/**
 * A super class of local search solvers, and also an implementation of a random
 * walk solver. Local search is an iterative procedure. It first finds an
 * initial solution, and iteratively make a small change.
 * Only useful for the optimization problems containing {@link Serialized} constraints
 * in the current implementation.
 * The default exchangeRate value for {@link ParallelSolver}  
 * is 0.05.
 *  * 
 * @see Code
 * @see Serialized
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class LocalSearch extends Solver {
	/**
	 * Timeout in milli seconds to find an initial solution for the local search.
	 * The initial solution is searched by the {@link DefaultSolver#findFirst(long)} method.
	 * The default value is 0 (no timeout).
	 */
	public long initialTimeout = 0;
	
	/**
	 * Timeout in milli seconds to find the next solution in the iteration.
	 * The next solution is searched by the {@link DefaultSolver#findBest(long)} method.
	 * The default value is 5000 (5 seconds).
	 */
	public long iterationTimeout = 5000;

	/**
	 * Sub-solver used to find the initial solution and the next solution.
	 */
	public DefaultSolver solver = null;
	
	private int iterations = 0;

	private boolean newCandidate = false;

	private Solution candidate = null;

	private double exchangeRate = 0.05;

	/**
	 * Constructs a random-walk solver for the given network. This constructor
	 * is equivalent to <tt>LocalSearch(network, DEFAULT, null)</tt>.
	 * 
	 * @param network
	 *            the constraint network
	 */
	public LocalSearch(Network network) {
		this(network, DEFAULT, null);
	}

	/**
	 * Constructs a random-walk solver for the given network and option. This
	 * constructor is equivalent to <tt>LocalSearch(network, option, null)</tt>.
	 * 
	 * @param network
	 *            the constraint network
	 * @param option
	 *            the option for search strategy
	 */
	public LocalSearch(Network network, int option) {
		this(network, option, null);
	}

	/**
	 * Constructs a random-walk solver for the given network and name. This
	 * constructor is equivalent to <tt>LocalSearch(network, DEFAULT, name)</tt>.
	 * 
	 * @param network
	 *            the constraint network
	 * @param name
	 *            the name of the solver
	 */
	public LocalSearch(Network network, String name) {
		this(network, DEFAULT, name);
	}

	/**
	 * Constructs a random-walk solver for the given network, option, and name.
	 * 
	 * @param network
	 *            the constraint network
	 * @param option
	 *            the option for search strategy, or DEFAULT for default search
	 *            strategy
	 * @param name
	 *            the name of the solver, or <tt>null</tt> for a default name
	 */
	public LocalSearch(Network network, int option, String name) {
		super(network, option, name);
		solver = new DefaultSolver(network, option);
	}

	/**
	 * Returns the iterations count.
	 * @return the iterations count
	 */
	public int getIterations() {
		return iterations;
	}

	/**
	 * Returns the candidate solution for the next iteration.
	 * @return the candidate solution or null 
	 */
	public synchronized Solution getCandidate() {
		if (newCandidate) {
			newCandidate = false;
			return candidate;
		} else if (solution != null) {
			return solution;
		}
		return bestSolution;
	}

	/**
	 * Sets a candidate solution for the next iteration.
	 * 
	 * @param candidate
	 *            the candidate
	 */
	public synchronized void setCandidate(Solution candidate) {
		if (candidate != null) {
			this.candidate = candidate;
			newCandidate = true;
		}
	}

	/**
	 * Returns the solution exchange rate for {@link ParallelSolver}.
	 * @return the exchange rate
	 */
	public double getExchangeRate() {
		return exchangeRate;
	}

	/**
	 * Sets the solution exchange rate used for {@link ParallelSolver}.
	 * @param exchangeRate the exchange rate to set
	 */
	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public synchronized void stop() {
		if (solver != null)
			solver.stop();
		super.stop();
	}

	/**
	 * Finds the initial solution.
	 */
	public void startSearch() {
		// solver = new DefaultSolver(network, option); 
		solution = solver.findFirst(initialTimeout);
	}

	/**
	 * Finds the next solution.
	 */
	public void nextSearch() {
		solution = getCandidate();
		if (solution == null)
			return;
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
			solution = sol;
			return;
		}
		solution = null;
	}

	/**
	 * End of the iteration.
	 */
	public void endSearch() {
		solver = null;
	}

	public void run() {
		iterations = 0;
		clearBest();
		startSearch();
		while (!isAborted() && solution != null) {
			iterations++;
			success();
			if (isAborted())
				break;
			nextSearch();
			if (isAborted())
				break;
			if (solution == null) {
				solution = bestSolution;
			}
		}
		fail();
		endSearch();
	}

}
