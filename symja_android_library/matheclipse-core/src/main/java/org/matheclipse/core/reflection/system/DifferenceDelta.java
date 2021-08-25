package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 *
 *
 * <pre><code>DifferenceDelta(f(x), x)
 * </code></pre>
 *
 * <p>generates a forward difference <code>f(x+1) - f(x)</code>
 *
 * <pre><code>DifferenceDelta(f(x), {x,n,h})
 * </code></pre>
 *
 * <p>generates a higher-order forward difference for integers <code>n</code>.
 *
 * <p>See:
 *
 * <ul>
 *   <li><a
 *       href="https://en.wikipedia.org/wiki/Finite_difference#Higher-order_differences">Wikipedia -
 *       Finite difference - Higher-order differences</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre><code>&gt;&gt; DifferenceDelta(b(a),a)
 * -b(a)+b(1+a)
 *
 * &gt;&gt; DifferenceDelta(b(a),{a,5,c})
 * -b(a)+5*b(a+c)-10*b(a+2*c)+10*b(a+3*c)-5*b(a+4*c)+b(a+5*c)
 *
 * </code></pre>
 */
public class DifferenceDelta extends AbstractCoreFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    IExpr arg2 = ast.arg2();
    if (arg2.isList()) {
      if (arg2.isList2() || arg2.isList3()) {
        if (arg2.second().isInteger() && arg2.second().isNonNegativeResult()) {
          IExpr stepH = F.C1;
          int n = arg2.second().toIntDefault();
          if (n >= 1) {
            IExpr symbolX = arg2.first();
            if (arg2.isList3()) {
              stepH = arg2.getAt(3);
            }
            IASTAppendable result = F.PlusAlloc(n + 1);

            for (int i = 0; i <= n; i++) {
              IExpr diffTerm = F.subs(arg1, symbolX, F.Plus(F.Times(F.ZZ(i), stepH), symbolX));
              result.append(F.Times(F.Power(F.CN1, n - i), F.Binomial(n, i), diffTerm));
            }
            return result;
          }
        }
      }
      return F.NIL;
    }
    IExpr f2 = F.subs(arg1, arg2, F.Plus(F.C1, arg2));
    return F.Subtract(f2, arg1);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_2;
  }
}
