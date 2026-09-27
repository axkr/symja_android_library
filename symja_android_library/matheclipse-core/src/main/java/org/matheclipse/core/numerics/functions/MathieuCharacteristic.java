package org.matheclipse.core.numerics.functions;

import java.util.Arrays;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.linear.EigenDecompositionSymmetric;
import org.matheclipse.core.basic.OperationSystem;

/**
 * The Mathieu characteristic values <code>MathieuCharacteristicA(r, q)</code>,
 * <code>MathieuCharacteristicB(r, q)</code> and the characteristic exponent
 * <code>MathieuCharacteristicExponent(a, q)</code>.
 *
 * <p>
 * A characteristic value is an eigenvalue <code>a</code> of the three term recurrence
 * <code>(a - s(k))*x(k) == q*(alpha(k)*x(k-1) + beta(k)*x(k+1))</code> for the Fourier coefficients
 * of the solution. An integer <code>r == n</code> has four such systems, one for each parity and
 * period (DLMF 28.4.5 - 28.4.8):
 *
 * <pre>
 * a(2m)   Cos(2*k*z)       s = (2k)^2,   the first coupling doubled
 * a(2m+1) Cos((2k+1)*z)    s = (2k+1)^2, s(0) = 1 + q
 * b(2m+1) Sin((2k+1)*z)    s = (2k+1)^2, s(0) = 1 - q
 * b(2m+2) Sin((2k+2)*z)    s = (2k+2)^2
 * </pre>
 *
 * and a non-integer <code>r</code> the two-sided system of the Floquet solution
 * <code>Sum(c(k)*Exp(I*(r+2*k)*z), k)</code> with <code>s = (r+2k)^2</code>, for which
 * <code>MathieuCharacteristicA</code> and <code>MathieuCharacteristicB</code> agree.
 *
 * <p>
 * For a real <code>q</code> the systems are symmetric, their eigenvalues are simple and keep their
 * order as <code>q</code> grows from <code>0</code>, where they are the <code>s(k)</code>. So the
 * characteristic value is the eigenvalue whose rank is the rank of <code>r^2</code> among the
 * <code>s(k)</code>: found in <code>double</code> and polished by Newton's method on the
 * determinant of the truncated system at the working precision. A complex <code>q</code> or
 * <code>r</code> is reached by continuation along <code>t*q</code> from <code>t == 0</code>.
 *
 * <p>
 * The characteristic exponent <code>nu</code> comes from the monodromy,
 * <code>Cos(Pi*nu) == 2*y1(Pi/2)*y2'(Pi/2) - 1</code>, which fixes it up to sign and an even
 * integer. For real parameters the branch is the one on which <code>a</code> is increasing in
 * <code>nu</code>: counting the characteristic values below <code>a</code> tells which stability
 * band or gap it lies in, and <code>nu</code> is real in <code>(m, m+1)</code> in the <code>m</code>th
 * band and <code>m + I*mu</code> with <code>mu > 0</code> in the gap above it - continuous in
 * <code>q</code> from <code>Sqrt(a)</code> at <code>q == 0</code>. Complex parameters follow
 * <code>nu</code> by continuation from <code>Sqrt(a)</code>.
 */
public final class MathieuCharacteristic {

  private static final int GUARD_DIGITS = MathieuFunctions.GUARD_DIGITS;

  /** Precision of the continuation steps, which only have to stay on the branch. */
  private static final long STEP_PRECISION = 25L;

  private static final int MAX_NEWTON = 80;

  /** The five recurrences. */
  private enum Kind {
    A_EVEN, A_ODD, B_ODD, B_EVEN, FLOQUET
  }

  /** A truncated recurrence and the rank of the eigenvalue asked for. */
  private static final class Problem {
    final Kind kind;
    /** the Floquet exponent, for {@link Kind#FLOQUET} */
    final Apcomplex r;
    final int size;
    final int target;

    Problem(Kind kind, Apcomplex r, int size, int target) {
      this.kind = kind;
      this.r = r;
      this.size = size;
      this.target = target;
    }

