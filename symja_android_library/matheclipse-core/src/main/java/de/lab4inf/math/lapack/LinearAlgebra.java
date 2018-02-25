/*
    * Project: Lab4Math
    *
    * Copyright (c) 2008,  Prof. Dr. Nikolaus Wulff
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
package de.lab4inf.math.lapack;

import java.util.Arrays;

import de.lab4inf.math.BinaryOperator;
import de.lab4inf.math.Complex;
import de.lab4inf.math.Field;
import de.lab4inf.math.Interval;
import de.lab4inf.math.L4MLoader;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.Numeric;
import de.lab4inf.math.Operand;
import de.lab4inf.math.Rational;
import de.lab4inf.math.Solver;
import de.lab4inf.math.functions.Polynomial;
import de.lab4inf.math.util.Accuracy;
import de.lab4inf.math.util.Strings;

import static de.lab4inf.math.util.Accuracy.isSimilar;
import static de.lab4inf.math.util.BinomialCoefficient.binomial;
import static de.lab4inf.math.util.LeviCivita.leviCivita;
import static de.lab4inf.math.util.Randomizer.rndBox;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

/**
 * Basic linear algebra methods based on double precision arrays.
 * It provides basic methods comparable to the BLAS I,II and III but
 * without multi-processor parallel computing support.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: LinearAlgebra.java,v 1.102 2015/04/21 15:46:30 nwulff Exp $
 * @since 30.10.2007
 */
public final class LinearAlgebra extends L4MObject {
    /**
     * reference to the square check mismatch.
     */
    public static final String SQUARE_MISMATCH = "square mismatch %d != %d";
    /**
     * reference to the DIMENSION_MISMATCH attribute.
     */
    public static final String DIMENSION_MISMATCH = "dimension mismatch %d != %d";
    /** Reference to LinearAlgebra.java. */
    // private static final String MATRIX_IS_NULL = "field matrix is null";
    /**
     * Signal different row length in matrix.
     */
    public static final String ROWS_LENGTH_MISSMATCH = "All rows must have the same length.";
    /**
     * Signal a column mismatch.
     */
    public static final String COL_MISMATCH = "column mismatch %d != %d";
    /**
     * Signal a row mismatch.
     */
    public static final String ROW_MISMATCH = "row mismatch %d != %d";
    /**
     * Signal a column row mismatch.
     */
    public static final String COL_ROW_MISMATCH = "col:%d != %d:row mismatch";
    /**
     * Signal a null pointer array.
     */
    public static final String NULL_ARRAY = "null pointer array";
    private static final String COLON = ", ";
    private static final String FMT_3G = "%.3g";
    /**
     * Complex prototype injection.
     */
    private static final Complex COMPLEX = L4MLoader.load(Complex.class);
    /**
     * Complex zero constant.
     */
    public static final Complex ZERO = newComplex();
    /**
     * Complex one constant.
     */
    public static final Complex ONE = newComplex(1);
    /**
     * Interval prototype injection.
     */
    private static final Interval INTERVAL = L4MLoader.load(Interval.class);
    /**
     * Rational prototype injection.
     */
    private static final Rational RATIONAL = L4MLoader.load(Rational.class);
    /**
     * Rational zero constant.
     */
    public static final Rational RZERO = newRational();
    /**
     * Rational one constant.
     */
    public static final Rational RONE = newRational(1);
    @SuppressWarnings("rawtypes")
    private static final Add<?> ADD = new Add();
    @SuppressWarnings("rawtypes")
    private static final Sub<?> SUB = new Sub();

    // @SuppressWarnings("rawtypes")
    // private static final Mul<?> MUL = new Mul();
    // @SuppressWarnings("rawtypes")
    // private static final Div<?> DIV = new Div();

    /**
     * No public constructor allowed.
     */
    private LinearAlgebra() {
    }

    /**
     * Convert a double to a Complex value.
     *
     * @param x double to take
     * @return x as float
     */
    public static Complex asComplex(final double x) {
        return newComplex(x);
    }

    /**
     * Convert a double array to a Complex array.
     *
     * @param x doubles to take
     * @return x as Complex
     */
    public static Complex[] asComplex(final double... x) {
        final int n = x.length;
        final Complex[] y = new Complex[n];
        for (int j = 0; j < n; y[j] = asComplex(x[j]), j++) ;
        return y;
    }

    /**
     * Convert a double matrix to a Complex matrix.
     *
     * @param x doubles to take
     * @return x as Complex
     */
    public static Complex[][] asComplex(final double[][] x) {
        final int n = x.length;
        final Complex[][] y = new Complex[n][];
        for (int j = 0; j < n; y[j] = asComplex(x[j]), j++) ;
        return y;
    }

    /**
     * Convert a double to a float value.
     *
     * @param x double to take
     * @return x as float
     */
    public static float asFloat(final double x) {
        return (float) x;
    }

    /**
     * Convert a double array to a float array.
     *
     * @param x doubles to take
     * @return x as floats
     */
    public static float[] asFloat(final double... x) {
        final int n = x.length;
        final float[] y = new float[n];
        for (int j = 0; j < n; y[j] = asFloat(x[j]), j++) ;
        return y;
    }

    /**
     * Convert a double mmatrix to a float matrix.
     *
     * @param x doubles to take
     * @return x as floats
     */
    public static float[][] asFloat(final double[][] x) {
        final int n = x.length;
        final float[][] y = new float[n][];
        for (int j = 0; j < n; y[j] = asFloat(x[j]), j++) ;
        return y;
    }

    /**
     * Convert a float to a double value.
     *
     * @param x float to take
     * @return x as double
     */
    public static double asDouble(final float x) {
        return x;
    }

    /**
     * Convert a float array to a double array.
     *
     * @param x floats to take
     * @return x as doubles
     */
    public static double[] asDouble(final float... x) {
        final int n = x.length;
        final double[] y = new double[n];
        for (int j = 0; j < n; y[j] = asDouble(x[j]), j++) ;
        return y;
    }

    /**
     * Convert a float matrix to a double matrix.
     *
     * @param x floats to take
     * @return x as doubles
     */
    public static double[][] asDouble(final float[][] x) {
        final int n = x.length;
        final double[][] y = new double[n][];
        for (int j = 0; j < n; y[j] = asDouble(x[j]), j++) ;
        return y;
    }

    /**
     * String representation of a n-dimensional array vector, using a
     * default format assignment of 4 decimal digits.
     *
     * @param x the vector array
     * @return x as String
     */
    public static String asString(final float... x) {
        return asString(FMT_3G, x);
    }

    /**
     * String representation of a n-dimensional array vector, using a
     * default format assignment of 4 decimal digits.
     *
     * @param x the vector array
     * @return x as String
     */
    public static String asString(final double... x) {
        return asString(FMT_3G, x);
    }

    /**
     * String representation of a n-dimensional array vector.
     *
     * @param fmt the format assignment for the elements
     * @param x   the vector array
     * @return x as String
     */
    public static String asString(final String fmt, final float... x) {
        final StringBuffer sb = new StringBuffer();
        final int n = x.length - 1;
        for (int i = 0; i < n; i++) {
            sb.append(String.format(fmt, x[i]));
            sb.append(COLON);
        }
        sb.append(String.format(fmt, x[n]));
        return sb.toString();
    }

    /**
     * String representation of a n-dimensional array vector.
     *
     * @param fmt the format assignment for the elements
     * @param x   the vector array
     * @return x as String
     */
    public static String asString(final String fmt, final double... x) {
        final StringBuffer sb = new StringBuffer();
        final int n = x.length - 1;
        for (int i = 0; i < n; i++) {
            sb.append(String.format(fmt, x[i]));
            sb.append(COLON);
        }
        sb.append(String.format(fmt, x[n]));
        return sb.toString();
    }

