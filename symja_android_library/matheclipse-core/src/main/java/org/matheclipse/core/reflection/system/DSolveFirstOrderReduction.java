package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * First order equations which a change of the variables makes one the cascade already solves.
 *
 * <p>
 * Two changes are tried. If the right hand side depends on <code>x</code> and <code>y</code> only
 * through one linear combination of them, then in that combination the equation has no
 * <code>x</code> left and is separable. Otherwise, if the right hand side is a ratio of two linear
 * expressions, moving the origin to where those two lines meet cancels the constant terms and
 * leaves an equation which is homogeneous of degree zero.
 */
final class DSolveFirstOrderReduction {

  private DSolveFirstOrderReduction() {}

  /** How big a right hand side is still worth changing the variables in. */
  private static final int MAX_LEAF_COUNT = 300;

  /**
   * The general solution of the equation, or {@link F#NIL} if neither change of variables applies.
   */
  static IExpr solve(IExpr lhs, IExpr yFunction, IExpr xVar, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr head = yFunction.head();
    IExpr dyx = engine.evaluate(F.D(yFunction, xVar));
    IExpr coefficient = engine.evaluate(F.Coefficient(lhs, dyx));
    if (coefficient.isZero() || !DSolveODE.isLinearInDerivative(lhs, dyx, engine)) {
      return F.NIL;
    }
    IExpr rest = engine.evaluate(F.Subtract(lhs, F.Times(coefficient, dyx)));
    IExpr yDummy = F.Dummy("Y");
    IExpr right = engine.evaluate(
        F.subst(F.Cancel(F.Together(F.Divide(F.Negate(rest), coefficient))), yFunction, yDummy));
    if (!right.isFree(head, true) || right.leafCount() > MAX_LEAF_COUNT || right.isFree(yDummy)) {
      return F.NIL;
    }

    IExpr shifted = alongOneLine(right, yFunction, xVar, yDummy, c_n, ctx);
    if (shifted.isPresent()) {
      return shifted;
    }
    return aboutTheIntersection(right, yFunction, xVar, yDummy, c_n, ctx);
  }

  /**
   * The right hand side seen through one linear combination <code>v == y + r*x</code>.
   *
   * <p>
   * It depends on that combination alone exactly when the ratio of its two partial derivatives is
   * the constant <code>r</code>, and then <code>v' == F(v) + r</code> has no <code>x</code> in it.
   * This covers <code>y' == F(a*x + b*y + c)</code> and the ratios of two linear expressions whose
   * lines are parallel.
   */
  private static IExpr alongOneLine(IExpr right, IExpr yFunction, IExpr xVar, IExpr yDummy,
      IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr byY = engine.evaluate(F.D(right, yDummy));
    if (byY.isZero()) {
      return F.NIL;
    }
    IExpr slope = engine.evaluate(F.Cancel(F.Together(F.Divide(F.D(right, xVar), byY))));
    if (!slope.isFree(xVar) || !slope.isFree(yDummy, true) || slope.isZero()) {
      return F.NIL;
    }

    IExpr combination = F.Dummy("v");
    IExpr autonomous = engine.evaluate(F.Simplify(
        F.subst(right, yDummy, F.Subtract(combination, F.Times(slope, xVar)))));
    if (!autonomous.isFree(xVar)) {
      return F.NIL;
    }

    // v' == F(v) + r, which has no x in it and is therefore separable.
    IExpr vFunction = F.unaryAST1(F.Dummy("vf"), xVar);
    IExpr equation = F.Equal(F.Subtract(engine.evaluate(F.D(vFunction, xVar)),
        F.Plus(F.subst(autonomous, combination, vFunction), slope)), F.C0);
    IAST branches = DSolveODE.solveSubODE(equation, xVar, vFunction, c_n, ctx);

    IASTAppendable results = F.ListAlloc(branches.argSize());
    for (int i = 1; i <= branches.argSize(); i++) {
      IExpr body = engine.evaluate(
          F.Subtract(branches.get(i), F.Times(slope, xVar)));
      if (body.isPresent() && body.isFree(combination, true)) {
        results.append(body);
      }
    }
    return results.argSize() == 0 ? F.NIL
        : results.argSize() == 1 ? results.arg1() : results;
  }

