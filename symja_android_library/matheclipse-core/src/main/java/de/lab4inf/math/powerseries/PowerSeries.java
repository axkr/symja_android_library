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

package de.lab4inf.math.powerseries;

import java.util.Arrays;

import de.lab4inf.math.Differentiable;
import de.lab4inf.math.Field;
import de.lab4inf.math.Function;
import de.lab4inf.math.Integrable;
import de.lab4inf.math.Operand;
import de.lab4inf.math.functions.L4MFunction;
import de.lab4inf.math.lapack.LinearAlgebra;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;

/**
 * Truncated power series of order n.
 * <pre>
 *           n
 *  p(x) := sum p_j x**j
 *          j=0
 * </pre>
 * <p>
 * Unlike an ordinary polynomial this class offers the algebraic properties of a
 * mathematical ring and with the exception of series truncation the division is
 * the inverse of the multiplication operation.
 * <p>
 * Further there are special methods like the sin, cosine or exponentiation or
 * root of a power series defined via:
 * <pre>
 *
 *  exp(p(x)+q(x)) = exp(p(x) * exp(q(x)) for all p,q as power series
 *  log(p(x)*q(x)) = log(p(x) + log(q(x)) for all p,q as power series
 *
 *  q(x) = log(p(x))  &larr; p(x) = exp(q(x))
 *
 *  q(x) = sqrt(p(x)) &larr; p(x) = q(x)*q(x)
 *
 *  etc...
 * </pre>
 * To derive further none straightforward formulae for a power function f
 * use the derivative chain rule:
 * <pre>
 *  q(x) := f(p(x)) then  q'(x) = df/dp*p'(x)
 * </pre>
 * and do a recursive power by power comparison.
 * E.g. for the exp power series:
 * <pre>
 *   q(x) := exp(p(x))
 *
 *   q'(x) = exp'(p(x)) = exp(p(x))*p'(x)
 *
 *  q_1 + 2*q_2*x + ... = (q_0 + q_1*x + ...)*(p_1 + 2*p_2*x + ...)
 *
 *  &ge; q_0 := exp(p_0) initial start and than recursive via
 *
 *     q_1 = p_1*q_0 ,
 *   2*q_2 = p_1*q_1 + 2*p_2*q_0
 *        ...
 *   n*q_n = sum_k=1 to n  k*p_k*q[n-k]
 * </pre>
 *
 * @author nwulff
 * @version $Id: PowerSeries.java,v 1.21 2014/11/16 21:47:23 nwulff Exp $
 * @since 22.09.2009
 */
public class PowerSeries extends L4MFunction implements Differentiable, Integrable, Field<PowerSeries> {
    /**
     * smallest numerical difference between two coefficients.
     */
    private static final double TINY = 1.E-12;
    private final double[] a;

    /**
     * Sole constructor.
     */
    protected PowerSeries() {
        a = new double[0];
    }

    /**
     * Constructor with given coefficients.
     *
     * @param coeff the power series coefficients
     */
    public PowerSeries(final double... coeff) {
        a = coeff.clone();
    }

    /**
     * Distance between two power series, building block for a metric space.
     * Let k be the index of the first different coefficient than the
     * distance is 2**(-k). If all coefficients are the same the distance
     * is zero.
     *
     * @param x first series
     * @param y second series
     * @return distance(x, y)
     */
    public static double distance(final PowerSeries x, final PowerSeries y) {
        int k = 0;
        final int n = min(y.getSize(), x.getSize());
        final int m = max(y.getSize(), x.getSize());
        for (k = 0; k < n; k++) {
            if (abs(y.a[k] - x.a[k]) > TINY) {
                return Math.pow(2, -k);
            }
        }
        if (m != n) {
            return Math.pow(2, -(n + 1));
        }
        return 0;
    }

