package org.matheclipse.core.numerics;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.hipparchus.analysis.MultivariateFunction;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.numerics.optim.DifferentialEvolution;

public class DifferentialEvolutionTest {

  /** 6 minima per variable in the box, the lowest one at the shift. */
  private static MultivariateFunction rastrigin(double... shift) {
    return x -> {
      double sum = 10.0 * x.length;
      for (int i = 0; i < x.length; i++) {
        double t = x[i] - shift[i];
        sum += t * t - 10.0 * Math.cos(2.0 * Math.PI * t);
      }
      return sum;
    };
  }

  private static double[] fill(int dimension, double value) {
    double[] result = new double[dimension];
    java.util.Arrays.fill(result, value);
    return result;
  }

  @Test
  public void testGlobalMinimumInABox() {
    double[] shift = {1.25, -2.5, 0.75};
    DifferentialEvolution.Result result = DifferentialEvolution.minimize(rastrigin(shift),
        fill(3, -5.0), fill(3, 5.0), true, null, 45, 300, 42L);
    assertEquals(0.0, result.value, 1e-6);
    assertArrayEquals(shift, result.point, 1e-4);
  }

  @Test
  public void testStaysInTheBox() {
    // the minimum of the plane is at the corner
    DifferentialEvolution.Result result = DifferentialEvolution.minimize(x -> x[0] + 2.0 * x[1],
        new double[] {-1.0, 3.0}, new double[] {4.0, 7.0}, true, null, 30, 300, 42L);
    assertArrayEquals(new double[] {-1.0, 3.0}, result.point, 1e-6);
    assertEquals(5.0, result.value, 1e-6);
  }

  @Test
  public void testLeavesTheStartBox() {
    // not bounded: the box is the place to start from, the minimum is outside of it
    DifferentialEvolution.Result result =
        DifferentialEvolution.minimize(x -> (x[0] - 12.5) * (x[0] - 12.5) + x[1] * x[1],
            fill(2, -1.0), fill(2, 1.0), false, null, 30, 500, 42L);
    assertArrayEquals(new double[] {12.5, 0.0}, result.point, 1e-4);
  }

  @Test
  public void testDeterministic() {
    double[] shift = {0.5, -1.5};
    DifferentialEvolution.Result first = DifferentialEvolution.minimize(rastrigin(shift),
        fill(2, -5.0), fill(2, 5.0), true, null, 30, 50, 7L);
    DifferentialEvolution.Result second = DifferentialEvolution.minimize(rastrigin(shift),
        fill(2, -5.0), fill(2, 5.0), true, null, 30, 50, 7L);
    assertArrayEquals(first.point, second.point, 0.0);
    assertEquals(first.value, second.value, 0.0);
    assertEquals(first.evaluations, second.evaluations);
  }

  @Test
  public void testStartPoint() {
    // the start point is a member of the population: the result is not worse than it
    double[] start = {0.5, -1.5};
    DifferentialEvolution.Result result = DifferentialEvolution.minimize(rastrigin(start),
        fill(2, -5.0), fill(2, 5.0), true, start, 5, 1, 7L);
    assertEquals(0.0, result.value, 0.0);
  }

  @Test
  public void testNotANumber() {
    // values which are not numbers are the worst ones
    DifferentialEvolution.Result result =
        DifferentialEvolution.minimize(x -> x[0] < 0.0 ? Double.NaN : (x[0] - 2.0) * (x[0] - 2.0),
            new double[] {-10.0}, new double[] {10.0}, true, null, 20, 300, 42L);
    assertEquals(2.0, result.point[0], 1e-5);
    assertTrue(result.evaluations > 20);
    assertNull(DifferentialEvolution.minimize(x -> Double.NaN, new double[] {-1.0},
        new double[] {1.0}, true, null, 20, 10, 42L));
  }
}
