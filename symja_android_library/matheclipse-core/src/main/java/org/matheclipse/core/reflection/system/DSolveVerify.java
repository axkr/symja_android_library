package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

/**
 * Back substitution of a candidate solution into the equation it claims to solve.
 *
 * <p>
 * Every method of {@link DSolve} recognizes its equations by a structural test, and a test which is
 * not tight enough answers confidently with something wrong rather than declining. Putting the
 * candidate back into the equation catches that whole class of defect once, instead of one instance
 * at a time.
 *
 * <p>
 * The test is deliberately one sided. A candidate is rejected only when the residual can be
 * <b>proved</b> to be nonzero, by evaluating it numerically at several points; everything a
 * numeric evaluation cannot decide is accepted. Symja routinely returns correct antiderivatives it
 * cannot simplify to zero afterwards, so a two sided test would throw away correct answers.
 * {@link org.matheclipse.core.expression.S#PossibleZeroQ} is not used for the same reason: it
 * answers <code>False</code> for an expression it merely fails to decide.
 */
final class DSolveVerify {

  private DSolveVerify() {}

  /** The points the residual is sampled at. Positive, to stay off the usual branch cuts. */
  private static final int[][] SAMPLES =
      new int[][] {{3, 10}, {7, 10}, {13, 10}, {23, 10}};

  /** Relative size a sample has to exceed before it counts as proof of a nonzero residual. */
  private static final double TOLERANCE = 1.0e-6;

  /**
   * Relative size at which a sample counts against a candidate under the strict policy. The band
   * between this and {@link #TOLERANCE} is neither for nor against: a solution written with
   * radicals or logarithms loses accuracy near a branch cut without being wrong.
   */
  private static final double STRICT_TOLERANCE = 1.0e-3;

  /** How many samples have to come out zero before a strict candidate is believed. */
  private static final int STRICT_REQUIRED_ZEROS = 2;

  /**
   * Whether <code>body</code> may be returned as a solution of the differential equations
   * <code>residuals</code> (each of which is meant to be zero) for <code>yFunction</code>.
   *
   * @param residuals the equations in the form which is equal to zero
   * @param yFunction the applied unknown function, e.g. <code>y(x)</code>
   * @param xVar the independent variable
   * @param body the candidate solution, an expression in <code>xVar</code>
   */
  static boolean acceptODE(IAST residuals, IExpr yFunction, IExpr xVar, IExpr body,
      EvalEngine engine) {
    return accept(residuals, F.list(yFunction), F.list(xVar), F.list(body), engine, false);
  }

  /**
   * Whether <code>body</code> is shown to solve <code>residuals</code>, rather than merely not
   * shown to fail.
   *
   * <p>
   * This is the reverse of the policy above, for the methods which search rather than recognize. A
   * search which inverts a relation produces several branches of which only one is the solution,
   * and their residuals are the kind of expression full of logarithms and radicals that nothing
   * here can simplify to zero. Keeping what cannot be disproved would return one of the wrong
   * branches, so a candidate has to be seen to vanish numerically before it is believed.
   */
  static boolean acceptODEStrict(IAST residuals, IExpr yFunction, IExpr xVar, IExpr body,
      EvalEngine engine) {
    return accept(residuals, F.list(yFunction), F.list(xVar), F.list(body), engine, true);
  }

  /**
   * Whether the solutions <code>bodies</code> may be returned for the unknowns
   * <code>yFunctions</code> of the system <code>residuals</code>.
   */
  static boolean acceptSystem(IAST residuals, IAST yFunctions, IExpr xVar, IAST bodies,
      EvalEngine engine) {
    return accept(residuals, yFunctions, F.list(xVar), bodies, engine, false);
  }

  /**
   * Whether the solutions <code>bodies</code> are shown to solve the system, in the sense of
   * {@link #acceptODEStrict}.
   */
  static boolean acceptSystemStrict(IAST residuals, IAST yFunctions, IExpr xVar, IAST bodies,
      EvalEngine engine) {
    return accept(residuals, yFunctions, F.list(xVar), bodies, engine, true);
  }

  /**
   * Whether <code>body</code> may be returned as a solution of the partial differential equation
   * <code>residual</code> for <code>uFunction</code>.
   *
   * @param xVars the independent variables, e.g. <code>{x, y}</code>
   */
  static boolean acceptPDE(IAST residuals, IExpr uFunction, IAST xVars, IExpr body,
      EvalEngine engine) {
    return accept(residuals, F.list(uFunction), xVars, F.list(body), engine, false);
  }

