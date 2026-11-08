package org.matheclipse.core.sympy.core.traversal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Ported from <code>sympy/core/tests/test_traversal.py</code>, plus the behaviour which sympy
 * doesn't have: iterative walks, and the {@link F#NIL} contract.
 */
public class TestTraversal {
  /** Deeper than any recursive walk gets on {@link SmallStack}. */
  private static final int DEEP = 100_000;

  private static final Comparator<IExpr> NATURAL = Comparator.naturalOrder();

  private static final ISymbol w = F.w;
  private static final ISymbol x = F.x;
  private static final ISymbol y = F.y;
  private static final ISymbol z = F.z;

  private static final IAST b1 = F.ast(new IExpr[0], F.f);
  private static final IAST b2 = F.ast(new IExpr[] {b1}, F.f);
  private static final IAST b3 = F.ast(new IExpr[] {b2}, F.f);
  private static final IAST b21 = F.ast(new IExpr[] {b2, b1}, F.f);

  private static List<IExpr> list(Iterable<IExpr> iterable) {
    final List<IExpr> result = new ArrayList<IExpr>();
    iterable.forEach(result::add);
    return result;
  }

  private static List<IExpr> listOf(IExpr... exprs) {
    return new ArrayList<IExpr>(List.of(exprs));
  }

  private static long count(Iterable<IExpr> iterable) {
    long result = 0;
    for (IExpr expr : iterable) {
      if (expr != null) {
        result++;
      }
    }
    return result;
  }

  /** <code>f(f(...f(x)...))</code> */
  private static IExpr unaryNest(int depth) {
    IExpr result = x;
    for (int i = 0; i < depth; i++) {
      result = F.unaryAST1(F.f, result);
    }
    return result;
  }

  /** <code>f(f(...f(x,1)...,1),1)</code> */
  private static IExpr binaryNest(int depth) {
    IExpr result = x;
    for (int i = 0; i < depth; i++) {
      result = F.binaryAST2(F.f, result, F.C1);
    }
    return result;
  }

  private static IExpr expand(IExpr expr) {
    return F.eval(F.Expand(expr));
  }

  @Test
  public void test_preorder_traversal() {
    final IAST expr = F.ast(new IExpr[] {b21, b3}, F.f);
    assertEquals(listOf(expr, b21, b2, b1, b1, b3, b2, b1), list(new PreorderTraversal(expr)));

    final IExpr nested = F.List(F.$str("abc"), F.List(F.$str("d"), F.$str("ef")));
    assertEquals(listOf(nested, F.$str("abc"), nested.second(), F.$str("d"), F.$str("ef")),
        list(new PreorderTraversal(nested)));

    final List<IExpr> result = new ArrayList<IExpr>();
    final PreorderTraversal pt = new PreorderTraversal(expr);
    for (IExpr i : pt) {
      result.add(i);
      if (i.equals(b2)) {
        pt.skip();
      }
    }
    assertEquals(listOf(expr, b21, b2, b1, b3, b2), result);

    // the canonical order of Symja has w*(x+y) before z, sympy's default_sort_key the other way
    final IExpr sum = F.eval(F.Plus(z, F.Times(w, F.Plus(x, y))));
    assertEquals(listOf(F.List(sum), sum, F.Times(w, F.Plus(x, y)), w, F.Plus(x, y), x, y, z),
        list(new PreorderTraversal(F.List(sum), NATURAL, false)));

    final IExpr product = F.eval(F.Times(F.Plus(x, y), z));
    assertEquals(listOf(product, F.Plus(x, y), x, y, z),
        list(new PreorderTraversal(product, NATURAL, false)));
  }

  @Test
  public void test_preorder_skip_on_last_node() {
    final PreorderTraversal pt = new PreorderTraversal(F.unaryAST1(F.f, x));
    pt.next();
    pt.skip();
    assertEquals(false, pt.hasNext());
  }

  @Test
  public void test_preorder_heads() {
    final IExpr expr = F.unaryAST1(F.unaryAST1(F.f, x), y);
    assertEquals(listOf(expr, expr.head(), F.f, x, y),
        list(new PreorderTraversal(expr, null, true)));
  }

