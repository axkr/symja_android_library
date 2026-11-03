package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.optimization.OptimizationMethod;

/**
 * <pre>
 * DualLinearProgramming(c, m, b)
 * DualLinearProgramming(c, m, b, bounds)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * gives a list <code>{x, y, z, w}</code>: <code>x</code> solves the primal problem
 * <code>LinearProgramming(c, m, b, bounds)</code>, and <code>y</code>, <code>z</code>,
 * <code>w</code> solve its dual problem: maximize <code>b.y + l.z - u.w</code> subject to
 * <code>y.m + z - w == c</code>. <code>y[[i]]</code> is non-negative for a row <code>&gt;=</code>,
 * non-positive for a row <code>&lt;=</code> and free for a row <code>==</code>;
 * <code>z[[j]]</code> and <code>w[[j]]</code> are non-negative when the variable has a finite lower
 * or upper bound and zero otherwise.
 *
 * </blockquote>
 *
 * <p>
 * The option <code>Method -&gt; "InteriorPoint"</code> gives machine numbers.
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; DualLinearProgramming({1, 1}, {{1, 2}, {3, 1}}, {3, 4})
 * {{1,1},{2/5,1/5},{0,0},{0,0}}
 * </pre>
 */
public class DualLinearProgramming extends AbstractFunctionEvaluator {

  private static final String INTERIOR_POINT = "InteriorPoint";

  /** The bounds of one variable; <code>null</code> stands for an infinite bound. */
  private static final class Bound {
    final IExpr lower;
    final IExpr upper;

    Bound(IExpr lower, IExpr upper) {
      this.lower = lower;
      this.upper = upper;
    }
  }

  /**
   * The bounds of <code>n</code> variables: one lower bound for all, a list of lower bounds or of
   * intervals <code>{l, u}</code>.
   *
   * @return <code>null</code> if <code>bounds</code> has none of these forms
   */
  private static Bound[] variableBounds(IExpr bounds, int n) {
    Bound[] result = new Bound[n];
    for (int j = 0; j < n; j++) {
      IExpr bound = bounds.isList() ? (bounds.argSize() == n ? bounds.getAt(j + 1) : F.NIL)
          : bounds;
      if (bound.isNIL()) {
        return null;
      }
      IExpr lower = bound.isList2() ? bound.first() : bound;
      IExpr upper = bound.isList2() ? bound.second() : F.CInfinity;
      if (lower.isList() || upper.isList()) {
        return null;
      }
      result[j] = new Bound(lower.isNegativeInfinity() ? null : lower,
          upper.isInfinity() ? null : upper);
    }
    return result;
  }

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    final IExpr method = OptimizationMethod.trailingMethod(ast, 3);
    if (method.isPresent()) {
      ast = ast.removeAtClone(ast.argSize());
    }
    ast = LinearProgramming.denseArguments(ast);
    if (ast.argSize() > 4 || !ast.arg1().isList() || !ast.arg2().isList() || !ast.arg3().isList()) {
      return F.NIL;
    }
    final IAST c = (IAST) ast.arg1();
    final IAST m = (IAST) ast.arg2();
    final IAST b = (IAST) ast.arg3();
    final int n = c.argSize();
    final int k = m.argSize();
    if (n == 0 || k == 0 || b.argSize() != k) {
      return F.NIL;
    }
    Bound[] bounds = variableBounds(ast.argSize() == 4 ? ast.arg4() : F.C0, n);
    if (bounds == null) {
      return F.NIL;
    }
    final IExpr option = OptimizationMethod.isORTools(method) ? F.Rule(S.Method, method) : F.NIL;
    IExpr primal = solve(ast.setAtCopy(0, S.LinearProgramming), option, engine);
    if (!primal.isList()) {
      return F.NIL;
    }

