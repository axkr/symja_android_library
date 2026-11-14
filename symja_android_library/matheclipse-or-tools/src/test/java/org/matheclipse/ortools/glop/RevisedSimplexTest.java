// Copyright 2010-2025 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified for the Symja matheclipse-or-tools module: tests ported from
// ortools/glop/revised_simplex_test.cc (OR-Tools 10.0) to JUnit 5; the primal ray and row
// index tests use the module's own programs.

package org.matheclipse.ortools.glop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.VariableStatus;
import org.matheclipse.ortools.util.TimeLimit;

/** Tests of {@link RevisedSimplex} ported from {@code revised_simplex_test.cc}. */
public class RevisedSimplexTest {

  /**
   * {@code (val - target_bound) / dir} underflows to 0.0 for a subnormal right hand side; the
   * iteration must then be treated as degenerate, otherwise the leaving variable is moved to its
   * bound without the step and the basis becomes inconsistent. As upstream (a {@code DCHECK_EQ}),
   * the inconsistency is detected by the Java {@code assert} in the primal pivot, so this test
   * needs assertions enabled (the surefire default).
   */
  @Test
  public void testSubnormalStepToBoundUnderflow() {
    LinearProgram lp = new LinearProgram();
    final int x = lp.createNewVariable();
    final int y = lp.createNewVariable();
    final int r = lp.createNewConstraint();
    lp.setVariableBounds(x, 0.0, 1.0);
    lp.setVariableBounds(y, 0.0, 1.0);
    lp.setObjectiveCoefficient(x, -1.0);
    lp.setObjectiveCoefficient(y, 0.0);
    final double denormMin = Double.MIN_VALUE;
    lp.setConstraintBounds(r, denormMin, denormMin);
    lp.setCoefficient(r, x, 2.0);
    lp.setCoefficient(r, y, 1.0);
    lp.addSlackVariablesWhereNecessary(false);

    BasisState state = new BasisState(new VariableStatus[] {VariableStatus.AT_LOWER_BOUND,
        VariableStatus.BASIC, VariableStatus.FIXED_VALUE});

    RevisedSimplex simplex = new RevisedSimplex();
    simplex.loadStateForNextSolve(state);
    Status status = simplex.solve(lp, TimeLimit.infinite());
    assertTrue(status.ok(), status.toString());
    assertNotEquals(ProblemStatus.ABNORMAL, simplex.getProblemStatus());
    assertEquals(ProblemStatus.OPTIMAL, simplex.getProblemStatus());
  }

  /**
   * The primal phase I ratio test stops at the peak of the (piecewise linear) decrease of the sum
   * of infeasibilities. Here the reduced cost of x is {@code 0.1 + 0.2 = 0.30000000000000004} and the
   * derivative is flat after the breakpoints of a (0.1) and b (0.2): the remaining rounding error
   * {@code 2.8e-17} must not count as an improvement, otherwise the ratio test goes on to the
   * breakpoint of c and the solve needs a second iteration.
   */
  @Test
  public void testPrimalPhaseIStopsAtFlatPeak() {
    LinearProgram lp = new LinearProgram();
    final int x = lp.createNewVariable();
    final int a = lp.createNewVariable();
    final int b = lp.createNewVariable();
    final int c = lp.createNewVariable();
    lp.setVariableBounds(x, 0.0, 100.0);
    lp.setVariableBounds(a, 1.0, 5.0);
    lp.setVariableBounds(b, 3.0, 10.0);
    lp.setVariableBounds(c, 0.0, 100.0);
    lp.setObjectiveCoefficient(x, 1.0);
    // a = 0.1 x, b = 0.2 x, c = 20 - x
    final int r1 = lp.createNewConstraint();
    lp.setConstraintBounds(r1, 0.0, 0.0);
    lp.setCoefficient(r1, a, 1.0);
    lp.setCoefficient(r1, x, -0.1);
    final int r2 = lp.createNewConstraint();
    lp.setConstraintBounds(r2, 0.0, 0.0);
    lp.setCoefficient(r2, b, 1.0);
    lp.setCoefficient(r2, x, -0.2);
    final int r3 = lp.createNewConstraint();
    lp.setConstraintBounds(r3, 20.0, 20.0);
    lp.setCoefficient(r3, c, 1.0);
    lp.setCoefficient(r3, x, 1.0);
    lp.addSlackVariablesWhereNecessary(false);

    // a and b are basic and below their lower bounds.
    final BasisState state = new BasisState(new VariableStatus[] {VariableStatus.AT_LOWER_BOUND,
        VariableStatus.BASIC, VariableStatus.BASIC, VariableStatus.BASIC,
        VariableStatus.FIXED_VALUE, VariableStatus.FIXED_VALUE, VariableStatus.FIXED_VALUE});

    // One phase I iteration: b leaves at its lower bound (x = 15), not c (x = 20).
    RevisedSimplex simplex = new RevisedSimplex();
    simplex.setParameters(new GlopParameters().setMaxNumberOfIterations(1));
    simplex.loadStateForNextSolve(state);
    assertTrue(simplex.solve(lp, TimeLimit.infinite()).ok());
    assertEquals(VariableStatus.AT_LOWER_BOUND, simplex.getVariableStatus(b));
    assertEquals(VariableStatus.BASIC, simplex.getVariableStatus(c));
    assertEquals(15.0, simplex.getVariableValue(x), 1e-12);

    // That point is optimal: the full solve needs a single iteration.
    simplex = new RevisedSimplex();
    simplex.loadStateForNextSolve(state);
    assertTrue(simplex.solve(lp, TimeLimit.infinite()).ok());
    assertEquals(ProblemStatus.OPTIMAL, simplex.getProblemStatus());
    assertEquals(1, simplex.getNumberOfIterations());
    assertEquals(15.0, simplex.getObjectiveValue(), 1e-12);
    assertEquals(1.5, simplex.getVariableValue(a), 1e-12);
    assertEquals(3.0, simplex.getVariableValue(b), 1e-12);
    assertEquals(5.0, simplex.getVariableValue(c), 1e-12);
  }

