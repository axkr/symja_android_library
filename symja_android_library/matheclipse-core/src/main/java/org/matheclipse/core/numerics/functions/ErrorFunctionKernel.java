package org.matheclipse.core.numerics.functions;

/**
 * Machine-precision error functions <code>Erf</code>, <code>Erfc</code> and <code>Erfi</code> of a
 * complex argument, and <code>Erfi</code> of a real one.
 *
 * <p>
 * Every method returns the value, or <code>null</code> (<code>NaN</code> for the real ones) where
 * it gives no usable double: an overflow, or a region it does not cover. The caller then falls
 * back to its arbitrary-precision path.
 *
 * <p>
 * Each component of a result is accurate relative to itself, also where it is tiny next to the
 * other one: near the imaginary axis <code>Re(Erf(x+I*y))</code> is about
 * <code>x*Exp(y^2)</code>, and a difference <code>1 - Exp(-z^2)*w(I*z)</code> would leave it
 * with an absolute error only. The regions of <code>Erf(X + I*Y)</code>, <code>X, Y &gt;= 0</code>:
 * <ul>
 * <li><code>|z| &lt;= 0.8</code>: the Maclaurin series;</li>
 * <li><code>X, Y &lt; 4</code> and <code>X*Y &lt; 1/4</code>: the Taylor series about the nearer
 * axis;</li>
 * <li><code>X, Y &lt; 4</code>: <code>1 - Exp(-z^2)*w(I*z)</code> with the Faddeeva function
 * <code>w</code> from a table of degree 20 Taylor polynomials on <code>0.5 x 0.5</code> cells;</li>
 * <li>otherwise, for <code>Y &lt;= 26</code>: Salzer's series (Abramowitz &amp; Stegun 7.1.29),
 * exponentially convergent with a relative error of <code>2*Exp(-4*Pi^2)</code>.</li>
 * </ul>
 * The squares and products that are exponentiated are formed exactly as double-double values
 * (Dekker's product), because a rounded <code>x^2</code> in <code>Exp(-x^2)</code> alone costs
 * <code>x^2</code> ulps.
 *
 * @see <a href="https://dlmf.nist.gov/7.6">DLMF 7.6 Series expansions</a>
 */
public final class ErrorFunctionKernel {

  private static final double INV_PI = 0.31830988618379067154;

  private static final double TWO_OVER_SQRTPI = 1.1283791670955125739;

  private static final double SQRTPI_INV = 0.56418958354775628695;

  /** <code>e^64</code> */
  private static final double EXP_P64 = 6.2351490808116168829e+27;

  /** <code>e^-700</code> */
  private static final double EXP_M700 = 9.8596765437597708567e-305;

  private ErrorFunctionKernel() {}

  // ----------------------------------------------------------------------------------------
  // double-double helpers (Dekker 1971, Knuth)
  // ----------------------------------------------------------------------------------------

  private static double splitHigh(double a) {
    final double c = 134217729.0 * a; // 2^27 + 1
    return c - (c - a);
  }

  /** The rounding error of <code>prod = a*b</code>: <code>a*b == prod + mulError(...)</code>. */
  private static double mulError(double a, double b, double prod) {
    final double ah = splitHigh(a);
    final double al = a - ah;
    final double bh = splitHigh(b);
    final double bl = b - bh;
    return ((ah * bh - prod) + ah * bl + al * bh) + al * bl;
  }

  /** The rounding error of <code>sum = a+b</code>. */
  private static double addError(double a, double b, double sum) {
    final double bb = sum - a;
    return (a - (sum - bb)) + (b - bb);
  }

  /** <code>Exp(h + l)</code> for <code>|l| &lt;&lt; 1</code>. */
  private static double expDD(double h, double l) {
    return Math.exp(h) * (1.0 + l);
  }

  /**
   * A table row <code>c_d .. c_1, c0_hi, c0_lo</code> at <code>u</code>:
   * <code>c0_hi + (c0_lo + u*(c_1 + u*(... + u*c_d)))</code>.
   */
  private static double tpoly(final double[] c, final int deg, final double u) {
    double q = c[0];
    for (int i = 1; i < deg; i++) {
      q = q * u + c[i];
    }
    return c[deg] + (c[deg + 1] + q * u);
  }

  // ----------------------------------------------------------------------------------------
  // sin and cos of an angle given as a double-double
  // ----------------------------------------------------------------------------------------

  /** <code>{Sin(Pi*s/2), Cos(Pi*s/2)}</code> for <code>|s| &lt;= 1/2</code>. */
  private static void sinCosHalfPi(double s, double[] out) {
    final double t = s * s;
    final double u = (t - SINHP_T_MID) * SINHP_T_IHALF;
    out[0] = s * tpoly(SINHP_T[0], 6, u);
    out[1] = tpoly(COSHP_T[0], 7, u);
  }

  /** Nearest integer, ties to even, for <code>|a| &lt; 2^51</code>. */
  private static double rintSmall(double a) {
    final double m = 6755399441055744.0; // 1.5 * 2^52
    return (a + m) - m;
  }

  /** An integer-valued <code>a</code> modulo 4, in <code>{0, 1, 2, 3}</code>. */
  private static double mod4(double a) {
    if (Math.abs(a) >= 2251799813685248.0) {
      final double q = a % 4.0;
      return q < 0.0 ? q + 4.0 : q;
    }
    final double q = a - 4.0 * rintSmall(0.25 * a);
    return q < 0.0 ? q + 4.0 : q;
  }

  /** Rotates <code>(Sin a, Cos a)</code> in <code>out</code> by <code>q</code> quarter turns. */
  private static void quarterTurn(double q, double sa, double ca, double[] out) {
    if (q == 0.0) {
      out[0] = sa;
      out[1] = ca;
    } else if (q == 1.0) {
      out[0] = ca;
      out[1] = -sa;
    } else if (q == 2.0) {
      out[0] = -sa;
      out[1] = -ca;
    } else {
      out[0] = -ca;
      out[1] = sa;
    }
  }

  /**
   * <code>{Sin(a), Cos(a)}</code> of <code>a = ah + al &gt;= 0</code>: a Cody-Waite reduction by
   * <code>Pi/2</code> in three pieces, then the half-pi polynomials.
   */
  private static void sinCosRad(double ah, double al, double[] out) {
    if (!(ah < 1.0e6)) {
      final double sa = Math.sin(ah);
      final double ca = Math.cos(ah);
      out[0] = sa + al * ca;
      out[1] = ca - al * sa;
      return;
    }
    final double k = rintSmall(ah * TWO_OVER_PI);
    final double r = ((ah - k * PIO2_1) - k * PIO2_2) - k * PIO2_3 + al;
    if (Math.abs(r) < 1e-6 && k != 0.0) {
      // within 1e-6 of a multiple of Pi/2, where the three pieces are too short
      final double sa = Math.sin(ah);
      final double ca = Math.cos(ah);
      out[0] = sa + al * ca;
      out[1] = ca - al * sa;
      return;
    }
    sinCosHalfPi(r * TWO_OVER_PI, out);
    quarterTurn(mod4(k), out[0], out[1], out);
  }

  // ----------------------------------------------------------------------------------------
  // real erf, erfc, erfi
  // ----------------------------------------------------------------------------------------

  /** <code>Erf(x)/(2*x/Sqrt(Pi))</code> Maclaurin coefficients <code>(-1)^k/(k!(2k+1))</code>. */
  private static final double[] ERF_MAC = {1, -0.33333333333333331, 0.10000000000000001,
      -0.023809523809523808, 0.0046296296296296294, -0.00075757575757575758,
      0.00010683760683760684, -1.3227513227513228e-05, 1.4589169000933706e-06,
      -1.4503852223150468e-07, 1.3122532963802806e-08, -1.0892221037148573e-09,
      8.3507027951472397e-11, -5.9477940136376354e-12, 3.9554295164585257e-13,
      -2.4668270102644571e-14, 1.4483264643598138e-15, -8.0327350124157733e-17,
      4.2214072888070882e-18, -2.1078551914421359e-19, 1.0025164934907719e-20,
      -4.5518467589281999e-22};

  /** <code>(2k-1)!!</code>, the asymptotic series of <code>Erfi</code>. */
  private static final double[] ERFI_ASY = {1, 1, 3, 15, 105, 945, 10395, 135135, 2027025,
      34459425, 654729075, 13749310575.0, 316234143225.0, 7905853580625.0, 213458046676875.0};

  /** <code>erfcx(x) = Exp(x^2)*Erfc(x)</code> for <code>1/2 &lt;= x &lt; 6</code>. */
  private static double erfcxTable(double x) {
    final int i = (int) ((x - 0.5) * 2.0);
    final double mid = 0.75 + 0.5 * i;
    return tpoly(ERFCX_T[i], 13, (x - mid) * 4.0);
  }

  /** <code>Erf(x)</code> for <code>|x| &lt; 1/2</code>. */
  private static double erfSmall(double x) {
    final double t = x * x;
    double p = ERF_MAC[12];
    for (int k = 11; k >= 0; k--) {
      p = p * t + ERF_MAC[k];
    }
    return TWO_OVER_SQRTPI * x * p;
  }

  /**
   * <code>Erfc(x)</code> for <code>x &gt;= 1/2</code>, given <code>emx2 = Exp(-x^2)</code> or a
   * negative value to have it formed here.
   */
  private static double erfcTail(double x, double emx2) {
    if (x < 6.0) {
      if (emx2 < 0.0) {
        final double h = x * x;
        emx2 = expDD(-h, -mulError(x, x, h));
      }
      return emx2 * erfcxTable(x);
    }
    if (x >= 27.4) {
      return 0.0; // below half the least subnormal
    }
    final double h = x * x;
    final double l = mulError(x, x, h);
    final double t = 1.0 / h;
    final double gx = tpoly(ERFCX2_T[0], 10, (t - ERFCX2_T_MID) * ERFCX2_T_IHALF) / x;
    if (h <= 700.0) {
      return (emx2 >= 0.0 ? emx2 : expDD(-h, -l)) * gx;
    }
    // one rounding into the subnormal range
    return (expDD(700.0 - h, -l) * gx) * EXP_M700;
  }

  /** <code>Erf(x)</code> for <code>x &gt;= 0</code> with <code>emx2 = Exp(-x^2)</code> known. */
  private static double erfRealFast(double x, double emx2) {
    return (x < 0.5) ? erfSmall(x) : 1.0 - erfcTail(x, emx2);
  }

  /** <code>Erfc(x)</code> for <code>x &gt;= 0</code> with <code>emx2 = Exp(-x^2)</code> known. */
  private static double erfcRealFast(double x, double emx2) {
    return (x < 0.5) ? 1.0 - erfSmall(x) : erfcTail(x, emx2);
  }

  /**
   * <code>Erfi(x)</code> of a real <code>x</code>, or <code>NaN</code> if it overflows.
   *
   * <p>
   * <code>Erfi(x) = Exp(x^2)*g(x)</code> with <code>g(x) = Exp(-x^2)*Erfi(x)</code> (the scaled
   * Dawson function) from piecewise polynomials below 10 and its asymptotic series above.
   */
  public static double erfi(double x) {
    final double ax = Math.abs(x);
    if (!(ax < 26.72)) {
      return Double.NaN;
    }
    double v;
    if (ax < 0.5) {
      final double t = ax * ax;
      double p = Math.abs(ERF_MAC[12]);
      for (int k = 11; k >= 0; k--) {
        p = p * t + Math.abs(ERF_MAC[k]);
      }
      v = TWO_OVER_SQRTPI * ax * p;
    } else {
      double g;
      if (ax < 4.0) {
        final int i = (int) ((ax - 0.5) * 2.0);
        g = tpoly(DAW1_T[i], 14, (ax - (0.75 + 0.5 * i)) * 4.0);
      } else if (ax < 10.0) {
        final int i = (int) ((ax - 4.0) * 2.0);
        g = tpoly(DAW2_T[i], 11, (ax - (4.25 + 0.5 * i)) * 4.0);
      } else {
        final double w = 1.0 / (2.0 * ax * ax);
        double p = ERFI_ASY[14];
        for (int k = 13; k >= 0; k--) {
          p = p * w + ERFI_ASY[k];
        }
        g = SQRTPI_INV / ax * p;
      }
      final double x2 = ax * ax;
      final double x2l = mulError(ax, ax, x2);
      if (x2 < 709.0) {
        v = expDD(x2, x2l) * g;
      } else {
        v = (expDD(x2 - 64.0, x2l) * g) * EXP_P64;
      }
    }
    if (!Double.isFinite(v)) {
      return Double.NaN;
    }
    return x < 0.0 ? -v : v;
  }

  // ----------------------------------------------------------------------------------------
  // Salzer's series
  // ----------------------------------------------------------------------------------------

  /** <code>e^(-n^2/4)</code>, <code>n = 0..54</code>. */
  private static final double[] SALZER_G = {1, 0.77880078307140488, 0.36787944117144233,
      0.10539922456186433, 0.018315638888734179, 0.0019304541362277093, 0.00012340980408667956,
      4.7851173921290088e-06, 1.1253517471925912e-07, 1.6052280551856116e-09,
      1.3887943864964021e-11, 7.2877240958196922e-14, 2.3195228302435696e-16,
      4.4777324417183015e-19, 5.2428856633634639e-22, 3.7233631217505106e-25,
      1.6038108905486379e-28, 4.1900931944943974e-32, 6.6396771995807348e-36,
      6.3815034480607908e-40, 3.7200759760208361e-44, 1.3153258948574645e-48,
      2.8207700884601352e-53, 3.6690596154291634e-58, 2.8946403116483003e-63,
      1.3851193699226017e-68, 4.0200602157433552e-74, 7.0766981754295491e-80,
      7.5558190197119609e-86, 4.8931122620973616e-92, 1.9219477278238491e-98,
      4.5787899692915775e-105, 6.6162610567094853e-112, 5.7986554185640377e-119,
      3.0824406969490981e-126, 9.9383644134836838e-134, 1.9435148500492928e-141,
      2.3052263152355648e-149, 1.6584104776811452e-157, 7.2364115192480096e-166,
      1.9151695967140057e-174, 3.0742839670838354e-183, 2.9931844522601929e-192,
      1.7675664115509242e-201, 6.3309773362105915e-211, 1.3753667993264065e-220,
      1.8122540257939923e-230, 1.4483461489989888e-240, 7.0206677985047347e-251,
      2.0641309109295095e-261, 3.6808558548018004e-272, 3.9811921806329143e-283,
      2.6117417612840555e-294, 1.0392022621430825e-305, 2.507972078894169e-317};

  /** <code>e^(-n)</code>, <code>n = 0..55</code>. */
  private static final double[] SALZER_EXPN = {1, 0.36787944117144233, 0.1353352832366127,
      0.049787068367863944, 0.018315638888734179, 0.006737946999085467, 0.0024787521766663585,
      0.00091188196555451624, 0.00033546262790251185, 0.00012340980408667956,
      4.5399929762484854e-05, 1.6701700790245659e-05, 6.1442123533282098e-06,
      2.2603294069810542e-06, 8.3152871910356788e-07, 3.0590232050182579e-07,
      1.1253517471925912e-07, 4.1399377187851668e-08, 1.5229979744712629e-08,
      5.6027964375372678e-09, 2.0611536224385579e-09, 7.5825604279119066e-10,
      2.7894680928689246e-10, 1.026187963170189e-10, 3.7751345442790977e-11,
      1.3887943864964021e-11, 5.1090890280633251e-12, 1.8795288165390832e-12,
      6.914400106940203e-13, 2.5436656473769228e-13, 9.3576229688401748e-14,
      3.4424771084699768e-14, 1.2664165549094176e-14, 4.6588861451033977e-15,
      1.713908431542013e-15, 6.3051167601469892e-16, 2.3195228302435696e-16,
      8.5330476257440658e-17, 3.1391327920480296e-17, 1.1548224173015786e-17,
      4.2483542552915889e-18, 1.5628821893349888e-18, 5.7495222642935599e-19,
      2.1151310375910805e-19, 7.7811322411337966e-20, 2.8625185805493937e-20,
      1.0530617357553812e-20, 3.8739976286871868e-21, 1.4251640827409352e-21,
      5.2428856633634639e-22, 1.9287498479639178e-22, 7.0954741622847037e-23,
      2.6102790696677047e-23, 9.6026800545086756e-24, 3.5326285722008071e-24,
      1.2995814250075031e-24};

  private static final double SALZER_EXP_M14 = 0.77880078307140488; // e^(-1/4)

  private static final double SALZER_EXP_M12 = 0.60653065971263342; // e^(-1/2)

  /** Switch between the hyperbolic recurrence and the scaled Gaussian weights. */
  private static final double SALZER_YS = 0.5;

  /** Beyond this <code>Exp(Y^2)</code> overflows. */
  private static final double SALZER_YMAX = 26.0;

