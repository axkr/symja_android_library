package org.matheclipse.core.reflection.system;

import java.util.Arrays;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>RegionDifference(reg1, reg2)</code> - the points of the first region which do not lie in
 * the second.
 *
 * <p>
 * Taking away nothing leaves the region, taking away everything or the region itself leaves the
 * empty region; otherwise the difference is carried as
 * <code>BooleanRegion(#1 && !#2 &, {reg1, reg2})</code>, a union taken away written out as
 * <code>#1 && !(#2 || #3)</code>.
 */
public class RegionDifference extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr from = unwrap(ast.arg1());
    IExpr away = unwrap(ast.arg2());
    List<IExpr> regions = Arrays.asList(from, away);
    int embeddingDimension =
        BooleanRegions.embeddingDimension(S.RegionDifference, regions, engine);
    if (embeddingDimension < 1) {
      return F.NIL;
    }
    IExpr empty = F.unaryAST1(S.EmptyRegion, F.ZZ(embeddingDimension));
    if (from.isAST(S.EmptyRegion, 2) || away.isAST(S.EmptyRegion, 2)) {
      return from;
    }
    if (away.isAST(S.FullRegion, 2) || from.equals(away)) {
      return empty;
    }
    return BooleanRegions.of(regions,
        bodies -> BooleanRegions.junction(S.And, bodies[0], F.Not(bodies[1])));
  }

  private static IExpr unwrap(IExpr region) {
    return region.isAST(S.Region, 2) ? region.first() : region;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
