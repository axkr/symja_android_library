package org.matheclipse.astro.meeus;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * The ported Night Vision core against two kinds of reference values.
 *
 * <ul>
 * <li>The worked examples of Meeus, <i>Astronomical Algorithms</i> 2nd ed., which Night Vision
 * itself was checked against.</li>
 * <li>The regression table at the end of Night Vision's <code>NearSkyDB.java</code>: the positions
 * Night Vision 5.5 displays for 1992-12-20 0h TT, geocentric and with delta T switched off. Those
 * are rounded to 0.1s of right ascension and 1" of declination, so matching them within a rounding
 * step shows the port computes what the original does.</li>
 * </ul>
 */
public class MeeusCoreTest {

  private static final double ARCSEC = Math.PI / 648000;

  /** One second of right ascension in radians. */
  private static final double SEC_RA = 15 * ARCSEC;

  private static double hms(int h, int m, double s) {
    return (h + m / 60.0 + s / 3600.0) * Math.PI / 12;
  }

  private static double dms(int sign, int d, int m, double s) {
    return sign * (d + m / 60.0 + s / 3600.0) * Math.PI / 180;
  }

  private static double normalize(double ra) {
    return ra < 0 ? ra + 2 * Math.PI : ra;
  }

  private static void assertRaDec(double ra, double dec, MeeusEphemeris.Position p, String what) {
    assertEquals(ra, normalize(p.rightAscension()), 0.06 * SEC_RA, what + " right ascension");
    assertEquals(dec, p.declination(), 0.6 * ARCSEC, what + " declination");
  }

  /** Meeus example 22.a, 1987 April 10 0h TD. */
  @Test
  public void testNutation() {
    Nutation n = Nutation.at(2446895.5);
    assertEquals(-3.788, n.deltaPsi() / ARCSEC, 0.001);
    assertEquals(9.443, n.deltaEpsilon() / ARCSEC, 0.001);
    assertEquals((23 * 3600 + 26 * 60 + 27.407), n.meanObliquity() / ARCSEC, 0.001);
    assertEquals((23 * 3600 + 26 * 60 + 36.850), n.trueObliquity() / ARCSEC, 0.001);
  }

  /** Meeus example 12.a, mean sidereal time at Greenwich on 1987 April 10 0h UT. */
  @Test
  public void testSiderealTime() {
    assertEquals(13 + 10 / 60.0 + 46.3668 / 3600, SiderealTime.greenwichHours(2446895.5),
        0.001 / 3600);
    // a local sidereal time is the Greenwich one shifted by the longitude
    assertEquals(13 + 10 / 60.0 + 46.3668 / 3600 - 5, SiderealTime.localHours(2446895.5, -75.0),
        0.001 / 3600);
  }

  /** Converting to azimuth and altitude and back returns the starting point. */
  @Test
  public void testHorizontalRoundTrip() {
    double ra = 1.234, dec = -0.321;
    double[] aa = SiderealTime.toHorizontal(ra, dec, 7.5, 48.0);
    double[] rd = SiderealTime.toEquatorial(aa[0], aa[1], 7.5, 48.0);
    assertEquals(ra, rd[0], 1e-12);
    assertEquals(dec, rd[1], 1e-12);
    // on the meridian, due south, at altitude 90 - latitude + declination
    double[] south = SiderealTime.toHorizontal(7.5 * Math.PI / 12, 0.0, 7.5, 48.0);
    assertEquals(Math.PI, south[0], 1e-12);
    assertEquals(Math.toRadians(42.0), south[1], 1e-12);
  }

  @Test
  public void testDeltaT() {
    // the table value at 2000.0 is 63.83 s
    assertEquals(63.83, DeltaT.seconds(2451544.5), 0.01);
    // the value at 1900.0 is -2.72 s
    assertEquals(-2.72, DeltaT.seconds(2415020.5), 0.02);
    // the table starts at 1620 with 124 s
    assertEquals(124.0, DeltaT.seconds(2312752.5), 0.5);
    // the formula pieces join without jumps
    for (double year : new double[] {948, 1620, 2024}) {
      double jd = 2451545.0 + (year - 2000) * 365.25;
      assertEquals(DeltaT.seconds(jd - 1), DeltaT.seconds(jd + 1), 0.1, "at " + year);
    }
    // and keeps growing into the future
    assertTrue(DeltaT.seconds(2451545.0 + 500 * 365.25) > 500);
  }

  /** Meeus example 25.b, the Sun on 1992 October 13.0 TD with the higher accuracy method. */
  @Test
  public void testSun() {
    MeeusEphemeris.Position p =
        MeeusEphemeris.position(MeeusBody.SUN, 2448908.5, true, true, null);
    assertEquals(198.378178, Math.toDegrees(normalize(p.rightAscension())), 0.0003);
    assertEquals(-7.783871, Math.toDegrees(p.declination()), 0.0003);
    assertEquals(0.99760775, p.distance(), 1e-5);
  }

