package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>ComplexListPlot({z1, z2, ...})</code> - the points <code>{Re(z), Im(z)}</code> of the
 * complex numbers, drawn by <code>ListPlot</code> with the options of the call. A list of lists is
 * several datasets, an association its values, and a wrapper such as <code>Tooltip</code> or
 * <code>Style</code> stays around the points of what it wraps.
 */
public class ComplexListPlot extends AbstractEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr points = points(engine.evaluate(ast.arg1()), engine);
    if (points.isNIL()) {
      return F.NIL;
    }
    IASTAppendable listPlot = F.ast(S.ListPlot, ast.argSize());
    listPlot.append(points);
    for (int i = 2; i < ast.size(); i++) {
      listPlot.append(ast.get(i));
    }
    return engine.evaluate(listPlot);
  }

  /** The data with every number replaced by its point, or <code>F.NIL</code>. */
  private static IExpr points(IExpr data, EvalEngine engine) {
    if (data.isAssociation()) {
      data = engine.evaluate(F.unaryAST1(S.Values, data));
    }
    if (data.isList()) {
      IAST list = (IAST) data;
      IASTAppendable result = F.ListAlloc(list.argSize());
      for (IExpr element : list) {
        IExpr point = points(element, engine);
        if (point.isNIL()) {
          return F.NIL;
        }
        result.append(point);
      }
      return result;
    }
    if (data.isNumericFunction(true)) {
      IExpr z = engine.evalN(data);
      if (!z.isNumber()) {
        return F.NIL;
      }
      return F.list(engine.evaluate(F.Re(z)), engine.evaluate(F.Im(z)));
    }
    if (data.isAST() && data.argSize() >= 1 && (data.isAST(S.Tooltip) || data.isAST(S.Style)
        || data.isAST(S.Labeled) || data.isAST(S.Callout) || data.isAST(S.Legended))) {
      IExpr inner = points(data.first(), engine);
      return inner.isNIL() ? F.NIL : ((IAST) data).setAtCopy(1, inner);
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }
}