    /** <code>s(j)</code> */
    Apcomplex diagonal(int j, Apcomplex q, FixedPrecisionApfloatHelper h) {
      long p = h.precision();
      switch (kind) {
        case A_EVEN:
          return square(2L * j, p);
        case A_ODD:
          return j == 0 ? h.add(new Apcomplex(new Apfloat(1, p)), q) : square(2L * j + 1, p);
        case B_ODD:
          return j == 0 ? h.subtract(new Apcomplex(new Apfloat(1, p)), q) : square(2L * j + 1, p);
        case B_EVEN:
          return square(2L * j + 2, p);
        default:
          Apcomplex frequency = h.add(r, new Apcomplex(new Apfloat(2L * (j - offset()), p)));
          return h.multiply(frequency, frequency);
      }
    }

    /** <code>q^2*alpha(j)*beta(j-1)</code>, the product of the couplings of <code>j-1</code> and <code>j</code> */
    Apcomplex coupling(int j, Apcomplex qSquared, FixedPrecisionApfloatHelper h) {
      if (kind == Kind.A_EVEN && j == 1) {
        return h.add(qSquared, qSquared);
      }
      return qSquared;
    }

    int offset() {
      return kind == Kind.FLOQUET ? size / 2 : 0;
    }

    Problem resized(int newSize) {
      return new Problem(kind, r, newSize, target);
    }
  }

  private MathieuCharacteristic() {}

  // ---------------------------------------------------------------------------------------------
  // characteristic values
  // ---------------------------------------------------------------------------------------------

  /** <code>MathieuCharacteristicA(r, q)</code>, or <code>null</code> if it could not be found. */
  public static Apcomplex characteristicA(Apcomplex r, Apcomplex q,
      FixedPrecisionApcomplexHelper h) {
    return characteristic(r, q, false, h);
  }

  /**
   * <code>MathieuCharacteristicB(r, q)</code>, or <code>null</code> if it could not be found - as
   * for <code>r == 0</code>, which has no odd periodic solution.
   */
  public static Apcomplex characteristicB(Apcomplex r, Apcomplex q,
      FixedPrecisionApcomplexHelper h) {
    return characteristic(r, q, true, h);
  }

  private static Apcomplex characteristic(Apcomplex r, Apcomplex q, boolean odd,
      FixedPrecisionApcomplexHelper h) {
    final long precision = h.precision();
    // both characteristic values are even in r
    if (r.real().signum() < 0 || (r.real().signum() == 0 && r.imag().signum() < 0)) {
      r = r.negate();
    }
    final boolean integer = isInteger(r);
    if (odd && integer && r.real().signum() == 0) {
      return null;
    }
    if (isZero(q)) {
      return h.multiply(r, r);
    }
    Problem problem;
    if (integer) {
      long n = r.real().longValue();
      if (n > 1000000L) {
        return null;
      }
      int m = (int) (n / 2);
      if (!odd) {
        problem = n % 2 == 0 ? new Problem(Kind.A_EVEN, null, 0, m)
            : new Problem(Kind.A_ODD, null, 0, m);
      } else {
        problem = n % 2 == 1 ? new Problem(Kind.B_ODD, null, 0, m)
            : new Problem(Kind.B_EVEN, null, 0, m - 1);
      }
    } else {
      if (magnitude(r) > 2000000.0) {
        return null;
      }
      problem = new Problem(Kind.FLOQUET, r, 0, 0);
    }
    problem = problem.resized(size(problem, magnitude(r), magnitude(q), precision));

    final FixedPrecisionApfloatHelper wh =
        new FixedPrecisionApfloatHelper(precision + GUARD_DIGITS);
    Apcomplex a;
    try {
      if (isReal(r) && isReal(q)) {
        a = fromSymmetricEigenvalues(problem, r, q);
      } else {
        a = continuation(problem, r, q);
      }
      if (a == null) {
        return null;
      }
      a = newton(problem, q, wh.valueOf(a), precision, wh);
    } catch (ArithmeticException ex) {
      // a pivot vanished exactly
      return null;
    }
    if (a == null) {
      return null;
    }
    if (isReal(r) && isReal(q)) {
      a = new Apcomplex(a.real());
    }
    return h.valueOf(a);
  }

