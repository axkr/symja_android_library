package org.matheclipse.astro.project;

import java.util.Locale;
import org.hipparchus.util.FastMath;

/**
 * Maps a direction on a sphere to a point on a flat chart.
 *
 * <p>
 * Used for both the sky and the Earth. The celestial sphere and the terrestrial one differ only in
 * what gets drawn on them and in which way round the longitude axis runs - a sky chart is mirrored,
 * because you look at the celestial sphere from the inside - so one set of projections serves both.
 *
 * <p>
 * Every projection here is the closed-form textbook formula. A coordinate reference system library
 * would bring an EPSG database and a datum model that neither a star chart nor a whole-world outline
 * has any use for.
 *
 * <p>
 * Angles are radians throughout; the returned coordinates are in projection units, roughly
 * <code>[-2, 2]</code> for the whole-sky projections, and are scaled to the viewport by the caller.
 */
public abstract class MapProjection {

  /** Longitude of the centre of the projection, in radians. */
  protected final double centerLongitude;

  /** Latitude of the centre of the projection, in radians. */
  protected final double centerLatitude;

  protected MapProjection(double centerLongitude, double centerLatitude) {
    this.centerLongitude = centerLongitude;
    this.centerLatitude = centerLatitude;
  }

  /**
   * Project a direction onto the chart.
   *
   * @return <code>{x, y}</code> in projection units, or <code>null</code> when the direction cannot
   *         be shown - which happens on the azimuthal projections for anything on the far side of
   *         the sphere
   */
  public abstract double[] project(double longitude, double latitude);

  /** The name this projection is selected by. */
  public abstract String name();

  /**
   * The direction a chart point shows: the inverse of {@link #project}.
   *
   * @return <code>{longitude, latitude}</code> in radians, the longitude within half a turn of the
   *         centre, or <code>null</code> for a point outside the projected sphere
   */
  public abstract double[] inverse(double x, double y);

  /** The direction at a relative longitude, or <code>null</code> if that is off the sphere. */
  protected double[] direction(double lambda, double latitude) {
    if (!Double.isFinite(lambda) || !Double.isFinite(latitude)
        || FastMath.abs(lambda) > FastMath.PI + 1.0e-9
        || FastMath.abs(latitude) > FastMath.PI / 2.0 + 1.0e-9) {
      return null;
    }
    return new double[] {centerLongitude + lambda, latitude};
  }

  /**
   * Whether a direction is on the visible part of the sphere. The whole-sphere projections show
   * everything; the azimuthal ones do not.
   */
  public boolean isVisible(double longitude, double latitude) {
    return project(longitude, latitude) != null;
  }

  /**
   * Whether this projection tears along the meridian opposite its centre.
   *
   * <p>
   * The whole-sphere projections lay the sphere out as a sheet, so the two sides of that meridian
   * end up at opposite edges of the map and a shape crossing it has to be cut. An azimuthal
   * projection is laid out around its centre instead and has no such edge, so cutting there would
   * only put a gap in an unbroken curve.
   */
  public boolean hasSeam() {
    return true;
  }

  /**
   * Longitude measured from the centre of the projection, wrapped to <code>[-pi, pi]</code> so that
   * a chart centred near the 0/360 seam does not tear.
   */
  protected double relativeLongitude(double longitude) {
    double delta = (longitude - centerLongitude) % (2.0 * FastMath.PI);
    if (delta > FastMath.PI) {
      delta -= 2.0 * FastMath.PI;
    } else if (delta < -FastMath.PI) {
      delta += 2.0 * FastMath.PI;
    }
    return delta;
  }

  /**
   * Build a projection by name.
   *
   * @param name one of the names below, case insensitive
   * @return the projection, or <code>null</code> if the name is not one of them
   */
  public static MapProjection of(String name, double centerLongitude, double centerLatitude) {
    switch (name.toLowerCase(Locale.US)) {
      case "equirectangular":
      case "plate carree":
      case "platecarree":
      case "car":
        return new Equirectangular(centerLongitude, centerLatitude);
      case "mercator":
        return new Mercator(centerLongitude, centerLatitude);
      case "mollweide":
      case "mol":
        return new Mollweide(centerLongitude, centerLatitude);
      case "aitoff":
        return new Aitoff(centerLongitude, centerLatitude);
      case "hammer":
      case "hammeraitoff":
        return new Hammer(centerLongitude, centerLatitude);
      case "orthographic":
      case "sin":
        return new Orthographic(centerLongitude, centerLatitude);
      case "stereographic":
      case "stg":
        return new Stereographic(centerLongitude, centerLatitude);
      case "gnomonic":
      case "tan":
        return new Gnomonic(centerLongitude, centerLatitude);
      case "lambertazimuthal":
      case "lambert":
        return new LambertAzimuthal(centerLongitude, centerLatitude);
      case "sinusoidal":
        return new Sinusoidal(centerLongitude, centerLatitude);
      default:
        return null;
    }
  }

