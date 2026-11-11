package org.matheclipse.core.numbertheory;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;

/**
 * Cosines of rational multiples of <code>Pi</code> with the denominator a Fermat prime
 * <code>p</code> (or <code>2*p</code>) in nested square roots, by Gauss's descent through the
 * periods of the <code>p</code>-th roots of unity.
 *
 * <p>
 * With a primitive root <code>g</code> modulo <code>p</code>, the <code>e</code> periods of length
 * <code>f = (p-1)/e</code> are <code>P(e,j) = Sum(z^(g^(j+e*t)), {t, 0, f-1})</code>,
 * <code>z = Exp(2*Pi*I/p)</code>. A period <code>P(e,j)</code> is the sum of the two periods
 * <code>P(2*e,j)</code> and <code>P(2*e,j+e)</code>, and the square of their difference is
 * invariant under the subgroup of the periods of length <code>f</code>: an integer combination of
 * the periods <code>P(e,k)</code>, found by counting the exponents of the product (no product of
 * radicals is formed). The periods of length <code>2</code> are <code>2*Cos(2*Pi*g^j/p)</code>.
 */
public final class GaussPeriods {

  /** The Fermat primes whose cosines are expanded, with a primitive root. */
  private static final int[][] FERMAT_PRIMES = {{17, 3}, {257, 3}};

  private GaussPeriods() {}

  /**
   * <code>Cos(r*Pi)</code> in nested square roots, if the denominator of <code>r</code> is a
   * Fermat prime <code>p</code> of {@link #FERMAT_PRIMES} or <code>2*p</code>.
   *
   * @param r the rational factor of <code>Pi</code>
   * @param maxPrime the largest prime which is expanded
   * @return {@link F#NIL} if the denominator doesn't have this form
   */
  public static IExpr cosine(IRational r, int maxPrime, EvalEngine engine) {
    IInteger denominator = r.denominator();
    int q = denominator.toIntDefault();
    if (q <= 0) {
      return F.NIL;
    }
    boolean half = q % 2 == 0;
    int p = half ? q / 2 : q;
    int generator = 0;
    for (int[] fermat : FERMAT_PRIMES) {
      if (fermat[0] == p && p <= maxPrime) {
        generator = fermat[1];
      }
    }
    if (generator == 0) {
      return F.NIL;
    }
    // Cos(r*Pi) == Cos(n*Pi/q): reduce n modulo 2*q
    int n = r.numerator().mod(F.ZZ(2L * q)).toIntDefault();
    double value = Math.cos(Math.PI * n / q);
    if (!half) {
      // Cos(n*Pi/p): n even is Cos(2*Pi*(n/2)/p), n odd is -Cos(2*Pi*((n+p)/2)/p)
      return n % 2 == 0 ? halfPeriod(n / 2, p, generator, engine)
          : engine.evaluate(F.Expand(F.Negate(halfPeriod((n + p) / 2, p, generator, engine))));
    }
    // Cos(n*Pi/(2*p)) with n odd: +-Sqrt((1 + Cos(n*Pi/p))/2)
    IExpr doubled = cosine(F.QQ(n, p), maxPrime, engine);
    if (doubled.isNIL()) {
      return F.NIL;
    }
    IExpr root = clearedSqrt(engine.evaluate(F.Times(F.C1D2, F.Plus(F.C1, doubled))), engine);
    return value < 0 ? engine.evaluate(F.Negate(root)) : root;
  }

  /** <code>Cos(2*Pi*a/p)</code>, half of the period of length 2 which contains <code>a</code>. */
  private static IExpr halfPeriod(int a, int p, int g, EvalEngine engine) {
    a = Math.floorMod(a, p);
    if (a == 0) {
      return F.C1;
    }
    IExpr[] periods = periodsOfLengthTwo(p, g, engine);
    int[] index = discreteLogarithms(p, g);
    int e = (p - 1) / 2;
    return engine.evaluate(F.Expand(F.Times(F.C1D2, periods[index[a] % e])));
  }

