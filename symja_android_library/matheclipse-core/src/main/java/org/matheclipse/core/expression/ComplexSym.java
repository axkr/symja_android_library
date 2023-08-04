package org.matheclipse.core.expression;

import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.S.Pi;
import java.math.BigInteger;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.fraction.BigFraction;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.BigIntegerLimitExceeded;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import org.matheclipse.parser.client.ParserConfig;

/** A symbolic complex number implementation */
public class ComplexSym implements IComplex {
  private static final Logger LOGGER = LogManager.getLogger();

  /** */
  private static final long serialVersionUID = 1489050560741527824L;

  private static final ComplexSym ZERO = ComplexSym.valueOf(0, 1, 0, 1);
  private static final ComplexSym MINUS_ONE = ComplexSym.valueOf(-1, 1, 0, 1);
  private static final ComplexSym ONE = ComplexSym.valueOf(1, 1, 0, 1);
  private static final ComplexSym POSITIVE_I = ComplexSym.valueOf(0, 1, 1, 1);
  private static final ComplexSym NEGATIVE_I = ComplexSym.valueOf(0, 1, -1, 1);

  public static ComplexSym valueOf(final BigFraction real, final BigFraction imaginary) {
    final ComplexSym c = new ComplexSym();
    c.fReal = AbstractFractionSym.valueOf(real);
    c.fImaginary = AbstractFractionSym.valueOf(imaginary);
    return c;
  }

  public static ComplexSym valueOf(final BigInteger real) {
    final ComplexSym c = new ComplexSym();
    c.fReal = AbstractIntegerSym.valueOf(real);
    c.fImaginary = F.C0;
    return c;
  }

  public static ComplexSym valueOf(final BigInteger real, final BigInteger imaginary) {
    final ComplexSym c = new ComplexSym();
    c.fReal = AbstractIntegerSym.valueOf(real);
    c.fImaginary = AbstractIntegerSym.valueOf(imaginary);
    return c;
  }

  public static ComplexSym valueOf(final IFraction real) {
    final ComplexSym c = new ComplexSym();
    c.fReal = real;
    c.fImaginary = F.C0;
    return c;
  }

  public static ComplexSym valueOf(final IRational real) {
    final ComplexSym c = new ComplexSym();
    c.fReal = real;
    c.fImaginary = F.C0;
    return c;
  }

  public static ComplexSym valueOf(final IRational real, final IRational imaginary) {
    final ComplexSym c = new ComplexSym();
    c.fReal = real;
    c.fImaginary = imaginary;
    return c;
  }

  public static ComplexSym valueOf(final long real_numerator, final long real_denominator,
      final long imag_numerator, final long imag_denominator) {
    final ComplexSym c = new ComplexSym();
    if (real_denominator == 1L) {
      c.fReal = AbstractIntegerSym.valueOf(real_numerator);
    } else {
      c.fReal = AbstractFractionSym.valueOf(real_numerator, real_denominator);
    }
    if (imag_denominator == 1L) {
      c.fImaginary = AbstractIntegerSym.valueOf(imag_numerator);
    } else {
      c.fImaginary = AbstractFractionSym.valueOf(imag_numerator, imag_denominator);
    }
    return c;
  }

  private IRational fReal;

  private IRational fImaginary;

  private transient int fHashValue;

  private ComplexSym() {}

  /** {@inheritDoc} */
  @Override
  public IExpr abs() {
    if (fReal.isZero()) {
      return fImaginary.abs();
    }
    if (fImaginary.isZero()) {
      return fReal.abs();
    }
    return F.Sqrt(fReal.multiply(fReal).add(fImaginary.multiply(fImaginary)));
  }

