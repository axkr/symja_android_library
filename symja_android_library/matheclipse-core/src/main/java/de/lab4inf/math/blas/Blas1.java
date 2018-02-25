/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2013,  Prof. Dr. Nikolaus Wulff
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
package de.lab4inf.math.blas;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

/**
 * <h1> BLAS I </h1>
 * <p>
 * Basic Linear Algebra Subroutines Level 1.
 * Performs basic vector-scalar multiplications and calculates vector norms
 * <pre>
 *
 *   axpy:   y  &larr;  &alpha; * x +  y
 *   scale:  x  &larr;  &alpha; * x
 *   swap:   x  &harr; y
 *   nrm2:   || x ||
 *   dot :   x * y
 *
 * </pre>
 * with optional increments in x and/or y.
 *
 * @author nwulff
 * @version $Id: Blas1.java,v 1.14 2014/11/16 21:47:23 nwulff Exp $
 * @since 27.01.2013
 */
public final class Blas1 extends BasicBlas {
    private static final String LENGTH_MISSMATCH = " length missmatch";
    private static final String VECTOR_LENGTH_MISSMATCH = " vector length missmatch";

    /**
     * Hidden constructor for a utility class.
     */
    private Blas1() {
    }

    /**
     * Perform  y = a*x + y.
     *
     * @param n    length to use
     * @param a    scalar to scale with
     * @param x    vector to add
     * @param incX increment in x
     * @param y    updated vector
     * @param incY increment in y
     */
    public static void daxpy(final int n, final double a, final double[] x, final int incX, final double[] y,
                             final int incY) {
        final int xlen = x.length;
        final int ylen = y.length;
        int j;
        assert xlen == ylen : VECTOR_LENGTH_MISSMATCH;
        assert n <= min(xlen, ylen) : LENGTH_MISSMATCH;

        if (incX == 1 && incY == 1) {
            final int m = n % 4;
            for (j = 0; j < m; y[j] += a * x[j], j++)
                ;
            for (j = m; j < n; j += 4) {
                y[j] += a * x[j];
                y[j + 1] += a * x[j + 1];
                y[j + 2] += a * x[j + 2];
                y[j + 3] += a * x[j + 3];
            }
        } else {
            int ix = 0, iy = 0;
            if (incX < 0)
                ix = -(n - 1) * incX;
            if (incY < 0)
                iy = -(n - 1) * incY;
            for (j = 0; (j < n) && (ix < xlen) && (iy < ylen); y[iy] += a * x[ix], ix += incX, iy += incY, j++)
                ;
        }
    }

    /**
     * Perform  y = a*x + y.
     *
     * @param n length to use
     * @param a scalar to scale with
     * @param x vector to add
     * @param y updated vector
     */
    public static void daxpy(final int n, final double a, final double[] x, final double[] y) {
        daxpy(n, a, x, 1, y, 1);
    }

    /**
     * Perform  y = a*x + y.
     *
     * @param a scalar to scale with
     * @param x vector to add
     * @param y updated vector
     */
    public static void daxpy(final double a, final double[] x, final double[] y) {
        final int xlen = x.length;
        final int m = xlen % 4;
        assert xlen == y.length : VECTOR_LENGTH_MISSMATCH;
        int j;
        if (a == 1) {
            for (j = 0; j < m; y[j] += x[j], j++)
                ;
            for (j = m; j < xlen; y[j] += x[j], y[j + 1] += x[j + 1], y[j + 2] += x[j + 2], y[j + 3] += x[j + 3], j += 4)
                ;
        } else if (a == -1) {
            for (j = 0; j < m; y[j] -= x[j], j++)
                ;
            for (j = m; j < xlen; y[j] -= x[j], y[j + 1] -= x[j + 1], y[j + 2] -= x[j + 2], y[j + 3] -= x[j + 3], j += 4)
                ;
        } else {
            for (j = 0; j < m; y[j] += a * x[j], j++)
                ;
            for (j = m; j < xlen; y[j] += a * x[j], y[j + 1] += a * x[j + 1], y[j + 2] += a * x[j + 2], y[j + 3] += a * x[j + 3], j += 4)
                ;
        }
    }

