package org.matheclipse.io.others;

import org.matheclipse.core.numerics.geodesy.GeodesicSolver;
import org.matheclipse.core.numerics.geodesy.ReferenceEllipsoid;

/**
 * Worked examples for {@link GeodesicSolver}, the Vincenty direct and inverse solvers.
 *
 * <p>
 * Adapted from the examples of the Geodesy library by Mike Gavaghan (Apache License 2.0,
 * <a href=
 * "http://www.gavaghan.org/blog/free-source-code/geodesy-library-vincentys-formula/">gavaghan.org</a>),
 * which Symja used before {@link GeodesicSolver} existed.
 */
public class GeodesyJUnit {
  /**
   * Calculate the destination if we start at: Lincoln Memorial in Washington, D.C --> 38.8892N,
   * 77.04978W and travel at 51.7679 degrees for 6179.016136 kilometers
   *
   * <p>
   * WGS84 reference ellipsoid
   */
  static void TwoDimensionalDirectCalculation() {
    // set the direction and distance
    double startBearing = 51.7679;
    double distance = 6179016.13586;

    // find the destination
    GeodesicSolver.GeodesicPoint dest = GeodesicSolver.direct(ReferenceEllipsoid.WGS84, //
        38.88922, -77.04978, startBearing, distance);

    System.out.println("Travel from Lincoln Memorial at 51.767921 deg for 6179.016 km");
    System.out.printf("   Destination: %1.4f%s", dest.latitude(),
        (dest.latitude() > 0) ? "N" : "S");
    System.out.printf(", %1.4f%s\n", dest.longitude(), (dest.longitude() > 0) ? "E" : "W");
    System.out.printf("   End Bearing: %1.2f degrees\n", dest.finalBearing());
  }

  /**
   * Calculate the two-dimensional path from
   *
   * <p>
   * Lincoln Memorial in Washington, D.C --> 38.8892N, 77.04978W
   *
   * <p>
   * to
   *
   * <p>
   * Eiffel Tower in Paris --> 48.85889N, 2.29583E
   *
   * <p>
   * using WGS84 reference ellipsoid
   */
  static void TwoDimensionalCalculation() {
    // calculate the geodetic curve
    GeodesicSolver.GeodesicCurve geoCurve = GeodesicSolver.inverse(ReferenceEllipsoid.WGS84, //
        38.88922, -77.04978, // Lincoln Memorial
        48.85889, 2.29583); // Eiffel Tower
    double ellipseKilometers = geoCurve.ellipsoidalDistance() / 1000.0;
    double ellipseMiles = ellipseKilometers * 0.621371192;

    System.out.println("2-D path from Lincoln Memorial to Eiffel Tower using WGS84");
    System.out.printf("   Ellipsoidal Distance: %1.2f kilometers (%1.2f miles)\n",
        ellipseKilometers, ellipseMiles);
    System.out.printf("   Azimuth:              %1.2f degrees\n", geoCurve.azimuth());
    System.out.printf("   Reverse Azimuth:      %1.2f degrees\n", geoCurve.reverseAzimuth());
  }

  /**
   * Calculate the three-dimensional path from
   *
   * <p>
   * Pike's Peak in Colorado --> 38.840511N, 105.0445896W, 4301 meters
   *
   * <p>
   * to
   *
   * <p>
   * Alcatraz Island --> 37.826389N, 122.4225W, sea level
   *
   * <p>
   * using WGS84 reference ellipsoid
   */
  static void ThreeDimensionalCalculation() {
    GeodesicSolver.GeodesicMeasurement geoMeasurement =
        GeodesicSolver.measure(ReferenceEllipsoid.WGS84, //
            38.840511, -105.0445896, 4301.0, // Pike's Peak
            37.826389, -122.4225, 0.0); // Alcatraz Island

    double p2pKilometers = geoMeasurement.pointToPointDistance() / 1000.0;
    double p2pMiles = p2pKilometers * 0.621371192;
    double elevChangeMeters = geoMeasurement.elevationChange();
    double elevChangeFeet = elevChangeMeters * 3.2808399;

    System.out.println("3-D path from Pike's Peak to Alcatraz Island using WGS84");
    System.out.printf("   Point-to-Point Distance: %1.2f kilometers (%1.2f miles)\n", p2pKilometers,
        p2pMiles);
    System.out.printf("   Elevation change:        %1.1f meters (%1.1f} feet)\n", elevChangeMeters,
        elevChangeFeet);
    System.out.printf("   Azimuth:                 %1.2f degrees\n",
        geoMeasurement.curve().azimuth());
    System.out.printf("   Reverse Azimuth:         %1.2f degrees\n",
        geoMeasurement.curve().reverseAzimuth());
  }

  public static void main(String[] args) {
    TwoDimensionalDirectCalculation();

    System.out.println();

    TwoDimensionalCalculation();

    System.out.println();

    ThreeDimensionalCalculation();

    System.out.println();
  }
}
