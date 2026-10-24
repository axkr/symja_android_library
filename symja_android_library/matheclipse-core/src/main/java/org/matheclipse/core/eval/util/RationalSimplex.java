package org.matheclipse.core.eval.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hipparchus.fraction.BigFraction;

/**
 * Exact linear programs over the rationals: minimize <code>c.x</code> subject to rows
 * <code>a_i.x &lt;= b_i</code>, <code>a_i.x == b_i</code> or <code>a_i.x &gt;= b_i</code>, with free
 * variables <code>x</code>. The two-phase simplex method on a dense tableau with Bland's rule, which
 * cannot cycle. Integer variables are found by depth first branch and bound on the exact
 * relaxations.
 */
public final class RationalSimplex {

  /** The relation <code>a.x &lt;= b</code> of a row. */
  public static final int LEQ = -1;

  /** The relation <code>a.x == b</code> of a row. */
  public static final int EQ = 0;

  /** The relation <code>a.x &gt;= b</code> of a row. */
  public static final int GEQ = 1;

  public enum Status {
    OPTIMAL, INFEASIBLE, UNBOUNDED,
    /** the branch and bound search was stopped by its node limit */
    UNDECIDED
  }

  /** The result of a linear program: its status, and for an optimum its point and value. */
  public static final class Result {
    public final Status status;
    public final BigFraction[] point;
    public final BigFraction value;

    Result(Status status, BigFraction[] point, BigFraction value) {
      this.status = status;
      this.point = point;
      this.value = value;
    }
  }

  private static final Result INFEASIBLE = new Result(Status.INFEASIBLE, null, null);
  private static final Result UNBOUNDED = new Result(Status.UNBOUNDED, null, null);
  private static final Result UNDECIDED = new Result(Status.UNDECIDED, null, null);

  private RationalSimplex() {}

  /**
   * Minimize <code>c.x</code> subject to <code>a[i].x rel[i] b[i]</code> with free variables.
   *
   * @param c the coefficients of the objective
   * @param a the coefficients of the rows, each of the length of <code>c</code>
   * @param rel the relations {@link #LEQ}, {@link #EQ} or {@link #GEQ} of the rows
   * @param b the right hand sides of the rows
   */
  public static Result minimize(BigFraction[] c, BigFraction[][] a, int[] rel, BigFraction[] b) {
    final int n = c.length;
    final int m = a.length;
    // every free variable x_j is p_j - q_j with p_j, q_j >= 0; a row with a negative right hand
    // side is negated; an inequality gets a slack column, a row without a slack which starts the
    // basis gets an artificial column
    int slacks = 0;
    for (int i = 0; i < m; i++) {
      if (rel[i] != EQ) {
        slacks++;
      }
    }
    final int structural = 2 * n;
    final int artificialStart = structural + slacks;
    final int columns = artificialStart + m;
    BigFraction[][] t = new BigFraction[m][columns + 1];
    int[] basis = new int[m];
    int slack = structural;
    int artificials = 0;
    for (int i = 0; i < m; i++) {
      Arrays.fill(t[i], BigFraction.ZERO);
      boolean negate = b[i].signum() < 0;
      int relation = negate ? -rel[i] : rel[i];
      for (int j = 0; j < n; j++) {
        BigFraction aij = negate ? a[i][j].negate() : a[i][j];
        t[i][j] = aij;
        t[i][n + j] = aij.negate();
      }
      t[i][columns] = negate ? b[i].negate() : b[i];
      basis[i] = -1;
      if (relation == LEQ) {
        t[i][slack] = BigFraction.ONE;
        basis[i] = slack;
        slack++;
      } else if (relation == GEQ) {
        t[i][slack] = BigFraction.MINUS_ONE;
        slack++;
      }
      if (basis[i] < 0) {
        t[i][artificialStart + i] = BigFraction.ONE;
        basis[i] = artificialStart + i;
        artificials++;
      }
    }
    boolean[] allowed = new boolean[columns];
    Arrays.fill(allowed, 0, artificialStart, true);
    if (artificials > 0) {
      // phase 1: minimize the sum of the artificial variables
      BigFraction[] cost = new BigFraction[columns];
      Arrays.fill(cost, BigFraction.ZERO);
      for (int i = 0; i < m; i++) {
        if (basis[i] >= artificialStart) {
          cost[basis[i]] = BigFraction.ONE;
        }
      }
      BigFraction[] r = reducedCosts(t, basis, cost, columns);
      boolean[] phase1 = new boolean[columns];
      Arrays.fill(phase1, true);
      if (!iterate(t, basis, r, phase1, columns)) {
        // phase 1 is bounded below by 0
        return UNDECIDED;
      }
      if (r[columns].signum() != 0) {
        return INFEASIBLE;
      }
      // drive the artificial variables at level 0 out of the basis
      for (int i = 0; i < m; i++) {
        if (basis[i] >= artificialStart) {
          for (int j = 0; j < artificialStart; j++) {
            if (t[i][j].signum() != 0) {
              pivot(t, basis, null, i, j, columns);
              break;
            }
          }
          // a row without such a column is redundant: its artificial variable stays at 0, and its
          // column is never entered again
        }
      }
    }
    BigFraction[] cost = new BigFraction[columns];
    Arrays.fill(cost, BigFraction.ZERO);
    for (int j = 0; j < n; j++) {
      cost[j] = c[j];
      cost[n + j] = c[j].negate();
    }
    BigFraction[] r = reducedCosts(t, basis, cost, columns);
    if (!iterate(t, basis, r, allowed, columns)) {
      return UNBOUNDED;
    }
    BigFraction[] values = new BigFraction[columns];
    Arrays.fill(values, BigFraction.ZERO);
    for (int i = 0; i < m; i++) {
      values[basis[i]] = t[i][columns];
    }
    BigFraction[] point = new BigFraction[n];
    BigFraction value = BigFraction.ZERO;
    for (int j = 0; j < n; j++) {
      point[j] = values[j].subtract(values[n + j]);
      value = value.add(c[j].multiply(point[j]));
    }
    return new Result(Status.OPTIMAL, point, value);
  }

