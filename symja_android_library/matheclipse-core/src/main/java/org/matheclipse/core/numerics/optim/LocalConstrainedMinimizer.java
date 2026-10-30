package org.matheclipse.core.numerics.optim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A local search for the minimum of a function of several variables under equations and
 * inequalities, which starts at a given point and stays strictly inside the inequalities: no
 * point outside of them is ever evaluated, so a logarithm of a variable which has to be positive
 * never sees a negative argument.
 * <p>
 * The linear equations are solved for some of the variables (the pivot variables of the reduced
 * row echelon form), and every other equation determines one more variable implicitly: at each
 * point of the search Newton's method solves the equations for these dependent variables,
 * starting from their values at the last accepted point, so the search stays on one branch. The
 * search runs over the remaining variables with the BFGS method; a trial point outside of the
 * inequalities or without a solution of the equations counts as infinitely bad.
 * <p>
 * A start point outside of the inequalities is moved inside by Gauss-Newton steps on the
 * violations first. An inequality the search ends up pressed against is active: it is imposed
 * as an equation and the problem is solved again from the point found, which lands on the
 * boundary exactly instead of as close as the line search got.
 */
public final class LocalConstrainedMinimizer {

  /** A function of the point with an optional gradient. */
  public interface Function {
    /**
     * @return the value at <code>x</code>; a value which is not a finite number marks a point
     *         where the function is not defined
     */
    double value(double[] x);

    /**
     * @return the gradient at <code>x</code> or <code>null</code> if it is not known there - the
     *         gradient is then approximated by central differences
     */
    default double[] gradient(double[] x) {
      return null;
    }
  }

  /** The inequality <code>g(x) &gt; 0</code> or <code>g(x) &gt;= 0</code>. */
  public static final class Inequality {
    final Function function;
    final boolean strict;
    /** the coefficients of a linear inequality <code>a.x + c</code>, or <code>null</code> */
    final double[] coefficients;
    final double constant;

    /**
     * @param function the function <code>g</code>
     * @param strict <code>true</code> for <code>g(x) &gt; 0</code>
     * @param coefficients the coefficients <code>a</code> if <code>g(x) = a.x + c</code> is
     *        linear, <code>null</code> otherwise
     * @param constant the constant <code>c</code> of a linear <code>g</code>
     */
    public Inequality(Function function, boolean strict, double[] coefficients, double constant) {
      this.function = function;
      this.strict = strict;
      this.coefficients = coefficients;
      this.constant = constant;
    }
  }

  /** The result of a search. */
  public static final class Result {
    /** the point which was found */
    public final double[] point;
    /** the value of the objective at {@link #point} */
    public final double value;
    /**
     * <code>true</code> if the search ran to a stationary point without an inequality becoming
     * active
     */
    public final boolean stationary;
    /** <code>false</code> if the iterations ran out */
    public final boolean converged;
    /** the start point and the points of the accepted steps */
    public final List<double[]> steps;

    Result(double[] point, double value, boolean stationary, boolean converged,
        List<double[]> steps) {
      this.point = point;
      this.value = value;
      this.stationary = stationary;
      this.converged = converged;
      this.steps = steps;
    }
  }

  /** The tolerance of the residuals of the equations at a point of the search. */
  private static final double EQUATION_TOLERANCE = 1e-9;

  /** An inequality with a smaller value at the point found is active. */
  private static final double ACTIVE_TOLERANCE = 1e-6;

  /** The value phase one gives the violated inequalities. */
  private static final double INSIDE_MARGIN = 1e-6;

  private final int n;
  private final Function objective;
  /** the rows <code>{a_1, ..., a_n, b}</code> of the linear equations <code>a.x == b</code> */
  private final List<double[]> linearRows;
  private final List<Function> equations;
  private final List<Inequality> inequalities;
  /** the variables which are bounded and should rather stay variables of the search */
  private final boolean[] bounded;
  private final int maxIterations;
  /** called once per iteration, to stop a search which runs out of time */
  private final Runnable timeCheck;

