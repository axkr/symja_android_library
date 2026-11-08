package org.matheclipse.core.numerics.functions;

import java.math.RoundingMode;
import java.util.function.Function;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInexactNumber;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.parser.client.ParserConfig;

/**
 * The bridge between the numbers of Symja and the arbitrary precision routines of
 * <code>org.matheclipse.external.apfloat</code>: the working precision of an evaluation, the
 * arguments as apfloat takes them, and the result as the kind of number the arguments arrived as.
 *
 * <p>
 * The routines work in apfloat at whatever precision they are given, so a machine number is
 * answered at machine precision and a 30 digit one with 30 digits - rather than every question
 * being answered in <code>double</code>, which is what the built-ins' numeric entry points would
 * otherwise do to a precision the user asked for.
 */
public final class WorkingPrecision {

  /**
   * The digits which a computation carries more than the result shows. A caller adds them to the
   * <code>digits</code> it gives {@link #apcomplex(IExpr, int)} and rounds them away with
   * {@link #round(Apcomplex, long)}; the <code>evaluate</code> routines of this class work at the
   * precision of the result instead.
   */
  public static final int GUARD_DIGITS = 10;

  /**
   * A routine that answers at the precision of its arguments, or declines with an
   * <code>ArithmeticException</code>.
   */
  @FunctionalInterface
  public interface Routine {
    /**
     * @param a the first argument, with <code>precision</code> digits
     * @param b the second argument, with <code>precision</code> digits
     * @param precision the working precision
     */
    Apcomplex apply(Apcomplex a, Apcomplex b, int precision);
  }

  private WorkingPrecision() {}

  /**
   * The digits of an apfloat computation: the precision of the engine, or machine precision plus
   * one digit.
   */
  public static int digits(boolean arbitrary, EvalEngine engine) {
    return arbitrary ? (int) engine.getNumericPrecision()
        : (int) ParserConfig.MACHINE_PRECISION + 1;
  }

  /**
   * The number as apfloat takes it: a nonzero part with <code>digits</code> digits. A machine number
   * is the binary number it is, see {@link ApfloatNum#apfloatOf(double, long)}.
   */
  public static Apcomplex apcomplex(IExpr z, int digits) {
    if (z instanceof Num) {
      return ApfloatNum.apfloatOf(((Num) z).doubleValue(), digits);
    }
    if (z instanceof ComplexNum) {
      ComplexNum c = (ComplexNum) z;
      return new Apcomplex(ApfloatNum.apfloatOf(c.getRealPart(), digits),
          ApfloatNum.apfloatOf(c.getImaginaryPart(), digits));
    }
    Apcomplex value = ((INumber) z).apcomplexValue();
    return new Apcomplex(precision(value.real(), digits), precision(value.imag(), digits));
  }

  private static Apfloat precision(Apfloat x, int digits) {
    return x.signum() == 0 ? x : x.precision(digits);
  }

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

  /**
   * The number of <code>value</code> at arbitrary precision or as a machine number: its real part
   * if <code>real</code> is set, otherwise the complex number.
   */
  public static IExpr toNumber(Apcomplex value, boolean real, boolean arbitrary) {
    if (real) {
      return arbitrary ? F.num(value.real()) : F.num(value.real().doubleValue());
    }
    return arbitrary ? F.complexNum(value)
        : F.complexNum(value.real().doubleValue(), value.imag().doubleValue());
  }

