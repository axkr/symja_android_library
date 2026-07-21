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
 * AsymptoticRSolveValue(eqn, f, x -&gt; x0)
 * </pre>
 *
 * <blockquote>
 * <p>
 * Computes an asymptotic approximation to the difference equation <code>eqn</code> for
 * <code>f[x]</code> near <code>x0</code> (commonly Infinity).
 * </p>
 * </blockquote>
 */
public class AsymptoticRSolveValue extends AbstractFunctionOptionEvaluator {
  private final static boolean DEBUG = false;

  /** Budget (seconds) for one bounded Series attempt; $Aborted counts as "cannot solve". */
  private final static long SERIES_BUDGET_SECONDS = 10;

  public AsymptoticRSolveValue() {}

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {

    // 1. Parse arguments and series specification
    IAST eqns = ast.arg1().makeList();
    IExpr yFunc = ast.arg2();

    // Check if the target is a valid single function application (e.g. a(n))
    if (!yFunc.isAST1()) {
      return F.NIL;
    }

    IExpr targetSymbol = yFunc.head();
    IExpr spec = ast.arg3();

    IExpr xVar = F.NIL;
    IExpr x0 = F.NIL;
    int order = 3; // Default SeriesTermGoal

    // Parse the domain specification: x -> x0 or {x, x0, order}
    if (spec.isRule()) {
      xVar = spec.first();
      x0 = spec.second();
    } else if (spec.isList() && ((IAST) spec).argSize() >= 2) {
      IAST listSpec = (IAST) spec;
      xVar = listSpec.arg1();
      x0 = listSpec.arg2();
      if (listSpec.argSize() >= 3 && listSpec.arg3().isInteger()) {
        order = listSpec.arg3().toIntDefault();
      }
    } else {
      // Support implicit Infinity if only the variable is provided
      if (spec.isSymbol()) {
        xVar = spec;
        x0 = S.Infinity;
      } else {
        return F.NIL;
      }
    }

    // Save the Constant Counter state before any internal attempts
    int savedConstantCounter = engine.getConstantCounter();

    // ==============================================================================
    // STRATEGY 1: ATTEMPT EXACT SOLUTION FIRST
    // If RSolve can solve the recurrence exactly, computing the Series of the
    // exact solution natively handles fractional powers and complex scales.
    // ==============================================================================
    IExpr rSolve = F.RSolve(ast.arg1(), yFunc, xVar);
    if (DEBUG) {
      System.out.println("rSolve: " + rSolve);
    }

    IExpr rsolveRes = engine.evaluate(rSolve);
    if (DEBUG) {
      System.out.println("rsolveRes: " + rsolveRes);
    }

    if (rsolveRes.isList() && ((IAST) rsolveRes).argSize() > 0 && !rsolveRes.isAST(S.RSolve)) {
      IAST firstSols = (IAST) ((IAST) rsolveRes).arg1();
      IExpr exactSol = F.NIL;

      for (int i = 1; i <= firstSols.argSize(); i++) {
        IExpr rule = firstSols.get(i);

        // Handle rule format: a(n) -> solution
        if (rule.isRule() && rule.first().equals(yFunc)) {
          exactSol = rule.second();
          break;
        }
        // Handle pure function format: a -> Function({n}, solution)
        else if (rule.isRule() && rule.first().equals(targetSymbol)) {
          IExpr rhs = rule.second();
          if (rhs.isAST(S.Function, 2)) {
            exactSol = engine.evaluate(F.subst(rhs.second(), ((IAST) rhs.first()).arg1(), xVar));
          } else {
            exactSol = engine.evaluate(F.unaryAST1(rhs, xVar));
          }
          break;
        }
      }

      if (exactSol.isPresent() && exactSol.isFreeAST(S.ConditionalExpression)) {
        // Simplify the exact solution prior to Series expansion
        exactSol = engine.evaluate(F.Simplify(exactSol));

        IAST seriesSpec = F.List(xVar, x0, F.ZZ(order));
        // Time-boxed: Series of special sequences (Fibonacci, LucasL) or exponential growth
        // at Infinity can grind through the series/limit machinery without terminating; a
        // bounded attempt that ends in $Aborted is treated like a failed Series below.
        // evalTimeConstrained sets the engine's deadline without restoring it, so save it.
        final long savedSeconds = engine.getSeconds();
        IExpr seriesRes;
        try {
          seriesRes =
              engine.evalTimeConstrained(F.Series(exactSol, seriesSpec), SERIES_BUDGET_SECONDS);
        } finally {
          engine.setSeconds(savedSeconds);
        }

        // Use a robust check: If Series evaluates successfully, it drops the S.Series head
        if (seriesRes.isPresent() && !seriesRes.equals(S.$Aborted)
            && !seriesRes.isAST(S.Series)) {
          IExpr normalPoly = engine.evaluate(seriesRes.normal(false));

          // Rejects "exact" solutions that are effectively unresolved summations or products, or
          // that diverged to ComplexInfinity/Indeterminate (e.g. a telescoping Sum evaluated at a
          // pole of the lower bound). Such results fall through to the algebraic fallback below.
          if (normalPoly.isFreeAST(S.Sum)//
              && normalPoly.isFreeAST(S.Product) //
              && normalPoly.isSpecialsFree()) {
            // Collect arbitrary constants to match canonical grouping: (1+2/n+...)*C(1)
            IExpr cPattern = F.unaryAST1(S.C, F.$b());
            return S.Collect.funEval(engine, normalPoly, cPattern);
          }
        } else if (exactSol.isFreeAST(S.Sum) && exactSol.isFreeAST(S.Product)
            && exactSol.isSpecialsFree()) {
          // Series of the exact solution failed - special sequences (Fibonacci, LucasL) and
          // exponentially growing solutions have no asymptotic power series. The exact
          // solution itself is the sharpest asymptotic answer, so return it directly.
          IExpr cPattern = F.unaryAST1(S.C, F.$b());
          return S.Collect.funEval(engine, exactSol, cPattern);
        }
      }
    }

    // Exact solution failed or was rejected (unresolved sum/product).
    // Restore constant counter so the fallback properly starts at C(1)
    engine.setConstantCounter(savedConstantCounter);

    // ==============================================================================
    // STRATEGY 2: ALGEBRAIC FALLBACK (Method of Undetermined Coefficients)
    // ==============================================================================
    // Evaluates rational/polynomial asymptotic limits structurally.

    if (x0.isInfinity()) {
      // Over-parameterize the ansatz.
      // Difference equations inherently reduce the degree of leading terms.
      int ansatzDegree = order + 2;
      int seriesDegree = ansatzDegree + 1;

      IASTAppendable polyAnsatz = F.PlusAlloc();
      IASTAppendable polyTrunc = F.PlusAlloc();
      IASTAppendable cVars = F.ListAlloc();

      for (int i = 0; i <= ansatzDegree; i++) {
        IExpr c_i = F.Dummy("c" + i);
        cVars.append(c_i);
        IExpr term = i == 0 ? c_i : F.Times(c_i, F.Power(xVar, F.ZZ(-i)));
        polyAnsatz.append(term);
        if (i <= order) {
          polyTrunc.append(term);
        }
      }
      IExpr ansatzExpr = engine.evaluate(polyAnsatz);
      IExpr ansatzTrunc = engine.evaluate(polyTrunc);

      IASTAppendable algebraicSystem = F.ListAlloc();
      IExpr z = F.Dummy("z");

      IASTAppendable rules = F.ListAlloc();
      rules.append(F.Rule(targetSymbol, F.Function(F.List(xVar), ansatzExpr)));

      for (int i = 1; i <= eqns.argSize(); i++) {
        IExpr eq = eqns.get(i);
        IExpr lhs = eq.isEqual() ? F.Subtract(eq.first(), eq.second()) : eq;

        // Substitute the parameterized polynomial scale into the recurrence
        IExpr subbed = engine.evaluate(F.subst(lhs, rules));
        subbed = engine.evaluate(subbed);

        // NATIVE Infinity Expansion:
        // Avoid mapping to 1/z early to prevent 1/infinity denominator collapse bugs.
        // The Series attempt MUST be time-boxed: for non-linear recurrences like
        // a(n-1)+Sin(n*a(n-2)) the substituted ansatz produces Sin(n*polynomial) whose
        // asymptotic series does not exist, and the series/limit machinery grinds for
        // minutes exploring it instead of failing. A bounded attempt that ends in $Aborted
        // is treated exactly like an unevaluated Series: give up and return F.NIL.
        // evalTimeConstrained sets the engine's deadline without restoring it, so save it.
        final long oldSeconds = engine.getSeconds();
        IExpr seriesN;
        try {
          seriesN = engine.evalTimeConstrained(
              F.Series(subbed, F.List(xVar, S.Infinity, F.ZZ(seriesDegree))),
              SERIES_BUDGET_SECONDS);

          if (seriesN.isAST(S.Series)) {
            // If Series initially fails, attempt a simplification first
            IExpr simplified =
                engine.evalTimeConstrained(F.Simplify(subbed), SERIES_BUDGET_SECONDS);
            if (simplified.equals(S.$Aborted) || simplified.isNIL()) {
              return F.NIL;
            }
            subbed = simplified;
            seriesN = engine.evalTimeConstrained(
                F.Series(subbed, F.List(xVar, S.Infinity, F.ZZ(seriesDegree))),
                SERIES_BUDGET_SECONDS);
          }
        } finally {
          engine.setSeconds(oldSeconds);
        }

        // Catch unevaluated/failed/aborted Series expansions involving non-polynomial
        // limits (e.g. Sin(n*C))
        if (seriesN.isNIL() || seriesN.equals(S.$Aborted) || seriesN.isAST(S.Series)) {
          return F.NIL;
        }

        IExpr normalN = engine.evaluate(seriesN.normal(false));

        // Map xVar^-k -> z^k purely for algebraic coefficient extraction
        IExpr normalZ = engine.evaluate(F.subst(normalN, xVar, F.Power(z, F.CN1)));
        normalZ = engine.evaluate(F.PowerExpand(normalZ));

        IExpr collectedZ = engine.evaluate(F.Collect(normalZ, z));

        // Extract constraints across independent magnitudes, including negative powers
        for (int k = -seriesDegree; k <= seriesDegree; k++) {
          IExpr coeff = engine.evaluate(F.Coefficient(collectedZ, z, F.ZZ(k)));
          if (!coeff.isZero()) {
            algebraicSystem.append(F.Equal(coeff, F.C0));
          }
        }
      }

      IASTAppendable cVarsReversed = F.ListAlloc(cVars.argSize());
      for (int i = cVars.argSize(); i >= 1; i--) {
        cVarsReversed.append(cVars.get(i));
      }

      IAST solve = F.Solve(algebraicSystem, cVarsReversed);
      IExpr solList = engine.evaluate(solve);

      if (solList.isList() && ((IAST) solList).argSize() > 0) {

        // Iterate branches; skip any that contain ConditionalExpression — those come
        // from transcendental roots of equations like -Sin(c0/z + ...) == 0 and are
        // not valid polynomial asymptotic coefficients.
        IAST firstSol = F.NIL;
        for (int bi = 1; bi <= ((IAST) solList).argSize(); bi++) {
          IAST candidate = (IAST) ((IAST) solList).get(bi);
          if (candidate.isFreeAST(S.ConditionalExpression)) {
            firstSol = candidate;
            break;
          }
        }

        if (firstSol.isPresent() && firstSol.argSize() > 0) {
          // Drop spurious coefficients beyond our requested `order` bounds
          IExpr finalSeries = engine.evaluate(F.subst(ansatzTrunc, firstSol));

          // Assign unbound generic variables to unified Canonical form: C(1), C(2)
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
      }
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_4;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    super.setUp(newSymbol);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.Assumptions, S.GenerateConditions, S.GeneratedParameters,
            S.SeriesTermGoal}, //
        new IExpr[] {S.$Assumptions, S.Automatic, S.None, F.C3});
  }
}
