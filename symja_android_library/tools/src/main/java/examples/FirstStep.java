/*
 * @(#)FirstStep.java
 */
package examples;

import jp.ac.kobe_u.cs.cream.*;

/**
 * First step example.
 * 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class FirstStep {
	public static void main(String[] args) {
		// Create a constraint network
		Network net = new Network();
		// Declare variables
		IntVariable x = new IntVariable(net);
		IntVariable y = new IntVariable(net);
		// x >= 0
		x.ge(0);
		// y >= 0
		y.ge(0);
		// x + y == 7
		x.add(y).equals(7);
		// 2x + 4y == 20
		x.multiply(2).add(y.multiply(4)).equals(20);
		// Solve the problem
		Solver solver = new DefaultSolver(net);
		Solution solution = solver.findFirst();
		int xv = solution.getIntValue(x);
		int yv = solution.getIntValue(y);
		System.out.println("x = " + xv + ", y = " + yv);

//		for (solver.start(); solver.waitNext(); solver.resume()) {
//			Solution solution = solver.getSolution();
//			int xv = solution.getIntValue(x);
//			int yv = solution.getIntValue(y);
//			System.out.println("x = " + xv + ", y = " + yv);
//		}
//		solver.stop();

//		solver.findAll(new FirstStepHandler(x, y));
	}
}

class FirstStepHandler implements SolutionHandler {
	IntVariable x;

	IntVariable y;

	public FirstStepHandler(IntVariable x, IntVariable y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public synchronized void solved(Solver solver, Solution solution) {
		if (solution != null) {
			int xv = solution.getIntValue(x);
			int yv = solution.getIntValue(y);
			System.out.println("x = " + xv + ", y = " + yv);
		}
	}
}
