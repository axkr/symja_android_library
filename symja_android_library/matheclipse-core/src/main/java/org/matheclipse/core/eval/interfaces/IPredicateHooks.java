package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The hooks of a predicate with one argument and possible options, as
 * {@link AbstractPredicateEvaluator} and {@link AbstractCorePredicateEvaluator} declare them, and
 * the evaluation of a call through them.
 */
public interface IPredicateHooks {

  /**
   * Evaluate the function for 1 argument.
   *
   * @param arg1
   * @param engine
   * @return
   */
  boolean evalArg1Boole(final IExpr arg1, final EvalEngine engine);

  /**
   * Evaluate the function for 1 argument, with options.
   *
   * @param arg1
   * @param engine
   * @param options
   * @return
   */
  default boolean evalArg1Boole(final IExpr arg1, EvalEngine engine, OptionArgs options) {
    return false;
  }

  /**
   * Apply the predicate to <code>arg1</code>, the first argument of <code>ast</code>, threading
   * over a list or association if the function is <code>Listable</code>. A second argument of
   * <code>ast</code> holds the options.
   */
  default IExpr evaluatePredicate(final IAST ast, IExpr arg1, final EvalEngine engine) {
    if (arg1.isList() || arg1.isAssociation()) {
      // thread over list?
      if (Attribute.LISTABLE.isSetIn(ast.topHead().getAttributes())) {
        return arg1.mapThread(ast, 1);
      }
    }
    if (ast.size() == 3) {
      OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
      return F.booleSymbol(evalArg1Boole(arg1, engine, options));
    }
    return F.booleSymbol(evalArg1Boole(arg1, engine));
  }
}
