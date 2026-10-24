package org.matheclipse.ortools.modelbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.linearsolver.MPConstraint;
import org.matheclipse.ortools.linearsolver.MPSolver;
import org.matheclipse.ortools.linearsolver.MPVariable;

/**
 * Tests of {@link ModelSolver} with the Java backends: statuses, enforced constraints, the
 * solver names, and the LP and MPS export and import round trips.
 */
public class ModelSolverTest {

  private static final double INF = Double.POSITIVE_INFINITY;

  @Test
  public void testSolverNames() {
    for (String name : new String[] {"glop", "GLOP", "clp", "pdlp", "scip", "cbc", "sat",
        "highs", "gurobi", "cplex", "xpress"}) {
      assertTrue(new ModelSolver(name).solverIsSupported(), name);
    }
    assertFalse(new ModelSolver("no such solver").solverIsSupported());
    final ModelBuilder model = new ModelBuilder();
    model.newNumVar(0, 1, "x");
    assertEquals(SolveStatus.SOLVER_TYPE_UNAVAILABLE,
        new ModelSolver("no such solver").solve(model));
  }

  @Test
  public void testLpStatuses() {
    // infeasible
    ModelBuilder model = new ModelBuilder();
    Variable x = model.newNumVar(0, 10, "x");
    model.addGreaterOrEqual(x, 11);
    ModelSolver solver = new ModelSolver("glop");
    assertEquals(SolveStatus.INFEASIBLE, solver.solve(model));
    assertTrue(solver.hasResponse());
    assertFalse(solver.hasSolution());

    // unbounded
    model = new ModelBuilder();
    x = model.newNumVar(0, INF, "x");
    Variable y = model.newNumVar(0, INF, "y");
    model.addLessOrEqual(LinearExpr.newBuilder().add(x).addTerm(y, -1), 1);
    model.maximize(LinearExpr.newBuilder().add(x).add(y));
    // the presolve only proves "infeasible or unbounded", which is reported as INFEASIBLE (as in
    // C++); the simplex without presolve finds the unbounded ray
    assertEquals(SolveStatus.INFEASIBLE, new ModelSolver("glop").solve(model));
    solver = new ModelSolver("glop");
    solver.setSolverSpecificParameters("use_preprocessing: false");
    assertEquals(SolveStatus.UNBOUNDED, solver.solve(model));

    // invalid objective offset
    model = new ModelBuilder();
    model.newNumVar(0, 1, "x");
    model.setObjectiveOffset(Double.NaN);
    assertEquals(SolveStatus.MODEL_INVALID, new ModelSolver("glop").solve(model));

    // a NaN coefficient also makes the bound NaN (offset 0 times NaN): invalid bounds are
    // reported as infeasible, as in C++
    model = new ModelBuilder();
    x = model.newNumVar(0, 1, "x");
    model.addLessOrEqual(LinearExpr.term(x, Double.NaN), 1);
    assertEquals(SolveStatus.INFEASIBLE, new ModelSolver("glop").solve(model));

    // infeasible variable bounds
    model = new ModelBuilder();
    model.newNumVar(2, 1, "x");
    assertEquals(SolveStatus.INFEASIBLE, new ModelSolver("glop").solve(model));

    // enforced constraints need a MIP solver
    model = new ModelBuilder();
    x = model.newNumVar(0, 1, "x");
    Variable z = model.newBoolVar("z");
    model.addEnforcedLessOrEqual(x, 0.5, z, true);
    assertEquals(SolveStatus.MODEL_INVALID, new ModelSolver("glop").solve(model));

    // invalid parameters
    model = new ModelBuilder();
    model.newNumVar(0, 1, "x");
    solver = new ModelSolver("glop");
    solver.setSolverSpecificParameters("no_such_parameter: 3");
    assertEquals(SolveStatus.INVALID_SOLVER_PARAMETERS, solver.solve(model));
  }

  @Test
  public void testEmptyModel() {
    final ModelBuilder model = new ModelBuilder();
    model.minimize(LinearExpr.constant(4.5));
    final ModelSolver solver = new ModelSolver("scip");
    assertEquals(SolveStatus.OPTIMAL, solver.solve(model));
    assertEquals(4.5, solver.getObjectiveValue(), 0.0);
  }

