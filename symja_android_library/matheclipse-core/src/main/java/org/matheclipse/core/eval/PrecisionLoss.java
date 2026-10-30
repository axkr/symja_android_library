package org.matheclipse.core.eval;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

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

  /**
   * Start tracking the digits which sums of machine numbers lose, see
   * {@link #machineSum(EvalEngine, IExpr, IExpr, IExpr)}.
   *
   * @return the state before, for {@link #stopMachine(EvalEngine, long)}
   */
  public static long startMachine(EvalEngine engine) {
    final long before = engine.fMachineLoss;
    engine.fMachineLoss = 0L;
    return before;
  }

  /**
   * The largest loss of a machine sum reported since {@link #startMachine(EvalEngine)}: digits, or
   * {@link #TOTAL}.
   */
  public static long machineLost(EvalEngine engine) {
    return engine.fMachineLoss;
  }

  /** Set the tracking of machine sums back to the state which startMachine() returned. */
  public static void stopMachine(EvalEngine engine, long before) {
    engine.fMachineLoss = before;
  }

  /** The binary digits a machine sum has to lose before it is reported: two decimal digits. */
  private static final int MACHINE_THRESHOLD_BITS = 7;

  /**
   * Report the digits which the sum or difference of two machine numbers cancels, while
   * {@link #startMachine(EvalEngine)} tracks them. The loss is told from the binary exponents of the
   * operands and the result.
   *
   * @param a the first operand
   * @param b the second operand
   * @param result their sum
   */
  public static void machineSum(EvalEngine engine, IExpr a, IExpr b, IExpr result) {
    
    if (engine.fMachineLoss >= 0L && result instanceof Num) {
      reportMachine(engine, machineValue(a), machineValue(b), ((Num) result).doubleValue());
    }
  }

  /**
   * Report the digits which the sum of two machine numbers cancels, see
   * {@link #machineSum(EvalEngine, IExpr, IExpr, IExpr)}, for a caller without an engine at hand.
   */
  public static void machineSum(IExpr a, IExpr b, IExpr result) {
    if (result instanceof Num) {
      final double sum = ((Num) result).doubleValue();
      final double da = machineValue(a);
      final double db = machineValue(b);
      if (da != 0.0 && db != 0.0 && (sum == 0.0 || Math.max(Math.getExponent(da),
          Math.getExponent(db)) - Math.getExponent(sum) >= MACHINE_THRESHOLD_BITS)) {
        final EvalEngine engine = EvalEngine.get();
        if (engine.fMachineLoss >= 0L) {
          reportMachine(engine, da, db, sum);
        }
      }
    }
  }

  /**
   * The value of a machine number, or of a rational number which a machine sum converts to one;
   * <code>0.0</code> for anything else, which is not reported.
   */
  private static double machineValue(IExpr x) {
    if (x instanceof Num) {
      return ((Num) x).doubleValue();
    }
    if (x instanceof IRational) {
      final double d = ((IRational) x).doubleValue();
      return Double.isFinite(d) ? d : 0.0;
    }
    return 0.0;
  }

  private static void reportMachine(EvalEngine engine, double a, double b, double sum) {
    if (a == 0.0 || b == 0.0) {
      return;
    }
    long digits;
    if (sum == 0.0) {
      digits = TOTAL;
    } else {
      final int bits =
          Math.max(Math.getExponent(a), Math.getExponent(b)) - Math.getExponent(sum);
      if (bits < MACHINE_THRESHOLD_BITS) {
        return;
      }
      // log10(2) decimal digits per binary digit
      digits = (long) (bits * 0.30103);
    }
    if (digits > engine.fMachineLoss) {
      engine.fMachineLoss = digits;
    }
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
