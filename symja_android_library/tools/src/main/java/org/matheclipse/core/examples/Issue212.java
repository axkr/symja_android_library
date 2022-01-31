package org.matheclipse.core.examples;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class Issue212 {
  public static void main(String[] args) {
    try {
      ExprEvaluator util = new ExprEvaluator(false, (short) 100);

      ISymbol a = F.Dummy("a");
      ISymbol b = F.Dummy("b");
      ISymbol c = F.Dummy("c");
      IAST compound = F.CompoundExpression(F.Set(a, F.ZZ(13)), F.Set(b, F.ZZ(11)),
          F.Set(c, F.ZZ(12)), F.Plus(a, b, c));
      IExpr result = util.eval(compound);
      // print 36
      if (result.isInteger()) {
        IInteger n = (IInteger) result;
        System.out.println(n.toString());
      }

      // solve({x==y,y==2},{x,y})
      ISymbol x = F.Dummy("x");
      ISymbol y = F.Dummy("y");
      IAST solve = F.Solve( //
          F.List(F.Equal(x, y), F.Equal(y, F.ZZ(2))), //
          F.List(x, y));
      result = util.eval(solve);
      if (result.isListOfLists()) {
        IAST list = (IAST) result;
        for (int i = 1; i < list.size(); i++) {
          IExpr arg = list.get(i);
          if (arg.isListOfRules()) {
            IAST listOfRules = (IAST) arg;
            for (int j = 1; j < listOfRules.size(); j++) {
              IAST rule = (IAST) listOfRules.get(j);
              System.out.println(rule.toString());
            }
          }
        }
      }

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
