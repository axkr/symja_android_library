package org.matheclipse.core.sympy.core.strategies.branch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Ported from <code>sympy/strategies/branch/tests/test_core.py</code>.
 */
public class TestBranchCore {
  private static final Function<IExpr, Iterator<IExpr>> posdec =
      x -> Iterators.singletonIterator(x.isPositive() ? x.plus(F.CN1) : x);
  private static final Function<IExpr, Iterator<IExpr>> inc =
      x -> Iterators.singletonIterator(x.plus(F.C1));

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

  private static List<IExpr> listOf(int... values) {
    final List<IExpr> result = new ArrayList<IExpr>();
    for (int value : values) {
      result.add(z(value));
    }
    return result;
  }

  private static Iterator<IExpr> branch5(IExpr expr) {
    final int x = expr.toIntDefault();
    if (0 < x && x < 5) {
      return Iterators.singletonIterator(z(x - 1));
    } else if (5 < x && x < 10) {
      return Iterators.singletonIterator(z(x + 1));
    } else if (x == 5) {
      return listOf(x + 1, x - 1).iterator();
    }
    return Iterators.singletonIterator(z(x));
  }

  private static boolean even(IExpr x) {
    return ((IInteger) x).isEven();
  }

  private static Iterator<IExpr> oneToN(IExpr n) {
    final List<IExpr> result = new ArrayList<IExpr>();
    for (int i = 0; i < n.toIntDefault(); i++) {
      result.add(z(i));
    }
    return result.iterator();
  }

  @Test
  public void test_exhaust() {
    final Function<IExpr, Iterator<IExpr>> brl = BranchCore.exhaust(TestBranchCore::branch5);
    assertEquals(setOf(0), Sets.newHashSet(brl.apply(z(3))));
    assertEquals(setOf(10), Sets.newHashSet(brl.apply(z(7))));
    assertEquals(setOf(0, 10), Sets.newHashSet(brl.apply(z(5))));
  }

  @Test
  public void test_exhaust_of_a_cycle_ends() {
    final Function<IExpr, Iterator<IExpr>> cycle =
        x -> Iterators.singletonIterator(x.equals(F.a) ? F.b : F.a);
    assertEquals(List.of(F.b), Lists.newArrayList(BranchCore.exhaust(cycle).apply(F.a)));
  }

  @Test
  public void test_exhaust_of_an_endless_rule_hits_the_iteration_limit() {
    assertThrows(IterationLimitExceeded.class,
        () -> Lists.newArrayList(BranchCore.exhaust(inc).apply(F.C0)));
  }

  @Test
  public void test_debug() {
    final StringBuilder file = new StringBuilder();
    Iterators.getLast(BranchCore.debug(posdec, "posdec", file).apply(z(5)));
    final String log = file.toString();
    assertTrue(log.contains("posdec"));
    assertTrue(log.contains("5"));
    assertTrue(log.contains("4"));
  }

  @Test
  public void test_debug_writes_to_any_appendable() {
    final java.io.StringWriter file = new java.io.StringWriter();
    Iterators.getLast(BranchCore.debug(posdec, "posdec", file).apply(z(5)));
    assertTrue(file.toString().contains("posdec"));
  }

  @Test
  public void test_multiplex() {
    final Function<IExpr, Iterator<IExpr>> brl =
        BranchCore.multiplex(posdec, TestBranchCore::branch5);
    assertEquals(setOf(2), Sets.newHashSet(brl.apply(z(3))));
    assertEquals(setOf(6, 8), Sets.newHashSet(brl.apply(z(7))));
    assertEquals(setOf(4, 6), Sets.newHashSet(brl.apply(z(5))));
  }

  @Test
  public void test_condition() {
    final Function<IExpr, Iterator<IExpr>> brl =
        BranchCore.condition(TestBranchCore::even, TestBranchCore::branch5);
    assertEquals(Sets.newHashSet(branch5(z(4))), Sets.newHashSet(brl.apply(z(4))));
    assertEquals(Collections.emptySet(), Sets.newHashSet(brl.apply(z(5))));
  }

  @Test
  public void test_sfilter() {
    final Function<IExpr, Iterator<IExpr>> brl =
        BranchCore.sfilter(TestBranchCore::even, TestBranchCore::oneToN);
    assertEquals(setOf(0, 2, 4, 6, 8), Sets.newHashSet(brl.apply(z(10))));
  }

  @Test
  public void test_notempty() {
    final Function<IExpr, Iterator<IExpr>> identIfEven = x -> even(x) ? Iterators.singletonIterator(x)
        : Collections.<IExpr>emptyIterator();
    final Function<IExpr, Iterator<IExpr>> brl = BranchCore.notempty(identIfEven);
    assertEquals(setOf(4), Sets.newHashSet(brl.apply(z(4))));
    assertEquals(setOf(5), Sets.newHashSet(brl.apply(z(5))));
  }

  @Test
  public void test_chain() {
    assertEquals(listOf(2), Lists.newArrayList(BranchCore.chain().apply(z(2))));
    assertEquals(listOf(4), Lists.newArrayList(BranchCore.chain(inc, inc).apply(z(2))));
    assertEquals(listOf(4),
        Lists.newArrayList(BranchCore.chain(TestBranchCore::branch5, inc).apply(z(4))));
    assertEquals(setOf(5, 7),
        Sets.newHashSet(BranchCore.chain(TestBranchCore::branch5, inc).apply(z(5))));
    assertEquals(listOf(7),
        Lists.newArrayList(BranchCore.chain(inc, TestBranchCore::branch5).apply(z(5))));
  }

  @Test
  public void test_onaction() {
    final List<IExpr> l = new ArrayList<IExpr>();
    final java.util.function.BiConsumer<IExpr, IExpr> record = (input, output) -> {
      l.add(input);
      l.add(output);
    };
    Lists.newArrayList(BranchCore.onaction(inc, record).apply(z(2)));
    assertEquals(listOf(2, 3), l);

    Lists.newArrayList(BranchCore.onaction(BranchCore.identity(), record).apply(z(2)));
    assertEquals(listOf(2, 3), l);
  }

  @Test
  public void test_yieldify() {
    final Function<IExpr, Iterator<IExpr>> yinc = BranchCore.yieldify(x -> x.plus(F.C1));
    assertEquals(listOf(4), Lists.newArrayList(yinc.apply(z(3))));
  }

  @Test
  public void test_do_one() {
    final Function<IExpr, Iterator<IExpr>> bad = x -> {
      throw new IllegalArgumentException();
    };
    assertEquals(listOf(4), Lists.newArrayList(BranchCore.doOne(inc).apply(z(3))));
    assertEquals(listOf(4), Lists.newArrayList(BranchCore.doOne(inc, bad).apply(z(3))));
    assertEquals(listOf(4), Lists.newArrayList(BranchCore.doOne(inc, posdec).apply(z(3))));
  }

  @Test
  public void test_results_are_lazy() {
    final int[] calls = new int[1];
    final Function<IExpr, Iterator<IExpr>> rule = x -> {
      calls[0]++;
      return x.isZero() ? Iterators.singletonIterator(F.C1) : Iterators.forArray(F.C1, F.C2);
    };
    final Iterator<IExpr> results =
        BranchCore.multiplex(rule, x -> {
          throw new IllegalStateException("the second rule must not run");
        }).apply(F.C0);
    assertEquals(F.C1, results.next());
    assertEquals(1, calls[0]);
  }
}
