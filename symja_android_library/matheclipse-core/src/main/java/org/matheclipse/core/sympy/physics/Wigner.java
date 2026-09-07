package org.matheclipse.core.sympy.physics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;

/**
 * Exact Wigner 3j and 6j symbols and Wigner D-functions.
 *
 * <p>
 * The 3j and 6j symbols follow the binomial formulation of L. Wei, <i>Unified approach for exact
 * calculation of angular momentum coupling and recoupling coefficients</i>, Computer Physics
 * Communications 120, 222 (1999), in the form ported from SymPy's
 * <code>sympy/physics/wigner.py</code>. Every value is a rational number times the square root of
 * a rational number. All bookkeeping is done on the <em>doubled</em> quantum numbers
 * <code>2*j</code> and <code>2*m</code>, which are plain integers for integer and half-integer
 * arguments; see {@link #doubled(IExpr...)}.
 *
 * <p>
 * The D-functions use the general form of Edmonds, <i>Angular momentum in quantum mechanics</i>,
 * equations 4.1.12 and 4.1.15.
 */
public final class Wigner {

  /** Three angular momenta do not couple: they violate the triangle relation. */
  public static class TriangularException extends Exception {
    private static final long serialVersionUID = 5417213398120583681L;

    public TriangularException() {
      super("not triangular", null, false, false);
    }
  }

  /** The magnetic quantum numbers make the symbol vanish identically. */
  public static class NotPhysicalException extends Exception {
    private static final long serialVersionUID = -8127545010375363107L;

    public NotPhysicalException() {
      super("not physical", null, false, false);
    }
  }

  /**
   * Doubles the arguments, so that integer and half-integer quantum numbers become
   * <code>int</code>s.
   *
   * @return the values <code>2*v</code>, or <code>null</code> if one of the arguments is not a
   *         real number, is neither an integer nor a half-integer, or is too large for an
   *         <code>int</code>. Use {@link #isTooLarge(IExpr...)} to tell the last case apart, since
   *         it is the only one in which the symbol still has a well defined value.
   */
  public static int[] doubled(IExpr... values) {
    int[] result = new int[values.length];
    for (int i = 0; i < values.length; i++) {
      if (!values[i].isReal()) {
        return null;
      }
      int d = ((IReal) values[i]).multiply(F.C2).toIntDefault();
      if (F.isNotPresent(d)) {
        return null;
      }
      result[i] = d;
    }
    return result;
  }

  /**
   * Tests whether {@link #doubled(IExpr...)} failed only because a value is too large, rather than
   * because it is not an integer or half-integer. Such a symbol is well defined but far beyond what
   * the factorials in the formulas can represent, so it is better left unevaluated than reported as
   * a selection-rule zero.
   */
  public static boolean isTooLarge(IExpr... values) {
    boolean tooLarge = false;
    for (IExpr value : values) {
      if (!value.isReal()) {
        return false;
      }
      IExpr doubledValue = ((IReal) value).multiply(F.C2);
      if (!doubledValue.isInteger()) {
        return false;
      }
      tooLarge |= F.isNotPresent(doubledValue.toIntDefault());
    }
    return tooLarge;
  }

