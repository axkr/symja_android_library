/*
 * @(#)Golomb.java
 * See http://4c.ucc.ie/~tw/csplib/prob/prob006/index.html
 * in http://csplib.org/
 */
package examples;

import jp.ac.kobe_u.cs.cream.*;

/**
 * Golomb ruler.
 * See http://4c.ucc.ie/~tw/csplib/prob/prob006/index.html
 * in http://csplib.org/
 * 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Golomb {
	static void golomb(int m) {
		int n = (1 << (m - 1)) - 1;
		Network net = new Network();
		IntVariable[] a = new IntVariable[m];
		a[0] = new IntVariable(net, 0);
		for (int i = 1; i < m; i++) {
			a[i] = new IntVariable(net, 1, n);
			a[i - 1].lt(a[i]);
		}
		IntVariable[] d = new IntVariable[m * (m - 1) / 2];
		int k = 0;
		for (int i = 0; i < m; i++) {
			for (int j = i + 1; j < m; j++) {
				d[k++] = a[j].subtract(a[i]);
			}
		}
		new NotEquals(net, d);
		net.setObjective(a[m - 1]);

		Solver solver = new DefaultSolver(net, Solver.MINIMIZE);
		Solution solution = solver.findBest();
		System.out.print("0");
		for (int i = 1; i < m; i++) {
			System.out.print("," + solution.getIntValue(a[i]));
		}
		System.out.println();
		System.out.println("Time = " + solver.getElapsedTime());
	}

	public static void main(String[] args) {
		int m = 8;
		if (args.length == 1) {
			m = Integer.parseInt(args[0]);
		}
		golomb(m);
	}
}
