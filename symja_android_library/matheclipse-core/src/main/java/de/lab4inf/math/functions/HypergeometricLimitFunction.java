/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2014,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math.functions;

import de.lab4inf.math.Differentiable;
import de.lab4inf.math.Function;
import de.lab4inf.math.Integrable;
import de.lab4inf.math.util.Strings;

import static de.lab4inf.math.util.Accuracy.hasConverged;
import static de.lab4inf.math.util.Accuracy.isInteger;
import static java.lang.String.format;

/**
 * The confluent hypergeometric limit series  <sub>0</sub>F<sub>1</sub>(a;x),
 * a solution of the differential equation:
 * <pre>
 *
 *  xy'' + ay' - y = 0.
 *
 * </pre>
 * <p>
 * The Taylor series expansion is:
 * <p>
 * <pre>
 *            &infin;
 *  F(a;x) =  &sum; 1/(a)<sub>k</sub> x<sup>k</sup>/k!
 *           k=0
 * </pre>
 *
 * @author nwulff
 * @version $Id: HypergeometricLimitFunction.java,v 1.6 2014/06/17 09:38:22 nwulff Exp $
 * @since 26.05.2014
 */
public class HypergeometricLimitFunction extends L4MFunction implements Differentiable, Integrable {
    public static final String MSG0F1 = format("%sF%s", Strings.toLowerScript(0), Strings.toLowerScript(1));
    private static final String A_0 = "a=%.0f none positiv integer for " + MSG0F1;
    private static final double PRECISSION = 5.E-14;
    private static final int MAX_ITERATIONS = 500;
    private double a, scale = 1;
    private boolean aSetted;

    /**
     * Default POJO constructor.
     */
    public HypergeometricLimitFunction() {
        aSetted = false;
    }

    public HypergeometricLimitFunction(final double a) {
        this.a = a;
        this.scale = 1;
        aSetted = true;
        checkA(a);
    }

    private static void checkA(final double a) {
        if (a <= 0 && isInteger(a))
            throw new IllegalArgumentException(format(A_0, a));
    }

    /**
     * Calculation of the limit series F(a; x)
     *
     * @param a first parameter
     * @param x the real argument
     * @return F(a; x)
     */
    public static double limitSeries(final double a, final double x) {
        checkA(a);
        double y;
        y = seriesExpansion(a, x);
        return y;
    }

    /**
     * Using series expansion but without using the gamma function to calculate
     * the Pochhammer symbols.
     *
     * @param a the parameter
     * @param x the real argument
     * @return F(a;x)
     */
    protected static double seriesExpansion(final double a, final double x) {
        int k = 0;
        double d = 1, yo = 0, ym, yk = 1, ak = a;
        do {
            ym = yo;
            yo = yk;
            d *= x / ((ak++) * (++k));
            yk += d;
        } while (!hasConverged(yk, yo, PRECISSION, k, MAX_ITERATIONS)
                || !hasConverged(yo, ym, PRECISSION, k, MAX_ITERATIONS));
        return yk;
    }

    @Override
    public String toString() {
        return format("%s(%.2f; x)", MSG0F1, a);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Integrable#getAntiderivative()
     */
    @Override
    public Function getAntiderivative() {
        HypergeometricLimitFunction antiderivative = new HypergeometricLimitFunction(a - 1);
        antiderivative.scale = scale * a;
        return antiderivative;
    }

    /*
    * (non-Javadoc)
    *
    * @see de.lab4inf.math.Differentiable#getDerivative()
    */
    @Override
    public Function getDerivative() {
        HypergeometricLimitFunction derivative = new HypergeometricLimitFunction(a + 1);
        derivative.scale = scale / a;
        return derivative;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.L4MFunction#f(double[])
     */
    @Override
    public double f(final double... x) {
        if (aSetted) {
            return scale * limitSeries(a, x[0]);
        }
        if (x.length != 2) {
            String msg = this + " needs two arguments";
            // logger.warn(msg);
            throw new IllegalArgumentException(msg);
        }
        return scale * limitSeries(x[0], x[1]);
    }
}
 