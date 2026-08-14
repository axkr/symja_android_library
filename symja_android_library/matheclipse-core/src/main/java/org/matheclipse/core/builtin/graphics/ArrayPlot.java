package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
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
 * Functions for generating Array Plots.
 * <p>
 * Example: <code>ArrayPlot[RandomReal[1, {10, 10}]]</code> <code>ArrayPlot[{{1, 0}, {0, 1}}]</code>
 * <code>ArrayPlot[{{Red, Blue}, {Green, Yellow}}]</code>
 */
public class ArrayPlot extends ListPlot {

  public ArrayPlot() {}

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
    // MaxPlotPoints paints a large array from a sample of it rather than from every cell
    IAST thinned = GraphicsOptions.downsampleMatrix((IAST) dataArg,
        GraphicsOptions.optionValue(originalAST, S.MaxPlotPoints, S.Automatic).toIntDefault(-1));
    if (thinned.isPresent()) {
      dataArg = thinned;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    boolean colorFunctionScaling = true;
    IExpr colorFunctionOpt = S.Automatic;
    IExpr colorRulesOpt = S.None;
    IExpr meshOpt = S.None;
    // the options array holds resolved values, not the rules the caller wrote,
    // so the option rules are read back off the original call
    for (IExpr opt : originalAST) {
      if (opt.isRuleAST()) {
        IExpr key = ((IAST) opt).arg1();
        IExpr val = ((IAST) opt).arg2();
        if (key.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) key).ordinal()) {
            case ID.ColorFunctionScaling:
              if (val.isFalse()) {
                colorFunctionScaling = false;
              }
              break;
            case ID.ColorFunction:
              colorFunctionOpt = val;
              break;
            case ID.ColorRules:
              colorRulesOpt = val;
              break;
            case ID.Mesh:
              meshOpt = val;
              break;
          }
        }
      }
    }

    IAST list = (IAST) dataArg;
    int rows = list.argSize();
    if (rows == 0)
      return F.NIL;

    // Determine dimensions
    int cols = 0;
    boolean hasSubLists = false;
    for (IExpr row : list) {
      if (row.isList()) {
        hasSubLists = true;
        cols = Math.max(cols, ((IAST) row).argSize());
      }
    }

    // Handle 1D list as 1xN array
    if (!hasSubLists) {
      cols = rows;
      rows = 1;
      // Wrap for consistent processing
      list = F.List(list);
    }

    if (cols == 0)
      return F.NIL;

    // First pass: Determine range for numeric scaling
    double min = Double.MAX_VALUE;
    double max = -Double.MAX_VALUE;

    // Store data in a mixed array since it might be Numbers or Colors
    IExpr[][] grid = new IExpr[rows][cols];

    for (int r = 0; r < rows; r++) {
      IExpr rowExpr = list.get(r + 1);
      if (rowExpr.isList()) {
        IAST rowAst = (IAST) rowExpr;
        for (int c = 0; c < Math.min(cols, rowAst.size()); c++) {
          IExpr val = rowAst.get(c + 1);
          grid[r][c] = val;

          // Check for numeric
          try {
            double d = val.evalDouble();
            if (Double.isFinite(d)) {
              if (d < min)
                min = d;
              if (d > max)
                max = d;
            }
          } catch (Exception e) {
            // Not a number, likely a color or missing
          }
        }
      }
    }

    IASTAppendable primitives = F.ListAlloc();
    // one raster rather than one rectangle per cell
    java.util.function.DoubleFunction<IExpr> colorMap =
        GraphicsOptions.colorFunction(colorFunctionOpt, engine, t -> F.GrayLevel(1.0 - t));
    IExpr[][] cells = new IExpr[rows][cols];

    // Draw cells
    // Coordinate System:
    // Matrix index (r, c) (0-based)
    // r=0 is TOP row. r=rows-1 is BOTTOM row.
    // x range: c to c+1
    // y range: rows-1-r to rows-r

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        IExpr val = grid[r][c];
        if (val == null || val.equals(S.None) || val.isAST(S.Missing))
          continue;

        IExpr color = F.NIL;

        // 1. Check if explicit color
        if (isColor(val)) {
          color = val;
        } else {
          // 2. Numeric Mapping
          try {
            double d = val.evalDouble();
            if (Double.isFinite(d)) {
              double t = 0.0;
              if (colorFunctionScaling && max > min) {
                t = (d - min) / (max - min);
              } else {
                // If not scaling, assume 0..1 range or clip
                t = d;
              }
              if (t < 0)
                t = 0;
              if (t > 1)
                t = 1;

              // ArrayPlot Default: Grayscale, Min->White(1), Max->Black(0)??
              // Mma: 0 is White, 1 is Black.
              // Wait, standard grayscale: 0 is Black, 1 is White.
              // Mma ArrayPlot[{0,1}] -> 0 is White, 1 is Black.
              // So we invert: GrayLevel[1 - t]
              // ArrayPlot's own scale runs white at 0 to black at 1
              color = colorMap.apply(t);
            }
          } catch (Exception e) {
          }
        }

        // an explicit rule for this value takes precedence over the colour scale
        IExpr ruleColor = GraphicsOptions.colorRule(colorRulesOpt, list.getAt(r + 1).getAt(c + 1));
        if (ruleColor != null) {
          color = ruleColor;
        }
        if (color.isPresent()) {
          // row 0 of the array is drawn at the top, which is the order rasterTopFirst expects
          cells[r][c] = color;
        }
      }
    }
    primitives.append(GraphicsOptions.rasterTopFirst(cells, 0, 0, cols, rows));
    IExpr meshLines = GraphicsOptions.meshGrid(meshOpt, 0, 0, cols, rows, cols, rows);
    if (meshLines.isPresent()) {
      primitives.append(meshLines);
    }

    graphicsOptions.setBoundingBox(new double[] {0, cols, 0, rows});

    if (graphicsOptions.aspectRatio().isAutomatic()) {
      // Square cells
      graphicsOptions.setAspectRatio(F.num((double) rows / (double) cols));
    }

    // Default FrameTicks -> None for ArrayPlot
    if (graphicsOptions.frameTicks().isNone() && options[GraphicsOptions.X_FRAMETICKS].isNone()) {
      // Ensure options reflect this so SVG doesn't draw default ticks
      graphicsOptions.setFrameTicks(S.None);
    }

    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  private boolean isColor(IExpr e) {
    if (e.isAST()) {
      IExpr head = e.head();
      return head.equals(S.RGBColor) || head.equals(S.Hue) || head.equals(S.GrayLevel)
          || head.equals(S.CMYKColor);
    }
    return e.isSymbol() && (e.equals(S.Red) || e.equals(S.Green) || e.equals(S.Blue)
        || e.equals(S.Black) || e.equals(S.White) || e.equals(S.Gray) || e.equals(S.Yellow)
        || e.equals(S.Cyan) || e.equals(S.Magenta) || e.equals(S.Orange) || e.equals(S.Pink)
        || e.equals(S.Purple) || e.equals(S.Brown));
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

    // ArrayPlot Defaults
    defaults[GraphicsOptions.X_FRAME] = S.True;
    defaults[GraphicsOptions.X_AXES] = S.False;
    defaults[GraphicsOptions.X_ASPECTRATIO] = S.Automatic;
    defaults[GraphicsOptions.X_FRAMETICKS] = S.None;

    GraphicsOptions.OptionSet optionSet = GraphicsOptions.rasterExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
