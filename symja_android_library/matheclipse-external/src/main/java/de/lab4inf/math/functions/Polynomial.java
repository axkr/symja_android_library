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

import java.util.Arrays;

import de.lab4inf.math.Differentiable;
import de.lab4inf.math.Function;
import de.lab4inf.math.Integrable;

import static java.lang.Math.max;

/**
 * Generic polynomial function.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Polynomial.java,v 1.12 2014/06/17 09:38:22 nwulff Exp $
 * @since 21.01.2005
 */
public class Polynomial extends L4MFunction implements Differentiable, Integrable {
    /**
     * the polynomial coefficients.
     */
    private final double[] coeff;
    /**
     * the degree of the polynomial.
     */
    private final int degree;
    /**
     * internal cached hashcode.
     */
    private int hashCode = 0;
    /**
     * the derivative.
     */
    private Polynomial derivative = null;
    /**
     * the anti derivative.
     */
    private Polynomial primitive = null;

    /**
     * Constructor with given coefficients.
     *
     * @param a the polynomial coefficients
     */
    public Polynomial(final double... a) {
        degree = a.length - 1;
        coeff = new double[degree + 1];
        System.arraycopy(a, 0, coeff, 0, degree + 1);
    }

    /**
     * Constructor with given coefficients.
     *
     * @param a the polynomial coefficients
     */
    public Polynomial(final Double... a) {
        degree = a.length - 1;
        coeff = new double[degree + 1];
        System.arraycopy(a, 0, coeff, 0, degree + 1);
    }

    /**
     * Multiplication of two polynomial.
     *
     * @param p1 first polynomial
     * @param p2 second polynomial
     * @return polynomial p1 times p2
     */
    public static Polynomial mult(final Polynomial p1, final Polynomial p2) {
        int d, p, q;
        int dP = p1.getDegree();
        int dQ = p2.getDegree();
        int degree = dP + dQ;
        double[] a = new double[degree + 1];
        for (p = 0; p <= dP; p++) {
            for (q = 0; q <= dQ; q++) {
                d = p + q;
                a[d] += p1.coeff[p] * p2.coeff[q];
            }
        }
        return new Polynomial(a);
    }

    /**
     * Division of two polynomial ignoring a possible remainder.
     *
     * @param p first polynomial
     * @param q second polynomial
     * @return quotient polynomial p / q
     */
    public static Polynomial divide(final Polynomial p, final Polynomial q) {
        int n = p.getDegree();
        double[] a, r = new double[n + 1];
        a = divide(p.coeff, q.coeff, r);
        return new Polynomial(a);
    }

    /**
     * Addition of two polynomial.
     *
     * @param p1 first polynomial
     * @param p2 second polynomial
     * @return polynomial p1 plus p2
     */
    public static Polynomial plus(final Polynomial p1, final Polynomial p2) {
        int p;
        int dP = p1.getDegree();
        int dQ = p2.getDegree();
        int degree = max(dP, dQ);
        double[] a = new double[degree + 1];
        for (p = 0; p <= degree; p++) {
            if (p <= dP && p <= dQ) {
                a[p] = p1.getCoefficient(p) + p2.getCoefficient(p);
            } else {
                if (p <= dP) {
                    a[p] = p1.getCoefficient(p);
                } else {
                    a[p] = p2.getCoefficient(p);
                }
            }
        }
        return new Polynomial(a);
    }

    /**
     * Polynomial division with quotient q(x) and remainder r(x), using only the
     * coefficients: a(x) = b(x)*q(x)+r(x) of the polynomials.
     * .
     *
     * @param a the dividend coefficients  0...n
     * @param b the divisor coefficients   0...m
     * @param r the remainder coefficients 0...n
     * @return polynomial coefficients q = a/b  0...(n-m)
     */
    public static double[] divide(final double[] a, final double[] b, final double[] r) {
        double[] q, x;
        int k, j;
        final int n = a.length - 1, nv = b.length - 1, m = n - nv;
        if (m < 0) {
            for (j = 0; j <= n; r[j] = a[j], j++)
                ;
            x = new double[]{0};
        } else {
            q = new double[n + 1];
            x = new double[m + 1];
            for (j = 0; j <= n; r[j] = a[j], q[j] = 0, j++)
                ;
            for (k = n - nv; k >= 0; k--)
                for (q[k] = r[nv + k] / b[nv], j = nv + k - 1; j >= k; r[j] -= q[k] * b[j - k], j--)
                    ;

            for (j = nv; j <= n; r[j] = 0, j++)
                ;
            for (j = 0; j <= m; x[j] = q[j], j++)
                ;
        }
        return x;
    }

    /**
     * Return the polynomial degree.
     *
     * @return degree
     */
    public int getDegree() {
        return degree;
    }

    /**
     * Return the coefficient a[k].
     *
     * @param k the index
     * @return coefficient a[k]
     */
    public double getCoefficient(final int k) {
        return coeff[k];
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double)
     */
    @Override
    public double f(final double... x) {
        double xx = x[0];
        double y = 0;
        for (int i = degree; i >= 0; i--) {
            y = y * xx + coeff[i];
        }
        return y;
    }

    /**
     * Return the derivative at point x.
     *
     * @param x the point
     * @return derivative value p'(x)
     */
    public double dF(final double x) {
        if (getDegree() > 0) {
            return getDerivative().f(x);
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (null == o)
            return false;
        if (this == o)
            return true;
        if (o instanceof Polynomial) {
            Polynomial p = (Polynomial) o;
            if (getDegree() != p.getDegree())
                return false;
            return Arrays.equals(coeff, p.coeff);
            // for (int d = 0; d<=getDegree(); d++) {
            // if (coeff[d]!=p.coeff[d]) {
            // return false;
            // }
            // }
            // return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (hashCode == 0) {
            // hashCode = Double.valueOf(coeff[0]).hashCode();
            // for (int d = 1; d<=getDegree(); d++) {
            // hashCode ^= Double.valueOf(coeff[d]).hashCode();
            // }
            hashCode = Arrays.hashCode(coeff);
        }
        return hashCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiable#getDerivative()
     */
    public Function getDerivative() {
        if (derivative == null && getDegree() >= 1) {
            double[] a = new double[getDegree()];
            for (int i = 1; i <= degree; i++) {
                a[i - 1] = i * coeff[i];
            }
            derivative = new Polynomial(a);
        }
        return derivative;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Integrable#getAntiderivative()
     */
    public Function getAntiderivative() {
        if (primitive == null) {
            double[] a = new double[getDegree() + 2];
            a[0] = 0;
            for (int i = 0; i <= degree; i++) {
                a[i + 1] = coeff[i] / (i + 1);
            }
            primitive = new Polynomial(a);
        }
        return primitive;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("P");
        sb.append(getDegree());
        sb.append(": (");
        for (int i = degree; i >= 0; i--) {
            sb.append(coeff[i]);
            if (i > 0) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
 