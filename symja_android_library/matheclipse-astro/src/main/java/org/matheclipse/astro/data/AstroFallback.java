package org.matheclipse.astro.data;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.ISymbol;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.DateComponents;
import org.orekit.time.TimeScalesFactory;

/**
 * Tells the user when a result was computed with the lower accuracy Meeus theories instead of the
 * JPL ephemerides.
 *
 * <p>
 * A scope is opened by {@link AstroDataContext#checkAvailable(ISymbol, EvalEngine)}, which every
 * astronomy evaluator calls first. The position providers call {@link #report(AbsoluteDate)} each
 * time they fall back; only the first report in a scope prints the <code>astrofallback</code>
 * message, so an event search which evaluates a body thousands of times still prints it once.
 */
public final class AstroFallback {

  private static final class Scope {
    /** A builtin symbol; the engine is not kept, so a pooled thread pins nothing. */
    ISymbol symbol;
    boolean reported;
    boolean forbidden;
  }

  private static final ThreadLocal<Scope> SCOPE = ThreadLocal.withInitial(Scope::new);

  private AstroFallback() {}

  /** Start reporting for the evaluation of {@code symbol}. */
  public static void begin(ISymbol symbol, EvalEngine engine) {
    Scope scope = SCOPE.get();
    scope.symbol = symbol;
    scope.reported = false;
    scope.forbidden = false;
  }

  /**
   * End the current scope, so that nothing of it - a {@link #forbid()} above all - carries over to
   * whatever runs next on this thread.
   */
  public static void end() {
    SCOPE.remove();
  }

  /**
   * Switch the fallback off for the rest of the current evaluation, for the computations whose
   * results the Meeus theories are not accurate enough for. The providers then report the dates
   * outside the ephemerides as missing data, which prints the <code>orekitdata</code> message.
   */
  public static void forbid() {
    SCOPE.get().forbidden = true;
  }

  /** Whether the current evaluation may use the Meeus theories. */
  public static boolean isAllowed() {
    return !SCOPE.get().forbidden;
  }

  /**
   * Record that the Meeus theories were used for {@code date}; prints the message the first time in
   * the current scope.
   */
  public static void report(AbsoluteDate date) {
    Scope scope = SCOPE.get();
    if (scope.reported || scope.symbol == null) {
      return;
    }
    scope.reported = true;
    DateComponents day = date.getComponents(TimeScalesFactory.getUTC()).getDate();
    // The date `1` is outside the range of the Orekit ephemerides; the lower accuracy algorithms
    // of Meeus were used.
    Errors.printMessage(scope.symbol, "astrofallback", F.List(F.stringx(day.toString())),
        EvalEngine.get());
  }

  /** Whether the Meeus theories were used since the current scope began. */
  public static boolean wasUsed() {
    return SCOPE.get().reported;
  }
}
