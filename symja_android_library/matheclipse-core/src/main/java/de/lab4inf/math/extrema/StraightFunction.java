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

package de.lab4inf.math.extrema;

import de.lab4inf.math.Function;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.gof.Visitor;
import de.lab4inf.math.lapack.LinearAlgebra;
import de.lab4inf.math.util.Accuracy;

import static de.lab4inf.math.lapack.LinearAlgebra.add;
import static de.lab4inf.math.lapack.LinearAlgebra.mult;
import static de.lab4inf.math.util.Accuracy.hasReachedAccuracy;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.signum;

/**
 * Helper class, to evaluate and optimize a multi-valued function
 * on a straight line from vector x0 to x1.
 * <hr/>
 * <pre>
 * Note: The algorithm is based on
 *
 * R.P. Brent (1973). Algorithms for Minimization without Derivatives,
 * Chapter 4. Prentice-Hall, Englewood Cliffs, NJ. ISBN 0-13-022335-2.
 *
 * </pre>
 *
 * @author nwulff
 * @version $Id: StraightFunction.java,v 1.16 2014/11/16 21:47:23 nwulff Exp $
 * @since 30.11.2009
 */
public class StraightFunction extends L4MObject implements Function {
    /**
     * long golden ratio 1.618033...
     */
    public static final double GL = (1 + Math.sqrt(5)) / 2;
    /**
     * short golden ratio 0.618033...
     */
    public static final double GS = GL - 1;
    /**
     * minor golden ratio 0.381966...
     */
    public static final double GM = 1 - GS;
    private static final double EPS = Accuracy.FEPS;
    private static final double ZEPS = 1.E-12;
    private static final double TINY = 1.E-25;
    private static final int GLIMIT = 100;
    private final Function fct;
    private boolean isInverting = false;
    private double[] x0, x1;

    /**
     * Constructor decorating the given function.
     *
     * @param fct to decorate
     */
    public StraightFunction(final Function fct) {
        this.fct = fct;
    }

    /**
     * Constructor decorating the given function, using the
     * two points x0 and x1 for the straight line.
     *
     * @param fct to decorate
     * @param x0  the start/offset vector
     * @param x1  the direction vector
     */
    public StraightFunction(final Function fct, final double[] x0, final double[] x1) {
        this.fct = fct;
        setX0(x0);
        setX1(x1);
    }

    /**
     * Set the X0 offset vector.
     *
     * @param x0 offset vector
     */
    public void setX0(final double... x0) {
        this.x0 = LinearAlgebra.copy(x0);
    }

    /**
     * Set the X1 to point to.
     *
     * @param x1 the direction vector
     */
    public void setX1(final double... x1) {
        this.x1 = LinearAlgebra.copy(x1);
    }

    /**
     * Calculate f(a) := f(x0 + a*x1).
     * The argument a should be a single real value,
     * i.e. only a[0] will be used.
     *
     * @param a the real argument
     * @return f(a) the value on the straight line
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public final double f(final double... a) {
        if (isInverting) {
            return -fct.f(vector(a[0]));
        }
        return fct.f(vector(a[0]));
    }

    /**
     * Caclulate x(a) = x0 + a*(x1-x0)
     *
     * @param a the parameter
     * @return x(a)
     */
    protected final double[] vector(final double a) {
        // dx = sub(x1, x0);
        return add(x0, mult(x1, a));
    }

    /**
     * Calculate the vector x where f(x) has is extremal, with a &lt; x &lt; b.
     *
     * @param a      left border
     * @param b      right border
     * @param minmal flag to indicate if maximal or minimal
     * @return vector x with max_x f(x)
     */
    public double[] extremum(final double a, final double b, final boolean minmal) {
        return extremum(a, b, EPS, minmal);
    }

    /**
     * Calculate the vector x where f(x) has is extremal, with a &lt; x &lt; b
     * with given accuracy.
     *
     * @param a      left border
     * @param b      right border
     * @param eps    accuracy to reach
     * @param minmal flag to indicate if maximal or minimal
     * @return vector x with max_x f(x)
     */
    public double[] extremum(final double a, final double b, final double eps, final boolean minmal) {
        if (minmal) {
            return minimum(a, b, eps);
        }
        return maximum(a, b, eps);
    }

    /**
     * Calculate the vector x where f(x) is maximal, with x(a) &lt; x &lt; x(b).
     *
     * @param a left border
     * @param b right border
     * @return vector x with max_x f(x)
     */
    public double[] maximum(final double a, final double b) {
        return maximum(a, b, EPS);
    }

    /**
     * Calculate the vector x where f(x) is minimal, with x(a) &lt; x &lt; x(b)
     * with default precision.
     *
     * @param a left border
     * @param b right border
     * @return vector x with min_x f(x)
     */
    public double[] minimum(final double a, final double b) {
        return minimum(a, b, EPS);
    }

