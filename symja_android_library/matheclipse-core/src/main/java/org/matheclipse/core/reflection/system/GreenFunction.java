package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 *
 *
 * <pre>
 * GreenFunction({operator, condition1, condition2}, y, {x, xmin, xmax}, s)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * returns the Green's function of the linear differential <code>operator</code> in
 * <code>y(x)</code> on the interval from <code>xmin</code> to <code>xmax</code>, for the boundary
 * conditions given, with the source at <code>s</code>.
 *
 * </blockquote>
 *
 * <p>
 * The Green's function <code>G(x,s)</code> solves the equation with a unit impulse at
 * <code>s</code> as its right hand side, so that the solution of the equation with any right hand
 * side <code>f</code> is <code>Integrate(G(x,s)*f(s), {s, xmin, xmax})</code>.
 *
 * <p>
 * It is built from the solutions of the homogeneous equation which meet one boundary condition
 * each: with <code>y1</code> meeting the one at <code>xmin</code> and <code>y2</code> the one at
 * <code>xmax</code>, it is <code>y1(x)*y2(s)/(a*W)</code> below the source and
 * <code>y1(s)*y2(x)/(a*W)</code> above it, where <code>W</code> is the {@link Wronskian} of the two
 * at <code>s</code> and <code>a</code> the coefficient of the highest derivative there. The size of
 * the step the derivative takes across the source is what that denominator sets.
 *
 * <p>
 * A homogeneous problem which has a solution of its own has no Green's function, and is declined:
 * that is exactly the case in which <code>y1</code> and <code>y2</code> are proportional and their
 * Wronskian vanishes.
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; GreenFunction({y''(x), y(0) == 0, y(1) == 0}, y, {x, 0, 1}, s)
 * Piecewise({{(-1+s)*x,x&lt;=s}},s*(-1+x))
 * </pre>
 *
 * <h3>Related terms</h3>
 *
 * <p>
 * <a href="DSolve.md">DSolve</a>, <a href="DiracDelta.md">DiracDelta</a>,
 * <a href="Wronskian.md">Wronskian</a>
 */
public class GreenFunction extends AbstractFunctionEvaluator {

