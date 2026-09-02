package org.matheclipse.core.eval;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.matheclipse.core.basic.Config;

/**
 * The single use worker behind one time limited evaluation, together with the disposal it needs.
 *
 * <p>
 * A Java thread cannot be killed. {@link EvalEngine} polls {@link Thread#interrupted()} in its
 * evaluation loop, so interrupting the worker unwinds an evaluation that is executing Symja code -
 * but an evaluation that has descended into a long running third party computation (JAS'
 * subresultant GCD on complex-rational coefficients is the usual one) polls nothing and cannot be
 * stopped at all. The timeout still fires and the caller still gets {@code $Aborted}; the worker
 * simply keeps running.
 *
 * <p>
 * That is what made a long test class degrade and then appear to hang: every timed out evaluation
 * left one such thread behind at normal priority, running flat out, and after a few dozen of them
 * the machine had no capacity left for the evaluations that followed - which then timed out too,
 * leaking more threads. What this class can do about it is bound the damage:
 *
 * <ul>
 * <li>the worker is a <b>daemon</b> thread, so one that will not stop can never hold the JVM (or a
 * surefire fork) open,
 * <li>a worker that ignored the interrupt is dropped to {@link Thread#MIN_PRIORITY}, so it yields
 * to the evaluations that come after it instead of competing with them,
 * <li>it is counted, so the leak is visible rather than silent - see {@link #abandonedWorkers()}.
 * </ul>
 *
 * <p>
 * Thread priority is advisory and the platform may ignore it, so this is mitigation, not a cure.
 * The cure would be for the offending third party code to poll for interruption.
 *
 * <p>
 * Disposal is also much quicker than waiting out a fixed grace period: the previous code paid
 * {@code MoreExecutors.shutdownAndAwaitTermination(executor, 1, SECONDS)} on <em>every</em> call,
 * so a class with two hundred timeouts spent over three minutes purely asleep in cleanup.
 */
public final class TimeConstrainedExecutor {

  /**
   * How long to wait for an interrupted worker to unwind before giving up on it. An evaluation that
   * is polling the interrupt flag returns almost immediately; one that is not will not return no
   * matter how long the wait, so waiting longer only adds dead time.
   */
  private static final long TERMINATION_GRACE_MILLIS = 250;

  private static final AtomicLong ABANDONED_WORKERS = new AtomicLong();

  private final ExecutorService executor;

  private volatile Thread worker;

  private TimeConstrainedExecutor() {
    ThreadFactory factory = runnable -> {
      Thread thread = Config.THREAD_FACTORY.newThread(runnable);
      thread.setDaemon(true);
      worker = thread;
      return thread;
    };
    this.executor = Executors.newSingleThreadExecutor(factory);
  }

  /** A fresh single use executor for one time limited evaluation. */
  public static TimeConstrainedExecutor create() {
    return new TimeConstrainedExecutor();
  }

  /** The executor to hand to {@code SimpleTimeLimiter.create(..)}. */
  public ExecutorService service() {
    return executor;
  }

  /**
   * Shut the worker down, and if it will not stop, get it out of the way of everything that runs
   * after it. Safe to call more than once.
   */
  public void dispose() {
    executor.shutdownNow();
    try {
      if (executor.awaitTermination(TERMINATION_GRACE_MILLIS, TimeUnit.MILLISECONDS)) {
        return;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return;
    }
    Thread stuck = worker;
    if (stuck != null && stuck.isAlive()) {
      try {
        stuck.setPriority(Thread.MIN_PRIORITY);
      } catch (RuntimeException rex) {
        // a SecurityManager may refuse; the thread is a daemon either way
      }
      ABANDONED_WORKERS.incrementAndGet();
    }
  }

  /**
   * How many workers ignored their interrupt and were left running at minimum priority. A number
   * that climbs during a run is the signature of the starvation described above.
   */
  public static long abandonedWorkers() {
    return ABANDONED_WORKERS.get();
  }
}
