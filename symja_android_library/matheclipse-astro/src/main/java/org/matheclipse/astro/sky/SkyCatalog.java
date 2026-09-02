package org.matheclipse.astro.sky;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The bundled star, constellation and deep sky catalogue.
 *
 * <p>
 * The data comes from <a href="https://github.com/ofrohn/d3-celestial">d3-celestial</a> under the
 * BSD 3 clause licence; see the README beside the resources. Positions are J2000, and right
 * ascension is stored the way GeoJSON stores longitude, in the range <code>-180</code> to
 * <code>180</code>. That is deliberately convenient: charting the sky then becomes the same problem
 * as projecting a map, which is why {@link org.matheclipse.astro.project.MapProjection} serves both.
 *
 * <p>
 * Each file is parsed on first use and then held, so a session which only asks for
 * {@code StarData("Sirius")} never touches the five megabyte magnitude 8.5 file or the deep sky one.
 */
public final class SkyCatalog {

  /** A single catalogued star. */
  public static final class Star {

    /** Hipparcos catalogue number. */
    public final int hipparcos;

    /** Right ascension in degrees, in the range <code>[0, 360)</code>. */
    public final double rightAscension;

    /** Declination in degrees. */
    public final double declination;

    /** Apparent visual magnitude. */
    public final double magnitude;

    /** B-V colour index, or {@link Double#NaN} when the catalogue has none. */
    public final double colorIndex;

    /** Proper name such as {@code "Sirius"}, or {@code ""}. */
    public final String properName;

    /** Bayer designation as a Greek letter, or {@code ""}. */
    public final String bayer;

    /** Flamsteed number, or {@code ""}. */
    public final String flamsteed;

    /** Henry Draper designation such as {@code "HD 48915"}, or {@code ""}. */
    public final String henryDraper;

    /** Gliese designation, or {@code ""}. */
    public final String gliese;

    /** Variable star designation, or {@code ""}. */
    public final String variable;

    /** Three letter IAU constellation code such as {@code "CMa"}, or {@code ""}. */
    public final String constellation;

    Star(int hipparcos, double rightAscension, double declination, double magnitude,
        double colorIndex, String properName, String bayer, String flamsteed, String henryDraper,
        String gliese, String variable, String constellation) {
      this.hipparcos = hipparcos;
      this.rightAscension = rightAscension;
      this.declination = declination;
      this.magnitude = magnitude;
      this.colorIndex = colorIndex;
      this.properName = properName;
      this.bayer = bayer;
      this.flamsteed = flamsteed;
      this.henryDraper = henryDraper;
      this.gliese = gliese;
      this.variable = variable;
      this.constellation = constellation;
    }
  }

  /** A nebula, cluster or galaxy. */
  public static final class DeepSkyObject {
    public final String name;
    public final String designation;
    public final String alternateName;

    /** Short type code such as {@code "gg"} for galaxy or {@code "snr"} for supernova remnant. */
    public final String type;

    public final double magnitude;
    public final double rightAscension;
    public final double declination;

    DeepSkyObject(String name, String designation, String alternateName, String type,
        double magnitude, double rightAscension, double declination) {
      this.name = name;
      this.designation = designation;
      this.alternateName = alternateName;
      this.type = type;
      this.magnitude = magnitude;
      this.rightAscension = rightAscension;
      this.declination = declination;
    }
  }

  /** One of the IAU constellations. */
  public static final class Constellation {
    /** Three letter IAU code, e.g. {@code "CMa"}. */
    public final String code;

    /** Full IAU name, e.g. {@code "Canis Major"}. */
    public final String name;

    /** Genitive form used in star designations, e.g. {@code "Canis Majoris"}. */
    public final String genitive;

    /** 1 to 3, for drawing labels at a size that suits the constellation. */
    public final int rank;

    public final double labelRightAscension;
    public final double labelDeclination;

    Constellation(String code, String name, String genitive, int rank, double labelRightAscension,
        double labelDeclination) {
      this.code = code;
      this.name = name;
      this.genitive = genitive;
      this.rank = rank;
      this.labelRightAscension = labelRightAscension;
      this.labelDeclination = labelDeclination;
    }
  }

  /** The magnitude limit of the smaller of the two bundled star files. */
  public static final double BRIGHT_MAGNITUDE_LIMIT = 6.0;

  private static final class Holder {
    private static final SkyCatalog INSTANCE = new SkyCatalog();
  }

  public static SkyCatalog get() {
    return Holder.INSTANCE;
  }

