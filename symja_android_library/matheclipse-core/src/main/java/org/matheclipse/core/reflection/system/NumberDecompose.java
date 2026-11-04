package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>NumberDecompose(x, {u1, u2, ..., un})</code> - the coefficients <code>c1, ..., cn</code>
 * with <code>x == c1*u1 + ... + cn*un</code> for the non-increasing positive units
 * <code>ui</code>: every coefficient but the last is the integer part of the remainder divided by
 * its unit, the last one takes what remains.
 */
public class NumberDecompose extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr x = ast.arg1();
    if (!x.isRealResult() || !ast.arg2().isList() || ast.arg2().argSize() < 1) {
      return F.NIL;
    }
    IAST units = (IAST) ast.arg2();
    double previous = Double.POSITIVE_INFINITY;
    for (int i = 1; i < units.size(); i++) {
      double u;
      try {
        u = units.get(i).evalf();
      } catch (RuntimeException rex) {
        return F.NIL;
      }
      if (!(u > 0.0) || u > previous) {
        // `1` is not a list of nonincreasing positive numbers.
        return F.NIL;
      }
      previous = u;
    }
    IASTAppendable result = F.ListAlloc(units.argSize());
    IExpr remainder = x;
    for (int i = 1; i < units.size(); i++) {
      IExpr unit = units.get(i);
      IExpr quotient = engine.evaluate(F.Divide(remainder, unit));
      if (i == units.argSize()) {
        result.append(quotient);
      } else {
        IExpr coefficient = engine.evaluate(F.IntegerPart(quotient));
        if (!coefficient.isInteger()) {
          return F.NIL;
        }
        result.append(coefficient);
        remainder = engine.evaluate(F.Subtract(remainder, F.Times(coefficient, unit)));
      }
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
