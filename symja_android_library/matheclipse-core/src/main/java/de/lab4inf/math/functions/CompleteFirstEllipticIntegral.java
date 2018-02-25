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

import de.lab4inf.math.Differentiable;
import de.lab4inf.math.Function;
import de.lab4inf.math.gof.Visitor;
import de.lab4inf.math.util.Accuracy;

import static de.lab4inf.math.functions.CompleteSecondEllipticIntegral.cseint;
import static de.lab4inf.math.util.Mean.agmMean;
import static java.lang.Math.sqrt;

/**
 * Complete elliptic integral of the first kind K(m), see  A&amp;ST 17.3.1.
 * <pre>
 *        &pi;/2
 *        &#8992;
 * K(m) = &#9134; dt 1.0/&radic;(1-m sin<sup>2</sup>t)
 *        &#8993;
 *        0
 * </pre>
 * other definitions use variables k or &alpha; instead of m, with
 * m = k<sup>2</sup>=sin<sup>2</sup>&alpha;.
 * K(m) is related to the first incomplete elliptic integral F(&phi;\&alpha;) via:
 * <pre>
 *
 * K(m) = F(1|m) = F(1,k) = F(&pi;/2\&alpha;)
 *
 * </pre>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: CompleteFirstEllipticIntegral.java,v 1.29 2015/01/29 15:13:03 nwulff Exp $
 * @see <a href="IncompleteFirstEllipticIntegral.html">IncompleteFirstEllipticIntegral</a>
 * @since 20.05.2006
 */
public class CompleteFirstEllipticIntegral extends L4MFunction implements Differentiable {
    private static final double PIH = Math.PI / 2;
    private static final double EPS = Accuracy.DEPS / PIH;
    private static final KDifferential DERIVATIVE = new KDifferential();

    /**
     * Calculate the first elliptic integral using
     * series expansion 17.3.11 from  A&amp;ST.
     *
     * @param m the real argument 0 less equal |m| less one
     * @return K(m)
     */
    public static double cfeint(final double m) {
        double a = 1;
        double y = 1;
        double n = 0;
        double d;
        double mm = 1;
        if (m >= 1) {
            return Double.POSITIVE_INFINITY;
        } else if (m < 0) {
            return cfeint(-m);
            // throw new IllegalArgumentException(String.format("m=%f < 0", m));
        } else if (m > 0.5) {
            // using A&ST 17.3.29
            mm = Math.sqrt(1 - m);
            a = (1 - mm) / (1 + mm);
            return 2.0 / (1.0 + mm) * cfeint(a * a);
        }
        do {
            n++;
            a *= (2 * n - 1) / (2 * n);
            mm *= m;
            d = a * a * mm;
            y += d;
        } while (d > EPS);
        y *= PIH;
        return y;
    }

    /**
     * Calculate the first elliptic integral using the arithmetic-geometric mean
     * method 17.6.1 and 17.6.3 from  A&amp;ST using the angular angle.
     *
     * @param m the real argument |m| less one, with m = sin<sup>2</sup>(&alpha;)
     * @return K(&alpha;)
     */
    public static double cfeagm(final double m) {
        double bn = sqrt(1 - m); // == cos(alpha);
        double an = agmMean(1, bn);
        return PIH / an;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return cfeint(x[0]);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiable#getDerivative()
     */
    @Override
    public Function getDerivative() {
        return DERIVATIVE;
    }

    /**
     * dK/dm  = [(1-m)K(m) - E(m)]/(2m*(m-1))
     */
    private static class KDifferential implements Function {

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
         */
        @Override
        public void accept(final Visitor<Function> visitor) {
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... x) {
            double m = x[0];
            return ((1 - m) * cfeint(m) - cseint(m)) / (2 * m * (m - 1));
        }
    }
}
 