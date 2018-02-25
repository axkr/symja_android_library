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
package de.lab4inf.math.functions;

import de.lab4inf.math.CDifferentiable;
import de.lab4inf.math.CFunction;
import de.lab4inf.math.CIntegrable;
import de.lab4inf.math.Complex;
import de.lab4inf.math.IFunction;
import de.lab4inf.math.Interval;

/**
 * Abstract base for classes like sine or cosine functions.
 *
 * @author nwulff
 * @since 03.02.2005
 */
abstract class TrigonometricFunction extends L4MFunction implements CDifferentiable, CIntegrable, IFunction {
    protected final boolean negative;
    protected volatile CFunction primitive;
    protected volatile CFunction derivative;

    /**
     * Constructor used for derivatives of sine and cosine.
     *
     * @param negative sign flag
     */
    protected TrigonometricFunction(final boolean negative) {
        this.negative = negative;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.CDifferentiable#getCDerivative()
     */
    @Override
    public CFunction getCDerivative() {
        return getDerivative();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.CIntegrable#getCAntiderivative()
     */
    @Override
    public CFunction getCAntiderivative() {
        return getAntiderivative();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiable#getDerivative()
     */
    @Override
    public final CFunction getDerivative() {
        if (derivative == null) {
            derivative = createDerivative();
        }
        return derivative;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Integrable#getAntiderivative()
     */
    @Override
    public final CFunction getAntiderivative() {
        if (primitive == null) {
            primitive = createAntiDerivative();
        }
        return primitive;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
    */
    @Override
    public final double f(final double... x) {
        return f(x[0]);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.sets.CFunction#f(de.lab4inf.math.sets.Complex[])
     */
    @Override
    public final Complex f(final Complex... z) {
        return f(z[0]);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.IFunction#f(de.lab4inf.math.Interval[])
     */
    @Override
    public Interval f(final Interval... v) {
        return f(v[0]);
    }

    /**
     * calculate f(x) for a real input argument.
     *
     * @param x the real argument
     * @return value f(x)
     */
    public abstract double f(final double x);

    /**
     * Factory method to create the derivative for this function.
     *
     * @return the derivative.
     */
    protected abstract CFunction createDerivative();

    /**
     * Factory method to create the primitive (up to a constant)
     * for this function.
     *
     * @return the primitive
     */
    protected abstract CFunction createAntiDerivative();

    /**
     * calculate f(z) for a complex input argument.
     *
     * @param z the complex argument
     * @return value f(z)
     */
    public abstract Complex f(final Complex z);

    /**
     * calculate f(z) for an interval input argument.
     *
     * @param v the inverval argument
     * @return interval f(v)
     */
    public abstract Interval f(final Interval v);
}
 