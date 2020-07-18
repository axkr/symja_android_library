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
package de.lab4inf.math.functions;

/**
 * Implements the Gaussian normal distribution function.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Gaussian.java,v 1.6 2011/09/20 14:05:12 nwulff Exp $
 * @since 19.09.2006
 */
public class Gaussian extends L4MFunction {
    private static final double SQRT2PI = Math.sqrt(2 * Math.PI);
    private final double m, s;

    /**
     * Standard Gaussian(0,1) distribution.
     */
    public Gaussian() {
        this(0, 1);
    }

    /**
     * Gaussian with given mean and derivation.
     *
     * @param mean  value
     * @param sigma derivation value
     */
    public Gaussian(final double mean, final double sigma) {
        this.m = mean;
        this.s = sigma;
    }

    /**
     * Gaussian with given mean and derivation.
     *
     * @param mean  value
     * @param sigma derivation value
     */
    public Gaussian(final Double mean, final Double sigma) {
        this(mean.doubleValue(), sigma.doubleValue());
    }

    /**
     * Calculate the normal(0,1) distribution value at argument x.
     *
     * @param x the argument
     * @return gaussian value
     */
    public static double gaussian(final double x) {
        return gaussian(x, 0, 1);
    }

    /**
     * Calculate the (mean,sigma) gaussian distribution value at argument x.
     *
     * @param x     the argument
     * @param mean  the mean
     * @param sigma the sigma
     * @return gaussian value
     */
    public static double gaussian(final double x, final double mean,
                                  final double sigma) {
        double z = (x - mean) / sigma;
        z = -0.5 * z * z;
        z = Math.exp(z) / (sigma * SQRT2PI);
        return z;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return gaussian(x[0], m, s);
    }
}
 