package org.matheclipse.core.sympy.core.strategies.branch;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.interfaces.IExpr;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;

/**
 * Port of <code>sympy.strategies.branch.core</code>.
 *
 * <p>
 * A branching rule returns all expressions a rule can lead to. In sympy it's a generator, here a
 * <code>Function&lt;IExpr, Iterator&lt;IExpr&gt;&gt;</code>. The iterators are lazy like the
 * generators, because the number of results grows combinatorially. An empty iterator means that the
 * rule doesn't apply; to the rules of the parent package that is {@link org.matheclipse.core.expression.F#NIL}.
 *
 * <p>
 * Lazy iterators nest, so unlike the walks in
 * {@link org.matheclipse.core.sympy.core.traversal.Traversal} these strategies do use the Java
 * stack, one level for each rule in a {@link #chain(Function...)} and for each level of a tree in
 * {@link BranchTraverse}. Only {@link #exhaust(Function)} has an explicit stack.
 */
public final class BranchCore {

  private BranchCore() {}

  /**
   * The branching rule which yields its argument.
   *
   * @return
   */
  public static Function<IExpr, Iterator<IExpr>> identity() {
    return Iterators::singletonIterator;
  }

  private static final class Frame {
    final IExpr expr;
    final Iterator<IExpr> results;
    final Set<IExpr> seen = new HashSet<IExpr>();

    Frame(IExpr expr, Iterator<IExpr> results) {
      this.expr = expr;
      this.results = results;
      seen.add(expr);
    }
  }

  /**
   * Apply a branching rule repeatedly until it has no effect, and yield the end points.
   *
   * <p>
   * Different from sympy, which recurses until the stack overflows if the rule leads back to an
   * earlier expression: the current path is kept in a set, and such a result counts as no effect.
   * For a cycle <code>a -&gt; b -&gt; a</code> the end point is <code>b</code>. The search checks the
   * time limit, and its depth is limited by <code>$IterationLimit</code>.
   *
   * @param brule
   * @return
   */
  public static Function<IExpr, Iterator<IExpr>> exhaust(Function<IExpr, Iterator<IExpr>> brule) {
    return expr -> new AbstractIterator<IExpr>() {
      private final EvalEngine engine = EvalEngine.get();
      private final int limit = engine.getIterationLimit();
      private final ArrayDeque<Frame> stack = new ArrayDeque<Frame>();
      private final Set<IExpr> path = new HashSet<IExpr>();

      {
        push(expr);
      }

      private void push(IExpr node) {
        if (limit >= 0 && stack.size() > limit) {
          IterationLimitExceeded.throwIt(stack.size(), expr);
        }
        stack.push(new Frame(node, brule.apply(node)));
        path.add(node);
      }

      @Override
      protected IExpr computeNext() {
        while (!stack.isEmpty()) {
          engine.checkTimeLimit();
          final Frame frame = stack.peek();
          if (frame.results.hasNext()) {
            final IExpr next = frame.results.next();
            if (!path.contains(next) && frame.seen.add(next)) {
              push(next);
            }
          } else {
            stack.pop();
            path.remove(frame.expr);
            if (frame.seen.size() == 1) {
              return frame.expr;
            }
          }
        }
        return endOfData();
      }
    };
  }

  /**
   * Call <code>action</code> for each result which differs from the input. sympy passes the rule as
   * well, for its name.
   *
   * @param brule
   * @param action is called with the input and the result
   * @return
   */
  public static Function<IExpr, Iterator<IExpr>> onaction(Function<IExpr, Iterator<IExpr>> brule,
      BiConsumer<IExpr, IExpr> action) {
    return expr -> Iterators.transform(brule.apply(expr), result -> {
      if (!result.equals(expr)) {
        action.accept(expr, result);
      }
      return result;
    });
  }

