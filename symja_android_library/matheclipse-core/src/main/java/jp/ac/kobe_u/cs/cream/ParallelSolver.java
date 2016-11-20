/*
 * @(#)ParallelSolver.java
 */
package jp.ac.kobe_u.cs.cream;

/**
 * Parallel solver.
 * Only useful for the optimization problems containing {@link Serialized} constraints
 * in the current implementation.
 * 
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class ParallelSolver extends Solver implements SolutionHandler {
	private Solver[] solvers;

	public ParallelSolver(Solver[] solvers) {
		this(solvers, null);
	}

	public ParallelSolver(Solver[] solvers, String name) {
		super(null, NONE, name);
		this.solvers = solvers;
		network = solvers[0].network;
		option = solvers[0].option;
	}

	/**
	 * Returns the sub-solvers.
	 * @return the sub-solvers
	 */
	public synchronized Solver[] getSolvers() {
		return solvers;
	}

	public void setMonitor(Monitor monitor) {
		for (Solver solver : solvers) {
			solver.setMonitor(monitor);
		}
	}

	public synchronized void stop() {
		for (Solver solver : solvers) {
			solver.stop();
		}
		super.stop();
	}

	public synchronized void solved(Solver solver, Solution solution) {
		if (isAborted() || solution == null) {
			return;
		}
		int oldBestValue = bestValue;
		this.solution = solution;
		success();
		if (!(solver instanceof LocalSearch))
			return;
		if (network.getObjective() == null)
			return;
		int value = solution.getObjectiveIntValue();
		if (!isBetter(value, oldBestValue)) {
			double rate = 0.0;
			if (solver instanceof LocalSearch) {
				rate = ((LocalSearch) solver).getExchangeRate();
			}
			if (Math.random() < rate) {
				// System.out.println(header + "Get " + best);
				((LocalSearch) solver).setCandidate(bestSolution);
			}
		}
	}

	public synchronized void allStart() {
		for (Solver solver : solvers) {
			synchronized (solver) {
				if (solver != null) {
					solver.start(this, totalTimeout);
				}
			}
		}
	}

	public synchronized void allJoin() {
		for (Solver solver : solvers) {
			synchronized (solver) {
				if (solver != null) {
					solver.join();
				}
			}
		}
	}

	public void run() {
		clearBest();
		allStart();
		allJoin();
		fail();
	}
}
