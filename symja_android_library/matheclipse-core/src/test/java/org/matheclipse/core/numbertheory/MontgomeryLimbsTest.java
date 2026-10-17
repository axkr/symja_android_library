package org.matheclipse.core.numbertheory;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** Tests for {@link MontgomeryLimbs} and the elliptic curve method of {@link IntegerSplitter}. */
public class MontgomeryLimbsTest {

  /** Odd moduli with 2 to 600 bits, including both ends of every limb count. */
  private static List<BigInteger> moduli(Random random) {
    List<BigInteger> moduli = new ArrayList<>();
    moduli.add(BigInteger.valueOf(3));
    for (int bits : new int[] {64, 127, 128, 192, 256, 320, 512}) {
      BigInteger power = BigInteger.ONE.shiftLeft(bits);
      moduli.add(power.subtract(BigInteger.ONE));
      moduli.add(power.add(BigInteger.ONE));
    }
    moduli.add(new BigInteger("230584300921369395189927877359334064089"));
    for (int i = 0; i < 200; i++) {
      int bits = 2 + random.nextInt(599);
      moduli.add(new BigInteger(bits, random).setBit(bits - 1).setBit(0));
    }
    return moduli;
  }

  private static BigInteger below(BigInteger n, Random random) {
    switch (random.nextInt(8)) {
      case 0:
        return BigInteger.ZERO;
      case 1:
        return n.subtract(BigInteger.ONE);
      default:
        BigInteger x;
        do {
          x = new BigInteger(n.bitLength(), random);
        } while (x.compareTo(n) >= 0);
        return x;
    }
  }

  /** The residue of a Montgomery form, computed independently of {@link MontgomeryLimbs}. */
  private static BigInteger residue(MontgomeryLimbs m, long[] a) {
    BigInteger r = BigInteger.ONE.shiftLeft(64 * m.k);
    return MontgomeryLimbs.toBigInteger(a).multiply(r.modInverse(m.modulus)).mod(m.modulus);
  }

  @Test
  public void testFits() {
    assertTrue(MontgomeryLimbs.fits(BigInteger.valueOf(3)));
    assertTrue(MontgomeryLimbs.fits(BigInteger.ONE.shiftLeft(1000).add(BigInteger.ONE)));
    assertTrue(!MontgomeryLimbs.fits(BigInteger.ONE));
    assertTrue(!MontgomeryLimbs.fits(BigInteger.ONE.shiftLeft(200)));
    assertEquals(2, new MontgomeryLimbs(BigInteger.valueOf(3)).k);
    assertEquals(2, new MontgomeryLimbs(BigInteger.ONE.shiftLeft(128).subtract(BigInteger.ONE)).k);
    assertEquals(3, new MontgomeryLimbs(BigInteger.ONE.shiftLeft(128).add(BigInteger.ONE)).k);
    assertEquals(5, new MontgomeryLimbs(BigInteger.ONE.shiftLeft(256).add(BigInteger.ONE)).k);
  }

  @Test
  public void testArithmeticAgainstBigInteger() {
    Random random = new Random(20261017L);
    for (BigInteger n : moduli(random)) {
      MontgomeryLimbs m = new MontgomeryLimbs(n);
      BigInteger r = BigInteger.ONE.shiftLeft(64 * m.k);
      BigInteger rInverse = r.mod(n).modInverse(n);
      long[] result = m.newElement();
      for (int i = 0; i < 100; i++) {
        BigInteger a = below(n, random);
        BigInteger b = below(n, random);
        long[] al = MontgomeryLimbs.toLimbs(a, m.k);
        long[] bl = MontgomeryLimbs.toLimbs(b, m.k);

        m.mul(al, bl, result);
        assertEquals(a.multiply(b).multiply(rInverse).mod(n), MontgomeryLimbs.toBigInteger(result),
            "mul " + a + "*" + b + " mod " + n);
        m.sqr(al, result);
        assertEquals(a.multiply(a).multiply(rInverse).mod(n), MontgomeryLimbs.toBigInteger(result),
            "sqr " + a + " mod " + n);
        m.add(al, bl, result);
        assertEquals(a.add(b).mod(n), MontgomeryLimbs.toBigInteger(result),
            "add " + a + "+" + b + " mod " + n);
        m.sub(al, bl, result);
        assertEquals(a.subtract(b).mod(n), MontgomeryLimbs.toBigInteger(result),
            "sub " + a + "-" + b + " mod " + n);

        // the result may be an operand
        long[] alias = al.clone();
        m.mul(alias, bl, alias);
        assertEquals(a.multiply(b).multiply(rInverse).mod(n), MontgomeryLimbs.toBigInteger(alias),
            "mul in place " + a + " mod " + n);
        alias = al.clone();
        m.sqr(alias, alias);
        assertEquals(a.multiply(a).multiply(rInverse).mod(n), MontgomeryLimbs.toBigInteger(alias),
            "sqr in place " + a + " mod " + n);
        alias = bl.clone();
        m.sub(al, alias, alias);
        assertEquals(a.subtract(b).mod(n), MontgomeryLimbs.toBigInteger(alias),
            "sub in place " + a + " mod " + n);

        m.toMontgomery(a, result);
        assertEquals(a.multiply(r).mod(n), MontgomeryLimbs.toBigInteger(result),
            "toMontgomery " + a + " mod " + n);
        assertEquals(a, residue(m, result), "residue " + a + " mod " + n);
        if (a.gcd(n).equals(BigInteger.ONE)) {
          long[] inverse = m.newElement();
          m.inverse(result, inverse);
          assertEquals(a.modInverse(n), residue(m, inverse), "inverse " + a + " mod " + n);
        }
      }
      assertEquals(r.mod(n), MontgomeryLimbs.toBigInteger(m.one), "one mod " + n);
    }
  }

