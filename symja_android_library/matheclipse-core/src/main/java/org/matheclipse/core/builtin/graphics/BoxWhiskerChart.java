package org.matheclipse.core.builtin.graphics;

import java.util.Arrays;
import org.matheclipse.core.builtin.QuantityFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions for generating Box-and-Whisker Charts.
 * <p>
 * Example: <code>BoxWhiskerChart[RandomReal[NormalDistribution[0, 1], {5, 20}], "Notched"]</code>
 */
public class BoxWhiskerChart extends ListPlot {

  public BoxWhiskerChart() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }

    IExpr dataArg =
        GraphicsOptions.chartData(engine.evaluate(ast.arg1()), F.ListAlloc());
    if (!dataArg.isList()) {
      return F.NIL;
    }
    dataArg = QuantityFunctions.quantityPlotMagnitudes(dataArg,
        GraphicsOptions.optionValue(originalAST, S.TargetUnits, S.Automatic), engine);

    // Check for "Outliers" and "Notched" specifications in the second argument
    boolean showOutliers = false;
    boolean showNotched = false;

    if (argSize >= 2) {
      IExpr arg2 = ast.arg2();
      String str = arg2.toString();
      // Handle string inputs or list of strings
      if (str.contains("Outliers")) {
        showOutliers = true;
      }
      if (str.contains("Notched")) {
        showNotched = true;
      }
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    IExpr chartStyle = S.Automatic;
    IExpr barSpacing = S.Automatic;
    IExpr chartLabels = S.None;
    IExpr chartLegends = S.None;

    // the options array holds resolved values, not the rules the caller wrote,
    // so the option rules are read back off the original call
    for (IExpr opt : originalAST) {
      if (opt.isRuleAST()) {
        IExpr key = ((IAST) opt).arg1();
        IExpr val = ((IAST) opt).arg2();
        if (key == S.ChartStyle) {
          chartStyle = val;
        } else if (key == S.BarSpacing) {
          barSpacing = val;
        } else if (key == S.ChartLabels) {
          chartLabels = val;
        } else if (key == S.ChartLegends) {
          chartLegends = val;
        }
      }
    }

    if (!chartLegends.isNone()) {
      if (!chartLegends.isAutomatic()) {
        graphicsOptions.setPlotLegends(chartLegends);
      }
    }

    IExpr chartElementsOption =
        GraphicsOptions.optionValue(originalAST, S.ChartElements, S.Automatic);
    IExpr chartElements = chartElementsOption.isAutomatic() || chartElementsOption.isNone() ? F.NIL
        : chartElementsOption;

    IAST dataList = (IAST) dataArg;

    boolean isMultiDataset = false;
    if (dataList.argSize() > 0 && dataList.arg1().isList()) {
      isMultiDataset = true;
    }

    IAST datasets;
    if (isMultiDataset) {
      datasets = dataList;
    } else {
      datasets = F.List(dataList);
    }

    IASTAppendable primitives = F.ListAlloc();

    double minVal = Double.MAX_VALUE;
    double maxVal = -Double.MAX_VALUE;

    int count = datasets.argSize();

    double spacing = 0.5; // Default spacing
    if (barSpacing.isNumber()) {
      double spacingValue = barSpacing.evalfNaN();
      if (!Double.isNaN(spacingValue)) {
        spacing = spacingValue;
      }
    }

    double boxWidth = 1.0 / (1.0 + spacing);

    for (int i = 1; i <= count; i++) {
      IExpr datasetExpr = datasets.get(i);
      if (!datasetExpr.isList())
        continue;
      IAST dataset = (IAST) datasetExpr;

      // Extract numbers
      double[] values = new double[dataset.size()];
      int n = 0;
      for (IExpr e : dataset) {
        try {
          if (e instanceof INumber) {
            values[n++] = ((INumber) e).reDoubleValue();
          } else {
            double d = e.evalDouble();
            if (Double.isFinite(d))
              values[n++] = d;
          }
        } catch (Exception ex) {
        }
      }

      if (n == 0)
        continue;

      // Calculate Statistics
      double[] sorted = Arrays.copyOf(values, n);
      Arrays.sort(sorted);

      double min = sorted[0];
      double max = sorted[n - 1];
      double q1 = getQuantile(sorted, 0.25);
      double median = getQuantile(sorted, 0.50);
      double q3 = getQuantile(sorted, 0.75);
      double iqr = q3 - q1;

      double whiskerBottom = min;
      double whiskerTop = max;

      // Outlier Logic
      IASTAppendable outlierPoints = F.ListAlloc();
      if (showOutliers) {
        double lowerFence = q1 - 1.5 * iqr;
        double upperFence = q3 + 1.5 * iqr;

        // Find adjacent values (values inside fences)
        whiskerBottom = q1;
        whiskerTop = q3;

        // Find lowest value >= lowerFence
        for (int k = 0; k < n; k++) {
          if (sorted[k] >= lowerFence) {
            whiskerBottom = sorted[k];
            break;
          }
          outlierPoints.append(F.Point(F.List(F.num(i), F.num(sorted[k]))));
        }

        // Find highest value <= upperFence
        for (int k = n - 1; k >= 0; k--) {
          if (sorted[k] <= upperFence) {
            whiskerTop = sorted[k];
            break;
          }
          outlierPoints.append(F.Point(F.List(F.num(i), F.num(sorted[k]))));
        }

        if (whiskerBottom > q1)
          whiskerBottom = q1;
        if (whiskerTop < q3)
          whiskerTop = q3;

        if (min < minVal)
          minVal = min;
        if (max > maxVal)
          maxVal = max;
      } else {
        // Standard Range
        if (min < minVal)
          minVal = min;
        if (max > maxVal)
          maxVal = max;
      }

      double x = i;
      double w = boxWidth * 0.8;
      double xLeft = x - w / 2.0;
      double xRight = x + w / 2.0;

      IASTAppendable group = F.ListAlloc();

      // 1. Whiskers & Caps (Gray style)
      group.append(F.Directive(F.GrayLevel(0.4), F.Thickness(0.003)));

      // Lower Whisker
      group.append(
          F.Line(F.List(F.List(F.num(x), F.num(whiskerBottom)), F.List(F.num(x), F.num(q1)))));
      // Upper Whisker
      group
          .append(F.Line(F.List(F.List(F.num(x), F.num(q3)), F.List(F.num(x), F.num(whiskerTop)))));

      // Caps
      double capW = w / 3.0;
      group.append(F.Line(F.List(F.List(F.num(x - capW), F.num(whiskerBottom)),
          F.List(F.num(x + capW), F.num(whiskerBottom)))));
      group.append(F.Line(F.List(F.List(F.num(x - capW), F.num(whiskerTop)),
          F.List(F.num(x + capW), F.num(whiskerTop)))));

      // 2. Box
      IExpr color = getChartStyle(chartStyle, i - 1);
      group.append(color);
      group.append(F.EdgeForm(F.None));

      if (showNotched) {
        // Calculate Notch
        // Standard notch extent: +/- 1.57 * IQR / sqrt(N)
        double notchExtent = 1.57 * (iqr / Math.sqrt(n));
        double notchLower = median - notchExtent;
        double notchUpper = median + notchExtent;

        // Indentation depth (e.g. 25% of width from each side)
        double indentXLeft = xLeft + w * 0.25;
        double indentXRight = xRight - w * 0.25;

        // Polygon Points (Counter-Clockwise from bottom-left)
        // 1. Bottom Left (xLeft, q1)
        // 2. Notch Bottom Left (xLeft, notchLower)
        // 3. Notch Inner Left (indentXLeft, median)
        // 4. Notch Top Left (xLeft, notchUpper)
        // 5. Top Left (xLeft, q3)
        // 6. Top Right (xRight, q3)
        // 7. Notch Top Right (xRight, notchUpper)
        // 8. Notch Inner Right (indentXRight, median)
        // 9. Notch Bottom Right (xRight, notchLower)
        // 10. Bottom Right (xRight, q1)

        IAST polyPts = F.List(F.List(F.num(xLeft), F.num(q1)),
            F.List(F.num(xLeft), F.num(notchLower)), F.List(F.num(indentXLeft), F.num(median)),
            F.List(F.num(xLeft), F.num(notchUpper)), F.List(F.num(xLeft), F.num(q3)),
            F.List(F.num(xRight), F.num(q3)), F.List(F.num(xRight), F.num(notchUpper)),
            F.List(F.num(indentXRight), F.num(median)), F.List(F.num(xRight), F.num(notchLower)),
            F.List(F.num(xRight), F.num(q1)));

        group.append(F.Polygon(polyPts));

      } else if (chartElements.isPresent()) {
        // ChartElements replaces the box with the caller's own drawing, stretched to fill it
        group.append(fittedElement(chartElements, xLeft, q1, xRight, q3));
      } else {
        // Standard Box
        group
            .append(F.Rectangle(F.List(F.num(xLeft), F.num(q1)), F.List(F.num(xRight), F.num(q3))));
      }

      // 3. Median Line (White)
      group.append(F.Directive(S.White, F.Thickness(0.005)));
      if (showNotched) {
        // Median is inside the notch
        double indentXLeft = xLeft + w * 0.25;
        double indentXRight = xRight - w * 0.25;
        group.append(F.Line(F.List(F.List(F.num(indentXLeft), F.num(median)),
            F.List(F.num(indentXRight), F.num(median)))));
      } else {
        group.append(F.Line(
            F.List(F.List(F.num(xLeft), F.num(median)), F.List(F.num(xRight), F.num(median)))));
      }

      primitives.append(group);

      // 4. Draw Outliers
      if (showOutliers && outlierPoints.argSize() > 0) {
        IASTAppendable outGroup = F.ListAlloc();
        outGroup.append(S.Black); // Use Black for outliers as requested
        outGroup.append(F.PointSize(0.015));
        outGroup.appendArgs(outlierPoints);
        primitives.append(outGroup);
      }

      // Labels
      if (chartLabels != S.None && chartLabels.isList()) {
        if (i <= ((IAST) chartLabels).argSize()) {
          IExpr lbl = ((IAST) chartLabels).get(i);
          primitives.append(
              F.List(S.Black, F.Text(lbl, F.List(F.num(x), F.num(minVal)), F.List(F.C0, F.C1))));
        }
      }
    }

    double yRange = maxVal - minVal;
    if (yRange == 0)
      yRange = 1.0;
    graphicsOptions.setBoundingBox(
        new double[] {0.5, count + 0.5, minVal - 0.1 * yRange, maxVal + 0.1 * yRange});

    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /**
   * The caller's own drawing, stretched to fill the box.
   *
   * <p>
   * The drawing is measured and then mapped onto the rectangle, so a graphic drawn at any scale
   * lands in the right place. Only the primitives that carry an obvious extent are measured; a
   * drawing made of anything else is assumed to occupy the unit square.
   */
  private static IExpr fittedElement(IExpr element, double x0, double y0, double x1, double y1) {
    IExpr body =
        element.isAST(S.Graphics) && element.size() > 1 ? ((IAST) element).arg1() : element;
    double[] bounds = {Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE};
    accumulateBounds(body, bounds);
    if (bounds[0] > bounds[1] || bounds[2] > bounds[3]) {
      bounds = new double[] {0, 1, 0, 1};
    }
    double width = bounds[1] - bounds[0];
    double height = bounds[3] - bounds[2];
    if (width <= 0) {
      width = 1;
    }
    if (height <= 0) {
      height = 1;
    }
    double sx = (x1 - x0) / width;
    double sy = (y1 - y0) / height;
    IAST matrix = F.List(F.List(F.num(sx), F.C0), F.List(F.C0, F.num(sy)));
    IAST vector = F.List(F.num(x0 - bounds[0] * sx), F.num(y0 - bounds[2] * sy));
    return F.binaryAST2(S.GeometricTransformation, body, F.List(matrix, vector));
  }

  /** Grow {@code bounds}, as {xMin, xMax, yMin, yMax}, to hold a drawing. */
  private static void accumulateBounds(IExpr expr, double[] bounds) {
    if (expr.isList()) {
      IAST list = (IAST) expr;
      if (isPoint(list)) {
        addPoint(list, bounds);
        return;
      }
      for (int i = 1; i < list.size(); i++) {
        accumulateBounds(list.get(i), bounds);
      }
      return;
    }
    if (!expr.isAST()) {
      return;
    }
    IAST ast = (IAST) expr;
    if (ast.isAST(S.Disk) || ast.isAST(S.Circle)) {
      double cx = 0;
      double cy = 0;
      if (ast.argSize() >= 1 && ast.arg1().isList() && ast.arg1().size() == 3) {
        cx = ((IAST) ast.arg1()).arg1().evalfNaN();
        cy = ((IAST) ast.arg1()).arg2().evalfNaN();
      }
      double r = 1;
      if (ast.argSize() >= 2 && ast.arg2().isNumber()) {
        r = ast.arg2().evalfNaN();
      }
      if (Double.isFinite(cx) && Double.isFinite(cy) && Double.isFinite(r)) {
        grow(bounds, cx - r, cy - r);
        grow(bounds, cx + r, cy + r);
      }
      return;
    }
    if (ast.isAST(S.Rectangle) || ast.isAST(S.Line) || ast.isAST(S.Polygon) || ast.isAST(S.Point)) {
      for (int i = 1; i < ast.size(); i++) {
        accumulateBounds(ast.get(i), bounds);
      }
      if (ast.isAST(S.Rectangle) && ast.argSize() == 1) {
        // Rectangle[{x, y}] is the unit square with that corner
        double[] corner =
            new double[] {Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE};
        accumulateBounds(ast.arg1(), corner);
        if (corner[0] <= corner[1]) {
          grow(bounds, corner[0] + 1, corner[2] + 1);
        }
      }
    }
  }

  /** Whether a list is a single {x, y} point rather than a list of them. */
  private static boolean isPoint(IAST list) {
    return list.argSize() == 2 && list.arg1().isNumericFunction(true)
        && list.arg2().isNumericFunction(true);
  }

  private static void addPoint(IAST point, double[] bounds) {
    double x = point.arg1().evalfNaN();
    double y = point.arg2().evalfNaN();
    if (Double.isFinite(x) && Double.isFinite(y)) {
      grow(bounds, x, y);
    }
  }

  private static void grow(double[] bounds, double x, double y) {
    bounds[0] = Math.min(bounds[0], x);
    bounds[1] = Math.max(bounds[1], x);
    bounds[2] = Math.min(bounds[2], y);
    bounds[3] = Math.max(bounds[3], y);
  }

  private double getQuantile(double[] sorted, double phi) {
    int n = sorted.length;
    if (n == 0)
      return Double.NaN;
    if (n == 1)
      return sorted[0];

    double index = phi * (n - 1);
    int lhs = (int) index;
    double delta = index - lhs;

    if (lhs >= n - 1)
      return sorted[n - 1];

    return (1 - delta) * sorted[lhs] + delta * sorted[lhs + 1];
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

  @Override
  protected IExpr createGraphicsFunction(IAST primitives, GraphicsOptions graphicsOptions,
      IAST plotAST) {
    graphicsOptions.addPadding();
    IASTAppendable result = F.Graphics(primitives);
    result.appendArgs(graphicsOptions.getListOfRules());
    return result;
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

    defaults[GraphicsOptions.X_AXES] = F.List(S.False, S.False);
    defaults[GraphicsOptions.X_FRAME] = S.True;

    GraphicsOptions.OptionSet optionSet = GraphicsOptions.chartExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