    /**
     * Calculate exp(p) for the given series p(x).
     *
     * @param p input series
     * @return exp(p) series
     */
    public static PowerSeries exp(final PowerSeries p) {
        final int n = p.getSize();
        final double[] e = new double[n];
        final double[] a = p.a;
        e[0] = Math.exp(a[0]);
        for (int j = 1; j < n; j++) {
            for (int k = 1; k <= j; k++) {
                e[j] += k * a[k] * e[j - k];
            }
            e[j] /= j;
        }
        return new PowerSeries(e);
    }

    /**
     * Calculate log(p) for the given series p(x).
     *
     * @param p input series
     * @return log(p) series
     */
    public static PowerSeries log(final PowerSeries p) {
        final int n = p.getSize();
        final double[] l = new double[n];
        final double[] a = p.a;
        l[0] = Math.log(a[0]);
        for (int j = 1; j < n; j++) {
            l[j] = j * a[j];
            for (int k = 1; k < j; k++) {
                l[j] -= k * l[k] * a[j - k];
            }
            l[j] /= j * a[0];
        }
        return new PowerSeries(l);
    }

    /**
     * Calculate sin(p) and cos(p) for the given series p(x) at once.
     * The arrays c and s have to be allocated appropiate.
     *
     * @param p input series
     * @param c coefficients for the cosine series
     * @param s coefficients for the sine series
     */
    public static void sincos(final PowerSeries p, final double[] c, final double[] s) {
        final int n = p.getSize();
        final double[] a = p.a;
        s[0] = Math.sin(a[0]);
        c[0] = Math.cos(a[0]);
        for (int j = 1; j < n; j++) {
            s[j] = a[1] * c[j - 1];
            c[j] = a[1] * s[j - 1];
            for (int k = 2; k <= j; k++) {
                s[j] += k * a[k] * c[j - k];
                c[j] += k * a[k] * s[j - k];
            }
            s[j] /= j;
            c[j] /= -j;
        }
    }

    /**
     * Calculate sin(p) for the given series p(x).
     *
     * @param p input series
     * @return sin(p) series
     */
    public static PowerSeries sin(final PowerSeries p) {
        final int n = p.getSize();
        final double[] s = new double[n];
        final double[] c = new double[n];
        sincos(p, c, s);
        return new PowerSeries(s);
    }

    /**
     * Calculate cos(p) for the given series p(x).
     *
     * @param p input series
     * @return cos(p) series
     */
    public static PowerSeries cos(final PowerSeries p) {
        final int n = p.getSize();
        final double[] s = new double[n];
        final double[] c = new double[n];
        sincos(p, c, s);
        return new PowerSeries(c);
    }

    /**
     * Calculate tan(p) for the given series p(x).
     *
     * @param p input series
     * @return tan(p) series
     */
    public static PowerSeries tan(final PowerSeries p) {
        final int n = p.getSize();
        final double[] s = new double[n];
        final double[] c = new double[n];
        final double[] t = new double[n];
        sincos(p, c, s);
        t[0] = s[0] / c[0];
        for (int j = 1; j < n; j++) {
            t[j] = s[j];
            for (int k = 0; k < j; k++) {
                t[j] -= t[k] * c[j - k];
            }
            t[j] /= c[0];
        }
        return new PowerSeries(t);
    }

    /**
     * Calculate cotan(p) for the given series p(x).
     *
     * @param p input series
     * @return cotan(p) series
     */
    public static PowerSeries cotan(final PowerSeries p) {
        final int n = p.getSize();
        final double[] s = new double[n];
        final double[] c = new double[n];
        final double[] ct = new double[n];
        sincos(p, c, s);
        ct[0] = c[0] / s[0];
        for (int j = 1; j < n; j++) {
            ct[j] = c[j];
            for (int k = 0; k < j; k++) {
                ct[j] -= ct[k] * s[j - k];
            }
            ct[j] /= s[0];
        }
        return new PowerSeries(ct);
    }

