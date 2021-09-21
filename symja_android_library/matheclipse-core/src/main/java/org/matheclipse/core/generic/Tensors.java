package org.matheclipse.core.generic;

import java.util.function.Supplier;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IDimensionFunction;
import org.matheclipse.core.interfaces.IExpr;

/** Provide builder methods for creating dense tensor arrays. */
public class Tensors {

  private static class ArraySupplierBuilder {
    final Supplier<IExpr> supplier;
    final int[] dimension;

    private ArraySupplierBuilder(Supplier<IExpr> supplier, int[] dimension) {
      this.supplier = supplier;
      this.dimension = dimension;
    }

    private void createArrayRecursive(IASTMutable list, int position) {
      if (dimension.length - 1 == position) {
        int size = dimension[position];
        for (int i = 1; i <= size; i++) {
          list.set(i, supplier.get());
        }
        return;
      }
      int size1 = dimension[position];
      int size2 = dimension[position + 1];
      for (int i = 1; i <= size1; i++) {
        IASTMutable currentList = F.astMutable(S.List, size2);
        list.set(i, currentList);
        createArrayRecursive(currentList, position + 1);
      }
    }

    private IASTMutable createArray(IExpr head) {
      IASTMutable list = F.astMutable(head, dimension[0]);
      createArrayRecursive(list, 0);
      return list;
    }
  }

  private static class ArrayIndexBuilder {
    final IDimensionFunction<IExpr> function;
    final int[] dimension;

    public ArrayIndexBuilder(IDimensionFunction<IExpr> function, int[] dimension) {
      this.function = function;
      this.dimension = dimension;
    }

    private void createArrayRecursive(IASTMutable list, int position, int[] index) {
      final int size = dimension[position];
      if (dimension.length - 1 == position) {
        for (int i = 1; i <= size; i++) {
          index[position] = i;
          list.set(i, function.apply(index));
        }
        return;
      }
      final int size2 = dimension[position + 1];
      for (int i = 1; i <= size; i++) {
        index[position] = i;
        IASTMutable currentList = F.astMutable(S.List, size2);
        list.set(i, currentList);
        createArrayRecursive(currentList, position + 1, index);
      }
    }

    private IASTMutable createArray(IExpr head) {
      IASTMutable list = F.astMutable(head, dimension[0]);
      int[] index = startIndex();
      createArrayRecursive(list, 0, index);
      return list;
    }

    /**
     * Build the start index by creating an array completely filled with position <code>1</code>.
     *
     * @return
     */
    private int[] startIndex() {
      int[] index = new int[dimension.length];
      for (int i = 0; i < index.length; i++) {
        index[i] = 1;
      }
      return index;
    }
  }

  /**
   * Create a dense tensor.
   *
   * @param supplier
   * @param dimension the dimension of the tensor
   * @return
   */
  public static IASTMutable build(Supplier<IExpr> supplier, int[] dimension) {
    ArraySupplierBuilder builder = new ArraySupplierBuilder(supplier, dimension);
    return builder.createArray(S.List);
  }

  /**
   * Create a dense tensor.
   *
   * @param function
   * @param dimension the dimension of the tensor
   * @return
   */
  public static IASTMutable build(IDimensionFunction<IExpr> function, int[] dimension) {
    ArrayIndexBuilder builder = new ArrayIndexBuilder(function, dimension);
    return builder.createArray(S.List);
  }
}