    /**
     * Check the dimension is greater than zero.
     *
     * @param n dimension to check
     */
    public static void dimensionCheck(final int n) {
        if (n <= 0) {
            final String msg = format("dimension %d is not positive", n);
            getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that both vectors have the same dimension.
     *
     * @param x vector one
     * @param y vector two
     */
    public static void dimensionCheck(final float[] x, final float[] y) {
        if (x.length != y.length) {
            final int n = x.length;
            final int m = y.length;
            final String msg = format(DIMENSION_MISMATCH, n, m);
            if (DEBUG)
                getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that both vectors have the same dimension.
     *
     * @param x vector one
     * @param y vector two
     */
    public static void dimensionCheck(final double[] x, final double[] y) {
        if (x.length != y.length) {
            final int n = x.length;
            final int m = y.length;
            final String msg = format(DIMENSION_MISMATCH, n, m);
            if (DEBUG)
                getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that both vectors have the same dimension.
     *
     * @param x   vector one
     * @param y   vector two
     * @param <T> type of vector elements
     */
    public static <T> void dimensionCheck(final T[] x, final T[] y) {
        if (x.length != y.length) {
            final int n = x.length;
            final int m = y.length;
            final String msg = format(DIMENSION_MISMATCH, n, m);
            if (DEBUG)
                getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that the matrix is square.
     *
     * @param x matrix to check
     */
    public static void squareCheck(final float[][] x) {
        if (x.length != x[0].length) {
            final int n = x.length;
            final int m = x[0].length;
            final String msg = format(SQUARE_MISMATCH, n, m);
            if (DEBUG)
                getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that the matrix is square.
     *
     * @param x matrix to check
     */
    public static void squareCheck(final double[][] x) {
        if (x.length != x[0].length) {
            final int n = x.length;
            final int m = x[0].length;
            final String msg = format(SQUARE_MISMATCH, n, m);
            if (DEBUG)
                getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that the complex matrix is square.
     *
     * @param x   matrix to check
     * @param <T> of matrix elements
     */
    public static <T> void squareCheck(final T[][] x) {
        // if (x==null) {
        // String msg = format(NULL_ARRAY);
        // if (DEBUG)
        // getLogger().warn(msg);
        // throw new NullPointerException(msg);
        // }
        if (x.length != x[0].length) {
            final int n = x.length;
            final int m = x[0].length;
            final String msg = format(SQUARE_MISMATCH, n, m);
            if (DEBUG)
                getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that the vector length matches the matrix rows.
     *
     * @param x matrix
     * @param y vector
     */
    public static void dimensionCheck(final float[][] x, final float[] y) {
        final int m = y.length;
        final int u = x[0].length;
        if (m != u) {
            final String msg = format(ROW_MISMATCH, m, u);
            if (DEBUG)
                getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that the vector length matches the matrix rows.
     *
     * @param x matrix
     * @param y vector
     */
    public static void dimensionCheck(final double[][] x, final double[] y) {
        final int m = y.length;
        final int u = x[0].length;
        if (m != u) {
            final String msg = format(ROW_MISMATCH, m, u);
            if (DEBUG)
                getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that the vector length matches the matrix rows.
     *
     * @param x   matrix
     * @param y   vector
     * @param <T> type of elements
     */
    public static <T> void dimensionCheck(final T[][] x, final T[] y) {
        final int m = y.length;
        final int u = x[0].length;
        if (m != u) {
            final String msg = format(ROW_MISMATCH, m, u);
            if (DEBUG)
                getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that both matrix have the same dimension for addition or subtraction.
     *
     * @param x   matrix one
     * @param y   matrix two
     * @param <T> matrix elements
     */
    public static <T> void dimensionCheck(final T[][] x, final T[][] y) {
        // if (x==null||y==null) {
        // String msg = format(NULL_ARRAY);
        // if (DEBUG)
        // getLogger().warn(msg);
        // throw new NullPointerException(msg);
        // }
        final int n = x.length;
        final int m = y.length;
        final int u = x[0].length;
        final int v = y[0].length;
        if (n != m) {
            final String msg = format(ROW_MISMATCH, n, m);
            if (DEBUG)
                getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
        if (u != v) {
            final String msg = format(COL_MISMATCH, u, v);
            if (DEBUG)
                getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that both matrix have correct dimensions for multiplication.
     *
     * @param x matrix one
     * @param y matrix two
     */
    public static void multDimensionCheck(final float[][] x, final float[][] y) {
        // if (x==null||y==null) {
        // String msg = format(NULL_ARRAY);
        // if (DEBUG)
        // getLogger().warn(msg);
        // throw new NullPointerException(msg);
        // }
        final int m = y.length;
        final int n = x[0].length;
        if (n != m) {
            final String msg = format(COL_ROW_MISMATCH, n, m);
            if (DEBUG)
                getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that both matrix have correct dimensions for multiplication.
     *
     * @param x matrix one
     * @param y matrix two
     */
    public static void multDimensionCheck(final double[][] x, final double[][] y) {
        // if (x==null||y==null) {
        // String msg = format(NULL_ARRAY);
        // if (DEBUG)
        // getLogger().warn(msg);
        // throw new NullPointerException(msg);
        // }
        final int m = y.length;
        final int n = x[0].length;
        if (n != m) {
            final String msg = format(COL_ROW_MISMATCH, n, m);
            if (DEBUG)
                getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that both matrix have correct dimensions for multiplication.
     *
     * @param x   complex matrix one
     * @param y   complex matrix two
     * @param <T> type of matrix elements
     */
    public static <T> void multDimensionCheck(final T[][] x, final T[][] y) {
        // if (x==null||y==null) {
        // String msg = format(NULL_ARRAY);
        // if (DEBUG)
        // getLogger().warn(msg);
        // throw new NullPointerException(msg);
        // }
        final int m = y.length;
        final int n = x[0].length;
        if (n != m) {
            final String msg = format(COL_ROW_MISMATCH, n, m);
            if (DEBUG)
                getLogger().warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Trace of a real matrix A.
     *
     * @param a matrix to analyse
     * @return tr(A)
     */
    public static float trace(final float[][] a) {
        squareCheck(a);
        final int n = a.length;
        float tr = 0;
        for (int j = 0; j < n; tr += a[j][j], j++) ;
        return tr;
    }

    /**
     * Trace of a real matrix A.
     *
     * @param a matrix to analyse
     * @return tr(A)
     */
    public static double trace(final double[][] a) {
        squareCheck(a);
        final int n = a.length;
        double tr = 0;
        for (int j = 0; j < n; tr += a[j][j], j++) ;
        return tr;
    }

    /**
     * Trace of a generic matrix A.
     *
     * @param a   matrix to analyse
     * @param <T> the elements type
     * @return tr(A)
     */
    public static <T extends Numeric<T>> T trace(final T[][] a) {
        squareCheck(a);
        final int n = a.length;
        T tr = a[0][0].getZero();
        for (int j = 0; j < n; tr = tr.plus(a[j][j]), j++) ;
        return tr;
    }

    /**
     * Normalize the vector to unity for the Euclidian norm.
     *
     * @param x double[] the vector to be normalized
     */
    public static void normalize(final double[] x) {
        final int n = x.length;
        final double norm = norm(x);
        for (int i = 0; i < n; x[i] /= norm, i++) {
        }
    }

    /**
     * Helper method to calculate sqrt(x_0*x_0 + ... + x_n*x_n) without
     * over- or underflow.
     *
     * @param x the vector elements
     * @return sqrt(x*x + ...)
     */
    public static double hypot(final double[] x) {
        final int n = x.length;
        double tmp = 0, xj;
        final double amax = maxnorm(x);
        if (amax <= 0)
            return tmp;
        for (int j = 0; j < n; xj = x[j] / amax, tmp += xj * xj, j++) ;
        return amax * sqrt(tmp);
    }

    /**
     * Euclidean norm of a vector or parameter list.
     *
     * @param x double[] the vector
     * @return double the Euclidean norm
     */
    public static double norm(final double... x) {
        final int n = x.length;
        double tmp = 0, result = 0;
        // this simple implementation may cause rounding errors...
        for (int i = 0; i < n; tmp = x[i], result += tmp * tmp, i++) ;
        return sqrt(result);
    }

    /**
     * Euclidean norm of a vector or parameter list with given
     * start to end range to be included into the calculation.
     *
     * @param beg the start index
     * @param end the end  index
     * @param x   double[] the vector
     * @return double the Euclidean norm
     */
    public static double norm(final int beg, final int end, final double... x) {
        final int m = max(0, beg), n = min(end, x.length);
        double tmp = 0, result = 0;
        // this simple implementation may cause rounding errors...
        for (int i = m; i < n; tmp = x[i], result += tmp * tmp, i++) ;
        return sqrt(result);
    }

    /**
     * Euclidean norm of a vector or parameter list with given
     * start to end range to be included into the calculation.
     *
     * @param beg the start index
     * @param end the end  index
     * @param x   T[] the vector
     * @param <T> the vector elements type
     * @return T the Euclidean norm
     */
    public static <T extends Numeric<T>> T norm(final int beg, final int end, final T... x) {
        final int m = max(0, beg), n = min(end, x.length);
        T tmp, result = x[0].getZero();
        for (int i = m; i < n; tmp = x[i], result = result.plus(tmp.multiply(tmp)), i++) ;
        return result.sqrt();
    }

    /**
     * Max norm of a vector.
     *
     * @param x double[] the vector
     * @return double the max norm
     */
    public static double maxnorm(final double[] x) {
        final int n = x.length;
        double result = 0;
        for (int i = 0; i < n; result = max(result, abs(x[i])), i++) ;
        return result;
    }

    /**
     * L1 norm of a vector.
     *
     * @param x double[] the vector
     * @return double the L1 norm
     */
    public static double norm1(final double[] x) {
        final int n = x.length;
        double result = 0;
        for (int i = 0; i < n; result += abs(x[i]), i++) ;
        return result;
    }

    /**
     * L1 norm of a vector.
     *
     * @param x float[] the vector
     * @return float the L1 norm
     */
    public static float norm1(final float[] x) {
        final int n = x.length;
        float result = 0;
        for (int i = 0; i < n; result += abs(x[i]), i++) ;
        return result;
    }

    /**
     * L1 norm of a vector.
     *
     * @param x   T[] the vector
     * @param <T> type of the elements
     * @return T the L1 norm
     */
    public static <T extends Numeric<T>> T norm1(final T[] x) {
        final int n = x.length;
        T result = x[0].abs();
        for (int i = 1; i < n; result = result.plus(x[i].abs()), i++) ;
        return result;
    }

    /**
     * L_infinity norm of a vector.
     *
     * @param x double[] the vector
     * @return double the infinity norm
     */
    public static double norm8(final double[] x) {
        return maxnorm(x);
    }

    /**
     * Max norm of a generic vector.
     *
     * @param x float[] the vector
     * @return float the max norm
     */
    public static float maxnorm(final float[] x) {
        final int n = x.length;
        float result = abs(x[0]);
        for (int i = 1; i < n; result = max(result, abs(x[i])), i++) ;
        return result;
    }

    /**
     * Max norm of a generic vector.
     *
     * @param x   T[] the vector
     * @param <T> type of the array elements
     * @return double the max norm
     */
    public static <T extends Numeric<T>> double maxnorm(final T[] x) {
        final int n = x.length;
        double result = x[0].abs().doubleValue();
        for (int i = 1; i < n; result = max(result, x[i].abs().doubleValue()), i++) ;
        return result;
    }

    /**
     * L1 (column) norm of a NxM matrix.
     *
     * @param x   double[][] the matrix
     * @param <T> type of the array elements
     * @return double the norm
     */
    public static <T extends Numeric<T>> double norm1(final T[][] x) {
        // squareCheck(x);
        final int n = x.length;
        final int m = x[0].length;
        int j;
        double amax, norm = 0;
        for (int i = 0; i < m; norm = max(norm, amax), i++)
            for (amax = 0, j = 0; j < n; amax += x[j][i].abs().doubleValue(), j++) ;

        return norm;
    }

    /**
     * L1 (column) norm of a NxM matrix.
     *
     * @param x float[][] the matrix
     * @return float the norm
     */
    public static float norm1(final float[][] x) {
        // squareCheck(x);
        final int n = x.length;
        final int m = x[0].length;
        int j;
        float amax, norm = 0;
        for (int i = 0; i < m; norm = max(norm, amax), i++)
            for (amax = 0, j = 0; j < n; amax += abs(x[j][i]), j++) ;

        return norm;
    }

    /**
     * L1 (column) norm of a NxM matrix.
     *
     * @param x double[][] the matrix
     * @return double the norm
     */
    public static double norm1(final double[][] x) {
        // squareCheck(x);
        final int n = x.length;
        final int m = x[0].length;
        int j;
        double amax, norm = 0;
        for (int i = 0; i < m; norm = max(norm, amax), i++)
            for (amax = 0, j = 0; j < n; amax += abs(x[j][i]), j++) ;

        return norm;
    }

    /**
     * L_infinity (row) norm of a NxM matrix.
     *
     * @param x double[][] the matrix
     * @return double the norm
     */
    public static double norm8(final double[][] x) {
        // squareCheck(x);
        final int n = x.length;
        final int m = x[0].length;
        int j;
        double amax, norm = 0;
        for (int i = 0; i < n; norm = max(norm, amax), i++)
            for (amax = 0, j = 0; j < m; amax += abs(x[i][j]), j++) ;

        return norm;
    }

    /**
     * L2  (Euclidean or Frobenius) norm of a NxM matrix.
     *
     * @param x double[][] the matrix
     * @return double the norm
     */
    public static double norm2(final double[][] x) {
        // squareCheck(x);
        final int n = x.length;
        final int m = x[0].length;
        double aij, norm = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; aij = x[i][j], norm += aij * aij, j++) ;

        norm = sqrt(norm);
        return norm;
    }

    /**
     * Spectral norm of a NxM matrix consistent with L2 vector norm.
     *
     * @param x double[][] the matrix
     * @return double the norm
     */
    public static double spectralnorm(final double[][] x) {
        double ev = 0;
        // squareCheck(x);
        double[][] b;
        b = mult(x, transpose(x));
        final SVDSolver solver = new SVDSolver();
        final double[] evs = solver.eigenvalues(b);
        ev = sqrt(maxabs(evs));
        return ev;
    }

    /**
     * Norm of a NxN matrix, the default is
     * the L2 norm according to the euclidean
     * vector norm.
     *
     * @param x double[][] the matrix
     * @return double the norm
     */
    public static double norm(final double[][] x) {
        return norm2(x);
    }

    /**
     * The Tanimoto or Jaccard index calculating the similarity of two vectors x,y.
     *
     * @param x vector one
     * @param y vector two
     * @return double  &lang;x|y &rang;/( ||x||&sup2; + ||y||&sup2; - &lang; x|y &rang;)
     */
    public static double jaccarddistance(final double[] x, final double[] y) {
        dimensionCheck(x, y);
        final int n = x.length;
        double xj, yj, xy = 0, xx = 0, yy = 0;
        for (int j = 0; j < n; xj = x[j], yj = y[j], xx += xj * xj, xy += xj * yj, yy += yj * yj, j++)
            ;
        if (abs(xy) > 0) {
            xy /= xx + yy - xy;
        }
        return xy;
    }

    /**
     * The euclidean norm of the difference of two vectors x,y.
     *
     * @param x float[] vector one
     * @param y float[] vector two
     * @return float  ||x-y||;
     */
    public static float diff(final float[] x, final float[] y) {
        dimensionCheck(x, y);
        final int n = x.length;
        float tmp, result = 0;
        for (int i = 0; i < n; tmp = x[i] - y[i], result += tmp * tmp, i++) {
        }
        return (float) sqrt(result);
    }

    /**
     * The euclidean norm of the difference of two vectors x,y.
     *
     * @param x double[] vector one
     * @param y double[] vector two
     * @return double  ||x-y||;
     */
    public static double diff(final double[] x, final double[] y) {
        dimensionCheck(x, y);
        final int n = x.length;
        double tmp, result = 0;
        for (int i = 0; i < n; tmp = x[i] - y[i], result += tmp * tmp, i++) {
        }
        return sqrt(result);
    }

    /**
     * The max norm of the difference of two vectors x,y.
     *
     * @param x float[] vector one
     * @param y float[] vector two
     * @return float  max |x-y|;
     */
    public static float maxdiff(final float[] x, final float[] y) {
        dimensionCheck(x, y);
        final int n = x.length;
        float result = 0;
        for (int i = 0; i < n; result = max(result, abs(x[i] - y[i])), i++) ;
        return result;
    }

    /**
     * The max norm of the difference of two vectors x,y.
     *
     * @param x double[] vector one
     * @param y double[] vector two
     * @return double  max |x-y|;
     */
    public static double maxdiff(final double[] x, final double[] y) {
        dimensionCheck(x, y);
        final int n = x.length;
        double result = 0;
        for (int i = 0; i < n; result = max(result, abs(x[i] - y[i])), i++) ;
        return result;
    }

    /**
     * The maximum matrix norm of the difference of two matrixes x,y, e.g.
     * the greatest difference |x_ij - y_ij|.
     *
     * @param x float[][] matrix one
     * @param y float[][] matrix two
     * @return float  ||x-y||;
     */
    public static float diff(final float[][] x, final float[][] y) {
        dimensionCheck(x, y);
        final int n = x.length;
        final int m = x[0].length;
        float amax = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; amax = max(amax, abs(x[i][j] - y[i][j])), j++) ;

        return amax;
    }

    /**
     * The maximum matrix norm of the difference of two matrixes x,y, e.g.
     * the greatest difference |x_ij - y_ij|.
     *
     * @param x double[][] matrix one
     * @param y double[][] matrix two
     * @return double  ||x-y||;
     */
    public static double diff(final double[][] x, final double[][] y) {
        dimensionCheck(x, y);
        final int n = x.length;
        final int m = x[0].length;
        double amax = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; amax = max(amax, abs(x[i][j] - y[i][j])), j++) ;

        return amax;
    }

    /**
     * Addition of two vectors x,y.
     *
     * @param x float[] vector one
     * @param y float[] vector two
     * @return float[]  x+y;
     */
    public static float[] add(final float[] x, final float[] y) {
        dimensionCheck(x, y);
        final int n = x.length;
        final float[] result = new float[n];
        for (int i = 0; i < n; result[i] = x[i] + y[i], i++) ;
        return result;
    }

    /**
     * Addition of two vectors x,y.
     *
     * @param x double[] vector one
     * @param y double[] vector two
     * @return double[]  x+y;
     */
    public static double[] add(final double[] x, final double[] y) {
        dimensionCheck(x, y);
        final int n = x.length;
        final double[] result = new double[n];
        for (int i = 0; i < n; result[i] = x[i] + y[i], i++) ;
        return result;
    }

    /**
     * Generic operation for two vectors x,y via an binary operator.
     *
     * @param x   T[] vector one
     * @param y   T[] vector two
     * @param o   BinaryOperator<T> used for the operation
     * @param <T> elements type
     * @return T[]  x o y;
     */
    @SuppressWarnings("unchecked")
    private static <T extends Numeric<T>> T[] op(final T[] x, final BinaryOperator<?> o, final T[] y) {
        dimensionCheck(x, y);
        final int n = x.length;
        final T[] z = create(x, n);
        for (int j = 0; j < n; z[j] = ((BinaryOperator<T>) o).op(x[j], y[j]), j++) ;
        return z;
    }

    /**
     * Generic operation for two matrixes x,y via an binary operator.
     *
     * @param x   T[][] matrix one
     * @param y   T[][] matrix two
     * @param o   BinaryOperator<T> used for the operation
     * @param <T> elements type
     * @return T[][]  x o y;
     */
    private static <T extends Numeric<T>> T[][] op(final T[][] x, final BinaryOperator<?> o, final T[][] y) {
        dimensionCheck(x, y);
        final int n = x.length;
        final int m = x[0].length;
        final T[][] z = create(x, n, m);
        for (int j = 0; j < n; z[j] = op(x[j], o, y[j]), j++) ;
        return z;
    }

    /**
     * Addition of two vectors x,y.
     *
     * @param x   T[] vector one
     * @param y   T[] vector two
     * @param <T> elements type
     * @return T[]  x+y;
     */
    @Operand(symbol = "+")
    public static <T extends Numeric<T>> T[] add(final T[] x, final T[] y) {
        return op(x, ADD, y);
    }

    /**
     * Subtraction of two vectors x,y.
     *
     * @param x float[] vector one
     * @param y float[] vector two
     * @return float[]  x-y;
     */
    @Operand(symbol = "-")
    public static float[] sub(final float[] x, final float[] y) {
        dimensionCheck(x, y);
        final int n = x.length;
        final float[] result = new float[n];
        for (int i = 0; i < n; result[i] = x[i] - y[i], i++) ;
        return result;
    }

    /**
     * Subtraction of two vectors x,y.
     *
     * @param x double[] vector one
     * @param y double[] vector two
     * @return double[]  x-y;
     */
    @Operand(symbol = "-")
    public static double[] sub(final double[] x, final double[] y) {
        dimensionCheck(x, y);
        final int n = x.length;
        final double[] result = new double[n];
        for (int i = 0; i < n; result[i] = x[i] - y[i], i++) ;
        return result;
    }

    /**
     * Subtraction of two vectors x,y.
     *
     * @param x   T[] vector one
     * @param y   T[] vector two
     * @param <T> type of the elements
     * @return T[]  x-y;
     */
    @Operand(symbol = "-")
    public static <T extends Numeric<T>> T[] sub(final T[] x, final T[] y) {
        return op(x, SUB, y);
    }

    /**
     * Scale the vectors x by the factor scale.
     *
     * @param x     double[] vector
     * @param scale double scaling factor
     * @return double[] x*scale;
     */
    @Operand(symbol = "*")
    public static double[] mult(final double[] x, final double scale) {
        final int n = x.length;
        final double[] y = new double[n];
        for (int i = 0; i < n; y[i] = x[i] * scale, i++) ;
        return y;
    }

    /**
     * Scale the vectors x by the factor scale.
     *
     * @param x     float[] vector
     * @param scale double scaling factor
     * @return double[] x*scale;
     */
    @Operand(symbol = "*")
    public static float[] mult(final float[] x, final float scale) {
        final int n = x.length;
        final float[] y = new float[n];
        for (int i = 0; i < n; y[i] = x[i] * scale, i++) ;
        return y;
    }

    /**
     * Scale the vector x by the factor scale.
     *
     * @param x     T[] vector
     * @param scale real scaling factor
     * @param <T>   type of the elements
     * @return T[] x*scale;
     */
    @Operand(symbol = "*")
    public static <T extends Numeric<T>> T[] mult(final T[] x, final long scale) {
        final int n = x.length;
        final T[] y = create(x, n);
        for (int i = 0; i < n; y[i] = x[i].multiply(scale), i++) ;
        return y;
    }

    /**
     * Scale the vector x by the factor scale.
     *
     * @param x     T[] vector
     * @param scale real scaling factor
     * @param <T>   type of the elements
     * @return T[] x*scale;
     */
    @Operand(symbol = "*")
    public static <T extends Numeric<T>> T[] mult(final T[] x, final double scale) {
        final int n = x.length;
        final T[] y = create(x, n);
        for (int i = 0; i < n; y[i] = x[i].multiply(scale), i++) ;
        return y;
    }

    /**
     * Scale the vector x by the factor scale.
     *
     * @param x     T[] vector
     * @param scale scaling factor
     * @param <T>   type of the elements
     * @return T[] x*scale;
     */
    @Operand(symbol = "*")
    public static <T extends Numeric<T>> T[] mult(final T[] x, final T scale) {
        final int n = x.length;
        final T[] y = create(x, n);
        for (int i = 0; i < n; y[i] = x[i].multiply(scale), i++) ;
        return y;
    }

    /**
     * Cross- or vector product of two vectors u,v resulting in a vector perpendicular to u and v.
     *
     * @param u float[] vector one
     * @param v float[] vector two
     * @return float[] u x v;
     */
    @Operand(symbol = "x")
    public static float[] crossProduct(final float[] u, final float[] v) {
        dimensionCheck(u, v);
        final int n = u.length;
        final float[] w = new float[n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    w[i] += leviCivita(i, j, k) * u[j] * v[k];
        return w;
    }

    /**
     * Cross- or vector product of two vectors u,v resulting in a vector perpendicular to u and v.
     *
     * @param u double[] vector one
     * @param v double[] vector two
     * @return double[] u x v;
     */
    @Operand(symbol = "x")
    public static double[] crossProduct(final double[] u, final double[] v) {
        dimensionCheck(u, v);
        final int n = u.length;
        final double[] w = new double[n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    w[i] += leviCivita(i, j, k) * u[j] * v[k];
        return w;
    }

    /**
     * Cross- or vector product of two generic vectors u,v of type T resulting in a vector perpendicular to u and v.
     *
     * @param u   T[] vector one
     * @param v   T[] vector two
     * @param <T> type of the elements
     * @return T[] u x v;
     */
    @Operand(symbol = "x")
    public static <T extends Numeric<T>> T[] crossProduct(final T[] u, final T[] v) {
        dimensionCheck(u, v);
        final int n = u.length;
        final T zero = u[0].getZero();
        final T[] w = create(u, n);
        for (int i = 0; i < n; i++) {
            w[i] = zero;
            for (int j = 0; j < n; j++)
                for (int k = 0; k < n; k++)
                    w[i] = w[i].plus(u[j].multiply(v[k]).multiply(leviCivita(i, j, k)));
        }
        return w;
    }

    /**
     * Scalar product of two vectors x,y.
     *
     * @param x float[] vector one
     * @param y float[] vector two
     * @return float xT*y;
     */
    @Operand(symbol = "*")
    public static float mult(final float[] x, final float[] y) {
        dimensionCheck(x, y);
        float result = 0;
        final int n = x.length;
        for (int i = 0; i < n; result += x[i] * y[i], i++) ;
        return result;
    }

    /**
     * Scalar product of two vectors x,y.
     *
     * @param x double[] vector one
     * @param y double[] vector two
     * @return double xT*y;
     */
    @Operand(symbol = "*")
    public static double mult(final double[] x, final double[] y) {
        dimensionCheck(x, y);
        double result = 0;
        final int n = x.length;
        for (int i = 0; i < n; result += x[i] * y[i], i++) ;
        return result;
    }

    /**
     * Scalar product of two vectors x,y.
     *
     * @param x   T[] vector one
     * @param y   T[] vector two
     * @param <T> type of the elements
     * @return T xT*y;
     */
    @Operand(symbol = "*")
    public static <T extends Numeric<T>> T mult(final T[] x, final T[] y) {
        dimensionCheck(x, y);
        T result = x[0].getZero();
        final int n = x.length;
        for (int i = 0; i < n; result = result.plus(x[i].multiply(y[i])), i++) ;
        return result;
    }

    /**
     * Product of two vectors x,y creating a matrix.
     *
     * @param x double[] vector one
     * @param y double[] vector two
     * @return double[][] y*xT
     */
    public static double[][] multTrans(final double[] x, final double[] y) {
        dimensionCheck(x, y);
        final int n = x.length;
        final double[][] result = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; result[i][j] = x[i] * y[j], j++) ;
        return result;
    }

    /**
     * Calculate the transpose of matrix A.
     *
     * @param a float[][] the matrix
     * @return float[][] the transpose matrix
     */
    public static float[][] transpose(final float[][] a) {
        final int n = a.length;
        final int m = a[0].length;
        final float[][] b = new float[m][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; b[j][i] = a[i][j], j++) ;
        return b;
    }

    /**
     * Calculate the transpose of matrix A.
     *
     * @param a double[][] the matrix
     * @return double[][] the transpose matrix
     */
    public static double[][] transpose(final double[][] a) {
        final int n = a.length;
        final int m = a[0].length;
        final double[][] b = new double[m][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; b[j][i] = a[i][j], j++) ;
        return b;
    }

    /**
     * Calculate the transpose of matrix A.
     *
     * @param a   the matrix
     * @param <T> the matrix elements type
     * @return T[][] the transpose matrix
     */
    public static <T extends Numeric<T>> T[][] transpose(final T[][] a) {
        final int n = a.length;
        final int m = a[0].length;
        final T[][] b = create(a, m, n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; b[j][i] = a[i][j], j++) ;
        return b;
    }

    /**
     * Calculate the conjugate transpose of matrix A.
     *
     * @param a Complex the matrix
     * @return Complex[][] the conjugate matrix
     */
    public static Complex[][] conjugate(final Complex[][] a) {
        final int n = a.length;
        final int m = a[0].length;
        final Complex[][] b = new Complex[m][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; b[j][i] = a[i][j].conj(), j++) ;
        return b;
    }

    /**
     * Multiply a Matrix A with a vector x.
     *
     * @param a float[][] the matrix
     * @param x float[] the vector
     * @return float[] y=A*x
     */
    @Operand(symbol = "*")
    public static float[] mult(final float[][] a, final float[] x) {
        dimensionCheck(a, x);
        int i, j;
        final int n = a.length;
        final int m = a[0].length;
        float tmp;
        float[] z;
        final float[] y = new float[n];
        for (i = 0; i < n; y[i] = tmp, i++)
            for (z = a[i], tmp = 0, j = 0; j < m; tmp += z[j] * x[j], j++) ;
        return y;
    }

    /**
     * Multiply a Matrix A with a vector x.
     *
     * @param a double[][] the matrix
     * @param x double[] the vector
     * @return double[] y=A*x
     */
    @Operand(symbol = "*")
    public static double[] mult(final double[][] a, final double[] x) {
        dimensionCheck(a, x);
        int i, j;
        final int n = a.length;
        final int m = a[0].length;
        double tmp;
        double[] z;
        final double[] y = new double[n];
        for (i = 0; i < n; y[i] = tmp, i++)
            for (z = a[i], tmp = 0, j = 0; j < m; tmp += z[j] * x[j], j++) ;
        return y;
    }

    /**
     * Multiply a Matrix A with a vector x.
     *
     * @param a   T[][] the matrix
     * @param x   T[] the vector
     * @param <T> the elements type
     * @return T[] y=A*x
     */
    @Operand(symbol = "*")
    public static <T extends Numeric<T>> T[] mult(final T[][] a, final T[] x) {
        dimensionCheck(a, x);
        int i, j;
        final int n = a.length;
        final int m = a[0].length;
        T tmp;
        final T zero = x[0].getZero();
        T[] z;
        final T[] y = create(zero, n);
        for (i = 0; i < n; y[i] = tmp, i++)
            for (z = a[i], tmp = zero, j = 0; j < m; tmp = tmp.plus(z[j].multiply(x[j])), j++) ;
        return y;
    }

    /**
     * Multiply the transposed vector x with the Matrix A.
     *
     * @param x double[] the vector
     * @param a double[][] the matrix
     * @return double[] y = xT*A
     */
    @Operand(symbol = "*")
    public static double[] mult(final double[] x, final double[][] a) {
        // dimensionCheck(a, x);
        int i, j;
        final int n = a.length;
        final int m = a[0].length;
        double tmp;
        final double[] y = new double[m];
        for (i = 0; i < m; y[i] = tmp, i++)
            for (tmp = 0, j = 0; j < n; tmp += a[j][i] * x[j], j++) ;
        return y;
    }

    /**
     * Scale matrix A by the value scale.
     *
     * @param a     double[][] the  matrix
     * @param scale double the scaling factor
     * @return double[][] C=A*scale;
     */
    @Operand(symbol = "*")
    public static double[][] mult(final double[][] a, final double scale) {
        int i, j;
        final int n = a.length;
        final int m = a[0].length;
        final double[][] c = new double[n][m];
        for (i = 0; i < n; i++)
            for (j = 0; j < m; c[i][j] = a[i][j] * scale, j++) ;
        return c;
    }

    /**
     * Multiply matrix A with matrix B.
     *
     * @param a float[][] the 1.st matrix
     * @param b float[][] the 2.nd matrix
     * @return float[][] C=A*B;
     */
    @Operand(symbol = "*")
    public static float[][] mult(final float[][] a, final float[][] b) {
        multDimensionCheck(a, b);
        int i, j, k;
        final int n = a.length;
        final int l = a[0].length;
        final int m = b[0].length;
        float tmp;
        final float[][] c = new float[n][m];
        float[] z;
        for (i = 0; i < n; i++)
            for (j = 0; j < m; c[i][j] = tmp, j++)
                for (z = a[i], tmp = 0, k = 0; k < l; tmp += z[k] * b[k][j], k++) ;
        return c;
    }

    /**
     * Multiply matrix A with matrix B.
     *
     * @param a double[][] the 1.st matrix
     * @param b double[][] the 2.nd matrix
     * @return double[][] C=A*B;
     */
    @Operand(symbol = "*")
    public static double[][] mult(final double[][] a, final double[][] b) {
        multDimensionCheck(a, b);
        int i, j, k;
        final int n = a.length;
        final int l = a[0].length;
        final int m = b[0].length;
        double tmp;
        final double[][] c = new double[n][m];
        double[] z;
        for (i = 0; i < n; i++)
            for (j = 0; j < m; c[i][j] = tmp, j++)
                for (z = a[i], tmp = 0, k = 0; k < l; tmp += z[k] * b[k][j], k++) ;
        return c;
    }

    /**
     * Multiply matrix A with transposed matrix B.
     *
     * @param a double[][] the 1.st matrix
     * @param b double[][] the 2.nd matrix
     * @return double[][] C=A*trans(B);
     */
    public static double[][] multTransposeB(final double[][] a, final double[][] b) {
        // multDimensionCheck(a, b);
        int i, j, k;
        final int n = a.length;
        final int l = a[0].length;
        final int m = b.length;
        double tmp;
        final double[][] c = new double[n][m];
        double[] z, v;
        for (i = 0; i < n; i++)
            for (j = 0; j < m; c[i][j] = tmp, j++)
                for (z = a[i], v = b[j], tmp = 0, k = 0; k < l; tmp += z[k] * v[k], k++) ;
        return c;
    }

    /**
     * Multiply the transposed matrix A with matrix B.
     *
     * @param a float[][] the 1.st matrix
     * @param b float[][] the 2.nd matrix
     * @return float[][] C=trans(A)*B;
     */
    public static float[][] multTransposeA(final float[][] a, final float[][] b) {
        // multDimensionCheck(a, b);
        int i, j, k;
        final int n = a[0].length;
        final int l = a.length;
        final int m = b[0].length;
        double tmp;
        final float[][] c = new float[n][m];
        for (i = 0; i < n; i++)
            for (j = 0; j < m; c[i][j] = (float) tmp, j++)
                for (tmp = 0, k = 0; k < l; tmp += a[k][i] * b[k][j], k++) ;
        return c;
    }

    /**
     * Multiply the transposed matrix A with matrix B.
     *
     * @param a double[][] the 1.st matrix
     * @param b double[][] the 2.nd matrix
     * @return double[][] C=trans(A)*B;
     */
    public static double[][] multTransposeA(final double[][] a, final double[][] b) {
        // multDimensionCheck(a, b);
        int i, j, k;
        final int n = a[0].length;
        final int l = a.length;
        final int m = b[0].length;
        double tmp;
        final double[][] c = new double[n][m];
        for (i = 0; i < n; i++)
            for (j = 0; j < m; c[i][j] = tmp, j++)
                for (tmp = 0, k = 0; k < l; tmp += a[k][i] * b[k][j], k++) ;
        return c;
    }

    /**
     * Multiply matrix A with matrix B.
     *
     * @param a   T[][] the 1.st matrix
     * @param b   T[][] the 2.nd matrix
     * @param <T> the elements type
     * @return T[][] C=A*B;
     */
    @Operand(symbol = "*")
    public static <T extends Numeric<T>> T[][] mult(final T[][] a, final T[][] b) {
        multDimensionCheck(a, b);
        int i, j, k;
        final int n = a.length;
        final int l = a[0].length;
        final int m = b[0].length;
        T tmp;
        final T zero = a[0][0].getZero();
        final T[][] c = create(zero, n, m);
        T[] z;
        for (i = 0; i < n; i++)
            for (j = 0; j < m; c[i][j] = tmp, j++)
                for (z = a[i], tmp = zero, k = 0; k < l; tmp = tmp.plus(z[k].multiply(b[k][j])), k++)
                    ;
        return c;
    }

    /**
     * Addition of matrix A with matrix B.
     *
     * @param a float[][] the 1.st matrix
     * @param b float[][] the 2.nd matrix
     * @return float[][] C=A+B;
     */
    @Operand(symbol = "+")
    public static float[][] add(final float[][] a, final float[][] b) {
        dimensionCheck(a, b);
        final int n = a.length;
        final float[][] c = new float[n][];
        for (int i = 0; i < n; c[i] = add(a[i], b[i]), i++) ;
        return c;
    }

    /**
     * Addition of matrix A with matrix B.
     *
     * @param a double[][] the 1.st matrix
     * @param b double[][] the 2.nd matrix
     * @return double[][] C=A+B;
     */
    @Operand(symbol = "+")
    public static double[][] add(final double[][] a, final double[][] b) {
        dimensionCheck(a, b);
        final int n = a.length;
        final double[][] c = new double[n][];
        for (int i = 0; i < n; c[i] = add(a[i], b[i]), i++) ;
        return c;
    }

    /**
     * Addition of matrix A with matrix B.
     *
     * @param a   T[][] the 1.st matrix
     * @param b   T[][] the 2.nd matrix
     * @param <T> type of the elements
     * @return T[][] C=A+B;
     */
    @Operand(symbol = "+")
    public static <T extends Numeric<T>> T[][] add(final T[][] a, final T[][] b) {
        return op(a, ADD, b);
    }

    /**
     * Subtraction of matrix B from matrix A.
     *
     * @param a float[][] the 1.st matrix
     * @param b float[][] the 2.nd matrix
     * @return float[][] C=A-B;
     */
    @Operand(symbol = "-")
    public static float[][] sub(final float[][] a, final float[][] b) {
        dimensionCheck(a, b);
        final int n = a.length;
        final float[][] c = new float[n][];
        for (int i = 0; i < n; c[i] = sub(a[i], b[i]), i++) ;
        return c;
    }

    /**
     * Subtraction of matrix B from matrix A.
     *
     * @param a double[][] the 1.st matrix
     * @param b double[][] the 2.nd matrix
     * @return double[][] C=A-B;
     */
    @Operand(symbol = "-")
    public static double[][] sub(final double[][] a, final double[][] b) {
        dimensionCheck(a, b);
        final int n = a.length;
        final double[][] c = new double[n][];
        for (int i = 0; i < n; c[i] = sub(a[i], b[i]), i++) ;
        return c;
    }

    /**
     * Subtraction of matrix B from matrix A.
     *
     * @param a   T[][] the 1.st matrix
     * @param b   T[][] the 2.nd matrix
     * @param <T> type of the elements
     * @return T[][] C=A-B;
     */
    @Operand(symbol = "-")
    public static <T extends Numeric<T>> T[][] sub(final T[][] a, final T[][] b) {
        return op(a, SUB, b);
    }

    /**
     * Create a N times N identity matrix.
     *
     * @param n the dimension
     * @return NxN identity matrix
     */
    public static double[][] identity(final int n) {
        // dimensionCheck(n);
        final double[][] e = new double[n][n];
        for (int i = 0; i < n; e[i][i] = 1, i++) ;
        return e;
    }

    /**
     * Create a N times N float identity matrix.
     *
     * @param n the dimension
     * @return NxN identity matrix
     */
    public static float[][] identityFloat(final int n) {
        // dimensionCheck(n);
        final float[][] e = new float[n][n];
        for (int i = 0; i < n; e[i][i] = 1, i++) ;
        return e;
    }

    /**
     * Create a identity matrix of type T.
     *
     * @param type of the elements
     * @param n    dimension of the NxN matrix
     * @param <T>  type of elements
     * @return identity matrix
     */
    public static <T extends Numeric<T>> T[][] identity(final T type, final int n) {
        // dimensionCheck(n);
        final T one = type.getOne();
        final T zero = type.getZero();
        final T[][] e = create(type, n, n);
        for (int i = 0; i < n; e[i][i] = one, i++)
            for (int j = 0; j < n; e[i][j] = zero, j++) ;

        return e;
    }

    /**
     * Create a N times N complex identity matrix.
     *
     * @param n the dimension
     * @return NxN identity matrix
     */
    public static Complex[][] complexIdentity(final int n) {
        return identity(ONE, n);
    }

    /**
     * Create a N times N rational identity matrix.
     *
     * @param n the dimension
     * @return NxN identity matrix
     */
    public static Rational[][] rationalIdentity(final int n) {
        return identity(RONE, n);
    }

    /**
     * Create a n times m float random matrix.
     *
     * @param n int 1.st dimension
     * @param m int 2.nd dimension
     * @return float[][] random matrix
     */
    public static float[][] rndFloatMatrix(final int n, final int m) {
        // dimensionCheck(n);
        // dimensionCheck(m);
        final float[][] r = new float[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; r[i][j] = (float) rndBox(-1, 1), j++) ;
        }
        return r;
    }

    /**
     * Create a n times m random matrix.
     *
     * @param n int 1.st dimension
     * @param m int 2.nd dimension
     * @return double[][] random matrix
     */
    public static double[][] rndMatrix(final int n, final int m) {
        // dimensionCheck(n);
        // dimensionCheck(m);
        final double[][] r = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; r[i][j] = rndBox(-1, 1), j++) ;
        }
        return r;
    }

    /**
     * Create a n times m random matrix of type T.
     *
     * @param o   a prototype of type T
     * @param n   int 1.st dimension
     * @param m   int 2.nd dimension
     * @param <T> the type
     * @return T[][] random matrix
     */
    public static <T extends Numeric<T>> T[][] rndMatrix(final T o, final int n, final int m) {
        final T[][] r = create(o, n, m);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; r[i][j] = o.rnd(), j++) ;
        return r;
    }

    /**
     * Create a n times m complex random matrix.
     *
     * @param n int 1.st dimension
     * @param m int 2.nd dimension
     * @return Complex[][] random matrix
     */
    public static Complex[][] rndComplexMatrix(final int n, final int m) {
        final Complex o = rndComplex();
        return rndMatrix(o, n, m);
    }

    /**
     * Create a n times m rational random matrix.
     *
     * @param n int 1.st dimension
     * @param m int 2.nd dimension
     * @return Rational[][] random matrix
     */
    public static Rational[][] rndRationalMatrix(final int n, final int m) {
        final Rational o = rndRational();
        return rndMatrix(o, n, m);
    }

    /**
     * Create a n times n random matrix.
     *
     * @param n the dimension
     * @return double[][] random matrix
     */
    public static double[][] rndMatrix(final int n) {
        // dimensionCheck(n);
        return rndMatrix(n, n);
    }

    /**
     * Create a n times n random float matrix.
     *
     * @param n the dimension
     * @return float[][] random matrix
     */
    public static float[][] rndFloatMatrix(final int n) {
        // dimensionCheck(n);
        return rndFloatMatrix(n, n);
    }

    /**
     * Create a n times n random symmetric matrix.
     *
     * @param n the dimension
     * @return double[][] random symmetric matrix
     */
    public static double[][] rndSymmetricMatrix(final int n) {
        // dimensionCheck(n);
        final double[][] a = rndMatrix(n);
        for (int j = 0; j < n; j++)
            for (int k = 0; k < j; k++)
                a[j][k] = a[k][j];
        return a;
    }

    /**
     * Create a n times n complex random matrix.
     *
     * @param n the dimension
     * @return Complex[][] random matrix
     */
    public static Complex[][] rndComplexMatrix(final int n) {
        return rndComplexMatrix(n, n);
    }

    /**
     * Create a n times n Rational random matrix.
     *
     * @param n the dimension
     * @return Rational[][] random matrix
     */
    public static Rational[][] rndRationalMatrix(final int n) {
        return rndRationalMatrix(n, n);
    }

    /**
     * Create a n times n hermitian random matrix.
     *
     * @param n the dimension
     * @return Complex[][] random matrix
     */
    public static Complex[][] rndHermitianMatrix(final int n) {
        Complex[][] c = rndComplexMatrix(n, n);
        final Complex[][] a = conjugate(c);
        c = add(c, a);
        return c;
    }

    /**
     * Create a n times n random orthonormal matrix.
     *
     * @param n the dimension
     * @return double[][] random orthonormal matrix
     */
    public static double[][] rndOrthonormalMatrix(final int n) {
        final double[][] p = identity(n);
        double[] w;
        w = rndVector(n);
        normalize(w);
        for (int j = 0; j < n; j++)
            for (int k = 0; k < n; k++)
                p[j][k] -= 2 * w[j] * w[k];

        // // check if all vectors are orthonormal
        // double check,eps = 10*n*DEPS;
        // for(int j=0;j<n;j++) {
        // for(int k=0;k<j;k++) {
        // check = abs(mult(p[j],p[k]));
        // if(check>eps) throw new IllegalStateException("not ON: "+check);
        // }
        // check = abs(mult(p[j],p[j]));
        // if(check-1>eps) throw new IllegalStateException("not ON: "+check);
        // }

        return p;
    }

    /**
     * Create a n times n random positive definite matrix.
     *
     * @param n the dimension
     * @return double[][] random positive definite matrix
     */
    public static double[][] rndPositiveDefinitMatrix(final int n) {
        // dimensionCheck(n);
        double[][] p = new double[n][n];
        double[][] r;
        for (int j = 0; j < n; p[j][j] = 1.E-3 + rndBox(), j++) ;
        r = rndOrthonormalMatrix(n);
        p = multTransposeA(r, mult(p, r));
        return p;
    }

    /**
     * Create the N,N dimensional tridiagonal Toeplitz matrix.
     *
     * @param dim the matrix dimension
     * @param a   the diagonal elements
     * @param b   the up diagonal elements
     * @param c   the lower diagonal elements
     * @return the matrix
     */
    public static double[][] createTridiagonalToeplitz(final int dim, final double a, final double b, final double c) {
        final double[][] mat = new double[dim][dim];
        int i;
        mat[0][0] = a;
        mat[0][1] = c;
        for (i = 1; i < dim - 1; i++) {
            mat[i][i] = a;
            mat[i][i + 1] = c;
            mat[i][i - 1] = b;
        }
        mat[i][i] = a;
        mat[i][i - 1] = b;
        return mat;
    }

    public static double[] eigenvaluesTridiagonalToeplitzMatrix(final int dim, final double a, final double b,
                                                                final double c) {
        final double[] ev = new double[dim];
        final double v = 2 * Math.sqrt(b * c);
        for (int k = 0; k < dim; k++) {
            ev[k] = a + v * Math.cos(Math.PI * (k + 1) / (dim + 1));
        }
        return ev;
    }

    /**
     * create the N,N dimensional nonesymmetric Pseudo Hilbert matrix.
     * The greatest eigenvalue of the matrix converges to pi. This
     * matrix seems to be due to: E. E. Tyrtyshnikov...
     * <p>
     * H_i,j = 1/(i-j+1/2)
     *
     * @param dim the matrix dimension
     * @return the matrix
     */
    public static double[][] createPseudoHilbertMatrix(final int dim) {
        double hij;
        final double[][] mat = new double[dim][dim];
        for (int i = 1; i <= dim; i++) {
            for (int j = 1; j <= dim; j++) {
                hij = 1.0 / (i - j + 0.5);
                mat[i - 1][j - 1] = hij;
            }
        }
        return mat;
    }

    /**
     * create the N,N dimensional symmetric Hilbert matrix.
     * A matrix with a very bad condition.
     * <p>
     * H_i,j = 1/(i+j-1)
     *
     * @param dim the matrix dimension
     * @return the matrix
     */
    public static double[][] createHilbertMatrix(final int dim) {
        double hij;
        final double[][] mat = new double[dim][dim];
        for (int i = 1; i <= dim; i++) {
            for (int j = 1; j <= i; j++) {
                hij = 1.0 / (i + j - 1);
                mat[i - 1][j - 1] = hij;
                mat[j - 1][i - 1] = hij;
            }
        }
        return mat;
    }

    /**
     * Create a default N,N dimensional Vandermonde matrix V, with the seed values
     * x[j] = 1/(j+1).
     *
     * @param n the matrix dimension
     * @return the Vandermonde matrix
     */
    public static double[][] createVandermondeMatrix(final int n) {
        final double[] x = new double[n];
        for (int j = 0; j < n; x[j] = 1.0 / (j + 2), j++) ;
        return createVandermondeMatrix(x);
    }

    /**
     * Create a default N,N dimensional Vandermonde matrix V, with the seed values
     * x[j] = 1/(j+1).
     *
     * @param n the matrix dimension
     * @return the Vandermonde matrix
     */
    public static double[][] createInverseVandermondeMatrix(final int n) {
        final double[] x = new double[n];
        for (int j = 0; j < n; x[j] = 1.0 / (j + 2), j++) ;
        return createInverseVandermondeMatrix(x);
    }

    /**
     * create the N,N dimensional Vandermonde matrix V.
     * <p>
     * V_i,j = x_i**j
     *
     * @param x the matrix column seeds
     * @return the Vandermonde matrix
     */
    public static double[][] createVandermondeMatrix(final double[] x) {
        final int n = x.length;
        double hij, xi;
        final double[][] mat = new double[n][n];
        for (int i = 0; i < n; i++) {
            hij = 1;
            xi = x[i];
            for (int j = 0; j < n; j++) {
                mat[i][j] = hij;
                hij *= xi;
            }
        }
        return mat;
    }

    /**
     * Create the N,N dimensional inverse Vandermonde matrix V**(-1).
     * The algorithmn uses an explizit formular for the inverse
     * of the LU decomposition of V.
     *
     * @param x the matrix column seeds
     * @return the inverse Vandermonde matrix
     */
    public static double[][] createInverseVandermondeMatrix(final double[] x) {
        final int n = x.length;
        double lij, xj;
        final double[][] invL = new double[n][n];
        final double[][] invU = new double[n][n];
        invL[0][0] = 1;
        for (int i = 1; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                lij = 1;
                xj = x[j];
                for (int k = 0; k <= i; k++) {
                    if (k != j)
                        lij *= (xj - x[k]);
                }
                invL[i][j] = 1.0 / lij;
            }
        }
        for (int i = 0; i < n; i++) {
            invU[i][i] = 1;
            // if(i>0) invU[i][0]=0;
            for (int j = i + 1; j < n; j++) {
                if (i == 0)
                    invU[i][j] = -invU[i][j - 1] * x[j - 1];
                else
                    invU[i][j] = invU[i - 1][j - 1] - invU[i][j - 1] * x[j - 1];
            }
        }

        return mult(invU, invL);
    }

    /**
     * create the N,N dimensional inverse symmetric Hilbert matrix.
     * A matrix with a very bad condition.
     * <pre>
     *
     *               (-1)**(i+j) (n+i-1)! (n+j-1)!
     *   inH_i,j = -----------------------------------------
     *              (i+j-1) [(i-1)!(j-1)!]**2 (n-i)!(n-j)!
     *
     * </pre>
     *
     * @param dim the matrix dimension
     * @return the matrix
     */
    public static double[][] createInverseHilbertMatrix(final int dim) {
        double hij;
        final double[][] mat = new double[dim][dim];
        int sign = 1;
        for (int i = 1; i <= dim; i++) {
            for (int j = 1; j <= i; j++) {
                hij = binomial(i + j - 2, i - 1);
                hij *= hij;
                hij *= binomial(dim + i - 1, dim - j) * binomial(dim + j - 1, dim - i);
                // sign = (i+j)%2==0 ? 1 : -1;
                sign = 1 - 2 * ((i + j) & 1);
                hij *= sign * (i + j - 1);
                mat[i - 1][j - 1] = hij;
                mat[j - 1][i - 1] = hij;
            }
        }
        return mat;
    }

    /**
     * Calculate the determinant of the inverse Hilbert matrix.
     * <pre>
     *               n-1         (2k) **2
     *   |H**(-1)| = prod (2k+1) (  )
     *               k=1         (k )
     * </pre>
     *
     * @param dim the matrix dimension
     * @return |H|**(-1)
     */
    public static double detInverseHilbertMatrix(final int dim) {
        double bi, det = 1;
        for (int k = 1; k < dim; k++) {
            bi = binomial(2 * k, k);
            det *= (2 * k + 1) * bi * bi;
        }
        return det;
    }

    /**
     * Return the eigenvalues of the first 10 hilbert matrix.
     * The values are adapted from:
     * <pre>
     * Eigenvalues and Eigenvectors of Hilbert Matrices of Order 3 Through 10
     * Henry E. Fettis and James C. Caslin, Math. Comp. (1967), pp. 431–441.
     * </pre>
     *
     * @param dim the matrix dimension
     * @return eigenvalues
     */
    public static double[] eigenvaluesHilbertMatrix(final int dim) {
        final double a = Math.sqrt(13. / 36);
        final double[][] evs = {
                 /* 1 */{1},
                 /* 2 */{2. / 3 + a, 2. / 3 - a},
                 /* 3 */{1.408318927123654, 1.223270658539058E-1, 2.687340355773529E-3},
                 /* 4 */{1.500214280059243, 1.691412202214500E-1, 6.738273605760748E-3, 9.670230402258689E-5},
                 /* 5 */{1.567050691098231, 2.08534218611013E-1, 1.140749162341981E-2, 3.05898040151191E-4,
                3.287928772171863E-6},
                 /* 6 */{1.618899858924339, 2.423608705752096E-1, 1.632152131987582E-2, 6.157483541826577E-4,
                1.257075712262519E-5, 1.082799484565550E-7},
                 /* 7 */{1.660885338926931, 2.719201981493452E-1, 2.128975490832795E-2, 1.008587610770142E-3,
                2.938636814592969E-5, 4.856763361574250E-7, 3.493898605991218E-9},
                 /* 8 */{1.695938996921949, 2.981252113169307E-1, 2.621284357811905E-2, 1.467688117741867E-3,
                5.436943369749942E-5, 1.294332091872811E-6, 1.798873745817577E-8, 1.111538966372442E-10},
                 /* 9 */{1.725882660901847, 3.216331222992068E-1, 3.103892578126833E-2, 1.978933860215924E-3,
                8.758085051459757E-5, 2.673013410599414E-6, 5.385613348522494E-8, 6.460905422638582E-10,
                3.499676402911493E-12},
                 /* 10 */{1.75191967026517, 3.429295484835091E-1, 3.574181627163924E-2, 2.530890768670038E-3,
                1.287496142763771E-4, 4.729689293182348E-6, 1.228967738751175E-7, 2.147438817350479E-9,
                2.266746747762926E-11, 1.093153819379666E-13}};
        if (dim < 1 || dim > 10)
            throw new IllegalArgumentException("wrong dimension:" + dim);
        return evs[dim - 1];
    }

    /**
     * Return the eigenvalues of the first 10 inverse hilbert matrix.
     *
     * @param dim the matrix dimension
     * @return eigenvalues
     */
    public static double[] eigenvaluesInverseHilbertMatrix(final int dim) {
        final double[] evs = new double[dim];
        final double[] ev = eigenvaluesHilbertMatrix(dim);
        for (int j = 0; j < evs.length; evs[j] = 1.0 / ev[dim - j - 1], j++) ;
        return evs;
    }

    /**
     * Calculate the determinant of the Hilbert matrix.
     *
     * @param dim the matrix dimension
     * @return |H|
     */
    public static double detHilbertMatrix(final int dim) {
        return 1.0 / detInverseHilbertMatrix(dim);
    }

    /**
     * Create a n dimensional random vector.
     *
     * @param n int the dimension
     * @return double[] random vector
     */
    public static double[] rndVector(final int n) {
        // dimensionCheck(n);
        final double[] r = new double[n];
        for (int i = 0; i < n; r[i] = rndBox(-1, 1), i++) ;
        return r;
    }

    /**
     * Create a n dimensional random vector.
     *
     * @param type to generate
     * @param n    int the dimension
     * @param <T>  the type
     * @return T[] random vector
     */
    public static <T extends Numeric<T>> T[] rndVector(final T type, final int n) {
        final T[] r = create(type, n);
        for (int i = 0; i < n; r[i] = type.rnd(), i++) ;
        return r;
    }

    /**
     * Create a n dimensional random vector.
     *
     * @param a   type to generate
     * @param n   int the dimension
     * @param <T> the type
     * @return T[] random vector
     */
    public static <T extends Numeric<T>> T[] rndVector(final T[] a, final int n) {
        return rndVector(a[0], n);
    }

    /**
     * Create a n dimensional random vector.
     *
     * @param a   type to generate
     * @param n   int the dimension
     * @param <T> the type
     * @return T[] random vector
     */
    public static <T extends Numeric<T>> T[] rndVector(final T[][] a, final int n) {
        return rndVector(a[0], n);
    }

    /**
     * Create a n dimensional complex random vector.
     *
     * @param n int the dimension
     * @return double[] random vector
     */
    public static Complex[] rndComplexVector(final int n) {
        // dimensionCheck(n);
        final Complex[] r = new Complex[n];
        for (int i = 0; i < n; r[i] = rndComplex(), i++) ;
        return r;
    }

    /**
     * Create a n dimensional complex Rational vector.
     *
     * @param n int the dimension
     * @return double[] random vector
     */
    public static Rational[] rndRationalVector(final int n) {
        // dimensionCheck(n);
        final Rational[] r = new Rational[n];
        for (int i = 0; i < n; r[i] = rndRational(), i++) ;
        return r;
    }

    /**
     * Make a deep copy of vector x.
     *
     * @param x float[] vector to copy.
     * @return float[] the copy.
     */
    public static float[] copy(final float[] x) {
        if (null == x) {
            // we don't like NP and return zero length array
            return new float[0];
        }
        final int n = x.length;
        final float[] y = new float[n];
        if (n > 0) {
            System.arraycopy(x, 0, y, 0, n);
        }
        return y;
    }

    /**
     * Make a deep copy of vector x.
     *
     * @param x double[] vector to copy.
     * @return double[] the copy.
     */
    public static double[] copy(final double[] x) {
        if (null == x) {
            // we don't like NP and return zero length array
            return new double[0];
        }
        final int n = x.length;
        final double[] y = new double[n];
        if (n > 0) {
            System.arraycopy(x, 0, y, 0, n);
        }
        return y;
    }

    /**
     * Make a deep copy of vector x.
     *
     * @param x   T[] vector to copy.
     * @param <T> the typ of the field array
     * @return T[] the copy.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Field<T>> T[] copy(final T[] x) {
        if (null == x) {
            // we don't like NP
            return (T[]) new Field[0];
        }
        final int n = x.length;
        final T[] y = Arrays.copyOf(x, n);
        return y;
    }

    /**
     * Make a deep copy of matrix x.
     *
     * @param x float[][] matrix to copy.
     * @return float[][] the copy.
     */
    public static float[][] copy(final float[][] x) {
        // if (null==x) {
        // // we don't like NP and return zero length matrix
        // return new float[0][0];
        // }
        final int n = x.length;
        final int m = x[0].length;
        final float[][] y = new float[n][m];
        for (int i = 0; i < n; i++) {
            System.arraycopy(x[i], 0, y[i], 0, m);
        }
        return y;
    }

    /**
     * Make a deep copy of matrix x.
     *
     * @param x double[][] matrix to copy.
     * @return double[][] the copy.
     */
    public static double[][] copy(final double[][] x) {
        // if (null==x) {
        // // we don't like NP and return zero length matrix
        // return new double[0][0];
        // }
        final int n = x.length;
        final int m = x[0].length;
        final double[][] y = new double[n][m];
        for (int i = 0; i < n; i++) {
            System.arraycopy(x[i], 0, y[i], 0, m);
        }
        return y;
    }

    /**
     * Make a deep copy of matrix x.
     *
     * @param x   T[][] matrix to copy.
     * @param <T> the type of the field elements
     * @return T[][] the copy.
     */
    public static <T extends Field<T>> T[][] copy(final T[][] x) {
        // if (null==x) {
        // // we don't like NP
        // throw new NullPointerException(MATRIX_IS_NULL);
        // }
        final int n = x.length;
        final int m = x[0].length;
        final T[][] y = Arrays.copyOf(x, n);
        for (int i = 0; i < n; i++) {
            if (x[i].length != m) {
                throw new IllegalArgumentException(ROWS_LENGTH_MISSMATCH);
            }
            y[i] = copy(x[i]);
        }
        return y;
    }

    /**
     * Swap element k with element j of vector v.
     * @param v  vector with elements to exchange
     * @param j  first index
     * @param k  second index
     */
    // public static void swap(final double[] v, final int j, final int k) {
    // double t = v[k];
    // v[k] = v[j];
    // v[j] = t;
    // }

    /**
     * Swap element k with element j of vector v.
     * @param v  vector with elements to exchange
     * @param j  first index
     * @param k  second index
     */
    // public static void swap(final long[] v, final int j, final int k) {
    // long t = v[k];
    // v[k] = v[j];
    // v[j] = t;
    // }

    /**
     * Swap element k with element j of vector v.
     * @param v  vector with elements to exchange
     * @param j  first index
     * @param k  second index
     */
    // public static void swap(final int[] v, final int j, final int k) {
    // int t = v[k];
    // v[k] = v[j];
    // v[j] = t;
    // }
    //

    /**
     * Create the strike matrix, that is a copy of x without
     * row i and column j.
     *
     * @param x double[][] the matrix to copy
     * @param i int the row to leave out
     * @param j int the column to leave out
     * @return double[][] the strike matrix
     */
    public static double[][] strike(final double[][] x, final int i, final int j) {
        final int n = x.length;
        final int m = x[0].length;
        int ir = 0, ic = 0;
        final double[][] strike = new double[n - 1][m - 1];
        for (int r = 0; r < n; r++) {
            if (r != i) {
                for (int c = 0; c < m; c++) {
                    if (c != j) {
                        strike[ir][ic] = x[r][c];
                        ic++;
                    }
                }
                ic = 0;
                ir++;
            }
        }
        return strike;
    }

    /**
     * Calculate the maximal {|x_0|, |x_1|, .... |x_n-1|} element, using the
     * absolute magnitude.
     *
     * @param x array or parameter list
     * @return the absolute maximal element
     */
    public static float maxabs(final float... x) {
        return maxnorm(x);
    }

    /**
     * Calculate the maximal {|x_0|, |x_1|, .... |x_n-1|} element, using the
     * absolute magnitude.
     *
     * @param x array or parameter list
     * @return the absolute maximal element
     */
    public static double maxabs(final double... x) {
        return maxnorm(x);
    }

    /**
     * Calculate the maximal {|x_0|, |x_1|, .... |x_n-1|} element, using the
     * absolute magnitude.
     *
     * @param x   array or parameter list
     * @param <T> type of the elements
     * @return the element with absolute maximal element
     */
    public static <T extends Numeric<T>> T maxabs(final T... x) {
        int i;
        final int n = x.length;
        T axi, amax = x[0].abs();
        for (i = 1; i < n; i++) {
            axi = x[i].abs();
            if (amax.lt(axi)) {
                amax = axi;
            }
        }
        return amax;
    }

    /**
     * Calculate the maximal {|x_0|, |x_1|, .... |x_n-1|} element, using the
     * absolute magnitude, beginning from the given start index.
     *
     * @param start index
     * @param x     array or parameter list
     * @return the absolute maximal element
     */
    public static double maxabs(final int start, final double... x) {
        int i;
        final int n = x.length;
        // double m = abs(x[start]);
        // for(i=start+1; i<n; m = max(m, abs(x[i])),i++);
        double axi, m = abs(x[start]);
        for (i = start + 1; i < n; i++) {
            axi = abs(x[i]);
            if (m < axi) {
                m = axi;
            }
        }
        return m;

    }

    /**
     * Check if the given matrix is symmetric, that is
     * A[i][j] == A[j][i].
     *
     * @param a double[][] the matrix to check
     * @return boolean indicating symmetry
     */
    public static boolean isSymmetric(final float[][] a) {
        boolean ret = isSquare(a);
        if (ret) {
            final int n = a.length;
            for (int i = 0; ret && i < n; i++) {
                for (int j = 0; ret && j < i; j++)
                    if (!isSimilar(a[i][j], a[j][i])) {
                        ret = false;
                    }

            }
        }
        return ret;
    }

    /**
     * Check if the given matrix is symmetric, that is
     * A[i][j] == A[j][i].
     *
     * @param a double[][] the matrix to check
     * @return boolean indicating symmetry
     */
    public static boolean isSymmetric(final double[][] a) {
        boolean ret = isSquare(a);
        if (ret) {
            final int n = a.length;
            for (int i = 0; ret && i < n; i++) {
                for (int j = 0; ret && j < i; j++)
                    if (!isSimilar(a[i][j], a[j][i])) {
                        ret = false;
                    }

            }
        }
        return ret;
    }

    /**
     * Check if the given matrix is symmetric, that is
     * A[i][j] == A[j][i].
     *
     * @param a   generic matrix to check
     * @param <T> type of matrix elements
     * @return boolean indicating symmetry
     */
    public static <T> boolean isSymmetric(final T[][] a) {
        boolean ret = isSquare(a);
        if (ret) {
            final int n = a.length;
            for (int i = 0; ret && i < n; i++) {
                for (int j = 0; ret && j < i; j++)
                    if (!a[i][j].equals(a[j][i])) {
                        ret = false;
                    }

            }
        }
        return ret;
    }

    /**
     * Check if the given matrix is self-adjoint, that is
     * A[i][j] == A*[j][i].
     *
     * @param a   generic matrix to check
     * @param <T> type of matrix elements
     * @return boolean indicating symmetry/hermitian
     */
    public static <T> boolean isSelfAdjoint(final T[][] a) {
        if (a[0][0] instanceof Complex) {
            return isHermitian((Complex[][]) a);
        }
        return isSymmetric(a);
    }

    /**
     * Check if the given matrix is self-adjoint, that is
     * A[i][j] == A*[j][i].
     *
     * @param a generic matrix to check
     * @return boolean indicating symmetry
     */
    public static boolean isSelfAdjoint(final double[][] a) {
        return isSymmetric(a);
    }

    /**
     * Check if the given matrix is hermitian (self-adjoint), that is
     * A[i][j] == A*[j][i].
     *
     * @param a complex matrix to check
     * @return boolean indicating symmetry
     */
    public static boolean isHermitian(final Complex[][] a) {
        boolean ret = isSquare(a);
        if (ret) {
            final int n = a.length;
            for (int i = 0; ret && i < n; i++) {
                if (a[i][i].imag() != 0) {
                    ret = false;
                    break;
                }
                for (int j = 0; j < i; j++) {
                    if (!isSimilar(a[i][j].real(), a[j][i].real()) || !isSimilar(a[i][j].imag(), -a[j][i].imag())) {
                        ret = false;
                        break;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Check if the given complex vector has only real coefficients.
     *
     * @param v complex vector to check
     * @return boolean indicating real vector
     */
    public static boolean isReal(final Complex[] v) {
        final int n = v.length;
        for (int i = 0; i < n; i++)
            if (!v[i].isReal())
                return false;

        return true;
    }

    /**
     * Check if the given complex matrix has only real coefficients.
     *
     * @param a complex matrix to check
     * @return boolean indicating real matrix
     */
    public static boolean isReal(final Complex[][] a) {
        final int n = a.length;
        for (int i = 0; i < n; i++)
            if (!isReal(a[i]))
                return false;

        return true;
    }

    /**
     * Check if the given complex vector has only imaginary coefficients.
     *
     * @param v complex vector to check
     * @return boolean indicating imaginary vector
     */
    public static boolean isImaginary(final Complex[] v) {
        final int n = v.length;
        for (int i = 0; i < n; i++)
            if (v[i].real() != 0)
                return false;

        return true;
    }

    /**
     * Check if the given complex matrix has only imaginary coefficients.
     *
     * @param a complex matrix to check
     * @return boolean indicating imaginary matrix
     */
    public static boolean isImaginary(final Complex[][] a) {
        final int n = a.length;
        for (int i = 0; i < n; i++)
            if (!isImaginary(a[i]))
                return false;

        return true;
    }

    /**
     * Check if the given NxM matrix is square, i.e. a NxN matrix.
     *
     * @param a float[][] the matrix to check
     * @return boolean indicating NxN
     */
    public static boolean isSquare(final float[][] a) {
        boolean ret = true;
        if (null == a) {
            ret = false;
        } else {
            final int n = a.length;
            ret = (n == a[0].length);
             /*
              * // time consuming ... for (int i = 0; ret && i<n; i++) { if (n!=a[i].length) { ret = false; } }
              */
        }
        return ret;
    }

    /**
     * Check if the given NxM matrix is square, i.e. a NxN matrix.
     *
     * @param a double[][] the matrix to check
     * @return boolean indicating NxN
     */
    public static boolean isSquare(final double[][] a) {
        boolean ret = true;
        if (null == a) {
            ret = false;
        } else {
            final int n = a.length;
            ret = (n == a[0].length);
             /*
              * // time consuming ... for (int i = 0; ret && i<n; i++) { if (n!=a[i].length) { ret = false; } }
              */
        }
        return ret;
    }

    /**
     * Check if the given NxM matrix is square, i.e. a NxN matrix.
     *
     * @param a   matrix of type T to check
     * @param <T> type of the matrix elements
     * @return boolean indicating NxN
     */
    public static <T> boolean isSquare(final T[][] a) {
        boolean ret = true;
        if (null == a) {
            ret = false;
        } else {
            final int n = a.length;
            ret = (n == a[0].length);
            // time consuming...
            // for (int i = 0; ret && i<n; i++) {
            // if (n!=a[i].length) {
            // ret = false;
            // }
            // }
        }
        return ret;
    }

    /**
     * Minimal check if matrix a can be positive definite.
     * Necessary but not sufficient are:
     * <pre>
     *
     * 1)  a[i][i] &gt; 0
     * 2) a)  a[i][i] + a[j][j] &gt; 2*a[i][j]
     *    b)  a[i][i] * a[j][j] &gt; a[i][j]**2
     * 3)  max |a[i][j]| is on the diagonal
     * 4)  det(a) &gt; 0
     *
     * </pre>
     * Only the first three conditions are checked
     *
     * @param a double[][] matrix to check
     * @return boolean positive definite flag
     */
    public static boolean isPositiveDefinite(final float[][] a) {
        boolean ret = isSquare(a);
        if (ret) {
            final int n = a.length;
            int i, j;
            float aii, ajj, aij, maxij = 0, maxii = 0;
            for (i = 0; ret && i < n; i++) {
                aii = a[i][i];
                ret = aii > 0;
                maxii = max(maxii, abs(aii));
                for (j = 0; ret && j < n; j++) {
                    if (i == j) {
                        continue;
                    }
                    ajj = a[j][j];
                    aij = a[i][j];
                    if ((aii + ajj < 2 * aij) || (aii * ajj < aij * aij)) {
                        ret = false;
                    } else {
                        maxij = max(maxij, abs(aij));
                    }
                }
            }
            ret &= maxij <= maxii;
        }
        return ret;
    }

    /**
     * Minimal check if matrix a can be positive definite.
     * Necessary but not sufficient are:
     * <pre>
     *
     * 1)  a[i][i] &gt; 0
     * 2) a)  a[i][i] + a[j][j] &gt; 2*a[i][j]
     *    b)  a[i][i] * a[j][j] &gt; a[i][j]**2
     * 3)  max |a[i][j]| is on the diagonal
     * 4)  det(a) &gt; 0
     *
     * </pre>
     * Only the first three conditions are checked
     *
     * @param a double[][] matrix to check
     * @return boolean positive definite flag
     */
    public static boolean isPositiveDefinite(final double[][] a) {
        boolean ret = isSquare(a);
        if (ret) {
            final int n = a.length;
            int i, j;
            double aii, ajj, aij, maxij = 0, maxii = 0;
            for (i = 0; ret && i < n; i++) {
                aii = a[i][i];
                ret = aii > 0;
                maxii = max(maxii, abs(aii));
                for (j = 0; ret && j < n; j++) {
                    if (i == j) {
                        continue;
                    }
                    ajj = a[j][j];
                    aij = a[i][j];
                    if ((aii + ajj < 2 * aij) || (aii * ajj < aij * aij)) {
                        ret = false;
                    } else {
                        maxij = max(maxij, abs(aij));
                    }
                }
            }
            ret &= maxij <= maxii;
        }
        return ret;
    }

    /**
     * Minimal check if matrix a can be positive definite.
     * Necessary but not sufficient are:
     * <pre>
     *
     * 1)  a[i][i] &gt; 0
     * 2) a)  a[i][i] + a[j][j] &gt; 2*a[i][j]
     *    b)  a[i][i] * a[j][j] &gt; a[i][j]**2
     * 3)  max |a[i][j]| is on the diagonal
     * 4)  det(a) &gt; 0
     *
     * </pre>
     * Only the first three conditions are checked
     *
     * @param a   T[][] matrix to check
     * @param <T> type of the elements
     * @return boolean positive definite flag
     */
    public static <T extends Numeric<T>> boolean isPositiveDefinite(final T[][] a) {
        boolean ret = isSquare(a);
        if (ret) {
            int i, j;
            final int n = a.length;
            final T zero = a[0][0].getZero();
            final T one = zero.getOne();
            final T two = one.multiply(2);
            T aii, ajj, aij;
            T maxij = zero, maxii = maxij;
            for (i = 0; ret && i < n; i++) {
                aii = a[i][i];
                ret = aii.gt(zero);
                maxii = fmax(maxii, aii);
                for (j = 0; ret && j < n; j++) {
                    if (i == j)
                        continue;
                    ajj = a[j][j];
                    aij = a[i][j];
                    if (aii.plus(ajj).lt(aij.multiply(two)) || aii.multiply(ajj).lt(aij.multiply(aij))) {
                        ret = false;
                    } else {
                        maxij = fmax(maxij, aij);
                    }
                }
            }
            ret &= maxij.leq(maxii);
        }
        return ret;
    }

    /**
     * Get the greater of the two numerics.
     *
     * @param x   first argument
     * @param y   second argumen
     * @param <T> the type
     * @return max(x, y)
     */
    public static <T extends Numeric<T>> T fmax(final T x, final T y) {
        if (y.lt(x))
            return x;
        return y;
    }

    /**
     * Check if the given matrix is diagonal dominant, that is
     * abs(A[i][i]) greater than sum abs(A[i][j]).
     *
     * @param a float[][] the matrix to check
     * @return boolean indicating diagonal dominance
     */
    public static boolean isDiagonalDominant(final float[][] a) {
        boolean ret = isSquare(a);
        final int n = a.length;
        float aii, aij;
        for (int i = 0; ret && i < n; i++) {
            aii = abs(a[i][i]);
            aij = norm1(a[i]) - aii;
            if (aii < aij) {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Check if the given matrix is diagonal dominant, that is
     * abs(A[i][i]) greater than sum abs(A[i][j]).
     *
     * @param a double[][] the matrix to check
     * @return boolean indicating diagonal dominance
     */
    public static boolean isDiagonalDominant(final double[][] a) {
        boolean ret = isSquare(a);
        final int n = a.length;
        double aii, aij;
        for (int i = 0; ret && i < n; i++) {
            aii = abs(a[i][i]);
            aij = norm1(a[i]) - aii;
            if (aii < aij) {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Check if the given matrix is diagonal dominant, that is
     * abs(A[i][i]) greater than sum abs(A[i][j]).
     *
     * @param a   T[][] matrix to check
     * @param <T> type of the elements
     * @return boolean indicating diagonal dominance
     */
    public static <T extends Numeric<T>> boolean isDiagonalDominant(final T[][] a) {
        boolean ret = isSquare(a);
        final int n = a.length;
        T aii, aij;
        for (int i = 0; ret && i < n; i++) {
            aii = a[i][i].abs();
            aij = norm1(a[i]).minus(aii);
            if (aii.lt(aij)) {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Check if the given matrix is diagonal dominant using the
     * given (pivot) reordering p, that is abs(A[p[i]][i]) is greater
     * than sum abs(A[p[i]][j]).
     *
     * @param p int[] (pivot) permutation of the rows
     * @param a double[][] the matrix to check
     * @return boolean indicating diagonal dominance
     */
    public static boolean isDiagonalDominant(final int[] p, final double[][] a) {
        boolean ret = isSquare(a);
        final int n = a.length;
        double aii, aij;
        for (int i = 0; ret && i < n; i++) {
            aii = abs(a[p[i]][i]);
            aij = norm1(a[p[i]]) - aii;
            if (aii < aij) {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Check if the given matrix is diagonal dominant using the
     * given (pivot) reordering p, that is abs(A[p[i]][i]) is greater
     * than sum abs(A[p[i]][j]).
     *
     * @param p   int[] (pivot) permutation of the rows
     * @param a   double[][] the matrix to check
     * @param <T> the type
     * @return boolean indicating diagonal dominance
     */
    public static <T extends Numeric<T>> boolean isDiagonalDominant(final int[] p, final T[][] a) {
        boolean ret = isSquare(a);
        final int n = a.length;
        T aii, aij;
        for (int i = 0; ret && i < n; i++) {
            aii = a[p[i]][i].abs();
            aij = norm1(a[p[i]]).minus(aii);
            if (aii.lt(aij)) {
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Quick check, if a matrix is regular or might be singular.
     * If all eigenvalues are either positive or all are negative
     * the matrix cannot be singular, but is definite regular,
     * otherwise it depends on the exact location of the eigenvalues
     * not checked by this method.
     *
     * @param a double[][] the matrix to check
     * @return boolean indicating matrix is not singular
     */
    public static boolean isDefiniteRegular(final double[][] a) {
        final double[] minmax = eigenvalueRange(a);
        final double min = minmax[0];
        final double max = minmax[1];
        if (0 < min) {
            // all eigenvalues are positive
            return true;
        }
        if (max < 0) {
            // all eigenvalues are negative
            return true;
        }
        // exact result is not known by this method...
        return false;
    }

    /**
     * Using the Gerschgorin circles find the real range [a,b] which holds all
     * eigenvalues of matrix a.
     *
     * @param a double[][] the matrix to check
     * @return double[2] array [minEV, maxEV] range of the eigenvalues.
     */
    public static double[] eigenvalueRange(final double[][] a) {
        final int n = a.length;
        double sum, left = Double.MAX_VALUE, right = -left;
        for (int i = 0; i < n; i++) {
            sum = 0;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    sum += abs(a[i][j]);
                }
            }
            left = min(left, a[i][i] - sum);
            right = max(right, a[i][i] + sum);
        }
        return new double[]{left, right};
    }

    /**
     * Get the row vector of a NxM matrix.
     *
     * @param mat the matrix
     * @param row the row index
     * @return mat[row] vector
     */
    public static double[] getRow(final double[][] mat, final int row) {
        final int n = mat.length;
        final int m = mat[0].length;
        final double[] v = new double[m];
        if (row < 0 || row >= n) {
            throw new IllegalArgumentException(format(ROW_MISMATCH, row, n));
        }
        for (int i = 0; i < m; i++) {
            v[i] = mat[row][i];
        }
        return v;
    }

    /**
     * Get the row vector of a NxM matrix.
     *
     * @param mat the matrix
     * @param row the row index
     * @param <T> the elements type
     * @return mat[row] vector
     */
    public static <T extends Numeric<T>> T[] getRow(final T[][] mat, final int row) {
        final int n = mat.length;
        final int m = mat[0].length;
        final T[] v = create(mat, m);
        if (row < 0 || row >= n) {
            throw new IllegalArgumentException(format(ROW_MISMATCH, row, n));
        }
        for (int i = 0; i < m; i++) {
            v[i] = mat[row][i];
        }
        return v;
    }

    /**
     * Get the column vector of a NxM matrix.
     *
     * @param mat the matrix
     * @param col the column index
     * @return mat[?][col] vector
     */
    public static float[] getCol(final float[][] mat, final int col) {
        final int n = mat.length;
        final int m = mat[0].length;
        final float[] v = new float[n];
        if (col < 0 || col >= m) {
            throw new IllegalArgumentException(format(COL_MISMATCH, col, m));
        }
        for (int i = 0; i < n; v[i] = mat[i][col], i++) ;
        return v;
    }

    /**
     * Get the column vector of a NxM matrix.
     *
     * @param mat the matrix
     * @param col the column index
     * @return mat[?][col] vector
     */
    public static double[] getCol(final double[][] mat, final int col) {
        final int n = mat.length;
        final int m = mat[0].length;
        final double[] v = new double[n];
        if (col < 0 || col >= m) {
            throw new IllegalArgumentException(format(COL_MISMATCH, col, m));
        }
        for (int i = 0; i < n; v[i] = mat[i][col], i++) ;
        return v;
    }

    /**
     * Get the column vector of a NxM matrix.
     *
     * @param mat the matrix
     * @param col the column index
     * @param <T> the elements type
     * @return mat[?][col] vector
     */
    public static <T extends Numeric<T>> T[] getCol(final T[][] mat, final int col) {
        final int n = mat.length;
        final int m = mat[0].length;
        final T[] v = create(mat, n);
        if (col < 0 || col >= m) {
            throw new IllegalArgumentException(format(COL_MISMATCH, col, m));
        }
        for (int i = 0; i < n; v[i] = mat[i][col], i++) ;
        return v;
    }

    /**
     * Set the column vector of a NxM matrix.
     *
     * @param mat the matrix
     * @param col the column index
     * @param v   the vector to set
     */
    public static void setCol(final float[][] mat, final int col, final float[] v) {
        final int n = mat.length;
        final int m = mat[0].length;
        if (col < 0 || col >= m) {
            throw new IllegalArgumentException(format(COL_MISMATCH, col, m));
        }
        if (n != v.length) {
            throw new IllegalArgumentException(format(COL_MISMATCH, v.length, n));
        }
        for (int i = 0; i < n; i++) {
            mat[i][col] = v[i];
        }
    }

    /**
     * Set the column vector of a NxM matrix.
     *
     * @param mat the matrix
     * @param col the column index
     * @param v   the vector to set
     */
    public static void setCol(final double[][] mat, final int col, final double[] v) {
        final int n = mat.length;
        final int m = mat[0].length;
        if (col < 0 || col >= m) {
            throw new IllegalArgumentException(format(COL_MISMATCH, col, m));
        }
        if (n != v.length) {
            throw new IllegalArgumentException(format(COL_MISMATCH, v.length, n));
        }
        for (int i = 0; i < n; i++) {
            mat[i][col] = v[i];
        }
    }

    /**
     * Set the column vector of a NxM matrix.
     *
     * @param mat the matrix
     * @param col the column index
     * @param v   the vector to set
     * @param <T> the elements type
     */
    public static <T extends Numeric<T>> void setCol(final T[][] mat, final int col, final T[] v) {
        final int n = mat.length;
        final int m = mat[0].length;
        if (col < 0 || col >= m) {
            throw new IllegalArgumentException(format(COL_MISMATCH, col, m));
        }
        if (n != v.length) {
            throw new IllegalArgumentException(format(COL_MISMATCH, v.length, n));
        }
        for (int i = 0; i < n; i++) {
            mat[i][col] = v[i];
        }
    }

    /**
     * Calculate the k-th power of matrix A.
     *
     * @param a double[][] the matrix
     * @param k the exponent not negative
     * @return double[][] the k-the power
     */
    public static double[][] pow(final double[][] a, final int k) {
        squareCheck(a);
        double[][] b;
        final int n = a.length;
        if (k < 0) {
            throw new IllegalArgumentException("negative power");
        } else if (k == 0) {
            b = identity(n);
        } else if (k == 1) {
            b = a;
        } else {
            b = pow(a, k / 2);
            b = mult(b, b);
            if ((k & 1) == 1) {
                b = mult(a, b);
            }
        }
        return b;
    }

    /**
     * Calculate the characteristic polynomial of A.
     *
     * @param a the matrix elements of A
     * @return the characteristic polynomial of A
     */
    public static Polynomial characteristicPolynomial(final double[][] a) {
        return new Polynomial(characteristic(a));
    }

    /**
     * Calculate the coefficient of the characteristic polynomial of a
     * NxN square matrix A.
     * This algorithm uses the method of Faddejew-Leverrier.
     *
     * @param a the matrix elements of A
     * @return the characteristic polynomial coefficients
     */
    public static double[] characteristic(final double[][] a) {
        squareCheck(a);
        final int n = a.length;
        int j, k;
        double nbk, tr;
        double[][] bk = identity(n);
        final double[] c = new double[n + 1];
        c[n] = 1;
        for (k = 1; k <= n; k++) {
            bk = mult(a, bk);
            for (j = 0, tr = 0; j < n; tr += bk[j][j], j++) ;
            for (j = 0, tr /= k; j < n; bk[j][j] -= tr, j++) ;
            c[n - k] = -tr;
        }
        // check if the calculation is numerical correct.
        nbk = norm(bk);
        if (abs(c[0]) > 1)
            nbk /= n * n * abs(c[0]);
        if (nbk > Accuracy.FEPS) {
            String msg = format("characteristic polynomial p%s numerical instable %.3g", Strings.toLowerScript(n), nbk);
            getLogger().error(msg);
            // throw new IllegalStateException(msg);
        }
        return c;
    }

    /**
     * Create a new random complex number.
     *
     * @return Complex(rnd, rnd)
     */
    public static Complex rndComplex() {
        return newComplex(rndBox(), rndBox());
    }

    /**
     * Create a new complex with zero value.
     *
     * @return Complex(0, 0)
     */
    public static Complex newComplex() {
        return newComplex(0, 0);
    }

    /**
     * Create a new complex with given real value.
     *
     * @param re real part for this complex
     * @return Complex(re, 0)
     */
    public static Complex newComplex(final double re) {
        return newComplex(re, 0);
    }

    /**
     * Factory method: Create a new complex with given real and imaginary part.
     *
     * @param re real part for this complex
     * @param im imaginary part for this complex
     * @return Complex(re, im)
     */
    public static Complex newComplex(final double re, final double im) {
        return COMPLEX.newComplex(re, im);
    }

    /**
     * Create a new random rational number.
     *
     * @return Complex(rnd, rnd)
     */
    public static Rational rndRational() {
        final double range = 1000;
        return newRational((long) rndBox(-range, range), (long) rndBox(1, range));
    }

    /**
     * Create a new rational with zero value.
     *
     * @return Rational(0, 1)
     */
    public static Rational newRational() {
        return newRational(0, 1);
    }

    /**
     * Create a new rational with given numerator and divider 1.
     *
     * @param n the  numerator
     * @return Rational n/1
     */
    public static Rational newRational(final long n) {
        return newRational(n, 1);
    }

    /**
     * Factory method: Create a new rational with given numerator and divider.
     *
     * @param n the numerator
     * @param d the divider
     * @return Rational n/d
     */
    public static Rational newRational(final long n, final long d) {
        return RATIONAL.newRational(n, d);
    }

    /**
     * Factory method: Create a new complex with given real and imaginary part.
     *
     * @param left  the left interval border
     * @param right the right interval border
     * @return Interval [left,right]
     */
    public static Interval newRational(final double left, final double right) {
        return INTERVAL.newInterval(left, right);
    }

    /**
     * Return the real part of matrix c.
     *
     * @param c complex input matrix
     * @return real part of c
     */
    public static double[][] real(final Complex[][] c) {
        final int n = c.length;
        final int m = c[0].length;
        final double[][] b = new double[n][m];
        for (int j = 0; j < n; j++)
            for (int k = 0; k < m; k++)
                b[j][k] = c[j][k].real();

        return b;
    }

    /**
     * Return the imaginary part of matrix c.
     *
     * @param c complex input matrix
     * @return real part of c
     */
    public static double[][] imag(final Complex[][] c) {
        final int n = c.length;
        final int m = c[0].length;
        final double[][] b = new double[n][m];
        for (int j = 0; j < n; j++)
            for (int k = 0; k < m; k++)
                b[j][k] = c[j][k].imag();
        return b;
    }

    /**
     * Return the real part of vector a.
     *
     * @param a complex input vector
     * @return real part of a
     */
    public static double[] real(final Complex[] a) {
        final int n = a.length;
        final double[] b = new double[n];
        for (int j = 0; j < n; j++)
            b[j] = a[j].real();

        return b;
    }

    /**
     * Return the imaginary part of vector a.
     *
     * @param a complex input vector
     * @return imaginary part of a
     */
    public static double[] imag(final Complex[] a) {
        final int n = a.length;
        final double[] b = new double[n];
        for (int j = 0; j < n; j++)
            b[j] = a[j].imag();
        return b;
    }

    /**
     * To solve the system C*z = b decompose it into a real system via
     * <p>
     * C = (A + jB), z=(x+jy) and b=(c + jd) using reals.
     * <p>
     * This method returns the corresponding real matrix for the system:
     * <pre>
     *        ( A  | -B ) ( x )   ( c )
     *        (----+----) (   ) = (   )
     *        ( B  |  A ) ( y )   ( d )
     * </pre>
     *
     * @param c complex matrix to transform
     * @return the real representation
     */
    public static double[][] createRealMatrix(final Complex[][] c) {
        final int n = c.length;
        final int m = c[0].length;
        final double[][] sys = new double[2 * n][2 * m];
        final double[][] a = LinearAlgebra.real(c);
        final double[][] b = imag(c);
        for (int j = 0; j < n; j++) {
            for (int k = 0; k < m; k++) {
                sys[j][k] = a[j][k];
                sys[n + j][m + k] = a[j][k];
                sys[j][m + k] = -b[j][k];
                sys[n + j][k] = b[j][k];
            }
        }
        return sys;
    }

    /**
     * Transform the complex vector z into a real one twice as long with
     * the first half the real and the second half the imaginary part.
     *
     * @param z the complex input vector
     * @return the tuple (real(z), imag(z))
     */
    public static double[] createRealVector(final Complex[] z) {
        final int n = z.length;
        final double[] v = new double[2 * n];
        final double[] c = real(z);
        final double[] d = imag(z);
        for (int j = 0; j < n; j++) {
            v[j] = c[j];
            v[n + j] = d[j];
        }
        return v;
    }

    /**
     * Create a complex vector from the real and imaginary parts.
     *
     * @param real input vector
     * @param imag imaginary part of the vector
     * @return complex vector.
     */
    public static Complex[] createComplexVector(final double[] real, final double[] imag) {
        final int n = real.length;
        if (imag.length != n)
            throw new IllegalArgumentException("#real != #imag");
        final Complex[] v = new Complex[n];
        for (int j = 0; j < n; j++) {
            v[j] = newComplex(real[j], imag[j]);
        }
        return v;
    }

    /**
     * Create a complex vector from the real input vector, where the first
     * half are the real and the second half are the and imaginary parts.
     *
     * @param v input vector
     * @return complex vector.
     */
    public static Complex[] createComplexVector(final double[] v) {
        if (v.length % 2 != 0)
            throw new IllegalArgumentException("not a real/imag input vector");
        final int n = v.length / 2;
        final Complex[] z = new Complex[n];
        for (int j = 0; j < n; j++) {
            z[j] = newComplex(v[j], v[n + j]);
        }
        return z;
    }

    /**
     * Create a vector of type T from the real input vector.
     *
     * @param o   the type to use
     * @param a   input vector
     * @param <T> the type
     * @return vector for given type.
     */
    public static <T extends Numeric<T>> T[] createVector(final T o, final double[] a) {
        final int n = a.length;
        final T one = o.getOne();
        final T[] z = create(o, n);
        for (int j = 0; j < n; z[j] = one.multiply(a[j]), j++) ;
        return z;
    }

    /**
     * Create a matrix of type T from the real input matrix.
     *
     * @param o   the type to use
     * @param a   input matrix
     * @param <T> the type
     * @return matrix for given type.
     */
    public static <T extends Numeric<T>> T[][] createMatrix(final T o, final double[][] a) {
        final int n = a.length;
        final int m = a[0].length / 2;
        final T[][] z = create(o, n, m);
        for (int j = 0; j < n; z[j] = createVector(o, a[j]), j++) ;
        return z;
    }

    /**
     * Create a complex matrix from the real input matrix, where the first
     * half are the real and the second half are the and imaginary parts.
     *
     * @param a input matrix
     * @return complex matrix.
     */
    public static Complex[][] createComplexMatrix(final double[][] a) {
        if (a.length % 2 != 0 || a[0].length % 2 != 0)
            throw new IllegalArgumentException("not a real/imag input matrix");
        final int n = a.length / 2;
        final int m = a[0].length / 2;
        final Complex[][] z = new Complex[n][m];
        for (int j = 0; j < n; j++) {
            for (int k = 0; k < m; k++) {
                z[j][k] = newComplex(a[j][k], a[j + n][k]);
            }
        }
        return z;
    }

    /**
     * Create a n times n diagonal matrix with given eigenvalues.
     *
     * @param ev the n eigenvalues
     * @return double[][] diagonal matrix
     */
    public static double[][] createDiagonalMatrix(final double[] ev) {
        final int n = ev.length;
        final double[][] p = new double[n][n];
        for (int j = 0; j < n; p[j][j] = ev[j], j++) ;
        return p;
    }

    /**
     * Create a n times n random matrix with given eigenvalues.
     *
     * @param ev the n eigenvalues
     * @return double[][] random matrix
     */
    public static double[][] createRndMatrix(final double[] ev) {
        // Solver solver = L4MLoader.load(Solver.class);
        final int n = ev.length;
        double[][] p = createDiagonalMatrix(ev);
        final double[][] u = rndOrthonormalMatrix(n);
        // p = mult(p,u);
        p = multTransposeA(u, mult(p, u));
        return p;
    }

    /**
     * Create a n times n random symmetric matrix with given eigenvalues.
     *
     * @param ev the n eigenvalues
     * @return double[][] random matrix
     */
    public static double[][] createRndSymmetricMatrix(final double[] ev) {
        // Solver solver = L4MLoader.load(Solver.class);
        final int n = ev.length;
        double[][] p = createDiagonalMatrix(ev);
        final double[][] u = rndOrthonormalMatrix(n);
        p = multTransposeA(u, mult(p, u));
        return p;
    }

    /**
     * Create a n times n random matrix.
     *
     * @param n the dimension
     * @return double[][] random matrix
     */
    public static double[][] createRndMatrix(final int n) {
        return rndMatrix(n);
    }

    /**
     * Create a n times NxM random matrix.
     *
     * @param n cols dimension
     * @param m rows dimension
     * @return double[][] random matrix
     */
    public static double[][] createRndMatrix(final int n, final int m) {
        return rndMatrix(n, m);
    }

    /**
     * Return the sign vector of x. For all x[j] &ge; 0 sign is +1
     * other wise -1.
     *
     * @param x the input vector
     * @return sign(x)
     */
    public static double[] sign(final double[] x) {
        final int n = x.length;
        final double[] s = new double[n];
        // for(int j=0;j<n; s[j] = x[j]>=0 ? 1:-1,j++);
        for (int j = 0; j < n; j++)
            if (x[j] >= 0)
                s[j] = 1;
            else
                s[j] = -1;

        return s;
    }

    /**
     * Return absolute values of a  vector of x.
     *
     * @param x the input vector
     * @return abs(x)
     */
    public static double[] vabs(final double[] x) {
        final int n = x.length;
        final double[] s = new double[n];
        for (int j = 0; j < n; s[j] = abs(x[j]), j++) ;
        return s;
    }

    /**
     * Return the index j of the absolute maximal x[j] value of x.
     *
     * @param x the input vector
     * @return index of the absolute greatest element
     */
    public static int maxindex(final double[] x) {
        int k = 0;
        final int n = x.length;
        double max = abs(x[k]);
        for (int j = 1; j < n; j++)
            if (max < abs(x[j])) {
                k = j;
                max = abs(x[j]);
            }
        return k;
    }

    /**
     * Estimate the L1 condition of a square matrix A.
     * This algorithm gives a lower bound and is based on the
     * approximation of
     * <pre>
     * W.W. Hagger: "Condition estimates", SIAM (1984)
     * </pre>
     * and
     * <pre>
     * N.J. Higham: "FORTAN Codes for estimations of the one-norm
     * of a real or complex matrix", ACM (1988)
     * </pre>
     *
     * @param a the real matrix coefficients
     * @return an approximation for cond(A).
     */
    public static double cond(final double[][] a) {
        final Solver solver = L4MLoader.load(Solver.class);
        squareCheck(a);
        double dot, max, cond = 0;
        int k = 0;
        final int n = a.length;
        final double[][] at = transpose(a);
        double[] z, y;
        final double[] x = new double[n];
        for (int j = 0; j < n; x[j] = 1.0 / n, j++) ;
        try {
            do {
                y = solver.solve(a, x);
                z = solver.solve(at, sign(y));
                dot = mult(z, x);
                max = norm8(z);
                cond = norm1(y);
                for (int j = 0; j < n; x[j] = 0, j++) ;
                x[maxindex(z)] = 1;
            } while (max > dot && ++k < n);
        } catch (final SingularException e) {
            // if matrix is singular it has bad condition...
            cond = Double.MAX_VALUE;
        }
        return cond;
    }

    /**
     * L1 condition of a real square matrix.
     *
     * @param a the matrix
     * @return L1 condition
     */
    public static float cond(final float[][] a) {
        return asFloat(cond(asDouble(a)));
    }

    static final class Add<T extends Numeric<T>> implements BinaryOperator<T> {
        /*
          * (non-Javadoc)
          *
          * @see de.lab4inf.math.BinaryOperator#op(java.lang.Object, java.lang.Object)
          */
        @Override
        public T op(final T x, final T y) {
            return x.plus(y);
        }
    }

    // static final class Mul<T extends Numeric<T>> implements BinaryOperator<T> {
    // /* (non-Javadoc)
    // * @see de.lab4inf.math.BinaryOperator#op(java.lang.Object, java.lang.Object)
    // */
    // @Override
    // public T op(final T x, final T y) {
    // return x.multiply(y);
    // }
    // }
    static final class Sub<T extends Numeric<T>> implements BinaryOperator<T> {
        /*
          * (non-Javadoc)
          *
          * @see de.lab4inf.math.BinaryOperator#op(java.lang.Object, java.lang.Object)
          */
        @Override
        public T op(final T x, final T y) {
            return x.minus(y);
        }
    }
    // static final class Div<T extends Numeric<T>> implements BinaryOperator<T> {
    // /* (non-Javadoc)
    // * @see de.lab4inf.math.BinaryOperator#op(java.lang.Object, java.lang.Object)
    // */
    // @Override
    // public T op(final T x, final T y) {
    // return x.div(y);
    // }
    // }
}
 