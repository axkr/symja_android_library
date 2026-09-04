package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The state one {@link DSolve} call carries through its solvers: the conditions it still has to
 * satisfy, the messages it wants to show afterwards, and a time boxed integration.
 *
 * <p>
 * The whole cascade runs with the messages of its speculative sub-solves switched off, because
 * trying a method which does not fit is a step of the algorithm and not something to report. That
 * also silences the messages <code>DSolve</code> itself wants to show, so those are collected here
 * and shown by {@link DSolve} once the cascade has finished.
 */
final class DSolveContext {

  /** How long a single integration inside the cascade may take. */
  private static final int INTEGRATE_SECONDS = 5;

  final EvalEngine engine;

  /** The boundary and initial conditions, in the form which is equal to zero. */
  final IAST conditions;

  private final List<IAST> pendingMessages;

  DSolveContext(EvalEngine engine, IAST conditions) {
    this(engine, conditions, new ArrayList<>());
  }

  private DSolveContext(EvalEngine engine, IAST conditions, List<IAST> pendingMessages) {
    this.engine = engine;
    this.conditions = conditions.isPresent() && conditions.isAST() ? conditions : F.CEmptyList;
    this.pendingMessages = pendingMessages;
  }

  /** The same context with the given conditions, sharing the collected messages. */
  DSolveContext withConditions(IAST newConditions) {
    return new DSolveContext(engine, newConditions, pendingMessages);
  }

  /** A fresh arbitrary constant <code>C(k)</code>. */
  IExpr nextConstant() {
    return F.C(engine.incConstantCounter());
  }

  /**
   * The antiderivative of <code>function</code>, or {@link F#NIL} if it is not one this solver can
   * use.
   *
   * <p>
   * An integral which stays unevaluated, takes too long, or comes back as an elliptic integral is
   * of no use to a solver which has to differentiate, invert or substitute into the result
   * afterwards, and the attempt to do so is where several equations used to hang. Declining here
   * lets the next method of the cascade run instead.
   */
  IExpr integrate(IExpr function, IExpr variable) {
    return integrate(function, variable, engine);
  }

  /**
   * The antiderivative of <code>function</code>, or {@link F#NIL} if it is not one this solver can
   * use.
   *
   * <p>
   * The time limit matters as much as the shape of the result. Symja spends minutes on some of the
   * integrals a differential equation leads to, and a solver which has already spent that long is
   * not going to produce anything the caller wanted: the equation <code>y''(x) == y(x)^3</code>
   * reaches such an integral through reduction of order, and used to stop responding there.
   */
  static IExpr integrate(IExpr function, IExpr variable, EvalEngine engine) {
    IExpr result;
    try {
      result = engine.evaluate(F.TimeConstrained(F.Integrate(function, variable),
          F.ZZ(INTEGRATE_SECONDS), S.$Aborted));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
    if (result.isNIL() || result.equals(S.$Aborted) || result.isIndeterminate()) {
      return F.NIL;
    }
    return isUsable(result) ? result : F.NIL;
  }

  /** Whether an antiderivative is in a form the solvers can go on working with. */
  static boolean isUsable(IExpr expr) {
    return expr.isFree(
        x -> x.isAST(S.Integrate) || x.isAST(S.EllipticF) || x.isAST(S.EllipticE)
            || x.isAST(S.EllipticPi) || x.isAST(S.WeierstrassP) || x.isAST(S.WeierstrassPPrime),
        true);
  }

  /** Remembers a message to be shown once the cascade has finished. */
  void addMessage(String tag, IAST arguments) {
    pendingMessages.add(F.binaryAST2(S.List, F.$str(tag), arguments));
  }

  /** Shows the messages which were collected while the messages of the cascade were switched off. */
  void flushMessages() {
    for (IAST message : pendingMessages) {
      Errors.printMessage(S.DSolve, message.arg1().toString(), (IAST) message.arg2());
    }
    pendingMessages.clear();
  }
}
