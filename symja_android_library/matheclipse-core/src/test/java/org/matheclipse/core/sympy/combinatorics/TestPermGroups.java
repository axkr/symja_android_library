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
}
