package org.matheclipse.core.expression;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * A built-in function which lives in a context other than <code>System`</code>, for example
 * <code>Internal`Bag</code>. It has no symbol ID: the parser finds it through
 * {@link ContextPath#registerSystemAlias(String, org.matheclipse.core.interfaces.IBuiltInSymbol)},
 * and only an {@link org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator} is called for it.
 *
 * <p>
 * Unlike a {@link BuiltInDummy} it keeps its context: it prints with the context name unless the
 * context is on <code>$ContextPath</code>, <code>Context(symbol)</code> gives it, and a symbol of
 * the same name in <code>Global`</code> doesn't take its place.
 */
public class ContextBuiltInSymbol extends BuiltInDummy {
  private static final long serialVersionUID = 4466238541305927261L;

  private final Context fContext;

  /**
   * @param context the context the symbol lives in
   * @param symbolName the name of the symbol without its context
   * @param evaluator the evaluator of the function, usually a core function evaluator
   */
  public ContextBuiltInSymbol(Context context, String symbolName, IFunctionEvaluator evaluator) {
    this(context, symbolName, ISymbol.NOATTRIBUTE, evaluator);
  }

  /**
   * @param context the context the symbol lives in
   * @param symbolName the name of the symbol without its context
   * @param attributes the attributes of the function, for example {@link ISymbol#HOLDALL}. They
   *        are set here because {@link #setEvaluator(IFunctionEvaluator)} doesn't call the
   *        evaluator's <code>setUp()</code>
   * @param evaluator the evaluator of the function, usually a core function evaluator
   */
  public ContextBuiltInSymbol(Context context, String symbolName, int attributes,
      IFunctionEvaluator evaluator) {
    super(symbolName, evaluator);
    fContext = context;
    fAttributes = attributes;
  }

  /** {@inheritDoc} */
  @Override
  public String fullFormString() {
    return fContext.completeContextName() + fSymbolName;
  }

  /** {@inheritDoc} */
  @Override
  public Context getContext() {
    return fContext;
  }

  /** {@inheritDoc} */
  @Override
  protected ISymbol globalSubstitute(EvalEngine engine) {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public ISymbol mapToGlobal(EvalEngine engine) {
    // a block localizes this symbol itself
    return null;
  }
}
