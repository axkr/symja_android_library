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

import static de.lab4inf.math.functions.CompleteFirstEllipticIntegral.cfeint;

/**
 * Complete elliptic integral of the second kind E(m), see  A&amp;ST 17.3.3.
 * <pre>
 *        &pi;/2
 *        &#8992;
 * E(m) = &#9134; dt &radic;(1-m sin<sup>2</sup>t)
 *        &#8993;
 *        0
 * </pre>
 * other definitions use variables k or &alpha; instead of m, with
 * m = k<sup>2</sup>=sin<sup>2</sup>&alpha;.
 * E(m) is related to the second incomplete elliptic integral E(&phi;\&alpha;) via:
 * <pre>
 *
 * E(m) = E(1|m) = E(1,k) = E(&pi;/2\&alpha;)
 *
 * </pre>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: CompleteSecondEllipticIntegral.java,v 1.24 2015/01/29 15:13:03 nwulff Exp $
 * @see <a href="IncompleteSecondEllipticIntegral.html">IncompleteSecondEllipticIntegral</a>
 * @since 20.05.2006
 */
public class CompleteSecondEllipticIntegral extends L4MFunction implements Differentiable {
    private static final double PIH = Math.PI / 2;
    private static final double EPS = Accuracy.DEPS / PIH;
    private static final EDifferential DERIVATIVE = new EDifferential();

    /**
     * Calculate the second elliptic integral using
     * series expansion 17.3.12 from  A&amp;ST.
     *
     * @param m the real argument |m| less one
     * @return E(m)
     */
    public static double cseint(final double m) {
        double a = 1;
        double y = 1;
        double n = 0;
        double d;
        double mm = 1;
        if (m >= 1) {
            return 1.0;
        } else if (m < 0) {
            return cseint(-m);
        } else if (m > 0.5) {
            // using A&ST 17.3.30
            mm = Math.sqrt(1 - m);
            a = (1 - mm) / (1 + mm);
            return (1.0 + mm) * cseint(a * a) - 2 * mm / (1 + mm) * cfeint(a * a);
        }
        do {
            n++;
            a *= (2 * n - 1) / (2 * n);
            mm *= m;
            d = a * a * mm / (2 * n - 1);
            y -= d;
        } while (d > EPS);
        y *= PIH;
        return y;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return cseint(x[0]);
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
     * dE/dm  = (E(m) - K(m))/(2m)
     */
    private static class EDifferential implements Function {

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
            return (cseint(m) - cfeint(m)) / (2 * m);
        }
    }
}
 