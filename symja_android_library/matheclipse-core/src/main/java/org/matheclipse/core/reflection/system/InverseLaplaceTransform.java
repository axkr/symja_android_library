package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import com.google.common.math.DoubleMath;

/**
 * <pre>
 * InverseLaplaceTransform(f, s, t)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns the inverse Laplace transform.
 *
 * </blockquote>
 *
 * <p>
 * See:
 *
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Laplace_transform">Wikipedia - Laplace transform</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; InverseLaplaceTransform(3/(s-1)+(2*s)/(s^2+4),s,t)
 * 3*E^t+2*Cos(2*t)
 * </pre>
 */
public class InverseLaplaceTransform extends AbstractFunctionEvaluator {

  /**
   * Numerical inverse Laplace transform using Stehfest's method.
   *
   * @see <a href=
   *      "https://www.codeproject.com/Articles/25189/Numerical-Laplace-Transforms-and-Inverse-Transform">
   *      Numerical-Laplace-Transforms-and-Inverse-Transform</a>
   */
  private static class InverseLaplaceTransformStehfest {
    private final double[] V;
    static final double ln2 = Math.log(2.0);
    final UnaryNumerical function;

    InverseLaplaceTransformStehfest(UnaryNumerical function) {
      this(function, 16);
    }

    InverseLaplaceTransformStehfest(UnaryNumerical function, int n) {
      this.function = function;
      int N2 = n / 2;
      int NV = 2 * N2;
      V = new double[NV];
      int sign = 1;
      if ((N2 % 2) != 0)
        sign = -1;
      for (int i = 0; i < NV; i++) {
        int kmin = (i + 2) / 2;
        int kmax = i + 1;
        if (kmax > N2)
          kmax = N2;
        V[i] = 0;
        sign = -sign;
        for (int k = kmin; k <= kmax; k++) {
          V[i] = V[i] + (Math.pow(k, N2) / DoubleMath.factorial(k))
              * (DoubleMath.factorial(2 * k) / DoubleMath.factorial(2 * k - i - 1))
              / DoubleMath.factorial(N2 - k) / DoubleMath.factorial(k - 1)
              / DoubleMath.factorial(i + 1 - k);
        }
        V[i] = sign * V[i];
      }
    }

    public double inverseTransform(double time) {
      if (time == 0.0) {
        time = Config.DOUBLE_EPSILON;
      } else if (time == -0.0) {
        time = -Config.DOUBLE_EPSILON;
      }
      double ln2t = ln2 / time;
      double x = 0;
      double y = 0;
      for (int i = 0; i < V.length; i++) {
        x += ln2t;
        y += V[i] * function.valueLimit(x);
      }
      return ln2t * y;
    }
  }

