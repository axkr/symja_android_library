package org.matheclipse.core.sympy.core.traversal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Runs a test body on a small thread stack, to prove that a walk doesn't depend on the stack size.
 * The depth of the tests is out of reach for a recursive walk on this stack.
 */
public final class SmallStack {
  private static final long STACK_BYTES = 256 * 1024;

  private SmallStack() {}

  public static void run(Runnable body) throws InterruptedException {
    final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();
    final Thread thread = new Thread(null, () -> {
      try {
        body.run();
      } catch (Throwable th) {
        failure.set(th);
      }
    }, "small-stack", STACK_BYTES);
    thread.start();
    thread.join();
    assertEquals(null, failure.get());
  }
}
