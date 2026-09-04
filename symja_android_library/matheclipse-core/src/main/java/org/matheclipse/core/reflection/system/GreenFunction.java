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
    IExpr general = values.arg1();
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
    // The general solution has to be exactly the combination of the two, with nothing left over.
    if (!rest.isZero()) {
      return null;
    }
    for (int i = 0; i < order; i++) {
      if (basis[i].isZero()) {
        return null;
      }
      if (!basis[i].isFree(S.C, true)) {
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
}