  public InverseLaplaceTransform() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // Numeric 2-arg form: InverseLaplaceTransform(f, tVal)
    if (ast.isAST2()) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      if (arg1.isNumericFunction(true)) {
        return numericInverseLaplaceTransform(arg1, F.Dummy("s"), arg2.evalDouble(), engine);
      }
    }

    if (ast.argSize() != 3) {
      return F.NIL;
    }
    IExpr f = ast.arg1();
    IExpr s = ast.arg2();
    IExpr t = ast.arg3();

    if (s.isList() && t.isList() && s.argSize() == t.argSize() && s.argSize() > 0) {
      // InverseLaplaceTransform(f, {s1, s2}, {t1, t2}) is the transform in one variable at a time
      IExpr result = f;
      for (int i = 1; i <= s.argSize(); i++) {
        result = engine.evaluate(F.InverseLaplaceTransform(result, s.getAt(i), t.getAt(i)));
        if (result.has(S.InverseLaplaceTransform)) {
          return F.NIL;
        }
      }
      return result;
    }

    if (!s.isSymbol()) {
      return F.NIL;
    }

    // Linearity: constant free of s => c * DiracDelta(t)
    if (f.isFree(s)) {
      return F.Times(f, F.DiracDelta(t));
    }

    // Linearity: distribute over sums
    if (f.isPlus()) {
      return f.mapThread(F.InverseLaplaceTransform(F.Slot1, s, t), 1);
    }

    // Linearity: pull out factors free of s
    if (f.isTimes()) {
      IASTAppendable consts = F.TimesAlloc();
      IASTAppendable nonConsts = F.TimesAlloc();
      IAST times = (IAST) f;
      for (int i = 1; i <= times.argSize(); i++) {
        IExpr arg = times.get(i);
        if (arg.isFree(s)) {
          consts.append(arg);
        } else {
          nonConsts.append(arg);
        }
      }
      if (consts.argSize() > 0) {
        IExpr c = consts.argSize() == 1 ? consts.arg1() : consts;
        IExpr nc = nonConsts.argSize() == 0 ? F.C1
            : (nonConsts.argSize() == 1 ? nonConsts.arg1() : nonConsts);
        return engine.evaluate(F.Times(c, F.InverseLaplaceTransform(nc, s, t)));
      }
    }

    // ---- Algebraic normalization pipeline (BEFORE time shift) ----
    // This handles: Expand numerator, Factor denominator, Cancel E^(a*s) terms, then Apart
    // This ensures E^(Pi*s) in num/denom cancel before we check for time shifts
    IExpr simplified = simplifyRationalForm(f, s, engine);
    if (!simplified.equals(f)) {
      if (simplified.isPlus()) {
        return simplified.mapThread(F.InverseLaplaceTransform(F.Slot1, s, t), 1);
      }
      // Recursively evaluate the simplified form
      return engine.evaluate(F.InverseLaplaceTransform(simplified, s, t));
    }

    // ---- Try rational function decomposition (the core algorithm, like SymPy) ----
    IExpr rationalResult = tryRational(f, s, t, engine);
    if (rationalResult.isPresent()) {
      return rationalResult;
    }

    // ---- Time shift: E^(a*s) * G(s) where a is free of s ----
    // Apply AFTER rational simplification to avoid false time-shift matches
    IExpr timeShiftResult = tryTimeShift(f, s, t, engine);
    if (timeShiftResult.isPresent()) {
      return timeShiftResult;
    }

    // ---- Simple table rules (non-rational, e.g. (s+a)^n with symbolic n) ----
    IExpr simpleResult = trySimpleRules(f, s, t, engine);
    if (simpleResult.isPresent()) {
      return simpleResult;
    }

    return F.NIL;
  }

  // =====================================================================
  // Rational simplification pipeline: cancel exponentials, expand/factor, then Apart
  // This mimics the original lines 163-182 to handle cases like:
  // (E^(Pi*s)*s) / (E^(Pi*s)*(1+s^2)) => s/(1+s^2)
  // 1 / (E^(Pi*s)*(1+s^2)) => E^(-Pi*s)/(1+s^2)
  // =====================================================================
  private static IExpr simplifyRationalForm(IExpr f, IExpr s, EvalEngine engine) {
    // Only process rational functions
    IExpr num = engine.evaluate(F.Numerator(f));
    IExpr den = engine.evaluate(F.Denominator(f));

    // Check that both are polynomial or have exponential factors
    if (!num.isPolynomial(s) && num.isFree(x -> x.isExp(), false)) {
      return f;
    }
    if (!den.isPolynomial(s) && den.isFree(x -> x.isExp(), false)) {
      return f;
    }

    // Expand numerator and factor denominator to expose common factors like E^(a*s)
    IExpr expNum = engine.evaluate(F.Expand(num));
    IExpr facDen = engine.evaluate(F.Factor(den));
    // If numerator is a sum with mixed exponential/non-exponential terms,
    // distribute the fraction to separate them before exponential extraction
    if (expNum.isPlus()) {
      IAST plus = (IAST) expNum;
      boolean hasExpTerms = false;
      boolean hasNonExpTerms = false;

      for (int i = 1; i <= plus.argSize(); i++) {
        IExpr term = plus.get(i);
        if (term.isFree(x -> x.isExp(), false)) {
          hasNonExpTerms = true;
        } else {
          hasExpTerms = true;
        }
      }

      // If mixed, distribute: (a + E*b + E*c)/den = a/den + E*b/den + E*c/den
      if (hasExpTerms && hasNonExpTerms) {
        IASTAppendable distributedSum = F.PlusAlloc(plus.argSize());
        for (int i = 1; i <= plus.argSize(); i++) {
          IExpr term = plus.get(i);
          distributedSum.append(F.Divide(term, facDen));
        }
        // Return as a sum so linearity in evaluate() distributes the ILT
        return distributedSum;
      }
    }


    // Try to extract exponential factors from numerator and denominator
    ExponentialFactorExtraction numExp = extractExponentialFactor(expNum, s, engine);
    ExponentialFactorExtraction denExp = extractExponentialFactor(facDen, s, engine);

    IExpr simplifiedNum = numExp.remainder;
    IExpr simplifiedDen = denExp.remainder;
    IExpr numExponent = numExp.exponent; // a from E^(a*s) in numerator
    IExpr denExponent = denExp.exponent; // a from E^(a*s) in denominator

    // Cancel matching exponential factors and combine into net exponential shift
    IExpr netExponent = null;
    if (numExponent != null && denExponent != null) {
      // Both have exponential factors: E^(num_a) / E^(den_a) = E^(num_a - den_a)
      netExponent = engine.evaluate(F.Subtract(numExponent, denExponent));
    } else if (denExponent != null && numExponent == null) {
      // Only denominator has exponential: 1 / E^(den_a) = E^(-den_a)
      netExponent = engine.evaluate(F.Negate(denExponent));
    } else if (numExponent != null && denExponent == null) {
      // Only numerator has exponential: E^(num_a) / 1 = E^(num_a)
      netExponent = numExponent;
    }

    // Build the simplified fraction
    IExpr fraction = engine.evaluate(F.Divide(simplifiedNum, simplifiedDen));

    // If we extracted an exponential factor, attach it back
    if (netExponent != null && !netExponent.isZero()) {
      IExpr fullExponent = engine.evaluate(F.Times(netExponent, s));
      fraction = engine.evaluate(F.Times(F.Exp(fullExponent), fraction));
    }

    // Try partial fraction decomposition if it's rational
    if (isRationalFunction(fraction, s, engine)) {
      IExpr apart = engine.evaluate(F.Apart(fraction, s));
      if (!apart.equals(f)) {
        return apart;
      }
    }

    // If fraction has form E^(a*s) * G(s) where G is rational, return it
    if (fraction.isTimes()) {
      IAST times = (IAST) fraction;
      boolean hasExpFactor = false;
      for (int i = 1; i <= times.argSize(); i++) {
        IExpr arg = times.get(i);
        if (arg.isExp()) {
          hasExpFactor = true;
          break;
        }
      }
      if (hasExpFactor) {
        return fraction;
      }
    }

    return f;
  }

  // =====================================================================
  // Helper class to hold exponential factor extraction result
  // =====================================================================
  private static class ExponentialFactorExtraction {
    IExpr exponent; // The coefficient 'a' from E^(a*s), or null if none
    IExpr remainder; // The remainder after extracting E^(a*s)

    ExponentialFactorExtraction(IExpr exp, IExpr rem) {
      this.exponent = exp;
      this.remainder = rem;
    }
  }

  /**
   * Try to extract an exponential factor E^(a*s) from an expression. If found, return the exponent
   * 'a' and the remainder. If not found, return (null, original_expression).
   */
  private static ExponentialFactorExtraction extractExponentialFactor(IExpr expr, IExpr s,
      EvalEngine engine) {
    // Case 1: expr is a product containing E^(a*s)
    if (expr.isTimes()) {
      IAST times = (IAST) expr;
      IASTAppendable nonExpFactors = F.TimesAlloc();
      for (int i = 1; i <= times.argSize(); i++) {
        IExpr arg = times.get(i);
        if (arg.isExp()) {
          // This is E^(something)
          IExpr exponent = arg.exponent();
          // Check if exponent is linear in s: a*s
          IExpr a = extractLinearCoeff(exponent, s, engine);
          if (a != null) {
            // Found E^(a*s), collect remaining factors
            for (int j = 1; j <= times.argSize(); j++) {
              if (i != j) {
                nonExpFactors.append(times.get(j));
              }
            }
            IExpr remainder = nonExpFactors.argSize() == 0 ? F.C1
                : (nonExpFactors.argSize() == 1 ? nonExpFactors.arg1() : nonExpFactors);
            return new ExponentialFactorExtraction(a, remainder);
          }
        }
      }
    }

    // Case 2: expr is E^(a*s) itself
    if (expr.isExp()) {
      IExpr exponent = expr.exponent();
      IExpr a = extractLinearCoeff(exponent, s, engine);
      if (a != null) {
        return new ExponentialFactorExtraction(a, F.C1);
      }
    }

    // Case 3: expr contains no exponential factor
    return new ExponentialFactorExtraction(null, expr);
  }


  // =====================================================================
  // Time shift rule: L^{-1}{ E^(a*s) * G(s) }
  // a < 0 => HeavisideTheta(t + a) * L^{-1}{G(s)}(t + a)
  // E^(a*s) alone => DiracDelta(t + a)
  // =====================================================================
  private static IExpr tryTimeShift(IExpr f, IExpr s, IExpr t, EvalEngine engine) {
    if (f.isFree(x -> x.isExp(), false)) {
      return F.NIL;
    }

    // Match E^(a*s) as a standalone
    if (f.isExp()) {
      IExpr exponent = f.exponent();
      // exponent = a * s where a is free of s
      IExpr a = extractLinearCoeff(exponent, s, engine);
      if (a != null) {
        // E^(a*s) => DiracDelta(t + a)
        return F.DiracDelta(F.Plus(t, a));
      }
    }

    // Match E^(a*s) * G(s) in a Times
    if (f.isTimes()) {
      IAST times = (IAST) f;
      IExpr expShift = null;
      IASTAppendable rest = F.TimesAlloc();
      for (int i = 1; i <= times.argSize(); i++) {
        IExpr factor = times.get(i);
        if (expShift == null && factor.isExp()) {
          IExpr exponent = factor.exponent();
          IExpr a = extractLinearCoeff(exponent, s, engine);
          if (a != null) {
            expShift = a;
            continue;
          }
        }
        rest.append(factor);
      }
      if (expShift != null) {
        IExpr g = rest.argSize() == 1 ? rest.arg1() : rest;
        // L^{-1}{G(s)}(t + a) with HeavisideTheta(t + a)
        IExpr innerILT = engine.evaluate(F.InverseLaplaceTransform(g, s, t));
        if (innerILT.has(S.InverseLaplaceTransform)) {
          // Could not resolve inner transform
          return F.NIL;
        }
        IExpr shifted = engine.evaluate(F.subst(innerILT, t, F.Plus(t, expShift)));
        if (shifted.isPresent()) {
          return F.Times(F.HeavisideTheta(F.Plus(t, expShift)), shifted);
        }
      }
    }

    return F.NIL;
  }

  /**
   * If {@code expr} is of the form {@code a * s} where {@code a} is free of {@code s}, return
   * {@code a}. Otherwise return {@code null}.
   */
  private static IExpr extractLinearCoeff(IExpr expr, IExpr s, EvalEngine engine) {
    // expr = a * s
    IExpr coeff = engine.evaluate(F.Coefficient(expr, s, F.C1));
    if (!coeff.isZero()) {
      IExpr remainder = engine.evaluate(F.Subtract(expr, F.Times(coeff, s)));
      if (remainder.isZero() && coeff.isFree(s)) {
        return coeff;
      }
    }
    return null;
  }

  // =====================================================================
  // Rational function decomposition (following SymPy's _inverse_laplace_rational)
  //
  // After partial fractions, each term is N(s)/D(s) where D is monic.
  // deg(D)=1: N / (s + a) => N * exp(-a*t)
  // deg(D)=2: (l*s + m) / (s^2 + p*s + q) =>
  // complete the square, then cos/sin or cosh/sinh
  // deg(D)=0: polynomial in s => DiracDelta derivatives
  // =====================================================================
  private static IExpr tryRational(IExpr f, IExpr s, IExpr t, EvalEngine engine) {
    // Check that f is a rational function in s
    if (!isRationalFunction(f, s, engine)) {
      return F.NIL;
    }

    IExpr apart = engine.evaluate(F.Apart(f, s));
    if (apart.isPlus()) {
      // Transform each partial fraction term individually
      IASTAppendable result = F.PlusAlloc(((IAST) apart).argSize());
      for (int i = 1; i <= ((IAST) apart).argSize(); i++) {
        IExpr term = ((IAST) apart).get(i);
        IExpr transformed = transformRationalTerm(term, s, t, engine);
        if (!transformed.isPresent()) {
          return F.NIL;
        }
        result.append(transformed);
      }
      return result;
    }
    // Single term after Apart
    return transformRationalTerm(apart, s, t, engine);
  }

  /**
   * Transform a single partial-fraction term {@code n(s)/d(s)}.
   */
  private static IExpr transformRationalTerm(IExpr term, IExpr s, IExpr t, EvalEngine engine) {
    IExpr num = engine.evaluate(F.Numerator(term));
    IExpr den = engine.evaluate(F.Denominator(term));

    // Get polynomial coefficients of denominator in s (highest degree first)
    IExpr[] dc = polynomialCoeffsDescending(den, s, engine);
    if (dc == null) {
      return F.NIL;
    }

    // Make monic: divide all coefficients by leading coefficient
    IExpr lead = dc[0];
    for (int i = 0; i < dc.length; i++) {
      dc[i] = engine.evaluate(F.Divide(dc[i], lead));
    }

    // Get polynomial coefficients of numerator, also divided by leading coeff of denom
    IExpr[] nc = polynomialCoeffsDescending(num, s, engine);
    if (nc == null) {
      return F.NIL;
    }
    for (int i = 0; i < nc.length; i++) {
      nc[i] = engine.evaluate(F.Divide(nc[i], lead));
    }

    int degD = dc.length - 1;

    if (degD > 0 && nc.length - 1 >= degD) {
      // An improper fraction, which Apart leaves whole when a coefficient is inexact:
      // (2.0*s)/(3.0 + 2.0*s). The branches below read the numerator as having a lower degree than
      // the denominator, and took the coefficient of s as the constant, which answered E^(-1.5*t)
      // for s/(s + 1.5) instead of DiracDelta(t) - 1.5*E^(-1.5*t). Divide first - by hand, on the
      // coefficients, since PolynomialQuotientRemainder does not take inexact ones.
      int quotientLength = nc.length - degD;
      IExpr[] rest = nc.clone();
      IASTAppendable quotient = F.PlusAlloc(quotientLength);
      for (int i = 0; i < quotientLength; i++) {
        IExpr q = rest[i];
        quotient.append(F.Times(q, F.Power(s, F.ZZ(quotientLength - 1 - i))));
        for (int j = 0; j <= degD; j++) {
          rest[i + j] = engine.evaluate(F.Subtract(rest[i + j], F.Times(q, dc[j])));
        }
      }
      IASTAppendable remainder = F.PlusAlloc(degD);
      IASTAppendable monic = F.PlusAlloc(degD + 1);
      for (int k = 0; k < degD; k++) {
        remainder.append(F.Times(rest[quotientLength + k], F.Power(s, F.ZZ(degD - 1 - k))));
      }
      for (int j = 0; j <= degD; j++) {
        monic.append(F.Times(dc[j], F.Power(s, F.ZZ(degD - j))));
      }
      IExpr polynomialPart =
          engine.evaluate(F.InverseLaplaceTransform(engine.evaluate(quotient), s, t));
      IExpr remainderPolynomial = engine.evaluate(remainder);
      IExpr properPart = remainderPolynomial.isZero() ? F.C0
          : engine.evaluate(F.InverseLaplaceTransform(
              F.Divide(remainderPolynomial, engine.evaluate(monic)), s, t));
      if (polynomialPart.has(S.InverseLaplaceTransform)
          || properPart.has(S.InverseLaplaceTransform)) {
        return F.NIL;
      }
      return engine.evaluate(F.Plus(polynomialPart, properPart));
    }

    // deg(D) == 0: polynomial in s => sum of DiracDelta derivatives
    if (degD == 0) {
      // nc are coefficients: nc[0]*s^N + nc[1]*s^(N-1) + ... + nc[N]
      int N = nc.length - 1;
      IASTAppendable result = F.PlusAlloc(nc.length);
      for (int i = 0; i < nc.length; i++) {
        if (!nc[i].isZero()) {
          int derivOrder = N - i;
          if (derivOrder == 0) {
            result.append(F.Times(nc[i], F.DiracDelta(t)));
          } else {
            // DiracDelta^(n)(t) represented as Derivative[n][DiracDelta][t]
            result.append(F.Times(nc[i], F.unaryAST1(
                F.unaryAST1(F.Derivative(F.ZZ(derivOrder)), S.DiracDelta), t)));
          }
        }
      }
      return engine.evaluate(result);
    }

    // deg(D) == 1: 1/(s + a) => e^(-a*t)
    // term = nc[0] / (s + dc[1]) => nc[0] * e^(-dc[1] * t)
    if (degD == 1) {
      IExpr a = dc[1]; // dc = [1, a] means (s + a)
      IExpr r = F.Times(nc[0], F.Exp(F.Times(F.CN1, a, t)));
      return engine.evaluate(r);
    }

    // deg(D) == 2: (l*s + m) / (s^2 + p*s + q)
    // Complete the square: s^2 + p*s + q = (s + p/2)^2 + (q - p^2/4)
    if (degD == 2) {
      IExpr p = dc[1]; // coefficient of s
      IExpr q = dc[2]; // constant
      IExpr a = engine.evaluate(F.Divide(p, F.C2)); // shift
      IExpr bSq = engine.evaluate(F.Subtract(q, F.Power(a, F.C2))); // q - a^2

      // Pad numerator to degree 1 if constant
      IExpr l, m;
      if (nc.length == 1) {
        l = F.C0;
        m = nc[0];
      } else {
        l = nc[0];
        m = nc[1];
      }

      // b^2 = 0 => repeated root: (m*t + l*(1 - a*t)) * e^(-a*t)
      if (engine.evaluate(bSq).isZero()) {
        IExpr r = F.Times(F.Plus(F.Times(m, t), F.Times(l, F.Subtract(F.C1, F.Times(a, t)))),
            F.Exp(F.Times(F.CN1, a, t)));
        return engine.evaluate(r);
      }

      // b^2 < 0 => hyperbolic (real distinct roots)
      IExpr bSqEvaled = engine.evaluate(bSq);
      boolean hyp = bSqEvaled.isNegativeResult();
      IExpr bSqPos = hyp ? engine.evaluate(F.Negate(bSqEvaled)) : bSqEvaled;
      IExpr b = engine.evaluate(F.Sqrt(bSqPos));
      // Simplify expressions like Sqrt(a^2) to a
      b = engine.evaluate(F.PowerExpand(b));

      IExpr r;
      IExpr expPart = F.Exp(F.Times(F.CN1, a, t));
      IExpr mMinusAL = engine.evaluate(F.Subtract(m, F.Times(a, l)));
      if (hyp) {
        // l*e^(-a*t)*cosh(b*t) + (m - a*l)/b * e^(-a*t)*sinh(b*t)
        r = F.Plus(F.Times(l, expPart, F.Cosh(F.Times(b, t))),
            F.Times(F.Divide(mMinusAL, b), expPart, F.Sinh(F.Times(b, t))));
      } else {
        // l*e^(-a*t)*cos(b*t) + (m - a*l)/b * e^(-a*t)*sin(b*t)
        r = F.Plus(F.Times(l, expPart, F.Cos(F.Times(b, t))),
            F.Times(F.Divide(mMinusAL, b), expPart, F.Sin(F.Times(b, t))));
      }
      return engine.evaluate(r);
    }

    // deg(D) >= 3: a power of a quadratic, which Apart leaves as one term, or the generic pipeline
    return powerOfQuadratic(num, den, s, t, engine);
  }

  /** The highest power of a quadratic denominator {@link #powerOfQuadratic} inverts. */
  private static final int MAX_QUADRATIC_POWER = 8;

  /**
   * The inverse transform of <code>(l*s + m)/(c*Q^n)</code> for a quadratic <code>Q</code> in
   * <code>s</code> with two distinct roots and <code>n &gt;= 2</code>, or {@link F#NIL}.
   *
   * <p>
   * Written as <code>Q == alpha*((s + a)^2 + b2)</code>, the shift <code>a</code> is a factor
   * <code>E^(-a*t)</code>, and for <code>g(n) == L^-1{1/(s^2 + b2)^n}</code> and
   * <code>h(n) == L^-1{s/(s^2 + b2)^n}</code> differentiating in <code>s</code> gives
   * <code>h(n+1) == t*g(n)/(2*n)</code>, while
   * <code>1/(s^2+b2)^(n+1) == (1/(s^2+b2)^n - s*s/(s^2+b2)^(n+1))/b2</code> gives
   * <code>g(n+1) == (g(n) - D(h(n+1), t))/b2</code>, as <code>h(n+1)</code> vanishes at 0. This is
   * the term <code>1/(1+s^2)^2</code> a resonant forcing leaves, <code>(Sin(t) - t*Cos(t))/2</code>.
   */
  private static IExpr powerOfQuadratic(IExpr num, IExpr den, IExpr s, IExpr t,
      EvalEngine engine) {
    IExpr power = F.NIL;
    IExpr constant = F.C1;
    if (den.isPower()) {
      power = den;
    } else if (den.isTimes()) {
      IAST times = (IAST) den;
      int index = times.indexOf(x -> x.isPower() && !x.isFree(s));
      if (index <= 0) {
        return F.NIL;
      }
      power = times.get(index);
      constant = times.removeAtCopy(index).oneIdentity1();
      if (!constant.isFree(s)) {
        return F.NIL;
      }
    }
    if (power.isNIL() || !power.exponent().isInteger()) {
      return F.NIL;
    }
    int n = power.exponent().toIntDefault();
    if (n < 2 || n > MAX_QUADRATIC_POWER) {
      return F.NIL;
    }
    IExpr[] qc = polynomialCoeffsDescending(power.base(), s, engine);
    IExpr[] nc = polynomialCoeffsDescending(num, s, engine);
    if (qc == null || qc.length != 3 || nc == null || nc.length > 2) {
      return F.NIL;
    }
    IExpr alpha = qc[0];
    IExpr a = engine.evaluate(F.Divide(qc[1], F.Times(F.C2, alpha)));
    IExpr b2 = engine.evaluate(F.Subtract(F.Divide(qc[2], alpha), F.Sqr(a)));
    if (b2.isZero() || !b2.isRealResult()) {
      return F.NIL;
    }
    boolean hyperbolic = b2.isNegativeResult();
    if (!hyperbolic && !b2.isPositiveResult()) {
      return F.NIL;
    }
    IExpr b = engine.evaluate(F.PowerExpand(F.Sqrt(hyperbolic ? F.Negate(b2) : b2)));
    IExpr g = hyperbolic ? F.Divide(F.Sinh(F.Times(b, t)), b)
        : F.Divide(F.Sin(F.Times(b, t)), b);
    IExpr h = F.NIL;
    for (int k = 1; k < n; k++) {
      h = engine.evaluate(F.Divide(F.Times(t, g), F.ZZ(2 * k)));
      g = engine.evaluate(F.Expand(F.Divide(F.Subtract(g, F.D(h, t)), b2)));
    }
    IExpr l = nc.length == 2 ? nc[0] : F.C0;
    IExpr m = nc.length == 2 ? nc[1] : nc[0];
    // l*s + m == l*(s + a) + (m - a*l)
    IExpr body = F.Plus(F.Times(l, h), F.Times(F.Subtract(m, F.Times(a, l)), g));
    return engine.evaluate(F.Expand(F.Divide(F.Times(F.Exp(F.Times(F.CN1, a, t)), body),
        F.Times(constant, F.Power(alpha, F.ZZ(n))))));
  }

  // =====================================================================
  // Simple table-based rules for non-rational expressions
  //
  // 1/s^b => t^(b-1) / Gamma(b)
  // (s+a)^(-c) => t^(c-1)*E^(-a*t) / Gamma(c)
  // s (the variable itself) => DiracDelta'(t)
  // =====================================================================
  private static IExpr trySimpleRules(IExpr f, IExpr s, IExpr t, EvalEngine engine) {
    // Rule: f == s => DiracDelta'(t)
    if (f.equals(s)) {
      return F.unaryAST1(F.unaryAST1(F.Derivative(F.C1), S.DiracDelta), t);
    }

    // Rule: f == 1/s^b => t^(b-1) / Gamma(b), where b is free of s
    // Matches s^(-n) for positive integer n as well as general b
    if (f.isPower() && f.base().equals(s) && f.exponent().isFree(s)) {
      IExpr negB = f.exponent(); // this is -b since f = s^(-b)
      IExpr b = engine.evaluate(F.Negate(negB));
      if (engine.evaluate(F.Greater(b, F.C0)).isTrue()) {
        // t^(b-1) / Gamma(b)
        return engine.evaluate(F.Divide(F.Power(t, F.Subtract(b, F.C1)), F.Gamma(b)));
      }
      // Negative exponent means s^n for positive n => DiracDelta derivatives
      if (negB.isInteger() && ((IInteger) negB).isPositive()) {
        int n = negB.toIntDefault();
        return F.unaryAST1(F.Derivative(F.ZZ(n)), S.DiracDelta).apply(t);
      }
    }

    // Rule: b * (s+a)^(-c) => b * t^(c-1) * E^(-a*t) / Gamma(c)
    // where a, b, c are all free of s
    // This handles the general shifted power rule.
    if (f.isPower()) {
      // f = base^exponent, try base = s + a, exponent = -c
      IExpr base = f.base();
      IExpr exp = f.exponent();
      if (base.isPlus() && exp.isFree(s)) {
        // base = s + a (extract terms)
        IExpr a = extractAdditiveConst(base, s, engine);
        if (a != null) {
          IExpr c = engine.evaluate(F.Negate(exp));
          // t^(c-1) * E^(-a*t) / Gamma(c)
          return engine.evaluate(F.Divide(
              F.Times(F.Power(t, F.Subtract(c, F.C1)), F.Exp(F.Times(F.CN1, a, t))), F.Gamma(c)));
        }
        // base = k*s + d == k*(s + d/k), with k free of s: 1/Sqrt(1 + p*q) in p
        IExpr k = engine.evaluate(F.Coefficient(base, s, F.C1));
        IExpr d = engine.evaluate(F.Subtract(base, F.Times(k, s)));
        if (!k.isZero() && !k.isOne() && k.isFree(s) && d.isFree(s)) {
          IExpr c = engine.evaluate(F.Negate(exp));
          IExpr shift = engine.evaluate(F.Divide(d, k));
          return engine.evaluate(F.Times(F.Power(k, exp), F.Divide(
              F.Times(F.Power(t, F.Subtract(c, F.C1)), F.Exp(F.Times(F.CN1, shift, t))),
              F.Gamma(c))));
        }
      }
    }

    IExpr bessel = inverseOfSqrtQuadratic(f, s, t, engine);
    if (bessel.isPresent()) {
      return bessel;
    }
    return inverseOfExponentialOfReciprocal(f, s, t, engine);
  }

  /**
   * <code>L^-1{1/Sqrt(alpha*((s+b)^2 + c))} == E^(-b*t)*BesselJ(0, Sqrt(c)*t)/Sqrt(alpha)</code>,
   * written with <code>BesselI(0, Sqrt(-c)*t)</code> when <code>c</code> is negative, or
   * {@link F#NIL}.
   */
  private static IExpr inverseOfSqrtQuadratic(IExpr f, IExpr s, IExpr t, EvalEngine engine) {
    if (!f.isPower() || !f.exponent().equals(F.CN1D2)) {
      return F.NIL;
    }
    IExpr[] qc = polynomialCoeffsDescending(f.base(), s, engine);
    if (qc == null || qc.length != 3) {
      return F.NIL;
    }
    IExpr alpha = qc[0];
    IExpr b = engine.evaluate(F.Divide(qc[1], F.Times(F.C2, alpha)));
    IExpr c = engine.evaluate(F.Subtract(F.Divide(qc[2], alpha), F.Sqr(b)));
    if (c.isZero()) {
      return F.NIL;
    }
    IExpr negated = engine.evaluate(F.Negate(c));
    boolean modified = c.isNegativeResult() || (c.isTimes() && c.first().isNegative());
    IExpr root = engine.evaluate(F.PowerExpand(F.Sqrt(modified ? negated : c)));
    IExpr function = modified ? F.BesselI(F.C0, F.Times(root, t)) : F.BesselJ(F.C0, F.Times(root, t));
    return engine.evaluate(
        F.Divide(F.Times(F.Exp(F.Times(F.CN1, b, t)), function), F.Sqrt(alpha)));
  }

  /**
   * <code>L^-1{s^(-nu)*E^(-k/s)} == (t/k)^((nu-1)/2)*BesselJ(nu-1, 2*Sqrt(k*t))</code> for
   * <code>nu &gt; 0</code>, or {@link F#NIL}. The inverse of <code>1/(1 + p*q)</code> in
   * <code>p</code> is <code>E^(-x/q)/q</code>, whose inverse in <code>q</code> this is.
   */
  private static IExpr inverseOfExponentialOfReciprocal(IExpr f, IExpr s, IExpr t,
      EvalEngine engine) {
    IAST factors = f.isTimes() ? (IAST) f : F.Times(f);
    IExpr k = F.NIL;
    IExpr nu = F.C0;
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isExp() && k.isNIL()) {
        IExpr reciprocal = engine.evaluate(F.Negate(F.Times(factor.exponent(), s)));
        if (!reciprocal.isFree(s)) {
          return F.NIL;
        }
        k = reciprocal;
      } else if (factor.equals(s)) {
        nu = engine.evaluate(F.Subtract(nu, F.C1));
      } else if (factor.isPower() && factor.base().equals(s) && factor.exponent().isFree(s)) {
        nu = engine.evaluate(F.Subtract(nu, factor.exponent()));
      } else {
        return F.NIL;
      }
    }
    if (k.isNIL() || k.isZero() || !engine.evaluate(F.Greater(nu, F.C0)).isTrue()) {
      return F.NIL;
    }
    IExpr order = engine.evaluate(F.Subtract(nu, F.C1));
    return engine.evaluate(F.Times(F.Power(F.Divide(t, k), F.Divide(order, F.C2)),
        F.BesselJ(order, F.Times(F.C2, F.Sqrt(F.Times(k, t))))));
  }

  /**
   * If {@code expr} is of the form {@code s + a} where {@code a} is free of {@code s} (and the
   * coefficient of {@code s} is 1), return {@code a}. Otherwise return {@code null}.
   */
  private static IExpr extractAdditiveConst(IExpr expr, IExpr s, EvalEngine engine) {
    IExpr coeff = engine.evaluate(F.Coefficient(expr, s, F.C1));
    if (coeff.isOne()) {
      IExpr remainder = engine.evaluate(F.Subtract(expr, s));
      if (remainder.isFree(s)) {
        return remainder;
      }
    }
    return null;
  }

  /**
   * Check if {@code f} is a rational function in {@code s}.
   */
  private static boolean isRationalFunction(IExpr f, IExpr s, EvalEngine engine) {
    IExpr num = engine.evaluate(F.Numerator(f));
    IExpr den = engine.evaluate(F.Denominator(f));
    return num.isPolynomial(s) && den.isPolynomial(s);
  }

  /**
   * Return coefficients of polynomial in {@code s} in descending order of degree. Returns
   * {@code null} if not a polynomial.
   */
  private static IExpr[] polynomialCoeffsDescending(IExpr poly, IExpr s, EvalEngine engine) {
    if (!poly.isPolynomial(s)) {
      return null;
    }
    IExpr exponent = engine.evaluate(F.Exponent(poly, s));
    if (!exponent.isInteger() || exponent.isNegative()) {
      return null;
    }
    int deg = exponent.toIntDefault();
    IExpr[] coeffs = new IExpr[deg + 1];
    for (int i = deg; i >= 0; i--) {
      coeffs[deg - i] = engine.evaluate(F.Coefficient(poly, s, F.ZZ(i)));
    }
    return coeffs;
  }

  private static IExpr numericInverseLaplaceTransform(IExpr function, IExpr s, double t,
      EvalEngine engine) {
    final IAST cacheKey = F.List(S.InverseLaplaceTransform, function, s);
    Object value = engine.getObjectCache(cacheKey);
    final InverseLaplaceTransformStehfest laplace;
    if (value instanceof InverseLaplaceTransformStehfest) {
      laplace = (InverseLaplaceTransformStehfest) value;
    } else {
      final UnaryNumerical unaryNumerical =
          new UnaryNumerical(function, (ISymbol) s, false, Double.NaN, engine);
      laplace = new InverseLaplaceTransformStehfest(unaryNumerical);
      engine.putObjectCache(cacheKey, laplace);
    }
    return F.num(laplace.inverseTransform(t));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
