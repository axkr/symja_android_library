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
import org.uncommons.maths.number.NumberGenerator;

/**
 * Discrete, uniformly distributed random sequence.  Generates
 * values between the specified minimum and maximum values (inclusive).
 * @author Daniel Dyer
 */
public class DiscreteUniformGenerator implements NumberGenerator<Integer>
{
    private final Random rng;
    private final int range;
    private final int minimumValue;

    public DiscreteUniformGenerator(int minimumValue,
                                    int maximumValue,
                                    Random rng)
    {
        this.rng = rng;
        this.minimumValue = minimumValue;
        this.range = maximumValue - minimumValue + 1;
    }


    /**
     * {@inheritDoc}
     */
    public Integer nextValue()
    {
        return rng.nextInt(range) + minimumValue;
    }
}
