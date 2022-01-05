package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Plot a list of Points in 3 dimensions */
public class ListPointPlot3D extends AbstractEvaluator {
  public ListPointPlot3D() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() > 0) {
      if (ast.argSize() > 1) {
        final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
        IExpr colorFunction = options.getOption(S.ColorFunction);
        if (colorFunction.isPresent()) {
          // ... color function is set...
        }
      }
      if (ast.arg1().isList()) {
        IAST heightValueMatrix = (IAST) ast.arg1();
        int[] dimension = heightValueMatrix.isMatrix(false);
        if (dimension != null) {
          if (dimension[0] > 3) {
            if (dimension[1] == 3) {
              IASTAppendable pointList = F.ListAlloc(dimension[0]);
              for (int i = 1; i < heightValueMatrix.size(); i++) {
                IAST rowList = (IAST) heightValueMatrix.get(i);
                pointList.append(rowList);
              }
              return F.Graphics3D(F.Point(pointList));
            } else if (dimension[1] > 3) {
              IASTAppendable pointList = F.ListAlloc(dimension[0] * dimension[1]);
              for (int i = 1; i < heightValueMatrix.size(); i++) {
                IAST rowList = (IAST) heightValueMatrix.get(i);
                for (int j = 1; j < rowList.size(); j++) {
                  pointList.append(//
                      F.List(F.ZZ(i), F.ZZ(j), rowList.get(j)));
                }
              }
              return F.Graphics3D(F.Point(pointList));
            }
          }
        }
      }
    }
    return F.NIL;

  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
