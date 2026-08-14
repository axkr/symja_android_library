package org.matheclipse.core.numerics.geodesy;

/**
 * Thaddeus Vincenty's algorithms for the direct and inverse geodetic problems on an oblate
 * ellipsoid of revolution.
 *
 * <p>
 * The <em>inverse</em> problem determines the geodesic (shortest surface path) between two points:
 * see {@link #inverse}. The <em>direct</em> problem determines the point reached by travelling a
 * given distance along a given initial bearing: see {@link #direct}.
 *
 * <p>
 * All latitudes and longitudes are in <b>degrees</b>, all distances and altitudes in <b>meters</b>,
 * and all azimuths in degrees clockwise from north in the range <code>[0, 360)</code>.
 *
 * <p>
 * This is a port of <code>org.gavaghan.geodesy.GeodeticCalculator</code> (Mike Gavaghan, Apache
 * License 2.0, <a href=
 * "http://www.gavaghan.org/blog/free-source-code/geodesy-library-vincentys-formula/">gavaghan.org</a>),
 * which Symja depended on before this class existed. The floating point operations are kept in
 * their original order — including the degree/radian conversions, which multiply and divide by
 * <code>PI/180</code> rather than using {@link Math#toRadians(double)} — so that results are
 * reproduced bit for bit.
 *
 * @see <a href="http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf">Vincenty's original publication</a>
 */
public final class GeodesicSolver {

  /** Degrees/radians conversion constant. */
  private static final double PI_OVER_180 = Math.PI / 180.0;

  private static final double TWO_PI = 2.0 * Math.PI;

  /** Solution of the inverse geodetic problem. */
  public static final class GeodesicCurve {
    private final double ellipsoidalDistance;
    private final double azimuth;
    private final double reverseAzimuth;

    GeodesicCurve(double ellipsoidalDistance, double azimuth, double reverseAzimuth) {
      this.ellipsoidalDistance = ellipsoidalDistance;
      this.azimuth = azimuth;
      this.reverseAzimuth = reverseAzimuth;
    }

    /** @return length of the geodesic along the ellipsoid surface, in meters */
    public double ellipsoidalDistance() {
      return ellipsoidalDistance;
    }

    /** @return initial bearing at the starting point, in degrees */
    public double azimuth() {
      return azimuth;
    }

    /** @return bearing back towards the starting point as seen from the end point, in degrees */
    public double reverseAzimuth() {
      return reverseAzimuth;
    }
  }

  /** Solution of the three dimensional inverse geodetic problem. */
  public static final class GeodesicMeasurement {
    private final GeodesicCurve curve;
    private final double elevationChange;
    private final double pointToPointDistance;

    GeodesicMeasurement(GeodesicCurve curve, double elevationChange) {
      this.curve = curve;
      this.elevationChange = elevationChange;
      this.pointToPointDistance =
          Math.sqrt(curve.ellipsoidalDistance() * curve.ellipsoidalDistance()
              + elevationChange * elevationChange);
    }

    /** @return the geodesic across the ellipsoid raised to the average altitude */
    public GeodesicCurve curve() {
      return curve;
    }

    /** @return the difference in altitude between the two positions, in meters */
    public double elevationChange() {
      return elevationChange;
    }

    /** @return the straight-through distance between the two positions, in meters */
    public double pointToPointDistance() {
      return pointToPointDistance;
    }
  }

  /** Solution of the direct geodetic problem. */
  public static final class GeodesicPoint {
    private final double latitude;
    private final double longitude;
    private final double finalBearing;

    GeodesicPoint(double latitude, double longitude, double finalBearing) {
      this.latitude = latitude;
      this.longitude = longitude;
      this.finalBearing = finalBearing;
    }

    /** @return latitude of the destination in degrees */
    public double latitude() {
      return latitude;
    }

    /** @return longitude of the destination in degrees */
    public double longitude() {
      return longitude;
    }

    /** @return bearing on arrival at the destination, in degrees */
    public double finalBearing() {
      return finalBearing;
    }
  }

  private GeodesicSolver() {}

  /**
   * Canonicalize a latitude/longitude pair such that <code>-90 &lt;= latitude &lt;= +90</code> and
   * <code>-180 &lt; longitude &lt;= +180</code>.
   *
   * <p>
   * A latitude that runs over a pole flips the longitude by 180 degrees.
   *
   * @param latitude latitude in degrees
   * @param longitude longitude in degrees
   * @return a two element array <code>{latitude, longitude}</code>
   */
  public static double[] canonicalize(double latitude, double longitude) {
    double lat = (latitude + 180) % 360;
    if (lat < 0) {
      lat += 360;
    }
    lat -= 180;

    double lon = longitude;
    if (lat > 90) {
      lat = 180 - lat;
      lon += 180;
    } else if (lat < -90) {
      lat = -180 - lat;
      lon += 180;
    }

    lon = ((lon + 180) % 360);
    if (lon <= 0) {
      lon += 360;
    }
    lon -= 180;

    return new double[] {lat, lon};
  }

