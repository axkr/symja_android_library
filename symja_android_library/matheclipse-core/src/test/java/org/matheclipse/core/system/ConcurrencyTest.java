package org.matheclipse.core.system;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

@RunWith(JUnit4.class)
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

}
