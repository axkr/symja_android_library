/*
 * Licensed to the Hipparchus project under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Hipparchus project licenses this file to You under the Apache License, Version 2.0
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

package org.hipparchus.ode;

import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;

import java.io.Serializable;

/**
 * Class mapping the part of a complete state or derivative that pertains
 * to a specific differential equation.
 * <p>
 * Instances of this class are guaranteed to be immutable.
 * </p>
 *
 * @see SecondaryODE
 */
public class EquationsMapper implements Serializable {

    /**
     * Serializable UID.
     */
    private static final long serialVersionUID = 20160327L;

    /**
     * Start indices of the components.
     */
    private final int[] start;

    /**
     * Create a mapper by adding a new equation to another mapper.
     * <p>
     * The new equation will have index {@code mapper.}{@link #getNumberOfEquations()},
     * or 0 if {@code mapper} is null.
     * </p>
     *
     * @param mapper    former mapper, with one equation less (null for first equation)
     * @param dimension dimension of the equation state vector
     */
    EquationsMapper(final EquationsMapper mapper, final int dimension) {
        final int index = (mapper == null) ? 0 : mapper.getNumberOfEquations();
        this.start = new int[index + 2];
        if (mapper == null) {
            start[0] = 0;
        } else {
            System.arraycopy(mapper.start, 0, start, 0, index + 1);
        }
        start[index + 1] = start[index] + dimension;
    }

    /**
     * Get the number of equations mapped.
     *
     * @return number of equations mapped
     */
    public int getNumberOfEquations() {
        return start.length - 1;
    }

    /**
     * Return the dimension of the complete set of equations.
     * <p>
     * The complete set of equations correspond to the primary set plus all secondary sets.
     * </p>
     *
     * @return dimension of the complete set of equations
     */
    public int getTotalDimension() {
        return start[start.length - 1];
    }

    /**
     * Map flat arrays to a state and derivative.
     *
     * @param t    time
     * @param y    state array to map, including primary and secondary components
     * @param yDot state derivative array to map, including primary and secondary components
     * @return mapped state
     * @throws MathIllegalArgumentException if an array does not match total dimension
     */
    public ODEStateAndDerivative mapStateAndDerivative(final double t, final double[] y, final double[] yDot)
            throws MathIllegalArgumentException {

        if (y.length != getTotalDimension()) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                    y.length, getTotalDimension());
        }

        if (yDot.length != getTotalDimension()) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                    yDot.length, getTotalDimension());
        }

        final int n = getNumberOfEquations();
        int index = 0;
        final double[] state = extractEquationData(index, y);
        final double[] derivative = extractEquationData(index, yDot);
        if (n < 2) {
            return new ODEStateAndDerivative(t, state, derivative);
        } else {
            final double[][] secondaryState = new double[n - 1][];
            final double[][] secondaryDerivative = new double[n - 1][];
            while (++index < getNumberOfEquations()) {
                secondaryState[index - 1] = extractEquationData(index, y);
                secondaryDerivative[index - 1] = extractEquationData(index, yDot);
            }
            return new ODEStateAndDerivative(t, state, derivative, secondaryState, secondaryDerivative);
        }
    }

    /**
     * Extract equation data from a complete state or derivative array.
     *
     * @param index    index of the equation, must be between 0 included and
     *                 {@link #getNumberOfEquations()} (excluded)
     * @param complete complete state or derivative array from which
     *                 equation data should be retrieved
     * @return equation data
     * @throws MathIllegalArgumentException if index is out of range
     * @throws MathIllegalArgumentException if complete state has not enough elements
     */
    public double[] extractEquationData(final int index, final double[] complete)
            throws MathIllegalArgumentException {
        checkIndex(index);
        final int begin = start[index];
        final int end = start[index + 1];
        if (complete.length < end) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                    complete.length, end);
        }
        final int dimension = end - begin;
        final double[] equationData = new double[dimension];
        System.arraycopy(complete, begin, equationData, 0, dimension);
        return equationData;
    }

    /**
     * Insert equation data into a complete state or derivative array.
     *
     * @param index        index of the equation, must be between 0 included and
     *                     {@link #getNumberOfEquations()} (excluded)
     * @param equationData equation data to be inserted into the complete array
     * @param complete     placeholder where to put equation data (only the
     *                     part corresponding to the equation will be overwritten)
     * @throws MathIllegalArgumentException if either array has not enough elements
     */
    public void insertEquationData(final int index, double[] equationData, double[] complete)
            throws MathIllegalArgumentException {
        checkIndex(index);
        final int begin = start[index];
        final int end = start[index + 1];
        final int dimension = end - begin;
        if (complete.length < end) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                    complete.length, end);
        }
        if (equationData.length != dimension) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                    equationData.length, dimension);
        }
        System.arraycopy(equationData, 0, complete, begin, dimension);
    }

    /**
     * Check equation index.
     *
     * @param index index of the equation, must be between 0 included and
     *              {@link #getNumberOfEquations()} (excluded)
     * @throws MathIllegalArgumentException if index is out of range
     */
    private void checkIndex(final int index) throws MathIllegalArgumentException {
        if (index < 0 || index > start.length - 2) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.OUT_OF_RANGE_SIMPLE,
                    index, 0, start.length - 2);
        }
    }

}
