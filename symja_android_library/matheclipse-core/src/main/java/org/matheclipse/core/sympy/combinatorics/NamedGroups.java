package org.matheclipse.core.sympy.combinatorics;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.sympy.exception.ValueError;

/**
 * Generators of well known permutation groups. Ported from
 * <a href="https://github.com/sympy/sympy/blob/master/sympy/combinatorics/named_groups.py">sympy/combinatorics/named_groups.py</a>
 *
 * <p>
 * All permutations are in the 0-based array form of {@link Permutations}.
 */
public class NamedGroups {

  private NamedGroups() {}

  private static int[] rotation(int n) {
    // a = list(range(1, n))
    // a.append(0)
    int[] a = new int[n];
    for (int i = 0; i < n; i++) {
      a[i] = (i + 1) % n;
    }
    return a;
  }

  /**
   * Generates the symmetric group on <code>n</code> elements as a permutation group.
   *
   * <p>
   * The generators taken are the <code>n</code>-cycle <code>(0 1 2 ... n-1)</code> and the
   * transposition <code>(0 1)</code>.
   */
  public static PermGroups symmetricGroup(int n) {
    // >>> G = SymmetricGroup(4)
    // >>> G.order()
    // 24
    if (n < 1) {
      throw new ValueError("Positive degree expected.");
    }
    List<int[]> gens = new ArrayList<int[]>(2);
    if (n == 1) {
      gens.add(new int[] {0});
    } else if (n == 2) {
      gens.add(new int[] {1, 0});
    } else {
      gens.add(rotation(n));
      int[] b = Permutations.identity(n);
      b[0] = 1;
      b[1] = 0;
      gens.add(b);
    }
    return new PermGroups(gens, n);
  }

  /**
   * Generates the cyclic group of order <code>n</code> as a permutation group.
   *
   * <p>
   * The generator taken is the <code>n</code>-cycle <code>(0 1 2 ... n-1)</code>
   */
  public static PermGroups cyclicGroup(int n) {
    // >>> G = CyclicGroup(6)
    // >>> G.order()
    // 6
    if (n < 1) {
      throw new ValueError("Positive degree expected.");
    }
    List<int[]> gens = new ArrayList<int[]>(1);
    gens.add(rotation(n));
    return new PermGroups(gens, n);
  }

  /**
   * Generates the dihedral group <code>D_n</code> as a permutation group.
   *
   * <p>
   * The dihedral group <code>D_n</code> is the group of symmetries of the regular
   * <code>n</code>-gon. The generators taken are the <code>n</code>-cycle <code>a =
   * (0 1 2 ... n-1)</code> (a rotation of the <code>n</code>-gon) and <code>b = (0 n-1)(1 n-2)...
   * </code> (a reflection of the <code>n</code>-gon).
   */
  public static PermGroups dihedralGroup(int n) {
    // >>> G = DihedralGroup(5)
    // >>> G.order()
    // 10
    if (n < 1) {
      throw new ValueError("Positive degree expected.");
    }
    List<int[]> gens = new ArrayList<int[]>(3);
    // small cases are special
    if (n == 1) {
      gens.add(new int[] {1, 0});
      return new PermGroups(gens, 2);
    }
    if (n == 2) {
      gens.add(new int[] {1, 0, 3, 2});
      gens.add(new int[] {2, 3, 0, 1});
      gens.add(new int[] {3, 2, 1, 0});
      return new PermGroups(gens, 4);
    }
    gens.add(rotation(n));
    // a = list(range(n))
    // a.reverse()
    int[] b = new int[n];
    for (int i = 0; i < n; i++) {
      b[i] = n - 1 - i;
    }
    gens.add(b);
    return new PermGroups(gens, n);
  }

  /**
   * Generates the alternating group on <code>n</code> elements as a permutation group.
   *
   * <p>
   * For <code>n &gt; 2</code>, the generators taken are <code>(0 1 2), (0 1 2 ... n-1)</code> for
   * <code>n</code> odd and <code>(0 1 2), (1 2 ... n-1)</code> for <code>n</code> even.
   */
  public static PermGroups alternatingGroup(int n) {
    // >>> G = AlternatingGroup(4)
    // >>> G.order()
    // 12
    if (n < 1) {
      throw new ValueError("Positive degree expected.");
    }
    List<int[]> gens = new ArrayList<int[]>(2);
    // small cases are special
    if (n == 1 || n == 2) {
      gens.add(Permutations.identity(n));
      return new PermGroups(gens, n);
    }
    // a = list(range(n))
    // a[0], a[1], a[2] = a[1], a[2], a[0]
    int[] gen1 = Permutations.identity(n);
    gen1[0] = 1;
    gen1[1] = 2;
    gen1[2] = 0;
    gens.add(gen1);
    int[] gen2;
    if ((n & 1) == 1) {
      // a = list(range(1, n))
      // a.append(0)
      gen2 = rotation(n);
    } else {
      // a = list(range(2, n))
      // a.append(1)
      // a.insert(0, 0)
      gen2 = new int[n];
      gen2[0] = 0;
      for (int i = 1; i < n - 1; i++) {
        gen2[i] = i + 1;
      }
      gen2[n - 1] = 1;
    }
    if (!java.util.Arrays.equals(gen1, gen2)) {
      gens.add(gen2);
    }
    return new PermGroups(gens, n);
  }

  /**
   * Returns the direct product of cyclic groups with the given orders.
   */
  public static PermGroups abelianGroup(int... cyclicOrders) {
    // >>> AbelianGroup(3, 4)
    // PermutationGroup([
    // (6)(0 1 2),
    // (3 4 5 6)])
    int degree = 0;
    for (int i = 0; i < cyclicOrders.length; i++) {
      if (cyclicOrders[i] < 1) {
        throw new ValueError("Positive order expected.");
      }
      degree += cyclicOrders[i];
    }
    List<int[]> gens = new ArrayList<int[]>(cyclicOrders.length);
    int offset = 0;
    for (int i = 0; i < cyclicOrders.length; i++) {
      int[] gen = Permutations.identity(degree);
      final int size = cyclicOrders[i];
      for (int k = 0; k < size; k++) {
        gen[offset + k] = offset + (k + 1) % size;
      }
      gens.add(gen);
      offset += size;
    }
    if (gens.isEmpty()) {
      gens.add(new int[0]);
    }
    return new PermGroups(gens, degree);
  }
}
