package org.matheclipse.core.sympy.core.strategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.core.strategies.branch.BranchCore;

/**
 * Port of <code>sympy.strategies.tree</code>, which runs a strategy written down as a tree.
 *
 * <p>
 * A node of the tree is one of the following. In sympy a tuple and a list stand for the two inner
 * nodes; here they are the factory methods {@link #tuple(Node...)} and {@link #list(Node...)}.
 * <ul>
 * <li>{@link #leaf(Object)}: a rule,
 * <li>{@link #list(Node...)}: a selection among operations,
 * <li>{@link #tuple(Node...)}: a sequence of chained operations.
 * </ul>
 *
 * <p>
 * For example "run f or g, whichever is better, then run h" is
 * <code>tuple(list(leaf(f), leaf(g)), leaf(h))</code>. This is the form of the Fu algorithm in
 * <code>sympy.simplify.fu</code>, which is written out by hand in
 * {@link org.matheclipse.core.reflection.system.TrigSimplifyFu}.
 *
 * <p>
 * The recursion follows the depth of the strategy, not of the expression, so it uses the Java stack.
 */
public final class Tree {

  private Tree() {}

  /**
   * A node of a strategy tree.
   *
   * @param <T> the type of the leaves
   */
  public static final class Node<T> {
    private final T value;
    private final boolean isList;
    private final List<Node<T>> children;

    private Node(T value, boolean isList, List<Node<T>> children) {
      this.value = value;
      this.isList = isList;
      this.children = children;
    }
  }

  /**
   * A leaf of a tree.
   *
   * @param <T>
   * @param value
   * @return
   */
  public static <T> Node<T> leaf(T value) {
    return new Node<T>(value, false, null);
  }

  /**
   * A selection, a Python list.
   *
   * @param <T>
   * @param children
   * @return
   */
  @SafeVarargs
  public static <T> Node<T> list(Node<T>... children) {
    return new Node<T>(null, true, Arrays.asList(children));
  }

  /**
   * A sequence, a Python tuple.
   *
   * @param <T>
   * @param children
   * @return
   */
  @SafeVarargs
  public static <T> Node<T> tuple(Node<T>... children) {
    return new Node<T>(null, false, Arrays.asList(children));
  }

  /**
   * Apply functions onto a tree. sympy takes a dictionary from container types to functions; here
   * there are two parameters for the two container types.
   *
   * @param <T> the type of the leaves
   * @param <R> the type of the result
   * @param tree
   * @param joinList combines the results for the children of a list
   * @param joinTuple combines the results for the children of a tuple
   * @param leaf maps a leaf
   * @return
   */
  public static <T, R> R treeapply(Node<T> tree, Function<List<R>, R> joinList,
      Function<List<R>, R> joinTuple, Function<T, R> leaf) {
    if (tree.children == null) {
      return leaf.apply(tree.value);
    }
    final List<R> results = new ArrayList<R>(tree.children.size());
    for (Node<T> child : tree.children) {
      results.add(treeapply(child, joinList, joinTuple, leaf));
    }
    return (tree.isList ? joinList : joinTuple).apply(results);
  }

  /**
   * Execute a strategic tree, selecting alternatives greedily with the canonical order of
   * expressions as objective.
   *
   * @param tree
   * @return
   * @see #greedy(Node, Function)
   */
  public static Function<IExpr, IExpr> greedy(Node<Function<IExpr, IExpr>> tree) {
    return greedy(tree, Function.identity());
  }

  /**
   * Execute a strategic tree, selecting the alternative which minimizes the objective. The choice is
   * greedy: in <code>tuple(list(a, b), c)</code> it is made without foresight to <code>c</code>.
   *
   * @param <C> the type of the score
   * @param tree
   * @param objective
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <C extends Comparable<? super C>> Function<IExpr, IExpr> greedy(
      Node<Function<IExpr, IExpr>> tree, Function<? super IExpr, C> objective) {
    return Tree.<Function<IExpr, IExpr>, Function<IExpr, IExpr>>treeapply(tree,
        rules -> Core.minimizeBy(objective, rules.toArray(new Function[0])),
        rules -> Core.chain(rules.toArray(new Function[0])), Function.identity());
  }

  /**
   * Execute a strategic tree and return all possibilities as a lazy iterator. In
   * <code>tuple(list(a, b), list(c, d))</code> all of <code>(a, c), (b, c), (a, d), (b, d)</code> are
   * run, which can lead to combinatorial blowup.
   *
   * @param tree
   * @return
   */
  public static Function<IExpr, Iterator<IExpr>> allresults(Node<Function<IExpr, IExpr>> tree) {
    return allresults(tree, BranchCore::yieldify);
  }

  /**
   * Execute a strategic tree and return all possibilities as a lazy iterator.
   *
   * @param tree
   * @param leaf turns a rule into a branching rule
   * @return
   * @see #allresults(Node)
   */
  @SuppressWarnings("unchecked")
  public static Function<IExpr, Iterator<IExpr>> allresults(Node<Function<IExpr, IExpr>> tree,
      Function<Function<IExpr, IExpr>, Function<IExpr, Iterator<IExpr>>> leaf) {
    return Tree.<Function<IExpr, IExpr>, Function<IExpr, Iterator<IExpr>>>treeapply(tree,
        rules -> BranchCore.multiplex(rules.toArray(new Function[0])),
        rules -> BranchCore.chain(rules.toArray(new Function[0])), leaf);
  }

  /**
   * Execute all possibilities of a strategic tree, and select the one which minimizes the objective.
   *
   * @param <C> the type of the score
   * @param tree
   * @param objective
   * @return
   */
  public static <C extends Comparable<? super C>> Function<IExpr, IExpr> brute(
      Node<Function<IExpr, IExpr>> tree, Function<? super IExpr, C> objective) {
    final Function<IExpr, Iterator<IExpr>> results = allresults(tree);
    return expr -> Core.minimum(results.apply(expr), objective);
  }
}
