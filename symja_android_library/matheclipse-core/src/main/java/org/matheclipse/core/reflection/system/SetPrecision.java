package org.matheclipse.core.reflection.system;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.external.apfloat.ApfloatSignificance;
import org.matheclipse.parser.client.ParserConfig;

/**
 * <code>SetPrecision(expr, n)</code> gives every number of <code>expr</code> the precision
 * <code>n</code>, a real number: exact numbers and constants are evaluated to <code>n</code>
 * digits, a number of a lower precision is filled up with zeros (binary zeros for a machine
 * number), a number of a higher precision is cut, and a zero becomes the exact <code>0</code>. An
 * exact exponent stays exact, and the expression is evaluated with the numbers set.
 * <code>SetPrecision(expr, MachinePrecision)</code> gives machine numbers and
 * <code>SetPrecision(expr, Infinity)</code> the exact rational values.
 * <p>
 * Nothing of the conversion is kept anywhere: the numbers are built with the precision asked
 * for, and the numeric mode which evaluates the rest of the expression is the one of the
 * {@link EvalEngine} of the call, restored when the call returns. Two engines with different
 * precisions do not see each other.
 */
public class SetPrecision extends AbstractFunctionEvaluator {

  /** <code>precision</code> value for machine numbers */
  static final long MACHINE = -1L;

  /** <code>precision</code> value for exact numbers */
  static final long EXACT = -2L;

  /** How the precision of a single number is derived from the second argument. */
  @FunctionalInterface
  interface IPrecisionOf {
    /**
     * @param scale the number of digits before the decimal point of the number,
     *        <code>Floor(Log10(Abs(x)))+1</code>
     * @return the precision in digits; a value <code>&lt;= 0</code> means that no digit is left
     */
    long digits(long scale);
  }

