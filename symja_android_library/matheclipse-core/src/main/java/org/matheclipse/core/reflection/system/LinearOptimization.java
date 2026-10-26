package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.RationalSimplex;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.optimization.ILinearOptimizer;
import org.matheclipse.core.optimization.OptimizationMethod;

public class LinearOptimization extends LinearProgramming {

  public LinearOptimization() {
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

  /**
   * The variables of the specification <code>{x, Element(y, Integers), ...}</code>,
   * <code>Element({x, y}, dom)</code> or <code>Element(x, dom)</code>. For each variable
   * <code>integer</code> gets <code>TRUE</code> for the domain <code>Integers</code>,
   * <code>FALSE</code> for <code>Reals</code> and <code>null</code> if the specification gives no
   * domain.
   *
   * @return {@link F#NIL} if <code>spec</code> has none of these forms
   */
  private static IAST variablesOfSpecification(IExpr spec, List<Boolean> integer,
      EvalEngine engine) {
    if (spec.isAST(S.Element, 3)) {
      IAST vars = spec.first().makeList();
      Boolean isInteger = integerDomain(spec.second(), engine);
      for (int i = 1; i < vars.size(); i++) {
        integer.add(isInteger);
      }
      return vars;
    }
    if (!spec.isList()) {
      return F.NIL;
    }
    IAST list = (IAST) spec;
    IASTAppendable vars = F.ListAlloc(list.argSize());
    for (IExpr v : list) {
      if (v.isAST(S.Element, 3)) {
        Boolean isInteger = integerDomain(v.second(), engine);
        IAST elements = v.first().makeList();
        for (int i = 1; i < elements.size(); i++) {
          vars.append(elements.get(i));
          integer.add(isInteger);
        }
      } else {
        // no domain given here
        vars.append(v);
        integer.add(null);
      }
    }
    return vars;
  }

  /**
   * Whether <code>domain</code> is <code>Integers</code>; <code>Reals</code> and, after a message,
   * any other domain are taken as the reals.
   */
  private static Boolean integerDomain(IExpr domain, EvalEngine engine) {
    if (domain == S.Integers) {
      return Boolean.TRUE;
    }
    if (domain != S.Reals) {
      // `1` is not a valid domain specification; Reals is used instead.
      Errors.printMessage(S.LinearOptimization, "lpdom", F.list(domain), engine);
    }
    return Boolean.FALSE;
  }

  /**
   * The rules for an infeasible problem, after the message that there are no points which satisfy
   * the constraints.
   */
  private static IAST indeterminateRules(IAST variables, EvalEngine engine) {
    Errors.printMessage(S.LinearOptimization, "nsolc", F.CEmptyList, engine);
    IASTAppendable result = F.ListAlloc(variables.argSize());
    for (int i = 1; i < variables.size(); i++) {
      result.append(F.Rule(variables.get(i), S.Indeterminate));
    }
    return result;
  }

  /**
   * <code>LinearOptimization(f, cons, vars)</code> and <code>LinearOptimization(f, cons, vars,
   * dom)</code>, with <code>vars</code> like <code>{x, Element(y, Integers)}</code> and
   * <code>dom</code> the domain <code>Integers</code> or <code>Reals</code> of all variables or a
   * list of the domains of each variable. All variables are non-negative. A problem with integer
   * variables is solved by branch and bound, and their values are exact integers.
   */
  @Override
  public IExpr numericEval(IAST ast, EvalEngine engine) {
    IAST variables = F.NIL;
    try {
      if (ast.arg2().isList()) {
        if (ast.arg1().isList()) {
          IAST matrixForm = denseArguments(ast);
          if (OptimizationMethod.trailingMethod(matrixForm, 2).isPresent()) {
            matrixForm = matrixForm.removeAtClone(matrixForm.argSize());
          }
          if (isAffineConstraints(matrixForm.arg2())
              && (matrixForm.argSize() == 2 || isAffineConstraints(matrixForm.arg3()))) {
            return matrixForm(matrixForm, engine);
          }
          // fall back to LinearProgramming
          return ast.argSize() >= 3 && ast.arg3().isList() ? super.numericEval(ast, engine)
              : F.NIL;
        }
        // LinearOptimization(f, cons, vars, [dom], Method -> "ORTools")
        final IExpr method = OptimizationMethod.trailingMethod(ast, 3);
        if (method.isPresent()) {
          ast = ast.removeAtClone(ast.argSize());
        }
        if (ast.argSize() > 4) {
          return F.NIL;
        }
        final boolean orTools = OptimizationMethod.useORTools(S.LinearOptimization, method,
            ILinearOptimizer.isAvailable(), engine);

        List<Boolean> integerList = new ArrayList<Boolean>();
        variables = variablesOfSpecification(ast.arg3(), integerList, engine);
        if (variables.isNIL()) {
          return F.NIL;
        }
        final int n = variables.argSize();
        // the domains of the 4th argument: the domain of all variables or the list of the
        // domains of the variables
        Boolean[] domainArgument = new Boolean[n];
        if (ast.argSize() == 4) {
          IExpr domains = ast.arg4();
          if (domains.isList()) {
            if (domains.argSize() != n) {
              return F.NIL;
            }
            for (int i = 0; i < n; i++) {
              domainArgument[i] = integerDomain(domains.getAt(i + 1), engine);
            }
          } else {
            java.util.Arrays.fill(domainArgument, integerDomain(domains, engine));
          }
        }
        boolean[] integer = new boolean[n];
        for (int i = 0; i < n; i++) {
          Boolean inSpecification = integerList.get(i);
          if (inSpecification != null && domainArgument[i] != null
              && !inSpecification.equals(domainArgument[i])) {
            // `1`.
            return Errors.printMessage(S.LinearOptimization, "error",
                F.list(F.stringx("The variable " + variables.get(i + 1)
                    + " is given in the domains Integers and Reals.")),
                engine);
          }
          integer[i] = inSpecification != null ? inSpecification
              : domainArgument[i] != null && domainArgument[i];
        }
        boolean anyInteger = false;
        for (boolean b : integer) {
          anyInteger |= b;
        }

        IExpr arg1 = F.evalExpand(ast.arg1());
        if (!arg1.isPlus()) {
          return Errors.printMessage(S.LinearOptimization, "error",
              F.List(F.stringx("Plus expression expected at position 1.")), engine);
        }
        IAST plusAST = (IAST) arg1;

        Map<IExpr, Integer> variablesMap = new HashMap<IExpr, Integer>();
        if (createVariablesMap(variables, variablesMap, engine).isNIL()) {
          return F.NIL;
        }

        IExpr objectiveFunction = createObjectiveFunction(plusAST, variables, variablesMap, engine);
        if (objectiveFunction.isNIL()) {
          return F.NIL;
        }

        IAST constraintsList = (IAST) ast.arg2();
        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        if (!createConstraints(constraintsList, variables, variablesMap, constraints, engine)) {
          // a constraint which isn't linear was reported; solving without it would answer a
          // different problem
          return F.NIL;
        }

        if (orTools) {
          ILinearOptimizer.Result solution = solveORTools(objectiveFunction.toDoubleVector(),
              constraints, true, anyInteger ? integer : null);
          if (solution.status == ILinearOptimizer.Status.INFEASIBLE) {
            return indeterminateRules(variables, engine);
          }
          IExpr values = resultList(S.LinearOptimization, solution, integer, engine);
          if (!values.isList()) {
            return values;
          }
          IASTAppendable result = F.ListAlloc(values.argSize());
          for (int i = 1; i < values.size(); i++) {
            result.append(F.Rule(variables.get(i), values.get(i)));
          }
          return result;
        }

        LinearObjectiveFunction f =
            new LinearObjectiveFunction(objectiveFunction.toDoubleVector(), 0);

        if (anyInteger) {
          double[] values = branchAndBound(f, constraints, true, integer);
          if (values == INCOMPLETE) {
            // The search for an integer solution stopped after `1` subproblems.
            return Errors.printMessage(S.LinearOptimization, "lpbb", F.list(F.ZZ(MAX_NODES)),
                engine);
          }
          if (values == null) {
            return indeterminateRules(variables, engine);
          }
          IASTAppendable result = F.ListAlloc(n);
          for (int i = 0; i < n; i++) {
            result.append(F.Rule(variables.get(i + 1),
                integer[i] ? F.ZZ(Math.round(values[i])) : F.num(values[i])));
          }
          return result;
        }

        SimplexSolver solver = new SimplexSolver();
        // PointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, true);
        PointValuePair solution = solver.optimize(f, new LinearConstraintSet(constraints),
            GoalType.MINIMIZE, new NonNegativeConstraint(true), PivotSelectionRule.BLAND);
        double[] values = solution.getPointRef();
        if (values != null) {
          IASTAppendable result = F.ListAlloc(values.length);
          for (int i = 0; i < values.length; i++) {
            IExpr variable = variables.get(i + 1);
            result.append(F.Rule(variable, F.num(values[i])));
          }
          return result;
        }
      }
    } catch (MathIllegalStateException mise) {
      if (mise.getSpecifier() == LocalizedOptimFormats.NO_FEASIBLE_SOLUTION
          && variables.isPresent()) {
        // There are no points which satisfy the constraints
        return indeterminateRules(variables, engine);
      }
      if (mise.getSpecifier() == LocalizedOptimFormats.UNBOUNDED_SOLUTION) {
        // `1`.
        return Errors.printMessage(S.LinearOptimization, "error",
            F.list(F.$str("The problem is unbounded.")), engine);
      }
    } catch (MathIllegalArgumentException miae) {
      // `1`.
      return Errors.printMessage(ast.topHead(), "error", F.list(F.$str(miae.getMessage())), engine);
    } catch (MathRuntimeException mre) {
      return Errors.printMessage(S.LinearOptimization, mre, engine);
    }
    return F.NIL;
  }

  /**
   * Whether <code>constraints</code> is a pair <code>{a, b}</code> of a matrix <code>a</code> and
   * a vector <code>b</code> with one element for each row of <code>a</code>.
   */
  private static boolean isAffineConstraints(IExpr constraints) {
    if (!constraints.isList2()) {
      return false;
    }
    IExpr a = constraints.first().normal(false);
    IExpr b = constraints.second().normal(false);
    int[] dimensions = a.isMatrix(false);
    return dimensions != null && b.isList() && b.argSize() == dimensions[0]
        && b.isVector() == dimensions[0];
  }

  /** The rows <code>{a, b}</code> of the affine constraints, as dense lists. */
  private static IAST[] affineConstraints(IExpr constraints) {
    return new IAST[] {(IAST) constraints.first().normal(false),
        (IAST) constraints.second().normal(false)};
  }

  /** The vector of <code>n</code> times <code>Indeterminate</code>. */
  private static IAST indeterminateVector(int n) {
    return F.constantArray(S.Indeterminate, n);
  }

  /**
   * <code>LinearOptimization(c, {a, b})</code> and <code>LinearOptimization(c, {a, b}, {aeq,
   * beq})</code>: the vector <code>x</code> of free variables which minimizes <code>c.x</code>
   * subject to <code>a.x + b &gt;= 0</code> and <code>aeq.x + beq == 0</code>. Exact numbers are
   * solved by the exact simplex method, other numbers by the machine number simplex method.
   */
  private static IExpr matrixForm(IAST ast, EvalEngine engine) {
    IAST[] inequalities = affineConstraints(ast.arg2());
    IAST[] equations = ast.argSize() == 3 ? affineConstraints(ast.arg3()) : null;
    BigFraction[] c = rationalVector(ast.arg1());
    final int n = ast.arg1().argSize();
    if (n == 0) {
      return F.NIL;
    }
    // the rows of the inequalities, then of the equations
    List<IAST> rows = new ArrayList<IAST>();
    List<IExpr> offsets = new ArrayList<IExpr>();
    List<Integer> relations = new ArrayList<Integer>();
    for (int k = 0; k < 2; k++) {
      IAST[] part = k == 0 ? inequalities : equations;
      if (part == null) {
        continue;
      }
      for (int i = 1; i < part[0].size(); i++) {
        IExpr row = part[0].get(i);
        if (row.argSize() != n) {
          // `1`.
          return Errors.printMessage(S.LinearOptimization, "error",
              F.list(F.$str("The constraint matrices must have one column for each variable.")),
              engine);
        }
        rows.add((IAST) row);
        offsets.add(part[1].get(i));
        relations.add(k == 0 ? RationalSimplex.GEQ : RationalSimplex.EQ);
      }
    }
    final int m = rows.size();
    if (c != null) {
      BigFraction[][] a = new BigFraction[m][];
      BigFraction[] b = new BigFraction[m];
      int[] rel = new int[m];
      boolean exact = true;
      for (int i = 0; i < m && exact; i++) {
        a[i] = rationalVector(rows.get(i));
        exact = a[i] != null && offsets.get(i).isRational();
        if (exact) {
          // a.x + b >= 0 is a.x >= -b
          b[i] = ((IRational) offsets.get(i)).toBigFraction().negate();
          rel[i] = relations.get(i);
        }
      }
      if (exact) {
        RationalSimplex.Result result = RationalSimplex.minimize(c, a, rel, b);
        switch (result.status) {
          case OPTIMAL:
            return rationalList(result.point);
          case INFEASIBLE:
            // There are no points which satisfy the constraints.
            Errors.printMessage(S.LinearOptimization, "nsolc", F.CEmptyList, engine);
            return indeterminateVector(n);
          case UNBOUNDED:
            // The problem is unbounded.
            Errors.printMessage(S.LinearOptimization, "ubnd", F.CEmptyList, engine);
            return indeterminateVector(n);
          default:
            return F.NIL;
        }
      }
    }
    double[] cD = ast.arg1().toDoubleVector();
    if (cD == null) {
      return F.NIL;
    }
    Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
    for (int i = 0; i < m; i++) {
      double[] row = rows.get(i).toDoubleVector();
      double offset = offsets.get(i).evalfNaN();
      if (row == null || Double.isNaN(offset)) {
        return F.NIL;
      }
      constraints.add(new LinearConstraint(row,
          relations.get(i) == RationalSimplex.EQ ? Relationship.EQ : Relationship.GEQ, -offset));
    }
    try {
      PointValuePair solution = new SimplexSolver().optimize(new LinearObjectiveFunction(cD, 0),
          new LinearConstraintSet(constraints), GoalType.MINIMIZE,
          new NonNegativeConstraint(false), PivotSelectionRule.BLAND);
      return F.List(solution.getPointRef());
    } catch (MathIllegalStateException mise) {
      if (mise.getSpecifier() == LocalizedOptimFormats.NO_FEASIBLE_SOLUTION) {
        // There are no points which satisfy the constraints.
        Errors.printMessage(S.LinearOptimization, "nsolc", F.CEmptyList, engine);
        return indeterminateVector(n);
      }
      if (mise.getSpecifier() == LocalizedOptimFormats.UNBOUNDED_SOLUTION) {
        // The problem is unbounded.
        Errors.printMessage(S.LinearOptimization, "ubnd", F.CEmptyList, engine);
        return indeterminateVector(n);
      }
      throw mise;
    }
  }

  private boolean createConstraints(IAST constraintsList, IAST variables,
      Map<IExpr, Integer> variablesMap, Collection<LinearConstraint> constraints,
      EvalEngine engine) {
    for (int i = 1; i < constraintsList.size(); i++) {
      IExpr temp = constraintsList.get(i);
      int headID = temp.headID();
      if (headID < 0) {
        messageConstraintIsNotConvex(temp, engine);
        return false;
      }
      if (temp.isAST2()) {
        IAST relation = (IAST) temp;
        IExpr lhs = relation.arg1();
        IExpr lhsList = createObjectiveFunction(lhs, variables, variablesMap, engine);
        if (lhsList.isNIL()) {
          return false;
        }
        double rhs = relation.arg2().evalfNaN();
        if (Double.isNaN(rhs)) {
          return false;
        }
        switch (headID) {
          case ID.Equal:
            constraints.add(new LinearConstraint(lhsList.toDoubleVector(), Relationship.EQ, rhs));
            continue;
          case ID.Greater:
          case ID.GreaterEqual:
            constraints.add(new LinearConstraint(lhsList.toDoubleVector(), Relationship.GEQ, rhs));
            continue;
          case ID.Less:
          case ID.LessEqual:
            constraints.add(new LinearConstraint(lhsList.toDoubleVector(), Relationship.LEQ, rhs));
            continue;
          default:
            break;
        }

      }
      messageConstraintIsNotConvex(temp, engine);
      return false;
    }
    return true;
  }

  private static IAST messageConstraintIsNotConvex(IExpr temp, EvalEngine engine) {
    // The constraint `1` is not convex.
    return Errors.printMessage(S.LinearOptimization, "ctnc", F.List(temp), engine);
  }

  private static IExpr createObjectiveFunction(IExpr expr, IAST variables,
      Map<IExpr, Integer> variablesMap, EvalEngine engine) {
    Integer index = variablesMap.get(expr);
    if (index != null) {
      IASTAppendable functionList = F.ListAlloc(variables.argSize());
      for (int i = 1; i < variables.size(); i++) {
        functionList.append(F.C0);
      }
      functionList.set(index, F.C1);
      return functionList;
    }
    if (expr.isPlus()) {
      IAST plusAST = (IAST) expr;
      IASTAppendable functionList = F.ListAlloc(variables.argSize());
      for (int i = 1; i < variables.size(); i++) {
        functionList.append(F.C0);
      }
      for (int i = 1; i < plusAST.size(); i++) {
        IExpr temp = plusAST.get(i);
        if (temp.isTimes()) {
          IAST timesAST = (IAST) temp;
          if (timesAST.arg1().isReal()) {
            index = variablesMap.get(timesAST.arg2());
            if (index == null) {
              // The objective function `1` is not a numeric valued linear function of the
              // variables
              // `2`.
              return Errors.printMessage(S.LinearOptimization, "linobj", F.List(plusAST, variables),
                  engine);
            }
            functionList.set(index, timesAST.arg1());
            continue;
          }

        } else {
          index = variablesMap.get(temp);
          if (index == null) {
            // The objective function `1` is not a numeric valued linear function of the variables
            // `2`.
            return Errors.printMessage(S.LinearOptimization, "linobj", F.List(plusAST, variables),
                engine);
          }
          functionList.set(index, F.C1);
          continue;
        }
        // The objective function `1` is not a numeric valued linear function of the variables
        // `2`.
        return Errors.printMessage(S.LinearOptimization, "linobj", F.List(plusAST, variables),
            engine);
      }
      return functionList;
    }
    // The objective function `1` is not a numeric valued linear function of the variables
    // `2`.
    return Errors.printMessage(S.LinearOptimization, "linobj", F.List(expr, variables), engine);
  }

  private static IExpr createVariablesMap(IAST variables, Map<IExpr, Integer> variablesMap,
      EvalEngine engine) {
    int counter = 1;
    for (int i = 1; i < variables.size(); i++) {
      IExpr variable = variables.get(i);
      if (!variable.isVariable()) {
        return Errors.printMessage(S.LinearOptimization, "ivar", F.List(variable), engine);
      }
      Integer index = variablesMap.get(variable);
      if (index != null) {
        // The variable `1` has been specified more than once.
        return Errors.printMessage(S.LinearOptimization, "dpvar", F.List(variable), engine);
      }
      variablesMap.put(variable, counter++);
    }
    return S.True;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // LinearOptimization(f, cons, vars, [dom]); a trailing Method -> ... rule is not counted. The
    // matrix form LinearOptimization({c...}, ...) takes no domain argument.
    if (ast.argSize() >= 1 && ast.arg1().isList()) {
      // LinearOptimization(c, {a, b}, [{aeq, beq}]) or LinearOptimization(c, m, b)
      return OptimizationMethod.trailingMethod(ast, 2).isPresent() ? ARGS_3_4
          : IFunctionEvaluator.ARGS_2_3;
    }
    final boolean method = OptimizationMethod.trailingMethod(ast, 3).isPresent();
    return method ? ARGS_4_5 : IFunctionEvaluator.ARGS_3_4;
  }

  private static final int[] ARGS_4_5 = new int[] {4, 5};

  private static final int[] ARGS_3_4 = new int[] {3, 4};
}
