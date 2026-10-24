package org.matheclipse.ortools.constraintsolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Checks the propagation and search of the reimplemented original CP solver against brute force
 * enumeration on small models.
 */
public class SolverFacadeTest {

  private static int countSolutions(Solver solver, IntVar[] vars) {
    DecisionBuilder db =
        solver.makePhase(vars, Solver.CHOOSE_FIRST_UNBOUND, Solver.ASSIGN_MIN_VALUE);
    solver.newSearch(db);
    int count = 0;
    while (solver.nextSolution()) {
      count++;
    }
    solver.endSearch();
    return count;
  }

  @Test
  public void testArithmetic() {
    Solver solver = new Solver("arithmetic");
    IntVar x = solver.makeIntVar(-3, 5, "x");
    IntVar y = solver.makeIntVar(0, 5, "y");
    IntVar z = solver.makeIntVar(-4, 6, "z");
    solver.addConstraint(solver.makeEquality(
        solver.makeDifference(solver.makeSum(x, solver.makeProd(y, 2)), z), 3));
    solver.addConstraint(solver.makeGreaterOrEqual(solver.makeAbs(solver.makeDifference(x, y)), 2));
    solver.addConstraint(solver.makeNonEquality(solver.makeProd(x, z), 4));
    solver.addConstraint(solver.makeLessOrEqual(solver.makeSquare(z), 20));
    int expected = 0;
    for (int a = -3; a <= 5; a++) {
      for (int b = 0; b <= 5; b++) {
        for (int c = -4; c <= 6; c++) {
          if (a + 2 * b - c == 3 && Math.abs(a - b) >= 2 && a * c != 4 && c * c <= 20) {
            expected++;
          }
        }
      }
    }
    assertTrue(expected > 0);
    assertEquals(expected, countSolutions(solver, new IntVar[] {x, y, z}));
    // the search restores the model
    assertEquals(-3, x.min());
    assertEquals(5, x.max());
  }

  @Test
  public void testElementAndReification() {
    Solver solver = new Solver("element");
    long[] table = {7, 3, 9, 3, 1, 5};
    IntVar i = solver.makeIntVar(0, 5, "i");
    IntVar j = solver.makeIntVar(0, 5, "j");
    IntExpr e = solver.makeElement(table, i);
    IntVar b = solver.makeIsLessOrEqualCstVar(e, 4);
    solver.addConstraint(solver.makeEquality(solver.makeSum(b, j), 3));
    solver.addConstraint(solver.makeLess(i, j));
    int expected = 0;
    for (int a = 0; a <= 5; a++) {
      for (int c = 0; c <= 5; c++) {
        int bv = table[a] <= 4 ? 1 : 0;
        if (bv + c == 3 && a < c) {
          expected++;
        }
      }
    }
    assertEquals(expected, countSolutions(solver, new IntVar[] {i, j}));
  }

  @Test
  public void testCumulative() {
    Solver solver = new Solver("cumulative");
    IntervalVar[] tasks = new IntervalVar[3];
    IntVar[] starts = new IntVar[3];
    long[] durations = {2, 3, 1};
    for (int k = 0; k < 3; k++) {
      tasks[k] = solver.makeFixedDurationIntervalVar(0, 5, durations[k], false, "t" + k);
      starts[k] = tasks[k].startExpr().var();
    }
    solver.addConstraint(solver.makeCumulative(tasks, new long[] {1, 1, 1}, 1, "machine"));
    int expected = 0;
    for (int a = 0; a <= 5; a++) {
      for (int b = 0; b <= 5; b++) {
        for (int c = 0; c <= 5; c++) {
          int[] s = {a, b, c};
          boolean ok = true;
          for (int t = 0; t < 10 && ok; t++) {
            int usage = 0;
            for (int k = 0; k < 3; k++) {
              if (s[k] <= t && t < s[k] + durations[k]) {
                usage++;
              }
            }
            ok = usage <= 1;
          }
          if (ok) {
            expected++;
          }
        }
      }
    }
    assertEquals(expected, countSolutions(solver, starts));
  }

