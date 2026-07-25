package org.matheclipse.core.integrate;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.matheclipse.core.interfaces.IASTAppendable;
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

  /**
   * <code>true</code> if {@code expr} contains an exponential tower, i.e. a power whose
   * {@code x}-dependent exponent contains another power with an {@code x}-dependent exponent -
   * <code>E^(1-x*E^(x^2))</code>, but not <code>E^(x^2)</code>.
   *
   * <p>
   * The Rubi rules have nothing for these and grind until the evaluation deadline, so
   * {@code Integrate} lets this stage try a tower before it calls them.
   */
  public static boolean hasExponentialTower(IExpr expr, IExpr x) {
    if (!expr.isAST()) {
      return false;
    }
    IAST ast = (IAST) expr;
    if (ast.isPower() && !ast.exponent().isFree(x, true)
        && containsVariableExponent(ast.exponent(), x)) {
      return true;
    }
    for (int i = 1; i < ast.size(); i++) {
      if (hasExponentialTower(ast.get(i), x)) {
        return true;
      }
    }
    return false;
  }

  /** <code>true</code> if {@code expr} contains a power whose exponent depends on {@code x}. */
  private static boolean containsVariableExponent(IExpr expr, IExpr x) {
    if (!expr.isAST()) {
      return false;
    }
    IAST ast = (IAST) expr;
    if (ast.isPower() && !ast.exponent().isFree(x, true)) {
      return true;
    }
    for (int i = 1; i < ast.size(); i++) {
      if (containsVariableExponent(ast.get(i), x)) {
        return true;
      }
    }
    return false;
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
      }
      if (gOfT.isNIL() || !gOfT.isFree(x, true)) {
        // last resort for an inner function the structural substitution above cannot see
        gOfT = rewriteFrozen(integrand, du, u, t, x, engine);
      }
      if (gOfT.isNIL() || !gOfT.isFree(x, true)) {
        continue;
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
   * Rewrite <code>integrand/u'</code> in terms of <code>t == u(x)</code> for an inner function that
   * a structural substitution cannot find, and return {@link F#NIL} if that fails.
   *
   * <p>
   * Two things hide an inner function like <code>u = x*E^(x^2)</code> from {@link F#subst}:
   *
   * <ul>
   * <li><code>Times</code> is flat, so <code>x*E^(x^2)*rest</code> has no <code>x*E^(x^2)</code>
   * node to replace - only the pattern matcher can match a sub-product of a flat product.
   * <li>the engine keeps <code>E^a*E^b</code> merged as <code>E^(a+b)</code>, so after cancelling
   * the quotient the factor <code>E^(x^2)</code> sits inside an exponent such as
   * <code>E^(1-t+x^2)</code>, where nothing can match it. Splitting the power apart again is
   * pointless: it is re-merged on the next evaluation.
   * </ul>
   *
   * <p>
   * So every power whose exponent depends on <code>x</code> is first replaced by an opaque dummy
   * symbol ("frozen"). Nothing merges with a symbol, the inner function becomes an ordinary
   * sub-product, and the cancellation runs in a plain rational domain. The dummies are thawed
   * afterwards and the substitution is applied once more, because a thawed exponent can contain
   * <code>u</code> again (<code>E^(1-x*E^(x^2))</code>).
   *
   * <p>
   * Freezing loses the relation between different powers of the same base (<code>E^(2*x^2)</code>
   * and <code>E^(x^2)</code> become unrelated symbols), so this can fail to cancel where the
   * unfrozen form would. It only ever runs after the direct substitutions have failed, and the
   * caller diff-back verifies whatever comes out.
   */
  private static IExpr rewriteFrozen(IExpr integrand, IExpr du, IExpr u, ISymbol t, IExpr x,
      EvalEngine engine) {
    Map<IExpr, IExpr> frozen = new LinkedHashMap<>();
    IExpr integrandFrozen = freezePowers(integrand, x, frozen);
    IExpr duFrozen = freezePowers(du, x, frozen);
    IExpr uFrozen = freezePowers(u, x, frozen);
    IExpr quotient;
    try {
      quotient = engine.evaluate(F.Cancel(F.Together(F.Divide(integrandFrozen, duFrozen))));
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
    IExpr gOfT = replaceRepeated(quotient, uFrozen, t, engine);
    if (gOfT.isNIL()) {
      return F.NIL;
    }
    if (!frozen.isEmpty()) {
      IASTAppendable thawRules = F.ListAlloc(frozen.size());
      for (Map.Entry<IExpr, IExpr> entry : frozen.entrySet()) {
        thawRules.append(F.Rule(entry.getValue(), entry.getKey()));
      }
      try {
        gOfT = engine.evaluate(F.ReplaceRepeated(gOfT, thawRules));
      } catch (RuntimeException rex) {
        org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
        return F.NIL;
      }
      gOfT = replaceRepeated(gOfT, u, t, engine);
    }
    return gOfT;
  }

  /** Replace every occurrence of {@code from} by {@code to} using the pattern matcher. */
  private static IExpr replaceRepeated(IExpr expr, IExpr from, IExpr to, EvalEngine engine) {
    if (from.equals(to)) {
      return expr;
    }
    try {
      return engine.evaluate(F.ReplaceRepeated(expr, F.Rule(from, to)));
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /**
   * Replace every power whose exponent depends on {@code x} by dummy symbols. {@code frozen}
   * collects primitive power -&gt; dummy for thawing.
   */
  private static IExpr freezePowers(IExpr expr, IExpr x, Map<IExpr, IExpr> frozen) {
    return F.subst(expr, e -> {
      if (e.isPower() && !e.exponent().isFree(x, true)) {
        return freezePower((IAST) e, x, frozen);
      }
      return F.NIL;
    });
  }

  /**
   * Freeze one power <code>b^(c1*f1 + c2*f2 + ...)</code> as <code>d1^c1 * d2^c2 * ...</code>, one
   * dummy <code>di</code> per <i>primitive</i> power <code>b^fi</code>.
   *
   * <p>
   * Splitting the exponent by its additive terms is what keeps the frozen powers related to each
   * other: the integrand stores <code>E^(1-x*E^(x^2)+2*x^2)</code> as one merged power, and a
   * single opaque dummy for it would lose the <code>E^(2*x^2)</code> that has to cancel against
   * <code>u' = E^(x^2)*(1+2*x^2)</code>. Per additive term, <code>E^(2*x^2)</code> becomes
   * <code>d^2</code> of the same <code>d</code> that <code>E^(x^2)</code> becomes, and the
   * cancellation goes through. Terms free of {@code x} stay as they are (a constant factor).
   */
  private static IExpr freezePower(IAST power, IExpr x, Map<IExpr, IExpr> frozen) {
    IExpr base = power.base();
    IAST terms = power.exponent().isPlus() ? (IAST) power.exponent() : F.Plus(power.exponent());
    IASTAppendable product = F.TimesAlloc(terms.argSize());
    for (int i = 1; i < terms.size(); i++) {
      IExpr term = terms.get(i);
      if (term.isFree(x, true)) {
        product.append(F.Power(base, term));
        continue;
      }
      IExpr coefficient = F.C1;
      IExpr primitiveExponent = term;
      if (term.isTimes() && term.first().isNumber()) {
        coefficient = term.first();
        primitiveExponent = ((IAST) term).rest().oneIdentity1();
      }
      IExpr primitive = F.Power(base, primitiveExponent);
      IExpr dummy = frozen.get(primitive);
      if (dummy == null) {
        dummy = F.Dummy("dd$e" + (frozen.size() + 1));
        frozen.put(primitive, dummy);
      }
      product.append(coefficient.isOne() ? dummy : F.Power(dummy, coefficient));
    }
    return product.oneIdentity1();
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
        if (ast.isTimes() && ast.first().isNumber()) {
          // x*E^(x^2) only occurs as -x*E^(x^2) inside 1-x*E^(x^2); a constant factor makes no
          // difference to the method (it just scales g), so offer the bare product as well
          collector.add(ast.rest().oneIdentity1());
        }
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
