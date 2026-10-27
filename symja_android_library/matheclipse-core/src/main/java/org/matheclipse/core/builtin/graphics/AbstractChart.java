package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The common part of the charts <code>BarChart</code>, <code>RectangleChart</code>,
 * <code>BubbleChart</code>, <code>Histogram</code>, <code>BoxWhiskerChart</code> and
 * <code>SectorChart</code>: a padded <code>Graphics</code> with an
 * optional legend, and the chart defaults - no clipping and, unless a chart sets its own, a
 * vertical axis only, no frame.
 */
public abstract class AbstractChart extends ListPlot {

  @Override
  protected IExpr createGraphicsFunction(IAST primitives, GraphicsOptions graphicsOptions,
      IAST plotAST) {
    graphicsOptions.addPadding();
    IASTAppendable result = F.Graphics(primitives);
    result.appendArgs(graphicsOptions.getGraphicsRules());
    return GraphicsOptions.legended(result);
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, false);
    // charts and rasters draw their own extent, so the reference rendering does not clip
    defaults[GraphicsOptions.X_PLOTRANGECLIPPING] = S.False;
    chartDefaults(defaults);

    GraphicsOptions.OptionSet optionSet = optionExtras(GraphicsOptions.chartExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults)));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }

  /** Add the chart's own options beyond the chart options; without an override none. */
  protected GraphicsOptions.OptionSet optionExtras(GraphicsOptions.OptionSet optionSet) {
    return optionSet;
  }

  /**
   * Set the chart's own defaults for frame, axes and aspect ratio in the list plot defaults.
   * Without an override the horizontal axis, usually hidden or replaced by labels, is not shown,
   * the vertical axis is, and there is no frame.
   */
  protected void chartDefaults(IExpr[] defaults) {
    defaults[GraphicsOptions.X_AXES] = F.List(S.False, S.True);
    defaults[GraphicsOptions.X_FRAME] = S.False;
  }
}
