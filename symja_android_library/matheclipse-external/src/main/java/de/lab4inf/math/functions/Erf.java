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

import de.lab4inf.math.util.Accuracy;

import static de.lab4inf.math.util.Accuracy.hasConverged;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

/**
 * Approximation for the error function erf(x).
 * <pre>
 *                   x
 *                  /
 *   erf(x) = 2/&radic;&pi; / exp(-t<sup>2</sup>) dt
 *                /
 *               0
 * </pre>
 * <p>
 * This implementation includes two different methods:
 * <ol>
 * <li>
 * Series expansion or rational approximation from Abramowitz and Stegun. <br/>
 * "Pocketbook of Mathematical Functions", formula 7.1.5 and 7.1.26<br/>
 * with an accuracy of ~1.E-7 for the erf and erfc functions.
 * <li>
 * A rational approximation based on <br/>
 * "Rational chebyshev approximations for the error function"  <br/>
 * By W. J. Cody, Math. Comp., 1969, pp. 631-638.  <br>
 * with an accuracy of ~1.E-15 for the erf and erfc functions which
 * are now the default for the calculations.
 * </ol>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Erf.java,v 1.23 2015/01/29 15:13:03 nwulff Exp $
 * @since 09.01.2009
 */
public class Erf extends L4MFunction {
    static final double PI_SQRT = sqrt(PI);
    static final double TSQPI = 2. / PI_SQRT;
    // ===========================================================================
    static final double THRESHOLD = 0.46875;
    static final double SQRPI = 1. / PI_SQRT;
    static final double X_INF = Double.MAX_VALUE;

    ;
    static final double X_MIN = Double.MIN_VALUE;
    // private static final double X_NEG = -9.38241396824444;
    static final double X_NEG = -sqrt(log(X_INF / 2));
    static final double X_SMALL = Accuracy.DEPS;
    static final double X_HUGE = 1.0 / (2.0 * sqrt(X_SMALL));
    static final double X_MAX = min(X_INF, (1.0 / (PI_SQRT * X_MIN)));
    // static final double X_BIG = 9.194E0;
    static final double X_BIG = 26.543;
    /**
     * Nominator coefficients for approximation to erf in first interval.
     */
    private static final double[] ERF_A = {3.16112374387056560E00, 1.13864154151050156E02, 3.77485237685302021E02,
            3.20937758913846947E03, 1.85777706184603153E-1};
    /**
     * Denominator coefficients for approximation to erf in first interval.
     */
    private static final double[] ERF_B = {2.36012909523441209E01, 2.44024637934444173E02, 1.28261652607737228E03,
            2.84423683343917062E03};
    /**
     * Nominator coefficients for approximation to erfc in second interval.
     */
    private static final double[] ERF_C = {5.64188496988670089E-1, 8.88314979438837594E0, 6.61191906371416295E01,
            2.98635138197400131E02, 8.81952221241769090E02, 1.71204761263407058E03, 2.05107837782607147E03,
            1.23033935479799725E03, 2.15311535474403846E-8};
    /**
     * Denominator coefficients for approximation to erfc in second interval.
     */
    private static final double[] ERF_D = {1.57449261107098347E01, 1.17693950891312499E02, 5.37181101862009858E02,
            1.62138957456669019E03, 3.29079923573345963E03, 4.36261909014324716E03, 3.43936767414372164E03,
            1.23033935480374942E03};
    /**
     * Nominator coefficients for approximation to erfc in third interval.
     */
    private static final double[] ERF_P = {3.05326634961232344E-1, 3.60344899949804439E-1, 1.25781726111229246E-1,
            1.60837851487422766E-2, 6.58749161529837803E-4, 1.63153871373020978E-2};
    /**
     * Denominator coefficients for approximation to erfc in third interval.
     */
    private static final double[] ERF_Q = {2.56852019228982242, 1.87295284992346047, 5.27905102951428412E-1,
            6.05183413124413191E-2, 2.33520497626869185E-3};
    private static boolean firstCall = true;

