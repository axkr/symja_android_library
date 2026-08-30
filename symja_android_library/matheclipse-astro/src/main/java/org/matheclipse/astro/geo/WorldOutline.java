package org.matheclipse.astro.geo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.matheclipse.astro.sky.GeoJson;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The world landmass outline drawn under a {@code GeoGraphics} chart.
 *
 * <p>
 * Loaded from the bundled <code>ne_110m_land.geojson</code>, which is Natural Earth data and in the
 * public domain; see the README beside the resource. Coordinates are
 * <code>{longitude, latitude}</code> in degrees on WGS84, the same convention
 * <code>GeoPosition</code> uses, so nothing is converted on load.
 *
 * <p>
 * Parsed on first use and then held, so a chart which draws only its own primitives never reads the
 * file.
 */
public final class WorldOutline {

  private static final class Holder {
    private static final WorldOutline INSTANCE = new WorldOutline();
  }

  public static WorldOutline get() {
    return Holder.INSTANCE;
  }

  private final Object lock = new Object();

  private List<double[][]> land;

  private WorldOutline() {}

  /**
   * The landmass rings, each a closed ring of <code>{longitude, latitude}</code> pairs in degrees.
   */
  public List<double[][]> land() {
    synchronized (lock) {
      if (land == null) {
        List<double[][]> rings = new ArrayList<double[][]>();
        for (JsonNode feature : GeoJson
            .features(GeoJson.read("/geo-data/ne_110m_land.geojson"))) {
          rings.addAll(GeoJson.rings(feature.path("geometry")));
        }
        land = Collections.unmodifiableList(rings);
      }
      return land;
    }
  }
}
