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
 * number <code>x</code>. As for {@link SetPrecision}, nothing is kept outside of the
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
    final long accuracy = SetPrecision.digitsArgument(ast, arg2, engine);
    if (accuracy == Long.MIN_VALUE) {
      return F.NIL;
    }
    // the constants and functions of the expression are evaluated to the precision which the
    // expression as a whole needs for that accuracy
    long evalPrecision = accuracy;
    IExpr arg1 = ast.arg1();
    if (!arg1.isNumber() && arg1.isNumericFunction(true)) {
      double magnitude = Math.abs(arg1.evalf());
      if (Double.isFinite(magnitude) && magnitude > 0.0) {
        evalPrecision += (long) Math.floor(Math.log10(magnitude)) + 1L;
      }
    }
    return SetPrecision.convert(arg1, Math.max(evalPrecision, 1L), scale -> accuracy + scale,
        engine);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
