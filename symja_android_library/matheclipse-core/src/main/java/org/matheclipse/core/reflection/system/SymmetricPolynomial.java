package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Combinatoric;
import org.matheclipse.core.builtin.Combinatoric.KSubsetsList;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import com.google.common.math.IntMath;

/**
 * Implementation of SymmetricPolynomial, which calculates
 * <code>SymmetricPolynomial(k, {x1, ..., xn})</code> - the k-th elementary symmetric polynomial.
 */
public class SymmetricPolynomial extends AbstractFunctionEvaluator {

  public SymmetricPolynomial() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    int k = arg1.toIntDefault();
    if (!F.isPresent(k)) {
      return F.NIL;
    }

    IAST vars = ast.arg2().makeList();
    int n = vars.argSize();
    if (k < 0) {
      return F.C0;
    }
    if (k == 0) {
      return F.C1;
    }
    if (k > n) {
      return F.C0;
    }
    try {
      int sumArgSize = IntMath.binomial(n, k);
      if (sumArgSize != Integer.MAX_VALUE) {
        if (Config.MAX_AST_SIZE < sumArgSize) {
          ASTElementLimitExceeded.throwIt(sumArgSize);
        }
        IASTAppendable sumResult = F.PlusAlloc(sumArgSize);

        final KSubsetsList iter = Combinatoric.subsets(vars, k, F.ast(S.Times), 1);
        for (IAST part : iter) {
          if (part == null) {
            break;
          }
          sumResult.append(part);
        }
        return sumResult;
      }
    } catch (IllegalArgumentException iae) {
      Errors.printMessage(S.SymmetricPolynomial, iae);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }

}
