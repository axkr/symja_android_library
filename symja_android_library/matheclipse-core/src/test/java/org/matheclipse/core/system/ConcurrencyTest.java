package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrencyTest {

  @Test
  public void test001() throws InterruptedException, ExecutionException {

    String str = "SinDegree(x_):=Sin(x*Pi/180)";

    ExprEvaluator exprEvaluator = new ExprEvaluator();

    ExecutorService threadPool = Executors.newFixedThreadPool(4);
    List<Callable<IExpr>> tasks = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      tasks.add(() -> {
        exprEvaluator.eval(str);
        return exprEvaluator.eval("Definition[SinDegree]");
      });
      tasks.add(() -> exprEvaluator.eval("Int[Sin(x) * Cos(x) , x]"));
    }


    List<Future<IExpr>> futures = threadPool.invokeAll(tasks);
    threadPool.shutdown();

    for (Future<IExpr> future : futures) {
      IExpr iExpr = future.get();
      //System.out.println("iExpr = " + iExpr);
    }

  }

}
