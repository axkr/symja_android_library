package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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
          // PlotMarkers and Mesh are family options appended after the positional block, so they
          // are read from the call rather than by index
          graphicsOptions
              .setPlotMarkers(GraphicsOptions.optionValue(originalAST, S.PlotMarkers, S.Automatic));
          graphicsOptions.setMesh(GraphicsOptions.optionValue(originalAST, S.Mesh, S.None));
          graphicsOptions.readColorFunction(originalAST);
          graphicsOptions.applyPlotTheme(originalAST);
          IASTMutable listPlot = ast.removeAtCopy(2);
          listPlot.set(1, tableValues);
          IAST graphicsPrimitives = listPlot(listPlot, options, graphicsOptions, engine);
          if (graphicsPrimitives.isPresent()) {
            graphicsOptions.addPadding();
            // ExtentSize widens the stems into bars of that width, so the plot reads as a
            // histogram rather than a lollipop chart
            IExpr extentSize = GraphicsOptions.optionValue(originalAST, S.ExtentSize, S.Automatic);
            IAST bars = extentBars(tableValues, extentSize);
            if (bars.isPresent()) {
              IASTAppendable withBars = F.ListAlloc(graphicsPrimitives.size() + 1);
              withBars.append(bars);
              withBars.appendArgs(graphicsPrimitives);
              graphicsPrimitives = withBars;
              graphicsOptions.setFilling(S.None);
              return createGraphicsFunction(graphicsPrimitives, graphicsOptions, ast);
            }
            // stems to the axis are the default, but an explicit Filling must win
            if (GraphicsOptions.optionValue(originalAST, S.Filling, S.Automatic) == S.Automatic) {
              graphicsOptions.setFilling(S.Axis);
            } else {
              graphicsOptions
                  .setFilling(GraphicsOptions.optionValue(originalAST, S.Filling, S.Axis));
            }
            return createGraphicsFunction(graphicsPrimitives, graphicsOptions, ast);
          }
        }
      }
    }
    return F.NIL;
  }

  /**
   * Bars from the axis to each value, as {@code ExtentSize} asks for.
   *
   * @param extentSize the option value; {@code Full} fills the whole step, a number gives that
   *        fraction of it, and anything else means no bars
   * @return the bar primitives, or {@link F#NIL} when the option did not ask for any
   */
  private static IAST extentBars(IExpr points, IExpr extentSize) {
    if (extentSize == null || extentSize == S.Automatic || extentSize.isNone()
        || !points.isList()) {
      return F.NIL;
    }
    double width = 1.0;
    if (extentSize != S.Full) {
      double w = extentSize.evalfNaN();
      if (!Double.isFinite(w) || w <= 0) {
        return F.NIL;
      }
      width = w;
    }
    IAST list = (IAST) points;
    IASTAppendable bars = F.ListAlloc(list.size());
    for (int i = 1; i < list.size(); i++) {
      IExpr point = list.get(i);
      if (!point.isList() || ((IAST) point).argSize() < 2) {
        continue;
      }
      double x = ((IAST) point).arg1().evalfNaN();
      double y = ((IAST) point).arg2().evalfNaN();
      if (!Double.isFinite(x) || !Double.isFinite(y)) {
        continue;
      }
      double half = width / 2.0;
      bars.append(F.Rectangle(F.List(F.num(x - half), F.C0), F.List(F.num(x + half), F.num(y))));
    }
    return bars.argSize() > 0 ? bars : F.NIL;
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
