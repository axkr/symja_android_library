package org.matheclipse.internal;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The evaluator of an <code>Internal`</code> function.
 *
 * <p>
 * An <code>Internal`</code> function has no symbol ID, and the engine calls only a core function
 * evaluator for such a symbol, with the arguments as they were written. This class checks the
 * number of arguments, evaluates those the attributes of the function don't hold and passes them
 * to {@link #evaluateArgs(IAST, EvalEngine)}.
 */
public abstract class InternalFunction extends AbstractCoreFunctionEvaluator {
  private final int fMinArgs;

  private final int fMaxArgs;

  /**
   * @param minArgs the least number of arguments
   * @param maxArgs the largest number of arguments
   */
  protected InternalFunction(int minArgs, int maxArgs) {
    fMinArgs = minArgs;
    fMaxArgs = maxArgs;
  }

  @Override
  public final IExpr evaluate(final IAST ast, final EvalEngine engine) {
    int argSize = ast.argSize();
    if (argSize < fMinArgs || argSize > fMaxArgs) {
      return Errors.printArgMessage(ast, new int[] {fMinArgs, fMaxArgs}, engine);
    }
    int attributes = ast.head().isSymbol() ? ((ISymbol) ast.head()).getAttributes()
        : ISymbol.NOATTRIBUTE;
    IASTMutable evaluated = engine.evalArgs(ast, attributes, false);
    return evaluateArgs(evaluated.isPresent() ? evaluated : ast, engine);
  }

  /**
   * Evaluate the function for its arguments, those which the attributes of the function don't
   * hold already evaluated.
   *
   * @param ast the function with its arguments; the number of arguments has been checked
   * @param engine the evaluation engine
   * @return the result or {@link org.matheclipse.core.expression.F#NIL} if the function stays
   *         unevaluated
   */
  protected abstract IExpr evaluateArgs(IAST ast, EvalEngine engine);
}
