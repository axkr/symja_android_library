package org.matheclipse.core.integrate;

import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.integrate.DifferentialTower.Monomial;
import org.matheclipse.core.integrate.DifferentialTower.MonomialType;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Integration over a <b>primitive monomial</b> - the {@code Log} case of the transcendental Risch
 * algorithm.
 *
 * <p>
 * With {@code t = Log(u)} adjoined, {@code D(t) = D(u)/u} lies in the base field, and an integrand
 * that is rational in {@code t} splits by partial fractions in {@code t}. Each simple pole
 * contributes a logarithm of a <em>tower</em> element rather than of a base-field element: since
 *
 * <pre>
 * D(Log(t - a)) = (D(t) - D(a)) / (t - a)
 * </pre>
 *
 * a term {@code c/(t - a)} integrates to {@code lambda*Log(t - a)} exactly when
 * {@code lambda = c/(D(t) - D(a))} is a constant. That is the step no rewriting rule performs, and
 * it is what
 *
 * <pre>
 * Integrate((2*Log(x)^2-Log(x)-x^2)/(Log(x)^3-x^2*Log(x)), x)
 * </pre>
 *
 * needs: over {@code t = Log(x)} it is {@code 1/t + ((x-1)/(2*x))/(t-x) + ((x+1)/(2*x))/(t+x)},
 * whose last two terms give {@code -Log(x-Log(x))/2 + Log(x+Log(x))/2} by the test above, while
 * {@code 1/t} is left to the engine (which knows {@code Integrate(1/Log(x),x) == LogIntegral(x)}).
 *
 * <p>
 * Numerator terms that are not polynomial in {@code t} (an algebraic element such as
 * {@code Sqrt(x+Log(x))}) are split off first and handed back to <code>Integrate</code> on their
 * own - the point of the split is that the rational-in-{@code t} part then cancels against the
 * denominator instead of being ground over by the rules as one big fraction.
 *
 * <p>
 * Reference: Bronstein, <i>Symbolic Integration I</i>, ch. 5 (the primitive case). This implements
 * the partial-fraction/logarithmic-derivative part of it, not the full structure theorem.
 */
public class PrimitiveTowerIntegration {

