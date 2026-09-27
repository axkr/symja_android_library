package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * <code>MinValue(f, x)
 * MinValue({f, cons}, {x, y,...})
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * gives the exact minimum value of <code>f</code>. The same class implements
 * <code>MaxValue</code>.
 * </p>
 * </blockquote>
 */
public class MinValue extends AbstractFunctionEvaluator {

  private final boolean maximize;

  public MinValue() {
    this(false);
  }

  public MinValue(boolean maximize) {
    this.maximize = maximize;
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IASTAppendable optimize = F.ast(maximize ? S.Maximize : S.Minimize, ast.argSize());
    optimize.appendArgs(ast);
    IExpr result = engine.evaluate(optimize);
    if (result.isList2() && (result.second().isListOfRules() || result.second().isList())) {
      return result.first();
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }
}
