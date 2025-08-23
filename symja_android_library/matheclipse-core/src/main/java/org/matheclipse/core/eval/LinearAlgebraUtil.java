package org.matheclipse.core.eval;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumericArray;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.ITensorAccess;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class LinearAlgebraUtil {

  public final static class DimensionsData {
    boolean isNonRectangular = false;
    final EvalEngine engine;
    final IntArrayList dimensions;
  
    /**
     * Determine the <a href=
     * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Dimensions.md">Dimensions</a>
     * recursively.
     * 
     * @param ast
     * @param header
     * @param maxLevel
     * @param throwIllegalArgumentException
     */
    public DimensionsData(IAST ast, IExpr header, int maxLevel,
        boolean throwIllegalArgumentException) {
      this.engine = EvalEngine.get();
      this.dimensions = dimensionsRecursive(ast, header, maxLevel, throwIllegalArgumentException);
    }
  
    /**
     * Determine the <a href=
     * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Dimensions.md">Dimensions</a>
     * recursively.
     * 
     * @param ast
     * @param header the header, which all sub-expressions of the detected dimension must contain
     * @param maxLevel the maximum level (depth) of analyzing for the dimension
     * @param throwIllegalArgumentException
     * @return a list of the dimensions or a list of size <code>0</code> if no dimensions are found
     * @throws IllegalArgumentException
     */
    private IntArrayList dimensionsRecursive(IAST ast, IExpr header, int maxLevel,
        boolean throwIllegalArgumentException) throws IllegalArgumentException {
      final int size = ast.size();
      if (header.equals(ast.head())) {
        IntArrayList subDim = null;
        IntArrayList sub = new IntArrayList();
        for (int i = 1; i < size; i++) {
          IExpr element = ast.get(i);
          if (element.isAST() && maxLevel > 0) {
            sub = dimensionsRecursive((IAST) element, header, maxLevel - 1,
                throwIllegalArgumentException);
          } else {
            if (element.isSparseArray()) {
              sub = new IntArrayList(((ISparseArray) element).getDimension());
            } else if (element.isNumericArray()) {
              sub = new IntArrayList(((INumericArray) element).getDimension());
            } else {
              sub = new IntArrayList();
            }
          }
          if (subDim == null) {
            subDim = sub;
          } else {
            if (!subDim.equals(sub)) {
              if (subDim.size() != sub.size()) {
                isNonRectangular = true;
                if (throwIllegalArgumentException) {
                  throw new IllegalArgumentException();
                } else {
                  // print message: Nonrectangular tensor encountered
                  Errors.printMessage(ast.topHead(), "rect", F.CEmptyList, engine);
                }
              }
              int minSize = subDim.size() > sub.size() ? sub.size() : subDim.size();
              int j = 0;
              while (j < minSize) {
                if (subDim.getInt(j) != sub.getInt(j)) {
                  isNonRectangular = true;
                  if (throwIllegalArgumentException) {
                    throw new IllegalArgumentException();
                  } else {
                    // print message: Nonrectangular tensor encountered
                    Errors.printMessage(ast.topHead(), "rect", F.list(ast), engine);
                  }
                  break;
                }
                j++;
              }
              sub = new IntArrayList();
              for (int k = 0; k < j; k++) {
                sub.add(subDim.getInt(k));
              }
              subDim = sub;
            }
          }
        }
        if (subDim == null) {
          subDim = new IntArrayList();
        }
        subDim.add(0, size - 1);
        return subDim;
      }
      return new IntArrayList();
    }
  
    public boolean isNonRectangular() {
      return isNonRectangular;
    }
  
    public IntArrayList getDimensions() {
      return dimensions;
    }
  }

  /**
   * Validate <code>intOrListOf2Ints</code> to get the dimension parameters.
   * 
   * @param intOrListOf2Ints
   * @param engine
   * @return <code>null</code> if the dimension couldn't be determined
   */
  public static int[] dimensionMatrix(final IExpr intOrListOf2Ints, EvalEngine engine) {
    int[] dimension = new int[2];
    dimension[0] = intOrListOf2Ints.toIntDefault();
    if (dimension[0] < 0) {
      if (intOrListOf2Ints.isNumber()) {
        return null;
      }
      if (intOrListOf2Ints.isList2() && intOrListOf2Ints.first().isNumber()
          && intOrListOf2Ints.second().isNumber()) {
        dimension[0] = intOrListOf2Ints.first().toIntDefault();
        if (dimension[0] < 0) {
          return null;
        }
        dimension[1] = intOrListOf2Ints.second().toIntDefault();
        if (dimension[1] < 0) {
          return null;
        }
      } else {
        return null;
      }
    } else {
      dimension[1] = dimension[0];
    }
    return dimension;
  }

  /**
   * Return the <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Dimensions.md">Dimensions</a>
   * of an {@link IAST}.
   * 
   * @param ast
   * @return a list of size <code>0</code> if no dimensions are found
   */
  public static IntArrayList dimensions(IAST ast) {
    LinearAlgebraUtil.DimensionsData dimensionsData = new LinearAlgebraUtil.DimensionsData(ast, ast.head(), Integer.MAX_VALUE, false);
    return dimensionsData.getDimensions();
  }

  /**
   * Return the <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Dimensions.md">Dimensions</a>
   * of an <code>expr</code>. All (sub-)expressions in the dimension must have the same
   * <code>header</code>.
   * 
   * @param expr
   * @param header the header, which all sub-expressions of the detected dimension must contain
   * @param maxLevel the maximum level (depth) of analyzing for the dimension
   * @param throwIllegalArgumentException
   * @return a list of size <code>0</code> if no dimensions are found
   */
  public static IntArrayList dimensions(IExpr expr, IExpr header, int maxLevel,
      boolean throwIllegalArgumentException) {
    if (expr.isAST()) {
      org.matheclipse.core.eval.LinearAlgebraUtil.DimensionsData dimensionsData =
          new org.matheclipse.core.eval.LinearAlgebraUtil.DimensionsData((IAST) expr, header, maxLevel, throwIllegalArgumentException);
      return dimensionsData.getDimensions();
    }
    if (expr.isSparseArray()) {
      int[] dims = ((ISparseArray) expr).getDimension();
  
      if (dims.length > maxLevel) {
        IntArrayList list = new IntArrayList(maxLevel);
        if (throwIllegalArgumentException) {
          throw new IllegalArgumentException();
        }
        for (int i = 0; i < maxLevel; i++) {
          list.add(dims[i]);
        }
        return list;
      }
      IntArrayList list = new IntArrayList(dims.length);
      for (int i = 0; i < dims.length; i++) {
        list.add(dims[i]);
      }
      return list;
    }
  
    return new IntArrayList();
  }

  /**
   * Return the <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Dimensions.md">Dimensions</a>
   * of an {@link IAST}. All (sub-)expressions in the dimension must have the same
   * <code>header</code>.
   * 
   * @param ast
   * @param header the header, which all sub-expressions of the detected dimension must contain
   * @return a list of size <code>0</code> if no dimensions are found
   */
  public static IntArrayList dimensions(ITensorAccess ast, IExpr header) {
    return LinearAlgebraUtil.dimensions(ast, header, Integer.MAX_VALUE);
  }

  /**
   * Return the <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Dimensions.md">Dimensions</a>
   * of an {@link IAST}. All (sub-)expressions in the dimension must have the same
   * <code>header</code>.
   * 
   * @param ast
   * @param header the header, which all sub-expressions of the detected dimension must contain
   * @param maxLevel maxLevel the maximum level (depth) of analyzing for the dimension
   * @return a list of size <code>0</code> if no dimensions are found
   */
  public static IntArrayList dimensions(ITensorAccess ast, IExpr header, int maxLevel) {
    if (ast.isSparseArray()) {
      int[] dims = ((ISparseArray) ast).getDimension();
      if (dims.length <= maxLevel) {
        maxLevel = dims.length;
      }
      final IntArrayList list = new IntArrayList(maxLevel);
      list.addElements(0, dims, 0, maxLevel);
      return list;
    }
    org.matheclipse.core.eval.LinearAlgebraUtil.DimensionsData dimensionsData = new org.matheclipse.core.eval.LinearAlgebraUtil.DimensionsData((IAST) ast, header, maxLevel, false);
    return dimensionsData.getDimensions();
  }

  private LinearAlgebraUtil() {
    // private constructor to avoid instantiation
  }
}
