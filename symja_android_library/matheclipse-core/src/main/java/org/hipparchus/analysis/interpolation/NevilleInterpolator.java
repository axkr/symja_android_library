/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hipparchus.analysis.interpolation;

import org.hipparchus.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.hipparchus.exception.MathIllegalArgumentException;

import java.io.Serializable;

/**
 * Implements the <a href="http://mathworld.wolfram.com/NevillesAlgorithm.html">
 * Neville's Algorithm</a> for interpolation of real univariate functions. For
 * reference, see <b>Introduction to Numerical Analysis</b>, ISBN 038795452X,
 * chapter 2.
 * <p>
 * The actual code of Neville's algorithm is in PolynomialFunctionLagrangeForm,
 * this class provides an easy-to-use interface to it.</p>
 */
public class NevilleInterpolator implements UnivariateInterpolator,
        Serializable {

    /**
     * serializable version identifier
     */
    static final long serialVersionUID = 3003707660147873733L;

    /**
     * Computes an interpolating function for the data set.
     *
     * @param x Interpolating points.
     * @param y Interpolating values.
     * @return a function which interpolates the data set
     * @throws MathIllegalArgumentException if the array lengths are different.
     * @throws MathIllegalArgumentException if the number of points is less than 2.
     * @throws MathIllegalArgumentException if two abscissae have the same
     *                                      value.
     */
    @Override
    public PolynomialFunctionLagrangeForm interpolate(double x[], double y[])
            throws MathIllegalArgumentException {
        return new PolynomialFunctionLagrangeForm(x, y);
    }
}
