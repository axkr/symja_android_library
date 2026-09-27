package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>SectorChart3D({{x, y, z}, ...})</code> - a three dimensional sector chart: the angle of a
 * sector is proportional to <code>x</code>, its radius is <code>y</code> and its height
 * <code>z</code>.
 *
 * <p>
 * The sectors start at the left and follow one another clockwise, as those of
 * <code>SectorChart</code> do. Several datasets are concentric rings with
 * <code>ChartLayout -&gt; "Grouped"</code> (the default), or stack the radii of the sectors of the
 * same position with <code>ChartLayout -&gt; "Stacked"</code>. <code>ChartElementFunction</code>
 * chooses the shape: <code>"CylindricalSector3D"</code> (the default, a wedge of a cylinder),
 * <code>"ProfileSector3D"</code> (a wedge whose height falls off towards the rim) or
 * <code>"TorusSector3D"</code> (a round tube along the arc).
 */
public class SectorChart3D extends AbstractEvaluator {

  /** The angle one polygon of a curved face spans at most. */
  private static final double STEP = Math.PI / 36.0;

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IAST datasets = datasets(ast.arg1());
    if (datasets == null) {
      return F.NIL;
    }
    int argSize = 1;
    while (argSize < ast.argSize() && !ast.get(argSize + 1).isRuleAST()) {
      argSize++;
    }
    IExpr layout = GraphicsOptions.optionValue(ast, S.ChartLayout, S.Automatic);
    boolean stacked = layout.isString("Stacked");
    IExpr element = GraphicsOptions.optionValue(ast, S.ChartElementFunction, S.Automatic);
    String shape = element.isString() ? element.toString() : "CylindricalSector3D";
    IExpr chartStyle = GraphicsOptions.optionValue(ast, S.ChartStyle, S.Automatic);

