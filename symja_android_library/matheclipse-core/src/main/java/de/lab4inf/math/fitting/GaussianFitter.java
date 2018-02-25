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
package de.lab4inf.math.fitting;

import java.util.ArrayList;

import static java.lang.Math.exp;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

/**
 * Fit the (x,y) data tuples with a gaussian distribution.
 * <br/>
 * <pre>
 *         y(x;a) := amplitude/(sigma*sqrt(2pi))*exp(-0.5*[(x-mean)/sigma]^2)
 *            a[] := {amplitude, sigma, mean}
 * </pre>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: GaussianFitter.java,v 1.26 2014/12/12 09:11:01 nwulff Exp $
 * @since 10.10.2006
 */
public class GaussianFitter extends GenericFitter {
    protected static final double SQRT2PI = sqrt(2 * Math.PI);

    /**
     * Default bean constructor.
     */
    public GaussianFitter() {
        super(3);
        setApproximate(false);
        setUsePearson(false);
        setNewton(false);
        // setDebug(true);
        setEps(0.00005);
    }

    /**
     * Constructor for derived classes.
     *
     * @param n dimension of parameter array
     */
    protected GaussianFitter(final int n) {
        super(n);
    }
  
      /*
       * (non-Javadoc)
       * 
       * @see de.lab4inf.fitting.GenericFitter#initParameters(double[], double[])
       */
    // @Override
    // protected void newinitParameters(final double[] x, final double[] y) {
    // final DataCollector1D sampler = new DataCollector1D();
    // final int n = x.length;
    // for (int i = 0; i<n; i++) {
    // if (y[i]>0)
    // sampler.collect(x[i], y[i]);
    // }
    // // our best guess if data is gaussian
    // a[2] = sampler.getMean();
    // a[1] = sampler.getSigma();
    // a[0] = sampler.getSumWeight()/(SQRT2PI*a[1]);
    // }

    @Override
    protected void initParameters(final double[] x, final double[] y) {
        final int n = x.length;
        final PolyFitter poly = new PolyFitter(2);
        final ArrayList<Double> lny = new ArrayList<Double>(n);
        final ArrayList<Double> lnx = new ArrayList<Double>(n);
        for (int j = 0; j < n; j++) {
            if (0 < y[j]) {
                lnx.add(x[j]);
                lny.add(-2 * Math.log(y[j]));
            }
        }
        final int m = lnx.size();
        final double[] lx = new double[m];
        final double[] ly = new double[m];
        for (int j = 0; j < m; j++) {
            lx[j] = lnx.get(j);
            ly[j] = lny.get(j);
        }
        poly.fitt(lx, ly);
        // our best guess if data is gaussian
        double a2, a1, a0, amp, sig, mu;
        a2 = poly.getParameters()[2];
        a1 = poly.getParameters()[1];
        a0 = poly.getParameters()[0];
        sig = 1 / Math.sqrt(a2);
        mu = -a1 / (2 * a2);
        amp = Math.exp(0.5 * (mu * mu / (sig * sig) - a0));
        // amp = SQRT2PI*sig*Math.exp(0.5*(mu*mu/(sig*sig)-a0));
        a[2] = mu;
        a[1] = sig;
        a[0] = amp;
    }

    // @Override
    // protected void fittParameters(final double[] x, final double[] y, final double[] dy) {
    // }

    // @Override
    // protected void fittParameters(final double[] x, final double[] y) {
    // }