  @Test
  public void test_use() {
    assertEquals(F.C0, Traversal.use(F.C0, TestTraversal::expand, 0));

    IExpr f = F.eval(F.Plus(F.Times(F.Power(F.Plus(x, y), 2), x), F.C1));
    final IExpr expanded = expand(f);

    assertEquals(expanded, Traversal.use(f, TestTraversal::expand, 0));
    assertEquals(expanded, F.eval(Traversal.use(f, TestTraversal::expand, 1)));
    assertEquals(F.eval(F.Plus(F.C1, F.Times(x, expand(F.Sqr(F.Plus(x, y)))))),
        F.eval(Traversal.use(f, TestTraversal::expand, 2)));
    assertSame(f, Traversal.use(f, TestTraversal::expand, 3));

    f = F.eval(F.Plus(F.Sqr(F.Plus(F.Sqr(x), F.C1)), F.CN1));
    final Function<IExpr, IExpr> factor =
        e -> F.eval(F.binaryAST2(S.Factor, e, F.Rule(S.GaussianIntegers, S.True)));
    assertEquals("x^2*(2+x^2)", Traversal.use(f, factor, 0).toString());
    assertEquals("-1+(I-x)^2*(I+x)^2", F.eval(Traversal.use(f, factor, 1)).toString());
    assertEquals("-1+(I-x)^2*(I+x)^2", F.eval(Traversal.use(f, factor, 2)).toString());
    // Factor returns a new object of the same value here
    assertEquals(f, F.eval(Traversal.use(f, factor, 3)));
  }

  @Test
  public void test_postorder_traversal() {
    final IExpr expr = F.eval(F.Plus(z, F.Times(w, F.Plus(x, y))));
    final IExpr product = F.Times(w, F.Plus(x, y));
    assertEquals(listOf(w, x, y, F.Plus(x, y), product, z, expr),
        list(new PostorderTraversal(expr, NATURAL, false)));

    final IExpr first = F.List(x, F.Less(x, F.C1));
    final IExpr second = F.List(F.Sqr(x), S.True);
    final IExpr piecewise = F.Piecewise(F.List(first, second));
    assertEquals(
        listOf(x, F.C1, x, F.Less(x, F.C1), first, S.True, F.C2, x, F.Sqr(x), second,
            piecewise.first(), piecewise),
        list(new PostorderTraversal(piecewise, NATURAL, false)));

    final IExpr integral = F.Integrate(F.Sqr(x), F.List(x, F.C0, F.C1));
    assertEquals(
        listOf(F.C2, x, F.Sqr(x), F.C0, F.C1, x, integral.second(), integral),
        list(new PostorderTraversal(integral, NATURAL, false)));

    final IExpr nested = F.List(F.$str("abc"), F.List(F.$str("d"), F.$str("ef")));
    assertEquals(listOf(F.$str("abc"), F.$str("d"), F.$str("ef"), nested.second(), nested),
        list(new PostorderTraversal(nested)));

    assertEquals(listOf(x), list(new PostorderTraversal(x)));
  }

  @Test
  public void test_iterargs() {
    final IExpr fx = F.unaryAST1(F.f, x);
    final IExpr integral = F.Integrate(fx, F.List(fx, F.C1));
    assertEquals(listOf(integral, F.C1), list(Traversal.iterFreeArgs(integral)));
    assertEquals(listOf(integral, fx, F.List(fx, F.C1), x, fx, F.C1, x),
        list(Traversal.iterArgs(integral)));
  }

  @Test
  public void test_iterargs_is_breadth_first() {
    final IExpr expr = F.binaryAST2(F.f, F.unaryAST1(F.g, x), y);
    assertEquals(listOf(expr, expr.first(), y, x), list(Traversal.iterArgs(expr)));
    assertEquals(listOf(expr, F.f, expr.first(), y, F.g, x),
        list(Traversal.iterArgs(expr, true)));
  }

  @Test
  public void test_iterfreeargs_binders() {
    // the body depends on x, the limits and the free symbol y don't
    final IExpr integral = F.Integrate(F.Times(x, y), F.List(x, z, F.C2));
    assertEquals(listOf(integral, y, z, F.C2), list(Traversal.iterFreeArgs(integral)));

    final IExpr bareVariable = F.Integrate(F.Times(x, y), x);
    assertEquals(listOf(bareVariable, y), list(Traversal.iterFreeArgs(bareVariable)));

    // the second argument of Sum is an iterator only inside a list
    final IExpr repeat = F.Table(x, y);
    assertEquals(listOf(repeat, x, y), list(Traversal.iterFreeArgs(repeat)));

    final IExpr module = F.Module(F.List(x, F.Set(y, z)), F.Plus(x, y, w));
    // breadth first: w is an argument of the body, z of the assignment inside of the list
    assertEquals(listOf(module, w, z), list(Traversal.iterFreeArgs(module)));

    final IExpr function = F.Function(F.List(x), F.Plus(x, y));
    assertEquals(listOf(function, y), list(Traversal.iterFreeArgs(function)));

    // outside of a binder it's iterArgs
    final IExpr plain = F.binaryAST2(F.f, x, F.Sum(x, F.List(x, F.C1, y)));
    assertEquals(listOf(plain, x, plain.second(), F.C1, y), list(Traversal.iterFreeArgs(plain)));
  }

