package org.matheclipse.astro.builtin;

import java.util.Locale;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.matheclipse.astro.convert.AstroBodies;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.data.AstroDataContext;
import org.matheclipse.astro.solve.DateRootFinder;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinatesProvider;

/**
 * <code>FindAstroEvent(eventType, date, location)</code> - the next date at which a named
 * astronomical event happens.
 *
 * <p>
 * Every event here is a root or an extremum of a smooth function of the date, so they all go
 * through {@link DateRootFinder}. What differs between them is only which function and whether the
 * event is a crossing or a turning point; the dispatch below is that table.
 */
public class AstroFindEventFunctions {

  /** Scan step for events driven by the Sun's motion along the ecliptic. */
  private static final double SOLAR_STEP = 0.5 * Constants.JULIAN_DAY;

  /** Scan step for events driven by the Moon, which moves about 13 degrees a day. */
  private static final double LUNAR_STEP = 0.25 * Constants.JULIAN_DAY;

  /** Scan step for planetary configurations, which change slowly. */
  private static final double PLANET_STEP = 2.0 * Constants.JULIAN_DAY;

  /** How far ahead a search runs before giving up. Long enough for the outer planets. */
  private static final double HORIZON = 4.0 * Constants.JULIAN_YEAR;

  /** Horizon for events which recur within a year. */
  private static final double YEAR_HORIZON = 1.2 * Constants.JULIAN_YEAR;

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.FindAstroEvent.setEvaluator(new FindAstroEvent());
    }
  }

  /** Normalize an angle to <code>[0, 2 pi)</code>. */
  static double normalizeAngle(double angle) {
    double normalized = angle % (2.0 * FastMath.PI);
    return normalized < 0.0 ? normalized + 2.0 * FastMath.PI : normalized;
  }

  /**
   * The apparent geocentric ecliptic longitude of a body, in radians.
   *
   * <p>
   * Corrected for light travel time, which for these events is not a nicety: the Sun's longitude
   * changes by only about a degree a day, so the 20 arcseconds of displacement move an equinox or a
   * solstice by some eight minutes. Rise and set times, where the Sun moves a degree every four
   * minutes, are unaffected at this level.
   */
  static double eclipticLongitude(String bodyName, AbsoluteDate date) {
    Frame ecliptic = FramesFactory.getEcliptic(IERSConventions.IERS_2010);
    return normalizeAngle(apparentPosition(bodyName, date, ecliptic).getAlpha());
  }

  /**
   * Where a body is seen from the Earth, corrected for light travel time.
   *
   * <p>
   * Two iterations are plenty: the correction to the light time itself is of the order of the
   * body's motion during it, far below the accuracy of the ephemerides.
   */
  static Vector3D apparentPosition(String bodyName, AbsoluteDate date, Frame frame) {
    PVCoordinatesProvider body = CelestialBodyFactory.getBody(bodyName);
    Vector3D position = body.getPosition(date, frame);
    for (int i = 0; i < 2; i++) {
      double lightTime = position.getNorm() / Constants.SPEED_OF_LIGHT;
      position = body.getPosition(date.shiftedBy(-lightTime), frame);
    }
    return position;
  }

  /**
   * The elongation of a body from the Sun in geocentric ecliptic longitude, in
   * <code>[0, 2 pi)</code>. Zero at conjunction, pi at opposition.
   */
  static double elongationFromSun(String bodyName, AbsoluteDate date) {
    return normalizeAngle(eclipticLongitude(bodyName, date)
        - eclipticLongitude(CelestialBodyFactory.SUN, date));
  }

  /** The angle between a body and the Sun seen from the Earth, in <code>[0, pi]</code>. */
  static double separationFromSun(String bodyName, AbsoluteDate date) {
    Frame frame = FramesFactory.getGCRF();
    Vector3D body = CelestialBodyFactory.getBody(bodyName).getPosition(date, frame);
    Vector3D sun = CelestialBodyFactory.getSun().getPosition(date, frame);
    return Vector3D.angle(body, sun);
  }

  /** The angle between two bodies seen from the centre of the Earth, in radians. */
  static double separation(String firstName, String secondName, AbsoluteDate date) {
    Frame frame = FramesFactory.getGCRF();
    Vector3D first = CelestialBodyFactory.getBody(firstName).getPosition(date, frame);
    Vector3D second = CelestialBodyFactory.getBody(secondName).getPosition(date, frame);
    return Vector3D.angle(first, second);
  }

  /** The distance from the Earth's centre to a body, in meters. */
  static double geocentricDistance(String bodyName, AbsoluteDate date) {
    return CelestialBodyFactory.getBody(bodyName)
        .getPosition(date, FramesFactory.getGCRF()).getNorm();
  }

  /** The distance from a body to the body it orbits, in meters. */
  static double primaryDistance(String bodyName, AbsoluteDate date) {
    Frame frame = FramesFactory.getGCRF();
    Vector3D body = CelestialBodyFactory.getBody(bodyName).getPosition(date, frame);
    Vector3D primary = CelestialBodyFactory.getBody(AstroBodies.primaryOf(bodyName))
        .getPosition(date, frame);
    return body.subtract(primary).getNorm();
  }

  /**
   * Find the next date at which a wrapped angle function reaches {@code target}.
   *
   * <p>
   * Re-centering on the target turns "reaches" into a sign change, and the jump guard of
   * {@link DateRootFinder#findCrossing} keeps the branch cut from being mistaken for the root.
   */
  static AbsoluteDate findAngle(DateRootFinder.DateFunction angle, double target,
      AbsoluteDate start, double step, double horizon, int direction) {
    DateRootFinder.DateFunction offset =
        date -> normalizeAngle(angle.value(date) - target + FastMath.PI) - FastMath.PI;
    return DateRootFinder.findCrossing(offset, start, step, horizon, direction, FastMath.PI);
  }

  /** The scan step which suits a body's apparent motion. */
  private static double stepFor(String bodyName) {
    if (CelestialBodyFactory.MOON.equals(bodyName)) {
      return LUNAR_STEP;
    }
    if (CelestialBodyFactory.SUN.equals(bodyName)) {
      return SOLAR_STEP;
    }
    return PLANET_STEP;
  }

  private static final class FindAstroEvent extends AbstractFunctionOptionEvaluator {

    /** Set when a message was printed for an unusable event specification. */
    private boolean rejected;

    /** Report an unusable specification and remember that the expression stays unevaluated. */
    private AbsoluteDate reject(String messageShortcut, IExpr argument, IAST ast,
        EvalEngine engine) {
      rejected = true;
      Errors.printMessage(S.FindAstroEvent, messageShortcut, F.List(argument, ast), engine);
      return null;
    }

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      if (!AstroDataContext.checkAvailable(S.FindAstroEvent, engine)) {
        return F.NIL;
      }
      IExpr eventSpec = ast.arg1();
      rejected = false;
      AbsoluteDate date = null;
      GeodeticPoint point = null;
      for (int i = 2; i <= argSize; i++) {
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
        return AstroConvert.reportUnreadableArgument(S.FindAstroEvent, arg, ast, engine);
      }
      if (date == null) {
        date = AstroConvert.nowUTC();
      }
      int direction = options[0] != null && options[0].isReal() && options[0].evalf() < 0 ? -1 : 1;

      try {
        AbsoluteDate found = dispatch(eventSpec, date, point, direction, ast, engine);
        if (found == null) {
          // a rejected specification has already been reported and leaves the expression
          // unevaluated; a legal event which simply does not occur gives Missing
          return rejected ? F.NIL : S.Missing;
        }
        return AstroConvert.toDateObject(found);
      } catch (OrekitException oex) {
        return Errors.printMessage(S.FindAstroEvent, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    /**
     * Resolve the event specification and run the matching search.
     *
     * @return the event date, or <code>null</code> if the event does not occur or the
     *         specification was rejected with a message
     */
    private AbsoluteDate dispatch(IExpr eventSpec, AbsoluteDate date, GeodeticPoint point,
        int direction, IAST ast, EvalEngine engine) {
      if (eventSpec.isString()) {
        return simpleEvent(eventSpec.toString(), eventSpec, date, point, direction, ast, engine);
      }
      if (eventSpec.isList() && eventSpec.size() >= 2) {
        IAST list = (IAST) eventSpec;
        if (!list.arg1().isString()) {
          return reject("astroevent", eventSpec, ast, engine);
        }
        return compoundEvent(list, date, point, direction, ast, engine);
      }
      return reject("astroevent", eventSpec, ast, engine);
    }

    /** The events which are named by a bare string. */
    private AbsoluteDate simpleEvent(String name, IExpr eventSpec, AbsoluteDate date,
        GeodeticPoint point, int direction, IAST ast, EvalEngine engine) {
      switch (name.toLowerCase(Locale.US)) {
        case "marchequinox":
          return solarQuarter(0, date, direction);
        case "junesolstice":
          return solarQuarter(1, date, direction);
        case "septemberequinox":
          return solarQuarter(2, date, direction);
        case "decembersolstice":
          return solarQuarter(3, date, direction);
        case "newmoon":
          return AstroEventFunctions.findPhase(date, 0.0, direction);
        case "fullmoon":
          return AstroEventFunctions.findPhase(date, FastMath.PI, direction);
        case "sunrise":
          return riseSet(CelestialBodyFactory.SUN, "Rise", point, date, direction, eventSpec, ast,
              engine);
        case "sunset":
          return riseSet(CelestialBodyFactory.SUN, "Set", point, date, direction, eventSpec, ast,
              engine);
        case "moonrise":
          return riseSet(CelestialBodyFactory.MOON, "Rise", point, date, direction, eventSpec, ast,
              engine);
        case "moonset":
          return riseSet(CelestialBodyFactory.MOON, "Set", point, date, direction, eventSpec, ast,
              engine);
        case "firstequilux":
          return equilux(point, date, direction, true, eventSpec, ast, engine);
        case "secondequilux":
          return equilux(point, date, direction, false, eventSpec, ast, engine);
        default:
          return reject("astroevent", eventSpec, ast, engine);
      }
    }

    /** The events which are written as <code>{name, body, ...}</code>. */
    private AbsoluteDate compoundEvent(IAST list, AbsoluteDate date, GeodeticPoint point,
        int direction, IAST ast, EvalEngine engine) {
      String name = list.arg1().toString();
      String lowerName = name.toLowerCase(Locale.US);

      if ("moonphase".equals(lowerName)) {
        if (list.argSize() != 2 || !list.arg2().isReal()) {
          return reject("astroevent", list, ast, engine);
        }
        return AstroEventFunctions.findPhase(date,
            normalizeAngle(list.arg2().evalf() * 2.0 * FastMath.PI), direction);
      }

      if ("separation".equals(lowerName) || "distance".equals(lowerName)) {
        // the conditional forms of these take a comparison to solve for, which this
        // implementation does not support
        return reject("astroevent", list, ast, engine);
      }

      if ("appulse".equals(lowerName) && list.argSize() == 3) {
        String first = AstroBodies.nameOf(list.arg2());
        String second = AstroBodies.nameOf(list.arg3());
        if (first == null || second == null) {
          return reject("astrobody", first == null ? list.arg2() : list.arg3(), ast, engine);
        }
        // the closest approach of the two, i.e. a minimum of their angular separation
        return DateRootFinder.findExtremum(d -> separation(first, second, d), date,
            LUNAR_STEP, HORIZON, direction, false);
      }

      if (list.argSize() != 2) {
        return reject("astroevent", list, ast, engine);
      }
      switch (lowerName) {
        case "rise":
        case "set":
        case "upperculmination":
        case "lowerculmination": {
          // these work for a star as well, so resolve the wider notion of a target
          AstroBodies.Target target = AstroBodies.target(list.arg2());
          if (target == null) {
            return reject("astrobody", list.arg2(), ast, engine);
          }
          if (point == null) {
            return reject("argillegal", list, ast, engine);
          }
          return AstroEventFunctions.riseSetDateOf(target.provider, target.meanRadius, name, point,
              date, direction);
        }
        default:
          break;
      }

      String bodyName = AstroBodies.nameOf(list.arg2());
      if (bodyName == null) {
        return reject("astrobody", list.arg2(), ast, engine);
      }
      double step = stepFor(bodyName);

      switch (lowerName) {
        case "conjunction":
          return findAngle(d -> elongationFromSun(bodyName, d), 0.0, date, step, HORIZON,
              direction);
        case "opposition":
          if (AstroBodies.isInferior(bodyName)) {
            // an inner planet is never opposite the Sun
            return null;
          }
          return findAngle(d -> elongationFromSun(bodyName, d), FastMath.PI, date, step, HORIZON,
              direction);
        case "easternquadrature":
          if (AstroBodies.isInferior(bodyName)) {
            return null;
          }
          return findAngle(d -> elongationFromSun(bodyName, d), FastMath.PI / 2.0, date, step,
              HORIZON, direction);
        case "westernquadrature":
          if (AstroBodies.isInferior(bodyName)) {
            return null;
          }
          return findAngle(d -> elongationFromSun(bodyName, d), 3.0 * FastMath.PI / 2.0, date,
              step, HORIZON, direction);
        case "inferiorconjunction":
        case "superiorconjunction":
          return conjunction(bodyName, date, step, direction,
              "inferiorconjunction".equals(lowerName));
        case "greatesteasternelongation":
        case "greatestwesternelongation":
          return greatestElongation(bodyName, date, step, direction,
              "greatesteasternelongation".equals(lowerName));
        case "perihelion":
        case "periapsis":
          return DateRootFinder.findExtremum(d -> primaryDistance(bodyName, d), date, step,
              HORIZON, direction, false);
        case "aphelion":
        case "apoapsis":
          return DateRootFinder.findExtremum(d -> primaryDistance(bodyName, d), date, step,
              HORIZON, direction, true);
        case "perigee":
          return DateRootFinder.findExtremum(d -> geocentricDistance(bodyName, d), date, step,
              HORIZON, direction, false);
        case "apogee":
          return DateRootFinder.findExtremum(d -> geocentricDistance(bodyName, d), date, step,
              HORIZON, direction, true);
        default:
          return reject("astroevent", list, ast, engine);
      }
    }

    /**
     * An equinox or a solstice, located through the Sun's apparent declination.
     *
     * <p>
     * The equinoxes are where the Sun's apparent ecliptic longitude is 0 or 180 degrees and the
     * solstices where it is 90 or 270. Reading the longitude off Orekit's ecliptic frame would
     * inherit the <em>mean</em> equinox that frame is built on, and the nutation in longitude of up
     * to 17 arcseconds moves the answer by several minutes.
     *
     * <p>
     * Right ascension in the true equator and equinox of date gives the same four instants without
     * that error: the ecliptic meets the equator at the equinoxes, so longitude 0 and 180 are right
     * ascension 0 and 180, and the solstitial colure meets the ecliptic at longitude 90 and 270,
     * which are right ascension 90 and 270. Declination would express the equinoxes just as well
     * but is useless for the solstices - it is flat to nine decimal places for an hour either side,
     * so its extremum cannot be located.
     *
     * @param quarter 0 for the March equinox, 1 for the June solstice, 2 for September, 3 for
     *        December
     */
    private AbsoluteDate solarQuarter(int quarter, AbsoluteDate date, int direction) {
      Frame trueOfDate = FramesFactory.getTOD(IERSConventions.IERS_2010, false);
      DateRootFinder.DateFunction rightAscension = d -> normalizeAngle(
          apparentPosition(CelestialBodyFactory.SUN, d, trueOfDate).getAlpha());
      return findAngle(rightAscension, quarter * FastMath.PI / 2.0, date, SOLAR_STEP,
          YEAR_HORIZON, direction);
    }

    /**
     * A conjunction of the requested kind. Both kinds are at zero elongation; what tells them
     * apart is whether the body is on this side of the Sun or beyond it.
     */
    private AbsoluteDate conjunction(String bodyName, AbsoluteDate date, double step,
        int direction, boolean inferior) {
      if (!AstroBodies.isInferior(bodyName)) {
        // only Mercury and Venus have two kinds of conjunction
        return inferior ? null
            : findAngle(d -> elongationFromSun(bodyName, d), 0.0, date, step, HORIZON, direction);
      }
      AbsoluteDate from = date;
      for (int i = 0; i < 40; i++) {
        AbsoluteDate found =
            findAngle(d -> elongationFromSun(bodyName, d), 0.0, from, step, HORIZON, direction);
        if (found == null) {
          return null;
        }
        boolean nearer = geocentricDistance(bodyName, found)
            < geocentricDistance(CelestialBodyFactory.SUN, found);
        if (nearer == inferior) {
          return found;
        }
        from = found.shiftedBy(direction < 0 ? -step : step);
      }
      return null;
    }

    /**
     * The greatest elongation east or west of the Sun, which is a maximum of the angular
     * separation restricted to the half of the cycle on the requested side.
     */
    private AbsoluteDate greatestElongation(String bodyName, AbsoluteDate date, double step,
        int direction, boolean eastern) {
      AbsoluteDate from = date;
      for (int i = 0; i < 40; i++) {
        AbsoluteDate found = DateRootFinder.findExtremum(d -> separationFromSun(bodyName, d), from,
            step, HORIZON, direction, true);
        if (found == null) {
          return null;
        }
        // east of the Sun means the body leads it in ecliptic longitude
        boolean isEastern = elongationFromSun(bodyName, found) < FastMath.PI;
        if (isEastern == eastern) {
          return found;
        }
        from = found.shiftedBy(direction < 0 ? -step : step);
      }
      return null;
    }

    /** Delegate a rise, set or culmination to the shared search, requiring a location. */
    private AbsoluteDate riseSet(String bodyName, String eventType, GeodeticPoint point,
        AbsoluteDate date, int direction, IExpr eventSpec, IAST ast, EvalEngine engine) {
      if (point == null) {
        return reject("argillegal", eventSpec, ast, engine);
      }
      return AstroEventFunctions.riseSetDate(bodyName, eventType, point, date, direction);
    }

    /**
     * An equilux: the day on which the interval from sunrise to sunset is exactly twelve hours.
     *
     * <p>
     * This is not the equinox - the equinox is about the Sun's position, while the equilux depends
     * on the observer's latitude and on refraction, and the two fall a few days apart.
     *
     * @param first the equilux of the lengthening half of the year rather than the shortening one
     */
    private AbsoluteDate equilux(GeodeticPoint point, AbsoluteDate date, int direction,
        boolean first, IExpr eventSpec, IAST ast, EvalEngine engine) {
      if (point == null) {
        return reject("argillegal", eventSpec, ast, engine);
      }
      DateRootFinder.DateFunction excess = d -> {
        AbsoluteDate midnight = d.shiftedBy(-0.5 * Constants.JULIAN_DAY);
        AbsoluteDate sunrise =
            AstroEventFunctions.riseSetDate(CelestialBodyFactory.SUN, "Rise", point, midnight, 1);
        if (sunrise == null) {
          return Double.NaN;
        }
        AbsoluteDate sunset =
            AstroEventFunctions.riseSetDate(CelestialBodyFactory.SUN, "Set", point, sunrise, 1);
        if (sunset == null) {
          return Double.NaN;
        }
        return sunset.durationFrom(sunrise) - 0.5 * Constants.JULIAN_DAY;
      };
      // one day is far finer than needed to separate the two equiluxes of a year
      double step = Constants.JULIAN_DAY;
      AbsoluteDate from = date;
      for (int i = 0; i < 4; i++) {
        AbsoluteDate found =
            DateRootFinder.findCrossing(excess, from, step, YEAR_HORIZON, direction);
        if (found == null) {
          return null;
        }
        // the day is lengthening at the spring equilux and shortening at the autumn one
        boolean lengthening = excess.value(found.shiftedBy(step)) > excess.value(
            found.shiftedBy(-step));
        if (lengthening == first) {
          return found;
        }
        from = found.shiftedBy(direction < 0 ? -step : step);
      }
      return null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.TimeDirection, S.TimeZone, S.CalendarType, S.DateGranularity}, //
          new IExpr[] {F.C1, S.Automatic, S.Automatic, S.Automatic});
    }

    @Override
    public int status() {
      // the conditional {"Distance",...} and {"Separation",...} forms are not implemented, and
      // the results are always returned in UTC regardless of TimeZone
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private AstroFindEventFunctions() {}
}
