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

public class ListLogLogPlot extends ListPlot {
  public ListLogLogPlot() {}

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
    // PlotMarkers and Mesh are family options appended after the positional block, so they
    // are read from the call rather than by index
    graphicsOptions
        .setPlotMarkers(GraphicsOptions.optionValue(originalAST, S.PlotMarkers, S.Automatic));
    graphicsOptions.setMesh(GraphicsOptions.optionValue(originalAST, S.Mesh, S.None));
    graphicsOptions.readColorFunction(originalAST);
    graphicsOptions.applyPlotTheme(originalAST);
    IAST graphicsPrimitives = listPlot(ast, options, graphicsOptions, engine);

    if (graphicsPrimitives.isPresent()) {
      graphicsOptions.addPadding();
      // Use Natural Log "Log"
      graphicsOptions.addOption(F.Rule(S.$Scaling, F.List(F.stringx("Log"), F.stringx("Log"))));
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
    GraphicsOptions.OptionSet optionSet = GraphicsOptions.listPlotExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(),
            GraphicsOptions.listPlotDefaultOptionValues(false, false)));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
