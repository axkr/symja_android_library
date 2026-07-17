package org.matheclipse.core.integrate;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Derivative-divides (Geddes) heuristic for indefinite integration.
 *
 * <p>
 * Tries to write the integrand as <code>c * f(u(x)) * u'(x)</code> for some inner function
 * <code>u(x)</code> taken from the subexpressions of the integrand. If
 * <code>integrand / u'(x)</code> can be completely rewritten in terms of <code>u</code> (i.e. it
 * is free of <code>x</code> after substituting <code>u -&gt; t</code>), the integral reduces to
 * <code>c * Integrate(f(t), t)</code> with the back-substitution <code>t -&gt; u(x)</code>.
 *
 * <p>
 * See K.O. Geddes, S.R. Czapor, G. Labahn: "Algorithms for Computer Algebra", section "The
 * derivative-divides method".
 */
public class DerivativeDivides {

  /** Recursion depth guard for nested derivative-divides applications. */
  private static final ThreadLocal<Integer> RECURSION_DEPTH = ThreadLocal.withInitial(() -> 0);

  private DerivativeDivides() {}

  /**
   * Try the derivative-divides heuristic.
   *
   * @param integrand the integrand
   * @param x the integration variable
   * @param engine the evaluation engine
   * @return the antiderivative or {@link F#NIL}
   */
  public static IExpr integrate(IExpr integrand, IExpr x, EvalEngine engine) {
    if (!Config.INTEGRATE_ALGORITHM_DERIVATIVE_DIVIDES) {
      return F.NIL;
    }
    int depth = RECURSION_DEPTH.get();
    if (depth >= Config.INTEGRATE_DERIVATIVE_DIVIDES_RECURSION_LIMIT) {
      return F.NIL;
    }
    RECURSION_DEPTH.set(depth + 1);
    try {
      return integrateRecursive(integrand, x, engine);
    } finally {
      RECURSION_DEPTH.set(depth);
    }
  }

  private static IExpr integrateRecursive(IExpr integrand, IExpr x, EvalEngine engine) {
    Set<IExpr> candidates = candidateInnerFunctions(integrand, x);
    if (candidates.isEmpty()) {
      return F.NIL;
    }
    ISymbol t = F.Dummy("dd$t");
    for (IExpr u : candidates) {
      IExpr du = engine.evaluateNIL(F.D(u, x));
      if (du.isNIL() || du.isZero() || !du.isFree(S.D)) {
        continue;
      }
      // candidate g = integrand / u'(x)
      IExpr g;
      try {
        g = engine.evaluate(F.Cancel(F.Together(F.Divide(integrand, du))));
      } catch (RuntimeException rex) {
        org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
        continue;
      }
      // rewrite g in terms of t == u(x)
      IExpr gOfT = F.subst(g, u, t);
      if (!gOfT.isFree(x, true)) {
        // try harder: simplify powers of u first
        gOfT = F.subst(engine.evaluate(F.PowerExpand(g)), u, t);
        if (!gOfT.isFree(x, true)) {
          continue;
        }
      }
      if (gOfT.equals(g) && u.equals(x)) {
        continue; // no progress
      }
      // inner integral Integrate(g(t), t)
      IExpr inner;
      try {
        inner = engine.evaluateNIL(F.Integrate(gOfT, t));
      } catch (RuntimeException rex) {
        org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
        continue;
      }
      if (inner.isNIL() || !inner.isFreeAST(S.Integrate) || !inner.isSpecialsFree()) {
        continue;
      }
      // back-substitute t -> u(x)
      IExpr result = engine.evaluate(F.subst(inner, t, u));
      if (verifyAntiderivative(result, integrand, x, engine)) {
        return result;
      }
      // spurious / wrong-branch candidate: try the next inner function
    }
    return F.NIL;
  }

  /**
   * Diff-back self-verification: {@code true} iff {@code D(result, x)} equals the integrand. Guards
   * against wrong-branch or spurious substitutions so the stage never short-circuits the Rubi rules
   * with an incorrect antiderivative.
   */
  private static boolean verifyAntiderivative(IExpr result, IExpr integrand, IExpr x,
      EvalEngine engine) {
    if (result.isNIL() || !result.isFreeAST(S.Integrate) || !result.isSpecialsFree()) {
      return false;
    }
    try {
      IExpr diff = engine.evaluate(F.Together(F.Subtract(F.D(result, x), integrand)));
      if (diff.isZero()) {
        return true;
      }
      return engine.evaluate(F.Simplify(diff)).isZero();
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
      return false;
    }
  }

  /**
   * Collect candidate inner functions <code>u(x)</code> from the subexpressions of the integrand.
   * Candidates are arguments of unary functions, bases and exponents of powers and other composite
   * subexpressions depending on <code>x</code>. The candidates are ordered by decreasing leaf
   * count, so that "bigger" inner functions are tried first.
   */
  private static Set<IExpr> candidateInnerFunctions(IExpr integrand, IExpr x) {
    Set<IExpr> collector = new LinkedHashSet<>();
    collectCandidates(integrand, x, collector, 0);
    collector.remove(integrand);
    collector.remove(x);
    Set<IExpr> sorted = new TreeSet<>(Comparator.comparingLong(IExpr::leafCount).reversed()
        .thenComparing(Comparator.naturalOrder()));
    sorted.addAll(collector);
    if (sorted.size() > 16) {
      Set<IExpr> limited = new LinkedHashSet<>();
      int i = 0;
      for (IExpr e : sorted) {
        if (i++ >= 16) {
          break;
        }
        limited.add(e);
      }
      return limited;
    }
    return sorted;
  }

  private static void collectCandidates(IExpr expr, IExpr x, Set<IExpr> collector, int level) {
    if (level > 6 || expr.isFree(x, true) || expr.equals(x)) {
      return;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      if (ast.isPower()) {
        IExpr base = ast.base();
        IExpr exponent = ast.exponent();
        if (!base.isFree(x, true)) {
          collector.add(base);
          collectCandidates(base, x, collector, level + 1);
        }
        if (!exponent.isFree(x, true)) {
          collector.add(exponent);
          collectCandidates(exponent, x, collector, level + 1);
        }
        collector.add(ast);
      } else if (ast.isTimes() || ast.isPlus()) {
        for (int i = 1; i < ast.size(); i++) {
          collectCandidates(ast.get(i), x, collector, level + 1);
        }
        collector.add(ast);
      } else {
        // function call like Sin(u), Log(u), f(u,...)
        for (int i = 1; i < ast.size(); i++) {
          IExpr arg = ast.get(i);
          if (!arg.isFree(x, true)) {
            collector.add(arg);
            collectCandidates(arg, x, collector, level + 1);
          }
        }
        collector.add(ast);
      }
    }
  }
}
