package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <p>
 * Implementation of the <code>DiscreteLimit</code> function.
 * </p>
 *
 * <p>
 * <code>DiscreteLimit(f, n -> Infinity)</code> gives the limit of the sequence <code>f</code> as the
 * <b>integer</b> variable <code>n</code> tends to infinity. <code>-Infinity</code> is allowed as
 * well; every other limit point is rejected with the <code>dlim</code> message, because a sequence
 * has no interesting limit at a finite point.
 * </p>
 *
 * <p>
 * The multivariate forms <code>DiscreteLimit(f, {k1 -> Infinity, ..., kn -> Infinity})</code> and
 * <code>DiscreteLimit(f, {k1, ..., kn} -> {Infinity, ..., Infinity})</code> are computed as
 * <i>iterated</i> limits in the given order (with a retry in the reverse order), which agrees with
 * the nested limit for separable sequences.
 * </p>
 *
 * <p>
 * The bulk of the work is delegated to {@link Limit}. Two rewrites are applied first that are only
 * valid because the limit variable runs through the integers, and that a continuous limit therefore
 * has to answer with <code>Indeterminate</code>:
 * </p>
 * <ul>
 * <li>integer-periodic trigonometric arguments collapse: <code>Sin(k*Pi*n) -> 0</code>,
 * <code>Cos(k*Pi*n) -> 1</code> for even <code>k</code> and <code>-> (-1)^n</code> for odd
 * <code>k</code>;</li>
 * <li>a product containing a bounded oscillating factor like <code>(-1)^n</code> tends to
 * <code>0</code> if the remaining factors do, and oscillates (<code>Indeterminate</code>)
 * otherwise.</li>
 * </ul>
 */
public class DiscreteLimit extends AbstractFunctionEvaluator {

  public DiscreteLimit() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    List<IAST> limitSpecs = limitSpecifications(ast, engine);
    if (limitSpecs == null) {
      return F.NIL;
    }
    for (int i = 0; i < limitSpecs.size(); i++) {
      IExpr limitPoint = limitSpecs.get(i).arg2();
      if (!limitPoint.isInfinity() && !limitPoint.isNegativeInfinity()) {
        // The limit point `1` in `2` should be Infinity or -Infinity.
        return Errors.printMessage(S.DiscreteLimit, "dlim", F.List(limitPoint, ast), engine);
      }
    }