  /** Meeus example 33.a, Venus on 1992 December 20.0 TD. */
  @Test
  public void testVenus() {
    MeeusEphemeris.Position p =
        MeeusEphemeris.position(MeeusBody.VENUS, 2448976.5, true, true, null);
    assertEquals(hms(21, 4, 41.454), normalize(p.rightAscension()), 0.05 * SEC_RA);
    assertEquals(dms(-1, 18, 53, 16.84), p.declination(), 0.5 * ARCSEC);
    assertEquals(0.910947, p.distance(), 1e-5);
  }

  /** Meeus example 47.a, the Moon on 1992 April 12.0 TD. */
  @Test
  public void testMoon() {
    double[] lbd = MoonTheory.coordinates(2448724.5);
    // the longitude is not reduced to 0..360 degrees, as in Night Vision
    assertEquals(133.162655, Math.toDegrees(normalize(lbd[0] % (2 * Math.PI))), 1e-6);
    assertEquals(-3.229126, Math.toDegrees(lbd[1]), 1e-6);
    assertEquals(368409.7, lbd[2] * MeeusEphemeris.AU_KM, 0.1);
    MeeusEphemeris.Position p =
        MeeusEphemeris.position(MeeusBody.MOON, 2448724.5, true, true, null);
    assertEquals(134.688470, Math.toDegrees(normalize(p.rightAscension())), 1e-5);
    assertEquals(13.768368, Math.toDegrees(p.declination()), 1e-5);
  }

  /** Night Vision's regression table: apparent geocentric positions on 1992-12-20 0h TT. */
  @Test
  public void testNightVisionApparent() {
    double jde = 2448976.5;
    assertRaDec(hms(16, 33, 59.3), dms(-1, 20, 53, 32), apparent(MeeusBody.MERCURY, jde),
        "Mercury");
    assertRaDec(hms(21, 4, 41.5), dms(-1, 18, 53, 17), apparent(MeeusBody.VENUS, jde), "Venus");
    assertRaDec(hms(7, 48, 35.3), dms(1, 24, 35, 35), apparent(MeeusBody.MARS, jde), "Mars");
    assertRaDec(hms(12, 47, 9.6), dms(-1, 3, 41, 55), apparent(MeeusBody.JUPITER, jde),
        "Jupiter");
    assertRaDec(hms(21, 11, 41.8), dms(-1, 17, 15, 41), apparent(MeeusBody.SATURN, jde),
        "Saturn");
    assertRaDec(hms(19, 13, 48.6), dms(-1, 22, 46, 13), apparent(MeeusBody.URANUS, jde),
        "Uranus");
    assertRaDec(hms(19, 17, 14.6), dms(-1, 21, 34, 15), apparent(MeeusBody.NEPTUNE, jde),
        "Neptune");
    assertRaDec(hms(15, 41, 11.2), dms(-1, 5, 5, 57), apparent(MeeusBody.PLUTO, jde), "Pluto");
    assertRaDec(hms(17, 52, 49.9), dms(-1, 23, 25, 46), apparent(MeeusBody.SUN, jde), "Sun");
    MeeusEphemeris.Position moon = apparent(MeeusBody.MOON, jde);
    assertRaDec(hms(14, 23, 33.2), dms(-1, 18, 0, 20), moon, "Moon");
    assertEquals(378437, moon.distance() * MeeusEphemeris.AU_KM, 0.5);
    assertEquals(1.216, apparent(MeeusBody.MERCURY, jde).distance(), 0.0005);
    assertEquals(30.502, apparent(MeeusBody.PLUTO, jde).distance(), 0.0005);
  }

  /** Night Vision's regression table: J2000 geocentric positions on 1992-12-20 0h TT. */
  @Test
  public void testNightVisionJ2000() {
    double jde = 2448976.5;
    assertRaDec(hms(16, 34, 24.4), dms(-1, 20, 54, 25), j2000(MeeusBody.MERCURY, jde),
        "Mercury");
    assertRaDec(hms(7, 48, 58.2), dms(1, 24, 34, 40), j2000(MeeusBody.MARS, jde), "Mars");
    assertRaDec(hms(15, 41, 33.6), dms(-1, 5, 7, 16), j2000(MeeusBody.PLUTO, jde), "Pluto");
    assertRaDec(hms(17, 53, 15.9), dms(-1, 23, 25, 53), j2000(MeeusBody.SUN, jde), "Sun");
    assertRaDec(hms(14, 23, 55.5), dms(-1, 18, 2, 10), j2000(MeeusBody.MOON, jde), "Moon");
  }

  /** A geometric position differs from an astrometric one only by the light-time. */
  @Test
  public void testGeometric() {
    double jde = 2448976.5;
    MeeusEphemeris.Position astrometric =
        MeeusEphemeris.position(MeeusBody.MARS, jde, false, true, null);
    MeeusEphemeris.Position geometric =
        MeeusEphemeris.position(MeeusBody.MARS, jde, false, false, null);
    double separation = angle(astrometric.cartesian(), geometric.cartesian());
    // Mars at 0.65 AU moves by some tens of arc seconds during the 5.4 minutes of light-time
    assertTrue(separation > 1 * ARCSEC && separation < 60 * ARCSEC, "separation " + separation);
    // the Moon and the Sun are not corrected for light-time at all
    assertArrayEquals(MeeusEphemeris.position(MeeusBody.MOON, jde, false, true, null).cartesian(),
        MeeusEphemeris.position(MeeusBody.MOON, jde, false, false, null).cartesian(), 0.0);
  }

