package org.matheclipse.core.numbertheory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import java.util.BitSet;
import org.junit.jupiter.api.Test;

/** Tests for the pair table of {@link IntegerSplitter.Stage2Plan}. */
public class Stage2PlanTest {

  private static final int D = 2310;

  @Test
  public void testPairsCoverExactlyThePrimes() {
    for (int b1 : new int[] {2000, 11000}) {
      long b2 = 100L * b1;
      IntegerSplitter.Stage2Plan plan = IntegerSplitter.stage2Plan(b1);
      int giants = plan.kLast - plan.kFirst + 1;
      assertEquals(giants + 1, plan.pairStart.length, "B1=" + b1);
      assertEquals(plan.pairBabies.length, plan.pairStart[giants], "B1=" + b1);

      BitSet covered = new BitSet();
      for (int g = 0; g < giants; g++) {
        long kd = (long) (plan.kFirst + g) * D;
        int previous = -1;
        for (int p = plan.pairStart[g]; p < plan.pairStart[g + 1]; p++) {
          int index = plan.pairBabies[p] & 0xFF;
          assertTrue(index > previous, "baby indices ascend in giant " + g);
          previous = index;
          int j = plan.babies[index];
          boolean anyPrime = false;
          for (long candidate : new long[] {kd - j, kd + j}) {
            if (candidate > b1 && candidate <= b2
                && BigInteger.valueOf(candidate).isProbablePrime(30)) {
              covered.set((int) candidate);
              anyPrime = true;
            }
          }
          assertTrue(anyPrime, "pair (" + (plan.kFirst + g) + ", " + j + ") covers no prime");
        }
      }
      for (long q = b1 + 1; q <= b2; q++) {
        if (BigInteger.valueOf(q).isProbablePrime(30)) {
          assertTrue(covered.get((int) q), "prime " + q + " isn't covered, B1=" + b1);
        }
      }
    }
  }
}
