package org.matheclipse.core.expression;

import static org.matheclipse.core.expression.F.num;
import java.util.function.Function;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatRuntimeException;
import org.hipparchus.complex.Complex;
import org.hipparchus.exception.NullArgumentException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.functions.HypergeometricJS;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.form.DoubleToMMA;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
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
import com.google.common.math.DoubleMath;

/**
 * <code>IComplexNum</code> implementation which wraps a {@link Complex} value to represent a
 * numeric complex floating-point number.
 */
public class ComplexNum implements IComplexNum {

  /** */
  private static final long serialVersionUID = -6033055105824482264L;

  /** The square root of -1. A number representing "0.0 + 1.0i" */
  public static final ComplexNum I = new ComplexNum(Complex.I);

  public static final ComplexNum INF = new ComplexNum(Complex.INF);

  /** A complex number representing "NaN + NaNi" */
  public static final ComplexNum NaN = new ComplexNum(Complex.NaN);

  /** The square root of -1. A number representing "0.0 - 1.0i" */
  public static final ComplexNum NI = new ComplexNum(Complex.MINUS_I);

  /** A complex number representing "-1.0 + 0.0i" */
  public static final ComplexNum MINUS_ONE = new ComplexNum(Complex.MINUS_ONE);

  /** A complex number representing "1.0 + 0.0i" */
  public static final ComplexNum ONE = new ComplexNum(Complex.ONE);

  /** A complex number representing "0.0 + 0.0i" */
  public static final ComplexNum ZERO = new ComplexNum(Complex.ZERO);

  /**
   * Return the absolute value of this complex number. Returns {@code NaN} if either real or
   * imaginary part is {@code NaN} and {@code Double.POSITIVE_INFINITY} if neither part is {@code
   * NaN}, but at least one part is infinite.
   *
   * @return the absolute value.
   */
  public static double dabs(Complex c) {
    if (c.isNaN()) {
      return Double.NaN;
    }

    if (c.isInfinite()) {
      return Double.POSITIVE_INFINITY;
    }

    if (Math.abs(c.getReal()) < Math.abs(c.getImaginary())) {
      if (F.isZero(c.getImaginary())) {
        return Math.abs(c.getReal());
      }
      final double q = c.getReal() / c.getImaginary();
      return Math.abs(c.getImaginary()) * Math.sqrt(1 + q * q);
    } else {
      if (F.isZero(c.getReal())) {
        return Math.abs(c.getImaginary());
      }
      final double q = c.getImaginary() / c.getReal();
      return Math.abs(c.getReal()) * Math.sqrt(1 + q * q);
    }
  }

  /**
   * Be cautious with this method, no new internal couble complex is created
   *
   * @param value a double complex numeric value
   * @return
   */
  private static ComplexNum newInstance(final Complex value) {
    return new ComplexNum(value);
  }

  public static ComplexNum valueOf(final Complex c) {
    double real = c.getReal();
    double imaginary = c.getImaginary();
    if (real == 0.0d || real == -0.0d) {
      if (imaginary == 0.0d || imaginary == -0.0d) {
        return ZERO;
      }
      return newInstance(new Complex(0.0d, imaginary));
    }
    if (imaginary == 0.0d || imaginary == -0.0d) {
      return newInstance(new Complex(real, 0.0d));
    }
    return newInstance(c);
  }

  /**
   * Create complex number on unit circle with given argument <code>arg</code>.
   * 
   * @param arg angle
   * @return E^(I * angle), i.e. complex number on unit circle with given argument
   */
  public static ComplexNum unitOf(final double arg) {
    return newInstance(new Complex(Math.cos(arg), Math.sin(arg)));
  }

  public static ComplexNum valueOf(final double real) {
    if (real == 0.0d) {
      return ZERO;
    }
    return newInstance(new Complex(real, 0.0));
  }

