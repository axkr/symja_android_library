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
package de.lab4inf.math.integration;

import de.lab4inf.math.Function;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.Letters;
import de.lab4inf.math.gof.Decorator;
import de.lab4inf.math.gof.Pattern;
import de.lab4inf.math.gof.Visitable;
import de.lab4inf.math.gof.Visitor;
import de.lab4inf.math.util.Accuracy;
import de.lab4inf.math.util.Aitken;

import static de.lab4inf.math.util.Accuracy.hasReachedAccuracy;
import static de.lab4inf.math.util.Accuracy.relativeDifference;
import static java.lang.Double.isInfinite;
import static java.lang.Math.abs;
import static java.lang.String.format;

/**
 * Integrate an arbitrary Function on the interval [a,b] with given
 * accuracy epsilon, using an enhanced Simpson, trapezoid or Gauss-Legendre
 * approximation formula.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Integrator.java,v 1.38 2014/06/26 13:02:38 nwulff Exp $
 * @since 05.11.2004
 */
public final class Integrator extends L4MObject implements de.lab4inf.math.Integrator {
    public static final char INTEGRAL = Letters.INTEGRAL;
    public static final double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
    public static final double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
    private static final String FMT = INTEGRAL + " %s=%+.3g \u0394=%6.1g \u2265 \u03B5=%6.1g"
            + " poor convergence with %d splits";
    private static final String FMTL = INTEGRAL + " %s[%+.3g,%+.3g]=%+.3g "
            + "\u0394=%6.1g \u2265 \u03B5=%6.1g poor convergence with %d weights";
    /**
     * default integration precision.
     */
    private static final double EPS = 1.E-8;

    ;
    /**
     * minimal number of interval splits.
     */
    private static final int NMIN = 6;
    /**
     * maximal number of interval splits.
     */
    private static final int NMAX = 20;
    private static Method method = Method.SIMPSON;
    /**
     * throw an exception if no convergence.
     */
    private static boolean throwing = true;
    /**
     * should Aitken acceleration be used.
     */
    private static boolean usingAitken = true;
    /**
     * Private constructor as no instances are allowed.
     */
    private Integrator() {
    }

    /**
     * Indicate if the Aitken method is enabled.
     *
     * @return the usingAitken
     */
    public static boolean isUsingAitken() {
        return usingAitken;
    }

    /**
     * Set the Aitken acceleration flag.
     *
     * @param usingAitken the usingAitken to set
     */
    public static void setUsingAitken(final boolean usingAitken) {
        Integrator.usingAitken = usingAitken;
    }

    /**
     * Indicate if no convergence will throw an exception.
     *
     * @return the throwing flag
     */
    public static boolean isThrowing() {
        return throwing;
    }

    /**
     * Set the throwing flag. If true an exception
     * is thrown if no convergence is reached
     * otherwise only a warning will be logged.
     *
     * @param throwing the throwing to set
     */
    public static void setThrowing(final boolean throwing) {
        Integrator.throwing = throwing;
    }

    /**
     * Get the actual integration method.
     *
     * @return the method
     */
    public static Method getMethod() {
        return method;
    }

    /**
     * Set the actual integration method, this can be
     * Trapez-, Legendre- or Simpson-method.
     *
     * @param method the method to set
     */
    public static void setMethod(final Method method) {
        Integrator.method = method;
    }

    /**
     * Check for convergence. This is either
     * the relative precision has been reached or
     * the maximal number of interval splits has been exceeded.
     *
     * @param fct the function just in use
     * @param n   the current split counter
     * @param xn  the new approximation
     * @param xo  the old approximation
     * @param eps the precision to reach
     * @return boolean indication flag
     */
    private static boolean convergence(final Function fct, final int n,
                                       final double xn, final double xo, final double eps) {
        if (n >= NMAX) {
            double err = abs(xn - xo);
            if (abs(xn) > 1) {
                err /= abs(xn);
            }
            if (err > eps) {
                String msg = format(FMT, fct.toString(), xn, err, eps, n);
                getLogger().warn(msg);
                if (throwing) {
                    throw new ArithmeticException(msg);
                }
            }
            return true;
        }
        return n > NMIN && relativeDifference(xn, xo) < eps;
    }

