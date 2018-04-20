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
 * Discrete random sequence that follows a
 * <a href="http://en.wikipedia.org/wiki/Poisson_distribution" target="_top">Poisson
 * distribution</a>.
 * @author Daniel Dyer
 */
public class PoissonGenerator implements NumberGenerator<Integer>
{
    private final Random rng;
    private final NumberGenerator<Double> mean;


    /**
     * <p>Creates a generator of Poisson-distributed values.  The mean is
     * determined by the provided {@link org.uncommons.maths.number.NumberGenerator}.  This means that
     * the statistical parameters of this generator may change over time.
     * One example of where this is useful is if the mean generator is attached
     * to a GUI control that allows a user to tweak the parameters while a
     * program is running.</p>
     * <p>To create a Poisson generator with a constant mean, use the
     * {@link #PoissonGenerator(double, Random)} constructor instead.</p>
     * @param mean A {@link NumberGenerator} that provides the mean of the
     * Poisson distribution used for the next generated value.
     * @param rng The source of randomness.
     */
    public PoissonGenerator(NumberGenerator<Double> mean,
                            Random rng)
    {
        this.mean = mean;
        this.rng = rng;
    }


    /**
     * Creates a generator of Poisson-distributed values from a distribution
     * with the specified mean.
     * @param mean The mean of the values generated.
     * @param rng The source of randomness.
     */
    public PoissonGenerator(double mean,
                            Random rng)
    {
        this(new ConstantGenerator<Double>(mean), rng);
        if (mean <= 0)
        {
            throw new IllegalArgumentException("Mean must be a positive value.");
        }
    }


    /**
     * {@inheritDoc}
     */
    public Integer nextValue()
    {
        int x = 0;
        double t = 0.0;
        while (true)
        {
            t -= Math.log(rng.nextDouble()) / mean.nextValue();
            if (t > 1.0)
            {
                break;
            }
            ++x;
        }
        return x;
    }
}
