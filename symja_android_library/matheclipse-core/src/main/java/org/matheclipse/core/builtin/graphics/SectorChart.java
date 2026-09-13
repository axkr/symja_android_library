package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>SectorChart({{angle, radius}, ...})</code> - a pie whose sectors also differ in radius: the
 * first number of a pair is what the angle is proportional to, the second is how far the sector
 * reaches.
 *
 * <p>
 * The sectors start at the left, as Mathematica draws them, and follow one another clockwise.
 * <code>SectorOrigin -&gt; {angle, r}</code> starts them elsewhere and leaves a hole of radius
 * <code>r</code> in the middle, which turns the sectors into rings.
 */
public class SectorChart extends ListPlot {

  public SectorChart() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }
    IASTAppendable keyLabels = F.ListAlloc();
    IExpr dataArg = GraphicsOptions.chartData(engine.evaluate(ast.arg1()), keyLabels);
    if (!dataArg.isList() || ((IAST) dataArg).argSize() == 0) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);
    IExpr chartStyle = GraphicsOptions.optionValue(originalAST, S.ChartStyle, S.Automatic);
    IExpr chartLabels = GraphicsOptions.chartLabels(
        GraphicsOptions.optionValue(originalAST, S.ChartLabels, S.None), keyLabels);
    IExpr chartLegends = GraphicsOptions.optionValue(originalAST, S.ChartLegends, S.None);
    IExpr baseStyle = GraphicsOptions.optionValue(originalAST, S.ChartBaseStyle, F.NIL);
    IExpr sectorOrigin = GraphicsOptions.optionValue(originalAST, S.SectorOrigin, S.Automatic);
    IExpr labelingFunction =
        GraphicsOptions.optionValue(originalAST, S.LabelingFunction, S.Automatic);
    IExpr polarAxes = GraphicsOptions.optionValue(originalAST, S.PolarAxes, S.False);
    IExpr polarGridLines = GraphicsOptions.optionValue(originalAST, S.PolarGridLines, S.None);
    if (!chartLegends.isNone() && !chartLegends.isAutomatic()) {
      graphicsOptions.setPlotLegends(chartLegends);
    }

    // the sectors begin at the left and go clockwise, which is where Mathematica starts them
    double startAngle = Math.PI;
    int direction = -1;
    double innerRadius = 0.0;
    if (sectorOrigin.isPresent() && !sectorOrigin.isAutomatic()) {
      IExpr angleExpr = sectorOrigin;
      if (sectorOrigin.isList() && sectorOrigin.argSize() > 0) {
        IAST spec = (IAST) sectorOrigin;
        angleExpr = spec.arg1();
        for (int i = 2; i < spec.size(); i++) {
          IExpr entry = spec.get(i);
          if (entry.isString("Counterclockwise") || entry.isString("CounterClockwise")) {
            direction = 1;
          } else if (entry.isString("Clockwise")) {
            direction = -1;
          } else {
            double hole = entry.evalfNaN();
            if (Double.isFinite(hole) && hole > 0) {
              innerRadius = hole;
            }
          }
        }
      }
      double angle = angleExpr.isAutomatic() ? Double.NaN : angleExpr.evalfNaN();
      if (Double.isFinite(angle)) {
        startAngle = angle;
      }
    }

    IAST rings = GraphicsOptions.chartDatasets(sectorDatasets((IAST) dataArg));
    IASTAppendable primitives = F.ListAlloc();
    double reach = innerRadius;
    int colorIndex = 0;
    double ringBase = innerRadius;

    for (int d = 1; d < rings.size(); d++) {
      IExpr ringExpr = rings.get(d);
      if (!ringExpr.isList()) {
        continue;
      }
      IAST ring = (IAST) ringExpr;
      double total = 0.0;
      for (int i = 1; i < ring.size(); i++) {
        double width = angleValue(ring.get(i));
        if (Double.isFinite(width) && width > 0) {
          total += width;
        }
      }
      if (!(total > 0)) {
        continue;
      }

      double currentAngle = startAngle;
      double ringTop = ringBase;
      for (int i = 1; i < ring.size(); i++) {
        IExpr item = ring.get(i);
        double width = angleValue(item);
        double radius = radiusValue(item);
        if (!Double.isFinite(width) || width <= 0 || !Double.isFinite(radius)) {
          continue;
        }
        double sweep = width / total * 2.0 * Math.PI;
        double endAngle = currentAngle + direction * sweep;
        double a1 = Math.min(currentAngle, endAngle);
        double a2 = Math.max(currentAngle, endAngle);
        double outer = ringBase + Math.abs(radius);

        PlotWrapper wrapper = PlotWrapper.of(item);
        IExpr color = wrapper.style.isPresent() ? wrapper.style
            : GraphicsOptions.chartStyleColor(chartStyle, colorIndex);
        boolean colorIsExplicit = wrapper.style.isPresent() || !chartStyle.isAutomatic();
        IExpr style = GraphicsOptions.chartElementStyle(baseStyle, color, colorIsExplicit);

        IASTAppendable group = F.ListAlloc(2);
        if (style.isPresent()) {
          group.append(style);
        }
        group.append(ringBase > 0
            ? GraphicsOptions.annulusSector(0, 0, ringBase, outer, a1, a2)
            : F.function(S.Disk, F.List(F.C0, F.C0), F.num(outer), F.List(F.num(a1), F.num(a2))));
        primitives.append(
            wrapper.hasTooltip() ? F.binaryAST2(S.Tooltip, group, wrapper.tooltip) : group);

        double midAngle = (a1 + a2) / 2.0;
        double labelRadius = (ringBase + outer) / 2.0;
        IExpr label = null;
        if (wrapper.label.isPresent()) {
          label = wrapper.label;
        }
        if (chartLabels.isList() && i < ((IAST) chartLabels).size()) {
          label = ((IAST) chartLabels).get(i);
        }
        if (label != null) {
          primitives.append(F.List(S.Black,
              F.Text(label, F.List(F.num(labelRadius * Math.cos(midAngle)),
                  F.num(labelRadius * Math.sin(midAngle))), F.List(F.C0, F.C0))));
        }
        IExpr valueLabel =
            GraphicsOptions.labelingText(labelingFunction, PlotWrapper.strip(item), engine);
        if (valueLabel.isPresent()) {
          primitives.append(F.List(S.Black,
              F.Text(valueLabel, F.List(F.num(labelRadius * Math.cos(midAngle)),
                  F.num(labelRadius * Math.sin(midAngle))), F.List(F.C0, F.C0))));
        }

        currentAngle = endAngle;
        ringTop = Math.max(ringTop, outer);
        reach = Math.max(reach, outer);
        colorIndex++;
      }
      // the next dataset is a ring around this one
      ringBase = ringTop;
    }

    if (primitives.argSize() == 0) {
      return F.NIL;
    }

    double extent = reach * 1.05;
    double[] boundingBox = new double[] {-extent, extent, -extent, extent};
    IAST polar = PolarPlot.polarScale(polarAxes.isAutomatic() ? S.False : polarAxes, polarGridLines,
        boundingBox);
    if (polar.isPresent()) {
      // the scale belongs behind the sectors
      IASTAppendable withScale = F.ListAlloc(primitives.argSize() + 1);
      withScale.append(polar);
      withScale.appendArgs(primitives);
      primitives = withScale;
    }

    graphicsOptions.setBoundingBox(boundingBox);
    if (graphicsOptions.aspectRatio() == S.Automatic) {
      graphicsOptions.setAspectRatio(F.C1);
    }
    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /** <code>{{angle, radius}, ...}</code> is one ring; a list of those is several. */
  private static IAST sectorDatasets(IAST data) {
    for (int i = 1; i < data.size(); i++) {
      IExpr item = PlotWrapper.strip(data.get(i));
      if (item.isList() && item.argSize() > 0 && item.first().isList()) {
        return data;
      }
    }
    return F.List(data);
  }

  /** The first number of a pair, which the angle of the sector is proportional to. */
  private static double angleValue(IExpr item) {
    IExpr datum = PlotWrapper.strip(item);
    if (datum.isList() && datum.argSize() >= 1) {
      return ((IAST) datum).arg1().evalfNaN();
    }
    return datum.evalfNaN();
  }

  /** The second number of a pair: how far the sector reaches. */
  private static double radiusValue(IExpr item) {
    IExpr datum = PlotWrapper.strip(item);
    if (datum.isList() && datum.argSize() >= 2) {
      return ((IAST) datum).arg2().evalfNaN();
    }
    return 1.0;
  }

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
    return IFunctionEvaluator.ARGS_1_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, false);
    // charts draw their own extent, so the reference rendering does not clip
    defaults[GraphicsOptions.X_PLOTRANGECLIPPING] = S.False;
    defaults[GraphicsOptions.X_AXES] = S.False;
    defaults[GraphicsOptions.X_FRAME] = S.False;
    defaults[GraphicsOptions.X_ASPECTRATIO] = F.C1;

    GraphicsOptions.OptionSet optionSet = GraphicsOptions.polarExtras(GraphicsOptions.chartExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults)));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
