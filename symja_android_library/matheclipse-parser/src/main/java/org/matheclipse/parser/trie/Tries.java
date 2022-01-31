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

import java.nio.ByteBuffer;

/**
 * A class that neatly creates Tries and will hide which Trie implementation is returned.
 *
 * @author Philip Diffenderfer
 */
public final class Tries {

  /**
   * Creates a Trie where the keys are case-sensitive Strings.
   *
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<String, T> forStrings() {
    return new Trie<String, T>(TrieSequencerCharSequence.INSTANCE);
  }

  /**
   * Creates a Trie where the keys are case-sensitive Strings.
   *
   * @param defaultValue The default value of the Trie is the value returned when
   *        {@link Trie#get(Object)} or {@link Trie#get(Object, TrieMatch)} is called and no match
   *        was found.
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<String, T> forStrings(T defaultValue) {
    return new Trie<String, T>(TrieSequencerCharSequence.INSTANCE, defaultValue);
  }

  /**
   * Creates a Trie where the keys are case-insensitive Strings.
   *
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<String, T> forInsensitiveStrings() {
    return new Trie<String, T>(TrieSequencerCharSequenceCaseInsensitive.INSTANCE);
  }

  /**
   * Creates a Trie where the keys are case-insensitive Strings.
   *
   * @param defaultValue The default value of the Trie is the value returned when
   *        {@link Trie#get(Object)} or {@link Trie#get(Object, TrieMatch)} is called and no match
   *        was found.
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<String, T> forInsensitiveStrings(T defaultValue) {
    return new Trie<String, T>(TrieSequencerCharSequenceCaseInsensitive.INSTANCE, defaultValue);
  }

  /**
   * Creates a Trie where the keys are case-sensitive character arrays.
   *
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<char[], T> forChars() {
    return new Trie<char[], T>(TrieSequencerCharArray.INSTANCE);
  }

  /**
   * Creates a Trie where the keys are case-sensitive character arrays.
   *
   * @param defaultValue The default value of the Trie is the value returned when
   *        {@link Trie#get(Object)} or {@link Trie#get(Object, TrieMatch)} is called and no match
   *        was found.
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<char[], T> forChars(T defaultValue) {
    return new Trie<char[], T>(TrieSequencerCharArray.INSTANCE, defaultValue);
  }

  /**
   * Creates a Trie where the keys are case-insensitive character arrays.
   *
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<char[], T> forInsensitiveChars() {
    return new Trie<char[], T>(TrieSequencerCharArrayCaseInsensitive.INSTANCE);
  }

  /**
   * Creates a Trie where the keys are case-insensitive character arrays.
   *
   * @param defaultValue The default value of the Trie is the value returned when
   *        {@link Trie#get(Object)} or {@link Trie#get(Object, TrieMatch)} is called and no match
   *        was found.
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<char[], T> forInsensitiveChars(T defaultValue) {
    return new Trie<char[], T>(TrieSequencerCharArrayCaseInsensitive.INSTANCE, defaultValue);
  }

  /**
   * Creates a Trie where the keys are bytes.
   *
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<byte[], T> forBytes() {
    return new Trie<byte[], T>(TrieSequencerByteArray.INSTANCE);
  }

  /**
   * Creates a Trie where the keys are bytes.
   *
   * @param defaultValue The default value of the Trie is the value returned when
   *        {@link Trie#get(Object)} or {@link Trie#get(Object, TrieMatch)} is called and no match
   *        was found.
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<byte[], T> forBytes(T defaultValue) {
    return new Trie<byte[], T>(TrieSequencerByteArray.INSTANCE, defaultValue);
  }

  /**
   * Creates a Trie where the keys are shorts.
   *
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<short[], T> forShorts() {
    return new Trie<short[], T>(TrieSequencerShortArray.INSTANCE);
  }

  /**
   * Creates a Trie where the keys are shorts.
   *
   * @param defaultValue The default value of the Trie is the value returned when
   *        {@link Trie#get(Object)} or {@link Trie#get(Object, TrieMatch)} is called and no match
   *        was found.
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<short[], T> forShorts(T defaultValue) {
    return new Trie<short[], T>(TrieSequencerShortArray.INSTANCE, defaultValue);
  }

  /**
   * Creates a Trie where the keys are integers.
   *
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<int[], T> forInts() {
    return new Trie<int[], T>(TrieSequencerIntArray.INSTANCE);
  }

  /**
   * Creates a Trie where the keys are integers.
   *
   * @param defaultValue The default value of the Trie is the value returned when
   *        {@link Trie#get(Object)} or {@link Trie#get(Object, TrieMatch)} is called and no match
   *        was found.
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<int[], T> forInts(T defaultValue) {
    return new Trie<int[], T>(TrieSequencerIntArray.INSTANCE, defaultValue);
  }

  /**
   * Creates a Trie where the keys are longs.
   *
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<long[], T> forLongs() {
    return new Trie<long[], T>(TrieSequencerLongArray.INSTANCE);
  }

  /**
   * Creates a Trie where the keys are longs.
   *
   * @param defaultValue The default value of the Trie is the value returned when
   *        {@link Trie#get(Object)} or {@link Trie#get(Object, TrieMatch)} is called and no match
   *        was found.
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<long[], T> forLongs(T defaultValue) {
    return new Trie<long[], T>(TrieSequencerLongArray.INSTANCE, defaultValue);
  }

  /**
   * Creates a Trie where the keys are ByteBuffers.
   *
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<ByteBuffer, T> forByteBuffers() {
    return new Trie<ByteBuffer, T>(TrieSequencerByteBuffer.INSTANCE);
  }

  /**
   * Creates a Trie where the keys are ByteBuffers.
   *
   * @param defaultValue The default value of the Trie is the value returned when
   *        {@link Trie#get(Object)} or {@link Trie#get(Object, TrieMatch)} is called and no match
   *        was found.
   * @param <T> The value type.
   * @return The reference to a newly instantiated Trie.
   */
  public static <T> Trie<ByteBuffer, T> forByteBuffers(T defaultValue) {
    return new Trie<ByteBuffer, T>(TrieSequencerByteBuffer.INSTANCE, defaultValue);
  }
}