  // the reduction of the problem to the variables of the search, set by prepare()
  private double[][] rref;
  private int[] pivots;
  private int[] free;
  /** positions in {@link #free} of the variables the equations determine */
  private int[] dependent;
  /** positions in {@link #free} of the variables of the search */
  private int[] search;
  /** the values of the dependent variables at the last accepted point */
  private double[] anchor;

  /**
   * @param n the number of variables
   * @param objective the function to minimize
   * @param linearRows the linear equations <code>a.x == b</code> as rows
   *        <code>{a_1, ..., a_n, b}</code>
   * @param equations the other equations <code>h(x) == 0</code>
   * @param inequalities the inequalities
   * @param bounded the variables which have bounds of their own; the equations are preferably
   *        solved for the other variables
   * @param maxIterations the maximum number of iterations of the search
   * @param timeCheck called once per iteration
   */
  public LocalConstrainedMinimizer(int n, Function objective, List<double[]> linearRows,
      List<Function> equations, List<Inequality> inequalities, boolean[] bounded,
      int maxIterations, Runnable timeCheck) {
    this.n = n;
    this.objective = objective;
    this.linearRows = linearRows;
    this.equations = equations;
    this.inequalities = inequalities;
    this.bounded = bounded;
    this.maxIterations = maxIterations;
    this.timeCheck = timeCheck;
  }

  /**
   * Search for a local minimum from <code>start</code>.
   *
   * @return <code>null</code> if the equations are inconsistent or have no solution near the
   *         start, or no point strictly inside the inequalities is found
   */
  public Result minimize(double[] start) {
    if (!prepare(start)) {
      return null;
    }
    double[] s = new double[search.length];
    for (int k = 0; k < search.length; k++) {
      s[k] = start[free[search[k]]];
    }
    if (point(s) == null) {
      return null;
    }
    if (!(phi(s) < Double.POSITIVE_INFINITY)) {
      s = phaseOne(s);
      if (s == null || !(phi(s) < Double.POSITIVE_INFINITY)) {
        return null;
      }
    }
    List<double[]> steps = new ArrayList<double[]>();
    steps.add(point(s));
    boolean converged = true;
    if (s.length > 0) {
      converged = bfgs(s, steps);
    }
    double[] x = point(s);
    if (x == null) {
      return null;
    }
    double value = objective.value(x);
    if (!Double.isFinite(value)) {
      return null;
    }
    double[] gradient = s.length > 0 ? gradient(s) : new double[0];
    boolean stationary = true;
    for (double g : gradient) {
      stationary &= Math.abs(g) <= 1e-6 * Math.max(1.0, Math.abs(value));
    }
    if (!stationary) {
      // the search stopped at an inequality which keeps the objective from decreasing further
      Result active = activeSet(x, value);
      if (active != null) {
        return new Result(active.point, active.value, false, active.converged, steps);
      }
    }
    return new Result(x, value, stationary, converged, steps);
  }

  /**
   * Impose the inequality with the smallest value at the point found as an equation, if it is
   * active there, and search again from the point.
   *
   * @return <code>null</code> if no inequality is active or the search with the equation doesn't
   *         find a value which is as good
   */
  private Result activeSet(double[] x, double value) {
    double scale = 1.0;
    for (double xi : x) {
      scale = Math.max(scale, Math.abs(xi));
    }
    int active = -1;
    double smallest = ACTIVE_TOLERANCE * scale;
    for (int k = 0; k < inequalities.size(); k++) {
      double g = inequalities.get(k).function.value(x);
      if (g < smallest) {
        smallest = g;
        active = k;
      }
    }
    if (active < 0) {
      return null;
    }
    Inequality inequality = inequalities.get(active);
    List<Inequality> otherInequalities = new ArrayList<Inequality>(inequalities);
    otherInequalities.remove(active);
    List<double[]> rows = new ArrayList<double[]>(linearRows);
    List<Function> otherEquations = new ArrayList<Function>(equations);
    if (inequality.coefficients != null) {
      double[] row = Arrays.copyOf(inequality.coefficients, n + 1);
      row[n] = -inequality.constant;
      int variable = singleVariable(row);
      if (variable >= 0) {
        // the bound of one variable; a strict bound is kept by the nearest point inside
        double bound = row[n] / row[variable];
        if (inequality.strict) {
          bound += Math.signum(row[variable]) * 2 * Math.ulp(bound);
        }
        row = new double[n + 1];
        row[variable] = 1.0;
        row[n] = bound;
      }
      rows.add(row);
    } else {
      otherEquations.add(inequality.function);
    }
    Result result = new LocalConstrainedMinimizer(n, objective, rows, otherEquations,
        otherInequalities, bounded, maxIterations, timeCheck).minimize(x);
    if (result == null
        || !(result.value <= value + 1e-9 * Math.max(1.0, Math.abs(value)))) {
      return null;
    }
    return result;
  }

