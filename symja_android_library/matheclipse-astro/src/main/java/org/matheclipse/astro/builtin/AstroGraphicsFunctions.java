package org.matheclipse.astro.builtin;

import java.util.ArrayList;
import java.util.List;
import org.hipparchus.util.FastMath;
import org.matheclipse.astro.convert.AstroBodies;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.data.AstroDataContext;
import org.matheclipse.astro.project.MapProjection;
import org.matheclipse.astro.sky.SkyCatalog;
import org.matheclipse.astro.sky.SkyFrame;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
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
import org.orekit.bodies.GeodeticPoint;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;

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

  /** Point size of a magnitude 0 star, as a fraction of the chart width. */
  private static final double BASE_POINT_SIZE = 0.011;

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

    boolean wholeSky;

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
      Chart chart = new Chart();

      // ---- the frame everything else is expressed in, so it is resolved first
      chart.frame = resolveFrame(options[7], engine);
      if (chart.frame == null) {
        return Errors.printMessage(S.AstroGraphics, "astroframe",
            F.List(S.AstroReferenceFrame, options[7] == null ? S.Automatic : options[7]), engine);
      }
      boolean horizon = chart.frame.isHorizon();

      // ---- centre. Looking up from the ground the natural centre is the zenith.
      double[] center =
          horizon && (options[1] == null || options[1] == S.Automatic) ? new double[] {0.0, 90.0}
              : resolveCenter(options[1], chart, engine);
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

      // ---- projection
      String projectionName = AstroConvert.optionString(options[4],
          horizon ? "LambertAzimuthal" : chart.wholeSky ? "Mollweide" : "Stereographic");
      chart.projection =
          MapProjection.of(projectionName, FastMath.toRadians(chart.centerRightAscension),
              FastMath.toRadians(chart.centerDeclination));
      if (chart.projection == null) {
        return Errors.printMessage(S.AstroGraphics, "astroproj",
            F.List(F.stringx(projectionName), ast), engine);
      }

      // ---- how faint to go
      chart.magnitudeLimit = resolveMagnitudeLimit(options[8], chart.range);

      try {
        IASTAppendable primitives = F.ListAlloc(16);
        appendBackground(primitives, chart, options[0]);
        appendMainPlanes(primitives, chart);
        appendGridLines(primitives, chart, options[2], options[3]);
        appendConstellations(primitives, chart);
        appendDeepSkyObjects(primitives, chart);
        appendStars(primitives, chart);
        if (argSize >= 1) {
          IExpr projected = projectUserPrimitives(ast.arg1(), chart, ast, engine);
          if (projected.isNIL()) {
            return F.NIL;
          }
          primitives.append(projected);
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
    private SkyFrame resolveFrame(IExpr referenceFrame, EvalEngine engine) {
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
      if (date == null) {
        date = AstroDataContext.isAvailable() ? AstroConvert.nowUTC() : null;
      }
      if (name == null) {
        return date == null ? SkyFrame.equatorial(null) : SkyFrame.equatorial(date);
      }
      if (date == null) {
        // every named frame needs an instant to be evaluated at
        return null;
      }
      return SkyFrame.of(name, date, location);
    }

    /**
     * The observer to fall back on, from <code>$GeoLocation</code>.
     *
     * @return the location, or <code>null</code> when the variable is unset
     */
    private GeodeticPoint defaultLocation(EvalEngine engine) {
      IExpr value = S.$GeoLocation.assignedValue();
      if (value == null) {
        return null;
      }
      return AstroConvert.toGeodeticPoint(engine.evaluate(value));
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
      if (center.isString()) {
        AstroBodies.Target target = AstroBodies.target(center);
        if (target == null) {
          return null;
        }
        if (!AstroDataContext.isAvailable()) {
          return null;
        }
        AbsoluteDate when = chart.frame.date() == null ? AstroConvert.nowUTC() : chart.frame.date();
        org.hipparchus.geometry.euclidean.threed.Vector3D position =
            target.provider.getPosition(when, FramesFactory.getGCRF());
        return chart.frame.toFrame(
            SkyCatalog.normalizeRightAscensionDegrees(FastMath.toDegrees(position.getAlpha())),
            FastMath.toDegrees(position.getDelta()));
      }
      return null;
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
        // a whole-sky or wide chart cannot show anything fainter legibly
        return 6.0;
      }
      return range >= 20.0 ? 7.0 : 8.5;
    }

    // --------------------------------------------------------- primitives

    /**
     * The sky ground: the Milky Way band, or a flat fill, behind everything else.
     *
     * <p>
     * The documented styles are <code>"GalacticSky"</code>, <code>"BlackSky"</code> and
     * <code>"WhiteSky"</code>. {@link S#Automatic} keeps the Milky Way.
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
      String name = style == null || style == S.Automatic ? "GalacticSky"
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
        // a colour, or anything else: use it as the ground and draw no band
        milkyWay = false;
      }
      if (!milkyWay) {
        return;
      }
      IASTAppendable group = F.ListAlloc(16);
      for (List<double[][]> polygon : SkyCatalog.get().milkyWayPolygons()) {
        for (int ring = 0; ring < polygon.size(); ring++) {
          // The outline is the band, the rings after it are holes punched in it. There is no
          // compound path here to punch one with, but the sky behind is a flat colour, so painting
          // a hole in that colour removes it exactly. Draw in file order so a hole lands on top of
          // the outline it belongs to.
          group.append(ring == 0 ? F.GrayLevel(0.22) : groundColor(chart));
          // one polygon per run: the point list carries gap markers where a ring leaves the view
          // or crosses the seam, and feeding those to Polygon as if they were vertices smears the
          // band across the chart
          appendPolygonRuns(group, projectRing(polygon.get(ring), chart));
        }
      }
      primitives.append(layer(group, "AstroBackground"));
    }

    /**
     * The reference great circles: the celestial equator, the ecliptic and the galactic equator.
     *
     * <p>
     * Each is a circle of zero latitude in its own frame, so it is generated there and pushed
     * through the chart's frame like everything else - which is what makes it curve correctly
     * whichever frame the chart is drawn in.
     */
    private void appendMainPlanes(IASTAppendable primitives, Chart chart) {
      IASTAppendable group = F.ListAlloc(8);
      group.append(F.RGBColor(0.45, 0.45, 0.30));
      group.append(F.Thickness(0.0016));
      appendPlane(group, chart, "Equatorial");
      appendPlane(group, chart, "Ecliptic");
      appendPlane(group, chart, "Galactic");
      if (chart.frame.isHorizon()) {
        // the horizon itself is worth a line when you are standing under it
        IASTAppendable line = F.ListAlloc(181);
        for (double azimuth = 0.0; azimuth <= 360.0; azimuth += 2.0) {
          appendVisibleInFrame(line, chart, azimuth, 0.0);
        }
        appendLineRuns(group, line);
      }
      primitives.append(layer(group, "MainPlanes"));
    }

    /** One great circle, generated as the zero latitude line of {@code planeFrame}. */
    private void appendPlane(IASTAppendable group, Chart chart, String planeFrame) {
      SkyFrame plane = SkyFrame.of(planeFrame, chart.frame.date(), null);
      if (plane == null) {
        return;
      }
      IASTAppendable line = F.ListAlloc(181);
      for (double longitude = 0.0; longitude <= 360.0; longitude += 2.0) {
        // walk the circle in the plane's own frame, then convert back to catalogue coordinates
        double[] equatorial = plane.fromFrame(longitude, 0.0);
        appendVisible(line, chart, equatorial[0], equatorial[1]);
      }
      appendLineRuns(group, line);
    }

    /** The constellation figures, their IAU boundaries and their names. */
    private void appendConstellations(IASTAppendable primitives, Chart chart) {
      IASTAppendable group = F.ListAlloc(16);

      group.append(F.GrayLevel(0.34));
      group.append(F.Thickness(0.0008));
      for (double[][] ring : SkyCatalog.get().constellationBoundaries()) {
        appendLineRuns(group, projectRing(ring, chart));
      }

      group.append(F.RGBColor(0.35, 0.55, 0.85));
      group.append(F.Thickness(0.0015));
      for (double[][] ring : SkyCatalog.get().constellationLines()) {
        appendLineRuns(group, projectRing(ring, chart));
      }

      // names only where there is room for them, which is what the catalogue's rank records
      group.append(chart.darkOnLight ? F.GrayLevel(0.35) : F.GrayLevel(0.62));
      for (SkyCatalog.Constellation constellation : SkyCatalog.get().constellations()) {
        if (constellation.rank > (chart.wholeSky ? 1 : 3)) {
          continue;
        }
        if (angularDistance(chart, constellation.labelRightAscension,
            constellation.labelDeclination) > chart.range) {
          continue;
        }
        double[] xy = chart.map(constellation.labelRightAscension, constellation.labelDeclination);
        if (xy != null) {
          group.append(F.Text(F.stringx(constellation.name), F.List(F.num(xy[0]), F.num(xy[1]))));
        }
      }
      primitives.append(layer(group, "Constellations"));
    }

    /**
     * The Messier and NGC objects bright enough for the chart, as small open circles.
     *
     * <p>
     * Drawn before the stars so that a star sitting inside a cluster stays on top of it, and drawn
     * as circles rather than points so that an extended object does not read as one more star.
     */
    private void appendDeepSkyObjects(IASTAppendable primitives, Chart chart) {
      List<SkyCatalog.DeepSkyObject> visible = new ArrayList<SkyCatalog.DeepSkyObject>();
      for (SkyCatalog.DeepSkyObject object : SkyCatalog.get()
          .deepSkyObjects(chart.magnitudeLimit)) {
        if (angularDistance(chart, object.rightAscension, object.declination) > chart.range) {
          continue;
        }
        visible.add(object);
      }
      IASTAppendable group = F.ListAlloc(visible.size() * 2 + 3);
      group.append(chart.darkOnLight ? F.RGBColor(0.15, 0.45, 0.40) : F.RGBColor(0.45, 0.78, 0.70));
      group.append(F.Thickness(0.0012));
      for (SkyCatalog.DeepSkyObject object : visible) {
        double[] xy = chart.map(object.rightAscension, object.declination);
        if (xy == null) {
          continue;
        }
        double radius = markerRadius(chart, object.rightAscension, object.declination);
        group.append(F.binaryAST2(S.Circle, F.List(F.num(xy[0]), F.num(xy[1])), F.num(radius)));
        // Messier objects only, and only once the view is narrow enough to have room. Labelling
        // every object turns a field like Orion - eighteen of them inside twelve degrees - into a
        // pile of overlapping text, and a ninety degree planisphere has no room even for the
        // Messier numbers. Offset so that a label sits beside its marker rather than on top of it.
        if (chart.range <= 45.0 && object.designation.startsWith("M ")) {
          group.append(F.Text(F.stringx(object.designation),
              F.List(F.num(xy[0] + radius * 1.8), F.num(xy[1]))));
        }
      }
      primitives.append(layer(group, "DeepSkyObjects"));
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
      if (gridLines == S.None || gridLines == null) {
        return;
      }
      int count = gridLines.isReal() ? gridLines.toIntDefault() : 12;
      if (count <= 0) {
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
      // and Config.MAX_AST_SIZE rightly refuses that.
      List<SkyCatalog.Star> visible = new ArrayList<SkyCatalog.Star>();
      for (SkyCatalog.Star star : SkyCatalog.get().starsToMagnitude(chart.magnitudeLimit)) {
        // outside the view there is nothing to draw, and on a zoomed chart that is almost all of
        // them - without this test the whole catalogue lands off-canvas and the SVG balloons
        if (angularDistance(chart, star.rightAscension, star.declination) > chart.range) {
          continue;
        }
        visible.add(star);
      }
      IASTAppendable group = F.ListAlloc(visible.size() * 2 + 2);
      group.append(chart.darkOnLight ? F.GrayLevel(0.1) : F.GrayLevel(1.0));
      for (SkyCatalog.Star star : visible) {
        double[] xy = chart.map(star.rightAscension, star.declination);
        if (xy == null) {
          continue;
        }
        group.append(F.PointSize(pointSize(star.magnitude, chart)));
        group.append(F.unaryAST1(S.Point, F.List(F.num(xy[0]), F.num(xy[1]))));
      }
      primitives.append(layer(group, "AstroStars"));
    }

    /**
     * How big to draw a star. Brightness is logarithmic, so the radius grows linearly with
     * magnitude rather than with flux, which is what makes a chart look right.
     */
    private static double pointSize(double magnitude, Chart chart) {
      double faintest = chart.magnitudeLimit;
      double brightness = Double.isNaN(magnitude) ? faintest : magnitude;
      double steps = FastMath.max(0.0, faintest - brightness);
      // The size is already a fraction of the chart width, so zooming in enlarges the spacing
      // between stars on its own. Scaling the dots up as well only fattens them until they merge
      // and hide the constellation figures underneath.
      return BASE_POINT_SIZE * (0.22 + 0.14 * steps);
    }

    // ------------------------------------------------------- user content

    /**
     * Walk a user primitive tree, replacing sky positions with chart coordinates.
     *
     * <p>
     * A position is a <code>{rightAscension, declination}</code> pair of numbers or angle
     * quantities, or the name of a body or star inside a <code>Point</code> or <code>Text</code>.
     * Everything else - styles, colours, heads - is passed through untouched.
     */
    private IExpr projectUserPrimitives(IExpr expr, Chart chart, IAST ast, EvalEngine engine) {
      if (expr.isString()) {
        double[] position = resolveNamedPosition(expr, chart, engine);
        if (position == null) {
          return Errors.printMessage(S.AstroGraphics, "astrobody", F.List(expr, ast), engine);
        }
        return F.List(F.num(position[0]), F.num(position[1]));
      }
      if (!expr.isAST()) {
        return expr;
      }
      IAST list = (IAST) expr;
      if (list.isList() && list.argSize() == 2 && isAngle(list.arg1()) && isAngle(list.arg2())) {
        Double ra = AstroConvert.toRadians(list.arg1(), engine);
        Double dec = AstroConvert.toRadians(list.arg2(), engine);
        if (ra != null && dec != null) {
          double[] xy = chart.map(FastMath.toDegrees(ra), FastMath.toDegrees(dec));
          // a position off the visible hemisphere collapses to an empty list, which draws nothing
          return xy == null ? F.List() : F.List(F.num(xy[0]), F.num(xy[1]));
        }
      }
      IASTAppendable result = F.ast(list.head(), list.argSize());
      for (int i = 1; i < list.size(); i++) {
        IExpr projected = projectUserPrimitives(list.get(i), chart, ast, engine);
        if (projected.isNIL()) {
          return F.NIL;
        }
        result.append(projected);
      }
      return result;
    }

    /** Chart coordinates of a named body or star. */
    private double[] resolveNamedPosition(IExpr name, Chart chart, EvalEngine engine) {
      AstroBodies.Target target = AstroBodies.target(name);
      if (target == null || !AstroDataContext.isAvailable()) {
        return null;
      }
      org.hipparchus.geometry.euclidean.threed.Vector3D position =
          target.provider.getPosition(AstroConvert.nowUTC(), FramesFactory.getGCRF());
      double[] xy = chart.map(
          SkyCatalog.normalizeRightAscensionDegrees(FastMath.toDegrees(position.getAlpha())),
          FastMath.toDegrees(position.getDelta()));
      return xy;
    }

    private static boolean isAngle(IExpr expr) {
      return expr.isReal() || expr.isAST(S.Quantity, 3);
    }

    // ------------------------------------------------------------ output

    /** Wrap the primitives in a {@code Graphics} with a range that fits the projection. */
    private IExpr buildGraphics(IAST primitives, Chart chart, IExpr rangePadding) {
      double[] bounds = projectionBounds(chart);
      double padding = 0.0;
      if (rangePadding != null && rangePadding.isReal()) {
        padding = rangePadding.evalf();
      } else if (rangePadding == null || rangePadding == S.Automatic) {
        padding = 0.02;
      }
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
      graphics.append(F.Rule(S.Background, groundColor(chart)));
      graphics.append(F.Rule(S.MetaInformation, metaInformation(chart)));
      return graphics;
    }

    /**
     * The colour the sky is painted in, which is also what a hole in the Milky Way is filled with.
     */
    private static IExpr groundColor(Chart chart) {
      return chart.darkOnLight ? F.GrayLevel(1.0) : F.GrayLevel(0.06);
    }

    /** How the chart was made, so that it carries its own provenance. */
    private IExpr metaInformation(Chart chart) {
      IASTAppendable rules = F.ListAlloc(7);
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
      for (double ra = 0.0; ra <= 360.0; ra += 2.0) {
        for (double dec = -90.0; dec <= 90.0; dec += 2.0) {
          if (angularDistance(chart, ra, dec) > chart.range) {
            continue;
          }
          double[] xy = chart.map(ra, dec);
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

    /** Split a point list on its gap markers and append each run as a {@code Polygon}. */
    private void appendPolygonRuns(IASTAppendable group, IAST points) {
      IASTAppendable run = F.ListAlloc(points.argSize());
      for (int i = 1; i < points.size(); i++) {
        IExpr point = points.get(i);
        if (point.isList() && ((IAST) point).argSize() == 2) {
          run.append(point);
        } else {
          if (run.argSize() >= 3) {
            group.append(F.unaryAST1(S.Polygon, run));
          }
          run = F.ListAlloc(points.argSize());
        }
      }
      if (run.argSize() >= 3) {
        group.append(F.unaryAST1(S.Polygon, run));
      }
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
      if (angularDistance(chart, rightAscension, declination) > chart.range) {
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
          new IExpr[] {S.Automatic, S.Automatic, F.ZZ(12), S.Automatic, S.Automatic, S.Automatic,
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
