package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.RegionPrimitives;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>RegionSymmetricDifference(reg1, reg2, ...)</code> - the points which lie in an odd number
 * of the regions.
 *
 * <p>
 * An empty part changes nothing and two equal parts cancel; otherwise the symmetric difference is
 * carried as <code>BooleanRegion(Xor(#1, #2, ...) &, {reg1, reg2, ...})</code>.
 */
public class RegionSymmetricDifference extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    List<IExpr> regions = new ArrayList<IExpr>(ast.argSize());
    for (int i = 1; i < ast.size(); i++) {
      regions.add(RegionPrimitives.unwrapRegion(ast.get(i)));
    }
    int embeddingDimension =
        BooleanRegions.embeddingDimension(S.RegionSymmetricDifference, regions, engine);
    if (embeddingDimension < 1) {
      return F.NIL;
    }
    List<IExpr> odd = new ArrayList<IExpr>(regions.size());
    for (IExpr region : regions) {
      if (region.isAST(S.EmptyRegion, 2)) {
        continue;
      }
      // a region taken twice cancels
      if (!odd.remove(region)) {
        odd.add(region);
      }
    }
    if (odd.isEmpty()) {
      return F.unaryAST1(S.EmptyRegion, F.ZZ(embeddingDimension));
    }
    if (odd.size() == 1) {
      return odd.get(0);
    }
    return BooleanRegions.of(odd, bodies -> BooleanRegions.junction(S.Xor, bodies));
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }
}
