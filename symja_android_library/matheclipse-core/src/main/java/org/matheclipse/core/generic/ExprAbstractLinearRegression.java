package org.matheclipse.core.generic;


import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.NullArgumentException;
import org.hipparchus.linear.Array2DRowFieldMatrix;
import org.hipparchus.linear.ArrayFieldVector;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.FieldVector;
import org.hipparchus.stat.LocalizedStatFormats;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Abstract base class for implementations of {@link IExprLinearRegression}
 */
public abstract class ExprAbstractLinearRegression { // implements IExprLinearRegression {

  /** X sample data. */
  private FieldMatrix<IExpr> xMatrix;

  /** Y sample data. */
  private FieldVector<IExpr> yVector;

  /** Whether or not the regression model includes an intercept. True means no intercept. */
  private boolean noIntercept;

  /**
   * Empty constructor.
   * <p>
   * This constructor is not strictly necessary, but it prevents spurious javadoc warnings with JDK
   * 18 and later.
   * </p>
   * 
   * @since 3.0
   */
  protected ExprAbstractLinearRegression() { // NOPMD - unnecessary constructor added
                                                 // intentionally to make javadoc happy
    // nothing to do
  }

  /**
   * Get the X sample data.
   * 
   * @return the X sample data.
   */
  protected FieldMatrix<IExpr> getX() {
    return xMatrix;
  }

  /**
   * Get the Y sample data.
   * 
   * @return the Y sample data.
   */
  protected FieldVector<IExpr> getY() {
    return yVector;
  }

  /**
   * Chekc if the model has no intercept term.
   * 
   * @return true if the model has no intercept term; false otherwise
   */
  public boolean isNoIntercept() {
    return noIntercept;
  }

  /**
   * Set intercept flag.
   * 
   * @param noIntercept true means the model is to be estimated without an intercept term
   */
  public void setNoIntercept(boolean noIntercept) {
    this.noIntercept = noIntercept;
  }

  /**
   * <p>
   * Loads model x and y sample data from a flat input array, overriding any previous sample.
   * </p>
   * <p>
   * Assumes that rows are concatenated with y values first in each row. For example, an input
   * <code>data</code> array containing the sequence of values (1, 2, 3, 4, 5, 6, 7, 8, 9) with
   * <code>nobs = 3</code> and <code>nvars = 2</code> creates a regression dataset with two
   * independent variables, as below:
   * </p>
   * 
   * <pre>
   *   y   x[0]  x[1]
   *   --------------
   *   1     2     3
   *   4     5     6
   *   7     8     9
   * </pre>
   * <p>
   * Note that there is no need to add an initial unitary column (column of 1's) when specifying a
   * model including an intercept term. If {@link #isNoIntercept()} is <code>true</code>, the X
   * matrix will be created without an initial column of "1"s; otherwise this column will be added.
   * </p>
   * <p>
   * Throws IllegalArgumentException if any of the following preconditions fail:
   * </p>
   * <ul>
   * <li><code>data</code> cannot be null</li>
   * <li><code>data.length = nobs * (nvars + 1)</code></li>
   * <li><code>nobs &gt; nvars</code></li>
   * </ul>
   *
   * @param data input data array
   * @param nobs number of observations (rows)
   * @param nvars number of independent variables (columns, not counting y)
   * @throws NullArgumentException if the data array is null
   * @throws MathIllegalArgumentException if the length of the data array is not equal to
   *         <code>nobs * (nvars + 1)</code>
   * @throws MathIllegalArgumentException if <code>nobs</code> is less than <code>nvars + 1</code>
   */
  public void newSampleData(FieldMatrix<IExpr> data, int nobs, int nvars) {
    MathUtils.checkNotNull(data, LocalizedCoreFormats.INPUT_ARRAY);
    MathUtils.checkDimension(data.getRowDimension() * data.getColumnDimension(),
        nobs * (nvars + 1));
    if (nobs <= nvars) {
      throw new MathIllegalArgumentException(
          LocalizedCoreFormats.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, nobs, nvars + 1);
    }
    // double[] y=new double[nobs];
    FieldVector<IExpr> y = new ArrayFieldVector<IExpr>(F.EXPR_FIELD, nobs);
    final int cols = noIntercept ? nvars : nvars + 1;
    // double[][] x = new double[nobs][cols];
    FieldMatrix<IExpr> x = new Array2DRowFieldMatrix<IExpr>(F.EXPR_FIELD, nobs, cols);
    // int pointer = 0;
    for (int i = 0; i < nobs; i++) {
      // y[i] = data[pointer++];
      y.setEntry(i, data.getEntry(i, 0));
      if (!noIntercept) {
        // x[i][0] = 1.0d;
        x.setEntry(i, 0, F.C1);
      }
      for (int j = noIntercept ? 0 : 1; j < cols; j++) {
        // x[i][j] = data[pointer++];
        x.setEntry(i, j, data.getEntry(i, j));
      }
    }
    this.xMatrix = x;
    this.yVector = y;
  }

