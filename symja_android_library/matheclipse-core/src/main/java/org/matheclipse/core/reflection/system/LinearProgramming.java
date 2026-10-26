package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.fraction.BigFraction;
import org.hipparchus.optim.LocalizedOptimFormats;
import org.hipparchus.optim.PointValuePair;
import org.hipparchus.optim.linear.LinearConstraint;
import org.hipparchus.optim.linear.LinearConstraintSet;
import org.hipparchus.optim.linear.LinearObjectiveFunction;
import org.hipparchus.optim.linear.NonNegativeConstraint;
import org.hipparchus.optim.linear.PivotSelectionRule;
import org.hipparchus.optim.linear.Relationship;
import org.hipparchus.optim.linear.SimplexSolver;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.RationalSimplex;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.optimization.ILinearOptimizer;
import org.matheclipse.core.optimization.OptimizationMethod;

/**
 *
 *
 * <pre>
 * LinearProgramming(coefficientsOfLinearObjectiveFunction, constraintList, constraintRelationList)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * the <code>LinearProgramming</code> function provides an implementation of
 * <a href="http://en.wikipedia.org/wiki/Simplex_algorithm">George Dantzig's simplex algorithm</a>
 * for solving linear optimization problems with linear equality and inequality constraints and
 * implicit non-negative variables.
 *
 * </blockquote>
 *
 * <p>
 * See:<br>
 *
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/Linear_programming">Wikipedia - Linear programming</a>
 * </ul>
 *
 * <p>
 * See also: <a href="NMaximize.md">NMaximize</a>, <a href="NMinimize.md">NMinimize</a>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; LinearProgramming({-2, 1, -5}, {{1, 2, 0},{3, 2, 0},{0,1,0},{0,0,1}}, {{6,-1},{12,-1},{0,1},{1,0}})
 * {4,0,1}
 * </pre>
 *
 * <p>
 * solves the linear problem:
 *
 * <pre>
 * Minimize -2x + y - 5
 * </pre>
 *
 * <p>
 * with the constraints:
 *
 * <pre>
 *   x  + 2y &lt;=  6
 *   3x + 2y &lt;= 12
 *         x &gt;= 0
 *         y &gt;= 0
 * </pre>
 */
public class LinearProgramming extends AbstractFunctionEvaluator {

  public LinearProgramming() {
    super();
  }

