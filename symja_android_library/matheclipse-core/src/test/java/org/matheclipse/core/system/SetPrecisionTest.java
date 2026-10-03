package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.junit.jupiter.api.Test;

/**
 * <code>SetPrecision</code> and <code>SetAccuracy</code>.
 */
public class SetPrecisionTest extends ExprEvaluatorTestCase {

  @Test
  public void testSetPrecisionNumbers() {
    check("SetPrecision(2/7, 30)", //
        "0.285714285714285714285714285714");
    check("Precision(SetPrecision(2/7, 30))", //
        "30");
    check("SetPrecision(0.75, 30)", //
        "0.75");
    // a machine number is filled up with binary zeros
    check("SetPrecision(0.3, 30)", //
        "0.299999999999999988897769753748");
    // a higher precision is cut
    check("SetPrecision(N(E, 50), 12)", //
        "2.71828182845");
    check("Precision(SetPrecision(N(E, 50), 12))", //
        "12");
    check("SetPrecision(2/7 + 3*I/11, 20)", //
        "0.28571428571428571428+I*0.27272727272727272727");
    // the exact zero has no digits
    check("SetPrecision(0, 20)", //
        "0");
  }

  @Test
  public void testSetPrecisionExpressions() {
    check("SetPrecision({2/7, 1.25, y}, 20)", //
        "{0.28571428571428571428,1.25,y}");
    check("SetPrecision(E, 25)", //
        "2.718281828459045235360287");
    check("SetPrecision(Sqrt(3) + y, 25)", //
        "1.732050807568877293527446+y");
    check("SetPrecision(g(2/7, 0), 20)", //
        "g(0.28571428571428571428,0)");
    check("Precision(SetPrecision(2/7, 30) + SetPrecision(3/7, 30))", //
        "30");
  }

  @Test
  public void testSetPrecisionMachineAndExact() {
    check("SetPrecision(N(E, 30), MachinePrecision)", //
        "2.718281828459045");
    check("SetPrecision(2/7, MachinePrecision)", //
        "0.285714");
    check("SetPrecision(1.25, Infinity)", //
        "5/4");
    // the exact binary value
    check("SetPrecision(0.3, Infinity)", //
        "5404319552844595/18014398509481984");
  }

  @Test
  public void testSetPrecisionMessages() {
    check("SetPrecision(2/7, 0)", //
        "SetPrecision(2/7,0)");
    check("SetPrecision(2/7, a)", //
        "SetPrecision(2/7,a)");
  }

  @Test
  public void testSetAccuracy() {
    check("SetAccuracy(2/7, 30)", //
        "0.285714285714285714285714285714");
    // 20 digits behind the decimal point and 4 in front of it
    check("Precision(SetAccuracy(4321.5, 20))", //
        "24");
    check("SetAccuracy({2/7, 654321, y}, 10)", //
        "{0.2857142857,654321,y}");
    check("SetAccuracy(E*1000, 20)", //
        "2718.28182845904523536028");
    check("SetAccuracy(1.25, Infinity)", //
        "5/4");
  }

  /**
   * The precision belongs to the call and to the engine it runs on: engines which work in
   * different threads with different precisions do not see each other, and an engine has the
   * numeric mode and the precision after the call which it had before.
   */
  @Test
  public void testSetPrecisionPerEngine() throws Exception {
    final int[] precisions = {18, 23, 31, 47};
    ExecutorService pool = Executors.newFixedThreadPool(precisions.length);
    try {
      List<Future<String>> futures = new ArrayList<>();
      for (int precision : precisions) {
        Callable<String> task = () -> {
          EvalEngine engine = new EvalEngine(true);
          EvalEngine.set(engine);
          // parsing is done before the precision is read: an input with a machine number in it
          // sets the precision of the engine to the one of that number
          IExpr expr = engine
              .parse("Precision(SetPrecision({2/7, Sqrt(3), 1.25+E}, " + precision + "))");
          final long enginePrecision = engine.getNumericPrecision();
          for (int i = 0; i < 200; i++) {
            String digits = engine.evaluate(expr).toString();
            if (!digits.equals(Integer.toString(precision))) {
              return "precision " + precision + " gave " + digits;
            }
            if (engine.isNumericMode() || engine.getNumericPrecision() != enginePrecision) {
              return "precision " + precision + " changed the engine: numeric mode "
                  + engine.isNumericMode() + ", precision " + engine.getNumericPrecision();
            }
          }
          return "";
        };
        futures.add(pool.submit(task));
      }
      for (Future<String> future : futures) {
        assertEquals("", future.get());
      }
    } finally {
      pool.shutdownNow();
    }
    // the engine of this test is as it was
    assertFalse(EvalEngine.get().isNumericMode());
  }
}
