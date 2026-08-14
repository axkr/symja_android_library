package org.matheclipse.parser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.matheclipse.parser.trie.PerfectHashMap;
import org.matheclipse.parser.trie.Trie;
import org.matheclipse.parser.trie.TrieBuilder;
import org.matheclipse.parser.trie.TrieMatch;
import org.matheclipse.parser.trie.TrieNode;
import org.matheclipse.parser.trie.TrieSequencerCharSequence;
import org.matheclipse.parser.trie.TrieSequencerIntArray;

/** Tests for the {@link Trie} with <code>int[]</code> sequences. */
public class TrieTestCase {

  private static Trie<int[], String> newTrie() {
    // the same configuration which `Config.TRIE_INT2EXPR_BUILDER` uses for sparse arrays
    return new TrieBuilder<int[], String, ArrayList<String>>(TrieSequencerIntArray.INSTANCE,
        TrieMatch.EXACT, () -> new ArrayList<String>(), (String) null, false).build();
  }

  /** The entries which start with the given prefix, in iteration order. */
  private static List<String> prefix(Trie<int[], String> trie, int... prefix) {
    List<String> result = new ArrayList<String>();
    for (TrieNode<int[], String> node : trie.nodeSet(prefix, TrieMatch.PARTIAL)) {
      if (startsWith(node.getKey(), prefix)) {
        result.add(Arrays.toString(node.getKey()) + "=" + node.getValue());
      }
    }
    return result;
  }

  private static boolean startsWith(int[] key, int[] prefix) {
    if (key.length < prefix.length) {
      return false;
    }
    for (int i = 0; i < prefix.length; i++) {
      if (key[i] != prefix[i]) {
        return false;
      }
    }
    return true;
  }

  @Test
  public void testNodeSetIsSortedLexicographically() {
    Trie<int[], String> trie = newTrie();
    trie.put(new int[] {3, 2}, "c");
    trie.put(new int[] {1, 5}, "b");
    trie.put(new int[] {1, 2}, "a");
    trie.put(new int[] {2, 1}, "d");

    StringBuilder buf = new StringBuilder();
    for (TrieNode<int[], String> node : trie.nodeSet()) {
      buf.append(node.getValue());
    }
    assertEquals("abdc", buf.toString());
  }

  @Test
  public void testPrefixIterationForRowWithSeveralEntries() {
    Trie<int[], String> trie = newTrie();
    trie.put(new int[] {1, 2}, "a");
    trie.put(new int[] {1, 5}, "b");
    trie.put(new int[] {2, 1}, "c");

    assertEquals("[[1, 2]=a, [1, 5]=b]", prefix(trie, 1).toString());
    assertEquals("[[2, 1]=c]", prefix(trie, 2).toString());
  }

  /**
   * A prefix which selects a single entry is stored in one leaf node. Iterating such a node must
   * return that entry and must not continue with the siblings of the leaf.
   */
  @Test
  public void testPrefixIterationForLeafNode() {
    Trie<int[], String> trie = newTrie();
    trie.put(new int[] {1, 2}, "a");
    trie.put(new int[] {3, 2}, "b");
    trie.put(new int[] {5, 4}, "c");

    assertEquals("[[3, 2]=b]", prefix(trie, 3).toString());
    assertEquals("[[1, 2]=a]", prefix(trie, 1).toString());
    assertEquals("[[5, 4]=c]", prefix(trie, 5).toString());
    // a complete key is a prefix of itself
    assertEquals("[[3, 2]=b]", prefix(trie, 3, 2).toString());
  }

  @Test
  public void testPrefixIterationForUnusedPrefix() {
    Trie<int[], String> trie = newTrie();
    trie.put(new int[] {1, 2}, "a");
    trie.put(new int[] {3, 2}, "b");

    assertEquals("[]", prefix(trie, 2).toString());
    assertEquals("[]", prefix(trie, 4).toString());
    assertEquals("[]", prefix(trie, 3, 7).toString());
  }