  /**
   * The LinearProgramming provides an implementation of
   * <a href="http://en.wikipedia.org/wiki/Simplex_algorithm">George Dantzig's simplex algorithm</a>
   * for solving linear optimization problems with linear equality and inequality constraints.
   */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // switch to numeric calculation
    return numericEval(ast, engine);
  }

  @Override
  public IExpr numericEval(IAST ast, EvalEngine engine) {
    // LinearProgramming(c, m, b, ..., Method -> "ORTools")
    final IExpr method = OptimizationMethod.trailingMethod(ast, 3);
    if (method.isPresent()) {
      ast = ast.removeAtClone(ast.argSize());
    }
    final boolean orTools = OptimizationMethod.useORTools(S.LinearProgramming, method,
        ILinearOptimizer.isAvailable(), engine);
    ast = denseArguments(ast);
    if (!orTools) {
      IExpr exact = exactLinearProgramming(ast, engine);
      if (exact != null) {
        return exact;
      }
    }
    try {
      if (ast.arg1().isList() && ast.arg2().isList() && ast.arg3().isList()) {
        double[] arg1D = ast.arg1().toDoubleVector();
        if (arg1D != null) {
          LinearObjectiveFunction f = new LinearObjectiveFunction(arg1D, 0);
          Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

          IAST arg2 = (IAST) ast.arg2();
          IAST arg3 = (IAST) ast.arg3();
          if (arg2.size() != arg3.size()) {
            return F.NIL;
          }
          double[] arg2D;
          double[] arg3D;
          for (int i = 1; i < arg2.size(); i++) {
            if (arg2.get(i).isList()) {
              arg2D = arg2.get(i).toDoubleVector();
              if (arg2D == null) {
                return F.NIL;
              }
              if (arg3.get(i).isList()) {
                arg3D = arg3.get(i).toDoubleVector();
                if (arg3D == null) {
                  return F.NIL;
                }
                if (arg3D.length >= 2) {
                  double val = arg3D[1];
                  if (val == 0.0) {
                    constraints.add(new LinearConstraint(arg2D, Relationship.EQ, arg3D[0]));
                  } else if (val < 0.0) {
                    constraints.add(new LinearConstraint(arg2D, Relationship.LEQ, arg3D[0]));
                  } else if (val > 0.0) {
                    constraints.add(new LinearConstraint(arg2D, Relationship.GEQ, arg3D[0]));
                  }
                } else if (arg3D.length == 1) {
                  constraints.add(new LinearConstraint(arg2D, Relationship.GEQ, arg3D[0]));
                }
              } else {
                IReal sn = arg3.get(i).evalReal();
                if (sn != null) {
                  constraints.add(new LinearConstraint(arg2D, Relationship.GEQ, sn.doubleValue()));
                } else {
                  // `1`.
                  return Errors.printMessage(ast.topHead(), "error",
                      F.list(F.$str("Numeric vector or number expected.")), engine);
                }
              }
            } else {
              // `1`.
              return Errors.printMessage(ast.topHead(), "error",
                  F.list(F.$str("Numeric vector expected.")), engine);
            }
          }
          int n = arg1D.length;
          boolean nonNegative = true;
          if (ast.argSize() >= 4) {
            // the bounds of the variables replace x >= 0
            if (!addBounds(ast.arg4(), n, constraints)) {
              return F.NIL;
            }
            nonNegative = false;
          }
          boolean[] integer = new boolean[n];
          if (ast.argSize() == 5 && !integerDomains(ast.arg5(), integer, engine)) {
            return F.NIL;
          }
          boolean anyInteger = false;
          for (boolean b : integer) {
            anyInteger |= b;
          }
          if (orTools) {
            return solveWithORTools(ast.topHead(), arg1D, constraints, nonNegative, integer,
                engine);
          }
          if (!anyInteger) {
            return F.List(solve(f, constraints, nonNegative));
          }
          double[] values = branchAndBound(f, constraints, nonNegative, integer);
          if (values == INCOMPLETE) {
            // The search for an integer solution stopped after `1` subproblems.
            return Errors.printMessage(ast.topHead(), "lpbb", F.list(F.ZZ(MAX_NODES)), engine);
          }
          if (values == null) {
            // No solution can be found that satisfies the constraints.
            return Errors.printMessage(ast.topHead(), "lpsnf", F.CEmptyList, engine);
          }
          IASTAppendable result = F.ListAlloc(n);
          for (int i = 0; i < n; i++) {
            result.append(integer[i] ? F.ZZ(Math.round(values[i])) : F.num(values[i]));
          }
          return result;
        }
      }
    } catch (MathIllegalArgumentException miae) {
      if (Config.DEBUG) {
        miae.printStackTrace();
      }
      // `1`.
      return Errors.printMessage(ast.topHead(), "error", F.list(F.$str(miae.getMessage())), engine);
    } catch (MathIllegalStateException mise) {
      if (Config.DEBUG) {
        mise.printStackTrace();
      }
      if (mise.getSpecifier() == LocalizedOptimFormats.UNBOUNDED_SOLUTION) {
        // The problem is unbounded.
        return Errors.printMessage(ast.topHead(), "ubnd", F.CEmptyList, engine);
      }
      // No solution can be found that satisfies the constraints.
      return Errors.printMessage(ast.topHead(), "lpsnf", F.CEmptyList, engine);
    } catch (MathRuntimeException mre) {
      if (Config.DEBUG) {
        mre.printStackTrace();
      }
      // `1`.
      return Errors.printMessage(ast.topHead(), "error", F.list(F.$str(mre.getMessage())), engine);

    }
    return F.NIL;
  }

  /** The arguments with each <code>SparseArray</code> replaced by its list form. */
  static IAST denseArguments(IAST ast) {
    IASTMutable result = null;
    for (int i = 1; i < ast.size(); i++) {
      if (ast.get(i).isSparseArray()) {
        if (result == null) {
          result = ast.copy();
        }
        result.set(i, ast.get(i).normal(false));
      }
    }
    return result == null ? ast : result;
  }

  /**
   * The exact values of a list of rational numbers, or <code>null</code> if <code>list</code>
   * isn't such a list.
   */
  static BigFraction[] rationalVector(IExpr list) {
    if (list.isSparseArray()) {
      list = list.normal(false);
    }
    if (!list.isList()) {
      return null;
    }
    BigFraction[] result = new BigFraction[list.argSize()];
    for (int i = 0; i < result.length; i++) {
      IExpr x = list.getAt(i + 1);
      if (!x.isRational()) {
        return null;
      }
      result[i] = ((IRational) x).toBigFraction();
    }
    return result;
  }

  /** The integer or fraction of the exact <code>value</code>. */
  static IExpr rational(BigFraction value) {
    if (value.getDenominator().equals(java.math.BigInteger.ONE)) {
      return F.ZZ(value.getNumerator());
    }
    return F.QQ(value.getNumerator(), value.getDenominator());
  }

  /** The list of the exact <code>values</code>. */
  static IAST rationalList(BigFraction[] values) {
    IASTAppendable result = F.ListAlloc(values.length);
    for (BigFraction v : values) {
      result.append(rational(v));
    }
    return result;
  }

  /**
   * The exact bound of a variable: <code>null</code> for an infinite one, else the rational value.
   *
   * @return <code>false</code> in <code>valid[0]</code> if <code>bound</code> is neither rational
   *         nor infinite in the direction <code>sign</code>
   */
  private static BigFraction exactBound(IExpr bound, int sign, boolean[] valid) {
    if (sign < 0 ? bound.isNegativeInfinity() : bound.isInfinity()) {
      return null;
    }
    if (bound.isRational()) {
      return ((IRational) bound).toBigFraction();
    }
    valid[0] = false;
    return null;
  }

  /**
   * Solve <code>LinearProgramming(c, m, b, [bounds], [domains])</code> exactly over the rationals
   * if all its numbers are rational.
   *
   * @return <code>null</code> if the problem has numbers which aren't rational or a form which the
   *         numeric solver has to check; else the exact result or {@link F#NIL} after a message
   */
  private static IExpr exactLinearProgramming(IAST ast, EvalEngine engine) {
    if (ast.argSize() < 3 || ast.argSize() > 5 || !ast.arg2().isList() || !ast.arg3().isList()
        || ast.arg2().argSize() != ast.arg3().argSize()) {
      return null;
    }
    BigFraction[] c = rationalVector(ast.arg1());
    if (c == null || c.length == 0) {
      return null;
    }
    final int n = c.length;
    IAST m = (IAST) ast.arg2();
    IAST b = (IAST) ast.arg3();
    List<BigFraction[]> rows = new ArrayList<BigFraction[]>();
    List<Integer> relations = new ArrayList<Integer>();
    List<BigFraction> rhs = new ArrayList<BigFraction>();
    for (int i = 1; i < m.size(); i++) {
      BigFraction[] row = rationalVector(m.get(i));
      if (row == null || row.length != n) {
        return null;
      }
      IExpr spec = b.get(i);
      int relation = RationalSimplex.GEQ;
      if (spec.isList()) {
        if (spec.argSize() == 2 && spec.second().isRational()) {
          relation = ((IRational) spec.second()).complexSign();
        } else if (spec.argSize() != 1) {
          return null;
        }
        spec = spec.first();
      }
      if (!spec.isRational()) {
        return null;
      }
      rows.add(row);
      relations.add(relation);
      rhs.add(((IRational) spec).toBigFraction());
    }
    // the bounds of the variables; by default x >= 0
    IExpr bounds = ast.argSize() >= 4 ? ast.arg4() : F.C0;
    for (int j = 0; j < n; j++) {
      IExpr bound = bounds.isList() ? (bounds.argSize() == n ? bounds.getAt(j + 1) : F.NIL)
          : bounds;
      if (bound.isNIL()) {
        return null;
      }
      boolean[] valid = new boolean[] {true};
      BigFraction lower;
      BigFraction upper = null;
      if (bound.isList2()) {
        lower = exactBound(bound.first(), -1, valid);
        upper = exactBound(bound.second(), 1, valid);
      } else {
        lower = exactBound(bound, -1, valid);
      }
      if (!valid[0]) {
        return null;
      }
      BigFraction[] unit = new BigFraction[n];
      java.util.Arrays.fill(unit, BigFraction.ZERO);
      unit[j] = BigFraction.ONE;
      if (lower != null) {
        rows.add(unit);
        relations.add(RationalSimplex.GEQ);
        rhs.add(lower);
      }
      if (upper != null) {
        rows.add(unit);
        relations.add(RationalSimplex.LEQ);
        rhs.add(upper);
      }
    }
    boolean[] integer = new boolean[n];
    if (ast.argSize() == 5 && !integerDomains(ast.arg5(), integer, engine)) {
      return F.NIL;
    }
    boolean anyInteger = false;
    for (boolean isInteger : integer) {
      anyInteger |= isInteger;
    }
    BigFraction[][] a = rows.toArray(new BigFraction[rows.size()][]);
    int[] rel = new int[relations.size()];
    for (int i = 0; i < rel.length; i++) {
      rel[i] = relations.get(i);
    }
    BigFraction[] r = rhs.toArray(new BigFraction[rhs.size()]);
    RationalSimplex.Result result = anyInteger
        ? RationalSimplex.minimizeInteger(c, a, rel, r, integer, MAX_NODES)
        : RationalSimplex.minimize(c, a, rel, r);
    switch (result.status) {
      case OPTIMAL:
        return rationalList(result.point);
      case INFEASIBLE:
        // No solution can be found that satisfies the constraints.
        return Errors.printMessage(ast.topHead(), "lpsnf", F.CEmptyList, engine);
      case UNBOUNDED:
        // The problem is unbounded.
        return Errors.printMessage(ast.topHead(), "ubnd", F.CEmptyList, engine);
      default:
        // The search for an integer solution stopped after `1` subproblems.
        return Errors.printMessage(ast.topHead(), "lpbb", F.list(F.ZZ(MAX_NODES)), engine);
    }
  }

  /**
   * Minimize <code>objective . x</code> subject to <code>constraints</code> with the installed
   * {@link ILinearOptimizer}, the Glop simplex and branch and bound of OR-Tools.
   *
   * @return the list of the values of the variables; the integer variables as exact integers
   */
  static IExpr solveWithORTools(ISymbol head, double[] objective,
      Collection<LinearConstraint> constraints, boolean nonNegative, boolean[] integer,
      EvalEngine engine) {
    return resultList(head, solveORTools(objective, constraints, nonNegative, integer), integer,
        engine);
  }

  /** Minimize <code>objective . x</code> subject to <code>constraints</code> with OR-Tools. */
  static ILinearOptimizer.Result solveORTools(double[] objective,
      Collection<LinearConstraint> constraints, boolean nonNegative, boolean[] integer) {
    return ILinearOptimizer.get().solve(OptimizationMethod.linearProblem(objective, 0.0, false,
        constraints, nonNegative, integer, OptimizationMethod.DEFAULT_TIME_LIMIT));
  }

  /**
   * The values of the variables of an OR-Tools result, the integer ones as exact integers, or a
   * message and {@link F#NIL} if there is no solution.
   */
  static IExpr resultList(ISymbol head, ILinearOptimizer.Result result, boolean[] integer,
      EvalEngine engine) {
    switch (result.status) {
      case OPTIMAL:
      case FEASIBLE:
        IASTAppendable list = F.ListAlloc(result.values.length);
        for (int i = 0; i < result.values.length; i++) {
          list.append(integer != null && integer[i] ? F.ZZ(Math.round(result.values[i]))
              : F.num(result.values[i]));
        }
        return list;
      case INFEASIBLE:
        // No solution can be found that satisfies the constraints.
        return Errors.printMessage(head, "lpsnf", F.CEmptyList, engine);
      case UNBOUNDED:
        // `1`.
        return Errors.printMessage(head, "error",
            F.list(F.$str("The problem is unbounded.")), engine);
      default:
        // `1`.
        return Errors.printMessage(head, "error",
            F.list(F.$str("The OR-Tools solver returned the status " + result.status + ".")),
            engine);
    }
  }

  /** The largest number of subproblems the branch and bound solves. */
  static final int MAX_NODES = 10000;

  private static final double INTEGER_TOLERANCE = 1.0e-9;

  /** The result of a branch and bound which stopped before it proved a solution optimal. */
  static final double[] INCOMPLETE = new double[0];

  /**
   * Add the bounds of the variables to the constraints: one lower bound <code>l</code> for all,
   * or a list of lower bounds <code>li</code> or of intervals <code>{li, ui}</code>.
   *
   * @return <code>false</code> if <code>bounds</code> has none of these forms
   */
  private static boolean addBounds(IExpr bounds, int n, Collection<LinearConstraint> constraints) {
    for (int i = 0; i < n; i++) {
      IExpr bound = bounds.isList() ? (bounds.argSize() == n ? bounds.getAt(i + 1) : F.NIL)
          : bounds;
      if (bound.isNIL()) {
        return false;
      }
      double lower;
      double upper = Double.POSITIVE_INFINITY;
      if (bound.isList2()) {
        lower = bound.first().isNegativeInfinity() ? Double.NEGATIVE_INFINITY
            : bound.first().evalfNaN();
        upper = bound.second().isInfinity() ? Double.POSITIVE_INFINITY : bound.second().evalfNaN();
      } else {
        lower = bound.isNegativeInfinity() ? Double.NEGATIVE_INFINITY : bound.evalfNaN();
      }
      if (Double.isNaN(lower) || Double.isNaN(upper)) {
        return false;
      }
      double[] unit = new double[n];
      unit[i] = 1.0;
      if (lower != Double.NEGATIVE_INFINITY) {
        constraints.add(new LinearConstraint(unit, Relationship.GEQ, lower));
      }
      if (upper != Double.POSITIVE_INFINITY) {
        constraints.add(new LinearConstraint(unit, Relationship.LEQ, upper));
      }
    }
    return true;
  }

  /**
   * Mark the variables which must be integers: <code>Integers</code> for all, or a list of
   * <code>Integers</code> and <code>Reals</code> where the variables beyond its end are real. Any
   * other domain is reported and taken as <code>Reals</code>.
   *
   * @return <code>false</code> if the list is longer than the number of variables
   */
  private static boolean integerDomains(IExpr domains, boolean[] integer, EvalEngine engine) {
    boolean unknown = false;
    if (domains.isList()) {
      if (domains.argSize() > integer.length) {
        return false;
      }
      for (int i = 1; i <= domains.argSize(); i++) {
        IExpr domain = domains.getAt(i);
        integer[i - 1] = domain == S.Integers;
        unknown |= domain != S.Integers && domain != S.Reals;
      }
    } else if (domains == S.Integers) {
      java.util.Arrays.fill(integer, true);
    } else {
      unknown = domains != S.Reals;
    }
    if (unknown) {
      // `1` is not a valid domain specification; Reals is used instead.
      Errors.printMessage(S.LinearProgramming, "lpdom", F.list(domains), engine);
    }
    return true;
  }

  private static double[] solve(LinearObjectiveFunction f, Collection<LinearConstraint> constraints,
      boolean nonNegative) {
    SimplexSolver solver = new SimplexSolver();
    PointValuePair solution = solver.optimize(f, new LinearConstraintSet(constraints),
        GoalType.MINIMIZE, new NonNegativeConstraint(nonNegative), PivotSelectionRule.BLAND);
    return solution.getPointRef();
  }

  /** The solution of a subproblem, or <code>null</code> if it has none. */
  private static double[] solveOrNull(LinearObjectiveFunction f,
      Collection<LinearConstraint> constraints, boolean nonNegative) {
    try {
      return solve(f, constraints, nonNegative);
    } catch (MathIllegalStateException mise) {
      if (mise.getSpecifier() == org.hipparchus.optim.LocalizedOptimFormats.NO_FEASIBLE_SOLUTION) {
        return null;
      }
      throw mise;
    }
  }

  /** The first variable which should be an integer but isn't, or <code>-1</code>. */
  private static int fractionalVariable(double[] values, boolean[] integer) {
    for (int i = 0; i < values.length; i++) {
      if (integer[i] && Math.abs(values[i] - Math.rint(values[i])) > INTEGER_TOLERANCE) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Branch and bound: a subproblem whose solution has a fractional value <code>v</code> for an
   * integer variable is split into the subproblems with the variable <code>&lt;= Floor(v)</code>
   * and <code>&gt;= Ceiling(v)</code>, and both are solved at once. The subproblem with the
   * smallest objective value is split next; a solution only replaces the best one found so far if
   * it is strictly better.
   *
   * @return <code>null</code> if no solution was found, {@link #INCOMPLETE} if the search was
   *         stopped after {@link #MAX_NODES} subproblems
   */
  static double[] branchAndBound(LinearObjectiveFunction f,
      Collection<LinearConstraint> constraints, boolean nonNegative, boolean[] integer) {
    final class Node {
      final Collection<LinearConstraint> constraints;
      final double[] values;
      final double objective;
      final int order;

      Node(Collection<LinearConstraint> constraints, double[] values, int order) {
        this.constraints = constraints;
        this.values = values;
        this.objective = f.value(values);
        this.order = order;
      }
    }
    java.util.PriorityQueue<Node> open = new java.util.PriorityQueue<Node>((a, b) -> {
      int c = Double.compare(a.objective, b.objective);
      return c != 0 ? c : Integer.compare(a.order, b.order);
    });
    double[] best = null;
    double bestObjective = Double.POSITIVE_INFINITY;
    int count = 0;
    double[] root = solveOrNull(f, constraints, nonNegative);
    if (root == null) {
      return null;
    }
    if (fractionalVariable(root, integer) < 0) {
      return root;
    }
    open.add(new Node(constraints, root, count++));
    while (!open.isEmpty()) {
      Node node = open.poll();
      if (node.objective >= bestObjective - INTEGER_TOLERANCE) {
        continue;
      }
      if (count > MAX_NODES) {
        return INCOMPLETE;
      }
      int i = fractionalVariable(node.values, integer);
      double[] unit = new double[integer.length];
      unit[i] = 1.0;
      for (int side = 0; side < 2; side++) {
        Collection<LinearConstraint> branch = new ArrayList<LinearConstraint>(node.constraints);
        branch.add(side == 0
            ? new LinearConstraint(unit, Relationship.LEQ, Math.floor(node.values[i]))
            : new LinearConstraint(unit, Relationship.GEQ, Math.ceil(node.values[i])));
        double[] values = solveOrNull(f, branch, nonNegative);
        if (values == null) {
          continue;
        }
        Node child = new Node(branch, values, count++);
        if (fractionalVariable(values, integer) < 0) {
          if (child.objective < bestObjective - INTEGER_TOLERANCE) {
            best = values;
            bestObjective = child.objective;
          }
        } else if (child.objective < bestObjective - INTEGER_TOLERANCE) {
          open.add(child);
        }
      }
    }
    return best;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // a trailing Method -> ... rule is not counted
    return OptimizationMethod.trailingMethod(ast, 3).isPresent() ? ARGS_4_6
        : IFunctionEvaluator.ARGS_3_5;
  }

  private static final int[] ARGS_4_6 = new int[] {4, 6};
}
