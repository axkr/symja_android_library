package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.matheclipse.core.basic.Config;

/**
 * Contexts as they are written in Wolfram Language source: <code>Foo`Bar</code>, and the relative
 * <code>`x</code> / <code>`Private`x</code> that every package uses after
 * <code>Begin["`Private`"]</code>.
 *
 * <p>
 * The file path is what matters here. <code>Get</code> and the console's <code>-file</code> read a
 * package through {@code Parser}/{@code AST2Expr} rather than through the interactive
 * {@code ExprParser}, and that is where the context used to be dropped: every
 * <code>Internal`Kernel`x</code> in a package became a bare <code>x</code> in whatever context
 * happened to be current.
 */
public class ContextSymbolTest extends ExprEvaluatorTestCase {

  @Test
  public void testLeadingBacktickIsTheCurrentContext() {
    check("BeginPackage(\"Foo`\")", //
        "");
    check("Begin(\"`Private`\")", //
        "Foo`Private`");
    check("`x = 41", //
        "41");
    check("Context(`x)", //
        "Foo`Private`");
    // the same symbol under its full name
    check("Foo`Private`x", //
        "41");
    check("End()", //
        "Foo`Private`");
    // back in Foo`, the longer relative spelling names it
    check("`Private`x", //
        "41");
    check("EndPackage()", //
        "");
    check("Foo`Private`x", //
        "41");
  }

  @Test
  public void testContextIsPartOfTheName() {
    check("Context(Foo`Bar`Baz)", //
        "Foo`Bar`");
    check("Head(Foo`Bar`Baz)", //
        "Symbol");
    // two contexts, two symbols
    check("A`x === B`x", //
        "False");
  }

  @Test
  public void testExplicitSystemContextNamesTheBuiltIn() {
    check("System`Plus === Plus", //
        "True");
    check("Context(System`Plus)", //
        "System`");
  }

  @Test
  public void testContextWithNoSymbolAfterItIsASyntaxError() {
    // it used to build a symbol with an empty name in that context, which then matched nothing
    check("Global`", //
        "Syntax error in line: 1 - Symbol name expected after the context \"Global`\"\n" //
            + "Global`\n" //
            + "      ^");
    check("a`b`", //
        "Syntax error in line: 1 - Symbol name expected after the context \"a`b`\"\n" //
            + "a`b`\n" //
            + "   ^");
  }

  @Test
  public void testPrecisionMarksAreNotContexts() {
    // the backtick of a precision mark must not start a context
    check("1`30", //
        "1");
    check("1.5``20", //
        "1.5");
    check("2.5*^3", //
        "2500.0");
    check("2^^101", //
        "5");
  }

  @Test
  public void testPackageReadFromAFileKeepsItsContexts(@TempDir Path directory) throws IOException {
    Path file = directory.resolve("Pkg.m");
    Files.write(file, ("BeginPackage(\"Pkg`\")\n" //
        + "Begin(\"`Private`\")\n" //
        + "helper(x_) := x^2\n" //
        + "exported(x_) := helper(x) + Internal`Kernel`Offset\n" //
        + "End()\n" //
        + "EndPackage()\n").getBytes(StandardCharsets.UTF_8));

    boolean fileSystem = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = true;
    try {
      check("Get(\"" + file.toString().replace("\\", "\\\\") + "\")", //
          "");
      check("Pkg`Private`helper(3)", //
          "9");
      check("Context(Pkg`Private`helper)", //
          "Pkg`Private`");
      // the qualified symbol in the body stayed in its own context
      // (the name is lower-cased because this suite runs in relaxed syntax; the context is not)
      assertTrue(String.valueOf(evaluator.eval("Pkg`Private`exported(3)"))
          .contains("Internal`Kernel`"));
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }
}
