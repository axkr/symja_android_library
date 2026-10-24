package org.matheclipse.ortools.glop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.lpdata.ConstraintStatus;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.VariableStatus;

/** Tests of {@link LPSolver} on small problems with hand computed solutions. */
public class LPSolverTest {

  private static final double TOL = 1e-7;

  /** max 10x+6y+4z, x+y+z<=100, 10x+4y+5z<=600, 2x+2y+6z<=300, x,y,z>=0. */
  static LinearProgram firstExample() {
    LinearProgram lp = new LinearProgram();
    int x = lp.createNewVariable();
    int y = lp.createNewVariable();
    int z = lp.createNewVariable();
    lp.setObjectiveCoefficient(x, 10);
    lp.setObjectiveCoefficient(y, 6);
    lp.setObjectiveCoefficient(z, 4);
    lp.setMaximizationProblem(true);
    int c0 = lp.createNewConstraint();
    lp.setConstraintBounds(c0, -LpTypes.INFINITY, 100);
    lp.setCoefficient(c0, x, 1);
    lp.setCoefficient(c0, y, 1);
    lp.setCoefficient(c0, z, 1);
    int c1 = lp.createNewConstraint();
    lp.setConstraintBounds(c1, -LpTypes.INFINITY, 600);
    lp.setCoefficient(c1, x, 10);
    lp.setCoefficient(c1, y, 4);
    lp.setCoefficient(c1, z, 5);
    int c2 = lp.createNewConstraint();
    lp.setConstraintBounds(c2, -LpTypes.INFINITY, 300);
    lp.setCoefficient(c2, x, 2);
    lp.setCoefficient(c2, y, 2);
    lp.setCoefficient(c2, z, 6);
    lp.cleanUp();
    return lp;
  }

  private static void checkFirstExample(LPSolver solver, ProblemStatus status) {
    assertEquals(ProblemStatus.OPTIMAL, status);
    // optimum x = 100/3, y = 200/3, z = 0: c0 and c1 tight
    assertEquals(2200.0 / 3.0, solver.getObjectiveValue(), 1e-6);
    assertEquals(100.0 / 3.0, solver.variableValues()[0], 1e-6);
    assertEquals(200.0 / 3.0, solver.variableValues()[1], 1e-6);
    assertEquals(0.0, solver.variableValues()[2], 1e-6);
    // duals: y0 + 10 y1 = 10, y0 + 4 y1 = 6 -> y1 = 2/3, y0 = 10/3
    assertEquals(10.0 / 3.0, solver.dualValues()[0], 1e-6);
    assertEquals(2.0 / 3.0, solver.dualValues()[1], 1e-6);
    assertEquals(0.0, solver.dualValues()[2], 1e-6);
    assertEquals(ConstraintStatus.AT_UPPER_BOUND, solver.constraintStatuses()[0]);
    assertEquals(ConstraintStatus.AT_UPPER_BOUND, solver.constraintStatuses()[1]);
    assertEquals(ConstraintStatus.BASIC, solver.constraintStatuses()[2]);
    assertEquals(VariableStatus.BASIC, solver.variableStatuses()[0]);
    assertEquals(VariableStatus.BASIC, solver.variableStatuses()[1]);
    assertEquals(VariableStatus.AT_LOWER_BOUND, solver.variableStatuses()[2]);
    // reduced cost of z: 4 - 10/3 - 5*2/3 = -8/3
    assertEquals(-8.0 / 3.0, solver.reducedCosts()[2], 1e-6);
  }

  @Test
  public void testFirstExamplePrimal() {
    LPSolver solver = new LPSolver();
    checkFirstExample(solver, solver.solve(firstExample()));
  }

  @Test
  public void testFirstExampleDual() {
    LPSolver solver = new LPSolver();
    solver.setParameters(new GlopParameters().setUseDualSimplex(true));
    checkFirstExample(solver, solver.solve(firstExample()));
  }

  @Test
  public void testFirstExampleRuizScaling() {
    for (boolean dual : new boolean[] {false, true}) {
      LPSolver solver = new LPSolver();
      solver.setParameters(new GlopParameters().setUseDualSimplex(dual)
          .setScalingMethod(GlopParameters.ScalingAlgorithm.RUIZ));
      checkFirstExample(solver, solver.solve(firstExample()));
    }
  }

  @Test
  public void testFirstExampleNoPresolveNoScaling() {
    LPSolver solver = new LPSolver();
    solver.setParameters(new GlopParameters().setUsePreprocessing(false).setUseScaling(false));
    checkFirstExample(solver, solver.solve(firstExample()));
  }

