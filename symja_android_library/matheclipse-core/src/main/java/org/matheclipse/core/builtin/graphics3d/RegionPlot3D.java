package org.matheclipse.core.builtin.graphics3d;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsComplexBuilder;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.PlotWrapper;
import org.matheclipse.core.graphics.RegionFunctionFilter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>RegionPlot3D(pred, {x, xmin, xmax}, {y, ymin, ymax}, {z, zmin, zmax})</code> - the solid
 * where the condition <code>pred</code> holds, drawn as its surface.
 *
 * <p>
 * The condition becomes a field that is negative inside and positive outside - <code>a &lt; b</code>
 * is <code>a - b</code>, <code>And</code> the largest and <code>Or</code> the smallest of its
 * parts - and the surface is its zero level, extracted with {@link MarchingCubes}. Around the samples lies one layer of "outside" on the very faces of the
 * box, so where the solid meets the box its surface is closed by a flat cap, as Mathematica draws
 * it.
 */
public class RegionPlot3D extends AbstractFunctionOptionEvaluator {

  /** Samples per axis by default; the cost is cubic. */
  private static final int DEFAULT_PLOT_POINTS = 30;

  /** An upper bound, because a cubic grid gets out of hand quickly. */
  private static final int MAX_PLOT_POINTS = 80;

  public RegionPlot3D() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (ast.size() > 1) {
      IExpr unwrapped = PlotWrapper.strip(ast.arg1());
      if (unwrapped != ast.arg1()) {
        ast = ast.setAtCopy(1, unwrapped);
      }
    }
    if (argSize < 4) {
      return F.NIL;
    }
    for (int i = 2; i <= 4; i++) {
      IExpr range = ast.get(i);
      if (!range.isList3() || !range.first().isSymbol()) {
        return Errors.printMessage(S.RegionPlot3D, "pllim", F.list(range), engine);
      }
    }
    IExpr field = outsideField(ast.arg1());
    if (field.isNIL()) {
      return F.NIL;
    }

    ISymbol[] vars = new ISymbol[3];
    double[] min = new double[3];
    double[] max = new double[3];
    for (int i = 0; i < 3; i++) {
      IAST range = (IAST) ast.get(i + 2);
      vars[i] = (ISymbol) range.arg1();
      min[i] = range.arg2().evalfNaN();
      max[i] = range.arg3().evalfNaN();
      if (!Double.isFinite(min[i]) || !Double.isFinite(max[i]) || max[i] <= min[i]) {
        return Errors.printMessage(S.RegionPlot3D, "pllim", F.list(range), engine);
      }
    }

    int points = Math.max(2, Math.min(
        Plot3DTools.plotPoints(options[Plot3DTools.X_PLOT_POINTS], DEFAULT_PLOT_POINTS)[0],
        MAX_PLOT_POINTS));
    double[][][] samples = MarchingCubes.sample(field, vars, min, max,
        options[Plot3DTools.X_EVALUATION_MONITOR], points, engine,
        RegionFunctionFilter.of(options[Plot3DTools.X_REGION_FUNCTION], engine));
    if (samples == null) {
      return F.NIL;
    }

