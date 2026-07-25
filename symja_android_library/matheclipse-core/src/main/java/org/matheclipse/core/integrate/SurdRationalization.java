package org.matheclipse.core.integrate;

import java.util.LinkedHashSet;
import java.util.Set;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Rationalize a denominator which contains a single square root.
 *
 * <p>
 * Classical "rationalizing the denominator": if the integrand is <code>N/D</code> and the
 * denominator, read as a polynomial in <code>t = Sqrt(R(x))</code> and reduced modulo
 * <code>t^2 - R</code>, has the form <code>D = A + B*t</code> with <code>A, B</code> free of the
 * root, then multiplying by the conjugate gives the identity
 *
 * <pre>
 * N / (A + B*Sqrt(R)) == N*(A - B*Sqrt(R)) / (A^2 - B^2*R)
 * </pre>
 *
 * with a denominator free of the root. The rewritten integrand is handed back to
 * <code>Integrate()</code>, which can then split it by linearity into a rational part and a part
 * with the root in the numerator - forms the Rubi rules and the algebraic stages do handle.
 *
 * <p>
 * Example: <code>Integrate(Log(x^2+Sqrt(1-x^2)), x)</code> reduces by parts to
 * <code>Integrate(x^2/(x^2+Sqrt(1-x^2)), x)</code>, which stays unevaluated; conjugating turns the
 * denominator into <code>x^4+x^2-1</code> and the integral is elementary (golden-ratio constants).
 *
 * <p>
 * The transformation is an algebraic identity (valid wherever <code>D != 0</code>, and independent
 * of the branch chosen for the root, because the same value <code>Sqrt(R)</code> occurs on both
 * sides), but the result is still diff-back verified like the other heuristic stages.
 */
public class SurdRationalization {

  /** Recursion depth guard for nested rationalizations. */
  private static final ThreadLocal<Integer> RECURSION_DEPTH = ThreadLocal.withInitial(() -> 0);

  /** Maximum degree in {@code t = Sqrt(R)} accepted for the denominator. */
  private static final int MAX_DEGREE = 8;

  private SurdRationalization() {}

