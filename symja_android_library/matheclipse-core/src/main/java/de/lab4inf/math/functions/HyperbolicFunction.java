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

import de.lab4inf.math.CFunction;
import de.lab4inf.math.Complex;
import de.lab4inf.math.Differentiable;
import de.lab4inf.math.Integrable;

/**
 * Abstract base for for hyperbolic functions like sinh, cosh, etc.
 *
 * @author nwulff
 * @since 14.11.2010
 */
abstract class HyperbolicFunction extends L4MFunction implements Differentiable, Integrable, CFunction {
    protected volatile CFunction primitive;
    protected volatile CFunction derivative;

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiable#getDerivative()
     */
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
    public final Complex f(final Complex... z) {
        return f(z[0]);
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

}
 