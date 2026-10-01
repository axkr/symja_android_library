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
 * Some native stages call {@link EvalEngine#evaluate} internally on inputs that can grind far
 * longer than they are worth (the Rubi rules on an integral they cannot finish, or a
 * {@code RootSum} over a solvable cubic/quartic that {@code FullSimplify} chews on for tens of
 * seconds). The engine has no per-call deadline of its own, but it does enforce time limits through
 * the <em>interrupt flag</em> of the evaluating thread - its evaluation loop throws
 * {@link org.matheclipse.core.eval.exception.TimeoutException} when it sees the thread interrupted.
 *
 * <p>
 * So a watchdog that interrupts <em>this</em> thread after the budget elapses reuses exactly that
 * mechanism, and nothing has to move to another thread - which matters because {@link EvalEngine}
 * is thread-local. The interrupt is best-effort: it only takes effect once control returns to the
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
    final Budget budget = new Budget(ACTIVE.get());
    ScheduledFuture<?> watchdog = scheduler().schedule(() -> {
      synchronized (budget) {
        if (!budget.finished) {
          budget.exceeded = true;
          evaluationThread.interrupt();
        }
      }
    }, budgetMillis, TimeUnit.MILLISECONDS);
    ACTIVE.set(budget);
    try {
      IExpr result = work.get();
      return result == null ? F.NIL : result;
    } catch (RuntimeException rex) {
      if (!budget.isExceeded() || budget.isEnclosingExceeded()) {
        // not our interrupt - the caller's deadline or a genuine failure - or not only ours
        throw rex;
      }
      return F.NIL;
    } finally {
      synchronized (budget) {
        budget.finished = true;
      }
      watchdog.cancel(false);
      if (budget.parent == null) {
        ACTIVE.remove();
      } else {
        ACTIVE.set(budget.parent);
      }
      if (budget.isExceeded() && !budget.isEnclosingExceeded()) {
        // clear the flag we raised, or the caller aborts on the next interruption check
        Thread.interrupted();
      }
    }
  }

  /** The innermost budget running on this thread. */
  private static final ThreadLocal<Budget> ACTIVE = new ThreadLocal<Budget>();

  /**
   * One call of {@link #runWithin}, linked to the one it runs inside.
   *
   * <p>
   * All budgets of a thread share its one interrupt flag. When a budget and one around it run out
   * close together - a nested <code>Integrate</code> arms the budget of its rules a few
   * milliseconds after the stage which asked for it armed its own, of the same length - both
   * watchdogs have fired before the thread looks at the flag. The inner call must then neither
   * answer "did not finish" nor clear the flag: the interrupt is the outer one's as well, whose
   * watchdog has fired and will not fire again, so the work would go on without any limit.
   */
  private static final class Budget {
    final Budget parent;
    boolean exceeded;
    boolean finished;

    Budget(Budget parent) {
      this.parent = parent;
    }

    synchronized boolean isExceeded() {
      return exceeded;
    }

    boolean isEnclosingExceeded() {
      for (Budget b = parent; b != null; b = b.parent) {
        if (b.isExceeded()) {
          return true;
        }
      }
      return false;
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
