package org.matheclipse.core.reduce;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The entry point the built-in functions use to decide a condition over a discrete domain.
 *
 * <p>
 * The engine is pure: it lowers an expression into the linear IR, decides it there, and emits the
 * answer. It never evaluates <code>Reduce</code> or <code>Solve</code>, which is what keeps the
 * integer paths of those two functions from calling each other.
 *
 * <p>
 * Every entry point returns {@link F#NIL} when it cannot decide the input. That is a deliberate
 * decline, not an answer: a caller must leave the expression unevaluated rather than report an
 * empty or truncated solution set.
 */
public final class IntegerReduceEngine {

  private IntegerReduceEngine() {}

  /**
   * Eliminate the quantifiers of a formula over the integers.
   *
   * @param expr the quantified formula
   * @param engine the evaluation engine, used only to evaluate the emitted expression
   * @return the quantifier free condition, or {@link F#NIL} if the formula is outside linear
   *         integer arithmetic
   */
  public static IExpr resolve(IExpr expr, EvalEngine engine) {
    Formula formula = Lowering.lower(expr);
    if (formula == null) {
      return F.NIL;
    }
    Formula result = Presburger.eliminateQuantifiers(formula);
    if (result == null) {
      return F.NIL;
    }
    return engine.evaluate(Emitter.formula(result));
  }

  /**
   * Reduce a condition over a discrete domain.
   *
   * @param condition the condition to reduce
   * @param variables the variables of the reduction
   * @param domainSymbol {@code Integers}, {@code Primes} or {@code Rationals}
   * @param engine the evaluation engine
   * @return the reduced condition, or {@link F#NIL} if no exact method applies
   */
  public static IExpr reduce(IExpr condition, IAST variables, ISymbol domainSymbol,
      EvalEngine engine) {
    IntegerDomain domain = IntegerDomain.of(domainSymbol);
    if (domain != IntegerDomain.INTEGERS) {
      // the prime and rational domains are not decided by the linear engine yet
      return F.NIL;
    }
    Lowering.LinearRequest request = Lowering.request(condition, variables, domain);
    if (request == null) {
      return F.NIL;
    }
    if (!request.formula().containsQuantifier()) {
      // quantifier free conditions still take the established routes
      return F.NIL;
    }
    Formula result = Presburger.eliminateQuantifiers(request.formula());
    if (result == null) {
      return F.NIL;
    }
    IExpr expression = Emitter.formula(result, request.targets());
    expression = Emitter.withDomainConditions(expression, request.targets(), domain);
    return engine.evaluate(expression);
  }
}