  /**
   * <code>Erf(X + I*Y) - Erf(X)</code> for <code>X, Y &gt;= 0</code>,
   * <code>Y &lt;= SALZER_YMAX</code>:
   *
   * <pre>
   * Erf(X)
   *   + e^(-X^2)/(2*Pi*X) ((1 - cos 2XY) + I sin 2XY)
   *   + (2/Pi) e^(-X^2) sum_(n&gt;=1) e^(-n^2/4)/(n^2 + 4X^2) (f_n + I g_n),
   * f_n = 2X - 2X cosh(nY) cos(2XY) + n sinh(nY) sin(2XY),
   * g_n = 2X cosh(nY) sin(2XY) + n sinh(nY) cos(2XY).
   * </pre>
   *
   * For <code>Y &gt; 1/2</code> the weights <code>e^(-n^2/4) cosh(nY)</code> are carried as
   * <code>e^(Y^2) (P_n + Q_n)/2</code>, <code>P_n = e^(-(n/2 - Y)^2)</code>, generated outward from
   * the peak <code>n0 = round(2Y)</code> and summed with compensated additions.
   *
   * @param emx2 <code>Exp(-X^2)</code>
   * @param sn <code>Sin(X*Y)</code>
   * @param cn <code>Cos(X*Y)</code>
   * @param snx <code>Sin(X*Y)/X</code>, the limit <code>Y</code> at <code>X = 0</code>
   * @param eyh high part of <code>Y^2 - X^2</code>, read for <code>Y &gt; 1/2</code>
   * @param eyl low part of <code>Y^2 - X^2</code>
   * @param out receives the real and imaginary part
   */
  private static void salzerCore(double x, double y, double emx2, double sn, double cn,
      double snx, double eyh, double eyl, double[] out) {
    final double fx2 = 4.0 * x * x;
    final double s = 2.0 * sn * cn; // sin 2XY
    final double c = (cn - sn) * (cn + sn); // cos 2XY
    final double omc = 2.0 * sn * sn; // 1 - cos 2XY
    final int bigN = (int) (2.0 * y + 13.0);

    if (y <= SALZER_YS) {
      // recurrence on (cosh nY, sinh nY)
      final double em = Math.expm1(y);
      final double e = em + 1.0;
      final double sh1 = 0.5 * (em + em / e);
      final double ch1 = 0.5 * (e + 1.0 / e);
      double ch = ch1;
      double sh = sh1;
      double s0 = 0.0;
      double sd = 0.0;
      double ss = 0.0;
      for (int n = 1; n <= bigN; n++) {
        final double en = SALZER_G[n] / ((double) n * n + fx2);
        // cosh - 1 without cancellation while nY is small
        final double chm1 = (ch < 1.5) ? sh * sh / (ch + 1.0) : ch - 1.0;
        s0 += en;
        sd += en * chm1;
        ss += en * n * sh;
        final double nch = ch * ch1 + sh * sh1;
        sh = sh * ch1 + ch * sh1;
        ch = nch;
      }
      final double re = sn * snx + 4.0 * x * omc * s0 - 4.0 * x * c * sd + 2.0 * s * ss;
      final double im = cn * snx + 2.0 * c * ss + 4.0 * x * s * (s0 + sd);
      out[0] = emx2 * INV_PI * re;
      out[1] = emx2 * INV_PI * im;
      return;
    }

    int n0 = (int) (2.0 * y + 0.5);
    if (n0 < 1) {
      n0 = 1;
    }
    if (n0 > bigN) {
      n0 = bigN;
    }
    final double d0 = 0.5 * n0 - y; // exact (Sterbenz)
    final double dsh = d0 * d0;
    final double dsl = mulError(d0, d0, dsh);
    final double sch = eyh - dsh;
    final double scl = addError(eyh, -dsh, sch);
    final double scale = expDD(sch, scl + (eyl - dsl)); // e^(Y^2-X^2) P_n0
    final double ed = Math.exp(-d0);
    // e^(-2Y) = e^(-n0) e^(2 d0); the mirror terms Q_n are negligible once 2Y > 38
    final double eY2 = SALZER_EXPN[n0] / (ed * ed);
    double mn0 = 0.0; // e^(-2 n0 Y)
    if (y < 19.0) {
      double b2 = eY2;
      mn0 = 1.0;
      for (int k = n0; k != 0; k >>= 1) {
        if ((k & 1) != 0) {
          mn0 *= b2;
        }
        b2 *= b2;
      }
    }

    double sc = 0.0;
    double ssScaled = 0.0;
    double s0 = 0.0;
    double eSc = 0.0;
    double eSs = 0.0;
    {
      // forward: n = n0 .. N
      double p = 1.0;
      double r = ed * SALZER_EXP_M14; // P_(n+1)/P_n
      double m = mn0;
      for (int n = n0; n <= bigN; n++) {
        final double q = 1.0 / ((double) n * n + fx2);
        final double qq = p * m;
        if (n <= 13) {
          s0 += SALZER_G[n] * q;
        }
        double v = (p + qq) * q;
        double t = sc + v;
        eSc += v - (t - sc);
        sc = t;
        v = n * (p - qq) * q;
        t = ssScaled + v;
        eSs += v - (t - ssScaled);
        ssScaled = t;
        p *= r;
        r *= SALZER_EXP_M12;
        m *= eY2;
      }
    }
    {
      // backward: n = n0-1 .. 1
      double p = 1.0;
      double r = SALZER_EXP_M14 / ed; // P_(n-1)/P_n
      double m = mn0;
      final double eY2i = (mn0 != 0.0) ? 1.0 / eY2 : 0.0;
      for (int n = n0 - 1; n >= 1; n--) {
        p *= r;
        r *= SALZER_EXP_M12;
        m *= eY2i;
        final double q = 1.0 / ((double) n * n + fx2);
        final double qq = p * m;
        if (n <= 13) {
          s0 += SALZER_G[n] * q;
        }
        double v = (p + qq) * q;
        double t = sc + v;
        eSc += v - (t - sc);
        sc = t;
        v = n * (p - qq) * q;
        t = ssScaled + v;
        eSs += v - (t - ssScaled);
        ssScaled = t;
      }
    }
    sc = 0.5 * (sc + eSc);
    ssScaled = 0.5 * (ssScaled + eSs);
    out[0] = INV_PI
        * (emx2 * (sn * snx + 4.0 * x * s0) + scale * (2.0 * s * ssScaled - 4.0 * x * c * sc));
    out[1] = INV_PI * (emx2 * cn * snx + scale * (2.0 * c * ssScaled + 4.0 * x * s * sc));
  }

  /**
   * <code>Erf(X + I*Y) - Erf(X)</code> by Salzer's series from the exact argument, also
   * returning <code>Exp(-X^2)</code> in <code>out[2]</code>.
   */
  private static void salzer(double x, double y, double[] out) {
    final double x2 = x * x;
    final double x2l = mulError(x, x, x2);
    final double p = x * y;
    final double pl = mulError(x, y, p);
    final double emx2 = expDD(-x2, -x2l);
    final double[] sc = new double[2];
    sinCosRad(p, pl, sc);
    final double snx = (x == 0.0) ? y : sc[0] / x;
    double eyh = 0.0;
    double eyl = 0.0;
    if (y > SALZER_YS) {
      final double y2 = y * y;
      final double y2l = mulError(y, y, y2);
      eyh = y2 - x2;
      eyl = addError(y2, -x2, eyh) + (y2l - x2l);
    }
    salzerCore(x, y, emx2, sc[0], sc[1], snx, eyh, eyl, out);
    out[2] = emx2;
  }

  // ----------------------------------------------------------------------------------------
  // Faddeeva function on the box [0,4) x [0,4)
  // ----------------------------------------------------------------------------------------

  /**
   * <code>w(a + I*b)</code> for <code>0 &lt;= a, b &lt; 4</code>: the degree 20 Taylor polynomial
   * about the centre of the <code>0.5 x 0.5</code> cell.
   */
  private static void wBox(double a, double b, double[] out) {
    final int i = (int) (a * 2.0);
    final int j = (int) (b * 2.0);
    final double[] c = W_T[8 * i + j];
    final double hr = a - (0.5 * i + 0.25);
    final double hi = b - (0.5 * j + 0.25);
    // Horner from the highest coefficient (c[40], c[41]) down to (c[0], c[1])
    double pr = c[40];
    double pi = c[41];
    for (int k = 19; k >= 0; k--) {
      final double t = pr * hr - pi * hi + c[2 * k];
      pi = pr * hi + pi * hr + c[2 * k + 1];
      pr = t;
    }
    out[0] = pr;
    out[1] = pi;
  }

  /** <code>Erf(X + I*Y)</code> for <code>|z| &lt;= 0.8</code> by its Maclaurin series. */
  private static void erfMaclaurin(double x, double y, double[] out) {
    final double wr = (x - y) * (x + y);
    final double wi = 2.0 * x * y;
    double pr = ERF_MAC[18];
    double pi = 0.0;
    for (int k = 17; k >= 0; k--) {
      final double t = pr * wr - pi * wi + ERF_MAC[k];
      pi = pr * wi + pi * wr;
      pr = t;
    }
    out[0] = TWO_OVER_SQRTPI * (pr * x - pi * y);
    out[1] = TWO_OVER_SQRTPI * (pr * y + pi * x);
  }

  /**
   * <code>Erfc(X + I*Y) = Exp(-z^2)*w(I*z)</code> for <code>0 &lt;= X, Y &lt; 4</code>, with
   * <code>w(I*z) = conj(w(Y + I*X))</code> and the exponent and phase from exact products.
   */
  private static void erfcFromW(double x, double y, double[] out) {
    final double x2 = x * x;
    final double x2l = mulError(x, x, x2);
    final double y2 = y * y;
    final double y2l = mulError(y, y, y2);
    final double eh = y2 - x2;
    final double el = addError(y2, -x2, eh);
    final double mag = expDD(eh, el + (y2l - x2l));
    final double p = x * y;
    final double pl = mulError(x, y, p);
    final double[] sc = new double[2];
    sinCosRad(2.0 * p, 2.0 * pl, sc);
    final double s2 = sc[0];
    final double c2 = sc[1];
    wBox(y, x, out);
    final double wr = out[0];
    final double wi = out[1];
    // (c2 - I s2)(wr - I wi)
    out[0] = mag * (c2 * wr - s2 * wi);
    out[1] = -mag * (s2 * wr + c2 * wi);
  }

  /** Below this <code>X*Y</code> the series about the nearer axis is used. */
  private static final double ERF_AXIS_XY = 0.25;

  /**
   * The Taylor series about the real axis (<code>X &gt;= Y &gt;= 0</code>):
   * <code>Erf(X + I*Y) - Erf(X) = (2/Sqrt(Pi)) Exp(-X^2) sum_k (-1)^k H_k(X) (I*Y)^(k+1)/(k+1)!</code>.
   */
  private static void erfAxisReal(double x, double y, double emx2, double[] out) {
    double hm = 0.0;
    double h = 1.0;
    double t = y;
    double sr = 0.0;
    double si = 0.0;
    for (int k = 0; k < 60; k++) {
      final double term = h * t;
      switch (k & 3) {
        case 0:
          si += term;
          break;
        case 1:
          sr += term;
          break;
        case 2:
          si -= term;
          break;
        default:
          sr -= term;
          break;
      }
      if (k > 2 && Math.abs(term) < 1e-19 * (Math.abs(sr) + Math.abs(si))) {
        break;
      }
      final double hn = 2.0 * x * h - 2.0 * k * hm;
      hm = h;
      h = hn;
      t *= y / (k + 2);
    }
    out[0] = TWO_OVER_SQRTPI * emx2 * sr;
    out[1] = TWO_OVER_SQRTPI * emx2 * si;
  }

  /**
   * The Taylor series about the imaginary axis (<code>Y &gt; X &gt;= 0</code>):
   * <code>Erf(X + I*Y) - I*Erfi(Y) = (2/Sqrt(Pi)) Exp(Y^2) sum_k (-I)^k h_k(Y) X^(k+1)/(k+1)!</code>,
   * <code>h_(k+1) = 2*Y*h_k + 2*k*h_(k-1)</code>.
   */
  private static void erfAxisImag(double x, double y, double[] out) {
    double hm = 0.0;
    double h = 1.0;
    double t = x;
    double sr = 0.0;
    double si = 0.0;
    for (int k = 0; k < 60; k++) {
      final double term = h * t;
      switch (k & 3) {
        case 0:
          sr += term;
          break;
        case 1:
          si -= term;
          break;
        case 2:
          sr -= term;
          break;
        default:
          si += term;
          break;
      }
      if (k > 2 && Math.abs(term) < 1e-19 * (Math.abs(sr) + Math.abs(si))) {
        break;
      }
      final double hn = 2.0 * y * h + 2.0 * k * hm;
      hm = h;
      h = hn;
      t *= x / (k + 2);
    }
    final double y2 = y * y;
    final double c = TWO_OVER_SQRTPI * expDD(y2, mulError(y, y, y2));
    out[0] = c * sr;
    out[1] = c * si;
  }

  /** <code>Erf(X + I*Y)</code> for <code>X, Y &gt;= 0</code>; false beyond the covered range. */
  private static boolean erfQuadrant(double x, double y, double[] out) {
    if (x * x + y * y <= 0.64) {
      erfMaclaurin(x, y, out);
      return true;
    }
    if (x < 4.0 && y < 4.0) {
      if (x * y < ERF_AXIS_XY) {
        if (x >= y) {
          final double h = x * x;
          final double emx2 = expDD(-h, -mulError(x, x, h));
          erfAxisReal(x, y, emx2, out);
          out[0] += erfRealFast(x, emx2);
        } else {
          erfAxisImag(x, y, out);
          out[1] += erfi(y);
        }
        return true;
      }
      erfcFromW(x, y, out);
      out[0] = 1.0 - out[0];
      out[1] = -out[1];
      return true;
    }
    if (y > SALZER_YMAX) {
      if (!erfcAsymptotic(x, y, out)) {
        return false;
      }
      out[0] = 1.0 - out[0];
      out[1] = -out[1];
      return true;
    }
    final double[] a = new double[3];
    salzer(x, y, a);
    out[0] = erfRealFast(x, a[2]) + a[0];
    out[1] = a[1];
    return true;
  }

  /**
   * <code>Erfc(X + I*Y)</code> for <code>X &gt;= 0</code> and <code>Y &gt; 26</code> by the
   * asymptotic expansion
   * <code>Exp(-z^2)/(z*Sqrt(Pi)) * sum_k (-1)^k (2k-1)!!/(2*z^2)^k</code>, which needs a few
   * terms only for <code>|z| &gt; 26</code>. <code>Exp(-z^2)</code> is formed from the exact
   * exponent <code>Y^2-X^2</code> and phase <code>2*X*Y</code>.
   *
   * @return false if the value overflows
   */
  private static boolean erfcAsymptotic(double x, double y, double[] out) {
    final double x2 = x * x;
    final double x2l = mulError(x, x, x2);
    final double y2 = y * y;
    final double y2l = mulError(y, y, y2);
    final double eh = y2 - x2;
    final double el = addError(y2, -x2, eh) + (y2l - x2l);
    if (!(eh <= 709.0)) {
      return false;
    }
    if (eh < -746.0) {
      out[0] = 0.0;
      out[1] = 0.0;
      return true;
    }
    final double mag = expDD(eh, el);
    final double p = x * y;
    final double pl = mulError(x, y, p);
    final double[] sc = new double[2];
    sinCosRad(2.0 * p, 2.0 * pl, sc);
    // Exp(-z^2) = mag*(c2 - I*s2)
    final double er = mag * sc[1];
    final double ei = -mag * sc[0];
    // the series in 1/(2*z^2); beyond |z| = 1e100 its first term is all there is
    double sr = 1.0;
    double si = 0.0;
    if (y < 1.0e100) {
      final double zr2 = (x - y) * (x + y);
      final double zi2 = 2.0 * x * y;
      final double den = 2.0 * (zr2 * zr2 + zi2 * zi2);
      final double ir = zr2 / den;
      final double ii = -zi2 / den;
      double tr = 1.0;
      double ti = 0.0;
      for (int k = 1; k < 60; k++) {
        // t *= -(2k-1)/(2 z^2)
        final double f = -(2.0 * k - 1.0);
        final double nr = f * (tr * ir - ti * ii);
        ti = f * (tr * ii + ti * ir);
        tr = nr;
        sr += tr;
        si += ti;
        if (Math.abs(tr) + Math.abs(ti) < 1.0e-17 * (Math.abs(sr) + Math.abs(si))) {
          break;
        }
      }
    }
    // Exp(-z^2)*S/(z*Sqrt(Pi)), 1/z = conj(z)/|z|^2 scaled against overflow
    final double scale = Math.max(x, y);
    final double xs = x / scale;
    final double ys = y / scale;
    final double n = (xs * xs + ys * ys) * scale;
    final double qr = xs / n;
    final double qi = -ys / n;
    final double ar = er * sr - ei * si;
    final double ai = er * si + ei * sr;
    out[0] = SQRTPI_INV * (ar * qr - ai * qi);
    out[1] = SQRTPI_INV * (ar * qi + ai * qr);
    return true;
  }

  private static double[] finite(double re, double im) {
    if (Double.isFinite(re) && Double.isFinite(im)) {
      return new double[] {re, im};
    }
    return null;
  }

