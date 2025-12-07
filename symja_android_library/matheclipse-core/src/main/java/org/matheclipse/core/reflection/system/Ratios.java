package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * Implements the Ratios function.
 * <p>
 * Supported usages: 1. Ratios[list] - Standard ratios of successive elements (Order 1). 2.
 * Ratios[list, n] - n-th repeated ratios (Order n). 3. Ratios[array, {n1, n2, ...}] - Applies n1-th
 * ratios to dim 1, n2-th to dim 2, etc.
 * </p>
 */
public class Ratios extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();

    // Ratios requires the first argument to be an AST (List/Array)
    if (!arg1.isAST()) {
      return F.NIL;
    }
    IAST list = (IAST) arg1;

    if (ast.isAST1()) {
      // Standard usage Ratios[list] (Default order 1)
      return calculateRepeatedRatios(list, 1);
    }
    IExpr arg2 = ast.arg2();



    if (arg2.isList()) {
      // Tensor usage Ratios(array, {n1, n2, ...})
      // Example: Ratios(matrix, {0, 1}) -> Identity on rows, Ratios on columns.
      IntArrayList dimensions = LinearAlgebraUtil.dimensions(list);
      if (dimensions.size() < arg2.argSize()) {
        // Requested ratios `1` exceeds the array depth `2`, of the input.
        return Errors.printMessage(S.Ratios, "depthratios", F.List(arg2, F.ZZ(dimensions.size())),
            engine);
      }
      return evaluateTensorRatios(list, (IAST) arg2, engine);
    }


    int n = arg2.toIntDefault();
    if (n != Integer.MIN_VALUE) {
      // Repeated usage Ratios(list, n)
      return calculateRepeatedRatios(list, n);
    }
    // Single or list of non-negative machine-sized integers expected at position `1` of `2`.
    return Errors.printMessage(S.Ratios, "ilmsn", F.List(F.C2, ast), engine);
  }

  /**
   * Applies Ratios recursively along dimensions specified in dimList. dimList = {orderDim1,
   * orderDim2, ...}
   */
  private static IAST evaluateTensorRatios(IAST currentExpr, IAST dimOrders, EvalEngine engine) {
    // Iterate through the orders for each dimension
    // i=1 corresponds to Dimension 1, i=2 to Dimension 2, etc.
    int maxDim = dimOrders.argSize();

    for (int i = 1; i <= maxDim; i++) {
      int order = dimOrders.get(i).toIntDefault();

      if (!F.isPresent(order)) {
        return F.NIL;
      }

      if (order == 0) {
        // If order is 0, we do nothing for this dimension (Identity)
        continue;
      }

      if (!currentExpr.isAST()) {
        return F.NIL;
      }

      if (i == 1) {
        // Dimension 1: Direct application
        currentExpr = calculateRepeatedRatios(currentExpr, order);
      } else {
        // Dimension > 1: Map the operation to the deeper level

        // We need to construct a Map command:
        // Map(Function(x, Ratios(x, order)), currentExpr, {levelSpec})
        // where levelSpec is {i-1} to target exactly the nested lists at that depth.
        IExpr functionToMap = order == 1 ? //
            S.Ratios : //
            F.Function(F.binaryAST2(S.Ratios, F.Slot1, F.ZZ(order)));
        // Map at level {i-1}
        IExpr temp = engine.evaluate(F.Map(functionToMap, currentExpr, F.List(F.ZZ(i - 1))));
        if (!temp.isAST()) {
          return F.NIL;
        }
        currentExpr = (IAST) temp;
      }
    }

    return currentExpr;
  }

  /**
   * Helper: Calculates the n-th repeated ratios on a simple list (1D).
   */
  private static IAST calculateRepeatedRatios(IAST list, int n) {
    if (n == 0) {
      return list;
    }
    if (n < 0) {
      return F.NIL;
    }

    IAST currentExpr = list;
    for (int k = 0; k < n; k++) {
      if (!currentExpr.isAST()) {
        return F.NIL;
      }
      IAST currentList = currentExpr;
      int size = currentList.argSize();

      if (size < 2) {
        return F.List();
      }

      IASTAppendable nextList = F.ListAlloc(size - 1);
      for (int i = 1; i < size; i++) {
        IExpr numerator = currentList.get(i + 1);
        IExpr denominator = currentList.get(i);
        nextList.append(numerator.times(denominator.inverse()));
      }
      currentExpr = nextList;
    }
    return currentExpr;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
