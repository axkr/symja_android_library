package org.matheclipse.astro.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;

/**
 * Values for the astronomy functions, checked against published tables where one exists and against
 * internal consistency otherwise.
 *
 * <p>
 * Every date here lies inside the range of the bundled <code>orekit-data</code> files, which is
 * 1990 to 2149 for the DE 440 ephemerides and 1973 to late 2026 for the Earth orientation
 * parameters. Tests which are checked against a published value name the source in a comment.
 */
public class AstroFunctionsTest extends AbstractTestCase {

  /**
   * A date list holding a component that is not a finite number is not a date. Reading one used to
   * narrow the value to an <code>int</code> and hand it to {@link java.time.LocalDateTime}, whose
   * range complaint escaped as a Java exception; a date that cannot be read has to come back as an
   * unevaluated expression naming the part that is at fault.
   *
   * <p>
   * The lists are built rather than parsed, because a parsed <code>Infinity`</code> reads back as a
   * symbol rather than as the machine number that provoked this, and a list holding a symbol never
   * reaches the date conversion at all.
   */
  @Test
  public void testUnreadableDateSpecification() {
    // Expression Infinity cannot be interpreted as a date specification.
    check(F.binaryAST2(S.FindAstroEvent, F.C0, dateSpec(Double.POSITIVE_INFINITY)), //
        "FindAstroEvent[0,{1,1,1,Infinity}]");
    check(F.unaryAST1(S.MoonPhase, dateSpec(Double.NEGATIVE_INFINITY)), //
        "MoonPhase[{1,1,1,-Infinity}]");
    check(F.unaryAST1(S.SiderealTime, dateSpec(Double.NaN)), //
        "SiderealTime[{1,1,1,Indeterminate}]");
    // finite, but outside the calendar: the list as a whole is what cannot be read
    check(F.unaryAST1(S.MoonPhase, F.List(F.ZZ(2026), F.ZZ(13), F.C1, F.C0)), //
        "MoonPhase[{2026,13,1,0}]");
    // that a date which does read still works is what the rest of this class checks
  }

  /** A four element date list whose hour is <code>hour</code>. */
  private static IAST dateSpec(double hour) {
    return F.List(F.C1, F.C1, F.C1, F.num(hour));
  }

  @Test
  public void testSunPosition() {
    // at noon UTC on the Greenwich meridian the Sun is due south
    check("SunPosition(GeoPosition({51.4779,0.0}), DateObject({2000,1,1,12,0,0}))", //
        "{Quantity(179.2111,\"AngularDegrees\"),Quantity(15.48418,\"AngularDegrees\")}");
    // maximum solar altitude at Berlin on the June solstice is 90 - 52.52 + 23.44 = 60.92 degrees
    check("SunPosition(GeoPosition({52.52,13.405}), DateObject({2026,6,21,11,0,0}))", //
        "{Quantity(176.1277,\"AngularDegrees\"),Quantity(60.87449,\"AngularDegrees\")}");
    // solar right ascension and declination at the J2000.0 epoch, 18h44m and -23.03 degrees
    check("SunPosition(DateObject({2000,1,1,12,0,0}), CelestialSystem->\"Equatorial\")", //
        "{Quantity(281.289,\"AngularDegrees\"),Quantity(-23.03325,\"AngularDegrees\")}");
  }

  @Test
  public void testMoonPosition() {
    check("MoonPosition(GeoPosition({52.52,13.405}), DateObject({2026,6,21,11,0,0}))", //
        "{Quantity(92.77592,\"AngularDegrees\"),Quantity(1.56621,\"AngularDegrees\")}");
  }

  @Test
  public void testSunriseSunset() {
    // Berlin on the June solstice: 04:42:37 and 21:33:48 local summer time
    check("Sunrise(GeoPosition({52.52,13.405}), DateObject({2026,6,21}))", //
        "DateObject({2026,6,21,2,42,36.96802},Instant,Gregorian,0.0)");
    check("Sunset(GeoPosition({52.52,13.405}), DateObject({2026,6,21}))", //
        "DateObject({2026,6,21,19,33,48.2999},Instant,Gregorian,0.0)");
    // the definition is the upper limb at the horizon, so the centre is one semidiameter plus
    // the horizontal refraction below it
    check("SunPosition(GeoPosition({52.52,13.405}), DateObject({2026,6,21,2,42,37}))", //
        "{Quantity(47.62601,\"AngularDegrees\"),Quantity(-0.890127,\"AngularDegrees\")}");
  }

