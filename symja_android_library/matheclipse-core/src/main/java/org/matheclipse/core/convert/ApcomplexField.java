package org.matheclipse.core.convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.CalculusFieldElement;
import org.hipparchus.Field;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.util.FieldSinCos;
import org.hipparchus.util.FieldSinhCosh;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;

/**
 * A {@link CalculusFieldElement} backed directly by an {@link Apcomplex}. This class is
 * intentionally NOT part of the IExpr hierarchy, which avoids the Java type-erasure conflict that
 * would arise from implementing both CalculusFieldElement&lt;IExpr&gt; (via IExpr) and
 * CalculusFieldElement&lt;ApcomplexField&gt;.
 */
public class ApcomplexField implements CalculusFieldElement<ApcomplexField> {

  public static final ApcomplexField I = new ApcomplexField(Apcomplex.I);
  public static final ApcomplexField ONE = new ApcomplexField(Apcomplex.ONE);
  public static final ApcomplexField ZERO = new ApcomplexField(Apcomplex.ZERO);

  public static ApcomplexField valueOf(final Apcomplex value) {
    return new ApcomplexField(value);
  }

  // -------------------------------------------------------------------------
  // Construction
  // -------------------------------------------------------------------------

  public static ApcomplexField valueOf(final Apfloat real) {
    return new ApcomplexField(real);
  }

  public static ApcomplexField valueOf(final Apfloat real, final Apfloat imag) {
    return new ApcomplexField(real, imag);
  }

