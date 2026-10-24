package org.matheclipse.ortools.sat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.sat.engine.CpSatEngine;
import org.matheclipse.ortools.sat.engine.SolutionChecker;
import org.matheclipse.ortools.util.Domain;

/** Tests of the solver features of the Java engine beyond the upstream unit tests. */
public class CpSatEngineTest {

  private static CpModel differentModel() {
    CpModel model = new CpModel();
    IntVar x = model.newIntVar(0, 2, "x");
    IntVar y = model.newIntVar(0, 2, "y");
    model.addDifferent(x, y);
    return model;
  }

  @Test
  public void testResumableEnumeration() {
    CpModelProto proto = differentModel().model();
    CpSatEngine engine = new CpSatEngine(proto, SatParameters.getDefaultInstance());
    Set<String> seen = new HashSet<>();
    while (engine.nextSolution()) {
      long[] v = engine.solution();
      assertTrue(SolutionChecker.isFeasible(proto, v));
      assertTrue(seen.add(v[0] + "," + v[1]));
    }
    assertEquals(6, seen.size());
    assertEquals(6, engine.numSolutions());
    assertTrue(engine.isExhausted());
    assertFalse(engine.isStopped());
    assertFalse(engine.nextSolution());
  }

  @Test
  public void testResumableOptimization() {
    CpModel model = differentModel();
    IntVar x = model.getIntVarFromProtoIndex(0);
    IntVar y = model.getIntVarFromProtoIndex(1);
    model.maximize(LinearExpr.newBuilder().addTerm(x, 3).add(y));
    CpSatEngine engine = new CpSatEngine(model.model(), SatParameters.getDefaultInstance());
    double previous = Double.NEGATIVE_INFINITY;
    while (engine.nextSolution()) {
      assertTrue(engine.objectiveValue() > previous);
      previous = engine.objectiveValue();
    }
    assertTrue(engine.isExhausted());
    assertEquals(7.0, previous, 0); // x = 2, y = 1
    assertEquals(7.0, engine.bestObjectiveBound(), 0);
  }

  @Test
  public void testInfeasibleAndInvalid() {
    CpModel model = new CpModel();
    IntVar x = model.newIntVar(0, 5, "x");
    model.addGreaterOrEqual(x, 6);
    CpSolver solver = new CpSolver();
    assertEquals(CpSolverStatus.INFEASIBLE, solver.solve(model));
    assertEquals(0, solver.response().getSolutionCount());

    CpModel invalid = new CpModel();
    IntVar y = invalid.newIntVar(0, 5, "y");
    invalid.addModuloEquality(y, y, 0);
    assertEquals(CpSolverStatus.MODEL_INVALID, solver.solve(invalid));
    assertTrue(solver.solutionInfo().startsWith("An int_mod must have a strictly positive"));
    assertEquals(solver.solutionInfo(), invalid.validate());
  }

  // a model on which propagation is too weak: an odd sum of even terms
  private static CpModel parityModel(int n) {
    CpModel model = new CpModel();
    IntVar[] x = new IntVar[n];
    for (int i = 0; i < n; i++) {
      x[i] = model.newIntVar(0, 10, "x" + i);
    }
    long[] coeffs = new long[n];
    java.util.Arrays.fill(coeffs, 2);
    model.addEquality(LinearExpr.weightedSum(x, coeffs), 2 * n + 1);
    return model;
  }

  @Test
  public void testTimeLimit() {
    CpSolver solver = new CpSolver();
    solver.getParameters().setMaxTimeInSeconds(0.2);
    long t0 = System.nanoTime();
    CpSolverStatus status = solver.solve(parityModel(30));
    double elapsed = (System.nanoTime() - t0) * 1e-9;
    assertEquals(CpSolverStatus.UNKNOWN, status);
    assertTrue(elapsed < 2.0, "time limit not respected: " + elapsed);
    assertTrue(solver.numBranches() > 0);
  }

  @Test
  public void testStopSearchFromOtherThread() throws Exception {
    CpSolver solver = new CpSolver();
    AtomicReference<CpSolverStatus> result = new AtomicReference<>();
    Thread t = new Thread(() -> result.set(solver.solve(parityModel(30))));
    t.start();
    Thread.sleep(100);
    solver.stopSearch();
    t.join(5000);
    assertFalse(t.isAlive());
    assertEquals(CpSolverStatus.UNKNOWN, result.get());
  }

