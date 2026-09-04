package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.SolveUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The partial differential equation solvers of {@link DSolve}.
 */
final class DSolvePDE {

  private DSolvePDE() {}

  /** The symbol standing for u, its first derivatives and its second derivatives. */
  private static final int U = 0, P = 1, Q = 2, PP = 3, PQ = 4, QQ = 5;

  /**
   * Solves a partial differential equation in two independent variables.
   *
   * <p>
   * The order the methods are tried in carries meaning. An equation which is quasi-linear may also
   * be a Clairaut equation with a linear <code>f</code>, and it has to receive the general solution
   * with its arbitrary function rather than the two parameter complete integral, so the linear and
   * quasi-linear methods come first. Charpit's method comes after Clairaut's because Clairaut's
   * gives the same family in a much simpler form.
   *
   * @param equation the equation, or an expression which is meant to be zero
   * @param uFunc the unknown, either <code>u</code> or <code>u(x,y)</code>
   * @param xVars the two independent variables
   */
  static IExpr solvePDE(IExpr equation, IExpr uFunc, IAST xVars, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (xVars.argSize() != 2) {
      return F.NIL;
    }
    IExpr x = xVars.arg1();
    IExpr y = xVars.arg2();
    if (!x.isSymbol() || !y.isSymbol()) {
      return F.NIL;
    }
    IExpr uApplied = uFunc.isAST() ? uFunc : F.binaryAST2(uFunc, x, y);

    IExpr lhs = equation;
    if (equation.isEqual()) {
      lhs = S.Subtract.of(engine, equation.first(), equation.second());
    }
    lhs = engine.evaluate(F.ExpandAll(lhs));

    int order = partialOrder(lhs, uApplied.head(), engine);
    IExpr body = F.NIL;
    // A method which declines has usually taken arbitrary constants out of the counter already, so
    // it is reset before the next one is tried and the answer starts at C(1).
    int counter = engine.getConstantCounter();
    if (order == 1) {
      body = pdeCharacteristics(lhs, uApplied, x, y, ctx);
      if (body.isNIL()) {
        engine.setConstantCounter(counter);
        body = pdeSingleDerivative(lhs, uApplied, x, y, ctx);
      }
      if (body.isNIL()) {
        engine.setConstantCounter(counter);
        body = pdeSingleDerivative(lhs, uApplied, y, x, ctx);
      }
      if (body.isNIL()) {
        engine.setConstantCounter(counter);
        body = pdeClairaut(lhs, uApplied, x, y, ctx);
      }
      if (body.isNIL()) {
        engine.setConstantCounter(counter);
        body = pdeCharpit(lhs, uApplied, x, y, ctx);
      }
    } else if (order == 2) {
      body = pdeSecondOrderConstant(lhs, uApplied, x, y, ctx);
    }
    if (body.isNIL()) {
      return F.NIL;
    }
    if (ctx.conditions.argSize() > 0) {
      if (body.isList()) {
        return F.NIL;
      }
      body = applyPDEConditions(body, uApplied, x, y, ctx);
      if (body.isNIL()) {
        return F.NIL;
      }
    }
    // A complete integral may come in several branches, one per root of the equation for the
    // second derivative, and each of them is a solution in its own right.
    IAST branches = body.makeList();
    IASTAppendable result = F.ListAlloc(branches.argSize());
    for (int i = 1; i <= branches.argSize(); i++) {
      IExpr branch = branches.get(i);
      if (!DSolveVerify.acceptPDE(F.list(lhs), uApplied, xVars, branch, engine)) {
        continue;
      }
      if (uFunc.isSymbol()) {
        result.append(F.List(F.Rule(uFunc, F.Function(F.List(x, y), branch))));
      } else {
        result.append(F.List(F.Rule(uFunc, branch)));
      }
    }
    return result.argSize() > 0 ? result : F.NIL;
  }

