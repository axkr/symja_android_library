package org.matheclipse.core.integrate;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.basic.MachineProfile;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.TimeLimits;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Indefinite integrals of <code>P(x)*R(E^(c*x))</code>: a polynomial in <code>x</code> times a
 * rational function of exponentials, trigonometric or hyperbolic functions of the arguments
 * <code>c_i*x</code> with rational ratios <code>c_i/c_0</code>.
 *
 * <p>
 * With <code>T = E^(b*x)</code>, <code>b = c_0/q</code> (<code>q</code> the common denominator of
 * the ratios) every exponential is a power of <code>T</code>, and the integrand is
 * <code>Sum(x^n*N_n(T))/D(T)</code>. The polynomial part of <code>N_n/D</code> integrates
 * elementarily; the partial fractions over the roots <code>r</code> of <code>D</code> by
 *
 * <pre>
 * Integrate(x^n/(E^(b*x)-r), x) = -1/r*Sum(n!/(n-k)!*x^(n-k)*PolyLog(k+1, r/E^(b*x))/b^(k+1), {k,0,n})
 * </pre>
 *
 * and its derivatives with respect to <code>r</code> for a multiple root; the root
 * <code>T=0</code> gives <code>x^n*E^(-j*b*x)</code>. The roots are the ones in radicals of the
 * irreducible factors of <code>D</code> of degree up to 4. The result is verified by
 * differentiating it back.
 */
public final class ExponentialPolyLogIntegration {

  /** the largest power of x */
  private static final int MAX_POLYNOMIAL_DEGREE = 6;

  /** the largest degree of the denominator in T */
  private static final int MAX_DENOMINATOR_DEGREE = 8;

  /** the time the simplification of one partial fraction coefficient may take */
  private static final long SIMPLIFY_MILLIS = 2000L;

  private ExponentialPolyLogIntegration() {}

