package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ListLogLinearPlot extends ListPlot {

  public ListLogLinearPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (ToggleFeature.JS_ECHARTS) {
      return evaluateECharts(ast, argSize, options, engine, originalAST);
    }
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);
    IAST graphicsPrimitives = listPlot(ast, options, graphicsOptions, engine);

    if (graphicsPrimitives.isPresent()) {
      graphicsOptions.addPadding();
      // Use Natural Log "Log" instead of "Log10"
      graphicsOptions.addOption(F.Rule(S.$Scaling, F.List(F.stringx("Log"), S.None)));
      return createGraphicsFunction(graphicsPrimitives, graphicsOptions, ast);
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(),
        GraphicsOptions.listPlotDefaultOptionValues(false, false));
  }
}
