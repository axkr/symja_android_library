package org.matheclipse.core.sympy.combinatorics;

import java.math.BigInteger;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.sympy.exception.ValueError;

/**
 * Functions for permutations in <i>array form</i>. Ported from
 * <a href="https://github.com/sympy/sympy/blob/master/sympy/combinatorics/permutations.py">sympy/combinatorics/permutations.py</a>
 *
 * <p>
 * As in sympy the array form is 0-based: the permutation maps <code>i</code> to <code>a[i]</code>.
 * Use {@link #fromList(IAST)} and {@link #toList(int[])} to convert from and to the 1-based
 * permutation lists of the Symja functions.
 */
public class Permutations {

  private Permutations() {}

  /**
   * Convert a 1-based permutation list into the 0-based array form.
   *
   * @param permutationList a list which contains the integers <code>1..n</code> exactly once
   * @return <code>null</code> if the list isn't a valid permutation list
   */
  public static int[] fromList(IAST permutationList) {
    final int n = permutationList.argSize();
    int[] a = new int[n];
    boolean[] used = new boolean[n];
    for (int i = 0; i < n; i++) {
      int v = permutationList.get(i + 1).toIntDefault();
      if (v < 1 || v > n || used[v - 1]) {
        return null;
      }
      used[v - 1] = true;
      a[i] = v - 1;
    }
    return a;
  }

  /**
   * Convert the 0-based array form into a 1-based permutation list.
   */
  public static IAST toList(int[] a) {
    IASTAppendable list = F.ListAlloc(a.length);
    for (int i = 0; i < a.length; i++) {
      list.append(F.ZZ(a[i] + 1));
    }
    return list;
  }

  /**
   * The identity permutation of the given size.
   */
  public static int[] identity(int size) {
    int[] a = new int[size];
    for (int i = 0; i < size; i++) {
      a[i] = i;
    }
    return a;
  }

  public static boolean isIdentity(int[] a) {
    for (int i = 0; i < a.length; i++) {
      if (a[i] != i) {
        return false;
      }
    }
    return true;
  }

  /**
   * Return the array form padded with fixed points up to <code>size</code>.
   */
  public static int[] resize(int[] a, int size) {
    if (a.length >= size) {
      return a;
    }
    int[] b = new int[size];
    System.arraycopy(a, 0, b, 0, a.length);
    for (int i = a.length; i < size; i++) {
      b[i] = i;
    }
    return b;
  }

  /**
   * Return the product <code>a*b</code> where <code>a</code> is applied first: <code>i -&gt;
   * b[a[i]]</code>.
   */
  public static int[] mul(int[] a, int[] b) {
    // a = self.array_form
    // b = other.array_form
    // perm = [b[i] for i in a] + b[len(a):]
    final int n = Math.max(a.length, b.length);
    a = resize(a, n);
    b = resize(b, n);
    int[] perm = new int[n];
    for (int i = 0; i < n; i++) {
      perm[i] = b[a[i]];
    }
    return perm;
  }

  /**
   * Return the inverse of the permutation.
   */
  public static int[] inverse(int[] a) {
    // >>> p = Permutation([[2, 0], [3, 1]])
    // >>> ~p
    // Permutation([2, 3, 0, 1])
    int[] inv = new int[a.length];
    for (int i = 0; i < a.length; i++) {
      inv[a[i]] = i;
    }
    return inv;
  }

  /**
   * Return the permutation raised to the power <code>n</code>. Negative <code>n</code> are
   * allowed.
   */
  public static int[] power(int[] a, long n) {
    // >>> p = Permutation([3, 1, 0, 2])
    // >>> p.order()
    // 3
    // >>> p**3
    // Permutation([0, 1, 2, 3])
    return power(a, BigInteger.valueOf(n));
  }

  public static int[] power(int[] a, BigInteger n) {
    if (n.signum() < 0) {
      a = inverse(a);
      n = n.negate();
    }
    // every point moves along its cycle
    int[] result = new int[a.length];
    boolean[] visited = new boolean[a.length];
    int[] cycle = new int[a.length];
    for (int i = 0; i < a.length; i++) {
      if (visited[i]) {
        continue;
      }
      int len = 0;
      int j = i;
      while (!visited[j]) {
        visited[j] = true;
        cycle[len++] = j;
        j = a[j];
      }
      int shift = n.mod(BigInteger.valueOf(len)).intValue();
      for (int k = 0; k < len; k++) {
        result[cycle[k]] = cycle[(k + shift) % len];
      }
    }
    return result;
  }

