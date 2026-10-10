package org.matheclipse.core.numerics.functions;

import org.apfloat.ApfloatMath;
import java.math.RoundingMode;
import java.util.function.Supplier;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatArithmeticException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.external.apfloat.ApcomplexEllipticMath;

/**
 * Carlson's symmetric elliptic integrals in arbitrary precision.
 *
 * <p>
 * The integrals themselves are computed by
 * {@link org.matheclipse.external.apfloat.ApcomplexEllipticMath}, with Carlson's duplication
 * algorithms run until the truncation error is below the requested precision. (The generic field
 * versions of hipparchus' <code>CarlsonEllipticIntegral</code> stop after 16 duplication steps,
 * which bounds their accuracy to about 45 digits and loses the last digits at lower precisions
 * too.) This class converts between the expressions of Symja and the numbers of apfloat.
 *
 * <p>
 * The arguments must be numbers; they are taken with <code>digits</code> digits.
 */
public final class CarlsonArbitraryPrecision {

  /**
   * The digits which a computation carries more than the result shows. The <code>digits</code> of
   * the methods of this class include them.
   */
  public static final int GUARD_DIGITS = 10;

  private CarlsonArbitraryPrecision() {}

  /**
   * The value rounded to <code>precision</code> digits, real if its imaginary part is zero. The
   * guard digits are rounded away here; cutting them off left the last digit one too small in
   * half of the cases.
   */
  public static IExpr round(Apcomplex value, long precision) {
    precision = Math.min(value.precision(), precision);
    Apfloat re = round(value.real(), precision);
    if (value.imag().signum() == 0) {
      return F.num(re);
    }
    return F.complexNum(new Apcomplex(re, round(value.imag(), precision)));
  }

  private static Apfloat round(Apfloat x, long precision) {
    return x.signum() == 0 || x.precision() <= precision ? x
        : ApfloatMath.roundToPrecision(x, precision, RoundingMode.HALF_EVEN);
  }

  /** The number as apfloat takes it: a nonzero part with <code>digits</code> digits. */
  public static Apcomplex apcomplex(IExpr z, int digits) {
    Apcomplex value = ((INumber) z).apcomplexValue();
    return new Apcomplex(precision(value.real(), digits), precision(value.imag(), digits));
  }

  private static Apfloat precision(Apfloat x, int digits) {
    return x.signum() == 0 ? x : x.precision(digits);
  }

  /** An integral which diverges is reported by apfloat with an exception. */
  private static IExpr evaluate(Supplier<Apcomplex> integral, int digits) {
    Apcomplex value;
    try {
      value = integral.get();
    } catch (ApfloatArithmeticException aae) {
      return F.CComplexInfinity;
    }
    return round(value, Math.max(digits - GUARD_DIGITS, 1));
  }

  /**
   * <code>CarlsonRF(x,y,z)</code>
   *
   * @param digits the number of correct digits which are wanted
   */
  public static IExpr rF(IExpr x, IExpr y, IExpr z, int digits) {
    return evaluate(() -> ApcomplexEllipticMath.carlsonRF(apcomplex(x, digits),
        apcomplex(y, digits), apcomplex(z, digits)), digits);
  }

  /** <code>CarlsonRD(x,y,z) == CarlsonRJ(x,y,z,z)</code> */
  public static IExpr rD(IExpr x, IExpr y, IExpr z, int digits) {
    return evaluate(() -> ApcomplexEllipticMath.carlsonRD(apcomplex(x, digits),
        apcomplex(y, digits), apcomplex(z, digits)), digits);
  }

  /** <code>CarlsonRJ(x,y,z,p)</code> */
  public static IExpr rJ(IExpr x, IExpr y, IExpr z, IExpr p, int digits) {
    return evaluate(() -> ApcomplexEllipticMath.carlsonRJ(apcomplex(x, digits),
        apcomplex(y, digits), apcomplex(z, digits), apcomplex(p, digits)), digits);
  }

  /** <code>CarlsonRG(x,y,z)</code> */
  public static IExpr rG(IExpr x, IExpr y, IExpr z, int digits) {
    return evaluate(() -> ApcomplexEllipticMath.carlsonRG(apcomplex(x, digits),
        apcomplex(y, digits), apcomplex(z, digits)), digits);
  }
}
