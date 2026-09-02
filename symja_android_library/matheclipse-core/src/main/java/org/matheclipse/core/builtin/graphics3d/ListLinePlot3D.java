package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** Plot a list of Lines in 3 dimensions */
public class ListLinePlot3D extends AbstractFunctionOptionEvaluator {
  public ListLinePlot3D() {}

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    if (ast.argSize() > 0) {

      // Plot3DTools.curveStyle is what the rest of this package styles a line with: it cycles
      // through an explicit PlotStyle and falls back to the palette, counting from zero the way
      // the loops below do
      IExpr plotStyle = options[Plot3DTools.X_PLOT_STYLE];
      IExpr dataRange = options[Plot3DTools.X_DATA_RANGE];
      RegionFunctionFilter region =
          RegionFunctionFilter.of(options[Plot3DTools.X_REGION_FUNCTION], engine);

      // case 1: single line heights
      // e.g.: ListLinePlot3D[{1, 2, 3, 4, 5}]
      if (ast.arg1().isASTSizeGE(S.List, 2)) {
        // only a numeric first height identifies this case; otherwise fall through
        if (!Double.isNaN(((IAST) ast.arg1()).arg1().evalfNaN())) {
          IExpr heightLinePlot =
              heightLinePlot(F.list(ast.arg1()), plotStyle, dataRange, engine, region);
          if (heightLinePlot.isPresent()) {
            return Plot3DTools.graphics3D(heightLinePlot, ast, 1,
                new IExpr[] {F.Rule(S.Axes, S.True),
                    F.Rule(S.PlotRange, options[Plot3DTools.X_PLOT_RANGE]),
                    F.Rule(S.BoxRatios, Plot3DTools.FLAT_BOX_RATIOS)});
          }
          return F.NIL;
        }
      }

      // try if arg1 is a matrix
      int[] dimension = ast.arg1().isMatrix(false);

      // case 2: single line coordinates
      // e.g.: ListLinePlot3D[{{x_1, y_1, z_1}, {x_2, y_2, z_2}}]
      if (dimension != null && dimension.length == 2 && dimension[1] == 3) {
        return Plot3DTools.graphics3D(
            coordinateLinePlot(F.list(ast.arg1()), plotStyle, engine, region), ast, 1,
            new IExpr[] {F.Rule(S.Axes, S.True),
                F.Rule(S.PlotRange, options[Plot3DTools.X_PLOT_RANGE]),
                F.Rule(S.BoxRatios, Plot3DTools.FLAT_BOX_RATIOS)});
      }

      // case 3: multiple line heights
      // e.g.: ListLinePlot3D[{{1, 2, 3, 4}, {-1, -2, -3, -4}}]
      if (ast.arg1().isASTSizeGE(S.List, 2) && ((IAST) ast.arg1()).arg1().isASTSizeGE(S.List, 2)) {
        // only a numeric first height identifies this case; otherwise fall through
        if (!Double.isNaN(((IAST) ((IAST) ast.arg1()).arg1()).arg1().evalfNaN())) {
          IExpr heightLinePlot =
              heightLinePlot((IAST) ast.arg1(), plotStyle, dataRange, engine, region);
          if (heightLinePlot.isPresent()) {
            return Plot3DTools.graphics3D(heightLinePlot, ast, 1,
                new IExpr[] {F.Rule(S.Axes, S.True),
                    F.Rule(S.PlotRange, options[Plot3DTools.X_PLOT_RANGE]),
                    F.Rule(S.BoxRatios, Plot3DTools.FLAT_BOX_RATIOS)});
          }
        }
      }

      if (ast.arg1().isASTSizeGE(S.List, 2)) {
        dimension = ((IAST) ast.arg1()).arg1().isMatrix(false);
        // case 4: multiple line coordinates
        // e.g.: ListLinePlot3D[{{coord1, coord2}, {coord3, coord4}}]
        if (dimension != null && dimension.length == 2 && dimension[1] == 3) {
          return Plot3DTools.graphics3D(
              coordinateLinePlot((IAST) ast.arg1(), plotStyle, engine, region), ast, 1,
              new IExpr[] {F.Rule(S.Axes, S.True),
                  F.Rule(S.PlotRange, options[Plot3DTools.X_PLOT_RANGE]),
                  F.Rule(S.BoxRatios, Plot3DTools.FLAT_BOX_RATIOS)});
        }
      }
    }

