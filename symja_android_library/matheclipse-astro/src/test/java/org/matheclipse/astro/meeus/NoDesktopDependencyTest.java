package org.matheclipse.astro.meeus;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Keeps the module free of desktop dependencies.
 *
 * <p>
 * The Night Vision code this module ports from is a Swing application. Only its formulas and data
 * were taken; everything the module draws is a Symja <code>Graphics</code> expression rendered by
 * the SVG pipeline. This test fails if an AWT, Swing or ImageIO import slips in, which would also
 * break headless servers and the GraalVM native image.
 */
public class NoDesktopDependencyTest {

  private static final Pattern DESKTOP =
      Pattern.compile("^\\s*import\\s+(static\\s+)?(java\\.awt|javax\\.swing|javax\\.imageio)\\b",
          Pattern.MULTILINE);

  @Test
  public void testNoAwtOrSwingImports() throws IOException {
    Path sources = Paths.get("src", "main", "java");
    assertTrue(Files.isDirectory(sources), "run from the module directory: " + sources);
    List<String> offenders = new ArrayList<>();
    try (Stream<Path> files = Files.walk(sources)) {
      for (Path file : (Iterable<Path>) files.filter(p -> p.toString().endsWith(".java"))::iterator) {
        String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        if (DESKTOP.matcher(text).find()) {
          offenders.add(file.toString());
        }
      }
    }
    assertTrue(offenders.isEmpty(), "desktop dependencies in " + offenders);
  }
}