  /**
   * Minimize <code>c.x</code> subject to <code>a[i].x rel[i] b[i]</code>, where the variables
   * <code>j</code> with <code>integer[j]</code> take integer values. Of several optimal points the
   * first one found is returned; the branch <code>x &lt;= Floor(v)</code> is searched before
   * <code>x &gt;= Ceiling(v)</code>.
   *
   * @param maxNodes the number of relaxations which may be solved
   * @return the optimum, or the status {@link Status#INFEASIBLE}, {@link Status#UNBOUNDED} (a
   *         relaxation without a bound) or {@link Status#UNDECIDED} (the node limit was reached)
   */
  public static Result minimizeInteger(BigFraction[] c, BigFraction[][] a, int[] rel,
      BigFraction[] b, boolean[] integer, int maxNodes) {
    final int n = c.length;
    Result incumbent = null;
    // a node is the list of the branching rows {variable, relation, bound}
    ArrayDeque<List<long[]>> open = new ArrayDeque<List<long[]>>();
    ArrayDeque<List<BigFraction>> openBounds = new ArrayDeque<List<BigFraction>>();
    open.push(new ArrayList<long[]>());
    openBounds.push(new ArrayList<BigFraction>());
    int nodes = 0;
    while (!open.isEmpty()) {
      if (++nodes > maxNodes) {
        return UNDECIDED;
      }
      List<long[]> branches = open.pop();
      List<BigFraction> bounds = openBounds.pop();
      final int m = a.length + branches.size();
      BigFraction[][] rows = Arrays.copyOf(a, m);
      int[] relations = Arrays.copyOf(rel, m);
      BigFraction[] rhs = Arrays.copyOf(b, m);
      for (int k = 0; k < branches.size(); k++) {
        BigFraction[] row = new BigFraction[n];
        Arrays.fill(row, BigFraction.ZERO);
        row[(int) branches.get(k)[0]] = BigFraction.ONE;
        rows[a.length + k] = row;
        relations[a.length + k] = (int) branches.get(k)[1];
        rhs[a.length + k] = bounds.get(k);
      }
      Result relaxed = minimize(c, rows, relations, rhs);
      if (relaxed.status == Status.INFEASIBLE) {
        continue;
      }
      if (relaxed.status != Status.OPTIMAL) {
        return relaxed;
      }
      if (incumbent != null && relaxed.value.compareTo(incumbent.value) >= 0) {
        // no integer point of this node is better than the one which is known
        continue;
      }
      int fractional = -1;
      for (int j = 0; j < n; j++) {
        if (integer[j] && !relaxed.point[j].getDenominator().equals(java.math.BigInteger.ONE)) {
          fractional = j;
          break;
        }
      }
      if (fractional < 0) {
        incumbent = relaxed;
        continue;
      }
      BigFraction v = relaxed.point[fractional];
      BigFraction floor = new BigFraction(floor(v));
      // the branch x <= Floor(v) is searched first
      List<long[]> up = new ArrayList<long[]>(branches);
      up.add(new long[] {fractional, GEQ});
      List<BigFraction> upBounds = new ArrayList<BigFraction>(bounds);
      upBounds.add(floor.add(BigFraction.ONE));
      open.push(up);
      openBounds.push(upBounds);
      List<long[]> down = new ArrayList<long[]>(branches);
      down.add(new long[] {fractional, LEQ});
      List<BigFraction> downBounds = new ArrayList<BigFraction>(bounds);
      downBounds.add(floor);
      open.push(down);
      openBounds.push(downBounds);
    }
    return incumbent == null ? INFEASIBLE : incumbent;
  }

