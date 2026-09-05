package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Linear equations of the second order whose coefficients become rational in another variable.
 *
 * <p>
 * Substituting <code>t == phi(x)</code> in <code>y'' + P*y' + Q*y == 0</code> gives
 * <code>Y'' + A*Y' + B*Y == 0</code> with <code>A == (phi'' + P*phi')/phi'^2</code> and
 * <code>B == Q/phi'^2</code>, both written in <code>t</code>. For the right <code>phi</code> what
 * comes out is rational even though the equation started with a cotangent in it, and the methods
 * which read the equations of the named functions off a rational normal form can then have it:
 * <code>y'' + Cot(x)*y' + k*(k+1)*y == 0</code> becomes Legendre's equation under
 * <code>t == Cos(x)</code>.
 *
 * <p>
 * Only equations whose coefficients are not rational already are tried, which is what keeps this
 * from being asked about every equation and from entering itself.
 */
final class DSolveChangeOfVariable {

  private DSolveChangeOfVariable() {}

  /** The substitutions, with their inverses. */
  private static final ISymbol[] PHI = {S.Cos, S.Sin, S.Tan};

  private static final ISymbol[] INVERSE = {S.ArcCos, S.ArcSin, S.ArcTan};

  /** The functions whose presence means the coefficients are not rational. */
  private static final int[] CIRCULAR = {ID.Sin, ID.Cos, ID.Tan, ID.Cot, ID.Sec, ID.Csc};

  /** How deep in the cascade this is still attempted. */
  private static final int MAX_DEPTH = 2;

  private static final int MAX_LEAF_COUNT = 200;

  /** How long one of the evaluations inside may take. */
  private static final int STEP_SECONDS = 3;

  /**
   * The general solution of the equation, or {@link F#NIL} if no substitution of this kind makes
   * its coefficients rational.
   */
  static IExpr solve(LinearODEForm lf, IExpr yFunction, IExpr xVar, IExpr c_n,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (lf.order != 2 || !lf.g.isZero() || lf.a[2].isZero() || ctx.depth() > MAX_DEPTH) {
      return F.NIL;
    }
    IExpr p = engine.evaluate(F.Cancel(F.Together(F.Divide(lf.a[1], lf.a[2]))));
    IExpr q = engine.evaluate(F.Cancel(F.Together(F.Divide(lf.a[0], lf.a[2]))));
    if (!hasCircular(p, xVar) && !hasCircular(q, xVar)) {
      // Already rational, so the methods which read a rational normal form have had their chance.
      return F.NIL;
    }
    if (p.leafCount() + q.leafCount() > MAX_LEAF_COUNT) {
      return F.NIL;
    }

    IExpr tDummy = F.Dummy("t");
    for (int i = 0; i < PHI.length; i++) {
      IExpr substitution = F.unaryAST1(PHI[i], xVar);
      IExpr first = engine.evaluate(F.D(substitution, xVar));
      if (first.isZero()) {
        continue;
      }
      IExpr second = engine.evaluate(F.D(substitution, F.List(xVar, F.C2)));
      IExpr inverse = engine.evaluate(F.unaryAST1(INVERSE[i], tDummy));

      IExpr firstCoefficient = rationalize(
          F.Divide(F.Plus(second, F.Times(p, first)), F.Sqr(first)), xVar, inverse, tDummy, ctx);
      IExpr zeroCoefficient =
          rationalize(F.Divide(q, F.Sqr(first)), xVar, inverse, tDummy, ctx);
      if (firstCoefficient.isNIL() || zeroCoefficient.isNIL()) {
        continue;
      }

      IExpr unknown = F.unaryAST1(F.Dummy("Y"), tDummy);
      IExpr equation = F.Equal(F.Plus(engine.evaluate(F.D(unknown, F.List(tDummy, F.C2))),
          F.Times(firstCoefficient, engine.evaluate(F.D(unknown, tDummy))),
          F.Times(zeroCoefficient, unknown)), F.C0);
      IAST branches = DSolveODE.solveSubODE(equation, tDummy, unknown, c_n, ctx);
      if (branches.argSize() != 1) {
        continue;
      }
      IExpr body = engine.evaluate(F.subst(branches.arg1(), tDummy, substitution));
      if (body.isPresent() && body.isFree(tDummy, true)) {
        return body;
      }
    }
    return F.NIL;
  }

  /**
   * The coefficient written in the new variable, or {@link F#NIL} if it does not come out as a
   * ratio of polynomials there.
   */
  private static IExpr rationalize(IExpr coefficient, IExpr xVar, IExpr inverse, IExpr tDummy,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr substituted = ctx.evalTimeConstrained(
        F.Simplify(F.subst(engine.evaluate(coefficient), xVar, inverse)), STEP_SECONDS);
    if (substituted.isNIL() || !substituted.isFree(xVar) || substituted.isIndeterminate()) {
      return F.NIL;
    }
    IExpr together = engine.evaluate(F.Together(substituted));
    if (!engine.evaluate(F.PolynomialQ(F.Numerator(together), tDummy)).isTrue()
        || !engine.evaluate(F.PolynomialQ(F.Denominator(together), tDummy)).isTrue()) {
      return F.NIL;
    }
    return together;
  }

  /** Whether the coefficient applies a circular function to something containing the variable. */
  private static boolean hasCircular(IExpr expr, IExpr xVar) {
    return !expr.isFree(x -> x.isAST1() && x.isFunctionID(CIRCULAR) && !x.first().isFree(xVar),
        true);
  }
}
