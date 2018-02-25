/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2014,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
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

import de.lab4inf.math.statistic.DataCollector3D;
import de.lab4inf.math.util.Accuracy;

/**
 * Fit the (x,y) tuples according to an algebraic circle equation.
 * <pre>
 * L. Kasa: A Circle Fitting Procedure and Its Error Analysis,
 *          IEEE Transactions on Instrumentation and Measurement, 1976.
 * </pre>
 * <p>
 * <pre>
 *          1  N
 *   chi2 = -  &sum;  (xj**2  +yj**2 + A*xj + B*yj + C )**2
 *          2  j=1
 * </pre>
 * <p>
 * where {A,B,C} are the three free parameter of the circle.
 * They are  related to the radius R and the (Xc,Yc)-center via:
 * <p>
 * <pre>
 *
 *  Xc = -A/2,   Yc = -B/2,  R**2 = (A**2 + B**2)/4 - C
 *
 * </pre>
 * As this parameterization depends only linear on A,B and C the data samples
 * can be evaluated once during initialization and the means and variances
 * are cached for later iterations.
 *
 * @author nwulff
 * @version $Id: KazaCircleFitter.java,v 1.9 2014/12/12 17:24:33 nwulff Exp $
 * @since 30.11.2014
 */
public class KazaCircleFitter extends CircleFitter {
    /* the cached sampled mean values. */
    private double[] sx;
    /* the cached sampled x*x or x*y-mean values. */
    private double[][] sxy;