  public static ApcomplexField valueOf(final BigInteger realNumerator,
      final BigInteger realDenominator, final BigInteger imagNumerator,
      final BigInteger imagDenominator) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    long prec = h.precision();
    Apfloat real = h.divide(new Apfloat(realNumerator, prec), new Apfloat(realDenominator, prec));
    Apfloat imag = h.divide(new Apfloat(imagNumerator, prec), new Apfloat(imagDenominator, prec));
    return new ApcomplexField(real, imag);
  }

  public static ApcomplexField valueOf(final Complex c, long precision) {
    return new ApcomplexField(new Apcomplex(new Apfloat(new BigDecimal(c.getReal()), precision),
        new Apfloat(new BigDecimal(c.getImaginary()), precision)));
  }

  public static ApcomplexField valueOf(final double real) {
    return new ApcomplexField(new Apcomplex(new Apfloat(real)));
  }

  public static ApcomplexField valueOf(final double real, final double imaginary) {
    return new ApcomplexField(new Apcomplex(new Apfloat(real), new Apfloat(imaginary)));
  }

  private final Apcomplex value;

  public ApcomplexField(Apcomplex value) {
    this.value = value;
  }

  public ApcomplexField(Apfloat real) {
    this.value = new Apcomplex(real);
  }

  public ApcomplexField(Apfloat real, Apfloat imag) {
    this.value = new Apcomplex(real, imag);
  }

  @Override
  public ApcomplexField abs() {
    // |z| is real; embed back into complex
    return new ApcomplexField(h().abs(value));
  }

  @Override
  public ApcomplexField acos() {
    return wrap(h().acos(value));
  }

  // -------------------------------------------------------------------------
  // Helper
  // -------------------------------------------------------------------------

  @Override
  public ApcomplexField acosh() {
    return wrap(h().acosh(value));
  }

  @Override
  public ApcomplexField add(ApcomplexField a) {
    return wrap(h().add(value, a.value));
  }

  // -------------------------------------------------------------------------
  // FieldElement
  // -------------------------------------------------------------------------

  @Override
  public ApcomplexField asin() {
    return wrap(h().asin(value));
  }

  @Override
  public ApcomplexField asinh() {
    return wrap(h().asinh(value));
  }

  @Override
  public ApcomplexField atan() {
    return wrap(h().atan(value));
  }

  /**
   * atan2(y, x) for complex numbers using: atan2(y,x) = -i * log( (x + iy) / sqrt(x^2 + y^2) )
   * which reduces to 2*atan( y / (sqrt(x^2+y^2) + x) ) for Re(x) >= 0.
   */
  @Override
  public ApcomplexField atan2(ApcomplexField x) throws MathIllegalArgumentException {
    FixedPrecisionApfloatHelper h = h();
    Apcomplex th = value; // y
    Apcomplex xv = x.value;
    final Apcomplex r = h.sqrt(h.add(h.multiply(xv, xv), h.multiply(th, th)));
    if (xv.real().compareTo(Apfloat.ZERO) >= 0) {
      return wrap(h.multiply(h.atan(h.divide(th, h.add(r, xv))), new Apfloat(2)));
    } else {
      return wrap(
          h.add(h.multiply(h.atan(h.divide(th, h.subtract(r, xv))), new Apfloat(-2)), h.pi()));
    }
  }

  @Override
  public ApcomplexField atanh() {
    return wrap(h().atanh(value));
  }

  @Override
  public ApcomplexField cbrt() {
    return wrap(h().cbrt(value));
  }

  /**
   * ceil applied component-wise (real and imaginary parts independently).
   */
  @Override
  public ApcomplexField ceil() {
    return new ApcomplexField(ApfloatMath.ceil(value.real()), ApfloatMath.ceil(value.imag()));
  }

  /**
   * copySign: copy sign of each component from {@code sign}.
   */
  @Override
  public ApcomplexField copySign(ApcomplexField sign) {
    return new ApcomplexField(ApfloatMath.copySign(value.real(), sign.value.real()),
        ApfloatMath.copySign(value.imag(), sign.value.imag()));
  }

  // -------------------------------------------------------------------------
  // CalculusFieldElement — basic numeric
  // -------------------------------------------------------------------------

  @Override
  public ApcomplexField copySign(double sign) {
    Apfloat s = new Apfloat(sign);
    return new ApcomplexField(ApfloatMath.copySign(value.real(), s),
        ApfloatMath.copySign(value.imag(), s));
  }

  @Override
  public ApcomplexField cos() {
    return wrap(h().cos(value));
  }

  @Override
  public ApcomplexField cosh() {
    return wrap(h().cosh(value));
  }

  @Override
  public ApcomplexField divide(ApcomplexField a) {
    return wrap(h().divide(value, a.value));
  }

  // -------------------------------------------------------------------------
  // CalculusFieldElement — elementary functions
  // -------------------------------------------------------------------------

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof ApcomplexField))
      return false;
    return value.equals(((ApcomplexField) obj).value);
  }

  @Override
  public ApcomplexField exp() {
    return wrap(h().exp(value));
  }

  @Override
  public ApcomplexField expm1() {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.subtract(h.exp(value), Apfloat.ONE));
  }

  /**
   * floor applied component-wise.
   */
  @Override
  public ApcomplexField floor() {
    return new ApcomplexField(ApfloatMath.floor(value.real()), ApfloatMath.floor(value.imag()));
  }

  @Override
  public ApcomplexField getAddendum() {
    // imaginary part as a purely imaginary complex: 0 + imag*i
    return new ApcomplexField(Apfloat.ZERO, value.imag());
  }

  /** Unwrap the underlying {@link Apcomplex}. */
  public Apcomplex getApcomplex() {
    return value;
  }

  /** Return the corresponding {@link ApcomplexNum}. */
  public ApcomplexNum getApcomplexNum() {
    return ApcomplexNum.valueOf(value);
  }

  @Override
  public Field<ApcomplexField> getField() {
    return ApcomplexFieldImpl.getInstance();
  }

  @Override
  public double getReal() {
    return value.real().doubleValue();
  }

  private FixedPrecisionApfloatHelper h() {
    return EvalEngine.getApfloat();
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  /**
   * hypot(y): sqrt(this^2 + y^2) in the complex sense.
   */
  @Override
  public ApcomplexField hypot(ApcomplexField y) throws MathIllegalArgumentException {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.sqrt(h.add(h.multiply(value, value), h.multiply(y.value, y.value))));
  }

  @Override
  public boolean isFinite() {
    return true;
  }

  @Override
  public boolean isInfinite() {
    return false; // Apcomplex does not have IEEE infinity
  }

  @Override
  public boolean isNaN() {
    return false; // Apcomplex does not have NaN
  }

  @Override
  public ApcomplexField linearCombination(ApcomplexField a1, ApcomplexField b1, ApcomplexField a2,
      ApcomplexField b2) {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.add(h.multiply(a1.value, b1.value), h.multiply(a2.value, b2.value)));
  }

  @Override
  public ApcomplexField linearCombination(ApcomplexField a1, ApcomplexField b1, ApcomplexField a2,
      ApcomplexField b2, ApcomplexField a3, ApcomplexField b3) {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.add(h.add(h.multiply(a1.value, b1.value), h.multiply(a2.value, b2.value)),
        h.multiply(a3.value, b3.value)));
  }

  @Override
  public ApcomplexField linearCombination(ApcomplexField a1, ApcomplexField b1, ApcomplexField a2,
      ApcomplexField b2, ApcomplexField a3, ApcomplexField b3, ApcomplexField a4,
      ApcomplexField b4) {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.add(h.add(h.add(h.multiply(a1.value, b1.value), h.multiply(a2.value, b2.value)),
        h.multiply(a3.value, b3.value)), h.multiply(a4.value, b4.value)));
  }

  @Override
  public ApcomplexField linearCombination(ApcomplexField[] a, ApcomplexField[] b)
      throws MathIllegalArgumentException {
    FixedPrecisionApfloatHelper h = h();
    Apcomplex sum = Apcomplex.ZERO;
    for (int i = 0; i < a.length; i++) {
      sum = h.add(sum, h.multiply(a[i].value, b[i].value));
    }
    return wrap(sum);
  }

  @Override
  public ApcomplexField linearCombination(double a1, ApcomplexField b1, double a2,
      ApcomplexField b2) {
    return linearCombination(new ApcomplexField(new Apfloat(a1)), b1,
        new ApcomplexField(new Apfloat(a2)), b2);
  }

  @Override
  public ApcomplexField linearCombination(double a1, ApcomplexField b1, double a2,
      ApcomplexField b2, double a3, ApcomplexField b3) {
    return linearCombination(new ApcomplexField(new Apfloat(a1)), b1,
        new ApcomplexField(new Apfloat(a2)), b2, new ApcomplexField(new Apfloat(a3)), b3);
  }

  @Override
  public ApcomplexField linearCombination(double a1, ApcomplexField b1, double a2,
      ApcomplexField b2, double a3, ApcomplexField b3, double a4, ApcomplexField b4) {
    return linearCombination(new ApcomplexField(new Apfloat(a1)), b1,
        new ApcomplexField(new Apfloat(a2)), b2, new ApcomplexField(new Apfloat(a3)), b3,
        new ApcomplexField(new Apfloat(a4)), b4);
  }

  @Override
  public ApcomplexField linearCombination(double[] a, ApcomplexField[] b)
      throws MathIllegalArgumentException {
    FixedPrecisionApfloatHelper h = h();
    Apcomplex sum = Apcomplex.ZERO;
    for (int i = 0; i < a.length; i++) {
      sum = h.add(sum, h.multiply(new Apfloat(a[i]), b[i].value));
    }
    return wrap(sum);
  }

  @Override
  public ApcomplexField log() {
    return wrap(h().log(value));
  }

  @Override
  public ApcomplexField log10() {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.log(value, new Apfloat(10)));
  }

  @Override
  public ApcomplexField log1p() {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.log(h.add(value, Apfloat.ONE)));
  }

  @Override
  public ApcomplexField multiply(ApcomplexField a) {
    return wrap(h().multiply(value, a.value));
  }

  @Override
  public ApcomplexField multiply(int n) {
    return wrap(h().multiply(value, new Apfloat(n)));
  }

  @Override
  public ApcomplexField negate() {
    return wrap(h().negate(value));
  }

  @Override
  public ApcomplexField newInstance(double value) {
    return new ApcomplexField(new Apfloat(value));
  }

  @Override
  public ApcomplexField pow(ApcomplexField e) {
    return wrap(h().pow(value, e.value));
  }

  @Override
  public ApcomplexField pow(double e) {
    return wrap(h().pow(value, new Apfloat(e)));
  }

  @Override
  public ApcomplexField pow(int e) {
    if (e == -1) {
      return reciprocal();
    }
    return wrap(h().pow(value, e));
  }

  @Override
  public ApcomplexField reciprocal() {
    return wrap(h().inverseRoot(value, 1));
  }

  /**
   * remainder(a) applied component-wise: mod(re, a.re) + i*mod(im, a.im).
   */
  @Override
  public ApcomplexField remainder(ApcomplexField a) {
    FixedPrecisionApfloatHelper h = h();
    return new ApcomplexField(h.mod(value.real(), a.value.real()),
        h.mod(value.imag(), a.value.imag()));
  }

  @Override
  public ApcomplexField remainder(double a) {
    FixedPrecisionApfloatHelper h = h();
    Apfloat av = new Apfloat(a);
    return new ApcomplexField(h.mod(value.real(), av), h.mod(value.imag(), av));
  }

  /**
   * rint applied component-wise.
   */
  @Override
  public ApcomplexField rint() {
    return valueOf( //
        ApfloatNum.apfloatRint(value.real()), //
        ApfloatNum.apfloatRint(value.imag()));
  }

  @Override
  public ApcomplexField rootN(int n) {
    return wrap(h().root(value, n));
  }

  /**
   * scalb(n): multiply by 2^n.
   */
  @Override
  public ApcomplexField scalb(int n) {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.multiply(value, h.pow(new Apfloat(2), n)));
  }

  /**
   * sign(z) = z / |z| for complex numbers. Returns this unchanged if zero.
   */
  @Override
  public ApcomplexField sign() {
    if (value.isZero()) {
      return this;
    }
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.divide(value, h.abs(value)));
  }

  @Override
  public ApcomplexField sin() {
    return wrap(h().sin(value));
  }

  @Override
  public FieldSinCos<ApcomplexField> sinCos() {
    return new FieldSinCos<>(sin(), cos());
  }

  @Override
  public ApcomplexField sinh() {
    return wrap(h().sinh(value));
  }

  @Override
  public FieldSinhCosh<ApcomplexField> sinhCosh() {
    return new FieldSinhCosh<>(sinh(), cosh());
  }

  @Override
  public ApcomplexField sqrt() {
    return wrap(h().sqrt(value));
  }

  @Override
  public ApcomplexField subtract(ApcomplexField a) {
    return wrap(h().subtract(value, a.value));
  }

  @Override
  public ApcomplexField tan() {
    return wrap(h().tan(value));
  }

  @Override
  public ApcomplexField tanh() {
    return wrap(h().tanh(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }

  private ApcomplexField wrap(Apcomplex v) {
    return new ApcomplexField(v);
  }
}
