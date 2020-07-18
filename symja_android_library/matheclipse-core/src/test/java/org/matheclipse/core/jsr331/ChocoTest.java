package org.matheclipse.core.jsr331;

//import org.chocosolver.solver.Model;
//import org.chocosolver.solver.Solver;
//import org.chocosolver.solver.search.strategy.Search;
//import org.chocosolver.solver.variables.IntVar;

public class ChocoTest {
//	public static void main(String[] args) {
//		// 1. Create a Model
//		Model model = new Model("my first problem");
//		// 2. Create variables
//		IntVar x = model.intVar("X", 0, 5);
//		IntVar y = model.intVar("Y", 0, 5);
//		// 3. Create and post constraints thanks to the model
//		model.element(x, new int[] { 5, 0, 4, 1, 3, 2 }, y).post();
//		// 3b. Or directly through variables
//		x.add(y).lt(5).post();
//		// 4. Get the solver
//		Solver solver = model.getSolver();
//		// 5. Define the search strategy
//		solver.setSearch(Search.inputOrderLBSearch(x, y));
//		// 6. Launch the resolution process
//		while (solver.solve()) {
//			System.out.println("X:" + x.getValue() + ", Y:" + y.getValue());
//		}
//		// 7. Print search statistics
//		// solver.printStatistics();
//
//	}
}
