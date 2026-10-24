package org.matheclipse.ortools.sat.engine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Linear relaxation of the objective for the branch and bound of the Java engine (the C++ CP-SAT
 * solver of OR-Tools uses a much richer LP with cuts).
 *
 * <p>
 * The relaxation {@code min c.x, L <= A x <= U, l <= x <= u} uses the linear constraints of the
 * model (enforced ones through a big-M reformulation), the clauses and at-most-one constraints,
 * and the current variable bounds. It is solved by a dense bounded dual simplex which is warm
 * started from the basis of the previous node. Pruning never relies on the floating point LP
 * optimum: from the current dual values {@code y} a Lagrangian bound
 * {@code sum_j min (c - A'y)_j x_j + sum_i min y_i s_i} over the box is computed, which is a valid
 * lower bound for every {@code y}, and a safety margin for the rounding errors is subtracted.
 * The same bound gives reduced cost fixing.
 */
final class LpRelaxation {
  private static final double PRIMAL_TOL = 1e-7;
  private static final double PIVOT_TOL = 1e-9;

  private final DomainStore s;
  private final int n; // structural columns
  private final int m; // rows
  private final int[] colVar;
  // sparse columns of A
  private final int[][] colRows;
  private final double[][] colVals;
  private final double[] cost;
  private final double[] rowLo;
  private final double[] rowHi;

  // basis: basic[i] = variable index in [0, n + m) of row i; slack of row i is n + i
  private final int[] basic;
  private final int[] posInBasis; // -1 if nonbasic
  private final boolean[] atUpper; // nonbasic position
  private double[][] binv;
  private int pivotsSinceRefactor;
  private final double[] xb;
  private final double[] y;
  private final double[] d;
  private final double[] lo;
  private final double[] hi;
  private double lastBound = Double.NEGATIVE_INFINITY;
  private final boolean enabled;

  LpRelaxation(ModelLoader loader, DomainStore s) {
    this.s = s;
    Map<Integer, Integer> colOf = new HashMap<>();
    int[] objVars = loader.objVars;
    for (int v : objVars) {
      colOf.putIfAbsent(v, colOf.size());
    }
    int rowsUsed = 0;
    for (ModelLoader.LinearRow r : loader.rows) {
      if (r.lower == Long.MIN_VALUE && r.upper == Long.MAX_VALUE) {
        continue;
      }
      rowsUsed++;
      for (int v : r.vars) {
        colOf.putIfAbsent(v, colOf.size());
      }
    }
    this.n = colOf.size();
    this.m = rowsUsed;
    this.enabled = (long) m * m <= 4_000_000L && n <= 200_000 && m > 0;
    colVar = new int[n];
    for (Map.Entry<Integer, Integer> e : colOf.entrySet()) {
      colVar[e.getValue()] = e.getKey();
    }
    cost = new double[n + m];
    for (int i = 0; i < objVars.length; i++) {
      cost[colOf.get(objVars[i])] += loader.objCoeffs[i];
    }
    rowLo = new double[m];
    rowHi = new double[m];
    int[] counts = new int[n];
    int k = 0;
    for (ModelLoader.LinearRow r : loader.rows) {
      if (r.lower == Long.MIN_VALUE && r.upper == Long.MAX_VALUE) {
        continue;
      }
      rowLo[k] = r.lower == Long.MIN_VALUE ? Double.NEGATIVE_INFINITY : r.lower;
      rowHi[k] = r.upper == Long.MAX_VALUE ? Double.POSITIVE_INFINITY : r.upper;
      for (int v : r.vars) {
        counts[colOf.get(v)]++;
      }
      k++;
    }
    colRows = new int[n][];
    colVals = new double[n][];
    for (int j = 0; j < n; j++) {
      colRows[j] = new int[counts[j]];
      colVals[j] = new double[counts[j]];
    }
    int[] fill = new int[n];
    k = 0;
    for (ModelLoader.LinearRow r : loader.rows) {
      if (r.lower == Long.MIN_VALUE && r.upper == Long.MAX_VALUE) {
        continue;
      }
      for (int t = 0; t < r.vars.length; t++) {
        int j = colOf.get(r.vars[t]);
        colRows[j][fill[j]] = k;
        colVals[j][fill[j]] = r.coeffs[t];
        fill[j]++;
      }
      k++;
    }
    basic = new int[m];
    posInBasis = new int[n + m];
    atUpper = new boolean[n + m];
    xb = new double[m];
    y = new double[m];
    d = new double[n + m];
    lo = new double[n + m];
    hi = new double[n + m];
    if (enabled) {
      resetBasis();
      initKnapsackRows(loader);
    }
  }

  private boolean optimal;

  /** True if the last solve reached the LP optimum. */
  boolean lastOptimal() {
    return optimal;
  }

  /** Number of structural columns. */
  int numColumns() {
    return n;
  }

  /** The engine variable of a column. */
  int columnVar(int j) {
    return colVar[j];
  }

  /** The value of a column in the last LP solution. */
  double columnValue(int j) {
    int p = posInBasis[j];
    return p >= 0 ? xb[p] : (atUpper[j] ? hi[j] : lo[j]);
  }

  /** The (safe) bound of the last solve. */
  double lastBound() {
    return lastBound;
  }

  private void resetBasis() {
    Arrays.fill(posInBasis, -1);
    for (int i = 0; i < m; i++) {
      basic[i] = n + i;
      posInBasis[n + i] = i;
    }
    binv = new double[m][m];
    for (int i = 0; i < m; i++) {
      binv[i][i] = -1; // B = -I
    }
    Arrays.fill(atUpper, false);
    pivotsSinceRefactor = 0;
  }

  /**
   * Solves the relaxation with the current bounds; prunes the node if the bound exceeds
   * {@code upper} and fixes variables by reduced costs. Returns -1 if the node is infeasible.
   */
  int solveAndPrune(long upper, boolean root) {
    if (!enabled) {
      return 0;
    }
    for (int j = 0; j < n; j++) {
      lo[j] = s.lb[colVar[j]];
      hi[j] = s.ub[colVar[j]];
    }
    for (int i = 0; i < m; i++) {
      lo[n + i] = rowLo[i];
      hi[n + i] = rowHi[i];
    }
    computeDuals();
    // nonbasic structural variables at the bound given by the reduced cost sign
    for (int j = 0; j < n + m; j++) {
      if (posInBasis[j] >= 0) {
        continue;
      }
      if (j >= n) {
        // a nonbasic slack must sit at a finite bound
        if (atUpper[j] && Double.isInfinite(hi[j])) {
          atUpper[j] = false;
        }
        if (!atUpper[j] && Double.isInfinite(lo[j])) {
          atUpper[j] = true;
        }
        continue;
      }
      if (d[j] > 1e-12) {
        atUpper[j] = false;
      } else if (d[j] < -1e-12) {
        atUpper[j] = true;
      }
    }
    optimal = false;
    boolean infeasibleRay = iterate(root ? 50 * (n + m) + 1000 : 10 * (n + m) + 200);
    computeDuals();
    if (infeasibleRay) {
      return -1;
    }
    double bound = safeBound();
    double knapsack = knapsackBound();
    lastBound = Math.max(bound, knapsack);
    if (upper == Long.MAX_VALUE) {
      return 0;
    }
    if (bound > upper || knapsack > upper) {
      return -1;
    }
    // reduced cost fixing
    double gap = upper - bound;
    for (int j = 0; j < n; j++) {
      double dj = dSafe[j];
      if (Math.abs(dj) < 1e-9) {
        continue;
      }
      int v = colVar[j];
      if (s.lb[v] == s.ub[v]) {
        continue;
      }
      double q = gap / Math.abs(dj);
      if (q >= (double) (s.ub[v] - s.lb[v])) {
        continue;
      }
      long kmax = (long) Math.floor(q * (1 + 1e-9) + 1e-9);
      if (dj > 0) {
        if (!s.setUb(v, s.lb[v] + kmax)) {
          return -1;
        }
      } else {
        if (!s.setLb(v, s.ub[v] - kmax)) {
          return -1;
        }
      }
    }
    return 0;
  }

  private double nonbasicValue(int j) {
    return atUpper[j] ? hi[j] : lo[j];
  }

  private void computePrimal() {
    double[] r = new double[m];
    for (int j = 0; j < n; j++) {
      if (posInBasis[j] >= 0) {
        continue;
      }
      double x = nonbasicValue(j);
      if (x == 0) {
        continue;
      }
      int[] rows = colRows[j];
      double[] vals = colVals[j];
      for (int t = 0; t < rows.length; t++) {
        r[rows[t]] -= vals[t] * x;
      }
    }
    for (int i = 0; i < m; i++) {
      int j = n + i;
      if (posInBasis[j] >= 0) {
        continue;
      }
      // column of slack i is -e_i
      r[i] += nonbasicValue(j);
    }
    for (int i = 0; i < m; i++) {
      double sum = 0;
      double[] row = binv[i];
      for (int k = 0; k < m; k++) {
        sum += row[k] * r[k];
      }
      xb[i] = sum;
    }
  }

  private void computeDuals() {
    // y = c_B' B^-1
    Arrays.fill(y, 0);
    for (int i = 0; i < m; i++) {
      double cb = cost[basic[i]];
      if (cb == 0) {
        continue;
      }
      double[] row = binv[i];
      for (int k = 0; k < m; k++) {
        y[k] += cb * row[k];
      }
    }
    for (int j = 0; j < n; j++) {
      double sum = cost[j];
      int[] rows = colRows[j];
      double[] vals = colVals[j];
      for (int t = 0; t < rows.length; t++) {
        sum -= y[rows[t]] * vals[t];
      }
      d[j] = sum;
    }
    for (int i = 0; i < m; i++) {
      d[n + i] = y[i]; // cost 0 - y . (-e_i)
    }
  }

  // returns true if the LP is proven infeasible
  private boolean iterate(int maxIterations) {
    double[] rho = new double[m];
    double[] alpha = new double[n + m];
    for (int it = 0; it < maxIterations; it++) {
      computePrimal();
      // leaving row: largest violation
      int p = -1;
      double best = PRIMAL_TOL;
      boolean toLower = false;
      for (int i = 0; i < m; i++) {
        int j = basic[i];
        double scale = 1 + Math.abs(xb[i]);
        if (xb[i] < lo[j] - PRIMAL_TOL * scale) {
          double viol = (lo[j] - xb[i]) / scale;
          if (viol > best) {
            best = viol;
            p = i;
            toLower = true;
          }
        } else if (xb[i] > hi[j] + PRIMAL_TOL * scale) {
          double viol = (xb[i] - hi[j]) / scale;
          if (viol > best) {
            best = viol;
            p = i;
            toLower = false;
          }
        }
      }
      if (p < 0) {
        optimal = true;
        return false;
      }
      System.arraycopy(binv[p], 0, rho, 0, m);
      // alpha_j = rho . col_j for nonbasic j
      int q = -1;
      double bestRatio = Double.POSITIVE_INFINITY;
      double bestAbs = 0;
      for (int j = 0; j < n + m; j++) {
        if (posInBasis[j] >= 0) {
          continue;
        }
        double a;
        if (j < n) {
          a = 0;
          int[] rows = colRows[j];
          double[] vals = colVals[j];
          for (int t = 0; t < rows.length; t++) {
            a += rho[rows[t]] * vals[t];
          }
        } else {
          a = -rho[j - n];
        }
        alpha[j] = a;
        if (Math.abs(a) < PIVOT_TOL) {
          continue;
        }
        if (lo[j] == hi[j]) {
          continue; // fixed nonbasic variables cannot move
        }
        boolean canIncrease = !atUpper[j] && hi[j] > lo[j];
        boolean canDecrease = atUpper[j] && hi[j] > lo[j];
        // d(x_Bp)/d(x_j) = -a
        boolean ok;
        if (toLower) {
          ok = (canIncrease && a < 0) || (canDecrease && a > 0);
        } else {
          ok = (canIncrease && a > 0) || (canDecrease && a < 0);
        }
        if (!ok) {
          continue;
        }
        double ratio = Math.abs(d[j]) / Math.abs(a);
        if (ratio < bestRatio - 1e-12 || (ratio <= bestRatio + 1e-12 && Math.abs(a) > bestAbs)) {
          bestRatio = ratio;
          bestAbs = Math.abs(a);
          q = j;
        }
      }
      if (q < 0) {
        return provenInfeasible(rho);
      }
      pivot(p, q, toLower);
      computeDuals();
    }
    return false;
  }

  private void pivot(int p, int q, boolean leavingToLower) {
    // w = B^-1 col_q
    double[] w = new double[m];
    if (q < n) {
      int[] rows = colRows[q];
      double[] vals = colVals[q];
      for (int i = 0; i < m; i++) {
        double sum = 0;
        double[] row = binv[i];
        for (int t = 0; t < rows.length; t++) {
          sum += row[rows[t]] * vals[t];
        }
        w[i] = sum;
      }
    } else {
      int r = q - n;
      for (int i = 0; i < m; i++) {
        w[i] = -binv[i][r];
      }
    }
    double wp = w[p];
    double[] rowP = binv[p];
    for (int k = 0; k < m; k++) {
      rowP[k] /= wp;
    }
    for (int i = 0; i < m; i++) {
      if (i == p || w[i] == 0) {
        continue;
      }
      double f = w[i];
      double[] row = binv[i];
      for (int k = 0; k < m; k++) {
        row[k] -= f * rowP[k];
      }
    }
    int leaving = basic[p];
    posInBasis[leaving] = -1;
    atUpper[leaving] = !leavingToLower;
    basic[p] = q;
    posInBasis[q] = p;
    if (++pivotsSinceRefactor >= 64) {
      refactor();
    }
  }

  // recomputes B^-1 from the basis columns (Gauss-Jordan with partial pivoting)
  private void refactor() {
    double[][] b = new double[m][2 * m];
    for (int i = 0; i < m; i++) {
      int j = basic[i];
      if (j < n) {
        int[] rows = colRows[j];
        double[] vals = colVals[j];
        for (int t = 0; t < rows.length; t++) {
          b[rows[t]][i] = vals[t];
        }
      } else {
        b[j - n][i] = -1;
      }
      b[i][m + i] = 1;
    }
    for (int c = 0; c < m; c++) {
      int piv = c;
      for (int r = c + 1; r < m; r++) {
        if (Math.abs(b[r][c]) > Math.abs(b[piv][c])) {
          piv = r;
        }
      }
      if (Math.abs(b[piv][c]) < 1e-12) {
        resetBasis();
        return;
      }
      double[] tmp = b[c];
      b[c] = b[piv];
      b[piv] = tmp;
      double f = b[c][c];
      for (int k = 0; k < 2 * m; k++) {
        b[c][k] /= f;
      }
      for (int r = 0; r < m; r++) {
        if (r != c && b[r][c] != 0) {
          double g = b[r][c];
          for (int k = 0; k < 2 * m; k++) {
            b[r][k] -= g * b[c][k];
          }
        }
      }
    }
    // b now holds [I | B^-1] with rows in basis order
    for (int i = 0; i < m; i++) {
      System.arraycopy(b[i], m, binv[i], 0, m);
    }
    pivotsSinceRefactor = 0;
  }

  private double[] dSafe;

  /** Rows kept as knapsack subproblems in {@link #knapsackBound()}; disjoint in their columns. */
  private int[] knapsackRows;
  private boolean[] isKnapsackRow;
  private boolean[] inKnapsackRow;
  private int[][] rowCols;
  private long[][] rowCoefs;

  private void initKnapsackRows(ModelLoader loader) {
    rowCols = new int[m][];
    rowCoefs = new long[m][];
    int[] counts = new int[m];
    for (int j = 0; j < n; j++) {
      for (int r : colRows[j]) {
        counts[r]++;
      }
    }
    for (int i = 0; i < m; i++) {
      rowCols[i] = new int[counts[i]];
      rowCoefs[i] = new long[counts[i]];
    }
    int[] fill = new int[m];
    for (int j = 0; j < n; j++) {
      for (int t = 0; t < colRows[j].length; t++) {
        int r = colRows[j][t];
        rowCols[r][fill[r]] = j;
        rowCoefs[r][fill[r]] = (long) colVals[j][t];
        fill[r]++;
      }
    }
    isKnapsackRow = new boolean[m];
    inKnapsackRow = new boolean[n];
    java.util.List<Integer> kept = new java.util.ArrayList<>();
    Integer[] order = new Integer[m];
    for (int i = 0; i < m; i++) {
      order[i] = i;
    }
    Arrays.sort(order, (a, b) -> Integer.compare(rowCols[b].length, rowCols[a].length));
    for (int i : order) {
      if (Double.isInfinite(rowHi[i]) || rowCols[i].length < 2 || rowHi[i] > 100000) {
        continue;
      }
      boolean ok = true;
      for (int t = 0; t < rowCols[i].length && ok; t++) {
        int j = rowCols[i][t];
        int v = colVar[j];
        if (rowCoefs[i][t] <= 0 || inKnapsackRow[j] || s.lb[v] < 0 || s.ub[v] > 1) {
          ok = false;
        }
      }
      if (!ok) {
        continue;
      }
      // a knapsack row whose capacity is not binding is useless
      long sumCoef = 0;
      for (long c : rowCoefs[i]) {
        sumCoef += c;
      }
      if (sumCoef <= rowHi[i]) {
        continue;
      }
      kept.add(i);
      isKnapsackRow[i] = true;
      for (int j : rowCols[i]) {
        inKnapsackRow[j] = true;
      }
    }
    knapsackRows = new int[kept.size()];
    for (int i = 0; i < knapsackRows.length; i++) {
      knapsackRows[i] = kept.get(i);
    }
  }

  /**
   * Lagrangian bound where the knapsack rows are not dualized but solved exactly by dynamic
   * programming; never weaker than the box Lagrangian with the same other duals.
   */
  private double knapsackBound() {
    if (knapsackRows == null || knapsackRows.length == 0) {
      return Double.NEGATIVE_INFINITY;
    }
    double[] yy = y.clone();
    for (int i = 0; i < m; i++) {
      if (isKnapsackRow[i]) {
        yy[i] = 0;
      } else if (yy[i] > 0 && Double.isInfinite(rowLo[i])) {
        yy[i] = 0;
      } else if (yy[i] < 0 && Double.isInfinite(rowHi[i])) {
        yy[i] = 0;
      }
    }
    double[] dd = new double[n];
    double sum = 0;
    double abs = 0;
    for (int j = 0; j < n; j++) {
      double dj = cost[j];
      int[] rows = colRows[j];
      double[] vals = colVals[j];
      for (int t = 0; t < rows.length; t++) {
        dj -= yy[rows[t]] * vals[t];
      }
      dd[j] = dj;
      if (!inKnapsackRow[j]) {
        double term = dj > 0 ? dj * lo[j] : dj * hi[j];
        sum += term;
        abs += Math.abs(term);
      }
    }
    for (int i = 0; i < m; i++) {
      if (yy[i] == 0) {
        continue;
      }
      double term = yy[i] > 0 ? yy[i] * rowLo[i] : yy[i] * rowHi[i];
      sum += term;
      abs += Math.abs(term);
    }
    for (int r : knapsackRows) {
      long cap = (long) rowHi[r];
      double base = 0;
      int numItems = 0;
      long[] w = new long[rowCols[r].length];
      double[] p = new double[rowCols[r].length];
      double totalProfit = 0;
      for (int t = 0; t < rowCols[r].length; t++) {
        int j = rowCols[r][t];
        long a = rowCoefs[r][t];
        if (lo[j] == hi[j]) {
          base += dd[j] * lo[j];
          cap -= a * (long) lo[j];
        } else if (dd[j] < 0) {
          w[numItems] = a;
          p[numItems] = -dd[j];
          totalProfit += -dd[j];
          numItems++;
        }
      }
      if (cap < 0) {
        return Double.POSITIVE_INFINITY;
      }
      double best;
      if ((long) numItems * (cap + 1) > 2_000_000L) {
        best = totalProfit;
      } else {
        double[] dp = new double[(int) cap + 1];
        for (int k = 0; k < numItems; k++) {
          long wk = w[k];
          double pk = p[k];
          for (int c = (int) cap; c >= wk; c--) {
            double cand = dp[c - (int) wk] + pk;
            if (cand > dp[c]) {
              dp[c] = cand;
            }
          }
        }
        best = dp[(int) cap];
      }
      sum += base - best;
      abs += Math.abs(base) + Math.abs(best);
    }
    return sum - 1e-9 * abs - 1e-9;
  }

  // Lagrangian bound with the current duals, corrected for invalid signs, minus a margin
  private double safeBound() {
    double[] yy = y.clone();
    for (int i = 0; i < m; i++) {
      if (yy[i] > 0 && Double.isInfinite(rowLo[i])) {
        yy[i] = 0;
      } else if (yy[i] < 0 && Double.isInfinite(rowHi[i])) {
        yy[i] = 0;
      }
    }
    if (dSafe == null) {
      dSafe = new double[n];
    }
    double sum = 0;
    double abs = 0;
    for (int j = 0; j < n; j++) {
      double dj = cost[j];
      int[] rows = colRows[j];
      double[] vals = colVals[j];
      for (int t = 0; t < rows.length; t++) {
        dj -= yy[rows[t]] * vals[t];
      }
      dSafe[j] = dj;
      double term = dj > 0 ? dj * lo[j] : dj * hi[j];
      sum += term;
      abs += Math.abs(term);
    }
    for (int i = 0; i < m; i++) {
      if (yy[i] == 0) {
        continue;
      }
      double term = yy[i] > 0 ? yy[i] * rowLo[i] : yy[i] * rowHi[i];
      sum += term;
      abs += Math.abs(term);
    }
    return sum - 1e-9 * abs - 1e-9;
  }


  // the leaving row rho proves infeasibility if the Lagrangian with c = 0 is positive
  private boolean provenInfeasible(double[] rho) {
    for (int sign = -1; sign <= 1; sign += 2) {
      double sum = 0;
      double abs = 0;
      boolean valid = true;
      for (int j = 0; j < n && valid; j++) {
        double a = 0;
        int[] rows = colRows[j];
        double[] vals = colVals[j];
        for (int t = 0; t < rows.length; t++) {
          a += sign * rho[rows[t]] * vals[t];
        }
        double dj = -a;
        double term = dj > 0 ? dj * lo[j] : dj * hi[j];
        sum += term;
        abs += Math.abs(term);
      }
      for (int i = 0; i < m && valid; i++) {
        double yi = sign * rho[i];
        if (yi == 0) {
          continue;
        }
        double bnd = yi > 0 ? rowLo[i] : rowHi[i];
        if (Double.isInfinite(bnd)) {
          valid = false;
          break;
        }
        double term = yi * bnd;
        sum += term;
        abs += Math.abs(term);
      }
      if (valid && sum - 1e-9 * abs > 1e-6) {
        return true;
      }
    }
    return false;
  }
}
