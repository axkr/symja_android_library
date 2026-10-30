package org.matheclipse.core.builtin;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class GraphicsFunctions {
  private static final DecimalFormatSymbols US_SYMBOLS = new DecimalFormatSymbols(Locale.US);

  protected static final DecimalFormat FORMATTER = new DecimalFormat("0.0####", US_SYMBOLS);

  private static class Arrow extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Circle extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() == 0) {
        return F.Circle(F.List(F.C0, F.C0));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_3;
    }

    protected String getJSONType() {
      return "circle";
    }


    @Override
    public void setUp(final ISymbol newSymbol) {}
  }
  private static class Cone extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.Cone(F.list(F.List(0, 0, -1), F.List(0, 0, 1)), F.C1);
      }
      if (ast.isAST1()) {
        return F.Cone(ast.arg1(), F.C1);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Cube extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Cuboid extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.Cuboid(F.List(0, 0, 0), F.List(1, 1, 1));
      } else if (ast.isAST1() && ast.arg1().isList3()) {
        IASTMutable list2 = ((IAST) ast.arg1()).copy();
        for (int i = 1; i < list2.size(); i++) {
          list2.set(i, F.Plus(F.C1, list2.get(i)));
        }
        return F.Cuboid(ast.arg1(), list2);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Cylinder extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.Cylinder(F.list(F.List(0, 0, -1), F.List(0, 0, 1)), F.C1);
      }
      if (ast.isAST1()) {
        return F.Cylinder(ast.arg1(), F.C1);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * A symbol like <code>Thick</code> or <code>Dashed</code> whose value is a graphics directive.
   */
  private abstract static class GraphicsDirectiveSymbol extends AbstractSymbolEvaluator {
    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.PROTECTED, Attribute.READPROTECTED);
    }
  }

  private static class Dashed extends GraphicsDirectiveSymbol {
    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.Dashing(F.List(S.Small, S.Small));
    }
  }

  private static class Disk extends Circle {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() == 0) {
        return F.Disk(F.List(F.C0, F.C0));
      }
      return F.NIL;
    }

    @Override
    protected String getJSONType() {
      return "disk";
    }
  }

  private static class Dodecahedron extends Tetrahedron {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class DotDashed extends GraphicsDirectiveSymbol {
    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.Dashing(F.List(F.C0, S.Small, S.Small, S.Small));
    }
  }

  private static class Dotted extends GraphicsDirectiveSymbol {
    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.Dashing(F.List(F.C0, S.Small));
    }
  }

  /** <code>Thick</code> is equivalent to <code>Thickness(Large)</code>. */
  private static class Thick extends GraphicsDirectiveSymbol {
    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.unaryAST1(S.Thickness, S.Large);
    }
  }

  /** <code>Thin</code> is equivalent to <code>Thickness(Tiny)</code>. */
  private static class Thin extends GraphicsDirectiveSymbol {
    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.unaryAST1(S.Thickness, S.Tiny);
    }
  }

  private static class GraphicsComplex extends AbstractFunctionOptionEvaluator {


    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          new IBuiltInSymbol[] {S.VertexColors, S.VertexNormals, S.VertexTextureCoordinates}, //
          new IExpr[] {S.Automatic, S.Automatic, S.Automatic});
    }

  }

  private static class Icosahedron extends Tetrahedron {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  /**
   * <code>Show(g1, g2, ..., options)</code> - graphics shown together.
   *
   * <p>
   * A mesh region is shown as its picture: <code>Show(ConvexHullMesh(...))</code> is the
   * <code>Graphics</code> or <code>Graphics3D</code> the reference implementation draws it as.
   * Several graphics of the same kind become one, each keeping its primitives in a list of their
   * own so that one's directives do not reach into the next, and their options merged with the
   * first setting of each winning - so the options given to <code>Show</code> itself win over all
   * of them.
   *
   * <p>
   * Not done: the plot ranges are not joined into one, so the first graphic's range can clip the
   * others; <code>Epilog</code> and <code>Prolog</code> are not merged; two and three dimensional
   * graphics are not combined, and nothing but <code>Graphics</code>, <code>Graphics3D</code> and
   * mesh regions is shown.
   */
  private static final class Show extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IASTAppendable graphics = F.ListAlloc(ast.argSize());
      IASTAppendable options = F.ListAlloc();
      IASTAppendable legends = F.ListAlloc();
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST()) {
          options.append(arg);
        } else if (!collect(arg, graphics, legends, engine)) {
          return F.NIL;
        }
      }
      if (legends.argSize() > 0) {
        // the legends of legended graphics stay beside the combined picture
        IASTAppendable show = F.ast(S.Show, graphics.argSize() + options.argSize());
        show.appendArgs(graphics);
        show.appendArgs(options);
        IExpr combined = engine.evaluate(show);
        if (!combined.isAST(S.Graphics) && !combined.isAST(S.Graphics3D)) {
          return F.NIL;
        }
        return F.binaryAST2(S.Legended, combined,
            legends.argSize() == 1 ? legends.arg1() : legends);
      }
      if (graphics.argSize() == 0) {
        return F.NIL;
      }
      IExpr head = graphics.arg1().head();
      for (IExpr g : graphics) {
        if (g.head() != head) {
          // two and three dimensional graphics cannot be one picture
          return F.NIL;
        }
      }
      if (graphics.argSize() == 1 && options.argSize() == 0) {
        return graphics.arg1();
      }
      IASTAppendable primitives = F.ListAlloc(graphics.argSize());
      for (IExpr g : graphics) {
        if (((IAST) g).argSize() > 0) {
          // an empty Graphics() or Graphics3D() adds no primitives
          primitives.append(((IAST) g).arg1());
        }
        for (int i = 2; i < ((IAST) g).size(); i++) {
          addOptions(((IAST) g).get(i), options);
        }
      }
      IASTAppendable result = F.ast(head, options.argSize() + 1);
      result.append(primitives);
      java.util.Set<IExpr> seen = new java.util.HashSet<IExpr>();
      for (IExpr option : options) {
        // the first setting of an option wins
        if (seen.add(option.first())) {
          result.append(option);
        }
      }
      return result;
    }

    /**
     * A graphic, a mesh region as its picture, a legended graphic whose legend goes to
     * <code>legends</code>, or a list of these.
     */
    private static boolean collect(IExpr arg, IASTAppendable graphics, IASTAppendable legends,
        EvalEngine engine) {
      if (arg.isAST(S.Legended, 3)
          && (arg.first().isAST(S.Graphics) || arg.first().isAST(S.Graphics3D))) {
        graphics.append(arg.first());
        legends.append(arg.second());
        return true;
      }
      if (arg.isAST(S.Graphics) || arg.isAST(S.Graphics3D)) {
        graphics.append(arg);
        return true;
      }
      if (MeshFunctions.isBoundaryMeshRegion(arg)) {
        IAST picture = MeshFunctions.meshToGraphics((IAST) arg, engine);
        if (picture.isPresent()) {
          graphics.append(picture);
          return true;
        }
        return false;
      }
      if (arg.isList()) {
        for (IExpr element : (IAST) arg) {
          if (!collect(element, graphics, legends, engine)) {
            return false;
          }
        }
        return true;
      }
      return false;
    }

    /** An option of a graphic - a rule, or a list of them - added in order. */
    private static void addOptions(IExpr option, IASTAppendable options) {
      if (option.isRuleAST()) {
        options.append(option);
      } else if (option.isList()) {
        for (IExpr rule : (IAST) option) {
          addOptions(rule, options);
        }
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /**
   * <code>VerticalGauge(value)</code>, <code>VerticalGauge(value, {min, max})</code>: a vertical
   * scale of width <code>0.2</code> drawn in coordinates in which <code>y</code> runs from
   * <code>min</code> to <code>max</code> (<code>{0, 1}</code> if not given). A light track covers the
   * whole range, a bar fills it up to the value and a single needle <code>Line</code> marks the
   * value; the scale is labelled by <code>Text</code>s at <code>4</code> divisions. A value outside
   * the range is drawn at the nearest end.
   *
   * <p>
   * <code>GaugeStyle -&gt; colour</code> colours the bar, <code>ScaleDivisions -&gt; n</code> or
   * <code>None</code> sets the number of labelled divisions; the other options of built-in symbols
   * go to the <code>Graphics</code> and any other option is ignored.
   */
  private static class VerticalGauge extends AbstractEvaluator {
    /** The width of the scale in the coordinates of the picture. */
    private static final double WIDTH = 0.2;

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      double value = ast.arg1().evalfNaN();
      if (!Double.isFinite(value)) {
        return F.NIL;
      }
      IExpr min = F.C0;
      IExpr max = F.C1;
      int optionStart = 2;
      if (ast.size() > 2 && !ast.arg2().isRuleAST()) {
        if (!ast.arg2().isList2()) {
          return F.NIL;
        }
        min = ast.arg2().first();
        max = ast.arg2().second();
        optionStart = 3;
      }
      double low = min.evalfNaN();
      double high = max.evalfNaN();
      if (!Double.isFinite(low) || !Double.isFinite(high) || low >= high) {
        return F.NIL;
      }
      IExpr barColor = GraphicsOptions.plotStyleColorExpr(0, F.NIL);
      int divisions = 4;
      IASTAppendable graphicsOptions = F.ListAlloc();
      for (int i = optionStart; i < ast.size(); i++) {
        IExpr option = ast.get(i);
        if (!option.isRuleAST()) {
          return F.NIL;
        }
        IExpr name = option.first();
        IExpr setting = option.second();
        if (name == S.GaugeStyle) {
          if (GraphicsOptions.isColorExpr(setting)) {
            barColor = setting;
          }
        } else if (name == S.ScaleDivisions) {
          if (setting.isNone()) {
            divisions = 0;
          } else if (setting.isInteger() && setting.isPositive()) {
            divisions = setting.toIntDefault(4);
          }
        } else if (name.isBuiltInSymbol()) {
          graphicsOptions.append(option);
        }
      }
      double level = Math.max(low, Math.min(high, value));
      IASTAppendable primitives = F.ListAlloc();
      primitives.append(F.List(F.GrayLevel(0.9), //
          F.Rectangle(F.List(F.CD0, F.num(low)), F.List(F.num(WIDTH), F.num(high)))));
      primitives.append(F.List(barColor, //
          F.Rectangle(F.List(F.CD0, F.num(low)), F.List(F.num(WIDTH), F.num(level)))));
      primitives.append(F.List(S.Black, F.AbsoluteThickness(F.C2), //
          F.Line(F.List(F.List(F.CD0, F.num(level)), F.List(F.num(WIDTH), F.num(level))))));
      for (int k = 0; k <= divisions && divisions > 0; k++) {
        // the labels keep exact bounds exact
        IExpr label =
            engine.evaluate(F.Plus(min, F.Times(F.QQ(k, divisions), F.Subtract(max, min))));
        double y = low + k * (high - low) / divisions;
        primitives.append(F.Text(label, F.List(F.num(1.5 * WIDTH), F.num(y)), F.List(F.CN1, F.C0)));
      }
      IASTAppendable graphics = F.ast(S.Graphics, graphicsOptions.size() + 2);
      graphics.append(primitives);
      graphics.appendArgs(graphicsOptions);
      if (!graphicsOptions.exists(rule -> rule.first() == S.AspectRatio)) {
        graphics.append(F.Rule(S.AspectRatio, F.C3));
      }
      return graphics;
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

  private static class Initializer {

    private static void init() {
      S.Blend.setEvaluator(new Blend());
      S.PlotRange.setEvaluator(new PlotRange());
      S.AbsoluteOptions.setEvaluator(new AbsoluteOptions());
      S.ToColor.setEvaluator(new ToColor());
      S.Lighter.setEvaluator(new LighterDarker(true));
      S.Darker.setEvaluator(new LighterDarker(false));
      S.Dashed.setEvaluator(new Dashed());
      S.Show.setEvaluator(new Show());
      S.DotDashed.setEvaluator(new DotDashed());
      S.Dotted.setEvaluator(new Dotted());
      S.Thick.setEvaluator(new Thick());
      S.Thin.setEvaluator(new Thin());
      S.VerticalGauge.setEvaluator(new VerticalGauge());

      S.Arrow.setEvaluator(new Arrow());
      S.Circle.setEvaluator(new Circle());
      S.Disk.setEvaluator(new Disk());
      S.Cone.setEvaluator(new Cone());
      S.Cube.setEvaluator(new Cube());
      S.Cuboid.setEvaluator(new Cuboid());
      S.Cylinder.setEvaluator(new Cylinder());
      S.Dodecahedron.setEvaluator(new Dodecahedron());
      S.Icosahedron.setEvaluator(new Icosahedron());
      S.Labeled.setEvaluator(new Labeled());
      S.Line.setEvaluator(new Line());
      S.Octahedron.setEvaluator(new Octahedron());
      S.Overlay.setEvaluator(new Overlay());
      S.GraphicsRow.setEvaluator(new GraphicsRow());
      S.GraphicsColumn.setEvaluator(new GraphicsColumn());
      S.GraphicsGrid.setEvaluator(new GraphicsGrid());
      S.Point.setEvaluator(new Point());
      S.Polygon.setEvaluator(new Polygon());
      S.Rectangle.setEvaluator(new Rectangle());
      S.Scaled.setEvaluator(new Scaled());
      S.Sphere.setEvaluator(new Sphere());
      S.Tetrahedron.setEvaluator(new Tetrahedron());
      S.Text.setEvaluator(new Text());
      S.Tooltip.setEvaluator(new Tooltip());
      S.Tube.setEvaluator(new Tube());
      S.GraphicsComplex.setEvaluator(new GraphicsComplex());
    }
  }

  private static final class Labeled extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

  }

  /**
   * <code>Overlay({e1, e2, ...})</code> stacks its items on one canvas, later ones on top.
   *
   * <p>
   * A display wrapper: <code>evaluate()</code> always returns {@link F#NIL} so the expression
   * survives evaluation and the SVG factory can composite it, the same contract
   * <code>TableForm</code> and <code>Row</code> follow. Only graphics are composited - an
   * <code>Overlay</code> of anything else simply prints as itself.
   */
  private static final class Overlay extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // the wrapper must survive evaluation - it is resolved by
      // org.matheclipse.core.graphics.svg.SvgLayout#overlay
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      // {layers}, the layer selection, the selectable layer, and any number of option rules
      return ARGS_1_INFINITY;
    }
  }

  /**
   * The options every graphics layout accepts, with the defaults the Wolfram pages give.
   *
   * <p>
   * They are registered so that <code>Options(GraphicsGrid)</code> answers, and so that a name
   * given in a call is recognised as an option rather than counted as a positional argument. The
   * values themselves are read straight off the call by
   * {@link org.matheclipse.core.graphics.svg.LayoutSpec}, which needs the rules the caller wrote
   * rather than a resolved array.
   */
  private static IAST layoutOptions() {
    return F.List(F.Rule(S.Alignment, F.List(S.Center, S.Center)), //
        F.Rule(S.AspectRatio, S.Automatic), //
        F.Rule(S.Background, S.None), //
        F.Rule(S.BaseStyle, F.CEmptyList), //
        F.Rule(S.Dividers, S.None), //
        F.Rule(S.Frame, S.None), //
        F.Rule(S.FrameStyle, S.Automatic), //
        F.Rule(S.ImageMargins, F.C0), //
        F.Rule(S.ImageSize, S.Automatic), //
        F.Rule(S.ItemAspectRatio, S.Automatic), //
        F.Rule(S.Spacings, F.Scaled(0.1)));
  }

  /**
   * <code>GraphicsRow({g1, g2, ...})</code> draws its items side by side.
   *
   * <p>
   * A display wrapper, exactly like {@link Overlay}: <code>evaluate()</code> returns {@link F#NIL}
   * so the expression survives into the output stage, where
   * {@link org.matheclipse.core.graphics.svg.SvgLayout} lays it out. The arguments are still
   * evaluated first, which is what lets <code>GraphicsRow(Table(Plot(...), {2}))</code> work - an
   * {@code AbstractCoreFunctionEvaluator} would hold the table unevaluated and the row would find
   * no pictures to draw.
   */
  private static final class GraphicsRow extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // resolved by org.matheclipse.core.graphics.svg.SvgLayout#row
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      // {items}, an optional spacing, and any number of option rules
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, layoutOptions());
    }
  }

  /**
   * <code>GraphicsColumn({g1, g2, ...})</code> draws its items one above the other, the first at
   * the top. The optional second and third arguments are the horizontal alignment and the gap.
   */
  private static final class GraphicsColumn extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // resolved by org.matheclipse.core.graphics.svg.SvgLayout#column
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      // {items}, an optional alignment and spacing, and any number of option rules
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, layoutOptions());
    }
  }

  /**
   * <code>GraphicsGrid({{g11, g12, ...}, ...})</code> arranges its items in a grid.
   *
   * <p>
   * A cell may span several columns or rows by putting <code>SpanFromLeft</code>,
   * <code>SpanFromAbove</code> or <code>SpanFromBoth</code> in the positions it should cover, and
   * an <code>Item(expr, opts)</code> cell carries settings of its own.
   */
  private static final class GraphicsGrid extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // resolved by org.matheclipse.core.graphics.svg.SvgLayout#grid
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      // {{rows}}, and any number of option rules
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, layoutOptions());
    }
  }

  /**
   * <code>Tooltip(expr, label)</code> shows <code>label</code> while the pointer is over what
   * <code>expr</code> draws; <code>Tooltip(expr)</code> shows <code>expr</code> itself.
   *
   * <p>
   * A display wrapper: <code>evaluate()</code> returns {@link F#NIL} so it survives into the
   * picture, where the collectors read it and the renderer writes an SVG <code>title</code>. The
   * evaluator exists to pin the arity - without it <code>Tooltip()</code> reached the collector and
   * was silently ignored - and to give the symbol somewhere to hang its documentation.
   *
   * <p>
   * Not an {@code AbstractCoreFunctionEvaluator}: that would hold the arguments, and a tooltip
   * labels a value rather than the expression that computed it - <code>Tooltip(Prime(4))</code> has
   * to become <code>Tooltip(7)</code> before a plot can read either the number or the label.
   */
  private static final class Tooltip extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // the wrapper must survive evaluation - it is read by
      // org.matheclipse.core.graphics.svg.PrimitiveCollector and its 3D counterpart
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      // the expression, an optional label, and any of the styling options - which are accepted
      // and ignored, because the browser draws the tooltip and nothing here can style it
      return ARGS_1_INFINITY;
    }
  }

  private static class Line extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      // options may follow the points, as for Polygon and Point: WMA writes the outline of
      // a surface as Line[{{i, j}, ...}, VertexColors -> None]
      return ARGS_1_INFINITY;
    }


    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Octahedron extends Tetrahedron {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Point extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Polygon extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.NHOLDREST);
    }
  }

  private static class Rectangle extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() == 0) {
        return F.Rectangle(F.List(F.C0, F.C0));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   * <code>AbsoluteOptions(expr)</code>, <code>AbsoluteOptions(expr, name)</code> - the options of
   * <code>expr</code> with the values they actually take: the <code>PlotRange</code> of a two
   * dimensional graphic is the range it is drawn in.
   */
  private static class AbsoluteOptions extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr expr = ast.arg1();
      IExpr options = engine.evaluate(F.Options(expr));
      if (!options.isList()) {
        return F.NIL;
      }
      IExpr plotRange = F.NIL;
      if (expr.isAST(S.Graphics)) {
        plotRange = engine.evaluate(F.unaryAST1(S.PlotRange, expr));
        if (!plotRange.isList()) {
          plotRange = F.NIL;
        }
      }
      if (ast.isAST1()) {
        IASTAppendable result = F.ListAlloc(options.argSize() + 1);
        boolean hasPlotRange = false;
        for (IExpr rule : (IAST) options) {
          if (plotRange.isPresent() && rule.isRuleAST() && rule.first() == S.PlotRange) {
            result.append(F.Rule(S.PlotRange, plotRange));
            hasPlotRange = true;
          } else {
            result.append(rule);
          }
        }
        if (plotRange.isPresent() && !hasPlotRange) {
          result.append(F.Rule(S.PlotRange, plotRange));
        }
        return result;
      }
      if (ast.isAST2()) {
        IAST names = ast.arg2().makeList();
        IASTAppendable result = F.ListAlloc(names.argSize());
        for (IExpr name : names) {
          if (plotRange.isPresent() && name == S.PlotRange) {
            result.append(F.Rule(S.PlotRange, plotRange));
            continue;
          }
          IExpr rule = optionRule((IAST) options, name);
          if (rule.isNIL()) {
            IExpr defaults = engine.evaluate(F.Options(expr.head()));
            if (defaults.isList()) {
              rule = optionRule((IAST) defaults, name);
            }
          }
          if (rule.isPresent()) {
            result.append(rule);
          }
        }
        return result;
      }
      return F.NIL;
    }

    /** The rule for the option <code>name</code> in a list of rules, or {@link F#NIL}. */
    private static IExpr optionRule(IAST rules, IExpr name) {
      for (IExpr rule : rules) {
        if (rule.isRuleAST() && rule.first().equals(name)) {
          return rule;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>PlotRange(graphics)</code> - the range <code>{{xmin, xmax}, {ymin, ymax}}</code> a two
   * dimensional graphic is drawn in, without its padding.
   */
  private static class PlotRange extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.isAST1() || !ast.arg1().isAST(S.Graphics)) {
        return F.NIL;
      }
      double[][] range = org.matheclipse.core.graphics.svg.SvgGraphics2D.plotRange((IAST) ast.arg1());
      if (range == null) {
        return F.NIL;
      }
      return F.list(F.list(F.num(range[0][0]), F.num(range[0][1])),
          F.list(F.num(range[1][0]), F.num(range[1][1])));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return null;
    }
  }

  /**
   * A color directive taken apart: its color space (<code>RGBColor, GrayLevel, Hue</code> or
   * <code>CMYKColor</code>), its components and its opacity, which is <code>null</code> if the
   * directive has none.
   */
  private static final class ColorValue {
    final ISymbol space;
    final IExpr[] components;
    final IExpr opacity;

    ColorValue(ISymbol space, IExpr[] components, IExpr opacity) {
      this.space = space;
      this.components = components;
      this.opacity = opacity;
    }

    /**
     * The directive of the color space: a hue always has its saturation and brightness.
     */
    IAST toDirective() {
      IASTAppendable result = F.ast(space, components.length + 1);
      for (IExpr component : components) {
        result.append(component);
      }
      if (opacity != null) {
        result.append(opacity);
      }
      return result;
    }

    /**
     * Take apart a color directive with real components.
     *
     * @return <code>null</code> if <code>color</code> is no such directive
     */
    static ColorValue of(IExpr color, EvalEngine engine) {
      if (!color.isAST() || color.argSize() == 0 || !((IAST) color).forAll(x -> x.isReal())) {
        return null;
      }
      IAST directive = (IAST) color;
      int n = directive.argSize();
      if (directive.isAST(S.RGBColor) && (n == 3 || n == 4)) {
        return new ColorValue(S.RGBColor,
            new IExpr[] {directive.arg1(), directive.arg2(), directive.arg3()},
            n == 4 ? directive.arg4() : null);
      }
      if (directive.isAST(S.GrayLevel) && n <= 2) {
        return new ColorValue(S.GrayLevel, new IExpr[] {directive.arg1()},
            n == 2 ? directive.arg2() : null);
      }
      if (directive.isAST(S.CMYKColor) && (n == 4 || n == 5)) {
        return new ColorValue(S.CMYKColor,
            new IExpr[] {directive.arg1(), directive.arg2(), directive.arg3(), directive.arg4()},
            n == 5 ? directive.arg5() : null);
      }
      if (directive.isAST(S.Hue) && n != 2 && n <= 4) {
        IExpr hue = directive.arg1();
        double h = hue.evalf();
        if (h < 0.0 || h > 1.0) {
          // a hue of more than one turn is the hue of its fractional part
          hue = engine.evaluate(F.Mod(hue, F.C1));
        }
        return new ColorValue(S.Hue,
            new IExpr[] {hue, n >= 3 ? directive.arg2() : F.C1, n >= 3 ? directive.arg3() : F.C1},
            n == 4 ? directive.arg4() : null);
      }
      return null;
    }

    ColorValue withOpacity(IExpr newOpacity) {
      return new ColorValue(space, components, newOpacity);
    }

    /** The same color in the RGB color space. */
    ColorValue toRGB(EvalEngine engine) {
      IExpr[] rgb;
      if (space == S.RGBColor) {
        return this;
      } else if (space == S.GrayLevel) {
        rgb = new IExpr[] {components[0], components[0], components[0]};
      } else if (space == S.CMYKColor) {
        IExpr white = F.Subtract(F.C1, components[3]);
        rgb = new IExpr[3];
        for (int i = 0; i < 3; i++) {
          rgb[i] = engine.evaluate(F.Times(F.Subtract(F.C1, components[i]), white));
        }
      } else {
        rgb = hueToRGB(components[0], components[1], components[2], engine);
      }
      return new ColorValue(S.RGBColor, rgb, opacity);
    }

    private static IExpr[] hueToRGB(IExpr h, IExpr s, IExpr b, EvalEngine engine) {
      IExpr sector = engine.evaluate(F.Times(F.C6, h));
      int i = engine.evaluate(F.Floor(sector)).toIntDefault(0);
      IExpr f = engine.evaluate(F.Subtract(sector, F.ZZ(i)));
      IExpr p = engine.evaluate(F.Times(b, F.Subtract(F.C1, s)));
      IExpr q = engine.evaluate(F.Times(b, F.Subtract(F.C1, F.Times(s, f))));
      IExpr t = engine.evaluate(F.Times(b, F.Subtract(F.C1, F.Times(s, F.Subtract(F.C1, f)))));
      switch (Math.floorMod(i, 6)) {
        case 0:
          return new IExpr[] {b, t, p};
        case 1:
          return new IExpr[] {q, b, p};
        case 2:
          return new IExpr[] {p, b, t};
        case 3:
          return new IExpr[] {p, q, b};
        case 4:
          return new IExpr[] {t, p, b};
        default:
          return new IExpr[] {b, p, q};
      }
    }

    /** The same color in the given color space. */
    ColorValue convert(ISymbol target, EvalEngine engine) {
      if (target == space) {
        return this;
      }
      ColorValue rgbValue = toRGB(engine);
      if (target == S.RGBColor) {
        return rgbValue;
      }
      double r = rgbValue.components[0].evalf();
      double g = rgbValue.components[1].evalf();
      double b = rgbValue.components[2].evalf();
      if (target == S.GrayLevel) {
        // the luminance of the color
        return new ColorValue(S.GrayLevel, new IExpr[] {F.num(0.299 * r + 0.587 * g + 0.114 * b)},
            opacity);
      }
      double max = Math.max(r, Math.max(g, b));
      double min = Math.min(r, Math.min(g, b));
      if (target == S.CMYKColor) {
        double k = 1.0 - max;
        double white = 1.0 - k;
        IExpr[] cmyk = new IExpr[4];
        for (int i = 0; i < 3; i++) {
          double c = i == 0 ? r : i == 1 ? g : b;
          cmyk[i] = F.num(white == 0.0 ? 0.0 : (white - c) / white);
        }
        cmyk[3] = F.num(k);
        return new ColorValue(S.CMYKColor, cmyk, opacity);
      }
      double delta = max - min;
      double hue = 0.0;
      if (delta > 0.0) {
        if (max == r) {
          hue = (g - b) / delta;
        } else if (max == g) {
          hue = 2.0 + (b - r) / delta;
        } else {
          hue = 4.0 + (r - g) / delta;
        }
        hue /= 6.0;
        if (hue < 0.0) {
          hue += 1.0;
        }
      }
      return new ColorValue(S.Hue,
          new IExpr[] {F.num(hue), F.num(max == 0.0 ? 0.0 : delta / max), F.num(max)}, opacity);
    }
  }

  /**
   * <code>Blend({c1, c2, ...}, t)</code> - the color at <code>t</code> of the colors spread evenly
   * over <code>0 ... 1</code>; <code>Blend({{p1, c1}, {p2, c2}, ...}, t)</code> - the colors at
   * the positions <code>pi</code>; <code>Blend({c1, c2, ...}, {w1, w2, ...})</code> - the colors
   * mixed by the weights <code>wi</code>; <code>Blend({c1, c2, ...})</code> - mixed equally;
   * <code>Blend("gradient", t)</code> - the color of a named gradient.
   *
   * <p>
   * Colors of one color space are blended in it, other colors as RGB colors. Outside of the
   * positions <code>t</code> gives the color at the nearer end.
   */
  private static class Blend extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isString()) {
        if (ast.isAST2() && ast.arg2().isReal()) {
          return engine.evaluate(F.unaryAST1(F.unaryAST1(S.ColorData, arg1), ast.arg2()));
        }
        return F.NIL;
      }
      if (!arg1.isList() || arg1.argSize() == 0) {
        return F.NIL;
      }
      IAST list = (IAST) arg1;
      int n = list.argSize();
      boolean positioned = ast.isAST2() && ast.arg2().isReal()
          && list.forAll(x -> x.isList2() && x.first().isReal());
      if (positioned) {
        // the colors in the order of their positions
        IExpr[] pairs = new IExpr[n];
        for (int i = 0; i < n; i++) {
          pairs[i] = list.get(i + 1);
        }
        java.util.Arrays.sort(pairs,
            java.util.Comparator.comparingDouble((IExpr pair) -> pair.first().evalf()));
        list = F.mapRange(0, n, i -> pairs[i]);
      }
      IExpr[] colors = new IExpr[n];
      IExpr[] positions = new IExpr[n];
      ColorValue[] values = new ColorValue[n];
      ISymbol space = null;
      boolean opaque = true;
      for (int i = 0; i < n; i++) {
        IExpr element = list.get(i + 1);
        colors[i] = positioned ? element.second() : element;
        positions[i] = positioned ? element.first() : n == 1 ? F.C0 : F.QQ(i, n - 1);
        values[i] = ColorValue.of(colors[i], engine);
        if (values[i] == null) {
          if (colors[i].isAST(S.Image) || colors[i] instanceof org.matheclipse.core.interfaces.IDataExpr) {
            return F.NIL;
          }
          return Errors.printMessage(S.Blend, "color", F.list(colors[i]), engine);
        }
        space = (space == null || space == values[i].space) ? values[i].space : S.RGBColor;
        opaque &= values[i].opacity == null;
      }
      for (int i = 0; i < n; i++) {
        ColorValue value = values[i].convert(space, engine);
        values[i] = opaque || value.opacity != null ? value : value.withOpacity(F.C1);
      }

      if (ast.isAST1()) {
        return mix(values, F.constantArray(F.C1, n), engine);
      }
      IExpr arg2 = ast.arg2();
      if (arg2.isList() && arg2.argSize() == n
          && ((IAST) arg2).forAll(x -> x.isReal() && !x.isNegative())) {
        return mix(values, (IAST) arg2, engine);
      }
      if (!arg2.isReal()) {
        return Errors.printMessage(S.Blend, "argl", F.list(arg2, list), engine);
      }

      // the color at the parameter arg2
      double t = arg2.evalf();
      if (n == 1 || t <= positions[0].evalf()) {
        return colors[0];
      }
      if (t >= positions[n - 1].evalf()) {
        return colors[n - 1];
      }
      int k = 0;
      while (k < n - 2 && positions[k + 1].evalf() <= t) {
        k++;
      }
      IExpr width = engine.evaluate(F.Subtract(positions[k + 1], positions[k]));
      if (width.isZero()) {
        return colors[k + 1];
      }
      IExpr u = engine.evaluate(F.Divide(F.Subtract(arg2, positions[k]), width));
      ColorValue from = values[k];
      ColorValue to = values[k + 1];
      IExpr[] components = new IExpr[from.components.length];
      for (int i = 0; i < components.length; i++) {
        components[i] = interpolate(from.components[i], to.components[i], u, engine);
      }
      return new ColorValue(space, components,
          opaque ? null : interpolate(from.opacity, to.opacity, u, engine)).toDirective();
    }

    /** <code>a + u (b - a)</code>; for machine numbers as one fused multiply-add. */
    private static IExpr interpolate(IExpr a, IExpr b, IExpr u, EvalEngine engine) {
      if (a.isMachineNumber() || b.isMachineNumber() || u.isMachineNumber()) {
        double x = a.evalf();
        return F.num(Math.fma(u.evalf(), b.evalf() - x, x));
      }
      return engine.evaluate(F.Plus(a, F.Times(u, F.Subtract(b, a))));
    }

    /** The colors mixed by the weights. */
    private static IExpr mix(ColorValue[] values, IAST weights, EvalEngine engine) {
      IExpr total = engine.evaluate(weights.setAtCopy(0, S.Plus));
      if (total.isZero()) {
        return F.NIL;
      }
      int length = values[0].components.length;
      IExpr[] components = new IExpr[length];
      for (int i = 0; i < length; i++) {
        IASTAppendable sum = F.PlusAlloc(values.length);
        for (int j = 0; j < values.length; j++) {
          sum.append(F.Times(F.Divide(weights.get(j + 1), total), values[j].components[i]));
        }
        components[i] = engine.evaluate(sum);
      }
      IExpr opacity = null;
      if (values[0].opacity != null) {
        IASTAppendable sum = F.PlusAlloc(values.length);
        for (int j = 0; j < values.length; j++) {
          sum.append(F.Times(F.Divide(weights.get(j + 1), total), values[j].opacity));
        }
        opacity = engine.evaluate(sum);
      }
      return new ColorValue(values[0].space, components, opacity).toDirective();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>ToColor(color, space)</code> - the color converted to the color space
   * <code>RGBColor, GrayLevel, Hue</code> or <code>CMYKColor</code>.
   */
  private static class ToColor extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      ColorValue value = ColorValue.of(ast.arg1(), engine);
      if (value == null) {
        return Errors.printMessage(S.ToColor, "color", F.list(ast.arg1()), engine);
      }
      IExpr space = ast.arg2();
      if (space != S.RGBColor && space != S.GrayLevel && space != S.Hue
          && space != S.CMYKColor) {
        return Errors.printMessage(S.ToColor, "colconv", F.list(space), engine);
      }
      if (space == value.space) {
        return ast.arg1();
      }
      return value.convert((ISymbol) space, engine).toDirective();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>Lighter(colour, f)</code> - the colour blended with white by the fraction
   * <code>f</code>; <code>Darker(colour, f)</code> - blended with black. <code>f</code> is 1/3 if
   * it is not given. A list of colours gives the list of the results, and a colour which is not
   * an <code>RGBColor</code> is converted to one.
   */
  private static class LighterDarker extends AbstractEvaluator {
    private final boolean lighter;

    LighterDarker(boolean lighter) {
      this.lighter = lighter;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr fraction = ast.isAST2() ? ast.arg2() : F.C1D3;
      if (!fraction.isReal()) {
        return F.NIL;
      }
      return blend(ast.arg1(), fraction, engine);
    }

    private IExpr blend(IExpr colour, IExpr fraction, EvalEngine engine) {
      if (colour.isList()) {
        IAST list = (IAST) colour;
        IASTAppendable result = F.ListAlloc(list.argSize());
        for (int i = 1; i <= list.argSize(); i++) {
          IExpr each = blend(list.get(i), fraction, engine);
          if (each.isNIL()) {
            return F.NIL;
          }
          result.append(each);
        }
        return result;
      }
      IAST rgb = F.NIL;
      if (colour.isAST(S.RGBColor) && colour.argSize() >= 3 && colour.argSize() <= 4
          && ((IAST) colour).forAll(x -> x.isReal())) {
        rgb = (IAST) colour;
      } else if (colour.isAST(S.GrayLevel) && colour.argSize() >= 1 && colour.first().isReal()) {
        IExpr gray = colour.first();
        rgb = colour.argSize() == 2 ? F.RGBColor(gray, gray, gray, colour.second())
            : F.RGBColor(gray, gray, gray);
      } else {
        java.awt.Color parsed = org.matheclipse.core.graphics.svg.ColorUtil.parse(colour);
        if (parsed == null || !colour.isAST()) {
          return F.NIL;
        }
        rgb = F.RGBColor(F.num(parsed.getRed() / 255.0), F.num(parsed.getGreen() / 255.0),
            F.num(parsed.getBlue() / 255.0));
      }
      IASTAppendable result = F.ast(S.RGBColor, rgb.argSize());
      for (int i = 1; i <= 3; i++) {
        IExpr c = rgb.get(i);
        // towards 1 for the lighter colour, towards 0 for the darker one
        result.append(engine.evaluate(lighter ? F.Plus(c, F.Times(fraction, F.Subtract(F.C1, c)))
            : F.Times(c, F.Subtract(F.C1, fraction))));
      }
      if (rgb.argSize() == 4) {
        result.append(rgb.arg4());
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Scaled extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // Scaled({x, y}) is a position in the plot range, which only the renderer knows: it stays
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Sphere extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // the radius 1 stays implied, as in Sphere[{0, 0, 0}]: writing it out turns
      // Sphere[{0,0,0}] /. Sphere -> Cuboid into Cuboid[{0,0,0}, 1], which is no cuboid at all
      if (ast.isAST0()) {
        return F.unaryAST1(S.Sphere, F.List(0, 0, 0));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Tetrahedron extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Text extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      // Text(expr,coords,offset) specifies an offset for the block, Text(expr,coords,offset,dir)
      // also the direction its baseline runs in
      return ARGS_1_4;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Tube extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  public static void initialize() {
    Initializer.init();
  }

  public static IAST textAtPoint(IExpr labeledPoint, IExpr x, IExpr y) {
    double xValue = x.evalfNaN();
    // xValue += (GraphicsOptions.MEDIUM_FONTSIZE / 2);
    double yValue = y.evalfNaN();
    return F.Text(labeledPoint.second(), F.List(F.num(xValue), F.num(yValue)));
  }

  /**
   * <code>Normal(GraphicsComplex(points, data))</code> - <code>data</code> with every point index
   * replaced by the point it stands for, as "an ordinary list of graphics primitives and
   * directives".
   *
   * <p>
   * As in the reference implementation a primitive holding several index lists becomes a list of
   * one primitive each - <code>Polygon({{1,2,3},{1,2,4}})</code> two polygons, a <code>Point</code>
   * a list of single points - the point arguments of shapes such as <code>Disk</code>,
   * <code>Rectangle</code> and <code>Inset</code> are resolved too, and the complex's
   * <code>VertexColors</code> and <code>VertexNormals</code> move onto each polygon with the values
   * of its own corners. The coordinates are the ones in the table, exact ones included.
   *
   * @return {@link F#NIL} if this is not a <code>GraphicsComplex</code> with a point table
   */
  public static IExpr graphicsComplexNormal(IAST graphicsComplex) {
    if (graphicsComplex.argSize() < 2 || !graphicsComplex.arg1().isList()) {
      return F.NIL;
    }
    IAST points = (IAST) graphicsComplex.arg1();
    IASTAppendable vertexData = F.ListAlloc(2);
    for (int i = 3; i < graphicsComplex.size(); i++) {
      IExpr option = graphicsComplex.get(i);
      if ((option.isRuleAST()
          && (option.first() == S.VertexColors || option.first() == S.VertexNormals))
          && option.second().isList()) {
        vertexData.append(option);
      }
    }
    IExpr substituted = substituteIndices(points, vertexData, graphicsComplex.arg2());
    // data which is not a list becomes one, even when a split made a list of it
    return graphicsComplex.arg2().isList() ? substituted : F.List(substituted);
  }

  /**
   * Walk <code>expr</code>, replacing the indices in the point arguments of every primitive which
   * takes them. Everything else - a directive, an integer which is not a point - is left as it is,
   * but still walked, so that primitives nested inside a list of directives are reached.
   */
  private static IExpr substituteIndices(IAST points, IAST vertexData, IExpr expr) {
    if (!expr.isAST()) {
      return expr;
    }
    IAST ast = (IAST) expr;
    if (ast.isAST(S.Polyhedron, 2) && ast.arg1().isList() && ast.arg1().argSize() > 0
        && (isIndexList(ast.arg1()) || ((IAST) ast.arg1()).forAll(x -> isIndexList(x)))) {
      // faces given by indices alone become the self-contained form, the point table and the faces
      IExpr faces = isIndexList(ast.arg1()) ? F.List(ast.arg1()) : ast.arg1();
      return F.binaryAST2(S.Polyhedron, points, faces);
    }
    int[] pointArguments = ast.argSize() >= 1 ? pointArguments(ast) : null;
    if (pointArguments == null) {
      return ast.map(x -> substituteIndices(points, vertexData, x), 1);
    }
    if (ast.isAST(S.Point) && (ast.arg1().isInteger() || isIndexList(ast.arg1()))) {
      // a list of single points
      IAST indices = ast.arg1().isInteger() ? F.List(ast.arg1()) : (IAST) ast.arg1();
      return indices.map(
          x -> substitutePrimitive(points, vertexData, ast.setAtCopy(1, x), pointArguments), 1);
    }
    if (isSplittable(ast) && ast.arg1().isList() && ast.arg1().argSize() > 0
        && ((IAST) ast.arg1()).forAll(x -> isIndexList(x))) {
      // several primitives of the same kind written as one
      return ((IAST) ast.arg1()).map(
          x -> substitutePrimitive(points, vertexData, ast.setAtCopy(1, x), pointArguments), 1);
    }
    return substitutePrimitive(points, vertexData, ast, pointArguments);
  }

  private static IExpr substitutePrimitive(IAST points, IAST vertexData, IAST primitive,
      int[] pointArguments) {
    IASTAppendable result = primitive.copyAppendable();
    for (int i = 1; i < primitive.size(); i++) {
      IExpr arg = primitive.get(i);
      boolean isPoint = false;
      for (int position : pointArguments) {
        isPoint |= position == i;
      }
      result.set(i,
          isPoint ? substitutePoints(points, arg) : substituteIndices(points, vertexData, arg));
    }
    if (primitive.isAST(S.Polygon) && isIndexList(primitive.arg1())) {
      // the values the complex gives the corners of this one polygon
      IAST corners = (IAST) primitive.arg1();
      for (IExpr option : vertexData) {
        IAST values = (IAST) option.second();
        IASTAppendable own = F.ListAlloc(corners.argSize());
        for (IExpr corner : corners) {
          int index = corner.toIntDefault();
          if (index < 1 || index >= values.size()) {
            own = null;
            break;
          }
          own.append(values.get(index));
        }
        if (own != null) {
          result.append(F.Rule(option.first(), own));
        }
      }
    }
    return result;
  }

  private static boolean isIndexList(IExpr expr) {
    return expr.isList() && expr.argSize() > 0 && ((IAST) expr).forAll(IExpr::isInteger);
  }

  /** The primitives whose first argument may be a list of several index lists. */
  private static boolean isSplittable(IAST ast) {
    switch (ast.headID()) {
      case ID.Line:
      case ID.Polygon:
      case ID.Triangle:
      case ID.Arrow:
      case ID.BezierCurve:
      case ID.BSplineCurve:
      case ID.Tube:
        return true;
      default:
        return false;
    }
  }

  /**
   * The positions of the arguments of {@code ast} which are a point or a list of points, or
   * <code>null</code> for an expression which is no such primitive.
   */
  private static int[] pointArguments(IAST ast) {
    switch (ast.headID()) {
      case ID.Point:
      case ID.Line:
      case ID.Polygon:
      case ID.Triangle:
      case ID.Arrow:
      case ID.BezierCurve:
      case ID.BSplineCurve:
      case ID.Tube:
      case ID.Sphere:
      case ID.Ball:
      case ID.Simplex:
      case ID.Disk:
      case ID.Circle:
      case ID.Annulus:
      case ID.Cylinder:
      case ID.Cone:
      case ID.CapsuleShape:
        return new int[] {1};
      case ID.Rectangle:
      case ID.Cuboid:
        return new int[] {1, 2};
      case ID.Inset:
      case ID.Text:
        return new int[] {2};
      default:
        return null;
    }
  }

  /**
   * Replace every index in a point argument with the point it stands for. An index outside the
   * table, and a coordinate which is written out rather than indexed, are left alone.
   */
  private static IExpr substitutePoints(IAST points, IExpr expr) {
    if (expr.isInteger()) {
      int index = expr.toIntDefault();
      return index >= 1 && index < points.size() ? points.get(index) : expr;
    }
    if (expr.isList()) {
      return ((IAST) expr).map(x -> substitutePoints(points, x), 1);
    }
    return expr;
  }

  private GraphicsFunctions() {}


}