  @Test
  public void testSunriseTwilight() {
    check(
        "Sunrise(GeoPosition({52.52,13.405}), DateObject({2026,6,21}), "
            + "ReferenceAltitude->\"Civil\")", //
        "DateObject({2026,6,21,1,52,53.06471},Instant,Gregorian,0.0)");
    // civil dawn is defined as the centre of the Sun at -6 degrees, without refraction
    check("SunPosition(GeoPosition({52.52,13.405}), DateObject({2026,6,21,1,52,53}))", //
        "{Quantity(37.41105,\"AngularDegrees\"),Quantity(-6.0001,\"AngularDegrees\")}");
  }

  @Test
  public void testSunrisePolarDay() {
    // Longyearbyen is in the midnight sun on the solstice, so the next sunrise is in August
    check("Sunrise(GeoPosition({78.22,15.65}), DateObject({2026,6,21}))", //
        "DateObject({2026,8,24,23,23,34.7953},Instant,Gregorian,0.0)");
  }

  @Test
  public void testAstroRiseSet() {
    check("AstroRiseSet(\"Moon\", \"Rise\", GeoPosition({52.52,13.405}), DateObject({2026,6,21}))", //
        "DateObject({2026,6,21,10,43,0.721378},Instant,Gregorian,0.0)");
    // the upper culmination has to fall exactly midway between sunrise and sunset
    check(
        "AstroRiseSet(\"Sun\", \"UpperCulmination\", GeoPosition({52.52,13.405}), "
            + "DateObject({2026,6,21}))", //
        "DateObject({2026,6,21,11,8,12.75198},Instant,Gregorian,0.0)");
  }

  @Test
  public void testAstroRiseSetEventList() {
    // a list of event types gives one date per event
    check(
        "AstroRiseSet(\"Sun\", {\"Rise\", \"Set\"}, GeoPosition({52.52,13.405}), "
            + "DateObject({2026,6,21}))", //
        // the test harness wraps the output line, hence the embedded newline
        "{DateObject({2026,6,21,2,42,36.96802},Instant,Gregorian,0.0),DateObject({2026,6,\n"
            + "21,19,33,48.2999},Instant,Gregorian,0.0)}");
  }

  @Test
  public void testLocalTime() {
    // with an explicit zone the result does not depend on the host time zone
    check("LocalTime(DateObject({2026,6,1,0,0,0}), \"Europe/Berlin\")", //
        "DateObject({2026,6,1,2,0,0.0},Instant,Gregorian,2.0)");
  }

  @Test
  public void testDaylightQ() {
    check("DaylightQ(GeoPosition({52.52,13.405}), DateObject({2026,6,21,12,0,0}))", //
        "True");
    check("DaylightQ(GeoPosition({52.52,13.405}), DateObject({2026,6,21,0,0,0}))", //
        "False");
  }

  @Test
  public void testMoonPhase() {
    check("MoonPhase(DateObject({2026,6,21}))", //
        "0.406064");
    check("MoonPhase(DateObject({2026,6,21}), \"Name\")", //
        "First Quarter");
    // the illumination is 0, 1/2 and 1 at the new, first quarter and full instants
    check("MoonPhase(DateObject({2026,6,15,2,54,43}))", //
        "0.0016748");
    check("MoonPhase(DateObject({2026,6,21,21,56,4}))", //
        "0.501277");
    check("MoonPhase(DateObject({2026,6,29,23,57,24}))", //
        "0.998754");
  }

  @Test
  public void testMoonPhaseDates() {
    // new and full moon of June 2026
    check("NewMoon(DateObject({2026,6,1}))", //
        "DateObject({2026,6,15,2,54,42.58406},Instant,Gregorian,0.0)");
    check("FullMoon(DateObject({2026,6,1}))", //
        "DateObject({2026,6,29,23,57,23.84426},Instant,Gregorian,0.0)");
    check("MoonPhaseDate(\"FirstQuarter\", DateObject({2026,6,1}))", //
        "DateObject({2026,6,21,21,56,3.98996},Instant,Gregorian,0.0)");
    // the quarter is where the elongation is exactly a right angle
    check("MoonPhase(DateObject({2026,6,21,21,56,4}), \"PhaseAngle\")", //
        "1.5708");
  }

  @Test
  public void testLunationNumber() {
    check("LunationNumber(DateObject({2026,6,21}))", //
        "1280");
    check("FromLunationNumber(1000)", //
        "DateObject({2003,10,25,12,50,52.43269},Instant,Gregorian,0.0)");
    check("LunationNumber(FromLunationNumber(1280))", //
        "1280");
  }