    /**
     * Internal trapezoid integration formula.
     *
     * @param a   double left border
     * @param b   double right border
     * @param acc double accumulator for the approximation
     * @param j   integer the actual splitting level which has 2**j points
     * @param fct Function to integrate
     * @return double the accumulated approximations
     */
    private static double qtrapez(final double a, final double b,
                                  final double acc, final int j, final Function fct) {
        int i, n = 1;
        double h, s, x, r;
        if (j == 1) {
            h = (b - a) / 2.0;
            r = h * (fct.f(b) + fct.f(a));
        } else {
            if (j > 1) {
                n = 1 << (j - 2);
            }
            h = (b - a) / n;
            for (i = 0, x = a + h / 2.0, s = 0.0; i < n; i++, x += h) {
                s += fct.f(x);
            }
            r = (acc + h * s) / 2;
        }
        return r;
    }

    /**
     * Public method to integrate the function over the interval [a,b]
     * using the trapez method.
     * <p>
     * By smart rearrangement the last n function calls to
     * approximate I(n) are included within the next better
     * approximation I(2n). After all 1 + 2**(n-2) interior
     * points are used after n splittings.
     *
     * @param a   double lower integration border
     * @param b   double upper integration border
     * @param eps double the relative tolerated error
     * @param fct Function to integrate
     * @return double the result of the integration
     */
    @SuppressWarnings("rawtypes")
    public static double integrateTrapez(final double a, final double b,
                                         final double eps, final Function fct) {
        final Aitken aitken = new Aitken();
        int n = 0;
        double sn = 0, tn = 0, so = 0, to = 1;
        do {
            to = tn;
            so = sn;
            tn = qtrapez(a, b, to, n, fct);
            sn = (4 * tn - to) / 3;
            if (usingAitken) {
                sn = aitken.next(sn);
            }
        } while (!convergence(fct, ++n, sn, so, eps));

        return sn;
    }

    /**
     * Internal Simpson integration formula.
     *
     * @param a   double left border
     * @param b   double right border
     * @param j   integer the actual splitting level which has 2**j points
     * @param fct Function to integrate
     * @return double the accumulated approximations
     */
    private static double qsimpson(final double a, final double b, final int j,
                                   final Function fct) {
        int i, n = 1;
        double h, s = 0, x;
        n = 1 << (j - 1);
        h = (b - a) / n;
        for (i = 0, x = a + h / 2; i < n; i++, x += h) {
            s += fct.f(x);
        }
        return s;
    }

    /**
     * Public method to integrate the function over the interval [a,b]
     * using the Simpson method.
     * By smart rearrangement the last n function calls to
     * approximate I(n) are included within the next better
     * approximation I(2n).
     *
     * @param a   double lower integration border
     * @param b   double upper integration border
     * @param eps double the relative tolerated error
     * @param fct Function to integrate
     * @return double the result of the integration
     */
    @SuppressWarnings("rawtypes")
    public static double integrateSimpson(final double a, final double b,
                                          final double eps, final Function fct) {
        final Aitken aitken = new Aitken();
        int n = 1;
        double to, tn = 0, h = (b - a) / 3, s = h * (fct.f(a) + fct.f(b)), sn = s, so;
        do {
            to = tn;
            so = sn;
            tn = qsimpson(a, b, n, fct);
            s = s / 2 + h * (2 * tn - to);
            h /= 2;
            sn = s;
            if (usingAitken) {
                sn = aitken.next(s);
            }
        } while (!convergence(fct, ++n, sn, so, eps));
        return sn;
    }

