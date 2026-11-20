package org.matheclipse.astro.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.junit.jupiter.api.Test;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.core.numerics.geodesy.ReferenceEllipsoid;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;

/**
 * <code>GeomagneticModelData</code> and <code>GeogravityModelData</code>, and the core
 * <code>GeoPositionXYZ</code> conversion cross-checked against Orekit.
 *
 * <p>
 * The tolerances of the comparisons with reference values say how far another implementation is
 * expected to be off: the magnetic and gravity models used elsewhere carry more spherical harmonic
 * degrees than the bundled WMM-2025 and EIGEN-6S, which shows on the ground and fades with height.
 */
public class GeophysicsTest extends AbstractTestCase {

  /**
   * Rows of the official WMM2025 test value table (NOAA NCEI, WMM2025_TestValues.txt, shipped with
   * the coefficients): decimal year, height above the WGS84 ellipsoid in km, latitude, longitude,
   * then D and I in degrees and X, F in nT.
   */
  @Test
  public void testWorldMagneticModelTestValues() {
    // 2025.0, 28 km, 89 N 121 W: F 56214.419888, X -255.388723, Y -1482.460628,
    // Z 56194.288771, H 1504.298146, D -99.77, I 88.47
    check("GeomagneticModelData(GeoPosition({89, -121, 28000}), DateObject({2025,1,1,0,0,0}))", //
        "<|Magnitude->Quantity(56214.42,\"Nanoteslas\"),"
            + "NorthComponent->Quantity(-255.3887,\"Nanoteslas\"),"
            + "EastComponent->Quantity(-1482.461,\"Nanoteslas\"),"
            + "DownComponent->Quantity(56194.29,\"Nanoteslas\"),"
            + "HorizontalComponent->Quantity(1504.298,\"Nanoteslas\"),"
            + "Declination->Quantity(-99.7746,\"AngularDegrees\"),"
            + "Inclination->Quantity(88.46658,\"AngularDegrees\")|>");
    // 2025.0, 65 km, 43 N 93 E: D 0.50, I 64.10, F 55626.621348
    check("Lookup(GeomagneticModelData(GeoPosition({43, 93, 65000}), DateObject({2025,1,1,0,0,0})), " //
        + "{\"Declination\",\"Inclination\",\"Magnitude\"})", //
        "{Quantity(0.496358,\"AngularDegrees\"),Quantity(64.09661,\"AngularDegrees\"),"
            + "Quantity(55626.62,\"Nanoteslas\")}");
    // 2025.5 (2 July 12:00), 63 km, 26 N 81 E: X 34802.613433, secular variation applied
    check("GeomagneticModelData(GeoPosition({26, 81, 63000}), DateObject({2025,7,2,12,0,0}), " //
        + "\"NorthComponent\")", //
        "Quantity(34802.61,\"Nanoteslas\")");
  }

  @Test
  public void testGeomagneticModels() {
    // reference value 19.823745 degrees
    check("Abs(First(GeomagneticModelData(GeoPosition({-0.18, -78.47, 2800}), "
        + "DateObject({2027,7,1,0,0,0}), \"Inclination\")) - 19.823745) < 0.001", //
        "True");
    // before 2025 IGRF-14: reference values -4.361377 (1950) and 1.496088 (2010) degrees
    check("Abs(First(GeomagneticModelData(GeoPosition({51.32, 9.49}), DateObject({1950,1,1}), "
        + "\"Declination\")) + 4.361377) < 0.001", //
        "True");
    check("Abs(First(GeomagneticModelData(GeoPosition({51.32, 9.49}), DateObject({2010,1,1}), "
        + "\"Declination\")) - 1.496088) < 0.001", //
        "True");
    // the Model suboption of Method forces a model: reference values 3.948422 (IGRF) and
    // 3.938231 (WMM) degrees
    check("Abs(First(GeomagneticModelData(GeoPosition({51.32, 9.49, 0}), DateObject({2026,1,1}), "
        + "\"Declination\", Method->{\"Model\"->\"IGRF\"})) - 3.948422) < 0.001", //
        "True");
    check("Abs(First(GeomagneticModelData(GeoPosition({51.32, 9.49, 0}), DateObject({2026,1,1}), "
        + "\"Declination\", Method->{\"Model\"->\"WMM\"})) - 3.938231) < 0.001", //
        "True");
    // GeomagneticModelData: Calculations for WMM are only valid between the years 2025 and 2030.
    check("GeomagneticModelData(GeoPosition({51.32, 9.49, 0}), DateObject({2010,1,1}), "
        + "\"Declination\", Method->{\"Model\"->\"WMM\"})", //
        "Missing(NotAvailable)");
    // GeomagneticModelData: Improper model specification.
    check("Abs(First(GeomagneticModelData(GeoPosition({51.32, 9.49, 0}), DateObject({2026,1,1}), "
        + "\"Declination\", Method->{\"Model\"->\"Bogus\"})) - 3.938231) < 0.001", //
        "True");
    // GeomagneticModelData: Improper model specification.
    // GeomagneticModelData: Method option IGRF in IGRF is not one of {Model}.
    check("GeomagneticModelData(GeoPosition({51.32, 9.49}), DateObject({2026,1,1}), "
        + "\"Declination\", Method->\"IGRF\")", //
        "Quantity(3.93833,\"AngularDegrees\")");
    // a position without height is on the ellipsoid, not on the ground: reference value
    // 49528.842068 nT at 0 m (49524.915530 nT on the terrain, 172 m higher)
    check("Abs(First(GeomagneticModelData(GeoPosition({51.32, 9.49}), "
        + "DateObject({2026,3,15,12,0,0}), \"Magnitude\")) - 49528.842068) < 0.1", //
        "True");
    // a list of properties is not accepted
    check("Head(GeomagneticModelData(GeoPosition({51, 9}), DateObject({2026,1,1}), "
        + "{\"Declination\",\"Inclination\"}))", //
        "GeomagneticModelData");
    // GeomagneticModelData: Unknown option ModelDegree->3 in GeomagneticModelData.
    check("GeomagneticModelData(GeoPosition({51.32, 9.49}), DateObject({2026,1,1}), "
        + "\"Declination\", \"ModelDegree\"->3)", //
        "Quantity(3.93833,\"AngularDegrees\")");
    // GeomagneticModelData: Calculations for WMM are only valid between the years 2025 and 2030.
    check("GeomagneticModelData(GeoPosition({51, 9}), DateObject({2035,1,1}), \"Declination\")", //
        "Missing(NotAvailable)");
    check("GeomagneticModelData(GeoPosition({51, 9}), DateObject({2026,1,1}), \"Potential\")", //
        "Missing(NotAvailable)");
    check("GeomagneticModelData(GeoPosition({51, 9}), DateObject({2026,1,1}), \"Bogus\")", //
        "GeomagneticModelData(GeoPosition({51.0,9.0,0.0}),DateObject({2026,1,1},Day),Bogus)");
    // a list of positions would be a region, which is not supported
    check("Head(GeomagneticModelData({GeoPosition({51, 9}),GeoPosition({-33, 18})}, "
        + "DateObject({2026,1,1}), \"Declination\"))", //
        "GeomagneticModelData");
  }

