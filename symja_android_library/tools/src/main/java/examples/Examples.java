/*
 * @(#)Examples.java
 */
package examples;

import java.util.List;

import jp.ac.kobe_u.cs.cream.*;

/**
 * Various example programs.
 * 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Examples {
	
	static void runExample(Network net, int opt) {
		System.out.println("# Problem");
		System.out.println(net);
		System.out.println("# Solutions");
		Solver solver = new DefaultSolver(net, opt);
		for (solver.start(); solver.waitNext(); solver.resume()) {
			Solution solution = solver.getSolution();
			System.out.println(solution);
		}
		solver.stop();
		long count = solver.getCount();
		long time = solver.getElapsedTime();
		System.out.println("Found " + count + " solutions in " + time + " milli seconds");
		System.out.println();
	}

	static void maxExample() {
		Network net = new Network();
		IntVariable x = new IntVariable(net, -1, 1, "x");
		IntVariable y = new IntVariable(net, -1, 1, "y");
		IntVariable z = x.max(y);
		z.setName("z");
		runExample(net, Solver.DEFAULT);
	}

	static void absExample() {
		Network net = new Network();
		IntVariable x = new IntVariable(net, -3, 2, "x");
		IntVariable y = x.abs();
		y.notEquals(2);
		y.setName("y");
		runExample(net, Solver.DEFAULT);
	}

	static void signExample() {
		Network net = new Network();
		IntVariable x = new IntVariable(net, -2, 2, "x");
		IntVariable y = x.sign();
		y.setName("y");
		runExample(net, Solver.DEFAULT);
	}

	static void minimizeExample() {
		Network net = new Network();
		IntVariable x = new IntVariable(net, 1, 10, "x");
		IntVariable y = new IntVariable(net, 1, 10, "y");
		// x + y >= 10
		x.add(y).ge(10);
		// z = max(x, y)
		IntVariable z = x.max(y);
		z.setName("z");
		// minimize z
		net.setObjective(z);
		runExample(net, Solver.MINIMIZE | Solver.BETTER);
	}
	
	static void elementExample() {
		Network net = new Network();
		IntVariable x = new IntVariable(net, "x");
		IntVariable i = new IntVariable(net, "i");
		int n = 3;
		IntVariable[] v = new IntVariable[n];
		for (int j = 0; j < n; j++) {
			v[j] = new IntVariable(net, 10*(j+1)); 
		}
		new Element(net, x, i, v);
		runExample(net, Solver.DEFAULT);
	}
	
	static void relationExample() {
		Network net = new Network();
		IntVariable x = new IntVariable(net, "x");
		IntVariable y = new IntVariable(net, "y");
		boolean[][] rel = {
				{ false,  true, false, false },
				{ false, false, false,  true },
				{  true, false, false, false },
				{ false, false,  true, false }
		};
		new Relation(net, x, rel, y);
		runExample(net, Solver.DEFAULT);
	}

	static void neighborhoodSearchExample() {
    	Network net = new Network();
    	int n = 4;
    	int min = 1;
    	int max = 9;
    	IntVariable[] v = new IntVariable[n];
    	for (int i = 0; i < n; i++) {
    		v[i] = new IntVariable(net, min, max, "v["+i+"]");
    	}
    	for (int i = 1; i < n; i++) {
    		v[i-1].le(v[i]);
    	}

    	int[] values = null;

    	// find a first solution and extract values
		Solver solver = new NeighborhoodSearchSolver(net, values);
		Solution solution = solver.findFirst();
		System.out.println(solution);
		List<Variable> vs = net.getVariables();
		values = new int[vs.size()];
		for (int i = 0; i < values.length; i++) {
			values[i] = solution.getIntValue(net.getVariable(i));
		}
    	
    	// add a new constraint
    	v[2].ge(3);
    	// find a neighborhood solution
    	solver = new NeighborhoodSearchSolver(net, values);
    	solution = solver.findFirst();
		System.out.println(solution);
	}
	
	public static void main(String[] args) {
		maxExample();
		absExample();
		signExample();
		minimizeExample();
		elementExample();
		relationExample();
		neighborhoodSearchExample();
	}

}