  /**
   * The lengths of all cycles including the cycles of length 1.
   */
  public static int[] cycleLengths(int[] a) {
    boolean[] visited = new boolean[a.length];
    int[] lengths = new int[a.length];
    int count = 0;
    for (int i = 0; i < a.length; i++) {
      if (visited[i]) {
        continue;
      }
      int len = 0;
      int j = i;
      while (!visited[j]) {
        visited[j] = true;
        len++;
        j = a[j];
      }
      lengths[count++] = len;
    }
    int[] result = new int[count];
    System.arraycopy(lengths, 0, result, 0, count);
    return result;
  }

  /**
   * Returns the number of cycles contained in the permutation (including singletons).
   */
  public static int cycles(int[] a) {
    // >>> Permutation([0, 1, 2]).cycles
    // 3
    // >>> Permutation(0, 1)(2, 3).cycles
    // 2
    return cycleLengths(a).length;
  }

  /**
   * Computes the order of a permutation. When the permutation is raised to the power of its order
   * it equals the identity permutation.
   */
  public static IInteger order(int[] a) {
    // >>> p = Permutation([3, 1, 5, 2, 4, 0])
    // >>> p.order()
    // 4
    // >>> (p**(p.order()))
    // Permutation([], size=6)
    BigInteger lcm = BigInteger.ONE;
    int[] lengths = cycleLengths(a);
    for (int i = 0; i < lengths.length; i++) {
      if (lengths[i] > 1) {
        BigInteger len = BigInteger.valueOf(lengths[i]);
        lcm = lcm.divide(lcm.gcd(len)).multiply(len);
      }
    }
    return F.ZZ(lcm);
  }

  /**
   * Return the elements in permutation, P, for which P[i] != i.
   */
  public static int[] support(int[] a) {
    // >>> p = Permutation([[3, 2], [0, 1], [4]])
    // >>> p.array_form
    // [1, 0, 3, 2, 4]
    // >>> p.support()
    // [0, 1, 2, 3]
    int[] result = new int[length(a)];
    int k = 0;
    for (int i = 0; i < a.length; i++) {
      if (a[i] != i) {
        result[k++] = i;
      }
    }
    return result;
  }

  /**
   * Returns the number of integers moved by a permutation.
   */
  public static int length(int[] a) {
    // >>> Permutation([0, 3, 2, 1]).length()
    // 2
    // >>> Permutation([[0, 1], [2, 3]]).length()
    // 4
    int count = 0;
    for (int i = 0; i < a.length; i++) {
      if (a[i] != i) {
        count++;
      }
    }
    return count;
  }

  /**
   * The maximum element moved by the permutation.
   *
   * @return <code>-1</code> for the identity permutation (sympy returns 0 in this case)
   */
  public static int max(int[] a) {
    // >>> p = Permutation([1, 0, 2, 3, 4])
    // >>> p.max()
    // 1
    for (int i = a.length - 1; i >= 0; i--) {
      if (a[i] != i) {
        return i;
      }
    }
    return -1;
  }