  private static boolean accept(IAST residuals, IAST yFunctions, IAST xVars, IAST bodies,
      EvalEngine engine, boolean strict) {
    if (residuals.argSize() == 0 || yFunctions.argSize() != bodies.argSize()) {
      return !strict;
    }
    for (int i = 1; i <= bodies.argSize(); i++) {
      if (isUndecidable(bodies.get(i))) {
        return !strict;
      }
    }
    boolean quietMode = engine.isQuietMode();
    try {
      engine.setQuietMode(true);
      // An arbitrary function C(k)(...) cannot be differentiated or evaluated as it stands, so
      // each one is pinned to a concrete function first. They are pinned to *different* functions
      // so that a mistake in one branch of the solution cannot cancel against another.
      IAST testFunctions = F.List(S.Sin, S.Cos, S.Exp, S.Sqrt);
      IASTAppendable pinned = F.ListAlloc(bodies.argSize());
      for (int i = 1; i <= bodies.argSize(); i++) {
        pinned.append(pinArbitraryFunctions(bodies.get(i), testFunctions, engine));
      }
      for (int i = 1; i <= residuals.argSize(); i++) {
        IExpr residual = substituteSolution(residuals.get(i), yFunctions, xVars, pinned, engine);
        if (residual.isNIL()) {
          return !strict;
        }
        if (strict ? !isNumericallyZero(residual, engine) : isDecidablyNonzero(residual, engine)) {
          return false;
        }
      }
      return true;
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
      return !strict;
    } finally {
      engine.setQuietMode(quietMode);
    }
  }

  /**
   * Whether the expression contains something a numeric back substitution cannot say anything
   * about, in which case the candidate is accepted without a test.
   */
  private static boolean isUndecidable(IExpr expr) {
    return expr.isNIL() || !expr.isFree(x -> x.isAST(S.Integrate) || x.isAST(S.Inactive)
        || x.isAST(S.Root) || x.isAST(S.Solve) || x.isAST(S.InverseFunction)
        || x.isAST(S.DSolve) || x.isAST(S.Derivative), true);
  }

  /** Replaces every arbitrary function <code>C(k)(arg)</code> by a concrete test function. */
  private static IExpr pinArbitraryFunctions(IExpr expr, IAST testFunctions, EvalEngine engine) {
    if (!expr.isAST()) {
      return expr;
    }
    IAST ast = (IAST) expr;
    if (ast.isAST1() && ast.head().isAST(S.C, 2) && ast.head().first().isInteger()) {
      int index = ast.head().first().toIntDefault();
      if (index > 0) {
        IExpr testFunction = testFunctions.get((index - 1) % testFunctions.argSize() + 1);
        IExpr argument = pinArbitraryFunctions(ast.arg1(), testFunctions, engine);
        return engine.evaluate(F.unaryAST1(testFunction, argument));
      }
    }
    IASTAppendable result = F.ast(pinArbitraryFunctions(ast.head(), testFunctions, engine),
        ast.argSize());
    for (int i = 1; i <= ast.argSize(); i++) {
      result.append(pinArbitraryFunctions(ast.get(i), testFunctions, engine));
    }
    return engine.evaluate(result);
  }

  /**
   * Substitutes the solutions into one equation, replacing the derivatives from the highest order
   * downwards so that a lower order replacement cannot destroy a higher order one.
   */
  private static IExpr substituteSolution(IExpr residual, IAST yFunctions, IAST xVars, IAST bodies,
      EvalEngine engine) {
    IExpr result = residual;
    for (int i = 1; i <= yFunctions.argSize(); i++) {
      IExpr yFunction = yFunctions.get(i);
      IExpr body = bodies.get(i);
      IExpr head = yFunction.head();
      if (xVars.argSize() == 1) {
        IExpr xVar = xVars.arg1();
        int order = LinearODEForm.highestDerivativeOrder(result, head, xVar);
        for (int k = order; k >= 1; k--) {
          IExpr derivative = engine.evaluate(F.D(yFunction, F.List(xVar, F.ZZ(k))));
          result = F.subst(result, derivative, engine.evaluate(F.D(body, F.List(xVar, F.ZZ(k)))));
        }
      } else {
        // A partial derivative Derivative(i,j)(u)(x,y) is replaced by D(body,{x,i},{y,j}).
        for (int total = 4; total >= 1; total--) {
          for (int i1 = 0; i1 <= total; i1++) {
            int i2 = total - i1;
            IExpr derivative = engine.evaluate(F.D(yFunction, F.List(xVars.arg1(), F.ZZ(i1)),
                F.List(xVars.arg2(), F.ZZ(i2))));
            if (derivative.isFree(head, true)) {
              continue;
            }
            if (!result.isFree(derivative, true)) {
              result = F.subst(result, derivative, engine.evaluate(F.D(body,
                  F.List(xVars.arg1(), F.ZZ(i1)), F.List(xVars.arg2(), F.ZZ(i2)))));
            }
          }
        }
      }
      result = F.subst(result, yFunction, body);
      if (!result.isFree(head, true)) {
        // Something referring to the unknown is left, so nothing can be concluded.
        return F.NIL;
      }
    }
    return engine.evaluate(result);
  }