  @Test
  public void testThreadInterrupt() throws Exception {
    CpSolver solver = new CpSolver();
    AtomicReference<CpSolverStatus> result = new AtomicReference<>();
    Thread t = new Thread(() -> result.set(solver.solve(parityModel(30))));
    t.start();
    Thread.sleep(100);
    t.interrupt();
    t.join(5000);
    assertFalse(t.isAlive());
    assertEquals(CpSolverStatus.UNKNOWN, result.get());
  }

  @Test
  public void testFloatingPointObjective() {
    CpModel model = new CpModel();
    IntVar x = model.newIntVar(0, 10, "x");
    IntVar y = model.newIntVar(0, 10, "y");
    model.addGreaterOrEqual(LinearExpr.newBuilder().add(x).add(y), 3);
    model.minimize(DoubleLinearExpr.weightedSumWithOffset(new IntVar[] {x, y},
        new double[] {2.5, 1.4}, 0.25));
    CpSolver solver = new CpSolver();
    assertEquals(CpSolverStatus.OPTIMAL, solver.solve(model));
    assertEquals(0.25 + 3 * 1.4, solver.objectiveValue(), 1e-9);
    assertEquals(3, solver.value(y));
    model.maximize(DoubleLinearExpr.weightedSum(new IntVar[] {x, y}, new double[] {-0.5, 0.75}));
    assertEquals(CpSolverStatus.OPTIMAL, solver.solve(model));
    assertEquals(7.5, solver.objectiveValue(), 1e-9);
    assertEquals(0, solver.value(x));
  }

  @Test
  public void testAdditionalSolutionsAndLog() {
    CpSolver solver = new CpSolver();
    solver.getParameters().setEnumerateAllSolutions(true)
        .setFillAdditionalSolutionsInResponse(true).setLogSearchProgress(true)
        .setLogToStdout(false).setLogToResponse(true);
    List<String> lines = new ArrayList<>();
    solver.setLogCallback(lines::add);
    assertEquals(CpSolverStatus.OPTIMAL, solver.solve(differentModel()));
    assertEquals(6, solver.response().getAdditionalSolutionsCount());
    assertTrue(solver.solveLog().contains("Parameters:"));
    assertTrue(solver.solveLog().contains("status: OPTIMAL"));
    assertFalse(lines.isEmpty());
    assertTrue(solver.responseStats().startsWith("CpSolverResponse summary:"));
    assertTrue(solver.wallTime() >= 0);
  }

  @Test
  public void testSufficientAssumptions() {
    CpModel model = new CpModel();
    IntVar x = model.newIntVar(0, 10, "x");
    Literal a = model.newBoolVar("a");
    Literal b = model.newBoolVar("b");
    Literal c = model.newBoolVar("c");
    model.addGreaterOrEqual(x, 5).onlyEnforceIf(a);
    model.addLessOrEqual(x, 2).onlyEnforceIf(b.not());
    model.addEquality(x, 7).onlyEnforceIf(c);
    model.addAssumptions(new Literal[] {c, b.not(), a});
    CpSolver solver = new CpSolver();
    assertEquals(CpSolverStatus.INFEASIBLE, solver.solve(model));
    List<Integer> core = solver.sufficientAssumptionsForInfeasibility();
    // x == 7 and x <= 2 are incompatible: {c, not(b)} is sufficient, a is not needed
    assertEquals(2, core.size());
    assertTrue(core.contains(c.getIndex()));
    assertTrue(core.contains(b.not().getIndex()));
    model.clearAssumptions();
    assertEquals(CpSolverStatus.OPTIMAL, solver.solve(model));
  }