    // the samples inside a layer of "outside" that lies on the faces of the box
    int n = points + 2;
    double outside = 1.0;
    for (double[][] plane : samples) {
      for (double[] row : plane) {
        for (double value : row) {
          if (Double.isFinite(value)) {
            outside = Math.max(outside, Math.abs(value));
          }
        }
      }
    }
    double[][][] grid = new double[n][n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        for (int k = 0; k < n; k++) {
          boolean inner = i > 0 && j > 0 && k > 0 && i < n - 1 && j < n - 1 && k < n - 1;
          double value = inner ? samples[i - 1][j - 1][k - 1] : outside;
          // where the condition cannot be decided, the point is not in the region
          grid[i][j][k] = Double.isFinite(value) ? value : outside;
        }
      }
    }
    double[][] axis = new double[3][n];
    for (int c = 0; c < 3; c++) {
      double step = (max[c] - min[c]) / (points - 1);
      axis[c][0] = min[c];
      for (int i = 0; i < points; i++) {
        axis[c][i + 1] = min[c] + i * step;
      }
      axis[c][n - 1] = max[c];
    }

    // A style of its own is used as given, in one flat Directive; Mathematica adds no highlight to
    // it. The lights of the plot go into that Directive as well, as Mathematica puts them there.
    IExpr plotStyle = options[Plot3DTools.X_PLOT_STYLE];
    if (plotStyle != null && plotStyle.isList() && plotStyle.argSize() > 0) {
      plotStyle = plotStyle.first();
    }
    boolean ownStyle =
        plotStyle != null && !plotStyle.isAutomatic() && !plotStyle.isNone() && !plotStyle.isList();
    IExpr lighting = options[Plot3DTools.X_LIGHTING];
    boolean ownLights = lighting != null && lighting.isList();
    IExpr style;
    if (ownStyle || ownLights) {
      IASTAppendable directive = F.ast(S.Directive);
      if (ownLights) {
        directive.append(F.Rule(S.Lighting, lighting));
      }
      IExpr parts = ownStyle ? plotStyle : Plot3DTools.surfaceStyle(0, S.Automatic);
      if (parts.isAST(S.Directive)) {
        directive.appendArgs((IAST) parts);
      } else {
        directive.append(parts);
      }
      style = directive;
    } else {
      style = Plot3DTools.surfaceStyle(0, S.Automatic);
    }

    GraphicsComplexBuilder builder = new GraphicsComplexBuilder(true, false);
    Plot3DTools.applyStyle(builder, style, options[Plot3DTools.X_MESH]);
    MarchingCubes.marchingCubes(builder, grid, 0.0, axis);
    IExpr complex = builder.build();

    IExpr plotRange =
        F.Rule(S.PlotRange, F.List(F.List(F.num(min[0]), F.num(max[0])),
            F.List(F.num(min[1]), F.num(max[1])), F.List(F.num(min[2]), F.num(max[2]))));
    IExpr[] defaults = new IExpr[] {plotRange, F.Rule(S.BoxRatios, F.List(F.C1, F.C1, F.C1)),
        F.Rule(S.Axes, S.True), F.Rule(S.Lighting, Plot3DTools.PLOT_LIGHTING)};
    IExpr result = Plot3DTools.graphics3D(complex.isPresent() ? F.List(complex) : F.CEmptyList,
        originalAST, argSize, defaults);
    return optionsInAList(result);
  }

  /**
   * <code>Graphics3D(primitives, {options})</code>: the options gathered in a list, as
   * Mathematica's <code>RegionPlot3D</code> writes them. The WLJS notebook relies on that shape:
   * its demo adds an option with <code>Insert(plot, "Renderer" -> "PathTracing", {2, -1})</code>,
   * which put the rule into the first option when the options followed one by one.
   */
  private static IExpr optionsInAList(IExpr graphics) {
    if (!graphics.isAST(S.Graphics3D) || graphics.argSize() < 2) {
      return graphics;
    }
    IAST ast = (IAST) graphics;
    IASTAppendable options = F.ListAlloc(ast.argSize());
    for (int i = 2; i <= ast.argSize(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isList()) {
        options.appendArgs((IAST) arg);
      } else {
        options.append(arg);
      }
    }
    return F.binaryAST2(S.Graphics3D, ast.arg1(), options);
  }

  /**
   * A field that is negative where <code>condition</code> holds and positive where it does not.
   *
   * @return the field or {@link F#NIL} if the condition is not made of comparisons, <code>And</code>,
   *         <code>Or</code> and <code>Not</code>
   */
  static IExpr outsideField(IExpr condition) {
    if (condition.isTrue()) {
      return F.CN1;
    }
    if (condition.isFalse()) {
      return F.C1;
    }
    if (!condition.isAST() || condition.argSize() < 1) {
      return F.NIL;
    }
    IAST ast = (IAST) condition;
    if (ast.isAST(S.Not, 2)) {
      IExpr inner = outsideField(ast.arg1());
      return inner.isPresent() ? F.Negate(inner) : F.NIL;
    }
    if (ast.isAnd() || ast.isOr()) {
      IASTAppendable parts = F.ast(ast.isAnd() ? S.Max : S.Min, ast.argSize());
      for (IExpr arg : ast) {
        IExpr part = outsideField(arg);
        if (part.isNIL()) {
          return F.NIL;
        }
        parts.append(part);
      }
      return parts;
    }
    boolean less = ast.isAST(S.Less) || ast.isAST(S.LessEqual);
    boolean greater = ast.isAST(S.Greater) || ast.isAST(S.GreaterEqual);
    if ((less || greater) && ast.argSize() >= 2) {
      // a < b < c holds where both a < b and b < c do
      IASTAppendable parts = F.ast(S.Max, ast.argSize() - 1);
      for (int i = 1; i < ast.argSize(); i++) {
        IExpr a = ast.get(i);
        IExpr b = ast.get(i + 1);
        parts.append(less ? F.Subtract(a, b) : F.Subtract(b, a));
      }
      return parts.argSize() == 1 ? parts.arg1() : parts;
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return new int[] {4, Integer.MAX_VALUE};
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    GraphicsOptions.OptionSet options =
        Plot3DTools.frameExtras(Plot3DTools.surfaceExtras(Plot3DTools.base3D()));
    setOptions(newSymbol, options.keys(), options.values());
  }
}
