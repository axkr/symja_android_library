package org.matheclipse.core.sympy.core.strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.matheclipse.core.sympy.core.strategies.Tree.leaf;
import static org.matheclipse.core.sympy.core.strategies.Tree.list;
import static org.matheclipse.core.sympy.core.strategies.Tree.tuple;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.core.strategies.Tree.Node;
import com.google.common.collect.Sets;

/**
 * Ported from <code>sympy/strategies/tests/test_tree.py</code>.
 */
public class TestTree {
  private static final Function<IExpr, IExpr> inc = x -> x.plus(F.C1);
  private static final Function<IExpr, IExpr> dec = x -> x.plus(F.CN1);
  private static final Function<IExpr, IExpr> doubled = x -> x.times(F.C2);
  private static final Function<IExpr, IExpr> square = x -> x.times(x);
  private static final Function<IExpr, IExpr> negative = x -> x.negate();

  private static IExpr z(int value) {
    return F.ZZ(value);
  }

  private static Set<IExpr> setOf(int... values) {
    final Set<IExpr> result = new HashSet<IExpr>();
    for (int value : values) {
      result.add(z(value));
    }
    return result;
  }

  private static int add(List<Integer> args) {
    return args.stream().mapToInt(Integer::intValue).sum();
  }

  private static int mul(List<Integer> args) {
    return args.stream().reduce(1, (a, b) -> a * b);
  }

  private static Node<Integer> numbers() {
    return tuple(list(leaf(3), leaf(3)), list(leaf(4), leaf(1)), leaf(2));
  }

  /** An inc, or a dec followed by a double. */
  private static Node<Function<IExpr, IExpr>> incOrDecDouble() {
    return list(leaf(inc), tuple(leaf(dec), leaf(doubled)));
  }

  @Test
  public void test_treeapply() {
    final Function<Integer, Integer> identity = Function.identity();
    assertEquals(3, Tree.<Integer, Integer>treeapply(numbers(), Collections::min, Collections::max,
        identity));
    assertEquals(60, Tree.<Integer, Integer>treeapply(numbers(), TestTree::add, TestTree::mul,
        identity));
  }

  @Test
  public void test_treeapply_leaf() {
    final Function<Integer, Integer> identity = Function.identity();
    assertEquals(9, Tree.<Integer, Integer>treeapply(leaf(3), l -> 0, l -> 0, x -> x * x));
    final Node<Integer> treep1 =
        tuple(list(leaf(4), leaf(4)), list(leaf(5), leaf(2)), leaf(3));
    assertEquals(
        Tree.<Integer, Integer>treeapply(treep1, Collections::min, Collections::max, identity),
        Tree.<Integer, Integer>treeapply(numbers(), Collections::min, Collections::max,
            x -> x + 1));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void test_treeapply_strategies() {
    final Function<List<Function<IExpr, IExpr>>, Function<IExpr, IExpr>> chain =
        rules -> Core.chain(rules.toArray(new Function[0]));
    final Function<List<Function<IExpr, IExpr>>, Function<IExpr, IExpr>> minimize =
        rules -> Core.minimize(rules.toArray(new Function[0]));
    final Function<Function<IExpr, IExpr>, Function<IExpr, IExpr>> identity = Function.identity();

    assertEquals(inc, Tree.treeapply(leaf(inc), chain, minimize, identity));
    assertEquals(Core.minimize(inc, dec).apply(z(5)),
        Tree.treeapply(tuple(leaf(inc), leaf(dec)), chain, minimize, identity).apply(z(5)));
    assertEquals(Core.chain(inc, dec).apply(z(5)),
        Tree.treeapply(list(leaf(inc), leaf(dec)), chain, minimize, identity).apply(z(5)));

    // list is the chain and tuple the selection here, so the tree is inc or dec-then-double
    final Node<Function<IExpr, IExpr>> tree =
        tuple(leaf(inc), list(leaf(dec), leaf(doubled)));
    assertEquals(z(6), Tree.treeapply(tree, chain, minimize, identity).apply(z(5)));
    assertEquals(F.C0, Tree.treeapply(tree, chain, minimize, identity).apply(F.C1));

    final Function<List<Function<IExpr, IExpr>>, Function<IExpr, IExpr>> maximize =
        rules -> Core.minimizeBy(negative, rules.toArray(new Function[0]));
    assertEquals(z(6), Tree.treeapply(tree, chain, maximize, identity).apply(z(4)));
    assertEquals(F.C2, Tree.treeapply(tree, chain, maximize, identity).apply(F.C1));
  }

  @Test
  public void test_greedy() {
    final Function<IExpr, IExpr> fn = Tree.greedy(incOrDecDouble(), negative);
    assertEquals(z(6), fn.apply(z(4)));
    assertEquals(F.C2, fn.apply(F.C1));

    final Node<Function<IExpr, IExpr>> tree = list(leaf(inc), leaf(dec),
        list(leaf(inc), leaf(dec), list(tuple(leaf(inc), leaf(inc)), tuple(leaf(dec), leaf(dec)))));
    assertEquals(z(8), Tree.greedy(tree).apply(z(10)));
    assertEquals(z(12), Tree.greedy(tree, negative).apply(z(10)));
  }

  @Test
  public void test_allresults() {
    assertEquals(Set.of(inc.apply(z(3))), Sets.newHashSet(Tree.allresults(leaf(inc)).apply(z(3))));
    assertEquals(setOf(2, 4),
        Sets.newHashSet(Tree.allresults(list(leaf(inc), leaf(dec))).apply(z(3))));
    assertEquals(setOf(3),
        Sets.newHashSet(Tree.allresults(tuple(leaf(inc), leaf(dec))).apply(z(3))));
    assertEquals(setOf(5, 6), Sets.newHashSet(Tree.allresults(incOrDecDouble()).apply(z(4))));
  }

  @Test
  public void test_brute() {
    final Node<Function<IExpr, IExpr>> tree = tuple(list(leaf(inc), leaf(dec)), leaf(square));
    final Function<IExpr, IExpr> fn = Tree.brute(tree, negative);
    assertEquals(z(9), fn.apply(F.C2));
    assertEquals(z(9), fn.apply(z(-2)));
    assertEquals(F.C2, Tree.brute(leaf(inc), Function.identity()).apply(F.C1));
  }

  @Test
  public void test_brute_scores_each_candidate_once() {
    final int[] calls = new int[1];
    final Function<IExpr, IExpr> objective = x -> {
      calls[0]++;
      return x;
    };
    final Node<Function<IExpr, IExpr>> tree = list(leaf(inc), leaf(dec), leaf(doubled));
    assertEquals(z(3), Tree.brute(tree, objective).apply(z(4)));
    assertEquals(3, calls[0]);
  }
}
