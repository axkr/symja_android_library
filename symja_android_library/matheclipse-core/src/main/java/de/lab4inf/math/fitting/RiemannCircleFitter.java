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

import de.lab4inf.math.lapack.EigenValueDecomposition;
import de.lab4inf.math.lapack.JacobiEigenvalueDecomposition;
import de.lab4inf.math.lapack.LinearAlgebra;
import de.lab4inf.math.statistic.DataCollector3D;
import de.lab4inf.math.util.Accuracy;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Fit the (x,y) tuples to a circle, after a transformation on the Riemann sphere.
 * <p>
 * <pre>
 * R. Frühwirt et. all: Track Fitting on the Riemann Sphere.
 * </pre>
 * <p>
 * <pre>
 *          1  N
 *   chi2 = -  &sum;  ( A*wj + B*uj + C*vj + D )**2
 *          2  j=1
 * </pre>
 * <p>
 * where {A,B,C,D} are four free parameter after the transformation.
 * <p>
 * They are  related to the radius R and the (Xc,Yc)-center via:
 * <p>
 * <pre>
 *
 *  Xc = -A/2,   Yc = -B/2,  R**2 = (A**2 + B**2)/4 - C
 *
 * </pre>
 *
 * @author nwulff
 * @version $Id: RiemannCircleFitter.java,v 1.6 2014/12/12 14:15:29 nwulff Exp $
 * @since 03.12.2014
 */
public class RiemannCircleFitter extends CircleFitter {
    boolean crossCheck = false;
    private DataCollector3D dataSample = new DataCollector3D();

    /**
     *
     */
    public RiemannCircleFitter() {
        super(3);
        setApproximate(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.DataFitter#clear()
     */
    @Override
    public void clear() {
        dataSample = new DataCollector3D();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.CircleFitter#getRadiusSquared()
     */
    @Override
    public double getRadiusSquared() {
        return a[0] * a[0];
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.CircleFitter#getXCenter()
     */
    @Override
    public double getXCenter() {
        return a[1];
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.CircleFitter#getYCenter()
     */
    @Override
    public double getYCenter() {
        return a[2];
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.GenericFitter#initParameters(double[], double[])
     */
    @Override
    protected void initParameters(final double[] x, final double[] y) {
        final int n = x.length;
        double w, r2;
        // perform the transformation to the Riemann sphere and sample the data
        for (int j = 0; j < n; j++) {
            r2 = x[j] * x[j] + y[j] * y[j];
            w = 1 / (1 + r2);
            dataSample.collect(x[j] * w, y[j] * w, r2 * w);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.GenericFitter#initParameters(double[], double[], double[])
     */
    @Override
    protected void initParameters(final double[] x, final double[] y, final double[] dy) {
        final int n = x.length;
        double w, s, r2;
        // perform the transformation to the Riemann sphere and sample the weighted data
        for (int j = 0; j < n; j++) {
            r2 = x[j] * x[j] + y[j] * y[j];
            s = dy[j] * dy[j];
            w = 1 / (1 + r2);
            dataSample.collect(x[j] * w, y[j] * w, r2 * w, s);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.GenericFitter#fittParameters(double[], double[], double[])
     */
    @Override
    protected void fittParameters(final double[] x, final double[] y, final double[] dy) {
        fittParameters(x, y);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.GenericFitter#fittParameters(double[], double[])
     */
    @Override
    protected void fittParameters(final double[] x, final double[] y) {
        final int n = 3;
        final int smallestEigenvalue = n - 1;
        final double[][] A = new double[n][n];
        final double[] center = {dataSample.getMeanX(), dataSample.getMeanY(), dataSample.getMeanZ()};
        for (int j = 0; j < n; j++)
            for (int k = 0; k < n; k++)
                A[j][k] = dataSample.getCovar(j, k);
        // A[j][k] = dataSample.getMeanSum(j, k)-dataSample.getMean(j)*dataSample.getMean(k);

        final EigenValueDecomposition solver = new JacobiEigenvalueDecomposition(A);
        final double[] eigenvector = solver.eigenvectors()[smallestEigenvalue];
        // if desired make a consistency check for the accuracy of the
        // eigenvalue and eigenvector.
        if (crossCheck) {
            final double eigenvalue = solver.eigenvalues()[smallestEigenvalue];
            final double[] vt = LinearAlgebra.mult(A, eigenvector);
            final double[] wt = LinearAlgebra.mult(vt, eigenvalue);
            final double diff = LinearAlgebra.diff(vt, wt);
            if (diff > 1.E5 * Accuracy.DEPS) {
                getLogger().error(String.format("eigenvalue/vector missmatch: %.2g", diff));
                // throw new IllegalStateException("wrong eigenvalue/vector: "+diff);
            }
        }
        // back transformation from the Riemann sphere to the original xy-plane
        double R, R2, cx, cy, nx, ny, nz, d;
        nx = eigenvector[0];
        ny = eigenvector[1];
        nz = eigenvector[2];
        d = -LinearAlgebra.mult(eigenvector, center);

        cx = -0.5 * nx / (d + nz);
        cy = -0.5 * ny / (d + nz);
        R2 = 0.25 * (nx * nx + ny * ny - 4 * d * (d + nz)) / ((d + nz) * (d + nz));
        R = sqrt(R2);
        a[0] = R;
        a[1] = cx;
        a[2] = cy;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.GenericFitter#chi2(double[], double[], double[])
     */
    @Override
    public double chi2(final double[] x, final double[] y, final double[] dy) {
        final int n = x.length;
        final double R2 = a[0] * a[0], xc = a[1], yc = a[2];
        double w, sumW = 0, chi2 = 0, dchi;
        for (int j = 0; j < n; j++) {
            w = dy[j] * dy[j];
            dchi = (R2 - pow(x[j] - xc, 2) - pow(y[j] - yc, 2));
            chi2 += w * dchi * dchi;
            sumW += w;
        }
        chi2 /= 2 * sumW;
        return chi2;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.GenericFitter#chi2(double[], double[])
     */
    @Override
    public double chi2(final double[] x, final double[] y) {
        final int n = x.length;
        final double R2 = a[0] * a[0], xc = a[1], yc = a[2];
        double chi2 = 0, dchi;
        for (int j = 0; j < n; j++) {
            dchi = R2 - pow(x[j] - xc, 2) - pow(y[j] - yc, 2);
            chi2 += dchi * dchi;
        }
        chi2 /= 2 * n;
        return chi2;
    }
}
 