  /** x - y &lt;= 3, -x + y + z &lt;= 5, x, y &gt;= 0, 0 &lt;= z &lt;= 4: unbounded along (1, 1, 0). */
  private static LinearProgram unboundedProgram(boolean maximize, boolean withSlacks) {
    final double sign = maximize ? -1.0 : 1.0;
    LinearProgram lp = new LinearProgram();
    final int x = lp.createNewVariable();
    final int y = lp.createNewVariable();
    final int z = lp.createNewVariable();
    lp.setVariableBounds(x, 0.0, Double.POSITIVE_INFINITY);
    lp.setVariableBounds(y, 0.0, Double.POSITIVE_INFINITY);
    lp.setVariableBounds(z, 0.0, 4.0);
    lp.setMaximizationProblem(maximize);
    lp.setObjectiveCoefficient(x, sign * UNBOUNDED_COSTS[0]);
    lp.setObjectiveCoefficient(y, sign * UNBOUNDED_COSTS[1]);
    lp.setObjectiveCoefficient(z, sign * UNBOUNDED_COSTS[2]);
    for (int i = 0; i < UNBOUNDED_ROWS.length; i++) {
      final int row = lp.createNewConstraint();
      lp.setConstraintBounds(row, Double.NEGATIVE_INFINITY, UNBOUNDED_RHS[i]);
      for (int j = 0; j < 3; j++) {
        if (UNBOUNDED_ROWS[i][j] != 0.0) {
          lp.setCoefficient(row, j, UNBOUNDED_ROWS[i][j]);
        }
      }
    }
    if (withSlacks) {
      lp.addSlackVariablesWhereNecessary(false);
    }
    return lp;
  }

  private static final double[] UNBOUNDED_COSTS = {-2.0, -3.0, 1.0};
  private static final double[][] UNBOUNDED_ROWS = {{1.0, -1.0, 0.0}, {-1.0, 1.0, 1.0}};
  private static final double[] UNBOUNDED_RHS = {3.0, 5.0};

  /**
   * The ray r of an unbounded problem in equation form: A.r = 0 with the slack columns, an
   * improving objective, and no move towards a finite bound.
   */
  private static void assertPrimalRay(LinearProgram lp, double[] ray) {
    assertEquals(5, ray.length);
    for (int i = 0; i < UNBOUNDED_ROWS.length; i++) {
      double activity = ray[3 + i];
      for (int j = 0; j < 3; j++) {
        activity += UNBOUNDED_ROWS[i][j] * ray[j];
      }
      assertEquals(0.0, activity, 1e-12, "row " + i);
    }
    double cost = 0.0;
    for (int j = 0; j < 3; j++) {
      cost += UNBOUNDED_COSTS[j] * ray[j];
    }
    assertTrue(cost < -1e-6, "cost " + cost);
    final double[] lower = lp.variableLowerBounds();
    final double[] upper = lp.variableUpperBounds();
    for (int j = 0; j < ray.length; j++) {
      if (lower[j] != Double.NEGATIVE_INFINITY) {
        assertTrue(ray[j] >= 0.0, "column " + j + " decreases: " + ray[j]);
      }
      if (upper[j] != Double.POSITIVE_INFINITY) {
        assertTrue(ray[j] <= 0.0, "column " + j + " increases: " + ray[j]);
      }
    }
  }