  @Test
  public void testGravityModel() {
    check("Keys(GeogravityModelData(GeoPosition({51.32, 9.49})))", //
        "{Magnitude,NorthComponent,EastComponent,DownComponent,HorizontalComponent,"
            + "Declination,Inclination,Potential}");
    // gravity includes the centrifugal acceleration: reference values 9.780421 at the equator
    // and 9.832204 at the pole, where the bundled degree 240 model is a few 10^-4 off
    check("Abs(First(GeogravityModelData(GeoPosition({0, 0, 0}), \"Magnitude\")) - 9.780421)"
        + " < 0.0003", //
        "True");
    check("Abs(First(GeogravityModelData(GeoPosition({90, 0, 0}), \"Magnitude\")) - 9.832204)"
        + " < 0.0003", //
        "True");
    // the potential energy per unit mass, negative, including the centrifugal potential:
    // reference value -6.2637029470*10^7 J/kg
    check("Abs(First(GeogravityModelData(GeoPosition({0, 0, 0}), \"Potential\")) + 6.2637029470*^7)"
        + " < 10", //
        "True");
    // at the height of a low orbit the models agree closely: reference value 8.685057914
    check("Abs(First(GeogravityModelData(GeoPosition({51.32, 9.49, 400000}), \"Magnitude\"))"
        + " - 8.685057914) < 1*^-5", //
        "True");
    // a position without height is on the ellipsoid: reference value 9.812209024 at 0 m
    check("Abs(First(GeogravityModelData(GeoPosition({51.32, 9.49}), \"Magnitude\")) - 9.812209024)"
        + " < 1*^-4", //
        "True");
    check("Head(GeogravityModelData(GeoPosition({0, 0, 0}), {\"NorthComponent\",\"EastComponent\"}))", //
        "GeogravityModelData");
    // GeogravityModelData: Unknown option ModelDegree->2 in GeogravityModelData.
    check("Abs(First(GeogravityModelData(GeoPosition({51.32, 9.49, 400000}), \"Magnitude\", "
        + "\"ModelDegree\"->2)) - 8.685057914) < 1*^-5", //
        "True");
    // a list of positions would be a region, which is not supported
    check("Head(GeogravityModelData({GeoPosition({0, 0}), GeoPosition({45, 45})}, \"Magnitude\"))", //
        "GeogravityModelData");
  }

  /** The core conversion against Orekit's ellipsoid, which knows nothing of it. */
  @Test
  public void testCartesianAgainstOrekit() {
    OneAxisEllipsoid orekit = AstroConvert.earthEllipsoidGeometryOnly();
    ReferenceEllipsoid core = ReferenceEllipsoid.WGS84;
    double[][] positions = {{51.32, 9.49, 150.0}, {-77.85, 166.67, 30.0}, {0.0, -179.5, 0.0},
        {33.3, 44.4, 400000.0}};
    for (double[] p : positions) {
      GeodeticPoint point = AstroConvert.newGeodeticPoint(p[0], p[1], p[2]);
      Vector3D expected = orekit.transform(point);
      double[] xyz = core.toCartesian(p[0], p[1], p[2]);
      assertEquals(0.0, expected.distance(new Vector3D(xyz)), 1.0e-6);
      GeodeticPoint back = orekit.transform(expected, orekit.getBodyFrame(), null);
      double[] geodetic = core.toGeodetic(xyz[0], xyz[1], xyz[2]);
      assertEquals(Math.toDegrees(back.getLatitude()), geodetic[0], 1.0e-10);
      assertEquals(back.getAltitude(), geodetic[2], 1.0e-6);
    }
  }
}
