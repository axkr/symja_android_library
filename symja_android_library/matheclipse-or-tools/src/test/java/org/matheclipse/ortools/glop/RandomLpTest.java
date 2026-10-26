package org.matheclipse.ortools.glop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.LpTypes;

/**
 * Random LPs with boxed variables solved by Glop with several parameter sets and compared with a
 * brute force vertex enumeration; optimal solutions are also checked with the KKT conditions
 * (primal feasibility, dual sign feasibility, complementary slackness, zero duality gap).
 */
public class RandomLpTest {

  /** A dense random LP. */
  static final class DenseLp {
    int n;
    int m;
    double[][] a;
    double[] rowLb;
    double[] rowUb;
    double[] colLb;
    double[] colUb;
    double[] c;
    boolean maximize;

    LinearProgram toLinearProgram() {
      LinearProgram lp = new LinearProgram();
      for (int j = 0; j < n; ++j) {
        lp.createNewVariable();
        lp.setVariableBounds(j, colLb[j], colUb[j]);
        lp.setObjectiveCoefficient(j, c[j]);
      }
      for (int i = 0; i < m; ++i) {
        lp.createNewConstraint();
        lp.setConstraintBounds(i, rowLb[i], rowUb[i]);
        for (int j = 0; j < n; ++j) {
          if (a[i][j] != 0.0) {
            lp.setCoefficient(i, j, a[i][j]);
          }
        }
      }
      lp.setMaximizationProblem(maximize);
      lp.cleanUp();
      return lp;
    }

    boolean feasible(double[] x, double tol) {
      for (int j = 0; j < n; ++j) {
        if (x[j] < colLb[j] - tol || x[j] > colUb[j] + tol) {
          return false;
        }
      }
      for (int i = 0; i < m; ++i) {
        double s = 0;
        for (int j = 0; j < n; ++j) {
          s += a[i][j] * x[j];
        }
        if (s < rowLb[i] - tol || s > rowUb[i] + tol) {
          return false;
        }
      }
      return true;
    }

    double objective(double[] x) {
      double s = 0;
      for (int j = 0; j < n; ++j) {
        s += c[j] * x[j];
      }
      return s;
    }
  }

  static DenseLp randomLp(Random r, int n, int m) {
    DenseLp p = new DenseLp();
    p.n = n;
    p.m = m;
    p.a = new double[m][n];
    p.rowLb = new double[m];
    p.rowUb = new double[m];
    p.colLb = new double[n];
    p.colUb = new double[n];
    p.c = new double[n];
    for (int j = 0; j < n; ++j) {
      double lo = r.nextInt(7) - 3;
      double hi = lo + r.nextInt(6);
      if (r.nextInt(8) == 0) {
        hi = lo;
      }
      p.colLb[j] = lo;
      p.colUb[j] = hi;
      p.c[j] = r.nextInt(11) - 5;
    }
    for (int i = 0; i < m; ++i) {
      for (int j = 0; j < n; ++j) {
        if (r.nextInt(3) != 0) {
          p.a[i][j] = r.nextInt(9) - 4;
        }
      }
      double lo = r.nextInt(21) - 10;
      int kind = r.nextInt(4);
      if (kind == 0) {
        p.rowLb[i] = -LpTypes.INFINITY;
        p.rowUb[i] = lo;
      } else if (kind == 1) {
        p.rowLb[i] = lo;
        p.rowUb[i] = LpTypes.INFINITY;
      } else if (kind == 2) {
        p.rowLb[i] = lo;
        p.rowUb[i] = lo;
      } else {
        p.rowLb[i] = lo;
        p.rowUb[i] = lo + r.nextInt(8);
      }
    }
    p.maximize = r.nextBoolean();
    return p;
  }

