package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * NCache(x, xn)
 *
 * <p>
 * Pairs the exact value <code>x</code> with an approximate numerical value <code>xn</code> of the
 * same quantity, and evaluates to <code>x</code>.
 *
 * <p>
 * The pair is a note left by whoever wrote it, not a computation: the second value is what
 * <code>N</code> of the first would give, worked out once so that a renderer can use it without
 * doing the arithmetic again. Nothing downstream needs the note, because asking for the numerical
 * value of the exact expression gives the same answer, so the exact value is what comes out and the
 * cached one is dropped.
 *
 * <p>
 * These appear in graphics that were formatted with the coordinates kept in exact form, where every
 * coordinate arrives as its exact value alongside the number that was drawn. Collapsing to the
 * exact value is what lets such an expression be read back and computed with.
 */
public class NCache extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    return ast.arg1();
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

  @Override
  public int status() {
    return ImplementationStatus.FULL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    // NHoldRest: the cached value is already a number, and asking for the numerical value of a
    // number again is work that changes nothing
    newSymbol.setAttributes(ISymbol.NHOLDREST);
  }
}
