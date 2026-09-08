package org.matheclipse.astro.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.hipparchus.util.FastMath;
import org.junit.jupiter.api.Test;
import org.matheclipse.astro.project.MapProjection;
import org.matheclipse.astro.convert.AstroConvert;
import org.matheclipse.astro.sky.SkyCatalog;
import org.matheclipse.astro.sky.SkyFrame;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

/**
 * The star catalogue and the projections, tested directly rather than through the evaluator.
 *
 * <p>
 * Positions are checked against the standard J2000 coordinates of well known stars, and the
 * projections against the invariants that define them - a Mollweide pole at
 * <code>(0, sqrt 2)</code>, a Hammer extent of <code>2 sqrt 2</code> by <code>sqrt 2</code>, an
 * azimuthal projection showing exactly one hemisphere.
 */
public class SkyChartTest {

  private static final double TOLERANCE = 1.0e-4;

  /**
   * The J2000 epoch, the reference the catalogue positions are given for.
   *
   * <p>
   * Built on demand rather than in a field initialiser: the time scales need the bundled Orekit
   * data to be registered first, and a static field would run before that could happen.
   */
  private static AbsoluteDate j2000() {
    org.matheclipse.astro.data.AstroDataContext.initialize();
    return new AbsoluteDate("2000-01-01T12:00:00.000", TimeScalesFactory.getUTC());
  }

  /** The observer date used by the horizon tests, built the same way. */
  private static AbsoluteDate horizonDate() {
    org.matheclipse.astro.data.AstroDataContext.initialize();
    return new AbsoluteDate("2026-01-15T22:00:00.000", TimeScalesFactory.getUTC());
  }

  @Test
  public void testCatalogueSize() {
    SkyCatalog catalog = SkyCatalog.get();
    assertEquals(5044, catalog.brightStars().size(), "stars to magnitude 6");
    assertEquals(493, catalog.namedStars().size(), "stars with a proper name");
    // 89 rather than 88 because Serpens is split into Caput and Cauda
    assertEquals(89, catalog.constellations().size(), "constellations");
    assertEquals(110, catalog.messierObjects().size(), "Messier objects");
    assertTrue(catalog.constellationLines().size() > 100, "constellation figure lines");
    assertTrue(catalog.milkyWay().size() > 100, "Milky Way rings");
  }

  @Test
  public void testKnownStarPositions() {
    // standard J2000 coordinates
    assertStar("Sirius", 101.2872, -16.7161, -1.44, "CMa");
    assertStar("Vega", 279.2347, 38.7837, 0.03, "Lyr");
    assertStar("Polaris", 37.9545, 89.2641, 1.97, "UMi");
    assertStar("Betelgeuse", 88.7929, 7.4071, 0.45, "Ori");
  }

  private static void assertStar(String name, double rightAscension, double declination,
      double magnitude, String constellation) {
    SkyCatalog.Star star = SkyCatalog.get().star(name);
    assertTrue(star != null, name + " is in the catalogue");
    assertEquals(rightAscension, star.rightAscension, TOLERANCE, name + " right ascension");
    assertEquals(declination, star.declination, TOLERANCE, name + " declination");
    assertEquals(magnitude, star.magnitude, TOLERANCE, name + " magnitude");
    assertEquals(constellation, star.constellation, name + " constellation");
  }

  @Test
  public void testStarNameForms() {
    // every designation the catalogue records has to reach the same star
    for (String name : new String[] {"Sirius", "sirius", "SIRIUS", "alpha CMa", "alp CMa",
        "9 CMa", "HD 48915", "hd48915", "HIP 32349", "HIP32349", "GJ 244A",
        "alpha Canis Majoris"}) {
      SkyCatalog.Star star = SkyCatalog.get().star(name);
      assertTrue(star != null, name + " resolves");
      assertEquals(32349, star.hipparcos, name + " is Sirius");
    }
    assertEquals(null, SkyCatalog.get().star("Nonexistent"));
  }

