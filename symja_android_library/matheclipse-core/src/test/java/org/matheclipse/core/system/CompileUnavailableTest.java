package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.compile.IExprCompiler;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.UnaryCompiled;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>matheclipse-core</code> has no Java compiler: <code>Compile</code>,
 * <code>CompiledFunction</code> and <code>CompilePrint</code> live in
 * <code>matheclipse-compile</code>, and no {@link IExprCompiler} is installed on a core-only
 * classpath - the situation on Android, and in every module which does not depend on
 * <code>matheclipse-compile</code>.
 *
 * <p>
 * Everything still has to work; only slower. This pins that down, because the failure mode is
 * silent otherwise.
 */
public class CompileUnavailableTest extends ExprEvaluatorTestCase {

  @Test
  public void testNoCompilerIsInstalled() {
    assertNull(IExprCompiler.get(), "no IExprCompiler may be installed on a core-only classpath");
  }

  @Test
  public void testCompileStaysUnevaluated() {
    check("Compile({{x, _Real}}, x^2)", //
        "Compile({{x,_Real}},x^2)");
  }

  @Test
  public void testCompilePrintStaysUnevaluated() {
    check("CompilePrint({{x, _Real}}, x^2)", //
        "CompilePrint({{x,_Real}},x^2)");
  }

  /**
   * {@link UnaryCompiled} used to dereference the compiled function without a null check, so it
   * threw a {@link NullPointerException} whenever compilation failed. It now falls back to
   * interpreted evaluation.
   */
  @Test
  public void testUnaryCompiledFallsBackToInterpretation() {
    ISymbol x = F.Dummy("x");
    UnaryCompiled function = new UnaryCompiled(F.Plus(F.Sqr(x), F.C1), x);
    assertEquals(10.0, function.value(3.0), 1E-12);
    assertEquals(F.num(10.0), function.apply(F.num(3.0)));
  }

  /**
   * {@link UnaryNumerical} may ask for a compiled fast path, but must sample the interpreted way
   * when there is no compiler to give it one - even with the flag switched on.
   */
  @Test
  public void testUnaryNumericalFallsBackToInterpretation() {
    boolean savedFlag = Config.COMPILE_NUMERIC_FUNCTIONS;
    try {
      Config.COMPILE_NUMERIC_FUNCTIONS = true;
      ISymbol x = F.Dummy("x");
      UnaryNumerical function = new UnaryNumerical(F.Plus(F.Sqr(x), F.C1), x, Double.NaN);
      assertEquals(10.0, function.value(3.0), 1E-12);
    } finally {
      Config.COMPILE_NUMERIC_FUNCTIONS = savedFlag;
    }
  }
}