  /** The unrolled 2, 3 and 4 limb methods agree with the loops. */
  @Test
  public void testUnrolledAgainstLoops() {
    Random random = new Random(20261018L);
    for (BigInteger n : moduli(random)) {
      MontgomeryLimbs m = new MontgomeryLimbs(n);
      if (m.k > 4) {
        continue;
      }
      long[] expected = m.newElement();
      long[] actual = m.newElement();
      for (int i = 0; i < 100; i++) {
        long[] a = MontgomeryLimbs.toLimbs(below(n, random), m.k);
        long[] b = MontgomeryLimbs.toLimbs(below(n, random), m.k);
        m.mulGeneric(a, b, expected);
        m.mul(a, b, actual);
        assertArrayEquals(expected, actual, "mul mod " + n);
        m.mulGeneric(a, a, expected);
        m.sqr(a, actual);
        assertArrayEquals(expected, actual, "sqr mod " + n);
        m.addGeneric(a, b, expected);
        m.add(a, b, actual);
        assertArrayEquals(expected, actual, "add mod " + n);
        m.subGeneric(a, b, expected);
        m.sub(a, b, actual);
        assertArrayEquals(expected, actual, "sub mod " + n);
      }
    }
  }

  @Test
  public void testBrentRhoFindsSmallFactors() {
    BigInteger big = BigInteger.ONE.shiftLeft(127).subtract(BigInteger.ONE);
    BigInteger[] smallFactors = {new BigInteger("10000000019"), new BigInteger("1000000007")};
    BigInteger[] cofactors = {new BigInteger("100000000000000000039"), big,
        big.multiply(BigInteger.ONE.shiftLeft(90).nextProbablePrime()),
        big.multiply(big.add(BigInteger.valueOf(20)))};
    for (BigInteger p : smallFactors) {
      for (BigInteger q : cofactors) {
        BigInteger n = p.multiply(q);
        BigInteger d = IntegerSplitter.brentRho(n, BigInteger.ONE, 1L << 22);
        assertTrue(d.compareTo(BigInteger.ONE) > 0 && n.mod(d).signum() == 0, n + " -> " + d);
        assertTrue(d.compareTo(n) < 0, "no proper divisor of " + n);
      }
    }
  }

  /** <code>[k]P</code> with the textbook Montgomery ladder in <code>BigInteger</code> arithmetic. */
  private static BigInteger[] ladder(BigInteger x, BigInteger z, BigInteger a24, BigInteger k,
      BigInteger n) {
    BigInteger[] r0 = {x, z};
    BigInteger[] r1 = doubled(r0, a24, n);
    for (int i = k.bitLength() - 2; i >= 0; i--) {
      if (k.testBit(i)) {
        r0 = differentialAdd(r1, r0, x, z, n);
        r1 = doubled(r1, a24, n);
      } else {
        r1 = differentialAdd(r0, r1, x, z, n);
        r0 = doubled(r0, a24, n);
      }
    }
    return r0;
  }

  private static BigInteger[] doubled(BigInteger[] p, BigInteger a24, BigInteger n) {
    BigInteger s = p[0].add(p[1]).pow(2).mod(n);
    BigInteger d = p[0].subtract(p[1]).pow(2).mod(n);
    BigInteger t = s.subtract(d);
    return new BigInteger[] {s.multiply(d).mod(n), t.multiply(d.add(a24.multiply(t))).mod(n)};
  }

