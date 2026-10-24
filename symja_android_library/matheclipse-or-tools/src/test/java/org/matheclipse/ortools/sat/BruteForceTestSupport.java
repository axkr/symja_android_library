package org.matheclipse.ortools.sat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.matheclipse.ortools.util.Domain;

/**
 * Cross-checks the engine against brute force enumeration: for a small model, the set of
 * solutions enumerated by the solver must be exactly the set of assignments accepted by an
 * independent checker written in the test, and the optimum of a random objective must match.
 */
final class BruteForceTestSupport {
  private BruteForceTestSupport() {}

  /** An independent definition of the feasible assignments of a model. */
  interface Checker {
    boolean ok(long[] v);
  }

  /** A random affine expression {@code a * x + b} with its evaluation. */
  static final class Aff {
    final IntVar var;
    final long a;
    final long b;
    final LinearArgument expr;

    Aff(IntVar var, long a, long b) {
      this.var = var;
      this.a = a;
      this.b = b;
      this.expr = var == null ? LinearExpr.constant(b) : LinearExpr.affine(var, a, b);
    }

    long eval(long[] v) {
      return var == null ? b : a * v[var.getIndex()] + b;
    }
  }

  static Aff randomAff(Random r, IntVar[] vars, boolean allowConstant) {
    if (allowConstant && r.nextInt(6) == 0) {
      return new Aff(null, 0, r.nextInt(7) - 3);
    }
    long[] coeffs = {-2, -1, 1, 1, 1, 2, 3};
    return new Aff(vars[r.nextInt(vars.length)], coeffs[r.nextInt(coeffs.length)],
        r.nextInt(5) - 2);
  }

  /** A random domain inside [lo, hi], possibly with holes. */
  static Domain randomDomain(Random r, int lo, int hi) {
    if (r.nextInt(3) > 0) {
      int a = lo + r.nextInt(hi - lo + 1);
      int b = lo + r.nextInt(hi - lo + 1);
      return new Domain(Math.min(a, b), Math.max(a, b));
    }
    List<Long> values = new ArrayList<>();
    for (int x = lo; x <= hi; x++) {
      if (r.nextInt(3) > 0) {
        values.add((long) x);
      }
    }
    if (values.isEmpty()) {
      values.add((long) lo);
    }
    long[] vs = new long[values.size()];
    for (int i = 0; i < vs.length; i++) {
      vs[i] = values.get(i);
    }
    return Domain.fromValues(vs);
  }

  /** Optionally adds an enforcement literal; returns the wrapped checker. */
  static Checker maybeEnforce(Random r, CpModel model, Constraint ct, Checker inner) {
    if (r.nextInt(3) != 0) {
      return inner;
    }
    BoolVar b = model.newBoolVar("enf");
    Checker one;
    if (r.nextBoolean()) {
      ct.onlyEnforceIf(b);
      one = v -> v[b.getIndex()] == 0 || inner.ok(v);
    } else {
      ct.onlyEnforceIf(b.not());
      one = v -> v[b.getIndex()] == 1 || inner.ok(v);
    }
    if (r.nextInt(3) != 0) {
      return one;
    }
    // a second enforcement literal: the constraint holds if both are true
    BoolVar c = model.newBoolVar("enf2");
    ct.onlyEnforceIf(c);
    return v -> v[c.getIndex()] == 0 || one.ok(v);
  }

  /** Number of assignments of all model variables. */
  static long searchSpace(CpModel model) {
    long n = 1;
    for (IntegerVariableProto v : model.model().getVariablesList()) {
      n *= CpSatHelper.variableDomain(v).size();
      if (n > 10_000_000L) {
        return n;
      }
    }
    return n;
  }

  private static String key(long[] v) {
    return Arrays.toString(v);
  }

