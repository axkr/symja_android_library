/*
 * @(#)FT06.java
 */
package examples;

import jp.ac.kobe_u.cs.cream.*;

/**
 * Job-shop scheduling benchmark problem of FT06.
 * The optimum makespan is 55.
 * 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class FT06 {
	private static void ft06(Network net) {
		int n = 37;
		IntVariable[] v = new IntVariable[n];
		for (int i = 0; i < v.length; i++) {
			v[i] = new IntVariable(net, 0, IntDomain.MAX_VALUE);
		}
		IntVariable[][] jobs = {
				{ v[1], v[2], v[3], v[4], v[5], v[6], v[0] },
				{ v[7], v[8], v[9], v[10], v[11], v[12], v[0] },
				{ v[13], v[14], v[15], v[16], v[17], v[18], v[0] },
				{ v[19], v[20], v[21], v[22], v[23], v[24], v[0] },
				{ v[25], v[26], v[27], v[28], v[29], v[30], v[0] },
				{ v[31], v[32], v[33], v[34], v[35], v[36], v[0] } };
		int[][] jobs_pt = {
				{ 1, 3, 6, 7, 3, 6 },
				{ 8, 5, 10, 10, 10, 4 },
				{ 5, 4, 8, 9, 1, 7 },
				{ 5, 5, 5, 3, 8, 9 },
				{ 9, 3, 5, 4, 3, 1 },
				{ 3, 3, 9, 10, 4, 1 } };
		IntVariable[][] machines = {
				{ v[2], v[11], v[16], v[20], v[29], v[34] },
				{ v[3], v[7], v[17], v[19], v[26], v[31] },
				{ v[1], v[8], v[13], v[21], v[25], v[36] },
				{ v[4], v[12], v[14], v[22], v[30], v[32] },
				{ v[6], v[9], v[18], v[23], v[27], v[35] },
				{ v[5], v[10], v[15], v[24], v[28], v[33] } };
		int[][] machines_pt = {
				{ 3, 10, 9, 5, 3, 10 },
				{ 6, 8, 1, 5, 3, 3 },
				{ 1, 5, 5, 5, 9, 1 },
				{ 7, 4, 4, 3, 1, 3 },
				{ 6, 10, 7, 8, 5, 4 },
				{ 3, 10, 8, 9, 4, 9 } };
		for (int j = 0; j < jobs.length; j++) {
			new Sequential(net, jobs[j], jobs_pt[j]);
		}
		for (int m = 0; m < machines.length; m++) {
			new Serialized(net, machines[m], machines_pt[m]);
		}
		net.setObjective(v[0]);
	}
	
	private static void solve(Network net, String solverName, long timeout) {
		boolean showMonitor = true;
		boolean showSolution = false;
		int opt = Solver.MINIMIZE;
		Solver solver = null;
		if (solverName.equals("bb")) {
			solver = new DefaultSolver(net, opt, "bb");
		} else if (solverName.equals("random")) {
			solver = new LocalSearch(net, opt, "rs");
		} else if (solverName.equals("sa")) {
			solver = new SASearch(net, opt, "sa");
		} else if (solverName.equals("taboo")) {
			solver = new TabooSearch(net, opt, "taboo");
		} else if (solverName.equals("ibb")) {
			solver = new IBBSearch(net, opt, "ibb");
		} else {
			Solver sa = new SASearch((Network) net.clone(), opt, "sa");
			Solver taboo = new TabooSearch((Network) net.clone(), opt, "taboo");
			solver = new ParallelSolver(new Solver[] { sa, taboo });
		}
		if (showMonitor) {
			Monitor monitor = new Monitor();
			monitor.setX(0, (int) (timeout / 1000));
			solver.setMonitor(monitor);
		}

		System.out.println("Start " + solver + ", timeout = " + timeout
				+ " msecs");

		Solution bestSolution;
		for (solver.start(timeout); solver.waitNext(); solver.resume()) {
			if (showSolution) {
				Solution solution = solver.getSolution();
				int value = solution.getObjectiveIntValue();
				System.out.println(value);
			}
		}
		solver.stop();
		bestSolution = solver.getBestSolution();
		System.out.println("Best = " + bestSolution.getObjectiveIntValue());
	}
	

	public static void main(String[] args) {
		Network net = new Network();
		ft06(net);
		String solverName = "para";
		if (args.length >= 1) {
			solverName = args[0];
		}
		long timeout = 30*1000;
		solve(net, solverName, timeout);
	}
}
