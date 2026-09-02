package org.matheclipse.astro.convert;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.hipparchus.util.FastMath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.DateObjectExpr;
import org.matheclipse.core.expression.data.GeoPositionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.units.Units;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.DateComponents;
import org.orekit.time.DateTimeComponents;
import org.orekit.time.TimeComponents;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

/**
 * Converts between the Symja expression model and the Orekit types.
 *
 * <p>
 * Everything in here which touches a {@link Frame} or an {@link AbsoluteDate} needs the external
 * Orekit data to be loaded; see
 * {@link org.matheclipse.astro.data.AstroDataContext AstroDataContext}.
 */
public class AstroConvert {

  /** The unit name of an angle in degrees in <code>units.json</code>. */
  public static final String DEGREES = "AngularDegrees";

  /** The unit name of a length in meters in <code>units.json</code>. */
  public static final String METERS = "Meters";

  private AstroConvert() {}

  /**
   * The Earth rotating frame. Uses the IERS 2010 conventions with the full Earth orientation
   * parameters, which is what the external data set provides.
   */
  public static Frame earthFrame() {
    return FramesFactory.getITRF(IERSConventions.IERS_2010, false);
  }

  /** The WGS84 reference ellipsoid, expressed in {@link #earthFrame()}. */
  public static OneAxisEllipsoid earthEllipsoid() {
    return new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
        Constants.WGS84_EARTH_FLATTENING, earthFrame());
  }

  /**
   * The WGS84 reference ellipsoid attached to GCRF instead of to ITRF. Earth orientation
   * parameters, and therefore the external Orekit data, are not needed to build this frame, so it
   * can be used for calculations which only depend on the shape of the ellipsoid and not on how it
   * is oriented in space - the loxodrome of {@link org.matheclipse.core.expression.S#GeoDistance}
   * for instance.
   */
  public static OneAxisEllipsoid earthEllipsoidGeometryOnly() {
    return new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
        Constants.WGS84_EARTH_FLATTENING, FramesFactory.getGCRF());
  }

  /**
   * Convert a location argument into a {@link GeodeticPoint}. Accepts a
   * <code>GeoPosition(...)</code> object as well as a plain <code>{latitude, longitude}</code> or
   * <code>{latitude, longitude, altitude}</code> list of degrees and meters.
   *
   * @return the geodetic point, or <code>null</code> if <code>location</code> is not a location
   */
  public static GeodeticPoint toGeodeticPoint(IExpr location) {
    if (location instanceof GeoPositionExpr) {
      GeoPositionExpr geo = (GeoPositionExpr) location;
      return newGeodeticPoint(geo.latitude(), geo.longitude(), geo.altitude());
    }
    if (location.isList()) {
      double[] vector = location.toDoubleVector();
      if (vector != null) {
        if (vector.length == 2) {
          return newGeodeticPoint(vector[0], vector[1], 0.0);
        } else if (vector.length == 3) {
          return newGeodeticPoint(vector[0], vector[1], vector[2]);
        }
      }
    }
    return null;
  }

  /**
   * @param latitude latitude in degrees
   * @param longitude longitude in degrees
   * @param altitude altitude in meters above the reference ellipsoid
   */
  public static GeodeticPoint newGeodeticPoint(double latitude, double longitude,
      double altitude) {
    return new GeodeticPoint(FastMath.toRadians(latitude), FastMath.toRadians(longitude),
        altitude);
  }

  /** The observer frame at <code>point</code> on the WGS84 ellipsoid. */
  public static TopocentricFrame toTopocentricFrame(GeodeticPoint point) {
    return new TopocentricFrame(earthEllipsoid(), point, "Symja");
  }

  /**
   * Convert a date argument into an {@link AbsoluteDate} in the UTC time scale. Accepts a
   * <code>DateObject(...)</code> as well as a <code>{year, month, day, ...}</code> list.
   *
   * @return the date, or <code>null</code> if <code>date</code> is not a date
   */
  public static AbsoluteDate toAbsoluteDate(IExpr date) {
    LocalDateTime dateTime = toLocalDateTime(date);
    if (dateTime == null) {
      return null;
    }
    return new AbsoluteDate(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(),
        dateTime.getHour(), dateTime.getMinute(),
        dateTime.getSecond() + dateTime.getNano() / 1.0e9, TimeScalesFactory.getUTC());
  }

  /** The current instant as an Orekit date in the UTC time scale. */
  public static AbsoluteDate nowUTC() {
    return new AbsoluteDate(LocalDateTime.now(ZoneOffset.UTC).toString(),
        TimeScalesFactory.getUTC());
  }

  /**
   * Reduce a date argument to a {@link LocalDateTime} in UTC. A <code>DateObject</code> carrying a
   * numeric time zone offset is shifted to UTC; a date without a time zone is taken to be UTC
   * already.
   *
   * @return the instant, or <code>null</code> if <code>date</code> is not a date
   */
  public static LocalDateTime toLocalDateTime(IExpr date) {
    if (date instanceof DateObjectExpr) {
      DateObjectExpr dateObject = (DateObjectExpr) date;
      LocalDateTime dateTime = dateObject.toData();
      IExpr timeZone = dateObject.getTimeZone();
      if (timeZone != null && timeZone.isReal()) {
        double offsetHours = timeZone.evalf();
        return dateTime.minusNanos((long) (offsetHours * 3600.0 * 1.0e9));
      }
      return dateTime;
    }
    if (date.isList()) {
      double[] vector = date.toDoubleVector();
      if (vector != null && vector.length >= 3 && isFiniteDateSpec(vector)) {
        int hour = vector.length > 3 ? (int) vector[3] : 0;
        int minute = vector.length > 4 ? (int) vector[4] : 0;
        double second = vector.length > 5 ? vector[5] : 0.0;
        try {
          return LocalDateTime.of((int) vector[0], (int) vector[1], (int) vector[2], hour, minute,
              (int) second, (int) ((second - (int) second) * 1.0e9));
        } catch (DateTimeException dte) {
          // a component outside its calendar range is not a date either, and the callers all know
          // how to report a date they could not read; letting this escape made
          // FindAstroEvent(0,{1,1,1,Infinity}) fail with a Java exception instead of a message
          return null;
        }
      }
    }
    return null;
  }

  /**
   * Report an argument that could be read neither as a position nor as a date, and answer
   * {@link F#NIL} so the expression stays unevaluated.
   *
   * <p>
   * The astronomy functions accept their arguments in any order and identify each one by trying to
   * convert it, so the only thing known at the point of failure is that nothing recognised it. That
   * made every rejection come out as the general "Illegal arguments" message naming the whole
   * expression, which is a poor answer for the common case of a date that is very nearly right:
   * <code>{1,1,1,Infinity}</code> is not an illegal argument in general, it is a date whose hour is
   * not a number. Where {@link #dateSpecComplaint(IExpr)} can recognise that, the reader is told
   * which part is at fault instead.
   *
   * @param symbol the built-in reporting the message
   * @param arg the argument that could not be read
   * @param ast the whole expression, named by the general message
   * @param engine the evaluation engine
   * @return {@link F#NIL}
   */
  public static IExpr reportUnreadableArgument(ISymbol symbol, IExpr arg, IAST ast,
      EvalEngine engine) {
    IExpr complaint = dateSpecComplaint(arg);
    if (complaint != null) {
      // Expression `1` cannot be interpreted as a date specification.
      return Errors.printMessage(symbol, "date", F.List(complaint), engine);
    }
    // Illegal arguments: "`1`" in `2`
    return Errors.printMessage(symbol, "argillegal", F.List(arg, ast), engine);
  }

  /**
   * Name what makes {@code date} unreadable as a date, for the {@code date} message.
   *
   * <p>
   * Only for arguments that were meant as a date. The astronomy functions accept a position and a
   * date in either order and tell them apart by trying {@link #toGeodeticPoint(IExpr)} first, which
   * takes a numeric list of two or three elements; so anything still unread by the time a date is
   * attempted is either a <code>DateObject</code> or a longer list, and a caller that takes no
   * position at all has nothing to confuse it with. Anything else is some other kind of wrong
   * argument and stays with the general <code>argillegal</code> message.
   *
   * <p>
   * The component is named rather than the whole list where one can be singled out, because
   * {@code Infinity} says what is wrong with <code>{1,1,1,Infinity}</code> and the list does not.
   * A list whose parts are all finite numbers failed on the calendar instead &mdash; a thirteenth
   * month, say &mdash; and there the list as a whole is the answer.
   *
   * @param date the argument that {@link #toAbsoluteDate(IExpr)} could not read
   * @return the offending expression, or <code>null</code> if this was not meant as a date
   */
  public static IExpr dateSpecComplaint(IExpr date) {
    if (date instanceof DateObjectExpr) {
      return date;
    }
    if (date.isList()) {
      double[] vector = date.toDoubleVector();
      if (vector != null && vector.length >= 3) {
        for (int i = 0; i < vector.length; i++) {
          if (Double.isNaN(vector[i]) || Double.isInfinite(vector[i])) {
            return ((IAST) date).get(i + 1);
          }
        }
        return date;
      }
    }
    return null;
  }

  /**
   * A date specification has to consist of finite components.
   *
   * <p>
   * The range check below cannot stand in for this one. Narrowing a {@code NaN} to {@code int}
   * answers {@code 0}, which is a perfectly valid hour, so a list holding one would otherwise be
   * read as a date at midnight rather than rejected.
   *
   * @param vector the components of a <code>{year, month, day, ...}</code> list
   * @return <code>true</code> if every component is finite
   */
  private static boolean isFiniteDateSpec(double[] vector) {
    for (int i = 0; i < vector.length; i++) {
      if (Double.isNaN(vector[i]) || Double.isInfinite(vector[i])) {
        return false;
      }
    }
    return true;
  }

  /** Wrap a {@link LocalDateTime} in a <code>DateObject</code> with instant granularity. */
  public static IExpr toDateObject(LocalDateTime dateTime) {
    return DateObjectExpr.newInstance(dateTime, DateObjectExpr.newInstance(dateTime)
        .getGranularity(), DateObjectExpr.GREGORIAN, F.CD0, true);
  }

  /** An {@link AbsoluteDate} as a UTC <code>DateObject</code>. */
  public static IExpr toDateObject(AbsoluteDate date) {
    return toDateObject(toLocalDateTime(date, TimeScalesFactory.getUTC()));
  }

  /**
   * Read an {@link AbsoluteDate} in the given time scale.
   *
   * <p>
   * Goes through {@link AbsoluteDate#getComponents} rather than through the ISO string, because
   * Orekit keeps the seconds in an exact representation and prints far more fractional digits than
   * {@link LocalDateTime#parse} accepts.
   */
  public static LocalDateTime toLocalDateTime(AbsoluteDate date, TimeScale timeScale) {
    DateTimeComponents components = date.getComponents(timeScale);
    DateComponents day = components.getDate();
    TimeComponents time = components.getTime();
    double seconds = time.getSecond();
    int wholeSeconds = (int) seconds;
    // LocalDateTime resolves to the nanosecond, which is far finer than the accuracy of any
    // event this module searches for
    int nanos = (int) FastMath.round((seconds - wholeSeconds) * 1.0e9);
    if (nanos >= 1000000000) {
      nanos = 999999999;
    }
    return LocalDateTime.of(day.getYear(), day.getMonth(), day.getDay(), time.getHour(),
        time.getMinute(), wholeSeconds, nanos);
  }

  /**
   * An angle in radians as a <code>Quantity</code> in degrees.
   */
  public static IExpr degrees(double radians) {
    return F.Quantity(F.num(FastMath.toDegrees(radians)), F.stringx(DEGREES));
  }

  /**
   * An angle in radians as a <code>Quantity</code> in degrees, normalized to
   * <code>[0, 360)</code>.
   */
  public static IExpr degreesPositive(double radians) {
    double degrees = FastMath.toDegrees(radians) % 360.0;
    if (degrees < 0.0) {
      degrees += 360.0;
    }
    return F.Quantity(F.num(degrees), F.stringx(DEGREES));
  }

  /** A length in meters as a <code>Quantity</code>. */
  public static IExpr meters(double meters) {
    return F.Quantity(F.num(meters), F.stringx(METERS));
  }

  /**
   * Read an angle written either as a plain number of degrees or as an angle
   * <code>Quantity</code> in any compatible unit.
   *
   * @return the angle in radians, or <code>null</code> if {@code expr} is not an angle
   */
  public static Double toRadians(IExpr expr, EvalEngine engine) {
    if (expr.isAST(S.Quantity, 3)) {
      IAST quantity = (IAST) expr;
      IExpr fromUnit = Units.normalize(quantity.arg2());
      IExpr toUnit = Units.normalize(F.stringx(DEGREES));
      if (fromUnit.isNIL() || toUnit.isNIL()) {
        // a unit the registry does not recognise normalizes to NIL; handing that to
        // convertMagnitude builds an expression with NIL inside it, which nothing downstream is
        // allowed to see, and it surfaces as a NullPointerException far from here
        return null;
      }
      IExpr converted = Units.convertMagnitude(quantity.arg1(), fromUnit, toUnit, engine);
      if (converted.isPresent() && converted.isReal()) {
        return FastMath.toRadians(converted.evalf());
      }
      return null;
    }
    if (expr.isReal()) {
      return FastMath.toRadians(expr.evalf());
    }
    return null;
  }

  /**
   * Read an option which is expected to be one of a fixed set of strings.
   *
   * @param option the option value, may be {@link S#Automatic} or <code>null</code>
   * @param defaultValue returned when the option is absent or {@link S#Automatic}
   * @return the lower-cased option string, or {@code defaultValue}
   */
  public static String optionString(IExpr option, String defaultValue) {
    if (option == null || !option.isString() || option == S.Automatic) {
      return defaultValue;
    }
    return option.toString();
  }
}
