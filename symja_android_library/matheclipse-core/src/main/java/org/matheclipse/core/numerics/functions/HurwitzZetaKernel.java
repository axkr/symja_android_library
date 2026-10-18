package org.matheclipse.core.numerics.functions;

import org.hipparchus.complex.Complex;

/**
 * The Hurwitz zeta function <code>HurwitzZeta(s, a)</code> in machine arithmetic, for
 * <code>Re(s) &gt;= 1/2</code>, <code>s != 1</code> and <code>Re(a) &gt; 0</code>, by
 * Euler-Maclaurin summation (DLMF 25.11.5).
 *
 * <p>
 * The direct sum runs to <code>w = a + N</code> with <code>w &gt;= W(s) = Max(10, |s|/2+4)</code>,
 * so that the Bernoulli correction series converges geometrically (its term ratio is about
 * <code>|s+2k|^2/(2*Pi*w)^2</code>); the correction is truncated at <code>2^-56</code> of the
 * running total. <code>k + a</code> is formed with its exact rounding residual <code>e</code>,
 * and each term <code>(k+a)^(-s)</code> is corrected to first order by <code>1 - s*e/w</code>.
 *
 * <p>
 * The methods return <code>NaN</code> or <code>null</code> where they give no usable double: the
 * analytic continuation to <code>Re(s) &lt; 1/2</code>, a sum which cancels to far below its
 * terms, or no convergence. The caller then uses its arbitrary precision path.
 */
public final class HurwitzZetaKernel {

  /** <code>BernoulliB(2*k)/(2*k)!</code>, <code>k = 1..25</code>. */
  private static final double[] B2K_FACT = {8.33333333333333333333e-2, -1.38888888888888888889e-3,
      3.30687830687830687831e-5, -8.26719576719576719577e-7, 2.08767569878680989792e-8,
      -5.28419013868749318485e-10, 1.33825365306846788328e-11, -3.38968029632258286683e-13,
      8.58606205627784456414e-15, -2.17486869855806187304e-16, 5.5090028283602295152e-18,
      -1.39544646858125233407e-19, 3.53470703962946747169e-21, -8.9535174270375468504e-23,
      2.26795245233768306031e-24, -5.74479066887220244526e-26, 1.45517247561486490187e-27,
      -3.68599494066531017818e-29, 9.33673425709504467203e-31, -2.36502241570062993456e-32,
      5.99067176248213430466e-34, -1.51745488446829026171e-35, 3.84375812545418823223e-37,
      -9.73635307264669103527e-39, 2.46624704420068095711e-40};

  /** <code>2^-56</code>, the cut-off of the correction series. */
  private static final double EPS = 1.3877787807814457e-17;

  /** A sum whose terms exceed its value by more than this factor is declined. */
  private static final double CANCEL_MAX = 16.0;

  /**
   * From this real part on <code>Zeta(s)</code> is <code>1 + Sum(n^(-s), {n, 2, 8})</code> to
   * double precision (<code>9^(-20) &lt; 2^-63</code>), and summed directly: the Euler-Maclaurin
   * sum leaves a few ulp of 1 in <code>Zeta(s) - 1</code>, which is all there is of it.
   */
  private static final double DIRECT_SUM_MIN = 20.0;

  private static final int DIRECT_SUM_TERMS = 8;

  private HurwitzZetaKernel() {}

  /** The smallest <code>w = a + N</code> the correction series is run at. */
  private static double startW(double absS) {
    double w = 0.5 * absS + 4.0;
    return w < 10.0 ? 10.0 : w;
  }