  /**
   * The number of equations kept: the eigenvector is concentrated around the rank asked for, and
   * past about <code>Sqrt(|q|)</code> further indices its coefficients fall like
   * <code>q^k/(4^k*k!^2)</code>.
   */
  private static int size(Problem problem, double r, double q, long precision) {
    int center = problem.kind == Kind.FLOQUET ? (int) Math.ceil(r / 2.0) : problem.target;
    double qSize = Math.max(1.0, q);
    int m = 0;
    double digits = 0.0;
    while (digits < precision + 2 * GUARD_DIGITS) {
      m++;
      digits += Math.max(0.0, Math.log10(4.0 * m * m / qSize));
    }
    int tail = (int) Math.ceil(Math.sqrt(qSize)) + m + 5;
    return problem.kind == Kind.FLOQUET ? 2 * (center + tail) + 1 : center + tail + 1;
  }

  /** The eigenvalue of rank <code>target</code> of the real symmetric truncated system. */
  private static Apcomplex fromSymmetricEigenvalues(Problem problem, Apcomplex r, Apcomplex q) {
    final int n = problem.size;
    final double qd = q.real().doubleValue();
    final FixedPrecisionApfloatHelper dh = new FixedPrecisionApfloatHelper(20);
    double[] main = new double[n];
    double[] secondary = new double[n - 1];
    for (int j = 0; j < n; j++) {
      main[j] = problem.diagonal(j, new Apcomplex(new Apfloat(qd, 20)), dh).real().doubleValue();
    }
    for (int j = 1; j < n; j++) {
      double product = problem.kind == Kind.A_EVEN && j == 1 ? 2.0 * qd * qd : qd * qd;
      secondary[j - 1] = Math.sqrt(product);
    }
    int target = problem.target;
    if (problem.kind == Kind.FLOQUET) {
      // the rank of r^2 among the (r+2k)^2
      double rd = r.real().doubleValue();
      target = 0;
      for (int j = 0; j < n; j++) {
        double frequency = rd + 2.0 * (j - problem.offset());
        if (frequency * frequency < rd * rd) {
          target++;
        }
      }
    }
    double[] eigenvalues = new EigenDecompositionSymmetric(main, secondary).getEigenvalues();
    Arrays.sort(eigenvalues);
    if (target >= eigenvalues.length) {
      return null;
    }
    return new Apcomplex(new Apfloat(eigenvalues[target]));
  }

  /**
   * Follows the characteristic value from <code>s(target)</code> at <code>q == 0</code> along
   * <code>t*q</code>.
   */
  private static Apcomplex continuation(Problem problem, Apcomplex r, Apcomplex q) {
    final FixedPrecisionApfloatHelper sh = new FixedPrecisionApfloatHelper(STEP_PRECISION);
    final int steps = (int) Math.min(4000.0, 16.0 + Math.ceil(8.0 * magnitude(q)));
    Apcomplex previous = null;
    Apcomplex a = problem.kind == Kind.FLOQUET ? sh.multiply(r, r)
        : problem.diagonal(problem.target, Apcomplex.ZERO, sh);
    for (int i = 1; i <= steps; i++) {
      OperationSystem.checkInterrupt();
      Apcomplex t = new Apcomplex(sh.divide(new Apfloat(i, STEP_PRECISION),
          new Apfloat(steps, STEP_PRECISION)));
      Apcomplex qt = sh.multiply(t, q);
      Apcomplex predicted = previous == null ? a : sh.subtract(sh.add(a, a), previous);
      Apcomplex next = newton(problem, qt, predicted, STEP_PRECISION - 5, sh);
      if (next == null) {
        return null;
      }
      previous = a;
      a = next;
    }
    return a;
  }