  /** The variable of a row with exactly one nonzero coefficient, or <code>-1</code>. */
  private int singleVariable(double[] row) {
    int variable = -1;
    for (int i = 0; i < n; i++) {
      if (row[i] != 0.0) {
        if (variable >= 0) {
          return -1;
        }
        variable = i;
      }
    }
    return variable;
  }

  /**
   * The reduced row echelon form of the linear equations and the choice of the variables the
   * other equations determine.
   *
   * @return <code>false</code> if the linear equations are inconsistent or the Jacobian matrix of
   *         the other equations has not full rank at the start
   */
  private boolean prepare(double[] start) {
    rref = new double[linearRows.size()][];
    for (int i = 0; i < rref.length; i++) {
      rref[i] = linearRows.get(i).clone();
    }
    boolean[] isPivot = new boolean[n];
    List<Integer> pivotList = new ArrayList<Integer>();
    int rank = 0;
    for (int i = 0; i < rref.length; i++) {
      // the column with the largest weighted coefficient; bounded variables are taken last
      int column = -1;
      double best = 0.0;
      double max = 0.0;
      for (int c = 0; c < n; c++) {
        max = Math.max(max, Math.abs(rref[i][c]));
      }
      for (int c = 0; c < n; c++) {
        double weight = Math.abs(rref[i][c]) * (bounded[c] ? 1e-3 : 1.0);
        if (!isPivot[c] && Math.abs(rref[i][c]) > 1e-12 * max && weight > best) {
          best = weight;
          column = c;
        }
      }
      if (column < 0) {
        if (Math.abs(rref[i][n]) > EQUATION_TOLERANCE * Math.max(1.0, max)) {
          return false;
        }
        continue;
      }
      double pivot = rref[i][column];
      for (int c = 0; c <= n; c++) {
        rref[i][c] /= pivot;
      }
      for (int r = 0; r < rref.length; r++) {
        if (r != i && rref[r][column] != 0.0) {
          double factor = rref[r][column];
          for (int c = 0; c <= n; c++) {
            rref[r][c] -= factor * rref[i][c];
          }
          rref[r][column] = 0.0;
        }
      }
      // keep the rows with a pivot in front
      double[] swap = rref[rank];
      rref[rank] = rref[i];
      rref[i] = swap;
      rank++;
      isPivot[column] = true;
      pivotList.add(column);
    }
    rref = Arrays.copyOf(rref, rank);
    pivots = pivotList.stream().mapToInt(Integer::intValue).toArray();
    free = new int[n - rank];
    for (int c = 0, k = 0; c < n; c++) {
      if (!isPivot[c]) {
        free[k++] = c;
      }
    }
    final int equationCount = equations.size();
    if (equationCount > free.length) {
      return false;
    }
    dependent = new int[equationCount];
    anchor = new double[equationCount];
    if (equationCount > 0) {
      double[] w = new double[free.length];
      for (int k = 0; k < free.length; k++) {
        w[k] = start[free[k]];
      }
      double[] x = linearPoint(w);
      double[][] jacobian = new double[equationCount][];
      for (int j = 0; j < equationCount; j++) {
        double[] gx = gradient(equations.get(j), x);
        jacobian[j] = new double[free.length];
        for (int k = 0; k < free.length; k++) {
          jacobian[j][k] = reduce(gx, free[k]);
        }
      }
      boolean[] used = new boolean[free.length];
      for (int j = 0; j < equationCount; j++) {
        // the variable the equation depends on most strongly, bounded ones taken last
        int best = -1;
        double bestWeight = 0.0;
        for (int k = 0; k < free.length; k++) {
          double weight = Math.abs(jacobian[j][k]) * (bounded[free[k]] ? 1e-3 : 1.0);
          if (!used[k] && Math.abs(jacobian[j][k]) > 1e-12 && weight > bestWeight) {
            best = k;
            bestWeight = weight;
          }
        }
        if (best < 0) {
          return false;
        }
        for (int r = j + 1; r < equationCount; r++) {
          double factor = jacobian[r][best] / jacobian[j][best];
          for (int k = 0; k < free.length; k++) {
            jacobian[r][k] -= factor * jacobian[j][k];
          }
        }
        used[best] = true;
        dependent[j] = best;
        anchor[j] = w[best];
      }
    }
    search = new int[free.length - equationCount];
    for (int k = 0, m = 0; k < free.length; k++) {
      boolean isDependent = false;
      for (int d : dependent) {
        isDependent |= d == k;
      }
      if (!isDependent) {
        search[m++] = k;
      }
    }
    return true;
  }

