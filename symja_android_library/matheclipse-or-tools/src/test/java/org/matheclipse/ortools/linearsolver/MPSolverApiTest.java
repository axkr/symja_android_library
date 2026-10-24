package org.matheclipse.ortools.linearsolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests of the {@link MPSolver} API: solver names, lookups, solution invalidation, clear. */
public class MPSolverApiTest {

  private static final double INF = Double.POSITIVE_INFINITY;

  @Test
  public void testSolverNames() {
    for (String name : new String[] {"GLOP", "glop", "CLP", "PDLP", "GLPK_LP", "XPRESS_LP",
        "GUROBI_LP", "CPLEX_LP", "GLOP_LINEAR_PROGRAMMING"}) {
      final MPSolver solver = MPSolver.createSolver(name);
      assertNotNull(solver, name);
      assertFalse(solver.isMip(), name);
    }
    for (String name : new String[] {"SCIP", "scip", "CBC", "SAT", "CP_SAT", "BOP", "GLPK",
        "XPRESS", "GUROBI", "CPLEX", "SCIP_MIXED_INTEGER_PROGRAMMING"}) {
      final MPSolver solver = MPSolver.createSolver(name);
      assertNotNull(solver, name);
      assertTrue(solver.isMip(), name);
    }
    assertNull(MPSolver.createSolver("NO_SUCH_SOLVER"));
    // OR-Tools 10.0 removed the dedicated knapsack MIP solver type.
    assertNull(MPSolver.createSolver("KNAPSACK"));
    assertNull(MPSolver.createSolver("KNAPSACK_MIXED_INTEGER_PROGRAMMING"));
    assertThrows(IllegalArgumentException.class,
        () -> MPSolver.OptimizationProblemType.swigToEnum(13));
  }

  /** max x + 2y, x + y <= 4, x - y >= -1, x, y >= 0: optimum 6.5 at (1.5, 2.5). */
  private static MPSolver smallLp() {
    final MPSolver solver = MPSolver.createSolver("GLOP");
    final MPVariable x = solver.makeNumVar(0, INF, "x");
    final MPVariable y = solver.makeNumVar(0, INF, "y");
    final MPConstraint c0 = solver.makeConstraint(-INF, 4, "c0");
    c0.setCoefficient(x, 1);
    c0.setCoefficient(y, 1);
    final MPConstraint c1 = solver.makeConstraint(-1, INF, "c1");
    c1.setCoefficient(x, 1);
    c1.setCoefficient(y, -1);
    solver.objective().setCoefficient(x, 1);
    solver.objective().setCoefficient(y, 2);
    solver.objective().setMaximization();
    return solver;
  }

  @Test
  public void testLookupAndIndices() {
    final MPSolver solver = smallLp();
    final MPVariable x = solver.lookupVariableOrNull("x");
    assertNotNull(x);
    assertEquals(0, x.index());
    assertSame(x, solver.variables()[0]);
    assertNull(solver.lookupVariableOrNull("z"));
    final MPConstraint c1 = solver.lookupConstraintOrNull("c1");
    assertNotNull(c1);
    assertEquals(1, c1.index());
    assertNull(solver.lookupConstraintOrNull("c2"));
    assertEquals(2, solver.numVariables());
    assertEquals(2, solver.numConstraints());
  }

  @Test
  public void testSolutionAndInvalidation() {
    final MPSolver solver = smallLp();
    assertEquals(MPSolver.ResultStatus.OPTIMAL, solver.solve());
    final MPVariable x = solver.lookupVariableOrNull("x");
    final MPVariable y = solver.lookupVariableOrNull("y");
    assertEquals(6.5, solver.objective().value(), 1e-9);
    assertEquals(1.5, x.solutionValue(), 1e-9);
    assertEquals(2.5, y.solutionValue(), 1e-9);
    // duals: 1 = u + v, 2 = u - v -> u = 3/2, v = -1/2
    assertEquals(1.5, solver.lookupConstraintOrNull("c0").dualValue(), 1e-9);
    assertEquals(-0.5, solver.lookupConstraintOrNull("c1").dualValue(), 1e-9);
    final double[] activities = solver.computeConstraintActivities();
    assertEquals(4.0, activities[0], 1e-9);
    assertEquals(-1.0, activities[1], 1e-9);
    assertEquals(MPSolver.BasisStatus.BASIC, x.basisStatus());
    assertTrue(solver.verifySolution(1e-9, false));
    assertTrue(solver.iterations() >= 0);
    assertEquals(-1, solver.nodes());
    // a change of the model invalidates the solution
    solver.objective().setCoefficient(x, 3);
    assertEquals(0.0, x.solutionValue(), 0.0);
    assertEquals(0.0, solver.objective().value(), 0.0);
    // solving again: max 3x + 2y gives x = 4, y = 0 (12) against (1.5, 2.5) (9.5)
    assertEquals(MPSolver.ResultStatus.OPTIMAL, solver.solve());
    assertEquals(12.0, solver.objective().value(), 1e-9);
    assertEquals(4.0, x.solutionValue(), 1e-9);
    // a new bound
    y.setLb(1);
    assertEquals(MPSolver.ResultStatus.OPTIMAL, solver.solve());
    // y = 1: x <= 3 -> 9 + 2 = 11
    assertEquals(11.0, solver.objective().value(), 1e-9);
  }

  @Test
  public void testClearAndReuse() {
    final MPSolver solver = smallLp();
    assertEquals(MPSolver.ResultStatus.OPTIMAL, solver.solve());
    solver.clear();
    assertEquals(0, solver.numVariables());
    assertEquals(0, solver.numConstraints());
    assertNull(solver.lookupVariableOrNull("x"));
    final MPVariable z = solver.makeNumVar(-2, 5, "z");
    solver.objective().setCoefficient(z, -1);
    solver.objective().setMinimization();
    assertEquals(MPSolver.ResultStatus.OPTIMAL, solver.solve());
    assertEquals(-5.0, solver.objective().value(), 1e-9);
  }

  @Test
  public void testInfeasibleLp() {
    final MPSolver solver = MPSolver.createSolver("GLOP");
    final MPVariable x = solver.makeNumVar(0, 1, "x");
    final MPConstraint c = solver.makeConstraint(2, INF, "c");
    c.setCoefficient(x, 1);
    assertEquals(MPSolver.ResultStatus.INFEASIBLE, solver.solve());
    assertFalse(solver.verifySolution(1e-9, false));
  }

  @Test
  public void testMpsExportOfMip() {
    final MPSolver solver = MPSolver.createSolver("SCIP");
    final MPVariable x = solver.makeIntVar(0, 10, "x");
    final MPVariable b = solver.makeBoolVar("b");
    final MPConstraint c = solver.makeConstraint(-INF, 7.5, "cap");
    c.setCoefficient(x, 2);
    c.setCoefficient(b, 3);
    solver.objective().setCoefficient(x, 1);
    solver.objective().setCoefficient(b, 2);
    solver.objective().setMaximization();
    final String mps = solver.exportModelAsMpsFormat(false, false);
    assertTrue(mps.contains("MARKER"), mps);
    assertTrue(mps.contains("cap"), mps);
    final String lp = solver.exportModelAsLpFormat(false);
    assertTrue(lp.contains("Generals"), lp);
    assertTrue(lp.contains("Binaries"), lp);
    // 2x + 3b <= 7.5: b = 1 -> x = 2 (4); b = 0 -> x = 3 (3)
    assertEquals(MPSolver.ResultStatus.OPTIMAL, solver.solve());
    assertEquals(4.0, solver.objective().value(), 1e-9);
  }
}