  @Test
  public void testBrightestStarsInOrder() {
    List<SkyCatalog.Star> named = SkyCatalog.get().namedStars();
    assertEquals("Sirius", named.get(0).properName);
    assertEquals("Canopus", named.get(1).properName);
    assertEquals("Arcturus", named.get(2).properName);
  }

  @Test
  public void testMagnitudeLimitSelectsTheRightFile() {
    // the magnitude 6 file cannot answer a magnitude 8 request
    assertTrue(SkyCatalog.get().starsToMagnitude(6.0).size() <= 5044);
    assertTrue(SkyCatalog.get().starsToMagnitude(8.0).size() > 5044,
        "a fainter limit has to reach the deeper file");
  }

  @Test
  public void testGalacticRotation() {
    // the standard J2000 galactic frame, checked against values that define it
    SkyFrame galactic = SkyFrame.of("Galactic", j2000(), null);
    assertCoordinates("north galactic pole", galactic, 192.85948, 27.12825, null, 90.0);
    assertCoordinates("south galactic pole", galactic, 12.85948, -27.12825, null, -90.0);
    // the galactic centre is the origin of the frame by construction
    assertCoordinates("galactic centre", galactic, 266.405, -28.936, 0.0, 0.0);
    assertCoordinates("galactic anticentre", galactic, 86.405, 28.936, 180.0, 0.0);
    // and two stars whose galactic coordinates are widely tabulated
    assertCoordinates("Sirius", galactic, 101.2872, -16.7161, 227.23, -8.89);
    assertCoordinates("Vega", galactic, 279.2347, 38.7837, 67.45, 19.24);
  }

  @Test
  public void testGalacticRoundTrip() {
    SkyFrame galactic = SkyFrame.of("Galactic", j2000(), null);
    double[] forward = galactic.toFrame(101.2872, -16.7161);
    double[] back = galactic.fromFrame(forward[0], forward[1]);
    assertEquals(101.2872, back[0], 1.0e-9, "right ascension survives the round trip");
    assertEquals(-16.7161, back[1], 1.0e-9, "declination survives the round trip");
  }

  @Test
  public void testHorizonFrameAgreesWithTheObserverMachinery() {
    // AstroFunctionsTest pins Sirius at altitude 20.69436 and azimuth 177.1244 from Berlin at this
    // instant, computed through AstroObserver. The chart frame has to reproduce it exactly, or the
    // rotation here and the tested machinery disagree.
    AbsoluteDate date = horizonDate();
    GeodeticPoint berlin = AstroConvert.newGeodeticPoint(52.52, 13.405, 0.0);
    SkyFrame horizon = SkyFrame.of("Horizon", date, berlin);
    assertTrue(horizon.isHorizon(), "the horizon frame reports itself as one");
    double[] sirius = horizon.toFrame(101.2872, -16.7161);
    assertEquals(177.1244, sirius[0], 1.0e-3, "Sirius azimuth");
    assertEquals(20.69436, sirius[1], 1.0e-4, "Sirius altitude");
    // Polaris sits within three quarters of a degree of the pole, so its altitude is the latitude
    double[] polaris = horizon.toFrame(37.9545, 89.2641);
    assertEquals(52.89931, polaris[1], 1.0e-4, "Polaris altitude");
  }

  @Test
  public void testHorizonFrameNeedsALocation() {
    AbsoluteDate date = horizonDate();
    assertEquals(null, SkyFrame.of("Horizon", date, null), "a horizon needs somewhere to stand");
    assertEquals(null, SkyFrame.of("Nonsense", date, null), "an unknown frame is rejected");
  }

  private static void assertCoordinates(String label, SkyFrame frame, double rightAscension,
      double declination, Double expectedLongitude, double expectedLatitude) {
    double[] actual = frame.toFrame(rightAscension, declination);
    if (expectedLongitude != null) {
      // the tabulated positions are rounded, so a hundredth of a degree is the useful tolerance
      assertEquals(expectedLongitude.doubleValue(), actual[0], 0.01, label + " longitude");
    }
    assertEquals(expectedLatitude, actual[1], 0.01, label + " latitude");
  }