    // `1` is not a valid dataset or a list of datasets.
    return Errors.printMessage(ast.topHead(), "ldata", F.list(ast.arg1()), engine);
  }

  /**
   * One line per row of heights, laid out over the {@code DataRange} rectangle.
   *
   * <p>
   * The heights are the z coordinates as they were given. They used to be squeezed into a box of
   * 2.5 by 2.5 by 1 whatever the data covered, which meant the axes were labelled with numbers the
   * data never contained, two plots of the same quantity could not be compared, and a row of equal
   * heights divided by a zero range and gave up with a division error. The shape of the picture is
   * the business of {@code BoxRatios}, which is how the other plots in this package do it.
   */
  private IExpr heightLinePlot(IAST heights, IExpr plotStyle, IExpr dataRange, EvalEngine engine,
      RegionFunctionFilter region) {
    final int valuesSize = heights.size();
    IASTAppendable resultList = F.NIL;

    final int rowCount = heights.argSize();
    int columnCount = 0;
    for (int i = 1; i <= rowCount; i++) {
      if (heights.get(i).isAST()) {
        columnCount = Math.max(columnCount, ((IAST) heights.get(i)).argSize());
      }
    }
    double[] xRange = {1.0, rowCount};
    double[] yRange = {1.0, columnCount};
    if (dataRange != null && dataRange.isList() && ((IAST) dataRange).argSize() == 2) {
      double[] parsedX = pair(((IAST) dataRange).arg1());
      double[] parsedY = pair(((IAST) dataRange).arg2());
      if (parsedX != null && parsedY != null) {
        xRange = parsedX;
        yRange = parsedY;
      }
    }
    int lineColorNumber = 0;

    for (int i = 1; i < valuesSize; i++) {
      if (heights.get(i).isAST()) {
        IAST rowList = (IAST) heights.get(i);
        final int rowListSize = rowList.size();

        final int rowLength = rowList.argSize();
        // one row may fall apart into several lines where the region cuts it, rather than being
        // drawn straight across the gap
        List<IASTAppendable> segments = new ArrayList<IASTAppendable>();
        IASTAppendable lineList = F.ListAlloc(rowListSize);

        for (int j = 1; j < rowListSize; j++) {
          double value = rowList.get(j).evalfNaN();
          if (Double.isNaN(value)) {
            return F.NIL;
          }
          // the row index runs along x and the column index along y, each spread over its own
          // side of the DataRange rectangle
          double x = rowCount > 1 ? xRange[0] + (i - 1) * (xRange[1] - xRange[0]) / (rowCount - 1)
              : xRange[0];
          double y = rowLength > 1 ? yRange[0] + (j - 1) * (yRange[1] - yRange[0]) / (rowLength - 1)
              : yRange[0];
          if (region != null && !region.accepts(x, y, value)) {
            if (lineList.argSize() > 1) {
              segments.add(lineList);
            }
            lineList = F.ListAlloc(rowListSize);
            continue;
          }
          lineList.append(F.List(F.num(x), F.num(y), F.num(value)));
        }
        if (lineList.argSize() > 1 || (region == null && lineList.argSize() > 0)) {
          segments.add(lineList);
        }
        if (segments.isEmpty()) {
          continue;
        }

        final IExpr color = Plot3DTools.curveStyle(lineColorNumber++, plotStyle);
        if (resultList.isNIL()) {
          resultList = F.ListAlloc(valuesSize);
        }
        resultList.append(color);
        for (IASTAppendable segment : segments) {
          resultList.append(F.Line(segment));
        }
      }
    }
    if (resultList.isNIL() && region != null) {
      // a region that keeps nothing draws nothing; the data was valid, so this is an empty
      // picture rather than the "not a valid dataset" the caller would otherwise be told about
      return F.CEmptyList;
    }
    return resultList;
  }

  /**
   * One line per list of coordinates, drawn where the coordinates say.
   *
   * <p>
   * As with the heights above, the points used to be divided down into a fixed 2.5 by 2.5 by 1 box;
   * they are now left alone, so the axes carry the numbers that were plotted.
   */
  private IExpr coordinateLinePlot(IAST coordinates, IExpr plotStyle, EvalEngine engine,
      RegionFunctionFilter region) {
    IASTAppendable lineList = F.ListAlloc(coordinates.size() * 2);
    int lineColorNumber = 0;
    for (int i = 1; i <= coordinates.argSize(); i++) {
      if (!coordinates.get(i).isList()) {
        continue;
      }
      IAST line = (IAST) coordinates.get(i);
      List<IASTAppendable> segments = new ArrayList<IASTAppendable>();
      IASTAppendable points = F.ListAlloc(line.argSize());
      for (int j = 1; j <= line.argSize(); j++) {
        IExpr coordinate = line.get(j);
        if (!coordinate.isList3()) {
          continue;
        }
        double x = coordinate.first().evalfNaN();
        double y = coordinate.second().evalfNaN();
        double z = coordinate.last().evalfNaN();
        // a point that cannot be evaluated is left out rather than abandoning the whole line
        if (Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)) {
          if (region != null && !region.accepts(x, y, z)) {
            // the line is broken where the region ends rather than drawn across the gap
            if (points.argSize() > 1) {
              segments.add(points);
            }
            points = F.ListAlloc(line.argSize());
            continue;
          }
          points.append(F.List(F.num(x), F.num(y), F.num(z)));
        }
      }
      if (points.argSize() > 1 || (region == null && points.argSize() > 0)) {
        segments.add(points);
      }
      if (!segments.isEmpty()) {
        lineList.append(Plot3DTools.curveStyle(lineColorNumber++, plotStyle));
        for (IASTAppendable segment : segments) {
          lineList.append(F.Line(segment));
        }
      }
    }
    return lineList;
  }

  private static double[] pair(IExpr expr) {
    if (!expr.isList() || ((IAST) expr).argSize() < 2) {
      return null;
    }
    double lo = ((IAST) expr).arg1().evalfNaN();
    double hi = ((IAST) expr).arg2().evalfNaN();
    return Double.isFinite(lo) && Double.isFinite(hi) ? new double[] {lo, hi} : null;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet options = Plot3DTools.listPlot();
    setOptions(newSymbol, options.keys(), options.values());
  }
}
