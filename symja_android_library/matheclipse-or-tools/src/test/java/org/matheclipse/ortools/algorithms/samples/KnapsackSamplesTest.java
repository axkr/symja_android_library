package org.matheclipse.ortools.algorithms.samples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.matheclipse.ortools.testutil.OutputCapture.assertContains;
import static org.matheclipse.ortools.testutil.OutputCapture.assertFind;

import java.util.regex.Matcher;
import org.junit.jupiter.api.Test;
import org.matheclipse.ortools.testutil.OutputCapture;

/** Runs the knapsack sample and checks its result against an independent dynamic program. */
public class KnapsackSamplesTest {

  private static final long[] VALUES = {360, 83, 59, 130, 431, 67, 230, 52, 93, 125, 670, 892, 600,
      38, 48, 147, 78, 256, 63, 17, 120, 164, 432, 35, 92, 110, 22, 42, 50, 323, 514, 28, 87, 73,
      78, 15, 26, 78, 210, 36, 85, 189, 274, 43, 33, 10, 19, 389, 276, 312};
  private static final long[] WEIGHTS = {7, 0, 30, 22, 80, 94, 11, 81, 70, 64, 59, 18, 0, 36, 3, 8,
      15, 42, 9, 0, 42, 47, 52, 32, 26, 48, 55, 6, 29, 84, 2, 4, 18, 56, 7, 29, 93, 44, 71, 3, 86,
      66, 31, 65, 0, 79, 20, 65, 52, 13};
  private static final int CAPACITY = 850;

  // The classical 0-1 knapsack table, best[c] = best value with weight at most c.
  private static long optimum() {
    long[] best = new long[CAPACITY + 1];
    for (int i = 0; i < VALUES.length; ++i) {
      for (int c = CAPACITY; c >= WEIGHTS[i]; --c) {
        best[c] = Math.max(best[c], best[c - (int) WEIGHTS[i]] + VALUES[i]);
      }
    }
    return best[CAPACITY];
  }

  @Test
  public void testKnapsack() {
    String out = OutputCapture.run(() -> Knapsack.main(new String[0]));
    assertContains(out, "Total value = 7534");
    assertContains(out, "Total weight: 850");
    assertEquals(7534, optimum());

    // The packed items must have the printed total value and weight.
    Matcher items = assertFind(out, "^Packed items: \\[([0-9, ]*)\\]$");
    long value = 0;
    long weight = 0;
    for (String s : items.group(1).split(",\\s*")) {
      int item = Integer.parseInt(s.trim());
      value += VALUES[item];
      weight += WEIGHTS[item];
    }
    assertEquals(7534, value);
    assertEquals(850, weight);
    assertFind(out, "^Packed weights: \\[[0-9, ]*\\]$");
  }
}
