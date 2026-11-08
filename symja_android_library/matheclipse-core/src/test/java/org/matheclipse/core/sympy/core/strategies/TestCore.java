package org.matheclipse.core.sympy.core.strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Ported from <code>sympy/strategies/tests/test_core.py</code>, plus the differences to sympy.
 */
public class TestCore {
  private static final Function<IExpr, IExpr> posdec = x -> x.isPositive() ? x.plus(F.CN1) : x;
  private static final Function<IExpr, IExpr> inc = x -> x.plus(F.C1);
  private static final Function<IExpr, IExpr> dec = x -> x.plus(F.CN1);

  private static IExpr z(int value) {
    return F.ZZ(value);
  }

  private static boolean isEven(IExpr x) {
    return ((IInteger) x).isEven();
  }

  @Test
  public void test_null_safe() {
    final Function<IExpr, IExpr> rl = expr -> expr.equals(F.C1) ? F.C2 : F.NIL;
    final Function<IExpr, IExpr> safe = Core.nullSafe(rl);
    assertEquals(rl.apply(F.C1), safe.apply(F.C1));
    assertTrue(rl.apply(F.C3).isNIL());
    assertEquals(F.C3, safe.apply(F.C3));
  }

  @Test
  public void test_exhaust() {
    final Function<IExpr, IExpr> sink = Core.exhaust(posdec);
    assertEquals(F.C0, sink.apply(z(5)));
    assertEquals(F.C0, sink.apply(z(10)));
  }

  @Test
  public void test_exhaust_stops_on_nil() {
    final Function<IExpr, IExpr> rule = x -> x.isPositive() ? x.plus(F.CN1) : F.NIL;
    assertEquals(F.C0, Core.exhaust(rule).apply(z(5)));
  }

  @Test
  public void test_exhaust_of_a_cycle_hits_the_iteration_limit() {
    final Function<IExpr, IExpr> cycle = x -> x.equals(F.a) ? F.b : F.a;
    assertThrows(IterationLimitExceeded.class, () -> Core.exhaust(cycle).apply(F.a));
  }

  @Test
  public void test_exhaust_stops_on_an_equal_copy() {
    // the rule fires, but the result is a new object of the same value
    final Function<IExpr, IExpr> copy = x -> ((org.matheclipse.core.interfaces.IAST) x).copy();
    assertEquals(F.List(F.a), Core.exhaust(copy).apply(F.List(F.a)));
  }

  @Test
  public void test_memoize() {
    final Function<IExpr, IExpr> rl = Core.memoize(posdec);
    assertEquals(posdec.apply(z(5)), rl.apply(z(5)));
    assertEquals(posdec.apply(z(5)), rl.apply(z(5)));
    assertEquals(posdec.apply(z(-2)), rl.apply(z(-2)));
  }

  @Test
  public void test_memoize_calls_the_rule_once() {
    final AtomicInteger calls = new AtomicInteger();
    final Function<IExpr, IExpr> rl = Core.memoize(x -> {
      calls.incrementAndGet();
      return F.NIL;
    });
    assertEquals(F.List(F.a), rl.apply(F.List(F.a)));
    // another object of the same value
    assertEquals(F.List(F.a), rl.apply(F.List(F.a)));
    assertEquals(1, calls.get());
  }

  @Test
  public void test_memoize_bound() {
    final AtomicInteger calls = new AtomicInteger();
    final Function<IExpr, IExpr> rl = Core.memoize(x -> {
      calls.incrementAndGet();
      return x;
    }, 1);
    rl.apply(z(1));
    rl.apply(z(2));
    rl.apply(z(2));
    assertEquals(2, calls.get());
    rl.apply(z(1));
    assertEquals(3, calls.get());
  }

  @Test
  public void test_condition() {
    final Function<IExpr, IExpr> rl = Core.condition(TestCore::isEven, posdec);
    assertEquals(z(5), rl.apply(z(5)));
    assertEquals(z(3), rl.apply(z(4)));
  }

  @Test
  public void test_chain() {
    final Function<IExpr, IExpr> rl = Core.chain(posdec, posdec);
    assertEquals(z(3), rl.apply(z(5)));
    assertEquals(F.C0, rl.apply(F.C1));
  }

  @Test
  public void test_tryit() {
    final Function<IExpr, IExpr> rl = x -> {
      throw new ArithmeticException();
    };
    assertEquals(F.C1, Core.tryit(rl, ArithmeticException.class).apply(F.C1));
    assertThrows(ArithmeticException.class,
        () -> Core.tryit(rl, IllegalStateException.class).apply(F.C1));
  }

  @Test
  public void test_do_one() {
    final Function<IExpr, IExpr> rl = Core.doOne(posdec, posdec);
    assertEquals(z(4), rl.apply(z(5)));

    final Function<IExpr, IExpr> rl1 = x -> x.equals(F.C1) ? F.C2 : x;
    final Function<IExpr, IExpr> rl2 = x -> x.equals(F.C2) ? F.C3 : x;
    final Function<IExpr, IExpr> rule = Core.doOne(rl1, rl2);
    assertEquals(F.C2, rule.apply(F.C1));
    assertEquals(F.C3, rule.apply(rule.apply(F.C1)));
  }

  @Test
  public void test_do_one_skips_nil() {
    final Function<IExpr, IExpr> none = x -> F.NIL;
    assertEquals(z(4), Core.doOne(none, posdec).apply(z(5)));
    assertSame(F.a, Core.doOne(none, none).apply(F.a));
  }

  @Test
  public void test_debug() {
    final StringBuilder file = new StringBuilder();
    Core.debug(posdec, "posdec", file).apply(z(5));
    final String log = file.toString();
    assertTrue(log.contains("posdec"));
    assertTrue(log.contains("5"));
    assertTrue(log.contains("4"));
  }

  @Test
  public void test_debug_is_silent_without_effect() {
    final StringBuilder file = new StringBuilder();
    Core.debug(posdec, "posdec", file).apply(z(0));
    assertEquals("", file.toString());
  }

  @Test
  public void test_switch() {
    final Function<IExpr, IExpr> key = x -> F.eval(F.Mod(x, F.C3));
    final Function<IExpr, IExpr> rl = Core.switchRule(key, Map.of(F.C0, inc, F.C1, dec));
    assertEquals(z(4), rl.apply(z(3)));
    assertEquals(z(3), rl.apply(z(4)));
    assertEquals(z(5), rl.apply(z(5)));
  }

  @Test
  public void test_minimize() {
    final Function<IExpr, IExpr> key = x -> x.negate();
    assertEquals(z(3), Core.minimize(inc, dec).apply(z(4)));
    assertEquals(z(5), Core.minimizeBy(key, inc, dec).apply(z(4)));
  }

  @Test
  public void test_minimize_scores_each_candidate_once() {
    final AtomicInteger calls = new AtomicInteger();
    final Function<IExpr, Long> objective = x -> {
      calls.incrementAndGet();
      return x.leafCount();
    };
    Core.minimizeBy(objective, inc, dec, Core.identity()).apply(z(4));
    assertEquals(3, calls.get());
  }

  @Test
  public void test_minimize_tie_goes_to_the_first_rule() {
    final Function<IExpr, Long> objective = x -> 1L;
    assertEquals(z(5), Core.minimizeBy(objective, inc, dec).apply(z(4)));
  }

  @Test
  public void test_minimize_needs_a_rule() {
    assertThrows(IllegalArgumentException.class,
        () -> Core.minimize(List.<Function<IExpr, IExpr>>of().toArray(new Function[0])));
  }
}
