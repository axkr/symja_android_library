package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

public class DiscretePlot extends ListPlot {
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr function = ast.arg1();
    if (ast.arg2().isAST(S.List, 3, 5)) {
      IAST iteratorList = (IAST) ast.arg2();
      IExpr variable = iteratorList.arg1();
      if (variable.isVariable()) {
        IExpr tableValues;
        if (function.isList()) {
          IASTMutable listPlotPoints = ((IAST) function).copy();
          for (int i = 1; i < listPlotPoints.size(); i++) {
            tableValues =
                S.Table.ofNIL(engine, F.List(variable, listPlotPoints.get(i)), iteratorList);
            if (!tableValues.isList()) {
              return F.NIL;
            }
            listPlotPoints.set(i, tableValues);
          }
          tableValues = listPlotPoints;
        } else {
          tableValues = S.Table.ofNIL(engine, F.List(variable, function), iteratorList);
        }
        if (tableValues.isList()) {
          GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
          IASTMutable listPlot = ast.removeAtCopy(2);
          listPlot.set(1, tableValues);
          IAST graphicsPrimitives = plot(listPlot, graphicsOptions, engine);
          if (graphicsPrimitives.isPresent()) {
            graphicsOptions.addPadding();
            IAST listOfOptions = F.List(F.Rule(S.Filling, S.Axis), //
                F.Rule(S.Axes, S.True), //
                graphicsOptions.plotRange());
            return createGraphicsFunction(graphicsPrimitives, listOfOptions, graphicsOptions);
          }
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }
}