  @Test
  public void testSiderealTime() {
    // Greenwich mean sidereal time at J2000.0 is 280.46061837 degrees
    check("SiderealTime(\"MeanTime\", GeoPosition({0,0}), DateObject({2000,1,1,12,0,0}))", //
        "Quantity(280.4621,\"AngularDegrees\")");
    // the apparent time differs from it by the equation of the equinoxes
    check("SiderealTime(GeoPosition({0,0}), DateObject({2000,1,1,12,0,0}))", //
        "Quantity(280.4586,\"AngularDegrees\")");
  }

  @Test
  public void testSolarTime() {
    // apparent solar time at Greenwich is mean time plus the equation of time, about -1.6 min
    check("SolarTime(GeoPosition({0,0}), DateObject({2026,6,21,12,0,0}))", //
        "Quantity(11.96931,\"Hours\")");
  }

  @Test
  public void testTimeSystemConvert() {
    // TAI has been ahead of UTC by 37 leap seconds since 2017
    check("TimeSystemConvert(DateObject({2026,1,1,0,0,0}), \"TAI\")", //
        "DateObject({2026,1,1,0,0,37.0},Instant,Gregorian,0.0)");
    // TT is TAI plus a fixed 32.184 seconds
    check("TimeSystemConvert(DateObject({2026,1,1,0,0,0}), \"TT\")", //
        "DateObject({2026,1,1,0,1,9.184},Instant,Gregorian,0.0)");
  }

  @Test
  public void testTimeZoneConvert() {
    check("TimeZoneConvert(DateObject({2026,1,1,0,0,0}), 2)", //
        "DateObject({2026,1,1,2,0,0.0},Instant,Gregorian,2.0)");
    // summer time, so Berlin is two hours ahead
    check("TimeZoneConvert(DateObject({2026,6,1,0,0,0}), \"Europe/Berlin\")", //
        "DateObject({2026,6,1,2,0,0.0},Instant,Gregorian,2.0)");
  }

  @Test
  public void testFromDMS() {
    check("FromDMS({52,31,12})", //
        "52.52");
    // the trailing s is the seconds marker here, not a southern hemisphere marker
    check("FromDMS(\"52d31m12s\")", //
        "52.52");
    check("FromDMS(\"52 31 12 S\")", //
        "-52.52");
    check("FromDMS(DMSList(-12.3456))", //
        "-12.3456");
    check("FromDMS(DMSString(-12.3456))", //
        "-12.3456");
  }

  @Test
  public void testDMSListAndString() {
    check("DMSList(52.52)", //
        "{52,31,12.0}");
    check("DMSString(52.52)", //
        "52d31m12.00s");
  }

  @Test
  public void testAstroPosition() {
    check("AstroPosition(\"Mars\", \"Equatorial\", DateObject({2026,6,21}))", //
        "{Quantity(51.76583,\"AngularDegrees\"),Quantity(18.37471,\"AngularDegrees\"),Quantity(3.19395*10^11,\"Meters\")}");
  }

  @Test
  public void testAstroDistance() {
    check("AstroDistance(\"Moon\", DateObject({2026,6,21}))", //
        "Quantity(3.83106*10^8,\"Meters\")");
    // the Earth is at perihelion in early January, 1.471*10^11 meters from the Sun
    check("AstroDistance(\"Sun\", DateObject({2026,1,3}))", //
        "Quantity(1.471*10^11,\"Meters\")");
  }

  @Test
  public void testAstroAngularSeparation() {
    check("AstroAngularSeparation(\"Sun\", \"Moon\", DateObject({2026,6,21}))", //
        "Quantity(79.02957,\"AngularDegrees\")");
  }

  @Test
  public void testAstroSubpoint() {
    // on the solstices the subsolar point sits on a tropic, at the obliquity of the ecliptic
    check("AstroSubpoint(\"Sun\", DateObject({2026,6,21,12,0,0}))", //
        "GeoPosition({23.437796666650257,0.4603246720565153,0.0})");
    check("AstroSubpoint(\"Sun\", DateObject({2026,12,21,12,0,0}))", //
        "GeoPosition({-23.436931386226547,-0.47725974793263504,0.0})");
  }

  @Test
  public void testFindAstroEventSeasons() {
    // published UTC instants for 2026: 14:46, 08:24, 00:05 and 20:50
    check("FindAstroEvent(\"MarchEquinox\", DateObject({2026,1,1}))", //
        "DateObject({2026,3,20,14,46,0.8266},Instant,Gregorian,0.0)");
    check("FindAstroEvent(\"JuneSolstice\", DateObject({2026,1,1}))", //
        "DateObject({2026,6,21,8,24,30.33559},Instant,Gregorian,0.0)");
    check("FindAstroEvent(\"SeptemberEquinox\", DateObject({2026,1,1}))", //
        "DateObject({2026,9,23,0,5,13.27611},Instant,Gregorian,0.0)");
    check("FindAstroEvent(\"DecemberSolstice\", DateObject({2026,1,1}))", //
        "DateObject({2026,12,21,20,50,14.22261},Instant,Gregorian,0.0)");
  }

