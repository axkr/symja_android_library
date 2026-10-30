package org.matheclipse.core.reflection.system;

import java.util.Set;
import java.util.TreeSet;
import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.analysis.solvers.AllowedSolution;
import org.hipparchus.analysis.solvers.BracketingNthOrderBrentSolver;
import org.hipparchus.exception.MathRuntimeException;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.Solve.SolveData;
import org.matheclipse.core.eval.util.SolveUtils;

/** Try to solve a set of equations (i.e. <code>Equal[...]</code> expressions) numerically. */
public class NSolve extends AbstractFunctionOptionEvaluator {
  public NSolve() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    return solveWithOptions(ast, argSize, options, engine);
  }

  /**
   * Solve the equations of an <code>NSolve(...)</code> / <code>NSolveValues(...)</code> call
   * numerically with the working precision of its options or of its fourth argument.
   *
   * @param ast the call with its options
   * @param argSize the number of arguments without options
   * @param options the option values of the call
   * @param engine the evaluation engine
   * @return the list of solution lists or {@link F#NIL}
   */
  static IExpr solveWithOptions(IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine) {
    if (argSize > 0 && argSize < ast.argSize()) {
      ast = ast.copyUntil(argSize + 1);
    }
    SolveOptions solveOptions = SolveOptions.of(SolveOptions.NSOLVE_KEYS, options);
    long precision = SolveUtils.workingPrecision(ast, solveOptions.workingPrecision(), engine);
    if (precision == SolveUtils.INVALID_PRECISION) {
      return F.NIL;
    }
    if (ast.size() == 5) {
      // the working precision was given as the fourth argument
      ast = ast.copyUntil(4);
    }
    return solveNumeric(solveOptions, ast, precision, engine);
  }

  /**
   * Solve the equations numerically, if requested with a working precision beyond machine
   * precision.
   *
   * <p>
   * A machine number carries about 16 significant digits, so a higher working precision cannot be
   * served by the numerical polynomial solver: the equations are solved exactly and the exact
   * solutions - <code>Sqrt(2)</code>, or an inert <code>Root</code> object for a polynomial which
   * has no closed radical form - are then evaluated to the requested number of digits. Equations
   * which have no exact solution fall back to the machine precision solution.
   *
   * @param options the option values of the <code>NSolve(...)</code> /
   *        <code>NSolveValues(...)</code> call
   * @param ast the <code>NSolve(...)</code> / <code>NSolveValues(...)</code> ast without the
   *        working precision argument
   * @param precision the number of significant digits or {@link Solve#MACHINE_PRECISION_REQUESTED}
   * @param engine the evaluation engine
   * @return the list of solution lists
   */
  static IExpr solveNumeric(SolveOptions options, IAST ast, long precision, EvalEngine engine) {
    if (precision == SolveUtils.MACHINE_PRECISION_REQUESTED) {
      IExpr bounded = rootsInBoundedInterval(ast, engine);
      if (bounded.isPresent()) {
        return bounded;
      }
      SolveData solveData = new SolveData(options);
      IAST radicalSystem = realRadicalSystem(ast, engine);
      IExpr result;
      if (radicalSystem.isPresent()) {
        // the elimination often fails on such a system: its message only if the search fails too
        boolean quiet = engine.isQuietMode();
        engine.setQuietMode(true);
        try {
          result = solveData.of(ast, true, true, engine);
        } finally {
          engine.setQuietMode(quiet);
        }
        if (!result.isListOfLists()) {
          IExpr roots = multiStartRealRoots(radicalSystem, (IAST) ast.arg2(), engine);
          if (roots.isPresent()) {
            return roots;
          }
          if (result.isNIL()) {
            // The system cannot be solved with the methods available to NSolve.
            return Errors.printMessage(ast.topHead(), "nsmet", F.list(ast.topHead()), engine);
          }
        }
      } else {
        result = solveData.of(ast, true, true, engine);
      }
      if (!solveData.isCompleteSolutions()) {
        result = Solve.withRationalizedSolutions(ast, result, true, false, engine);
      }
      return distinctSolutions(result,
          ast.argSize() >= 2 ? ast.arg2().makeList() : F.CEmptyList);
    }
    IExpr result = new SolveData(options).of(ast, false, true, engine);
    if (result.isNIL()) {
      result = new SolveData(options).of(ast, true, true, engine);
    }
    if (result.isNIL()) {
      return result;
    }
    return Solve.sortNumericSolutions(Solve.withNumericValues(result,
        value -> engine.evaluate(F.N(value, precision))));
  }

  /** The number of start points per variable of {@link #multiStartRealRoots}. */
  private static final int[] GRID_POINTS = {0, 0, 16, 8};

  /**
   * A system of as many equations as variables, two or three of them, whose functions are built
   * from the variables and real numbers by sums, products and powers with rational exponents, one
   * of them a radical.
   *
   * @return the functions <code>lhs - rhs</code> of the equations, or {@link F#NIL} if the system
   *         isn't of this kind
   */
  private static IAST realRadicalSystem(IAST ast, EvalEngine engine) {
    if (ast.argSize() < 2 || ast.argSize() > 3 || (ast.argSize() == 3 && ast.arg3() != S.Reals)
        || !ast.arg2().isList()) {
      return F.NIL;
    }
    final IAST variables = (IAST) ast.arg2();
    final int n = variables.argSize();
    if (n < 2 || n >= GRID_POINTS.length || !variables.forAll(x -> x.isSymbol())) {
      return F.NIL;
    }
    IExpr system = ast.arg1();
    if (!system.isList() || system.argSize() != n) {
      return F.NIL;
    }
    IASTAppendable functions = F.ListAlloc(n);
    boolean[] radical = new boolean[1];
    for (IExpr equation : (IAST) system) {
      if (!equation.isEqual()) {
        return F.NIL;
      }
      IExpr function = engine.evaluate(F.Subtract(equation.first(), equation.second()));
      if (!isRadicalExpression(function, variables, radical)) {
        return F.NIL;
      }
      functions.append(function);
    }
    return radical[0] ? functions : F.NIL;
  }

  /**
   * Whether the expression is built from the variables and real numbers by sums, products and
   * powers with rational exponents; <code>radical[0]</code> is set if a variable is under a
   * radical.
   */
  private static boolean isRadicalExpression(IExpr expr, IAST variables, boolean[] radical) {
    if (expr.isReal()) {
      return true;
    }
    if (expr.isSymbol()) {
      return variables.contains(expr);
    }
    if (expr.isPlus() || expr.isTimes()) {
      return ((IAST) expr).forAll(x -> isRadicalExpression(x, variables, radical));
    }
    if (expr.isPower() && expr.exponent().isRational()) {
      if (!expr.exponent().isInteger() && !expr.base().isFree(x -> variables.contains(x), true)) {
        radical[0] = true;
      }
      return isRadicalExpression(expr.base(), variables, radical);
    }
    return false;
  }

  /**
   * The real roots of a system of radical equations by Newton's method from the points of a grid
   * in a box, which is twice as large as the largest number of the system. Each root found is
   * refined until the residuals vanish to machine precision, and the distinct ones are the
   * solutions. The search is a completion for the systems the elimination doesn't solve; a root
   * outside the box may be missed.
   *
   * @param functions the functions of the equations <code>f == 0</code>
   * @return the solutions or {@link F#NIL} if no root is found
   */
  private static IExpr multiStartRealRoots(IAST functions, IAST variables, EvalEngine engine) {
    final int n = variables.argSize();
    final org.matheclipse.core.generic.MultiVariateNumerical[] f =
        new org.matheclipse.core.generic.MultiVariateNumerical[n];
    final org.matheclipse.core.generic.MultiVariateNumerical[][] jacobian =
        new org.matheclipse.core.generic.MultiVariateNumerical[n][n];
    double[] largest = {1.0};
    for (int i = 0; i < n; i++) {
      IExpr function = functions.get(i + 1);
      function.isFree(x -> {
        if (x.isReal()) {
          largest[0] = Math.max(largest[0], Math.abs(x.evalf()));
        }
        return false;
      }, false);
      f[i] = new org.matheclipse.core.generic.MultiVariateNumerical(function, variables);
      for (int j = 0; j < n; j++) {
        jacobian[i][j] = new org.matheclipse.core.generic.MultiVariateNumerical(
            engine.evaluate(F.D(function, variables.get(j + 1))), variables);
      }
    }
    final double box = 2.0 * largest[0];
    final int grid = GRID_POINTS[n];
    java.util.List<double[]> roots = new java.util.ArrayList<double[]>();
    int[] index = new int[n];
    for (int start = 0; start < Math.pow(grid, n); start++) {
      engine.checkTimeLimit();
      double[] x = new double[n];
      for (int k = 0, rest = start; k < n; k++, rest /= grid) {
        index[k] = rest % grid;
        // the points of the grid are off the symmetry planes of the system
        x[k] = -box + (2 * box) * (index[k] + 0.5) / grid + 1.0e-3 * box * (k + 1) / grid;
      }
      double[] root = newton(f, jacobian, x, box);
      if (root == null) {
        continue;
      }
      boolean known = false;
      for (double[] other : roots) {
        double distance = 0.0;
        for (int k = 0; k < n; k++) {
          distance = Math.max(distance, Math.abs(other[k] - root[k]));
        }
        known |= distance <= 1.0e-7 * Math.max(1.0, box);
      }
      if (!known) {
        roots.add(root);
      }
    }
    if (roots.isEmpty()) {
      return F.NIL;
    }
    roots.sort((a, b) -> {
      for (int k = 0; k < n; k++) {
        int c = Double.compare(a[k], b[k]);
        if (c != 0) {
          return c;
        }
      }
      return 0;
    });
    IASTAppendable solutions = F.ListAlloc(roots.size());
    for (double[] root : roots) {
      solutions.append(F.mapRange(1, n + 1, k -> F.Rule(variables.get(k),
          F.num(Math.abs(root[k - 1]) < 1.0e-12 * box ? 0.0 : root[k - 1]))));
    }
    return solutions;
  }

  /**
   * Newton's method with halved steps where the residuals don't decrease.
   *
   * @return the root, or <code>null</code> if the iteration doesn't converge to a point where the
   *         residuals vanish
   */
  private static double[] newton(org.matheclipse.core.generic.MultiVariateNumerical[] f,
      org.matheclipse.core.generic.MultiVariateNumerical[][] jacobian, double[] x, double box) {
    final int n = x.length;
    double[] value = values(f, x);
    double norm = norm(value);
    for (int iteration = 0; iteration < 100; iteration++) {
      if (!Double.isFinite(norm)) {
        return null;
      }
      double[][] matrix = new double[n][n];
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          matrix[i][j] = jacobian[i][j].value(x);
        }
      }
      double[] step;
      try {
        step = new org.hipparchus.linear.LUDecomposition(
            new org.hipparchus.linear.Array2DRowRealMatrix(matrix, false)).getSolver()
                .solve(new org.hipparchus.linear.ArrayRealVector(value, false)).toArray();
      } catch (MathRuntimeException mre) {
        return null;
      }
      double factor = 1.0;
      double[] next = new double[n];
      double nextNorm = Double.NaN;
      for (int halvings = 0; halvings < 30; halvings++) {
        for (int k = 0; k < n; k++) {
          next[k] = x[k] - factor * step[k];
        }
        nextNorm = norm(values(f, next));
        if (nextNorm < norm || nextNorm == 0.0) {
          break;
        }
        factor *= 0.5;
      }
      if (!(nextNorm < norm || nextNorm == 0.0)) {
        // no step decreases the residuals: a root if they vanish to machine precision
        return norm <= 1.0e-10 ? x : null;
      }
      double stepSize = 0.0;
      double size = 1.0;
      for (int k = 0; k < n; k++) {
        stepSize = Math.max(stepSize, Math.abs(next[k] - x[k]));
        size = Math.max(size, Math.abs(next[k]));
      }
      x = next.clone();
      value = values(f, x);
      norm = nextNorm;
      if (size > 10.0 * box) {
        return null;
      }
      if (stepSize <= 1.0e-14 * size && norm <= 1.0e-10) {
        return x;
      }
    }
    return norm <= 1.0e-10 ? x : null;
  }

  private static double[] values(org.matheclipse.core.generic.MultiVariateNumerical[] f,
      double[] x) {
    double[] result = new double[f.length];
    for (int i = 0; i < f.length; i++) {
      result[i] = f[i].value(x);
    }
    return result;
  }

  private static double norm(double[] values) {
    double sum = 0.0;
    for (double v : values) {
      sum += v * v;
    }
    return Math.sqrt(sum);
  }

  /** The first number of equidistant samples of the sign change search in a bounded interval. */
  private static final int INTERVAL_SAMPLES = 2000;

  /**
   * The most samples of the search in one interval: the samples are doubled until two searches
   * agree on the number of roots, and without agreement up to this number the system is left to
   * the other methods.
   */
  private static final int MAX_INTERVAL_SAMPLES = INTERVAL_SAMPLES << 7;

  /** The relative size of a function value which counts as zero at a refined root. */
  private static final double ZERO = 1.0e-12;

  /**
   * All real roots of one transcendental equation in one variable whose inequalities restrict the
   * variable to a bounded real interval (or a union of bounded intervals), as in
   * <code>NSolve({Cos(x)==0.3, 0&lt;x&lt;8}, x)</code>. The general solution of such an equation is
   * an infinite family, so the interval is searched numerically instead: the sign changes between
   * equidistant samples are refined by a bracketing root finder, and at a sample whose absolute
   * value is a local minimum the zero of the derivative is a candidate for a root of even
   * multiplicity. Without a variable list the variable is the only variable of the system.
   *
   * @return the solutions sorted ascending, <code>{}</code> if the interval contains no root, or
   *         {@link F#NIL} if the system isn't of this kind
   */
  private static IExpr rootsInBoundedInterval(IAST ast, EvalEngine engine) {
    if (ast.argSize() > 3 || (ast.argSize() == 3 && ast.arg3() != S.Reals)) {
      return F.NIL;
    }
    IExpr system = ast.arg1();
    if (!system.isList() && !system.isAnd()) {
      return F.NIL;
    }
    IExpr equation = F.NIL;
    IASTAppendable conditions = F.ast(S.And, system.argSize());
    for (IExpr term : (IAST) system) {
      if (term.isEqual()) {
        if (equation.isPresent()) {
          return F.NIL;
        }
        equation = term;
      } else {
        conditions.append(term);
      }
    }
    if (equation.isNIL() || conditions.argSize() == 0) {
      return F.NIL;
    }
    IAST variables = ast.argSize() >= 2 ? ast.arg2().makeList()
        : new VariablesSet(system).getVarList().removeIf(v -> v.isBuiltInSymbol());
    if (variables.argSize() != 1 || !variables.arg1().isVariable()
        || variables.arg1().isBuiltInSymbol()) {
      return F.NIL;
    }
    ISymbol x = (ISymbol) variables.arg1();
    IExpr f = engine.evaluate(F.Subtract(equation.first(), equation.second()));
    if (!f.isNumericFunction(x) || f.isFree(x) || isAlgebraic(f, x)) {
      return F.NIL;
    }
    IAST intervalData = IntervalDataSym.toIntervalData(conditions, x, engine, false);
    if (intervalData.isNIL() || !intervalData.isAST(S.IntervalData)) {
      return F.NIL;
    }
    double[][] pieces = new double[intervalData.argSize()][];
    for (int i = 1; i < intervalData.size(); i++) {
      IExpr piece = intervalData.get(i);
      if (!piece.isList4()) {
        return F.NIL;
      }
      double lower = piece.first().evalfNaN();
      double upper = piece.last().evalfNaN();
      if (!Double.isFinite(lower) || !Double.isFinite(upper) || lower > upper) {
        return F.NIL;
      }
      pieces[i - 1] = new double[] {lower, upper, piece.second() == S.LessEqual ? 1.0 : 0.0,
          piece.get(3) == S.LessEqual ? 1.0 : 0.0};
    }
    UnaryNumerical function = new UnaryNumerical(f, x, Double.NaN, engine);
    TreeSet<Double> roots = new TreeSet<Double>();
    for (double[] piece : pieces) {
      // a root pair closer than the sample distance is missed, so the samples are doubled until
      // the number of roots doesn't change
      TreeSet<Double> previousRoots = null;
      for (int samples = INTERVAL_SAMPLES;; samples *= 2) {
        TreeSet<Double> pieceRoots = new TreeSet<Double>();
        rootsInInterval(function, piece[0], piece[1], piece[2] != 0.0, piece[3] != 0.0, samples,
            pieceRoots);
        if (previousRoots != null
            && distinctRoots(previousRoots) == distinctRoots(pieceRoots)) {
          roots.addAll(pieceRoots);
          break;
        }
        if (samples >= MAX_INTERVAL_SAMPLES) {
          return F.NIL;
        }
        previousRoots = pieceRoots;
      }
    }
    IASTAppendable result = F.ListAlloc(roots.size());
    double previous = Double.NaN;
    for (Double root : roots) {
      double r = root.doubleValue();
      if (!isSameRoot(r, previous)) {
        result.append(F.list(F.Rule(x, F.num(r))));
      }
      previous = r;
    }
    return result;
  }

  private static boolean isSameRoot(double r, double previous) {
    return Math.abs(r - previous) <= SAME * Math.max(1.0, Math.abs(r));
  }

  /** The number of roots which are not the same as the root before them. */
  private static int distinctRoots(TreeSet<Double> roots) {
    int count = 0;
    double previous = Double.NaN;
    for (Double root : roots) {
      if (!isSameRoot(root.doubleValue(), previous)) {
        count++;
      }
      previous = root.doubleValue();
    }
    return count;
  }

  /**
   * Test if every subexpression which contains <code>x</code> is a sum, a product or a power with
   * an exponent free of <code>x</code>. The algebraic equations are left to the exact solvers.
   */
  private static boolean isAlgebraic(IExpr expr, IExpr x) {
    if (expr.isFree(x)) {
      return true;
    }
    if (expr.equals(x)) {
      return true;
    }
    if (expr.isPlus() || expr.isTimes()) {
      return ((IAST) expr).forAll(arg -> isAlgebraic(arg, x));
    }
    if (expr.isPower()) {
      return expr.exponent().isFree(x) && isAlgebraic(expr.base(), x);
    }
    return false;
  }

  /**
   * Add the roots of <code>function</code> in the interval from <code>lower</code> to
   * <code>upper</code>.
   */
  private static void rootsInInterval(UnaryNumerical function, double lower, double upper,
      boolean lowerClosed, boolean upperClosed, int samples, Set<Double> roots) {
    double width = (upper - lower) / samples;
    double[] xs = new double[samples + 1];
    double[] ys = new double[samples + 1];
    for (int k = 0; k <= samples; k++) {
      xs[k] = k == samples ? upper : lower + k * width;
      ys[k] = function.value(xs[k]);
    }
    UnivariateFunction derivative = null;
    for (int k = 0; k <= samples; k++) {
      double y = ys[k];
      if (Double.isNaN(y)) {
        continue;
      }
      if (y == 0.0 || ((k == 0 || k == samples)
          && Math.abs(y) <= ZERO * Math.max(1.0, Math.abs(ys[k == 0 ? 1 : samples - 1])))) {
        // a root at a closed end, like Sin(x)==0 at x==2*Pi, needn't be a sign change
        addRoot(xs[k], lower, upper, lowerClosed, upperClosed, roots);
      } else if (k < samples && y * ys[k + 1] < 0.0) {
        double root = refinedRoot(function, xs[k], xs[k + 1]);
        double value = Double.isNaN(root) ? Double.NaN : Math.abs(function.value(root));
        double next = Math.abs(ys[k + 1]);
        // a pole or a jump of the function is a sign change, too
        if (value < 1.0e-3 * Math.min(Math.abs(y), next)
            || value <= ZERO * Math.max(1.0, Math.max(Math.abs(y), next))) {
          addRoot(root, lower, upper, lowerClosed, upperClosed, roots);
        }
      } else if (k > 0 && k < samples && y * ys[k - 1] > 0.0 && y * ys[k + 1] > 0.0
          && Math.abs(y) < Math.abs(ys[k - 1]) && Math.abs(y) < Math.abs(ys[k + 1])) {
        // a local minimum of the absolute value: at the extremum, where the derivative changes
        // its sign, the function touches zero at a root of even multiplicity, or crosses zero
        // twice between the samples
        if (derivative == null) {
          derivative = function.derivative();
        }
        if (derivative.value(xs[k - 1]) * derivative.value(xs[k + 1]) < 0.0) {
          double extremum = refinedRoot(derivative, xs[k - 1], xs[k + 1]);
          if (Double.isNaN(extremum)) {
            continue;
          }
          double value = function.value(extremum);
          if (Math.abs(value) <= ZERO
              * Math.max(1.0, Math.max(Math.abs(ys[k - 1]), Math.abs(ys[k + 1])))) {
            addRoot(extremum, lower, upper, lowerClosed, upperClosed, roots);
          } else if (value * y < 0.0) {
            for (double root : new double[] {refinedRoot(function, xs[k - 1], extremum),
                refinedRoot(function, extremum, xs[k + 1])}) {
              if (!Double.isNaN(root)) {
                addRoot(root, lower, upper, lowerClosed, upperClosed, roots);
              }
            }
          }
        }
      }
    }
  }

  /** The root of <code>function</code> between <code>a</code> and <code>b</code>, or NaN. */
  private static double refinedRoot(UnivariateFunction function, double a, double b) {
    try {
      return new BracketingNthOrderBrentSolver(1.0e-15, 1.0e-15, 0.0, 5).solve(1000, function, a,
          b, AllowedSolution.ANY_SIDE);
    } catch (MathRuntimeException mre) {
      return Double.NaN;
    }
  }

  private static void addRoot(double root, double lower, double upper, boolean lowerClosed,
      boolean upperClosed, Set<Double> roots) {
    if ((root > lower || (lowerClosed && root == lower))
        && (root < upper || (upperClosed && root == upper))) {
      roots.add(root);
    }
  }

  /**
   * The solutions of several variables without the copies which the branches of exact radicals
   * leave behind once their values are machine numbers: an imaginary part which is only rounding
   * noise is dropped, and solutions which agree to working precision are merged. The solutions of
   * one variable keep their multiplicities.
   */
  private static IExpr distinctSolutions(IExpr result, IAST variables) {
    if (!result.isListOfLists() || ((IAST) result).exists(s -> s.argSize() < 2)) {
      return result;
    }
    IAST list = (IAST) result;
    IASTAppendable distinct = F.ListAlloc(list.argSize());
    for (IExpr solution : list) {
      IAST cleaned = inVariableOrder(withoutImaginaryNoise((IAST) solution), variables);
      if (!distinct.exists(other -> sameSolution((IAST) other, cleaned))) {
        distinct.append(cleaned);
      }
    }
    return distinct;
  }

  /** The rules of the solution in the order of the variables they are for. */
  static IAST inVariableOrder(IAST solution, IAST variables) {
    if (variables.argSize() != solution.argSize()) {
      return solution;
    }
    IASTAppendable ordered = F.ListAlloc(solution.argSize());
    for (IExpr variable : variables) {
      IExpr rule = F.NIL;
      for (IExpr r : solution) {
        if (r.isRuleAST() && r.first().equals(variable)) {
          rule = r;
          break;
        }
      }
      if (rule.isNIL()) {
        return solution;
      }
      ordered.append(rule);
    }
    return ordered;
  }

  /** The solution with real values, if all its imaginary parts are rounding noise. */
  static IAST withoutImaginaryNoise(IAST solution) {
    IASTMutable real = solution.copy();
    for (int i = 1; i < solution.size(); i++) {
      IExpr rule = solution.get(i);
      if (!rule.isRuleAST() || !rule.second().isNumber() || !rule.second().isInexactNumber()) {
        return solution;
      }
      INumber value = (INumber) rule.second();
      double re = value.reDoubleValue();
      double im = value.imDoubleValue();
      if (Math.abs(im) > NOISE * Math.max(1.0, Math.abs(re))) {
        return solution;
      }
      real.set(i, F.Rule(rule.first(), F.num(re)));
    }
    return real;
  }

  private static boolean sameSolution(IAST a, IAST b) {
    if (a.argSize() != b.argSize()) {
      return false;
    }
    for (int i = 1; i < a.size(); i++) {
      IExpr ruleA = a.get(i);
      IExpr ruleB = b.get(i);
      if (!ruleA.isRuleAST() || !ruleB.isRuleAST() || !ruleA.first().equals(ruleB.first())
          || !ruleA.second().isNumber() || !ruleB.second().isNumber()) {
        return ruleA.equals(ruleB) && ruleA.isRuleAST();
      }
      INumber x = (INumber) ruleA.second();
      INumber y = (INumber) ruleB.second();
      double distance = Math.hypot(x.reDoubleValue() - y.reDoubleValue(),
          x.imDoubleValue() - y.imDoubleValue());
      double size = Math.max(1.0, Math.max(Math.hypot(x.reDoubleValue(), x.imDoubleValue()),
          Math.hypot(y.reDoubleValue(), y.imDoubleValue())));
      if (!(distance <= SAME * size)) {
        return false;
      }
    }
    return true;
  }

  /** The relative size below which an imaginary part is rounding noise. */
  private static final double NOISE = 1.0e-12;

  /**
   * The relative distance below which two solutions are the same: a few hundred units of the last
   * digit, the rounding error of the branches of exact radicals evaluated with machine numbers.
   */
  private static final double SAME = 1.0e-13;

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_4;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, SolveOptions.NSOLVE_KEYS, SolveOptions.NSOLVE_DEFAULTS);
  }
}
