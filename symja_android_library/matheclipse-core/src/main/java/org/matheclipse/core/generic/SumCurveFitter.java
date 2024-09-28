package org.matheclipse.core.generic;

import java.util.Collection;
import org.hipparchus.analysis.ParametricUnivariateFunction;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.fitting.AbstractCurveFitter;
import org.hipparchus.fitting.WeightedObservedPoint;
import org.hipparchus.linear.DiagonalMatrix;
import org.hipparchus.optim.nonlinear.vector.leastsquares.LeastSquaresBuilder;
import org.hipparchus.optim.nonlinear.vector.leastsquares.LeastSquaresProblem;

/**
 * Fits points to a {@link org.hipparchus.analysis.ParametricUnivariateFunction} function. <br>
 * They must be sorted in increasing order of the polynomial's degree. The optimal values of the
 * coefficients will be returned in the same order.
 *
 */
public class SumCurveFitter extends AbstractCurveFitter {

  private final ParametricUnivariateFunction modelFunction;

  /** Initial guess. */
  private final double[] initialGuess;

  /** Maximum number of iterations of the optimization algorithm. */
  private final int maxIter;

  /**
   * Constructor used by the factory methods.
   *
   * @param initialGuess Initial guess.
   * @param maxIter Maximum number of iterations of the optimization algorithm.
   * @throws MathRuntimeException if {@code initialGuess} is {@code null}.
   */
  private SumCurveFitter(ParametricUnivariateFunction modelFunction, double[] initialGuess,
      int maxIter) {
    this.modelFunction = modelFunction;
    this.initialGuess = initialGuess.clone();
    this.maxIter = maxIter;
  }

  /**
   * Creates a default curve fitter. Zero will be used as initial guess for the coefficients, and
   * the maximum number of iterations of the optimization algorithm is set to
   * {@link Integer#MAX_VALUE}.
   * 
   * @param modelFunction the model function to fit.
   * @param initialGuess new start point (initial guess)
   * @param maxIter maximum number of iterations of the fitting algorithm.
   * @return a new instance.
   */
  public static SumCurveFitter create(ParametricUnivariateFunction modelFunction,
      double[] initialGuess, int maxIter) {
    return new SumCurveFitter(modelFunction, initialGuess, maxIter);
  }

  /** {@inheritDoc} */
  @Override
  protected LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> observations) {
    // Prepare least-squares problem.
    final int len = observations.size();
    final double[] target = new double[len];
    final double[] weights = new double[len];

    int i = 0;
    for (WeightedObservedPoint obs : observations) {
      target[i] = obs.getY();
      weights[i] = obs.getWeight();
      ++i;
    }

    final AbstractCurveFitter.TheoreticalValuesFunction model =
        new AbstractCurveFitter.TheoreticalValuesFunction(modelFunction, observations);

    if (initialGuess == null) {
      throw MathRuntimeException.createInternalError();
    }

    // Return a new least squares problem set up to fit a polynomial curve to the
    // observed points.
    return new LeastSquaresBuilder().maxEvaluations(Integer.MAX_VALUE).maxIterations(maxIter)
        .start(initialGuess).target(target).weight(new DiagonalMatrix(weights))
        .model(model.getModelFunction(), model.getModelFunctionJacobian()).build();

  }

}