    /**
     * Calculate the absolute sum of the vector elements of x with given
     * increment and length, e.g. the maximum norm.
     *
     * @param n    length to use
     * @param x    input vector
     * @param incX increment to use
     * @return max norm of x
     */
    public static double dasum(final int n, final double[] x, final int incX) {
        int j;
        final int m = n % 4;
        double asum = 0;
        if (incX == 1) {
            for (j = 0; j < m; asum += abs(x[j]), j++)
                ;
            for (j = m; j < n; asum += abs(x[j]) + abs(x[j + 1]) + abs(x[j + 2]) + abs(x[j + 3]), j += 4)
                ;
        } else {
            for (j = 0; j < n; asum += abs(x[j]), j += incX)
                ;
        }
        return asum;
    }

    /**
     * Calculate the maximum norm of vector x.
     *
     * @param n length to use
     * @param x input vector
     * @return max norm of x
     */
    public static double dasum(final int n, final double[] x) {
        return dasum(n, x, 1);
    }

    /**
     * Calculate the maximum norm of vector x.
     *
     * @param x input vector
     * @return max norm of x
     */
    public static double dasum(final double[] x) {
        return dasum(x.length, x, 1);
    }

    /**
     * Copy vector x to y using the increments.
     *
     * @param n    length to use
     * @param x    source vector
     * @param incX increment in x
     * @param y    destination vector
     * @param incY increment in y
     */
    public static void dcopy(final int n, final double[] x, final int incX, final double[] y, final int incY) {
        final int xlen = x.length;
        final int ylen = y.length;
        assert xlen == ylen : VECTOR_LENGTH_MISSMATCH;
        assert n <= min(xlen, ylen) : LENGTH_MISSMATCH;
        if (incX == 1 && incY == 1) {
            System.arraycopy(x, 0, y, 0, n);
        } else {
            int ix = 0, iy = 0;
            if (incX < 0)
                ix = -(n - 1) * incX;
            if (incY < 0)
                iy = -(n - 1) * incY;
            for (int j = 0; (j < n) && (ix < xlen) && (iy < ylen); y[iy] = x[ix], ix += incX, iy += incY, j++)
                ;
        }
    }

    /**
     * Copy vector x to y.
     *
     * @param x source vector
     * @param y destination vector
     */
    public static void dcopy(final double[] x, final double[] y) {
        dcopy(x.length, x, 1, y, 1);
    }

    /**
     * Dot product of vector x and y with given increments.
     *
     * @param n    the maximal length to use
     * @param x    first vector
     * @param incX increment x
     * @param y    second vector
     * @param incY increment y
     * @return x*y
     */
    public static double ddot(final int n, final double[] x, final int incX, final double[] y, final int incY) {
        final int xlen = x.length;
        final int ylen = y.length;
        int j;
        final int m = n % 4;
        // if(n != y.length) throw new IllegalArgumentException(VECTOR_LENGTH_MISSMATCH);
        // assert xlen == ylen : VECTOR_LENGTH_MISSMATCH;
        // assert n <= min(xlen,ylen) : LENGTH_MISSMATCH;
        double tmp = 0;
        if (incX == 1 && incY == 1) {
            for (j = 0; j < m; tmp += x[j] * y[j], j++)
                ;
            for (j = m; j < n; tmp += y[j] * x[j] + y[j + 1] * x[j + 1] + y[j + 2] * x[j + 2] + y[j + 3] * x[j + 3], j += 4)
                ;
        } else {
            int ix = 0, iy = 0;
            if (incX < 0)
                ix = -(n - 1) * incX;
            if (incY < 0)
                iy = -(n - 1) * incY;
            for (j = 0; (j < n) && (ix < xlen) && (iy < ylen); tmp += y[iy] * x[ix], ix += incX, iy += incY, j++)
                ;
        }
        return tmp;
    }

    /**
     * Dot product of vector x and y.
     *
     * @param n the maximal length to use
     * @param x first vector
     * @param y second vector
     * @return x*y
     */
    public static double ddot(final int n, final double[] x, final double[] y) {
        return ddot(n, x, 1, y, 1);
    }

    /**
     * Dot product of vector x and y with increment one.
     *
     * @param x first vector
     * @param y second vector
     * @return x*y
     */
    public static double ddot(final double[] x, final double[] y) {
        final int n = x.length, m = n % 4;
        int j;
        if (n != y.length)
            throw new IllegalArgumentException(VECTOR_LENGTH_MISSMATCH);
        double tmp = 0;
        for (j = 0; j < m; tmp += x[j] * y[j], j++)
            ;
        for (j = m; j < n; tmp += y[j] * x[j] + y[j + 1] * x[j + 1] + y[j + 2] * x[j + 2] + y[j + 3] * x[j + 3], j += 4)
            ;
        return tmp;
    }

