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
import com.google.common.collect.Sets;

/**
 * Ported from <code>sympy/strategies/branch/tests/test_tools.py</code>.
 */
public class TestBranchTools {
  private static final IExpr C8 = F.ZZ(8);
  private static final IExpr C10 = F.ZZ(10);

  private static IAST basic(IExpr... args) {
    return F.ast(args, F.f);
  }

  private static Iterator<IExpr> posdec(IExpr x) {
    return Iterators.singletonIterator(x.isInteger() && x.isPositive() ? x.plus(F.CN1) : x);
  }

  private static Iterator<IExpr> branch5(IExpr expr) {
    if (expr.isInteger()) {
      final int x = expr.toIntDefault();
      if (0 < x && x < 5) {
        return Iterators.singletonIterator(F.ZZ(x - 1));
      } else if (5 < x && x < 10) {
        return Iterators.singletonIterator(F.ZZ(x + 1));
      } else if (x == 5) {
        return List.<IExpr>of(F.ZZ(x + 1), F.ZZ(x - 1)).iterator();
      }
      return Iterators.singletonIterator(expr);
    }
    return Collections.emptyIterator();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void test_zero_ints() {
    final IExpr expr = basic(F.C2, basic(F.C5, F.C3), C8);
    final Set<IExpr> expected = Set.of(basic(F.C0, basic(F.C0, F.C0), F.C0));
    final Function<IExpr, Iterator<IExpr>> rule = TestBranchTools::posdec;
    assertEquals(expected, Sets.newHashSet(BranchTools.canon(rule).apply(expr)));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void test_split5() {
    final IExpr expr = basic(F.C2, basic(F.C5, F.C3), C8);
    final Set<IExpr> expected = Set.of(basic(F.C0, basic(F.C0, F.C0), C10),
        basic(F.C0, basic(C10, F.C0), C10));
    final Function<IExpr, Iterator<IExpr>> rule = TestBranchTools::branch5;
    assertEquals(expected, Sets.newHashSet(BranchTools.canon(rule).apply(expr)));
  }
}
