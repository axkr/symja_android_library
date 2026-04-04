package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * FiniteAbelianGroupCount[n] - returns the number of abelian groups of order n. Equals the product
 * of integer partitions of the exponents in the prime factorization of n.
 */
public class FiniteAbelianGroupCount extends AbstractEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.arg1().isInteger()) {
      IInteger n = (IInteger) ast.arg1();

      // Abort evaluation for zero and negative inputs
      if (!n.isPositive()) {
        // Positive integer expected at position `2` in `1`.
        return Errors.printMessage(S.FiniteAbelianGroupCount, "intp", F.List(ast, F.C1), engine);
      }
      if (n.isOne()) {
        return F.C1;
      }

      IExpr factored = engine.evaluate(F.FactorInteger(n));
      if (factored.isList()) {
        IAST list = (IAST) factored;
        IExpr result = F.C1;

        // Iterate through the factorization result list
        for (int i = 1; i <= list.argSize(); i++) {
          IExpr pair = list.get(i);
          if (pair.isList() && ((IAST) pair).argSize() == 2) {
            IExpr exponent = ((IAST) pair).arg2();
            IExpr partitionCount = engine.evaluate(F.PartitionsP(exponent));
            result = engine.evaluate(F.Times(result, partitionCount));
          }
        }
        return result;
      }
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}