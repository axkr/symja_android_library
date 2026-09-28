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

  /** Create the array form from cycles, like <code>Permutation(cycles, size=size)</code> */
  private static int[] cycles(int size, int[]... cycles) {
    int[] a = Permutations.identity(size);
    for (int[] cycle : cycles) {
      for (int j = 0; j < cycle.length; j++) {
        a[cycle[j]] = cycle[(j + 1) % cycle.length];
      }
    }
    return a;
  }

  @Test
  public void testPermutationSympy() {
    // https://github.com/sympy/sympy/blob/master/sympy/combinatorics/tests/test_permutations.py
    // Only the asserts for the ported methods: mul (p*q applies p first), rmul(p, q) == q*p,
    // inverse, power, order, support, length, max, min, cycles, parity, signature, inversions,
    // rank and unrank_lex.
    int[] p = {2, 5, 1, 6, 3, 0, 4};
    // q = Permutation([[1], [0, 3, 5, 6, 2, 4]])
    int[] q = cycles(7, new int[] {0, 3, 5, 6, 2, 4});
    // assert q.array_form == [3, 1, 4, 5, 0, 6, 2]
    assertArrayEquals(new int[] {3, 1, 4, 5, 0, 6, 2}, q);
    // assert p.support() == list(range(7))
    assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5, 6}, Permutations.support(p));
    // assert q.support() == [0, 2, 3, 4, 5, 6]
    assertArrayEquals(new int[] {0, 2, 3, 4, 5, 6}, Permutations.support(q));
    // assert q.cycles == 2
    assertEquals(2, Permutations.cycles(q));
    // assert rmul(q, p) == Permutation([4, 6, 1, 2, 5, 3, 0])
    assertArrayEquals(new int[] {4, 6, 1, 2, 5, 3, 0}, Permutations.mul(p, q));
    // assert rmul(p, q) == Permutation([6, 5, 3, 0, 2, 4, 1])
    assertArrayEquals(new int[] {6, 5, 3, 0, 2, 4, 1}, Permutations.mul(q, p));
    // assert rmul(Permutation([[1, 2, 3], [0, 4]]),
    // Permutation([[1, 2, 4], [0], [3]])).cyclic_form == [[0, 4, 2], [1, 3]]
    assertArrayEquals(cycles(5, new int[] {0, 4, 2}, new int[] {1, 3}), Permutations.mul(
        cycles(5, new int[] {1, 2, 4}), cycles(5, new int[] {1, 2, 3}, new int[] {0, 4})));
    // p3 = Permutation([0, 3, 1, 2])
    // assert p3.order() == 3
    assertEquals("3", Permutations.order(new int[] {0, 3, 1, 2}).toString());

    // assert p**13 == p
    assertArrayEquals(p, Permutations.power(p, 13));
    // assert q**0 == Permutation(list(range(q.size)))
    assertArrayEquals(Permutations.identity(7), Permutations.power(q, 0));
    // assert q**-2 == ~q**2
    assertArrayEquals(Permutations.power(Permutations.inverse(q), 2), Permutations.power(q, -2));
    // assert q**2 == Permutation([5, 1, 0, 6, 3, 2, 4])
    assertArrayEquals(new int[] {5, 1, 0, 6, 3, 2, 4}, Permutations.power(q, 2));
    // assert q**3 == q**2*q
    assertArrayEquals(Permutations.mul(Permutations.power(q, 2), q), Permutations.power(q, 3));
    // assert q**4 == q**2*q**2
    assertArrayEquals(Permutations.mul(Permutations.power(q, 2), Permutations.power(q, 2)),
        Permutations.power(q, 4));

    // a = Permutation(1, 3)
    // b = Permutation(2, 0, 3)
    // I = Permutation(3)
    int[] a = cycles(4, new int[] {1, 3});
    int[] b = cycles(4, new int[] {2, 0, 3});
    // assert ~a == a**-1
    assertArrayEquals(Permutations.power(a, -1), Permutations.inverse(a));
    // assert a*~a == I
    assertTrue(Permutations.isIdentity(Permutations.mul(a, Permutations.inverse(a))));
    // assert a*b**-1 == a*~b
    assertArrayEquals(Permutations.mul(a, Permutations.inverse(b)),
        Permutations.mul(a, Permutations.power(b, -1)));

    // ans = Permutation(0, 5, 3, 1, 6)(2, 4)
    // assert (p + q.rank()).rank() == ans.rank()
    BigInteger n7 = BigInteger.valueOf(5040);
    int[] ans = cycles(7, new int[] {0, 5, 3, 1, 6}, new int[] {2, 4});
    BigInteger pRank = Permutations.rank(p).toBigNumerator();
    BigInteger qRank = Permutations.rank(q).toBigNumerator();
    assertEquals(Permutations.rank(ans).toBigNumerator(), pRank.add(qRank).mod(n7));
    // assert (p - q.rank()).rank() == Permutation(0, 6, 3, 1, 2, 5, 4).rank()
    assertEquals(Permutations.rank(cycles(7, new int[] {0, 6, 3, 1, 2, 5, 4})).toBigNumerator(),
        pRank.subtract(qRank).mod(n7));
    // assert (q - p.rank()).rank() == Permutation(1, 4, 6, 2)(3, 5).rank()
    assertEquals(
        Permutations.rank(cycles(7, new int[] {1, 4, 6, 2}, new int[] {3, 5})).toBigNumerator(),
        qRank.subtract(pRank).mod(n7));

    // assert p*Permutation([]) == p
    assertArrayEquals(p, Permutations.mul(p, new int[0]));
    // assert Permutation([])*p == p
    assertArrayEquals(p, Permutations.mul(new int[0], p));
    // assert p*Permutation([[0, 1]]) == Permutation([2, 5, 0, 6, 3, 1, 4])
    assertArrayEquals(new int[] {2, 5, 0, 6, 3, 1, 4}, Permutations.mul(p, new int[] {1, 0}));
    // assert Permutation([[0, 1]])*p == Permutation([5, 2, 1, 6, 3, 0, 4])
    assertArrayEquals(new int[] {5, 2, 1, 6, 3, 0, 4}, Permutations.mul(new int[] {1, 0}, p));

    // pq = p ^ q
    // assert pq == Permutation([5, 6, 0, 4, 1, 2, 3])
    // assert pq == rmul(q, p, ~q)
    assertArrayEquals(new int[] {5, 6, 0, 4, 1, 2, 3},
        Permutations.mul(Permutations.mul(Permutations.inverse(q), p), q));
    // qp = q ^ p
    // assert qp == Permutation([4, 3, 6, 2, 1, 5, 0])
    assertArrayEquals(new int[] {4, 3, 6, 2, 1, 5, 0},
        Permutations.mul(Permutations.mul(Permutations.inverse(p), q), p));

    // assert Permutation(list(range(500, -1, -1))).inversions() == 125250
    int[] reversed = new int[501];
    for (int i = 0; i <= 500; i++) {
      reversed[i] = 500 - i;
    }
    assertEquals(125250L, Permutations.inversions(reversed));

    // s = Permutation([0, 4, 1, 3, 2])
    // assert s.parity() == 0
    assertEquals(0, Permutations.parity(new int[] {0, 4, 1, 3, 2}));
    // assert Permutation([0, 1, 4, 3, 2]).parity() == 1
    assertEquals(1, Permutations.parity(new int[] {0, 1, 4, 3, 2}));

    // r = Permutation([3, 2, 1, 0])
    // assert (r**2).is_Identity
    int[] r = {3, 2, 1, 0};
    assertTrue(Permutations.isIdentity(Permutations.power(r, 2)));
    // assert rmul(~p, p).is_Identity
    assertTrue(Permutations.isIdentity(Permutations.mul(p, Permutations.inverse(p))));
    // assert (~p)**13 == Permutation([5, 2, 0, 4, 6, 1, 3])
    assertArrayEquals(new int[] {5, 2, 0, 4, 6, 1, 3},
        Permutations.power(Permutations.inverse(p), 13));
    // assert p.max() == 6
    assertEquals(6, Permutations.max(p));
    // assert p.min() == 0
    assertEquals(0, Permutations.min(p));
    // q = Permutation([[6], [5], [0, 1, 2, 3, 4]])
    int[] q2 = cycles(7, new int[] {0, 1, 2, 3, 4});
    // assert q.max() == 4
    assertEquals(4, Permutations.max(q2));
    // assert q.min() == 0
    assertEquals(0, Permutations.min(q2));

    // p = Permutation([1, 5, 2, 0, 3, 6, 4])
    // q = Permutation([[1, 2, 3, 5, 6], [0, 4]])
    int[] p3 = {1, 5, 2, 0, 3, 6, 4};
    int[] q3 = cycles(7, new int[] {1, 2, 3, 5, 6}, new int[] {0, 4});
    // assert p.inversions() == 7
    assertEquals(7L, Permutations.inversions(p3));
    // # test the merge-sort with a longer permutation
    // big = list(p) + list(range(p.max() + 1, p.max() + 130))
    // assert Permutation(big).inversions() == 7
    int[] big = new int[7 + 129];
    System.arraycopy(p3, 0, big, 0, 7);
    for (int i = 7; i < big.length; i++) {
      big[i] = i;
    }
    assertEquals(7L, Permutations.inversions(big));
    // assert p.signature() == -1
    assertEquals(-1, Permutations.signature(p3));
    // assert q.inversions() == 11
    assertEquals(11L, Permutations.inversions(q3));
    // assert q.signature() == -1
    assertEquals(-1, Permutations.signature(q3));
    // assert rmul(p, ~p).inversions() == 0
    assertEquals(0L, Permutations.inversions(Permutations.mul(Permutations.inverse(p3), p3)));
    // assert rmul(p, ~p).signature() == 1
    assertEquals(1, Permutations.signature(Permutations.mul(Permutations.inverse(p3), p3)));
    // assert p.order() == 6
    assertEquals("6", Permutations.order(p3).toString());
    // assert q.order() == 10
    assertEquals("10", Permutations.order(q3).toString());
    // assert (p**(p.order())).is_Identity
    assertTrue(Permutations.isIdentity(Permutations.power(p3, 6)));
    // assert p.length() == 6
    assertEquals(6, Permutations.length(p3));
    // assert q.length() == 7
    assertEquals(7, Permutations.length(q3));
    // assert r.length() == 4
    assertEquals(4, Permutations.length(r));
  }

  @Test
  public void testRankingSympy() {
    // assert Permutation.unrank_lex(5, 10).rank() == 10
    assertEquals("10", Permutations.rank(Permutations.unrankLex(5, 10)).toString());
    // p = Permutation.unrank_lex(15, 225)
    // assert p.rank() == 225
    assertEquals("225", Permutations.rank(Permutations.unrankLex(15, 225)).toString());
    // assert Permutation.unrank_lex(10, 0).is_Identity
    assertTrue(Permutations.isIdentity(Permutations.unrankLex(10, 0)));
    // p = Permutation.unrank_lex(4, 23)
    // assert p.rank() == 23
    // assert p.array_form == [3, 2, 1, 0]
    assertArrayEquals(new int[] {3, 2, 1, 0}, Permutations.unrankLex(4, 23));
    // p = Permutation([2, 5, 1, 6, 3, 0, 4])
    // q = Permutation([[6], [5], [0, 1, 2, 3, 4]])
    // assert p.rank() == 1964
    assertEquals("1964", Permutations.rank(new int[] {2, 5, 1, 6, 3, 0, 4}).toString());
    // assert q.rank() == 870
    assertEquals("870", Permutations.rank(cycles(7, new int[] {0, 1, 2, 3, 4})).toString());
    // next_lex, the Trotter-Johnson and the non-lexicographic ranking aren't ported
  }

  @Test
  public void testMulSympy() {
    // a, b = [0, 2, 1, 3], [0, 1, 3, 2]
    // assert _af_rmul(a, b) == [0, 2, 3, 1]
    assertArrayEquals(new int[] {0, 2, 3, 1},
        Permutations.mul(new int[] {0, 1, 3, 2}, new int[] {0, 2, 1, 3}));
    // a = Permutation([0, 2, 1, 3])
    // b = (0, 1, 3, 2)
    // c = (3, 1, 2, 0)
    // assert Permutation.rmul(a, b, c) == Permutation([1, 2, 3, 0])
    int[] a = {0, 2, 1, 3};
    int[] b = {0, 1, 3, 2};
    int[] c = {3, 1, 2, 0};
    assertArrayEquals(new int[] {1, 2, 3, 0}, Permutations.mul(Permutations.mul(c, b), a));
    // assert Permutation.rmul(a, c) == Permutation([3, 2, 1, 0])
    assertArrayEquals(new int[] {3, 2, 1, 0}, Permutations.mul(c, a));
  }
}
