/*
 * @(#)Queens.java
 */
package examples;

import jp.ac.kobe_u.cs.cream.*;

/**
 * N-Queens problem
 * 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Queens {
	static void queens(int n) {
		Network net = new Network();
		IntVariable[] q = new IntVariable[n];
		IntVariable[] u = new IntVariable[n];
		IntVariable[] d = new IntVariable[n];
		for (int i = 0; i < n; ++i) {
			q[i] = new IntVariable(net, 1, n);
			u[i] = q[i].add(i);
			d[i] = q[i].subtract(i);
		}
		new NotEquals(net, q);
		new NotEquals(net, u);
		new NotEquals(net, d);
		Solver solver = new DefaultSolver(net);
		for (solver.start(); solver.waitNext(); solver.resume()) {
			Solution solution = solver.getSolution();
			for (int i = 0; i < n; i++) {
				int j = solution.getIntValue(q[i]);
				System.out.print(j + " ");
			}
			System.out.println();
		}
		solver.stop();
		long count = solver.getCount();
		long time = solver.getElapsedTime() / 1000;
		System.out.println(count + " solutions found in " + time + " seconds");
	}

	public static void main(String[] args) {
		int n = 8;
		if (args.length == 1) {
			n = Integer.parseInt(args[0]);
		}
		queens(n);
	}
}