  @Test
  public void test_iterfreeargs_repeat_count_binds_nothing() {
    for (IExpr expr : List.of(F.Table(F.Plus(x, F.C3), F.List(F.C3)),
        F.Table(F.Plus(x, y), F.List(y)), F.Do(F.Plus(x, y), F.List(y)))) {
      assertEquals(list(Traversal.iterArgs(expr)), list(Traversal.iterFreeArgs(expr)));
    }
  }

  @Test
  public void test_walk() {
    final IExpr inner = F.Min(F.C1, z);
    final IExpr max = F.Max(y, inner);
    final IExpr expr = F.Min(x, max);
    assertEquals(listOf(expr), list(Traversal.walk(expr, S.Min)));
    assertEquals(listOf(expr, max, inner), list(Traversal.walk(expr, S.Min, S.Max)));
    assertEquals(listOf(), list(Traversal.walk(expr, S.Max)));
  }

  @Test
  public void test_bottom_up_order() {
    final List<IExpr> seen = new ArrayList<IExpr>();
    final IExpr expr = F.binaryAST2(F.f, F.unaryAST1(F.g, x), y);
    Traversal.bottomUp(expr, e -> {
      seen.add(e);
      return F.NIL;
    });
    // atoms are not visited by default
    assertEquals(listOf(expr.first(), expr), seen);
  }

  @Test
  public void test_bottom_up_rewrites_with_new_arguments() {
    final IExpr expr = F.binaryAST2(F.f, F.unaryAST1(F.g, x), y);
    final IExpr result = Traversal.bottomUp(expr,
        e -> e.isAST(F.g, 2) ? F.C1 : e.isAST(F.f, 3) ? F.List(e.first(), e.second()) : F.NIL);
    assertEquals(F.List(F.C1, y), result);
  }

  @Test
  public void test_bottom_up_atoms_and_nonbasic() {
    final IExpr expr = F.ternaryAST3(F.f, x, F.$str("a"), F.ast(new IExpr[0], F.g));
    final Function<IExpr, IExpr> wrap = e -> e.isAST(F.f) ? F.NIL : F.unaryAST1(F.h, e);

    assertSame(expr, Traversal.bottomUp(expr, wrap));

    IExpr result = Traversal.bottomUp(expr, wrap, true, false);
    assertEquals("f(h(x),a,h(g()))", result.toString());

    result = Traversal.bottomUp(expr, wrap, false, true);
    assertEquals("f(x,h(a),g())", result.toString());

    result = Traversal.bottomUp(expr, wrap, true, true);
    assertEquals("f(h(x),h(a),h(g()))", result.toString());
  }

  @Test
  public void test_bottom_up_rule_returning_its_argument_copies_nothing() {
    final IExpr expr = binaryNest(20);
    assertSame(expr, Traversal.bottomUp(expr, e -> e));
    assertSame(expr, Traversal.bottomUp(expr, e -> F.NIL));
    assertTrue(Traversal.bottomUpNIL(expr, e -> e).isNIL());
    assertTrue(Traversal.bottomUpNIL(x, e -> e).isNIL());
  }

  @Test
  public void test_bottom_up_shares_unchanged_subtrees() {
    final IExpr shared = F.unaryAST1(F.g, x);
    final IExpr expr = F.binaryAST2(F.f, shared, F.unaryAST1(F.h, y));
    final IExpr result = Traversal.bottomUp(expr, e -> e.isAST(F.h, 2) ? F.C0 : F.NIL);
    assertSame(shared, result.first());
    assertEquals(F.C0, result.second());
  }

  @Test
  public void test_association_values_are_arguments() {
    final IExpr expr = F.assoc(F.List(F.Rule(x, F.unaryAST1(F.f, y))));
    assertEquals(listOf(expr, F.unaryAST1(F.f, y), y), list(new PreorderTraversal(expr)));
    assertEquals(listOf(y, F.unaryAST1(F.f, y), expr), list(new PostorderTraversal(expr)));

    final IExpr result = Traversal.bottomUp(expr, e -> e.isAST(F.f, 2) ? F.C1 : F.NIL);
    assertEquals(F.assoc(F.List(F.Rule(x, F.C1))), result);
  }