  /**
   * <code>routine(a, b)</code>, or {@link F#NIL} when it declines - which leaves the question to the
   * library routine it stands in front of.
   *
   * @param realResult whether real arguments give a real result, so that a vanishing imaginary part
   *        is rounding and not an answer
   */
  public static IExpr evaluate(IInexactNumber a, IInexactNumber b, boolean realResult,
      Routine routine) {
    boolean machinePrecision = !(a instanceof ApfloatNum) && !(a instanceof ApcomplexNum)
        && !(b instanceof ApfloatNum) && !(b instanceof ApcomplexNum);
    FixedPrecisionApcomplexHelper h =
        machinePrecision ? EvalEngine.getApfloatDouble() : EvalEngine.getApfloat();
    final int precision = (int) h.precision();
    Apcomplex value;
    try {
      value = routine.apply(apcomplex(a, precision),
          apcomplex(b, precision), precision);
    } catch (ArithmeticException | ArgumentTypeException | ApfloatRuntimeException ex) {
      // the routine declines: its expansion doesn't reach the precision here
      Errors.rethrowsInterruptException(ex);
      return F.NIL;
    }
    if (realResult && a.isReal() && b.isReal()) {
      return machinePrecision ? F.num(value.real().doubleValue()) : F.num(value.real());
    }
    return machinePrecision
        ? F.complexNum(value.real().doubleValue(), value.imag().doubleValue())
        : F.complexNum(value);
  }

  /**
   * A routine of a list of numbers that answers at the given precision, or declines with an
   * <code>ArithmeticException</code>.
   */
  @FunctionalInterface
  public interface ArrayRoutine {
    /**
     * @param arguments the arguments, with <code>precision</code> digits (exact zeros stay exact)
     * @param precision the working precision
     */
    Apcomplex apply(Apcomplex[] arguments, int precision);
  }

  /**
   * <code>routine(arguments)</code> for a function of numbers which has no other evaluation: at
   * machine precision, or at the precision of the engine for arbitrary precision arguments and for
   * exact ones under <code>N(expr, precision)</code>.
   *
   * @param realResult whether real arguments give a real result
   * @return {@link F#NIL} if an argument is not a number, all are exact outside of a numeric
   *         evaluation, or the routine declines
   */
  public static IExpr evaluate(IAST ast, EvalEngine engine, boolean realResult,
      Function<Apcomplex[], Apcomplex> routine) {
    IExpr[] numbers = new IExpr[ast.argSize()];
    for (int i = 0; i < numbers.length; i++) {
      numbers[i] = ast.get(i + 1);
    }
    return evaluate(numbers, engine, realResult, (arguments, precision) -> routine.apply(arguments));
  }

  /**
   * <code>routine(numbers)</code> for a function of a list of numbers: at machine precision, or at
   * the precision of the engine for arbitrary precision numbers and for exact ones under
   * <code>N(expr, precision)</code>.
   *
   * @param numbers the arguments of the routine
   * @param realResult whether real arguments give a real result
   * @return {@link F#NIL} if an argument is not a number, all are exact outside of a numeric
   *         evaluation, or the routine declines
   */
  public static IExpr evaluate(IExpr[] numbers, EvalEngine engine, boolean realResult,
      ArrayRoutine routine) {
    boolean arbitrary = false;
    boolean inexact = false;
    boolean real = true;
    for (IExpr arg : numbers) {
      if (!arg.isNumber()) {
        return F.NIL;
      }
      arbitrary |= arg instanceof ApfloatNum || arg instanceof ApcomplexNum;
      inexact |= arg.isInexactNumber();
      real &= arg.isReal();
    }
    if (!inexact) {
      if (!engine.isNumericMode()) {
        return F.NIL;
      }
      arbitrary = engine.isArbitraryMode();
    }
    final int precision =
        arbitrary ? (int) engine.getNumericPrecision() : (int) ParserConfig.MACHINE_PRECISION + 1;
    Apcomplex[] arguments = new Apcomplex[numbers.length];
    for (int i = 0; i < arguments.length; i++) {
      arguments[i] = apcomplex(numbers[i], precision);
    }
    Apcomplex value;
    try {
      value = routine.apply(arguments, precision);
    } catch (ArithmeticException | ArgumentTypeException | ApfloatRuntimeException ex) {
      Errors.rethrowsInterruptException(ex);
      return F.NIL;
    }
    if (value.imag().signum() == 0 || (realResult && real)) {
      return arbitrary ? F.num(value.real()) : F.num(value.real().doubleValue());
    }
    return arbitrary ? F.complexNum(value)
        : F.complexNum(value.real().doubleValue(), value.imag().doubleValue());
  }
}
