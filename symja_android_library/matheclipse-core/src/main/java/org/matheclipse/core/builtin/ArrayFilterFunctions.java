package org.matheclipse.core.builtin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.external.fastutil.ints.IntArrayList;

/**
 * Functions on the elements of rectangular arrays of any rank: <code>ArrayFilter</code>,
 * <code>ArrayResample</code>, <code>ArrayComponents</code>, <code>MaxDetect</code> and
 * <code>MinDetect</code>, and <code>SubsetCount</code>.
 */
public final class ArrayFilterFunctions {

  private static class Initializer {

    private static void init() {
      S.ArrayFilter.setEvaluator(new ArrayFilter());
      S.ArrayResample.setEvaluator(new ArrayResample());
      S.ArrayComponents.setEvaluator(new ArrayComponents());
      S.MaxDetect.setEvaluator(new ExtremaDetect(true));
      S.MinDetect.setEvaluator(new ExtremaDetect(false));
      S.SubsetCount.setEvaluator(new SubsetCount());
    }
  }

  /**
   * A rectangular array as its dimensions and its elements in row-major order.
   */
  private static final class FlatArray {
    final int[] dimensions;
    final IExpr[] elements;
    /** <code>strides[k]</code> is the distance of two neighbors along the axis <code>k</code>. */
    final int[] strides;

    FlatArray(int[] dimensions, IExpr[] elements) {
      this.dimensions = dimensions;
      this.elements = elements;
      this.strides = new int[dimensions.length];
      int stride = 1;
      for (int k = dimensions.length - 1; k >= 0; k--) {
        strides[k] = stride;
        stride *= dimensions[k];
      }
    }

    /**
     * The array <code>expr</code> down to the depth <code>rank</code>, or <code>null</code> if it
     * isn't a non-empty rectangular array of that depth.
     */
    static FlatArray of(IExpr expr, int rank) {
      if (!expr.isList()) {
        return null;
      }
      IntArrayList dims = LinearAlgebraUtil.dimensions((IAST) expr);
      if (dims.size() < rank || rank < 1) {
        return null;
      }
      int[] dimensions = new int[rank];
      long count = 1;
      for (int k = 0; k < rank; k++) {
        dimensions[k] = dims.getInt(k);
        count *= dimensions[k];
      }
      if (count == 0 || count > Integer.MAX_VALUE) {
        return null;
      }
      IExpr[] elements = new IExpr[(int) count];
      int[] index = new int[] {0};
      collect(expr, rank, elements, index);
      return new FlatArray(dimensions, elements);
    }

    private static void collect(IExpr expr, int depth, IExpr[] elements, int[] index) {
      if (depth == 0) {
        elements[index[0]++] = expr;
        return;
      }
      for (IExpr element : (IAST) expr) {
        collect(element, depth - 1, elements, index);
      }
    }

    int rank() {
      return dimensions.length;
    }

    /** The nested lists with the given row-major elements and dimensions. */
    static IAST build(IExpr[] elements, int[] dimensions) {
      return build(elements, dimensions, 0, 0);
    }

    private static IAST build(IExpr[] elements, int[] dimensions, int axis, int offset) {
      int stride = 1;
      for (int k = axis + 1; k < dimensions.length; k++) {
        stride *= dimensions[k];
      }
      IASTAppendable list = F.ListAlloc(dimensions[axis]);
      for (int i = 0; i < dimensions[axis]; i++) {
        if (axis == dimensions.length - 1) {
          list.append(elements[offset + i]);
        } else {
          list.append(build(elements, dimensions, axis + 1, offset + i * stride));
        }
      }
      return list;
    }
  }