  /** Solves A x = b by Gaussian elimination, returns null if singular. */
  static double[] solveSquare(double[][] mat, double[] rhs) {
    final int n = rhs.length;
    double[][] a = new double[n][n + 1];
    for (int i = 0; i < n; ++i) {
      System.arraycopy(mat[i], 0, a[i], 0, n);
      a[i][n] = rhs[i];
    }
    for (int k = 0; k < n; ++k) {
      int p = k;
      for (int i = k + 1; i < n; ++i) {
        if (Math.abs(a[i][k]) > Math.abs(a[p][k])) {
          p = i;
        }
      }
      if (Math.abs(a[p][k]) < 1e-9) {
        return null;
      }
      double[] t = a[k];
      a[k] = a[p];
      a[p] = t;
      for (int i = 0; i < n; ++i) {
        if (i == k) {
          continue;
        }
        final double f = a[i][k] / a[k][k];
        for (int j = k; j <= n; ++j) {
          a[i][j] -= f * a[k][j];
        }
      }
    }
    double[] x = new double[n];
    for (int i = 0; i < n; ++i) {
      x[i] = a[i][n] / a[i][i];
    }
    return x;
  }

  /** Brute force over all vertices (bounded problem); returns NaN if infeasible. */
  static double bruteForce(DenseLp p) {
    // each candidate hyperplane: row i at lb/ub or column j at lb/ub
    final int numPlanes = 2 * p.m + 2 * p.n;
    double best = Double.NaN;
    // enumerate subsets of size n of the planes
    final int total = numPlanes;
    int[] idx = new int[p.n];
    for (int i = 0; i < p.n; ++i) {
      idx[i] = i;
    }
    while (true) {
      double[][] mat = new double[p.n][p.n];
      double[] rhs = new double[p.n];
      boolean ok = true;
      for (int k = 0; k < p.n && ok; ++k) {
        final int plane = idx[k];
        if (plane < 2 * p.m) {
          final int row = plane / 2;
          final double v = (plane % 2 == 0) ? p.rowLb[row] : p.rowUb[row];
          if (Double.isInfinite(v)) {
            ok = false;
            break;
          }
          mat[k] = p.a[row].clone();
          rhs[k] = v;
        } else {
          final int col = (plane - 2 * p.m) / 2;
          final double v = (plane % 2 == 0) ? p.colLb[col] : p.colUb[col];
          mat[k] = new double[p.n];
          mat[k][col] = 1.0;
          rhs[k] = v;
        }
      }
      if (ok) {
        double[] x = solveSquare(mat, rhs);
        if (x != null && p.feasible(x, 1e-7)) {
          final double obj = p.objective(x);
          if (Double.isNaN(best) || (p.maximize ? obj > best : obj < best)) {
            best = obj;
          }
        }
      }
      // next combination
      int k = p.n - 1;
      while (k >= 0 && idx[k] == total - p.n + k) {
        --k;
      }
      if (k < 0) {
        break;
      }
      ++idx[k];
      for (int l = k + 1; l < p.n; ++l) {
        idx[l] = idx[l - 1] + 1;
      }
    }
    return best;
  }

  static void checkKkt(DenseLp p, LPSolver solver) {
    final double[] x = solver.variableValues();
    final double[] y = solver.dualValues();
    final double[] rc = solver.reducedCosts();
    assertTrue(p.feasible(x, 1e-6), "primal infeasible");
    final double sign = p.maximize ? -1.0 : 1.0;
    // reduced costs consistent with duals
    for (int j = 0; j < p.n; ++j) {
      double d = p.c[j];
      for (int i = 0; i < p.m; ++i) {
        d -= p.a[i][j] * y[i];
      }
      assertEquals(d, rc[j], 1e-6, "reduced cost");
      final double mrc = sign * rc[j];
      if (mrc > 1e-6) {
        assertEquals(p.colLb[j], x[j], 1e-6, "complementary slackness (lb)");
      } else if (mrc < -1e-6) {
        assertEquals(p.colUb[j], x[j], 1e-6, "complementary slackness (ub)");
      }
    }
    for (int i = 0; i < p.m; ++i) {
      double act = 0;
      for (int j = 0; j < p.n; ++j) {
        act += p.a[i][j] * x[j];
      }
      final double my = sign * y[i];
      if (my > 1e-6) {
        assertEquals(p.rowLb[i], act, 1e-6, "dual sign / slackness (row lb)");
      } else if (my < -1e-6) {
        assertEquals(p.rowUb[i], act, 1e-6, "dual sign / slackness (row ub)");
      }
    }
  }

