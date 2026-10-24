package org.matheclipse.ortools.linearsolver.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.linearsolver.MPSolver;

/** Tests of the presolve, logging and reformulation details of {@link BranchAndBound}. */
public class BranchAndBoundInternalTest {

  private static MPModel.Variable var(double lb, double ub, boolean integer, double cost) {
    final MPModel.Variable v = new MPModel.Variable();
    v.lowerBound = lb;
    v.upperBound = ub;
    v.isInteger = integer;
    v.objectiveCoefficient = cost;
    return v;
  }

  private static MPModel.Constraint row(double lo, double hi, int[] vars, double[] coefficients) {
    final MPModel.Constraint c = new MPModel.Constraint();
    c.lowerBound = lo;
    c.upperBound = hi;
    for (int k = 0; k < vars.length; ++k) {
      c.varIndex.add(vars[k]);
      c.coefficient.add(coefficients[k]);
    }
    return c;
  }

  /** Items 0..n-1 into bins 0..k-1: variable i * k + b. */
  private static MPModel multipleKnapsack(int[] weights, int[] values, int[] capacities) {
    final MPModel model = new MPModel();
    model.maximize = true;
    final int n = weights.length;
    final int k = capacities.length;
    for (int i = 0; i < n; ++i) {
      for (int b = 0; b < k; ++b) {
        model.variables.add(var(0, 1, true, values[i]));
      }
    }
    for (int i = 0; i < n; ++i) {
      final int[] vars = new int[k];
      final double[] ones = new double[k];
      for (int b = 0; b < k; ++b) {
        vars[b] = i * k + b;
        ones[b] = 1;
      }
      model.constraints.add(row(0, 1, vars, ones));
    }
    for (int b = 0; b < k; ++b) {
      final int[] vars = new int[n];
      final double[] w = new double[n];
      for (int i = 0; i < n; ++i) {
        vars[i] = i * k + b;
        w[i] = weights[i];
      }
      model.constraints.add(row(0, capacities[b], vars, w));
    }
    return model;
  }

  private static List<String> solveWithLog(MPModel model, MPSolverInterface.SolveResult[] result) {
    final List<String> log = new ArrayList<>();
    final BranchAndBound.Options options = new BranchAndBound.Options();
    options.logSearchProgress = true;
    options.logCallback = log::add;
    final BranchAndBound bb = new BranchAndBound(options);
    result[0] = bb.solve(model);
    return log;
  }

  private static boolean contains(List<String> log, String text) {
    for (String line : log) {
      if (line.contains(text)) {
        return true;
      }
    }
    return false;
  }

  @Test
  public void testSymmetryDetected() {
    // 6 items, 3 identical bins of capacity 10 (weights 6, 5, 4, 4, 3, 7; values equal weights):
    // the best packing fills 10 + 10 + 9 of the total weight 29, so the optimum is 29
    final int[] weights = {6, 5, 4, 4, 3, 7};
    final MPModel model = multipleKnapsack(weights, weights, new int[] {10, 10, 10});
    final MPSolverInterface.SolveResult[] result = new MPSolverInterface.SolveResult[1];
    final List<String> log = solveWithLog(model, result);
    assertTrue(contains(log, "Symmetry: 3 interchangeable columns over 6 packing rows"),
        log.toString());
    assertEquals(MPSolver.ResultStatus.OPTIMAL, result[0].status);
    assertEquals(29.0, result[0].objectiveValue, 1e-9);
    // the input model is not modified by the presolve
    assertEquals(9, model.constraints.size());
  }

  @Test
  public void testNoSymmetryForDifferentBins() {
    final int[] weights = {6, 5, 4, 4, 3, 7};
    final MPModel model = multipleKnapsack(weights, weights, new int[] {10, 10, 9});
    final MPSolverInterface.SolveResult[] result = new MPSolverInterface.SolveResult[1];
    final List<String> log = solveWithLog(model, result);
    assertFalse(contains(log, "Symmetry:"), log.toString());
    assertEquals(MPSolver.ResultStatus.OPTIMAL, result[0].status);
    // 10 + 10 + 9 is still reachable: {6, 4}, {7, 3}, {5, 4}
    assertEquals(29.0, result[0].objectiveValue, 1e-9);
  }

