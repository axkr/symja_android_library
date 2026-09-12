package org.matheclipse.core.system;

import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/**
 * A file name the file system cannot spell is an answer, not a stack trace.
 *
 * <p>
 * <code>Path.of</code> throws {@link java.nio.file.InvalidPathException} on a NUL character - and
 * on more than that under Windows - so every built-in that turns a user supplied name into a path
 * either goes through <code>FileSandbox</code> or asks it for the name alone. Before that,
 * <code>ExpandFileName("\000")</code> came out of the engine as an exception.
 */
public class FileNameValidationTest extends ExprEvaluatorTestCase {

  /** A name no file has: <code>Path.of</code> refuses it on every platform. */
  private static final String NUL = String.valueOf((char) 0);

  private final boolean fileSystemEnabled = Config.FILESYSTEM_ENABLED;

  @AfterEach
  public void restoreFileSystemFlag() {
    Config.FILESYSTEM_ENABLED = fileSystemEnabled;
  }

  /** The name is only taken apart here - these three open nothing. */
  @Test
  public void testNameOnlyFunctions() {
    check("ExpandFileName(\"" + NUL + "\")", //
        "ExpandFileName(" + NUL + ")");
    check("DirectoryName(\"a" + NUL + "b/c\")", //
        "DirectoryName(a" + NUL + "b/c)");
    check("ParentDirectory(\"a" + NUL + "b\")", //
        "ParentDirectory(a" + NUL + "b)");
  }

  /** And here the name would be opened, so the sandbox refuses it before anything is. */
  @Test
  public void testFileSystemFunctions() {
    Config.FILESYSTEM_ENABLED = true;
    check("FileExistsQ(\"a" + NUL + "b\")", //
        "False");
    check("DirectoryQ(\"a" + NUL + "b\")", //
        "False");
    check("FileType(\"a" + NUL + "b\")", //
        "None");
    check("Head(PacletDirectoryUnload(\"a" + NUL + "b\"))", //
        "List");
  }

  /** The other branch: with a sandbox root set, the name is refused before it is resolved. */
  @Test
  public void testInsideASandbox(@TempDir Path root) {
    Config.FILESYSTEM_ENABLED = true;
    EvalEngine engine = evaluator.getEvalEngine();
    engine.setFileSandboxRoot(root);
    try {
      check("FileExistsQ(\"a" + NUL + "b\")", //
          "False");
      check("DirectoryQ(\"a" + NUL + "b\")", //
          "False");
    } finally {
      engine.setFileSandboxRoot(null);
    }
  }

  /**
   * A name that is spelled out normally is untouched by any of it. The results themselves are the
   * platform's - a separator and a working directory - so only that there is one is checked here.
   */
  @Test
  public void testValidNames() {
    check("StringQ(ExpandFileName(\"x.txt\"))", //
        "True");
    check("StringQ(DirectoryName(\"/a/b/c.txt\"))", //
        "True");
    check("StringQ(ParentDirectory(\"/a/b\"))", //
        "True");
  }
}
