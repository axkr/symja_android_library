package org.matheclipse.core.numerics.optim;

import java.util.SplittableRandom;
import org.hipparchus.analysis.MultivariateFunction;

/**
 * Differential evolution, a population search for the global minimum of a function of several
 * variables which needs no derivative and no start point.
 * <p>
 * The start population is a Latin hypercube sample of a box. A member <code>x</code> is replaced
 * by the trial point <code>x+F*(best-x)+F*(a-b)</code> ("current-to-best") or
 * <code>c+F*(a-b)</code> ("rand") of other members <code>a, b, c</code>, crossed over with
 * <code>x</code>, if the trial point is not worse. The scaling factor <code>F</code> is drawn
 * anew for every generation from <code>[0.5, 1.0]</code>.
 * <p>
 * A search is <b>deterministic</b>: the random numbers come from a generator which is created
 * for the call from the given seed. There is no static state, the same call gives the same
 * answer in every thread.
 */
public final class DifferentialEvolution {

  /** The result of a search. */
  public static final class Result {
    /** the best point which was found */
    public final double[] point;
    /** the value of the function at {@link #point} */
    public final double value;
    /** the number of function evaluations */
    public final int evaluations;

    Result(double[] point, double value, int evaluations) {
      this.point = point;
      this.value = value;
      this.evaluations = evaluations;
    }
  }

  private static final double CROSSOVER = 0.9;

  /** The search stops when the values of the population differ by less than this, relatively. */
  private static final double VALUE_TOLERANCE = 1e-12;

  private DifferentialEvolution() {}

  /**
   * Search for the global minimum of <code>function</code>.
   *
   * @param function the function to minimize; a value which is not a finite number counts as
   *        infinitely bad
   * @param lower the lower corner of the box of the start population
   * @param upper the upper corner of the box of the start population
   * @param bounded <code>true</code> if the search has to stay in the box, <code>false</code> if
   *        the box is only the place to start from
   * @param start a point which becomes a member of the start population, or <code>null</code>
   * @param populationSize the number of members of the population
   * @param maxGenerations the search stops after this number of generations
   * @param seed the seed of the random numbers of this search
   * @return <code>null</code> if the function has no finite value at any point of the start
   *         population
   */
  public static Result minimize(MultivariateFunction function, double[] lower, double[] upper,
      boolean bounded, double[] start, int populationSize, int maxGenerations, long seed) {
    final int dimension = lower.length;
    final int size = Math.max(populationSize, 5);
    final SplittableRandom random = new SplittableRandom(seed);
    final double[][] population = new double[size][dimension];
    final double[] values = new double[size];
    int evaluations = 0;

    // Latin hypercube: every variable has one member in each of the `size` slices of its range
    for (int d = 0; d < dimension; d++) {
      int[] slices = new int[size];
      for (int i = 0; i < size; i++) {
        slices[i] = i;
      }
      for (int i = size - 1; i > 0; i--) {
        int j = random.nextInt(i + 1);
        int swap = slices[i];
        slices[i] = slices[j];
        slices[j] = swap;
      }
      double width = upper[d] - lower[d];
      for (int i = 0; i < size; i++) {
        population[i][d] = lower[d] + (slices[i] + random.nextDouble()) / size * width;
      }
    }
    if (start != null) {
      System.arraycopy(start, 0, population[0], 0, dimension);
      if (bounded) {
        clip(population[0], lower, upper);
      }
    }
    int best = -1;
    for (int i = 0; i < size; i++) {
      values[i] = value(function, population[i]);
      evaluations++;
      if (values[i] < Double.POSITIVE_INFINITY && (best < 0 || values[i] < values[best])) {
        best = i;
      }
    }
    if (best < 0) {
      return null;
    }

    final double[] trial = new double[dimension];
    for (int generation = 0; generation < maxGenerations; generation++) {
      if (Thread.currentThread().isInterrupted()) {
        break;
      }
      final double factor = 0.5 + 0.5 * random.nextDouble();
      for (int i = 0; i < size; i++) {
        int a = random.nextInt(size);
        while (a == i) {
          a = random.nextInt(size);
        }
        int b = random.nextInt(size);
        while (b == i || b == a) {
          b = random.nextInt(size);
        }
        int c = random.nextInt(size);
        while (c == i || c == a || c == b) {
          c = random.nextInt(size);
        }
        final boolean toBest = random.nextBoolean();
        final int forced = random.nextInt(dimension);
        final double[] x = population[i];
        for (int d = 0; d < dimension; d++) {
          if (d == forced || random.nextDouble() < CROSSOVER) {
            double difference = factor * (population[a][d] - population[b][d]);
            trial[d] = toBest ? x[d] + factor * (population[best][d] - x[d]) + difference
                : population[c][d] + difference;
          } else {
            trial[d] = x[d];
          }
        }
        if (bounded) {
          clip(trial, lower, upper);
        }
        double value = value(function, trial);
        evaluations++;
        if (value <= values[i]) {
          System.arraycopy(trial, 0, x, 0, dimension);
          values[i] = value;
          if (value < values[best]) {
            best = i;
          }
        }
      }
      double worst = values[best];
      for (int i = 0; i < size; i++) {
        worst = Math.max(worst, values[i]);
      }
      if (worst - values[best] <= VALUE_TOLERANCE * (1.0 + Math.abs(values[best]))) {
        break;
      }
      if (!bounded && values[best] < -1e200) {
        // the function has no lower bound in this direction
        break;
      }
    }
    return new Result(population[best].clone(), values[best], evaluations);
  }

  private static double value(MultivariateFunction function, double[] point) {
    double value;
    try {
      value = function.value(point);
    } catch (ArithmeticException | org.hipparchus.exception.MathRuntimeException ex) {
      return Double.POSITIVE_INFINITY;
    }
    return Double.isNaN(value) ? Double.POSITIVE_INFINITY : value;
  }

  private static void clip(double[] point, double[] lower, double[] upper) {
    for (int d = 0; d < point.length; d++) {
      point[d] = Math.min(upper[d], Math.max(lower[d], point[d]));
    }
  }
}
