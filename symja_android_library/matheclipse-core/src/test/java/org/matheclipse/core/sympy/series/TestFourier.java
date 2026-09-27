package org.matheclipse.core.sympy.series;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.exception.ValueError;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestFourier extends ExprEvaluatorTestCase {

  private IExpr parse(String str) {
    return evaluator.getEvalEngine().parse(str);
  }

  /** The difference of both expressions must simplify to <code>0</code>. */
  private void checkEqual(String expected, IExpr actual) {
    EvalEngine engine = evaluator.getEvalEngine();
    assertTrue(actual.isPresent(), "no result for expected: " + expected);
    assertEquals("0",
        engine.evaluate(F.FullSimplify(F.Subtract(parse(expected), actual))).toString(),
        "expected: " + expected + " but was: " + actual);
  }

  @Test
  public void testFourierSeries() {
    // https://github.com/sympy/sympy/blob/master/sympy/series/tests/test_fourier.py
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = parse("x");
    IExpr pi = S.Pi;
    IExpr mpi = F.Negate(S.Pi);
    // fo = fourier_series(x, (x, -pi, pi))
    // assert fo.truncate() == 2*sin(x) - sin(2*x) + (2*sin(3*x) / 3)
    checkEqual("2*Sin(x) - Sin(2*x) + 2*Sin(3*x)/3",
        Fourier.fourierSeries(x, x, mpi, pi, 3, engine));
    // fe = fourier_series(x**2, (-pi, pi))
    // assert fe.truncate() == -4*cos(x) + cos(2*x) + pi**2 / 3
    checkEqual("-4*Cos(x) + Cos(2*x) + Pi^2/3",
        Fourier.fourierSeries(parse("x^2"), x, mpi, pi, 2, engine));
    // fp = fourier_series(Piecewise((0, x < 0), (pi, True)), (x, -pi, pi))
    // assert fp.truncate() == 2*sin(x) + (2*sin(3*x) / 3) + pi / 2
    checkEqual("2*Sin(x) + 2*Sin(3*x)/3 + Pi/2",
        Fourier.fourierSeries(parse("Piecewise({{0, x < 0}}, Pi)"), x, mpi, pi, 3, engine));
    // assert fourier_series(1, (-pi, pi)) == 1
    assertEquals("1", Fourier.fourierSeries(F.C1, x, mpi, pi, 3, engine).toString());

    // assert fo.term(3) == 2*sin(3*x) / 3
    checkEqual("2/3", Fourier.fourierSinCoefficient(x, x, mpi, pi, F.C3, engine));
    // assert fe.term(3) == -4*cos(3*x) / 9
    checkEqual("-4/9", Fourier.fourierCosCoefficient(parse("x^2"), x, mpi, pi, F.C3, engine));

    // s = fourier_series(x, (x, 0, pi))
    // assert s.truncate(4) == pi/2 - sin(2*x) - sin(4*x)/2 - sin(6*x)/3
    checkEqual("Pi/2 - Sin(2*x) - Sin(4*x)/2 - Sin(6*x)/3",
        Fourier.fourierSeries(x, x, F.C0, pi, 3, engine));
    // s = fourier_series(x, (x, 0, 1))
    // assert s.truncate(4) == S.Half - sin(2*pi*x)/pi - sin(4*pi*x)/(2*pi) - sin(6*pi*x)/(3*pi)
    checkEqual("1/2 - Sin(2*Pi*x)/Pi - Sin(4*Pi*x)/(2*Pi) - Sin(6*Pi*x)/(3*Pi)",
        Fourier.fourierSeries(x, x, F.C0, F.C1, 3, engine));

    // raises(ValueError, lambda: fourier_series(x, (x, 0, oo)))
    assertThrows(ValueError.class,
        () -> Fourier.fourierSeries(x, x, F.C0, F.CInfinity, 3, engine));
  }

  @Test
  public void testSymbolicCoefficient() {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = parse("x");
    IExpr n = parse("n");
    // the n-th sine coefficient of x on (-Pi, Pi) is -2*(-1)^n/n
    IExpr bn = Fourier.fourierSinCoefficient(x, x, F.Negate(S.Pi), S.Pi, n, engine);
    for (int k = 1; k < 5; k++) {
      checkEqual(((k & 1) == 1 ? "2/" : "-2/") + k, F.subst(bn, n, F.ZZ(k)));
    }
  }

  @Test
  public void testExponentialSeries() {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = parse("x");
    // c(n) of x on (-Pi, Pi) is I*(-1)^n/n
    checkEqual("-I", Fourier.fourierExpCoefficient(x, x, F.Negate(S.Pi), S.Pi, F.C1, engine));
    checkEqual("I/2", Fourier.fourierExpCoefficient(x, x, F.Negate(S.Pi), S.Pi, F.C2, engine));
    checkEqual("0", Fourier.fourierExpCoefficient(x, x, F.Negate(S.Pi), S.Pi, F.C0, engine));
    // the exponential series is equal to the trigonometric series
    checkEqual("2*Sin(x) - Sin(2*x)",
        Fourier.fourierExpSeries(x, x, F.Negate(S.Pi), S.Pi, 2, engine));
  }

  @Test
  public void testHalfRangeSeries() {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = parse("x");
    // even extension of x on (0, Pi) is Abs(x)
    checkEqual("Pi/2 - 4*Cos(x)/Pi - 4*Cos(3*x)/(9*Pi)",
        Fourier.fourierCosSeries(x, x, S.Pi, 3, engine));
    // odd extension of x on (0, Pi) is x
    checkEqual("2*Sin(x) - Sin(2*x) + 2*Sin(3*x)/3",
        Fourier.fourierSinSeries(x, x, S.Pi, 3, engine));
    // odd extension of 1 on (0, Pi) is the square wave
    checkEqual("4*Sin(x)/Pi + 4*Sin(3*x)/(3*Pi)",
        Fourier.fourierSinSeries(F.C1, x, S.Pi, 3, engine));
  }
}
