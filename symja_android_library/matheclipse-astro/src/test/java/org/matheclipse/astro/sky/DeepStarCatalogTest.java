package org.matheclipse.astro.sky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.astro.meeus.SpectralColor;

/**
 * The Night Vision star database: the magnitude prefix loading, the cap query and the counts
 * documented in <code>star-data/README.md</code>.
 */
public class DeepStarCatalogTest {

  private static final DeepStarCatalog CATALOG = DeepStarCatalog.get();

  @Test
  public void testBrightestIsSirius() {
    List<double[]> stars = new ArrayList<double[]>();
    // everything to magnitude 0 in the whole sky
    CATALOG.forEachInCap(0.0, 0.0, 180.0, 0.0, 100,
        (index, ra, dec, mag) -> stars.add(new double[] {index, ra, dec, mag}));
    double[] sirius = stars.get(0);
    assertEquals(0, (int) sirius[0]);
    // Sirius: 6h45m09s, -16 42' 58" (J2000), magnitude -1.46
    assertEquals(101.2875, sirius[1], 0.01);
    assertEquals(-16.7161, sirius[2], 0.01);
    assertEquals(-1.46, sirius[3], 1e-9);
    assertEquals("A1", CATALOG.spectralType(0));
    assertEquals(SpectralColor.index("A1"), CATALOG.colorIndex(0));
    // sorted by magnitude
    for (int i = 1; i < stars.size(); i++) {
      assertTrue(stars.get(i - 1)[3] <= stars.get(i)[3]);
    }
  }

  @Test
  public void testCounts() {
    assertEquals(5254, CATALOG.count(6.0));
    assertEquals(47580, CATALOG.count(8.0));
    // asking for less after more still counts only the prefix
    assertEquals(5254, CATALOG.count(6.0));
  }

  @Test
  public void testCap() {
    // Orion's belt, 5 degrees around Alnilam (5h36m, -1.2), to magnitude 9
    List<double[]> stars = new ArrayList<double[]>();
    double limit = CATALOG.forEachInCap(84.05, -1.20, 5.0, 9.0, Integer.MAX_VALUE,
        (index, ra, dec, mag) -> stars.add(new double[] {ra, dec, mag}));
    assertEquals(9.0, limit, 0.0);
    assertTrue(stars.size() > 100, "stars: " + stars.size());
    for (double[] star : stars) {
      assertTrue(Math.abs(star[0] - 84.05) < 5.1 && Math.abs(star[1] + 1.2) <= 5.0);
      assertTrue(star[2] <= 9.0);
    }
    // the three belt stars are the brightest in the cap
    assertTrue(stars.get(0)[2] < 2.1 && stars.get(2)[2] < 2.3);

    // a cap on the count keeps the brightest stars and reports where it stopped
    List<double[]> capped = new ArrayList<double[]>();
    double effective = CATALOG.forEachInCap(84.05, -1.20, 5.0, 9.0, 50,
        (index, ra, dec, mag) -> capped.add(new double[] {ra, dec, mag}));
    assertEquals(50, capped.size());
    assertTrue(effective < 9.0 && effective >= capped.get(49)[2], "effective " + effective);
    for (int i = 0; i < 50; i++) {
      assertEquals(stars.get(i)[2], capped.get(i)[2], 0.0);
    }
  }

  @Test
  public void testMatch() {
    // Betelgeuse: 5h55m10s, +7 24' 25", magnitude about 0.45, spectral type M1-M2
    int index = CATALOG.match(88.7929, 7.4071, 0.05, 0.45, 0.7);
    assertTrue(index >= 0);
    assertEquals('M', CATALOG.spectralType(index).charAt(0));
    // nothing that bright in an empty spot of sky
    assertEquals(-1, CATALOG.match(88.0, 20.0, 0.05, 0.45, 0.7));
  }

  /** Loads the whole 25 MB file. */
  @Tag("slow")
  @Test
  public void testWholeFile() {
    assertEquals(1064436, CATALOG.size());
    assertEquals(371994, CATALOG.count(10.0));
    assertEquals(982168, CATALOG.count(11.0));
  }
}
