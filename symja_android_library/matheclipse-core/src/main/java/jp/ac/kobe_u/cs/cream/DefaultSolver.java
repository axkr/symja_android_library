/*
 * @(#)DefaultSolver.java
 */
package jp.ac.kobe_u.cs.cream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A branch-and-bound solver.
 * 
 * @see Solver
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class DefaultSolver extends Solver {

	/**
	 * An option for choosing the value of the selected variable
	 */
	public static final int STEP = 0;

	/**
	 * An option for choosing the value of the selected variable
	 */
	public static final int ENUM = 1;

	/**
	 * An option for choosing the value of the selected variable
	 */
	public static final int BISECT = 2;

	/**
	 * An option for choosing a random value of the selected variable
	 */
	public static final int RANDOM = 3;

	private int choice = STEP;

	private Trail trail = new Trail();

	/**
	 * Constructs a branch-and-bound solver for the given network. This
	 * constructor is equivalent to
	 * <tt>DefaultSolver(network, DEFAULT, null)</tt>.
	 * 
	 * @param network
	 *            the constraint network
	 */
	public DefaultSolver(Network network) {
		this(network, DEFAULT, null);
	}

	/**
	 * Constructs a branch-and-bound solver for the given network and options.
	 * This constructor is equivalent to
	 * <tt>DefaultSolver(network, options, null)</tt>.
	 * 
	 * @param network
	 *            the constraint network
	 * @param options
	 *            the options for search choice
	 */
	public DefaultSolver(Network network, int options) {
		this(network, options, null);
	}

	/**
	 * Constructs a branch-and-bound solver for the given network and name. This
	 * constructor is equivalent to
	 * <tt>DefaultSolver(network, DEFAULT, name)</tt>.
	 * 
	 * @param network
	 *            the constraint network
	 * @param name
	 *            the name of the solver
	 */
	public DefaultSolver(Network network, String name) {
		this(network, DEFAULT, name);
	}

	/**
	 * Constructs a branch-and-bound solver for the given network, options, and
	 * name.
	 * 
	 * @param network
	 *            the constraint network
	 * @param options
	 *            the options for search choice, or DEFAULT for default search
	 *            choice
	 * @param name
	 *            the name of the solver, or <tt>null</tt> for a default name
	 */
	public DefaultSolver(Network network, int options, String name) {
		super(network, options, name);
	}

	/**
	 * Returns the option for choosing variable value.
	 * @return the choice
	 */
	public int getChoice() {
		return choice;
	}

	/**
	 * Sets the option for choosing variable value.
	 * @param choice the choice to set
	 */
	public void setChoice(int choice) {
		this.choice = choice;
	}

	/**
	 * Returns the trail stack.
	 * @return the trail
	 */
	public Trail getTrail() {
		return trail;
	}

	private List<Constraint> modifiedConstraints() {
		List<Constraint> list = new ArrayList<Constraint>();
		for (Constraint c : network.getConstraints()) {
			if (c.isModified()) {
				list.add(c);
			}
		}
		for (Variable v : network.getVariables()) {
			v.setModified(false);
		}
		return list;
	}

	/**
	 * Performs the consistency algorithm until
	 * no variables are changed.
	 * @return true when the consistency algorithm succeeds
	 */
	public boolean satisfy() {
		boolean changed = true;
		while (! isAborted() && changed) {
			for (Constraint c : modifiedConstraints()) {
				if (! c.satisfy(trail)) {
					return false;
				}
			}
			changed = false;
			for (Variable v : network.getVariables()) {
				changed |= v.isModified();
				if (changed)
					break;
			}
		}
		return true;
	}

	protected Variable infVariable() {
		Variable v_min = null;
		int inf_min = Integer.MAX_VALUE;
		int inf;
		for (Variable v : network.getVariables()) {
			Domain d = v.getDomain();
			if (!(d instanceof IntDomain))
				continue;
			if (d.size() <= 1)
				continue;
			inf = ((IntDomain) d).min();
			if (inf < inf_min) {
				v_min = v;
				inf_min = inf;
			}
		}
		return v_min;
	}

	protected Variable supVariable() {
		Variable v_max = null;
		int sup_max = Integer.MIN_VALUE;
		int sup;
		for (Variable v : network.getVariables()) {
			Domain d = v.getDomain();
			if (!(d instanceof IntDomain))
				continue;
			if (d.size() <= 1)
				continue;
			sup = ((IntDomain) d).max();
			if (sup > sup_max) {
				v_max = v;
				sup_max = sup;
			}
		}
		return v_max;
	}

	protected Variable minimumSizeVariable() {
		Variable v_min = null;
		int min_size = Integer.MAX_VALUE;
		for (Variable v : network.getVariables()) {
			int size = v.getDomain().size();
			if (1 < size && size <= min_size) {
				v_min = v;
				min_size = size;
			}
		}
		return v_min;
	}

	/**
	 * Selects a decision variable.
	 * @return the decision variable
	 */
	public Variable selectVariable() {
		Variable v = null;
		if (isOption(MINIMIZE)) {
			v = infVariable();
			// v = minimumSizeVariable();
		} else if (isOption(MAXIMIZE)) {
			v = supVariable();
			// v = minimumSizeVariable();
		}
		if (v == null) {
			v = minimumSizeVariable();
		}
		return v;
	}

	protected void solve(int level) {
		Variable objective = network.getObjective();
		while (! isAborted()) {
			if (isOption(MINIMIZE)) {
				if (bestValue < IntDomain.MAX_VALUE) {
					IntDomain d = (IntDomain) objective.getDomain();
					d = d.delete(bestValue, IntDomain.MAX_VALUE);
					if (d.isEmpty())
						break;
					objective.updateDomain(d, trail);
				}
			} else if (isOption(MAXIMIZE)) {
				if (bestValue > IntDomain.MIN_VALUE) {
					IntDomain d = (IntDomain) objective.getDomain();
					d = d.delete(IntDomain.MIN_VALUE, bestValue);
					if (d.isEmpty())
						break;
					objective.updateDomain(d, trail);
				}
			}
			boolean sat = satisfy();
			if (isAborted() || !sat)
				break;
			Variable v0 = selectVariable();
			if (v0 == null) {
				solution = new Solution(network);
				success();
				break;
			}
			if (v0.getDomain() instanceof IntDomain) {
				IntDomain d = (IntDomain) v0.getDomain();
				int value;
				switch (choice) {
				case STEP:
					value = d.min();
					if (! isAborted()) {
						int t0 = trail.size();
						v0.updateDomain(new IntDomain(value), trail);
						solve(level + 1);
						trail.undo(t0);
					}
					if (! isAborted()) {
						v0.updateDomain(d.delete(value), trail);
						continue;
					}
					break;
				case ENUM:
					Iterator<Domain> iter = v0.getDomain().elements();
					while (! isAborted() && iter.hasNext()) {
						int t0 = trail.size();
						v0.updateDomain(iter.next(), trail);
						solve(level + 1);
						trail.undo(t0);
					}
					break;
				case BISECT:
					int mid;
					if (d.min() + 1 == d.max())
						mid = d.min();
					else
						mid = (d.min() + d.max()) / 2;
					if (! isAborted()) {
						int t0 = trail.size();
						v0.updateDomain(d.capInterval(d.min(), mid), trail);
						solve(level + 1);
						trail.undo(t0);
					}
					if (! isAborted()) {
						int t0 = trail.size();
						v0.updateDomain(d.capInterval(mid + 1, d.max()), trail);
						solve(level + 1);
						trail.undo(t0);
					}
					break;
				case RANDOM:
					value = d.randomElement();
					if (! isAborted()) {
						int t0 = trail.size();
						v0.updateDomain(new IntDomain(value), trail);
						solve(level + 1);
						trail.undo(t0);
					}
					if (! isAborted()) {
						v0.updateDomain(d.delete(value), trail);
						continue;
					}
					break;
				}
			} else {
				Iterator<Domain> iter = v0.getDomain().elements();
				while (! isAborted() && iter.hasNext()) {
					int t0 = trail.size();
					v0.updateDomain(iter.next(), trail);
					solve(level + 1);
					trail.undo(t0);
				}
			}
			break;
		}
	}

	// protected void solve(int level) {
	// if (isAborted())
	// return;
	// Variable objective = network.getObjective();
	// if (isOption(MINIMIZE)) {
	// if (bestValue < IntDomain.MAX_VALUE) {
	// IntDomain d = (IntDomain)objective.getDomain();
	// d = (IntDomain)d.delete(bestValue, IntDomain.MAX_VALUE);
	// if (d.isEmpty())
	// return;
	// objective.updateDomain(d, trail);
	// }
	// } else if (isOption(MAXIMIZE)) {
	// if (bestValue > IntDomain.MIN_VALUE) {
	// IntDomain d = (IntDomain)objective.getDomain();
	// d = (IntDomain)d.delete(IntDomain.MIN_VALUE, bestValue);
	// if (d.isEmpty())
	// return;
	// objective.updateDomain(d, trail);
	// }
	// }
	// boolean sat = satisfy();
	// if (isAborted() || ! sat)
	// return;
	// Variable v0 = selectVariable();
	// if (v0 == null) {
	// solution = new Solution(network);
	// success();
	// } else if (v0.getDomain() instanceof IntDomain) {
	// IntDomain d = (IntDomain)v0.getDomain();
	// int t0 = trail.size();
	// int choice = d.min();
	// if (! isAborted()) {
	// v0.updateDomain(new IntDomain(choice), trail);
	// solve(level + 1);
	// trail.undo(t0);
	// }
	// if (! isAborted()) {
	// v0.updateDomain(d.delete(choice), trail);
	// solve(level + 1);
	// trail.undo(t0);
	// }
	// } else {
	// Iterator iter = v0.getDomain().elements();
	// while (! isAborted() && iter.hasNext()) {
	// int t0 = trail.size();
	// v0.updateDomain((Domain)iter.next(), trail);
	// solve(level + 1);
	// trail.undo(t0);
	// }
	// }
	// }

	public void run() {
		clearBest();
		trail = new Trail();
		solve(0);
		trail.undo(0);
		fail();
	}

}