  /**
   * <code>Erf(x + I*y)</code>.
   *
   * @return <code>{re, im}</code>, or <code>null</code> if not covered or not a finite double
   */
  public static double[] erf(double x, double y) {
    if (!Double.isFinite(x) || !Double.isFinite(y)) {
      return null;
    }
    final double[] f = new double[3];
    if (!erfQuadrant(Math.abs(x), Math.abs(y), f)) {
      return null;
    }
    // odd, and real on the real axis
    return finite(x < 0.0 || (x == 0.0 && 1.0 / x < 0.0) ? -f[0] : f[0],
        y < 0.0 || (y == 0.0 && 1.0 / y < 0.0) ? -f[1] : f[1]);
  }

  /**
   * <code>Erfc(x + I*y)</code>.
   *
   * @return <code>{re, im}</code>, or <code>null</code> if not covered or not a finite double
   */
  public static double[] erfc(double x, double y) {
    if (!Double.isFinite(x) || !Double.isFinite(y)) {
      return null;
    }
    final double ax = Math.abs(x);
    final double ay = Math.abs(y);
    final boolean negY = y < 0.0 || (y == 0.0 && 1.0 / y < 0.0);
    final double[] f = new double[3];
    if (x >= 0.0 && ax * ax + ay * ay > 0.25) {
      if (ax < 4.0 && ay < 4.0 && ax * ay < ERF_AXIS_XY && ax >= ay) {
        // near the real axis: erfc(X) - D
        final double h = ax * ax;
        final double emx2 = expDD(-h, -mulError(ax, ax, h));
        erfAxisReal(ax, ay, emx2, f);
        return finite(erfcRealFast(ax, emx2) - f[0], negY ? f[1] : -f[1]);
      }
      if (ax < 4.0 && ay < 4.0 && ax * ay < ERF_AXIS_XY) {
        // near the imaginary axis: 1 - erf
        if (!erfQuadrant(ax, ay, f)) {
          return null;
        }
        return finite(1.0 - f[0], negY ? f[1] : -f[1]);
      }
      if (ax < 4.0 && ay < 4.0) {
        erfcFromW(ax, ay, f);
        return finite(f[0], negY ? -f[1] : f[1]);
      }
      // erfc(X) - (erf(X + I*Y) - erf(X)): no cancellation against 1 when erfc is small; from
      // X = 27.4 on erfc(X) is below the double range, while the difference carries
      // Exp(Y^2 - X^2) and may still be in it
      if (ay > SALZER_YMAX) {
        if (!erfcAsymptotic(ax, ay, f) || (f[0] == 0.0 && f[1] == 0.0)) {
          return null;
        }
        return finite(f[0], negY ? -f[1] : f[1]);
      }
      salzer(ax, ay, f);
      final double fr = erfcRealFast(ax, f[2]) - f[0];
      final double fi = -f[1];
      if (fr == 0.0 && fi == 0.0) {
        return null; // underflow, left to the caller's convention
      }
      return finite(fr, negY ? -fi : fi);
    }
    // x < 0 (erfc = 1 - erf is about 1..2) or |z| small
    if (!erfQuadrant(ax, ay, f)) {
      return null;
    }
    final double fr = (x < 0.0) ? -f[0] : f[0];
    final double fi = negY ? -f[1] : f[1];
    return finite(1.0 - fr, -fi);
  }

  /**
   * <code>Erfi(x + I*y) = -I*Erf(I*z)</code>.
   *
   * @return <code>{re, im}</code>, or <code>null</code> if not covered or not a finite double
   */
  public static double[] erfi(double x, double y) {
    final double[] e = erf(-y, x);
    if (e == null) {
      return null;
    }
    return new double[] {e[1], -e[0]};
  }

  // ----------------------------------------------------------------------------------------
  // Generated polynomial tables. Each row holds the coefficients
  // of u^d .. u^1 and then the constant term as hi, lo. W_T holds per cell the real and
  // imaginary parts of the Taylor coefficients of w, of degree 0 to 20.
  // ----------------------------------------------------------------------------------------

  private static final double[][] ERFCX_T = {
      {-2.1463289279578493e-13, 2.585599367357183e-12, -2.940348914756972e-11,
          3.3060435565014089e-10, -3.5801438250789833e-09, 3.7195450896335891e-08,
          -3.6935621775979211e-07, 3.4885738732198706e-06, -3.114966996088564e-05,
          0.00026090055675162256, -0.0020286884686700011, 0.014434883221955935,
          -0.091993172913948845, 0.50693765029314486, -5.1246841920062868e-17},
      {-2.4751971707679498e-14, 3.2792698382308406e-13, -4.134526075119391e-12,
          5.1541247449058564e-11, -6.2164352840924157e-10, 7.2322189424948945e-09,
          -8.0919368117483784e-08, 8.6745847038166433e-07, -8.8687769853124598e-06,
          8.5981891605258742e-05, -0.00078466053743605417, 0.006674723218537403,
          -0.052205468991152464, 0.36782291645236109, 3.607072079916514e-19},
      {-3.4468073314145138e-15, 5.0106344274055084e-14, -6.9800192100477697e-13,
          9.6183001328507728e-12, -1.2876651117526543e-10, 1.670918856329355e-09,
          -2.0967611888717531e-08, 2.5371204134690283e-07, -2.9501705580352682e-06,
          3.2829371903672606e-05, -0.00034781242564669245, 0.0034852268804429517,
          -0.0327440863786213, 0.28497223473743638, 8.5680678203841265e-18},
      {-5.6764479834772385e-16, 9.0285651000044855e-15, -1.3842367546873179e-13,
          2.1007977934821924e-12, -3.1090870108434632e-11, 4.4789512426041272e-10,
          -6.2695972361924414e-09, 8.5091655732782494e-08, -1.1169223473185161e-06,
          1.4136700602967932e-05, -0.00017190719931937571, 0.001999539213169141,
          -0.02212162570218729, 0.23108725873039188, -5.7433590693734701e-18},
      {-1.0847655660329031e-16, 1.8816092271791067e-15, -3.1618731471706809e-14,
          5.2637569209356109e-13, -8.5730504053862697e-12, 1.3641597543517909e-10,
          -2.1178350568482745e-09, 3.2026806763082239e-08, -4.7089363767687664e-07,
          6.7171167394121682e-06, -9.2724029640593381e-05, 0.001234912061707679,
          -0.015809409390158711, 0.19366209627906869, -1.2015097410980445e-17},
      {-2.3647193114602224e-17, 4.4576483107070906e-16, -8.1750393057483755e-15,
          1.4865392573332567e-13, -2.6520085237328398e-12, 4.6368900337520466e-11,
          -7.9374025049740104e-10, 1.3286232618759893e-08, -2.1717047809422727e-07,
          3.4609553809934849e-06, -5.3679239076680842e-05, 0.00080858068018863484,
          -0.011799850580292594, 0.16633534842682188, -6.1319103876987767e-19},
      {-5.7919755237342193e-18, 1.1822811391010087e-16, -2.3562615397644294e-15,
          4.6599785350317236e-14, -9.0637361743755091e-13, 1.7323694828089317e-11,
          -3.2511430597063878e-10, 5.9854309997986076e-09, -1.0798786613700727e-07,
          1.9071186800608639e-06, -3.2926294846392347e-05, 0.00055492222045783115,
          -0.0091140643831808827, 0.14558972127503855, -1.3715613215488988e-17},
      {-1.572855915960239e-18, 3.464057794827773e-17, -7.4713675226060136e-16,
          1.6003116101420737e-14, -3.3780809631697506e-13, 7.0230140839413469e-12,
          -1.4371341769277275e-10, 2.8926009873373831e-09, -5.7222168177107583e-08,
          1.1116217064069109e-06, -2.1186455736001536e-05, 0.00039574164211704684,
          -0.007236082853653833, 0.12934527478598792, -1.291749990896316e-17},
      {-4.6806560531982464e-19, 1.1083358204341322e-17, -2.5766961323483821e-16,
          5.9531551655776678e-15, -1.3578700234248099e-13, 3.0562129851083572e-12,
          -6.7844709477031084e-11, 1.4846471070051045e-09, -3.2007598764288934e-08,
          6.7940743765881237e-07, -1.4189045266088914e-05, 0.00029133289806077125,
          -0.0058758621495407877, 0.11630270721024731, -3.1774763029766119e-18},
      {-1.5110350537465862e-19, 3.8337895366992007e-18, -9.5708241558982669e-17,
          2.3759351424942052e-15, -5.8317655349098968e-14, 1.4147478442928538e-12,
          -3.3908575455358766e-11, 8.0262394535889626e-10, -1.8753983078193703e-08,
          4.3235959401961982e-07, -9.8297107975397389e-06, 0.00022025943375696231,
          -0.0048613611680371621, 0.1056127354688918, 2.7634222986244153e-18},
      {-5.2452197211357137e-20, 1.4213315274561339e-18, -3.79659659064509e-17,
          1.0090202844901735e-15, -2.6548713258425354e-14, 6.9134276644083136e-13,
          -1.7812390821590837e-11, 4.5393092554588553e-10, -1.1437905173619582e-08,
          2.848605034195586e-07, -7.0093077855946228e-06, 0.00017032961517810219,
          -0.0040858045359506209, 0.096698778169713923, -1.7756570392502226e-18},
  };

  private static final double[][] ERFCX2_T = {
      {1.3882079089691593e-14, -1.4099402756741329e-13, 1.5299551962912177e-12,
          -1.8967621191703385e-11, 2.6548781079434583e-10, -4.2926661707052483e-09,
          8.287606241195554e-08, -2.0081529597221729e-06, 6.644128385530334e-05,
          -0.0035699920043331264, 0.56016488684534838, 2.3348399689394879e-17},
  };

  private static final double[][] DAW1_T = {
      {4.1928509177656599e-13, -4.3512471392678494e-12, -3.5246135539852033e-11,
          5.6832319198202494e-10, 1.6803206882951338e-09, -5.5066167488977167e-08,
          3.0771844840361804e-08, 3.8724565042219908e-06, -1.3586767281368121e-05,
          -0.00017609726493192189, 0.0011804566242611266, 0.0035025207248745552,
          -0.048282174150978109, 0.060786025055923815, 0.59015671124787827,
          -3.0306458354097698e-18},
      {-1.2293942227781154e-13, 3.5651203734616447e-12, -3.7104562698326562e-12,
          -3.6181076467801799e-10, 2.1649114329322987e-09, 2.1026965164969582e-08,
          -2.7832760799544152e-07, -1.2231106310599512e-07, 1.8424522227076993e-05,
          -8.5273189179012186e-05, -0.00045801112101595618, 0.0057009831718767764,
          -0.013848559986871184, -0.067580796190666509, 0.55948094074327137,
          4.3119326063834253e-17},
      {-2.9296122375601564e-14, -1.5010477357576972e-12, 1.3931277494964472e-11,
          6.4136057086392517e-11, -1.7866186095346087e-09, 6.8553163520066979e-09,
          9.4942513403780036e-08, -1.1581759693634755e-06, 2.0309108144221608e-06,
          5.0641477182346878e-05, -0.00045197405934165134, 0.00113815932830793,
          0.0064960546007745285, -0.072788206084823698, 0.40558056898137357,
          -1.342369464684246e-17},
      {4.4594061446612948e-14, 1.9392747997099772e-13, -6.9071722375763688e-12,
          4.0060906084890711e-11, 3.0279141711211516e-10, -6.2480254837909573e-09,
          3.200872413446966e-08, 1.6177778139733604e-07, -3.5045582986579989e-06,
          2.2481469418698079e-05, -3.4114426449198409e-05, -0.0005922289387785333,
          0.0064217220953826778, -0.043582004327755219, 0.28949048542367412,
          -1.8083939832420209e-17},
      {-1.7893092980597655e-14, 1.0400519502250397e-13, 9.2269274904089978e-13,
          -2.0664325657831097e-11, 1.3864155511382667e-10, 2.9302019632416414e-10,
          -1.4314484721921766e-08, 1.3636211759289989e-07, -5.8385629457052422e-07,
          -1.2138594212950993e-06, 4.1377555778083942e-05, -0.00040659873669565081,
          0.0031485043187531002, -0.02487517782558912, 0.22325088698143075,
          -1.3268893757100936e-17},
      {2.5105428509530973e-15, -4.5418518531554552e-14, 3.016645053692425e-13,
          8.4339537701226202e-13, -3.9915267983533269e-11, 4.4462705435302979e-10,
          -2.5869348020611916e-09, 1.6170373763086122e-09, 1.4454234244199476e-07,
          -1.9696045552623353e-06, 1.8666826781163268e-05, -0.00016388456594306503,
          0.0015331609002626071, -0.015997862120780418, 0.18344163316594372,
          6.8522781803759234e-18},
      {3.3200302333697779e-16, 2.7886168895353425e-15, -8.0557457227145515e-14,
          8.9309866081946115e-13, -5.6601998392130361e-12, 6.3423581090814158e-12,
          3.5767816136379591e-10, -5.8218422297612811e-09, 6.4436232263312464e-08,
          -6.4052031271178798e-07, 6.5148655417680864e-06, -7.2102170619005369e-05,
          0.00087305686194852957, -0.011365400834371762, 0.15651210272439994,
          6.3412689565903857e-18},
  };

  private static final double[][] DAW2_T = {
      {-9.1288110797806524e-16, 6.8274356780709853e-13, -1.1578174963930539e-11,
          1.4054275213587108e-10, -1.5554784237515909e-09, 1.7449895673082374e-08,
          -2.0954152864378418e-07, 2.7246104125535074e-06, -3.7936715838187097e-05,
          0.00055773663613827658, -0.0085710416342373759, 0.13678392160381908,
          -1.0098113619400473e-17},
      {-1.5991390759437935e-14, 2.0662789595884173e-13, -2.4692100254836153e-12,
          3.0627824430656838e-11, -4.0420289855367207e-10, 5.719455501236555e-09,
          -8.603426158411958e-08, 1.3601171911544182e-06, -2.2400856175952691e-05,
          0.00038209251721285571, -0.0067221372788207438, 0.12160712802218901,
          -4.8744490186199962e-18},
      {-2.721178727876911e-15, 3.7423078279810087e-14, -5.3752640180068553e-13,
          8.3435001380382218e-12, -1.3652243049343785e-10, 2.3329426822084382e-09,
          -4.1346534177740351e-08, 7.5629598622696323e-07, -1.4228354345172331e-05,
          0.00027459396968666365, -0.0054249928591356619, 0.1095313465268624,
          -5.354620606049404e-19},
      {-5.1956563103616306e-16, 8.8636655846386622e-15, -1.5648298122276071e-13,
          2.8977294541248525e-12, -5.5382983224008255e-11, 1.0883469882679509e-09,
          -2.1930532583935371e-08, 4.5216159669193689e-07, -9.5224954208253622e-06,
          0.00020454822358441995, -0.0044760692523418254, 0.099676821226511295,
          -9.5031372170526483e-19},
      {-1.3480809681566991e-16, 2.7037980453561946e-15, -5.5309638924024612e-14,
          1.1681028930400651e-12, -2.5220773291833574e-11, 5.5575924822611513e-10,
          -1.2481617634295404e-08, 2.8536399752531083e-07, -6.6348352328259642e-06,
          0.00015673923289974813, -0.003759244776905874, 0.091473291696250889,
          -2.907095504461035e-18},
      {-4.2516063767248282e-17, 9.638992956876233e-16, -2.2152076115957231e-14,
          5.21454147363234e-13, -1.2484465536458552e-11, 3.0370709007602301e-10,
          -7.5009612801835365e-09, 1.8794801440227065e-07, -4.7745579376737243e-06,
          0.00012289872785629276, -0.0032036762616157333, 0.084532879417924117,
          -5.608066081589422e-18},
      {-1.5263459403084034e-17, 3.8330728241747367e-16, -9.7257151656076366e-15,
          2.515294770130931e-13, -6.5941576032370486e-12, 1.7513255049128798e-10,
          -4.7095711096181288e-09, 1.2817119980724355e-07, -3.5285819500322655e-06,
          9.8227398157095801e-05, -0.0027639085797550029, 0.078581710442381561,
          -3.3223869958792725e-18},
      {-6.0387846494899732e-18, 1.6580287695077194e-16, -4.5899596652159398e-15,
          1.2907801429032263e-13, -3.6709626282297479e-12, 1.0553880041937156e-10,
          -3.0661288945516381e-09, 8.9984133329690488e-08, -2.6668629774410353e-06,
          7.9793260034119074e-05, -0.0024095863495991062, 0.073420484677026387,
          -4.6527519533134347e-19},
      {-2.5813077654876541e-18, 7.675648384689107e-17, -2.2979201563725451e-15,
          6.9711911875245886e-14, -2.1350518783357449e-12, 6.5995130667567478e-11,
          -2.0582764020722489e-09, 6.4755355003712668e-08, -2.0545956590407658e-06,
          6.5729485388225321e-05, -0.0021197627219944571, 0.068900498059605478,
          1.1284350962344978e-18},
      {-1.1760281252548414e-18, 3.7596113949586863e-17, -1.2087886431657715e-15,
          3.9308825114348495e-14, -1.2887801935532387e-12, 4.2591533946999687e-11,
          -1.4185319951485899e-09, 4.7604226203863327e-08, -1.6094066373297862e-06,
          5.4805897068018401e-05, -0.0018795806380847292, 0.064908427979877226,
          2.3653581523317188e-18},
      {-5.6541024911864977e-19, 1.9318653302853724e-17, -6.6331403197240696e-16,
          2.3001130555149905e-14, -8.0328518206379381e-13, 2.8249475777754235e-11,
          -1.0002466325502055e-09, 3.5653150568426496e-08, -1.2791567057300172e-06,
          4.6187897293820765e-05, -0.0016782524389338479, 0.061356333883851241,
          1.7945325734108425e-19},
      {-2.8469704080965663e-19, 1.0345761465629839e-17, -3.7756670146006251e-16,
          1.3899333923308201e-14, -5.1489020512710137e-13, 1.9191158924811587e-11,
          -7.1962134606382658e-10, 2.714405686876093e-08, -1.0298333640392458e-06,
          3.9294891377730132e-05, -0.0015077847629945333, 0.058174887494743116,
          -2.8790349341702713e-18},
  };

