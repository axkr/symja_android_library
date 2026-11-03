package org.matheclipse.core.sympy.core.strategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.core.traversal.Traversal;
import org.matheclipse.core.sympy.core.traversal.Traversal.Fns;

/**
 * Port of <code>sympy.strategies.rl</code>, the generic rules on the arguments of one node.
 *
 * <p>
 * Each rule looks at one level only; {@link Traverse} applies it to a tree. Nothing is evaluated
 * unless a {@link Fns} says so: {@link Util#BASIC_FNS} is sympy's default, the unevaluated
 * <code>Basic.__new__</code>. The Symja evaluator does the job of most of these rules on its own for
 * the attributes <code>Flat</code>, <code>Orderless</code> and <code>OneIdentity</code>, so they are
 * for expressions on which the evaluator doesn't run, and for heads which don't have them.
 */
public final class Rl {

  private Rl() {}

  /**
   * Create a rule to remove identities. If there are only identities, one of them is kept.
   *
   * @param isid whether or not an argument is an identity
   * @return
   */
  public static Function<IExpr, IExpr> rmId(Predicate<IExpr> isid) {
    return rmId(isid, Util.BASIC_FNS);
  }

  /**
   * Create a rule to remove identities. If there are only identities, one of them is kept.
   *
   * @param isid whether or not an argument is an identity
   * @param fns rebuilds the node
   * @return
   */
  public static Function<IExpr, IExpr> rmId(Predicate<IExpr> isid, Fns fns) {
    return expr -> {
      if (!expr.isAST()) {
        return expr;
      }
      final IAST ast = (IAST) expr;
      final IASTAppendable rest = ast.copyHead(ast.size());
      ast.forEach(x -> {
        if (!isid.test(x)) {
          rest.append(x);
        }
      });
      if (rest.size() == ast.size()) {
        // no identities, the common case
        return expr;
      }
      if (rest.argSize() == 0) {
        rest.append(ast.arg1());
      }
      return fns.rebuild(rest);
    };
  }

  /**
   * Create a rule to conglomerate identical arguments, for example <code>x+x -&gt; 2*x</code>. This
   * is what the evaluator's <code>Plus</code> does with its coefficient map, for any head and any
   * notion of "identical".
   *
   * <p>
   * Example: for sums <code>key</code> is the term without its numeric factor, <code>count</code>
   * the factor, and <code>combine</code> is <code>(count, term) -&gt; count*term</code>.
   *
   * @param key maps an argument to what is identical
   * @param count maps an argument to how many times <code>key</code> it stands for
   * @param combine maps the summed up count and the key to the new argument
   * @return
   */
  public static Function<IExpr, IExpr> glom(Function<IExpr, IExpr> key,
      Function<IExpr, IExpr> count, BiFunction<IExpr, IExpr, IExpr> combine) {
    return expr -> {
      if (!expr.isAST()) {
        return expr;
      }
      final IAST ast = (IAST) expr;
      final EvalEngine engine = EvalEngine.get();
      // the first occurrence decides the position, as in the dict of sympy's sift()
      final Map<IExpr, IASTAppendable> counts = new LinkedHashMap<IExpr, IASTAppendable>();
      ast.forEach(x -> counts.computeIfAbsent(key.apply(x), k -> F.PlusAlloc(4)).append(count.apply(x)));
      final IASTAppendable newArgs = ast.copyHead(counts.size() + 1);
      counts.forEach((k, sum) -> newArgs.append(combine.apply(engine.evaluate(sum), k)));
      if (asSet(newArgs).equals(asSet(ast))) {
        return expr;
      }
      return newArgs;
    };
  }

  private static Set<IExpr> asSet(IAST ast) {
    final Set<IExpr> result = new HashSet<IExpr>();
    ast.forEach(result::add);
    return result;
  }

  /**
   * Create a rule to sort the arguments by a key function.
   *
   * @param <C> the type of the key
   * @param key
   * @return
   */
  public static <C extends Comparable<? super C>> Function<IExpr, IExpr> sort(
      Function<IExpr, C> key) {
    return sort(key, Util.BASIC_FNS);
  }

