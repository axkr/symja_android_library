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

/**
 * A {@link TrieSequencer} implementation where any subclass of CharSequence (i.e. String) is the
 * sequence type. This implementation is case-sensitive.
 *
 * @author Philip Diffenderfer
 */
public class TrieSequencerCharSequence<S extends CharSequence> implements TrieSequencer<S> {

  /** */
  private static final long serialVersionUID = 1L;

  /** Only a single instance is needed of this sequencer. */
  public static final TrieSequencerCharSequence<String> INSTANCE =
      new TrieSequencerCharSequence<String>();

  @Override
  public int matches(S sequenceA, int indexA, S sequenceB, int indexB, int count) {
    for (int i = 0; i < count; i++) {
      if (sequenceA.charAt(indexA + i) != sequenceB.charAt(indexB + i)) {
        return i;
      }
    }

    return count;
  }

  @Override
  public int lengthOf(S sequence) {
    return sequence.length();
  }

  @Override
  public int hashOf(S sequence, int i) {
    return sequence.charAt(i);
  }

  /**
   * When deserialized, just return the single instance.
   *
   * @return {@link #INSTANCE}
   */
  protected Object readResolve() {
    return INSTANCE;
  }
}
