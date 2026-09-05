package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The two initial value problems whose solution is a formula in the data.
 *
 * <p>
 * Both are answered by writing the solution down rather than by fitting the conditions to a general
 * solution afterwards, because neither general solution can be written with a finite number of
 * arbitrary functions of one variable in a way the fitting would reach.
 */
final class DSolvePDEInitialValue {

  private DSolvePDEInitialValue() {}

  /**
   * The solution of the problem, or {@link F#NIL} if it is not one of the two.
   *
   * @param lhs the residual of the equation, which is zero on a solution
   * @param uApplied the unknown, applied to both variables
   */
  static IExpr solve(IExpr lhs, IExpr uApplied, IExpr x, IExpr y, DSolveContext ctx) {
    int count = ctx.conditions.argSize();
    if (count < 1 || count > 2) {
      return F.NIL;
    }
    Prescribed[] conditions = new Prescribed[count];
    for (int i = 0; i < count; i++) {
      conditions[i] = Prescribed.of(ctx.conditions.get(i + 1), uApplied, x, y, ctx.engine);
      if (conditions[i] == null) {
        return F.NIL;
      }
    }
    if (count == 2) {
      return dAlembert(lhs, uApplied, x, y, conditions, ctx);
    }
    return heatKernel(lhs, uApplied, x, y, conditions[0], ctx);
  }

  /**
   * The displacement of a string which is let go from a given shape at a given speed.
   *
   * <p>
   * What starts at a point of the string reaches it again from both sides at the speed the equation
   * names, so the shape contributes its value at the two ends of the interval the signal has had
   * time to cross, and the speed contributes its mean over that interval.
   */
  private static IExpr dAlembert(IExpr lhs, IExpr uApplied, IExpr x, IExpr y,
      Prescribed[] conditions, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    Prescribed first = conditions[0];
    Prescribed second = conditions[1];
    if (first.freeIsX != second.freeIsX
        || !DSolveODE.isVanishing(engine.evaluate(F.Subtract(first.at, second.at)), engine)) {
      return F.NIL;
    }
    IExpr space = first.freeIsX ? x : y;
    IExpr time = first.freeIsX ? y : x;

    // One condition gives the shape, the other the speed it is let go at.
    Prescribed shape = null;
    Prescribed speed = null;
    for (Prescribed condition : conditions) {
      int inTime = first.freeIsX ? condition.orderY : condition.orderX;
      int inSpace = first.freeIsX ? condition.orderX : condition.orderY;
      if (inSpace != 0) {
        return F.NIL;
      }
      if (inTime == 0) {
        shape = condition;
      } else if (inTime == 1) {
        speed = condition;
      }
    }
    if (shape == null || speed == null) {
      return F.NIL;
    }

    IExpr inTime = coefficientOf(lhs, uApplied, space, time, 0, 2, engine);
    IExpr inSpace = coefficientOf(lhs, uApplied, space, time, 2, 0, engine);
    if (inTime.isZero() || !inTime.isFree(x) || !inTime.isFree(y) || !inSpace.isFree(x)
        || !inSpace.isFree(y)) {
      return F.NIL;
    }
    // Nothing but the two second derivatives, so no damping, no source and no mixed term.
    IExpr rest = engine.evaluate(F.Subtract(lhs, F.Plus(
        F.Times(inTime, derivative(uApplied, space, time, 0, 2, engine)),
        F.Times(inSpace, derivative(uApplied, space, time, 2, 0, engine)))));
    if (!DSolveODE.isVanishing(rest, engine)) {
      return F.NIL;
    }
    IExpr speedSquared = engine.evaluate(F.Cancel(F.Divide(F.Negate(inSpace), inTime)));
    if (speedSquared.isZero()) {
      return F.NIL;
    }
    IExpr c = engine.evaluate(F.Sqrt(speedSquared));

    if (!verifyDAlembert(space, time, shape.at, c, inTime, inSpace, ctx)) {
      return F.NIL;
    }
    return dAlembertBody(shape.data, speed.data, space, time, shape.at, c, ctx);
  }

