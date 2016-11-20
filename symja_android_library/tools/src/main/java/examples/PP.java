/*
 * @(#)PP.java
 */
package examples;

import jp.ac.kobe_u.cs.cream.*;

/**
 * Production planning.
 * 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class PP {
	static void pp() {
		Network net = new Network();
		// number of materials
		int m = 3;
		// limit of each material
		int[] limit = { 1650, 1400, 1800 };
		// number of products
		int n = 2;
		// profit of each product
		int[] p = { 5, 4 };
		// amount of materials required to make each product 
		int[][] a = { { 15, 10, 9 }, { 11, 14, 20 } };

		// initialize variables for products
		IntVariable[] x = new IntVariable[n];
		for (int j = 0; j < n; j++) {
			x[j] = new IntVariable(net);
			x[j].ge(0);
		}
		// generate constraits of limiting materials
		for (int i = 0; i < m; i++) {
			IntVariable sum = new IntVariable(net, 0);
			for (int j = 0; j < n; j++) {
				sum = sum.add(x[j].multiply(a[j][i]));
			}
			sum.le(limit[i]);
		}
		// total profit
		IntVariable profit = new IntVariable(net, 0);
		for (int j = 0; j < n; j++) {
			profit = profit.add(x[j].multiply(p[j]));
		}
		// maximize the total profit
		net.setObjective(profit);
		// iteratively find a better solution until the optimal solution is found 
		Solver solver = new DefaultSolver(net, Solver.MAXIMIZE | Solver.BETTER);
		for (solver.start(); solver.waitNext(); solver.resume()) {
			Solution solution = solver.getSolution();
			System.out.println("Profit = " + solution.getIntValue(profit));
			for (int j = 0; j < n; j++) {
				System.out.println("x[" + j + "]=" + solution.getIntValue(x[j]));
			}
			System.out.println();
		}
		solver.stop();
	}

	public static void main(String[] args) {
		pp();
	}
}
