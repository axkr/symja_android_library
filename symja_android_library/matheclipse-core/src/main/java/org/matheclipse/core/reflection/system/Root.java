package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class Root extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    // Root[{f, c}] — near-float approximation form. Represents a root of f[x]==0 near x = c
    // (used for transcendental equations whose roots cannot be expressed in closed algebraic form).
    if (ast.isAST1() && ast.arg1().isList2()) {
      IAST list = (IAST) ast.arg1();
      IExpr f = list.first();
      IExpr c = list.second();

      // Validate: f must be a pure Function (or otherwise applicable) and c must be numeric.
      if (!c.isNumber() && !c.isNumericFunction(true)) {
        return Errors.printMessage(S.Root, "rapp", F.List(c), engine);
      }

      // No exact solution. Refine c via FindRoot when either
      // (a) the evaluator is in numeric mode (e.g. inside N[...]), or
      // (b) c itself is an inexact number (machine- or arbitrary-precision float).
      //
      // For case (b) — auto-evaluation of Root[{f, c}] with an inexact c — verify the
      // input c is genuinely close to the refined root. Emits "Root::invrt"
      // and leaves the expression unevaluated when the deviation exceeds a precision-
      // dependent tolerance (e.g. Root[{2#-Tan[#]&, 4.60421}] is rejected because
      // |4.60421 - 4.6042167...| ≈ 7e-6 while Root[{...&, 4.604216}] is accepted).
      //
      // For case (a) — explicit N[Root[{f, c}]] — the user is asking for numeric
      // refinement, so skip the precision check and return whatever FindRoot converges
      // to regardless of how rough the initial seed was.
      if (engine.isNumericMode()) {
        IExpr refinedNumeric = refineNumerically(f, c, engine);
        if (refinedNumeric.isPresent() && refinedNumeric.isNumber() && c.isNumber()) {
          if (!engine.isNumericMode()) {
            try {
              double cVal = c.evalf();
              double rVal = refinedNumeric.evalf();
              // Tolerance: ~1e-5 relative. Matches behavior on the
              // 4.60421 vs 4.604216 boundary for machine-precision Num inputs.
              double tol = Config.SPECIAL_FUNCTIONS_TOLERANCE * Math.max(1.0, Math.abs(cVal));
              if (Math.abs(rVal - cVal) > tol) {
                // c is not close enough to any root of f → emit Root::invrt, stay inert.
                return Errors.printMessage(S.Root, "invrt", F.List(c, f), engine);
              }
            } catch (ArgumentTypeException atex) {
              // fall through – keep numeric refinement result
            }
          }
          return refinedNumeric;
        }
        // refineNumerically failed (e.g. FindRoot did not converge from this seed) →
        // treat as "not equal to a root" and stay inert (only emit message in the
        // auto-evaluation path; under N[...] we just leave the expression unevaluated).
        if (!engine.isNumericMode()) {
          Errors.printMessage(S.Root, "invrt", F.List(c, f), engine);
        }
        return F.NIL;
      }

      // Symbolic mode with an exact c: refine once via FindRoot so the inert
      // Root[{f, c}] carries a precise numerical seed. Avoid looping by only returning
      // a new expression when the refined value differs from c.
      IExpr refined = refineNumerically(f, c, engine);
      if (refined.isPresent() && refined.isNumber() && c.isNumber()) {
        try {
          double cVal = c.evalf();
          double rVal = refined.evalf();
          if (Math.abs(rVal - cVal) > Config.SPECIAL_FUNCTIONS_TOLERANCE
              * Math.max(1.0, Math.abs(cVal))) {
            return F.unaryAST1(S.Root, F.list(f, refined));
          }
        } catch (ArgumentTypeException atex) {
          // ignore – keep inert
        }
      }

      // Try exact algebraic resolution via Solve (should succeed for algebraic f).
      IExpr exact = ToRadicals.rootToRadicals(ast, engine);
      if (exact.isPresent()) {
        return exact;
      }

      // Stay inert: Root[{f, c}] is a valid symbolic placeholder for a transcendental root.
      return F.NIL;
    }
    // Root[f, k] or Root[f, k, n] (n in {0,1}): auto-expand only for polynomials of
    // degree <= 2 . For higher degrees the user must call ToRadicals[Root[f, k]] explicitly.
    //
    // The 3-argument form Root[f, k, 0] is canonical input form (real-root ordering);
    // Root[f, k, 1] selects an alternate complex ordering. Symja currently only
    // implements the real-first ordering, so both forms are treated equivalently to Root[f, k].
    if ((ast.isAST2() || ast.isAST3()) && ast.arg2().isInteger()) {
      if (ast.isAST3()) {
        IExpr arg3 = ast.arg3();
        if (!arg3.equals(F.C0) && !arg3.equals(F.C1)) {
          // Normalize any invalid third argument to 0 (behavior:
          // Root[f, k, 2] → Root[f, k, 0]). The returned expression will be
          // re-evaluated and handled by the n==0 branch below.
          return F.ternaryAST3(S.Root, ast.arg1(), ast.arg2(), F.C0);
        }
      }
      IExpr radical = ToRadicals.rootToRadicals(ast, engine, 2);
      if (radical.isPresent()) {
        return radical;
      }
    }
    // Root(f, k) stays unevaluated
    // Use ToRadicals(Root(f, k)) to expand to radical form.
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

  /**
   * Refine the numerical approximation {@code c} of a root of {@code f[x] == 0} by calling
   * {@code FindRoot[f[x] == 0, {x, c}]}.
   *
   * @param f a pure {@code Function} (or any expression applicable to a single argument)
   * @param c a numeric starting value for the root
   * @param engine the evaluation engine
   * @return the refined numeric root value, or {@link F#NIL} if {@code FindRoot} fails
   */
  private static IExpr refineNumerically(IExpr f, IExpr c, EvalEngine engine) {
    if (!c.isNumber() && !c.isNumericFunction(true)) {
      return F.NIL;
    }
    try {

      ISymbol x = F.Dummy("x");
      IASTMutable unaryAST1 = F.unaryAST1(f, x);
      if (!engine.isNumericMode()) {
        IExpr result = engine.evaluate(F.subst(unaryAST1, x, c));
        if (!result.isPossibleZero(true)) {
          return Errors.printMessage(S.Root, "invrt", F.List(c, f), engine);
        }
        return F.NIL;
      }
      IExpr eq = F.Equal(unaryAST1, F.C0);

      IExpr findRootResult = engine.evaluate(F.FindRoot(eq, F.list(x, c)));
      // FindRoot returns {x -> value}
      if (findRootResult.isList() && findRootResult.size() == 2) {
        IExpr rule = findRootResult.first();
        if (rule.isRuleAST()) {
          IExpr value = rule.second();
          if (value.isNumber()) {
            return value;
          }
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }
}
