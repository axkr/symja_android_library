package org.matheclipse.core.numerics.functions;

import org.apfloat.Apcomplex;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInexactNumber;

/**
 * Runs one of the asymptotic routines in this package at the precision its arguments arrived with,
 * and hands the answer back as the kind of number they arrived as.
 *
 * <p>
 * The routines work in apfloat at whatever precision they are given, so a machine number is
 * answered at machine precision and a 30 digit one with 30 digits - rather than every question
 * being answered in <code>double</code>, which is what the built-ins' numeric entry points would
 * otherwise do to a precision the user asked for.
 */
public final class WorkingPrecision {

  /** A routine that answers at the precision of <code>h</code>, or declines with null. */
  @FunctionalInterface
  public interface Routine {
    Apcomplex apply(Apcomplex a, Apcomplex b, FixedPrecisionApcomplexHelper h);
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
    Apcomplex value;
    try {
      value = routine.apply(a.apcomplexValue(), b.apcomplexValue(), h);
    } catch (ArgumentTypeException | ApfloatRuntimeException ex) {
      return F.NIL;
    }
    if (value == null) {
      return F.NIL;
    }
    if (realResult && a.isReal() && b.isReal()) {
      return machinePrecision ? F.num(value.real().doubleValue()) : F.num(value.real());
    }
    return machinePrecision
        ? F.complexNum(value.real().doubleValue(), value.imag().doubleValue())
        : F.complexNum(value);
  }
}
