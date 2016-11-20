/*
 * @(#)Magic.java
 */
package examples;

import jp.ac.kobe_u.cs.cream.*;

/**
 * Magic squares.
 * There is only one magic square of 3*3.
 * There are 880 magic squares of 4*4.
 * 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Magic {
	public static void magic(int n) {
		Network net = new Network();
		IntVariable[][] square = new IntVariable[n][n];

		// All squares have different numbers 1 .. n*n
		IntVariable[] v = new IntVariable[n * n];
		int k = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				square[i][j] = new IntVariable(net, 1, n * n);
				v[k++] = square[i][j];
			}
		}
		new NotEquals(net, v);

		// Sum of each row is n*(n*n+1)/2
		IntVariable s;
		int sum = n * (n * n + 1) / 2;
		for (int i = 0; i < n; i++) {
			s = square[i][0];
			for (int j = 1; j < n; j++)
				s = s.add(square[i][j]);
			s.equals(sum);
		}

		// Sum of each column is n*(n*n+1)/2
		for (int j = 0; j < n; j++) {
			s = square[0][j];
			for (int i = 1; i < n; i++)
				s = s.add(square[i][j]);
			s.equals(sum);
		}

		// Sum of down-diagonal is n*(n*n+1)/2
		s = square[0][0];
		for (int i = 1; i < n; i++)
			s = s.add(square[i][i]);
		s.equals(sum);

		// Sum of up-diagonal is n*(n*n+1)/2
		s = square[0][n - 1];
		for (int i = 1; i < n; i++)
			s = s.add(square[i][n - i - 1]);
		s.equals(sum);

		// Left-upper corner is minimum
		square[0][0].lt(square[0][n - 1]);
		square[0][0].lt(square[n - 1][0]);
		square[0][0].lt(square[n - 1][n - 1]);

		// Upper-right is less than lower-left
		square[0][n - 1].lt(square[n - 1][0]);

		System.out.println("Start");
		boolean output = true;
		Solver solver = new DefaultSolver(net);
		for (solver.start(); solver.waitNext(); solver.resume()) {
			if (output) {
				Solution solution = solver.getSolution();
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						System.out.print(solution.getIntValue(square[i][j])
								+ " ");
					}
					System.out.println();
				}
				System.out.println();
			}
		}
		solver.stop();
		long count = solver.getCount();
		long time = solver.getElapsedTime() / 1000;
		System.out.println(count + " solutions found in " + time + " seconds");
	}

	public static void main(String[] args) {
		int n = 4;
		if (args.length >= 1) {
			n = Integer.parseInt(args[0]);
		}
		magic(n);
	}

}