  public SetPrecision() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg2 = ast.arg2();
    if (arg2.equals(S.MachinePrecision)) {
      return convert(ast.arg1(), MACHINE, scale -> MACHINE, engine);
    }
    if (arg2.isInfinity()) {
      return convert(ast.arg1(), EXACT, scale -> EXACT, engine);
    }
    double precision = digitsArgument(ast, arg2, "precbd", engine);
    if (Double.isNaN(precision)) {
      return F.NIL;
    }
    if (precision < 0.0) {
      // Requested precision `1` is smaller than `2`.
      Errors.printMessage(ast.topHead(), "precsm", F.list(arg2, F.C0), engine);
      precision = 0.0;
    }
    return setDigits(ast.arg1(), precision, false, engine);
  }

  /**
   * The number of digits in the second argument of <code>SetPrecision</code> and
   * <code>SetAccuracy</code>, a real number.
   *
   * @param messageShortcut the message for an argument which is not a real number
   * @return <code>NaN</code> if the argument is not a real number, or if it is too large; a
   *         message is printed
   */
  static double digitsArgument(final IAST ast, IExpr arg2, String messageShortcut,
      EvalEngine engine) {
    double digits = Double.NaN;
    if (arg2.isReal() || arg2.isNumericFunction(true)) {
      try {
        digits = arg2.evalf();
      } catch (RuntimeException rex) {
        // a list or another value which is no real number
      }
    }
    if (!Double.isFinite(digits)) {
      // Requested precision `1` is not a machine-sized real number.
      Errors.printMessage(ast.topHead(), messageShortcut, F.list(arg2), engine);
      return Double.NaN;
    }
    if (digits > Config.MAX_PRECISION_APFLOAT) {
      // Requested precision `1` is greater than `2`.
      Errors.printMessage(ast.topHead(), "precgt",
          F.list(arg2, F.ZZ(Config.MAX_PRECISION_APFLOAT)), engine);
      return Double.NaN;
    }
    return Math.max(digits, -(double) Config.MAX_PRECISION_APFLOAT);
  }

  /**
   * <code>expr</code> with every number set to the precision (or the accuracy) <code>digits</code>,
   * a real number, and every numeric constant evaluated to it; then what is left is evaluated.
   * An exact exponent stays exact: the square root of <code>SetPrecision(Sqrt(2), 20)</code> is
   * taken of <code>2</code> with 20 digits, which gives the precision <code>20.301</code>.
   *
   * @param expr the expression
   * @param digits the precision or the accuracy
   * @param accuracy <code>true</code> if <code>digits</code> is an accuracy
   * @param engine the engine of the call; its numeric precision is the same after the call
   */
  static IExpr setDigits(IExpr expr, double digits, boolean accuracy, EvalEngine engine) {
    IExpr result = setNumbers(expr, digits, accuracy, engine);
    if (result.isNumber() || result.isFree(x -> x.isInexactNumber(), false)) {
      return result;
    }
    final boolean oldNumericMode = engine.isNumericMode();
    final long oldPrecision = engine.getNumericPrecision();
    final int oldSignificantFigures = engine.getSignificantFigures();
    try {
      // the exact numbers which are left, the exponents, are taken as arbitrary-precision numbers
      // of no error
      final long precision = Math.min(Config.MAX_PRECISION_APFLOAT,
          Math.max((long) Math.ceil(digits) + 20L, ParserConfig.MACHINE_PRECISION + 1L));
      engine.setNumericMode(false, precision, oldSignificantFigures);
      return engine.evalWithoutNumericReset(result);
    } finally {
      engine.setNumericMode(oldNumericMode, oldPrecision, oldSignificantFigures);
    }
  }

  private static IExpr setNumbers(IExpr expr, double digits, boolean accuracy,
      EvalEngine engine) {
    if (expr.isNumber()) {
      return setNumber((INumber) expr, digits, accuracy);
    }
    if (expr.isBuiltInSymbol() && ((ISymbol) expr).isConstantAttribute()) {
      // a numeric constant like Pi, with enough digits for its precision or accuracy
      final long precision = Math.min(Config.MAX_PRECISION_APFLOAT,
          (long) Math.ceil(Math.max(digits, accuracy ? 0.0 : 1.0)) + 20L);
      final IExpr value = engine.evalN(expr, precision);
      return value.isNumber() ? setNumber((INumber) value, digits, accuracy) : expr;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IASTMutable result = F.NIL;
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (i == 2 && ast.isPower() && arg.isRational()) {
          // an exact exponent stays exact
          continue;
        }
        IExpr temp = setNumbers(arg, digits, accuracy, engine);
        if (temp != arg) {
          if (result.isNIL()) {
            result = ast.copy();
          }
          result.set(i, temp);
        }
      }
      return result.orElse(expr);
    }
    return expr;
  }

  /** One number with the precision or the accuracy <code>digits</code>. */
  private static IExpr setNumber(INumber x, double digits, boolean accuracy) {
    if (x instanceof IComplex || x instanceof IComplexNum) {
      return setComplex(x, digits, accuracy);
    }
    if (x.isZero()) {
      // the zero has no relative error: SetPrecision gives the exact 0, SetAccuracy a zero of that
      // accuracy
      return accuracy ? ApfloatNum.zero(digits) : F.C0;
    }
    final Apfloat value;
    if (x instanceof IRational) {
      IRational rational = (IRational) x;
      final long precision = Math.max(Math.max(1L, (long) Math.ceil(digits)),
          ApfloatNum.MIN_STORED_DIGITS) + 10L + Math.abs(new Apfloat(rational.toBigNumerator())
              .scale() - new Apfloat(rational.toBigDenominator()).scale());
      value = new Apfloat(rational.toBigNumerator(), precision)
          .divide(new Apfloat(rational.toBigDenominator(), precision));
    } else if (x instanceof ApfloatNum) {
      value = ((ApfloatNum) x).extendedValue();
    } else if (x instanceof INum) {
      final double d = ((INum) x).doubleValue();
      if (!Double.isFinite(d)) {
        return x;
      }
      // the binary value of the machine number
      value = exact(d);
    } else {
      return x;
    }
    final double log10 = ApfloatSignificance.log10Abs(value);
    final double precision = accuracy ? digits + log10 : digits;
    if (precision <= 0.0) {
      // not one digit is left
      return ApfloatNum.zero(accuracy ? digits : precision - log10);
    }
    return ApfloatNum.valueOf(value, precision);
  }

  /**
   * A complex number with the precision or the accuracy <code>digits</code>: both parts get the
   * same accuracy, the one which gives the number that precision or accuracy. A part below the
   * accuracy becomes a zero of it; for <code>SetPrecision</code> an exact or a machine zero part
   * stays the exact <code>0</code>.
   */
  private static IExpr setComplex(INumber z, double digits, boolean accuracy) {
    final INumber re;
    final INumber im;
    if (z instanceof IComplex) {
      re = ((IComplex) z).re();
      im = ((IComplex) z).im();
    } else if (z instanceof ApcomplexNum) {
      final Apcomplex value = ((ApcomplexNum) z).apcomplexValue();
      re = ApfloatNum.valueOf(value.real());
      im = ApfloatNum.valueOf(value.imag());
    } else {
      final IComplexNum value = (IComplexNum) z;
      re = F.num(value.reDoubleValue());
      im = F.num(value.imDoubleValue());
    }
    if (accuracy) {
      // also an exact zero part becomes inexact, so both parts count
      final double partAccuracy = digits + Math.log10(2.0) / 2.0;
      return complex(setNumber(re, partAccuracy, true), setNumber(im, partAccuracy, true));
    }
    final int counted = (isExactOrMachineZero(re) ? 0 : 1) + (isExactOrMachineZero(im) ? 0 : 1);
    final double partAccuracy = digits - log10Abs(z) + Math.log10(Math.max(counted, 1)) / 2.0;
    return complex(setPart(re, partAccuracy), setPart(im, partAccuracy));
  }

  private static boolean isExactOrMachineZero(INumber part) {
    return part.isZero() && !(part instanceof ApfloatNum);
  }

  /** A part of a complex number under <code>SetPrecision</code>, with the accuracy given. */
  private static IExpr setPart(INumber part, double partAccuracy) {
    if (isExactOrMachineZero(part)) {
      return F.C0;
    }
    return setNumber(part, partAccuracy, true);
  }

  /** <code>Log10(Abs(x))</code> of a complex number, <code>0</code> for zero. */
  private static double log10Abs(INumber x) {
    final double abs = x.evalfc().norm();
    return abs > 0.0 && Double.isFinite(abs) ? Math.log10(abs) : 0.0;
  }

  /**
   * Replace the numbers of <code>expr</code> and evaluate what is left with the precision of the
   * call.
   *
   * @param expr
   * @param evalPrecision the precision for the numeric evaluation of the constants and functions
   *        which are left after the numbers were replaced; {@link #MACHINE} or {@link #EXACT}
   * @param precisionOf the precision of a single number
   * @param engine the engine of the call; its numeric mode and precision are the same after the
   *        call as before
   */
  static IExpr convert(IExpr expr, long evalPrecision, IPrecisionOf precisionOf,
      EvalEngine engine) {
    IExpr result = replaceNumbers(expr, precisionOf);
    if (evalPrecision == EXACT || result.isNumber() || result.isFree(x -> x.isNumber(), false)
        && result.isFree(x -> x.isConstantAttribute(), false)) {
      return result;
    }
    if (evalPrecision == MACHINE) {
      return engine.evalN(result);
    }
    final boolean oldNumericMode = engine.isNumericMode();
    final long oldPrecision = engine.getNumericPrecision();
    final int oldSignificantFigures = engine.getSignificantFigures();
    try {
      long precision = Math.max(evalPrecision, ParserConfig.MACHINE_PRECISION);
      engine.setNumericMode(true, precision,
          (int) Math.min(precision, Math.min(Config.MAX_OUTPUT_SIZE, Short.MAX_VALUE)));
      return engine.evalWithoutNumericReset(result);
    } finally {
      engine.setNumericMode(oldNumericMode, oldPrecision, oldSignificantFigures);
    }
  }

  private static IExpr replaceNumbers(IExpr expr, IPrecisionOf precisionOf) {
    if (expr.isNumber()) {
      return number((INumber) expr, precisionOf);
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IASTMutable result = F.NIL;
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        IExpr temp = replaceNumbers(arg, precisionOf);
        if (temp != arg) {
          if (result.isNIL()) {
            result = ast.copy();
          }
          result.set(i, temp);
        }
      }
      return result.orElse(expr);
    }
    return expr;
  }

  private static IExpr number(INumber x, IPrecisionOf precisionOf) {
    if (x.isZero() && x.isExactNumber()) {
      // the exact zero has no digits to set
      return x;
    }
    if (x instanceof IComplex) {
      IComplex c = (IComplex) x;
      IExpr re = real(c.re(), null, precisionOf);
      IExpr im = real(c.im(), null, precisionOf);
      return complex(re, im);
    }
    if (x instanceof IComplexNum) {
      Apcomplex c = x.apcomplexValue();
      IComplexNum cn = (IComplexNum) x;
      boolean machine = !(x instanceof ApcomplexNum);
      IExpr re = real(null, machine ? exact(cn.reDoubleValue()) : c.real(), precisionOf);
      IExpr im = real(null, machine ? exact(cn.imDoubleValue()) : c.imag(), precisionOf);
      return complex(re, im);
    }
    if (x instanceof IRational) {
      return real((IRational) x, null, precisionOf);
    }
    if (x instanceof ApfloatNum) {
      return real(null, ((ApfloatNum) x).apfloatValue(), precisionOf);
    }
    if (x instanceof INum) {
      double d = ((INum) x).doubleValue();
      if (!Double.isFinite(d)) {
        return x;
      }
      return real(null, exact(d), precisionOf);
    }
    return x;
  }

  /** The binary value of a machine number, which <code>SetPrecision</code> fills up with zeros. */
  private static Apfloat exact(double d) {
    return Double.isFinite(d) ? new Apfloat(new BigDecimal(d)) : new Apfloat(d);
  }

  private static IExpr complex(IExpr re, IExpr im) {
    if (re instanceof IRational && im instanceof IRational) {
      return F.CC((IRational) re, (IRational) im);
    }
    if (im.isZero() && im.isExactNumber()) {
      // an exact zero imaginary part leaves a real number
      return re;
    }
    if (re instanceof ApfloatNum && im instanceof ApfloatNum) {
      return F.complexNum(
          new Apcomplex(((ApfloatNum) re).apfloatValue(), ((ApfloatNum) im).apfloatValue()));
    }
    if (re.isZero() && re.isExactNumber() && im instanceof ApfloatNum) {
      return F.complexNum(new Apcomplex(Apfloat.ZERO, ((ApfloatNum) im).apfloatValue()));
    }
    return F.complexNum(re.evalf(), im.evalf());
  }

  /**
   * One real number, given as the exact <code>rational</code> or as the <code>inexact</code>
   * value.
   */
  private static IExpr real(IRational rational, Apfloat inexact, IPrecisionOf precisionOf) {
    final long scale;
    if (rational != null) {
      if (rational.isZero()) {
        scale = 0;
      } else {
        // digits of the numerator minus digits of the denominator, within one digit
        scale = new Apfloat(rational.toBigNumerator(), 20L)
            .divide(new Apfloat(rational.toBigDenominator(), 20L)).scale();
      }
    } else {
      scale = inexact.signum() == 0 ? 0 : inexact.scale();
    }
    final long precision = precisionOf.digits(scale);
    if (precision == EXACT) {
      if (rational != null) {
        return rational;
      }
      BigDecimal value = new BigDecimal(inexact.toString(true));
      BigInteger unscaled = value.unscaledValue();
      return value.scale() >= 0 //
          ? F.QQ(unscaled, BigInteger.TEN.pow(value.scale())).normalize()
          : F.ZZ(unscaled.multiply(BigInteger.TEN.pow(-value.scale())));
    }
    if (precision == MACHINE) {
      return F.num(rational != null ? rational.doubleValue() : inexact.doubleValue());
    }
    if (precision <= 0) {
      // no digit is left of the number; an arbitrary-precision zero would have infinite precision,
      // so it is the machine zero
      return F.CD0;
    }
    if (rational != null) {
      return F.num(new Apfloat(rational.toBigNumerator(), precision)
          .divide(new Apfloat(rational.toBigDenominator(), precision)));
    }
    return F.num(inexact.precision(precision));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