  /**
   * Whether the residual can be proved to be nonzero by evaluating it numerically. Only a residual
   * which stays clearly away from zero at <b>every</b> sample point counts as proof; anything else,
   * including an expression which does not evaluate to a number at all, leaves the question open
   * and the candidate is kept.
   */
  private static boolean isDecidablyNonzero(IExpr residual, EvalEngine engine) {
    if (residual.isZero()) {
      return false;
    }
    double[] magnitudes = relativeMagnitudes(residual, engine);
    for (double magnitude : magnitudes) {
      if (Double.isNaN(magnitude) || magnitude <= TOLERANCE) {
        return false;
      }
    }
    return true;
  }

  /**
   * Whether the residual is seen to vanish: several sample points come out zero and none of them
   * clearly does not. A point the evaluation cannot reach at all is passed over rather than held
   * against the candidate, because a solution is entitled to a pole.
   */
  private static boolean isNumericallyZero(IExpr residual, EvalEngine engine) {
    if (residual.isZero()) {
      return true;
    }
    double[] magnitudes = relativeMagnitudes(residual, engine);
    int zeros = 0;
    for (double magnitude : magnitudes) {
      if (Double.isNaN(magnitude)) {
        continue;
      }
      if (magnitude > STRICT_TOLERANCE) {
        return false;
      }
      if (magnitude <= TOLERANCE) {
        zeros++;
      }
    }
    return zeros >= STRICT_REQUIRED_ZEROS;
  }

  /**
   * The size of the residual at each sample point, measured against the size of the terms it is
   * made of, or {@link Double#NaN} where it does not evaluate to a finite number.
   */
  private static double[] relativeMagnitudes(IExpr residual, EvalEngine engine) {
    IASTAppendable symbols = F.ListAlloc();
    collectFreeSymbols(residual, symbols);
    double[] magnitudes = new double[SAMPLES.length];
    for (int sample = 0; sample < SAMPLES.length; sample++) {
      IASTAppendable rules = F.ListAlloc(symbols.argSize());
      for (int i = 1; i <= symbols.argSize(); i++) {
        int[] fraction = SAMPLES[(sample + i) % SAMPLES.length];
        rules.append(F.Rule(symbols.get(i), F.QQ(fraction[0] + i, fraction[1])));
      }
      magnitudes[sample] = Double.NaN;
      IExpr value;
      try {
        value = engine.evalN(F.subst(residual, rules));
      } catch (RuntimeException rex) {
        org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
        continue;
      }
      if (!(value instanceof INumber)) {
        continue;
      }
      double abs = ((INumber) value).abs().evalf();
      if (!Double.isFinite(abs)) {
        continue;
      }
      // The scale only matters for a residual which is not already small: dividing by it can only
      // make the value smaller, so anything within the tolerance stays within it. Working the scale
      // out costs an evaluation of every term of the residual, which is expensive for a solution
      // written with special functions, and for a correct solution it never changes the answer.
      magnitudes[sample] = abs <= TOLERANCE //
          ? abs
          : abs / (1.0 + scaleOf(residual, rules, engine));
    }
    return magnitudes;
  }

  /** The summed magnitude of the top level terms, so that the tolerance is a relative one. */
  private static double scaleOf(IExpr residual, IAST rules, EvalEngine engine) {
    double scale = 0.0;
    IAST terms = residual.isPlus() ? (IAST) residual : F.Plus(residual);
    for (int i = 1; i <= terms.argSize(); i++) {
      try {
        IExpr value = engine.evalN(F.subst(terms.get(i), rules));
        if (value instanceof INumber) {
          double abs = ((INumber) value).abs().evalf();
          if (Double.isFinite(abs)) {
            scale += abs;
          }
        }
      } catch (RuntimeException rex) {
        org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
      }
    }
    return scale;
  }

  private static void collectFreeSymbols(IExpr expr, IASTAppendable symbols) {
    if (expr.isSymbol()) {
      if (!expr.isBuiltInSymbol() && !symbols.contains(expr)) {
        symbols.append(expr);
      }
      return;
    }
    if (expr.isAST(S.C, 2)) {
      if (!symbols.contains(expr)) {
        symbols.append(expr);
      }
      return;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      for (int i = 0; i < ast.size(); i++) {
        collectFreeSymbols(ast.get(i), symbols);
      }
    }
  }
}