    /**
     * Public method to integrate the function over the interval [a,b]
     * using the Gauss-Legendre weights.
     *
     * @param a   double lower integration border
     * @param b   double upper integration border
     * @param eps double the relative tolerated error
     * @param fct Function to integrate
     * @return double the result of the integration
     */
    @SuppressWarnings("rawtypes")
    public static double integrateLegendre(final double a, final double b,
                                           final double eps, final Function fct) {
        final Aitken aitken = new Aitken();
        final int max = 512;
        double z, yo, yn = 0;
        int n = 2;
        do {
            n *= 2;
            yo = yn;
            z = sumWGL(a, b, n, fct);
            yn = z;
            if (usingAitken) {
                yn = aitken.next(z);
            }
        } while ((!hasReachedAccuracy(yn, yo, eps) || n < 2 * NMIN) && 2 * n <= max);
        if (n >= max) {
            double err = abs(yn - yo);
            if (abs(yn) > 1) {
                err /= abs(yn);
            }
            if (err > eps) {
                String msg = format(FMTL, fct.toString(), a, b, yn, err, eps, n);
                getLogger().warn(msg);
                if (throwing) {
                    throw new ArithmeticException(msg);
                }
            }
        }
        return yn;
    }

    /**
     * Internal summation with n Gauss-Legendre weights and points.
     *
     * @param a   left border
     * @param b   right border
     * @param n   the number of weights
     * @param fct the integrand
     * @return an integral approximation
     */
    private static double sumWGL(final double a, final double b,
                                 final int n, final Function fct) {
        int k;
        double[] w = GaussLegendre.getWeights(n);
        double[] x = GaussLegendre.getAbscissas(a, b, n);
        double z;
        for (z = 0, k = 0; k < n; z += w[k] * fct.f(x[k]), k++) ;
        z *= (b - a) / 2;
        return z;
    }

    /**
     * Integrate the given function within the borders.
     *
     * @param a   left border
     * @param b   right border
     * @param eps precision
     * @param fct function to integrate
     * @return the integral value
     */
    private static double integrateSelected(final double a, final double b,
                                            final double eps, final Function fct) {
        double y;
        switch (method) {
            case ADAPTIVE:
                y = integrateAdaptive(a, b, eps, fct);
                break;
            case LEGENDRE:
                y = integrateLegendre(a, b, eps, fct);
                break;
            case TRAPEZ:
                y = integrateTrapez(a, b, eps, fct);
                break;
            case SIMPSON:
            default:
                y = integrateSimpson(a, b, eps, fct);
                break;
        }
        y = Accuracy.round(y, eps / 10);
        return y;
    }

    /**
     * Public method to integrate the given function over the interval [a,b].
     * The special cases where one or both borders are infinite are handled
     * by this method and transformed to a proper integration kernel.
     *
     * @param a   double lower integration border
     * @param b   double upper integration border
     * @param eps double the relative tolerated error
     * @param fct Function to integrate
     * @return double the result of the integration
     */
    public static double integrate(final double a, final double b,
                                   final double eps, final Function fct) {
        Function kernel = fct;
        double left = a, right = b;
        // for infinite integrals do a proper transformation into
        // the [0,1] or [-1,1] or [0,1/z] interval with a new kernel...
        if (isInfinite(a) || isInfinite(b)) {
            if ((a == 0 || b == 0) || (isInfinite(a) && isInfinite(b))) {
                kernel = new KernelZeroToInfinity(fct);
                if (isInfinite(a)) {
                    left = -1;
                }
                if (isInfinite(b)) {
                    right = 1;
                }
            } else {
                kernel = new KernelInfinity(fct);
                if (isInfinite(a)) {
                    right = 0;
                    left = 1.0 / b;
                } else {
                    right = 1.0 / a;
                    left = 0;
                }
            }
        }
        return integrateSelected(left, right, eps, kernel);
    }

    /**
     * Public method to integrate the give function over the interval [a,b]
     * with a relative precision of 1E-8.
     *
     * @param a   double lower integration border
     * @param b   double upper integration border
     * @param fct Function to integrate
     * @return double the result of the integration
     */
    public static double integrate(final double a, final double b,
                                   final Function fct) {
        return integrate(a, b, EPS, fct);
    }