  /** Whether the expression differentiates the given function, i.e. is an equation and not a condition. */
  static boolean differentiates(IExpr expr, IExpr head) {
    if (!expr.isAST()) {
      return false;
    }
    IAST ast = (IAST) expr;
    if (ast.head().isAST() && ((IAST) ast.head()).head().isAST(S.Derivative)
        && ((IAST) ast.head()).isAST1() && ((IAST) ast.head()).arg1().equals(head)) {
      return true;
    }
    for (int i = 0; i < ast.size(); i++) {
      if (differentiates(ast.get(i), head)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Determines the arbitrary function of a general solution from a condition which prescribes the
   * values of the unknown along a line where one of the variables is constant.
   *
   * <p>
   * Writing the general solution as <code>S(v1, v2, C(1)(g(v1,v2)))</code> and the condition as
   * <code>u(v1, k) == f(v1)</code>, the argument of the arbitrary function along that line is
   * <code>g(v1, k)</code>. Solving it for <code>v1</code> expresses the line in terms of that
   * argument, and the condition then determines the value of the arbitrary function there.
   *
   * @return the solution with the arbitrary function determined, or {@link F#NIL} if the condition
   *         is not of that shape or does not determine it
   */
  private static IExpr applyPDEConditions(IExpr body, IExpr uApplied, IExpr x, IExpr y,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (ctx.conditions.argSize() != 1) {
      return F.NIL;
    }
    IExpr head = uApplied.head();
    IExpr condition = ctx.conditions.arg1();
    IExpr value = F.NIL;
    IExpr applied = F.NIL;
    if (condition.isEqual() && condition.first().isAST(head, 3)) {
      applied = condition.first();
      value = condition.second();
    } else if (condition.isEqual() && condition.second().isAST(head, 3)) {
      applied = condition.second();
      value = condition.first();
    }
    if (applied.isNIL()) {
      return F.NIL;
    }
    // Exactly one of the two arguments has to be held fixed.
    IExpr firstArgument = ((IAST) applied).arg1();
    IExpr secondArgument = ((IAST) applied).arg2();
    IExpr fixedVariable;
    IExpr fixedValue;
    IExpr freeVariable;
    if (firstArgument.equals(x) && secondArgument.isFree(x) && secondArgument.isFree(y)) {
      fixedVariable = y;
      fixedValue = secondArgument;
      freeVariable = x;
    } else if (secondArgument.equals(y) && firstArgument.isFree(x) && firstArgument.isFree(y)) {
      fixedVariable = x;
      fixedValue = firstArgument;
      freeVariable = y;
    } else {
      return F.NIL;
    }

    // The one arbitrary function of the general solution, and the argument it is applied to.
    IExpr arbitrary = findArbitraryFunction(body);
    if (arbitrary.isNIL()) {
      return F.NIL;
    }
    IExpr constant = arbitrary.head();
    IExpr argument = ((IAST) arbitrary).arg1();

    IExpr s = F.Dummy("s");
    IExpr argumentOnLine = engine.evaluate(F.subst(argument, fixedVariable, fixedValue));
    IExpr inverted = engine.evaluate(F.Solve(F.Equal(argumentOnLine, s), F.List(freeVariable)));
    IAST invertedValues = DSolveUtil.extractSolveResults(inverted);
    if (invertedValues.argSize() == 0) {
      return F.NIL;
    }
    IExpr line = invertedValues.arg1();
    if (!line.isFree(freeVariable, true)) {
      return F.NIL;
    }

    IExpr onLine = engine.evaluate(F.subst(body, fixedVariable, fixedValue));
    IExpr residual = engine.evaluate(F.Subtract(onLine, F.subst(value, fixedVariable, fixedValue)));
    residual = engine.evaluate(F.subst(residual, freeVariable, line));
    // Solve does not accept C(1)(s) as an unknown, because C is a protected symbol, so the value
    // of the arbitrary function is solved for under a plain name.
    IExpr unknown = F.Dummy("w");
    residual = engine.evaluate(F.subst(residual, F.unaryAST1(constant, s), unknown));
    if (!residual.isFree(constant, true)) {
      return F.NIL;
    }
    IExpr solved = engine.evaluate(F.Solve(F.Equal(residual, F.C0), F.List(unknown)));
    IAST values = DSolveUtil.extractSolveResults(solved);
    if (values.argSize() == 0 || !values.arg1().isFree(unknown, true)) {
      return F.NIL;
    }
    IExpr determined = engine.evaluate(
        F.subst(body, constant, F.Function(F.List(s), values.arg1())));
    return engine.evaluate(F.Simplify(determined));
  }

  /** The first arbitrary function <code>C(k)(...)</code> of a general solution. */
  private static IExpr findArbitraryFunction(IExpr expr) {
    if (!expr.isAST()) {
      return F.NIL;
    }
    IAST ast = (IAST) expr;
    if (ast.isAST1() && ast.head().isAST(S.C, 2)) {
      return ast;
    }
    for (int i = 0; i < ast.size(); i++) {
      IExpr found = findArbitraryFunction(ast.get(i));
      if (found.isPresent()) {
        return found;
      }
    }
    return F.NIL;
  }

  /** The highest total order in which the unknown is differentiated. */
  private static int partialOrder(IExpr expr, IExpr head, EvalEngine engine) {
    if (!expr.isAST()) {
      return -1;
    }
    IAST ast = (IAST) expr;
    if (ast.head().isAST() && ((IAST) ast.head()).head().isAST(S.Derivative)
        && ((IAST) ast.head()).isAST1() && ((IAST) ast.head()).arg1().equals(head)) {
      IAST derivative = (IAST) ((IAST) ast.head()).head();
      int total = 0;
      for (int i = 1; i <= derivative.argSize(); i++) {
        int part = derivative.get(i).toIntDefault();
        if (!F.isPresent(part)) {
          return -1;
        }
        total += part;
      }
      return total;
    }
    if (ast.head().equals(head)) {
      return 0;
    }
    int max = -1;
    for (int i = 0; i < ast.size(); i++) {
      int found = partialOrder(ast.get(i), head, engine);
      if (found > max) {
        max = found;
      }
    }
    return max;
  }

  /**
   * The equation with <code>u</code> and its derivatives replaced by plain symbols, so that it can
   * be differentiated with respect to them and solved for them as an algebraic expression.
   *
   * @param symbols filled with the symbols standing for u, u_x, u_y, u_xx, u_xy and u_yy
   */
  private static IExpr toAlgebraic(IExpr lhs, IExpr uApplied, IExpr x, IExpr y, IExpr[] symbols,
      EvalEngine engine) {
    symbols[U] = F.Dummy("pdeU");
    symbols[P] = F.Dummy("pdeP");
    symbols[Q] = F.Dummy("pdeQ");
    symbols[PP] = F.Dummy("pdePP");
    symbols[PQ] = F.Dummy("pdePQ");
    symbols[QQ] = F.Dummy("pdeQQ");
    IExpr result = lhs;
    // Highest order first, so that replacing a lower one cannot destroy a higher one.
    result = F.subst(result, engine.evaluate(F.D(uApplied, F.List(x, F.C2))), symbols[PP]);
    result = F.subst(result, engine.evaluate(F.D(uApplied, F.List(x, F.C1), F.List(y, F.C1))), symbols[PQ]);
    result = F.subst(result, engine.evaluate(F.D(uApplied, F.List(y, F.C2))), symbols[QQ]);
    result = F.subst(result, engine.evaluate(F.D(uApplied, x)), symbols[P]);
    result = F.subst(result, engine.evaluate(F.D(uApplied, y)), symbols[Q]);
    result = F.subst(result, uApplied, symbols[U]);
    result = engine.evaluate(F.ExpandAll(result));
    return result.isFree(uApplied.head(), true) ? result : F.NIL;
  }

  /**
   * Solves an equation in which only one of the two first derivatives occurs, by treating the other
   * variable as a parameter and solving the resulting ordinary differential equation.
   *
   * <p>
   * The constant of that integration is a constant along the characteristic only, so it becomes an
   * arbitrary function of the parameter: <code>D(u(x,y),x) == 1</code> has the general solution
   * <code>x + C(1)(y)</code>, not <code>x + C(1)</code>.
   *
   * @param solveVar the variable which is differentiated
   * @param paramVar the variable which is held fixed
   */
  private static IExpr pdeSingleDerivative(IExpr lhs, IExpr uApplied, IExpr solveVar,
      IExpr paramVar, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr otherDerivative = engine.evaluate(F.D(uApplied, paramVar));
    if (!lhs.isFree(otherDerivative, true)) {
      return F.NIL;
    }
    IExpr wDummy = F.Dummy("w");
    IExpr w = F.unaryAST1(wDummy, solveVar);
    IExpr converted = F.subst(lhs, engine.evaluate(F.D(uApplied, solveVar)),
        engine.evaluate(F.D(w, solveVar)));
    converted = F.subst(converted, uApplied, w);
    if (!converted.isFree(uApplied.head(), true)) {
      return F.NIL;
    }
    IExpr constant = ctx.nextConstant();
    IExpr solved = DSolveODE.solveSingleODE(F.Equal(converted, F.C0), solveVar, F.List(w), constant,
        ctx);
    if (solved.isNIL()) {
      solved = DSolveODE.odeSolve(engine, F.Equal(converted, F.C0), solveVar, w, constant);
    }
    if (solved.isNIL()) {
      return F.NIL;
    }
    IExpr body = solved.isList() ? ((IAST) solved).arg1() : solved;
    if (body.isRule()) {
      body = ((IAST) body).second();
    }
    if (body.isFree(constant, true)) {
      return F.NIL;
    }
    return engine.evaluate(F.subst(body, constant, F.unaryAST1(constant, paramVar)));
  }

  /**
   * The complete integral of a Clairaut equation <code>u == x*u_x + y*u_y + f(u_x,u_y)</code>,
   * which is <code>C(1)*x + C(2)*y + f(C(1),C(2))</code>.
   */
  private static IExpr pdeClairaut(IExpr lhs, IExpr uApplied, IExpr x, IExpr y,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr[] symbols = new IExpr[6];
    IExpr algebraic = toAlgebraic(lhs, uApplied, x, y, symbols, engine);
    if (algebraic.isNIL() || !isNonlinearInDerivatives(algebraic, symbols, engine)) {
      return F.NIL;
    }
    IExpr clairautPart = F.Plus(F.Negate(symbols[U]), F.Times(x, symbols[P]),
        F.Times(y, symbols[Q]));
    for (int sign = 0; sign < 2; sign++) {
      IExpr rest = engine.evaluate(F.ExpandAll(sign == 0 //
          ? F.Plus(algebraic, clairautPart)
          : F.Subtract(algebraic, clairautPart)));
      if (rest.isFree(x) && rest.isFree(y) && rest.isFree(symbols[U], true)) {
        IExpr c1 = ctx.nextConstant();
        IExpr c2 = ctx.nextConstant();
        IExpr f = engine.evaluate(
            F.subst(rest, F.List(F.Rule(symbols[P], c1), F.Rule(symbols[Q], c2))));
        // For sign 0 the equation reads u == x*u_x + y*u_y - rest, for sign 1 it reads
        // u == x*u_x + y*u_y + rest.
        IExpr body = F.Plus(F.Times(c1, x), F.Times(c2, y), sign == 0 ? F.Negate(f) : f);
        ctx.addMessage("nlpde", F.CEmptyList);
        return engine.evaluate(F.Expand(body));
      }
    }
    return F.NIL;
  }

  /**
   * A complete integral of a nonlinear first-order equation by Charpit's method, for the three
   * standard forms in which the auxiliary system has an immediate first integral.
   *
   * <p>
   * A complete integral is a family with two parameters rather than the general solution with its
   * arbitrary function, which is why a message is shown alongside it.
   */
  private static IExpr pdeCharpit(IExpr lhs, IExpr uApplied, IExpr x, IExpr y, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr[] symbols = new IExpr[6];
    IExpr f = toAlgebraic(lhs, uApplied, x, y, symbols, engine);
    if (f.isNIL() || !isNonlinearInDerivatives(f, symbols, engine)) {
      return F.NIL;
    }
    boolean freeOfX = f.isFree(x);
    boolean freeOfY = f.isFree(y);
    boolean freeOfU = f.isFree(symbols[U], true);

    IExpr body = F.NIL;
    if (freeOfX && freeOfY && freeOfU) {
      body = charpitDerivativesOnly(f, symbols, x, y, ctx);
    } else if (freeOfX && freeOfY) {
      body = charpitWithoutVariables(f, symbols, x, y, ctx);
    } else if (freeOfU) {
      body = charpitSeparable(f, symbols, x, y, ctx);
    }
    if (body.isPresent()) {
      ctx.addMessage("nlpde", F.CEmptyList);
    }
    return body;
  }

  /**
   * Charpit for <code>F(u_x,u_y) == 0</code>. Both derivatives are constant along a characteristic,
   * so <code>u_x == C(1)</code> and <code>u_y</code> is whatever the equation makes of it.
   */
  private static IExpr charpitDerivativesOnly(IExpr f, IExpr[] symbols, IExpr x, IExpr y,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr c1 = ctx.nextConstant();
    IExpr c2 = ctx.nextConstant();
    IExpr substituted = engine.evaluate(F.subst(f, symbols[P], c1));
    IExpr solutions = engine.evaluate(F.Solve(F.Equal(substituted, F.C0), F.List(symbols[Q])));
    IAST values = DSolveUtil.extractSolveResults(solutions);
    IASTAppendable branches = F.ListAlloc(values.argSize());
    for (int i = 1; i <= values.argSize(); i++) {
      IExpr q = values.get(i);
      if (!q.isFree(symbols[Q], true) || !q.isFree(symbols[P], true)) {
        continue;
      }
      branches.append(engine.evaluate(F.Expand(F.Plus(F.Times(c1, x), F.Times(q, y), c2))));
    }
    if (branches.argSize() == 0) {
      return F.NIL;
    }
    return branches.argSize() == 1 ? branches.arg1() : branches;
  }

  /**
   * Charpit for <code>F(u,u_x,u_y) == 0</code>. Charpit's equations reduce to
   * <code>du_x/u_x == du_y/u_y</code>, so <code>u_y == C(1)*u_x</code>, and the equation becomes a
   * quadrature in <code>u</code>.
   */
  private static IExpr charpitWithoutVariables(IExpr f, IExpr[] symbols, IExpr x, IExpr y,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr c1 = ctx.nextConstant();
    IExpr c2 = ctx.nextConstant();
    IExpr substituted = engine.evaluate(F.subst(f, symbols[Q], F.Times(c1, symbols[P])));
    IExpr solutions = engine.evaluate(F.Solve(F.Equal(substituted, F.C0), F.List(symbols[P])));
    IAST values = DSolveUtil.extractSolveResults(solutions);
    for (int i = 1; i <= values.argSize(); i++) {
      IExpr p = values.get(i);
      if (p.isZero() || !p.isFree(symbols[P], true) || !p.isFree(symbols[Q], true)) {
        continue;
      }
      IExpr integral = ctx.integrate(F.Divide(F.C1, p), symbols[U]);
      if (integral.isNIL()) {
        continue;
      }
      // Integrate(1/P, u) == x + C(1)*y + C(2) is the complete integral in implicit form.
      IExpr relation = F.Equal(integral, F.Plus(x, F.Times(c1, y), c2));
      IExpr explicit = engine.evaluate(F.Solve(relation, F.List(symbols[U])));
      IAST explicitValues = DSolveUtil.extractSolveResults(explicit);
      if (explicitValues.argSize() > 0 && explicitValues.arg1().isFree(symbols[U], true)) {
        return explicitValues.arg1();
      }
    }
    return F.NIL;
  }

  /**
   * Charpit for a separable <code>f(x,u_x) == g(y,u_y)</code>, recognized by the four mixed second
   * derivatives across the two groups vanishing. Each side equals a constant, which leaves one
   * quadrature per variable.
   */
  private static IExpr charpitSeparable(IExpr f, IExpr[] symbols, IExpr x, IExpr y,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr[] first = new IExpr[] {x, symbols[P]};
    IExpr[] second = new IExpr[] {y, symbols[Q]};
    for (IExpr a : first) {
      for (IExpr b : second) {
        if (!engine.evaluate(F.D(F.D(f, a), b)).isZero()) {
          return F.NIL;
        }
      }
    }
    IAST toZeroSecond = F.List(F.Rule(y, F.C0), F.Rule(symbols[Q], F.C0));
    IAST toZeroFirst = F.List(F.Rule(x, F.C0), F.Rule(symbols[P], F.C0));
    IExpr fPart = engine.evaluate(F.subst(f, toZeroSecond));
    IExpr gPart = engine.evaluate(F.subst(f, toZeroFirst));
    // Subtracting the value at the reference point keeps fPart + gPart == reference equivalent to
    // the equation itself when it has a constant term.
    IExpr reference = engine.evaluate(F.subst(gPart, toZeroSecond));

    IExpr c1 = ctx.nextConstant();
    IExpr c2 = ctx.nextConstant();
    IExpr pSolutions = engine.evaluate(F.Solve(F.Equal(fPart, c1), F.List(symbols[P])));
    IExpr qSolutions = engine
        .evaluate(F.Solve(F.Equal(gPart, F.Subtract(reference, c1)), F.List(symbols[Q])));
    IAST pValues = DSolveUtil.extractSolveResults(pSolutions);
    IAST qValues = DSolveUtil.extractSolveResults(qSolutions);
    if (pValues.argSize() == 0 || qValues.argSize() == 0) {
      return F.NIL;
    }
    IExpr qIntegral = ctx.integrate(qValues.arg1(), y);
    if (qIntegral.isNIL() || !qIntegral.isFree(symbols[Q], true)) {
      return F.NIL;
    }
    IASTAppendable branches = F.ListAlloc(pValues.argSize());
    for (int i = 1; i <= pValues.argSize(); i++) {
      IExpr pIntegral = ctx.integrate(pValues.get(i), x);
      if (pIntegral.isNIL() || !pIntegral.isFree(symbols[P], true)) {
        continue;
      }
      branches.append(engine.evaluate(F.Plus(pIntegral, qIntegral, c2)));
    }
    if (branches.argSize() == 0) {
      return F.NIL;
    }
    return branches.argSize() == 1 ? branches.arg1() : branches;
  }

  /** Whether the equation contains a derivative of the unknown other than in the first power. */
  private static boolean isNonlinearInDerivatives(IExpr f, IExpr[] symbols, EvalEngine engine) {
    IExpr dp = engine.evaluate(F.D(f, symbols[P]));
    IExpr dq = engine.evaluate(F.D(f, symbols[Q]));
    return !dp.isFree(symbols[P], true) || !dp.isFree(symbols[Q], true)
        || !dq.isFree(symbols[P], true) || !dq.isFree(symbols[Q], true);
  }

  /**
   * The general solution of a homogeneous linear second-order equation with constant coefficients.
   *
   * <p>
   * Substituting <code>u == f(lambda*x + y)</code> into the principal part turns it into
   * <code>a*lambda^2 + b*lambda + c == 0</code>, so each root contributes an arbitrary function of
   * <code>lambda*x + y</code>. A repeated root contributes <code>x</code> times the second one, as
   * a repeated root of a characteristic polynomial does for an ordinary equation. Lower order terms
   * are carried by an exponential factor, and only when the operator factors.
   */
  private static IExpr pdeSecondOrderConstant(IExpr lhs, IExpr uApplied, IExpr x, IExpr y,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr[] symbols = new IExpr[6];
    IExpr f = toAlgebraic(lhs, uApplied, x, y, symbols, engine);
    if (f.isNIL()) {
      return F.NIL;
    }
    IExpr[] coefficients = new IExpr[6];
    IExpr rest = f;
    for (int i = 0; i < 6; i++) {
      coefficients[i] = engine.evaluate(F.Coefficient(rest, symbols[i]));
      rest = engine.evaluate(F.ExpandAll(F.Subtract(rest, F.Times(coefficients[i], symbols[i]))));
    }
    // Constant coefficients, no forcing, and nothing left which is not accounted for.
    if (!rest.isZero()) {
      return F.NIL;
    }
    for (int i = 0; i < 6; i++) {
      if (!coefficients[i].isFree(x) || !coefficients[i].isFree(y)) {
        return F.NIL;
      }
      for (int j = 0; j < 6; j++) {
        if (!coefficients[i].isFree(symbols[j], true)) {
          return F.NIL;
        }
      }
    }
    IExpr a = coefficients[PP];
    IExpr b = coefficients[PQ];
    IExpr c = coefficients[QQ];
    IExpr d = coefficients[P];
    IExpr e = coefficients[Q];
    IExpr g = coefficients[U];

    if (a.isZero() && c.isZero()) {
      return pdeMixedSecondOrder(b, d, e, g, x, y, ctx);
    }
    if (a.isZero()) {
      // Swapping the variables turns the missing leading coefficient into the other one.
      return pdeSecondOrderRoots(c, b, a, e, d, g, y, x, ctx);
    }
    return pdeSecondOrderRoots(a, b, c, d, e, g, x, y, ctx);
  }

  /** The factored solution of an equation whose principal part is the mixed derivative alone. */
  private static IExpr pdeMixedSecondOrder(IExpr b, IExpr d, IExpr e, IExpr g, IExpr x, IExpr y,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (b.isZero()) {
      return F.NIL;
    }
    // b*(D_x + e/b)(D_y + d/b) u == 0 holds exactly when g == d*e/b.
    if (!DSolveODE.isVanishing(engine.evaluate(F.Subtract(g, F.Divide(F.Times(d, e), b))), engine)) {
      return F.NIL;
    }
    IExpr c1 = ctx.nextConstant();
    IExpr c2 = ctx.nextConstant();
    IExpr first = F.Times(F.Exp(F.Times(F.Negate(F.Divide(e, b)), x)), F.unaryAST1(c1, y));
    IExpr second = F.Times(F.Exp(F.Times(F.Negate(F.Divide(d, b)), y)), F.unaryAST1(c2, x));
    return engine.evaluate(F.Plus(first, second));
  }

  /** The solution built from the roots of the principal part. */
  private static IExpr pdeSecondOrderRoots(IExpr a, IExpr b, IExpr c, IExpr d, IExpr e, IExpr g,
      IExpr v1, IExpr v2, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr lambda = F.Dummy("lambda");
    IExpr characteristic = F.Plus(F.Times(a, F.Sqr(lambda)), F.Times(b, lambda), c);
    IExpr solved = engine.evaluate(F.Roots(F.Equal(characteristic, F.C0), lambda));
    IAST equations = solved.isOr() ? (IAST) solved : F.Or(solved);
    IASTAppendable roots = F.ListAlloc(2);
    for (int i = 1; i <= equations.argSize(); i++) {
      IExpr equation = equations.get(i);
      if (!equation.isEqual() || !equation.first().equals(lambda)
          || !equation.second().isFree(S.Root, true)) {
        return F.NIL;
      }
      roots.append(equation.second());
    }
    IExpr c1 = ctx.nextConstant();
    IExpr c2 = ctx.nextConstant();
    if (roots.argSize() == 2) {
      IExpr l1 = roots.arg1();
      IExpr l2 = roots.arg2();
      IExpr m1 = engine.evaluate(F.Simplify(F.Divide(F.Negate(F.Plus(e, F.Times(l1, d))),
          F.Times(a, F.Subtract(l2, l1)))));
      IExpr m2 = engine.evaluate(F.Simplify(F.Subtract(F.Divide(d, a), m1)));
      if (!DSolveODE.isVanishing(
          engine.evaluate(F.Subtract(F.Times(m1, m2), F.Divide(g, a))), engine)) {
        return F.NIL;
      }
      return engine.evaluate(F.Plus(dampedTerm(c1, m1, l1, v1, v2), dampedTerm(c2, m2, l2, v1, v2)));
    }
    if (roots.argSize() == 1) {
      IExpr l = roots.arg1();
      if (!DSolveODE.isVanishing(engine.evaluate(F.Plus(e, F.Times(l, d))), engine)) {
        return F.NIL;
      }
      IExpr m = F.Dummy("m");
      IExpr inner = F.Plus(F.Sqr(m), F.Times(F.Negate(F.Divide(d, a)), m), F.Divide(g, a));
      IExpr innerSolved = engine.evaluate(F.Roots(F.Equal(inner, F.C0), m));
      IAST innerEquations = innerSolved.isOr() ? (IAST) innerSolved : F.Or(innerSolved);
      if (innerEquations.argSize() != 1 || !innerEquations.arg1().isEqual()) {
        return F.NIL;
      }
      IExpr mValue = innerEquations.arg1().second();
      if (!mValue.isFree(S.Root, true)) {
        return F.NIL;
      }
      IExpr argument = engine.evaluate(F.Plus(F.Times(l, v1), v2));
      IExpr factor = engine.evaluate(F.Exp(F.Times(F.Negate(mValue), v1)));
      return engine.evaluate(F.Times(factor,
          F.Plus(F.unaryAST1(c1, argument), F.Times(v1, F.unaryAST1(c2, argument)))));
    }
    return F.NIL;
  }

  /** One factor of the solution: the kernel of a single first-order operator. */
  private static IExpr dampedTerm(IExpr constant, IExpr m, IExpr lambda, IExpr v1, IExpr v2) {
    IExpr argument = F.Plus(F.Times(lambda, v1), v2);
    if (m.isZero()) {
      return F.unaryAST1(constant, argument);
    }
    return F.Times(F.Exp(F.Times(F.Negate(m), v1)), F.unaryAST1(constant, argument));
  }

  /**
   * Solves a first-order linear or quasi-linear equation by the method of characteristics.
   *
   * @return the solution as an expression in the independent variables, or {@link F#NIL}
   */
  private static IExpr pdeCharacteristics(IExpr lhs, IExpr uApplied, IExpr x, IExpr y,
      DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr ux = engine.evaluate(F.D(uApplied, x));
    IExpr uy = engine.evaluate(F.D(uApplied, y));

    IExpr A = engine.evaluate(F.Coefficient(lhs, ux));
    IExpr B = engine.evaluate(F.Coefficient(lhs, uy));

    if (A.isZero() || B.isZero()) {
      return F.NIL;
    }

    IExpr rest = engine.evaluate(F.Subtract(lhs, F.Plus(F.Times(A, ux), F.Times(B, uy))));

    if (!A.isFree(ux) || !A.isFree(uy) || !B.isFree(ux) || !B.isFree(uy)) {
      return F.NIL;
    }

    // Check if the characteristic equation dy/dx = B/A depends on u
    // If B/A perfectly cancels u (as in quasi-linear fractional equations), we can decouple it.
    IExpr ratioAB = engine.evaluate(F.Simplify(F.Divide(B, A)));
    if (!ratioAB.isFree(uApplied)) {
      return F.NIL;
    }

    IExpr yDummy = F.Dummy("y");
    IExpr yFuncX = F.unaryAST1(yDummy, x);
    IExpr yPrimeX = F.D(yFuncX, x);

    // Formulate normalized characteristic equation: y'(x) - B/A = 0
    IExpr ratio_sub = engine.evaluate(F.subst(ratioAB, y, yFuncX));
    IExpr charEq = F.Equal(F.Subtract(yPrimeX, ratio_sub), F.C0);
    IExpr C_1 = F.C(engine.incConstantCounter());

    IExpr charSolList = DSolveODE.solveSingleODE(charEq, x, F.List(yFuncX), C_1, ctx);
    if (charSolList.isNIL()) {
      charSolList = DSolveODE.odeSolve(engine, charEq, x, yFuncX, C_1);
    }

    if (charSolList.isPresent()) {
      IExpr yxExpr = charSolList.isList() ? ((IAST) charSolList).arg1() : charSolList;
      if (yxExpr.isRule()) {
        yxExpr = ((IAST) yxExpr).second();
      }

      // Solve for the characteristic constant: C_1 = g(x,y)
      IExpr eqForC1 = F.Equal(y, yxExpr);
      IExpr c1Sol = engine.evaluate(F.Solve(eqForC1, F.List(C_1)));
      IAST c1Results = DSolveUtil.extractSolveResults(c1Sol);
      if (c1Results.argSize() > 0) {
        IExpr g_xy = c1Results.arg1();

        // Prepare dummy variables for the 1D characteristic ODE
        IExpr uDummy = F.Dummy("u");
        IExpr uFuncX = F.unaryAST1(uDummy, x);
        IExpr uPrimeX = F.D(uFuncX, x);

        // Formulate the ODE for u: du/dx = -rest / A
        IExpr uRatio = engine.evaluate(F.Simplify(F.Divide(F.Negate(rest), A)));

        // Substitute u(x,y) -> u(x) BEFORE substituting y -> y(x).
        // This securely maps factors like `u` generated by Simplify into the 1D curve function.
        IExpr uRatio_u = F.subst(uRatio, uApplied, uFuncX);

        // Transform along the characteristic curve y -> y(x)
        IExpr uRatio_sub = engine.evaluate(F.subst(uRatio_u, y, yxExpr));

        IExpr uEq = F.Equal(F.Subtract(uPrimeX, uRatio_sub), F.C0);
        try {
          IExpr C_2 = F.C(engine.incConstantCounter());

          IExpr uSolList = DSolveODE.solveSingleODE(uEq, x, F.List(uFuncX), C_2, ctx);
          if (uSolList.isNIL()) {
            uSolList = DSolveODE.odeSolve(engine, uEq, x, uFuncX, C_2);
          }

          if (uSolList.isPresent()) {
            IExpr u_sol_x = uSolList.isList() ? ((IAST) uSolList).arg1() : uSolList;
            if (u_sol_x.isRule()) {
              u_sol_x = ((IAST) u_sol_x).second();
            }

            // Substitute the curve definitions back to form the generalized arbitrary function
            IExpr final_u = engine.evaluate(F.subst(u_sol_x, C_1, g_xy));

            IExpr arbFunc = F.unaryAST1(C_1, g_xy);
            final_u = engine.evaluate(F.subst(final_u, C_2, arbFunc));

            return final_u;
          }
        } finally {
          engine.decConstantCounter();
        }
      }
    }

    return F.NIL;
  }

  /**
   * Solves a system of decoupled first-order Partial Differential Equations by solving each
   * equation independently using the Method of Characteristics. Each equation must involve exactly
   * one unknown function from the target list.
   *
   * @param equations the list of PDE equations
   * @param funcList the list of unknown functions (e.g., {@code {u(x,y), v(x,y)}} or
   *        {@code {u, v}})
   * @param xVars the list of independent variables (e.g., {@code {x, y}})
   * @param engine the evaluation engine
   * @return the combined solution list, or {@code F.NIL} if the system cannot be solved
   */
  static IExpr solveSystemPDE(IAST equations, IAST funcList, IAST xVars, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    int numEqs = equations.argSize();
    int numFuncs = funcList.argSize();
    if (numEqs != numFuncs || numEqs == 0) {
      return F.NIL;
    }

    // Collect function heads for identifying which equation involves which function
    IExpr[] funcHeads = new IExpr[numFuncs];
    for (int i = 0; i < numFuncs; i++) {
      IExpr f = funcList.get(i + 1);
      funcHeads[i] = f.isAST() ? f.head() : f;
    }

    // Strategy: Decoupled system — each equation involves exactly one unknown function.
    // For each function, find the unique equation that involves only that function.
    IExpr[] funcSolutions = new IExpr[numFuncs];
    boolean[] eqUsed = new boolean[numEqs];

    int savedCounter = engine.getConstantCounter();
    try {
      for (int fi = 0; fi < numFuncs; fi++) {
        int matchedEq = -1;

        for (int ei = 0; ei < numEqs; ei++) {
          if (eqUsed[ei]) {
            continue;
          }
          IExpr eq = equations.get(ei + 1);

          // Check that this equation involves funcHeads[fi]
          if (eq.isFree(funcHeads[fi])) {
            continue;
          }

          // Check that this equation does NOT involve any other unknown function
          boolean involvesOther = false;
          for (int fj = 0; fj < numFuncs; fj++) {
            if (fj != fi && !eq.isFree(funcHeads[fj])) {
              involvesOther = true;
              break;
            }
          }

          if (!involvesOther) {
            matchedEq = ei;
            break;
          }
        }

        if (matchedEq == -1) {
          return F.NIL; // No decoupled equation found for this function
        }

        eqUsed[matchedEq] = true;
        IExpr equation = equations.get(matchedEq + 1);
        IExpr func = funcList.get(fi + 1);

        IExpr pdeResult = solvePDE(equation, func, xVars, ctx);
        if (!pdeResult.isPresent()) {
          return F.NIL;
        }

        // Extract the Rule from the result {{func -> solution}}
        if (pdeResult.isList() && ((IAST) pdeResult).argSize() > 0) {
          IAST innerList = (IAST) ((IAST) pdeResult).arg1();
          if (innerList.isList() && innerList.argSize() > 0) {
            funcSolutions[fi] = innerList.arg1();
          } else {
            return F.NIL;
          }
        } else {
          return F.NIL;
        }

        // Advance the counter permanently so the next PDE uses a different C(n)
        // engine.incConstantCounter();
      }

      // Build combined result: {{u(x,y) -> ..., v(x,y) -> ...}}
      IASTAppendable rules = F.ListAlloc(numFuncs);
      for (int fi = 0; fi < numFuncs; fi++) {
        rules.append(funcSolutions[fi]);
      }
      return F.List(rules);
    } finally {
      engine.setConstantCounter(savedCounter);
    }
  }
}
