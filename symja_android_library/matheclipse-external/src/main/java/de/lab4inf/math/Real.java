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
package de.lab4inf.math;

/**
 * Real numbers,i.e. a Quad Double (Decimal128),  modeled as a field.
 * <p>
 * You can resolve a real implementation via the L4MLoader:
 * <pre>
 *
 *   Real w = L4MLoader.load(Real.class);
 *
 * </pre>
 *
 * @author nwulff
 * @version $Id: Real.java,v 1.3 2014/02/10 10:55:11 nwulff Exp $
 * @since 26.02.2013
 */
@Service
public interface Real extends Numeric<Real>, Cloneable {
    /**
     * Factory method to construct a Real from value.
     *
     * @param value as seed
     * @return Real representing the value
     */
    Real newReal(Double value);

    /**
     * Factory method to construct a Real from value.
     *
     * @param value as seed
     * @return Real representing the value
     */
    Real newReal(double value);

    /**
     * Addition of this and x.
     *
     * @param x scalar to shift with
     * @return this + x
     */
    @Operand(symbol = "+")
    Real plus(double x);

    /**
     * Subtraction of this and x.
     *
     * @param x scalar to shift with
     * @return this + x
     */
    @Operand(symbol = "-")
    Real minus(double x);

    /**
     * Multiply this by x.
     *
     * @param x scalar to scale with
     * @return this * x
     */
    @Operand(symbol = "*")
    Real multiply(double x);

    /**
     * Divide this by x.
     *
     * @param x scalar to divide with
     * @return this / x
     */
    @Operand(symbol = "/")
    Real div(double x);

    /**
     * Representation as a double
     *
     * @return double value
     */
    double getValue();

    /**
     * Get the absolute value of of this real.
     *
     * @return the abs(this)
     */
    Real abs();

    /**
     * Get the absolute value of of this real.
     *
     * @return the abs(this)
     */
    double absValue();

    /**
     * Compare this to a double.
     *
     * @param x double to compare to
     * @return -1 if less 1 if greater than x else 0
     */
    int compareTo(Double x);
}
 