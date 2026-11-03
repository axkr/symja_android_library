package org.matheclipse.core.sympy.core.strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Ported from <code>sympy/strategies/tests/test_rl.py</code>.
 */
public class TestRl {

  private static IAST basic(IExpr... args) {
    return F.ast(args, F.f);
  }

  private static IExpr key(IExpr x) {
    if (x.isNumber()) {
      return F.C1;
    }
    return x.isTimes() && x.first().isNumber() ? F.eval(((IAST) x).rest().oneIdentity1()) : x;
  }

  private static IExpr count(IExpr x) {
    if (x.isNumber()) {
      return x;
    }
    return x.isTimes() && x.first().isNumber() ? x.first() : F.C1;
  }

  @Test
  public void test_rm_id() {
    final Function<IExpr, IExpr> rmzeros = Rl.rmId(x -> x.isZero());
    assertEquals(basic(F.C1), rmzeros.apply(basic(F.C0, F.C1)));
    assertEquals(basic(F.C0), rmzeros.apply(basic(F.C0, F.C0)));
    final IExpr same = basic(F.C2, F.C1);
    assertSame(same, rmzeros.apply(same));
  }

  @Test
  public void test_rm_id_evaluates_with_expr_fns() {
    final IExpr expr = F.Plus(F.a, F.C0);
    assertEquals(F.a, Rl.rmId(x -> x.isZero(), Util.EXPR_FNS).apply(expr));
  }

  @Test
  public void test_glom() {
    final Function<IExpr, IExpr> rl =
        Rl.glom(TestRl::key, TestRl::count, (cnt, arg) -> F.eval(F.Times(cnt, arg)));
    final IExpr result =
        rl.apply(F.Plus(F.x, F.Times(F.CN1, F.x), F.Times(F.C3, F.x), F.C2, F.C3));
    assertEquals(F.eval(F.Plus(F.Times(F.C3, F.x), F.C5)), F.eval(result));
  }

  @Test
  public void test_glom_without_effect() {
    final Function<IExpr, IExpr> rl =
        Rl.glom(TestRl::key, TestRl::count, (cnt, arg) -> F.eval(F.Times(cnt, arg)));
    final IExpr expr = F.Plus(F.x, F.y);
    assertSame(expr, rl.apply(expr));
  }

  @Test
  public void test_flatten() {
    assertEquals(basic(F.C1, F.C2, F.C3, F.C4),
        Rl.flatten(basic(F.C1, F.C2, basic(F.C3, F.C4))));
    final IExpr other = basic(F.C1, F.unaryAST1(F.g, F.C2));
    assertSame(other, Rl.flatten(other));
  }

  @Test
  public void test_unpack() {
    assertEquals(F.C2, Rl.unpack(basic(F.C2)));
    assertEquals(basic(F.C2, F.C3), Rl.unpack(basic(F.C2, F.C3)));
  }

  @Test
  public void test_sort() {
    assertEquals(basic(F.C1, F.C2, F.C3),
        Rl.sort(x -> x.toString()).apply(basic(F.C3, F.C1, F.C2)));
    final IExpr sorted = basic(F.C1, F.C2);
    assertSame(sorted, Rl.sort(x -> x.toString()).apply(sorted));
  }

  @Test
  public void test_sort_packed() {
    final IExpr sorted = new ASTRealVector(new double[] {1.0, 2.0}, false);
    assertSame(sorted, Rl.sort(x -> x.toString()).apply(sorted));

    final IExpr unsorted = new ASTRealVector(new double[] {2.0, 1.0}, false);
    assertEquals(sorted, Rl.sort(x -> x.toString()).apply(unsorted));
  }

  @Test
  public void test_distribute() {
    final Function<IExpr, IExpr> distributeT12 = Rl.distribute(F.f, F.g);
    assertEquals(
        F.binaryAST2(F.g, basic(F.C1, F.C2, F.C3, F.C5), basic(F.C1, F.C2, F.C4, F.C5)),
        distributeT12.apply(basic(F.C1, F.C2, F.binaryAST2(F.g, F.C3, F.C4), F.C5)));
    final IExpr none = basic(F.C1, F.C2, F.C3);
    assertSame(none, distributeT12.apply(none));
  }

  @Test
  public void test_distribute_add_mul() {
    final IExpr expr = F.Times(F.C2, F.Plus(F.x, F.y));
    final IExpr expected = F.Plus(F.Times(F.C2, F.x), F.Times(F.C2, F.y));
    assertEquals(expected, Rl.distribute(S.Times, S.Plus).apply(expr));
  }

  @Test
  public void test_subs() {
    final Function<IExpr, IExpr> rl = Rl.subs(F.C1, F.C2);
    assertEquals(F.C2, rl.apply(F.C1));
    assertEquals(F.C3, rl.apply(F.C3));
  }

  @Test
  public void test_rebuild() {
    assertEquals(F.C3, Rl.rebuild(F.Plus(F.C1, F.C2)));
  }
}
