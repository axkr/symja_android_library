package jp.ac.kobe_u.cs.cream;

/**
 * A branch-and-bound solver with preset values.
 * An array of initial values can be passed as an argument to the solver.
 * <ul>
 * <li>The solver tries to preset the initial values to the variables as
 * mush as possible until it violates some constraint.</li>
 * <li>Remaining variable values are determined by a backtrack search.</li>
 * <li>Preset values are also undone when the problem is unsatisfiable
 * during the further search.</li>
 * <li>The changes are not ensured to the minimum changes.</li>
 * </ul>
 * @see Solver
 * @see DefaultSolver
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class NeighborhoodSearchSolver extends DefaultSolver {
	private int[] values;

	public NeighborhoodSearchSolver(Network network, int[] values) {
		this(network, values, DEFAULT, null);
	}

	public NeighborhoodSearchSolver(Network network, int[] values, int options) {
		this(network, values, options, null);
	}

	public NeighborhoodSearchSolver(Network network, int[] values, String name) {
		this(network, values, DEFAULT, name);
	}

	public NeighborhoodSearchSolver(Network network, int[] values, int options, String name) {
		super(network, options, name);
		this.values = values;
	}

	public void run() {
		clearBest();
		Trail trail = getTrail();
		if (values != null) {
			// Preset values[i] to the i-th variable as much as possible
			for (int i = 0; i < values.length; i++) {
				Variable v = network.getVariable(i);
				int t0 = trail.size();
				// Preset values[i] to the i-th variable
				v.updateDomain(new IntDomain(values[i]), trail);
				if (! satisfy()) {
					// Preset value violates some constraint
					trail.undo(t0);
					break;
				}
			}
		}
		// Invoke DefaultSolver
		solve(0);
		trail.undo(0);
		fail();
	}
}