  /**
   * The periods <code>P(e,j)</code>, <code>e = (p-1)/2</code>, in nested square roots.
   */
  private static IExpr[] periodsOfLengthTwo(int p, int g, EvalEngine engine) {
    int[] power = new int[p - 1];
    power[0] = 1;
    for (int i = 1; i < p - 1; i++) {
      power[i] = (int) ((long) power[i - 1] * g % p);
    }
    int[] index = discreteLogarithms(p, g);
    // level e = 1: the sum of all p-th roots of unity except 1
    IExpr[] periods = {F.CN1};
    for (int e = 1; e < (p - 1) / 2; e *= 2) {
      engine.checkTimeLimit();
      int f = (p - 1) / e;
      IExpr[] next = new IExpr[2 * e];
      double[] nextValues = new double[2 * e];
      for (int j = 0; j < 2 * e; j++) {
        double v = 0.0;
        for (int t = 0; t < f / 2; t++) {
          v += Math.cos(2.0 * Math.PI * power[(j + 2 * e * t) % (p - 1)] / p);
        }
        nextValues[j] = v;
      }
      for (int j = 0; j < e; j++) {
        // (P(2e,j) - P(2e,j+e))^2 as c0 + Sum(c_k*P(e,k))
        long[] count = new long[p];
        accumulate(count, power, j, 2 * e, j, 2 * e, f / 2, p, 1);
        accumulate(count, power, j + e, 2 * e, j + e, 2 * e, f / 2, p, 1);
        accumulate(count, power, j, 2 * e, j + e, 2 * e, f / 2, p, -2);
        IASTAppendable discriminant = F.PlusAlloc(e + 1);
        discriminant.append(F.ZZ(count[0]));
        for (int k = 0; k < e; k++) {
          long c = count[power[k]];
          if (c != 0) {
            discriminant.append(F.Times(F.ZZ(c), periods[k]));
          }
        }
        IExpr root = clearedSqrt(engine.evaluate(discriminant), engine);
        IExpr sum = periods[j];
        boolean firstLarger = nextValues[j] >= nextValues[j + e];
        IExpr larger = engine.evaluate(F.Expand(F.Times(F.C1D2, F.Plus(sum, root))));
        IExpr smaller = engine.evaluate(F.Expand(F.Times(F.C1D2, F.Subtract(sum, root))));
        next[j] = firstLarger ? larger : smaller;
        next[j + e] = firstLarger ? smaller : larger;
      }
      periods = next;
    }
    return periods;
  }

  /**
   * Add <code>factor</code> times the exponent counts of <code>P(e1,j1)*P(e2,j2)</code>, periods
   * of <code>len</code> terms each, to <code>count</code>.
   */
  private static void accumulate(long[] count, int[] power, int j1, int e1, int j2, int e2,
      int len, int p, long factor) {
    for (int s = 0; s < len; s++) {
      int a = power[(j1 + e1 * s) % (p - 1)];
      for (int t = 0; t < len; t++) {
        int b = power[(j2 + e2 * t) % (p - 1)];
        count[(a + b) % p] += factor;
      }
    }
  }

  private static int[] discreteLogarithms(int p, int g) {
    int[] index = new int[p];
    int x = 1;
    for (int i = 0; i < p - 1; i++) {
      index[x] = i;
      x = (int) ((long) x * g % p);
    }
    return index;
  }

  /**
   * <code>Sqrt(d)</code> with the rational denominators of the terms of <code>d</code> taken out:
   * <code>Sqrt(17/4 + Sqrt(17)/4)</code> is <code>Sqrt(17 + Sqrt(17))/2</code>.
   */
  private static IExpr clearedSqrt(IExpr d, EvalEngine engine) {
    d = engine.evaluate(F.Expand(d));
    IAST terms = d.isPlus() ? (IAST) d : F.Plus(d);
    IInteger lcm = F.C1;
    for (IExpr term : terms) {
      IExpr coefficient = term.isRational() ? term
          : term.isTimes() && term.first().isRational() ? term.first() : F.C1;
      lcm = lcm.lcm(((IRational) coefficient).denominator());
    }
    // the smallest m with lcm | m^2
    IInteger m = F.C1;
    IAST factors = lcm.factorInteger();
    for (IExpr factor : factors) {
      IInteger prime = (IInteger) factor.first();
      int exponent = factor.second().toIntDefault();
      m = m.multiply(prime.powerRational((exponent + 1) / 2));
    }
    if (m.isOne()) {
      return engine.evaluate(F.Sqrt(d));
    }
    IExpr scaled = engine.evaluate(F.Expand(F.Times(m.multiply(m), d)));
    return engine.evaluate(F.Divide(F.Sqrt(scaled), m));
  }
}