    // protected void oldinitParameters(final double[] x, final double[] y) {
    // double sumXY = 0, sumX2Y = 0, sumY = 0;
    // final int n = x.length;
    // for (int i = 0; i<n; i++) {
    // sumXY += x[i]*y[i];
    // sumX2Y += x[i]*x[i]*y[i];
    // sumY += y[i];
    // }
    // sumXY /= sumY;
    // sumX2Y /= sumY;
    // // our best guess if data is gaussian
    // a[2] = sumXY;
    // a[1] = sqrt(sumX2Y-sumXY*sumXY);
    // a[0] = (10*a[1]*sumY/n);
    // }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.fitting.GenericFitter#fct(double)
     */
    @Override
    public double fct(final double x) {
        final double z = (x - a[2]) / a[1];
        // final double g = a[0]/(SQRT2PI*a[1])*exp(-0.5*z*z);
        final double g = a[0] * exp(-0.5 * z * z);
        // double g = a[0]/(SQRT2PI*a[1])*exp(-0.5*z*z);
        // introduce a penalty term if a[0]<= 0 or a[1] <=0
        double p = exp(-getPenaltyValue() * (a[0] + a[1]));
        if (isUsePenalty() && p > 1 && getLogger().isWarnEnabled()) {
            final String msg = format("a=%.2e s=%.2f g=%.2f penalty:%.2g", a[0], a[1], g, p);
            getLogger().warn(format(msg));
        } else {
            p = 0;
        }
        return g * (1 + p);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.fitting.GenericFitter#dFct(int, double)
     */
    @Override
    protected double dFct(final int k, final double x) {
        final double dx = (x - a[2]);
        final double y = fct(x);
        final double s = a[1];
        final double z = dx / s;
        double dF = 0;
        switch (k) {
            case 2:
                dF = y * z / s;
                break;
            case 1:
                dF = y * z * z / s;
                break;
            case 0:
                dF = y / a[0];
                break;
            default:
                throw new IllegalArgumentException("k:" + k);
        }
        return dF;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.fitting.GenericFitter#ddFct(int, int, double)
     */
    @Override
    protected double ddFct(final int k, final int l, final double x) {
        final double my = a[2], sig = a[1], sig2 = sig * sig, dx = (x - my);
        final double z = dx / sig, z2 = z * z, y = fct(x);
        double ddF = 0;
        if (k == 2) {
            if (l == 2) {
                ddF = y / sig2 * (z2 - 1);
            } else if (l == 1) {
                ddF = y / sig2 * z * (z2 - 2);
            } else {
                ddF = dFct(2, x) / a[0];
            }
        } else if (k == 1) {
            if (l == 2) {
                ddF = ddFct(l, k, x);
            } else if (l == 1) {
                ddF = y * z2 * (z2 - 3) / sig2;
            } else {
                ddF = dFct(1, x) / a[0];
            }
        } else {
            if (l != 0) {
                ddF = dFct(l, x) / a[0];
            } else {
                ddF = 0;
            }
        }
        return ddF;
    }

    // protected double ddFctold(final int k, final int l, final double x) {
    // final double my = a[2], sig = a[1], amp = a[0], c2 = sig*sig, dx = (x-my);
    // final double z = dx/sig, z2 = z*z, y = fct(x);
    // double ddF = 0;
    // if (k==2) {
    // if (l==2) {
    // ddF = y*(z2-1)/c2;
    // } else if (l==1) {
    // ddF = y*z*(z2-3)/c2;
    // } else {
    // ddF = dFct(2, x)/amp;
    // }
    // } else if (k==1) {
    // if (l==2) {
    // ddF = y*z*(z2-3)/c2;
    // } else if (l==1) {
    // ddF = y*(2-5*z2+z2*z2)/c2;
    // } else {
    // ddF = dFct(1, x)/amp;
    // }
    // } else {
    // if (l==2) {
    // ddF = dFct(2, x)/amp;
    // } else if (l==1) {
    // ddF = dFct(1, x)/amp;
    // } else {
    // ddF = 0;
    // }
    // }
    // return ddF;
    // }
    //
    // protected double dFctOld(final int k, final double x) {
    // final double dx = (x-a[2]);
    // final double y = fct(x);
    // final double c = a[1];
    // final double z = dx/c;
    // double dF = 0;
    // switch (k) {
    // case 2:
    // dF = z*y/c;
    // break;
    // case 1:
    // dF = y/c*(z*z-1);
    // break;
    // case 0:
    // dF = y/a[0];
    // break;
    // default:
    // throw new IllegalArgumentException("k:"+k);
    // }
    // return dF;
    // }

    /**
     * Get the calculated mean value.
     *
     * @return the mean
     */
    public double getMean() {
        return a[2];
    }

    /**
     * Get the calculated deviation of the distribution.
     *
     * @return the deviation
     */
    public double getSigma() {
        return a[1];
    }

    /**
     * Get the calculated amplitude of the distribution.
     *
     * @return the amplitude
     */
    public double getAmplitude() {
        return a[0] * SQRT2PI * getSigma();
    }
}
 