package org.matheclipse.core.integrate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.exception.TimeoutException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

public class IntegrateTimeBudgetTest {

  @BeforeAll
  public static void initSymja() {
    // before any watchdog is armed: an interrupt which arrives while the static initializer of F
    // runs makes the class unusable for the rest of the JVM
    F.initSymja();
  }

  /** Work which looks at the interrupt flag only at its end, as a long JAS call does. */
  private static void busy(long millis) {
    long end = System.nanoTime() + millis * 1_000_000L;
    while (System.nanoTime() < end) {
      Thread.onSpinWait();
    }
    if (Thread.currentThread().isInterrupted()) {
      throw TimeoutException.TIMED_OUT;
    }
  }

  @Test
  public void testBudgetWhichRunsOut() {
    IExpr result = IntegrateTimeBudget.runWithin(() -> {
      busy(300);
      return F.C1;
    }, 50);
    assertTrue(result.isNIL());
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void testBudgetWhichIsKept() {
    IExpr result = IntegrateTimeBudget.runWithin(() -> F.C1, 5000);
    assertTrue(result.isOne());
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void testNestedBudgetsWhichRunOutTogether() {
    // Both watchdogs have fired before the work looks at the flag. The inner call must leave the
    // interrupt to the outer one: answering "did not finish" itself and clearing the flag would
    // let the rest of the outer work run without any limit, its watchdog being spent.
    boolean[] wentOn = new boolean[1];
    IExpr result = IntegrateTimeBudget.runWithin(() -> {
      IExpr inner = IntegrateTimeBudget.runWithin(() -> {
        busy(400);
        return F.C1;
      }, 60);
      wentOn[0] = true;
      return inner;
    }, 50);
    assertTrue(result.isNIL());
    assertFalse(wentOn[0]);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void testInnerBudgetAloneRunsOut() {
    boolean[] wentOn = new boolean[1];
    IExpr result = IntegrateTimeBudget.runWithin(() -> {
      IExpr inner = IntegrateTimeBudget.runWithin(() -> {
        busy(300);
        return F.C1;
      }, 50);
      wentOn[0] = inner.isNIL() && !Thread.currentThread().isInterrupted();
      return F.C2;
    }, 20000);
    assertTrue(wentOn[0]);
    assertTrue(result.equals(F.C2));
    assertFalse(Thread.currentThread().isInterrupted());
  }
}
