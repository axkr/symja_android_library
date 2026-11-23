package org.matheclipse.io.servlet;

import java.util.concurrent.Callable;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TimeLimits;

/**
 * The request time limit of the servlets for a JVM in which a worker thread cannot be waited for
 * with a timeout: the evaluation runs on the calling thread, under the engine deadline that
 * <code>TimeConstrained</code> uses, instead of on an executor whose <code>Future</code> is given
 * up after {@link Config#SERVER_REQUEST_TIMEOUT_SECONDS}.
 *
 * <p>
 * Used only when {@link Config#CHEEPRJ} is set - the notebook kernel running in the browser on
 * CheerpJ. There every Java thread shares the one thread of the page and nothing preempts a busy
 * one, so <code>Future.get(timeout)</code> never times out: on Java 17 it returns when the task has
 * ended by itself, and on Java 21 it hangs. A Symja evaluation does look at its engine's deadline,
 * so that is what limits it here. A server keeps the executor path; nothing changes for it.
 */
final class CallingThreadLimit {

  private CallingThreadLimit() {}

  /**
   * Run <code>work</code> on this thread within {@link Config#SERVER_REQUEST_TIMEOUT_SECONDS}.
   *
   * <p>
   * The evaluations of the servlets catch nearly every exception themselves and turn it into the
   * JSON of an error, the {@link org.matheclipse.core.eval.exception.TimeoutException} of the
   * deadline included - so a timeout often comes back as an ordinary answer rather than reaching
   * {@link TimeLimits}. The clock is therefore asked once more afterwards: if the deadline has
   * passed, whatever the work answered was cut short, and the answer is the timeout, as the
   * executor path gives it.
   *
   * <p>
   * Unlike the executor path, the engine is not asked to stop: when this returns, the evaluation has
   * already ended, and a stop flag left behind would only abort the next thing evaluated in it.
   *
   * @param engine the engine <code>work</code> evaluates in; the deadline is put on its stack
   * @param work the evaluation the servlet would have submitted to its executor
   * @param timeoutAnswer what the servlet answers when its request time limit runs out
   * @return the answer of <code>work</code>, or <code>timeoutAnswer</code>
   * @throws Exception what <code>work</code> throws, as the executor's <code>Future</code> would
   *         have reported it
   */
  static <T> T call(EvalEngine engine, Callable<T> work, T timeoutAnswer) throws Exception {
    final long millis = Config.SERVER_REQUEST_TIMEOUT_SECONDS * 1000L;
    final long deadline = EvalEngine.deadlineClockMillis() + millis;
    // TimeLimits takes a Supplier, which cannot throw a checked exception; it is carried across
    // wrapped and unwrapped again below, so the caller sees what the work threw
    final Exception[] thrown = new Exception[1];
    T answer = TimeLimits.call(engine, () -> {
      try {
        return work.call();
      } catch (RuntimeException rex) {
        // the deadline's own TimeoutException must reach TimeLimits as it is
        throw rex;
      } catch (Exception ex) {
        thrown[0] = ex;
        return null;
      }
    }, millis, () -> timeoutAnswer);
    if (EvalEngine.deadlineClockMillis() >= deadline) {
      return timeoutAnswer;
    }
    if (thrown[0] != null) {
      throw thrown[0];
    }
    return answer;
  }
}