  /**
   * <code>HurwitzZeta(s, a)</code> for real <code>s &gt;= 1/2, s != 1</code> and real
   * <code>a &gt; 0</code>.
   *
   * @return <code>NaN</code> where the kernel declines
   */
  public static double hurwitzZeta(double s, double a) {
    if (!Double.isFinite(s) || !Double.isFinite(a) || s == 1.0 || !(a > 0.0) || s < 0.5) {
      return Double.NaN;
    }
    if (a == 1.0 && s >= DIRECT_SUM_MIN) {
      double sum = 0.0;
      for (int k = DIRECT_SUM_TERMS; k >= 2; k--) {
        sum += Math.pow(k, -s);
      }
      return 1.0 + sum;
    }
    final double bigW = startW(Math.abs(s));
    int n = 0;
    if (a < bigW) {
      double count = Math.ceil(bigW - a);
      if (count > 1.0e5) {
        return Double.NaN;
      }
      n = (int) count;
    }
    double sum = 0.0;
    for (int k = n - 1; k >= 0; k--) {
      // small terms first; w_k + e == k + a exactly
      final double wk = k + a;
      final double bb = wk - k;
      final double e = (k - (wk - bb)) + (a - bb);
      final double f = Math.pow(wk, -s);
      sum += e != 0.0 ? f * (1.0 - s * e / wk) : f;
    }
    final double w = a + n;
    final double wps = Math.pow(w, -s);
    final double tail = 0.5 * wps;
    final double head = w * wps / (s - 1.0);
    final double total = sum + head + tail;
    double t = s * wps / w; // (s)_1 w^(-s-1)
    final double w2 = 1.0 / (w * w);
    double correction = 0.0;
    boolean converged = false;
    for (int k = 1; k <= B2K_FACT.length; k++) {
      final double term = B2K_FACT[k - 1] * t;
      correction += term;
      if (Math.abs(term) <= EPS * Math.abs(total)) {
        converged = true;
        break;
      }
      t *= (s + 2.0 * k - 1.0) * (s + 2.0 * k) * w2;
    }
    if (!converged) {
      return Double.NaN;
    }
    final double value = sum + (head + (tail + correction));
    return Double.isFinite(value) ? value : Double.NaN;
  }

  /**
   * <code>PolyGamma(n, z) = (-1)^(n+1) * n! * HurwitzZeta(n+1, z)</code> for <code>n &gt;= 1</code>
   * and <code>Re(z) &gt; 0</code>.
   *
   * @return <code>null</code> where the kernel declines
   */
  public static Complex polyGamma(int n, Complex z) {
    if (n < 1 || n > 170) {
      return null;
    }
    Complex h = hurwitzZeta(new Complex(n + 1.0), z);
    if (h == null) {
      return null;
    }
    double factorial = 1.0;
    for (int i = 2; i <= n; i++) {
      factorial *= i;
    }
    Complex value = h.multiply((n & 1) == 1 ? factorial : -factorial);
    return Double.isFinite(value.getReal()) && Double.isFinite(value.getImaginary()) ? value
        : null;
  }

