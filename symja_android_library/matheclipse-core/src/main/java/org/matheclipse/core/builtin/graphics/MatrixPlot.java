package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions for generating Matrix Plots.
 * <p>
 * Example: <code>MatrixPlot[RandomReal[1, {10, 10}]]</code>
 */
public class MatrixPlot extends ListPlot {

  public MatrixPlot() {}

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

    boolean colorFunctionScaling = true;
    for (IExpr opt : options) {
      if (opt.isRuleAST()) {
        IExpr key = ((IAST) opt).arg1();
        IExpr val = ((IAST) opt).arg2();
        if (key.isBuiltInSymbol() && ((IBuiltInSymbol) key).ordinal() == ID.ColorFunctionScaling) {
          if (val.isFalse())
            colorFunctionScaling = false;
        }
      }
    }

    IAST list = (IAST) dataArg;
    int rows = list.argSize();
    if (rows == 0)
      return F.NIL;

    int cols = 0;
    for (IExpr row : list) {
      if (row.isList()) {
        cols = Math.max(cols, ((IAST) row).argSize());
      }
    }
    if (cols == 0)
      return F.NIL;

    double min = Double.MAX_VALUE;
    double max = -Double.MAX_VALUE;

    double[][] data = new double[rows][cols];
    for (int r = 0; r < rows; r++) {
      IExpr rowExpr = list.get(r + 1);
      if (rowExpr.isList()) {
        IAST rowAst = (IAST) rowExpr;
        for (int c = 0; c < Math.min(cols, rowAst.size()); c++) {
          try {
            double val = rowAst.get(c + 1).evalf();
            data[r][c] = val;
            if (Double.isFinite(val)) {
              if (val < min)
                min = val;
              if (val > max)
                max = val;
            }
          } catch (Exception e) {
            data[r][c] = Double.NaN;
          }
        }
        for (int c = rowAst.size(); c < cols; c++)
          data[r][c] = Double.NaN;
      } else {
        for (int c = 0; c < cols; c++)
          data[r][c] = Double.NaN;
      }
    }

    IASTAppendable primitives = F.ListAlloc();
    primitives.append(F.EdgeForm(S.None));

    // Draw cells
    // Row 0 at top (y = rows-1 to rows)
    // Row rows-1 at bottom (y = 0 to 1)

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        double val = data[r][c];
        if (Double.isNaN(val))
          continue;

        double t = 0.5;
        if (colorFunctionScaling) {
          if (max > min)
            t = (val - min) / (max - min);
        } else {
          t = val;
        }

        // Use the matrix-specific color map
        IExpr color = GraphicsOptions.getMatrixColor(t);
        primitives.append(color);

        double x0 = c;
        // Overlap by 0.02
        double x1 = c + 1.02;
        // Invert Y: row 0 is top
        double y0 = rows - 1.0 - r;
        double y1 = rows - r + 0.02;

        primitives.append(F.Rectangle(F.List(F.num(x0), F.num(y0)), F.List(F.num(x1), F.num(y1))));
      }
    }

    graphicsOptions.setBoundingBox(new double[] {0, cols, 0, rows});

    if (graphicsOptions.aspectRatio().equals(S.Automatic)) {
      graphicsOptions.setAspectRatio(F.num((double) rows / (double) cols));
    }

    // Generate FrameTicks to match MatrixPlot style
    // Top: Columns 1..Cols
    // Left: Rows 1..Rows (Inverted labels)

    // Top Ticks
    IASTAppendable topTicks = F.ListAlloc();
    List<Double> cTicks = getNiceTicks(0, cols, 10);
    for (double v : cTicks) {
      if (v > 0 && v <= cols) {
        topTicks.append(F.List(F.num(v), F.num(v))); // {val, label}
      }
    }

    // Left Ticks (Inverted: Row 1 is at top y=rows-0.5)
    IASTAppendable leftTicks = F.ListAlloc();
    List<Double> rTicks = getNiceTicks(0, rows, 10);
    for (double v : rTicks) {
      if (v > 0 && v <= rows) {
        // Map index v to Y coordinate: rows - v + 0.5 (center of cell)
        leftTicks.append(F.List(F.num(rows - v + 0.5), F.num(v)));
      }
    }

    // FrameTicks -> {{Left, Right}, {Bottom, Top}}
    IExpr frameTicks = F.List(F.List(leftTicks, S.None), F.List(S.None, topTicks));
    graphicsOptions.setFrameTicks(frameTicks);

    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  private List<Double> getNiceTicks(double min, double max, int maxTicks) {
    List<Double> ticks = new ArrayList<>();
    double range = max - min;
    if (range <= 0)
      return ticks;
    double step = Math.pow(10, Math.floor(Math.log10(range / maxTicks)));
    if (range / step < maxTicks / 2)
      step /= 2;
    if (range / step < maxTicks / 2)
      step /= 2;
    else if (range / step > maxTicks * 2)
      step *= 2;

    double start = Math.ceil(min / step) * step;
    for (double t = start; t <= max; t += step) {
      ticks.add(t);
    }
    return ticks;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, false);

    defaults[GraphicsOptions.X_FRAME] = S.True;
    defaults[GraphicsOptions.X_AXES] = S.False;
    defaults[GraphicsOptions.X_ASPECTRATIO] = S.Automatic;

    setOptions(newSymbol, GraphicsOptions.listPlotDefaultOptionKeys(), defaults);
  }
}
