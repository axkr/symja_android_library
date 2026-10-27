package org.matheclipse.core.builtin.graphics;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions for generating Array Plots.
 * <p>
 * Example: <code>ArrayPlot[RandomReal[1, {10, 10}]]</code> <code>ArrayPlot[{{1, 0}, {0, 1}}]</code>
 * <code>ArrayPlot[{{Red, Blue}, {Green, Yellow}}]</code>
 */
public class ArrayPlot extends ListPlot {

  /**
   * The data is drawn as one field rather than point by point, so no wrapper is read here; the
   * label goes over the whole picture instead.
   */
  @Override
  protected boolean readsArgumentWrapper() {
    return false;
  }

  // CPD-OFF matrix plots share the preamble, the cell colouring differs
  public ArrayPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    // the shape tests below read the data through any display wrapper; `wrappedAST` keeps the
    // wrapper, so the label can still be put over the finished picture
    final IAST wrappedAST = ast;
    ast = PlotWrapper.stripFirstArgument(ast);
    if (argSize < 1) {
      return F.NIL;
    }

    IExpr dataArg = GraphicsOptions.matrixPlotData(ast.arg1(), originalAST, engine);
    if (dataArg.isNIL()) {
      return F.NIL;
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    GraphicsOptions.MatrixPlotOptions matrixOptions =
        GraphicsOptions.readMatrixPlotOptions(originalAST);

    IAST list = (IAST) dataArg;
    int rows = list.argSize();
    if (rows == 0)
      return F.NIL;

    // Determine dimensions
    int cols = 0;
    // CPD-ON
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
        // argSize(), not size(): the loop indexes get(c + 1), so a row shorter than the widest
        // one would otherwise read one position past its last argument. The cells a ragged row
        // leaves unset stay null and are skipped when drawing - transparent, rather than the
        // default value of the array.
        for (int c = 0; c < Math.min(cols, rowAst.argSize()); c++) {
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
    // the value the grey scale reaches black at: the extreme furthest from zero on whichever side
    // the data lies, which is the maximum unless every value is negative
    final double greyLimit = max > 0.0 ? max : min;
    final boolean scaling = matrixOptions.colorFunctionScaling;
    // A ColorFunction is a scale over the range of the data. The default grey scale is a
    // different one - see greyColor - so the two are separate objects rather than one function
    // reached through two sets of arithmetic.
    PlotColorFunction colorMap = PlotColorFunction
        .of(PlotColorFunction.Family.ARRAY, matrixOptions.colorFunction, F.bool(scaling),
            S.ArrayPlot, engine)
        .range(1, min, max).sink(PlotColorFunction.Sink.FLAT)
        .fallback(t -> greyColor(t, greyLimit, scaling)).build();
    // compiled once for the whole array: the rules are matched like Replace, once per cell
    GraphicsOptions.ColorRuleTable colorRules =
        GraphicsOptions.colorRules(matrixOptions.colorRules, engine);
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
        if (val == null || val == S.None || val.isAST(S.Missing))
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
              color = colorMap == null ? greyColor(d, greyLimit, scaling) : colorMap.color(d);
            }
          } catch (Exception e) {
          }
        }

        // an explicit rule for this value takes precedence over the colour scale
        IExpr ruleColor = colorRules == null ? null : colorRules.color(val);
        if (ruleColor != null) {
          color = ruleColor;
        }
        if (color.isPresent()) {
          // row 0 of the array is drawn at the top, which is the order rasterTopFirst expects
          cells[r][c] = color;
        }
      }
    }
    IExpr frameTicksOpt = GraphicsOptions.finishMatrixRaster(primitives, cells, rows, cols,
        matrixOptions.mesh, graphicsOptions, originalAST, S.None);
    if (!(frameTicksOpt.isAutomatic() || frameTicksOpt.isTrue())
        && graphicsOptions.frameTicks().isNone()
        && options[GraphicsOptions.X_FRAMETICKS].isNone()) {
      // Default FrameTicks -> None for ArrayPlot
      // Ensure options reflect this so SVG doesn't draw default ticks
      graphicsOptions.setFrameTicks(S.None);
    }

    // the raster alone is the content itself, so that First(ArrayPlot(...)) is the Raster
    return createGraphicsFunction(
        primitives.argSize() == 1 && primitives.arg1().isAST() ? (IAST) primitives.arg1()
            : primitives,
        graphicsOptions, wrappedAST);
  }

  /**
   * The scale {@code ArrayPlot} paints with when no {@code ColorFunction} was given.
   *
   * <p>
   * It runs from white at zero to black at the largest value, rather than from white at the
   * smallest: {@code ArrayPlot({{1, 2, 3}})} is three greys with no white among them, and
   * {@code ArrayPlot({{-1, 0, 1}})} paints -1 and 0 alike because everything below zero is off the
   * white end of the scale. That is the reference behaviour, and it belongs to this scale alone - a
   * {@code ColorFunction} is given the usual scale over the whole range of the data.
   *
   * <p>
   * The clamp lives here too. An unscaled value is a position on this scale and positions outside
   * it are the ends of it, which is why {@code ColorFunctionScaling -> False} still paints 3 black.
   * A caller's own function gets its value as it stands.
   *
   * @param value the cell, raw when scaling is off and already a position on the scale when it is
   */
  private static IExpr greyColor(double value, double greyLimit, boolean scaling) {
    double t = value;
    if (scaling) {
      t = greyLimit == 0.0 ? 0.0 : value / greyLimit;
    }
    t = t < 0.0 ? 0.0 : t > 1.0 ? 1.0 : t;
    return F.GrayLevel(1.0 - t);
  }

  private boolean isColor(IExpr e) {
    if (e.isAST()) {
      IExpr head = e.head();
      return head == S.RGBColor || head == S.Hue || head == S.GrayLevel || head == S.CMYKColor;
    }
    return e.isSymbol() && (e == S.Red || e == S.Green || e == S.Blue || e == S.Black
        || e == S.White || e == S.Gray || e == S.Yellow || e == S.Cyan || e == S.Magenta
        || e == S.Orange || e == S.Pink || e == S.Purple || e == S.Brown);
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
