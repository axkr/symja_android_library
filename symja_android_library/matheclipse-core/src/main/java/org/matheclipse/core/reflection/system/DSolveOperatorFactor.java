package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Equations of order three and above which the operator that makes them splits off a factor of the
 * first order.
 *
 * <p>
 * Writing the equation as <code>L[y] == 0</code>, the operator <code>D - r</code> divides
 * <code>L</code> from the right exactly when <code>Exp(Integrate(r))</code> solves the equation.
 * Dividing it out leaves an equation of one order less, which the cascade is asked about, and the
 * answer to the whole is what the remaining first order equation makes of that.
 *
 * <p>
 * Equations of order two are left to the methods above, whose search for a solution of this shape
 * is the same one and which reach more than this does.
 */
final class DSolveOperatorFactor {

  private DSolveOperatorFactor() {}

  /**
   * How many places the coefficients may become infinite at.
   *
   * <p>
   * The equations the unknowns of the ansatz satisfy are of the same degree as the order of the
   * equation, so a guess with many poles in it is a system {@link S#Solve} does not finish
   * eliminating.
   */
  private static final int MAX_POLES = 3;

  /** How far the guess reaches, in the degree of its polynomial part and in the order of its poles. */
  private static final int MAX_POLYNOMIAL_DEGREE = 2, MAX_POLE_ORDER = 2;

  /** The general solution, or {@link F#NIL} if no such factor is found. */
  static IExpr solve(LinearODEForm lf, IExpr yFunction, IExpr xVar, IExpr c_n, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    int n = lf.order;
    if (n < 3 || !lf.g.isZero() || lf.constantCoefficients) {
      return F.NIL;
    }
    IExpr[] a = monic(lf, xVar, engine);
    if (a == null || ctx.expired()) {
      return F.NIL;
    }

    IExpr r = findFactor(a, n, xVar, ctx);
    if (r.isNIL()) {
      return F.NIL;
    }
    IExpr[] quotient = divide(a, n, r, xVar, engine);

    // The equation of one order less which the factor leaves behind.
    IExpr zFunction = F.unaryAST1(F.Dummy("ofz"), xVar);
    IASTAppendable reduced = F.PlusAlloc(n);
    for (int j = 0; j < n; j++) {
      reduced.append(F.Times(quotient[j],
          j == 0 ? zFunction : engine.evaluate(F.D(zFunction, F.List(xVar, F.ZZ(j))))));
    }
    IAST branches = DSolveODE.solveSubODE(F.Equal(engine.evaluate(reduced), F.C0), xVar, zFunction,
        c_n, ctx);
    if (branches.argSize() == 0) {
      return F.NIL;
    }
    IExpr z = branches.arg1();

    // What is left is (D - r)y == z, which is linear and of the first order.
    IExpr integral = ctx.integrate(r, xVar, MAX_LEAF_COUNT);
    if (integral.isNIL()) {
      return F.NIL;
    }
    IExpr factor = engine.evaluate(F.Exp(integral));
    IExpr particular = ctx.integrate(engine.evaluate(F.Divide(z, factor)), xVar, MAX_LEAF_COUNT);
    if (particular.isNIL()) {
      return F.NIL;
    }
    return engine
        .evaluate(F.Times(factor, F.Plus(particular, ctx.nextConstant())));
  }

  /** How big an integral is still worth carrying. */
  private static final int MAX_LEAF_COUNT = 400;

  /**
   * The coefficients divided by the leading one, which is the form the division below is written
   * for.
   *
   * @return <code>null</code> if any coefficient is not a rational function with numbers in it
   */
  private static IExpr[] monic(LinearODEForm lf, IExpr xVar, EvalEngine engine) {
    int n = lf.order;
    if (lf.a[n].isZero()) {
      return null;
    }
    IExpr[] a = new IExpr[n + 1];
    for (int k = 0; k <= n; k++) {
      a[k] = engine.evaluate(F.Cancel(F.Together(F.Divide(lf.a[k], lf.a[n]))));
      // A coefficient carrying a symbol of its own would leave the equations for the unknowns of
      // the guess with two kinds of unknown in them, which Solve does not finish.
      if (!engine.evaluate(F.PolynomialQ(F.Numerator(a[k]), xVar)).isTrue()
          || !engine.evaluate(F.PolynomialQ(F.Denominator(a[k]), xVar)).isTrue()) {
        return null;
      }
    }
    return a;
  }

