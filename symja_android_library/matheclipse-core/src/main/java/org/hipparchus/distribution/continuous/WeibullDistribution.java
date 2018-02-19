/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hipparchus.distribution.continuous;

import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.special.Gamma;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;

/**
 * Implementation of the Weibull distribution. This implementation uses the
 * two parameter form of the distribution defined by
 * <a href="http://mathworld.wolfram.com/WeibullDistribution.html">
 * Weibull Distribution</a>, equations (1) and (2).
 *
 * @see <a href="http://en.wikipedia.org/wiki/Weibull_distribution">Weibull distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/WeibullDistribution.html">Weibull distribution (MathWorld)</a>
 */
public class WeibullDistribution extends AbstractRealDistribution {
    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 20160320L;
    /**
     * The shape parameter.
     */
    private final double shape;
    /**
     * The scale parameter.
     */
    private final double scale;

    /**
     * Create a Weibull distribution with the given shape and scale.
     *
     * @param alpha Shape parameter.
     * @param beta  Scale parameter.
     * @throws MathIllegalArgumentException if {@code alpha <= 0} or {@code beta <= 0}.
     */
    public WeibullDistribution(double alpha, double beta)
            throws MathIllegalArgumentException {
        if (alpha <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.SHAPE,
                    alpha);
        }
        if (beta <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.SCALE,
                    beta);
        }
        scale = beta;
        shape = alpha;
    }

    /**
     * Access the shape parameter, {@code alpha}.
     *
     * @return the shape parameter, {@code alpha}.
     */
    public double getShape() {
        return shape;
    }

    /**
     * Access the scale parameter, {@code beta}.
     *
     * @return the scale parameter, {@code beta}.
     */
    public double getScale() {
        return scale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double density(double x) {
        if (x < 0) {
            return 0;
        }

        final double xscale = x / scale;
        final double xscalepow = FastMath.pow(xscale, shape - 1);

        /*
         * FastMath.pow(x / scale, shape) =
         * FastMath.pow(xscale, shape) =
         * FastMath.pow(xscale, shape - 1) * xscale
         */
        final double xscalepowshape = xscalepow * xscale;

        return (shape / scale) * xscalepow * FastMath.exp(-xscalepowshape);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double logDensity(double x) {
        if (x < 0) {
            return Double.NEGATIVE_INFINITY;
        }

        final double xscale = x / scale;
        final double logxscalepow = FastMath.log(xscale) * (shape - 1);

        /*
         * FastMath.pow(x / scale, shape) =
         * FastMath.pow(xscale, shape) =
         * FastMath.pow(xscale, shape - 1) * xscale
         */
        final double xscalepowshape = FastMath.exp(logxscalepow) * xscale;

        return FastMath.log(shape / scale) + logxscalepow - xscalepowshape;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(double x) {
        double ret;
        if (x <= 0.0) {
            ret = 0.0;
        } else {
            ret = 1.0 - FastMath.exp(-FastMath.pow(x / scale, shape));
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns {@code 0} when {@code p == 0} and
     * {@code Double.POSITIVE_INFINITY} when {@code p == 1}.
     */
    @Override
    public double inverseCumulativeProbability(double p) {
        MathUtils.checkRangeInclusive(p, 0, 1);

        double ret;
        if (p == 0) {
            ret = 0.0;
        } else if (p == 1) {
            ret = Double.POSITIVE_INFINITY;
        } else {
            ret = scale * FastMath.pow(-FastMath.log1p(-p), 1.0 / shape);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The mean is {@code scale * Gamma(1 + (1 / shape))}, where {@code Gamma()}
     * is the Gamma-function.
     */
    @Override
    public double getNumericalMean() {
        final double sh = getShape();
        final double sc = getScale();

        return sc * FastMath.exp(Gamma.logGamma(1 + (1 / sh)));
    }

    /**
     * {@inheritDoc}
     * <p>
     * The variance is {@code scale^2 * Gamma(1 + (2 / shape)) - mean^2}
     * where {@code Gamma()} is the Gamma-function.
     */
    @Override
    public double getNumericalVariance() {
        final double sh = getShape();
        final double sc = getScale();
        final double mn = getNumericalMean();

        return (sc * sc) * FastMath.exp(Gamma.logGamma(1 + (2 / sh))) -
                (mn * mn);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The lower bound of the support is always 0 no matter the parameters.
     *
     * @return lower bound of the support (always 0)
     */
    @Override
    public double getSupportLowerBound() {
        return 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The upper bound of the support is always positive infinity
     * no matter the parameters.
     *
     * @return upper bound of the support (always
     * {@code Double.POSITIVE_INFINITY})
     */
    @Override
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The support of this distribution is connected.
     *
     * @return {@code true}
     */
    @Override
    public boolean isSupportConnected() {
        return true;
    }
}

