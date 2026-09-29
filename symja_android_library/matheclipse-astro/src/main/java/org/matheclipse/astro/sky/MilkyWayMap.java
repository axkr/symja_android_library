package org.matheclipse.astro.sky;

import java.util.ArrayList;
import java.util.List;
import org.hipparchus.util.FastMath;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The brightness of the Milky Way at any point of the sky, from the five contour levels of the
 * d3-celestial outline (<code>milkyway.json</code>, <code>ol1</code> faintest to <code>ol5</code>
 * brightest).
 *
 * <p>
 * The contours are turned once into a grid of half a degree in galactic coordinates, counting for
 * each cell how many levels it lies inside, and the grid is then smoothed, so that a chart can
 * paint the band as the soft glow it is rather than as five hard-edged steps.
 *
 * <p>
 * A cell is inside a level when a ray from it to the north galactic pole crosses the level's rings
 * an odd number of times. Every level is a band along the galactic equator with holes and islands,
 * none of which reaches the pole, so the count is right for all of them at once - the band edges,
 * the dark lanes recorded as holes and the detached clouds - without telling outlines and holes
 * apart. Working along meridians of galactic longitude also means the band, which runs right round
 * the sky, is never cut open at a seam.
 */
public final class MilkyWayMap {

  /** Cells per degree. */
  private static final int PER_DEGREE = 2;

  private static final int COLUMNS = 360 * PER_DEGREE;

  private static final int ROWS = 180 * PER_DEGREE;

  /** The number of contour levels. */
  public static final int LEVELS = 5;

  /**
   * The rotation from J2000 equatorial to galactic coordinates (Hipparcos, ESA 1997, vol. 1,
   * eq. 1.5.11).
   */
  private static final double[][] TO_GALACTIC = {
      {-0.0548755604162154, -0.8734370902348850, -0.4838350155487132},
      {0.4941094278755837, -0.4448296299600112, 0.7469822444972189},
      {-0.8676661490190047, -0.1980763734312015, 0.4559837761750669}};

  private static volatile MilkyWayMap instance;

  /** Smoothed level, 0 to {@link #LEVELS}, by column (longitude) and row (latitude from -90). */
  private final float[][] level;

  private MilkyWayMap(float[][] level) {
    this.level = level;
  }

  /** The map, built on first use from the bundled contours. */
  public static MilkyWayMap get() {
    MilkyWayMap result = instance;
    if (result == null) {
      synchronized (MilkyWayMap.class) {
        result = instance;
        if (result == null) {
          result = new MilkyWayMap(build());
          instance = result;
        }
      }
    }
    return result;
  }

  /**
   * The brightness at a J2000 position, from 0 outside the Milky Way to {@link #LEVELS} in its
   * brightest parts, interpolated between the cells.
   */
  public double brightness(double rightAscension, double declination) {
    double[] galactic = toGalactic(rightAscension, declination);
    double x = galactic[0] * PER_DEGREE - 0.5;
    double y = (galactic[1] + 90.0) * PER_DEGREE - 0.5;
    int x0 = (int) FastMath.floor(x);
    int y0 = (int) FastMath.floor(y);
    double fx = x - x0;
    double fy = y - y0;
    return (1.0 - fx) * ((1.0 - fy) * cell(x0, y0) + fy * cell(x0, y0 + 1))
        + fx * ((1.0 - fy) * cell(x0 + 1, y0) + fy * cell(x0 + 1, y0 + 1));
  }

  private double cell(int column, int row) {
    int c = Math.floorMod(column, COLUMNS);
    int r = FastMath.max(0, FastMath.min(ROWS - 1, row));
    return level[c][r];
  }

  /** Galactic longitude in [0, 360) and latitude, in degrees, of a J2000 position in degrees. */
  static double[] toGalactic(double rightAscension, double declination) {
    double ra = FastMath.toRadians(rightAscension);
    double dec = FastMath.toRadians(declination);
    double x = FastMath.cos(dec) * FastMath.cos(ra);
    double y = FastMath.cos(dec) * FastMath.sin(ra);
    double z = FastMath.sin(dec);
    double gx = TO_GALACTIC[0][0] * x + TO_GALACTIC[0][1] * y + TO_GALACTIC[0][2] * z;
    double gy = TO_GALACTIC[1][0] * x + TO_GALACTIC[1][1] * y + TO_GALACTIC[1][2] * z;
    double gz = TO_GALACTIC[2][0] * x + TO_GALACTIC[2][1] * y + TO_GALACTIC[2][2] * z;
    double longitude = FastMath.toDegrees(FastMath.atan2(gy, gx));
    if (longitude < 0.0) {
      longitude += 360.0;
    }
    double latitude = FastMath.toDegrees(FastMath.asin(FastMath.max(-1.0, FastMath.min(1.0, gz))));
    return new double[] {longitude, latitude};
  }

