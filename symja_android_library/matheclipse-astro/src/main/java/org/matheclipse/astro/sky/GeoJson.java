package org.matheclipse.astro.sky;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Minimal GeoJSON reading for the bundled sky and world data.
 *
 * <p>
 * Public because {@link org.matheclipse.astro.geo.WorldOutline} reads the Natural Earth basemap
 * through it as well; the sky catalogue is simply where it started.
 *
 * <p>
 * Only what those files actually use is handled: a <code>FeatureCollection</code> of
 * <code>Point</code>, <code>LineString</code>, <code>MultiLineString</code>, <code>Polygon</code> and
 * <code>MultiPolygon</code>. Jackson is already on this module's classpath through
 * <code>matheclipse-core</code>, and core's own <code>JsonMini</code> is package private, so this is
 * the smallest thing that does the job.
 */
public final class GeoJson {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private GeoJson() {}

  /**
   * Read a bundled resource as a JSON tree.
   *
   * @param resource absolute classpath name, e.g. <code>/sky-data/stars.6.json</code>
   * @throws IllegalStateException if the resource is missing or unreadable, which can only mean a
   *         broken build rather than anything a user did
   */
  public static JsonNode read(String resource) {
    try (InputStream is = GeoJson.class.getResourceAsStream(resource)) {
      if (is == null) {
        throw new IllegalStateException("resource " + resource + " not found on classpath");
      }
      return MAPPER.readTree(is);
    } catch (IOException e) {
      throw new IllegalStateException("cannot read " + resource, e);
    }
  }

  /** The <code>features</code> array of a FeatureCollection, or an empty node if there is none. */
  public static JsonNode features(JsonNode root) {
    JsonNode features = root.path("features");
    return features.isArray() ? features : MAPPER.createArrayNode();
  }

  /**
   * Flatten a geometry into a list of coordinate rings, whatever its type.
   *
   * <p>
   * The callers only ever want "the sequences of points to draw", so a <code>LineString</code>, the
   * strings of a <code>MultiLineString</code> and the rings of a <code>Polygon</code> all reduce to
   * the same thing. Each entry is <code>{longitude, latitude}</code> pairs in degrees.
   */
  public static List<double[][]> rings(JsonNode geometry) {
    List<double[][]> result = new ArrayList<double[][]>();
    String type = geometry.path("type").asText("");
    JsonNode coordinates = geometry.path("coordinates");
    switch (type) {
      case "LineString":
        addRing(result, coordinates);
        break;
      case "MultiLineString":
      case "Polygon":
        for (JsonNode ring : coordinates) {
          addRing(result, ring);
        }
        break;
      case "MultiPolygon":
        for (JsonNode polygon : coordinates) {
          for (JsonNode ring : polygon) {
            addRing(result, ring);
          }
        }
        break;
      default:
        // Point and anything unexpected contribute no rings
        break;
    }
    return result;
  }

  /**
   * Split a geometry into polygons, each keeping its own rings in GeoJSON order.
   *
   * <p>
   * {@link #rings} throws that structure away, which is right for anything drawn as a line but
   * wrong for anything filled: a GeoJSON polygon's first ring is its outline and the rest are holes
   * punched in it. Flattened and filled one by one, a hole comes out as a solid shape covering
   * exactly the area it was meant to remove.
   *
   * @return one entry per polygon; within an entry, element 0 is the outline and the rest are holes
   */
  public static List<List<double[][]>> polygons(JsonNode geometry) {
    List<List<double[][]>> result = new ArrayList<List<double[][]>>();
    String type = geometry.path("type").asText("");
    JsonNode coordinates = geometry.path("coordinates");
    if ("Polygon".equals(type)) {
      addPolygon(result, coordinates);
    } else if ("MultiPolygon".equals(type)) {
      for (JsonNode polygon : coordinates) {
        addPolygon(result, polygon);
      }
    }
    return result;
  }

  private static void addPolygon(List<List<double[][]>> result, JsonNode polygon) {
    List<double[][]> rings = new ArrayList<double[][]>();
    for (JsonNode ring : polygon) {
      addRing(rings, ring);
    }
    if (!rings.isEmpty()) {
      result.add(rings);
    }
  }

  private static void addRing(List<double[][]> result, JsonNode ring) {
    if (!ring.isArray() || ring.size() == 0) {
      return;
    }
    double[][] points = new double[ring.size()][];
    for (int i = 0; i < ring.size(); i++) {
      points[i] = point(ring.get(i));
    }
    result.add(points);
  }

  /** A single <code>[longitude, latitude]</code> position. */
  public static double[] point(JsonNode coordinates) {
    if (!coordinates.isArray() || coordinates.size() < 2) {
      return new double[] {Double.NaN, Double.NaN};
    }
    return new double[] {coordinates.get(0).asDouble(), coordinates.get(1).asDouble()};
  }

  /**
   * Read a text field, with the thin spaces the star name file uses inside catalogue designations
   * (<code>"HD 48915"</code>) turned into ordinary ones.
   */
  public static String text(JsonNode node, String field) {
    String value = node.path(field).asText("");
    return value.replace('\u2009', ' ').replace('\u00a0', ' ').trim();
  }
}
