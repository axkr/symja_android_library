package org.matheclipse.core.sympy.combinatorics;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestPermGroups {

  private static PermGroups group(int[]... gens) {
    return new PermGroups(Arrays.asList(gens));
  }

  private static String toString(List<int[]> list) {
    StringBuilder buf = new StringBuilder();
    for (int[] a : list) {
      buf.append(Arrays.toString(a));
    }
    return buf.toString();
  }

  @Test
  public void testOrder() {
    // https://github.com/sympy/sympy/blob/master/sympy/combinatorics/tests/test_perm_groups.py
    // a = Permutation([2, 0, 1, 3, 4, 5, 6, 7, 8, 9])
    // b = Permutation([2, 1, 3, 4, 5, 6, 7, 8, 9, 0])
    // g = PermutationGroup([a, b])
    // assert g.order() == 1814400
    PermGroups g = group(new int[] {2, 0, 1, 3, 4, 5, 6, 7, 8, 9},
        new int[] {2, 1, 3, 4, 5, 6, 7, 8, 9, 0});
    assertEquals("1814400", g.order().toString());
    // assert PermutationGroup().order() == 1
    assertEquals("1", group(new int[] {0, 1, 2}).order().toString());

    // a = Permutation([1, 0, 2]); G = PermutationGroup([a]); G.order() == 2
    assertEquals("2", group(new int[] {1, 0, 2}).order().toString());

    // Rubik's cube group restricted to 2x2x2 is too large for a test; use the Mathieu group M11
    // M11 = <(1,2,3,4,5,6,7,8,9,10,11), (3,7,11,8)(4,10,5,6)> has order 7920
    PermGroups m11 = group(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0},
        new int[] {0, 1, 6, 9, 5, 3, 10, 2, 8, 4, 7});
    assertEquals("7920", m11.order().toString());
    assertTrue(m11.isTransitive());
  }

  @Test
  public void testNamedGroups() {
    // https://github.com/sympy/sympy/blob/master/sympy/combinatorics/tests/test_named_groups.py
    // G = SymmetricGroup(5); elements = list(G.generate()); assert len(elements) == 120
    assertEquals("120", NamedGroups.symmetricGroup(5).order().toString());
    assertEquals(120, NamedGroups.symmetricGroup(5).elements(1000).size());
    assertEquals("1", NamedGroups.symmetricGroup(1).order().toString());
    assertEquals("2", NamedGroups.symmetricGroup(2).order().toString());
    assertEquals("2432902008176640000", NamedGroups.symmetricGroup(20).order().toString());
    // G = CyclicGroup(10); assert len(elements) == 10; assert G.is_abelian
    assertEquals("10", NamedGroups.cyclicGroup(10).order().toString());
    assertTrue(NamedGroups.cyclicGroup(10).isAbelian());
    // G = DihedralGroup(6); assert len(elements) == 12; assert G.is_transitive() is True
    assertEquals("12", NamedGroups.dihedralGroup(6).order().toString());
    assertTrue(NamedGroups.dihedralGroup(6).isTransitive());
    assertFalse(NamedGroups.dihedralGroup(6).isAbelian());
    // assert DihedralGroup(1).order() == 2; DihedralGroup(2).order() == 4
    assertEquals("2", NamedGroups.dihedralGroup(1).order().toString());
    assertEquals("4", NamedGroups.dihedralGroup(2).order().toString());
    // G = AlternatingGroup(5); assert len(elements) == 60
    assertEquals("60", NamedGroups.alternatingGroup(5).order().toString());
    assertEquals("360", NamedGroups.alternatingGroup(6).order().toString());
    assertEquals("1", NamedGroups.alternatingGroup(2).order().toString());
    assertEquals("3", NamedGroups.alternatingGroup(3).order().toString());
    for (int[] element : NamedGroups.alternatingGroup(5).elements(100)) {
      assertEquals(0, Permutations.parity(element));
    }
    // A = AbelianGroup(3, 3, 3); assert A.order() == 27; assert A.is_abelian is True
    PermGroups a = NamedGroups.abelianGroup(3, 3, 3);
    assertEquals("27", a.order().toString());
    assertTrue(a.isAbelian());
    assertEquals(9, a.degree());
    assertEquals("12", NamedGroups.abelianGroup(3, 4).order().toString());
  }

  @Test
  public void testOrbits() {
    // a = Permutation([2, 0, 1]); b = Permutation([2, 1, 0]); g = PermutationGroup([a, b])
    // assert g.orbit(0) == {0, 1, 2}; assert g.orbits() == [{0, 1, 2}]
    PermGroups g = group(new int[] {2, 0, 1}, new int[] {2, 1, 0});
    assertArrayEquals(new int[] {0, 1, 2}, g.orbit(0));
    assertEquals("[0, 1, 2]", toString(g.orbits()));
    assertTrue(g.isTransitive());
    // a = Permutation(1, 5)(2, 3)(4, 0, 6); b = Permutation(1, 5)(3, 4)(2, 6, 0)
    // G.orbits() == [{0, 2, 3, 4, 6}, {1, 5}]
    g = group(new int[] {6, 5, 3, 2, 0, 1, 4}, new int[] {2, 5, 6, 4, 3, 1, 0});
    assertEquals("[0, 2, 3, 4, 6][1, 5]", toString(g.orbits()));
    assertFalse(g.isTransitive());
    // a = Permutation([1, 2, 0, 4, 5, 6, 3]); G.orbit(0) == {0, 1, 2}
    g = group(new int[] {1, 2, 0, 4, 5, 6, 3});
    assertArrayEquals(new int[] {0, 1, 2}, g.orbit(0));
    assertArrayEquals(new int[] {3, 4, 5, 6}, g.orbit(4));
    assertEquals("12", g.order().toString());
  }

  @Test
  public void testContains() {
    // a = Permutation(1, 2); b = Permutation(2, 3, 1); G = PermutationGroup(a, b, degree=5)
    List<int[]> gens = new ArrayList<int[]>();
    gens.add(new int[] {0, 2, 1});
    gens.add(new int[] {0, 3, 1, 2});
    PermGroups g = new PermGroups(gens, 5);
    assertEquals("6", g.order().toString());
    // assert G.contains(G[0])
    assertTrue(g.contains(new int[] {0, 2, 1}));
    // elem = Permutation([[2, 3]], size=5); assert G.contains(elem)
    assertTrue(g.contains(new int[] {0, 1, 3, 2, 4}));
    // elem = Permutation(1, 2)(3, 4); assert not G.contains(elem)
    assertFalse(g.contains(new int[] {0, 2, 1, 4, 3}));
    assertFalse(g.contains(new int[] {1, 0}));
    assertTrue(g.contains(new int[] {0, 1, 2, 3, 4, 5, 6}));

    // A4 is a subgroup of S4
    assertTrue(NamedGroups.alternatingGroup(4).isSubgroup(NamedGroups.symmetricGroup(4)));
    assertFalse(NamedGroups.symmetricGroup(4).isSubgroup(NamedGroups.alternatingGroup(4)));
    for (int[] element : NamedGroups.symmetricGroup(4).elements(24)) {
      assertEquals(Permutations.parity(element) == 0,
          NamedGroups.alternatingGroup(4).contains(element));
    }
  }

  @Test
  public void testElements() {
    // p = PermutationGroup(Permutation(1, 3), Permutation(1, 2))
    // assert len(p.elements) == 6
    PermGroups p = group(new int[] {0, 3, 2, 1}, new int[] {0, 2, 1});
    List<int[]> elements = p.elements(10);
    assertEquals(
        "[0, 1, 2, 3][0, 1, 3, 2][0, 2, 1, 3][0, 2, 3, 1][0, 3, 1, 2][0, 3, 2, 1]",
        toString(elements));
    assertNull(NamedGroups.symmetricGroup(5).elements(119));
    // the elements are unique
    elements = NamedGroups.dihedralGroup(7).elements(100);
    assertEquals(14, elements.size());
    for (int i = 1; i < elements.size(); i++) {
      assertTrue(Arrays.compare(elements.get(i - 1), elements.get(i)) < 0);
    }
  }

  @Test
  public void testStabilizer() {
    // S = SymmetricGroup(2); H = S.stabilizer(0); assert H.generators == [Permutation(1)]
    PermGroups h = NamedGroups.symmetricGroup(2).stabilizer(0);
    assertEquals("1", h.order().toString());
    // G = DihedralGroup(6); G.stabilizer(5) == PermutationGroup([(5)(0 4)(1 3)])
    h = NamedGroups.dihedralGroup(6).stabilizer(5);
    assertEquals("2", h.order().toString());
    assertTrue(h.contains(new int[] {4, 3, 2, 1, 0, 5}));
    // the stabilizer of a point in S(n) is S(n-1)
    h = NamedGroups.symmetricGroup(6).stabilizer(2);
    assertEquals("120", h.order().toString());
    assertArrayEquals(new int[] {2}, h.orbit(2));
    assertArrayEquals(new int[] {0, 1, 3, 4, 5}, h.orbit(0));
  }

  /** sympy.combinatorics.generators.rubik_cube_generators() */
  private static final int[][] RUBIK_CUBE_GENERATORS = {
      {2, 4, 7, 1, 6, 0, 3, 5, 32, 33, 34, 11, 12, 13, 14, 15, 8, 9, 10, 19, 20, 21, 22, 23, 16, 17, 18, 27, 28, 29, 30, 31, 24, 25, 26, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47},
      {16, 1, 2, 19, 4, 21, 6, 7, 10, 12, 15, 9, 14, 8, 11, 13, 40, 17, 18, 43, 20, 45, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 5, 35, 3, 37, 38, 0, 39, 41, 42, 36, 44, 34, 46, 47},
      {0, 1, 2, 3, 4, 24, 27, 29, 8, 9, 7, 11, 6, 13, 14, 5, 18, 20, 23, 17, 22, 16, 19, 21, 42, 25, 26, 41, 28, 40, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 10, 12, 15, 43, 44, 45, 46, 47},
      {0, 1, 37, 3, 35, 5, 6, 32, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 2, 19, 4, 21, 22, 7, 26, 28, 31, 25, 30, 24, 27, 29, 47, 33, 34, 44, 36, 42, 38, 39, 40, 41, 18, 43, 20, 45, 46, 23},
      {13, 11, 8, 3, 4, 5, 6, 7, 45, 9, 10, 46, 12, 47, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 0, 27, 1, 29, 30, 2, 34, 36, 39, 33, 38, 32, 35, 37, 40, 41, 42, 43, 44, 31, 28, 26},
      {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 21, 22, 23, 16, 17, 18, 19, 20, 29, 30, 31, 24, 25, 26, 27, 28, 37, 38, 39, 32, 33, 34, 35, 36, 13, 14, 15, 42, 44, 47, 41, 46, 40, 43, 45},
  };

  @Test
  public void testNamedGroupsSympy() {
    // https://github.com/sympy/sympy/blob/master/sympy/combinatorics/tests/test_named_groups.py
    // G = SymmetricGroup(5)
    // assert (G.generators[0]).size == 5
    // assert len(elements) == 120
    // assert G.is_abelian is False
    // assert G.is_transitive() is True
    PermGroups g = NamedGroups.symmetricGroup(5);
    assertEquals(5, g.generators().get(0).length);
    assertEquals(120, g.elements(1000).size());
    assertFalse(g.isAbelian());
    assertTrue(g.isTransitive());
    // H = SymmetricGroup(1); assert H.order() == 1
    assertEquals("1", NamedGroups.symmetricGroup(1).order().toString());
    // L = SymmetricGroup(2); assert L.order() == 2
    assertEquals("2", NamedGroups.symmetricGroup(2).order().toString());

    // G = CyclicGroup(10)
    // assert len(elements) == 10
    // assert G.is_abelian is True
    g = NamedGroups.cyclicGroup(10);
    assertEquals(10, g.elements(1000).size());
    assertTrue(g.isAbelian());
    // H = CyclicGroup(1); assert H.order() == 1
    assertEquals("1", NamedGroups.cyclicGroup(1).order().toString());
    // L = CyclicGroup(2); assert L.order() == 2
    assertEquals("2", NamedGroups.cyclicGroup(2).order().toString());

    // G = DihedralGroup(6)
    // assert len(elements) == 12
    // assert G.is_transitive() is True
    // assert G.is_abelian is False
    g = NamedGroups.dihedralGroup(6);
    assertEquals(12, g.elements(1000).size());
    assertTrue(g.isTransitive());
    assertFalse(g.isAbelian());
    // H = DihedralGroup(1); assert H.order() == 2
    assertEquals("2", NamedGroups.dihedralGroup(1).order().toString());
    // L = DihedralGroup(2); assert L.order() == 4; assert L.is_abelian is True
    assertEquals("4", NamedGroups.dihedralGroup(2).order().toString());
    assertTrue(NamedGroups.dihedralGroup(2).isAbelian());

    // G = AlternatingGroup(5)
    // assert len(elements) == 60
    // assert [perm.is_even for perm in elements] == [True]*60
    g = NamedGroups.alternatingGroup(5);
    List<int[]> elements = g.elements(1000);
    assertEquals(60, elements.size());
    for (int[] element : elements) {
      assertEquals(0, Permutations.parity(element));
    }
    // H = AlternatingGroup(1); assert H.order() == 1
    assertEquals("1", NamedGroups.alternatingGroup(1).order().toString());
    // L = AlternatingGroup(2); assert L.order() == 1; assert L.degree == 2
    assertEquals("1", NamedGroups.alternatingGroup(2).order().toString());
    assertEquals(2, NamedGroups.alternatingGroup(2).degree());

    // A = AbelianGroup(3, 3, 3); assert A.order() == 27; assert A.is_abelian is True
    assertEquals("27", NamedGroups.abelianGroup(3, 3, 3).order().toString());
    assertTrue(NamedGroups.abelianGroup(3, 3, 3).isAbelian());
    // B = AbelianGroup(1); assert B.order() == 1; assert B.degree == 1
    assertEquals("1", NamedGroups.abelianGroup(1).order().toString());
    assertEquals(1, NamedGroups.abelianGroup(1).degree());
    // is_solvable, is_nilpotent, derived_subgroup, index, random and RubikGroup aren't ported
  }

  @Test
  public void testPermGroupsSympy() {
    // https://github.com/sympy/sympy/blob/master/sympy/combinatorics/tests/test_perm_groups.py
    // test_generate:
    // a = Permutation([1, 0])
    // g = list(PermutationGroup([a]).generate())
    // assert g == [Permutation([0, 1]), Permutation([1, 0])]
    assertEquals("[0, 1][1, 0]", toString(group(new int[] {1, 0}).elements(10)));
    // a = Permutation([2, 0, 1]); b = Permutation([2, 1, 0])
    // v1 == [[0, 1, 2], [0, 2, 1], [1, 0, 2], [1, 2, 0], [2, 0, 1], [2, 1, 0]]
    assertEquals("[0, 1, 2][0, 2, 1][1, 0, 2][1, 2, 0][2, 0, 1][2, 1, 0]",
        toString(group(new int[] {2, 0, 1}, new int[] {2, 1, 0}).elements(10)));
    // a = Permutation([2, 0, 1, 3, 4, 5]); b = Permutation([2, 1, 3, 4, 5, 0])
    // assert len(list(g)) == 360
    PermGroups g360 = group(new int[] {2, 0, 1, 3, 4, 5}, new int[] {2, 1, 3, 4, 5, 0});
    assertEquals(360, g360.elements(1000).size());

    // test_order:
    // assert PermutationGroup().order() == 1
    assertEquals("1", new PermGroups(new ArrayList<int[]>(), 0).order().toString());

    // test_stabilizer:
    // S = SymmetricGroup(2); H = S.stabilizer(0); assert H.generators == [Permutation(1)]
    PermGroups h = NamedGroups.symmetricGroup(2).stabilizer(0);
    assertEquals(1, h.generators().size());
    assertTrue(Permutations.isIdentity(h.generators().get(0)));
    // G = PermutationGroup([a, b]); G0 = G.stabilizer(0); assert G0.order() == 60
    assertEquals("60", g360.stabilizer(0).order().toString());
    // gens_cube = [[1, 3, 5, 7, 0, 2, 4, 6], [1, 3, 0, 2, 5, 7, 4, 6]]
    // G2 = G.stabilizer(2); assert G2.order() == 6
    PermGroups cube = group(new int[] {1, 3, 5, 7, 0, 2, 4, 6}, new int[] {1, 3, 0, 2, 5, 7, 4, 6});
    PermGroups g2 = cube.stabilizer(2);
    assertEquals("6", g2.order().toString());
    // G2_1 = G2.stabilizer(1)
    // v = list(G2_1.generate(af=True))
    // assert v == [[0, 1, 2, 3, 4, 5, 6, 7], [3, 1, 2, 0, 7, 5, 6, 4]]
    assertEquals("[0, 1, 2, 3, 4, 5, 6, 7][3, 1, 2, 0, 7, 5, 6, 4]",
        toString(g2.stabilizer(1).elements(10)));
    // gens = (...) 20 points; G2 = G.stabilizer(2); assert G2.order() == 181440
    PermGroups g20 = group(
        new int[] {1, 2, 0, 4, 5, 3, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19},
        new int[] {0, 1, 2, 3, 4, 5, 19, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16, 7, 17, 18},
        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 9, 18, 16, 11, 12, 13, 14, 15, 8, 17, 10, 19});
    assertEquals("181440", g20.stabilizer(2).order().toString());

    // test_coset_factor:
    // a = Permutation([0, 2, 1]); G = PermutationGroup([a]); c = Permutation([2, 1, 0])
    // assert not G.coset_factor(c)
    assertFalse(group(new int[] {0, 2, 1}).contains(new int[] {2, 1, 0}));
    // assert g.order() == 360
    assertEquals("360", g360.order().toString());
    // d = Permutation([1, 0, 2, 3, 4, 5]); assert not g.contains(d)
    assertFalse(g360.contains(new int[] {1, 0, 2, 3, 4, 5}));
    // assert Permutation(2) in G
    assertTrue(group(new int[] {0, 2, 1}).contains(new int[] {0, 1, 2}));
    // c = Permutation([1, 0, 2, 3, 5, 4]); assert g.contains(c)
    assertTrue(g360.contains(new int[] {1, 0, 2, 3, 5, 4}));

    // test_orbits:
    // a = Permutation(list(range(1, 100)) + [0]); G = PermutationGroup([a])
    // assert [min(o) for o in G.orbits()] == [0]
    int[] a100 = new int[100];
    for (int i = 0; i < 100; i++) {
      a100[i] = (i + 1) % 100;
    }
    assertEquals(1, group(a100).orbits().size());
    // G = PermutationGroup(rubik_cube_generators())
    // assert [min(o) for o in G.orbits()] == [0, 1]
    // assert not G.is_transitive()
    PermGroups rubik = group(RUBIK_CUBE_GENERATORS);
    List<int[]> orbits = rubik.orbits();
    assertEquals(2, orbits.size());
    assertEquals(0, orbits.get(0)[0]);
    assertEquals(1, orbits.get(1)[0]);
    assertFalse(rubik.isTransitive());
    // test_rubik: G = PermutationGroup(rubik_cube_generators())
    // assert G.order() == 43252003274489856000
    assertEquals("43252003274489856000", rubik.order().toString());

    // test_elements:
    // p = Permutation(2, 3)
    // assert set(PermutationGroup(p).elements) == {Permutation(3), Permutation(2, 3)}
    assertEquals("[0, 1, 2, 3][0, 1, 3, 2]", toString(group(new int[] {0, 1, 3, 2}).elements(10)));

    // test_pointwise_stabilizer:
    // S = SymmetricGroup(5)
    // for point in (2, 0, 3, 4, 1): stab = stab.stabilizer(point) ...
    PermGroups s5 = NamedGroups.symmetricGroup(5);
    PermGroups stab = s5;
    int[] expectedOrders = {24, 6, 2, 1, 1};
    int[] points = {2, 0, 3, 4, 1};
    for (int i = 0; i < points.length; i++) {
      stab = stab.stabilizer(points[i]);
      assertEquals(Integer.toString(expectedOrders[i]), stab.order().toString());
      assertTrue(stab.isSubgroup(s5));
    }
    // S = SymmetricGroup(3); assert [G.order() for G in S.basic_stabilizers] == [6, 2]
    assertEquals("2", NamedGroups.symmetricGroup(3).stabilizer(0).order().toString());

    // test_schreier_sims_incremental: _verify_bsgs isn't ported; the order and membership of
    // A7 with transformed generators and of C11 generated by gen**3 are checked instead
    PermGroups a7 = NamedGroups.alternatingGroup(7);
    assertEquals("2520", a7.order().toString());
    int[] gen = NamedGroups.cyclicGroup(11).generators().get(0);
    List<int[]> gens = new ArrayList<int[]>();
    gens.add(Permutations.power(gen, 3));
    assertEquals("11", new PermGroups(gens).order().toString());
  }
}
