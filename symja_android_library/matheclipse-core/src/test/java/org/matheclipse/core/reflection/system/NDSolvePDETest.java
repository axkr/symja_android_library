package org.matheclipse.core.reflection.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>NDSolve</code> of a partial differential equation in one space variable: the heat and the
 * wave equation, with given values at the ends or a periodic domain. Each is checked against a
 * closed form solution.
 */
public class NDSolvePDETest {

  @BeforeEach
  public void setUp() throws InterruptedException {
    F.await();
  }

  private static double value(String input) {
    IExpr result = new ExprEvaluator().eval(input);
    assertTrue(result.isReal(), () -> input + " gave " + result);
    return result.evalf();
  }

  @Test
  public void testHeatEquationWithValuesAtTheEnds() {
    double u = value("With({sol = NDSolve({D(u(t,x),t) == D(u(t,x),{x,2}), u(0,x) == Sin(Pi*x),"
        + " u(t,0) == 0, u(t,1) == 0}, u, {t,0,0.1}, {x,0,1})}, u(0.05,0.3) /. sol[[1]])");
    assertEquals(Math.exp(-Math.PI * Math.PI * 0.05) * Math.sin(Math.PI * 0.3), u, 1e-3);
  }

  /** u(t,0) == u(t,1) joins the ends; it is not the value u(t,1) for the end at 0. */
  @Test
  public void testHeatEquationOnAPeriodicDomain() {
    String sol = "sol = NDSolve({D(u(t,x),t) == D(u(t,x),{x,2}), u(0,x) == Cos(2*Pi*x),"
        + " u(t,0) == u(t,1)}, u, {t,0,0.1}, {x,0,1})";
    double u = value("With({" + sol + "}, u(0.05,0.3) /. sol[[1]])");
    assertEquals(Math.exp(-4 * Math.PI * Math.PI * 0.05) * Math.cos(2 * Math.PI * 0.3), u, 1e-3);
    double gap =
        value("With({" + sol + "}, Abs((u(0.07,0) /. sol[[1]]) - (u(0.07,1) /. sol[[1]])))");
    assertEquals(0.0, gap, 1e-12);
  }

  @Test
  public void testWaveEquationWithValuesAtTheEnds() {
    double u = value("With({sol = NDSolve({D(u(t,x),t,t) == D(u(t,x),{x,2}), u(0,x) == Sin(Pi*x),"
        + " (D(u(t,x),t) /. t -> 0) == 0, u(t,0) == 0, u(t,1) == 0}, u, {t,0,1}, {x,0,1})},"
        + " u(0.3,0.7) /. sol[[1]])");
    assertEquals(Math.sin(Math.PI * 0.7) * Math.cos(Math.PI * 0.3), u, 1e-3);
  }

  /** The standing wave Cos(2 Pi x) Cos(2 Pi t) on a domain one period wide. */
  @Test
  public void testWaveEquationOnAPeriodicDomain() {
    double u = value("With({sol = NDSolve({D(u(t,x),t,t) == D(u(t,x),{x,2}), u(0,x) == Cos(2*Pi*x),"
        + " (D(u(t,x),t) /. t -> 0) == 0, u(t,0) == u(t,1)}, u, {t,0,1}, {x,0,1})},"
        + " u(0.3,0.7) /. sol[[1]])");
    assertEquals(Math.cos(2 * Math.PI * 0.7) * Math.cos(2 * Math.PI * 0.3), u, 3e-3);
  }

  /**
   * A nonlinear Klein-Gordon term on a periodic domain, with a method option, as the Wolfram
   * Demonstration "Nonlinear Wave Equations" writes it: the ends stay joined and the solution keeps
   * the symmetry x -> -x of the equation and its initial data.
   */
  @Test
  public void testNonlinearWaveEquationOnAPeriodicDomain() {
    String sol = "sol = NDSolve({D(u(t,x),t,t) == D(u(t,x),{x,2}) - u(t,x) + u(t,x)^3/6,"
        + " u(0,x) == Cos(Pi*x), (D(u(t,x),t) /. t -> 0) == 0, u(t,-1) == u(t,1)}, u, {t,0,1},"
        + " {x,-1,1}, Method -> {\"MethodOfLines\", \"SpatialDiscretization\" ->"
        + " {\"TensorProductGrid\", \"DifferenceOrder\" -> \"Pseudospectral\","
        + " \"MinStepSize\" -> 0.2}})";
    assertEquals(0.0,
        value("With({" + sol + "}, Abs((u(0.6,-1) /. sol[[1]]) - (u(0.6,1) /. sol[[1]])))"), 1e-6);
    assertEquals(0.0,
        value("With({" + sol + "}, Abs((u(0.6,0.4) /. sol[[1]]) - (u(0.6,-0.4) /. sol[[1]])))"),
        1e-2);
    value("With({" + sol + "}, u(0.6,0.2) /. sol[[1]])");
  }

  /** A periodic end together with a given value is no problem this solver takes. */
  @Test
  public void testPeriodicAndGivenEndsTogetherAreDeclined() {
    IExpr result = new ExprEvaluator().eval("Head(NDSolve({D(u(t,x),t,t) == D(u(t,x),{x,2}),"
        + " u(0,x) == Cos(2*Pi*x), (D(u(t,x),t) /. t -> 0) == 0, u(t,0) == u(t,1), u(t,1) == 0},"
        + " u, {t,0,1}, {x,0,1}))");
    assertEquals("NDSolve", result.toString());
  }

  /**
   * The cyclic tridiagonal solver with different corners - a symmetric system could not tell the
   * top right corner from the bottom left one - checked by multiplying the solution back.
   */
  @Test
  public void testCyclicTridiagonalWithUnequalCorners() {
    int m = 5;
    double[] lower = {0, 1, 2, -1, 0.5};
    double[] diagonal = {4, 5, 6, 7, 8};
    double[] upper = {1, -2, 0.5, 1.5, 0};
    double topRight = 0.7;
    double bottomLeft = -1.3;
    double[] right = {1, 2, 3, 4, 5};
    double[] x = new double[m];
    assertTrue(NDSolvePDE.solveCyclicTridiagonal(lower, diagonal, upper, right, x, m, bottomLeft,
        topRight));
    for (int i = 0; i < m; i++) {
      double row = diagonal[i] * x[i];
      if (i > 0) {
        row += lower[i] * x[i - 1];
      }
      if (i < m - 1) {
        row += upper[i] * x[i + 1];
      }
      if (i == 0) {
        row += topRight * x[m - 1];
      }
      if (i == m - 1) {
        row += bottomLeft * x[0];
      }
      assertEquals(right[i], row, 1e-12, "row " + i);
    }
  }
}
