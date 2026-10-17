package org.matheclipse.core.numbertheory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

/** Tests for {@link LucasChains}. */
public class LucasChainsTest {

  /**
   * The multiples of a point as integers: <code>[k]P</code> is <code>k</code>. A differential
   * addition checks that it was given <code>P-Q</code> or <code>P+Q</code>, which is what a chain
   * has to keep track of.
   */
  private static final class IntegerCurve implements LucasChains.DifferentialCurve<BigInteger[]> {
    int additions;
    int doublings;

    @Override
    public BigInteger[] newPoint() {
      return new BigInteger[1];
    }

    @Override
    public void copy(BigInteger[] from, BigInteger[] to) {
      to[0] = from[0];
    }

    @Override
    public void dbl(BigInteger[] p, BigInteger[] out) {
      doublings++;
      out[0] = p[0].shiftLeft(1);
    }

    @Override
    public void add(BigInteger[] p, BigInteger[] q, BigInteger[] difference, BigInteger[] out) {
      additions++;
      // x-only points can't tell P from -P: the addition gives the other one of P+Q and P-Q
      BigInteger sum = p[0].add(q[0]);
      BigInteger difference0 = p[0].subtract(q[0]).abs();
      if (difference[0].abs().equals(difference0)) {
        out[0] = sum;
      } else {
        assertEquals(sum, difference[0].abs(), "difference of " + p[0] + " and " + q[0]);
        out[0] = difference0;
      }
    }
  }

  private static BigInteger run(IntegerCurve curve, byte[] program) {
    BigInteger[] point = {BigInteger.ONE};
    LucasChains.run(curve, point, program);
    return point[0];
  }

  @Test
  public void testEveryPrimeChain() {
    long chainCost = 0;
    long ladderCost = 0;
    for (int k = 3; k < 200_000; k = BigInteger.valueOf(k).nextProbablePrime().intValueExact()) {
      IntegerCurve curve = new IntegerCurve();
      assertEquals(BigInteger.valueOf(k), run(curve, LucasChains.program(k)), "chain for " + k);
      // an addition costs 6 multiplications, a doubling 5
      chainCost += 6L * curve.additions + 5L * curve.doublings;
      // the Montgomery ladder takes one addition and one doubling per bit after the first
      ladderCost += 11L * (32 - Integer.numberOfLeadingZeros(k) - 1);
    }
    assertTrue(chainCost < ladderCost, chainCost + " >= " + ladderCost);
  }

  @Test
  public void testPrimeTwo() {
    assertEquals(BigInteger.valueOf(2), run(new IntegerCurve(), LucasChains.program(2)));
  }

  /** The stage 1 program multiplies by the product of all prime powers up to B1. */
  @Test
  public void testStage1Program() {
    for (int b1 : new int[] {2000, 11000}) {
      assertEquals(MontgomeryLimbsTest.stage1Exponent(b1),
          run(new IntegerCurve(), IntegerSplitter.stage1Program(b1)), "B1=" + b1);
    }
  }
}
