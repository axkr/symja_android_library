package org.matheclipse.core.eval.util;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IDimensionFunction;
import org.matheclipse.core.interfaces.IExpr;

public class CreateTensor {
  IDimensionFunction<IExpr> function;
  int[] dimension;

  public CreateTensor(IDimensionFunction<IExpr> function, int[] dims) {
    this.function = function;
    this.dimension = dims;
  }

  private void createTensorRecursive(IASTMutable list, int[] dims, int position, int[] index) {
    final int size = dims[position];
    if (dims.length - 1 == position) {
      for (int i = 1; i <= size; i++) {
        index[position] = i;
        list.set(i, function.apply(index));
      }
      return;
    }
    final int size2 = dims[position + 1];
    for (int i = 1; i <= size; i++) {
      index[position] = i;
      IASTMutable currentList = F.ast(S.List, size2);
      list.set(i, currentList);
      createTensorRecursive(currentList, dims, position + 1, index);
    }
  }

  private IASTMutable createTensorMutable(IExpr head, int[] dims) {
    IASTMutable list = F.ast(head, dims[0]);
    int[] index = new int[dims.length];
    for (int i = 0; i < index.length; i++) {
      index[i] = 1;
    }
    createTensorRecursive(list, dims, 0, index);
    return list;
  }

  public IASTMutable createTensor() {
    int[] dims = new int[dimension.length];
    for (int i = 0; i < dims.length; i++) {
      dims[i] = dimension[i];
    }
    return createTensorMutable(S.List, dims);
  }
}