  /**
   * Solve the inverse geodetic problem: the geodesic between two points on the ellipsoid.
   *
   * <p>
   * The inputs are canonicalized before the computation.
   *
   * @param ellipsoid reference ellipsoid
   * @param latitude1 latitude of the starting point in degrees
   * @param longitude1 longitude of the starting point in degrees
   * @param latitude2 latitude of the end point in degrees
   * @param longitude2 longitude of the end point in degrees
   * @return the distance together with the forward and reverse azimuths
   */
  public static GeodesicCurve inverse(ReferenceEllipsoid ellipsoid, double latitude1,
      double longitude1, double latitude2, double longitude2) {
    double[] start = canonicalize(latitude1, longitude1);
    double[] end = canonicalize(latitude2, longitude2);
    return curve(ellipsoid, start[0], start[1], end[0], end[1]);
  }

  /**
   * The inverse geodetic problem for coordinates that are already canonicalized.
   *
   * <p>
   * All equation numbers refer to Vincenty's publication.
   */
  private static GeodesicCurve curve(ReferenceEllipsoid ellipsoid, double latitude1,
      double longitude1, double latitude2, double longitude2) {
    double a = ellipsoid.semiMajorAxis();
    double b = ellipsoid.semiMinorAxis();
    double f = ellipsoid.flattening();

    double phi1 = toRadians(latitude1);
    double lambda1 = toRadians(longitude1);
    double phi2 = toRadians(latitude2);
    double lambda2 = toRadians(longitude2);

    double a2 = a * a;
    double b2 = b * b;
    double a2b2b2 = (a2 - b2) / b2;

    double omega = lambda2 - lambda1;

    double tanU1 = (1.0 - f) * Math.tan(phi1);
    double u1 = Math.atan(tanU1);
    double sinU1 = Math.sin(u1);
    double cosU1 = Math.cos(u1);

    double tanU2 = (1.0 - f) * Math.tan(phi2);
    double u2Angle = Math.atan(tanU2);
    double sinU2 = Math.sin(u2Angle);
    double cosU2 = Math.cos(u2Angle);

    double sinU1sinU2 = sinU1 * sinU2;
    double cosU1sinU2 = cosU1 * sinU2;
    double sinU1cosU2 = sinU1 * cosU2;
    double cosU1cosU2 = cosU1 * cosU2;

    // eq. 13
    double lambda = omega;

    double bigA = 0.0;
    double bigB = 0.0;
    double sigma = 0.0;
    double deltasigma = 0.0;
    boolean converged = false;

    for (int i = 0; i < 20; i++) {
      double lambda0 = lambda;

      double sinlambda = Math.sin(lambda);
      double coslambda = Math.cos(lambda);

      // eq. 14
      double sin2sigma = (cosU2 * sinlambda * cosU2 * sinlambda)
          + (cosU1sinU2 - sinU1cosU2 * coslambda) * (cosU1sinU2 - sinU1cosU2 * coslambda);
      double sinsigma = Math.sqrt(sin2sigma);

      // eq. 15
      double cossigma = sinU1sinU2 + (cosU1cosU2 * coslambda);

      // eq. 16
      sigma = Math.atan2(sinsigma, cossigma);

      // eq. 17 Careful! sin2sigma might be almost 0!
      double sinalpha = (sin2sigma == 0) ? 0.0 : cosU1cosU2 * sinlambda / sinsigma;
      double alpha = Math.asin(sinalpha);
      double cosalpha = Math.cos(alpha);
      double cos2alpha = cosalpha * cosalpha;

      // eq. 18 Careful! cos2alpha might be almost 0!
      double cos2sigmam = cos2alpha == 0.0 ? 0.0 : cossigma - 2 * sinU1sinU2 / cos2alpha;
      double u2 = cos2alpha * a2b2b2;

      double cos2sigmam2 = cos2sigmam * cos2sigmam;

      // eq. 3
      bigA = 1.0 + u2 / 16384 * (4096 + u2 * (-768 + u2 * (320 - 175 * u2)));

      // eq. 4
      bigB = u2 / 1024 * (256 + u2 * (-128 + u2 * (74 - 47 * u2)));

      // eq. 6
      deltasigma = bigB * sinsigma * (cos2sigmam + bigB / 4 * (cossigma * (-1 + 2 * cos2sigmam2)
          - bigB / 6 * cos2sigmam * (-3 + 4 * sin2sigma) * (-3 + 4 * cos2sigmam2)));

      // eq. 10
      double c = f / 16 * cos2alpha * (4 + f * (4 - 3 * cos2alpha));

      // eq. 11 (modified)
      lambda = omega + (1 - c) * f * sinalpha
          * (sigma + c * sinsigma * (cos2sigmam + c * cossigma * (-1 + 2 * cos2sigmam2)));

      // see how much improvement we got
      double change = Math.abs((lambda - lambda0) / lambda);

      if ((i > 1) && (change < 0.0000000000001)) {
        converged = true;
        break;
      }
    }

    // eq. 19
    double s = b * bigA * (sigma - deltasigma);
    double alpha1;
    double alpha2;

    if (!converged) {
      // didn't converge? must be N/S
      if (phi1 > phi2) {
        alpha1 = 180.0;
        alpha2 = 0.0;
      } else if (phi1 < phi2) {
        alpha1 = 0.0;
        alpha2 = 180.0;
      } else {
        alpha1 = Double.NaN;
        alpha2 = Double.NaN;
      }
    } else {
      double radians;

      // eq. 20
      radians = Math.atan2(cosU2 * Math.sin(lambda), (cosU1sinU2 - sinU1cosU2 * Math.cos(lambda)));
      if (radians < 0.0) {
        radians += TWO_PI;
      }
      alpha1 = toDegrees(radians);

      // eq. 21
      radians = Math.atan2(cosU1 * Math.sin(lambda), (-sinU1cosU2 + cosU1sinU2 * Math.cos(lambda)))
          + Math.PI;
      if (radians < 0.0) {
        radians += TWO_PI;
      }
      alpha2 = toDegrees(radians);
    }

    if (alpha1 >= 360.0) {
      alpha1 -= 360.0;
    }
    if (alpha2 >= 360.0) {
      alpha2 -= 360.0;
    }

    return new GeodesicCurve(s, alpha1, alpha2);
  }

