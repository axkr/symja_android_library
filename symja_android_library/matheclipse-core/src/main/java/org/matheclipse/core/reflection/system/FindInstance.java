package org.matheclipse.core.reflection.system;

import org.matheclipse.core.builtin.BooleanFunctions;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 *
 * <pre>
 * FindInstance(equations, vars)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * attempts to find one instance which solves the <code>equations</code> for the variables <code>
 * vars</code>.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; FindInstance({x^2==4,x+y^2==6}, {x,y})
 * {{x-&gt;-2,y-&gt;-2*Sqrt(2)}}
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p>
 * <a href="Solve.md">Solve</a>
 */
public class FindInstance extends Solve {

  public FindInstance() {
    // empty constructor
  }

  /**
   * Try to find at least one solution for a set of equations (i.e. <code>Equal[...]</code>
   * expressions).
   */
  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    IAST vars = Validate.checkIsVariableOrVariableList(ast, 2, ast.topHead(), engine);
    if (vars.isNIL()) {
      return F.NIL;
    }
    try {
      boolean formula = false;
      int maxChoices = 1;
      if (argSize > 0 && argSize < ast.argSize()) {
        ast = ast.copyUntil(argSize + 1);
      }
      if (argSize >= 4) {
        maxChoices = ast.arg4().toMachineInt();
        if (maxChoices < 0) {
          maxChoices = 1;
        }
      } else if (argSize >= 3) {
        maxChoices = ast.arg3().toMachineInt();
        if (maxChoices < 0) {
          maxChoices = 1;
        }
      }

      try {
        if (ast.arg1().isBooleanFormula()) {
          formula = ast.arg1().isBooleanFormula();
          if (ast.isAST2()) {
            return BooleanFunctions.solveInstances(ast.arg1(), vars, maxChoices);
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }

      ISymbol domain = S.Complexes;
      // FindInstance(expr, vars, n) is FindInstance(expr, vars, Complexes, n)
      if (argSize >= 3 && !(argSize == 3 && ast.arg3().isInteger())) {
        if (!ast.arg3().isSymbol()) {
          // Warning: `1` is not a valid domain specification.
          Errors.printMessage(ast.topHead(), "bdomv", F.List(ast.arg3()), engine);
        } else {
          if (ast.arg3() == S.Booleans || formula) {
            return BooleanFunctions.solveInstances(ast.arg1(), vars, maxChoices);
          } else if (ast.arg3() == S.Integers || ast.arg3() == S.Primes) {
            domain = (ISymbol) ast.arg3();
            // options[1] is the Method option
            return Solve.solveIntegers(ast, vars, vars, maxChoices, domain, false,
                options.length > 1 && options[1] != null ? options[1] : S.Automatic, engine);
          }
          domain = (ISymbol) ast.arg3();
          if (domain != S.Reals && domain != S.Complexes) {
            // Warning: `1` is not a valid domain specification.
            Errors.printMessage(ast.topHead(), "bdomv", F.List(ast.arg3()), engine);
          }
        }
      }

      if (formula && argSize == 3) {
        IAST instances = BooleanFunctions.solveInstances(ast.arg1(), vars, maxChoices);
        if (instances.isPresent()) {
          return instances;
        }
      }
      if (domain == S.Reals || domain == S.Complexes) {
        // inequalities in one variable, wherever their solution set lies, or linear systems
        IExpr instances = inequalityInstances(ast.arg1(), vars, maxChoices, engine);
        if (instances.isPresent()) {
          return instances;
        }
      }
      if (domain == S.Reals) {
        // try to find one feasible instance of a real constrained (inequality) system by
        // delegating to the symbolic optimizers Maximize/Minimize.
        IExpr instance = findInstanceViaOptimizer(ast.arg1(), vars, engine);
        if (instance.isPresent()) {
          return instance;
        }
      }

      IASTMutable termsEqualZeroList = Validate.checkEquations(ast, 1);
      SolveData solveData =
          new Solve.SolveData(SolveOptions.of(SolveOptions.FIND_INSTANCE_KEYS, options));
      return solveData.solveEquations(termsEqualZeroList, F.List(), vars, maxChoices, false,
          engine);
    } catch (final ValidateException ve) {
      return Errors.printMessage(ast.topHead(), ve, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.FindInstance, rex, engine);
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_4;
  }

  /**
   * Instances of a condition with inequalities in a single variable or of a linear system with
   * inequalities. The condition is reduced to a cylindrical form, the first variable takes a value
   * of its solution set, the condition with this value substituted is reduced for the remaining
   * variables, and so on. A value of a bounded interval is <code>0</code> if the interval contains
   * it, else its midpoint; several values are spread over the interval. A closed bound with an
   * inexact value is taken itself, else the integer next to the bound inside the set. Each
   * instance is verified by substitution into the condition.
   *
   * @param arg1 the condition or the list of conditions
   * @param vars the variables
   * @param maxChoices the number of instances
   * @return the list of instances or {@link F#NIL} if the condition can't be handled this way
   */
  private static IExpr inequalityInstances(IExpr arg1, IAST vars, int maxChoices,
      EvalEngine engine) {
    if (engine.getOptimizeExpressionDepth() != 0
        || !vars.forAll(v -> v.isSymbol() && v.isVariable())) {
      return F.NIL;
    }
    IAST conditions = arg1.isAnd() || arg1.isList() ? (IAST) arg1 : arg1.makeList();
    boolean hasInequality = false;
    for (IExpr condition : conditions) {
      if (condition.isAST(S.LessEqual, 3) || condition.isAST(S.Less, 3)
          || condition.isAST(S.GreaterEqual, 3) || condition.isAST(S.Greater, 3)) {
        hasInequality = true;
      } else if (condition.isAST(S.Inequality)) {
        hasInequality = true;
        if (vars.argSize() > 1) {
          return F.NIL;
        }
      } else if (!condition.isAST(S.Equal, 3) && !condition.isAST(S.Unequal, 3)) {
        return F.NIL;
      }
      if (!VariablesSet.getVariables(condition).forAll(v -> vars.indexOf(v) > 0)) {
        return F.NIL;
      }
      if (vars.argSize() > 1 && !isLinear(condition, vars, engine)) {
        return F.NIL;
      }
    }
    if (!hasInequality) {
      return F.NIL;
    }
    final IExpr condition =
        conditions.argSize() == 1 ? conditions.arg1() : conditions.setAtCopy(0, S.And);
    IExpr reduced = engine.evalQuiet(F.Reduce(condition, vars, S.Reals));
    IExpr exactCondition = condition;
    boolean inexact = false;
    if (reduced.isAST(S.Reduce) && condition.has(IExpr::isInexactNumber, false)) {
      // solve the system with rationalized coefficients
      exactCondition = engine.evalQuiet(F.Rationalize(condition, F.C0));
      reduced = engine.evalQuiet(F.Reduce(exactCondition, vars, S.Reals));
      inexact = true;
    }
    if (reduced.isFalse()) {
      return F.CEmptyList;
    }
    if (reduced.isAST(S.Reduce)) {
      return F.NIL;
    }
    IASTAppendable instances = F.ListAlloc(maxChoices);
    collectInstances(condition, exactCondition, reduced, vars, 1, F.CEmptyList, maxChoices,
        inexact, instances, engine);
    if (instances.isEmpty()) {
      return F.NIL;
    }
    return instances;
  }

  /** Test if the sides of the comparison are polynomials of degree &lt;= 1 in the variables. */
  private static boolean isLinear(IExpr comparison, IAST vars, EvalEngine engine) {
    return Solve.isAffine(engine.evalQuiet(F.Subtract(comparison.first(), comparison.second())),
        vars);
  }

  /**
   * Choose values for the variable <code>vars[k]</code> from its conditions in the cylindrical form
   * <code>reduced</code> and recurse for the remaining variables.
   *
   * @param original the condition to verify an instance
   * @param exactCondition the condition with rationalized coefficients if <code>inexact</code>
   * @param reduced the reduced <code>exactCondition</code> with the values of the previous
   *        variables substituted
   * @param rules the values of the previous variables
   * @param maxChoices the number of instances to collect in <code>instances</code>
   * @return <code>false</code> if the reduced form couldn't be handled
   */
  private static boolean collectInstances(IExpr original, IExpr exactCondition, IExpr reduced,
      IAST vars, int k, IAST rules, int maxChoices, boolean inexact, IASTAppendable instances,
      EvalEngine engine) {
    if (k >= vars.size()) {
      IAST instance = rules;
      if (inexact) {
        instance = (IAST) engine.evalQuiet(F.N(rules));
      }
      if (instances.indexOf(instance) <= 0 && engine.evalTrue(F.subst(original, instance))) {
        instances.append(instance);
      }
      return true;
    }
    final IExpr x = vars.get(k);
    final IAST laterVars = vars.copyFrom(k + 1);
    IAST branches = reduced.isOr() ? (IAST) reduced : F.list(reduced);
    for (IExpr branch : branches) {
      if (branch.isFalse()) {
        continue;
      }
      // the parts of the cylindrical form, which only contain x
      IAST parts = branch.isAnd() ? (IAST) branch : F.list(branch);
      IASTAppendable xParts = F.ListAlloc(parts.argSize());
      for (IExpr part : parts) {
        if (!part.isTrue() && part.isFree(v -> laterVars.indexOf(v) > 0, false)) {
          xParts.append(part);
        }
      }
      int need = maxChoices - instances.argSize();
      java.util.List<IExpr> values = values(xParts, x, need, engine);
      if (values == null) {
        return false;
      }
      int spread = Math.min(need, values.size());
      for (int i = 0; i < values.size() && instances.argSize() < maxChoices; i++) {
        // spread the remaining instances over the remaining values
        int remainingValues = Math.max(1, spread - i);
        int share = (maxChoices - instances.argSize() + remainingValues - 1) / remainingValues;
        IASTAppendable newRules = rules.copyAppendable();
        newRules.append(F.Rule(x, values.get(i)));
        IExpr next = S.True;
        if (laterVars.argSize() > 0) {
          next = engine.evalQuiet(F.Reduce(F.subst(exactCondition, newRules), laterVars, S.Reals));
          if (next.isFalse()) {
            continue;
          }
          if (next.isAST(S.Reduce)) {
            return false;
          }
        }
        int target = Math.min(maxChoices, instances.argSize() + share);
        if (!collectInstances(original, exactCondition, next, vars, k + 1, newRules, target,
            inexact, instances, engine)) {
          return false;
        }
      }
      if (instances.argSize() >= maxChoices) {
        break;
      }
    }
    return true;
  }

  /**
   * The values to try for the variable <code>x</code> restricted by the conditions
   * <code>xParts</code> (comparisons of <code>x</code> with numbers). The first <code>need</code>
   * values are distinct points of the solution set, if it isn't a single point; further values
   * are alternatives if a value fails the verification.
   *
   * @return <code>null</code> if a condition isn't a comparison of <code>x</code> with a number
   */
  private static java.util.List<IExpr> values(IAST xParts, IExpr x, int need,
      EvalEngine engine) {
    IExpr lower = F.NIL;
    IExpr upper = F.NIL;
    boolean lowerClosed = false;
    boolean upperClosed = false;
    java.util.List<IExpr> values = new java.util.ArrayList<IExpr>();
    for (IExpr part : xParts) {
      if (part.isAST(S.Inequality, 6) && ((IAST) part).arg3().equals(x)) {
        IAST inequality = (IAST) part;
        lower = inequality.arg1();
        lowerClosed = inequality.arg2() == S.LessEqual;
        upper = inequality.arg5();
        upperClosed = inequality.arg4() == S.LessEqual;
      } else if (part.isAST(S.Equal, 3) && part.first().equals(x)) {
        IExpr value = engine.evalQuiet(part.second());
        if (!value.isRealResult()) {
          return null;
        }
        values.add(value);
        return values;
      } else if ((part.isAST(S.Greater, 3) || part.isAST(S.GreaterEqual, 3))
          && part.first().equals(x)) {
        lower = part.second();
        lowerClosed = part.isAST(S.GreaterEqual);
      } else if ((part.isAST(S.Less, 3) || part.isAST(S.LessEqual, 3))
          && part.first().equals(x)) {
        upper = part.second();
        upperClosed = part.isAST(S.LessEqual);
      } else {
        return null;
      }
    }
    if ((lower.isPresent() && !lower.isRealResult())
        || (upper.isPresent() && !upper.isRealResult())) {
      return null;
    }
    final boolean containsZero = (lower.isNIL() || (lowerClosed //
        ? engine.evalTrue(F.LessEqual(lower, F.C0))
        : engine.evalTrue(F.Less(lower, F.C0)))) //
        && (upper.isNIL() || (upperClosed //
            ? engine.evalTrue(F.GreaterEqual(upper, F.C0))
            : engine.evalTrue(F.Greater(upper, F.C0))));
    if (lower.isPresent() && upper.isPresent()) {
      IExpr midpoint = engine.evalQuiet(F.Times(F.C1D2, F.Plus(lower, upper)));
      if (need <= 1) {
        values.add(containsZero ? F.C0 : midpoint);
      } else {
        for (int j = 1; j <= need; j++) {
          values.add(engine.evalQuiet(
              F.Plus(lower, F.Times(F.QQ(j, need + 1), F.Subtract(upper, lower)))));
        }
      }
      values.add(midpoint);
      return values;
    }
    IExpr start;
    int step = 1;
    if (containsZero) {
      start = F.C0;
      step = upper.isPresent() ? -1 : 1;
    } else if (lower.isPresent()) {
      if (lowerClosed && lower.isInexactNumber()) {
        values.add(lower);
      }
      start = lowerClosed ? engine.evalQuiet(F.Ceiling(lower))
          : engine.evalQuiet(F.Plus(F.Floor(lower), F.C1));
    } else {
      if (upperClosed && upper.isInexactNumber()) {
        values.add(upper);
      }
      start = upperClosed ? engine.evalQuiet(F.Floor(upper))
          : engine.evalQuiet(F.Plus(F.Ceiling(upper), F.CN1));
      step = -1;
    }
    if (!start.isInteger()) {
      return null;
    }
    for (int j = 0; j <= Math.max(need, 1); j++) {
      values.add(engine.evalQuiet(F.Plus(start, F.ZZ(step * j))));
    }
    return values;
  }

  /**
   * Try to find one feasible instance for a real constrained system (containing at least one
   * inequality) by delegating to the symbolic optimizers {@code Maximize}/{@code Minimize}. A
   * trivial linear objective (the first variable) is optimized over the constraint region; if the
   * region is compact the returned extremum point is a feasible instance.
   *
   * <p>
   * To avoid deep mutual recursion (the optimizers internally call {@code Solve}, which can call
   * {@code FindInstance} again) the delegation only happens at optimizer reentrancy depth
   * <code>0</code>, guarded by {@link EvalEngine#incOptimizeExpressionDepth()}.
   *
   * @param arg1 the equations/inequalities (a single condition or a list of conditions)
   * @param vars the list of variables (all must be symbols)
   * @param engine the evaluation engine
   * @return a list containing one instance <code>{{v_i -> p_i}}</code> or {@link F#NIL} if no
   *         instance could be derived this way
   */
  private static IExpr findInstanceViaOptimizer(IExpr arg1, IAST vars, EvalEngine engine) {
    if (engine.getOptimizeExpressionDepth() != 0) {
      return F.NIL;
    }
    for (int i = 1; i < vars.size(); i++) {
      if (!vars.get(i).isSymbol()) {
        return F.NIL;
      }
    }
    IAST conditions = arg1.makeList();
    if (conditions.argSize() < 1) {
      return F.NIL;
    }
    boolean hasInequality = false;
    IASTAppendable andParts = F.ast(S.And, conditions.argSize());
    for (int i = 1; i < conditions.size(); i++) {
      IExpr c = conditions.get(i);
      if (c.isAST(S.LessEqual, 3) || c.isAST(S.Less, 3) || c.isAST(S.GreaterEqual, 3)
          || c.isAST(S.Greater, 3)) {
        hasInequality = true;
      } else if (!c.isAST(S.Equal, 3)) {
        return F.NIL;
      }
      andParts.append(c);
    }
    if (!hasInequality) {
      // pure equation systems are handled by the regular Solve based path
      return F.NIL;
    }
    IExpr constraint = andParts.isAST1() ? andParts.arg1() : andParts;
    IExpr objective = vars.arg1();
    IExpr function = F.List(objective, constraint);

    engine.incOptimizeExpressionDepth();
    try {
      IExpr result = Maximize.multivariateExtremum(S.FindInstance, function, vars, true, engine);
      IExpr point = extractFeasiblePoint(result, vars);
      if (point.isPresent() && engine.evalTrue(F.subst(constraint, (IAST) point))) {
        return F.list(point);
      }
      result = Maximize.multivariateExtremum(S.FindInstance, function, vars, false, engine);
      point = extractFeasiblePoint(result, vars);
      if (point.isPresent() && engine.evalTrue(F.subst(constraint, (IAST) point))) {
        return F.list(point);
      }
    } finally {
      engine.decOptimizeExpressionDepth();
    }
    return F.NIL;
  }

  /**
   * Extract a fully determined feasible point from an optimizer result
   * <code>{value, {v_i -> p_i}}</code>. Returns {@link F#NIL} if the value is infinite /
   * indeterminate or some variable isn't determined to a value free of the variables.
   *
   * @param result the optimizer result
   * @param vars the list of variables
   * @return the rule list <code>{v_i -> p_i}</code> or {@link F#NIL}
   */
  private static IAST extractFeasiblePoint(IExpr result, IAST vars) {
    if (!result.isList2()) {
      return F.NIL;
    }
    IExpr value = ((IAST) result).first();
    if (value.isIndeterminate() || value.isInfinity() || value.isNegativeInfinity()
        || value.isDirectedInfinity() || value.isAST(S.Piecewise)) {
      return F.NIL;
    }
    IExpr rulesExpr = ((IAST) result).second();
    if (!rulesExpr.isListOfRules(false)) {
      return F.NIL;
    }
    IAST rules = (IAST) rulesExpr;
    for (int i = 1; i < rules.size(); i++) {
      IExpr rhs = ((IAST) rules.get(i)).second();
      if (rhs.isIndeterminate() || rhs.isDirectedInfinity()) {
        return F.NIL;
      }
      for (int j = 1; j < vars.size(); j++) {
        if (!rhs.isFree(vars.get(j))) {
          return F.NIL;
        }
      }
    }
    return rules;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, SolveOptions.FIND_INSTANCE_KEYS, SolveOptions.FIND_INSTANCE_DEFAULTS);
  }
}
