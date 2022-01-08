/*
 * NOTICE OF LICENSE
 *
 * This source file is subject to the Open Software License (OSL 3.0) that is
 * bundled with this package in the file LICENSE.txt. It is also available
 * through the world-wide-web at http://opensource.org/licenses/osl-3.0.php
 * If you did not receive a copy of the license and are unable to obtain it
 * through the world-wide-web, please send an email to magnos.software@gmail.com
 * so we can send you a copy immediately. If you use any of this software please
 * notify me via our website or email, your feedback is much appreciated.
 *
 * @copyright   Copyright (c) 2011 Magnos Software (http://www.magnos.org)
 * @license     http://opensource.org/licenses/osl-3.0.php
 *              Open Software License (OSL 3.0)
 */

package org.matheclipse.parser.trie;

/**
 * A {@link TrieSequencer} implementation where any subclass of CharSequence (i.e. String) is the
 * sequence type. This implementation is case-insensitive.
 *
 * @author Philip Diffenderfer
 */
public class TrieSequencerCharSequenceCaseInsensitive<S extends CharSequence>
    extends TrieSequencerCharSequence<S> {

  /** */
  private static final long serialVersionUID = 1L;

  /** Only a single instance is needed of this sequencer. */
  public static final TrieSequencerCharSequenceCaseInsensitive<String> INSTANCE =
      new TrieSequencerCharSequenceCaseInsensitive<String>();

  @Override
  public int matches(S sequenceA, int indexA, S sequenceB, int indexB, int count) {
    for (int i = 0; i < count; i++) {
      char a = sequenceA.charAt(indexA + i);
      char b = sequenceB.charAt(indexB + i);

      if (Character.toLowerCase(a) != Character.toLowerCase(b)) {
        return i;
      }
    }

    return count;
  }

  @Override
  public int hashOf(S sequence, int i) {
    return Character.toLowerCase(sequence.charAt(i));
  }

  /**
   * When deserialized, just return the single instance.
   *
   * @return {@link #INSTANCE}
   */
  @Override
  protected Object readResolve() {
    return INSTANCE;
  }
}
