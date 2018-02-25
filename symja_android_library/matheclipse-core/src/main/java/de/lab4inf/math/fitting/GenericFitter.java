/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package de.lab4inf.math.fitting;

import de.lab4inf.math.Function;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.Solver;
import de.lab4inf.math.differentiation.Gradient;
import de.lab4inf.math.differentiation.GradientApproximator;
import de.lab4inf.math.differentiation.Hessian;
import de.lab4inf.math.differentiation.HessianApproximator;
import de.lab4inf.math.gof.Visitor;

import static de.lab4inf.math.blas.Blas1.daxpy;
import static de.lab4inf.math.extrema.GoldenSearch.minimum;
import static de.lab4inf.math.lapack.LinearAlgebra.add;
import static de.lab4inf.math.lapack.LinearAlgebra.copy;
import static de.lab4inf.math.lapack.LinearAlgebra.diff;
import static de.lab4inf.math.lapack.LinearAlgebra.identity;
import static de.lab4inf.math.lapack.LinearAlgebra.mult;
import static de.lab4inf.math.lapack.LinearAlgebra.norm;
import static de.lab4inf.math.lapack.LinearAlgebra.sub;
import static de.lab4inf.math.util.Accuracy.hasConverged;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;

/**
 * The basic algorithm to fit (x,y) data tuples to a model function f(x;a)
 * with an array of free parameters a_0...a_n. Using a pure chi-square fit
 * without errors of the data tuples:
 * <pre>
 *      chi2(a) = 1/2 sum || y - f(x;a)||**2 = min
 * </pre>
 * or using Pearson's chi-square assuming binned data with a variance of
 * sqrt(y(x;a)) within each bin:
 * <pre>
 *      chi2(a) = 1/2 sum || y - f(x;a)||**2/f(x;a) = min
 * </pre>
 * If the errors/variance dy are given also a fit of the form
 * <pre>
 *      chi2(a) = 1/2 sum || (y - f(x;a))/dy(x;a)||**2 = min
 * </pre>
 * is provided.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: GenericFitter.java,v 1.60 2014/12/12 14:12:48 nwulff Exp $
 * @since 10.10.2006
 */
public abstract class GenericFitter extends L4MObject implements DataFitter {
    protected static final String NOT_IMPLEMENTED = "not implemented yet";
    /**
     * reference to the F_IS_ZERO attribute.
     */
    private static final String F_IS_ZERO = "fit-function(%f) == 0";
    private static final String BAD_CHI2 = "Chi2(%d):%g <= Chi2(%d):%g";
    /**
     * maximal number of iteration steps.
     */
    private static final int MAX_ITERATIONS = 500;
    /**
     * maximal number internal marquardt updates.
     */
    private static final int MAX_MARQUARDTS = 50;
    /**
     * the penalty factor to start with.
     */
    private static final int DEFAULT_PENALTY = 10;
    protected double[] a;
    protected Solver laSolver;
    /**
     * accuracy to reach.
     */
    private double eps = 0.0005;
    private int numParams = -1;
    private double penaltyValue = DEFAULT_PENALTY;
    private boolean usePenalty = true;
    private boolean usePearson = false;
    private boolean useLinear = false;
    private boolean approximate = true;
    private boolean debug = false;
    private boolean newton = false;
    private boolean shouldThrowSingular = true;

    /**
     * Constructor initializing a parameter array of size n on the stack.
     *
     * @param n the parameter size
     */
    protected GenericFitter(final int n) {
        if (n <= 0) {
            throw new IllegalArgumentException(format("paramter size %d illegal", n));
        }
        this.numParams = n;
        this.a = new double[n];
        laSolver = resolve(Solver.class);
        laSolver.setShouldThrowSingular(shouldThrowSingular);
    }

