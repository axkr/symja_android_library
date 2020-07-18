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

import de.lab4inf.math.InnerProductSpace;
import de.lab4inf.math.L4MLoader;
import de.lab4inf.math.Operand;
import de.lab4inf.math.Real;

/**
 * Inner product space over the set of reals.
 *
 * @author nwulff
 * @version $Id: RealVector.java,v 1.3 2013/02/26 16:57:44 nwulff Exp $
 * @since 19.06.2011
 */

public class RealVector extends AbstractVectorSpace<Real> {
    /**
     * Real prototype injection.
     */
    private static final Real REAL = L4MLoader.load(Real.class);

    /**
     * Construct a real vector with given dimension.
     *
     * @param n dimension to use
     */
    private RealVector(final int n) {
        this.n = n;
        elements = new Real[n];
    }

    /**
     * Construct a real vector with given values.
     *
     * @param x ordered tuple with the values to assign
     */
    public RealVector(final double... x) {
        this(x.length);
        for (int i = 0; i < n; elements[i] = newReal(x[i]), i++) ;
    }

    /**
     * Construct a real vector with given values.
     *
     * @param x ordered tuple with the values to assign
     */
    public RealVector(final Real... x) {
        this(x.length);
        for (int i = 0; i < n; elements[i] = x[i], i++) ;
    }

    /**
     * Copy constructor
     *
     * @param rv real vector to copy
     */
    public RealVector(final RealVector rv) {
        this(rv.n);
        for (int i = 0; i < n; elements[i] = rv.elements[i], i++) ;
    }

    /**
     * Factory method to create Real from double.
     *
     * @param value as seed
     * @return Real
     */
    private static Real newReal(final double value) {
        return REAL.newReal(value);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.NormedVectorSpace#norm()
     */
    @Override
    public RealNumber norm() {
        double e, norm = 0;
        if (!isZero()) {
            for (int i = 0; i < n; e = elements[i].getValue(), norm += e * e, i++) ;
            norm = Math.sqrt(norm);
        }
        return RealNumber.asReal(norm);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.InnerProductSpace#product(de.lab4inf.math.InnerProductSpace)
     */
    @Override
    @Operand(symbol = "*")
    public Real product(final InnerProductSpace<Real> that) {
        checkDimension(that);
        double prod = 0;
        Real[] otherElements = that.getElements();
        for (int i = 0; i < n; prod += elements[i].multiply(otherElements[i]).getValue(), i++) ;
        return newReal(prod);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.sets.AbstractVectorSpace#createVector(int)
     */
    @Override
    protected AbstractVectorSpace<Real> createVector(final int dim) {
        return new RealVector(n);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.sets.AbstractVectorSpace#acos(de.lab4inf.math.Field)
     */
    @Override
    protected double acos(final Real x) {
        return Math.acos(x.getValue());
    }
}
 