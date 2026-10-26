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
      if (argSize >= 3) {
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

      if ((domain == S.Reals || domain == S.Complexes) && maxChoices == 1) {
        // inequalities in one variable, wherever their solution set lies
        IExpr instance = univariateInequalityInstance(ast.arg1(), vars, engine);
        if (instance.isPresent()) {
          return instance;
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
   * One instance of a condition with inequalities in a single variable: the condition is reduced
   * to intervals and the instance is an integer of the first one, as <code>x &gt; 1000</code> gives
   * <code>x -&gt; 1001</code>, or its midpoint if it holds no integer. The search box of the
   * optimizer would miss an interval far from the origin.
   *
   * @return <code>{{x -&gt; value}}</code> or {@link F#NIL}
   */
  private static IExpr univariateInequalityInstance(IExpr arg1, IAST vars, EvalEngine engine) {
    if (vars.argSize() != 1 || !vars.arg1().isSymbol()
        || engine.getOptimizeExpressionDepth() != 0) {
      return F.NIL;
    }
    final IExpr x = vars.arg1();
    IAST conditions = arg1.isAnd() ? (IAST) arg1 : arg1.makeList();
    boolean hasInequality = false;
    for (IExpr condition : conditions) {
      if (condition.isAST(S.LessEqual) || condition.isAST(S.Less)
          || condition.isAST(S.GreaterEqual) || condition.isAST(S.Greater)
          || condition.isAST(S.Inequality)) {
        hasInequality = true;
      } else if (!condition.isAST(S.Equal, 3) && !condition.isAST(S.Unequal, 3)) {
        return F.NIL;
      }
      if (!VariablesSet.getVariables(condition).forAll(v -> v.equals(x))) {
        return F.NIL;
      }
    }
    if (!hasInequality) {
      return F.NIL;
    }
    final IExpr condition = conditions.argSize() == 1 ? conditions.arg1() : conditions.apply(S.And);
    IExpr reduced = engine.evalQuiet(F.Reduce(condition, x, S.Reals));
    if (reduced.isFalse()) {
      return F.CEmptyList;
    }
    IAST branches = reduced.isOr() ? (IAST) reduced : F.list(reduced);
    for (IExpr branch : branches) {
      for (IExpr candidate : candidates(branch, x)) {
        IExpr value = engine.evalQuiet(candidate);
        if (value.isNumber() && value.isReal()
            && engine.evalTrue(F.subst(condition, x, value))) {
          return F.list(F.list(F.Rule(x, value)));
        }
      }
    }
    return F.NIL;
  }

  /** The values to try for an instance of a reduced condition on <code>x</code>. */
  private static java.util.List<IExpr> candidates(IExpr branch, IExpr x) {
    java.util.List<IExpr> candidates = new java.util.ArrayList<IExpr>();
    if (branch.isAnd()) {
      // an interval x > a && x < b
      IExpr lower = F.NIL;
      IExpr upper = F.NIL;
      for (IExpr bound : (IAST) branch) {
        candidates.addAll(candidates(bound, x));
        if ((bound.isAST(S.Greater, 3) || bound.isAST(S.GreaterEqual, 3))
            && bound.first().equals(x)) {
          lower = bound.second();
        } else if ((bound.isAST(S.Less, 3) || bound.isAST(S.LessEqual, 3))
            && bound.first().equals(x)) {
          upper = bound.second();
        }
      }
      if (lower.isPresent() && upper.isPresent()) {
        candidates.add(F.Floor(F.Times(F.C1D2, F.Plus(lower, upper))));
        candidates.add(F.Times(F.C1D2, F.Plus(lower, upper)));
      }
    } else if (branch.isTrue()) {
      candidates.add(F.C0);
    } else if (branch.isAST(S.Equal, 3) && branch.first().equals(x)) {
      candidates.add(branch.second());
    } else if (branch.isAST(S.Unequal, 3) && branch.first().equals(x)) {
      candidates.add(F.Plus(F.Floor(branch.second()), F.C1));
    } else if (branch.isAST(S.Greater, 3) && branch.first().equals(x)) {
      candidates.add(F.Plus(F.Floor(branch.second()), F.C1));
    } else if (branch.isAST(S.GreaterEqual, 3) && branch.first().equals(x)) {
      candidates.add(F.Ceiling(branch.second()));
    } else if (branch.isAST(S.Less, 3) && branch.first().equals(x)) {
      candidates.add(F.Plus(F.Ceiling(branch.second()), F.CN1));
    } else if (branch.isAST(S.LessEqual, 3) && branch.first().equals(x)) {
      candidates.add(F.Floor(branch.second()));
    } else if (branch.isAST(S.Inequality, 6) && ((IAST) branch).arg3().equals(x)) {
      IAST inequality = (IAST) branch;
      IExpr lower = inequality.arg1();
      IExpr upper = inequality.arg5();
      candidates.add(inequality.arg2() == S.Less ? F.Plus(F.Floor(lower), F.C1) : F.Ceiling(lower));
      candidates.add(F.Floor(F.Times(F.C1D2, F.Plus(lower, upper))));
      candidates.add(F.Times(F.C1D2, F.Plus(lower, upper)));
    }
    return candidates;
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
      if (point.isPresent()) {
        return F.list(point);
      }
      result = Maximize.multivariateExtremum(S.FindInstance, function, vars, false, engine);
      point = extractFeasiblePoint(result, vars);
      if (point.isPresent()) {
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
