/*
 * Project: Lab4Math
 *
 * Copyright (c) 2006-2010,  Prof. Dr. Nikolaus Wulff
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
import static java.lang.String.format;

/**
 * Fit for a sigmoid s-shaped or logistic-function.
 * <p>
 * <pre>
 *            a0                                 a0
 *  f(x;a) =  -- (1 + tanh((x-a1)/a2)) = -----------------------
 *            2                           1  + exp(-2*(x-a1)/a2)
 *
 * </pre>
 *
 * @author nwulff
 * @version $Id: SigmoidFitter.java,v 1.3 2011/05/03 06:07:02 nwulff Exp $
 * @since 19.12.2010
 */

public class SigmoidFitter extends GenericFitter {

    /**
     * Default constructor.
     */
    public SigmoidFitter() {
        super(3);
        setApproximate(false);
        setUsePearson(true);
        setNewton(false);
        setEps(1.E-5);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#fct(double)
     */
    @Override
    public double fct(final double x) {
        double y = a[0] / (1 + exp(-2 * (x - a[1]) / a[2]));
        return y;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#dFct(int, double)
     */
    @Override
    protected double dFct(final int k, final double x) {
        double dF, f = fct(x), b = a[1], c = a[2];
        switch (k) {
            case 0:
                dF = f / a[0];
                break;
            case 1:
                dF = -2 * f * (1 - f) / c;
                break;
            case 2:
                dF = -2 * (x - b) / (c * c) * f * (1 - f);
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
        double ddF, f = fct(x), b = a[1], c = a[2];
        switch (k) {
            case 0:
                if (l == 0) {
                    ddF = 0;
                } else {
                    ddF = dFct(l, x) / a[0];
                }
                break;
            case 1:
                if (l == 0) {
                    ddF = ddFct(l, k, x);
                } else if (l == 1) {
                    ddF = 2 * (2 * f - 1) * dFct(l, x) / c;
                } else {
                    ddF = (1 + 2 * b / (c * c) * (2 * f - 1)) * dFct(l, x);
                }
                break;
            case 2:
                if (l == 2) {
                    ddF = 2 * (b * (2 * f - 1) / c - 1) * dFct(l, x) / c;
                } else {
                    ddF = ddFct(l, k, x);
                }
                break;
            default:
                throw new IllegalArgumentException(format("k:%d l:%d", k, l));

        }
        return ddF;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#initParameters(double[], double[])
     */
    @Override
    protected void initParameters(final double[] x, final double[] y) {
        int n = x.length;
        double sx = 0, sy = 0;
        double ymin = Double.MAX_VALUE, ymax = -ymin;
        for (int j = 0; j < n; j++) {
            sx += x[j];
            sy += y[j];
            if (ymin > y[j]) {
                ymin = y[j];
            }
            if (ymax < y[j]) {
                ymax = y[j];
            }
        }
        a[0] = y[n - 1];
        a[2] = sy / n;
        sy = (ymax + ymin) / 2;
        for (int j = 1; j < n; j++) {
            if (y[j - 1] < sy && sy <= y[j]) {
                sx = x[j];
            }
        }
        a[1] = sx;
    }

}
 