  public static ComplexNum valueOf(final double real, final double imaginary) {
    if (real == 0.0d && imaginary == 0.0d) {
      return ZERO;
    }
    // if (real == 0.0d || real == -0.0d) {
    // if (imaginary == 0.0d || imaginary == -0.0d) {
    // // Complex.ZERO constructor
    // return newInstance(new Complex(0.0d, 0.0d));
    // }
    // return newInstance(new Complex(0.0d, imaginary));
    // }
    // if (imaginary == 0.0d || imaginary == -0.0d) {
    // return newInstance(new Complex(real, 0.0d));
    // }
    return newInstance(new Complex(real, imaginary));
  }

  public static ComplexNum valueOf(final INum d) {
    double real = d.getRealPart();
    if (real == 0.0d || real == -0.0d) {
      return ZERO;
    }
    return newInstance(new Complex(real, 0.0));
  }

  Complex fComplex;

  private ComplexNum(final double r, final double i) {
    fComplex = new Complex(r, i);
  }

  private ComplexNum(Complex complex) {
    fComplex = complex;
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

  /**
   * Returns a {@code ComplexNum} whose value is {@code (this + addend)}. Uses the definitional
   * formula
   *
   * <p>
   * {@code (a + bi) + (c + di) = (a+c) + (b+d)i} If either {@code this} or {@code addend} has a
   * {@code NaN} value in either part, {@link #NaN} is returned; otherwise {@code Infinite} and
   * {@code NaN} values are returned in the parts of the result according to the rules for
   * {@link java.lang.Double} arithmetic.
   *
   * @param addend Value to be added to this {@code Complex}.
   * @return {@code this + addend}.
   * @throws NullArgumentException if {@code addend} is {@code null}.
   */
  private ComplexNum add(final ComplexNum addend) {
    return newInstance(fComplex.add(addend.fComplex));
  }

  @Override
  public IComplexNum add(final IComplexNum val) {
    if (val instanceof ApcomplexNum) {
      return apcomplexNumValue().add(val);
    }
    return newInstance(fComplex.add(((ComplexNum) val).fComplex));
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return ApcomplexNum.valueOf(apcomplexValue());
  }

  @Override
  public Apcomplex apcomplexValue() {
    return new Apcomplex(new Apfloat(fComplex.getReal()), new Apfloat(fComplex.getImaginary()));
  }

  @Override
  public INumber ceilFraction() throws ArithmeticException {
    try {
      return F.complex(NumberUtil.toLong(Math.ceil(fComplex.getReal())),
          NumberUtil.toLong(Math.ceil(fComplex.getImaginary())));
    } catch (ArithmeticException ae) {
      ArgumentTypeException.throwArg(this, F.Ceiling(this));
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public int compareAbsValueToOne() {
    double temp = dabs();
    return Double.compare(temp, 1.0);
  }

  public int compareTo(final Complex that) {
    if (fComplex.getReal() < that.getReal()) {
      return -1;
    }
    if (fComplex.getReal() > that.getReal()) {
      return 1;
    }
    long l1 = Double.doubleToLongBits(fComplex.getReal());
    long l2 = Double.doubleToLongBits(that.getReal());
    if (l1 < l2) {
      return -1;
    }
    if (l1 > l2) {
      return 1;
    }

    if (F.isZero(that.getImaginary())) {
      if (!F.isZero(imDoubleValue())) {
        return 1;
      }
    } else if (F.isZero(imDoubleValue())) {
      return -1;
    }

    if (fComplex.getImaginary() < that.getImaginary()) {
      return -1;
    }
    if (fComplex.getImaginary() > that.getImaginary()) {
      return 1;
    }
    l1 = Double.doubleToLongBits(fComplex.getImaginary());
    l2 = Double.doubleToLongBits(that.getImaginary());
    if (l1 < l2) {
      return -1;
    }
    if (l1 > l2) {
      return 1;
    }
    return 0;
  }

  /**
   * Compares this expression with the specified expression for order. Returns a negative integer,
   * zero, or a positive integer as this expression is canonical less than, equal to, or greater
   * than the specified expression.
   */
  @Override
  public int compareTo(final IExpr expr) {
    if (expr.isNumber()) {
      if (expr instanceof ComplexNum) {
        return compareTo(((ComplexNum) expr).fComplex);
      }
      if (expr instanceof ApcomplexNum) {
        ApcomplexNum apcomplexNum = (ApcomplexNum) expr;
        return -1 * apcomplexNum.compareTo(apcomplexNumValue());
      }
      return compareTo(
          new Complex(((INumber) expr).reDoubleValue(), ((INumber) expr).imDoubleValue()));
    }
    return -1;
    // return IComplexNum.super.compareTo(expr);
  }

  @Override
  public ComplexNum complexNumValue() {
    return this;
  }

  @Override
  public int complexSign() {
    final int i = (int) Math.signum(fComplex.getReal());
    if (i == 0) {
      return (int) Math.signum(fComplex.getImaginary());
    }
    return i;
  }

  public Complex complexValue() {
    return fComplex;
  }

  /** {@inheritDoc} */
  @Override
  public IComplexNum conjugate() {
    return newInstance(fComplex.conjugate());
  }

  @Override
  public IExpr copy() {
    return this;
  }

  @Override
  public IExpr copySign(IExpr that) {
    if (that instanceof ComplexNum) {
      return newInstance(fComplex.copySign(((ComplexNum) that).fComplex));
    }
    if (that instanceof Num) {
      return newInstance(fComplex.copySign(((Num) that).value));
    }
    return IComplexNum.super.copySign(that);
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

  /** {@inheritDoc} */
  @Override
  public double dabs() {
    return dabs(fComplex);
  }

  /** {@inheritDoc} */
  @Override
  public long determinePrecision() {
    return ParserConfig.MACHINE_PRECISION;
  }

  @Override
  public IComplexNum divide(final IComplexNum that) {
    if (that instanceof ApcomplexNum) {
      return apcomplexNumValue().divide(that);
    }
    return newInstance(fComplex.divide(((ComplexNum) that).fComplex));
  }

  @Override
  public Num abs() {
    return Num.valueOf(dabs());
  }

  @Override
  public IExpr complexArg() {
    return num(fComplex.getArgument());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ComplexNum) {
      return fComplex.equals(((ComplexNum) obj).fComplex);
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
    if (fComplex.isInfinite()) {
      return F.CComplexInfinity;
    }
    if (fComplex.isNaN()) {
      return S.Indeterminate;
    }
    if (engine.isNumericMode() && engine.isArbitraryMode()) {
      return ApcomplexNum.valueOf(getRealPart(), getImaginaryPart());
    }
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public Complex evalfc() {
    return fComplex;
  }

  @Override
  public INumber evalNumber() {
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public INumber fractionalPart() {
    return F.complexNum(getRealPart() % 1, getImaginaryPart() % 1);
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    double re = fComplex.getReal();
    double im = fComplex.getImaginary();
    StringBuilder buf = new StringBuilder("Complex");
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
      buf.append('(');
    } else {
      buf.append('[');
    }

    String str = Double.toString(re);
    if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      int indx = str.indexOf("E");
      if (indx > 0) {
        str = str.replace("E", "`*^");
      } else {
        str = str + "`";
      }
    }
    buf.append(str);
    buf.append(',');
    str = Double.toString(im);
    if (!ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      int indx = str.indexOf("E");
      // `*^
      if (indx > 0) {
        str = str.replace("E", "`*^");
      } else {
        str = str + "`";
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

  /** {@inheritDoc} */
  @Override
  public IComplex integerPart() {
    // isNegative() ? ceilFraction() : floorFraction();
    double re = fComplex.getReal();
    double im = fComplex.getImaginary();
    IInteger reInt;
    IInteger imInt;
    if (re < 0.0) {
      reInt = F.ZZ(NumberUtil.toLong(Math.ceil(re)));
    } else {
      reInt = F.ZZ(NumberUtil.toLong(Math.floor(re)));
    }
    if (im < 0.0) {
      imInt = F.ZZ(NumberUtil.toLong(Math.ceil(im)));
    } else {
      imInt = F.ZZ(NumberUtil.toLong(Math.floor(im)));
    }
    return F.complex(reInt, imInt);
  }

  @Override
  public INumber floorFraction() throws ArithmeticException {
    try {
      return F.complex(NumberUtil.toLong(Math.floor(fComplex.getReal())),
          NumberUtil.toLong(Math.floor(fComplex.getImaginary())));
    } catch (ArithmeticException ae) {
      ArgumentTypeException.throwArg(this, F.Floor(this));
    }
    return null;
  }

  public Complex getCMComplex() {
    return new Complex(fComplex.getReal(), fComplex.getImaginary());
  }

  /** {@inheritDoc} */
  @Override
  public IReal im() {
    return F.num(getImaginaryPart());
  }

  @Override
  public double imDoubleValue() {
    return fComplex.getImaginary();
  }

  /** @return */
  @Override
  public double getImaginaryPart() {
    double temp = fComplex.getImaginary();
    if (temp == (-0.0)) {
      temp = 0.0;
    }
    return temp;
  }

  /** {@inheritDoc} */
  @Override
  public IReal re() {
    return F.num(getRealPart());
  }

  @Override
  public IExpr sqrt() {
    return valueOf(fComplex.sqrt());
  }

  @Override
  public double reDoubleValue() {
    return fComplex.getReal();
  }

  @Override
  public INumber roundExpr() throws ArithmeticException {
    return F.complex(F.ZZ(DoubleMath.roundToBigInteger(fComplex.getReal(), Config.ROUNDING_MODE)), //
        F.ZZ(DoubleMath.roundToBigInteger(fComplex.getImaginary(), Config.ROUNDING_MODE)));
  }

  @Override
  public double getRealPart() {
    double temp = fComplex.getReal();
    if (temp == (-0.0)) {
      temp = 0.0;
    }
    return temp;
  }

  @Override
  public final int hashCode() {
    return fComplex.hashCode();
  }

  @Override
  public ISymbol head() {
    return S.Complex;
  }

  @Override
  public int hierarchy() {
    return DOUBLECOMPLEXID;
  }

  @Override
  public IExpr hypergeometric0F1(IExpr arg2) {
    try {
      return F
          .complexNum(HypergeometricJS.hypergeometric0F1(fComplex, ((ComplexNum) arg2).evalfc()));
    } catch (RuntimeException e) {
      // try as computation with complex numbers
    }
    return IComplexNum.super.hypergeometric0F1(arg2);
  }

  @Override
  public IExpr hypergeometric1F1(IExpr arg2, IExpr arg3) {
    try {
      return F.complexNum(HypergeometricJS.hypergeometric1F1(fComplex, //
          arg2.evalfc(), //
          arg3.evalfc()));
    } catch (RuntimeException e) {
      // try as computation with complex numbers
    }
    return IComplexNum.super.hypergeometric1F1(arg2, arg3);
  }

  @Override
  public IExpr hypergeometric2F1(IExpr arg2, IExpr arg3, IExpr arg4) {
    try {
      return F.complexNum(HypergeometricJS.hypergeometric2F1(fComplex, //
          arg2.evalfc(), //
          arg3.evalfc(), //
          arg4.evalfc()));
    } catch (RuntimeException e) {
      // try as computation with complex numbers
    }
    return IComplexNum.super.hypergeometric2F1(arg2, arg3, arg4);
  }

  @Override
  public CharSequence internalFormString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = AbstractAST.stringFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
  }

  @Override
  public CharSequence internalJavaString(SourceCodeProperties properties, int depth,
      Function<ISymbol, ? extends CharSequence> variables) {
    String prefix = AbstractAST.getPrefixF(properties);
    return new StringBuilder(prefix).append("complexNum(").append(fComplex.getReal()).append(",")
        .append(fComplex.getImaginary()).append(")");
  }

  @Override
  public CharSequence internalScalaString(boolean symbolsAsFactoryMethod, int depth) {
    SourceCodeProperties p = AbstractAST.scalaFormProperties(symbolsAsFactoryMethod);
    return internalJavaString(p, depth, x -> null);
  }

  @Override
  public INumber inverse() {
    final double tmp = (fComplex.getReal() * fComplex.getReal())
        + (fComplex.getImaginary() * fComplex.getImaginary());
    return valueOf(fComplex.getReal() / tmp, -fComplex.getImaginary() / tmp);
  }

  @Override
  public boolean isImaginaryUnit() {
    return equals(I);
  }

  /** @return */
  @Override
  public boolean isInfinite() {
    return fComplex.isInfinite();
  }

  /** @return */
  @Override
  public boolean isMathematicalIntegerNegative() {
    return fComplex.isMathematicalInteger() && fComplex.getReal() < 0.0;
  }

  /** @return */
  @Override
  public boolean isMathematicalIntegerNonNegative() {
    return fComplex.isMathematicalInteger() && fComplex.getReal() >= 0.0;
  }

  /** @return */
  @Override
  public boolean isNaN() {
    return fComplex.isNaN();
  }

  @Override
  public boolean isNegativeImaginaryUnit() {
    return equals(NI);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumIntValue() {
    return F.isZero(fComplex.getImaginary()) && F.isNumIntValue(fComplex.getReal());
  }

  @Override
  public int toIntDefault(int defaultValue) {
    if (F.isZero(fComplex.getImaginary())) {
      final double re = fComplex.getReal();
      if (DoubleMath.isMathematicalInteger(re)) {
        return (int) re;
      }
    }
    return defaultValue;
  }

  @Override
  public boolean isSame(IExpr expression, double epsilon) {
    if (expression instanceof ComplexNum) {
      return F.isZero(fComplex.getReal() - ((ComplexNum) expression).fComplex.getReal(), epsilon)
          && F.isZero(fComplex.getImaginary() - ((ComplexNum) expression).fComplex.getImaginary(),
              epsilon);
    }
    return false;
  }

  @Override
  public boolean isZero() {
    return F.isZero(fComplex.getReal(), Config.DOUBLE_TOLERANCE) && //
        F.isZero(fComplex.getImaginary(), Config.DOUBLE_TOLERANCE);
  }

  @Override
  public boolean isZero(double tolerance) {
    return F.isZero(fComplex.getReal(), tolerance) && //
        F.isZero(fComplex.getImaginary(), tolerance);
  }

  @Override
  public long leafCountSimplify() {
    return 5;
  }

  /**
   * Returns a {@code ComplexNum} whose value is {@code this * factor}. Implements preliminary
   * checks for {@code NaN} and infinity followed by the definitional formula:
   *
   * <p>
   * {@code (a + bi)(c + di) = (ac - bd) + (ad + bc)i} Returns {@link #NaN} if either {@code
   * this} or {@code factor} has one or more {@code NaN} parts.
   *
   * <p>
   * Returns {@link #INF} if neither {@code this} nor {@code factor} has one or more {@code NaN}
   * parts and if either {@code this} or {@code factor} has one or more infinite parts (same result
   * is returned regardless of the sign of the components).
   *
   * <p>
   * Returns finite values in components of the result per the definitional formula in all remaining
   * cases.
   *
   * @param factor value to be multiplied by this {@code ComplexNum}.
   * @return {@code this * factor}.
   * @throws NullArgumentException if {@code factor} is {@code null}.
   */
  public ComplexNum multiply(final ComplexNum factor) {
    return newInstance(fComplex.multiply(factor.fComplex));
  }

  @Override
  public IComplexNum multiply(final IComplexNum val) {
    if (val instanceof ApcomplexNum) {
      return apcomplexNumValue().multiply(val);
    }
    return newInstance(fComplex.multiply(((ComplexNum) val).fComplex));
  }

  /**
   * Returns a {@code ComplexNum} whose value is {@code (-this)}. Returns {@code NaN} if either real
   * or imaginary part of this Complex number is {@code Double.NaN}.
   *
   * @return {@code -this}.
   */
  @Override
  public ComplexNum negate() {
    return newInstance(fComplex.negate());
  }

  /** @return */
  @Override
  public INumber opposite() {
    return newInstance(fComplex.negate());
  }

  @Override
  public INumber plus(final INumber that) {
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        ApcomplexNum acn = (ApcomplexNum) that;
        return ApcomplexNum.valueOf(getRealPart(), getImaginaryPart()).add(acn);
      }
      return newInstance(fComplex.add(((ComplexNum) that).fComplex));
    }
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        ApfloatNum afn = (ApfloatNum) that;
        return ApcomplexNum.valueOf(getRealPart(), getImaginaryPart())
            .add(ApcomplexNum.valueOf(afn.fApfloat, Apcomplex.ZERO));
      }
      return add(ComplexNum.valueOf(((Num) that).getRealPart()));
    }
    if (that instanceof IReal) {
      return this.add(F.complexNum(that.evalf()));
    }
    if (that instanceof ComplexSym) {
      return F.complexNum(fComplex.add(that.evalfc()));
    }
    throw new java.lang.ArithmeticException();
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
  public IComplexNum pow(final IComplexNum val) {
    if (Complex.equals(fComplex, Complex.ZERO, Config.DOUBLE_EPSILON)) {
      IReal sn = val.re();
      if (sn.isNegative()) {
        IOFunctions.printMessage(S.Power, "infy", F.list(F.Power(F.C0, sn)), EvalEngine.get());
        // EvalEngine.get().printMessage("Infinite expression 0^(negative number)");
        return INF;
      }
      if (sn.isZero()) {
        IOFunctions.printMessage(S.Power, "indet", F.list(F.Power(F.C0, F.C0)), EvalEngine.get());
        // EvalEngine.get().printMessage("Infinite expression 0^0.");
        return NaN;
      }
      return ZERO;
    }
    return newInstance(fComplex.pow(((ComplexNum) val).fComplex));
  }

  @Override
  public long precision() throws ApfloatRuntimeException {
    return 15;
  }

  /**
   * Returns a {@code ComplexNum} whose value is {@code (this - subtrahend)}. Uses the definitional
   * formula
   *
   * <p>
   * {@code (a + bi) - (c + di) = (a-c) + (b-d)i} If either {@code this} or {@code subtrahend} has a
   * {@code NaN]} value in either part, {@link #NaN} is returned; otherwise infinite and {@code NaN}
   * values are returned in the parts of the result according to the rules for
   * {@link java.lang.Double} arithmetic.
   *
   * @param subtrahend value to be subtracted from this {@code ComplexNum}.
   * @return {@code this - subtrahend}.
   * @throws NullArgumentException if {@code subtrahend} is {@code null}.
   */
  @Override
  public IComplexNum subtract(final IComplexNum subtrahend) {
    if (subtrahend instanceof ApcomplexNum) {
      return apcomplexNumValue().subtract(((ApcomplexNum) subtrahend));
    }
    return newInstance(fComplex.subtract(((ComplexNum) subtrahend).fComplex));
  }

  @Override
  public INumber times(final INumber that) {
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return apcomplexNumValue().multiply((ApcomplexNum) that);
      }
      return newInstance(fComplex.multiply(((ComplexNum) that).fComplex));
    }
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        return F.complexNum(apcomplexValue().multiply(((ApfloatNum) that).apcomplexValue()));
      }
      return multiply(ComplexNum.valueOf(((Num) that).getRealPart()));
    }
    if (that instanceof IReal) {
      return this.multiply(F.complexNum(that.evalf()));
    }
    if (that instanceof ComplexSym) {
      return F.complexNum(fComplex.multiply(that.evalfc()));
    }
    throw new java.lang.ArithmeticException();
  }


