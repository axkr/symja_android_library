package org.matheclipse.core.numerics.functions;

import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.core.interfaces.IAST;
import java.util.function.Function;
import org.apfloat.Apcomplex;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInexactNumber;

/**
 * Runs one of the asymptotic routines of
 * {@link org.matheclipse.external.apfloat.ApcomplexAsymptoticMath} at the precision its arguments
 * arrived with,
 * and hands the answer back as the kind of number they arrived as.
 *
 * <p>
 * The routines work in apfloat at whatever precision they are given, so a machine number is
 * answered at machine precision and a 30 digit one with 30 digits - rather than every question
 * being answered in <code>double</code>, which is what the built-ins' numeric entry points would
 * otherwise do to a precision the user asked for.
 */
public final class WorkingPrecision {

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
      value = routine.apply(CarlsonArbitraryPrecision.apcomplex(a, precision),
          CarlsonArbitraryPrecision.apcomplex(b, precision), precision);
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
      arguments[i] = CarlsonArbitraryPrecision.apcomplex(numbers[i], precision);
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