  /**
   * Newton's method on the determinant of <code>a - M</code>, whose logarithmic derivative is
   * <code>Sum(u'(j)/u(j))</code> over the pivots <code>u(j) = a - s(j) - p(j)/u(j-1)</code>.
   *
   * @return the root to <code>digits</code> digits, or <code>null</code> if Newton's method did not
   *         settle
   */
  private static Apcomplex newton(Problem problem, Apcomplex q, Apcomplex a, long digits,
      FixedPrecisionApfloatHelper h) {
    final long precision = h.precision();
    final int n = problem.size;
    final Apcomplex one = new Apcomplex(new Apfloat(1, precision));
    final Apcomplex qSquared = h.multiply(q, q);
    Apcomplex[] diagonal = new Apcomplex[n];
    Apcomplex[] coupling = new Apcomplex[n];
    for (int j = 0; j < n; j++) {
      diagonal[j] = problem.diagonal(j, q, h);
      coupling[j] = j == 0 ? Apcomplex.ZERO : problem.coupling(j, qSquared, h);
    }
    final Apfloat tolerance = new Apfloat("1e-" + (digits + 2), precision);
    for (int iteration = 0; iteration < MAX_NEWTON; iteration++) {
      OperationSystem.checkInterrupt();
      Apcomplex u = h.subtract(a, diagonal[0]);
      Apcomplex du = one;
      Apcomplex logDerivative = h.divide(du, u);
      for (int j = 1; j < n; j++) {
        Apcomplex ratio = h.divide(coupling[j], u);
        du = h.add(one, h.multiply(h.divide(ratio, u), du));
        u = h.subtract(h.subtract(a, diagonal[j]), ratio);
        logDerivative = h.add(logDerivative, h.divide(du, u));
      }
      Apcomplex delta = h.divide(one, logDerivative);
      a = h.subtract(a, delta);
      Apfloat scale = h.abs(a);
      if (scale.compareTo(Apfloat.ONE) < 0) {
        scale = Apfloat.ONE;
      }
      if (h.abs(delta).compareTo(h.multiply(tolerance, scale)) <= 0) {
        return a;
      }
    }
    return null;
  }

  // ---------------------------------------------------------------------------------------------
  // characteristic exponent
  // ---------------------------------------------------------------------------------------------

  /**
   * <code>MathieuCharacteristicExponent(a, q)</code>, or <code>null</code> if it could not be
   * determined.
   */
  public static Apcomplex characteristicExponent(Apcomplex a, Apcomplex q,
      FixedPrecisionApcomplexHelper h) {
    final long precision = h.precision();
    final FixedPrecisionApfloatHelper wh =
        new FixedPrecisionApfloatHelper(2 * precision + GUARD_DIGITS);
    Apcomplex nu;
    try {
      if (isZero(q)) {
        nu = wh.sqrt(a);
      } else if (isReal(a) && isReal(q)) {
        nu = realExponent(a, q, wh);
      } else {
        nu = complexExponent(a, q, wh);
      }
    } catch (ArithmeticException ex) {
      return null;
    }
    if (nu == null) {
      return null;
    }
    return h.valueOf(nu);
  }

  /**
   * The exponent from the band or gap that <code>a</code> lies in, counted by the characteristic
   * values below it: <code>a(0) < b(1), a(1) < b(2), a(2) < ...</code> in increasing order are the
   * band edges <code>e(0) < e(1) < ...</code>, band <code>m</code> is
   * <code>(e(2m), e(2m+1))</code> with <code>nu</code> in <code>(m, m+1)</code>.
   */
  private static Apcomplex realExponent(Apcomplex a, Apcomplex q, FixedPrecisionApfloatHelper h) {
    Apcomplex cosine = MathieuFunctions.cosPiNu(a, q, h);
    if (cosine == null) {
      return null;
    }
    Apcomplex reduced = h.divide(h.acos(cosine), new Apcomplex(h.pi()));
    int below = countCharacteristicValuesBelow(a.real().doubleValue(), q.real().doubleValue());
    if (below < 0) {
      return null;
    }
    Apfloat c = cosine.real();
    boolean stable = c.compareTo(Apfloat.ONE) <= 0 && c.compareTo(Apfloat.ONE.negate()) >= 0;
    if (stable) {
      Apfloat fraction = reduced.real();
      long m = below >= 1 ? (below - 1) / 2 : 0;
      Apfloat mm = new Apfloat(m, h.precision());
      return new Apcomplex(m % 2 == 0 ? h.add(mm, fraction)
          : h.subtract(h.add(mm, new Apfloat(1, h.precision())), fraction));
    }
    // a gap: Cos(Pi*(p+I*mu)) == (-1)^p*Cosh(Pi*mu)
    long parity = c.signum() > 0 ? 0 : 1;
    long p;
    if (below % 2 == 0) {
      p = below / 2;
    } else {
      p = ((below - 1) / 2) % 2 == parity ? (below - 1) / 2 : (below + 1) / 2;
    }
    Apfloat mu = h.abs(reduced.imag());
    return new Apcomplex(new Apfloat(p, h.precision()), mu);
  }