  private static java.math.BigInteger floor(BigFraction v) {
    java.math.BigInteger[] qr = v.getNumerator().divideAndRemainder(v.getDenominator());
    return qr[1].signum() < 0 ? qr[0].subtract(java.math.BigInteger.ONE) : qr[0];
  }

  /**
   * The row of the reduced costs of <code>cost</code> for the basis; its last entry is the negated
   * value of the objective.
   */
  private static BigFraction[] reducedCosts(BigFraction[][] t, int[] basis, BigFraction[] cost,
      int columns) {
    BigFraction[] r = new BigFraction[columns + 1];
    System.arraycopy(cost, 0, r, 0, columns);
    r[columns] = BigFraction.ZERO;
    for (int i = 0; i < t.length; i++) {
      BigFraction cb = cost[basis[i]];
      if (cb.signum() != 0) {
        for (int j = 0; j <= columns; j++) {
          if (t[i][j].signum() != 0) {
            r[j] = r[j].subtract(cb.multiply(t[i][j]));
          }
        }
      }
    }
    return r;
  }

  /**
   * Simplex iterations with Bland's rule until the reduced costs of the allowed columns are not
   * negative.
   *
   * @return <code>false</code> if the objective has no lower bound
   */
  private static boolean iterate(BigFraction[][] t, int[] basis, BigFraction[] r,
      boolean[] allowed, int columns) {
    while (true) {
      int entering = -1;
      for (int j = 0; j < columns; j++) {
        if (allowed[j] && r[j].signum() < 0) {
          entering = j;
          break;
        }
      }
      if (entering < 0) {
        return true;
      }
      int leaving = -1;
      BigFraction best = null;
      for (int i = 0; i < t.length; i++) {
        if (t[i][entering].signum() > 0) {
          BigFraction ratio = t[i][columns].divide(t[i][entering]);
          int cmp = best == null ? -1 : ratio.compareTo(best);
          if (cmp < 0 || (cmp == 0 && basis[i] < basis[leaving])) {
            best = ratio;
            leaving = i;
          }
        }
      }
      if (leaving < 0) {
        return false;
      }
      pivot(t, basis, r, leaving, entering, columns);
    }
  }

  private static void pivot(BigFraction[][] t, int[] basis, BigFraction[] r, int row, int column,
      int columns) {
    BigFraction p = t[row][column];
    if (!p.equals(BigFraction.ONE)) {
      for (int j = 0; j <= columns; j++) {
        if (t[row][j].signum() != 0) {
          t[row][j] = t[row][j].divide(p);
        }
      }
    }
    for (int i = 0; i < t.length; i++) {
      if (i != row) {
        eliminate(t[i], t[row], column, columns);
      }
    }
    if (r != null) {
      eliminate(r, t[row], column, columns);
    }
    basis[row] = column;
  }

  private static void eliminate(BigFraction[] target, BigFraction[] pivotRow, int column,
      int columns) {
    BigFraction factor = target[column];
    if (factor.signum() == 0) {
      return;
    }
    for (int j = 0; j <= columns; j++) {
      if (pivotRow[j].signum() != 0) {
        target[j] = target[j].subtract(factor.multiply(pivotRow[j]));
      }
    }
  }
}
