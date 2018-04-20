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

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.uncommons.maths.binary.BinaryUtils;

/**
 * <p>Non-linear random number generator based on the AES block cipher in counter mode.
 * Uses the seed as a key to encrypt a 128-bit counter using AES(Rijndael).</p>
 * 
 * <p>By default, we only use a 128-bit key for the cipher because any larger key requires
 * the inconvenience of installing the unlimited strength cryptography policy
 * files for the Java platform.  Larger keys may be used (192 or 256 bits) but if the
 * cryptography policy files are not installed, a
 * {@link java.security.GeneralSecurityException} will be thrown.</p>
 *
 * <p><em>NOTE: Because instances of this class require 128-bit seeds, it is not
 * possible to seed this RNG using the {@link #setSeed(long)} method inherited
 * from {@link Random}.  Calls to this method will have no effect.
 * Instead the seed must be set by a constructor.</em></p>
 *
 * <p><em>NOTE: THIS CLASS IS NOT SERIALIZABLE</em></p>
 * 
 * @author Daniel Dyer
 */
public class AESCounterRNG extends Random implements RepeatableRNG
{
    private static final int DEFAULT_SEED_SIZE_BYTES = 16;

    private final byte[] seed;
    private final Cipher cipher; // TO DO: This field is not Serializable.
    private final byte[] counter = new byte[16]; // 128-bit counter.

    // Lock to prevent concurrent modification of the RNG's internal state.
    private final ReentrantLock lock = new ReentrantLock();


    private byte[] currentBlock = null;
    private int index = 0;


    /**
     * Creates a new RNG and seeds it using 128 bits from the default seeding strategy.
     * @throws GeneralSecurityException If there is a problem initialising the AES cipher.
     */
    public AESCounterRNG() throws GeneralSecurityException
    {
        this(DEFAULT_SEED_SIZE_BYTES);
    }


    /**
     * Seed the RNG using the provided seed generation strategy to create a 128-bit
     * seed.
     * @param seedGenerator The seed generation strategy that will provide
     * the seed value for this RNG.
     * @throws SeedException If there is a problem generating a seed.
     * @throws GeneralSecurityException If there is a problem initialising the AES cipher.
     */
    public AESCounterRNG(SeedGenerator seedGenerator) throws SeedException,
                                                             GeneralSecurityException
    {
        this(seedGenerator.generateSeed(DEFAULT_SEED_SIZE_BYTES));
    }


    /**
     * Seed the RNG using the default seed generation strategy to create a seed of the
     * specified size.
     * @param seedSizeBytes The number of bytes to use for seed data.  Valid values
     * are 16 (128 bits), 24 (192 bits) and 32 (256 bits).  Any other values will
     * result in an exception from the AES implementation.
     * @throws GeneralSecurityException If there is a problem initialising the AES cipher.
     * @since 1.0.2
     */
    public AESCounterRNG(int seedSizeBytes) throws GeneralSecurityException
    {
        this(DefaultSeedGenerator.getInstance().generateSeed(seedSizeBytes));
    }


    /**
     * Creates an RNG and seeds it with the specified seed data.
     * @param seed The seed data used to initialise the RNG.
     * @throws GeneralSecurityException If there is a problem initialising the AES cipher.
     */
    public AESCounterRNG(byte[] seed) throws GeneralSecurityException
    {
        if (seed == null)
        {
            throw new IllegalArgumentException("AES RNG requires a 128-bit, 192-bit or 256-bit seed.");
        }
        this.seed = seed.clone();

        cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new AESKey(this.seed));
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getSeed()
    {
        return seed.clone();
    }


    private void incrementCounter()
    {
        for (int i = 0; i < counter.length; i++)
        {
            ++counter[i];
            if (counter[i] != 0) // Check whether we need to loop again to carry the one.
            {
                break;
            }
        }
    }


    /**
     * Generates a single 128-bit block (16 bytes).
     * @throws GeneralSecurityException If there is a problem with the cipher
     * that generates the random data.
     * @return A 16-byte block of random data.
     */
    private byte[] nextBlock() throws GeneralSecurityException
    {
        incrementCounter();
        return cipher.doFinal(counter);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected final int next(int bits)
    {
        int result;
        try
        {
            lock.lock();
            if (currentBlock == null || currentBlock.length - index < 4)
            {
                try
                {
                    currentBlock = nextBlock();
                    index = 0;
                }
                catch (GeneralSecurityException ex)
                {
                    // Should never happen.  If initialisation succeeds without exceptions
                    // we should be able to proceed indefinitely without exceptions.
                    throw new IllegalStateException("Failed creating next random block.", ex);
                }
            }
            result = BinaryUtils.convertBytesToInt(currentBlock, index);
            index += 4;
        }
        finally
        {
            lock.unlock();
        }
        return result >>> (32 - bits);
    }



    /**
     * Trivial key implementation for use with AES cipher.
     */
    private static final class AESKey implements SecretKey
    {
        private final byte[] keyData;

        private AESKey(byte[] keyData)
        {
            this.keyData = keyData;
        }

        public String getAlgorithm()
        {
            return "AES";
        }

        public String getFormat()
        {
            return "RAW";
        }

        public byte[] getEncoded()
        {
            return keyData;
        }


        @Override
        public boolean equals(Object other)
        {
            if (this == other)
            {
                return true;
            }
            else if (other == null || getClass() != other.getClass())
            {
                return false;
            }
            else
            {
                return Arrays.equals(keyData, ((AESKey) other).keyData);
            }
        }


        @Override
        public int hashCode()
        {
            return Arrays.hashCode(keyData);
        }
    }
}