  /** The point with the free variables <code>w</code>, the pivot variables following. */
  private double[] linearPoint(double[] w) {
    double[] x = new double[n];
    for (int k = 0; k < free.length; k++) {
      x[free[k]] = w[k];
    }
    for (int r = 0; r < pivots.length; r++) {
      double v = rref[r][n];
      for (int c : free) {
        v -= rref[r][c] * x[c];
      }
      x[pivots[r]] = v;
    }
    return x;
  }

  /**
   * The derivative along the free variable <code>column</code>, the pivot variables moving with
   * it, of a function with the gradient <code>gx</code>.
   */
  private double reduce(double[] gx, int column) {
    double v = gx[column];
    for (int r = 0; r < pivots.length; r++) {
      v -= rref[r][column] * gx[pivots[r]];
    }
    return v;
  }

  /** The point of the search variables <code>s</code>, starting Newton's method at the anchor. */
  private double[] point(double[] s) {
    return point(s, anchor.clone());
  }

  /**
   * The point of the search variables <code>s</code>; the dependent variables are solved for by
   * Newton's method from the values in <code>y</code>, which are replaced by the solution.
   *
   * @return <code>null</code> if Newton's method doesn't converge
   */
  private double[] point(double[] s, double[] y) {
    double[] w = new double[free.length];
    for (int k = 0; k < search.length; k++) {
      w[search[k]] = s[k];
    }
    final int m = dependent.length;
    for (int iteration = 0; iteration < 100; iteration++) {
      for (int k = 0; k < m; k++) {
        w[dependent[k]] = y[k];
      }
      double[] x = linearPoint(w);
      if (m == 0) {
        return x;
      }
      double[][] jacobian = new double[m][m];
      double[] residual = new double[m];
      double size = 0.0;
      for (int j = 0; j < m; j++) {
        Function h = equations.get(j);
        residual[j] = -h.value(x);
        size = Math.max(size, Math.abs(residual[j]));
        double[] gx = gradient(h, x);
        for (int k = 0; k < m; k++) {
          jacobian[j][k] = reduce(gx, free[dependent[k]]);
        }
      }
      double[] step = solve(jacobian, residual);
      if (step == null) {
        return null;
      }
      boolean small = true;
      for (int k = 0; k < m; k++) {
        if (!Double.isFinite(step[k])) {
          return null;
        }
        small &= Math.abs(step[k]) <= 1e-14 * Math.max(1.0, Math.abs(y[k]));
        y[k] += step[k];
      }
      if (small || size == 0.0) {
        for (int k = 0; k < m; k++) {
          w[dependent[k]] = y[k];
        }
        x = linearPoint(w);
        for (Function h : equations) {
          if (!(Math.abs(h.value(x)) <= EQUATION_TOLERANCE)) {
            return null;
          }
        }
        return x;
      }
    }
    return null;
  }

  /**
   * The value of the objective at the point of the search variables <code>s</code>, or
   * <code>+Infinity</code> if the point is not strictly inside the inequalities.
   */
  private double phi(double[] s) {
    double[] x = point(s);
    if (x == null || !inside(x)) {
      return Double.POSITIVE_INFINITY;
    }
    double value = objective.value(x);
    return Double.isFinite(value) ? value : Double.POSITIVE_INFINITY;
  }