    /**
     * Adaptive integration, it splits the integration interval into two halves
     * and compares these integrations with the result of an integration over the
     * whole interval. If they don't agree a recursive call will be done. Thus in
     * regions of rapid change of the integration kernel more calls will be done.
     *
     * @param a   double lower integration border
     * @param b   double upper integration border
     * @param eps double the relative tolerated error
     * @param fct Function to integrate
     * @return double the result of the integration
     */
    public static double integrateAdaptive(final double a, final double b,
                                           final double eps, final Function fct) {
        final int n = 16;
        final double epsMin = 50 * Accuracy.DEPS;
        double e = Math.max(epsMin, eps), m = (a + b) / 2;
        double so, sn, sl, sr;
        // setThrowing(false);
        if (abs(b - a) < Accuracy.FEPS) {
            getLogger().warn(format("to small \u0394x: %.2g", abs(b - a)));
            return 0;
        }
        if (eps < epsMin) {
            throw new ArithmeticException(format("to small \u03B5=%.2g", eps));
        }
        so = sumWGL(a, b, n, fct);
        sl = sumWGL(a, m, n, fct);
        sr = sumWGL(m, b, n, fct);
        sn = sl + sr;
        if (relativeDifference(sn, so) > e) {
            //getLogger().info(format("recursive: a=%.2g b=%.2g", a, b));
            // make a recursive call for left and right part
            double wl = abs(sl) / (abs(sl) + abs(sr));
            double wr = abs(sr) / (abs(sl) + abs(sr));
            sl = integrateAdaptive(a, m, e / wl, fct);
            sr = integrateAdaptive(m, b, e / wr, fct);
            sn = sl + sr;
        }
        return sn;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.gof.Visitor#visit(de.lab4inf.math.gof.Visitable)
     */
    @Override
    public void visit(final Visitable<Function> subject) {
        throw new IllegalStateException("not implemented yet...");
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.Integrator#antiderivative(de.lab4inf.math.Function)
     */
    @Override
    public Function antiderivative(final Function fct) {
        throw new IllegalStateException("not implemented yet...");
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.Integrator#integrate(de.lab4inf.math.Function, double, double)
     */
    @Override
    public double integrate(final Function fct, final double a, final double b) {
        return integrate(a, b, fct);
    }

    /**
     * Enumeration to choose the trapez or simpson integration method.
     */
    public enum Method {
        /**
         * choose the trapez method.
         */
        TRAPEZ,
        /**
         * choose the Simpson method.
         */
        SIMPSON,
        /**
         * choose the Gauss-Legendre method.
         */
        LEGENDRE,
        /**
         * choose the adaptive method.
         */
        ADAPTIVE
    }

    /**
     * Internal kernel for indefinite integrals, where one or both integration borders
     * are infinity.
     */
    @Pattern(name = "Decorator")
    private abstract static class IntegratorKernel implements Function, Decorator<Function> {
        protected final Function decorated;

        public IntegratorKernel(final Function fct) {
            decorated = fct;
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
         */
        @Override
        public void accept(final Visitor<Function> visitor) {
            visitor.visit(this);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.gof.Decorator#getDecorated()
         */
        @Override
        public Function getDecorated() {
            return decorated;
        }
    }

    /**
     * Internal kernel for indefinite integrals, where the borders
     * a and/or b are zero or infinity.
     */
    private static class KernelZeroToInfinity extends IntegratorKernel {

        public KernelZeroToInfinity(final Function fct) {
            super(fct);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.Function#f(double[])
         */
        public double f(final double... x) {
            double t = x[0], y = decorated.f(t);
            if (t != 0) {
                double z = 1.0 / t;
                y += z * z * decorated.f(z);
            }
            return y;
        }

    }

    /**
     * Internal kernel for indefinite integrals, where one of the borders
     * is infinity.
     */
    private static class KernelInfinity extends IntegratorKernel {

        public KernelInfinity(final Function fct) {
            super(fct);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.Function#f(double[])
         */
        public double f(final double... x) {
            double t = x[0], y = 0;
            if (t != 0) {
                double z = 1.0 / t;
                y = z * z * decorated.f(z);
            }
            return y;
        }
    }
}
 