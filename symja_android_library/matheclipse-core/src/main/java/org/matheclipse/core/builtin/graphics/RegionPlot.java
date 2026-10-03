package org.matheclipse.core.builtin.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.RegionClip;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>RegionPlot(cond, {x, xmin, xmax}, {y, ymin, ymax})</code> - the region of the plane in
 * which the condition <code>cond</code> is <code>True</code>; a list of conditions gives one region
 * for each of them.
 *
 * <p>
 * The condition is sampled on a grid of <code>PlotPoints</code> cells per direction. A cell the
 * boundary of the region runs through is cut along it with {@link RegionClip}, so the outline is
 * not the staircase of the grid. The result is a <code>Graphics</code> of one
 * <code>GraphicsComplex</code> per region, which holds the filled polygons and the outline:
 * <code>RegionPlot(...)[[1]]</code> are primitives another <code>Graphics</code> can take.
 */
public class RegionPlot extends ContourPlot {

  /** The colour of a single region. */
  private static final IAST REGION_COLOR = F.RGBColor(F.num(0.24), F.num(0.6), F.num(0.8));

  /** How often a cell the boundary crosses twice is split into four. */
  private static final int MAX_SPLIT_DEPTH = 2;

  public RegionPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 3) {
      return F.NIL;
    }
    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    int plotPoints = GraphicsOptions.optionValue(originalAST, S.PlotPoints, S.Automatic)
        .toIntDefault(40);
    int maxRecursion =
        GraphicsOptions.optionValue(originalAST, S.MaxRecursion, S.Automatic).toIntDefault(-1);
    if (plotPoints < 2) {
      plotPoints = 2;
    }
    if (maxRecursion > 0) {
      // the grid is not refined adaptively: each level doubles the sampling resolution instead
      plotPoints *= 1 << Math.min(maxRecursion, 4);
    }
    plotPoints = Math.min(400, plotPoints);

    IExpr xIter = ast.arg2();
    IExpr yIter = ast.arg3();
    double[] xRange = range(xIter, engine);
    if (xRange == null) {
      // Range specification `1` is not of the form {x, xmin, xmax}.
      return Errors.printMessage(S.RegionPlot, "pllim", F.list(xIter), engine);
    }
    double[] yRange = range(yIter, engine);
    if (yRange == null) {
      return Errors.printMessage(S.RegionPlot, "pllim", F.list(yIter), engine);
    }
    graphicsOptions.setBoundingBox(new double[] {xRange[0], xRange[1], yRange[0], yRange[1]});
    ISymbol xVar = (ISymbol) xIter.first();
    ISymbol yVar = (ISymbol) yIter.first();

    IExpr plotStyle = GraphicsOptions.optionValue(originalAST, S.PlotStyle, S.Automatic);
    IExpr boundaryStyle = GraphicsOptions.optionValue(originalAST, S.BoundaryStyle, S.Automatic);

    IAST conditions = ast.arg1().isList() ? (IAST) ast.arg1() : F.list(ast.arg1());
    IASTAppendable primitives = F.ListAlloc(conditions.argSize() + 1);
    for (int k = 1; k < conditions.size(); k++) {
      final IExpr condition = conditions.get(k);
      RegionClip.Membership member = (x, y) -> engine.evalTrue(
          condition.replaceAll(F.List(F.Rule(xVar, F.num(x)), F.Rule(yVar, F.num(y)))));
      IExpr color = k == 1 ? REGION_COLOR : GraphicsOptions.plotStyleColorExpr(k - 1, F.NIL);
      IExpr style = plotStyle;
      if (style.isList()) {
        style = GraphicsOptions.getPlotStyle(style, k - 1);
      }
      IExpr region = new Tracer(member, xRange, yRange, plotPoints).trace(color, style,
          boundaryStyle);
      if (region.isPresent()) {
        primitives.append(region);
      }
    }
    // the second part of Mathematica's content list, which holds the labels
    primitives.append(F.CEmptyList);
    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /** The numeric range of <code>{x, xmin, xmax}</code>, or <code>null</code>. */
  private static double[] range(IExpr iter, EvalEngine engine) {
    if (iter.isList3() && iter.first().isSymbol()) {
      double min = engine.evalDouble(iter.second(), null, Double.NaN);
      double max = engine.evalDouble(((IAST) iter).arg3(), null, Double.NaN);
      if (Double.isFinite(min) && Double.isFinite(max) && min < max) {
        return new double[] {min, max};
      }
    }
    return null;
  }

  /** Traces one region on the sampling grid. */
  private static final class Tracer {
    private final RegionClip.Membership member;
    private final double x0;
    private final double y0;
    private final double dx;
    private final double dy;
    private final int n;

    /** The points of the <code>GraphicsComplex</code>, and the index each of them has. */
    private final IASTAppendable points = F.ListAlloc(256);
    private final Map<Long, Map<Long, Integer>> index = new HashMap<Long, Map<Long, Integer>>();
    private final IASTAppendable polygons = F.ListAlloc(256);
    /** The pieces of the outline, as pairs of point indices. */
    private final List<int[]> outline = new ArrayList<int[]>();

    Tracer(RegionClip.Membership member, double[] xRange, double[] yRange, int plotPoints) {
      this.member = member;
      this.n = plotPoints;
      this.x0 = xRange[0];
      this.y0 = yRange[0];
      this.dx = (xRange[1] - xRange[0]) / plotPoints;
      this.dy = (yRange[1] - yRange[0]) / plotPoints;
    }

    /** The <code>GraphicsComplex</code> of the region, or {@link F#NIL} if it is empty. */
    IExpr trace(IExpr color, IExpr plotStyle, IExpr boundaryStyle) {
      boolean[][] inside = new boolean[n + 1][n + 1];
      for (int i = 0; i <= n; i++) {
        for (int j = 0; j <= n; j++) {
          inside[i][j] = member.inside(x0 + i * dx, y0 + j * dy);
        }
      }
      for (int j = 0; j < n; j++) {
        int runStart = -1;
        for (int i = 0; i <= n; i++) {
          boolean full = i < n && inside[i][j] && inside[i + 1][j] && inside[i + 1][j + 1]
              && inside[i][j + 1];
          if (full) {
            if (runStart < 0) {
              runStart = i;
            }
            continue;
          }
          if (runStart >= 0) {
            // the cells of a row which are all inside are one rectangle
            double left = x0 + runStart * dx;
            double right = x0 + i * dx;
            double bottom = y0 + j * dy;
            double top = y0 + (j + 1) * dy;
            polygon(new double[][] {{left, bottom}, {right, bottom}, {right, top}, {left, top}});
            runStart = -1;
          }
          if (i < n && (inside[i][j] || inside[i + 1][j] || inside[i + 1][j + 1]
              || inside[i][j + 1])) {
            // every corner is computed from its grid index, so that two cells agree on the
            // corners - and with them on the boundary point - of the edge they share
            cell(x0 + i * dx, y0 + j * dy, x0 + (i + 1) * dx, y0 + (j + 1) * dy, new boolean[] {
                inside[i][j], inside[i + 1][j], inside[i + 1][j + 1], inside[i][j + 1]}, 0);
          }
        }
      }
      if (polygons.isEmpty()) {
        return F.NIL;
      }
      IExpr fillStyle = plotStyle.isAutomatic() || plotStyle.isNone()
          ? F.Directive(color, F.AbsoluteThickness(F.num(2.0)), F.Opacity(F.num(0.3)))
          : F.Directive(plotStyle);
      IASTAppendable content = F.ListAlloc(2);
      content.append(
          F.List(F.headAST0(S.EdgeForm), fillStyle,
              F.unaryAST1(S.GraphicsGroup, F.List(F.Polygon(polygons)))));
      if (!boundaryStyle.isNone() && !outline.isEmpty()) {
        IExpr lineStyle = boundaryStyle.isAutomatic()
            ? F.Directive(color, F.AbsoluteThickness(F.num(2.0)))
            : F.Directive(boundaryStyle);
        content.append(F.List(lineStyle, F.Line(outlineLoops())));
      }
      return F.GraphicsComplex(points, content);
    }

    /**
     * A cell which is partly inside: the part on the region's side of its boundary. A cell the
     * boundary crosses twice has no single edge, so it is split into four smaller cells.
     */
    private void cell(double left, double bottom, double right, double top, boolean[] inside,
        int depth) {
      double[][] corners = {{left, bottom}, {right, bottom}, {right, top}, {left, top}};
      if (inside[0] && inside[1] && inside[2] && inside[3]) {
        polygon(corners);
        return;
      }
      if (!(inside[0] || inside[1] || inside[2] || inside[3])) {
        return;
      }
      double[][] boundary = RegionClip.cellBoundary(member, corners, inside);
      if (boundary == null) {
        if (depth < MAX_SPLIT_DEPTH) {
          double midX = (left + right) / 2.0;
          double midY = (bottom + top) / 2.0;
          boolean south = member.inside(midX, bottom);
          boolean east = member.inside(right, midY);
          boolean north = member.inside(midX, top);
          boolean west = member.inside(left, midY);
          boolean centre = member.inside(midX, midY);
          cell(left, bottom, midX, midY, new boolean[] {inside[0], south, centre, west}, depth + 1);
          cell(midX, bottom, right, midY, new boolean[] {south, inside[1], east, centre}, depth + 1);
          cell(midX, midY, right, top, new boolean[] {centre, east, inside[2], north}, depth + 1);
          cell(left, midY, midX, top, new boolean[] {west, centre, north, inside[3]}, depth + 1);
        }
        return;
      }
      double[][] clipped =
          RegionClip.clipPolygon(corners, boundary[0], boundary[1], boundary[2][0], boundary[2][1]);
      if (clipped != null) {
        polygon(clipped);
      }
      int a = point(boundary[0]);
      int b = point(boundary[1]);
      if (a != b) {
        outline.add(new int[] {a, b});
      }
    }

    private void polygon(double[][] corners) {
      polygons.append(F.mapRange(0, corners.length, i -> F.ZZ(point(corners[i]))));
    }

    /** The index of a point, which is added to the points if it is new. */
    private int point(double[] p) {
      Map<Long, Integer> column =
          index.computeIfAbsent(Double.doubleToLongBits(p[0]), k -> new HashMap<Long, Integer>());
      Integer known = column.get(Double.doubleToLongBits(p[1]));
      if (known != null) {
        return known.intValue();
      }
      points.append(F.List(F.num(p[0]), F.num(p[1])));
      column.put(Double.doubleToLongBits(p[1]), Integer.valueOf(points.argSize()));
      return points.argSize();
    }

    /** The pieces of the outline joined end to end, as the index lists of a <code>Line</code>. */
    private IAST outlineLoops() {
      Map<Integer, List<int[]>> pieces = new HashMap<Integer, List<int[]>>();
      for (int[] piece : outline) {
        pieces.computeIfAbsent(piece[0], k -> new ArrayList<int[]>(2)).add(piece);
        pieces.computeIfAbsent(piece[1], k -> new ArrayList<int[]>(2)).add(piece);
      }
      java.util.Set<int[]> used =
          java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<int[], Boolean>());
      IASTAppendable loops = F.ListAlloc(4);
      for (int[] start : outline) {
        if (!used.add(start)) {
          continue;
        }
        java.util.ArrayDeque<Integer> chain = new java.util.ArrayDeque<Integer>();
        chain.add(start[0]);
        chain.add(start[1]);
        extend(chain, pieces, used, true);
        if (!chain.peekFirst().equals(chain.peekLast())) {
          extend(chain, pieces, used, false);
        }
        IASTAppendable loop = F.ListAlloc(chain.size());
        for (Integer i : chain) {
          loop.append(F.ZZ(i.intValue()));
        }
        loops.append(loop);
      }
      return loops;
    }

    /** Follow the unused pieces from one end of the chain as far as they go. */
    private static void extend(java.util.ArrayDeque<Integer> chain,
        Map<Integer, List<int[]>> pieces, java.util.Set<int[]> used, boolean atEnd) {
      while (true) {
        Integer end = atEnd ? chain.peekLast() : chain.peekFirst();
        int[] next = null;
        for (int[] piece : pieces.get(end)) {
          if (!used.contains(piece)) {
            next = piece;
            break;
          }
        }
        if (next == null) {
          return;
        }
        used.add(next);
        Integer other = Integer.valueOf(next[0] == end.intValue() ? next[1] : next[0]);
        if (atEnd) {
          chain.addLast(other);
        } else {
          chain.addFirst(other);
        }
        if (chain.peekFirst().equals(chain.peekLast())) {
          return;
        }
      }
    }
  }
}
