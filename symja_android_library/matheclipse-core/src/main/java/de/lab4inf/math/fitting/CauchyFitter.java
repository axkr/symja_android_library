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

import static java.lang.Math.exp;
import static java.lang.String.format;

/**
 * Fit for the Cauchy or Breit-Wigner distribution.
 * <pre>
 *            1          a0*a1
 *  f(x;a) = ---  ---------------------
 *            pi  a1**2  + (x - a2)**2
 *
 * </pre>
 * This distribution has no mean or variance, but
 * a median of a2 and a half-width at half-maximum
 * of a1.
 *
 * @author nwulff
 * @version $Id: CauchyFitter.java,v 1.12 2011/09/12 14:49:20 nwulff Exp $
 * @since 15.06.2009
 */
public class CauchyFitter extends GenericFitter {

    /**
     * Sole constructor.
     */
    public CauchyFitter() {
        super(3);
        // at present no derivatives
        setApproximate(true);
        setUsePearson(true);
        setUsePenalty(false);
        // setDebug(true);
        setEps(0.005);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#dFct(int, double)
     */
    @Override
    protected double dFct(final int k, final double x) {
        // double[] a = peek();
        double dF, f = fct(x), x0 = a[2], g = a[1];
        double z = x - x0;
        switch (k) {
            case 0:
                dF = f / a[0];
                break;
            case 1:
                double v = z - g;
                dF = f / g * (v * v / (g * g + z * z));
                break;
            case 2:
                dF = -2 * f * z / (g * g + z * z);
                break;
            default:
                throw new IllegalArgumentException(format("k:%d", k));
        }
        return dF;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#ddFct(int, int, double)
     */
    @Override
    protected double ddFct(final int k, final int l, final double x) {
        throw new IllegalStateException(NOT_IMPLEMENTED);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#fct(double)
     */
    @Override
    public double fct(final double x) {
        // double[] a = peek();
        double z = (x - a[2]) / a[1];
        double y = a[0] / (a[1] * (1 + z * z));

        // introduce a penalty term if a[0]<= 0 or a[1] <=0
        double p = 1;
        if (isUsePenalty()) {
            p *= (1 + exp(-getPenaltyValue() * a[0]));
            p *= (1 + exp(-getPenaltyValue() * a[1]));
        }
        return y * p;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#initParameters(double[], double[])
     */
    @Override
    protected void initParameters(final double[] x, final double[] y) {
        // double[] a = pop();
        double x0 = 0, max = 0, gam = 0;
        int n = x.length;
        // maximum and its position
        for (int i = 0; i < n; i++) {
            if (max < y[i]) {
                max = y[i];
                x0 = x[i];
            }
        }
        // find position of max/2
        for (int i = 0; i < n; i++) {
            if (y[i] > max / 2) {
                gam = x[i] - x0;
                break;
            }
        }

        a[2] = x0;
        a[1] = Math.abs(gam);
        a[0] = max * a[1];
        getLogger().info("x0 : " + a[2]);
        getLogger().info("gam: " + a[1]);
        getLogger().info("amp: " + a[0]);
        // push(a);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#fitt(double[], double[])
     */
    @Override
    public double[] fitt(final double[] x, final double[] y) {
        super.fitt(x, y);
        // double[] a = pop();
        a[0] = a[0] * Math.PI;
        // push(a);
        return getParameters();
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#fitt(double[], double[], double[])
     */
    @Override
    public double[] fitt(final double[] x, final double[] y, final double[] dy) {
        super.fitt(x, y, dy);
        // double[] a = pop();
        a[0] = a[0] * Math.PI;
        // push(a);
        return getParameters();
    }

    /**
     * Get the calculated median value.
     *
     * @return median
     */
    public double getMedian() {
        // double[] a = peek();
        return a[2];
    }

    /**
     * Get the calculated amplitude.
     *
     * @return amplitude
     */
    public double getAmplitude() {
        // double[] a = peek();
        return a[0];
    }

    /**
     * Get the calculated half-width.
     *
     * @return half-width
     */
    public double getHalfWidth() {
        // double[] a = peek();
        return a[1];
    }
}
 