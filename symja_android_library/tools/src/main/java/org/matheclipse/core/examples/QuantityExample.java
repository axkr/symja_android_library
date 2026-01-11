package org.matheclipse.core.examples;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class QuantityExample {
  public static void main(String[] args) {
    try {
      F.initSymja();
      EvalEngine engine = new EvalEngine(false);
      // ExprEvaluator engine = new ExprEvaluator(false, 100);

      IAST function = F.Times(F.C7, F.Quantity(F.C2, F.$str("ft")));
      IExpr result = engine.evaluate(function);
      // print: 14[ft]
      System.out.println("Out[1]: " + result.toString());

    } catch (SyntaxError e) {
      // catch Symja parser errors here
      System.out.println(e.getMessage());
    } catch (MathException me) {
      // catch Symja math errors here
      System.out.println(me.getMessage());
    } catch (final Exception ex) {
      System.out.println(ex.getMessage());
    } catch (final StackOverflowError soe) {
      System.out.println(soe.getMessage());
    } catch (final OutOfMemoryError oome) {
      System.out.println(oome.getMessage());
    }
  }
}
