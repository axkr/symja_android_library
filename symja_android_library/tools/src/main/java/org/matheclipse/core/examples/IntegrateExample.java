package org.matheclipse.core.examples;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class IntegrateExample {

  public static void main(String[] args) {
    try {
      EvalEngine engine = EvalEngine.get();
      ExprParser parser = new ExprParser(engine, true);
      IExpr sinx = parser.parse("Sin(x^2)");
      VariablesSet varSet = new VariablesSet(sinx);
      IExpr result = engine.evaluate(F.Integrate(sinx, varSet.firstVariable()));
      // print: Sqrt(Pi/2)*FresnelS(Sqrt(2/Pi)*x)
      System.out.println(result.toString());
    } catch (SyntaxError e) {
      // catch Symja parser errors here
      System.out.println(e.getMessage());
    } catch (MathException me) {
      // catch Symja math errors here
      System.out.println(me.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    } catch (final StackOverflowError soe) {
      System.out.println(soe.getMessage());
    } catch (final OutOfMemoryError oome) {
      System.out.println(oome.getMessage());
    }
  }
}
