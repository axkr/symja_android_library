package org.matheclipse.core.expression;

import org.matheclipse.external.apfloat.HypergeometricUWorkaround;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.UnaryOperator;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.matheclipse.external.apfloat.ApfloatRounding;
import org.matheclipse.external.apfloat.ApfloatSignificance;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatArithmeticException;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LossOfPrecisionException;
import org.apfloat.NumericComputationException;
import org.apfloat.OverflowException;
import org.hipparchus.complex.Complex;
import org.matheclipse.external.apfloat.ApcomplexEllipticMath;
import org.matheclipse.external.apfloat.ApcomplexZetaMath;
import org.matheclipse.external.apfloat.ApcomplexTrigMath;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.PrecisionLoss;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import org.matheclipse.parser.client.ParserConfig;

/**
 * <code>IComplexNum</code> implementation which wraps a {@link Apcomplex} value to represent a
 * numeric complex floating-point number.
 */
public class ApcomplexNum implements IComplexNum {

  /** */
  private static final long serialVersionUID = -6033055105824482264L;

  /** The square root of -1. A number representing "0.0 + 1.0i" */
  public static final ApcomplexNum I = new ApcomplexNum(Apcomplex.I);

  /** A complex number representing "1.0 + 0.0i" */
  public static final ApcomplexNum ONE = new ApcomplexNum(Apcomplex.ONE);

  /** A complex number representing "-1.0 + 0.0i" */
  public static final ApcomplexNum MINUS_ONE = new ApcomplexNum(new Apfloat(-1));

  /** A complex number representing "0.0 + 0.0i" */
  public static final ApcomplexNum ZERO = new ApcomplexNum(Apcomplex.ZERO);

  public static ApcomplexNum valueOf(final Apcomplex value) {
    return new ApcomplexNum(value);
  }

  public static ApcomplexNum valueOf(final Apfloat real) {
    return new ApcomplexNum(real);
  }

  public static ApcomplexNum valueOf(final Apfloat real, final Apfloat imag) {
    return new ApcomplexNum(real, imag);
  }

  /**
   * A complex number with the absolute error <code>10^-accuracy10</code>: each part keeps the
   * digits it is known to, a part below the error is <code>0</code>.
   *
   * @param value the value
   * @param accuracy10 the decimal accuracy, <code>Infinity</code> for the value of an exact number,
   *        <code>NaN</code> for a number without a precision of its own
   */
  public static ApcomplexNum valueOf(final Apcomplex value, final double accuracy10) {
    if (Double.isNaN(accuracy10)) {
      return new ApcomplexNum(value);
    }
    if (Double.isInfinite(accuracy10) && accuracy10 > 0) {
      return exact(value);
    }
    final ApcomplexNum result = new ApcomplexNum(
        new Apcomplex(knownPart(value.real(), accuracy10), knownPart(value.imag(), accuracy10)));
    result.fSignificance = TRACKED;
    result.fAccuracy = accuracy10;
    return result;
  }

  /** The digits of a part which are known to the accuracy, or <code>0</code> if none is. */
  private static Apfloat knownPart(final Apfloat part, final double accuracy10) {
    if (part.signum() == 0) {
      return Apfloat.ZERO;
    }
    final long digits = Math.round(accuracy10 + ApfloatSignificance.log10Abs(part));
    return digits < 1L ? Apfloat.ZERO : part.precision(digits);
  }

  /** The value of an exact or a machine number in a numeric calculation: it adds no error. */
  public static ApcomplexNum exact(final Apcomplex value) {
    final ApcomplexNum result = new ApcomplexNum(value);
    result.fSignificance = EXACT;
    return result;
  }

  /** A real arbitrary-precision number as a complex number, with its error. */
  public static ApcomplexNum valueOf(final ApfloatNum real) {
    if (real.isSignificanceTracked()) {
      return valueOf(new Apcomplex(real.extendedValue()), real.significanceAccuracy());
    }
    if (real.isExactValue() || real.isMachineValue()) {
      return exact(new Apcomplex(real.apfloatValue()));
    }
    return new ApcomplexNum(real.apfloatValue());
  }

  /**
   * A complex number of a real and an imaginary part: the errors of the parts add in quadrature,
   * an exact part has none.
   */
  public static ApcomplexNum valueOf(final IReal re, final IReal im) {
    final Apcomplex value = new Apcomplex(re.apfloatValue(), im.apfloatValue());
    if (!isTracked(re) && !isTracked(im)) {
      return new ApcomplexNum(value);
    }
    return valueOf(value,
        0.5 * ApfloatSignificance.sumAccuracy(2.0 * errorAccuracy(re), 2.0 * errorAccuracy(im)));
  }

  /** This value as the value of an exact or a machine number, see {@link #EXACT}. */
  public ApcomplexNum asExact() {
    return fSignificance == EXACT ? this : exact(fApcomplex);
  }

  /** Test if this number has an error of its own, see {@link #TRACKED}. */
  public boolean isSignificanceTracked() {
    return fSignificance == TRACKED;
  }

  /**
   * The decimal accuracy: <code>Infinity</code> for the value of an exact number; for a number
   * without a precision of its own the one of its digits.
   */
  public double accuracy10() {
    switch (fSignificance) {
      case TRACKED:
        return fAccuracy;
      case EXACT:
        return Double.POSITIVE_INFINITY;
      default:
        final long precision = fApcomplex.precision();
        if (precision == Apcomplex.INFINITE || isZeroValue(fApcomplex)) {
          return Double.POSITIVE_INFINITY;
        }
        return precision - log10Abs(fApcomplex);
    }
  }

  /**
   * The decimal precision, a real number: the accuracy plus <code>Log10(Abs(z))</code>,
   * <code>0</code> for a zero.
   */
  public double precision10() {
    switch (fSignificance) {
      case TRACKED:
        return isZeroValue(fApcomplex) ? 0.0 : fAccuracy + log10Abs(fApcomplex);
      case EXACT:
        return Double.POSITIVE_INFINITY;
      default:
        final long precision = fApcomplex.precision();
        return precision == Apcomplex.INFINITE ? Double.POSITIVE_INFINITY : precision;
    }
  }

  private static boolean isZeroValue(final Apcomplex z) {
    return z.real().signum() == 0 && z.imag().signum() == 0;
  }

  /** <code>Log10(Abs(z))</code> of a nonzero number. */
  private static double log10Abs(final Apcomplex z) {
    if (z.imag().signum() == 0) {
      return ApfloatSignificance.log10Abs(z.real());
    }
    if (z.real().signum() == 0) {
      return ApfloatSignificance.log10Abs(z.imag());
    }
    final double re = ApfloatSignificance.log10Abs(z.real());
    final double im = ApfloatSignificance.log10Abs(z.imag());
    final double larger = Math.max(re, im);
    // Log10(Sqrt(re^2+im^2)) relative to the larger part, also beyond the double range
    return larger + 0.5 * Math.log10(
        Math.pow(10.0, 2.0 * (re - larger)) + Math.pow(10.0, 2.0 * (im - larger)));
  }

  /** Test if <code>operand</code> has an error of its own. */
  private static boolean isTracked(final Object operand) {
    if (operand instanceof ApcomplexNum) {
      return ((ApcomplexNum) operand).isSignificanceTracked();
    }
    return operand instanceof ApfloatNum && ((ApfloatNum) operand).isSignificanceTracked();
  }

  /**
   * The accuracy of an operand: exact numbers and machine numbers, which are taken as the binary
   * numbers they are, add no error.
   */
  private static double errorAccuracy(final Object operand) {
    if (operand instanceof ApcomplexNum) {
      return ((ApcomplexNum) operand).accuracy10();
    }
    if (operand instanceof ApfloatNum) {
      return ((ApfloatNum) operand).significanceAccuracy();
    }
    return Double.POSITIVE_INFINITY;
  }

  /** The precision of an operand, see {@link #errorAccuracy(Object)}. */
  private static double errorPrecision(final Object operand) {
    if (operand instanceof ApcomplexNum) {
      return ((ApcomplexNum) operand).precision10();
    }
    if (operand instanceof ApfloatNum) {
      return ((ApfloatNum) operand).significancePrecision();
    }
    return Double.POSITIVE_INFINITY;
  }

  /** The value of an operand, or <code>null</code>. */
  private static Apcomplex operandValue(final Object operand) {
    if (operand instanceof INumber) {
      return ((INumber) operand).apcomplexValue();
    }
    if (operand instanceof Apcomplex) {
      return (Apcomplex) operand;
    }
    if (operand instanceof Double) {
      return new Apcomplex(new Apfloat(((Double) operand).doubleValue()));
    }
    return null;
  }

  /**
   * The sum <code>value</code> of this number and <code>operand</code>: the absolute errors add;
   * of a real and an imaginary number, which are errors in two directions, in quadrature.
   */
  private ApcomplexNum sumResult(final Apcomplex value, final Object operand) {
    if (!isTracked(this) && !isTracked(operand)) {
      return new ApcomplexNum(value);
    }
    final double a1 = errorAccuracy(this);
    final double a2 = errorAccuracy(operand);
    final Apcomplex that = operandValue(operand);
    final boolean orthogonal = that != null
        && ((isReal(fApcomplex) && isImaginary(that)) || (isImaginary(fApcomplex) && isReal(that)));
    return valueOf(value, orthogonal
        ? 0.5 * ApfloatSignificance.sumAccuracy(2.0 * a1, 2.0 * a2)
        : ApfloatSignificance.sumAccuracy(a1, a2));
  }