  /** Primal and dual simplex, with and without presolve. */
  private static GlopParameters[] parameterSets() {
    return new GlopParameters[] {new GlopParameters(),
        new GlopParameters().setUseDualSimplex(true),
        new GlopParameters().setUsePreprocessing(false),
        new GlopParameters().setUsePreprocessing(false).setUseDualSimplex(true)};
  }

  /** Creates an LP from dense data; rows lo &lt;= a.x &lt;= hi, columns lb &lt;= x &lt;= ub. */
  static LinearProgram lp(boolean maximize, double[] c, double[][] a, double[] lo, double[] hi,
      double[] lb, double[] ub) {
    LinearProgram lp = new LinearProgram();
    for (int j = 0; j < c.length; ++j) {
      final int col = lp.createNewVariable();
      lp.setVariableBounds(col, lb[j], ub[j]);
      lp.setObjectiveCoefficient(col, c[j]);
    }
    for (int i = 0; i < a.length; ++i) {
      final int row = lp.createNewConstraint();
      lp.setConstraintBounds(row, lo[i], hi[i]);
      for (int j = 0; j < c.length; ++j) {
        if (a[i][j] != 0.0) {
          lp.setCoefficient(row, j, a[i][j]);
        }
      }
    }
    lp.setMaximizationProblem(maximize);
    lp.cleanUp();
    return lp;
  }

  private static final double INF = LpTypes.INFINITY;

  @Test
  public void testInfeasible() {
    // x + y >= 5 and x + y <= 3
    for (GlopParameters p : parameterSets()) {
      LPSolver solver = new LPSolver();
      solver.setParameters(p);
      final ProblemStatus status = solver.solve(lp(false, new double[] {1, 1},
          new double[][] {{1, 1}, {1, 1}}, new double[] {5, -INF}, new double[] {INF, 3},
          new double[] {0, 0}, new double[] {INF, INF}));
      // the dual simplex proves the infeasibility with an unbounded dual ray
      assertTrue(status == ProblemStatus.PRIMAL_INFEASIBLE
          || status == ProblemStatus.DUAL_UNBOUNDED
          || status == ProblemStatus.INFEASIBLE_OR_UNBOUNDED, status.toString());
    }
  }

  @Test
  public void testInvalidBounds() {
    LPSolver solver = new LPSolver();
    assertEquals(ProblemStatus.INVALID_PROBLEM,
        solver.solve(lp(false, new double[] {1}, new double[0][], new double[0], new double[0],
            new double[] {2}, new double[] {1})));
  }

  @Test
  public void testUnbounded() {
    // max x + y with x - y <= 1, x, y >= 0: the ray (1, 1) is feasible
    for (GlopParameters p : parameterSets()) {
      LPSolver solver = new LPSolver();
      solver.setParameters(p);
      final ProblemStatus status = solver.solve(lp(true, new double[] {1, 1},
          new double[][] {{1, -1}}, new double[] {-INF}, new double[] {1},
          new double[] {0, 0}, new double[] {INF, INF}));
      assertTrue(status == ProblemStatus.PRIMAL_UNBOUNDED
          || status == ProblemStatus.DUAL_INFEASIBLE
          || status == ProblemStatus.INFEASIBLE_OR_UNBOUNDED, status.toString());
    }
  }

  @Test
  public void testDegenerateBeale() {
    // Beale's cycling example: min -3/4 x4 + 20 x5 - 1/2 x6 + 6 x7 subject to
    // 1/4 x4 - 8 x5 - x6 + 9 x7 <= 0, 1/2 x4 - 12 x5 - 1/2 x6 + 3 x7 <= 0, x6 <= 1, x >= 0.
    // At x = (1, 0, 1, 0) the duals (0, -3/2, -5/4) are feasible (reduced costs of x5 and x7:
    // 20 - 18 = 2 and 6 + 9/2 = 21/2), so -5/4 is optimal.
    for (GlopParameters p : parameterSets()) {
      LPSolver solver = new LPSolver();
      solver.setParameters(p);
      final ProblemStatus status = solver.solve(lp(false, new double[] {-0.75, 20, -0.5, 6},
          new double[][] {{0.25, -8, -1, 9}, {0.5, -12, -0.5, 3}, {0, 0, 1, 0}},
          new double[] {-INF, -INF, -INF}, new double[] {0, 0, 1}, new double[] {0, 0, 0, 0},
          new double[] {INF, INF, INF, INF}));
      assertEquals(ProblemStatus.OPTIMAL, status);
      assertEquals(-1.25, solver.getObjectiveValue(), 1e-7);
      assertEquals(1.0, solver.variableValues()[0], 1e-7);
      assertEquals(0.0, solver.variableValues()[1], 1e-7);
      assertEquals(1.0, solver.variableValues()[2], 1e-7);
      assertEquals(0.0, solver.variableValues()[3], 1e-7);
      assertEquals(0.0, solver.dualValues()[0], 1e-7);
      assertEquals(-1.5, solver.dualValues()[1], 1e-7);
      assertEquals(-1.25, solver.dualValues()[2], 1e-7);
      assertEquals(2.0, solver.reducedCosts()[1], 1e-7);
      assertEquals(10.5, solver.reducedCosts()[3], 1e-7);
    }
  }

