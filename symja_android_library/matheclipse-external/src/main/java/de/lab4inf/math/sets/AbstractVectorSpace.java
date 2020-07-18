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

import de.lab4inf.math.Field;
import de.lab4inf.math.InnerProductSpace;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.NormedVectorSpace;
import de.lab4inf.math.Operand;
import de.lab4inf.math.VectorSpace;

/**
 * Base class for an inner product space over the set of type T.
 *
 * @param <T> type of the vector elements
 * @author nwulff
 * @version $Id: AbstractVectorSpace.java,v 1.3 2012/01/09 15:22:45 nwulff Exp $
 * @since 19.06.2011
 */

public abstract class AbstractVectorSpace<T extends Field<T>> extends L4MObject
        implements InnerProductSpace<T> {
    protected int n;
    protected T[] elements;

    protected abstract AbstractVectorSpace<T> createVector(final int dim);

    /**
     * Internal check if the dimension matches between this and that
     *
     * @param that the other vector to match
     */
    protected final void checkDimension(final VectorSpace<T> that) {
        int m = that.getElements().length;
        if (n != m) {
            throw new IllegalArgumentException("dimension missmatch");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Group#isZero()
     */
    @Override
    public final boolean isZero() {
        for (int i = 0; i < n; i++) {
            if (!elements[i].isZero())
                return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.VectorSpace#getElements()
     */
    @Override
    public final T[] getElements() {
        return elements;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.VectorSpace#multiply(de.lab4inf.math.Field)
     */
    @Override
    @Operand(symbol = "*")
    public final VectorSpace<T> multiply(final T scalar) {
        AbstractVectorSpace<T> anOther = createVector(n);
        T[] otherElements = anOther.getElements();
        for (int i = 0; i < n; otherElements[i] = elements[i].multiply(scalar), i++)
            ;
        return anOther;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Group#plus(java.lang.Object)
    */
    @Override
    @Operand(symbol = "+")
    public final VectorSpace<T> plus(final VectorSpace<T> that) {
        checkDimension(that);
        AbstractVectorSpace<T> anOther = createVector(n);
        T[] thatElements = that.getElements();
        T[] otherElements = anOther.getElements();
        for (int i = 0; i < n; i++) {
            otherElements[i] = thatElements[i].plus(elements[i]);
        }
        return anOther;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Field#minus(java.lang.Object)
     */
    @Operand(symbol = "-")
    public final AbstractVectorSpace<T> minus(final VectorSpace<T> that) {
        checkDimension(that);
        AbstractVectorSpace<T> anOther = createVector(n);
        T[] thatElements = that.getElements();
        T[] otherElements = anOther.getElements();
        for (int i = 0; i < n; i++) {
            otherElements[i] = elements[i].minus(thatElements[i]);
        }
        return anOther;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.NormedVectorSpace#distance(de.lab4inf.math.NormedVectorSpace)
     */
    @Override
    public T distance(final NormedVectorSpace<T> that) {
        AbstractVectorSpace<T> diff = minus(that);
        return diff.norm();
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.InnerProductSpace#angle(de.lab4inf.math.InnerProductSpace)
     */
    @Override
    public double angle(final InnerProductSpace<T> that) {
        double phi = 0;
        if (!(this.isZero() || that.isZero())) {
            T prod = this.product(that);
            T norm = this.norm().multiply(that.norm());
            phi = acos(prod.div(norm));
        }
        return phi;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.InnerProductSpace#isOrthogonal(de.lab4inf.math.InnerProductSpace)
     */
    @Override
    public boolean isOrthogonal(final InnerProductSpace<T> that) {
        boolean ortho = false;
        if (!(this.isZero() || that.isZero())) {
            T prod = this.product(that);
            ortho = prod.isZero();
        }
        return ortho;
    }

    /**
     * Calculate the value arccos(T) for some element of type T.
     *
     * @param x element of type T
     * @return arccos(x)
     */
    protected abstract double acos(final T x);
}
 