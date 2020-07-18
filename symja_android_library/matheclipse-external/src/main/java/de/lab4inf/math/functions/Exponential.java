/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer sciences (Lab4Inf).
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
import de.lab4inf.math.Function;
import de.lab4inf.math.IDifferentiable;
import de.lab4inf.math.IFunction;
import de.lab4inf.math.IIntegrable;
import de.lab4inf.math.Interval;
import de.lab4inf.math.sets.ComplexNumber;
import de.lab4inf.math.sets.IntervalNumber;

/**
 * Wrapper for exponential function.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Exponential.java,v 1.15 2011/09/12 14:49:20 nwulff Exp $
 * @since 18.10.2005
 */
public class Exponential extends L4MFunction implements
        CDifferentiable, CIntegrable, IDifferentiable, IIntegrable {
    /**
     * Calculate the real exponential exp(x).
     *
     * @param x the real argument
     * @return exp(x)
     */
    public static double exp(final double x) {
        return Math.exp(x);
    }

    /**
     * Calculate the complex exponential exp(z).
     *
     * @param z the complex argument
     * @return exp(z)
     */
    public static ComplexNumber exp(final Complex z) {
        double r = exp(z.real());
        double t = z.imag();
        return new ComplexNumber(r * Math.cos(t), r * Math.sin(t));
    }

    /**
     * Calculate the exponential for an interval number.
     *
     * @param x the interval
     * @return exp(x) as interval number
     */
    public static Interval exp(final Interval x) {
        double l = exp(x.left());
        double r = exp(x.right());
        return new IntervalNumber(l, r);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.Differentiable#getDerivative()
     */
    @Override
    public Function getDerivative() {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.CDifferentiable#getCDerivative()
     */
    public CFunction getCDerivative() {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.IDifferentiable#getIDerivative()
     */
    public IFunction getIDerivative() {
        return this;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.Integrable#getAntiderivative()
     */
    @Override
    public Function getAntiderivative() {
        return this;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.IIntegrable#getIAntiderivative()
     */
    @Override
    public IFunction getIAntiderivative() {
        return this;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.CIntegrable#getCAntiderivative()
     */
    @Override
    public CFunction getCAntiderivative() {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.functions.TrigonometricFunction#f(double)
     */
    @Override
    public double f(final double... x) {
        return exp(x[0]);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
     */
    @Override
    public Complex f(final Complex... z) {
        return exp(z[0]);
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.IFunction#f(de.lab4inf.math.Interval[])
     */
    public Interval f(final Interval... v) {
        return exp(v[0]);
    }
}
 