  private final Object lock = new Object();

  private List<Star> brightStars;
  private List<Star> deepStars;
  private Map<String, Star> nameIndex;
  private Map<String, Constellation> constellationsByName;
  private List<Constellation> constellationList;
  private List<double[][]> constellationLines;
  private List<double[][]> constellationBoundaries;
  private List<double[][]> milkyWay;
  private List<List<double[][]>> milkyWayPolygons;
  private List<DeepSkyObject> messier;
  private List<DeepSkyObject> deepSky;

  private SkyCatalog() {}

  // ------------------------------------------------------------------ stars

  /**
   * Every star at least as bright as {@code magnitudeLimit}.
   *
   * <p>
   * Reads the magnitude 6 file when that is enough and the magnitude 8.5 file otherwise, so a whole
   * sky chart does not pay to project forty thousand points it cannot show.
   */
  public List<Star> starsToMagnitude(double magnitudeLimit) {
    List<Star> source =
        magnitudeLimit <= BRIGHT_MAGNITUDE_LIMIT ? brightStars() : deepStars();
    List<Star> result = new ArrayList<Star>();
    for (Star star : source) {
      if (star.magnitude <= magnitudeLimit) {
        result.add(star);
      }
    }
    return result;
  }

  /** All stars to magnitude 6. */
  public List<Star> brightStars() {
    synchronized (lock) {
      if (brightStars == null) {
        brightStars = loadStars("/sky-data/stars.6.json");
      }
      return brightStars;
    }
  }

  /** All stars to magnitude 8.5. */
  public List<Star> deepStars() {
    synchronized (lock) {
      if (deepStars == null) {
        deepStars = loadStars("/sky-data/stars.8.json");
      }
      return deepStars;
    }
  }

  /**
   * Look a star up by any of its names: proper name, Bayer or Flamsteed designation with the
   * constellation code or genitive, Henry Draper, Hipparcos or Gliese number. Matching ignores case
   * and whitespace, so {@code "HD48915"} and {@code "hd 48915"} both work.
   *
   * @return the star, or <code>null</code> if nothing matches
   */
  public Star star(String name) {
    return nameIndex().get(normalize(name));
  }

  /** The stars which have a proper name, in order of brightness. */
  public List<Star> namedStars() {
    List<Star> result = new ArrayList<Star>();
    for (Star star : brightStars()) {
      if (!star.properName.isEmpty()) {
        result.add(star);
      }
    }
    result.sort((a, b) -> Double.compare(a.magnitude, b.magnitude));
    return result;
  }

  private List<Star> loadStars(String resource) {
    Map<Integer, JsonNode> names = starNames();
    List<Star> result = new ArrayList<Star>();
    for (JsonNode feature : GeoJson.features(GeoJson.read(resource))) {
      int hip = feature.path("id").asInt(-1);
      double[] position = GeoJson.point(feature.path("geometry").path("coordinates"));
      JsonNode properties = feature.path("properties");
      double magnitude = properties.path("mag").asDouble(Double.NaN);
      // the colour index is stored as a string and is empty for stars which have none
      String bv = properties.path("bv").asText("");
      double colorIndex = Double.NaN;
      if (!bv.isEmpty()) {
        try {
          colorIndex = Double.parseDouble(bv);
        } catch (NumberFormatException nfe) {
          colorIndex = Double.NaN;
        }
      }
      JsonNode name = names.get(Integer.valueOf(hip));
      result.add(new Star(hip, normalizeRightAscensionDegrees(position[0]), position[1], magnitude,
          colorIndex, //
          name == null ? "" : GeoJson.text(name, "name"), //
          name == null ? "" : GeoJson.text(name, "bayer"), //
          name == null ? "" : GeoJson.text(name, "flam"), //
          name == null ? "" : GeoJson.text(name, "hd"), //
          name == null ? "" : GeoJson.text(name, "gl"), //
          name == null ? "" : GeoJson.text(name, "var"), //
          name == null ? "" : GeoJson.text(name, "c")));
    }
    return Collections.unmodifiableList(result);
  }

  private Map<Integer, JsonNode> starNames() {
    Map<Integer, JsonNode> result = new LinkedHashMap<Integer, JsonNode>();
    JsonNode root = GeoJson.read("/sky-data/starnames.json");
    for (Map.Entry<String, JsonNode> entry : iterable(root)) {
      try {
        result.put(Integer.valueOf(entry.getKey()), entry.getValue());
      } catch (NumberFormatException nfe) {
        // the file is keyed by Hipparcos number; ignore anything that is not one
      }
    }
    return result;
  }