  /** {@inheritDoc} */
  @Override
  public IExpr times(final IExpr that) {
    if (that instanceof INumber) {
      return times((INumber) that);
    }
    return IComplexNum.super.times(that);
  }

  @Override
  public IAST toPolarCoordinates() {
    return F.list(abs(), complexArg());
  }

  @Override
  public String toString() {
    if (ParserConfig.EXPLICIT_TIMES_OPERATOR) {
      return fComplex.toString();
    }
    StringBuilder buf = new StringBuilder();
    buf.append("(");
    double realPart = fComplex.getReal();
    double imaginaryPart = fComplex.getImaginary();
    if (realPart != 0.0 || imaginaryPart == 0.0) {
      DoubleToMMA.doubleToMMA(buf, realPart, 5, 7);
    }
    if (imaginaryPart != 0.0) {
      if (imaginaryPart < 0.0) {
        buf.append("-I*");
        imaginaryPart *= (-1);
      } else {
        if (realPart != 0.0) {
          buf.append("+");
        }
        buf.append("I*");
      }
      DoubleToMMA.doubleToMMA(buf, imaginaryPart, 5, 7);
    }
    buf.append(")");
    return buf.toString();
  }

  /**
   * Return the quotient and remainder as an array <code>[quotient, remainder]</code> of the
   * division of <code>Complex</code> numbers <code>c1, c2</code>.
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
   * @param cn1
   * @param cn2
   * @return the quotient and remainder as an array <code>[quotient, remainder]</code>
   */
  public static ComplexNum[] quotientRemainder(ComplexNum cn1, ComplexNum cn2) {
    Complex c1 = cn1.fComplex;
    Complex c2 = cn2.fComplex;
    Complex[] arr = quotientRemainder(c1, c2);
    return new ComplexNum[] {valueOf(arr[0]), valueOf(arr[1])};
  }

