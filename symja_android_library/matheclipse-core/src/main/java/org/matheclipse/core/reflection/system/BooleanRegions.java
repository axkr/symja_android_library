package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * What {@link RegionIntersection}, {@link RegionUnion} and {@link RegionDifference} share: the
 * space the regions live in, and the <code>BooleanRegion(f, {reg1, reg2, ...})</code> a
 * combination is carried as when it cannot be written as one shape.
 *
 * <p>
 * A part that is itself a <code>BooleanRegion</code> is inlined rather than nested - its regions
 * join the list and its function joins <code>f</code> with its slots renumbered - which is how the
 * reference implementation writes <code>RegionIntersection(RegionUnion(a, b), c)</code>:
 * <code>BooleanRegion((#1 || #2) && #3 &, {a, b, c})</code>.
 */
final class BooleanRegions {

  private BooleanRegions() {}

  /**
   * The space the regions live in, or <code>-1</code> when that is not one space. Regions of
   * different embedding dimensions are reported with the <code>regdims</code> message of
   * <code>head</code>.
   */
  static int embeddingDimension(ISymbol head, List<IExpr> regions, EvalEngine engine) {
    int dimension = -1;
    IExpr first = F.NIL;
    for (IExpr region : regions) {
      int part = RegionEmbeddingDimension.getEmbeddingDimension(region);
      if (part < 1) {
        return -1;
      }
      if (dimension < 0) {
        dimension = part;
        first = region;
      } else if (dimension != part) {
        // Boolean operations involving regions `1` and `2` with different embedding dimensions
        // are not well defined.
        Errors.printMessage(head, "regdims", F.List(first, region), engine);
        return -1;
      }
    }
    return dimension;
  }

  /**
   * <code>BooleanRegion(f, {...})</code> for the operands, where <code>combine</code> builds the
   * body of <code>f</code> from the operands' own bodies: a slot for a plain region, the inlined
   * function for a <code>BooleanRegion</code>.
   */
  static IExpr of(List<IExpr> operands, Function<IExpr[], IExpr> combine) {
    List<IExpr> regions = new ArrayList<IExpr>();
    IExpr[] bodies = new IExpr[operands.size()];
    for (int i = 0; i < operands.size(); i++) {
      IExpr operand = operands.get(i);
      if (isBooleanRegion(operand)) {
        final int offset = regions.size();
        IExpr body = operand.first().first();
        bodies[i] = body.replaceAll(x -> x.isAST(S.Slot, 2) && x.first().isInteger()
            ? F.Slot(x.first().toIntDefault() + offset)
            : F.NIL).orElse(body);
        IAST parts = (IAST) operand.second();
        for (int j = 1; j < parts.size(); j++) {
          regions.add(parts.get(j));
        }
      } else {
        regions.add(operand);
        bodies[i] = F.Slot(regions.size());
      }
    }
    return F.binaryAST2(S.BooleanRegion, F.Function(combine.apply(bodies)),
        F.ast(regions.toArray(new IExpr[0]), S.List));
  }

  /** <code>head(b1, b2, ...)</code>, a body with the same head taken apart into its arguments. */
  static IExpr junction(ISymbol head, IExpr... bodies) {
    IASTAppendable result = F.ast(head, bodies.length);
    for (IExpr body : bodies) {
      if (body.isAST(head)) {
        result.appendArgs((IAST) body);
      } else {
        result.append(body);
      }
    }
    return result;
  }

  /**
   * The parts <code>{outer, hole}</code> of <code>RegionDifference(outer, hole)</code>, the form
   * <code>BooleanRegion(#1 && !#2 &, {outer, hole})</code>, where both are regions of the same full
   * dimension and the hole lies inside <code>outer</code>.
   *
   * @return <code>null</code> if the region is not of that form or the hole isn't known to lie inside
   */
  static IAST[] withHole(IExpr region, EvalEngine engine) {
    if (!isBooleanRegion(region) || region.second().argSize() != 2
        || !region.first().first().equals(F.And(F.Slot1, F.Not(F.Slot2)))) {
      return null;
    }
    IExpr outer = region.second().first();
    IExpr hole = region.second().second();
    if (!outer.isAST() || !hole.isAST()) {
      return null;
    }
    int dimension = RegionDimension.getRegionDimension(outer);
    if (dimension < 1 || dimension != RegionDimension.getRegionDimension(hole)
        || !S.RegionWithin.ofQ(engine, outer, hole)) {
      return null;
    }
    return new IAST[] {(IAST) outer, (IAST) hole};
  }

  /** The measure of a region with a hole: the measure of the outer region less that of the hole. */
  static IExpr holeMeasure(IExpr region, EvalEngine engine) {
    IAST[] parts = withHole(region, engine);
    if (parts == null) {
      return F.NIL;
    }
    IExpr outer = engine.evaluate(F.unaryAST1(S.RegionMeasure, parts[0]));
    IExpr hole = engine.evaluate(F.unaryAST1(S.RegionMeasure, parts[1]));
    if (outer.has(S.RegionMeasure) || hole.has(S.RegionMeasure)) {
      return F.NIL;
    }
    return engine.evaluate(F.Subtract(outer, hole));
  }

  /**
   * The centroid of a region with a hole, <code>(m1*c1 - m2*c2)/(m1 - m2)</code> with the measures
   * <code>m</code> and centroids <code>c</code> of the outer region and the hole.
   */
  static IExpr holeCentroid(IExpr region, EvalEngine engine) {
    IAST[] parts = withHole(region, engine);
    if (parts == null) {
      return F.NIL;
    }
    IExpr m1 = engine.evaluate(F.unaryAST1(S.RegionMeasure, parts[0]));
    IExpr m2 = engine.evaluate(F.unaryAST1(S.RegionMeasure, parts[1]));
    IExpr c1 = engine.evaluate(F.unaryAST1(S.RegionCentroid, parts[0]));
    IExpr c2 = engine.evaluate(F.unaryAST1(S.RegionCentroid, parts[1]));
    if (m1.has(S.RegionMeasure) || m2.has(S.RegionMeasure) || !c1.isList() || !c2.isList()
        || c1.argSize() != c2.argSize()) {
      return F.NIL;
    }
    IExpr centroid = engine.evaluate(F.Divide(F.Subtract(F.Times(m1, c1), F.Times(m2, c2)), //
        F.Subtract(m1, m2)));
    return centroid.isList() ? engine.evaluate(F.Map(S.Together, centroid)) : F.NIL;
  }

  /** Whether the region is a <code>BooleanRegion</code> with a pure function over its parts. */
  static boolean isBooleanRegion(IExpr region) {
    return region.isAST(S.BooleanRegion, 3) && region.second().isList()
        && region.first().isAST(S.Function, 2);
  }

  /**
   * Whether the region is a <code>BooleanRegion(#1 op #2 op ... &, {...})</code>, asking for all
   * (<code>op</code> is <code>And</code>) or for any (<code>Or</code>) of its parts in order - the
   * form which is taken apart when it is combined by the same operation again.
   */
  static boolean isJunction(IExpr region, ISymbol op) {
    if (!isBooleanRegion(region) || !region.first().first().isAST(op)) {
      return false;
    }
    IAST junction = (IAST) region.first().first();
    if (junction.argSize() != region.second().argSize()) {
      return false;
    }
    for (int i = 1; i <= junction.argSize(); i++) {
      if (!junction.get(i).isAST(S.Slot, 2) || junction.get(i).first().toIntDefault() != i) {
        return false;
      }
    }
    return true;
  }
}
