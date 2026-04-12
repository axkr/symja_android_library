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
            result.append(F.Times(nc[i],
                F.unaryAST1(F.unaryAST1(F.unaryAST1(S.Derivative, F.ZZ(derivOrder)), S.DiracDelta),
                    t)));
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

    // deg(D) >= 3: fall through, let the generic pipeline handle it
    return F.NIL;
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
      return F.unaryAST1(F.unaryAST1(F.unaryAST1(S.Derivative, F.C1), S.DiracDelta), t);
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
        return F.unaryAST1(F.unaryAST1(S.Derivative, F.ZZ(n)), S.DiracDelta).apply(t);
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
      }
    }

    return F.NIL;
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
          new UnaryNumerical(function, (ISymbol) s, false, engine);
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