  @Test
  public void testFindAstroEventApsides() {
    // the Earth passes perihelion on 3 January 2026 and aphelion on 6 July
    check("FindAstroEvent({\"Perihelion\",\"Earth\"}, DateObject({2026,1,1}))", //
        "DateObject({2026,1,3,17,15,42.91846},Instant,Gregorian,0.0)");
    check("FindAstroEvent({\"Aphelion\",\"Earth\"}, DateObject({2026,1,1}))", //
        "DateObject({2026,7,6,17,30,2.46302},Instant,Gregorian,0.0)");
    check("FindAstroEvent({\"Perigee\",\"Moon\"}, DateObject({2026,6,1}))", //
        "DateObject({2026,6,14,23,19,41.04326},Instant,Gregorian,0.0)");
  }

  @Test
  public void testFindAstroEventConfigurations() {
    // Mars comes to opposition on 19 February 2027
    check("FindAstroEvent({\"Opposition\",\"Mars\"}, DateObject({2026,1,1}))", //
        "DateObject({2027,2,19,15,50,55.1675},Instant,Gregorian,0.0)");
    // Venus reaches inferior conjunction on 24 October 2026
    check("FindAstroEvent({\"InferiorConjunction\",\"Venus\"}, DateObject({2026,1,1}))", //
        "DateObject({2026,10,24,3,44,6.89999},Instant,Gregorian,0.0)");
    // and Mercury its greatest eastern elongation on 19 February 2026
    check("FindAstroEvent({\"GreatestEasternElongation\",\"Mercury\"}, DateObject({2026,1,1}))", //
        "DateObject({2026,2,19,17,30,32.52457},Instant,Gregorian,0.0)");
    // an inner planet is never opposite the Sun
    check("FindAstroEvent({\"Opposition\",\"Venus\"}, DateObject({2026,1,1}))", //
        "Missing");
  }

  @Test
  public void testFindAstroEventDelegates() {
    // the moon phase and rise/set events agree with the dedicated functions
    check("FindAstroEvent(\"NewMoon\", DateObject({2026,6,1}))", //
        "DateObject({2026,6,15,2,54,42.58406},Instant,Gregorian,0.0)");
    check("FindAstroEvent(\"Sunrise\", DateObject({2026,6,21}), GeoPosition({52.52,13.405}))", //
        "DateObject({2026,6,21,2,42,36.96802},Instant,Gregorian,0.0)");
  }

  @Test
  public void testFindAstroEventUnknown() {
    // an unusable specification is reported and leaves the expression unevaluated
    check("FindAstroEvent(\"Nonsense\", DateObject({2026,1,1}))", //
        "FindAstroEvent(Nonsense,DateObject({2026,1,1},Day))");
  }

  @Test
  public void testOrbitalElements() {
    // Mars: eccentricity 0.0934, semimajor axis 1.5237 au, inclination 1.850 degrees
    check("OrbitalElements(\"Mars\", \"Eccentricity\", DateObject({2026,1,1}))", //
        "0.093572");
    check("OrbitalElements(\"Mars\", \"SemimajorAxis\", DateObject({2026,1,1}))", //
        "Quantity(2.27975*10^11,\"Meters\")");
    check("OrbitalElements(\"Mars\", \"Inclination\", DateObject({2026,1,1}))", //
        "Quantity(1.84944,\"AngularDegrees\")");
    // UnitSystem->None drops the units
    check("OrbitalElements(\"Mars\", \"SemimajorAxis\", DateObject({2026,1,1}), UnitSystem->None)", //
        "2.27975*10^11");
  }

  @Test
  public void testOrbitalElementsSelection() {
    check("OrbitalElements(\"Mars\", {\"Eccentricity\",\"Inclination\"}, DateObject({2026,1,1}))", //
        "{0.093572,Quantity(1.84944,\"AngularDegrees\")}");
    // without an element specification the default set comes back as an association
    check("Keys(OrbitalElements(\"Mars\", DateObject({2026,1,1})))", //
        "{SemimajorAxis,Eccentricity,Inclination,AscendingNodeLongitude,PeriapsisArgument,MeanAnomaly}");
    // the Moon's elements are referred to the Earth, not to the Sun
    check("OrbitalElements(\"Moon\", \"SemimajorAxis\", DateObject({2026,1,1}))", //
        "Quantity(3.89891*10^8,\"Meters\")");
  }