  private boolean inside(double[] x) {
    for (Inequality inequality : inequalities) {
      if (!(inequality.function.value(x) > 0.0)) {
        return false;
      }
    }
    return true;
  }

  /**
   * The gradient of {@link #phi} in the search variables: from the gradients of the objective and
   * the equations by the implicit function theorem, or by central differences.
   */
  private double[] gradient(double[] s) {
    double[] x = point(s);
    double[] gx = x == null ? null : objective.gradient(x);
    if (gx != null && isFinite(gx)) {
      double[] g = new double[search.length];
      for (int k = 0; k < search.length; k++) {
        g[k] = reduce(gx, free[search[k]]);
      }
      final int m = dependent.length;
      if (m == 0) {
        return g;
      }
      // the multipliers solve J_y^T lambda = grad_y f, the gradient along a variable of the
      // search is its own partial derivative less lambda . J_s
      double[][] hx = new double[m][];
      double[][] transposed = new double[m][m];
      double[] gy = new double[m];
      for (int j = 0; j < m; j++) {
        hx[j] = gradient(equations.get(j), x);
      }
      for (int k = 0; k < m; k++) {
        for (int j = 0; j < m; j++) {
          transposed[k][j] = reduce(hx[j], free[dependent[k]]);
        }
        gy[k] = reduce(gx, free[dependent[k]]);
      }
      double[] lambda = solve(transposed, gy);
      if (lambda != null) {
        for (int k = 0; k < search.length; k++) {
          for (int j = 0; j < m; j++) {
            g[k] -= lambda[j] * reduce(hx[j], free[search[k]]);
          }
        }
        if (isFinite(g)) {
          return g;
        }
      }
    }
    double[] g = new double[search.length];
    double value = phi(s);
    for (int k = 0; k < s.length; k++) {
      double h = 6e-6 * Math.max(1.0, Math.abs(s[k]));
      double[] p = s.clone();
      p[k] = s[k] + h;
      double plus = phi(p);
      p[k] = s[k] - h;
      double minus = phi(p);
      if (Double.isFinite(plus) && Double.isFinite(minus)) {
        g[k] = (plus - minus) / (2 * h);
      } else if (Double.isFinite(plus)) {
        g[k] = (plus - value) / h;
      } else {
        g[k] = (value - minus) / h;
      }
    }
    return g;
  }

  /** The gradient of a function of the point, approximated by central differences if unknown. */
  private double[] gradient(Function function, double[] x) {
    double[] g = function.gradient(x);
    if (g != null && isFinite(g)) {
      return g;
    }
    g = new double[n];
    for (int i = 0; i < n; i++) {
      double h = 6e-6 * Math.max(1.0, Math.abs(x[i]));
      double[] p = x.clone();
      p[i] = x[i] + h;
      double plus = function.value(p);
      p[i] = x[i] - h;
      g[i] = (plus - function.value(p)) / (2 * h);
    }
    return g;
  }

