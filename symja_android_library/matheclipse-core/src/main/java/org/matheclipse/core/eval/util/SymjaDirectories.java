package org.matheclipse.core.eval.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * The directories Symja looks in for start-up files and packages.
 *
 * <p>
 * <b>{@code $BaseDirectory}</b> holds files meant for every user of an installation,
 * <b>{@code $UserBaseDirectory}</b> those belonging to one user. They are deliberately different
 * places: the user directory is read after the base directory, so it can override it. Each may
 * contain a {@code Kernel/init.m} evaluated at start-up, and an {@code Autoload} directory whose
 * sub-directories are packages loaded automatically.
 *
 * <p>
 * Both can be pointed elsewhere with the environment variables {@code SYMJA_BASE_DIRECTORY} and
 * {@code SYMJA_USER_BASE_DIRECTORY}, which is what a test or a sandboxed deployment wants.
 */
public final class SymjaDirectories {

  public static final String BASE_DIRECTORY_ENV = "SYMJA_BASE_DIRECTORY";
  public static final String USER_BASE_DIRECTORY_ENV = "SYMJA_USER_BASE_DIRECTORY";

  /** Sub-directory of a base directory holding {@code init.m}. */
  public static final String KERNEL = "Kernel";

  /** Sub-directory of a base directory whose entries are loaded at start-up. */
  public static final String AUTOLOAD = "Autoload";

  /** The start-up file. */
  public static final String INIT_FILE = "init.m";

  private SymjaDirectories() {}

  /** Installation-wide directory, or <code>null</code> when there is no home directory. */
  public static Path baseDirectory() {
    Path override = fromEnvironment(BASE_DIRECTORY_ENV);
    if (override != null) {
      return override;
    }
    String userHome = System.getProperty("user.home");
    return userHome == null ? null : Paths.get(userHome, "Symja");
  }

  /**
   * Per-user directory, in the place the platform expects it: {@code ~/Library/Symja} on macOS,
   * {@code %APPDATA%\Symja} on Windows, {@code ~/.Symja} elsewhere.
   */
  public static Path userBaseDirectory() {
    Path override = fromEnvironment(USER_BASE_DIRECTORY_ENV);
    if (override != null) {
      return override;
    }
    String userHome = System.getProperty("user.home");
    if (userHome == null) {
      return null;
    }
    String os = System.getProperty("os.name", "").toLowerCase(Locale.US);
    if (os.contains("mac") || os.contains("darwin")) {
      return Paths.get(userHome, "Library", "Symja");
    }
    if (os.contains("win")) {
      String appData = System.getenv("APPDATA");
      if (appData != null && !appData.isEmpty()) {
        return Paths.get(appData, "Symja");
      }
      return Paths.get(userHome, "AppData", "Roaming", "Symja");
    }
    return Paths.get(userHome, ".Symja");
  }

  /** {@code <base>/Kernel/init.m} for both base directories, base first, existing files only. */
  public static List<Path> kernelInitFiles() {
    List<Path> files = new ArrayList<Path>();
    for (Path base : baseDirectories()) {
      Path init = base.resolve(KERNEL).resolve(INIT_FILE);
      if (Files.isRegularFile(init)) {
        files.add(init);
      }
    }
    return files;
  }

  /**
   * The application directories under {@code <base>/Autoload}, base directory first and sorted by
   * name inside each, so start-up order is the same on every run.
   */
  public static List<Path> autoloadDirectories() {
    List<Path> directories = new ArrayList<Path>();
    for (Path base : baseDirectories()) {
      Path autoload = base.resolve(AUTOLOAD);
      if (!Files.isDirectory(autoload)) {
        continue;
      }
      try (Stream<Path> entries = Files.list(autoload)) {
        entries.filter(Files::isDirectory)
            .sorted(Comparator.comparing(p -> p.getFileName().toString()))
            .forEach(directories::add);
      } catch (IOException ioe) {
        // an unreadable Autoload directory simply contributes nothing
      }
    }
    return directories;
  }

  /**
   * The start-up files of the autoloaded applications: both {@code <app>/init.m} and
   * {@code <app>/Kernel/init.m} are recognised, as in the Wolfram Language.
   */
  public static List<Path> autoloadInitFiles() {
    List<Path> files = new ArrayList<Path>();
    for (Path application : autoloadDirectories()) {
      Path direct = application.resolve(INIT_FILE);
      if (Files.isRegularFile(direct)) {
        files.add(direct);
      }
      Path inKernel = application.resolve(KERNEL).resolve(INIT_FILE);
      if (Files.isRegularFile(inKernel)) {
        files.add(inKernel);
      }
    }
    return files;
  }

  /**
   * Directories searched for packages, in order: the two base directories, then every autoloaded
   * application, then the working directory. This is the value of <code>$Path</code>.
   */
  public static List<Path> searchPath() {
    List<Path> path = new ArrayList<Path>(baseDirectories());
    path.addAll(autoloadDirectories());
    path.add(Paths.get("."));
    return path;
  }

  /** The two base directories that exist, installation-wide first. */
  private static List<Path> baseDirectories() {
    List<Path> bases = new ArrayList<Path>(2);
    Path base = baseDirectory();
    Path userBase = userBaseDirectory();
    if (base != null) {
      bases.add(base);
    }
    // Only distinct directories: if both resolve to the same place, do not read it twice.
    if (userBase != null && (base == null || !userBase.equals(base))) {
      bases.add(userBase);
    }
    return bases;
  }

  private static Path fromEnvironment(String name) {
    String value = System.getenv(name);
    return value == null || value.trim().isEmpty() ? null : Paths.get(value.trim());
  }
}
