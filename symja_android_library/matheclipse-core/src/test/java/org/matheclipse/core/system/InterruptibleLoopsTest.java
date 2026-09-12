package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A search that runs longer than its deadline still has to stop when the deadline does.
 *
 * <p>
 * <code>TimeConstrained</code> and the engine's own time limit interrupt the thread they are
 * waiting on, and every check in <code>EvalEngine</code> is between two evaluation steps. A loop
 * inside one built-in never reaches such a check: the caller was handed its <code>$Aborted</code>
 * on time while the thread went on running at full speed, and enough of those accumulating starve
 * everything that follows. The loops below are the ones long enough for that to matter.
 */
public class InterruptibleLoopsTest extends ExprEvaluatorTestCase {

  /** Long enough that neither computation can finish on its own, short enough to wait for. */
  private static final long INTERRUPT_AFTER_MS = 300;

  /** Generous: the check is that the loop reacts at all, not how fast this machine is. */
  private static final long MUST_STOP_WITHIN_MS = 15_000;

  /**
   * Every Goldbach pair of a number whose half does not fit a Java int is a search that cannot
   * finish and a list that could not be held, so it is refused rather than begun.
   */
  @Test
  public void testGoldbachListRefusesAnEndlessSearch() {
    check("GoldbachList(10^40)", //
        "GoldbachList(10000000000000000000000000000000000000000)");
  }

  /** With a limit on the pairs the same number answers from the first primes it reaches. */
  @Test
  public void testGoldbachListWithAPairLimitStillAnswers() {
    check("GoldbachList(3325581707333960528,1)", //
        "{{9781,3325581707333950747}}");
  }

  @Test
  public void testGoldbachListStopsWhenInterrupted() throws InterruptedException {
    assertStopsWhenInterrupted("GoldbachList(2*(10^18+9),1000000)");
  }

  @Test
  public void testEulerEStopsWhenInterrupted() throws InterruptedException {
    assertStopsWhenInterrupted("EulerE(40000,x)");
  }

  /**
   * Evaluates <code>input</code> on its own thread, interrupts it, and gives it
   * {@link #MUST_STOP_WITHIN_MS} to notice.
   */
  private void assertStopsWhenInterrupted(String input) throws InterruptedException {
    final AtomicReference<String> finished = new AtomicReference<String>();
    Thread worker = new Thread(() -> {
      EvalEngine engine = new EvalEngine(true);
      EvalEngine.set(engine);
      try {
        IExpr result = engine.evaluate(engine.parse(input));
        finished.set(String.valueOf(result));
      } catch (Throwable t) {
        // the interruption itself, as TimeoutException - which is the point
      }
    }, "interruptible-" + input);
    worker.setDaemon(true);
    worker.start();

    Thread.sleep(INTERRUPT_AFTER_MS);
    if (!worker.isAlive()) {
      // it answered before the deadline: nothing to interrupt, and nothing this test can say
      return;
    }
    worker.interrupt();
    worker.join(MUST_STOP_WITHIN_MS);
    boolean stopped = !worker.isAlive();
    assertTrue(stopped, input + " ignored its interruption and is still running");
    assertFalse(finished.get() != null && F.NIL.toString().equals(finished.get()),
        "unexpected result " + finished.get());
  }
}
