package org.matheclipse.io.system;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IBuiltInSymbol;

/**
 * Every plot is drawn by <code>matheclipse-core</code>, whatever else is on the classpath.
 *
 * <p>
 * <code>matheclipse-image</code> used to install its own <code>ArrayPlot</code> and
 * <code>ListDensityPlot</code> over the ones core had already registered, so the same call gave a
 * JFreeChart bitmap or a <code>Graphics</code> depending on which modules happened to be loaded,
 * and the bitmap versions understood none of the options. The servlets and the Discord bot
 * re-registered <code>Plot</code>, <code>ListPlot</code> and <code>Histogram</code> in the same
 * way.
 *
 * <p>
 * This module has every other module on its test classpath and <code>IOInit.init()</code> has run
 * by the time the test does, so this is the assembled system: if anything installs a second
 * evaluator for a plot symbol again, the class named here stops coming from core and this fails.
 * The narrower {@link org.matheclipse.io.archunit.JFreeChartDependencyTest} bans the library that
 * was used to draw the bitmaps; this one covers the defect whatever it is drawn with.
 *
 * <p>
 * Plots a module legitimately owns - <code>GraphPlot</code> and <code>TreePlot</code> in
 * <code>matheclipse-graphtheory</code>, <code>MoleculePlot</code> and
 * <code>PeriodicTablePlot</code> in <code>matheclipse-chem</code> - are not listed: they are that
 * module's own symbols and core has no implementation of them to be replaced.
 */
public class PlotOwnershipTest extends AbstractTestCase {

  private static final String CORE_GRAPHICS = "org.matheclipse.core.builtin.graphics";

  /** The plot and chart symbols implemented in matheclipse-core. */
  private static final IBuiltInSymbol[] CORE_PLOTS = { //
      S.ArrayPlot, S.BarChart, S.BoxWhiskerChart, S.ComplexPlot, S.ContourPlot, S.DensityHistogram,
      S.DensityPlot, S.DiscretePlot, S.Histogram, S.ListContourPlot, S.ListDensityPlot,
      S.ListLinePlot, S.ListLogLinearPlot, S.ListLogLogPlot, S.ListLogPlot, S.ListPlot,
      S.ListPolarPlot, S.ListStepPlot, S.LogLinearPlot, S.LogLogPlot, S.LogPlot, S.MatrixPlot,
      S.NumberLinePlot, S.ParametricPlot, S.PieChart, S.Plot, S.PolarPlot, S.WordCloud, //
      S.ComplexPlot3D, S.ContourPlot3D, S.DiscretePlot3D, S.ListLinePlot3D, S.ListPlot3D,
      S.ListPointPlot3D, S.ParametricPlot3D, S.Plot3D, S.RevolutionPlot3D, S.SphericalPlot3D};

  @Test
  public void testEveryPlotIsEvaluatedByCore() {
    List<String> foreign = new ArrayList<String>();
    for (IBuiltInSymbol symbol : CORE_PLOTS) {
      IFunctionEvaluator evaluator = symbol.getEvaluator();
      String className = evaluator == null ? "<none>" : evaluator.getClass().getName();
      // catches an unregistered symbol as well - the placeholder evaluator is not in this package
      if (!className.startsWith(CORE_GRAPHICS)) {
        foreign.add(symbol.toString() + " -> " + className);
      }
    }
    assertTrue(foreign.isEmpty(), "not evaluated by matheclipse-core: " + foreign);
  }

  /** The two that were actually replaced, checked through the evaluator rather than the class. */
  @Test
  public void testReplacedPlotsReturnGraphics() {
    check("Head(ArrayPlot({{1, 2}, {3, 4}}))", //
        "Graphics");
    check("Head(ListDensityPlot({{1, 2}, {3, 4}}))", //
        "Graphics");
    check("Head(Histogram({1, 2, 2, 3}))", //
        "Graphics");
    check("Head(ListPlot({1, 2, 3}))", //
        "Graphics");
    check("Head(Plot(Sin(x), {x, 0, 2*Pi}))", //
        "Graphics");
  }
}
