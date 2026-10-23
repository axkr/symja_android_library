package org.matheclipse.core.system;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Starts Symja from two threads at the same time in a fresh JVM: one initializes {@code F}, the
 * other creates an {@code EvalEngine}. Exits with 0 when both are done within a minute and with 1
 * when they wait for each other. Run by {@link ClassInitRaceTest}.
 */
public final class ClassInitRace {

  private ClassInitRace() {}

  public static void main(String[] args) throws InterruptedException {
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(2);
    Thread f = new Thread(() -> run(start, done, "org.matheclipse.core.expression.F"));
    Thread engine = new Thread(() -> {
      try {
        start.await();
        new org.matheclipse.core.eval.EvalEngine(false);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        done.countDown();
      }
    });
    f.setDaemon(true);
    engine.setDaemon(true);
    f.start();
    engine.start();
    start.countDown();
    boolean finished = done.await(60, TimeUnit.SECONDS);
    System.exit(finished ? 0 : 1);
  }

  private static void run(CountDownLatch start, CountDownLatch done, String className) {
    try {
      start.await();
      Class.forName(className);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    } finally {
      done.countDown();
    }
  }
}
