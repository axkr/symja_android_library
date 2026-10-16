package org.matheclipse.core.expression;

import org.matheclipse.external.apfloat.HypergeometricUWorkaround;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.function.UnaryOperator;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatArithmeticException;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LossOfPrecisionException;
import org.apfloat.NumericComputationException;
import org.apfloat.OverflowException;
import org.matheclipse.external.apfloat.ApcomplexEllipticMath;
import org.matheclipse.external.apfloat.ApcomplexBesselMath;
import org.matheclipse.external.apfloat.ApcomplexBetaMath;
import org.matheclipse.external.apfloat.ApcomplexTrigMath;
import org.matheclipse.external.apfloat.ApfloatTrigMath;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.PrecisionLoss;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBigNumber;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import org.matheclipse.parser.client.ParserConfig;

/**
 * <code>INum</code> implementation which wraps a {@link Apfloat} value to represent a numeric
 * floating-point number.
 */
public class ApfloatNum implements INum {

  /** */
  private static final long serialVersionUID = 2500259920655377884L;

  public static final Apint MINUS_ONE = new Apint(-1);

  public static final Apint TWO = new Apint(2);

  public static Apfloat apfloatRint(Apfloat fApfloat) {
    if (fApfloat.scale() > 0) {
      return ApfloatMath.roundToPrecision(fApfloat, fApfloat.scale(), Config.ROUNDING_MODE);
    }
    if (ApfloatMath.abs(fApfloat).compareTo(new Apfloat("0.5")) <= 0) {
      return Apfloat.ZERO;
    }
    return ApfloatMath.copySign(Apfloat.ONE, fApfloat);
  }

  public static void checkHypergeometric2F1(Apfloat a, Apfloat b, Apfloat x)
      throws ArithmeticException {
    // With real a, b and c the result is real if z <= 1 except if it's a polynomial, in which case
    // it's always real (nb. additional checks might throw an exception later)
    if (x.compareTo(Apfloat.ONE) > 0 && maxNonPositiveInteger(a, b) == null) {
      throw new ApfloatArithmeticException("Result would be complex", "complex");
    }
  }