  /** The formula itself, so that the check below can rebuild it with data it can differentiate. */
  private static IExpr dAlembertBody(IExpr shape, IExpr speed, IExpr space, IExpr time, IExpr at,
      IExpr c, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr reach = engine.evaluate(F.Times(c, F.Subtract(time, at)));
    IExpr from = engine.evaluate(F.Subtract(space, reach));
    IExpr to = engine.evaluate(F.Plus(space, reach));

    IExpr ends = F.Times(F.C1D2, F.Plus(engine.evaluate(F.subst(shape, space, from)),
        engine.evaluate(F.subst(shape, space, to))));
    IExpr s = F.Dummy("K");
    IExpr mean = F.Times(F.Power(F.Times(F.C2, c), F.CN1),
        F.Integrate(F.subst(speed, space, s), F.List(s, from, to)));
    return engine.evaluate(F.Plus(ends, mean));
  }

  /**
   * Whether the formula solves the equation, checked on data it can be differentiated for.
   *
   * <p>
   * The formula is a fixed one in the shape and the speed, so it is right for every pair exactly
   * when it is right for one, and an integral of a function which is not given cannot be
   * differentiated to see it directly.
   */
  private static boolean verifyDAlembert(IExpr space, IExpr time, IExpr at, IExpr c, IExpr inTime,
      IExpr inSpace, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    IExpr body =
        dAlembertBody(F.Cos(space), F.Sin(space), space, time, at, c, ctx);
    IExpr residual = engine.evaluate(F.Plus(
        F.Times(inTime, F.D(body, F.List(time, F.C2))),
        F.Times(inSpace, F.D(body, F.List(space, F.C2)))));
    if (!DSolveODE.isVanishing(residual, engine)) {
      return false;
    }
    IExpr atStart = engine.evaluate(F.Subtract(F.subst(body, time, at), F.Cos(space)));
    if (!DSolveODE.isVanishing(atStart, engine)) {
      return false;
    }
    IExpr speedAtStart = engine.evaluate(
        F.Subtract(F.subst(engine.evaluate(F.D(body, time)), time, at), F.Sin(space)));
    return DSolveODE.isVanishing(speedAtStart, engine);
  }

  /**
   * The temperature of a rod which starts from a given one.
   *
   * <p>
   * Heat spreads from every point at once, so every point of the starting temperature contributes
   * everywhere, weighted by the kernel which is the temperature a single hot point would have
   * spread to. The integral is left as it stands: it has no closed form for a temperature which is
   * not given.
   */
  private static IExpr heatKernel(IExpr lhs, IExpr uApplied, IExpr x, IExpr y,
      Prescribed condition, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    if (condition.orderX != 0 || condition.orderY != 0) {
      return F.NIL;
    }
    IExpr space = condition.freeIsX ? x : y;
    IExpr time = condition.freeIsX ? y : x;

    IExpr inTime = coefficientOf(lhs, uApplied, space, time, 0, 1, engine);
    IExpr inSpace = coefficientOf(lhs, uApplied, space, time, 2, 0, engine);
    if (inTime.isZero() || !inTime.isFree(x) || !inTime.isFree(y) || !inSpace.isFree(x)
        || !inSpace.isFree(y)) {
      return F.NIL;
    }
    // Nothing but those two, so no drift along the rod and no heat made or lost in it.
    IExpr rest = engine.evaluate(F.Subtract(lhs, F.Plus(
        F.Times(inTime, derivative(uApplied, space, time, 0, 1, engine)),
        F.Times(inSpace, derivative(uApplied, space, time, 2, 0, engine)))));
    if (!DSolveODE.isVanishing(rest, engine)) {
      return F.NIL;
    }
    IExpr rate = engine.evaluate(F.Cancel(F.Divide(F.Negate(inSpace), inTime)));
    if (rate.isZero() || rate.isNegativeResult()) {
      return F.NIL;
    }

    IExpr elapsed = engine.evaluate(F.Subtract(time, condition.at));
    IExpr spread = engine.evaluate(F.Times(F.C4, rate, elapsed));
    IExpr s = F.Dummy("K");
    IExpr kernel = F.Times(F.Power(F.Times(S.Pi, spread), F.CN1D2),
        F.Exp(F.Divide(F.Negate(F.Sqr(F.Subtract(space, s))), spread)));

    // The convolution solves the equation because the kernel does, the limits being fixed.
    IExpr residual = engine.evaluate(F.Subtract(engine.evaluate(F.D(kernel, time)),
        F.Times(rate, engine.evaluate(F.D(kernel, F.List(space, F.C2))))));
    if (!DSolveODE.isVanishing(residual, engine)) {
      return F.NIL;
    }
    return F.Integrate(F.Times(F.subst(condition.data, space, s), kernel),
        F.List(s, F.CNInfinity, F.CInfinity));
  }

