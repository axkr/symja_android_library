package org.matheclipse.core.io.paclet;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;

/**
 * The directories <code>PacletDirectoryLoad</code> was given, and the paclets found in them.
 *
 * <p>
 * This is what makes <code>Needs["A`"]</code> able to find a file: a paclet says which context lives
 * where, so the context name does not have to be guessed at from the file system. Load order
 * matters and is kept - a directory loaded later provides a context in preference to an earlier one,
 * which is how an application overrides a bundled paclet.
 *
 * <p>
 * Process-global, like the loaded-package list it works with: paclets are loaded once by a script
 * and then used by whatever evaluates afterwards.
 */
public final class PacletRegistry {

  private static final Map<Path, List<PacletInfo>> DIRECTORIES =
      new LinkedHashMap<Path, List<PacletInfo>>();

  private PacletRegistry() {}

  /**
   * Read the paclets in <code>directory</code> and its immediate subdirectories, and remember them.
   *
   * @return the directory, or <code>null</code> if it is not one
   */
  public static synchronized Path load(Path directory, EvalEngine engine) {
    if (directory == null || !Files.isDirectory(directory)) {
      return null;
    }
    Path key = directory.toAbsolutePath().normalize();
    List<PacletInfo> paclets = new ArrayList<PacletInfo>();
    for (Path pacletInfoFile : PacletInfoParser.findIn(key)) {
      PacletInfo paclet = PacletInfoParser.parse(pacletInfoFile, engine);
      if (paclet != null) {
        paclets.add(paclet);
      }
    }
    // re-loading a directory moves it to the end, so that it wins over the ones loaded before
    DIRECTORIES.remove(key);
    DIRECTORIES.put(key, paclets);
    return key;
  }

  /** Forget a directory. Nothing already read from it is unloaded - as in the Wolfram Language. */
  public static synchronized boolean unload(Path directory) {
    if (directory == null) {
      return false;
    }
    return DIRECTORIES.remove(directory.toAbsolutePath().normalize()) != null;
  }

  /** The loaded directories, in the order they were loaded. */
  public static synchronized List<Path> directories() {
    return new ArrayList<Path>(DIRECTORIES.keySet());
  }

  /** Every paclet found, the most recently loaded directory first. */
  public static synchronized List<PacletInfo> paclets() {
    List<PacletInfo> all = new ArrayList<PacletInfo>();
    List<List<PacletInfo>> byDirectory = new ArrayList<List<PacletInfo>>(DIRECTORIES.values());
    for (int i = byDirectory.size() - 1; i >= 0; i--) {
      all.addAll(byDirectory.get(i));
    }
    return all;
  }

  /** The file a loaded paclet provides <code>context</code> in, or <code>null</code>. */
  public static synchronized Path resolveContext(String context) {
    for (PacletInfo paclet : paclets()) {
      Path file = paclet.fileFor(context);
      if (file != null && Files.isRegularFile(file)) {
        return file;
      }
    }
    return null;
  }

  /** The paclets whose name matches, for <code>PacletFind</code>. */
  public static synchronized List<PacletInfo> find(String name) {
    List<PacletInfo> found = new ArrayList<PacletInfo>();
    for (PacletInfo paclet : paclets()) {
      if (name == null || name.equals("*") || name.equals(paclet.name())) {
        found.add(paclet);
      }
    }
    return found;
  }

  /** Forget everything. For tests, which must not see the paclets another test loaded. */
  public static synchronized void clear() {
    DIRECTORIES.clear();
  }
}