  /**
   * <code>ArrayFilter(f, array, r)</code> - apply <code>f</code> to the block of radius
   * <code>r</code> around every element of <code>array</code>. The block has the rank of the array;
   * at the edges the edge elements are repeated. <code>r</code> may be a list of radii, one per
   * axis of the array.
   */
  private static final class ArrayFilter extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr function = ast.arg1();
      int[] radii;
      int rank;
      if (ast.arg3().isList()) {
        IAST list = (IAST) ast.arg3();
        rank = list.argSize();
        radii = new int[rank];
        for (int k = 0; k < rank; k++) {
          radii[k] = list.get(k + 1).toIntDefault();
          if (radii[k] < 0) {
            return F.NIL;
          }
        }
      } else {
        int r = ast.arg3().toIntDefault();
        if (r < 0 || !ast.arg2().isList()) {
          return F.NIL;
        }
        rank = LinearAlgebraUtil.dimensions((IAST) ast.arg2()).size();
        radii = new int[rank];
        java.util.Arrays.fill(radii, r);
      }
      FlatArray array = FlatArray.of(ast.arg2(), rank);
      if (array == null) {
        return F.NIL;
      }
      int[] blockDimensions = new int[rank];
      int blockSize = 1;
      for (int k = 0; k < rank; k++) {
        blockDimensions[k] = 2 * radii[k] + 1;
        blockSize *= blockDimensions[k];
      }
      final IExpr[] elements = array.elements;
      final int[] dimensions = array.dimensions;
      IExpr[] result = new IExpr[elements.length];
      int[] center = new int[rank];
      int[] offset = new int[rank];
      IExpr[] block = new IExpr[blockSize];
      for (int position = 0; position < elements.length; position++) {
        engine.checkTimeLimit();
        unravel(position, array.strides, center);
        // the block in row-major order, with the indices clamped to the array
        for (int b = 0; b < blockSize; b++) {
          unravelInBox(b, blockDimensions, offset);
          int source = 0;
          for (int k = 0; k < rank; k++) {
            int index = center[k] + offset[k] - radii[k];
            index = Math.max(0, Math.min(dimensions[k] - 1, index));
            source += index * array.strides[k];
          }
          block[b] = elements[source];
        }
        result[position] =
            engine.evaluate(F.unaryAST1(function, FlatArray.build(block, blockDimensions)));
      }
      return FlatArray.build(result, dimensions);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }

  /**
   * Write the multi-index of the row-major position into <code>index</code>, for an array with the
   * given strides.
   */
  private static void unravel(int position, int[] strides, int[] index) {
    for (int k = 0; k < strides.length; k++) {
      index[k] = position / strides[k];
      position %= strides[k];
    }
  }

  /**
   * Write the multi-index of the row-major position into <code>index</code>, for an array with the
   * given dimensions.
   */
  private static void unravelInBox(int position, int[] dimensions, int[] index) {
    for (int k = dimensions.length - 1; k >= 0; k--) {
      index[k] = position % dimensions[k];
      position /= dimensions[k];
    }
  }

  /**
   * <code>ArrayResample(array, n)</code>, <code>ArrayResample(array, {n1, n2, ...})</code> -
   * resample every axis of <code>array</code> to <code>n</code> (or <code>nk</code>) points by
   * linear interpolation; the first and the last element of each axis are kept.
   */
  private static final class ArrayResample extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        return F.NIL;
      }
      final int rank = LinearAlgebraUtil.dimensions((IAST) ast.arg1()).size();
      int[] counts = new int[rank];
      if (ast.arg2().isList()) {
        IAST list = (IAST) ast.arg2();
        for (int k = 1; k < list.size(); k++) {
          if (list.get(k).toIntDefault() < 1) {
            // Invalid dimension specification `1`.
            return Errors.printMessage(S.ArrayResample, "nodim", F.list(ast.arg2()), engine);
          }
        }
        if (list.argSize() != rank) {
          return F.NIL;
        }
        for (int k = 0; k < rank; k++) {
          counts[k] = list.get(k + 1).toIntDefault();
        }
      } else {
        int n = ast.arg2().toIntDefault();
        if (n < 1) {
          // Invalid dimension specification `1`.
          return Errors.printMessage(S.ArrayResample, "nodim", F.list(ast.arg2()), engine);
        }
        java.util.Arrays.fill(counts, n);
      }
      return resample(ast.arg1(), counts, 0, engine);
    }

    private static IExpr resample(IExpr array, int[] counts, int axis, EvalEngine engine) {
      if (axis == counts.length) {
        return array;
      }
      IAST list = (IAST) array;
      final int length = list.argSize();
      final int count = counts[axis];
      IASTAppendable result = F.ListAlloc(count);
      for (int k = 0; k < count; k++) {
        engine.checkTimeLimit();
        IExpr value;
        if (count == 1 || length == 1) {
          value = list.arg1();
        } else {
          // the position k*(length-1)/(count-1) on the axis, counted from 0
          long numerator = (long) k * (length - 1);
          int index = (int) (numerator / (count - 1));
          long remainder = numerator % (count - 1);
          if (remainder == 0) {
            value = list.get(index + 1);
          } else {
            IExpr left = list.get(index + 1);
            IExpr right = list.get(index + 2);
            value = engine.evaluate(
                F.Plus(left, F.Times(F.QQ(remainder, count - 1), F.Subtract(right, left))));
          }
        }
        result.append(resample(value, counts, axis + 1, engine));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>ArrayComponents(array)</code>, <code>ArrayComponents(array, level)</code>,
   * <code>ArrayComponents(array, level, rules)</code> - replace the elements at the given level by
   * the numbers <code>1, 2, 3, ...</code> in the order of their first occurrence, equal elements
   * by the same number. <code>0</code> stays <code>0</code>; the rules give some elements a fixed
   * label, and their integer labels are not used for other elements.
   */
  private static final class ArrayComponents extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg1().isList()) {
        return F.NIL;
      }
      int level = Integer.MAX_VALUE;
      if (ast.argSize() >= 2) {
        if (!ast.arg2().isInfinity()) {
          level = ast.arg2().toIntDefault();
          if (level < 1) {
            return F.NIL;
          }
        }
      }
      Map<IExpr, IExpr> labels = new HashMap<IExpr, IExpr>();
      labels.put(F.C0, F.C0);
      if (ast.argSize() == 3) {
        IExpr rules = ast.arg3();
        IAST ruleList = rules.isList() ? (IAST) rules : F.list(rules);
        for (IExpr rule : ruleList) {
          if (!rule.isRuleAST()) {
            return F.NIL;
          }
          labels.put(rule.first(), rule.second());
        }
      }
      Set<Integer> used = new HashSet<Integer>();
      for (IExpr label : labels.values()) {
        int n = label.toIntDefault();
        if (n != Integer.MIN_VALUE) {
          used.add(n);
        }
      }
      int[] next = new int[] {1};
      return label(ast.arg1(), 0, level, labels, used, next, engine);
    }

    private static IExpr label(IExpr expr, int depth, int level, Map<IExpr, IExpr> labels,
        Set<Integer> used, int[] next, EvalEngine engine) {
      if (depth < level && expr.isList()) {
        IAST list = (IAST) expr;
        IASTAppendable result = F.ListAlloc(list.argSize());
        for (IExpr element : list) {
          result.append(label(element, depth + 1, level, labels, used, next, engine));
        }
        return result;
      }
      IExpr label = labels.get(expr);
      if (label == null) {
        engine.checkTimeLimit();
        while (used.contains(next[0])) {
          next[0]++;
        }
        label = F.ZZ(next[0]);
        used.add(next[0]);
        labels.put(expr, label);
      }
      return label;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * <code>MaxDetect(array)</code>, <code>MinDetect(array)</code> - an array of the same shape with
   * <code>1</code> on the regional maxima (minima) of the real numbers in <code>array</code> and
   * <code>0</code> elsewhere. A regional maximum is a connected set of equal elements - neighbors
   * include the diagonal ones - which has no larger neighbor.
   */
  private static final class ExtremaDetect extends AbstractFunctionEvaluator {

    private final boolean maximum;

    ExtremaDetect(boolean maximum) {
      this.maximum = maximum;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.isAST1() || !ast.arg1().isList()) {
        return F.NIL;
      }
      final int rank = LinearAlgebraUtil.dimensions((IAST) ast.arg1()).size();
      FlatArray array = FlatArray.of(ast.arg1(), rank);
      if (array == null) {
        return F.NIL;
      }
      final int size = array.elements.length;
      double[] values = new double[size];
      for (int i = 0; i < size; i++) {
        IExpr element = array.elements[i];
        if (!element.isRealResult()) {
          return F.NIL;
        }
        values[i] = element.evalf();
      }
      int[][] neighborOffsets = neighborOffsets(rank);
      int[] zone = new int[size];
      java.util.Arrays.fill(zone, -1);
      IExpr[] mask = new IExpr[size];
      java.util.Arrays.fill(mask, F.C0);
      int[] queue = new int[size];
      int[] index = new int[rank];
      int zoneCount = 0;
      for (int start = 0; start < size; start++) {
        if (zone[start] >= 0) {
          continue;
        }
        engine.checkTimeLimit();
        final int id = zoneCount++;
        final double value = values[start];
        // the connected set of elements equal to values[start]
        int head = 0;
        int tail = 0;
        queue[tail++] = start;
        zone[start] = id;
        boolean extremum = true;
        while (head < tail) {
          int cell = queue[head++];
          unravel(cell, array.strides, index);
          for (int[] offset : neighborOffsets) {
            int neighbor = neighbor(index, offset, array);
            if (neighbor < 0) {
              continue;
            }
            double neighborValue = values[neighbor];
            if (neighborValue == value) {
              if (zone[neighbor] < 0) {
                zone[neighbor] = id;
                queue[tail++] = neighbor;
              }
            } else if (maximum ? neighborValue > value : neighborValue < value) {
              extremum = false;
            }
          }
        }
        if (extremum) {
          for (int i = 0; i < tail; i++) {
            mask[queue[i]] = F.C1;
          }
        }
      }
      return FlatArray.build(mask, array.dimensions);
    }

    /** The position of the neighbor at <code>index + offset</code>, or -1 outside the array. */
    private static int neighbor(int[] index, int[] offset, FlatArray array) {
      int position = 0;
      for (int k = 0; k < index.length; k++) {
        int i = index[k] + offset[k];
        if (i < 0 || i >= array.dimensions[k]) {
          return -1;
        }
        position += i * array.strides[k];
      }
      return position;
    }

    /** All offsets in <code>{-1, 0, 1}^rank</code> except the zero offset. */
    private static int[][] neighborOffsets(int rank) {
      int count = 1;
      for (int k = 0; k < rank; k++) {
        count *= 3;
      }
      int[][] offsets = new int[count - 1][];
      int n = 0;
      for (int code = 0; code < count; code++) {
        int[] offset = new int[rank];
        int c = code;
        boolean zero = true;
        for (int k = 0; k < rank; k++) {
          offset[k] = c % 3 - 1;
          c /= 3;
          if (offset[k] != 0) {
            zero = false;
          }
        }
        if (!zero) {
          offsets[n++] = offset;
        }
      }
      return offsets;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>SubsetCount(list, pattern)</code> - the number of the subsets <code>SubsetCases</code>
   * finds.
   */
  private static final class SubsetCount extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr cases = engine.evaluate(ast.setAtCopy(0, S.SubsetCases));
      if (cases.isList()) {
        return F.ZZ(cases.argSize());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ArrayFilterFunctions() {}
}