    /**
     * Dot product of vector x and y with given increments and start positions.
     *
     * @param n      the number of elements to multiply
     * @param xstart first x element
     * @param x      first vector
     * @param incx   the increment in x
     * @param ystart first y element
     * @param y      second vector
     * @param incy   the increment in y
     * @return x*y
     */
    public static double ddot(final int n, final int xstart, final double[] x, final int incx, final int ystart,
                              final double[] y, final int incy) {
        int jx = xstart, jy = ystart, max;
        double tmp = 0;
        if (incx == 1 && incy == 1) {
            max = n + xstart;
            for (; jx < max; tmp += x[jx] * y[jy], jx++, jy++)
                ;
        } else if (incx == 1) {
            max = n + xstart;
            for (; jx < max; tmp += x[jx] * y[jy], jx++, jy += incy)
                ;
        } else if (incy == 1) {
            max = n + ystart;
            for (; jy < max; tmp += x[jx] * y[jy], jx += incx, jy++)
                ;
        } else {
            max = xstart + n * incx;
            for (; jx < max; tmp += x[jx] * y[jy], jx += incx, jy += incy)
                ;
        }
        return tmp;
    }

    /**
     * Calculate the euclidean norm of vector x with given increment and length.
     *
     * @param n    length to use
     * @param x    input vector
     * @param incX increment to use
     * @return ||x||
     */
    public static double dnrm2(final int n, final double[] x, final int incX) {
        int j;
        double maxx = 0, ax, dx, ssq = 1;
        if (n < 1 || incX < 1)
            return 0;
        if (n == 1)
            return abs(x[0]);
        for (j = 0; j < n; j += incX) {
            if (x[j] != 0) {
                ax = abs(x[j]);
                if (maxx < ax) {
                    dx = maxx / ax;
                    maxx = ax;
                    // ssq = 1 + ssq*dx*dx;
                    ssq *= dx * dx;
                    ssq += 1;
                } else {
                    dx = ax / maxx;
                    ssq += dx * dx;
                }
            }
        }
        return maxx * sqrt(ssq);
    }

    /**
     * Calculate the euclidean norm of vector x with given increment.
     *
     * @param x    input vector
     * @param incX increment to use
     * @return ||x||
     */
    public static double dnrm2(final double[] x, final int incX) {
        return dnrm2(x.length, x, incX);
    }

    /**
     * Calculate the euclidean norm of vector x.
     *
     * @param x input vector
     * @return || x ||
     */
    public static double dnrm2(final double[] x) {
        return dnrm2(x.length, x, 1);
    }

    /**
     * Apply a plane rotation for x and y, with given cosine, sine values.
     *
     * @param n    maximal length to use
     * @param x    first vector
     * @param incX increment to use
     * @param y    second vector
     * @param incY increment to use
     * @param c    cosine value
     * @param s    sine value
     */
    public static void drot(final int n, final double[] x, final int incX, final double[] y, final int incY,
                            final double c, final double s) {
        final int xlen = x.length;
        final int ylen = y.length;
        int j;
        double tmp;
        assert xlen == ylen : VECTOR_LENGTH_MISSMATCH;
        assert n <= min(xlen, ylen) : LENGTH_MISSMATCH;
        if (incX == 1 && incY == 1) {
            for (j = 0; j < n; j++) {
                tmp = c * x[j] + s * y[j];
                y[j] = c * y[j] - s * x[j];
                x[j] = tmp;
            }
        } else {
            int ix = 0, iy = 0;
            if (incX < 0)
                ix = -(n - 1) * incX;
            if (incY < 0)
                iy = -(n - 1) * incY;
            for (j = 0; (j < n) && (ix < xlen) && (iy < ylen); ix += incX, iy += incY, j++) {
                tmp = c * x[ix] + s * y[iy];
                y[iy] = c * y[iy] - s * x[ix];
                x[ix] = tmp;
            }
        }
    }

    /**
     * Apply a plane rotation for x and y, with given cosine, sine values.
     *
     * @param n maximal length to use
     * @param x first vector
     * @param y second vector
     * @param c cosine value
     * @param s sine value
     */
    public static void drot(final int n, final double[] x, final double[] y, final double c, final double s) {
        drot(n, x, 1, y, 1, c, s);
    }

    /**
     * Apply a plane rotation for x and y, with given cosine, sine values.
     *
     * @param x first vector
     * @param y second vector
     * @param c cosine value
     * @param s sine value
     */
    public static void drot(final double[] x, final double[] y, final double c, final double s) {
        drot(x.length, x, 1, y, 1, c, s);
    }