  @Test
  public void testMinimizeAndCollectors() {
    Solver solver = new Solver("knapsack");
    IntVar[] x = solver.makeIntVarArray(4, 0, 3, "x");
    long[] weights = {3, 4, 5, 2};
    long[] values = {4, 5, 7, 2};
    solver.addConstraint(solver.makeScalProdLessOrEqual(x, weights, 12));
    IntVar value = solver.makeScalProd(x, values).var();
    OptimizeVar objective = solver.makeMaximize(value, 1);
    SolutionCollector collector = solver.makeLastSolutionCollector();
    collector.add(x);
    collector.addObjective(value);
    DecisionBuilder db = solver.makePhase(x, Solver.CHOOSE_FIRST_UNBOUND, Solver.ASSIGN_MAX_VALUE);
    assertTrue(solver.solve(db, objective, collector));
    long best = 0;
    for (int a = 0; a <= 3; a++) {
      for (int b = 0; b <= 3; b++) {
        for (int c = 0; c <= 3; c++) {
          for (int d = 0; d <= 3; d++) {
            if (3 * a + 4 * b + 5 * c + 2 * d <= 12) {
              best = Math.max(best, 4 * a + 5 * b + 7 * c + 2 * d);
            }
          }
        }
      }
    }
    assertEquals(best, objective.best());
    assertEquals(1, collector.solutionCount());
    assertEquals(best, collector.objectiveValue(0));
    long weight = 0;
    long total = 0;
    for (int k = 0; k < 4; k++) {
      weight += weights[k] * collector.value(0, x[k]);
      total += values[k] * collector.value(0, x[k]);
    }
    assertTrue(weight <= 12);
    assertEquals(best, total);
  }

  @Test
  public void testLimitsAndAllSolutionCollector() {
    Solver solver = new Solver("limits");
    IntVar[] x = solver.makeIntVarArray(3, 0, 2, "x");
    solver.addConstraint(solver.makeAllDifferent(x));
    DecisionBuilder db = solver.makePhase(x, Solver.CHOOSE_FIRST_UNBOUND, Solver.ASSIGN_MIN_VALUE);
    solver.newSearch(db, solver.makeSolutionsLimit(4));
    int count = 0;
    while (solver.nextSolution()) {
      count++;
    }
    solver.endSearch();
    assertEquals(4, count);
    SolutionCollector all = solver.makeAllSolutionCollector();
    all.add(x);
    assertTrue(solver.solve(db, all));
    assertEquals(6, all.solutionCount());
    assertEquals(2, all.value(5, x[0]));
    SolutionCollector first = solver.makeFirstSolutionCollector();
    first.add(x);
    assertTrue(solver.solve(db, first));
    assertEquals(1, first.solutionCount());
    assertEquals(0, first.value(0, x[0]));
  }

  @Test
  public void testInfeasibleModel() {
    Solver solver = new Solver("infeasible");
    IntVar x = solver.makeIntVar(0, 3, "x");
    IntVar y = solver.makeIntVar(0, 3, "y");
    solver.addConstraint(solver.makeEquality(solver.makeSum(x, y), 7));
    DecisionBuilder db =
        solver.makePhase(x, y, Solver.CHOOSE_FIRST_UNBOUND, Solver.ASSIGN_MIN_VALUE);
    assertFalse(solver.solve(db));
    assertEquals("x(0..3)", x.toString());
  }

  @Test
  public void testDomainsAndToString() {
    Solver solver = new Solver("domains");
    IntVar x = solver.makeIntVar(new long[] {1, 3, 4, 8}, "x");
    assertEquals(4, x.size());
    assertTrue(x.contains(3));
    assertFalse(x.contains(2));
    x.removeValue(4);
    assertEquals("x(1 3 8)", x.toString());
    x.setMin(2);
    assertEquals(3, x.min());
    IntVar c = solver.makeIntConst(5, "c");
    assertEquals("c(5)", c.toString());
  }
}
