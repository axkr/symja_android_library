package org.matheclipse.core.integrate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The three finite-domain families {@link DiffUnderIntegral} answers by differentiating under the
 * integral sign, and the near misses it has to turn away.
 *
 * <p>
 * The stage is driven directly rather than through <code>Integrate</code>, so that a case which
 * some other stage of the cascade can also do still tells us whether this one is right.
 */
public class IntegrateDiffUnderIntTest {

  private ExprEvaluator evaluator;

  private EvalEngine engine;

  @BeforeEach
  public void setUp() {
    F.initSymbols();
    evaluator = new ExprEvaluator(false, (short) 100);
    engine = evaluator.getEvalEngine();
  }

  /**
   * The value of the integral over the stated interval, as the stage returns it.
   *
   * <p>
   * The variable is the one the parser made, not {@link F#x}: which of the two a source line means
   * depends on whether lowercase symbols are switched on, and handing over the wrong one leaves an
   * integrand the stage reads as having no variable in it at all.
   */
  private IExpr value(String integrand, String upper) {
    IExpr f = evaluator.eval(integrand);
    return DiffUnderIntegral.integrate(f, evaluator.eval("x"), F.C0, evaluator.eval(upper), engine);
  }

  /** Asserts the stage answers, and that the answer is the expected one. */
  private void check(String integrand, String upper, String expected) {
    IExpr actual = value(integrand, upper);
    assertTrue(actual.isPresent(), integrand + " was not recognized");
    assertEquals(evaluator.eval(expected), engine.evaluate(actual), integrand);
  }

  /** Asserts the stage declines, which for a near miss is the only right answer. */
  private void checkDeclines(String integrand, String upper) {
    assertEquals(F.NIL, value(integrand, upper), integrand + " should not have been claimed");
  }

  /**
   * <code>Integrate(Log(1+c*x^p)/(x*Sqrt(1-x^(2*p))), {x,0,1})</code>, whose value is
   * <code>(Pi^2/8 - ArcCos(c)^2/2)/p</code>. The first case is the classical one, with
   * <code>ArcCos(1) == 0</code> leaving <code>Pi^2/8</code>.
   */
  @Test
  public void powerLog() {
    check("Log(1+x)/(x*Sqrt(1-x^2))", "1", "Pi^2/8");
    check("Log(1+x^2)/(x*Sqrt(1-x^4))", "1", "Pi^2/16");
    check("Log(1+x*Sqrt(x))/(x*Sqrt(1-x^3))", "1", "Pi^2/12");
    check("Log(1+Sqrt(x))/(x*Sqrt(1-x))", "1", "Pi^2/4");
  }

  /** The same family with the parameter inside the logarithm not one. */
  @Test
  public void powerLogWithCoefficient() {
    check("Log(1+x/2)/(x*Sqrt(1-x^2))", "1", "5/72*Pi^2");
    check("Log(1+(Sqrt(3)/2)*x^2)/(x*Sqrt(1-x^4))", "1", "Pi^2/18");
  }

  /**
   * <code>Integrate(Sec(2*x)*Log(1+c*Sqrt(1-Tan(x)^2)), {x,0,Pi/4})</code>, the same integral
   * reached by <code>t = Tan(x)</code> and so carrying the same value with <code>p == 1</code>.
   */
  @Test
  public void secantRadical() {
    check("Sec(2*x)*Log(1+Sqrt(1-Tan(x)^2))", "Pi/4", "Pi^2/8");
    check("Log(1+Sqrt(1-Tan(x)^2))/Cos(2*x)", "Pi/4", "Pi^2/8");
    check("Sec(2*x)*Log(1+(1/2)*Sqrt(1-Tan(x)^2))", "Pi/4", "5/72*Pi^2");
  }

  /**
   * <code>Integrate(Csc(2*x)^2*Log(1+Tan(x)^a), {x,0,Pi/4})</code>, which is
   * <code>(Pi*Csc(Pi/a) - a)/4</code>. The exponent may be a symbol: nothing here needs it to be a
   * number, and the reflection formula holds for every <code>a &gt; 1</code>.
   */
  @Test
  public void tangentPower() {
    check("Csc(2*x)^2*Log(1+Tan(x)^2)", "Pi/4", "(Pi-2)/4");
    check("Csc(2*x)^2*Log(1+Tan(x)^3)", "Pi/4", "(Pi*Csc(Pi/3)-3)/4");
    check("Csc(2*x)^2*Log(1+Tan(x)^(5/2))", "Pi/4", "(Pi*Csc(2*Pi/5)-5/2)/4");
    check("Log(1+Tan(x)^a)/Sin(2*x)^2", "Pi/4",
        "ConditionalExpression((Pi*Csc(Pi/a)-a)/4, a>1)");
  }

  /**
   * The near misses. Each is one edit away from a family above and none of them has that family's
   * value: the radical no longer matches the power in the logarithm, the logarithm's constant term
   * is not one (so the base case is not zero), the exponent is one (the integral diverges), the
   * cosecant is cubed, and the last is not this kind of integral at all.
   */
  @Test
  public void nearMissesAreDeclined() {
    checkDeclines("Log(1+x)/(x*Sqrt(1-x^3))", "1");
    checkDeclines("Sec(2*x)*Log(2+Sqrt(1-Tan(x)^2))", "Pi/4");
    checkDeclines("Csc(2*x)^2*Log(1+Tan(x))", "Pi/4");
    checkDeclines("Csc(2*x)^3*Log(1+Tan(x)^2)", "Pi/4");
    checkDeclines("x^2", "1");
  }

  /** The interval is part of the claim: the same integrand over another one is not this integral. */
  @Test
  public void otherIntervalsAreDeclined() {
    checkDeclines("Log(1+x)/(x*Sqrt(1-x^2))", "1/2");
    checkDeclines("Csc(2*x)^2*Log(1+Tan(x)^2)", "Pi/3");
  }

  /** Switched off, the stage claims nothing at all. */
  @Test
  public void killSwitch() {
    boolean old = Config.INTEGRATE_ALGORITHM_DIFF_UNDER_INT;
    Config.INTEGRATE_ALGORITHM_DIFF_UNDER_INT = false;
    try {
      checkDeclines("Log(1+x)/(x*Sqrt(1-x^2))", "1");
      checkDeclines("Csc(2*x)^2*Log(1+Tan(x)^2)", "Pi/4");
    } finally {
      Config.INTEGRATE_ALGORITHM_DIFF_UNDER_INT = old;
    }
  }

  /** The names a <code>Method</code> option may use to ask for this stage by hand. */
  @Test
  public void methodNames() {
    assertTrue(DiffUnderIntegral.isMethodName("DiffUnderInt"));
    assertTrue(DiffUnderIntegral.isMethodName("Feynman"));
    assertTrue(DiffUnderIntegral.isMethodName("leibniz"));
    assertTrue(!DiffUnderIntegral.isMethodName("Chebyshev"));
  }
}
