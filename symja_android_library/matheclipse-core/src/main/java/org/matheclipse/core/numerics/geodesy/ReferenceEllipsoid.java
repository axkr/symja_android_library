package org.matheclipse.core.numerics.geodesy;

/**
 * An oblate ellipsoid of revolution used as a reference surface for geodetic computations.
 *
 * <p>
 * An instance is fully determined by its semi-major axis <code>a</code> and its flattening
 * <code>f = (a - b) / a</code>. The remaining parameters are derived.
 *
 * <p>
 * This is a port of <code>org.gavaghan.geodesy.Ellipsoid</code> (Mike Gavaghan, Apache License 2.0,
 * <a href=
 * "http://www.gavaghan.org/blog/free-source-code/geodesy-library-vincentys-formula/">gavaghan.org</a>).
 * The derived quantities are computed exactly as in the original so that {@link GeodesicSolver}
 * reproduces its results bit for bit.
 */
public final class ReferenceEllipsoid {

  /** The WGS84 ellipsoid. */
  public static final ReferenceEllipsoid WGS84 = fromAAndInverseF(6378137.0, 298.257223563);

  /** The GRS80 ellipsoid. */
  public static final ReferenceEllipsoid GRS80 = fromAAndInverseF(6378137.0, 298.257222101);

  /** The GRS67 ellipsoid. */
  public static final ReferenceEllipsoid GRS67 = fromAAndInverseF(6378160.0, 298.25);

  /** The ANS ellipsoid. */
  public static final ReferenceEllipsoid ANS = fromAAndInverseF(6378160.0, 298.25);

  /** The WGS72 ellipsoid. */
  public static final ReferenceEllipsoid WGS72 = fromAAndInverseF(6378135.0, 298.26);

  /** The Clarke 1858 ellipsoid. */
  public static final ReferenceEllipsoid CLARKE1858 = fromAAndInverseF(6378293.645, 294.26);

  /** The Clarke 1880 ellipsoid. */
  public static final ReferenceEllipsoid CLARKE1880 = fromAAndInverseF(6378249.145, 293.465);

  /** The IERS 2010 conventions' best estimate of the Earth's equatorial radius and flattening. */
  public static final ReferenceEllipsoid IERS2010 = fromAAndInverseF(6378136.6, 298.25642);

  /** A perfect sphere with the Earth's mean radius. */
  public static final ReferenceEllipsoid SPHERE = fromAAndF(6371000, 0.0);

  private final double semiMajorAxis;

  private final double semiMinorAxis;

  private final double flattening;

  private final double inverseFlattening;

  private ReferenceEllipsoid(double semiMajorAxis, double semiMinorAxis, double flattening,
      double inverseFlattening) {
    this.semiMajorAxis = semiMajorAxis;
    this.semiMinorAxis = semiMinorAxis;
    this.flattening = flattening;
    this.inverseFlattening = inverseFlattening;
  }

  /**
   * Build an ellipsoid from its semi-major axis and its <em>inverse</em> flattening.
   *
   * @param semiMajorAxis semi-major axis in meters
   * @param inverseFlattening inverse flattening ratio <code>1/f</code>
   */
  public static ReferenceEllipsoid fromAAndInverseF(double semiMajorAxis,
      double inverseFlattening) {
    double f = 1.0 / inverseFlattening;
    double b = (1.0 - f) * semiMajorAxis;
    return new ReferenceEllipsoid(semiMajorAxis, b, f, inverseFlattening);
  }

  /**
   * Build an ellipsoid from its semi-major axis and its flattening.
   *
   * @param semiMajorAxis semi-major axis in meters
   * @param flattening flattening ratio <code>f</code>
   */
  public static ReferenceEllipsoid fromAAndF(double semiMajorAxis, double flattening) {
    double inverseF = 1.0 / flattening;
    double b = (1.0 - flattening) * semiMajorAxis;
    return new ReferenceEllipsoid(semiMajorAxis, b, flattening, inverseF);
  }