  /**
   * Solve the direct geodetic problem: the point reached after travelling a given distance from a
   * given start point along a given initial bearing.
   *
   * @param ellipsoid reference ellipsoid
   * @param latitude latitude of the starting point in degrees
   * @param longitude longitude of the starting point in degrees
   * @param startBearing initial bearing in degrees clockwise from north
   * @param distance distance to travel in meters
   * @return the destination together with the bearing on arrival
   */
  public static GeodesicPoint direct(ReferenceEllipsoid ellipsoid, double latitude,
      double longitude, double startBearing, double distance) {
    double[] start = canonicalize(latitude, longitude);
    double startLatitude = start[0];
    double startLongitude = start[1];

    double a = ellipsoid.semiMajorAxis();
    double b = ellipsoid.semiMinorAxis();
    double aSquared = a * a;
    double bSquared = b * b;
    double f = ellipsoid.flattening();
    double phi1 = toRadians(startLatitude);
    double alpha1 = toRadians(startBearing);
    double cosAlpha1 = Math.cos(alpha1);
    double sinAlpha1 = Math.sin(alpha1);
    double s = distance;
    double tanU1 = (1.0 - f) * Math.tan(phi1);
    double cosU1 = 1.0 / Math.sqrt(1.0 + tanU1 * tanU1);
    double sinU1 = tanU1 * cosU1;

    // eq. 1
    double sigma1 = Math.atan2(tanU1, cosAlpha1);

    // eq. 2
    double sinAlpha = cosU1 * sinAlpha1;

    double sin2Alpha = sinAlpha * sinAlpha;
    double cos2Alpha = 1 - sin2Alpha;
    double uSquared = cos2Alpha * (aSquared - bSquared) / bSquared;

    // eq. 3
    double bigA =
        1 + (uSquared / 16384) * (4096 + uSquared * (-768 + uSquared * (320 - 175 * uSquared)));

    // eq. 4
    double bigB = (uSquared / 1024) * (256 + uSquared * (-128 + uSquared * (74 - 47 * uSquared)));

    // iterate until there is a negligible change in sigma
    double deltaSigma;
    double sOverbA = s / (b * bigA);
    double sigma = sOverbA;
    double sinSigma;
    double prevSigma = sOverbA;
    double sigmaM2;
    double cosSigmaM2;
    double cos2SigmaM2;

    for (;;) {
      // eq. 5
      sigmaM2 = 2.0 * sigma1 + sigma;
      cosSigmaM2 = Math.cos(sigmaM2);
      cos2SigmaM2 = cosSigmaM2 * cosSigmaM2;
      sinSigma = Math.sin(sigma);
      double cosSignma = Math.cos(sigma);

      // eq. 6
      deltaSigma = bigB * sinSigma
          * (cosSigmaM2 + (bigB / 4.0) * (cosSignma * (-1 + 2 * cos2SigmaM2) - (bigB / 6.0)
              * cosSigmaM2 * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM2)));

      // eq. 7
      sigma = sOverbA + deltaSigma;

      // break after converging to tolerance
      if (Math.abs(sigma - prevSigma) < 0.0000000000001) {
        break;
      }

      prevSigma = sigma;
    }

