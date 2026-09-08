package org.matheclipse.core.io.paclet;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.io.FileSandbox;

/**
 * Finds the file a Wolfram Language context lives in.
 *
 * <p>
 * <code>Needs["A`B`"]</code> names a context, not a file, and the file is looked for in this order:
 *
 * <ol>
 * <li>a paclet that says where the context is ({@link PacletRegistry}),
 * <li>the directories of a <code>Path -&gt; {…}</code> option,
 * <li><code>$Path</code>,
 * </ol>
 *
 * and inside each directory by the usual conventions - <code>A/B.wl</code>, <code>A/B.m</code>,
 * <code>A/B/init.m</code> and so on. Without this a context reaches <code>Get</code> as a literal
 * file name and nothing can be loaded at all.
 */
public final class PackageResolver {

  private PackageResolver() {}

  /**
   * Contexts the system provides itself, which <code>Needs</code> therefore has nothing to read.
   *
   * <p>
   * In the Wolfram Language these are part of the kernel, and a package that asks for one expects
   * silence rather than a file. What Symja actually defines in them is another matter - a symbol
   * that is missing is missing whether or not the Needs said so.
   */
  private static final java.util.Set<String> STANDARD_CONTEXTS =
      new java.util.HashSet<>(java.util.Arrays.asList(//
          "System`", "Global`", "Internal`", "Developer`", "Experimental`", "Language`", //
          "PacletManager`", "PacletManager`Package`", "Parallel`", "Parallel`Developer`", //
          "GeneralUtilities`", "Documentation`"));

  /** Is this one of the contexts the system provides? */
  public static boolean isStandardContext(String context) {
    return STANDARD_CONTEXTS.contains(context);
  }

  /**
   * The file <code>context</code> lives in, or <code>null</code> when nothing provides it.
   *
   * @param context a context name, ending in a backtick
   * @param extraDirectories directories from a <code>Path</code> option, searched before
   *        <code>$Path</code>; may be empty
   */
  public static Path resolve(String context, List<Path> extraDirectories, EvalEngine engine) {
    Path fromPaclet = PacletRegistry.resolveContext(context);
    if (fromPaclet != null) {
      return fromPaclet;
    }
    List<Path> directories = new ArrayList<Path>();
    if (extraDirectories != null) {
      directories.addAll(extraDirectories);
    }
    directories.addAll(FileSandbox.searchPath(engine));
    directories.add(FileSandbox.workingDirectory(engine));
    for (Path directory : directories) {
      Path file = resolveIn(directory, context, engine);
      if (file != null) {
        return file;
      }
    }
    return null;
  }

  /** The names a context may be written as inside one directory. */
  private static Path resolveIn(Path directory, String context, EvalEngine engine) {
    if (directory == null) {
      return null;
    }
    Path base = directory.isAbsolute() ? directory
        : FileSandbox.workingDirectory(engine).resolve(directory);
    if (!Files.isDirectory(base)) {
      return null;
    }
    String[] segments = context.split("`");
    if (segments.length == 0) {
      return null;
    }
    String nested = String.join("/", segments);
    String last = segments[segments.length - 1];
    String[] candidates = new String[] {//
        nested + ".wl", //
        nested + ".m", //
        nested + "/init.wl", //
        nested + "/init.m", //
        nested + "/Kernel/init.wl", //
        nested + "/Kernel/init.m", //
        last + ".wl", //
        last + ".m"};
    for (String candidate : candidates) {
      Path file = base.resolve(candidate);
      if (Files.isRegularFile(file)) {
        return file;
      }
    }
    return null;
  }
}
