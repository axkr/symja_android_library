/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2010,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math.roots;

import java.util.Arrays;
import java.util.Comparator;

import de.lab4inf.math.Complex;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.Letters;
import de.lab4inf.math.functions.Polynomial;
import de.lab4inf.math.lapack.LinearAlgebra;
import de.lab4inf.math.util.Accuracy;

import static de.lab4inf.math.functions.Polynomial.divide;
import static de.lab4inf.math.util.Accuracy.round;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

/**
 * Find all complex roots of a real polynomial of degree n using the
 * Bairstow method.
 * <pre>
 * p_n(x) = a_n*x**n + a_n-1*x**(n-1) + ... + a_1*x + a_0
 * </pre>
 * There are exactly n complex or real roots.
 *
 * @author nwulff
 * @version $Id: BairstowRootFinder.java,v 1.12 2012/06/24 17:18:08 nwulff Exp $
 * @since 13.05.2010
 */
public class BairstowRootFinder extends L4MObject {
    /**
     * reference to the MAX_ITERATIONS attribute.
     */
    private static final int MAX_ITERATIONS = 2000;
    /**
     * Internal comparator to sort the roots.
     */
    private static final Comparator<Complex> CP = new Comparator<Complex>() {
        @Override
        public int compare(final Complex x, final Complex y) {
            int ret = 0;
            if (x.real() < y.real()) {
                ret = -1;
            } else if (x.real() > y.real()) {
                ret = 1;
            } else {
                if (x.imag() < y.imag()) {
                    ret = -1;
                } else if (x.imag() > y.imag()) {
                    ret = 1;
                }
            }
            return ret;
        }
    };
    private final Complex cmplx;
    private double eps = Accuracy.DEPS * 1E2;
    private double reps = sqrt(eps) * 10;
    private boolean sorted;

    /**
     * POJO constructor for unsorted roots.
     */
    public BairstowRootFinder() {
        this(false);
    }

    /**
     * Constructor indicating if the roots should be sorted
     * with increasing real and imaginary part.
     *
     * @param sort if true roots are sorted
     */
    public BairstowRootFinder(final boolean sort) {
        sorted = sort;
        cmplx = resolve(Complex.class);
    }

    /**
     * Find all roots p(x)=0 for the polynomial p of order n.
     * The n complex roots are coded as (real,imag) parts within a
     * 2n-dimensional array.
     *
     * @param p the polynomial
     * @return x 2n-dimensional array with all roots of p.
     */
    public double[] roots(final Polynomial p) {
        int n = p.getDegree();
        double[] coeff = new double[n + 1];
        for (int k = 0; k <= n; coeff[k] = p.getCoefficient(k), k++) ;
        return roots(coeff);
    }

    /**
     * Find all roots p(x)=0 for the polynomial p of order n.
     * The n complex roots are coded as (real,imag) parts within a
     * 2n-dimensional array.
     *
     * @param a the polynomial coefficients
     * @return x 2n-dimensional array with all roots of p.
     */
    public double[] roots(final double[] a) {
        double[] roots;
        roots = bairstowRoots(a);
        if (sorted) {
            roots = sort(roots);
        }
        return roots;
    }

    /**
     * Get the precision of the roots.
     *
     * @return the current precision
     */
    public double getEps() {
        return eps;
    }

    /**
     * Set the precision to aim for.
     *
     * @param eps the precision of the roots
     */
    public void setEps(final double eps) {
        this.eps = eps;
        reps = 10 * sqrt(eps);
    }

    /**
     * Solution of the linear equation y=a0+a1*x = 0.
     *
     * @param a the coefficients
     * @return the solution
     */
    private double linearRoot(final double... a) {
        if (a[1] != 0) {
            return -a[0] / a[1];
        }
        throw new IllegalArgumentException("constant polynom");
    }

