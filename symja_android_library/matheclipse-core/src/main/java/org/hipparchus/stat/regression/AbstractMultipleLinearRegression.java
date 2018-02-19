/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hipparchus.stat.regression;

import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.NullArgumentException;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.hipparchus.stat.descriptive.moment.Variance;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;

/**
 * Abstract base class for implementations of MultipleLinearRegression.
 */
public abstract class AbstractMultipleLinearRegression implements
        MultipleLinearRegression {

    /**
     * X sample data.
     */
    private RealMatrix xMatrix;

    /**
     * Y sample data.
     */
    private RealVector yVector;

    /**
     * Whether or not the regression model includes an intercept.  True means no intercept.
     */
    private boolean noIntercept = false;

    /**
     * @return the X sample data.
     */
    protected RealMatrix getX() {
        return xMatrix;
    }

    /**
     * @return the Y sample data.
     */
    protected RealVector getY() {
        return yVector;
    }

    /**
     * @return true if the model has no intercept term; false otherwise
     */
    public boolean isNoIntercept() {
        return noIntercept;
    }

    /**
     * @param noIntercept true means the model is to be estimated without an intercept term
     */
    public void setNoIntercept(boolean noIntercept) {
        this.noIntercept = noIntercept;
    }

    /**
     * <p>Loads model x and y sample data from a flat input array, overriding any previous sample.
     * </p>
     * <p>Assumes that rows are concatenated with y values first in each row.  For example, an input
     * <code>data</code> array containing the sequence of values (1, 2, 3, 4, 5, 6, 7, 8, 9) with
     * <code>nobs = 3</code> and <code>nvars = 2</code> creates a regression dataset with two
     * independent variables, as below:
     * <pre>
     *   y   x[0]  x[1]
     *   --------------
     *   1     2     3
     *   4     5     6
     *   7     8     9
     * </pre>
     * </p>
     * <p>Note that there is no need to add an initial unitary column (column of 1's) when
     * specifying a model including an intercept term.  If {@link #isNoIntercept()} is <code>true</code>,
     * the X matrix will be created without an initial column of "1"s; otherwise this column will
     * be added.
     * </p>
     * <p>Throws IllegalArgumentException if any of the following preconditions fail:
     * <ul><li><code>data</code> cannot be null</li>
     * <li><code>data.length = nobs * (nvars + 1)</li>
     * <li><code>nobs > nvars</code></li></ul>
     * </p>
     *
     * @param data  input data array
     * @param nobs  number of observations (rows)
     * @param nvars number of independent variables (columns, not counting y)
     * @throws NullArgumentException        if the data array is null
     * @throws MathIllegalArgumentException if the length of the data array is not equal
     *                                      to <code>nobs * (nvars + 1)</code>
     * @throws MathIllegalArgumentException if <code>nobs</code> is less than
     *                                      <code>nvars + 1</code>
     */
    public void newSampleData(double[] data, int nobs, int nvars) {
        MathUtils.checkNotNull(data, LocalizedCoreFormats.INPUT_ARRAY);
        MathUtils.checkDimension(data.length, nobs * (nvars + 1));
        if (nobs <= nvars) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE,
                    nobs, nvars + 1);
        }
        double[] y = new double[nobs];
        final int cols = noIntercept ? nvars : nvars + 1;
        double[][] x = new double[nobs][cols];
        int pointer = 0;
        for (int i = 0; i < nobs; i++) {
            y[i] = data[pointer++];
            if (!noIntercept) {
                x[i][0] = 1.0d;
            }
            for (int j = noIntercept ? 0 : 1; j < cols; j++) {
                x[i][j] = data[pointer++];
            }
        }
        this.xMatrix = new Array2DRowRealMatrix(x);
        this.yVector = new ArrayRealVector(y);
    }

    /**
     * Loads new y sample data, overriding any previous data.
     *
     * @param y the array representing the y sample
     * @throws NullArgumentException        if y is null
     * @throws MathIllegalArgumentException if y is empty
     */
    protected void newYSampleData(double[] y) {
        if (y == null) {
            throw new NullArgumentException();
        }
        if (y.length == 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NO_DATA);
        }
        this.yVector = new ArrayRealVector(y);
    }

    /**
     * <p>Loads new x sample data, overriding any previous data.
     * </p>
     * The input <code>x</code> array should have one row for each sample
     * observation, with columns corresponding to independent variables.
     * For example, if <pre>
     * <code> x = new double[][] {{1, 2}, {3, 4}, {5, 6}} </code></pre>
     * then <code>setXSampleData(x) </code> results in a model with two independent
     * variables and 3 observations:
     * <pre>
     *   x[0]  x[1]
     *   ----------
     *     1    2
     *     3    4
     *     5    6
     * </pre>
     * </p>
     * <p>Note that there is no need to add an initial unitary column (column of 1's) when
     * specifying a model including an intercept term.
     * </p>
     *
     * @param x the rectangular array representing the x sample
     * @throws NullArgumentException        if x is null
     * @throws MathIllegalArgumentException if x is empty
     * @throws MathIllegalArgumentException if x is not rectangular
     */
    protected void newXSampleData(double[][] x) {
        if (x == null) {
            throw new NullArgumentException();
        }
        if (x.length == 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NO_DATA);
        }
        if (noIntercept) {
            this.xMatrix = new Array2DRowRealMatrix(x, true);
        } else { // Augment design matrix with initial unitary column
            final int nVars = x[0].length;
            final double[][] xAug = new double[x.length][nVars + 1];
            for (int i = 0; i < x.length; i++) {
                MathUtils.checkDimension(x[i].length, nVars);
                xAug[i][0] = 1.0d;
                System.arraycopy(x[i], 0, xAug[i], 1, nVars);
            }
            this.xMatrix = new Array2DRowRealMatrix(xAug, false);
        }
    }

    /**
     * Validates sample data.  Checks that
     * <ul><li>Neither x nor y is null or empty;</li>
     * <li>The length (i.e. number of rows) of x equals the length of y</li>
     * <li>x has at least one more row than it has columns (i.e. there is
     * sufficient data to estimate regression coefficients for each of the
     * columns in x plus an intercept.</li>
     * </ul>
     *
     * @param x the [n,k] array representing the x data
     * @param y the [n,1] array representing the y data
     * @throws NullArgumentException        if {@code x} or {@code y} is null
     * @throws MathIllegalArgumentException if {@code x} and {@code y} do not
     *                                      have the same length
     * @throws MathIllegalArgumentException if {@code x} or {@code y} are zero-length
     * @throws MathIllegalArgumentException if the number of rows of {@code x}
     *                                      is not larger than the number of columns + 1 if the model has an intercept;
     *                                      or the number of columns if there is no intercept term
     */
    protected void validateSampleData(double[][] x, double[] y) throws MathIllegalArgumentException {
        if ((x == null) || (y == null)) {
            throw new NullArgumentException();
        }
        MathUtils.checkDimension(x.length, y.length);
        if (x.length == 0) {  // Must be no y data either
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NO_DATA);
        }
        if (x[0].length + (noIntercept ? 0 : 1) > x.length) {
            throw new MathIllegalArgumentException(
                    LocalizedCoreFormats.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS,
                    x.length, x[0].length);
        }
    }

    /**
     * Validates that the x data and covariance matrix have the same
     * number of rows and that the covariance matrix is square.
     *
     * @param x          the [n,k] array representing the x sample
     * @param covariance the [n,n] array representing the covariance matrix
     * @throws MathIllegalArgumentException if the number of rows in x is not equal
     *                                      to the number of rows in covariance
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
     * {@inheritDoc}
     */
    @Override
    public double[] estimateRegressionParameters() {
        RealVector b = calculateBeta();
        return b.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] estimateResiduals() {
        RealVector b = calculateBeta();
        RealVector e = yVector.subtract(xMatrix.operate(b));
        return e.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[][] estimateRegressionParametersVariance() {
        return calculateBetaVariance().getData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] estimateRegressionParametersStandardErrors() {
        double[][] betaVariance = estimateRegressionParametersVariance();
        double sigma = calculateErrorVariance();
        int length = betaVariance[0].length;
        double[] result = new double[length];
        for (int i = 0; i < length; i++) {
            result[i] = FastMath.sqrt(sigma * betaVariance[i][i]);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double estimateRegressandVariance() {
        return calculateYVariance();
    }

    /**
     * Estimates the variance of the error.
     *
     * @return estimate of the error variance
     */
    public double estimateErrorVariance() {
        return calculateErrorVariance();

    }

    /**
     * Estimates the standard error of the regression.
     *
     * @return regression standard error
     */
    public double estimateRegressionStandardError() {
        return FastMath.sqrt(estimateErrorVariance());
    }

    /**
     * Calculates the beta of multiple linear regression in matrix notation.
     *
     * @return beta
     */
    protected abstract RealVector calculateBeta();

    /**
     * Calculates the beta variance of multiple linear regression in matrix
     * notation.
     *
     * @return beta variance
     */
    protected abstract RealMatrix calculateBetaVariance();


    /**
     * Calculates the variance of the y values.
     *
     * @return Y variance
     */
    protected double calculateYVariance() {
        return new Variance().evaluate(yVector.toArray());
    }

    /**
     * <p>Calculates the variance of the error term.</p>
     * Uses the formula <pre>
     * var(u) = u &middot; u / (n - k)
     * </pre>
     * where n and k are the row and column dimensions of the design
     * matrix X.
     *
     * @return error variance estimate
     */
    protected double calculateErrorVariance() {
        RealVector residuals = calculateResiduals();
        return residuals.dotProduct(residuals) /
                (xMatrix.getRowDimension() - xMatrix.getColumnDimension());
    }

    /**
     * Calculates the residuals of multiple linear regression in matrix
     * notation.
     * <p>
     * <pre>
     * u = y - X * b
     * </pre>
     *
     * @return The residuals [n,1] matrix
     */
    protected RealVector calculateResiduals() {
        RealVector b = calculateBeta();
        return yVector.subtract(xMatrix.operate(b));
    }

}
