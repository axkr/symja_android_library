package org.matheclipse.core.eval.util;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A real valued expression in machine numbers, translated once into a tree of <code>double</code>
 * operations, so that evaluating it at many points needs neither a substitution nor the evaluation
 * engine. Only the operations of elementary functions are translated: <code>Plus</code>,
 * <code>Times</code>, <code>Power</code> and functions like <code>Exp</code>, <code>Log</code>,
 * <code>Sin</code>, <code>ArcTan</code>, <code>Abs</code>. A subexpression free of the variables is
 * evaluated to a number when it is translated.
 *
 * <p>
 * A value which isn't a finite real number, where the expression may be complex or infinite, is
 * returned as <code>NaN</code>: the caller then evaluates the expression symbolically at that point.
 */
public final class MachineExpression {

  /** One node of the tree. */
  private interface Node {
    double value(double[] x);
  }

  private final Node fRoot;

  private MachineExpression(Node root) {
    fRoot = root;
  }

  /**
   * Translate <code>expr</code>, in which <code>variables[i]</code> stands for the number
   * <code>x[i]</code> of {@link #value(double[])}. A variable is matched as a whole before its
   * parts, so a variable <code>y(t)</code> is found in an expression which also holds the variable
   * <code>t</code>.
   *
   * @return <code>null</code> if <code>expr</code> holds an operation which isn't translated
   */
  public static MachineExpression compile(IExpr expr, IExpr[] variables, EvalEngine engine) {
    Map<IExpr, Integer> indices = new HashMap<IExpr, Integer>();
    for (int i = 0; i < variables.length; i++) {
      indices.putIfAbsent(variables[i], i);
    }
    try {
      Node root = node(expr, indices, variables, engine);
      return root == null ? null : new MachineExpression(root);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return null;
    }
  }

  /**
   * The value at the point <code>x</code>, or <code>NaN</code> if it isn't a finite real number.
   */
  public double value(double[] x) {
    double value = fRoot.value(x);
    return Double.isFinite(value) ? value : Double.NaN;
  }

  private static boolean isFree(IExpr expr, IExpr[] variables) {
    for (IExpr variable : variables) {
      if (!expr.isFree(variable, true)) {
        return false;
      }
    }
    return true;
  }

  private static Node node(IExpr expr, Map<IExpr, Integer> indices, IExpr[] variables,
      EvalEngine engine) {
    Integer index = indices.get(expr);
    if (index != null) {
      final int i = index;
      return x -> x[i];
    }
    if (expr.isReal()) {
      final double c = expr.evalf();
      return x -> c;
    }
    if (isFree(expr, variables)) {
      IExpr value = engine.evalN(expr);
      if (value.isReal()) {
        final double c = value.evalf();
        return x -> c;
      }
      return null;
    }
    if (!expr.isAST()) {
      return null;
    }
    final IAST ast = (IAST) expr;
    final int size = ast.size();
    final Node[] args = new Node[size - 1];
    for (int i = 1; i < size; i++) {
      args[i - 1] = node(ast.get(i), indices, variables, engine);
      if (args[i - 1] == null) {
        return null;
      }
    }
    switch (expr.headID()) {
      case ID.Plus:
        if (args.length == 2) {
          final Node a = args[0];
          final Node b = args[1];
          return x -> a.value(x) + b.value(x);
        }
        return x -> {
          double sum = 0.0;
          for (Node arg : args) {
            sum += arg.value(x);
          }
          return sum;
        };
      case ID.Times:
        if (args.length == 2) {
          final Node a = args[0];
          final Node b = args[1];
          return x -> a.value(x) * b.value(x);
        }
        return x -> {
          double product = 1.0;
          for (Node arg : args) {
            product *= arg.value(x);
          }
          return product;
        };
      case ID.Power:
        return args.length == 2 ? power(ast, args[0], args[1]) : null;
      default:
        return args.length == 1 ? unary(expr.headID(), args[0])
            : args.length == 2 ? binary(expr.headID(), args[0], args[1]) : null;
    }
  }

  private static Node power(IAST power, Node base, Node exponent) {
    if (power.base() == S.E) {
      return x -> Math.exp(exponent.value(x));
    }
    IExpr e = power.exponent();
    if (e.isInteger()) {
      final int n = e.toIntDefault();
      switch (n) {
        case -1:
          return x -> 1.0 / base.value(x);
        case 2:
          return x -> {
            double b = base.value(x);
            return b * b;
          };
        case 3:
          return x -> {
            double b = base.value(x);
            return b * b * b;
          };
        case 4:
          return x -> {
            double b = base.value(x);
            b *= b;
            return b * b;
          };
        default:
          if (n != Integer.MIN_VALUE) {
            return x -> Math.pow(base.value(x), n);
          }
          return null;
      }
    }
    if (e.equals(F.C1D2)) {
      return x -> Math.sqrt(base.value(x));
    }
    if (e.equals(F.CN1D2)) {
      return x -> 1.0 / Math.sqrt(base.value(x));
    }
    // a negative base gives NaN, where the symbolic power is complex
    return x -> Math.pow(base.value(x), exponent.value(x));
  }

  private static Node unary(int id, Node a) {
    switch (id) {
      case ID.Exp:
        return x -> Math.exp(a.value(x));
      case ID.Log:
        // NaN for a negative argument, where the logarithm is complex
        return x -> Math.log(a.value(x));
      case ID.Sqrt:
        return x -> Math.sqrt(a.value(x));
      case ID.Sin:
        return x -> Math.sin(a.value(x));
      case ID.Cos:
        return x -> Math.cos(a.value(x));
      case ID.Tan:
        return x -> Math.tan(a.value(x));
      case ID.Cot:
        return x -> 1.0 / Math.tan(a.value(x));
      case ID.Sec:
        return x -> 1.0 / Math.cos(a.value(x));
      case ID.Csc:
        return x -> 1.0 / Math.sin(a.value(x));
      case ID.Sinh:
        return x -> Math.sinh(a.value(x));
      case ID.Cosh:
        return x -> Math.cosh(a.value(x));
      case ID.Tanh:
        return x -> Math.tanh(a.value(x));
      case ID.Sech:
        return x -> 1.0 / Math.cosh(a.value(x));
      case ID.ArcSin:
        return x -> Math.asin(a.value(x));
      case ID.ArcCos:
        return x -> Math.acos(a.value(x));
      case ID.ArcTan:
        return x -> Math.atan(a.value(x));
      case ID.Abs:
        return x -> Math.abs(a.value(x));
      default:
        return null;
    }
  }

  private static Node binary(int id, Node a, Node b) {
    switch (id) {
      case ID.ArcTan:
        // ArcTan(x, y) is the argument of x + I*y
        return x -> {
          double re = a.value(x);
          double im = b.value(x);
          return re == 0.0 && im == 0.0 ? Double.NaN : Math.atan2(im, re);
        };
      case ID.Log:
        // Log(b, z)
        return x -> Math.log(b.value(x)) / Math.log(a.value(x));
      case ID.Max:
        return x -> Math.max(a.value(x), b.value(x));
      case ID.Min:
        return x -> Math.min(a.value(x), b.value(x));
      default:
        return null;
    }
  }
}
