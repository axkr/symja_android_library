/*
 * Project: Lab4Math
 *
 * Copyright (c) 2006-2011,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math.sets;

import de.lab4inf.math.Complex;
import de.lab4inf.math.InnerProductSpace;
import de.lab4inf.math.Operand;

/**
 * Implementation of an inner product space for an ordered tuple of complex numbers.
 *
 * @author nwulff
 * @version $Id: ComplexVector.java,v 1.2 2012/01/09 15:22:45 nwulff Exp $
 * @since 19.06.2011
 */

public class ComplexVector extends AbstractVectorSpace<Complex> {
    /**
     * Construct a real vector with given dimension.
     *
     * @param n dimension to use
     */
    private ComplexVector(final int n) {
        this.n = n;
        elements = new Complex[n];
    }

    /**
     * Construct a complex vector with given values.
     *
     * @param x ordered tuple with the values to assign
     */
    public ComplexVector(final Complex... x) {
        this(x.length);
        for (int i = 0; i < n; elements[i] = new ComplexNumber(x[i]), i++) ;
    }

    /**
     * Copy constructor
     *
     * @param rv real vector to copy
     */
    public ComplexVector(final ComplexVector rv) {
        this(rv.n);
        for (int i = 0; i < n; elements[i] = rv.elements[i], i++) ;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.NormedVectorSpace#norm()
     */
    @Override
    public Complex norm() {
        double prod = 0;
        if (!isZero()) {
            for (int i = 0; i < n; prod += elements[i].abs2(), i++) ;
            prod = Math.sqrt(prod);
        }
        return new ComplexNumber(prod);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.InnerProductSpace#product(de.lab4inf.math.InnerProductSpace)
     */
    @Override
    @Operand(symbol = "*")
    public Complex product(final InnerProductSpace<Complex> that) {
        checkDimension(that);
        Complex prod = ComplexNumber.ZERO;
        Complex[] otherElements = that.getElements();
        for (int i = 0; i < n; i++) {
            prod = prod.plus(elements[i].cmultiply(otherElements[i]));
        }
        return prod;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.sets.AbstractVectorSpace#createVector(int)
     */
    @Override
    protected AbstractVectorSpace<Complex> createVector(final int dim) {
        return new ComplexVector(dim);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.sets.AbstractVectorSpace#acos(de.lab4inf.math.Field)
     */
    @Override
    protected double acos(final Complex x) {
        return Math.acos(x.real());
    }
}
 