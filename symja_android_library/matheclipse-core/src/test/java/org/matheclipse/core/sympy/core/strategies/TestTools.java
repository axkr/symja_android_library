package org.matheclipse.core.sympy.core.strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Ported from <code>sympy/strategies/tests/test_tools.py</code>.
 */
public class TestTools {

  private static IAST basic(IExpr... args) {
    return F.ast(args, F.f);
  }

  @Test
  public void test_subs() {
    final Map<IExpr, IExpr> mapping =
        Map.of(F.a, F.d, F.d, F.a, F.unaryAST1(F.f, F.e), F.unaryAST1(F.f, F.g));
    final IExpr expr = basic(F.a, basic(F.b, F.c), basic(F.d, basic(F.e)));
    final IExpr result = basic(F.d, basic(F.b, F.c), basic(F.a, basic(F.g)));
    assertEquals(result, Tools.subs(mapping).apply(expr));
  }

  @Test
  public void test_subs_empty() {
    final IExpr expr = basic(F.C1, F.C2);
    assertEquals(expr, Tools.subs(Map.of()).apply(expr));
  }

  @Test
  public void test_typed() {
    final Function<IExpr, IExpr> rmzeros = Rl.rmId(x -> x.isZero());
    final Function<IExpr, IExpr> rmones = Rl.rmId(x -> x.isOne());
    final Function<IExpr, IExpr> removeSomething = Tools.typed(Map.of(F.f, rmzeros, F.g, rmones));

    assertEquals(F.unaryAST1(F.f, F.C1), removeSomething.apply(F.binaryAST2(F.f, F.C0, F.C1)));
    assertEquals(F.unaryAST1(F.g, F.C0), removeSomething.apply(F.binaryAST2(F.g, F.C0, F.C1)));
  }

  @Test
  public void test_canon() {
    // flatten and remove zeros anywhere in the tree, until nothing is left to do
    final Function<IExpr, IExpr> rmzeros = Rl.rmId(x -> x.isZero());
    final Function<IExpr, IExpr> canon = Tools.canon(rmzeros, Rl::flatten, Rl::unpack);
    final IExpr expr = basic(F.C0, F.a, basic(F.C0, basic(F.b, F.C0), F.g), basic(basic(F.C0)));
    assertEquals(basic(F.a, F.b, F.g), canon.apply(expr));
  }
}
