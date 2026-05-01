package org.matheclipse.core.convert;

import java.math.BigInteger;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.CalculusFieldElement;
import org.hipparchus.Field;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.util.FieldSinCos;
import org.hipparchus.util.FieldSinhCosh;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;

/**
 * A {@link CalculusFieldElement} backed directly by an {@link Apfloat}. This class is intentionally
 * NOT part of the IExpr hierarchy, which avoids the Java type-erasure conflict that would arise
 * from implementing both CalculusFieldElement&lt;IExpr&gt; (via IExpr) and
 * CalculusFieldElement&lt;ApfloatField&gt;.
 */
public class ApfloatField implements CalculusFieldElement<ApfloatField> {
  public final static ApfloatField ONE = new ApfloatField(Apfloat.ONE);

  public final static ApfloatField ZERO = new ApfloatField(Apfloat.ZERO);

  public static ApfloatField of(Apfloat value) {
    return new ApfloatField(value);
  }

  public static ApfloatField valueOf(final Apfloat value) {
    return new ApfloatField(value);
  }

  public static ApfloatField valueOf(final BigInteger numerator) {
    return new ApfloatField(new Apfloat(numerator));

  }

  public static ApfloatField valueOf(final BigInteger numerator, final BigInteger denominator) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    Apfloat n = new Apfloat(numerator, h.precision());
    Apfloat d = new Apfloat(denominator, h.precision());
    return new ApfloatField(h.divide(n, d));
  }

  /**
   * Create a new instance.
   *
   * @param value
   * @return
   */
  public static ApfloatField valueOf(final double value) {
    return valueOf(new Apfloat(value));
  }

  public static ApfloatField valueOf(final String value, long precision) {
    return new ApfloatField(value, precision);
  }

  private final Apfloat value;

  public ApfloatField(Apfloat value) {
    this.value = value;
  }

  public ApfloatField(double value) {
    this.value = new Apfloat(value);
  }

  public ApfloatField(String value, long precision) {
    this.value = new Apfloat(value, precision);
  }

  @Override
  public ApfloatField abs() {
    return wrap(h().abs(value));
  }

  @Override
  public ApfloatField acos() {
    return wrap(h().acos(value));
  }

  // -------------------------------------------------------------------------
  // Helper
  // -------------------------------------------------------------------------

  @Override
  public ApfloatField acosh() {
    return wrap(h().acosh(value));
  }

  @Override
  public ApfloatField add(ApfloatField a) {
    return wrap(h().add(value, a.value));
  }

  // -------------------------------------------------------------------------
  // FieldElement
  // -------------------------------------------------------------------------

  @Override
  public ApfloatField asin() {
    return wrap(h().asin(value));
  }

  @Override
  public ApfloatField asinh() {
    return wrap(h().asinh(value));
  }

  @Override
  public ApfloatField atan() {
    return wrap(h().atan(value));
  }

  @Override
  public ApfloatField atan2(ApfloatField x) throws MathIllegalArgumentException {
    return wrap(h().atan2(value, x.value));
  }

  @Override
  public ApfloatField atanh() {
    return wrap(h().atanh(value));
  }

  @Override
  public ApfloatField cbrt() {
    return wrap(h().cbrt(value));
  }

  @Override
  public ApfloatField ceil() {
    return wrap(ApfloatMath.ceil(value));
  }

  @Override
  public ApfloatField copySign(ApfloatField sign) {
    return wrap(ApfloatMath.copySign(value, sign.value));
  }

  // -------------------------------------------------------------------------
  // CalculusFieldElement — basic numeric
  // -------------------------------------------------------------------------

  @Override
  public ApfloatField copySign(double sign) {
    return wrap(ApfloatMath.copySign(value, new Apfloat(sign)));
  }

  @Override
  public ApfloatField cos() {
    return wrap(h().cos(value));
  }

  @Override
  public ApfloatField cosh() {
    return wrap(h().cosh(value));
  }

  @Override
  public ApfloatField divide(ApfloatField a) {
    return wrap(h().divide(value, a.value));
  }

  // -------------------------------------------------------------------------
  // CalculusFieldElement — elementary functions
  // -------------------------------------------------------------------------

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof ApfloatField))
      return false;
    return value.equals(((ApfloatField) obj).value);
  }

  @Override
  public ApfloatField exp() {
    return wrap(h().exp(value));
  }

  @Override
  public ApfloatField expm1() {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.subtract(h.exp(value), Apfloat.ONE));
  }

  @Override
  public ApfloatField floor() {
    return wrap(ApfloatMath.floor(value));
  }

  @Override
  public ApfloatField getAddendum() {
    return isFinite() ? new ApfloatField(value.subtract(value.real())) : ZERO;
  }

  /** Unwrap the underlying {@link Apfloat}. */
  public Apfloat getApfloat() {
    return value;
  }

  public ApfloatNum getApfloatNum() {
    return ApfloatNum.valueOf(value);
  }

  @Override
  public Field<ApfloatField> getField() {
    return F.APFLOAT_FIELD;
  }

  @Override
  public double getReal() {
    return value.doubleValue();
  }

  private FixedPrecisionApfloatHelper h() {
    return EvalEngine.getApfloat();
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public ApfloatField hypot(ApfloatField y) throws MathIllegalArgumentException {
    FixedPrecisionApfloatHelper h = h();
    Apfloat x2 = h.multiply(value, value);
    Apfloat y2 = h.multiply(y.value, y.value);
    return wrap(h.sqrt(h.add(x2, y2)));
  }

  @Override
  public boolean isFinite() {
    return true;
  }

  @Override
  public boolean isInfinite() {
    return false; // Apfloat does not have IEEE infinity
  }

  @Override
  public boolean isNaN() {
    return false; // Apfloat does not have NaN
  }

  @Override
  public ApfloatField linearCombination(ApfloatField a1, ApfloatField b1, ApfloatField a2,
      ApfloatField b2) {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.add(h.multiply(a1.value, b1.value), h.multiply(a2.value, b2.value)));
  }

  @Override
  public ApfloatField linearCombination(ApfloatField a1, ApfloatField b1, ApfloatField a2,
      ApfloatField b2, ApfloatField a3, ApfloatField b3) {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.add(h.add(h.multiply(a1.value, b1.value), h.multiply(a2.value, b2.value)),
        h.multiply(a3.value, b3.value)));
  }

  @Override
  public ApfloatField linearCombination(ApfloatField a1, ApfloatField b1, ApfloatField a2,
      ApfloatField b2, ApfloatField a3, ApfloatField b3, ApfloatField a4, ApfloatField b4) {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.add(h.add(h.add(h.multiply(a1.value, b1.value), h.multiply(a2.value, b2.value)),
        h.multiply(a3.value, b3.value)), h.multiply(a4.value, b4.value)));
  }

  @Override
  public ApfloatField linearCombination(ApfloatField[] a, ApfloatField[] b)
      throws MathIllegalArgumentException {
    FixedPrecisionApfloatHelper h = h();
    Apfloat sum = Apfloat.ZERO;
    for (int i = 0; i < a.length; i++) {
      sum = h.add(sum, h.multiply(a[i].value, b[i].value));
    }
    return wrap(sum);
  }

  @Override
  public ApfloatField linearCombination(double a1, ApfloatField b1, double a2, ApfloatField b2) {
    return linearCombination(wrap(new Apfloat(a1)), b1, wrap(new Apfloat(a2)), b2);
  }

  @Override
  public ApfloatField linearCombination(double a1, ApfloatField b1, double a2, ApfloatField b2,
      double a3, ApfloatField b3) {
    return linearCombination(wrap(new Apfloat(a1)), b1, wrap(new Apfloat(a2)), b2,
        wrap(new Apfloat(a3)), b3);
  }

  @Override
  public ApfloatField linearCombination(double a1, ApfloatField b1, double a2, ApfloatField b2,
      double a3, ApfloatField b3, double a4, ApfloatField b4) {
    return linearCombination(wrap(new Apfloat(a1)), b1, wrap(new Apfloat(a2)), b2,
        wrap(new Apfloat(a3)), b3, wrap(new Apfloat(a4)), b4);
  }

  @Override
  public ApfloatField linearCombination(double[] a, ApfloatField[] b)
      throws MathIllegalArgumentException {
    FixedPrecisionApfloatHelper h = h();
    Apfloat sum = Apfloat.ZERO;
    for (int i = 0; i < a.length; i++) {
      sum = h.add(sum, h.multiply(new Apfloat(a[i]), b[i].value));
    }
    return wrap(sum);
  }

  @Override
  public ApfloatField log() {
    return wrap(h().log(value));
  }

  @Override
  public ApfloatField log10() {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.log(value, new Apfloat(10)));
  }

  @Override
  public ApfloatField log1p() {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.log(h.add(value, Apfloat.ONE)));
  }

  @Override
  public ApfloatField multiply(ApfloatField a) {
    return wrap(h().multiply(value, a.value));
  }

  @Override
  public ApfloatField multiply(int n) {
    return wrap(h().multiply(value, new Apfloat(n)));
  }

  @Override
  public ApfloatField negate() {
    return wrap(h().negate(value));
  }

  @Override
  public ApfloatField newInstance(double value) {
    return new ApfloatField(value);
  }

  @Override
  public ApfloatField pow(ApfloatField e) {
    return wrap(h().pow(value, e.value));
  }

  @Override
  public ApfloatField pow(double e) {
    return wrap(h().pow(value, new Apfloat(e)));
  }

  @Override
  public ApfloatField pow(int e) {
    return wrap(h().pow(value, e));
  }

  // -------------------------------------------------------------------------
  // linearCombination — compensated summation using Apfloat's full precision
  // -------------------------------------------------------------------------

  @Override
  public ApfloatField reciprocal() {
    return wrap(h().inverseRoot(value, 1));
  }

  @Override
  public ApfloatField remainder(ApfloatField a) {
    return wrap(h().mod(value, a.value));
  }

  @Override
  public ApfloatField remainder(double a) {
    return valueOf(EvalEngine.getApfloat().mod(value, new Apfloat(a)));
  }

  @Override
  public ApfloatField rint() {
    return wrap(ApfloatNum.apfloatRint(value));
  }

  @Override
  public ApfloatField rootN(int n) {
    return wrap(h().root(value, n));
  }

  @Override
  public ApfloatField scalb(int n) {
    FixedPrecisionApfloatHelper h = h();
    return wrap(h.multiply(value, h.pow(new Apfloat(2), n)));
  }

  @Override
  public ApfloatField sign() {
    if (value.signum() == 0) {
      return this;
    }
    return wrap(ApfloatMath.copySign(Apfloat.ONE, value));
  }

  @Override
  public ApfloatField sin() {
    return wrap(h().sin(value));
  }

  // -------------------------------------------------------------------------
  // Object
  // -------------------------------------------------------------------------

  @Override
  public FieldSinCos<ApfloatField> sinCos() {
    return new FieldSinCos<>(sin(), cos());
  }

  @Override
  public ApfloatField sinh() {
    return wrap(h().sinh(value));
  }

  @Override
  public FieldSinhCosh<ApfloatField> sinhCosh() {
    return new FieldSinhCosh<>(sinh(), cosh());
  }

  @Override
  public ApfloatField sqrt() {
    return wrap(h().sqrt(value));
  }

  @Override
  public ApfloatField subtract(ApfloatField a) {
    return wrap(h().subtract(value, a.value));
  }

  @Override
  public ApfloatField tan() {
    return wrap(h().tan(value));
  }

  @Override
  public ApfloatField tanh() {
    return wrap(h().tanh(value));
  }

  @Override
  public String toString() {
    return value.toString();
  }

  private ApfloatField wrap(Apfloat v) {
    return new ApfloatField(v);
  }
}
