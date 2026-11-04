package org.matheclipse.core.reflection.system;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The discrete cosine and sine transforms of the types 1 to 4 with orthogonal normalizations: the
 * type 2 transform is inverted by the type 3 transform, the types 1 and 4 by themselves. An array
 * of higher rank is transformed in every dimension.
 */
public class FourierCosineSineTransforms {

  private FourierCosineSineTransforms() {}

  /** <code>FourierDCT(list)</code>, <code>FourierDCT(list, m)</code> - discrete cosine transform. */
  public static final class FourierDCT extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return transform(ast, true, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** <code>FourierDST(list)</code>, <code>FourierDST(list, m)</code> - discrete sine transform. */
  public static final class FourierDST extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return transform(ast, false, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static IExpr transform(IAST ast, boolean cosine, EvalEngine engine) {
    int type = 2;
    if (ast.isAST2()) {
      type = transformType(ast.arg2());
      if (type < 1) {
        return F.NIL;
      }
    }
    int[] dims = ast.arg1().isList() ? dimensions(ast.arg1()) : null;
    if (dims == null) {
      return F.NIL;
    }
    int size = 1;
    for (int d : dims) {
      size *= d;
      if (d < 1 || (cosine && type == 1 && d < 2) || size > 1 << 20) {
        return F.NIL;
      }
    }
    Complex[] data = new Complex[size];
    boolean[] real = new boolean[] {true};
    int[] count = new int[] {0};
    if (!flatten(ast.arg1(), data, count, real) || count[0] != size) {
      return F.NIL;
    }
    // transform every dimension: the lines along dimension k have the stride of the later ones
    int stride = size;
    for (int k = 0; k < dims.length; k++) {
      engine.checkTimeLimit();
      int n = dims[k];
      stride /= n;
      Complex[] line = new Complex[n];
      for (int start = 0; start < size; start++) {
        if ((start / stride) % n != 0) {
          continue;
        }
        for (int r = 0; r < n; r++) {
          line[r] = data[start + r * stride];
        }
        Complex[] y = transformLine(line, type, cosine);
        for (int s = 0; s < n; s++) {
          data[start + s * stride] = y[s];
        }
      }
    }
    return unflatten(dims, 0, data, new int[] {0}, real[0]);
  }

  /** The type 1 to 4 of <code>m</code>, also a roman numeral string; <code>-1</code> otherwise. */
  private static int transformType(IExpr m) {
    if (m.isString()) {
      switch (m.toString()) {
        case "I":
          return 1;
        case "II":
          return 2;
        case "III":
          return 3;
        case "IV":
          return 4;
        default:
          return -1;
      }
    }
    int type = m.toIntDefault();
    return type >= 1 && type <= 4 ? type : -1;
  }

  /** The transform of one line of values. */
  private static Complex[] transformLine(Complex[] x, int type, boolean cosine) {
    final int n = x.length;
    Complex[] y = new Complex[n];
    for (int s = 0; s < n; s++) {
      Complex sum = Complex.ZERO;
      for (int r = 0; r < n; r++) {
        sum = sum.add(x[r].multiply(coefficient(type, cosine, n, r, s)));
      }
      y[s] = sum;
    }
    return y;
  }

  /** The factor of the input value <code>r</code> in the output value <code>s</code>. */
  private static double coefficient(int type, boolean cosine, int n, int r, int s) {
    final double sign = (s % 2 == 0) ? 1.0 : -1.0;
    switch (type) {
      case 1:
        if (cosine) {
          double c = (r == 0) ? 1.0
              : (r == n - 1) ? sign : 2.0 * Math.cos(Math.PI * r * s / (n - 1));
          return c / Math.sqrt(2.0 * (n - 1));
        }
        return Math.sqrt(2.0 / (n + 1)) * Math.sin(Math.PI * (r + 1) * (s + 1) / (n + 1));
      case 3:
        if (cosine) {
          return (r == 0 ? 1.0 : 2.0 * Math.cos(Math.PI * r * (s + 0.5) / n)) / Math.sqrt(n);
        }
        return (r == n - 1 ? sign : 2.0 * Math.sin(Math.PI * (r + 1) * (s + 0.5) / n))
            / Math.sqrt(n);
      case 4: {
        double angle = Math.PI * (r + 0.5) * (s + 0.5) / n;
        return Math.sqrt(2.0 / n) * (cosine ? Math.cos(angle) : Math.sin(angle));
      }
      default:
        return (cosine ? Math.cos(Math.PI * s * (r + 0.5) / n)
            : Math.sin(Math.PI * (s + 1) * (r + 0.5) / n)) / Math.sqrt(n);
    }
  }

  /** The dimensions of a full array of numbers, <code>null</code> for a ragged array. */
  private static int[] dimensions(IExpr list) {
    int[] dims = list.isMatrix(false);
    if (dims != null) {
      return dims;
    }
    int depth = 0;
    IExpr e = list;
    while (e.isList() && e.argSize() > 0) {
      depth++;
      e = e.first();
    }
    dims = new int[depth];
    e = list;
    for (int i = 0; i < depth; i++) {
      dims[i] = e.argSize();
      e = e.first();
    }
    return dims;
  }

  private static boolean flatten(IExpr expr, Complex[] data, int[] index, boolean[] real) {
    if (expr.isList()) {
      for (IExpr element : (IAST) expr) {
        if (!flatten(element, data, index, real)) {
          return false;
        }
      }
      return true;
    }
    if (!expr.isNumericFunction() || index[0] >= data.length) {
      return false;
    }
    try {
      Complex c = expr.evalfc();
      if (c.getImaginary() != 0.0) {
        real[0] = false;
      }
      data[index[0]++] = c;
      return true;
    } catch (RuntimeException rex) {
      return false;
    }
  }

  private static IExpr unflatten(int[] dims, int level, Complex[] data, int[] index,
      boolean real) {
    IASTAppendable result = F.ListAlloc(dims[level]);
    for (int i = 0; i < dims[level]; i++) {
      if (level == dims.length - 1) {
        Complex c = data[index[0]++];
        result.append(real ? F.num(c.getReal()) : F.complexNum(c));
      } else {
        result.append(unflatten(dims, level + 1, data, index, real));
      }
    }
    return result;
  }
}
