package org.matheclipse.astro.system;

import org.junit.jupiter.api.Test;

/**
 * <code>SatelliteData</code> and the satellite forms of the position functions, on Orekit's
 * SGP4/SDP4.
 */
public class SatelliteTest extends AbstractTestCase {

  /**
   * An invented low orbit (catalogue number 99901): epoch 2026-03-15 12:00 UTC, inclination 51.6,
   * node 100, eccentricity 0.0005, perigee argument 30, mean anomaly 45, 15.5 revolutions a day.
   * The lines were written by Orekit's own TLE formatter, so the checksums are right.
   */
  private static final String OWN = "{\"1 99901U 26042A   26074.50000000  .00000000  00000-0 "
      + " 30000-4 0  9993\",\"2 99901  51.6000 100.0000 0005000  30.0000  45.0000 15.50000000 "
      + "10002\"}";

  /** Vallado's SGP4 verification case 00005 (an SDP4-free, highly eccentric orbit). */
  private static final String VALLADO_00005 =
      "{\"1 00005U 58002B   00179.78495062  .00000023  00000-0  28098-4 0  4753\","
          + "\"2 00005  34.2682 348.7242 1859667 331.7664  19.3264 10.82419157413667\"}";

  private static final String HALF_AN_HOUR_LATER = "DateObject({2026,3,15,12,30,0})";

  @Test
  public void testElements() {
    // the mean elements of the set; distances are from the Earth's centre, in miles
    check("SatelliteData(" + OWN + ")", //
        "<|Name->99901,NORADNumber->99901,"
            + "Epoch->DateObject({2026,3,15,12,0,0.0},Instant,Gregorian,0.0),"
            + "Inclination->Quantity(51.6,\"AngularDegrees\"),Eccentricity->0.0005,"
            + "SemimajorAxis->Quantity(4222.133,\"Miles\"),"
            + "ApoapsisDistance->Quantity(4224.244,\"Miles\"),"
            + "PeriapsisDistance->Quantity(4220.022,\"Miles\")|>");
    check("SatelliteData(" + VALLADO_00005 + ", \"NSSDCID\")", //
        "1958-002B");
    // the three-line form carries a name
    check("SatelliteData({\"TESTSAT 1\"," + OWN.substring(1) + ", \"Name\")", //
        "TESTSAT 1");
    check("SatelliteData(" + OWN + ", {\"SemiminorAxis\",\"PeriapsisLongitude\",\"Bogus\"})", //
        "{Quantity(4222.133,\"Miles\"),Quantity(130.0,\"AngularDegrees\"),"
            + "Missing(UnknownProperty,{Satellite,Bogus})}");
    // the osculating period at a date, slightly longer than the mean one of 92.9 minutes
    check("SatelliteData(Entity(\"Satellite\", " + OWN + "), {\"PeriodOsculating\", "
        + HALF_AN_HOUR_LATER + "})", //
        "Quantity(93.02652,\"Minutes\")");
    check("EntityValue(Entity(\"Satellite\", " + OWN + "), \"NORADNumber\")", //
        "99901");
  }

  @Test
  public void testPropagation() {
    // Vallado's tcppver.out at t = 0: v = (1.893841015, 6.405893759, 4.534807250) km/s in TEME,
    // 8.07382 km/s
    check("SatelliteData(" + VALLADO_00005
        + ", {\"Velocity\", DateObject({2000,6,27,18,50,19.733568})})", //
        "Quantity(18060.62,\"Miles\"/\"Hours\")");
    check("SatelliteData(" + OWN + ", {\"ElevationWGS84\",\"Velocity\"}, " + HALF_AN_HOUR_LATER
        + ")", //
        "{Quantity(262.3949,\"Miles\"),Quantity(17127.84,\"Miles\"/\"Hours\")}");
    // a dated property in the list form PlanetData uses
    check("SatelliteData(Entity(\"Satellite\", " + OWN + "), {\"ElevationWGS84\", "
        + HALF_AN_HOUR_LATER + "})", //
        "Quantity(262.3949,\"Miles\")");
    // seen from a location: altitude above the horizon, azimuth and range
    check("SatelliteData(" + OWN + ", {\"Altitude\",\"Azimuth\",\"DistanceFromLocation\"}, "
        + "GeoPosition({51.32, 9.49}), " + HALF_AN_HOUR_LATER + ")", //
        "{Quantity(-44.54823,\"AngularDegrees\"),Quantity(259.1809,\"AngularDegrees\"),"
            + "Quantity(5918.963,\"Miles\")}");
    // without a location and without $GeoLocation there is nothing to look from
    check("SatelliteData(" + OWN + ", \"Altitude\", " + HALF_AN_HOUR_LATER + ")", //
        "Missing(NotAvailable)");
  }

  @Test
  public void testPositionFunctions() {
    // below the horizon of central Germany, on the far side of the Earth
    check("AstroPosition(" + OWN + ", {\"Horizon\", " + HALF_AN_HOUR_LATER
        + ", GeoPosition({51.32, 9.49})})", //
        "{Quantity(259.1809,\"AngularDegrees\"),Quantity(-44.54823,\"AngularDegrees\"),"
            + "Quantity(9.52565*10^6,\"Meters\")}");
    check("AstroDistance(" + OWN + ", GeoPosition({51.32, 9.49}), " + HALF_AN_HOUR_LATER + ")", //
        "Quantity(9.52565*10^6,\"Meters\")");
    // the sub-satellite point agrees with SatelliteData's position
    check("GeoDistance(AstroSubpoint(" + OWN + ", " + HALF_AN_HOUR_LATER + "), "
        + "GeoPosition({-8.9986, -73.56009})) < Quantity(1, \"Meters\")", //
        "True");
  }

  @Test
  public void testGeoPositionOfEntity() {
    // the position now, the same as SatelliteData's; both calls run a few milliseconds apart
    check("GeoDistance(GeoPosition(Entity(\"Satellite\", " + OWN + ")), SatelliteData(" + OWN
        + ", \"Position\")) < Quantity(50, \"Kilometers\")", //
        "True");
    check("Head(GeoPosition(Entity(\"Satellite\", " + OWN + ")))", //
        "GeoPosition");
    // a catalogue number needs a satellite catalogue, which Symja does not have
    check("GeoPosition(Entity(\"Satellite\", \"25544\"))", //
        "GeoPosition(Entity(Satellite,25544))");
  }

  @Test
  public void testErrors() {
    // SatelliteData: {1 bad,2 bad} is not a valid two-line element set: wrong format or checksum
    check("SatelliteData({\"1 bad\",\"2 bad\"})", //
        "SatelliteData({1 bad,2 bad})");
    // rise and set searches step too coarsely for a satellite pass, so they refuse one
    check("Head(AstroRiseSet(" + OWN + ", GeoPosition({51, 9}), DateObject({2026,3,15})))", //
        "AstroRiseSet");
  }
}
