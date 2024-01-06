package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 *
 *
 * <pre>
 * Outer(f, x, y)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * computes a generalised outer product of <code>x</code> and <code>y</code>, using the function
 * <code>f</code> in place of multiplication.
 *
 * </blockquote>
 *
 * <pre>
 * &gt;&gt; Outer(f, {a, b}, {1, 2, 3})
 * {{f(a, 1), f(a, 2), f(a, 3)}, {f(b, 1), f(b, 2), f(b, 3)}}
 * </pre>
 *
 * <p>
 * Outer product of two matrices:
 *
 * <pre>
 * &gt;&gt; Outer(Times, {{a, b}, {c, d}}, {{1, 2}, {3, 4}})
 * {{{{a, 2 a}, {3 a, 4 a}}, {{b, 2 b}, {3 b, 4 b}}}, {{{c, 2 c}, {3 c, 4 c}}, {{d, 2 d}, {3 d, 4 d}}}}
 * </pre>
 *
 * <p>
 * Outer of multiple lists:
 *
 * <pre>
 * &gt;&gt; Outer(f, {a, b}, {x, y, z}, {1, 2})
 * {{{f(a, x, 1), f(a, x, 2)}, {f(a, y, 1), f(a, y, 2)}, {f(a, z, 1), f(a, z, 2)}}, {{f(b, x, 1), f(b, x, 2)}, {f(b, y, 1), f(b, y, 2)}, {f(b, z, 1), f(b, z, 2)}}}
 * </pre>
 *
 * <p>
 * Arrays can be ragged:
 *
 * <pre>
 * &gt;&gt; Outer(Times, {{1, 2}}, {{a, b}, {c, d, e}})
 * {{{{a, b}, {c, d, e}}, {{2 a, 2 b}, {2 c, 2 d, 2 e}}}}
 * </pre>
 *
 * <p>
 * Word combinations:
 *
 * <pre>
 * &gt;&gt; Outer(StringJoin, {"", "re", "un"}, {"cover", "draw", "wind"}, {"", "ing", "s"})
 * {{{"cover", "covering", "covers"}, {"draw", "drawing", "draws"}, {"wind", "winding", "winds"}}, {{"recover", "recovering", "recovers"}, {"redraw", "redrawing", "redraws"}, {"rewind", "rewinding", "rewinds"}}, {{"uncover", "uncovering", "uncovers"}, {"undraw", "undrawing", "undraws"}, {"unwind", "unwinding", "unwinds"}}}
 * </pre>
 *
 * <p>
 * Compositions of trigonometric functions:
 *
 * <pre>
 * &gt;&gt; trigs = Outer(Composition, {Sin, Cos, Tan}, {ArcSin, ArcCos, ArcTan})
 * {{Composition(Sin, ArcSin), Composition(Sin, ArcCos), Composition(Sin, ArcTan)}, {Composition(Cos, ArcSin), Composition(Cos, ArcCos), Composition(Cos, ArcTan)}, {Composition(Tan, ArcSin), Composition(Tan, ArcCos), Composition(Tan, ArcTan)}}
 * </pre>
 *
 * <p>
 * Evaluate at <code>0</code>:
 *
 * <pre>
 * &gt;&gt; Map(#(0) &amp;, trigs, {2})
 * {{0, 1, 0}, {1, 0, 1}, {0, ComplexInfinity, 0}}
 * </pre>
 */
public class Outer extends AbstractFunctionEvaluator {

  private static class OuterAlgorithm {
    final IAST ast;
    final IExpr f;
    final IExpr head;

    public OuterAlgorithm(final IAST ast, final IExpr head) {
      this.ast = ast;
      this.f = ast.arg1();
      this.head = head;
    }

    private IAST outer(int astPosition, IExpr expr, IASTAppendable current) {
      if (expr.isAST() && head.equals(expr.head())) {
        IAST list = (IAST) expr;
        int size = list.size();
        return F.mapRange(head, 1, size, i -> outer(astPosition, list.get(i), current));
      }

      if (ast.size() > astPosition) {
        try {
          current.append(expr);
          return outer(astPosition + 1, ast.get(astPosition), current);
        } finally {
          current.remove(current.argSize());
        }
      } else {
        IASTAppendable result = F.ast(f);
        result.appendArgs(current);
        result.append(expr);
        return result;
      }
    }
  }

  // private static class OuterSparseArrayAlgorithm {
  // final IAST ast;
  // final IExpr f;
  // final IExpr head;
  //
  // public OuterSparseArrayAlgorithm(final IAST ast, final IExpr head) {
  // this.ast = ast;
  // this.f = ast.arg1();
  // this.head = head;
  // }
  //
  // private ISparseArray outer(int astPosition, IExpr expr, ISparseArray current) {
  //
  // if (expr.isSparseArray()) {
  // ISparseArray sparseArray = (ISparseArray) expr;
  // int size = sparseArray.size();
  // return F.mapRange(head, 1, size, i -> outer(astPosition, sparseArray.get(i), current));
  // } else {
  // return null;
  // }
  //
  //
  // if (ast.size() > astPosition) {
  // try {
  // current.append(expr);
  // return outer(astPosition + 1, ast.get(astPosition), current);
  // } finally {
  // current.remove(current.argSize());
  // }
  // } else {
  // IASTAppendable result = F.ast(f);
  // result.appendArgs(current);
  // result.append(expr);
  // return result;
  // }
  // }
  // }

  public Outer() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr head = null;
    IExpr arg2 = ast.arg2();
    if (arg2.isAST()) {
      head = arg2.head();
    } else if (arg2.isSparseArray()) {
      head = S.SparseArray;
    } else {
      return F.NIL;
    }
    for (int i = 3; i < ast.size(); i++) {
      IExpr list = ast.get(i);
      if (!head.equals(list.head())) {
        return F.NIL;
      }
    }
    if (head != S.SparseArray) {
      OuterAlgorithm algorithm = new OuterAlgorithm(ast, head);
      return algorithm.outer(3, ast.arg2(), F.ListAlloc(ast.argSize()));
    }
    // OuterSparseArrayAlgorithm algorithm = new OuterSparseArrayAlgorithm(ast, head);
    // final Trie<int[], IExpr> trie = Config.TRIE_INT2EXPR_BUILDER.build();
    // ISparseArray resultTensor =
    // new SparseArrayExpr(trie, resultDimensions, function.apply(getDefaultValue()), false);
    // ISparseArray result = algorithm.outer(3, ast.arg2(), resultTensor);
    // if (result != null) {
    // return result;
    // }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_3_INFINITY;
  }
}