  /**
   * <code>LerchPhi(z, s, a) = Sum(z^k/(k+a)^s, {k, 0, Infinity})</code> for <code>|z| &lt; 1</code>
   * (or <code>z == 1</code>, the Hurwitz zeta function) and <code>Re(a) &gt; 0</code>, where
   * <code>(k+a)^s</code> is the principal power for every <code>k</code>.
   *
   * <p>
   * <code>z^k</code> is carried as a double-double, so that the rounding of a running power does
   * not grow into the terms; <code>k + a</code> is formed with its exact residual and each
   * <code>(k+a)^(-s)</code> corrected to first order; the sum is compensated (Neumaier) and declined
   * if it cancels to far below its terms. It ends on two consecutive terms below <code>2^-58</code>
   * of the sum once the terms are sure to decrease.
   *
   * @return <code>null</code> where the kernel declines
   */
  public static Complex lerchPhi(Complex z, Complex s, Complex a) {
    final double zr = z.getReal();
    final double zi = z.getImaginary();
    final double sr = s.getReal();
    final double si = s.getImaginary();
    final double ar = a.getReal();
    final double ai = a.getImaginary();
    if (!Double.isFinite(zr) || !Double.isFinite(zi) || !Double.isFinite(sr)
        || !Double.isFinite(si) || !Double.isFinite(ar) || !Double.isFinite(ai) || !(ar > 0.0)) {
      return null;
    }
    if (zi == 0.0 && zr == 1.0) {
      return hurwitzZeta(s, a);
    }
    final double zm2 = zr * zr + zi * zi;
    if (!(zm2 < 1.0)) {
      return null;
    }
    if (zm2 == 0.0) {
      return powNeg(ar, ai, sr, si);
    }
    final double logZ = 0.5 * Math.log(zm2);
    final boolean realSA = si == 0.0 && ai == 0.0;
    // z^k = (hr + lr) + I*(hi + li)
    double hr = 1.0;
    double hi = 0.0;
    double lr = 0.0;
    double li = 0.0;
    double sumR = 0.0;
    double sumI = 0.0;
    double compR = 0.0;
    double compI = 0.0;
    double absSum = 0.0;
    int small = 0;
    for (int k = 0; k < 200000; k++) {
      final double wk = k + ar;
      final double bb = wk - k;
      final double e = (k - (wk - bb)) + (ar - bb);
      double tr;
      double ti;
      if (realSA) {
        double f = Math.pow(wk, -sr);
        if (e != 0.0) {
          f *= 1.0 - sr * e / wk;
        }
        tr = (hr + lr) * f;
        ti = (hi + li) * f;
      } else {
        Complex f = powNeg(wk, ai, sr, si);
        if (e != 0.0) {
          Complex q = new Complex(e * sr, e * si).divide(new Complex(wk, ai));
          f = f.multiply(new Complex(1.0 - q.getReal(), -q.getImaginary()));
        }
        final double pr = hr + lr;
        final double pi = hi + li;
        tr = pr * f.getReal() - pi * f.getImaginary();
        ti = pr * f.getImaginary() + pi * f.getReal();
      }
      double n = sumR + tr;
      compR += Math.abs(sumR) >= Math.abs(tr) ? (sumR - n) + tr : (tr - n) + sumR;
      sumR = n;
      n = sumI + ti;
      compI += Math.abs(sumI) >= Math.abs(ti) ? (sumI - n) + ti : (ti - n) + sumI;
      sumI = n;
      final double at = Math.abs(tr) + Math.abs(ti);
      absSum += at;
      if (at <= 0.25 * EPS * (Math.abs(sumR) + Math.abs(sumI))
          && (sr >= 0.0 || logZ - sr * Math.log1p(1.0 / wk) < 0.0)) {
        if (++small >= 2) {
          break;
        }
      } else {
        small = 0;
      }
      // z^(k+1) as a double-double: the rounding error of every product is kept
      final double p1 = hr * zr;
      final double p2 = hi * zi;
      final double p3 = hr * zi;
      final double p4 = hi * zr;
      final double e1 = Math.fma(hr, zr, -p1);
      final double e2 = Math.fma(hi, zi, -p2);
      final double e3 = Math.fma(hr, zi, -p3);
      final double e4 = Math.fma(hi, zr, -p4);
      final double nr = p1 - p2;
      final double ni = p3 + p4;
      final double b1 = nr - p1;
      final double f1 = (p1 - (nr - b1)) + (-p2 - b1);
      final double b2 = ni - p3;
      final double f2 = (p3 - (ni - b2)) + (p4 - b2);
      final double nlr = (e1 - e2) + f1 + (lr * zr - li * zi);
      final double nli = (e3 + e4) + f2 + (lr * zi + li * zr);
      final double qr = nr + nlr;
      final double qi = ni + nli;
      lr = nlr - (qr - nr);
      li = nli - (qi - ni);
      hr = qr;
      hi = qi;
      if (hr == 0.0 && hi == 0.0) {
        small = 2;
        break;
      }
    }
    if (small < 2) {
      return null;
    }
    sumR += compR;
    sumI += compI;
    if (!Double.isFinite(sumR) || !Double.isFinite(sumI)
        || absSum > CANCEL_MAX * (Math.abs(sumR) + Math.abs(sumI))) {
      return null;
    }
    return new Complex(sumR, sumI);
  }

  /** <code>w^(-s)</code>, principal branch. */
  private static Complex powNeg(double wr, double wi, double sr, double si) {
    double lr;
    double li;
    if (wi == 0.0 && wr > 0.0) {
      lr = Math.log(wr);
      li = 0.0;
    } else {
      lr = Math.log(Math.hypot(wr, wi));
      li = Math.atan2(wi, wr);
    }
    final double er = -(sr * lr - si * li);
    final double ei = -(sr * li + si * lr);
    final double m = Math.exp(er);
    return new Complex(m * Math.cos(ei), m * Math.sin(ei));
  }

