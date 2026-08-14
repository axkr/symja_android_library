package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * {@code ListPointPlot3D[{{x, y, z}, ...}]} - a scatter of points in space, also accepting a
 * rectangular array of heights.
 */
public class ListPointPlot3D extends AbstractFunctionOptionEvaluator {

  public ListPointPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 1 || !ast.arg1().isList()) {
      return F.NIL;
    }
    IAST listData = (IAST) ast.arg1();
    if (listData.isEmpty()) {
      return F.Graphics3D(F.CEmptyList);
    }

    boolean isMultiDataset = false;
    boolean isHeightMap = false;
    IExpr first = listData.arg1();
    if (first.isList()) {
      IAST firstList = (IAST) first;
      if (firstList.isList3() && !firstList.arg1().isList()) {
        // {{x,y,z}, ...} - one set of explicit coordinates
        isMultiDataset = false;
      } else if (!firstList.isEmpty() && firstList.arg1().isList()) {
        isMultiDataset = true;
      } else if (!firstList.isEmpty() && firstList.arg1().isNumber()) {
        isHeightMap = true;
      } else {
        isMultiDataset = true;
      }
    }

    IExpr plotStyle = options[Plot3DTools.X_PLOT_STYLE];
    IExpr dataRange = options[Plot3DTools.X_DATA_RANGE];
    IExpr filling = options[Plot3DTools.X_FILLING];
    IExpr fillingStyle = options[Plot3DTools.X_FILLING_STYLE];
    IAST datasets = isMultiDataset ? listData : F.List(listData);

    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(false, false);
    boolean any = false;
    for (int i = 1; i < datasets.size(); i++) {
      if (!datasets.get(i).isList()) {
        continue;
      }
      IAST dataset = (IAST) datasets.get(i);
      IASTAppendable indices = F.ListAlloc(dataset.argSize());
      List<double[]> coordinates = new ArrayList<>();
      if (isHeightMap) {
        addHeightMap(builder, dataset, dataRange, indices, coordinates);
      } else {
        addCoordinates(builder, dataset, indices, coordinates);
      }
      if (indices.argSize() > 0) {
        IExpr style = Plot3DTools.curveStyle(i - 1, plotStyle);
        builder.addPrimitive(style);
        builder.addPrimitive(F.Point(indices));
        addFilling(builder, coordinates, filling, fillingStyle, style);
        any = true;
      }
    }
    if (!any) {
      return F.NIL;
    }

    return Plot3DTools.graphics3D(builder.build(), originalAST, argSize,
        new IExpr[] {F.Rule(S.PlotRange, options[Plot3DTools.X_PLOT_RANGE]),
            F.Rule(S.BoxRatios, Plot3DTools.FLAT_BOX_RATIOS), F.Rule(S.Axes, S.True)});
  }

  /**
   * A rectangular array of heights, laid out over the {@code DataRange} rectangle.
   *
   * <p>
   * The column index runs along x and the row index along y. The extent of each has to come from
   * the count in that direction: reading both from the row count put the x extent of any array that
   * was not square in the wrong place.
   */
  private static void addHeightMap(GraphicsComplexBuilder builder, IAST rows, IExpr dataRange,
      IASTAppendable indices, List<double[]> coordinates) {
    int rowCount = rows.argSize();
    int colCount = 0;
    for (int r = 1; r <= rowCount; r++) {
      if (rows.get(r).isList()) {
        colCount = Math.max(colCount, ((IAST) rows.get(r)).argSize());
      }
    }
    if (rowCount == 0 || colCount == 0) {
      return;
    }
    double[] x = {1.0, colCount};
    double[] y = {1.0, rowCount};
    if (dataRange != null && dataRange.isList() && ((IAST) dataRange).argSize() == 2) {
      IAST ranges = (IAST) dataRange;
      double[] parsedX = pair(ranges.arg1());
      double[] parsedY = pair(ranges.arg2());
      if (parsedX != null && parsedY != null) {
        x = parsedX;
        y = parsedY;
      }
    }

    for (int r = 1; r <= rowCount; r++) {
      if (!rows.get(r).isList()) {
        continue;
      }
      IAST row = (IAST) rows.get(r);
      for (int c = 1; c <= row.argSize(); c++) {
        double z = row.get(c).evalfNaN();
        if (!Double.isFinite(z)) {
          continue;
        }
        double px = colCount > 1 ? x[0] + (c - 1) * (x[1] - x[0]) / (colCount - 1) : x[0];
        double py = rowCount > 1 ? y[0] + (r - 1) * (y[1] - y[0]) / (rowCount - 1) : y[0];
        indices.append(F.ZZ(builder.addVertex(px, py, z, null, null)));
        coordinates.add(new double[] {px, py, z});
      }
    }
  }

  private static void addCoordinates(GraphicsComplexBuilder builder, IAST dataset,
      IASTAppendable indices, List<double[]> coordinates) {
    for (int k = 1; k < dataset.size(); k++) {
      IExpr point = dataset.get(k);
      if (!point.isList3()) {
        continue;
      }
      double x = point.first().evalfNaN();
      double y = point.second().evalfNaN();
      double z = point.last().evalfNaN();
      // a point that cannot be evaluated is left out; it used to abandon the whole plot
      if (Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)) {
        indices.append(F.ZZ(builder.addVertex(x, y, z, null, null)));
        coordinates.add(new double[] {x, y, z});
      }
    }
  }

  /**
   * Drops a stem from every point to the level {@code Filling} names.
   *
   * <p>
   * The stems are added as fresh vertices rather than by reusing the point indices, because the
   * base of a stem is a position the data never contained. {@code FillingStyle -> Automatic} leaves
   * them in the colour of the points they belong to, which is what keeps two overlapping datasets
   * apart.
   */
  private static void addFilling(GraphicsComplexBuilder builder, List<double[]> coordinates,
      IExpr filling, IExpr fillingStyle, IExpr pointStyle) {
    if (coordinates.isEmpty() || filling.isNone() || filling.equals(S.Automatic)) {
      return;
    }
    double base;
    if (filling.equals(S.Bottom) || filling.equals(S.Axis)) {
      base = filling.equals(S.Axis) ? 0.0 : Double.MAX_VALUE;
      if (filling.equals(S.Bottom)) {
        for (double[] xyz : coordinates) {
          base = Math.min(base, xyz[2]);
        }
      }
    } else if (filling.equals(S.Top)) {
      base = -Double.MAX_VALUE;
      for (double[] xyz : coordinates) {
        base = Math.max(base, xyz[2]);
      }
    } else {
      base = filling.evalfNaN();
      if (!Double.isFinite(base)) {
        return;
      }
    }

    IASTAppendable stems = F.ListAlloc(coordinates.size());
    for (double[] xyz : coordinates) {
      int from = builder.addVertex(xyz[0], xyz[1], base, null, null);
      int to = builder.addVertex(xyz[0], xyz[1], xyz[2], null, null);
      stems.append(F.List(F.ZZ(from), F.ZZ(to)));
    }
    builder.addPrimitive(fillingStyle.equals(S.Automatic) ? pointStyle : fillingStyle);
    builder.addPrimitive(F.unaryAST1(S.Line, stems));
    // the points are drawn after the stems again, so they keep their own colour
    builder.addPrimitive(pointStyle);
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
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet options = Plot3DTools.listPlot();
    setOptions(newSymbol, options.keys(), options.values());
  }
}