  /** The names {@link #of} accepts, for error messages and documentation. */
  public static String[] names() {
    return new String[] {"Equirectangular", "Mercator", "Mollweide", "Aitoff", "Hammer",
        "Orthographic", "Stereographic", "Gnomonic", "LambertAzimuthal", "Sinusoidal"};
  }

  // ------------------------------------------------------- whole sphere

  /** Longitude and latitude used directly as x and y. Cylindrical, neither equal area nor conformal. */
  static final class Equirectangular extends MapProjection {

    Equirectangular(double centerLongitude, double centerLatitude) {
      super(centerLongitude, centerLatitude);
    }

    @Override
    public double[] project(double longitude, double latitude) {
      return new double[] {relativeLongitude(longitude), latitude};
    }

    @Override
    public double[] inverse(double x, double y) {
      return direction(x, y);
    }

    @Override
    public String name() {
      return "Equirectangular";
    }
  }

  /** Conformal cylindrical. The poles are at infinity, so latitudes are clamped near them. */
  static final class Mercator extends MapProjection {

    /** Beyond about 85 degrees the ordinate runs away; charts conventionally stop there. */
    private static final double LIMIT = FastMath.toRadians(85.0);

    Mercator(double centerLongitude, double centerLatitude) {
      super(centerLongitude, centerLatitude);
    }

    @Override
    public double[] project(double longitude, double latitude) {
      double clamped = FastMath.max(-LIMIT, FastMath.min(LIMIT, latitude));
      return new double[] {relativeLongitude(longitude),
          FastMath.log(FastMath.tan(FastMath.PI / 4.0 + clamped / 2.0))};
    }

    @Override
    public double[] inverse(double x, double y) {
      double latitude = 2.0 * FastMath.atan(FastMath.exp(y)) - FastMath.PI / 2.0;
      return latitude > LIMIT + 1.0e-9 || latitude < -LIMIT - 1.0e-9 ? null
          : direction(x, latitude);
    }

    @Override
    public String name() {
      return "Mercator";
    }
  }

  /**
   * Equal-area pseudocylindrical, the usual choice for a whole-sky map. Needs one Newton solve per
   * point for the auxiliary angle.
   */
  static final class Mollweide extends MapProjection {

    Mollweide(double centerLongitude, double centerLatitude) {
      super(centerLongitude, centerLatitude);
    }

    @Override
    public double[] project(double longitude, double latitude) {
      double theta = auxiliaryAngle(latitude);
      double lambda = relativeLongitude(longitude);
      return new double[] {2.0 * FastMath.sqrt(2.0) / FastMath.PI * lambda * FastMath.cos(theta),
          FastMath.sqrt(2.0) * FastMath.sin(theta)};
    }

    /**
     * Solve <code>2*theta + sin(2*theta) = pi*sin(latitude)</code> for theta.
     *
     * <p>
     * Newton's method, which converges in a handful of steps everywhere except exactly at the poles
     * where the derivative vanishes - those are returned directly.
     */
    private static double auxiliaryAngle(double latitude) {
      if (FastMath.abs(FastMath.abs(latitude) - FastMath.PI / 2.0) < 1.0e-12) {
        return latitude > 0.0 ? FastMath.PI / 2.0 : -FastMath.PI / 2.0;
      }
      double target = FastMath.PI * FastMath.sin(latitude);
      double theta = latitude;
      for (int i = 0; i < 30; i++) {
        double f = 2.0 * theta + FastMath.sin(2.0 * theta) - target;
        double df = 2.0 + 2.0 * FastMath.cos(2.0 * theta);
        if (FastMath.abs(df) < 1.0e-14) {
          break;
        }
        double step = f / df;
        theta -= step;
        if (FastMath.abs(step) < 1.0e-13) {
          break;
        }
      }
      return theta;
    }