  private Map<String, Star> nameIndex() {
    synchronized (lock) {
      if (nameIndex == null) {
        Map<String, Star> index = new LinkedHashMap<String, Star>();
        for (Star star : brightStars()) {
          addName(index, star.properName, star);
          addName(index, "HIP" + star.hipparcos, star);
          addName(index, star.henryDraper, star);
          addName(index, star.gliese, star);
          if (!star.constellation.isEmpty()) {
            Constellation constellation = constellation(star.constellation);
            String genitive = constellation == null ? "" : constellation.genitive;
            for (String designation : GreekLetters.spellings(star.bayer)) {
              addName(index, designation + star.constellation, star);
              addName(index, designation + genitive, star);
            }
            if (!star.flamsteed.isEmpty()) {
              addName(index, star.flamsteed + star.constellation, star);
              addName(index, star.flamsteed + genitive, star);
            }
            if (!star.variable.isEmpty()) {
              addName(index, star.variable + star.constellation, star);
            }
          }
        }
        nameIndex = index;
      }
      return nameIndex;
    }
  }

  /** Register a name, keeping the first star to claim it so brighter entries win. */
  private static void addName(Map<String, Star> index, String name, Star star) {
    String key = normalize(name);
    if (!key.isEmpty()) {
      index.putIfAbsent(key, star);
    }
  }