    /**
     * Public POJO constructor.
     */
    public KazaCircleFitter() {
        super(3);
        setNewton(false);
        setApproximate(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.DataFitter#clear()
     */
    @Override
    public void clear() {
        sx = null;
        sxy = null;
    }

    /**
     * Calculate mean, variance and correlation coefficients for the data sample,
     * used within later iterative optimization steps.
     *
     * @param x data sample
     * @param y data sample
     * @param w data weights, i.e. the measurement errors
     */
    protected void initChi2(final double[] x, final double[] y, final double[] w) {
        final int n = x.length;
        double sig2 = 1, z2;
        DataCollector3D sampler = new DataCollector3D();
        for (int j = 0; j < n; j++) {
            if (w != null) {
                sig2 = 1 / (w[j] * w[j]);
            }
            z2 = (x[j] * x[j] + y[j] * y[j]);
            sampler.collect(x[j], y[j], z2, sig2);
        }
        sx = new double[3];
        sxy = new double[3][3];
        for (int j = 0; j < 3; j++) {
            sx[j] = sampler.getMean(j);
            for (int k = 0; k < 3; k++) {
                sxy[j][k] = sampler.getMean(j, k);
            }
        }
    }

    /**
     * Calculate mean, variance and correlation coefficients for the data sample,
     * used within iterative later optimisation steps.
     *
     * @param x data sample
     * @param y data sample
     */
    protected void initChi2(final double[] x, final double[] y) {
        initChi2(x, y, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.GenericFitter#initParameters(double[], double[], double[])
     */
    @Override
    protected void initParameters(final double[] x, final double[] y, final double[] dy) {
        final int n = x.length;
        double R2 = 0, Xc = 0, Yc = 0, X2 = 0, Y2 = 0;
        if (n < 3)
            throw new IllegalArgumentException("circle fit needs at least three data points");
        // if possible calculate initial guess from three distinct points.
        final double ax = x[0], ay = y[0], bx = x[n / 2], by = y[n / 2], cx = x[n - 1], cy = y[n - 1];
        final double det = (ax - bx) * (ay - cy) - (ax - cx) * (ay - by);
        initChi2(x, y, dy);
        if (Math.abs(det) > Accuracy.FEPS) {
            final double[][] A = {{ax - bx, ay - by}, {ax - cx, ay - cy}};
            final double[] v = {ax * ax - bx * bx + ay * ay - by * by, ax * ax - cx * cx + ay * ay - cy * cy};
            final double[] d = laSolver.solve(A, v);
            Xc = d[0] / 2;
            Yc = d[1] / 2;
            R2 = (ax - Xc) * (ax - Xc) + (ay - Yc) * (ay - Yc);
        } else {
            // if the samples are equal phi distributed over 2pi
            // this is a good initial guess. Otherwise (Xc,Yc) will
            // have a big bias towards the main sector in phi and R2
            // is underestimated.
            Xc = sx[0];
            Yc = sx[1];
            X2 = sxy[0][0];
            Y2 = sxy[1][1];
            R2 = X2 + Y2 - (Xc * Xc + Yc * Yc);
        }
        // transform R,Xc,Yc into the Kaza variables A,B,C.
        a[0] = -2 * Xc;
        a[1] = -2 * Yc;
        a[2] = Xc * Xc + Yc * Yc - R2;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.GenericFitter#initParameters(double[], double[])
     */
    @Override
    protected void initParameters(final double[] x, final double[] y) {
        initParameters(x, y, null);
    }

    /**
     * get the fitted circle radius squared.
     *
     * @return R2
     */
    @Override
    public double getRadiusSquared() {
        return (a[0] * a[0] + a[1] * a[1]) / 4 - a[2];
    }

    /**
     * get the fitted circle x center.
     *
     * @return Xc
     */
    @Override
    public double getXCenter() {
        return -a[0] / 2;
    }

    /**
     * get the fitted circle y center.
     *
     * @return Yc
     */
    @Override
    public double getYCenter() {
        return -a[1] / 2;
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
        return chi2(x, y, null);
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
        if (null == sx) {
            initChi2(x, y, dy);
        }
        double chi2;
        chi2 = (a[0] * a[0] * sxy[0][0] + a[1] * a[1] * sxy[1][1] + a[2] * a[2] + sxy[2][2]) / 2;
        chi2 += a[0] * a[1] * sxy[0][1] + a[0] * a[2] * sx[0] + a[0] * sxy[0][2];
        chi2 += a[1] * a[2] * sx[1] + a[1] * sxy[1][2] + a[2] * sx[2];
        return chi2;
    }

    /**
     * The gradient vector of the chi2 function for the (x,y) values and the
     * actual parameters a.
     *
     * @param x double[] the x values
     * @param y double[] the y values
     * @return the gradient of chi2
     */
    @Override
    protected double[] gradChi2(final double[] x, final double[] y) {
        return gradChi2(x, y, null);
    }

    /**
     * The gradient vector of the chi2 function for the (x,y) values and the
     * actual parameters a.
     *
     * @param x double[] the x values
     * @param y double[] the y values
     * @param w double[] the approximate errors
     * @return the gradient of chi2
     */
    @Override
    protected double[] gradChi2(final double[] x, final double[] y, final double[] w) {
        if (null == sx) {
            initChi2(x, y, w);
        }
        final double[] grad = new double[3];
        grad[0] = a[0] * sxy[0][0] + a[1] * sxy[0][1] + a[2] * sx[0] + sxy[0][2];
        grad[1] = a[0] * sxy[1][0] + a[1] * sxy[1][1] + a[2] * sx[1] + sxy[1][2];
        grad[2] = a[0] * sx[0] + a[1] * sx[1] + sx[2] + a[2];

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
    @Override
    protected double[][] hessChi2(final double[] x, final double[] y) {
        return hessChi2(x, y, null);
    }

    /**
     * The hessian matrix of the chi2 function for the (x,y) values and the actual
     * parameters a.
     *
     * @param x double[] the x values
     * @param y double[] the y values
     * @param w double[] the approximate errors
     * @return the hessian matrix of chi2
     */
    @Override
    protected double[][] hessChi2(final double[] x, final double[] y, final double[] w) {
        if (null == sx) {
            initChi2(x, y, w);
        }
        final double[][] hess = new double[3][3];
        hess[0][0] = sxy[0][0];
        hess[0][1] = sxy[0][1];
        hess[0][2] = sx[0];

        hess[1][0] = hess[0][1];
        hess[1][1] = sxy[1][1];
        hess[1][2] = sx[1];

        hess[2][0] = hess[0][2];
        hess[2][1] = hess[1][2];
        hess[2][2] = 1;
        return hess;
    }

}
 