    IASTAppendable objective = F.ListAlloc(k + 2 * n);
    IASTAppendable dualBounds = F.ListAlloc(k + 2 * n);
    for (int i = 1; i <= k; i++) {
      IExpr spec = b.get(i);
      IExpr relation = F.C1;
      if (spec.isList2()) {
        relation = spec.second();
        spec = spec.first();
      }
      if (spec.isList() || !relation.isReal()) {
        return F.NIL;
      }
      objective.append(F.Negate(spec));
      dualBounds.append(relation.isZero() ? F.list(F.CNInfinity, F.CInfinity)
          : relation.isNegative() ? F.list(F.CNInfinity, F.C0) : F.list(F.C0, F.CInfinity));
    }
    for (Bound bound : bounds) {
      appendBoundVariable(bound.lower, false, objective, dualBounds);
    }
    for (Bound bound : bounds) {
      appendBoundVariable(bound.upper, true, objective, dualBounds);
    }
    // one equality row per primal variable: column j of m, then +z_j and -w_j
    IASTAppendable rows = F.ListAlloc(n);
    IASTAppendable rhs = F.ListAlloc(n);
    for (int j = 1; j <= n; j++) {
      IASTAppendable row = F.ListAlloc(k + 2 * n);
      for (int i = 1; i <= k; i++) {
        if (m.get(i).argSize() != n) {
          return F.NIL;
        }
        row.append(m.get(i).getAt(j));
      }
      for (int i = 1; i <= n; i++) {
        row.append(i == j ? F.C1 : F.C0);
      }
      for (int i = 1; i <= n; i++) {
        row.append(i == j ? F.CN1 : F.C0);
      }
      rows.append(row);
      rhs.append(F.list(c.get(j), F.C0));
    }
    IExpr dual = solve(
        F.quaternary(S.LinearProgramming, engine.evaluate(objective), rows, rhs, dualBounds),
        option, engine);
    if (!dual.isList() || dual.argSize() != k + 2 * n) {
      return F.NIL;
    }
    IAST values = (IAST) dual;
    IAST result = F.List(primal, values.copyFrom(1, k + 1), values.copyFrom(k + 1, k + n + 1),
        values.copyFrom(k + n + 1, k + 2 * n + 1));
    if (method.isString() && method.toString().equals(INTERIOR_POINT)) {
      if (!ast.isNumericMode() && c.isFree(x -> x.isInexactNumber(), false)
          && m.isFree(x -> x.isInexactNumber(), false)
          && b.isFree(x -> x.isInexactNumber(), false)) {
        // Warning: Method -> InteriorPoint specified for non-machine-precision problem.
        Errors.printMessage(S.DualLinearProgramming, "lpipp", F.CEmptyList, engine);
      }
      return engine.evaluate(F.N(result));
    }
    return result;
  }

  /** Solve the linear program <code>problem</code> with an additional <code>option</code>. */
  private static IExpr solve(IAST problem, IExpr option, EvalEngine engine) {
    return engine.evaluate(option.isPresent() ? problem.appendClone(option) : problem);
  }

  /**
   * Add the dual variable of one bound of a primal variable: non-negative for a finite bound,
   * else fixed at zero.
   *
   * @param bound the bound, <code>null</code> if it is infinite
   * @param upper <code>true</code> for an upper bound, which is subtracted in the objective
   */
  private static void appendBoundVariable(IExpr bound, boolean upper, IASTAppendable objective,
      IASTAppendable dualBounds) {
    if (bound == null) {
      objective.append(F.C0);
      dualBounds.append(F.list(F.C0, F.C0));
    } else {
      objective.append(upper ? bound : F.Negate(bound));
      dualBounds.append(F.list(F.C0, F.CInfinity));
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // a trailing Method -> ... rule is not counted
    return OptimizationMethod.trailingMethod(ast, 3).isPresent() ? ARGS_4_5 : ARGS_3_4;
  }

  private static final int[] ARGS_4_5 = new int[] {4, 5};

  private static final int[] ARGS_3_4 = new int[] {3, 4};
}
