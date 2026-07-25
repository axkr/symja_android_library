package org.matheclipse.core.integrate;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Run a piece of integration work under a wall-clock time budget.
 *
 * <p>
 * Some native stages call {@link EvalEngine#evaluate} internally on inputs that can grind far longer
 * than they are worth (the Rubi rules on an integral they cannot finish, or a {@code RootSum} over a
 * solvable cubic/quartic that {@code FullSimplify} chews on for tens of seconds). The engine has no
 * per-call deadline of its own, but it does enforce time limits through the <em>interrupt flag</em>
 * of the evaluating thread - its evaluation loop throws
 * {@link org.matheclipse.core.eval.exception.TimeoutException} when it sees the thread interrupted.
 *
 * <p>
 * So a watchdog that interrupts <em>this</em> thread after the budget elapses reuses exactly that
 * mechanism, and nothing has to move to another thread - which matters because {@link EvalEngine} is
 * thread-local. The interrupt is best-effort: it only takes effect once control returns to the
 * evaluation loop, so code that never checks for interruption (notably JAS) can still overrun.
 */
public final class IntegrateTimeBudget {

  private static ScheduledExecutorService watchdogScheduler;

  private IntegrateTimeBudget() {}

  /**
   * Run {@code work} and return its result, or {@link F#NIL} if it does not finish within
   * {@code budgetMillis} (or if the work itself returns {@code null}).
   *
   * <p>
   * Only an interrupt this method raised is swallowed; an interrupt that arrives from the outside
   * (the caller's own deadline) is left to propagate as an abort. A {@code budgetMillis <= 0}, or a
   * request while {@link Config#JAS_NO_THREADS} is set, runs {@code work} without a watchdog.
   *
   * @param work the computation, typically a native integration stage
   * @param budgetMillis the wall-clock budget in milliseconds
   */
  public static IExpr runWithin(Supplier<IExpr> work, long budgetMillis) {
    if (budgetMillis <= 0 || Config.JAS_NO_THREADS) {
      IExpr result = work.get();
      return result == null ? F.NIL : result;
    }
    final Thread evaluationThread = Thread.currentThread();
    final boolean[] finished = new boolean[] {false};
    final boolean[] budgetExceeded = new boolean[] {false};
    final Object lock = new Object();
    ScheduledFuture<?> watchdog = scheduler().schedule(() -> {
      synchronized (lock) {
        if (!finished[0]) {
          budgetExceeded[0] = true;
          evaluationThread.interrupt();
        }
      }
    }, budgetMillis, TimeUnit.MILLISECONDS);
    try {
      IExpr result = work.get();
      return result == null ? F.NIL : result;
    } catch (RuntimeException rex) {
      synchronized (lock) {
        if (!budgetExceeded[0]) {
          throw rex; // not our interrupt - the caller's deadline or a genuine failure
        }
      }
      return F.NIL;
    } finally {
      synchronized (lock) {
        finished[0] = true;
      }
      watchdog.cancel(false);
      if (budgetExceeded[0]) {
        // clear the flag we raised, or the caller aborts on the next interruption check
        Thread.interrupted();
      }
    }
  }

  private static synchronized ScheduledExecutorService scheduler() {
    if (watchdogScheduler == null) {
      ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, runnable -> {
        Thread thread = Config.THREAD_FACTORY.newThread(runnable);
        thread.setDaemon(true);
        thread.setName("symja-integrate-watchdog");
        return thread;
      });
      executor.setRemoveOnCancelPolicy(true);
      watchdogScheduler = executor;
    }
    return watchdogScheduler;
  }
}
