package org.matheclipse.io.system;

import org.junit.jupiter.api.Test;

/**
 * <code>Compile</code>, <code>CompiledFunction</code> and <code>CompilePrint</code> moved out of
 * <code>matheclipse-core</code> into <code>matheclipse-compile</code>, which reaches the two
 * servlets and the two consoles through
 * {@link org.matheclipse.io.IOInit#init()}.
 *
 * <p>
 * Both servlets set <code>ToggleFeature.COMPILE</code> and then call <code>IOInit.init()</code>,
 * which is exactly what {@link AbstractTestCase#setUp()} does, so this covers the servlet path
 * without standing a servlet container up. If the dependency or the
 * <code>CompileInit.init()</code> call is ever dropped from <code>matheclipse-io</code>, these
 * expressions come back unevaluated and this test fails - the loss would otherwise be silent.
 */
public class CompileAvailabilityTest extends AbstractTestCase {

  @Test
  public void testCompileIsRegistered() {
    check("f=Compile({{x, _Real}}, x^2+1); f(3.0)", //
        "10.0");
  }

  @Test
  public void testCompiledFunctionIsRegistered() {
    check("Head(Compile({{x, _Real}}, x^2))", //
        "CompiledFunction");
  }

  @Test
  public void testCompilePrintIsRegistered() {
    check("StringQ(CompilePrint({{x, _Real}}, x^2))", //
        "True");
  }
}