  @Test
  public void testDayNightTerminator() {
    check("Head(DayNightTerminator(DateObject({2026,6,21,12,0,0})))", //
        "Line");
    check("Length(First(DayNightTerminator(DateObject({2026,6,21,12,0,0}))))", //
        "361");
    // the curve is sampled by longitude from one edge of the map to the other, which is what
    // makes it closeable into a fillable polygon
    check("Part(First(DayNightTerminator(DateObject({2026,6,21,12,0,0}))), 1)", //
        "GeoPosition({65.77651083326467,-179.999999,0.0})");
    // on the June solstice it reaches the midnight sun limit, 66.56 degrees less the 0.83 of
    // solar semidiameter and refraction
    check("Part(First(DayNightTerminator(DateObject({2026,6,21,12,0,0}))), -1)", //
        "GeoPosition({65.77651082721516,179.999999,0.0})");
    // and on the central meridian it is the subsolar latitude less the cap radius
    check("Part(First(DayNightTerminator(DateObject({2026,6,21,12,0,0}))), 181)", //
        "GeoPosition({-67.34654615420422,2.842170943040401E-14,0.0})");
  }

  @Test
  public void testHemispheres() {
    check("Head(DayHemisphere(DateObject({2026,6,21,12,0,0})))", //
        "Polygon");
    check("Head(NightHemisphere(DateObject({2026,6,21,12,0,0})))", //
        "Polygon");
    // A cap containing a pole does not project to a simple closed polygon, so each hemisphere is
    // closed along the pole edge it encloses. On the June solstice the lit cap takes the north
    // pole and the dark one the south; in December they swap.
    check("Take(First(DayHemisphere(DateObject({2026,6,21,12,0,0}))), -2)", //
        "{GeoPosition({90.0,179.999999,0.0}),GeoPosition({90.0,-179.999999,0.0})}");
    check("Take(First(NightHemisphere(DateObject({2026,6,21,12,0,0}))), -2)", //
        "{GeoPosition({-90.0,179.999999,0.0}),GeoPosition({-90.0,-179.999999,0.0})}");
    check("Take(First(DayHemisphere(DateObject({2026,12,21,12,0,0}))), -2)", //
        "{GeoPosition({-90.0,179.999999,0.0}),GeoPosition({-90.0,-179.999999,0.0})}");
  }

  @Test
  public void testSolarEclipse() {
    // NASA canon, annular eclipse of 17 February 2026: greatest eclipse 12:12 UT, gamma -0.9743,
    // magnitude 0.9630
    check("SolarEclipse(DateObject({2026,1,1}))", //
        "DateObject({2026,2,17,12,12,35.77337},Instant,Gregorian,0.0)");
    check("SolarEclipse(DateObject({2026,1,1}), \"Type\")", //
        "Annular");
    check("SolarEclipse(DateObject({2026,1,1}), \"Gamma\")", //
        "-0.973675");
    check("SolarEclipse(DateObject({2026,1,1}), \"MaximumEclipseMagnitude\")", //
        "0.963844");
  }

  @Test
  public void testSolarEclipseTotal() {
    // NASA canon, total eclipse of 12 August 2026: 17:46 UT, gamma 0.8977, magnitude 1.0386
    check("SolarEclipse(DateObject({2026,3,1}))", //
        "DateObject({2026,8,12,17,46,30.96522},Instant,Gregorian,0.0)");
    check("SolarEclipse(DateObject({2026,3,1}), \"Type\")", //
        "Total");
    check("SolarEclipse(DateObject({2026,3,1}), \"Gamma\")", //
        "0.897203");
    check("SolarEclipse(DateObject({2026,3,1}), \"MaximumEclipseMagnitude\")", //
        "1.03955");
    // the whole Sun is hidden, so the obscuration is complete
    check("SolarEclipse(DateObject({2026,3,1}), \"MaximumEclipseObscuration\")", //
        "1.0");
    check("SolarEclipse(DateObject({2026,3,1}), \"Central\")", //
        "True");
  }

  @Test
  public void testSolarEclipsePastEvents() {
    // NASA canon: 8 April 2024 at 18:17 UT with gamma 0.3431, and 21 August 2017 at 18:26 UT
    check("SolarEclipse(DateObject({2024,1,1}), EclipseType->\"Total\")", //
        "DateObject({2024,4,8,18,17,53.66021},Instant,Gregorian,0.0)");
    check("SolarEclipse(DateObject({2024,1,1}), \"Gamma\", EclipseType->\"Total\")", //
        "0.34369");
    check("SolarEclipse(DateObject({2017,1,1}), EclipseType->\"Total\")", //
        "DateObject({2017,8,21,18,26,6.56077},Instant,Gregorian,0.0)");
  }

