/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math.util;

import de.lab4inf.math.L4MObject;

import static java.lang.String.format;

/**
 * Helper class to merge arrays/vector fields.
 * Given some field
 * <pre>
 *      x={x_0, x_1, ... ,x_n}
 * </pre>
 * and some indicator which elements are flexible
 * construct the final output array
 * <pre>
 *      z={x_0, y_0, x_2, ... y_m,....,x_n}
 * </pre>
 * The default x0 and the changeable field have to
 * be set before the first use of the merge method.
 *
 * @author nwulff
 * @version $Id: ParameterMerge.java,v 1.3 2014/11/18 23:41:21 nwulff Exp $
 * @since 06.07.2010
 */
public class ParameterMerge extends L4MObject {
    /**
     * dimension mismatch format string.
     */
    private static final String DIMENSION_MISMATCH = "dimension missmatch %d!=%d";
    /**
     * parameter mismatch format string.
     */
    private static final String PARAMETER_MISMATCH = "parameter missmatch %d!=%d";
    private boolean[] changeable = new boolean[0];
    private double[] x0;
    private int n, m;

    /**
     * Set the fixed part of the merge field and indicate with
     * dimensions are variable or static with the boolean field.
     *
     * @param x    the default static x0 elements of the field
     * @param vars boolean indicator for changeable elements within x0
     */
    public void setFixture(final double[] x, final boolean[] vars) {
        n = x.length;
        setX0(x);
        setChangeable(vars);
    }

    /**
     * Set the x0 default field to merge.
     *
     * @param x the default field
     */
    public void setX0(final double... x) {
        final int p = x.length;
        if (n != p) {
            final String msg = format(DIMENSION_MISMATCH, n, p);
            logger.info(msg);
            throw new IllegalArgumentException(msg);
        }
        x0 = x;
    }

    /**
     * Set the indicator for the changeable field elements.
     *
     * @param vars boolean field indicating changeable elements
     */
    public void setChangeable(final boolean... vars) {
        final int q = vars.length;
        if (n != q) {
            final String msg = format(DIMENSION_MISMATCH, n, q);
            logger.info(msg);
            throw new IllegalArgumentException(msg);
        }
        changeable = vars;
        m = 0;
        for (int i = 0; i < n; i++) {
            if (vars[i]) {
                m++;
            }
        }
    }

    /**
     * Merge the y field with the default preset x0 field
     *
     * @param y field elements to merge
     * @return x (x0,y) fields merged
     */
    public double[] merge(final double... y) {
        return merge(x0, y);
    }

    /**
     * Merge the (z,y) fields, the z field will be overwritten.
     *
     * @param z the n-dimensional field
     * @param y the m-dimensional variable part to merge
     * @return the merger of both
     */
    public double[] merge(final double[] z, final double... y) {
        int j = 0;
        final int p = z.length, q = y.length;
        if (n != p) {
            final String msg = format(DIMENSION_MISMATCH, n, p);
            logger.info(msg);
            throw new IllegalArgumentException(msg);
        }
        if (m != q) {
            final String msg = format(PARAMETER_MISMATCH, m, q);
            logger.info(msg);
            throw new IllegalArgumentException(msg);
        }
        for (int i = 0; i < n; i++) {
            if (changeable[i]) {
                z[i] = y[j++];
            }
        }
        return z;
    }
}
 