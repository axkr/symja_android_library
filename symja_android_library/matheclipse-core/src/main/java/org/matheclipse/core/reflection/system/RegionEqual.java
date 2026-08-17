package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <pre>
 * RegionEqual(reg1, reg2, ...)
 * </pre>
 *
 * <blockquote>
 * <p>
 * returns <code>True</code> if all the regions contain exactly the same points.
 * </p>
 * </blockquote>
 */
public class RegionEqual extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() <= 1) {
      // a single region, and vacuously the empty case, is equal to itself
      if (ast.argSize() == 0) {
        return S.True;
      }
      return canonical(ast.arg1(), engine).isPresent() ? S.True : F.NIL;
    }

    IExpr first = canonical(ast.arg1(), engine);
    if (!first.isPresent()) {
      return F.NIL;
    }
    boolean equal = true;
    for (int i = 2; i <= ast.argSize(); i++) {
      IExpr other = canonical(ast.get(i), engine);
      if (!other.isPresent()) {
        return F.NIL;
      }
      if (!first.equals(other)) {
        equal = false;
      }
    }
    return equal ? S.True : S.False;
  }

  /**
   * Rewrite a region into a normal form, so that regions describing the same point set become
   * structurally equal. <code>Rectangle</code> and <code>Triangle</code> become
   * <code>Polygon</code>, a two dimensional <code>Ball</code> becomes a <code>Disk</code> and a two
   * dimensional <code>Sphere</code> becomes a <code>Circle</code>. The corner points of a polygon
   * and the end points of a line are brought into a canonical order.
   *
   * @return {@link F#NIL} if <code>region</code> is not a supported region
   */
  private static IExpr canonical(IExpr region, EvalEngine engine) {
    if (!region.isAST()) {
      return F.NIL;
    }
    IAST reg = (IAST) region;
    IExpr head = reg.head();
    if (!head.isBuiltInSymbol()) {
      return F.NIL;
    }
    switch (((IBuiltInSymbol) head).ordinal()) {
      case ID.Point:
        return reg.argSize() == 1 ? F.Point(reg.arg1()) : F.NIL;
      case ID.Interval:
        return reg;
      case ID.Disk:
      case ID.Ball:
        return ballForm(reg, S.Disk, S.Ball, engine);
      case ID.Circle:
      case ID.Sphere:
        return ballForm(reg, S.Circle, S.Sphere, engine);
      case ID.Line:
        return lineForm(reg);
      case ID.Rectangle:
        return rectangleForm(reg, engine);
      case ID.Triangle:
        if (reg.argSize() == 0) {
          return polygonForm(F.list(F.List(F.C0, F.C0), F.List(F.C1, F.C0), F.List(F.C0, F.C1)));
        }
        return reg.argSize() == 1 && reg.arg1().isListOfLists() ? polygonForm((IAST) reg.arg1())
            : F.NIL;
      case ID.Polygon:
        return reg.argSize() == 1 && reg.arg1().isListOfLists() ? polygonForm((IAST) reg.arg1())
            : F.NIL;
    }
    return F.NIL;
  }

  /**
   * A ball is described by its center and its radius. In two dimensions <code>Ball</code> and
   * <code>Disk</code> - and <code>Sphere</code> and <code>Circle</code> - describe the same region,
   * so the two dimensional head is used for both.
   */
  private static IExpr ballForm(IAST reg, IBuiltInSymbol head2D, IBuiltInSymbol headND,
      EvalEngine engine) {
    IExpr center;
    if (reg.argSize() == 0) {
      center = (reg.head() == S.Disk || reg.head() == S.Circle) ? F.List(F.C0, F.C0)
          : F.List(F.C0, F.C0, F.C0);
    } else if (reg.arg1().isList() && !reg.arg1().isListOfLists()) {
      center = reg.arg1();
    } else {
      return F.NIL;
    }
    IExpr radius = reg.argSize() >= 2 ? reg.arg2() : F.C1;
    if (radius.isList()) {
      return F.NIL;
    }
    IBuiltInSymbol head = center.argSize() == 2 ? head2D : headND;
    return F.binaryAST2(head, center, radius);
  }

  /** The two end points of a line segment are interchangeable. */
  private static IExpr lineForm(IAST reg) {
    if (reg.argSize() != 1 || !reg.arg1().isListOfLists()) {
      return F.NIL;
    }
    IAST points = (IAST) reg.arg1();
    if (points.argSize() != 2) {
      return F.Line(points);
    }
    if (points.arg2().compareTo(points.arg1()) < 0) {
      return F.Line(F.list(points.arg2(), points.arg1()));
    }
    return F.Line(points);
  }

  /** An axis aligned rectangle is the polygon through its four corner points. */
  private static IExpr rectangleForm(IAST reg, EvalEngine engine) {
    IExpr lower;
    IExpr upper;
    if (reg.argSize() == 0) {
      lower = F.List(F.C0, F.C0);
      upper = F.List(F.C1, F.C1);
    } else if (reg.argSize() == 1 && reg.arg1().isList2()) {
      lower = reg.arg1();
      upper = engine.evaluate(F.Plus(lower, F.List(F.C1, F.C1)));
    } else if (reg.argSize() == 2 && reg.arg1().isList2() && reg.arg2().isList2()) {
      lower = reg.arg1();
      upper = reg.arg2();
    } else {
      return F.NIL;
    }
    IExpr x1 = ((IAST) lower).arg1();
    IExpr y1 = ((IAST) lower).arg2();
    IExpr x2 = ((IAST) upper).arg1();
    IExpr y2 = ((IAST) upper).arg2();
    return polygonForm(F.List(F.List(x1, y1), F.List(x2, y1), F.List(x2, y2), F.List(x1, y2)));
  }

  /**
   * The corner points of a polygon are cyclic and may be traversed in either direction. The
   * lexicographically smallest of all those orderings is used as the normal form.
   */
  private static IExpr polygonForm(IAST points) {
    int n = points.argSize();
    if (n < 3) {
      return F.NIL;
    }
    IAST best = null;
    for (int direction = 0; direction < 2; direction++) {
      for (int start = 0; start < n; start++) {
        IASTAppendable rotated = F.ListAlloc(n);
        for (int i = 0; i < n; i++) {
          int index = direction == 0 ? (start + i) % n : (start - i + 2 * n) % n;
          rotated.append(points.get(index + 1));
        }
        if (best == null || rotated.compareTo(best) < 0) {
          best = rotated;
        }
      }
    }
    return F.Polygon(best);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_0_INFINITY;
  }
}
