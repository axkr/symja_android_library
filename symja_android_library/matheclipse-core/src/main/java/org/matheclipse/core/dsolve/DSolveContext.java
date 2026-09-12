package org.matheclipse.core.dsolve;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.basic.MachineProfile;
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

  /**
   * How long a single integration inside the cascade may take, on the machine the cascade was
   * tuned on. What it may take here is {@link MachineProfile#seconds(int)} of that.
   */
  private static final int INTEGRATE_SECONDS = 5;

  /**
   * What one call keeps for itself, as opposed to what {@link #withConditions} hands on. A method
   * which asks a sub-solver to work on other conditions is still the same attempt at the same
   * equation, so the depth it has reached, the methods it is already inside of, and the time it has
   * left are shared with that sub-solver rather than copied.
   */
  private static final class State {
    int depth;
    int rewritingBase;
    boolean linearizableActive;
    long deadlineNanos;
  }

  final EvalEngine engine;

  /** The boundary and initial conditions, in the form which is equal to zero. */
  final IAST conditions;

  private final List<IAST> pendingMessages;

  private final State state;

  DSolveContext(EvalEngine engine, IAST conditions) {
    this(engine, conditions, new ArrayList<>(), new State());
  }

  private DSolveContext(EvalEngine engine, IAST conditions, List<IAST> pendingMessages,
      State state) {
    this.engine = engine;
    this.conditions = conditions.isPresent() && conditions.isAST() ? conditions : F.CEmptyList;
    this.pendingMessages = pendingMessages;
    this.state = state;
  }

  /** The same context with the given conditions, sharing the collected messages. */
  DSolveContext withConditions(IAST newConditions) {
    return new DSolveContext(engine, newConditions, pendingMessages, state);
  }

  /** Notes that one more solver of the cascade has been entered. */
  void enter() {
    state.depth++;
  }

  /** Notes that a solver of the cascade has been left. */
  void leave() {
    state.depth--;
  }

  /**
   * How deep in the cascade the caller is: <code>1</code> in the solver the equation was handed to,
   * more in every solver one of those calls in turn.
   *
   * <p>
   * Rewriting the coefficients of an equation is worth doing once, on the equation the user asked
   * about. Doing it again inside a recursion costs time and hands the inner method a form it did
   * not ask for.
   */
  int depth() {
    return state.depth;
  }

  /**
   * Whether the equation the caller is looking at may have its coefficients rewritten before the
   * solvers are asked about it.
   *
   * <p>
   * Rewriting is worth doing once, on the equation as it was handed over, and doing it a second
   * time inside a recursion hands the inner method a form it did not ask for. That is normally the
   * same thing as being at the top of the cascade, which is why this used to be read off
   * {@link #depth()}. It is not the same thing when a method recurses on an equation it wrote down
   * itself without rewriting anything -- variation of parameters asking about the homogeneous
   * equation beside the one it was given -- and such a method says so with
   * {@link #enterUnrewritten()}.
   */
  boolean mayRewrite() {
    return state.depth <= state.rewritingBase + 1;
  }

  /**
   * Says that the sub-solve about to start is being handed an equation which nothing has rewritten,
   * so that it may rewrite it as the top of the cascade would.
   *
   * @return what to hand back to {@link #leaveUnrewritten(int)} afterwards
   */
  int enterUnrewritten() {
    int previous = state.rewritingBase;
    state.rewritingBase = state.depth;
    return previous;
  }

  /** Takes back what {@link #enterUnrewritten()} allowed. */
  void leaveUnrewritten(int previous) {
    state.rewritingBase = previous;
  }

  /**
   * Claims the substitution search for the caller, or answers <code>false</code> if it is already
   * running. The equation it produces is of the same kind as the one it started from, so without
   * this the search would enter itself.
   */
  boolean enterLinearizable() {
    if (state.linearizableActive) {
      return false;
    }
    state.linearizableActive = true;
    return true;
  }

  /** Releases the substitution search. */
  void leaveLinearizable() {
    state.linearizableActive = false;
  }

  /**
   * Gives the caller the given number of seconds from now, as they are meant on the machine the
   * solvers were tuned on: a slower machine is given proportionally longer, see
   * {@link MachineProfile}.
   */
  void startDeadline(int baseSeconds) {
    state.deadlineNanos =
        System.nanoTime() + MachineProfile.seconds((long) baseSeconds) * 1_000_000_000L;
  }

  /** Withdraws a deadline, so that the methods after the caller are not bound by it. */
  void clearDeadline() {
    state.deadlineNanos = 0;
  }

  /**
   * Whether the time a method gave itself is up.
   *
   * <p>
   * This is wall clock rather than processor time on purpose: what has to be bounded is how long
   * the user waits, and much of that time is spent inside sub-evaluations which the caller cannot
   * interrupt.
   */
  boolean expired() {
    return state.deadlineNanos != 0 && System.nanoTime() >= state.deadlineNanos;
  }

  /**
   * Evaluates <code>expr</code> with a time limit, or {@link F#NIL} if it does not finish in time.
   *
   * <p>
   * The limit is given as it is meant on the machine the solvers were tuned on, and scaled to
   * this machine by {@link MachineProfile}.
   *
   * <p>
   * Note that a time limit is not a reliable bound on its own: an integration which has entered a
   * long chain of rules does not observe it. A method which can grow its input has to keep it small
   * by structural means first, and use this as a second line of defence.
   */
  IExpr evalTimeConstrained(IExpr expr, int baseSeconds) {
    IExpr result;
    try {
      result = engine.evaluate(
          F.TimeConstrained(expr, F.ZZ(MachineProfile.seconds(baseSeconds)), S.$Aborted));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
    return result.isNIL() || result.equals(S.$Aborted) ? F.NIL : result;
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
   * The antiderivative of <code>function</code>, or {@link F#NIL} if the integrand is already
   * bigger than <code>maxLeafCount</code>.
   *
   * <p>
   * The size test is what actually keeps a long integration out; see
   * {@link #evalTimeConstrained(IExpr, int)} on why the time limit alone does not.
   */
  IExpr integrate(IExpr function, IExpr variable, int maxLeafCount) {
    return integrate(function, variable, maxLeafCount, INTEGRATE_SECONDS);
  }

  /**
   * {@link #integrate(IExpr, IExpr, int)} with a budget of its own, for a method which does
   * several integrals in a row and has to finish inside a deadline covering all of them.
   */
  IExpr integrate(IExpr function, IExpr variable, int maxLeafCount, int baseSeconds) {
    if (function.leafCount() > maxLeafCount) {
      return F.NIL;
    }
    return integrate(function, variable, baseSeconds, engine);
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
    return integrate(function, variable, INTEGRATE_SECONDS, engine);
  }

  /** {@link #integrate(IExpr, IExpr, EvalEngine)} with a budget of its own. */
  static IExpr integrate(IExpr function, IExpr variable, int baseSeconds, EvalEngine engine) {
    IExpr result;
    try {
      result = engine.evaluate(F.TimeConstrained(F.Integrate(function, variable),
          F.ZZ(MachineProfile.seconds(baseSeconds)), S.$Aborted));
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