  private static final double[][] W_T = {
      {0.73474305228013492, 0.17971177639571495, -0.27751563794221001, 0.67115175275758765,
          -0.49757620460518553, -0.27812080509955933, 0.22158632521241103, -0.31815166688760094,
          0.18132085329009129, 0.15113107025917841, -0.091653508388055693, 0.094015474400113402,
          -0.044967869197683003, -0.050573853920730945, 0.025786289202083916,
          -0.020037155320145692, 0.0083780020167813999, 0.012284142612561598,
          -0.0055132786784753036, 0.0033048042584022091, -0.0012346962565124042,
          -0.0023464048015086648, 0.0009518820985865889, -0.0004380961807085349,
          0.00014786694778143721, 0.00036965972033985856, -0.00013791290853030515,
          4.7494540565878609e-05, -1.4502155072484468e-05, -4.9579304049821705e-05,
          1.7219149504796112e-05, -4.1965567713736091e-06, 1.1435285629302546e-06,
          5.7904569833082592e-06, -1.8891079293766602e-06, 2.897718099781153e-07,
          -6.6534292010173414e-08, -5.9895810593984708e-07, 1.8484231325202545e-07,
          -1.298933794638002e-08, 1.707637921057205e-09, 5.5599486211343571e-08},
      {0.49276021592125979, 0.089995268309958174, -0.11138720549569264, 0.3442412090586438,
          -0.20673250775335378, -0.092515166452849643, 0.062455971729595904, -0.11070869108694402,
          0.054043498252873398, 0.036675180213694362, -0.01938418445301739, 0.024402908937546153,
          -0.010298423478819811, -0.0094125897027726203, 0.004256956584469394,
          -0.0040931268293537548, 0.0015410848026717867, 0.0018107884929397533,
          -0.00072980809231833904, 0.0005521369120255503, -0.00018890601911460787,
          -0.0002802933303414773, 0.00010305719988016056, -6.1888011837131821e-05,
          1.9454285044453143e-05, 3.64120722317733e-05, -1.240180261037598e-05,
          5.8760430762844058e-06, -1.7066860120922635e-06, -4.0828187204374885e-06,
          1.3021813430761239e-06, -4.7670985161411146e-07, 1.2795103595158111e-07,
          4.0317002200424041e-07, -1.2138724536009868e-07, 3.2935772841101829e-08,
          -8.100266108970008e-09, -3.5595947910493539e-08, 1.0180563258365373e-08,
          -1.890693292815888e-09, 4.1371053247667496e-10, 2.8433198789923484e-09},
      {0.36123331484793897, 0.051429596928865548, -0.052042665101805607, 0.19958108151123241,
          -0.098746296683447046, -0.036271535929416641, 0.020926546240597713,
          -0.044720217783046287, 0.01880719394724488, 0.010646703787215534, -0.0049279859973558066,
          0.0074198197608745225, -0.0027668075822709247, -0.0021138920769131356,
          0.00085066365622346227, -0.00098081064680216666, 0.00033203208992808772,
          0.00032394129208358741, -0.00011749890302244442, 0.00010773004697136892,
          -3.3598961091653086e-05, -4.0800035009674822e-05, 1.3617927187866212e-05,
          -1.0096606700797065e-05, 2.9289534864483674e-06, 4.3836296166735535e-06,
          -1.3647120828671326e-06, 8.2146268285664882e-07, -2.2299244459439614e-07,
          -4.1187216911482863e-07, 1.2074933101629279e-07, -5.8633877977992861e-08,
          1.4939095545978985e-08, 3.4449246854870112e-08, -9.5791230981411635e-09,
          3.68796433315313e-09, -8.8159548388914254e-10, -2.5997037850535486e-09,
          6.8946234082068559e-10, -2.0379410863455943e-10, 4.5448726289077184e-11,
          1.7888243861863316e-10},
      {0.28151958517820319, 0.032399203283992505, -0.027362581095127847, 0.12686101732980509,
          -0.052672159577262352, -0.016229940699970039, 0.0080854831763305813, -0.0204181685964023,
          0.007459496869737842, 0.0035924436452460483, -0.0014654324058337828,
          0.0025863752652198258, -0.0008556606847149004, -0.00055817691711396156,
          0.00020072513487516438, -0.00027126423934007425, 8.2691745537744819e-05,
          6.8680997729360596e-05, -2.2490294496271007e-05, 2.4307430048151261e-05,
          -6.9062338658824732e-06, -7.0799679745848306e-06, 2.1503470922214861e-06,
          -1.9002779616746772e-06, 5.0719344331606942e-07, 6.3198834226931646e-07,
          -1.8017936216603072e-07, 1.3149113081603487e-07, -3.314844626378581e-08,
          -4.9935320168967349e-08, 1.3477288458171241e-08, -8.1330026416223834e-09,
          1.9432961908004791e-09, 3.5479145034466592e-09, -9.1226613233408316e-10,
          4.5238302139527994e-10, -1.0261770780835759e-10, -2.2939383635675929e-10,
          5.6471720595983558e-11, -2.2679323541206774e-11, 4.8810961462249848e-12,
          1.3623815619908975e-11},
      {0.22910177157197284, 0.021950829186570982, -0.015772154446417001, 0.086445780428349328,
          -0.030655726996582588, -0.0080749267892200605, 0.003511667279878341,
          -0.010301108659155664, 0.00330015784675638, 0.0013744762871413544,
          -0.00049765403819975536, 0.0010128537728673879, -0.00029894111608493935,
          -0.00016932271480291734, 5.4689488261264628e-05, -8.5115880850155756e-05,
          2.3439503026693179e-05, 1.6887584106902714e-05, -5.0116221727570695e-06,
          6.2566895585267624e-06, -1.6218091953637391e-06, -1.4351213215661996e-06,
          3.9782754510437373e-07, -4.0887973428487267e-07, 1.003954844911137e-07,
          1.070382131087628e-07, -2.8013913343451674e-08, 2.4035437061950177e-08,
          -5.6160389665518407e-09, -7.1451096216405824e-09, 1.778856858186443e-09,
          -1.281742930906452e-09, 2.859253946832141e-10, 4.3288967793096239e-10,
          -1.0309840370736248e-10, 6.2375102751350535e-11, -1.3311868062870531e-11,
          -2.4056893919692714e-11, 5.5050904635548958e-12, -2.779913276838807e-12,
          5.6807905740944903e-13, 1.2365418695903902e-12},
      {0.19243386944318505, 0.015717154144862272, -0.0097725869248500245, 0.062134308085563694,
          -0.019121375476672389, -0.0043761171229156296, 0.0016790724706667597,
          -0.0056376641626571453, 0.0015990154558492735, 0.00058404193462316357,
          -0.00018908440576615126, 0.00043774447016634114, -0.00011598235381676586,
          -5.7831978769277631e-05, 1.6869157887094069e-05, -2.9810000707975888e-05,
          7.4468905995146624e-06, 4.7235736891907287e-06, -1.2757895314884955e-06,
          1.8111462526695293e-06, -4.294582043602667e-07, -3.3358780815294975e-07,
          8.4688656392354616e-08, -9.9407134298283348e-08, 2.2486070156983137e-08,
          2.0924297774757566e-08, -5.0412853924641673e-09, 4.9752872189062051e-09,
          -1.0776727081250046e-09, -1.1863692500296651e-09, 2.7309175092184531e-10,
          -2.2867932787400344e-10, 4.7566452342629238e-11, 6.1567095870386412e-11,
          -1.3608688278110589e-11, 9.7035070546089918e-12, -1.9421817636585403e-12,
          -2.9521199854705044e-12, 6.2904250094540377e-13, -3.8152391665056619e-13,
          7.3573036763313239e-14, 1.317634087033286e-13},
      {0.16553021546200228, 0.011746387720597613, -0.006413587547116662, 0.046559572732198871,
          -0.012608207195576792, -0.0025421213755181791, 0.00086916325038451927,
          -0.0032982460017965008, 0.00083580843857101745, 0.00027095115610880835,
          -7.9009641069458601e-05, 0.00020565231496539685, -4.9228668222037677e-05,
          -2.1860967124805696e-05, 5.7910471340998595e-06, -1.1483971846449419e-05,
          2.6144994843880257e-06, 1.4777642251483769e-06, -3.6487517188103134e-07,
          5.8164610353360918e-07, -1.265861709867076e-07, -8.7466288483685482e-08,
          2.041023219195098e-08, -2.6977177401070565e-08, 5.6346310642067537e-09,
          4.6462213683520724e-09, -1.0336416170551439e-09, 1.1543186308170147e-09,
          -2.320978728268101e-10, -2.2506653866101552e-10, 4.8026644615139463e-11,
          -4.5831187928617046e-11, 8.8924813631274796e-12, 1.0054717580495816e-11,
          -2.0672862140364621e-12, 1.6961110709798567e-12, -3.1814431432598112e-13,
          -4.1778501695803082e-13, 8.3054840789932364e-14, -5.8704820545358977e-14,
          1.0658993735608135e-14, 1.6253298952709035e-14},
      {0.14503670020510698, 0.0090812457519903449, -0.0044090069626259061, 0.03606329268121506,
          -0.0086971009098940136, -0.0015632928124469626, 0.00048062276228219965,
          -0.0020388940440005162, 0.00046554627716076326, 0.00013534048244442156,
          -3.5793008962323831e-05, 0.00010370415361461955, -2.2569149621786312e-05,
          -9.0142457464540263e-06, 2.180535662447945e-06, -4.8046517418021115e-06,
          1.0016429186041017e-06, 5.0959998693119013e-07, -1.1554365357933503e-07,
          2.0468684451198491e-07, -4.1036267657864901e-08, -2.5496599427335998e-08,
          5.4891768438711381e-09, -8.0775801705740967e-09, 1.5621746345407091e-09,
          1.1552635509104592e-09, -2.3807418255262973e-10, 2.9701683127981893e-10,
          -5.5548995943318689e-11, -4.8105653451150349e-11, 9.5420308128860808e-12,
          -1.0224224417278167e-11, 1.8528308344130055e-12, 1.8598867508933856e-12,
          -3.5654861242813372e-13, 3.3072201182424113e-13, -5.816512632945198e-14,
          -6.7278884138216033e-14, 1.2504639841282803e-14, -1.0082428110972861e-14,
          1.7229860952983055e-15, 2.2907091761148733e-15},
      {0.507277922815775, 0.41747743914501639, -0.55217816465115432, 0.24852404697010044,
          -0.031013287584884159, -0.46582593320980314, 0.30598776469157779, 0.072399149888981967,
          -0.090188874230776858, 0.16751481481008612, -0.078586962126389451, -0.070195216975540931,
          0.043860097193894568, -0.03174055384894435, 0.010787643076780698, 0.023724459446793016,
          -0.01150492865994546, 0.0028125746236636029, -0.00032351176131237817,
          -0.0051017018332343003, 0.0020944274045242338, 0.00021891593831804338,
          -0.00021683269227296112, 0.00080252873242994715, -0.00028852845036867099,
          -0.00012776738576204393, 6.173648978599283e-05, -9.7626320079422538e-05,
          3.1117071858474392e-05, 2.6507571910730375e-05, -1.045965342695547e-05,
          9.3288497575674878e-06, -2.6175149186082396e-06, -3.8611619840208901e-06,
          1.3479410729301679e-06, -6.7983523998820715e-07, 1.5962225599039576e-07,
          4.4822812730883374e-07, -1.4269481401007962e-07, 3.1974587421998198e-08,
          -4.4607498627336488e-09, -4.3653536437281248e-08},
      {0.39583925365510192, 0.22803209243211883, -0.25171074183447462, 0.19257214796468147,
          -0.062627086305734839, -0.18367814702977397, 0.10728163086096351, -0.0052288153086999046,
          -0.010877874160756361, 0.053569267682788128, -0.023578509791322061,
          -0.010715891933129568, 0.0068416125181335764, -0.0092828221298164697,
          0.003281481087245579, 0.0035847997548262111, -0.0016535308793620255,
          0.0010332778745656566, -0.00028141656039995939, -0.00069324666693976319,
          0.00026893165989143455, -6.0456090812172959e-05, 6.2501358858643185e-06,
          9.761636184187582e-05, -3.3401165070737652e-05, -2.907297080605358e-06,
          2.5569638625746003e-06, -1.0828463881287471e-05, 3.3374420375487282e-06,
          1.3015602992342872e-06, -5.4451668884139077e-07, 9.7989495049336136e-07,
          -2.7426666350595803e-07, -2.0351174943415816e-07, 7.030386757591067e-08,
          -7.3124840092738133e-08, 1.8521681417163491e-08, 2.2847497646864307e-08,
          -7.0588953056457958e-09, 4.4313637678649781e-09, -9.9039871120304099e-10,
          -2.0876848993528692e-09},
      {0.31412713279378834, 0.13742025486286136, -0.12764006203352912, 0.13693095281674972,
          -0.047233395247704348, -0.080568391933512229, 0.041569745701611406, -0.01164194320465674,
          0.00075182848283743557, 0.018668833604995261, -0.0075190300229981627,
          -0.0013197870410546007, 0.0010792367443643119, -0.0027600685984855359,
          0.00093129049046228817, 0.0005830835884181036, -0.00026221253167209958,
          0.00028966069852352447, -8.2790048567512871e-05, -0.00010501409949347167,
          3.8607488746178936e-05, -2.1482512638805926e-05, 4.9056893107947751e-06,
          1.3248476916336808e-05, -4.2876932639756679e-06, 9.023405521766418e-07,
          -8.6460565014188033e-08, -1.317787038538416e-06, 3.864721270804698e-07,
          2.772506185641504e-08, -2.2498293730086071e-08, 1.0852041110606899e-07,
          -2.9243486612539878e-08, -1.01240628779199e-08, 3.7383329520107185e-09,
          -7.5732947861122619e-09, 1.8859020462101679e-09, 1.2367908641656331e-09,
          -3.7966009541697923e-10, 4.5140253476056072e-10, -1.0369038061967328e-10,
          -1.1007676459648295e-10},
      {0.25608983588608103, 0.089514774973819725, -0.070833041420752529, 0.097792579033499277,
          -0.031828041511892911, -0.038901386762627263, 0.017751097146716337,
          -0.0086116442104774964, 0.0017221706417600193, 0.0071478499568658951,
          -0.0026135950814084145, 9.4783247899216227e-05, 0.00013463211770663999,
          -0.00088171533344186053, 0.00027703404560147963, 9.4542013198731201e-05,
          -4.4239782202492537e-05, 8.1499810935055692e-05, -2.2495453291836176e-05,
          -1.7388278343480226e-05, 6.1363770140558543e-06, -5.8183117833464415e-06,
          1.40202271098873e-06, 2.0024277102531481e-06, -6.1394092572573208e-07,
          3.104918760710505e-07, -6.1262497472321929e-08, -1.7859999958252382e-07,
          4.9619685651508116e-08, -9.9046436867991757e-09, 8.9528090423897133e-10,
          1.32258709943312e-08, -3.3932340112009684e-09, -1.9768764267092786e-10,
          1.5337543876325075e-10, -8.3993479326191856e-10, 2.0092406043557476e-10,
          6.2136857753519772e-11, -2.0560945581186547e-11, 4.6496320440476096e-11,
          -1.0413479047885169e-11, -6.1027443316800388e-12},
      {0.21417726589651406, 0.06198167154884307, -0.042348376874977257, 0.071608963237934739,
          -0.021295815954927952, -0.020404546008595304, 0.0082733402145558563,
          -0.0055933118886002482, 0.0012529295223302505, 0.0029922572211474071,
          -0.00099218344348875115, 0.00021201101899865157, -1.058904898890704e-05,
          -0.00030628424584890182, 8.8853050591543512e-05, 1.186500731801876e-05,
          -7.3386181223020966e-06, 2.4366531632353713e-05, -6.3387535171157971e-06,
          -3.0284478371342953e-06, 1.0557351253173559e-06, -1.5666000681984899e-06,
          3.6765491266930512e-07, 3.323625193307476e-07, -9.7276773554192113e-08,
          8.1684104199082083e-08, -1.7062784316265595e-08, -2.6885054920481066e-08,
          7.0832126029012719e-09, -3.3041497567319572e-09, 5.7547172152569823e-10,
          1.7901251842002899e-09, -4.3587934119936159e-10, 8.3343061893614805e-11,
          -7.1812148665345599e-12, -1.0257693681434559e-10, 2.3385238279664993e-11,
          1.0830415740941269e-12, -8.3377582121077904e-13, 5.1734599478451319e-12,
          -1.1119621531105364e-12, -3.0871409372537229e-13},
      {0.1830694915391699, 0.045034638127075095, -0.026913727609841825, 0.053945006439465475,
          -0.014535428123258478, -0.011480642029609179, 0.0041623554139069621,
          -0.003574731718865186, 0.00079157466797449697, 0.0013576067152569606,
          -0.00040904717917247731, 0.00015187853819703946, -2.2374434517760151e-05,
          -0.00011554529206014256, 3.0879557684401534e-05, -1.054249779454897e-06,
          -9.2110515976049205e-07, 7.8542989406573775e-06, -1.9087570506163064e-06,
          -5.118744970437295e-07, 1.8900361617049312e-07, -4.4426223573594764e-07,
          9.9142398220832822e-08, 5.9147496250333493e-08, -1.6784133357950104e-08,
          2.1210003073484539e-08, -4.3425830385058099e-09, -4.4459433570898633e-09,
          1.1163894864046197e-09, -8.4763459996802359e-10, 1.5657276983870401e-10,
          2.6821309592709021e-10, -6.202913124801872e-11, 2.6987457620783745e-11,
          -4.2159309347687844e-12, -1.3867479789485475e-11, 3.0061677808900274e-12,
          -5.5478196756171992e-13, 4.5858388242793091e-14, 6.3332682817991469e-13,
          -1.2989127945773571e-13, -4.6323721240897054e-15},
      {0.15932906970441515, 0.034001299589766609, -0.0179851572231398, 0.04173826463216411,
          -0.01019084173252695, -0.0068532370886853463, 0.0022368453228717574,
          -0.0023187341232916762, 0.00048866091983759186, 0.00066127019091044559,
          -0.00018168515691640132, 9.3853396254667368e-05, -1.5791171440873991e-05,
          -4.7061159374380625e-05, 1.1594219294377073e-05, -2.0674912974404082e-06,
          9.4040063352464747e-08, 2.7326412851838608e-06, -6.1859225889863893e-07,
          -6.3915527187332857e-08, 3.2435733492536527e-08, -1.3485595967455693e-07,
          2.8360652697623002e-08, 1.0843884198637608e-08, -3.0772665616969263e-09,
          5.7584875430506605e-09, -1.1288643479131783e-09, -7.9409746621701414e-10,
          1.9187115106093062e-10, -2.1344361609572435e-10, 3.8835897640850174e-11,
          4.4079858312104376e-11, -9.7173168471536297e-12, 6.7708818786103742e-12,
          -1.1226522235295572e-12, -2.0678517609191602e-12, 4.2653197686816962e-13,
          -1.7459703682777147e-13, 2.4769775914544501e-14, 8.5533748812467104e-14,
          -1.6712462516355993e-14, 2.9944953496151508e-15},
      {0.1407456454948218, 0.026478730646883657, -0.012527988390605243, 0.033068729914023709,
          -0.0073419170242789381, -0.0043003216176317775, 0.0012721467284635215,
          -0.0015408665731692398, 0.00030477866427332375, 0.0003427106578852507,
          -8.6226303839529719e-05, 5.6365435492135083e-05, -9.5795177660566366e-06,
          -2.0545365035371854e-05, 4.6759495089793522e-06, -1.438062883683945e-06,
          1.6995495512683195e-07, 1.0222753848655605e-06, -2.1552956268413882e-07,
          7.5611696242566233e-09, 4.0093205954468986e-09, -4.3942080403646487e-08,
          8.6799583134326047e-09, 1.8837160810095219e-09, -5.7589233778927404e-10,
          1.6632416112528462e-09, -3.0936661813730396e-10, -1.4946938811375063e-10,
          3.5343870852241008e-11, -5.5859250307520466e-11, 9.7848701793228622e-12,
          7.8432413531316274e-12, -1.6587960515611939e-12, 1.6604445150263761e-12,
          -2.7224778932977139e-13, -3.3742229953493336e-13, 6.6405363366946917e-14,
          -4.2938731154281509e-14, 6.4656342080005817e-15, 1.2695393186799318e-14,
          -2.3646864572449913e-15, 9.1710579841798362e-16},
      {0.25431723957757096, 0.44229993637721055, -0.41464313075532216, -0.10452929363629927,
          0.23785435045750691, -0.20797753664300592, 0.043553872348458027, 0.20335841788378661,
          -0.12072854321106639, -0.028554476899420932, 0.040087274976207891, -0.054993274382697542,
          0.018957043631710715, 0.029091417044580296, -0.016145921501343204, 0.0039684977625841413,
          0.00055437067140358138, -0.0075038897181186675, 0.00301710794056868,
          0.0011717270482696218, -0.00080656476700940517, 0.0010569907845278941,
          -0.00031720987012271817, -0.00041660442494129786, 0.00018315433307124641,
          -7.6155464303432038e-05, 1.0650475031969564e-05, 7.1693872623504047e-05,
          -2.5506279815047478e-05, -2.3034993191343439e-06, 2.7541999876074932e-06,
          -8.3250571361098988e-06, 2.4977831933138299e-06, 1.5026588427962307e-06,
          -6.4714873747655566e-07, 6.8497503344542982e-07, -1.6862261256741976e-07,
          -2.4412116113709765e-07, 8.3883864515953254e-08, -3.5543992513916086e-08,
          5.4881783793999162e-09, 2.6758018565050448e-08},
      {0.2664464054943364, 0.28064386644653988, -0.24515021406603113, 0.027099892737658324,
          0.060316281641446291, -0.13065607181908942, 0.047841872099937476, 0.060655323870079159,
          -0.037313564431904381, 0.0094777564532686851, 0.0023633603119578078,
          -0.017806938445094688, 0.0070013867360453688, 0.0036694654563771069,
          -0.0023894270399233398, 0.0022768761635969369, -0.00057673645336087293,
          -0.0011808725952326933, 0.00049437625782220243, -8.1829573230092359e-05,
          -2.0521209767889876e-05, 0.00018247547368073139, -6.0339661882143914e-05,
          -2.3795247553618994e-05, 1.3016558575892588e-05, -1.7912777971183284e-05,
          4.7129815667524184e-06, 5.6036617054890249e-06, -2.1007198936023346e-06,
          1.0533520948939558e-06, -1.7294235047720443e-07, -7.1264158718729576e-07,
          2.2280208016354603e-07, -4.105418506491589e-09, -1.2781095718481977e-08,
          6.4784917670323621e-08, -1.7581891362522316e-08, -7.4766563102834938e-09,
          3.0685229146233949e-09, -4.4476503958502638e-09, 1.0410499922355375e-09,
          1.0734827119128779e-09},
      {0.24352998264694767, 0.18537629034687814, -0.14538423075017382, 0.056113484610948054,
          0.0083421615544546808, -0.073787857672845916, 0.02848113781069872, 0.01712909035802733,
          -0.011266110435146958, 0.0083875362309691788, -0.0015656317912214189,
          -0.0054123490411220427, 0.0021525712909237259, 0.00011164660315338274,
          -0.00028157830528328855, 0.00073773619243590208, -0.00021960704219368442,
          -0.0001704609905235374, 7.6224637749104971e-05, -5.558914478652773e-05,
          1.0967962804828709e-05, 2.893332486406317e-05, -9.7759882136476164e-06,
          1.0386427637114328e-06, 4.2505431947835038e-07, -3.0019405086071566e-06,
          8.4496071977549729e-07, 3.357638419537808e-07, -1.5165005953649943e-07,
          2.1800497234936557e-07, -5.1052257322422144e-08, -5.5827664395981796e-08,
          1.8210100086818734e-08, -1.0550633775170082e-08, 1.7766282346983671e-09,
          5.4415684125201153e-09, -1.514324984949061e-09, 1.6976532957188655e-10,
          3.4577332468717606e-11, -3.958809308735418e-10, 9.7625215577123668e-11,
          2.8186416843414369e-11},
      {0.21496521048990289, 0.12804620642654696, -0.089251303731842854, 0.055885414314485071,
          -0.0056016057747504504, -0.041713192788928301, 0.015503482379770925,
          0.0040392578516590654, -0.0033545229797799199, 0.0047665131548776758,
          -0.0011875722536040364, -0.0016507936322565204, 0.00065003314677868507,
          -0.00020825655691665394, 3.0233844361530788e-06, 0.00022101609186846822,
          -6.6758554138514263e-05, -1.8326120170549808e-05, 1.0745355097450575e-05,
          -1.8062438202862445e-05, 4.343518682338353e-06, 4.4199593007178716e-06,
          -1.5345135771122254e-06, 8.9751479688599792e-07, -1.4245430273292908e-07,
          -4.7607567281316246e-07, 1.3529985047774653e-07, -8.1731040133721537e-09,
          -5.8533489125221934e-09, 3.5645330641974462e-08, -8.7471780951517979e-09,
          -3.4853598256242778e-09, 1.3359927295774319e-09, -1.9976336491785581e-09,
          4.2133274083735663e-10, 4.2875466003964353e-10, -1.2359311117275016e-10,
          8.0484225295958857e-11, -1.3262521852999045e-11, -3.2954947063924136e-11,
          8.2500106127131733e-12, -1.6081128223305362e-12},
      {0.18876108220848337, 0.092174889020024811, -0.057115704931096765, 0.048517074607275452,
          -0.0082030331782426344, -0.024310896184151408, 0.0084466533263729254,
          0.00021891351597315702, -0.00093136403439195896, 0.0025161421524229404,
          -0.00064845137617254383, -0.00050740885164797013, 0.00020008611279990202,
          -0.00014095516382491813, 2.3198461876171584e-05, 6.6688300751239487e-05,
          -1.9758878363706916e-05, 1.3495621661206767e-06, 1.0081446560519074e-06,
          -5.3150615867888371e-06, 1.3079617946734296e-06, 6.0518786824971567e-07,
          -2.3298621742396986e-07, 2.9375685699299566e-07, -5.9296015776537841e-08,
          -7.4694158381171336e-08, 2.1391443198001027e-08, -1.0303557464510978e-08,
          1.3391010691266936e-09, 5.6346940023296778e-09, -1.3849677372223493e-09,
          3.2961674141842668e-11, 5.8284046152548611e-11, -3.1996483578208689e-10,
          6.9669623414349166e-11, 2.7747678440297834e-11, -9.2154221033127714e-12,
          1.4280398338825443e-11, -2.7388894234579951e-12, -2.6172080664606216e-12,
          6.7503157330988664e-13, -4.846387052969178e-13},
      {0.1665356403708963, 0.068777951647778154, -0.03806036686446089, 0.040488265936137476,
          -0.0076174504659421299, -0.014722275190682553, 0.0047306154483410211,
          -0.00075828877762895285, -0.00019055649148188379, 0.0013304718398904679,
          -0.00033344890971595201, -0.0001523082682635806, 6.2839963633992371e-05,
          -7.4367334280708055e-05, 1.4396795971861252e-05, 2.0702153177424764e-05,
          -5.9762593402252067e-06, 2.2246134715771641e-06, -1.7972994438723148e-07,
          -1.5662681847282003e-06, 3.7873685254133911e-07, 4.549582127959453e-08,
          -3.0650475049192261e-08, 8.5067647934549908e-08, -1.7747954484972754e-08,
          -1.1256929135417199e-08, 3.3659789281555241e-09, -3.4137864125543602e-09,
          5.9322402717912242e-10, 8.9538858552606552e-10, -2.1935871359103291e-10,
          8.8424614120558877e-11, -9.4822432948492999e-12, -5.0335361350177951e-11,
          1.0916385176071197e-11, 6.7147838588133223e-14, -4.4206462434692559e-13,
          2.2479297019718882e-12, -4.4021028581208915e-13, -1.7488234201041559e-13,
          5.1140104108339421e-14, -8.1874848847562349e-14},
      {0.14809309301799634, 0.05291080211538169, -0.026312518795009822, 0.033497057190082252,
          -0.0063370086564667255, -0.0092664375192025878, 0.0027452384521232122,
          -0.00087915477170810805, 2.4103791630679174e-05, 0.00072167800721864106,
          -0.00017196586728039125, -4.0512024045960211e-05, 1.9729821440146393e-05,
          -3.7382969499973075e-05, 7.3739827443702137e-06, 6.605376068700223e-06,
          -1.869956911833359e-06, 1.2902013737236509e-06, -1.8741825332814433e-07,
          -4.7572618275474882e-07, 1.1162392690812113e-07, -1.7286864392749148e-08,
          -1.5079935606257828e-09, 2.4464909235325749e-08, -5.0379966570883695e-09,
          -1.3988821798957072e-09, 5.0140343035772255e-10, -9.7581805767983251e-10,
          1.7711909738310867e-10, 1.4129908619041426e-10, -3.5144036262368272e-11,
          2.980761779289488e-11, -4.5392867785299948e-12, -8.0425613223544954e-12,
          1.72703769857396e-12, -5.8843930702697399e-13, 5.2006878608319969e-14,
          3.5169310397476027e-13, -6.8320390412251488e-14, -2.1262556230017123e-15,
          2.6483278632238818e-15, -1.269940156061908e-14},
      {0.13282143158082796, 0.041768259424046814, -0.018791633271718788, 0.027797781679185885,
          -0.0050902086942323987, -0.006046861754083711, 0.0016524417077969137,
          -0.00076728125547316733, 7.3675925730939552e-05, 0.00040465345959337187,
          -9.0834456594177511e-05, -5.9281162038283259e-06, 5.8789030824753707e-06,
          -1.8871367370140259e-05, 3.6337714580163793e-06, 2.1346968163488594e-06,
          -6.0400108592190566e-07, 6.4408834553569061e-07, -1.029864010789238e-07,
          -1.4995626134696136e-07, 3.4079621443891063e-08, -1.4088802961204936e-08,
          1.373429667189357e-09, 7.2306699334320085e-09, -1.4468993795846213e-09,
          -1.664928459086073e-11, 5.7347652319337195e-11, -2.7445933142324667e-10,
          4.9427474478324957e-11, 2.0667107524629227e-11, -5.5507122933178383e-12,
          8.4363223631655378e-12, -1.3566094062258618e-12, -1.2996674223305318e-12,
          2.7914367263066726e-13, -2.0287680140063429e-13, 2.7432423353905458e-14,
          5.6274961301813613e-14, -1.0779273362236633e-14, 3.1222644417075584e-15,
          -2.2498399947063221e-16, -1.9755516745560685e-15},
      {0.10578791337150541, 0.36041686533239126, -0.19004926413407328, -0.18597381825360956,
          0.18030484429972041, 0.012549632644943801, -0.081564536819467681, 0.079290500033351877,
          -0.0088721399286570048, -0.065458436749221333, 0.032290469002924843,
          0.014991919703979882, -0.0146294003001555, 0.010383320005508442, -0.0011694824217930307,
          -0.0084301084681659387, 0.0036421168553129545, 0.0011654351048075516,
          -0.0010917473997339448, 0.0012177928490943297, -0.00028542213870099374,
          -0.00060472714815782845, 0.00026182806462204125, -1.6029964571432992e-05,
          -2.9464077588406113e-05, 9.4553761667054325e-05, -2.8711921296241072e-05,
          -2.1857399838278546e-05, 1.0606512842465478e-05, -7.0179045180009426e-06,
          1.1194730256568331e-06, 4.1982806045551764e-06, -1.4395025607782678e-06,
          -7.6119349548103086e-08, 1.6242666097860116e-07, -4.3590542384134465e-07,
          1.1625328312281996e-07, 8.8705241780645099e-08, -3.6178273262986822e-08,
          2.6485045257316916e-08, -4.632004359826379e-09, -1.26009502665203e-08},
      {0.16276175620775082, 0.26921811711577492, -0.16583897105346548, -0.058026877121325884,
          0.083936285294819377, -0.043291853863355496, -0.0090122790733233628,
          0.047223604940722315, -0.01637354660548088, -0.016295122738958015, 0.010177857431478556,
          -0.0025707920773740509, -0.0011219326525457106, 0.0043868719335845626,
          -0.0014069489540957545, -0.0012185098048541859, 0.00066755274214316037,
          -0.00029981701487948032, 3.0819764856919124e-06, 0.00027611667206353473,
          -9.3171739389094038e-05, -3.7139728719194868e-05, 2.4020685619241181e-05,
          -2.5680607684204211e-05, 5.3125139653781351e-06, 1.0677546325353559e-05,
          -3.8937577407135002e-06, 4.6315632935413579e-07, 2.641327611265845e-07,
          -1.2239645137411674e-06, 3.3514026979148018e-07, 1.9742426651306251e-07,
          -8.7820004172109739e-08, 7.8389605624962229e-08, -1.4430948031948994e-08,
          -3.1616596850313428e-08, 9.9290795100317136e-09, -1.3597055681057772e-09,
          -4.1733897754587743e-10, 2.7946602065236579e-09, -7.1027411444336845e-10,
          -3.2179455601512159e-10},
      {0.17576122325714599, 0.19748905132410335, -0.1214416530897526, -0.0022355706817141752,
          0.033967206297778343, -0.041774736268912788, 0.0065204144883330107, 0.021921567520059086,
          -0.0089879861261436284, -0.0023692625008034365, 0.0024987932425656174,
          -0.0026161501943894145, 0.00044830340288900996, 0.0012746779292592968,
          -0.00048285051029921814, -4.9975838693033529e-05, 8.3553797942082476e-05,
          -0.00014591426841811637, 3.4275228528428528e-05, 4.4640791332696466e-05,
          -1.7546891740192363e-05, 4.98976958507238e-06, 4.8528072695428786e-07,
          -5.7162315329695775e-06, 1.5920601753017312e-06, 7.3450578148858684e-07,
          -3.6203904721101286e-07, 3.7550326095959798e-07, -6.9873252354708726e-08,
          -1.3415538273630246e-07, 4.2216401388183346e-08, -7.1186367636910368e-09,
          -1.6129682536532402e-09, 1.1730311917191574e-08, -2.9095078879765534e-09,
          -1.3403763263914902e-09, 5.5879296106920512e-10, -6.3864094289286373e-10,
          1.1929673973572261e-10, 1.8521123948605215e-10, -5.3604820624915448e-11,
          1.6540034912261921e-11},
      {0.17064026723171211, 0.14631021284351733, -0.085155190358681701, 0.019052486832209479,
          0.011723167852347447, -0.030630481672190953, 0.0073575357938263338, 0.00935687490167777,
          -0.0041121622068037182, 0.00069013147752938547, 0.00041859126150263894,
          -0.0013473284501790749, 0.00034060090378690641, 0.00031171953421812575,
          -0.00013403818807085859, 5.8790766762933858e-05, -7.8755820694240483e-07,
          -4.5009136732314377e-05, 1.2588983478101697e-05, 4.7452109735034448e-06,
          -2.5878087352209075e-06, 2.9348592884010749e-06, -5.3169353395695058e-07,
          -9.7319080755795252e-07, 3.025314177365257e-07, -5.0218615124999095e-08,
          -1.3172157469341193e-08, 8.1791292767350912e-08, -1.9477911403187788e-08,
          -9.9806959495025568e-09, 3.9723046017720461e-09, -4.0318306533523735e-09,
          6.8383433834000671e-10, 1.2606083174710163e-09, -3.4858236921091525e-10,
          7.4006588903892147e-11, 6.1884820401006896e-12, -8.6677855770413987e-11,
          1.9585924004475218e-11, 7.0368226446480124e-12, -2.8149409419798299e-12,
          4.008804913444833e-12},
      {0.15913306227764507, 0.11052278300898256, -0.059613194431336121, 0.025450646314670845,
          0.0024539821852025598, -0.020931726589150278, 0.0054815605210956735,
          0.0037722768664242563, -0.0017795450738327058, 0.0009983654502212834,
          -4.8413751556220224e-05, -0.0006061759952751657, 0.00016679104989565611,
          5.7124494170584028e-05, -3.2840135393532551e-05, 3.7408076631833534e-05,
          -6.2881601338371772e-06, -1.2174580910211118e-05, 3.655913017727244e-06,
          -4.3426660840676186e-07, -2.1735750322014273e-07, 9.4174863700733028e-07,
          -2.1029144615009108e-07, -1.3177074983831728e-07, 4.8147224474431372e-08,
          -3.9665678492095023e-08, 5.6593887250188273e-09, 1.428529725107892e-08,
          -3.7013194183266791e-09, 2.7611195305922171e-10, 1.9188962019148134e-10,
          -8.5873659702633732e-10, 1.7916940496029096e-10, 9.9365680788254452e-11,
          -3.3160387893931525e-11, 3.3142999351322044e-11, -5.1741086228262421e-12,
          -9.1950063213024557e-12, 2.2659277643102029e-12, -5.6947304080881114e-13,
          -7.2579306536438123e-15, 5.0932466730199183e-13},
      {0.14617116219868043, 0.085324130037192997, -0.042316352490820046, 0.025803319872594762,
          -0.0011584156901097472, -0.014109970464478698, 0.0036941074474637885,
          0.0013831810586965095, -0.00075126221576824056, 0.00076530406561719447,
          -0.00010992495576883307, -0.00026259683206557527, 7.3829866727788824e-05,
          -1.1553270460489141e-06, -6.4155601089805369e-06, 1.7596148827069032e-05,
          -3.5533068156582613e-06, -2.9987857754063536e-06, 9.7493025644778252e-07,
          -5.7259555045504302e-07, 5.4508220624654729e-08, 2.639539566942554e-07,
          -6.2626593023950176e-08, -7.131178268673537e-09, 5.9129294880676405e-09,
          -1.3208543984702292e-08, 2.4546877633693075e-09, 2.1516267922640826e-09,
          -6.1309419931967155e-10, 3.8468653556779318e-10, -4.3184658899793455e-11,
          -1.5184255751714987e-10, 3.38875399027685e-11, -2.5530992293615298e-14,
          -1.9045583716304543e-12, 6.9054708260059192e-12, -1.284946442322103e-12,
          -7.5794527013699924e-13, 2.1777527924393544e-13, -2.1531198809267231e-13,
          3.1173173639036717e-14, 5.3585923137835341e-14},
      {0.1336922271407566, 0.067276137459769722, -0.030627901504144902, 0.023913209571400709,
          -0.0023754684014507042, -0.0095835743212500275, 0.0024255697750806987,
          0.0003855451970010833, -0.00030812840734349879, 0.00051288422874293054,
          -8.7788527526020473e-05, -0.00011267010937393629, 3.1860158349580555e-05,
          -1.0132940959658511e-05, -2.5680220132447261e-07, 7.6734975477719689e-06,
          -1.6179718667509564e-06, -6.152681486594749e-07, 2.4191810777674504e-07,
          -2.9741549348383997e-07, 4.5602964863834537e-08, 6.9902282396354672e-08,
          -1.7189250636418694e-08, 4.8867024514103699e-09, 6.0001119496980238e-11,
          -3.7648245196603449e-09, 7.4593369052351664e-10, 2.318056645584688e-10,
          -8.7430952585444403e-11, 1.3355430178308502e-10, -2.1183739027195018e-11,
          -2.4183346236823102e-11, 5.7383275766700745e-12, -2.7982867537825975e-12,
          2.4085103743869945e-13, 1.2271509919723417e-12, -2.4128624091974315e-13,
          -1.4665928204974872e-14, 1.4077433421545549e-14, -4.3926877328891626e-14,
          7.3888381113140671e-15, 4.5786304910512178e-15},
      {0.12236150187325839, 0.054084354280804325, -0.022632599450371927, 0.02137266306325946,
          -0.0026069663478845446, -0.006614266702613651, 0.0015941936162457938,
          -1.4381686079019312e-05, -0.00011840190167095849, 0.00033060429616510396,
          -5.8889671080990607e-05, -4.8067480377527333e-05, 1.3735258215654855e-05,
          -8.5499796502387879e-06, 7.9729871862833241e-07, 3.2922075590684301e-06,
          -6.9618815668695581e-07, -5.0313443246803041e-08, 5.1634476310739732e-08,
          -1.3187854351343121e-07, 2.2256656993558836e-08, 1.7494321646006738e-08,
          -4.5418036139895313e-09, 3.2365485285588713e-09, -3.6190728116356506e-10,
          -1.0210863364206702e-09, 2.070873222228087e-10, -1.4230020839896811e-11,
          -7.6940158394233161e-12, 3.8487344936422446e-11, -6.5727001322978277e-12,
          -3.2360364534673384e-12, 8.8263804630524951e-13, -1.0220819558422189e-12,
          1.4062073139474359e-13, 2.0173967088782769e-13, -4.1355617824077442e-14,
          1.5745532117581323e-14, -9.687005012292747e-16, -7.8116616056110092e-15,
          1.3757112680187388e-15, 1.5575025718477248e-16},
      {0.045872706502587117, 0.27695875393108987, -0.067947802296097107, -0.14087157884568532,
          0.071791953952210025, 0.056989249045726401, -0.052891187889962582,
          -0.0035348133301677238, 0.02316475773383192, -0.017906561040179188,
          -0.0014824629084816154, 0.015213354494845167, -0.0053419591886789981,
          -0.0053176236153673444, 0.0034778471940478642, -0.00054663187517111213,
          -0.00065496374168036865, 0.0014195208839975952, -0.00036651078983725242,
          -0.00055189981742297448, 0.00026832761289168858, -1.7223719467317924e-05,
          -4.3914958006522981e-05, 9.5194778727548655e-05, -2.4286710449187453e-05,
          -3.0997632194672637e-05, 1.3970868995157937e-05, -2.981296719575133e-06,
          -1.1275812814016698e-06, 4.8875475092753142e-06, -1.3615898979580469e-06,
          -1.0311719807925208e-06, 4.9167069457614311e-07, -2.7837663475032884e-07,
          2.1851373702840313e-08, 1.8054138062784999e-07, -5.5077882250063483e-08,
          -1.4811590565338195e-08, 1.0354838286154522e-08, -1.4046929609823493e-08,
          2.8267763703759931e-09, 4.3828472615902425e-09},
      {0.098968531750880384, 0.23424423046654283, -0.093992047179147484, -0.074172667630250683,
          0.056884073679513439, 0.0031383070858818194, -0.021095592190230925, 0.016298947618254341,
          0.0014026097310984511, -0.011994622542140444, 0.0035775013554616313,
          0.0038547983212951289, -0.002186963013305258, 0.00021273343421006037,
          0.00042934728575221286, -0.00076949322522537741, 0.00016095292536093654,
          0.00029915396455821984, -0.00012602797653236782, -5.4039753447376748e-06,
          2.3711408065667561e-05, -3.8494807526656838e-05, 7.9647641344769432e-06,
          1.3497042950993615e-05, -5.2515575258325781e-06, 3.5881463101058234e-07,
          6.3390019567758395e-07, -1.5947242655989217e-06, 3.7560555519411759e-07,
          3.9341283140411484e-07, -1.5786040950816832e-07, 5.7045497139210039e-08,
          2.7955611317085813e-09, -5.0421236604526396e-08, 1.3382878765697554e-08,
          6.3888958084932846e-09, -3.1239296109064573e-09, 2.8898957734603712e-09,
          -4.4069634853292075e-10, -1.1103383253262396e-09, 3.282742651110849e-10,
          -6.1112280076641273e-12},
      {0.12386719749337795, 0.18845699017260495, -0.086259913288688345, -0.029345282414654603,
          0.033536004387852592, -0.014605213128771662, -0.0049684086632963706,
          0.013524670979716737, -0.0027256230853949163, -0.0048073928732352668,
          0.0020367278055563398, 0.00027959673672250333, -0.0005025061854012395,
          0.00054413015288807003, -6.4550342083846612e-05, -0.00025021624256260329,
          8.3743537971660086e-05, 2.48860801206489e-05, -2.0614448489239432e-05,
          1.9898475517015143e-05, -2.4975868949204861e-06, -8.7779178844767359e-06,
          2.7748402994935651e-06, 5.4069515303783999e-07, -5.1165580627378921e-07,
          6.8213390262910656e-07, -1.1860728570633171e-07, -2.2091179632478357e-07,
          7.1768921958150862e-08, -5.2604648236326962e-09, -6.5931159638731486e-09,
          1.9071558630702475e-08, -4.1368703438822732e-09, -3.6761433925758045e-09,
          1.3301052937515891e-09, -6.6225271382989094e-10, 3.5146393405981577e-11,
          3.892867090559525e-10, -9.711329395737969e-11, -2.7113197189839919e-11,
          1.4946702151082284e-11, -2.0689039793208808e-11},
      {0.13167352034033916, 0.14935464016709274, -0.06978960094670171, -0.0045740348475917817,
          0.017348520806454076, -0.016931260103283212, 0.00075048263428961705,
          0.008206305779122916, -0.0023380358100703064, -0.0014231362548750893,
          0.00080784379693486644, -0.00036507461521237145, -3.9497769884931046e-05,
          0.00027694249815563623, -6.6949840834688035e-05, -5.3978545239765938e-05,
          2.3918114398347187e-05, -9.5821374763647063e-06, -8.0770158782919226e-07,
          7.4848120809953227e-06, -1.8004729367979375e-06, -1.1690423854347368e-06,
          5.1144391292977497e-07, -3.0975255897650441e-07, 1.794285908284351e-08,
          1.6182646591746091e-07, -4.132600469394097e-08, -1.3193383497347501e-08,
          7.4217471940236506e-09, -8.5458349762903331e-09, 1.2895816398506235e-09,
          2.5911272805945814e-09, -7.2360414283087944e-10, 5.7378840651741714e-11,
          5.1640076783459213e-11, -1.7104969671846605e-10, 3.4230777867864512e-11,
          2.6345871399305917e-11, -8.6899002144598959e-12, 5.4597499790805078e-12,
          -5.1239399219388594e-13, -2.342298347693224e-12},
      {0.13051475299777376, 0.11853130123119379, -0.053925532949609833, 0.0076719230651586239,
          0.0080795230354552775, -0.014460678991178567, 0.002140052259789122,
          0.0044571185568125166, -0.0014330619335763198, -0.00019147767308756042,
          0.00026140493052423456, -0.00032076177672751469, 4.1062280753294617e-05,
          0.00010834352568164692, -3.1434894124411965e-05, -4.4003679288867875e-06,
          4.9413507966592572e-06, -6.9285465154311831e-06, 1.0505833716018831e-06,
          1.971457399138582e-06, -5.738768469403369e-07, 2.5790956253027146e-08,
          5.4303488287851974e-08, -1.3422984456220644e-07, 2.4946141337950908e-08,
          2.5673890894045063e-08, -8.1024695056369425e-09, 3.1284264677254613e-09,
          4.6196371730642875e-11, -2.0688991512491758e-09, 4.4580061052431332e-10,
          1.8968730482549843e-10, -7.780641369412205e-11, 7.9881417714012427e-11,
          -1.0706234100707146e-11, -2.286541927885315e-11, 5.6053607825881715e-12,
          -4.8279973444464107e-13, -3.1496074343333505e-13, 1.1936480968979164e-12,
          -2.2109908918428557e-13, -1.4942468108506669e-13},
      {0.1251358479368391, 0.094910927179719107, -0.04110121622732095, 0.013032831134161506,
          0.0031821741935771633, -0.011206452606449887, 0.0020823864160234278,
          0.002287138798675693, -0.00078895596663586026, 0.00016691383268257516,
          6.0711019513735688e-05, -0.00019722640558514855, 3.6661185790265463e-05,
          3.6630092073745323e-05, -1.2133124096866669e-05, 3.9972678559975945e-06,
          4.0770750791948296e-07, -3.0644633708391424e-06, 6.1966842983115373e-07,
          3.9479535146931045e-07, -1.4325485169979502e-07, 9.44171295995042e-08,
          -6.8541649287233415e-09, -3.8778736526137924e-08, 8.6725328904238733e-09,
          1.9473301897158874e-09, -1.1236732389248743e-09, 1.6227351000940811e-09,
          -2.4024951108345472e-10, -3.5834039398345223e-10, 9.0506474054420509e-11,
          -2.0771074420241742e-11, -2.5638137743320269e-12, 1.9522813472417465e-11,
          -3.6529595309441667e-12, -1.8946785897981745e-12, 6.1917851077904704e-13,
          -5.7934977058612388e-13, 7.0168001324154803e-14, 1.5741838620784985e-13,
          -3.4415595168680832e-14, 3.2196397977036003e-15},
      {0.11806609409050388, 0.076913474845644023, -0.031359836910581321, 0.014838918701839291,
          0.00072002473928180146, -0.0083815719653931222, 0.0016664482397797495,
          0.0011196918784195853, -0.00041526733696129272, 0.00022315422983243473,
          -2.7381938645711777e-06, -0.00010886802016734487, 2.253573587090234e-05,
          1.0232648534649184e-05, -4.2031726020997807e-06, 3.6009769668433162e-06,
          -3.4385559348426341e-07, -1.1686339383055896e-06, 2.6195275287693503e-07,
          3.2440016259581389e-08, -2.8021609529040175e-08, 4.8859490974298528e-08,
          -7.2928701400226206e-09, -9.327934542249545e-09, 2.3524633469633416e-09,
          -6.9496838319392203e-10, -3.9818405542329977e-11, 4.9940115797000143e-10,
          -9.1402595870084915e-11, -4.275348631800126e-11, 1.4203388762202258e-11,
          -1.4152983623563018e-11, 1.6809717973187535e-12, 3.5545857492325893e-12,
          -7.568437201368868e-13, 8.1412629000440428e-14, 3.1835290804519254e-14,
          -1.4200245267096648e-13, 2.3548036278534468e-14, 1.417138888363648e-14,
          -3.8761358559403252e-15, 3.3585709777547361e-15},
      {0.11058371258517936, 0.06313437377701249, -0.024118903305713438, 0.01489664071011108,
          -0.00045377748440758494, -0.0062059279783370416, 0.0012451151512443978,
          0.00051224187178380419, -0.00021341229335152239, 0.00019210097482849498,
          -1.7823534238646569e-05, -5.7669186031883562e-05, 1.24185992563046e-05,
          1.4976490460558956e-06, -1.2863229043797636e-06, 2.208493847747299e-06,
          -3.1063019809944019e-07, -4.1076232801580128e-07, 9.8862693343166493e-08,
          -2.6536748186410101e-08, -2.2647335243444575e-09, 1.9946982279669935e-08,
          -3.4483380662962206e-09, -1.7911293139191878e-09, 5.5112654105233331e-10,
          -4.9761226245682231e-10, 5.2654979186982593e-11, 1.2985113469242895e-10,
          -2.6094069875205084e-11, 1.1415767782389267e-12, 1.3783454600833097e-12,
          -4.6089227215263278e-12, 7.1366654803673847e-13, 5.0746298373536244e-13,
          -1.271892946068661e-13, 9.3044876821646273e-14, -8.7302607877906844e-15,
          -2.6650455756479855e-14, 4.9361233992205987e-15, -3.6091938458055959e-17,
          -2.511361629673372e-16, 8.221199870933236e-16},
      {0.024577530293193938, 0.21960181300393977, -0.025375510110596779, -0.091719569572753207,
          0.0222752301177589, 0.038970880848780712, -0.01742576833403001, -0.014012773527222324,
          0.011071219709509023, 0.00196034421729409, -0.0050119999251185135, 0.0023416088009145281,
          0.0010990607615985065, -0.0023822561461764712, 0.0003982910840510007,
          0.0011242372601917548, -0.00047832548192270491, -0.00020224227259090107,
          0.00019256520513082502, -9.9664364463577902e-05, -1.522898466059167e-05,
          8.5635594716606815e-05, -2.3504836115462948e-05, -2.4004777243989621e-05,
          1.2311014944519563e-05, -2.2910413777949364e-06, -1.6804946656022435e-06,
          4.1888288148916624e-06, -9.4892105862721114e-07, -1.2583020281080819e-06,
          5.300602759733405e-07, -6.5502396391684532e-08, -6.5640037424674539e-08,
          1.6323981864898492e-07, -3.6322378634498736e-08, -4.3176128886688863e-08,
          1.7192505160874871e-08, -3.9360966168850946e-09, -1.256951022329309e-09,
          5.2318177150425424e-09, -1.2427935420708637e-09, -1.013716434389957e-09},
      {0.063725628993736738, 0.19934907963919007, -0.051467340006766976, -0.063629214410637958,
          0.030087645216893978, 0.014231764995139522, -0.013733240395557882, 0.0012840845075558533,
          0.0043209146257785439, -0.0037315335471248532, -0.00037916999427070158,
          0.0022947787110814341, -0.00051903770274101314, -0.00076491013754868798,
          0.00035224030675627764, 5.6572126923776556e-05, -0.00010179851141147949,
          8.6289139610273564e-05, -1.6838990371119742e-06, -4.8337417176315385e-05,
          1.4035234176260176e-05, 9.5803363804855454e-06, -5.4050441204071542e-06,
          2.0845575450518189e-06, 3.9867588560806052e-07, -1.8765144231787803e-06,
          4.463537873232158e-07, 4.2720772376673562e-07, -1.8653471541740745e-07,
          5.2418263189692209e-08, 1.4124050328923185e-08, -5.7527588130044484e-08,
          1.3068485739416919e-08, 1.1898695802654716e-08, -4.8398075600386677e-09,
          1.7652718080213323e-09, 1.738821007450463e-10, -1.4581486227427088e-09,
          3.4400213851923774e-10, 2.2255003462761388e-10, -9.5297545570223968e-11,
          5.8813442362734219e-11},
      {0.088385416846661999, 0.17153858002529593, -0.057273342593401132, -0.036046565160270091,
          0.024058068834853501, -0.00081884759280178221, -0.0066066041289654836,
          0.005483873331272076, 0.00048246709194583635, -0.0030017744534947865,
          0.00061104062369838014, 0.00086716902036251635, -0.00035962251055441204,
          -4.8913710708369649e-05, 9.0508326268797429e-05, -8.0893765063281376e-05,
          2.401851746529357e-06, 3.9559039199099159e-05, -1.0592137682862047e-05,
          -6.8657572038673357e-06, 3.62886607530142e-06, -1.4876069569772852e-06,
          -2.2668231280699374e-07, 1.1673806802596538e-06, -2.5771064412627006e-07,
          -2.3988950378800339e-07, 9.7773031449112636e-08, -2.8545575336124134e-08,
          -6.6924516469921253e-09, 2.8024792378707704e-08, -5.9117065261999551e-09,
          -5.35427188621092e-09, 2.032100592034794e-09, -7.3886394173471639e-10,
          -7.0605885772249873e-11, 5.7012023363975267e-10, -1.2503156823460179e-10,
          -8.2301038173254551e-11, 3.2796463231667013e-11, -1.9737149302110564e-11,
          1.0169857719879305e-12, 9.5582619714474835e-12},
      {0.10108772842399091, 0.14376412279447512, -0.052808076551287109, -0.016130557758068802,
          0.015906006015428222, -0.0069909549950334781, -0.0021117408216327297,
          0.0050134489782743185, -0.00066259152197907999, -0.0015502416286818101,
          0.00048837786275281268, 0.00016370026562561977, -0.00013132404524877347,
          8.1801546131311152e-05, 4.547419260316808e-06, -4.5382125228963389e-05,
          9.8499807830540797e-06, 8.760328635695938e-06, -3.623175844721697e-06,
          9.0083446898998131e-07, 3.3804262213261069e-07, -9.7941313943108345e-07,
          1.7810647997320389e-07, 2.1835946831153269e-07, -7.428439541895652e-08,
          1.1206376936876978e-08, 7.0441180105325036e-09, -1.8335278908426224e-08,
          3.260904685749464e-09, 3.8412047918376086e-09, -1.2385996680836953e-09,
          2.7538433744149855e-10, 7.8395874000415081e-11, -3.0387028758190778e-10,
          5.7792707213437091e-11, 4.9772491048002528e-11, -1.6691551055929183e-11,
          7.3176332862651046e-12, 9.6306993824431818e-14, -4.2827124460374202e-12,
          8.9319600423465117e-13, 4.2912887011450446e-13},
      {0.10575839318584052, 0.1192888769172466, -0.044871216394513115, -0.0036224252856260895,
          0.0094869950064118535, -0.0083669704941203467, -2.9135656593505116e-05,
          0.0035239035866869144, -0.0007390444403670781, -0.00062910457096664128,
          0.00025840903317121081, -5.2406410281090148e-05, -2.9831607995401498e-05,
          6.3933958201471604e-05, -9.2916300658701673e-06, -1.6083101938086686e-05,
          4.7991528289623524e-06, 3.0018494411866362e-07, -7.1791646433540223e-07,
          9.9099988368779289e-07, -1.1902656274849245e-07, -2.8202451590108784e-07,
          7.4669882021147066e-08, 9.5231456771466545e-09, -1.0814755839347003e-08,
          1.4638105123558938e-08, -1.8451641438361848e-09, -3.9145744812927652e-09,
          1.0115949502839698e-09, 3.9799146232511823e-11, -1.1295651872287745e-10,
          2.0387175880192344e-10, -3.0281633311466152e-11, -4.3286789476415877e-11,
          1.1627733412643958e-11, -1.964636798938933e-12, -6.7945181899081481e-13,
          2.5030156105610037e-12, -4.3446903017441672e-13, -3.5683047761836781e-13,
          1.0713730773291332e-13, -5.4417647921805473e-14},
      {0.10559414984763835, 0.098916483182678955, -0.036727166657276641, 0.0035706854287673754,
          0.0052249433889826986, -0.0077361598042784765, 0.00072275525053893969,
          0.002223439808864009, -0.00054903042679437907, -0.00018293830453981622,
          0.00011359923426444327, -8.4210319077988784e-05, 1.6847183675636392e-06,
          3.4039595925688784e-05, -7.0352345655997526e-06, -4.0090129224158222e-06,
          1.6593477877980426e-06, -9.1697883341148802e-07, -1.1036365050323734e-08,
          4.3722184396728813e-07, -8.5327542599922005e-08, -5.100624672203282e-08,
          1.916725976627618e-08, -1.1327986060347709e-08, 2.4426942945105127e-10,
          4.9080406717882547e-09, -9.7567520766905678e-10, -4.3705641816313566e-10,
          1.7670460595574739e-10, -1.4614695724996077e-10, 1.1711121180447908e-11,
          4.7069717896296325e-11, -9.933558123395531e-12, -1.9375437763857343e-12,
          1.209166855744592e-12, -1.6969632555173942e-12, 2.1574447971389656e-13,
          3.643315417512156e-13, -8.4268677383364699e-14, 1.0710968051403759e-14,
          4.5449545231716703e-15, -1.6204784108832298e-14},
      {0.10274876649455389, 0.082388231262949113, -0.029594712510877145, 0.0073769129346921139,
          0.0026116599481076212, -0.0064919261730017157, 0.00087592506088373919,
          0.0013253261398085572, -0.00035657195558004654, 2.6142032801549905e-07,
          4.2198973210975666e-05, -6.687447603017941e-05, 7.7275773839267965e-06,
          1.5498908606435651e-05, -3.7366737273881412e-06, -2.4632832436591963e-07,
          4.3692707805033829e-07, -6.6932902510447816e-07, 7.9956651368923742e-08,
          1.4821558660880781e-07, -3.5021442567250654e-08, 3.7540899625090778e-10,
          3.1949808961510905e-09, -6.4415041827151229e-09, 8.8339275150183412e-10,
          1.1591732656207728e-09, -2.8569197684824971e-10, 5.888480921185181e-11,
          1.337654496705298e-11, -5.6086795170936242e-11, 8.8832525177748282e-12,
          6.9173475153734215e-12, -1.9155137457462672e-12, 1.0241898526113924e-12,
          -3.3761493645360968e-14, -4.1275881604463323e-13, 7.4099077902883533e-14,
          2.4513527317641353e-14, -9.5096849268667741e-15, 1.1002590814394527e-14,
          -1.2189024207217692e-15, -2.386417604490929e-15},
      {0.098552743668498574, 0.069105485537073838, -0.023748948648688404, 0.009153419127867133,
          0.0010821868448962775, -0.0052188307061269441, 0.00080154645149839844,
          0.00076010976374728256, -0.00021801398623228182, 6.1364831351461675e-05,
          1.1244051283343162e-05, -4.45242406370981e-05, 6.7089809379900835e-06,
          6.3038793626737489e-06, -1.7297717865112385e-06, 5.7996967779499512e-07,
          5.5694441661763496e-08, -3.5303794429821028e-07, 5.6159951294022277e-08,
          4.0451225065215566e-08, -1.1688442745153276e-08, 6.2394516032567826e-09,
          -1.1250731502506116e-10, -2.5051012145176248e-09, 4.3395138450518351e-10,
          1.7857986133511083e-10, -6.3259155747311975e-11, 5.9490600607943387e-11,
          -5.2712791314696979e-12, -1.4993882707790749e-11, 2.8704150939517767e-12,
          2.0131647753233949e-13, -2.3342819826892688e-13, 4.5952572403220678e-13,
          -5.9443068657817919e-14, -6.9371350013227325e-14, 1.519489716981375e-14,
          -5.0936671143349354e-15, -1.5201581451851507e-16, 2.7787442306154566e-15,
          -4.3565628150798704e-16, -1.977820215413138e-16},
      {0.015834456486758908, 0.18189949599571603, -0.011974219166074876, -0.061884785120021164,
          0.0076105595229791481, 0.022219610435871454, -0.0048034644497596613,
          -0.0081543924515372363, 0.0029810509129277211, 0.0027415155720322392,
          -0.0016798288496989492, -0.00060031835431978843, 0.00077610442000463872,
          -0.00012350790268939651, -0.00024953928313956459, 0.00023076940944547638,
          2.3147650640078795e-05, -0.00014102696430587765, 3.0900594996186461e-05,
          4.9284735975245839e-05, -2.2250680076774665e-05, -5.3747152725435892e-06,
          7.2855338973082374e-06, -4.7734984218700928e-06, -4.3677994915743828e-07,
          3.1778669448823886e-06, -7.8023574251168711e-07, -8.3774987103204781e-07,
          3.947298063660585e-07, -3.7160132628615939e-08, -6.8256821511354266e-08,
          1.14645046731138e-07, -1.802923434642158e-08, -3.9796507983717999e-08,
          1.3753259546034693e-08, 2.2589309179471637e-09, -2.900458494300487e-09,
          3.2240741793201159e-09, -3.7060535733979804e-10, -1.2644270923328861e-09,
          3.7888191325716094e-10, 9.7796521009671359e-11},
      {0.04404967552904368, 0.1710801581101998, -0.029702653773484237, -0.049716373914351651,
          0.015196668799016348, 0.012775047441556237, -0.0067368228281011458,
          -0.0021333545799789217, 0.0025489947286640915, -0.00039451396777444113,
          -0.00073731820635519307, 0.00060151157149911474, 9.9474373538207395e-05,
          -0.00033580332827709563, 4.6335427470913528e-05, 0.00011864099007079731,
          -4.0270942566394597e-05, -2.1132865014045182e-05, 1.5265663802074448e-05,
          -4.390215966745447e-06, -2.5270253530812898e-06, 4.7903638108824098e-06,
          -6.2910155389062726e-07, -1.6878540734202576e-06, 5.5095247469343916e-07,
          1.9449801552523306e-07, -1.5624930418755774e-07, 9.8849410298949366e-08,
          4.4278316629050682e-09, -5.6938802979450026e-08, 1.32206332064705e-08,
          1.1050776751611257e-08, -4.8883508775282182e-09, 1.3885379539825713e-09,
          4.3622477776272228e-10, -1.3996778757539352e-09, 2.6898466033154662e-10,
          3.1480517321063073e-10, -1.1308642567708166e-10, 1.8402375533760566e-11,
          1.123480047692892e-11, -2.897980744375413e-11},
      {0.064981766525915863, 0.15383430175721524, -0.037795728025415018, -0.033998210641176155,
          0.015356586255212754, 0.0039045428583760239, -0.0048216658207042695,
          0.0014084756882920865, 0.001037211136220615, -0.0012275032847224841,
          -3.3459791166333645e-05, 0.00051375842671208722, -9.5422927179973838e-05,
          -0.00013346228771129415, 5.0501841389180276e-05, 1.1220762092738858e-05,
          -1.3670526179734618e-05, 8.4668772933543781e-06, 1.0024367359222684e-06,
          -4.8111012381049593e-06, 8.797460480712093e-07, 1.1832311621167806e-06,
          -4.3319498900140414e-07, -2.4378654338834462e-08, 8.294405804330185e-08,
          -9.3751133210635584e-08, 7.1442898226736384e-09, 3.4675348418349662e-08,
          -8.9741163491505861e-09, -3.9820159181918372e-09, 2.2725424552434624e-09,
          -1.4021534997050607e-09, -2.0542313127749184e-11, 7.1229209039736953e-10,
          -1.547546852448901e-10, -1.0436681207961221e-10, 4.3670725008236297e-11,
          -1.9961843842501919e-11, -1.2765764036847669e-12, 1.2068884032362956e-11,
          -2.4435746655807111e-12, -1.7666308758071728e-12},
      {0.078368089276038375, 0.13448697055215489, -0.03868818336170738, -0.020074453959628463,
          0.012238212220160792, -0.0015406743003744609, -0.0025214575863136677,
          0.0024431827003760505, 0.0001160473305083581, -0.00099355934989939264,
          0.00016222995993502666, 0.00023312094336293949, -7.8444349804016885e-05,
          -1.5995381972152464e-05, 1.8492073850503256e-05, -1.2530954227546983e-05,
          -8.9601502708147923e-07, 6.0899634933248657e-06, -1.0939086442599852e-06,
          -1.265200184081387e-06, 4.4742355975680076e-07, -1.2744553521076795e-08,
          -6.9549253383909399e-08, 9.5205227900088207e-08, -9.1298895723234707e-09,
          -2.9160207288728072e-08, 7.4140048828748543e-09, 2.3911926984375538e-09,
          -1.5401484392505839e-09, 1.2020460676821471e-09, -4.0658911582226548e-11,
          -4.8034435329546798e-10, 1.0396091045321892e-10, 5.3778271974627532e-11,
          -2.3894361345310206e-11, 1.4545103068799456e-11, -9.4478412284633397e-14,
          -6.5816360659925451e-12, 1.3351108494471903e-12, 7.3795277549204462e-13,
          -2.9532144913076573e-13, 1.8468455591108168e-13},
      {0.085652255340067096, 0.11578632902257903, -0.035701179108830541, -0.009667120581553057,
          0.008625555455137773, -0.004040534137662876, -0.00094871862007246243,
          0.0022609045032649438, -0.00022759240377807297, -0.00058639430139257567,
          0.00014760270168716179, 6.2783953904636431e-05, -3.6950826806590278e-05,
          1.674679080213108e-05, 2.9050756397290152e-06, -9.7347610562098064e-06,
          1.4015296502497284e-06, 2.0886906102901262e-06, -6.1344291775285513e-07,
          -4.5983364509998504e-08, 9.7739452459910804e-08, -1.1179962213774147e-07,
          8.0437358996775808e-09, 3.4439703349610722e-08, -7.732043599539803e-09,
          -3.0379699204613232e-09, 1.5769189965828921e-09, -1.1029543090995177e-09,
          1.7915666453069666e-11, 4.3921481181760692e-10, -8.6254878128767087e-11,
          -4.8640543843614852e-11, 1.9121432977161244e-11, -1.0882446067016586e-11,
          -4.4150905622855453e-14, 4.8217963956242306e-12, -9.0321118263693806e-13,
          -5.2100585345674885e-13, 1.9024463988581255e-13, -1.1540023273228277e-13,
          2.5265579360411013e-15, 4.6800617009358966e-14},
      {0.088596360094168408, 0.09903214847166468, -0.031199524017938889, -0.0026097784882340358,
          0.0056252021214893864, -0.004751677335572118, -9.9663699816628215e-05,
          0.0017222826631650709, -0.00028250888669069981, -0.00028583307260931752,
          9.2710652754311756e-05, -6.5702955141457973e-06, -1.2289682474904774e-05,
          1.7410745985311356e-05, -1.3970380717900029e-06, -4.6337148948937072e-06,
          1.0218350618161469e-06, 3.7267053012892565e-07, -1.9979598250176203e-07,
          1.3610872266228777e-07, 3.6017372717420987e-10, -5.3116985380303066e-08,
          9.5552196532022559e-09, 6.4601821952669311e-09, -2.2748560938495806e-09,
          9.740898665632231e-10, 7.950919767042221e-11, -5.1848000054016285e-10,
          8.4375885705037243e-11, 7.033140594266359e-11, -2.137592798259578e-11,
          7.7158994050205048e-12, 7.8932545027567917e-13, -4.5780346321052301e-12,
          7.3188529775416933e-13, 5.8730213659569121e-13, -1.7254131025984207e-13,
          7.2957568816140884e-14, 3.1060289299689247e-15, -3.6834277056166681e-14,
          6.1152454332984682e-15, 3.8212252058986286e-15},
      {0.088692942210527925, 0.084617536384347714, -0.026490137870171369, 0.0018610562288209306,
          0.0034484386111970551, -0.0045730210499587828, 0.00028026264760993181,
          0.0011958911314364558, -0.00023632301938042601, -0.00011223936597098862,
          4.9203690388295892e-05, -2.5225351617743317e-05, -1.8571223797338034e-06,
          1.1436588322230915e-05, -1.7140373162601586e-06, -1.687975055820657e-06,
          4.8545618154054585e-07, -9.5012028242066359e-08, -3.8329859007406957e-08,
          9.3118123911244103e-08, -1.1650047410985993e-08, -1.6609966539080871e-08,
          4.0382039711088369e-09, -2.3146883155032699e-10, -3.7106486627604849e-10,
          7.0634622258595205e-10, -8.2556604970359258e-11, -1.320300886856707e-10,
          3.0039434885898045e-11, -1.2770668862649529e-12, -2.5629367718893984e-12,
          5.1403190249150858e-12, -6.2548169328543413e-13, -8.8742817950856665e-13,
          2.0136596372520206e-13, -2.627787509818633e-14, -1.2706753654508683e-14,
          3.5376876830085057e-14, -4.7467541737128828e-15, -4.989476339203883e-15,
          1.1917906616662934e-15, -3.7341276631055689e-16},
      {0.087035826494136284, 0.072475046798383597, -0.022170021224008876, 0.0045226641999971283,
          0.001976733233881802, -0.0040361258583409701, 0.00040677749674292125,
          0.00078799680836951229, -0.00017188603345531241, -2.5139690822949671e-05,
          2.3031308560313114e-05, -2.4688075095001741e-05, 1.4846663426794174e-06,
          6.3361759268437174e-06, -1.1702326995306519e-06, -4.2057012922518715e-07,
          1.8536298655018731e-07, -1.452375959054787e-07, 5.1471130215551068e-09,
          4.3884692523286784e-08, -7.504701381583195e-09, -3.3378657252069983e-09,
          1.2229399998298238e-09, -8.8981795189582859e-10, 3.2221510421151667e-11,
          2.7395817825110037e-10, -4.6202575424144659e-11, -1.8673352538394834e-11,
          6.8445410983339767e-12, -5.7157320943963876e-12, 3.3650953340970363e-13,
          1.5443270301907569e-12, -2.683713398375219e-13, -7.0655188001245195e-14,
          3.1851807771479183e-14, -3.6271193505412112e-14, 3.20399877054657e-15,
          7.676920861198626e-15, -1.4185632153958614e-15, -7.3031019266637704e-17,
          1.1324653572400885e-16, -2.1199579908475735e-16},
      {0.011293684286057372, 0.15564470330529059, -0.0068802804927849976, -0.044602949837195489,
          0.0033566301025874947, 0.013336428707388761, -0.0015819834767139445,
          -0.0041652102274394925, 0.00076725268911496231, 0.0013393027573439104,
          -0.00038415536725247454, -0.00041959531395156487, 0.00018947703653130867,
          0.00011007283726252552, -8.5390088692657596e-05, -1.1584881404495135e-05,
          3.1959893928758385e-05, -1.1320502455626092e-05, -8.2865864786873053e-06,
          1.0232620473534085e-05, 3.3459209694050548e-07, -4.9960355400909797e-06,
          1.0514285872977571e-06, 1.5307026868312661e-06, -6.4912893793321304e-07,
          -1.678261137251179e-07, 2.062843694646942e-07, -1.1370346551981089e-07,
          -2.1837616277048989e-08, 7.7520431008319303e-08, -1.4001760089824088e-08,
          -2.2871832892283238e-08, 8.5782822988523131e-09, 1.4686727950248564e-09,
          -2.0940741567195145e-09, 1.7905575689678761e-09, -3.0873868768018318e-11,
          -8.5108279327494592e-10, 2.1021899645061547e-10, 1.4828646031633427e-10,
          -7.2037575284270615e-11, 2.4245381797603848e-11},
      {0.032293470096192843, 0.1491387965457425, -0.018492830902832599, -0.038602012141845395,
          0.0081031366830453568, 0.0094883721633021913, -0.0031851016907405633,
          -0.0020378239885478914, 0.0011563133329104188, 0.00027114703090391258,
          -0.00037908521379822915, 6.1515049190162099e-05, 0.00010379750190852067,
          -7.2504851672782858e-05, -1.8438302032374427e-05, 3.7865719471823743e-05,
          -1.5636449208121903e-06, -1.391571745556884e-05, 3.0811516430541134e-06,
          3.4424343727041224e-06, -1.4817695922225286e-06, -2.6085503487244087e-07,
          4.1451691847746154e-07, -2.4598196323010413e-07, -4.2859220748421693e-08,
          1.4539995135453919e-07, -2.2268442639227045e-08, -4.109591365970794e-08,
          1.3649135057248881e-08, 3.6301509783979792e-09, -3.4924267455543694e-09,
          2.2994661597038474e-09, 1.4650810729473634e-10, -1.2042286272652038e-09,
          2.3998233797061242e-10, 2.4782471906701344e-10, -9.5619259487141417e-11,
          1.0544353031771555e-11, 1.3315594724210167e-11, -2.2700168244294811e-11,
          2.8660653088132187e-12, 6.4594581841176365e-12},
      {0.049382973715001791, 0.13792475472347707, -0.025560416053820784, -0.029513927618069834,
          0.0095761769642388507, 0.0047029939115608025, -0.0029810034484159349,
          -6.1680503721156724e-05, 0.00076274266883472996, -0.00037271885604327291,
          -0.00013807205190735739, 0.00020237915113600709, 2.6671549126230529e-06,
          -7.1204298610852304e-05, 1.1161385063273019e-05, 1.7515150003974354e-05,
          -5.6571028487322335e-06, -2.1073113082856996e-06, 1.6485803298034958e-06,
          -5.6474534155281966e-07, -2.4620101299438003e-07, 4.3287618537088071e-07,
          -3.3496599883812673e-08, -1.3650710679000146e-07, 3.3529896511862622e-08,
          2.0149369157731768e-08, -1.0315969321308837e-08, 2.928400278258317e-09,
          1.2593555415526311e-09, -2.6051297927949157e-09, 3.1159650659904361e-10,
          7.0221893570424368e-10, -1.937585964585925e-10, -5.2210856168100338e-11,
          4.1145254107123865e-11, -3.1085879941250207e-11, 6.7393736701694098e-14,
          1.3039037590431531e-11, -2.6420193296542954e-12, -1.8836582309205423e-12,
          7.4856059608512363e-13, -2.6727950624116275e-13},
      {0.061779604637662355, 0.12431483392408138, -0.028245116048182851, -0.020210703566916028,
          0.008770849300920279, 0.00090425753617371805, -0.0020420787613094583,
          0.0009805010197693996, 0.00030141141929331997, -0.0005037492640087071,
          1.2089890777708439e-05, 0.00015243549459997681, -2.6662131386589081e-05,
          -2.9680383200731892e-05, 1.0272123234491374e-05, 1.5784778083710531e-06,
          -2.2739986445260565e-06, 1.4462189397451343e-06, 1.7472329489673867e-07,
          -6.7162248988771277e-07, 8.8689386271957853e-08, 1.533199262528992e-07,
          -4.3454294995274004e-08, -1.0642483552106453e-08, 9.2733122906888927e-09,
          -6.2275994484617519e-09, -3.4137309686421681e-10, 2.733489996174225e-09,
          -4.5850795487759794e-10, -4.8936930252560174e-10, 1.6058421976472081e-10,
          -1.2795492088989602e-11, -2.0759372549479615e-11, 3.204125165888141e-11,
          -3.1369861530740925e-12, -8.3565058435560432e-12, 1.9887983774760428e-12,
          5.3170789137037925e-13, -3.5689673184872675e-13, 3.0338990424568915e-13,
          -1.1950330061336147e-14, -1.0448507515564416e-13},
      {0.069829302594627718, 0.11019075420672515, -0.027861375529444697, -0.012283351130750826,
          0.0070133155966005389, -0.0014400925251589851, -0.0011191774262766937,
          0.0012691586718305381, 1.9603381777886264e-05, -0.00040055164254148618,
          5.7769419556510494e-05, 7.5520951479916349e-05, -2.2105521428329607e-05,
          -4.2110398367829228e-06, 4.4718417619897032e-06, -2.8548939652110781e-06,
          -2.7184915021417649e-07, 1.2138120604619081e-06, -1.6029562503272185e-07,
          -2.4116459411981785e-07, 6.6067481463458659e-08, 1.0244064762206581e-08,
          -1.1710597225506057e-08, 9.8359123579565781e-09, -3.6565104014408109e-12,
          -3.4633153911925202e-09, 6.0489992451204443e-10, 4.8611500114132533e-10,
          -1.6727992199296342e-10, 3.9908472394350031e-11, 1.4959179446514121e-11,
          -3.4585593084796032e-11, 4.1706768284680484e-12, 7.0161684898722901e-12,
          -1.7426868765949001e-12, -1.3047783720919576e-13, 2.3009153611579292e-13,
          -2.8953679199992015e-13, 2.4040614122195394e-14, 7.3529984310353916e-14,
          -1.5480137437572935e-14, -4.0292030938846693e-15},
      {0.074375481720718908, 0.096736792852828699, -0.025763752214833995, -0.0062119287646566056,
          0.0051557849821029147, -0.0025917413945729293, -0.00046515353541832502,
          0.0011683668623480503, -9.9225176413528696e-05, -0.00025523105841593276,
          5.4145014530097033e-05, 2.4647536739560593e-05, -1.2012634013514513e-05,
          4.6346685616045677e-06, 1.0422004471414151e-06, -2.5693715166893746e-06,
          2.5965266645960654e-07, 5.336058490854238e-07, -1.2188485808444982e-07,
          -3.2376722254418406e-08, 2.1675913031485936e-08, -1.5401956179823553e-08,
          -3.1912641420676374e-10, 5.5500540167582556e-09, -8.6942340535422795e-10,
          -7.5552479065855098e-10, 2.3104169384216972e-10, -5.0141798009932731e-11,
          -1.9267555868746231e-11, 4.4027410732833135e-11, -5.0283973092106997e-12,
          -8.2633618132185864e-12, 1.9249750989919049e-12, 9.8536083383248337e-14,
          -2.2579824502411891e-13, 3.0590235038866673e-13, -2.6333357398069495e-14,
          -6.9413858169380166e-14, 1.4069497400114111e-14, 2.822773746442108e-15,
          -1.8664630049642624e-15, 2.0137338769908461e-15},
      {0.076331742589298451, 0.084546657679027512, -0.022934794506059501, -0.0018770923276336788,
          0.0035731867436152248, -0.0029694793057078424, -7.6975684032054022e-05,
          0.00093318853819240421, -0.0001258325896848544, -0.00013990336970474892,
          3.766477752392959e-05, 1.6200587047239595e-07, -4.9612689836154288e-06,
          5.6284402459020905e-06, -2.1931086748283866e-07, -1.4698664559585981e-06,
          2.5165468870265747e-07, 1.4907982081546937e-07, -5.3307732778189217e-08,
          2.0653197692655789e-08, 3.0744403433366773e-09, -1.0655836126762723e-08,
          1.2994752870359691e-09, 1.6935012121564038e-09, -4.0726562170220815e-10,
          1.365198305155142e-11, 4.1867652194592851e-11, -6.4781981241160806e-11,
          5.674355276958902e-12, 1.3315796710053549e-11, -2.6493526900686274e-12,
          -4.791881428875239e-13, 3.3791948080174974e-13, -3.6355561643778685e-13,
          2.3599868663442195e-14, 8.7562751991004466e-14, -1.5760004924321496e-14,
          -4.6115862982963361e-15, 2.1592099298210866e-15, -2.0051881440366676e-15,
          1.1461062193732518e-16, 5.1136095665153071e-16},
      {0.076485838011863455, 0.073823599533612783, -0.019966788586879957, 0.0010583855044407854,
          0.0023585648305893304, -0.0029170879744658982, 0.00012206037861523219,
          0.00069071752339756258, -0.00011305026882779581, -6.5414579041041021e-05,
          2.2629383234039304e-05, -8.5897375557697734e-06, -1.3404780446627439e-06,
          4.2553025825100925e-06, -4.7005882346890524e-07, -6.6881556033079566e-07,
          1.4878507035766375e-07, 3.8690891846964068e-09, -1.630580131771606e-08,
          2.1413880454876678e-08, -1.4672527420881952e-09, -4.6048771898097431e-09,
          8.2540175250186515e-10, 2.4665577995283481e-10, -1.1717410916177808e-10,
          9.74435738507697e-11, -3.1669909545863027e-12, -2.6564041928700521e-11,
          4.2050240012642511e-12, 2.0068284945079838e-12, -6.7683229276662641e-13,
          4.3594600927395204e-13, -4.0131710765102145e-15, -1.3793811642630687e-13,
          2.0542793847633989e-14, 1.1337802219013187e-14, -3.3895052812019769e-15,
          2.0428756862644416e-15, -1.8038444172096648e-17, -6.6188855136815052e-16,
          9.75067379216775e-17, 5.0685054701148502e-17},
  };

  private static final double[][] SINHP_T = {
      {2.1491751791144769e-13, -1.085323745611809e-10, 3.8624317182343126e-08,
          -8.9884641489031256e-06, 0.0012179990581971101, -0.078282393638219497,
          1.4912869069012666, -1.6047148016588481e-17},
  };

  private static final double[][] COSHP_T = {
      {-3.0142101619856778e-15, 1.7758555332750598e-12, -7.5838669443859155e-10,
          2.2061010107419925e-07, -3.9858925971993001e-05, 0.0038426780118633549,
          -0.14640674972236453, 0.84971049196953352, -4.3622706935448593e-17},
  };

  private static final double ERFCX2_T_MID = 0.014574759945130316;
  private static final double ERFCX2_T_IHALF = 75.740259740259745;
  private static final double SINHP_T_MID = 0.125;
  private static final double SINHP_T_IHALF = 8;
  private static final double COSHP_T_MID = 0.125;
  private static final double COSHP_T_IHALF = 8;
  private static final double PIO2_1 = 1.5707963267341256;
  private static final double PIO2_2 = 6.077100506303966e-11;
  private static final double PIO2_3 = 2.0222662487959506e-21;
  private static final double TWO_OVER_PI = 0.63661977236758138;
}