    /**
     * Internal check, that the x and y array have the same dimension.
     *
     * @param x first array to compare with
     * @param y second array
     */
    protected static void dimensionCheck(final double[] x, final double[] y) {
        final String fmt = "|x|=%d != |y|=%d";
        final int n = x.length;
        final int m = y.length;
        if (n != m) {
            throw new IllegalArgumentException(format(fmt, n, m));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.DataFitter#clear()
     */
    public void clear() {
        // nothing to do for this generic fit.
    }

    /**
     * The number of parameters.
     *
     * @return int the parameter count
     */
    public final int numParameters() {
        return numParams;
    }

    /**
     * Get the desired accuracy for the approximation.
     *
     * @return the accuracy
     */
    public double getEps() {
        return eps;
    }

    /**
     * Set the desired accuracy for the approximation.
     *
     * @param accuracy to reach
     */
    @Override
    public void setEps(final double accuracy) {
        eps = accuracy;
    }

    /**
     * Get the shouldThrowSingular value.
     *
     * @return the shouldThrowSingular
     */
    public boolean isShouldThrowSingular() {
        return shouldThrowSingular;
    }

    /**
     * Set the shouldThrowSingular value.
     *
     * @param shouldThrowSingular the shouldThrowSingular to set
     */
    public void setShouldThrowSingular(final boolean shouldThrowSingular) {
        this.shouldThrowSingular = shouldThrowSingular;
        if (null != laSolver)
            laSolver.setShouldThrowSingular(shouldThrowSingular);

    }

    /**
     * Signal if derivatives are approximated.
     *
     * @return boolean
     */
    @Override
    public boolean isApproximate() {
        return approximate;
    }

    /**
     * Should the derivatives be approximated.
     *
     * @param approx signal if approximation needed
     */
    @Override
    public void setApproximate(final boolean approx) {
        this.approximate = approx;
    }

    /**
     * Getter for the debug variable.
     *
     * @return boolean
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Set the debug variable.
     *
     * @param debug debug to set
     */
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    /**
     * Get the Newton method flag.
     *
     * @return the Newton method flag
     */
    public boolean isNewton() {
        return newton;
    }

    /**
     * Indicate if Newton method should be used.
     * If not the default Marquardt method will
     * be used.
     *
     * @param newton if true will Newton will be used
     */
    public void setNewton(final boolean newton) {
        this.newton = newton;
    }

    /**
     * Get the value of the usePearson attribute.
     *
     * @return the usePearson
     */
    public boolean isUsePearson() {
        return usePearson;
    }

    /**
     * Set the value of the usePearson attribute.
     *
     * @param usePearson the flag to set
     */
    public void setUsePearson(final boolean usePearson) {
        this.usePearson = usePearson;
    }

    /**
     * Get the value of the useLinear attribute.
     *
     * @return the usePearson
     */
    public boolean isUseLinear() {
        return useLinear;
    }

    /**
     * Set the value of the useLinear attribute.
     *
     * @param linear the flag to set
     */
    public void setUseLinear(final boolean linear) {
        this.useLinear = linear;
    }

    /**
     * The array with the parameters a_0,...,a_n.
     *
     * @return array with the model parameters
     */
    @Override
    public final double[] getParameters() {
        return copy(a);
    }

    /**
     * Set the array with the parameters a_0,...,a_n.
     * Should only be used for test purposes.
     *
     * @param p array with the model parameters
     */
    protected final void setParameters(final double... p) {
        a = copy(p);
    }

    /**
     * Calculate the chi square between the data and the fit function.
     *
     * @param x double[] the x array
     * @param y double[] the y array
     * @return double the chi2 value
     */
    @Override
    public double chi2(final double[] x, final double[] y) {
        dimensionCheck(x, y);
        if (useLinear) {
            return chi2Linear(x, y);
        } else if (usePearson) {
            return chi2Pearson(x, y);
        }
        return chi2Bins(x, y);
    }

    /**
     * Calculate the chi square between the data and the fit function.
     *
     * @param x  double[] the x array
     * @param y  double[] the y array
     * @param dy the y errors
     * @return double the chi2 value
     */
    @Override
    public double chi2(final double[] x, final double[] y, final double[] dy) {
        dimensionCheck(x, y);
        dimensionCheck(x, dy);
        final int n = x.length;
        double rj, fj, sj, chi2 = 0;
        for (int j = 0; j < n; j++) {
            fj = fct(x[j]);
            rj = fj - y[j];
            sj = dy[j];
            sj *= sj;
            if (sj > 0) {
                chi2 += rj * rj / sj;
            }
        }
        return chi2 / 2;
    }

    /**
     * None linear fit with an variance proportional
     * to the number of entries within each bin.
     *
     * @param x double[] the x array
     * @param y double[] the y array
     * @return double the chi2 value
     */
    private double chi2Bins(final double[] x, final double[] y) {
        final int n = x.length;
        double fj, dy, chi2 = 0;
        for (int j = 0; j < n; j++) {
            fj = fct(x[j]);
            if (y[j] != 0) {
                dy = fj - y[j];
                chi2 += dy * dy / Math.abs(y[j]);
            }
        }
        return chi2 / 2;
    }

    /**
     * None linear fit with an variance proportional
     * to the model function within each bin - (Pearson's method).
     *
     * @param x double[] the x array
     * @param y double[] the y array
     * @return double the chi2 value
     */
    private double chi2Pearson(final double[] x, final double[] y) {
        final int n = x.length;
        double fj, dy, chi2 = 0;
        for (int j = 0; j < n; j++) {
            fj = fct(x[j]);
            if (fj != 0) {
                dy = fj - y[j];
                chi2 += dy * dy / Math.abs(fj);
            }
        }
        return chi2 / 2;
    }

    /**
     * Linear fit with an equal variance within each bin.
     *
     * @param x double[] the x array
     * @param y double[] the y array
     * @return double the chi2 value
     */
    private double chi2Linear(final double[] x, final double[] y) {
        final int n = x.length;
        double dy, chi2 = 0;
        for (int j = 0; j < n; j++) {
            dy = fct(x[j]) - y[j];
            chi2 += dy * dy;
        }
        return chi2 / 2;
    }

    /**
     * Return a fresh Chi2 handler.
     *
     * @param x the x values
     * @param y the y values
     * @return Chi2 handler with given data
     */
    protected Chi2 getChi2(final double[] x, final double[] y) {
        return new Chi2(x, y);
    }

    /**
     * Return a fresh Chi2 handler.
     *
     * @param x  the x values
     * @param y  the y values
     * @param dy the y errors
     * @return Chi2 handler with given data
     */
    protected Chi2 getChi2(final double[] x, final double[] y, final double[] dy) {
        return new Chi2(x, y, dy);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.DataFitter#fct(double)
     */
    @Override
    public abstract double fct(final double x);

    /**
     * The value of the derivative for the fit function y(x;a) with respect to
     * the parameter a_k.
     *
     * @param k index of parameter a_k
     * @param x the x value
     * @return dy/da_k
     */
    protected abstract double dFct(final int k, final double x);

    /**
     * The value of the second derivative for the fit function y(x;a) with
     * respect to the parameters a_k and a_l.
     *
     * @param k index of parameter a_k
     * @param l index of parameter a_l
     * @param x the x value
     * @return d2y/da_k da_l
     */
    protected abstract double ddFct(final int k, final int l, final double x);

    /**
     * Set the initial parameters (a) for the given data tuples (x,y).
     *
     * @param x double[] the x values
     * @param y double[] the y values
     */
    protected abstract void initParameters(final double[] x, final double[] y);

    /**
     * Set the initial parameters (a) for the given data tuples (x,y).
     *
     * @param x  double[] the x values
     * @param y  double[] the y values
     * @param dy double[] the approximate error bounds.
     */
    protected void initParameters(final double[] x, final double[] y, final double[] dy) {
        initParameters(x, y);
    }

    /**
     * The gradient vector of the chi2 function for the (x,y) values and the
     * actual parameters a.
     *
     * @param x double[] the x values
     * @param y double[] the y values
     * @return the gradient of chi2
     */
    protected double[] gradChi2(final double[] x, final double[] y) {
        if (useLinear) {
            return gradChi2Linear(x, y);
        } else if (usePearson) {
            return gradChi2Pearson(x, y);
        }
        return gradChi2Bins(x, y);
    }

    /**
     * The gradient vector of the chi2 function for the (x,y) values and the
     * actual parameters a.
     *
     * @param x  double[] the x values
     * @param y  double[] the y values
     * @param dy double[] the y errors
     * @return the gradient of chi2
     */
    protected double[] gradChi2(final double[] x, final double[] y, final double[] dy) {
        final int mMax = x.length, p = numParameters();
        double vm, xm, ym, fm;
        final double[] grad = new double[p];
        for (int m = 0; m < mMax; m++) {
            xm = x[m];
            ym = y[m];
            vm = dy[m];
            vm *= vm;
            fm = fct(xm);
            for (int k = 0; k < p; k++) {
                grad[k] += (fm - ym) * dFct(k, xm) / vm;
            }
        }
        return grad;
    }

    /**
     * Gradient of the chi square function for the Pearson fit.
     *
     * @param x the x values
     * @param y the y values
     * @return grad chi2
     */
    private double[] gradChi2Pearson(final double[] x, final double[] y) {
        final int mMax = x.length, p = numParameters();
        double xm, ym, fm, rm;
        final double[] grad = new double[p];
        for (int m = 0; m < mMax; m++) {
            xm = x[m];
            ym = y[m];
            fm = fct(xm);
            if (Math.abs(fm) > 0) {
                rm = ym / fm;
            } else {
                logger.info(String.format(F_IS_ZERO, xm));
                continue;
            }
            for (int k = 0; k < p; k++) {
                grad[k] += (1 - rm * rm) * dFct(k, xm);
            }
        }
        for (int k = 0; k < p; k++) {
            grad[k] *= 0.5;
        }
        return grad;
    }

    /**
     * Gradient of the chi square function for the linear fit.
     *
     * @param x the x values
     * @param y the y values
     * @return grad chi2
     */
    private double[] gradChi2Linear(final double[] x, final double[] y) {
        final int mMax = x.length, p = numParameters();
        double xm, ym, fm;
        final double[] grad = new double[p];
        for (int m = 0; m < mMax; m++) {
            xm = x[m];
            ym = y[m];
            fm = fct(xm);
            for (int k = 0; k < p; k++) {
                grad[k] += (fm - ym) * dFct(k, xm);
            }
        }
        return grad;
    }

    /**
     * Gradient of the chi square function with a variance equal to the bin
     * entries..
     *
     * @param x the x values
     * @param y the y values
     * @return grad chi2
     */
    private double[] gradChi2Bins(final double[] x, final double[] y) {
        final int mMax = x.length, p = numParameters();
        double xm, ym, fm;
        final double[] grad = new double[p];
        for (int m = 0; m < mMax; m++) {
            xm = x[m];
            ym = y[m];
            fm = fct(xm);
            if (ym != 0) {
                for (int k = 0; k < p; k++) {
                    grad[k] += (fm / ym - 1) * dFct(k, xm);
                }
            }
        }
        return grad;
    }

    /**
     * The hessian matrix of the chi2 function for the (x,y) values and the actual
     * parameters a.
     *
     * @param x double[] the x values
     * @param y double[] the y values
     * @return the hessian matrix of chi2
     */
    protected double[][] hessChi2(final double[] x, final double[] y) {
        if (useLinear) {
            return hessChi2Linear(x, y);
        } else if (usePearson) {
            return hessChi2Pearson(x, y);
        }
        return hessChi2Bins(x, y);
    }

    protected double[][] hessChi2(final double[] x, final double[] y, final double[] dy) {
        int j, k, m;
        final int mMax = x.length, p = numParameters();
        double vm, xm, ym, fm, dFk, dFj;
        final double[][] hess = new double[p][p];
        for (m = 0; m < mMax; m++) {
            xm = x[m];
            fm = fct(xm);
            ym = y[m];
            if (Math.abs(fm) <= 0 && Math.abs(ym) > 0) {
                logger.error(String.format(F_IS_ZERO, xm));
            }
            vm = dy[m];
            vm *= vm;
            for (k = 0; k < p; k++) {
                dFk = dFct(k, xm);
                for (j = 0; j <= k; j++) {
                    dFj = dFct(j, xm);
                    hess[j][k] += (fm - ym) * ddFct(j, k, xm) / vm + dFj * dFk / vm;
                    hess[k][j] = hess[j][k];
                }
            }
        }
        return hess;
    }

    private double[][] hessChi2Pearson(final double[] x, final double[] y) {
        int j, k, m;
        final int mMax = x.length, p = numParameters();
        double xm, rm, fm, dFk, dFj;
        final double[][] hess = new double[p][p];
        for (m = 0; m < mMax; m++) {
            xm = x[m];
            fm = fct(xm);
            if (Math.abs(fm) <= 0) {
                if (Math.abs(y[m]) <= 0)
                    logger.error(String.format(F_IS_ZERO, xm));
                continue;
            }
            rm = y[m] / fm;
            for (k = 0; k < p; k++) {
                dFk = dFct(k, xm);
                for (j = 0; j <= k; j++) {
                    dFj = dFct(j, xm);
                    hess[j][k] += (1 - rm * rm) * ddFct(j, k, xm) / 2 + rm * rm / fm * dFj * dFk;
                    hess[k][j] = hess[j][k];
                }
            }
        }
        return hess;
    }

    private double[][] hessChi2Linear(final double[] x, final double[] y) {
        int j, k, m;
        final int mMax = x.length, p = numParameters();
        double xm, rm, dFk, dFj;
        final double[][] hess = new double[p][p];
        for (m = 0; m < mMax; m++) {
            xm = x[m];
            rm = fct(xm) - y[m];
            for (k = 0; k < p; k++) {
                dFk = dFct(k, xm);
                for (j = 0; j <= k; j++) {
                    dFj = dFct(j, xm);
                    hess[j][k] += rm * ddFct(j, k, xm) + dFj * dFk;
                    hess[k][j] = hess[j][k];
                }
            }
        }
        return hess;
    }

    private double[][] hessChi2Bins(final double[] x, final double[] y) {
        int j, k, m;
        final int mMax = x.length, p = numParameters();
        double xm, ym, dFk, dFj;
        final double[][] hess = new double[p][p];
        for (m = 0; m < mMax; m++) {
            xm = x[m];
            ym = y[m];
            if (ym != 0) {
                for (k = 0; k < p; k++) {
                    dFk = dFct(k, xm);
                    for (j = 0; j <= k; j++) {
                        dFj = dFct(j, xm);
                        hess[j][k] += ((fct(xm) - ym) * ddFct(j, k, xm) + dFj * dFk) / ym;
                        hess[k][j] = hess[j][k];
                    }
                }
            }
        }
        return hess;
    }

    /**
     * Fit the model parameters to the data values.
     *
     * @param x double[] the x values
     * @param y double[] the y values
     * @return double[] the fitted model parameters
     */
    @Override
    public double[] fitt(final double[] x, final double[] y) {
        dimensionCheck(x, y);
        penaltyValue = DEFAULT_PENALTY;
        initParameters(x, y);
        fittParameters(x, y);
        return getParameters();
    }

    @Override
    public double[] fitt(final double[] x, final double[] y, final double[] dy) {
        dimensionCheck(x, y);
        dimensionCheck(x, dy);
        penaltyValue = DEFAULT_PENALTY;
        initParameters(x, y, dy);
        fittParameters(x, y, dy);
        return getParameters();

    }

    /**
     * Internal print method for debugging.
     *
     * @param n     the iteration counter
     * @param x     the x values
     * @param y     the y values
     * @param delta the norm||a_n - a_n-1||
     */
    protected void printFit(final int n, final double[] x, final double[] y, final double delta) {
        // dimensionCheck(x, y);
        final int p = numParameters();
        final StringBuffer b = new StringBuffer(format("Ite:%2d ", n));
        for (int i = 0; i < p; i++) {
            b.append(format("a%d=", i));
            b.append(format("%-8.2g ", a[i]));
        }
        b.append(format(" chi2: %8.2g", chi2(x, y)));
        b.append(format(" delta: %8.2g", delta));
        if (isUsePenalty()) {
            b.append(format(" penalty: %.0f", penaltyValue));
        }
        logger.info(b.toString());
    }

    /**
     * Fit the model parameters to the (x,y) data.
     *
     * @param x double[] the x values
     * @param y double[] the y values
     */
    protected void fittParameters(final double[] x, final double[] y) {
        final double[] dy = new double[0];
        fittParameters(x, y, dy);
    }

    /**
     * Fit the model parameters to the (x,y) data.
     *
     * @param x  double[] the x values
     * @param y  double[] the y values
     * @param dy double[] the y errors
     */
    protected void fittParameters(final double[] x, final double[] y, final double[] dy) {
        if (newton) {
            fittNewton(x, y, dy);
        } else {
            fittMarquardt(x, y, dy);
        }
    }

    /**
     * Calculate an analytical or approximate gradient vector of Chi2(fit).
     *
     * @param fit the actual function fit parameters
     * @param x   the x data
     * @param y   the y data
     * @param dy  the (optinal) y errors
     * @return gradient of Chi2 with respect to fit parameters
     */
    protected double[] calculateGradient(final double[] fit, final double[] x, final double[] y, final double[] dy) {
        double[] gradF;
        if (isApproximate()) {
            final Chi2 chi2 = new Chi2(x, y, dy);
            // create an approximate gradient vector
            final Gradient gradient = new GradientApproximator(chi2);
            gradF = gradient.gradient(fit);
        } else {
            // take the analytical provided values
            if (null == dy || dy.length == 0) {
                gradF = gradChi2(x, y);
            } else {
                gradF = gradChi2(x, y, dy);
            }
        }
        return gradF;
    }

    /**
     * Calculate an analytical or approximate Hessian matrix of Chi2(fit).
     *
     * @param fit the actual function fit parameters
     * @param x   the x data
     * @param y   the y data
     * @param dy  the (optinal) y errors
     * @return hessian for Chi2 with respect to fit parameters
     */
    protected double[][] calculateHessian(final double[] fit, final double[] x, final double[] y, final double[] dy) {
        double[][] hessF;
        if (isApproximate()) {
            final Chi2 chi2 = new Chi2(x, y, dy);
            // create an approximate hesse-matrix
            final Hessian hessian = new HessianApproximator(chi2);
            hessF = hessian.hessian(fit);
        } else {
            // take the analytical provided values
            if (null == dy || dy.length == 0) {
                hessF = hessChi2(x, y);
            } else {
                hessF = hessChi2(x, y, dy);
            }
        }
        return hessF;
    }

    /**
     * Fit the model parameters to the (x,y) data using the
     * Newton method.
     *
     * @param x  double[] the x values
     * @param y  double[] the y values
     * @param dy double[] the y errors (optional, maybe NP)
     */
    protected void fittNewton(final double[] x, final double[] y, final double[] dy) {
        final FittOptimizer opt = new FittOptimizer(x, y, dy);
        double delta, chiA, chiB, lambda, lmin, lmax;
        double[] da, ao;
        double[] gradF; // storage for the gradient of chi2
        double[][] hessF; // storage hesse-matrix of chi2
        int n = 0; // the iteration counter
        chiA = chi2(x, y);
        lambda = 0.05;
        do {
            // remember old value to compare to
            ao = copy(a);
            gradF = calculateGradient(a, x, y, dy);
            hessF = calculateHessian(a, x, y, dy);
            // solve the equation HessF(a)*da = GradF(a)
            da = laSolver.solveSymmetric(hessF, gradF);
            opt.setDG(da);
            // we have the direction now comes the scale
            lmin = max(0, lambda / 10);
            lmax = min(1, lambda * 5);
            lambda = minimum(opt, lmin, lmax, (lmax - lmin) / 15);
            // a = sub(a, mult(da, lamda));
            daxpy(-lambda, da, a);
            delta = norm(da);
            chiB = chi2(x, y);
            if (chiA < chiB) {
                logger.error(format(BAD_CHI2, n, chiA, n, chiB));
                // throw new ArithmeticException("leaving solution space");
                return;
            }
            chiA = chiB;
            if (isDebug()) {
                printFit(n, x, y, delta);
            }
            // We reduce the penalty function
            // as soon as we begin to converge.
            penaltyUpdate(ao, a);
        } while (!hasConverged(a, ao, eps, ++n, MAX_ITERATIONS));
    }

    /**
     * Fit the model parameters to the (x,y) data using the
     * Marquardt method.
     *
     * @param x  double[] the x values
     * @param y  double[] the y values
     * @param dy double[] the y errors  (optional, maybe NP)
     */
    protected void fittMarquardt(final double[] x, final double[] y, final double[] dy) {
        double lamda = 0.01;
        final double nu = 5;
        double delta, chi0, chi1, chi2;
        double[] da, ao, a1, a2;
        double[] gradF; // storage for the gradient of chi2
        double[][] hessF; // storage hesse-matrix of chi2
        double[][] mat; // storage marquardt-matrix
        double[][] id; // storage identity-matrix
        int m, n = 0; // the iteration counter
        id = identity(a.length);
        chi0 = chi2(x, y);
        do {
            // remember old value to compare to
            ao = copy(a);
            gradF = calculateGradient(a, x, y, dy);
            hessF = calculateHessian(a, x, y, dy);
            // solve the equation (HessF(a)+lamda*Id)*da = GradF(a)
            mat = add(hessF, mult(id, lamda));
            da = laSolver.solveSymmetric(mat, gradF);
            a1 = sub(a, da);
            a = a1;
            chi1 = chi2(x, y);
            a = ao;
            m = 0;
            do {
                mat = add(hessF, mult(id, lamda / nu));
                da = laSolver.solveSymmetric(mat, gradF);
                a2 = sub(a, da);
                a = a2;
                chi2 = chi2(x, y);
                a = ao;
                if (chi2 <= chi0) {
                    chi1 = chi2;
                    a = a2;
                    lamda /= nu;
                } else if (chi1 < chi0) {
                    a = a1;
                    break;
                } else {
                    lamda *= nu;
                }
            } while (chi1 > chi0 && ++m < MAX_MARQUARDTS);
            if (m >= MAX_MARQUARDTS) {
                final String msg = format("fitt error at iteration %d", n);
                final IllegalStateException error = new IllegalStateException(msg);
                logger.error(msg, error);
                throw error;
            }
            chi0 = chi1;
            if (isDebug()) {
                delta = diff(a, ao);
                printFit(n, x, y, delta);
            }
            // We reduce the penalty function
            // as soon as we begin to converge.
            penaltyUpdate(ao, a);
        } while (!hasConverged(a, ao, eps, ++n, MAX_ITERATIONS));
    }

    ;

    /**
     * If the algorithm begins to converge the penalty factor is reduced.
     *
     * @param aOld the old parameter values
     * @param aNew the new parameter values
     */
    private void penaltyUpdate(final double[] aOld, final double[] aNew) {
        if (isUsePenalty()) {
            final double delta = diff(aNew, aOld);
            if (delta < eps * 30) {
                double p = getPenaltyValue();
                p *= 1.5;
                setPenaltyValue(p);
            }
        }
    }

    /**
     * Get the usePenalty value.
     *
     * @return the usePenalty
     */
    public boolean isUsePenalty() {
        return usePenalty;
    }

    /**
     * Set the usePenalty value.
     *
     * @param usePenalty the usePenalty to set
     */
    public void setUsePenalty(final boolean usePenalty) {
        this.usePenalty = usePenalty;
    }

    /**
     * Get the penaltyValue value.
     *
     * @return the penaltyValue
     */
    public double getPenaltyValue() {
        return penaltyValue;
    }

    /**
     * Set the penaltyValue value.
     *
     * @param penaltyValue the penaltyValue to set
     */
    public void setPenaltyValue(final double penaltyValue) {
        this.penaltyValue = penaltyValue;
    }

    /**
     * Internal chi2 function, parameterized with
     * the (x,y)-tuples.
     */
    protected class Chi2 implements Function {
        private final double[] x, y, dy;

        /**
         * Parameterized constructor.
         *
         * @param x the arguments
         * @param y the function values to fit for
         */
        public Chi2(final double[] x, final double[] y) {
            this(x, y, null);
        }

        /**
         * Parameterized constructor.
         *
         * @param x  the arguments
         * @param y  the function values to fit for
         * @param dy the errors of the y values
         */
        public Chi2(final double[] x, final double[] y, final double[] dy) {
            this.x = copy(x);
            this.y = copy(y);
            this.dy = copy(dy);
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... z) {
            a = z;
            if (dy == null || dy.length == 0) {
                return chi2(x, y);
            }
            return chi2(x, y, dy);
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
         */
        @Override
        public void accept(final Visitor<Function> visitor) {
            visitor.visit(this);
        }
    }

    /**
     * Internal helper class to optimize the step width for
     * a newton or gradient method.
     */
    private class FittOptimizer implements Function {
        private final double[] x, y, dy;
        private double[] dg;

        /**
         * Constructor with arguments.
         *
         * @param x  data tuples
         * @param y  data tuples
         * @param dy data error of y (optional)
         */
        FittOptimizer(final double[] x, final double[] y, final double[] dy) {
            this(calculateGradient(getParameters(), x, y, dy), x, y, dy);
        }

        /**
         * Constructor with arguments.
         *
         * @param dg the gradient or step direction
         * @param x  data tuples
         * @param y  data tuples
         * @param dy data error of y (optional)
         */
        FittOptimizer(final double[] dg, final double[] x, final double[] y, final double[] dy) {
            this.dg = dg;
            this.x = x;
            this.y = y;
            this.dy = dy;
        }

        /**
         * Change the gradient direction.
         *
         * @param g gradient/update direction
         */
        void setDG(final double[] g) {
            this.dg = g;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... lambda) {
            double ret = 0;
            final double[] b = copy(a); // save the original
            // double[] g = sub(a, mult(dg, lamda[0]));
            try {
                daxpy(-lambda[0], dg, a);
                if (null == dy || dy.length == 0) {
                    ret = chi2(x, y);
                } else {
                    ret = chi2(x, y, dy);
                }
            } catch (final Exception e) {
                getLogger().warn(e);
                ret = Double.MAX_VALUE;
            } finally {
                a = b; // restore the original
            }
            // if (Double.isInfinite(ret) || Double.isNaN(ret)) {
            // ret = Double.MAX_VALUE;
            // }
            ret = Math.abs(ret);
            return ret;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
         */
        @Override
        public void accept(final Visitor<Function> visitor) {
            visitor.visit(this);
        }
    }
}
 