package org.matheclipse.core.eval;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The machine underflows of one {@link EvalEngine}: a machine precision result which isn't zero,
 * but too small for a normalized machine number. It returns the subnormal number or <code>0.</code>
 * and prints <code>General::munfl</code>, the first three times in a calculation.
 * <p>
 * Every underflow is counted, printed or not. A caller which needs the value of an exact
 * expression, like a comparison, reads {@link #count()} before and after a numeric evaluation to
 * learn that the machine result isn't that value, and evaluates inside a {@link #quiet()} scope,
 * because the message belongs to a result the user gets to see.
 */
public final class MachineUnderflow {
  /** Prints this many <code>General::munfl</code> messages in one calculation. */
  private static final int MESSAGE_LIMIT = 3;

  /** Restores the printing of the message when a {@link MachineUnderflow#quiet()} scope ends. */
  public final class Quiet implements AutoCloseable {
    private Quiet() {
      fQuiet++;
    }

    @Override
    public void close() {
      fQuiet--;
    }
  }

  private final EvalEngine fEngine;

  private long fCount;

  private long fZeroCount;

  /** The messages printed in the current calculation. */
  private int fPrinted;

  /** The depth of the open {@link #quiet()} scopes. */
  private int fQuiet;

  MachineUnderflow(EvalEngine engine) {
    fEngine = engine;
  }

  /** Whether a machine result is zero or subnormal. */
  public static boolean isBelowNormal(double result) {
    return Math.abs(result) < Double.MIN_NORMAL;
  }

  /** Whether a message is <code>General::munfl</code>. */
  static boolean isMessage(ISymbol symbol, String messageShortcut) {
    return symbol == S.General && "munfl".equals(messageShortcut);
  }

  /**
   * Report <code>operation</code>, if its machine <code>result</code> is zero or subnormal. The
   * caller knows that the exact result isn't zero.
   */
  public void check(double result, IExpr operation) {
    if (isBelowNormal(result)) {
      report(operation, result == 0.0);
    }
  }

  /**
   * Count the underflow of <code>operation</code> and print <code>General::munfl</code> for it,
   * unless a {@link #quiet()} scope is open.
   *
   * @param zero the machine value is <code>0.0</code>: nothing of the value is left, where a
   *        subnormal result only lost digits
   */
  public void report(IExpr operation, boolean zero) {
    fCount++;
    if (zero) {
      fZeroCount++;
    }
    if (fQuiet == 0) {
      Errors.printMessage(S.General, "munfl", F.list(operation), fEngine);
    }
  }

  /** The number of underflows so far. */
  public long count() {
    return fCount;
  }

  /** The number of underflows to <code>0.0</code> so far. */
  public long zeroCount() {
    return fZeroCount;
  }

  /**
   * Open a scope in which underflows are counted, but not printed:
   * <code>try (MachineUnderflow.Quiet quiet = engine.machineUnderflow().quiet()) { ... }</code>
   */
  public Quiet quiet() {
    return new Quiet();
  }

  /** A new calculation starts: the message is printed again. */
  void newCalculation() {
    fPrinted = 0;
  }

  /**
   * Print the text of a <code>General::munfl</code> message: the first three of a calculation,
   * followed by <code>General::stop</code>. A later one was generated, so that <code>Check</code>
   * sees it, but isn't printed.
   *
   * @return <code>false</code> if the message wasn't printed
   */
  boolean print(String text) {
    if (++fPrinted > MESSAGE_LIMIT) {
      return false;
    }
    Errors.logMessage("General", text, fEngine);
    if (fPrinted == MESSAGE_LIMIT) {
      Errors.logMessage("General",
          "Further output of General::munfl will be suppressed during this calculation.", fEngine);
    }
    return true;
  }
}
