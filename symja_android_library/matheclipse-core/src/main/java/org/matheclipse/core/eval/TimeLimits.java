package org.matheclipse.core.eval;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apfloat.ApfloatInterruptedException;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.TimeoutException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import edu.jas.kern.PreemptingException;

/**
 * The time limits of an evaluation: <code>TimeConstrained</code>, the budgets of the internal
 * stages like the ones of <code>Integrate</code>, and the limit a host sets for a whole input.
 *
 * <p>
 * Every limit is a frame on a stack of the {@link EvalEngine}; the earliest deadline of the stack
 * is the engine's <code>fTimeConstrainedMillis</code>. The evaluation runs on the calling thread -
 * no worker thread, no copy of the engine - and these end it when a deadline has passed:
 * <ul>
 * <li>the evaluation loop compares the clock with the earliest deadline, see
 * {@link EvalEngine#evalLoop(IExpr)}, and throws {@link TimeoutException}; so do the checkpoints
 * {@link EvalEngine#checkTimeLimit()} of the loops in built-in functions and of the iterators;</li>
 * <li>one shared watchdog thread interrupts the evaluating thread shortly after the deadline, if
 * the evaluation hasn't noticed it by then. That reaches code which doesn't pass a checkpoint:
 * <code>Pause</code>, a blocking call, and the loops of JAS and apfloat which test the interrupt
 * flag.</li>
 * </ul>
 * The frame whose deadline passed first, and only that one, answers its timeout value; an
 * enclosing limit which has passed as well gets the timeout instead. The watchdog's interrupt is
 * cleared again when the frame which owns it ends, and the evaluation modes of the engine are
 * restored when a limit cut the evaluation short.
 *
 * <p>
 * A thread is never started per call, which matters on phones: a thread and an engine copy for
 * every <code>TimeConstrained</code> cost time and memory there, and an abandoned worker which
 * ignored its interrupt went on computing in the background. The price is that code which passes
 * no checkpoint and doesn't test the interrupt flag runs to its end. With
 * {@link Config#TIMECONSTRAINED_NO_THREAD} or {@link Config#JAS_NO_THREADS} there is no watchdog,
 * and only the checkpoints enforce the deadlines.
 */
public final class TimeLimits {

  /** A deadline far enough away to be no limit, which the clock can still be added to. */
  private static final long NO_DEADLINE = Long.MAX_VALUE / 4L;

  /**
   * How long after a deadline the watchdog interrupts. The evaluation loop notices a deadline
   * within a fraction of a millisecond, so an interrupt - which can't be told apart from an abort
   * of the user - is only raised for code which doesn't look at the clock.
   */
  private static final long WATCHDOG_GRACE_MILLIS = 20L;

  private TimeLimits() {}

  /** The watchdog thread, started on the first limit which needs it. */
  private static final class Watchdog {
    static final ScheduledExecutorService SCHEDULER = create();