  public static String fullFormString(Apfloat apfloat) {
    String str = apfloat.toString();
    long precision = apfloat.precision();
    if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      int indx = str.indexOf("e");
      if (indx > 0) {
        str = str.substring(0, indx) + "`" + precision + "*^" + str.substring(indx + 1);
      } else {
        str = str + "`" + precision;
      }
    }
    return str;
  }

  /**
   * Eval in double numeric mode by "widen the input domain" to Apfloat values.
   * 
   * @param powerAST2 "binary {@link S#Power} function"
   * @param engine the {@link EvalEngine} to use for evaluation
   */
  public static IExpr intPowerFractionNumeric(IAST powerAST2, EvalEngine engine) {
    final IExpr base = powerAST2.base();
    final IExpr exponent = powerAST2.exponent();
    if ((base instanceof IBigNumber) && exponent.isFraction()) {
      final int nthRoot = ((IFraction) exponent).toIntRoot();
      if (F.isPresent(nthRoot)) {
        long oldPrecision = engine.getNumericPrecision();
        try {
          engine.setNumericPrecision(ParserConfig.MACHINE_PRECISION * 2);
          if (base.isRational()) {
            IRational ratBase = (IRational) base;
            final double fNum = base.evalfNaN();
            if (!Double.isFinite(fNum) || fNum <= Double.MIN_VALUE || fNum >= Double.MAX_VALUE) {
              if (ratBase.isPositive()) {
                // out of the double range the root is an arbitrary precision number of machine
                // precision, like every other machine overflow
                return machineOrPromoted(ratBase.apfloatNumValue().rootN(nthRoot).apfloatValue());
              } else if (ratBase.isNegative()) {
                ApcomplexNum apcomplex = ratBase.apcomplexNumValue();
                return F.complexNum(apcomplex.rootN(nthRoot).evalfc());
              }
            }
          } else if (base.isComplex()) {
            final IComplex cmpBase = (IComplex) base;
            org.hipparchus.complex.Complex fComplex = base.evalfc();
            if (!fComplex.isFinite()) {
              ApcomplexNum apcomplex = cmpBase.apcomplexNumValue();
              return F.complexNum(apcomplex.rootN(nthRoot).evalfc());
            }
          }
        } finally {
          engine.setNumericPrecision(oldPrecision);
        }
      }
    }
    if (base == S.E && exponent.isNumericFunction()) {
      // don't use F.Exp() here; it creates a `Power(E, exponent)` expression again
      return F.unaryAST1(S.Exp, exponent);
    }
    return F.NIL;
  }

  private static Apfloat maxNonPositiveInteger(Apcomplex... a) {
    return Arrays.stream(a).filter(Apcomplex::isInteger).map(Apcomplex::real)
        .filter(x -> x.signum() <= 0).reduce(ApfloatMath::max).orElse(null);
  }

  /**
   * <code>radians * (180 / Pi)</code>
   *
   * @param radians
   * @param h
   * @return
   */
  static Apfloat toDegrees(Apfloat radians, FixedPrecisionApfloatHelper h) {
    return h.divide(h.multiply(radians, new Apfloat(180)), h.pi());
  }

  /**
   * <code>degrees * (Pi / 180)</code>
   *
   * @param degrees
   * @param h
   * @return
   */
  static Apfloat toRadians(Apfloat degrees, FixedPrecisionApfloatHelper h) {
    return h.divide(h.multiply(degrees, h.pi()), new Apfloat(180));
  }

  public static ApfloatNum valueOf(final Apfloat value) {
    return new ApfloatNum(value);
  }

  /**
   * A machine precision result which may lie outside the double range: the machine number of
   * <code>value</code>, or the arbitrary precision number of machine precision if
   * <code>value</code> is too large for a double or too small to be one which isn't zero.
   */
  public static INum machineOrPromoted(final Apfloat value) {
    final double d = value.doubleValue();
    if (Double.isInfinite(d) || (d == 0.0 && value.signum() != 0)) {
      return valueOf(value.precision(ParserConfig.MACHINE_PRECISION));
    }
    return F.num(d);
  }

  /**
   * The quotient of two integers as an arbitrary precision number of machine precision: the
   * machine precision value of a rational number beyond the double range.
   */
  public static ApfloatNum promoted(final BigInteger numerator, final BigInteger denominator) {
    final long precision = ParserConfig.MACHINE_PRECISION;
    Apfloat n = apfloatOf(numerator, precision);
    return valueOf(denominator.equals(BigInteger.ONE) ? n
        : n.divide(apfloatOf(denominator, precision)));
  }

  /**
   * The literal <code>mantissa*^exponent</code> as an arbitrary precision number of machine
   * precision: a literal like <code>1.*^400</code>, which is beyond the double range.
   *
   * @param precision the precision of the literal, at least the machine precision
   */
  public static ApfloatNum promotedLiteral(final String mantissa, final long exponent,
      final long precision) {
    Apfloat value = new Apfloat(mantissa, Math.max(precision, ParserConfig.MACHINE_PRECISION));
    return valueOf(exponent == 0 ? value : ApfloatMath.scale(value, exponent));
  }

  public static ApfloatNum valueOf(final BigInteger numerator) {
    return new ApfloatNum(numerator);
  }

  /** From this many bits beyond the precision on, an integer is cut before it is converted. */
  private static final int LONG_INTEGER_BITS = 4096;

  /**
   * An integer as an arbitrary precision number of the given precision. A very long integer is cut
   * to that precision first: apfloat would build all its digits, and beyond its memory limit ask
   * for file storage, which is disabled - <code>N(3^(10^6), 20)</code>.
   *
   * @param value the integer
   * @param precision the precision of the result
   */
  public static Apfloat apfloatOf(final BigInteger value, final long precision) {
    if (precision == Apfloat.INFINITE || precision > Integer.MAX_VALUE / 8) {
      return new Apfloat(value, precision);
    }
    final long bits = value.bitLength();
    if (bits <= Math.ceil(precision * 3.3219280948873626) + LONG_INTEGER_BITS) {
      return new Apfloat(value, precision);
    }
    // The leading decimal digits, some more than the precision, are the quotient by a power of
    // ten: cutting bits instead would turn 10^(10^6)+1 into 9.99...
    final long digits = (long) (bits * 0.30102999566398120);
    final int drop = (int) (digits - precision - 20);
    Apfloat leading = new Apfloat(value.divide(BigInteger.TEN.pow(drop)), precision);
    return ApfloatMath.scale(leading, drop);
  }

  public static ApfloatNum valueOf(final BigInteger numerator, final BigInteger denominator) {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    Apfloat n = apfloatOf(numerator, h.precision());
    Apfloat d = apfloatOf(denominator, h.precision());
    return new ApfloatNum(h.divide(n, d));
  }

  /**
   * Create a new instance.
   *
   * @param value
   * @return
   */

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

  /**
   * A machine double as the binary number it is, rounded to <code>digits</code> digits behind the
   * decimal point, and to <code>digits</code> digits for a number below 1.
   *
   * <p>
   * <code>new Apfloat(double)</code> has 16 digits and what it holds behind them isn't the number:
   * <code>7.7*10^17</code> is <code>770000000000000016</code> with more digits. A routine which
   * adds digits to its arguments - the guard digits of an oscillating function of a large argument
   * - takes that for the argument, and with it another phase.
   *
   * @param value the machine double to convert
   * @param digits the number of digits, at least 1
   * @return the value with at least <code>digits</code> digits; zero has no precision
   */
  public static Apfloat apfloatOf(final double value, final long digits) {
    if (!Double.isFinite(value)) {
      throw new SymjaMathException(
          "cannot convert " + value + " into an arbitrary precision number");
    }
    if (value == 0.0) {
      return Apfloat.ZERO;
    }
    BigDecimal exact = new BigDecimal(value);
    final long precision = digits + Math.max(0, exact.precision() - exact.scale());
    if (exact.precision() > precision) {
      exact = exact.round(new MathContext((int) precision, RoundingMode.HALF_EVEN));
    }
    return new Apfloat(exact).precision(precision);
  }

  public static ApfloatNum valueOf(final double value) {
    if (!Double.isFinite(value)) {
      // Apfloat has no representation for an infinity or a NaN, and answers a NumberFormatException
      // which none of the paths reaching here expect - arithmetic between an ApfloatNum and a
      // machine Num mostly, as in ApfloatNum#times. Raising it as a Symja exception instead means
      // EvalEngine#evalASTBuiltinFunction turns it into a message and an unevaluated result, the
      // same treatment Num#apfloatValue() already gets.
      throw new SymjaMathException(
          "cannot convert " + value + " into an arbitrary precision number");
    }
    return valueOf(apfloatOf(value));
  }

  public static ApfloatNum valueOf(final String value, long precision) {
    return new ApfloatNum(value, precision);
  }

  Apfloat fApfloat;

  /**
   * The helper for calculations with this number: the engine's while a precision is set, else one
   * of this number's precision - see {@link EvalEngine#getApfloat(long)}.
   */
  private FixedPrecisionApfloatHelper h() {
    if (fApfloat == null) {
      // while the number is constructed
      return EvalEngine.getApfloat();
    }
    return EvalEngine.getApfloat(fApfloat.precision());
  }

  private ApfloatNum(final Apfloat value) {
    fApfloat = value;
  }

  private ApfloatNum(final BigInteger value) {
    fApfloat = apfloatOf(value, h().precision());
  }

  private ApfloatNum(final String value, long precision) {
    fApfloat = new Apfloat(value, precision);
  }

  /** {@inheritDoc} */
  @Override
  public ApfloatNum abs() {
    return valueOf(h().abs(fApfloat));
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

  @Override
  public long accept(IVisitorLong visitor) {
    return visitor.visit(this);
  }

  @Override
  public IInexactNumber acos() {
    if (fApfloat.compareTo(Apfloat.ONE) == 1 //
        || fApfloat.compareTo(new Apint(-1)) == -1) {
      return F.complexNum(ApcomplexMath.acos(new Apcomplex(fApfloat)));
    }
    return valueOf(inverseCircular(fApfloat, true, h()));
  }

  /**
   * ArcSin or ArcCos of a number between -1 and 1. Next to 1 and -1 the functions behave like the
   * square root of the distance to them, and apfloat calculates them from that distance with the
   * digits which the number has left of it: <code>1-10^-30</code> at 48 digits gave 20. The
   * digits which the distance starts later than the number are added to the working precision,
   * with the argument as the exact number it reads.
   *
   * @param arcCos <code>true</code> for ArcCos, <code>false</code> for ArcSin
   */
  public static Apfloat inverseCircular(Apfloat x, boolean arcCos, FixedPrecisionApfloatHelper h) {
    final long precision = x.precision();
    if (x.signum() != 0 && precision != Apfloat.INFINITE) {
      final Apfloat distance = Apfloat.ONE.subtract(ApfloatMath.abs(x));
      if (distance.signum() > 0 && distance.scale() < 0) {
        final long working = precision - distance.scale() + 3;
        final Apfloat extended = x.precision(working);
        return (arcCos ? ApfloatMath.acos(extended) : ApfloatMath.asin(extended))
            .precision(precision);
      }
    }
    return arcCos ? h.acos(x) : h.asin(x);
  }

  @Override
  public ApfloatNum acosh() {
    return valueOf(h().acosh(fApfloat));
  }

  /** The sum with a real number; the digits which cancel are reported to {@link PrecisionLoss}. */
  private Apfloat sum(Apfloat that) {
    return PrecisionLoss.sum(fApfloat, that, h().add(fApfloat, that));
  }

  /** The difference to a real number; the digits which cancel are reported. */
  private Apfloat difference(Apfloat that) {
    return PrecisionLoss.sum(fApfloat, that, h().subtract(fApfloat, that));
  }

  /** The difference to a complex number; the digits which cancel are reported. */
  private Apcomplex difference(Apcomplex that) {
    return PrecisionLoss.sum(fApfloat, that, h().subtract(fApfloat, that));
  }

  @Override
  public IExpr add(double value) {
    return valueOf(sum(apfloatOf(value)));
  }

  @Override
  public INum add(final INum value) {
    return valueOf(sum(value.apfloatValue()));
  }

  @Override
  public IReal add(IReal value) {
    // Bypass INum's default, which narrows the operand to a machine double.
    return (IReal) plus((INumber) value);
  }

  @Override
  public IExpr agm(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(h().agm(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      return F.complexNum(h().agm(fApfloat, ((INumber) arg2).apcomplexValue()));
    }
    return INum.super.agm(arg2);
  }

  /**
   * An Airy function of this number. The functions of a negative argument oscillate with the phase
   * <code>2/3*Abs(x)^(3/2)</code>, whose digits in front of the decimal point are no digits of the
   * result: the argument is taken with as many more.
   *
   * @return {@link F#NIL} if no digit of the value is left
   */
  private IExpr airy(UnaryOperator<Apfloat> function, UnaryOperator<Apfloat> helperFunction) {
    try {
      if (fApfloat.signum() >= 0 || fApfloat.scale() <= 0) {
        return valueOf(helperFunction.apply(fApfloat));
      }
      final long precision = fApfloat.precision();
      // the library takes twice the digits of the phase, which has 3/2 of those of the argument
      final long phaseDigits = 3 * fApfloat.scale() + 10;
      Apfloat value = function.apply(fApfloat.precision(precision + phaseDigits));
      return valueOf(value.precision() > precision ? value.precision(precision) : value);
    } catch (LossOfPrecisionException lope) {
      return F.NIL;
    }
  }

  @Override
  public IExpr airyAi() {
    return airy(ApfloatMath::airyAi, x -> h().airyAi(x));
  }

  @Override
  public IExpr airyAiPrime() {
    return airy(ApfloatMath::airyAiPrime, x -> h().airyAiPrime(x));
  }

  @Override
  public IExpr airyBi() {
    return airy(ApfloatMath::airyBi, x -> h().airyBi(x));
  }

  @Override
  public IExpr airyBiPrime() {
    return airy(ApfloatMath::airyBiPrime, x -> h().airyBiPrime(x));
  }

  @Override
  public IExpr angerJ(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(h().angerJ(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(h().angerJ(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.angerJ(arg2);
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return ApcomplexNum.valueOf(fApfloat);
  }

  // private static Apfloat erf(Apfloat x, FixedPrecisionApfloatHelper h) {
  // Apint two = new Apint(2);
  // // 1/2
  // Aprational oneHalf = new Aprational(Apint.ONE, new Apint(2));
  // // 3/2
  // Aprational threeHalf = new Aprational(new Apint(3), new Apint(2));
  // Apfloat erf = h.hypergeometric1F1(oneHalf, threeHalf, h.multiply(x, x).negate()).multiply(two)
  // .multiply(x).divide(h.sqrt(h.pi()));
  // return erf;
  // }

  @Override
  public Apcomplex apcomplexValue() {
    return new Apcomplex(fApfloat);
  }

  @Override
  public ApfloatNum apfloatNumValue() {
    return this;
  }

  @Override
  public Apfloat apfloatValue() {
    return fApfloat;
  }

  @Override
  public IInexactNumber asin() {
    if (fApfloat.compareTo(Apfloat.ONE) == 1 //
        || fApfloat.compareTo(new Apint(-1)) == -1) {
      return F.complexNum(ApcomplexMath.asin(new Apcomplex(fApfloat)));
    }
    return valueOf(inverseCircular(fApfloat, false, h()));
  }

  @Override
  public ApfloatNum asinh() {
    return valueOf(h().asinh(fApfloat));
  }

  @Override
  public ApfloatNum atan() {
    return valueOf(h().atan(fApfloat));
  }

  @Override
  public ApfloatNum atanh() {
    return valueOf(h().atanh(fApfloat));
  }

  @Override
  public IExpr besselI(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (!arg2.isZero()) {
        try {
          return guardedBesselFirstKind(apfloatValue(), ((INumber) arg2).apcomplexValue(),
              h().precision(), true);
        } catch (LossOfPrecisionException lope) {
          // no digit is left with the guard digits: the library alone gives a wrong number then,
          // -1.5*10^849 for BesselI(3001/2, 1200) with 30 digits
          return F.NIL;
        } catch (ArithmeticException | ApfloatRuntimeException are) {
          // the library routines below
        }
      }
      if (arg2 instanceof IReal) {
        try {
          Apfloat besselI = h().besselI(apfloatValue(), ((IReal) arg2).apfloatValue());
          return F.num(besselI);
        } catch (ArithmeticException | ApfloatRuntimeException are) {
          // result would be complex exception
        }
      }
      try {
        Apcomplex besselI = h().besselI(apfloatValue(), ((INumber) arg2).apcomplexValue());
        return F.complexNum(besselI);
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.BesselI, are);
      }
    }
    return INum.super.besselI(arg2);
  }

  @Override
  public IExpr besselJ(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (!arg2.isZero()) {
        try {
          return guardedBesselFirstKind(apfloatValue(), ((INumber) arg2).apcomplexValue(),
              h().precision(), false);
        } catch (LossOfPrecisionException lope) {
          // no digit is left with the guard digits: the library alone gives a wrong number then,
          // -1.5*10^849 for BesselI(3001/2, 1200) with 30 digits
          return F.NIL;
        } catch (ArithmeticException | ApfloatRuntimeException are) {
          // the library routines below
        }
      }
      if (arg2 instanceof IReal) {
        try {
          Apfloat besselJ = h().besselJ(apfloatValue(), ((IReal) arg2).apfloatValue());
          return F.num(besselJ);
        } catch (LossOfPrecisionException lopex) {
          if ("lossOfPrecision".equals(lopex.getLocalizationKey())) {
            return F.NIL;
          }
        } catch (ArithmeticException | ApfloatRuntimeException are) {
          // result would be complex exception
        }
      }
      try {
        Apcomplex besselJ = h().besselJ(apfloatValue(), ((INumber) arg2).apcomplexValue());
        return F.complexNum(besselJ);
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.BesselJ, are);
      }
    }
    return INum.super.besselJ(arg2);
  }

  @Override
  public IExpr besselK(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return guardedBessel(apfloatValue(), ((INumber) arg2).apcomplexValue(), h().precision(),
            false);
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.BesselK, are);
      }
    }
    return INum.super.besselK(arg2);
  }

  /**
   * <code>BesselY(nu, z)</code> or <code>BesselK(nu, z)</code> with the digits which the difference
   * of the two Bessel functions behind them cancels, and at an integer order - see
   * {@link ApcomplexBesselMath}.
   *
   * @param precision the precision of the arguments, which are padded to it
   * @return a real number if the imaginary part of the value is zero
   */
  /**
   * <code>BesselJ(nu, z)</code> or <code>BesselI(nu, z)</code> with the digits which the series
   * cancels where the order and the argument are large - see {@link ApcomplexBesselMath}.
   *
   * @return a real number if the imaginary part of the value is zero
   */
  static IExpr guardedBesselFirstKind(Apcomplex nu, Apcomplex z, long precision,
      boolean modified) {
    nu = new Apcomplex(nu.real().signum() == 0 ? nu.real() : nu.real().precision(precision),
        nu.imag().signum() == 0 ? nu.imag() : nu.imag().precision(precision));
    z = new Apcomplex(z.real().signum() == 0 ? z.real() : z.real().precision(precision),
        z.imag().signum() == 0 ? z.imag() : z.imag().precision(precision));
    Apcomplex value =
        modified ? ApcomplexBesselMath.besselI(nu, z) : ApcomplexBesselMath.besselJ(nu, z);
    return value.imag().signum() == 0 ? F.num(value.real()) : F.complexNum(value);
  }

  /** <code>Pochhammer(a, n)</code> of numbers which are padded to the precision. */
  static Apcomplex pochhammer(Apcomplex a, Apcomplex n, long precision) {
    a = new Apcomplex(a.real().signum() == 0 ? a.real() : a.real().precision(precision),
        a.imag().signum() == 0 ? a.imag() : a.imag().precision(precision));
    n = new Apcomplex(n.real().signum() == 0 ? n.real() : n.real().precision(precision),
        n.imag().signum() == 0 ? n.imag() : n.imag().precision(precision));
    return ApcomplexBetaMath.pochhammer(a, n);
  }

  static IExpr guardedBessel(Apcomplex nu, Apcomplex z, long precision, boolean besselY) {
    nu = new Apcomplex(nu.real().signum() == 0 ? nu.real() : nu.real().precision(precision),
        nu.imag().signum() == 0 ? nu.imag() : nu.imag().precision(precision));
    z = new Apcomplex(z.real().signum() == 0 ? z.real() : z.real().precision(precision),
        z.imag().signum() == 0 ? z.imag() : z.imag().precision(precision));
    Apcomplex value =
        besselY ? ApcomplexBesselMath.besselY(nu, z) : ApcomplexBesselMath.besselK(nu, z);
    return value.imag().signum() == 0 ? F.num(value.real()) : F.complexNum(value);
  }

  @Override
  public IExpr besselY(IExpr arg2) {
    if (arg2 instanceof INumber) {
      try {
        return guardedBessel(apfloatValue(), ((INumber) arg2).apcomplexValue(), h().precision(),
            true);
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.BesselY, are);
      }
    }
    return INum.super.besselY(arg2);
  }

  @Override
  public IExpr beta(IExpr b) {
    if (b instanceof IReal) {
      try {
        return valueOf(h().beta(fApfloat, ((IReal) b).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (b instanceof INumber) {
      try {
        return F.complexNum(h().beta(fApfloat, ((INumber) b).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // java.lang.ArithmeticException: Beta is infinite
      }
    }
    return INum.super.beta(b);
  }

  @Override
  public IExpr beta(IExpr a, IExpr b) {
    if (a instanceof IReal && b instanceof IReal) {
      Apfloat af = ((IReal) a).apfloatValue();
      Apfloat bf = ((IReal) b).apfloatValue();
      if (!(fApfloat.signum() == 0 && af.signum() < 0 && !af.isInteger()
          || fApfloat.signum() < 0 && !af.isInteger())) {
        try {
          return valueOf(h().beta(fApfloat, af, bf));
        } catch (ApfloatArithmeticException aaex) {
          if ("divide.byZero".equals(aaex.getLocalizationKey())) {
            return F.ComplexInfinity;
          }
        } catch (ArithmeticException | NumericComputationException e) {
          if ("Division by zero".equals(e.getMessage())) {
            return F.ComplexInfinity;
          }
        }
      }
    }
    if (a instanceof INumber && b instanceof INumber) {
      try {
        return F.complexNum(
            h().beta(fApfloat, ((INumber) a).apcomplexValue(), ((INumber) b).apcomplexValue()));
      } catch (ApfloatArithmeticException aaex) {
        if ("divide.byZero".equals(aaex.getLocalizationKey())) {
          return F.ComplexInfinity;
        }
      } catch (ArithmeticException | NumericComputationException aex) {
        if ("Division by zero".equals(aex.getMessage())) {
          return F.ComplexInfinity;
        }
      }
    }
    return INum.super.beta(a, b);
  }

  @Override
  public IExpr beta(IExpr x2, IExpr a, IExpr b) {
    if (x2 instanceof IReal && a instanceof IReal && b instanceof IReal) {
      Apfloat x2f = ((IReal) x2).apfloatValue();
      Apfloat af = ((IReal) a).apfloatValue();
      Apfloat bf = ((IReal) b).apfloatValue();
      if (!((fApfloat.signum() == 0 || x2f.signum() == 0) && af.signum() < 0 && !af.isInteger()
          || (fApfloat.signum() < 0 || x2f.signum() < 0) && !af.isInteger())) {
        try {
          return valueOf(h().beta(fApfloat, x2f, af, bf));
        } catch (ArithmeticException | NumericComputationException e) {
          // try as computation with complex numbers
        }
      }
    }
    if (x2 instanceof INumber && a instanceof INumber && b instanceof INumber) {
      try {
        return F.complexNum(h().beta(fApfloat, ((INumber) x2).apcomplexValue(),
            ((INumber) a).apcomplexValue(), ((INumber) b).apcomplexValue()));
      } catch (NumericComputationException aex) {
      } catch (ApfloatArithmeticException aaex) {
        if ("divide.byZero".equals(aaex.getLocalizationKey())) {
          return F.ComplexInfinity;
        }
      } catch (ArithmeticException aex) {
        if ("Division by zero".equals(aex.getMessage())) {
          return F.ComplexInfinity;
        }
      }
    }
    return INum.super.beta(x2, a, b);
  }

  @Override
  public ApfloatNum cbrt() {
    return valueOf(h().cbrt(fApfloat));
  }

  @Override
  public IExpr ceil() {
    return valueOf(h().ceil(fApfloat));
  }

  /** {@inheritDoc} */
  @Override
  public IInteger ceilFraction() {
    return F.ZZ(ApfloatMath.ceil(fApfloat).toBigInteger());
  }

  @Override
  public IExpr chebyshevT(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        try {
          Apfloat chebyshevT = h().chebyshevT(apfloatValue(), ((IReal) arg2).apfloatValue());
          return F.complexNum(chebyshevT);
        } catch (ArithmeticException | ApfloatRuntimeException are) {
          // java.lang.ArithmeticException: Result would be complex
        }
      }
      try {
        Apcomplex chebyshevT = h().chebyshevT(apfloatValue(), ((INumber) arg2).apcomplexValue());
        return F.complexNum(chebyshevT);
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.ChebyshevT, are);
      }
    }
    return INum.super.chebyshevT(arg2);
  }

  @Override
  public IExpr chebyshevU(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        try {
          Apfloat chebyshevU = h().chebyshevU(apfloatValue(), ((IReal) arg2).apfloatValue());
          return F.complexNum(chebyshevU);
        } catch (ArithmeticException | ApfloatRuntimeException are) {

        }
      }
      try {
        Apcomplex chebyshevU = h().chebyshevU(apfloatValue(), ((INumber) arg2).apcomplexValue());
        return F.complexNum(chebyshevU);
      } catch (ArithmeticException | ApfloatRuntimeException are) {
        return Errors.printMessage(S.ChebyshevU, are);
      }
    }
    return INum.super.chebyshevU(arg2);
  }

  /** {@inheritDoc} */
  @Override
  public int compareAbsValueToOne() {
    return h().abs(fApfloat).compareTo(Apfloat.ONE);
  }

  /**
   * The result of an operation of this number with a machine number: the lower precision governs,
   * so it is a machine number (<code>N[Pi,20]*2.</code>), or an arbitrary precision number of
   * machine precision if it lies beyond the double range (<code>N[10^400,20]*2.</code>).
   */
  private IInexactNumber machineResult(INum result) {
    if (EvalEngine.get().isArbitraryMode()) {
      return result;
    }
    double d = result.doubleValue();
    if (Double.isFinite(d)
        && (d == 0.0 ? result.apfloatValue().signum() == 0 : Math.abs(d) >= Double.MIN_NORMAL)) {
      return Num.valueOf(d);
    }
    Apfloat value = result.apfloatValue();
    return value.precision() > ParserConfig.MACHINE_PRECISION
        ? valueOf(value.precision(ParserConfig.MACHINE_PRECISION))
        : result;
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {

    if (expr.isNumber()) {
      if (expr.isReal()) {
        return RealNumberComparison.order(this, (IReal) expr);
      }
      int c = this.compareTo(((INumber) expr).re());
      if (c != 0) {
        return c;
      }

    }
    return IExpr.compareHierarchy(this, expr);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr complexArg() {
    try {
      return F.num(h().arg(fApfloat));
    } catch (ArithmeticException | NumericComputationException ex) {
      // Indeterminate expression `1` encountered.
      Errors.printMessage(S.Arg, "indet", F.list(F.Arg(this)), EvalEngine.get());
      return S.Indeterminate;
    }
  }

  @Override
  public ComplexNum complexNumValue() {
    return ComplexNum.valueOf(fApfloat.doubleValue());
  }

  /** {@inheritDoc} */
  @Override
  public int complexSign() {
    return fApfloat.signum();
  }

  @Override
  public IExpr copy() {
    return this;
  }

  @Override
  public IExpr copySign(double d) {
    return valueOf(h().copySign(fApfloat, apfloatOf(d)));
  }

  @Override
  public ApfloatNum cos() {
    PrecisionLoss.periodic(fApfloat);
    return valueOf(h().cos(fApfloat));
  }

  @Override
  public ApfloatNum cosh() {
    return valueOf(h().cosh(fApfloat));
  }

  @Override
  public IExpr coshIntegral() {
    try {
      if (isNonNegativeResult()) {
        return valueOf(h().coshIntegral(fApfloat));
      }
    } catch (ArithmeticException aex) {
      // java.lang.ArithmeticException: Result would be complex
    }
    Apcomplex coshIntegral = h().coshIntegral(apcomplexValue());
    return F.complexNum(coshIntegral);
  }

  @Override
  public IExpr cosIntegral() {
    try {
      if (isNonNegativeResult()) {
        return valueOf(h().cosIntegral(fApfloat));
      }
    } catch (ArithmeticException aex) {
      // java.lang.ArithmeticException: Result would be complex
    }
    Apcomplex cosIntegral = h().cosIntegral(apcomplexValue());
    return F.complexNum(cosIntegral);
  }

  // public Apfloat apfloatValue() {
  // return fApfloat;
  // }

  @Override
  public IInexactNumber dawsonF() {
    try {
      return valueOf(
          org.matheclipse.external.apfloat.ApfloatDawsonMath.dawsonF(fApfloat));
    } catch (ApfloatArithmeticException aae) {
      //
    }
    Apcomplex dawsonF = h().dawsonF(apcomplexValue());
    return F.complexNum(dawsonF);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr dec() {
    return add(F.CND1);
  }

  /** {@inheritDoc} */
  @Override
  public long determinePrecision(boolean postParserProcessing) {
    return precision();
  }

  @Override
  public IExpr digamma() {
    try {
      return valueOf(h().digamma(fApfloat));
    } catch (ArithmeticException | NumericComputationException aex) {
    }
    return INum.super.digamma();
  }

  @Override
  public ApfloatNum divide(double value) {
    return valueOf(h().divide(fApfloat, apfloatOf(value)));
  }

  @Override
  public IExpr divide(final IExpr that) {
    if (that instanceof ApfloatNum) {
      return divide((ApfloatNum) that);
    }
    if (that instanceof Num) {
      return divide(((Num) that).getRealPart());
    }
    if (that instanceof ApcomplexNum) {
      return ApcomplexNum.valueOf(fApfloat).divide((ApcomplexNum) that);
    }
    if (that instanceof ComplexNum) {
      return ApcomplexNum.valueOf(fApfloat)
          .divide(ApcomplexNum.valueOf(((ComplexNum) that).apcomplexValue()));
    }
    return INum.super.divide(that);
  }

  @Override
  public INum divide(final INum value) {
    return valueOf(h().divide(fApfloat, value.apfloatValue()));
  }

  @Override
  public IReal divideBy(IReal that) {
    return valueOf(h().divide(fApfloat, that.apfloatValue()));
  }

  /** @return */
  @Override
  public double doubleValue() {
    return fApfloat.doubleValue();
  }

  @Override
  public IExpr ellipticE() {
    try {
      return valueOf(h().ellipticE(fApfloat));
    } catch (ArithmeticException aex) {
      // java.lang.ArithmeticException: Result would be complex
    }
    return F.complexNum(h().ellipticE(apcomplexValue()));
  }

  @Override
  public IExpr ellipticK() {
    try {
      return valueOf(h().ellipticK(fApfloat));
    } catch (ArithmeticException aex) {
      // java.lang.ArithmeticException: Result would be complex
    }
    return F.complexNum(ApcomplexEllipticMath.ellipticK(apcomplexValue()));
  }

  @Override
  public boolean equals(final Object arg0) {
    if (this == arg0) {
      return true;
    }
    if (arg0 instanceof ApfloatNum) {
      return fApfloat.equals(((ApfloatNum) arg0).fApfloat);
    }
    return false;
  }

  @Override
  public boolean equalsInt(final int i) {
    try {
      return fApfloat.intValueExact() == i;
    } catch (RuntimeException rex) {
      // ArithmeticException
      Errors.rethrowsInterruptException(rex);
    }
    return false;
  }

  @Override
  public IExpr erf() {
    return valueOf(h().erf(fApfloat));
    // FixedPrecisionApfloatHelper h = h();
    // try {
    // Apfloat erf = erf(fApfloat, h);
    // return F.num(erf);
    // } catch (Exception ce) {
    // //
    // }
    // return F.NIL;
  }

  @Override
  public IExpr erfc() {
    try {
      return valueOf(h().erfc(fApfloat));
    } catch (OverflowException of) {
      // return Underflow? https://github.com/mtommila/apfloat/issues/38
      return F.Overflow();
    } catch (ArithmeticException | NumericComputationException e) {
      e.printStackTrace();
    }
    // FixedPrecisionApfloatHelper h = h();
    // try {
    // Apfloat c = erf(fApfloat, h);
    // return F.num(h.subtract(Apcomplex.ONE, c));
    // } catch (Exception ce) {
    // //
    // }
    return INum.super.erfc();
  }

  @Override
  public IExpr erfi() {
    return valueOf(h().erfi(fApfloat));
  }

  @Override
  public INumber evalNumber() {
    return this;
  }

  @Override
  public IReal evalReal() {
    return this;
  }

  @Override
  public IExpr evaluate(EvalEngine engine) {
    final long precision = fApfloat.precision();
    if (precision != Apfloat.INFINITE && engine.getNumericPrecision() < precision
        && engine.isNumericMode()) {
      // Only when the conversion actually lowers the precision. It does not always reach the
      // engine's: converting a 30 digit value here answers one of 255 digits, so the test above
      // stays true for ever and evalLoop() is told the expression changed on every pass. That is
      // an endless loop rather than a wrong answer - UnitStep(-0.8`30 + 1.2`30*I) never returned.
      ApfloatNum reduced = valueOf(h().valueOf(fApfloat));
      if (reduced.fApfloat.precision() < precision) {
        return reduced;
      }
    }
    return F.NIL;
  }

  @Override
  public ApfloatNum exp() {
    return valueOf(h().exp(fApfloat));
  }

  @Override
  public IExpr expIntegralE(IExpr z) {
    if (z instanceof IReal) {
      try {
        return valueOf(h().expIntegralE(fApfloat, ((IReal) z).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (z instanceof INumber) {
      try {
        return F.complexNum(h().expIntegralE(fApfloat, ((INumber) z).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        return Errors.printMessage(S.ExpIntegralE, e);
      }
    }
    return INum.super.expIntegralE(z);
  }

  @Override
  public IExpr expIntegralEi() {
    try {
      return valueOf(h().expIntegralEi(fApfloat));
    } catch (ArithmeticException | NumericComputationException e) {
      return Errors.printMessage(S.ExpIntegralEi, e);
    }
  }

  @Override
  public IExpr expm1() {
    FixedPrecisionApfloatHelper h = h();
    return valueOf(h.subtract(h.exp(fApfloat), Apfloat.ONE));
  }

  @Override
  public IExpr factorial() {
    try {
      FixedPrecisionApfloatHelper h = h();
      return valueOf(gammaOfLarge(h.add(fApfloat, Apfloat.ONE), h));
    } catch (ArithmeticException | NumericComputationException e) {
      // try as computation with complex numbers
    }
    try {
      return F.complexNum(h().gamma(apcomplexValue().add(Apfloat.ONE)));
    } catch (ApfloatArithmeticException aaex) {
      if ("gamma.ofZero".equals(aaex.getLocalizationKey())) {
        return F.ComplexInfinity;
      }
      if ("gamma.ofNegativeInteger".equals(aaex.getLocalizationKey())) {
        return F.ComplexInfinity;
      }
    }
    return INum.super.factorial();
  }

  @Override
  public IExpr fibonacci(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(h().fibonacci(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(h().fibonacci(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.fibonacci(arg2);
  }

  @Override
  public IExpr floor() {
    return valueOf(h().floor(fApfloat));
  }

  /** {@inheritDoc} */
  @Override
  public IInteger floorFraction() {
    return F.ZZ(ApfloatMath.floor(fApfloat).toBigInteger());
  }

  /** {@inheritDoc} */
  @Override
  public IReal fractionalPart() {
    return F.num(fApfloat.frac());
  }

  @Override
  public IExpr fresnelC() {
    return valueOf(h().fresnelC(fApfloat));
  }

  @Override
  public IExpr fresnelS() {
    return valueOf(h().fresnelS(fApfloat));
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    return fullFormString(fApfloat);
  }

  /**
   * Gamma of a real number. The relative error of a large argument is multiplied by about
   * <code>x*Log(x)</code>: the digits which the argument has before its radix point are added to
   * the working precision, with the argument as the exact number it reads -
   * <code>N(Gamma(10^6), 30)</code> had 24 digits.
   */
  private static Apfloat gammaOfLarge(Apfloat x, FixedPrecisionApfloatHelper h) {
    final long scale = x.scale();
    final long precision = h.precision();
    if (scale <= 1 || precision >= Apfloat.INFINITE - scale - 5) {
      return h.gamma(x);
    }
    return ApfloatMath.gamma(x.precision(precision + scale + 5)).precision(precision);
  }

  @Override
  public IExpr gamma() {
    // the pole at zero itself, not a number below the tolerance of isZero()
    if (fApfloat.signum() == 0 || isMathematicalIntegerNegative()) {
      return F.CComplexInfinity;
    }
    try {
      return valueOf(gammaOfLarge(fApfloat, h()));
    } catch (OverflowException of) {
      return F.Overflow();
    } catch (ArithmeticException | NumericComputationException are) {
      return Errors.printMessage(S.Gamma, are, EvalEngine.get());
    }
    // return INum.super.gamma();
  }

  @Override
  public IExpr gamma(IExpr x) {
    if (isZero() && x.isZero()) {
      return F.CInfinity;
    }
    if (x instanceof IReal) {
      if (!(x.isNegative() && !(isMathematicalIntegerNonNegative() && !isZero()))) {
        try {
          return valueOf(h().gamma(fApfloat, ((IReal) x).apfloatValue()));
        } catch (ArithmeticException | NumericComputationException e) {
          // try as computation with complex numbers
        }
      }
    }
    if (x instanceof INumber) {
      try {
        return F.complexNum(h().gamma(fApfloat, ((INumber) x).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException are) {
        // try as computation with complex numbers
        // java.lang.ArithmeticException: Upper gamma with first argument real part nonpositive and
        // second argment zero
        return Errors.printMessage(S.Gamma, are, EvalEngine.get());
      }
    }
    return INum.super.gamma(x);
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
    if (x0 instanceof IReal && x1 instanceof IReal) {
      try {
        return valueOf(
            h().gamma(fApfloat, ((IReal) x0).apfloatValue(), ((IReal) x1).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (x0 instanceof INumber && x1 instanceof INumber) {
      try {
        return F.complexNum(
            h().gamma(fApfloat, ((INumber) x0).apcomplexValue(), ((INumber) x1).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException are) {
        return Errors.printMessage(S.Gamma, are, EvalEngine.get());
      }
    }
    return INum.super.gamma(x0, x1);
  }

  @Override
  public IExpr gegenbauerC(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(h().gegenbauerC(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(h().gegenbauerC(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.gegenbauerC(arg2);
  }

  @Override
  public IExpr gegenbauerC(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(h().gegenbauerC(fApfloat, ((IReal) arg2).apfloatValue(),
            ((IReal) arg3).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(h().gegenbauerC(fApfloat, ((INumber) arg2).apcomplexValue(),
            ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.gegenbauerC(arg2, arg3);
  }

  @Override
  public IExpr getAddendum() {
    return isFinite() ? valueOf(fApfloat.subtract(fApfloat.real())) : zero();
  }

  @Override
  public ApfloatNum getPi() {
    return valueOf(h().pi());
  }

  @Override
  public double getReal() {
    return fApfloat.doubleValue();
  }

  @Override
  public double getRealPart() {
    double temp = fApfloat.doubleValue();
    if (temp == (-0.0)) {
      temp = 0.0;
    }
    return temp;
  }

  @Override
  public IExpr harmonicNumber() {
    Apfloat harmonicNumber = h().harmonicNumber(apfloatValue());
    return F.num(harmonicNumber);
  }

  @Override
  public IExpr harmonicNumber(IExpr r) {
    if (r instanceof INumber) {
      if (r instanceof IReal) {
        if (this.isGE(F.C1) || r.isInteger()) {
          try {
            Apfloat harmonicNumber = h().harmonicNumber(fApfloat, ((IReal) r).apfloatValue());
            return F.num(harmonicNumber);
          } catch (ArithmeticException | NumericComputationException e) {
            // try as computation with complex numbers
          }
        }
      }
      try {
        Apcomplex harmonicNumber = h().harmonicNumber(fApfloat, ((INumber) r).apcomplexValue());
        return F.complexNum(harmonicNumber);
      } catch (ArithmeticException | NumericComputationException e) {
      }
    }
    return INum.super.harmonicNumber(r);
  }

  @Override
  public final int hashCode() {
    return fApfloat.hashCode();
  }

  @Override
  public ISymbol head() {
    return S.Real;
  }

  @Override
  public IExpr hermiteH(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal) {
        try {
          Apfloat hermiteH = h().hermiteH(fApfloat, ((IReal) arg2).apfloatValue());
          return F.num(hermiteH);
        } catch (ArithmeticException | NumericComputationException are) {

        }
      }
      try {
        Apcomplex hermiteH = h().hermiteH(fApfloat, ((INumber) arg2).apcomplexValue());
        return F.complexNum(hermiteH);
      } catch (ArithmeticException | NumericComputationException are) {

      }
    }
    return INum.super.hermiteH(arg2);
  }


  @Override
  public int hierarchy() {
    return DOUBLEID;
  }

  @Override
  public IExpr hypergeometric0F1(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(h().hypergeometric0F1(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      return F.complexNum(h().hypergeometric0F1(fApfloat, ((INumber) arg2).apcomplexValue()));
    }
    return INum.super.hypergeometric0F1(arg2);
  }

  @Override
  public IExpr hypergeometric0F1Regularized(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(h().hypergeometric0F1Regularized(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      return F.complexNum(
          h().hypergeometric0F1Regularized(fApfloat, ((INumber) arg2).apcomplexValue()));
    }
    return INum.super.hypergeometric0F1Regularized(arg2);
  }

  @Override
  public IExpr hypergeometric1F1(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(h().hypergeometric1F1(fApfloat, ((IReal) arg2).apfloatValue(),
            ((IReal) arg3).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(h().hypergeometric1F1(fApfloat, ((INumber) arg2).apcomplexValue(),
            ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | ApfloatRuntimeException e) {
        return Errors.printMessage(S.Hypergeometric1F1, e);
      }
    }
    return INum.super.hypergeometric1F1(arg2, arg3);
  }

  @Override
  public IExpr hypergeometric1F1Regularized(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(h().hypergeometric1F1Regularized(fApfloat, ((IReal) arg2).apfloatValue(),
            ((IReal) arg3).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      return F.complexNum(h().hypergeometric1F1Regularized(fApfloat,
          ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue()));
    }
    return INum.super.hypergeometric1F1Regularized(arg2, arg3);
  }

  @Override
  public IExpr hypergeometric2F1(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof IReal && arg3 instanceof IReal && arg4 instanceof IReal) {
      try {
        return valueOf(h().hypergeometric2F1(fApfloat, ((IReal) arg2).apfloatValue(),
            ((IReal) arg3).apfloatValue(), ((IReal) arg4).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        return F.complexNum(h().hypergeometric2F1(fApfloat, ((INumber) arg2).apcomplexValue(),
            ((INumber) arg3).apcomplexValue(), ((INumber) arg4).apcomplexValue()));
      } catch (ApfloatArithmeticException aaex) {
        if ("divide.byZero".equals(aaex.getLocalizationKey())) {
          return F.ComplexInfinity;
        }
      } catch (ArithmeticException | NumericComputationException aex) {
        if (aex.getMessage().equals("Division by zero")) {
          return F.ComplexInfinity;
        }
      }
    }
    return INum.super.hypergeometric2F1(arg2, arg3, arg4);
  }

  @Override
  public IExpr hypergeometric2F1Regularized(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof IReal && arg3 instanceof IReal && arg4 instanceof IReal) {
      try {
        return valueOf(h().hypergeometric2F1Regularized(fApfloat, ((IReal) arg2).apfloatValue(),
            ((IReal) arg3).apfloatValue(), ((IReal) arg4).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        return F.complexNum(
            h().hypergeometric2F1Regularized(fApfloat, ((INumber) arg2).apcomplexValue(),
                ((INumber) arg3).apcomplexValue(), ((INumber) arg4).apcomplexValue()));
      } catch (NumericComputationException ex) {
        // org.apfloat.OverflowException: Apfloat disk file storage is disabled
      }
    }
    return INum.super.hypergeometric2F1Regularized(arg2, arg3, arg4);
  }

  @Override
  public IExpr hypergeometricU(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(h().hypergeometricU(fApfloat, ((IReal) arg2).apfloatValue(),
            ((IReal) arg3).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      Apcomplex u = HypergeometricUWorkaround.hypergeometricU(h(), fApfloat,
          ((INumber) arg2).apcomplexValue(), ((INumber) arg3).apcomplexValue());
      return u.imag().signum() == 0 && arg2.isReal() && arg3.isReal() ? valueOf(u.real())
          : F.complexNum(u);
    }
    return INum.super.hypergeometricU(arg2, arg3);
  }

  /** {@inheritDoc} */
  @Override
  public IReal im() {
    return F.CD0;
  }

  @Override
  public double imDoubleValue() {
    return 0.0;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr inc() {
    return add(F.CD1);
  }

  /** {@inheritDoc} */
  @Override
  public IInteger integerPart() {
    return isNegative() ? ceilFraction() : floorFraction();
  }

  /** @return */
  @Override
  public int intValue() {
    return fApfloat.intValue();
  }

  @Override
  public ApfloatNum inverse() {
    if (isOne()) {
      return this;
    }
    return valueOf(h().inverseRoot(fApfloat, 1));
  }

  @Override
  public IExpr inverseErf() {
    if (MINUS_ONE.compareTo(fApfloat) == -1 && fApfloat.compareTo(Apint.ONE) == -1) {
      return valueOf(h().inverseErf(fApfloat));
    }
    return INum.super.inverseErf();
  }

  @Override
  public IExpr inverseErfc() {
    if (Apint.ZERO.compareTo(fApfloat) == -1 && fApfloat.compareTo(TWO) == -1) {
      // 0.0 < fApfloat && fApfloat < 2.0
      return valueOf(h().inverseErfc(fApfloat));
    }
    return INum.super.inverseErfc();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isE() {
    return fApfloat.compareTo(h().exp(Apfloat.ONE)) == 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMathematicalIntegerNegative() {
    return (fApfloat.isInteger() && fApfloat.signum() < 0);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMathematicalIntegerNonNegative() {
    return (fApfloat.isInteger() && fApfloat.signum() >= 0);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMinusOne() {
    return fApfloat.compareTo(ApfloatNum.MINUS_ONE) == 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNegative() {
    return fApfloat.signum() == -1;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNonNegative() {
    return fApfloat.signum() >= 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumEqualInteger(IInteger ii) throws ArithmeticException {
    return fApfloat.truncate().equals(new Apint(ii.toBigNumerator()))
        && fApfloat.frac().equals(Apfloat.ZERO);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumEqualRational(IRational value) throws ArithmeticException {
    long precision = fApfloat.precision();
    return fApfloat.equals(new Apfloat(value.toBigNumerator(), precision)
        .divide(new Apfloat(value.toBigDenominator(), precision)));
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumIntValue() {
    return fApfloat.frac().equals(Apfloat.ZERO);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isOne() {
    return fApfloat.compareTo(Apfloat.ONE) == 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPi() {
    return fApfloat.compareTo(h().pi()) == 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isPositive() {
    return fApfloat.signum() > 0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRationalValue(IRational value) {
    return fApfloat.compareTo(value.apfloatNumValue().fApfloat) == 0;
  }

  @Override
  public boolean isSame(IExpr expression, double epsilon) {
    if (expression instanceof ApfloatNum) {
      return fApfloat.compareTo(((ApfloatNum) expression).fApfloat) == 0;
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isZero() {
    return fApfloat.signum() == 0;
  }

  @Override
  public boolean isFuzzyZero() {
    return F.isZero(fApfloat);
  }

  @Override
  public boolean isZero(double tolerance) {
    return F.isZero(fApfloat, tolerance);
  }

  @Override
  public IExpr jacobiP(IExpr arg2, IExpr arg3, IExpr arg4) {
    if (arg2 instanceof IReal && arg3 instanceof IReal && arg4 instanceof IReal) {
      try {
        return valueOf(h().jacobiP(fApfloat, ((IReal) arg2).apfloatValue(),
            ((IReal) arg3).apfloatValue(), ((IReal) arg4).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber && arg4 instanceof INumber) {
      try {
        return F.complexNum(h().jacobiP(fApfloat, ((INumber) arg2).apcomplexValue(),
            ((INumber) arg3).apcomplexValue(), ((INumber) arg4).apcomplexValue()));
      } catch (OverflowException of) {
        return F.Overflow();
      } catch (ArithmeticException | NumericComputationException e) {
        //
      }
    }
    return INum.super.jacobiP(arg2, arg3, arg4);
  }

  @Override
  public IExpr laguerreL(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(h().laguerreL(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(h().laguerreL(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.laguerreL(arg2);
  }

  @Override
  public IExpr laguerreL(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(
            h().laguerreL(fApfloat, ((IReal) arg2).apfloatValue(), ((IReal) arg3).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(h().laguerreL(fApfloat, ((INumber) arg2).apcomplexValue(),
            ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.laguerreL(arg2, arg3);
  }

  @Override
  public long leafCountSimplify() {
    return 2;
  }

  @Override
  public IExpr legendreP(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(h().legendreP(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(h().legendreP(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.legendreP(arg2);
  }

  @Override
  public IExpr legendreP(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(
            h().legendreP(fApfloat, ((IReal) arg2).apfloatValue(), ((IReal) arg3).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(h().legendreP(fApfloat, ((INumber) arg2).apcomplexValue(),
            ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.legendreP(arg2, arg3);
  }

  @Override
  public IExpr legendreQ(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(h().legendreQ(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(h().legendreQ(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.legendreQ(arg2);
  }

  @Override
  public IExpr legendreQ(IExpr arg2, IExpr arg3) {
    if (arg2 instanceof IReal && arg3 instanceof IReal) {
      try {
        return valueOf(
            h().legendreQ(fApfloat, ((IReal) arg2).apfloatValue(), ((IReal) arg3).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber && arg3 instanceof INumber) {
      try {
        return F.complexNum(h().legendreQ(fApfloat, ((INumber) arg2).apcomplexValue(),
            ((INumber) arg3).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.legendreQ(arg2, arg3);
  }

  @Override
  public IInexactNumber barnesG() {
    try {
      return valueOf(h().barnesG(fApfloat));
    } catch (ApfloatArithmeticException aae) {
      //
    }
    Apcomplex barnesG = h().barnesG(apcomplexValue());
    return F.complexNum(barnesG);
  }

  @Override
  public IInexactNumber logBarnesG() {
    try {
      return valueOf(h().logBarnesG(fApfloat));
    } catch (ApfloatArithmeticException aae) {
      //
    }
    Apcomplex logBarnesG = h().logBarnesG(apcomplexValue());
    return F.complexNum(logBarnesG);
  }

  @Override
  public IInexactNumber log() {
    if (isNegative()) {
      return ApcomplexNum.valueOf(h().log(apcomplexValue()));
    }
    return valueOf(h().log(fApfloat));
  }

  @Override
  public IExpr log(final IExpr base) {
    if (base instanceof INumber) {
      if (base.isZero()) {
        return S.Indeterminate;
      }
      try {
        if (base instanceof IReal) {
          if (isNegative()) {
            return ApcomplexNum
                .valueOf(h().log(apcomplexValue(), ((INumber) base).apcomplexValue()));
          }
          return valueOf(h().log(fApfloat, ((IReal) base).apfloatValue()));
        }
        return ApcomplexNum.valueOf(h().log(apcomplexValue(), ((INumber) base).apcomplexValue()));
      } catch (ApfloatArithmeticException aex) {
        if (aex.getLocalizationKey().equals("divide.byZero")) {
          // log(x,0) is undefined
          return F.CComplexInfinity;
        }
      }
    }
    return INum.super.log(base);
  }

  @Override
  public ApfloatNum log10() {
    FixedPrecisionApfloatHelper h = h();
    return valueOf(h.log(fApfloat, new Apfloat(10)));
  }

  @Override
  public ApfloatNum log1p() {
    FixedPrecisionApfloatHelper h = h();
    return valueOf(h.log(h.add(fApfloat, Apfloat.ONE)));
  }

  @Override
  public IExpr logGamma() {
    if (isPositive()) {
      try {
        return valueOf(h().logGamma(fApfloat));
      } catch (ArithmeticException | NumericComputationException ex) {
      }
    }
    try {
      Apcomplex logGamma = h().logGamma(apcomplexValue());
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
    return INum.super.logGamma();
  }

  @Override
  public IExpr logIntegral() {
    try {
      if (isNonNegativeResult()) {
        return valueOf(h().logIntegral(fApfloat));
      }
    } catch (ArithmeticException | NumericComputationException ex) {
      // java.lang.ArithmeticException: Result would be complex
    }
    Apcomplex logIntegral = h().logIntegral(apcomplexValue());
    return F.complexNum(logIntegral);
  }

  @Override
  public IExpr logisticSigmoid() {
    try {
      return valueOf(h().logisticSigmoid(fApfloat));
    } catch (NumericComputationException ex) {
    }
    Apcomplex logisticSigmoid = h().logisticSigmoid(apcomplexValue());
    return F.complexNum(logisticSigmoid);
  }

  /** @return */
  public long longValue() {
    return fApfloat.longValue();
  }

  @Override
  public ApfloatNum multiply(double value) {
    return valueOf(h().multiply(fApfloat, apfloatOf(value)));
  }

  @Override
  public IExpr multiply(int value) {
    return valueOf(h().multiply(fApfloat, apfloatOf(value)));
  }

  @Override
  public INum multiply(final INum value) {
    return valueOf(h().multiply(fApfloat, value.apfloatValue()));
  }

  @Override
  public IReal multiply(IReal value) {
    // Use the same arbitrary-precision path regardless of the operand's static type.
    return (IReal) times((INumber) value);
  }

  /** @return */
  @Override
  public ApfloatNum negate() {
    // Not through h(): that helper works at Config.MAX_PRECISION_APFLOAT - 1,
    // so it raised the precision of a sign flip from 30 to 255. Negation cannot gain or lose
    // significant digits, and Apfloat#negate() keeps the ones it has. Reparsing a printed number
    // depended on this: -3.1415`30 came back as -3.1415`255.
    return valueOf(fApfloat.negate());
  }

  @Override
  public ApfloatNum newInstance(double d) {
    return valueOf(d);
  }

  @Override
  public Num numValue() {
    return Num.valueOf(doubleValue());
  }

  @Override
  public INum numericValue() {
    return this;
  }

  @Override
  public ApfloatNum one() {
    return valueOf(Apfloat.ONE);
  }

  /** @return */
  @Override
  public ApfloatNum opposite() {
    return valueOf(h().negate(fApfloat));
  }

  @Override
  public IExpr plus(final IExpr that) {
    if (that instanceof INumber) {
      return plus((INumber) that);
    }
    return INum.super.plus(that);
  }

  @Override
  public IInexactNumber plus(final IInexactNumber that) {
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        return add((ApfloatNum) that);
      }
      return machineResult(add(ApfloatNum.valueOf(((Num) that).getRealPart())));
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return ApcomplexNum.valueOf(fApfloat).add((ApcomplexNum) that);
      }
      ComplexNum cn = (ComplexNum) that;
      return ApcomplexNum.valueOf(fApfloat)
          .add(ApcomplexNum.valueOf(cn.getRealPart(), cn.getImaginaryPart()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public INumber plus(final INumber that) {
    if (that instanceof IInexactNumber) {
      return plus((IInexactNumber) that);
    }
    if (that instanceof IReal) {
      return ApfloatNum.valueOf(sum(((IReal) that).apfloatValue()));
    }
    if (that instanceof ComplexSym) {
      final Apcomplex complex = that.apcomplexValue();
      return F.complexNum(PrecisionLoss.sum(fApfloat, complex, fApfloat.add(complex)));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IExpr pochhammer(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (ApcomplexNum.isPochhammerPole(apfloatValue(), ((INumber) arg2).apcomplexValue())) {
        return F.CComplexInfinity;
      }
      // with the digits which a large first argument takes from the gamma functions
      Apcomplex pochhammer = pochhammer(apfloatValue(), ((INumber) arg2).apcomplexValue(),
          h().precision());
      return pochhammer.imag().signum() == 0 ? F.num(pochhammer.real()) : F.complexNum(pochhammer);
    }
    return INum.super.pochhammer(arg2);
  }

  @Override
  public IExpr polyGamma(long n) {
    try {
      Apfloat polygamma = h().polygamma(n, fApfloat);
      return F.num(polygamma);
    } catch (ApfloatArithmeticException aaex) {
      if ("polygamma.ofNonpositiveInteger".equals(aaex.getLocalizationKey())) {
        return F.ComplexInfinity;
      }
    } catch (ArithmeticException | NumericComputationException aex) {
      // java.lang.ArithmeticException: Polygamma of non-positive integer
    }
    try {
      Apcomplex polygamma = h().polygamma(n, apcomplexValue());
      return F.complexNum(polygamma);
    } catch (ApfloatArithmeticException aaex) {
      if ("polygamma.ofNonpositiveInteger".equals(aaex.getLocalizationKey())) {
        return F.ComplexInfinity;
      }
    } catch (ArithmeticException | NumericComputationException aex) {
      // java.lang.ArithmeticException: Polygamma of nonpositive integer
    }
    return INum.super.polyGamma(n);
  }

  @Override
  public IExpr polyLog(IExpr arg2) {
    if (arg2 instanceof INumber) {
      if (arg2 instanceof IReal && ((IReal) arg2).isLE(F.C1)) {
        try {
          return valueOf(h().polylog(fApfloat, ((IReal) arg2).apfloatValue()));
        } catch (ArithmeticException | NumericComputationException e) {
          // java.lang.ArithmeticException: Result would be complex
        }
      }

      try {
        return F.complexNum(h().polylog(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (LossOfPrecisionException lope) {
        // Complete loss of precision
      } catch (InfiniteExpansionException iee) {
        // Cannot calculate power to infinite precision
      } catch (ArithmeticException | NumericComputationException ex) {
      }
    }
    return INum.super.polyLog(arg2);
  }

  @Override
  public ApfloatNum pow(double value) {
    return valueOf(h().pow(fApfloat, apfloatOf(value)));
  }

  @Override
  public ApfloatNum pow(int n) {
    if (n == (-1)) {
      return inverse();
    }
    return valueOf(h().pow(fApfloat, n));
  }

  @Override
  public INum pow(final INum value) {
    return valueOf(pow(fApfloat, value.apfloatValue(), h()));
  }

  /**
   * A power with a real exponent. An integer exponent - which arrives as an inexact number from
   * <code>N(10^6, 30)</code> - is a product, not <code>Exp(n*Log(x))</code>, whose last digit is
   * cut: <code>N(10, 30)^6</code> was <code>999999.999...</code>.
   */
  private static Apfloat pow(Apfloat base, Apfloat exponent, FixedPrecisionApfloatHelper h) {
    if (exponent.scale() <= 18 && exponent.isInteger()) {
      final long n = exponent.longValue();
      final long precision = h.precision();
      if (Math.abs(n) < 1000 || base.precision() == Apfloat.INFINITE) {
        return h.pow(base, n);
      }
      // the relative error of the base is multiplied by n: digits for that, with the base as the
      // exact number it reads
      return ApfloatMath.pow(base.precision(precision + exponent.scale() + 3), n)
          .precision(precision);
    }
    return h.pow(base, exponent);
  }

  @Override
  public IExpr power(final IExpr that) {
    if (that instanceof IComplexNum) {
      ApcomplexNum.periodicPower(fApfloat, ((IComplexNum) that).apcomplexValue());
      return F.complexNum(h().pow(fApfloat, ((IComplexNum) that).apcomplexValue()));
    }
    if (that instanceof INum) {
      final Apfloat exponent = ((INum) that).apfloatValue();
      if (fApfloat.compareTo(Apfloat.ZERO) < 0 && !(exponent.scale() <= 18 && exponent.isInteger())) {
        return F.complexNum(h().pow(fApfloat, ((INum) that).apcomplexValue()));
      }
      return valueOf(pow(fApfloat, exponent, h()));
    }
    return INum.super.power(that);
  }

  @Override
  public ApfloatNum power(long n) {
    if (n == (-1L)) {
      return inverse();
    }
    return valueOf(h().pow(fApfloat, n));
  }

  @Override
  public long precision() throws NumericComputationException {
    return fApfloat.precision();
  }

  /** {@inheritDoc} */
  @Override
  public IReal re() {
    return this;
  }

  @Override
  public ApfloatNum reciprocal() {
    return valueOf(h().inverseRoot(fApfloat, 1));
  }

  @Override
  public double reDoubleValue() {
    return doubleValue();
  }

  @Override
  public ApfloatNum remainder(double value) {
    return valueOf(h().mod(fApfloat, apfloatOf(value)));
  }

  @Override
  public ApfloatNum rint() {
    return valueOf(apfloatRint(fApfloat));
  }

  @Override
  public ApfloatNum rootN(int n) {
    return valueOf(h().root(fApfloat, n));
  }

  @Override
  public IReal roundClosest(IReal multiple) {
    throw new ArithmeticException("Apfloat: Round closest not implemented");
    // final long precision = precision();
    // Apfloat factor = multiple.apfloatNumValue(precision).fApfloat;
    // return F.num(h().round(fApfloat.divide(factor), precision,
    // RoundingMode.HALF_EVEN).multiply(factor));
  }

  @Override
  public IInteger roundExpr() {
    BigInteger round = NumberUtil.round(fApfloat, Config.ROUNDING_MODE);
    return F.ZZ(round);
  }

  @Override
  public ApfloatNum scalb(int n) {
    FixedPrecisionApfloatHelper h = h();
    return valueOf(h.multiply(fApfloat, h.pow(new Apfloat(2), n)));
  }

  @Override
  public ApfloatNum sign() {
    if (isNaN() || isZero()) {
      return this;
    }
    return valueOf(h().abs(fApfloat));
  }

  @Override
  public ApfloatNum sin() {
    PrecisionLoss.periodic(fApfloat);
    return valueOf(h().sin(fApfloat));
  }

  @Override
  public ApfloatNum sinc() {
    return valueOf(h().sinc(fApfloat));
  }

  @Override
  public ApfloatNum sinh() {
    return valueOf(h().sinh(fApfloat));
  }

  @Override
  public IExpr sinhIntegral() {
    return valueOf(h().sinhIntegral(fApfloat));
  }

  @Override
  public IExpr sinIntegral() {
    valueOf(h().sinIntegral(fApfloat));
    return valueOf(h().sinIntegral(fApfloat));
  }

  @Override
  public IExpr sqr() {
    return this.multiply(this);
  }

  @Override
  public IExpr sqrt() {
    if (isNegative()) {
      return F.complexNum(h().sqrt(apcomplexValue()));
    }
    return valueOf(h().sqrt(fApfloat));
  }

  @Override
  public IExpr struveH(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(h().struveH(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(h().struveH(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.struveH(arg2);
  }

  @Override
  public IExpr struveL(IExpr arg2) {
    if (arg2 instanceof IReal) {
      try {
        return valueOf(h().struveL(fApfloat, ((IReal) arg2).apfloatValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    if (arg2 instanceof INumber) {
      try {
        return F.complexNum(h().struveL(fApfloat, ((INumber) arg2).apcomplexValue()));
      } catch (ArithmeticException | NumericComputationException e) {
        // try as computation with complex numbers
      }
    }
    return INum.super.struveL(arg2);
  }

  @Override
  public ApfloatNum subtract(double value) {
    return valueOf(difference(apfloatOf(value)));
  }

  @Override
  public IExpr subtract(final IExpr that) {
    if (that instanceof ApfloatNum) {
      return subtract((ApfloatNum) that);
    }
    if (that instanceof Num) {
      return subtract(((Num) that).getRealPart());
    }
    if (that instanceof ApcomplexNum) {
      return F.complexNum(difference(((ApcomplexNum) that).apcomplexValue()));
    }
    if (that instanceof ComplexNum) {
      return F.complexNum(difference(((ComplexNum) that).apcomplexValue()));
    }
    return INum.super.subtract(that);
  }

  @Override
  public INum subtract(final INum value) {
    return valueOf(difference(value.apfloatValue()));
  }

  @Override
  public IReal subtractFrom(IReal that) {
    return valueOf(difference(that.apfloatValue()));
  }

  /**
   * A reciprocal trigonometric or hyperbolic function, or the inverse of one: the real value, the
   * complex one where the function of a real argument is not real, and <code>ComplexInfinity
   * </code> at a pole.
   */
  private static IExpr reciprocalTrig(UnaryOperator<Apfloat> real,
      UnaryOperator<Apcomplex> complex, Apfloat x) {
    try {
      return F.num(real.apply(x));
    } catch (ArithmeticException ae) {
      // the result would be complex, or a pole
      return ApcomplexNum.reciprocalTrig(complex, x);
    }
  }

  @Override
  public IExpr cot() {
    PrecisionLoss.periodic(fApfloat);
    return reciprocalTrig(ApfloatTrigMath::cot, ApcomplexTrigMath::cot, fApfloat);
  }

  @Override
  public IExpr sec() {
    PrecisionLoss.periodic(fApfloat);
    return reciprocalTrig(ApfloatTrigMath::sec, ApcomplexTrigMath::sec, fApfloat);
  }

  @Override
  public IExpr csc() {
    PrecisionLoss.periodic(fApfloat);
    return reciprocalTrig(ApfloatTrigMath::csc, ApcomplexTrigMath::csc, fApfloat);
  }

  @Override
  public IExpr coth() {
    return reciprocalTrig(ApfloatTrigMath::coth, ApcomplexTrigMath::coth, fApfloat);
  }

  @Override
  public IExpr sech() {
    return reciprocalTrig(ApfloatTrigMath::sech, ApcomplexTrigMath::sech, fApfloat);
  }

  @Override
  public IExpr csch() {
    return reciprocalTrig(ApfloatTrigMath::csch, ApcomplexTrigMath::csch, fApfloat);
  }

  @Override
  public IExpr acot() {
    final long precision = ApcomplexNum.zeroPrecision(fApfloat);
    return reciprocalTrig(x -> ApfloatTrigMath.acot(x, precision),
        z -> ApcomplexTrigMath.acot(z, precision), fApfloat);
  }

  @Override
  public IExpr asec() {
    final long precision = ApcomplexNum.zeroPrecision(fApfloat);
    return reciprocalTrig(x -> ApfloatTrigMath.asec(x, precision),
        z -> ApcomplexTrigMath.asec(z, precision), fApfloat);
  }

  @Override
  public IExpr acsc() {
    final long precision = ApcomplexNum.zeroPrecision(fApfloat);
    return reciprocalTrig(x -> ApfloatTrigMath.acsc(x, precision),
        z -> ApcomplexTrigMath.acsc(z, precision), fApfloat);
  }

  @Override
  public IExpr acoth() {
    final long precision = ApcomplexNum.zeroPrecision(fApfloat);
    return reciprocalTrig(x -> ApfloatTrigMath.acoth(x, precision),
        z -> ApcomplexTrigMath.acoth(z, precision), fApfloat);
  }

  @Override
  public IExpr asech() {
    if (fApfloat.signum() == 0) {
      // an inexact zero has no direction
      return S.Indeterminate;
    }
    final long precision = ApcomplexNum.zeroPrecision(fApfloat);
    return reciprocalTrig(x -> ApfloatTrigMath.asech(x, precision),
        z -> ApcomplexTrigMath.asech(z, precision), fApfloat);
  }

  @Override
  public IExpr acsch() {
    final long precision = ApcomplexNum.zeroPrecision(fApfloat);
    return reciprocalTrig(x -> ApfloatTrigMath.acsch(x, precision),
        z -> ApcomplexTrigMath.acsch(z, precision), fApfloat);
  }

  @Override
  public ApfloatNum tan() {
    PrecisionLoss.periodic(fApfloat);
    return valueOf(h().tan(fApfloat));
  }

  @Override
  public ApfloatNum tanh() {
    return valueOf(h().tanh(fApfloat));
  }

  @Override
  public IExpr times(final IExpr that) {
    if (that instanceof INumber) {
      return times((INumber) that);
    }
    return INum.super.times(that);
  }

  @Override
  public IInexactNumber times(final IInexactNumber that) {
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        return multiply((ApfloatNum) that);
      }
      return machineResult(multiply(ApfloatNum.valueOf(((Num) that).getRealPart())));
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return ApcomplexNum.valueOf(fApfloat).multiply((ApcomplexNum) that);
      }
      ComplexNum cn = (ComplexNum) that;
      return ApcomplexNum.valueOf(fApfloat)
          .multiply(ApcomplexNum.valueOf(cn.getRealPart(), cn.getImaginaryPart()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IInexactNumber times(final INumber that) {
    if (that instanceof IInexactNumber) {
      return times((IInexactNumber) that);
    }
    if (that instanceof IReal) {
      return ApfloatNum.valueOf(h().multiply(fApfloat, ((IReal) that).apfloatValue()));
    }
    if (that instanceof ComplexSym) {
      return F.complexNum(fApfloat.multiply(that.apcomplexValue()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public ApfloatNum toDegrees() {
    FixedPrecisionApfloatHelper h = h();
    // radians * (180 / Pi)
    return valueOf(toDegrees(fApfloat, h));
  }

  /** {@inheritDoc} */
  @Override
  public int toInt() throws ArithmeticException {
    return fApfloat.intValueExact();
  }

  /** {@inheritDoc} */
  @Override
  public int toIntDefault(int defaultValue) {
    try {
      return fApfloat.intValueExact();
    } catch (RuntimeException rex) {
      // ArithmeticException
      Errors.rethrowsInterruptException(rex);
    }
    return defaultValue;
  }

  /** {@inheritDoc} */
  @Override
  public long toLong() throws ArithmeticException {
    return fApfloat.longValueExact();
  }

  /** {@inheritDoc} */
  @Override
  public long toLongDefault(long defaultValue) {
    try {
      return fApfloat.longValueExact();
    } catch (RuntimeException rex) {
      // ArithmeticException
      Errors.rethrowsInterruptException(rex);
    }
    return defaultValue;
  }

  @Override
  public ApfloatNum toRadians() {
    FixedPrecisionApfloatHelper h = h();
    // degrees * (Pi / 180)
    return valueOf(toRadians(fApfloat, h));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    String str = fApfloat.toString();
    if (ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      return str.replace("e", "E");
    }
    long precision = fApfloat.precision();
    int index = str.indexOf('e');
    if (index > 0) {
      String exponentStr = str.substring(index + 1);
      String result = str.substring(0, index);
      return result + "`" + precision + "*10^" + exponentStr;
    }
    return str + "`" + precision;
  }

  @Override
  public ApfloatNum ulp() {
    return valueOf(h().ulp(Apfloat.ONE));
  }

  @Override
  public ApfloatNum zero() {
    return valueOf(Apfloat.ZERO);
  }
}