    /**
     * Solution of the quadratic equation y=a0+a1*x+a2*x**2 = 0.
     *
     * @param a the coefficients
     * @return the solution
     */
    private double[] quadricRoots(final double... a) {
        double[] roots = new double[4];
        double p, q, d;
        if (a[2] == 0) {
            roots[0] = linearRoot(a);
        } else {
            p = a[1] / a[2];
            q = a[0] / a[2];
            d = p * p / 4 - q;
            p /= -2;
            if (d >= -reps) {  // two real roots
                d = sqrt(abs(d));
                roots[0] = p + d;
                roots[2] = p - d;
            } else {   // two conjugate complex roots
                d = sqrt(-d);
                roots[0] = p;
                roots[1] = d;
                roots[2] = p;
                roots[3] = -d;
            }
        }
        return roots;
    }

    /**
     * Solution of the generic equation y=a0+a1*x + ... + an*x**n= 0.
     *
     * @param a the coefficients
     * @return the solution
     */
    private double[] bairstowRoots(final double[] a) {
        int n = a.length - 1, j = 0;
        double[] roots = new double[2 * n];
        double[] z, b = LinearAlgebra.copy(a), r = new double[n + 1];

        while (n > 0) {
            if (n == 1) {
                roots[j++] = round(linearRoot(b), eps);
                n--;
            } else {
                z = b;
                if (n > 2) {
                    z = splitQuadric(b);
                    b = divide(b, z, r);
                    if (LinearAlgebra.maxnorm(r) > reps) {
                        getLogger().warn(Letters.RHO + ": " + LinearAlgebra.asString("%+.1e", r));
                    }
                }
                z = quadricRoots(z);
                n -= 2;
                for (int k = 0; k < 4; roots[j++] = round(z[k], eps), k++) ;
            }
        }
        return roots;
    }

    /**
     * Find a quadratic divisor q0+q1*x+x**2 of the polynomial a.
     *
     * @param a the polynomial
     * @return the quotient q
     */
    private double[] splitQuadric(final double[] a) {
        int i = 0, j, n = a.length - 1;
        double u, v, dv, du;
        double b0, b1, b2, f0, f1, f2, c, d, g, h, dis;
        u = a[n - 1] / a[n];
        v = a[n - 2] / a[n];
        do {
            for (b0 = 0, b1 = 0, f0 = 0, f1 = 0, j = n - 2; j >= 0; b2 = b1, b1 = b0, b0 = a[j + 2] - u * b1 - v * b2, f2 = f1, f1 = f0, f0 = b2 - u * f1 - v * f2, j--)
                ;

            c = a[1] - u * b0 - v * b1;
            d = a[0] - v * b0;
            g = b1 - u * f0 - v * f1;
            h = b0 - v * f0;
            dis = v * g * g + h * (h - u * g);
            du = (-h * c + g * d) / dis;
            dv = (-g * v * c + (g * u - h) * d) / dis;
             /*
             if(abs(dis)<eps*1E3) {
                 String msg = format("discriminant: %cu=%.1e %cv=%.1e %c=%.1e",
                         Letters.UPPER_DELTA,du,Letters.UPPER_DELTA,dv,Letters.UPPER_DELTA,dis);
                 getLogger().warn(msg);
             }
             */
            u -= du;
            v -= dv;
            dv = max(abs(du), abs(dv)); // error in (u,v)
            du = max(abs(c), abs(d));   // (c,d) should converge to zero
        } while (++i < MAX_ITERATIONS && (dv > eps));
        if (i >= MAX_ITERATIONS) {
            String msg = format("poor convergence: %cu=%.1e %cv=%.1e", Letters.UPPER_DELTA, du, Letters.UPPER_DELTA, dv);
            getLogger().warning(msg);
            //throw new IllegalStateException("no convergence");
        }
        return new double[]{v, u, 1};
    }

    /**
     * Sort the array of complex coefficients
     *
     * @param x the array
     * @return the sorted array
     */
    private double[] sort(final double[] x) {
        int n = x.length;
        Complex[] c = new Complex[n / 2];
        for (int i = 0; i < n / 2; i++) {
            c[i] = cmplx.newComplex(x[2 * i], x[2 * i + 1]);
        }
        Arrays.sort(c, CP);
        for (int i = 0; i < n / 2; i++) {
            x[2 * i] = c[i].real();
            x[2 * i + 1] = c[i].imag();
        }
        return x;
    }
}
 