  private static boolean isReal(final Apcomplex z) {
    return z.imag().signum() == 0;
  }

  private static boolean isImaginary(final Apcomplex z) {
    return z.real().signum() == 0 && z.imag().signum() != 0;
  }

  /**
   * The product or quotient <code>value</code> of this number and <code>operand</code>: the
   * relative errors add.
   */
  private ApcomplexNum productResult(final Apcomplex value, final Object operand) {
    if (!isTracked(this) && !isTracked(operand)) {
      return new ApcomplexNum(value);
    }
    if (isZeroValue(value)) {
      // a zero factor: its absolute error times the other factor
      final Apcomplex that = operandValue(operand);
      if (isZeroValue(fApcomplex) && that != null && !isZeroValue(that)) {
        return valueOf(value, errorAccuracy(this) - log10Abs(that));
      }
      if (!isZeroValue(fApcomplex)) {
        return valueOf(value, errorAccuracy(operand) - log10Abs(fApcomplex));
      }
      return valueOf(value, errorAccuracy(this) + errorAccuracy(operand));
    }
    final double precision =
        ApfloatSignificance.productPrecision(errorPrecision(this), errorPrecision(operand));
    return valueOf(value, precision - log10Abs(value));
  }

  /** <code>value</code> with the error of this number: a sign flip, a conjugate. */
  private ApcomplexNum sameSignificance(final Apcomplex value) {
    final ApcomplexNum result = new ApcomplexNum(value);
    result.fSignificance = fSignificance;
    result.fAccuracy = fAccuracy;
    return result;
  }

  /**
   * <code>value</code> of the relative error of this number times <code>factor</code>: the power
   * <code>z^e</code> of an exact exponent has <code>Abs(e)</code> times the relative error of
   * <code>z</code>, <code>1/z</code> the relative error of <code>z</code>.
   */
  private ApcomplexNum scaledRelativeError(final Apcomplex value, final double factor) {
    if (fSignificance != TRACKED) {
      return new ApcomplexNum(value);
    }
    if (isZeroValue(value) || isZeroValue(fApcomplex)) {
      return new ApcomplexNum(value);
    }
    final double precision = precision10() - Math.log10(Math.abs(factor));
    return valueOf(value, precision - log10Abs(value));
  }

  /**
   * A part of this number as a real number, known to the accuracy of the number: a part below the
   * error is a zero of that accuracy.
   */
  private IReal knownRealPart(final Apfloat part) {
    if (fSignificance == EXACT) {
      return ApfloatNum.exact(part);
    }
    if (fSignificance != TRACKED) {
      return F.num(part);
    }
    if (part.signum() == 0) {
      return ApfloatNum.zero(fAccuracy);
    }
    final double precision = fAccuracy + ApfloatSignificance.log10Abs(part);
    return precision <= 0.0 ? ApfloatNum.zero(fAccuracy) : ApfloatNum.valueOf(part, precision);
  }

