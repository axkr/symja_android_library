package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ConcurrencyTest {

  @Test
  public void test001() throws InterruptedException, ExecutionException {

    String str = "SinDegree(x_):=Sin(x*Pi/180)";

    ExecutorService threadPool = Executors.newFixedThreadPool(4);
    List<Callable<IExpr>> tasks = new ArrayList<>();
    for (int i = 0; i < 20; i++) {

      tasks.add(() -> {
        ExprEvaluator exprEvaluator = new ExprEvaluator();
        exprEvaluator.eval(str);
        return exprEvaluator.eval("Definition[SinDegree]");
      });
      tasks.add(() -> {
        ExprEvaluator exprEvaluator = new ExprEvaluator();
        return exprEvaluator.eval("Int[Sin(x) * Cos(x) , x]");
      });
    }


    List<Future<IExpr>> futures = threadPool.invokeAll(tasks);
    threadPool.shutdown();

    for (Future<IExpr> future : futures) {
      IExpr iExpr = future.get();
      // System.out.println("iExpr = " + iExpr);
    }

  }

  /**
   * A symbol's value read once, not twice.
   *
   * <p>
   * Symbols are global and the engines that evaluate them are not, so a <code>Clear</code> in one
   * thread lands in the middle of another thread's evaluation. <code>Symbol#evaluate</code> asked
   * <code>hasAssignedSymbolValue()</code> and then <code>assignedValue()</code>, and the value that
   * went away in between arrived at <code>ISymbol#evalAssignedValue</code> as a
   * <code>NullPointerException</code>. The window is wide - a few dozen iterations were enough to
   * find it - so the loop below is short.
   */
  @Test
  public void testClearWhileEvaluating() throws InterruptedException {
    final int iterations = 100_000;
    final ISymbol x = F.symbol("concurrencyTestX");
    final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();

    Thread writer = new Thread(() -> {
      EvalEngine.set(new EvalEngine(true));
      for (int i = 0; i < iterations && failure.get() == null; i++) {
        x.assignValue(F.ZZ(i), false);
        x.clearValue(null);
      }
    });
    Thread reader = new Thread(() -> {
      EvalEngine engine = new EvalEngine(true);
      EvalEngine.set(engine);
      for (int i = 0; i < iterations && failure.get() == null; i++) {
        try {
          if (x.evaluate(engine) == null) {
            failure.compareAndSet(null, new AssertionError("evaluate() returned null"));
          }
        } catch (Throwable t) {
          failure.compareAndSet(null, t);
        }
      }
    });

    writer.start();
    reader.start();
    writer.join();
    reader.join();
    x.clearValue(null);

    Throwable thrown = failure.get();
    if (thrown != null) {
      throw new AssertionError("evaluating a symbol while it is cleared: " + thrown, thrown);
    }
  }

  /**
   * Built-in rules use the formal symbols <code>k</code>, <code>j</code>, ... as the iterator
   * variables of <code>Sum</code> and <code>Product</code>. There is one of each in the JVM, and the
   * iterators used to assign their values to it, so concurrent evaluations read each other's
   * counters and left values behind (issue #1498). They are replaced by fresh symbols now.
   */
  @Test
  public void testFormalIteratorVariablesAreLocal() throws Exception {
    final String[] inputs = {"Simplify(D(Tan(x),{x,n}) /. n->4)", "LerchPhi(z,s,5)",
        "Hypergeometric1F1(-4,b,z)", "FunctionExpand(FactorialPower(x,4))"};
    final String[] expected = new String[inputs.length];
    ExprEvaluator single = new ExprEvaluator();
    for (int i = 0; i < inputs.length; i++) {
      expected[i] = single.eval(inputs[i]).toString();
    }

    final int threads = 8;
    ExecutorService threadPool = Executors.newFixedThreadPool(threads);
    try {
      List<Callable<String>> tasks = new ArrayList<>();
      for (int t = 0; t < threads; t++) {
        final int offset = t;
        tasks.add(() -> {
          ExprEvaluator exprEvaluator = new ExprEvaluator();
          for (int r = 0; r < 50; r++) {
            int i = (r + offset) % inputs.length;
            String result = exprEvaluator.eval(inputs[i]).toString();
            if (!expected[i].equals(result)) {
              return inputs[i] + " gave " + result;
            }
          }
          return null;
        });
      }
      for (Future<String> future : threadPool.invokeAll(tasks, 120, TimeUnit.SECONDS)) {
        assertFalse(future.isCancelled(), "timed out");
        assertNull(future.get());
      }
    } finally {
      threadPool.shutdownNow();
    }
    assertNull(S.k.assignedValue());
    assertNull(S.j.assignedValue());
    assertEquals(expected[0], new ExprEvaluator().eval(inputs[0]).toString());
  }

}
