package org.matheclipse.core.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRules;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.data.GeoPositionExpr;
import org.matheclipse.core.io.FileSandbox;

/**
 * Where the computer is, as far as a kernel may know without asking anybody.
 *
 * <p>
 * A Java process has no permission-free way to learn its position: the Windows and macOS location
 * services need native code and a user's consent, and an IP address lookup sends the user's address
 * to a third party. This class uses the two sources which need neither, in this order:
 *
 * <ol>
 * <li>a setting, the system property {@value #PROPERTY} or the environment variable
 * {@value #ENVIRONMENT}, written <code>latitude,longitude</code> or
 * <code>latitude,longitude,altitude</code> in degrees and meters;</li>
 * <li>the principal city of the computer's time zone, from the IANA <code>zone.tab</code> bundled
 * in <code>tz/</code>. That is an estimate - within a few degrees in Europe, but a zone such as
 * <code>Asia/Shanghai</code> spans a whole country - so its source is reported as
 * {@value #SOURCE_TIME_ZONE} and a caller can tell.</li>
 * </ol>
 *
 * <p>
 * Both are facts about the host, so they are only used in a kernel for which
 * {@link FileSandbox#isHostVisible(EvalEngine)} holds: a console or a notebook on the user's own
 * machine. On a server they would describe the server rather than its user.
 */
public final class GeoLocations {

  /** The system property holding a configured location. */
  public static final String PROPERTY = "symja.geolocation";

  /** The environment variable holding a configured location, read when the property is unset. */
  public static final String ENVIRONMENT = "SYMJA_GEOLOCATION";

  /** The source of a location assigned to <code>$GeoLocation</code>. */
  public static final String SOURCE_USER = "User";

  /** The source of a location read from {@link #PROPERTY} or {@link #ENVIRONMENT}. */
  public static final String SOURCE_CONFIGURATION = "Configuration";

  /** The source of a location estimated from the time zone. */
  public static final String SOURCE_TIME_ZONE = "TimeZone";

  private static final String ZONE_TAB = "/tz/zone.tab";

  /** A position and where it came from. */
  public static final class Estimate {
    /** Degrees north. */
    public final double latitude;
    /** Degrees east. */
    public final double longitude;
    /** Meters above the ellipsoid, or NaN when not known. */
    public final double altitude;
    /** {@link #SOURCE_CONFIGURATION} or {@link #SOURCE_TIME_ZONE}. */
    public final String source;

    Estimate(double latitude, double longitude, double altitude, String source) {
      this.latitude = latitude;
      this.longitude = longitude;
      this.altitude = altitude;
      this.source = source;
    }
  }

  /** Time zone id to {latitude, longitude}; read on first use. */
  private static volatile Map<String, double[]> zones;

  private GeoLocations() {}

  /** The estimate as a <code>GeoPosition</code>; an unknown altitude is taken as zero. */
  public static GeoPositionExpr toGeoPosition(Estimate estimate) {
    return Double.isNaN(estimate.altitude)
        ? GeoPositionExpr.newInstance(estimate.latitude, estimate.longitude)
        : GeoPositionExpr.newInstance(estimate.latitude, estimate.longitude, estimate.altitude);
  }

  /**
   * The location a kernel falls back on when <code>$GeoLocation</code> is not assigned: the
   * configured one, else the time zone estimate - and nothing outside a host visible kernel.
   *
   * @return the estimate, or <code>null</code>
   */
  public static Estimate automatic(EvalEngine engine) {
    if (!FileSandbox.isHostVisible(engine)) {
      return null;
    }
    Estimate configured = configured();
    return configured != null ? configured : ofTimeZone(ZoneId.systemDefault());
  }

  /**
   * The location in {@link #PROPERTY} or {@link #ENVIRONMENT}, or <code>null</code> when neither
   * is set or the value cannot be read.
   */
  public static Estimate configured() {
    String value = System.getProperty(PROPERTY);
    if (value == null || value.isBlank()) {
      try {
        value = System.getenv(ENVIRONMENT);
      } catch (SecurityException sex) {
        value = null;
      }
    }
    if (value == null || value.isBlank()) {
      return null;
    }
    double[] position = parse(value);
    return position == null ? null
        : new Estimate(position[0], position[1], position[2], SOURCE_CONFIGURATION);
  }

  /**
   * Read <code>latitude,longitude[,altitude]</code>.
   *
   * @return <code>{latitude, longitude, altitude}</code> with altitude NaN when absent, or
   *         <code>null</code> when the text is not a position on Earth
   */
  static double[] parse(String value) {
    String[] parts = value.trim().split("\\s*[,;]\\s*");
    if (parts.length < 2 || parts.length > 3) {
      return null;
    }
    try {
      double latitude = Double.parseDouble(parts[0]);
      double longitude = Double.parseDouble(parts[1]);
      double altitude = parts.length == 3 ? Double.parseDouble(parts[2]) : Double.NaN;
      if (!(Math.abs(latitude) <= 90.0) || !(Math.abs(longitude) <= 180.0)
          || Double.isInfinite(altitude)) {
        return null;
      }
      return new double[] {latitude, longitude, altitude};
    } catch (NumberFormatException nfe) {
      return null;
    }
  }

