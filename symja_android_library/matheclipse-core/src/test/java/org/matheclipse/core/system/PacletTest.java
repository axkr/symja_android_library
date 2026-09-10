package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.io.paclet.PacletRegistry;

/**
 * Paclets, and the <code>Needs</code> that finds a package through them.
 *
 * <p>
 * <code>Needs["A`"]</code> names a context and not a file. Without a paclet saying where that
 * context lives, the name reached <code>Get</code> as if it were a file name and nothing could be
 * loaded at all - which is what an application built out of packages does on its first line.
 */
public class PacletTest extends ExprEvaluatorTestCase {

  @AfterEach
  public void forgetPaclets() {
    // the registry is process-global, so a test must not leave its paclets to the next one
    PacletRegistry.clear();
  }

  /**
   * A file name as Wolfram Language source.
   *
   * <p>
   * A backslash begins an escape inside a string literal, so a Windows temporary directory written
   * out as it stands reaches the parser as <code>C:sershartppDataocalempunit-…</code> - the
   * <code>\U</code>, <code>\k</code>, <code>\A</code>, <code>\L</code>, <code>\T</code> and
   * <code>\j</code> each eaten as an escape. Doubling them is what makes the test say the name it
   * means, on the one platform where the name contains any.
   */
  private static String wl(Path path) {
    return path.toString().replace("\\", "\\\\");
  }

  /** A paclet directory holding one package, described in the current spelling. */
  private static Path modernPaclet(Path root) throws IOException {
    Path paclet = root.resolve("Widgets");
    Files.createDirectories(paclet.resolve("Kernel"));
    Files.write(paclet.resolve("PacletInfo.wl"), ("PacletObject[<|\n" //
        + "  \"Name\" -> \"Acme/Widgets\",\n" //
        + "  \"Version\" -> \"1.2.3\",\n" //
        + "  \"Extensions\" -> {\n" //
        + "    {\"Kernel\", \"Root\" -> \"Kernel\", \"Context\" -> {\n" //
        + "      {\"Acme`Widgets`\", \"Widgets.wl\"}\n" //
        + "    }}\n" //
        + "  }\n" //
        + "|>]\n").getBytes(StandardCharsets.UTF_8));
    Files.write(paclet.resolve("Kernel").resolve("Widgets.wl"), ("BeginPackage[\"Acme`Widgets`\"]\n" //
        + "widgetCount::usage = \"how many\"\n" //
        + "Begin[\"`Private`\"]\n" //
        + "widgetCount[] := 42\n" //
        + "End[]\n" //
        + "EndPackage[]\n").getBytes(StandardCharsets.UTF_8));
    return paclet;
  }

  /** The same, written the way paclets were described before version 12. */
  private static Path legacyPaclet(Path root) throws IOException {
    Path paclet = root.resolve("Gadgets");
    Files.createDirectories(paclet);
    Files.write(paclet.resolve("PacletInfo.m"), ("Paclet[\n" //
        + "  Name -> \"Gadgets\",\n" //
        + "  Version -> \"0.0.1\",\n" //
        + "  Extensions -> {\n" //
        + "    {\"Kernel\", Context -> {\"Gadgets`\"}}\n" //
        + "  }\n" //
        + "]\n").getBytes(StandardCharsets.UTF_8));
    Files.write(paclet.resolve("Gadgets.wl"), ("BeginPackage[\"Gadgets`\"]\n" //
        + "gadgetCount::usage = \"how many\"\n" //
        + "Begin[\"`Private`\"]\n" //
        + "gadgetCount[] := 7\n" //
        + "End[]\n" //
        + "EndPackage[]\n").getBytes(StandardCharsets.UTF_8));
    return paclet;
  }

