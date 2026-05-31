package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.longexponent.ExprMonomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;

/**
 *
 *
 * <pre>
 * <code>Maximize(unary-function, variable)
 * </code>
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns the maximum of the unary function for the given <code>variable</code>.
 *
 * </p>
 *
 * <p>
 * See
 *
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Derivative_test">Wikipedia - Derivative test</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; Maximize(-x^4-7*x^3+2*x^2 - 42,x)
 * {-42-7*(-21/8-Sqrt(505)/8)^3+2*(21/8+Sqrt(505)/8)^2-(21/8+Sqrt(505)/8)^4,{x-&gt;-21/8-Sqrt(505)/8}}
 * </code>
 * </pre>
 *
 * <p>
 * Print a message if no maximum can be found
 *
 * <pre>
 * <code>&gt;&gt; Maximize(x^4+7*x^3-2*x^2 + 42, x)
 * {Infinity,{x-&gt;-Infinity}}
 * </code>
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p>
 * <a href="Minimize.md">Minimize</a>
 */
public class Maximize extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr function = ast.arg1();
    IExpr x = ast.arg2();
    ISymbol head = ast.topHead();
    if (x.isList()) {
      if (x.isList1()) {
        x = x.first();
      } else {
        IExpr result = multivariateExtremum(head, function, (IAST) x, true, engine);
        if (result.isPresent()) {
          return result;
        }
        // `1` currently not supported in `2`.
        return Errors.printMessage(S.Maximize, "unsupported",
            F.List("Multiple variables", "Maximize"), engine);
      }
    }
    if (x.isSymbol() || (x.isAST() && !x.isList())) {
      return maximize(head, function, x, engine);
    }

    return F.NIL;
  }

  /**
   * Dispatch the supported multivariate symbolic cases:
   *
   * <ul>
   * <li>a linear objective over an axis-aligned ellipsoid / n-sphere constraint
   * <code>{a.v + c0, Sum(k_i*v_i^2) &lt;= R^2}</code></li>
   * <li>an unconstrained multivariate quadratic <code>v^T.Q.v + b.v + c0</code> with definite
   * Hessian</li>
   * </ul>
   *
   * @param head the calling symbol ({@code Maximize} or {@code Minimize}) used for messages
   * @param function the objective (or <code>{objective, constraint}</code>)
   * @param varList the list of variables
   * @param isMax <code>true</code> for a maximum, <code>false</code> for a minimum
   * @param engine the evaluation engine
   * @return the result <code>{value, {v_i -> p_i}}</code> or {@link F#NIL} if no case matches
   */
  public static IExpr multivariateExtremum(ISymbol head, IExpr function, IAST varList,
      boolean isMax, EvalEngine engine) {
    for (int i = 1; i < varList.size(); i++) {
      if (!varList.get(i).isSymbol()) {
        return F.NIL;
      }
    }
    if (function.isList2()) {
      // linear objective with a single ellipsoid/sphere constraint
      IExpr result = linearEllipsoidExtremum(head, function.first(), function.second(), varList,
          isMax, engine);
      if (result.isPresent()) {
        return result;
      }
      // exact (rational) linear programming: linear objective with linear constraints
      result = linearProgrammingExtremum(head, function.first(), function.second(), varList, isMax,
          engine);
      if (result.isPresent()) {
        return result;
      }
      // general single-constraint Lagrange / KKT case
      return lagrangeExtremum(head, function.first(), function.second(), varList, isMax, engine);
    }
    if (!function.isList()) {
      // unconstrained multivariate quadratic
      return quadraticExtremum(head, function, varList, isMax, engine);
    }
    return F.NIL;
  }

  /**
   * Compute the symbolic closed form of a linear objective <code>a.v + c0</code> optimized over an
   * axis-aligned ellipsoid constraint <code>Sum(k_i*v_i^2) &lt;= B</code> (with all
   * <code>k_i &gt; 0</code>).
   *
   * <p>
   * By the Cauchy-Schwarz / Lagrange condition the optimum value is
   * <code>c0 +/- Sqrt(B*Sum(a_i^2/k_i))</code>, attained at
   * <code>v_i = +/- (a_i/k_i)*Sqrt(B/Sum(a_j^2/k_j))</code>. The result is built as an AST and
   * evaluated, so perfect-square radicals collapse to rationals. The two-variable disk
   * <code>v1^2 + v2^2 &lt;= R^2</code> is the special case <code>k_1 = k_2 = 1</code>.
   *
   * @param head the calling symbol ({@code Maximize} or {@code Minimize}) used for messages
   * @param objective the linear objective function
   * @param constraint the ellipsoid constraint <code>Sum(k_i*v_i^2) &lt;= B</code>
   * @param varList the list of variables
   * @param isMax <code>true</code> for a maximum, <code>false</code> for a minimum
   * @param engine the evaluation engine
   * @return the result <code>{value, {v_i -> p_i}}</code> or {@link F#NIL} if the pattern doesn't
   *         match
   */
  public static IExpr linearEllipsoidExtremum(ISymbol head, IExpr objective, IExpr constraint,
      IAST varList, boolean isMax, EvalEngine engine) {
    try {
      // constraint must be `lhs <= rhs`
      if (!constraint.isAST(S.LessEqual, 3)) {
        return F.NIL;
      }
      IExpr lhs = constraint.first();
      IExpr rhs = constraint.second();
      int n = varList.argSize();
      IExpr[] vars = new IExpr[n];
      for (int i = 0; i < n; i++) {
        vars[i] = varList.get(i + 1);
      }

      // extract linear objective coefficients: objective == Sum(a_i*v_i) + c0
      IExpr[] a = new IExpr[n];
      IExpr linApprox = F.NIL;
      boolean allZero = true;
      for (int i = 0; i < n; i++) {
        a[i] = S.Coefficient.of(engine, objective, vars[i]);
        if (!isFreeOfAll(a[i], vars)) {
          return F.NIL;
        }
        linApprox = linApprox.isPresent() ? F.Plus(linApprox, F.Times(a[i], vars[i]))
            : F.Times(a[i], vars[i]);
        if (!a[i].isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          allZero = false;
        }
      }
      IExpr c0 = replaceAllByZero(objective, vars, engine);
      if (!isFreeOfAll(c0, vars)) {
        return F.NIL;
      }
      IExpr linearRemainder =
          engine.evaluate(F.Subtract(objective, F.Plus(linApprox, c0)));
      if (!linearRemainder.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        return F.NIL;
      }
      if (allZero) {
        // The `1` is not attained at any point satisfying the constraints.
        return Errors.printMessage(head, "natt", F.List(isMax ? "maximum" : "minimum"));
      }

      // extract ellipsoid constraint: lhs == Sum(k_i*v_i^2) + constLhs, no cross/linear terms
      IExpr[] k = new IExpr[n];
      IExpr quadApprox = F.NIL;
      IExpr s2 = F.C0; // Sum(a_i^2 / k_i)
      for (int i = 0; i < n; i++) {
        k[i] = S.Coefficient.of(engine, lhs, vars[i], F.C2);
        if (!isFreeOfAll(k[i], vars) || !k[i].isPositiveResult()) {
          return F.NIL;
        }
        IExpr lin = S.Coefficient.of(engine, lhs, vars[i], F.C1);
        if (!lin.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          return F.NIL;
        }
        IExpr term = F.Times(k[i], F.Sqr(vars[i]));
        quadApprox = quadApprox.isPresent() ? F.Plus(quadApprox, term) : term;
        s2 = F.Plus(s2, F.Divide(F.Sqr(a[i]), k[i]));
      }
      IExpr constLhs = replaceAllByZero(lhs, vars, engine);
      // verify there are no cross terms and nothing else
      IExpr quadRemainder = engine.evaluate(F.Subtract(lhs, F.Plus(quadApprox, constLhs)));
      if (!quadRemainder.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        return F.NIL;
      }

      // Sum(k_i*v_i^2) <= B with B = rhs - constLhs
      IExpr b = engine.evaluate(F.Subtract(rhs, constLhs));
      IExpr sumA2K = engine.evaluate(s2);
      IExpr factor = isMax ? F.C1 : F.CN1;
      IExpr normFactor = F.Sqrt(F.Divide(b, sumA2K)); // Sqrt(B / Sum(a_i^2/k_i))

      IExpr value = F.Plus(c0, F.Times(factor, F.Sqrt(F.Times(b, sumA2K))));
      IASTAppendable rules = F.ListAlloc(n);
      for (int i = 0; i < n; i++) {
        rules.append(F.Rule(vars[i], F.Times(factor, F.Divide(a[i], k[i]), normFactor)));
      }
      return engine.evaluate(F.list(value, rules));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(head, rex);
    }
  }

  /**
   * Compute the symbolic closed form of an unconstrained multivariate quadratic
   * <code>f = v^T.Q.v + b.v + c0</code>. If the Hessian is positive definite (for a minimum) or
   * negative definite (for a maximum) there is a unique extremum at the solution of
   * <code>Grad(f) == 0</code>.
   *
   * @param head the calling symbol ({@code Maximize} or {@code Minimize}) used for messages
   * @param function the quadratic objective function
   * @param varList the list of variables
   * @param isMax <code>true</code> for a maximum, <code>false</code> for a minimum
   * @param engine the evaluation engine
   * @return the result <code>{value, {v_i -> p_i}}</code> or {@link F#NIL} if the function isn't a
   *         quadratic with definite Hessian
   */
  public static IExpr quadraticExtremum(ISymbol head, IExpr function, IAST varList, boolean isMax,
      EvalEngine engine) {
    try {
      int n = varList.argSize();
      IExpr[] vars = new IExpr[n];
      IExpr[] grad = new IExpr[n];
      IASTAppendable equations = F.ListAlloc(n);
      for (int i = 0; i < n; i++) {
        vars[i] = varList.get(i + 1);
        grad[i] = S.D.of(engine, function, vars[i]);
        equations.append(F.Equal(grad[i], F.C0));
      }
      // Hessian must be constant (function quadratic in the variables)
      IExpr[][] hessian = new IExpr[n][n];
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          IExpr hij = S.D.of(engine, grad[i], vars[j]);
          if (!isFreeOfAll(hij, vars)) {
            return F.NIL;
          }
          hessian[i][j] = hij;
        }
      }
      // definiteness via Sylvester's criterion: for a maximum test -Hessian
      IExpr[][] testMatrix = hessian;
      if (isMax) {
        testMatrix = new IExpr[n][n];
        for (int i = 0; i < n; i++) {
          for (int j = 0; j < n; j++) {
            testMatrix[i][j] = engine.evaluate(F.Negate(hessian[i][j]));
          }
        }
      }
      if (!isPositiveDefinite(testMatrix, n, engine)) {
        return F.NIL;
      }
      IExpr solution = S.Solve.of(engine, equations, varList, S.Reals);
      if (!solution.isListOfLists() || solution.size() < 2) {
        return F.NIL;
      }
      IAST rules = (IAST) solution.first();
      IExpr value = function;
      for (int i = 1; i < rules.size(); i++) {
        IAST rule = (IAST) rules.get(i);
        value = F.xreplace(value, rule.first(), rule.second());
      }
      value = engine.evaluate(value);
      return engine.evaluate(F.list(value, rules));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(head, rex);
    }
  }

  /**
   * Compute the symbolic extrema of an objective subject to a single smooth constraint using the
   * Lagrange / KKT conditions. The constraint may be an equality (<code>g == c</code>) or an
   * inequality (<code>g &lt;= c</code> / <code>g &gt;= c</code>).
   *
   * <p>
   * Candidate points are collected from the interior stationary points (for inequality constraints)
   * and from the boundary by solving <code>Grad(f) == lambda*Grad(g)</code> together with
   * <code>g == c</code>. The objective is evaluated at every feasible candidate and the global
   * minimum / maximum is returned. This works for the global optimum whenever the feasible region is
   * compact (e.g. a disk / ellipsoid).
   *
   * @param head the calling symbol ({@code Maximize} or {@code Minimize}) used for messages
   * @param objective the objective function
   * @param constraint the single equality or inequality constraint
   * @param varList the list of variables
   * @param isMax <code>true</code> for a maximum, <code>false</code> for a minimum
   * @param engine the evaluation engine
   * @return the result <code>{value, {v_i -> p_i}}</code> or {@link F#NIL} if no candidate is found
   */
  public static IExpr lagrangeExtremum(ISymbol head, IExpr objective, IExpr constraint,
      IAST varList, boolean isMax, EvalEngine engine) {
    try {
      int n = varList.argSize();
      IExpr[] vars = new IExpr[n];
      for (int i = 0; i < n; i++) {
        vars[i] = varList.get(i + 1);
      }

      // parse the constraint into a boundary expression gExpr (gExpr == 0 on the boundary)
      IExpr gExpr;
      boolean allowInterior;
      int interiorSign; // -1: feasible where gExpr < 0 ; +1: gExpr > 0 ; 0: equality (no interior)
      if (constraint.isAST(S.Equal, 3)) {
        gExpr = engine.evaluate(F.Subtract(constraint.first(), constraint.second()));
        allowInterior = false;
        interiorSign = 0;
      } else if (constraint.isAST(S.LessEqual, 3)) {
        gExpr = engine.evaluate(F.Subtract(constraint.first(), constraint.second()));
        allowInterior = true;
        interiorSign = -1;
      } else if (constraint.isAST(S.GreaterEqual, 3)) {
        gExpr = engine.evaluate(F.Subtract(constraint.first(), constraint.second()));
        allowInterior = true;
        interiorSign = 1;
      } else {
        return F.NIL;
      }

      List<IExpr> candidateValues = new ArrayList<>();
      List<IAST> candidateRules = new ArrayList<>();

      // interior stationary points (only for inequality constraints)
      if (allowInterior) {
        IASTAppendable equations = F.ListAlloc(n);
        for (int i = 0; i < n; i++) {
          equations.append(F.Equal(S.D.of(engine, objective, vars[i]), F.C0));
        }
        IExpr solution = S.Solve.of(engine, equations, varList, S.Reals);
        collectCandidates(solution, vars, objective, gExpr, interiorSign, true, candidateValues,
            candidateRules, engine);
      }

      // boundary points: Grad(f) parallel to Grad(g) on g == 0.
      // Instead of introducing a Lagrange multiplier we require that all 2x2 minors of the
      // Jacobian [Grad(f); Grad(g)] vanish; this keeps the system in the original variables and is
      // much more reliably solvable.
      IExpr[] df = new IExpr[n];
      IExpr[] dg = new IExpr[n];
      for (int i = 0; i < n; i++) {
        df[i] = S.D.of(engine, objective, vars[i]);
        dg[i] = S.D.of(engine, gExpr, vars[i]);
      }
      IASTAppendable equations = F.ListAlloc(n);
      for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
          equations
              .append(F.Equal(F.Subtract(F.Times(df[i], dg[j]), F.Times(df[j], dg[i])), F.C0));
        }
      }
      equations.append(F.Equal(gExpr, F.C0));
      IExpr solution = S.Solve.of(engine, equations, varList, S.Reals);
      collectCandidates(solution, vars, objective, gExpr, interiorSign, false, candidateValues,
          candidateRules, engine);

      if (candidateValues.isEmpty()) {
        return F.NIL;
      }

      // select the global minimum / maximum (deterministic lexicographic tie-break)
      int best = -1;
      IExpr bestValue = F.NIL;
      for (int i = 0; i < candidateValues.size(); i++) {
        IExpr value = candidateValues.get(i);
        if (best < 0) {
          best = i;
          bestValue = value;
          continue;
        }
        boolean better = isMax ? value.greater(bestValue).isTrue() : value.less(bestValue).isTrue();
        if (better) {
          best = i;
          bestValue = value;
        } else if (value.equals(bestValue)
            && isLexSmaller(candidateRules.get(i), candidateRules.get(best))) {
          best = i;
          bestValue = value;
        }
      }
      return engine.evaluate(F.list(bestValue, candidateRules.get(best)));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(head, rex);
    }
  }

  /**
   * Collect feasible candidate points from a {@code Solve} result, evaluating the objective at each
   * point. Interior candidates must satisfy the strict inequality given by <code>interiorSign</code>.
   */
  private static void collectCandidates(IExpr solution, IExpr[] vars, IExpr objective, IExpr gExpr,
      int interiorSign, boolean interior, List<IExpr> values, List<IAST> rulesList,
      EvalEngine engine) {
    if (!solution.isListOfLists()) {
      return;
    }
    IAST solutions = (IAST) solution;
    for (int s = 1; s < solutions.size(); s++) {
      IAST solutionRules = (IAST) solutions.get(s);
      IAST ordered = orderedVarRules(solutionRules, vars);
      if (!ordered.isPresent()) {
        continue;
      }
      // every variable must be determined to a value free of the variables
      boolean determined = true;
      for (int i = 1; i < ordered.size(); i++) {
        IExpr value = ((IAST) ordered.get(i)).second();
        if (!isFreeOfAll(value, vars)) {
          determined = false;
          break;
        }
      }
      if (!determined) {
        continue;
      }
      if (interior) {
        IExpr gAtPoint = engine.evaluate(applyRules(gExpr, ordered));
        if (interiorSign < 0 && !gAtPoint.isNegativeResult()) {
          continue;
        }
        if (interiorSign > 0 && !gAtPoint.isPositiveResult()) {
          continue;
        }
      }
      IExpr functionValue = engine.evaluate(applyRules(objective, ordered));
      if (!isFreeOfAll(functionValue, vars)) {
        continue;
      }
      values.add(functionValue);
      rulesList.add(ordered);
    }
  }

  /**
   * Build a rule list <code>{v_1 -> .., v_n -> ..}</code> in the order of <code>vars</code> from a
   * {@code Solve} solution (ignoring extra variables like the Lagrange multiplier). Returns
   * {@link F#NIL} if some variable isn't determined.
   */
  private static IAST orderedVarRules(IAST solutionRules, IExpr[] vars) {
    IASTAppendable rules = F.ListAlloc(vars.length);
    for (IExpr var : vars) {
      IExpr value = F.NIL;
      for (int i = 1; i < solutionRules.size(); i++) {
        IAST rule = (IAST) solutionRules.get(i);
        if (rule.first().equals(var)) {
          value = rule.second();
          break;
        }
      }
      if (!value.isPresent()) {
        return F.NIL;
      }
      rules.append(F.Rule(var, value));
    }
    return rules;
  }

  /** Apply a list of replacement rules to an expression (without evaluating). */
  private static IExpr applyRules(IExpr expr, IAST rules) {
    IExpr result = expr;
    for (int i = 1; i < rules.size(); i++) {
      IAST rule = (IAST) rules.get(i);
      result = F.xreplace(result, rule.first(), rule.second());
    }
    return result;
  }

  /**
   * Lexicographic comparison of two ordered rule lists by their right-hand values. Used as a
   * deterministic tie-break preferring the "smallest" point among equal optima.
   */
  private static boolean isLexSmaller(IAST a, IAST b) {
    for (int i = 1; i < a.size(); i++) {
      IExpr av = ((IAST) a.get(i)).second();
      IExpr bv = ((IAST) b.get(i)).second();
      if (av.less(bv).isTrue()) {
        return true;
      }
      if (av.greater(bv).isTrue()) {
        return false;
      }
    }
    return false;
  }

  /**
   * Test whether an <code>n x n</code> symmetric matrix is positive definite using Sylvester's
   * criterion (all leading principal minors are positive).
   */
  private static boolean isPositiveDefinite(IExpr[][] matrix, int n, EvalEngine engine) {
    for (int size = 1; size <= n; size++) {
      IASTAppendable sub = F.ListAlloc(size);
      for (int i = 0; i < size; i++) {
        IASTAppendable row = F.ListAlloc(size);
        for (int j = 0; j < size; j++) {
          row.append(matrix[i][j]);
        }
        sub.append(row);
      }
      IExpr det = S.Det.of(engine, sub);
      if (!det.isPositiveResult()) {
        return false;
      }
    }
    return true;
  }
  /**
   * Replace every variable in <code>vars</code> by <code>0</code> in <code>expr</code> and evaluate
   * (used to extract the constant term).
   */
  private static IExpr replaceAllByZero(IExpr expr, IExpr[] vars, EvalEngine engine) {
    IExpr result = expr;
    for (IExpr var : vars) {
      result = F.xreplace(result, var, F.C0);
    }
    return engine.evaluate(result);
  }

  /** Return <code>true</code> if <code>expr</code> is free of all the given variables. */
  private static boolean isFreeOfAll(IExpr expr, IExpr[] vars) {
    for (IExpr var : vars) {
      if (!expr.isFree(var)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Exact (rational) linear programming. Optimizes a linear (affine) objective over a feasible
   * region described by a conjunction of linear equalities / inequalities. Uses
   * <a href="https://en.wikipedia.org/wiki/Vertex_enumeration_problem">vertex enumeration</a> and
   * returns the exact rational optimum. Following {@code Minimize}/{@code Maximize} conventions,
   * the variables range over all real numbers (no implicit non-negativity).
   *
   * <p>
   * Unbounded problems are detected by searching the recession cone for an objective-improving
   * extreme ray; in that case {@code Infinity}/{@code -Infinity} is returned.
   *
   * @param head the calling symbol ({@code Maximize} or {@code Minimize}) used for messages
   * @param objective the linear objective function
   * @param constraint a single linear (in)equality or an {@code And} of them
   * @param varList the list of variables
   * @param isMax <code>true</code> for a maximum, <code>false</code> for a minimum
   * @param engine the evaluation engine
   * @return the result <code>{value, {v_i -> p_i}}</code> or {@link F#NIL} if the pattern doesn't
   *         match
   */
  public static IExpr linearProgrammingExtremum(ISymbol head, IExpr objective, IExpr constraint,
      IAST varList, boolean isMax, EvalEngine engine) {
    try {
      int n = varList.argSize();
      IExpr[] vars = new IExpr[n];
      for (int i = 0; i < n; i++) {
        vars[i] = varList.get(i + 1);
      }
      // objective must be affine in the variables
      IExpr[] objCoeff = linearCoefficients(objective, vars, engine);
      if (objCoeff == null) {
        return F.NIL;
      }

      // collect the constraints (flatten a top-level And)
      List<IExpr> conjuncts = new ArrayList<>();
      if (constraint.isAST(S.And)) {
        for (int i = 1; i < constraint.size(); i++) {
          conjuncts.add(constraint.get(i));
        }
      } else {
        conjuncts.add(constraint);
      }

      // each constraint becomes a boundary expression E (E <= 0 for inequalities, E == 0 for
      // equalities) plus its normal vector
      List<IExpr> boundary = new ArrayList<>();
      List<Boolean> isEquality = new ArrayList<>();
      List<IExpr[]> normals = new ArrayList<>();
      for (IExpr c : conjuncts) {
        IExpr expr;
        boolean eq;
        if (c.isAST(S.LessEqual, 3) || c.isAST(S.Less, 3)) {
          expr = F.Subtract(c.first(), c.second());
          eq = false;
        } else if (c.isAST(S.GreaterEqual, 3) || c.isAST(S.Greater, 3)) {
          expr = F.Subtract(c.second(), c.first());
          eq = false;
        } else if (c.isAST(S.Equal, 3)) {
          expr = F.Subtract(c.first(), c.second());
          eq = true;
        } else {
          return F.NIL; // unsupported constraint type
        }
        expr = engine.evaluate(expr);
        IExpr[] coeff = linearCoefficients(expr, vars, engine);
        if (coeff == null) {
          return F.NIL; // not linear
        }
        IExpr[] normal = new IExpr[n];
        System.arraycopy(coeff, 0, normal, 0, n);
        boundary.add(expr);
        isEquality.add(eq);
        normals.add(normal);
      }
      int m = boundary.size();

      // unbounded detection: look for an objective-improving extreme ray of the recession cone
      // (intersection of n-1 constraint hyperplanes through the origin)
      for (int[] combo : combinations(m, n - 1)) {
        IExpr nullSpace;
        if (combo.length == 0) {
          nullSpace = F.NIL;
        } else {
          IASTAppendable matrix = F.ListAlloc(combo.length);
          for (int idx : combo) {
            IExpr[] nb = normals.get(idx);
            IASTAppendable row = F.ListAlloc(n);
            for (int j = 0; j < n; j++) {
              row.append(nb[j]);
            }
            matrix.append(row);
          }
          nullSpace = S.NullSpace.of(engine, matrix);
        }
        if (!nullSpace.isList()) {
          continue;
        }
        IAST nsList = (IAST) nullSpace;
        for (int r = 1; r < nsList.size(); r++) {
          IExpr dir = nsList.get(r);
          if (!dir.isList() || ((IAST) dir).argSize() != n) {
            continue;
          }
          IExpr[] base = new IExpr[n];
          for (int j = 0; j < n; j++) {
            base[j] = ((IAST) dir).get(j + 1);
          }
          for (int sign = 0; sign < 2; sign++) {
            IExpr[] ray = base;
            if (sign == 1) {
              ray = new IExpr[n];
              for (int j = 0; j < n; j++) {
                ray[j] = engine.evaluate(F.Negate(base[j]));
              }
            }
            if (isRecession(ray, normals, isEquality, engine)
                && isImproving(objCoeff, ray, isMax, engine)) {
              IASTAppendable rules = F.ListAlloc(n);
              for (int j = 0; j < n; j++) {
                rules.append(F.Rule(vars[j], S.Indeterminate));
              }
              return F.list(isMax ? F.CInfinity : F.CNInfinity, rules);
            }
          }
        }
      }

      // vertex enumeration: choose n boundary hyperplanes, solve, keep feasible vertices
      IExpr bestValue = F.NIL;
      IAST bestRules = F.NIL;
      for (int[] combo : combinations(m, n)) {
        IASTAppendable equations = F.ListAlloc(n);
        for (int idx : combo) {
          equations.append(F.Equal(boundary.get(idx), F.C0));
        }
        IExpr solution = S.Solve.of(engine, equations, varList, S.Reals);
        if (!solution.isListOfLists()) {
          continue;
        }
        IAST solutions = (IAST) solution;
        for (int s = 1; s < solutions.size(); s++) {
          IAST ordered = orderedVarRules((IAST) solutions.get(s), vars);
          if (!ordered.isPresent()) {
            continue;
          }
          boolean determined = true;
          for (int i = 1; i < ordered.size(); i++) {
            if (!isFreeOfAll(((IAST) ordered.get(i)).second(), vars)) {
              determined = false;
              break;
            }
          }
          if (!determined || !isFeasible(boundary, isEquality, ordered, engine)) {
            continue;
          }
          IExpr value = engine.evaluate(applyRules(objective, ordered));
          if (!isFreeOfAll(value, vars)) {
            continue;
          }
          if (!bestValue.isPresent()) {
            bestValue = value;
            bestRules = ordered;
          } else {
            boolean better =
                isMax ? value.greater(bestValue).isTrue() : value.less(bestValue).isTrue();
            if (better || (value.equals(bestValue) && isLexSmaller(ordered, bestRules))) {
              bestValue = value;
              bestRules = ordered;
            }
          }
        }
      }
      if (bestValue.isPresent()) {
        return engine.evaluate(F.list(bestValue, bestRules));
      }
      return F.NIL;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(head, rex);
    }
  }

  /**
   * Extract the affine coefficients of <code>expr</code> with respect to <code>vars</code>. Returns
   * an array of length <code>n+1</code> ({@code c_0..c_{n-1}}, constant) or <code>null</code> if
   * <code>expr</code> isn't affine in the variables.
   */
  private static IExpr[] linearCoefficients(IExpr expr, IExpr[] vars, EvalEngine engine) {
    int n = vars.length;
    IExpr[] res = new IExpr[n + 1];
    IExpr approx = F.NIL;
    for (int i = 0; i < n; i++) {
      IExpr ci = S.Coefficient.of(engine, expr, vars[i]);
      if (!isFreeOfAll(ci, vars)) {
        return null;
      }
      res[i] = ci;
      approx = approx.isPresent() ? F.Plus(approx, F.Times(ci, vars[i])) : F.Times(ci, vars[i]);
    }
    IExpr constant = replaceAllByZero(expr, vars, engine);
    if (!isFreeOfAll(constant, vars)) {
      return null;
    }
    res[n] = constant;
    IExpr remainder =
        engine.evaluate(F.Subtract(expr, F.Plus(approx.isPresent() ? approx : F.C0, constant)));
    if (!remainder.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
      return null;
    }
    return res;
  }

  /** Test whether <code>point</code> satisfies every constraint (E &lt;= 0 or E == 0). */
  private static boolean isFeasible(List<IExpr> boundary, List<Boolean> isEquality, IAST point,
      EvalEngine engine) {
    for (int i = 0; i < boundary.size(); i++) {
      IExpr value = engine.evaluate(applyRules(boundary.get(i), point));
      if (isEquality.get(i)) {
        if (!value.isZero()) {
          return false;
        }
      } else if (!(value.isZero() || value.isNegativeResult())) {
        return false;
      }
    }
    return true;
  }

  /** Test whether <code>ray</code> belongs to the recession cone of the feasible region. */
  private static boolean isRecession(IExpr[] ray, List<IExpr[]> normals, List<Boolean> isEquality,
      EvalEngine engine) {
    for (int i = 0; i < normals.size(); i++) {
      IExpr d = dot(normals.get(i), ray, engine);
      if (isEquality.get(i)) {
        if (!d.isZero()) {
          return false;
        }
      } else if (!(d.isZero() || d.isNegativeResult())) {
        return false;
      }
    }
    return true;
  }

  /** Test whether moving along <code>ray</code> strictly improves the objective. */
  private static boolean isImproving(IExpr[] objCoeff, IExpr[] ray, boolean isMax,
      EvalEngine engine) {
    IExpr d = F.C0;
    for (int j = 0; j < ray.length; j++) {
      d = F.Plus(d, F.Times(objCoeff[j], ray[j]));
    }
    d = engine.evaluate(d);
    return isMax ? d.isPositiveResult() : d.isNegativeResult();
  }

  /** Exact dot product of two vectors. */
  private static IExpr dot(IExpr[] a, IExpr[] b, EvalEngine engine) {
    IExpr d = F.C0;
    for (int j = 0; j < a.length; j++) {
      d = F.Plus(d, F.Times(a[j], b[j]));
    }
    return engine.evaluate(d);
  }

  /** Generate all <code>k</code>-element index combinations from <code>{0, .., m-1}</code>. */
  private static List<int[]> combinations(int m, int k) {
    List<int[]> result = new ArrayList<>();
    if (k < 0 || k > m) {
      return result;
    }
    if (k == 0) {
      result.add(new int[0]);
      return result;
    }
    int[] combo = new int[k];
    for (int i = 0; i < k; i++) {
      combo[i] = i;
    }
    while (true) {
      result.add(combo.clone());
      int i = k - 1;
      while (i >= 0 && combo[i] == m - k + i) {
        i--;
      }
      if (i < 0) {
        break;
      }
      combo[i]++;
      for (int j = i + 1; j < k; j++) {
        combo[j] = combo[j - 1] + 1;
      }
    }
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  public static IAST maximizeCubicPolynomial(ExprPolynomial polynomial, IExpr x) {
    long varDegree = polynomial.degree(0);
    IExpr a;
    IExpr b;
    IExpr c;
    IExpr d;
    IExpr e;
    if (varDegree <= 3) {
      // solve cubic or quadratic maximize:
      a = F.C0;
      b = F.C0;
      c = F.C0;
      d = F.C0;
      e = F.C0;
      for (ExprMonomial monomial : polynomial) {
        IExpr coeff = monomial.coefficient();
        long lExp = monomial.exponent().getVal(0);
        if (lExp == 4) {
          a = coeff;
        } else if (lExp == 3) {
          b = coeff;
        } else if (lExp == 2) {
          c = coeff;
        } else if (lExp == 1) {
          d = coeff;
        } else if (lExp == 0) {
          e = coeff;
        } else {
          throw new ArithmeticException("Maximize::Unexpected exponent value: " + lExp);
        }
      }
      if (a.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        if (b.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          // quadratic
          if (c.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
            if (d.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
              // The `1` is not attained at any point satisfying the constraints.
              return Errors.printMessage(S.Maximize, "natt", F.List("maximum"));
            } else {
              // linear
              return F.list(F.Piecewise(F.list(F.list(e, F.Equal(d, F.C0))), F.CInfinity), F.list(
                  F.Rule(x, F.Piecewise(F.list(F.list(F.C0, F.Equal(d, F.C0))), S.Indeterminate))));
            }
          } else {
            return F
                .List(
                    F.Piecewise(
                        F.list(F.list(e, F.And(F.Equal(d, 0), F.LessEqual(c, 0))),
                            F.list(
                                F.Times(F.C1D4, F.Power(c, -1),
                                    F.Plus(F.Times(-1, F.Power(d, 2)), F.Times(4, c, e))),
                                F.Or(F.And(F.Greater(d, 0), F.Less(c, 0)),
                                    F.And(F.Less(d, 0), F.Less(c, 0))))),
                        F.CInfinity),
                    F.list(F.Rule(x,
                        F.Piecewise(
                            F.list(
                                F.list(F.Times(F.CN1D2, F.Power(c, -1), d),
                                    F.Or(F.And(F.Greater(d, 0), F.Less(c, 0)),
                                        F.And(F.Less(d, 0), F.Less(c, 0)))),
                                F.list(F.C0, F.And(F.Equal(d, 0), F.LessEqual(c, 0)))),
                            S.Indeterminate))));
          }
        } else {
          // cubic
          return F.list(
              F.Piecewise(
                  F.list(
                      F.list(e,
                          F.Or(F.And(F.Equal(d, F.C0), F.Equal(c, F.C0), F.Equal(b, F.C0)),
                              F.And(F.Equal(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0)))),
                      F.list(
                          F.Times(F.C1D4, F.Power(c, F.CN1),
                              F.Plus(F.Negate(F.Sqr(d)), F.Times(F.C4, c, e))),
                          F.Or(F.And(F.Greater(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0)),
                              F.And(F.Less(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0))))),
                  F.oo),
              F.list(F.Rule(x,
                  F.Piecewise(
                      F.list(
                          F.list(F.Times(F.CN1D2, F.Power(c, F.CN1), d),
                              F.Or(F.And(F.Greater(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0)),
                                  F.And(F.Less(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0)))),
                          F.list(F.C0,
                              F.Or(F.And(F.Equal(d, F.C0), F.Equal(c, F.C0), F.Equal(b, F.C0)),
                                  F.And(F.Equal(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0))))),
                      F.Indeterminate))));
        }
      }
    }
  
    return F.NIL;
  }

  public static IAST maximizeExprPolynomial(final IExpr expr, IAST varList) {
    IAST result = F.NIL;
    try {
      // try to generate a common expression polynomial
      ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, varList);
      ExprPolynomial ePoly = ring.create(expr, false, false, false);
      ePoly = ePoly.multiplyByMinimumNegativeExponents();
      result = Maximize.maximizeCubicPolynomial(ePoly, varList.arg1());
  
      // result = QuarticSolver.sortASTArguments(result);
      return result;
    } catch (ArithmeticException | JASConversionException e2) {
      return Errors.printMessage(S.Maximize, e2);
    }
  }

  public static IExpr maximize(ISymbol head, IExpr function, IExpr x, EvalEngine engine) {
    try {
      IExpr temp = maximizeExprPolynomial(function, F.list(x));
      if (temp.isPresent()) {
        return temp;
      }
  
      IExpr yNInf = S.Limit.funEval(function, F.Rule(x, F.CNInfinity));
      if (yNInf.isInfinity()) {
        // MinMaxFunctions.LOGGER.log(engine.getLogLevel(), "{}: the maximum cannot be found.",
        // head);
        return F.list(F.CInfinity, F.list(F.Rule(x, F.CNInfinity)));
      }
      IExpr yInf = S.Limit.funEval(function, F.Rule(x, F.CInfinity));
      if (yInf.isInfinity()) {
        // MinMaxFunctions.LOGGER.log(engine.getLogLevel(), "{}: the maximum cannot be found.",
        // head);
        return F.list(F.CInfinity, F.list(F.Rule(x, F.CInfinity)));
      }
  
      IExpr first_derivative = S.D.of(engine, function, x);
      IExpr second_derivative = S.D.funEval(engine, first_derivative, x);
      IExpr candidates = S.Solve.of(engine, F.Equal(first_derivative, F.C0), x, S.Reals);
      if (candidates.isFree(S.Solve)) {
        IExpr maxCandidate = F.NIL;
        IExpr maxValue = F.CNInfinity;
        if (candidates.isListOfLists()) {
          for (int i = 1; i < candidates.size(); i++) {
            IExpr candidate = ((IAST) candidates).get(i).first().second();
            IExpr value = engine.evaluate(F.xreplace(second_derivative, x, candidate));
            if (value.isNegative()) {
              IExpr functionValue = engine.evaluate(F.xreplace(function, x, candidate));
              if (functionValue.greater(maxValue).isTrue()) {
                // if (S.Greater.ofQ(functionValue, maxValue)) {
                maxValue = functionValue;
                maxCandidate = candidate;
              }
            }
          }
          if (maxCandidate.isPresent()) {
            return F.list(maxValue, F.list(F.Rule(x, maxCandidate)));
          }
        }
        return F.CEmptyList;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      // MinMaxFunctions.LOGGER.log(engine.getLogLevel(), head, rex);
      return Errors.printMessage(S.Maximize, rex);
    }
    return F.NIL;
  }
}
