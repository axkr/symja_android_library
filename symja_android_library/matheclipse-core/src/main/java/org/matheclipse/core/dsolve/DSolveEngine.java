package org.matheclipse.core.dsolve;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The differential equation solvers, as the rest of the library sees them.
 *
 * <p>
 * Everything behind this class is package-private: the cascade of methods a single equation is
 * offered to, the systems, the partial differential equations, the verification of what they
 * answer, and the shared utilities they are written in terms of. What a caller needs is here, so
 * that the way the solvers are divided between classes stays a question for this package alone.
 *
 * <p>
 * The four solving methods are instance methods because a call to <code>DSolve</code> is a single
 * piece of work with a state of its own: the depth it has reached, the time it has left, the
 * arbitrary constants it has handed out, and the messages it has collected but not shown. That
 * state is the {@link DSolveContext} this object holds. A caller which solves one equation and
 * then another gives each its own engine; a caller which offers one equation to several of these
 * methods gives them the same one, and calls {@link #flushMessages()} when it is done.
 */
public final class DSolveEngine {

  private final DSolveContext context;

  /** An engine for one call, with no conditions on the solution yet. */
  public DSolveEngine(EvalEngine engine) {
    this.context = new DSolveContext(engine, F.CEmptyList);
  }

  /**
   * Solves a single ordinary differential equation for one unknown function.
   *
   * @param uFunction1Arg the applied unknown, e.g. <code>y(x)</code>
   * @param arg2 the second argument of <code>DSolve</code>, the unknown as it is to be reported
   * @param xVar the independent variable
   * @param equations the equation, together with any which came with it
   * @param boundaryConditions the conditions the solution has to satisfy, empty for the general
   *        solution
   * @return the solutions, or {@link F#NIL} if none of the methods answers
   */
  public IExpr solveODE(IAST uFunction1Arg, IExpr arg2, IExpr xVar, IASTAppendable equations,
      IAST boundaryConditions) {
    return DSolveODE.unaryODE(uFunction1Arg, arg2, xVar, equations, boundaryConditions,
        context.withConditions(boundaryConditions));
  }

  /**
   * Solves a system of ordinary differential equations, or a differential algebraic one.
   *
   * @param outputFunctions the second argument of <code>DSolve</code>, which decides how the
   *        answer is written
   * @return the solutions, or {@link F#NIL}
   */
  public IExpr solveSystem(IASTAppendable equations, IAST dependentFunctions, IExpr xVar,
      IAST boundaryConditions, IExpr outputFunctions) {
    return DSolveSystem.solveSystemODE(equations, dependentFunctions, xVar, boundaryConditions,
        outputFunctions, context.withConditions(boundaryConditions));
  }

  /**
   * Solves one partial differential equation in two variables.
   *
   * @param conditions the conditions the solution has to satisfy, empty for the general solution
   * @return the solutions, or {@link F#NIL}
   */
  public IExpr solvePDE(IExpr equation, IExpr uFunc, IAST xVars, IAST conditions) {
    return DSolvePDE.solvePDE(equation, uFunc, xVars, context.withConditions(conditions));
  }

  /**
   * Solves a system of partial differential equations.
   *
   * @return the solutions, or {@link F#NIL}
   */
  public IExpr solveSystemPDE(IAST equations, IAST funcList, IAST xVars) {
    return DSolvePDE.solveSystemPDE(equations, funcList, xVars, context);
  }

  /**
   * Shows the messages the solvers collected while they worked.
   *
   * <p>
   * Trying a method the equation does not belong to is a step of the algorithm rather than
   * something to report, so the solvers run with the messages switched off and keep back the ones
   * which are worth showing. This shows them, and the caller which switched the messages off calls
   * it once the whole cascade has finished.
   */
  public void flushMessages() {
    context.flushMessages();
  }

  /** The highest derivative order the solvers read an equation for. */
  public static int maxDerivativeOrder() {
    return DSolveODE.MAX_DERIVATIVE_ORDER;
  }

  /** Whether <code>expr</code> contains a derivative of <code>head</code>. */
  public static boolean differentiates(IExpr expr, IExpr head) {
    return DSolvePDE.differentiates(expr, head);
  }

  /**
   * Whether <code>candidate</code> prescribes a value of <code>uApplied</code> along a line where
   * one of the two variables is fixed, which is what tells a condition from the equation itself.
   */
  public static boolean prescribesValue(IExpr candidate, IExpr uApplied, IExpr x, IExpr y,
      EvalEngine engine) {
    return DSolvePDEInitialValue.prescribesValue(candidate, uApplied, x, y, engine);
  }

  /**
   * Whether <code>expr</code> is zero, as far as simplifying it shows.
   *
   * @return <code>false</code> when it is not zero and when it cannot be decided
   */
  public static boolean isVanishing(IExpr expr, EvalEngine engine) {
    return DSolveODE.isVanishing(expr, engine);
  }

  /**
   * The right hand sides of a <code>Solve</code> or <code>DSolve</code> result, one per solution.
   *
   * @return an empty list if the result is not one of solutions
   */
  public static IAST solutionsOf(IExpr solveResult) {
    return DSolveUtil.extractSolveResults(solveResult);
  }

  /**
   * Collects the arbitrary constants <code>C(k)</code> which occur in <code>general</code> into
   * <code>constants</code>, in the order they are met.
   */
  public static void constantsOf(IExpr general, IASTAppendable constants) {
    DSolveUtil.extractCVars(general, constants);
  }
}
