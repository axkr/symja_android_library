package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.builtin.QuantityFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions for generating Pie Charts.
 * <p>
 * Example: <code>PieChart[{1, 2, 3}]</code>
 * <code>PieChart[{1, 2, 3}, ChartLabels -> {"A", "B", "C"}]</code>
 * <p>
 * Several datasets, <code>PieChart[{{1, 2, 3}, {2, 2, 1}}]</code>, are drawn as rings around one
 * another, as Mathematica draws them.
 */
public class PieChart extends ListPlot {

  /** How wide a ring is, in the units the innermost pie has radius 1 in. */
  private static final double RING_THICKNESS = 1.0;

  /** The gap Mathematica leaves between two rings. */
  private static final double RING_GAP = 0.25;

  public PieChart() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }

    // a Dataset plots its rows and an Association its values, labelled by its keys
    IASTAppendable keyLabels = F.ListAlloc();
    IExpr dataArg = GraphicsOptions.chartData(engine.evaluate(ast.arg1()), keyLabels);
    if (!dataArg.isList()) {
      return F.NIL;
    }
    dataArg = QuantityFunctions.quantityPlotMagnitudes(dataArg,
        GraphicsOptions.optionValue(originalAST, S.TargetUnits, S.Automatic), engine);

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    IExpr chartStyle = GraphicsOptions.optionValue(originalAST, S.ChartStyle, S.Automatic);
    IExpr chartLabels = GraphicsOptions.chartLabels(
        GraphicsOptions.optionValue(originalAST, S.ChartLabels, S.None), keyLabels);
    IExpr chartLegends = GraphicsOptions.optionValue(originalAST, S.ChartLegends, S.None);
    IExpr baseStyle = GraphicsOptions.optionValue(originalAST, S.ChartBaseStyle, F.NIL);
    IExpr sectorOrigin = GraphicsOptions.optionValue(originalAST, S.SectorOrigin, S.Automatic);
    IExpr sectorSpacing = GraphicsOptions.optionValue(originalAST, S.SectorSpacing, S.None);
    IExpr labelingFunction =
        GraphicsOptions.optionValue(originalAST, S.LabelingFunction, S.Automatic);

    // The sectors begin at the left and follow one another clockwise, which is where Mathematica
    // starts them: PieChart[{1, 2, 3, 4}] draws its first sector from 144 to 180 degrees.
    double startAngle = Math.PI;
    int direction = -1; // -1 for Clockwise

    // SectorOrigin accepts a bare angle as well as {angle} and {angle, "Clockwise"}
    if (sectorOrigin != S.Automatic) {
      IExpr angleExpr = sectorOrigin;
      if (sectorOrigin.isList() && sectorOrigin.argSize() > 0) {
        IAST spec = (IAST) sectorOrigin;
        angleExpr = spec.arg1();
        for (int i = 1; i < spec.size(); i++) {
          IExpr entry = spec.get(i);
          if (entry.isString("Counterclockwise") || entry.isString("CounterClockwise")) {
            direction = 1;
          } else if (entry.isString("Clockwise")) {
            direction = -1;
          }
        }
      }
      if (angleExpr.isList() && angleExpr.argSize() > 0) {
        // the nested form {{angle, direction}, radius}
        angleExpr = ((IAST) angleExpr).arg1();
      }
      double angle = angleExpr.evalfNaN();
      if (Double.isFinite(angle)) {
        startAngle = angle;
      }
    }

    // SectorSpacing pushes each sector out along its own bisector, which is how the reference
    // separates them; None or Automatic leaves the pie whole
    double sectorOffset = 0.0;
    if (sectorSpacing.isNumber()) {
      double offset = sectorSpacing.evalfNaN();
      if (Double.isFinite(offset) && offset > 0) {
        sectorOffset = offset;
      }
    }

    // Handle Legends
    if (!chartLegends.isNone()) {
      if (chartLegends.isAutomatic()) {
        if (chartLabels.isList()) {
          graphicsOptions.setPlotLegends(chartLabels);
        }
      } else {
        graphicsOptions.setPlotLegends(chartLegends);
      }
    }

    // several datasets are drawn as rings around one another
    IAST rings = GraphicsOptions.chartDatasets((IAST) dataArg);

    // the largest datum sets the top of the scale, so a gradient runs across the whole pie
    double largest = 0.0;
    for (int d = 1; d < rings.size(); d++) {
      IExpr ringExpr = rings.get(d);
      if (ringExpr.isList()) {
        IAST ring = (IAST) ringExpr;
        for (int i = 1; i < ring.size(); i++) {
          double v = getDoubleVal(ring.get(i));
          if (!Double.isNaN(v)) {
            largest = Math.max(largest, v);
          }
        }
      }
    }
    PlotColorFunction sectorColors = PlotColorFunction
        .of(PlotColorFunction.Family.CHART,
            GraphicsOptions.optionValue(originalAST, S.ColorFunction, S.Automatic),
            GraphicsOptions.optionValue(originalAST, S.ColorFunctionScaling, S.True), S.PieChart,
            engine)
        .range(1, 0, largest).build();

    IASTAppendable primitives = F.ListAlloc();

    // Default EdgeForm(White) for sector separators
    primitives.append(F.EdgeForm(S.White));

    double outerReach = 0.0;
    boolean drew = false;
    for (int d = 1; d < rings.size(); d++) {
      IExpr ringExpr = rings.get(d);
      if (!ringExpr.isList()) {
        continue;
      }
      IAST dataList = (IAST) ringExpr;

      double total = 0.0;
      for (IExpr e : dataList) {
        double v = getDoubleVal(e);
        if (!Double.isNaN(v) && v > 0) {
          total += v;
        }
      }
      if (total <= 0) {
        continue;
      }

      double ringInner = (d - 1) * (RING_THICKNESS + RING_GAP);
      double ringOuter = ringInner + RING_THICKNESS;
      outerReach = Math.max(outerReach, ringOuter);

      double currentAngle = startAngle;
      int index = 0;
      for (int i = 1; i < dataList.size(); i++) {
        IExpr item = dataList.get(i);
        double val = getDoubleVal(item);
        // one place knows the wrappers; an unrecognised one used to make the value NaN and the
        // wedge simply did not appear
        PlotWrapper wrapper = PlotWrapper.of(item);
        IExpr label = wrapper.label.isPresent() ? wrapper.label : null;
        IExpr style = wrapper.style.isPresent() ? wrapper.style : null;

        // Global ChartLabels override
        if (chartLabels.isList() && i < ((IAST) chartLabels).size()) {
          label = ((IAST) chartLabels).get(i);
        }

        if (!Double.isNaN(val) && val > 0) {
          double sweep = val / total * 2.0 * Math.PI;
          double endAngle = currentAngle + (direction * sweep);
          double a1 = Math.min(currentAngle, endAngle);
          double a2 = Math.max(currentAngle, endAngle);

          // Color: a ColorFunction is given the value of the sector and outranks ChartStyle
          IExpr functionColor = sectorColors == null ? F.NIL : sectorColors.color(val);
          IExpr color;
          if (functionColor.isPresent()) {
            color = functionColor;
          } else if (style != null) {
            color = style;
          } else {
            color = GraphicsOptions.chartStyleColor(chartStyle, index);
          }
          boolean colorIsExplicit =
              functionColor.isPresent() || style != null || !chartStyle.isAutomatic();
          IExpr elementStyle = GraphicsOptions.chartElementStyle(baseStyle, color, colorIsExplicit);

          double midAngle = (a1 + a2) / 2.0;
          // an offset sector keeps its shape but sits further out along its own bisector
          double cx = sectorOffset * Math.cos(midAngle);
          double cy = sectorOffset * Math.sin(midAngle);

          IASTAppendable group = F.ListAlloc();
          if (elementStyle.isPresent()) {
            group.append(elementStyle);
          }
          // a ring has a hole, and a disk has none
          group.append(ringInner > 0
              ? GraphicsOptions.annulusSector(cx, cy, ringInner, ringOuter, a1, a2)
              : F.function(S.Disk, F.List(F.num(cx), F.num(cy)), F.num(ringOuter),
                  F.List(F.num(a1), F.num(a2))));
          // a tooltip covers the whole wedge
          primitives.append(wrapper.hasTooltip()
              ? F.binaryAST2(S.Tooltip, group, wrapper.tooltip)
              : group);
          drew = true;

          if (label != null) {
            double rLbl = ringInner > 0 ? (ringInner + ringOuter) / 2.0 : 0.667 * ringOuter;
            double lx = cx + rLbl * Math.cos(midAngle);
            double ly = cy + rLbl * Math.sin(midAngle);
            primitives.append(
                F.List(S.Black, F.Text(label, F.List(F.num(lx), F.num(ly)), F.List(F.C0, F.C0))));
          }

          // the value written on the sector, which LabelingFunction asks for
          IExpr valueLabel = GraphicsOptions.labelingText(labelingFunction, datum(item), engine);
          if (valueLabel.isPresent()) {
            double radius = ringInner
                + labelRadius(GraphicsOptions.labelingPlacement(labelingFunction)) * RING_THICKNESS;
            double lx = cx + radius * Math.cos(midAngle);
            double ly = cy + radius * Math.sin(midAngle);
            primitives.append(F.List(S.Black,
                F.Text(valueLabel, F.List(F.num(lx), F.num(ly)), F.List(F.C0, F.C0))));
          }

          currentAngle = endAngle;
          index++;
        }
      }
    }
    if (!drew) {
      return F.NIL;
    }

    // Bounds for PieChart are fixed -1..1 usually, widened for sectors pushed out from the
    // centre and for labels written outside the rim
    double reach = outerReach * 1.2 + sectorOffset;
    if (GraphicsOptions.labelingPlacement(labelingFunction) == GraphicsOptions.LABELING_ABOVE) {
      reach += 0.15;
    }
    graphicsOptions.setBoundingBox(new double[] {-reach, reach, -reach, reach});

    // Ensure Aspect Ratio 1
    if (graphicsOptions.aspectRatio() == S.Automatic) {
      graphicsOptions.setAspectRatio(F.C1);
    }

    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /** How far out from the centre of its ring a value label sits, for each placement. */
  private static double labelRadius(int placement) {
    switch (placement) {
      case GraphicsOptions.LABELING_ABOVE:
        return 1.15; // outside the rim
      case GraphicsOptions.LABELING_BELOW:
        return 0.3; // close in to the centre
      default:
        return 0.55;
    }
  }

  /** The datum itself, with every display wrapper taken off. */
  private static IExpr datum(IExpr item) {
    return PlotWrapper.strip(item);
  }

  private double getDoubleVal(IExpr expr) {
    try {
      if (PlotWrapper.isWrapper(expr)) {
        return getDoubleVal(PlotWrapper.strip(expr));
      }
      if (expr instanceof INumber)
        return ((INumber) expr).reDoubleValue();
      return expr.evalfNaN();
    } catch (Exception e) {
      return Double.NaN;
    }
  }

  @Override
  protected IExpr createGraphicsFunction(IAST primitives, GraphicsOptions graphicsOptions,
      IAST plotAST) {
    // PieChart usually has no axes or frame
    return super.createGraphicsFunction(primitives, graphicsOptions, plotAST);
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
    // charts and rasters draw their own extent, so the reference rendering does not clip
    defaults[GraphicsOptions.X_PLOTRANGECLIPPING] = S.False;

    // PieChart Defaults
    defaults[GraphicsOptions.X_AXES] = S.False;
    defaults[GraphicsOptions.X_FRAME] = S.False;
    defaults[GraphicsOptions.X_ASPECTRATIO] = F.C1;

    GraphicsOptions.OptionSet optionSet = GraphicsOptions.chartExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