  /**
   * The Wigner 3j symbol for the doubled arguments <code>2*j1, 2*j2, 2*j3, 2*m1, 2*m2,
   * 2*m3</code>.
   *
   * @return <code>c*Sqrt(r)</code> as an unevaluated product, or {@link F#C0}
   * @throws NotPhysicalException if some <code>j</code> is negative, <code>m1+m2+m3 != 0</code>,
   *         some <code>|m| &gt; j</code> or some <code>j-m</code> is not an integer
   * @throws TriangularException if <code>j1, j2, j3</code> do not couple
   */
  public static IExpr wigner3j(int dj1, int dj2, int dj3, int dm1, int dm2, int dm3)
      throws TriangularException, NotPhysicalException {
    if (dj1 < 0 || dj2 < 0 || dj3 < 0 || dm1 + dm2 + dm3 != 0) {
      throw new NotPhysicalException();
    }
    checkCouple(dj1, dj2, dj3);
    if (Math.abs(dm1) > dj1 || Math.abs(dm2) > dj2 || Math.abs(dm3) > dj3 //
        || (dj1 - dm1) % 2 != 0 || (dj2 - dm2) % 2 != 0 || (dj3 - dm3) % 2 != 0) {
      throw new NotPhysicalException();
    }

    int sumj = (dj1 + dj2 + dj3) / 2;
    int jm1 = sumj - dj1; // j2+j3-j1
    int jm2 = sumj - dj2; // j1+j3-j2
    int jm3 = sumj - dj3; // j1+j2-j3
    int j1mm1 = (dj1 - dm1) / 2;
    int j2mm2 = (dj2 - dm2) / 2;
    int j3mm3 = (dj3 - dm3) / 2;
    int j1pm1 = (dj1 + dm1) / 2;

    int imin = Math.max(0, Math.max(j1pm1 - jm2, j2mm2 - jm1));
    int imax = Math.min(jm3, Math.min(j1pm1, j2mm2));
    IInteger sum = F.C0;
    for (int ii = imin; ii <= imax; ii++) {
      IInteger term = binomial(jm3, ii) //
          .multiply(binomial(jm2, j1pm1 - ii)) //
          .multiply(binomial(jm1, j2mm2 - ii));
      // alternating sum; the last term (ii == imax) carries a plus sign
      sum = term.subtract(sum);
    }
    if (sum.isZero()) {
      return F.C0;
    }
    if ((dj1 + (dj3 + dm3) / 2 + imax) % 2 != 0) {
      sum = sum.negate();
    }
    IInteger numerator = binomial(dj1, jm2).multiply(binomial(dj2, jm1));
    IInteger denominator = binomial(sumj, jm3) //
        .multiply(binomial(dj1, j1mm1)) //
        .multiply(binomial(dj2, j2mm2)) //
        .multiply(binomial(dj3, j3mm3)) //
        .multiply(F.ZZ(sumj + 1));
    return F.Times(sum, F.Sqrt(F.QQ(numerator, denominator)));
  }

  /**
   * The Wigner 6j symbol for the doubled arguments <code>2*j1, ..., 2*j6</code>.
   *
   * @return <code>c*Sqrt(r)</code> as an unevaluated product, or {@link F#C0}
   * @throws TriangularException if one of the triples <code>{j1,j2,j3}, {j1,j5,j6}, {j4,j2,j6},
   *         {j4,j5,j3}</code> does not couple
   */
  public static IExpr wigner6j(int dj1, int dj2, int dj3, int dj4, int dj5, int dj6)
      throws TriangularException {
    checkCouple(dj1, dj2, dj3);
    checkCouple(dj1, dj5, dj6);
    checkCouple(dj4, dj2, dj6);
    checkCouple(dj4, dj5, dj3);

    int j123 = (dj1 + dj2 + dj3) / 2;
    int j156 = (dj1 + dj5 + dj6) / 2;
    int j426 = (dj4 + dj2 + dj6) / 2;
    int j453 = (dj4 + dj5 + dj3) / 2;
    int jpm123 = (dj1 + dj2 - dj3) / 2;
    int jpm132 = (dj1 + dj3 - dj2) / 2;
    int jpm231 = (dj2 + dj3 - dj1) / 2;
    int jpm156 = (dj1 + dj5 - dj6) / 2;
    int jpm426 = (dj4 + dj2 - dj6) / 2;
    int jpm453 = (dj4 + dj5 - dj3) / 2;

    int imin = Math.max(Math.max(j123, j453), Math.max(j426, j156));
    // terms beyond the true upper bound vanish (binomial with k > n); the sign fix-up on imax below
    // accounts for the extra sign flips they cause
    int imax = Math.max(jpm123 + j453, Math.max(jpm132 + j426, jpm231 + j156));
    IInteger sum = F.C0;
    for (int ii = imin; ii <= imax; ii++) {
      IInteger term = binomial(ii + 1, j123 + 1) //
          .multiply(binomial(jpm123, ii - j453)) //
          .multiply(binomial(jpm132, ii - j426)) //
          .multiply(binomial(jpm231, ii - j156));
      sum = term.subtract(sum);
    }
    if (sum.isZero()) {
      return F.C0;
    }
    if (imax % 2 != 0) {
      sum = sum.negate();
    }
    IInteger numerator = binomial(j123 + 1, dj1 + 1).multiply(binomial(dj1, jpm123));
    IInteger denominator = binomial(j156 + 1, dj1 + 1) //
        .multiply(binomial(dj1, jpm156)) //
        .multiply(binomial(j426 + 1, dj4 + 1)) //
        .multiply(binomial(dj4, jpm426)) //
        .multiply(binomial(j453 + 1, dj4 + 1)) //
        .multiply(binomial(dj4, jpm453)) //
        .multiply(F.ZZ((long) (dj4 + 1) * (dj4 + 1)));
    return F.Times(sum, F.Sqrt(F.QQ(numerator, denominator)));
  }

