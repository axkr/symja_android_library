package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
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

    IExpr dataArg = engine.evaluate(ast.arg1());
    if (!dataArg.isList()) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    IExpr chartStyle = S.Automatic;
    IExpr chartLabels = S.None;
    IExpr chartLegends = S.None;
    // Default sector origin: {90 degree, "Clockwise"} -> Start at Pi/2, subtract angles
    double startAngle = Math.PI / 2.0;
    int direction = -1; // -1 for Clockwise

    for (IExpr opt : options) {
      if (opt.isRuleAST()) {
        IExpr key = ((IAST) opt).arg1();
        IExpr val = ((IAST) opt).arg2();
        if (key.equals(S.ChartStyle)) {
          chartStyle = val;
        } else if (key.equals(S.ChartLabels)) {
          chartLabels = val;
        } else if (key.equals(S.ChartLegends)) {
          chartLegends = val;
        } else if (key.toString().equals("SectorOrigin")) {
          // Basic parsing for SectorOrigin -> {val, "Clockwise"/"CounterClockwise"}
          if (val.isList() && val.argSize() > 0) {
            try {
              startAngle = ((IAST) val).arg1().evalf();
              // Convert degrees to radians if mostly numbers are used?
              // Mma usually assumes Radians in math, but Degrees in SectorOrigin options often.
              // For safety, let's assume the user provided Radians or Degrees wrapped.
              // If simple number, assume radians to match Disk? Or Degrees to match SectorOrigin
              // default?
              // "SectorOrigin -> {90 Degree, ...}" -> 1.57
            } catch (ArgumentTypeException e) {
            }
          }
        }
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

        // Color
        IExpr color;
        if (style != null) {
          color = style;
        } else {
          color = getChartStyle(chartStyle, index);
        }

        // Group for sector
        IASTAppendable group = F.ListAlloc();
        if (!color.equals(S.Automatic))
          group.append(color);

        // Disk Primitive
        group.append(F.function(S.Disk, F.List(F.C0, F.C0), F.C1, F.List(F.num(a1), F.num(a2))));
        primitives.append(group);

        // Label
        if (label != null) {
          // Position label at mid-angle, radius 0.7
          double midAngle = (a1 + a2) / 2.0;
          // If angles wrapped around 2Pi? simple average works for small wedges < Pi.
          // For PieChart logic here, simple average is fine.

          double rLbl = 0.7; // Internal label
          // For external: rLbl = 1.1;

          double lx = rLbl * Math.cos(midAngle);
          double ly = rLbl * Math.sin(midAngle);

          // Text Primitive
          // Text[lbl, {lx, ly}, {0,0}] (Centered)
          primitives.append(
              F.List(S.Black, F.Text(label, F.List(F.num(lx), F.num(ly)), F.List(F.C0, F.C0))));
        }

        currentAngle = endAngle;
        index++;
      }
    }

    // Bounds for PieChart are fixed -1..1 usually
    graphicsOptions.setBoundingBox(new double[] {-1.2, 1.2, -1.2, 1.2});

    // Ensure Aspect Ratio 1
    if (graphicsOptions.aspectRatio().equals(S.Automatic)) {
      graphicsOptions.setAspectRatio(F.C1);
    }

    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  private IExpr getChartStyle(IExpr styleOption, int index) {
    if (styleOption.isAutomatic()) {
      return GraphicsOptions.plotStyleColorExpr(index, F.NIL);
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
      return expr.evalf();
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

    // PieChart Defaults
    defaults[GraphicsOptions.X_AXES] = S.False;
    defaults[GraphicsOptions.X_FRAME] = S.False;
    defaults[GraphicsOptions.X_ASPECTRATIO] = F.C1;

    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(), defaults);
  }
}