    @Override
    public double[] inverse(double x, double y) {
      double sinTheta = y / FastMath.sqrt(2.0);
      if (FastMath.abs(sinTheta) > 1.0) {
        return null;
      }
      double theta = FastMath.asin(sinTheta);
      double latitude =
          FastMath.asin(clamp((2.0 * theta + FastMath.sin(2.0 * theta)) / FastMath.PI));
      double cosTheta = FastMath.cos(theta);
      if (cosTheta < 1.0e-12) {
        return FastMath.abs(x) < 1.0e-9 ? direction(0.0, latitude) : null;
      }
      return direction(FastMath.PI * x / (2.0 * FastMath.sqrt(2.0) * cosTheta), latitude);
    }

    @Override
    public String name() {
      return "Mollweide";
    }
  }

  /** Aitoff: the azimuthal equidistant stretched to two to one. Neither equal area nor conformal. */
  static final class Aitoff extends MapProjection {

    Aitoff(double centerLongitude, double centerLatitude) {
      super(centerLongitude, centerLatitude);
    }

    @Override
    public double[] project(double longitude, double latitude) {
      double lambda = relativeLongitude(longitude);
      double cosLatitude = FastMath.cos(latitude);
      double alpha = FastMath.acos(clamp(cosLatitude * FastMath.cos(lambda / 2.0)));
      // sinc(alpha), taken in the limit at the centre where alpha is zero
      double sinc = FastMath.abs(alpha) < 1.0e-12 ? 1.0 : FastMath.sin(alpha) / alpha;
      return new double[] {2.0 * cosLatitude * FastMath.sin(lambda / 2.0) / sinc,
          FastMath.sin(latitude) / sinc};
    }

    /**
     * Aitoff has no closed-form inverse; Newton's method from the Hammer inverse, which lands close
     * by, converges in a few steps.
     */
    @Override
    public double[] inverse(double x, double y) {
      if (x * x / 4.0 + y * y > FastMath.PI * FastMath.PI / 4.0 + 1.0e-9) {
        // outside the ellipse the whole sphere fills, semi-axes pi and pi/2
        return null;
      }
      double[] start = Hammer.inverseAt(x * 2.0 * FastMath.sqrt(2.0) / FastMath.PI,
          y * FastMath.sqrt(2.0) * 2.0 / FastMath.PI);
      double lambda = start == null ? 0.0 : start[0];
      double latitude = start == null ? 0.0 : start[1];
      double h = 1.0e-7;
      for (int i = 0; i < 40; i++) {
        double[] p = projectRelative(lambda, latitude);
        double fx = p[0] - x;
        double fy = p[1] - y;
        if (FastMath.abs(fx) + FastMath.abs(fy) < 1.0e-12) {
          break;
        }
        double[] pl = projectRelative(lambda + h, latitude);
        double[] pb = projectRelative(lambda, latitude + h);
        double a = (pl[0] - p[0]) / h;
        double b = (pb[0] - p[0]) / h;
        double c = (pl[1] - p[1]) / h;
        double d = (pb[1] - p[1]) / h;
        double det = a * d - b * c;
        if (FastMath.abs(det) < 1.0e-14) {
          break;
        }
        lambda -= (d * fx - b * fy) / det;
        latitude -= (a * fy - c * fx) / det;
        latitude = FastMath.max(-FastMath.PI / 2.0, FastMath.min(FastMath.PI / 2.0, latitude));
      }
      double[] p = projectRelative(lambda, latitude);
      if (FastMath.abs(p[0] - x) + FastMath.abs(p[1] - y) > 1.0e-6) {
        return null;
      }
      return direction(lambda, latitude);
    }

    private static double[] projectRelative(double lambda, double latitude) {
      double cosLatitude = FastMath.cos(latitude);
      double alpha = FastMath.acos(clamp(cosLatitude * FastMath.cos(lambda / 2.0)));
      double sinc = FastMath.abs(alpha) < 1.0e-12 ? 1.0 : FastMath.sin(alpha) / alpha;
      return new double[] {2.0 * cosLatitude * FastMath.sin(lambda / 2.0) / sinc,
          FastMath.sin(latitude) / sinc};
    }

