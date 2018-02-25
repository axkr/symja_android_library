/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math.fitting;

import de.lab4inf.math.Function;

import static java.lang.System.arraycopy;

/**
 * Generic fit to an arbitrary function using approximate derivatives.
 *
 * @author nwulff
 * @version $Id: FunctionFitter.java,v 1.6 2010/02/25 15:31:54 nwulff Exp $
 * @since 20.05.2009
 */
public class FunctionFitter extends GenericFitter {
    private static final String MSG = "Function fitter has to use approximated derivatives";
    private Function fct;
    private double[] fctArgs;

    /**
     * Constructor for the given function and initial parameters.
     *
     * @param f      function to fit
     * @param params initial parameters for the function
     */
    public FunctionFitter(final Function f, final double... params) {
        super(params.length);
        setApproximate(true);
        setEps(0.001);
        // pop();
        setParameters(params);
        setUsePearson(true);
        fctArgs = new double[params.length + 1];
        fct = f;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#dFct(int, double)
     */
    @Override
    protected double dFct(final int k, final double x) {
        throw new IllegalStateException(MSG);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#ddFct(int, int, double)
     */
    @Override
    protected double ddFct(final int k, final int l, final double x) {
        throw new IllegalStateException(MSG);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#fct(double)
     */
    @Override
    public double fct(final double x) {
        fctArgs[0] = x;
        arraycopy(a, 0, fctArgs, 1, a.length);
        return fct.f(fctArgs);
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#initParameters(double[], double[])
     */
    @Override
    protected void initParameters(final double[] x, final double[] y) {
        // not used by this fit...
    }

}
 