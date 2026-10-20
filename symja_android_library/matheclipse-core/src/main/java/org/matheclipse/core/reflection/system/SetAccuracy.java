package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.ParserConfig;

/**
 * <code>SetAccuracy(expr, a)</code> gives every number of <code>expr</code> <code>a</code> digits
 * to the right of the decimal point, which is the precision <code>a+Log10(Abs(x))</code> for the
 * number <code>x</code>; a zero, and a number of which no digit is left, becomes a zero of the
 * accuracy <code>a</code>. As for {@link SetPrecision}, nothing is kept outside of the
 * {@link EvalEngine} of the call.
 */
public class SetAccuracy extends AbstractFunctionEvaluator {

  public SetAccuracy() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg2 = ast.arg2();
    if (arg2.isInfinity()) {
      return SetPrecision.convert(ast.arg1(), SetPrecision.EXACT, scale -> SetPrecision.EXACT,
          engine);
    }
    if (arg2.equals(S.MachinePrecision)) {
      arg2 = F.ZZ(ParserConfig.MACHINE_PRECISION);
    }
    final double accuracy = SetPrecision.digitsArgument(ast, arg2, "accbd", engine);
    if (Double.isNaN(accuracy)) {
      return F.NIL;
    }
    return SetPrecision.setDigits(ast.arg1(), accuracy, true, engine);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