  @Override
  public IExpr complexArg() {
    // ic == ( x + I * y )
    IRational x = getRealPart();
    IRational y = getImaginaryPart();
    int xi = x.compareTo(F.C0);
    int yi = y.compareTo(F.C0);
    if (xi < 0) {
      // x < 0
      if (yi < 0) {
        // y < 0

        // -Pi + ArcTan(y/x)
        return Plus(Negate(Pi), ArcTan(Divide(y, x)));
      } else {
        // y >= 0

        // Pi + ArcTan(y/x)
        return Plus(Pi, ArcTan(Divide(y, x)));
      }
    }
    if (xi > 0) {
      // ArcTan(y/x)
      return ArcTan(Divide(y, x));
    }
    if (yi < 0) {
      // y < 0

      // -Pi/2 + ArcTan(x/y)
      return Plus(Times(CN1D2, Pi), ArcTan(Divide(x, y)));
    } else {
      if (yi > 0) {
        // y > 0

        // Pi/2 + ArcTan(x/y)
        return Plus(Times(C1D2, Pi), ArcTan(Divide(x, y)));
      }
    }
    return F.C0;
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

  public ComplexSym add(final ComplexSym parm1) {
    return ComplexSym.valueOf(fReal.add(parm1.fReal), fImaginary.add(parm1.fImaginary));
  }

  @Override
  public IComplex add(final IComplex parm1) {
    return ComplexSym.valueOf(fReal.add(parm1.getRealPart()),
        fImaginary.add(parm1.getImaginaryPart()));
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return ApcomplexNum.valueOf(apcomplexValue());
  }

  @Override
  public Apcomplex apcomplexValue() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    long precision = h.precision();
    Apfloat real = h.divide(new Apfloat(fReal.toBigNumerator(), precision),
        new Apfloat(fReal.toBigDenominator(), precision));
    Apfloat imag = h.divide(new Apfloat(fImaginary.toBigNumerator(), precision),
        new Apfloat(fImaginary.toBigDenominator(), precision));
    return new Apcomplex(real, imag);
  }

  @Override
  public INumber ceilFraction() {
    return valueOf(fReal.ceilFraction(), fImaginary.ceilFraction());
  }

