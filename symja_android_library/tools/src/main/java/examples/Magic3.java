/*
 * @(#)Magic3.java
 */
package examples;

import jp.ac.kobe_u.cs.cream.*;

/**
 * Magic squares of 3*3.
 * 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Magic3 {
	public static void main(String[] args) {
		Network net = new Network();
		int n = 3;
		int sum = n * (n * n + 1) / 2;
		IntVariable[][] v = new IntVariable[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				v[i][j] = new IntVariable(net, 1, n * n);
		IntVariable[] u = {
				v[0][0], v[0][1], v[0][2],
				v[1][0], v[1][1], v[1][2],
				v[2][0], v[2][1], v[2][2] };
		new NotEquals(net, u);
		for (int i = 0; i < n; i++)
			v[i][0].add(v[i][1]).add(v[i][2]).equals(sum);
		for (int j = 0; j < n; j++)
			v[0][j].add(v[1][j]).add(v[2][j]).equals(sum);
		v[0][0].add(v[1][1]).add(v[2][2]).equals(sum);
		v[0][2].add(v[1][1]).add(v[2][0]).equals(sum);
		Solver solver = new DefaultSolver(net);
		for (solver.start(); solver.waitNext(); solver.resume()) {
			Solution solution = solver.getSolution();
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++)
					System.out.print(solution.getIntValue(v[i][j]) + " ");
				System.out.println();
			}
			System.out.println();
		}
		solver.stop();
	}
}