  /** The primal simplex leaves a primal ray when its phase II finds the problem unbounded. */
  @Test
  public void testPrimalRayOfPrimalSimplex() {
    for (boolean maximize : new boolean[] {false, true}) {
      final LinearProgram lp = unboundedProgram(maximize, true);
      RevisedSimplex simplex = new RevisedSimplex();
      simplex.setParameters(new GlopParameters().setUseDualSimplex(false));
      assertTrue(simplex.solve(lp, TimeLimit.infinite()).ok());
      assertEquals(ProblemStatus.PRIMAL_UNBOUNDED, simplex.getProblemStatus());
      assertPrimalRay(lp, simplex.getPrimalRay());
    }
  }

  /**
   * The dual simplex phase I ends DUAL_INFEASIBLE on an unbounded problem; it then leaves the
   * primal ray built from the improvement direction of the dual infeasibilities.
   */
  @Test
  public void testPrimalRayOfDualSimplexPhaseI() {
    for (boolean maximize : new boolean[] {false, true}) {
      final LinearProgram lp = unboundedProgram(maximize, true);
      RevisedSimplex simplex = new RevisedSimplex();
      simplex.setParameters(new GlopParameters().setUseDualSimplex(true));
      assertTrue(simplex.solve(lp, TimeLimit.infinite()).ok());
      assertEquals(ProblemStatus.DUAL_INFEASIBLE, simplex.getProblemStatus());
      assertPrimalRay(lp, simplex.getPrimalRay());
    }
  }

  /**
   * A second solve of the same matrix and objective with another bound starts from the basis of
   * the DUAL_INFEASIBLE answer; it must build its ray again.
   */
  @Test
  public void testPrimalRayOfIncrementalDualSimplex() {
    final LinearProgram lp = unboundedProgram(false, true);
    RevisedSimplex simplex = new RevisedSimplex();
    simplex.setParameters(new GlopParameters().setUseDualSimplex(true));
    assertTrue(simplex.solve(lp, TimeLimit.infinite()).ok());
    assertEquals(ProblemStatus.DUAL_INFEASIBLE, simplex.getProblemStatus());
    for (double upper : new double[] {6.0, 2.5}) {
      lp.setVariableBounds(2, 0.0, upper);
      assertTrue(simplex.solve(lp, TimeLimit.infinite()).ok());
      assertEquals(ProblemStatus.DUAL_INFEASIBLE, simplex.getProblemStatus());
      assertPrimalRay(lp, simplex.getPrimalRay());
    }
  }

  /**
   * minimize -c*x with x - y &lt;= 3, x, y &gt;= 0 is unbounded along (1, 1) however small the
   * cost c is, as long as it is a dual infeasibility.
   */
  @Test
  public void testPrimalRayWithSmallCosts() {
    for (double cost : new double[] {1e-7, 3e-5, 0.25}) {
      for (boolean useDual : new boolean[] {false, true}) {
        LinearProgram lp = new LinearProgram();
        final int x = lp.createNewVariable();
        final int y = lp.createNewVariable();
        lp.setVariableBounds(x, 0.0, Double.POSITIVE_INFINITY);
        lp.setVariableBounds(y, 0.0, Double.POSITIVE_INFINITY);
        lp.setObjectiveCoefficient(x, -cost);
        final int row = lp.createNewConstraint();
        lp.setConstraintBounds(row, Double.NEGATIVE_INFINITY, 3.0);
        lp.setCoefficient(row, x, 1.0);
        lp.setCoefficient(row, y, -1.0);
        lp.addSlackVariablesWhereNecessary(false);
        RevisedSimplex simplex = new RevisedSimplex();
        simplex.setParameters(new GlopParameters().setUseDualSimplex(useDual));
        assertTrue(simplex.solve(lp, TimeLimit.infinite()).ok());
        assertEquals(useDual ? ProblemStatus.DUAL_INFEASIBLE : ProblemStatus.PRIMAL_UNBOUNDED,
            simplex.getProblemStatus(), "cost " + cost);
        final double[] ray = simplex.getPrimalRay();
        assertTrue(ray[x] > 0.0);
        assertEquals(0.0, ray[x] - ray[y] + ray[2], 1e-12);
      }
    }
  }

  /** Without preprocessing and scaling the solver hands out the ray of a DUAL_INFEASIBLE solve. */
  @Test
  public void testLpSolverPrimalRayOfDualSimplex() {
    final LinearProgram lp = unboundedProgram(false, false);
    LPSolver solver = new LPSolver();
    solver.setParameters(new GlopParameters().setUseDualSimplex(true).setUsePreprocessing(false)
        .setUseScaling(false));
    assertEquals(ProblemStatus.DUAL_INFEASIBLE, solver.solve(lp));
    final double[] ray = solver.getPrimalRay();
    assertEquals(3, ray.length);
    assertTrue(UNBOUNDED_COSTS[0] * ray[0] + UNBOUNDED_COSTS[1] * ray[1]
        + UNBOUNDED_COSTS[2] * ray[2] < -1e-6);
    assertEquals(0.0, ray[2], 0.0);
  }

