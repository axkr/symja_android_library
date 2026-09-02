package org.matheclipse.astro.builtin;

import java.util.Locale;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.matheclipse.astro.convert.AstroBodies;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.convert.AstroObserver;
import org.matheclipse.astro.data.AstroDataContext;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GeoPositionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.orekit.bodies.CelestialBody;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.LoxodromeArc;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.TrackingCoordinates;

/**
 * The position of a solar system body as seen from a location on Earth, plus the distance
 * measurement on the reference ellipsoid.
 */
public class AstroPositionFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.SunPosition.setEvaluator(new SunPosition());
      S.MoonPosition.setEvaluator(new MoonPosition());
      S.GeoDistance.setEvaluator(new GeoDistance());
      S.AstroPosition.setEvaluator(new AstroPosition());
      S.AstroDistance.setEvaluator(new AstroDistance());
      S.AstroAngularSeparation.setEvaluator(new AstroAngularSeparation());
      S.AstroSubpoint.setEvaluator(new AstroSubpoint());
    }
  }

  /**
   * The horizontal or equatorial coordinates of a celestial body, shared by {@link S#SunPosition}
   * and {@link S#MoonPosition}.
   *
   * <p>
   * The argument forms are <code>f()</code>, <code>f(location)</code>, <code>f(date)</code> and
   * <code>f(location, date)</code>.
   */
  private abstract static class AbstractBodyPosition extends AbstractFunctionOptionEvaluator {

    /** The Orekit name of the body, one of the {@link CelestialBodyFactory} constants. */
    protected abstract String bodyName();

    /** The symbol this evaluator is registered for, used for error messages. */
    protected abstract IBuiltInSymbol symbol();

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      if (!AstroDataContext.checkAvailable(symbol(), engine)) {
        return F.NIL;
      }

      GeodeticPoint point = null;
      AbsoluteDate date = null;
      for (int i = 1; i <= argSize; i++) {
        IExpr arg = ast.get(i);
        GeodeticPoint argPoint = AstroConvert.toGeodeticPoint(arg);
        if (argPoint != null) {
          point = argPoint;
          continue;
        }
        AbsoluteDate argDate = AstroConvert.toAbsoluteDate(arg);
        if (argDate != null) {
          date = argDate;
          continue;
        }
        return AstroConvert.reportUnreadableArgument(symbol(), arg, ast, engine);
      }

      try {
        if (date == null) {
          date = AstroConvert.nowUTC();
        }
        Frame frame = AstroConvert.earthFrame();
        CelestialBody body = CelestialBodyFactory.getBody(bodyName());
        Vector3D position = body.getPosition(date, frame);

        String system = AstroConvert.optionString(options[0], "Horizon");
        if ("Horizon".equalsIgnoreCase(system)) {
          if (point == null) {
            // `1` called without a location; a GeoPosition argument or $GeoLocation is required.
            return Errors.printMessage(symbol(), "argillegal", F.List(ast, ast), engine);
          }
          TopocentricFrame topocentric = AstroConvert.toTopocentricFrame(point);
          TrackingCoordinates tracking = topocentric.getTrackingCoordinates(position, frame, date);
          return F.List(AstroConvert.degreesPositive(tracking.getAzimuth()),
              AstroConvert.degrees(tracking.getElevation()));
        }
        if ("Equatorial".equalsIgnoreCase(system)) {
          Vector3D equatorial = body.getPosition(date, FramesFactory.getGCRF());
          return F.List(AstroConvert.degreesPositive(equatorial.getAlpha()),
              AstroConvert.degrees(equatorial.getDelta()));
        }
        // `1` is not a supported celestial system in `2`.
        return Errors.printMessage(symbol(), "astrocsys", F.List(options[0], ast), engine);
      } catch (OrekitException oex) {
        return Errors.printMessage(symbol(), "orekitdata", F.List(F.stringx(oex.getMessage())),
            engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.CelestialSystem, S.AltitudeMethod}, //
          new IExpr[] {F.stringx("Horizon"), F.stringx("ApparentAltitude")});
    }

    @Override
    public int status() {
      // "Horizon" and "Equatorial" are supported, the remaining reference frames are not
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  private static final class SunPosition extends AbstractBodyPosition {

    @Override
    protected String bodyName() {
      return CelestialBodyFactory.SUN;
    }

    @Override
    protected IBuiltInSymbol symbol() {
      return S.SunPosition;
    }

  }

  private static final class MoonPosition extends AbstractBodyPosition {

    @Override
    protected String bodyName() {
      return CelestialBodyFactory.MOON;
    }

    @Override
    protected IBuiltInSymbol symbol() {
      return S.MoonPosition;
    }

  }

  /**
   * <code>GeoDistance({lat1,lon1}, {lat2,lon2})</code> measures the rhumb line (loxodrome) on the
   * WGS84 ellipsoid, i.e. the track of constant bearing between the two points. This is always at
   * least as long as the geodesic which
   * {@link org.matheclipse.core.numerics.geodesy.GeodesicSolver} computes.
   */
  private static final class GeoDistance extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      GeodeticPoint from = AstroConvert.toGeodeticPoint(ast.arg1());
      GeodeticPoint to = AstroConvert.toGeodeticPoint(ast.arg2());
      if (from == null || to == null) {
        return F.NIL;
      }
      try {
        // the loxodrome is pure ellipsoid geometry, so this works without the external data
        LoxodromeArc arc = new LoxodromeArc(from, to, AstroConvert.earthEllipsoidGeometryOnly());
        return AstroConvert.meters(arc.getDistance());
      } catch (OrekitException oex) {
        return Errors.printMessage(S.GeoDistance, "orekitdata", F.List(F.stringx(oex.getMessage())),
            engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * Resolve a celestial reference frame name to an Orekit {@link Frame}.
   *
   * @return the frame, or <code>null</code> if the name is not supported
   */
  public static Frame celestialFrame(String name) {
    switch (name.toLowerCase(Locale.US)) {
      case "icrs":
      case "gcrs":
      case "bcrs":
        return FramesFactory.getGCRF();
      case "equatorial":
      case "j2000":
        return FramesFactory.getEME2000();
      case "ecliptic":
      case "trueecliptic":
      case "meanecliptic":
      case "eclipticicrs":
        return FramesFactory.getEcliptic(IERSConventions.IERS_2010);
      case "itrs":
      case "tirs":
        return AstroConvert.earthFrame();
      case "teme":
        return FramesFactory.getTEME();
      case "cirs":
        return FramesFactory.getCIRF(IERSConventions.IERS_2010, false);
      case "tete":
        return FramesFactory.getTOD(IERSConventions.IERS_2010, false);
      case "meme":
        return FramesFactory.getMOD(IERSConventions.IERS_2010);
      default:
        return null;
    }
  }

  /**
   * <code>AstroPosition(body, frame, date)</code> - the coordinates of a body in a celestial
   * reference frame, as <code>{longitudeLike, latitudeLike, distance}</code>.
   *
   * <p>
   * The default frame is <code>"Horizon"</code>, which needs a location; every other frame is
   * geocentric and the location is ignored.
   */
  private static final class AstroPosition extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!AstroDataContext.checkAvailable(S.AstroPosition, engine)) {
        return F.NIL;
      }
      AstroBodies.Target target = AstroBodies.target(ast.arg1());
      if (target == null) {
        return Errors.printMessage(S.AstroPosition, "astrobody", F.List(ast.arg1(), ast), engine);
      }
      String frameName = "Horizon";
      GeodeticPoint point = null;
      AbsoluteDate date = null;
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isString()) {
          frameName = arg.toString();
          continue;
        }
        GeodeticPoint argPoint = AstroConvert.toGeodeticPoint(arg);
        if (argPoint != null) {
          point = argPoint;
          continue;
        }
        AbsoluteDate argDate = AstroConvert.toAbsoluteDate(arg);
        if (argDate != null) {
          date = argDate;
          continue;
        }
        return AstroConvert.reportUnreadableArgument(S.AstroPosition, arg, ast, engine);
      }
      if (date == null) {
        date = AstroConvert.nowUTC();
      }
      try {
        if ("Horizon".equalsIgnoreCase(frameName)) {
          if (point == null) {
            return Errors.printMessage(S.AstroPosition, "argillegal", F.List(ast, ast), engine);
          }
          AstroObserver observer = new AstroObserver(point, target.provider, target.meanRadius);
          TrackingCoordinates tracking = observer.tracking(date);
          // a star has only a direction, so its range is not reported
          return target.isStar
              ? F.List(AstroConvert.degreesPositive(tracking.getAzimuth()),
                  AstroConvert.degrees(tracking.getElevation()))
              : F.List(AstroConvert.degreesPositive(tracking.getAzimuth()),
                  AstroConvert.degrees(tracking.getElevation()),
                  AstroConvert.meters(tracking.getRange()));
        }
        Frame frame = celestialFrame(frameName);
        if (frame == null) {
          return Errors.printMessage(S.AstroPosition, "astrocsys",
              F.List(F.stringx(frameName), ast), engine);
        }
        Vector3D position = target.provider.getPosition(date, frame);
        return target.isStar
            ? F.List(AstroConvert.degreesPositive(position.getAlpha()),
                AstroConvert.degrees(position.getDelta()))
            : F.List(AstroConvert.degreesPositive(position.getAlpha()),
                AstroConvert.degrees(position.getDelta()), AstroConvert.meters(position.getNorm()));
      } catch (OrekitException oex) {
        return Errors.printMessage(S.AstroPosition, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }

    @Override
    public int status() {
      // the frame parameter dictionary and the coordinate system
      // argument which selects Cartesian or cylindrical output, are not supported
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>AstroDistance(body)</code> or <code>AstroDistance(body, observer)</code> - the distance
   * between two bodies, or from a location on Earth to a body.
   */
  private static final class AstroDistance extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!AstroDataContext.checkAvailable(S.AstroDistance, engine)) {
        return F.NIL;
      }
      AstroBodies.Target astroTarget = AstroBodies.target(ast.arg1());
      if (astroTarget == null) {
        return Errors.printMessage(S.AstroDistance, "astrobody", F.List(ast.arg1(), ast), engine);
      }
      if (astroTarget.isStar) {
        // the catalogue holds directions, not distances
        return Errors.printMessage(S.AstroDistance, "astrostarprop",
            F.List(F.stringx("a distance to a star"),
                F.stringx("the HYG database, which is not bundled")),
            engine);
      }
      String targetName = astroTarget.name;
      GeodeticPoint point = null;
      String observerName = null;
      AbsoluteDate date = null;
      for (int i = 2; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        GeodeticPoint argPoint = AstroConvert.toGeodeticPoint(arg);
        if (argPoint != null) {
          point = argPoint;
          continue;
        }
        AbsoluteDate argDate = AstroConvert.toAbsoluteDate(arg);
        if (argDate != null) {
          date = argDate;
          continue;
        }
        String argBody = AstroBodies.nameOf(arg);
        if (argBody != null) {
          observerName = argBody;
          continue;
        }
        return AstroConvert.reportUnreadableArgument(S.AstroDistance, arg, ast, engine);
      }
      if (date == null) {
        date = AstroConvert.nowUTC();
      }
      try {
        Frame frame = FramesFactory.getGCRF();
        Vector3D target = CelestialBodyFactory.getBody(targetName).getPosition(date, frame);
        Vector3D observer;
        if (observerName != null) {
          observer = CelestialBodyFactory.getBody(observerName).getPosition(date, frame);
        } else if (point != null) {
          observer =
              AstroConvert.toTopocentricFrame(point).getPVCoordinates(date, frame).getPosition();
        } else {
          // geocentric by default, which is what the ephemerides give directly
          observer = Vector3D.ZERO;
        }
        return AstroConvert.meters(target.subtract(observer).getNorm());
      } catch (OrekitException oex) {
        return Errors.printMessage(S.AstroDistance, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public int status() {
      // light time, aberration and light deflection corrections are not applied
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>AstroAngularSeparation(body1, body2, location, date)</code> - the angle between two
   * bodies as seen from the observer, or from the centre of the Earth if no location is given.
   */
  private static final class AstroAngularSeparation extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!AstroDataContext.checkAvailable(S.AstroAngularSeparation, engine)) {
        return F.NIL;
      }
      AstroBodies.Target firstTarget = AstroBodies.target(ast.arg1());
      AstroBodies.Target secondTarget = AstroBodies.target(ast.arg2());
      if (firstTarget == null) {
        return Errors.printMessage(S.AstroAngularSeparation, "astrobody", F.List(ast.arg1(), ast),
            engine);
      }
      if (secondTarget == null) {
        return Errors.printMessage(S.AstroAngularSeparation, "astrobody", F.List(ast.arg2(), ast),
            engine);
      }
      GeodeticPoint point = null;
      AbsoluteDate date = null;
      for (int i = 3; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        GeodeticPoint argPoint = AstroConvert.toGeodeticPoint(arg);
        if (argPoint != null) {
          point = argPoint;
          continue;
        }
        AbsoluteDate argDate = AstroConvert.toAbsoluteDate(arg);
        if (argDate != null) {
          date = argDate;
          continue;
        }
        return AstroConvert.reportUnreadableArgument(S.AstroAngularSeparation, arg, ast, engine);
      }
      if (date == null) {
        date = AstroConvert.nowUTC();
      }
      try {
        Frame frame = FramesFactory.getGCRF();
        Vector3D observer = point == null ? Vector3D.ZERO
            : AstroConvert.toTopocentricFrame(point).getPVCoordinates(date, frame).getPosition();
        Vector3D first = firstTarget.provider.getPosition(date, frame).subtract(observer);
        Vector3D second = secondTarget.provider.getPosition(date, frame).subtract(observer);
        return AstroConvert.degrees(Vector3D.angle(first, second));
      } catch (OrekitException oex) {
        return Errors.printMessage(S.AstroAngularSeparation, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_4;
    }
  }

  /**
   * <code>AstroSubpoint(body, date)</code> - the point on the Earth's surface which has the body
   * directly overhead, as a <code>GeoPosition</code>.
   */
  private static final class AstroSubpoint extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!AstroDataContext.checkAvailable(S.AstroSubpoint, engine)) {
        return F.NIL;
      }
      AstroBodies.Target subTarget = AstroBodies.target(ast.arg1());
      if (subTarget == null) {
        return Errors.printMessage(S.AstroSubpoint, "astrobody", F.List(ast.arg1(), ast), engine);
      }
      AbsoluteDate date =
          ast.isAST2() ? AstroConvert.toAbsoluteDate(ast.arg2()) : AstroConvert.nowUTC();
      if (date == null) {
        return AstroConvert.reportUnreadableArgument(S.AstroSubpoint, ast.arg2(), ast, engine);
      }
      try {
        Frame frame = AstroConvert.earthFrame();
        Vector3D position = subTarget.provider.getPosition(date, frame);
        GeodeticPoint subpoint = AstroConvert.earthEllipsoid().transform(position, frame, date);
        return GeoPositionExpr.newInstance(FastMath.toDegrees(subpoint.getLatitude()),
            FastMath.toDegrees(subpoint.getLongitude()));
      } catch (OrekitException oex) {
        return Errors.printMessage(S.AstroSubpoint, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private AstroPositionFunctions() {}
}