  @Test
  public void testProjectionNames() {
    for (String name : MapProjection.names()) {
      assertTrue(MapProjection.of(name, 0.0, 0.0) != null, name + " resolves");
    }
    assertEquals(null, MapProjection.of("Nonsense", 0.0, 0.0));
  }

  @Test
  public void testEquirectangularIsIdentity() {
    MapProjection projection = MapProjection.of("Equirectangular", 0.0, 0.0);
    double[] xy = projection.project(FastMath.toRadians(45.0), FastMath.toRadians(30.0));
    assertEquals(FastMath.toRadians(45.0), xy[0], TOLERANCE);
    assertEquals(FastMath.toRadians(30.0), xy[1], TOLERANCE);
  }

  @Test
  public void testMollweideInvariants() {
    MapProjection projection = MapProjection.of("Mollweide", 0.0, 0.0);
    // the pole sits at (0, sqrt 2) and the equator ends at (2 sqrt 2, 0)
    double[] pole = projection.project(0.0, FastMath.PI / 2.0);
    assertEquals(0.0, pole[0], TOLERANCE);
    assertEquals(FastMath.sqrt(2.0), pole[1], TOLERANCE);
    double[] edge = projection.project(FastMath.PI, 0.0);
    assertEquals(2.0 * FastMath.sqrt(2.0), edge[0], TOLERANCE);
    assertEquals(0.0, edge[1], TOLERANCE);
  }

  @Test
  public void testHammerIsEqualArea() {
    MapProjection projection = MapProjection.of("Hammer", 0.0, 0.0);
    double maxX = 0.0;
    double maxY = 0.0;
    for (int lon = -180; lon <= 180; lon += 5) {
      for (int lat = -90; lat <= 90; lat += 5) {
        double[] xy = projection.project(FastMath.toRadians(lon), FastMath.toRadians(lat));
        maxX = FastMath.max(maxX, FastMath.abs(xy[0]));
        maxY = FastMath.max(maxY, FastMath.abs(xy[1]));
      }
    }
    assertEquals(2.0 * FastMath.sqrt(2.0), maxX, TOLERANCE, "Hammer half-width");
    assertEquals(FastMath.sqrt(2.0), maxY, TOLERANCE, "Hammer half-height");
  }

  @Test
  public void testAzimuthalProjectionsClipTheFarSide() {
    MapProjection orthographic = MapProjection.of("Orthographic", 0.0, 0.0);
    // the point opposite the centre is never visible, the centre always is
    assertTrue(orthographic.project(0.0, 0.0) != null, "centre is visible");
    assertEquals(null, orthographic.project(FastMath.PI, 0.0), "antipode is clipped");
    int visible = 0;
    int clipped = 0;
    for (int lon = -180; lon < 180; lon += 5) {
      for (int lat = -90; lat <= 90; lat += 5) {
        if (orthographic.project(FastMath.toRadians(lon), FastMath.toRadians(lat)) == null) {
          clipped++;
        } else {
          visible++;
        }
      }
    }
    // a hemisphere either way, give or take the sampling
    assertTrue(visible > clipped * 0.8 && clipped > visible * 0.8,
        "orthographic shows about half the sphere: " + visible + " visible, " + clipped
            + " clipped");
  }

  @Test
  public void testGnomonicClipsWellShortOfTheHorizon() {
    MapProjection gnomonic = MapProjection.of("Gnomonic", 0.0, 0.0);
    assertTrue(gnomonic.project(FastMath.toRadians(45.0), 0.0) != null, "45 degrees is shown");
    assertEquals(null, gnomonic.project(FastMath.toRadians(85.0), 0.0),
        "85 degrees is past the usable limit");
  }
}