  /**
   * The minimum element moved by the permutation.
   *
   * @return <code>-1</code> for the identity permutation (sympy returns the size in this case)
   */
  public static int min(int[] a) {
    // >>> p = Permutation([0, 1, 4, 3, 2])
    // >>> p.min()
    // 2
    for (int i = 0; i < a.length; i++) {
      if (a[i] != i) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Computes the number of inversions of a permutation. An inversion is where
   * <code>i &gt; j</code> but <code>p[i] &lt; p[j]</code>.
   */
  public static long inversions(int[] a) {
    // >>> p = Permutation([0, 1, 2, 3, 4, 5])
    // >>> p.inversions()
    // 0
    // >>> Permutation([3, 2, 1, 0]).inversions()
    // 6
    int[] work = a.clone();
    return mergeCount(work, new int[work.length], 0, work.length);
  }

  private static long mergeCount(int[] a, int[] tmp, int from, int to) {
    if (to - from < 2) {
      return 0;
    }
    int mid = (from + to) >>> 1;
    long count = mergeCount(a, tmp, from, mid) + mergeCount(a, tmp, mid, to);
    int i = from;
    int j = mid;
    int k = from;
    while (i < mid && j < to) {
      if (a[i] <= a[j]) {
        tmp[k++] = a[i++];
      } else {
        count += mid - i;
        tmp[k++] = a[j++];
      }
    }
    while (i < mid) {
      tmp[k++] = a[i++];
    }
    while (j < to) {
      tmp[k++] = a[j++];
    }
    System.arraycopy(tmp, from, a, from, to - from);
    return count;
  }

  /**
   * Computes the parity of a permutation. The parity of a permutation reflects the parity of the
   * number of inversions in the permutation.
   *
   * @return <code>0</code> for an even and <code>1</code> for an odd permutation
   */
  public static int parity(int[] a) {
    // >>> p = Permutation([0, 1, 2, 3])
    // >>> p.parity()
    // 0
    // >>> p = Permutation([3, 2, 0, 1])
    // >>> p.parity()
    // 1
    return (a.length - cycles(a)) % 2;
  }

  /**
   * Gives the signature of the permutation needed to place the elements of the permutation in
   * canonical order.
   *
   * @return <code>1</code> for an even and <code>-1</code> for an odd permutation
   */
  public static int signature(int[] a) {
    // >>> p = Permutation([0, 1, 2])
    // >>> p.signature()
    // 1
    // >>> q = Permutation([0,2,1])
    // >>> q.signature()
    // -1
    return parity(a) == 0 ? 1 : -1;
  }

  /**
   * Returns the lexicographic rank of the permutation.
   */
  public static IInteger rank(int[] a) {
    // >>> p = Permutation([0, 1, 2, 3])
    // >>> p.rank()
    // 0
    // >>> p = Permutation([3, 2, 1, 0])
    // >>> p.rank()
    // 23
    final int size = a.length;
    if (size < 2) {
      return F.C0;
    }
    int[] rho = a.clone();
    int n = size - 1;
    BigInteger psize = BigInteger.ONE;
    for (int i = 2; i <= n; i++) {
      psize = psize.multiply(BigInteger.valueOf(i));
    }
    BigInteger rank = BigInteger.ZERO;
    for (int j = 0; j < size - 1; j++) {
      rank = rank.add(psize.multiply(BigInteger.valueOf(rho[j])));
      for (int i = j + 1; i < size; i++) {
        if (rho[i] > rho[j]) {
          rho[i]--;
        }
      }
      psize = psize.divide(BigInteger.valueOf(n));
      n--;
    }
    return F.ZZ(rank);
  }

  /**
   * Lexicographic permutation unranking.
   *
   * @param size the size of the permutation
   * @param rank the lexicographic rank <code>0 &lt;= rank &lt; size!</code>
   */
  public static int[] unrankLex(int size, BigInteger rank) {
    // >>> a = Permutation.unrank_lex(5, 10)
    // >>> a.rank()
    // 10
    // >>> a
    // Permutation([0, 2, 4, 1, 3])
    if (rank.signum() < 0) {
      throw new ValueError("rank must be non-negative");
    }
    int[] permArray = new int[size];
    BigInteger psize = BigInteger.ONE;
    for (int i = 0; i < size; i++) {
      BigInteger newPsize = psize.multiply(BigInteger.valueOf(i + 1));
      BigInteger bd = rank.mod(newPsize).divide(psize);
      rank = rank.subtract(bd.multiply(psize));
      int d = bd.intValue();
      permArray[size - i - 1] = d;
      for (int j = size - i; j < size; j++) {
        if (permArray[j] > d - 1) {
          permArray[j]++;
        }
      }
      psize = newPsize;
    }
    if (rank.signum() != 0) {
      throw new ValueError("rank is greater or equal than size!");
    }
    return permArray;
  }

  public static int[] unrankLex(int size, long rank) {
    return unrankLex(size, BigInteger.valueOf(rank));
  }
}