  /**
   * A rational <code>r</code> for which <code>D - r</code> divides the operator.
   *
   * <p>
   * Such an <code>r</code> can only become infinite where the coefficients do, so it is guessed as
   * a polynomial plus a part for each place they do, and what the guess has to satisfy is read off
   * as a system in its unknowns. The guess is widened a step at a time so that the cheapest one
   * which works is the one taken.
   */
  private static IExpr findFactor(IExpr[] a, int n, IExpr xVar, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IASTAppendable sum = F.PlusAlloc(n + 1);
    for (int k = 0; k <= n; k++) {
      sum.append(a[k]);
    }
    IExpr factorList =
        engine.evaluate(F.FactorList(F.Denominator(F.Together(engine.evaluate(sum)))));
    if (!factorList.isList()) {
      return F.NIL;
    }
    IAST places = (IAST) factorList;
    int poles = 0;
    for (int i = 1; i <= places.argSize(); i++) {
      IExpr entry = places.get(i);
      if (entry.isList() && entry.size() > 1 && !entry.first().isFree(xVar)) {
        poles++;
      }
    }
    if (poles > MAX_POLES) {
      return F.NIL;
    }

    for (int degree = 0; degree <= MAX_POLYNOMIAL_DEGREE; degree++) {
      for (int order = 1; order <= MAX_POLE_ORDER; order++) {
        if (ctx.expired()) {
          return F.NIL;
        }
        IASTAppendable unknowns = F.ListAlloc();
        IExpr guess = ansatz(places, xVar, degree, order, unknowns, engine);
        IExpr found = matchGuess(a, n, xVar, guess, unknowns, ctx);
        if (found.isPresent()) {
          return found;
        }
      }
    }
    return F.NIL;
  }

  /** A polynomial of the given degree plus a part at each place the coefficients become infinite. */
  private static IExpr ansatz(IAST places, IExpr xVar, int degree, int poleOrder,
      IASTAppendable unknowns, EvalEngine engine) {
    IASTAppendable terms = F.PlusAlloc(degree + 1);
    for (int j = 0; j <= degree; j++) {
      IExpr unknown = F.Dummy("of" + unknowns.argSize());
      unknowns.append(unknown);
      terms.append(F.Times(unknown, F.Power(xVar, F.ZZ(j))));
    }
    for (int i = 1; i <= places.argSize(); i++) {
      IExpr entry = places.get(i);
      if (!entry.isList() || entry.size() <= 1) {
        continue;
      }
      IExpr place = entry.first();
      int placeDegree = engine.evaluate(F.Exponent(place, xVar)).toIntDefault();
      if (placeDegree < 1) {
        continue;
      }
      for (int k = 1; k <= poleOrder; k++) {
        for (int l = 0; l < placeDegree; l++) {
          IExpr unknown = F.Dummy("of" + unknowns.argSize());
          unknowns.append(unknown);
          terms.append(F.Times(unknown, F.Power(xVar, F.ZZ(l)), F.Power(place, F.ZZ(-k))));
        }
      }
    }
    return engine.evaluate(terms);
  }

