package org.matheclipse.core.reflection.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.SparseArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.trie.Trie;
import org.matheclipse.parser.trie.TrieNode;

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
        return F.mapRange(head, 1, size,
            i -> outer(astPosition, list.get(i).normal(false), current));
      }

      if (ast.size() > astPosition) {
        try {
          current.append(expr);
          return outer(astPosition + 1, ast.get(astPosition).normal(false), current);
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

  public Outer() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr head = null;
    IExpr f = ast.arg1();
    IExpr arg2 = ast.arg2();
    if (arg2.isAST()) {
      head = arg2.head();
    } else if (arg2.isSparseArray()) {
      head = S.SparseArray;
    } else {
      return F.NIL;
    }
    for (int i = 3; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isSparseArray()) {
        head = S.SparseArray;
      }
    }
    if (head != S.SparseArray || f != S.Times) {
      OuterAlgorithm algorithm = new OuterAlgorithm(ast, head == S.SparseArray ? S.List : head);
      return algorithm.outer(3, arg2.normal(false), F.ListAlloc(ast.argSize()));
    } else {
      // At least one argument is a SparseArray — handle sparse outer product
      // Only support f=Times with default value 0 for now

      // Collect all arguments as SparseArrayExpr (converting dense lists if needed)
      int argCount = ast.argSize(); // number of tensor arguments (ast args 2..end)
      SparseArrayExpr[] sparseArgs = new SparseArrayExpr[argCount - 1];
      for (int i = 2; i <= argCount; i++) {
        IExpr arg = ast.get(i);
        if (arg instanceof SparseArrayExpr) {
          sparseArgs[i - 2] = (SparseArrayExpr) arg;
        } else if (arg.isList()) {
          sparseArgs[i - 2] = SparseArrayExpr.newDenseList((IAST) arg, F.C0);
          if (sparseArgs[i - 2] == null)
            return F.NIL;
        } else {
          return F.NIL;
        }
      }

      // Compute result dimension = concatenation of all argument dimensions
      int totalDimLen = 0;
      for (SparseArrayExpr sa : sparseArgs)
        totalDimLen += sa.getDimension().length;
      int[] resultDim = new int[totalDimLen];
      int offset = 0;
      for (SparseArrayExpr sa : sparseArgs) {
        System.arraycopy(sa.getDimension(), 0, resultDim, offset, sa.getDimension().length);
        offset += sa.getDimension().length;
      }

      // Compute default value: f(d1, d2, ...)
      IExpr[] defaults = new IExpr[argCount - 1];
      for (int i = 0; i < argCount - 1; i++) {
        defaults[i] = sparseArgs[i].getDefaultValue();
      }
      IExpr resultDefault = engine.evaluate(F.ast(defaults, f));

      // Build result trie by iterating over non-default entries of each sparse array
      // For Times with default=0, we only need non-zero × non-zero pairs
      // General approach: iterate Cartesian product of each array's nodeSet()
      final Trie<int[], IExpr> resultTrie = Config.TRIE_INT2EXPR_BUILDER.build();

      // Recursive Cartesian iteration
      outerSparse(f, sparseArgs, 0, new int[0], F.NIL, resultDefault, resultTrie, engine);

      return new SparseArrayExpr(resultTrie, resultDim, resultDefault, false);
    }
  }

  private static void outerSparse(IExpr f, SparseArrayExpr[] args, int argIdx, int[] keyPrefix,
      IExpr valuePrefix, IExpr resultDefault, Trie<int[], IExpr> resultTrie, EvalEngine engine) {

    SparseArrayExpr current = args[argIdx];
    boolean isLast = (argIdx == args.length - 1);

    for (TrieNode<int[], IExpr> entry : current.toData().nodeSet()) {
      int[] key = entry.getKey();
      IExpr val = entry.getValue();

      // Combined value so far
      IExpr combinedVal =
          valuePrefix.isNIL() ? val : engine.evaluate(F.binaryAST2(f, valuePrefix, val));

      // Combined key so far
      int[] combinedKey = new int[keyPrefix.length + key.length];
      System.arraycopy(keyPrefix, 0, combinedKey, 0, keyPrefix.length);
      System.arraycopy(key, 0, combinedKey, keyPrefix.length, key.length);

      if (isLast) {
        if (!combinedVal.equals(resultDefault)) {
          resultTrie.put(combinedKey, combinedVal);
        }
      } else {
        outerSparse(f, args, argIdx + 1, combinedKey, combinedVal, resultDefault, resultTrie,
            engine);
      }
    }
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