  /** a + b &gt;= 4, b + c &gt;= 3, a + c &gt;= 5 with the given order of the rows and an extra row. */
  private static LinearProgram coverProgram(int[] rowOrder, double extraLowerBound) {
    final double[][] rows = {{1.0, 1.0, 0.0}, {0.0, 1.0, 1.0}, {1.0, 0.0, 1.0}};
    final double[] rhs = {4.0, 3.0, 5.0};
    final double[] costs = {3.0, 2.0, 4.0};
    LinearProgram lp = new LinearProgram();
    for (int j = 0; j < 3; j++) {
      final int col = lp.createNewVariable();
      lp.setVariableBounds(col, 0.0, Double.POSITIVE_INFINITY);
      lp.setObjectiveCoefficient(col, costs[j]);
    }
    for (int oldRow : rowOrder) {
      final int row = lp.createNewConstraint();
      if (oldRow < 0) {
        // the new row: a + b + c >= extraLowerBound
        lp.setConstraintBounds(row, extraLowerBound, Double.POSITIVE_INFINITY);
        for (int j = 0; j < 3; j++) {
          lp.setCoefficient(row, j, 1.0);
        }
      } else {
        lp.setConstraintBounds(row, rhs[oldRow], Double.POSITIVE_INFINITY);
        for (int j = 0; j < 3; j++) {
          if (rows[oldRow][j] != 0.0) {
            lp.setCoefficient(row, j, rows[oldRow][j]);
          }
        }
      }
    }
    lp.addSlackVariablesWhereNecessary(false);
    return lp;
  }

  /**
   * Rows are reordered and one row is added between two solves; with the old row indices the dual
   * edge norms of the previous basis are reused. The answer must be the one of a fresh solve, both
   * when the new row is slack at the old optimum and when it cuts it off.
   */
  @Test
  public void testOldRowIndicesForNextSolve() {
    final GlopParameters parameters = new GlopParameters().setUseDualSimplex(true);
    final int[] oldRowOfNewRow = {2, 0, LpTypes.INVALID_ROW, 1};
    for (double extraLowerBound : new double[] {1.0, 7.5}) {
      RevisedSimplex simplex = new RevisedSimplex();
      simplex.setParameters(parameters);
      assertTrue(simplex.solve(coverProgram(new int[] {0, 1, 2}, 0.0), TimeLimit.infinite()).ok());
      assertEquals(ProblemStatus.OPTIMAL, simplex.getProblemStatus());
      assertEquals(19.0, simplex.getObjectiveValue(), 1e-9);

      final VariableStatus[] old = simplex.getState().statuses;
      final VariableStatus[] statuses = new VariableStatus[3 + oldRowOfNewRow.length];
      System.arraycopy(old, 0, statuses, 0, 3);
      for (int row = 0; row < oldRowOfNewRow.length; row++) {
        statuses[3 + row] = oldRowOfNewRow[row] == LpTypes.INVALID_ROW ? VariableStatus.BASIC
            : old[3 + oldRowOfNewRow[row]];
      }

      final LinearProgram lp = coverProgram(oldRowOfNewRow, extraLowerBound);
      simplex.loadStateForNextSolve(new BasisState(statuses));
      simplex.setOldRowIndicesForNextSolve(oldRowOfNewRow);
      assertTrue(simplex.solve(lp, TimeLimit.infinite()).ok());
      assertEquals(ProblemStatus.OPTIMAL, simplex.getProblemStatus());
      assertTrue(simplex.dualEdgeNormsWereTransferred());

      RevisedSimplex fresh = new RevisedSimplex();
      fresh.setParameters(parameters);
      assertTrue(fresh.solve(lp, TimeLimit.infinite()).ok());
      assertTrue(!fresh.dualEdgeNormsWereTransferred());
      assertEquals(ProblemStatus.OPTIMAL, fresh.getProblemStatus());
      assertEquals(fresh.getObjectiveValue(), simplex.getObjectiveValue(), 1e-9);
      if (extraLowerBound == 1.0) {
        assertEquals(19.0, simplex.getObjectiveValue(), 1e-9);
        assertEquals(0, simplex.getNumberOfIterations());
      } else {
        assertTrue(simplex.getObjectiveValue() > 19.0 + 1e-6);
      }
    }
  }
}
