package org.matheclipse.core.patternmatching.ruleindex;

import java.util.List;
import org.matheclipse.core.eval.util.OpenIntToList;
import org.matheclipse.core.patternmatching.hash.AbstractHashedPatternRules;

/**
 * A prefilter over the rules of one
 * {@link org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher} rule map.
 *
 * <p>
 * A <i>feature</i> is a hash value which occurs as {@link AbstractHashedPatternRules#getHash1()} or
 * {@link AbstractHashedPatternRules#getHash2()} of some rule - exactly the values the matcher
 * compares an argument hash against. Two consequences drive the filter:
 * <ul>
 * <li>an argument whose hash value is no feature at all can never be selected by any rule, so an
 * expression with fewer than two such arguments cannot be rewritten;</li>
 * <li>a pair of arguments whose two features never occur together in one rule can never fire, so
 * the pair does not have to be looked up.</li>
 * </ul>
 *
 * <p>
 * The filter is only sound while every rule tests both hash values. A rule with a bare pattern
 * left-hand-side is tried on every pair without any hash test, so a map containing one gets no
 * index at all.
 *
 * <p>
 * Immutable and shareable; has to be discarded whenever a rule is added to the map.
 */
public final class OrderlessPairIndex {

  /** Largest number of features which fits the {@code long} masks. */
  private static final int MAX_FEATURES = 64;

  /** Open addressed hash value table; a slot is free while {@link #ids} is <code>-1</code>. */
  private final int[] keys;

  private final int[] ids;

  private final int mask;

  /** <code>pairedWith[f]</code> - the features which occur with <code>f</code> in some rule. */
  private final long[] pairedWith;

  /**
   * The marker instance for a rule map which cannot be indexed. Its table is empty but valid, so
   * that a stray query answers "unknown" instead of failing.
   */
  public static OrderlessPairIndex noIndex() {
    return new OrderlessPairIndex(new int[1], new int[] {-1}, 0, new long[0]);
  }

  private OrderlessPairIndex(int[] keys, int[] ids, int mask, long[] pairedWith) {
    this.keys = keys;
    this.ids = ids;
    this.mask = mask;
    this.pairedWith = pairedWith;
  }

  /**
   * Build an index for a rule map.
   *
   * @return the index, or <code>null</code> if the map cannot be indexed and every pair has to be
   *         looked up
   */
  public static OrderlessPairIndex build(OpenIntToList<AbstractHashedPatternRules> map) {
    if (map == null || map.isEmpty()) {
      return null;
    }
    int[] featureHash = new int[MAX_FEATURES];
    int featureCount = 0;
    long[] pairedWith = new long[MAX_FEATURES];
    OpenIntToList<AbstractHashedPatternRules>.Iterator iterator = map.iterator();
    while (iterator.hasNext()) {
      iterator.advance();
      List<AbstractHashedPatternRules> rules = iterator.value();
      if (rules == null) {
        continue;
      }
      for (int i = 0; i < rules.size(); i++) {
        AbstractHashedPatternRules rule = rules.get(i);
        if (rule.isPattern1() || rule.isPattern2()) {
          // matched without any hash test - nothing can be excluded
          return null;
        }
        int id1 = featureId(featureHash, featureCount, rule.getHash1());
        if (id1 < 0) {
          if (featureCount == MAX_FEATURES) {
            return null;
          }
          featureHash[featureCount] = rule.getHash1();
          id1 = featureCount++;
        }
        int id2 = featureId(featureHash, featureCount, rule.getHash2());
        if (id2 < 0) {
          if (featureCount == MAX_FEATURES) {
            return null;
          }
          featureHash[featureCount] = rule.getHash2();
          id2 = featureCount++;
        }
        pairedWith[id1] |= 1L << id2;
        pairedWith[id2] |= 1L << id1;
      }
    }
    if (featureCount == 0) {
      return null;
    }
    int capacity = Integer.highestOneBit(Math.max(4, featureCount * 4 - 1)) << 1;
    int[] keys = new int[capacity];
    int[] ids = new int[capacity];
    for (int i = 0; i < capacity; i++) {
      ids[i] = -1;
    }
    final int mask = capacity - 1;
    for (int f = 0; f < featureCount; f++) {
      int slot = spread(featureHash[f]) & mask;
      while (ids[slot] >= 0) {
        slot = (slot + 1) & mask;
      }
      keys[slot] = featureHash[f];
      ids[slot] = f;
    }
    long[] paired = new long[featureCount];
    System.arraycopy(pairedWith, 0, paired, 0, featureCount);
    return new OrderlessPairIndex(keys, ids, mask, paired);
  }

  private static int featureId(int[] featureHash, int featureCount, int hash) {
    for (int f = 0; f < featureCount; f++) {
      if (featureHash[f] == hash) {
        return f;
      }
    }
    return -1;
  }

  private static int spread(int hash) {
    int h = hash * 0x9E3779B9;
    return (h ^ (h >>> 16)) & 0x7FFFFFFF;
  }


  /**
   * @return the feature of the hash value, or <code>-1</code> if no rule uses it
   */
  public int featureId(int hashValue) {
    int slot = spread(hashValue) & mask;
    while (true) {
      int id = ids[slot];
      if (id < 0) {
        return -1;
      }
      if (keys[slot] == hashValue) {
        return id;
      }
      slot = (slot + 1) & mask;
    }
  }

  /** The features which occur together with <code>feature</code> in some rule. */
  public long pairedWith(int feature) {
    return pairedWith[feature];
  }

  /** @return <code>true</code> if some rule pairs the two features */
  public boolean pairPossible(int f1, int f2) {
    return f1 >= 0 && f2 >= 0 && (pairedWith[f1] & (1L << f2)) != 0L;
  }
}