  private static Trie<String, String> newStringTrie() {
    return new TrieBuilder<String, String, ArrayList<String>>(TrieSequencerCharSequence.INSTANCE,
        TrieMatch.EXACT, () -> new ArrayList<String>(), (String) null, false).build();
  }

  /** {@link Trie#nodeSetAll(Object, TrieMatch)} has to iterate the matched sub trie only. */
  @Test
  public void testNodeSetAllIsScopedToTheSequence() {
    Trie<String, String> trie = newStringTrie();
    trie.put("meow", "1");
    trie.put("moo", "2");
    trie.put("bark", "3");

    // `nodeSetAll` also returns the naked nodes, so the split node of "meow" and "moo" is counted
    assertEquals(4, count(trie.nodeSetAll()));
    assertEquals(3, trie.size());
    // the sub trie of "m" only contains "meow" and "moo"
    assertEquals(2, count(trie.nodeSetAll("m", TrieMatch.PARTIAL)));
    assertEquals(1, count(trie.nodeSetAll("b", TrieMatch.PARTIAL)));
    assertEquals(0, count(trie.nodeSetAll("z", TrieMatch.PARTIAL)));
  }

  private static int count(Iterable<TrieNode<String, String>> nodes) {
    int result = 0;
    for (@SuppressWarnings("unused")
    TrieNode<String, String> node : nodes) {
      result++;
    }
    return result;
  }

  /**
   * The scoped views return an empty container for a sequence which isn't in the Trie, so that they
   * can be iterated without a null check.
   */
  @Test
  public void testScopedViewsOfUnusedSequenceAreEmpty() {
    Trie<String, String> trie = newStringTrie();
    trie.put("meow", "1");

    assertEquals(0, trie.values("zzz", TrieMatch.PARTIAL).size());
    assertEquals(0, trie.keySet("zzz", TrieMatch.PARTIAL).size());
    assertEquals(0, trie.nodeSet("zzz", TrieMatch.PARTIAL).size());
    assertEquals(0, trie.entrySet("zzz", TrieMatch.PARTIAL).size());
    assertEquals(1, trie.values("meow", TrieMatch.PARTIAL).size());
  }

  /** The nesting depth of a Trie isn't bounded by the initial size of the iterator's index stack. */
  @Test
  public void testIterationOfDeeplyNestedTrie() {
    Trie<String, String> trie = newStringTrie();
    StringBuilder sequence = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      sequence.append('a');
      trie.put(sequence.toString(), "v" + i);
    }

