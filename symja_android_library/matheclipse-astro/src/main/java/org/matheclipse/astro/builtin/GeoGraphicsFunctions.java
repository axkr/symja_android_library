package org.matheclipse.astro.builtin;

import org.hipparchus.util.FastMath;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.geo.WorldOutline;
import org.matheclipse.astro.project.MapProjection;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GeoPositionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>GeoGraphics</code> - a map.
 *
 * <p>
 * This is the counterpart of {@link AstroGraphicsFunctions} and works the same way: project, scale,
 * and emit plain 2D primitives inside an ordinary <code>Graphics</code> for the existing SVG pipeline
 * to render. The projections are shared, since the Earth and the celestial sphere are the same
 * geometry - the difference is only what is drawn on them, and that a map is not mirrored.
 *
 * <p>
 * It is also what finally makes the tier 3 geographic primitives drawable:
 * <code>DayNightTerminator</code>, <code>DayHemisphere</code> and <code>NightHemisphere</code>
 * return <code>Line</code> and <code>Polygon</code> over <code>GeoPosition</code> points, and
 * nothing in the renderer understands a <code>GeoPosition</code>. This does, replacing each with
 * chart coordinates.
 */
public class GeoGraphicsFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.GeoGraphics.setEvaluator(new GeoGraphics());
    }
  }

  /** Everything one map needs, resolved from the options once. */
  private static final class Chart {
    MapProjection projection;
    double centerLongitude;
    double centerLatitude;

    /** Angular radius of the view, in degrees. */
    double range;

    /** Project a geographic position, or <code>null</code> if it is not visible. */
    double[] map(double longitude, double latitude) {
      return projection.project(FastMath.toRadians(longitude), FastMath.toRadians(latitude));
    }

    /** Angular distance from the centre of the map, in degrees. */
    double distance(double longitude, double latitude) {
      double lon1 = FastMath.toRadians(centerLongitude);
      double lat1 = FastMath.toRadians(centerLatitude);
      double lon2 = FastMath.toRadians(longitude);
      double lat2 = FastMath.toRadians(latitude);
      double cos = FastMath.sin(lat1) * FastMath.sin(lat2)
          + FastMath.cos(lat1) * FastMath.cos(lat2) * FastMath.cos(lon2 - lon1);
      return FastMath.toDegrees(FastMath.acos(FastMath.max(-1.0, FastMath.min(1.0, cos))));
    }
  }

  private static final class GeoGraphics extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      Chart chart = new Chart();

      double[] center = resolveCenter(options[1], engine);
      if (center == null) {
        return Errors.printMessage(S.GeoGraphics, "astrocenter", F.List(S.GeoCenter, options[1]), engine);
      }
      chart.centerLongitude = center[0];
      chart.centerLatitude = center[1];

      Double range = resolveRange(options[4], engine);
      if (range == null) {
        return Errors.printMessage(S.GeoGraphics, "astrorange", F.List(S.GeoRange, options[4]), engine);
      }
      chart.range = range.doubleValue();

      String projectionName = AstroConvert.optionString(options[3],
          chart.range >= 180.0 - 1.0e-9 ? "Equirectangular" : "Orthographic");
      chart.projection = MapProjection.of(projectionName,
          FastMath.toRadians(chart.centerLongitude), FastMath.toRadians(chart.centerLatitude));
      if (chart.projection == null) {
        return Errors.printMessage(S.GeoGraphics, "astroproj",
            F.List(F.stringx(projectionName), ast), engine);
      }

      IASTAppendable primitives = F.ListAlloc(8);
      appendLand(primitives, chart, options[0]);
      appendGridLines(primitives, chart, options[2]);
      if (argSize >= 1) {
        IExpr projected = projectPrimitives(ast.arg1(), chart);
        primitives.append(projected);
      }
      return buildGraphics(primitives, chart);
    }

    // ------------------------------------------------------------ options

    /** A <code>GeoPosition</code> or a <code>{latitude, longitude}</code> pair. */
    private double[] resolveCenter(IExpr center, EvalEngine engine) {
      if (center == null || center == S.Automatic) {
        return new double[] {0.0, 0.0};
      }
      if (center instanceof GeoPositionExpr) {
        GeoPositionExpr geo = (GeoPositionExpr) center;
        return new double[] {geo.longitude(), geo.latitude()};
      }
      if (center.isList() && ((IAST) center).argSize() == 2) {
        double[] vector = center.toDoubleVector();
        if (vector != null) {
          // a geographic pair is written latitude first, as GeoPosition does
          return new double[] {vector[1], vector[0]};
        }
      }
      return null;
    }

    private Double resolveRange(IExpr range, EvalEngine engine) {
      if (range == null || range == S.Automatic || range == S.All) {
        return Double.valueOf(180.0);
      }
      Double radians = AstroConvert.toRadians(range, engine);
      if (radians == null) {
        return null;
      }
      double degrees = FastMath.toDegrees(radians.doubleValue());
      if (degrees <= 0.0 || degrees > 180.0) {
        return null;
      }
      return Double.valueOf(degrees);
    }

    // --------------------------------------------------------- primitives

    /** The landmass basemap. */
    private void appendLand(IASTAppendable primitives, Chart chart, IExpr background) {
      if (background == S.None) {
        return;
      }
      IExpr fill = background == null || background == S.Automatic
          ? F.GrayLevel(0.85) : background;
      IASTAppendable group = F.ListAlloc(4);
      group.append(fill);
      for (double[][] ring : WorldOutline.get().land()) {
        appendRingRuns(group, S.Polygon, projectRing(ring, chart), 3);
      }
      primitives.append(group);
    }

    /** Meridians and parallels. */
    private void appendGridLines(IASTAppendable primitives, Chart chart, IExpr gridLines) {
      if (gridLines == S.None || gridLines == null) {
        return;
      }
      IASTAppendable group = F.ListAlloc(16);
      group.append(F.GrayLevel(0.6));
      group.append(F.Thickness(0.0010));
      for (double lon = -180.0; lon <= 180.0 + 1.0e-9; lon += 30.0) {
        IASTAppendable line = F.ListAlloc(91);
        for (double lat = -90.0; lat <= 90.0; lat += 2.0) {
          appendVisible(line, chart, lon, lat);
        }
        appendLineRuns(group, line);
      }
      for (double lat = -60.0; lat <= 60.0 + 1.0e-9; lat += 30.0) {
        IASTAppendable line = F.ListAlloc(181);
        for (double lon = -180.0; lon <= 180.0; lon += 2.0) {
          appendVisible(line, chart, lon, lat);
        }
        appendLineRuns(group, line);
      }
      primitives.append(group);
    }

    /**
     * Replace every <code>GeoPosition</code> in a primitive tree with chart coordinates, leaving
     * heads, styles and everything else alone.
     */
    private IExpr projectPrimitives(IExpr expr, Chart chart) {
      if (expr instanceof GeoPositionExpr) {
        GeoPositionExpr geo = (GeoPositionExpr) expr;
        if (chart.distance(geo.longitude(), geo.latitude()) > chart.range) {
          return F.List();
        }
        double[] xy = chart.map(geo.longitude(), geo.latitude());
        return xy == null ? F.List() : F.List(F.num(xy[0]), F.num(xy[1]));
      }
      if (!expr.isAST()) {
        return expr;
      }
      IAST list = (IAST) expr;
      // A Line or Polygon over geographic points has to be split where it crosses the
      // antimeridian, or the projected shape is joined straight across the map instead of
      // wrapping round the edge. Handled here rather than point by point because the split
      // produces several primitives from one.
      if ((list.isAST(S.Polygon, 2) || list.isAST(S.Line, 2)) && isGeoRing(list.arg1())) {
        return splitGeoRing(list.head(), (IAST) list.arg1(), chart);
      }
      IASTAppendable result = F.ast(list.head(), list.argSize());
      for (int i = 1; i < list.size(); i++) {
        result.append(projectPrimitives(list.get(i), chart));
      }
      return result;
    }

    // ------------------------------------------------------------ output

    private IExpr buildGraphics(IAST primitives, Chart chart) {
      double[] bounds = projectionBounds(chart);
      double padX = (bounds[1] - bounds[0]) * 0.02;
      double padY = (bounds[3] - bounds[2]) * 0.02;
      IASTAppendable graphics = F.ast(S.Graphics, 5);
      graphics.append(primitives);
      graphics.append(F.Rule(S.PlotRange, F.List(//
          F.List(F.num(bounds[0] - padX), F.num(bounds[1] + padX)), //
          F.List(F.num(bounds[2] - padY), F.num(bounds[3] + padY)))));
      graphics.append(F.Rule(S.AspectRatio,
          F.num((bounds[3] - bounds[2]) / (bounds[1] - bounds[0]))));
      graphics.append(F.Rule(S.Axes, S.False));
      return graphics;
    }

    /** Sample the visible sphere for the extent the projection covers. */
    private double[] projectionBounds(Chart chart) {
      double minX = Double.POSITIVE_INFINITY;
      double maxX = Double.NEGATIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;
      for (double lon = -180.0; lon <= 180.0; lon += 2.0) {
        for (double lat = -90.0; lat <= 90.0; lat += 2.0) {
          if (chart.distance(lon, lat) > chart.range) {
            continue;
          }
          double[] xy = chart.map(lon, lat);
          if (xy == null) {
            continue;
          }
          minX = FastMath.min(minX, xy[0]);
          maxX = FastMath.max(maxX, xy[0]);
          minY = FastMath.min(minY, xy[1]);
          maxY = FastMath.max(maxY, xy[1]);
        }
      }
      if (minX > maxX || minY > maxY) {
        return new double[] {-1.0, 1.0, -1.0, 1.0};
      }
      return new double[] {minX, maxX, minY, maxY};
    }

    // ----------------------------------------------------------- helpers

    private IAST projectRing(double[][] ring, Chart chart) {
      IASTAppendable points = F.ListAlloc(ring.length);
      double previousLongitude = Double.NaN;
      double previousLatitude = Double.NaN;
      for (double[] point : ring) {
        if (crossesAntimeridian(previousLongitude, previousLatitude, point[0], point[1])) {
          points.append(F.List());
        }
        previousLongitude = point[0];
        previousLatitude = point[1];
        appendVisible(points, chart, point[0], point[1]);
      }
      return points;
    }

    /**
     * Whether two consecutive longitudes step across the antimeridian.
     *
     * <p>
     * A real edge never jumps half way round the world between two points, so a step that large can
     * only be the seam at 180 degrees, where the data wraps from one end of the range to the other.
     */
    private static boolean crossesAntimeridian(double previousLongitude, double previousLatitude,
        double longitude, double latitude) {
      if (Double.isNaN(previousLongitude) || FastMath.abs(longitude - previousLongitude) <= 180.0) {
        return false;
      }
      // A segment which touches a pole is the edge that closes a polygon along the top or bottom
      // of the map - DayHemisphere and NightHemisphere are built that way - and must be kept.
      return FastMath.abs(previousLatitude) < 90.0 && FastMath.abs(latitude) < 90.0;
    }

    /** Whether a list holds only geographic positions, i.e. is a ring this can project. */
    private static boolean isGeoRing(IExpr expr) {
      if (!expr.isList() || ((IAST) expr).argSize() < 2) {
        return false;
      }
      IAST list = (IAST) expr;
      for (int i = 1; i < list.size(); i++) {
        if (!(list.get(i) instanceof GeoPositionExpr)) {
          return false;
        }
      }
      return true;
    }

    /**
     * Project a ring of geographic positions, splitting it at the antimeridian and wherever it
     * leaves the visible area, and return the pieces under {@code head}.
     */
    private IExpr splitGeoRing(IExpr head, IAST ring, Chart chart) {
      IASTAppendable points = F.ListAlloc(ring.argSize());
      double previousLongitude = Double.NaN;
      double previousLatitude = Double.NaN;
      for (int i = 1; i < ring.size(); i++) {
        GeoPositionExpr geo = (GeoPositionExpr) ring.get(i);
        if (crossesAntimeridian(previousLongitude, previousLatitude, geo.longitude(),
            geo.latitude())) {
          points.append(F.List());
        }
        previousLongitude = geo.longitude();
        previousLatitude = geo.latitude();
        appendVisible(points, chart, geo.longitude(), geo.latitude());
      }
      IASTAppendable pieces = F.ListAlloc(4);
      IASTAppendable run = F.ListAlloc(points.argSize());
      for (int i = 1; i < points.size(); i++) {
        IExpr point = points.get(i);
        if (point.isList() && ((IAST) point).argSize() == 2) {
          run.append(point);
        } else {
          if (run.argSize() >= 2) {
            pieces.append(F.unaryAST1(head, run));
          }
          run = F.ListAlloc(points.argSize());
        }
      }
      if (run.argSize() >= 2) {
        pieces.append(F.unaryAST1(head, run));
      }
      return pieces;
    }

    /** Append a projected point, or an empty list to mark a break in the line. */
    private void appendVisible(IASTAppendable points, Chart chart, double longitude,
        double latitude) {
      if (chart.distance(longitude, latitude) > chart.range) {
        points.append(F.List());
        return;
      }
      double[] xy = chart.map(longitude, latitude);
      points.append(xy == null ? F.List() : F.List(F.num(xy[0]), F.num(xy[1])));
    }

    /** Split a point list on its gap markers and append each run as a {@code Line}. */
    private void appendLineRuns(IASTAppendable group, IAST points) {
      appendRingRuns(group, S.Line, points, 2);
    }

    /**
     * Split a point list on its gap markers and append each run under {@code head}.
     *
     * @param minimumPoints 2 for a line, 3 for a polygon which needs an area to fill
     */
    private void appendRingRuns(IASTAppendable group, IExpr head, IAST points,
        int minimumPoints) {
      IASTAppendable run = F.ListAlloc(points.argSize());
      for (int i = 1; i < points.size(); i++) {
        IExpr point = points.get(i);
        if (point.isList() && ((IAST) point).argSize() == 2) {
          run.append(point);
        } else {
          if (run.argSize() >= minimumPoints) {
            group.append(F.unaryAST1(head, run));
          }
          run = F.ListAlloc(points.argSize());
        }
      }
      if (run.argSize() >= minimumPoints) {
        group.append(F.unaryAST1(head, run));
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.GeoBackground, S.GeoCenter, S.GeoGridLines, S.GeoProjection,
              S.GeoRange}, //
          new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.Automatic});
    }

    @Override
    public int status() {
      // only a landmass basemap: no coastline detail, political borders or tile imagery
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private GeoGraphicsFunctions() {}
}
