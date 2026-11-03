package org.matheclipse.core.sympy.core.strategies.branch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Ported from <code>sympy/strategies/branch/tests/test_traverse.py</code>.
 */
public class TestBranchTraverse {
  private static final Function<IExpr, Iterator<IExpr>> inc =
      x -> x.isInteger() ? Iterators.singletonIterator(x.plus(F.C1))
          : Collections.<IExpr>emptyIterator();

  private static IAST basic(IExpr... args) {
    return F.ast(args, F.f);
  }

  @Test
  public void test_top_down_easy() {
    final IExpr expr = basic(F.C1, F.C2);
    final IExpr expected = basic(F.C2, F.C3);
    assertEquals(Set.of(expected), Sets.newHashSet(BranchTraverse.topDown(inc).apply(expr)));
  }

  @Test
  public void test_top_down_big_tree() {
    final IExpr expr = basic(F.C1, basic(F.C2), basic(F.C3, basic(F.C4), F.C5));
    final IExpr expected = basic(F.C2, basic(F.C3), basic(F.C4, basic(F.C5), F.ZZ(6)));
    assertEquals(Set.of(expected), Sets.newHashSet(BranchTraverse.topDown(inc).apply(expr)));
  }

  @Test
  public void test_top_down_harder_function() {
    final Function<IExpr, Iterator<IExpr>> split5 =
        x -> x.equals(F.C5) ? Iterators.forArray(F.C4, F.ZZ(6)) : Collections.<IExpr>emptyIterator();
    final IExpr expr = basic(basic(F.C5, F.ZZ(6)), F.C1);
    final Set<IExpr> expected =
        Set.of(basic(basic(F.C4, F.ZZ(6)), F.C1), basic(basic(F.ZZ(6), F.ZZ(6)), F.C1));
    assertEquals(expected, Sets.newHashSet(BranchTraverse.topDown(split5).apply(expr)));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void test_sall() {
    assertEquals(List.of(basic(F.C2, F.C3)),
        Lists.newArrayList(BranchTraverse.sall(inc).apply(basic(F.C1, F.C2))));

    final IExpr expr = basic(F.C1, F.C2, basic(F.C3, F.C4));
    final IExpr expected = basic(F.C2, F.C3, basic(F.C3, F.C4));
    final Function<IExpr, Iterator<IExpr>> brl =
        BranchTraverse.sall(BranchCore.doOne(inc, BranchCore.identity()));
    assertEquals(List.of(expected), Lists.newArrayList(brl.apply(expr)));
  }

  @Test
  public void test_sall_has_no_limit_on_the_number_of_combinations() {
    final Function<IExpr, Iterator<IExpr>> both = x -> Iterators.forArray(x, x.plus(F.C1));
    final IExpr[] args = new IExpr[40];
    java.util.Arrays.fill(args, F.C1);
    final Iterator<IExpr> results = BranchTraverse.sall(both).apply(basic(args));
    assertEquals(basic(args), results.next());
    final IExpr[] last = args.clone();
    last[39] = F.C2;
    assertEquals(basic(last), results.next());
  }

  @Test
  public void test_sall_without_a_choice_for_an_argument() {
    final Function<IExpr, Iterator<IExpr>> onlyOne =
        x -> x.isOne() ? Iterators.singletonIterator(x) : Collections.<IExpr>emptyIterator();
    assertEquals(List.of(), Lists.newArrayList(BranchTraverse.sall(onlyOne).apply(basic(F.C1, F.C2))));
  }

  @Test
  public void test_sall_order_of_combinations() {
    final Function<IExpr, Iterator<IExpr>> both = x -> Iterators.forArray(x, x.plus(F.C1));
    assertEquals(
        List.of(basic(F.C1, F.C3), basic(F.C1, F.C4), basic(F.C2, F.C3), basic(F.C2, F.C4)),
        Lists.newArrayList(BranchTraverse.sall(both).apply(basic(F.C1, F.C3))));
  }
}
