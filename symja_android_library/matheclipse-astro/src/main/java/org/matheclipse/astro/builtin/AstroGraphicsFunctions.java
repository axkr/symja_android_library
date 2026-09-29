package org.matheclipse.astro.builtin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.matheclipse.astro.convert.AstroBodies;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.data.AstroFallback;
import org.matheclipse.astro.data.AstroDataContext;
import org.matheclipse.astro.meeus.MeeusBody;
import org.matheclipse.astro.meeus.MoonTheory;
import org.matheclipse.astro.meeus.SpectralColor;
import org.matheclipse.astro.project.MapProjection;
import org.matheclipse.astro.sky.DeepStarCatalog;
import org.matheclipse.astro.sky.MilkyWayMap;
import org.matheclipse.astro.sky.SkyCatalog;
import org.matheclipse.astro.sky.SkyFrame;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GeoPositionExpr;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.PVCoordinatesProvider;
import org.orekit.time.TimeScalesFactory;

/**
 * <code>AstroGraphics</code> - a chart of the sky.
 *
 * <p>
 * The catalogue stores the sky the way GeoJSON stores a map, so drawing it is a projection followed
 * by a scale. Everything here therefore reduces to plain 2D primitives inside an ordinary
 * <code>Graphics</code>, which Symja's existing SVG pipeline already renders - no new renderer is
 * involved, and no graphics code lives outside this class.
 *
 * <p>
 * Charts are mirrored in right ascension. That is the convention for a sky map, because you look at
 * the celestial sphere from the inside rather than at a globe from the outside.
 */
public class AstroGraphicsFunctions {

  /** Half-width of a whole-sky chart, in degrees. */
  private static final double WHOLE_SKY = 180.0;

  /**
   * Diameter of the faintest star drawn, as a fraction of the chart width. With {@link #STAR_GROWTH}
   * these are the sizes of the Wolfram Language, whose stars are disks from 0.0011 of the width at
   * its limit to 0.0071 for Sirius on a whole sky chart.
   */
  private static final double FAINTEST_STAR = 0.0011;

  /** How much a star's diameter grows per magnitude it is brighter than the chart's limit. */
  private static final double STAR_GROWTH = 0.0011;

  /**
   * How far below the horizon a planisphere reaches, in degrees: the Wolfram Language draws the sky
   * above you to an altitude of -4.5 degrees, so the horizon is a circle inside the square.
   */
  private static final double HORIZON_DIP = 4.5;

  /** Charts narrower than this, in degrees, show constellations, star names and deep sky objects. */
  private static final double DETAIL_RANGE = 60.0;

  /** From the circle of a zoomed chart to the corners of the square around it, with a margin. */
  private static final double SQUARE_REACH = 1.5;

  /** The most deep sky objects one chart draws, brightest first. */
  private static final int MAX_DEEP_SKY_OBJECTS = 40;

  /** The magnitude steps stars are grouped by for drawing. */
  private static final double SIZE_STEP = 0.5;

  /** The faintest magnitude of the d3-celestial files; fainter limits read the ASCC catalogue. */
  private static final double D3_MAGNITUDE_LIMIT = 8.5;

  /**
   * The most stars one chart draws, which keeps the SVG of a chart below a megabyte and within the
   * renderer's element budget; the brightest stars are kept.
   */
  static final int MAX_CHART_STARS = 8000;

