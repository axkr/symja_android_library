package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Plot a list of Points as a single line */
public class ListLinePlot extends ListPlot {

  public ListLinePlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    ast = withQuantityMagnitudes(withDatasetRows(ast), originalAST, engine);
    IExpr arg1 = ast.arg1();
    if (!checkList(engine, arg1)) {
      // `1` is not a list of numbers or pairs of numbers.
      return Errors.printMessage(ast.topHead(), "lpn", F.List(arg1), engine);
    }
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    GraphicsOptions graphicsOptions =
        setGraphicsOptions(options, GraphicsOptions.listPlotDefaultOptionKeys(), engine);
    // PlotMarkers and Mesh are family options appended after the positional block, so they
    // are read from the call rather than by index
    graphicsOptions
        .setPlotMarkers(GraphicsOptions.optionValue(originalAST, S.PlotMarkers, S.None));
    graphicsOptions.setMesh(GraphicsOptions.optionValue(originalAST, S.Mesh, S.None));
    graphicsOptions.readColorFunction(originalAST);
    graphicsOptions.applyPlotTheme(originalAST);
    graphicsOptions.readPassThroughOptions(originalAST);
    // GraphicsOptions graphicsOptions = new GraphicsOptions(engine);
    graphicsOptions.setJoined(true);
    IAST graphicsPrimitives = listPlot(ast, options, graphicsOptions, engine);
    if (graphicsPrimitives.isPresent()) {
      graphicsOptions.addPadding();
      return createGraphicsFunction(graphicsPrimitives, graphicsOptions, ast);
    }

    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet optionSet = GraphicsOptions.listPlotExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(),
            GraphicsOptions.listPlotDefaultOptionValues(false, true)));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
