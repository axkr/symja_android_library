package org.matheclipse.core.builtin;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.GraphicsUtil;
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
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.IStringX;
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

  private static class Dashed extends AbstractSymbolEvaluator {
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

  private static class DotDashed extends AbstractSymbolEvaluator {
    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.Dashing(F.List(F.C0, S.Small, S.Small, S.Small));
    }
  }

  private static class Dotted extends AbstractSymbolEvaluator {
    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.Dashing(F.List(F.C0, S.Small));
    }
  }

  /** <code>Thick</code> is equivalent to <code>Thickness(Large)</code>. */
  private static class Thick extends AbstractSymbolEvaluator {
    @Override
    public IExpr evaluate(final ISymbol symbol, EvalEngine engine) {
      return F.unaryAST1(S.Thickness, S.Large);
    }
  }

  /** <code>Thin</code> is equivalent to <code>Thickness(Tiny)</code>. */
  private static class Thin extends AbstractSymbolEvaluator {
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
   * <code>Graphics</code> or <code>Graphics3D</code> the reference implementation draws it as. Several
   * graphics of the same kind become one, each keeping its primitives in a list of their own so that
   * one's directives do not reach into the next, and their options merged with the first setting of
   * each winning - so the options given to <code>Show</code> itself win over all of them.
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
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isRuleAST()) {
          options.append(arg);
        } else if (!collect(arg, graphics, engine)) {
          return F.NIL;
        }
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
        primitives.append(((IAST) g).arg1());
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

    /** A graphic, a mesh region as its picture, or a list of either. */
    private static boolean collect(IExpr arg, IASTAppendable graphics, EvalEngine engine) {
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
          if (!collect(element, graphics, engine)) {
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

  private static class Initializer {

    private static void init() {
      S.Dashed.setEvaluator(new Dashed());
      S.Show.setEvaluator(new Show());
      S.DotDashed.setEvaluator(new DotDashed());
      S.Dotted.setEvaluator(new Dotted());
      S.Thick.setEvaluator(new Thick());
      S.Thin.setEvaluator(new Thin());


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
   * labels a value rather than the expression that computed it -
   * <code>Tooltip(Prime(4))</code> has to become <code>Tooltip(7)</code> before a plot can read
   * either the number or the label.
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
      // options may follow the points, as for Polygon and Point: Mathematica writes the outline of
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

  private static class Scaled extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // TODO make this dependent on the graphics environment
      if (ast.isAST1() && ast.arg1().isList()) {
        return ast.arg1();
      }
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
      // Text(expr,coords,offset) specifies an offset for the block
      return ARGS_1_3;
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
   * <code>VertexColors</code> and <code>VertexNormals</code> move onto each polygon with the
   * values of its own corners. The coordinates are the ones in the table, exact ones included.
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
      if ((option.isRuleAST() && (option.first() == S.VertexColors
          || option.first() == S.VertexNormals)) && option.second().isList()) {
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