  /**
   * Loads new y sample data, overriding any previous data.
   *
   * @param y the array representing the y sample
   * @throws NullArgumentException if y is null
   * @throws MathIllegalArgumentException if y is empty
   */
  protected void newYSampleData(FieldVector<IExpr> y) {
    if (y == null) {
      throw new NullArgumentException();
    }
    if (y.getDimension() == 0) {
      throw new MathIllegalArgumentException(LocalizedCoreFormats.NO_DATA);
    }
    this.yVector = new ArrayFieldVector<IExpr>(y);
  }

  /**
   * <p>
   * Loads new x sample data, overriding any previous data.
   * </p>
   * <p>
   * The input <code>x</code> array should have one row for each sample observation, with columns
   * corresponding to independent variables. For example, if
   * </p>
   * 
   * <pre>
   * <code> x = new double[][] {{1, 2}, {3, 4}, {5, 6}} </code>
   * </pre>
   * <p>
   * then <code>setXSampleData(x) </code> results in a model with two independent variables and 3
   * observations:
   * </p>
   * 
   * <pre>
   *   x[0]  x[1]
   *   ----------
   *     1    2
   *     3    4
   *     5    6
   * </pre>
   * <p>
   * Note that there is no need to add an initial unitary column (column of 1's) when specifying a
   * model including an intercept term.
   * </p>
   * 
   * @param x the rectangular array representing the x sample
   * @throws NullArgumentException if x is null
   * @throws MathIllegalArgumentException if x is empty
   * @throws MathIllegalArgumentException if x is not rectangular
   */
  protected void newXSampleData(FieldMatrix<IExpr> x) {
    if (x == null) {
      throw new NullArgumentException();
    }
    if (x.getRowDimension() == 0 || x.getColumnDimension() == 0) {
      throw new MathIllegalArgumentException(LocalizedCoreFormats.NO_DATA);
    }
    if (noIntercept) {
      this.xMatrix = x.copy();
    } else { // Augment design matrix with initial unitary column
      final int nVars = x.getColumnDimension();
      // new double[x.length][nVars + 1];
      final FieldMatrix<IExpr> xAug =
          new Array2DRowFieldMatrix<IExpr>(F.EXPR_FIELD, x.getRowDimension(), nVars + 1);
      for (int i = 0; i < x.getRowDimension(); i++) {
        // MathUtils.checkDimension(x[i].length, nVars);
        // xAug[i][0] = 1.0d;
        IExpr[] array = new IExpr[nVars + 1];
        array[0] = F.C1;
        System.arraycopy(x.getRow(i), 0, array, 1, nVars);
        xAug.setColumn(i, array);
      }
      this.xMatrix = xAug;
    }
  }

  /**
   * Validates sample data.
   * <p>
   * Checks that
   * </p>
   * <ul>
   * <li>Neither x nor y is null or empty;</li>
   * <li>The length (i.e. number of rows) of x equals the length of y</li>
   * <li>x has at least one more row than it has columns (i.e. there is sufficient data to estimate
   * regression coefficients for each of the columns in x plus an intercept.</li>
   * </ul>
   *
   * @param x the [n,k] array representing the x data
   * @param y the [n,1] array representing the y data
   * @throws NullArgumentException if {@code x} or {@code y} is null
   * @throws MathIllegalArgumentException if {@code x} and {@code y} do not have the same length
   * @throws MathIllegalArgumentException if {@code x} or {@code y} are zero-length
   * @throws MathIllegalArgumentException if the number of rows of {@code x} is not larger than the
   *         number of columns + 1 if the model has an intercept; or the number of columns if there
   *         is no intercept term
   */
  protected void validateSampleData(FieldMatrix<IExpr> x, FieldVector<IExpr> y)
      throws MathIllegalArgumentException {
    if ((x == null) || (y == null)) {
      throw new NullArgumentException();
    }
    MathUtils.checkDimension(x.getRowDimension(), y.getDimension());
    if (x.getRowDimension() == 0) { // Must be no y data either
      throw new MathIllegalArgumentException(LocalizedCoreFormats.NO_DATA);
    }
    if (x.getColumnDimension() + (noIntercept ? 0 : 1) > x.getRowDimension()) {
      throw new MathIllegalArgumentException(
          LocalizedStatFormats.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, x.getRowDimension(),
          x.getColumnDimension());
    }
  }

