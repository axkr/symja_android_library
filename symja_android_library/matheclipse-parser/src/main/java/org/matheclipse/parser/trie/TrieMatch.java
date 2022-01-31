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
 * The matching logic used for retrieving values from a Trie or for determining the existence of
 * values given an input/key sequence.
 *
 * @author Philip Diffenderfer
 */
public enum TrieMatch {

  /**
   * A PARTIAL match only requires the input sequence to be a subset of the sequences stored in the
   * Trie. If the sequence "meow" is stored in the Trie, then it can partially match on "m", "me",
   * "meo", "meow", "meowa", etc.
   */
  PARTIAL,

  /**
   * An EXACT match requires the input sequence to be an exact match to the sequences stored in the
   * Trie. If the sequence "meow" is stored in the Trie, then it can only match on "meow".
   */
  EXACT,

  /**
   * A START_WITH match requires the input sequence to be a superset of the sequences stored in the
   * Trie. If the sequence "meow" is stored in the Trie, then it can match on "meow", "meowa",
   * "meowab", etc.
   */
  STARTS_WITH;
}