    /**
     * Scale  x = a*x with given increment.
     *
     * @param n    number of x entries
     * @param a    to scale with
     * @param x    input/output vector
     * @param incX increment to use
     */
    public static void dscal(final int n, final double a, final double[] x, final int incX) {
        int j;
        final int m = n % 4;
        if (incX == 1) {
            for (j = 0; j < m; x[j] *= a, j++)
                ;
            for (j = m; j < n; j += 4) {
                x[j] *= a;
                x[j + 1] *= a;
                x[j + 2] *= a;
                x[j + 3] *= a;
            }
        } else {
            if (incX < 0 || n < 0)
                return;
            for (j = 0; j < n; x[j] *= a, j += incX)
                ;
        }
    }

    /**
     * Scale  x = a*x.
     *
     * @param n number of x entries
     * @param a to scale with
     * @param x input/output vector
     */
    public static void dscal(final int n, final double a, final double[] x) {
        dscal(n, a, x, 1);
    }

    /**
     * Scale  x = a*x.
     *
     * @param a to scale with
     * @param x input/output vector
     */
    public static void dscal(final double a, final double[] x) {
        dscal(x.length, a, x, 1);
    }

    /**
     * Exchange the two given vectors with increments.
     *
     * @param n    length to use
     * @param x    source  vector
     * @param incX increment to use
     * @param y    destination vector
     * @param incY increment to use
     */
    public static void dswap(final int n, final double[] x, final int incX, final double[] y, final int incY) {
        final int xlen = x.length;
        final int ylen = y.length;
        int j;
        final int m = n % 4;
        double tmp;
        assert xlen == ylen : VECTOR_LENGTH_MISSMATCH;
        assert n <= min(xlen, ylen) : LENGTH_MISSMATCH;
        if (incX == 1 && incY == 1) {
            for (j = 0; j < m; tmp = y[j], y[j] = x[j], x[j] = tmp, j++)
                ;
            for (j = m; j < n; j += 4) {
                tmp = y[j];
                y[j] = x[j];
                x[j] = tmp;
                tmp = y[j + 1];
                y[j + 1] = x[j + 1];
                x[j + 1] = tmp;
                tmp = y[j + 2];
                y[j + 2] = x[j + 2];
                x[j + 2] = tmp;
                tmp = y[j + 3];
                y[j + 3] = x[j + 3];
                x[j + 3] = tmp;
            }
        } else {
            int ix = 0, iy = 0;
            if (incX < 0)
                ix = -(n - 1) * incX;
            if (incY < 0)
                iy = -(n - 1) * incY;
            for (j = 0; (j < n) && (ix < xlen) && (iy < ylen); tmp = y[iy], y[iy] = x[ix], x[ix] = tmp, ix += incX, iy += incY, j++)
                ;
        }
    }

    /**
     * Exchange the two given vectors.
     *
     * @param n length to use
     * @param x source  vector
     * @param y destination vector
     */
    public static void dswap(final int n, final double[] x, final double[] y) {
        dswap(n, x, 1, y, 1);
    }

    /**
     * Exchange the two given vectors.
     *
     * @param x source  vector
     * @param y destination vector
     */
    public static void dswap(final double[] x, final double[] y) {
        dswap(x.length, x, 1, y, 1);
    }

    /**
     * Find the index of the x vector element with maximal absolute value.
     *
     * @param n    number of x entries
     * @param x    input vector
     * @param incX increment to use
     * @return index of maximal absolute element
     */
    public static int idamax(final int n, final double[] x, final int incX) {
        int idx = -1;
        double max = 0, ax;
        final int m = min(n, x.length);
        if (incX == 1) {
            for (int j = 0; j < m; j++) {
                ax = abs(x[j]);
                if (max < ax) {
                    idx = j;
                    max = ax;
                }
            }
        } else {
            for (int j = 0; j < m; j += incX) {
                ax = abs(x[j]);
                if (max < ax) {
                    idx = j;
                    max = ax;
                }
            }
        }
        return idx;
    }

    /**
     * Find the index of the x vector element with maximal absolute value.
     *
     * @param n number of x entries
     * @param x input vector
     * @return index of maximal absolute element
     */
    public static int idamax(final int n, final double[] x) {
        return idamax(n, x, 1);
    }

    /**
     * Find the index of the x vector element with maximal absolute value.
     *
     * @param x input vector
     * @return index of maximal absolute element
     */
    public static int idamax(final double[] x) {
        return idamax(x.length, x, 1);
    }
}
 