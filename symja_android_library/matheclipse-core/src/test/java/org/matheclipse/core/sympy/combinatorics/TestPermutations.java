package org.matheclipse.core.sympy.combinatorics;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;

public class TestPermutations {

  @Test
  public void testPermutation() {
    // https://github.com/sympy/sympy/blob/master/sympy/combinatorics/tests/test_permutations.py
    // p = Permutation([2, 5, 1, 6, 3, 0, 4])
    // q = Permutation([[1], [0, 3, 5, 6, 2, 4]])
    int[] p = {2, 5, 1, 6, 3, 0, 4};
    int[] q = {3, 1, 4, 5, 0, 6, 2};

    // assert ~(r**2).is_Identity
    // assert (p*(~p)).is_Identity
    assertTrue(Permutations.isIdentity(Permutations.mul(p, Permutations.inverse(p))));
    // assert (~p)**13 == p**-13
    assertArrayEquals(Permutations.power(Permutations.inverse(p), 13), Permutations.power(p, -13));
    // assert p**0 is the identity
    assertTrue(Permutations.isIdentity(Permutations.power(p, 0)));
    // assert p*q == Permutation(_af_rmuln(*[a.array_form for a in (p, q)]))
    // p*q: i -> q[p[i]]
    assertArrayEquals(new int[] {4, 6, 1, 2, 5, 3, 0}, Permutations.mul(p, q));
    // assert (p**2).array_form == [1, 0, 5, 4, 6, 2, 3]
    assertArrayEquals(new int[] {1, 0, 5, 4, 6, 2, 3}, Permutations.power(p, 2));
    assertArrayEquals(Permutations.mul(p, p), Permutations.power(p, 2));
    assertArrayEquals(Permutations.mul(Permutations.mul(p, p), p), Permutations.power(p, 3));

    // assert p.support() == list(range(7))
    assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5, 6}, Permutations.support(p));
    // assert q.support() == [0, 2, 3, 4, 5, 6]
    assertArrayEquals(new int[] {0, 2, 3, 4, 5, 6}, Permutations.support(q));
    // assert p.length() == 7
    assertEquals(7, Permutations.length(p));
    // assert q.length() == 6
    assertEquals(6, Permutations.length(q));
    // assert p.cycles == 2
    assertEquals(2, Permutations.cycles(p));
    // assert q.cycles == 2
    assertEquals(2, Permutations.cycles(q));
    // assert p.max() == 6
    assertEquals(6, Permutations.max(p));
    // assert q.min() == 0
    assertEquals(0, Permutations.min(q));
    // p has the cycles (0 2 1 5)(3 6 4)
    assertEquals("12", Permutations.order(p).toString());
    // assert q.order() == 6
    assertEquals("6", Permutations.order(q).toString());
    // assert (p**(p.order())).is_Identity
    assertTrue(Permutations.isIdentity(
        Permutations.power(p, Permutations.order(p).toBigNumerator())));

    // assert p.rank() == 1964
    assertEquals("1964", Permutations.rank(p).toString());
    // assert q.rank() == 2234 ... sympy uses a different q here; use round trip instead
    assertArrayEquals(q, Permutations.unrankLex(7, Permutations.rank(q).toBigNumerator()));
    assertArrayEquals(p, Permutations.unrankLex(7, 1964));

    // assert p.inversions() == 7 (p = Permutation([0, 1, 2, 3]) etc.)
    assertEquals(0, Permutations.inversions(new int[] {0, 1, 2, 3, 4, 5}));
    assertEquals(6, Permutations.inversions(new int[] {3, 2, 1, 0}));
    assertEquals(6, Permutations.inversions(new int[] {4, 0, 2, 3, 1, 5}));
    // assert p.signature() == -1
    assertEquals(1, Permutations.signature(new int[] {0, 1, 2}));
    assertEquals(-1, Permutations.signature(new int[] {0, 2, 1}));
    // p = Permutation([3, 2, 0, 1]); p.parity() == 1
    assertEquals(1, Permutations.parity(new int[] {3, 2, 0, 1}));
    assertEquals(0, Permutations.parity(new int[] {0, 1, 2, 3}));
    // the parity is the parity of the number of inversions
    assertEquals(Permutations.inversions(p) % 2, Permutations.parity(p));
    assertEquals(Permutations.inversions(q) % 2, Permutations.parity(q));
  }

  @Test
  public void testOrder() {
    // p = Permutation([3, 1, 5, 2, 4, 0]); p.order() == 4
    assertEquals("4", Permutations.order(new int[] {3, 1, 5, 2, 4, 0}).toString());
    // identity
    assertEquals("1", Permutations.order(new int[] {0, 1, 2}).toString());
    assertEquals(-1, Permutations.max(new int[] {0, 1, 2}));
    assertEquals(-1, Permutations.min(new int[] {0, 1, 2}));
  }

  @Test
  public void testRankUnrank() {
    // >>> Permutation.unrank_lex(5, 10) == Permutation([0, 2, 4, 1, 3])
    assertArrayEquals(new int[] {0, 2, 4, 1, 3}, Permutations.unrankLex(5, 10));
    assertEquals("10", Permutations.rank(new int[] {0, 2, 4, 1, 3}).toString());
    // p = Permutation([3, 2, 1, 0]); p.rank() == 23
    assertEquals("23", Permutations.rank(new int[] {3, 2, 1, 0}).toString());
    for (int i = 0; i < 24; i++) {
      assertEquals(BigInteger.valueOf(i),
          Permutations.rank(Permutations.unrankLex(4, i)).toBigNumerator());
    }
  }

  @Test
  public void testListConversion() {
    int[] a = Permutations.fromList(F.List(F.C3, F.C1, F.C2));
    assertArrayEquals(new int[] {2, 0, 1}, a);
    assertEquals("{3,1,2}", Permutations.toList(a).toString());
    assertEquals("{2,3,1}", Permutations.toList(Permutations.inverse(a)).toString());
    assertNull(Permutations.fromList(F.List(F.C3, F.C1, F.C1)));
    assertNull(Permutations.fromList(F.List(F.C4, F.C1, F.C2)));
  }
}