  /**
   * Return the quotient and remainder as an array <code>[quotient, remainder]</code> of the
   * division of <code>Complex</code> numbers <code>c1, c2</code>.
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
   * @param c1
   * @param c2
   * @return the quotient and remainder as an array <code>[quotient, remainder]</code>
   */
  public static Complex[] quotientRemainder(Complex c1, Complex c2) {
    // use hipparchus Complex implementation - see:
    // https://github.com/Hipparchus-Math/hipparchus/issues/67
    Complex remainder = c1.remainder(c2);
    Complex quotient = c1.subtract(remainder).divide(c2).rint();
    return new Complex[] {quotient, remainder};

    // double numeratorReal = c1.getReal() * c2.getReal() + //
    // c1.getImaginary() * c2.getImaginary();
    // double numeratorImaginary = c1.getReal() * (-c2.getImaginary()) + //
    // c2.getReal() * c1.getImaginary();
    // double denominator = c2.getReal() * c2.getReal() + //
    // c2.getImaginary() * c2.getImaginary();
    // if (denominator == 0.0) {
    // throw new IllegalArgumentException("Denominator cannot be zero.");
    // }
    //
    // double divisionReal = Math.rint(numeratorReal / denominator);
    // double divisionImaginary = Math.rint(numeratorImaginary / denominator);
    //
    // double remainderReal = c1.getReal() - //
    // (c2.getReal() * divisionReal) + //
    // (c2.getImaginary() * divisionImaginary);
    // double remainderImaginary = c1.getImaginary() - //
    // (c2.getReal() * divisionImaginary) - //
    // (c2.getImaginary() * divisionReal);
    // return new Complex[] { new Complex(divisionReal, divisionImaginary),
    // new Complex(remainderReal, remainderImaginary) };
  }