  /**
   * Create a rule to sort the arguments by a key function. The sort is stable.
   *
   * @param <C> the type of the key
   * @param key
   * @param fns rebuilds the node
   * @return
   */
  public static <C extends Comparable<? super C>> Function<IExpr, IExpr> sort(
      Function<IExpr, C> key, Fns fns) {
    return expr -> {
      if (!expr.isAST()) {
        return expr;
      }
      final IAST ast = (IAST) expr;
      // one read of each argument: get() of a packed vector or matrix makes a new object each time
      final IExpr[] args = new IExpr[ast.argSize()];
      for (int i = 1; i < ast.size(); i++) {
        args[i - 1] = ast.get(i);
      }
      final IExpr[] sorted = args.clone();
      Arrays.sort(sorted, Comparator.comparing(key));
      IASTMutable result = F.NIL;
      for (int i = 0; i < sorted.length; i++) {
        if (sorted[i] != args[i]) {
          result = Traversal.setArgument(ast, result, i + 1, sorted[i]);
        }
      }
      return result.isNIL() ? expr : fns.rebuild(result);
    };
  }

  /**
   * Turns an <code>A</code> containing <code>B</code>s into a <code>B</code> of <code>A</code>s.
   * Only the first <code>B</code> is distributed over, so {@link Core#exhaust(Function)} and a tree
   * walk are needed for a full expansion.
   *
   * @param a the head of the outer function
   * @param b the head of the inner function
   * @return
   */
  public static Function<IExpr, IExpr> distribute(IExpr a, IExpr b) {
    return expr -> {
      if (!expr.isAST()) {
        return expr;
      }
      final IAST ast = (IAST) expr;
      for (int i = 1; i < ast.size(); i++) {
        final IExpr arg = ast.get(i);
        if (arg.isAST() && arg.head().equals(b)) {
          final IAST inner = (IAST) arg;
          final IASTAppendable result = F.ast(b, inner.size());
          final IASTMutable outer = ast.setAtCopy(0, a);
          final int position = i;
          inner.forEach(x -> result.append(outer.setAtCopy(position, x)));
          return result;
        }
      }
      return expr;
    };
  }

  /**
   * Replace expressions exactly.
   *
   * @param a
   * @param b
   * @return
   */
  public static Function<IExpr, IExpr> subs(IExpr a, IExpr b) {
    return expr -> expr.equals(a) ? b : expr;
  }

  /**
   * Rule to unpack singleton arguments.
   *
   * @param expr
   * @return
   */
  public static IExpr unpack(IExpr expr) {
    return expr.isAST() && expr.argSize() == 1 ? expr.first() : expr;
  }

  /**
   * Flatten <code>T(a, b, T(c, d), T2(e))</code> to <code>T(a, b, c, d, T2(e))</code>.
   *
   * @param expr
   * @return
   */
  public static IExpr flatten(IExpr expr) {
    return flatten(expr, Util.BASIC_FNS);
  }

  /**
   * Flatten <code>T(a, b, T(c, d), T2(e))</code> to <code>T(a, b, c, d, T2(e))</code>. Only one
   * level is flattened.
   *
   * @param expr
   * @param fns rebuilds the node
   * @return <code>expr</code>, if there was nothing to flatten
   */
  public static IExpr flatten(IExpr expr, Fns fns) {
    if (!expr.isAST()) {
      return expr;
    }
    final IAST ast = (IAST) expr;
    final IExpr head = ast.head();
    final List<IExpr> args = new ArrayList<IExpr>(ast.size());
    boolean flattened = false;
    for (int i = 1; i < ast.size(); i++) {
      final IExpr arg = ast.get(i);
      if (arg.isAST() && arg.head().equals(head)) {
        flattened = true;
        ((IAST) arg).forEach(x -> args.add(x));
      } else {
        args.add(arg);
      }
    }
    return flattened ? fns.rebuild(F.ast(head, args)) : expr;
  }

  /**
   * Rebuild an expression tree, which forces canonicalization and removes what unevaluated
   * construction has left behind.
   *
   * <p>
   * In sympy this recursively calls the constructors. {@link EvalEngine#evaluate(IExpr)} does the
   * same thing inner parts first, and stops at nodes which are known to be evaluated.
   *
   * @param expr
   * @return
   */
  public static IExpr rebuild(IExpr expr) {
    return EvalEngine.get().evaluate(expr);
  }
}