    IASTAppendable primitives = F.ListAlloc();
    double ringBase = 0.0;
    double[] stackedBase = new double[maxLength(datasets) + 1];
    int colorIndex = 0;
    for (int d = 1; d < datasets.size(); d++) {
      IAST data = (IAST) datasets.get(d);
      double total = 0.0;
      for (int i = 1; i < data.size(); i++) {
        total += Math.max(0.0, component(data.get(i), 1));
      }
      if (!(total > 0.0)) {
        return F.NIL;
      }
      double angle = Math.PI;
      double ringTop = ringBase;
      for (int i = 1; i < data.size(); i++) {
        IExpr item = data.get(i);
        double x = component(item, 1);
        double y = component(item, 2);
        double z = component(item, 3);
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
          return F.NIL;
        }
        double sweep = Math.max(0.0, x) / total * 2.0 * Math.PI;
        // clockwise from the left
        double a1 = angle - sweep;
        double a2 = angle;
        angle = a1;
        double inner = stacked ? stackedBase[i] : ringBase;
        double outer = inner + Math.abs(y);
        if (stacked) {
          stackedBase[i] = outer;
        }
        ringTop = Math.max(ringTop, outer);
        if (sweep <= 0.0 || outer <= inner || z == 0.0) {
          colorIndex++;
          continue;
        }
        PlotWrapper wrapper = PlotWrapper.of(item);
        IExpr color = wrapper.style.isPresent() ? wrapper.style
            : GraphicsOptions.chartStyleColor(chartStyle, stacked ? d - 1 : colorIndex);
        IAST solid;
        switch (shape) {
          case "TorusSector3D":
            solid = torusSector(inner, outer, z, a1, a2);
            break;
          case "ProfileSector3D":
            solid = cylindricalSector(inner, outer, z, 0.5 * z, a1, a2);
            break;
          default:
            solid = cylindricalSector(inner, outer, z, z, a1, a2);
        }
        IAST group = F.List(F.EdgeForm(S.None), color, solid);
        primitives.append(
            wrapper.hasTooltip() ? F.binaryAST2(S.Tooltip, group, wrapper.tooltip) : group);
        colorIndex++;
      }
      if (!stacked) {
        // the next dataset is a ring around this one
        ringBase = ringTop;
      }
    }
    if (primitives.argSize() == 0) {
      return F.NIL;
    }
    IExpr[] defaults = {F.Rule(S.Boxed, S.False), F.Rule(S.Lighting, F.stringx("Neutral"))};
    return Plot3DTools.graphics3D(primitives, ast, argSize, defaults, false);
  }

  /** The datasets of the argument: one list of triples, or a list of such lists. */
  private static IAST datasets(IExpr arg) {
    if (!arg.isList() || arg.argSize() == 0) {
      return null;
    }
    IAST list = (IAST) arg;
    if (isTriple(list.arg1())) {
      return list.forAll(SectorChart3D::isTriple) ? F.List(list) : null;
    }
    for (IExpr data : list) {
      if (!data.isList() || data.argSize() == 0 || !((IAST) data).forAll(SectorChart3D::isTriple)) {
        return null;
      }
    }
    return list;
  }

  private static boolean isTriple(IExpr item) {
    IExpr datum = PlotWrapper.strip(item);
    // three numbers, not a dataset of three triples
    return datum.isList() && datum.argSize() == 3 && ((IAST) datum).forAll(x -> !x.isList());
  }

  private static int maxLength(IAST datasets) {
    int max = 0;
    for (IExpr data : datasets) {
      max = Math.max(max, data.argSize());
    }
    return max;
  }

  /** The n-th number of a triple. */
  private static double component(IExpr item, int n) {
    return ((IAST) PlotWrapper.strip(item)).get(n).evalfNaN();
  }

  private static IAST point(double r, double angle, double z) {
    return F.List(F.num(r * Math.cos(angle)), F.num(r * Math.sin(angle)), F.num(z));
  }

  /**
   * The wedge between the radii <code>inner</code> and <code>outer</code> and the angles
   * <code>a1 &lt; a2</code>: height <code>innerHeight</code> at the inner radius, falling linearly
   * to <code>outerHeight</code> at the rim.
   */
  private static IAST cylindricalSector(double inner, double outer, double innerHeight,
      double outerHeight, double a1, double a2) {
    int n = Math.max(2, (int) Math.ceil((a2 - a1) / STEP));
    IASTAppendable polygons = F.ListAlloc(4 * n + 2);
    for (int k = 0; k < n; k++) {
      double b1 = a1 + (a2 - a1) * k / n;
      double b2 = a1 + (a2 - a1) * (k + 1) / n;
      // top and bottom
      polygons.append(F.List(point(inner, b1, innerHeight), point(outer, b1, outerHeight),
          point(outer, b2, outerHeight), point(inner, b2, innerHeight)));
      polygons.append(F.List(point(inner, b1, 0), point(inner, b2, 0), point(outer, b2, 0),
          point(outer, b1, 0)));
      // the rim, and the inner wall of a ring
      polygons.append(F.List(point(outer, b1, 0), point(outer, b2, 0),
          point(outer, b2, outerHeight), point(outer, b1, outerHeight)));
      if (inner > 0.0) {
        polygons.append(F.List(point(inner, b1, 0), point(inner, b1, innerHeight),
            point(inner, b2, innerHeight), point(inner, b2, 0)));
      }
    }
    // the two straight sides
    polygons.append(F.List(point(inner, a1, 0), point(outer, a1, 0), point(outer, a1, outerHeight),
        point(inner, a1, innerHeight)));
    polygons.append(F.List(point(inner, a2, 0), point(inner, a2, innerHeight),
        point(outer, a2, outerHeight), point(outer, a2, 0)));
    return F.unaryAST1(S.Polygon, polygons);
  }

  /**
   * A tube along the arc from <code>a1</code> to <code>a2</code>: its centre runs at the middle
   * radius and half the height, its cross section is an ellipse filling the sector's
   * <code>(outer - inner) x height</code> rectangle, and its ends are closed.
   */
  private static IAST torusSector(double inner, double outer, double height, double a1,
      double a2) {
    int n = Math.max(2, (int) Math.ceil((a2 - a1) / STEP));
    int sides = 16;
    double middle = 0.5 * (inner + outer);
    double halfWidth = 0.5 * (outer - inner);
    double halfHeight = 0.5 * height;
    IASTAppendable polygons = F.ListAlloc(n * sides + 2);
    for (int k = 0; k < n; k++) {
      double b1 = a1 + (a2 - a1) * k / n;
      double b2 = a1 + (a2 - a1) * (k + 1) / n;
      for (int j = 0; j < sides; j++) {
        double c1 = 2.0 * Math.PI * j / sides;
        double c2 = 2.0 * Math.PI * (j + 1) / sides;
        polygons.append(F.List(
            point(middle + halfWidth * Math.cos(c1), b1, halfHeight + halfHeight * Math.sin(c1)),
            point(middle + halfWidth * Math.cos(c1), b2, halfHeight + halfHeight * Math.sin(c1)),
            point(middle + halfWidth * Math.cos(c2), b2, halfHeight + halfHeight * Math.sin(c2)),
            point(middle + halfWidth * Math.cos(c2), b1, halfHeight + halfHeight * Math.sin(c2))));
      }
    }
    for (double end : new double[] {a1, a2}) {
      IASTAppendable cap = F.ListAlloc(sides);
      for (int j = 0; j < sides; j++) {
        double c = 2.0 * Math.PI * j / sides;
        cap.append(point(middle + halfWidth * Math.cos(c), end,
            halfHeight + halfHeight * Math.sin(c)));
      }
      polygons.append(cap);
    }
    return F.unaryAST1(S.Polygon, polygons);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_INFINITY;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }
}