  @Override
  public IExpr cos() {
    return valueOf(fComplex.cos());
  }

  @Override
  public IExpr cosh() {
    return valueOf(fComplex.cosh());
  }

  @Override
  public IExpr exp() {
    return valueOf(fComplex.exp());
  }

  @Override
  public IExpr log() {
    return valueOf(fComplex.log());
  }

  @Override
  public IExpr pow(int n) {
    return valueOf(fComplex.pow(n));
  }

  @Override
  public IExpr rootN(int n) {
    return valueOf(fComplex.rootN(n));
  }

  @Override
  public IExpr sign() {
    if (isNaN() || isZero()) {
      return this;
    }
    return valueOf(fComplex.sign());
  }

  @Override
  public IExpr sin() {
    return valueOf(fComplex.sin());
  }

  @Override
  public IExpr sinh() {
    return valueOf(fComplex.sinh());
  }

  @Override
  public IExpr tan() {
    return valueOf(fComplex.tan());
  }

  @Override
  public IExpr tanh() {
    return valueOf(fComplex.tanh());
  }

  @Override
  public IExpr ulp() {
    return valueOf(fComplex.ulp());
  }

  @Override
  public IExpr getPi() {
    return F.num(Math.PI);
  }

  @Override
  public IExpr toDegrees() {
    // radians * (180 / Pi)
    return valueOf(fComplex.toDegrees());
  }

  @Override
  public IExpr toRadians() {
    // degrees * (Pi / 180)
    return valueOf(fComplex.toRadians());
  }

  @Override
  public IComplexNum zero() {
    return ZERO;
  }

  @Override
  public IComplexNum one() {
    return ONE;
  }
}
