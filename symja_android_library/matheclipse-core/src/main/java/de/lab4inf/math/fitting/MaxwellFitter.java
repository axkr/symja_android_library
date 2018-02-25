/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
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

import static java.lang.Math.exp;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

/**
 * Fit for the Maxwell-Boltzmann distribution.
 * <pre>
 *
 *  f(x;a) = sqrt(2/pi)*a0/a1*(x/a1)**2*exp(-0.5*(x/a1)**2)
 *
 * <pre>
 * This distribution occurs if the components (x,y,z) of
 * a three-dimensional vector are a0*N(0,a1) distributed,
 * i.e. are the result of Brownian motion in three dimensions.
 * @author nwulff
 * @since 14.06.2009
 * @version $Id: MaxwellFitter.java,v 1.13 2010/02/25 15:31:54 nwulff Exp $
 */
public class MaxwellFitter extends GenericFitter {
    /**
     * internal normalization constant of the pdf.
     */
    protected static final double SQ2BYPI = sqrt(2.0 / Math.PI);

    // private double[] a;

    /**
     * Default bean constructor.
     */
    public MaxwellFitter() {
        super(2);
        setApproximate(false);
        setUsePearson(true);
        setUsePenalty(false);
        setEps(0.005);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#dFct(int, double)
     */
    @Override
    protected double dFct(final int k, final double x) {
        double dF = fct(x);
        double z = x / a[1];
        switch (k) {
            case 1:
                dF = dF / a[1] * (z * z - 3);
                break;
            case 0:
                dF /= a[0];
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
        double ddF, f = fct(x);
        double z = x / a[1];
        double q = (z * z - 3);
        int i = k + l;
        switch (i) {
            case 2:
                ddF = f / (a[1] * a[1]) * (q * q - q - 2 * z * z);
                break;
            case 1:
                ddF = f * q / (a[0] * a[1]);
                break;
            case 0:
                ddF = 0;
                break;
            default:
                throw new IllegalArgumentException(format("k:%d l:%d", k, l));

        }
        return ddF;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#fct(double)
     */
    @Override
    public double fct(final double x) {
        double z = x / a[1];
        z *= z;
        double y = SQ2BYPI * a[0] / a[1] * z * exp(-0.5 * z);
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
        double sumXY = 0, sumY = 0;
        int n = x.length;
        for (int i = 0; i < n; i++) {
            sumXY += x[i] * y[i];
            sumY += y[i];
            if (a[0] < y[i]) {
                a[0] = y[i];
            }
        }
        sumXY /= sumY;
        a[1] = sumXY / (2 * SQ2BYPI);
        a[0] *= Math.PI * a[1] / (2 * SQ2BYPI);
        getLogger().info("amp: " + a[0]);
        getLogger().info("sig: " + a[1]);
    }
}
 