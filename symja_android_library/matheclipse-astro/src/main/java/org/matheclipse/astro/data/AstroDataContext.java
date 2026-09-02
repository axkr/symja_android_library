package org.matheclipse.astro.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.ISymbol;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvider;
import org.orekit.data.ClasspathCrawler;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.data.ZipJarCrawler;
import org.orekit.time.TimeScalesFactory;

/**
 * Locates the external <code>orekit-data</code> files and feeds them to Orekit's global
 * {@link DataContext}.
 *
 * <p>
 * A subset of these files is shipped in this module's resources; the full set is available from
 * <a href="https://gitlab.orekit.org/orekit/orekit-data">the Orekit data repository</a>. Without
 * them Orekit cannot build the UTC time scale, so essentially every astronomy function is
 * unavailable. The search order is
 *
 * <ol>
 * <li>the <code>orekit.data.path</code> system property ({@link DataProvidersManager#OREKIT_DATA_PATH}),
 * which may name several directories or ZIP/JAR archives separated by the platform path separator;
 * <li>the <code>OREKIT_DATA_PATH</code> environment variable, with the same syntax;
 * <li><code>$HOME/orekit-data</code>;
 * <li><code>$HOME/orekit-data.zip</code>;
 * <li>the {@link #BUNDLED_RESOURCES} shipped in this module.
 * </ol>

 * <p>
 * An external data set is registered before the bundled one, so that a user can widen the date
 * range or refresh the Earth orientation parameters without rebuilding Symja.
 *
 * <p>
 * A missing data set is <em>not</em> an error at initialization time - {@code matheclipse-astro}
 * has to stay loadable so that the rest of the engine keeps working. Every astronomy evaluator
 * instead calls {@link #checkAvailable(ISymbol, EvalEngine)} first and returns the
 * <code>orekitdata</code> message when the files were not found.
 */
public class AstroDataContext {

  /** Environment variable read when the {@code orekit.data.path} property is not set. */
  public static final String OREKIT_DATA_ENV = "OREKIT_DATA_PATH";

  /** Directory name looked up in the user home directory as a last resort. */
  public static final String DEFAULT_DIRECTORY_NAME = "orekit-data";

  /**
   * The subset of the official <code>orekit-data</code> bundle which is shipped in this module's
   * resources, used when no external data set is configured. Only the files which positional
   * astronomy needs are included; the gravity field, ocean tide, space weather and solar activity
   * files of the bundle are for orbit propagation and are left out.
   *
   * <p>
   * {@link ClasspathCrawler} matches a loader's file name pattern against the last path segment
   * only, so the directory layout below is kept purely to mirror the upstream bundle and make
   * refreshing the files obvious.
   */
  public static final String[] BUNDLED_RESOURCES = { //
      // leap seconds, 1972 to end of 2026 - without this there is no UTC time scale
      DEFAULT_DIRECTORY_NAME + "/tai-utc.dat", //
      // maps the IERS files to ITRF versions
      DEFAULT_DIRECTORY_NAME + "/itrf-versions.conf", //
      // IERS Earth orientation parameters, 1973 to late 2026, CIO based (IAU-2000). The
      // equinox based IAU-1980 file of the bundle is not needed as long as the frames are
      // built with IERSConventions.IERS_2010.
      DEFAULT_DIRECTORY_NAME + "/Earth-Orientation-Parameters/IAU-2000/finals2000A.all", //
      // JPL DE 440 planetary and lunar ephemerides, 1990 to 2149
      DEFAULT_DIRECTORY_NAME + "/DE-440-ephemerides/lnxp1990.440" //
  };

  private static final Object LOCK = new Object();

  private static boolean initialized = false;

  /** {@code true} if a data set was found <em>and</em> Orekit could read a time scale from it. */
  private static boolean available = false;

  /** The locations which were searched, used as the argument of the {@code orekitdata} message. */
  private static String searchedLocations = "";

  private AstroDataContext() {}

  /**
   * Register the external Orekit data with the default {@link DataContext}. Repeated calls are
   * ignored, so this is safe to call from every servlet session. This method never throws; use
   * {@link #isAvailable()} to find out whether the data was actually found.
   */
  public static void initialize() {
    synchronized (LOCK) {
      if (initialized) {
        return;
      }
      initialized = true;

      List<String> searched = new ArrayList<String>();
      List<File> roots = resolveDataRoots(searched);
      searchedLocations = String.join(", ", searched);

      DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
      for (File root : roots) {
        if (root.isDirectory()) {
          manager.addProvider(new DirectoryCrawler(root));
        } else {
          manager.addProvider(new ZipJarCrawler(root));
        }
      }
      // An externally configured data set wins, because it is the only way for a user to widen
      // the date range or to refresh the Earth orientation parameters. The bundled subset is
      // registered afterwards so that it can still fill in what the external set does not cover.
      addBundledResources(manager, searched);

      // Reading a time scale is the cheapest way to find out whether the data set is usable:
      // UTC needs the leap second file, which is the one thing no astronomy function can do
      // without.
      try {
        TimeScalesFactory.getUTC();
        available = true;
      } catch (RuntimeException rex) {
        available = false;
      }
    }
  }