  @Test
  public void test_association_rule_stays_the_value_of_its_key() {
    final IExpr rule = F.Rule(y, F.C3);
    final IExpr expr = F.assoc(F.List(F.Rule(x, F.C1), F.Rule(y, F.C2)));
    final Function<IExpr, IExpr> toRule = e -> e.isOne() ? rule : F.NIL;

    final IExpr bottomUp = Traversal.bottomUp(expr, toRule, true, false);
    assertEquals(rule, ((IAST) bottomUp).get(1));
    assertEquals(F.C2, ((IAST) bottomUp).get(2));
    assertEquals(2, bottomUp.argSize());
    assertEquals(bottomUp, Traversal.use(expr, toRule, 1));
  }

  @Test
  public void test_no_op_on_packed_matrix_changes_nothing() {
    // get() of a packed matrix makes a new row object in each call
    final IExpr matrix = new ASTRealMatrix(new double[][] {{1.0, 2.0}, {3.0, 4.0}}, false);
    assertTrue(Traversal.bottomUpNIL(matrix, e -> F.NIL).isNIL());
    assertSame(matrix, Traversal.bottomUp(matrix, e -> e));
    assertSame(matrix, Traversal.bottomUp(matrix, e -> e, true, true));

    final IExpr nested = F.List(matrix, x);
    assertTrue(Traversal.bottomUpNIL(nested, e -> F.NIL).isNIL());
    assertSame(nested, Traversal.transform(nested, e -> e, e -> e, Traversal.BASIC_FNS)
        .orElse(nested));
  }

  @Test
  public void test_rewriting_a_packed_matrix_unpacks_it() {
    final IExpr matrix = new ASTRealMatrix(new double[][] {{1.0, 2.0}, {3.0, 4.0}}, false);
    final IExpr one = F.num(1.0);
    final IExpr two = F.num(2.0);

    // the first write keeps the row packed, the second one needs to unpack it
    final IExpr result = Traversal.bottomUp(matrix,
        e -> e.equals(one) ? F.num(5.0) : e.equals(two) ? x : F.NIL, true, false);
    assertEquals("{{5.0,x},{3.0,4.0}}", result.toString().replace("\n", ""));
    // the original is untouched
    assertEquals(new ASTRealMatrix(new double[][] {{1.0, 2.0}, {3.0, 4.0}}, false), matrix);
  }

  @Test
  public void test_transform_visits_what_pre_returned() {
    final Function<IExpr, IExpr> pre = e -> e.isAST(F.f, 2) ? F.unaryAST1(F.g, e.first()) : F.NIL;
    final IExpr result =
        Traversal.transform(F.unaryAST1(F.f, F.unaryAST1(F.f, x)), pre, null, Traversal.BASIC_FNS);
    assertEquals("g(g(x))", result.toString());
    assertTrue(Traversal.transform(x, pre, null, Traversal.BASIC_FNS).isNIL());
  }

  @Test
  public void test_deep_iterators() throws InterruptedException {
    final IExpr unary = unaryNest(DEEP);
    final IExpr binary = binaryNest(DEEP);
    SmallStack.run(() -> {
      assertEquals(DEEP + 1L, count(new PreorderTraversal(unary)));
      assertEquals(DEEP + 1L, count(new PostorderTraversal(unary)));
      assertEquals(DEEP + 1L, count(Traversal.iterArgs(unary)));
      assertEquals(2L * DEEP + 1L, count(new PreorderTraversal(binary)));
      assertEquals(2L * DEEP + 1L, count(new PostorderTraversal(binary)));
      assertEquals(2L * DEEP + 1L, count(Traversal.iterArgs(binary)));
      assertEquals((long) DEEP, count(Traversal.walk(binary, F.f)));
    });
  }

  @Test
  public void test_deep_bottom_up() throws InterruptedException {
    final IExpr binary = binaryNest(DEEP);
    SmallStack.run(() -> {
      assertSame(binary, Traversal.bottomUp(binary, e -> e));

      final IExpr result =
          Traversal.bottomUp(binary, e -> e.equals(x) ? y : F.NIL, true, false);
      assertEquals(2L * DEEP + 1L, count(new PreorderTraversal(result)));
      IExpr leaf = result;
      while (leaf.isAST()) {
        leaf = leaf.first();
      }
      assertSame(y, leaf);
    });
  }

  @Test
  public void test_wide_expression_uses_one_frame() {
    // below Config.MAX_AST_SIZE, which other tests in this JVM set
    final int width = 15_000;
    final IExpr[] args = new IExpr[width];
    java.util.Arrays.fill(args, F.C1);
    final IExpr wide = F.ast(args, F.f);
    assertEquals(width + 1L, count(new PreorderTraversal(wide)));
    assertEquals(width + 1L, count(new PostorderTraversal(wide)));
  }
}
