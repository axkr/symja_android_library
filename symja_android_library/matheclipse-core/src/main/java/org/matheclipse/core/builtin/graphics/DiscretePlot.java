package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class DiscretePlot extends ListPlot {

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    IExpr function = ast.arg1();
    IExpr arg2 = engine.evaluate(ast.arg2());
    if (arg2.isAST(S.List, 3, 5)) {
      IAST iteratorList = (IAST) arg2;
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
          GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);
          IASTMutable listPlot = ast.removeAtCopy(2);
          listPlot.set(1, tableValues);
          IAST graphicsPrimitives = listPlot(listPlot, options, graphicsOptions, engine);
          if (graphicsPrimitives.isPresent()) {
            graphicsOptions.addPadding();
            graphicsOptions.setFilling(S.Axis);
            return createGraphicsFunction(graphicsPrimitives, graphicsOptions, ast);
          }
        }
      }
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