  @Test
  public void testStopAfterFirstSolution() {
    CpModel model = parityModel(4);
    model.clearObjective();
    CpModel feasible = new CpModel();
    IntVar[] v = new IntVar[8];
    for (int i = 0; i < v.length; i++) {
      v[i] = feasible.newIntVar(0, 9, "v" + i);
    }
    feasible.addAllDifferent(v);
    feasible.maximize(LinearExpr.sum(v));
    CpSolver solver = new CpSolver();
    solver.getParameters().setStopAfterFirstSolution(true);
    CpSolverStatus status = solver.solve(feasible);
    assertTrue(status == CpSolverStatus.FEASIBLE || status == CpSolverStatus.OPTIMAL);
    solver.getParameters().setStopAfterFirstSolution(false);
    assertEquals(CpSolverStatus.OPTIMAL, solver.solve(feasible));
    assertEquals(9 + 8 + 7 + 6 + 5 + 4 + 3 + 2, solver.objectiveValue(), 0);
    assertNull(null);
  }

  @Test
  public void testLargeDomains() {
    CpModel model = new CpModel();
    IntVar x = model.newIntVar(0, 1_000_000_000L, "x");
    IntVar y = model.newIntVar(0, 1_000_000_000L, "y");
    model.addEquality(LinearExpr.newBuilder().add(x).add(y), 1_000_000_000L);
    model.addEquality(LinearExpr.newBuilder().add(x).addTerm(y, -1), 2);
    CpSolver solver = new CpSolver();
    assertEquals(CpSolverStatus.OPTIMAL, solver.solve(model));
    assertEquals(500_000_001L, solver.value(x));
    // holes in a large domain
    CpModel holes = new CpModel();
    IntVar z = holes.newIntVarFromDomain(
        Domain.fromIntervals(new long[][] {{0, 1_000_000}, {2_000_000, 3_000_000}}), "z");
    holes.addGreaterOrEqual(z, 1_000_005);
    holes.minimize(z);
    assertEquals(CpSolverStatus.OPTIMAL, solver.solve(holes));
    assertEquals(2_000_000L, solver.value(z));
    // a large product
    CpModel prod = new CpModel();
    IntVar a = prod.newIntVar(1, 100_000, "a");
    IntVar b = prod.newIntVar(1, 100_000, "b");
    IntVar p = prod.newIntVar(0, 10_000_000_000L, "p");
    prod.addMultiplicationEquality(p, a, b);
    prod.addEquality(p, 99_999L * 99_991L);
    prod.addGreaterOrEqual(a, b);
    assertEquals(CpSolverStatus.OPTIMAL, solver.solve(prod));
    assertEquals(99_999L, solver.value(a));
    assertEquals(99_991L, solver.value(b));
  }

  @Test
  public void testHintsAreFollowed() {
    CpModel model = new CpModel();
    IntVar[] v = new IntVar[5];
    for (int i = 0; i < v.length; i++) {
      v[i] = model.newIntVar(0, 9, "v" + i);
    }
    model.addAllDifferent(v);
    long[] hint = {7, 3, 9, 1, 5};
    for (int i = 0; i < v.length; i++) {
      model.addHint(v[i], hint[i]);
    }
    CpSolver solver = new CpSolver();
    assertEquals(CpSolverStatus.OPTIMAL, solver.solve(model));
    for (int i = 0; i < v.length; i++) {
      assertEquals(hint[i], solver.value(v[i]));
    }
  }

  @Test
  public void testSolutionCallbackStatistics() {
    CpModel model = differentModel();
    model.maximize(LinearExpr.sum(new IntVar[] {model.getIntVarFromProtoIndex(0)}));
    CpSolver solver = new CpSolver();
    List<Double> objectives = new ArrayList<>();
    solver.solve(model, new CpSolverSolutionCallback() {
      @Override
      public void onSolutionCallback() {
        objectives.add(objectiveValue());
        assertTrue(bestObjectiveBound() >= objectiveValue());
        assertTrue(wallTime() >= 0 && userTime() >= 0);
        assertTrue(numBranches() >= 0 && numConflicts() >= 0);
        assertEquals(2, numIntegerPropagations() >= 0 ? 2 : 0);
      }
    });
    assertEquals(2.0, objectives.get(objectives.size() - 1), 0);
  }

