package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.numerics.geodesy.ReferenceEllipsoid;

/**
 * <code>GeoPositionXYZ</code> and <code>GeoPositionENU</code>: Earth-centred Cartesian and local
 * east-north-up coordinates, and their conversion back to <code>GeoPosition</code>.
 */
public class GeoPositionXYZTest extends ExprEvaluatorTestCase {

  @Test
  public void testCartesianRoundTrip() {
    ReferenceEllipsoid wgs84 = ReferenceEllipsoid.WGS84;
    double[][] positions = {{0.0, 0.0, 0.0}, {47.123, -122.456, 123.4}, {-33.25, 151.5, -40.0},
        {89.9999, 12.0, 5000.0}, {-90.0, 0.0, 0.0}, {12.5, 179.99, 400000.0},
        {-61.0, -45.0, 35786000.0}};
    for (double[] p : positions) {
      double[] xyz = wgs84.toCartesian(p[0], p[1], p[2]);
      double[] back = wgs84.toGeodetic(xyz[0], xyz[1], xyz[2]);
      assertEquals(p[0], back[0], 1.0e-11, "latitude");
      if (Math.abs(p[0]) < 90.0) {
        assertEquals(p[1], back[1], 1.0e-11, "longitude");
      }
      assertEquals(p[2], back[2], 1.0e-6, "altitude");
    }
    // the equator at longitude 0 and the north pole
    double[] equator = wgs84.toCartesian(0.0, 0.0, 0.0);
    assertEquals(6378137.0, equator[0], 1.0e-9);
    double[] pole = wgs84.toCartesian(90.0, 0.0, 0.0);
    assertEquals(wgs84.semiMinorAxis(), pole[2], 1.0e-9);
    assertEquals(0.0, pole[0], 1.0e-9);
  }

  @Test
  public void testGeoPositionXYZ() {
    // coordinates stay as they are; the datum ITRF00 is implied
    check("GeoPositionXYZ({4000000, 600000, 4900000})", //
        "GeoPositionXYZ({4000000,600000,4900000})");
    // a GeoPosition is in the datum ITRF00, on the GRS80 ellipsoid
    check("Chop(First(GeoPositionXYZ(GeoPosition({51.32, 9.49})))" //
        + " - {3939637.070815547, 658562.3153801959, 4955871.318483929}, 1*^-6)", //
        "{0,0,0}");
    check("GeoPositionXYZ(GeoPosition({90, 0}))[[1, 3]]", //
        "6.35675*10^6");
    // back to latitude, longitude and height, compared as local offsets in meters
    check("Chop(First(GeoPositionENU(GeoPosition(GeoPositionXYZ({4000000, 600000, 4900000})), "
        + "GeoPosition({50.65088246777151, 8.530765609948133, -11653.417216639888}))), 1*^-6)", //
        "{0,0,0}");
    check("GeoPosition(GeoPositionXYZ({6378137, 0, 0}, \"WGS84\"))", //
        "GeoPosition({0.0,0.0,0.0})");
    check("GeoPosition(GeoPositionXYZ({0, 6378237, 0}, \"WGS84\"))", //
        "GeoPosition({0.0,90.0,100.0})");
    check("p = GeoPosition({-33.25, 151.5, 250});" //
        + "Chop(First(GeoPositionENU(GeoPosition(GeoPositionXYZ(p)), p)), 1*^-6)", //
        "{0,0,0}");
    // GeoPositionXYZ: No direct transformation between datums ITRF00 and WGS84 is available.
    check("GeoPositionXYZ(GeoPosition({0, 0}), \"WGS84\")", //
        "GeoPositionXYZ(GeoPosition({0.0,0.0,0.0}),WGS84)");
    // GeoPositionXYZ: Unknown geodetic datum Nonsense.
    check("GeoPositionXYZ(GeoPosition({10, 20}), \"Nonsense\")", //
        "GeoPositionXYZ(GeoPosition({10.0,20.0,0.0}),Nonsense)");
    // symbolic coordinates stay unevaluated
    check("GeoPosition(GeoPositionXYZ({a, b, c}))", //
        "GeoPosition(GeoPositionXYZ({a,b,c}))");
  }

  @Test
  public void testGeoPositionENU() {
    // the reference itself is the origin
    check("ref = GeoPosition({51.32, 9.49, 150});" //
        + "Chop(First(GeoPositionENU(ref, ref)), 1*^-6)", //
        "{0,0,0}");
    // a point straight above the reference is "up"
    check("ref = GeoPosition({51.32, 9.49, 150});" //
        + "Chop(First(GeoPositionENU(GeoPosition({51.32, 9.49, 1150}), ref)), 1*^-6)", //
        "{0,0,1000.0}");
    // a small step east along the equator is almost pure east, and a millimetre below the
    // tangent plane because the Earth curves away from it
    check("ref = GeoPosition({0, 0});" //
        + "Round(First(GeoPositionENU(GeoPosition({0, 0.001}), ref)), 0.001)", //
        "{111.319,0.0,-0.001}");
    // offsets of a nearby point, and back from offsets to a position and to Cartesian coordinates
    check("Chop(First(GeoPositionENU(GeoPosition({51.33, 9.50, 200}), GeoPosition({51.32, 9.49})))"
        + " - {697.0073407942377, 1112.6273127686275, 199.86489502580014}, 1*^-6)", //
        "{0,0,0}");
    check("p = GeoPosition(GeoPositionENU({1000, 2000, 30}, GeoPosition({51.32, 9.49})));"
        + "Chop(First(GeoPositionENU(p, "
        + "GeoPosition({51.33797582568484, 9.504349921652114, 30.391985120576365}))), 1*^-6)", //
        "{0,0,0}");
    check("Chop(First(GeoPositionENU(GeoPositionXYZ({4000000, 600000, 4900000}), "
        + "GeoPosition({51.32, 9.49}))) - {-67713.22049253112, -73857.70526370782, "
        + "-12441.518205532586}, 1*^-6)", //
        "{0,0,0}");
    check("Chop(First(GeoPositionXYZ(GeoPositionENU({1000, 2000, 30}, GeoPosition({51.32, 9.49}))))"
        + " - {3937950.7579437774, 659294.301436628, 4957144.678335539}, 1*^-6)", //
        "{0,0,0}");
    // round trip from local offsets back to a GeoPosition and again
    check("ref = GeoPosition({-12.5, -77.0, 300});" //
        + "Chop(First(GeoPositionENU(GeoPosition(GeoPositionENU({1000, -2000, 30}, ref)), ref))" //
        + " - {1000, -2000, 30}, 1*^-6)", //
        "{0,0,0}");
    check("ref = GeoPosition({-12.5, -77.0, 300});" //
        + "Chop(First(GeoPositionENU(GeoPositionXYZ(GeoPositionENU({1000, -2000, 30}, ref)), ref))" //
        + " - {1000, -2000, 30}, 1*^-6)", //
        "{0,0,0}");
    // the offsets of a list stay as they are
    check("GeoPositionENU({1, 2, 3}, GeoPosition({0, 0}))", //
        "GeoPositionENU({1,2,3},GeoPosition({0.0,0.0,0.0}))");
  }
}