  /**
   * The Wigner D-function <code>D^j_{m1,m2}(alpha, beta, gamma) = Exp(I*m1*alpha) *
   * d^j_{m1,m2}(beta) * Exp(I*m2*gamma)</code> for the doubled arguments <code>2*j, 2*m1,
   * 2*m2</code>, which must satisfy <code>|m1|, |m2| &lt;= j</code> with <code>j-m1</code> and
   * <code>j-m2</code> integers.
   */
  public static IExpr wignerD(int dj, int dm1, int dm2, IExpr alpha, IExpr beta, IExpr gamma,
      EvalEngine engine) {
    IExpr d = wignerDSmall(dj, dm1, dm2, beta, engine);
    if (alpha.isZero() && gamma.isZero()) {
      return d;
    }
    return engine.evaluate(F.Times(F.Exp(F.Times(S.I, F.QQ(dm1, 2), alpha)), d,
        F.Exp(F.Times(S.I, F.QQ(dm2, 2), gamma))));
  }

  /**
   * The small Wigner d-function <code>d^j_{m1,m2}(beta)</code> (Edmonds, equation 4.1.15) for the
   * doubled arguments <code>2*j, 2*m1, 2*m2</code>, which must satisfy <code>|m1|, |m2| &lt;=
   * j</code> with <code>j-m1</code> and <code>j-m2</code> integers.
   */
  public static IExpr wignerDSmall(int dj, int dm1, int dm2, IExpr beta, EvalEngine engine) {
    int jpm1 = (dj + dm1) / 2;
    int jmm1 = (dj - dm1) / 2;
    int jpm2 = (dj + dm2) / 2;
    int jmm2 = (dj - dm2) / 2;
    // m1+m2 is an integer because j-m1 and j-m2 are
    int m1pm2 = (dm1 + dm2) / 2;
    int sigmaMin = Math.max(0, -m1pm2);
    int sigmaMax = Math.min(jmm1, jmm2);

    IExpr halfBeta = F.Times(F.C1D2, beta);
    IExpr cos = F.Cos(halfBeta);
    IExpr sin = F.Sin(halfBeta);
    IASTAppendable terms = F.PlusAlloc(sigmaMax - sigmaMin + 1);
    for (int s = sigmaMin; s <= sigmaMax; s++) {
      int r = jmm1 - s;
      IInteger coefficient = binomial(jpm2, r).multiply(binomial(jmm2, s));
      if (r % 2 != 0) {
        coefficient = coefficient.negate();
      }
      terms.append(F.Times(coefficient, //
          F.Power(cos, F.ZZ(2 * s + m1pm2)), //
          F.Power(sin, F.ZZ(dj - 2 * s - m1pm2))));
    }
    IRational dij = F.QQ(factorial(jpm1).multiply(factorial(jmm1)),
        factorial(jpm2).multiply(factorial(jmm2)));
    return engine.evaluate(F.Times(F.Sqrt(dij), terms));
  }

  /**
   * @throws TriangularException if the doubled angular momenta are negative, do not add up to an
   *         integer or violate the triangle relation
   */
  private static void checkCouple(int dj1, int dj2, int dj3) throws TriangularException {
    if (dj1 < 0 || dj2 < 0 || dj3 < 0 //
        || (dj1 + dj2 + dj3) % 2 != 0 //
        || dj1 > dj2 + dj3 || dj2 > dj1 + dj3 || dj3 > dj1 + dj2) {
      throw new TriangularException();
    }
  }

  /** <code>Binomial(n, k)</code> for <code>n &gt;= 0</code>; <code>0</code> outside <code>0 &lt;= k &lt;= n</code>. */
  private static IInteger binomial(int n, int k) {
    if (k < 0 || k > n) {
      return F.C0;
    }
    return AbstractIntegerSym.binomial(n, k);
  }

  private static IInteger factorial(int n) {
    return AbstractIntegerSym.factorial(n);
  }

  private Wigner() {}
}