  private static void runRandom(int seed, int count, int maxN, int maxM) {
    Random r = new Random(seed);
    GlopParameters[] params = {new GlopParameters(), new GlopParameters().setUseDualSimplex(true),
        new GlopParameters().setUsePreprocessing(false).setUseScaling(false),
        new GlopParameters().setUseDualSimplex(true).setUsePreprocessing(false),
        new GlopParameters().setFeasibilityRule(GlopParameters.PricingRule.DANTZIG)
            .setOptimizationRule(GlopParameters.PricingRule.DEVEX),
        new GlopParameters().setUseDualSimplex(true).setPerturbCostsInDualSimplex(true),
        new GlopParameters().setInitialBasis(GlopParameters.InitialBasisHeuristic.MAROS),
        new GlopParameters().setInitialBasis(GlopParameters.InitialBasisHeuristic.BIXBY)};
    for (int t = 0; t < count; ++t) {
      final int n = 1 + r.nextInt(maxN);
      final int m = 1 + r.nextInt(maxM);
      DenseLp p = randomLp(r, n, m);
      final double expected = bruteForce(p);
      for (int k = 0; k < params.length; ++k) {
        LPSolver solver = new LPSolver();
        solver.setParameters(params[k]);
        final ProblemStatus status = solver.solve(p.toLinearProgram());
        final String where = "seed " + seed + " problem " + t + " params " + k;
        if (Double.isNaN(expected)) {
          if (status != ProblemStatus.PRIMAL_INFEASIBLE && status != ProblemStatus.DUAL_UNBOUNDED
              && status != ProblemStatus.INFEASIBLE_OR_UNBOUNDED) {
            fail(where + ": expected infeasible, got " + status + " obj "
                + solver.getObjectiveValue());
          }
        } else {
          assertEquals(ProblemStatus.OPTIMAL, status, where);
          assertEquals(expected, solver.getObjectiveValue(), 1e-6, where);
          checkKkt(p, solver);
        }
      }
    }
  }

  @Test
  public void testSmallRandomLps() {
    runRandom(1, 60, 3, 3);
  }

  /**
   * Larger random LPs whose rows contain a random point of the box (so they are feasible and
   * bounded), too large for the vertex enumeration: every parameter set must reach the same
   * objective with a solution satisfying the KKT conditions.
   */
  @Test
  public void testLargerFeasibleLps() {
    final Random r = new Random(7);
    GlopParameters[] params = {new GlopParameters(), new GlopParameters().setUseDualSimplex(true),
        new GlopParameters().setUsePreprocessing(false),
        new GlopParameters().setUsePreprocessing(false).setUseDualSimplex(true)};
    for (int t = 0; t < 30; ++t) {
      final int n = 20 + r.nextInt(30);
      final int m = 10 + r.nextInt(30);
      final DenseLp p = randomLp(r, n, m);
      final double[] point = new double[n];
      for (int j = 0; j < n; ++j) {
        point[j] = p.colLb[j] + r.nextDouble() * (p.colUb[j] - p.colLb[j]);
      }
      for (int i = 0; i < m; ++i) {
        double activity = 0;
        for (int j = 0; j < n; ++j) {
          activity += p.a[i][j] * point[j];
        }
        if (p.rowLb[i] > activity) {
          p.rowLb[i] = Math.floor(activity);
        }
        if (p.rowUb[i] < activity) {
          p.rowUb[i] = Math.ceil(activity);
        }
      }
      double objective = Double.NaN;
      for (int k = 0; k < params.length; ++k) {
        LPSolver solver = new LPSolver();
        solver.setParameters(params[k]);
        final String where = "problem " + t + " params " + k;
        assertEquals(ProblemStatus.OPTIMAL, solver.solve(p.toLinearProgram()), where);
        checkKkt(p, solver);
        if (k == 0) {
          objective = solver.getObjectiveValue();
        } else {
          assertEquals(objective, solver.getObjectiveValue(),
              1e-6 * Math.max(1.0, Math.abs(objective)), where);
        }
      }
    }
  }

  @Test
  public void testMoreRandomLps() {
    runRandom(2, 400, 4, 5);
  }
}
