package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * AsymptoticDSolveValue(equation, y(x), {x, x0, order})
 * </pre>
 *
 * <blockquote>
 * <p>
 * Finds a series expansion solution to a differential equation around the point <code>x0</code> up
 * to the specified <code>order</code>.
 * </p>
 * </blockquote>
 */
public class AsymptoticDSolveValue extends AbstractFunctionOptionEvaluator {
  private final static boolean DEBUG = false;

  public AsymptoticDSolveValue() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {

    // 1. Parse arguments and series specification
    IAST eqns = ast.arg1().makeList();
    IExpr yFunc = ast.arg2();

    if (!yFunc.isAST1()) {
      return F.NIL;
    }
    IExpr head = yFunc.head();

    IAST xSpec = ast.arg3().isList() ? (IAST) ast.arg3() : F.NIL;
    if (xSpec.isNIL() || xSpec.argSize() < 3) {
      return F.NIL;
    }
    IExpr xVar = xSpec.arg1();
    IExpr x0 = xSpec.arg2();
    IExpr orderExpr = xSpec.arg3();

    int order = orderExpr.toMachineInt();
    if (F.isNotPresent(order)) {
      return F.NIL;
    }

    // ==============================================================================
    // STRATEGY 1: ATTEMPT EXACT SOLUTION FIRST
    // If DSolve can solve the ODE exactly, computing the Series of the exact solution
    // avoids all algebraic approximation errors (crucial for distant boundary points).
    // ==============================================================================
    IAST dSolve = F.DSolve(ast.arg1(), yFunc, xVar);
    if (DEBUG) {
      System.out.println("dSolve: " + dSolve);
    }
    IExpr dsolveRes = engine.evaluate(dSolve);
    if (DEBUG) {
      System.out.println("dsolveRes: " + dsolveRes);
    }
    if (dsolveRes.isList() && ((IAST) dsolveRes).argSize() > 0 && !dsolveRes.isAST(S.DSolve)) {
      IAST firstSols = (IAST) ((IAST) dsolveRes).arg1();
      IExpr exactSol = F.NIL;
      for (int i = 1; i <= firstSols.argSize(); i++) {
        IExpr rule = firstSols.get(i);
        if (rule.isRule() && rule.first().equals(yFunc)) {
          exactSol = rule.second();
          break;
        }
      }
      if (exactSol.isPresent()) {
        // Simplify the exact solution to collapse identity artifacts (like Cos^2 + Sin^2 = 1)
        // before passing it to the Series expansion.
        exactSol = engine.evaluate(F.Simplify(exactSol));

        IExpr seriesRes = engine.evaluate(F.Series(exactSol, xSpec));

        if (seriesRes.isPresent()) {
          IExpr normalPoly = engine.evaluate(seriesRes.normal(false));
          // Collect constants to get canonical grouping: (1+2*x+2*x^2)*C(1)
          IExpr cPattern = F.C(F.$b());
          return engine.evaluate(F.Collect(normalPoly, cPattern));
        }
      }
    }

    // ==============================================================================
    // STRATEGY 2: ALGEBRAIC FALLBACK (Method of Undetermined Coefficients)
    // ==============================================================================

    // 2. Determine the maximum derivative order in the equations
    int max_d = 0;
    for (int i = 1; i <= 10; i++) {
      IExpr derivX = engine.evaluate(F.D(yFunc, F.List(xVar, F.ZZ(i))));
      for (int j = 1; j <= eqns.argSize(); j++) {
        if (!eqns.get(j).isFree(derivX)) {
          max_d = Math.max(max_d, i);
        }
      }
    }

    // 3. Build the series ansatz up to order + max_d to avoid truncation artifacts
    int ansatzDegree = order + max_d;
    // CPD-OFF ansatz at infinity and at a finite point
    IASTAppendable polyFull = F.PlusAlloc();
    IASTAppendable polyTrunc = F.PlusAlloc();
    IASTAppendable cVars = F.ListAlloc();

    for (int i = 0; i <= ansatzDegree; i++) {
      IExpr c_i = F.Dummy("c" + i);
      cVars.append(c_i);
      IExpr term = i == 0 ? c_i : F.Times(c_i, F.Power(F.Subtract(xVar, x0), F.ZZ(i)));
      // CPD-ON
      polyFull.append(term);
      if (i <= order) {
        polyTrunc.append(term);
      }
    }
    IExpr ansatzFull = engine.evaluate(polyFull);
    IExpr ansatzTrunc = engine.evaluate(polyTrunc);

    // 4. Prepare pure function substitution rules for y(x) and its derivatives
    // Replacing the HEADS natively triggers boundary conditions like y'(0) -> ansatz'(0)
    IASTAppendable rules = F.ListAlloc();
    rules.append(F.Rule(head, F.Function(F.List(xVar), ansatzFull)));

    for (int i = 1; i <= max_d; i++) {
      IExpr derivTargetX = engine.evaluate(F.D(yFunc, F.List(xVar, F.ZZ(i))));
      if (derivTargetX.isAST()) {
        IExpr derivHead = derivTargetX.head(); // Automatically extracts Derivative(i)[y]
        IExpr derivAnsatzX = engine.evaluate(F.D(ansatzFull, F.List(xVar, F.ZZ(i))));
        rules.append(F.Rule(derivHead, F.Function(F.List(xVar), derivAnsatzX)));
      }
    }

    // 5. Substitute into all equations to form the algebraic system
    IASTAppendable algebraicSystem = F.ListAlloc();

    for (int i = 1; i <= eqns.argSize(); i++) {
      // Substitute the polynomial series into the differential equation
      IExpr subbed = substituteEquation(eqns.get(i), rules, engine);

      if (subbed.isFree(xVar)) {
        algebraicSystem.append(F.Equal(subbed, F.C0));
      } else {
        IExpr normal = engine.evaluate(F.ExpandAll(subbed));
        for (int k = 0; k <= order; k++) {
          IExpr diff = k == 0 ? normal
              : engine.evaluate(F.Divide(F.D(normal, F.List(xVar, F.ZZ(k))), F.Factorial(F.ZZ(k))));
          IExpr coeff = engine.evaluate(F.subst(diff, xVar, x0));
          algebraicSystem.append(F.Equal(coeff, F.C0));
        }
      }
    }

    // 6. Solve the algebraic system for the sequence of coefficients
    IAST cVarsReversed = cVars.reverse(F.NIL);

    IAST solve = F.Solve(algebraicSystem, cVarsReversed);
    if (DEBUG) {
      System.out.println("solve: " + solve);
    }
    IExpr solList = engine.evaluate(solve);
    if (DEBUG) {
      System.out.println("solList: " + solList);
    }
    return seriesFromSolutions(solList, ansatzTrunc, cVars, engine);
  }