  /**
   * The guess made to satisfy what the equation asks of it, if it can be.
   *
   * <p>
   * Every branch of the solution is tried, and the one which is kept is the one whose division
   * leaves nothing behind. Unknowns the branch does not fix are set to zero, which is a choice and
   * therefore has to be checked rather than assumed.
   */
  private static IExpr matchGuess(IExpr[] a, int n, IExpr xVar, IExpr guess, IAST unknowns,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr residual = riccatiResidual(a, n, guess, xVar, engine);
    IExpr numerator = engine.evaluate(F.Numerator(F.Together(residual)));
    IExpr coefficients = engine.evaluate(F.CoefficientList(numerator, xVar));
    if (!coefficients.isList()) {
      return F.NIL;
    }
    IASTAppendable equations = F.ListAlloc(((IAST) coefficients).argSize());
    for (int i = 1; i <= ((IAST) coefficients).argSize(); i++) {
      IExpr coefficient = ((IAST) coefficients).get(i);
      if (!coefficient.isZero()) {
        equations.append(F.Equal(coefficient, F.C0));
      }
    }
    if (equations.argSize() == 0) {
      return F.NIL;
    }
    IExpr solutions = ctx.evalTimeConstrained(F.Solve(equations, unknowns), STEP_SECONDS);
    if (solutions.isNIL() || !solutions.isList()) {
      return F.NIL;
    }
    IAST branches = (IAST) solutions;
    for (int i = 1; i <= branches.argSize(); i++) {
      if (ctx.expired()) {
        return F.NIL;
      }
      IExpr branch = branches.get(i);
      if (!branch.isList()) {
        continue;
      }
      IExpr candidate = engine.evaluate(F.ReplaceAll(guess, branch));
      for (int j = 1; j <= unknowns.argSize(); j++) {
        candidate = F.subst(candidate, unknowns.get(j), F.C0);
      }
      candidate = engine.evaluate(F.Cancel(F.Together(candidate)));
      if (!candidate.isFree(xVar) || candidate.isNumber()) {
        IExpr remainder = remainderOf(a, n, candidate, xVar, engine);
        if (DSolveODE.isVanishing(remainder, engine)) {
          return candidate;
        }
      }
    }
    return F.NIL;
  }

  /** How long one search step may take. */
  private static final int STEP_SECONDS = 4;

  /**
   * What the equation asks of <code>Exp(Integrate(r))</code>.
   *
   * <p>
   * Its <code>k</code>th derivative is that same function times <code>P[k]</code>, where
   * <code>P[0] == 1</code> and <code>P[k+1] == P[k]' + r*P[k]</code>, so the equation holds exactly
   * when the sum below vanishes.
   */
  private static IExpr riccatiResidual(IExpr[] a, int n, IExpr r, IExpr xVar, EvalEngine engine) {
    IExpr p = F.C1;
    IExpr residual = a[0];
    for (int k = 1; k <= n; k++) {
      p = engine.evaluate(F.Plus(F.D(p, xVar), F.Times(r, p)));
      residual = engine.evaluate(F.Plus(residual, F.Times(a[k], p)));
    }
    return residual;
  }

  /** The operator divided by <code>D - r</code> from the right, which is of one order less. */
  private static IExpr[] divide(IExpr[] a, int n, IExpr r, IExpr xVar, EvalEngine engine) {
    IExpr[] derivatives = new IExpr[n];
    derivatives[0] = r;
    for (int i = 1; i < n; i++) {
      derivatives[i] = engine.evaluate(F.D(derivatives[i - 1], xVar));
    }
    IExpr[] q = new IExpr[n];
    q[n - 1] = a[n];
    for (int m = n - 1; m >= 1; m--) {
      IASTAppendable sum = F.PlusAlloc(n - m + 1);
      sum.append(a[m]);
      for (int j = m; j <= n - 1; j++) {
        sum.append(F.Times(F.Binomial(F.ZZ(j), F.ZZ(m)), derivatives[j - m], q[j]));
      }
      q[m - 1] = engine.evaluate(F.Cancel(F.Together(engine.evaluate(sum))));
    }
    return q;
  }

  /** What the division leaves behind, which is zero exactly when the factor divides. */
  private static IExpr remainderOf(IExpr[] a, int n, IExpr r, IExpr xVar, EvalEngine engine) {
    IExpr[] q = divide(a, n, r, xVar, engine);
    IExpr[] derivatives = new IExpr[n];
    derivatives[0] = r;
    for (int i = 1; i < n; i++) {
      derivatives[i] = engine.evaluate(F.D(derivatives[i - 1], xVar));
    }
    IASTAppendable sum = F.PlusAlloc(n + 1);
    sum.append(a[0]);
    for (int j = 0; j < n; j++) {
      sum.append(F.Times(q[j], derivatives[j]));
    }
    return engine.evaluate(F.Together(engine.evaluate(sum)));
  }
}
