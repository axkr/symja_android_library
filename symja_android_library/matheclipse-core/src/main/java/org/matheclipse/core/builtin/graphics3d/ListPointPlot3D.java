package org.matheclipse.core.builtin.graphics3d;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.builtin.QuantityFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.PlotColorFunction;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.RegionFunctionFilter;
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
    // a display wrapper comes off before the argument's shape is read, so a labelled dataset is
    // still recognised as a dataset; Plot3DTools.graphics3D puts the label back on the finished
    // primitives, reading it from the original call
    if (ast.size() > 1) {
      IExpr unwrapped = PlotWrapper.strip(ast.arg1());
      if (unwrapped != ast.arg1()) {
        ast = ast.setAtCopy(1, unwrapped);
      }
    }
    if (argSize < 1 || !ast.arg1().isList()) {
      return F.NIL;
    }
    IExpr plotData = QuantityFunctions.quantityPlotMagnitudes(ast.arg1(),
        GraphicsOptions.optionValue(originalAST, S.TargetUnits, S.Automatic), engine);
    IAST listData = plotData.isList() ? (IAST) plotData : (IAST) ast.arg1();
    if (listData.isEmpty()) {
      return F.Graphics3D(F.CEmptyList);
    }

    boolean isMultiDataset = false;
    boolean isHeightMap = false;
    IExpr first = PlotWrapper.strip(listData.arg1());
    if (first.isList()) {
      IAST firstList = (IAST) first;
      if (firstList.isList3() && !PlotWrapper.strip(firstList.arg1()).isList()) {
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

    RegionFunctionFilter region =
        RegionFunctionFilter.of(options[Plot3DTools.X_REGION_FUNCTION], engine);

    // The points are gathered before any of them is registered: a colour function is scaled over
    // the extent the data reaches, and that is not known until the last dataset has been read.
    List<List<double[]>> perDataset = new ArrayList<>();
    // parallel to perDataset: the label of each point, or NIL. A point label cannot ride on the
    // drawn geometry, because one Point primitive holds every point of a dataset.
    List<List<IExpr>> perDatasetLabels = new ArrayList<>();
    IExpr[] datasetLabels = new IExpr[datasets.size()];
    for (int i = 1; i < datasets.size(); i++) {
      List<double[]> coordinates = new ArrayList<>();
      List<IExpr> labels = new ArrayList<>();
      PlotWrapper datasetWrapper = PlotWrapper.of(datasets.get(i));
      datasetLabels[i] = datasetWrapper.tooltip;
      if (datasetWrapper.datum.isList()) {
        IAST dataset = (IAST) datasetWrapper.datum;
        if (isHeightMap) {
          collectHeightMap(dataset, dataRange, coordinates, region);
        } else {
          collectCoordinates(dataset, coordinates, labels, region);
        }
      }
      while (labels.size() < coordinates.size()) {
        labels.add(F.NIL);
      }
      perDataset.add(coordinates);
      perDatasetLabels.add(labels);
    }
    double[] box = extentOf(perDataset);
    PlotColorFunction pointColors = Plot3DTools
        .plotColors(PlotColorFunction.Family.SURFACE_3D, options, S.ListPointPlot3D, engine)
        .ranges(box[0], box[1], box[2], box[3], box[4], box[5]).build();

    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(false, pointColors != null);
    boolean any = false;
    for (int i = 0; i < perDataset.size(); i++) {
      List<double[]> coordinates = perDataset.get(i);
      if (coordinates.isEmpty()) {
        continue;
      }
      List<IExpr> labels = perDatasetLabels.get(i);
      IASTAppendable indices = F.ListAlloc(coordinates.size());
      // a labelled point is drawn by itself so that the label has a primitive of its own to sit
      // on; the rest stay in the one batched Point they were always in
      IASTAppendable labelled = F.ListAlloc(4);
      for (int k = 0; k < coordinates.size(); k++) {
        double[] point = coordinates.get(k);
        IExpr color = pointColors == null ? null : pointColors.color(point[0], point[1], point[2]);
        IExpr index = F.ZZ(builder.addVertex(point[0], point[1], point[2], null, color));
        IExpr label = k < labels.size() ? labels.get(k) : F.NIL;
        if (label.isPresent()) {
          labelled.append(F.binaryAST2(S.Tooltip, F.Point(F.List(index)), label));
        } else {
          indices.append(index);
        }
      }
      IExpr style = Plot3DTools.curveStyle(i, plotStyle);
      if (pointColors == null) {
        // a ColorFunction colours each point itself, and outranks PlotStyle where it does
        builder.addPrimitive(style);
      }
      IExpr batched = F.Point(indices);
      IExpr datasetLabel = datasetLabels[i + 1];
      if (indices.argSize() > 0) {
        builder.addPrimitive(
            datasetLabel != null && datasetLabel.isPresent()
                ? F.binaryAST2(S.Tooltip, batched, datasetLabel)
                : batched);
      }
      for (int k = 1; k < labelled.size(); k++) {
        builder.addPrimitive(labelled.get(k));
      }
      addFilling(builder, coordinates, filling, fillingStyle, style);
      any = true;
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
  private static void collectHeightMap(IAST rows, IExpr dataRange, List<double[]> coordinates,
      RegionFunctionFilter region) {
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
        if (region != null && !region.accepts(px, py, z)) {
          continue;
        }
        coordinates.add(new double[] {px, py, z});
      }
    }
  }

  /**
   * The box the points reach, as {@code {xMin, xMax, yMin, yMax, zMin, zMax}}.
   *
   * <p>
   * This is the range a colour function's arguments are scaled over. An empty plot reports the
   * unit box, which nothing will be coloured against anyway.
   */
  private static double[] extentOf(List<List<double[]>> datasets) {
    double[] box = {Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE,
        Double.MAX_VALUE, -Double.MAX_VALUE};
    boolean any = false;
    for (List<double[]> points : datasets) {
      for (double[] p : points) {
        any = true;
        for (int c = 0; c < 3; c++) {
          box[c * 2] = Math.min(box[c * 2], p[c]);
          box[c * 2 + 1] = Math.max(box[c * 2 + 1], p[c]);
        }
      }
    }
    return any ? box : new double[] {0, 1, 0, 1, 0, 1};
  }

  private static void collectCoordinates(IAST dataset, List<double[]> coordinates,
      List<IExpr> labels, RegionFunctionFilter region) {
    for (int k = 1; k < dataset.size(); k++) {
      // a display wrapper says how the point is shown, not where it is; one left on used to make
      // the point vanish from the plot with nothing said
      PlotWrapper wrapper = PlotWrapper.of(dataset.get(k));
      IExpr point = wrapper.datum;
      if (!point.isList3()) {
        continue;
      }
      double x = point.first().evalfNaN();
      double y = point.second().evalfNaN();
      double z = point.last().evalfNaN();
      // a point that cannot be evaluated is left out; it used to abandon the whole plot
      if (Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)
          && (region == null || region.accepts(x, y, z))) {
        coordinates.add(new double[] {x, y, z});
        labels.add(wrapper.tooltip);
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
    if (coordinates.isEmpty() || filling.isNone() || filling == S.Automatic) {
      return;
    }
    double base;
    if (filling == S.Bottom || filling == S.Axis) {
      base = filling == S.Axis ? 0.0 : Double.MAX_VALUE;
      if (filling == S.Bottom) {
        for (double[] xyz : coordinates) {
          base = Math.min(base, xyz[2]);
        }
      }
    } else if (filling == S.Top) {
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
    builder.addPrimitive(fillingStyle == S.Automatic ? pointStyle : fillingStyle);
    builder.addPrimitive(F.Line(stems));
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