  /**
   * Substitute the <code>rules</code> into the equation <code>eq</code>, written as
   * <code>lhs - rhs</code> if it is an {@link S#Equal}.
   *
   * @param eq the equation or expression
   * @param rules the replacement rules
   * @param engine the evaluation engine
   * @return the evaluated substitution
   */
  static IExpr substituteEquation(IExpr eq, IAST rules, EvalEngine engine) {
    IExpr lhs = eq.isEqual() ? F.Subtract(eq.first(), eq.second()) : eq;
    IExpr subbed = engine.evaluate(F.subst(lhs, rules));
    // Re-evaluate to strictly execute pure functions spawned from boundary conditions
    return engine.evaluate(subbed);
  }

  /**
   * The asymptotic series of the first usable solution in <code>solList</code>: the result of
   * <code>Solve</code> for the coefficients <code>cVars</code> of the ansatz. A solution with a
   * <code>ConditionalExpression</code> comes from a transcendental root and is skipped. Coefficients
   * left free are renamed to <code>C(1), C(2), ...</code> and collected.
   *
   * @return {@link F#NIL} if there is no usable solution
   */
  static IExpr seriesFromSolutions(IExpr solList, IExpr ansatzTrunc, IAST cVars,
      EvalEngine engine) {
    if (!solList.isList() || ((IAST) solList).argSize() == 0) {
      return F.NIL;
    }
    // skip any branch that contains ConditionalExpression - those come from transcendental roots
    // of equations like -Sin(c0/z + ...) == 0 and are not valid polynomial asymptotic coefficients
    IAST firstSol = F.NIL;
    for (int bi = 1; bi <= ((IAST) solList).argSize(); bi++) {
      IAST candidate = (IAST) ((IAST) solList).get(bi);
      if (candidate.isFreeAST(S.ConditionalExpression)) {
        firstSol = candidate;
        break;
      }
    }
    if (firstSol.isNIL() || firstSol.argSize() == 0) {
      return F.NIL;
    }
    // Drop spurious coefficients beyond our requested `order` bounds
    IExpr finalSeries = canonicalConstants(ansatzTrunc, firstSol, cVars, engine);

    IExpr cPattern = F.C(F.$b());
    finalSeries = engine.evaluate(F.Collect(finalSeries, cPattern));
    return engine.evaluate(finalSeries);
  }

  /**
   * The ansatz with the coefficients of <code>solution</code> substituted. The coefficients of
   * <code>cVars</code> which are still free are renamed to the constants <code>C(n)</code>.
   */
  static IExpr canonicalConstants(IExpr ansatz, IAST solution, IAST cVars, EvalEngine engine) {
    IExpr series = engine.evaluate(F.subst(ansatz, solution));
    int cIndex = engine.incConstantCounter();
    IASTAppendable rules = F.ListAlloc();
    for (int i = 1; i <= cVars.argSize(); i++) {
      IExpr c = cVars.get(i);
      if (!series.isFree(c)) {
        rules.append(F.Rule(c, F.C(cIndex++)));
      }
    }
    if (rules.argSize() > 0) {
      series = engine.evaluate(F.subst(series, rules));
    }
    return series;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_3;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.Assumptions, S.GenerateConditions, S.GeneratedParameters}, //
        new IExpr[] {S.$Assumptions, S.Automatic, S.None});
  }
}