  @Test
  public void testNeedsFindsAContextThroughAPaclet(@TempDir Path root) throws IOException {
    modernPaclet(root);
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("PacletDirectoryLoad(\"" + wl(root) + "\") // Length", //
          "1");
      check("Needs(\"Acme`Widgets`\")", //
          "");
      check("Acme`Widgets`widgetCount()", //
          "42");
      // and the paclet can be found by name
      check("StringQ(PacletFind(\"Acme/Widgets\")[[1]][\"Location\"])", //
          "True");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testALegacyPacletInfoIsRead(@TempDir Path root) throws IOException {
    // a paclet from before version 12 writes its keys as symbols, and such paclets are still
    // shipped - the WLJS Notebook's LetWL is one
    legacyPaclet(root);
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("PacletDirectoryLoad(\"" + wl(root) + "\") // Length", //
          "1");
      check("Needs(\"Gadgets`\")", //
          "");
      check("Gadgets`gadgetCount()", //
          "7");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testNeedsCanGiveTheContextAShortName(@TempDir Path root) throws IOException {
    modernPaclet(root);
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("PacletDirectoryLoad(\"" + wl(root) + "\") // Length", //
          "1");
      check("Needs(\"Acme`Widgets`\" -> \"w`\")", //
          "");
      // w` is the same context under another name
      check("w`widgetCount()", //
          "42");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testAnAliasedContextStaysOffTheContextPath(@TempDir Path root) throws IOException {
    modernPaclet(root);
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("PacletDirectoryLoad(\"" + wl(root) + "\") // Length", //
          "1");
      check("Needs(\"Acme`Widgets`\" -> \"w`\")", //
          "");
      // the alias reaches the package, and only the alias does: a bare name written after an
      // aliased Needs still belongs to the reading context, so a package that reads another one
      // this way keeps writing its own definitions
      check("MemberQ($ContextPath, \"Acme`Widgets`\")", //
          "False");
      check("Context(widgetCount)", //
          "Global`");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testNeedsSaysWhenNothingProvidesTheContext() {
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("Needs(\"No`Such`Package`\")", //
          "$Failed");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testGetSearchesTheDirectoriesOfItsPathOption(@TempDir Path root) throws IOException {
    Path directory = root.resolve("lib");
    Files.createDirectories(directory);
    Files.write(directory.resolve("answer.wl"),
        "theAnswer[] := 42\n".getBytes(StandardCharsets.UTF_8));
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("Get(\"answer.wl\", Path -> {\"" + wl(directory) + "\"})", //
          "");
      check("theAnswer()", //
          "42");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testPacletDirectoriesCanBeUnloaded(@TempDir Path root) throws IOException {
    modernPaclet(root);
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("PacletDirectoryLoad(\"" + wl(root) + "\") // Length", //
          "1");
      // this is how an application starts from nothing
      check("PacletDirectoryUnload /@ PacletDirectoryLoad() // Last // Length", //
          "0");
      assertTrue(PacletRegistry.directories().isEmpty());
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  @Test
  public void testDirectoryFollowsSetDirectory(@TempDir Path root) throws IOException {
    Files.write(root.resolve("here.txt"), "x".getBytes(StandardCharsets.UTF_8));
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      // a Java process cannot change its own working directory, so a relative name has to be
      // resolved against Directory[] for SetDirectory to mean anything
      check("SetDirectory(\"" + wl(root) + "\")", //
          root.toString());
      check("FileExistsQ(\"here.txt\")", //
          "True");
      assertEquals(root.toString(), String.valueOf(evaluator.eval("Directory()")));
      check("ResetDirectory() // StringQ", //
          "True");
      check("FileExistsQ(\"here.txt\")", //
          "False");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }

  /**
   * A directory added to <code>$Path</code> is searched by <code>Needs</code>. The WLJS kernel adds
   * its shared directory with <code>AppendTo[$Path, dir]</code> at every launch.
   */
  @Test
  public void testAppendingToPathFindsAPackage(@TempDir Path root) throws IOException {
    Path lib = root.resolve("lib");
    Files.createDirectories(lib);
    Files.write(lib.resolve("Probes.wl"), ("BeginPackage[\"Probes`\"]\n" //
        + "probeCount::usage = \"how many\"\n" //
        + "Begin[\"`Private`\"]\n" //
        + "probeCount[] := 5\n" //
        + "End[]\n" //
        + "EndPackage[]\n").getBytes(StandardCharsets.UTF_8));
    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("AppendTo($Path, \"" + wl(lib) + "\"); Last($Path) === \"" + wl(lib) + "\"", //
          "True");
      check("Needs(\"Probes`\")", //
          "");
      check("Probes`probeCount()", //
          "5");
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
      org.matheclipse.core.eval.EvalEngine.get()
          .removeDollarValue(org.matheclipse.core.expression.S.$Path);
    }
  }

}
