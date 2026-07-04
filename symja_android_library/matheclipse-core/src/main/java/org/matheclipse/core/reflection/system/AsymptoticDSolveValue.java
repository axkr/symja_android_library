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

    if (!orderExpr.isInteger()) {
      return F.NIL;
    }
    int order = orderExpr.toIntDefault();

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
          IExpr cPattern = F.unaryAST1(S.C, F.$b());
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
    IASTAppendable polyFull = F.PlusAlloc();
    IASTAppendable polyTrunc = F.PlusAlloc();
    IASTAppendable cVars = F.ListAlloc();

    for (int i = 0; i <= ansatzDegree; i++) {
      IExpr c_i = F.Dummy("c" + i);
      cVars.append(c_i);
      IExpr term = i == 0 ? c_i : F.Times(c_i, F.Power(F.Subtract(xVar, x0), F.ZZ(i)));
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
      IExpr eq = eqns.get(i);
      IExpr lhs = eq.isEqual() ? F.Subtract(eq.first(), eq.second()) : eq;

      // Substitute the polynomial series into the differential equation
      IExpr subbed = engine.evaluate(F.subst(lhs, rules));
      // Re-evaluate to strictly execute pure functions spawned from boundary conditions
      subbed = engine.evaluate(subbed);

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
    IASTAppendable cVarsReversed = F.ListAlloc(cVars.argSize());
    for (int i = cVars.argSize(); i >= 1; i--) {
      cVarsReversed.append(cVars.get(i));
    }

    IAST solve = F.Solve(algebraicSystem, cVarsReversed);
    if (DEBUG) {
      System.out.println("solve: " + solve);
    }
    IExpr solList = engine.evaluate(solve);
    if (DEBUG) {
      System.out.println("solList: " + solList);
    }
    if (solList.isList() && ((IAST) solList).argSize() > 0) {
      IAST firstSol = (IAST) ((IAST) solList).arg1();

      IExpr finalSeries = engine.evaluate(F.subst(ansatzTrunc, firstSol));

      int cIndex = engine.incConstantCounter();
      IASTAppendable finalRules = F.ListAlloc();
      for (int i = 1; i <= cVars.argSize(); i++) {
        IExpr c = cVars.get(i);
        if (!finalSeries.isFree(c)) {
          finalRules.append(F.Rule(c, F.C(cIndex++)));
        }
      }
      if (finalRules.argSize() > 0) {
        finalSeries = engine.evaluate(F.subst(finalSeries, finalRules));
      }

      IExpr cPattern = F.unaryAST1(S.C, F.$b());
      finalSeries = engine.evaluate(F.Collect(finalSeries, cPattern));

      return engine.evaluate(finalSeries);
    }

    return F.NIL;
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
