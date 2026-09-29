package org.matheclipse.core.system;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/**
 * A kernel with the file system disabled, or confined to a sandbox directory, says nothing about the
 * host it runs on: no environment variables, user or machine name, process id, host directories or
 * memory of the process. <code>Environment("DB_PASSWORD")</code> would otherwise read a server's
 * secrets.
 */
public class SandboxHostInfoTest extends ExprEvaluatorTestCase {

  /** The host facts, each tested for the type of value it has when it is visible. */
  private static final String HOST_FACTS = "{StringQ($HomeDirectory), StringQ($UserName), " //
      + "StringQ($MachineName), IntegerQ($ProcessID), StringQ($TemporaryDirectory), " //
      + "IntegerQ($SystemMemory), StringQ($UserDocumentsDirectory), " //
      + "StringQ($InstallationDirectory), StringQ($BaseDirectory), StringQ($UserBaseDirectory), " //
      + "IntegerQ(MemoryInUse()), IntegerQ(MaxMemoryUsed()), IntegerQ(MemoryAvailable()), " //
      + "MatchQ(Environment(\"PATH\"), _String|$Failed)}";

  private static final String NONE_VISIBLE =
      "{False,False,False,False,False,False,False,False,False,False,False,False,False,False}";

  private static final String ALL_VISIBLE =
      "{True,True,True,True,True,True,True,True,True,True,True,True,True,True}";

  private final boolean fileSystemEnabled = Config.FILESYSTEM_ENABLED;

  @AfterEach
  public void restoreFileSystemFlag() {
    Config.FILESYSTEM_ENABLED = fileSystemEnabled;
    evaluator.getEvalEngine().setFileSandboxRoot(null);
  }

  @Test
  public void testFileSystemDisabled() {
    Config.FILESYSTEM_ENABLED = false;
    check(HOST_FACTS, //
        NONE_VISIBLE);
    check("{$Path, Head(Directory())}", //
        "{{},Directory}");
  }

  @Test
  public void testFileSystemEnabled() {
    Config.FILESYSTEM_ENABLED = true;
    check(HOST_FACTS, //
        ALL_VISIBLE);
    check("StringQ(Directory())", //
        "True");
  }

  @Test
  public void testInsideASandbox(@TempDir Path root) throws IOException {
    Config.FILESYSTEM_ENABLED = true;
    EvalEngine engine = evaluator.getEvalEngine();
    engine.setFileSandboxRoot(root);
    Files.createDirectories(root.resolve("sub"));
    check(HOST_FACTS, //
        NONE_VISIBLE);
    // the session's own directory, not where the host keeps it
    check("{Directory(), SetDirectory(), $Path}", //
        "{.,.,{.}}");
  }

  @Test
  public void testSessionTimeAndTimeUsed(@TempDir Path root) {
    // WMA: Real, positive, Protected; $TimeUnit is 1/100
    Config.FILESYSTEM_ENABLED = true;
    check("{Head(SessionTime()), Head(TimeUsed()), SessionTime() > 0, TimeUsed() > 0, $TimeUnit}", //
        "{Real,Real,True,True,1/100}");
    check("MemberQ(Attributes(SessionTime), Protected)", //
        "True");
    check("SessionTime(1)", //
        "SessionTime(1)");

    // without JMX (Android) TimeUsed falls back on the wall clock
    boolean disableJMX = Config.DISABLE_JMX;
    Config.DISABLE_JMX = true;
    try {
      check("{Head(TimeUsed()), TimeUsed() > 0}", //
          "{Real,True}");
    } finally {
      Config.DISABLE_JMX = disableJMX;
    }

    // a sandboxed session counts from its own engine, and TimeUsed is its own wall clock -
    // never the uptime or the CPU load of the whole server
    Config.FILESYSTEM_ENABLED = false;
    check("{Head(SessionTime()), Abs(TimeUsed() - SessionTime()) < 1}", //
        "{Real,True}");
    Config.FILESYSTEM_ENABLED = true;
    evaluator.getEvalEngine().setFileSandboxRoot(root);
    check("{Head(SessionTime()), Abs(TimeUsed() - SessionTime()) < 1}", //
        "{Real,True}");
  }

  @Test
  public void testFileSize(@TempDir Path root) throws IOException {
    Path file = root.resolve("hello.txt");
    Files.write(file, "hello".getBytes(StandardCharsets.UTF_8));
    String name = file.toString().replace("\\", "\\\\");

    // the file system is disabled: unevaluated
    Config.FILESYSTEM_ENABLED = false;
    check("FileSize(\"" + name + "\")", //
        "FileSize(" + file + ")");

    Config.FILESYSTEM_ENABLED = true;
    check("{QuantityMagnitude(FileSize(\"" + name + "\")), QuantityUnit(FileSize(\"" + name
        + "\")), QuantityMagnitude(FileSize(File(\"" + name + "\")))}", //
        "{5.0,Bytes,5.0}");
    // WMA: FileSize::fdnfnd and FileSize::badfile, the call stays unevaluated
    check("Head(FileSize(\"" + name + ".missing\"))", //
        "FileSize");
    check("FileSize(1)", //
        "FileSize(1)");

    // in a sandbox a name outside it is refused for where it is: an existing host file and a
    // missing one get the same answer
    EvalEngine engine = evaluator.getEvalEngine();
    engine.setFileSandboxRoot(root);
    check("QuantityMagnitude(FileSize(\"hello.txt\"))", //
        "5.0");
    check("{Head(FileSize(\"" + name + "\")), Head(FileSize(\"/no/such/file\"))}", //
        "{FileSize,FileSize}");
  }
}