    sigmaM2 = 2.0 * sigma1 + sigma;
    cosSigmaM2 = Math.cos(sigmaM2);
    cos2SigmaM2 = cosSigmaM2 * cosSigmaM2;

    double cosSigma = Math.cos(sigma);
    sinSigma = Math.sin(sigma);

    // eq. 8
    double phi2 = Math.atan2(sinU1 * cosSigma + cosU1 * sinSigma * cosAlpha1, (1.0 - f)
        * Math.sqrt(sin2Alpha + Math.pow(sinU1 * sinSigma - cosU1 * cosSigma * cosAlpha1, 2.0)));

    // eq. 9 — atan2 rather than atan, so that paths crossing a pole stay correct
    double lambda =
        Math.atan2(sinSigma * sinAlpha1, (cosU1 * cosSigma - sinU1 * sinSigma * cosAlpha1));

    // eq. 10
    double c = (f / 16) * cos2Alpha * (4 + f * (4 - 3 * cos2Alpha));

    // eq. 11
    double l = lambda - (1 - c) * f * sinAlpha
        * (sigma + c * sinSigma * (cosSigmaM2 + c * cosSigma * (-1 + 2 * cos2SigmaM2)));

    // eq. 12
    double alpha2 = Math.atan2(sinAlpha, -sinU1 * sinSigma + cosU1 * cosSigma * cosAlpha1);

    double[] destination = canonicalize(toDegrees(phi2), startLongitude + toDegrees(l));
    return new GeodesicPoint(destination[0], destination[1], toDegrees(alpha2));
  }

  /**
   * The three dimensional point-to-point distance between two positions above the ellipsoid.
   *
   * <p>
   * The reference ellipsoid is first expanded to pass through the average altitude of the two
   * positions and the geodesic is computed across that surface. The point-to-point distance is then
   * the hypotenuse of the right triangle whose legs are that geodesic length and the difference in
   * altitude.
   *
   * @param ellipsoid reference ellipsoid
   * @param latitude1 latitude of the starting point in degrees
   * @param longitude1 longitude of the starting point in degrees
   * @param altitude1 altitude of the starting point in meters above the ellipsoid
   * @param latitude2 latitude of the end point in degrees
   * @param longitude2 longitude of the end point in degrees
   * @param altitude2 altitude of the end point in meters above the ellipsoid
   * @return the point-to-point distance in meters
   */
  public static double pointToPointDistance(ReferenceEllipsoid ellipsoid, double latitude1,
      double longitude1, double altitude1, double latitude2, double longitude2, double altitude2) {
    return measure(ellipsoid, latitude1, longitude1, altitude1, latitude2, longitude2, altitude2)
        .pointToPointDistance();
  }

  /**
   * The three dimensional inverse geodetic problem between two positions above the ellipsoid.
   *
   * @param ellipsoid reference ellipsoid
   * @param latitude1 latitude of the starting point in degrees
   * @param longitude1 longitude of the starting point in degrees
   * @param altitude1 altitude of the starting point in meters above the ellipsoid
   * @param latitude2 latitude of the end point in degrees
   * @param longitude2 longitude of the end point in degrees
   * @param altitude2 altitude of the end point in meters above the ellipsoid
   * @see #pointToPointDistance
   */
  public static GeodesicMeasurement measure(ReferenceEllipsoid ellipsoid, double latitude1,
      double longitude1, double altitude1, double latitude2, double longitude2, double altitude2) {
    double[] start = canonicalize(latitude1, longitude1);
    double[] end = canonicalize(latitude2, longitude2);

    // average altitude
    double elev12 = (altitude1 + altitude2) / 2.0;

    // average latitude
    double phi1 = toRadians(start[0]);
    double phi2 = toRadians(end[0]);
    double phi12 = (phi1 + phi2) / 2.0;

    // an ellipsoid accommodating the average altitude
    double f = ellipsoid.flattening();
    double a = ellipsoid.semiMajorAxis() + elev12 * (1.0 + f * Math.sin(phi12));
    ReferenceEllipsoid averageEllipsoid = ReferenceEllipsoid.fromAAndF(a, f);

    GeodesicCurve averageCurve = curve(averageEllipsoid, start[0], start[1], end[0], end[1]);

    return new GeodesicMeasurement(averageCurve, altitude2 - altitude1);
  }

  private static double toRadians(double degrees) {
    return degrees * PI_OVER_180;
  }

  private static double toDegrees(double radians) {
    return radians / PI_OVER_180;
  }
}