  /**
   * <code>HurwitzZeta(s, a)</code> for complex <code>s</code> with <code>Re(s) &gt;= 1/2</code>,
   * <code>s != 1</code>, and complex <code>a</code> with <code>Re(a) &gt; 0</code>.
   *
   * @return <code>null</code> where the kernel declines
   */
  public static Complex hurwitzZeta(Complex s, Complex a) {
    final double sr = s.getReal();
    final double si = s.getImaginary();
    final double ar = a.getReal();
    final double ai = a.getImaginary();
    if (!Double.isFinite(sr) || !Double.isFinite(si) || !Double.isFinite(ar)
        || !Double.isFinite(ai) || !(ar > 0.0) || sr < 0.5 || (sr == 1.0 && si == 0.0)) {
      return null;
    }
    if (si == 0.0 && ai == 0.0) {
      double v = hurwitzZeta(sr, ar);
      return Double.isNaN(v) ? null : new Complex(v);
    }
    if (ar == 1.0 && ai == 0.0 && sr >= DIRECT_SUM_MIN) {
      // 1 + Sum(n^(-s)): the small part to its own relative accuracy
      Complex sum = Complex.ZERO;
      for (int k = DIRECT_SUM_TERMS; k >= 2; k--) {
        sum = sum.add(powNeg(k, 0.0, sr, si));
      }
      return sum.add(1.0);
    }
    final double absS = Math.hypot(sr, si);
    if (!(absS < 1.0e4)) {
      return null;
    }
    final double bigW = startW(absS);
    int n = 0;
    if (ar < bigW) {
      double count = Math.ceil(bigW - ar);
      if (count > 1.0e5) {
        return null;
      }
      n = (int) count;
    }
    double sumR = 0.0;
    double sumI = 0.0;
    double absSum = 0.0;
    for (int k = n - 1; k >= 0; k--) {
      final double wk = k + ar;
      final double bb = wk - k;
      final double e = (k - (wk - bb)) + (ar - bb);
      Complex term = powNeg(wk, ai, sr, si);
      if (e != 0.0) {
        // term *= 1 - s*e/w, w = wk + I*ai
        Complex q = new Complex(e * sr, e * si).divide(new Complex(wk, ai));
        term = term.multiply(new Complex(1.0 - q.getReal(), -q.getImaginary()));
      }
      sumR += term.getReal();
      sumI += term.getImaginary();
      absSum += term.norm();
    }
    final Complex w = new Complex(ar + n, ai);
    final Complex wps = powNeg(w.getReal(), w.getImaginary(), sr, si);
    final Complex head = w.multiply(wps).divide(new Complex(sr - 1.0, si));
    final Complex tail = wps.multiply(0.5);
    final double totalNorm = Math.hypot(sumR + head.getReal() + tail.getReal(),
        sumI + head.getImaginary() + tail.getImaginary());
    Complex t = s.multiply(wps).divide(w);
    final Complex w2 = w.multiply(w).reciprocal();
    double cr = 0.0;
    double ci = 0.0;
    boolean converged = false;
    for (int k = 1; k <= B2K_FACT.length; k++) {
      final double b = B2K_FACT[k - 1];
      final double er = b * t.getReal();
      final double ei = b * t.getImaginary();
      cr += er;
      ci += ei;
      if (Math.hypot(er, ei) <= EPS * totalNorm) {
        converged = true;
        break;
      }
      // t *= (s+2k-1)(s+2k)/w^2
      t = t.multiply(new Complex(sr + 2.0 * k - 1.0, si).multiply(new Complex(sr + 2.0 * k, si))
          .multiply(w2));
    }
    if (!converged) {
      return null;
    }
    final double rr = sumR + (head.getReal() + (tail.getReal() + cr));
    final double ri = sumI + (head.getImaginary() + (tail.getImaginary() + ci));
    if (!Double.isFinite(rr) || !Double.isFinite(ri)) {
      return null;
    }
    // a complex s or a can make the sum exponentially small against its terms
    absSum += head.norm() + tail.norm();
    if (absSum > CANCEL_MAX * Math.hypot(rr, ri)) {
      return null;
    }
    return new Complex(rr, ri);
  }
}