  @Test
  public void testParametersAndLog() {
    final ModelBuilder model = new ModelBuilder();
    final Variable x = model.newIntVar(0, 10, "x");
    final Variable y = model.newIntVar(0, 10, "y");
    model.addLessOrEqual(LinearExpr.newBuilder().addTerm(x, 2).addTerm(y, 3), 12.5);
    model.maximize(LinearExpr.newBuilder().addTerm(x, 3).addTerm(y, 4));
    final ModelSolver solver = new ModelSolver("cbc");
    final List<String> log = new ArrayList<>();
    solver.setLogCallback(log::add);
    solver.enableOutput(true);
    solver.setTimeLimit(Duration.ofSeconds(10));
    solver.setSolverSpecificParameters("max_nodes: 1000, relative_mip_gap: 0");
    assertEquals(SolveStatus.OPTIMAL, solver.solve(model));
    // 2x + 3y <= 12 (integer activity): x = 6, y = 0 gives 18; x = 3, y = 2 gives 17
    double best = 0;
    for (int a = 0; a <= 10; ++a) {
      for (int b = 0; b <= 10; ++b) {
        if (2 * a + 3 * b <= 12.5) {
          best = Math.max(best, 3 * a + 4 * b);
        }
      }
    }
    assertEquals(best, solver.getObjectiveValue(), 1e-9);
    assertEquals(best, solver.getBestObjectiveBound(), 1e-9);
    assertFalse(log.isEmpty());
    assertTrue(solver.getWallTime() >= 0.0);
  }

  @Test
  public void testRandomEnforcedConstraints() {
    // z_k => sum a_kj x_j <= b_k (or >=, by the sign of the indicator value), x in [0, 3]^2
    final Random r = new Random(8);
    for (int t = 0; t < 60; ++t) {
      final ModelBuilder model = new ModelBuilder();
      final Variable[] x = {model.newIntVar(0, 3, "x0"), model.newIntVar(0, 3, "x1")};
      final Variable[] z = {model.newBoolVar("z0"), model.newBoolVar("z1")};
      final int[][] a = new int[2][2];
      final int[] b = new int[2];
      final boolean[] value = new boolean[2];
      final boolean[] less = new boolean[2];
      for (int k = 0; k < 2; ++k) {
        a[k][0] = r.nextInt(7) - 3;
        a[k][1] = r.nextInt(7) - 3;
        b[k] = r.nextInt(9) - 4;
        value[k] = r.nextBoolean();
        less[k] = r.nextBoolean();
        final LinearExpr expr = LinearExpr.newBuilder().addTerm(x[0], a[k][0])
            .addTerm(x[1], a[k][1]).build();
        if (less[k]) {
          model.addEnforcedLessOrEqual(expr, b[k], z[k], value[k]);
        } else {
          model.addEnforcedGreaterOrEqual(expr, b[k], z[k], value[k]);
        }
      }
      // at least one indicator is active, and a random objective
      model.addGreaterOrEqual(LinearExpr.newBuilder().add(z[0]).add(z[1]), 1);
      final int[] c = {r.nextInt(7) - 3, r.nextInt(7) - 3, r.nextInt(5) - 2, r.nextInt(5) - 2};
      model.maximize(LinearExpr.weightedSum(new Variable[] {x[0], x[1], z[0], z[1]},
          new double[] {c[0], c[1], c[2], c[3]}));
      double best = Double.NaN;
      for (int x0 = 0; x0 <= 3; ++x0) {
        for (int x1 = 0; x1 <= 3; ++x1) {
          for (int z0 = 0; z0 <= 1; ++z0) {
            for (int z1 = 0; z1 <= 1; ++z1) {
              if (z0 + z1 < 1) {
                continue;
              }
              final int[] zz = {z0, z1};
              boolean ok = true;
              for (int k = 0; k < 2; ++k) {
                if ((zz[k] == 1) == value[k]) {
                  final int s = a[k][0] * x0 + a[k][1] * x1;
                  ok &= less[k] ? s <= b[k] : s >= b[k];
                }
              }
              if (ok) {
                final double v = c[0] * x0 + c[1] * x1 + c[2] * z0 + c[3] * z1;
                if (Double.isNaN(best) || v > best) {
                  best = v;
                }
              }
            }
          }
        }
      }
      final ModelSolver solver = new ModelSolver("scip");
      final SolveStatus status = solver.solve(model);
      if (Double.isNaN(best)) {
        assertEquals(SolveStatus.INFEASIBLE, status, "problem " + t);
      } else {
        assertEquals(SolveStatus.OPTIMAL, status, "problem " + t);
        assertEquals(best, solver.getObjectiveValue(), 1e-9, "problem " + t);
      }
    }
  }

