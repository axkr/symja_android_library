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
package de.lab4inf.math.differentiation;

import de.lab4inf.math.Complex;
import de.lab4inf.math.Differentiable;
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
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.String.format;

/**
 * Numerical differentiation of a function using a five point approximation or
 * the exact derivative if function implements the Differentiable interface.
 * Static methods for the first and second derivative are also provided.
 * <br/>
 * <b>Note</b><br/>:
 * This implementation is for functions with <u>one</u> real variable as
 * argument. For functions with more variables use the Gradient.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Differentiator.java,v 1.44 2015/01/16 09:44:36 nwulff Exp $
 * @since 21.01.2005
 */
@Pattern(name = "Visitor,Decorator")
public final class Differentiator extends L4MObject implements Function, Decorator<Function>,
        de.lab4inf.math.Differentiator {
    /**
     * maximal number of interval splitting.
     */
    public static final int NMAX = 32;
    /**
     * reference to the DD_F_NO_CONVERGENCE attribute.
     */
    private static final String DD_F_NO_CONVERGENCE = Letters.PARTIAL + "\u00B2%s no convergence after %d iterations";
    /**
     * reference to the DF_NO_CONVERGENCE attribute.
     */
    private static final String DF_NO_CONVERGENCE = Letters.PARTIAL + "%s(%.2g), %d iterations," + Letters.DELTA
            + "x: %.2e no convergence";
    /**
     * reference to the DF_BAD_CONVERGENCE attribute.
     */
    private static final String DF_BAD_CONVERGENCE = Letters.PARTIAL + "%s(%.2g), %d iterations," + Letters.DELTA
            + "x: %.2e slow convergence";
    // public static final int NMAX = 16;
    private static final double H_START = Math.pow(1000 * Accuracy.DEPS, 1. / 5.);// = 0.1;
    private static final double EPS = Accuracy.FEPS;
    private static boolean shouldThrow = false;
    private final Function fct;
    private final double eps;
    private Function derivative;

    /**
     * Wrapper constructor to differentiate the given function, with
     * the default accuracy of about 10E-8.
     *
     * @param fct Function to wrap
     */
    public Differentiator(final Function fct) {
        this(fct, EPS);
    }

    /**
     * Wrapper constructor to differentiate the function, with
     * the given accuracy.
     *
     * @param fct Function to wrap
     * @param eps the accuracy to reach
     */
    public Differentiator(final Function fct, final double eps) {
        this.fct = fct;
        this.eps = eps;
        fct.accept(this);
    }

    /**
     * Differentiate function fct at point x with the default floating point
     * precision, that is about 10.E-8.
     *
     * @param fct function to differentiate
     * @param x   the argument for f'(x)
     * @return the derivative at point x of f
     */
    public static double dF(final Function fct, final double... x) {
        return dF(EPS, fct, x);
    }

    /**
     * Calculate the second derivative of function fct at point x with the default
     * floating point precision, that is about 10.E-8.
     *
     * @param fct function to differentiate
     * @param x   the argument for f'(x)
     * @return the derivative at point x of f
     */
    public static double ddF(final Function fct, final double... x) {
        return ddF(EPS, fct, x);
    }

    /**
     * Differentiate function fct at point x with eps precision.
     *
     * @param fct function to differentiate
     * @param x   the argument for f'(x)
     * @param eps the precision to reach
     * @return the derivative at point x of f
     */
    public static double dF(final double eps, final Function fct, final double... x) {
        int n = 0;
        double dx, xx = x[0];
        double dy, dyo, err = 1. / eps, erro, fpp, fmm, fp, fm, df, dfo, h;
        Aitken<?> aitken = new Aitken<Complex>();
        // check if H is within range of function
        h = initialH(fct, x[0]);
        dx = max(1, abs(xx)) * h;
        fpp = fct.f(xx + 2 * dx);
        fmm = fct.f(xx - 2 * dx);
        fp = fct.f(xx + dx);
        fm = fct.f(xx - dx);
        df = (fmm - 8 * fm + 8 * fp - fpp) / (12 * dx);
        df = aitken.next(df);
        fpp = fp;
        fmm = fm;
        dy = df;
        do {
            erro = err;
            dyo = dy;
            dfo = df;
            h /= 2;
            dx = max(1, abs(xx)) * h;
            fp = fct.f(xx + dx);
            fm = fct.f(xx - dx);
            // using 5-point formula needs as many function evaluations
            // as the 3-point method when h is halved in each step...
            df = (fmm - 8 * fm + 8 * fp - fpp) / (12 * dx);
            // eliminate the h**4 term of the 5-point formula using the
            // Richardson expansion...
            dy = (16 * df - dfo) / 15;
            dy = aitken.next(dy);
            err = abs(dy - dyo);
            fpp = fp;
            fmm = fm;
        } while (!hasReachedAccuracy(dy, dyo, eps / 3) && (err < 20 * erro) && (++n < NMAX));
        if (err > erro) {
            String msg = format(DF_BAD_CONVERGENCE, fct.getClass().getSimpleName(), xx, n, dx);
            getLogger().error(msg);
            if (shouldThrow) {
                throw new ArithmeticException(msg);
            }
        }
        if (n >= NMAX) {
            String msg = format(DF_NO_CONVERGENCE, fct.getClass().getSimpleName(), xx, n, dx);
            getLogger().error(msg);
            if (shouldThrow) {
                throw new ArithmeticException(msg);
            }
        }
        // dy = round(dy, eps);
        return dy;
    }

    /**
     * Initial adjustment of the H value for the numerical derivative.
     *
     * @param fct function to use
     * @param x   the argument
     * @return the initial h value
     */
    private static double initialH(final Function fct, final double x) {
        int n = 0;
        boolean hOk = true;
        double dx, xx = x;
        double fpp, h = H_START;
        // check if H is within range of function
        do {
            hOk = true;
            try {
                dx = max(1, abs(xx)) * h;
                fpp = fct.f(xx + 2 * dx);
                if (Double.isNaN(fpp)) {
                    hOk = false;
                    h /= 2;
                    n++;
                }
                fpp = fct.f(xx - 2 * dx);
                if (Double.isNaN(fpp)) {
                    hOk = false;
                    h /= 2;
                    n++;
                }
            } catch (IllegalArgumentException error) {
                hOk = false;
                h /= 2;
                n++;
            }

        } while (!hOk && n < NMAX);
        if (abs(H_START - h) >= H_START / 2) {
            getLogger().warn(format(Letters.UPPER_DELTA + " start adjusted to %.1E ", h));
        }
        return h;
    }

    /**
     * Calculate the second derivative of function fct at point x with
     * eps precision.
     *
     * @param fct function to differentiate
     * @param x   the argument for f''(x)
     * @param eps the precision to reach
     * @return the second derivative f''(x) of f at point x
     */
    public static double ddF(final double eps, final Function fct, final double... x) {
        int n = 0;
        double dx, xx = x[0];
        double ddy, ddyo, fp, fm, f0, fpp, fmm, ddF, tmp, h;
        Aitken<?> aitken = new Aitken<Complex>();
        h = initialH(fct, x[0]);
        dx = max(1, abs(xx)) * h;
        fpp = fct.f(xx + 2 * dx);
        fmm = fct.f(xx - 2 * dx);
        fp = fct.f(xx + dx);
        fm = fct.f(xx - dx);
        f0 = fct.f(xx);
        ddF = (-fmm + 16 * fm - 30 * f0 + 16 * fp - fpp) / (12 * dx * dx);
        ddF = aitken.next(ddF);
        fpp = fp;
        fmm = fm;
        ddy = ddF;
        do {
            ddyo = ddy;
            tmp = ddF;
            h /= 2;
            dx = max(1, abs(xx)) * h;
            fp = fct.f(xx + dx);
            fm = fct.f(xx - dx);
            // using 5-point formula needs as many function evaluations
            // as the 3-point method when h is halved in each step...
            ddF = (-fmm + 16 * fm - 30 * f0 + 16 * fp - fpp) / (12 * dx * dx);
            // eliminate the h**6 term of the 5-point formula
            // using the Richardson expansion...
            ddy = (64 * ddF - tmp) / 63;
            ddy = aitken.next(ddy);
            fpp = fp;
            fmm = fm;
        } while (!hasReachedAccuracy(ddy, ddyo, eps / 3) && (++n < NMAX));
        if (n >= NMAX) {
            String msg = format(DD_F_NO_CONVERGENCE, fct.getClass().getSimpleName(), n);
            getLogger().warn(msg);
            if (shouldThrow) {
                throw new ArithmeticException(msg);
            }
        }
        // ddy = Accuracy.round(ddy, eps);
        if (getLogger().isInfoEnabled()) {
            String msg = format(Letters.PARTIAL + "\u00B2%s convergence after %d iterations", fct.getClass()
                    .getSimpleName(), n);
            getLogger().info(msg);
        }
        return ddy;
    }

    /**
     * Get the value of the shouldThrow attribute.
     *
     * @return the shouldThrow
     */
    public static boolean isShouldThrow() {
        return shouldThrow;
    }

    /**
     * Set the value of the shouldThrow attribute.
     *
     * @param shouldThrow the shouldThrow to set
     */
    public static void setShouldThrow(final boolean shouldThrow) {
        Differentiator.shouldThrow = shouldThrow;
    }

    /**
     * Calculate the derivative of the wrapped function
     * giving output y=f'(x).
     *
     * @param x input value
     * @return output value y=f'(x)
     */
    @Override
    public double f(final double... x) {
        if (null != derivative) {
            return derivative.f(x);
        }
        return dF(eps, fct, x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiator#differential(de.lab4inf.math.Function)
     */
    @Override
    public Function differential(final Function f) {
        return new Differentiator(f);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiator#differentiate(de.lab4inf.math.Function, double)
     */
    @Override
    public double differentiate(final Function f, final double x) {
        return dF(eps, f, x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiator#differentiate(de.lab4inf.math.Function, double[])
     */
    @Override
    public double[] differentiate(final Function f, final double... x) {
        Gradient grad = new GradientApproximator(f);
        return grad.gradient(x);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.gof.Visitor#visit(de.lab4inf.math.gof.Visitable)
     */
    @Override
    public void visit(final Visitable<Function> subject) {
        if (subject instanceof Differentiable) {
            derivative = ((Differentiable) subject).getDerivative();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
     */
    @Override
    public void accept(final Visitor<Function> visitor) {
        visitor.visit(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.gof.Decorator#getDecorated()
     */
    @Override
    public Function getDecorated() {
        return fct;
    }

}
 