  /**
   * A ratio of two linear expressions, seen from where the two lines meet.
   *
   * <p>
   * Moving the origin there cancels both constant terms, and what is left is homogeneous of degree
   * zero, which the substitution <code>y == v*x</code> separates.
   */
  private static IExpr aboutTheIntersection(IExpr right, IExpr yFunction, IExpr xVar,
      IExpr yDummy, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr together = engine.evaluate(F.Together(right));
    IExpr[] upper = linearParts(engine.evaluate(F.Numerator(together)), xVar, yDummy, engine);
    IExpr[] lower = linearParts(engine.evaluate(F.Denominator(together)), xVar, yDummy, engine);
    if (upper == null || lower == null) {
      return F.NIL;
    }
    if (upper[2].isZero() && lower[2].isZero()) {
      // Already homogeneous, so the solver for those has had its chance.
      return F.NIL;
    }
    IExpr determinant = engine.evaluate(
        F.Subtract(F.Times(upper[0], lower[1]), F.Times(lower[0], upper[1])));
    if (determinant.isZero()) {
      // Parallel lines; that is the other method's case.
      return F.NIL;
    }
    IExpr atX = engine.evaluate(F.Divide(
        F.Subtract(F.Times(upper[1], lower[2]), F.Times(lower[1], upper[2])), determinant));
    IExpr atY = engine.evaluate(F.Divide(
        F.Subtract(F.Times(lower[0], upper[2]), F.Times(upper[0], lower[2])), determinant));
    if (!atX.isFree(xVar) || !atX.isFree(yDummy, true) || !atY.isFree(xVar)
        || !atY.isFree(yDummy, true)) {
      return F.NIL;
    }

    IExpr shiftedVar = F.Dummy("X");
    IExpr shiftedFunction = F.unaryAST1(F.Dummy("Yh"), shiftedVar);
    IExpr moved = engine.evaluate(F.Simplify(F.subst(
        F.subst(right, yDummy, F.Plus(shiftedFunction, atY)),
        xVar, F.Plus(shiftedVar, atX))));
    if (!moved.isFree(xVar) || !moved.isFree(yDummy, true)) {
      return F.NIL;
    }
    IExpr equation = F.Equal(
        F.Subtract(engine.evaluate(F.D(shiftedFunction, shiftedVar)), moved), F.C0);
    IAST branches = DSolveODE.solveSubODE(equation, shiftedVar, shiftedFunction, c_n, ctx);

    IASTAppendable results = F.ListAlloc(branches.argSize());
    for (int i = 1; i <= branches.argSize(); i++) {
      IExpr body = engine.evaluate(F.Plus(
          F.subst(branches.get(i), shiftedVar, F.Subtract(xVar, atX)), atY));
      if (body.isPresent() && body.isFree(shiftedVar, true)) {
        results.append(body);
      }
    }
    return results.argSize() == 0 ? F.NIL
        : results.argSize() == 1 ? results.arg1() : results;
  }

  /**
   * The coefficients <code>{a, b, c}</code> of <code>a*x + b*y + c</code>, or <code>null</code> if
   * the expression is not of that shape.
   */
  private static IExpr[] linearParts(IExpr expr, IExpr xVar, IExpr yDummy, EvalEngine engine) {
    IExpr expanded = engine.evaluate(F.ExpandAll(expr));
    IExpr a = engine.evaluate(F.Coefficient(expanded, xVar));
    IExpr b = engine.evaluate(F.Coefficient(expanded, yDummy));
    IExpr c = engine.evaluate(F.subst(F.subst(expanded, xVar, F.C0), yDummy, F.C0));
    IExpr rest = engine.evaluate(F.ExpandAll(F.Subtract(expanded,
        F.Plus(F.Times(a, xVar), F.Times(b, yDummy), c))));
    if (!DSolveODE.isVanishing(rest, engine) || !a.isFree(xVar) || !a.isFree(yDummy, true)
        || !b.isFree(xVar) || !b.isFree(yDummy, true) || !c.isFree(xVar)
        || !c.isFree(yDummy, true)) {
      return null;
    }
    return new IExpr[] {a, b, c};
  }
}