  /** A small mixed integer program with ranged rows, a free variable and an objective offset. */
  private static MPSolver buildMpSolverModel(String type) {
    final MPSolver solver = MPSolver.createSolver(type);
    final MPVariable x = solver.makeIntVar(-3, 7, "x");
    final MPVariable y = solver.makeNumVar(-INF, INF, "y");
    final MPVariable z = solver.makeBoolVar("z");
    final MPVariable w = solver.makeNumVar(0.5, 4.25, "w");
    MPConstraint c = solver.makeConstraint(-2, 5.5, "range");
    c.setCoefficient(x, 1);
    c.setCoefficient(y, -1);
    c.setCoefficient(w, 0.5);
    c = solver.makeConstraint(1, 1, "eq");
    c.setCoefficient(y, 2);
    c.setCoefficient(z, -3);
    c.setCoefficient(w, 1);
    c = solver.makeConstraint(-INF, 9, "le");
    c.setCoefficient(x, 2);
    c.setCoefficient(w, 1);
    solver.objective().setCoefficient(x, 1.5);
    solver.objective().setCoefficient(y, -1);
    solver.objective().setCoefficient(z, 2);
    solver.objective().setCoefficient(w, 0.25);
    solver.objective().setOffset(3.75);
    solver.objective().setMaximization();
    return solver;
  }

  @Test
  public void testExportImportRoundTrip() {
    final MPSolver reference = buildMpSolverModel("SCIP");
    assertEquals(MPSolver.ResultStatus.OPTIMAL, reference.solve());
    final double expected = reference.objective().value();

    // the exporter writes the CPLEX LP format while the importer reads the lp_solve format (as in
    // C++), so the LP format is checked with the same model written by hand
    final String lp = "max: 3.75 + 1.5 x - y + 2 z + 0.25 w;\n"
        + "range: -2 <= x - y + 0.5 w <= 5.5;\n"
        + "eq: 2 y - 3 z + w = 1;\n"
        + "le: 2 x + w <= 9;\n"
        + "-3 <= x <= 7;\n"
        + "y >= -1e30;\n"
        + "0.5 <= w <= 4.25;\n"
        + "int x;\n"
        + "bin z;\n";
    final ModelBuilder fromLp = new ModelBuilder();
    assertTrue(fromLp.importFromLpString(lp), lp);
    ModelSolver solver = new ModelSolver("scip");
    assertEquals(SolveStatus.OPTIMAL, solver.solve(fromLp));
    assertEquals(expected, solver.getObjectiveValue(), 1e-6);
    final String exported = buildMpSolverModel("SCIP").exportModelAsLpFormat(false);
    assertTrue(exported.contains(" range_rhs: +1 x -1 y +0.5 w  <= 5.5"), exported);
    assertTrue(exported.contains(" y free"), exported);

    for (boolean fixed : new boolean[] {false, true}) {
      final String mps = buildMpSolverModel("SCIP").exportModelAsMpsFormat(fixed, false);
      final ModelBuilder fromMps = new ModelBuilder();
      assertTrue(fromMps.importFromMpsString(mps), mps);
      solver = new ModelSolver("scip");
      assertEquals(SolveStatus.OPTIMAL, solver.solve(fromMps));
      assertEquals(expected, solver.getObjectiveValue(), 1e-6);
      // and once more through the model builder exporter
      final ModelBuilder again = new ModelBuilder();
      assertTrue(again.importFromMpsString(fromMps.exportToMpsString(false)));
      solver = new ModelSolver("scip");
      assertEquals(SolveStatus.OPTIMAL, solver.solve(again));
      assertEquals(expected, solver.getObjectiveValue(), 1e-6);
    }

    // the LP relaxation through Glop
    final MPSolver relaxed = buildMpSolverModel("GLOP");
    assertEquals(MPSolver.ResultStatus.OPTIMAL, relaxed.solve());
    final ModelBuilder relaxedModel = new ModelBuilder();
    assertTrue(relaxedModel.importFromMpsString(
        buildMpSolverModel("SCIP").exportModelAsMpsFormat(false, false)));
    solver = new ModelSolver("glop");
    assertEquals(SolveStatus.OPTIMAL, solver.solve(relaxedModel));
    assertEquals(relaxed.objective().value(), solver.getObjectiveValue(), 1e-6);
  }
}
