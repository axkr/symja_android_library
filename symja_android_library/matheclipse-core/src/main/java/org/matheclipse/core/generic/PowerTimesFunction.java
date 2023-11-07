package org.matheclipse.core.generic;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Function4;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Analyze if a {@link S#Times} expression <code>factor1 * factor2</code> is of the form
 * <code>x^n_ * f_(m_*x)</code>. If <code>true</code> call the defined <code>function</code>.
 */
public class PowerTimesFunction {
  Function4<IAST, IExpr, IExpr, IExpr, IExpr> function;

  /**
   * Define the function which should be called, if the form <code>x^n_ * f_(m_*x)</code> was found.
   * 
   * @param function <code>function(f,x,n,m)</code>
   * @see #xPowNTimesFmx(IExpr, IExpr, IExpr, EvalEngine)
   */
  public PowerTimesFunction(Function4<IAST, IExpr, IExpr, IExpr, IExpr> function) {
    this.function = function;
  }

  /**
   * Analyze if <code>factor1 * factor2</code> is of the form <code>x^n_ * f_(m_*x)</code>. If
   * <code>true</code> call {@link #function}.
   * 
   * @param factor1
   * @param factor2
   * @param x
   * 
   * @return {@link F#NIL} if the expression is not of the form.
   */
  public IExpr xPowNTimesFmx(IExpr factor1, IExpr factor2, final IExpr x, EvalEngine engine) {
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
          IExpr temp = factor2;
          factor2 = factor1;
          factor1 = temp;
        }
      }
    }
    if (n.isPresent() && factor2.isAST1()) {
      IExpr m = F.NIL;
      IExpr t2Arg1 = factor2.first();
      if (t2Arg1.equals(x)) {
        m = F.C1;
      } else if (t2Arg1.isTimes()) {
        IAST timesAST = (IAST) t2Arg1;
        IASTAppendable[] filter = timesAST.filter(arg -> arg.equals(x));
        if (filter[0].argSize() == 1) {
          IExpr rest = engine.evaluate(filter[1]);
          if (rest.isFree(x)) {
            m = rest;
          }
        }
      }
      if (m.isPresent()) {
        IExpr temp = function.apply((IAST) factor2, x, n, m);
        if (temp.isPresent()) {
          return engine.evaluate(temp);
        }
      }
    }
    return F.NIL;
  }
}
