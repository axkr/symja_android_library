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

  /** The IERS 2010 ellipsoid, also known as ITRF00. */
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
      case "ITRF00":
        return IERS2010;
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

  @Override
  public String toString() {
    return "ReferenceEllipsoid[a=" + semiMajorAxis + ", 1/f=" + inverseFlattening + "]";
  }
}
