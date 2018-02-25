/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2014,  Prof. Dr. Nikolaus Wulff
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
package de.lab4inf.math.functions;

import de.lab4inf.math.Differentiable;
import de.lab4inf.math.Function;
import de.lab4inf.math.Integrable;
import de.lab4inf.math.util.Strings;

import static de.lab4inf.math.functions.Gamma.gamma;
import static de.lab4inf.math.util.Accuracy.hasConverged;
import static de.lab4inf.math.util.Accuracy.isInteger;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.String.format;

/**
 * The confluent hypergeometric series  <sub>2</sub>F<sub>1</sub>(a,b,c;x)
 * also known as Gauss series, a solution of the differential equation:
 * <pre>
 *
 *  x(1-x)y'' + (c-(a+b+1)x)y' - aby = 0
 *
 * </pre>
 * <p>
 * To be found in A&amp;ST 15.1.1. The Taylor series expansion is:
 * <p>
 * <pre>
 *                &infin;
 *  F(a,b,c;x) =  &sum; (a)<sub>k</sub>(b)<sub>k</sub>/(c)<sub>k</sub> x<sup>k</sup>/k!
 *               k=0
 * </pre>
 * <p>
 * <p>
 * <hr/>
 * <p>
 * Relations for some special a,b,c values are:
 * <pre>
 *  F(1,1,2,x) = -ln(1-x)/x
 *  F(1/2,1,3/2, x<sup>2</sup>) = 1/(2x)ln((1+x)/(1-x))
 *  F(1/2,1,3/2,-x<sup>2</sup>) = arctan(x)/x
 *  F(1/2,1/2,3/2,x<sup>2</sup>)= arcsin(x)/x
 *  F(a,b,b,x) = (1-x)<sup>-a</sup>
 * </pre>
 *
 * @author nwulff
 * @version $Id: HypergeometricGaussSeries.java,v 1.7 2014/11/16 21:47:23 nwulff Exp $
 * @since 23.05.2014
 */
public class HypergeometricGaussSeries extends L4MFunction implements Differentiable, Integrable {
    public static final String GAUSS = format("%sF%s", Strings.toLowerScript(2), Strings.toLowerScript(1));
    private static final String C_0 = "c=%.0f none positiv integer for " + GAUSS;
    private static final double PRECISSION = 5.E-14;
    private static final int MAX_ITERATIONS = 50000;
    private final boolean abcSetted;
    private double a, b, c, scale = 1;

    /**
     * Default POJO constructor.
     */
    public HypergeometricGaussSeries() {
        abcSetted = false;
    }

    public HypergeometricGaussSeries(final double a, final double b, final double c) {
        checkC(c);
        this.a = a;
        this.b = b;
        this.c = c;
        this.scale = 1;
        abcSetted = true;
    }

    private static void checkC(final double c) {
        if (c <= 0 && isInteger(c))
            throw new IllegalArgumentException(format(C_0, c));
    }

    /**
     * Calculation of the Gauss series F(a,b,c; x)
     *
     * @param a first parameter
     * @param b second parameter must be positive
     * @param c third parameter must not be a negative integer
     * @param x the real argument
     * @return F(a, b, c; x)
     */
    public static double gaussSeries(final double a, final double b, final double c, final double x) {
        checkC(c);
        double y = abs(x);
        if (y > 1) {
            // A&S 15.3.7
            final double z = 1 / x;
            double y1 = gaussSeries(a, 1 - c + a, 1 - b + a, z);
            double y2 = gaussSeries(b, 1 - c + b, 1 - a + b, z);
            y1 /= pow(-x, a);
            y1 *= gamma(b - a) / (gamma(b) * gamma(c - a));
            y2 /= pow(-x, b);
            y2 *= gamma(a - b) / (gamma(a) * gamma(c - b));

            y = gamma(c) * (y1 + y2);
        } else {
            y = seriesExpansion(a, b, c, x);
        }
        return y;
    }

    /**
     * Using series expansion of A&amp;ST 15.1.1 but without using
     * the gamma function to calculate the Pochhammer symbols.
     *
     * @param a first parameter
     * @param b second parameter
     * @param c third parameter
     * @param x the real argument
     * @return F(a, b, c;x)
     */
    protected static double seriesExpansion(final double a, final double b, final double c, final double x) {
        int k = 0;
        double d = 1, yo = 0, ym, yk = 1, ak = a, bk = b, ck = c;
        do {
            ym = yo;
            yo = yk;
            d *= (ak++) * (bk++) * x / ((ck++) * (++k));
            yk += d;
        } while (!hasConverged(yk, yo, PRECISSION, k, MAX_ITERATIONS)
                || !hasConverged(yo, ym, PRECISSION, k, MAX_ITERATIONS));
        return yk;
    }

    @Override
    public String toString() {
        return format("%s(%.2f,%.2f,%.2f; x)", GAUSS, a, b, c);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Integrable#getAntiderivative()
     */
    @Override
    public Function getAntiderivative() {
        final HypergeometricGaussSeries antiderivative = new HypergeometricGaussSeries(a - 1, b - 1, c - 1);
        antiderivative.scale = scale * c / (a * b);
        return antiderivative;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiable#getDerivative()
     */
    @Override
    public Function getDerivative() {
        final HypergeometricGaussSeries derivative = new HypergeometricGaussSeries(a + 1, b + 1, c + 1);
        derivative.scale = scale * a * b / c;
        return derivative;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.L4MFunction#f(double[])
     */
    @Override
    public double f(final double... x) {
        if (abcSetted) {
            return scale * gaussSeries(a, b, c, x[0]);
        }
        if (x.length != 4) {
            final String msg = this + " needs four arguments";
            logger.warn(msg);
            throw new IllegalArgumentException(msg);
        }
        return scale * gaussSeries(x[0], x[1], x[2], x[3]);
    }

}
 