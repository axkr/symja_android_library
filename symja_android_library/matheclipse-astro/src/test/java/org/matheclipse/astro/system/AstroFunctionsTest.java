package org.matheclipse.astro.system;

import org.junit.jupiter.api.Tag;
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
 * parameters, except in {@link #testMeeusFallback()}. Tests which are checked against a published
 * value name the source in a comment.
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

  /**
   * Outside the 1990-2149 range of the bundled JPL ephemerides the bodies fall back to the Meeus
   * theories (with the astrofallback message); outside 1583-3000 and for eclipses the functions
   * still decline with the orekitdata message.
   */
  @Test
  public void testMeeusFallback() {
    // the June solstice at Berlin in 1600 looks like the one in 2026: about 60.9 degrees
    check("SunPosition(GeoPosition({52.52,13.405}), DateObject({1600,6,21,11,0,0}))", //
        "{Quantity(176.5746,\"AngularDegrees\"),Quantity(60.93791,\"AngularDegrees\")}");
    // in J2000 coordinates the New Year Sun has moved back by 500 years of precession, about 7
    // degrees, from 281.3 degrees in 2000
    check("SunPosition(DateObject({2500,1,1,12,0,0}), CelestialSystem->\"Equatorial\")", //
        "{Quantity(274.4015,\"AngularDegrees\"),Quantity(-23.31454,\"AngularDegrees\")}");
    check("MoonPosition(DateObject({1700,3,1,0,0,0}), CelestialSystem->\"Equatorial\")", //
        "{Quantity(105.6713,\"AngularDegrees\"),Quantity(17.94449,\"AngularDegrees\")}");
    check("Sunrise(GeoPosition({52.52,13.405}), DateObject({1650,6,21}))", //
        "DateObject({1650,6,21,2,41,20.7601},Instant,Gregorian,0.0)");
    // the full moon of 1950-01-04 07:48 UT
    check("FullMoon(DateObject({1950,1,1}))", //
        "DateObject({1950,1,4,7,48,24.86852},Instant,Gregorian,0.0)");
    check("NewMoon(DateObject({2600,1,1}))", //
        "DateObject({2600,1,7,11,27,15.83926},Instant,Gregorian,0.0)");

    // before the Gregorian calendar and after 3000 there is no fallback
    check("SunPosition(DateObject({1200,1,1,12,0,0}), CelestialSystem->\"Equatorial\")", //
        "SunPosition(DateObject({1200,1,1,12,0,0},Instant,Gregorian,0.0),CelestialSystem->Equatorial)");
    // eclipses and orbital elements are too sensitive for the Meeus theories
    check("SolarEclipse(DateObject({1700,1,1}))", //
        "SolarEclipse(DateObject({1700,1,1},Day))");
    check("OrbitalElements(\"Mars\", \"SemimajorAxis\", DateObject({2300,1,1}))", //
        "OrbitalElements(Mars,SemimajorAxis,DateObject({2300,1,1},Day))");
  }

  /**
   * A function given no location stands at <code>$GeoLocation</code>. Here it is assigned; on the
   * user's own machine it can also come from the <code>symja.geolocation</code> setting or the time
   * zone (see <code>FindGeoLocation</code>), which this kernel - with the file system off - does
   * not look at.
   */
  @Test
  public void testDefaultObserver() {
    // without a location there is nothing to stand on
    check("SunPosition(DateObject({2026,6,21,11,0,0}))", //
        "SunPosition(DateObject({2026,6,21,11,0,0},Instant,Gregorian,0.0))");
    check("$GeoLocation = GeoPosition({52.52,13.405}); $GeoLocationSource", //
        "User");
    check(
        "Sunrise(DateObject({2026,6,21})) == "
            + "Sunrise(GeoPosition({52.52,13.405}), DateObject({2026,6,21}))", //
        "True");
    check(
        "SunPosition(DateObject({2026,6,21,11,0,0})) == "
            + "SunPosition(GeoPosition({52.52,13.405}), DateObject({2026,6,21,11,0,0}))", //
        "True");
    check(
        "SiderealTime(DateObject({2026,6,21,11,0,0})) == "
            + "SiderealTime(GeoPosition({52.52,13.405}), DateObject({2026,6,21,11,0,0}))", //
        "True");
    // a horizon chart needs somewhere to stand, and takes the same place
    check(
        "Cases(AstroGraphics(AstroReferenceFrame -> {\"Horizon\", "
            + "DateObject({2026,9,25,20,0,0})}), Rule(MetaInformation, m_) :> m[\"Location\"], "
            + "Infinity) == Cases(AstroGraphics(AstroReferenceFrame -> {\"Horizon\", "
            + "DateObject({2026,9,25,20,0,0}), GeoPosition({52.52,13.405})}), "
            + "Rule(MetaInformation, m_) :> m[\"Location\"], Infinity)", //
        "True");
    // unset again, the functions have no location to use
    check("$GeoLocation =.; {$GeoLocationSource, SunPosition(DateObject({2026,6,21,11,0,0}))}", //
        "{None,SunPosition(DateObject({2026,6,21,11,0,0},Instant,Gregorian,0.0))}");
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
    // Meeus example 48.a: the bright limb on 1992 April 12 is at position angle 285.0 degrees
    check("MoonPhase(DateObject({1992,4,12,0,0,0}), \"BrightLimbAngle\")", //
        "Quantity(285.0464,\"AngularDegrees\")");
    // a waxing Moon has its bright limb towards the west (about 270 degrees), a waning one
    // towards the east (about 90 degrees)
    check("MoonPhase(DateObject({2026,9,25,0,0,0}), \"BrightLimbAngle\")", //
        "Quantity(250.4577,\"AngularDegrees\")");
    check("MoonPhase(DateObject({2026,10,9,0,0,0}), \"BrightLimbAngle\")", //
        "Quantity(107.1928,\"AngularDegrees\")");
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
    // delta T = TT - UT1: measured, 63.83 s at the start of 2000 ...
    check("TimeSystemConvert(DateObject({2000,1,1,0,0,0}), \"DeltaT\")", //
        "Quantity(63.8285,\"Seconds\")");
    check("TimeSystemConvert(DateObject({2026,1,1,0,0,0}), \"DeltaT\")", //
        "Quantity(69.10986,\"Seconds\")");
    // ... from the delta T table in 1700 and from the Meeus polynomial in 2500
    check("TimeSystemConvert(DateObject({1700,1,1,0,0,0}), \"DeltaT\")", //
        "Quantity(9.0,\"Seconds\")");
    check("TimeSystemConvert(DateObject({2500,1,1,0,0,0}), \"DeltaT\")", //
        "Quantity(1185.709,\"Seconds\")");
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

  /**
   * The documentation example of the Wolfram Language: bodies as entities, and the frame written
   * with its parameters, {frame, date} or {frame, "Date" -> date, "Location" -> location}.
   */
  @Test
  public void testAstroPositionEntitiesAndFrameSpecs() {
    check(
        "AstroPosition(Entity(\"Planet\", \"Mars\"), {\"Equatorial\", DateObject({2022,7,1})}) "
            + "== AstroPosition(\"Mars\", \"Equatorial\", DateObject({2022,7,1}))", //
        "True");
    check("AstroPosition(Entity(\"Planet\", \"Mars\"), {\"Horizon\", "
        + "\"Date\" -> DateObject({2022,7,1,22,0,0}), \"Location\" -> GeoPosition({52.52,13.405})}) "
        + "== AstroPosition(\"Mars\", \"Horizon\", DateObject({2022,7,1,22,0,0}), "
        + "GeoPosition({52.52,13.405}))", //
        "True");
    check(
        "AstroPosition(Entity(\"Star\", \"Sirius\"), {\"Equatorial\", DateObject({2022,7,1})}) "
            + "== AstroPosition(\"Sirius\", \"Equatorial\", DateObject({2022,7,1}))", //
        "True");
    // an entity of the wrong type names nothing
    check("AstroPosition(Entity(\"Star\", \"Mars\"), {\"Equatorial\", DateObject({2022,7,1})})", //
        "AstroPosition(Entity(Star,Mars),{Equatorial,DateObject({2022,7,1},Day)})");
    check("AstroPosition(Entity(\"Planet\", \"Sirius\"), {\"Equatorial\", DateObject({2022,7,1})})", //
        "AstroPosition(Entity(Planet,Sirius),{Equatorial,DateObject({2022,7,1},Day)})");
    // DateRange of date strings gives date strings, "July 1, 2022 12:00 am", as in the Wolfram
    // Language, and AstroPosition reads them
    check(
        "mars = AstroPosition(Entity(\"Planet\", \"Mars\"), {\"Equatorial\", #}) & /@ "
            + "DateRange(\"1 Jul 2022\", \"1 July 2023\", \"Week\"); "
            + "{Length(mars), MatchQ(mars, {{_Quantity, _Quantity, _Quantity}..}), "
            + "mars[[1]] == AstroPosition(\"Mars\", \"Equatorial\", DateObject({2022,7,1}))}", //
        "{53,True,True}");
    // the path plotted: the {ra, dec, distance} results are positions, and the chart centres on
    // them - Mars' retrograde loop in Taurus
    check("Cases(AstroGraphics({Red, Point(AstroPosition(Entity(\"Planet\", \"Mars\"), "
        + "{\"Equatorial\", #}) & /@ DateRange(\"1 Jul 2022\", \"1 July 2023\", \"Week\"))}, "
        + "AstroReferenceFrame -> \"Equatorial\", AstroRange -> Quantity(60, \"AngularDegrees\")), "
        + "Rule(MetaInformation, m_) :> Round(QuantityMagnitude(m[\"Center\"])), Infinity)", //
        "{{81,24}}");
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
  @Tag("slow")
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
    // a whole-sky chart draws the naked eye stars to magnitude 4.5, about 950 as in the Wolfram
    // Language; the twenty brightest named ones are drawn with their names in LabeledData
    check(stars("AstroGraphics()"), //
        "895");
    // at a fixed date, since the Sun, the Moon and the planets name themselves first and a star
    // whose name would run into one already placed goes without
    check("Cases(AstroGraphics(AstroReferenceFrame -> DateObject({2026,9,25,20,0,0})), "
        + "Annotation(g_,\"LabeledData\",_) :> "
        + "Cases(DeleteCases(g, _Annotation), Text(Style(t_,___),__) :> t, Infinity), Infinity)", //
        "{{Sirius,Canopus,Arcturus,Rigil Kentaurus,Vega,Capella,Rigel,Procyon,Achernar,Altair,"
            + "Antares,Pollux,Fomalhaut,Deneb,Regulus,Adhara,Polaris}}");
    // gathered into one Point per colour and half-magnitude size step rather than one per star
    check("Count(AstroGraphics(), _Point, Infinity) < 100", //
        "True");
    // a narrow view draws far fewer stars, not more - without the range filter the whole
    // catalogue lands off-canvas instead
    check(
        stars(
            "AstroGraphics(AstroCenter->{83,0}, " + "AstroRange->Quantity(10,\"AngularDegrees\"))"), //
        "888");
    // an azimuthal projection shows one hemisphere, so about half the stars
    check(
        stars("AstroGraphics(AstroProjection->\"Orthographic\", "
            + "AstroRange->Quantity(89,\"AngularDegrees\"))"), //
        "431");
  }

  /**
   * The number of stars a chart draws: the coordinates of the <code>Point</code>s in its
   * <code>AstroStars</code> layer, which holds one multi-point <code>Point</code> per colour and
   * size step.
   */
  private static String stars(String chart) {
    return "Total(Cases(" + chart + ", Annotation(g_,\"AstroStars\",_) :> "
        + "Total(Cases(g, Point(p_) :> Length(p), Infinity)), Infinity))";
  }

  /**
   * Fainter than magnitude 8.5 the stars come from the ASCC-2.5 catalogue of Night Vision, capped
   * at the brightest 8000; every chart colours its stars by spectral class unless it is printed on
   * white.
   */
  @Test
  public void testAstroGraphicsDeepStars() {
    String orion =
        "AstroGraphics(AstroCenter->{83.8,-5.4}, " + "AstroRange->Quantity(1,\"AngularDegrees\"))";
    // a telescope field goes down to magnitude 11.1 by itself; the view is a square, so the stars
    // reach past the circle of the range into its corners, and a little beyond where the plot
    // range clips them
    check(stars(orion), //
        "257");
    check(
        "Cases(" + orion + ", Rule(MetaInformation, m_) :> "
            + "{m[\"MagnitudeLimit\"],m[\"StarCatalog\"]}, Infinity)", //
        "{{11.1,ASCC-2.5}}");
    // a wide field at magnitude 11 would hold 300000 stars; the brightest 8000 are drawn and the
    // chart says how deep that reaches
    String wide = "AstroGraphics(AstroCenter->{83.8,-5.4}, "
        + "AstroRange->Quantity(30,\"AngularDegrees\"), AstroZoomLevel->11)";
    check(stars(wide), //
        "8000");
    check(
        "Cases(" + wide + ", Rule(MetaInformation, m_) :> m[\"EffectiveMagnitudeLimit\"], "
            + "Infinity)", //
        "{7.99}");
    // the stars are coloured by spectral class - six of them are present on a naked eye chart -
    // and plain dark on a white sky
    check(
        "Cases(AstroGraphics(), Annotation(g_,\"AstroStars\",_) :> "
            + "Length(Union(Cases(g, _RGBColor, 1))), Infinity)", //
        "{6}");
    check(
        "Cases(AstroGraphics(AstroBackground -> \"WhiteSky\"), Annotation(g_,\"AstroStars\",_) "
            + ":> Union(Cases(g, _GrayLevel|_RGBColor, 1)), Infinity)", //
        "{{GrayLevel(0.1)}}");
  }

  /**
   * A dated chart shows the Sun, the Moon and the planets, farthest first, nested in LabeledData as
   * AstroSolarSystem the way the Wolfram Language nests them; a horizon chart only those above the
   * horizon. 2026-09-25 20:00 UTC is the evening before a full moon, with Saturn near opposition.
   */
  @Test
  public void testAstroGraphicsSolarSystem() {
    String dated = "AstroGraphics(AstroReferenceFrame -> DateObject({2026,9,25,20,0,0}))";
    // the nearer bodies name themselves first; a name which would run into one of theirs is left
    // out, as the Wolfram Language leaves it out
    check(
        "Cases(" + dated + ", Annotation(g_,\"AstroSolarSystem\",_) :> "
            + "Cases(g, Text(t_,__) :> t, Infinity), Infinity)", //
        "{{Pluto,Uranus,Mars,Mercury,Sun,Venus,Moon}}");
    // planets are points, the Sun a disk, the Moon a dark disk with its lit part on top
    check(
        "Cases(" + dated + ", Annotation(g_,\"AstroSolarSystem\",_) :> "
            + "{Count(g,_Point,Infinity),Count(g,_Disk,Infinity),Count(g,_Polygon,Infinity)}, "
            + "Infinity)", //
        "{{8,2,1}}");
    check("Cases(" + dated + ", Rule(MetaInformation, m_) :> m[\"Ephemeris\"], Infinity)", //
        "{JPL DE440}");
    String horizon = "AstroGraphics(AstroReferenceFrame -> {\"Horizon\", "
        + "DateObject({2026,9,25,20,0,0}), GeoPosition({52.52,13.405})})";
    check(
        "Cases(" + horizon + ", Annotation(g_,\"AstroSolarSystem\",_) :> "
            + "Cases(g, Text(t_,__) :> t, Infinity), Infinity)", //
        "{{Pluto,Neptune,Uranus,Saturn,Moon}}");
    // a horizon chart is the sky at its instant, so it has the solar system even when the instant
    // is left to be now
    check(
        "Length(Cases(AstroGraphics(AstroReferenceFrame -> {\"Horizon\", "
            + "GeoPosition({52.52,13.405})}), Annotation(_,\"AstroSolarSystem\",_), Infinity))", //
        "1");
    // outside the JPL ephemerides the Meeus theories place the bodies
    check(
        "Cases(AstroGraphics(AstroReferenceFrame -> DateObject({1700,9,25,20,0,0})), "
            + "Rule(MetaInformation, m_) :> m[\"Ephemeris\"], Infinity)", //
        "{Meeus}");
    // as in the Wolfram Language an undated chart is the sky now, the solar system included
    check("Length(Cases(AstroGraphics(), Annotation(_,\"AstroSolarSystem\",_), Infinity))", //
        "1");
  }

  /**
   * A zoomed chart shows what the Wolfram Language shows at that scale: all the constellations in
   * view, the names or Bayer letters of the brighter stars and the Messier, NGC and IC objects with
   * the symbol of their kind - the Orion field of the Wolfram Language documentation.
   */
  @Test
  public void testAstroGraphicsZoomedDetail() {
    String betelgeuse = "AstroGraphics(Entity(\"Star\", \"Betelgeuse\"), "
        + "AstroRange -> Quantity(20, \"AngularDegrees\"), AstroReferenceFrame -> \"Equatorial\")";
    check(
        "Intersection(Flatten(Cases(" + betelgeuse + ", Annotation(g_,\"Constellations\",_) :> "
            + "Cases(g, Text(Style(t_,___),__) :> t, Infinity), Infinity)), "
            + "{\"Orion\",\"Gemini\"})", //
        "{Gemini,Orion}");
    check("Intersection(Flatten(Cases(" + betelgeuse + ", Annotation(g_,\"LabeledData\",_) :> "
        + "Cases(g, Text(Style(t_,___),__) :> t, Infinity), Infinity)), "
        + "{\"Meissa\",\"Bellatrix\",\"Rigel\",\"Saiph\",\"Alhena\",\"\u03bb Gem\",\"\u03c4 Ori\"})", //
        "{Alhena,Bellatrix,Meissa,Rigel,Saiph,λ Gem,τ Ori}");
    // clusters as dashed circles, nebulae as squares; the name of M42 gives way to those of the
    // stars of the sword
    check(
        "Intersection(Flatten(Cases(" + betelgeuse + ", Annotation(g_,\"DeepSkyObjects\",_) :> "
            + "Cases(g, Text(Style(t_,___),__) :> t, Infinity), Infinity)), "
            + "{\"M35\",\"M42\",\"NGC 1909\",\"IC 434\"})", //
        "{IC 434,M35,NGC 1909}");
    check(
        "Cases(" + betelgeuse + ", Annotation(g_,\"DeepSkyObjects\",_) :> "
            + "{Count(g,_Rectangle,Infinity) > 3, Count(g,_Dashing,Infinity) > 10}, Infinity)", //
        "{{True,True}}");
    // the square is what is shown
    check("Cases(" + betelgeuse + ", Rule(PlotRangeClipping, c_) :> c, Infinity)", //
        "{True}");
  }

  /**
   * Objects can be named by entity as well as by string. One standing on its own is drawn as a
   * labelled marker, and an automatic centre follows the named objects, so a zoomed chart shows
   * what it was asked for.
   */
  @Test
  public void testAstroGraphicsEntities() {
    String betelgeuse = "AstroGraphics(Entity(\"Star\", \"Betelgeuse\"), "
        + "AstroRange -> Quantity(20, \"AngularDegrees\"), AstroReferenceFrame -> \"Equatorial\")";
    // Betelgeuse: 5h55m10s, +7 24' 25"
    check("Cases(" + betelgeuse + ", Rule(MetaInformation, m_) :> m[\"Center\"], Infinity)", //
        "{{Quantity(88.7929,\"AngularDegrees\"),Quantity(7.4071,\"AngularDegrees\")}}");
    // the point and the name, in a colour that shows on the night sky
    check("Last(First(" + betelgeuse + "))", //
        "{RGBColor(1.0,0.45,0.35),{Point({0.0,0.0}),"
            + "Text(Betelgeuse,{0.0,0.0},{-1.15,-1.15})}}");
    // inside a primitive an entity is a position; the user's own colour comes after the default
    check(
        "Cases(AstroGraphics({Red, Point(Entity(\"Star\",\"Rigel\"))}, "
            + "AstroRange -> Quantity(20, \"AngularDegrees\")), "
            + "{RGBColor(__), {Red, p_Point}} :> p, Infinity)", //
        "{Point({0.0,0.0})}");
    // an explicit centre wins over the named objects
    check(
        "Cases(AstroGraphics(Entity(\"Star\", \"Rigel\"), "
            + "AstroCenter -> Entity(\"Star\",\"Betelgeuse\"), "
            + "AstroRange -> Quantity(20, \"AngularDegrees\")), "
            + "Rule(MetaInformation, m_) :> m[\"Center\"], Infinity)", //
        "{{Quantity(88.7929,\"AngularDegrees\"),Quantity(7.4071,\"AngularDegrees\")}}");
    // an unknown object is reported and the chart stays unevaluated
    check(
        "AstroGraphics(Entity(\"Star\", \"Nonexistent\"), "
            + "AstroRange -> Quantity(20, \"AngularDegrees\"))", //
        "AstroGraphics(Entity(Star,Nonexistent),AstroRange->Quantity(20,\"AngularDegrees\"))");
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
    // every group of primitives is a named layer, so a chart can be taken apart again - the
    // layers of the Wolfram Language, AstroBackground and AstroGridLines empty unless asked for
    check("Cases(AstroGraphics(), Annotation(_,n_,_) :> n, Infinity)", //
        "{AstroBackground,AstroGridLines,MainPlanes,Constellations,AstroStars,AstroSolarSystem,"
            + "LabeledData}");
    // Symja's own namespace
    check("Union(Cases(AstroGraphics(), Annotation(_,_,ns_) :> ns, Infinity))", //
        "{SymjaAstroGraphics}");
    // Annotation renders its first argument and ignores the rest, so the stars are still drawn
    check(stars("AstroGraphics()"), //
        "895");
    // the main planes carry their scales: 24 hours on the equator, the months on the ecliptic and
    // every 45 degrees of galactic longitude
    check(
        "Length(Cases(AstroGraphics(), Annotation(g_,\"MainPlanes\",_) :> "
            + "Cases(g, Text(t_,__) :> t, Infinity), Infinity)[[1]])", //
        "44");
    // the Milky Way, the grid and the constellations are opt-in, as in the Wolfram Language
    check(
        "Cases(AstroGraphics(), Annotation(g_,\"AstroBackground\"|\"AstroGridLines\""
            + "|\"Constellations\",_) :> g, Infinity)", //
        "{{},{},{}}");
    // the Milky Way is one smooth raster, the grid 12 meridians and 11 parallels
    check("Map(Length, Cases(AstroGraphics(AstroBackground -> \"GalacticSky\", "
        + "AstroGridLines -> 12), Annotation(g_,\"AstroBackground\"|\"AstroGridLines\",_) :> g, "
        + "Infinity))", //
        "{1,25}");
    // a named constellation is drawn on any chart and centres it - on a wide one it is the only
    // constellation drawn
    String orion = "AstroGraphics(Entity(\"Constellation\", \"Orion\"), "
        + "AstroRange -> Quantity(70, \"AngularDegrees\"))";
    check(
        "Cases(" + orion + ", Annotation(g_,\"Constellations\",_) :> "
            + "Cases(g, Text(t_,__) :> t, Infinity), Infinity)", //
        "{{Orion}}");
    // the four figure polylines of Orion in the catalogue, and its IAU boundary
    check(
        "Count(Cases(" + orion + ", Annotation(g_,\"Constellations\",_) :> g, Infinity), "
            + "_Line, Infinity)", //
        "5");
  }

  @Test
  public void testAstroGraphicsReferenceFrames() {
    String horizon = "AstroGraphics(AstroReferenceFrame -> {\"Horizon\", "
        + "DateObject({2022,10,23,15,0,0}), GeoPosition({52.52,13.405})})";
    // the frame is now honoured rather than being read only for its date
    check("Cases(" + horizon + ", Rule(MetaInformation, m_) :> m[\"ReferenceFrame\"], Infinity)", //
        "{Horizon}");
    // a horizon chart is a planisphere: zenith centred, ninety degrees to the horizon, and
    // stereographic as in the Wolfram Language, which puts the horizon at radius 2
    check("Cases(" + horizon + ", Rule(MetaInformation, m_) :> m[\"Projection\"], Infinity)", //
        "{Stereographic}");
    // with the compass directions along the horizon
    check("Cases(" + horizon + ", Annotation(g_,\"MainPlanes\",_) :> "
        + "Intersection(Cases(g, Text(Style(t_,___),__) :> t, Infinity), {\"N\",\"NE\",\"E\",\"SE\",\"S\",\"SW\",\"W\",\"NW\"}), "
        + "Infinity)", //
        "{{E,N,NE,NW,S,SE,SW,W}}");
    check("Cases(" + horizon + ", Rule(MetaInformation, m_) :> m[\"Center\"], Infinity)", //
        "{{Quantity(0.0,\"AngularDegrees\"),Quantity(90.0,\"AngularDegrees\")}}");
    // half the sky is below the horizon and is not drawn
    check(stars(horizon) + " < " + stars("AstroGraphics()"), //
        "True");
    // the galactic frame has no Orekit frame behind it, so it is worth checking it resolves
    check(
        "Cases(AstroGraphics(AstroReferenceFrame -> \"Galactic\"), "
            + "Rule(MetaInformation, m_) :> m[\"ReferenceFrame\"], Infinity)", //
        "{Galactic}");
    check("Head(AstroGraphics(AstroReferenceFrame -> \"Ecliptic\"))", //
        "Graphics");
    // a horizon frame with nowhere known to stand stands at GeoPosition({0, 0}), as in the
    // Wolfram Language
    check(
        "Cases(AstroGraphics(AstroReferenceFrame -> \"Horizon\"), "
            + "Rule(MetaInformation, m_) :> m[\"Location\"], Infinity)", //
        "{GeoPosition({0.0,0.0,0.0})}");
    check("AstroGraphics(AstroReferenceFrame -> \"Nonsense\")", //
        "AstroGraphics(AstroReferenceFrame->Nonsense)");
  }

  /**
   * With a location on record a plain AstroGraphics() is the sky above it, now, as in the Wolfram
   * Language; asking for objects, a centre or a range asks for a chart of that part of the sky.
   */
  @Test
  public void testAstroGraphicsSkyAbove() {
    check(
        "($GeoLocation = GeoPosition({52.52,13.405}); "
            + "Cases(AstroGraphics(), Rule(MetaInformation, m_) :> "
            + "{m[\"ReferenceFrame\"], m[\"Projection\"], m[\"MagnitudeLimit\"]}, Infinity))", //
        "{{Horizon,Stereographic,4.5}}");
    check(
        "($GeoLocation = GeoPosition({52.52,13.405}); "
            + "Cases(AstroGraphics(Entity(\"Constellation\", \"Orion\")), "
            + "Rule(MetaInformation, m_) :> m[\"ReferenceFrame\"], Infinity))", //
        "{ICRS}");
    check(
        "($GeoLocation = GeoPosition({52.52,13.405}); "
            + "Cases(AstroGraphics(AstroRange -> Quantity(30, \"AngularDegrees\")), "
            + "Rule(MetaInformation, m_) :> m[\"ReferenceFrame\"], Infinity))", //
        "{ICRS}");
  }

  /**
   * The planisphere looks like the charts of the Wolfram Language documentation: a black disk of
   * sky in a slate ground, reaching 4.5 degrees below the horizon as the Wolfram Language does, and
   * the planes' scales turned to run along the lines.
   */
  @Test
  public void testAstroGraphicsGround() {
    String sky = "AstroGraphics(AstroReferenceFrame -> {\"Horizon\", "
        + "DateObject({2026,6,15,15,0,0},\"Instant\",\"Gregorian\",0.), GeoPosition({40.11,-88.24})})";
    check("Cases(" + sky + ", Annotation(_,n_,_) :> n, Infinity)", //
        "{AstroBackground,AstroGridLines,MainPlanes,Constellations,AstroStars,AstroSolarSystem,"
            + "LabeledData,AstroGround}");
    // the ground is the square with the sky cut out of it, laid over the sky at 0.78 opacity
    check(
        "Cases(" + sky + ", Annotation(g_,\"AstroGround\",_) :> "
            + "{Cases(g, _Opacity), Cases(g, Polygon(_Rule) :> True)}, Infinity)", //
        "{{{Opacity(0.78)},{True}}}");
    // the stereographic radius of an altitude of -4.5 degrees is 2 Tan(47.25 Degree)
    check(
        "Cases(" + sky + ", Rule(PlotRange, {{a_,b_},_}) :> "
            + "Abs(b - 2*Tan(47.25*Degree)) < 0.01, Infinity)", //
        "{True}");
    // the scales are Text with a direction, so they follow the lines
    check(
        "Cases(" + sky + ", Annotation(g_,\"MainPlanes\",_) :> "
            + "Count(g, Text(_,_,_,{_?NumberQ,_?NumberQ}), Infinity) > 20, Infinity)", //
        "{True}");
    // an equatorial chart has no horizon, and so no ground
    check("Cases(AstroGraphics(AstroCenter -> {0,0}), Annotation(_,\"AstroGround\",_), Infinity)", //
        "{}");
  }

  /**
   * The documentation example of the Wolfram Language. With no location known, a horizon chart
   * stands at GeoPosition({0, 0}), as the Wolfram Language's own chart does, rather than failing;
   * the Milky Way is a glow painted cell by cell, bright near the galactic centre and nothing near
   * the galactic poles, and hidden below the horizon.
   */
  @Test
  public void testAstroGraphicsGalacticSky() {
    String example = "AstroGraphics(AstroReferenceFrame -> {\"Horizon\", "
        + "DateObject({2022,10,23,15,0,0})}, AstroBackground -> \"GalacticSky\")";
    check(
        "Cases(" + example + ", Rule(MetaInformation, m_) :> "
            + "{m[\"ReferenceFrame\"], m[\"Location\"]}, Infinity)", //
        "{{Horizon,GeoPosition({0.0,0.0,0.0})}}");
    check(
        "Cases(" + example + ", Annotation(g_,\"AstroBackground\",_) :> "
            + "Cases(g, Raster(c_,__) :> Dimensions(c), Infinity), Infinity)", //
        "{{{200,200,4}}}");
  }

  /**
   * The Wolfram Language example of Venus passing Jupiter on 2065-11-22, seen from the south pole
   * in a view 0.04 degrees across: the date with TimeZone -> 0 and a fractional hour, a plot range
   * that small, the planets as disks of their true size, and the light time, without which Venus
   * misses Jupiter by 20 arcseconds.
   */
  @Test
  public void testAstroGraphicsVenusJupiter() {
    String chart = "AstroGraphics(Entity(\"Planet\", \"Jupiter\"), AstroReferenceFrame -> "
        + "{\"Equatorial\", DateObject({2065, 11, 22, 12.75, 0, 0}, TimeZone -> 0), "
        + "GeoPosition({-90, 0})}, AstroRange -> Quantity(0.02, \"AngularDegrees\"))";
    check(
        "Cases(" + chart + ", Annotation(g_,\"AstroSolarSystem\",_) :> "
            + "{Count(g, _Disk, Infinity), Count(g, _Polygon, Infinity)}, Infinity)", //
        "{{2,2}}");
    // the disks of Jupiter and Venus overlap: the distance of their centres is less than the sum
    // of their radii
    check(
        "Cases(" + chart + ", Annotation(g_,\"AstroSolarSystem\",_) :> "
            + "Apply(EuclideanDistance(#1[[1]], #2[[1]]) < #1[[2]] + #2[[2]] &, "
            + "Cases(g, Disk(c_, r_) :> {c, r}, Infinity)), Infinity)", //
        "{True}");
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
        "{GrayLevel(0.0)}");
    // a colour is the colour of the sky
    check(
        "Cases(AstroGraphics(AstroBackground -> RGBColor(0,0,0.2)), Rule(Background, b_) :> b, "
            + "Infinity)", //
        "{RGBColor(0,0,0.2)}");
  }

  @Test
  public void testAstroGraphicsMetaInformation() {
    // a chart records how it was made
    check("Sort(Keys(Cases(AstroGraphics(), Rule(MetaInformation, m_) :> m, Infinity)[[1]]))", //
        "{Center,Date,EffectiveMagnitudeLimit,Location,MagnitudeLimit,Projection,Range,"
            + "ReferenceFrame,StarCatalog}");
    check("Cases(AstroGraphics(), Rule(MetaInformation, m_) :> m[\"MagnitudeLimit\"], Infinity)", //
        "{4.5}");
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
    // the spectral class comes from the ASCC-2.5 star database of Night Vision
    check(
        "StarData({\"Sirius\",\"Vega\",\"Betelgeuse\",\"Arcturus\",\"Rigel\"}, \"SpectralClass\")", //
        "{A1,A0,M2,K2,B8}");
    // the other astrophysical properties need a catalogue which is not bundled
    check("StarData(\"Sirius\", \"Mass\")", //
        "StarData(Sirius,Mass)");
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

  /**
   * <code>AstronomicalData(n)</code> names the nth major body counting outwards from the Sun, and
   * the name it gives feeds straight back in as the body of a position query - the two compose,
   * which is how a notebook walks the planets.
   */
  @Test
  public void testAstronomicalDataNames() {
    check("AstronomicalData(1)", //
        "Mercury");
    check("AstronomicalData(3)", //
        "Earth");
    check("AstronomicalData(9)", //
        "Pluto");
    check("AstronomicalData(\"Mars\")", //
        "Mars");
    // out of the range of the classic nine, and an unknown body
    check("AstronomicalData(10)", //
        "AstronomicalData(10)");
    check("AstronomicalData(\"Vulcan\", {\"Position\"})", //
        "AstronomicalData(Vulcan,{Position})");
    check("AstronomicalData(\"Mars\", {\"Mass\"})", //
        "AstronomicalData(Mars,{Mass})");
  }

  /**
   * The position is heliocentric and referred to the ecliptic of J2000, in meters. The Earth is at
   * perihelion in early January, 1.471*10^11 meters from the Sun, and lies in the ecliptic plane -
   * which is what tells this frame from the equatorial one, where its z would be 5.8*10^10.
   */
  @Test
  public void testAstronomicalDataPosition() {
    check(
        "Round(Norm(AstronomicalData(\"Earth\","
            + " {\"Position\", DateObject({2020,1,1})}))/10^9)", //
        "147");
    check(
        "Abs(Last(AstronomicalData(\"Earth\","
            + " {\"Position\", DateObject({2020,1,1})}))) < 10^10", //
        "True");
    check("AstronomicalData(\"Sun\", {\"Position\", DateObject({2020,1,1})})", //
        "{0.0,0.0,0.0}");
    // every date puts Mars between its perihelion and its aphelion, 1.381 and 1.666 au
    check(
        "Table(1.38 < Norm(AstronomicalData(\"Mars\", {\"Position\", DateObject({y,1,1})}))"
            + "/1.495978707*^11 < 1.67, {y, 1995, 2145, 30})", //
        "{True,True,True,True,True,True}");
    // the date may be left out, and is then the current instant
    check("Length(AstronomicalData(\"Jupiter\", \"Position\"))", //
        "3");
  }

  /**
   * <code>PlanetData</code> is the spelling which superseded <code>AstronomicalData</code>: the
   * eight planets, and coordinates in astronomical units. Measured 2026-09-18, where
   * <code>PlanetData[]</code> is those eight entities and <code>"Position"</code> is not a property
   * of a planet.
   */
  @Test
  public void testPlanetData() {
    check("PlanetData()", //
        "{Entity(Planet,Mercury),Entity(Planet,Venus),Entity(Planet,Earth),Entity(Planet,Mars),"
            + "Entity(Planet,Jupiter),Entity(Planet,Saturn),Entity(Planet,Uranus),"
            + "Entity(Planet,Neptune)}");
    check("PlanetData(\"Mars\")", //
        "Entity(Planet,Mars)");
    check("QuantityUnit(First(PlanetData(\"Mars\", \"HelioCoordinates\")))", //
        "AstronomicalUnit");
    // the Earth is at perihelion in early January, 0.983 astronomical units from the Sun
    check(
        "Round(1000*Norm(QuantityMagnitude(PlanetData(\"Earth\","
            + " {\"HelioCoordinates\", DateObject({2020,1,1})}))))", //
        "983");
    // Pluto is a planet to the older function and not to this one
    check("PlanetData(\"Pluto\", \"HelioCoordinates\")", //
        "PlanetData(Pluto,HelioCoordinates)");
    check("PlanetData(\"Mars\", \"Position\")", //
        "PlanetData(Mars,Position)");
  }

  /**
   * The observed properties: geometry from the ephemerides, magnitude and size formulas from Meeus
   * via Night Vision. Venus on 1992-12-20 is Meeus example 41.a: magnitude -4.2, phase angle 72.96
   * degrees, 0.910947 AU from the Earth and 0.724604 AU from the Sun.
   */
  @Test
  public void testPlanetDataObserved() {
    check("PlanetData(\"Properties\")", //
        "{AngularDiameter,ApparentMagnitude,DistanceFromEarth,DistanceFromSun,HelioCoordinates,"
            + "IlluminationFraction,PhaseAngle}");
    check("PlanetData(\"Venus\", {\"ApparentMagnitude\", DateObject({1992,12,20})})", //
        "-4.21679");
    check("PlanetData(\"Venus\", {\"PhaseAngle\", DateObject({1992,12,20})})", //
        "Quantity(72.96186,\"AngularDegrees\")");
    check("PlanetData(\"Venus\", {\"IlluminationFraction\", DateObject({1992,12,20})})", //
        "0.646504");
    check("PlanetData(\"Venus\", {\"DistanceFromEarth\", DateObject({1992,12,20})})", //
        "Quantity(0.910841,\"AstronomicalUnit\")");
    check("PlanetData(\"Venus\", {\"DistanceFromSun\", DateObject({1992,12,20})})", //
        "Quantity(0.724602,\"AstronomicalUnit\")");
    check("PlanetData(\"Venus\", {\"AngularDiameter\", DateObject({1992,12,20})})", //
        "Quantity(18.46645,\"Arcseconds\")");
    // Mars at its closest approach of 2003 and Jupiter at the opposition of January 2026
    check("PlanetData(\"Mars\", {\"ApparentMagnitude\", DateObject({2003,8,28})})", //
        "-2.88338");
    check("PlanetData(\"Jupiter\", {\"ApparentMagnitude\", DateObject({2026,1,10})})", //
        "-2.6806");
    check("PlanetData(\"Jupiter\", {\"AngularDiameter\", DateObject({2026,1,10})})", //
        "Quantity(46.52434,\"Arcseconds\")");
    // Saturn's rings are nearly edge on in 2025-2026, which leaves the planet fainter
    check("PlanetData(\"Saturn\", {\"ApparentMagnitude\", DateObject({2026,9,21})})", //
        "0.364164");
    // what does not apply to a body is Missing
    check("PlanetData(\"Earth\", {\"ApparentMagnitude\", DateObject({2003,8,28})})", //
        "Missing(NotApplicable)");
    check("PlanetData(\"Earth\", {\"DistanceFromSun\", DateObject({2003,8,28})})", //
        "Quantity(1.01031,\"AstronomicalUnit\")");
    // the Sun and the Moon at perihelion and at the perigee full moon of 2026-01-03, both about
    // 32.5 and 33 minutes of arc
    check("AstronomicalData(\"Sun\", {\"ApparentMagnitude\", DateObject({2026,1,3})})", //
        "-26.77656");
    check("AstronomicalData(\"Sun\", {\"AngularDiameter\", DateObject({2026,1,3})})", //
        "Quantity(1951.848,\"Arcseconds\")");
    check("AstronomicalData(\"Moon\", {\"AngularDiameter\", DateObject({2026,1,3})})", //
        "Quantity(1983.932,\"Arcseconds\")");
    check("AstronomicalData(\"Moon\", {\"ApparentMagnitude\", DateObject({2026,1,3})})", //
        "Missing(NotApplicable)");
    check("AstronomicalData(\"Pluto\", {\"ApparentMagnitude\", DateObject({2026,1,3})})", //
        "14.54846");
  }

  /**
   * <code>HelioCoordinates</code> for Mars at one instant, measured 2026-09-18, which this matches
   * to about 150 km. The ecliptic is the one of the date asked for - pinned at J2000 the vector
   * stays turned by the precession since then and lands 0.014 astronomical units away.
   */
  @Test
  public void testPlanetDataAgreesWithWMA() {
    check(
        "Max(Abs(QuantityMagnitude(PlanetData(\"Mars\","
            + " {\"HelioCoordinates\", {2026,9,18,16,39,57}}))"
            + " - {0.281385, 1.516740, 0.0246979})) < 10^-5", //
        "True");
  }

  /** The two spellings read the same ephemerides, so they answer with the same vector. */
  @Test
  public void testPlanetDataAgreesWithAstronomicalData() {
    check(
        "Chop(149597870700 * QuantityMagnitude(PlanetData(\"Mars\","
            + " {\"HelioCoordinates\", DateObject({2020,1,1})}))"
            + " - AstronomicalData(\"Mars\", {\"Position\", DateObject({2020,1,1})}), 1)", //
        "{0,0,0}");
  }

  /**
   * The astro types join the entity registry, so the generic <code>EntityValue</code> and
   * <code>EntityList</code> reach them without knowing anything about astronomy.
   */
  @Test
  public void testAstroEntities() {
    check("EntityList(\"Planet\")[[4]]", //
        "Entity(Planet,Mars)");
    check("Length(EntityValue(Entity(\"Planet\", \"Mars\"), \"HelioCoordinates\"))", //
        "3");
    // a list is a list of properties to EntityValue, so a date is asked of PlanetData itself
    check(
        "QuantityUnit(First(EntityValue(Entity(\"Planet\", \"Earth\"),"
            + " EntityProperty(\"Planet\", \"HelioCoordinates\"))))", //
        "AstronomicalUnit");
    check(
        "Round(1000*Norm(QuantityMagnitude(PlanetData(Entity(\"Planet\", \"Earth\"),"
            + " {\"HelioCoordinates\", DateObject({2020,1,1})}))))", //
        "983");
    // a star is a different type, answered by a different function through the same call
    check("EntityValue(Entity(\"Star\", \"Sirius\"), \"ApparentMagnitude\")", //
        "-1.44");
    check("StarData(Entity(\"Star\", \"Sirius\"), \"Constellation\")", //
        "Canis Major");
    check("Head(First(StarData()))", //
        "Entity");
    check("Take(StarData(\"Properties\"), 2)", //
        "{EntityProperty(Star,Name),EntityProperty(Star,AlternateNames)}");
    // an entity of the wrong type is not silently read as a name of the right one
    check("PlanetData(Entity(\"Element\", \"Iron\"), \"HelioCoordinates\")", //
        "PlanetData(Entity(Element,Iron),HelioCoordinates)");
    // and what PlanetData cannot answer is reported as the half that was unknown
    check("EntityValue(Entity(\"Planet\", \"Vulcan\"), \"HelioCoordinates\")", //
        "Missing(UnknownEntity,{Planet,Vulcan})");
    check("EntityValue(Entity(\"Planet\", \"Mars\"), \"Nonsense\")", //
        "Missing(UnknownProperty,{Planet,Nonsense})");
  }

  @Test
  public void testUnknownBody() {
    check("AstroDistance(\"Vulcan\", DateObject({2026,6,21}))", //
        "AstroDistance(Vulcan,DateObject({2026,6,21},Day))");
  }
}