    private static ScheduledExecutorService create() {
      ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, runnable -> {
        Thread thread = Config.THREAD_FACTORY.newThread(runnable);
        thread.setDaemon(true);
        thread.setName("symja-time-limit-watchdog");
        return thread;
      });
      executor.setRemoveOnCancelPolicy(true);
      return executor;
    }
  }

  /** One limit on the stack of an engine. */
  static final class Frame {
    /** The deadline of {@link EvalEngine#deadlineClockMillis()}. */
    final long deadline;
    /** The earliest deadline of the limits this one runs in, <code>-1</code> for none. */
    final long enclosing;
    /** The enclosing frame of the same thread, or <code>null</code>. */
    final Frame parent;
    final Thread thread;
    /** The evaluation modes when the frame began, see {@link EvalEngine#modeFlags()}. */
    final int modeFlags;
    final int conditionDepth;
    final long precision;
    final int significantFigures;
    private ScheduledFuture<?> watchdog;
    /** Guarded by <code>this</code>: the frame has ended, its watchdog must not interrupt. */
    private boolean closed;
    /** Guarded by <code>this</code>: the watchdog has interrupted the thread for this frame. */
    private boolean fired;
    /** The interrupt of a watchdog of a frame inside this one, which was left to this one. */
    private boolean pending;

    Frame(long deadline, long enclosing, Frame parent, EvalEngine engine) {
      this.deadline = deadline;
      this.enclosing = enclosing;
      this.parent = parent;
      this.thread = Thread.currentThread();
      this.modeFlags = engine.modeFlags();
      this.conditionDepth = engine.conditionDepth();
      this.precision = engine.getNumericPrecision();
      this.significantFigures = engine.getSignificantFigures();
    }

    void restoreModes(EvalEngine engine) {
      engine.restoreModes(modeFlags, conditionDepth, precision, significantFigures);
    }

    /** Whether an enclosing limit has passed - a timeout is then the enclosing one's. */
    boolean isEnclosingPassed(long now) {
      return enclosing > 0 && now >= enclosing;
    }

    /** Whether a pending interrupt of the thread was raised by a watchdog for this frame. */
    synchronized boolean ownsInterrupt() {
      return fired || pending;
    }
  }

  /**
   * Run <code>work</code> under a limit of <code>millis</code> milliseconds.
   *
   * @param work the computation
   * @param millis the limit in milliseconds; a value greater than 0 is expected
   * @param onTimeout the result if this limit runs out first; it may also throw
   * @return the result of <code>work</code>, or of <code>onTimeout</code>
   */
  public static <T> T call(Supplier<T> work, long millis, Supplier<T> onTimeout) {
    return call(EvalEngine.get(), work, millis, onTimeout);
  }

  /**
   * Run <code>work</code> under a limit of <code>millis</code> milliseconds on the stack of
   * <code>engine</code>, which evaluates on the calling thread.
   *
   * @see #call(Supplier, long, Supplier)
   */
  public static <T> T call(EvalEngine engine, Supplier<T> work, long millis,
      Supplier<T> onTimeout) {
    final Frame frame = push(engine, millis);
    try {
      T result = work.get();
      if (frame.ownsInterrupt() && Thread.currentThread().isInterrupted()) {
        final long now = EvalEngine.deadlineClockMillis();
        if (now >= frame.deadline && !frame.isEnclosingPassed(now)) {
          // the work was cut short and returned anyway, as an interrupted Pause does
          return timeout(engine, frame, onTimeout);
        }
      }
      return result;
    } catch (RuntimeException rex) {
      if (isOwnTimeout(frame, rex)) {
        return timeout(engine, frame, onTimeout);
      }
      throw rex;
    } finally {
      pop(engine, frame);
    }
  }

  /**
   * Evaluate <code>expr</code> under a limit of <code>millis</code> milliseconds, as
   * <code>TimeConstrained(expr, seconds, defaultValue)</code> does.
   *
   * @param defaultValue the result if the limit runs out; {@link F#NIL} for {@link S#$Aborted}
   */
  public static IExpr evaluate(IExpr expr, long millis, IExpr defaultValue, EvalEngine engine) {
    return call(engine, () -> engine.evaluate(expr), millis,
        () -> defaultValue.isPresent() ? defaultValue : S.$Aborted);
  }

  /**
   * Evaluate <code>expr</code> under a limit of <code>millis</code> milliseconds, for an internal
   * step which has a way on without the result. An iteration, recursion or size limit which the
   * step runs into is a failed step as well, as it was when such steps ran in a worker thread.
   *
   * @return {@link F#NIL} if the limit runs out or the step exceeds a limit of the engine - never
   *         {@link S#$Aborted}, which a caller would take for a value
   */
  public static IExpr evaluateOrNil(IExpr expr, long millis) {
    final EvalEngine engine = EvalEngine.get();
    final int modeFlags = engine.modeFlags();
    final int conditionDepth = engine.conditionDepth();
    final long precision = engine.getNumericPrecision();
    final int significantFigures = engine.getSignificantFigures();
    try {
      return call(engine, () -> engine.evaluate(expr), millis, () -> F.NIL);
    } catch (LimitException le) {
      engine.restoreModes(modeFlags, conditionDepth, precision, significantFigures);
      return F.NIL;
    }
  }

  /**
   * A checkpoint for a long loop in Java code which doesn't pass the evaluation loop, see
   * {@link EvalEngine#checkTimeLimit()}.
   */
  public static void check() {
    EvalEngine.get().checkTimeLimit();
  }

  /** <code>seconds</code> in milliseconds, without overflow: a huge value is no limit. */
  public static long millis(long seconds) {
    return seconds >= NO_DEADLINE / 1000L ? NO_DEADLINE : seconds * 1000L;
  }

  /** Whether <code>rex</code> is how an interrupt or a passed deadline ends an evaluation. */
  static boolean isTimeout(RuntimeException rex) {
    return rex instanceof TimeoutException || rex instanceof PreemptingException
        || rex instanceof ApfloatInterruptedException
        || rex.getCause() instanceof InterruptedException;
  }

  private static Frame push(EvalEngine engine, long millis) {
    final long now = EvalEngine.deadlineClockMillis();
    final long deadline = millis >= NO_DEADLINE ? NO_DEADLINE : now + Math.max(1L, millis);
    final Frame top = engine.fTimeLimitFrame;
    final Frame parent;
    final long enclosing;
    if (top == null) {
      parent = null;
      // a deadline the engine was copied with, e.g. into a ParallelTable worker
      enclosing = engine.fTimeConstrainedMillis;
    } else if (top.thread == Thread.currentThread()) {
      parent = top;
      enclosing = earliest(top.deadline, top.enclosing);
    } else {
      // the frame of another thread which evaluates in this engine - a worker the host gave up on
      // and which hasn't come back yet: its deadline is none of this evaluation's business
      parent = null;
      enclosing = -1L;
    }
    final Frame frame = new Frame(deadline, enclosing, parent, engine);
    engine.fTimeLimitFrame = frame;
    engine.fTimeConstrainedMillis = earliest(deadline, enclosing);
    if ((enclosing <= 0 || deadline < enclosing) && deadline < NO_DEADLINE
        && !Config.TIMECONSTRAINED_NO_THREAD && !Config.JAS_NO_THREADS) {
      // an enclosing limit which runs out earlier has a watchdog of its own
      frame.watchdog = Watchdog.SCHEDULER.schedule(() -> {
        synchronized (frame) {
          if (!frame.closed) {
            frame.fired = true;
            frame.thread.interrupt();
          }
        }
      }, deadline - now + WATCHDOG_GRACE_MILLIS, TimeUnit.MILLISECONDS);
    }
    return frame;
  }

  private static long earliest(long deadline, long enclosing) {
    return enclosing > 0 && enclosing < deadline ? enclosing : deadline;
  }

  /** This frame's limit ran out: restore the modes, end the frame, answer the timeout value. */
  private static <T> T timeout(EvalEngine engine, Frame frame, Supplier<T> onTimeout) {
    frame.restoreModes(engine);
    pop(engine, frame);
    return onTimeout.get();
  }

  /** End the frame; a second call does nothing. */
  private static void pop(EvalEngine engine, Frame frame) {
    final boolean ownsInterrupt;
    synchronized (frame) {
      if (frame.closed) {
        return;
      }
      frame.closed = true;
      ownsInterrupt = frame.fired || frame.pending;
    }
    if (frame.watchdog != null) {
      frame.watchdog.cancel(false);
    }
    if (engine.fTimeLimitFrame == frame) {
      // otherwise another thread has begun an evaluation in this engine since, and the stack is
      // its own now
      engine.fTimeLimitFrame = frame.parent;
      engine.fTimeConstrainedMillis = frame.enclosing;
    }
    if (ownsInterrupt) {
      if (frame.parent != null && frame.isEnclosingPassed(EvalEngine.deadlineClockMillis())) {
        // the enclosing limit takes the timeout, and the interrupt with it
        synchronized (frame.parent) {
          frame.parent.pending = true;
        }
      } else {
        // taken, or not needed any more. Left set, the next evaluation of the thread would abort
        // at once.
        Thread.interrupted();
      }
    }
  }

  private static boolean isOwnTimeout(Frame frame, RuntimeException rex) {
    final long now = EvalEngine.deadlineClockMillis();
    if (now < frame.deadline || frame.isEnclosingPassed(now)) {
      // not passed yet, or an enclosing limit passed as well and takes the timeout
      return false;
    }
    final boolean ownsInterrupt = frame.ownsInterrupt();
    if (Thread.currentThread().isInterrupted() && !ownsInterrupt) {
      // an interrupt from outside: an abort
      return false;
    }
    return isTimeout(rex) || ownsInterrupt;
  }
}
