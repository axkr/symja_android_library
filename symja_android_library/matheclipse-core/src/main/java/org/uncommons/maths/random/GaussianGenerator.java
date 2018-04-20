// ============================================================================
//   Copyright 2006-2012 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.maths.random;

import java.util.Random;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;

/**
 * <a href="http://en.wikipedia.org/wiki/Normal_distribution" target="_top">Normally distributed</a>
 * random sequence.
 * @author Daniel Dyer
 */
public class GaussianGenerator implements NumberGenerator<Double>
{
    private final Random rng;
    private final NumberGenerator<Double> mean;
    private final NumberGenerator<Double> standardDeviation;


    /**
     * <p>Creates a generator of normally-distributed values.  The mean and
     * standard deviation are determined by the provided
     * {@link NumberGenerator}s.  This means that the statistical parameters
     * of this generator may change over time.  One example of where this
     * is useful is if the mean and standard deviation generators are attached
     * to GUI controls that allow a user to tweak the parameters while a
     * program is running.</p>
     * <p>To create a Gaussian generator with a constant mean and standard
     * deviation, use the {@link #GaussianGenerator(double, double, Random)}
     * constructor instead.</p>
     * @param mean A {@link NumberGenerator} that provides the mean of the
     * Gaussian distribution used for the next generated value.
     * @param standardDeviation A {@link NumberGenerator} that provides the
     * standard deviation of the Gaussian distribution used for the next
     * generated value.
     * @param rng The source of randomness. 
     */
    public GaussianGenerator(NumberGenerator<Double> mean,
                             NumberGenerator<Double> standardDeviation,
                             Random rng)
    {
        this.mean = mean;
        this.standardDeviation = standardDeviation;
        this.rng = rng;
    }


    /**
     * Creates a generator of normally-distributed values from a distribution
     * with the specified mean and standard deviation.
     * @param mean The mean of the values generated.
     * @param standardDeviation The standard deviation of the values generated.
     * @param rng The source of randomness.
     */
    public GaussianGenerator(double mean,
                             double standardDeviation,
                             Random rng)
    {
        this(new ConstantGenerator<Double>(mean),
             new ConstantGenerator<Double>(standardDeviation),
             rng);
    }


    /**
     * {@inheritDoc}
     */
    public Double nextValue()
    {
        return rng.nextGaussian() * standardDeviation.nextValue() + mean.nextValue();
    }
}
