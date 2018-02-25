/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer Sciences (Lab4Inf).
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

import static de.lab4inf.math.functions.Gamma.gamma;
import static java.lang.Math.exp;
import static java.lang.Math.pow;

/**
 * Fit for the Gamma distribution.
 * <pre>
 *
 * f(x;a) =  A/s*(x/s)**(k-1)*exp(-x/s)
 *     A :=  a[0] includes Gamma(k)
 *     s :=  a[1] is the scale parameter
 *     k :=  a[2] is the shape parameter
 * </pre>
 *
 * @author nwulff
 * @version $Id: GammaFitter.java,v 1.11 2010/02/25 15:31:54 nwulff Exp $
 * @since 16.06.2009
 */
public class GammaFitter extends GenericFitter {
    /**
     * reference to the SHAPE attribute.
     */
    private static final String SHAPE = "shape: ";
    /**
     * reference to the AMPLI attribute.
     */
    private static final String AMPLI = "ampli: ";
    /**
     * reference to the SCALE attribute.
     */
    private static final String SCALE = "scale: ";

    /**
     * Sole constructor.
     */
    public GammaFitter() {
        super(3);
        // at present no derivatives
        setApproximate(true);
        setUsePearson(true);
        setUsePenalty(false);
        setEps(0.005);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#dFct(int, double)
     */
    @Override
    protected double dFct(final int k, final double x) {
        throw new IllegalStateException(NOT_IMPLEMENTED);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#ddFct(int, int, double)
     */
    @Override
    protected double ddFct(final int k, final int l, final double x) {
        throw new IllegalStateException(NOT_IMPLEMENTED);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#fct(double)
     */
    @Override
    public double fct(final double x) {
        double scale = a[1];
        double shape = a[2];
        double z = x / scale;

        double norm = a[0] / (scale * gamma(shape));
        double g = norm * pow(z, shape - 1) * exp(-z);
        // introduce a penalty term if a[0]<= 0, a[1]<=0 or a[1] <1
        double p = 1;
        if (isUsePenalty()) {
            p *= (1 + exp(-getPenaltyValue() * a[0]));
            p *= (1 + exp(-getPenaltyValue() * a[1]));
            p *= (1 + exp(-getPenaltyValue() * (a[2] - 1)));
        }
        return g * p;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#initParameters(double[], double[])
    */
    @Override
    protected void initParameters(final double[] x, final double[] y) {
        double mean = 0, var = 0, sumY = 0;
        int n = x.length;
        for (int i = 0; i < n; i++) {
            mean += x[i] * y[i];
            var += x[i] * x[i] * y[i];
            sumY += y[i];
        }
        mean /= sumY;
        var = var / sumY - mean * mean;
        // our best guess if data is Gamma distributed
        double shape, scale;
        scale = var / mean;
        shape = (mean - var) / (scale * (1 - scale));
        a[1] = scale;
        a[2] = shape;
        a[0] = (10 * scale * sumY / n);
        getLogger().info(SCALE + scale);
        getLogger().info(SHAPE + shape);
        getLogger().info(AMPLI + a[0]);
    }

    /**
     * Get the calculated amplitude.
     *
     * @return amplitude
     */
    public double getAmplitude() {
        return a[0];
    }

    /**
     * Get the calculated scale.
     *
     * @return scale
     */
    public double getScale() {
        return a[1];
    }

    /**
     * Get the calculated shape.
     *
     * @return shape
     */
    public double getShape() {
        return a[2];
    }
}
 