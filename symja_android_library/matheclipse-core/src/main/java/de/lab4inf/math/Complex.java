/*
 * Project: Lab4Math
 *
 * Copyright (c) 2006-2010,  Prof. Dr. Nikolaus Wulff
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
 * Interface of complex numbers as a basic data type.
 * Some of the methods like plus, multiply etc. are named
 * suitable for usage with SymjaMMA operator overloading.
 * <p>
 * You can resolve a complex implementation via the L4MLoader:
 * <pre>
 *
 *   Complex w = L4MLoader.load(Complex.class);
 *
 * </pre>
 *
 * @author nwulff
 * @version $Id: Complex.java,v 1.9 2013/06/22 13:53:31 nwulff Exp $
 * @since 14.11.2010
 */
@Service
public interface Complex extends Numeric<Complex> {
    /**
     * Factory method, mimic a constructor for a new complex number
     * without exposing the implementing class to a client.
     *
     * @param real part
     * @param imag part
     * @return new Complex(real, imag)
     */
    Complex newComplex(final double real, final double imag);

    /**
     * Test if the imaginary part is not zero.
     *
     * @return boolean indicating true if imaginary part not zero.
     */
    boolean isComplex();

    /**
     * Test if the imaginary part is zero, i.e. this is a real.
     *
     * @return boolean indicating true if imaginary part is zero.
     */
    boolean isReal();

    /**
     * Test if this complex is zero.
     *
     * @return boolean indicating true if zero.
     */
    boolean isZero();

    /**
     * Test if this complex is one.
     *
     * @return boolean indicating true if one.
     */
    boolean isOne();

    /**
     * The real part of this complex.
     *
     * @return re z
     */
    double real();

    /**
     * The imaginary part of this complex.
     *
     * @return im z
     */
    double imag();

    /**
     * The conjugate complex of this.
     *
     * @return x -jy
     */
    Complex conj();

    /**
     * The inverse of this complex.
     *
     * @return 1/this
     */
    Complex invers();

    /**
     * The phase in radian.
     *
     * @return phase
     */
    double rad();

    /**
     * The phase in degree.
     *
     * @return phase
     */
    double deg();

    /**
     * The absolute value of this.
     *
     * @return |z|
     */
    Complex abs();

    /**
     * The absolute value squared.
     *
     * @return |z|**2
     */
    double abs2();

    /**
     * Addition of this plus z.
     *
     * @param z argument
     * @return this+z
     */
    @Operand(symbol = "+")
    Complex plus(Complex z);

    /**
     * Subtraction of this minus z.
     *
     * @param z argument
     * @return this-z
     */
    @Operand(symbol = "-")
    Complex minus(Complex z);

    /**
     * Multiplication of this times z.
     *
     * @param z argument
     * @return this*z
     */
    @Operand(symbol = "*")
    Complex multiply(Complex z);

    /**
     * Complex conjugate multiplication times z.
     *
     * @param z argument
     * @return this*z.conj()
     */
    Complex cmultiply(Complex z);

    /**
     * Division of this by z.
     *
     * @param z argument
     * @return this/z
     */
    @Operand(symbol = "/")
    Complex div(Complex z);

    /**
     * Addition of this plus x.
     *
     * @param x real argument
     * @return this+z
     */
    @Operand(symbol = "+")
    Complex plus(double x);

    /**
     * Subtraction of this minus x.
     *
     * @param x real argument
     * @return this-x
     */
    @Operand(symbol = "-")
    Complex minus(double x);

    /**
     * Multiplication of this times x.
     *
     * @param x real  argument
     * @return this*x
     */
    @Operand(symbol = "*")
    Complex multiply(double x);

    /**
     * Division of this by x.
     *
     * @param x real argument
     * @return this/x
     */
    @Operand(symbol = "/")
    Complex div(double x);
}
 