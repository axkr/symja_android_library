package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.builtin.QuantityFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
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
 */
public class PieChart extends ListPlot {

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

    // Default sector origin: {90 degree, "Clockwise"} -> Start at Pi/2, subtract angles
    double startAngle = Math.PI / 2.0;
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
        // Auto-generate labels from data if wrappers exist, or indices?
        // If ChartLabels is set, use that for legend?
        // For now, if ChartLegends -> Automatic, we might pass it through
        // but SVGGraphics needs explicit labels.
        // We'll leave it to SVGGraphics if it supports it, or set explicit if we have labels.
        if (chartLabels.isList()) {
          graphicsOptions.setPlotLegends(chartLabels);
        }
      } else {
        graphicsOptions.setPlotLegends(chartLegends);
      }
    }

    IAST dataList = (IAST) dataArg;
    // Calculate total for normalization
    double total = 0.0;
    int count = 0;

    // First pass: calculate total
    for (IExpr e : dataList) {
      double v = getDoubleVal(e);
      if (!Double.isNaN(v) && v > 0) {
        total += v;
        count++;
      }
    }

    if (total <= 0)
      return F.NIL;

    IASTAppendable primitives = F.ListAlloc();

    // Default EdgeForm(White) for sector separators
    primitives.append(F.EdgeForm(S.White));

    // the largest datum sets the top of the scale, so a gradient runs across the whole pie
    double largest = 0.0;
    for (int i = 1; i < dataList.size(); i++) {
      double v = getDoubleVal(dataList.get(i));
      if (!Double.isNaN(v)) {
        largest = Math.max(largest, v);
      }
    }
    PlotColorFunction sectorColors = PlotColorFunction
        .of(PlotColorFunction.Family.CHART,
            GraphicsOptions.optionValue(originalAST, S.ColorFunction, S.Automatic),
            GraphicsOptions.optionValue(originalAST, S.ColorFunctionScaling, S.True), S.PieChart,
            engine)
        .range(1, 0, largest).build();

    double currentAngle = startAngle;
    int index = 0;

    for (int i = 1; i < dataList.size(); i++) {
      IExpr item = dataList.get(i);
      double val = getDoubleVal(item);
      IExpr label = null;
      IExpr style = null;

      // Unwrap wrappers
      if (item.isAST(S.Labeled)) {
        label = ((IAST) item).arg2();
      } else if (item.isAST(S.Style)) {
        style = ((IAST) item).arg2();
      }

      // Global ChartLabels override
      if (chartLabels.isList() && i <= ((IAST) chartLabels).size()) {
        label = ((IAST) chartLabels).get(i);
      }

      if (!Double.isNaN(val) && val > 0) {
        // Fraction
        double fraction = val / total;
        double sweep = fraction * 2.0 * Math.PI;

        double endAngle = currentAngle + (direction * sweep);

        // Define Sector: Disk[{0,0}, 1, {ang1, ang2}]
        // Mma Disk usually takes {min, max} for counter-clockwise fill from min to max.
        // To get our specific wedge, we pass {min(start, end), max(start, end)}.
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
          color = getChartStyle(chartStyle, index);
        }
        boolean colorIsExplicit =
            functionColor.isPresent() || style != null || !chartStyle.isAutomatic();
        IExpr elementStyle = GraphicsOptions.chartElementStyle(baseStyle, color, colorIsExplicit);

        double midAngle = (a1 + a2) / 2.0;
        // an offset sector keeps its shape but sits further out along its own bisector
        double cx = sectorOffset * Math.cos(midAngle);
        double cy = sectorOffset * Math.sin(midAngle);

        // Group for sector
        IASTAppendable group = F.ListAlloc();
        if (elementStyle.isPresent())
          group.append(elementStyle);

        // Disk Primitive
        group.append(
            F.function(S.Disk, F.List(F.num(cx), F.num(cy)), F.C1, F.List(F.num(a1), F.num(a2))));
        primitives.append(group);

        // Label
        if (label != null) {
          // Position label at mid-angle, radius 0.7
          double rLbl = 0.7; // Internal label
          // For external: rLbl = 1.1;

          double lx = cx + rLbl * Math.cos(midAngle);
          double ly = cy + rLbl * Math.sin(midAngle);

          // Text Primitive
          // Text[lbl, {lx, ly}, {0,0}] (Centered)
          primitives.append(
              F.List(S.Black, F.Text(label, F.List(F.num(lx), F.num(ly)), F.List(F.C0, F.C0))));
        }

        // the value written on the sector, which LabelingFunction asks for
        IExpr valueLabel = GraphicsOptions.labelingText(labelingFunction, datum(item), engine);
        if (valueLabel.isPresent()) {
          double radius = labelRadius(GraphicsOptions.labelingPlacement(labelingFunction));
          double lx = cx + radius * Math.cos(midAngle);
          double ly = cy + radius * Math.sin(midAngle);
          primitives.append(F.List(S.Black,
              F.Text(valueLabel, F.List(F.num(lx), F.num(ly)), F.List(F.C0, F.C0))));
        }

        currentAngle = endAngle;
        index++;
      }
    }

    // Bounds for PieChart are fixed -1..1 usually, widened for sectors pushed out from the
    // centre and for labels written outside the rim
    double reach = 1.2 + sectorOffset;
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

  /** How far out from the centre a value label sits, for each placement. */
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

  /** The datum itself, with any {@code Labeled} or {@code Style} wrapper taken off. */
  private static IExpr datum(IExpr item) {
    if (item.isAST(S.Labeled) || item.isAST(S.Style)) {
      return ((IAST) item).arg1();
    }
    return item;
  }

  private IExpr getChartStyle(IExpr styleOption, int index) {
    if (styleOption.isAutomatic()) {
      return GraphicsOptions.chartStyleColorExpr(index);
    }
    if (styleOption.isList()) {
      return GraphicsOptions.getPlotStyle(styleOption, index);
    }
    return styleOption;
  }

  private double getDoubleVal(IExpr expr) {
    try {
      if (expr.isAST(S.Labeled) || expr.isAST(S.Style)) {
        return getDoubleVal(expr.first());
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
    // We override these defaults before creation if not set by user
    // However, ListPlot.createGraphicsFunction reads options.
    // We can force them off in setUp or here.
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