    int count = 0;
    for (@SuppressWarnings("unused")
    String key : trie.keySet()) {
      count++;
    }
    assertEquals(100, trie.size());
    assertEquals(100, count);
    assertEquals("v99", trie.get(sequence.toString()));
  }

  /** The cached views are transient and have to be created again after a deserialization. */
  @Test
  public void testSerializationRestoresTheCachedViews() throws Exception {
    Trie<String, String> trie = newStringTrie();
    trie.put("meow", "1");
    trie.put("bark", "2");

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
      output.writeObject(trie);
    }
    Trie<String, String> copy;
    try (ObjectInputStream input =
        new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
      copy = (Trie<String, String>) input.readObject();
    }

    assertEquals(2, copy.size());
    assertEquals("1", copy.get("meow"));
    assertEquals("[bark, meow]", copy.keySet().toString());
    assertEquals(2, copy.values().size());
    assertEquals(2, copy.entrySet().size());
    assertEquals(2, copy.nodeSet().size());
  }

  @Test
  public void testIteratorContract() {
    Trie<String, String> trie = newStringTrie();
    trie.put("a", "1");

    Iterator<String> iterator = trie.keySet().iterator();
    assertThrows(IllegalStateException.class, () -> iterator.remove());
    assertEquals("a", iterator.next());
    assertThrows(NoSuchElementException.class, () -> iterator.next());

    Trie<String, String> empty = newStringTrie();
    Iterator<String> emptyIterator = empty.keySet(("zzz"), TrieMatch.PARTIAL).iterator();
    assertThrows(NoSuchElementException.class, () -> emptyIterator.next());
  }

  /** Removing through the iterator has to keep working for all nodes, including a valued root. */
  @Test
  public void testIteratorRemove() {
    Trie<String, String> trie = newStringTrie();
    trie.put("a", "1");
    trie.put("b", "2");
    trie.put("c", "3");

    Iterator<String> iterator = trie.keySet().iterator();
    assertEquals("a", iterator.next());
    iterator.remove();
    assertEquals(2, trie.size());
    assertEquals(null, trie.get("a"));
    assertEquals("2", trie.get("b"));

    // the sub view of a single entry is rooted in the valued node itself
    Trie<String, String> single = newStringTrie();
    single.put("meow", "1");
    single.put("bark", "2");
    Iterator<String> subIterator = single.keySet("meow", TrieMatch.PARTIAL).iterator();
    assertEquals("meow", subIterator.next());
    subIterator.remove();
    assertEquals(1, single.size());
    assertEquals(null, single.get("meow"));
    assertEquals("2", single.get("bark"));
  }

  /**
   * Removing the last child of a node leaves an empty {@link PerfectHashMap} behind and removing an
   * entry shrinks the table of its parent. The iteration has to handle both.
   */
  @Test
  public void testIterationAfterRemove() {
    Trie<String, String> trie = newStringTrie();
    trie.put("a", "1");
    trie.put("ab", "2");
    trie.put("abc", "3");
    trie.put("b", "4");

    trie.remove("abc");
    assertEquals("[a, ab, b]", trie.keySet().toString());
    trie.remove("ab");
    assertEquals("[a, b]", trie.keySet().toString());
    trie.remove("a");
    assertEquals("[b]", trie.keySet().toString());
    trie.remove("b");
    assertEquals("[]", trie.keySet().toString());
    assertEquals(0, trie.size());

    // the trie is still usable after everything was removed
    trie.put("c", "5");
    assertEquals("[c]", trie.keySet().toString());
    assertEquals("5", trie.get("c"));
  }

  /**
   * Random sequences of <code>put</code> and <code>remove</code> operations compared against a
   * {@link HashMap}. The seed is fixed, so a failure can be reproduced.
   */
  @Test
  public void testRandomOperationsAgainstHashMap() {
    Random random = new Random(12345);
    String[] alphabet = {"a", "b", "c"};

    for (int round = 0; round < 50; round++) {
      Trie<String, String> trie = newStringTrie();
      Map<String, String> expected = new HashMap<String, String>();

      for (int operation = 0; operation < 200; operation++) {
        StringBuilder sequence = new StringBuilder();
        int length = 1 + random.nextInt(5);
        for (int i = 0; i < length; i++) {
          sequence.append(alphabet[random.nextInt(alphabet.length)]);
        }
        String key = sequence.toString();
        if (random.nextInt(3) == 0) {
          assertEquals(expected.remove(key), trie.remove(key));
        } else {
          String value = "v" + operation;
          assertEquals(expected.put(key, value), trie.put(key, value));
        }
        assertEquals(expected.size(), trie.size());
      }

      for (Map.Entry<String, String> entry : expected.entrySet()) {
        assertEquals(entry.getValue(), trie.get(entry.getKey(), TrieMatch.EXACT));
      }
      // the keys are iterated completely and in lexicographic order
      List<String> sorted = new ArrayList<String>(expected.keySet());
      Collections.sort(sorted);
      List<String> iterated = new ArrayList<String>();
      for (String key : trie.keySet()) {
        iterated.add(key);
      }
      assertEquals(sorted, iterated);
    }
  }

  @Test
  public void testPrefixIterationWithThreeDimensions() {
    Trie<int[], String> trie = newTrie();
    trie.put(new int[] {1, 1, 1}, "a");
    trie.put(new int[] {1, 2, 3}, "b");
    trie.put(new int[] {2, 1, 1}, "c");

    assertEquals("[[1, 1, 1]=a, [1, 2, 3]=b]", prefix(trie, 1).toString());
    assertEquals("[[2, 1, 1]=c]", prefix(trie, 2).toString());
    assertEquals("[[1, 2, 3]=b]", prefix(trie, 1, 2).toString());
  }
}
