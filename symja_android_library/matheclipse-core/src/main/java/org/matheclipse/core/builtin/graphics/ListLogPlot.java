package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class ListLogPlot extends ListPlot {

  public ListLogPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (ToggleFeature.JS_ECHARTS) {
      return evaluateECharts(ast, argSize, options, engine, originalAST);
    }
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }

    // Use standard options initially (no data transformation)
    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    // Generate raw points
    IAST graphicsPrimitives = listPlot(ast, options, graphicsOptions, engine);

    if (graphicsPrimitives.isPresent()) {
      graphicsOptions.addPadding();
      // Force scaling option into the output Graphics object using Natural Log "Log"
      graphicsOptions.addOption(F.Rule(S.$Scaling, F.List(S.None, F.stringx("Log"))));
      return createGraphicsFunction(graphicsPrimitives, graphicsOptions, ast);
    }

    return F.NIL;
  }

  // Override setGraphicsOptions to NOT apply transformations to data generation
  @Override
  protected GraphicsOptions setGraphicsOptions(final IExpr[] options,
      final IBuiltInSymbol[] optionSymbols, final EvalEngine engine) {
    GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setGraphicOptions(optionSymbols, options, engine);
    // Do NOT set X/Y functions here; SVGGraphics handles the scaling
    return graphicsOptions;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(),
        GraphicsOptions.listPlotDefaultOptionValues(false, false));
  }
}