    @Override
    public String name() {
      return "Aitoff";
    }
  }

  /** Hammer: equal area, and the one most often confused with Aitoff. */
  static final class Hammer extends MapProjection {

    Hammer(double centerLongitude, double centerLatitude) {
      super(centerLongitude, centerLatitude);
    }

    @Override
    public double[] project(double longitude, double latitude) {
      double lambda = relativeLongitude(longitude);
      double cosLatitude = FastMath.cos(latitude);
      double denominator =
          FastMath.sqrt(1.0 + cosLatitude * FastMath.cos(lambda / 2.0));
      return new double[] {
          2.0 * FastMath.sqrt(2.0) * cosLatitude * FastMath.sin(lambda / 2.0) / denominator,
          FastMath.sqrt(2.0) * FastMath.sin(latitude) / denominator};
    }

    @Override
    public double[] inverse(double x, double y) {
      double[] relative = inverseAt(x, y);
      return relative == null ? null : direction(relative[0], relative[1]);
    }

    /** The relative longitude and the latitude of a Hammer chart point, or null off the ellipse. */
    static double[] inverseAt(double x, double y) {
      if (x * x / 8.0 + y * y / 2.0 > 1.0 + 1.0e-12) {
        return null;
      }
      double z = FastMath.sqrt(FastMath.max(0.0, 1.0 - x * x / 16.0 - y * y / 4.0));
      return new double[] {2.0 * FastMath.atan2(z * x, 2.0 * (2.0 * z * z - 1.0)),
          FastMath.asin(clamp(z * y))};
    }

    @Override
    public String name() {
      return "Hammer";
    }
  }

  /** Equal-area pseudocylindrical with straight parallels. */
  static final class Sinusoidal extends MapProjection {

    Sinusoidal(double centerLongitude, double centerLatitude) {
      super(centerLongitude, centerLatitude);
    }

    @Override
    public double[] project(double longitude, double latitude) {
      return new double[] {relativeLongitude(longitude) * FastMath.cos(latitude), latitude};
    }

    @Override
    public double[] inverse(double x, double y) {
      double cosLatitude = FastMath.cos(y);
      if (FastMath.abs(y) > FastMath.PI / 2.0 + 1.0e-9) {
        return null;
      }
      if (cosLatitude < 1.0e-12) {
        return FastMath.abs(x) < 1.0e-9 ? direction(0.0, y) : null;
      }
      return direction(x / cosLatitude, y);
    }

    @Override
    public String name() {
      return "Sinusoidal";
    }
  }

  // ---------------------------------------------------------- azimuthal

  /**
   * Azimuthal projections show one hemisphere at most, so they share the cosine of the angular
   * distance from the centre and reject anything with the wrong sign.
   */
  abstract static class Azimuthal extends MapProjection {

    @Override
    public boolean hasSeam() {
      return false;
    }

    Azimuthal(double centerLongitude, double centerLatitude) {
      super(centerLongitude, centerLatitude);
    }

    /** Cosine of the angular distance from the centre of the projection. */
    double cosDistance(double longitude, double latitude) {
      double lambda = relativeLongitude(longitude);
      return FastMath.sin(centerLatitude) * FastMath.sin(latitude)
          + FastMath.cos(centerLatitude) * FastMath.cos(latitude) * FastMath.cos(lambda);
    }

    /**
     * The angular distance from the centre of a point at radius {@code rho} on the chart, or
     * <code>NaN</code> where no direction projects to that radius.
     */
    abstract double distanceAt(double rho);

    @Override
    public double[] inverse(double x, double y) {
      double rho = FastMath.hypot(x, y);
      if (rho < 1.0e-15) {
        return new double[] {centerLongitude, centerLatitude};
      }
      double c = distanceAt(rho);
      if (Double.isNaN(c)) {
        return null;
      }
      double sinC = FastMath.sin(c);
      double cosC = FastMath.cos(c);
      double latitude = FastMath.asin(clamp(cosC * FastMath.sin(centerLatitude)
          + y * sinC * FastMath.cos(centerLatitude) / rho));
      double lambda = FastMath.atan2(x * sinC, rho * FastMath.cos(centerLatitude) * cosC
          - y * FastMath.sin(centerLatitude) * sinC);
      return new double[] {centerLongitude + lambda, latitude};
    }

