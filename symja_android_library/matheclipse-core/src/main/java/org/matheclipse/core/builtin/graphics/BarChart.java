package org.matheclipse.core.builtin.graphics;

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
 * Functions for generating Bar Charts.
 * <p>
 * Example: <code>BarChart[{1, 2, 3, 5}]</code>
 * <code>BarChart[{1, 2, 3}, ChartStyle -> {Red, Green, Blue}]</code>
 */
public class BarChart extends ListPlot {

  public BarChart() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1) {
      return F.NIL;
    }

    IExpr dataArg = engine.evaluate(ast.arg1());
    if (!dataArg.isList()) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    // Parse BarChart specific options
    IExpr chartStyle = S.Automatic;
    IExpr barSpacing = S.Automatic;
    IExpr chartLabels = S.None;
    IExpr chartLegends = S.None;

    for (IExpr opt : options) {
      if (opt.isRuleAST()) {
        IExpr key = ((IAST) opt).arg1();
        IExpr val = ((IAST) opt).arg2();
        if (key.equals(S.ChartStyle)) {
          chartStyle = val;
        } else if (key.equals(S.BarSpacing)) {
          barSpacing = val;
        } else if (key.equals(S.ChartLabels)) {
          chartLabels = val;
        } else if (key.equals(S.ChartLegends)) {
          chartLegends = val;
        }
      }
    }

    // Pass chart legends to graphics options if present
    if (!chartLegends.isNone()) {
      if (chartLegends.isAutomatic()) {
        // Basic automatic legends: use indices 1, 2, 3...
        // More complex logic needed for labelled data wrappers
        // For now, let SVGGraphics handle if it supports it, or generate here.
        // Currently SVGGraphics supports PlotLegends mainly.
        // We can map ChartLegends -> PlotLegends for basic compatibility if list.
      } else {
        graphicsOptions.setPlotLegends(chartLegends);
      }
    }

    IAST dataList = (IAST) dataArg;
    // Check if multiple datasets (stacked/grouped) or single series
    // Basic implementation: Single series {y1, y2, ...}

    IASTAppendable primitives = F.ListAlloc();
    double minVal = Double.MAX_VALUE;
    double maxVal = -Double.MAX_VALUE;

    int count = dataList.argSize();

    // Bar dimensions
    double spacing = 0.1; // Default Automatic
    if (barSpacing.isNumber()) {
      spacing = barSpacing.evalf();
    }

    // Width logic: Total width per item = 1.0. Bar width + spacing = 1.0?
    // Bars are centered at 1, 2, 3.
    // Width depends on spacing. Standard seems to be filling most of the step.
    double barWidth = 1.0 / (1.0 + spacing);

    // Generate Bars
    for (int i = 1; i <= count; i++) {
      IExpr item = dataList.get(i);
      double val = Double.NaN;
      IExpr label = null;
      IExpr specificStyle = null;

      // Handle wrappers: Labeled[val, lbl], Style[val, style]
      if (item.isAST(S.Labeled)) {
        val = getDoubleVal(((IAST) item).arg1());
        label = ((IAST) item).arg2();
      } else if (item.isAST(S.Style)) {
        val = getDoubleVal(((IAST) item).arg1());
        specificStyle = ((IAST) item).arg2(); // Simple extraction
      } else {
        val = getDoubleVal(item);
      }

      if (!Double.isNaN(val)) {
        if (val < minVal)
          minVal = val;
        if (val > maxVal)
          maxVal = val;

        // X center is i.
        double x = i;
        double y = val;

        double x0 = x - barWidth / 2.0;
        double x1 = x + barWidth / 2.0;
        double y0 = 0.0;
        double y1 = y;

        // Determine Color
        IExpr color = S.Automatic;
        if (specificStyle != null) {
          color = specificStyle;
        } else if (chartStyle.isList()) {
          // Cycle through styles
          color = GraphicsOptions.getPlotStyle(chartStyle, i - 1);
        } else if (!chartStyle.equals(S.Automatic)) {
          color = chartStyle;
        } else {
          // Default BarChart color (often a specific yellow/orange or blue)
          // Mathematica 12+ uses a gradient or distinct palette.
          // Let's use a nice default similar to Histogram but maybe distinct.
          color = F.RGBColor(0.6, 0.6, 0.9); // Periwinkle blueish
        }

        // Primitive Group
        IASTAppendable group = F.ListAlloc();

        // Style
        if (!color.equals(S.Automatic)) {
          group.append(color);
        }

        // Tooltip wrapper? (Not implemented in SVG yet)
        // Bar Rectangle
        group.append(F.Rectangle(F.List(F.num(x0), F.num(y0)), F.List(F.num(x1), F.num(y1))));

        primitives.append(group);

        // Label
        if (label != null || (!chartLabels.equals(S.None) && chartLabels.isList())) {
          IExpr txt = label;
          if (txt == null && chartLabels.isList()) {
            txt = ((IAST) chartLabels).get(i); // get(i) is safe? dataList size vs labels size?
          }

          if (txt != null) {
            // Place label above bar? or below axis?
            // Standard is below x-axis for categories, or Callout.
            // Simple text primitive:
            // Text[lbl, {x, y + offset}]
            // Use black for text
            primitives.append(
                F.List(S.Black, F.Text(txt, F.List(F.num(x), F.num(y1)), F.List(F.C0, F.CN1)))); // Offset
                                                                                                 // {0,
                                                                                                 // -1}
                                                                                                 // puts
                                                                                                 // text
                                                                                                 // above
                                                                                                 // point
          }
        }
      }
    }

    // Bounds
    double yMin = Math.min(0.0, minVal);
    double yMax = Math.max(0.0, maxVal);
    // Add some padding
    yMax += (yMax - yMin) * 0.05;

    graphicsOptions.setBoundingBox(new double[] {0.5, count + 0.5, yMin, yMax});

    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  private double getDoubleVal(IExpr expr) {
    try {
      if (expr instanceof INumber)
        return ((INumber) expr).reDoubleValue();
      return expr.evalDouble();
    } catch (Exception e) {
      return Double.NaN;
    }
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
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, false);

    // BarChart Defaults
    defaults[GraphicsOptions.X_AXES] = F.List(S.False, S.True); // X axis usually hidden/replaced by
                                                                // labels, Y axis shown
    defaults[GraphicsOptions.X_FRAME] = S.False;

    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(), defaults);
  }
}
