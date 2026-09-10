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
      check("PacletDirectoryLoad(\"" + root + "\") // Length", //
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
      check("PacletDirectoryLoad(\"" + root + "\") // Length", //
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
      check("PacletDirectoryLoad(\"" + root + "\") // Length", //
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
      check("PacletDirectoryLoad(\"" + root + "\") // Length", //
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
      check("Get(\"answer.wl\", Path -> {\"" + directory + "\"})", //
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
      check("PacletDirectoryLoad(\"" + root + "\") // Length", //
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
      check("SetDirectory(\"" + root + "\")", //
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
}
