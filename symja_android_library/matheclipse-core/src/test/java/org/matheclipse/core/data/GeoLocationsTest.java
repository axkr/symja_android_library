package org.matheclipse.core.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

/** The parsing and the time zone table behind <code>FindGeoLocation</code>. */
public class GeoLocationsTest {

  @Test
  public void testParseSetting() {
    assertArrayEquals(new double[] {52.52, 13.405, Double.NaN}, GeoLocations.parse("52.52,13.405"),
        0.0);
    assertArrayEquals(new double[] {-33.87, 151.21, 58.0},
        GeoLocations.parse(" -33.87 , 151.21 , 58 "), 0.0);
    assertArrayEquals(new double[] {40.0, -74.0, Double.NaN}, GeoLocations.parse("40;-74"), 0.0);
    // not a position on Earth
    assertNull(GeoLocations.parse("95,10"));
    assertNull(GeoLocations.parse("10,190"));
    assertNull(GeoLocations.parse("Berlin"));
    assertNull(GeoLocations.parse("52.5"));
    assertNull(GeoLocations.parse("NaN,10"));
  }

  @Test
  public void testIso6709() {
    // zone.tab writes degrees and minutes, or degrees, minutes and seconds
    assertArrayEquals(new double[] {52.5, 13 + 22 / 60.0},
        GeoLocations.parseIso6709("+5230+01322"), 1e-12);
    assertArrayEquals(new double[] {40 + 42 / 60.0 + 51 / 3600.0, -(74 + 0 / 60.0 + 23 / 3600.0)},
        GeoLocations.parseIso6709("+404251-0740023"), 1e-12);
    assertArrayEquals(new double[] {-(33 + 52 / 60.0), 151 + 13 / 60.0},
        GeoLocations.parseIso6709("-3352+15113"), 1e-12);
  }

  @Test
  public void testTimeZones() {
    GeoLocations.Estimate berlin = GeoLocations.ofTimeZone("Europe/Berlin");
    assertEquals(52.5, berlin.latitude, 1e-9);
    assertEquals(13 + 22 / 60.0, berlin.longitude, 1e-9);
    assertEquals(GeoLocations.SOURCE_TIME_ZONE, berlin.source);
    // zone.tab keeps Amsterdam, which zone1970.tab merges into Brussels
    assertEquals(52 + 22 / 60.0, GeoLocations.ofTimeZone("Europe/Amsterdam").latitude, 1e-9);
    // an alias shares its rules with the zone it links to
    GeoLocations.Estimate calcutta = GeoLocations.ofTimeZone(ZoneId.of("Asia/Calcutta"));
    GeoLocations.Estimate kolkata = GeoLocations.ofTimeZone("Asia/Kolkata");
    assertEquals(kolkata.latitude, calcutta.latitude, 0.0);
    assertEquals(kolkata.longitude, calcutta.longitude, 0.0);
    // zones without a place have no location
    assertNull(GeoLocations.ofTimeZone("UTC"));
    assertNull(GeoLocations.ofTimeZone("Etc/GMT+5"));
    assertNull(GeoLocations.ofTimeZone(ZoneId.of("+02:00")));
    assertNull(GeoLocations.ofTimeZone("Not/AZone"));
  }
}