  /**
   * Print the input and output expressions at each rule application.
   *
   * @param brule
   * @param name the name which is printed for the rule
   * @param out the destination, or <code>null</code> for the output stream of the engine
   * @return
   */
  public static Function<IExpr, Iterator<IExpr>> debug(Function<IExpr, Iterator<IExpr>> brule,
      String name, Appendable out) {
    return onaction(brule, (expr, result) -> {
      try {
        final Appendable writer = out == null ? EvalEngine.get().getOutPrintStream() : out;
        writer.append("Rule: " + name + "\nIn: " + expr + "\nOut: " + result + "\n\n");
      } catch (IOException ioex) {
        throw new UncheckedIOException(ioex);
      }
    });
  }

  /**
   * Multiplex many branching rules into one. Each result is yielded once.
   *
   * @param brules
   * @return
   */
  @SafeVarargs
  public static Function<IExpr, Iterator<IExpr>> multiplex(
      Function<IExpr, Iterator<IExpr>>... brules) {
    return expr -> {
      final Set<IExpr> seen = new HashSet<IExpr>();
      final Iterator<Iterator<IExpr>> results =
          Iterators.transform(Arrays.asList(brules).iterator(), brl -> brl.apply(expr));
      return Iterators.filter(Iterators.concat(results), seen::add);
    };
  }

  /**
   * Only apply the branching rule if the condition is true. Otherwise nothing is yielded.
   *
   * @param cond
   * @param brule
   * @return
   */
  public static Function<IExpr, Iterator<IExpr>> condition(Predicate<IExpr> cond,
      Function<IExpr, Iterator<IExpr>> brule) {
    return expr -> cond.test(expr) ? brule.apply(expr) : Collections.<IExpr>emptyIterator();
  }

  /**
   * Yield only those results which satisfy the predicate.
   *
   * @param pred
   * @param brule
   * @return
   */
  public static Function<IExpr, Iterator<IExpr>> sfilter(Predicate<IExpr> pred,
      Function<IExpr, Iterator<IExpr>> brule) {
    return expr -> Iterators.filter(brule.apply(expr), pred::test);
  }

  /**
   * Yield the input if the rule yields nothing.
   *
   * @param brule
   * @return
   */
  public static Function<IExpr, Iterator<IExpr>> notempty(Function<IExpr, Iterator<IExpr>> brule) {
    return expr -> {
      final Iterator<IExpr> results = brule.apply(expr);
      return results.hasNext() ? results : Iterators.singletonIterator(expr);
    };
  }

  /**
   * Execute one of the branching rules: the first which yields something.
   *
   * @param brules
   * @return
   */
  @SafeVarargs
  public static Function<IExpr, Iterator<IExpr>> doOne(
      Function<IExpr, Iterator<IExpr>>... brules) {
    return expr -> {
      for (Function<IExpr, Iterator<IExpr>> brl : brules) {
        final Iterator<IExpr> results = brl.apply(expr);
        if (results.hasNext()) {
          return results;
        }
      }
      return Collections.emptyIterator();
    };
  }

  /**
   * Compose a sequence of branching rules so that they apply to the expression sequentially. Without
   * rules this is {@link #identity()}.
   *
   * @param brules
   * @return
   */
  @SafeVarargs
  public static Function<IExpr, Iterator<IExpr>> chain(
      Function<IExpr, Iterator<IExpr>>... brules) {
    return chain(Arrays.asList(brules));
  }

  private static Function<IExpr, Iterator<IExpr>> chain(
      List<Function<IExpr, Iterator<IExpr>>> brules) {
    if (brules.isEmpty()) {
      return identity();
    }
    final Function<IExpr, Iterator<IExpr>> head = brules.get(0);
    final Function<IExpr, Iterator<IExpr>> tail = chain(brules.subList(1, brules.size()));
    return expr -> Iterators.concat(Iterators.transform(head.apply(expr), tail::apply));
  }

  /**
   * Turn a rule into a branching rule.
   *
   * @param rule
   * @return
   */
  public static Function<IExpr, Iterator<IExpr>> yieldify(Function<IExpr, IExpr> rule) {
    return expr -> Iterators.singletonIterator(rule.apply(expr).orElse(expr));
  }
}