  /**
   * The BFGS method with a backtracking line search which rejects the points outside of the
   * inequalities. <code>s</code> is replaced by the point found.
   *
   * @return <code>false</code> if the iterations ran out
   */
  private boolean bfgs(double[] s, List<double[]> steps) {
    final int m = s.length;
    double[][] inverse = identity(m);
    double value = phi(s);
    double[] g = gradient(s);
    boolean first = true;
    for (int iteration = 0; iteration < maxIterations; iteration++) {
      timeCheck.run();
      if (!isFinite(g) || norm(g) <= 1e-14 * Math.max(1.0, Math.abs(value))) {
        return true;
      }
      double[] d = new double[m];
      double slope = 0.0;
      for (int i = 0; i < m; i++) {
        for (int j = 0; j < m; j++) {
          d[i] -= inverse[i][j] * g[j];
        }
        slope += d[i] * g[i];
      }
      if (!(slope < 0.0)) {
        inverse = identity(m);
        first = true;
        slope = 0.0;
        for (int i = 0; i < m; i++) {
          d[i] = -g[i];
          slope -= g[i] * g[i];
        }
      }
      if (first) {
        // the first step goes no further than the size of the point
        double limit = Math.max(1.0, norm(s)) / Math.max(norm(d), Double.MIN_NORMAL);
        if (limit < 1.0) {
          for (int i = 0; i < m; i++) {
            d[i] *= limit;
          }
          slope *= limit;
        }
      }
      double alpha = 1.0;
      double[] next = new double[m];
      double nextValue = Double.POSITIVE_INFINITY;
      boolean accepted = false;
      for (int halvings = 0; halvings < 60; halvings++) {
        for (int i = 0; i < m; i++) {
          next[i] = s[i] + alpha * d[i];
        }
        nextValue = phi(next);
        if (nextValue <= value + 1e-4 * alpha * slope && nextValue < Double.POSITIVE_INFINITY) {
          accepted = true;
          break;
        }
        alpha *= 0.5;
      }
      if (!accepted || Arrays.equals(next, s)) {
        return true;
      }
      // the dependent variables of the new point are the start of the next Newton iterations
      double[] y = anchor.clone();
      double[] x = point(next, y);
      if (x == null) {
        return true;
      }
      anchor = y;
      steps.add(x);
      double[] nextGradient = gradient(next);
      double[] ds = new double[m];
      double[] dg = new double[m];
      double sy = 0.0;
      double yy = 0.0;
      for (int i = 0; i < m; i++) {
        ds[i] = next[i] - s[i];
        dg[i] = nextGradient[i] - g[i];
        sy += ds[i] * dg[i];
        yy += dg[i] * dg[i];
      }
      if (sy > 1e-14 * norm(ds) * Math.sqrt(yy)) {
        if (first) {
          inverse = identity(m);
          for (int i = 0; i < m; i++) {
            inverse[i][i] = sy / yy;
          }
        }
        updateInverse(inverse, ds, dg, sy);
        first = false;
      }
      double change = Math.abs(value - nextValue);
      System.arraycopy(next, 0, s, 0, m);
      g = nextGradient;
      double stepSize = 0.0;
      for (int i = 0; i < m; i++) {
        stepSize = Math.max(stepSize, Math.abs(ds[i]) / Math.max(1.0, Math.abs(s[i])));
      }
      value = nextValue;
      if (stepSize <= 1e-15 || (stepSize <= 1e-10 && change <= 1e-16 * Math.abs(value))) {
        return true;
      }
    }
    return false;
  }