  /**
   * Look up one of the predefined ellipsoids by name.
   *
   * @param name case insensitive name, e.g. {@code "WGS84"}
   * @return the ellipsoid or <code>null</code> if the name is unknown
   */
  public static ReferenceEllipsoid of(String name) {
    switch (name.toUpperCase()) {
      case "WGS84":
        return WGS84;
      case "GRS80":
        return GRS80;
      case "GRS67":
        return GRS67;
      case "ANS":
        return ANS;
      case "WGS72":
        return WGS72;
      case "CLARKE1858":
        return CLARKE1858;
      case "CLARKE1880":
        return CLARKE1880;
      case "IERS2010":
        return IERS2010;
      case "ITRF00":
        // ITRF coordinates are given on the GRS80 ellipsoid; the IERS 2010 numerical standard
        // a = 6378136.6 m is a best estimate of the Earth's size, not a reference ellipsoid
        return GRS80;
      case "SPHERE":
        return SPHERE;
      default:
        return null;
    }
  }

  /** @return semi-major axis in meters */
  public double semiMajorAxis() {
    return semiMajorAxis;
  }

  /** @return semi-minor axis in meters */
  public double semiMinorAxis() {
    return semiMinorAxis;
  }

  /** @return flattening ratio <code>f = (a - b) / a</code> */
  public double flattening() {
    return flattening;
  }

  /** @return inverse flattening ratio <code>1/f</code> */
  public double inverseFlattening() {
    return inverseFlattening;
  }

  /** @return first eccentricity <code>e = sqrt(2*f - f^2)</code> */
  public double eccentricity() {
    return Math.sqrt(eccentricitySquared());
  }

  /** @return squared first eccentricity <code>e^2 = 2*f - f^2</code> */
  public double eccentricitySquared() {
    return flattening * (2.0 - flattening);
  }

  /**
   * Earth-centred, Earth-fixed Cartesian coordinates of a geodetic position.
   *
   * @param latitude geodetic latitude in degrees
   * @param longitude longitude in degrees
   * @param altitude height above the ellipsoid in meters
   * @return <code>{x, y, z}</code> in meters
   */
  public double[] toCartesian(double latitude, double longitude, double altitude) {
    double phi = Math.toRadians(latitude);
    double lambda = Math.toRadians(longitude);
    double sinPhi = Math.sin(phi);
    double cosPhi = Math.cos(phi);
    double e2 = eccentricitySquared();
    double n = semiMajorAxis / Math.sqrt(1.0 - e2 * sinPhi * sinPhi);
    double r = (n + altitude) * cosPhi;
    return new double[] {r * Math.cos(lambda), r * Math.sin(lambda),
        (n * (1.0 - e2) + altitude) * sinPhi};
  }

  /**
   * Geodetic latitude, longitude and height of Earth-centred, Earth-fixed Cartesian coordinates.
   *
   * <p>
   * The latitude is iterated with the height written as
   * <code>h = p cos(phi) + z sin(phi) - a^2/N</code>, which, unlike <code>p/cos(phi) - N</code>,
   * stays well conditioned at the poles. Each step gains several digits, so a handful of steps
   * reach machine precision anywhere outside the ellipsoid's centre.
   *
   * @return <code>{latitude, longitude, altitude}</code> in degrees, degrees and meters
   */
  public double[] toGeodetic(double x, double y, double z) {
    double e2 = eccentricitySquared();
    double a = semiMajorAxis;
    double p = Math.hypot(x, y);
    double longitude = Math.toDegrees(Math.atan2(y, x));
    if (p == 0.0) {
      // on the polar axis
      double latitude = z >= 0.0 ? 90.0 : -90.0;
      return new double[] {latitude, 0.0, Math.abs(z) - semiMinorAxis};
    }
    double phi = Math.atan2(z, p * (1.0 - e2));
    double h = 0.0;
    for (int i = 0; i < 20; i++) {
      double sinPhi = Math.sin(phi);
      double n = a / Math.sqrt(1.0 - e2 * sinPhi * sinPhi);
      h = p * Math.cos(phi) + z * sinPhi - a * a / n;
      double next = Math.atan2(z, p * (1.0 - e2 * n / (n + h)));
      if (Math.abs(next - phi) < 1.0e-15) {
        phi = next;
        break;
      }
      phi = next;
    }
    double sinPhi = Math.sin(phi);
    double n = a / Math.sqrt(1.0 - e2 * sinPhi * sinPhi);
    h = p * Math.cos(phi) + z * sinPhi - a * a / n;
    return new double[] {Math.toDegrees(phi), longitude, h};
  }

  @Override
  public String toString() {
    return "ReferenceEllipsoid[a=" + semiMajorAxis + ", 1/f=" + inverseFlattening + "]";
  }
}