    // ===========================================================================
    private static Type type = Type.CODY;

    static {
        getLogger().info(String.format("X_MIN  = %.3e", Erf.X_MIN));
        getLogger().info(String.format("X_MAX  = %.3e", Erf.X_MAX));
        getLogger().info(String.format("X_INF  = %.3e", Erf.X_INF));
        getLogger().info(String.format("X_NEG  = %.3f", Erf.X_NEG));
        getLogger().info(String.format("X_SMALL= %.3e", Erf.X_SMALL));
        getLogger().info(String.format("X_HUGE = %.3e", Erf.X_HUGE));
        getLogger().info(String.format("X_BIG  = %.3f", Erf.X_BIG));
    }
    // ===========================================================================

    /**
     * Get the actual calculation method in use.
     *
     * @return the type
     */
    public static Type getType() {
        if (firstCall) {
            firstCall = false;
            getLogger().info(type.toString());
        }
        return type;
    }

    /**
     * Set the calculation method to use.
     * Type 1 means Abramowitz and Stegun.
     * Type 2 means W. J. Cody (the default).
     *
     * @param type the type to set
     */
    public static void setType(final Type type) {
        getLogger().info("switching to " + type.toString());
        Erf.type = type;
    }
    // ===========================================================================

    /**
     * Calculate the error function erf.
     *
     * @param x the argument
     * @return the value erf(x)
     */
    public static double erf(final double x) {
        if (getType() == Type.AS) {
            return erfAS(x);
        }
        return erfCody(x);
    }

    /**
     * Calculate the remaining error function erfc.
     *
     * @param x the argument
     * @return the value erfc(x)
     */
    public static double erfc(final double x) {
        if (getType() == Type.AS) {
            return erfcAS(x);
        }
        return erfcCody(x);
    }

    /**
     * Internal helper method to calculate the error function at value x.
     * This code is  based on a Fortran implementation from
     * <a href="http://www.netlib.org/specfun/erf">W. J. Cody</a>.
     * Refactored by N.Wulff for Java.
     *
     * @param x the argument
     * @return the approximation of erf(x)
     */
    private static double erfCody(final double x) {
        double result = 0;
        final double y = abs(x);
        if (y <= THRESHOLD) {
            result = x * calcLower(y);
        } else {
            result = calcUpper(y);
            result = (0.5 - result) + 0.5;
            if (x < 0) {
                result = -result;
            }
        }
        return result;
    }

    /**
     * Internal helper method to calculate the erfc functions.
     * This code is  based on a Fortran implementation from
     * <a href="http://www.netlib.org/specfun/erf">W. J. Cody</a>.
     * Refactored by N.Wulff for Java.
     *
     * @param x the argument
     * @return the approximation erfc(x)
     */
    private static double erfcCody(final double x) {
        double result = 0;
        final double y = abs(x);
        if (y <= THRESHOLD) {
            result = x * calcLower(y);
            result = 1 - result;
        } else {
            result = calcUpper(y);
            if (x < 0) {
                result = 2.0 - result;
            }
        }
        return result;
    }

    /**
     * Internal helper method to calculate the erf/erfc functions.
     * This code is  based on a Fortran implementation from
     * <a href="http://www.netlib.org/specfun/erf">W. J. Cody</a>.
     * Refactored by N.Wulff for Java.
     *
     * @param y the value y=abs(x)<=THRESHOLD
     * @return the series expansion
     */
    private static double calcLower(final double y) {
        double result;
        double ySq;
        double xNum;
        double xDen;
        ySq = 0.0;
        if (y > X_SMALL)
            ySq = y * y;
        xNum = ERF_A[4] * ySq;
        xDen = ySq;
        for (int i = 0; i < 3; i++) {
            xNum = (xNum + ERF_A[i]) * ySq;
            xDen = (xDen + ERF_B[i]) * ySq;
        }
        result = (xNum + ERF_A[3]) / (xDen + ERF_B[3]);
        return result;
    }