  /** The BFGS update of the inverse Hessian matrix approximation <code>h</code>. */
  private static void updateInverse(double[][] h, double[] s, double[] y, double sy) {
    final int m = s.length;
    double[] hy = new double[m];
    double yhy = 0.0;
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < m; j++) {
        hy[i] += h[i][j] * y[j];
      }
      yhy += y[i] * hy[i];
    }
    double factor = (sy + yhy) / (sy * sy);
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < m; j++) {
        h[i][j] += factor * s[i] * s[j] - (hy[i] * s[j] + s[i] * hy[j]) / sy;
      }
    }
  }

  /**
   * Move the search variables to a point strictly inside all inequalities by Gauss-Newton steps on
   * the violated ones.
   *
   * @return <code>null</code> if no such point is found
   */
  private double[] phaseOne(double[] s) {
    final int m = s.length;
    if (m == 0) {
      return null;
    }
    double violation = violation(s);
    for (int iteration = 0; iteration < 100; iteration++) {
      timeCheck.run();
      if (violation == 0.0) {
        return s;
      }
      if (!Double.isFinite(violation)) {
        return null;
      }
      double[] x = point(s);
      List<double[]> rows = new ArrayList<double[]>();
      List<Double> targets = new ArrayList<Double>();
      for (Inequality inequality : inequalities) {
        double g = inequality.function.value(x);
        if (!(g >= INSIDE_MARGIN)) {
          double[] row = new double[m];
          for (int k = 0; k < m; k++) {
            double h = 1e-7 * Math.max(1.0, Math.abs(s[k]));
            double[] p = s.clone();
            p[k] += h;
            double[] xp = point(p);
            row[k] = xp == null ? Double.NaN : (inequality.function.value(xp) - g) / h;
          }
          rows.add(row);
          targets.add(2 * INSIDE_MARGIN - g);
        }
      }
      // the step of minimal length which moves the violated inequalities to the targets
      final int r = rows.size();
      double[][] normal = new double[r][r];
      double[] rhs = new double[r];
      for (int i = 0; i < r; i++) {
        for (int j = 0; j < r; j++) {
          for (int k = 0; k < m; k++) {
            normal[i][j] += rows.get(i)[k] * rows.get(j)[k];
          }
        }
        normal[i][i] *= 1.0 + 1e-12;
        rhs[i] = targets.get(i);
      }
      double[] multipliers = solve(normal, rhs);
      if (multipliers == null) {
        return null;
      }
      double[] d = new double[m];
      for (int i = 0; i < r; i++) {
        for (int k = 0; k < m; k++) {
          d[k] += multipliers[i] * rows.get(i)[k];
        }
      }
      if (!isFinite(d)) {
        return null;
      }
      double alpha = 1.0;
      boolean accepted = false;
      for (int halvings = 0; halvings < 40; halvings++) {
        double[] next = s.clone();
        for (int k = 0; k < m; k++) {
          next[k] += alpha * d[k];
        }
        double nextViolation = violation(next);
        if (nextViolation < violation) {
          s = next;
          violation = nextViolation;
          accepted = true;
          break;
        }
        alpha *= 0.5;
      }
      if (!accepted) {
        return null;
      }
    }
    return violation == 0.0 ? s : null;
  }

  /**
   * The sum of the squares of the amounts by which the inequalities fall short of a small margin
   * at the point of the search variables <code>s</code>.
   */
  private double violation(double[] s) {
    double[] x = point(s);
    if (x == null) {
      return Double.POSITIVE_INFINITY;
    }
    double sum = 0.0;
    for (Inequality inequality : inequalities) {
      double g = inequality.function.value(x);
      if (Double.isNaN(g)) {
        return Double.POSITIVE_INFINITY;
      }
      if (g < INSIDE_MARGIN) {
        sum += (INSIDE_MARGIN - g) * (INSIDE_MARGIN - g);
      }
    }
    return sum;
  }

  /**
   * Solve the linear system by Gaussian elimination with partial pivoting.
   *
   * @return <code>null</code> if the matrix is singular
   */
  private static double[] solve(double[][] matrix, double[] rhs) {
    final int m = rhs.length;
    double[][] a = new double[m][];
    for (int i = 0; i < m; i++) {
      a[i] = Arrays.copyOf(matrix[i], m + 1);
      a[i][m] = rhs[i];
    }
    for (int c = 0; c < m; c++) {
      int best = c;
      for (int r = c + 1; r < m; r++) {
        if (Math.abs(a[r][c]) > Math.abs(a[best][c])) {
          best = r;
        }
      }
      if (!(Math.abs(a[best][c]) > 0.0)) {
        return null;
      }
      double[] swap = a[c];
      a[c] = a[best];
      a[best] = swap;
      for (int r = c + 1; r < m; r++) {
        double factor = a[r][c] / a[c][c];
        for (int k = c; k <= m; k++) {
          a[r][k] -= factor * a[c][k];
        }
      }
    }
    double[] x = new double[m];
    for (int r = m - 1; r >= 0; r--) {
      double v = a[r][m];
      for (int k = r + 1; k < m; k++) {
        v -= a[r][k] * x[k];
      }
      x[r] = v / a[r][r];
    }
    return x;
  }

  private static double[][] identity(int m) {
    double[][] result = new double[m][m];
    for (int i = 0; i < m; i++) {
      result[i][i] = 1.0;
    }
    return result;
  }

  private static double norm(double[] v) {
    double sum = 0.0;
    for (double d : v) {
      sum += d * d;
    }
    return Math.sqrt(sum);
  }

  private static boolean isFinite(double[] v) {
    for (double d : v) {
      if (!Double.isFinite(d)) {
        return false;
      }
    }
    return true;
  }
}
