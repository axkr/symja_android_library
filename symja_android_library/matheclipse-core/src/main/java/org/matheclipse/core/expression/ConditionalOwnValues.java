package org.matheclipse.core.expression;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Several delayed own values of a symbol: <code>x := {}; x := {1} /; c == 1; x := {2} /; c == 2
 * </code> keeps all three definitions. The conditional ones are tried in the order they were given
 * and the unconditional one is the fallback; a new definition with the same condition replaces the
 * old one, and <code>x = value</code> replaces them all, as a conditional definition replaces a
 * value of <code>Set</code>.
 *
 * <p>
 * A symbol has a single value slot, so more than one definition is stored there as
 * <code>head({condition1, condition2, ...}, fallback)</code> with a private head no input can name.
 */
public final class ConditionalOwnValues {
  private static final ISymbol HEAD = new Symbol("ConditionalOwnValues", Context.DUMMY);

  private ConditionalOwnValues() {}

  /** Whether an assigned value holds several definitions. */
  public static boolean isConditionalOwnValues(IExpr value) {
    return value.head() == HEAD;
  }

  /**
   * The value to store for the definition <code>symbol := value</code>.
   *
   * @return <code>value</code> if the symbol has no delayed value this definition goes with
   */
  public static IExpr merge(ISymbol symbol, IExpr value) {
    if (!symbol.hasAssignedSymbolValue()
        || !symbol.isEvalFlagOn(ISymbol.SETDELAYED_FLAG_ASSIGNED_VALUE)) {
      return value;
    }
    IExpr existing = symbol.assignedValue();
    IExpr fallback = F.NIL;
    IAST conditions = F.CEmptyList;
    if (existing != null) {
      if (isConditionalOwnValues(existing)) {
        conditions = (IAST) existing.first();
        fallback = existing.argSize() == 2 ? existing.second() : F.NIL;
      } else if (existing.isCondition()) {
        conditions = F.list(existing);
      } else {
        fallback = existing;
      }
    }
    if (conditions.isEmpty() && !value.isCondition()) {
      return value;
    }
    if (!value.isCondition()) {
      fallback = value;
    } else {
      conditions = withCondition(conditions, value);
    }
    if (fallback.isNIL()) {
      return conditions.argSize() == 1 ? conditions.arg1() : F.unaryAST1(HEAD, conditions);
    }
    return F.binaryAST2(HEAD, conditions, fallback);
  }

  /** The list with <code>condition</code> in place of one of the same test, or at its end. */
  private static IAST withCondition(IAST conditions, IExpr condition) {
    int position = conditions.indexOf(x -> x.second().equals(condition.second()));
    return position > 0 ? conditions.setAtCopy(position, condition)
        : conditions.appendClone(condition);
  }

  /**
   * The value of the first definition which applies.
   *
   * @return {@link F#NIL} if none applies
   */
  public static IExpr evaluate(IAST values, EvalEngine engine) {
    IAST conditions = (IAST) values.first();
    for (int i = 1; i < conditions.size(); i++) {
      IExpr result = ISymbol.evalAssignedValue(conditions.get(i), true, engine);
      if (result.isPresent()) {
        return result;
      }
    }
    return values.argSize() == 2
        ? ISymbol.evalAssignedValue(values.second(), true, engine)
        : F.NIL;
  }

  /** The definitions held by an assigned value, in the order they are tried. */
  public static IAST definitions(IExpr value) {
    if (!isConditionalOwnValues(value)) {
      return F.list(value);
    }
    IAST conditions = (IAST) value.first();
    return value.argSize() == 2 ? conditions.appendClone(value.second()) : conditions;
  }

  /** The rules of <code>OwnValues(symbol)</code>. */
  public static IAST ownValues(ISymbol symbol) {
    IExpr value = symbol.assignedValue();
    if (value == null) {
      return F.CEmptyList;
    }
    IAST definitions = definitions(value);
    IASTAppendable result = F.ListAlloc(definitions.argSize());
    definitions.forEach(x -> result.append(F.RuleDelayed(F.HoldPattern(symbol), x)));
    return result;
  }
}