  /** The Sun, the Moon and the planets, in the order they are looked up. */
  private static final MeeusBody[] SOLAR_SYSTEM = MeeusBody.values();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.AstroGraphics.setEvaluator(new AstroGraphics());
    }
  }

  /** Everything one chart needs to know, resolved from the options once. */
  private static final class Chart {
    MapProjection projection;

    /** The frame everything is drawn in; the catalogue is rotated into it once. */
    SkyFrame frame;

    /** Whether the star colour is dark on a light ground, as the white sky style wants. */
    boolean darkOnLight;

    /** Centre of the view, in the coordinates of {@link #frame}. */
    /**
     * Where the chart is centred, in the chart's own frame - azimuth and altitude on a horizon
     * chart, right ascension and declination on an equatorial one. This is what the projection is
     * built around.
     */
    double centerRightAscension;
    double centerDeclination;

    /**
     * The same point in catalogue equatorial coordinates. The angle between two directions does not
     * change when both are rotated, so keeping the centre in the catalogue's own frame lets the
     * range test stay a plain spherical distance on the unrotated coordinates, instead of rotating
     * every star twice.
     */
    double centerEquatorialRightAscension;
    double centerEquatorialDeclination;

    /** Angular radius of the view, in degrees. */
    double range;

    /** Faintest star to draw. */
    double magnitudeLimit;

    /**
     * How far from the centre anything is drawn. A zoomed chart shows a square view, as in the
     * Wolfram Language, so it has to reach the corners of the square around the circle of
     * {@link #range}; the plot range, and so the visible square, still follows {@link #range}.
     */
    double selectRange;

    /**
     * Whether the chart is narrow enough for the detail the Wolfram Language shows on a zoomed
     * chart: the constellations, the names of the brighter stars and the deep sky objects.
     */
    boolean detailed;

    /** The names of the stars and bodies asked for as entities, which their marker labels instead. */
    final java.util.Set<String> markedStars = new java.util.HashSet<String>();

    /**
     * The faintest star actually drawn: {@link #magnitudeLimit}, or brighter when
     * {@link #MAX_CHART_STARS} cut the star layer short.
     */
    double effectiveMagnitudeLimit;

    /** Which catalogue the star layer was drawn from. */
    String starCatalog = "d3-celestial";

    /**
     * Whether the date was given in <code>AstroReferenceFrame</code>. Only a dated chart shows the
     * Sun, the Moon and the planets: an undated one is a chart of the fixed stars, which should not
     * change from one evaluation to the next.
     */
    boolean dated;

    boolean wholeSky;

    /**
     * The angular radius the plot range is fitted to: {@link #range}, or on a planisphere the sky
     * down to {@link #HORIZON_DIP} below the horizon.
     */
    double viewRange;

    /** The plot range, {minX, maxX, minY, maxY}, and its width, which label sizes are taken from. */
    double[] bounds;

    double width;

    /** The fraction of the extent the plot range is padded by on each side. */
    double padding;

    /**
     * The horizon of a horizon chart projected as a closed ring, when it is one, which the ground is
     * cut from; <code>null</code> when the horizon is drawn as a line instead.
     */
    double[][] horizonRing;

    /** Whether the Background is the ground, with the sky drawn over it as a disk. */
    boolean groundBackground;

    /** The colour of the sky given as <code>AstroBackground</code>, or <code>null</code>. */
    IExpr sky;

    /**
     * The boxes of the labels placed so far, {left, bottom, right, top}. A label that would overlap
     * one of them is left out, as the Wolfram Language leaves them out, so the brightest objects
     * keep their names.
     */
    final List<double[]> labelBoxes = new ArrayList<double[]>();

    /**
     * Project a catalogue position to chart coordinates, or <code>null</code> if it is not visible.
     * Everything drawn goes through here, which is what keeps the layers in one frame.
     */
    double[] map(double rightAscension, double declination) {
      double[] coordinates = frame.toFrame(rightAscension, declination);
      double[] xy = projection.project(FastMath.toRadians(coordinates[0]),
          FastMath.toRadians(coordinates[1]));
      if (xy == null) {
        return null;
      }
      if (frame.isHorizon()) {
        // Azimuth already runs clockwise from north, so it carries the inside-the-sphere flip
        // that right ascension does not - mirroring again would swap east and west. Negating both
        // axes is a rotation, not a mirror, and it is what puts north at the top and east on the
        // left, the way a planisphere is drawn.
        return new double[] {-xy[0], -xy[1]};
      }
      // right ascension increases to the left on a sky chart
      return new double[] {-xy[0], xy[1]};
    }

    /**
     * The catalogue position a chart point shows, <code>{rightAscension, declination}</code> in
     * degrees - the inverse of {@link #map} - or <code>null</code> off the projected sphere.
     */
    double[] unmap(double x, double y) {
      // undo the flips map() makes
      double[] lonLat = projection.inverse(-x, frame.isHorizon() ? -y : y);
      if (lonLat == null) {
        return null;
      }
      return frame.fromFrame(FastMath.toDegrees(lonLat[0]), FastMath.toDegrees(lonLat[1]));
    }

    /** The same, for a position already expressed in the chart's frame. */
    double[] mapInFrame(double longitude, double latitude) {
      double[] xy = projection.project(FastMath.toRadians(longitude), FastMath.toRadians(latitude));
      if (xy == null) {
        return null;
      }
      return frame.isHorizon() ? new double[] {-xy[0], -xy[1]} : new double[] {-xy[0], xy[1]};
    }
  }

  private static final class AstroGraphics extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      // the chart needs no ephemerides unless it is dated, so there is no checkAvailable() here;
      // open the fallback scope directly
      AstroFallback.begin(S.AstroGraphics, engine);
      Chart chart = new Chart();

      // ---- the frame everything else is expressed in, so it is resolved first
      // a plain AstroGraphics() is the sky above the observer; asking for objects, a centre or a
      // range asks for a chart of that part of the sky instead
      boolean plain = argSize == 0 && (options[1] == null || options[1] == S.Automatic)
          && (options[5] == null || options[5] == S.Automatic);
      chart.frame = resolveFrame(options[7], engine, chart, plain);
      if (chart.frame == null) {
        return Errors.printMessage(S.AstroGraphics, "astroframe",
            F.List(S.AstroReferenceFrame, options[7] == null ? S.Automatic : options[7]), engine);
      }
      boolean horizon = chart.frame.isHorizon();

      // ---- centre. Looking up from the ground the natural centre is the zenith.
      boolean automaticCenter = options[1] == null || options[1] == S.Automatic;
      double[] center = automaticCenter && argSize >= 1 ? centerOnNamedObjects(ast.arg1(), chart)
          : horizon && automaticCenter ? new double[] {0.0, 90.0}
              : resolveCenter(options[1], chart, engine);
      if (horizon && automaticCenter && argSize >= 1 && center[0] == 0.0 && center[1] == 0.0) {
        // no named objects to centre on: looking up, the natural centre is the zenith
        center = new double[] {0.0, 90.0};
      }
      if (center == null) {
        return Errors.printMessage(S.AstroGraphics, "astrocenter",
            F.List(S.AstroCenter, options[1]), engine);
      }
      chart.centerRightAscension = center[0];
      chart.centerDeclination = center[1];
      double[] equatorialCenter =
          chart.frame.fromFrame(chart.centerRightAscension, chart.centerDeclination);
      chart.centerEquatorialRightAscension = equatorialCenter[0];
      chart.centerEquatorialDeclination = equatorialCenter[1];

      // ---- range. A horizon chart shows the sky above the horizon and nothing below it.
      Double range =
          horizon && (options[5] == null || options[5] == S.Automatic) ? Double.valueOf(90.0)
              : resolveRange(options[5], engine);
      if (range == null) {
        return Errors.printMessage(S.AstroGraphics, "astrorange", F.List(S.AstroRange, options[5]),
            engine);
      }
      chart.range = range.doubleValue();
      chart.wholeSky = chart.range >= WHOLE_SKY - 1.0e-9;
      chart.detailed = chart.range < DETAIL_RANGE;
      chart.selectRange =
          chart.detailed ? FastMath.min(WHOLE_SKY, chart.range * SQUARE_REACH) : chart.range;
      chart.viewRange = chart.range;
      if (horizon && (options[5] == null || options[5] == S.Automatic)
          && chart.centerDeclination == 90.0) {
        // the planisphere: the sky just below the horizon is in the square too, under the ground
        chart.viewRange = chart.range + HORIZON_DIP;
        chart.selectRange = FastMath.max(chart.selectRange, chart.viewRange);
      }

      // ---- projection
      String projectionName = AstroConvert.optionString(options[4],
          horizon || !chart.wholeSky ? "Stereographic" : "Mollweide");
      chart.projection =
          MapProjection.of(projectionName, FastMath.toRadians(chart.centerRightAscension),
              FastMath.toRadians(chart.centerDeclination));
      if (chart.projection == null) {
        return Errors.printMessage(S.AstroGraphics, "astroproj",
            F.List(F.stringx(projectionName), ast), engine);
      }

      // ---- how faint to go
      chart.magnitudeLimit = resolveMagnitudeLimit(options[8], chart.range);
      chart.bounds = projectionBounds(chart);
      chart.width = chart.bounds[1] - chart.bounds[0];
      chart.padding = resolvePadding(options[6], chart);
      if (horizon) {
        chart.horizonRing = horizonRing(chart);
      }

      try {
        IASTAppendable primitives = F.ListAlloc(16);
        appendBackground(primitives, chart, options[0]);
        appendGridLines(primitives, chart, options[2], options[3]);
        appendMainPlanes(primitives, chart);
        if (argSize >= 1) {
          collectMarkedStars(ast.arg1(), chart);
        }
        appendConstellations(primitives, chart, chart.detailed
            ? new ArrayList<SkyCatalog.Constellation>(SkyCatalog.get().constellations())
            : argSize >= 1 ? constellationsIn(ast.arg1())
                : new ArrayList<SkyCatalog.Constellation>());
        // Built in the order the labels claim their space - the stars and planets before the deep
        // sky objects - and drawn with the deep sky objects underneath. The star layer comes first
        // of all, since it settles how faint the chart goes, which the labelled stars are sized by.
        IASTAppendable stars = F.ListAlloc(1);
        appendStars(stars, chart);
        IASTAppendable labeledData = F.ListAlloc(1);
        appendLabeledData(labeledData, chart);
        IASTAppendable deepSky = F.ListAlloc(1);
        appendDeepSkyObjects(deepSky, chart);
        primitives.appendArgs(deepSky);
        primitives.appendArgs(stars);
        primitives.appendArgs(labeledData);
        appendGround(primitives, chart);
        if (argSize >= 1) {
          IExpr projected = projectUserPrimitives(ast.arg1(), chart, ast, engine, true);
          if (projected.isNIL()) {
            return F.NIL;
          }
          // the Graphics default of black would vanish on the night sky; a colour the user gives
          // comes after this one and wins
          primitives.append(F.List(chart.darkOnLight ? F.RGBColor(0.75, 0.1, 0.1)
              : F.RGBColor(1.0, 0.45, 0.35), projected));
        }
        return buildGraphics(primitives, chart, options[6]);
      } catch (RuntimeException rex) {
        return Errors.printMessage(S.AstroGraphics, "orekitdata",
            F.List(F.stringx(String.valueOf(rex.getMessage()))), engine);
      }
    }

    // ------------------------------------------------------------ options

    /**
     * Resolve the <code>AstroReferenceFrame</code> option.
     *
     * <p>
     * Accepts the documented forms: a bare frame name, <code>{name, date, location}</code> in any
     * order, or <code>{name, "Date" -&gt; ..., "Location" -&gt; ...}</code>. A bare date is also
     * taken, which keeps the option's older meaning working - it then just supplies the instant for
     * an equatorial chart.
     *
     * @return the frame, or <code>null</code> if the specification is not usable - which for a
     *         horizon frame includes having no location to stand at
     */
    private SkyFrame resolveFrame(IExpr referenceFrame, EvalEngine engine, Chart chart,
        boolean plain) {
      String name = null;
      AbsoluteDate date = null;
      GeodeticPoint location = null;

      if (referenceFrame != null && referenceFrame != S.Automatic) {
        IAST parts = referenceFrame.isList() ? (IAST) referenceFrame : F.List(referenceFrame);
        for (int i = 1; i < parts.size(); i++) {
          IExpr part = parts.get(i);
          if (part.isRule()) {
            IExpr key = part.first();
            IExpr value = part.second();
            if (key.isString() && "Date".equalsIgnoreCase(key.toString())
                || key.isString() && "ObservationDate".equalsIgnoreCase(key.toString())) {
              date = AstroConvert.toAbsoluteDate(value);
            } else if (key.isString() && "Location".equalsIgnoreCase(key.toString())) {
              location = AstroConvert.toGeodeticPoint(value);
            }
            // any other frame parameter - aberration, refraction, polar motion - is accepted and
            // ignored, rather than rejecting a specification this cannot honour
            continue;
          }
          AbsoluteDate partDate = AstroConvert.toAbsoluteDate(part);
          if (partDate != null) {
            date = partDate;
            continue;
          }
          GeodeticPoint partPoint = AstroConvert.toGeodeticPoint(part);
          if (partPoint != null) {
            location = partPoint;
            continue;
          }
          if (part.isString()) {
            name = part.toString();
            continue;
          }
          return null;
        }
      }

      if (location == null) {
        location = defaultLocation(engine);
      }
      chart.dated = date != null;
      if (date == null) {
        date = AstroDataContext.isAvailable() ? AstroConvert.nowUTC() : null;
      }
      if (name == null && plain && (referenceFrame == null || referenceFrame == S.Automatic)
          && location != null && date != null) {
        // like the Wolfram Language: AstroGraphics() is the sky above the observer, now. Without a
        // known place - a server, a sandboxed session - it stays the whole-sky chart, which also
        // keeps that output independent of the time of day.
        name = "Horizon";
      }
      if (name == null) {
        return date == null ? SkyFrame.equatorial(null) : SkyFrame.equatorial(date);
      }
      if (date == null) {
        // every named frame needs an instant to be evaluated at
        return null;
      }
      if (location == null && "Horizon".equalsIgnoreCase(name)) {
        // A horizon asked for with nowhere known to stand. The Wolfram Language then stands at
        // GeoPosition({0, 0}) - its own chart without a location reports exactly that - and so
        // does this; MetaInformation says where the chart was drawn for.
        location = new GeodeticPoint(0.0, 0.0, 0.0);
      }
      return SkyFrame.of(name, date, location);
    }

    /**
     * The observer to fall back on, from <code>$GeoLocation</code>.
     *
     * @return the location, or <code>null</code> when the variable is unset
     */
    private GeodeticPoint defaultLocation(EvalEngine engine) {
      return AstroConvert.defaultObserver(engine);
    }

    /**
     * The centre of the chart: a <code>{rightAscension, declination}</code> pair, the name of a
     * body or star, or {@link S#Automatic} for the origin.
     *
     * @param referenceFrame the <code>AstroReferenceFrame</code> value, which supplies the date
     *        when the centre is a moving body
     * @return <code>{rightAscension, declination}</code> in degrees, or <code>null</code> if the
     *         value is not a legal centre
     */
    /**
     * Where to centre the chart, in the chart's own frame.
     *
     * <p>
     * A pair of coordinates is read as being in that frame already - on a horizon chart
     * <code>{180, 30}</code> means thirty degrees up in the south, which is what someone drawing a
     * horizon chart means by it. A body or star name is a direction on the sky rather than a pair
     * of numbers, so that is resolved to equatorial coordinates and then rotated into the frame.
     */
    private double[] resolveCenter(IExpr center, Chart chart, EvalEngine engine) {
      if (center == null || center == S.Automatic) {
        return new double[] {0.0, 0.0};
      }
      if (center.isList() && ((IAST) center).argSize() == 2) {
        IAST list = (IAST) center;
        Double ra = AstroConvert.toRadians(list.arg1(), engine);
        Double dec = AstroConvert.toRadians(list.arg2(), engine);
        if (ra == null || dec == null) {
          return null;
        }
        return new double[] {FastMath.toDegrees(ra), FastMath.toDegrees(dec)};
      }
      double[] equatorial = namedDirection(center, chart);
      return equatorial == null ? null : chart.frame.toFrame(equatorial[0], equatorial[1]);
    }

    /**
     * The name of a sky object written as a string or as an entity -
     * <code>Entity("Star", "Betelgeuse")</code>, <code>Entity("Planet", "Mars")</code> - or
     * <code>null</code> when {@code expr} is neither. Whether the name is known is decided later,
     * by {@link AstroBodies#target(IExpr)}.
     */
    private static IExpr objectName(IExpr expr) {
      if (expr.isString()) {
        return expr;
      }
      if (expr.isAST(S.Entity, 3) && expr.first().isString() && expr.second().isString()) {
        return expr.second();
      }
      return null;
    }

    /**
     * The J2000 right ascension and declination in degrees of a named object at the date of the
     * chart, or <code>null</code> when {@code expr} names nothing the module knows.
     */
    private static double[] namedDirection(IExpr expr, Chart chart) {
      SkyCatalog.Constellation constellation = constellationOf(expr);
      if (constellation != null) {
        return new double[] {constellation.labelRightAscension, constellation.labelDeclination};
      }
      IExpr name = objectName(expr);
      if (name == null) {
        return null;
      }
      AstroBodies.Target target = AstroBodies.target(name);
      if (target == null) {
        return null;
      }
      if (target.isStar) {
        // a star is a fixed direction and needs no ephemerides
        SkyCatalog.Star star = SkyCatalog.get().star(name.toString());
        if (star != null) {
          return new double[] {star.rightAscension, star.declination};
        }
      }
      if (!AstroDataContext.isAvailable()) {
        return null;
      }
      AbsoluteDate when = chart.frame.date() == null ? AstroConvert.nowUTC() : chart.frame.date();
      // where the solar system layer draws it, so a chart centres on the body it shows
      Frame gcrf = FramesFactory.getGCRF();
      Vector3D position = observed(target.provider, when, gcrf, observerPosition(chart, when, gcrf));
      return new double[] {
          SkyCatalog.normalizeRightAscensionDegrees(FastMath.toDegrees(position.getAlpha())),
          FastMath.toDegrees(position.getDelta())};
    }

    /**
     * The automatic centre of a chart with primitives: the mean direction of the objects named in
     * them, so that <code>AstroGraphics(Entity("Star", "Betelgeuse"), AstroRange -&gt; ...)</code>
     * shows Betelgeuse. Without named objects the centre stays at the origin, as before.
     */
    private double[] centerOnNamedObjects(IExpr primitives, Chart chart) {
      double[] sum = new double[3];
      int[] count = {0};
      collectNamedDirections(primitives, chart, sum, count, false);
      if (count[0] == 0) {
        return new double[] {0.0, 0.0};
      }
      double norm = FastMath.sqrt(sum[0] * sum[0] + sum[1] * sum[1] + sum[2] * sum[2]);
      if (norm < 1.0e-9) {
        // objects on opposite sides of the sky have no meaningful middle
        return new double[] {0.0, 0.0};
      }
      double ra = SkyCatalog
          .normalizeRightAscensionDegrees(FastMath.toDegrees(FastMath.atan2(sum[1], sum[0])));
      double dec = FastMath.toDegrees(FastMath.asin(sum[2] / norm));
      return chart.frame.toFrame(ra, dec);
    }

    /**
     * Add up the directions of the objects in {@code expr}: named objects anywhere, and
     * <code>{rightAscension, declination}</code> positions - with or without the distance that
     * AstroPosition adds - inside a <code>Point</code>, <code>Line</code> or other geometric
     * primitive, where a pair of numbers is a position and not, say, the offset of a
     * <code>Text</code>.
     */
    private void collectNamedDirections(IExpr expr, Chart chart, double[] sum, int[] count,
        boolean inGeometry) {
      double[] direction = namedDirection(expr, chart);
      if (direction == null && inGeometry) {
        direction = explicitDirection(expr);
      }
      if (direction != null) {
        double ra = FastMath.toRadians(direction[0]);
        double dec = FastMath.toRadians(direction[1]);
        sum[0] += FastMath.cos(dec) * FastMath.cos(ra);
        sum[1] += FastMath.cos(dec) * FastMath.sin(ra);
        sum[2] += FastMath.sin(dec);
        count[0]++;
        return;
      }
      if (expr.isAST() && !expr.isAST(S.Entity)) {
        IAST list = (IAST) expr;
        boolean geometry = inGeometry && list.isList() || list.isAST(S.Point)
            || list.isAST(S.Line) || list.isAST(S.Polygon) || list.isAST(S.Arrow);
        for (int i = 1; i < list.size(); i++) {
          collectNamedDirections(list.get(i), chart, sum, count, geometry && (i == 1 || list.isList()));
        }
      }
    }

    /**
     * The catalogue direction in degrees of an explicit position, or <code>null</code>; read the
     * way {@link #projectUserPrimitives} reads it.
     */
    private static double[] explicitDirection(IExpr expr) {
      if (!expr.isList()) {
        return null;
      }
      IAST list = (IAST) expr;
      if (!(list.argSize() == 2 || list.argSize() == 3 && isDistance(list.arg3()))
          || !isAngle(list.arg1()) || !isAngle(list.arg2())) {
        return null;
      }
      EvalEngine engine = EvalEngine.get();
      Double ra = AstroConvert.toRadians(list.arg1(), engine);
      Double dec = AstroConvert.toRadians(list.arg2(), engine);
      return ra == null || dec == null ? null
          : new double[] {FastMath.toDegrees(ra), FastMath.toDegrees(dec)};
    }

    /** The angular radius of the view in degrees; {@link S#Automatic} means the whole sky. */
    private Double resolveRange(IExpr range, EvalEngine engine) {
      if (range == null || range == S.Automatic || range == S.All) {
        return Double.valueOf(WHOLE_SKY);
      }
      Double radians = AstroConvert.toRadians(range, engine);
      if (radians == null) {
        return null;
      }
      double degrees = FastMath.toDegrees(radians.doubleValue());
      if (degrees <= 0.0 || degrees > WHOLE_SKY) {
        return null;
      }
      return Double.valueOf(degrees);
    }

    /**
     * How faint to draw.
     *
     * <p>
     * <code>AstroZoomLevel</code> selects background survey imagery, which this implementation does
     * not have. It is taken here as the limiting magnitude instead, which is the same idea - how
     * much detail to show - expressed in what the catalogue can actually provide. Left to
     * {@link S#Automatic} the limit follows the size of the view.
     */
    private double resolveMagnitudeLimit(IExpr zoomLevel, double range) {
      if (zoomLevel != null && zoomLevel.isReal()) {
        return zoomLevel.evalf();
      }
      if (range >= 60.0) {
        // a whole-sky chart or the sky above the horizon: about 950 stars, as the Wolfram Language
        // draws - fainter stars only crowd a chart this size into a grey haze
        return 4.5;
      }
      if (range >= 20.0) {
        return 6.0;
      }
      if (range >= 5.0) {
        return D3_MAGNITUDE_LIMIT;
      }
      // a telescope field: the ASCC catalogue goes down to magnitude 11.1
      return range >= 2.0 ? 10.0 : 11.1;
    }

    // --------------------------------------------------------- primitives

    /**
     * The sky: the Milky Way band, or a flat fill, behind everything else.
     *
     * <p>
     * The documented styles are <code>"GalacticSky"</code>, <code>"BlackSky"</code> and
     * <code>"WhiteSky"</code>. {@link S#Automatic} is the black sky, as in the Wolfram Language; the
     * Milky Way is drawn only for <code>"GalacticSky"</code>.
     */
    private void appendBackground(IASTAppendable primitives, Chart chart, IExpr background) {
      IExpr style = background;
      if (style != null && style.isAST(S.AstroStyling, 2)) {
        // AstroStyling[style] selects a sky style; its extra directives are not supported
        style = style.first();
      }
      if (style == S.None) {
        return;
      }
      String name = style == null || style == S.Automatic ? "BlackSky"
          : style.isString() ? style.toString() : "";
      boolean milkyWay;
      if ("GalacticSky".equalsIgnoreCase(name) || "MilkyWay".equalsIgnoreCase(name)) {
        milkyWay = true;
      } else if ("WhiteSky".equalsIgnoreCase(name)) {
        chart.darkOnLight = true;
        milkyWay = false;
      } else if ("BlackSky".equalsIgnoreCase(name)) {
        milkyWay = false;
      } else {
        // a colour, or anything else: use it as the sky and draw no band
        milkyWay = false;
        if (style != null && GraphicsOptions.isColorExpr(style)) {
          chart.sky = style;
        }
      }
      IASTAppendable group = F.ListAlloc(16);
      if (chart.horizonRing != null) {
        // The ground is laid over the sky, so the sky has to fill the plot range beneath it, as the
        // Wolfram Language fills it with a square. The Background is then the ground as it looks
        // on the sky, which carries it on into the margin the renderer leaves around the plot.
        chart.groundBackground = chart.centerDeclination > 0.0;
        double[] b = chart.bounds;
        group.append(skyColor(chart));
        group.append(F.Rectangle(F.List(F.num(b[0] - chart.width), F.num(b[2] - chart.width)),
            F.List(F.num(b[1] + chart.width), F.num(b[3] + chart.width))));
      }
      if (!milkyWay) {
        // the sky is the Background of the Graphics; the layer stays, as in the Wolfram Language
        primitives.append(layer(group, "AstroBackground"));
        return;
      }
      group.append(milkyWayRaster(chart));
      primitives.append(layer(group, "AstroBackground"));
    }

    /** The cells across the longer side of the Milky Way raster; the glow needs no more. */
    private static final int MILKY_WAY_CELLS = 200;

    /** The colour of the brightest parts of the Milky Way, a warm cream as in a photograph. */
    private static final double[] MILKY_WAY_COLOR = {0.66, 0.58, 0.49};

    /**
     * The Milky Way as the glowing band the Wolfram Language shows: a smooth raster over the plot
     * range, each cell the brightness of the band at the direction it shows, found by running the
     * projection backwards. A raster rather than the contour polygons, because a polygon has to be
     * cut where it leaves the view or crosses a seam, and those cuts are what smeared the band
     * across the chart; the inverse projection has no such edge.
     */
    private IExpr milkyWayRaster(Chart chart) {
      MilkyWayMap map = MilkyWayMap.get();
      double[] b = chart.bounds;
      // the padded plot range, and a little more so the smoothing has no edge in view
      double marginX = (FastMath.max(0.0, chart.padding) + 0.01) * chart.width;
      double marginY = (FastMath.max(0.0, chart.padding) + 0.01) * (b[3] - b[2]);
      double x0 = b[0] - marginX;
      double x1 = b[1] + marginX;
      double y0 = b[2] - marginY;
      double y1 = b[3] + marginY;
      double longer = FastMath.max(x1 - x0, y1 - y0);
      int columns = FastMath.max(2, (int) FastMath.round(MILKY_WAY_CELLS * (x1 - x0) / longer));
      int rows = FastMath.max(2, (int) FastMath.round(MILKY_WAY_CELLS * (y1 - y0) / longer));
      // one colour with 64 steps of opacity, each cell sharing the list of its step
      IAST[] steps = new IAST[65];
      for (int k = 0; k < steps.length; k++) {
        double alpha = k / 64.0;
        steps[k] = F.List(F.num(MILKY_WAY_COLOR[0]), F.num(MILKY_WAY_COLOR[1]),
            F.num(MILKY_WAY_COLOR[2]), F.num(alpha));
      }
      IASTAppendable cells = F.ListAlloc(rows);
      // the first row of a Raster is its bottom row
      for (int r = 0; r < rows; r++) {
        double y = y0 + (r + 0.5) * (y1 - y0) / rows;
        IASTAppendable row = F.ListAlloc(columns);
        for (int c = 0; c < columns; c++) {
          double x = x0 + (c + 0.5) * (x1 - x0) / columns;
          double[] sky = chart.unmap(x, y);
          if (sky != null && chart.horizonRing != null
              && chart.frame.toFrame(sky[0], sky[1])[1] < 0.0) {
            // below the horizon: the ground hides it, as in the Wolfram Language
            sky = null;
          }
          double level = sky == null ? 0.0 : map.brightness(sky[0], sky[1]) / MilkyWayMap.LEVELS;
          // the faint outer levels lift the sky a little; the bright core stands out
          double alpha = FastMath.min(1.0, 0.85 * FastMath.pow(level, 0.85));
          row.append(steps[(int) FastMath.round(alpha * 64.0)]);
        }
        cells.append(row);
      }
      IASTAppendable raster = F.ast(S.Raster, 3);
      raster.append(cells);
      raster.append(F.List(F.List(F.num(x0), F.num(y0)), F.List(F.num(x1), F.num(y1))));
      // samples of something continuous: drawn smoothly between the cells
      raster.append(F.Rule(S.InterpolationOrder, F.C1));
      return raster;
    }

    /**
     * The reference great circles, each labelled the way the Wolfram Language labels them: the
     * celestial equator with the hours of right ascension, the ecliptic with the months in which the
     * Sun passes each point, the galactic equator with galactic longitude, and on a horizon chart the
     * horizon with the compass directions.
     *
     * <p>
     * Each is a circle of zero latitude in its own frame, so it is generated there and pushed
     * through the chart's frame like everything else - which is what makes it curve correctly
     * whichever frame the chart is drawn in.
     */
    private void appendMainPlanes(IASTAppendable primitives, Chart chart) {
      IASTAppendable group = F.ListAlloc(16);
      group.append(F.Thickness(PLANE_THICKNESS));

      // the equator: Hue(0.54) in the Wolfram Language, 0h to 23h
      IASTAppendable equator = planeGroup(0.0, 0.76, 1.0);
      IASTAppendable scale = scaleGroup(chart);
      appendPlane(equator, chart, "Equatorial");
      for (int hour = 0; hour < 24; hour++) {
        // a superscript h, as the Wolfram Language writes Superscript(hour, "h")
        appendPlaneTick(scale, chart, "Equatorial", 15.0 * hour, hour + "\u02b0", false);
      }
      group.append(equator);

      // the ecliptic: Hue(0.14), the Sun's place on the first of each month, labelled on the
      // other side of the line (TickLabelPositioning -> Before)
      IASTAppendable ecliptic = planeGroup(1.0, 0.84, 0.0);
      appendPlane(ecliptic, chart, "Ecliptic");
      int year = chart.frame.date() == null ? 2000
          : chart.frame.date().getComponents(TimeScalesFactory.getUTC()).getDate().getYear();
      for (int month = 1; month <= 12; month++) {
        appendPlaneTick(scale, chart, "Ecliptic", solarLongitude(year, month), MONTHS[month - 1],
            true);
      }
      group.append(ecliptic);

      // the galactic equator: Hue(0.02, 0.7, 0.8), every 45 degrees
      IASTAppendable galactic = planeGroup(0.8, 0.31, 0.24);
      appendPlane(galactic, chart, "Galactic");
      for (int longitude = 0; longitude < 360; longitude += 45) {
        appendPlaneTick(scale, chart, "Galactic", longitude, longitude + "\u00b0", false);
      }
      group.append(galactic);
      group.append(scale);

      if (chart.frame.isHorizon()) {
        // the horizon, with the compass directions just above it; where the ground is drawn its
        // edge is the horizon, and a line along it would only blur that edge
        IASTAppendable horizonGroup = F.ListAlloc(12);
        horizonGroup.append(chart.darkOnLight ? F.GrayLevel(0.55) : F.GrayLevel(0.4));
        if (chart.horizonRing == null) {
          IASTAppendable line = F.ListAlloc(181);
          for (double azimuth = 0.0; azimuth <= 360.0; azimuth += 2.0) {
            appendVisibleInFrame(line, chart, azimuth, 0.0);
          }
          appendLineRuns(horizonGroup, line);
        }
        for (int k = 0; k < COMPASS.length; k++) {
          double[] xy = chart.mapInFrame(45.0 * k, 3.0);
          if (xy != null) {
            horizonGroup.append(
                label(COMPASS[k], xy, NAME_FONT, false));
          }
        }
        group.append(horizonGroup);
      }
      primitives.append(layer(group, "MainPlanes"));
    }

    /** Font sizes as fractions of the image width, those of the Wolfram Language. */
    private static final double NAME_FONT = 0.018;

    private static final double TICK_FONT = 0.0126;

    /** The large constellation names of a zoomed chart. */
    private static final double CONSTELLATION_FONT = 0.04;

    /**
     * A text label in a font scaled to the image, like the labels of the Wolfram Language.
     *
     * @param upperRight whether the text sits above and to the right of the point, clear of the
     *        object there, rather than centred on it
     */
    private static IAST label(String text, double[] xy, double size, boolean upperRight) {
      IAST styled = F.binaryAST2(S.Style, F.stringx(text),
          F.Rule(S.FontSize, F.unaryAST1(S.Scaled, F.num(size))));
      IAST position = F.List(F.num(xy[0]), F.num(xy[1]));
      return upperRight ? F.Text(styled, position, F.List(F.num(-1.15), F.num(-1.15)))
          : F.Text(styled, position);
    }

    private static final String[] MONTHS =
        {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private static final String[] COMPASS = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

    /** A group for one plane, drawn in its colour at half opacity as the Wolfram Language does. */
    private static IASTAppendable planeGroup(double red, double green, double blue) {
      IASTAppendable group = F.ListAlloc(40);
      group.append(F.RGBColor(red, green, blue));
      group.append(F.Opacity(F.num(0.5)));
      return group;
    }

    /** The thickness of the main planes, as a fraction of the chart width. */
    private static final double PLANE_THICKNESS = 0.0028;

    /** The length of a scale tick on a plane, as a fraction of the chart width. */
    private static final double TICK_LENGTH = 0.008;

    /** How far a scale label sits from its plane, to its centre, as a fraction of the width. */
    private static final double TICK_LABEL_GAP = 0.018;

    /**
     * The ticks and labels of the planes' scales, grey like the ticks of an <code>AxisObject</code>,
     * which is what the Wolfram Language draws each plane with.
     */
    private static IASTAppendable scaleGroup(Chart chart) {
      IASTAppendable group = F.ListAlloc(80);
      group.append(chart.darkOnLight ? F.GrayLevel(0.55) : F.GrayLevel(0.4));
      group.append(F.Thickness(0.0014));
      return group;
    }

    /**
     * The apparent ecliptic longitude of the Sun in degrees at 0h UT on the first of {@code month},
     * from the low accuracy formula of the Astronomical Almanac (good to 0.01 degrees), which is
     * plenty for placing a month label.
     */
    private static double solarLongitude(int year, int month) {
      double n = java.time.LocalDate.of(year, month, 1).toEpochDay() - 10957.5;
      double meanLongitude = 280.460 + 0.9856474 * n;
      double anomaly = FastMath.toRadians(357.528 + 0.9856003 * n);
      return meanLongitude + 1.915 * FastMath.sin(anomaly) + 0.020 * FastMath.sin(2 * anomaly);
    }

    /**
     * A tick across a plane at {@code longitude} in the plane's own frame, with its label beside it
     * and turned to run along the line, if it is in view.
     *
     * @param before whether the tick and label go on the left of the direction of increasing
     *        longitude rather than on the right
     */
    private void appendPlaneTick(IASTAppendable group, Chart chart, String planeFrame,
        double longitude, String label, boolean before) {
      SkyFrame plane = SkyFrame.of(planeFrame, chart.frame.date(), null);
      if (plane == null) {
        return;
      }
      double[] equatorial = plane.fromFrame(longitude, 0.0);
      if (angularDistance(chart, equatorial[0], equatorial[1]) > chart.selectRange) {
        return;
      }
      double[] xy = chart.map(equatorial[0], equatorial[1]);
      double[] ahead = plane.fromFrame(longitude + 0.5, 0.0);
      double sign = 1.0;
      if (crossesSeam(chart, chart.frame.toFrame(equatorial[0], equatorial[1])[0],
          chart.frame.toFrame(ahead[0], ahead[1])[0])) {
        // the point ahead is across the seam, at the other edge of the map: look back instead
        ahead = plane.fromFrame(longitude - 0.5, 0.0);
        sign = -1.0;
      }
      double[] direction =
          xy == null ? null : chartDirection(chart, xy, ahead[0], ahead[1], sign);
      if (direction == null) {
        return;
      }
      // the normal on the right of the direction of travel, or on the left
      double side = before ? -1.0 : 1.0;
      double nx = side * direction[1];
      double ny = -side * direction[0];
      double tick = TICK_LENGTH * chart.width;
      double gap = TICK_LABEL_GAP * chart.width;
      group.append(F.Line(F.List(F.List(F.num(xy[0]), F.num(xy[1])),
          F.List(F.num(xy[0] + nx * tick), F.num(xy[1] + ny * tick)))));
      // read left to right whichever way the line runs
      double dx = direction[0] < 0.0 ? -direction[0] : direction[0];
      double dy = direction[0] < 0.0 ? -direction[1] : direction[1];
      IAST styled = F.binaryAST2(S.Style, F.stringx(label),
          F.Rule(S.FontSize, F.unaryAST1(S.Scaled, F.num(TICK_FONT))));
      IASTAppendable text = F.ast(S.Text, 4);
      text.append(styled);
      text.append(F.List(F.num(xy[0] + nx * gap), F.num(xy[1] + ny * gap)));
      text.append(F.List(F.C0, F.C0));
      text.append(F.List(F.num(dx), F.num(dy)));
      group.append(text);
    }

    /**
     * How much of the sky below the horizon shows through the ground: a little, so that a bright
     * star or a plane which has just set can still be made out, as on the charts of the Wolfram
     * Language documentation.
     */
    private static final double GROUND_OPACITY = 0.78;

    /**
     * The ground colours, a slate on the black sky - which comes out as RGB 91, 95, 116, the ground
     * of the documentation charts - and a light stone on the white one.
     */
    private static final double[] GROUND_ON_BLACK = {0.46, 0.48, 0.59};

    private static final double[] GROUND_ON_WHITE = {0.8, 0.78, 0.74};

    /**
     * The horizon of a horizon chart as a closed ring of chart points, or <code>null</code> when it
     * is not one - on a projection with a seam, which cuts it open, when the chart looks along the
     * horizon, which projects it to an open curve, or when part of it does not project at all.
     */
    private static double[][] horizonRing(Chart chart) {
      if (chart.projection.hasSeam() || FastMath.abs(chart.centerDeclination) < 1.0) {
        return null;
      }
      double[][] ring = new double[360][];
      for (int azimuth = 0; azimuth < 360; azimuth++) {
        double[] xy = chart.mapInFrame(azimuth, 0.0);
        if (xy == null || !Double.isFinite(xy[0]) || !Double.isFinite(xy[1])) {
          return null;
        }
        ring[azimuth] = xy;
      }
      return ring;
    }

    /**
     * The ground: everything below the horizon, covered in a slate colour through which the sky
     * there shows faintly. Drawn over the stars, the planes and the labels, so that what has set
     * fades out under it instead of being cut off.
     */
    private void appendGround(IASTAppendable primitives, Chart chart) {
      if (chart.horizonRing == null) {
        return;
      }
      IAST ring = ringPoints(chart.horizonRing);
      IAST ground;
      if (chart.centerDeclination > 0.0) {
        // looking up, the ground is all around the horizon: a box beyond the plot range with the
        // sky cut out of it
        double[] b = chart.bounds;
        double margin = chart.width;
        IAST box = F.List(F.List(F.num(b[0] - margin), F.num(b[2] - margin)),
            F.List(F.num(b[1] + margin), F.num(b[2] - margin)),
            F.List(F.num(b[1] + margin), F.num(b[3] + margin)),
            F.List(F.num(b[0] - margin), F.num(b[3] + margin)));
        ground = F.unaryAST1(S.Polygon, F.Rule(box, F.List(ring)));
      } else {
        // looking down, it is the inside of the horizon
        ground = F.unaryAST1(S.Polygon, ring);
      }
      double[] color = chart.darkOnLight ? GROUND_ON_WHITE : GROUND_ON_BLACK;
      IASTAppendable group = F.ListAlloc(3);
      group.append(F.RGBColor(color[0], color[1], color[2]));
      group.append(F.Opacity(F.num(GROUND_OPACITY)));
      group.append(ground);
      primitives.append(layer(group, "AstroGround"));
    }

    /** The ground colour laid over the sky, as it looks where nothing shows through. */
    private static IExpr groundOverSky(Chart chart) {
      double[] color = chart.darkOnLight ? GROUND_ON_WHITE : GROUND_ON_BLACK;
      double[] sky = skyRGB(chart);
      return F.RGBColor(GROUND_OPACITY * color[0] + (1.0 - GROUND_OPACITY) * sky[0],
          GROUND_OPACITY * color[1] + (1.0 - GROUND_OPACITY) * sky[1],
          GROUND_OPACITY * color[2] + (1.0 - GROUND_OPACITY) * sky[2]);
    }

    /**
     * The red, green and blue of the sky: of the colour given as <code>AstroBackground</code> when
     * it is an <code>RGBColor</code> or a <code>GrayLevel</code> (a named colour evaluates to the
     * former), else black or white.
     */
    private static double[] skyRGB(Chart chart) {
      IExpr sky = chart.sky == null ? null : EvalEngine.get().evaluate(chart.sky);
      if (sky != null && sky.isAST(S.RGBColor) && ((IAST) sky).argSize() >= 3) {
        double[] rgb = new double[3];
        for (int i = 0; i < 3; i++) {
          rgb[i] = ((IAST) sky).get(i + 1).evalf();
        }
        return rgb;
      }
      if (sky != null && sky.isAST(S.GrayLevel) && ((IAST) sky).argSize() >= 1) {
        double level = sky.first().evalf();
        return new double[] {level, level, level};
      }
      double level = chart.darkOnLight ? 1.0 : 0.0;
      return new double[] {level, level, level};
    }

    private static IAST ringPoints(double[][] ring) {
      IASTAppendable points = F.ListAlloc(ring.length);
      for (double[] xy : ring) {
        points.append(F.List(F.num(xy[0]), F.num(xy[1])));
      }
      return points;
    }

    /** One great circle, generated as the zero latitude line of {@code planeFrame}. */
    private void appendPlane(IASTAppendable group, Chart chart, String planeFrame) {
      SkyFrame plane = SkyFrame.of(planeFrame, chart.frame.date(), null);
      if (plane == null) {
        return;
      }
      double[][] circle = new double[181][];
      for (int k = 0; k <= 180; k++) {
        // walk the circle in the plane's own frame, then convert back to catalogue coordinates
        circle[k] = plane.fromFrame(2.0 * k, 0.0);
      }
      // through projectRing, which breaks the line where it crosses the seam of a whole sky map;
      // joined across it, the line jumps from one edge of the map to the other
      appendLineRuns(group, projectRing(circle, chart));
    }

    /**
     * The constellations named in the primitives - <code>Entity("Constellation", "Orion")</code> -
     * with their figure, IAU boundary and name. As in the Wolfram Language no constellation is drawn
     * unless it is asked for; the layer is there, empty, either way.
     */
    private void appendConstellations(IASTAppendable primitives, Chart chart,
        List<SkyCatalog.Constellation> constellations) {
      IASTAppendable group = F.ListAlloc(16);
      if (!constellations.isEmpty()) {
        // boundaries dotted, figures in a light grey, as the Wolfram Language draws them
        IASTAppendable boundaries = F.ListAlloc(constellations.size() + 3);
        boundaries.append(chart.darkOnLight ? F.GrayLevel(0.6) : F.GrayLevel(0.4));
        boundaries.append(F.Thickness(0.0012));
        boundaries.append(F.Dashing(F.List(F.num(0.004), F.num(0.006))));
        for (SkyCatalog.Constellation constellation : constellations) {
          for (double[][] ring : SkyCatalog.get().constellationBoundary(constellation.code)) {
            appendLineRuns(boundaries, projectRing(ring, chart));
          }
        }
        group.append(boundaries);
        group.append(chart.darkOnLight ? F.GrayLevel(0.45) : F.GrayLevel(0.62));
        group.append(F.Thickness(0.0018));
        for (SkyCatalog.Constellation constellation : constellations) {
          for (double[][] ring : SkyCatalog.get().constellationLines(constellation.code)) {
            appendLineRuns(group, projectRing(ring, chart));
          }
        }
        group.append(chart.darkOnLight ? F.GrayLevel(0.55) : F.GrayLevel(0.45));
        for (SkyCatalog.Constellation constellation : constellations) {
          if (angularDistance(chart, constellation.labelRightAscension,
              constellation.labelDeclination) > chart.selectRange) {
            continue;
          }
          double[] xy =
              chart.map(constellation.labelRightAscension, constellation.labelDeclination);
          if (xy != null) {
            group.append(
                F.Text(F.binaryAST2(S.Style, F.stringx(constellation.name),
                    F.List(F.Rule(S.FontSize, F.unaryAST1(S.Scaled, F.num(CONSTELLATION_FONT))),
                        S.Bold)),
                    F.List(F.num(xy[0]), F.num(xy[1]))));
          }
        }
      }
      primitives.append(layer(group, "Constellations"));
    }

    /** Record the proper names of the stars named in the primitives, which their markers label. */
    private void collectMarkedStars(IExpr expr, Chart chart) {
      if (constellationOf(expr) == null && objectName(expr) != null) {
        AstroBodies.Target target = AstroBodies.target(objectName(expr));
        if (target != null) {
          chart.markedStars.add(target.name);
        }
        return;
      }
      if (expr.isAST() && !expr.isAST(S.Entity)) {
        IAST list = (IAST) expr;
        for (int i = 1; i < list.size(); i++) {
          collectMarkedStars(list.get(i), chart);
        }
      }
    }

    /** The constellation an <code>Entity("Constellation", name)</code> names, or null. */
    private static SkyCatalog.Constellation constellationOf(IExpr expr) {
      if (expr.isAST(S.Entity, 3) && expr.first().isString("Constellation")
          && expr.second().isString()) {
        return SkyCatalog.get().constellation(expr.second().toString());
      }
      return null;
    }

    /** The constellations named anywhere in the primitives, in order and without repeats. */
    private static List<SkyCatalog.Constellation> constellationsIn(IExpr primitives) {
      List<SkyCatalog.Constellation> result = new ArrayList<SkyCatalog.Constellation>();
      collectConstellations(primitives, result);
      return result;
    }

    private static void collectConstellations(IExpr expr,
        List<SkyCatalog.Constellation> result) {
      SkyCatalog.Constellation constellation = constellationOf(expr);
      if (constellation != null) {
        if (!result.contains(constellation)) {
          result.add(constellation);
        }
        return;
      }
      if (expr.isAST() && !expr.isAST(S.Entity)) {
        IAST list = (IAST) expr;
        for (int i = 1; i < list.size(); i++) {
          collectConstellations(list.get(i), result);
        }
      }
    }

    /**
     * The deep sky objects of a zoomed chart - the Messier, NGC and IC objects bright enough for it,
     * at most {@link #MAX_DEEP_SKY_OBJECTS} - each with the symbol of its kind and its designation,
     * as the Wolfram Language draws them: a dashed circle for an open cluster, a square for a
     * nebula, an ellipse for a galaxy and a circle for a globular cluster or a planetary nebula.
     * Dark nebulae, whose catalogue magnitude is no brightness, are left out.
     */
    private void appendDeepSkyObjects(IASTAppendable primitives, Chart chart) {
      if (!chart.detailed) {
        // a wide chart has no deep sky objects, and so no layer for them
        return;
      }
      IASTAppendable group = F.ListAlloc(8);
      {
        List<SkyCatalog.DeepSkyObject> visible = new ArrayList<SkyCatalog.DeepSkyObject>();
        for (SkyCatalog.DeepSkyObject object : SkyCatalog.get()
            .deepSkyObjects(chart.magnitudeLimit + 2.5)) {
          String designation = object.designation;
          if (!(designation.startsWith("M ") || designation.startsWith("NGC ")
              || designation.startsWith("IC ")) || "dn".equals(object.type)
              || angularDistance(chart, object.rightAscension, object.declination)
                  > chart.selectRange) {
            continue;
          }
          visible.add(object);
        }
        visible.sort((a, b) -> Double.compare(a.magnitude, b.magnitude));
        if (visible.size() > MAX_DEEP_SKY_OBJECTS) {
          visible = visible.subList(0, MAX_DEEP_SKY_OBJECTS);
        }
        group.append(chart.darkOnLight ? F.GrayLevel(0.4) : F.GrayLevel(0.6));
        group.append(F.Thickness(0.0012));
        for (SkyCatalog.DeepSkyObject object : visible) {
          double[] xy = chart.map(object.rightAscension, object.declination);
          if (xy == null) {
            continue;
          }
          // the catalogue size where it is larger than a marker, so big nebulae come out big
          double marker = 0.8 * markerRadius(chart, object.rightAscension, object.declination);
          double major = FastMath.max(marker,
              sizeOnChart(chart, object.rightAscension, object.declination, object.majorAxis));
          double minor = FastMath.max(0.5 * marker,
              sizeOnChart(chart, object.rightAscension, object.declination, object.minorAxis));
          group.append(deepSkySymbol(object.type, xy, major, minor));
          String name = designation(object);
          double[] corner = {xy[0] + 0.7 * major, xy[1] + 0.7 * major};
          if (claimLabel(chart, name, corner, NAME_FONT * 0.85)) {
            group.append(label(name, corner, NAME_FONT * 0.85, true));
          }
        }
      }
      primitives.append(layer(group, "DeepSkyObjects"));
    }

    /** "M 35" is written "M35", as in the Wolfram Language; NGC and IC keep their space. */
    private static String designation(SkyCatalog.DeepSkyObject object) {
      return object.designation.startsWith("M ") ? "M" + object.designation.substring(2)
          : object.designation;
    }

    /** Half of an angular size in minutes of arc, as a length on the chart. */
    private double sizeOnChart(Chart chart, double rightAscension, double declination,
        double arcminutes) {
      if (Double.isNaN(arcminutes) || arcminutes <= 0.0) {
        return 0.0;
      }
      double half = arcminutes / 120.0;
      double[] here = chart.map(rightAscension, declination);
      double[] there =
          chart.map(rightAscension, declination + (declination > 0.0 ? -half : half));
      return here == null || there == null ? 0.0
          : FastMath.hypot(there[0] - here[0], there[1] - here[1]);
    }

    private static IExpr deepSkySymbol(String type, double[] xy, double major, double minor) {
      IAST center = F.List(F.num(xy[0]), F.num(xy[1]));
      switch (type) {
        case "oc":
        case "sfr":
          return F.List(F.Dashing(F.List(F.num(0.002), F.num(0.003))),
              F.binaryAST2(S.Circle, center, F.num(major)));
        case "bn":
        case "en":
        case "rn":
        case "snr":
          return F.List(F.EdgeForm(F.GrayLevel(0.6)), F.FaceForm(S.None),
              F.binaryAST2(S.Rectangle, F.List(F.num(xy[0] - major), F.num(xy[1] - major)),
                  F.List(F.num(xy[0] + major), F.num(xy[1] + major))));
        case "gc":
        case "pn":
          return F.binaryAST2(S.Circle, center, F.num(major));
        default:
          // the galaxies: s, s0, e, i, g, gg, sd
          return F.binaryAST2(S.Circle, center, F.List(F.num(major), F.num(minor)));
      }
    }

    /**
     * The radius to draw a deep sky marker with, as the projected length of a fixed angle at that
     * point on the sky.
     *
     * <p>
     * Measured locally rather than taken as a constant because every projection here stretches by a
     * different amount in different places; a constant would come out lopsided near the edges.
     */
    private double markerRadius(Chart chart, double rightAscension, double declination) {
      double offset = FastMath.min(1.5, FastMath.max(0.25, chart.range * 0.02));
      double otherDeclination = declination + (declination > 0.0 ? -offset : offset);
      // An object on the seam has its two sample points land at opposite edges of the chart, and
      // the distance between them would ask for a circle the size of the whole map. Half a degree
      // of declination can carry a point right across the seam once the frame is rotated, so the
      // check is made on the frame's longitude, the only place the jump is visible.
      if (crossesSeam(chart, chart.frame.toFrame(rightAscension, declination)[0],
          chart.frame.toFrame(rightAscension, otherDeclination)[0])) {
        return 0.0;
      }
      double[] here = chart.map(rightAscension, declination);
      double[] there = chart.map(rightAscension, otherDeclination);
      if (here == null || there == null) {
        return 0.0;
      }
      return FastMath.hypot(there[0] - here[0], there[1] - here[1]);
    }

    /**
     * Wrap a group of primitives as a named layer.
     *
     * <p>
     * <code>Annotation</code> renders its first argument and ignores the rest, so this changes
     * nothing about the drawing while making a layer selectable:
     * <code>Cases[chart, Annotation[_, "AstroStars", _], Infinity]</code>.
     */
    private static IExpr layer(IAST primitives, String name) {
      return F.ternaryAST3(S.Annotation, primitives, F.stringx(name),
          F.stringx("SymjaAstroGraphics"));
    }

    /** Lines of constant right ascension and declination. */
    private void appendGridLines(IASTAppendable primitives, Chart chart, IExpr gridLines,
        IExpr style) {
      int count = gridLines == null || !gridLines.isReal() ? 0 : gridLines.toIntDefault();
      if (count <= 0) {
        // like the Wolfram Language, a chart has no grid unless it is asked for: None, Automatic
        primitives.append(layer(F.List(), "AstroGridLines"));
        return;
      }
      IASTAppendable group = F.ListAlloc(count * 2 + 2);
      group.append(style == null || style == S.Automatic ? F.GrayLevel(0.45) : style);
      group.append(F.Thickness(0.0012));

      // meridians
      double raStep = 360.0 / count;
      for (double ra = 0.0; ra < 360.0 - 1.0e-9; ra += raStep) {
        IASTAppendable line = F.ListAlloc(91);
        for (double dec = -90.0; dec <= 90.0; dec += 2.0) {
          appendVisible(line, chart, ra, dec);
        }
        appendLineRuns(group, line);
      }
      // parallels
      for (double dec = -75.0; dec <= 75.0 + 1.0e-9; dec += 15.0) {
        IASTAppendable line = F.ListAlloc(181);
        for (double ra = 0.0; ra <= 360.0; ra += 2.0) {
          appendVisible(line, chart, ra, dec);
        }
        appendLineRuns(group, line);
      }
      primitives.append(layer(group, "AstroGridLines"));
    }

    /** One point per star, sized by magnitude. */
    private void appendStars(IASTAppendable primitives, Chart chart) {
      // Select first, then size the result to what survives. Sizing it to the whole catalogue
      // instead would ask for a 41000 element AST for a chart which draws a few hundred stars,
      // and Config.MAX_AST_SIZE rightly refuses that. Each entry is {ra, dec, magnitude, color}.
      List<double[]> visible = new ArrayList<double[]>();
      chart.effectiveMagnitudeLimit = chart.magnitudeLimit;
      if (chart.magnitudeLimit > D3_MAGNITUDE_LIMIT && DeepStarCatalog.isAvailable()) {
        // fainter than the d3-celestial files go: the ASCC-2.5 catalogue of Night Vision, which
        // is sorted by magnitude, so the cap keeps the brightest stars of the field
        chart.starCatalog = "ASCC-2.5";
        DeepStarCatalog catalog = DeepStarCatalog.get();
        chart.effectiveMagnitudeLimit = catalog.forEachInCap(chart.centerEquatorialRightAscension,
            chart.centerEquatorialDeclination, chart.selectRange, chart.magnitudeLimit,
            MAX_CHART_STARS,
            (index, ra, dec, magnitude) -> visible
                .add(new double[] {ra, dec, magnitude, catalog.colorIndex(index)}));
      } else {
        for (SkyCatalog.Star star : SkyCatalog.get().starsToMagnitude(chart.magnitudeLimit)) {
          // outside the view there is nothing to draw, and on a zoomed chart that is almost all
          // of them - without this test the whole catalogue lands off-canvas and the SVG balloons
          if (angularDistance(chart, star.rightAscension, star.declination) > chart.selectRange) {
            continue;
          }
          if (isLabeled(star, chart)) {
            // drawn with its name in the LabeledData layer
            continue;
          }
          visible.add(new double[] {star.rightAscension, star.declination, star.magnitude,
              colorIndexOf(star.colorIndex)});
        }
      }
      // One PointSize and one multi-point Point per colour and size step, instead of a pair per
      // star: a few hundred primitives for 8000 stars, and the SVG renderer then writes each
      // group's paint once rather than on every circle.
      TreeMap<Integer, List<double[]>> groups = new TreeMap<Integer, List<double[]>>();
      for (double[] star : visible) {
        double[] xy = chart.map(star[0], star[1]);
        if (xy == null) {
          continue;
        }
        int color = chart.darkOnLight ? 0 : (int) star[3];
        // faintest step first, so that brighter stars are drawn over fainter ones
        int key = sizeStep(star[2], chart) * (SpectralColor.UNSPECIFIED + 1) + color;
        groups.computeIfAbsent(key, k -> new ArrayList<double[]>()).add(xy);
      }
      IASTAppendable group = F.ListAlloc(3 * groups.size() + 1);
      if (chart.darkOnLight) {
        // colours do not read on white paper; Night Vision prints stars black as well
        group.append(F.GrayLevel(0.1));
      }
      int lastColor = -1;
      for (Map.Entry<Integer, List<double[]>> entry : groups.entrySet()) {
        int color = entry.getKey() % (SpectralColor.UNSPECIFIED + 1);
        int step = entry.getKey() / (SpectralColor.UNSPECIFIED + 1);
        if (!chart.darkOnLight && color != lastColor) {
          int[] rgb = SpectralColor.rgb(color);
          group.append(F.RGBColor(rgb[0] / 255.0, rgb[1] / 255.0, rgb[2] / 255.0));
          lastColor = color;
        }
        group.append(F.PointSize(starSize(step * SIZE_STEP)));
        IASTAppendable points = F.ListAlloc(entry.getValue().size());
        for (double[] xy : entry.getValue()) {
          points.append(F.List(F.num(xy[0]), F.num(xy[1])));
        }
        group.append(F.unaryAST1(S.Point, points));
      }
      primitives.append(layer(group, "AstroStars"));
    }

    /**
     * How many {@link #SIZE_STEP} magnitude steps a star is brighter than the faintest one drawn.
     * The steps are what the stars are grouped by: half a magnitude changes the dot by a fifth of a
     * pixel on a chart 360 pixels wide, which no one can see.
     */
    private static int sizeStep(double magnitude, Chart chart) {
      double faintest = chart.effectiveMagnitudeLimit;
      double brightness = Double.isNaN(magnitude) ? faintest : magnitude;
      return (int) FastMath.round(FastMath.max(0.0, faintest - brightness) / SIZE_STEP);
    }

    /**
     * The spectral colour of a B-V colour index, by the usual boundaries between the classes, so
     * that the d3-celestial stars are coloured like the ASCC ones.
     */
    private static int colorIndexOf(double bv) {
      if (Double.isNaN(bv)) {
        return SpectralColor.UNSPECIFIED;
      }
      char letter = bv < -0.30 ? 'O'
          : bv < -0.02 ? 'B'
              : bv < 0.30 ? 'A' : bv < 0.58 ? 'F' : bv < 0.81 ? 'G' : bv < 1.40 ? 'K' : 'M';
      return SpectralColor.index(letter);
    }

    /**
     * The Sun, the Moon and the planets at the date of the chart, drawn farthest first so that the
     * nearer bodies cover the farther ones, as Night Vision draws them.
     *
     * <p>
     * The planets are points sized by their magnitude like the stars (with the Meeus magnitude
     * formulas), clamped so that Venus does not swamp the chart and Neptune does not vanish. The
     * Sun and the Moon are disks at least a marker in size; the Moon shows its phase, turned by the
     * position angle of its bright limb. On a chart with a location the positions are topocentric,
     * which moves the Moon by up to a degree.
     */
    private IAST solarSystem(Chart chart) {
      AbsoluteDate date = chart.frame.date();
      if (date == null) {
        // no date, and no ephemerides to take one from
        return null;
      }
      Frame gcrf = FramesFactory.getGCRF();
      Vector3D observer = observerPosition(chart, date, gcrf);
      Vector3D sun = observed(AstroBodies.sun(), date, gcrf, observer);
      List<Object[]> bodies = new ArrayList<Object[]>();
      for (MeeusBody body : SOLAR_SYSTEM) {
        String name = body.name().charAt(0) + body.name().substring(1).toLowerCase(Locale.US);
        Vector3D position = body == MeeusBody.SUN ? sun
            : observed(AstroBodies.provider(name), date, gcrf, observer);
        bodies.add(new Object[] {body, name, position, null});
      }
      bodies.sort((a, b) -> Double.compare(((Vector3D) b[2]).getNorm(),
          ((Vector3D) a[2]).getNorm()));

      // the nearest bodies claim their label first, so the Moon keeps its name next to a planet
      java.util.Set<String> labeled = new java.util.HashSet<String>();
      for (int i = bodies.size() - 1; i >= 0; i--) {
        Object[] entry = bodies.get(i);
        Vector3D position = (Vector3D) entry[2];
        double ra = SkyCatalog.normalizeRightAscensionDegrees(FastMath.toDegrees(position.getAlpha()));
        double dec = FastMath.toDegrees(position.getDelta());
        double[] xy = angularDistance(chart, ra, dec) > chart.selectRange ? null : chart.map(ra, dec);
        entry[3] = xy;
        if (xy != null && claimLabel(chart, (String) entry[1], xy, NAME_FONT)) {
          labeled.add((String) entry[1]);
        }
      }

      IASTAppendable group = F.ListAlloc(4 * bodies.size());
      for (Object[] entry : bodies) {
        MeeusBody body = (MeeusBody) entry[0];
        String name = (String) entry[1];
        Vector3D position = (Vector3D) entry[2];
        double[] xy = (double[]) entry[3];
        if (xy == null) {
          continue;
        }
        double ra = SkyCatalog.normalizeRightAscensionDegrees(FastMath.toDegrees(position.getAlpha()));
        double dec = FastMath.toDegrees(position.getDelta());
        if (body.isPlanet()) {
          double magnitude = SolarSystemDataFunctions
              .observedProperty(name, "ApparentMagnitude", date).evalf();
          double pointSize = pointSize(FastMath.max(-2.0, FastMath.min(2.0, magnitude)), chart);
          // zoomed in far enough the planet is larger than its point: then it is drawn as it
          // is, a disk of its true size showing its phase
          double trueRadius = FastMath.toDegrees(FastMath.asin(
              FastMath.min(1.0, AstroBodies.meanRadius(name) / position.getNorm())));
          double diskRadius = angularRadiusOnChart(chart, ra, dec, trueRadius);
          double pointRadius = 0.5 * pointSize * chart.width * (1.0 + 2.0 * chart.padding);
          IExpr color = chart.darkOnLight ? F.RGBColor(0.55, 0.35, 0.0)
              : F.RGBColor(1.0, 0.85, 0.45);
          if (diskRadius > pointRadius) {
            appendPhased(group, chart, xy, diskRadius, ra, dec, position, sun,
                chart.darkOnLight ? F.GrayLevel(0.8) : F.GrayLevel(0.22), color);
          } else {
            group.append(color);
            group.append(F.PointSize(pointSize));
            group.append(F.unaryAST1(S.Point, F.List(F.num(xy[0]), F.num(xy[1]))));
          }
        } else {
          double trueRadius = FastMath.toDegrees(FastMath.asin(
              FastMath.min(1.0, AstroBodies.meanRadius(name) / position.getNorm())));
          double radius = diskRadius(chart, ra, dec, trueRadius);
          if (radius <= 0.0) {
            continue;
          }
          if (body == MeeusBody.SUN) {
            group.append(chart.darkOnLight ? F.RGBColor(0.8, 0.55, 0.0)
                : F.RGBColor(1.0, 0.9, 0.35));
            group.append(F.binaryAST2(S.Disk, F.List(F.num(xy[0]), F.num(xy[1])), F.num(radius)));
          } else {
            appendPhased(group, chart, xy, radius, ra, dec, position, sun,
                chart.darkOnLight ? F.GrayLevel(0.75) : F.GrayLevel(0.32),
                chart.darkOnLight ? F.GrayLevel(0.35) : F.RGBColor(0.96, 0.96, 0.86));
          }
        }
        // a body asked for as an entity is labelled by its marker; its space stays claimed
        if (labeled.contains(name) && !chart.markedStars.contains(name)) {
          group.append(chart.darkOnLight ? F.GrayLevel(0.2) : F.GrayLevel(0.7));
          group.append(label(name, xy, NAME_FONT, true));
        }
      }
      return group;
    }

    /** The observer of a chart with a location, in the geocentric frame; else the geocentre. */
    private static Vector3D observerPosition(Chart chart, AbsoluteDate date, Frame gcrf) {
      return chart.frame.location() == null ? Vector3D.ZERO
          : AstroConvert.toTopocentricFrame(chart.frame.location()).getPVCoordinates(date, gcrf)
              .getPosition();
    }

    /**
     * Where a body is seen from {@code observer} at {@code date}: its geocentric position at the
     * time the light left it. Taking the Earth at that earlier time too - which the geocentric
     * frame does - makes up for the aberration as well (Meeus, ch. 33), so this is the apparent
     * place the Wolfram Language draws with <code>"LightTime" -&gt; "Observation"</code>. It matters
     * where two bodies pass each other: Jupiter moves about 25 arcseconds in the 50 minutes its
     * light takes to arrive, Venus as much in the minutes its light takes, so without it an
     * occultation of Jupiter by Venus is drawn as a near miss.
     */
    private static Vector3D observed(PVCoordinatesProvider provider, AbsoluteDate date,
        Frame gcrf, Vector3D observer) {
      Vector3D position = provider.getPosition(date, gcrf).subtract(observer);
      for (int i = 0; i < 2; i++) {
        double lightTime = position.getNorm() / Constants.SPEED_OF_LIGHT;
        position = provider.getPosition(date.shiftedBy(-lightTime), gcrf).subtract(observer);
      }
      return position;
    }

    /**
     * The magnitude up to which a named star gets its name on the chart. Down to 1.6 these are the
     * twenty or so brightest stars - Sirius to Castor - which the Wolfram Language labels.
     */
    private static final double LABEL_MAGNITUDE = 1.6;

    /** The pole star, at magnitude 2, which the Wolfram Language labels as well. */
    private static final String POLARIS = "Polaris";

    private static final double POLARIS_MAGNITUDE = 2.0;

    /**
     * Whether a star is drawn with its name. On a wide chart the named stars to magnitude 1.6 and
     * Polaris; on a zoomed one, as in the Wolfram Language, every star within 2.3 magnitudes of the
     * chart's limit that has a name or a Bayer letter.
     */
    private static boolean isLabeled(SkyCatalog.Star star, Chart chart) {
      if (POLARIS.equals(star.properName)) {
        return true;
      }
      if (!chart.detailed) {
        return !star.properName.isEmpty() && star.magnitude <= LABEL_MAGNITUDE;
      }
      return !starLabel(star).isEmpty() && star.magnitude <= labelMagnitude(chart);
    }

    /**
     * Claim the space of a label with its lower left corner at {@code xy}, as {@link #label} places
     * a label with <code>upperRight</code>. Returns <code>false</code> when it would overlap a label
     * placed before; the text size is estimated from the number of characters, which is close
     * enough to tell whether two names run into each other.
     */
    private static boolean claimLabel(Chart chart, String text, double[] xy, double size) {
      double height = size * chart.width;
      // with a little room to either side, so two names do not read as one
      double[] box = {xy[0] - 0.2 * height, xy[1],
          xy[0] + (0.55 * text.length() + 0.5) * height, xy[1] + 1.05 * height};
      for (double[] other : chart.labelBoxes) {
        if (box[0] < other[2] && other[0] < box[2] && box[1] < other[3] && other[1] < box[3]) {
          return false;
        }
      }
      chart.labelBoxes.add(box);
      return true;
    }

    private static double labelMagnitude(Chart chart) {
      return chart.detailed ? FastMath.max(LABEL_MAGNITUDE, chart.magnitudeLimit - 2.3)
          : LABEL_MAGNITUDE;
    }

    /** The proper name, else the Bayer designation with the constellation, else nothing. */
    private static String starLabel(SkyCatalog.Star star) {
      if (!star.properName.isEmpty()) {
        return star.properName;
      }
      if (!star.bayer.isEmpty() && !star.constellation.isEmpty()) {
        return star.bayer + " " + star.constellation;
      }
      return "";
    }

    /**
     * The brightest named stars with their names, and the Sun, the Moon and the planets - the
     * <code>LabeledData</code> layer of the Wolfram Language, with the solar system nested in it as
     * <code>AstroSolarSystem</code>.
     */
    private void appendLabeledData(IASTAppendable primitives, Chart chart) {
      IASTAppendable group = F.ListAlloc(64);
      // the Sun, the Moon and the planets place their labels first, then the stars, brightest
      // first; a name which would run into one already placed is left out
      IAST solarSystem = solarSystem(chart);
      double labelLimit = FastMath.max(labelMagnitude(chart), POLARIS_MAGNITUDE);
      List<SkyCatalog.Star> candidates = SkyCatalog.get().starsToMagnitude(labelLimit);
      candidates.sort((a, b) -> Double.compare(a.magnitude, b.magnitude));
      for (SkyCatalog.Star star : candidates) {
        if (!isLabeled(star, chart) || star.magnitude > chart.magnitudeLimit
            || angularDistance(chart, star.rightAscension, star.declination) > chart.selectRange) {
          continue;
        }
        double[] xy = chart.map(star.rightAscension, star.declination);
        if (xy == null) {
          continue;
        }
        int[] rgb = SpectralColor.rgb(colorIndexOf(star.colorIndex));
        group.append(chart.darkOnLight ? F.GrayLevel(0.1)
            : F.RGBColor(rgb[0] / 255.0, rgb[1] / 255.0, rgb[2] / 255.0));
        group.append(F.PointSize(pointSize(star.magnitude, chart)));
        group.append(F.unaryAST1(S.Point, F.List(F.num(xy[0]), F.num(xy[1]))));
        // the name above and to the right, clear of the star
        // a star asked for as an entity is labelled by its marker instead
        String name = starLabel(star);
        if (!chart.markedStars.contains(star.properName) && claimLabel(chart, name, xy, NAME_FONT)) {
          group.append(chart.darkOnLight ? F.GrayLevel(0.3) : F.GrayLevel(0.7));
          group.append(label(name, xy, NAME_FONT, true));
        }
      }
      if (solarSystem != null) {
        group.append(layer(solarSystem, "AstroSolarSystem"));
      }
      primitives.append(layer(group, "LabeledData"));
    }

    /**
     * The Moon, or a planet seen close up, as a dim disk with its lit part on top: a semicircle on
     * the side of the bright limb closed by the terminator, a half ellipse whose width follows the
     * illuminated fraction - the vector form of Night Vision's <code>drawPhasedObject</code>. The
     * bright limb faces the Sun, so the position angle formula of the Moon serves any body.
     */
    private void appendPhased(IASTAppendable group, Chart chart, double[] xy, double radius,
        double ra, double dec, Vector3D moon, Vector3D sun, IExpr darkColor, IExpr litColor) {
      // the angle Sun - Moon - observer gives the illuminated fraction
      double phaseAngle = Vector3D.angle(sun.subtract(moon), moon.negate());
      double fraction = (1.0 + FastMath.cos(phaseAngle)) / 2.0;
      double positionAngle = MoonTheory.brightLimbAngle(FastMath.toRadians(ra),
          FastMath.toRadians(dec), sun.getAlpha(), sun.getDelta());
      // north and east on the chart at the Moon, which carry the position angle over whatever the
      // projection and the frame have done to the sky
      double[] north = chartDirection(chart, xy, ra, dec + (dec > 89.0 ? -1.0 : 1.0),
          dec > 89.0 ? -1.0 : 1.0);
      double[] east = chartDirection(chart, xy,
          ra + 1.0 / FastMath.max(0.01, FastMath.cos(FastMath.toRadians(dec))), dec, 1.0);
      if (north == null || east == null) {
        return;
      }
      double ux = FastMath.cos(positionAngle) * north[0] + FastMath.sin(positionAngle) * east[0];
      double uy = FastMath.cos(positionAngle) * north[1] + FastMath.sin(positionAngle) * east[1];
      double norm = FastMath.hypot(ux, uy);
      ux /= norm;
      uy /= norm;
      double vx = -uy;
      double vy = ux;

      group.append(darkColor);
      group.append(F.binaryAST2(S.Disk, F.List(F.num(xy[0]), F.num(xy[1])), F.num(radius)));
      group.append(litColor);
      int steps = 24;
      IASTAppendable outline = F.ListAlloc(2 * steps + 2);
      double terminator = radius * (1.0 - 2.0 * fraction);
      for (int k = 0; k <= steps; k++) {
        // the bright limb, from one cusp to the other
        double t = -FastMath.PI / 2 + FastMath.PI * k / steps;
        double a = radius * FastMath.cos(t);
        double b = radius * FastMath.sin(t);
        outline.append(F.List(F.num(xy[0] + a * ux + b * vx), F.num(xy[1] + a * uy + b * vy)));
      }
      for (int k = steps; k >= 0; k--) {
        // and back along the terminator
        double t = -FastMath.PI / 2 + FastMath.PI * k / steps;
        double a = terminator * FastMath.cos(t);
        double b = radius * FastMath.sin(t);
        outline.append(F.List(F.num(xy[0] + a * ux + b * vx), F.num(xy[1] + a * uy + b * vy)));
      }
      group.append(F.unaryAST1(S.Polygon, outline));
    }

    /**
     * The unit vector on the chart from {@code xy} towards another sky position, or
     * <code>null</code> when that is not on the chart.
     */
    private static double[] chartDirection(Chart chart, double[] xy, double ra, double dec,
        double sign) {
      double[] other = chart.map(ra, dec);
      if (other == null) {
        return null;
      }
      double dx = (other[0] - xy[0]) * sign;
      double dy = (other[1] - xy[1]) * sign;
      double norm = FastMath.hypot(dx, dy);
      return norm == 0.0 ? null : new double[] {dx / norm, dy / norm};
    }

    /**
     * The radius to draw the Sun or the Moon with: its true angular radius, but never smaller than
     * a marker, which a whole-sky chart would otherwise shrink to a dot.
     */
    private double diskRadius(Chart chart, double ra, double dec, double trueRadiusDegrees) {
      return FastMath.max(1.2 * markerRadius(chart, ra, dec),
          angularRadiusOnChart(chart, ra, dec, trueRadiusDegrees));
    }

    /** An angular radius at a place on the chart, in chart units; zero off the chart. */
    private static double angularRadiusOnChart(Chart chart, double ra, double dec,
        double trueRadiusDegrees) {
      double otherDeclination = dec + (dec > 0.0 ? -trueRadiusDegrees : trueRadiusDegrees);
      double[] here = chart.map(ra, dec);
      double[] there = chart.map(ra, otherDeclination);
      double actual = here == null || there == null ? 0.0
          : FastMath.hypot(there[0] - here[0], there[1] - here[1]);
      return actual;
    }

    /**
     * How big to draw a star. Brightness is logarithmic, so the radius grows linearly with
     * magnitude rather than with flux, which is what makes a chart look right.
     */
    private static double pointSize(double magnitude, Chart chart) {
      // sized against what is actually drawn, so a chart cut short by the star cap still shows
      // its faintest stars as dots
      double faintest = chart.effectiveMagnitudeLimit;
      double brightness = Double.isNaN(magnitude) ? faintest : magnitude;
      double steps = FastMath.max(0.0, faintest - brightness);
      return starSize(steps);
    }

    /**
     * The diameter of a star {@code steps} magnitudes brighter than the faintest one drawn, as a
     * fraction of the chart width.
     *
     * <p>
     * The size is already a fraction of the chart width, so zooming in enlarges the spacing between
     * stars on its own. Scaling the dots up as well only fattens them until they merge and hide the
     * constellation figures underneath.
     */
    private static double starSize(double steps) {
      return FAINTEST_STAR + STAR_GROWTH * steps;
    }

    // ------------------------------------------------------- user content

    /**
     * Walk a user primitive tree, replacing sky positions with chart coordinates.
     *
     * <p>
     * A position is a <code>{rightAscension, declination}</code> pair of numbers or angle
     * quantities, or a named object - a string or an entity such as
     * <code>Entity("Star", "Betelgeuse")</code> - inside a <code>Point</code>, <code>Text</code> or
     * other primitive. A named object standing on its own, where a primitive is expected, is drawn
     * as a labelled marker. Everything else - styles, colours, heads - is passed through untouched.
     *
     * @param primitivePosition whether {@code expr} stands where a primitive is expected (at the top
     *        or in a list or a style wrapper) rather than inside a primitive, where it is a position
     */
    private IExpr projectUserPrimitives(IExpr expr, Chart chart, IAST ast, EvalEngine engine,
        boolean primitivePosition) {
      SkyCatalog.Constellation constellation = constellationOf(expr);
      if (constellation != null) {
        if (primitivePosition) {
          // drawn in the Constellations layer
          return F.List();
        }
        double[] xy =
            chart.map(constellation.labelRightAscension, constellation.labelDeclination);
        return xy == null ? F.List() : F.List(F.num(xy[0]), F.num(xy[1]));
      }
      if (objectName(expr) != null) {
        double[] direction = namedDirection(expr, chart);
        if (direction == null) {
          return Errors.printMessage(S.AstroGraphics, "astrobody", F.List(expr, ast), engine);
        }
        if (angularDistance(chart, direction[0], direction[1]) > chart.selectRange) {
          // outside the view: nothing to draw, which an empty list says in either position
          return F.List();
        }
        double[] xy = chart.map(direction[0], direction[1]);
        if (xy == null) {
          return F.List();
        }
        return primitivePosition ? objectMarker(expr, direction, xy, chart)
            : F.List(F.num(xy[0]), F.num(xy[1]));
      }
      if (!expr.isAST()) {
        return expr;
      }
      IAST list = (IAST) expr;
      // {rightAscension, declination}, or the {rightAscension, declination, distance} which
      // AstroPosition returns for a solar system body; the distance plays no part on a chart
      if (list.isList() && (list.argSize() == 2 || list.argSize() == 3 && isDistance(list.arg3()))
          && isAngle(list.arg1()) && isAngle(list.arg2())) {
        Double ra = AstroConvert.toRadians(list.arg1(), engine);
        Double dec = AstroConvert.toRadians(list.arg2(), engine);
        if (ra != null && dec != null) {
          double[] xy = chart.map(FastMath.toDegrees(ra), FastMath.toDegrees(dec));
          // a position off the visible hemisphere collapses to an empty list, which draws nothing
          return xy == null ? F.List() : F.List(F.num(xy[0]), F.num(xy[1]));
        }
      }
      // a list, and the first argument of a wrapper, still hold primitives; anything below a
      // primitive head is a position
      boolean container = list.isList();
      boolean wrapper = list.isAST(S.Style) || list.isAST(S.Tooltip) || list.isAST(S.Annotation);
      IASTAppendable result = F.ast(list.head(), list.argSize());
      for (int i = 1; i < list.size(); i++) {
        boolean childPosition = primitivePosition && (container || (wrapper && i == 1));
        IExpr projected = projectUserPrimitives(list.get(i), chart, ast, engine, childPosition);
        if (projected.isNIL()) {
          return F.NIL;
        }
        result.append(projected);
      }
      return result;
    }

    /**
     * A named object drawn on its own: a point, a ring around it and its name, in the current
     * colour. The ring is what makes it stand out among the stars; its size is a fixed angle, so it
     * looks the same at every zoom.
     */
    private IExpr objectMarker(IExpr expr, double[] direction, double[] xy, Chart chart) {
      double radius = 1.8 * markerRadius(chart, direction[0], direction[1]);
      IExpr center = F.List(F.num(xy[0]), F.num(xy[1]));
      String label = objectName(expr).toString();
      AstroBodies.Target target = AstroBodies.target(objectName(expr));
      if (target != null && target.isStar) {
        // "alpha Ori" is found, but the proper name reads better
        label = target.name;
      }
      // the object and its name in the current colour; the Wolfram Language draws no ring either
      return F.List(F.unaryAST1(S.Point, center), label(label, xy, NAME_FONT, true));
    }

    private static boolean isAngle(IExpr expr) {
      return expr.isReal() || expr.isAST(S.Quantity, 3);
    }

    /** Whether {@code expr} is a length quantity, the third entry of an AstroPosition result. */
    private static boolean isDistance(IExpr expr) {
      if (!expr.isAST(S.Quantity, 3) || !expr.second().isString()) {
        return false;
      }
      String unit = expr.second().toString();
      return unit.endsWith("Meters") || unit.startsWith("AstronomicalUnit")
          || unit.equals("LightYears") || unit.equals("Parsecs");
    }

    // ------------------------------------------------------------ output

    /** Wrap the primitives in a {@code Graphics} with a range that fits the projection. */
    /** The fraction of the extent the plot range is padded by on each side. */
    private static double resolvePadding(IExpr rangePadding, Chart chart) {
      if (rangePadding != null && rangePadding.isReal()) {
        return rangePadding.evalf();
      }
      if ((rangePadding == null || rangePadding == S.Automatic)
          && chart.viewRange == chart.range) {
        // a planisphere already has its margin: the sky below the horizon
        return 0.02;
      }
      return 0.0;
    }

    private IExpr buildGraphics(IAST primitives, Chart chart, IExpr rangePadding) {
      double[] bounds = chart.bounds;
      double padding = chart.padding;
      double padX = (bounds[1] - bounds[0]) * padding;
      double padY = (bounds[3] - bounds[2]) * padding;
      IASTAppendable graphics = F.ast(S.Graphics, 6);
      graphics.append(primitives);
      graphics.append(F.Rule(S.PlotRange, F.List(//
          F.List(F.num(bounds[0] - padX), F.num(bounds[1] + padX)), //
          F.List(F.num(bounds[2] - padY), F.num(bounds[3] + padY)))));
      graphics
          .append(F.Rule(S.AspectRatio, F.num((bounds[3] - bounds[2]) / (bounds[1] - bounds[0]))));
      graphics.append(F.Rule(S.Axes, S.False));
      // a zoomed chart reaches past its square; the square is what is shown
      graphics.append(F.Rule(S.PlotRangeClipping, S.True));
      graphics.append(
          F.Rule(S.Background, chart.groundBackground ? groundOverSky(chart) : skyColor(chart)));
      graphics.append(F.Rule(S.MetaInformation, metaInformation(chart)));
      return graphics;
    }

    /**
     * The colour the sky is painted in, which is also what a hole in the Milky Way is filled with.
     */
    private static IExpr skyColor(Chart chart) {
      if (chart.sky != null) {
        return chart.sky;
      }
      return chart.darkOnLight ? F.GrayLevel(1.0) : F.GrayLevel(0.0);
    }

    /** How the chart was made, so that it carries its own provenance. */
    private IExpr metaInformation(Chart chart) {
      IASTAppendable rules = F.ListAlloc(10);
      rules.append(F.Rule(F.stringx("ReferenceFrame"), F.stringx(chart.frame.name())));
      rules.append(F.Rule(F.stringx("Date"),
          chart.frame.date() == null ? S.None : AstroConvert.toDateObject(chart.frame.date())));
      rules.append(F.Rule(F.stringx("Location"),
          chart.frame.location() == null ? S.None
              : GeoPositionExpr.newInstance(
                  FastMath.toDegrees(chart.frame.location().getLatitude()),
                  FastMath.toDegrees(chart.frame.location().getLongitude()))));
      rules.append(F.Rule(F.stringx("Projection"), F.stringx(chart.projection.name())));
      rules.append(F.Rule(F.stringx("Center"),
          F.List(AstroConvert.degrees(FastMath.toRadians(chart.centerRightAscension)),
              AstroConvert.degrees(FastMath.toRadians(chart.centerDeclination)))));
      rules.append(
          F.Rule(F.stringx("Range"), AstroConvert.degrees(FastMath.toRadians(chart.range))));
      rules.append(F.Rule(F.stringx("MagnitudeLimit"), F.num(chart.magnitudeLimit)));
      rules.append(F.Rule(F.stringx("EffectiveMagnitudeLimit"),
          F.num(chart.effectiveMagnitudeLimit)));
      rules.append(F.Rule(F.stringx("StarCatalog"), F.stringx(chart.starCatalog)));
      if (chart.dated) {
        // where the Sun, the Moon and the planets came from
        rules.append(F.Rule(F.stringx("Ephemeris"),
            F.stringx(AstroFallback.wasUsed() ? "Meeus" : "JPL DE440")));
      }
      return F.assoc(rules);
    }

    /**
     * The extent the projection actually covers, found by sampling the visible sphere. Cheaper and
     * more robust than a closed form for each projection, and it automatically respects the range
     * and the hemisphere clipping.
     */
    private double[] projectionBounds(Chart chart) {
      double minX = Double.POSITIVE_INFINITY;
      double maxX = Double.NEGATIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;
      // Rings around the centre out to the edge of the view, rather than a fixed grid over the
      // sky: a grid of 2 degrees has no point at all inside a view a fraction of a degree across,
      // which then fell back to a plot range thousands of times too wide.
      double ra0 = FastMath.toRadians(chart.centerEquatorialRightAscension);
      double dec0 = FastMath.toRadians(chart.centerEquatorialDeclination);
      int rings = 45;
      for (int k = 0; k <= rings; k++) {
        double d = FastMath.toRadians(chart.viewRange * k / rings);
        for (int b = 0; b < 180; b++) {
          double bearing = FastMath.toRadians(2.0 * b);
          double sinDec = FastMath.sin(dec0) * FastMath.cos(d)
              + FastMath.cos(dec0) * FastMath.sin(d) * FastMath.cos(bearing);
          double dec = FastMath.asin(FastMath.max(-1.0, FastMath.min(1.0, sinDec)));
          double ra = ra0 + FastMath.atan2(FastMath.sin(bearing) * FastMath.sin(d) * FastMath.cos(dec0),
              FastMath.cos(d) - FastMath.sin(dec0) * sinDec);
          double[] xy = chart.map(
              SkyCatalog.normalizeRightAscensionDegrees(FastMath.toDegrees(ra)),
              FastMath.toDegrees(dec));
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

    /** Project a ring of sky positions, dropping the parts which are not visible. */
    private IAST projectRing(double[][] ring, Chart chart) {
      IASTAppendable points = F.ListAlloc(ring.length + 8);
      double previousLongitude = Double.NaN;
      for (double[] point : ring) {
        double rightAscension = SkyCatalog.normalizeRightAscensionDegrees(point[0]);
        // Break the ring where it crosses the seam. Projected coordinates give no warning that
        // this happened - a point at longitude 359 and the next at 1 land at opposite edges of a
        // whole sky chart, and joining them draws a line, or fills a band, straight across it.
        // The jump is only visible in the frame's own longitude, so the test has to be made there.
        double longitude = chart.frame.toFrame(rightAscension, point[1])[0];
        if (!Double.isNaN(previousLongitude) && crossesSeam(chart, previousLongitude, longitude)) {
          points.append(F.List());
        }
        previousLongitude = longitude;
        appendVisible(points, chart, rightAscension, point[1]);
      }
      return points;
    }

    /**
     * Whether the step from one frame longitude to the next crosses the chart's seam.
     *
     * <p>
     * The seam is the meridian opposite the centre of the projection, not longitude zero, so the
     * test has to be made relative to that centre: on a chart centred at longitude zero the tear is
     * at 180, and two points a fifth of a degree apart either side of it land at opposite edges of
     * the map. An azimuthal projection is wrapped around its centre and has no seam at all, so
     * nothing is cut there.
     */
    private static boolean crossesSeam(Chart chart, double longitudeA, double longitudeB) {
      if (!chart.projection.hasSeam()) {
        return false;
      }
      return FastMath
          .abs(relativeLongitude(chart, longitudeA) - relativeLongitude(chart, longitudeB)) > 180.0;
    }

    /** A frame longitude in degrees, measured from the chart centre and wrapped to (-180, 180]. */
    private static double relativeLongitude(Chart chart, double longitude) {
      double delta = (longitude - chart.centerRightAscension) % 360.0;
      if (delta > 180.0) {
        delta -= 360.0;
      } else if (delta <= -180.0) {
        delta += 360.0;
      }
      return delta;
    }

    /**
     * Append a projected point, or a marker for a gap.
     *
     * <p>
     * An empty list marks a break, which {@link #appendLineRuns} then splits the polyline on. That
     * keeps a line which leaves the visible hemisphere, or wraps across the seam, from being drawn
     * straight across the chart.
     */
    private void appendVisible(IASTAppendable points, Chart chart, double rightAscension,
        double declination) {
      if (angularDistance(chart, rightAscension, declination) > chart.selectRange) {
        points.append(F.List());
        return;
      }
      double[] xy = chart.map(rightAscension, declination);
      points.append(xy == null ? F.List() : F.List(F.num(xy[0]), F.num(xy[1])));
    }

    /** As {@link #appendVisible} but for a position already in the chart's frame. */
    private void appendVisibleInFrame(IASTAppendable points, Chart chart, double longitude,
        double latitude) {
      double[] equatorial = chart.frame.fromFrame(longitude, latitude);
      appendVisible(points, chart, equatorial[0], equatorial[1]);
    }

    /** Split a point list on its gap markers and append each run as a {@code Line}. */
    private void appendLineRuns(IASTAppendable group, IAST points) {
      IASTAppendable run = F.ListAlloc(points.argSize());
      for (int i = 1; i < points.size(); i++) {
        IExpr point = points.get(i);
        if (point.isList() && ((IAST) point).argSize() == 2) {
          run.append(point);
        } else {
          if (run.argSize() >= 2) {
            group.append(F.unaryAST1(S.Line, run));
          }
          run = F.ListAlloc(points.argSize());
        }
      }
      if (run.argSize() >= 2) {
        group.append(F.unaryAST1(S.Line, run));
      }
    }

    /** Angular distance from the centre of the chart, in degrees. */
    private static double angularDistance(Chart chart, double rightAscension, double declination) {
      double ra1 = FastMath.toRadians(chart.centerEquatorialRightAscension);
      double dec1 = FastMath.toRadians(chart.centerEquatorialDeclination);
      double ra2 = FastMath.toRadians(rightAscension);
      double dec2 = FastMath.toRadians(declination);
      double cos = FastMath.sin(dec1) * FastMath.sin(dec2)
          + FastMath.cos(dec1) * FastMath.cos(dec2) * FastMath.cos(ra2 - ra1);
      return FastMath.toDegrees(FastMath.acos(FastMath.max(-1.0, FastMath.min(1.0, cos))));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.AstroBackground, S.AstroCenter, S.AstroGridLines,
              S.AstroGridLinesStyle, S.AstroProjection, S.AstroRange, S.AstroRangePadding,
              S.AstroReferenceFrame, S.AstroZoomLevel, S.AstroStyling}, //
          new IExpr[] {S.Automatic, S.Automatic, S.Automatic, S.Automatic, S.Automatic,
              S.Automatic,
              S.Automatic, S.Automatic, S.Automatic, S.Automatic});
    }

    @Override
    public int status() {
      // no survey imagery, and AstroStyling is accepted but has only the one style
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private AstroGraphicsFunctions() {}
}
