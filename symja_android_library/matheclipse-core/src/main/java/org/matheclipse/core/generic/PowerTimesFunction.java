package org.matheclipse.core.generic;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.Function5;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Analyze if an expression is of the form <code>x^n_. * f_(m_.*x^p_.)</code>. If <code>true</code>
 * call the defined <code>function</code>.
 */
public class PowerTimesFunction {
  Function5<IAST, IExpr, IExpr, IExpr, IExpr, IExpr> function;

  /**
   * Define the function which should be called, if the form <code>x^n_. * f_(m_.*x^p_.)</code> was
   * found.
   * 
   * @param function <code>function(f,x,n,m,p)</code>
   * @see #xPowNTimesFmx(IExpr, IExpr, IExpr, EvalEngine)
   */
  public PowerTimesFunction(Function5<IAST, IExpr, IExpr, IExpr, IExpr, IExpr> function) {
    this.function = function;
  }

  /**
   * Analyze if an expression is of the form <code>x^n_. * f_(m_.*x^p_.)</code>. If
   * <code>true</code> call {@link #function}.
   * 
   * @param fx
   * @param x
   * 
   * @return {@link F#NIL} if the expression is not of the form.
   */
  public IExpr xPowNTimesFmx(IAST fx, final IExpr x, EvalEngine engine) {
    if (fx.isTimes2()) {
      IExpr factor1 = fx.arg1();
      IExpr factor2 = fx.arg2();
      IExpr n = F.NIL;
      if (factor1.equals(x)) {
        n = F.C1;
      } else if (factor2.equals(x)) {
        n = F.C1;
        IExpr temp = factor2;
        factor2 = factor1;
        factor1 = temp;
      }
      if (n.isNIL()) {
        if (factor1.isPower() && factor1.base().equals(x)
            && (factor1.exponent().isInteger() || factor1.exponent().isVariable())) {
          if (!factor1.exponent().equals(x)) {
            n = factor1.exponent();
          }
        } else if (factor2.isPower() && factor2.base().equals(x)
            && (factor2.exponent().isInteger() || factor2.exponent().isVariable())) {
          if (!factor2.exponent().equals(x)) {
            n = factor2.exponent();
            factor2 = factor1;
          }
        }
      }
      if (n.isPresent() && factor2.argSize() >= 1) {
        return determineM(x, n, (IAST) factor2, engine);
      }
    } else if (!fx.isPlusTimesPower() && fx.argSize() >= 1) {
      return determineM(x, F.C0, fx, engine);
    }
    return F.NIL;
  }

  private IExpr determineM(final IExpr x, IExpr n, IAST builtInFunction, EvalEngine engine) {
    IExpr m = F.NIL;
    IExpr p = F.C1;
    IExpr t2Arg1 = builtInFunction.first();
    if (t2Arg1.equals(x)) {
      m = F.C1;
    } else if (t2Arg1.isTimes()) {
      IAST timesAST = (IAST) t2Arg1;
      IASTAppendable[] filter = timesAST.filter(arg -> arg.equals(x) //
          || (arg.isPower() && arg.base().equals(x)));
      if (filter[0].argSize() == 1) {
        IExpr rest = engine.evaluate(filter[1]);
        if (rest.isFree(x)) {
          IExpr xExpression = filter[0].arg1();
          if (xExpression.isPower()) {
            if ((xExpression.exponent().isInteger() || xExpression.exponent().isVariable())
                && !xExpression.exponent().equals(x)) {
              p = xExpression.exponent();
            } else {
              return F.NIL;
            }
          }
          m = rest;
        }
      }
    }
    if (m.isPresent()) {
      IExpr temp = function.apply(builtInFunction, x, n, m, p);
      if (temp.isPresent()) {
        return engine.evaluate(temp);
      }
    }
    return F.NIL;
  }
}