  @Test
  public void testSolarEclipseSearchDirection() {
    // EclipseType skips the annular eclipse of February and finds the total one of August
    check("SolarEclipse(DateObject({2026,1,1}), EclipseType->\"Total\")", //
        "DateObject({2026,8,12,17,46,30.96487},Instant,Gregorian,0.0)");
    // and TimeDirection searches backwards
    check("SolarEclipse(DateObject({2026,3,1}), TimeDirection->-1)", //
        "DateObject({2026,2,17,12,12,35.77351},Instant,Gregorian,0.0)");
  }

  @Test
  public void testLunarEclipse() {
    // NASA canon, total lunar eclipse of 3 March 2026: greatest eclipse 11:34 UT,
    // umbral magnitude 1.1516
    check("LunarEclipse(DateObject({2026,1,1}))", //
        "DateObject({2026,3,3,11,34,20.72621},Instant,Gregorian,0.0)");
    check("LunarEclipse(DateObject({2026,1,1}), \"Type\")", //
        "Total");
    check("LunarEclipse(DateObject({2026,1,1}), \"UmbralMagnitude\")", //
        "1.15806");
    // the two total lunar eclipses of 2025, on 14 March and 7 September
    check("LunarEclipse(DateObject({2025,1,1}), EclipseType->\"Total\")", //
        "DateObject({2025,3,14,6,59,29.34597},Instant,Gregorian,0.0)");
    check("LunarEclipse(DateObject({2025,5,1}), EclipseType->\"Total\")", //
        "DateObject({2025,9,7,18,12,24.25305},Instant,Gregorian,0.0)");
  }

  @Test
  public void testFindSolarEclipse() {
    // the same search as SolarEclipse, written the other way round
    check("FindSolarEclipse(DateObject({2026,1,1}))", //
        "DateObject({2026,2,17,12,12,35.77337},Instant,Gregorian,0.0)");
  }

  @Test
  public void testEclipseUnsupportedProperty() {
    // the Besselian element properties need an eclipse canon and are reported rather than guessed
    check("SolarEclipse(DateObject({2026,1,1}), \"BesselianElementsCoefficients\")", //
        "SolarEclipse(DateObject({2026,1,1},Day),BesselianElementsCoefficients)");
  }

  @Test
  public void testAstroGraphicsRenders() {
    // the chart is an ordinary Graphics, so the existing SVG pipeline draws it
    check("Head(AstroGraphics())", //
        "Graphics");
    // one point per star down to the magnitude limit of a whole-sky chart
    check("Count(AstroGraphics(), _Point, Infinity)", //
        "5044");
    // a narrow view draws far fewer stars, not more - without the range filter the whole
    // catalogue lands off-canvas instead
    check(
        "Count(AstroGraphics(AstroCenter->{83,0}, "
            + "AstroRange->Quantity(10,\"AngularDegrees\")), _Point, Infinity)", //
        "449");
    // an azimuthal projection shows one hemisphere, so about half the stars
    check(
        "Count(AstroGraphics(AstroProjection->\"Orthographic\", "
            + "AstroRange->Quantity(89,\"AngularDegrees\")), _Point, Infinity)", //
        "2434");
  }

  @Test
  public void testAstroGraphicsRejectsBadOptions() {
    check("AstroGraphics(AstroProjection->\"Nonsense\")", //
        "AstroGraphics(AstroProjection->Nonsense)");
    check("AstroGraphics(AstroRange->Quantity(400,\"AngularDegrees\"))", //
        "AstroGraphics(AstroRange->Quantity(400,\"AngularDegrees\"))");
  }

  @Test
  public void testAstroGraphicsLayers() {
    // every group of primitives is a named layer, so a chart can be taken apart again
    check("Cases(AstroGraphics(), Annotation(_,n_,_) :> n, Infinity)", //
        "{AstroBackground,MainPlanes,AstroGridLines,Constellations,DeepSkyObjects,AstroStars}");
    // Symja's own namespace
    check("Union(Cases(AstroGraphics(), Annotation(_,_,ns_) :> ns, Infinity))", //
        "{SymjaAstroGraphics}");
    // Annotation renders its first argument and ignores the rest, so the stars are still drawn
    check(
        "Count(Cases(AstroGraphics(), Annotation(g_,\"AstroStars\",_) :> g, Infinity), "
            + "_Point, Infinity)", //
        "5044");
    // the deep sky objects are circles rather than points, so an extended object does not read
    // as one more star
    check("Count(AstroGraphics(), _Circle, Infinity)", //
        "206");
    // the catalogue records the Lynds opacity class of a dark nebula in the same field as a
    // magnitude, and writes an unknown magnitude as 999 - neither is a brightness, and taking
    // them for one used to admit two thousand objects to a naked eye chart
    check(
        "Count(AstroGraphics(AstroCenter->{83.8,-5.4}, "
            + "AstroRange->Quantity(12,\"AngularDegrees\")), _Circle, Infinity)", //
        "18");
  }

