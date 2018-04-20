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
import org.uncommons.maths.binary.BinaryUtils;
import org.uncommons.maths.binary.BitString;
import org.uncommons.maths.number.ConstantGenerator;
import org.uncommons.maths.number.NumberGenerator;

/**
 * Discrete random sequence that follows a
 * <a href="http://en.wikipedia.org/wiki/Binomial_distribution" target="_top">binomial
 * distribution</a>.
 * @author Daniel Dyer
 */
public class BinomialGenerator implements NumberGenerator<Integer>
{
    private final Random rng;
    private final NumberGenerator<Integer> n;
    private final NumberGenerator<Double> p;

    // Cache the fixed-point representation of p to avoid having to
    // recalculate it for each value generated.  Only calculate it
    // if and when p changes.
    private transient BitString pBits;
    private transient double lastP;


    /**
     * <p>Creates a generator of binomially-distributed values.  The number of
     * trials ({@literal n}) and the probability of success in each trial
     * ({@literal p}) are determined by the provided {@link NumberGenerator}s.
     * This means that the statistical parameters of this generator may change
     * over time.  One example of where this is useful is if the {@literal n}
     * and {@literal p} generators are attached to GUI controls that allow a
     * user to tweak the parameters while a program is running.</p>
     * <p>To create a Binomial generator with a constant {@literal n} and
     * {@literal p}, use the {@link #BinomialGenerator(int, double, Random)}
     * constructor instead.</p>
     * @param n A {@link NumberGenerator} that provides the number of trials for
     * the Binomial distribution used for the next generated value.  This generator
     * must produce only positive values.
     * @param p A {@link NumberGenerator} that provides the probability of succes
     * in a single trial for the Binomial distribution used for the next
     * generated value.  This generator must produce only values in the range 0 - 1.
     * @param rng The source of randomness.
     */
    public BinomialGenerator(NumberGenerator<Integer> n,
                             NumberGenerator<Double> p,
                             Random rng)
    {
        this.n = n;
        this.p = p;
        this.rng = rng;
    }


    /**
     * Creates a generator of binomially-distributed values from a distribution
     * with the specified parameters.
     * @param n The number of trials (and therefore the maximum possible value returned
     * by this sequence).
     * @param p The probability (between 0 and 1) of success in any one trial.
     * @param rng The source of randomness used to generate the binomial values.
     */
    public BinomialGenerator(int n,
                             double p,
                             Random rng)
    {
        this(new ConstantGenerator<Integer>(n),
             new ConstantGenerator<Double>(p),
             rng);
        if (n <= 0)
        {
            throw new IllegalArgumentException("n must be a positive integer.");
        }
        if (p <= 0 || p >= 1)
        {
            throw new IllegalArgumentException("p must be between 0 and 1.");
        }
    }


    /**
     * Generate the next binomial value from the current values of
     * {@literal n} and {@literal p}.  The algorithm used is from
     * The Art of Computer Programming Volume 2 (Seminumerical Algorithms)
     * by Donald Knuth (page 589 in the Third Edition) where it is
     * credited to J.H. Ahrens. 
     */
    public Integer nextValue()
    {
        // Regenerate the fixed point representation of p if it has changed.
        double newP = p.nextValue();
        if (pBits == null || newP != lastP)
        {
            lastP = newP;
            pBits = BinaryUtils.convertDoubleToFixedPointBits(newP);
        }

        int trials = n.nextValue();
        int totalSuccesses = 0;
        int pIndex = pBits.getLength() - 1;

        while (trials > 0 && pIndex >= 0)
        {
            int successes = binomialWithEvenProbability(trials);
            trials -= successes;
            if (pBits.getBit(pIndex))
            {
                totalSuccesses += successes;
            }
            --pIndex;
        }

        return totalSuccesses;
    }


    /**
     * Generating binomial values when {@literal p = 0.5} is straightforward.
     * It simply a case of generating {@literal n} random bits and
     * counting how many are 1s.
     * @param n The number of trials.
     * @return The number of successful outcomes from {@literal n} trials.
     */
    private int binomialWithEvenProbability(int n)
    {
        BitString bits = new BitString(n, rng);
        return bits.countSetBits();
    }
}
