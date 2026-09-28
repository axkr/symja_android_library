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
    // Deviation: sympy's truncate(n) returns the first n nonzero terms, the Java API returns the
    // terms up to the harmonic of the given order. The order is chosen to return the same terms.
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
    checkEqual("0", Fourier.fourierCosCoefficient(x, x, mpi, pi, F.C3, engine));
    // assert fe.term(3) == -4*cos(3*x) / 9
    checkEqual("-4/9", Fourier.fourierCosCoefficient(parse("x^2"), x, mpi, pi, F.C3, engine));
    checkEqual("0", Fourier.fourierSinCoefficient(parse("x^2"), x, mpi, pi, F.C3, engine));
    // assert fp.term(3) == 2*sin(3*x) / 3
    checkEqual("2/3", Fourier.fourierSinCoefficient(parse("Piecewise({{0, x < 0}}, Pi)"), x, mpi,
        pi, F.C3, engine));

    // assert fo.as_leading_term(x) == 2*sin(x)
    checkEqual("2*Sin(x)", Fourier.fourierSeries(x, x, mpi, pi, 1, engine));
    // assert fe.as_leading_term(x) == pi**2 / 3
    checkEqual("Pi^2/3", Fourier.fourierSeries(parse("x^2"), x, mpi, pi, 0, engine));
    // assert fp.as_leading_term(x) == pi / 2
    checkEqual("Pi/2",
        Fourier.fourierSeries(parse("Piecewise({{0, x < 0}}, Pi)"), x, mpi, pi, 0, engine));

    // raises(ValueError, lambda: fourier_series(x, (x, 0, oo)))
    assertThrows(ValueError.class,
        () -> Fourier.fourierSeries(x, x, F.C0, F.CInfinity, 3, engine));
    // the iteration, subs and the lazy FourierSeries object aren't ported
  }

  @Test
  public void testFourierSeries2() {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = parse("x");
    // p = Piecewise((0, x < 0), (x, True))
    // f = fourier_series(p, (x, -2, 2))
    IExpr p = parse("Piecewise({{0, x < 0}}, x)");
    // assert f.term(3) == (2*sin(3*pi*x / 2) / (3*pi) - 4*cos(3*pi*x / 2) / (9*pi**2))
    checkEqual("2/(3*Pi)", Fourier.fourierSinCoefficient(p, x, F.CN2, F.C2, F.C3, engine));
    checkEqual("-4/(9*Pi^2)", Fourier.fourierCosCoefficient(p, x, F.CN2, F.C2, F.C3, engine));
    // assert f.truncate() == (2*sin(pi*x / 2) / pi - sin(pi*x) / pi -
    // 4*cos(pi*x / 2) / pi**2 + S.Half)
    checkEqual("2*Sin(Pi*x/2)/Pi - Sin(Pi*x)/Pi - 4*Cos(Pi*x/2)/Pi^2 + 1/2",
        Fourier.fourierSeries(p, x, F.CN2, F.C2, 2, engine));
  }

  @Test
  public void testSquareWave() {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = parse("x");
    // square_wave = Piecewise((1, x < pi), (-1, True))
    // s = fourier_series(square_wave, (x, 0, 2*pi))
    // assert s.truncate(3) == 4 / pi * sin(x) + 4 / (3 * pi) * sin(3 * x) +
    // 4 / (5 * pi) * sin(5 * x)
    checkEqual("4/Pi*Sin(x) + 4/(3*Pi)*Sin(3*x) + 4/(5*Pi)*Sin(5*x)", Fourier.fourierSeries(
        parse("Piecewise({{1, x < Pi}}, -1)"), x, F.C0, F.C2Pi, 5, engine));
    // sigma_approximation isn't ported
  }

  @Test
  public void testSawtoothWave() {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = parse("x");
    // s = fourier_series(x, (x, 0, pi))
    // assert s.truncate(4) == pi/2 - sin(2*x) - sin(4*x)/2 - sin(6*x)/3
    checkEqual("Pi/2 - Sin(2*x) - Sin(4*x)/2 - Sin(6*x)/3",
        Fourier.fourierSeries(x, x, F.C0, S.Pi, 3, engine));
    // s = fourier_series(x, (x, 0, 1))
    // assert s.truncate(4) == S.Half - sin(2*pi*x)/pi - sin(4*pi*x)/(2*pi) - sin(6*pi*x)/(3*pi)
    checkEqual("1/2 - Sin(2*Pi*x)/Pi - Sin(4*Pi*x)/(2*Pi) - Sin(6*Pi*x)/(3*Pi)",
        Fourier.fourierSeries(x, x, F.C0, F.C1, 3, engine));
  }

  @Test
  public void testFourierSeriesFinite() {
    EvalEngine engine = evaluator.getEvalEngine();
    IExpr x = parse("x");
    IExpr pi = S.Pi;
    IExpr mpi = F.Negate(S.Pi);
    // assert fourier_series(sin(x)).truncate(1) == sin(x)
    checkEqual("Sin(x)", Fourier.fourierSeries(parse("Sin(x)"), x, mpi, pi, 1, engine));
    // assert fourier_series(sin(x)*log(y)*exp(z),(x,pi,-pi)).truncate() == sin(x)*log(y)*exp(z)
    checkEqual("Sin(x)*Log(y)*Exp(z)",
        Fourier.fourierSeries(parse("Sin(x)*Log(y)*Exp(z)"), x, pi, mpi, 3, engine));
    // assert fourier_series(sin(x)**6).truncate(oo) == -15*cos(2*x)/32 + 3*cos(4*x)/16 -
    // cos(6*x)/32 + Rational(5, 16)
    checkEqual("-15*Cos(2*x)/32 + 3*Cos(4*x)/16 - Cos(6*x)/32 + 5/16",
        Fourier.fourierSeries(parse("Sin(x)^6"), x, mpi, pi, 8, engine));
    // assert fourier_series(sin(x) ** 6).truncate() == -15 * cos(2 * x) / 32 + 3 * cos(4 * x) / 16
    // + Rational(5, 16)
    checkEqual("-15*Cos(2*x)/32 + 3*Cos(4*x)/16 + 5/16",
        Fourier.fourierSeries(parse("Sin(x)^6"), x, mpi, pi, 4, engine));
  }

  // the operations shift, shiftx, scale, scalex, neg, add, sub of the FourierSeries object aren't
  // ported

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
