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
  static final int MAX_DERIVATIVE_ORDER = 10;

  /** How deep in the cascade the reduction of a Riccati equation is still attempted. */
  private static final int MAX_RICCATI_DEPTH = 3;

  /** How big the coefficient of the equation that reduction leaves may be. */
  private static final int MAX_RICCATI_LEAF_COUNT = 60;

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

    // IExpr diff = engine.evaluate(F.Simplify(F.Subtract(dMdy, dNdx)));
    IExpr diff = engine.evaluate(F.Subtract(dMdy, dNdx));

    if (diff.isZero()) {
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
    IExpr diff1 = engine.evaluate(F.Divide(F.Subtract(dMdy, dNdx), nDummy));

    if (diff1.isFree(yDummy)) {
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
    IExpr diff2 = engine.evaluate(F.Divide(F.Subtract(dNdx, dMdy), mDummy));

    if (diff2.isFree(x)) {
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
      fxExpr = S.Plus.of(engine, fxIntegral, C_1);
      if (!DSolveContext.isUsable(fxExpr)) {
        return F.NIL;
      }
      IExpr yEquation = S.Subtract.of(engine, gyExpr, fxExpr);
      IExpr result = Eliminate.extractVariable(yEquation, y, false, engine);
      if (result.isNIL()) {
        // The antiderivative is not always in a form the equation can be solved for y in. A sum
        // of logarithms is the usual case: Integrate answers 1/(1-y^2) with
        // -Log(1-y)/2 + Log(1+y)/2, which nothing here can invert, while the ArcTanh(y) it is
        // equal to inverts at once. So the integral is collected once and the equation offered
        // again, which is what makes y'(x) == (y(x)^2 + x*y(x) - x^2)/x^2 solvable.
        IExpr collected = engine.evaluate(F.FullSimplify(gyExpr));
        if (collected.isPresent() && !collected.equals(gyExpr)) {
          result = Eliminate.extractVariable(S.Subtract.of(engine, collected, fxExpr), y, false,
              engine);
        }
      }
      if (result.isNIL()) {
        result = solveSeparatedEquation(engine, yEquation, y);
      }
      if (result.isPresent()) {
        result = DSolveUtil.stripConditionalExpression(result);
        return engine.evaluate(result);
      }
    }
    return F.NIL;
  }

  /**
   * Solves the separated equation for <code>y</code> where {@link Eliminate#extractVariable} could
   * not, which is what an antiderivative that mixes a logarithm and a root needs.
   *
   * @return the single branch <code>Solve</code> answers with, or {@link F#NIL}
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
    IExpr root = DSolveUtil.stripConditionalExpression(roots.arg1());
    if (root.isNIL() || !DSolveContext.isUsable(root) || !root.isFree(y, true)) {
      return F.NIL;
    }
    return root;
  }

  static IExpr odeSolve(EvalEngine engine, IExpr w, IExpr x, IExpr y, IExpr C_1) {
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
      IExpr f = odeSeparable(engine, m, n, x, yVar, C_1);
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
  static IExpr applyUnaryBCs(IExpr root, IAST uFunction1Arg, IExpr xVar, IAST boundaryConditions,
      EvalEngine engine) {
    IExpr head = uFunction1Arg.head();
    IAST headRules = F.List(F.Rule(head, F.Function(F.List(xVar), root)));

    IASTAppendable evaluatedBCs = F.ListAlloc(boundaryConditions.argSize());
    for (int k = 1; k <= boundaryConditions.argSize(); k++) {
      IExpr evaluatedBC = engine.evaluate(F.subst(boundaryConditions.get(k), headRules));
      evaluatedBC = engine.evaluate(DSolveUtil.clearCorruptedIntegrals(evaluatedBC));
      evaluatedBCs.append(evaluatedBC);
    }

    // Use the guaranteed recursive constant extractor
    IASTAppendable cVars = F.ListAlloc();
    DSolveUtil.extractCVars(evaluatedBCs, cVars);

    if (cVars.argSize() == 0) {
      for (int k = 1; k <= evaluatedBCs.argSize(); k++) {
        IExpr bc = evaluatedBCs.get(k);
        if (bc.isEqual()) {
          if (!engine.evaluate(bc).isTrue())
            return F.NIL;
        } else if (!bc.isZero()) {
          return F.NIL;
        }
      }
      return root;
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
    if (cSols.isList() && ((IAST) cSols).argSize() > 0) {
      // Stripped like every other result Solve hands back here: inverting a periodic function
      // writes a whole number into the answer to choose a branch, and every value of it names the
      // same solution, so the principal one is taken rather than left standing where it reads as
      // a constant the conditions failed to determine.
      IAST cSol = (IAST) DSolveUtil.stripConditionalExpression(((IAST) cSols).arg1());
      IExpr fitted = DSolveUtil.togetherSolution(engine.evaluate(F.subst(root, cSol)), engine);
      return isFitted(fitted, cVars) ? fitted : F.NIL;
    }
    return F.NIL;
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

    IExpr aPrime = engine.evaluate(F.D(a, xVar));
    IExpr coeffUPrime = engine.evaluate(F.Subtract(b, F.Divide(aPrime, a)));

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
        LinearODEForm normalized = ctx.depth() == 1 ? lf.normalized(xVar, engine) : lf;

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
        }

        if (temp.isPresent()) {
          // Wrap in a list if it's a single root to uniformize processing
          IAST roots = temp.makeList();
          IASTAppendable resultList = F.ListAlloc();
          boolean bcUnsatisfiable = false;

          for (int r = 1; r <= roots.argSize(); r++) {
            IExpr root = roots.get(r);
            root = DSolveUtil.stripConditionalExpression(root);
            // root = engine.evaluate(F.Simplify(root));
            root = DSolveUtil.absorbConstants(root, F.list(c_n), false, engine);

            if (!DSolveVerify.acceptODE(listOfEquations, uFunction1Arg, xVar, root, engine)) {
              // The equation was recognized by a method it does not actually belong to. Putting
              // the answer back into it is what catches that.
              continue;
            }

            if (boundaryConditions.argSize() > 0) {
              root = applyUnaryBCs(root, uFunction1Arg, xVar, boundaryConditions, engine);
              if (!root.isPresent()) {
                // Skip this root branch if the BCs cannot be satisfied. A general solution with
                // more than one branch normally has branches which the conditions rule out, so this
                // is only worth reporting once none of them is left.
                bcUnsatisfiable = true;
                continue;
              }
            }

            if (arg2.isSymbol() && xVar.isSymbol()) {
              resultList.append(F.list(F.Rule(arg2, F.Function(F.list(xVar), root))));
            } else {
              resultList.append(F.list(F.Rule(arg2, root)));
            }
          }

          if (resultList.argSize() > 0) {
            return resultList;
          }
          if (bcUnsatisfiable) {
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
    if (!engine.evaluate(F.Expand(F.subst(polynomial, assignment))).isZero()) {
      return F.NIL;
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
