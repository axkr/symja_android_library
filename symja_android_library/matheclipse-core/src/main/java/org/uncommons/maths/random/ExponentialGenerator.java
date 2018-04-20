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
 * Continuous random sequence that follows an
 * <a href="http://en.wikipedia.org/wiki/Exponential_distribution" target="_top">exponential
 * distribution</a>.
 * @author Daniel Dyer
 * @since 1.0.2
 */
public class ExponentialGenerator implements NumberGenerator<Double>
{
    private final NumberGenerator<Double> rate;
    private final Random rng;


    /**
     * Creates a generator of exponentially-distributed values from a distribution
     * with a rate controlled by the specified generator parameter.  The mean of
     * this distribution is {@literal 1 / rate}
     * and the variance is {@literal 1 / rate^2}.
     * @param rate A number generator that provides values to use as the rate for
     * the exponential distribution.  This generator must only return non-zero, positive
     * values.
     * @param rng The source of randomness used to generate the exponential values.
     */
    public ExponentialGenerator(NumberGenerator<Double> rate,
                                Random rng)
    {
        this.rate = rate;
        this.rng = rng;
    }


    /**
     * Creates a generator of exponentially-distributed values from a distribution
     * with the specified rate.  The mean of this distribution is {@literal 1 / rate}
     * and the variance is {@literal 1 / rate^2}.
     * @param rate The rate (lamda) of the exponential distribution.
     * @param rng The source of randomness used to generate the exponential values.
     */
    public ExponentialGenerator(double rate,
                                Random rng)
    {
        this(new ConstantGenerator<Double>(rate), rng);
    }


    /**
     * Generate the next exponential value from the current value of
     * {@literal rate}.
     * @return The next exponentially-distributed value.
     */
    public Double nextValue()
    {
        double u;
        do
        {
            // Get a uniformly-distributed random double between
            // zero (inclusive) and 1 (exclusive)
            u = rng.nextDouble();
        } while (u == 0d); // Reject zero, u must be positive for this to work.
        return (-Math.log(u)) / rate.nextValue();
    }
}
