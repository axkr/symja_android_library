package org.matheclipse.core.eval.tasks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.ExitException;
import org.matheclipse.core.expression.S;

/**
 * What runs while an evaluation waits: the handlers of a socket that received something, and the
 * tasks a <code>ScheduledTask</code> is due to run.
 *
 * <p>
 * One thread evaluates. Everything that happens elsewhere - a selector thread noticing that bytes
 * arrived, a timer coming due - is turned into an {@link Event} and queued; the queue is drained on
 * the evaluating thread at the points where it would otherwise be idle: inside <code>Pause</code>,
 * inside a blocking socket read, in <code>SocketWaitNext</code>, and between the top-level
 * statements of a script. Nothing runs a handler behind a long computation, which is also how
 * <code>wolframscript</code> behaves.
 *
 * <p>
 * That is what makes a Wolfram Language main loop - <code>While[True, MicrotasksRun[]; Pause[0.01]]
 * </code> - able to serve a socket: the pause is where the work happens.
 */
public final class EventLoop {

  /** Something to do on the evaluating thread. */
  public interface Event {
    /** Run it. Anything thrown that is not an abort is reported and the loop carries on. */
    void dispatch(EvalEngine engine);
  }

  public static final EventLoop INSTANCE = new EventLoop();

  private final ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<Event>();

  private final ReentrantLock lock = new ReentrantLock();

  private final Condition wake = lock.newCondition();

  /** Scheduled tasks, the one due first at the head. */
  private final PriorityQueue<ScheduledTaskEntry> timers =
      new PriorityQueue<ScheduledTaskEntry>(Comparator.comparingLong(ScheduledTaskEntry::dueAt));

  private final java.util.Map<String, ScheduledTaskEntry> tasksByUuid =
      new java.util.concurrent.ConcurrentHashMap<String, ScheduledTaskEntry>();

  /**
   * Whether this thread is inside {@link #pump}. A handler that waits - and so pumps again - would
   * otherwise be re-entered from inside itself.
   */
  private final ThreadLocal<Boolean> pumping = ThreadLocal.withInitial(() -> Boolean.FALSE);

  private EventLoop() {}

  /** Queue something to run on the evaluating thread. Safe to call from any thread. */
  public void post(Event event) {
    queue.add(event);
    signal();
  }

  /** Add a scheduled task, and answer it. */
  public ScheduledTaskEntry schedule(ScheduledTaskEntry task) {
    lock.lock();
    try {
      timers.add(task);
      tasksByUuid.put(task.uuid(), task);
      wake.signalAll();
    } finally {
      lock.unlock();
    }
    return task;
  }

  /** Forget a scheduled task. */
  public boolean remove(String uuid) {
    lock.lock();
    try {
      ScheduledTaskEntry task = tasksByUuid.remove(uuid);
      if (task == null) {
        return false;
      }
      task.cancel();
      timers.remove(task);
      return true;
    } finally {
      lock.unlock();
    }
  }

  /** The tasks that have not been removed. */
  public List<ScheduledTaskEntry> tasks() {
    return new ArrayList<ScheduledTaskEntry>(tasksByUuid.values());
  }

  /** The task of that name, or <code>null</code>. */
  public ScheduledTaskEntry task(String uuid) {
    return tasksByUuid.get(uuid);
  }

  /**
   * Run what is waiting: every queued event, then every task that is due.
   *
   * @return how many things were run
   */
  public int pump(EvalEngine engine) {
    if (Boolean.TRUE.equals(pumping.get())) {
      // a handler is running; it will be back
      return 0;
    }
    pumping.set(Boolean.TRUE);
    try {
      int count = 0;
      Event event;
      while ((event = queue.poll()) != null) {
        count++;
        run(event, engine);
      }
      count += runDueTasks(engine);
      return count;
    } finally {
      pumping.set(Boolean.FALSE);
    }
  }

  private void run(Event event, EvalEngine engine) {
    try {
      event.dispatch(engine);
    } catch (ExitException | AbortException flow) {
      throw flow;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      Errors.printMessage(S.General, rex, engine);
    }
  }

  private int runDueTasks(EvalEngine engine) {
    int count = 0;
    while (true) {
      ScheduledTaskEntry due = null;
      lock.lock();
      try {
        ScheduledTaskEntry head = timers.peek();
        if (head == null || head.dueAt() > System.nanoTime()) {
          break;
        }
        due = timers.poll();
      } finally {
        lock.unlock();
      }
      if (due == null || due.isCancelled()) {
        continue;
      }
      count++;
      final ScheduledTaskEntry task = due;
      run(engine1 -> task.run(engine1), engine);
      lock.lock();
      try {
        if (!task.isCancelled() && task.reschedule()) {
          timers.add(task);
        } else {
          tasksByUuid.remove(task.uuid());
        }
      } finally {
        lock.unlock();
      }
    }
    return count;
  }

  /**
   * Wait until there is something to do, or until <code>millis</code> have passed.
   *
   * @return <code>true</code> if something arrived
   */
  public boolean awaitEvent(long millis) {
    if (!queue.isEmpty()) {
      return true;
    }
    long wait = millis;
    lock.lock();
    try {
      ScheduledTaskEntry head = timers.peek();
      if (head != null) {
        long untilDue = (head.dueAt() - System.nanoTime()) / 1_000_000L;
        wait = Math.min(wait, Math.max(0, untilDue));
      }
      if (wait <= 0) {
        return !queue.isEmpty() || head != null;
      }
      wake.await(wait, java.util.concurrent.TimeUnit.MILLISECONDS);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    } finally {
      lock.unlock();
    }
    return !queue.isEmpty();
  }

  /**
   * <code>Pause[seconds]</code>: wait, running whatever arrives while waiting.
   *
   * <p>
   * A pause is the ordinary way a Wolfram Language program yields, so it is the main place handlers
   * get to run.
   */
  public void pauseAndPump(double seconds, EvalEngine engine) {
    long deadline = System.nanoTime() + (long) (seconds * 1_000_000_000L);
    pump(engine);
    while (true) {
      long remainingMillis = (deadline - System.nanoTime()) / 1_000_000L;
      if (remainingMillis <= 0) {
        break;
      }
      awaitEvent(Math.min(remainingMillis, 50L));
      pump(engine);
    }
    pump(engine);
  }

  private void signal() {
    lock.lock();
    try {
      wake.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /** Forget every event and task. For tests, and for a session that starts again. */
  public void clear() {
    lock.lock();
    try {
      queue.clear();
      timers.clear();
      tasksByUuid.clear();
    } finally {
      lock.unlock();
    }
  }
}