  /** The unknown differentiated as given, applied to both variables. */
  private static IExpr derivative(IExpr uApplied, IExpr space, IExpr time, int inSpace, int inTime,
      EvalEngine engine) {
    IExpr result = uApplied;
    if (inSpace > 0) {
      result = engine.evaluate(F.D(result, F.List(space, F.ZZ(inSpace))));
    }
    if (inTime > 0) {
      result = engine.evaluate(F.D(result, F.List(time, F.ZZ(inTime))));
    }
    return result;
  }

  /** What one such derivative is multiplied by in the equation. */
  private static IExpr coefficientOf(IExpr lhs, IExpr uApplied, IExpr space, IExpr time,
      int inSpace, int inTime, EvalEngine engine) {
    IExpr term = derivative(uApplied, space, time, inSpace, inTime, engine);
    IExpr marker = F.Dummy("pdeC");
    return engine.evaluate(F.D(F.subst(lhs, term, marker), marker));
  }

  /**
   * Whether the equation prescribes a value along a line rather than differentiating the unknown
   * everywhere, which is what tells a condition from the equation itself when the condition
   * prescribes a derivative.
   */
  static boolean prescribesValue(IExpr candidate, IExpr uApplied, IExpr x, IExpr y,
      EvalEngine engine) {
    return Prescribed.of(candidate, uApplied, x, y, engine) != null;
  }

  /** A value the solution is required to take where one of the variables is held fixed. */
  private static final class Prescribed {
    final int orderX;
    final int orderY;
    /** Whether the variable left free is the first one. */
    final boolean freeIsX;
    /** What the other variable is held at. */
    final IExpr at;
    /** The value taken there, as a function of the free variable. */
    final IExpr data;

    private Prescribed(int orderX, int orderY, boolean freeIsX, IExpr at, IExpr data) {
      this.orderX = orderX;
      this.orderY = orderY;
      this.freeIsX = freeIsX;
      this.at = at;
      this.data = data;
    }

    /** Reads one condition, or returns null if it does not prescribe a value along a line. */
    static Prescribed of(IExpr condition, IExpr uApplied, IExpr x, IExpr y, EvalEngine engine) {
      if (!condition.isEqual()) {
        return null;
      }
      IExpr head = uApplied.head();
      IExpr applied = F.NIL;
      IExpr data = F.NIL;
      if (!condition.first().isFree(head, true) && condition.second().isFree(head, true)) {
        applied = condition.first();
        data = condition.second();
      } else if (!condition.second().isFree(head, true) && condition.first().isFree(head, true)) {
        applied = condition.second();
        data = condition.first();
      }
      if (applied.isNIL() || !applied.isAST()) {
        return null;
      }

      int orderX = 0;
      int orderY = 0;
      IAST call = (IAST) applied;
      if (!call.head().equals(head)) {
        // Derivative(i, j)[u][a, b]
        if (!call.head().isAST1() || !((IAST) call.head()).arg1().equals(head)) {
          return null;
        }
        IExpr orders = ((IAST) call.head()).head();
        if (!orders.isAST(S.Derivative, 3) || !((IAST) orders).arg1().isInteger()
            || !((IAST) orders).arg2().isInteger()) {
          return null;
        }
        orderX = ((IAST) orders).arg1().toIntDefault();
        orderY = ((IAST) orders).arg2().toIntDefault();
        if (orderX < 0 || orderY < 0) {
          return null;
        }
      }
      if (call.argSize() != 2) {
        return null;
      }

      IExpr firstArgument = call.arg1();
      IExpr secondArgument = call.arg2();
      boolean freeIsX = firstArgument.equals(x) && secondArgument.isFree(x)
          && secondArgument.isFree(y);
      boolean freeIsY = secondArgument.equals(y) && firstArgument.isFree(x)
          && firstArgument.isFree(y);
      if (freeIsX == freeIsY) {
        return null;
      }
      IExpr at = freeIsX ? secondArgument : firstArgument;
      if (!data.isFree(head, true) || !data.isFree(freeIsX ? y : x)) {
        return null;
      }
      return new Prescribed(orderX, orderY, freeIsX, at, engine.evaluate(data));
    }
  }
}
