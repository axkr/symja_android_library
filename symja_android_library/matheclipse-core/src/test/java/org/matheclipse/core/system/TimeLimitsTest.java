package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TimeLimits;
import org.matheclipse.core.eval.exception.TimeoutException;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The time limits of {@link TimeLimits}: <code>TimeConstrained</code> and the budgets of the
 * internal stages, nested in each other. The limits are short, so that the tests stay in the fast
 * tier.
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
public class TimeLimitsTest extends ExprEvaluatorTestCase {

  /**
   * A loop which runs much longer than every limit below, but ends after a few seconds: if a limit
   * doesn't stop it, the test fails instead of holding up the tier.
   */
  private static final String LONG_LOOP = "Do(i^2, {i, 10^7})";

  @Test
  public void testDefaultValue() {
    check("TimeConstrained(" + LONG_LOOP + ", 0.05, -1)", //
        "-1");
    check("TimeConstrained(" + LONG_LOOP + ", 0.05)", //
        "$Aborted");
    check("TimeConstrained(1+1, 0.05, -1)", //
        "2");
    // a loop which evaluates nothing passes the checkpoint of the loop built-ins
    check("TimeConstrained(While(True), 0.05, -1)", //
        "-1");
  }

  @Test
  public void testNested() {
    // the inner limit stops the inner evaluation, the outer one goes on
    check("TimeConstrained({TimeConstrained(" + LONG_LOOP + ", 0.05, -1), 2}, 5)", //
        "{-1,2}");
    // the outer limit runs out first: the outer default, not the inner one
    check("TimeConstrained({TimeConstrained(" + LONG_LOOP + ", 5, -1), 2}, 0.05, -2)", //
        "-2");
  }

  @Test
  public void testSequential() {
    // every limit belongs to its own call, and nothing is left behind for the next evaluation
    check("{TimeConstrained(" + LONG_LOOP + ", 0.05, -1), TimeConstrained(" + LONG_LOOP
        + ", 0.05, -2), 1+1}", //
        "{-1,-2,2}");
  }

  @Test
  public void testPauseIsInterrupted() {
    // a pause doesn't pass the evaluation loop: the watchdog's interrupt ends it
    long start = System.nanoTime();
    check("TimeConstrained({TimeConstrained(Pause(3), 0.05, -1), 2}, 5)", //
        "{-1,2}");
    assertTrue(System.nanoTime() - start < 2_000_000_000L);
  }

  @Test
  public void testTimeRemaining() {
    check("TimeConstrained(TimeConstrained(TimeRemaining(), 0.5), 5) <= 0.5", //
        "True");
    check("TimeConstrained(TimeConstrained(TimeRemaining(), 5), 0.5) <= 0.5", //
        "True");
    check("TimeRemaining()", //
        "Infinity");
  }

  @Test
  public void testJavaLoopIsInterrupted() {
    // a loop in Java code which only tests the interrupt flag, as JAS does
    String result = TimeLimits.call(() -> {
      while (true) {
        if (Thread.currentThread().isInterrupted()) {
          throw TimeoutException.TIMED_OUT;
        }
        Thread.onSpinWait();
      }
    }, 50, () -> "timeout");
    assertEquals("timeout", result);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void testSleepIsInterrupted() {
    long start = System.nanoTime();
    String result = TimeLimits.call(() -> {
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw TimeoutException.TIMED_OUT;
      }
      return "done";
    }, 50, () -> "timeout");
    assertEquals("timeout", result);
    assertFalse(Thread.currentThread().isInterrupted());
    assertTrue(System.nanoTime() - start < 2_000_000_000L);
  }

  @Test
  public void testInterruptFromOutsidePropagates() {
    // an abort is no timeout of the limit: it is passed on, and the flag stays set
    try {
      assertThrows(TimeoutException.class, () -> TimeLimits.call(() -> {
        Thread.currentThread().interrupt();
        TimeLimits.check();
        return "done";
      }, 5000, () -> "timeout"));
      assertTrue(Thread.currentThread().isInterrupted());
    } finally {
      Thread.interrupted();
    }
  }

  @Test
  public void testHostLimit() {
    // the limit of the host is the outermost frame: visible, and nested limits nest in it
    IExpr remaining = evaluator.evaluateWithTimeout("TimeRemaining() > 9", 10, TimeUnit.SECONDS,
        true, null);
    assertEquals("True", remaining.toString());
    IExpr nested = evaluator.evaluateWithTimeout(
        "{TimeConstrained(" + LONG_LOOP + ", 0.05, -1), TimeRemaining() > 2}", 3,
        TimeUnit.SECONDS, true, null);
    assertEquals("{-1,True}", nested.toString());
    // a longer nested limit is cut by the host's
    IExpr cut = evaluator.evaluateWithTimeout("TimeConstrained(" + LONG_LOOP + ", 30, -1)", 200,
        TimeUnit.MILLISECONDS, true, null);
    assertEquals("$Aborted", cut.toString());
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void testModesAreRestored() {
    // the work switches a mode and is cut short before it switches it back
    EvalEngine engine = EvalEngine.get();
    assertFalse(engine.isQuietMode());
    String result = TimeLimits.call(() -> {
      engine.setQuietMode(true);
      while (true) {
        TimeLimits.check();
        Thread.onSpinWait();
      }
    }, 50, () -> "timeout");
    assertEquals("timeout", result);
    assertFalse(engine.isQuietMode());
  }

  @Test
  public void testOtherThreadInTheSameEngine() throws InterruptedException {
    // a worker which a host gave up on still evaluates in the engine: its frame is not the
    // enclosing limit of the next evaluation, and its end doesn't touch that evaluation's limit
    EvalEngine engine = EvalEngine.get();
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch release = new CountDownLatch(1);
    Thread zombie = new Thread(() -> TimeLimits.call(engine, () -> {
      started.countDown();
      try {
        release.await();
      } catch (InterruptedException e) {
        // its own watchdog
        Thread.currentThread().interrupt();
        throw TimeoutException.TIMED_OUT;
      }
      return "done";
    }, 50, () -> "timeout"));
    zombie.start();
    started.await();
    double remaining = TimeLimits.call(engine, engine::getRemainingSeconds, 5000, () -> -1.0);
    assertTrue(remaining > 4.0, "remaining " + remaining);
    release.countDown();
    zombie.join();
    assertTrue(engine.getRemainingSeconds() < 0.0);
  }

  @Test
  public void testMillis() {
    assertEquals(3000L, TimeLimits.millis(3));
    // a huge number of seconds is no limit, not a negative one
    assertTrue(TimeLimits.millis(Long.MAX_VALUE / 10) > 0L);
  }

  @Test
  public void testWorkWhichEndsInTime() {
    assertEquals("done", TimeLimits.call(() -> "done", 5000, () -> "timeout"));
    // outside of every limit, the check never throws
    TimeLimits.check();
  }
}