    /**
     * Calculate the p-th power of the given series z(x).
     *
     * @param z input series
     * @param p the power
     * @return z**p series
     */
    public static PowerSeries pow(final PowerSeries z, final double p) {
        final int n = z.getSize();
        final double[] a = z.a;
        final double[] r = new double[n];
        r[0] = Math.pow(a[0], p);
        for (int i = 1; i < n; i++) {
            r[i] = i * p * a[i] * r[0];
            for (int k = 1; k < i; k++) {
                r[i] += k * (p * a[k] * r[i - k] - r[k] * a[i - k]);
            }
            r[i] /= i * a[0];
        }
        return new PowerSeries(r);
    }

    /**
     * Calculate the root of the given series z(x).
     *
     * @param z input series
     * @return sqrt(z) series
     */
    public static PowerSeries sqrt(final PowerSeries z) {
        final int n = z.getSize();
        final double[] a = z.a;
        final double[] r = new double[n];
        r[0] = Math.sqrt(a[0]);
        for (int i = 1; i < n; i++) {
            r[i] = a[i];
            for (int k = 1; k < i; k++) {
                r[i] -= r[k] * r[i - k];
            }
            r[i] /= 2 * r[0];
        }
        return new PowerSeries(r);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiable#getDerivative()
     */
    @Override
    public Function getDerivative() {
        return derivative();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Integrable#getAntiderivative()
     */
    @Override
    public Function getAntiderivative() {
        return primitive();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Group#isZero()
     */
    @Override
    public boolean isZero() {
        final int n = a.length;
        for (int j = 0; j < n; j++) {
            if (a[j] != 0)
                return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Ring#isOne()
     */
    @Override
    public boolean isOne() {
        final int n = a.length;
        if (n >= 1 && a[0] == 1) {
            for (int j = 1; j < a.length; j++) {
                if (a[j] != 0)
                    return false;
            }
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final PowerSeries ps = (PowerSeries) obj;
        return Arrays.equals(a, ps.a);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(a);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final String fmt = "%g";
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < a.length; i++) {
            if (i == 0)
                sb.append(format(fmt, a[i]));
            else
                sb.append(format(fmt, abs(a[i])));
            if (i == 1)
                sb.append("*x");
            else if (i > 1)
                sb.append(format("*x**%d", i));

            if (i < a.length - 1) {
                if (a[i + 1] >= 0)
                    sb.append('+');
                else
                    sb.append('-');
            }
        }
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        final double t = x[0];
        double y = 0;
        for (int i = a.length - 1; i >= 0; i--) {
            y = y * t + a[i];
        }
        return y;
    }

    /**
     * Addition of two power series.
     *
     * @param x the series to add
     * @return this + x
     */
    @Override
    @Operand(symbol = "+")
    public PowerSeries plus(final PowerSeries x) {
        final double[] b = x.a;
        final int nb = b.length;
        final int na = a.length;
        final int n = max(na, nb);
        final int m = min(na, nb);
        final double[] c = new double[n];

        for (int j = 0; j < m; j++) {
            c[j] = a[j] + b[j];
        }
        if (na < nb) {
            for (int j = m; j < n; j++) {
                c[j] = b[j];
            }
        } else {
            for (int j = m; j < n; j++) {
                c[j] = a[j];
            }
        }
        return new PowerSeries(c);
    }

    /**
     * Subtraction of two power series.
     *
     * @param x the series to subtract
     * @return this - x
     */
    @Override
    @Operand(symbol = "-")
    public PowerSeries minus(final PowerSeries x) {
        final double[] b = x.a;
        final int nb = b.length;
        final int na = a.length;
        final int n = max(na, nb);
        final int m = min(na, nb);
        final double[] c = new double[n];

        for (int j = 0; j < m; j++) {
            c[j] = a[j] - b[j];
        }
        if (na < nb) {
            for (int j = m; j < n; j++) {
                c[j] = -b[j];
            }
        } else {
            for (int j = m; j < n; j++) {
                c[j] = a[j];
            }
        }
        return new PowerSeries(c);
    }

    /**
     * Multiplication of two power series.
     *
     * @param x the series to multiply with
     * @return this * x
     */
    @Override
    @Operand(symbol = "*")
    public PowerSeries multiply(final PowerSeries x) {
        final double[] b = x.a;
        final int nb = b.length;
        final int na = a.length;
        // int n = na + nb - 1;
        final int n = min(na, nb);
        final double[] c = new double[n];
        for (int j = 0; j < n; j++) {
            for (int k = 0; k <= j; k++) {
                if ((j - k < na) && (k < nb)) {
                    c[j] += a[j - k] * b[k];
                }
            }
        }
        return new PowerSeries(c);
    }

    /**
     * Multiplication of a power series by a scalar.
     *
     * @param x the scalar to multiply with
     * @return this * x
     */
    @Operand(symbol = "*")
    public PowerSeries multiply(final double x) {
        final double[] c = LinearAlgebra.mult(a, x);
        return new PowerSeries(c);
    }

    /**
     * Division of two power series, truncated at the lowest degree
     * of both series.
     *
     * @param x the series to divide with
     * @return this / x
     */
    @Operand(symbol = "/")
    public PowerSeries divide(final PowerSeries x) {
        final double[] b = x.a;
        final int nb = b.length;
        final int na = a.length;
        final int n = min(na, nb);
        final double[] c = new double[n];

        for (int j = 0; j < n; j++) {
            c[j] = a[j];
            for (int k = 0; k < j; k++) {
                c[j] -= c[k] * b[j - k];
            }
            c[j] /= b[0];
        }
        return new PowerSeries(c);
    }

    /**
     * Division of two power series, truncated at the lowest degree
     * of both series.
     *
     * @param x the series to divide with
     * @return this / x
     */
    @Override
    @Operand(symbol = "/")
    public PowerSeries div(final PowerSeries x) {
        return divide(x);
    }

    /**
     * Division of a power series by a scalar.
     *
     * @param x scalar do divide by
     * @return this/x
     */
    @Operand(symbol = "/")
    public PowerSeries divide(final double x) {
        final double[] c = LinearAlgebra.mult(a, 1. / x);
        return new PowerSeries(c);
    }

    /**
     * Division of a power series by a scalar.
     *
     * @param x scalar do divide by
     * @return this/x
     */
    @Operand(symbol = "/")
    public PowerSeries div(final double x) {
        return divide(x);
    }

    /**
     * Size of the truncated series
     *
     * @return truncation index
     */
    public int getSize() {
        return a.length;
    }

    /**
     * Get the coefficient value with index j.
     *
     * @param j index
     * @return p[j]
     */
    public double getCoeff(final int j) {
        return a[j];
    }

    /**
     * Calculate the derivative of this series.
     *
     * @return p'(x)
     */
    public PowerSeries derivative() {
        final int n = a.length - 1;
        final double[] p = new double[n];
        for (int j = 0; j < n; j++) {
            p[j] = (j + 1) * a[j + 1];
        }
        return new PowerSeries(p);
    }

    /**
     * Calculate the k-th derivative of this series.
     *
     * @param k the order
     * @return dp(x)/dx**k
     */
    public PowerSeries derivative(final int k) {
        double[] p = {0};
        final int n = a.length - k;
        if (n > 0) {
            p = new double[n];
            double v;
            for (int j = 0; j < n; j++) {
                v = j + 1;
                for (int i = 2; i <= k; i++) {
                    v *= (j + i);
                }
                p[j] = v * a[j + k];
            }
        }
        return new PowerSeries(p);
    }

    /**
     * Calculate the anti-derivative/primitive of this series.
     * This is unique up to the constant p_0 set to zero.
     *
     * @return P(x)
     */
    public PowerSeries primitive() {
        final int n = a.length + 1;
        final double[] p = new double[n];
        for (int j = 1; j < n; j++) {
            p[j] = a[j - 1] / j;
        }

        return new PowerSeries(p);
    }

}
 