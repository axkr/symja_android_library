package org.matheclipse.astro.builtin;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.matheclipse.astro.convert.AstroBodies;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.convert.ReferenceAltitudes;
import org.matheclipse.astro.data.AstroDataContext;
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
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.models.earth.EarthStandardAtmosphereRefraction;
import org.orekit.time.AbsoluteDate;

/**
 * The day/night division of the Earth: the terminator line and the two hemispheres it separates.
 *
 * <p>
 * The results are ordinary graphics primitives over <code>GeoPosition</code> points -
 * <code>Line</code> for the terminator and <code>Polygon</code> for the hemispheres - so that they
 * can be drawn by whatever renders geo primitives.
 *
 * <p>
 * The locus where the Sun stands at a given altitude is a small circle on a spherical Earth and
 * only approximately one on the ellipsoid; these functions use the spherical form, which is the
 * usual convention for a terminator.
 */
public class AstroGeoFunctions {

  /** Shared refraction model, matching the one {@code AstroObserver} uses for rise and set. */
  private static final EarthStandardAtmosphereRefraction REFRACTION =
      new EarthStandardAtmosphereRefraction();

  /** Number of points used to draw the terminator; one per degree of longitude. */
  private static final int RING_POINTS = 361;

  /**
   * How far inside the antimeridian the curve starts and ends, in degrees.
   *
   * <p>
   * <code>GeoPosition</code> canonicalises longitude into <code>(-180, 180]</code>, so an endpoint
   * written as exactly <code>-180</code> comes back as <code>+180</code> and the sweep is no longer
   * monotonic. Starting a whisker inside - about a tenth of a millimetre on the ground - keeps the
   * sign and costs nothing visually.
   */
  private static final double EDGE_INSET = 1.0e-6;

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.DayNightTerminator.setEvaluator(new DayNightTerminator());
      S.DayHemisphere.setEvaluator(new DayHemisphere());
      S.NightHemisphere.setEvaluator(new NightHemisphere());
    }
  }

  /**
   * The geographic point which has the Sun directly overhead at {@code date}.
   */
  static GeodeticPoint subsolarPoint(AbsoluteDate date) {
    Frame frame = AstroConvert.earthFrame();
    Vector3D position = CelestialBodyFactory.getSun().getPosition(date, frame);
    return AstroConvert.earthEllipsoid().transform(position, frame, date);
  }

  /**
   * The terminator, sampled as a function of longitude.
   *
   * <p>
   * Walking the boundary by bearing would be the obvious thing, but it produces a ring whose
   * longitudes wrap, and such a ring cannot be filled: the day and night regions each contain a
   * pole, and a cap containing a pole does not project to a simple closed polygon in a cylindrical
   * projection. Solving for the latitude at each longitude instead gives a curve that runs
   * monotonically from one edge of the map to the other, which {@link #close} can then close along
   * the pole edge to make a fillable polygon.
   *
   * <p>
   * At the given longitude the boundary satisfies
   * <code>sin(latC) sin(lat) + cos(latC) cos(lat) cos(lon - lonC) = cos(radius)</code>, which is
   * <code>R sin(lat + phi) = cos(radius)</code> and so solvable directly.
   *
   * @param centerLatitude latitude of the centre of the lit cap, in radians
   * @param centerLongitude longitude of the centre of the lit cap, in radians
   * @param angularRadius angular distance from that centre to the terminator, in radians
   */
  static IASTAppendable terminator(double centerLatitude, double centerLongitude,
      double angularRadius) {
    IASTAppendable points = F.ListAlloc(RING_POINTS);
    double cosRadius = FastMath.cos(angularRadius);
    double sinCenter = FastMath.sin(centerLatitude);
    double cosCenter = FastMath.cos(centerLatitude);
    for (int i = 0; i < RING_POINTS; i++) {
      double edge = FastMath.PI - FastMath.toRadians(EDGE_INSET);
      double longitude = -edge + 2.0 * edge * i / (RING_POINTS - 1);
      double b = cosCenter * FastMath.cos(longitude - centerLongitude);
      double r = FastMath.hypot(sinCenter, b);
      if (r < FastMath.abs(cosRadius)) {
        // no crossing at this meridian; happens only in the degenerate equinox case where the
        // terminator runs along a meridian and latitude is not a function of longitude
        continue;
      }
      double latitude = FastMath.asin(cosRadius / r) - FastMath.atan2(b, sinCenter);
      // the solution can come back on the far branch, half a turn away
      if (latitude > FastMath.PI / 2.0) {
        latitude = FastMath.PI - latitude;
      } else if (latitude < -FastMath.PI / 2.0) {
        latitude = -FastMath.PI - latitude;
      }
      points.append(
          GeoPositionExpr.newInstance(FastMath.toDegrees(latitude), FastMath.toDegrees(longitude)));
    }
    return points;
  }

  /**
   * Close a terminator curve into a fillable polygon by running along the edge of the map to the
   * given pole and back.
   *
   * <p>
   * This is what makes the fill correct. The curve alone is open, and filling it directly joins its
   * two ends straight across the map; carrying it up to the pole instead encloses exactly the cap
   * on that side.
   *
   * @param poleLatitude <code>90</code> or <code>-90</code>, the pole inside the region to fill
   */
  static IAST close(IASTAppendable curve, double poleLatitude) {
    curve.append(GeoPositionExpr.newInstance(poleLatitude, 180.0 - EDGE_INSET));
    curve.append(GeoPositionExpr.newInstance(poleLatitude, -180.0 + EDGE_INSET));
    return curve;
  }

  /**
   * Which pole the lit cap contains: <code>90</code> for the north, <code>-90</code> for the south.
   *
   * <p>
   * The lit cap is a little over a hemisphere, so it always swallows one pole or the other - the
   * one in the summer hemisphere - and the dark cap takes the other.
   */
  static double litPole(double centerLatitude, double angularRadius) {
    double toNorthPole = FastMath.PI / 2.0 - centerLatitude;
    return toNorthPole < angularRadius ? 90.0 : -90.0;
  }

  /**
   * Shared argument handling: a single optional date and the <code>ReferenceAltitude</code> option.
   */
  private abstract static class AbstractTerminator extends AbstractFunctionOptionEvaluator {

    protected abstract IBuiltInSymbol symbol();

    /**
     * Build the result from the subsolar point and the angular radius of the day side.
     *
     * @param dayRadius angular distance from the subsolar point to the terminator, in radians
     */
    protected abstract IExpr result(GeodeticPoint subsolar, double dayRadius);

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      if (!AstroDataContext.checkAvailable(symbol(), engine)) {
        return F.NIL;
      }
      AbsoluteDate date;
      if (argSize >= 1) {
        date = AstroConvert.toAbsoluteDate(ast.arg1());
        if (date == null) {
          return AstroConvert.reportUnreadableArgument(symbol(), ast.arg1(), ast, engine);
        }
      } else {
        date = AstroConvert.nowUTC();
      }
      ReferenceAltitudes reference = ReferenceAltitudes.of(options[0], engine);
      if (reference == null) {
        return Errors.printMessage(symbol(), "astrorefalt", F.List(options[0], ast), engine);
      }
      try {
        GeodeticPoint subsolar = subsolarPoint(date);
        // the Sun's altitude at a point is 90 degrees minus the angular distance to the subsolar
        // point, so the terminator sits that far from it
        double apparentRadius = solarApparentRadius(date);
        double elevation = reference.centerElevation(apparentRadius);
        if (reference.isRefracted()) {
          // the reference altitude is an apparent one, but the angular distance is a geometric
          // quantity, so the refraction which lifts the Sun has to be taken back out. Without
          // this the terminator would not agree with Sunrise and Sunset.
          elevation -= REFRACTION.getRefraction(elevation);
        }
        double dayRadius = FastMath.PI / 2.0 - elevation;
        return result(subsolar, dayRadius);
      } catch (OrekitException oex) {
        return Errors.printMessage(symbol(), "orekitdata", F.List(F.stringx(oex.getMessage())),
            engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, S.ReferenceAltitude, S.Automatic);
    }

    @Override
    public int status() {
      // the boundary is a small circle on the sphere rather than on the reference ellipsoid, and
      // only the Earth is supported as the central body
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /** The angular radius of the solar disk seen from the Earth, in radians. */
  private static double solarApparentRadius(AbsoluteDate date) {
    Frame frame = AstroConvert.earthFrame();
    double distance = CelestialBodyFactory.getSun().getPosition(date, frame).getNorm();
    double radius = AstroBodies.meanRadius(CelestialBodyFactory.SUN);
    return distance <= radius ? 0.0 : FastMath.asin(radius / distance);
  }

  /** <code>DayNightTerminator(date)</code> - the boundary line as a <code>Line</code>. */
  private static final class DayNightTerminator extends AbstractTerminator {

    @Override
    protected IBuiltInSymbol symbol() {
      return S.DayNightTerminator;
    }

    @Override
    protected IExpr result(GeodeticPoint subsolar, double dayRadius) {
      return F.Line(terminator(subsolar.getLatitude(), subsolar.getLongitude(), dayRadius));
    }
  }

  /** <code>DayHemisphere(date)</code> - the lit side as a <code>Polygon</code>. */
  private static final class DayHemisphere extends AbstractTerminator {

    @Override
    protected IBuiltInSymbol symbol() {
      return S.DayHemisphere;
    }

    @Override
    protected IExpr result(GeodeticPoint subsolar, double dayRadius) {
      return F.unaryAST1(S.Polygon,
          close(terminator(subsolar.getLatitude(), subsolar.getLongitude(), dayRadius),
              litPole(subsolar.getLatitude(), dayRadius)));
    }
  }

  /** <code>NightHemisphere(date)</code> - the dark side as a <code>Polygon</code>. */
  private static final class NightHemisphere extends AbstractTerminator {

    @Override
    protected IBuiltInSymbol symbol() {
      return S.NightHemisphere;
    }

    @Override
    protected IExpr result(GeodeticPoint subsolar, double dayRadius) {
      // the same curve, closed towards the other pole: night is whatever the lit cap is not
      return F.unaryAST1(S.Polygon,
          close(terminator(subsolar.getLatitude(), subsolar.getLongitude(), dayRadius),
              -litPole(subsolar.getLatitude(), dayRadius)));
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private AstroGeoFunctions() {}
}