    /**
     * Bracket an interval such that f(a) &le; f(x) &le; f(b).
     *
     * @param a
     * @param b
     * @return
     */
    private double[] mnbrak(final double a, final double b) {
        double ax = a, bx = b, cx;
        double fa, fb, fc;
        double umax, u, r, q, fu;
        fa = f(ax);
        fb = f(bx);
        if (fb > fa) { // Switch roles of a and b so that we can go downhill
            u = ax;
            ax = bx;
            bx = u;
            u = fa;
            fa = fb;
            fb = u;
        }
        cx = bx + GL * (bx - ax); // First guess for c.
        fc = f(cx);
        while (fb > fc) {
            r = (bx - ax) * (fb - fc);
            // Compute u by parabolic extrapolation from a, b, c.
            q = (bx - cx) * (fb - fa);
            u = bx - ((bx - cx) * q - (bx - ax) * r) / (2 * max(abs(q - r), TINY) * signum(q - r));
            // maximal search region...
            umax = bx + GLIMIT * (cx - bx);
            if ((bx - u) * (u - cx) > 0) { // Parabolic u is between b and c.
                fu = f(u);
                if (fu < fc) { // Got a minimum between b and c.
                    ax = bx;
                    bx = u;
                    fa = fb;
                    fb = fu;
                } else if (fu > fb) { // Got a minimum between between a and u.
                    cx = u;
                    fc = fu;
                }
                u = cx + GL * (cx - bx); // Parabolic fit was no use.
                fu = f(u); // Use default magnification.
            } else if ((cx - u) * (u - umax) > 0) {
                // Parabolic fit is between c and its allowed limit.
                fu = f(u);
                if (fu < fc) {
                    bx = cx;
                    cx = u;
                    u = cx + GL * (cx - bx);
                    fb = fc;
                    fc = fu;
                    fu = f(u);
                }
            } else if ((u - umax) * (umax - cx) >= 0) {
                // Limit parabolic u to maximum allowed value.
                u = umax;
                fu = f(u);
            } else {
                // Reject parabolic u, use default magnification.
                u = cx + GL * (cx - bx);
                fu = f(u);
            }

            // Eliminate oldest point and continue.
            ax = bx;
            bx = cx;
            fa = fb;
            fb = fc;
            cx = u;
            fc = fu;
        }

        return new double[]{ax, bx, cx};
    }

    /**
     * Find the root of the one dimensional function fct using Brent's method.
     *
     * @param left  the left bracket interval
     * @param right the right bracket interval
     * @param eps   the precision
     * @return x0 with f(x0)=0
     */
    double brent(final double left, final double right, final double eps) {
        int iter = 0;
        final double[] abc = mnbrak(left, right);
        double a, b, d = 0, e = 0, p, q, r, tol1, tol2, tol3, xm;
        double u, x = abc[1], v = x, w = x;
        double fu, fx = f(x), fv = fx, fw = fx;
        a = min(abc[0], abc[2]); // a and b must be in ascending order,
        b = max(abc[0], abc[2]); // but input abscissas need not be.
        do { // Main program loop.
            xm = (a + b) / 2;
            tol1 = eps * abs(x) + ZEPS;
            tol2 = 2 * tol1;
            tol3 = tol2 - (b - a) / 2;
            if (abs(e) > tol1) { // Construct a trial parabolic fit.
                r = (x - w) * (fx - fv);
                q = (x - v) * (fx - fw);
                p = (x - v) * q - (x - w) * r;
                q = 2 * (q - r);
                if (q > 0.0) {
                    p = -p;
                }
                q = abs(q);
                if (2 * abs(p) >= abs(q * e) || p <= q * (a - x) || p >= q * (b - x)) {
                    if (x >= xm) {
                        e = a - x;
                    } else {
                        e = b - x;
                    }
                    d = GM * e;
                } else {
                    e = d;
                    d = p / q; // Take the parabolic step.
                    u = x + d;
                    if (u - a < tol2 || b - u < tol2) {
                        d = tol1 * signum(xm - x);
                    }
                }
            } else {
                if (x >= xm) {
                    e = a - x;
                } else {
                    e = b - x;
                }
                d = GM * e;
            }
            if (abs(d) >= tol1) {
                u = x + d;
            } else {
                u = x + tol1 * signum(d);
            }
            // This is the one function evaluation per iteration.
            fu = f(u);
            if (fu <= fx) {
                if (u >= x) {
                    a = x;
                } else {
                    b = x;
                }
                v = w;
                w = x;
                x = u;
                fv = fw;
                fw = fx;
                fx = fu;
            } else {
                if (u < x) {
                    a = u;
                } else {
                    b = u;
                }
                if (fu <= fw || (abs(w - x) < ZEPS)) {
                    v = w;
                    w = u;
                    fv = fw;
                    fw = fu;
                } else if (fu <= fv || (abs(v - x) < ZEPS) || (abs(v - w) < ZEPS)) {
                    v = u;
                    fv = fu;
                }
            }
        } while (!hasReachedAccuracy(x, xm, tol3) && ++iter < GLIMIT);
        if (iter == GLIMIT) {
            logger.warn("brent max iterations exceeded " + x);
        }
        return x;
    }

    /**
     * Calculate the vector x where f(x) is maximal, with a &lt; x &lt; b
     * with default precision.
     *
     * @param a   left border
     * @param b   right border
     * @param eps the accuracy of x
     * @return vector x with max_x f(x)
     */
    public double[] maximum(final double a, final double b, final double eps) {
        isInverting = true;
        return minimum(a, b, eps);
    }

    /**
     * Calculate the vector x where f(x) is minimal, with a  &lt; x &lt; b.
     *
     * @param a   left border
     * @param b   right border
     * @param eps the accuracy of x
     * @return vector x with min_x f(x)
     */
    public double[] minimum(final double a, final double b, final double eps) {
        final double z = brent(a, b, eps);
        final double[] x = vector(z);
        return x;
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
}
 