  /** Recursion guard: the stage re-enters {@code Integrate} for the individual terms. */
  private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);

  private PrimitiveTowerIntegration() {}

  /**
   * Integrate {@code f} over a primitive (logarithmic) monomial.
   *
   * @return the antiderivative or {@link F#NIL}
   */
  public static IExpr integrate(IExpr integrand, IExpr x, EvalEngine engine) {
    if (!Config.INTEGRATE_ALGORITHM_PRIMITIVE_TOWER || !x.isSymbol()) {
      return F.NIL;
    }
    int depth = DEPTH.get();
    if (depth >= 2) {
      return F.NIL;
    }
    DEPTH.set(depth + 1);
    try {
      IExpr result = integrateTower(integrand, x, engine);
      if (result.isPresent()) {
        return result;
      }
      return normalizeDependentLogs(integrand, x, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    } finally {
      DEPTH.set(depth);
    }
  }

  /**
   * The structure-theorem step for rationally <em>dependent</em> logarithms. Two {@code Log}
   * monomials such as {@code Log(x)} and {@code Log(x^2) = 2*Log(x)} are not algebraically
   * independent, so the tower must not treat them as separate transcendentals -
   * {@code 1/(Log(x)+Log(x^2))} is {@code 1/(3*Log(x))}, whose integral is {@code LogIntegral(x)/3}.
   *
   * <p>
   * The relations are exposed by {@code PowerExpand} ({@code Log(u^n) -> n*Log(u)},
   * {@code Log(u*v) -> Log(u)+Log(v)}). That is only valid up to a branch constant, but a constant
   * is irrelevant for an antiderivative and the result is diff-back verified against the
   * <em>original</em> integrand, so an incorrect expansion is rejected. Fires only when the tower
   * genuinely has several monomials that the expansion collapses to fewer, so it cannot disturb an
   * integrand whose logs are already independent.
   */
  private static IExpr normalizeDependentLogs(IExpr integrand, IExpr x, EvalEngine engine) {
    DifferentialTower tower = DifferentialTower.build(integrand, x, engine);
    if (tower.monomials().size() < 2) {
      return F.NIL;
    }
    IExpr normalized = engine.evaluate(F.PowerExpand(integrand));
    if (normalized.equals(integrand)) {
      return F.NIL;
    }
    DifferentialTower normalizedTower = DifferentialTower.build(normalized, x, engine);
    if (normalizedTower.monomials().size() >= tower.monomials().size()) {
      return F.NIL; // the logs were independent - nothing collapsed
    }
    IExpr result = integrateByEngine(normalized, x, engine);
    return result.isPresent() && verifyAntiderivative(result, integrand, x, engine) ? result
        : F.NIL;
  }

  private static IExpr integrateTower(IExpr integrand, IExpr x, EvalEngine engine) {
    DifferentialTower tower = DifferentialTower.build(integrand, x, engine);
    List<Monomial> monomials = tower.monomials();
    if (monomials.size() != 1 || monomials.get(0).type != MonomialType.PRIMITIVE) {
      // one Log monomial only - a taller tower needs the recursive algorithm
      return F.NIL;
    }
    final Monomial monomial = monomials.get(0);
    final ISymbol t = monomial.symbol;
    IExpr towerForm = tower.towerForm();
    if (towerForm.isFree(t, true)) {
      return F.NIL;
    }

    IExpr together = engine.evaluate(F.Together(towerForm));
    IExpr numerator = engine.evaluate(F.Numerator(together));
    IExpr denominator = engine.evaluate(F.Denominator(together));
    // Only the shape the rules have nothing for: the monomial and x mixed in a denominator of
    // degree >= 2. Anything flatter is left to them, so no ordinary Log integral changes form.
    if (!engine.evaluate(F.PolynomialQ(denominator, t)).isTrue() || denominator.isFree(x, true)) {
      return F.NIL;
    }
    int denominatorDegree = engine.evaluate(F.Exponent(denominator, t)).toIntDefault();
    if (denominatorDegree < 2) {
      return F.NIL;
    }

    // separate the numerator terms that are polynomial in t from the rest (algebraic elements)
    IAST numeratorTerms = numerator.isPlus() ? (IAST) numerator : F.Plus(numerator);
    IASTAppendable rationalTerms = F.PlusAlloc(numeratorTerms.size());
    IASTAppendable otherTerms = F.PlusAlloc(numeratorTerms.size());
    for (int i = 1; i < numeratorTerms.size(); i++) {
      IExpr term = numeratorTerms.get(i);
      if (engine.evaluate(F.PolynomialQ(term, t)).isTrue()) {
        rationalTerms.append(term);
      } else {
        otherTerms.append(term);
      }
    }

    IASTAppendable result = F.PlusAlloc(4);
    if (rationalTerms.argSize() > 0) {
      IExpr integrated = integrateRational(rationalTerms.oneIdentity0(), denominator, t, monomial,
          tower, x, engine);
      if (integrated.isNIL()) {
        return F.NIL;
      }
      result.append(integrated);
    }
    if (otherTerms.argSize() > 0) {
      // again no Cancel: it would expand (1+x)*Sqrt(u) into two fractions, and the algebraic
      // factor has to stay one power for the engine to recognize Sqrt(u)/u^2 as u^(-3/2)
      IExpr otherPart =
          engine.evaluate(F.Together(F.Divide(otherTerms.oneIdentity0(), denominator)));
      IExpr expression = tower.toExpression(otherPart);
      IExpr integrated = integrateByEngine(engine.evaluate(F.Simplify(expression)), x, engine);
      if (integrated.isNIL()) {
        integrated = integrateByEngine(expression, x, engine);
      }
      if (integrated.isNIL()) {
        return F.NIL;
      }
      result.append(integrated);
    }

    IExpr antiderivative = engine.evaluate(result);
    return verifyAntiderivative(antiderivative, integrand, x, engine) ? antiderivative : F.NIL;
  }

  /**
   * Integrate the part that is rational in the monomial: partial fractions in {@code t}, then every
   * term either by the logarithmic-derivative test or by handing it back to the engine.
   */
  private static IExpr integrateRational(IExpr numerator, IExpr denominator, ISymbol t,
      Monomial monomial, DifferentialTower tower, IExpr x, EvalEngine engine) {
    // Keep this one fraction: Cancel() would distribute over the numerator's terms and Apart would
    // then decompose each piece on its own. And hand Apart an expanded numerator and denominator -
    // it only splits over the factorization it is given, and a partly factored denominator
    // (t*(t^2-x^2)) stops one step short of the simple poles the logarithmic test needs.
    IExpr fraction = engine.evaluate(F.Together(F.Divide(numerator, denominator)));
    IExpr expanded = engine.evaluate(
        F.Divide(F.Expand(F.Numerator(fraction)), F.Expand(F.Denominator(fraction))));
    IExpr apart = engine.evaluate(F.Apart(expanded, t));
    IAST terms = apart.isPlus() ? (IAST) apart : F.Plus(apart);
    IASTAppendable sum = F.PlusAlloc(terms.size());
    for (int i = 1; i < terms.size(); i++) {
      IExpr term = terms.get(i);
      if (term.isFree(t, true)) {
        IExpr integrated = integrateByEngine(tower.toExpression(term), x, engine);
        if (integrated.isNIL()) {
          return F.NIL;
        }
        sum.append(integrated);
        continue;
      }
      // A repeated pole in the monomial - a denominator (a*t+b)^k with k >= 2 - is not a simple
      // pole and Apart leaves it as one term. Hermite reduction peels off its rational part and
      // leaves an order-1 remainder that the logarithmic test or the engine can finish.
      IExpr[] hermite = hermiteReduceLinear(term, t, monomial, tower, x, engine);
      IExpr toIntegrate = term;
      if (hermite != null) {
        // back-substitute t -> Log(u) so the rational part is in the same variable as the rest
        sum.append(tower.toExpression(hermite[0]));
        if (hermite[1].isZero()) {
          continue;
        }
        toIntegrate = hermite[1]; // the order-1 remainder, still in the monomial t
      }
      IExpr integrated = integrateTerm(toIntegrate, t, monomial, tower, x, engine);
      if (integrated.isNIL()) {
        return F.NIL;
      }
      sum.append(integrated);
    }
    return engine.evaluate(sum);
  }

  /**
   * Hermite reduction of one partial-fraction term over a <em>repeated linear</em> factor of the
   * monomial - a denominator {@code (t - r)^k} (up to a factor free of {@code t}) with {@code k >=
   * 2}. Returns {@code null} when the term is not of that shape (its denominator is squarefree in
   * {@code t}, or its repeated factor is not linear).
   *
   * <p>
   * The step is the primitive-case analogue of ordinary Hermite reduction (Bronstein, <i>Symbolic
   * Integration I</i>, ch. 5.3). Writing the derivation as {@code D}, and using that for a primitive
   * monomial {@code D(t - r) = D(t) - D(r)} lies in the base field (free of {@code t}),
   *
   * <pre>
   * D(B/(t-r)^(m-1)) = D(B)/(t-r)^(m-1) - (m-1)*B*D(t-r)/(t-r)^m
   * </pre>
   *
   * so a pole {@code P/(t-r)^m} is lowered by choosing the constant (in {@code t}) {@code B =
   * -P(r)/((m-1)*D(t-r))} and carrying {@code (Q - D(B))/(t-r)^(m-1)} forward, where {@code Q = (P +
   * (m-1)*B*D(t-r))/(t-r)}. Iterating down to {@code m = 1} yields the rational part
   * {@code sum B_i/(t-r)^i} and an order-1 remainder. Restricting to a linear factor keeps "mod
   * {@code (t-r)}" a plain evaluation at {@code t = r}, so no polynomial arithmetic over the base
   * field is needed.
   *
   * @return {@code {D(g) rational part, order-1 remainder over t-r}}, or {@code null} if not
   *         applicable
   */
  private static IExpr[] hermiteReduceLinear(IExpr term, ISymbol t, Monomial monomial,
      DifferentialTower tower, IExpr x, EvalEngine engine) {
    IExpr together = engine.evaluate(F.Together(term));
    IExpr numerator = engine.evaluate(F.Numerator(together));
    IExpr denominator = engine.evaluate(F.Denominator(together));
    if (!engine.evaluate(F.PolynomialQ(denominator, t)).isTrue()) {
      return null;
    }
    int degree = engine.evaluate(F.Exponent(denominator, t)).toIntDefault();
    if (degree < 2) {
      return null;
    }
    // the denominator must be (up to a t-free factor) a power of a single linear factor t - r
    IExpr radical =
        engine.evaluate(F.Cancel(F.Divide(denominator, F.PolynomialGCD(denominator,
            F.D(denominator, t)))));
    if (engine.evaluate(F.Exponent(radical, t)).toIntDefault() != 1) {
      return null; // repeated factor is not linear
    }
    IExpr c1 = engine.evaluate(F.Coefficient(radical, t, F.C1));
    IExpr c0 = engine.evaluate(F.Coefficient(radical, t, F.C0));
    if (c1.isZero() || !c1.isFree(t, true) || !c0.isFree(t, true)) {
      return null;
    }
    IExpr root = engine.evaluate(F.Divide(F.Negate(c0), c1)); // r with radical == c1*(t - r)
    int multiplicity = degree; // denominator == c1^degree * (t - r)^degree (single linear factor)

    // pull the whole denominator onto (t - r)^multiplicity: P0 = numerator / (denominator/(t-r)^k)
    IExpr linearPower = engine.evaluate(F.Power(F.Subtract(t, root), F.ZZ(multiplicity)));
    IExpr constantFactor = engine.evaluate(F.Cancel(F.Divide(denominator, linearPower)));
    if (!constantFactor.isFree(t, true)) {
      return null;
    }
    IExpr pm = engine.evaluate(F.Cancel(F.Divide(numerator, constantFactor)));
    // D(t - r) = D(t) - D(r), free of t for a primitive monomial
    IExpr dv = engine.evaluate(F.Subtract(monomial.derivative, F.D(root, x)));
    if (dv.isZero()) {
      return null;
    }

    IASTAppendable rationalPart = F.PlusAlloc(multiplicity);
    IExpr linear = F.Subtract(t, root);
    for (int m = multiplicity; m >= 2; m--) {
      IExpr pmAtRoot = engine.evaluate(F.ReplaceAll(pm, F.Rule(t, root)));
      IExpr b = engine.evaluate(F.Divide(F.Negate(pmAtRoot), F.Times(F.ZZ(m - 1), dv)));
      if (!b.isFree(t, true)) {
        return null;
      }
      rationalPart.append(F.Divide(b, F.Power(linear, F.ZZ(m - 1))));
      IExpr shifted =
          engine.evaluate(F.ExpandAll(F.Plus(pm, F.Times(F.ZZ(m - 1), b, dv))));
      IExpr quotient = engine.evaluate(F.Cancel(F.Divide(shifted, linear)));
      if (!engine.evaluate(F.PolynomialQ(quotient, t)).isTrue()) {
        return null; // exact division failed - not the expected shape
      }
      // D(b) for b free of t is the ordinary derivative in x
      pm = engine.evaluate(F.ExpandAll(F.Subtract(quotient, F.D(b, x))));
    }
    IExpr remainder = pm.isZero() ? F.C0 : engine.evaluate(F.Divide(pm, linear));
    return new IExpr[] {engine.evaluate(rationalPart), remainder};
  }

  /** One partial-fraction term: the logarithmic-derivative test first, else back to the engine. */
  private static IExpr integrateTerm(IExpr term, ISymbol t, Monomial monomial,
      DifferentialTower tower, IExpr x, EvalEngine engine) {
    IExpr logarithm = integrateSimplePole(term, t, monomial, x, engine);
    if (logarithm.isPresent()) {
      return logarithm;
    }
    return integrateByEngine(tower.toExpression(term), x, engine);
  }

  /**
   * The primitive-case logarithm: {@code c/(t-a)} integrates to {@code lambda*Log(t-a)} if
   * {@code lambda = c/(D(t)-D(a))} is free of {@code x}. Returns {@link F#NIL} otherwise.
   */
  private static IExpr integrateSimplePole(IExpr term, ISymbol t, Monomial monomial, IExpr x,
      EvalEngine engine) {
    IExpr together = engine.evaluate(F.Together(term));
    IExpr denominator = engine.evaluate(F.Denominator(together));
    if (!engine.evaluate(F.PolynomialQ(denominator, t)).isTrue()
        || engine.evaluate(F.Exponent(denominator, t)).toIntDefault() != 1) {
      return F.NIL;
    }
    IExpr leading = engine.evaluate(F.Coefficient(denominator, t, F.C1));
    if (leading.isZero() || !leading.isFree(t, true)) {
      return F.NIL;
    }
    // denominator == leading*(t - a)
    IExpr a = engine.evaluate(F.Divide(F.Negate(F.Coefficient(denominator, t, F.C0)), leading));
    IExpr c = engine.evaluate(F.Divide(F.Numerator(together), leading));
    if (!a.isFree(t, true) || !c.isFree(t, true)) {
      return F.NIL;
    }
    IExpr derivative = engine.evaluate(F.Subtract(monomial.derivative, F.D(a, x)));
    if (derivative.isZero()) {
      return F.NIL;
    }
    IExpr lambda = engine.evaluate(F.Cancel(F.Together(F.Divide(c, derivative))));
    if (!lambda.isFree(x, true) || !lambda.isFree(t, true)) {
      return F.NIL;
    }
    IExpr argument = engine.evaluate(F.Subtract(monomial.original, a));
    return engine.evaluate(F.Times(lambda, F.Log(argument)));
  }

  /** Hand a single term back to {@code Integrate}; {@link F#NIL} unless it comes back in closed form. */
  private static IExpr integrateByEngine(IExpr expr, IExpr x, EvalEngine engine) {
    IExpr integrated;
    try {
      integrated = engine.evaluateNIL(F.Integrate(expr, x));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
    if (integrated.isNIL() || !integrated.isFreeAST(S.Integrate) || !integrated.isSpecialsFree()) {
      return F.NIL;
    }
    return integrated;
  }

  /** Diff-back self-verification: {@code true} iff {@code D(result, x)} equals the integrand. */
  private static boolean verifyAntiderivative(IExpr result, IExpr integrand, IExpr x,
      EvalEngine engine) {
    if (result.isNIL() || !result.isFreeAST(S.Integrate) || !result.isSpecialsFree()) {
      return false;
    }
    try {
      IExpr difference = F.Subtract(F.D(result, x), integrand);
      if (engine.evaluate(F.Together(difference)).isZero()) {
        return true;
      }
      if (engine.evaluate(F.Simplify(difference)).isZero()) {
        return true;
      }
      return numericallyZero(difference, x, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return false;
    }
  }

  /** True iff {@code expr} vanishes numerically at the sample points where it evaluates at all. */
  private static boolean numericallyZero(IExpr expr, IExpr x, EvalEngine engine) {
    final double[] points = {1.3, 2.1, 3.7, 5.2};
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
