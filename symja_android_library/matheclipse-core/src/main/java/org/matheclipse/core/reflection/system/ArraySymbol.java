package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.SymbolicArrayUtil;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ArraySymbolExpr;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * ArraySymbol(a, {n1, n2, ...})
 * </pre>
 *
 * <blockquote>
 * <p>
 * represents a symbolic <code>n1</code> x <code>n2</code> x ... array named <code>a</code>.
 * </p>
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; TensorRank(ArraySymbol(a, {n1, n2, n3}))
 * 3
 * </pre>
 */
public class ArraySymbol extends AbstractEvaluator {

  public ArraySymbol() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    final int argSize = ast.argSize();
    IExpr name = ast.arg1();
    IExpr dimensions = ast.arg2();

    if (!dimensions.isList() || dimensions.argSize() < 1) {
      // The list `1` of dimensions `3` must have length `2`.
      return Errors.printMessage(S.ArraySymbol, "rankl",
          F.List(dimensions, F.C1, F.stringx("for an array")), engine);
    }
    IAST dimensionsList = (IAST) dimensions;
    for (int i = 1; i < dimensionsList.size(); i++) {
      IExpr dimension = dimensionsList.get(i);
      if (!SymbolicArrayUtil.isValidDimension(dimension)) {
        // `1` is not a valid dimension specification for `2`.
        return Errors.printMessage(S.ArraySymbol, "dimss", F.List(dimension, S.ArraySymbol),
            engine);
      }
    }

    IExpr domain = S.Complexes;
    if (argSize >= 3) {
      domain = ast.arg3();
      if (!SymbolicArrayUtil.isValidDomain(domain)) {
        // `1` is not a valid domain specification for `2`.
        return Errors.printMessage(S.ArraySymbol, "domss", F.List(domain, S.ArraySymbol), engine);
      }
    }

    IExpr symmetry = S.None;
    if (argSize == 4) {
      symmetry = ast.arg4();
      if (!SymbolicArrayUtil.isValidSymmetry(symmetry, dimensionsList)) {
        // `1` is not a valid symmetry specification for `2`.
        return Errors.printMessage(S.ArraySymbol, "symss", F.List(symmetry, S.ArraySymbol), engine);
      }
    }

    return new ArraySymbolExpr(name, dimensionsList, domain, symmetry);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_4;
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
