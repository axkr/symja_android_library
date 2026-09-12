package org.matheclipse.core.dsolve;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.basic.MachineProfile;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.reflection.system.Eliminate;

/**
 * The scalar ordinary differential equation cascade of {@link DSolve}.
 */
final class DSolveODE {

  private DSolveODE() {}

  /**
   * Note: We set a maximum derivative order to prevent infinite recursion in pathological cases.
   * This is a safeguard and can be adjusted as needed.
   */
  /** Beyond this the relation raised to that power is larger than Solve can use. */
  private static final int MAX_EXPONENTIATION_POWER = 12;

  /**
   * How deep in the cascade an inhomogeneous equation is still solved through its homogeneous one.
   * The homogeneous equation has no forcing term, so the method cannot enter itself; this bounds
   * the work a nested equation does before it is declined.
   */
  private static final int MAX_VARIATION_DEPTH = 2;

  /** How long a condition at a point the solution does not reach may take to read as a limit. */
  private static final int LIMIT_AT_CONDITION_SECONDS = 3;

  /** Beyond this, deciding whether the exactness test vanishes costs more than it is worth. */
  private static final int MAX_EXACT_DIFFERENCE_LEAF_COUNT = 100;

  static final int MAX_DERIVATIVE_ORDER = 10;

  /** How deep in the cascade the reduction of a Riccati equation is still attempted. */
  private static final int MAX_RICCATI_DEPTH = 3;

  /** How big the coefficient of the equation that reduction leaves may be. */
  private static final int MAX_RICCATI_LEAF_COUNT = 60;

  /** How close to zero a guessed particular solution's residual must come at a sample point. */
  private static final double RICCATI_NUMERIC_TOLERANCE = 1.0e-9;

  /** Above this size the separable candidate is not offered to {@code Factor}. */
  private static final int MAX_SEPARABLE_FACTOR_LEAF_COUNT = 100;

  /** How long {@code Solve} may work on a separated equation. */
  private static final int SOLVE_SEPARATED_SECONDS = 3;

  static IExpr odeExact(EvalEngine engine, IExpr m, IExpr n, IExpr x, IExpr y, IExpr C_1) {
    // Substitute y(x) with a dummy variable Y to treat it as an independent variable
    // for partial differentiation and integration without triggering the chain rule.
    IExpr yDummy = F.Dummy("Y");
    IExpr mDummy = F.subst(m, y, yDummy);
    IExpr nDummy = F.subst(n, y, yDummy);

    // Check for exactness: dM/dY == dN/dx
    IExpr dMdy = engine.evaluate(F.D(mDummy, yDummy));
    IExpr dNdx = engine.evaluate(F.D(nDummy, x));

    IExpr diff = engine.evaluate(F.Subtract(dMdy, dNdx));

    // A pair can be exact without saying so in the form it arrives in, and the integrating factor
    // method hands this one a pair it has just multiplied by mu. The cheap test comes first.
    if (diff.isZero()
        || (diff.leafCount() < MAX_EXACT_DIFFERENCE_LEAF_COUNT && isVanishing(diff, engine))) {
      // f(x,Y) = Integrate(M, x)
      IExpr intM = DSolveContext.integrate(mDummy, x, engine);
      if (intM.isNIL()) {
        return F.NIL;
      }

      // N - d/dY(intM)
      IExpr dIntMdy = engine.evaluate(F.D(intM, yDummy));
      // Simplify before integrating: for an exact equation g'(Y) is free of `x`, but it is not
      // necessarily in that form yet - Integrate() may return the antiderivative of M as a power
      // of the linear factor (for example Integrate(Y^2+2*x*Y, x) as Y*(2*x+Y)^2/4), and
      // integrating the unexpanded difference over Y then adds an `x` dependent term to g(Y)
      // (here -2/3*x^3), which is not a constant of the Y integration and breaks the implicit
      // solution.
      IExpr gPrime = engine.evaluate(F.Simplify(F.Subtract(nDummy, dIntMdy)));

      // g(Y) = Integrate(gPrime, Y)
      IExpr gy = DSolveContext.integrate(gPrime, yDummy, engine);
      if (gy.isNIL()) {
        return F.NIL;
      }

      // The implicit solution is intM + gy = C_1
      IExpr f_xy = engine.evaluate(F.Plus(intM, gy));

      // Substitute y(x) back
      IExpr f_xy_real = F.subst(f_xy, yDummy, y);

      IExpr equation = F.Equal(f_xy_real, C_1);

      // Attempt to extract explicit y(x) from the implicit equation
      IExpr ySols = engine.evaluate(F.Solve(equation, F.List(y)));
      IAST extracted = DSolveUtil.extractSolveResults(ySols);
      if (extracted.argSize() > 0) {
        IASTAppendable roots = F.ListAlloc(extracted.argSize());
        for (int i = 1; i <= extracted.argSize(); i++) {
          // roots.append(engine.evaluate(F.Simplify(extracted.get(i))));
          roots.append(extracted.get(i));
        }
        if (roots.argSize() == 1) {
          return roots.arg1();
        } else if (roots.argSize() > 1) {
          return roots; // Return all roots as a List
        }
      }
    }
    return F.NIL;
  }

  /**
   * Solves homogeneous first-order ODEs by applying the substitution y = v * x to reduce the
   * equation to a separable form.
   */
  static IExpr odeHomogeneous(EvalEngine engine, IExpr m, IExpr n, IExpr x, IExpr y, IExpr C_1) {
    // Substitute y -> x * v
    IExpr v = F.Dummy("v");

    IExpr mSub = F.subst(m, y, F.Times(x, v));
    IExpr nSub = F.subst(n, y, F.Times(x, v));

    // Transform to separable: M_v dx + N_v dv = 0 => (mSub + v * nSub) dx + (x * nSub) dv = 0
    IExpr m_v = engine.evaluate(F.Plus(mSub, F.Times(v, nSub)));
    IExpr n_v = engine.evaluate(F.Times(x, nSub));

    // Normalize so that the coefficient of dv is exactly 1
    IExpr normalizedM = engine.evaluate(F.Factor(F.Divide(m_v, n_v)));

    // The substitution only separates the variables when the equation really is homogeneous, which
    // is the case exactly when x drops out of dv/dx == -normalizedM apart from the factor 1/x.
    // Without this test an equation which is not homogeneous is still handed to the separable
    // solver, whose integrand then contains both variables: y'(x) == Sqrt(y(x)^4/2 + C(1)) sent
    // Integrate into a surd rationalization it did not return from.
    IExpr scaled = engine.evaluate(F.Simplify(F.Times(x, normalizedM)));
    if (!scaled.isFree(x)) {
      // An equation homogeneous of degree zero has x*normalizedM a function of v alone, but a
      // radical does not collapse to one while x is still a symbol: Sqrt(x^2*(1+v^2))/x is
      // Sqrt(1+v^2) only where x is positive, which is what PowerExpand assumes and what the
      // substitution y == v*x is entitled to. Without this the equations whose reduction leaves a
      // radical -- x*y' == y + Sqrt(x^2+y^2) and its relatives -- looked inhomogeneous and were
      // left to methods which do not answer them.
      IExpr expanded = engine.evaluate(F.PowerExpand(scaled));
      if (!expanded.isFree(x)) {
        return F.NIL;
      }
      scaled = expanded;
      // and the equation which is integrated below is written with them collapsed too
      normalizedM = engine.evaluate(F.Divide(scaled, x));
    }

    // Try to solve the transformed equation using the existing separable solver
    IExpr vSol = odeSeparable(engine, normalizedM, F.C1, x, v, C_1);

    if (vSol.isPresent()) {
      // odeSeparable has already solved for v, so undoing the substitution y == v*x is a
      // multiplication. Replacing v by y/x in a result which no longer contains v, and then asking
      // for y, left this method unable to return anything at all.
      IAST branches = DSolveUtil.stripConditionalExpression(vSol).makeList();
      IASTAppendable results = F.ListAlloc(branches.argSize());
      for (int i = 1; i <= branches.argSize(); i++) {
        IExpr branch = branches.get(i);
        if (!branch.isFree(v, true)) {
          continue;
        }
        results.append(engine.evaluate(F.Expand(F.Times(x, branch))));
      }
      if (results.argSize() == 1) {
        return results.arg1();
      } else if (results.argSize() > 1) {
        return results;
      }
    }
    return F.NIL;
  }

  /**
   * Solves ODEs by finding an integrating factor to make the equation exact.
   */
  static IExpr odeIntegratingFactor(EvalEngine engine, IExpr m, IExpr n, IExpr x, IExpr y,
      IExpr C_1) {
    // Substitute y(x) with a dummy variable Y for partial derivatives
    IExpr yDummy = F.Dummy("Y");
    IExpr mDummy = F.subst(m, y, yDummy);
    IExpr nDummy = F.subst(n, y, yDummy);

    IExpr dMdy = engine.evaluate(F.D(mDummy, yDummy));
    IExpr dNdx = engine.evaluate(F.D(nDummy, x));

    // Case 1: Integrating factor depends only on x
    // Check if (dM/dy - dN/dx) / N == f(x)
    // Cancelled, because the ratio is a function of x alone only after the common factor goes:
    // x^3 + y/x + (y^2 + Log(x))*y' == 0 is exact as it stands, but the coefficient of y' is
    // cleared of its denominator before it arrives here, and the pair which arrives is not. Its
    // integrating factor is 1/x, and the ratio which says so reads
    // (-Y^2 - Log(x))/(x*(Y^2 + Log(x))) until it is cancelled.
    IExpr diff1 = cancel(F.Divide(F.Subtract(dMdy, dNdx), nDummy), engine);

    if (diff1.isPresent() && diff1.isFree(yDummy)) {
      IExpr exponent1 = DSolveContext.integrate(diff1, x, engine);
      if (exponent1.isNIL()) {
        return F.NIL;
      }
      IExpr mu = engine.evaluate(F.Exp(exponent1));
      IExpr exactM = engine.evaluate(F.Times(mu, m));
      IExpr exactN = engine.evaluate(F.Times(mu, n));

      // The equation is now exact, pass it back to our exact solver
      return odeExact(engine, exactM, exactN, x, y, C_1);
    }

    // Case 2: Integrating factor depends only on y
    // Check if (dN/dx - dM/dy) / M == g(y)
    IExpr diff2 = cancel(F.Divide(F.Subtract(dNdx, dMdy), mDummy), engine);

    if (diff2.isPresent() && diff2.isFree(x)) {
      IExpr exponent2 = DSolveContext.integrate(diff2, yDummy, engine);
      if (exponent2.isNIL()) {
        return F.NIL;
      }
      IExpr muDummy = engine.evaluate(F.Exp(exponent2));

      // Substitute back y(x) into the integrating factor
      IExpr mu = engine.evaluate(F.subst(muDummy, yDummy, y));

      IExpr exactM = engine.evaluate(F.Times(mu, m));
      IExpr exactN = engine.evaluate(F.Times(mu, n));

      return odeExact(engine, exactM, exactN, x, y, C_1);
    }

    return F.NIL;
  }

  /**
   * Sorts {@code quotient} into a factor free of {@code y} and one free of {@code x}, which is what
   * separating the variables needs.
   *
   * @return the two factors, or {@code null} if the expression does not split into them
   */
  private static IExpr[] separateFactors(EvalEngine engine, IExpr quotient, IExpr x, IExpr y) {
    if (quotient.isFree(y)) {
      return new IExpr[] {quotient, F.C1};
    }
    IAST timesAST = quotient.isTimes() ? (IAST) quotient : F.Times(quotient);
    IASTAppendable fx = F.TimesAlloc(timesAST.argSize());
    IASTAppendable gy = F.TimesAlloc(timesAST.argSize());
    timesAST.forEach(expr -> {
      if (expr.isFree(y)) {
        fx.append(expr);
      } else {
        gy.append(expr);
      }
    });
    IExpr fxExpr = engine.evaluate(fx);
    IExpr gyExpr = engine.evaluate(gy);
    if (fxExpr.isNIL() || gyExpr.isNIL() || !gyExpr.isFree(x)) {
      // A factor which contains both variables is not separated by sorting it into one side. Its
      // reciprocal would be integrated over y with x still in it, and the equation that came out
      // of that would not be the one that was asked about.
      return null;
    }
    return new IExpr[] {fxExpr, gyExpr};
  }

  static IExpr odeSeparable(EvalEngine engine, IExpr m, IExpr n, IExpr x, IExpr y, IExpr C_1) {
    return odeSeparable(engine, m, n, x, y, C_1, null);
  }

  /**
   * The separable solver, with the option of naming the constant of separation before the relation
   * is solved for <code>y</code> rather than after.
   *
   * @param point <code>{x0, y0}</code> of a condition <code>y(x0) == y0</code>, or
   *        <code>null</code> for the general solution. The relation reads
   *        <code>G(y) == F(x) + C</code>, so the condition names the constant by evaluation, and
   *        the inversion which follows has numbers where it would have had <code>C</code>. That is
   *        the difference between a cubic in <code>y</code> which can be solved and one which
   *        cannot: the constant is buried under a square root and inside a cube root, and asking
   *        for it back afterwards is what fails.
   */
  static IExpr odeSeparable(EvalEngine engine, IExpr m, IExpr n, IExpr x, IExpr y, IExpr C_1,
      IExpr[] point) {
    // y' == -m/n separates whenever the quotient does, so the coefficient of y' is divided out
    // first. Only the pair (m, n) as a whole is exact, which is why the division stays local to
    // this method and the other members of the cascade keep seeing the pair.
    IExpr quotient = n.isOne() ? m : engine.evaluate(F.Divide(m, n));
    IExpr[] parts = separateFactors(engine, quotient, x, y);
    if (parts == null && quotient.leafCount() <= MAX_SEPARABLE_FACTOR_LEAF_COUNT) {
      // A sum never sorts into an x part and a y part, but the sum of a separable equation
      // factors into one: x^2*y'(x) == 1 - x^2 + y(x)^2 - x^2*y(x)^2 arrives fully expanded.
      IExpr factored = engine.evaluate(F.Factor(quotient));
      if (factored.isPresent() && !factored.equals(quotient)) {
        parts = separateFactors(engine, factored, x, y);
      }
      if (parts == null) {
        // A root of a product of the two is one factor as it stands and separates only where both
        // are positive, which is what PowerExpand assumes: y'(x) == 3*Sqrt(x*y(x)) is a separable
        // equation whose right hand side is a single Power.
        IExpr expanded = engine.evaluate(F.PowerExpand(quotient));
        if (expanded.isPresent() && !expanded.equals(quotient)) {
          parts = separateFactors(engine, expanded, x, y);
        }
      }
    }
    if (parts != null) {
      IExpr fxExpr = parts[0];
      IExpr gyExpr = parts[1];
      gyExpr = DSolveContext.integrate(gyExpr.inverse(), y, engine);
      // Separating the variables is only half of the method: the result still has to be solved
      // for y. An elliptic integral cannot be, and asking Eliminate to try is where
      // y''(x) == y(x)^3 with initial conditions used to run without ever returning.
      if (!DSolveContext.isUsable(gyExpr)) {
        return F.NIL;
      }
      IExpr fxIntegral = DSolveContext.integrate(F.Times(F.CN1, fxExpr), x, engine);
      if (fxIntegral.isNIL()) {
        return F.NIL;
      }
      IExpr constant = point == null ? C_1 : separationConstant(engine, gyExpr, fxIntegral, x, y,
          point);
      if (constant.isNIL()) {
        return F.NIL;
      }
      fxExpr = S.Plus.of(engine, fxIntegral, constant);
      if (!DSolveContext.isUsable(fxExpr)) {
        return F.NIL;
      }
      IExpr yEquation = S.Subtract.of(engine, gyExpr, fxExpr);
      // Every branch, not the first one. A relation of second degree in y is two functions, and
      // which of the two an initial condition picks is not known here: y'(x) == (2 - E^x)/(3 + 2y)
      // separates into (y + 3/2)^2 == ..., whose minus branch cannot meet y(0) == 0 and whose plus
      // branch can. Keeping one of them turned that equation, and six more of its kind, into an
      // equation with no solution.
      // Each attempt is filtered before the next is skipped. An inversion which answers with
      // something still carrying y is no answer, and letting it stand as one is what stopped the
      // methods below from ever being reached for the equations whose relation has a lone y in it
      // beside a logarithm.
      IExpr result = usableBranches(engine, Eliminate.extractVariable(yEquation, y, true, engine), y);
      if (result.isNIL()) {
        // The antiderivative is not always in a form the equation can be solved for y in. A sum
        // of logarithms is the usual case: Integrate answers 1/(1-y^2) with
        // -Log(1-y)/2 + Log(1+y)/2, which nothing here can invert, while the ArcTanh(y) it is
        // equal to inverts at once. So the integral is collected once and the equation offered
        // again, which is what makes y'(x) == (y(x)^2 + x*y(x) - x^2)/x^2 solvable.
        IExpr collected = engine.evaluate(F.FullSimplify(gyExpr));
        if (collected.isPresent() && !collected.equals(gyExpr)) {
          result = usableBranches(engine, Eliminate
              .extractVariable(S.Subtract.of(engine, collected, fxExpr), y, true, engine), y);
        }
      }
      if (result.isNIL()) {
        result = solveSeparatedEquation(engine, yEquation, y);
      }
      if (result.isNIL()) {
        // Before the exponentiated attempt, because the relations this reads are ones the other
        // would hand to Solve as an exponential equation, and Solve answers one of those with a
        // root which does not satisfy it: E^(10*y)/(3/2+y) == E^(4*x) comes back as
        // -ProductLog(-10/E^(4*x))/10, whose residual at x == 1 is about -54. The formula below is
        // exact, so it is asked first.
        result = solveProductLogEquation(engine, gyExpr, fxExpr, y);
      }
      if (result.isNIL()) {
        result = solveExponentiatedEquation(engine, gyExpr, fxExpr, y);
      }
      return result;
    }
    return F.NIL;
  }