    /**
     * Internal helper method to calculate the erf/erfc functions.
     * This code is  based on a Fortran implementation from
     * <a href="http://www.netlib.org/specfun/erf">W. J. Cody</a>.
     * Refactored by N.Wulff for Java.
     *
     * @param y the value y=abs(x)>THRESHOLD
     * @return the series expansion
     */
    private static double calcUpper(final double y) {
        double result;
        double ySq;
        double xNum;
        double xDen;
        if (y <= 4.0) {
            xNum = ERF_C[8] * y;
            xDen = y;
            for (int i = 0; i < 7; i++) {
                xNum = (xNum + ERF_C[i]) * y;
                xDen = (xDen + ERF_D[i]) * y;
            }
            result = (xNum + ERF_C[7]) / (xDen + ERF_D[7]);
        } else {
            result = 0.0;
            if (y >= X_HUGE) {
                result = SQRPI / y;
            } else {
                ySq = 1.0 / (y * y);
                xNum = ERF_P[5] * ySq;
                xDen = ySq;
                for (int i = 0; i < 4; i++) {
                    xNum = (xNum + ERF_P[i]) * ySq;
                    xDen = (xDen + ERF_Q[i]) * ySq;
                }
                result = ySq * (xNum + ERF_P[4]) / (xDen + ERF_Q[4]);
                result = (SQRPI - result) / y;
            }
        }
        ySq = round(y * 16.0) / 16.0;
        final double del = (y - ySq) * (y + ySq);
        result = exp(-ySq * ySq) * exp(-del) * result;
        return result;
    }

    /**
     * Calculate the error function at value x.
     *
     * @param x the argument
     * @return the value erf(x)
     */
    private static double erfAS(final double x) {
        if (x < 0) {
            return -erfAS(-x);
        }
        if (x < 2) {
            return erfSeries(x);
        }
        return erfRational(x);
    }

    /**
     * Calculate the remaining erfc error function at value x.
     *
     * @param x the argument
     * @return the value erfc(x)
     */
    private static double erfcAS(final double x) {
        return 1 - erfAS(x);
    }

    /**
     * Series expansion from A&S 7.1.5.
     *
     * @param x the argument
     * @return erf(x)
     */
    private static double erfSeries(final double x) {
        final double eps = 1.E-8; // we want only ~1.E-7
        final int kmax = 50; // this can be reached with ~30-40
        double an, ak = x;
        double erfo, erf = ak;
        int k = 1;
        do {
            erfo = erf;
            ak *= -x * x / k;
            an = ak / (2.0 * k + 1.0);
            erf += an;
        } while (!hasConverged(erf, erfo, eps, ++k, kmax));
        return TSQPI * erf;
    }

    /**
     * Rational approximation A&S 7.1.26 with accuracy 1.5E-7.
     *
     * @param x the argument
     * @return erf(x)
     */
    private static double erfRational(final double x) {
         /* coefficients for A&S 7.1.26. */
        final double[] a = {.254829592, -.284496736, 1.421413741, -1.453152027, 1.061405429};
         /* constant for A&S 7.1.26 */
        final double p = .3275911;
        double erf, r = 0;
        final double t = 1.0 / (1 + p * x);
        for (int i = 4; i >= 0; i--) {
            r = a[i] + r * t;
        }
        erf = 1 - t * r * exp(-x * x);
        return erf;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return erf(x[0]);
    }

    /**
     * Enumeration to switch between the Cody (the default) and the  A&amp;ST algorithm.
     */
    public static enum Type {
        AS("erf is using A&S 7.1.5/7.1.26 with error 1.E-7"), CODY("erf is using W.J. Cody algorithm");
        private final String msg;

        Type(final String s) {
            msg = s;
        }

        @Override
        public String toString() {
            return msg;
        }
    }
}
 