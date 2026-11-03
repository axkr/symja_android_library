package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>BiweightMidvariance(list)</code>, <code>BiweightMidvariance(list, c)</code> - the robust
 * dispersion estimate
 *
 * <pre>
 * n * Sum((x-M)^2 * (1-u^2)^4) / Sum((1-u^2) * (1-5*u^2))^2
 * </pre>
 *
 * over the elements with <code>|u| &lt; 1</code>, where <code>u = (x-M) / (c*MAD)</code>,
 * <code>M</code> is the median, <code>MAD</code> the median of the absolute deviations from it and
 * <code>c</code> is <code>9</code> by default. A zero <code>MAD</code> gives
 * <code>Indeterminate</code>.
 */
public class BiweightMidvariance extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (!ast.arg1().isList() || ast.arg1().argSize() == 0) {
      return F.NIL;
    }
    IAST data = (IAST) ast.arg1();
    if (!data.forAll(x -> x.isReal())) {
      return F.NIL;
    }
    IExpr c = ast.isAST2() ? ast.arg2() : F.C9;
    if (!c.isReal() || !c.isPositive()) {
      return F.NIL;
    }
    IExpr median = engine.evaluate(F.Median(data));
    IExpr mad = engine.evaluate(F.Median(F.mapList(data, x -> F.Abs(F.Subtract(x, median)))));
    if (!mad.isReal()) {
      return F.NIL;
    }
    if (mad.isZero()) {
      return S.Indeterminate;
    }
    IExpr scale = engine.evaluate(F.Times(c, mad));
    IASTAppendable numerator = F.PlusAlloc(data.argSize());
    IASTAppendable denominator = F.PlusAlloc(data.argSize());
    for (IExpr x : data) {
      engine.checkTimeLimit();
      IExpr deviation = engine.evaluate(F.Subtract(x, median));
      IExpr u2 = engine.evaluate(F.Sqr(F.Divide(deviation, scale)));
      if (engine.evalTrue(F.GreaterEqual(u2, F.C1))) {
        continue;
      }
      IExpr oneMinus = F.Subtract(F.C1, u2);
      numerator.append(F.Times(F.Sqr(deviation), F.Power(oneMinus, F.C4)));
      denominator.append(F.Times(oneMinus, F.Subtract(F.C1, F.Times(F.C5, u2))));
    }
    if (denominator.argSize() == 0) {
      return S.Indeterminate;
    }
    return engine
        .evaluate(F.Divide(F.Times(F.ZZ(data.argSize()), numerator), F.Sqr(denominator)));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
