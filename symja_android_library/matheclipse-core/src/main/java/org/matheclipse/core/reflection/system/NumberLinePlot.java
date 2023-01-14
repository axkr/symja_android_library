package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Plot along a number line */
public class NumberLinePlot extends ListPlot {

  public NumberLinePlot() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    double minValue = -1.0;
    double maxValue = -1.0;
    if (ast.arg1().isList()) {
      IASTAppendable result = F.ListAlloc();
      IAST list = (IAST) ast.arg1();
      if (!list.isListOfLists()) {
        list = F.List(list);
      }
      for (int i = 1; i < list.size(); i++) {
        IAST subList = (IAST) list.get(i);
        IASTAppendable numberLine = F.ListAlloc();
        for (int j = 1; j < subList.size(); j++) {
          try {
            double yValue = subList.get(j).evalf();
            numberLine.append(F.List(yValue, i));
            if (maxValue < yValue) {
              maxValue = yValue;
            }
            if (minValue > yValue) {
              minValue = yValue;
            }
          } catch (ArgumentTypeException ate) {
            //
          }
          result.append(numberLine);
        }

      }
      GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
      graphicsOptions.setJoined(false);
      ast = ast.setAtCopy(1, result);
      IAST graphicsPrimitives = listPlot(ast, graphicsOptions, engine);
      if (graphicsPrimitives.isPresent()) {
        graphicsOptions.addPadding();
        IAST listOfOptions = F.List(//
            F.Rule(S.Axes, F.List(S.True, S.False)), //
            F.Rule(S.PlotRange, F.List(F.List(minValue, maxValue), F.List(minValue, list.size()))) //
        );
        return createGraphicsFunction(graphicsPrimitives, listOfOptions, graphicsOptions);
      }
    }

    return F.NIL;

  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}
}
