package org.matheclipse.core.eval.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;

/**
 * Evaluates the start-up files of an interactive session:
 *
 * <ol>
 * <li><code>$BaseDirectory/Kernel/init.m</code> - installation wide</li>
 * <li><code>$UserBaseDirectory/Kernel/init.m</code> - this user, so it can override the above</li>
 * <li>for every application under an <code>Autoload</code> directory, its <code>init.m</code> and
 * <code>Kernel/init.m</code></li>
 * </ol>
 *
 * <p>
 * <b>This is opt-in.</b> Reading files from a user's home directory at start-up is right for a
 * command-line tool and wrong for an embedder - a server evaluating expressions on behalf of other
 * people would be executing whatever is in <code>~/.Symja</code>. Nothing calls this unless it asks
 * to.
 *
 * <p>
 * A file that fails is reported and skipped; the remaining files still run. A broken
 * <code>init.m</code> should not make the tool unusable, but it must not pass unnoticed either.
 */
public final class InitFileLoader {

  private InitFileLoader() {}

  /**
   * Evaluate the start-up files.
   *
   * @param engine the engine to evaluate in
   * @param onError called with the file and the problem for every file that fails; may be
   *        <code>null</code> to ignore failures
   * @return the number of files that failed
   */
  public static int loadStartupFiles(EvalEngine engine, BiConsumer<Path, Throwable> onError) {
    List<Path> files = new ArrayList<Path>(SymjaDirectories.kernelInitFiles());
    files.addAll(SymjaDirectories.autoloadInitFiles());
    return load(files, engine, onError);
  }

  /**
   * Evaluate specific files, for a <code>-initfile</code> option. Same error policy as
   * {@link #loadStartupFiles}.
   */
  public static int load(List<Path> files, EvalEngine engine, BiConsumer<Path, Throwable> onError) {
    int failures = 0;
    for (Path file : files) {
      if (!evaluate(file, engine, onError)) {
        failures++;
      }
    }
    return failures;
  }

  private static boolean evaluate(Path file, EvalEngine engine,
      BiConsumer<Path, Throwable> onError) {
    String source;
    try {
      source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    } catch (IOException ioe) {
      report(onError, file, ioe);
      return false;
    }
    boolean packageMode = engine.isPackageMode();
    try {
      // Package mode: an init file defines things, it does not print a result per line.
      engine.setPackageMode(true);
      PackageUtil.evaluatePackage(PackageUtil.parseReader(source, engine), engine);
      return true;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      report(onError, file, rex);
      return false;
    } finally {
      engine.setPackageMode(packageMode);
    }
  }

  private static void report(BiConsumer<Path, Throwable> onError, Path file, Throwable problem) {
    if (onError != null) {
      onError.accept(file, problem);
    }
  }
}
