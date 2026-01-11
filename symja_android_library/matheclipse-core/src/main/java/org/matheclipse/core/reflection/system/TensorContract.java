package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.data.ArraySymbolExpr;
import org.matheclipse.core.expression.data.MatrixSymbolExpr;
import org.matheclipse.core.expression.data.VectorSymbolExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Implementation of the TensorContract function.
 * <p>
 * Contracts specified slots of a tensor.
 * </p>
 */
public class TensorContract extends AbstractEvaluator {

  public TensorContract() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    if (ast.argSize() != 2) {
      return F.NIL;
    }

    IExpr tensor = ast.arg1();
    IExpr slots = ast.arg2();

    // 1. Handle MatrixSymbol
    if (tensor instanceof MatrixSymbolExpr) {
      if (isTraceContraction(slots)) {
        return F.Tr(tensor);
      }
    }

    // 2. Handle ArraySymbol
    if (tensor instanceof ArraySymbolExpr) {
      return evaluateArraySymbol((ArraySymbolExpr) tensor, slots);
    }

    // 3. Handle VectorSymbol (Vectors cannot be contracted)
    if (tensor instanceof VectorSymbolExpr) {
      return F.NIL;
    }

    // 4. Handle Explicit Lists (Calculation)
    if (tensor.isList()) {
      return contractList((IAST) tensor, slots, engine);
    }

    return F.NIL;
    }

  /**
   * logic for ArraySymbolExpr. Checks validity and simplifies to Tr if applicable.
   */
  private IExpr evaluateArraySymbol(ArraySymbolExpr array, IExpr slots) {
    // Dimensions are stored as an IAST: {d1, d2, ...}
    IAST dimensions = array.getDimensions();
    int rank = dimensions.argSize();

    // Parse slots to check validity
    List<int[]> contractions = parseSlots(slots);
    if (contractions == null) {
      return F.NIL; // Malformed slots
    }

    // Validate indices against rank
    for (int[] pair : contractions) {
      int u = pair[0];
      int v = pair[1];
      if (u < 1 || u > rank || v < 1 || v > rank || u == v) {
        // Invalid index for this symbol -> likely return NIL or error
        // returning NIL leaves it unevaluated, which is standard for symbolic errors
        return F.NIL;
      }
    }

    // Special Simplification:
    // If it is a Rank 2 array and we are contracting {1, 2}, convert to Tr.
    if (rank == 2 && contractions.size() == 1) {
      int[] pair = contractions.get(0);
      if ((pair[0] == 1 && pair[1] == 2) || (pair[0] == 2 && pair[1] == 1)) {
        return F.Tr(array);
      }
    }

    // For higher ranks or other indices, no standard simplification exists
    // (unless we implement partial trace logic or TensorReduce).
    // We return NIL to keep it as TensorContract[ArraySymbol[...], ...]
    return F.NIL;
  }

  /**
   * Performs contraction on an explicit nested list.
   */
  private IExpr contractList(IAST tensor, IExpr slots, EvalEngine engine) {
    List<int[]> contractions = parseSlots(slots);
    if (contractions == null) {
      return F.NIL;
        }

    IExpr currentTensor = tensor;

    for (int[] pair : contractions) {
      int u = pair[0];
      int v = pair[1];

      int rank = getRank(currentTensor);
      if (u < 1 || u > rank || v < 1 || v > rank || u == v) {
        return F.NIL;
      }

      if (u > v) {
        int swap = u;
        u = v;
        v = swap;
      }

      IASTAppendable permutation = F.ListAlloc(rank);
      permutation.append(u);
      permutation.append(v);
      for (int k = 1; k <= rank; k++) {
        if (k != u && k != v) {
          permutation.append(k);
        }
      }

      IExpr transposed = engine.evaluate(F.Transpose(currentTensor, permutation));
      if (!transposed.isList()) {
        return F.NIL;
      }

      currentTensor = engine.evaluate(F.Tr(transposed));

      updateRemainingIndices(contractions, u, v);
    }

    return currentTensor;
    }

  private void updateRemainingIndices(List<int[]> pairs, int removedU, int removedV) {
    for (int[] pair : pairs) {
      if (pair[0] > removedV)
        pair[0] -= 2;
      else if (pair[0] > removedU)
        pair[0] -= 1;

      if (pair[1] > removedV)
        pair[1] -= 2;
      else if (pair[1] > removedU)
        pair[1] -= 1;
        }
    }

    private List<int[]> parseSlots(IExpr slots) {
      List<int[]> list = new ArrayList<>();
      if (!slots.isList())
        return null;

      IAST slotList = (IAST) slots;

      // Case A: {1, 2}
      if (slotList.argSize() == 2 && slotList.arg1().isInteger() && slotList.arg2().isInteger()) {
        list.add(new int[] {slotList.arg1().toIntDefault(), slotList.arg2().toIntDefault()});
        return list;
      }

      // Case B: {{1, 2}, {3, 4}}
      for (IExpr arg : slotList) {
        if (arg.isList()) {
          IAST pair = (IAST) arg;
          if (pair.argSize() == 2 && pair.arg1().isInteger() && pair.arg2().isInteger()) {
            list.add(new int[] {pair.arg1().toIntDefault(), pair.arg2().toIntDefault()});
          } else {
            return null;
          }
        } else {
          return null;
        }
      }
      return list;
    }

    private int getRank(IExpr tensor) {
      if (tensor.isList()) {
        return LinearAlgebraUtil.arrayDepth(tensor);
      }
      return 0;
    }

    private boolean isTraceContraction(IExpr slots) {
      if (!slots.isList())
        return false;
      IAST slotList = (IAST) slots;
      if (slotList.argSize() == 1 && slotList.arg1().isList()) {
        IAST inner = (IAST) slotList.arg1();
        return inner.argSize() == 2 && inner.arg1().isOne() && inner.arg2().toIntDefault() == 2;
      }
      if (slotList.argSize() == 2) {
        return slotList.arg1().isOne() && slotList.arg2().toIntDefault() == 2;
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.PROTECTED);
    }
}