  /**
   * How many of <code>a(0), b(1), a(1), b(2), ...</code> are smaller than <code>a</code>, or
   * <code>-1</code> if the systems would be too large.
   */
  private static int countCharacteristicValuesBelow(double a, double q) {
    double reach = Math.sqrt(Math.max(a, 0.0) + 2.0 * Math.abs(q));
    double sizeD = Math.ceil(reach / 2.0) + Math.ceil(Math.sqrt(Math.abs(q))) + 20.0;
    if (sizeD > 200000.0) {
      return -1;
    }
    int size = (int) sizeD;
    int count = 0;
    for (Kind kind : new Kind[] {Kind.A_EVEN, Kind.A_ODD, Kind.B_ODD, Kind.B_EVEN}) {
      double[] main = new double[size];
      double[] secondary = new double[size - 1];
      for (int j = 0; j < size; j++) {
        double k;
        switch (kind) {
          case A_EVEN:
            k = 2.0 * j;
            break;
          case B_EVEN:
            k = 2.0 * j + 2.0;
            break;
          default:
            k = 2.0 * j + 1.0;
        }
        main[j] = k * k;
      }
      if (kind == Kind.A_ODD) {
        main[0] += q;
      } else if (kind == Kind.B_ODD) {
        main[0] -= q;
      }
      Arrays.fill(secondary, Math.abs(q));
      if (kind == Kind.A_EVEN && size > 1) {
        secondary[0] = Math.sqrt(2.0) * Math.abs(q);
      }
      for (double eigenvalue : new EigenDecompositionSymmetric(main, secondary).getEigenvalues()) {
        if (eigenvalue < a) {
          count++;
        }
      }
    }
    return count;
  }

  /** Follows <code>nu</code> from <code>Sqrt(a)</code> at <code>q == 0</code> along <code>t*q</code>. */
  private static Apcomplex complexExponent(Apcomplex a, Apcomplex q,
      FixedPrecisionApfloatHelper h) {
    final FixedPrecisionApfloatHelper sh = new FixedPrecisionApfloatHelper(2 * STEP_PRECISION);
    final int steps = (int) Math.min(2000.0, 8.0 + Math.ceil(4.0 * magnitude(q)));
    Apcomplex nu = sh.sqrt(a);
    for (int i = 1; i < steps; i++) {
      Apcomplex t = new Apcomplex(
          sh.divide(new Apfloat(i, sh.precision()), new Apfloat(steps, sh.precision())));
      Apcomplex cosine = MathieuFunctions.cosPiNu(a, sh.multiply(t, q), sh);
      if (cosine == null) {
        return null;
      }
      nu = nearest(sh.divide(sh.acos(cosine), new Apcomplex(sh.pi())), nu, sh);
    }
    Apcomplex cosine = MathieuFunctions.cosPiNu(a, q, h);
    if (cosine == null) {
      return null;
    }
    return nearest(h.divide(h.acos(cosine), new Apcomplex(h.pi())), h.valueOf(nu), h);
  }

  /** The one of <code>+-reduced + 2*k</code> nearest to <code>previous</code>. */
  private static Apcomplex nearest(Apcomplex reduced, Apcomplex previous,
      FixedPrecisionApfloatHelper h) {
    Apcomplex best = null;
    double bestDistance = Double.MAX_VALUE;
    for (Apcomplex base : new Apcomplex[] {reduced, reduced.negate()}) {
      double shift = Math.rint(h.subtract(previous, base).real().doubleValue() / 2.0);
      Apcomplex candidate = h.add(base, new Apcomplex(new Apfloat(2.0 * shift, h.precision())));
      double distance = magnitude(h.subtract(candidate, previous));
      if (distance < bestDistance) {
        bestDistance = distance;
        best = candidate;
      }
    }
    return best;
  }

  // ---------------------------------------------------------------------------------------------
  // helpers
  // ---------------------------------------------------------------------------------------------

  private static Apcomplex square(long k, long precision) {
    return new Apcomplex(new Apfloat(k * k, precision));
  }

  private static boolean isInteger(Apcomplex x) {
    return x.imag().signum() == 0 && x.real().equals(x.real().truncate());
  }

  private static boolean isZero(Apcomplex x) {
    return x.real().signum() == 0 && x.imag().signum() == 0;
  }

  private static boolean isReal(Apcomplex x) {
    return x.imag().signum() == 0;
  }

  private static double magnitude(Apcomplex x) {
    return Math.hypot(x.real().doubleValue(), x.imag().doubleValue());
  }
}
