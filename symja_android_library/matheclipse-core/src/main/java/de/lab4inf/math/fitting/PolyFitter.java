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
import de.lab4inf.math.gof.Visitor;

import static de.lab4inf.math.lapack.LinearAlgebra.copy;

/**
 * Generic polynomial fit to a given data tuple.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: PolyFitter.java,v 1.17 2014/12/12 14:12:48 nwulff Exp $
 * @since 04.12.2008
 */
public class PolyFitter extends L4MObject implements DataFitter, Function {
    private final int n;
    private final double[] a;
    private final double[] da;
    private final Solver laSolver;

    /**
     * Constructor for a polynomial fitter with given degree.
     *
     * @param degree of fit
     */
    public PolyFitter(final int degree) {
        this.n = degree;
        this.a = new double[n + 1];
        this.da = new double[n + 1];
        laSolver = resolve(Solver.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.DataFitter#clear()
     */
    public void clear() {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.DataFitter#fct(double)
     */
    public double fct(final double x) {
        return f(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    public double f(final double... x) {
        double xk = x[0], y = 0;
        for (int k = n; k >= 0; k--) {
            y = y * xk + a[k];
        }
        return y;
    }

    /**
     * Chi square test of this fit for the given x,y-tuples and
     * the parametrized test function.
     *
     * @param x the x values
     * @param y the y values
     * @return chi2 for the given values
     */
    public double chi2(final double[] x, final double[] y) {
        int nbins = x.length;
        double rm, chi2 = 0;
        for (int m = 0; m < nbins; m++) {
            rm = y[m] - f(x[m]);
            chi2 += rm * rm;
        }
        return chi2 / 2;
    }

    /**
     * Chi square test of this fit for the given x,y-tuples with y errors and
     * the parametrized test function.
     *
     * @param x  the x values
     * @param y  the y values
     * @param dy the y errors
     * @return chi2 for the given values
     */
    public double chi2(final double[] x, final double[] y, final double[] dy) {
        int nbins = x.length;
        double rm, chi2 = 0;
        for (int m = 0; m < nbins; m++) {
            rm = y[m] - f(x[m]);
            chi2 += rm * rm / dy[m];
        }
        return chi2 / 2;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.DataFitter#fitt(double[], double[])
     */
    public double[] fitt(final double[] x, final double[] y) {
        int m = x.length;
        double[][] matrix = new double[n + 1][n + 1];
        double[] vec = new double[n + 1];
        double xk, xpi, xpij, mij;
        for (int k = 0; k < m; k++) {
            xpi = 1;
            xk = x[k];
            for (int i = 0; i <= n; i++) {
                xpij = xpi;
                for (int j = i; j <= n; j++) {
                    mij = matrix[i][j];
                    mij += xpi * xpij;
                    matrix[i][j] = mij;
                    matrix[j][i] = mij;
                    xpij *= xk;
                }
                vec[i] += xpi * y[k];
                xpi *= xk;
            }
        }
        solveEquation(matrix, vec);
        return getParameters();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.DataFitter#fitt(double[], double[], double[])
     */
    public double[] fitt(final double[] x, final double[] y, final double[] dy) {
        int m = x.length;
        double[][] matrix = new double[n + 1][n + 1];
        double[] vec = new double[n + 1];
        double xk, vk, xpi, xpij, mij;
        for (int k = 0; k < m; k++) {
            xpi = 1;
            xk = x[k];
            vk = dy[k];
            vk *= vk;
            for (int i = 0; i <= n; i++) {
                xpij = xpi / vk;
                for (int j = i; j <= n; j++) {
                    mij = matrix[i][j];
                    mij += xpi * xpij;
                    matrix[i][j] = mij;
                    matrix[j][i] = mij;
                    xpij *= xk;
                }
                vec[i] += xpi * y[k] / vk;
                xpi *= xk;
            }
        }
        solveEquation(matrix, vec);
        return getParameters();
    }

    /**
     * Internal method to solve the equation y = A*x and
     * calculation of the errors dy of y.
     *
     * @param m matrix A
     * @param x vector
     */
    private void solveEquation(final double[][] m, final double[] x) {
        int k = m.length;
        double[][] sys = new double[k][k + 1];
        for (int i = 0; i < k; i++) {
            sys[i][i] = 1;
            sys[i][k] = x[i];
        }

        sys = laSolver.solve(m, sys);
        for (int i = 0; i < k; i++) {
            a[i] = sys[i][k];
            da[i] = Math.sqrt(sys[i][i]);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.DataFitter#getParameters()
     */
    public double[] getParameters() {
        return copy(a);
    }

    /**
     * Return the errors da of the parameters a.
     *
     * @return the parameter errors
     */
    public double[] getParameterErrors() {
        return copy(da);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.DataFitter#isApproximate()
     */
    public boolean isApproximate() {
        // as we have no gradient or hessian provided we are
        // approximate within this respect (used in the test!)
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.DataFitter#setApproximate(boolean)
     */
    public void setApproximate(final boolean approx) {
        // not used as the poly fitter uses analytic formula.
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.DataFitter#setEps(double)
     */
    public void setEps(final double accuracy) {
        // not used as the poly fitter uses analytic formula.
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
 