  /**
   * Try to rationalize a single square root out of the denominator of {@code integrand} and
   * integrate the rewritten integrand.
   *
   * @param integrand the integrand
   * @param x the integration variable
   * @param engine the evaluation engine
   * @return the antiderivative or {@link F#NIL}
   */
  public static IExpr integrate(IExpr integrand, IExpr x, EvalEngine engine) {
    if (!Config.INTEGRATE_ALGORITHM_SURD_RATIONALIZATION) {
      return F.NIL;
    }
    int depth = RECURSION_DEPTH.get();
    if (depth >= 2) {
      return F.NIL;
    }
    RECURSION_DEPTH.set(depth + 1);
    try {
      return rationalizeAndIntegrate(integrand, x, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    } finally {
      RECURSION_DEPTH.set(depth);
    }
  }

  private static IExpr rationalizeAndIntegrate(IExpr integrand, IExpr x, EvalEngine engine) {
    IExpr together = engine.evaluate(F.Together(integrand));
    IExpr numerator = engine.evaluate(F.Numerator(together));
    IExpr denominator = engine.evaluate(F.Denominator(together));
    if (denominator.isFree(x, true) || denominator.isOne()) {
      return F.NIL;
    }
    IExpr radicand = uniqueRadicand(denominator, x);
    if (radicand.isNIL()) {
      return F.NIL;
    }
    IExpr sqrtR = engine.evaluate(F.Sqrt(radicand));
    if (!sqrtR.isPower()) {
      // the root collapsed to something simpler - nothing to rationalize
      return F.NIL;
    }

    // read the denominator as a polynomial in t == Sqrt(R) ...
    final ISymbol t = F.Dummy("sr$t");
    IExpr denominatorInT = substituteRoot(denominator, radicand, t);
    if (denominatorInT.isNIL()) {
      return F.NIL;
    }
    denominatorInT = engine.evaluate(F.Expand(denominatorInT));
    if (!denominatorInT.isFree(sqrtR, true)) {
      return F.NIL;
    }
    int degree = engine.evaluate(F.Exponent(denominatorInT, t)).toIntDefault();
    if (degree < 1 || degree > MAX_DEGREE) {
      return F.NIL;
    }

    // ... and reduce it modulo t^2 - R to A + B*t
    IExpr a = F.C0;
    IExpr b = F.C0;
    for (int k = 0; k <= degree; k++) {
      IExpr coefficient = engine.evaluate(F.Coefficient(denominatorInT, t, F.ZZ(k)));
      if (coefficient.isZero()) {
        continue;
      }
      if (!coefficient.isFree(t, true)) {
        return F.NIL;
      }
      IExpr term = F.Times(coefficient, F.Power(radicand, F.ZZ(k / 2)));
      if ((k & 1) == 0) {
        a = F.Plus(a, term);
      } else {
        b = F.Plus(b, term);
      }
    }
    a = engine.evaluate(a);
    b = engine.evaluate(b);
    if (b.isZero() || !a.isFree(sqrtR, true) || !b.isFree(sqrtR, true)) {
      return F.NIL;
    }

    IExpr conjugateDenominator =
        engine.evaluate(F.Subtract(F.Sqr(a), F.Times(F.Sqr(b), radicand)));
    if (conjugateDenominator.isZero() || !conjugateDenominator.isFree(sqrtR, true)) {
      return F.NIL;
    }
    IExpr conjugateNumerator =
        engine.evaluate(F.ExpandAll(F.Times(numerator, F.Subtract(a, F.Times(b, sqrtR)))));
    IExpr rationalized = engine.evaluate(F.Divide(conjugateNumerator, conjugateDenominator));
    if (rationalized.equals(integrand) || rationalized.equals(together)) {
      return F.NIL;
    }

    IExpr result = engine.evaluateNIL(F.Integrate(rationalized, x));
    if (result.isPresent() && verifyAntiderivative(result, integrand, x, engine)) {
      return result;
    }
    // Second chance: the rational part and the part carrying the root sometimes only integrate
    // (or only integrate correctly) on their own.
    result = integrateByLinearity(conjugateNumerator, conjugateDenominator, x, engine);
    return result.isPresent() && verifyAntiderivative(result, integrand, x, engine) ? result
        : F.NIL;
  }

  /**
   * Integrate {@code numerator/denominator} summand by summand. The rationalized integrand is a sum
   * of a rational part and a part carrying the root, which the rules sometimes only handle
   * separately. Returns {@link F#NIL} unless every summand integrates in closed form.
   */
  private static IExpr integrateByLinearity(IExpr numerator, IExpr denominator, IExpr x,
      EvalEngine engine) {
    IAST summands = numerator.isPlus() ? (IAST) numerator : F.Plus(numerator);
    IASTAppendable sum = F.PlusAlloc(summands.argSize());
    for (int i = 1; i < summands.size(); i++) {
      IExpr part = engine.evaluateNIL(F.Integrate(F.Divide(summands.get(i), denominator), x));
      if (part.isNIL() || !part.isFreeAST(S.Integrate) || !part.isSpecialsFree()) {
        return F.NIL;
      }
      sum.append(part);
    }
    return engine.evaluate(sum);
  }

  /**
   * The radicand {@code R} of the one square root {@code Sqrt(R)} (possibly raised to an odd
   * multiple of {@code 1/2}) which occurs in {@code expr} and depends on {@code x}, or
   * {@link F#NIL} if there is none or more than one.
   */
  private static IExpr uniqueRadicand(IExpr expr, IExpr x) {
    Set<IExpr> radicands = new LinkedHashSet<>();
    if (!collectRadicands(expr, x, radicands)) {
      return F.NIL;
    }
    return radicands.size() == 1 ? radicands.iterator().next() : F.NIL;
  }

  /**
   * Collect the bases of all half-integer powers depending on {@code x}. Returns {@code false} if a
   * non-half-integer fractional power depending on {@code x} is found - such an integrand is not a
   * pure square-root extension and rationalizing by a conjugate would not clear the denominator.
   */
  private static boolean collectRadicands(IExpr expr, IExpr x, Set<IExpr> radicands) {
    if (expr.isFree(x, true)) {
      return true;
    }
    if (expr.isPower()) {
      IExpr exponent = expr.exponent();
      if (exponent.isRational() && !exponent.isInteger()) {
        IRational rational = (IRational) exponent;
        if (!rational.denominator().equals(F.C2)) {
          return false;
        }
        radicands.add(expr.base());
        return collectRadicands(expr.base(), x, radicands);
      }
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      for (int i = 1; i < ast.size(); i++) {
        if (!collectRadicands(ast.get(i), x, radicands)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Replace every power {@code R^(n/2)} of the given radicand by {@code t^n}. Returns {@link F#NIL}
   * if a power of the radicand cannot be expressed as a non-negative power of {@code t}.
   */
  private static IExpr substituteRoot(IExpr expr, IExpr radicand, ISymbol t) {
    boolean[] failed = new boolean[1];
    IExpr result = F.subst(expr, e -> {
      if (e.isPower() && e.base().equals(radicand)) {
        IExpr exponent = e.exponent();
        if (exponent.isRational()) {
          IExpr doubled = ((IRational) exponent).multiply(F.C2);
          int n = doubled.toIntDefault();
          if (n < 0) {
            failed[0] = true;
            return F.NIL;
          }
          return F.Power(t, F.ZZ(n));
        }
        failed[0] = true;
      }
      return F.NIL;
    });
    return failed[0] ? F.NIL : result;
  }

  /** Diff-back self-verification: {@code true} iff {@code D(result, x)} equals the integrand. */
  private static boolean verifyAntiderivative(IExpr result, IExpr integrand, IExpr x,
      EvalEngine engine) {
    if (result.isNIL() || !result.isFreeAST(S.Integrate) || !result.isSpecialsFree()) {
      return false;
    }
    try {
      IExpr diff = F.Subtract(F.D(result, x), integrand);
      if (engine.evaluate(F.Together(diff)).isZero()) {
        return true;
      }
      if (engine.evaluate(F.Simplify(diff)).isZero()) {
        return true;
      }
      // Nested radicals rarely cancel symbolically; confirm numerically at generic points. This can
      // only reject, so it never accepts a result a nonzero symbolic difference already disproved.
      return numericallyZero(diff, x, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return false;
    }
  }

  /**
   * True iff {@code expr} numerically vanishes at the sample points where it can be evaluated to a
   * number at all (at least two of them must be usable).
   */
  private static boolean numericallyZero(IExpr expr, IExpr x, EvalEngine engine) {
    final double[] points = {0.17, 0.43, 0.71, 0.93, 1.7};
    int evaluated = 0;
    for (double point : points) {
      IExpr value;
      try {
        value = engine.evaluate(F.N(F.ReplaceAll(expr, F.Rule(x, F.num(point)))));
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return false;
      }
      if (!value.isNumber() || !value.isSpecialsFree()) {
        continue;
      }
      evaluated++;
      if (!engine.evaluate(F.Less(F.Abs(value), F.num(1.0e-8))).isTrue()) {
        return false;
      }
    }
    return evaluated >= 2;
  }
}
