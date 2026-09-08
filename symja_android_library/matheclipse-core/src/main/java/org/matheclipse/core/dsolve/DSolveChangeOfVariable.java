package org.matheclipse.core.dsolve;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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
 * The same for <code>t == E^(r*x)</code>, which is what an equation built out of exponentials
 * needs: <code>y'' + a*(r*E^(r*x) - a*E^(2*r*x))*y == 0</code> becomes rational in <code>t</code>
 * and is then Liouvillian, so Kovacic's method finds it.
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

  /**
   * How long one of the evaluations inside may take. The number is what it is on the machine
   * the solvers were tuned on; a slower machine is given proportionally longer, see
   * {@link org.matheclipse.core.basic.MachineProfile}.
   */
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
    boolean circular = hasCircular(p, xVar) || hasCircular(q, xVar);
    if (p.leafCount() + q.leafCount() > MAX_LEAF_COUNT) {
      return F.NIL;
    }
    IExpr rate = exponentialRate(p, q, xVar, engine);
    if (!circular && rate.isNIL()) {
      // Already rational, so the methods which read a rational normal form have had their chance.
      return F.NIL;
    }

    // Only the substitutions this equation could want: trying the circular ones on an equation
    // with no circular function in it spends the whole budget below on coefficients that cannot
    // come out rational.
    IExpr tDummy = F.Dummy("t");
    IASTAppendable substitutions = F.ListAlloc(PHI.length + 1);
    IASTAppendable inverses = F.ListAlloc(PHI.length + 1);
    if (circular) {
      for (int i = 0; i < PHI.length; i++) {
        substitutions.append(F.unaryAST1(PHI[i], xVar));
        inverses.append(engine.evaluate(F.unaryAST1(INVERSE[i], tDummy)));
      }
    }
    if (rate.isPresent()) {
      substitutions.append(F.Exp(F.Times(rate, xVar)));
      inverses.append(engine.evaluate(F.Divide(F.Log(tDummy), rate)));
    }

    for (int i = 1; i <= substitutions.argSize(); i++) {
      IExpr substitution = substitutions.get(i);
      IExpr first = engine.evaluate(F.D(substitution, xVar));
      if (first.isZero()) {
        continue;
      }
      IExpr second = engine.evaluate(F.D(substitution, F.List(xVar, F.C2)));
      IExpr inverse = inverses.get(i);

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

  /**
   * The rate <code>r</code> of the exponential to substitute for, or {@link F#NIL} when the
   * coefficients carry none that would serve.
   *
   * <p>
   * Every exponential in the equation has to be a power of the one substituted for, or what comes
   * out in <code>t</code> is not rational: with <code>E^x</code> and <code>E^(Sqrt(2)*x)</code>
   * beside each other no substitution makes both polynomial. So each rate found is tried as the
   * base and kept only if every other is a positive integer multiple of it.
   */
  private static IExpr exponentialRate(IExpr p, IExpr q, IExpr xVar, EvalEngine engine) {
    IASTAppendable rates = F.ListAlloc();
    if (!collectRates(p, xVar, engine, rates) || !collectRates(q, xVar, engine, rates)) {
      return F.NIL;
    }
    for (int i = 1; i <= rates.argSize(); i++) {
      IExpr base = rates.get(i);
      boolean everyOneAMultiple = true;
      for (int k = 1; k <= rates.argSize(); k++) {
        IExpr ratio = engine.evaluate(F.Cancel(F.Divide(rates.get(k), base)));
        if (!ratio.isInteger() || !ratio.isPositive()) {
          everyOneAMultiple = false;
          break;
        }
      }
      if (everyOneAMultiple) {
        return base;
      }
    }
    return F.NIL;
  }

  /**
   * Collects the rate of every <code>E^(r*x)</code> in the expression, each once, and answers
   * whether they were all of that form. An exponential of anything else in the variable --
   * <code>E^(x^2/2)</code> -- is not made rational by any of these substitutions, so finding one
   * ends the search rather than being passed over.
   */
  private static boolean collectRates(IExpr expr, IExpr xVar, EvalEngine engine,
      IASTAppendable rates) {
    if (expr.isPower() && expr.base().equals(S.E)) {
      IExpr exponent = expr.exponent();
      if (exponent.isFree(xVar, true)) {
        return true;
      }
      IExpr rate = engine.evaluate(F.Cancel(F.Divide(exponent, xVar)));
      if (rate.isZero() || !rate.isFree(xVar, true)) {
        return false;
      }
      if (!rates.contains(rate)) {
        rates.append(rate);
      }
      return true;
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      for (int i = 0; i < ast.size(); i++) {
        if (!collectRates(ast.get(i), xVar, engine, rates)) {
          return false;
        }
      }
    }
    return true;
  }

  /** Whether the coefficient applies a circular function to something containing the variable. */
  private static boolean hasCircular(IExpr expr, IExpr xVar) {
    return !expr.isFree(x -> x.isAST1() && x.isFunctionID(CIRCULAR) && !x.first().isFree(xVar),
        true);
  }
}
