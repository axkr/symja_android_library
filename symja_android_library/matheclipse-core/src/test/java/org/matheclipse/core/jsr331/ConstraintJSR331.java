package org.matheclipse.core.jsr331;

//import javax.constraints.*;

public class ConstraintJSR331 {
//	Problem p = ProblemFactory.newProblem("Test");
//
//	public void define() {
//		// PROBLEM DEFINITION
//		// ======= Define variables
//
//		Var x = p.variable("X", 1, 10);
//		Var y = p.variable("Y", 1, 10);
//		Var z = p.variable("Z", 1, 10);
//		Var r = p.variable("R", 1, 10);
//		Var[] vars = { x, y, z, r };
//		// ======= Define and post constraints
//		try {
//			p.post(x, "<", y);
//			// X < Y
//			p.post(z, ">", 4);
//			// Z > 4
//			p.post(x.plus(y), "=", z);
//			// X + Y = Z
//			p.postAllDifferent(vars);
//			int[] coef1 = { 3, 4, -5, 2 };
//			p.post(coef1, vars, ">", 0);
//			// 3x + 4y -5z + 2r > 0
//			p.post(vars, ">=", 15);
//			// x + y + z + r >= 15
//			int[] coef2 = { 2, -4, 5, -1 };
//			p.post(coef2, vars, ">", x.multiply(y));// 2x-4y+5z-r > x*y
//		} catch (Exception e) {
//			p.log("Error posting constraints: " + e);
//			System.exit(-1);
//		}
//	}
//
//	public void solve() { // PROBLEM RESOLUTION
//		p.log("=== Find Solution:");
//		Solver solver = p.getSolver();
//		Solution solution = solver.findSolution();
//		if (solution != null)
//			solution.log();
//		else
//			p.log("No Solution");
//
//		solver.logStats();
//	}
//
//	public static void main(String[] args) {
//		ConstraintJSR331 t = new ConstraintJSR331();
//		t.define();
//		t.solve();
//	}
}