  public GreenFunction() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    try {
      if (!ast.arg1().isList() || !ast.arg3().isList()) {
        return F.NIL;
      }
      IAST problem = (IAST) ast.arg1();
      IAST range = (IAST) ast.arg3();
      if (problem.argSize() < 2 || range.argSize() != 3) {
        return F.NIL;
      }
      IExpr xVar = range.arg1();
      if (!xVar.isSymbol()) {
        // `1` is not a valid variable.
        return Errors.printMessage(S.GreenFunction, "ivar", F.list(xVar), engine);
      }
      IExpr source = ast.arg4();
      IExpr dependent = ast.arg2();
      IExpr head = dependent.isAST1() ? dependent.head() : dependent;
      if (!head.isSymbol()) {
        return F.NIL;
      }
      IExpr body =
          greenFunction(problem, head, xVar, range.arg2(), range.arg3(), source, engine);
      if (body.isNIL()) {
        return F.NIL;
      }
      // Asking about `u` rather than `u(x)` asks for the function itself.
      return dependent.isAST1() //
          ? body
          : F.Function(F.List(xVar, source), body);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.GreenFunction, rex);
    }
  }

  private static IExpr greenFunction(IAST problem, IExpr head, IExpr xVar, IExpr xMin, IExpr xMax,
      IExpr source, EvalEngine engine) {
    IExpr applied = F.unaryAST1(head, xVar);
    IExpr operator = engine.evaluate(F.ExpandAll(problem.arg1()));
    if (operator.isEqual()) {
      operator = S.Subtract.of(engine, operator.first(), operator.second());
    }
    // A difference operator shifts the argument instead of differentiating it, and is the other
    // kind of problem this solves.
    int shift = Casoratian.highestShift(operator, head, xVar, engine);
    if (shift >= 1) {
      return discreteGreenFunction(problem, operator, head, xVar, xMin, xMax, source, shift,
          engine);
    }

    LinearODEForm form = LinearODEForm.extract(operator, applied, xVar, engine);
    if (form == null || form.order < 1 || form.order > 2 || !form.g.isZero()) {
      // The operator has to be the homogeneous one, of first or second order.
      return F.NIL;
    }
    int order = form.order;
    if (problem.argSize() != order + 1) {
      return F.NIL;
    }

    IExpr[] basis = homogeneousBasis(operator, applied, xVar, order, engine);
    if (basis == null) {
      return F.NIL;
    }
    int conditionsAtMin = countAt(problem, head, xVar, xMin);
    int conditionsAtMax = countAt(problem, head, xVar, xMax);

    // Every condition at the near end makes this an initial value problem, whose Green's function
    // is causal: it vanishes before the source, which meets any homogeneous condition imposed
    // there, and after the source it is the solution which starts from rest with the step in its
    // derivative that the impulse gives it.
    if (conditionsAtMin == order && conditionsAtMax == 0
        && homogeneous(problem, head, xVar, engine)) {
      IExpr response = impulseResponse(basis, form.a[order], xVar, source, engine);
      return response.isNIL() //
          ? F.NIL
          : engine.evaluate(F.Times(response, F.HeavisideTheta(F.Subtract(xVar, source))));
    }
    if (order != 2 || conditionsAtMin != 1 || conditionsAtMax != 1) {
      return F.NIL;
    }

    // Otherwise each boundary condition picks out the solution which meets it.
    IExpr atMin = matching(problem, head, xVar, xMin, engine);
    IExpr atMax = matching(problem, head, xVar, xMax, engine);
    if (atMin.isNIL() || atMax.isNIL() || atMin.equals(atMax)) {
      return F.NIL;
    }
    IExpr y1 = meeting(atMin, basis, head, xVar, engine);
    IExpr y2 = meeting(atMax, basis, head, xVar, engine);
    if (y1.isNIL() || y2.isNIL()) {
      return F.NIL;
    }

    IExpr wronskian = engine.evaluate(F.Simplify(S.Wronskian.of(engine, F.List(y1, y2), xVar)));
    if (wronskian.isZero() || !wronskian.isFree(S.Wronskian, true)) {
      // Proportional solutions mean the homogeneous problem has a solution of its own, and then
      // the problem has no Green's function.
      return F.NIL;
    }
    // The jump of the derivative across the source is 1/a(s), which fixes the denominator.
    IExpr denominator = engine.evaluate(
        F.Times(F.subst(form.a[2], xVar, source), F.subst(wronskian, xVar, source)));
    if (denominator.isZero()) {
      return F.NIL;
    }
    IExpr below = engine.evaluate(F.Simplify(
        F.Divide(F.Times(y1, F.subst(y2, xVar, source)), denominator)));
    IExpr above = engine.evaluate(F.Simplify(
        F.Divide(F.Times(F.subst(y1, xVar, source), y2), denominator)));
    return engine.evaluate(F.Plus( //
        F.Times(above, F.HeavisideTheta(F.Subtract(xVar, source))),
        F.Times(below, F.HeavisideTheta(F.Subtract(source, xVar)))));
  }

  /** The two solutions of the homogeneous equation, or <code>null</code> if they are not found. */
  private static IExpr[] homogeneousBasis(IExpr operator, IExpr applied, IExpr xVar, int order,
      EvalEngine engine) {
    IExpr solutions = engine.evaluate(F.DSolve(F.Equal(operator, F.C0), applied, xVar));
    IAST values = DSolveUtil.extractSolveResults(solutions);
    if (values.argSize() != 1) {
      return null;
    }
    return splitConstants(values.arg1(), order, engine);
  }

  /**
   * The functions a general solution multiplies its arbitrary constants by, which are a basis of
   * the solutions of the homogeneous equation.
   *
   * <p>
   * The constants are read off the solution rather than assumed to be <code>C(1)</code> and
   * <code>C(2)</code>, because the counter they are taken from is not reset for every call.
   *
   * @return <code>null</code> unless the general solution is exactly a combination of as many
   *         independent functions as the order, with nothing left over
   */
  private static IExpr[] splitConstants(IExpr general, int order, EvalEngine engine) {
    IASTAppendable constants = F.ListAlloc();
    DSolveUtil.extractCVars(general, constants);
    if (constants.argSize() != order) {
      return null;
    }
    IExpr[] basis = new IExpr[order];
    IExpr rest = engine.evaluate(F.ExpandAll(general));
    for (int i = 0; i < order; i++) {
      basis[i] = engine.evaluate(F.Coefficient(rest, constants.get(i + 1)));
      rest = engine.evaluate(
          F.ExpandAll(F.Subtract(rest, F.Times(basis[i], constants.get(i + 1)))));
    }
    if (!rest.isZero()) {
      return null;
    }
    for (int i = 0; i < order; i++) {
      if (basis[i].isZero() || !basis[i].isFree(S.C, true)) {
        return null;
      }
    }
    return basis;
  }

  /**
   * The solution of the homogeneous equation which starts from rest at the source and whose
   * derivative steps by <code>1/a(s)</code> there, which is the response to an impulse.
   */
  private static IExpr impulseResponse(IExpr[] basis, IExpr leading, IExpr xVar, IExpr source,
      EvalEngine engine) {
    IASTAppendable fundamental = F.ListAlloc(basis.length);
    for (IExpr element : basis) {
      fundamental.append(element);
    }
    IExpr wronskian = engine.evaluate(F.Simplify(S.Wronskian.of(engine, fundamental, xVar)));
    if (wronskian.isZero() || !wronskian.isFree(S.Wronskian, true)) {
      return F.NIL;
    }
    IExpr denominator = engine.evaluate(
        F.Times(F.subst(leading, xVar, source), F.subst(wronskian, xVar, source)));
    if (denominator.isZero()) {
      return F.NIL;
    }
    IExpr numerator = basis.length == 1 //
        ? basis[0]
        : F.Subtract(F.Times(F.subst(basis[0], xVar, source), basis[1]),
            F.Times(F.subst(basis[1], xVar, source), basis[0]));
    return engine.evaluate(F.Simplify(F.Divide(numerator, denominator)));
  }

  /** How many of the conditions prescribe something at the given point. */
  private static int countAt(IAST problem, IExpr head, IExpr xVar, IExpr point) {
    int count = 0;
    for (int i = 2; i <= problem.argSize(); i++) {
      if (mentions(problem.get(i), head, xVar, point)) {
        count++;
      }
    }
    return count;
  }

  /** Whether every condition is homogeneous, i.e. met by the zero solution. */
  private static boolean homogeneous(IAST problem, IExpr head, IExpr xVar, EvalEngine engine) {
    for (int i = 2; i <= problem.argSize(); i++) {
      IExpr substituted = engine.evaluate(
          F.subst(problem.get(i), head, F.Function(F.List(xVar), F.C0)));
      // A condition the zero solution meets evaluates to True, or to an expression which is zero.
      if (substituted.isTrue()) {
        continue;
      }
      IExpr residual = substituted.isEqual() //
          ? S.Subtract.of(engine, substituted.first(), substituted.second())
          : substituted;
      if (!engine.evaluate(residual).isZero()) {
        return false;
      }
    }
    return true;
  }

  /** The condition which prescribes something at the given end of the interval. */
  private static IExpr matching(IAST problem, IExpr head, IExpr xVar, IExpr point,
      EvalEngine engine) {
    IExpr found = F.NIL;
    for (int i = 2; i <= problem.argSize(); i++) {
      IExpr condition = problem.get(i);
      if (mentions(condition, head, xVar, point)) {
        if (found.isPresent()) {
          return F.NIL;
        }
        found = condition;
      }
    }
    return found;
  }

  /** Whether the condition applies the unknown, or a derivative of it, at the given point. */
  private static boolean mentions(IExpr expr, IExpr head, IExpr xVar, IExpr point) {
    if (!expr.isAST()) {
      return false;
    }
    IAST ast = (IAST) expr;
    IAST[] derivative = ast.isDerivativeAST1();
    if (derivative != null && derivative[2] != null && derivative[1].isAST1()
        && derivative[1].arg1().equals(head) && derivative[2].isAST1()) {
      return derivative[2].first().equals(point);
    }
    if (ast.isAST1() && ast.head().equals(head)) {
      return ast.arg1().equals(point);
    }
    for (int i = 0; i < ast.size(); i++) {
      if (mentions(ast.get(i), head, xVar, point)) {
        return true;
      }
    }
    return false;
  }

  /**
   * The combination of the basis which meets the boundary condition. The condition is linear and
   * homogeneous in the two coefficients, so <code>c1*a + c2*b == 0</code> is met by
   * <code>(a, b) == (-c2, c1)</code>, which is nontrivial whenever the condition is.
   */
  private static IExpr meeting(IExpr condition, IExpr[] basis, IExpr head, IExpr xVar,
      EvalEngine engine) {
    IExpr a = F.Dummy("a");
    IExpr b = F.Dummy("b");
    IExpr candidate = F.Plus(F.Times(a, basis[0]), F.Times(b, basis[1]));
    IExpr substituted = engine.evaluate(
        F.subst(condition, head, F.Function(F.List(xVar), candidate)));
    IExpr residual = substituted.isEqual() //
        ? S.Subtract.of(engine, substituted.first(), substituted.second())
        : substituted;
    residual = engine.evaluate(F.ExpandAll(residual));
    IExpr c1 = engine.evaluate(F.Coefficient(residual, a));
    IExpr c2 = engine.evaluate(F.Coefficient(residual, b));
    IExpr rest = engine.evaluate(
        F.ExpandAll(F.Subtract(residual, F.Plus(F.Times(c1, a), F.Times(c2, b)))));
    // A condition which is not linear and homogeneous does not single out a solution.
    if (!rest.isZero() || !c1.isFree(a, true) || !c1.isFree(b, true) || !c2.isFree(a, true)
        || !c2.isFree(b, true)) {
      return F.NIL;
    }
    if (c1.isZero() && c2.isZero()) {
      return F.NIL;
    }
    return engine.evaluate(F.Simplify(
        F.Plus(F.Times(F.Negate(c2), basis[0]), F.Times(c1, basis[1]))));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_4_4;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
  /**
   * The Green's function of a linear difference operator, the discrete counterpart of the one
   * above.
   *
   * <p>
   * <code>G(n,m)</code> answers a unit impulse at <code>m</code>, so it solves
   * <code>L(G)(n) == KroneckerDelta(n,m)</code> and meets the conditions. Writing the equation at
   * <code>n == m</code> and at <code>n == m+1</code> for the two pieces gives two equations for
   * the two coefficients, whose determinant is the {@link Casoratian} one step along, so
   * <code>G(n,m) == y1(n)*y2(m+1)/(a(m)*C(m+1))</code> below the impulse and
   * <code>y1(m+1)*y2(n)/(a(m)*C(m+1))</code> above it. The Casoratian is to this what the
   * Wronskian is to the continuous case.
   *
   * <p>
   * The two pieces meet at <code>n == m+1</code>, where both expressions agree, so the answer is
   * written with {@link org.matheclipse.core.expression.S#UnitStep}, which is the step function
   * with a definite value at zero, rather than with the
   * {@link org.matheclipse.core.expression.S#HeavisideTheta} of the continuous case.
   */
  private static IExpr discreteGreenFunction(IAST problem, IExpr operator, IExpr head, IExpr xVar,
      IExpr xMin, IExpr xMax, IExpr source, int order, EvalEngine engine) {
    if (order > 2 || problem.argSize() != order + 1) {
      return F.NIL;
    }
    IExpr[] coefficients =
        Casoratian.shiftCoefficients(operator, head, xVar, order, engine);
    if (coefficients == null || coefficients[order].isZero()) {
      return F.NIL;
    }
    IExpr[] basis = discreteBasis(operator, head, xVar, order, engine);
    if (basis == null) {
      return F.NIL;
    }
    // Writing the equation at n == m makes the impulse land on the term at m + order, and the two
    // pieces of the answer meet at m + 1, which is where the Casoratian is taken.
    IExpr matchPoint = engine.evaluate(F.Plus(source, F.C1));
    IExpr leading = engine.evaluate(F.subst(coefficients[order], xVar, source));

    // Initial conditions for a difference equation occupy the first places of the range rather
    // than one point, so a second order problem starts from y(nmin) and y(nmin+1).
    int initial = 0;
    for (int k = 0; k < order; k++) {
      initial += countAt(problem, head, xVar, engine.evaluate(F.Plus(xMin, F.ZZ(k))));
    }
    int conditionsAtMax = countAt(problem, head, xVar, xMax);

    if (initial == order && conditionsAtMax == 0 && homogeneous(problem, head, xVar, engine)) {
      IExpr response = discreteResponse(basis, leading, xVar, matchPoint, engine);
      // The response starts one place beyond the last term the impulse reaches.
      return response.isNIL() //
          ? F.NIL
          : engine.evaluate(F.Times(response,
              F.UnitStep(F.Subtract(xVar, F.Plus(source, F.ZZ(order))))));
    }
    if (order != 2 || countAt(problem, head, xVar, xMin) != 1 || conditionsAtMax != 1) {
      return F.NIL;
    }
    IExpr atMin = matching(problem, head, xVar, xMin, engine);
    IExpr atMax = matching(problem, head, xVar, xMax, engine);
    if (atMin.isNIL() || atMax.isNIL() || atMin.equals(atMax)) {
      return F.NIL;
    }
    IExpr y1 = meeting(atMin, basis, head, xVar, engine);
    IExpr y2 = meeting(atMax, basis, head, xVar, engine);
    if (y1.isNIL() || y2.isNIL()) {
      return F.NIL;
    }
    IExpr casoratian = engine.evaluate(F.Simplify(
        S.Casoratian.of(engine, F.List(y1, y2), xVar)));
    if (casoratian.isZero() || !casoratian.isFree(S.Casoratian, true)) {
      // Proportional solutions mean the homogeneous problem has a solution of its own.
      return F.NIL;
    }
    IExpr denominator = engine.evaluate(
        F.Times(leading, F.subst(casoratian, xVar, matchPoint)));
    if (denominator.isZero()) {
      return F.NIL;
    }
    IExpr below = engine.evaluate(F.Simplify(
        F.Divide(F.Times(y1, F.subst(y2, xVar, matchPoint)), denominator)));
    IExpr above = engine.evaluate(F.Simplify(
        F.Divide(F.Times(F.subst(y1, xVar, matchPoint), y2), denominator)));
    return engine.evaluate(F.Plus( //
        F.Times(below, F.UnitStep(F.Subtract(matchPoint, xVar))),
        F.Times(above, F.UnitStep(F.Subtract(xVar, F.Plus(matchPoint, F.C1))))));
  }

  /** The solutions of the homogeneous difference equation. */
  private static IExpr[] discreteBasis(IExpr operator, IExpr head, IExpr xVar, int order,
      EvalEngine engine) {
    IExpr applied = F.unaryAST1(head, xVar);
    IExpr solutions = engine.evaluate(F.RSolve(F.Equal(operator, F.C0), applied, xVar));
    IAST values = DSolveUtil.extractSolveResults(solutions);
    if (values.argSize() != 1) {
      return null;
    }
    return splitConstants(values.arg1(), order, engine);
  }

  /**
   * The solution of the homogeneous difference equation which is zero at the impulse and steps to
   * <code>1/a(m)</code> one place further on.
   */
  private static IExpr discreteResponse(IExpr[] basis, IExpr leading, IExpr xVar, IExpr matchPoint,
      EvalEngine engine) {
    IASTAppendable fundamental = F.ListAlloc(basis.length);
    for (IExpr element : basis) {
      fundamental.append(element);
    }
    IExpr casoratian = engine.evaluate(F.Simplify(S.Casoratian.of(engine, fundamental, xVar)));
    if (casoratian.isZero() || !casoratian.isFree(S.Casoratian, true)) {
      return F.NIL;
    }
    IExpr denominator = engine.evaluate(
        F.Times(leading, F.subst(casoratian, xVar, matchPoint)));
    if (denominator.isZero()) {
      return F.NIL;
    }
    IExpr numerator = basis.length == 1 //
        ? basis[0]
        : F.Subtract(F.Times(F.subst(basis[0], xVar, matchPoint), basis[1]),
            F.Times(F.subst(basis[1], xVar, matchPoint), basis[0]));
    return engine.evaluate(F.Simplify(F.Divide(numerator, denominator)));
  }
}
