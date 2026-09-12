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
    IExpr symmetry = S.None;
    if (argSize == 3) {
      // a lone third argument is ambiguous between domain and symmetry; WMA resolves it by shape,
      // trying domain first and falling back to symmetry (see MatrixSymbol for the confirming
      // ground-truth example)
      IExpr arg3 = ast.arg3();
      if (SymbolicArrayUtil.isValidDomain(arg3)) {
        domain = arg3;
      } else if (SymbolicArrayUtil.isSymmetryShaped(arg3)) {
        if (!SymbolicArrayUtil.isValidSymmetry(arg3, dimensionsList)) {
          // Symmetry specification `1` is incompatible with expression `2`.
          return Errors.printMessage(S.ArraySymbol, "symmcomp", F.List(arg3, dimensionsList),
              engine);
        }
        symmetry = arg3;
      } else {
        // Invalid symmetry specification `1`.
        return Errors.printMessage(S.ArraySymbol, "symm", F.List(arg3), engine);
      }
    } else if (argSize == 4) {
      domain = ast.arg3();
      if (!SymbolicArrayUtil.isValidDomain(domain)) {
        // `1` is not a valid domain specification for `2`.
        return Errors.printMessage(S.ArraySymbol, "domss", F.List(domain, S.ArraySymbol), engine);
      }
      symmetry = ast.arg4();
      if (!SymbolicArrayUtil.isSymmetryShaped(symmetry)) {
        // Invalid symmetry specification `1`.
        return Errors.printMessage(S.ArraySymbol, "symm", F.List(symmetry), engine);
      }
      if (!SymbolicArrayUtil.isValidSymmetry(symmetry, dimensionsList)) {
        // Symmetry specification `1` is incompatible with expression `2`.
        return Errors.printMessage(S.ArraySymbol, "symmcomp", F.List(symmetry, dimensionsList),
            engine);
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
    // confirmed against real Mathematica (2026-09-12): Attributes[MatrixSymbol] is
    // {NHoldAll,NonThreadable,Protected,ReadProtected}, so the dimension/domain/symmetry
    // arguments are not evaluated numerically either
    newSymbol.setAttributes(Attribute.NONTHREADABLE, Attribute.NHOLDALL);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