  /** Lower case with all whitespace removed, so spacing in a designation does not matter. */
  static String normalize(String name) {
    if (name == null) {
      return "";
    }
    StringBuilder buf = new StringBuilder(name.length());
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (!Character.isWhitespace(c) && c != '\u2009' && c != '\u00a0') {
        buf.append(Character.toLowerCase(c));
      }
    }
    return buf.toString();
  }

  // --------------------------------------------------------- constellations

  /** Look a constellation up by its three letter code, case insensitively. */
  public Constellation constellation(String code) {
    return constellationsByName().get(normalize(code));
  }

  /**
   * The constellations, 89 entries for the 88 IAU figures: Serpens is split into Serpens Caput and
   * Serpens Cauda, which share the code {@code "Ser"}. Looking {@code "Ser"} up by code returns the
   * first of the two.
   */
  public Collection<Constellation> constellations() {
    loadConstellations();
    return constellationList;
  }

  private Map<String, Constellation> constellationsByName() {
    loadConstellations();
    return constellationsByName;
  }

  private void loadConstellations() {
    synchronized (lock) {
      if (constellationsByName != null) {
        return;
      }
      Map<String, Constellation> byName = new LinkedHashMap<String, Constellation>();
      List<Constellation> list = new ArrayList<Constellation>();
      for (JsonNode feature : GeoJson.features(GeoJson.read("/sky-data/constellations.json"))) {
        JsonNode properties = feature.path("properties");
        double[] label = GeoJson.point(feature.path("geometry").path("coordinates"));
        Constellation constellation = new Constellation(//
            feature.path("id").asText(""), //
            GeoJson.text(properties, "name"), //
            GeoJson.text(properties, "gen"), //
            properties.path("rank").asInt(3), //
            normalizeRightAscensionDegrees(label[0]), label[1]);
        list.add(constellation);
        byName.putIfAbsent(normalize(constellation.code), constellation);
        byName.putIfAbsent(normalize(constellation.name), constellation);
        byName.putIfAbsent(normalize(constellation.genitive), constellation);
      }
      constellationList = Collections.unmodifiableList(list);
      constellationsByName = byName;
    }
  }

  /** The constellation figure lines, as polylines of <code>{rightAscension, declination}</code>. */
  public List<double[][]> constellationLines() {
    synchronized (lock) {
      if (constellationLines == null) {
        constellationLines = loadRings("/sky-data/constellations.lines.json");
      }
      return constellationLines;
    }
  }

  /** The IAU constellation boundaries. */
  public List<double[][]> constellationBoundaries() {
    synchronized (lock) {
      if (constellationBoundaries == null) {
        constellationBoundaries = loadRings("/sky-data/constellations.bounds.json");
      }
      return constellationBoundaries;
    }
  }

  /** The outline of the Milky Way, as a set of closed rings. */
  public List<double[][]> milkyWay() {
    synchronized (lock) {
      if (milkyWay == null) {
        milkyWay = loadRings("/sky-data/milkyway.json");
      }
      return milkyWay;
    }
  }

  /**
   * The Milky Way contours as polygons, each with its outline first and its holes after.
   *
   * <p>
   * Use this rather than {@link #milkyWay()} when the contours are being filled: the outermost
   * contour is a band right round the sky with the starless middle recorded as a hole, so filling
   * every ring alike paints the sky that is not the Milky Way.
   */
  public List<List<double[][]>> milkyWayPolygons() {
    synchronized (lock) {
      if (milkyWayPolygons == null) {
        List<List<double[][]>> result = new ArrayList<List<double[][]>>();
        for (JsonNode feature : GeoJson.features(GeoJson.read("/sky-data/milkyway.json"))) {
          result.addAll(GeoJson.polygons(feature.path("geometry")));
        }
        milkyWayPolygons = Collections.unmodifiableList(result);
      }
      return milkyWayPolygons;
    }
  }

  private static List<double[][]> loadRings(String resource) {
    List<double[][]> result = new ArrayList<double[][]>();
    for (JsonNode feature : GeoJson.features(GeoJson.read(resource))) {
      result.addAll(GeoJson.rings(feature.path("geometry")));
    }
    return Collections.unmodifiableList(result);
  }

  // ------------------------------------------------------------- deep sky

  /** The 110 Messier objects. */
  public List<DeepSkyObject> messierObjects() {
    synchronized (lock) {
      if (messier == null) {
        messier = loadDeepSky("/sky-data/messier.json");
      }
      return messier;
    }
  }

  /** Deep sky objects at least as bright as {@code magnitudeLimit}, from the magnitude 14 set. */
  public List<DeepSkyObject> deepSkyObjects(double magnitudeLimit) {
    synchronized (lock) {
      if (deepSky == null) {
        deepSky = loadDeepSky("/sky-data/dsos.14.json");
      }
    }
    List<DeepSkyObject> result = new ArrayList<DeepSkyObject>();
    for (DeepSkyObject object : deepSky) {
      // objects with no recorded magnitude are kept only when no limit is being applied
      if (object.magnitude <= magnitudeLimit
          || (Double.isNaN(object.magnitude) && Double.isInfinite(magnitudeLimit))) {
        result.add(object);
      }
    }
    return result;
  }

  private static List<DeepSkyObject> loadDeepSky(String resource) {
    List<DeepSkyObject> result = new ArrayList<DeepSkyObject>();
    for (JsonNode feature : GeoJson.features(GeoJson.read(resource))) {
      JsonNode properties = feature.path("properties");
      double[] position = GeoJson.point(feature.path("geometry").path("coordinates"));
      result.add(new DeepSkyObject(//
          GeoJson.text(properties, "name"), //
          GeoJson.text(properties, "desig"), //
          GeoJson.text(properties, "alt"), //
          GeoJson.text(properties, "type"), //
          deepSkyMagnitude(properties), //
          normalizeRightAscensionDegrees(position[0]), position[1]));
    }
    return Collections.unmodifiableList(result);
  }

  /**
   * The magnitude of a deep sky object, or {@link Double#NaN} where the catalogue does not record
   * one.
   *
   * <p>
   * Two traps in the source data, both of which make an object look far brighter than it is:
   *
   * <ul>
   * <li>for a dark nebula the <code>mag</code> field is not a brightness at all but the Lynds
   * opacity class, which runs 1 to 6 - taking it as a magnitude admits every dark nebula in the
   * catalogue to a naked eye chart;
   * <li>an unknown magnitude is written as <code>999</code> rather than being left out.
   * </ul>
   */
  private static double deepSkyMagnitude(JsonNode properties) {
    if ("dn".equals(GeoJson.text(properties, "type"))) {
      return Double.NaN;
    }
    double magnitude = properties.path("mag").asDouble(Double.NaN);
    return magnitude >= 99.0 ? Double.NaN : magnitude;
  }

  // -------------------------------------------------------------- helpers

  /** Turn a GeoJSON longitude in <code>(-180, 180]</code> into a right ascension in degrees. */
  public static double normalizeRightAscensionDegrees(double longitude) {
    double degrees = longitude % 360.0;
    return degrees < 0.0 ? degrees + 360.0 : degrees;
  }

  /** Iterate the fields of an object node, which Jackson only exposes through an iterator. */
  private static Iterable<Map.Entry<String, JsonNode>> iterable(JsonNode object) {
    List<Map.Entry<String, JsonNode>> entries =
        new ArrayList<Map.Entry<String, JsonNode>>();
    object.fields().forEachRemaining(entries::add);
    return entries;
  }
}