  /** Meeus example 40.a: the topocentric position of Mars seen from Palomar. */
  @Test
  public void testParallax() {
    // 2003 August 28, 3h17m UT; Palomar at 33 21'22" N, 116 51'47" W. Night Vision ignores the
    // height above sea level, which only shifts the result by a fraction of an arc second here.
    double jd = 2452879.636805556;
    double jde = jd + 66.0 / 86400;
    MeeusEphemeris.Observer palomar = MeeusEphemeris.Observer.of(33 + 21 / 60.0 + 22 / 3600.0,
        -(116 + 51 / 60.0 + 47 / 3600.0), jd);
    MeeusEphemeris.Position geocentric =
        MeeusEphemeris.position(MeeusBody.MARS, jde, true, true, null);
    MeeusEphemeris.Position topocentric =
        MeeusEphemeris.position(MeeusBody.MARS, jde, true, true, palomar);
    // Meeus: delta alpha = +1.29 s of time, delta delta = -14.1" for this configuration
    assertEquals(1.29 * SEC_RA, topocentric.rightAscension() - geocentric.rightAscension(),
        0.05 * SEC_RA);
    assertEquals(-14.1 * ARCSEC, topocentric.declination() - geocentric.declination(),
        0.6 * ARCSEC);
  }

  /** The magnitude and size formulas at the Venus configuration of Meeus example 41.a. */
  @Test
  public void testPhotometry() {
    // 1992 December 20: r = 0.724604, Delta = 0.910947, R = 0.983824, magnitude -4.2
    assertEquals(-4.2,
        PlanetPhotometry.magnitude(MeeusBody.VENUS, 0.910947, 0.724604, 0.983824, 0, 0, 0), 0.05);
    // the phase angle is 72.96 degrees there
    assertEquals(72.96, PlanetPhotometry.phaseAngle(0.724604, 0.910947, 0.983824), 0.02);
    // Jupiter at 5 AU: 2 * 98.44 / 5 arc seconds
    assertEquals(39.376, PlanetPhotometry.angularDiameter(MeeusBody.JUPITER, 5.0), 1e-9);
    // the Moon at its mean distance spans about half a degree
    double moon = PlanetPhotometry.angularDiameter(MeeusBody.MOON, 384400 / MeeusEphemeris.AU_KM);
    assertEquals(1865, moon, 5);
  }

  @Test
  public void testMoonPhaseGeometry() {
    // full Moon: opposite the Sun, fully lit, and the bright limb faces the Sun's direction
    assertEquals(1.0, MoonTheory.illuminatedFraction(Math.PI, 0.0, 0.0, 0.0), 1e-15);
    assertEquals(0.0, MoonTheory.illuminatedFraction(1.0, 0.2, 1.0, 0.2), 1e-15);
    // Sun due east of the Moon on the equator: bright limb at position angle 90 degrees
    assertEquals(Math.PI / 2, MoonTheory.brightLimbAngle(0.0, 0.0, 0.5, 0.0), 1e-12);
    // Sun due west: 270 degrees, returned as -90
    assertEquals(-Math.PI / 2, MoonTheory.brightLimbAngle(0.5, 0.0, 0.0, 0.0), 1e-12);
  }

  @Test
  public void testSpectralColor() {
    assertArrayEquals(new int[] {202, 215, 255}, SpectralColor.rgb("A1"));
    assertArrayEquals(new int[] {255, 210, 161}, SpectralColor.rgb("K0"));
    assertArrayEquals(new int[] {225, 225, 225}, SpectralColor.rgb(""));
    assertArrayEquals(new int[] {225, 225, 225}, SpectralColor.rgb("?"));
    // the returned array is a copy
    SpectralColor.rgb("A1")[0] = 0;
    assertEquals(202, SpectralColor.rgb("A1")[0]);
  }

  @Test
  public void testHeliocentric() {
    // the Earth is about 1 AU from the Sun, and Neptune about 30
    assertEquals(1.0, MeeusEphemeris.earthHeliocentric(2451545.0)[2], 0.02);
    assertEquals(30.0, MeeusEphemeris.heliocentric(MeeusBody.NEPTUNE, 2451545.0)[2], 0.5);
    assertEquals(30.2, MeeusEphemeris.heliocentric(MeeusBody.PLUTO, 2451545.0)[2], 0.1);
  }

  private static MeeusEphemeris.Position apparent(MeeusBody body, double jde) {
    return MeeusEphemeris.position(body, jde, true, true, null);
  }

  private static MeeusEphemeris.Position j2000(MeeusBody body, double jde) {
    return MeeusEphemeris.position(body, jde, false, true, null);
  }

  private static double angle(double[] a, double[] b) {
    double dot = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    double na = Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
    double nb = Math.sqrt(b[0] * b[0] + b[1] * b[1] + b[2] * b[2]);
    return Math.acos(Math.min(1.0, dot / (na * nb)));
  }
}
