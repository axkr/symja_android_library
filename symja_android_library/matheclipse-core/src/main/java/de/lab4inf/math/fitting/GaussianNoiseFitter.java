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

import static java.lang.Math.exp;
import static java.lang.String.format;

/**
 * Fit the (x,y) data tuples with a gaussian plus quadratic polynom distribution.
 * <br/>
 * <pre>
 *         y(x;a) := a[0]*g(z)  + p(x)
 *         z = (x - a[1])/a[2]
 *         p(x) = a[3] + a[4]*x + a[5]*x*x
 * </pre>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: GaussianNoiseFitter.java,v 1.13 2014/12/10 17:48:02 nwulff Exp $
 * @since 10.10.2006
 */
public class GaussianNoiseFitter extends GaussianFitter {

    /**
     * Default bean constructor.
     */
    public GaussianNoiseFitter() {
        super(6);
        setApproximate(false);
        setUsePearson(false);
        setUseLinear(false);
        // setDebug(true);
        setEps(0.005);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.fitting.GenericFitter#initParameters(double[], double[])
     */
    @Override
    protected void initParameters(final double[] x, final double[] y) {
        double sumXY = 0, sumX2Y = 0, sumY = 0;
        int n = x.length;
        for (int i = 0; i < n; i++) {
            sumXY += x[i] * y[i];
            sumX2Y += x[i] * x[i] * y[i];
            sumY += y[i];
        }
        sumXY /= sumY;
        sumX2Y /= sumY;
        // our best guess if data is gaussian
        a[2] = sumXY;
        a[1] = Math.sqrt(sumX2Y - sumXY * sumXY);
        // a[0] = (10 * a[1]*sumY /n);
        a[0] = (10 * sumY / (n * SQRT2PI));
        // a[3] = (x[0] + x[n-1])/2;
        a[3] = sumY / n;
        a[4] = (y[0] - y[n - 1]) / (x[0] - x[n - 1]);
        a[5] = a[4] / 100;
        for (int i = 0; i < a.length; i++) {
            getLogger().info(format("a[%d]=%f", i, a[i]));
        }
    }

    @Override
    public double[] fitt(final double[] x, final double[] y) {
        super.fitt(x, y);
        // a[0] = a[0]*(SQRT2PI*a[1]);
        return getParameters();
    }

    @Override
    public double[] fitt(final double[] x, final double[] y, final double[] dy) {
        super.fitt(x, y, dy);
        // a[0] = a[0]*(SQRT2PI*a[1]);
        return getParameters();
    }

    /*
     * (non-Javadoc)
     *
    * @see de.lab4inf.fitting.GenericFitter#fct(double)
    */
    @Override
    public double fct(final double x) {
        // return a[0]/(SQRT2PI*a[1])*g(x) + p(x);
        double g = a[0] * g(x) + p(x);
        // introduce a penalty term if a[0]<= 0 or a[1] <=0
        double p = 1;
        if (isUsePenalty()) {
            p *= (1 + exp(-getPenaltyValue() * a[0]));
            p *= (1 + exp(-getPenaltyValue() * a[1]));
        }
        return g * p;
    }

    /**
     * The gaussian part of the fit function.
     *
     * @param x to fit y for
     * @return gaussian(x;a)
     */
    protected double g(final double x) {
        double z = (x - a[2]) / a[1];
        return exp(-0.5 * z * z);
    }

    /**
     * The quadratic polynomial noise part of the fit function.
     *
     * @param x to fit y for
     * @return poly(x;a)
     */
    protected double p(final double x) {
        return a[3] + a[4] * x + a[5] * x * x;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.fitting.GenericFitter#dFct(int, double)
     */
    @Override
    protected double dFct(final int k, final double x) {
        double g = g(x);
        double b = a[2];
        double c = a[1];
        double amp = a[0];
        double z = (x - b) / c;
        double dF = 0;
        switch (k) {
            case 5:
                dF = x * x;
                break;
            case 4:
                dF = x;
                break;
            case 3:
                dF = 1;
                break;
            case 2:
                dF = amp * g * z / c;
                break;
            case 1:
                dF = amp * g * z * z / c;
                break;
            case 0:
                dF = g;
                break;
            default:
                throw new IllegalArgumentException("k:" + k);
        }
        return dF;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.fitting.GenericFitter#ddFct(int, int, double)
     */
    @Override
    protected double ddFct(final int k, final int l, final double x) {
        double g = g(x);
        double b = a[2], c = a[1], amp = a[0], z = (x - b) / c, ddF = 0;
        double c2 = c * c, z2 = z * z;
        // if (k<0 || k>5 || l<0 || l>5) {
        // throw new IllegalArgumentException(format("(k,l)=(%d,%d)",k,l));
        // }
        if (k > 2 || l > 2) {
            return 0;
        }
        int kml = k * l;
        int kpl = k + l;
        if (kml == 4) {
            ddF = amp * g * (z2 - 1) / c2;
        } else if (kml == 2) {
            ddF = amp * g * z * (z2 - 2) / c2;
        } else if (kml == 1) {
            ddF = amp * g * z2 / c2 * (z2 - 3);
        } else {
            if (kpl == 2) {
                ddF = g * z / c;
            } else if (kpl == 1) {
                ddF = g * z2 / c;
            } else {
                ddF = 0;
            }
        }
        return ddF;
    }
}
 