  /**
   * The principal city of a time zone.
   *
   * <p>
   * A zone missing from <code>zone.tab</code> may be an alias - <code>US/Eastern</code>,
   * <code>Asia/Calcutta</code> - which shares its rules with the zone it links to; the first zone of
   * the table with identical rules is used then. Zones with no place, such as <code>UTC</code> or
   * <code>Etc/GMT+5</code>, have no location.
   *
   * @return the estimate, or <code>null</code>
   */
  public static Estimate ofTimeZone(ZoneId zone) {
    if (zone == null) {
      return null;
    }
    Map<String, double[]> table = zones();
    double[] position = table.get(zone.getId());
    if (position == null && !zone.getId().startsWith("Etc/") && !"UTC".equals(zone.getId())) {
      ZoneRules rules = zone.getRules();
      if (!rules.isFixedOffset()) {
        for (Map.Entry<String, double[]> entry : table.entrySet()) {
          try {
            if (ZoneId.of(entry.getKey()).getRules().equals(rules)) {
              position = entry.getValue();
              break;
            }
          } catch (DateTimeException dte) {
            // a zone newer than this JDK's tzdata
          }
        }
      }
    }
    return position == null ? null
        : new Estimate(position[0], position[1], Double.NaN, SOURCE_TIME_ZONE);
  }

  /**
   * The principal city of the time zone named {@code zoneId}.
   *
   * @return the estimate, or <code>null</code> when the name is no zone or a zone without a place
   */
  public static Estimate ofTimeZone(String zoneId) {
    try {
      return ofTimeZone(ZoneId.of(zoneId));
    } catch (DateTimeException dte) {
      return null;
    }
  }

  private static Map<String, double[]> zones() {
    Map<String, double[]> table = zones;
    if (table == null) {
      synchronized (GeoLocations.class) {
        table = zones;
        if (table == null) {
          zones = table = readZoneTab();
        }
      }
    }
    return table;
  }

  /** Read <code>zone.tab</code>: country code, ISO 6709 coordinates, zone id, comment. */
  private static Map<String, double[]> readZoneTab() {
    Map<String, double[]> table = new LinkedHashMap<String, double[]>();
    try (InputStream in = GeoLocations.class.getResourceAsStream(ZONE_TAB)) {
      if (in == null) {
        return Collections.emptyMap();
      }
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII));
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.isEmpty() || line.charAt(0) == '#') {
          continue;
        }
        String[] fields = line.split("\t");
        if (fields.length < 3) {
          continue;
        }
        double[] position = parseIso6709(fields[1]);
        if (position != null) {
          table.put(fields[2], position);
        }
      }
    } catch (IOException ioe) {
      return Collections.emptyMap();
    }
    return Collections.unmodifiableMap(table);
  }

  /**
   * Read the coordinates of <code>zone.tab</code>: <code>+DDMM+DDDMM</code> or
   * <code>+DDMMSS+DDDMMSS</code>.
   *
   * @return <code>{latitude, longitude}</code> in degrees, or <code>null</code>
   */
  static double[] parseIso6709(String text) {
    int split = Math.max(text.lastIndexOf('+'), text.lastIndexOf('-'));
    if (split <= 0) {
      return null;
    }
    double latitude = sexagesimal(text.substring(0, split), 2);
    double longitude = sexagesimal(text.substring(split), 3);
    if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
      return null;
    }
    return new double[] {latitude, longitude};
  }

  /** <code>+DDMM</code>, <code>+DDMMSS</code> (latitude) or with three degree digits. */
  private static double sexagesimal(String text, int degreeDigits) {
    if (text.length() < 1 + degreeDigits + 2) {
      return Double.NaN;
    }
    double sign = text.charAt(0) == '-' ? -1.0 : 1.0;
    try {
      String digits = text.substring(1);
      int degrees = Integer.parseInt(digits.substring(0, degreeDigits));
      int minutes = Integer.parseInt(digits.substring(degreeDigits, degreeDigits + 2));
      int seconds = digits.length() >= degreeDigits + 4
          ? Integer.parseInt(digits.substring(degreeDigits + 2, degreeDigits + 4))
          : 0;
      return sign * (degrees + minutes / 60.0 + seconds / 3600.0);
    } catch (NumberFormatException nfe) {
      return Double.NaN;
    }
  }
}
