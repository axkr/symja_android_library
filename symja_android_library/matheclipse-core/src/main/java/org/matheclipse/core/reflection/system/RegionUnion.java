package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>RegionUnion(reg1, reg2, ...)</code> - the points which lie in at least one of the regions.
 *
 * <p>
 * An empty part adds nothing and a full part covers everything; otherwise the union is carried as
 * <code>BooleanRegion(#1 || #2 || ... &, {reg1, reg2, ...})</code>, the form the reference
 * implementation gives it, which {@link RegionMember} and {@link RegionDimension} read. A nested
 * union is taken apart first.
 */
public class RegionUnion extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() == 0) {
      return F.NIL;
    }
    List<IExpr> regions = new ArrayList<IExpr>();
    flatten(ast, regions);

    boolean intervals = true;
    for (IExpr region : regions) {
      intervals &= region.isAST(S.Interval);
    }
    if (intervals) {
      return engine.evaluate(F.ast(regions.toArray(new IExpr[0]), S.IntervalUnion));
    }

    int embeddingDimension =
        BooleanRegions.embeddingDimension(S.RegionUnion, regions, engine);
    if (embeddingDimension < 1) {
      return F.NIL;
    }
    for (int i = 0; i < regions.size(); i++) {
      IExpr region = regions.get(i);
      if (region.isAST(S.FullRegion, 2)) {
        return region;
      }
      if (region.isAST(S.EmptyRegion, 2) || regions.subList(0, i).contains(region)) {
        // nothing to add, or added already
        regions.remove(i--);
      }
    }
    if (regions.isEmpty()) {
      return F.unaryAST1(S.EmptyRegion, F.ZZ(embeddingDimension));
    }
    if (regions.size() == 1) {
      return regions.get(0);
    }
    return BooleanRegions.of(regions, bodies -> BooleanRegions.junction(S.Or, bodies));
  }

  /** The regions to unite, a nested union taken apart and a <code>Region</code> wrapper removed. */
  private static void flatten(IAST ast, List<IExpr> regions) {
    for (int i = 1; i < ast.size(); i++) {
      IExpr region = ast.get(i);
      if (region.isAST(S.Region, 2)) {
        region = region.first();
      }
      if (region.isAST(S.RegionUnion)) {
        flatten((IAST) region, regions);
      } else if (BooleanRegions.isJunction(region, S.Or)) {
        flatten((IAST) region.second(), regions);
      } else {
        regions.add(region);
      }
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_0_INFINITY;
  }
}