  private static float[][] build() {
    float[][] count = new float[COLUMNS][ROWS];
    for (JsonNode feature : GeoJson.features(GeoJson.read("/sky-data/milkyway.json"))) {
      // every ring of the level together; the ray parity sorts out what is inside
      List<double[][]> rings = new ArrayList<double[][]>();
      for (List<double[][]> polygon : GeoJson.polygons(feature.path("geometry"))) {
        for (double[][] ring : polygon) {
          double[][] galactic = new double[ring.length][];
          for (int i = 0; i < ring.length; i++) {
            galactic[i] = toGalactic(ring[i][0], ring[i][1]);
          }
          rings.add(galactic);
        }
      }
      addLevel(count, rings);
    }
    return smooth(count);
  }

  /** Add one to every cell inside the given rings. */
  private static void addLevel(float[][] count, List<double[][]> rings) {
    // the latitudes where the rings cross each column's meridian
    List<List<Double>> crossings = new ArrayList<List<Double>>(COLUMNS);
    for (int c = 0; c < COLUMNS; c++) {
      crossings.add(new ArrayList<Double>());
    }
    for (double[][] ring : rings) {
      for (int i = 0; i < ring.length; i++) {
        double[] a = ring[i];
        double[] b = ring[(i + 1) % ring.length];
        double delta = b[0] - a[0];
        if (delta > 180.0) {
          delta -= 360.0;
        } else if (delta <= -180.0) {
          delta += 360.0;
        }
        if (delta == 0.0) {
          continue;
        }
        // the column centres this edge passes, taken half open so a shared vertex counts once
        double from = FastMath.min(a[0], a[0] + delta);
        double to = FastMath.max(a[0], a[0] + delta);
        int first = (int) FastMath.ceil(from * PER_DEGREE - 0.5);
        int last = (int) FastMath.ceil(to * PER_DEGREE - 0.5) - 1;
        for (int k = first; k <= last; k++) {
          double longitude = (k + 0.5) / PER_DEGREE;
          double t = (longitude - a[0]) / delta;
          crossings.get(Math.floorMod(k, COLUMNS)).add(a[1] + t * (b[1] - a[1]));
        }
      }
    }
    for (int c = 0; c < COLUMNS; c++) {
      List<Double> column = crossings.get(c);
      if (column.isEmpty()) {
        continue;
      }
      double[] sorted = new double[column.size()];
      for (int i = 0; i < sorted.length; i++) {
        sorted[i] = column.get(i);
      }
      java.util.Arrays.sort(sorted);
      // walk down from the pole, flipping at each crossing
      int next = sorted.length - 1;
      boolean inside = false;
      for (int r = ROWS - 1; r >= 0; r--) {
        double latitude = (r + 0.5) / PER_DEGREE - 90.0;
        while (next >= 0 && sorted[next] > latitude) {
          inside = !inside;
          next--;
        }
        if (inside) {
          count[c][r] += 1.0f;
        }
      }
    }
  }

  /** Two passes of a 5 by 5 box blur, which turns the contour steps into a glow. */
  private static float[][] smooth(float[][] grid) {
    float[][] result = grid;
    for (int pass = 0; pass < 2; pass++) {
      float[][] next = new float[COLUMNS][ROWS];
      for (int c = 0; c < COLUMNS; c++) {
        for (int r = 0; r < ROWS; r++) {
          float sum = 0.0f;
          int n = 0;
          for (int dc = -2; dc <= 2; dc++) {
            float[] column = result[Math.floorMod(c + dc, COLUMNS)];
            for (int dr = -2; dr <= 2; dr++) {
              int rr = r + dr;
              if (rr >= 0 && rr < ROWS) {
                sum += column[rr];
                n++;
              }
            }
          }
          next[c][r] = sum / n;
        }
      }
      result = next;
    }
    return result;
  }
}