  /**
   * Create a <code>ApcomplexNum</code> complex number from the real and imaginary <code>BigInteger
   * </code> parts.
   *
   * @param realNumerator the real numbers numerator part
   * @param realDenominator the real numbers denominator part
   * @param imagNumerator the imaginary numbers numerator part
   * @param imagDenominator the imaginary numbers denominator part
   * @return a new <code>ApcomplexNum</code> complex number object
   */
  public static ApcomplexNum valueOf(final BigInteger realNumerator,
      final BigInteger realDenominator, final BigInteger imagNumerator,
      final BigInteger imagDenominator) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    long prec = h.precision();
    Apfloat real = h.divide(ApfloatNum.apfloatOf(realNumerator, prec),
        ApfloatNum.apfloatOf(realDenominator, prec));
    Apfloat imag = h.divide(ApfloatNum.apfloatOf(imagNumerator, prec),
        ApfloatNum.apfloatOf(imagDenominator, prec));
    return new ApcomplexNum(real, imag);
  }

  public static ApcomplexNum valueOf(final Complex c, long precision) {
    return valueOf(new Apcomplex(new Apfloat(new BigDecimal(c.getReal()), precision),
        new Apfloat(new BigDecimal(c.getImaginary()), precision)));
  }

  /** A complex number representing "NaN + NaNi" */
  // public static final ApfloatComplexNum NaN = valueOf(Double.NaN,
  // Double.NaN);


  /**
   * An {@link Apfloat} for a machine double, refusing one that cannot be represented.
   *
   * <p>
   * Apfloat has no representation for an infinity or a NaN and answers a
   * {@link NumberFormatException} for either, which none of the arithmetic below expects: it
   * escapes the built-in that asked for the operation instead of being reported. A Symja exception
   * is turned into a message and an unevaluated result by
   * {@code EvalEngine#evalASTBuiltinFunction}. This mirrors {@code DMath#apfloatOf(double)}.
   *
   * @param value the machine double to convert
   * @return the value as an arbitrary precision number
   */
  private static Apfloat apfloatOf(final double value) {
    if (!Double.isFinite(value)) {
      throw new SymjaMathException(
          "cannot convert " + value + " into an arbitrary precision number");
    }
    return new Apfloat(value);
  }

  public static ApcomplexNum valueOf(final double real) {
    return valueOf(EvalEngine.getApfloat().valueOf(new Apcomplex(apfloatOf(real))));
  }

  public static ApcomplexNum valueOf(final double real, final double imaginary) {
    return valueOf(
        EvalEngine.getApfloat().valueOf(new Apcomplex(apfloatOf(real), apfloatOf(imaginary))));
  }


  public static ApcomplexNum valueOf(final String realPart, final String imaginaryPart,
      long precision) {
    return new ApcomplexNum(realPart, imaginaryPart, precision);
  }

  Apcomplex fApcomplex;

  /** The precision is the one of {@link #fApcomplex}, as before significance arithmetic. */
  private static final byte UNTRACKED = 0;

  /**
   * The number has the absolute error <code>10^-fAccuracy</code>: a complex number of numbers of a
   * precision of their own, or a result of significance arithmetic. Both parts are known to the
   * accuracy {@link #fAccuracy}, so no part shows more digits than it is known to.
   */
  private static final byte TRACKED = 1;

  /** The value of an exact number, or of a machine number, in a numeric calculation: no error. */
  private static final byte EXACT = 2;

  /**
   * What {@link #fAccuracy} means, see {@link #UNTRACKED}, {@link #TRACKED} and {@link #EXACT}. The
   * default <code>0</code> keeps numbers serialized before significance arithmetic as they were.
   */
  private byte fSignificance = UNTRACKED;

  /** The decimal accuracy of a {@link #TRACKED} number. */
  private double fAccuracy;

  /**
   * The helper for calculations with this number: the engine's while a precision is set, else one
   * of this number's precision - see {@link EvalEngine#getApfloat(long)}.
   */
  private FixedPrecisionApfloatHelper h() {
    if (fApcomplex == null) {
      // while the number is constructed
      return EvalEngine.getApfloat();
    }
    return EvalEngine.getApfloat(fApcomplex.precision());
  }

  private ApcomplexNum(Apcomplex complex) {
    fApcomplex = complex;
  }

  private ApcomplexNum(Apfloat real) {
    fApcomplex = new Apcomplex(real);
  }

  private ApcomplexNum(Apfloat real, Apfloat imag) {
    fApcomplex = new Apcomplex(real, imag);
  }

  private ApcomplexNum(final String realPart, String imaginaryPart, long precision) {
    fApcomplex =
        new Apcomplex(new Apfloat(realPart, precision), new Apfloat(imaginaryPart, precision));
  }

  @Override
  public INumber abs() {
    final Apfloat abs = h().abs(fApcomplex);
    if (fSignificance == TRACKED) {
      // the absolute value has the error of the number
      return abs.signum() == 0 ? ApfloatNum.zero(fAccuracy)
          : ApfloatNum.valueOf(abs, fAccuracy + ApfloatSignificance.log10Abs(abs));
    }
    return ApfloatNum.valueOf(abs);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr accept(IVisitor visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public boolean accept(IVisitorBoolean visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public int accept(IVisitorInt visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public long accept(IVisitorLong visitor) {
    return visitor.visit(this);
  }

  @Override
  public ApcomplexNum acos() {
    return valueOf(h().acos(fApcomplex));
  }

  @Override
  public ApcomplexNum acosh() {
    return valueOf(h().acosh(fApcomplex));
  }

  /**
   * The sum with a number; the digits which cancel are reported to {@link PrecisionLoss}.
   *
   * <p>
   * The four operations are done with the real and the imaginary parts, each with the digits of
   * the precision. The complex operations of the helper give both parts of a number one accuracy
   * and cut the smaller part off at it: <code>Exp(1000)+I</code> had no imaginary part.
   */
  private Apcomplex sum(Apcomplex that) {
    final FixedPrecisionApfloatHelper h = h();
    return PrecisionLoss.sum(fApcomplex, that, new Apcomplex(
        h.add(fApcomplex.real(), that.real()), h.add(fApcomplex.imag(), that.imag())));
  }

  /** The difference to a number; the digits which cancel are reported. */
  private Apcomplex difference(Apcomplex that) {
    final FixedPrecisionApfloatHelper h = h();
    return PrecisionLoss.sum(fApcomplex, that, new Apcomplex(
        h.subtract(fApcomplex.real(), that.real()), h.subtract(fApcomplex.imag(), that.imag())));
  }

  /**
   * A number with the precision for each of its parts. <code>Apcomplex.precision()</code> gives
   * the two parts one accuracy, see {@link #sum(Apcomplex)}.
   */
  public static Apcomplex precisionOfParts(Apcomplex z, long precision) {
    final Apfloat re = z.real();
    final Apfloat im = z.imag();
    return new Apcomplex(re.signum() == 0 ? re : re.precision(precision),
        im.signum() == 0 ? im : im.precision(precision));
  }

  /** The product with a number, by parts - see {@link #sum(Apcomplex)}. */
  private Apcomplex product(Apcomplex that) {
    final FixedPrecisionApfloatHelper h = h();
    final Apfloat a = fApcomplex.real();
    final Apfloat b = fApcomplex.imag();
    final Apfloat c = that.real();
    final Apfloat d = that.imag();
    if (d.signum() == 0) {
      return new Apcomplex(h.multiply(a, c), h.multiply(b, c));
    }
    if (b.signum() == 0) {
      return new Apcomplex(h.multiply(a, c), h.multiply(a, d));
    }
    return new Apcomplex(h.subtract(h.multiply(a, c), h.multiply(b, d)),
        h.add(h.multiply(a, d), h.multiply(b, c)));
  }

  /** The quotient by a number, by parts - see {@link #sum(Apcomplex)}. */
  private Apcomplex quotient(Apcomplex that) {
    final FixedPrecisionApfloatHelper h = h();
    final Apfloat a = fApcomplex.real();
    final Apfloat b = fApcomplex.imag();
    final Apfloat c = that.real();
    final Apfloat d = that.imag();
    if (d.signum() == 0) {
      return new Apcomplex(h.divide(a, c), h.divide(b, c));
    }
    if (c.signum() == 0) {
      // (a+b*I)/(d*I) = b/d - a/d*I
      return new Apcomplex(h.divide(b, d), h.divide(a, d).negate());
    }
    final Apfloat norm = h.add(h.multiply(c, c), h.multiply(d, d));
    return new Apcomplex(h.divide(h.add(h.multiply(a, c), h.multiply(b, d)), norm),
        h.divide(h.subtract(h.multiply(b, c), h.multiply(a, d)), norm));
  }

  public ApcomplexNum add(final ApcomplexNum that) {
    return sumResult(sum(that.fApcomplex), that);
  }

  @Override
  public ApcomplexNum add(double value) {
    // add, not divide: this was a copy of divide(double) below, so (6+8i).add(2.0) answered 3+4i
    // instead of 8+8i. IExpr#add(double) is defined as plus(F.num(that)).
    return sumResult(sum(new Apcomplex(apfloatOf(value))), Double.valueOf(value));
  }

  @Override
  public IComplexNum add(final IComplexNum val) {
    return sumResult(sum(val.apcomplexValue()), val);
  }

  @Override
  public IExpr agm(IExpr arg2) {
    if (arg2 instanceof INumber) {
      return valueOf(h().agm(fApcomplex, ((INumber) arg2).apcomplexValue()));
    }
    return IComplexNum.super.agm(arg2);
  }

  @Override
  public IExpr airyAi() {
    try {
      return valueOf(h().airyAi(fApcomplex));
    } catch (LossOfPrecisionException lope) {
      // no digit of the phase of a large argument is left
      return F.NIL;
    }
  }

  @Override
  public IExpr airyAiPrime() {
    try {
      return valueOf(h().airyAiPrime(fApcomplex));
    } catch (LossOfPrecisionException lope) {
      // no digit of the phase of a large argument is left
      return F.NIL;
    }
  }

  @Override
  public IExpr airyBi() {
    try {
      return valueOf(h().airyBi(fApcomplex));
    } catch (LossOfPrecisionException lope) {
      // no digit of the phase of a large argument is left
      return F.NIL;
    }
  }

  @Override
  public IExpr airyBiPrime() {
    try {
      return valueOf(h().airyBiPrime(fApcomplex));
    } catch (LossOfPrecisionException lope) {
      // no digit of the phase of a large argument is left
      return F.NIL;
    }
  }

  @Override
  public IExpr angerJ(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            h().angerJ(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.angerJ(arg2);
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return this;
  }

  @Override
  public Apcomplex apcomplexValue() {
    return fApcomplex;
  }

  @Override
  public ApcomplexNum asin() {
    return valueOf(h().asin(fApcomplex));
  }

  @Override
  public ApcomplexNum asinh() {
    return valueOf(h().asinh(fApcomplex));
  }

  @Override
  public ApcomplexNum atan() {
    return valueOf(h().atan(fApcomplex));
  }

  @Override
  public IExpr atan2(IExpr value) {
    try {
      if (value instanceof ApcomplexNum) {
        Apcomplex th = fApcomplex;
        Apcomplex x = ((ApcomplexNum) value).fApcomplex;

        // compute r = sqrt(x^2+y^2)
        FixedPrecisionApfloatHelper h = h();
        final Apcomplex r = h.sqrt(h.add(h.multiply(x, x), h.multiply(th, th)));

        if (x.real().compareTo(Apfloat.ZERO) >= 0) {
          // compute atan2(y, x) = 2 atan(y / (r + x))
          return valueOf(h.multiply(h.atan(h.divide(th, h.add(r, x))), new Apfloat(2)));
        } else {
          // compute atan2(y, x) = +/- pi - 2 atan(y / (r - x))
          return valueOf(
              h.add(h.multiply(h.atan(h.divide(th, h.subtract(r, x))), new Apfloat(-2)), h.pi()));
        }
      }
      return IComplexNum.super.atan2(value);
    } catch (ArithmeticException aex) {
      // Indeterminate expression `1` encountered.
      Errors.printMessage(S.ArcTan, "indet", F.list(F.ArcTan(value, this)), EvalEngine.get());
      return S.Indeterminate;
    }
  }

  @Override
  public ApcomplexNum atanh() {
    return valueOf(h().atanh(fApcomplex));
  }

  // public static Apcomplex fresnelC(Apcomplex z, FixedPrecisionApfloatHelper apfloat) {
  // // Complex m1 = HypergeometricJS.hypergeometric1F1(new Complex(0.5), new Complex(1.5),
  // // new Complex(0, Math.PI / 2).multiply(x.multiply(x)));
  // // Complex m2 = HypergeometricJS.hypergeometric1F1(new Complex(0.5), new Complex(1.5),
  // // new Complex(0, -Math.PI / 2).multiply(x.multiply(x)));
  // //
  // // return mul(0.5, x, m1.add(m2));
  // // z*z
  // Apcomplex sqr = apfloat.multiply(z, z);
  // // Math.PI / 2
  // Apfloat piHalf = apfloat.divide(apfloat.pi(), new Apint(2));
  // // 1/2
  // Aprational oneHalf = new Aprational(Apint.ONE, new Apint(2));
  // // 3/2
  // Aprational threeHalf = new Aprational(new Apint(3), new Apint(2));
  // Apcomplex m1 = apfloat.hypergeometric1F1(oneHalf, threeHalf,
  // new Apcomplex(Apfloat.ZERO, piHalf).multiply(sqr));
  // Apcomplex m2 = apfloat.hypergeometric1F1(oneHalf, threeHalf,
  // new Apcomplex(Apfloat.ZERO, piHalf.negate()).multiply(sqr));
  //
  // return apfloat.multiply(oneHalf, z).multiply(apfloat.add(m1, m2));
  // }

  @Override
  public IExpr besselI(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        if (!arg2.isZero()) {
          return ApfloatNum.guardedBesselFirstKind(apcomplexValue(),
              ((INumber) arg2).apcomplexValue(), h().precision(), true);
        }
        Apcomplex besselI =
            h().besselI(apcomplexValue(), ((INumber) arg2).apcomplexValue());
        return F.complexNum(besselI);
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.BesselI, are);
      }
    }
    return IComplexNum.super.besselI(arg2);
  }

  @Override
  public IExpr besselJ(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        if (!arg2.isZero()) {
          return ApfloatNum.guardedBesselFirstKind(apcomplexValue(),
              ((INumber) arg2).apcomplexValue(), h().precision(), false);
        }
        Apcomplex besselJ =
            h().besselJ(apcomplexValue(), ((INumber) arg2).apcomplexValue());
        return F.complexNum(besselJ);
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.BesselJ, are);
      }
    }
    return IComplexNum.super.besselJ(arg2);
  }

  @Override
  public IExpr besselK(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return ApfloatNum.guardedBessel(apcomplexValue(), ((INumber) arg2).apcomplexValue(),
            h().precision(), false);
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.BesselK, are);
      }
    }
    return IComplexNum.super.besselK(arg2);
  }

  @Override
  public IExpr besselY(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return ApfloatNum.guardedBessel(apcomplexValue(), ((INumber) arg2).apcomplexValue(),
            h().precision(), true);
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.BesselY, are);
      }
    }
    return IComplexNum.super.besselY(arg2);
  }

  // public static Apcomplex fresnelS(Apcomplex z, FixedPrecisionApfloatHelper apfloat) {
  // // Complex m1 = HypergeometricJS.hypergeometric1F1(new Complex(0.5), new Complex(1.5),
  // // new Complex(0, Math.PI / 2).multiply(x.multiply(x)));
  // // Complex m2 = HypergeometricJS.hypergeometric1F1(new Complex(0.5), new Complex(1.5),
  // // new Complex(0, -Math.PI / 2).multiply(x.multiply(x)));
  // //
  // // return mul(new Complex(0, -0.5), x, sub(m1, m2));
  //
  // // z*z
  // Apcomplex sqr = apfloat.multiply(z, z);
  // // Math.PI / 2
  // Apfloat piHalf = apfloat.divide(apfloat.pi(), new Apint(2));
  // // 1/2
  // Aprational oneHalf = new Aprational(Apint.ONE, new Apint(2));
  // // -1/2
  // Aprational minusOneHalf = new Aprational(new Apint(-1), new Apint(2));
  // // 3/2
  // Aprational threeHalf = new Aprational(new Apint(3), new Apint(2));
  // Apcomplex m1 = apfloat.hypergeometric1F1(oneHalf, threeHalf,
  // new Apcomplex(Apfloat.ZERO, piHalf).multiply(sqr));
  // Apcomplex m2 = apfloat.hypergeometric1F1(oneHalf, threeHalf,
  // new Apcomplex(Apfloat.ZERO, piHalf.negate()).multiply(sqr));
  //
  // return apfloat.multiply(new Apcomplex(Apfloat.ZERO, minusOneHalf), z)
  // .multiply(apfloat.subtract(m1, m2));
  // }

  @Override
  public IExpr beta(IExpr b) {
    if (b instanceof INumber) {
      try {
        return valueOf(h().beta(fApcomplex, ((INumber) b).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // java.lang.ArithmeticException: Beta is infinite
      }
    }
    return IComplexNum.super.beta(b);
  }

  @Override
  public IExpr beta(IExpr a, IExpr b) {
    if (a instanceof INumber && b instanceof INumber) {
      try {
        return valueOf(h().beta(fApcomplex, ((INumber) a).apcomplexValue(),
            ((INumber) b).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException aex) {
        if (Num.isDivisionByZeroAny(aex)) {
          return F.ComplexInfinity;
        }
      }
    }
    return IComplexNum.super.beta(a, b);
  }

  @Override
  public IExpr beta(IExpr x2, IExpr a, IExpr b) {
    if (x2 instanceof INumber && a instanceof INumber && b instanceof INumber) {
      try {
        return valueOf(h().beta(fApcomplex, ((INumber) x2).apcomplexValue(),
            ((INumber) a).apcomplexValue(), ((INumber) b).apcomplexValue()));
      } catch (NumericComputationException e) {
        //
      } catch (ArithmeticException aex) {
        if (Num.isDivisionByZeroAny(aex)) {
          return F.ComplexInfinity;
        }
      }
    }
    return IComplexNum.super.beta(x2, a, b);
  }

  @Override
  public ApcomplexNum cbrt() {
    return valueOf(h().cbrt(fApcomplex));
  }

  @Override
  public ApcomplexNum ceil() {
    FixedPrecisionApfloatHelper h = h();
    return valueOf(h.ceil(fApcomplex.real()), //
        h.ceil(fApcomplex.imag()));
  }

  @Override
  public INumber ceilFraction() throws ArithmeticException {
    return F.CC(F.ZZ(ApfloatMath.ceil(fApcomplex.real()).toBigInteger()),
        F.ZZ(ApfloatMath.ceil(fApcomplex.imag()).toBigInteger()));
  }

  @Override
  public IExpr chebyshevT(IExpr arg2) {
    if (arg2 instanceof INumber) {
      // if (arg2 instanceof IReal) {
      // try {
      // Apcomplex chebyshevT =
      // h().chebyshevT(apcomplexValue(), ((IReal) arg2).apfloatValue());
      // return F.complexNum(chebyshevT);
      // } catch (NumericComputationException are) {
      //
      // }
      // }
      try {
        Apcomplex chebyshevT =
            h().chebyshevT(apcomplexValue(), ((INumber) arg2).apcomplexValue());
        return F.complexNum(chebyshevT);
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.ChebyshevT, are);
      }
    }
    return IComplexNum.super.chebyshevT(arg2);
  }

  @Override
  public IExpr chebyshevU(IExpr arg2) {
    if (arg2 instanceof INumber) {
      // if (arg2 instanceof IReal) {
      // Apcomplex chebyshevU =
      // h().chebyshevU(apcomplexValue(), ((IReal) arg2).apfloatValue());
      // return F.complexNum(chebyshevU);
      // }
      try {
        Apcomplex chebyshevU =
            h().chebyshevU(apcomplexValue(), ((INumber) arg2).apcomplexValue());
        return F.complexNum(chebyshevU);
      } catch (ApfloatArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.ChebyshevU, are);
      }
    }
    return IComplexNum.super.chebyshevU(arg2);
  }

  /** {@inheritDoc} */
  @Override
  public int compareAbsValueToOne() {
    double temp = dabs();
    return Double.compare(temp, 1.0);
  }

  public int compareTo(final Apcomplex that) {
    // https://github.com/mtommila/apfloat/issues/27
    int result = fApcomplex.real().compareTo(that.real());
    if (result == 0) {
      result = ApfloatMath.abs(fApcomplex.imag()).compareTo(ApfloatMath.abs(that.imag()));
    }
    if (result == 0) {
      result = Integer.compare(fApcomplex.imag().signum(), that.imag().signum());
    }
    return result;
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr.isNumber()) {
      if (expr instanceof ApcomplexNum) {
        return compareTo(((ApcomplexNum) expr).fApcomplex);
      }
      try {
        return compareTo(((INumber) expr).apcomplexValue());
      } catch (NumberFormatException | SymjaMathException ex) {
        // see ApfloatNum#compareTo(IExpr): a non-finite value cannot be converted, and a throw
        // during Orderless sorting escapes the evaluation instead of the built-in. The real part
        // decides, then the sign of this imaginary part, which mirrors what Num#compareTo answers
        // for the same pair in the other direction.
        int c = Double.compare(reDoubleValue(), ((INumber) expr).reDoubleValue());
        if (c != 0) {
          return c;
        }
        double imaginary = imDoubleValue();
        return imaginary > 0.0 ? 1 : imaginary < 0.0 ? -1 : 0;
      }
    }
    return IExpr.compareHierarchy(this, expr);
  }

  @Override
  public IExpr complexArg() {
    try {
      return F.num(h().arg(fApcomplex));
    } catch (ArithmeticException | NumericComputationException ex) {
      // Indeterminate expression `1` encountered.
      Errors.printMessage(S.Arg, "indet", F.list(F.Arg(this)), EvalEngine.get());
      return S.Indeterminate;
    }
  }

  @Override
  public ComplexNum complexNumValue() {
    return ComplexNum.valueOf(fApcomplex.real().doubleValue(), fApcomplex.imag().doubleValue());
  }

  @Override
  public int complexSign() {
    final int signum = fApcomplex.real().signum();
    return (signum == 0) ? fApcomplex.imag().signum() : signum;
  }

  /** @return */
  @Override
  public IComplexNum conjugate() {
    return sameSignificance(h().conj(fApcomplex));
  }

  @Override
  public IExpr copy() {
    return this;
  }

  @Override
  public IExpr copySign(double d) {
    FixedPrecisionApfloatHelper h = h();
    Apfloat sign = apfloatOf(d);
    return valueOf(h.copySign(fApcomplex.real(), sign), //
        h.copySign(fApcomplex.imag(), sign));
  }

  @Override
  public ApcomplexNum cos() {
    PrecisionLoss.periodic(fApcomplex.real());
    return valueOf(h().cos(fApcomplex));
  }

  @Override
  public ApcomplexNum cosh() {
    PrecisionLoss.periodic(fApcomplex.imag());
    return valueOf(h().cosh(fApcomplex));
  }

  // private static Apcomplex erf(Apcomplex x, FixedPrecisionApfloatHelper h) {
  // Apint two = new Apint(2);
  // Aprational oneHalf = new Aprational(Apint.ONE, new Apint(2));
  // // 3/2
  // Aprational threeHalf = new Aprational(new Apint(3), new Apint(2));
  // Apcomplex erf = h.hypergeometric1F1(oneHalf, threeHalf, h.multiply(x,
  // x).negate()).multiply(two)
  // .multiply(x).divide(h.sqrt(h.pi()));
  // return erf;
  // }

  @Override
  public IExpr coshIntegral() {
    return valueOf(h().coshIntegral(fApcomplex));
  }

  @Override
  public IExpr cosIntegral() {
    return valueOf(h().cosIntegral(fApcomplex));
  }

  /** {@inheritDoc} */
  @Override
  public double dabs() {
    // if (isNaN()) {
    // return Double.NaN;
    // }
    //
    // if (isInfinite()) {
    // return Double.POSITIVE_INFINITY;
    // }

    if (Math.abs(reDoubleValue()) < Math.abs(imDoubleValue())) {
      if (fApcomplex.imag().signum() == 0) {
        // if (imDoubleValue() == 0.0) {
        return Math.abs(reDoubleValue());
      }
      Apfloat d = h().divide(fApcomplex.real(), fApcomplex.imag());
      final double q = d.doubleValue();
      return (Math.abs(imDoubleValue()) * Math.sqrt(1 + q * q));
    } else {
      if (fApcomplex.real().signum() == 0) {
        // if (reDoubleValue() == 0.0) {
        return Math.abs(imDoubleValue());
      }
      Apfloat d = h().divide(fApcomplex.imag(), fApcomplex.real());
      final double q = d.doubleValue();
      return (Math.abs(reDoubleValue()) * Math.sqrt(1 + q * q));
    }

  }

  @Override
  public ApcomplexNum dawsonF() {
    return valueOf(h().dawsonF(fApcomplex));
  }

  /** {@inheritDoc} */
  @Override
  public IExpr dec() {
    return add(MINUS_ONE);
  }

  /** {@inheritDoc} */
  @Override
  public long determinePrecision(boolean postParserProcessing) {
    return precision();
  }

  @Override
  public IExpr digamma() {
    try {
      return valueOf(h().digamma(fApcomplex));
    } catch (ArithmeticException | NumericComputationException aex) {
    }
    return IComplexNum.super.digamma();
  }

  public ApcomplexNum divide(final ApcomplexNum that) throws ArithmeticException {
    return productResult(quotient(that.fApcomplex), that);
  }

  @Override
  public ApcomplexNum divide(double value) {
    return productResult(quotient(apfloatOf(value)), Double.valueOf(value));
  }

  @Override
  public IComplexNum divide(final IComplexNum val) {
    return productResult(quotient(val.apcomplexValue()), val);
  }

  @Override
  public IExpr divide(final IExpr that) {
    if (that.isZero()) {
      // Infinite expression `1` encountered.
      Errors.printMessage(S.Power, "infy", F.list(F.Divide(this, that)), EvalEngine.get());
      return F.CComplexInfinity;
    }
    if (that instanceof IComplexNum || that instanceof INum) {
      return productResult(quotient(((INumber) that).apcomplexValue()), that);
    }
    return IComplexNum.super.divide(that);
  }

  @Override
  public IExpr ellipticE() {
    return valueOf(h().ellipticE(fApcomplex));
  }

  @Override
  public IExpr ellipticK() {
    return valueOf(ApcomplexEllipticMath.ellipticK(fApcomplex));
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ApcomplexNum) {
      return fApcomplex.equals(((ApcomplexNum) obj).fApcomplex);
    }
    return false;
  }

  @Override
  public boolean equalsInt(int i) {
    return false;
  }

  @Override
  public IExpr erf() {
    return valueOf(h().erf(fApcomplex));
    // FixedPrecisionApfloatHelper h = h();
    // try {
    // Apcomplex erf = erf(fApcomplex, h);
    // return F.complexNum(erf);
    // } catch (Exception ce) {
    // //
    // }
    // return F.NIL;
  }

  @Override
  public IExpr erfc() {
    try {
      return valueOf(h().erfc(fApcomplex));
    } catch (OverflowException of) {
      // return Underflow? https://github.com/mtommila/apfloat/issues/38
      return F.Overflow();
    } catch (ArithmeticException | NumericComputationException e) {
      e.printStackTrace();
    }
    return IComplexNum.super.erfc();
  }

  @Override
  public IExpr erfi() {
    return valueOf(h().erfi(fApcomplex));
  }

  @Override
  public INumber evalNumber() {
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(EvalEngine engine) {
    if (fSignificance == TRACKED) {
      // lowered to the precision N(expr, p) asked for only, see ApfloatNum#evaluate(EvalEngine)
      final long requested = engine.getRequestedPrecision();
      if (requested > 0 && engine.isNumericMode() && !isZeroValue(fApcomplex)
          && precision10() > requested) {
        return valueOf(fApcomplex, requested - log10Abs(fApcomplex));
      }
      if (fApcomplex.imag().signum() == 0) {
        return knownRealPart(fApcomplex.real());
      }
      return F.NIL;
    }
    if (fSignificance == EXACT && fApcomplex.imag().signum() == 0) {
      return ApfloatNum.exact(fApcomplex.real());
    }
    final long precision = fApcomplex.precision();
    if (precision != Apcomplex.INFINITE && engine.getNumericPrecision() < precision
        && engine.isNumericMode()) {
      // Only when the conversion actually lowers the precision. It does not always reach the
      // engine's: converting a 30 digit value here answers one of 255 digits, so the test above
      // stays true for ever and evalLoop() is told the expression changed on every pass. That is
      // an endless loop rather than a wrong answer - UnitStep(-0.8`30 + 1.2`30*I) never returned.
      ApcomplexNum reduced = valueOf(h().valueOf(fApcomplex));
      if (reduced.fApcomplex.precision() < precision) {
        return reduced;
      }
    }
    if (fApcomplex.imag().signum() == 0) {
      return ApfloatNum.valueOf(fApcomplex.real());
    }
    return F.NIL;
  }

  @Override
  public ApcomplexNum exp() {
    PrecisionLoss.periodic(fApcomplex.imag());
    return valueOf(h().exp(fApcomplex));
  }

  @Override
  public IExpr expIntegralE(IExpr z) {
    if (z instanceof INumber) {
      try {
        return valueOf(
            h().expIntegralE(fApcomplex, ((INumber) z).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        return Errors.printMessage(S.ExpIntegralE, e);
      }
    }
    return IComplexNum.super.expIntegralE(z);
  }

  @Override
  public IExpr expIntegralEi() {
    try {
      return valueOf(h().expIntegralEi(fApcomplex));
    } catch (ArithmeticException | NumericComputationException e) {
      return Errors.printMessage(S.ExpIntegralEi, e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public ApcomplexNum expm1() {
    FixedPrecisionApfloatHelper h = h();
    return valueOf(h.subtract(h.exp(fApcomplex), Apfloat.ONE));
  }

  @Override
  public IExpr factorial() {
    try {
      FixedPrecisionApfloatHelper h = h();
      return valueOf(h.gamma(h.add(fApcomplex, Apfloat.ONE)));
    } catch (ApfloatArithmeticException aaex) {
      if ("gamma.ofZero".equals(aaex.getLocalizationKey())) {
        return F.ComplexInfinity;
      }
      if ("gamma.ofNegativeInteger".equals(aaex.getLocalizationKey())) {
        return F.ComplexInfinity;
      }
    } catch (ArithmeticException | NumericComputationException e) {
      // try as computation with complex numbers
    }
    return IComplexNum.super.factorial();
  }

  @Override
  public IExpr fibonacci(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            h().fibonacci(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.fibonacci(arg2);
  }

  @Override
  public IExpr floor() {
    FixedPrecisionApfloatHelper h = h();
    return valueOf(h.floor(fApcomplex.real()), //
        h.floor(fApcomplex.imag()));
  }

  @Override
  public INumber floorFraction() throws ArithmeticException {
    return F.CC(F.ZZ(ApfloatMath.floor(fApcomplex.real()).toBigInteger()),
        F.ZZ(ApfloatMath.floor(fApcomplex.imag()).toBigInteger()));
  }

  /** {@inheritDoc} */
  @Override
  public INumber fractionalPart() {
    return F.complexNum(fApcomplex.real().frac(), fApcomplex.imag().frac());
  }

  @Override
  public IExpr fresnelC() {
    return valueOf(h().fresnelC(fApcomplex));
    // return valueOf(fresnelC(fApcomplex, h()));
  }

  @Override
  public IExpr fresnelS() {
    return valueOf(h().fresnelS(fApcomplex));
    // return valueOf(fresnelS(fApcomplex, h()));
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    StringBuilder buf = new StringBuilder();
    long precision = fApcomplex.precision();
    // the last digits rounded with the digits stored beyond the precision, not cut off
    Apcomplex rounded = ApfloatRounding.roundToPrecision(fApcomplex);
    String str = rounded.real().toString();
    if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      int indx = str.indexOf("e");
      if (indx > 0) {
        str = str.substring(0, indx) + "`" + precision + "*^" + str.substring(indx + 1);
      } else {
        str = str + "`" + precision;
      }
    }
    buf.append(str);
    buf.append(',');
    str = rounded.imag().toString();
    if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      int indx = str.indexOf("e");
      if (indx > 0) {
        str = str.substring(0, indx) + "``" + precision + "*^" + str.substring(indx + 1);
      } else {
        str = str + "`" + precision;
      }
    }
    buf.append(str);
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      buf.append(')');
    } else {
      buf.append(']');
    }
    return buf.toString();
  }

  @Override
  public IExpr gamma() {
    if (fApcomplex.isZero() || isMathematicalIntegerNegative()) {
      return F.CComplexInfinity;
    }
    try {
      // CPD-OFF numeric special-function dispatch per number type
      return valueOf(h().gamma(fApcomplex));
    } catch (OverflowException of) {
      return F.Overflow();
    } catch (ArithmeticException | NumericComputationException are) {
      return Errors.printMessage(S.Gamma, are, EvalEngine.get());
    }
    // return IComplexNum.super.gamma();
  }

  @Override
  public IExpr gamma(IExpr x) {
    if (isZero() && x.isZero()) {
      return F.CInfinity;
    }
    // if (x instanceof IReal) {
    // try {
    // return valueOf(h().gamma(fApcomplex, ((IReal) x).apfloatValue()));
    // } catch (ArithmeticException | NumericComputationException e) {
    // // try as computation with complex numbers
    // }
    // }
    if (x instanceof INumber) {
      try {
      // CPD-ON
        return F
            // CPD-OFF numeric special-function dispatch per number type
            .complexNum(h().gamma(fApcomplex, ((INumber) x).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException are) {
        // Upper gamma with first argument real part non-positive and second argment zero
        return Errors.printMessage(S.Gamma, are, EvalEngine.get());
      }
    }
    return IComplexNum.super.gamma(x);
  }

  @Override
  public IExpr gamma(IExpr x0, IExpr x1) {
    if (isZero()) {
      if (x0.isZero()) {
        return F.CComplexInfinity;
      }
      if (x1.isZero()) {
        return F.CNInfinity;
      }
    }
    // if (x0 instanceof IReal && x1 instanceof IReal) {
    // try {
    // return valueOf(h().gamma(fApcomplex, ((IReal) x0).apfloatValue(),
    // ((IReal) x1).apfloatValue()));
    // } catch (ArithmeticException | NumericComputationException e) {
    // // try as computation with complex numbers
    // }
    // }
    if (x0 instanceof INumber && x1 instanceof INumber) {
      try {
      // CPD-ON
        return F.complexNum(h().gamma(fApcomplex,
            ((INumber) x0).apcomplexValue(), ((INumber) x1).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException are) {
        return Errors.printMessage(S.Gamma, are, EvalEngine.get());
      }

    }
    return IComplexNum.super.gamma(x0, x1);
  }

  @Override
  public IExpr gegenbauerC(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            h().gegenbauerC(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.gegenbauerC(arg2);
  }

  @Override
  public IExpr gegenbauerC(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(h().gegenbauerC(fApcomplex,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.gegenbauerC(arg2, arg3);
  }

  @Override
  public IExpr getAddendum() {
    return valueOf(Apfloat.ZERO, fApcomplex.imag());
  }

  public Complex getCMComplex() {
    return new Complex(fApcomplex.real().doubleValue(), fApcomplex.imag().doubleValue());
  }

  public Apcomplex getComplex() {
    return fApcomplex;
  }

  /** @return */
  @Override
  public double getImaginaryPart() {
    double temp = fApcomplex.imag().doubleValue();
    if (temp == (-0.0)) {
      temp = 0.0;
    }
    return temp;
  }

  @Override
  public IExpr getPi() {
    return valueOf(h().pi());
  }

  @Override
  public double getReal() {
    return fApcomplex.real().doubleValue();
  }

  /** @return */
  @Override
  public double getRealPart() {
    double temp = fApcomplex.real().doubleValue();
    if (temp == (-0.0)) {
      temp = 0.0;
    }
    return temp;
  }

  @Override
  public IExpr harmonicNumber() {
    Apcomplex harmonicNumber = h().harmonicNumber(fApcomplex);
    return valueOf(harmonicNumber);
  }

  @Override
  public IExpr harmonicNumber(IExpr r) {
    if (r instanceof INumber) {
      try {
        FixedPrecisionApfloatHelper h = h();
        Apcomplex harmonicNumber = h.harmonicNumber(fApcomplex, ((INumber) r).apcomplexValue());
        return valueOf(harmonicNumber);
      } catch (ArithmeticException | NumericComputationException aex) {

      }
    }
    return IComplexNum.super.harmonicNumber(r);
  }

  @Override
  public final int hashCode() {
    return fApcomplex.hashCode();
  }

  @Override
  public ISymbol head() {
    return S.Complex;
  }

  @Override
  public IExpr hermiteH(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        Apcomplex hermiteH =
            h().hermiteH(apcomplexValue(), ((INumber) arg2).apcomplexValue());
        return F.complexNum(hermiteH);
      } catch (ArithmeticException | NumericComputationException are) {

      }
    }
    return IComplexNum.super.hermiteH(arg2);
  }

  @Override
  public int hierarchy() {
    return DOUBLECOMPLEXID;
  }

  @Override
  public IExpr hypergeometric0F1(IExpr arg2) {
    if (arg2 instanceof INumber) {
      return valueOf(
          h().hypergeometric0F1(fApcomplex, ((INumber) arg2).apcomplexValue()));
    }
    return IComplexNum.super.hypergeometric0F1(arg2);
  }

  @Override
  public IExpr hypergeometric0F1Regularized(IExpr arg2) {
    if (arg2 instanceof INumber) {
      return valueOf(h().hypergeometric0F1Regularized(fApcomplex,
          ((INumber) arg2).apcomplexValue()));
    }
    return IComplexNum.super.hypergeometric0F1Regularized(arg2);
  }

  @Override
  public IExpr hypergeometric1F1(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return valueOf(h().hypergeometric1F1(fApcomplex,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        return Errors.printMessage(S.Hypergeometric1F1, e);
      }

    }
    return IComplexNum.super.hypergeometric1F1(arg2, arg3);
  }

  @Override
  public IExpr hypergeometric1F1Regularized(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      return valueOf(h().hypergeometric1F1Regularized(fApcomplex,
          ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
    }
    return IComplexNum.super.hypergeometric1F1Regularized(arg2, arg3);
  }

  @Override
  public IExpr hypergeometric2F1(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        return valueOf(
            h().hypergeometric2F1(fApcomplex, ((INumber) arg2).apcomplexValue(),
                ((INumber) arg3).apcomplexValue(), ((INumber) arg4).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.hypergeometric2F1(arg2, arg3, arg4);
  }

  @Override
  public IExpr hypergeometric2F1Regularized(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        return valueOf(h().hypergeometric2F1Regularized(fApcomplex,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue(),
            ((INumber) arg4).apcomplexValue()));
      } catch (NumericComputationException ex) {
        // org.apfloat.OverflowException: Apfloat disk file storage is disabled
      }
    }
    return IComplexNum.super.hypergeometric2F1Regularized(arg2, arg3, arg4);
  }

  @Override
  public IExpr hypergeometricU(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      return valueOf(HypergeometricUWorkaround.hypergeometricU(h(), fApcomplex,
          ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
    }
    return IComplexNum.super.hypergeometricU(arg2, arg3);
  }

  /** {@inheritDoc} */
  @Override
  public IReal im() {
    return knownRealPart(fApcomplex.imag());
  }

  @Override
  public double imDoubleValue() {
    return fApcomplex.imag().doubleValue();
  }

  /** {@inheritDoc} */
  @Override
  public IExpr inc() {
    return add(ONE);
  }

  /** {@inheritDoc} */
  @Override
  public IComplex integerPart() {
    // isNegative() ? ceilFraction() : floorFraction();
    Apfloat re = fApcomplex.real();
    Apfloat im = fApcomplex.imag();
    IInteger reInt;
    IInteger imInt;
    if (re.signum() == -1) {
      // ceilFraction
      reInt = F.ZZ(ApfloatMath.ceil(re).toBigInteger());
    } else {
      // floorFraction
      reInt = F.ZZ(ApfloatMath.floor(re).toBigInteger());
    }
    if (im.signum() == -1) {
      // ceilFraction
      imInt = F.ZZ(ApfloatMath.ceil(im).toBigInteger());
    } else {
      // floorFraction
      imInt = F.ZZ(ApfloatMath.floor(im).toBigInteger());
    }
    return F.CC(reInt, imInt);
  }

  @Override
  public INumber inverse() {
    if (isOne()) {
      return this;
    }
    return scaledRelativeError(h().inverseRoot(fApcomplex, 1), 1.0);
  }

  @Override
  public boolean isMathematicalIntegerNegative() {
    return fApcomplex.real().isInteger() && fApcomplex.real().compareTo(Apfloat.ZERO) < 0
        && fApcomplex.imag().isZero();
  }

  @Override
  public boolean isMathematicalIntegerNonNegative() {
    return fApcomplex.real().isInteger() && fApcomplex.real().compareTo(Apfloat.ZERO) >= 0
        && fApcomplex.imag().isZero();
  }

  @Override
  public boolean isMinusOne() {
    return fApcomplex.imag().isZero() && fApcomplex.real().compareTo(ApfloatNum.MINUS_ONE) == 0;
  }

  @Override
  public boolean isOne() {
    return fApcomplex.imag().isZero() && fApcomplex.real().compareTo(Apfloat.ONE) == 0;
  }

  @Override
  public boolean isSame(IExpr expression, double epsilon) {
    if (expression instanceof ApcomplexNum) {
      return fApcomplex.equals(((ApcomplexNum) expression).fApcomplex);
    }
    return false;
  }

  @Override
  public boolean isZero() {
    return fApcomplex.isZero();
  }

  @Override
  public boolean isFuzzyZero() {
    return F.isZero(fApcomplex);
  }

  @Override
  public boolean isZero(double tolerance) {
    return F.isZero(fApcomplex, tolerance);
  }

  @Override
  public IExpr jacobiP(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        return valueOf(
            h().jacobiP(fApcomplex, ((INumber) arg2).apcomplexValue(),
                ((INumber) arg3).apcomplexValue(), ((INumber) arg4).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.jacobiP(arg2, arg3, arg4);
  }

  @Override
  public IExpr laguerreL(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            h().laguerreL(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.laguerreL(arg2);
  }

  @Override
  public IExpr laguerreL(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(h().laguerreL(fApcomplex,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.laguerreL(arg2, arg3);
  }

  @Override
  public long leafCountSimplify() {
    return 5;
  }

  @Override
  public IExpr legendreP(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            h().legendreP(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.legendreP(arg2);
  }

  @Override
  public IExpr legendreP(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(h().legendreP(fApcomplex,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.legendreP(arg2, arg3);
  }

  @Override
  public IExpr legendreQ(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            h().legendreQ(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.legendreQ(arg2);
  }

  @Override
  public IExpr legendreQ(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(h().legendreQ(fApcomplex,
            ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.legendreQ(arg2, arg3);
  }

  @Override
  public ApcomplexNum log() {
    return valueOf(h().log(fApcomplex));
  }

  @Override
  public ApcomplexNum barnesG() {
    return valueOf(h().barnesG(fApcomplex));
  }

  @Override
  public ApcomplexNum logBarnesG() {
    return valueOf(h().logBarnesG(fApcomplex));
  }

  @Override
  public IExpr log(final IExpr base) {
    if (base instanceof INumber) {
      if (base.isZero()) {
        return S.Indeterminate;
      }
      try {
        return valueOf(h().log(fApcomplex, ((INumber) base).apcomplexValue()));
      } catch (ApfloatArithmeticException aex) {
        if (aex.getLocalizationKey().equals("divide.byZero")) {
          // log(x,0) is undefined
          return F.CComplexInfinity;
        }
      }
      // return ApcomplexNum.valueOf(
      // h().log(apcomplexValue(), ((INumber) base).apcomplexValue()));
    }
    return IComplexNum.super.log(base);
  }

  @Override
  public ApcomplexNum log10() {
    return valueOf(h().log(fApcomplex, new Apfloat(10)));
  }

  @Override
  public ApcomplexNum log1p() {
    return valueOf(h().log(fApcomplex.add(Apfloat.ONE)));
  }

  @Override
  public IExpr logGamma() {
    try {
      Apcomplex logGamma = h().logGamma(fApcomplex);
      return F.complexNum(logGamma);
    } catch (ApfloatArithmeticException aaex) {
      String localizationKey = aaex.getLocalizationKey();
      if ("logGamma.ofZero".equals(localizationKey)) {
        return F.CInfinity;
      }
      if ("logGamma.ofNegativeInteger".equals(localizationKey)) {
        return F.CInfinity;
      }
      aaex.printStackTrace();
    }
    return IComplexNum.super.logGamma();
  }

  @Override
  public IExpr logIntegral() {
    return valueOf(h().logIntegral(fApcomplex));
  }

  @Override
  public IExpr logisticSigmoid() {
    return valueOf(h().logisticSigmoid(fApcomplex));
  }

  /**
   * @param that
   * @return
   */
  public ApcomplexNum multiply(final ApcomplexNum that) {
    return productResult(product(that.fApcomplex), that);
  }

  @Override
  public ApcomplexNum multiply(double value) {
    return productResult(product(apfloatOf(value)), Double.valueOf(value));
  }

  @Override
  public IComplexNum multiply(final IComplexNum val) {
    return productResult(product(val.apcomplexValue()), val);
  }

  @Override
  public IExpr multiply(final IExpr that) {
    if (that instanceof IComplexNum || that instanceof INum) {
      return productResult(product(((INumber) that).apcomplexValue()), that);
    }
    return IComplexNum.super.multiply(that);
  }

  @Override
  public ApcomplexNum multiply(int value) {
    return productResult(product(apfloatOf(value)), F.ZZ(value));
  }

  /** @return */
  @Override
  public ApcomplexNum negate() {
    return sameSignificance(h().negate(fApcomplex));
  }

  @Override
  public ApcomplexNum newInstance(double d) {
    return valueOf(d);
  }

  @Override
  public IComplexNum one() {
    return ONE;
  }

  /** @return */
  @Override
  public INumber opposite() {
    return sameSignificance(h().negate(fApcomplex));
  }

  /**
   * @param that
   * @return
   */
  @Override
  public IExpr plus(final IExpr that) {
    if (that instanceof INumber) {
      return plus((INumber) that);
    }
    return IComplexNum.super.plus(that);
  }

  @Override
  public IInexactNumber plus(final IInexactNumber that) {
    return sumResult(sum(that.apcomplexValue()), that);
  }

  @Override
  public INumber plus(final INumber that) {
    return sumResult(sum(that.apcomplexValue()), that);
  }

  /**
   * <code>PolyLog(nu, z)</code> for a complex order with an integer real part, or {@link F#NIL}.
   *
   * @param precision the precision of the arguments, which are padded to it
   */
  static IExpr polyLogImaginaryOrder(Apcomplex nu, Apcomplex z, long precision) {
    if (!nu.real().isInteger() || nu.imag().signum() == 0) {
      return F.NIL;
    }
    try {
      Apcomplex polylog = ApcomplexZetaMath.polylog(
          new Apcomplex(nu.real().truncate(), nu.imag().precision(precision)),
          precisionOfParts(z, precision));
      return F.complexNum(polylog);
    } catch (ArithmeticException | ApfloatRuntimeException ex) {
      return F.NIL;
    }
  }

  /**
   * Whether <code>Pochhammer(a, n) == Gamma(a+n)/Gamma(a)</code> has a pole: <code>a+n</code> is a
   * non-positive integer and <code>a</code> is not.
   */
  static boolean isPochhammerPole(Apcomplex a, Apcomplex n) {
    Apcomplex sum = a.add(n);
    return sum.imag().signum() == 0 && sum.real().signum() <= 0 && sum.real().isInteger()
        && !(a.imag().signum() == 0 && a.real().signum() <= 0 && a.real().isInteger());
  }

  @Override
  public IExpr pochhammer(IExpr arg2) {
    if (arg2 instanceof INumber) {
      Apcomplex n = ((INumber) arg2).apcomplexValue();
      if (isPochhammerPole(apcomplexValue(), n)) {
        return F.CComplexInfinity;
      }
      Apcomplex pochhammer = ApfloatNum.pochhammer(apcomplexValue(), n, h().precision());
      return F.complexNum(pochhammer);
    }
    return IComplexNum.super.pochhammer(arg2);
  }

  @Override
  public IExpr polyGamma(long n) {
    try {
      Apcomplex polygamma = h().polygamma(n, fApcomplex);
      return F.complexNum(polygamma);
    } catch (ArithmeticException | NumericComputationException aex) {
      if (Num.hasApfloatKey(aex, "polygamma.ofNonpositiveInteger")) {
        return F.ComplexInfinity;
      }
    }
    return IComplexNum.super.polyGamma(n);
  }

  @Override
  public IExpr polyLog(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return valueOf(
            h().polylog(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (LossOfPrecisionException lope) {
        // Complete loss of precision
      } catch (InfiniteExpansionException iee) {
        // a complex order with an integer real part
        IExpr polylog =
            polyLogImaginaryOrder(fApcomplex, ((INumber) arg2).apcomplexValue(), h().precision());
        if (polylog.isPresent()) {
          return polylog;
        }
      } catch (ArithmeticException | NumericComputationException ex) {
      }
    }
    return IComplexNum.super.polyLog(arg2);
  }

  @Override
  public IExpr pow(double value) {
    return valueOf(h().pow(fApcomplex, apfloatOf(value)));
  }

  /**
   * A power with a complex exponent is <code>Exp(exponent*Log(base))</code>, and periodic in the
   * imaginary part of that product: its size is reported to {@link PrecisionLoss} as the size of
   * the exponent, <code>E^(I*10^30)</code>.
   */
  static void periodicPower(Apcomplex base, Apcomplex exponent) {
    if (exponent.imag().signum() != 0 || base.imag().signum() != 0
        || base.real().signum() < 0) {
      PrecisionLoss.periodic(exponent.imag().signum() != 0 ? exponent.imag() : exponent.real());
    }
  }

  @Override
  public IComplexNum pow(final IComplexNum val) {
    periodicPower(fApcomplex, val.apcomplexValue());
    final IExpr power = powerResult(h().pow(fApcomplex, val.apcomplexValue()), val);
    return power instanceof IComplexNum ? (IComplexNum) power
        : valueOf(h().pow(fApcomplex, val.apcomplexValue()));
  }

  @Override
  public IExpr pow(int n) {
    if (n == (-1)) {
      return inverse();
    }
    return scaledRelativeError(h().pow(fApcomplex, n), n);
  }

  @Override
  public IExpr power(final IExpr that) {
    if (that instanceof IComplexNum) {
      periodicPower(fApcomplex, ((IComplexNum) that).apcomplexValue());
      return powerResult(h().pow(fApcomplex, ((IComplexNum) that).apcomplexValue()), that);
    }
    if (that instanceof INum) {
      return powerResult(h().pow(fApcomplex, ((INum) that).apcomplexValue()), that);
    }
    return IComplexNum.super.power(that);
  }

  /**
   * The power <code>value</code> of this number to <code>exponent</code>: an exponent without an
   * error of its own multiplies the relative error of the base by <code>Abs(exponent)</code>.
   */
  private IExpr powerResult(final Apcomplex value, final IExpr exponent) {
    if (fSignificance != TRACKED || isTracked(exponent)) {
      return valueOf(value);
    }
    final Apcomplex e = ((INumber) exponent).apcomplexValue();
    return isZeroValue(e) ? valueOf(value) : scaledRelativeError(value, ApcomplexMath.abs(e).doubleValue());
  }

  @Override
  public long precision() throws NumericComputationException {
    return fApcomplex.precision();
  }

  /** {@inheritDoc} */
  @Override
  public IReal re() {
    return knownRealPart(fApcomplex.real());
  }

  @Override
  public ApcomplexNum reciprocal() {
    return scaledRelativeError(h().inverseRoot(fApcomplex, 1), 1.0);
  }

  @Override
  public double reDoubleValue() {
    return fApcomplex.real().doubleValue();
  }

  @Override
  public IExpr remainder(double value) {
    FixedPrecisionApfloatHelper h = h();
    return valueOf(h.mod(fApcomplex.real(), apfloatOf(value)), //
        h.mod(fApcomplex.imag(), apfloatOf(value)));
  }

  @Override
  public IExpr rint() {
    return valueOf( //
        ApfloatNum.apfloatRint(fApcomplex.real()), //
        ApfloatNum.apfloatRint(fApcomplex.imag()));
  }

  @Override
  public IExpr rootN(int n) {
    return valueOf(h().root(fApcomplex, n));
  }

  /** {@inheritDoc} */
  @Override
  public INumber roundExpr() {
    return F.CC(F.ZZ(NumberUtil.round(fApcomplex.real(), Config.ROUNDING_MODE)),
        F.ZZ(NumberUtil.round(fApcomplex.imag(), Config.ROUNDING_MODE)));
  }

  @Override
  public IExpr scalb(int n) {
    FixedPrecisionApfloatHelper h = h();
    return valueOf(h.multiply(fApcomplex, h.pow(new Apfloat(2), n)));
  }

  @Override
  public IExpr sign() {
    if (isNaN() || isZero()) {
      return this;
    }
    FixedPrecisionApfloatHelper h = h();
    return valueOf(h.divide(fApcomplex, h.abs(fApcomplex)));
  }

  @Override
  public ApcomplexNum sin() {
    PrecisionLoss.periodic(fApcomplex.real());
    return valueOf(h().sin(fApcomplex));
  }

  @Override
  public ApcomplexNum sinc() {
    return valueOf(h().sinc(fApcomplex));
  }

  @Override
  public ApcomplexNum sinh() {
    PrecisionLoss.periodic(fApcomplex.imag());
    return valueOf(h().sinh(fApcomplex));
  }

  @Override
  public IExpr sinhIntegral() {
    return valueOf(h().sinhIntegral(fApcomplex));
  }

  @Override
  public IExpr sinIntegral() {
    return valueOf(h().sinIntegral(fApcomplex));
  }

  @Override
  public IExpr sqr() {
    return this.multiply(this);
  }

  @Override
  public IExpr sqrt() {
    return valueOf(h().sqrt(fApcomplex));
  }

  @Override
  public IExpr struveH(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            h().struveH(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.angerJ(arg2);
  }

  @Override
  public IExpr struveL(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(
            h().struveL(fApcomplex, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return IComplexNum.super.angerJ(arg2);
  }

  /**
   * @param that
   * @return
   */
  public Apcomplex subtract(final Apcomplex that) {
    return difference(that);
  }

  public ApcomplexNum subtract(final ApcomplexNum that) {
    return sumResult(difference(that.fApcomplex), that);
  }

  @Override
  public IExpr subtract(double value) {
    return sumResult(difference(apfloatOf(value)), Double.valueOf(value));
  }

  @Override
  public IComplexNum subtract(final IComplexNum val) {
    return sumResult(difference(val.apcomplexValue()), val);
  }

  @Override
  public IExpr subtract(final IExpr that) {
    if (that instanceof IComplexNum || that instanceof INum) {
      return sumResult(difference(((INumber) that).apcomplexValue()), that);
    }
    return IComplexNum.super.subtract(that);
  }

  /**
   * A reciprocal trigonometric or hyperbolic function, or the inverse of one, with <code>
   * ComplexInfinity</code> at a pole.
   */
  static IExpr reciprocalTrig(UnaryOperator<Apcomplex> complex, Apcomplex z) {
    try {
      return F.complexNum(complex.apply(z));
    } catch (ArithmeticException ae) {
      return F.CComplexInfinity;
    }
  }

  /**
   * A zero has infinite precision, and a result like Pi/2 can only be computed to the precision
   * of the engine.
   */
  static long zeroPrecision(Apcomplex z) {
    long precision = z.precision();
    if (precision == Apfloat.INFINITE) {
      // without a precision of the engine the digits of the input are not known
      return Math.max(EvalEngine.get().getNumericPrecision(), ParserConfig.MACHINE_PRECISION);
    }
    return precision;
  }

  @Override
  public IExpr cot() {
    PrecisionLoss.periodic(fApcomplex.real());
    return reciprocalTrig(ApcomplexTrigMath::cot, fApcomplex);
  }

  @Override
  public IExpr sec() {
    PrecisionLoss.periodic(fApcomplex.real());
    return reciprocalTrig(ApcomplexTrigMath::sec, fApcomplex);
  }

  @Override
  public IExpr csc() {
    PrecisionLoss.periodic(fApcomplex.real());
    return reciprocalTrig(ApcomplexTrigMath::csc, fApcomplex);
  }

  @Override
  public IExpr coth() {
    PrecisionLoss.periodic(fApcomplex.imag());
    return reciprocalTrig(ApcomplexTrigMath::coth, fApcomplex);
  }

  @Override
  public IExpr sech() {
    PrecisionLoss.periodic(fApcomplex.imag());
    return reciprocalTrig(ApcomplexTrigMath::sech, fApcomplex);
  }

  @Override
  public IExpr csch() {
    PrecisionLoss.periodic(fApcomplex.imag());
    return reciprocalTrig(ApcomplexTrigMath::csch, fApcomplex);
  }

  @Override
  public IExpr acot() {
    final long precision = zeroPrecision(fApcomplex);
    return reciprocalTrig(z -> ApcomplexTrigMath.acot(z, precision), fApcomplex);
  }

  @Override
  public IExpr asec() {
    final long precision = zeroPrecision(fApcomplex);
    return reciprocalTrig(z -> ApcomplexTrigMath.asec(z, precision), fApcomplex);
  }

  @Override
  public IExpr acsc() {
    final long precision = zeroPrecision(fApcomplex);
    return reciprocalTrig(z -> ApcomplexTrigMath.acsc(z, precision), fApcomplex);
  }

  @Override
  public IExpr acoth() {
    final long precision = zeroPrecision(fApcomplex);
    return reciprocalTrig(z -> ApcomplexTrigMath.acoth(z, precision), fApcomplex);
  }

  @Override
  public IExpr asech() {
    if (fApcomplex.isZero()) {
      // an inexact zero has no direction
      return S.Indeterminate;
    }
    final long precision = zeroPrecision(fApcomplex);
    return reciprocalTrig(z -> ApcomplexTrigMath.asech(z, precision), fApcomplex);
  }

  @Override
  public IExpr acsch() {
    final long precision = zeroPrecision(fApcomplex);
    return reciprocalTrig(z -> ApcomplexTrigMath.acsch(z, precision), fApcomplex);
  }

  @Override
  public ApcomplexNum tan() {
    PrecisionLoss.periodic(fApcomplex.real());
    return valueOf(h().tan(fApcomplex));
  }

  @Override
  public ApcomplexNum tanh() {
    PrecisionLoss.periodic(fApcomplex.imag());
    return valueOf(h().tanh(fApcomplex));
  }

  /**
   * @param that
   * @return
   */
  @Override
  public IExpr times(final IExpr that) {
    if (that instanceof INumber) {
      return times((INumber) that);
    }
    return IComplexNum.super.times(that);
  }

  @Override
  public IInexactNumber times(final IInexactNumber that) {
    return productResult(product(that.apcomplexValue()), that);
  }

  @Override
  public IInexactNumber times(final INumber that) {
    return productResult(product(that.apcomplexValue()), that);
  }

  @Override
  public IExpr toDegrees() {
    FixedPrecisionApfloatHelper h = h();
    return valueOf(ApfloatNum.toDegrees(fApcomplex.real(), h),
        ApfloatNum.toDegrees(fApcomplex.imag(), h));
  }

  @Override
  public IAST toPolarCoordinates() {
    return F.list(abs(), complexArg());
  }

  @Override
  public IExpr toRadians() {
    FixedPrecisionApfloatHelper h = h();
    return valueOf(ApfloatNum.toRadians(fApcomplex.real(), h),
        ApfloatNum.toRadians(fApcomplex.imag(), h));
  }

  @Override
  public String toString() {
    try {
      StringBuilder sb = new StringBuilder();
      OutputFormFactory.get().convertApcomplex(sb, this, Integer.MIN_VALUE,
          OutputFormFactory.NO_PLUS_CALL);
      return sb.toString();
    } catch (Exception e1) {
      Errors.rethrowsInterruptException(e1);
      // fall back to simple output format
    }

    String str = fApcomplex.toString();
    if (ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      return str.replace("e", "E");
    }
    int index = str.indexOf('e');
    if (index > 0) {
      String exponentStr1 = str.substring(++index);
      str = str.replace("e", "*10^");
      int index2 = exponentStr1.indexOf('e');
      if (index2 > 0) {
        str = str.replace("e", "*10^");
      }
    }
    return str;
  }

  @Override
  public IExpr ulp() {
    return valueOf(h().ulp(Apfloat.ONE));
  }

  @Override
  public IComplexNum zero() {
    return ZERO;
  }
}
