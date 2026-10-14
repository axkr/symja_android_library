package org.matheclipse.core.eval;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;

/**
 * The digits which a numeric evaluation loses.
 *
 * <p>
 * <code>N(expr, p)</code> calculates every operation with <code>p</code> digits and no more. The
 * difference of two numbers which agree in <code>k</code> digits has <code>p - k</code> of them
 * right, and a periodic function of an argument with <code>k</code> digits before its radix point
 * has as few. While an evaluation is tracked, these operations report what they lose here, and
 * <code>N</code> evaluates again with that many digits more - see
 * {@link org.matheclipse.core.reflection.system.N#evalN2(org.matheclipse.core.interfaces.IExpr, long, EvalEngine)}.
 *
 * <p>
 * Only a loss which an operation can see from its own operands is reported. A function which
 * multiplies the error of its argument for another reason has to carry its own working digits.
 */
public final class PrecisionLoss {

  /**
   * Lost digits below this number are not reported: the scale of a number is known to a digit
   * only, <code>1.5 - 0.6</code> already changes it by one.
   */
  public static final long THRESHOLD = 2;

  /** The sum of two numbers is zero: every digit is lost. */
  public static final long TOTAL = Long.MAX_VALUE;

  private PrecisionLoss() {}

  /**
   * Start tracking in the engine.
   *
   * @return the state before, for {@link #stop(EvalEngine, long)}
   */
  public static long start(EvalEngine engine) {
    final long before = engine.fPrecisionLoss;
    engine.fPrecisionLoss = 0L;
    return before;
  }

  /** Forget the loss reported so far; tracking goes on. */
  public static void reset(EvalEngine engine) {
    engine.fPrecisionLoss = 0L;
  }

  /** The largest loss reported since the start or the last reset: digits, or {@link #TOTAL}. */
  public static long lost(EvalEngine engine) {
    return engine.fPrecisionLoss;
  }

  /** Set the tracking back to the state which {@link #start(EvalEngine)} returned. */
  public static void stop(EvalEngine engine, long before) {
    engine.fPrecisionLoss = before;
  }

  /**
   * Whether the evaluation in progress asked for its numeric value itself, with
   * {@link EvalEngine#evalN(org.matheclipse.core.interfaces.IExpr, long)}: the message about the
   * limit of the extra precision is not printed then.
   */
  public static boolean isLimitQuiet(EvalEngine engine) {
    return engine.fPrecisionLimitQuiet;
  }

  private static void report(long digits) {
    final EvalEngine engine = EvalEngine.get();
    if (engine.fPrecisionLoss >= 0L && digits > engine.fPrecisionLoss) {
      engine.fPrecisionLoss = digits;
    }
  }

  /**
   * Report the digits which a sum or a difference cancels.
   *
   * @param a the first operand
   * @param b the second operand
   * @param result their sum or difference
   * @return <code>result</code>
   */
  public static Apfloat sum(Apfloat a, Apfloat b, Apfloat result) {
    if (a.signum() != 0 && b.signum() != 0) {
      if (result.signum() == 0) {
        report(TOTAL);
      } else {
        final long digits = Math.max(a.scale(), b.scale()) - result.scale();
        if (digits >= THRESHOLD) {
          report(digits);
        }
      }
    }
    return result;
  }

  /**
   * Report the digits which a sum or a difference of complex numbers cancels, in the size of the
   * numbers.
   *
   * @param a the first operand
   * @param b the second operand
   * @param result their sum or difference
   * @return <code>result</code>
   */
  public static Apcomplex sum(Apcomplex a, Apcomplex b, Apcomplex result) {
    if (!a.isZero() && !b.isZero()) {
      if (result.isZero()) {
        report(TOTAL);
      } else {
        final long digits = Math.max(a.scale(), b.scale()) - result.scale();
        if (digits >= THRESHOLD) {
          report(digits);
        }
      }
    }
    return result;
  }

  /**
   * Report the digits which a periodic function loses: those which its argument has before the
   * radix point, <code>Sin(10^30+1/3)</code>.
   *
   * @param argument the real argument of a circular function, or the imaginary part of the
   *        argument of an exponential or hyperbolic function
   */
  public static void periodic(Apfloat argument) {
    if (argument.signum() != 0) {
      final long digits = argument.scale();
      if (digits >= THRESHOLD) {
        report(digits);
      }
    }
  }
}
