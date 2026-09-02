package org.matheclipse.astro.builtin;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.data.AstroDataContext;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.DateObjectExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.SexagesimalAngle;
import org.orekit.errors.OrekitException;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

/**
 * Sidereal and solar time, conversion between the astronomical time systems, and the
 * degree-minute-second angle notation.
 *
 * <p>
 * Angles are returned as <code>Quantity</code> values in <code>"AngularDegrees"</code> rather than
 * in the mixed hour/minute/second units used for right ascension, so that they stay dimensionally
 * consistent with the rest of Symja's unit system and can be fed straight into
 * <code>UnitConvert</code>.
 */
public class AstroTimeFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.SiderealTime.setEvaluator(new SiderealTime());
      S.SolarTime.setEvaluator(new SolarTime());
      S.TimeSystemConvert.setEvaluator(new TimeSystemConvert());
      S.TimeZoneConvert.setEvaluator(new TimeZoneConvert());
      S.LocalTime.setEvaluator(new LocalTime());
      S.FromDMS.setEvaluator(new FromDMS());
      S.DMSList.setEvaluator(new DMSList());
      S.DMSString.setEvaluator(new DMSString());
    }
  }

  /**
   * Look up one of the astronomical time scales by name.
   *
   * @return the time scale, or <code>null</code> if {@code name} does not name one
   */
  static TimeScale timeScale(String name) {
    switch (name.toUpperCase(Locale.US)) {
      case "UTC":
        return TimeScalesFactory.getUTC();
      case "TAI":
        return TimeScalesFactory.getTAI();
      case "TT":
      case "TDT":
        return TimeScalesFactory.getTT();
      case "TDB":
        return TimeScalesFactory.getTDB();
      case "TCG":
        return TimeScalesFactory.getTCG();
      case "TCB":
        return TimeScalesFactory.getTCB();
      case "UT1":
        return TimeScalesFactory.getUT1(IERSConventions.IERS_2010, false);
      case "GPS":
        return TimeScalesFactory.getGPS();
      case "GST":
      case "GALILEO":
        return TimeScalesFactory.getGST();
      case "GLONASS":
        return TimeScalesFactory.getGLONASS();
      case "QZSS":
        return TimeScalesFactory.getQZSS();
      case "BDT":
      case "BEIDOU":
        return TimeScalesFactory.getBDT();
      default:
        return null;
    }
  }

  /**
   * <code>SiderealTime(type, location, date)</code> - the right ascension of the local meridian.
   *
   * <p>
   * Greenwich sidereal time comes from the IERS conventions; the observer's longitude is added to
   * turn it into the local one.
   */
  private static final class SiderealTime extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int argSize = ast.argSize();
      if (!AstroDataContext.checkAvailable(S.SiderealTime, engine)) {
        return F.NIL;
      }
      String type = "ApparentTime";
      GeodeticPoint point = null;
      AbsoluteDate date = null;
      for (int i = 1; i <= argSize; i++) {
        IExpr arg = ast.get(i);
        if (arg.isString()) {
          type = arg.toString();
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
        return AstroConvert.reportUnreadableArgument(S.SiderealTime, arg, ast, engine);
      }
      if (date == null) {
        date = AstroConvert.nowUTC();
      }
      double longitude = point == null ? 0.0 : point.getLongitude();
      try {
        TimeScale ut1 = TimeScalesFactory.getUT1(IERSConventions.IERS_2010, false);
        double greenwich;
        if ("MeanTime".equalsIgnoreCase(type)) {
          greenwich = IERSConventions.IERS_2010.getGMSTFunction(ut1).value(date);
        } else if ("ApparentTime".equalsIgnoreCase(type)) {
          greenwich = IERSConventions.IERS_2010
              .getGASTFunction(ut1, FramesFactory.getEOPHistory(IERSConventions.IERS_2010, false))
              .value(date);
        } else {
          return Errors.printMessage(S.SiderealTime, "astrotimetype", F.List(F.stringx(type), ast),
              engine);
        }
        return AstroConvert.degreesPositive(greenwich + longitude);
      } catch (OrekitException oex) {
        return Errors.printMessage(S.SiderealTime, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_3;
    }

    @Override
    public int status() {
      // returned as an angle in degrees instead of in hours of right ascension
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }

  /**
   * <code>SolarTime(location, date)</code> - local apparent solar time, the hour angle of the Sun
   * plus twelve hours, as a <code>Quantity</code> in hours.
   */
  private static final class SolarTime extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!AstroDataContext.checkAvailable(S.SolarTime, engine)) {
        return F.NIL;
      }
      GeodeticPoint point = null;
      AbsoluteDate date = null;
      for (int i = 1; i < ast.size(); i++) {
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
        return AstroConvert.reportUnreadableArgument(S.SolarTime, arg, ast, engine);
      }
      if (point == null) {
        return F.NIL;
      }
      if (date == null) {
        date = AstroConvert.nowUTC();
      }
      try {
        // the hour angle is the difference in longitude between the observer and the Sun's
        // ground point, measured westwards
        Vector3D sun = CelestialBodyFactory.getSun().getPosition(date, AstroConvert.earthFrame());
        double sunLongitude = sun.getAlpha();
        double hourAngle = point.getLongitude() - sunLongitude;
        double hours = FastMath.toDegrees(hourAngle) / 15.0 + 12.0;
        hours = hours % 24.0;
        if (hours < 0.0) {
          hours += 24.0;
        }
        return F.Quantity(F.num(hours), F.stringx("Hours"));
      } catch (OrekitException oex) {
        return Errors.printMessage(S.SolarTime, "orekitdata", F.List(F.stringx(oex.getMessage())),
            engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  /**
   * <code>TimeSystemConvert(date, "system")</code> - re-express a date in another time system.
   *
   * <p>
   * The instant does not move; only the reading of the clock does. A date read in UTC and converted
   * to TAI is therefore 37 seconds later as of 2017.
   */
  private static final class TimeSystemConvert extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!AstroDataContext.checkAvailable(S.TimeSystemConvert, engine)) {
        return F.NIL;
      }
      AbsoluteDate date = AstroConvert.toAbsoluteDate(ast.arg1());
      if (date == null) {
        return AstroConvert.reportUnreadableArgument(S.TimeSystemConvert, ast.arg1(), ast, engine);
      }
      if (!ast.arg2().isString()) {
        return F.NIL;
      }
      try {
        TimeScale target = timeScale(ast.arg2().toString());
        if (target == null) {
          return Errors.printMessage(S.TimeSystemConvert, "astrotimesys", F.List(ast.arg2(), ast),
              engine);
        }
        return AstroConvert.toDateObject(AstroConvert.toLocalDateTime(date, target));
      } catch (OrekitException oex) {
        return Errors.printMessage(S.TimeSystemConvert, "orekitdata",
            F.List(F.stringx(oex.getMessage())), engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>TimeZoneConvert(date, zone)</code> - re-express a date in another time zone, where the
   * zone is an offset in hours or an IANA zone name.
   */
  private static final class TimeZoneConvert extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      LocalDateTime utc = AstroConvert.toLocalDateTime(ast.arg1());
      if (utc == null) {
        return F.NIL;
      }
      IExpr zone = ast.isAST2() ? ast.arg2() : F.CD0;
      ZoneOffset offset = toZoneOffset(zone, utc);
      if (offset == null) {
        return Errors.printMessage(S.TimeZoneConvert, "astrotimezone", F.List(zone, ast), engine);
      }
      LocalDateTime shifted = utc.plusSeconds(offset.getTotalSeconds());
      return DateObjectExpr.newInstance(shifted,
          DateObjectExpr.newInstance(shifted).getGranularity(), DateObjectExpr.GREGORIAN,
          F.num(offset.getTotalSeconds() / 3600.0), true);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * Read a time zone written either as an offset in hours or as an IANA zone name.
   *
   * @param utc the instant, needed because a named zone's offset depends on the date
   * @return the offset, or <code>null</code> if {@code zone} is not a time zone
   */
  static ZoneOffset toZoneOffset(IExpr zone, LocalDateTime utc) {
    if (zone.isReal()) {
      try {
        return ZoneOffset.ofTotalSeconds((int) FastMath.round(zone.evalf() * 3600.0));
      } catch (DateTimeException dtex) {
        // an offset only runs to +/-18 hours, and anything outside that is not a time zone but a
        // number that happened to be in the argument position; the caller reports it as one
        return null;
      }
    }
    if (zone.isString()) {
      try {
        ZoneId zoneId = ZoneId.of(zone.toString());
        return ZonedDateTime.of(utc, ZoneOffset.UTC).withZoneSameInstant(zoneId).getOffset();
      } catch (RuntimeException rex) {
        return null;
      }
    }
    return null;
  }

  /**
   * <code>LocalTime(date)</code> - the date in the time zone of the host, or in the given one.
   */
  private static final class LocalTime extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      LocalDateTime utc = ast.size() > 1 ? AstroConvert.toLocalDateTime(ast.arg1())
          : LocalDateTime.now(ZoneOffset.UTC);
      if (utc == null) {
        return F.NIL;
      }
      ZoneOffset offset;
      if (ast.isAST2()) {
        offset = toZoneOffset(ast.arg2(), utc);
        if (offset == null) {
          return Errors.printMessage(S.LocalTime, "astrotimezone", F.List(ast.arg2(), ast), engine);
        }
      } else {
        offset = ZonedDateTime.of(utc, ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault())
            .getOffset();
      }
      LocalDateTime shifted = utc.plusSeconds(offset.getTotalSeconds());
      return DateObjectExpr.newInstance(shifted,
          DateObjectExpr.newInstance(shifted).getGranularity(), DateObjectExpr.GREGORIAN,
          F.num(offset.getTotalSeconds() / 3600.0), true);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  /**
   * <code>FromDMS({d, m, s})</code> or <code>FromDMS("dms")</code> - a sexagesimal angle as a
   * decimal number of degrees.
   */
  private static final class FromDMS extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        double[] vector = arg1.toDoubleVector();
        if (vector == null || vector.length < 1 || vector.length > 3) {
          return F.NIL;
        }
        double degrees = vector[0];
        double minutes = vector.length > 1 ? vector[1] : 0.0;
        double seconds = vector.length > 2 ? vector[2] : 0.0;
        // the sign of the whole angle is carried by the first non zero component
        double sign = degrees < 0.0
            || (degrees == 0.0 && (minutes < 0.0 || (minutes == 0.0 && seconds < 0.0))) ? -1.0
                : 1.0;
        double magnitude =
            FastMath.abs(degrees) + FastMath.abs(minutes) / 60.0 + FastMath.abs(seconds) / 3600.0;
        return F.num(sign * magnitude);
      }
      if (arg1.isString()) {
        Double parsed = parseDMS(arg1.toString());
        if (parsed == null) {
          return Errors.printMessage(S.FromDMS, "astrodms", F.List(arg1, ast), engine);
        }
        return F.num(parsed);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * Parse a sexagesimal string such as <code>"5d30m15s"</code>, <code>"5°30'15\""</code> or
   * <code>"52 31 12 N"</code>. A trailing or leading <code>S</code> or <code>W</code> negates.
   *
   * @return the angle in decimal degrees, or <code>null</code> if the string is not sexagesimal
   */
  static Double parseDMS(String text) {
    String normalized = text.trim().toUpperCase(Locale.US);
    double sign = 1.0;
    if (normalized.startsWith("-")) {
      sign = -1.0;
      normalized = normalized.substring(1);
    } else if (normalized.startsWith("+")) {
      normalized = normalized.substring(1);
    }
    // A cardinal direction at either end sets the sign and is then dropped. A trailing letter is
    // only a cardinal when it is set off by a space: "52 31 12 S" is a southern latitude, while
    // the trailing S of "52d31m12s" is the seconds marker.
    for (String cardinal : new String[] {"N", "S", "E", "W"}) {
      boolean leading = normalized.startsWith(cardinal);
      boolean trailing = normalized.length() > 1 && normalized.endsWith(cardinal)
          && Character.isWhitespace(normalized.charAt(normalized.length() - 2));
      if (leading || trailing) {
        if ("S".equals(cardinal) || "W".equals(cardinal)) {
          sign = -sign;
        }
        normalized =
            leading ? normalized.substring(1) : normalized.substring(0, normalized.length() - 1);
        break;
      }
    }
    // every separator is equivalent, so reduce them all to a single space
    String[] parts = normalized.replaceAll("[^0-9.]+", " ").trim().split("\\s+");
    if (parts.length == 0 || parts[0].isEmpty() || parts.length > 3) {
      return null;
    }
    try {
      double total = 0.0;
      double scale = 1.0;
      for (String part : parts) {
        total += Double.parseDouble(part) / scale;
        scale *= 60.0;
      }
      return sign * total;
    } catch (NumberFormatException nfe) {
      return null;
    }
  }

  /** <code>DMSList(angle)</code> - decimal degrees as <code>{degrees, minutes, seconds}</code>. */
  private static final class DMSList extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Double radians = AstroConvert.toRadians(ast.arg1(), engine);
      if (radians == null) {
        return F.NIL;
      }
      SexagesimalAngle angle = new SexagesimalAngle(radians);
      int sign = angle.getSign();
      return F.List(F.ZZ(sign * angle.getDegree()), F.ZZ(angle.getArcMinute()),
          F.num(angle.getArcSecond()));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>DMSString(angle)</code> - decimal degrees as a <code>"52d31m12.00s"</code> string. */
  private static final class DMSString extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Double radians = AstroConvert.toRadians(ast.arg1(), engine);
      if (radians == null) {
        return F.NIL;
      }
      SexagesimalAngle angle = new SexagesimalAngle(radians);
      String text = String.format(Locale.US, "%s%dd%dm%.2fs", angle.getSign() < 0 ? "-" : "",
          angle.getDegree(), angle.getArcMinute(), angle.getArcSecond());
      return F.stringx(text);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private AstroTimeFunctions() {}

  /** Unused, kept so that a future sidereal day constant has an obvious home. */
  static final double SIDEREAL_DAY = 0.99726956633 * Constants.JULIAN_DAY;
}