  /**
   * Register the {@link #BUNDLED_RESOURCES} which are actually present on the classpath.
   *
   * <p>
   * {@link ClasspathCrawler} rejects its whole argument list when one entry cannot be resolved, so
   * the resources are probed individually first. That keeps a stripped down repackaging of this
   * module working instead of failing outright.
   */
  private static void addBundledResources(DataProvidersManager manager, List<String> searched) {
    ClassLoader classLoader = AstroDataContext.class.getClassLoader();
    List<String> present = new ArrayList<String>();
    for (String resource : BUNDLED_RESOURCES) {
      if (classLoader.getResource(resource) != null) {
        present.add(resource);
      }
    }
    if (present.isEmpty()) {
      searched.add("classpath:" + DEFAULT_DIRECTORY_NAME);
      return;
    }
    manager.addProvider(new ClasspathCrawler(classLoader, present.toArray(new String[0])));
  }

  /**
   * Build the list of data roots to register, in the order described in the class comment. Records
   * every location it looked at in {@code searched}, so that a failure can tell the user where the
   * files were expected.
   *
   * @param searched collects the human readable locations which were tried
   * @return the readable directories and ZIP/JAR archives which were found, possibly empty
   */
  private static List<File> resolveDataRoots(List<String> searched) {
    List<File> roots = new ArrayList<File>();

    String property = System.getProperty(DataProvidersManager.OREKIT_DATA_PATH);
    if (addPathList(property, roots, searched)) {
      return roots;
    }

    String environment = System.getenv(OREKIT_DATA_ENV);
    if (addPathList(environment, roots, searched)) {
      return roots;
    }

    String home = System.getProperty("user.home");
    if (home != null) {
      File directory = new File(home, DEFAULT_DIRECTORY_NAME);
      searched.add(directory.getPath());
      if (directory.isDirectory()) {
        roots.add(directory);
        return roots;
      }
      File archive = new File(home, DEFAULT_DIRECTORY_NAME + ".zip");
      searched.add(archive.getPath());
      if (archive.isFile()) {
        roots.add(archive);
        return roots;
      }
    }
    return roots;
  }

  /**
   * Split a path-separator delimited list and collect the entries which exist.
   *
   * @return {@code true} if at least one entry of {@code pathList} was usable
   */
  private static boolean addPathList(String pathList, List<File> roots, List<String> searched) {
    if (pathList == null || pathList.isEmpty()) {
      return false;
    }
    for (String name : pathList.split(File.pathSeparator)) {
      if (name.isEmpty()) {
        continue;
      }
      File file = new File(name);
      searched.add(file.getPath());
      if (file.isDirectory() || (file.isFile() //
          && DataProvider.ZIP_ARCHIVE_PATTERN.matcher(name).matches())) {
        roots.add(file);
      }
    }
    return !roots.isEmpty();
  }

  /**
   * @return {@code true} if the external Orekit data files were found and could be read
   */
  public static boolean isAvailable() {
    initialize();
    return available;
  }

  /**
   * Guard for every evaluator which needs the external Orekit data. Prints the
   * <code>orekitdata</code> message when the data is missing, so the caller only has to return
   * {@link F#NIL} and leave the expression unevaluated.
   *
   * @return <code>true</code> if the data is available and the caller can continue
   */
  public static boolean checkAvailable(ISymbol symbol, EvalEngine engine) {
    if (isAvailable()) {
      return true;
    }
    // The external Orekit data files are not available: `1`
    Errors.printMessage(symbol, "orekitdata", F.List(F.stringx(describeMissingData())), engine);
    return false;
  }

  /**
   * @return a description of where the data was looked for and how to install it, used as the
   *         argument of the {@code orekitdata} message
   */
  public static String describeMissingData() {
    StringBuilder buf = new StringBuilder();
    buf.append("download orekit-data from ");
    buf.append("https://gitlab.orekit.org/orekit/orekit-data/-/archive/main/orekit-data-main.zip");
    buf.append(", unpack it to ");
    buf.append(new File(System.getProperty("user.home", "~"), DEFAULT_DIRECTORY_NAME).getPath());
    buf.append(" or set the system property ");
    buf.append(DataProvidersManager.OREKIT_DATA_PATH);
    buf.append(" (searched: ");
    buf.append(searchedLocations.isEmpty() ? "nothing" : searchedLocations);
    buf.append(")");
    return buf.toString();
  }
}