  /** {@inheritDoc} */
  @Override
  public int compareAbsValueToOne() {
    IRational temp = fReal.multiply(fReal).add(fImaginary.multiply(fImaginary));
    return temp.compareTo(F.C1);
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr instanceof ComplexSym) {
      final int cp = fReal.compareTo(((ComplexSym) expr).fReal);
      if (cp != 0) {
        return cp;
      }
      return fImaginary.compareTo(((ComplexSym) expr).fImaginary);
    }
    if (expr.isNumber()) {
      int c = fReal.compareTo(((INumber) expr).re());
      if (c != 0) {
        return c;
      }
      if (expr.isReal()) {
        return 1;
      }
      return fImaginary.compareTo(((INumber) expr).im());
    }
    return IComplex.super.compareTo(expr);
  }

  @Override
  public ComplexNum complexNumValue() {
    // double precision complex number
    double nr = fReal.numerator().doubleValue();
    double dr = fReal.denominator().doubleValue();
    double ni = fImaginary.numerator().doubleValue();
    double di = fImaginary.denominator().doubleValue();
    return ComplexNum.valueOf(nr / dr, ni / di);
  }

  @Override
  public int complexSign() {
    final int i = fReal.numerator().complexSign();

    if (i == 0) {
      return fImaginary.numerator().complexSign();
    }

    return i;
  }

  /** {@inheritDoc} */
  @Override
  public IComplex conjugate() {
    return ComplexSym.valueOf(fReal, fImaginary.negate());
  }

  @Override
  public IExpr copy() {
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr dec() {
    return add(MINUS_ONE);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr inc() {
    return add(ONE);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof ComplexSym) {
      if (hashCode() != obj.hashCode()) {
        return false;
      }
      if (this == obj) {
        return true;
      }
      return fReal.equals(((ComplexSym) obj).fReal)
          && fImaginary.equals(((ComplexSym) obj).fImaginary);
    }
    return false;
  }

  @Override
  public boolean equalsInt(int i) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(EvalEngine engine) {
    if (engine.isNumericMode()) {
      if (engine.isArbitraryMode()) {
        return numericNumber();
      }
      if (fReal.isZero()) {
        // if possible use predefined constants for imaginary unit
        if (isImaginaryUnit()) {
          return F.CDI;
        } else if (isNegativeImaginaryUnit()) {
          return F.CDNI;
        }
      }
      return numericNumber();
    }
    final INumber cTemp = normalizeNull(fReal, fImaginary);
    return (cTemp == null) ? F.NIL : cTemp;
  }

  /** {@inheritDoc} */
  @Override
  public IComplex fractionalPart() {
    return valueOf(fReal.fractionalPart(), fImaginary.fractionalPart());
  }

  /** {@inheritDoc} */
  @Override
  public IComplex integerPart() {
    return valueOf(fReal.integerPart(), fImaginary.integerPart());
  }

  @Override
  public INumber floorFraction() {
    return valueOf(fReal.floorFraction(), fImaginary.floorFraction());
  }

  @Override
  public String fullFormString() {
    StringBuilder buf = new StringBuilder("Complex");
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      buf.append('(');
    } else {
      buf.append('[');
    }
    if (fReal.denominator().isOne()) {
      buf.append(fReal.numerator().toString());
    } else {
      buf.append("Rational");
      if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        buf.append('(');
      } else {
        buf.append('[');
      }
      buf.append(fReal.numerator().toString());
      buf.append(',');
      buf.append(fReal.denominator().toString());
      if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        buf.append(')');
      } else {
        buf.append(']');
      }
    }
    buf.append(',');

    if (fImaginary.denominator().isOne()) {
      buf.append(fImaginary.numerator().toString());
    } else {
      buf.append("Rational");
      if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        buf.append('(');
      } else {
        buf.append('[');
      }
      buf.append(fImaginary.numerator().toString());
      buf.append(',');
      buf.append(fImaginary.denominator().toString());
      if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
        buf.append(')');
      } else {
        buf.append(']');
      }
    }
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      buf.append(')');
    } else {
      buf.append(']');
    }
    return buf.toString();
  }

  /** {@inheritDoc} */
  @Override
  public IInteger[] gaussianIntegers() {
    if (fReal.isInteger() && fImaginary.isInteger()) {
      return new IInteger[] {((IInteger) fReal), ((IInteger) fImaginary)};
    }
    return null;
  }

  @Override
  public double imDoubleValue() {
    return fImaginary.doubleValue();
  }

  /**
   * Returns the imaginary part of a complex number
   *
   * @return imaginary part
   */
  @Override
  public IRational getImaginaryPart() {
    return fImaginary;
  }

  @Override
  public double reDoubleValue() {
    return fReal.doubleValue();
  }

  /**
   * Returns the real part of a complex number
   *
   * @return real part
   */
  @Override
  public IRational getRealPart() {
    return fReal;
  }

  @Override
  public final int hashCode() {
    if (fHashValue == 0) {
      fHashValue = fReal.hashCode() * 29 + fImaginary.hashCode();
    }
    return fHashValue;
  }

  @Override
  public ISymbol head() {
    return S.Complex;
  }

  @Override
  public int hierarchy() {
    return COMPLEXID;
  }

  @Override
  public IRational im() {
    return imRational();
  }

  @Override
  public IRational imRational() {
    if (fImaginary.denominator().isOne()) {
      return fImaginary.numerator();
    }
    return fImaginary;
  }

  @Override
  public CharSequence internalFormString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = SourceCodeProperties.stringFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
  }

  @Override
  public CharSequence internalJavaString(SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {
    String prefix = SourceCodeProperties.getPrefixF(properties);
    if (fReal.isZero()) {
      if (fImaginary.isOne()) {
        return new StringBuilder(prefix).append("CI");
      }
      if (fImaginary.isMinusOne()) {
        return new StringBuilder(prefix).append("CNI");
      }
    }
    BigInteger realNumerator = fReal.toBigNumerator();
    BigInteger realDenominator = fReal.toBigDenominator();
    BigInteger imagNumerator = fImaginary.toBigNumerator();
    BigInteger imagDenominator = fImaginary.toBigDenominator();
    if (NumberUtil.hasIntValue(realNumerator) && NumberUtil.hasIntValue(realDenominator)
        && NumberUtil.hasIntValue(imagNumerator) && NumberUtil.hasIntValue(imagDenominator)) {
      return prefix + "CC(" //
          + realNumerator.intValue() + "L," + realDenominator.intValue() + "L,"
          + imagNumerator.intValue() + "L," + imagDenominator.intValue() + "L)";
    }
    return prefix + "CC(" + fReal.internalJavaString(properties, depth, variables) + ","
        + fImaginary.internalJavaString(properties, depth, variables) + ")";
  }

  @Override
  public CharSequence internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = SourceCodeProperties.scalaFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
  }

  @Override
  public IComplex inverse() {
    final IRational tmp = (fReal.multiply(fReal)).add(fImaginary.multiply(fImaginary));
    return ComplexSym.valueOf(fReal.divideBy(tmp), fImaginary.negate().divideBy(tmp));
  }

  @Override
  public boolean isImaginaryUnit() {
    return equals(F.CI);
  }

  @Override
  public boolean isNegativeImaginaryUnit() {
    return equals(F.CNI);
  }

  @Override
  public boolean isOne() {
    return fReal.isOne() && fImaginary.isZero();
  }

  @Override
  public boolean isMinusOne() {
    return fReal.isMinusOne() && fImaginary.isZero();
  }

  @Override
  public boolean isZero() {
    return fReal.isZero() && fImaginary.isZero();
  }

  @Override
  public long leafCountSimplify() {
    return 1 + fReal.leafCountSimplify() + fImaginary.leafCountSimplify();
  }

  @Override
  public long leafCount() {
    return 1 + fReal.leafCount() + fImaginary.leafCount();
  }

  @Override
  public IComplex multiply(final IComplex parm1) {
    checkBitLength();
    parm1.checkBitLength();
    return ComplexSym.valueOf(
        fReal.multiply(parm1.getRealPart()).subtract(fImaginary.multiply(parm1.getImaginaryPart())),
        fReal.multiply(parm1.getImaginaryPart()).add(parm1.getRealPart().multiply(fImaginary)));
  }

  /**
   * Return the quotient and remainder as an array <code>[quotient, remainder]</code> of the
   * division of <code>IComplex</code> numbers <code>this / c2</code>.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Gaussian_integer">Wikipedia - Gaussian integer</a>
   * <li><a href=
   * "http://fermatslasttheorem.blogspot.com/2005/06/division-algorithm-for-gaussian.html">Division
   * Algorithm for Gaussian Integers </a>
   * </ul>
   *
   * @param c2
   * @return the quotient and remainder as an array <code>[quotient, remainder]</code>
   */
  @Override
  public IComplex[] quotientRemainder(final IComplex c2) {
    final IRational re = c2.re();
    final IRational im = c2.im();
    IRational numeratorReal = fReal.multiply(re).subtract( //
        fImaginary.multiply(im.negate()));

    IRational numeratorImaginary = fReal.multiply(im.negate()).add( //
        re.multiply(fImaginary));

    IRational denominator = re.multiply(re).add( //
        im.multiply(im));

    if (denominator.isZero()) {
      throw new IllegalArgumentException("Denominator can not be zero.");
    }

    IInteger divisionReal = numeratorReal.divideBy(denominator).roundExpr();
    IInteger divisionImaginary = numeratorImaginary.divideBy(denominator).roundExpr();

    IRational remainderReal =
        fReal.subtract(re.multiply(divisionReal)).subtract(im.multiply(divisionImaginary).negate());
    IRational remainderImaginary =
        fImaginary.subtract(re.multiply(divisionImaginary)).subtract(im.multiply(divisionReal));

    return new ComplexSym[] { //
        valueOf(divisionReal, divisionImaginary), //
        valueOf(remainderReal, remainderImaginary)};
  }

  @Override
  public ComplexSym negate() {
    return ComplexSym.valueOf(fReal.negate(), fImaginary.negate());
  }

  @Override
  public INumber normalize() {
    INumber normalized = normalizeNull(fReal, fImaginary);
    return (normalized == null) ? this : normalized;
  }

  /**
   * Return the normalized form of this number (i.e. if the imaginary part equals zero, return the
   * real part as a fractional or integer number).
   * 
   * @param fReal
   * @param fImaginary
   * @return <code>null</code> if no new number was evaluated
   */
  private static INumber normalizeNull(IRational fReal, IRational fImaginary) {
    if (fImaginary.isZero()) {
      if (fReal instanceof IFraction) {
        if (fReal.denominator().isOne()) {
          return fReal.numerator();
        }
        if (fReal.numerator().isZero()) {
          return F.C0;
        }
      }
      return fReal;
    }
    if (fReal instanceof IFraction) {
      if (fReal.denominator().isOne()) {
        IRational newRe = fReal.numerator();
        if (fImaginary instanceof IFraction && fImaginary.denominator().isOne()) {
          IRational newIm = fImaginary.numerator();
          return valueOf(newRe, newIm);
        }
        return valueOf(newRe, fImaginary);
      }
      if (fReal.numerator().isZero()) {
        IRational newRe = F.C0;
        if (fImaginary instanceof IFraction && fImaginary.denominator().isOne()) {
          IRational newIm = fImaginary.numerator();
          return valueOf(newRe, newIm);
        }
        return valueOf(newRe, fImaginary);
      }
    }
    if (fImaginary instanceof IFraction && fImaginary.denominator().isOne()) {
      IRational newIm = fImaginary.numerator();
      return valueOf(fReal, newIm);
    }
    return null;
  }

  @Override
  public final INumber numericNumber() {
    return F.complexNum(this);
  }

  @Override
  public INumber opposite() {
    return ComplexSym.valueOf(fReal.negate(), fImaginary.negate());
  }

  @Override
  public INumber plus(final INumber that) {
    if (that.isZero()) {
      return this;
    }
    if (that instanceof ComplexSym) {
      return this.add((ComplexSym) that);
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        Apcomplex temp = apcomplexValue().add(((ApcomplexNum) that).apcomplexValue());
        return F.complexNum(temp);
      }
      return F.complexNum(evalfc().add(((IComplexNum) that).evalfc()));
    }
    if (that instanceof IInteger) {
      return this.add(valueOf((IInteger) that));
    }
    if (that instanceof IFraction) {
      return this.add(valueOf((IFraction) that));
    }
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        Apcomplex temp = apcomplexValue().add(((ApfloatNum) that).apfloatValue());
        return F.complexNum(temp);
      }
      return F.complexNum(evalfc().add(((INum) that).evalf()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IExpr plus(final IExpr that) {
    if (that instanceof INumber) {
      return plus((INumber) that);
    }
    return IComplex.super.plus(that);
  }

  @Override
  public IExpr power(final IExpr that) {
    if (that instanceof INumber) {
      if (that.isZero()) {
        if (!this.isZero()) {
          return F.C1;
        }
        return IComplex.super.power(that);
      } else if (that.isOne()) {
        return this;
      } else if (that.isMinusOne()) {
        return inverse();
      }
      long n = ((IInteger) that).toLongDefault();
      if (n != Long.MIN_VALUE) {
        return power(n);
      }
    }
    return IComplex.super.power(that);
  }

  @Override
  public IComplex pow(final int n) {

    if ((n == 0) && fReal.isZero() && fImaginary.isZero()) {
      throw new ArithmeticException("Indeterminate: 0^0");
    }
    if (n == Integer.MIN_VALUE) {
      throw new java.lang.ArithmeticException();
    }
    if (n == 1) {
      return this;
    }

    if (n < 0) {
      IComplex res = powPositive(-n);
      return res.inverse();
    }
    return powPositive(n);
  }

  /**
   * @param n must be greater equal 0
   * @return
   */
  private IComplex powPositive(final long n) {
    if (fReal.isZero()) {
      long modN = n % 4;
      if (fImaginary.isOne()) {
        if (modN == 0) {
          return ONE;
        }
        if (modN == 1) {
          return this;
        }
        if (modN == 2) {
          return MINUS_ONE;
        }
        return NEGATIVE_I;
      }
      if (fImaginary.isMinusOne()) {
        if (modN == 0) {
          return ONE;
        }
        if (modN == 1) {
          return NEGATIVE_I;
        }
        if (modN == 2) {
          return MINUS_ONE;
        }
        return POSITIVE_I;
      }
    }
    long exp = n;
    long b2pow = 0;

    while ((exp & 1) == 0L) {
      b2pow++;
      exp >>= 1;
    }

    IComplex r = this;
    IComplex x = r;

    while ((exp >>= 1) > 0L) {
      x = x.multiply(x);
      if ((exp & 1) != 0) {
        r.checkBitLength();
        r = r.multiply(x);
      }
    }
    r.checkBitLength();
    while (b2pow-- > 0L) {
      r = r.multiply(r);
      r.checkBitLength();
    }
    return r;
  }

  @Override
  public void checkBitLength() {
    if (Integer.MAX_VALUE > Config.MAX_BIT_LENGTH) {
      long bitLength = fReal.toBigNumerator().bitLength() + fReal.toBigDenominator().bitLength();
      if (bitLength > Config.MAX_BIT_LENGTH / 4) {
        BigIntegerLimitExceeded.throwIt(bitLength);
      }
      bitLength =
          fImaginary.toBigNumerator().bitLength() + fImaginary.toBigDenominator().bitLength();
      if (bitLength > Config.MAX_BIT_LENGTH / 4) {
        BigIntegerLimitExceeded.throwIt(bitLength);
      }
    }
  }

  @Override
  public IRational rationalFactor() {
    if (fReal.isZero()) {
      return fImaginary;
    }
    if (fImaginary.isZero()) {
      return fReal;
    }
    if (fReal.equals(fImaginary)) {
      return fReal;
    }
    return null;
  }

  @Override
  public IRational re() {
    return reRational();
  }

  @Override
  public IRational reRational() {
    if (fReal.denominator().isOne()) {
      return fReal.numerator();
    }
    return fReal;
  }

  @Override
  public INumber roundExpr() {
    return valueOf(fReal.roundExpr(), fImaginary.roundExpr());
  }

  @Override
  public IComplex sqrtCC() {
    // https://math.stackexchange.com/a/44414
    // this == c + d*I
    IRational c = fReal;
    IRational d = fImaginary;
    IExpr val1 = c.multiply(c).add(d.multiply(d)).sqrt();
    if (val1.isRational()) {
      IExpr a = c.add((IRational) val1).divide(F.C2).sqrt();
      if (a.isRational()) {
        IExpr val2 = ((IRational) val1).subtract(c).divide(F.C2).sqrt();
        if (val2.isRational()) {
          // Sqrt(c + d*I) -> a + b*I
          IRational b = ((IRational) val2);
          return valueOf( //
              (IRational) a, //
              (d.complexSign() >= 0) ? b : b.negate() //
          );
        }
      }
    }
    return null;
  }

  @Override
  public INumber times(final INumber that) {
    if (that.isZero()) {
      return F.C0;
    }
    if (that instanceof ComplexSym) {
      return multiply((ComplexSym) that);
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        Apcomplex temp = apcomplexValue().multiply(((ApcomplexNum) that).apcomplexValue());
        return F.complexNum(temp);
      }
      return F.complexNum(evalfc().multiply(((IComplexNum) that).evalfc()));
    }
    if (that instanceof IInteger) {
      return this.multiply(valueOf((IInteger) that));
    }
    if (that instanceof IFraction) {
      return this.multiply(valueOf((IFraction) that));
    }
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        Apcomplex temp = apcomplexValue().multiply(((ApfloatNum) that).apfloatValue());
        return F.complexNum(temp);
      }
      return F.complexNum(evalfc().multiply(((INum) that).evalf()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IExpr times(final IExpr that) {
    if (that instanceof INumber) {
      return times((INumber) that);
    }
    return IComplex.super.times(that);
  }

  @Override
  public IAST toPolarCoordinates() {
    return F.list(abs(), complexArg());
  }

  @Override
  public String toString() {
    try {
      StringBuilder sb = new StringBuilder();
      OutputFormFactory.get().convertComplex(sb, this, Integer.MIN_VALUE,
          OutputFormFactory.NO_PLUS_CALL);
      return sb.toString();
    } catch (Exception e1) {
      // fall back to simple output format
      final StringBuilder tb = new StringBuilder();
      tb.append('(');
      tb.append(fReal.toString());
      tb.append(")+I*(");
      tb.append(fImaginary.toString());
      tb.append(')');
      return tb.toString();
    }
  }
}
