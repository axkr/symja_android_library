package org.matheclipse.core.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.SymjaDirectories;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Resolves the file names built-in functions get from user input.
 *
 * <p>
 * Every built-in that opens a file the user named goes through here rather than constructing a
 * {@link File} or a {@link Path} itself. Two reasons that has to be one place:
 * <code>Config#isFileSystemEnabled</code> is checked in some twenty five places in
 * <code>org.matheclipse.core.builtin.FileFunctions</code> alone, and the built-ins between them
 * open files at around forty sites - patching each one is not something that survives.
 *
 * <p>
 * <b>Without a sandbox root set on the engine this is a no-op</b>: it hands back
 * <code>new File(fileName)</code>, exactly what the call sites used to do. Both consoles, the JUnit
 * suites and every embedding are unaffected. With a root set - which is what the servlets do, one
 * directory per browser session - a name is resolved against that root and anything that would
 * escape it is refused. That is the difference between a kernel that happens to run on your own
 * machine and one that is answering HTTP requests.
 *
 * <p>
 * Refused means <code>null</code> and a printed message, not an exception. The call sites already
 * have a "could not read that" path - they return {@link F#NIL} or print <code>noopen</code> - and
 * a <code>null</code> check joins it instead of introducing a second failure mode that has to be
 * caught everywhere.
 *
 * <p>
 * This is about paths the <i>user</i> supplies. The engine's own I/O - the startup tables in
 * <code>F</code> and <code>ConstantDefinitions</code>, Kryo serialization, the home directory, the
 * servlets reading their own resources - does not come through here and must not.
 */
public final class FileSandbox {

  private FileSandbox() {}

  /**
   * Resolve a user-supplied file name for reading.
   *
   * @param symbol the built-in to report a refusal against
   * @return the file, or <code>null</code> if a sandbox root is set and <code>fileName</code> lies
   *         outside it
   */
  public static File resolveRead(ISymbol symbol, String fileName, EvalEngine engine) {
    Path path = resolve(symbol, fileName, engine, false);
    return path == null ? null : path.toFile();
  }

  /**
   * Resolve a user-supplied file name for writing. Missing parent directories inside the root are
   * created, so that <code>Export("sub/x.csv", …)</code> works the way it does without a sandbox.
   *
   * @return the file, or <code>null</code> if refused
   */
  public static File resolveWrite(ISymbol symbol, String fileName, EvalEngine engine) {
    Path path = resolve(symbol, fileName, engine, true);
    return path == null ? null : path.toFile();
  }

  /** {@link #resolveRead(ISymbol, String, EvalEngine)} as a {@link Path}. */
  public static Path resolveReadPath(ISymbol symbol, String fileName, EvalEngine engine) {
    return resolve(symbol, fileName, engine, false);
  }

  /** {@link #resolveWrite(ISymbol, String, EvalEngine)} as a {@link Path}. */
  public static Path resolveWritePath(ISymbol symbol, String fileName, EvalEngine engine) {
    return resolve(symbol, fileName, engine, true);
  }

  /**
   * The directories <code>Get</code> and <code>Needs</code> search for a package, and the value of
   * <code>$Path</code>.
   *
   * <p>
   * Without a sandbox this is {@link SymjaDirectories#searchPath()} - the two base directories,
   * every autoloaded application, then the working directory - which is what a local tool such as
   * <code>symjascript</code> wants, and what it has always had.
   *
   * <p>
   * With one it is the session's own directory and nothing else. Two reasons, and they point the
   * same way. The base directories are outside the sandbox, so {@link #resolveRead} refuses
   * everything found in them anyway and searching them is guaranteed-useless work on the host's
   * file system. And <code>$Path</code> is readable from the notebook: naming
   * <code>/Users/someone/Symja</code> to a browser tells the caller where the server keeps its
   * files, which is not theirs to know. The directory is named <code>"."</code> rather than by its
   * real path for the same reason - relative names are what the sandbox resolves, so it is also the
   * name that works.
   *
   * <p>
   * The start-up files are the other half of this and are decided the other way round: a local tool
   * evaluates <code>Kernel/init.m</code> and the <code>Autoload</code> directories, a server never
   * does. See {@link org.matheclipse.core.eval.util.InitFileLoader}, which is opt-in for exactly
   * that reason and which the servlets accordingly do not call.
   */
  public static List<Path> searchPath(EvalEngine engine) {
    if (engine == null || engine.getFileSandboxRoot() == null) {
      return SymjaDirectories.searchPath();
    }
    return Collections.singletonList(Path.of("."));
  }

  /**
   * Whether <code>fileName</code> names an absolute location, which a sandbox refuses and a
   * <code>$Path</code> search has nothing to search for.
   *
   * <p>
   * Here rather than at the call site so that the built-ins do not have to build a {@link Path}
   * themselves just to ask a question about a string - which is what
   * <code>FileSandboxDependencyTest</code> stops them doing, because a path built is usually a path
   * about to be opened.
   */
  public static boolean isAbsolutePath(String fileName) {
    try {
      Path path = Path.of(fileName);
      return path.isAbsolute() || path.getRoot() != null;
    } catch (InvalidPathException ex) {
      return false;
    }
  }

  /**
   * The directory a bare, relative name is resolved against - the sandbox root when there is one,
   * and the process working directory otherwise. <code>FileNames()</code> lists it.
   */
  public static Path workingDirectory(EvalEngine engine) {
    Path root = engine == null ? null : engine.getFileSandboxRoot();
    return root != null ? root : Path.of("").toAbsolutePath();
  }

  private static Path resolve(ISymbol symbol, String fileName, EvalEngine engine,
      boolean forWriting) {
    Path root = engine == null ? null : engine.getFileSandboxRoot();
    if (root == null) {
      // no sandbox: what the call sites did before this class existed
      return Path.of(fileName);
    }
    try {
      if (fileName.startsWith("~")) {
        // the home directory is not this session's to reach, and Path.of does not expand it anyway
        return refuse(symbol, fileName, engine);
      }
      Path candidate = Path.of(fileName);
      if (candidate.isAbsolute() || candidate.getRoot() != null) {
        return refuse(symbol, fileName, engine);
      }
      Path resolved = root.resolve(candidate).normalize();
      if (!resolved.startsWith(root)) {
        // a "../.." that climbs out
        return refuse(symbol, fileName, engine);
      }
      if (Files.exists(resolved)) {
        // a symbolic link inside the root may still point outside it
        Path real = resolved.toRealPath();
        if (!real.startsWith(root.toRealPath())) {
          return refuse(symbol, fileName, engine);
        }
      } else if (forWriting) {
        Path parent = resolved.getParent();
        if (parent != null && !Files.exists(parent)) {
          Files.createDirectories(parent);
        }
      }
      return resolved;
    } catch (InvalidPathException | IOException | SecurityException ex) {
      return refuse(symbol, fileName, engine);
    }
  }

  private static Path refuse(ISymbol symbol, String fileName, EvalEngine engine) {
    // Cannot open `1`: the path is outside this session's directory.
    Errors.printMessage(symbol == null ? S.General : symbol, "fsandbox",
        F.list(F.stringx(fileName)), engine);
    return null;
  }
}