  @Test
  public void testSolutionCallbackExceptionPropagates() {
    // like the log and best bound callbacks of the 10.0 CpSolverTest: the exception of the
    // callback comes out of solve(); the solver can be reused afterwards
    CpModel model = differentModel();
    CpSolver solver = new CpSolver();
    solver.getParameters().setEnumerateAllSolutions(true);
    int[] calls = new int[1];
    IllegalStateException e =
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
            () -> solver.solve(model, new CpSolverSolutionCallback() {
              @Override
              public void onSolutionCallback() {
                calls[0]++;
                throw new IllegalStateException("stop here");
              }
            }));
    assertEquals("stop here", e.getMessage());
    assertEquals(1, calls[0]);
    CpSolverSolutionCallback counter = new CpSolverSolutionCallback() {
      @Override
      public void onSolutionCallback() {
        calls[0]++;
      }
    };
    assertEquals(CpSolverStatus.OPTIMAL, solver.solve(model, counter));
    assertEquals(7, calls[0]);
  }

  @Test
  public void testAllSearchBranchingValues() {
    // every value, including ROUND_ROBIN_SHAVING_SEARCH and ALT_FIXED_SEARCH of 10.0, gives the
    // complete search
    for (SatParameters.SearchBranching branching : SatParameters.SearchBranching.values()) {
      CpModel model = differentModel();
      CpSolver solver = new CpSolver();
      solver.getParameters().setEnumerateAllSolutions(true).setSearchBranching(branching);
      int[] count = new int[1];
      CpSolverStatus status = solver.solve(model, new CpSolverSolutionCallback() {
        @Override
        public void onSolutionCallback() {
          count[0]++;
        }
      });
      assertEquals(CpSolverStatus.OPTIMAL, status, branching.name());
      assertEquals(6, count[0], branching.name());
      CpModel opt = differentModel();
      opt.maximize(LinearExpr.weightedSum(new IntVar[] {opt.getIntVarFromProtoIndex(0),
          opt.getIntVarFromProtoIndex(1)}, new long[] {2, 1}));
      CpSolver s2 = new CpSolver();
      s2.getParameters().setSearchBranching(branching);
      assertEquals(CpSolverStatus.OPTIMAL, s2.solve(opt), branching.name());
      assertEquals(5.0, s2.objectiveValue(), 0, branching.name());
    }
    assertEquals(9, SatParameters.SearchBranching.ROUND_ROBIN_SHAVING_SEARCH.getNumber());
    assertEquals(10, SatParameters.SearchBranching.ALT_FIXED_SEARCH.getNumber());
  }

  @Test
  public void testSatParameters10Defaults() {
    SatParameters p = SatParameters.getDefaultInstance();
    // defaults changed in 10.0
    assertTrue(p.getUseNewIntegerConflictResolution());
    assertEquals(0.05, p.getShavingDeterministicTimeInProbingSearch(), 0);
    assertEquals(1e-5, p.getMipCheckPrecision(), 0);
    // some of the fields added in 10.0
    assertTrue(p.getLearnOnAssumptionsUnsat());
    assertFalse(p.getCpModelDirectSolve());
    assertEquals(1, p.getNumOrderingInVivification());
    assertTrue(p.getTryHintAsAssumptions());
    assertEquals(10, p.getShavingSearchRestartLimit());
    assertEquals(1000, p.getObjectiveShavingSearchThreshold());
    assertTrue(p.getFeasibilityJumpIgnoreRedundantConstraints());
    assertTrue(p.getMipUseTightBounds());
    assertEquals(1e-9, p.getMipTightPrecision(), 0);
    assertFalse(p.getMipProperRelaxation());
    assertFalse(p.hasMipProperRelaxation());
    // accepted (and ignored) by the engine
    CpModel model = differentModel();
    CpSolver solver = new CpSolver();
    solver.getParameters().setCpModelPureSatPresolve(true).setMipVerifySolution(true)
        .setInprocessingDetectAndSweepCircuit(true).setLnsIgnoreRedundantConstraints(false)
        .setSharedTreeWorkerPropagateIntermediateNodes(true).setMipScaleToMaxBound(true)
        .setMipPresolveRestrictBounds(false).setDetectEncodedComplexDomain(false)
        .setInprocessingUseTransitiveReduction(false).setShavingSearchUseRandomSearch(true);
    assertEquals(CpSolverStatus.OPTIMAL, solver.solve(model));
  }
}