  private static BigInteger[] differentialAdd(BigInteger[] p, BigInteger[] q, BigInteger dx,
      BigInteger dz, BigInteger n) {
    BigInteger u = p[0].subtract(p[1]).multiply(q[0].add(q[1]));
    BigInteger v = p[0].add(p[1]).multiply(q[0].subtract(q[1]));
    return new BigInteger[] {dz.multiply(u.add(v).pow(2)).mod(n),
        dx.multiply(u.subtract(v).pow(2)).mod(n)};
  }

  /** The product of all prime powers up to <code>b1</code>. */
  static BigInteger stage1Exponent(int b1) {
    BigInteger e = BigInteger.ONE;
    for (int p = 2; p <= b1; p = BigInteger.valueOf(p).nextProbablePrime().intValueExact()) {
      long power = p;
      while (power * p <= b1) {
        power *= p;
      }
      e = e.multiply(BigInteger.valueOf(power));
    }
    return e;
  }

  /** Stage 1 with Lucas chains gives the same point as the ladder over the whole exponent. */
  @Test
  public void testStage1AgainstLadder() {
    byte[] program = IntegerSplitter.stage1Program(2000);
    BigInteger exponent = stage1Exponent(2000);
    BigInteger p = new BigInteger("1000000007");
    for (int bits : new int[] {60, 100, 180, 240, 330}) {
      BigInteger n = p.multiply(BigInteger.ONE.shiftLeft(bits).nextProbablePrime());
      MontgomeryLimbs m = new MontgomeryLimbs(n);
      for (long sigma = 6; sigma < 12; sigma++) {
        BigInteger[] start = IntegerSplitter.suyama(n, sigma);
        long[][] q = IntegerSplitter.stage1(new IntegerSplitter.CurveLimbs(m, start[2]), start,
            program);
        BigInteger[] expected = ladder(start[0], start[1], start[2], exponent, n);
        BigInteger x = residue(m, q[0]);
        BigInteger z = residue(m, q[1]);
        // the same projective point: X*Z' = X'*Z
        assertEquals(x.multiply(expected[1]).mod(n), expected[0].multiply(z).mod(n),
            "sigma=" + sigma + " " + bits + " bits");
      }
    }
  }

  /**
   * Stage 2 has to find the prime factor <code>p</code> whenever <code>[r]Q</code> is the point
   * at infinity modulo <code>p</code> for a prime <code>r</code> in <code>(B1, B2]</code>; the
   * multiplications by every such <code>r</code> are done one by one modulo <code>p</code>.
   */
  @Test
  public void testStage2AgainstEveryPrime() {
    final int b1 = 2000;
    final long b2 = 100L * b1;
    byte[] program = IntegerSplitter.stage1Program(b1);
    IntegerSplitter.Stage2Plan plan = IntegerSplitter.stage2Plan(b1);
    List<byte[]> chains = new ArrayList<>();
    for (int r = b1 + 1; r <= b2; r++) {
      if (BigInteger.valueOf(r).isProbablePrime(30)) {
        chains.add(LucasChains.program(r));
      }
    }
    BigInteger p = new BigInteger("1000000007");
    MontgomeryLimbs mp = new MontgomeryLimbs(p);
    int found = 0;
    for (int bits : new int[] {60, 180}) {
      BigInteger n = p.multiply(BigInteger.ONE.shiftLeft(bits).nextProbablePrime());
      MontgomeryLimbs m = new MontgomeryLimbs(n);
      for (long sigma = 6; sigma < 46; sigma++) {
        BigInteger[] start = IntegerSplitter.suyama(n, sigma);
        IntegerSplitter.CurveLimbs curve = new IntegerSplitter.CurveLimbs(m, start[2]);
        long[][] q = IntegerSplitter.stage1(curve, start, program);
        if (!m.gcd(q[1]).equals(BigInteger.ONE)) {
          continue; // stage 1 already found a factor
        }
        BigInteger g = IntegerSplitter.stage2(curve, q, plan);

        IntegerSplitter.CurveLimbs curveP = new IntegerSplitter.CurveLimbs(mp, start[2].mod(p));
        BigInteger qx = residue(m, q[0]).mod(p);
        BigInteger qz = residue(m, q[1]).mod(p);
        boolean expected = false;
        long[][] point = curveP.newPoint();
        for (byte[] chain : chains) {
          mp.toMontgomery(qx, point[0]);
          mp.toMontgomery(qz, point[1]);
          LucasChains.run(curveP, point, chain);
          if (mp.gcd(point[1]).equals(p)) {
            expected = true;
            break;
          }
        }
        if (expected) {
          found++;
          assertEquals(0, g.mod(p).signum(), "stage 2 missed p for sigma=" + sigma);
        }
      }
    }
    // the comparison is only meaningful if some curves have a stage 2 prime
    assertTrue(found > 0, "no curve has its order's last prime in (B1, B2]");
  }
}
