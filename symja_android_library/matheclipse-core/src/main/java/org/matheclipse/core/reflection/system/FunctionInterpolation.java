package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>FunctionInterpolation(expr, {x, xmin, xmax})</code> - an <code>InterpolatingFunction</code>
 * which approximates <code>expr</code> as a function of <code>x</code> between <code>xmin</code>
 * and <code>xmax</code>.
 *
 * <p>
 * <code>expr</code> is evaluated with <code>x</code> set to the points of an equally spaced grid,
 * as <code>Block({x = xi}, expr)</code> does: a variable which only stands for an expression in
 * <code>x</code>, and a function defined for numeric arguments only, are evaluated at the points
 * too. The interpolation goes through the values at the grid points.
 */
public class FunctionInterpolation extends AbstractFunctionEvaluator {

  /** The number of intervals of the grid. */
  private static final int INTERVALS = 60;

  public FunctionInterpolation() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (!ast.arg2().isList3() || !ast.arg2().first().isSymbol()) {
      return F.NIL;
    }
    IAST range = (IAST) ast.arg2();
    ISymbol x = (ISymbol) range.arg1();
    IExpr min = engine.evaluate(range.arg2());
    IExpr max = engine.evaluate(range.arg3());
    if (!min.isRealResult() || !max.isRealResult()) {
      return F.NIL;
    }
    IASTAppendable points = F.ListAlloc(INTERVALS + 1);
    for (int i = 0; i <= INTERVALS; i++) {
      engine.checkTimeLimit();
      // the points are computed exactly for exact bounds, so the middle is 0.5 and not a bit less
      IExpr xi = engine.evalN(F.Plus(min, F.Times(F.QQ(i, INTERVALS), F.Subtract(max, min))));
      IExpr yi = engine.evalN(F.Block(F.list(F.Set(x, xi)), ast.arg1()));
      if (!yi.isNumber()) {
        return F.NIL;
      }
      points.append(F.list(xi, yi));
    }
    return engine.evaluate(F.unaryAST1(S.Interpolation, points));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
