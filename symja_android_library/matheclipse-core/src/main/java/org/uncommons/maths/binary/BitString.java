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
package org.uncommons.maths.binary;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

/**
 * <p>Implementation of a fixed-length bit-string.  Internally, bits are packed into an
 * array of ints.  This implementation makes more efficient use of space than the
 * alternative approach of using an array of booleans.</p>
 * <p>This class is preferable to {@link java.util.BitSet} if a fixed number of bits is
 * required.</p> 
 * @author Daniel Dyer
 */
public final class BitString implements Cloneable, Serializable
{
    private static final int WORD_LENGTH = 32;

    private final int length;

    /**
     * Store the bits packed in an array of 32-bit ints.  This field cannot
     * be declared final because it must be cloneable.
     */
    private int[] data;

    
    /**
     * Creates a bit string of the specified length with all bits
     * initially set to zero (off).
     * @param length The number of bits.
     */
    public BitString(int length)
    {
        if (length < 0)
        {
            throw new IllegalArgumentException("Length must be non-negative.");
        }
        this.length = length;
        this.data = new int[(length + WORD_LENGTH - 1) / WORD_LENGTH];
    }


    /**
     * Creates a bit string of the specified length with each bit set
     * randomly (the distribution of bits is uniform so long as the output
     * from the provided RNG is also uniform).  Using this constructor is
     * more efficient than creating a bit string and then randomly setting
     * each bit individually.
     * @param length The number of bits.
     * @param rng A source of randomness.
     */
    public BitString(int length, Random rng)
    {
        this(length);
        // We can set bits 32 at a time rather than calling rng.nextBoolean()
        // and setting each one individually.
        for (int i = 0; i < data.length; i++)
        {
            data[i] = rng.nextInt();
        }
        // If the last word is not fully utilised, zero any out-of-bounds bits.
        // This is necessary because the countSetBits() methods will count
        // out-of-bounds bits.
        int bitsUsed = length % WORD_LENGTH;
        if (bitsUsed < WORD_LENGTH)
        {
            int unusedBits = WORD_LENGTH - bitsUsed;
            int mask = 0xFFFFFFFF >>> unusedBits;
            data[data.length - 1] &= mask;
        }
    }


    /**
     * Initialises the bit string from a character string of 1s and 0s
     * in big-endian order.
     * @param value A character string of ones and zeros.
     */    
    public BitString(String value)
    {
        this(value.length());
        for (int i = 0; i < value.length(); i++)
        {
            if (value.charAt(i) == '1')
            {
                setBit(value.length() - (i + 1), true);
            }
            else if (value.charAt(i) != '0')
            {
                throw new IllegalArgumentException("Illegal character at position " + i);
            }
        }
    }


    /**
     * @return The length of this bit string.
     */
    public int getLength()
    {
        return length;
    }


    /**
     * Returns the bit at the specified index.
     * @param index The index of the bit to look-up (0 is the least-significant bit).
     * @return A boolean indicating whether the bit is set or not.
     * @throws IndexOutOfBoundsException If the specified index is not a bit
     * position in this bit string.
     */
    public boolean getBit(int index)
    {
        assertValidIndex(index);
        int word = index / WORD_LENGTH;
        int offset = index % WORD_LENGTH;
        return (data[word] & (1 << offset)) != 0;
    }


    /**
     * Sets the bit at the specified index.
     * @param index The index of the bit to set (0 is the least-significant bit).
     * @param set A boolean indicating whether the bit should be set or not.
     * @throws IndexOutOfBoundsException If the specified index is not a bit
     * position in this bit string.
     */
    public void setBit(int index, boolean set)
    {
        assertValidIndex(index);
        int word = index / WORD_LENGTH;
        int offset = index % WORD_LENGTH;
        if (set)
        {
            data[word] |= (1 << offset);
        }
        else // Unset the bit.
        {
            data[word] &= ~(1 << offset);
        }
    }


    /**
     * Inverts the value of the bit at the specified index.
     * @param index The bit to flip (0 is the least-significant bit).
     * @throws IndexOutOfBoundsException If the specified index is not a bit
     * position in this bit string.     
     */
    public void flipBit(int index)
    {
        assertValidIndex(index);
        int word = index / WORD_LENGTH;
        int offset = index % WORD_LENGTH;
        data[word] ^= (1 << offset);
    }