  @Test
  public void testAstroGraphicsReferenceFrames() {
    String horizon = "AstroGraphics(AstroReferenceFrame -> {\"Horizon\", "
        + "DateObject({2022,10,23,15,0,0}), GeoPosition({52.52,13.405})})";
    // the frame is now honoured rather than being read only for its date
    check("Cases(" + horizon + ", Rule(MetaInformation, m_) :> m[\"ReferenceFrame\"], Infinity)", //
        "{Horizon}");
    // a horizon chart is a planisphere: zenith centred, ninety degrees to the horizon
    check("Cases(" + horizon + ", Rule(MetaInformation, m_) :> m[\"Projection\"], Infinity)", //
        "{LambertAzimuthal}");
    check("Cases(" + horizon + ", Rule(MetaInformation, m_) :> m[\"Center\"], Infinity)", //
        "{{Quantity(0.0,\"AngularDegrees\"),Quantity(90.0,\"AngularDegrees\")}}");
    // half the sky is below the horizon and is not drawn
    check("Count(" + horizon + ", _Point, Infinity) < Count(AstroGraphics(), _Point, Infinity)", //
        "True");
    // the galactic frame has no Orekit frame behind it, so it is worth checking it resolves
    check(
        "Cases(AstroGraphics(AstroReferenceFrame -> \"Galactic\"), "
            + "Rule(MetaInformation, m_) :> m[\"ReferenceFrame\"], Infinity)", //
        "{Galactic}");
    check("Head(AstroGraphics(AstroReferenceFrame -> \"Ecliptic\"))", //
        "Graphics");
    // a horizon frame with nowhere to stand reports and stays unevaluated rather than guessing
    check("AstroGraphics(AstroReferenceFrame -> \"Horizon\")", //
        "AstroGraphics(AstroReferenceFrame->Horizon)");
    check("AstroGraphics(AstroReferenceFrame -> \"Nonsense\")", //
        "AstroGraphics(AstroReferenceFrame->Nonsense)");
  }

  @Test
  public void testAstroGraphicsUsesGeoLocation() {
    // Set and use in one evaluation: EvalEngine.init() drops the dollar symbol values, and the
    // script engine re-initialises between evaluations, so an assignment does not survive to the
    // next check
    check("($GeoLocation = GeoPosition({52.52,13.405}); Head($GeoLocation))", //
        "GeoPosition");
    // with an observer on record a bare "Horizon" is enough
    check(
        "($GeoLocation = GeoPosition({52.52,13.405}); "
            + "Cases(AstroGraphics(AstroReferenceFrame -> \"Horizon\"), "
            + "Rule(MetaInformation, m_) :> m[\"ReferenceFrame\"], Infinity))", //
        "{Horizon}");
    check(
        "($GeoLocation = GeoPosition({52.52,13.405}); "
            + "Cases(AstroGraphics(AstroReferenceFrame -> "
            + "{\"Horizon\", DateObject({2022,10,23,15,0,0})}), " + "Rule(MetaInformation, m_) :> "
            + "GeoDistance(m[\"Location\"], GeoPosition({52.52,13.405})) < Quantity(1,\"Meters\"), "
            + "Infinity))", //
        "{True}");
  }

  @Test
  public void testAstroGraphicsBackgrounds() {
    check("Head(AstroGraphics(AstroBackground -> \"BlackSky\"))", //
        "Graphics");
    check("Head(AstroGraphics(AstroBackground -> \"GalacticSky\"))", //
        "Graphics");
    check("Head(AstroGraphics(AstroBackground -> None))", //
        "Graphics");
    // AstroStyling wraps a style; the style inside it is what counts
    check("Head(AstroGraphics(AstroBackground -> AstroStyling(\"BlackSky\")))", //
        "Graphics");
    // a white sky needs dark stars on it, so the chart background flips with the style
    check(
        "Cases(AstroGraphics(AstroBackground -> \"WhiteSky\"), Rule(Background, b_) :> b, "
            + "Infinity)", //
        "{GrayLevel(1.0)}");
    check(
        "Cases(AstroGraphics(AstroBackground -> \"BlackSky\"), Rule(Background, b_) :> b, "
            + "Infinity)", //
        "{GrayLevel(0.06)}");
  }

