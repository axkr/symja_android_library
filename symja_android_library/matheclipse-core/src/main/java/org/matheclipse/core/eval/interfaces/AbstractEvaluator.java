package org.matheclipse.core.eval.interfaces;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Abstract interface for built-in Symja functions. The <code>numericEval()</code> method delegates
 * to the <code>evaluate()</code>
 */
public abstract class AbstractEvaluator implements IFunctionEvaluator {

  /**
   * The default return value for {@link #evaluate(IAST, EvalEngine)} if evaluation fails.
   * 
   * @return {@link F#NIL}
   */
  public IExpr defaultReturn() {
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr numericEval(final IAST ast, final EvalEngine engine) {
    return evaluate(ast, engine);
  }

  /** Evaluate built-in rules and define Attributes for a function. */
  @Override
  public void setUp(final ISymbol newSymbol) {
    // do nothing
  }

  /**
   * This method will be called by {@link #evaluate(IAST, EvalEngine)}. If this method throws a
   * {@link RuntimeException}, the exception will be catched in {@link #evaluate(IAST, EvalEngine)}.
   * 
   * <p>
   * <b>Symbolic evaluation</b> of a function. The method {@link IAST#head()} contains the
   * <i>head</i> (i.e. the function symbol) of this abstract syntax tree (AST).
   *
   * <p>
   * From <code>ast.get(1)</code> to <code>ast.get(n)</code> the <code>ast</code> contains the first
   * to n-th argument of the function (alternatively you get the first to fifth argument with the
   * methods {@link IAST#arg1()}, {@link IAST#arg2()}, {@link IAST#arg3()} ... {@link IAST#arg5()}.
   *
   * <p>
   * <b>Example:</b> the expression <code>Binomial(n,m)</code> is represented as AST with <code>
   * ast.head() &lt;=&gt; F.Binomial</code>, <code>ast.arg1() &lt;=&gt; n</code> and
   * <code>ast.arg2() &lt;=&gt; m</code>
   *
   * <p>
   * If necessary use the methods from the <code>Validate</code> class to check the number or types
   * of arguments in the evaluate method.
   *
   * <p>
   * <b>Note:</b> if the symbolic evaluation isn't possible or no result is found the evaluate
   * method returns with a {@link F#NIL} value without throwing an exception!
   *
   * @param ast the abstract syntax tree (AST) which should be evaluated
   * @param engine the users current evaluation engine
   * @return the evaluated object or <code>F#NIL</code>, if evaluation isn't possible
   * @see org.matheclipse.core.eval.exception.Validate
   */
  public IExpr evalCatched(final IAST ast, final EvalEngine engine) throws RuntimeException {
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    try {
      return evalCatched(ast, engine);
    } catch (RuntimeException rex) {
      Errors.printMessage(ast.topHead(), rex, engine);
    }
    return defaultReturn();
  }
}
