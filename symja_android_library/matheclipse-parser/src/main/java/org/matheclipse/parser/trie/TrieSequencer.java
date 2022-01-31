/*
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Open Software License (OSL 3.0) that is bundled with this
 * package in the file LICENSE.txt. It is also available through the world-wide-web at
 * http://opensource.org/licenses/osl-3.0.php If you did not receive a copy of the license and are
 * unable to obtain it through the world-wide-web, please send an email to magnos.software@gmail.com
 * so we can send you a copy immediately. If you use any of this software please notify me via our
 * website or email, your feedback is much appreciated.
 *
 * @copyright Copyright (c) 2011 Magnos Software (http://www.magnos.org)
 * 
 * @license http://opensource.org/licenses/osl-3.0.php Open Software License (OSL 3.0)
 */

package org.matheclipse.parser.trie;

import java.io.Serializable;

/**
 * A TrieSequencer enables a {@link Trie} to use keys of type S. A sequence is a linear set of
 * elements.
 *
 * @author Philip Diffenderfer
 * @param <S> The sequence type.
 */
public interface TrieSequencer<S> extends Serializable {

  /**
   * Determines the maximum number of elements that match between sequences A and B where comparison
   * starts at the given indices up to the given count.
   *
   * @param sequenceA The first sequence to count matches on.
   * @param indexA The offset into the first sequence.
   * @param sequenceB The second sequence to count matches on.
   * @param indexB The offset into the second sequence.
   * @param count The maximum number of matches to search for.
   * @return A number between 0 (inclusive) and count (inclusive) that is the number of matches
   *         between the two sequence sections.
   */
  public int matches(S sequenceA, int indexA, S sequenceB, int indexB, int count);

  /**
   * Calculates the length (number of elements) of the given sequence.
   *
   * @param sequence The sequence to measure.
   * @return The length of the given sequence.
   */
  public int lengthOf(S sequence);

  /**
   * Calculates the hash of the element at the given index in the given sequence. The hash is used
   * as a key for the {@link PerfectHashMap} used internally in a Trie to quickly retrieve entries.
   * Typical implementations based on characters return the ASCII value of the character, since it
   * yields dense numerical values. The more dense the hashes returned (the smaller the difference
   * between the minimum and maximum returnable hash means it's more dense), the less space that is
   * wasted.
   *
   * @param sequence The sequence.
   * @param index The index of the element to calculate the hash of.
   * @return The hash of the element in the sequence at the index.
   */
  public int hashOf(S sequence, int index);
}