    /**
     * Helper method to check whether a bit index is valid or not.
     * @param index The index to check.
     * @throws IndexOutOfBoundsException If the index is not valid.
     */
    private void assertValidIndex(int index)
    {
        if (index >= length || index < 0)
        {
            throw new IndexOutOfBoundsException("Invalid index: " + index + " (length: " + length + ")");
        }
    }


    /**
     * @return The number of bits that are 1s rather than 0s.
     */
    public int countSetBits()
    {
        int count = 0;
        for (int x : data)
        {
            while (x != 0)
            {
                x &= (x - 1); // Unsets the least significant on bit.
                ++count; // Count how many times we have to unset a bit before x equals zero.
            }
        }
        return count;
    }


    /**
     * @return The number of bits that are 0s rather than 1s.
     */
    public int countUnsetBits()
    {
        return length - countSetBits();
    }


    /**
     * Interprets this bit string as being a binary numeric value and returns
     * the integer that it represents.
     * @return A {@link BigInteger} that contains the numeric value represented
     * by this bit string.
     */
    public BigInteger toNumber()
    {
        return (new BigInteger(toString(), 2));
    }


    /**
     * An efficient method for exchanging data between two bit strings.  Both bit strings must
     * be long enough that they contain the full length of the specified substring. 
     * @param other The bitstring with which this bitstring should swap bits.
     * @param start The start position for the substrings to be exchanged.  All bit
     * indices are big-endian, which means position 0 is the rightmost bit.
     * @param length The number of contiguous bits to swap.
     */
    public void swapSubstring(BitString other, int start, int length)
    {
        assertValidIndex(start);
        other.assertValidIndex(start);
        
        int word = start / WORD_LENGTH;

        int partialWordSize = (WORD_LENGTH - start) % WORD_LENGTH;
        if (partialWordSize > 0)
        {
            swapBits(other, word, 0xFFFFFFFF << (WORD_LENGTH - partialWordSize));
            ++word;
        }

        int remainingBits = length - partialWordSize;
        int stop = remainingBits / WORD_LENGTH;
        for (int i = word; i < stop; i++)
        {
            int temp = data[i];
            data[i] = other.data[i];
            other.data[i] = temp;
        }

        remainingBits %= WORD_LENGTH;
        if (remainingBits > 0)
        {
            swapBits(other, word, 0xFFFFFFFF >>> (WORD_LENGTH - remainingBits));
        }
    }


    /**
     * @param other The BitString to exchange bits with.
     * @param word The word index of the word that will be swapped between the two bit strings.
     * @param swapMask A mask that specifies which bits in the word will be swapped.
     */
    private void swapBits(BitString other, int word, int swapMask)
    {
        int preserveMask = ~swapMask;
        int preservedThis = data[word] & preserveMask;
        int preservedThat = other.data[word] & preserveMask;
        int swapThis = data[word] & swapMask;
        int swapThat = other.data[word] & swapMask;
        data[word] = preservedThis | swapThat;
        other.data[word] = preservedThat | swapThis;
    }


    /**
     * Creates a textual representation of this bit string in big-endian
     * order (index 0 is the right-most bit).
     * @return This bit string rendered as a String of 1s and 0s.
     */
    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        for (int i = length - 1; i >= 0; i--)
        {
            buffer.append(getBit(i) ? '1' : '0');
        }
        return buffer.toString();
    }


    /**
     * @return An identical copy of this bit string.
     */
    @Override
    public BitString clone()
    {
        try
        {
            BitString clone = (BitString) super.clone();
            clone.data = data.clone();
            return clone;
        }
        catch (CloneNotSupportedException ex)
        {
            // Not possible.
            throw (Error) new InternalError("Cloning failed.").initCause(ex);
        }
    }


    /**
     * @return True if the argument is a BitString instance and both bit
     * strings are the same length with identical bits set/unset.
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        BitString bitString = (BitString) o;

        return length == bitString.length && Arrays.equals(data, bitString.data);
    }


    /**
     * Over-ridden to be consistent with {@link #equals(Object)}.
     */
    @Override
    public int hashCode()
    {
        int result = length;
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