  @Test
  public void testNoSymmetryForDifferentValues() {
    // the same bins but the value of an item depends on the bin
    final int[] weights = {6, 5, 4};
    final MPModel model = multipleKnapsack(weights, weights, new int[] {10, 10});
    model.variables.get(0).objectiveCoefficient = 8; // item 0 in bin 0
    final MPSolverInterface.SolveResult[] result = new MPSolverInterface.SolveResult[1];
    final List<String> log = solveWithLog(model, result);
    assertFalse(contains(log, "Symmetry:"), log.toString());
    // all items fit ({6, 4} and {5}); item 0 in bin 0 is worth 8: 8 + 5 + 4 = 17
    assertEquals(17.0, result[0].objectiveValue, 1e-9);
  }

  @Test
  public void testObjectiveStepPruning() {
    // all objective coefficients are multiples of 5: the bound is rounded to such a multiple
    final int[] weights = {48, 30, 42, 36, 36};
    final int[] values = {10, 30, 25, 50, 35};
    final MPModel model = multipleKnapsack(weights, values, new int[] {100});
    final MPSolverInterface.SolveResult[] result = new MPSolverInterface.SolveResult[1];
    solveWithLog(model, result);
    // one bin of capacity 100: every three items weigh at least 30 + 36 + 36 = 102, and the
    // best pair is {3, 4} (weight 72, value 85; next {1, 3} with 80)
    assertEquals(85.0, result[0].objectiveValue, 1e-9);
    assertEquals(85.0, result[0].bestObjectiveBound, 1e-9);
  }

  @Test
  public void testIndicatorBigM() {
    // z = 1 => x + y >= 8, z = 0 => x <= 1; min x + y + 3 (1 - z) with x, y in [0, 5] integer
    final MPModel model = new MPModel();
    model.variables.add(var(0, 5, true, 1));
    model.variables.add(var(0, 5, true, 1));
    model.variables.add(var(0, 1, true, -3));
    model.objectiveOffset = 3;
    final MPModel.IndicatorConstraint on = new MPModel.IndicatorConstraint();
    on.varIndex = 2;
    on.varValue = 1;
    on.constraint = row(8, Double.POSITIVE_INFINITY, new int[] {0, 1}, new double[] {1, 1});
    final MPModel.IndicatorConstraint off = new MPModel.IndicatorConstraint();
    off.varIndex = 2;
    off.varValue = 0;
    off.constraint = row(Double.NEGATIVE_INFINITY, 1, new int[] {0}, new double[] {1});
    model.generalConstraints.add(on);
    model.generalConstraints.add(off);
    // z = 1: x + y >= 8 -> 8; z = 0: x <= 1 -> 0 + 3 = 3: the optimum is 3 with z = 0
    double best = Double.POSITIVE_INFINITY;
    for (int x = 0; x <= 5; ++x) {
      for (int y = 0; y <= 5; ++y) {
        for (int z = 0; z <= 1; ++z) {
          final boolean ok = z == 1 ? x + y >= 8 : x <= 1;
          if (ok) {
            best = Math.min(best, x + y + 3 * (1 - z));
          }
        }
      }
    }
    assertEquals(3.0, best, 0.0);
    final MPModel linear = BranchAndBound.linearizeIndicatorConstraints(model);
    assertTrue(linear.generalConstraints.isEmpty());
    assertEquals(2, linear.constraints.size());
    final MPSolverInterface.SolveResult[] result = new MPSolverInterface.SolveResult[1];
    solveWithLog(model, result);
    assertEquals(MPSolver.ResultStatus.OPTIMAL, result[0].status);
    assertEquals(best, result[0].objectiveValue, 1e-9);
    assertEquals(0.0, result[0].variableValues[2], 0.0);
  }

  @Test
  public void testIndicatorWithInfiniteBoundThrows() {
    final MPModel model = new MPModel();
    model.variables.add(var(0, Double.POSITIVE_INFINITY, false, 1));
    model.variables.add(var(0, 1, true, 0));
    final MPModel.IndicatorConstraint ind = new MPModel.IndicatorConstraint();
    ind.varIndex = 1;
    ind.varValue = 1;
    ind.constraint = row(Double.NEGATIVE_INFINITY, 3, new int[] {0}, new double[] {1});
    model.generalConstraints.add(ind);
    assertThrows(IllegalArgumentException.class,
        () -> BranchAndBound.linearizeIndicatorConstraints(model));
  }

  @Test
  public void testModelWithoutIndicatorsIsShared() {
    final MPModel model = new MPModel();
    model.variables.add(var(0, 1, true, 1));
    assertSame(model, BranchAndBound.linearizeIndicatorConstraints(model));
  }
}
