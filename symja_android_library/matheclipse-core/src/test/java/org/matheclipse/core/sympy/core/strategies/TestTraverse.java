package org.matheclipse.core.sympy.core.strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.core.traversal.SmallStack;

/**
 * Ported from <code>sympy/strategies/tests/test_traverse.py</code>.
 */
public class TestTraverse {
  private static final Function<IExpr, IExpr> zeroSymbols = x -> x.isSymbol() ? F.C0 : x;

  /** <code>Basic</code> becomes <code>Basic2</code> if the first argument is no integer. */
  private static final Function<IExpr, IExpr> rl =
      x -> x.isAST() && x.argSize() > 0 && !x.first().isInteger() ? ((IAST) x).setAtCopy(0, F.g) : x;

  private static IAST basic(IExpr... args) {
    return F.ast(args, F.f);
  }

  private static IExpr addOneIfCan(IExpr x) {
    return x.isNumber() ? x.plus(F.C1) : x;
  }

  @Test
  public void test_sall() {
    assertEquals(basic(F.C0, F.C0, basic(F.x, F.z)),
        Traverse.sall(zeroSymbols).apply(basic(F.x, F.y, basic(F.x, F.z))));
  }

  private static void globalTraversal(Function<Function<IExpr, IExpr>, Function<IExpr, IExpr>> trav) {
    assertEquals(basic(F.C0, F.C0, basic(F.C0, F.C0)),
        trav.apply(zeroSymbols).apply(basic(F.x, F.y, basic(F.x, F.z))));
  }

  private static void stopOnNonBasics(Function<Function<IExpr, IExpr>, Function<IExpr, IExpr>> trav) {
    final IExpr expr = basic(F.C1, F.$str("a"), basic(F.C2, F.$str("b")));
    final IExpr expected = basic(F.C2, F.$str("a"), basic(F.C3, F.$str("b")));
    assertEquals(expected, trav.apply(TestTraverse::addOneIfCan).apply(expr));
  }

  @Test
  public void test_bottom_up() {
    globalTraversal(Traverse::bottomUp);
    stopOnNonBasics(Traverse::bottomUp);
  }

  @Test
  public void test_top_down() {
    globalTraversal(Traverse::topDown);
    stopOnNonBasics(Traverse::topDown);
  }

  @Test
  public void test_top_down_once() {
    final IExpr one = F.num(1.0);
    final IExpr two = F.num(2.0);
    final IExpr inner = basic(F.C3, F.C4);
    assertEquals(F.ternaryAST3(F.g, one, two, inner),
        Traverse.topDownOnce(rl).apply(basic(one, two, inner)));
  }

  @Test
  public void test_bottom_up_once() {
    final IExpr three = F.num(3.0);
    final IExpr four = F.num(4.0);
    assertEquals(basic(F.C1, F.C2, F.binaryAST2(F.g, three, four)),
        Traverse.bottomUpOnce(rl).apply(basic(F.C1, F.C2, basic(three, four))));
  }

  @Test
  public void test_expr_fns() {
    final IExpr expr = F.eval(F.Plus(F.x, F.Power(F.y, F.C3)));
    final IExpr e = Traverse.bottomUp(v -> F.eval(F.Plus(v, F.C1)), Util.EXPR_FNS).apply(expr);
    final IExpr b =
        Traverse.bottomUp(v -> F.binaryAST2(S.Plus, v, F.C1), Util.BASIC_FNS).apply(expr);
    assertEquals(e, Rl.rebuild(b));
  }

  @Test
  public void test_rule_sees_each_node_once() {
    final List<IExpr> seen = new ArrayList<IExpr>();
    final Function<IExpr, IExpr> record = x -> {
      seen.add(x);
      return F.NIL;
    };
    final IExpr expr = basic(F.a, basic(F.b));
    assertSame(expr, Traverse.topDown(record).apply(expr));
    assertEquals(List.of(expr, F.a, expr.second(), F.b), seen);

    seen.clear();
    assertSame(expr, Traverse.bottomUp(record).apply(expr));
    assertEquals(List.of(F.a, F.b, expr.second(), expr), seen);
  }

  @Test
  public void test_top_down_descends_into_the_replacement() {
    final Function<IExpr, IExpr> unfold =
        x -> x.equals(F.a) ? basic(F.b) : x.equals(F.b) ? F.c : x;
    assertEquals(basic(basic(F.c)), Traverse.topDown(unfold).apply(basic(F.a)));
  }

  @Test
  public void test_deep_trees_on_a_small_stack() throws InterruptedException {
    final int depth = 100_000;
    IExpr expr = F.a;
    for (int i = 0; i < depth; i++) {
      expr = F.binaryAST2(F.f, expr, F.C1);
    }
    final IExpr tree = expr;
    SmallStack.run(() -> {
      IExpr result = Traverse.topDown(x -> x.equals(F.a) ? F.b : x).apply(tree);
      for (int i = 0; i < depth; i++) {
        result = result.first();
      }
      assertSame(F.b, result);
    });
  }

  @Test
  public void test_sall_sees_a_packed_matrix_as_unchanged() {
    final IExpr matrix = new ASTRealMatrix(new double[][] {{1.0, 2.0}, {3.0, 4.0}}, false);
    assertSame(matrix, Traverse.sall(Core.identity()).apply(matrix));
  }
}
