/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2014,  Prof. Dr. Nikolaus Wulff
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

/**
 * Fit the (x,y) tuples according to a circle equation.
 * <pre>
 *
 *     R**2 = (x - Xcenter)**2 + (y-Ycenter)**2
 *
 * </pre>
 *
 * @author nwulff
 * @version $Id: CircleFitter.java,v 1.8 2014/12/08 15:21:05 nwulff Exp $
 * @since 30.11.2014
 */
public abstract class CircleFitter extends GenericFitter {

    /**
     * Protected constructor with number of fit parameters used.
     *
     * @param n number of parameters to fit.
     */
    protected CircleFitter(final int n) {
        super(n);
        setNewton(false);
        setApproximate(false);
        setShouldThrowSingular(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.GenericFitter#fct(double)
     */
    @Override
    public double fct(final double x) {
        final double R2 = getRadiusSquared();
        final double Xc = getXCenter();
        final double Yc = getYCenter();
        double y = R2 - Math.pow(x - Xc, 2);
        if (y < 0)
            throw new IllegalArgumentException("y is less zero" + y);

        y = Math.sqrt(y) + Yc;
        return y;
    }

    /**
     * get the fitted circle radius.
     *
     * @return R
     */
    public double getRadius() {
        return Math.sqrt(getRadiusSquared());
    }

    /**
     * get the fitted circle radius squared.
     *
     * @return R2
     */
    public abstract double getRadiusSquared();

    /**
     * get the fitted circle x center.
     *
     * @return Xc
     */
    public abstract double getXCenter();

    /**
     * get the fitted circle y center.
     *
     * @return Yc
     */
    public abstract double getYCenter();

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.GenericFitter#dFct(int, double)
     */
    @Override
    protected double dFct(final int k, final double x) {
        // should not be used by this fit method
        throw new IllegalStateException("not to be used for circle fit");
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.fitting.GenericFitter#ddFct(int, int, double)
     */
    @Override
    protected double ddFct(final int k, final int l, final double x) {
        // should not be used by this fit method
        throw new IllegalStateException("not to be used for circle fit");
    }
}
 