  /**
   * Validates that the x data and covariance matrix have the same number of rows and that the
   * covariance matrix is square.
   *
   * @param x the [n,k] array representing the x sample
   * @param covariance the [n,n] array representing the covariance matrix
   * @throws MathIllegalArgumentException if the number of rows in x is not equal to the number of
   *         rows in covariance
   * @throws MathIllegalArgumentException if the covariance matrix is not square
   */
  protected void validateCovarianceData(double[][] x, double[][] covariance) {
    MathUtils.checkDimension(x.length, covariance.length);
    if (covariance.length > 0 && covariance.length != covariance[0].length) {
      throw new MathIllegalArgumentException(LocalizedCoreFormats.NON_SQUARE_MATRIX,
          covariance.length, covariance[0].length);
    }
  }

  /**
   * Estimates the regression parameters b.
   *
   * @return The [k,1] array representing b
   */
  public IExpr[] estimateRegressionParameters() {
    FieldVector<IExpr> b = calculateBeta();
    return b.toArray();
  }

  /**
   * Estimates the residuals, ie u = y - X*b.
   *
   * @return The [n,1] array representing the residuals
   */
  public IExpr[] estimateResiduals() {
    // RealVector b = calculateBeta();
    // RealVector e = yVector.subtract(xMatrix.operate(b));
    FieldVector<IExpr> b = calculateBeta();
    FieldVector<IExpr> e = yVector.subtract(xMatrix.operate(b));
    return e.toArray();
  }

  /**
   * Estimates the variance of the regression parameters, ie Var(b).
   *
   * @return The [k,k] array representing the variance of b
   */
  public IExpr[][] estimateRegressionParametersVariance() {
    return calculateBetaVariance().getData();
  }

  /**
   * Returns the standard errors of the regression parameters.
   *
   * @return standard errors of estimated regression parameters
   */
  public IExpr[] estimateRegressionParametersStandardErrors() {
    IExpr[][] betaVariance = estimateRegressionParametersVariance();
    IExpr sigma = calculateErrorVariance();
    int length = betaVariance[0].length;
    IExpr[] result = new IExpr[length];
    for (int i = 0; i < length; i++) {
      // result[i] = FastMath.sqrt(sigma * betaVariance[i][i]);
      result[i] = sigma.multiply(betaVariance[i][i]).sqrt();
    }
    return result;
  }

  /**
   * Returns the variance of the regressand, ie Var(y).
   *
   * @return The double representing the variance of y
   */
  public IExpr estimateRegressandVariance() {
    return calculateYVariance();
  }

  /**
   * Estimates the variance of the error.
   *
   * @return estimate of the error variance
   */
  public IExpr estimateErrorVariance() {
    return calculateErrorVariance();

  }

  /**
   * Estimates the standard error of the regression.
   *
   * @return regression standard error
   */
  public IExpr estimateRegressionStandardError() {
    return FastMath.sqrt(estimateErrorVariance());
  }

  /**
   * Calculates the beta of multiple linear regression in matrix notation.
   *
   * @return beta
   */
  protected abstract FieldVector<IExpr> calculateBeta();

  /**
   * Calculates the beta variance of multiple linear regression in matrix notation.
   *
   * @return beta variance
   */
  protected abstract FieldMatrix<IExpr> calculateBetaVariance();


  /**
   * Calculates the variance of the y values.
   *
   * @return Y variance
   */
  protected IExpr calculateYVariance() {
    return EvalEngine.get().evaluate(F.Variance(F.List(yVector.toArray())));
  }

  /**
   * <p>
   * Calculates the variance of the error term.
   * </p>
   * Uses the formula
   * 
   * <pre>
   * var(u) = u &middot; u / (n - k)
   * </pre>
   * 
   * where n and k are the row and column dimensions of the design matrix X.
   *
   * @return error variance estimate
   */
  protected IExpr calculateErrorVariance() {
    FieldVector<IExpr> residuals = calculateResiduals();
    return residuals.dotProduct(residuals)
        .divide((xMatrix.getRowDimension() - xMatrix.getColumnDimension()));
  }

  /**
   * Calculates the residuals of multiple linear regression in matrix notation.
   *
   * <pre>
   * u = y - X * b
   * </pre>
   *
   * @return The residuals [n,1] matrix
   */
  protected FieldVector<IExpr> calculateResiduals() {
    FieldVector<IExpr> b = calculateBeta();
    return yVector.subtract(xMatrix.operate(b));
  }

}
