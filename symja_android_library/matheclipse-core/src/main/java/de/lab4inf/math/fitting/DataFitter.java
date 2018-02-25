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

/**
 * Generic interface for a parameterized model
 * to fit against the given (x,y) data tuples.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: DataFitter.java,v 1.10 2014/12/12 14:12:48 nwulff Exp $
 * @since 11.10.2006
 */
public interface DataFitter {
    /**
     * The generic fit function y(x;a).
     * The free parameters a_0,...,a_n have been
     * fitted to the data before..
     *
     * @param x the value of x
     * @return y(x;a) the value of the fit function at x
     */
    double fct(double x);

    /**
     * Set the desired accuracy for the approximation.
     *
     * @param accuracy to reach
     */
    void setEps(double accuracy);

    /**
     * Indication if the gradient and hessian are
     * approximated or are supplied by the fitter.
     *
     * @return approx flag for the approximation calculus
     */
    boolean isApproximate();

    /**
     * Indication if the gradient and hessian should
     * be approximated or are supplied by the fitter.
     *
     * @param approx flag for the approximation calculus
     */
    void setApproximate(boolean approx);

    /**
     * get the actual fitted parameters.
     *
     * @return the parameter array
     */
    double[] getParameters();

    /**
     * Fit the parameters (a):={a0,a1,...,an} to
     * the data values (x,y).
     *
     * @param x double[] the x values
     * @param y double[] the y values
     * @return double[] the fitted model parameters
     */
    double[] fitt(double[] x, double[] y);

    /**
     * Calculate the chi square between the data and the fit function.
     *
     * @param x double[] the x array
     * @param y double[] the y array
     * @return double the chi2 value
     */
    double chi2(double[] x, double[] y);

    /**
     * Fit the parameters (a):={a0,a1,...,an} to
     * the data values (x,y).
     *
     * @param x  double[] the x values
     * @param y  double[] the y values
     * @param dy double[] the errors of the y values
     * @return double[] the fitted model parameters
     */
    double[] fitt(double[] x, double[] y, double[] dy);

    /**
     * Calculate the chi square between the data and the fit function.
     *
     * @param x  double[] the x array
     * @param y  double[] the y array
     * @param dy double[] the errors of the y values
     * @return double the chi2 value
     */
    double chi2(double[] x, double[] y, double[] dy);

    /**
     * Clear any possible internal state for a new data sample.
     */
    void clear();
}
 