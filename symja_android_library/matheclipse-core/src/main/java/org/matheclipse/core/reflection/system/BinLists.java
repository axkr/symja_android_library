package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.tensor.qty.IQuantity;

/**
 * Group elements of a list into successive bins. Supports 1D and multi-dimensional nested data.
 * <ul>
 * <li>BinLists(list, dx)</li>
 * <li>BinLists(list, {xmin, xmax})</li>
 * <li>BinLists(list, {xmin, xmax, dx})</li>
 * <li>BinLists(list, {{b1, b2, ...}})</li>
 * <li>BinLists(data, xbins, ybins, ...)</li>
 * </ul>
 */
public class BinLists extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    try {
      IExpr arg1 = ast.arg1();

      // Convert SparseArray to a normal list seamlessly
      if (arg1.isSparseArray()) {
        arg1 = arg1.normal(false);
      }

      if (arg1.isList()) {
        IAST data = (IAST) arg1;
        int[] dims = data.isMatrix();

        if (dims != null && dims.length == 2) {
          // Multivariate data / Multi-dimensional structures
          return evaluateMultivariate(ast, data, dims[0], dims[1], engine);
        } else {
          // 1D data
          if (ast.argSize() == 2) {
            return evaluate1D(ast, data, ast.arg2(), engine);
          } else if (ast.argSize() == 1) {
            return evaluate1D(ast, data, F.C1, engine);
          }
        }
      }
    } catch (ArithmeticException aex) {
      return Errors.printMessage(ast.topHead(), aex, engine);
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  /**
   * Safely extracts the double value from an expression, including handling Quantity objects by
   * isolating their numerical magnitude.
   */
  private static double extractDouble(IExpr val, EvalEngine engine) {
    if (val.isInfinity()) {
      return Double.POSITIVE_INFINITY;
    }
    if (val.isNegativeInfinity()) {
      return Double.NEGATIVE_INFINITY;
    }

    final IExpr nVal;
    if (val.isQuantity()) {
      nVal = engine.evalN(((IQuantity) val).valueSI());
    } else {
      nVal = engine.evalN(val);
    }

    if (nVal instanceof INum) {
      return ((INum) nVal).doubleValue();
    }
    if (nVal.isReal()) {
      return ((IReal) nVal).doubleValue();
    }
    return Double.NaN;
  }

  private static class BinSpec {
    int capacity;
    double xMin = Double.NaN;
    double xMax = Double.NaN;
    double dx = Double.NaN;
    double[] boundaries = null;

    public int getBinIndex(IExpr val, EvalEngine engine) {
      double d = extractDouble(val, engine);
      if (Double.isNaN(d)) {
        return -1;
      }

      if (boundaries != null) {
        for (int i = 0; i < capacity; i++) {
          if (d >= boundaries[i] && d < boundaries[i + 1]) {
            return i;
          }
        }
        // Incorporate inclusive edge for the final interval limit
        if (d == boundaries[capacity]) {
          return capacity - 1;
        }
        return -1;
      } else {
        if (d < xMin || d > xMax) {
          return -1;
        }
        int idx = (int) Math.floor((d - xMin) / dx);
        if (idx >= capacity) {
          // Wolfram Language includes upper boundary mapping to the final bin
          if (d <= xMax && idx == capacity) {
            return capacity - 1;
          }
          return -1;
        }
        if (idx >= 0 && idx < capacity) {
          return idx;
        }
        return -1;
      }
    }
  }

  private static double getColValue(IAST data, int rowIndex, int colIndex, boolean is1D,
      EvalEngine engine) {
    IExpr val;
    if (is1D) {
      val = data.get(rowIndex);
    } else {
      val = ((IAST) data.get(rowIndex)).get(colIndex + 1);
    }
    return extractDouble(val, engine);
  }

  private static BinSpec createSpec(IAST data, int colIndex, IExpr specExpr, boolean is1D,
      EvalEngine engine) {
    BinSpec spec = new BinSpec();
    if (specExpr.isList()) {
      IAST list = (IAST) specExpr;
      if (list.argSize() == 1 && list.arg1().isList()) {
        // Handle interval bin boundaries {{b1, b2, ...}}
        IAST bList = EvalAttributes.copySortLess((IAST) list.arg1());
        if (bList.argSize() < 2)
          return null;

        spec.capacity = bList.argSize() - 1;
        spec.boundaries = new double[bList.argSize()];
        for (int i = 1; i <= bList.argSize(); i++) {
          double val = extractDouble(bList.get(i), engine);
          if (Double.isNaN(val)) {
            return null;
          }
          spec.boundaries[i - 1] = val;
        }
        return spec;
      } else if (list.argSize() == 2 || list.argSize() == 3) {
        // Handle explicit boundaries {xmin, xmax} or {xmin, xmax, dx}
        double dx = 1.0;
        if (list.argSize() == 3) {
          dx = extractDouble(list.arg3(), engine);
          if (Double.isNaN(dx) || dx <= 0) {
            return null;
          }
        }

        double xMin = extractDouble(list.arg1(), engine);
        double xMax = extractDouble(list.arg2(), engine);

        if (Double.isNaN(xMin) || Double.isNaN(xMax) || xMax <= xMin) {
          return null;
        }

        spec.dx = dx;
        spec.xMin = xMin;
        spec.capacity = (int) Math.ceil((xMax - xMin) / dx);
        spec.xMax = xMin + spec.capacity * dx;
        return spec;
      }
    } else {
      // Handle implicit bounds defined only by dx
      double dx = extractDouble(specExpr, engine);
      if (Double.isNaN(dx) || dx <= 0) {
        return null;
      }

      double colMin = Double.POSITIVE_INFINITY;
      double colMax = Double.NEGATIVE_INFINITY;
      for (int i = 1; i <= data.argSize(); i++) {
        double d = getColValue(data, i, colIndex, is1D, engine);
        if (!Double.isNaN(d)) {
          if (d < colMin)
            colMin = d;
          if (d > colMax)
            colMax = d;
        }
      }
      if (Double.isInfinite(colMin)) {
        colMin = 0.0;
        colMax = 0.0;
      }

      spec.dx = dx;

      // Wolfram start criteria: Ceiling[Min[data] - dx, dx]
      spec.xMin = Math.ceil((colMin - dx) / dx) * dx;

      // Wolfram end criteria: Floor[Max[data] + dx, dx]
      spec.xMax = Math.floor((colMax + dx) / dx) * dx;

      spec.capacity = (int) Math.round((spec.xMax - spec.xMin) / dx);
      if (spec.capacity <= 0) {
        spec.capacity = 1;
      }

      // Re-sync xMax to exact capacity
      spec.xMax = spec.xMin + spec.capacity * dx;
      return spec;
    }
    return null;
  }

  private static IExpr evaluate1D(IAST ast, IAST vector, IExpr specExpr, EvalEngine engine) {
    BinSpec spec = createSpec(vector, 0, specExpr, true, engine);
    if (spec == null || spec.capacity <= 0)
      return F.CEmptyList;
    if (spec.capacity > Config.MAX_AST_SIZE)
      ASTElementLimitExceeded.throwIt(spec.capacity);

    IASTAppendable[] flatBins = new IASTAppendable[spec.capacity];
    for (int i = 0; i < spec.capacity; i++) {
      flatBins[i] = F.ListAlloc();
    }

    for (int i = 1; i <= vector.argSize(); i++) {
      IExpr original = vector.get(i);
      // final IExpr nVal;
      // if (original.isQuantity()) {
      // nVal = engine.evalN(((IQuantity) original).SI());
      // } else {
      // nVal = engine.evalN(original);
      // }
      int bIdx = spec.getBinIndex(original, engine);
      if (bIdx >= 0) {
        flatBins[bIdx].append(original);
      }
    }

    IASTAppendable resultList = F.ListAlloc(spec.capacity);
    for (int i = 0; i < spec.capacity; i++) {
      resultList.append(flatBins[i]);
    }
    return resultList;
  }

  private static IExpr evaluateMultivariate(IAST ast, IAST matrix, int rows, int cols,
      EvalEngine engine) {
    int N = cols;
    BinSpec[] specs = new BinSpec[N];

    // Determine configuration applied dynamically over dimensions
    if (ast.argSize() == 1) {
      for (int i = 0; i < N; i++)
        specs[i] = createSpec(matrix, i, F.C1, false, engine);
    } else if (ast.argSize() == 2) {
      for (int i = 0; i < N; i++)
        specs[i] = createSpec(matrix, i, ast.arg2(), false, engine);
    } else if (ast.argSize() == N + 1) {
      for (int i = 0; i < N; i++)
        specs[i] = createSpec(matrix, i, ast.get(i + 2), false, engine);
    } else {
      return F.NIL;
    }

    int totalCapacity = 1;
    for (int i = 0; i < N; i++) {
      if (specs[i] == null || specs[i].capacity <= 0)
        return F.CEmptyList;
      totalCapacity *= specs[i].capacity;
      if (totalCapacity > Config.MAX_AST_SIZE) {
        ASTElementLimitExceeded.throwIt(totalCapacity);
      }
    }

    IASTAppendable[] flatBins = new IASTAppendable[totalCapacity];
    for (int i = 0; i < totalCapacity; i++) {
      flatBins[i] = F.ListAlloc();
    }

    // Assign rows recursively to their multi-dimensional structure block mapped onto a 1D flat
    // layer
    for (int i = 1; i <= rows; i++) {
      IAST row = (IAST) matrix.get(i);
      int flatIndex = 0;
      int multiplier = 1;
      boolean valid = true;
      for (int d = N - 1; d >= 0; d--) {
        int bIdx = specs[d].getBinIndex(row.get(d + 1), engine);
        if (bIdx < 0) {
          valid = false;
          break;
        }
        flatIndex += bIdx * multiplier;
        multiplier *= specs[d].capacity;
      }
      if (valid) {
        flatBins[flatIndex].append(row);
      }
    }

    return buildNestedList(flatBins, specs, 0, 0);
  }

  // Fold flat indices back into proper N-depth arrays
  private static IAST buildNestedList(IASTAppendable[] flatBins, BinSpec[] specs, int dim,
      int flatOffset) {
    if (dim == specs.length - 1) {
      IASTAppendable list = F.ListAlloc(specs[dim].capacity);
      for (int i = 0; i < specs[dim].capacity; i++) {
        list.append(flatBins[flatOffset + i]);
      }
      return list;
    } else {
      IASTAppendable list = F.ListAlloc(specs[dim].capacity);
      int multiplier = 1;
      for (int d = specs.length - 1; d > dim; d--) {
        multiplier *= specs[d].capacity;
      }
      for (int i = 0; i < specs[dim].capacity; i++) {
        list.append(buildNestedList(flatBins, specs, dim + 1, flatOffset + i * multiplier));
      }
      return list;
    }
  }
}
