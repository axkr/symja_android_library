/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2011,  Prof. Dr. Nikolaus Wulff
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

import de.lab4inf.math.CFunction;
import de.lab4inf.math.Complex;
import de.lab4inf.math.sets.ComplexNumber;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Function based on the Fourier coefficients a[j] and/or b[j].
 * <p>
 * <pre>
 * f(x) = &sum; a[j]*cos(&omega;*j*x) + b[j]*sin(&omega;*j*x)
 *        j
 * </pre>
 * or in the complex domain
 * <p>
 * <pre>
 * f(z) = &sum; a[j]*cos(&omega;*j*re(z)) + j b[j]*sin(&omega;*j*im(z))
 *        j
 * </pre>
 *
 * @author nwulff
 * @version $Id: FFTFunction.java,v 1.6 2013/02/17 17:21:59 nwulff Exp $
 * @since 16.02.2011
 */
public class FFTFunction extends L4MFunction implements CFunction {
    private double[] a, b;
    private double omega;
    private int n;

    /**
     * Constructor for a FFT function with a pitch frequency of one.
     * In case odd is true it will be a sine otherwise a cosine FFT function.
     *
     * @param odd flag indicating if sine or cosine FFT.
     * @param c   the coefficients
     */
    public FFTFunction(final boolean odd, final double[] c) {
        this(1, odd, c);
    }

    /**
     * Constructor for a FFT function with a pitch frequency of v.
     * In case odd is true it will be a sine otherwise a cosine FFT function.
     *
     * @param v   the pitch frequency
     * @param odd flag indicating if sine or cosine FFT.
     * @param c   the coefficients
     */
    public FFTFunction(final double v, final boolean odd, final double[] c) {
        if (odd) {
            b = c.clone();
        } else {
            a = c.clone();
        }
        omega = v * 2 * PI;
        n = c.length;
    }

    /**
     * Constructor for a FFT function with a pitch frequency of one.
     *
     * @param a the even cosine coefficients
     * @param b the odd sine coefficients
     */
    public FFTFunction(final double[] a, final double[] b) {
        this(1, a, b);
    }

    /**
     * Constructor for a FFT function with a pitch frequency of v.
     *
     * @param v the pitch frequency
     * @param a the even cosine coefficients
     * @param b the odd sine coefficients
     */
    public FFTFunction(final double v, final double[] a, final double[] b) {
        this.a = a.clone();
        this.b = b.clone();
        n = a.length;
        omega = v * 2 * PI;
        if (b.length != n) {
            throw new IllegalArgumentException("a != b");
        }
    }

    /**
     * Return the even coefficient a[k].
     *
     * @param k the index
     * @return a[k]
     */
    public double getA(final int k) {
        return a[k];
    }

    /**
     * Return the odd coefficient b[k].
     *
     * @param k the index
     * @return b[k]
     */
    public double getB(final int k) {
        return b[k];
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return f(x[0]);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
     */
    @Override
    public Complex f(final Complex... z) {
        return f(z[0]);
    }

    /**
     * Calculate the function at argument z=2*PI*v*t.
     *
     * @param t the time value
     * @return f(z)
     */
    public double f(final double t) {
        double z = omega * t;
        double signal = 0;
        boolean even = (null != a);
        boolean oddeven = even && (null != b);
        if (oddeven) {
            signal = a[0];
            for (int k = 1; k < n; k++) {
                signal += a[k] * cos(z * k);
                signal += b[k] * sin(z * k);
            }
        } else {
            if (even) {
                signal = a[0];
                for (int k = 1; k < n; k++) {
                    signal += a[k] * cos(z * k);
                }
            } else {
                for (int k = 1; k < n; k++) {
                    signal += b[k] * sin(z * k);
                }
            }
        }
        return signal;
    }

    /**
     * Calculate the function at argument z.
     *
     * @param z the argument
     * @return f(z)
     */
    public Complex f(final Complex z) {
        double real = 0, imag = 0, o = 2 * PI / n;
        double c, s;
        for (int k = 0; k < n; k++) {
            c = cos(k * o * z.real());
            s = sin(k * o * z.imag());
            real += a[k] * c - b[k] * s;
            imag += a[k] * s + b[k] * c;
        }
        return new ComplexNumber(real, imag);
    }

}
 