/*
 * @(#)JavaCreamSolver.java
 */
package examples;

import jp.ac.kobe_u.cs.cream.*;

/**
 * Cryptarithmetic puzzle.
 * JAVA + CREAM = SOLVER
 * 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class JavaCreamSolver {
	public static void main(String[] args) {
		Network net = new Network();
		IntVariable J = new IntVariable(net, 0, 9);
		IntVariable A = new IntVariable(net, 0, 9);
		IntVariable V = new IntVariable(net, 0, 9);
		IntVariable C = new IntVariable(net, 0, 9);
		IntVariable R = new IntVariable(net, 0, 9);
		IntVariable E = new IntVariable(net, 0, 9);
		IntVariable M = new IntVariable(net, 0, 9);
		IntVariable S = new IntVariable(net, 0, 9);
		IntVariable O = new IntVariable(net, 0, 9);
		IntVariable L = new IntVariable(net, 0, 9);
		new NotEquals(net, new IntVariable[] { J, A, V, C, R, E, M, S, O, L });
		J.notEquals(0);
		C.notEquals(0);
		S.notEquals(0);
		IntVariable JAVA = J.multiply(1000).add(A.multiply(100)).add(
				V.multiply(10)).add(A);
		IntVariable CREAM = C.multiply(10000).add(R.multiply(1000)).add(
				E.multiply(100)).add(A.multiply(10)).add(M);
		IntVariable SOLVER = S.multiply(100000).add(O.multiply(10000)).add(
				L.multiply(1000)).add(V.multiply(100)).add(E.multiply(10)).add(
				R);
		JAVA.add(CREAM).equals(SOLVER);
		Solver solver = new DefaultSolver(net);
		for (solver.start(); solver.waitNext(); solver.resume()) {
			Solution solution = solver.getSolution();
			System.out.println(solution.getIntValue(JAVA) + " + "
					+ solution.getIntValue(CREAM) + " = "
					+ solution.getIntValue(SOLVER));
		}
		solver.stop();
	}
}
