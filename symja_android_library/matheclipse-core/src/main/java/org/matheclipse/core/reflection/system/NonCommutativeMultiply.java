package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * <code>a ** b ** c
 * </code>
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * represents a general associative, but non-commutative, form of multiplication.
 *
 * </blockquote>
 *
 * <p>
 * Instances of <code>NonCommutativeMultiply</code> are automatically flattened because of the
 * {@link ISymbol#FLAT} attribute, but no other simplification is performed. In particular no
 * identity or zero element is folded away and the expression is not distributed over
 * {@link org.matheclipse.core.expression.S#Plus}.
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; a ** (b ** c) == (a ** b) ** c
 * True
 *
 * &gt;&gt; {0 ** a, 1 ** a}
 * {0**a,1**a}
 * </code>
 * </pre>
 */
public class NonCommutativeMultiply extends AbstractFunctionEvaluator {

  public NonCommutativeMultiply() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // NonCommutativeMultiply has no evaluation rules of its own. Flattening is performed by the
    // evaluation engine because of the ISymbol#FLAT attribute; everything else is left to the
    // rules a user associates with this symbol.
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.FULL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.FLAT, Attribute.ONEIDENTITY);
  }
}
