package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.UniformFlags;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Implementation of the SymbolicIdentityArray function.
 * <p>
 * This function creates a symbolic representation of an identity array. It is often the result of
 * differentiating a tensor with respect to itself.
 * </p>
 * <p>
 * Usage: ( SymbolicIdentityArray[{n1, n2, ...}] )
 * </p>
 */
public class SymbolicIdentityArray extends AbstractEvaluator {

  public SymbolicIdentityArray() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (!arg1.isList()) {
      return ast.setAtCopy(1, arg1.makeList());
    }
    IAST dimensions = (IAST) arg1;
    if (dimensions.isUniform(UniformFlags.INTEGER)) {
      return F.NIL;
    }

    // Validate dimensions: Must be Positive Integers or Symbolic Expressions
    for (int i = 1; i <= dimensions.argSize(); i++) {
      IExpr d = dimensions.get(i);
      if (d.isInteger()) {
        if (!d.isPositive()) {
          // `1` is not a valid specification dimension for `2`.
          return Errors.printMessage(S.SymbolicIdentityArray, "dimss",
              F.List(d, S.SymbolicIdentityArray));
        }
      } else if (!d.isSymbol() && !d.isAST()) {
        // `1` is not a valid specification dimension for `2`.
        return Errors.printMessage(S.SymbolicIdentityArray, "dimss",
            F.List(d, S.SymbolicIdentityArray));
      }
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

}