  /**
   * Solves the separated equation by raising both sides to the power of <code>E</code>.
   *
   * <p>
   * An autonomous equation separates into a sum of logarithms, which is what the antiderivative of
   * a rational function is, and nothing inverts a sum of logarithms as it stands. Exponentiating
   * turns that sum into a product of powers -- <code>E</code> is one to one, so the relation is the
   * same one -- and the product is algebraic in <code>y</code>:
   * <code>y' == y*(y-2)*(y-1)</code> separates into
   * <code>Log(y^2-2*y)/2 - Log(1-y) == x + C</code>, whose exponential is a quadratic in
   * <code>y</code> over another, and it is solved outright.
   *
   * @param gyExpr the antiderivative in <code>y</code>
   * @param fxExpr the antiderivative in <code>x</code>, with the constant already in it
   */
  private static IExpr solveExponentiatedEquation(EvalEngine engine, IExpr gyExpr, IExpr fxExpr,
      IExpr y) {
    int power = exponentiationPower(gyExpr, y);
    if (power == 0) {
      return F.NIL;
    }
    // The relation is raised to the power which clears the denominators of the logarithms'
    // coefficients before it is exponentiated, so that what comes out is rational in y rather than
    // a radical. Solve answers (1-y)^2/(y^2-2*y) == E^(-2*x) and does not answer the same relation
    // written with a Sqrt underneath, which is what taking the exponential on its own leaves.
    // Both signs of the power, because Solve reads the two the same relation can be written in
    // differently: it answers (1-E^y)/E^y == E^x and leaves E^y/(1-E^y) == E^(-x) as it stands,
    // and which of the two the antiderivatives come out as is not settled here.
    IExpr solved = solveExponentiated(engine, gyExpr, fxExpr, y, power);
    return solved.isPresent() ? solved : solveExponentiated(engine, gyExpr, fxExpr, y, -power);
  }