  /**
   * Verifies the solution set and the optimum of a random objective of the model. Returns the
   * number of solutions, or -1 if the model is too large for the brute force.
   */
  static long verify(CpModel model, Checker checker, Random r) {
    if (searchSpace(model) > 200_000) {
      return -1; // too large for the brute force
    }
    CpModelProto proto = model.model();
    String invalid = model.validate();
    assertTrue(invalid.isEmpty(), invalid);
    int n = proto.getVariablesCount();
    Domain[] domains = new Domain[n];
    long[][] values = new long[n][];
    for (int i = 0; i < n; i++) {
      domains[i] = CpSatHelper.variableDomain(proto.getVariables(i));
      List<Long> vs = new ArrayList<>();
      for (int k = 0; k < domains[i].numIntervals(); k++) {
        for (long x = domains[i].intervalStart(k); x <= domains[i].intervalEnd(k); x++) {
          vs.add(x);
        }
      }
      values[i] = new long[vs.size()];
      for (int k = 0; k < vs.size(); k++) {
        values[i][k] = vs.get(k);
      }
    }
    // random objective
    long[] objCoeffs = new long[n];
    for (int i = 0; i < n; i++) {
      objCoeffs[i] = r.nextInt(7) - 3;
    }
    // brute force
    Set<String> expected = new HashSet<>();
    long bestObj = Long.MAX_VALUE;
    int[] idx = new int[n];
    long[] cur = new long[n];
    boolean done = n == 0;
    if (n == 0) {
      if (checker.ok(cur)) {
        expected.add(key(cur));
        bestObj = 0;
      }
    }
    for (int i = 0; i < n; i++) {
      if (values[i].length == 0) {
        done = true;
      }
    }
    while (!done) {
      for (int i = 0; i < n; i++) {
        cur[i] = values[i][idx[i]];
      }
      if (checker.ok(cur)) {
        expected.add(key(cur));
        long obj = 0;
        for (int i = 0; i < n; i++) {
          obj += objCoeffs[i] * cur[i];
        }
        bestObj = Math.min(bestObj, obj);
      }
      int k = 0;
      while (k < n) {
        idx[k]++;
        if (idx[k] < values[k].length) {
          break;
        }
        idx[k] = 0;
        k++;
      }
      if (k == n) {
        done = true;
      }
    }
    // enumeration by the solver, with a random search strategy and random hints
    CpModel enumModel = model.getClone();
    if (n > 0 && r.nextBoolean()) {
      List<LinearArgument> exprs = new ArrayList<>();
      for (int i = 0; i < n; i++) {
        if (r.nextBoolean()) {
          IntVar var = enumModel.getIntVarFromProtoIndex(i);
          exprs.add(r.nextBoolean() ? var : LinearExpr.affine(var, -1, 3));
        }
      }
      DecisionStrategyProto.VariableSelectionStrategy[] vs =
          DecisionStrategyProto.VariableSelectionStrategy.values();
      DecisionStrategyProto.DomainReductionStrategy[] ds =
          DecisionStrategyProto.DomainReductionStrategy.values();
      enumModel.addDecisionStrategy(exprs, vs[r.nextInt(vs.length - 1)],
          ds[r.nextInt(ds.length - 1)]);
    }
    if (n > 0 && r.nextBoolean()) {
      for (int i = 0; i < n; i++) {
        if (r.nextBoolean()) {
          enumModel.addHint(enumModel.getIntVarFromProtoIndex(i), values[i].length == 0 ? 0
              : values[i][r.nextInt(values[i].length)]);
        }
      }
    }
    CpSolver solver = new CpSolver();
    solver.getParameters().setEnumerateAllSolutions(true);
    if (r.nextBoolean()) {
      solver.getParameters().setSearchBranching(SatParameters.SearchBranching.FIXED_SEARCH);
    }
    Set<String> found = new HashSet<>();
    List<String> duplicates = new ArrayList<>();
    CpSolverStatus status = solver.solve(enumModel, new CpSolverSolutionCallback() {
      @Override
      public void onSolutionCallback() {
        long[] v = new long[n];
        for (int i = 0; i < n; i++) {
          v[i] = solutionIntegerValue(i);
        }
        if (!found.add(key(v))) {
          duplicates.add(key(v));
        }
      }
    });
    assertTrue(duplicates.isEmpty(), () -> "duplicate solutions " + duplicates + "\n" + proto);
    if (!expected.equals(found)) {
      Set<String> missing = new HashSet<>(expected);
      missing.removeAll(found);
      Set<String> extra = new HashSet<>(found);
      extra.removeAll(expected);
      fail("solution sets differ; " + missing.size() + " missing, e.g. "
          + missing.stream().limit(3).collect(java.util.stream.Collectors.toList()) + "; "
          + extra.size() + " extra, e.g. "
          + extra.stream().limit(3).collect(java.util.stream.Collectors.toList()) + "\n"
          + proto);
    }
    assertEquals(expected.isEmpty() ? CpSolverStatus.INFEASIBLE : CpSolverStatus.OPTIMAL, status,
        () -> proto.toString());
    // optimization
    CpModel copy = model.getClone();
    LinearExprBuilder obj = LinearExpr.newBuilder();
    for (int i = 0; i < n; i++) {
      obj.addTerm(copy.getIntVarFromProtoIndex(i), objCoeffs[i]);
    }
    copy.minimize(obj);
    CpSolver opt = new CpSolver();
    CpSolverStatus optStatus = opt.solve(copy);
    if (expected.isEmpty()) {
      assertEquals(CpSolverStatus.INFEASIBLE, optStatus, () -> proto.toString());
    } else {
      assertEquals(CpSolverStatus.OPTIMAL, optStatus, () -> proto.toString());
      assertEquals((double) bestObj, opt.objectiveValue(), 1e-9, () -> proto.toString());
      long[] v = new long[n];
      for (int i = 0; i < n; i++) {
        v[i] = opt.response().getSolution(i);
      }
      assertTrue(checker.ok(v), () -> "optimal solution violates the model\n" + proto);
      assertEquals((double) bestObj, opt.bestObjectiveBound(), 1e-9);
    }
    return expected.size();
  }
}
