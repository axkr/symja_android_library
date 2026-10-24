package org.matheclipse.core.eval.tasks;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * One <code>ScheduledTask</code>: an expression to evaluate, when it is next due, and how often it
 * comes back.
 *
 * <p>
 * The expression is held. It is evaluated on the evaluating thread by {@link EventLoop}, not on a
 * thread of its own, so it sees the session it was submitted from.
 */
public final class ScheduledTaskEntry {

  private final String uuid;
  private final IExpr heldExpression;
  private final long periodNanos;
  private volatile long dueAt;
  private final java.util.concurrent.atomic.AtomicInteger remainingRuns;
  private volatile boolean cancelled;

  /**
   * @param periodNanos how long between runs, or <code>-1</code> for a task that runs once
   * @param remainingRuns how many times it may still run, or <code>-1</code> for no limit
   */
  public ScheduledTaskEntry(String uuid, IExpr heldExpression, long delayNanos, long periodNanos,
      int remainingRuns) {
    this.uuid = uuid;
    this.heldExpression = heldExpression;
    this.periodNanos = periodNanos;
    this.dueAt = System.nanoTime() + delayNanos;
    this.remainingRuns = new java.util.concurrent.atomic.AtomicInteger(remainingRuns);
  }

  public String uuid() {
    return uuid;
  }

  public long dueAt() {
    return dueAt;
  }

  public boolean isCancelled() {
    return cancelled;
  }

  public void cancel() {
    cancelled = true;
  }

  /** The expression, held, so that <code>TaskObject</code> can show what it runs. */
  public IExpr expression() {
    return heldExpression;
  }

  /** Evaluate it once. */
  public void run(EvalEngine engine) {
    if (cancelled) {
      return;
    }
    // -1 stands for no limit and stays
    remainingRuns.updateAndGet(n -> n > 0 ? n - 1 : n);
    engine.evaluate(F.ReleaseHold(heldExpression));
  }

  /**
   * Put it back in the queue if it comes round again.
   *
   * @return <code>true</code> if it is still scheduled
   */
  public boolean reschedule() {
    if (cancelled || periodNanos <= 0 || remainingRuns.get() == 0) {
      return false;
    }
    dueAt = System.nanoTime() + periodNanos;
    return true;
  }
}
