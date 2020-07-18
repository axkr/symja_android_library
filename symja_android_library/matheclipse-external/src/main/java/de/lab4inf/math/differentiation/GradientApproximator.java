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
package de.lab4inf.math.differentiation;

import de.lab4inf.math.Function;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.gof.Visitor;

/**
 * Calculate the gradient of a function with default accuracy 1.E-8.
 * This implementation uses function currying to approximate the gradient
 * for the different arguments with the help of the Differentiator.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: GradientApproximator.java,v 1.7 2014/06/26 11:25:35 nwulff Exp $
 * @since 13.10.2007
 */

public class GradientApproximator extends L4MObject implements Gradient {
    /**
     * Default accuracy of 1E-8.
     */
    public static final double EPS_DEFAULT = 1.E-8;
    protected final Function function;
    private double eps = EPS_DEFAULT;

    /**
     * Constructor for the gradient of the given function.
     *
     * @param fct function to wrap
     */
    public GradientApproximator(final Function fct) {
        function = fct;
    }

    /**
     * The accuracy to use.
     *
     * @return precision of the gradient approximation.
     */
    public double getEps() {
        return eps;
    }

    /**
     * Set the accuracy of the gradient.
     *
     * @param eps precision of approximation
     */
    public void setEps(final double eps) {
        this.eps = eps;
    }

    /*
     * 	(non-Javadoc)
     * @see de.lab4inf.math.differentiation.Gradient#gradient(double[])
     */
    public double[] gradient(final double... x) {
        final int n = x.length;
        double[] g = new double[n];
        GradientFunction dG = new GradientFunction(x);
        for (dG.i = 0; dG.i < n; dG.i++) {
            g[dG.i] = Differentiator.dF(eps, dG, x[dG.i]);
        }
        return g;
    }

    /**
     * Internal function wrapper, used to calculate the
     * derivatives df/dx[j] with j=0,..., n-1.
     */
    private class GradientFunction implements Function {
        final double[] x;
        int i;

        public GradientFunction(final double[] x) {
            this.x = x;
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.Function#f(double)
         */
        public double f(final double... xi) {
            double yi, xo = x[i];
            x[i] = xi[0];
            yi = function.f(x);
            x[i] = xo;
            return yi;
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
         */
        @Override
        public void accept(final Visitor<Function> visitor) {
            visitor.visit(this);
        }
    }
}
 