    /** The two components of the direction within the tangent plane, before radial scaling. */
    double[] tangentPlane(double longitude, double latitude) {
      double lambda = relativeLongitude(longitude);
      double x = FastMath.cos(latitude) * FastMath.sin(lambda);
      double y = FastMath.cos(centerLatitude) * FastMath.sin(latitude)
          - FastMath.sin(centerLatitude) * FastMath.cos(latitude) * FastMath.cos(lambda);
      return new double[] {x, y};
    }
  }

  /** The sphere as seen from infinitely far away: a disk, one hemisphere at a time. */
  static final class Orthographic extends Azimuthal {

    Orthographic(double centerLongitude, double centerLatitude) {
      super(centerLongitude, centerLatitude);
    }

    @Override
    public double[] project(double longitude, double latitude) {
      if (cosDistance(longitude, latitude) < 0.0) {
        return null;
      }
      return tangentPlane(longitude, latitude);
    }

    @Override
    double distanceAt(double rho) {
      return rho > 1.0 ? Double.NaN : FastMath.asin(rho);
    }

    @Override
    public String name() {
      return "Orthographic";
    }
  }

  /** Conformal azimuthal, projected from the far pole. Shows everything but the far point. */
  static final class Stereographic extends Azimuthal {

    Stereographic(double centerLongitude, double centerLatitude) {
      super(centerLongitude, centerLatitude);
    }

    @Override
    public double[] project(double longitude, double latitude) {
      double cosDistance = cosDistance(longitude, latitude);
      // the antipode maps to infinity; stop a little short of it
      if (cosDistance < -0.999999) {
        return null;
      }
      double scale = 2.0 / (1.0 + cosDistance);
      double[] plane = tangentPlane(longitude, latitude);
      return new double[] {scale * plane[0], scale * plane[1]};
    }

    @Override
    double distanceAt(double rho) {
      return 2.0 * FastMath.atan(rho / 2.0);
    }

    @Override
    public String name() {
      return "Stereographic";
    }
  }

  /**
   * Perspective from the centre of the sphere onto a tangent plane: great circles become straight
   * lines. Strictly less than a hemisphere, and it stretches badly toward the edge.
   */
  static final class Gnomonic extends Azimuthal {

    /** Beyond about 80 degrees from the centre the scale is useless. */
    private static final double MINIMUM_COSINE = FastMath.cos(FastMath.toRadians(80.0));

    Gnomonic(double centerLongitude, double centerLatitude) {
      super(centerLongitude, centerLatitude);
    }

    @Override
    public double[] project(double longitude, double latitude) {
      double cosDistance = cosDistance(longitude, latitude);
      if (cosDistance < MINIMUM_COSINE) {
        return null;
      }
      double[] plane = tangentPlane(longitude, latitude);
      return new double[] {plane[0] / cosDistance, plane[1] / cosDistance};
    }

    @Override
    double distanceAt(double rho) {
      double c = FastMath.atan(rho);
      return FastMath.cos(c) < MINIMUM_COSINE ? Double.NaN : c;
    }

    @Override
    public String name() {
      return "Gnomonic";
    }
  }

  /** Equal-area azimuthal. */
  static final class LambertAzimuthal extends Azimuthal {

    LambertAzimuthal(double centerLongitude, double centerLatitude) {
      super(centerLongitude, centerLatitude);
    }

    @Override
    public double[] project(double longitude, double latitude) {
      double cosDistance = cosDistance(longitude, latitude);
      if (cosDistance < -0.999999) {
        return null;
      }
      double scale = FastMath.sqrt(2.0 / (1.0 + cosDistance));
      double[] plane = tangentPlane(longitude, latitude);
      return new double[] {scale * plane[0], scale * plane[1]};
    }

    @Override
    double distanceAt(double rho) {
      return rho > 2.0 ? Double.NaN : 2.0 * FastMath.asin(rho / 2.0);
    }

    @Override
    public String name() {
      return "LambertAzimuthal";
    }
  }

  /** Clamp into the domain of {@code acos}, against rounding just outside it. */
  private static double clamp(double value) {
    return FastMath.max(-1.0, FastMath.min(1.0, value));
  }
}