  @Test
  public void testAstroGraphicsMetaInformation() {
    // a chart records how it was made
    check("Sort(Keys(Cases(AstroGraphics(), Rule(MetaInformation, m_) :> m, Infinity)[[1]]))", //
        "{Center,Date,Location,MagnitudeLimit,Projection,Range,ReferenceFrame}");
    check("Cases(AstroGraphics(), Rule(MetaInformation, m_) :> m[\"MagnitudeLimit\"], Infinity)", //
        "{6.0}");
    check("Cases(AstroGraphics(), Rule(MetaInformation, m_) :> m[\"Location\"], Infinity)", //
        "{None}");
  }

  @Test
  public void testGeoGraphicsRenders() {
    check("Head(GeoGraphics())", //
        "Graphics");
    // this is what the tier 3 geographic primitives were waiting for: nothing in the SVG
    // renderer understands a GeoPosition, and GeoGraphics is what replaces them
    check("Head(GeoGraphics(DayNightTerminator(DateObject({2026,6,21,12,0,0}))))", //
        "Graphics");
    check("FreeQ(GeoGraphics(DayNightTerminator(DateObject({2026,6,21,12,0,0}))), GeoPosition)", //
        "True");
  }

  @Test
  public void testStarsWorkInTheExistingFunctions() {
    // a star resolves wherever a solar system body does
    check("AstroPosition(\"Sirius\", \"Equatorial\")", //
        "{Quantity(101.2872,\"AngularDegrees\"),Quantity(-16.7161,\"AngularDegrees\")}");
    // at its upper culmination over Berlin, Sirius is overhead on Berlin's meridian at its own
    // declination - which ties the catalogue and the Orekit geometry together
    // asserted as a distance rather than as a literal GeoPosition: the subpoint is a rotation of a
    // rotation, so its last few bits move with the order the Orekit frame caches happen to be
    // warmed
    // in, and pinning all seventeen digits made this fail on a change that touched nothing near it
    check(
        "GeoDistance(AstroSubpoint(\"Sirius\", DateObject({2026,1,15,22,11,12})), "
            + "GeoPosition({-16.7161,13.405})) < Quantity(5,\"Kilometers\")", //
        "True");
    // Polaris is circumpolar at this latitude, so it never rises
    check(
        "AstroRiseSet(\"Polaris\", \"Rise\", GeoPosition({52.52,13.405}), "
            + "DateObject({2026,1,15}))", //
        "Missing");
    // a star has a direction but no catalogued distance
    check("AstroDistance(\"Sirius\")", //
        "AstroDistance(Sirius)");
  }

  @Test
  public void testStarData() {
    check("StarData(\"Sirius\", \"ApparentMagnitude\")", //
        "-1.44");
    check("StarData(\"Sirius\", \"Constellation\")", //
        "Canis Major");
    check("StarData(\"alpha CMa\", \"Name\")", //
        "Sirius");
    check("StarData({\"Sirius\",\"Vega\"}, \"ApparentMagnitude\")", //
        "{-1.44,0.03}");
    // Polaris stays within 0.74 degrees of the pole, so its altitude is its observer's latitude
    check(
        "StarData(\"Polaris\", \"Altitude\", GeoPosition({52.52,13.405}), "
            + "DateObject({2026,1,15,22,0,0}))", //
        "Quantity(52.89931,\"AngularDegrees\")");
    // the astrophysical properties need a catalogue which is not bundled
    check("StarData(\"Sirius\", \"SpectralClass\")", //
        "StarData(Sirius,SpectralClass)");
    check("StarData(\"Nonexistent\", \"Name\")", //
        "StarData(Nonexistent,Name)");
  }

  @Test
  public void testGeoDistance() {
    // Oslo to Berlin along a rhumb line
    check("GeoDistance({59.914,10.752},{52.523,13.412})", //
        "Quantity(839236.2,\"Meters\")");
    // a quarter of the equator, 40075/4 kilometers
    check("GeoDistance({0,0},{0,90})", //
        "Quantity(1.00188*10^7,\"Meters\")");
  }

  @Test
  public void testUnknownBody() {
    check("AstroDistance(\"Vulcan\", DateObject({2026,6,21}))", //
        "AstroDistance(Vulcan,DateObject({2026,6,21},Day))");
  }
}