  /** One attempt of {@link #solveExponentiatedEquation}, with the relation raised to `power`. */
  private static IExpr solveExponentiated(EvalEngine engine, IExpr gyExpr, IExpr fxExpr, IExpr y,
      int power) {
    IExpr lhs = engine.evaluate(F.PowerExpand(F.Exp(F.Times(F.ZZ(power), gyExpr))));
    IExpr rhs = engine.evaluate(F.PowerExpand(F.Exp(F.Times(F.ZZ(power), fxExpr))));
    if (lhs.isNIL() || rhs.isNIL() || !rhs.isFree(y, true) || !lhs.isFree(S.Log, true)) {
      return F.NIL;
    }
    IExpr solutions;
    try {
      solutions = engine.evaluate(F.TimeConstrained(F.Solve(F.Equal(lhs, rhs), y),
          F.ZZ(MachineProfile.seconds(SOLVE_SEPARATED_SECONDS)), S.$Aborted));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
    if (solutions.isNIL() || solutions.equals(S.$Aborted)) {
      return F.NIL;
    }
    return usableBranches(engine, DSolveUtil.extractSolveResults(solutions), y);
  }

  /**
   * Solves a separated relation which mixes a linear form with its own logarithm.
   *
   * <p>
   * <code>p*u + q*Log(u) == T</code> is what a first order equation separates into whenever the
   * denominator of the integrand has a repeated factor or a factor the numerator shares, and
   * nothing algebraic inverts it. <code>ProductLog</code> does: the relation is
   * <code>(p/q)*u*E^((p/q)*u) == (p/q)*E^(T/q)</code>, whose left side is the function
   * <code>ProductLog</code> inverts by definition, so <code>u == (q/p)*ProductLog((p/q)*E^(T/q))
   * </code>. The reciprocal form <code>p/u + q*Log(u) == T</code> is the same relation in
   * <code>1/u</code>, and it is what a homogeneous equation such as
   * <code>y'(x) == (x + 3*y(x))/(x - y(x))</code> reduces to.
   *
   * <p>
   * The principal branch is taken, as everywhere else here; a branch which does not answer the
   * equation is refused by the verification the caller does.
   */
  private static IExpr solveProductLogEquation(EvalEngine engine, IExpr gyExpr, IExpr fxExpr,
      IExpr y) {
    IExpr[] form = productLogForm(engine, gyExpr, y);
    if (form == null) {
      return F.NIL;
    }
    IExpr p = form[0];
    IExpr q = form[1];
    IExpr linear = form[2];
    boolean reciprocal = form[3].isTrue();
    IExpr constant = form[4];
    // What is left on the other side once the terms free of the unknown have moved across.
    IExpr t = engine.evaluate(F.Subtract(fxExpr, constant));
    if (reciprocal) {
      // p/u + q*Log(u) == T is the same relation as p*s + (-q)*Log(s) == T in s == 1/u, so only
      // the coefficient of the logarithm changes sign. The right hand side does not: negating it
      // as well put the argument of ProductLog on the wrong side of -1/E, where it is complex for
      // every real x, and the verification then refused an answer which was nearly right.
      q = engine.evaluate(F.Negate(q));
    }
    IExpr ratio = engine.evaluate(F.Divide(p, q));
    IExpr s = engine.evaluate(F.Times(F.Power(ratio, F.CN1),
        F.ProductLog(F.Times(ratio, F.Exp(F.Divide(t, q))))));
    IExpr u = reciprocal ? engine.evaluate(F.Power(s, F.CN1)) : s;
    // linear is gamma*y + delta, so y is (u - delta)/gamma.
    IExpr gamma = engine.evaluate(F.Coefficient(linear, y, F.C1));
    IExpr delta = engine.evaluate(F.Coefficient(linear, y, F.C0));
    IExpr root = engine.evaluate(F.Divide(F.Subtract(u, delta), gamma));
    return usableBranches(engine, root, y);
  }

  /**
   * Reads <code>gyExpr</code> as <code>p*u + q*Log(u) + constant</code>, or as
   * <code>p/u + q*Log(u) + constant</code>, with <code>u</code> a linear form in <code>y</code>.
   *
   * @return <code>{p, q, u, reciprocal, constant}</code>, or <code>null</code> when it is neither
   */
  private static IExpr[] productLogForm(EvalEngine engine, IExpr gyExpr, IExpr y) {
    IExpr logCoefficient = F.NIL;
    IExpr linear = F.NIL;
    IExpr algebraicCoefficient = F.NIL;
    boolean reciprocal = false;
    IASTAppendable rest = F.PlusAlloc(4);
    IAST terms = gyExpr.isPlus() ? (IAST) gyExpr : F.Plus(gyExpr);
    for (int i = 1; i <= terms.argSize(); i++) {
      IExpr term = terms.get(i);
      if (term.isFree(y, true)) {
        rest.append(term);
        continue;
      }
      IExpr carried = term;
      IExpr coefficient = F.C1;
      if (term.isTimes()) {
        IAST factors = (IAST) term;
        IASTAppendable carrying = F.TimesAlloc(factors.argSize());
        IASTAppendable coefficients = F.TimesAlloc(factors.argSize());
        for (int k = 1; k <= factors.argSize(); k++) {
          if (factors.get(k).isFree(y, true)) {
            coefficients.append(factors.get(k));
          } else {
            carrying.append(factors.get(k));
          }
        }
        carried = carrying.oneIdentity1();
        coefficient = coefficients.oneIdentity1();
      }
      if (carried.isLog()) {
        if (logCoefficient.isPresent() || !isLinearIn(carried.first(), y, engine)) {
          return null;
        }
        logCoefficient = coefficient;
        linear = carried.first();
        continue;
      }
      if (algebraicCoefficient.isPresent()) {
        return null;
      }
      if (isLinearIn(carried, y, engine)) {
        algebraicCoefficient = coefficient;
        continue;
      }
      if (carried.isPower() && carried.exponent().isMinusOne()
          && isLinearIn(carried.base(), y, engine)) {
        algebraicCoefficient = coefficient;
        reciprocal = true;
        continue;
      }
      return null;
    }
    if (logCoefficient.isNIL() || algebraicCoefficient.isNIL() || logCoefficient.isZero()
        || algebraicCoefficient.isZero()) {
      return null;
    }

    // Both terms have to speak about the same linear form, so the algebraic one is rewritten in
    // terms of the logarithm's argument: a*y == (a/gamma)*(u - delta), and what that leaves over
    // is free of y and moves to the other side.
    IExpr algebraic = F.NIL;
    for (int i = 1; i <= terms.argSize(); i++) {
      IExpr term = terms.get(i);
      if (!term.isFree(y, true) && term.isFree(S.Log, true)) {
        algebraic = term;
      }
    }
    IExpr p;
    if (reciprocal) {
      IExpr scaled = engine.evaluate(F.Simplify(F.Times(algebraic, linear)));
      if (!scaled.isFree(y, true)) {
        return null;
      }
      p = scaled;
    } else {
      IExpr gamma = engine.evaluate(F.Coefficient(linear, y, F.C1));
      IExpr delta = engine.evaluate(F.Coefficient(linear, y, F.C0));
      IExpr slope = engine.evaluate(F.Coefficient(algebraic, y, F.C1));
      IExpr offset = engine.evaluate(F.Coefficient(algebraic, y, F.C0));
      if (gamma.isZero() || !slope.isFree(y, true) || !offset.isFree(y, true)) {
        return null;
      }
      p = engine.evaluate(F.Divide(slope, gamma));
      rest.append(engine.evaluate(F.Subtract(offset, F.Times(p, delta))));
    }
    IExpr constant = engine.evaluate(rest.oneIdentity0());
    if (p.isNIL() || p.isZero() || !p.isFree(y, true) || !constant.isFree(y, true)) {
      return null;
    }
    return new IExpr[] {p, logCoefficient, linear, F.bool(reciprocal), constant};
  }

  /** Whether <code>expr</code> is <code>gamma*y + delta</code> with a non-zero slope. */
  private static boolean isLinearIn(IExpr expr, IExpr y, EvalEngine engine) {
    if (expr.isFree(y, true) || !expr.isPolynomial(y)) {
      return false;
    }
    IExpr slope = engine.evaluate(F.Coefficient(expr, y, F.C1));
    IExpr offset = engine.evaluate(F.Coefficient(expr, y, F.C0));
    if (slope.isZero() || !slope.isFree(y, true) || !offset.isFree(y, true)) {
      return false;
    }
    return DSolveODE.isVanishing(
        engine.evaluate(F.Subtract(expr, F.Plus(F.Times(slope, y), offset))), engine);
  }

  /**
   * The power the relation is raised to before it is exponentiated  /**
   * The power the relation is raised to before it is exponentiated, or <code>0</code> when
   * exponentiating it gives nothing algebraic in <code>y</code>.
   *
   * <p>
   * Every term has to be a logarithm, or <code>y</code> itself, or free of <code>y</code>: those
   * become a power, a power of <code>E^y</code>, and a factor. A term like <code>1/y</code>, which
   * a repeated root of the denominator leaves behind, becomes <code>E^(1/y)</code> and is no more
   * invertible than the sum was -- <code>y' == y^2*(y^2-1)</code> is of that kind, and this is
   * where it is declined rather than after a search which cannot end well.
   *
   * @return the least common multiple of the denominators of the logarithms' coefficients, which is
   *         what turns the fractional powers their exponential leaves into whole ones
   */
  /** <code>expr</code> with a common factor of its numerator and denominator taken out. */
  private static IExpr cancel(IExpr expr, EvalEngine engine) {
    IExpr cancelled = engine.evaluate(F.Cancel(expr));
    return cancelled.isPresent() ? cancelled : engine.evaluate(expr);
  }

  private static long gcd(long a, long b) {
    while (b != 0) {
      long r = a % b;
      a = b;
      b = r;
    }
    return a;
  }

  private static int exponentiationPower(IExpr gyExpr, IExpr y) {
    boolean anyLogarithm = false;
    long power = 1;
    IAST terms = gyExpr.isPlus() ? (IAST) gyExpr : F.Plus(gyExpr);
    for (int i = 1; i <= terms.argSize(); i++) {
      IExpr term = terms.get(i);
      if (term.isFree(y, true)) {
        continue;
      }
      IExpr carried = term;
      IExpr coefficient = F.C1;
      if (term.isTimes()) {
        IAST factors = (IAST) term;
        IASTAppendable carrying = F.TimesAlloc(factors.argSize());
        IASTAppendable coefficients = F.TimesAlloc(factors.argSize());
        for (int k = 1; k <= factors.argSize(); k++) {
          if (factors.get(k).isFree(y, true)) {
            coefficients.append(factors.get(k));
          } else {
            carrying.append(factors.get(k));
          }
        }
        carried = carrying.oneIdentity1();
        coefficient = coefficients.oneIdentity1();
      }
      if (carried.equals(y)) {
        continue;
      }
      if (!carried.isLog() || !carried.first().isFree(S.Log, true)) {
        return 0;
      }
      anyLogarithm = true;
      if (coefficient.isRational()) {
        long denominator = ((IRational) coefficient).denominator().toLongDefault();
        if (denominator < 1) {
          return 0;
        }
        power = power / gcd(power, denominator) * denominator;
        if (power > MAX_EXPONENTIATION_POWER) {
          return 0;
        }
      } else if (!coefficient.isInteger()) {
        return 0;
      }
    }
    return anyLogarithm ? (int) power : 0;
  }

  /**
   * The constant of separation which the condition <code>y(x0) == y0</code> names.  /**
   * The constant of separation which the condition <code>y(x0) == y0</code> names.
   *
   * <p>
   * The separated relation is <code>G(y) == F(x) + C</code>, so <code>C</code> is
   * <code>G(y0) - F(x0)</code>: it enters linearly, and naming it is an evaluation rather than
   * something to solve for.
   *
   * @return {@link F#NIL} when the condition names no usable constant, which is what a base point
   *         the antiderivative does not reach gives
   */
  private static IExpr separationConstant(EvalEngine engine, IExpr gyExpr, IExpr fxIntegral,
      IExpr x, IExpr y, IExpr[] point) {
    IExpr atValue = engine.evaluate(F.subst(gyExpr, y, point[1]));
    IExpr atPoint = engine.evaluate(F.subst(fxIntegral, x, point[0]));
    IExpr constant = engine.evaluate(F.Subtract(atValue, atPoint));
    if (constant.isNIL() || !constant.isFree(x) || !constant.isFree(y, true)
        || !constant.isSpecialsFree() || !DSolveContext.isUsable(constant)) {
      return F.NIL;
    }
    return constant;
  }

  /**
   * The branches of <code>result</code> which are solutions: free of the unknown, and of a shape
   * the rest of the cascade can use.
   *
   * <p>
   * An inversion which answers with several branches is answering with several functions, and one
   * of them being unusable does not make the others so. What comes back is a single branch as
   * itself and several as a list, which is what every caller of the separable solver reads.
   *
   * @return {@link F#NIL} if no branch is left
   */
  private static IExpr usableBranches(EvalEngine engine, IExpr result, IExpr y) {
    if (result.isNIL()) {
      return F.NIL;
    }
    IAST branches = DSolveUtil.stripConditionalExpression(result).makeList();
    IASTAppendable kept = F.ListAlloc(branches.argSize());
    for (int i = 1; i <= branches.argSize(); i++) {
      IExpr branch = engine.evaluate(DSolveUtil.stripConditionalExpression(branches.get(i)));
      if (branch.isPresent() && branch.isFree(y, true) && DSolveContext.isUsable(branch)) {
        kept.append(branch);
      }
    }
    if (kept.argSize() == 0) {
      return F.NIL;
    }
    return kept.argSize() == 1 ? kept.arg1() : kept;
  }

  /**
   * Solves the separated equation for <code>y</code> where {@link Eliminate#extractVariable} could
   * not, which is what an antiderivative that mixes a logarithm and a root needs.
   *
   * @return the branches <code>Solve</code> answers with, a list where there is more than one, or
   *         {@link F#NIL}
   */
  private static IExpr solveSeparatedEquation(EvalEngine engine, IExpr yEquation, IExpr y) {
    if (yEquation.isPlus() && DSolveUtil.hasRadical(yEquation)
        && !LinearODEForm.isRationalIn(yEquation, y, engine)) {
      // A sum with a fractional power of y in it is the one shape whose Solve does not come back;
      // the time limit below is only the second line of defence.
      return F.NIL;
    }
    IExpr solutions;
    try {
      solutions = engine.evaluate(F.TimeConstrained(F.Solve(F.Equal(yEquation, F.C0), y),
          F.ZZ(MachineProfile.seconds(SOLVE_SEPARATED_SECONDS)), S.$Aborted));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
    if (solutions.isNIL() || solutions.equals(S.$Aborted)) {
      return F.NIL;
    }
    IAST roots = DSolveUtil.extractSolveResults(solutions);
    if (roots.argSize() < 1) {
      return F.NIL;
    }
    return usableBranches(engine, roots, y);
  }

  static IExpr odeSolve(EvalEngine engine, IExpr w, IExpr x, IExpr y, IExpr C_1) {
    return odeSolve(engine, w, x, y, C_1, null);
  }

  /**
   * The cascade of methods for <code>M + N*y' == 0</code>, with the option of naming the constant
   * of a separable equation from a condition before the relation is inverted.
   *
   * @param point <code>{x0, y0}</code> of a condition <code>y(x0) == y0</code>, or
   *        <code>null</code>. Only the separable method is offered it: the others do not write a
   *        relation this can be read off.
   */
  static IExpr odeSolve(EvalEngine engine, IExpr w, IExpr x, IExpr y, IExpr C_1, IExpr[] point) {
    IExpr[] p = odeTransform(engine, w, x, y);
    if (p != null) {
      IExpr m = p[0];
      IExpr n = p[1];
      // The methods below integrate and differentiate with respect to the unknown, and neither
      // Integrate nor D takes a function application for a variable: they answer nothing at all
      // for y(x), which left every one of them except odeHomogeneous -- which substitutes a symbol
      // of its own -- unable to solve anything it was given. So the unknown is carried as a symbol
      // here, and what comes back is an expression in x which must be free of it.
      IExpr yVar = y;
      if (!y.isSymbol()) {
        yVar = F.Dummy("y");
        m = engine.evaluate(F.subst(m, y, yVar));
        n = engine.evaluate(F.subst(n, y, yVar));
        if (!m.isFree(y, true) || !n.isFree(y, true)) {
          return F.NIL;
        }
      }

      // Try separable first
      IExpr[] separationPoint = point;
      if (separationPoint != null && !y.isSymbol()) {
        // The unknown is carried as the dummy symbol above, and the value the condition prescribes
        // has to be free of the unknown for the substitution below to mean anything.
        if (!separationPoint[1].isFree(y, true)) {
          separationPoint = null;
        }
      }
      IExpr f = odeSeparable(engine, m, n, x, yVar, C_1, separationPoint);
      if (isSolvedFor(f, yVar)) {
        return f;
      }

      f = odeExact(engine, m, n, x, yVar, C_1);
      if (isSolvedFor(f, yVar)) {
        return f;
      }

      f = odeIntegratingFactor(engine, m, n, x, yVar, C_1);
      if (isSolvedFor(f, yVar)) {
        return f;
      }

      f = odeHomogeneous(engine, m, n, x, yVar, C_1);
      if (isSolvedFor(f, yVar)) {
        return f;
      }
    }
    return F.NIL;
  }

  /** Whether one of the methods of {@link #odeSolve} has answered with the unknown eliminated. */
  private static boolean isSolvedFor(IExpr solution, IExpr yVar) {
    return solution.isPresent() && solution.isFree(yVar, true);
  }

  static IExpr[] odeTransform(EvalEngine engine, IExpr w, IExpr x, IExpr y) {
    // Convert equation to an expression (lhs - rhs)
    IExpr expr = w;
    if (w.isEqual()) {
      expr = S.Subtract.of(engine, w.first(), w.second());
    }

    IExpr v = S.Together.of(engine, expr);
    IExpr numerator = S.Numerator.of(engine, v);
    IExpr dyx = S.D.of(engine, y, x);

    // This splitting reads the equation as M + N*y' == 0, which only accounts for all of it when
    // the equation has no higher derivative and contains y' in the first power only. Without that
    // check x*y''(x) + 2*y'(x) - x*y(x) == Sin(x) was read as if its second derivative were part
    // of M, and an answer was built from the wrong equation.
    IExpr head = y.head();
    if (LinearODEForm.highestDerivativeOrder(numerator, head, x) != 1) {
      return null;
    }
    IExpr m = S.Coefficient.of(engine, numerator, dyx, F.C0);
    IExpr n = S.Coefficient.of(engine, numerator, dyx, F.C1);

    // Guard against degenerate input with no derivative term
    if (n.isZero()) {
      return null;
    }
    IExpr remainder =
        engine.evaluate(F.ExpandAll(F.Subtract(numerator, F.Plus(m, F.Times(n, dyx)))));
    if (!remainder.isZero() || !m.isFree(dyx, true) || !n.isFree(dyx, true)) {
      return null;
    }

    return new IExpr[] {m, n};
  }

  /**
   * Applies multiple boundary/initial conditions to a general ODE solution by substituting the
   * solution into each boundary equation and solving for all integration constants simultaneously.
   * Handles both value conditions ({@code y(x0)==v0}) and derivative conditions
   * ({@code y'(x0)==v0}, {@code y''(x0)==v0}, etc.).
   *
   * <p>
   * This mirrors the approach used by {@link #applySystemBCs} for system ODEs.
   *
   * @param root the general solution expression (e.g., {@code C(1)*Cos(x) + C(2)*Sin(x)})
   * @param uFunction1Arg the target function application (e.g., {@code y(x)})
   * @param xVar the independent variable symbol
   * @param boundaryConditions list of boundary equations in subtracted form (each implicitly == 0)
   * @param engine the evaluation engine
   * @return the particular solution with constants determined, or {@code F.NIL} if the boundary
   *         conditions cannot be satisfied
   */
  /**
   * The condition read as a limit, for a solution which does not reach the point it is given at.
   *
   * <p>
   * A basis can be singular where the condition is: <code>t*x''(t) + (t-2)*x'(t) + x(t) == 0</code>
   * has a solution written with <code>ExpIntegralEi(t)</code>, and putting <code>t == 0</code> into
   * it gives <code>Indeterminate</code> from <code>0*(-Infinity)</code>, not because the condition
   * cannot be met but because that is not how to ask. The limit there is <code>-C(2)/3</code>, which
   * says <code>C(2) == 0</code>, and what is left solves the equation exactly.
   *
   * @return the one condition rewritten, or <code>null</code> when the limit does not exist, cannot
   *         be found, or the conditions are not a single prescribed value
   */
  private static IASTAppendable limitedConditions(IExpr root, IAST uFunction1Arg, IExpr xVar,
      IAST boundaryConditions, EvalEngine engine) {
    IExpr[] point = valuePoint(boundaryConditions, uFunction1Arg.head(), xVar, engine);
    if (point == null) {
      return null;
    }
    IExpr limit;
    try {
      limit = engine.evaluate(F.TimeConstrained(F.Limit(root, F.Rule(xVar, point[0])),
          F.ZZ(MachineProfile.seconds(LIMIT_AT_CONDITION_SECONDS)), S.$Aborted));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return null;
    }
    if (limit.isNIL() || limit.equals(S.$Aborted) || limit.isIndeterminate()
        || !limit.isFree(e -> e == S.Indeterminate || e.isDirectedInfinity(), true)
        || !DSolveContext.isUsable(limit)) {
      return null;
    }
    IASTAppendable conditions = F.ListAlloc(1);
    conditions.append(engine.evaluate(F.Subtract(limit, point[1])));
    return conditions;
  }

  /** Each condition with the candidate solution put in place of the unknown function. */  /** Each condition with the candidate solution put in place of the unknown function. */
  private static IASTAppendable evaluatedConditions(IExpr root, IAST uFunction1Arg, IExpr xVar,
      IAST boundaryConditions, EvalEngine engine) {
    IAST headRules =
        F.List(F.Rule(uFunction1Arg.head(), F.Function(F.List(xVar), root)));
    IASTAppendable evaluatedBCs = F.ListAlloc(boundaryConditions.argSize());
    for (int k = 1; k <= boundaryConditions.argSize(); k++) {
      IExpr evaluatedBC = engine.evaluate(F.subst(boundaryConditions.get(k), headRules));
      evaluatedBC = engine.evaluate(DSolveUtil.clearCorruptedIntegrals(evaluatedBC));
      evaluatedBCs.append(evaluatedBC);
    }
    return evaluatedBCs;
  }

  /**
   * Whether a branch which was fitted before it was written down does meet the conditions.
   *
   * <p>
   * There is no constant left to solve for here, so this is a check rather than a fit. It is not
   * the same check as the one {@link #applyUnaryBCs} makes on a branch with no constant in it,
   * because a branch which came out of solving a cubic is a difference of nested radicals whose
   * value is zero and whose form does not say so. What is asked instead is the measure the
   * differential equation itself is checked with: zero to rounding, against the size of the terms.
   * A branch which misses the condition by a whole unit -- the other two roots of that cubic are
   * <code>2</code> and <code>-2</code> where the condition asks for <code>0</code> -- is still
   * refused.
   */
  private static boolean refutedBy(IExpr root, IAST uFunction1Arg, IExpr xVar,
      IAST boundaryConditions, EvalEngine engine) {
    IAST evaluatedBCs = evaluatedConditions(root, uFunction1Arg, xVar, boundaryConditions, engine);
    for (int k = 1; k <= evaluatedBCs.argSize(); k++) {
      IExpr bc = evaluatedBCs.get(k);
      IExpr residual = bc.isEqual() ? engine.evaluate(F.Subtract(bc.first(), bc.second())) : bc;
      if (residual.isZero() || (bc.isEqual() && engine.evaluate(bc).isTrue())) {
        continue;
      }
      if (DSolveVerify.refutesCondition(residual, engine)) {
        return true;
      }
    }
    return false;
  }

  private static boolean meetsConditions(IExpr root, IAST uFunction1Arg, IExpr xVar,
      IAST boundaryConditions, EvalEngine engine) {
    IAST evaluatedBCs =
        evaluatedConditions(root, uFunction1Arg, xVar, boundaryConditions, engine);
    for (int k = 1; k <= evaluatedBCs.argSize(); k++) {
      IExpr bc = evaluatedBCs.get(k);
      IExpr residual = bc.isEqual() ? engine.evaluate(F.Subtract(bc.first(), bc.second())) : bc;
      if (residual.isZero() || (bc.isEqual() && engine.evaluate(bc).isTrue())) {
        continue;
      }
      if (!DSolveVerify.acceptCondition(residual, engine)) {
        return false;
      }
    }
    return true;
  }

  /**
   * The point and the value of a single condition <code>y(x0) == y0</code>, or <code>null</code>.
   *
   * <p>
   * A condition on a derivative names no point of the solution itself, and two conditions on a
   * first order equation are one too many, so neither is answered here.
   */
  private static IExpr[] valuePoint(IAST conditions, IExpr head, IExpr xVar, EvalEngine engine) {
    if (conditions.argSize() != 1 || !head.isSymbol()) {
      return null;
    }
    IExpr point = conditionPoint(conditions, head, xVar, engine);
    if (point.isNIL() || !point.isFree(xVar)) {
      return null;
    }
    IExpr value = conditionValue(conditions, head, 0, point, engine);
    if (value.isNIL() || !value.isFree(xVar) || !value.isFree(head, true)) {
      return null;
    }
    return new IExpr[] {point, value};
  }

  /**
   * The branch fitted to the conditions, the first of the candidates {@link #fitCandidates} finds.
   */
  static IExpr applyUnaryBCs(IExpr root, IAST uFunction1Arg, IExpr xVar, IAST boundaryConditions,
      EvalEngine engine) {
    IAST candidates = fitCandidates(root, uFunction1Arg, xVar, boundaryConditions, engine);
    return candidates.argSize() > 0 ? candidates.arg1() : F.NIL;
  }

  /**
   * Every way of fitting <code>root</code> to the conditions, one for each solution
   * <code>Solve</code> gives for the constants.
   *
   * <p>
   * All of them, because a condition can have more than one solution for the constant and only
   * some of those solve the equation. <code>y'(x) - 2*y(x) == 2*Sqrt(y(x))</code> has the general
   * solution <code>(C(1)*E^x - 1)^2</code>, and <code>y(0) == 1</code> asks for
   * <code>(C(1) - 1)^2 == 1</code>, which is <code>C(1) == 0</code> or <code>C(1) == 2</code>. The
   * first gives <code>y == 1</code>, which meets the condition and is not a solution at all -- the
   * squaring that wrote the general solution introduced it -- and taking the first root is what
   * returned it. The caller puts each candidate back into the equation and keeps one which solves
   * it.
   *
   * @return the candidates, empty when the conditions cannot be met
   */
  static IAST fitCandidates(IExpr root, IAST uFunction1Arg, IExpr xVar, IAST boundaryConditions,
      EvalEngine engine) {
    IASTAppendable evaluatedBCs =
        evaluatedConditions(root, uFunction1Arg, xVar, boundaryConditions, engine);
    if (!evaluatedBCs.isFree(e -> e == S.Indeterminate || e.isDirectedInfinity(), true)) {
      IASTAppendable atLimit =
          limitedConditions(root, uFunction1Arg, xVar, boundaryConditions, engine);
      if (atLimit != null) {
        evaluatedBCs = atLimit;
      }
    }

    // Use the guaranteed recursive constant extractor
    IASTAppendable cVars = F.ListAlloc();
    DSolveUtil.extractCVars(evaluatedBCs, cVars);

    if (cVars.argSize() == 0) {
      for (int k = 1; k <= evaluatedBCs.argSize(); k++) {
        IExpr bc = evaluatedBCs.get(k);
        if (bc.isEqual()) {
          if (!engine.evaluate(bc).isTrue())
            return F.CEmptyList;
        } else if (!bc.isZero()) {
          return F.CEmptyList;
        }
      }
      return F.list(root);
    }

    // Shield existing Equal expressions from being double-wrapped
    IAST evaluatedBCsEqualZero = evaluatedBCs.map(t -> {
      if (t.isEqual())
        return t;
      return F.Equal(t, F.C0);
    });

    final boolean quietMode = engine.isQuietMode();
    IExpr cSols;
    try {
      // Solving a boundary condition for the integration constants inverts the general solution, so
      // for a root like `-Sqrt(x^2-C(1))` this reports an `InverseFunction` warning. The caller
      // hands every branch of the general solution to this method and keeps the ones which can be
      // solved, so a branch failing here is a step of the algorithm and not something to report.
      engine.setQuietMode(true);
      cSols = engine.evaluate(F.Solve(evaluatedBCsEqualZero, cVars));
      if (!cSols.isList() || ((IAST) cSols).argSize() == 0) {
        // More conditions than constants is not by itself a contradiction: a condition may already
        // have been used to determine a constant earlier, and Solve declines a system it has more
        // equations than unknowns for. Determining the constants from as many conditions as there
        // are, and then checking that the remaining conditions hold, solves those cases and still
        // rejects the ones which really are contradictory.
        cSols = solveFromSubset(evaluatedBCsEqualZero, cVars, engine);
      }
    } finally {
      engine.setQuietMode(quietMode);
    }
    IASTAppendable candidates = F.ListAlloc();
    if (cSols.isList()) {
      IAST solutions = (IAST) cSols;
      for (int i = 1; i <= solutions.argSize(); i++) {
        if (!solutions.get(i).isList()) {
          continue;
        }
        // Stripped like every other result Solve hands back here: inverting a periodic function
        // writes a whole number into the answer to choose a branch, and every value of it names
        // the same solution, so the principal one is taken rather than left standing where it
        // reads as a constant the conditions failed to determine.
        IAST cSol = (IAST) DSolveUtil.stripConditionalExpression(solutions.get(i));
        cSol = completeSolution(cSol, evaluatedBCsEqualZero, cVars, engine);
        IExpr fitted = DSolveUtil.togetherSolution(engine.evaluate(F.subst(root, cSol)), engine);
        if (!isFitted(fitted, cVars)) {
          continue;
        }
        // Solving a condition for the constant answers formally, and formally is not always. The
        // minus branch of a solution written with a radical can be fitted to y(a) == b for every
        // b and meets it only for one sign of b, so the condition goes back in and is asked again.
        if (refutedBy(fitted, uFunction1Arg, xVar, boundaryConditions, engine)) {
          continue;
        }
        if (!candidates.contains(fitted)) {
          candidates.append(fitted);
        }
      }
    }
    return candidates;
  }

  /**
   * The rules of <code>cSol</code>, together with the ones for any constant it left undetermined.
   *
   * <p>
   * A constant which one of the conditions fixes on its own -- <code>y(0) == 0</code> makes
   * <code>C(1) == 0</code> outright -- can be missing from what <code>Solve</code> answers with,
   * and a general solution which still carries it is then refused as unfitted although the
   * conditions do determine it. What the first pass did find is put into the conditions and the
   * rest are asked about again.
   *
   * @return the rules to substitute, unchanged when nothing was missing
   */
  private static IAST completeSolution(IAST cSol, IAST conditions, IAST cVars, EvalEngine engine) {
    IASTAppendable missing = F.ListAlloc(cVars.argSize());
    for (int i = 1; i <= cVars.argSize(); i++) {
      IExpr cVar = cVars.get(i);
      if (cSol.isFree(x -> x.isRule() && x.first().equals(cVar), true)) {
        missing.append(cVar);
      }
    }
    if (missing.argSize() == 0) {
      return cSol;
    }
    IExpr rest = engine.evaluate(F.subst(conditions, cSol));
    IExpr more = engine.evaluate(F.Solve(rest, missing));
    if (!more.isList() || ((IAST) more).argSize() == 0 || !more.first().isList()) {
      return cSol;
    }
    IASTAppendable completed = cSol.copyAppendable();
    completed.appendArgs((IAST) DSolveUtil.stripConditionalExpression(more.first()));
    return completed;
  }

  /**
   * Whether the conditions really determined the constants.
   *
   * <p>
   * Solving for them can come back with something which is not a value -- <code>Undefined</code>
   * where a condition was imposed at a point the general solution does not reach, or a constant
   * left standing -- and putting that into the answer produces an expression which is not a
   * solution of anything. The branch is refused instead, which is what the caller already does for
   * a branch the conditions cannot be solved for at all.
   */
  private static boolean isFitted(IExpr fitted, IAST cVars) {
    if (fitted.isNIL() || fitted.isIndeterminate() || fitted.isDirectedInfinity()) {
      return false;
    }
    for (int i = 1; i <= cVars.argSize(); i++) {
      if (!fitted.isFree(cVars.get(i), true)) {
        return false;
      }
    }
    return fitted.isFree(x -> x == S.Undefined || x == S.Indeterminate
        || x == S.ComplexInfinity || x.isAST(S.ConditionalExpression), true);
  }

  /**
   * Determines the constants from the first <code>cVars.argSize()</code> conditions and keeps the
   * result only if the conditions which were left over hold for it.
   *
   * @return a <code>Solve</code> style result, or {@link F#NIL}
   */
  private static IExpr solveFromSubset(IAST conditions, IAST cVars, EvalEngine engine) {
    int wanted = cVars.argSize();
    if (conditions.argSize() <= wanted) {
      return F.NIL;
    }
    for (int start = 1; start + wanted - 1 <= conditions.argSize(); start++) {
      IASTAppendable subset = F.ListAlloc(wanted);
      for (int i = 0; i < wanted; i++) {
        subset.append(conditions.get(start + i));
      }
      IExpr solutions = engine.evaluate(F.Solve(subset, cVars));
      if (!solutions.isList() || ((IAST) solutions).argSize() == 0) {
        continue;
      }
      for (int candidate = 1; candidate <= ((IAST) solutions).argSize(); candidate++) {
        IExpr candidateRules = ((IAST) solutions).get(candidate);
        if (!candidateRules.isList()) {
          continue;
        }
        IAST rules = (IAST) candidateRules;
        boolean consistent = true;
        for (int i = 1; i <= conditions.argSize(); i++) {
          if (i >= start && i < start + wanted) {
            continue;
          }
          IExpr checked = engine.evaluate(F.Simplify(F.subst(conditions.get(i), rules)));
          if (checked.isFalse()) {
            consistent = false;
            break;
          }
        }
        if (consistent) {
          return F.list(rules);
        }
      }
    }
    return F.NIL;
  }

  /**
   * Recursively constructs the constant-coefficient operator for the t-domain transformed
   * Euler-Cauchy equation. Maps x^k * D(y, {x, k}) -> D_t(Op(k-1)) - (k-1)*Op(k-1)
   */
  static IExpr getEulerCauchyOperator(int k, IExpr uFunc, IExpr tVar, EvalEngine engine) {
    if (k == 0) {
      return uFunc;
    }
    IExpr prev = getEulerCauchyOperator(k - 1, uFunc, tVar, engine);
    IExpr dPrev = engine.evaluate(F.D(prev, tVar));
    return S.Subtract.of(engine, dPrev, F.Times(F.ZZ(k - 1), prev));
  }

  static IExpr linearODE(IExpr coefficient1, IExpr coefficient0, IExpr xVar, IExpr C_1,
      EvalEngine engine) {
    IExpr pExponent = DSolveContext.integrate(coefficient1, xVar, engine);
    if (pExponent.isNIL()) {
      return F.NIL;
    }
    IExpr pInt = engine.evaluate(F.Exp(pExponent));

    if (coefficient0.isZero()) {
      return F.Divide(C_1, pInt).eval(engine);
    } else {
      IExpr qIntegral = DSolveContext.integrate(F.Times(F.CN1, coefficient0, pInt), xVar, engine);
      if (qIntegral.isNIL()) {
        return F.NIL;
      }
      IExpr qInt = engine.evaluate(F.Plus(C_1, F.Expand(qIntegral)));
      return F.Expand(F.Divide(qInt, pInt)).eval(engine);
    }
  }

  /**
   * Solves Euler-Cauchy differential equations of the form: a_n * x^n * y^(n) + ... + a_1 * x * y'
   * + a_0 * y = f(x)
   */
  static IExpr solveEulerCauchyODE(LinearODEForm lf, IExpr yFunction, IExpr xVar, IExpr c_n,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    int n = lf.order;
    if (n < 1 || lf.a[n].isZero()) {
      return F.NIL;
    }

    // The centre is read off the leading coefficient: c[n](x) == a[n]*(x-b)^n gives
    // n*c[n]/c[n]' == x-b. A leading coefficient which does not depend on x has no centre and
    // belongs to the constant coefficient solver.
    IExpr leadingDerivative = engine.evaluate(F.D(lf.a[n], xVar));
    if (leadingDerivative.isZero()) {
      return F.NIL;
    }
    IExpr centre = engine.evaluate(F.Cancel(
        F.Together(F.Subtract(xVar, F.Divide(F.Times(F.ZZ(n), lf.a[n]), leadingDerivative)))));
    if (!centre.isFree(xVar)) {
      return F.NIL;
    }
    IExpr shifted = engine.evaluate(F.Subtract(xVar, centre));

    IExpr[] a = new IExpr[n + 1];
    for (int k = 0; k <= n; k++) {
      if (lf.a[k].isZero()) {
        a[k] = F.C0;
        continue;
      }
      a[k] = engine.evaluate(F.Cancel(F.Together(F.Divide(lf.a[k], F.Power(shifted, F.ZZ(k))))));
      if (!a[k].isFree(xVar)) {
        // Not of the Cauchy-Euler shape about this centre.
        return F.NIL;
      }
    }
    if (a[n].isZero()) {
      return F.NIL;
    }

    // Substituting x - b == E^t turns (x-b)^k*y^(k) into a product of the operators t d/dt, so the
    // equation becomes one with constant coefficients.
    IExpr tVar = F.Dummy("t");
    IExpr uDummy = F.Dummy("u");
    IExpr uFunc = F.unaryAST1(uDummy, tVar);

    IASTAppendable newLhsTerms = F.PlusAlloc(n + 2);
    for (int k = 0; k <= n; k++) {
      if (!a[k].isZero()) {
        newLhsTerms.append(F.Times(a[k], getEulerCauchyOperator(k, uFunc, tVar, engine)));
      }
    }
    if (!lf.g.isZero()) {
      newLhsTerms
          .append(engine.evaluate(F.Negate(F.subst(lf.g, xVar, F.Plus(centre, F.Exp(tVar))))));
    }

    IExpr newLhs = engine.evaluate(newLhsTerms);
    LinearODEForm tForm = LinearODEForm.extract(newLhs, uFunc, tVar, engine);
    if (tForm == null || tForm.order != n) {
      return F.NIL;
    }
    IExpr tSol = solveLinearConstantCoefficients(tForm, tVar, c_n, ctx);
    if (tSol.isNIL()) {
      // Variation of parameters does not close every integral the transformed forcing leads to.
      // The transformed equation is an ordinary one with constant coefficients, so the rest of the
      // cascade, and the Laplace transform at the end of it, can be asked for it instead.
      IAST branches = solveSubODE(F.Equal(newLhs, F.C0), tVar, uFunc, c_n, ctx);
      if (branches.argSize() != 1) {
        return F.NIL;
      }
      tSol = branches.arg1();
    }
    return engine.evaluate(F.subst(tSol, tVar, F.Log(shifted)));
  }

  /**
   * Solves linear ODEs using the Laplace Transform method. Manually constructs the s-domain
   * algebraic equation.
   */
  static IExpr solveLaplaceODE(IExpr lhs, IExpr yFunction, IExpr xVar, int n, IExpr c_n,
      EvalEngine engine) {
    if (n < 1)
      return F.NIL;

    IExpr s = F.Dummy("s");
    IExpr Y = F.Dummy("Y");
    IExpr head = yFunction.head();

    // 1. Extract constant coefficients
    IExpr[] coeffs = new IExpr[n + 1];
    IExpr rest = lhs;
    for (int k = n; k >= 1; k--) {
      IExpr dyx = engine.evaluate(F.D(yFunction, F.List(xVar, F.ZZ(k))));
      IExpr c = engine.evaluate(F.Coefficient(rest, dyx));
      if (!c.isFree(xVar) || !c.isFree(head))
        return F.NIL;
      coeffs[k] = c;
      rest = engine.evaluate(F.Subtract(rest, F.Times(c, dyx)));
    }
    IExpr c0 = engine.evaluate(F.Coefficient(rest, yFunction));
    if (!c0.isFree(xVar) || !c0.isFree(head))
      return F.NIL;
    coeffs[0] = c0;

    IExpr freeTerm = engine.evaluate(F.Subtract(rest, F.Times(c0, yFunction)));

    // 2. Map Initial Conditions
    IExpr[] y0 = new IExpr[n];
    for (int k = 0; k < n; k++) {
      y0[k] = (k == 0) ? c_n : F.C(engine.incConstantCounter());
    }

    // 3. Construct s-domain Equation
    IASTAppendable sEq = F.PlusAlloc();
    for (int k = 0; k <= n; k++) {
      if (coeffs[k].isZero())
        continue;

      IASTAppendable L_yk = F.PlusAlloc();
      IExpr sPowY = (k == 0) ? Y : F.Times(F.Power(s, F.ZZ(k)), Y);
      L_yk.append(sPowY);

      for (int i = 0; i < k; i++) {
        IExpr sPow = (k - 1 - i == 0) ? F.C1 : F.Power(s, F.ZZ(k - 1 - i));
        L_yk.append(F.Times(F.CN1, sPow, y0[i]));
      }
      sEq.append(F.Times(coeffs[k], L_yk));
    }

    // 4. Transform Forcing Function (Now handled natively by rules!)
    if (!freeTerm.isZero()) {
      IExpr transFree = engine.evaluate(F.LaplaceTransform(freeTerm, xVar, s));
      if (!transFree.isFree(S.LaplaceTransform))
        return F.NIL;
      sEq.append(transFree);
    }

    IExpr algebraicEq = engine.evaluate(sEq);

    // 5. Solve for Y
    IExpr ySolList = engine.evaluate(F.Solve(F.Equal(algebraicEq, F.C0), Y));
    if (ySolList.isList() && ((IAST) ySolList).argSize() > 0) {
      IAST firstSol = (IAST) ((IAST) ySolList).arg1();
      IExpr y_s = firstSol.isList() ? firstSol.arg1().second() : firstSol.second();

      if (y_s.isPresent()) {
        // 6. Inverse Transform (Now handled natively by rules!)
        IExpr inverse = engine.evaluate(F.InverseLaplaceTransform(y_s, s, xVar));
        if (inverse.isFree(S.InverseLaplaceTransform)) {
          return engine.evaluate(F.Simplify(inverse));
        }
      }
    }
    return F.NIL;
  }

  /**
   * Applies Reduction of Order for Non-Linear ODEs of order n >= 2. Case 1: Missing dependent
   * variable y(x). Substitutes v(x) = y'(x). Case 2: Missing independent variable x (for n=2).
   * Substitutes y' = v(y) and y'' = v(y)*v'(y).
   */
  static IExpr solveReductionOfOrderODE(IExpr lhs, IExpr yFunction, IExpr xVar, int n, IExpr c_n,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (n < 2)
      return F.NIL;

    // Case 1: Missing dependent variable y(x)
    IExpr p0 = F.Dummy("p0");
    IExpr testExpr = engine.evaluate(F.subst(lhs, yFunction, p0));

    if (testExpr.isFree(p0)) {
      IExpr vSym = F.Dummy("v");
      IExpr vFunc = F.unaryAST1(vSym, xVar);
      IASTAppendable rules = F.ListAlloc(n);

      for (int k = 1; k <= n; k++) {
        IExpr dky = engine.evaluate(F.D(yFunction, F.List(xVar, F.ZZ(k))));
        IExpr dkv = (k == 1) ? vFunc : engine.evaluate(F.D(vFunc, F.List(xVar, F.ZZ(k - 1))));
        rules.append(F.Rule(dky, dkv));
      }
      IExpr newLhs = engine.evaluate(F.subst(lhs, rules));

      // Normalize the ODE so the coefficient of the highest derivative is exactly 1
      int vOrder = n - 1;
      IExpr highestV = engine.evaluate(F.D(vFunc, F.List(xVar, F.ZZ(vOrder))));
      IExpr coeffHighest = engine.evaluate(F.Coefficient(newLhs, highestV));
      if (!coeffHighest.isZero() && !coeffHighest.isOne()) {
        newLhs = engine.evaluate(F.Simplify(F.Divide(newLhs, coeffHighest)));
      }

      IExpr vSols = solveSingleODE(F.Equal(newLhs, F.C0), xVar, F.List(vFunc), c_n, ctx);
      if (vSols.isNIL()) {
        vSols = odeSolve(engine, F.Equal(newLhs, F.C0), xVar, vFunc, c_n);
      }

      IAST roots = F.NIL;
      if (vSols.isList()) {
        roots = (IAST) vSols;
      } else if (vSols.isPresent()) {
        roots = F.List(vSols);
      }

      if (roots.isPresent() && roots.argSize() > 0) {
        IASTAppendable resultList = F.ListAlloc();
        IExpr C_2 = F.C(engine.incConstantCounter());
        for (int r = 1; r <= roots.argSize(); r++) {
          IExpr vSol = roots.get(r);
          IExpr vIntegral = DSolveContext.integrate(vSol, xVar, engine);
          if (vIntegral.isNIL()) {
            continue;
          }
          IExpr ySol = engine.evaluate(F.Plus(vIntegral, C_2));
          resultList.append(ySol);
        }
        return resultList.argSize() == 1 ? resultList.arg1() : resultList;
      }
    }

    // Case 2: Missing independent variable x (Specifically for 2nd order ODEs)
    if (n == 2) {
      IExpr dyx = engine.evaluate(F.D(yFunction, xVar));
      IExpr d2yx = engine.evaluate(F.D(yFunction, F.List(xVar, F.C2)));

      IExpr p1 = F.Dummy("p1");
      IExpr p2 = F.Dummy("p2");
      IAST replaceRules2 = F.List(F.Rule(d2yx, p2), F.Rule(dyx, p1), F.Rule(yFunction, p0));
      IExpr testExprX = engine.evaluate(F.subst(lhs, replaceRules2));

      if (testExprX.isFree(xVar)) {
        IExpr vSym = F.Dummy("v");
        IExpr yDummy = F.Dummy("Y");
        IExpr vFunc = F.unaryAST1(vSym, yDummy);
        IExpr vPrime = engine.evaluate(F.D(vFunc, yDummy));

        IExpr newLhs = engine.evaluate(F.subst(testExprX,
            F.List(F.Rule(p2, F.Times(vFunc, vPrime)), F.Rule(p1, vFunc), F.Rule(p0, yDummy))));

        // Normalize the ODE so the coefficient of v'(Y) is exactly 1
        IExpr coeffVPrime = engine.evaluate(F.Coefficient(newLhs, vPrime));
        if (!coeffVPrime.isZero() && !coeffVPrime.isOne()) {
          newLhs = engine.evaluate(F.Simplify(F.Divide(newLhs, coeffVPrime)));
        }

        IExpr vSols = solveSingleODE(F.Equal(newLhs, F.C0), yDummy, F.List(vFunc), c_n, ctx);
        if (vSols.isNIL()) {
          vSols = odeSolve(engine, F.Equal(newLhs, F.C0), yDummy, vFunc, c_n);
        }

        IAST roots = F.NIL;
        if (vSols.isList()) {
          roots = (IAST) vSols;
        } else if (vSols.isPresent()) {
          roots = F.List(vSols);
        }

        if (roots.isPresent() && roots.argSize() > 0) {
          IASTAppendable resultList = F.ListAlloc();
          IExpr C_2 = F.C(engine.incConstantCounter());

          for (int r = 1; r <= roots.argSize(); r++) {
            IExpr vSol = roots.get(r);
            // The first integral y'^2 == F(y) + C(1) is where an initial condition for y'(x0) can
            // still be used. Fitting the constant now often turns the second integration from an
            // elliptic one into an elementary one, which is how y''(x) == y(x)^3 with
            // y(0) == 5 and y'(0) == 25/Sqrt(2) becomes solvable at all.
            vSol = fitFirstIntegral(vSol, yDummy, yFunction.head(), xVar, c_n, ctx);
            if (vSol.isNIL()) {
              continue;
            }
            IExpr f_y = engine.evaluate(F.subst(vSol, yDummy, yFunction));

            // Formulate: y'(x) - f_y = 0. The coefficient of y' is naturally 1 here.
            IExpr firstOrderEq = F.Equal(S.Subtract.of(engine, dyx, f_y), F.C0);
            IExpr ySols = solveSingleODE(firstOrderEq, xVar, F.List(yFunction), C_2, ctx);
            if (ySols.isNIL()) {
              ySols = odeSolve(engine, firstOrderEq, xVar, yFunction, C_2);
            }

            if (ySols.isList()) {
              resultList.appendArgs((IAST) ySols);
            } else if (ySols.isPresent()) {
              resultList.append(ySols);
            }
          }
          if (resultList.argSize() > 0) {
            return resultList.argSize() == 1 ? resultList.arg1() : resultList;
          }
        }
      }
    }

    return F.NIL;
  }

  /**
   * A first order Riccati equation, solved through a particular solution, when nothing else in
   * the cascade has answered it.
   *
   * <p>
   * Once one solution <code>y_p</code> of <code>y' == a*y^2 + b*y + c</code> is known,
   * <code>y == y_p + 1/v</code> leaves <code>v' + (2*a*y_p + b)*v == -a</code>, which is linear. The
   * Riccati equations of the textbooks are set so that one can be guessed:
   * <code>y' == 1 + x - (2*x + 1)*y + x*y^2</code> has <code>y == 1</code> and
   * <code>x^3*y' == -2*x^4 + 2*x^2*y + 2*y^2</code> has <code>y == x^2</code>.
   *
   * <p>
   * This runs after the separable, exact and homogeneous methods have declined, not with the other
   * Riccati strategies before them. Those methods answer in their own forms, and a Riccati answer
   * <code>y_p + 1/v</code> built on a particular solution is often a less readable way of writing
   * the same function -- for <code>y' == -(1 + y^2)*(1 - 1/x^2)</code> the particular solution is
   * the constant <code>-I</code>, and the answer through it is a complex exponential where the
   * separable method gives <code>-Tan(1/x + x - C(1))</code>.
   *
   * @return {@link F#NIL} if the equation is not a Riccati equation or no particular solution of
   *         the kinds tried is found
   */
  static IExpr solveRiccatiThroughParticular(IExpr equation, IExpr xVar, IExpr yFunction,
      IExpr C_1, EvalEngine engine) {
    IExpr lhs = equation.isEqual() ? F.Subtract(equation.first(), equation.second()) : equation;
    lhs = engine.evaluate(F.ExpandAll(lhs));
    IExpr yPrime = engine.evaluate(F.D(yFunction, xVar));
    IExpr coeffDyx = engine.evaluate(F.Coefficient(lhs, yPrime, F.C1));
    IExpr rest = engine.evaluate(F.Coefficient(lhs, yPrime, F.C0));
    if (coeffDyx.isZero() || !coeffDyx.isFree(yPrime) || !rest.isFree(yPrime)
        || !coeffDyx.isFree(yFunction) || !engine
            .evaluate(F.ExpandAll(F.Subtract(lhs, F.Plus(F.Times(coeffDyx, yPrime), rest))))
            .isZero()) {
      return F.NIL;
    }
    IExpr expandedRest = engine.evaluate(F.ExpandAll(rest));
    IExpr q0 = engine.evaluate(F.Coefficient(expandedRest, yFunction, F.C0));
    IExpr q1 = engine.evaluate(F.Coefficient(expandedRest, yFunction, F.C1));
    IExpr q2 = engine.evaluate(F.Coefficient(expandedRest, yFunction, F.C2));
    IExpr remainder = engine.evaluate(F.ExpandAll(F.Subtract(expandedRest,
        F.Plus(q0, F.Times(q1, yFunction), F.Times(q2, F.Sqr(yFunction))))));
    if (!remainder.isZero() || q2.isZero() || q0.isZero() || !q0.isFree(yFunction)
        || !q1.isFree(yFunction) || !q2.isFree(yFunction)) {
      return F.NIL;
    }
    IExpr a = engine.evaluate(F.Divide(F.Negate(q2), coeffDyx));
    IExpr b = engine.evaluate(F.Divide(F.Negate(q1), coeffDyx));
    IExpr c = engine.evaluate(F.Divide(F.Negate(q0), coeffDyx));
    IExpr particular = riccatiParticular(a, b, c, xVar, engine);
    if (particular.isNIL()) {
      return F.NIL;
    }
    IExpr coefficient = engine.evaluate(F.Plus(F.Times(F.C2, a, particular), b));
    IExpr v = linearODE(coefficient, a, xVar, C_1, engine);
    if (v.isNIL() || v.isZero() || !DSolveContext.isUsable(v)) {
      return F.NIL;
    }
    return engine.evaluate(F.Plus(particular, F.Power(v, F.CN1)));
  }

  /** Whether <code>expr</code> is numerically zero at <code>xVar == point</code>. */
  private static boolean vanishesAt(IExpr expr, IExpr xVar, IExpr point, EvalEngine engine) {
    try {
      IExpr value = engine.evalN(F.subst(expr, xVar, point));
      return value.isNumber() && ((org.matheclipse.core.interfaces.INumber) value).abs()
          .evalf() < RICCATI_NUMERIC_TOLERANCE;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return false;
    }
  }

  /**
   * A particular solution of <code>y' == a*y^2 + b*y + c</code> of the form <code>k*m(x)</code>, for  /**
   * A particular solution of <code>y' == a*y^2 + b*y + c</code> of the form <code>k*m(x)</code>, for
   * a constant <code>k</code> and <code>m</code> one of a few simple functions, or {@link F#NIL}.
   *
   * <p>
   * The candidates for <code>m</code> are the low powers of <code>x</code>, the simplest
   * exponentials, and the square root of <code>c/a</code> or <code>-c/a</code>, which is where a
   * particular solution has to balance the two outer terms when the middle one is small. For each,
   * <code>k*m' - a*k^2*m^2 - b*k*m - c</code> is solved for <code>k</code> at one point, and a
   * <code>k</code> is only used if it makes that expression vanish identically.
   */
  private static IExpr riccatiParticular(IExpr a, IExpr b, IExpr c, IExpr xVar,
      EvalEngine engine) {
    IASTAppendable candidates = F.ListAlloc();
    candidates.append(F.C1);
    candidates.append(xVar);
    candidates.append(F.Sqr(xVar));
    candidates.append(F.Power(xVar, F.CN1));
    candidates.append(F.Power(xVar, F.CN2));
    candidates.append(F.Exp(xVar));
    candidates.append(F.Exp(F.Negate(xVar)));
    for (IExpr ratio : new IExpr[] {F.Divide(c, a), F.Negate(F.Divide(c, a))}) {
      IExpr root = engine.evaluate(F.PowerExpand(F.Simplify(F.Sqrt(ratio))));
      if (root.isPresent() && !root.isFree(xVar) && root.isFree(x -> x.isAST(S.C, 2), true)
          && root.leafCount() <= MAX_RICCATI_LEAF_COUNT) {
        candidates.append(root);
      }
    }
    IExpr k = F.Dummy("k");
    IExpr point = F.QQ(7, 10);
    for (int i = 1; i <= candidates.argSize(); i++) {
      IExpr m = candidates.get(i);
      IExpr guess = F.Times(k, m);
      IExpr residual = engine.evaluate(F.Subtract(F.D(guess, xVar),
          F.Plus(F.Times(a, F.Sqr(guess)), F.Times(b, guess), c)));
      IExpr atPoint = engine.evaluate(F.subst(residual, xVar, point));
      if (!atPoint.isFree(xVar) || atPoint.isFree(k)) {
        continue;
      }
      IAST roots = DSolveUtil.extractSolveResults(
          engine.evaluate(F.Solve(F.Equal(atPoint, F.C0), F.List(k))));
      for (int j = 1; j <= roots.argSize(); j++) {
        IExpr value = roots.get(j);
        if (value.isZero() || !value.isFree(xVar) || !value.isFree(k)) {
          continue;
        }
        IExpr fitted = engine.evaluate(F.subst(residual, k, value));
        // A cheap look first. Nearly every candidate is wrong, and deciding symbolically that a
        // wrong one does not vanish is where the time goes: Simplify on a rational function with
        // an irrational k in it can run for minutes. A candidate which is not zero at two points
        // is not zero, and only one which is gets the symbolic check.
        if (!vanishesAt(fitted, xVar, F.QQ(13, 10), engine)
            || !vanishesAt(fitted, xVar, F.QQ(23, 10), engine)) {
          continue;
        }
        if (isVanishing(fitted, engine)) {
          return engine.evaluate(F.Times(value, m));
        }
      }
    }
    return F.NIL;
  }

  /**
   * Solves a Riccati equation of the form: y' = a*y^2 + b*y + c
   */
  static IExpr solveRiccati(IExpr a, IExpr b, IExpr c, IExpr xVar, IExpr yFunction, IExpr C_1,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    // Strategy 1: Constants Coefficients (Separation of Variables)
    if (a.isFree(xVar) && b.isFree(xVar) && c.isFree(xVar)) {
      IExpr ySym = F.Dummy("Y");
      IExpr denominator = F.Plus(c, F.Times(b, ySym), F.Times(a, F.Sqr(ySym)));
      IExpr integral = DSolveContext.integrate(F.Divide(F.C1, denominator), ySym, engine);
      if (integral.isNIL()) {
        return F.NIL;
      }

      IExpr eq = F.Equal(integral, F.Plus(xVar, C_1));
      IExpr ySols = engine.evaluate(F.Solve(eq, F.List(ySym)));
      IAST extracted = DSolveUtil.extractSolveResults(ySols);
      if (extracted.argSize() > 0) {
        // return engine.evaluate(F.Simplify(extracted.arg1()));
        return engine.evaluate(extracted.arg1());
      }
    }

    // Strategy 2: Substitution into Second-Order Linear ODE
    // Projection: y = -u' / (a * u)
    IExpr uSym = F.Dummy("u");
    IExpr u = F.unaryAST1(uSym, xVar);
    IExpr uPrime = F.D(u, xVar);
    IExpr uDoublePrime = F.D(uPrime, xVar);

    // With y == -u'/(a*u), y' == a*y^2 + b*y + c becomes u'' - (b + a'/a)*u' + a*c*u == 0. The sign
    // of a'/a used to be the other one, which agrees only when a is constant: y' == y^2/E^x + 4*y +
    // 2*E^x came out as u'' - 5*u' + 2*u == 0 in place of u'' - 3*u' + 2*u == 0, whose solutions
    // E^x and E^(2*x) are the ones -E^x and -2*E^x come from. The verification refused the answer
    // that gave, so it declined rather than answered wrongly.
    IExpr aPrime = engine.evaluate(F.D(a, xVar));
    IExpr coeffUPrime = engine.evaluate(F.Plus(b, F.Divide(aPrime, a)));

    IExpr uEq =
        F.Equal(F.Plus(uDoublePrime, F.Times(F.CN1, coeffUPrime, uPrime), F.Times(a, c, u)), F.C0);

    // The equation this leaves is linear, and the methods for those do not lead back here, so a
    // coefficient which depends on the variable is no reason to decline: the Riccati equation
    // q'(r) == -(1 + r^2*q(r)^2)/2/r^2 leaves the Cauchy-Euler equation u''(r) + u(r)/(4*r^2) == 0.
    // The depth is bounded because the reduction of order of a second order equation can produce
    // a first order one which arrives back here.
    IExpr product = engine.evaluate(F.Times(a, c));
    if (coeffUPrime.isFree(xVar) && ctx.depth() <= MAX_RICCATI_DEPTH
        && product.leafCount() <= MAX_RICCATI_LEAF_COUNT) {
      IExpr uSols = engine.evaluate(F.DSolve(F.List(uEq), F.List(u), xVar));
      IAST extracted = DSolveUtil.extractSolveResults(uSols);
      if (extracted.argSize() > 0) {
        IExpr uSolExpr = extracted.arg1();
        if (uSolExpr.isAST(S.Function)) {
          uSolExpr = engine.evaluate(F.unaryAST1(uSolExpr, xVar));
        }

        IExpr uSolPrime = engine.evaluate(F.D(uSolExpr, xVar));
        IExpr ySol = engine.evaluate(F.Divide(F.Negate(uSolPrime), F.Times(a, uSolExpr)));

        // Homogeneous Riccati reduction yields one redundant arbitrary constant. Let's strictly
        // absorb.
        IExpr cVarsList = engine.evaluate(F.Cases(ySol, F.C(F.$b()), F.Infinity));
        cVarsList = engine.evaluate(F.DeleteDuplicates(cVarsList));

        if (cVarsList.isList() && ((IAST) cVarsList).argSize() >= 1) {
          IAST list = (IAST) cVarsList;
          IASTAppendable replaceRules = F.ListAlloc(list.argSize());
          IExpr cFirst = list.arg1();
          replaceRules.append(F.Rule(cFirst, C_1));
          if (list.argSize() >= 2) {
            IExpr cSecond = list.arg2();
            replaceRules.append(F.Rule(cSecond, F.C1));
          }
          ySol = engine.evaluate(F.subst(ySol, replaceRules));
        }

        return DSolveUtil.togetherSolution(engine.evaluate(ySol), engine);
      }
    }

    return F.NIL;
  }

  static IExpr solveSingleODE(IExpr equation, IExpr xVar, IAST listOfVariables, IExpr C_1,
      DSolveContext ctx) {
    ctx.enter();
    try {
      return solveSingleODEImpl(equation, xVar, listOfVariables, C_1, ctx);
    } finally {
      ctx.leave();
    }
  }

  /**
   * Solves the equation which one of the sub-solvers has produced, and returns its branches.
   *
   * <p>
   * A method which reduces an equation to a simpler one hands that one back to the cascade, exactly
   * as {@link #unaryODE} does for the equation the user asked about, including the fallback to
   * {@link #odeSolve} for a first order equation the cascade does not recognize.
   *
   * @return the branches of the solution, empty if there are none
   */
  static IAST solveSubODE(IExpr equation, IExpr xVar, IExpr yFunction, IExpr constant,
      DSolveContext ctx) {
    IExpr solution = solveSingleODE(equation, xVar, F.List(yFunction), constant, ctx);
    if (solution.isNIL()) {
      solution = odeSolve(ctx.engine, equation, xVar, yFunction, constant);
    }
    if (solution.isNIL()
        && LinearODEForm.highestDerivativeOrder(equation, yFunction.head(), xVar) == 1) {
      solution = solveRiccatiThroughParticular(equation, xVar, yFunction, constant, ctx.engine);
    }
    if (solution.isNIL()) {
      return F.CEmptyList;
    }
    IAST branches = DSolveUtil.stripConditionalExpression(solution).makeList();
    IASTAppendable usable = F.ListAlloc(branches.argSize());
    for (int i = 1; i <= branches.argSize(); i++) {
      IExpr branch = DSolveUtil.stripConditionalExpression(branches.get(i));
      if (branch.isPresent()) {
        usable.append(branch);
      }
    }
    return usable;
  }

  private static IExpr solveSingleODEImpl(IExpr equation, IExpr xVar, IAST listOfVariables,
      IExpr C_1, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr yFunction = listOfVariables.arg1();
    IExpr head = yFunction.head();

    IExpr lhs = equation;
    if (equation.isEqual()) {
      lhs = S.Subtract.of(engine, equation.first(), equation.second());
    }
    lhs = engine.evaluate(F.ExpandAll(lhs));

    IExpr dyx = S.D.of(engine, yFunction, xVar);

    // Attempt: Clairaut's Equation
    // Substitute y'(x) with a dummy variable `p`.
    IExpr pClairaut = F.Dummy("p");
    IExpr lhsP = engine.evaluate(F.subst(lhs, dyx, pClairaut));

    // Matches forms where y - x*p - f(p) = 0 or -y + x*p + f(p) = 0
    IExpr clairautTest1 = engine
        .evaluate(F.ExpandAll(F.Subtract(lhsP, F.Subtract(yFunction, F.Times(xVar, pClairaut)))));
    IExpr clairautTest2 =
        engine.evaluate(F.ExpandAll(F.Plus(lhsP, F.Subtract(yFunction, F.Times(xVar, pClairaut)))));

    if (clairautTest1.isFree(xVar) && clairautTest1.isFree(yFunction)) {
      IExpr f_c = F.subst(clairautTest1, pClairaut, C_1);
      return engine.evaluate(F.Expand(F.Subtract(F.Times(C_1, xVar), f_c)));
    } else if (clairautTest2.isFree(xVar) && clairautTest2.isFree(yFunction)) {
      IExpr f_c = F.subst(clairautTest2, pClairaut, C_1);
      return engine.evaluate(F.Expand(F.Plus(F.Times(C_1, xVar), f_c)));
    }

    int n = LinearODEForm.highestDerivativeOrder(lhs, head, xVar);
    if (n < 0) {
      return F.NIL;
    }

    // No derivative present — solve as a pure algebraic equation for yFunction
    if (n == 0) {
      IExpr solutions = engine.evaluate(F.Solve(F.Equal(lhs, F.C0), F.List(yFunction)));
      IAST extracted = DSolveUtil.extractSolveResults(solutions);
      if (extracted.argSize() > 0) {
        IASTAppendable roots = F.ListAlloc(extracted.argSize());
        for (int i = 1; i <= extracted.argSize(); i++) {
          roots.append(extracted.get(i));
        }
        if (roots.argSize() == 1) {
          return roots.arg1();
        } else if (roots.argSize() > 1) {
          return roots;
        }
      }
      return F.NIL;
    }

    // A product is split before the methods below are asked to recognize it whole: anything
    // solving one factor solves the product.
    IExpr factoredSol = DSolveFactorable.solve(lhs, yFunction, xVar, n, C_1, ctx);
    if (factoredSol.isPresent()) {
      return factoredSol;
    }

    // The one place which decides whether the equation is linear in the function it is solved
    // for. Reading coefficients off an equation which is not makes the solvers below answer
    // confidently with something wrong instead of declining.
    LinearODEForm lf = LinearODEForm.extract(lhs, yFunction, xVar, engine);

    // Route higher-order and first-order to their specific solvers
    if (n > 1) {
      if (lf != null) {
        // Clearing denominators and a common factor is worth doing on the equation which was
        // asked about, and only there: inside a recursion it costs time and hands the inner
        // method a form it did not ask for.
        LinearODEForm normalized = ctx.mayRewrite() ? lf.normalized(xVar, engine) : lf;

        IExpr linearSol = solveLinearConstantCoefficients(normalized, xVar, C_1, ctx);
        if (linearSol.isPresent())
          return linearSol;

        IExpr eulerCauchySol = solveEulerCauchyODE(normalized, yFunction, xVar, C_1, ctx);
        if (eulerCauchySol.isPresent())
          return eulerCauchySol;

        if (n == 3) {
          IExpr symmetricSquare = DSolveSymmetricSquare.solve(normalized, xVar, C_1, ctx);
          if (symmetricSquare.isPresent())
            return symmetricSquare;
        }
      }

      IExpr reductionOfOrderSol = solveReductionOfOrderODE(lhs, yFunction, xVar, n, C_1, ctx);
      if (reductionOfOrderSol.isPresent())
        return reductionOfOrderSol;

      if (lf != null && n >= 3) {
        // The operator which makes the equation may split off a factor of the first order, and
        // what is left of it is an equation of one order less which the cascade is asked about.
        IExpr operatorSol = DSolveOperatorFactor.solve(lf, yFunction, xVar, C_1, ctx);
        if (operatorSol.isPresent())
          return operatorSol;
      }

      if (lf != null && n == 2) {
        IExpr specialSol = DSolveSpecialFunctions.solve(lf, yFunction, xVar, C_1, ctx);
        if (specialSol.isPresent())
          return specialSol;

        // The coefficients may become rational, and the equation one of those above, in another
        // variable. This runs after them because it asks them about the equation it produces.
        IExpr changedSol = DSolveChangeOfVariable.solve(lf, yFunction, xVar, C_1, ctx);
        if (changedSol.isPresent())
          return changedSol;

        // Nothing above recognized the equation as one with a name, so ask the general question
        // of what kind of function a solution of it is. This runs last of the second order
        // methods: it answers more equations than they do, but in a form which is theirs when
        // they own it.
        IExpr kovacicSol = DSolveKovacic.solve(lf, yFunction, xVar, C_1, ctx);
        if (kovacicSol.isPresent())
          return kovacicSol;

        // Last of all, the equations which are one of the named ones only after being rewritten.
        // After Kovacic and not with the rows it belongs to, because several of them have
        // elementary solutions which Kovacic writes out and this would answer with a
        // hypergeometric function instead.
        IExpr rewrittenSol = DSolveSpecialFunctions.solveByRewriting(lf, yFunction, xVar, C_1, ctx);
        if (rewrittenSol.isPresent())
          return rewrittenSol;

        // Every method above answers a homogeneous equation, and an inhomogeneous one whose
        // coefficients are constant has its own path. What is left is the inhomogeneous equation
        // with variable coefficients, and the homogeneous one beside it may well be one of theirs.
        IExpr forcedSol = solveByVariationOfParameters(lf, yFunction, xVar, C_1, ctx);
        if (forcedSol.isPresent())
          return forcedSol;
      }

      if (lf == null && n == 2) {

        // Nothing above recognized it, so look for a symmetry of it. Only a nonlinear equation is
        // worth the search: a linear one of the second order has an eight dimensional symmetry
        // algebra, so the search always succeeds and costs a great deal without answering anything
        // the methods above do not already own.
        IExpr symmetrySol = DSolveSymmetry.solveSecondOrder(lhs, yFunction, xVar, C_1, ctx);
        if (symmetrySol.isPresent())
          return symmetrySol;

        // An equation which can be integrated once is, and what is left is of the first order.
        // After the symmetry search rather than before it: the two answer some of the same
        // equations, and where they do, the search writes the answer with the two constants the
        // equation should have while this one carries the constant of the quadrature besides
        // them.
        IExpr integratedSol = DSolveIntegratingFactor.solve(lhs, yFunction, xVar, C_1, ctx);
        if (integratedSol.isPresent()) {
          return integratedSol;
        }
      }

      IExpr algebraicSol = solveForHighestDerivative(lhs, yFunction, xVar, n, C_1, ctx);
      if (algebraicSol.isPresent())
        return algebraicSol;

    } else {
      // --- FIRST ORDER SOLVERS (n == 1) ---
      IExpr coeffDyx = engine.evaluate(F.Coefficient(lhs, dyx));

      if (!coeffDyx.isZero() && coeffDyx.isFree(head, true)
          && isLinearInDerivative(lhs, dyx, engine)) {
        IExpr rest = engine.evaluate(F.Subtract(lhs, F.Times(coeffDyx, dyx)));
        IExpr coeffY = engine.evaluate(F.Coefficient(rest, yFunction));

        // Attempt 1: Standard First-Order Linear ODE
        if (coeffY.isFree(head)) {
          // Expanded, or the coefficient which was just read off does not cancel against the term
          // it came from: -y(x) - x*y(x) + (1+x)*y(x) is zero without being written that way, and
          // an unexpanded difference still mentions the unknown, which reads as a forcing term
          // that is not free of it and sends the equation past this method.
          IExpr freeTerm =
              engine.evaluate(F.Expand(F.Subtract(rest, F.Times(coeffY, yFunction))));

          if (freeTerm.isFree(head)) {
            if (!freeTerm.isFree(x -> x.isFunctionID(ID.DiracDelta, ID.HeavisideTheta), false)) {
              // Defer discontinuous forcing functions to the Laplace Transform solver
            } else {
              IExpr p = engine.evaluate(F.Divide(coeffY, coeffDyx));
              IExpr q = engine.evaluate(F.Divide(freeTerm, coeffDyx));
              return linearODE(p, q, xVar, C_1, engine);
            }
          }
        }

        // Attempt 1.5: General Bernoulli Equation
        IExpr nonLinearPart =
            engine.evaluate(F.Expand(F.Subtract(rest, F.Times(coeffY, yFunction))));
        IExpr nExpr = F.NIL;
        IExpr coeffYn = F.NIL;

        if (!nonLinearPart.isZero()) {
          if (nonLinearPart.isPower() && nonLinearPart.first().equals(yFunction)) {
            coeffYn = F.C1;
            nExpr = nonLinearPart.second();
          } else if (nonLinearPart.isTimes()) {
            IASTAppendable remainingCoeff = F.TimesAlloc();
            for (int i = 1; i <= nonLinearPart.argSize(); i++) {
              IExpr arg = ((IAST) nonLinearPart).get(i);
              if (arg.isPower() && arg.first().equals(yFunction)) {
                nExpr = arg.second();
              } else {
                remainingCoeff.append(arg);
              }
            }
            if (nExpr.isPresent()) {
              coeffYn = remainingCoeff.argSize() == 1 ? remainingCoeff.arg1() : remainingCoeff;
            }
          }

          if (nExpr.isPresent() && coeffYn.isFree(head) && nExpr.isFree(head)
              && nExpr.isFree(xVar)) {
            IExpr oneMinusN = engine.evaluate(F.Subtract(F.C1, nExpr));
            IExpr p_u = engine.evaluate(F.Times(oneMinusN, F.Divide(coeffY, coeffDyx)));
            IExpr q_u = engine.evaluate(F.Times(oneMinusN, F.Divide(coeffYn, coeffDyx)));

            IExpr cConstant = C_1;
            if (p_u.isZero()) {
              cConstant = engine.evaluate(F.Times(F.CN1, C_1));
            }

            IExpr uSol = linearODE(p_u, q_u, xVar, cConstant, engine);
            if (uSol.isPresent()) {
              return DSolveUtil.bernoulliRoots(uSol, oneMinusN, engine);
            }
          }
        }

        // Attempt 2: Riccati or Bernoulli Polynomial Extraction
        IExpr expandedRest = engine.evaluate(F.ExpandAll(rest));
        IExpr q0 = engine.evaluate(F.Coefficient(expandedRest, yFunction, F.C0));
        IExpr q1 = engine.evaluate(F.Coefficient(expandedRest, yFunction, F.C1));
        IExpr q2 = engine.evaluate(F.Coefficient(expandedRest, yFunction, F.C2));

        IExpr remainder = engine.evaluate(F.ExpandAll(F.Subtract(expandedRest,
            F.Plus(q0, F.Times(q1, yFunction), F.Times(q2, F.Sqr(yFunction))))));

        if (remainder.isZero() && !q2.isZero()) {
          IExpr a = engine.evaluate(F.Divide(F.Negate(q2), coeffDyx));
          IExpr b = engine.evaluate(F.Divide(F.Negate(q1), coeffDyx));
          IExpr c = engine.evaluate(F.Divide(F.Negate(q0), coeffDyx));

          if (c.isZero()) {
            IExpr cConstant = C_1;
            if (b.isZero()) {
              cConstant = engine.evaluate(F.Times(F.CN1, C_1));
            }
            IExpr uSol = linearODE(b, a, xVar, cConstant, engine);
            if (uSol.isPresent()) {
              return engine.evaluate(F.Power(uSol, F.CN1));
            }
          } else {
            IExpr riccatiSol = solveRiccati(a, b, c, xVar, yFunction, C_1, ctx);
            if (riccatiSol.isPresent()) {
              return riccatiSol;
            }
          }
        }
      }
    }

    if (n == 1) {
      // A power of something linear in the unknown whose exponent is a fraction is a shape none
      // of the methods above produces, so asking about it here costs them nothing and saves the
      // searches below the budget they would spend on a radical of the unknown.
      IExpr shiftedSol = DSolvePolynomialShift.solve(lhs, yFunction, xVar, C_1, ctx);
      if (shiftedSol.isPresent()) {
        return shiftedSol;
      }

      // A change of the variables can make the equation one of the kinds already solved: seen
      // through one linear combination of x and y it may lose its x, and a ratio of two linear
      // expressions loses its constant terms when the origin moves to where the two lines meet.
      IExpr reducedSol = DSolveFirstOrderReduction.solve(lhs, yFunction, xVar, C_1, ctx);
      if (reducedSol.isPresent()) {
        return reducedSol;
      }

      // A substitution u == phi(y) can make an equation linear which is not linear as it stands.
      // This runs before the M + N*y' == 0 solvers: it produces
      // an explicit y for the equations whose right hand side is transcendental in y, and gets
      // there before an integrating factor search can spin on one of those.
      IExpr linearizableSol = DSolveLinearizable.solve(lhs, yFunction, xVar, C_1, ctx);
      if (linearizableSol.isPresent()) {
        return linearizableSol;
      }

      IExpr algebraicSol = solveForHighestDerivative(lhs, yFunction, xVar, n, C_1, ctx);
      if (algebraicSol.isPresent()) {
        return algebraicSol;
      }
    }

    // Attempt Laplace Transform as the ultimate linear fallback (handles BOTH n=1 and n>=2)
    // If we reach this point, all specific solvers above have failed.
    if (lf != null) {
      IExpr laplaceSol = solveLaplaceODE(lhs, yFunction, xVar, n, C_1, engine);
      if (laplaceSol.isPresent()) {
        return laplaceSol;
      }
    }

    return F.NIL;
  }

  /**
   * Whether the equation contains the derivative <code>dyx</code> in the first power only, so that
   * reading a coefficient of it accounts for every term it appears in.
   */
  static boolean isLinearInDerivative(IExpr lhs, IExpr dyx, EvalEngine engine) {
    IExpr expanded = engine.evaluate(F.ExpandAll(lhs));
    IExpr remainder =
        engine.evaluate(F.ExpandAll(F.Subtract(expanded, F.Plus(F.Coefficient(expanded, dyx, F.C0),
            F.Times(F.Coefficient(expanded, dyx, F.C1), dyx)))));
    return remainder.isZero();
  }

  /**
   * The first fitted candidate which solves the equation, or {@link F#NIL}.
   *
   * <p>
   * The general solution was checked against the equation before it was fitted, and fitting it
   * does not keep that true: a constant which meets the condition can pick the part of a squared
   * relation which was never a solution. So the fitted candidate is checked again, in the same
   * way, and one which the equation refutes is passed over for the next.
   */
  private static IExpr firstSolvingCandidate(IAST candidates, IAST listOfEquations,
      IAST uFunction1Arg, IExpr xVar, EvalEngine engine) {
    for (int i = 1; i <= candidates.argSize(); i++) {
      IExpr candidate = candidates.get(i);
      if (DSolveVerify.acceptODE(listOfEquations, uFunction1Arg, xVar, candidate, engine)) {
        return candidate;
      }
    }
    return F.NIL;
  }

  /**
   * The branches which answer the equation, written the way <code>DSolve</code> returns them.  /**
   * The branches which answer the equation, written the way <code>DSolve</code> returns them.
   *
   * @param fitted whether the branches already have the conditions in them, in which case there is
   *        no constant to solve for and what is left is to check that they do
   * @param bcUnsatisfiable set when a branch was ruled out by the conditions, which is only worth
   *        reporting once no branch is left
   */
  private static IASTAppendable acceptBranches(IAST roots, IAST listOfEquations,
      IAST uFunction1Arg, IExpr arg2, IExpr xVar, IAST boundaryConditions, IExpr c_n,
      boolean fitted, boolean[] bcUnsatisfiable, EvalEngine engine) {
    IASTAppendable resultList = F.ListAlloc();
    for (int r = 1; r <= roots.argSize(); r++) {
      IExpr root = roots.get(r);
      root = DSolveUtil.stripConditionalExpression(root);
      root = DSolveUtil.absorbConstants(root, F.list(c_n), false, engine);

      if (!DSolveVerify.acceptODE(listOfEquations, uFunction1Arg, xVar, root, engine)) {
        // The equation was recognized by a method it does not actually belong to. Putting
        // the answer back into it is what catches that.
        continue;
      }

      if (boundaryConditions.argSize() > 0) {
        if (fitted && root.isFree(c_n, true)) {
          if (!meetsConditions(root, uFunction1Arg, xVar, boundaryConditions, engine)) {
            bcUnsatisfiable[0] = true;
            continue;
          }
        } else {
          root = firstSolvingCandidate(
              fitCandidates(root, uFunction1Arg, xVar, boundaryConditions, engine),
              listOfEquations, uFunction1Arg, xVar, engine);
          if (!root.isPresent()) {
            // Skip this root branch if the BCs cannot be satisfied. A general solution with
            // more than one branch normally has branches which the conditions rule out, so this
            // is only worth reporting once none of them is left.
            bcUnsatisfiable[0] = true;
            continue;
          }
        }
      }

      if (arg2.isSymbol() && xVar.isSymbol()) {
        resultList.append(F.list(F.Rule(arg2, F.Function(F.list(xVar), root))));
      } else {
        resultList.append(F.list(F.Rule(arg2, root)));
      }
      if (fitted) {
        // An initial value problem has one solution, and the branches of the relation it was
        // inverted from are the same solution written for different parts of the plane.
        break;
      }
    }
    return resultList;
  }

  static IExpr unaryODE(IAST uFunction1Arg, IExpr arg2, IExpr xVar, IASTAppendable listOfEquations,
      IAST boundaryConditions, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IAST listOfVariables = F.list(uFunction1Arg);

    if (listOfEquations.argSize() == 1) {
      IExpr c_n = F.C(engine.incConstantCounter());
      try {
        IExpr equation = listOfEquations.arg1();
        IExpr temp = solveSingleODE(equation, xVar, listOfVariables, c_n, ctx);

        if (temp.isNIL()
            && LinearODEForm.highestDerivativeOrder(equation, uFunction1Arg.head(), xVar) == 1) {
          // The M + N*y' == 0 solvers read the equation as a first order one, so offering them an
          // equation of a higher order lets them answer from a part of it.
          temp = odeSolve(engine, equation, xVar, uFunction1Arg, c_n);
          if (temp.isNIL()) {
            temp = solveRiccatiThroughParticular(equation, xVar, uFunction1Arg, c_n, engine);
          }
        }

        if (temp.isPresent()) {
          boolean[] bcUnsatisfiable = new boolean[1];
          // Wrap in a list if it's a single root to uniformize processing
          IASTAppendable resultList = acceptBranches(temp.makeList(), listOfEquations,
              uFunction1Arg, arg2, xVar, boundaryConditions, c_n, false, bcUnsatisfiable, engine);
          if (resultList.argSize() > 0) {
            return resultList;
          }
          if (bcUnsatisfiable[0]) {
            // Every branch was ruled out by the conditions, which happens for a real reason and
            // also happens when the constant is somewhere the general solution cannot be solved
            // for it -- under a square root and inside a cube root, for the equations whose
            // relation is a cubic in y. Naming the constant from the condition first and inverting
            // afterwards asks the same question in the order it can be answered in.
            IExpr[] point = valuePoint(boundaryConditions, uFunction1Arg.head(), xVar, engine);
            if (point != null && LinearODEForm.highestDerivativeOrder(listOfEquations.arg1(),
                uFunction1Arg.head(), xVar) == 1) {
              IExpr fitted =
                  odeSolve(engine, listOfEquations.arg1(), xVar, uFunction1Arg, c_n, point);
              if (fitted.isPresent()) {
                resultList = acceptBranches(fitted.makeList(), listOfEquations, uFunction1Arg, arg2,
                    xVar, boundaryConditions, c_n, true, new boolean[1], engine);
                if (resultList.argSize() > 0) {
                  return resultList;
                }
              }
            }
            ctx.addMessage("bvfail", F.CEmptyList);
            return F.NIL;
          }
          return F.NIL;
        }
      } finally {
        engine.decConstantCounter();
      }
    }
    return F.NIL;
  }

  /**
   * Solves a linear differential equation with constant coefficients through the roots of its
   * characteristic polynomial <code>a[n]*r^n + ... + a[1]*r + a[0] == 0</code>.
   *
   * <p>
   * A root <code>rho</code> of multiplicity <code>m</code> contributes the solutions
   * <code>x^j*E^(rho*x)</code> for <code>j &lt; m</code>, and a pair of conjugate complex roots
   * <code>alpha +- I*beta</code> contributes <code>E^(alpha*x)*Cos(beta*x)</code> and
   * <code>E^(alpha*x)*Sin(beta*x)</code> instead, so that a real equation keeps a real solution. An
   * inhomogeneous equation gets a particular solution by
   * {@link #variationOfParameters(IExpr[], LinearODEForm, IExpr, DSolveContext)}.
   *
   * <p>
   * This replaces solving the equivalent first order system with a matrix exponential, which was
   * where equations of order three and above used to hang: the matrix exponential of the companion
   * matrix of <code>y'''(x) - 6*y''(x) + 11*y'(x) - 6*y(x) == 0</code> alone runs for minutes,
   * while its characteristic polynomial has the roots <code>1</code>, <code>2</code>,
   * <code>3</code>.
   *
   * @param c_n the first arbitrary constant to use
   * @return {@link F#NIL} if the equation has non constant coefficients, or if the characteristic
   *         polynomial cannot be solved in closed form
   */
  static IExpr solveLinearConstantCoefficients(LinearODEForm lf, IExpr xVar, IExpr c_n,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    int n = lf.order;
    if (n < 1 || !lf.constantCoefficients || lf.a[n].isZero()) {
      return F.NIL;
    }
    // A discontinuous forcing function is better served by the Laplace transform, which maps an
    // impulse to a delayed response instead of leaving an unevaluated integral behind.
    if (!lf.g.isFree(x -> x.isFunctionID(ID.DiracDelta, ID.HeavisideTheta), false)) {
      return F.NIL;
    }
    IExpr[] basis = characteristicBasis(lf, xVar, engine);
    if (basis == null || basis.length != n) {
      return F.NIL;
    }
    IASTAppendable homogeneous = F.PlusAlloc(n);
    for (int i = 0; i < n; i++) {
      homogeneous.append(F.Times(i == 0 ? c_n : ctx.nextConstant(), basis[i]));
    }
    IExpr solution = engine.evaluate(homogeneous);
    if (!lf.g.isZero()) {
      // Undetermined coefficients first: where the right hand side is one of the shapes it covers
      // it answers from a linear system, while variation of parameters has to integrate a product
      // of the basis with the forcing function. Those integrals are the expensive part -- the
      // integrand of x''(t) + 3*x'(t) + 3*x(t) == 8*Cos(10*t) + 6*Sin(10*t) takes a minute and a
      // half of them -- and they come back in a form which needs collecting afterwards.
      IExpr particular = undeterminedCoefficients(lf, xVar, ctx);
      if (particular.isNIL()) {
        particular = variationOfParameters(basis, lf, xVar, ctx);
      }
      if (particular.isNIL()) {
        return F.NIL;
      }
      solution = engine.evaluate(F.Expand(F.Plus(solution, particular)));
    }
    return solution;
  }

  /**
   * The fundamental system of a linear equation with constant coefficients, or <code>null</code> if
   * the characteristic polynomial cannot be solved in closed form or its roots do not account for
   * the order of the equation.
   */
  private static IExpr[] characteristicBasis(LinearODEForm lf, IExpr xVar, EvalEngine engine) {
    int n = lf.order;
    IExpr r = F.Dummy("r");
    IASTAppendable polynomial = F.PlusAlloc(n + 1);
    for (int k = 0; k <= n; k++) {
      if (!lf.a[k].isZero()) {
        polynomial.append(F.Times(lf.a[k], F.Power(r, F.ZZ(k))));
      }
    }
    IExpr characteristic = engine.evaluate(polynomial);
    IExpr solved = engine.evaluate(F.Roots(F.Equal(characteristic, F.C0), r));
    IAST equations = solved.isOr() ? (IAST) solved : F.Or(solved);

    java.util.List<IExpr> roots = new java.util.ArrayList<>();
    for (int i = 1; i <= equations.argSize(); i++) {
      IExpr equation = equations.get(i);
      if (!equation.isEqual() || !equation.first().equals(r)) {
        return null;
      }
      IExpr root = equation.second();
      // A root which is only expressible as a Root object cannot be put into an exponent.
      if (!root.isFree(S.Root, true) || !root.isFree(r, true)) {
        return null;
      }
      roots.add(root);
    }
    if (roots.isEmpty()) {
      return null;
    }

    // Roots reports every root once, so the multiplicities have to be recovered by division.
    int[] multiplicities = new int[roots.size()];
    IExpr remaining = characteristic;
    int total = 0;
    for (int i = 0; i < roots.size(); i++) {
      IExpr divisor = engine.evaluate(F.Subtract(r, roots.get(i)));
      while (total < n) {
        IExpr rest = engine.evaluate(F.PolynomialRemainder(remaining, divisor, r));
        if (!isVanishing(rest, engine)) {
          break;
        }
        remaining = engine.evaluate(F.PolynomialQuotient(remaining, divisor, r));
        multiplicities[i]++;
        total++;
      }
      if (multiplicities[i] == 0) {
        return null;
      }
    }
    if (total != n) {
      return null;
    }

    java.util.List<IExpr> basis = new java.util.ArrayList<>(n);
    boolean[] used = new boolean[roots.size()];
    for (int i = 0; i < roots.size(); i++) {
      if (used[i]) {
        continue;
      }
      used[i] = true;
      IExpr root = roots.get(i);
      int partner = conjugatePartner(roots, multiplicities, used, i, engine);
      if (partner >= 0) {
        used[partner] = true;
        IExpr alpha = engine.evaluate(F.Re(root));
        IExpr beta = engine.evaluate(F.Abs(F.Im(root)));
        for (int j = 0; j < multiplicities[i]; j++) {
          IExpr factor =
              engine.evaluate(F.Times(F.Power(xVar, F.ZZ(j)), F.Exp(F.Times(alpha, xVar))));
          basis.add(engine.evaluate(F.Times(factor, F.Cos(F.Times(beta, xVar)))));
          basis.add(engine.evaluate(F.Times(factor, F.Sin(F.Times(beta, xVar)))));
        }
      } else {
        for (int j = 0; j < multiplicities[i]; j++) {
          basis.add(engine.evaluate(F.Times(F.Power(xVar, F.ZZ(j)), F.Exp(F.Times(root, xVar)))));
        }
      }
    }
    return basis.toArray(new IExpr[0]);
  }

  /**
   * The index of the complex conjugate of <code>roots.get(index)</code>, or <code>-1</code> if the
   * root is real, if its real and imaginary parts are not explicit, or if no unused partner of the
   * same multiplicity is present.
   */
  private static int conjugatePartner(java.util.List<IExpr> roots, int[] multiplicities,
      boolean[] used, int index, EvalEngine engine) {
    IExpr root = roots.get(index);
    IExpr imaginary = engine.evaluate(F.Im(root));
    if (imaginary.isZero() || !imaginary.isFree(S.Im, true) || !imaginary.isFree(S.Re, true)
        || !imaginary.isRealResult()) {
      return -1;
    }
    IExpr conjugate = engine.evaluate(F.Conjugate(root));
    for (int j = 0; j < roots.size(); j++) {
      if (!used[j] && multiplicities[j] == multiplicities[index]
          && isVanishing(engine.evaluate(F.Subtract(roots.get(j), conjugate)), engine)) {
        return j;
      }
    }
    return -1;
  }

  /** How many undetermined coefficients an ansatz may carry. */
  private static final int MAX_UNDETERMINED_COEFFICIENTS = 12;

  /**
   * One shape of forcing term: <code>x^degree * E^(rate*x) * Cos(frequency*x)</code> or the same
   * with <code>Sin</code>, which is what an ansatz is written for. A frequency of zero means the
   * term carries no trigonometric factor, a rate of zero none exponential.
   */
  private static final class ForcingShape {
    final IExpr rate;
    final IExpr frequency;
    int degree;

    ForcingShape(IExpr rate, IExpr frequency, int degree) {
      this.rate = rate;
      this.frequency = frequency;
      this.degree = degree;
    }

    boolean sameFamilyAs(IExpr otherRate, IExpr otherFrequency) {
      return rate.equals(otherRate) && frequency.equals(otherFrequency);
    }
  }

  /**
   * The coefficient <code>c</code> of an argument which is <code>c*x</code>, or {@link F#NIL} if the
   * argument is not a multiple of the variable.
   *
   * <p>
   * A constant term is refused rather than absorbed: <code>Cos(2*x + 1)</code> would need the
   * ansatz to carry the phase as well, and variation of parameters answers it as it stands.
   */
  private static IExpr multipleOfVariable(IExpr argument, IExpr xVar, EvalEngine engine) {
    IExpr coefficient = engine.evaluate(F.Coefficient(argument, xVar));
    if (coefficient.isZero() || !coefficient.isFree(xVar, true)) {
      return F.NIL;
    }
    return engine.evaluate(F.Subtract(argument, F.Times(coefficient, xVar))).isZero() //
        ? coefficient
        : F.NIL;
  }

  /**
   * Reads one term of the forcing function as a shape an ansatz can be written for, and records it
   * among <code>shapes</code>.
   *
   * @return <code>false</code> if the term is not a power of the variable times an exponential
   *         times a sine or cosine, in which case there is no ansatz to write
   */
  private static boolean readForcingTerm(IExpr term, IExpr xVar, List<ForcingShape> shapes,
      EvalEngine engine) {
    IExpr rate = F.C0;
    IExpr frequency = F.C0;
    int degree = 0;
    IAST factors = term.isTimes() ? (IAST) term : F.Times(term);
    for (int i = 1; i <= factors.argSize(); i++) {
      IExpr factor = factors.get(i);
      if (factor.isFree(xVar, true)) {
        continue;
      }
      if (factor.equals(xVar)) {
        degree++;
        continue;
      }
      if (factor.isPower()) {
        IExpr base = factor.base();
        IExpr exponent = factor.exponent();
        if (base.equals(xVar)) {
          int power = exponent.toIntDefault();
          if (power < 1 || power > MAX_DERIVATIVE_ORDER) {
            return false;
          }
          degree += power;
          continue;
        }
        if (base == S.E) {
          IExpr newRate = multipleOfVariable(exponent, xVar, engine);
          if (newRate.isNIL() || (!rate.isZero() && !rate.equals(newRate))) {
            return false;
          }
          rate = newRate;
          continue;
        }
        return false;
      }
      if (factor.isAST1() && (factor.isCos() || factor.isSin())) {
        IExpr newFrequency = multipleOfVariable(factor.first(), xVar, engine);
        if (newFrequency.isNIL()) {
          return false;
        }
        // Cos and Sin of the same multiple are one family, and the sign of the multiple does not
        // make a second one: the ansatz for it carries both of them anyway.
        if (newFrequency.isNegative()) {
          newFrequency = engine.evaluate(F.Negate(newFrequency));
        }
        if (!frequency.isZero() && !frequency.equals(newFrequency)) {
          return false;
        }
        frequency = newFrequency;
        continue;
      }
      return false;
    }
    for (ForcingShape shape : shapes) {
      if (shape.sameFamilyAs(rate, frequency)) {
        shape.degree = Math.max(shape.degree, degree);
        return true;
      }
    }
    shapes.add(new ForcingShape(rate, frequency, degree));
    return true;
  }

  /**
   * How often <code>rate + I*frequency</code> is a root of the characteristic polynomial, which is
   * the power of <code>x</code> the ansatz for that shape has to be multiplied by.
   */
  private static int resonance(LinearODEForm lf, IExpr rate, IExpr frequency, EvalEngine engine) {
    IExpr r = F.Dummy("r");
    IASTAppendable polynomial = F.PlusAlloc(lf.order + 1);
    for (int k = 0; k <= lf.order; k++) {
      if (!lf.a[k].isZero()) {
        polynomial.append(F.Times(lf.a[k], F.Power(r, F.ZZ(k))));
      }
    }
    IExpr characteristic = engine.evaluate(polynomial);
    IExpr root = frequency.isZero() //
        ? rate
        : engine.evaluate(F.Plus(rate, F.Times(F.CI, frequency)));
    int multiplicity = 0;
    IExpr derivative = characteristic;
    while (multiplicity <= lf.order) {
      if (!isVanishing(engine.evaluate(F.subst(derivative, r, root)), engine)) {
        return multiplicity;
      }
      multiplicity++;
      // The derivative is evaluated before the root is substituted into it: substituting into an
      // unevaluated D() would replace the variable it differentiates for.
      derivative = engine.evaluate(F.D(derivative, r));
    }
    return -1;
  }

  /**
   * A particular solution of an inhomogeneous linear equation with constant coefficients by
   * undetermined coefficients.
   *
   * <p>
   * The forcing function is read as a sum of terms <code>x^k*E^(a*x)*Cos(b*x)</code>, an ansatz of
   * the same shape is written for each family <code>(a, b)</code> that occurs -- multiplied by
   * <code>x^s</code> where <code>a + I*b</code> is an <code>s</code>-fold root of the
   * characteristic polynomial, which is the case the plain ansatz cannot answer -- and the
   * coefficients come out of the linear system which says that the two sides agree. Replacing the
   * exponential and the two trigonometric functions by symbols of their own is what turns "the two
   * sides agree as functions" into "the coefficients of a polynomial vanish".
   *
   * @return {@link F#NIL} if the forcing function is not of that shape, or if the system it leads
   *         to has no solution, and then variation of parameters is asked instead
   */
  private static IExpr undeterminedCoefficients(LinearODEForm lf, IExpr xVar, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr g = engine.evaluate(F.ExpandAll(lf.g));
    if (g.isZero() || !g.isFree(S.Integrate, true)) {
      return F.NIL;
    }
    IAST terms = g.isPlus() ? (IAST) g : F.Times(g);
    List<ForcingShape> shapes = new ArrayList<>();
    for (int i = 1; i <= terms.argSize(); i++) {
      if (!readForcingTerm(terms.get(i), xVar, shapes, engine)) {
        return F.NIL;
      }
    }

    List<IExpr> unknowns = new ArrayList<>();
    IASTAppendable ansatzTerms = F.PlusAlloc(8);
    IASTAppendable atomRules = F.ListAlloc(3 * shapes.size());
    List<IExpr> atoms = new ArrayList<>();
    atoms.add(xVar);
    for (ForcingShape shape : shapes) {
      int s = resonance(lf, shape.rate, shape.frequency, engine);
      if (s < 0) {
        return F.NIL;
      }
      IExpr exponential = shape.rate.isZero() //
          ? F.C1
          : engine.evaluate(F.Exp(F.Times(shape.rate, xVar)));
      if (!shape.rate.isZero()) {
        IExpr symbol = F.Dummy("exp" + atoms.size());
        atomRules.append(F.Rule(exponential, symbol));
        atoms.add(symbol);
      }
      IExpr cosine = F.NIL;
      IExpr sine = F.NIL;
      if (!shape.frequency.isZero()) {
        cosine = engine.evaluate(F.Cos(F.Times(shape.frequency, xVar)));
        sine = engine.evaluate(F.Sin(F.Times(shape.frequency, xVar)));
        IExpr cosineSymbol = F.Dummy("cos" + atoms.size());
        IExpr sineSymbol = F.Dummy("sin" + atoms.size());
        atomRules.append(F.Rule(cosine, cosineSymbol));
        atomRules.append(F.Rule(sine, sineSymbol));
        atoms.add(cosineSymbol);
        atoms.add(sineSymbol);
      }
      for (int j = 0; j <= shape.degree; j++) {
        IExpr power = F.Power(xVar, F.ZZ(s + j));
        if (shape.frequency.isZero()) {
          appendAnsatzTerm(F.Times(power, exponential), unknowns, ansatzTerms);
        } else {
          appendAnsatzTerm(F.Times(power, exponential, cosine), unknowns, ansatzTerms);
          appendAnsatzTerm(F.Times(power, exponential, sine), unknowns, ansatzTerms);
        }
      }
      if (unknowns.size() > MAX_UNDETERMINED_COEFFICIENTS) {
        return F.NIL;
      }
    }
    if (unknowns.isEmpty()) {
      return F.NIL;
    }

    IExpr ansatz = engine.evaluate(ansatzTerms);
    IASTAppendable residual = F.PlusAlloc(lf.order + 2);
    for (int k = 0; k <= lf.order; k++) {
      if (!lf.a[k].isZero()) {
        IExpr derivative = k == 0 //
            ? ansatz
            : engine.evaluate(F.D(ansatz, F.List(xVar, F.ZZ(k))));
        residual.append(F.Times(lf.a[k], derivative));
      }
    }
    residual.append(F.Negate(g));
    IExpr polynomial = engine.evaluate(F.Expand(F.subst(engine.evaluate(residual), atomRules)));
    if (!polynomial.isFree(x -> x.isCos() || x.isSin() || x.isPower() && x.base() == S.E, true)) {
      // A function which the substitution did not reach means the ansatz does not span what the
      // equation produces, and the coefficients read off below would not be the whole condition.
      return F.NIL;
    }

    IAST equations = coefficientEquations(polynomial, atoms, engine);
    if (equations.isEmpty()) {
      return F.NIL;
    }
    IASTAppendable variables = F.ListAlloc(unknowns.size());
    for (IExpr unknown : unknowns) {
      variables.append(unknown);
    }
    IExpr rules = engine.evaluate(F.Solve(equations, variables));
    if (!rules.isList() || rules.isEmpty() || !rules.first().isList()) {
      return F.NIL;
    }
    IAST assignment = (IAST) rules.first();
    // The system may be solvable without the ansatz being right, so what it answers is put back.
    // A coefficient which came out as a ratio -- which is what a forcing function with a symbol in
    // it leads to, as 100*Cos(om*t) does -- cancels only once the terms are over one denominator.
    IExpr check = engine.evaluate(F.Expand(F.subst(polynomial, assignment)));
    if (!check.isZero()) {
      check = engine.evaluate(F.Together(check));
      if (!check.isZero()) {
        return F.NIL;
      }
    }
    IExpr particular = engine.evaluate(F.Expand(F.subst(ansatz, assignment)));
    for (IExpr unknown : unknowns) {
      if (!particular.isFree(unknown, true)) {
        return F.NIL;
      }
    }
    return particular;
  }

  /** Adds one member of the ansatz, with an undetermined coefficient of its own. */
  private static void appendAnsatzTerm(IExpr function, List<IExpr> unknowns,
      IASTAppendable ansatzTerms) {
    IExpr unknown = F.Dummy("uc" + (unknowns.size() + 1));
    unknowns.add(unknown);
    ansatzTerms.append(F.Times(unknown, function));
  }

  /**
   * The equations which say that <code>polynomial</code> vanishes identically: its terms are
   * gathered by the monomial they carry in <code>atoms</code>, and each of those coefficients has to
   * be zero.
   */
  private static IAST coefficientEquations(IExpr polynomial, List<IExpr> atoms,
      EvalEngine engine) {
    IAST plus = polynomial.isPlus() ? (IAST) polynomial : F.Times(polynomial);
    Map<IExpr, IASTAppendable> byMonomial = new LinkedHashMap<>();
    for (int i = 1; i <= plus.argSize(); i++) {
      IExpr term = plus.get(i);
      IAST factors = term.isTimes() ? (IAST) term : F.Times(term);
      IASTAppendable monomial = F.TimesAlloc(factors.argSize());
      IASTAppendable coefficient = F.TimesAlloc(factors.argSize());
      for (int k = 1; k <= factors.argSize(); k++) {
        IExpr factor = factors.get(k);
        boolean carriesAtom = false;
        for (IExpr atom : atoms) {
          if (!factor.isFree(atom, true)) {
            carriesAtom = true;
            break;
          }
        }
        if (carriesAtom) {
          monomial.append(factor);
        } else {
          coefficient.append(factor);
        }
      }
      byMonomial.computeIfAbsent(engine.evaluate(monomial), key -> F.PlusAlloc(4))
          .append(engine.evaluate(coefficient));
    }
    IASTAppendable equations = F.ListAlloc(byMonomial.size());
    for (IASTAppendable coefficient : byMonomial.values()) {
      equations.append(F.Equal(engine.evaluate(coefficient), F.C0));
    }
    return equations;
  }

  /**
   * An inhomogeneous linear equation with variable coefficients, solved through the homogeneous
   * equation beside it.
   *
   * <p>
   * The methods which find the solutions of a linear equation with variable coefficients --
   * Kovacic, the special functions, the changes of variable -- are written for the homogeneous
   * equation and decline a forcing term. But a basis of the homogeneous equation is all variation
   * of parameters needs. <code>t^2*y'' - t*(t+2)*y' + (t+2)*y == 2*t^3</code> has the homogeneous
   * basis <code>{t, t*E^t}</code>, which the cascade finds, and the particular solution
   * <code>-2*t^2 - 2*t</code> follows from it by two elementary integrals; the equation used to be
   * declined because nothing asked for the one and then the other.
   *
   * <p>
   * The basis is read off the general solution as the coefficients of its arbitrary constants, and
   * only a general solution which is exactly a combination of them is used.
   *
   * @return {@link F#NIL} if the homogeneous equation is not solved, or the integrals are not ones
   *         the cascade can use
   */
  private static IExpr solveByVariationOfParameters(LinearODEForm lf, IExpr yFunction, IExpr xVar,
      IExpr C_1, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (lf.g.isZero() || lf.constantCoefficients || ctx.depth() > MAX_VARIATION_DEPTH) {
      return F.NIL;
    }
    int n = lf.order;
    IASTAppendable homogeneous = F.PlusAlloc(n + 1);
    for (int k = 0; k <= n; k++) {
      if (!lf.a[k].isZero()) {
        IExpr derivative = k == 0 ? yFunction : F.D(yFunction, F.List(xVar, F.ZZ(k)));
        homogeneous.append(F.Times(lf.a[k], derivative));
      }
    }
    // The homogeneous equation is written down here rather than rewritten from anything, so the
    // methods which rewrite an equation once may treat it as the one they were asked about. Several
    // of them -- the normal form which removes the first derivative, above all -- solve it and
    // nothing else does.
    int rewriting = ctx.enterUnrewritten();
    IAST branches;
    try {
      branches = solveSubODE(F.Equal(engine.evaluate(homogeneous), F.C0), xVar, yFunction, C_1, ctx);
    } finally {
      ctx.leaveUnrewritten(rewriting);
    }
    if (branches.argSize() != 1) {
      return F.NIL;
    }
    IExpr general = engine.evaluate(F.Expand(branches.arg1()));
    IASTAppendable constants = F.ListAlloc();
    DSolveUtil.extractCVars(general, constants);
    if (constants.argSize() != n) {
      return F.NIL;
    }
    IExpr[] basis = new IExpr[n];
    IASTAppendable recombined = F.PlusAlloc(n);
    for (int i = 0; i < n; i++) {
      IExpr constant = constants.get(i + 1);
      IExpr member = engine.evaluate(F.Coefficient(general, constant, F.C1));
      if (member.isZero() || !member.isFree(x -> x.isAST(S.C, 2), true)) {
        return F.NIL;
      }
      basis[i] = member;
      recombined.append(F.Times(constant, member));
    }
    if (!isVanishing(engine.evaluate(F.Subtract(general, recombined)), engine)) {
      // Something in the general solution is not a multiple of a constant, so this is not the
      // basis it looks like.
      return F.NIL;
    }
    IExpr particular = variationOfParameters(basis, lf, xVar, ctx);
    if (particular.isNIL() || !DSolveContext.isUsable(particular)) {
      return F.NIL;
    }
    return engine.evaluate(F.Plus(general, particular));
  }

  /**
   * A particular solution of an inhomogeneous linear equation by variation of parameters:
   * <code>y_p == Sum(y_i*Integrate(W_i/W, x))</code>, where <code>W</code> is the determinant of
   * the fundamental matrix and <code>W_i</code> that determinant with its i-th column replaced by
   * the forcing function.
   *
   * @return {@link F#NIL} if one of the integrals is not one the caller can go on working with
   */
  private static IExpr variationOfParameters(IExpr[] basis, LinearODEForm lf, IExpr xVar,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    int n = basis.length;
    IExpr[][] derivatives = new IExpr[n][n];
    for (int row = 0; row < n; row++) {
      for (int column = 0; column < n; column++) {
        derivatives[row][column] = row == 0 //
            ? basis[column]
            : engine.evaluate(F.D(basis[column], F.List(xVar, F.ZZ(row))));
      }
    }
    IASTAppendable fundamental = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      fundamental.append(basis[i]);
    }
    IExpr wronskian = engine.evaluate(F.Simplify(S.Wronskian.of(engine, fundamental, xVar)));
    if (wronskian.isZero() || !wronskian.isFree(S.Wronskian, true)) {
      // A vanishing determinant means the basis is not one, so there is nothing to vary.
      return F.NIL;
    }
    IExpr forcing = engine.evaluate(F.Divide(lf.g, lf.a[n]));
    IASTAppendable particular = F.PlusAlloc(n);
    for (int i = 0; i < n; i++) {
      IExpr replaced = engine.evaluate(F.Det(matrix(derivatives, i, forcing)));
      IExpr integrand = engine.evaluate(F.Simplify(F.Divide(replaced, wronskian)));
      IExpr integral = ctx.integrate(integrand, xVar);
      if (integral.isNIL()) {
        return F.NIL;
      }
      particular.append(F.Times(basis[i], integral));
    }
    // Variation of parameters produces the particular solution in whatever form the integrals came
    // out in. Reducing the products of trigonometric functions makes the terms which are multiples
    // of a solution of the homogeneous equation recognizable, so that the arbitrary constants can
    // absorb them instead of the answer carrying them along.
    IExpr result = engine.evaluate(F.Expand(particular));
    IExpr reduced = engine.evaluate(F.Expand(F.TrigReduce(result)));
    return reduced.isPresent() && reduced.leafCount() <= result.leafCount() ? reduced : result;
  }

  /**
   * The matrix of the given rows, with column <code>replaceColumn</code> replaced by the vector
   * <code>(0, ..., 0, forcing)</code> when that column index is not negative.
   */
  private static IAST matrix(IExpr[][] rows, int replaceColumn, IExpr forcing) {
    int n = rows.length;
    IASTAppendable result = F.ListAlloc(n);
    for (int row = 0; row < n; row++) {
      IASTAppendable rowAST = F.ListAlloc(n);
      for (int column = 0; column < n; column++) {
        if (column == replaceColumn) {
          rowAST.append(row == n - 1 ? forcing : F.C0);
        } else {
          rowAST.append(rows[row][column]);
        }
      }
      result.append(rowAST);
    }
    return result;
  }

  /** Whether the expression is zero, allowing for a form which needs simplification first. */
  static boolean isVanishing(IExpr expr, EvalEngine engine) {
    if (expr.isZero()) {
      return true;
    }
    if (expr.isNumber()) {
      return false;
    }
    return engine.evaluate(F.Simplify(expr)).isZero();
  }

  /**
   * Solves an equation which is not linear in its highest derivative by solving for that derivative
   * algebraically first, and then solving each of the resulting equations.
   *
   * <p>
   * This is what lets <code>y'(x) + x*y'(x)^2 == 1</code> be solved: reading a coefficient of
   * <code>y'(x)</code> off it produces the wrong answer <code>x + C(1)</code>, while solving the
   * quadratic for <code>y'(x)</code> gives two equations which are plain quadratures.
   *
   * @return {@link F#NIL} if the equation is linear in the highest derivative, if solving for it
   *         fails, or if none of the resulting equations can be solved
   */
  static IExpr solveForHighestDerivative(IExpr lhs, IExpr yFunction, IExpr xVar, int n, IExpr c_n,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (n < 1) {
      return F.NIL;
    }
    IExpr highest = engine.evaluate(F.D(yFunction, F.List(xVar, F.ZZ(n))));
    IExpr dummy = F.Dummy("d");
    IExpr substituted = engine.evaluate(F.ExpandAll(F.subst(lhs, highest, dummy)));
    IExpr linearPart = engine.evaluate(
        F.ExpandAll(F.Subtract(substituted, F.Plus(F.Coefficient(substituted, dummy, F.C0),
            F.Times(F.Coefficient(substituted, dummy, F.C1), dummy)))));
    if (linearPart.isZero()) {
      // Linear in the highest derivative, so the ordinary solvers already had their chance.
      return F.NIL;
    }
    IExpr solutions = engine.evaluate(F.Solve(F.Equal(substituted, F.C0), F.List(dummy)));
    IAST extracted = DSolveUtil.extractSolveResults(solutions);
    if (extracted.argSize() == 0) {
      return F.NIL;
    }
    IASTAppendable roots = F.ListAlloc(extracted.argSize());
    for (int i = 1; i <= extracted.argSize(); i++) {
      IExpr value = extracted.get(i);
      if (!value.isFree(dummy, true) || !value.isFree(yFunction.head(), true)) {
        continue;
      }
      IExpr reduced = F.Equal(F.Subtract(highest, value), F.C0);
      IExpr solved = solveSingleODE(reduced, xVar, F.list(yFunction), c_n, ctx);
      if (solved.isPresent()) {
        if (solved.isList()) {
          roots.appendArgs((IAST) solved);
        } else {
          roots.append(solved);
        }
      }
    }
    if (roots.argSize() == 0) {
      return F.NIL;
    }
    return roots.argSize() == 1 ? roots.arg1() : roots;
  }

  /**
   * Determines the constant of the first integral <code>y' == v(y)</code> from the initial
   * conditions <code>y(x0)</code> and <code>y'(x0)</code>, if the context carries both of them.
   *
   * @param vSol the first integral, an expression in <code>yDummy</code> containing
   *        <code>c_n</code>
   * @return the first integral with <code>c_n</code> replaced, the unchanged first integral when
   *         the conditions do not determine it, or {@link F#NIL} when they contradict this branch
   */
  private static IExpr fitFirstIntegral(IExpr vSol, IExpr yDummy, IExpr head, IExpr xVar, IExpr c_n,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (ctx.conditions.argSize() < 2 || vSol.isFree(c_n, true)) {
      return vSol;
    }
    IExpr point = conditionPoint(ctx.conditions, head, xVar, engine);
    if (point.isNIL()) {
      return vSol;
    }
    IExpr value = conditionValue(ctx.conditions, head, 0, point, engine);
    IExpr slope = conditionValue(ctx.conditions, head, 1, point, engine);
    if (value.isNIL() || slope.isNIL()) {
      return vSol;
    }
    IExpr atPoint = engine.evaluate(F.subst(vSol, yDummy, value));
    IExpr solved = engine.evaluate(F.Solve(F.Equal(atPoint, slope), F.List(c_n)));
    IAST extracted = DSolveUtil.extractSolveResults(solved);
    if (extracted.argSize() == 0) {
      // This branch of the first integral cannot meet the condition; another one may.
      return solved.isEmptyList() ? F.NIL : vSol;
    }
    IExpr fitted = engine.evaluate(F.subst(vSol, c_n, extracted.arg1()));
    // With the constant known the radical usually collapses, e.g. Sqrt(y^4) to y^2, and only then
    // is the remaining integration an elementary one.
    IExpr expanded = engine.evaluate(F.PowerExpand(fitted));
    return expanded.isPresent() && expanded.leafCount() <= fitted.leafCount() ? expanded : fitted;
  }

  /** The point the conditions are given at, or {@link F#NIL} if they do not agree on one. */
  private static IExpr conditionPoint(IAST conditions, IExpr head, IExpr xVar, EvalEngine engine) {
    IExpr point = F.NIL;
    for (int i = 1; i <= conditions.argSize(); i++) {
      IExpr found = applicationPoint(conditions.get(i), head, xVar);
      if (found.isPresent()) {
        if (point.isPresent() && !point.equals(found)) {
          return F.NIL;
        }
        point = found;
      }
    }
    return point;
  }

  /** The argument the unknown function is applied to inside a condition. */
  private static IExpr applicationPoint(IExpr expr, IExpr head, IExpr xVar) {
    if (!expr.isAST()) {
      return F.NIL;
    }
    IAST[] derivative = expr.isDerivativeAST1();
    if (derivative != null && derivative[2] != null && derivative[1].isAST1()
        && derivative[1].arg1().equals(head) && derivative[2].isAST1()) {
      return derivative[2].first().isFree(xVar) ? derivative[2].first() : F.NIL;
    }
    if (expr.isAST1() && expr.head().equals(head) && expr.first().isFree(xVar)) {
      return expr.first();
    }
    IAST ast = (IAST) expr;
    for (int i = 0; i < ast.size(); i++) {
      IExpr found = applicationPoint(ast.get(i), head, xVar);
      if (found.isPresent()) {
        return found;
      }
    }
    return F.NIL;
  }

  /**
   * The value the conditions prescribe for the <code>order</code>-th derivative at
   * <code>point</code>, or {@link F#NIL} if none of them does.
   */
  private static IExpr conditionValue(IAST conditions, IExpr head, int order, IExpr point,
      EvalEngine engine) {
    if (!head.isSymbol()) {
      return F.NIL;
    }
    IExpr term = org.matheclipse.core.eval.util.ODEUtils
        .derivative((org.matheclipse.core.interfaces.ISymbol) head, order, point);
    for (int i = 1; i <= conditions.argSize(); i++) {
      IExpr condition = conditions.get(i);
      if (condition.isFree(term, true)) {
        continue;
      }
      IExpr solved = engine.evaluate(F.Solve(F.Equal(condition, F.C0), F.List(term)));
      IAST extracted = DSolveUtil.extractSolveResults(solved);
      if (extracted.argSize() == 1 && extracted.arg1().isFree(term, true)) {
        return extracted.arg1();
      }
    }
    return F.NIL;
  }
}
