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

import org.uncommons.maths.binary.BinaryUtils;

/**
 * Seed generator that maintains multiple strategies for seed
 * generation and will delegate to the best one available for the
 * current operating environment.
 * @author Daniel Dyer
 */
public final class DefaultSeedGenerator implements SeedGenerator
{
    private static final String DEBUG_PROPERTY = "org.uncommons.maths.random.debug";
    
    /** Singleton instance. */
    private static final DefaultSeedGenerator INSTANCE = new DefaultSeedGenerator();

    /** Delegate generators. */
    private static final SeedGenerator[] GENERATORS = new SeedGenerator[]
    {
        new DevRandomSeedGenerator(),
        new RandomDotOrgSeedGenerator(),
        new SecureRandomSeedGenerator()
    };


    private DefaultSeedGenerator()
    {
        // Private constructor prevents external instantiation.
    }


    /**
     * @return The singleton instance of this class.
     */
    public static DefaultSeedGenerator getInstance()
    {
        return INSTANCE;
    }


    /**
     * Generates a seed by trying each of the available strategies in
     * turn until one succeeds.  Tries the most suitable strategy first
     * and eventually degrades to the least suitable (but guaranteed to
     * work) strategy.
     * @param length The length (in bytes) of the seed.
     * @return A random seed of the requested length.
     */
    public byte[] generateSeed(int length)
    {
        for (SeedGenerator generator : GENERATORS)
        {
            try
            {
                byte[] seed = generator.generateSeed(length);
                try
                {
                    boolean debug = System.getProperty(DEBUG_PROPERTY, "false").equals("true");
                    if (debug)
                    {
                        String seedString = BinaryUtils.convertBytesToHexString(seed);
                        System.out.println(seed.length + " bytes of seed data acquired from " + generator + ":");
                        System.out.println("  " + seedString);
                    }
                }
                catch (SecurityException ex)
                {
                    // Ignore, means we can't read the property so just default to false.
                }

                return seed;
            }
            catch (SeedException ex)
            {
                // Ignore and try the next generator...
            }
        }
        // This shouldn't happen as at least one the generators should be
        // able to generate a seed.
        throw new IllegalStateException("All available seed generation strategies failed.");
    }
}