    IExpr result = iteratedLimit(ast.arg1(), limitSpecs, engine);
    if (result.isNIL() && limitSpecs.size() > 1) {
      // The iterated limit is computed one variable at a time, so a factor that only the *other*
      // variable resolves can block the first order. Separable sequences give the same answer in
      // either order, so a single reversed retry is cheap and never changes a resolved result.
      List<IAST> reversed = new ArrayList<IAST>(limitSpecs);
      java.util.Collections.reverse(reversed);
      result = iteratedLimit(ast.arg1(), reversed, engine);
    }
    return result;
  }

  /**
   * Normalize the second argument of <code>DiscreteLimit</code> into a list of
   * <code>variable -> limitPoint</code> rules.
   *
   * @return <code>null</code> if the specification is not valid; the appropriate message has been
   *         printed in that case
   */
  private static List<IAST> limitSpecifications(final IAST ast, final EvalEngine engine) {
    IExpr arg2 = ast.arg2();
    List<IAST> result = new ArrayList<IAST>();
    if (arg2.isRuleAST()) {
      IExpr lhs = arg2.first();
      IExpr rhs = arg2.second();
      if (lhs.isList()) {
        // DiscreteLimit(f, {k1, ..., kn} -> {p1, ..., pn})
        IAST variables = (IAST) lhs;
        if (!rhs.isList() || ((IAST) rhs).size() != variables.size()) {
          // Limit specification `1` is not of the form x->x0.
          Errors.printMessage(S.DiscreteLimit, "lim", F.List(arg2), engine);
          return null;
        }
        IAST points = (IAST) rhs;
        for (int i = 1; i < variables.size(); i++) {
          if (!addSpecification(result, variables.get(i), points.get(i), arg2, engine)) {
            return null;
          }
        }
        return result;
      }
      if (!addSpecification(result, lhs, rhs, arg2, engine)) {
        return null;
      }
      return result;
    }
    if (arg2.isList()) {
      // DiscreteLimit(f, {k1 -> p1, ..., kn -> pn})
      IAST list = (IAST) arg2;
      if (list.argSize() == 0) {
        // Limit specification `1` is not of the form x->x0.
        Errors.printMessage(S.DiscreteLimit, "lim", F.List(arg2), engine);
        return null;
      }
      for (int i = 1; i < list.size(); i++) {
        IExpr rule = list.get(i);
        if (!rule.isRuleAST()) {
          // Limit specification `1` is not of the form x->x0.
          Errors.printMessage(S.DiscreteLimit, "lim", F.List(rule), engine);
          return null;
        }
        if (!addSpecification(result, rule.first(), rule.second(), rule, engine)) {
          return null;
        }
      }
      return result;
    }
    // Limit specification `1` is not of the form x->x0.
    Errors.printMessage(S.DiscreteLimit, "lim", F.List(arg2), engine);
    return null;
  }

  private static boolean addSpecification(List<IAST> specifications, IExpr variable, IExpr point,
      IExpr specification, EvalEngine engine) {
    if (!variable.isSymbol()) {
      // `1` is not a valid variable.
      Errors.printMessage(S.DiscreteLimit, "ivar", F.List(specification), engine);
      return false;
    }
    specifications.add(F.Rule(variable, point));
    return true;
  }

  /**
   * Compute the limits one variable after the other, in the given order.
   *
   * @return {@link F#NIL} if one of the steps couldn't be resolved
   */
  private static IExpr iteratedLimit(IExpr expr, List<IAST> limitSpecs, EvalEngine engine) {
    IExpr result = expr;
    for (int i = 0; i < limitSpecs.size(); i++) {
      ISymbol variable = (ISymbol) limitSpecs.get(i).arg1();
      IExpr limitPoint = limitSpecs.get(i).arg2();
      if (result.isFree(variable, true)) {
        continue;
      }
      IExpr step = singleLimit(result, variable, limitPoint, engine);
      if (step.isNIL()) {
        return F.NIL;
      }
      result = step;
    }
    return result;
  }

  /**
   * Compute <code>DiscreteLimit(expr, variable -> limitPoint)</code> for a single integer variable.
   *
   * @return {@link F#NIL} if the limit couldn't be determined
   */
  private static IExpr singleLimit(IExpr expr, ISymbol variable, IExpr limitPoint,
      EvalEngine engine) {
    IExpr sequence = rewriteIntegerPeriodic(expr, variable, engine).orElse(expr);

    IExpr oscillating = oscillatingLimit(sequence, variable, limitPoint, engine);
    if (oscillating.isPresent()) {
      return oscillating;
    }

    IExpr result = engine.evaluate(F.Limit(sequence, F.Rule(variable, limitPoint)));
    if (result.isNIL() || !result.isFree(S.Limit, true)) {
      // an unevaluated Limit(...) in the result means "not resolved" - leave the whole
      // DiscreteLimit(...) unevaluated instead of adopting a half-answer
      return F.NIL;
    }
    return result;
  }

  /**
   * Collapse trigonometric functions whose argument is an integer multiple of <code>Pi</code> times
   * the limit variable. Only valid because the limit variable runs through the integers.
   *
   * @return {@link F#NIL} if nothing was rewritten
   */
  private static IExpr rewriteIntegerPeriodic(IExpr expr, ISymbol variable, EvalEngine engine) {
    return expr.replaceAll(x -> {
      if (!x.isAST1() || !x.isFunctionID(ID.Sin, ID.Cos, ID.Tan)) {
        return F.NIL;
      }
      IExpr argument = x.first();
      if (argument.isFree(variable, true)) {
        return F.NIL;
      }
      // argument == factor * Pi * variable exactly, if the quotient is an integer
      IExpr factor = engine.evaluate(F.Divide(argument, F.Times(S.Pi, variable)));
      if (!factor.isInteger()) {
        return F.NIL;
      }
      switch (((org.matheclipse.core.interfaces.IBuiltInSymbol) x.head()).ordinal()) {
        case ID.Sin:
        case ID.Tan:
          return F.C0;
        case ID.Cos:
          // Cos(k*Pi*n) == ((-1)^k)^n
          return ((IInteger) factor).isEven() ? F.C1 : F.Power(F.CN1, variable);
        default:
          return F.NIL;
      }
    });
  }

  /**
   * Handle a product with a bounded oscillating factor such as <code>(-1)^n</code> or
   * <code>Cos(n)</code>: the product tends to <code>0</code> if the remaining factors do, and
   * oscillates otherwise.
   *
   * @return {@link F#NIL} if the sequence has no bounded oscillating factor, or if the remaining
   *         factors couldn't be resolved
   */
  private static IExpr oscillatingLimit(IExpr sequence, ISymbol variable, IExpr limitPoint,
      EvalEngine engine) {
    if (!sequence.isTimes()) {
      return isBoundedOscillating(sequence, variable, engine) ? S.Indeterminate : F.NIL;
    }
    IAST times = (IAST) sequence;
    IASTAppendable rest = F.TimesAlloc(times.argSize());
    boolean oscillating = false;
    for (int i = 1; i < times.size(); i++) {
      IExpr factor = times.get(i);
      if (isBoundedOscillating(factor, variable, engine)) {
        oscillating = true;
      } else {
        rest.append(factor);
      }
    }
    if (!oscillating) {
      return F.NIL;
    }
    IExpr restLimit = engine.evaluate(F.Limit(rest, F.Rule(variable, limitPoint)));
    if (restLimit.isNIL() || !restLimit.isFree(S.Limit, true)) {
      return F.NIL;
    }
    // bounded * 0 -> 0; bounded * anything else keeps oscillating
    return restLimit.isZero() ? F.C0 : S.Indeterminate;
  }

  /**
   * Test if <code>expr</code> is bounded and oscillating in the limit variable: a power with a unit
   * modulus number base like <code>(-1)^n</code>, or a sine/cosine, in both cases with an exponent
   * resp. argument that is linear in the limit variable.
   */
  private static boolean isBoundedOscillating(IExpr expr, ISymbol variable, EvalEngine engine) {
    if (expr.isFree(variable, true)) {
      return false;
    }
    if (expr.isPower()) {
      IExpr base = expr.base();
      if (!base.isNumber() || base.isZero() || !base.isFree(variable, true)) {
        return false;
      }
      if (!engine.evaluate(F.Abs(base)).isOne()) {
        return false;
      }
      return isLinear(expr.exponent(), variable, engine);
    }
    if (expr.isAST1() && expr.isFunctionID(ID.Sin, ID.Cos)) {
      return isLinear(expr.first(), variable, engine);
    }
    return false;
  }

  /**
   * Test if <code>expr</code> is a linear function of <code>variable</code> with a real rational
   * slope, i.e. if its derivative is a rational number.
   */
  private static boolean isLinear(IExpr expr, ISymbol variable, EvalEngine engine) {
    if (expr.isFree(variable, true)) {
      return false;
    }
    IExpr derivative = engine.evaluate(F.D(expr, variable));
    return derivative.isRational() && !derivative.isZero();
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.NHOLDALL);
    super.setUp(newSymbol);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
