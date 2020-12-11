package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.hipparchus.exception.MathIllegalArgumentException;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractNonOrderlessArgMultiple;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class TensorFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.ArrayReshape.setEvaluator(new ArrayReshape());
      S.Ordering.setEvaluator(new Ordering());
      S.ListConvolve.setEvaluator(new ListConvolve());
      S.ListCorrelate.setEvaluator(new ListCorrelate());
      S.TensorDimensions.setEvaluator(new TensorDimensions());
      S.TensorProduct.setEvaluator(new TensorProduct());
      S.TensorRank.setEvaluator(new TensorRank());
      S.TensorSymmetry.setEvaluator(new TensorSymmetry());
    }
  }

  /**
   *
   *
   * <pre>
   * ArrayReshape(list - of - values, list - of - dimension)
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the <code>list-of-values</code> elements reshaped as nested list with dimensions
   * according to the <code>list-of-dimension</code>.
   *
   * </blockquote>
   *
   * <pre>
   * ArrayReshape(list - of - values, list - of - dimension, expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>Use <code>expr</code> to fill up elements, if there are too little elements in the <code>
   * list-of-values</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>A list of non-negative integers is expected at position 2. The optional third argument
   * <code>x</code> is used to fill up the structure:
   *
   * <pre>
   * &gt;&gt; ArrayReshape({a, b, c, d, e, f}, {2, 3, 3, 2}, x)
   * {{{{a,b},{c,d},{e,f}},{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}}},{{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}},{{x,x},{x,x},{x,x}}}}
   * </pre>
   *
   * <p>Ignore unnecessary elements
   *
   * <pre>
   * &gt;&gt; ArrayReshape(Range(1000), {3, 2, 2})
   * {{{1,2},{3,4}},{{5,6},{7,8}},{{9,10},{11,12}}}
   * </pre>
   */
  private static final class ArrayReshape extends AbstractEvaluator {
    static class Reshaper {
      final IAST list;
      final int[] dimension;
      final IExpr padding;
      int listPosition;

      public Reshaper(IAST list, int[] dimension, IExpr padding) {
        this.list = list;
        this.dimension = dimension;
        this.padding = padding;
        listPosition = 1;
      }

      /**
       * @param dimensionIndex the dimension[dimensionIndex] which should be used on this recursion
       *     level.
       * @return
       */
      public IAST recursiveCall(int dimensionIndex) {
        int dim = dimension[dimensionIndex];
        if (dimension.length == dimensionIndex + 1) {
          IASTAppendable result = F.ListAlloc(dim);
          for (int i = 0; i < dim; i++) {
            if (list.size() <= listPosition) {
              result.append(padding);
            } else {
              result.append(list.get(listPosition++));
            }
          }
          return result;
        } else {
          // recursive call
          IASTAppendable result = F.ListAlloc(dim);
          for (int i = 0; i < dim; i++) {
            IAST subList = recursiveCall(dimensionIndex + 1);
            result.append(subList);
          }
          return result;
        }
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList() && ast.arg2().isList()) {
        IAST list = (IAST) ast.arg1();
        IAST dims = (IAST) ast.arg2();
        if (dims.size() == 1) {
          if (list.isEmpty()) {
            return F.C0;
          }
          if (list.size() > 1) {
            return list.arg1();
          }
        }
        int[] dimension = Validate.checkListOfInts(ast, dims, 1, Integer.MAX_VALUE, engine);
        if (dimension == null) {
          return F.NIL;
        }
        IExpr padding = F.C0;
        if (ast.size() == 4) {
          padding = ast.arg3();
        }
        Reshaper reshaper = new Reshaper(list, dimension, padding);
        return reshaper.recursiveCall(0);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IOFunctions.ARGS_2_3;
    }
  }

  /**
   *
   *
   * <pre>
   * ListConvolve(kernel - list, tensor - list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>create the convolution of the <code>kernel-list</code> with <code>tensor-list</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ListConvolve({x, y}, {a, b, c, d, e, f})
   * {b*x+a*y,c*x+b*y,d*x+c*y,e*x+d*y,f*x+e*y}
   * </pre>
   */
  private static class ListConvolve extends AbstractEvaluator {
    /**
     * See: <a href=
     * "https://github.com/idsc-frazzoli/tensor/blob/master/src/main/java/ch/ethz/idsc/tensor/alg/ListConvolve.java">tensor/alg/ListConvolve.java</a>
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        if (ast.arg1().isAST() && ast.arg2().isAST()) {
          IAST kernel = (IAST) ast.arg1();
          IAST tensor = (IAST) ast.arg2();

          int kernelSize = kernel.size();
          int tensorSize = tensor.size();
          if (kernelSize <= tensorSize) {
            return ListCorrelate.listCorrelate(
                ListFunctions.reverse(kernel), kernelSize, tensor, tensorSize);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IOFunctions.ARGS_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * ListCorrelate(kernel - list, tensor - list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>create the correlation of the <code>kernel-list</code> with <code>tensor-list</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ListCorrelate({x, y}, {a, b, c, d, e, f})
   * {a*x+b*y,b*x+c*y,c*x+d*y,d*x+e*y,e*x+f*y}
   * </pre>
   */
  private static class ListCorrelate extends AbstractEvaluator {
    /**
     * See: <a href=
     * "https://github.com/idsc-frazzoli/tensor/blob/master/src/main/java/ch/ethz/idsc/tensor/alg/ListCorrelate.java">tensor/alg/ListCorrelate.java</a>
     *
     * @return correlation of kernel with tensor
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        if (ast.arg1().isAST() && ast.arg2().isAST()) {
          IAST kernel = (IAST) ast.arg1();
          IAST tensor = (IAST) ast.arg2();
          int kernelSize = kernel.size();
          int tensorSize = tensor.size();
          if (kernelSize <= tensorSize) {
            return listCorrelate(kernel, kernelSize, tensor, tensorSize);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IOFunctions.ARGS_2_2;
    }

    public static IExpr listCorrelate(IAST kernel, int kernelSize, IAST tensor, int tensorSize) {
      ISymbol fFunction = F.Plus;
      ISymbol gFunction = F.Times;
      int diff = tensorSize - kernelSize;
      IASTAppendable resultList = F.ListAlloc(tensorSize - 1);
      final int[] fi = new int[1];
      for (int i = 0; i <= diff; i++) {
        IASTAppendable plus = F.ast(fFunction, kernelSize, false);
        fi[0] = i;
        plus.appendArgs(
            kernelSize, k -> F.binaryAST2(gFunction, kernel.get(k), tensor.get(k + fi[0])));
        // for (int k = 1; k < kernelSize; k++) {
        // plus.append(F.binaryAST2(gFunction, kernel.get(k), tensor.get(k + i)));
        // }
        resultList.append(plus);
      }
      return resultList;
    }
  }

  /**
   *
   *
   * <pre>
   * Ordering(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>calculate the permutation list of the elements in the sorted <code>list</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Ordering(list, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>calculate the first <code>n</code> indexes of the permutation list of the elements in the
   * sorted <code>list</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Ordering(list, -n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>calculate the last <code>n</code> indexes of the permutation list of the elements in the
   * sorted <code>list</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Ordering(list, n, head)
   * </pre>
   *
   * <blockquote>
   *
   * <p>calculate the first <code>n</code> indexes of the permutation list of the elements in the
   * sorted <code>list</code> using comparator operation <code>head</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Ordering({1,3,4,2,5,9,6})
   * {1,4,2,3,5,7,6}
   *
   * &gt;&gt; Ordering({1,3,4,2,5,9,6}, All, Greater)
   * {6,7,5,3,2,4,1}
   * </pre>
   */
  private static class Ordering extends AbstractEvaluator {

    /**
     * See <a href="https://stackoverflow.com/a/4859279/24819">Get the indices of an array after
     * sorting?</a>
     */
    private static class ArrayIndexComparator implements Comparator<Integer> {
      protected final IAST ast;

      public ArrayIndexComparator(IAST ast) {
        this.ast = ast;
      }

      public Integer[] createIndexArray() {
        int size = ast.size();
        Integer[] indexes = new Integer[size - 1];
        for (int i = 1; i < size; i++) {
          indexes[i - 1] = i;
        }
        return indexes;
      }

      @Override
      public int compare(Integer index1, Integer index2) {
        return ast.get(index1).compareTo(ast.get(index2));
      }
    }

    private static class PredicateComparator extends ArrayIndexComparator {
      final Comparator<IExpr> comparator;

      public PredicateComparator(IAST ast, Comparator<IExpr> comparator) {
        super(ast);
        this.comparator = comparator;
      }

      @Override
      public int compare(Integer index1, Integer index2) {
        return comparator.compare(ast.get(index1), ast.get(index2));
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST()) {
        IAST list = (IAST) ast.arg1();
        ArrayIndexComparator comparator;
        if (ast.size() >= 4) {
          // use the 3rd argument as a head for the comparator operation:
          comparator = new PredicateComparator(list, new Predicates.IsBinaryFalse(ast.arg3()));
        } else {
          // use the default IExpr#compareTo() method
          comparator = new ArrayIndexComparator(list);
        }
        Integer[] indexes = comparator.createIndexArray();
        Arrays.sort(indexes, comparator);
        int n = indexes.length;
        if (ast.size() >= 3) {
          IExpr arg2 = ast.arg2();
          if (arg2.equals(F.All)) {
          } else if (arg2.isReal()) {
            ISignedNumber sn = (ISignedNumber) arg2;
            n = sn.toIntDefault(Integer.MIN_VALUE);
          }
        }
        if (n == Integer.MIN_VALUE) {
          return F.NIL;
        }
        return F.tensorList(n, indexes);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IOFunctions.ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class TensorDimensions extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList()) {
        // same as Dimensions for List structures
        return F.Dimensions(ast.arg1());
      }
      return F.NIL;
    }

    public int[] expectedArgSize(IAST ast) {
      return IOFunctions.ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class TensorSymmetry extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST()) {
        IAST tensor = (IAST) ast.arg1();
        ArrayList<Integer> dims =
            LinearAlgebra.dimensions(tensor, tensor.head(), Integer.MAX_VALUE);
        if (dims.size() > 0) {
          if (dims.size() == 2 && dims.get(0).equals(dims.get(1))) {
            // square matrix
            int rowColumnSize = dims.get(0) + 1;
            if (rowColumnSize == 2) {
              if (tensor.getPart(1, 1).isZero()) {
                return F.ZeroSymmetric(F.List());
              }
              return F.Symmetric(F.List(F.C1, F.C2));
            }
            return tensorSymmetrySquareMatrix(tensor, rowColumnSize, engine);
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return IOFunctions.ARGS_1_2;
    }

    /**
     * @param squareMatrix
     * @param rowColumnSize the row and column size of the square matrix
     * @param engine the evaluation engine
     * @return
     */
    private static IExpr tensorSymmetrySquareMatrix(
        IAST squareMatrix, int rowColumnSize, EvalEngine engine) {
      IExpr temp = isZeroSymmetricSquareMatrix(squareMatrix, rowColumnSize);
      if (temp.isPresent()) {
        return temp;
      }
      boolean isAntiSymmetric = true;
      boolean isSymmetric = true;
      for (int i = 1; i < rowColumnSize; i++) {
        if (isSymmetric) {
          for (int j = i + 1; j < rowColumnSize; j++) {
            if (!squareMatrix.getPart(i, j).equals(squareMatrix.getPart(j, i))) {
              isSymmetric = false;
              break;
            }
          }
        }
        if (isSymmetric) {
          isAntiSymmetric = false;
        } else if (isAntiSymmetric) {
          for (int j = i + 1; j < rowColumnSize; j++) {
            temp = squareMatrix.getPart(j, i).negate();
            if (!squareMatrix.getPart(i, j).equals(temp)) {
              isAntiSymmetric = false;
              break;
            }
          }
        }

        if (!isAntiSymmetric && !isSymmetric) {
          return F.NIL;
        }
      }
      if (isSymmetric) {
        return F.Symmetric(F.List(F.C1, F.C2));
      }
      if (isAntiSymmetric) {
        return F.AntiSymmetric(F.List(F.C1, F.C2));
      }
      return F.List();
    }

    /**
     * @param squareMatrix
     * @param rowColumnSize the row and column size of the square matrix
     * @return
     */
    private static IExpr isZeroSymmetricSquareMatrix(IAST squareMatrix, int rowColumnSize) {
      boolean isZero = true;
      for (int i = 1; i < rowColumnSize; i++) {
        for (int j = 1; j < rowColumnSize; j++) {
          if (!squareMatrix.getPart(i, j).isZero()) {
            isZero = false;
            break;
          }
        }
        if (!isZero) {
          break;
        }
      }
      if (isZero) {
        return F.ZeroSymmetric(F.List());
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class TensorProduct extends AbstractNonOrderlessArgMultiple {

    public IExpr evaluateAST1(final IAST ast, EvalEngine engine) {
      return ast.arg1();
    }

    @Override
    public IExpr e2ObjArg(IAST ast, final IExpr arg1, final IExpr arg2) {
      if (arg1.isList() && arg2.isList()) {
        // TODO
        // IAST tensor1 = (IAST) o0;
        // IAST tensor2 = (IAST) o0;
        // return tensor1.mapList(tensor2::times, 1);
      }

      return F.NIL;
    }

    private IExpr numericalDot(final IExpr o0, final IExpr o1) throws MathIllegalArgumentException {
      return F.NIL;
    }

    @Override
    public IExpr numericEval(final IAST ast, EvalEngine engine) {
      return evaluate(ast, engine);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.FLAT | ISymbol.ONEIDENTITY);
    }
  }

  private static class TensorRank extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        List<Integer> intList =
            LinearAlgebra.dimensions((IAST) ast.arg1(), list.head(), Integer.MAX_VALUE);
        return F.ZZ(intList.size());
      }
      return F.NIL;
    }

    public int[] expectedArgSize(IAST ast) {
      return IOFunctions.ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  public static void initialize() {
    Initializer.init();
  }

  private TensorFunctions() {}
}