  @Test
  public void testFreeVariablesAndEquality() {
    // min x + y, x + y >= 2, x - y = 1, x and y free: y = x - 1, 2x - 1 >= 2 -> x = 3/2, y = 1/2.
    // Duals: 1 = u + v, 1 = u - v -> u = 1 (row 0), v = 0 (row 1).
    for (GlopParameters p : parameterSets()) {
      LPSolver solver = new LPSolver();
      solver.setParameters(p);
      final ProblemStatus status = solver.solve(lp(false, new double[] {1, 1},
          new double[][] {{1, 1}, {1, -1}}, new double[] {2, 1}, new double[] {INF, 1},
          new double[] {-INF, -INF}, new double[] {INF, INF}));
      assertEquals(ProblemStatus.OPTIMAL, status);
      assertEquals(2.0, solver.getObjectiveValue(), 1e-7);
      assertEquals(1.5, solver.variableValues()[0], 1e-7);
      assertEquals(0.5, solver.variableValues()[1], 1e-7);
      assertEquals(1.0, solver.dualValues()[0], 1e-7);
      assertEquals(0.0, solver.dualValues()[1], 1e-7);
      assertEquals(0.0, solver.reducedCosts()[0], 1e-7);
      assertEquals(0.0, solver.reducedCosts()[1], 1e-7);
      assertEquals(ConstraintStatus.AT_LOWER_BOUND, solver.constraintStatuses()[0]);
    }
  }

  @Test
  public void testRangedRowsAndBoxedVariables() {
    // max x + 2y, 1 <= x + y <= 4, -1 <= x - y <= 1, -2 <= x <= 3, 0.5 <= y <= 3.
    // x + 2y = (x + y) + y <= 4 + y and y <= x + 1 = 5 - y gives y <= 2.5: optimum 6.5 at
    // (1.5, 2.5) where x + y = 4 and x - y = -1. Duals: 1 = u + v, 2 = u - v -> u = 3/2, v = -1/2.
    for (GlopParameters p : parameterSets()) {
      LPSolver solver = new LPSolver();
      solver.setParameters(p);
      final ProblemStatus status = solver.solve(lp(true, new double[] {1, 2},
          new double[][] {{1, 1}, {1, -1}}, new double[] {1, -1}, new double[] {4, 1},
          new double[] {-2, 0.5}, new double[] {3, 3}));
      assertEquals(ProblemStatus.OPTIMAL, status);
      assertEquals(6.5, solver.getObjectiveValue(), 1e-7);
      assertEquals(1.5, solver.variableValues()[0], 1e-7);
      assertEquals(2.5, solver.variableValues()[1], 1e-7);
      assertEquals(1.5, solver.dualValues()[0], 1e-7);
      assertEquals(-0.5, solver.dualValues()[1], 1e-7);
      assertEquals(ConstraintStatus.AT_UPPER_BOUND, solver.constraintStatuses()[0]);
      assertEquals(ConstraintStatus.AT_LOWER_BOUND, solver.constraintStatuses()[1]);
    }
  }

  @Test
  public void testReducedCostAtUpperBound() {
    // max 2x + y, x + y <= 3, 0 <= x <= 1, y >= 0: x = 1 at its upper bound, y = 2 basic.
    // Dual of the row: 1 (from y), reduced cost of x: 2 - 1 = 1.
    for (GlopParameters p : parameterSets()) {
      LPSolver solver = new LPSolver();
      solver.setParameters(p);
      final ProblemStatus status = solver.solve(lp(true, new double[] {2, 1},
          new double[][] {{1, 1}}, new double[] {-INF}, new double[] {3},
          new double[] {0, 0}, new double[] {1, INF}));
      assertEquals(ProblemStatus.OPTIMAL, status);
      assertEquals(4.0, solver.getObjectiveValue(), 1e-7);
      assertEquals(1.0, solver.variableValues()[0], 1e-7);
      assertEquals(2.0, solver.variableValues()[1], 1e-7);
      assertEquals(1.0, solver.dualValues()[0], 1e-7);
      assertEquals(1.0, solver.reducedCosts()[0], 1e-7);
      assertEquals(0.0, solver.reducedCosts()[1], 1e-7);
      assertEquals(VariableStatus.AT_UPPER_BOUND, solver.variableStatuses()[0]);
    }
  }
}
