package org.matheclipse.core.compile;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A Symja expression which was compiled to JVM bytecode.
 *
 * <p>
 * Implemented by <code>org.matheclipse.compile.expression.CompiledFunctionExpr</code> in the
 * <code>matheclipse-compile</code> module. <code>matheclipse-core</code> must not depend on that
 * module, so the numerical functions in core reach a compiled function through this interface and
 * {@link IExprCompiler} only.
 *
 * @see IExprCompiler
 */
public interface ICompiledFunction {

  /**
   * Evaluate the compiled function with the arguments <code>ast.arg1(), ast.arg2(), ...</code>.
   *
   * @param ast the arguments of the call; the head is ignored
   * @param engine the evaluation engine
   * @return the result, or {@link org.matheclipse.core.expression.F#NIL} if the compiled code
   *         didn't produce a value
   * @throws ArgumentTypeException if a non-numeric argument was passed; the caller is expected to
   *         fall back to an uncompiled evaluation
   */
  IExpr evaluate(IAST ast, EvalEngine engine);

  /**
   * Evaluate a compiled unary function of one real argument.
   *
   * @param arg the argument value
   * @param engine the evaluation engine
   * @return the computed value, or {@link Double#NaN} if it isn't a real number
   */
  double evalDouble(double arg, EvalEngine engine);

  /**
   * The uncompiled expression this function was compiled from, for the fall-back path and for error
   * messages.
   */
  IExpr getExpr();

  /** The list of variables of the compiled function, in argument order. */
  IAST getVariables();
}
