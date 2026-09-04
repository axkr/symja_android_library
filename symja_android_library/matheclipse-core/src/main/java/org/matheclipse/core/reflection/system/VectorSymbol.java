package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.SymbolicArrayUtil;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.VectorSymbolExpr;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * VectorSymbol(v, n)
 * </pre>
 *
 * <blockquote>
 * <p>
 * represents a symbolic vector of length <code>n</code> named <code>v</code>.
 * </p>
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; TensorDimensions(VectorSymbol(v, n))
 * {n}
 * </pre>
 */
public class VectorSymbol extends AbstractEvaluator {

  public VectorSymbol() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    final int argSize = ast.argSize();
    IExpr name = ast.arg1();
    IExpr dimensions = ast.arg2();
    if (dimensions.isList() && dimensions.argSize() != 1) {
      // The list `1` of dimensions `3` must have length `2`.
      return Errors.printMessage(S.VectorSymbol, "rankl",
          F.List(dimensions, F.C1, F.stringx("for a vector")), engine);
    }
    // VectorSymbol(v, {n}) is accepted as well and is stored as the scalar dimension n
    IExpr dimension = dimensions.isList() ? dimensions.first() : dimensions;
    if (!SymbolicArrayUtil.isValidDimension(dimension)) {
      // Invalid dimension specification `1`.
      return Errors.printMessage(S.VectorSymbol, "nodim", F.List(dimension), engine);
    }

    IExpr domain = S.Complexes;
    if (argSize == 3) {
      domain = ast.arg3();
      if (!SymbolicArrayUtil.isValidDomain(domain)) {
        // `1` is not a valid domain specification for `2`.
        return Errors.printMessage(S.VectorSymbol, "domss", F.List(domain, S.VectorSymbol), engine);
      }
    }

    return new VectorSymbolExpr(name, dimension, domain);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.NONTHREADABLE);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