  /**
   * The antiderivative, or {@link F#NIL} if <code>f</code> is not a polynomial times a rational
   * function of exponentials of commensurable linear arguments.
   *
   * @param f the integrand
   * @param x the integration variable
   * @param engine the evaluation engine
   */
  public static IExpr integrate(IExpr f, IExpr x, EvalEngine engine) {
    if (!x.isSymbol() || !isCandidate(f, x)) {
      return F.NIL;
    }
    try {
      IExpr result = ladder(f, (ISymbol) x, engine);
      if (result.isPresent() && result.isFreeAST(S.Integrate)
          && HolonomicIntegration.verified(result, f, x, engine)) {
        return result;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  /**
   * A trigonometric, hyperbolic or exponential function of <code>x</code>, no logarithm or
   * polylogarithm, and <code>x</code> outside of them.
   */
  private static boolean isCandidate(IExpr f, IExpr x) {
    if (!f.isFree(t -> t.isLog() || t.isAST(S.PolyLog), true)) {
      return false;
    }
    boolean[] kernel = new boolean[1];
    boolean[] polynomial = new boolean[1];
    scan(f, x, kernel, polynomial);
    return kernel[0] && polynomial[0];
  }

  private static void scan(IExpr f, IExpr x, boolean[] kernel, boolean[] polynomial) {
    if (f.equals(x)) {
      polynomial[0] = true;
      return;
    }
    if (f.isFree(x) || !f.isAST()) {
      return;
    }
    if (f.isTrigFunction() || f.isHyperbolicFunction() || f.isExp()) {
      kernel[0] = true;
      return;
    }
    for (IExpr arg : (IAST) f) {
      scan(arg, x, kernel, polynomial);
    }
  }

  private static IExpr ladder(IExpr f, ISymbol x, EvalEngine engine) {
    IExpr g = engine.evaluate(F.TrigToExp(f));
    // the exponents c*x+d of E
    List<IExpr> slopes = new ArrayList<>();
    if (!collectSlopes(g, x, slopes, engine) || slopes.isEmpty()) {
      return F.NIL;
    }
    IExpr c0 = slopes.get(0);
    IInteger q = F.C1;
    for (IExpr c : slopes) {
      IExpr ratio = engine.evaluate(F.Divide(c, c0));
      if (!ratio.isRational()) {
        return F.NIL;
      }
      IInteger denominator = ((org.matheclipse.core.interfaces.IRational) ratio).denominator();
      q = q.lcm(denominator);
    }
    final IExpr base = engine.evaluate(F.Divide(c0, q));
    final ISymbol t = F.Dummy("epl$t");
    IExpr h = F.subst(g, e -> {
      if (e.isPower() && e.base().isE() && !e.exponent().isFree(x)) {
        IExpr z = e.exponent();
        IExpr c = engine.evaluate(F.Coefficient(z, x, F.C1));
        IExpr d = engine.evaluate(F.Subtract(z, F.Times(c, x)));
        IExpr k = engine.evaluate(F.Divide(c, base));
        return F.Times(F.Exp(d), F.Power(t, k));
      }
      return F.NIL;
    });
    h = engine.evaluate(F.Together(h));
    IExpr numerator = engine.evaluate(F.Numerator(h));
    IExpr denominator = engine.evaluate(F.Denominator(h));
    if (!denominator.isFree(x) || denominator.isFree(t)
        || !engine.evaluate(F.PolynomialQ(denominator, t)).isTrue()
        || !engine.evaluate(F.PolynomialQ(numerator, F.List(x, t))).isTrue()) {
      return F.NIL;
    }
    int degreeX = engine.evaluate(F.Exponent(numerator, x)).toIntDefault();
    int degreeT = engine.evaluate(F.Exponent(denominator, t)).toIntDefault();
    if (degreeX < 1 || degreeX > MAX_POLYNOMIAL_DEGREE || degreeT < 1
        || degreeT > MAX_DENOMINATOR_DEGREE) {
      return F.NIL;
    }
    List<IExpr[]> roots = roots(denominator, t, engine);
    if (roots == null) {
      return F.NIL;
    }
    IExpr leading = engine.evaluate(F.Coefficient(denominator, t, F.ZZ(degreeT)));
    IASTAppendable result = F.PlusAlloc(8);
    for (int n = 0; n <= degreeX; n++) {
      TimeLimits.check();
      IExpr coefficient = engine.evaluate(F.Coefficient(numerator, x, F.ZZ(n)));
      if (coefficient.isZero()) {
        continue;
      }
      IExpr xn = F.Power(x, F.ZZ(n));
      IExpr qr = engine.evaluate(F.PolynomialQuotientRemainder(coefficient, denominator, t));
      if (!qr.isList2()) {
        return F.NIL;
      }
      IExpr quotient = qr.first();
      IExpr remainder = qr.second();
      if (!quotient.isZero()) {
        IExpr part = integrateElementary(
            F.Times(xn, F.subst(quotient, t, F.Exp(F.Times(base, x)))), x, engine);
        if (part.isNIL()) {
          return F.NIL;
        }
        result.append(part);
      }
      if (remainder.isZero()) {
        continue;
      }
      for (IExpr[] root : roots) {
        IExpr rho = root[0];
        int m = root[1].toIntDefault();
        // the cofactor of (T-rho)^m in the denominator
        IExpr cofactor = engine.evaluate(F.Times(leading, root[2]));
        IExpr fraction = F.Divide(remainder, cofactor);
        for (int j = 1; j <= m; j++) {
          IExpr cj = engine.evaluate(F.Times(F.Power(F.Factorial(F.ZZ(m - j)), F.CN1),
              F.subst(F.D(fraction, F.List(t, F.ZZ(m - j))), t, rho)));
          cj = simplified(cj, engine);
          if (cj.isZero()) {
            continue;
          }
          if (rho.isZero()) {
            IExpr part = integrateElementary(
                F.Times(xn, F.Exp(F.Times(F.ZZ(-j), base, x))), x, engine);
            if (part.isNIL()) {
              return F.NIL;
            }
            result.append(F.Times(cj, part));
          } else {
            result.append(F.Times(cj, poleTerm(n, j, rho, base, x, engine)));
          }
        }
      }
    }
    return engine.evaluate(result.oneIdentity0());
  }

  private static IExpr simplified(IExpr c, EvalEngine engine) {
    if (c.isNumber()) {
      return c;
    }
    IExpr together = engine.evaluate(F.Together(c));
    IExpr simplified = IntegrateTimeBudget.runWithin(() -> engine.evaluate(F.Simplify(together)),
        MachineProfile.millis(SIMPLIFY_MILLIS));
    return simplified.isPresent() && simplified.leafCount() <= together.leafCount() ? simplified
        : together;
  }

  private static IExpr integrateElementary(IExpr f, ISymbol x, EvalEngine engine) {
    IExpr result = engine.evaluate(F.Integrate(engine.evaluate(F.Expand(f)), x));
    return result.isFreeAST(S.Integrate) ? result : F.NIL;
  }

  /**
   * <code>Integrate(x^n/(E^(b*x)-rho)^j, x)</code>: the ladder for <code>j=1</code>, its
   * derivative of order <code>j-1</code> in <code>rho</code> divided by <code>(j-1)!</code>
   * otherwise.
   */
  private static IExpr poleTerm(int n, int j, IExpr rho, IExpr base, ISymbol x,
      EvalEngine engine) {
    ISymbol r = F.Dummy("epl$r");
    IASTAppendable sum = F.PlusAlloc(n + 1);
    for (int k = 0; k <= n; k++) {
      // -1/r * n!/(n-k)! * x^(n-k) * PolyLog(k+1, r/E^(b*x)) / b^(k+1)
      IExpr factor = F.Times(F.CN1, F.Power(r, F.CN1),
          F.Divide(F.Factorial(F.ZZ(n)), F.Factorial(F.ZZ(n - k))),
          F.Power(base, F.ZZ(-k - 1)));
      sum.append(F.Times(factor, F.Power(x, F.ZZ(n - k)),
          F.PolyLog(F.ZZ(k + 1), F.Times(r, F.Exp(F.Times(F.CN1, base, x))))));
    }
    IExpr term = sum;
    if (j > 1) {
      term = F.Times(F.Power(F.Factorial(F.ZZ(j - 1)), F.CN1), F.D(sum, F.List(r, F.ZZ(j - 1))));
    }
    return engine.evaluate(F.subst(engine.evaluate(term), r, rho));
  }

  /**
   * The roots of the polynomial in radicals: <code>{root, multiplicity, cofactor}</code>, where
   * the cofactor is the polynomial divided by <code>(t-root)^multiplicity</code> and by its
   * leading coefficient; <code>null</code> if a factor has no roots in radicals.
   */
  private static List<IExpr[]> roots(IExpr polynomial, ISymbol t, EvalEngine engine) {
    IExpr list = engine.evaluate(F.FactorSquareFreeList(polynomial));
    if (!list.isListOfLists()) {
      // complex coefficients: the polynomial as it stands, if it is square free
      IExpr gcd = engine.evaluate(F.PolynomialGCD(polynomial, F.D(polynomial, t)));
      if (!gcd.isFree(t)) {
        return null;
      }
      list = F.List(F.List(polynomial, F.C1));
    }
    List<IExpr[]> factors = new ArrayList<>();
    for (IExpr entry : (IAST) list) {
      if (!entry.isList2() || entry.first().isFree(t)) {
        continue;
      }
      IExpr factor = entry.first();
      // monic
      int degree = engine.evaluate(F.Exponent(factor, t)).toIntDefault();
      if (degree < 1) {
        return null;
      }
      IExpr lc = engine.evaluate(F.Coefficient(factor, t, F.ZZ(degree)));
      factors.add(new IExpr[] {engine.evaluate(F.Expand(F.Divide(factor, lc))), entry.second()});
    }
    List<IExpr[]> roots = new ArrayList<>();
    for (int i = 0; i < factors.size(); i++) {
      IExpr factor = factors.get(i)[0];
      IExpr multiplicity = factors.get(i)[1];
      int degree = engine.evaluate(F.Exponent(factor, t)).toIntDefault();
      if (degree > 4) {
        return null;
      }
      IExpr solutions = engine.evaluate(F.Solve(F.Equal(factor, F.C0), t));
      if (!solutions.isListOfLists() || ((IAST) solutions).argSize() != degree) {
        return null;
      }
      for (IExpr solution : (IAST) solutions) {
        if (!solution.isList1() || !solution.first().isRuleAST()) {
          return null;
        }
        IExpr rho = solution.first().second();
        if (!rho.isFree(t) || !rho.isFree(S.Root) || !rho.isFree(S.ConditionalExpression)) {
          return null;
        }
        // the cofactor: the other factors, and this one without (t-rho)
        IASTAppendable cofactor = F.TimesAlloc(factors.size() + 1);
        IExpr reduced = engine.evaluate(
            F.PolynomialQuotient(factor, F.Subtract(t, rho), t));
        cofactor.append(F.Power(reduced, multiplicity));
        for (int k = 0; k < factors.size(); k++) {
          if (k != i) {
            cofactor.append(F.Power(factors.get(k)[0], factors.get(k)[1]));
          }
        }
        roots.add(new IExpr[] {rho, multiplicity, cofactor});
      }
    }
    return roots;
  }

  /** the slopes c of the exponents c*x+d of E; false for an exponent which is not linear */
  private static boolean collectSlopes(IExpr g, IExpr x, List<IExpr> slopes, EvalEngine engine) {
    if (g.isFree(x) || !g.isAST()) {
      return true;
    }
    if (g.isPower() && g.base().isE()) {
      IExpr z = g.exponent();
      if (!z.isPolynomial(x) || engine.evaluate(F.Exponent(z, x)).toIntDefault() != 1) {
        return false;
      }
      IExpr c = engine.evaluate(F.Coefficient(z, x, F.C1));
      if (c.isZero() || !c.isFree(x)) {
        return false;
      }
      slopes.add(c);
      return true;
    }
    for (IExpr arg : (IAST) g) {
      if (!collectSlopes(arg, x, slopes, engine)) {
        return false;
      }
    }
    return true;
  }
}
