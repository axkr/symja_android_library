/*
 * @(#)Akari.java
 */
package examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import jp.ac.kobe_u.cs.cream.*;

/**
 * Akari puzzle by Nikoli
 * http://www.nikoli.co.jp/en/puzzles/akari/
 * 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Akari {
	static String[][] puzzle = null;

	static int m;

	static int n;

	static Network net = null;

	static IntVariable[][] v = null;

	static IntVariable[][] vsum = null;

	static IntVariable[][] hsum = null;

	static boolean parse(BufferedReader in) {
		puzzle = null;
		try {
			String line;
			do {
				line = in.readLine();
				if (line == null)
					return false;
			} while (!line.equals("begin"));
			line = in.readLine();
			if (line == null)
				return false;
			String[] tokens = line.split("\\s+");
			if (tokens.length != 3 || !tokens[0].equals("size"))
				return false;
			m = Integer.parseInt(tokens[1]);
			n = Integer.parseInt(tokens[2]);
			if (m <= 0 || n <= 0)
				return false;
			puzzle = new String[m][];
			for (int i = 0; i < m; i++) {
				line = in.readLine();
				puzzle[i] = line.split("\\s+");
				if (puzzle.length != n)
					return false;
			}
			line = in.readLine();
			if (!line.equals("end"))
				return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	static IntVariable vsum(int i, int j) {
		while (i - 1 >= 0 && puzzle[i - 1][j].equals("-"))
			i--;
		if (vsum[i][j] == null) {
			vsum[i][j] = v[i][j];
			for (int k = i + 1; k < m && v[k][j] != null; k++)
				vsum[i][j] = vsum[i][j].add(v[k][j]);
			vsum[i][j].le(1);
		}
		return vsum[i][j];
	}

	static IntVariable hsum(int i, int j) {
		while (j - 1 >= 0 && puzzle[i][j - 1].equals("-"))
			j--;
		if (hsum[i][j] == null) {
			hsum[i][j] = v[i][j];
			for (int k = j + 1; k < n && v[i][k] != null; k++)
				hsum[i][j] = hsum[i][j].add(v[i][k]);
			hsum[i][j].le(1);
		}
		return hsum[i][j];
	}

	static IntVariable adjacentSum(int i, int j) {
		IntVariable s = new IntVariable(net, 0);
		if (i - 1 >= 0 && v[i - 1][j] != null)
			s = s.add(v[i - 1][j]);
		if (i + 1 < m && v[i + 1][j] != null)
			s = s.add(v[i + 1][j]);
		if (j - 1 >= 0 && v[i][j - 1] != null)
			s = s.add(v[i][j - 1]);
		if (j + 1 < n && v[i][j + 1] != null)
			s = s.add(v[i][j + 1]);
		return s;
	}

	static void setProblem() {
		net = new Network();

		// Set constraint variables
		v = new IntVariable[m][n];
		vsum = new IntVariable[m][n];
		hsum = new IntVariable[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				v[i][j] = null;
				if (puzzle[i][j].equals("-")) {
					v[i][j] = new IntVariable(net, 0, 1);
				}
				vsum[i][j] = null;
				hsum[i][j] = null;
			}
		}
		// Set constraints
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (puzzle[i][j].equals("-")) {
					IntVariable vsum = vsum(i, j);
					IntVariable hsum = hsum(i, j);
					vsum.add(hsum).subtract(v[i][j]).ge(1);
				} else if (puzzle[i][j].matches("^\\d$")) {
					int sum = Integer.parseInt(puzzle[i][j]);
					IntVariable asum = adjacentSum(i, j);
					asum.equals(sum);
				}
			}
		}
	}

	static void printSolution(Solution solution) {
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				String s = null;
				if (v[i][j] == null) {
					s = puzzle[i][j];
				} else {
					int x = solution.getIntValue(v[i][j]);
					s = (x == 0) ? "-" : "*";
				}
				System.out.print(s + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	static void solve() {
		setProblem();
		Solver solver = new DefaultSolver(net);
		for (solver.start(); solver.waitNext(); solver.resume()) {
			Solution solution = solver.getSolution();
			printSolution(solution);
		}
		solver.stop();
	}

	public static void main(String[] args) {
		String data =
			"begin\n" +
			"size 8 8\n" +
			"- - - - - 1 - -\n" +
			"- 3 X - - - - -\n" +
			"- - - - - - 0 -\n" +
			"X - - - X - - -\n" +
			"- - - 4 - - - 0\n" +
			"- 2 - - - - - -\n" +
			"- - - - - 1 X -\n" +
			"- - X - - - - -\n" +
			"end\n";
		BufferedReader in;
		if (data == null) {
			in = new BufferedReader(new InputStreamReader(System.in));
		} else {
			in = new BufferedReader(new StringReader(data));
		}
		while (parse(in)) {
			solve();
		}
	}

}
