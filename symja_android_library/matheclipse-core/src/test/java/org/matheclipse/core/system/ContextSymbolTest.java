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
 * Contexts as they are written in script source: <code>Foo`Bar</code>, and the relative
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

  /** A relative name is resolved against the complete name of the context open now. */
  @Test
  public void testNestedRelativeContexts() {
    check("Begin(\"`rel`\")", //
        "Global`rel`");
    check("End()", //
        "Global`rel`");
    check("BeginPackage(\"Pk`\")", //
        "");
    check("Begin(\"`A`\")", //
        "Pk`A`");
    check("Begin(\"`B`\")", //
        "Pk`A`B`");
    check("`bx = 7; Context(`bx)", //
        "Pk`A`B`");
    check("End(); End(); EndPackage()", //
        "");
    check("Pk`A`B`bx", //
        "7");
  }

  /** <code>$ContextPath</code> and <code>$Context</code> can be assigned, as packages do. */
  @Test
  public void testContextPathAndContextAssignment() {
    check("Ctx1`zz = 3", //
        "3");
    check("AppendTo($ContextPath, \"Ctx1`\"); Last($ContextPath)", //
        "Ctx1`");
    // the symbol of the appended context is found by its short name
    check("zz", //
        "3");
    check("$ContextPath = DeleteCases($ContextPath, \"Ctx1`\"); MemberQ($ContextPath, \"Ctx1`\")", //
        "False");
    check("$ContextPath = {1, 2}", //
        "$ContextPath={1,2}");
    check("$Context = \"Ctx2`\"; ww = 1; $Context = \"Global`\"; Context(Ctx2`ww)", //
        "Ctx2`");
  }

  /** <code>$Context</code> is searched before <code>$ContextPath</code>. */
  @Test
  public void testCurrentContextHidesThePath() {
    // separate inputs: the names of one input are all read before any of it is evaluated
    check("BeginPackage(\"Sh`\")", //
        "");
    check("shv = 1", //
        "1");
    check("Begin(\"`Private`\")", //
        "Sh`Private`");
    check("Sh`Private`shv = 2", //
        "2");
    check("{shv, Context(shv)}", //
        "{2,Sh`Private`}");
    check("End(); EndPackage()", //
        "");
    check("{shv, Context(shv)}", //
        "{1,Sh`}");
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
      assertTrue(
          String.valueOf(evaluator.eval("Pkg`Private`exported(3)")).contains("Internal`Kernel`"));
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystem;
    }
  }
}
